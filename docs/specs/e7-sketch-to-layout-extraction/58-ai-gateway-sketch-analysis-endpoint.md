# E7 Issue 58: AI Gateway Sketch Analysis Endpoint

Status: Implemented
Issue: #58
Epic: #57
Related Gateway Issue: outis10/kalitron-furniture-ai-gateway#28

## Problem

Studio needs a backend endpoint that accepts a sketch image, validates the
design session and image metadata, calls the AI Gateway sketch analysis API, and
returns the structured sketch extraction contract from #59.

The browser should not call the AI Gateway directly.

## Goal

Add the Studio backend contract for sketch analysis.

## Acceptance Criteria

- [x] Studio can call the gateway with sketch image metadata/content.
- [x] Gateway returns the structured sketch extraction contract.
- [x] Gateway reports low confidence and missing data instead of inventing
      certainty.
- [x] Gateway failures return actionable Studio errors.
- [x] Request/response examples are documented.

## Studio Endpoint

```http
POST /api/chat/sketch-analysis
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "sessionId": 1501,
  "imageBase64": "base64-image-content",
  "imageFileName": "boceto-cocina.png",
  "imageMimeType": "image/png",
  "imageSizeBytes": 348812,
  "projectTypeHint": "KITCHEN",
  "unitHint": "MM",
  "userPrompt": "Cocina lineal con tarja al centro y módulos superiores."
}
```

Response:

```json
{
  "schemaVersion": "1.0",
  "requestId": "sketch-20260521-001",
  "sessionId": 1501,
  "sessionCode": "KD-2026-120",
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
    "sourceText": "600"
  },
  "walls": [],
  "zones": [],
  "obstacles": [],
  "cabinetCandidates": [],
  "missingInfo": [],
  "questions": [],
  "warnings": [
    "Confirma medidas antes de guardar."
  ]
}
```

## Gateway Call

Studio calls:

```http
POST /api/v1/sketch/analyze
Content-Type: application/json
```

Gateway request:

```json
{
  "session_id": "KD-2026-120",
  "image_b64": "base64-image-content",
  "image_mime_type": "image/png",
  "image_file_name": "boceto-cocina.png",
  "project_type_hint": "KITCHEN",
  "unit_hint": "MM",
  "user_prompt": "Cocina lineal con tarja al centro y módulos superiores."
}
```

The gateway response must follow the #59 sketch extraction contract.

## Validation

- User must be authenticated.
- `sessionId` is required and must exist.
- `imageBase64` is required.
- Allowed image types: `image/jpeg`, `image/png`, `image/webp`.
- Maximum sketch size: 5 MB.
- `projectTypeHint`, when present, must map to `ProjectType`.
- Gateway response must include `schemaVersion`.

## Persistence

Studio stores the uploaded sketch as `DesignImage`:

- `imageType`: `SKETCH`
- `filePath`: `sketch-images/{sessionCode}/{timestamp}-{fileName}`
- `imageDataBase64`: normalized base64 image content
- `description`: `Sketch uploaded for AI extraction`

Studio does not persist extracted layout, zones, obstacles, or cabinets as
confirmed design data in this issue.

## Error Responses

| Status | Cause |
| --- | --- |
| 400 | Missing image, unsupported type, or image too large. |
| 401 | Missing authentication. |
| 404 | Design session not found. |
| 503 | AI Gateway unavailable or returned an invalid response. |

## Manual Verification

1. Start Studio backend.
2. Ensure AI Gateway is running with `/api/v1/sketch/analyze`.
3. Create or reuse a design session.
4. POST a JPG, PNG, or WebP sketch as base64 to `/api/chat/sketch-analysis`.
5. Confirm the response includes `schemaVersion`, `projectType`, `layout`,
   confidence fields, warnings/questions where needed, and no confirmed layout
   is saved automatically.
