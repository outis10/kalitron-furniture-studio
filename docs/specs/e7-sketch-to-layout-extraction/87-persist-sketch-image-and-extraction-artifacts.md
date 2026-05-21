# E7 Issue 87: Persist Sketch Image and Extraction Artifacts

Status: Implemented
Issue: #87
Epic: #57
Depends on: #58, #59, #60, #61, #62, #63

## Problem

The sketch workflow can convert reviewed extraction data into measured layout
and cabinet plan data, but the original sketch and extraction snapshot also need
to remain tied to the session for audit, support, and later debugging.

## Goal

Persist the uploaded sketch image and raw extraction JSON snapshot when sketch
analysis succeeds.

## Acceptance Criteria

- [x] Original sketch image is saved as a session asset when sketch analysis
  succeeds.
- [x] Sketch binary data is not stored in `ChatMessage` or as long-lived base64
  in the database.
- [x] Raw extraction JSON is saved as a session artifact for audit/debug.
- [x] The saved extraction can be associated with later measured layout and
  cabinet plan saves through the shared design session.
- [x] Existing cabinet plan persistence remains unchanged.
- [x] Failure to persist artifacts is reported instead of silently losing the
  user's sketch.

## Persistence Rules

- Store the original sketch file under `app.output.dir`.
- Save a `DesignImage` record with `ImageType.SKETCH` and a file path.
- Do not populate `DesignImage.imageDataBase64` for sketch uploads.
- Save the extraction response as a `DesignArtifact` with
  `ArtifactType.SKETCH_EXTRACTION_JSON`.
- Store checksum and metadata JSON for the extraction artifact.
- Do not change cabinet plan persistence in this issue.

## Manual Verification

1. Upload and analyze a sketch from the chat UI.
2. Confirm a `DesignImage` row exists with type `SKETCH`.
3. Confirm the sketch file exists under `app.output.dir`.
4. Confirm `image_data_base64` is empty for the sketch row.
5. Confirm a `DesignArtifact` row exists with type `SKETCH_EXTRACTION_JSON`.
6. Continue with `Guardar layout medido` or `Guardar muebles` and confirm the
   existing E7/E6 save flow still works.
