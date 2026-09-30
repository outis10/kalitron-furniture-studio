# [E12] Issue 113: Declarative Validation Rules Engine and Conformance Vectors

Status: Implemented
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

- Rule = `{ code, ruleSet, scope, kind, severity, params, prerequisites, acknowledgeable, projectTypes, messageEsMx }` (from #105).

### Rule sets — what is validated, and when

| `ruleSet` | Validates | Runs when | Survey-only visit |
| --- | --- | --- | --- |
| `MEASUREMENT` (E12) | The site survey is **internally consistent**: lengths, closure, elements within walls, required data, photos, floor level, corners | Whenever a measurement exists: app on every edit; Studio on sync (#112) and on confirm | ✅ Always runs; `ERROR`s block confirmation |
| `DISTRIBUTION` (E13 #122) | The kitchen **fits and respects the site**: services, clearances, aisles, uppers vs windows/beams | Only when a distribution exists (v0, draft, proposal) | ⛔ Not run — nothing to validate |

Site-vs-kitchen checks live **only** in `DISTRIBUTION`; `MEASUREMENT` rules
protect the survey even if a kitchen is never designed.

### Scopes — what each rule iterates over and where the issue attaches

| `ruleSet` | `scope` | Evaluated | Issue carries |
| --- | --- | --- | --- |
| `MEASUREMENT` | `WALL` | Once per wall (wall values and their sources) | `wallCode` (+ `field`) |
| `MEASUREMENT` | `ELEMENT` | Once per element | `wallCode`, `elementUuid` (+ `field`) |
| `MEASUREMENT` | `SITE` | Once per measurement, site data (floor, corners, ceiling) | `cornerCode` when applicable |
| `MEASUREMENT` | `MEASUREMENT` | Once, whole measurement (e.g. at least one wall) | — |
| `DISTRIBUTION` | `RUN` | Once per wall run or freestanding run | `wallCode` or `runCode`, `row` |
| `DISTRIBUTION` | `ITEM` | Once per item | `wallCode`/`runCode`, `itemUuid` |
| `DISTRIBUTION` | `DISTRIBUTION` | Once, whole distribution (aisles between runs, room checks) | — |

Scope is used for: incremental re-validation in the app (editing wall B only
re-runs `WALL`/`ELEMENT` rules of B, plus `SITE`/`MEASUREMENT` rules), UI
placement (issue shown next to its wall/element/item), and vector comparison.

### Evaluation semantics

1. **All rules are evaluated** — no short-circuit on the first failure; the
   engine returns every issue so the measurer fixes everything in one pass.
2. **Prerequisites skip, not fail.** Each rule declares `prerequisites` from a
   closed set; if one is not met for a target, the rule is skipped for that
   target (no issue). The missing data is reported by its own rule.

   | Prerequisite | Met when | Reported by |
   | --- | --- | --- |
   | `WALL_COMPLETE` | Wall has the 3 lengths (design length computable) | `WALL_INCOMPLETE` |
   | `ELEMENT_HAS_XY` | Element has X and Y | `ELEMENT_MISSING_XY` |
   | `ELEMENT_HAS_WIDTH` | Element has A | `REQUIRED_FIELDS` rules |
   | `CODE_KNOWN` | Code exists in catalog | `UNKNOWN_CODE` |
   | `ROOM_CLOSED` | Walls + corners form a closed polygon | `DIST_ROOM_NOT_CLOSED` (INFO) |
   | `MEASUREMENT_PRESENT` | Distribution references a synced measurement | API `422` |

   Example: wall A missing its ceiling length → only `WALL_INCOMPLETE`;
   `WALL_CLOSURE_MISMATCH`, `ELEMENT_OUT_OF_WALL` are skipped for wall A.
3. **Deterministic order**: issues sorted by `ruleSet` → wall order (A, B, …)
   / run order → element/item order (X, then uuid) → rule `code`. App and
   Studio must produce the same list in the same order.
4. **Issue shape**: `{ ruleSet, code, severity, scope, wallCode, runCode, cornerCode, elementUuid, itemUuid, field, message, acknowledgeable, acknowledged }` (unused fields null).
5. Rules whose `projectTypes` don't include the document's `projectType` are not evaluated.

### Kinds (v1 set, `ruleSet` `MEASUREMENT`)

Closed set of `kind`s, implemented once per engine:

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
- Engine is pure: `validateMeasurement(measurement, catalog)` and (E13 #122)
  `validateDistribution(distribution, measurement, library, catalog)`, both
  returning `List<ValidationIssue>`.

## Conformance vectors

- Location: `src/test/resources/site-measurement/validation-vectors/`.
- One file per case: `{ name, ruleSet, catalogVersion, params, input, expectedIssues[] }`
  compared **in order** on `ruleSet`, `code`, `severity`, `scope`, `wallCode`,
  `runCode`, `elementUuid`, `itemUuid`, `field`. Vectors pin their `params`
  (independent of admin overrides, #127).
- **Distribution: GitHub release asset.** A Studio workflow packages
  `validation-vectors-{catalogVersion}.zip` (vectors + the catalog snapshot
  they were generated with + `manifest.json` with `catalogVersion`,
  `rulesEngineVersion` and sha256) and attaches it to a release tagged
  `vectors-{catalogVersion}`.
- KFS-APP CI downloads the asset for the `catalogVersion` / `rulesEngineVersion`
  it targets (`gh release download` with a read-only token), verifies the
  sha256 and runs the Dart engine against it. Studio does not need to be running.
- Coverage: each rule pass + fail, tolerance edges (5 mm OK, 6 mm fails),
  struck/removed elements, empty walls, **prerequisite skips** (incomplete wall
  produces only `WALL_INCOMPLETE`), and a survey-only case (no distribution).

## Acceptance Criteria

- [ ] Engine executes every rule in the catalog; unknown `kind`, `scope`, `ruleSet` or prerequisite fails startup.
- [ ] All rules are evaluated (no short-circuit); unmet prerequisites skip without issues (vectors).
- [ ] Issue order is deterministic and identical to the Dart engine (vectors compared in order).
- [ ] Survey-only sessions get `MEASUREMENT` validation on sync and confirm; `DISTRIBUTION` rules never run without a distribution.
- [ ] All rules covered by vectors, including tolerance edges.
- [ ] Changing a tolerance in the catalog changes results without code changes (test).
- [ ] A release asset `validation-vectors-{catalogVersion}.zip` (vectors, catalog snapshot, manifest with sha256) is published per catalog version.
- [ ] KFS-APP CI can download a given version and verify its checksum.

## Test Plan

- Backend: parameterized JUnit over all vector files; unit tests per kind.

## Implementation notes

- Engine: `service/validation/MeasurementRuleEngine` (pure, no Spring/DB);
  Spring wrapper `MeasurementValidationService` for the sync API (#112).
- Input: `SiteMeasurementPayloadDTO` (#112 payload shape + `projectType`).
- Effective params = catalog defaults + overrides map (admin overrides come
  with E13 #127).
- Supported (scope, kind) combinations for `MEASUREMENT` rules are declared
  in the engine; the catalog validator rejects any other combination at
  startup. No `MEASUREMENT`-scope kinds exist yet.
- Element prerequisites on a `WALL`-scoped rule mean "at least one element of
  the wall meets it".
- `NO_OVERLAP` uses strict overlap (touching edges don't overlap) and reports
  each pair once, on the later element (by X, then uuid).
- Order: `MEASUREMENT`-scope issues, then `SITE`, then walls in input order;
  wall-level before element-level; elements by X (missing X last), uuid; then
  rule code, corner code, field.
- Vectors: `src/test/resources/site-measurement/validation-vectors/` (8
  cases); packaged by `scripts/package-validation-vectors.sh` and published by
  `.github/workflows/validation-vectors.yml` on tag `vectors-<catalogVersion>`.

## Open Questions

Resolved at review (2026-09-29):

- [x] Vector distribution: GitHub release asset (versioned, no running Studio needed).
