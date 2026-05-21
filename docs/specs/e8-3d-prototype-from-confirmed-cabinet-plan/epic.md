# E8 Epic: 3D Prototype from Confirmed Cabinet Plan

Status: Reviewed
Issue: #64

## Problem

After a measured layout and cabinet plan are confirmed, the client and designer
need a spatial validation step before applying detailed visual styles or moving
toward fabrication exports.

## Goal

Generate a base 3D prototype from confirmed layout and cabinet data. The
prototype is a technical blockout used to review placement, volume, wall
relationships, zones, and clearances.

## Scope

- Define the Studio/Gateway contract for prototype generation.
- Generate a deterministic base model from confirmed cabinet plan data.
- Store prototype artifacts against the design session.
- Show preview and generation status in Studio.

## Out of Scope

- Applying final colors, textures, handles, countertops, tile, or other visual
  style details. That belongs to E9.
- Producing BOM, Fusion 360 manufacturing layout, DXF, STEP, or cut-list output.
- Treating AI renders or sketch photos as fabrication truth.

## Ownership Decision

Studio owns the MVP blockout prototype generation because it is deterministic
and based on validated domain data. The AI Gateway may later generate styled
visuals from a prototype reference, but it does not own E8 fabrication-adjacent
geometry.

Fusion 360 remains out of E8. Fusion consumes validated exports later in the
manufacturing workflow.

## Success Criteria

- A valid cabinet plan can produce prototype artifacts.
- Prototype artifacts are tied to the design session.
- The user can see generation status and preview the output.
- The prototype can be regenerated without automatically producing BOM/Fusion
  artifacts.
