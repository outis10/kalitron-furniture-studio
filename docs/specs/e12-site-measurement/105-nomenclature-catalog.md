# [E12] Issue 105: Nomenclature Catalog and Validation Rules Export

Status: Implemented
Issue: #105
Epic: #104
Related: #113 (rules engine), outis10/KFS-APP#10 (download), outis10/KFS-APP#13 (Dart engine)
Owner: TBD

## Problem

Element codes, required fields and validation rules must mean exactly the same
thing in the mobile app, in Studio and on the printed backup sheet. Hardcoding
them in each place guarantees drift.

## Goal

Studio owns one versioned catalog (codes + declarative validation rules) and
exports it as JSON for the app (offline cache) and the backup sheet legend.

## Non-Goals

- Admin UI to edit the catalog (changes go through code review).
- Pricing data.

## Catalog content

### Codes

| Group | Code | `labelEsMx` | Maps to |
| --- | --- | --- | --- |
| `WALL` | `A`…`n` | Muro A, Muro B… | `RoomWall` |
| `CORNER` | `E-AB` | Esquina entre muros A y B | `RoomWall.angleDeg` |
| `OPENING` | `V` | Ventana | `WINDOW` |
| `OPENING` | `P` | Puerta | `DOOR` |
| `SERVICE` | `TA` | Toma de agua | `WATER` |
| `SERVICE` | `DR` | Drenaje | `DRAIN` |
| `SERVICE` | `GS` | Toma de gas | `GAS` |
| `SERVICE` | `CT` | Contacto eléctrico | `OUTLET` |
| `SERVICE` | `AP` | Apagador | `SWITCH` |
| `SERVICE` | `CE` | Salida de extracción | `EXHAUST` |
| `OBSTRUCTION` | `CL` | Columna | `COLUMN` |
| `OBSTRUCTION` | `VG` | Viga | `BEAM` |
| `OBSTRUCTION` | `TB` | Tubería | `PIPE` |
| `OBSTRUCTION` | `RG` | Registro | `ACCESS_PANEL` |
| `APPLIANCE` | `RF` | Refrigerador | `APPLIANCE` + `FRIDGE` |
| `APPLIANCE` | `ES` | Estufa | `APPLIANCE` + `RANGE` |
| `APPLIANCE` | `PA` | Parrilla | `APPLIANCE` + `COOKTOP` |
| `APPLIANCE` | `HO` | Horno | `APPLIANCE` + `OVEN` |
| `APPLIANCE` | `CA` | Campana | `RANGE_HOOD` (existing type, no `ApplianceType`) |
| `APPLIANCE` | `MW` | Microondas | `APPLIANCE` + `MICROWAVE` |
| `APPLIANCE` | `LV` | Lavavajillas | `APPLIANCE` + `DISHWASHER` |
| `APPLIANCE` | `TJ` | Tarja (espacio) | `APPLIANCE` + `SINK` |
| `SITE` | `dP` | Desnivel de piso | `KitchenSpec.floorOutOfLevelMm` |
| `SITE` | `dPl` | Desplome de muro | `RoomWall.outOfPlumbMm` |

- `labelEsMx` is what the app buttons, the review screens, the backup sheet
  legend and validation messages show; codes stay as the short on-site notation.
- `TJ` records the measured sink space/position on site; the sink **cabinet**
  itself is a library module in the distribution (E13 #120).

Entry: `code`, `group`, `labelEsMx`, `icon`, `requiredFields`
(`X`, `Y`, `A`, `H`, `DEPTH`, `SWING`), `obstacleType`, `applianceType`, `sortOrder`.

### Validation rules

Declarative entries executed by #113 (Java) and KFS-APP#13 (Dart):

```json
{
  "code": "WALL_CLOSURE_MISMATCH",
  "ruleSet": "MEASUREMENT",
  "scope": "WALL",
  "kind": "SUM_WITHIN_TOLERANCE",
  "prerequisites": ["WALL_COMPLETE", "ELEMENT_HAS_WIDTH"],
  "severity": "ERROR",
  "params": { "toleranceMm": 5, "terms": ["rightmostElement.x", "rightmostElement.width", "wall.closingMm"], "target": "wall.designLength" },
  "messageEsMx": "Cierre: {sum} mm vs muro {length} mm (Δ {delta} mm)"
}
```

Full rule list: see [plan.md](plan.md#validation-rules-defined-once-in-the-catalog-113).

## API Contract

`GET /api/croquis/catalog`

- Auth: authenticated (any role).
- Headers: supports `If-None-Match`; returns `ETag` = `catalogVersion`.
- `200 OK`:

```json
{
  "catalogVersion": "2026-10-01.1",
  "rulesEngineVersion": 1,
  "minAppVersion": "1.0.0",
  "entries": [ { "code": "V", "group": "OPENING", "labelEsMx": "Ventana", "requiredFields": ["X", "Y", "A", "H"], "obstacleType": "WINDOW", "applianceType": null } ],
  "validationRules": [ { "code": "WALL_CLOSURE_MISMATCH", "ruleSet": "MEASUREMENT", "scope": "WALL", "kind": "SUM_WITHIN_TOLERANCE", "severity": "ERROR", "params": { "toleranceMm": 5 }, "messageEsMx": "…" } ]
}
```

- `304 Not Modified` when ETag matches. `401` unauthenticated.

## Backend Behavior

- Static resource `src/main/resources/croquis/catalog.json`, loaded once by
  `CroquisCatalogService` (+ Impl); schema-validated at startup (fail fast).
- Resource: `web/rest/custom/CroquisCatalogResource`.
- No DB table (static, versioned with code).

## Acceptance Criteria

- [ ] Catalog contains every code and rule from the plan tables.
- [ ] Every `obstacleType` / `applianceType` exists in the Java enums (unit test).
- [ ] **Label parity:** each code's `labelEsMx` equals the Spanish value of its
      mapped enum (`RoomObstacleType` / `ApplianceType` `getValue()`), except
      `APPLIANCE` codes, which compare against `ApplianceType` (unit test).
- [ ] Every rule `kind` is supported by the engine at `rulesEngineVersion` (unit test).
- [ ] `If-None-Match` returns `304` when unchanged.
- [ ] Invalid catalog JSON fails application startup with a clear message.

## Test Plan

- Backend: service load, enum/kind parity, label parity, resource IT (200/304/401).

## Implementation notes

- Catalog `src/main/resources/croquis/catalog.json` (`catalogVersion`
  `2026-09-30.1`, `rulesEngineVersion` 1, `minAppVersion` `0.1.0`): 22 codes,
  3 measurement params, 13 `MEASUREMENT` rules.
- Rules reference tunable values through `paramRefs` → `params[]` (keys as in
  E13 #127); fixed configuration (fields, groups) stays in `params`.
- `wallCodePattern` / `cornerCodePattern` describe wall and corner codes
  instead of catalog entries.
- Loaded with a strict mapper (unknown properties/enum values fail) and
  validated by `CroquisCatalogValidator`; any error stops startup.
- Model lives in `service/dto/croquis` (immutable records + enums `RuleSet`,
  `RuleScope`, `RuleKind`, `RulePrerequisite`, …) shared with the #113 engine.
- **Scope moved from #107:** the 5 new `RoomObstacleType` values and the
  `ApplianceType` enum were added here (JDL, Java, TS, i18n) because the parity
  checks need them. `RoomObstacle.applianceType` (field) stays in #107.
  Enum columns are `varchar`, so no migration was needed.

## Open Questions

Resolved at review (2026-09-29):

- [x] `catalogVersion` format: date + counter (`2026-10-01.1`); effective
      version with admin overrides per #127.
- [x] `CE` = "Salida de extracción" (hood duct outlet).
- [x] `TJ` stays in `APPLIANCE` as "Tarja (espacio)"; the sink cabinet is a library module (E13 #120).
- [x] `CA` maps to the existing `RoomObstacleType.RANGE_HOOD`; no `HOOD` in `ApplianceType`.
