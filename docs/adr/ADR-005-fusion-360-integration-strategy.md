# ADR-005: Integrate Fusion 360 Through Generated CSV and Artifacts

## Status

Accepted

## Context

Kalitron Furniture Studio is designed to move from AI-assisted design into fabrication.

The intended workflow is:

```text
Client conversation
  -> structured room/layout/specs
  -> visual concept
  -> cabinet modules and BOM
  -> quote/proposal
  -> Fusion 360 CSV/artifacts
  -> parametric manufacturing layout
```

Fusion 360 parametric models and scripts already exist outside the monolith. Those scripts read CSV files and generate layouts/models.

The application should not treat AI-generated images as fabrication truth. Renders are client-facing concept assets. Manufacturing data must come from validated structured data: room geometry, kitchen/closet specs, cabinet modules, cabinet parts, materials, hardware, and generated artifacts.

The documented application configuration includes `app.fusion.scripts-dir` and `app.output.dir`, which supports later integration with local script execution or artifact export.

## Decision

Integrate Fusion 360 through structured exports, primarily CSV files generated from validated domain data.

The Spring Boot monolith will own the canonical fabrication data:

- `KitchenSpec`
- `RoomWall`
- `RoomObstacle`
- `CabinetTemplate`
- `Cabinet`
- `CabinetPart`
- `Material`
- `Hardware`
- `Quote`
- `QuoteItem`

Generated files will be represented as `DesignArtifact` records, such as:

- CSV
- Fusion script
- Fusion model
- STEP
- DXF
- PDF
- BOM JSON

Long-running or failure-prone generation and export steps will be tracked with `GenerationJob`.

Fusion 360 automation will remain outside the JHipster monolith and consume exported files or scripts.

## Consequences

The web application can validate and review fabrication data before producing Fusion 360 input.

Existing Fusion 360 scripts can continue to evolve independently.

The CSV contract becomes an important integration boundary and should be documented and tested.

The application can support multiple generated artifacts per design session instead of storing one CSV path directly on `DesignSession`.

Future automation can run locally, on a workstation, or through a job runner without changing the client-facing design workflow.

The system must distinguish between conceptual visual approval and fabrication-ready layout approval.

