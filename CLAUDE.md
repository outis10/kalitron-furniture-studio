# Kalitron Furniture Studio — Claude Code Context

## Project Overview

AI-powered kitchen and closet design platform. Clients chat with an AI designer,
upload reference photos, select styles, and receive photorealistic concepts generated
by Stable Diffusion XL. Built as a JHipster monolith.

**GitHub:** https://github.com/outis10/kalitron-furniture-studio
**Issues:** https://github.com/outis10/kalitron-furniture-studio/issues

---

## Tech Stack

| Layer        | Technology                                           |
| ------------ | ---------------------------------------------------- |
| Backend      | Spring Boot 4 · Java 21 · Gradle 9.4                 |
| Frontend     | React 19 · TypeScript                                |
| Auth         | JWT stateless                                        |
| DB           | PostgreSQL 16 (prod/dev per generated `.yo-rc.json`) |
| Migrations   | Liquibase                                            |
| API docs     | OpenAPI / Swagger                                    |
| Tests        | JUnit 5 · Cypress                                    |
| Base package | com.kalitron.studio                                  |
| AI Gateway   | FastAPI Python — localhost:8000 (separate repo)      |

## https://www.jhipster.tech/2026/03/10/jhipster-release-9.0.0.html

## Project Structure

```
src/
├── main/
│   ├── java/com/kalitron/studio/
│   │   ├── domain/              ← JPA entities (never edit directly)
│   │   ├── repository/          ← Spring Data repos
│   │   ├── service/             ← Interfaces + Impl
│   │   ├── service/dto/         ← DTOs (MapStruct)
│   │   ├── service/mapper/      ← MapStruct mappers
│   │   ├── web/rest/            ← JHipster-generated CRUD resources
│   │   └── web/rest/custom/     ← Our custom endpoints (chat, design, images)
│   ├── resources/
│   │   ├── config/
│   │   │   ├── application.yml
│   │   │   ├── application-dev.yml
│   │   │   └── application-prod.yml
│   │   └── config/liquibase/
│   │       ├── master.xml       ← NEVER edit manually
│   │       └── changelog/       ← Add new changelogs here
│   └── webapp/app/
│       ├── entities/            ← JHipster CRUD (never edit)
│       ├── modules/
│       │   ├── chat/            ← Design conversation UI
│       │   ├── design/          ← Concept generation + spec display
│       │   └── quote/           ← Proposal and pricing view
│       └── shared/
│           ├── api/             ← Axios calls to backend
│           └── hooks/           ← Reusable React hooks
└── test/
    ├── java/                    ← JUnit tests
    └── javascript/cypress/      ← E2E tests
```

---

## Product Flow

The product is designed as:

```
Client conversation
  → AI-assisted design discovery
  → structured room/layout/specs
  → visual concept generation
  → cabinet modules and BOM
  → quote/proposal
  → Fusion 360 CSV/artifacts
  → parametric manufacturing layout
```

Do not treat the AI render as the source of manufacturing truth. The render is a
client-facing concept. Fabrication data must come from validated `KitchenSpec`,
room geometry, `Cabinet`, `CabinetPart`, and exported artifacts.

---

## Domain Entities

| Entity            | Purpose                                                  |
| ----------------- | -------------------------------------------------------- |
| `DesignSession`   | Core aggregate — one per client project                  |
| `ChatMessage`     | Full conversation history per session                    |
| `KitchenSpec`     | Confirmed kitchen/closet specifications                  |
| `RoomWall`        | Wall segment geometry for real room layout               |
| `RoomObstacle`    | Windows, doors, columns, outlets, water/gas points       |
| `CabinetTemplate` | Parametric module template that can produce cabinets     |
| `Cabinet`         | Individual furniture module — maps toward CSV/Fusion 360 |
| `CabinetPart`     | BOM/cut-list part belonging to a cabinet                 |
| `Material`        | Sheet/material catalog with costs and dimensions         |
| `Hardware`        | Hinges, slides, handles, and other priced hardware       |
| `Quote`           | Pricing document                                         |
| `QuoteItem`       | Individual line item in a quote                          |
| `DesignImage`     | Reference photos, renders, sketches                      |
| `DesignArtifact`  | CSV, Fusion script, PDF, STEP, DXF, BOM JSON outputs     |
| `GenerationJob`   | Async job tracking for AI render, BOM, quote, Fusion     |
| `CatalogStyle`    | Pre-loaded design inspirations (public, no auth)         |

---

## Domain Modeling Rules

- Keep uploaded/generated binary data out of `ChatMessage`; store files externally
  and reference them through `DesignImage` or `DesignArtifact`.
- Keep Fusion/CSV outputs out of `DesignSession`; model them as `DesignArtifact`
  records so a session can have multiple generated files.
- Use `RoomWall` and `RoomObstacle` for real room constraints instead of relying
  only on total width/height/depth fields.
- Use `CabinetTemplate` for reusable parametric modules and `Cabinet` for the
  placed/generated module instance.
- Use `CabinetPart` for BOM and cut-list details instead of fixed fields such as
  `panelTop`, `panelBottom`, etc.
- Use catalog entities (`Material`, `Hardware`) for pricing inputs that change
  over time. Avoid hardcoding real commercial prices in enums.
- Track long-running or failure-prone operations through `GenerationJob`.

Recommended `SessionStatus` progression:

```
DRAFT → CHATTING → SPECS_READY → VISUAL_GENERATED → LAYOUT_CONFIRMED
      → BOM_GENERATED → QUOTE_GENERATED → FUSION_GENERATED
      → COMPLETED / ARCHIVED
```

---

## Critical Rules

### NEVER touch these generated files

- `src/main/resources/config/liquibase/master.xml`, except to add a new explicit
  `<include>` for a hand-written changelog.
- Any file in `src/main/java/.../domain/` — entities are JHipster-generated
- Any file in `src/main/webapp/app/entities/` — CRUD is JHipster-generated
- Files annotated with `@Generated`

### ALWAYS follow these patterns

**Backend layer order:**

```
Custom Resource → Service Interface → ServiceImpl → Repository → Entity
                                   ↓
                              DTO ↔ Mapper (MapStruct)
```

- Never put business logic in Resource (Controller)
- Never return entities directly — always DTOs
- Always use constructor injection — never `@Autowired` on fields
- Always use `Optional<T>` in service and repository layers
- Custom endpoints go in `web/rest/custom/` — never modify JHipster-generated resources

**Frontend:**

- New components go in `modules/` not `entities/`
- API calls via axios in `shared/api/`
- Custom hooks in `shared/hooks/`

---

## Naming Conventions

| Element         | Convention                  | Example                                 |
| --------------- | --------------------------- | --------------------------------------- |
| Entity          | PascalCase                  | `DesignSession`                         |
| DTO             | `EntityDTO`                 | `DesignSessionDTO`                      |
| Mapper          | `EntityMapper`              | `DesignSessionMapper`                   |
| ServiceImpl     | `EntityServiceImpl`         | `DesignSessionServiceImpl`              |
| Custom Resource | descriptive                 | `ChatResource`, `DesignConceptResource` |
| React component | PascalCase                  | `DesignChat.tsx`                        |
| React hook      | camelCase + use             | `useImageGeneration.ts`                 |
| API endpoint    | kebab-case                  | `/api/chat/message`                     |
| DB table        | snake_case                  | `design_session`                        |
| Branch          | `feat/eN-short-description` | `feat/e2-chat-endpoint`                 |

---

## Key Configuration

### application.yml custom properties

```yaml
app:
  fastapi:
    base-url: ${FASTAPI_URL:http://localhost:8000}
  ai-gateway:
    timeout-seconds: 180
  fusion:
    scripts-dir: ${FUSION_SCRIPTS_DIR:C:/AI/FusionScripts}
  output:
    dir: ${OUTPUT_DIR:./outputs}
```

### Security — public endpoints

```java
// In SecurityConfiguration.java
.requestMatchers("/api/catalog-styles").permitAll()
.requestMatchers("/api/proposals/*/public").permitAll()
.requestMatchers("/api/chat/**").authenticated()
.requestMatchers("/api/design/**").authenticated()
```

### FastAPI Gateway service pattern

```java
@Service
public class FastApiGateway {

  private final RestClient restClient;

  public FastApiGateway(RestClient.Builder builder, @Value("${app.fastapi.base-url}") String baseUrl) {
    this.restClient = builder.baseUrl(baseUrl).build();
  }
}
```

---

## Git Workflow

```bash
# One branch per issue
git checkout -b feat/eN-short-description

# Atomic commits — one logical change per commit
git commit -m "feat(eN): short description - closes #N"

# Push and open PR
git push origin feat/eN-short-description
gh pr create --title "feat(eN): short description" --body "Closes #N"
```

---

## Spec-Driven Development

Use SDD for every feature, user story, task, or integration change before
implementation. Specs live in `docs/specs/` and are the source of truth for
scope, contracts, acceptance criteria, and verification.

### Required flow

1. Create or update the issue spec in `docs/specs/eN-feature-name/`.
2. Mark the spec status as `Draft` while requirements are still moving.
3. Confirm API contracts, data model impact, UX states, failure cases, and tests.
4. Move the spec to `Reviewed` before implementation starts.
5. Implement only the behavior covered by the spec.
6. Update the spec if implementation discovers a necessary contract or scope change.
7. In the PR, link both the GitHub issue and the spec file.

### Spec status values

```
Draft -> Reviewed -> Implementing -> Implemented
```

### SDD rules

- Do not start implementation for a new issue without a matching spec file.
- Keep specs small and testable; one issue should usually map to one spec.
- Acceptance criteria must be observable, not aspirational.
- API contracts must include request, response, auth, validation, and failure cases.
- Frontend specs must include loading, empty, error, success, and mobile states.
- Backend specs must include persistence rules, transaction boundaries, security, and tests.
- AI specs must define what is deterministic in Studio versus delegated to the AI Gateway.

### Commit message format

```
type(scope): description - closes #N

type:  feat | fix | docs | refactor | test | chore
scope: e1 | e2 | e3 | e4  (epic number)
```

---

## Common Commands

```bash
# Run full stack (backend :8080 + frontend :9000)
./gradlew

# Backend only
./gradlew bootRun

# Frontend only
npm run start

# Run backend tests
./gradlew test

# Run E2E tests
npm run e2e

# Production build
./gradlew -Pprod bootJar

# Regenerate a single entity after JDL change
jhipster entity Cabinet --regenerate

# Check Swagger
open http://localhost:8080/swagger-ui/index.html
```

---

## Liquibase — Adding a changelog

Always create new files in `src/main/resources/config/liquibase/changelog/`:

```xml
<!-- YYYYMMDD_description.xml -->
<databaseChangeLog>
    <changeSet id="20250601-1" author="kalitron">
        <addColumn tableName="design_session">
            <column name="render_url" type="varchar(500)"/>
        </addColumn>
    </changeSet>
</databaseChangeLog>
```

Then include it in `master.xml`:

```xml
<include file="config/liquibase/changelog/20250601_add_render_url.xml"
         relativeToChangelogFile="false"/>
```

### Liquibase immutability

- Treat every committed Liquibase changeset as immutable.
- Never edit a changeset that may have already run in any local, CI, staging, or
  production database.
- To fix schema or seed data, add a new incremental changeset with a new id.
- Do not use `clearCheckSums` as a normal fix; it hides migration history drift.
- Hand-written changelogs must be explicitly included in `master.xml` because this
  project does not use `includeAll`.

---

## Frequent Errors

| Error                                | Cause                       | Fix                                                      |
| ------------------------------------ | --------------------------- | -------------------------------------------------------- |
| `Could not find or load main class`  | Gradle cache corrupt        | `./gradlew clean build`                                  |
| `Liquibase: relation already exists` | Changelog out of sync       | `./gradlew liquibaseDiff`                                |
| `401 on /api/chat`                   | Missing SecurityConfig rule | Add `.requestMatchers("/api/chat/**").authenticated()`   |
| `MapStruct: no property found`       | DTO out of sync with entity | Regenerate entity or update DTO manually                 |
| `CORS error`                         | FastAPI missing origin      | Add `localhost:8080` to FastAPI `CORSMiddleware`         |
| `N+1 queries`                        | Missing `@EntityGraph`      | Add `@EntityGraph(attributePaths = {...})` in Repository |

---

## Issue Resolution Checklist

When resolving a GitHub issue, always:

- [ ] Create branch `feat/eN-issue-short-name`
- [ ] Implement following layer conventions above
- [ ] Add or update tests (JUnit for backend, Cypress for E2E)
- [ ] Run `./gradlew test` — must pass before PR
- [ ] Commit with `closes #N` in message
- [ ] Open PR referencing the issue
