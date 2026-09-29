# [E13] Issue 120: Module Library for Distribution

Status: Draft
Issue: #120
Epic: #118
Related: outis10/KFS-APP#21 (offline download), outis10/kalitron-furniture-ai-gateway#40 (proposal input), #122 (rules)
Owner: TBD

## Problem

`CabinetTemplate` already is the module library (code, category, default and
min/max widths, Fusion template, CSV profile), but it lacks what distribution
needs: the exact allowed widths, which row a module goes in, which services it
needs, and a compact representation the app can use offline.

## Goal

Extend `CabinetTemplate` and export an active, versioned library for the app,
the Studio editor and the AI gateway.

## Data Model Impact (`CabinetTemplate`, via JDL)

| Field | Purpose |
| --- | --- |
| `allowedWidthsMm` (String, CSV list, e.g. `300,400,450,600,800,900`) | Widths offered in the app stepper; validated by `DIST_WIDTH_NOT_ALLOWED`. Empty → min/max with `widthStepMm`. |
| `widthStepMm` (Integer) | Fallback step between min and max. |
| `row` (`DistributionRow`: `BASE` "Bajos", `WALL` "Alacenas" — defined in #119) | Where it goes; `TALL` category occupies both. |
| `tags` (String, CSV, e.g. `SINK_BASE`, `COOKTOP_BASE`, `CORNER_BLIND`) | Used by rules (`CONTAINS_POINT`, clearances). |
| `mountHeightMm` (Integer, uppers) | Bottom of upper modules from floor, for collision checks. |
| `shortLabelEsMx`, `icon` | App buttons. |
| `projectTypes` (String, CSV) | `KITCHEN` now; closet modules later (E14). |
| `allowedOnFreestanding` (Boolean) | Can be placed on islands/peninsulas (#128). |

Appliance slots (not templates): `FRIDGE`, `RANGE`, `DISHWASHER`, `WASHER`,
`OTHER` with default and allowed widths — defined in the library JSON.

## API Contract

`GET /api/module-library` — authenticated; `ETag` = `libraryVersion`; `304` on match.

```json
{
  "libraryVersion": "2026-10-01.1",
  "modules": [
    { "code": "BASE_SINK_800", "name": "Base tarja 800", "shortLabelEsMx": "Tarja", "category": "SINK", "row": "BASE", "tags": ["SINK_BASE"], "defaultWidthMm": 800, "allowedWidthsMm": [600, 800, 900], "heightMm": 720, "depthMm": 560, "mountHeightMm": null, "icon": "sink" }
  ],
  "applianceSlots": [
    { "applianceType": "FRIDGE", "labelEsMx": "Refrigerador", "defaultWidthMm": 900, "allowedWidthsMm": [600, 700, 800, 900, 1000] }
  ]
}
```

- Only `isActive = true` templates.
- `libraryVersion` changes whenever any exported field changes (computed hash
  or explicit version — open question).

## Acceptance Criteria

- [ ] Every active template exports allowed widths, row and tags.
- [ ] `304` when unchanged.
- [ ] Seed data (existing templates) updated via a new Liquibase changeset.
- [ ] Template min/max remain consistent with allowed widths (validation on save).

## Test Plan

- Resource IT (200/304/401); service tests for width expansion and version hash.

## Open Questions

- [ ] Real allowed widths per module type (Kalitron catalog).
- [ ] `libraryVersion`: content hash vs manual version.
