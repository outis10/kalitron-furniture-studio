# ADR-006: Use GPT-4o Vision for Sketch-to-Layout Extraction (E7)

## Status

Accepted

## Context

E7 introduces a sketch-driven path where a carpenter or client uploads a hand-drawn kitchen or closet layout. The system should extract a draft measured layout and cabinet list from the sketch to reduce manual data entry. The result is not fabrication-ready — it requires human review before becoming confirmed design data.

### Requirements

- Accept any sketch image: photo of a drawing, scan, or informal diagram.
- Extract project type, layout shape, walls, appliance zones, obstacles, and cabinet candidates.
- Report confidence per extracted field so Studio can present uncertain items for review.
- Represent missing dimensions as explicit nulls — never invent values.
- Provide follow-up questions and missing-info codes so the UI knows what to ask the user.

### Constraints

- The gateway already uses GPT-4o for chat and spec extraction.
- No dedicated computer vision models are in scope for MVP.
- The extraction must return a stable, versionable JSON contract that Studio can validate.

## Alternatives Considered

| Option | Description | Rejected because |
|--------|-------------|-----------------|
| **Dedicated CV model** (YOLO, LayoutParser) | Object detection trained on floor plan images | Requires labeled training data we don't have; adds GPU dependency for a non-image-gen pipeline; months of work |
| **Google Vision / AWS Rekognition** | Cloud OCR + object detection | Strong at text/object detection but weak on spatial reasoning and domain vocabulary; would still need LLM post-processing; adds a second vendor |
| **GPT-4o Vision (chosen)** | Single model call with a structured JSON system prompt | Zero training data needed; handles spatial reasoning, text reading, and domain vocabulary in one step; already integrated; deterministic with `temperature=0.1` and `response_format=json_object` |
| **Claude Vision** | Anthropic multimodal model | Viable alternative but GPT-4o is already the gateway's model; switching adds a second LLM vendor with no clear advantage for this use case |

## Decision

Use GPT-4o Vision (`gpt-4o`, `detail: high`) with `temperature=0.1` and `response_format: json_object` to extract sketch layout data in a single API call.

The system prompt defines the full expected JSON schema, confidence vocabulary, and extraction rules. The model returns the structured extraction directly — no post-processing pipeline needed.

### Confidence vocabulary

Four levels, compatible with Studio enums:

| Level | Meaning |
|-------|---------|
| `HIGH` | Clearly visible or explicitly labeled in the sketch |
| `MEDIUM` | Likely correct but should be reviewed by the user |
| `LOW` | Weak inference from context |
| `MISSING` | Not found — value is null and must not be treated as known |

### Field-level sourceText

Every extracted field includes `sourceText` — the exact text or visual cue from the sketch that justified the extraction. This allows Studio to show the user why a value was extracted and makes corrections easier.

### Null over invention

The system prompt explicitly forbids inventing dimensions. Missing measurements have `value: null` and `confidence: MISSING`. This is enforced both by the prompt rule and by Studio's conversion rules (only confirmed fields become measured layout data).

### Contract versioning

The response includes `schemaVersion: "1.0"`. Studio validates this field on every response so breaking changes can be detected before deserialization.

## API Contract

```
POST /api/v1/sketch/analyze
Content-Type: application/json
```

Request fields: `image_b64`, `image_mime_type`, `session_code`, `project_type_hint`, `unit_hint`, `language`, `user_prompt`.

Response top-level fields: `schemaVersion`, `requestId`, `projectType`, `layout`, `unit`, `walls`, `zones`, `obstacles`, `cabinetCandidates`, `missingInfo`, `questions`, `warnings`, `rawExtraction`.

All extracted value fields follow the `{value, confidence, sourceText}` pattern (`SketchStringField`).  
All extracted measurements follow the `{value, unit, confidence, sourceText}` pattern (`SketchMeasurement`).

Full contract detail: `docs/specs/e7-sketch-to-layout-extraction/59-sketch-extraction-contract.md`  
Gateway implementation: `outis10/kalitron-furniture-ai-gateway#29`

## Consequences

- Studio receives a typed, versionable JSON contract it can validate before presenting to the user.
- Ambiguous or missing measurements are surfaced explicitly — no silent data quality issues downstream.
- Studio must never write gateway extraction directly into `MeasuredLayoutRequestDTO` or `CabinetPlanItemDTO`. A draft review step is mandatory.
- GPT-4o accuracy on hand-drawn sketches varies with image quality. Low-confidence results are expected and the UI must handle them gracefully (review mode, not auto-save).
- `rawExtraction.textObserved` provides an audit trail of what numbers the model read from the sketch, useful for debugging extraction errors.
- If GPT-4o is unavailable, sketch analysis is unavailable — there is no local fallback. Studio must handle the 503 case and allow the user to retry.
- The prompt is the primary control surface. Improvements to extraction quality are prompt changes, not code changes, and can be shipped without a gateway redeploy if the schema is unchanged.
