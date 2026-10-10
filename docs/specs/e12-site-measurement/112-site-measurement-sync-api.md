# [E12] Issue 112: Site Measurement Sync API (Idempotent, Versioned) and Wall Photos

Status: Draft
Issue: #112
Epic: #104
Related: #107 (model), #113 (validation), #110 (conversion),
outis10/KFS-APP#17 (sync engine), outis10/KFS-APP#15 (confirm)
Owner: TBD

## Problem

The app captures offline and syncs later, possibly several times, over flaky
connections. The API must tolerate retries, detect conflicting edits and always
return Studio's authoritative validation.

## Goal

Endpoints to upsert a full measurement snapshot, upload wall photos and confirm,
all idempotent by client-generated UUIDs and versioned by schema and revision.

## Non-Goals

- Partial/patch updates (the app always sends the full snapshot).
- Real-time multi-user editing.

## API Contract

### Upsert measurement

`PUT /api/design-sessions/{sessionId}/site-measurements/{measurementUuid}`

- Auth: mobile access JWT (#115); `ROLE_ADMIN`, or `ROLE_MEASURER` assigned to the session (#116).
- Request:

```json
{
  "schemaVersion": 1,
  "projectType": "KITCHEN",
  "revision": 4,
  "baseRevision": 3,
  "catalogVersion": "2026-10-01.1",
  "device": { "deviceId": "uuid", "platform": "android", "appVersion": "1.0.0", "laserModel": "GLM 50-27 C", "laserId": "decoded-id" },
  "capturedAt": "2026-10-05T16:20:00Z",
  "corners": [
    { "cornerCode": "E-AB", "angleDeg": 90, "squareCheck": null },
    { "cornerCode": "E-BC", "angleDeg": 91, "squareCheck": { "status": "VERIFIED", "legAMm": 1000, "legBMm": 1000, "diagonalMm": { "value": 1426, "source": "LASER" } } }
  ],
  "walls": [
    {
      "wallCode": "A",
      "lengthFloorMm":   { "value": 3450, "source": "LASER" },
      "length900Mm":     { "value": 3448, "source": "LASER" },
      "lengthCeilingMm": { "value": 3452, "source": "MANUAL" },
      "outOfPlumbMm":    { "value": 4, "source": "MANUAL" },
      "closingMm":       { "value": 1348, "source": "LASER" },
      "ceilingHeightLeftMm":  { "value": 2440, "source": "LASER" },
      "ceilingHeightRightMm": { "value": 2385, "source": "LASER" },
      "elements": [
        { "elementUuid": "uuid", "code": "V", "xMm": {"value":1200,"source":"LASER"}, "yMm": {"value":1050,"source":"MANUAL"}, "widthMm": {"value":900,"source":"LASER"}, "heightMm": {"value":1000,"source":"LASER"}, "depthMm": null, "swing": null, "notes": null }
      ],
      "layers": { "OPENING": "DONE", "OBSTRUCTION": "NONE", "SERVICE": "DONE", "APPLIANCE": "NONE" },
      "photoUuids": ["uuid"]
    }
  ],
  "site": { "floorOutOfLevelMm": 7, "floorOutOfLevelNote": "esquina E-AB" }
}
```

- Guided survey fields (KFS-APP#27):
  - `walls[].ceilingHeightLeftMm` / `ceilingHeightRightMm` (required):
    floor-to-ceiling height near each end of the wall. Different values mean
    a sloped ceiling. There is no global ceiling height.
  - `corners[].squareCheck` (optional): `status` (`VERIFIED`,
    `ASSUMED_SQUARE`, `NOT_VERIFIABLE` — legs < 300 mm), legs and measured
    diagonal of the diagonal method. When present, Studio **recomputes** `angleDeg =
    round(acos((a² + b² − d²) / 2ab))` and stores the recomputed value
    (authoritative); a mismatch with the app value is logged, not rejected.
  - `walls[].layers`: per catalog group `OPENING`, `OBSTRUCTION`, `SERVICE`,
    `APPLIANCE` → `DONE` (elements captured) or `NONE` (explicitly confirmed
    empty). Missing keys mean unanswered.
- Semantics:
  - New `measurementUuid` → create (`201`).
  - `revision` ≤ stored revision and same payload hash → replay: `200` with stored result.
  - `baseRevision` = stored revision → update (`200`).
  - `baseRevision` ≠ stored revision → `409` with `{ serverRevision, serverPayload }`.
  - Measurement already `CONFIRMED` → `409` `ALREADY_CONFIRMED`.
- Response `200/201`:

```json
{ "measurementUuid": "uuid", "revision": 4, "status": "DRAFT", "catalogVersion": "2026-10-01.1", "catalogOutdated": false, "validationIssues": [ { "code": "WALL_WITHOUT_PHOTO", "severity": "WARNING", "wallCode": "B", "elementUuid": null, "message": "…" } ] }
```

### Upload photo

`PUT /api/design-sessions/{sessionId}/site-measurements/{measurementUuid}/photos/{photoUuid}`

- Multipart: `file` (jpeg/heic, ≤ 8 MB), `wallCode`, `sha256`.
- Idempotent by `photoUuid`; same sha256 → `200` no-op; different sha256 → `409`.
- Response: `{ photoUuid, designImageId }`.

### Confirm

`POST /api/design-sessions/{sessionId}/site-measurements/{measurementUuid}/confirm`

- Body: `{ "revision": 4 }` (must equal stored revision, else `409`).
- Studio re-validates (#113); any `ERROR` → `422` with `validationIssues[]`.
- Missing referenced photos → `422` `PHOTOS_PENDING`.
- Any wall with an unanswered layer → `422` `LAYERS_UNANSWERED` (list of
  `wallCode` + layer).
- Success → conversion (#110), `200` with `{ status: "CONFIRMED", sessionStatus: "MEASURED", validationIssues }`.
- Repeating confirm for the same revision returns the same `200`.

### Validation / errors

| Status | Cause |
| --- | --- |
| 400 | Unsupported `schemaVersion` (body lists supported versions), malformed payload, negative/zero mm |
| 401 | Missing/expired JWT |
| 403 | Not admin and not the assigned measurer |
| 404 | Session not found |
| 409 | Revision conflict, already confirmed, photo hash mismatch |
| 413 | Photo too large |
| 422 | Validation `ERROR`s or pending photos on confirm |

## Backend Behavior

- Resource: `web/rest/custom/SiteMeasurementResource`.
- Payload DTOs come from #113 (`service/dto/measurement`). `MeasurementWallDTO`
  does not have `layers` yet: add it (map of group → `DONE`/`NONE`) for the
  `LAYERS_UNANSWERED` check on confirm. The rules engine does not use it.
- Service: `SiteMeasurementService` (+ Impl); repository by `measurementUuid`
  with pessimistic lock on update.
- Transactions: upsert and confirm each in one transaction; photo file write
  before DB row, cleaned up on rollback.
- Logs include `measurementUuid`, `revision`, `deviceId` (no client PII).
- Security: `/api/design-sessions/*/site-measurements/**` authenticated.

## Acceptance Criteria

- [ ] Replaying the same upsert/photo/confirm returns the same result, no duplicates.
- [ ] Stale `baseRevision` returns `409` with the server revision and payload.
- [ ] Every upsert/confirm response includes authoritative `validationIssues[]`.
- [ ] `catalogOutdated: true` when the device catalog is older than Studio's.
- [ ] Unsupported `schemaVersion` returns `400` listing supported versions.

## Test Plan

- Backend: resource ITs for create/replay/update/conflict/confirm/422/403;
  concurrency test for two simultaneous updates.
- Contract fixtures shared with KFS-APP (`src/test/resources/site-measurement/fixtures/`).

## Open Questions

- [ ] Photo storage location (local disk `app.output.dir` vs object storage).
