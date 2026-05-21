# 43 - Deterministic Cabinet Layout Validation Rules

Status: Done

GitHub issue: #43

Epic: #38 - Measured layout and cabinet list generation

## Acceptance Criteria

- [x] Validate wall fit by segment and layout type.
- [x] Validate cabinet dimensions against template min/max/defaults.
- [x] Validate appliance zones and obstacles are not blocked.
- [x] Validate required clearances and fillers are represented as warnings or plan items.
- [x] Return structured warnings/errors that the UI can display.
- [x] Unit/integration tests cover valid and invalid plans.

## Implementation Notes

- `CabinetPlanValidator` centralizes deterministic checks so generated and future edited cabinet plans can use the same rules.
- Validation returns structured `CabinetPlanValidationMessageDTO` records with `severity`, `code`, `message`, `wallCode`, and `cabinetCode`.
- Current checks cover:
  - layout wall count review for L, U, and galley layouts;
  - unknown wall references;
  - modules outside wall bounds;
  - overfilled wall segments;
  - cabinet overlaps;
  - template or fallback dimension ranges;
  - required sink/appliance zone representation;
  - blocking obstacles such as doors, columns, low windows, and low outlets;
  - clearance review warnings.
- The cabinet generator now runs the validator before persisting the generated cabinet plan snapshot.

## Test Notes

- Unit tests cover valid and invalid validation outcomes.
- Integration tests continue to cover generation and persisted structured response data.
