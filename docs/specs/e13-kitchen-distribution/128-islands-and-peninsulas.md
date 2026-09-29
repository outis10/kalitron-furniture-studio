# [E13] Issue 128: Islands and Peninsulas in the Distribution

Status: Draft
Issue: #128
Epic: #118
Related: #119 (model), #122 (rules), #123 (editor), #124 (materialization), outis10/KFS-APP#25 (v0), outis10/kalitron-furniture-ai-gateway#40/#41
Owner: TBD

## Problem

`KitchenLayout` already has `ISLAND` and `PENINSULA`, but the distribution
model only has runs along walls. Islands and peninsulas are runs placed in the
room, anchored to a wall, and need plan-view position, aisle checks and
two-sided items.

## Goal

Support islands and peninsulas end to end: model, rules, Studio editor,
mobile v0, AI proposals and cabinet materialization.

## Model (addition to the E13 schema)

```json
"freestandingRuns": [
  {
    "runCode": "ISL-1",
    "type": "ISLAND",
    "anchor": { "wallCode": "A", "alongMm": 800, "offsetMm": 1100, "orientation": "PARALLEL" },
    "depthMm": 900,
    "overhangMm": 300,
    "sides": [
      { "side": "FRONT", "items": [ { "itemUuid": "i1", "kind": "MODULE", "templateCode": "BASE_DRAWER_600", "widthMm": 600 } ] },
      { "side": "BACK", "items": [ { "itemUuid": "i2", "kind": "GAP", "widthMm": 1800 } ] }
    ]
  },
  {
    "runCode": "PEN-1",
    "type": "PENINSULA",
    "anchor": { "wallCode": "C", "alongMm": 2400, "offsetMm": 0, "orientation": "PERPENDICULAR" },
    "depthMm": 600,
    "overhangMm": 0,
    "sides": [ { "side": "FRONT", "items": [] } ]
  }
]
```

- `anchor.alongMm`: distance along the anchor wall from its left corner to the
  run start; `offsetMm`: distance from the wall face to the run's back
  (island) — peninsula uses `0` and `PERPENDICULAR` (attached).
- Plan coordinates are derived from `RoomWall` start positions/angles
  (existing `positionX/positionY/angleDeg`) — never typed.
- `FRONT` = working side; `BACK` = seating/storage side; X along the run.
- Uppers are not allowed on freestanding runs (hood over island via a
  `HOOD_ISLAND` library item — open question).

## Rules (added to #122, params in #127)

| Code | Rule | Severity | Scope | Kind |
| --- | --- | --- | --- | --- |
| `DIST_WORK_AISLE_NARROW` | Work aisle between facing wall runs (galley/U) < `aisle.workMinMm` (or `aisle.workMultiCookMinMm` when `multiCook`) | WARNING | `DISTRIBUTION` | `MIN_DISTANCE_BETWEEN_RUNS` |
| `DIST_WALKWAY_NARROW` | Walkway between wall runs < `aisle.walkwayMinMm` | ERROR | `DISTRIBUTION` | `MIN_DISTANCE_BETWEEN_RUNS` |
| `DIST_ISLAND_WORK_AISLE_NARROW` | Island/peninsula work aisle < `island.workAisleMinMm` (or `island.multiCookAisleMinMm`) | WARNING | `DISTRIBUTION` | `MIN_DISTANCE_BETWEEN_RUNS` |
| `DIST_ISLAND_WALKWAY_NARROW` | Island/peninsula side without work zone < `island.walkwayMinMm` | ERROR | `DISTRIBUTION` | `MIN_DISTANCE_BETWEEN_RUNS` |
| `DIST_OUTSIDE_ROOM` | Freestanding run outside the room polygon | ERROR | `RUN` | `INSIDE_ROOM` |
| `DIST_PENINSULA_BLOCKS_OPENING` | Peninsula anchored over a door or low window | ERROR | `RUN` | `NO_COLLISION` |
| `DIST_ISLAND_SERVICE_REQUIRED` | Sink/cooktop on an island/peninsula (needs floor services) | WARNING, ack. | `ITEM` | `CONTAINS_POINT` (none) |
| `DIST_ROOM_NOT_CLOSED` | Room polygon not closed → aisle/inside checks skipped | INFO | `DISTRIBUTION` | `STATE_IS` |

## Editor (#123) and materialization (#124)

- Studio editor gains a **plan view** (top view): wall polygon, wall runs as
  bands, freestanding runs as rectangles; drag with numeric anchor fields;
  aisles shown with their distance.
- Materialization: cabinets of freestanding runs get `positionX/positionY` in
  room coordinates and `rotationDeg`; `wallCode` null, new `runCode` field on
  `Cabinet` (JDL, with #119). E11/E8 must place them by room coordinates.

## Acceptance Criteria

- [ ] Islands and peninsulas round-trip through API, editor and approval.
- [ ] Aisle and inside-room rules covered by conformance vectors.
- [ ] Plan view shows aisle distances and updates live.
- [ ] Approved islands produce cabinets with room coordinates and rotation.

## Open Questions

- [ ] Island hood (ceiling-mounted) as a library item?
- [ ] Floor services for islands: add a floor-service element to the E12 catalog?
