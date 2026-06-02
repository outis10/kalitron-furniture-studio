---
epic: E9
title: Styled render from confirmed prototype
status: Implementing
issues: "#74 #75 #76 #77 #78"
---

# E9 — Styled render from confirmed prototype

## Goal

Apply a selected visual style and material preferences to a confirmed blockout prototype
to produce a client-facing styled render using the AI image generation pipeline (ComfyUI img2img).

## Flow

```
Confirmed prototype (E8 PROTOTYPE_PREVIEW artifact)
  → user selects style + material preferences
  → Studio reads prototype PNG → base64
  → Studio calls AI Gateway POST /api/v1/images/generate (img2img pipeline)
  → AI Gateway: ControlNet Canny preserves cabinet structure + applies style prompt
  → Studio downloads result image → stores as STYLED_RENDER artifact
  → UI shows base prototype + styled render side by side
  → User accepts or regenerates
```

## Out of scope

- Fabrication BOM, cut list, CNC
- Fusion 360 / SketchUp export
- Per-cabinet zone-level material overrides (future epic)
