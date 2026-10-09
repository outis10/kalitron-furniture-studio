---
epic: E15
title: Room shell to Fusion 360 (walls, openings, services and obstructions from the confirmed measurement)
status: Draft
issues: "#134 #135 #136 #137 #138 #139 #140"
depends_on: "E12 #104 (confirmed SiteMeasurement, catalog #105)"
feeds: "E11 #99 (Fusion prototype), E13 #124 (Fusion gate)"
---

# E15 — Room shell to Fusion 360

Status: Draft
Epic: #133
Depends on: E12 #104 — confirmed `SiteMeasurement` (#107, #110, #112), catalog (#105)
Feeds: E11 #99 (cabinets placed in Fusion), E13 #124 (CSV/Fusion only from approved distribution)
Repos: `outis10/kalitron-furniture-studio` (export), `outis10/kalitron-fusion-addin` (new, add-in)

## Problem

The on-site measurement (E12) gives Studio an exact, validated description of
the room: walls, corners, ceiling heights, openings, services, obstructions and
appliance spaces. Today nothing turns that into a Fusion 360 model, so the
designer redraws the room by hand before placing cabinets — the same
transcription risk E12 removed on site.

## Goal

From a **confirmed** site measurement, Studio exports a **room-shell package**
(`manifest.json` + `walls.csv` + `elements.csv`). A versioned Fusion 360
add-in reads the package and builds the room:

- **Walls** as real solids (interior face exact, nominal thickness outward,
  mitered corners, sloped ceilings).
- **Windows and doors** cut through the walls; door swing drawn on the floor.
- **Obstructions** (columns, beams, pipes) as real solids — they constrain cabinets.
- **Services** (water, drain, gas, outlets, switches, exhaust) as small colored
  **markers** on the wall face at the measured point.
- **Appliance spaces** as translucent **envelopes** (A × H × depth).

Cabinets are **not** part of this epic: E11 places them later in the same
design, in the same coordinate frame.

## Non-Goals

- Cabinets, distribution, BOM or cut lists (E11 / E13 / E6).
- Running Fusion 360 on a server (Fusion runs on the designer's workstation).
- Importing changes made in Fusion back into Studio.
- Rooms without a confirmed E12 measurement (E6 manual layout / E7 sketch)
  — possible later from `RoomWall`/`RoomObstacle`, not in v1.
- Real appliance models (brand/model geometry); envelopes only.

## Recommendations (review of the original idea)

The original idea — CSV from the layout, a Python script in Fusion, real
components for walls/windows and simple rectangular markers for everything
else — is right. These upgrades make it robust:

| # | Recommendation | Why |
| --- | --- | --- |
| R1 | **Own epic (E15), gated only on `CONFIRMED` measurement**, not on distribution approval. | The room shell is useful as soon as the room is measured — the designer can start in Fusion while E13 is still iterating. E11 then reuses it. |
| R2 | **Studio exports data; Fusion runs a fixed, versioned add-in.** No Python code generated per session. | The current `fusion_gateway/script_builder.py` generates code: it already has a units bug (Fusion's API works in **cm**, the script converts to **m**, so everything is 100× too small) and builds variable names from part names (a `-` or space breaks the script). A static add-in is testable, reviewable and safe. |
| R3 | **Studio resolves all geometry; the add-in only draws.** Wall start/end points, directions, miters, defaults (marker size, obstruction depth, thickness) are computed in Studio (Java, unit-tested). Every CSV row is a fully-resolved box. | One place owns the math; the add-in stays dumb and cannot drift. Same principle as E12: define once, execute deterministically. |
| R4 | **Package = `manifest.json` + `walls.csv` + `elements.csv`** (zip as one `DesignArtifact`). | Walls and elements have different columns; one mixed CSV is error-prone. The manifest carries schema version, units, frame, source revision, defaults, warnings and file hashes. CSVs stay Excel-inspectable. |
| R5 | **Explicit coordinate frame**: mm, Z up, origin at the left end of wall A (interior face, floor), wall A along +X, walls clockwise seen from above (room on the right of each wall's direction). | Matches the E12 capture rules (`A…n` clockwise, X from the wall's left corner). Without a written frame every consumer guesses differently. |
| R6 | **One Fusion component per wall, placed with the wall's transform**; elements built in **wall-local coordinates** (X along the wall, Y up, depth into the room). | The X/Y captured on site are used as-is; no trigonometry per element in the add-in. |
| R7 | **Layer-first component tree** (`MUROS`, `SERVICIOS`, `OBSTRUCCIONES`, `ELECTRODOMESTICOS`) + Fusion **attributes** (`kalitron/elementUuid`, `code`, `wallCode`, `measurementUuid`, `revision`) on every occurrence. | Toggle a whole layer (hide all services) and trace any Fusion body back to the Studio element. |
| R8 | **Generate geometry in code**, do not insert pre-built `.f3d` components for v1. | Inserting library components needs Fusion Team hub/project ids and network; generated boxes are offline and deterministic. A real component library (doors, windows, appliances) can come in v2 behind the same `render_as` values. |
| R9 | **Shared golden fixtures** (`fixtures/inputs` → `fixtures/expected`), like E12's conformance vectors. Studio's exporter must reproduce `expected/`; the add-in uses `expected/` as test data. | The Fusion dev can start today without Studio running; contract drift is caught in CI. |
| R10 | **Add-in split into a pure-Python core (`kfs_room_core`) and a thin Fusion adapter.** | Parsing, validation and coordinate math are tested with `pytest` outside Fusion; only drawing needs Fusion. |
| R11 | **Feedback to E12** (see below): reflex corners (270°), open rooms, angle-sum and closure checks. | Found while building the fixtures; they affect capture and validation, not just export. |
| R12 | **Update E11 #100** to consume the room shell: drop `roomHeightMm` and per-wall `angleDeg`; cabinets reference `wallCode` + wall-local X and mount height. | E12 now has per-wall ceiling heights and corner angles; E11's contract predates them. |

## Feedback to E12 (to fold into #105 / #112 / #113)

| Topic | Proposal |
| --- | --- |
| Reflex corners | `corners[].angleDeg` is the **interior** angle; allow 180 < angle < 360 (e.g. 270 for an L-shaped room). The app's corner input must allow it. |
| Open rooms | A room is **closed** when a corner `E-{last}{first}` exists; otherwise it is open (e.g. a kitchen open to the living room). The app needs an explicit "open side" choice instead of forcing the last corner. |
| `ROOM_ANGLE_SUM_MISMATCH` | New `MEASUREMENT` rule, scope `SITE`, `WARNING`: closed room with Σ angles ≠ (n − 2) × 180 (tolerance 0.5°). |
| `ROOM_NOT_CLOSED` | New rule, scope `SITE`, `WARNING`: closed room whose polygon (lengths + angles) does not close within `closureToleranceMm` (default 20). Studio exports anyway and reports the gap; it never adjusts measured values. |

## Issues

| Id | Type | Title | Repo |
| --- | --- | --- | --- |
| #133 | epic | Room shell to Fusion 360 | studio |
| #134 | task | Room-shell package contract + golden fixtures ([room-shell-contract.md](room-shell-contract.md), this folder) | studio |
| #135 | task | `RoomShellExportService`: geometry + resolution from confirmed `SiteMeasurement`; golden-fixture tests | studio |
| #136 | task | Export endpoint + `DesignArtifact` (zip) + download | studio |
| #137 | task | Fusion add-in core (`kfs_room_core`): parse, validate, frame math; pytest on fixtures ([fusion-room-shell-addin.md](fusion-room-shell-addin.md)) | fusion add-in |
| #138 | task | Fusion add-in adapter: walls, openings, markers, solids, envelopes, door swings, attributes | fusion add-in |
| #139 | user-story | Studio UI: "Exportar muros a Fusion 360" with instructions and warnings | studio |
| #140 | task | E12 feedback: reflex corners, open rooms, `ROOM_ANGLE_SUM_MISMATCH`, `ROOM_NOT_CLOSED` | studio + KFS-APP |
| — | update | E11 #100: reuse the room-shell frame and walls | studio |

## Suggested order

1. #134 (this spec + fixtures) → review → `Reviewed`.
2. **In parallel:** #137/#138 (Fusion dev, from `fixtures/expected/` only) and
   #140 (E12 rules).
3. #135 → #136 once E12 #107/#112 exist (the exporter needs a stored
   confirmed measurement).
4. #139 UI.
5. E11 #100 rewritten on top of the room shell.

## Decisions

| Date | Decision |
| --- | --- |
| 2026-10-09 | Room shell is its own epic, gated on `CONFIRMED` measurement only (R1). |
| 2026-10-09 | Data-only export + static versioned Fusion add-in; no per-session code generation (R2). |
| 2026-10-09 | Studio resolves all geometry and defaults; CSV rows are final boxes (R3). |
| 2026-10-09 | Package: `manifest.json` + `walls.csv` + `elements.csv` (R4). |
| 2026-10-09 | Source of the export is the confirmed `SiteMeasurement.payload` snapshot (immutable per revision), not the projected `RoomWall`/`RoomObstacle` rows (which lack swing and per-value source). |
| 2026-10-09 | Add-in lives in a new repo `outis10/kalitron-fusion-addin` (runs on Windows workstations, not on the gateway server). |
| 2026-10-09 | New `ArtifactType.FUSION_ROOM_SHELL` ("Paquete de muros Fusion 360"), appended via JDL + new Liquibase changeset. |
| 2026-10-09 | Nominal wall thickness 150 mm (`app.fusion.room-shell.wall-thickness-mm`); no per-session override in v1. |
| 2026-10-09 | Door `swing` values: `LEFT_IN`, `RIGHT_IN`, `LEFT_OUT`, `RIGHT_OUT`, `SLIDING`, `NONE` (hinge side seen from inside facing the wall). KFS-APP#12 and E12 #112 adopt them. |

## Open Questions

Resolved 2026-10-09 (see *Decisions*): add-in repo, `ArtifactType`, wall
thickness, door `swing` values.

- [ ] Should the add-in also draw a floor slab and ceiling (translucent) for renders (E11/E9)?
- [ ] Future automation (Fusion add-in polling Studio for new packages, or
      Autodesk's Fusion automation APIs) — out of scope for v1.
