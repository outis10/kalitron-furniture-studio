# E3 Issue 15: Preserve Photo Structure

Status: Implementing
Issue: #15
Epic: #13

## Problem

When a client uploads a reference photo, generated concepts should use that
photo as structure input instead of behaving like a blank txt2img prompt.

## Goal

Use the latest active reference image from the design session when generating a
visual concept, route the request through img2img in the AI Gateway, and show
the photo-based badge in chat.

## Acceptance Criteria

- [x] When `clientImageB64` present, AI Gateway routes to img2img pipeline.
- [x] Studio automatically sends the latest reference image when available.
- [x] ControlNet Canny extracts structural lines from client photo.
- [x] ControlNet strength is `0.75`.
- [x] img2img denoise is `0.75`.
- [x] Without photo, routes to txt2img automatically.
- [x] Badge shown: `Based on your photo`.
- [ ] Manual visual check confirms the generated image reflects the room structure.

## Implementation Notes

- `DesignImage.imageDataBase64` stores the normalized base64 payload for local
  MVP reference reuse. This should move to object storage when S3/MinIO is
  introduced.
- Studio prefers an explicit `clientImageBase64` in the visual concept request,
  then falls back to the most recent active `REFERENCE` image for the session.
- AI Gateway selects img2img whenever `client_image_b64` is present; otherwise
  it uses txt2img.

## Test Plan

- [x] Backend integration test persists reference image base64.
- [x] Backend integration test sends latest reference image to visual generation.
- [x] Backend integration test keeps txt2img behavior when no reference exists.
- [x] Gateway workflow test validates Canny node, `strength=0.75`, and `denoise=0.75`.
- [ ] Manual UI test with uploaded reference photo.
