# E15 room-shell fixtures

Test data for the Fusion 360 room-shell add-in and for Studio's exporter.
Contract: [../room-shell-contract.md](../room-shell-contract.md).

```
inputs/<case>.json          confirmed site measurement (#112 payload + "_fixture" metadata)
expected/<case>/
  manifest.json             package manifest
  walls.csv                 walls (UTF-8 BOM, CRLF)
  elements.csv              elements (resolved boxes)
  preview.svg               plan + per-wall elevations — what Fusion should look like
build_fixtures.py           reference builder (stdlib only)
```

Regenerate after changing inputs or the contract:

```bash
python3 -I docs/specs/e15-room-shell-fusion/fixtures/build_fixtures.py
```

## Cases

| Case | Room | What it exercises | Expected warnings |
| --- | --- | --- | --- |
| `01-rect-basic` | 3600 × 2800, square, flat ceiling 2450 | Every `render_as`; window, door `LEFT_IN`, all service types, fridge/range/hood/sink envelopes | none |
| `02-l-room-reflex` | L-shaped, 6 walls | Reflex corner `E-BC` = 270°; column at the reflex corner (derived H); beam along D (derived Y); door `RIGHT_IN` | none |
| `03-skewed-sloped` | 4 walls at 91°/89° | Sloped ceilings on B and D; pipe full height (derived H); access panel; door `RIGHT_OUT`; manual values; floor out of level 7 mm | `ROOM_NOT_CLOSED` (22.2 mm) |
| `04-open-u` | U, 3 walls, open between C and A | Open room (square wall ends, no closure); elements at x = 0 and touching the wall end; oven column, dishwasher, cooktop, microwave | none |
| `05-edge-cases` | 4 walls, one corner 92° | Angle sum 362°; window at x = 0 touching the ceiling; door ending at the wall end (`RIGHT_OUT`); sliding door; two outlets at the same X; horizontal pipe; notes with `,` `"` and accents | `ROOM_NOT_CLOSED`, `ROOM_ANGLE_SUM_MISMATCH` |

## Manual check in Fusion (per case)

1. Run the add-in, pick `expected/<case>/manifest.json`.
2. Compare the top view with the plan in `preview.svg` (wall codes, origin dot at 0,0).
3. Inspect → Measure:
   - interior length of each wall = `length_mm`;
   - one opening per wall: distance from the wall's left corner = `x_mm`, sill = `y_mm`;
   - one marker per wall: center = (`point_x_mm`, `point_y_mm`).
4. Toggle `SERVICIOS`, `OBSTRUCCIONES`, `ELECTRODOMESTICOS` visibility.
5. Select any body → Attributes: `kalitron/elementUuid` matches `elements.csv`.
6. Save a screenshot as `fusion-<case>.png` in the add-in repo's test evidence.

## Quick reference for the Fusion dev

- mm in the CSV → **cm** in the Fusion API (÷ 10).
- Wall frame: X along the wall, Y up, Z into the room (`inner_normal`).
  Element boxes go straight into that frame: `X ∈ [x, x+w]`, `Y ∈ [y, y+h]`, `Z ∈ [0, depth]`.
- Openings are cut toward −Z (outward) through the wall thickness.
- Read CSVs with `csv.DictReader(open(p, encoding="utf-8-sig", newline=""))`.
