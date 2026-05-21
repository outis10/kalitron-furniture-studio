# E7 Issue 59: Sketch Extraction Contract

Status: Reviewed
Issue: #59
Epic: #57
Related Gateway Issue: outis10/kalitron-furniture-ai-gateway#28

## Problem

E7 introduces a sketch-driven path where a carpenter or client can upload a
photo of a hand-drawn kitchen or closet layout. The sketch may contain layout
shape, rough dimensions, appliance zones, obstacles, and cabinet/module counts.

The AI Gateway can help extract those details, but the result is not
fabrication-ready. Studio must treat the gateway response as a draft extraction
that requires review before it becomes a measured layout or cabinet plan.

## Goal

Define the structured contract Studio expects from the AI Gateway when a sketch
image is analyzed.

The contract must support:

- detected project type and layout type;
- walls, zones, obstacles, and cabinet candidates;
- measurements with units and original source text when available;
- confidence per extracted field;
- missing information and follow-up questions;
- separation between raw AI extraction and confirmed Studio data.

## Acceptance Criteria

- [x] Contract includes detected project type and layout type.
- [x] Contract includes walls, zones, obstacles, and cabinet candidates.
- [x] Contract includes measurements with units and source text when available.
- [x] Every extracted field has confidence: `HIGH`, `MEDIUM`, `LOW`, or
      `MISSING`.
- [x] Contract includes `missingInfo` and questions to ask the user.
- [x] Contract separates raw AI extraction from confirmed Studio data.

## Boundary Decision

The AI Gateway owns image analysis and draft extraction.

Studio owns:

- upload orchestration;
- auth and session association;
- validation of enum-like values;
- user review and correction;
- conversion to `MeasuredLayoutRequestDTO`;
- conversion to `CabinetPlanItemDTO`;
- persistence.

The gateway must not write Studio entities directly. Studio must not assume a
sketch extraction is dimensionally correct until the user confirms it.

## API Draft

Studio will call the gateway through a backend service, not directly from the
browser.

```http
POST /api/v1/sketch/analyze
Content-Type: multipart/form-data
```

Request parts:

| Part | Type | Required | Notes |
| --- | --- | --- | --- |
| `image` | file | yes | Sketch photo or scan. |
| `context` | JSON string | no | User prompt, session code, project hint, unit hint. |

Context example:

```json
{
  "sessionCode": "KD-2026-120",
  "projectTypeHint": "KITCHEN",
  "unitHint": "MM",
  "language": "es-MX",
  "userPrompt": "Cocina lineal con tarja al centro y módulos superiores."
}
```

## Response Contract

```json
{
  "schemaVersion": "1.0",
  "requestId": "sketch-20260521-001",
  "projectType": {
    "value": "KITCHEN",
    "confidence": "HIGH",
    "sourceText": "cocina"
  },
  "layout": {
    "value": "LINEAR",
    "confidence": "MEDIUM",
    "sourceText": "vista frontal lineal"
  },
  "unit": {
    "value": "MM",
    "confidence": "MEDIUM",
    "sourceText": "600, 312, 500"
  },
  "walls": [
    {
      "wallCode": {
        "value": "A",
        "confidence": "HIGH"
      },
      "length": {
        "value": 3120,
        "unit": "MM",
        "confidence": "MEDIUM",
        "sourceText": "312"
      },
      "height": {
        "value": 2400,
        "unit": "MM",
        "confidence": "MISSING",
        "sourceText": null
      },
      "angleDeg": {
        "value": 0,
        "confidence": "HIGH"
      }
    }
  ],
  "zones": [
    {
      "zoneCode": {
        "value": "SINK-1",
        "confidence": "HIGH"
      },
      "zoneType": {
        "value": "SINK",
        "confidence": "HIGH",
        "sourceText": "tarja dibujada"
      },
      "wallCode": {
        "value": "A",
        "confidence": "MEDIUM"
      },
      "x": {
        "value": 1450,
        "unit": "MM",
        "confidence": "LOW",
        "sourceText": null
      },
      "width": {
        "value": 800,
        "unit": "MM",
        "confidence": "LOW",
        "sourceText": null
      }
    }
  ],
  "obstacles": [
    {
      "obstacleType": {
        "value": "WINDOW",
        "confidence": "LOW",
        "sourceText": "rectangulo sobre tarja"
      },
      "label": {
        "value": "Posible ventana",
        "confidence": "LOW"
      },
      "wallCode": {
        "value": "A",
        "confidence": "LOW"
      },
      "x": {
        "value": null,
        "unit": "MM",
        "confidence": "MISSING",
        "sourceText": null
      },
      "width": {
        "value": null,
        "unit": "MM",
        "confidence": "MISSING",
        "sourceText": null
      }
    }
  ],
  "cabinetCandidates": [
    {
      "candidateCode": "A-001",
      "category": {
        "value": "LOWER",
        "confidence": "HIGH"
      },
      "label": {
        "value": "Base puerta izquierda",
        "confidence": "MEDIUM"
      },
      "wallCode": {
        "value": "A",
        "confidence": "MEDIUM"
      },
      "x": {
        "value": 0,
        "unit": "MM",
        "confidence": "LOW",
        "sourceText": null
      },
      "width": {
        "value": 600,
        "unit": "MM",
        "confidence": "MEDIUM",
        "sourceText": "600"
      },
      "height": {
        "value": 720,
        "unit": "MM",
        "confidence": "MISSING",
        "sourceText": null
      },
      "depth": {
        "value": 600,
        "unit": "MM",
        "confidence": "MEDIUM",
        "sourceText": "600"
      },
      "doors": {
        "value": 2,
        "confidence": "LOW",
        "sourceText": "dos frentes dibujados"
      },
      "drawers": {
        "value": 0,
        "confidence": "LOW"
      }
    }
  ],
  "missingInfo": [
    {
      "code": "ROOM_HEIGHT_MISSING",
      "message": "No se detectó altura total del cuarto.",
      "severity": "WARNING"
    },
    {
      "code": "CONFIRM_UNITS",
      "message": "Confirmar si las medidas del dibujo están en milímetros o centímetros.",
      "severity": "WARNING"
    }
  ],
  "questions": [
    "¿Las medidas del dibujo están en milímetros o centímetros?",
    "¿Cuál es la altura total del cuarto?"
  ],
  "warnings": [
    "Las posiciones X de los módulos son aproximadas porque el dibujo no tiene escala completa."
  ],
  "rawExtraction": {
    "model": "gateway-configured-vision-model",
    "pipeline": "sketch-analysis",
    "textObserved": ["600", "312", "500", "tarja"],
    "generatedAt": "2026-05-21T12:00:00Z"
  }
}
```

## Stable Values

The gateway response should use values compatible with Studio enums and DTOs.

### Project Type

- `KITCHEN`
- `CLOSET`
- `BOTH`

Maps to `ProjectType`.

### Layout

- `LINEAR`
- `L_SHAPE`
- `U_SHAPE`
- `ISLAND`
- `PENINSULA`
- `GALLEY`
- `CUSTOM`

Maps to `KitchenLayout`.

### Confidence

- `HIGH`: field is clearly visible or explicitly labeled.
- `MEDIUM`: field is likely correct but should be reviewed.
- `LOW`: field is a weak inference.
- `MISSING`: field was not found and should not be treated as known.

### Units

- `MM`
- `CM`
- `IN`
- `UNKNOWN`

Studio normalizes confirmed measurements to millimeters.

### Zone Types

Initial stable zone values:

- `SINK`
- `RANGE`
- `COOKTOP`
- `REFRIGERATOR`
- `DISHWASHER`
- `OVEN`
- `PANTRY`
- `TALL_STORAGE`
- `OPEN_SHELVING`
- `WORKSPACE`
- `APPLIANCE`
- `OTHER`

### Obstacle Types

Use existing `RoomObstacleType` values where possible. If the gateway cannot map
a detected item to a supported value, it should return `OTHER` with a warning.

Initial expected values:

- `WINDOW`
- `DOOR`
- `COLUMN`
- `OUTLET`
- `WATER`
- `GAS`
- `DRAIN`
- `RANGE_HOOD`
- `APPLIANCE`
- `OTHER`

### Cabinet Categories

Use existing `CabinetCategory` values:

- `UPPER`
- `LOWER`
- `CORNER`
- `TALL`
- `SINK`
- `ISLAND`
- `DRAWER_BASE`
- `APPLIANCE`
- `FILLER`
- `PANEL`

## Studio Draft DTO Shape

When implemented, Studio should avoid writing the gateway response directly into
`MeasuredLayoutRequestDTO` or `CabinetPlanItemDTO`. A sketch extraction is a
draft review model first.

Recommended Studio DTOs:

- `SketchExtractionResponseDTO`
- `SketchFieldDTO<T>`
- `SketchMeasurementDTO`
- `SketchWallCandidateDTO`
- `SketchZoneCandidateDTO`
- `SketchObstacleCandidateDTO`
- `SketchCabinetCandidateDTO`
- `SketchMissingInfoDTO`

`SketchFieldDTO<T>`:

```json
{
  "value": "LINEAR",
  "confidence": "MEDIUM",
  "sourceText": "vista frontal lineal"
}
```

`SketchMeasurementDTO`:

```json
{
  "value": 600,
  "unit": "MM",
  "confidence": "MEDIUM",
  "sourceText": "600"
}
```

## Conversion Rules

Studio can convert a reviewed extraction into `MeasuredLayoutRequestDTO` only
after the user confirms or corrects the draft.

Initial conversion rules:

- only confirmed fields become measured layout data;
- `MISSING` fields must remain empty or trigger required questions;
- `LOW` confidence dimensions require explicit user confirmation;
- all confirmed measurements are normalized to millimeters;
- wall and cabinet positions are allowed to be approximate only if the UI labels
  them as approximate;
- generated cabinet plan validation still runs after conversion;
- user edits override gateway values.

## Validation Rules

Studio validation for the gateway response:

- `schemaVersion` is required.
- `projectType.value` must map to `ProjectType`.
- `layout.value` must map to `KitchenLayout`.
- Every non-null extracted field must include `confidence`.
- Measurements must include `unit` when `value` is present.
- `MISSING` measurements should have `value: null`.
- Wall codes must be unique.
- Zones, obstacles, and cabinets must reference an extracted wall code when one
  is known.
- Unknown enum-like values should be rejected or surfaced as warnings before
  user confirmation.
- Raw AI metadata is stored only as artifact metadata, not as confirmed design
  state.

## Error Responses

Studio should map gateway failures to user-safe errors:

| Gateway Cause | Studio Behavior |
| --- | --- |
| Unsupported image type | Show validation error and ask for another image. |
| Image too large | Show size guidance before retry. |
| Model unavailable | Keep the uploaded image and allow retry. |
| Invalid gateway JSON | Log correlation id and show retry-safe error. |
| Low-confidence extraction | Show review UI with warnings; do not auto-save. |

## Persistence

This issue does not require a database migration.

Future implementation should persist:

- uploaded sketch as `DesignImage` or `DesignArtifact`;
- raw extraction JSON as a `DesignArtifact` or `GenerationJob` output;
- confirmed measured layout only after user approval;
- confirmed cabinet plan only after user approval and deterministic validation.

Records not stored as confirmed data:

- unreviewed `LOW` confidence dimensions;
- raw model text as cabinet/layout facts;
- inferred fabrication dimensions.

## Observability

The Studio call to the gateway should carry or generate:

- design session id;
- session code;
- gateway request id;
- artifact id for uploaded sketch;
- correlation id in logs.

Logs must avoid dumping full image payloads.

## Implementation Sequence

Recommended next issues:

1. Implement gateway contract in outis10/kalitron-furniture-ai-gateway#28.
2. Add Studio gateway client DTOs against this contract.
3. Build upload/review UI for sketch extraction.
4. Convert confirmed extraction to measured layout.
5. Convert confirmed cabinet candidates to editable cabinet plan.
