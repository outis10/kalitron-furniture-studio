# Kalitron Furniture Studio — Claude Code Context

## Project Overview
AI-powered kitchen and closet design platform. Clients chat with an AI designer,
upload reference photos, select styles, and receive photorealistic concepts generated
by Stable Diffusion XL. Built as a JHipster monolith.

**GitHub:** https://github.com/outis10/kalitron-furniture-studio
**Issues:** https://github.com/outis10/kalitron-furniture-studio/issues

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 4 · Java 21 · Gradle 9.4 |
| Frontend | React 19 · TypeScript |
| Auth | JWT stateless |
| DB | PostgreSQL 16 (prod) · H2 (dev) |
| Migrations | Liquibase |
| API docs | OpenAPI / Swagger |
| Tests | JUnit 5 · Cypress |
| Base package | mx.kalitron.studio |
| AI Gateway | FastAPI Python — localhost:8000 (separate repo) |

https://www.jhipster.tech/2026/03/10/jhipster-release-9.0.0.html
---

## Project Structure

```
src/
├── main/
│   ├── java/mx/kalitron/studio/
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

## Domain Entities

| Entity | Purpose |
|--------|---------|
| `DesignSession` | Core aggregate — one per client project |
| `ChatMessage` | Full conversation history per session |
| `KitchenSpec` | Confirmed kitchen specifications |
| `Cabinet` | Individual furniture piece — maps to CSV row for Fusion 360 |
| `Quote` | Pricing document |
| `QuoteItem` | Individual line item in a quote |
| `DesignImage` | Reference photos, renders, sketches |
| `CatalogStyle` | Pre-loaded design inspirations (public, no auth) |

---

## Critical Rules

### NEVER touch these generated files
- `src/main/resources/config/liquibase/master.xml`
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

| Element | Convention | Example |
|---------|-----------|---------|
| Entity | PascalCase | `DesignSession` |
| DTO | `EntityDTO` | `DesignSessionDTO` |
| Mapper | `EntityMapper` | `DesignSessionMapper` |
| ServiceImpl | `EntityServiceImpl` | `DesignSessionServiceImpl` |
| Custom Resource | descriptive | `ChatResource`, `DesignConceptResource` |
| React component | PascalCase | `DesignChat.tsx` |
| React hook | camelCase + use | `useImageGeneration.ts` |
| API endpoint | kebab-case | `/api/chat/message` |
| DB table | snake_case | `design_session` |
| Branch | `feat/eN-short-description` | `feat/e2-chat-endpoint` |

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

    public FastApiGateway(RestClient.Builder builder,
                          @Value("${app.fastapi.base-url}") String baseUrl) {
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

---

## Frequent Errors

| Error | Cause | Fix |
|-------|-------|-----|
| `Could not find or load main class` | Gradle cache corrupt | `./gradlew clean build` |
| `Liquibase: relation already exists` | Changelog out of sync | `./gradlew liquibaseDiff` |
| `401 on /api/chat` | Missing SecurityConfig rule | Add `.requestMatchers("/api/chat/**").authenticated()` |
| `MapStruct: no property found` | DTO out of sync with entity | Regenerate entity or update DTO manually |
| `CORS error` | FastAPI missing origin | Add `localhost:8080` to FastAPI `CORSMiddleware` |
| `N+1 queries` | Missing `@EntityGraph` | Add `@EntityGraph(attributePaths = {...})` in Repository |

---

## Issue Resolution Checklist

When resolving a GitHub issue, always:
- [ ] Create branch `feat/eN-issue-short-name`
- [ ] Implement following layer conventions above
- [ ] Add or update tests (JUnit for backend, Cypress for E2E)
- [ ] Run `./gradlew test` — must pass before PR
- [ ] Commit with `closes #N` in message
- [ ] Open PR referencing the issue
