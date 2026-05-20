# E6 Issue 40: Layout Zones and Obstacles

Status: Done
Issue: #40
Epic: #38

## Problem

Measured walls are not enough to generate useful cabinets. Cabinet planning must
know where appliances, utilities, doors, windows, and fixed obstacles are placed
so deterministic generation can avoid blocked areas and reserve functional
zones.

## Goal

Let a designer mark functional zones and physical obstacles on the measured
layout, save them in normalized backend units, and reopen them with the design
session.

## Acceptance Criteria

- [x] Designer can add sink, range, refrigerator, and other functional zones.
- [x] Designer can add windows, doors, columns, utility points, and appliance obstacles.
- [x] Each zone or obstacle references an existing wall segment.
- [x] UI captures positions and dimensions in centimeters.
- [x] Backend stores positions and dimensions in millimeters.
- [x] Layout preview shows zone and obstacle markers.
- [x] Zones and obstacles are reopened from the design session.
- [x] Backend rejects zones or obstacles that reference unknown walls.

## Implementation Notes

- `MeasuredLayoutRequestDTO` remains the API contract for measured walls, zones,
  and obstacles.
- `DesignArtifact` keeps the full inline `measured-layout.json` snapshot for
  lossless reopen behavior.
- `RoomObstacle` persists normalized zone/obstacle records for backend
  validation and later cabinet generation.
- `RoomObstacle` does not yet have a `wallCode` column; this issue stores the
  wall reference in `notes` until a schema extension is justified.
- Functional zones map to `RoomObstacle` types:
  - `SINK` -> `WATER`
  - `RANGE` -> `GAS`
  - refrigerator/appliance-like zones -> `APPLIANCE`
  - unknown zones -> `OTHER`

## API

```http
PUT /api/design-sessions/{sessionId}/measured-layout
GET /api/design-sessions/{sessionId}/measured-layout
```

Both endpoints require authentication and use the same measured layout contract
defined in #41.

## Test Plan

- [x] Integration test saves zones and obstacles and reopens them.
- [x] Integration test persists normalized `RoomObstacle` records.
- [x] Integration test rejects a zone that references an unknown wall.
- [ ] Manual desktop/tablet UI verification.
