# [E13] Issue 122: Shared Distribution Validation Rules

Status: Draft
Issue: #122
Epic: #118
Extends: E12 #113 (declarative engine + conformance vectors), E12 #105 (catalog)
Related: outis10/KFS-APP#23 (Dart), #121 (validate endpoint)
Owner: TBD

## Goal

Add distribution rules (scope `DISTRIBUTION`) to the catalog and new rule
kinds to the Java engine, with conformance vectors shared with the app — the
same "define once, execute twice" approach as E12.

## New rule kinds (`rulesEngineVersion` 2)

| Kind | Semantics |
| --- | --- |
| `FITS_SEGMENT` | Items of a run fit in the free segment between corners and blocking obstacles |
| `FILLER_AT_END` | Run end at a corner / out-of-square / out-of-plumb wall starts/ends with `FILLER` ≥ `minFillerMm` |
| `CONTAINS_POINT` | Item with tag T contains service point(s) of type S within `toleranceMm` |
| `MIN_CLEARANCE` | Distance between item with tag/type and neighbor or corner ≥ `minMm` |
| `NO_COLLISION` | 2D rectangles (X range × height band per row) don't overlap listed obstacle types / rows |
| `WIDTH_ALLOWED` | Width in the template/slot allowed widths |
| `CODE_IN_LIBRARY` | Template code exists in the library version |
| `STATE_IS` | Referenced measurement is in a given state |
| `MIN_DISTANCE_BETWEEN_RUNS` | Plan-view distance between facing runs (walls, islands, peninsulas) ≥ param (#128) |
| `INSIDE_ROOM` | Freestanding run inside the room polygon (#128) |

Rules and severities: see the table in [epic.md](epic.md#shared-distribution-rules).
Params: proposed standard defaults with admin overrides (#127). Every rule,
param and library entry carries `projectTypes` so closet rules (E14) can be
added without affecting kitchens.

## Acknowledgeable rules

- Rule definition flag `acknowledgeable: true`.
- Acknowledged issues are returned with `acknowledged: true` and do not block
  approval; unacknowledged `ERROR`s do.

## Engine inputs

`validateDistribution(distribution, measurement, library, catalog)`: derives X
per item, resolves geometry (segments from walls/corners/obstacles, service
points from `TA`/`DR`/`GS`, windows/beams/hood from elements), applies rules.

## Transition from `CabinetPlanValidator`

- Rules already in `CabinetPlanValidator` (fit, overlaps, dimension ranges,
  blocking obstacles) are re-expressed as declarative rules here.
- `CabinetPlanValidator` stays for the E6 generator path until its deprecation
  is decided (epic open question).

## Acceptance Criteria

- [ ] All rules in the epic table implemented and covered by vectors (pass/fail/edges).
- [ ] `rulesEngineVersion` = 2 and catalog `minAppVersion` bumped accordingly.
- [ ] Acknowledged errors do not block approval; unacknowledged do.
- [ ] Vectors published with the E12 vectors for the app CI.

## Test Plan

- Parameterized JUnit over distribution vectors; unit tests per kind with geometry edge cases (corners, out-of-plumb, beams).
