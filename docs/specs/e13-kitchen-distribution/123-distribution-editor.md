# [E13] Issue 123: Distribution Editor in Studio with Versions and Ripple

Status: Draft
Issue: #123
Epic: #118
Related: #121 (API), #122 (validation), #124 (approve), #125 (AI proposals)
Owner: TBD

## Goal

As a designer, I continue from the v0 captured on site (or from an AI
proposal), refine the distribution per wall, see validation live, compare
versions and approve one.

## Non-Goals

- 3D view (E8 prototype after approval).
- Free-form drawing.

## User Flow

1. Session → "Distribución" tab: version list (v0 Preliminar, Propuestas IA,
   borradores, aprobada) with status badges and issue counts.
2. "Continuar desde v0" (or from any version) → new `DRAFT`.
3. Editor per wall: elevation strip with measured elements (windows, services
   `TA`/`DR`/`GS`, beams, doors) and two rows (base, alacenas).
4. Add module from library panel (filtered by row), filler, appliance slot, gap.
5. Change width (allowed widths dropdown/stepper) → **following items shift**
   (ripple). Option "absorber en el siguiente hueco" when the next item is a `GAP`.
6. Drag to reorder within a run.
7. Validation panel (debounced `validate`), click issue → highlight item;
   acknowledge acknowledgeable errors with a reason.
8. Compare two versions side by side (per wall item lists + diff highlights).
8b. **Plan view** (top view) to place islands and peninsulas and see aisle distances (#128).
9. "Aprobar" (#124).

## Frontend Behavior

- Access: `ROLE_ADMIN` / `ROLE_DESIGNER` (#126).
- Module: `modules/design/distribution/` (not `entities/`); API in
  `shared/api/distributionApi.ts`; hooks `useDistribution.ts`,
  `useDistributionValidation.ts`.
- X computed client-side for display with the same formula as the server;
  the server response is authoritative.
- Loading: skeleton. Empty (no measurement confirmed): message + link to measurement.
- Error: save conflict (`412`) → reload prompt keeping local changes for copy.
- Success: saved indicator; issue counts per wall.
- Mobile/tablet: read-only view with version list; editing targets desktop.

## Acceptance Criteria

- [ ] Studio opens the v0 captured on site without re-capture.
- [ ] Changing a width shifts all following items in the run.
- [ ] Validation issues update within ~0.5 s after an edit.
- [ ] Versions can be compared side by side.
- [ ] Preliminary and AI versions are clearly labeled.

## Test Plan

- Component tests: ripple, gap absorption, reorder, issue highlighting.
- Cypress: open v0 → create v1 → change width → validate → save.
