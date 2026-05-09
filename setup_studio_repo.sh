#!/usr/bin/env bash
# ============================================================
#  kalitron-furniture-studio — GitHub Setup (WSL / bash)
#  FIX: removed set -u to handle optional gh label args
# ============================================================

set -eo pipefail

REPO="kalitron-furniture-studio"
GITHUB_USER=""

if [[ -z "$GITHUB_USER" ]]; then
  GITHUB_USER=$(gh api user --jq '.login')
  echo "Usuario detectado: $GITHUB_USER"
fi

FULL_REPO="$GITHUB_USER/$REPO"

echo ""
echo "========================================"
echo " Verificando autenticación..."
echo "========================================"
gh auth status

echo ""
echo "========================================"
echo " [1/3] Creando Labels..."
echo "========================================"

# Fix: pasar description como argumento con comillas explícitas
gh label create "epic"             --color "7C3AED" --description "High-level feature group"              --repo "$FULL_REPO" --force
gh label create "user-story"       --color "0D9488" --description "End-user facing feature"               --repo "$FULL_REPO" --force
gh label create "task"             --color "D97706" --description "Technical implementation task"         --repo "$FULL_REPO" --force
gh label create "bug"              --color "DC2626" --description "Something is broken"                   --repo "$FULL_REPO" --force
gh label create "infrastructure"   --color "2563EB" --description "DevOps CI/CD deployment"               --repo "$FULL_REPO" --force
gh label create "ai-ml"            --color "059669" --description "AI ML LLM related work"                --repo "$FULL_REPO" --force
gh label create "frontend"         --color "DB2777" --description "React UI UX"                           --repo "$FULL_REPO" --force
gh label create "backend"          --color "9333EA" --description "Spring Boot JHipster API"              --repo "$FULL_REPO" --force
gh label create "documentation"    --color "6B7280" --description "README ADR wiki"                       --repo "$FULL_REPO" --force
gh label create "good first issue" --color "84CC16" --description "Good entry point for contributors"     --repo "$FULL_REPO" --force
gh label create "priority:high"    --color "EF4444" --description "Must be done this sprint"              --repo "$FULL_REPO" --force
gh label create "priority:medium"  --color "F59E0B" --description "Important but not blocking"            --repo "$FULL_REPO" --force
gh label create "priority:low"     --color "6EE7B7" --description "Nice to have"                         --repo "$FULL_REPO" --force

echo "✓ Labels creados"

echo ""
echo "========================================"
echo " [2/3] Creando Milestones..."
echo "========================================"

gh api "repos/$FULL_REPO/milestones" -f title="v0.1 — Project Setup & Infrastructure"  -f description="JHipster generation, database, CI/CD, dev environment" -f due_on="2025-06-15T00:00:00Z" || true
gh api "repos/$FULL_REPO/milestones" -f title="v0.2 — Design Chat & Conversation"       -f description="Multi-turn AI chat, session management, image upload"   -f due_on="2025-07-01T00:00:00Z" || true
gh api "repos/$FULL_REPO/milestones" -f title="v0.3 — Visual Concept Generation"        -f description="AI image generation integrated into chat, R2 storage"   -f due_on="2025-07-20T00:00:00Z" || true
gh api "repos/$FULL_REPO/milestones" -f title="v0.4 — End-to-End Demo"                  -f description="Complete client demo flow. Portfolio-ready."             -f due_on="2025-08-10T00:00:00Z" || true

echo "✓ Milestones creados"

echo ""
echo "========================================"
echo " [3/3] Creando Issues..."
echo "========================================"

issue() {
  # usage: issue "title" "labels" "milestone" "body"
  local url
  url=$(gh issue create \
    --repo "$FULL_REPO" \
    --title "$1" \
    --label "$2" \
    --milestone "$3" \
    --body "$4")
  echo "  ✓ $(basename "$url") — $1"
}

# ── EPIC 1: Infrastructure ─────────────────────────────────

issue \
"[E1] epic: Project foundation — JHipster + PostgreSQL + JWT" \
"epic,infrastructure,backend" \
"v0.1 — Project Setup & Infrastructure" \
"## Epic: Project Foundation

### Overview
Set up the complete JHipster monolith with all domain entities, authentication, and development environment.

### Tech Stack
- JHipster 8 · Spring Boot 3 · Java 21
- React 18 · TypeScript
- PostgreSQL 16 · Liquibase · JWT · Gradle

### Definition of Done
- [ ] Project runs locally with \`./gradlew\`
- [ ] All 8 entities visible in Swagger UI
- [ ] GitHub Actions CI pipeline passes
- [ ] README with architecture diagram published"

issue \
"[E1] task: Generate JHipster project from kitchen.jdl domain model" \
"task,infrastructure,backend,priority:high" \
"v0.1 — Project Setup & Infrastructure" \
"## Task: Generate JHipster Project

### Domain Entities
| Entity | Purpose |
|--------|---------|
| \`DesignSession\` | Core aggregate — one per client project |
| \`ChatMessage\` | Full conversation history |
| \`KitchenSpec\` | Confirmed kitchen specifications |
| \`Cabinet\` | Individual furniture piece (CSV row) |
| \`Quote\` + \`QuoteItem\` | Pricing and BOM |
| \`DesignImage\` | Reference photos, renders, sketches |
| \`CatalogStyle\` | Pre-loaded design inspirations |

### Acceptance Criteria
- [ ] \`jhipster jdl kitchen.jdl\` runs without errors
- [ ] All 8 entities have CRUD endpoints visible in Swagger
- [ ] React frontend loads at \`localhost:9000\`
- [ ] H2 in-memory DB works for local dev
- [ ] All Liquibase changelogs generated cleanly

### Commands
\`\`\`bash
npm install -g generator-jhipster
mkdir kalitron-furniture-studio && cd kalitron-furniture-studio
jhipster jdl kitchen.jdl
./gradlew
\`\`\`

### Story Points: 3"

issue \
"[E1] task: Configure PostgreSQL for production and H2 for development" \
"task,infrastructure,backend,priority:high" \
"v0.1 — Project Setup & Infrastructure" \
"## Task: Database Configuration

### Acceptance Criteria
- [ ] \`application-dev.yml\` uses H2, app starts with \`./gradlew\`
- [ ] \`application-prod.yml\` uses PostgreSQL via environment variables
- [ ] Liquibase migrations run cleanly on both databases
- [ ] HikariCP connection pool: min=2, max=10
- [ ] Credentials in \`.env\`, never committed to git

### Environment Variables
\`\`\`
DATABASE_URL=jdbc:postgresql://localhost:5432/kalitron
DATABASE_USERNAME=kalitron_user
DATABASE_PASSWORD=<secret>
\`\`\`

### Story Points: 2"

issue \
"[E1] task: Set up GitHub Actions CI/CD pipeline" \
"task,infrastructure,priority:medium" \
"v0.1 — Project Setup & Infrastructure" \
"## Task: CI/CD Pipeline

### Acceptance Criteria
- [ ] Workflow file at \`.github/workflows/ci.yml\`
- [ ] Triggers on push to \`main\`, \`develop\` and all PRs
- [ ] Runs \`./gradlew test\` (JUnit backend tests)
- [ ] Runs \`npm test\` (React frontend tests)
- [ ] Build badge visible in README
- [ ] Pipeline completes in under 5 minutes

### Story Points: 2"

issue \
"[E1] doc: Write Architecture Decision Records (ADRs)" \
"documentation,priority:medium" \
"v0.1 — Project Setup & Infrastructure" \
"## Documentation: ADRs

### ADRs to write
- [ ] ADR-001: Monolith vs Microservices
- [ ] ADR-002: AI Gateway separation (FastAPI vs Java)
- [ ] ADR-003: ComfyUI vs cloud APIs (local GPU vs RunPod)
- [ ] ADR-004: Cloudflare R2 vs S3
- [ ] ADR-005: Fusion 360 integration strategy

### Format: Michael Nygard — Status · Context · Decision · Consequences
### Location: \`docs/adr/\`

### Story Points: 3"

issue \
"[E1] task: Create portfolio README with architecture diagram" \
"documentation,priority:high" \
"v0.1 — Project Setup & Infrastructure" \
"## Task: Portfolio README

### Sections
- [ ] Hero banner with tech stack badges
- [ ] Demo screenshot of chat + concept generation flow
- [ ] System architecture diagram
- [ ] Tech stack table with versions
- [ ] Feature list (img2img, txt2img, ControlNet, JWT, etc.)
- [ ] Getting started in under 10 commands
- [ ] Roadmap linking to GitHub milestones
- [ ] MIT License

### Story Points: 3"

# ── EPIC 2: Design Chat ────────────────────────────────────

issue \
"[E2] epic: AI-powered design conversation flow" \
"epic,ai-ml,backend,frontend" \
"v0.2 — Design Chat & Conversation" \
"## Epic: AI Design Chat

### Overview
Multi-turn AI conversation that guides clients through kitchen design discovery. Asks targeted questions, processes uploaded photos, and builds a structured spec from natural dialogue.

### Definition of Done
- [ ] Client completes design discovery in under 5 minutes
- [ ] AI correctly extracts: layout, dimensions, style, finish, cabinet types
- [ ] Session persists across page refreshes
- [ ] Works with and without uploaded photo"

issue \
"[E2] user-story: As a client, I can start a design session and chat with the AI designer" \
"user-story,frontend,backend,ai-ml,priority:high" \
"v0.2 — Design Chat & Conversation" \
"## User Story

**As a** prospective kitchen client,
**I want to** start a conversation with an AI designer,
**So that** I can describe what I want and get professional guidance without needing technical knowledge.

### Acceptance Criteria
- [ ] Client fills in name and email to start session
- [ ] AI greets and asks about project type (kitchen / closet / both)
- [ ] Messages appear without page reload
- [ ] AI asks maximum 2 questions per message
- [ ] Session saved to DB with unique code (e.g. KD-2025-001)
- [ ] Client can resume session after closing browser
- [ ] Loading indicator shown while AI is thinking
- [ ] Mobile responsive (375px minimum)

### API Contract
\`\`\`
POST /api/chat/message
Body:     { sessionId, message, imageBase64? }
Response: { sessionId, reply, specsReady, specsSummary? }
\`\`\`

### Story Points: 5"

issue \
"[E2] user-story: As a client, I can upload a reference photo of my current kitchen" \
"user-story,frontend,ai-ml,priority:high" \
"v0.2 — Design Chat & Conversation" \
"## User Story

**As a** client with an existing kitchen,
**I want to** upload a photo of my current space,
**So that** the AI can understand my layout and generate concepts that respect my structure.

### Acceptance Criteria
- [ ] Upload button in chat input area
- [ ] Accepts JPG, PNG, WebP — max 5MB
- [ ] Drag and drop supported
- [ ] Image preview shown inline in chat
- [ ] Image sent as base64 to AI Gateway
- [ ] AI acknowledges the image and comments on the space
- [ ] Error shown for invalid file type or size
- [ ] Saved to \`DesignImage\` entity with type \`REFERENCE\`

### Story Points: 3"

issue \
"[E2] user-story: As a client, I can browse catalog styles to inspire my design" \
"user-story,frontend,priority:medium" \
"v0.2 — Design Chat & Conversation" \
"## User Story

**As a** client who does not know design terminology,
**I want to** browse visual style cards and pick one that resonates with me,
**So that** I can communicate preferences without needing to describe them in words.

### Acceptance Criteria
- [ ] Style cards shown at session start (before first message)
- [ ] Each card: thumbnail, style name, price range badge, description
- [ ] Client can select one or skip
- [ ] Selected style sent as context to AI in every message
- [ ] Loads from public endpoint (no auth required)
- [ ] 6+ pre-loaded styles: Moderno, Minimalista, Rustico, Clasico, Industrial, Escandinavo

### API: \`GET /api/catalog-styles\` (public, no JWT)

### Story Points: 3"

issue \
"[E2] task: Implement ChatResource endpoint proxying to AI Gateway" \
"task,backend,ai-ml,priority:high" \
"v0.2 — Design Chat & Conversation" \
"## Task: Chat Proxy Endpoint

### Files to create
- \`ChatResource.java\` — custom controller (separate from JHipster-generated)
- \`FastApiGateway.java\` — RestClient service
- \`ChatRequestDTO.java\` / \`ChatResponseDTO.java\`

### Acceptance Criteria
- [ ] \`POST /api/chat/message\` requires JWT authentication
- [ ] Proxies to FastAPI \`POST /api/v1/chat/message\` via \`RestClient\`
- [ ] Saves user message and AI reply to \`ChatMessage\` entity
- [ ] Updates \`DesignSession.status\` when \`specsReady=true\`
- [ ] Gateway base URL configurable via \`app.fastapi.base-url\`
- [ ] Handles FastAPI unavailable with graceful error response

### Story Points: 5"

issue \
"[E2] task: Seed CatalogStyle table with 8 pre-loaded design styles" \
"task,backend,good first issue,priority:medium" \
"v0.2 — Design Chat & Conversation" \
"## Task: Seed Catalog Styles

### Styles
| Name | Style | Price Range |
|------|-------|-------------|
| Moderno Blanco | moderno | medio |
| Moderno Gris | moderno | medio |
| Minimalista Negro | minimalista | premium |
| Madera Natural | rustico | medio |
| Rustico Pino | rustico | economico |
| Clasico Crema | clasico | premium |
| Industrial | industrial | medio |
| Escandinavo | minimalista | medio |

### Acceptance Criteria
- [ ] Liquibase changelog: \`changelog/20250601_seed_catalog_styles.xml\`
- [ ] All 8 styles inserted with \`is_active=true\`
- [ ] \`GET /api/catalog-styles\` returns all 8 without auth
- [ ] Thumbnail placeholder paths set

### Story Points: 2"

# ── EPIC 3: Visual Concept Generation ─────────────────────

issue \
"[E3] epic: AI visual concept generation — img2img and txt2img" \
"epic,ai-ml,infrastructure" \
"v0.3 — Visual Concept Generation" \
"## Epic: Visual Concept Generation

### Two Pipelines
| Pipeline | Input | Model | Use case |
|----------|-------|-------|---------|
| img2img | Client photo + prompt | SDXL + ControlNet Canny | Preserve room structure, apply new style |
| txt2img | Prompt only | SDXL base | Generate kitchen from scratch |

### Definition of Done
- [ ] img2img generates in under 60s on RTX 3080
- [ ] txt2img generates in under 45s
- [ ] Image appears in chat without page reload
- [ ] URL persisted in DB and stored in R2"

issue \
"[E3] user-story: As a client, I can generate a visual concept from our conversation" \
"user-story,frontend,ai-ml,priority:high" \
"v0.3 — Visual Concept Generation" \
"## User Story

**As a** client who described their dream kitchen,
**I want to** generate a photorealistic visual concept,
**So that** I can see what my kitchen could look like before committing.

### Acceptance Criteria
- [ ] Generate button appears when \`specsReady=true\`
- [ ] Style, layout and finish selectors shown before generating
- [ ] Loading state with estimated time (~45-60s)
- [ ] Generated image displayed full-width in chat
- [ ] Badge: 'Based on your photo' or 'Generated from description'
- [ ] Regenerate button available after first generation
- [ ] Image saved to \`DesignImage\` with type \`RENDER\`

### Story Points: 5"

issue \
"[E3] user-story: As a client with a photo, my kitchen structure is preserved in the concept" \
"user-story,ai-ml,priority:high" \
"v0.3 — Visual Concept Generation" \
"## User Story

**As a** client who uploaded a photo of their existing kitchen,
**I want** the generated concept to preserve my space structure,
**So that** the result feels realistic and achievable in my actual space.

### Acceptance Criteria
- [ ] When \`clientImageB64\` present, AI Gateway routes to img2img pipeline
- [ ] ControlNet Canny extracts structural lines from client photo
- [ ] ControlNet strength: 0.75 (balances structure vs style)
- [ ] Generated image visibly reflects the room structure
- [ ] Without photo, routes to txt2img automatically
- [ ] Badge shown: 'Based on your photo'

### Technical Notes
- ControlNet Canny SDXL: 1024x1024
- \`denoise=0.75\` preserves structure while allowing style transformation

### Story Points: 3"

issue \
"[E3] user-story: As a returning client, I can resume my previous design session" \
"user-story,frontend,backend,priority:medium" \
"v0.3 — Visual Concept Generation" \
"## User Story

**As a** client who started a design session previously,
**I want to** find and continue my session,
**So that** I do not have to start the conversation from scratch.

### Acceptance Criteria
- [ ] My Sessions page lists all sessions for logged-in user
- [ ] Each card: session code, date, project type, status badge
- [ ] Clicking a session reopens chat with full history
- [ ] Generated concepts visible in session history
- [ ] Sessions sorted by most recently updated
- [ ] Empty state with CTA for new session

### Story Points: 3"

# ── EPIC 4: End-to-End Demo ────────────────────────────────

issue \
"[E4] epic: End-to-end client demo and portfolio readiness" \
"epic,frontend,backend" \
"v0.4 — End-to-End Demo" \
"## Epic: End-to-End Demo

### Definition of Done
- [ ] Full flow works: login → chat → concept → proposal
- [ ] Both repos have comprehensive READMEs
- [ ] CI pipeline green
- [ ] Demo runnable locally in under 10 minutes from README"

issue \
"[E4] user-story: As a designer, I can present the client a complete design proposal" \
"user-story,frontend,backend,priority:high" \
"v0.4 — End-to-End Demo" \
"## User Story

**As a** furniture designer,
**I want to** show the client a polished proposal page,
**So that** they can see their generated concept alongside specs and next steps.

### Acceptance Criteria
- [ ] Proposal page: full-size render, kitchen specs summary, cabinet count
- [ ] Download PDF button generates a shareable document
- [ ] Shareable link works without login
- [ ] WhatsApp share button for mobile clients
- [ ] Print-friendly CSS applied

### Story Points: 8"

issue \
"[E4] task: End-to-end Cypress test for the complete design flow" \
"task,infrastructure,priority:medium" \
"v0.4 — End-to-End Demo" \
"## Task: E2E Integration Test

### Happy path
\`\`\`
1. Login with test credentials
2. Click New Kitchen Design
3. Select Moderno Blanco from catalog
4. Type: I want an L-shaped kitchen in matte white
5. AI responds and asks follow-up questions
6. User confirms design
7. Select layout L_SHAPE, finish MATTE_WHITE
8. Click Generate Visual Concept
9. [Mocked] AI Gateway returns test image
10. Concept appears in chat
11. Click View Proposal
12. Proposal page loads with image and specs
\`\`\`

### Acceptance Criteria
- [ ] Test runs in CI with AI Gateway mocked
- [ ] Completes in under 2 minutes
- [ ] Screenshots saved on failure
- [ ] Test data cleaned up after run

### Story Points: 5"

echo ""
echo "========================================"
echo " ✅ LISTO — $FULL_REPO"
echo "========================================"
echo ""
echo " Verifica en:"
echo " https://github.com/$FULL_REPO/issues"
echo ""
