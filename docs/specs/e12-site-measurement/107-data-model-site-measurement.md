# [E12] Issue 107: Data Model for Site Measurements

Status: Draft
Issue: #107
Epic: #104
Related: #112 (sync API), #110 (conversion), outis10/KFS-APP#16 (local model)
Owner: TBD

## Problem

Laser measurements from the app carry data the current model cannot hold:
three wall lengths, out-of-plumb, new obstacle types, appliance subtype, floor
level, value source (laser/manual), per-wall photos and sync revisions.

## Goal

Extend the JDL and add incremental Liquibase changesets for a versioned
`SiteMeasurement` aggregate and the extended layout fields.

## Current state (verified)

- `RoomWall`: `name`, `lengthMm`, `heightMm`, `angleDeg`, `positionX`, `positionY`, `sortOrder`.
- `RoomObstacle`: already has `xMm`, `yMm`, `zMm`, `widthMm`, `heightMm`, `depthMm`.
- `RoomObstacleType`: `WINDOW, DOOR, COLUMN, OUTLET, WATER, GAS, DRAIN, RANGE_HOOD, APPLIANCE, OTHER`.
- `ImageType`: `REFERENCE, CATALOG, AI_RENDER, FUSION_RENDER, SKETCH`.
- `SessionStatus`: no `MEASURED`.

## Data Model Impact

| Entity / enum | Change |
| --- | --- |
| `SiteMeasurement` (new) | `projectType` (`KITCHEN`/`CLOSET`), `spaceLabel` (e.g. "Cocina", "Vestidor recámara"; a `BOTH` session can hold several), `measurementUuid` (UUID, unique, client-generated), `revision` (int), `status` (`SiteMeasurementStatus`), `schemaVersion` (int), `catalogVersion`, `payload` (JSON text, full snapshot), `deviceId`, `appVersion`, `laserModel`, `measuredBy` (User), `capturedAt`, `receivedAt`, `confirmedAt`; many-to-one `DesignSession` |
| `RoomWall` | + `wallCode`, `lengthFloorMm`, `length900Mm`, `lengthCeilingMm`, `outOfPlumbMm`, `closingMm`; `lengthMm` = min of the three (service) |
| `RoomObstacleType` | + 5 values (see *Enums* below) |
| `ApplianceType` (new enum) | see *Enums* below (no `HOOD`: `CA` uses existing `RANGE_HOOD`) |
| `SiteMeasurementStatus` (new enum) | see *Enums* below |
| `RoomObstacle` | + `applianceType`, `croquisCode` |
| `KitchenSpec` | + `floorOutOfLevelMm`, `floorOutOfLevelNote` |
| `ImageType` | + `SITE_PHOTO` (see *Enums*) |
| `DesignImage` | + `wallCode`, `photoUuid` (unique, client-generated), `sha256`; many-to-one `SiteMeasurement` (nullable) |
| `SessionStatus` | + `MEASURED` (see *Enums*) |
| `DesignSession` | + `assignedMeasurer` (User) — defined in #116 |
| `MobileRefreshToken` (new) | defined in #115 |

### Enums (JDL, with es-MX values — same pattern as existing enums)

Every enum value carries its Spanish label in the JDL; JHipster generates the
Java `getValue()`, the TypeScript enum and `i18n/es` + `i18n/en` files
(English labels added by hand in `i18n/en`).

```text
enum RoomObstacleType {
  WINDOW ("Ventana"), DOOR ("Puerta"), COLUMN ("Columna"),
  OUTLET ("Contacto eléctrico"), WATER ("Toma de agua"), GAS ("Toma de gas"),
  DRAIN ("Drenaje"), RANGE_HOOD ("Campana"), APPLIANCE ("Electrodoméstico"),
  OTHER ("Otro"),
  SWITCH ("Apagador"),
  EXHAUST ("Salida de extracción"),
  BEAM ("Viga"),
  PIPE ("Tubería"),
  ACCESS_PANEL ("Registro")
}

enum ApplianceType {
  FRIDGE ("Refrigerador"),
  RANGE ("Estufa"),
  COOKTOP ("Parrilla"),
  OVEN ("Horno"),
  MICROWAVE ("Microondas"),
  DISHWASHER ("Lavavajillas"),
  SINK ("Tarja (espacio)")
}

enum SiteMeasurementStatus {
  DRAFT ("Borrador"),
  CONFIRMED ("Confirmada"),
  SUPERSEDED ("Reemplazada")
}

// additions to existing enums
ImageType     + SITE_PHOTO ("Foto de obra")
SessionStatus + MEASURED ("Medido")
```

- New enum values are appended (never reordered); enum columns are `varchar`.
- Labels must match the catalog `labelEsMx` (parity test in #105).

- Per-value source (`LASER` / `MANUAL`) and raw laser metadata stay in
  `SiteMeasurement.payload`; only the resolved values are projected to
  `RoomWall` / `RoomObstacle` on confirmation (#110).
- Changes via JDL + `jhipster entity … --regenerate`; never hand-edit `domain/`.
- New Liquibase changelog(s), new ids, explicitly included in `master.xml`.
  Migrations are **new**, not corrective. New columns nullable; no backfill.

## Persistence rules

- `measurementUuid` unique → idempotency key for #112.
- `revision` monotonic per measurement; stored row keeps the latest revision.
- Photos never stored as base64 in DB for this flow; files stored externally
  and referenced by `DesignImage.filePath`.

## Acceptance Criteria

- [ ] JDL updated; entities, DTOs and mappers regenerated.
- [ ] Liquibase applies on an existing dev DB and on an empty DB.
- [ ] Unique constraints on `measurementUuid` and `photoUuid`.
- [ ] E6/E7 flows and tests pass unchanged.

## Test Plan

- Backend: `./gradlew test`; repository ITs for unique constraints and JSON payload round-trip.

## Open Questions

- [ ] `MEASURED` before or after `CHATTING`?
- [ ] Floor level on `KitchenSpec` or a separate site record?
- [ ] Re-measure: new `SiteMeasurement` (old → `SUPERSEDED`) or new revision?
- [ ] `payload` as `text` or `jsonb` (PostgreSQL-specific)?
