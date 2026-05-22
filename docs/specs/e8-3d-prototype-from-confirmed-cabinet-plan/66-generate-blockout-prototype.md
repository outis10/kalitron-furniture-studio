# E8 Issue 66: Generate Blockout Prototype from Confirmed Cabinet Plan

Status: Implemented
Issue: #66
Epic: #64
Depends on: #65

## Problem

Once a cabinet plan is confirmed and a measured layout exists for a session,
designers need a way to generate a deterministic 2D floor plan prototype that
visually represents the cabinet arrangement, walls, zones, and obstacles without
delegating to an external AI gateway.

## Goal

Generate a 2D top-down floor plan PNG (blockout mode) directly in the Studio
backend using Java `Graphics2D`, persisted as a `DesignArtifact`, and tracked
via a `GenerationJob`.

## API Contract

### Request

```
POST /api/design-sessions/{sessionId}/prototype-3d
Authorization: Bearer <token>
Content-Type: application/json

{}   (body optional — all fields have defaults)
```

Optional body:
```json
{
  "prototypeMode": "BLOCKOUT",
  "includeZones": true,
  "includeObstacles": true,
  "includeLabels": true,
  "cabinetPlanArtifactId": null
}
```

### Response — 202 Accepted

```json
{
  "jobId": 1,
  "sessionId": 42,
  "sessionCode": "ABC123",
  "status": "DONE",
  "prototypeMode": "BLOCKOUT",
  "startedAt": "2026-05-22T12:00:00Z",
  "finishedAt": "2026-05-22T12:00:01Z",
  "artifacts": [
    { "artifactId": 10, "artifactType": "PROTOTYPE_PREVIEW", "fileName": "prototype_preview_42.png", "mimeType": "image/png" },
    { "artifactId": 11, "artifactType": "PROTOTYPE_METADATA", "fileName": "prototype_metadata_42.json", "mimeType": "application/json" }
  ],
  "warnings": []
}
```

### Error cases

| Condition | Status | Detail |
|---|---|---|
| Session not found | 404 | "Design session not found" |
| No measured layout | 400 | "No measured layout found for session" |
| No cabinet plan | 400 | "No cabinet plan found for session" |
| Cabinet plan has ERRORs | 400 | "Cabinet plan has validation errors" |

## Implementation Details

- `Prototype3dServiceImpl` runs synchronously within the request (no async thread pool for MVP).
- Floor plan rendered at 1400x1000px on a white canvas, scale derived from bounding box of all walls.
- Walls chained by `sortOrder` and `angleDeg`/`lengthMm`; `startXMm`/`startYMm` used if set.
- Cabinet polygons projected along wall unit vector with perpendicular depth direction.
- Cabinet fill colors by category: LOWER=#8B9DC3, UPPER=#B8C5E0, CORNER=#9B8EA0, TALL=#7A9E7E, SINK=#76B0C0, ISLAND=#C0A882, DRAWER_BASE=#A0B0D0, APPLIANCE=#D0A0A0.
- Grid lines every 500mm; scale bar and legend rendered at bottom.
- Two artifacts saved: `PROTOTYPE_PREVIEW` (PNG) and `PROTOTYPE_METADATA` (JSON).
- A `GenerationJob` of type `PROTOTYPE_3D` is created and set to RUNNING→DONE (or FAILED on exception).
- Warning added if cabinet plan was sourced from sketch extraction rather than a manual plan.

## Acceptance Criteria

- [x] `POST /api/design-sessions/{sessionId}/prototype-3d` returns 202 with `Prototype3dJobDTO`.
- [x] Response includes `artifacts` list with `PROTOTYPE_PREVIEW` and `PROTOTYPE_METADATA` entries.
- [x] Returns 400 if session has no measured layout.
- [x] Returns 400 if session has no cabinet plan or cabinet plan has ERRORs.
- [x] Returns 404 if session does not exist.
- [x] `GenerationJob` record created with type `PROTOTYPE_3D` and status `DONE` on success.
- [x] `DesignArtifact` records created and file paths written under `app.output.dir`.
- [x] Floor plan PNG is readable and not empty.
