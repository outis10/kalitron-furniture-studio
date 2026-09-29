---
epic: E14
title: Closet and wardrobe distribution (future phase)
status: Draft
issues: ""
depends_on: "E12 #104, E13 #118"
---

# E14 — Closet and wardrobe distribution (future phase)

Status: Draft (placeholder — not scheduled)
Epic: #129
Depends on: E12 #104 (site measurement), E13 #118 (versioned distribution)

## Goal

Reuse the E12/E13 pipeline (measure on site → v0 in the app → versioned
distribution in Studio → approved → Fusion CSV) for closets and wardrobes
(`ProjectType.CLOSET` and `BOTH`).

## What changes for closets

| Area | Kitchen (E12/E13) | Closet (E14) |
| --- | --- | --- |
| Measurement | Walls, openings, services, appliances | Walls, openings, beams, baseboards/cornices, switches/outlets; no water/gas; ceiling height critical |
| Distribution unit | Runs of modules in base/upper rows | Full-height **sections** per wall, each with an **interior** stacked in Y (hanging rod, shelves, drawers, shoe rack) and doors (hinged/sliding/none) |
| Library | Kitchen modules + appliance slots | Wardrobe sections, interior components, door systems |
| Rules | Services, clearances, aisles | Hanging heights (long/short garments), drawer/door clash, sliding-door overlap, ceiling/beam clearance, door swing vs room openings, depth vs walkway |
| AI proposals | Zoning around services | Interior configuration from interview (garment mix, users, shoes) |

## Preparations already made in E12/E13 (so E14 needs no migration)

- Entity named `LayoutDistribution` with `projectType` (not kitchen-specific).
- `SiteMeasurement` has `projectType` + `spaceLabel` so a `BOTH` session can
  hold a kitchen and one or more closet measurements.
- Catalog rules, params and library entries carry `projectTypes`.
- Distribution `schemaVersion` allows adding `sections[].interior[]` later.

## Open Questions

- [ ] Closet nomenclature additions for the measurement catalog.
- [ ] Interior components as `CabinetPart`s or as a new entity?
- [ ] Sliding door systems: hardware catalog (`Hardware`) integration.
