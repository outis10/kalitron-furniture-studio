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

## Defaults (mm) — reviewed by Kalitron (2026-09-29)

Based on common kitchen design practice (NKBA guidelines; aisles use the
exact metric equivalents of 42 in = 1067 mm and 48 in = 1219 mm) and cabinet
manufacturing practice.

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
| `clearance.cookingLandingMinMm` | 300 | WARNING | `DIST_CLEARANCE_COOKING` | Minimum countertop each side of cooktop/range (≈ 12 in) |
| `clearance.cookingLandingPreferredMm` | 450 | INFO | `DIST_CLEARANCE_COOKING_PREFERRED` | Preferred landing on the main side (≈ 15–18 in) |
| `clearance.cookingToTallMm` | 300 | WARNING | `DIST_CLEARANCE_COOKING` | Heat/safety next to tall units or fridge |
| `clearance.sinkLandingPrimaryMm` | 450 | WARNING | `DIST_CLEARANCE_SINK` | ≈ 18–24 in on one side |
| `clearance.sinkLandingSecondaryMm` | 300 | INFO | `DIST_CLEARANCE_SINK` | Other side |
| `clearance.dishwasherToSinkMaxMm` | 900 | WARNING | `DIST_DISHWASHER_FAR_FROM_SINK` | ≈ 36 in from sink edge |
| `upper.defaultMountHeightMm` | 1450 | — | Geometry | Bottom of uppers (900 counter + 550) when template has none |
| `upper.windowClearanceMm` | 50 | ERROR | `DIST_UPPER_COLLISION` | Side clearance to window frame |
| `upper.beamClearanceMm` | 10 | ERROR | `DIST_UPPER_COLLISION` | Top of uppers below beam |
| `hood.minHeightAboveCookingMm` | 650 | WARNING | `DIST_HOOD_HEIGHT` | Typical for gas (600 electric) |
| `aisle.workMinMm` | 1067 | WARNING | `DIST_WORK_AISLE_NARROW` | Work aisle between facing wall runs, one cook (42 in) |
| `aisle.workMultiCookMinMm` | 1219 | WARNING | `DIST_WORK_AISLE_NARROW` | Same, when `multiCook = true` (48 in) |
| `aisle.walkwayMinMm` | 900 | ERROR | `DIST_WALKWAY_NARROW` | Passage without work zone (36 in) |
| `island.workAisleMinMm` | 1067 | WARNING | `DIST_ISLAND_WORK_AISLE_NARROW` | Island/peninsula ↔ facing run, work side, one cook |
| `island.multiCookAisleMinMm` | 1219 | WARNING | `DIST_ISLAND_WORK_AISLE_NARROW` | Same, when `multiCook = true` |
| `island.walkwayMinMm` | 900 | ERROR | `DIST_ISLAND_WALKWAY_NARROW` | Island/peninsula sides without work zone |

## Aisle definitions

- **Work aisle**: space between two facing runs where at least one side has a
  work item facing the aisle (sink, cooktop/range, fridge slot, dishwasher).
- **Walkway**: facing runs (or run ↔ wall/opening) with no work item on either side.
- `multiCook` (boolean, default `false`) is part of the distribution payload,
  set in the app (quick chip "Cocinan 2 o más personas") or in the Studio
  editor; when `true` the `…MultiCook…` params replace the one-cook minimums.
- Wall run ↔ wall run (galley, U) uses `aisle.*`; any aisle involving an island
  or peninsula uses `island.*`.

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

- [x] Kalitron review of defaults (2026-09-29).
