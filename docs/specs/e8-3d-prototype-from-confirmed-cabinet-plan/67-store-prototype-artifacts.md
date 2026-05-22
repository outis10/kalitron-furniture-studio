# E8 Issue 67: Store Prototype Artifacts

Status: Implemented
Issue: #67
Epic: #64
Depends on: #66

## Problem

The generated floor plan PNG and metadata JSON must be persisted as `DesignArtifact`
records so they can be retrieved across sessions and served by the API without
regenerating them on every request.

## Goal

Persist prototype generation outputs as `DesignArtifact` records using the existing
artifact storage pattern, and make the latest job and its preview retrievable by
the frontend.

## New ArtifactType Values

| Value | Description |
|---|---|
| `PROTOTYPE_PREVIEW` | 2D floor plan PNG generated from cabinet plan |
| `PROTOTYPE_METADATA` | JSON metadata about the generation (counts, layout, warnings) |

## New GenerationJobType Value

| Value | Description |
|---|---|
| `PROTOTYPE_3D` | Blockout floor plan prototype generation job |

## Storage Pattern

Files written to `${app.output.dir}/sessions/{sessionId}/` following the same pattern
as sketch images and visual concept renders (`writeOutputFile()` helper in `Prototype3dServiceImpl`).

`DesignArtifact` fields set:
- `session` → linked `DesignSession`
- `artifactType` → `PROTOTYPE_PREVIEW` or `PROTOTYPE_METADATA`
- `fileName` → `prototype_preview_{sessionId}.png` / `prototype_metadata_{sessionId}.json`
- `mimeType` → `image/png` / `application/json`
- `filePath` → relative path under output dir
- `fileSizeBytes` → byte length of written file
- `metadataJson` → JSON string (for METADATA artifact only)

## Retrieval API Contracts

### GET latest job

```
GET /api/design-sessions/{sessionId}/prototype-3d/latest
Authorization: Bearer <token>

200 OK  → Prototype3dJobDTO (if a PROTOTYPE_3D job exists)
204 No Content  → (if no job has been generated yet)
```

### GET preview image

```
GET /api/design-sessions/{sessionId}/prototype-3d/preview
Authorization: Bearer <token>

200 OK  Content-Type: image/png  → PNG bytes
204 No Content  → (if no PROTOTYPE_PREVIEW artifact exists or file missing)
```

## Repository Methods Added

- `DesignArtifactRepository.findBySessionIdAndArtifactTypeInOrderByCreatedAtDesc(Long, List<ArtifactType>)`
- `GenerationJobRepository.findFirstBySessionIdAndJobTypeOrderByCreatedAtDesc(Long, GenerationJobType)`
- `GenerationJobRepository.findBySessionIdAndJobTypeAndId(Long, GenerationJobType, Long)`

## Acceptance Criteria

- [x] `DesignArtifact` records created for both `PROTOTYPE_PREVIEW` and `PROTOTYPE_METADATA` on successful generation.
- [x] Files written to disk under `app.output.dir` and paths stored in `filePath`.
- [x] `GET .../prototype-3d/latest` returns 204 before any generation and 200 with job DTO after.
- [x] `GET .../prototype-3d/preview` returns 204 before generation and 200 PNG after.
- [x] `GET .../prototype-3d/preview` returns 204 if artifact record exists but file is missing (graceful degradation).
- [x] No Liquibase migration required — new enum values are `varchar(255)` columns.
