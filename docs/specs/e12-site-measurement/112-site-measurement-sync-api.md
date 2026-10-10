# [E12] Issue 112: Site Measurement Sync API (Idempotent, Versioned) and Wall Photos

Status: Reviewed
Issue: #112
Epic: #104
Related: #107 (model), #113 (validation, implemented), #110 (conversion),
#116 (access, implemented), #115 (mobile tokens), E15 #133 (door `swing` values),
outis10/KFS-APP#17 (sync engine), outis10/KFS-APP#15 (confirm), outis10/KFS-APP#14 (photos)
Owner: TBD

## Problem

The app captures offline and syncs later, possibly several times, over flaky
connections. The API must tolerate retries, detect conflicting edits and always
return Studio's authoritative validation.

## Goal

Endpoints to upsert a full measurement snapshot, read it back, upload wall
photos and confirm, all idempotent by client-generated UUIDs and versioned by
schema and revision.

## Non-Goals

- Partial/patch updates (the app always sends the full snapshot).
- Real-time multi-user editing (one assigned measurer per session, #116).
- Web review UI for measurements (later).
- Object storage (R2) — local disk behind an abstraction for now (ADR-004).

## Current state (verified 2026-10-10)

- `SiteMeasurementPayloadDTO` and the `measurement` DTOs exist (#113);
  `MeasurementWallDTO` has no `layers`.
- `MeasurementValidationService.validate(payload)` returns the authoritative issues (#113).
- `SessionAccessService.requireMeasurementAccess(sessionId)` and the URL rule
  `/api/design-sessions/*/site-measurements/**` → `ROLE_ADMIN`/`ROLE_MEASURER` exist (#116).
- No `spring.servlet.multipart` limits are configured (Spring default: 1 MB per
  file, 10 MB per request) → an 8 MB photo would be rejected today.
- Files are written straight to `app.output.dir` (e.g. `reference-images/…`);
  there is no storage abstraction yet.

## API Contract

Common to all endpoints:

- Auth: mobile access JWT (#115) or web JWT; `ROLE_ADMIN`, or `ROLE_MEASURER`
  assigned to the session **at the time of the call** (#116,
  `SessionAccessService`). Reassigned measurer → `403`.
- `404` unknown session; `404` measurement that does not belong to the path session.
- Error body: JHipster problem JSON with `message: "error.<CODE>"` plus the
  extra fields listed per error.

### Upsert measurement

`PUT /api/design-sessions/{sessionId}/site-measurements/{measurementUuid}`

- Max body 1 MB (`413` above).
- Request (envelope + #113 payload):

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

- Field notes:
  - `walls[].ceilingHeightLeftMm` / `ceilingHeightRightMm`: floor-to-ceiling
    height near each end; different values mean a sloped ceiling. No global height.
  - `corners[].squareCheck` (optional): `status` (`VERIFIED`, `ASSUMED_SQUARE`,
    `NOT_VERIFIABLE` — legs < 300 mm), legs and measured diagonal. Studio
    **recomputes** `angleDeg = round(acos((a² + b² − d²) / 2ab))` and uses the
    recomputed value for validation and projection (#110); the payload is stored
    as received; a mismatch with the app value is logged, not rejected.
  - `walls[].layers`: per catalog group `OPENING`, `OBSTRUCTION`, `SERVICE`,
    `APPLIANCE` → `DONE` or `NONE`. Missing keys mean unanswered (checked on confirm).
  - `elements[].swing` (doors): `LEFT_IN`, `RIGHT_IN`, `LEFT_OUT`, `RIGHT_OUT`,
    `SLIDING`, `NONE` (E15 decision; hinge side seen from inside facing the wall).
  - Unknown JSON properties are ignored (forward compatibility); the raw body
    is stored in `payload` unchanged.
- Request validation (`400`, before any state change):
  - `schemaVersion` not supported → `400 UNSUPPORTED_SCHEMA_VERSION` with
    `supportedSchemaVersions: [1]`;
  - `projectType` must be `KITCHEN` or `CLOSET` and compatible with the session
    (`KITCHEN` session → `KITCHEN`; `CLOSET` → `CLOSET`; `BOTH` → either) →
    `400 PROJECT_TYPE_MISMATCH`;
  - `revision` ≥ 1; `baseRevision` null/0 on create, ≥ 1 on update;
  - malformed JSON, negative or zero mm values, unknown `swing` → `400`.
- Semantics (`payloadSha256` = SHA-256 of the raw request body; row locked by `measurementUuid`):

| Stored state | Request | Result |
| --- | --- | --- |
| none | `baseRevision` null/0 | create `DRAFT`, `201` |
| none | `baseRevision` ≥ 1 | `409 REVISION_CONFLICT` (`serverRevision: null`) |
| exists in another session | any | `409 MEASUREMENT_SESSION_MISMATCH` |
| `DRAFT`, revision R | same `revision` and same hash | replay → `200`, stored state |
| `DRAFT`, revision R | `revision` ≤ R, different hash | `409 REVISION_CONFLICT` |
| `DRAFT`, revision R | `baseRevision` = R, `revision` > R | update, `200` |
| `DRAFT`, revision R | `baseRevision` ≠ R | `409 REVISION_CONFLICT` |
| `CONFIRMED`/`SUPERSEDED` | same `revision` and hash as stored | replay → `200` |
| `CONFIRMED`/`SUPERSEDED` | anything else | `409 ALREADY_CONFIRMED` |

- `409 REVISION_CONFLICT` body: `{ serverRevision, serverPayload }` (the app
  shows its conflict screen; nothing is overwritten).
- On create/update Studio stores: payload, hash, revision, schema/catalog
  versions, device fields, `capturedAt`, `receivedAt`, `floorOutOfLevel*` from
  `site`, `measuredBy` = current user.
- Response `200/201`:

```json
{ "measurementUuid": "uuid", "revision": 4, "status": "DRAFT", "catalogVersion": "2026-10-01.1", "catalogOutdated": false, "validationIssues": [ { "ruleSet": "MEASUREMENT", "code": "WALL_WITHOUT_PHOTO", "severity": "WARNING", "scope": "WALL", "wallCode": "B", "cornerCode": null, "elementUuid": null, "field": null, "message": "Muro B: sin foto de evidencia.", "acknowledgeable": false, "acknowledged": false } ] }
```

- `validationIssues` = Studio's current catalog run on the stored payload
  (recomputed on every call, including replays), serialized as #113
  `ValidationIssueDTO` (`runCode`/`itemUuid` are null for `MEASUREMENT` rules). `catalogOutdated` = payload
  `catalogVersion` ≠ Studio's current catalog version.

### Read measurement

`GET /api/design-sessions/{sessionId}/site-measurements/{measurementUuid}`

- `200`: the upsert response plus `payload` (stored snapshot), `uploadedPhotoUuids`,
  `confirmedAt`. Used by the app to recover after reinstall and by the conflict screen.

### Upload photo

`PUT /api/design-sessions/{sessionId}/site-measurements/{measurementUuid}/photos/{photoUuid}`

- The measurement must exist (`404` otherwise) and be `DRAFT` (`409 ALREADY_CONFIRMED`).
- Multipart: `file` (**`image/jpeg` only**, ≤ 8 MB — the app converts HEIC and
  compresses, KFS-APP#14), `wallCode`, `sha256` (hex).
- Studio computes the SHA-256 of the received bytes: ≠ `sha256` → `400 SHA256_MISMATCH`.
- Idempotent by `photoUuid`: same hash → `200` no-op; different hash → `409 PHOTO_HASH_CONFLICT`.
- `201` on create: `{ "photoUuid": "uuid", "designImageId": 123 }`.
- Stored as `DesignImage` (`SITE_PHOTO`, `wallCode`, `photoUuid`, `sha256`,
  `siteMeasurement`, session) with the file at
  `site-photos/{sessionCode}/{measurementUuid}/{photoUuid}.jpg` (relative to
  the storage root). Errors: `413` above 8 MB, `415` not JPEG.

### Confirm

`POST /api/design-sessions/{sessionId}/site-measurements/{measurementUuid}/confirm`

- Body: `{ "revision": 4 }`.
- Already `CONFIRMED` at that revision → replay `200` (same body). Confirmed at
  another revision or `SUPERSEDED` → `409 ALREADY_CONFIRMED`. `revision` ≠
  stored → `409 REVISION_CONFLICT`.
- Otherwise Studio checks, and reports **all** failures in one `422 MEASUREMENT_NOT_CONFIRMABLE`:

```json
{ "message": "error.MEASUREMENT_NOT_CONFIRMABLE", "reasons": ["VALIDATION_ERRORS", "LAYERS_UNANSWERED", "PHOTOS_PENDING"], "validationIssues": [ … ], "unansweredLayers": [ { "wallCode": "B", "layer": "SERVICE" } ], "pendingPhotoUuids": ["uuid"] }
```

  - `VALIDATION_ERRORS`: any `ERROR` from the current catalog (WARNING/INFO do not block);
  - `LAYERS_UNANSWERED`: a wall missing a key in `layers`;
  - `PHOTOS_PENDING`: a `photoUuid` referenced by a wall that was not uploaded.
- Success → #110 conversion in the same transaction → `200`:
  `{ "status": "CONFIRMED", "sessionStatus": "MEASURED", "validationIssues": [ … ] }`
  (`sessionStatus` follows the #107 progression rule and may stay unchanged).

### Errors summary

| Status | Codes / cause |
| --- | --- |
| 400 | `UNSUPPORTED_SCHEMA_VERSION`, `PROJECT_TYPE_MISMATCH`, `SHA256_MISMATCH`, malformed payload, invalid values |
| 401 | missing/expired JWT |
| 403 | not admin and not the assigned measurer |
| 404 | session, measurement (or not in that session) |
| 409 | `REVISION_CONFLICT`, `ALREADY_CONFIRMED`, `MEASUREMENT_SESSION_MISMATCH`, `PHOTO_HASH_CONFLICT` |
| 413 | body > 1 MB, photo > 8 MB |
| 415 | photo not JPEG |
| 422 | `MEASUREMENT_NOT_CONFIRMABLE` |

## Backend Behavior

- Resource: `web/rest/custom/SiteMeasurementResource`.
- Service `SiteMeasurementService` (+ Impl); repository by `measurementUuid`
  with pessimistic write lock. Concurrent creates with the same UUID: the
  unique constraint rejects the second insert, which is retried as an update/replay.
- Payload DTOs from #113; **add `layers`** (`Map<String, LayerStatus>`, enum
  `DONE`/`NONE`) to `MeasurementWallDTO`. The rules engine ignores it.
- Envelope DTO `SiteMeasurementSyncRequestDTO` (revision, baseRevision, device,
  capturedAt) + the payload; the raw body is kept for `payload`/`payloadSha256`.
- **Storage abstraction (ADR-004):** `FileStorageService` (`write`, `read`,
  `delete` by relative key) with a local-disk implementation on
  `app.output.dir`; R2 later without touching callers.
- Photo upload: write the file first, then the DB row; delete the file if the
  transaction rolls back.
- Config: `spring.servlet.multipart.max-file-size: 8MB`,
  `max-request-size: 9MB`; JSON body limit 1 MB for the upsert.
- Logs: `measurementUuid`, `revision`, `deviceId`, outcome — no client PII, no payload.

## Acceptance Criteria

- [ ] Replaying the same upsert/photo/confirm returns the same result, no duplicates.
- [ ] Every row of the upsert semantics table behaves as specified.
- [ ] Stale `baseRevision` returns `409` with the server revision and payload.
- [ ] Every upsert/read/confirm response includes authoritative `validationIssues[]` from the current catalog.
- [ ] `catalogOutdated: true` when the device catalog differs from Studio's.
- [ ] Unsupported `schemaVersion` returns `400` listing supported versions.
- [ ] Confirm returns one `422` listing every blocking reason; success runs #110 and is idempotent.
- [ ] Photo hash is verified server-side; an 8 MB JPEG uploads; a 9 MB one gets `413`.
- [ ] A measurer not (or no longer) assigned gets `403` on every endpoint.

## Test Plan

- Resource ITs: every row of the semantics table; read; photo 201/200/400/409/413/415;
  confirm 200/replay/409/422 (each reason and combined); 403/404 per endpoint.
- Concurrency ITs: two simultaneous updates with the same `baseRevision` (one
  `200`, one `409`); two simultaneous creates with the same UUID.
- Contract fixtures shared with KFS-APP: reuse the #113 vectors as payloads
  (`src/test/resources/site-measurement/validation-vectors/`).

## Open Questions

Resolved at review (2026-10-10):

- [x] Photo storage — local disk under `app.output.dir` behind `FileStorageService` (ADR-004); R2 later.
- [x] HEIC — not accepted; the app sends JPEG.
- [x] Read endpoint — added (recovery and conflict screen).
- [x] Several confirm failures — one `422` with all reasons.
