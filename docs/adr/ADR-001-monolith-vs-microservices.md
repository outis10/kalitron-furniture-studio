# ADR-001: Use a JHipster Monolith Instead of Microservices

## Status

Accepted

## Context

Kalitron Furniture Studio needs to support an end-to-end client flow: login, AI-guided design conversation, reference photo upload, structured kitchen/closet specifications, visual concept generation, proposal, quoting, and later Fusion 360 fabrication artifacts.

The core application is generated with JHipster 9 as a Spring Boot 4, Java 21, React 19, TypeScript, JWT, PostgreSQL, Liquibase, and Gradle application.

At this stage, the product is still validating domain workflow and demo readiness. Splitting the system into multiple Spring services would add deployment, observability, service discovery, distributed transaction, and API versioning overhead before the domain boundaries are stable.

The AI gateway is already separated as a FastAPI service because it has different runtime needs, model dependencies, and GPU-oriented execution constraints.

## Decision

Use a JHipster monolith for the main product application.

The monolith owns:

- Authentication and authorization.
- JHipster-generated CRUD resources.
- Design sessions, chat history, specs, images, cabinet modules, BOM data, quotes, and artifacts.
- PostgreSQL persistence and Liquibase migrations.
- React frontend and generated entity management screens.
- Custom Spring Boot endpoints for chat, design, image, quote, and proposal workflows.

Keep only the AI gateway as a separate service, called from the monolith through a configurable HTTP client.

## Consequences

This keeps local development, testing, and demo setup simpler.

The frontend and backend can evolve together while the product flow is still changing.

Database transactions remain local to one application and one PostgreSQL database.

Generated JHipster patterns remain useful: entity CRUD, DTOs, MapStruct mappers, repositories, services, Liquibase, and tests.

The monolith can still expose explicit custom APIs under `web/rest/custom/` while keeping generated CRUD isolated.

If the product later needs independent scaling, the strongest extraction candidates are AI gateway, image generation, artifact storage/export, and Fusion 360 processing jobs.

