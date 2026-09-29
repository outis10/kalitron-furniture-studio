# [E12] Issue 113: Declarative Validation Rules Engine and Conformance Vectors

Status: Draft
Issue: #113
Epic: #104
Related: #105 (rules in catalog), #112 (authoritative validation),
outis10/KFS-APP#13 (Dart engine)
Replaces: gateway validator (outis10/kalitron-furniture-ai-gateway#37, closed)
Owner: TBD

## Problem

Validation must run instantly offline in the app and authoritatively in Studio.
Two implementations of the same rules will drift unless the rules are data and
both engines are tested against the same vectors.

## Goal

A Java engine that executes the catalog's declarative `validationRules[]`, and a
versioned set of conformance vectors that the app engine must also pass.

## Design

- Rule = `{ code, kind, scope, severity, params, messageEsMx }` (from #105).
- Closed set of `kind`s, implemented once per engine (v1 set):

| Kind | Used by |
| --- | --- |
| `SUM_WITHIN_TOLERANCE` | `WALL_CLOSURE_MISMATCH` (X + A + `closingMm` vs design length) |
| `SPREAD_WITHIN_TOLERANCE` | `WALL_LENGTH_SPREAD` |
| `THRESHOLD_EXCEEDED` | `FLOOR_OUT_OF_LEVEL`, `CORNER_NOT_SQUARE` |
| `REQUIRED_FIELDS` | `ELEMENT_MISSING_XY`, `APPLIANCE_MISSING_DIMS`, `WALL_INCOMPLETE`, `WALL_CLOSURE_MISSING` (conditional: wall has elements with width) |
| `WITHIN_BOUNDS` | `ELEMENT_OUT_OF_WALL` |
| `CODE_IN_CATALOG` | `UNKNOWN_CODE` |
| `SOURCE_IS` | `MANUAL_VALUE` |
| `HAS_ATTACHMENT` | `WALL_WITHOUT_PHOTO` |
| `NO_OVERLAP` | `ELEMENTS_OVERLAP` (2D rectangles in the wall plane; `SERVICE` group excluded via params) |

- `rulesEngineVersion` increments when a new `kind` is added; the catalog sets
  `minAppVersion` accordingly (see #114, KFS-APP#7).
- Engine is pure: `validate(measurement, catalog) -> List<ValidationIssue>`.

## Conformance vectors

- Location: `src/test/resources/site-measurement/validation-vectors/`.
- One file per case: `{ name, catalogVersion, input (measurement payload), expectedIssues[] }`
  (order-insensitive; compare `code`, `severity`, `wallCode`, `elementUuid`).
- Published with each release as a versioned artifact (GitHub release asset or
  `GET /api/croquis/validation-vectors?catalogVersion=` — decision open) so the
  KFS-APP CI can run them.
- Coverage: each rule pass + fail, tolerance edges (5 mm OK, 6 mm fails),
  struck/removed elements, empty walls.

## Acceptance Criteria

- [ ] Engine executes every rule in the catalog; unknown `kind` fails startup.
- [ ] All rules covered by vectors, including tolerance edges.
- [ ] Changing a tolerance in the catalog changes results without code changes (test).
- [ ] Vectors are published in a form the app CI can download by version.

## Test Plan

- Backend: parameterized JUnit over all vector files; unit tests per kind.

## Open Questions

- [ ] Vector distribution: release asset vs endpoint vs git submodule.
