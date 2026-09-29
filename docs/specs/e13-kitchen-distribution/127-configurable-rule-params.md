# [E13] Issue 127: Configurable Rule Parameters (Standard Defaults + Admin Overrides)

Status: Draft
Issue: #127
Epic: #118
Related: E12 #105 (catalog), E12 #113 / #122 (engines), outis10/KFS-APP#13, #23
Owner: TBD

## Problem

Clearances and tolerances are not defined yet by Kalitron, and they will be
tuned with real projects. They must be adjustable **without code changes or
app releases**, while app and Studio keep computing identical results.

## Goal

Ship industry-standard **proposed defaults** in the catalog, and let admins
override them from Studio; the effective values flow to the app through the
catalog version.

## Proposed defaults (mm) — to be confirmed by Kalitron

Based on common kitchen design practice (NKBA-style guidelines, rounded to
metric) and cabinet manufacturing practice.

| Key | Default | Severity | Used by | Rationale |
| --- | --- | --- | --- | --- |
| `measure.lengthSpreadMm` | 5 | WARNING | `WALL_LENGTH_SPREAD` | E12 plan |
| `measure.closureToleranceMm` | 5 | ERROR | `WALL_CLOSURE_MISMATCH` | E12 plan |
| `measure.floorOutOfLevelMm` | 5 | WARNING | `FLOOR_OUT_OF_LEVEL` | E12 plan |
| `service.sinkToleranceMm` | 100 | ERROR (ack.) | `DIST_SINK_NOT_OVER_SERVICES` | Water/drain may be up to 100 mm outside the sink base (flexible connections) |
| `service.gasToleranceMm` | 300 | ERROR (ack.) | `DIST_COOKING_NOT_OVER_GAS` | Gas valve may sit in the adjacent cabinet, accessible |
| `filler.wallMinMm` | 20 | WARNING | `DIST_FILLER_REQUIRED` | Scribe filler against walls |
| `filler.outOfPlumbFactor` | 1.0 | WARNING | `DIST_FILLER_REQUIRED` | Filler ≥ `wallMinMm` + factor × out-of-plumb |
| `filler.cornerMinMm` | 50 | ERROR | `DIST_CLEARANCE_CORNER` | Doors/handles clash in inside corners |
| `filler.maxMm` | 150 | WARNING | `DIST_FILLER_TOO_WIDE` | Larger fillers → use a module |
| `clearance.fridgeSideMm` | 20 | WARNING | `DIST_CLEARANCE_FRIDGE` | Ventilation/installation gap |
| `clearance.fridgeHingeWallMm` | 100 | WARNING | `DIST_CLEARANCE_FRIDGE` | Door opening ≥ 90° next to wall/tall |
| `clearance.cookingLandingMm` | 300 | WARNING | `DIST_CLEARANCE_COOKING` | Countertop each side of cooktop/range (≈ 12–15 in) |
| `clearance.cookingToTallMm` | 300 | WARNING | `DIST_CLEARANCE_COOKING` | Heat/safety next to tall units or fridge |
| `clearance.sinkLandingPrimaryMm` | 450 | WARNING | `DIST_CLEARANCE_SINK` | ≈ 18–24 in on one side |
| `clearance.sinkLandingSecondaryMm` | 300 | INFO | `DIST_CLEARANCE_SINK` | Other side |
| `clearance.dishwasherToSinkMaxMm` | 900 | WARNING | `DIST_DISHWASHER_FAR_FROM_SINK` | ≈ 36 in from sink edge |
| `upper.defaultMountHeightMm` | 1450 | — | Geometry | Bottom of uppers (900 counter + 550) when template has none |
| `upper.windowClearanceMm` | 50 | ERROR | `DIST_UPPER_COLLISION` | Side clearance to window frame |
| `upper.beamClearanceMm` | 10 | ERROR | `DIST_UPPER_COLLISION` | Top of uppers below beam |
| `hood.minHeightAboveCookingMm` | 650 | WARNING | `DIST_HOOD_HEIGHT` | Typical for gas (600 electric) |
| `aisle.warningMm` | 1000 | WARNING | `DIST_AISLE_NARROW` | Work aisle (≈ 42 in = 1067) |
| `aisle.errorMm` | 900 | ERROR | `DIST_AISLE_NARROW` | Minimum passage |
| `island.walkwayMm` | 900 | WARNING | `DIST_ISLAND_CLEARANCE` | Non-working sides |

## Configuration model

- **Defaults** live in the catalog JSON (`params` section) with `min`, `max`,
  `unit`, `labelEsMx` and optional `projectTypes` (for future closets, E14).
- **Overrides**: new entity `RuleParamOverride` (`paramKey` unique,
  `valueMm`, `note`, `updatedBy`, `updatedAt`), JDL + Liquibase.
- **Effective catalog** = defaults + overrides; `catalogVersion` =
  `{baseVersion}+{overridesHash}` → ETag changes → the app refreshes (#10 in KFS-APP).
- Severities are also overridable per rule (`ruleSeverityOverride`), bounded
  to `INFO|WARNING|ERROR`.
- Every validation response and stored version records the effective
  `catalogVersion`; approved versions are **not** re-validated retroactively.

## API Contract

- `GET /api/admin/rule-params` (ADMIN): defaults, overrides, effective values.
- `PUT /api/admin/rule-params/{key}` `{ valueMm, note }` → `400` if outside `min..max`.
- `DELETE /api/admin/rule-params/{key}` → back to default.
- Audit log entry per change.

## Frontend (Studio)

- Admin → "Parámetros de validación": table grouped by area, default vs
  current, reset button, note; warning that changes affect new validations only.

## Acceptance Criteria

- [ ] Catalog ships with the proposed defaults above.
- [ ] Admin override changes Studio validation immediately and the app's after catalog refresh.
- [ ] Values outside bounds are rejected.
- [ ] Conformance vectors pin their `params` so engine tests don't depend on overrides.

## Open Questions

- [ ] Kalitron review of every default (especially fridge, cooking, aisle).
