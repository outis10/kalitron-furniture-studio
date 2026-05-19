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

### Cloud GPU provider comparison (validated 2026-05-14)

#### RunPod (persistent pod)

Tested end-to-end with RTX 3090 (24 GB VRAM). The gateway required zero code changes — the ComfyUI REST API on RunPod is identical to local.

**Model pre-installation:** `runpod/worker-comfyui` ships with empty model directories. SDXL 1.0, ControlNet Canny SDXL, and the VAE must be downloaded manually to the network volume on first run via `wget` from HuggingFace. Models persist across pod restarts.

**Corporate network constraints:** FortiGuard IPS categorises `*.runpod.net` proxy URLs as "Proxy Avoidance" and blocks them. Workarounds: (1) SSH tunnel (`ssh -L 8188:localhost:8188 <pod>@ssh.runpod.io`), (2) mobile hotspot, (3) IT whitelist for `*.runpod.net`. SSL inspection additionally requires `COMFYUI_VERIFY_SSL=false`.

#### Vast.ai (persistent instance)

Tested end-to-end with RTX 3090 (24 GB VRAM). Vast.ai provides a pre-built ComfyUI template with a panel-based launcher — no Docker setup needed. Model directories are empty on first run and require the same manual `wget` downloads.

**Authentication:** Vast.ai wraps ComfyUI behind a reverse proxy that requires a token on every request. The token is provided as `?token=<value>` and is visible in the "Advanced Connection Options" panel. The gateway handles this via `COMFYUI_TOKEN` setting, passed as a query parameter with `follow_redirects=True`. Both the direct IP (`http://<ip>:<port>`) and Cloudflare Quick Tunnel (`https://<name>.trycloudflare.com`) endpoints require the token.

**Cost vs RunPod:** Vast.ai tends to be cheaper per GPU-hour but instances can be reclaimed by the host. RunPod persistent pods offer more stability for longer sessions.

**Custom nodes:** The `CannyEdgePreprocessor` node required for img2img is not pre-installed. Install it via:
```bash
cd /workspace/ComfyUI/custom_nodes
git clone https://github.com/Fannovel16/comfyui_controlnet_aux
cd comfyui_controlnet_aux && pip install -r requirements.txt
```

### img2img pipeline tuning (validated 2026-05-18)

The initial img2img workflow used `VAEEncode` to encode the client reference photo into the latent space, then ran KSampler with `denoise=0.75`. This caused the original cabinet color (e.g. brown wood) to bleed into the output even at `denoise=0.88` because the VAE latent carries strong color signal.

**Decision:** Replace `VAEEncode` with `EmptyLatentImage` in the img2img workflow. ControlNet Canny still extracts structural edge lines from the reference photo, enforcing layout and cabinet positions. The KSampler starts from pure noise (`denoise=1.0`), so color and style come entirely from the positive prompt. This makes img2img behave as a ControlNet-guided txt2img — full style transformation while preserving room structure.

Final img2img parameters: `ControlNet strength=0.70`, `steps=35`, `cfg=7.5`, `denoise=1.0`.

### Prompt engineering learnings (validated 2026-05-18)

**Composite style names:** The Studio sends style names like `"Minimalista Negro"` where the color is embedded in the style label. STYLE_MAP only had exact keys (`"minimalista"`), so `"Minimalista Negro"` fell through to the `"modern"` default. Fix: substring match — check if any STYLE_MAP key appears within the style string.

**Color must be explicit and early:** SDXL assigns more weight to earlier prompt tokens. The minimalist template included `"neutral palette"` which competed against the intended black color and won, producing cream/beige cabinets. Fix: (1) remove `"neutral palette"` from the minimalist template, (2) extract the color modifier from the composite style name and inject it as the first extra token — e.g. `"Minimalista Negro"` → `"matte black cabinets, all-black kitchen furniture, dark black cabinet color"` prepended before layout and finish details.

**Layout and finish arrive as null:** The Studio often sends `layout=null` and `finish=null` even when the design brief contains the confirmed specs. The gateway now extracts layout and finish by scanning the brief for `Distribución:` and `Acabado/Color:` lines as a fallback. `_translate_design_brief` was also made prescriptive about layout keywords to prevent SDXL from ignoring the linear constraint and generating L-shaped or island kitchens.
