# E6 Issue 41: Layout and Cabinet Planning Contracts

Status: Done
Issue: #41
Epic: #38

## Problem

E6 needs a contract between the guided layout UI and deterministic cabinet
generation before implementation starts. A reference photo can help with style
and module suggestions, but cabinet dimensions must come from measured layout
data and backend validation.

## Goal

Define backend DTOs and API contracts for measured layout capture, wall
segments, functional zones, obstacles, generated cabinet plan items, and
validation messages.

## Acceptance Criteria

- [x] Define DTOs for measured layout, wall segments, zones, and obstacles.
- [x] Define DTO/entity contract for generated cabinet plan items.
- [x] Decide whether existing `RoomWall`, `RoomObstacle`, `CabinetTemplate`, and
      `Cabinet` entities are enough or need extension.
- [x] Add validation rules for required dimensions and supported layout types.
- [x] Document API request/response examples in SDD specs.

## DTO Contracts

Studio contract DTOs live in `com.kalitron.studio.service.dto`:

- `MeasuredLayoutRequestDTO`
- `MeasuredWallSegmentDTO`
- `LayoutZoneDTO`
- `LayoutObstacleDTO`
- `CabinetPlanItemDTO`
- `CabinetPlanValidationMessageDTO`
- `CabinetPlanResponseDTO`

These DTOs are API/service contracts only. They do not create a new persistence
model by themselves.

## Entity Decision

Use existing entities for MVP persistence:

- `RoomWall` stores measured wall segments.
- `RoomObstacle` stores physical obstacles and utility points.
- `CabinetTemplate` stores module defaults, min/max widths, and fabrication
  profile hints.
- `Cabinet` stores generated and later edited cabinet plan items.

Known gaps for later issues:

- `RoomObstacle` does not currently store a wall/segment reference. The contract
  uses `wallCode`; persistence can initially infer it through notes or add a
  new field in #40 if needed.
- `Cabinet` does not currently store `wallCode`. It has position fields and
  sequence, which are enough for first-pass layout generation; add an explicit
  wall reference later if validation/editing needs it.
- Functional zones such as sink/stove/refrigerator can be represented in the
  contract as `LayoutZoneDTO`. Persistence can map them to `RoomObstacle` with
  `APPLIANCE`, `WATER`, `GAS`, or `DRAIN` where appropriate.

## API Draft

### Save measured layout

```http
PUT /api/design-sessions/{sessionId}/measured-layout
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "sessionId": 1501,
  "layout": "L_SHAPE",
  "roomHeightMm": 2400,
  "defaultBaseDepthMm": 600,
  "defaultUpperDepthMm": 350,
  "walls": [
    {
      "wallCode": "A",
      "lengthMm": 3000,
      "heightMm": 2400,
      "angleDeg": 0,
      "startXMm": 0,
      "startYMm": 0,
      "sortOrder": 1
    },
    {
      "wallCode": "B",
      "lengthMm": 2500,
      "heightMm": 2400,
      "angleDeg": 90,
      "startXMm": 3000,
      "startYMm": 0,
      "sortOrder": 2
    }
  ],
  "zones": [
    {
      "zoneCode": "SINK-1",
      "zoneType": "SINK",
      "wallCode": "A",
      "xMm": 400,
      "widthMm": 800,
      "clearanceLeftMm": 100,
      "clearanceRightMm": 100
    },
    {
      "zoneCode": "RANGE-1",
      "zoneType": "RANGE",
      "wallCode": "A",
      "xMm": 1500,
      "widthMm": 760
    }
  ],
  "obstacles": [
    {
      "obstacleType": "WINDOW",
      "label": "Ventana sobre tarja",
      "wallCode": "A",
      "xMm": 350,
      "zMm": 1100,
      "widthMm": 900,
      "heightMm": 700
    }
  ],
  "notes": "Medidas confirmadas por el cliente."
}
```

Response: `204 No Content` or the saved measured layout representation.

### Generate cabinet plan

```http
POST /api/design-sessions/{sessionId}/cabinet-plan
Authorization: Bearer <token>
Content-Type: application/json
```

Request body can reuse `MeasuredLayoutRequestDTO` or accept only generation
options after a measured layout has been saved.

Response:

```json
{
  "sessionId": 1501,
  "sessionCode": "KD-2026-041",
  "layout": "L_SHAPE",
  "valid": true,
  "cabinetCount": 5,
  "totalOccupiedLengthMm": 2860,
  "cabinets": [
    {
      "cabinetCode": "A-001",
      "templateCode": "BASE_SINK_800",
      "category": "SINK",
      "label": "Base tarja 800",
      "widthMm": 800,
      "heightMm": 720,
      "depthMm": 560,
      "doors": 2,
      "drawers": 0,
      "shelves": 1,
      "finish": "HIGH_GLOSS_WHITE",
      "wallCode": "A",
      "xMm": 400,
      "yMm": 0,
      "zMm": 0,
      "rotationDeg": 0,
      "positionSeq": 1,
      "materialCode": "MEL-18-WHITE-GLOSS"
    }
  ],
  "validationMessages": [
    {
      "severity": "WARNING",
      "code": "FILLER_RECOMMENDED",
      "message": "Quedan 140 mm libres en pared A; considerar relleno.",
      "wallCode": "A"
    }
  ]
}
```

## Validation Rules

Initial validation rules:

- `layout` is required and must be one of the existing `KitchenLayout` values.
- `roomHeightMm` is required and must be greater than `0`.
- At least one wall segment is required.
- Each wall needs `wallCode` and positive `lengthMm`.
- `LINEAR` requires at least one wall.
- `L_SHAPE` requires at least two walls.
- `U_SHAPE` requires at least three walls.
- `ISLAND` and `PENINSULA` require at least one wall and should allow island or
  peninsula zones in later issues.
- Zones and obstacles must reference an existing `wallCode`.
- Width, height, depth, and clearance values must be positive when provided.
- Generated cabinet dimensions must respect `CabinetTemplate` min/max/defaults.
- Generated cabinets must not overlap hard obstacles or appliance zones.
- Validation returns structured `INFO`, `WARNING`, or `ERROR` messages.

## AI Gateway Boundary

The AI Gateway can later return suggestions such as likely module intent or
photo-detected appliances, but it must not be the authority for measured
dimensions or the final cabinet list.

The deterministic path is:

```text
measured layout + zones + obstacles + templates -> Studio backend validation -> CabinetPlanResponseDTO
```

## Test Plan

- [x] Java compile validates DTO contracts.
- [ ] #39 tests layout save validation.
- [ ] #40 tests zone and obstacle persistence.
- [ ] #44 tests cabinet plan generation.
- [ ] #43 tests deterministic validation rules.
