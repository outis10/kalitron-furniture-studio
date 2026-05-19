# E3 Issue 14: Visual Concept Generation

Status: Implementing
Issue: #14
Epic: #13

## Problem

After a design conversation reaches `SPECS_READY`, the client needs a visual
concept that turns the agreed style and layout into an image.

## Goal

Allow the client to generate and regenerate a visual concept from the chat,
show the result without a page reload, and persist the generated image metadata
in Studio.

## Acceptance Criteria

- [x] Generate button appears when `specsReady=true`.
- [x] Style, layout and finish selectors shown before generating.
- [x] Loading state with estimated time (~45-60s).
- [x] Generated image displayed full-width in chat.
- [x] Badge: `Based on your photo` or `Generated from description`.
- [x] Regenerate button available after first generation.
- [x] Image saved to `DesignImage` with type `AI_RENDER`.

## API Contract

Studio endpoint:

`POST /api/chat/visual-concept`

Request:

```json
{
  "sessionId": 1,
  "style": "minimalista",
  "layout": "lineal",
  "finish": "negro opaco"
}
```

Response:

```json
{
  "sessionId": 1,
  "sessionCode": "KD-2026-001",
  "imageUrl": "http://localhost:8000/outputs/concepts_...",
  "promptUsed": "kitchen interior design...",
  "pipeline": "txt2img",
  "badge": "Generated from description"
}
```

AI Gateway endpoint:

`POST {app.fastapi.base-url}/api/v1/images/generate`

Studio forwards `session_id`, `style`, `layout`, `finish`, and the latest
reference image base64 when available.

## Implementation Notes

- The existing enum uses `AI_RENDER` for generated AI concepts, so the MVP uses
  that value instead of adding a new `RENDER` enum alias.
- MVP persists the returned gateway URL as `DesignImage.filePath`.
- If a `REFERENCE` image exists for the session and base64 is still available in
  the request flow, the gateway can use img2img. If not, Studio routes to
  txt2img and labels the result as generated from description.

## Test Plan

- [x] Backend integration test for successful generation and `DesignImage` persistence.
- [x] Backend integration test rejecting generation before `SPECS_READY`.
- [x] Backend integration test for gateway unavailable.
- [ ] Manual UI test with txt2img.
- [ ] Manual UI test with img2img when a reference image is present.
