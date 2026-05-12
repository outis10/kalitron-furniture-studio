# ADR-003: Use Local ComfyUI/SDXL First, Keep Cloud GPU APIs as an Escape Hatch

## Status

Accepted

## Context

### Stable Diffusion vs ComfyUI — distinction

Stable Diffusion (SDXL 1.0) is the AI model — the mathematical brain trained on millions of images that performs the actual image synthesis.

ComfyUI is the execution engine and node-based workflow orchestrator that runs the model. It controls every step of the pipeline independently: sampler, scheduler, ControlNet application, VAE encoding/decoding, and seed management. It exposes a REST API that accepts a workflow as JSON and returns a `prompt_id`, which the caller polls until the output is ready.

The gateway interacts with ComfyUI exclusively through its REST API (`POST /prompt`, `GET /history/{id}`, `GET /view`). The Spring Boot monolith never touches ComfyUI directly.

### Generation need

The product needs visual concept generation for kitchens and closets.

Two generation paths are expected:

- txt2img: create a concept from the conversation and structured specs.
- img2img: transform a client reference photo while preserving room structure.

The desired image pipeline includes Stable Diffusion XL and ControlNet Canny for structure preservation. The target use case benefits from local iteration, visual inspection, and control over prompt, denoise, ControlNet strength, and seed parameters.

Cloud image APIs and hosted GPU platforms can reduce local setup burden, but they add cost, vendor constraints, external latency, and dependency on network availability.

The project is also intended as a portfolio-ready demo that should be runnable locally, with a separate AI gateway service.

## Alternatives Considered

| Option | Description | Rejected because |
|--------|-------------|-----------------|
| **AUTOMATIC1111** | Most popular SD web UI, has an API extension | API is less structured, harder to control individual pipeline nodes, not designed for automation |
| **InvokeAI** | User-friendly SD interface with canvas tools | Oriented toward end-users, not programmatic pipeline control |
| **Hugging Face Diffusers** | Python library for running SD directly in-process | Requires the model to load inside the gateway process — GPU memory conflict, couples inference and API layers |
| **Cloud image APIs** (Replicate, Stability AI) | Hosted inference, no local GPU required | Per-image cost, external latency, no ControlNet control, vendor lock-in, not runnable offline |

ComfyUI was chosen because:
- Its node workflow system gives full control over ControlNet application, denoise strength, and sampler parameters
- The REST API is clean and purpose-built for automation (`POST /prompt` → poll `/history` → `GET /view`)
- ComfyUI runs as a separate process, keeping GPU memory management decoupled from the FastAPI gateway
- Workflows are plain JSON — versionable, testable, and swappable without changing gateway code

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

