# ADR-003: Use Local ComfyUI/SDXL First, Keep Cloud GPU APIs as an Escape Hatch

## Status

Accepted

## Context

The product needs visual concept generation for kitchens and closets.

Two generation paths are expected:

- txt2img: create a concept from the conversation and structured specs.
- img2img: transform a client reference photo while preserving room structure.

The desired image pipeline includes Stable Diffusion XL and ControlNet Canny for structure preservation. The target use case benefits from local iteration, visual inspection, and control over prompt, denoise, ControlNet strength, and seed parameters.

Cloud image APIs and hosted GPU platforms can reduce local setup burden, but they add cost, vendor constraints, external latency, and dependency on network availability.

The project is also intended as a portfolio-ready demo that should be runnable locally, with a separate AI gateway service.

## Decision

Use a local ComfyUI/SDXL-oriented pipeline as the primary image generation strategy during MVP and demo development.

The FastAPI gateway will hide the concrete generation backend behind API endpoints. It may call ComfyUI locally, and it can later route to a cloud GPU provider such as RunPod if local GPU capacity is not available.

The application will model generation as jobs and artifacts rather than assuming synchronous image generation is always fast or always local.

## Consequences

Local development can tune model behavior without changing the Spring Boot monolith.

Generated images can preserve client room structure through img2img and ControlNet Canny.

The system must tolerate slow or failed generation requests.

The FastAPI gateway should expose enough metadata for traceability, such as prompt, seed, model name, pipeline type, and output file information.

The monolith should persist generated outputs through `DesignImage`, `DesignArtifact`, and `GenerationJob` rather than storing binary image data in chat messages.

Cloud GPU APIs remain possible later if local GPU setup becomes a barrier for demos or production deployments.

