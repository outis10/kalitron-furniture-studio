# [E15] Room-Shell Package Contract and Export API

Status: Draft
Issues: #134 (contract + fixtures), #135 (service), #136 (endpoint)
Epic: #133
Related: E12 #105 (catalog), #107 (model), #112 (payload), #110 (conversion); E11 #100
Owner: TBD

## Goal

Define the files Studio produces from a confirmed site measurement and the
Fusion add-in consumes, plus the Studio endpoint that produces them.

## Package

A zip (`{sessionCode}-r{revision}-room-shell.zip`) containing:

| File | Content |
| --- | --- |
| `manifest.json` | Contract id, schema version, units, frame, source, room summary, defaults, file hashes, warnings |
| `walls.csv` | One row per wall, in capture order |
| `elements.csv` | One row per element, grouped by wall, sorted by X |

### File format (both CSVs)

- UTF-8 **with BOM** (Excel shows accents correctly; read with `encoding="utf-8-sig"`).
- Comma separator, CRLF line endings, header row, RFC 4180 quoting (notes may contain `,` and `"`).
- Decimal point `.`; no thousands separator. Integers when exact, otherwise
  1 decimal for mm, 3 for degrees, 6 for unit normals.
- Empty cell = no value.
- Consumers must select columns **by header name**; new columns may be
  appended in the same `schemaVersion`.

### Coordinate frame

```
              wall A (+X) →
  (0,0) ●━━━━━━━━━━━━━━━━━━━━━━━━┓
        ┃       room interior     ┃ wall B (direction 270°)
  wall D┃     (right-hand side    ┃
        ┃    of every wall's      ┃
        ┃       direction)        ┃
        ┗━━━━━━━━━━━━━━━━━━━━━━━━━┛
              ← wall C (180°)
```

- Units **mm**, **Z up**. Plan coordinates are X, Y.
- Origin: left end of wall A, on the **interior face**, at finished floor.
- Wall A runs along +X. Walls follow capture order (clockwise seen from
  above); the room is on the **right** of each wall's direction, i.e. on the
  side of `inner_normal`.
- At a corner with interior angle θ the next wall turns clockwise by
  180° − θ (θ = 270 → turns left 90°: reflex corner).
- **Wall-local frame** (for elements): origin at the wall start (left end
  seen from inside), `u` = along the wall, `v` = up, `n` = `inner_normal`
  (into the room). Element `x_mm`/`y_mm` are `u`/`v`; `depth_mm` grows along `n`.
- Measured lengths are never adjusted. In a closed room the end of the last
  wall may not land on the origin; the gap is reported in the manifest.

### `manifest.json`

```json
{
  "contract": "kfs-room-shell",
  "schemaVersion": 1,
  "units": "mm",
  "frame": { "up": "+Z", "origin": "left end of wall A, interior face, finished floor", "wallA": "+X", "roomSide": "right of the wall direction (walls run clockwise seen from above)" },
  "session": { "sessionCode": "KD-2026-022", "projectType": "KITCHEN", "spaceLabel": "Cocina" },
  "source": { "measurementUuid": "uuid", "revision": 4, "catalogVersion": "2026-10-01.1" },
  "generatedAt": "2026-10-09T00:00:00Z",
  "room": { "closed": true, "wallCount": 4, "closureGapMm": 0.0, "closureGapXMm": 0.0, "closureGapYMm": 0.0, "angleSumDeg": 360, "expectedAngleSumDeg": 360, "maxCeilingHeightMm": 2450, "floorOutOfLevelMm": null, "floorOutOfLevelNote": null },
  "defaults": { "wallThicknessMm": 150, "markerSizeMm": 80, "markerDepthMm": 25, "panelDepthMm": 10, "obstructionDepthMm": { "CL": 300, "VG": 300, "TB": 100 }, "closureToleranceMm": 20 },
  "files": { "walls": { "name": "walls.csv", "rows": 4, "sha256": "…" }, "elements": { "name": "elements.csv", "rows": 15, "sha256": "…" } },
  "warnings": [ { "code": "ROOM_NOT_CLOSED", "message": "…" } ]
}
```

- `sha256` is over the exact file bytes (BOM included).
- `closureGap*` and `expectedAngleSumDeg` are `null` for open rooms.
- `warnings[]` are informational for the designer; they never block the export.

### `walls.csv`

| Column | Meaning |
| --- | --- |
| `wall_code` | `A`, `B`, … |
| `seq` | 1-based capture order |
| `length_mm` | Design length = min of the three measured lengths |
| `length_floor_mm`, `length_900_mm`, `length_ceiling_mm` | Measured lengths (reference) |
| `height_left_mm`, `height_right_mm` | Ceiling height at the start / end of the wall; different → sloped ceiling |
| `thickness_mm` | Nominal thickness, extruded **outward** (away from the room) |
| `out_of_plumb_mm` | Reference only (not modeled) |
| `direction_deg` | Angle of the wall direction from +X, counter-clockwise, `[0, 360)` |
| `start_x_mm`, `start_y_mm`, `end_x_mm`, `end_y_mm` | Interior-face endpoints |
| `inner_normal_x`, `inner_normal_y` | Unit vector pointing into the room |
| `outer_start_x_mm` … `outer_end_y_mm` | Exterior-face endpoints, mitered with the neighbouring wall; square ends on open ends |
| `corner_start_code`, `corner_start_angle_deg`, `corner_end_code`, `corner_end_angle_deg` | Corners at each end; empty on open ends |
| `color_hex` | Suggested appearance |

The wall slab in plan is the quad `start → end → outer_end → outer_start`,
extruded from Z = 0 to the ceiling; when `height_left_mm ≠ height_right_mm`
the top follows the line between both heights.

### `elements.csv`

Every row is a **resolved box** in wall-local coordinates:
`[x_mm, x_mm + width_mm] × [y_mm, y_mm + height_mm] × [0, depth_mm]`
(depth into the room; for `OPENING` the depth is the wall thickness, outward).

| Column | Meaning |
| --- | --- |
| `element_id` | `{wall}-{code}-{nn}`, numbered per wall and code by X — use as Fusion name |
| `element_uuid` | From the measurement (stable across revisions) |
| `wall_code`, `code`, `group`, `label_es_mx` | Catalog data (#105) |
| `render_as` | `OPENING`, `MARKER`, `PANEL`, `SOLID`, `ENVELOPE` (see below) |
| `x_mm`, `y_mm`, `width_mm`, `height_mm`, `depth_mm` | Resolved box |
| `point_x_mm`, `point_y_mm` | `MARKER` only: the measured point (box center) |
| `swing` | `P` only: `LEFT_IN`, `RIGHT_IN`, `LEFT_OUT`, `RIGHT_OUT`, `SLIDING`, `NONE`. Left/right = hinge side seen from inside the room facing the wall; IN = opens into the room |
| `derived_fields` | `;`-separated fields filled by a default instead of a measurement (`Y`, `H`, `DEPTH`) |
| `global_x_mm`, `global_y_mm`, `global_z_mm` | Room-frame position of the box's anchor (`x_mm`, `y_mm`, face) — for verification |
| `color_hex`, `opacity` | Suggested appearance |
| `notes` | Free text from the measurer |

### Resolution rules (Studio)

| `render_as` | Codes | Box |
| --- | --- | --- |
| `OPENING` | `V`, `P` | X, Y (sill; `P` forced to 0), A, H; depth = wall thickness. Cut through the wall. |
| `MARKER` | `TA`, `DR`, `GS`, `CT`, `AP`, `CE` | Measured point = center; box `markerSizeMm` square, `markerDepthMm` deep. Symbolic size. |
| `PANEL` | `RG` | X, Y, A, H; depth `panelDepthMm`. |
| `SOLID` | `CL` | Y = 0; H = ceiling height at the column (min of both sides) if not measured; depth default per code. |
| `SOLID` | `VG` | Y = ceiling − H if not measured (beam hangs from the ceiling); depth default per code. |
| `SOLID` | `TB` | Y = 0 and H = ceiling if not measured; depth default per code. |
| `ENVELOPE` | `RF`, `ES`, `PA`, `HO`, `CA`, `MW`, `LV`, `TJ` | X, Y (0 if not measured), A, H, depth. Translucent. |

"Ceiling height at X" is linear between `height_left_mm` (X = 0) and
`height_right_mm` (X = length). Site codes (`dP`, `dPl`) produce no rows;
they are in the manifest (`floorOutOfLevel*`) and `walls.csv` (`out_of_plumb_mm`).

## Export API (#136)

`POST /api/design-sessions/{sessionId}/room-shell-export`

- Auth: `ROLE_ADMIN` or `ROLE_DESIGNER` (#126).
- Preconditions: the session has a `CONFIRMED` `SiteMeasurement`.
- Idempotent per `measurementUuid` + `revision` + `schemaVersion`: returns the
  existing artifact unless `?force=true`.
- Response `200/201`: `{ artifactId, fileName, sha256, measurementUuid, measurementRevision, schemaVersion, warnings[] }`.
- Errors: `404` session; `403` role; `409 MEASUREMENT_NOT_CONFIRMED`.

`GET /api/design-sessions/{sessionId}/room-shell-export/latest` → same DTO, `404` if none.
`GET /api/design-sessions/{sessionId}/room-shell-export/latest/download` → `application/zip`.

## Backend Behavior (#135)

- `RoomShellExportService` (+ Impl) builds the package from the confirmed
  `SiteMeasurement.payload`; pure geometry in a stateless `RoomShellGeometry`
  class (no Spring, unit-tested).
- Defaults under `app.fusion.room-shell.*` in `application.yml` (wall
  thickness 150 mm, marker 80 × 80 × 25 mm, …), copied into the manifest.
- Stores the zip under `app.output.dir`, creates a `DesignArtifact`
  (new `ArtifactType.FUSION_ROOM_SHELL`, appended via JDL + new Liquibase changeset) with `measurementRevision`.
- Read-only on the measurement; one transaction for the artifact row; file
  written before the row and deleted on rollback.
- Synchronous (milliseconds); no `GenerationJob`.

## Acceptance Criteria

- [ ] For every `fixtures/inputs/*.json`, the exporter produces CSV rows equal
      (after parsing) to `fixtures/expected/<case>/` and the same manifest
      except `generatedAt`, `sha256`.
- [ ] Coordinates match the frame above (wall A on +X, room on `inner_normal`).
- [ ] Reflex corners (270°) and open rooms export correctly (fixtures 02, 04).
- [ ] Closure gap and angle-sum warnings appear in the manifest (fixtures 03, 05).
- [ ] Notes with commas, quotes and accents round-trip (fixture 05).
- [ ] `409` when the measurement is not confirmed; repeated export returns the same artifact.

## Test Plan

- Unit: `RoomShellGeometry` against all golden fixtures.
- IT: resource 200/201/404/403/409, idempotency, zip content.
- Regenerating fixtures: `python3 -I fixtures/build_fixtures.py` (reference
  implementation; it is the tie-breaker when Java and fixtures disagree, until
  this spec is `Implemented`).
