# E7 Issue 61: Convert Confirmed Sketch Cabinets to Editable Cabinet Plan

Status: Implemented
Issue: #61
Epic: #57
Depends on: #58, #59, #60, #62, #63

## Problem

Sketch extraction can detect cabinet candidates, but those candidates must not
be treated as fabrication data until the user reviews and confirms them. Once
confirmed, they should become the same editable cabinet plan used by E6.

## Goal

Convert confirmed sketch cabinet candidates into cabinet plan items and save
them through the existing cabinet plan workflow.

## Acceptance Criteria

- [x] Confirmed cabinet candidates become cabinet plan items.
- [x] Cabinet positions, dimensions, labels, and categories are prefilled when
  available.
- [x] Low-confidence cabinet data remains editable and marked for review.
- [x] Existing cabinet plan validation runs before persistence.
- [x] User can continue editing the cabinet list using the E6 editor.
- [x] No BOM/Fusion output is generated automatically.

## Conversion Rules

- The user must confirm the reviewed sketch extraction before saving cabinets.
- The measured layout is saved first because cabinet plan validation depends on
  the measured layout.
- Cabinet category values are normalized into existing `CabinetCategory` values.
- Cabinet `x`, `y`, `z`, width, height, depth, wall code, label, and category
  are mapped into `CabinetPlanItem`.
- Missing `y`, `z`, rotation, height, or depth fields receive editable defaults
  and are marked with low confidence.
- Missing or invalid required cabinet fields block conversion with an actionable
  error.
- The save action calls `PUT /api/design-sessions/{sessionId}/cabinet-plan`.

## Out of Scope

- Persisting the intermediate sketch review draft as its own entity.
- Generating BOM, Fusion 360, CSV, or render outputs.
- Adding AI Gateway extraction fields beyond the current sketch contract.

## Manual Verification

1. Upload and analyze a sketch with cabinet candidates.
2. Edit low-confidence cabinet fields, including `Y`, `Z`, and rotation.
3. Confirm the extraction.
4. Save the detected cabinets.
5. Open `Layout medido` and confirm the cabinet list is editable.
6. Change a cabinet into an invalid overlap or out-of-wall condition and confirm
   existing E6 validation reports the issue before persistence.
