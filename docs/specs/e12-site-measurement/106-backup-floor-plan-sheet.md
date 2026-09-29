# [E12] Issue 106: Printable Backup Floor-Plan Sheet

Status: Draft
Issue: #106
Epic: #104
Related: #105 (legend from catalog), outis10/KFS-APP#12 (manual entry of sheet data)
Owner: TBD

## Problem

If the phone or the laser fails on site, the designer still needs a structured
way to record measurements that follows the same rules as the app.

## Goal

A one-page printable PDF per session that mirrors the app's capture structure,
so data can be typed into the app later as manual values.

## Non-Goals

- QR codes, ArUco markers, elevation sheets, reserve sheets (removed from v1).
- Reading the sheet with AI or OCR.

## Sheet layout (letter, landscape)

- Header: logo, `sessionCode`, date, designer, ceiling height box. **No client
  address or phone.**
- Grid area for floor plan with wall/corner labelling reminder (clockwise from
  left of entry door).
- Table per wall: `L piso / L 900 / L techo`, out-of-plumb, closing measurement (`Cierre`).
- Element table: `Muro | Código | X | Y | A | H | Fondo | Notas`.
- Legend generated from the catalog (#105); units note "todo en mm".

## API Contract

`GET /api/design-sessions/{id}/backup-sheet.pdf`

- Auth: `ROLE_ADMIN`, or `ROLE_MEASURER` assigned to the session (#116).
- `200 application/pdf`; `401`, `403`, `404`.

## Backend Behavior

- Resource: `web/rest/custom/BackupSheetResource`; service `BackupSheetService`.
- Read-only; nothing persisted.

## Frontend Behavior

- Studio session detail → "Imprimir hoja de respaldo". Loading spinner; error toast.
- Mobile app: optional link opening the same PDF (KFS-APP#10, online only).

## Acceptance Criteria

- [ ] Prints legibly on letter paper at 100%.
- [ ] Legend lists every catalog code with its es-MX label.
- [ ] PDF contains no client address or phone.

## Test Plan

- Backend: PDF generated, page count 1, contains session code and legend codes; 403 foreign session.
- Manual: print and fill.

## Open Questions

- [ ] PDF library (OpenPDF vs PDFBox).
