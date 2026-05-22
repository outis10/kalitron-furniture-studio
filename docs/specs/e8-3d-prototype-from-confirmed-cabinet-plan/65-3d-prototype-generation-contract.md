# E8 Issue 65: 3D Prototype Generation Contract

Status: Implemented
Issue: #65
Epic: #64
Depends on: #39, #40, #42, #43, #44, #61

## Problem

E8 needs a stable contract before implementation starts. The prototype generator
must receive confirmed layout and cabinet data, return artifact metadata, and
make generation status observable without creating fabrication output.

## Goal

Define the Studio contract for generating a base 3D prototype from a confirmed
measured layout and cabinet plan.

## Acceptance Criteria

- [x] Contract includes design session id and cabinet plan reference.
- [x] Contract includes layout, wall, zone, obstacle, and cabinet data required
  by the generator.
- [x] Contract defines expected artifact outputs such as preview image, model
  file, and metadata JSON.
- [x] Contract defines generation statuses and errors.
- [x] Contract states whether Fusion 360, SketchUp, or gateway service owns
  generation for MVP.
- [x] Request/response examples are documented.

## Boundary Decision

Studio owns MVP prototype generation.

Reasons:

- E8 is a deterministic blockout from validated `MeasuredLayout` and
  `CabinetPlan`.
- The AI Gateway is reserved for AI inference and visual generation workflows.
- Fusion 360 is reserved for later fabrication-oriented exports.
- SketchUp is not part of the current runtime stack.

The generator must not create BOM, Fusion scripts, CSV, STEP, DXF, or quote
outputs automatically.

## Studio API

```http
POST /api/design-sessions/{sessionId}/prototype-3d
Authorization: Bearer <jwt>
Content-Type: application/json
```

### Request

```json
{
  "cabinetPlanArtifactId": 1204,
  "prototypeMode": "BLOCKOUT",
  "units": "MM",
  "includeZones": true,
  "includeObstacles": true,
  "includeLabels": true,
  "viewPreset": "FRONT_ISO"
}
```

### Validation

- `sessionId`: required path value. Must point to an existing design session
  owned by the authenticated user.
- `cabinetPlanArtifactId`: optional. If omitted, Studio uses the latest valid
  `cabinet-plan.json` artifact for the session.
- `prototypeMode`: required. MVP supports `BLOCKOUT`.
- `units`: required. MVP supports `MM`.
- The session must have a saved measured layout.
- The session must have a valid saved cabinet plan.
- Cabinet plan validation must have no `ERROR` messages.

## Generator Input Snapshot

Studio normalizes the latest confirmed data into this internal generation
payload. This payload may be persisted as artifact metadata for auditability.

```json
{
  "schemaVersion": "1.0",
  "designSessionId": 2902,
  "sessionCode": "KD-2026-041",
  "cabinetPlanArtifactId": 1204,
  "prototypeMode": "BLOCKOUT",
  "units": "MM",
  "layout": {
    "layoutType": "L_SHAPE",
    "roomHeightMm": 2400,
    "defaultBaseDepthMm": 600,
    "defaultUpperDepthMm": 350
  },
  "walls": [
    {
      "wallCode": "A",
      "lengthMm": 3000,
      "heightMm": 2400,
      "angleDeg": 0,
      "startXMm": 0,
      "startYMm": 0,
      "sortOrder": 1
    },
    {
      "wallCode": "B",
      "lengthMm": 2500,
      "heightMm": 2400,
      "angleDeg": 90,
      "startXMm": 3000,
      "startYMm": 0,
      "sortOrder": 2
    }
  ],
  "zones": [
    {
      "zoneCode": "SINK-1",
      "zoneType": "SINK",
      "wallCode": "A",
      "xMm": 900,
      "yMm": 0,
      "zMm": 0,
      "widthMm": 800,
      "heightMm": null,
      "depthMm": 600
    }
  ],
  "obstacles": [
    {
      "obstacleType": "WINDOW",
      "label": "Ventana",
      "wallCode": "A",
      "xMm": 850,
      "yMm": 0,
      "zMm": 1100,
      "widthMm": 900,
      "heightMm": 700,
      "depthMm": 60
    }
  ],
  "cabinets": [
    {
      "cabinetCode": "A-001",
      "category": "LOWER",
      "label": "Base puerta 600",
      "wallCode": "A",
      "xMm": 0,
      "yMm": 0,
      "zMm": 0,
      "widthMm": 600,
      "heightMm": 720,
      "depthMm": 600,
      "rotationDeg": 0,
      "positionSeq": 1
    },
    {
      "cabinetCode": "A-002",
      "category": "UPPER",
      "label": "Alacena 800",
      "wallCode": "A",
      "xMm": 600,
      "yMm": 0,
      "zMm": 1400,
      "widthMm": 800,
      "heightMm": 720,
      "depthMm": 350,
      "rotationDeg": 0,
      "positionSeq": 2
    }
  ]
}
```

## Success Response

Status: `202 Accepted`

The MVP may generate synchronously if fast, but the API contract is async-first
because prototype generation can become slower when model formats or previews
are added.

```json
{
  "jobId": 5301,
  "designSessionId": 2902,
  "sessionCode": "KD-2026-041",
  "status": "QUEUED",
  "prototypeMode": "BLOCKOUT",
  "artifactGroup": "prototype-3d",
  "links": {
    "status": "/api/design-sessions/2902/prototype-3d/jobs/5301",
    "latest": "/api/design-sessions/2902/prototype-3d/latest"
  }
}
```

## Status Endpoint

```http
GET /api/design-sessions/{sessionId}/prototype-3d/jobs/{jobId}
```

Status response:

```json
{
  "jobId": 5301,
  "designSessionId": 2902,
  "status": "COMPLETED",
  "progressPercent": 100,
  "startedAt": "2026-05-21T21:00:00Z",
  "finishedAt": "2026-05-21T21:00:05Z",
  "artifacts": [
    {
      "artifactId": 9101,
      "artifactType": "PROTOTYPE_PREVIEW",
      "fileName": "KD-2026-041-prototype-preview.png",
      "filePath": "outputs/KD-2026-041/prototype-preview.png",
      "mimeType": "image/png"
    },
    {
      "artifactId": 9102,
      "artifactType": "PROTOTYPE_MODEL",
      "fileName": "KD-2026-041-prototype.glb",
      "filePath": "outputs/KD-2026-041/prototype.glb",
      "mimeType": "model/gltf-binary"
    },
    {
      "artifactId": 9103,
      "artifactType": "PROTOTYPE_METADATA",
      "fileName": "KD-2026-041-prototype.json",
      "filePath": "outputs/KD-2026-041/prototype.json",
      "mimeType": "application/json"
    }
  ],
  "warnings": [
    {
      "code": "LOW_CONFIDENCE_SOURCE",
      "message": "Prototype was generated from sketch-confirmed cabinet data. Review dimensions before fabrication."
    }
  ]
}
```

## Status Values

| Status | Meaning |
| --- | --- |
| `QUEUED` | Request accepted and waiting to run. |
| `RUNNING` | Prototype generation is active. |
| `COMPLETED` | All requested prototype artifacts were created. |
| `FAILED` | Generation failed and can be retried. |
| `CANCELLED` | Job was cancelled before completion. |

## Artifact Types

| Artifact Type | Format | Purpose |
| --- | --- | --- |
| `PROTOTYPE_PREVIEW` | PNG or WebP | Lightweight preview for Studio UI. |
| `PROTOTYPE_MODEL` | GLB | Portable blockout model for 3D preview. |
| `PROTOTYPE_METADATA` | JSON | Generator input snapshot, bounds, warnings, and version metadata. |

If the existing `ArtifactType` enum does not yet include these values, #67 must
add them with a new Liquibase changeset if persistence requires database enum
or lookup changes.

## Error Responses

| Status | Cause | Message Code |
| --- | --- | --- |
| `400` | Missing measured layout | `error.prototype.missingLayout` |
| `400` | Missing cabinet plan | `error.prototype.missingCabinetPlan` |
| `400` | Cabinet plan has validation errors | `error.prototype.invalidCabinetPlan` |
| `400` | Unsupported mode or unit | `error.prototype.invalidRequest` |
| `401` | Missing authentication | `error.http.401` |
| `403` | Session is not accessible to user | `error.http.403` |
| `404` | Session, artifact, or job not found | `error.http.404` |
| `500` | Unexpected generation failure | `error.prototype.generationFailed` |

## Persistence

- Create one `GenerationJob` for the prototype request.
- Create `DesignArtifact` records for each completed output.
- Store the generator input snapshot in the metadata artifact.
- Do not update `Cabinet`, `CabinetPart`, `Quote`, BOM, Fusion, CSV, STEP, or
  DXF data.

## Manual Verification

1. Save a valid measured layout and cabinet plan.
2. Request a blockout prototype.
3. Confirm the response returns a job id and status link.
4. Confirm the completed job exposes preview, model, and metadata artifacts.
5. Make the cabinet plan invalid and confirm generation is blocked.
