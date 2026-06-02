---
issue: "#75"
title: Visual application contract
status: Implemented
---

# #75 — Visual application contract

## POST /api/design-sessions/{sessionId}/styled-render

**Auth:** Bearer JWT (authenticated)

### Request

```json
{
  "style": "moderno",
  "finish": "white matte",
  "countertopMaterial": "quartz",
  "backsplashNotes": "subway tile",
  "handleStyle": "bar",
  "wallColor": "light gray",
  "notes": "Luz natural, encimera blanca"
}
```

| Field | Type | Required | Values |
|-------|------|----------|--------|
| style | string | yes | moderno \| rustico \| minimalista \| clasico \| industrial |
| finish | string | no | white matte \| oak wood \| gray matte \| black matte |
| countertopMaterial | string | no | quartz \| granite \| marble \| laminate |
| backsplashNotes | string | no | free text |
| handleStyle | string | no | bar \| knob \| integrated \| none |
| wallColor | string | no | free text |
| notes | string | no | free text |

### Response 200

```json
{
  "jobId": 12,
  "sessionId": 101,
  "sessionCode": "KD-2026-022",
  "status": "DONE",
  "promptUsed": "kitchen interior design, moderno...",
  "pipeline": "img2img",
  "startedAt": "2026-06-02T10:00:00Z",
  "finishedAt": "2026-06-02T10:00:45Z",
  "warnings": [],
  "artifacts": [
    { "artifactId": 55, "artifactType": "STYLED_RENDER", "fileName": "styled-render.png", "mimeType": "image/png" }
  ]
}
```

### Errors

| Code | Condition |
|------|-----------|
| 404 | Session or PROTOTYPE_PREVIEW artifact not found |
| 502 | AI Gateway unreachable |
| 500 | Image download or storage failure |

## GET /api/design-sessions/{sessionId}/styled-render/latest

Returns the latest styled render job for the session. 404 if none exists.

## GET /api/design-sessions/{sessionId}/styled-render/{artifactId}/image

Returns the PNG bytes of the styled render artifact. Auth required.
