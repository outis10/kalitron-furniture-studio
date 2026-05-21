# E7 Issue 62: Convert Confirmed Sketch Extraction to Measured Layout

Status: Implemented
Issue: #62
Epic: #57
Depends on: #58, #59, #60, #63

## Problem

Reviewed sketch extraction data should become usable measured layout data only
after the user confirms it. Missing or uncertain required fields must block the
conversion instead of being silently saved as facts.

## Goal

Convert a confirmed sketch extraction draft into the existing E6 measured layout
model and save it through the existing measured layout endpoint.

## Acceptance Criteria

- [x] Confirmed walls become RoomWall/measured layout data.
- [x] Confirmed zones become layout zones.
- [x] Confirmed obstacles/services become layout obstacles.
- [x] Missing required layout fields block conversion with actionable messages.
- [x] Conversion reuses existing measured layout validation rules.
- [x] Converted layout can be edited manually after save.

## Conversion Rules

- The user must confirm the reviewed extraction before saving.
- `layout` must map to an existing `KitchenLayout` value.
- At least one wall must have code and length.
- Room height is required and is inferred from a confirmed wall height.
- Global unit is normalized to millimeters:
  - `MM`: unchanged
  - `CM`: multiplied by 10
  - `IN`: multiplied by 25.4 and rounded
- Zones require code, type, wall code, and X position.
- Obstacles require wall code and X position.
- Obstacle types that do not map to supported values become `OTHER`.
- The save action calls `PUT /api/design-sessions/{sessionId}/measured-layout`.

## Out of Scope

- Converting cabinet candidates into the editable cabinet plan. Covered by #61.
- Persisting the intermediate review draft as a separate entity.
- Adding new backend validation rules beyond E6 measured layout validation.

## Manual Verification

1. Upload and analyze a sketch.
2. Edit the review fields.
3. Try saving before confirmation and confirm it is blocked.
4. Confirm the extraction.
5. Save as measured layout.
6. Open `Layout medido` and confirm the layout can be edited manually.
7. Remove required fields such as room height or wall length and confirm an
   actionable error appears.
