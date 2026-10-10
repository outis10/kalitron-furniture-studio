# [E12] Issue 110: Convert Confirmed Site Measurement to Measured Layout

Status: Reviewed
Issue: #110
Epic: #104
Depends on: #107 (model), #112 (confirm endpoint), #113 (catalog/validation)
Related: E6 #39–#41 (measured layout), E7 #62 (sketch → layout, frontend), E13 #124, outis10/KFS-APP#15
Owner: TBD

## Goal

When a `SiteMeasurement` is confirmed (#112) with no validation `ERROR`,
project it into the session's measured layout — the same layout the web
"Captura de espacio" page and the cabinet planning use — and update the session status.

## Non-Goals

- Cabinet plan generation or adjustment (E6/E13); an existing cabinet plan is
  left as is and may need review.
- Projecting `CLOSET` measurements (E14).
- Migrating E6's `notes`-encoded wall codes for old rows.

## Current state (verified 2026-10-10)

- E7 #62 converts the sketch extraction **in the frontend** and saves through
  `PUT /api/design-sessions/{id}/measured-layout` → there is no backend
  conversion to reuse.
- `MeasuredLayoutService.saveMeasuredLayout(sessionId, MeasuredLayoutRequestDTO)`:
  validates (layout required, `roomHeightMm` > 0, ≥ 1 wall, unique codes, wall
  count vs `L_SHAPE`/`U_SHAPE`, zones/obstacles reference a wall and fit in it),
  **deletes and recreates** the session's `RoomWall`/`RoomObstacle` rows, and
  stores a JSON **snapshot** as a `DesignArtifact`.
- **The web page reads the snapshot** (`findMeasuredLayout`), not the rows.
- E6 semantics: `RoomWall.name` = wall code; `angleDeg` = wall **heading**
  (first wall 0, then 90, 180… for an L/U); `positionX/Y` = wall start.
  Obstacles: `xMm` along the wall, **`zMm` = height from the floor**, `yMm` =
  offset from the wall face (0); the wall code goes into `notes`.

## Decision

Build a `MeasuredLayoutRequestDTO` from the measurement and persist it through
`MeasuredLayoutService` (same validation, rows and snapshot as E6), adding
the measurement-only columns from #107 in the same transaction.

## Mapping

### Layout

| `MeasuredLayoutRequestDTO` | Source |
| --- | --- |
| `layout` | `KitchenSpec.layout` if the session has a spec; else the latest snapshot's `layout`; else `CUSTOM` |
| `roomHeightMm` | min over walls of min(`ceilingHeightLeftMm`, `ceilingHeightRightMm`) |
| `defaultBaseDepthMm`, `defaultUpperDepthMm` | from the latest snapshot if any, else null |
| `zones` | empty — functional zones are redefined in the distribution (E13); older snapshots stay as earlier artifacts |
| `notes` | `"Medición en obra {measurementUuid} rev {revision}"` |

### Walls (in capture order)

| Target | Source |
| --- | --- |
| `wallCode` / `RoomWall.name` | `wallCode` |
| `lengthMm` | min(`lengthFloorMm`, `length900Mm`, `lengthCeilingMm`) |
| `heightMm` | min(`ceilingHeightLeftMm`, `ceilingHeightRightMm`) |
| `angleDeg` (heading) | wall A = 0; next = previous + (180 − interior angle of the corner between them), normalized to [0, 360). 90° corners give 0, 90, 180, 270 (E6 convention); a 270° reflex corner turns −90 |
| `startXMm`, `startYMm` | chained from (0, 0): start(next) = start + length · (cos h, sin h), h = heading in E6's convention (clockwise-positive, y toward the room for wall A); rounded to mm |
| `sortOrder` | 0-based capture index |
| `RoomWall` extended (#107) | `lengthFloorMm`, `length900Mm`, `lengthCeilingMm`, `outOfPlumbMm`, `closingMm`, `heightLeftMm`, `heightRightMm`, `siteMeasurement` |

- Corner angle: the value **recomputed** from `squareCheck` when present (#112), else `angleDeg`.
- Open rooms (no corner between the last and first wall) need no closing; headings come only from existing corners.

### Elements → obstacles

| Target | Source |
| --- | --- |
| `obstacleType` | catalog entry `obstacleType` (#105) |
| `label` | catalog `labelEsMx` (+ " · " + element notes, truncated to 100) |
| `wallCode` | wall of the element |
| `xMm` | `xMm` (along the wall, from its left corner) |
| `zMm` | `yMm` (**height from the floor**) |
| `yMm` | 0 (on the wall face) |
| `widthMm`, `heightMm`, `depthMm` | `widthMm`, `heightMm`, `depthMm` (null when not captured, e.g. services) |
| `notes` | element notes (E6 keeps encoding `wallCode=…` in `notes`) |
| `RoomObstacle` extended (#107) | `wallCode`, `croquisCode` = code, `applianceType` (appliances), `siteMeasurement` |

- Door `swing` and value sources stay in the payload only (E15 reads the payload).
- Site codes (`dP`, `dPl`) are not elements: floor level stays on `SiteMeasurement`; out-of-plumb goes to the wall.

## Backend Behavior

- `SiteMeasurementConversionService` (+ Impl): pure mapping
  `SiteMeasurementPayloadDTO` → `MeasuredLayoutRequestDTO` + per-wall/per-element
  extras (no DB access in the mapper; unit-testable with the #113 vectors).
- `MeasuredLayoutService` gets an internal overload
  `saveMeasuredLayout(sessionId, request, MeasuredLayoutSource source)` that sets
  the #107 columns while creating the rows. The public E6 endpoint keeps calling
  the existing method; both paths now also fill `RoomObstacle.wallCode` (additive).
- Called from #112 confirm, in its transaction, after validation passes:
  1. KITCHEN measurement → build the request and persist (rows + snapshot);
  2. previous `CONFIRMED` measurement of the same session and `projectType` → `SUPERSEDED`;
  3. measurement → `CONFIRMED`, `confirmedAt`;
  4. session status → `MEASURED` if it is `DRAFT`, `CHATTING`, `SPECS_READY` or
     `VISUAL_GENERATED`; otherwise unchanged (#107); `updatedAt` = now.
  CLOSET measurements skip step 1 only.
- If E6 validation still rejects the request (should not happen after #113
  validation), the whole confirm rolls back and returns
  `422 MEASUREMENT_NOT_CONFIRMABLE` with reason `PROJECTION_FAILED` and the message.
- Idempotency: a repeated confirm of the same revision is answered by #112 as a
  replay and does not run the conversion again.

## Acceptance Criteria

- [ ] Confirming a KITCHEN measurement replaces the session's walls/obstacles and
      writes a new snapshot; "Captura de espacio" shows the measured walls.
- [ ] Walls: `lengthMm` = min of lengths, `heightMm` = min of heights, headings
      0/90/180/270 for square rooms, correct turn for a 270° corner, extended columns filled.
- [ ] Obstacles: `zMm` = captured height, `wallCode`/`croquisCode`/`applianceType` filled.
- [ ] Previous confirmed measurement of the same project type → `SUPERSEDED`.
- [ ] Session status follows the #107 rule (moves from early statuses, kept otherwise).
- [ ] A CLOSET measurement is confirmed without touching the layout.
- [ ] Any failure rolls back everything (no partial layout, measurement stays `DRAFT`).
- [ ] E6 measured layout ITs pass unchanged (plus `wallCode` now filled).

## Test Plan

- Unit: mapper with the #113 vectors and the E15 fixture payloads (rectangular,
  L with a 270° corner, open U, sloped ceilings): headings, starts, heights, `zMm`.
- ITs: confirm end-to-end through #112 (rows, snapshot, supersede, status rule,
  CLOSET skip, rollback on projection failure).

## Open Questions

Resolved at review (2026-10-10):

- [x] Reuse "#62 internals"? There are none in the backend; reuse `MeasuredLayoutService` instead.
- [x] `layout` when unknown → `KitchenSpec`, then previous snapshot, then `CUSTOM`.
- [x] E6 zones → not carried over (E13 redefines them).
