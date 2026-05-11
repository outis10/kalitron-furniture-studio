# ADR-002: Separate the AI Gateway as FastAPI Instead of Implementing AI Runtime in Java

## Status

Accepted

## Context

Kalitron Furniture Studio uses an AI-assisted workflow. Clients chat with an AI designer, upload reference photos, select styles, and later request visual concepts generated from conversation context and images.

The main application is a Spring Boot 4 JHipster monolith. It is well suited for authentication, domain persistence, generated CRUD, quotes, proposals, and business workflow.

The AI runtime has different concerns:

- Python-first model ecosystem.
- LLM orchestration and prompt tooling.
- Stable Diffusion XL and ControlNet integration.
- GPU/runtime dependencies.
- Long-running image generation calls.
- Possible local GPU, RunPod, or other model-serving backends.

The documented project configuration defines a FastAPI gateway at `localhost:8000` and Spring Boot custom properties for `app.fastapi.base-url` and `app.ai-gateway.timeout-seconds`.

## Decision

Keep AI execution in a separate FastAPI gateway service.

The Spring Boot monolith will call the gateway through a dedicated service such as `FastApiGateway`, using `RestClient` and a configurable base URL.

The monolith remains responsible for:

- JWT-protected user flow.
- Session ownership and authorization.
- Persisting user messages and AI replies.
- Persisting uploaded images and generated image metadata.
- Updating `DesignSession` and `KitchenSpec`.
- Returning stable application API responses to the React frontend.

The FastAPI gateway is responsible for:

- AI chat calls.
- Prompt construction support where model-specific.
- Image generation orchestration.
- Routing between txt2img and img2img.
- SDXL/ControlNet integration.
- Returning structured responses, generated image locations, and error details.

## Consequences

The Java application avoids Python/GPU dependency complexity.

The AI gateway can evolve independently as model choices and generation pipelines change.

The HTTP contract between Spring Boot and FastAPI must be explicit and versioned enough to avoid tight coupling.

Gateway failures must be handled gracefully by Spring Boot custom resources.

Integration tests should mock the FastAPI gateway for deterministic CI.

Operationally, local demo documentation must explain that the monolith and AI gateway are separate processes.

