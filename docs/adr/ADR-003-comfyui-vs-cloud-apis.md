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
| **Comfy.org Cloud** | Hosted ComfyUI with 900+ pre-installed models, RTX 6000 Pro (96 GB VRAM) | API is largely compatible but polling endpoint differs (`GET /api/job/{id}/status` vs local `GET /history/{id}`); custom model uploads (SDXL 1.0 + ControlNet Canny SDXL) require Creator plan ($35/mo); free tier model availability for our specific models is unconfirmed — retained as a testing escape hatch, not primary strategy |

ComfyUI was chosen because:
- Its node workflow system gives full control over ControlNet application, denoise strength, and sampler parameters
- The REST API is clean and purpose-built for automation (`POST /prompt` → poll `/history` → `GET /view`)
- ComfyUI runs as a separate process, keeping GPU memory management decoupled from the FastAPI gateway
- Workflows are plain JSON — versionable, testable, and swappable without changing gateway code

## Decision

Use a local ComfyUI/SDXL-oriented pipeline as the primary image generation strategy during MVP and demo development.

The FastAPI gateway will hide the concrete generation backend behind API endpoints. It may call ComfyUI locally, and it can later route to a cloud GPU provider such as RunPod or Comfy.org Cloud if local GPU capacity is not available.

Comfy.org Cloud is explicitly recognized as a viable escape hatch for testing and demos. Routing to it requires adapting the polling call from `GET /history/{id}` to `GET /api/job/{id}/status` and injecting an `X-API-Key` header. To guarantee SDXL 1.0 + ControlNet Canny SDXL availability, the Creator plan ($35/mo) is required so models can be pulled from HuggingFace.

The application will model generation as jobs and artifacts rather than assuming synchronous image generation is always fast or always local.

## Consequences

Local development can tune model behavior without changing the Spring Boot monolith.

Generated images can preserve client room structure through img2img and ControlNet Canny.

The system must tolerate slow or failed generation requests.

The FastAPI gateway should expose enough metadata for traceability, such as prompt, seed, model name, pipeline type, and output file information.

The monolith should persist generated outputs through `DesignImage`, `DesignArtifact`, and `GenerationJob` rather than storing binary image data in chat messages.

Cloud GPU APIs remain possible later if local GPU setup becomes a barrier for demos or production deployments.

Comfy.org Cloud is the lowest-friction cloud escape hatch: the workflow JSON is identical and only two integration changes are needed (polling endpoint + API key header). The main gate is confirming SDXL 1.0 + ControlNet Canny SDXL are in their pre-installed library; if not, the Creator plan ($35/mo) unlocks custom model uploads from HuggingFace.

### RunPod operational learnings (validated 2026-05-14)

RunPod persistent pods with `runpod/worker-comfyui` were tested end-to-end with an RTX 3090 (24 GB VRAM). The gateway required zero code changes — the ComfyUI REST API is identical to local.

**Model pre-installation:** The `runpod/worker-comfyui` image ships with empty model directories (placeholder files only). SDXL 1.0, ControlNet Canny SDXL, and the VAE must be downloaded manually to the network volume on first run. Models persist across pod restarts once downloaded.

**Corporate network constraints:** Firewalls running FortiGuard IPS categorise `*.runpod.net` proxy URLs as "Proxy Avoidance" and block them. Workarounds in order of preference: (1) SSH tunnel (`ssh -L 8188:localhost:8188 <pod>@ssh.runpod.io`), (2) mobile hotspot, (3) request IT whitelist for `*.runpod.net`. SSL inspection by corporate proxies additionally requires setting `COMFYUI_VERIFY_SSL=false` in the gateway so httpx skips certificate verification.
