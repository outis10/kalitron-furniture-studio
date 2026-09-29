# E12: Formal Sketch Extraction (v2)

Status: Draft
Repos: `outis10/kalitron-furniture-studio`, `outis10/kalitron-furniture-ai-gateway`
Related: E7 Sketch-to-Layout Extraction (v1, informal sketches)
Epics: Studio #104, Gateway outis10/kalitron-furniture-ai-gateway#31

> Originally drafted as "E9". Renumbered to E12 because E9, E10 and E11 were
> already in use. Issue mapping: G1–G7 → gateway #32–#38, S1–S7 → Studio
> #105–#111. The per-issue specs in this folder are the source of truth; this
> plan is kept as the original design narrative.

## Problem

E7 (`POST /api/v1/sketch/analyze`) extracts layouts from informal, free-form
sketches. It must guess wall order, units, and element positions, so most fields
land in MEDIUM/LOW confidence and need heavy review.

Kalitron's own site visits use a standardized croquis: pre-printed sheets per
session (floor plan + one elevation sheet per wall), fixed nomenclature, all
measurements in millimeters, cumulative X from the left corner of each wall,
and a QR code per sheet. That structure lets extraction be far more precise and
lets most validation be deterministic.

## Goal

Add a v2 extraction path for formal croquis that returns a validated, merged
layout per session, reviewed on site before the designer leaves the visit.

## Non-Goals

- Replacing v1. v1 stays for informal sketches (third-party carpenters using
  the service).
- Generating the cabinet design itself from the croquis (covered by E6/E7
  conversion flows).
- Rendering or quoting.

## Formal Croquis Conventions

| Rule | Value |
| --- | --- |
| Units | Millimeters only, no unit written |
| Wall codes | `A`, `B`, `C`… clockwise, starting left of the entry door |
| Corner codes | `E-AB` (corner between wall A and B), angle only if not 90° |
| X | Cumulative distance from the wall's left corner, facing the wall |
| Y | Height from finished floor |
| Element syntax | `CODE X= Y= A= H=` (A = width, H = height) |
| Wall length | `L=floor/900/ceiling`, e.g. `L=3450/3448/3452`; design uses the minimum |
| Errors | Struck through with one line, never erased |

### Nomenclature (single source of truth)

| Group | Codes | Required data |
| --- | --- | --- |
| Walls/corners | `A`…`n`, `E-AB` | 3 lengths; angle if ≠ 90° |
| Openings | `V` window, `P` door | X, A, H, sill Y; swing direction for `P` |
| Services | `TA` water, `DR` drain, `GS` gas, `CT` outlet, `AP` switch, `CE` exhaust | X, Y |
| Obstructions | `CL` column, `VG` beam, `TB` pipe, `RG` access panel | X, A, H or depth |
| Appliances | `RF` fridge, `ES` range, `PA` cooktop, `HO` oven, `CA` hood, `MW` microwave, `LV` dishwasher, `TJ` sink | A, H, depth |
| Site | `dP` floor out of level, `dPl` wall out of plumb | value in mm + location |

The catalog lives in Studio and is exported as JSON. The gateway prompt, the
printed pocket legend, and the review UI all consume the same catalog.

## Sheet Pack

- Generated when a visit is scheduled for a `DesignSession.sessionCode`.
- PDF: 1 floor-plan sheet + 6 elevation sheets.
- Each sheet: company logo, session code, client name, date, ceiling height
  box, "sheet n of m", grid, observations box, QR, and 4 ArUco markers at the
  corners.
- QR content is an identifier only: `{sessionCode}-{PLN|ELV}-{nn}`
  (e.g. `KD-2026-120-ELV-03`). Never client name, address, or phone.
- Reserve sheets: generic sheets with unassigned QR codes (`RSV-{uuid8}`),
  attached to a session at upload or from the review screen.

---

## AI Gateway Issues

### G1. v2 sketch contract spec

- Write `docs/specs/e12-formal-sketch-extraction/` contract (mirror in Studio).
- Acceptance: contract covers multi-sheet request, per-sheet metadata,
  extended wall/obstacle/zone fields, deterministic `validationIssues`,
  confidence + `sourceText` + `sourceSheet` per field.

### G2. Sheet preprocessing (OpenCV, before the LLM)

- Detect the 4 ArUco markers, compute homography, warp to a flat sheet.
- Decode QR → `sheetCode`, `sheetType` (`PLN`/`ELV`), session code.
- Reject with actionable feedback when markers or QR are not found
  ("sheet cut off", "too blurry", "glare").
- Acceptance:
  - [ ] Photos at up to ~25° tilt are rectified correctly.
  - [ ] QR is decoded deterministically; the LLM never reads the QR.
  - [ ] Unreadable sheets return `SHEET_UNREADABLE` with a reason, not a 500.

### G3. `POST /api/v2/sketch/analyze` (multi-sheet)

- Multipart: `images[]` + `context` JSON (`sessionCode`, `language`).
- Runs G2 per image, then a sheet-type-specific extraction, then merges by wall
  code into one session result.
- Unit fixed to `MM`; no `unit_hint`.
- Acceptance:
  - [ ] Accepts 1–12 images per call.
  - [ ] Sheets from a different session code are rejected per sheet.
  - [ ] Duplicate sheet codes: last upload wins, flagged as warning.

### G4. Formal extraction prompt

- Separate prompts for floor-plan and elevation sheets.
- Includes the nomenclature legend (loaded from the catalog JSON), cumulative X
  rule, `L=a/b/c` syntax, struck-through handling.
- Keeps v1 rules: never invent measurements, honest confidence, `sourceText`.
- Acceptance: prompt version is recorded in `rawExtraction`.

### G5. Extended extraction schema

- Walls: `lengthFloor`, `length900`, `lengthCeiling`, `outOfPlumb`, `angleDeg`.
- Obstacles: add `y`, `height`, `depth`; new types `SWITCH`, `EXHAUST`, `BEAM`,
  `PIPE`, `ACCESS_PANEL`.
- Zones/appliances: add `height`, `depth`; appliance subtype from the catalog.
- Site: `floorOutOfLevel` with location.
- Every field: `sourceSheet` (sheet code it came from).
- Acceptance: v1 schema and endpoint unchanged; v2 models in their own module.

### G6. Deterministic validator

Runs in Python on the merged result; output `validationIssues[]` with
`code`, `severity` (`ERROR`/`WARNING`/`INFO`), `wallCode`, `message`.

| Code | Rule | Severity |
| --- | --- | --- |
| `WALL_SUM_MISMATCH` | Elements + gaps ≠ wall length (tolerance 5 mm) | ERROR |
| `WALL_LENGTH_SPREAD` | Max − min of 3 lengths > 5 mm | WARNING |
| `FLOOR_OUT_OF_LEVEL` | `dP` > 5 mm | WARNING |
| `CORNER_NOT_SQUARE` | Angle ≠ 90° | WARNING |
| `ELEMENT_MISSING_XY` | Element without X or Y | ERROR |
| `APPLIANCE_MISSING_DIMS` | Appliance without A, H, depth | ERROR |
| `ELEMENT_OUT_OF_WALL` | X + A > wall length | ERROR |
| `UNKNOWN_CODE` | Code not in catalog | ERROR |
| `LOW_CONFIDENCE` | Field confidence LOW/MISSING | WARNING |
| `WALL_WITHOUT_SHEET` | Wall on floor plan with no elevation sheet | ERROR |

- Acceptance: validator is pure, fully unit-tested, independent from the LLM.

### G7. Golden set and accuracy tests

- 8–10 filled real croquis (photos) with expected JSON.
- Test reports field-level accuracy per sheet type.
- Acceptance: baseline accuracy recorded; regression check in CI (can be
  marked slow/manual because it calls the model).

---

## Studio Issues

### S1. Nomenclature catalog

- Table/enum of codes with group, label (es-MX), required fields, and mapping
  to `RoomObstacleType` / appliance subtype.
- Endpoint to export it as JSON for the gateway and the printed legend.

### S2. Sheet pack generation

- Generate the PDF sheet pack (see Sheet Pack) from a session.
- Generate reserve sheets in bulk.
- Acceptance: QR and ArUco markers print legibly on letter paper at 100%.

### S3. Data model changes (JDL + Liquibase)

- `RoomWall`: `lengthFloorMm`, `length900Mm`, `lengthCeilingMm`,
  `outOfPlumbMm`; `lengthMm` becomes the computed minimum.
- `RoomObstacleType`: `SWITCH`, `EXHAUST`, `BEAM`, `PIPE`, `ACCESS_PANEL`.
- `RoomObstacle`: `applianceType` (nullable).
- `KitchenSpec` (or site record): `floorOutOfLevelMm`, location note.
- `DesignImage`: `sheetCode`, `sheetType`.
- `SessionStatus`: add `MEASURED` (croquis validated) between `DRAFT` and
  `SPECS_READY`.
- `GenerationJobType`: `SKETCH_EXTRACTION`.

### S4. Mobile multi-sheet capture and routing

- Mobile-first capture of several sheets in one upload.
- Routing: if QR sheets are detected → v2, otherwise → v1 (informal).
- Persist images and raw extraction as `DesignImage` / `DesignArtifact`
  (reuse E7 #87).

### S5. v2 review preview

- One view per wall: elevation drawing + table of elements.
- Highlight LOW/MISSING fields and `validationIssues`.
- Inline edit; revalidate on each edit (call validator or port rules).
- "Confirm measurement" is blocked while any `ERROR` exists.
- Acceptance: designer can fix everything on a phone, on site.

### S6. Convert confirmed v2 extraction to measured layout

- Reuse the E7 conversion (#62) with the extended fields.
- Sets `SessionStatus.MEASURED`.

### S7. Assign reserve sheets

- Attach an `RSV-` sheet to a session from the review screen.

---

## Suggested Order

1. G1 + S1 (contract and catalog)
2. S3 (data model)
3. G2 → G5 → G4 → G3 (gateway pipeline)
4. G6 + G7 (validation and golden set)
5. S2 (sheet pack) in parallel with the gateway work
6. S4 → S5 → S6 → S7 (Studio flow)

## Prompt for Claude Code

> Read `E12-formal-sketch-extraction-plan.md`. Following each repo's
> `docs/specs/README.md` workflow and templates, create the spec files under
> `docs/specs/e12-formal-sketch-extraction/` in both repos, then use `gh` to
> create the E12 epic and its issues (G1–G7 in the gateway, S1–S7 in Studio),
> cross-linking related issues between repos. Do not implement code yet.
