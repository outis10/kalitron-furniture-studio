# [E12] Issue 107: Data Model for Site Measurements

Status: Reviewed
Issue: #107
Epic: #104
Related: #112 (sync API), #110 (conversion), #113 (payload DTOs, implemented),
#116 (assigned measurer, implemented), #115 (refresh tokens), #142 (CRUD exposure),
outis10/KFS-APP#16 (local model)
Owner: TBD

## Problem

Laser measurements from the app carry data the current model cannot hold:
three wall lengths, ceiling heights at both ends, out-of-plumb, closing
measurement, appliance subtype, floor level, value source (laser/manual),
per-wall photos and sync revisions.

## Goal

Extend the JDL and add hand-written Liquibase changesets for a versioned
`SiteMeasurement` aggregate and the extended layout fields used by #110 and #112.

## Current state (verified 2026-10-10)

- `RoomWall`: `name` (max 20), `lengthMm`, `heightMm`, `angleDeg`, `positionX`,
  `positionY`, `sortOrder`. **E6 already stores the wall code in `name`**
  (`MeasuredLayoutServiceImpl`).
- `RoomObstacle`: `obstacleType`, `label`, `xMm`, `yMm`, `zMm`, `widthMm`,
  `heightMm`, `depthMm`, `notes` (max 300). **It has no wall reference**: E6
  writes `"wallCode=A;notes=…"` into `notes`.
- `RoomObstacleType` + 5 values and the `ApplianceType` enum **already exist**
  (#130 / #105) — JDL, Java, TS and i18n. No entity field uses `ApplianceType` yet.
- `KitchenSpec` has required `layout`, `style`, `primaryFinish`,
  `confirmedByClient`: a session being measured may not have one yet.
- `ProjectType`: `KITCHEN`, `CLOSET`, `BOTH`.
- `ImageType`: `REFERENCE, CATALOG, AI_RENDER, FUSION_RENDER, SKETCH`.
- `SessionStatus`: no `MEASURED`.
- Payload DTOs (`service/dto/measurement`) exist from #113; `MeasurementWallDTO`
  lacks `layers` (added in #112).
- `DesignSession.assignedMeasurer` exists (#116).

## Data Model Impact

| Entity / enum | Change |
| --- | --- |
| `SiteMeasurement` (new, JDL) | `measurementUuid` (UUID, required, unique), `projectType` (`ProjectType`, required; `KITCHEN` or `CLOSET`), `revision` (Integer, required), `status` (`SiteMeasurementStatus`, required), `schemaVersion` (Integer, required), `catalogVersion` (String 20, required), `payload` (TextBlob, required — full snapshot as received), `payloadSha256` (String 64, required — replay detection in #112), `floorOutOfLevelMm` (Integer), `floorOutOfLevelNote` (String 200), `deviceId` (String 64), `appVersion` (String 20), `laserModel` (String 40), `capturedAt` (Instant), `receivedAt` (Instant, required), `confirmedAt` (Instant); many-to-one `DesignSession` (required), many-to-one `User` as `measuredBy` |
| `SiteMeasurementStatus` (new enum) | see *Enums* |
| `RoomWall` | + `lengthFloorMm`, `length900Mm`, `lengthCeilingMm`, `outOfPlumbMm`, `closingMm`, `heightLeftMm`, `heightRightMm` (Integer); many-to-one `SiteMeasurement` (nullable). `name` stays the wall code; `lengthMm` = min of the three lengths and `heightMm` = min of the two heights (set by #110) |
| `RoomObstacle` | + `wallCode` (String 5), `croquisCode` (String 5), `applianceType` (`ApplianceType`); many-to-one `SiteMeasurement` (nullable) |
| `DesignImage` | + `wallCode` (String 5), `photoUuid` (UUID, unique), `sha256` (String 64); many-to-one `SiteMeasurement` (nullable) |
| `ImageType` | + `SITE_PHOTO` |
| `SessionStatus` | + `MEASURED` |
| `KitchenSpec` | **no change** (floor level moves to `SiteMeasurement`) |
| `DesignSession` | no change (`assignedMeasurer` done in #116) |
| `MobileRefreshToken` | not here — hand-written entity in #115 |

### Enums (JDL, es-MX values)

```text
enum SiteMeasurementStatus {
  DRAFT ("Borrador"),
  CONFIRMED ("Confirmada"),
  SUPERSEDED ("Reemplazada")
}

// additions to existing enums (appended, never reordered; columns are varchar)
ImageType     + SITE_PHOTO ("Foto de obra")
SessionStatus + MEASURED ("Medido")
```

## Persistence rules

- `measurementUuid` unique → idempotency key for #112. `photoUuid` unique.
- `revision` is the latest synced revision of a **draft**; once `CONFIRMED`
  the row is immutable (#112 returns `409 ALREADY_CONFIRMED`).
- **Re-measuring = a new `SiteMeasurement`** (new UUID). When it is confirmed,
  the previous `CONFIRMED` one of the same session and `projectType` becomes
  `SUPERSEDED` (in the #110 transaction).
- **At most one `CONFIRMED` per (`session`, `projectType`)**: enforced in the
  service and by a PostgreSQL partial unique index
  (`… on site_measurement (session_id, project_type) where status = 'CONFIRMED'`,
  `<sql>` changeset with `dbms="postgresql"`).
- `projectType` of a measurement is `KITCHEN` or `CLOSET` (never `BOTH`); a
  `BOTH` session can have one of each. v1 projects only `KITCHEN` measurements
  into the layout (#110); `CLOSET` ones are stored and confirmed (E14 projects them).
- `payload` is the raw snapshot (text, not `jsonb`: no queries on it, portable,
  JHipster `TextBlob`). Size limit enforced at the API (#112).
- Projection rows (`RoomWall`, `RoomObstacle`) created by #110 reference their
  `SiteMeasurement`; rows created by E6/E7 keep it null.
- `RoomObstacle.wallCode` becomes the source of truth for new rows; E6 keeps
  writing `notes` until it is migrated (follow-up, not here). No backfill.
- Photos are never stored as base64 for this flow; files live outside the DB
  and are referenced by `DesignImage.filePath`.
- **`MEASURED` progression** (applied by #110): on confirm, the session moves to
  `MEASURED` only if its status is `DRAFT`, `CHATTING`, `SPECS_READY` or
  `VISUAL_GENERATED`; later statuses are kept (never move backwards).

## Generation procedure (lessons from #116)

1. Update `kitchen.jdl`; run `jhipster jdl kitchen.jdl --json-only` and keep
   only the `.jhipster` changes of the affected entities.
2. Regenerate only `SiteMeasurement`, `RoomWall`, `RoomObstacle`, `DesignImage`
   (`jhipster entity X --regenerate --skip-install --force`).
3. **Revert every overwrite outside those entities** (other entities, enums such
   as `ArtifactType`, repositories with custom methods, unrelated ITs) and
   restore custom methods in the regenerated repositories.
4. **Discard the generator's edits to existing Liquibase changelogs and fake
   data**; write one hand-written changelog
   `YYYYMMDDHHMMSS_add_site_measurement.xml` (new table, new columns, FKs,
   indexes, partial unique index) and include it in `master.xml`.
5. Verify the migration on a copy of a real dev DB and on an empty DB.

The generated CRUD endpoint `/api/site-measurements` is readable/writable by
any `ROLE_USER` until #142; it must not be used by the app (the app uses #112).

## Acceptance Criteria

- [ ] JDL updated; affected entities, DTOs and mappers regenerated; no unrelated file changed.
- [ ] Hand-written Liquibase changelog applies on an existing dev DB (copy) and on an empty DB.
- [ ] Unique constraints on `measurementUuid` and `photoUuid`; partial unique index on confirmed measurements.
- [ ] Enum parity: new `ImageType`/`SessionStatus` values have es/en labels.
- [ ] E6/E7 flows and tests pass unchanged (only the 8 known pre-existing failures).

## Test Plan

- Repository ITs: unique `measurementUuid`/`photoUuid`; second `CONFIRMED` for
  the same session + project type rejected by the DB; `payload` round-trip of a
  #113 vector.
- Full backend suite.

## Open Questions

Resolved at review (2026-10-10):

- [x] Where does `MEASURED` sit? Appended to the enum; applied on confirm only from early statuses (see *Persistence rules*).
- [x] Floor level? On `SiteMeasurement`, not `KitchenSpec` (a measured session may have no spec).
- [x] Re-measure? New `SiteMeasurement`; the previous confirmed one becomes `SUPERSEDED`.
- [x] `payload` type? `text` (`TextBlob`).
- [x] `RoomWall.wallCode`? Not added — `name` already holds it.
- [x] `spaceLabel`? Dropped for v1 — the sync payload has `projectType` only.
