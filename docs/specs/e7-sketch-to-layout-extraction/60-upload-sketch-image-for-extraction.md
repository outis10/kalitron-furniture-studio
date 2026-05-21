# E7 Issue 60: Upload Sketch Image for Extraction

Status: Implemented
Issue: #60
Epic: #57
Depends on: #58, #59

## Problem

After defining the sketch extraction contract and backend proxy, Studio needs a
user-facing way to upload a sketch image from the design session and pass it to
the extraction workflow.

The upload must be clearly labeled as a draft sketch input, not a confirmed
layout or fabrication plan.

## Goal

Let a designer or carpenter upload a sketch image from the chat session and run
the sketch extraction endpoint.

## Acceptance Criteria

- [x] User can upload a sketch image from the design session.
- [x] Image is stored as a design artifact or design image.
- [x] UI clearly labels the image as a sketch/reference, not a confirmed plan.
- [x] Upload supports common formats such as JPG and PNG.
- [x] Invalid image types or oversized files show actionable errors.
- [x] Uploaded sketch can be passed to the extraction workflow.

## User Flow

1. User starts or resumes a design chat session.
2. User selects `Subir boceto` in the side panel.
3. Studio validates local image type and size.
4. Studio shows a preview and file name.
5. User selects `Analizar`.
6. Studio calls `POST /api/chat/sketch-analysis`.
7. Studio adds the uploaded sketch to the chat history as draft input.
8. Studio adds a summarized extraction response to the chat history.

## UX Rules

- The sketch panel appears only after a session exists.
- The copy states that the sketch is a draft, not a confirmed plan.
- The upload accepts JPG, PNG, and WebP.
- The maximum local file size is 5 MB.
- While analysis is running, chat send and sketch actions are disabled.
- The extraction summary must show that results require review.

## Out of Scope

- Editable review UI for extracted walls/zones/cabinets. Covered by #63.
- Conversion to measured layout. Covered by #62.
- Conversion to editable cabinet plan. Covered by #61.
- AI Gateway model behavior. Covered in the gateway E7 issues.

## API Usage

Frontend calls:

```http
POST /api/chat/sketch-analysis
Content-Type: application/json
```

Request:

```json
{
  "sessionId": 1501,
  "imageBase64": "base64-image-content",
  "imageFileName": "boceto.png",
  "imageMimeType": "image/png",
  "imageSizeBytes": 123456,
  "projectTypeHint": "KITCHEN",
  "unitHint": "MM",
  "userPrompt": "Cocina lineal con tarja al centro"
}
```

Response follows the #59 sketch extraction contract.

## Manual Verification

1. Start a chat session.
2. Select `Subir boceto`.
3. Choose a PNG or JPG sketch under 5 MB.
4. Confirm preview and file name appear in the side panel.
5. Select `Analizar`.
6. Confirm the chat history shows the sketch image and an assistant summary.
7. Try a GIF and confirm an actionable validation error appears.
8. Try a file over 5 MB and confirm an actionable validation error appears.
