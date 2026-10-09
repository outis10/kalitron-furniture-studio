# [E15] Fusion 360 Room-Shell Add-in

Status: Draft
Issues: #137 (core), #138 (Fusion adapter)
Epic: #133
Contract: [room-shell-contract.md](room-shell-contract.md) · Test data: [fixtures/](fixtures/README.md)
Repo: `outis10/kalitron-fusion-addin` (new)
Owner: TBD (Fusion developer)

## Goal

A Fusion 360 Python add-in (script for v1) that asks for a room-shell
package (`manifest.json`) and builds the room in a **new** design.

## Non-Goals

- Cabinets (E11 adds them later to the same design and frame).
- Editing Studio data from Fusion.
- Generating Python per session (the add-in is static and versioned).

## Structure

```
kfs_room_shell/
  kfs_room_shell.py        # Fusion entry point: run(context) → file dialog → build
  kfs_room_shell.manifest  # Fusion add-in manifest
  core/                    # pure Python, NO adsk imports
    package.py             # load manifest + CSVs, sha256, schema/columns validation
    model.py               # dataclasses Wall, Element (mm, floats)
    frame.py               # wall-local ↔ room transforms, ceiling height at X
  fusion/                  # adsk only, no business logic
    build.py               # components, sketches, extrudes, cuts
    appearance.py          # colors / opacity
  tests/                   # pytest over ../fixtures/expected/*
```

## Fusion build rules

- **Units:** CSV is mm; the Fusion API works in **cm** internally → divide
  every length by 10 (`ValueInput.createByReal(mm / 10)`, `Point3D.create(x / 10, …)`).
- **New document per run**, named `{sessionCode} r{revision} — muros`; never
  modify an existing design.
- **Component tree** (layer first, one sub-component per wall):

```
ROOM KD-2026-022 r4
├── MUROS
│   ├── Muro A        ← occurrence transform = wall A frame
│   └── Muro B …
├── SERVICIOS
│   ├── Servicios A   ← same transform as Muro A; bodies "A-CT-01", "A-TA-01"…
├── OBSTRUCCIONES
│   └── Obstrucciones C …   (SOLID + PANEL)
└── ELECTRODOMESTICOS
    └── Electrodomésticos B … (ENVELOPE)
```

- **Wall transform** (`Matrix3D.setWithCoordinateSystem`):
  origin = (`start_x_mm`, `start_y_mm`, 0), local X = (`cos(direction_deg)`,
  `sin(direction_deg)`, 0), local Y = (0, 0, 1) (up), local Z =
  (`inner_normal_x`, `inner_normal_y`, 0) (into the room). This is
  right-handed, so inside a wall component an element box is simply
  `X ∈ [x, x + width]`, `Y ∈ [y, y + height]`, `Z ∈ [0, depth]`.
- **Wall body:** in the wall component, sketch the plan quad (interior
  start/end + mitered outer points converted to local X/Z) on the XZ plane and
  extrude up to `max(height_left, height_right)`. If the heights differ,
  intersect with the elevation trapezoid `(0,0) (L,0) (L,h_right) (0,h_left)`
  sketched on the XY plane. Convert points with `sketch.modelToSketchSpace()`
  (on the XZ plane sketch Y is model −Z).
- **OPENING:** rectangle on the XY plane (interior face), extrude **cut**
  toward −Z by `depth_mm` (= thickness), participant = wall body. For `P` with
  a hinged `swing`, add a floor sketch: leaf line + 90° arc of radius
  `width_mm` on the room side (`_IN`) or outside (`_OUT`); `SLIDING` → line
  only; `NONE` → nothing.
- **MARKER / PANEL / SOLID / ENVELOPE:** new body = box from the row, extruded
  toward +Z (into the room). Body name = `element_id`.
- **Appearance:** `color_hex` + `opacity` (envelopes translucent). Walls light grey.
- **Attributes** on every body: group `kalitron`, keys `elementUuid`, `code`,
  `wallCode`, `measurementUuid`, `revision`, `contractSchemaVersion`.
- **Floor labels (optional):** sketch text with the wall code at each wall's
  middle, 300 mm into the room.

## Validation before drawing (core)

Fail with one message listing all problems and draw nothing when:

- `contract` ≠ `kfs-room-shell` or `schemaVersion` not supported (v1 supports `1`).
- File missing or `sha256` mismatch.
- Required column missing (select columns by header; ignore unknown extra columns).
- Non-numeric value in a numeric column, width/height/depth ≤ 0, wall
  `length_mm` ≤ 0.
- Element `wall_code` not in `walls.csv`.

Show (do not block) `manifest.warnings[]` after building.

## Acceptance Criteria

- [ ] All fixtures in `fixtures/expected/` build without errors.
- [ ] Measured with Fusion's Inspect tool, wall interior lengths, opening
      positions and marker centers match the CSV within 0.1 mm.
- [ ] Fixture 02: the reflex corner E-BC is correct (room is an L, not a rectangle).
- [ ] Fixture 03: walls B and D have sloped tops; the beam on C hangs from the ceiling.
- [ ] Fixture 04: the room is open between C and A (no wall there, square ends).
- [ ] Fixture 05: the closure gap is visible at the origin and both warnings are shown.
- [ ] Hiding `SERVICIOS` hides every service marker; each body shows its `kalitron` attributes.
- [ ] Running twice creates two documents; nothing is overwritten.
- [ ] `pytest` passes for the core with no Fusion installed.

## Test Plan

- `pytest` over every `fixtures/expected/<case>`: parse, validate, transform
  each element anchor to room coordinates and compare with `global_*_mm`.
- Negative tests: tampered `sha256`, missing column, unknown `wall_code`, `schemaVersion: 2`.
- Manual in Fusion: checklist in [fixtures/README.md](fixtures/README.md), with
  a screenshot per fixture next to its `preview.svg`.
