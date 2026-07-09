<!--
Sync Impact Report
- Version change: (uninitialized template) → 1.0.0
- Rationale: First formal ratification. Establishes all architectural principles
  derived from reverse-engineering of the existing PMS codebase plus three
  mandatory spec-governance principles (SPEC-TYPE-01, DATA-REUSE-01,
  AMBIGUITY-01). A clean-slate founding adoption constitutes a MAJOR (1.0.0).
- Added principles (all new):
  - I. Layered Architecture With One-Way Dependency
  - II. Module-Per-Feature Vertical Slicing
  - III. Service-Layer Transaction Boundary
  - IV. External System Isolation via Dedicated Datasources
  - V. BPM-Driven Business Processes
  - VI. Spec Technology-Agnosticism (SPEC-TYPE-01) [MANDATORY USER]
  - VII. Database Schema As Contract (DATA-REUSE-01) [MANDATORY USER]
  - VIII. Ambiguity Resolution Discipline (AMBIGUITY-01) [MANDATORY USER]
- Added sections:
  - "Technical Constraints & Cross-Cutting Standards" (Section 2)
  - "Development Workflow & Quality Gates" (Section 3)
- Removed sections: none
- Templates requiring updates:
  - .specify/templates/plan-template.md            ✅ compatible (Constitution Check gate already generic)
  - .specify/templates/spec-template.md            ✅ compatible (Success Criteria already tech-agnostic)
  - .specify/templates/tasks-template.md           ✅ compatible (phase model unchanged)
  - .specify/templates/checklist-template.md       ✅ compatible
- Follow-up TODOs: none. All placeholders resolved.
-->

# PMS Constitution

## Core Principles

### I. Layered Architecture With One-Way Dependency

The system MUST follow a four-tier layered architecture: Presentation (Action)
→ Service → Data Access (DAO) → Persistence Resources (SQL mappings).
Dependencies MUST flow strictly downward. Upper layers MAY depend on lower
layers; lower layers MUST NOT depend on upper layers.

- Presentation layer MUST NOT contain business logic; it assembles parameters
  and forwards views only.
- Service layer MUST NOT reach into the web container (no `ServletAction`,
  `ServletContext`, `HttpServletRequest` usage). Any such access is a layering
  violation and MUST be pushed back to the presentation layer or an injected
  port.
- DAO layer MUST NOT orchestrate business rules; it exposes data operations
  only.
- Cross-layer shortcuts (e.g., Service calling `ServletActionContext`) are
  forbidden in new code and flagged as refactor targets in existing code.

**Rationale**: The reverse-engineered codebase shows repeated layering leaks
(Service invoking `ServletActionContext.getServletContext()`), which couples
business logic to the web container and blocks testability and reuse.

### II. Module-Per-Feature Vertical Slicing

Functionality MUST be organized into vertical feature modules under
`com.dp.plat.{module}`. Each module owns its own `action`, `service`, `dao`,
`bean`, `vo`, `param`, `util`, and `exception` subpackages. Cross-module
reuse goes through the module's published Service interface, not its internal
DAOs or beans.

- Naming suffixes are mandatory: `*Action`, `*Service` / `*ServiceImpl`,
  `*Dao` / `*DaoImpl`, `*VO` (view objects), `*Param` (query parameters),
  `*Bean` / entity (persistence).
- Build modules (`pms-struts`, `pms-activiti`, `pms-ext-d365`, `pms-security`,
  `pms-rules`, `pms-ext-fp`, `core`) map to deployment units; feature modules
  map to source packages. Both axes are valid; do not conflate them.
- A feature module MUST NOT import another feature module's `*DaoImpl` or
  `*ServiceImpl` internals directly.

**Rationale**: The existing codebase already follows this convention
consistently; codifying it preserves navigability and prevents entanglement.

### III. Service-Layer Transaction Boundary

Transactions MUST be declared at the Service layer. Exactly ONE transaction
strategy MAY be active for a given module. Mixing `@Transactional`,
`TransactionProxyFactoryBean` prefix-matching, and manual
`startTransaction/commit/rollback` within the same code path is forbidden.

- New code MUST use declarative `@Transactional` on Service methods.
- Manual transaction control inside DAOs is forbidden in new code; existing
  manual-transaction DAOs are flagged as refactor targets.
- Read-only operations SHOULD declare `@Transactional(readOnly = true)`.

**Rationale**: The codebase currently runs three competing transaction
mechanisms, creating ambiguous boundaries and transaction-leak risk.

### IV. External System Isolation via Dedicated Datasources

Every external system integration (SAP, D365, CRM, OA, EHR, ITR, SMS, SSE,
License, etc.) MUST be isolated behind a dedicated datasource and a dedicated
SQL-map configuration. Integrations MUST be consumable through a Service
interface so that downstream code is unaware of the source system.

- All external datasources MUST use a pooled connection provider. Use of
  `DriverManagerDataSource` (no pooling) is forbidden in new integrations.
- Cross-system data joins MUST be performed in the Service layer, never via
  cross-database SQL.
- Synchronization with external systems MUST run through the scheduled-task
  (`job`) package, not inline in request-handling actions.

**Rationale**: Existing external datasources use non-pooled
`DriverManagerDataSource`, a performance and resource-exhaustion risk under
load.

### V. BPM-Driven Business Processes

Long-running, multi-step, multi-actor business flows (presales, callback,
subcontract, PM closed-loop, etc.) MUST be modeled and executed as BPMN
processes via the Activiti engine, not as ad-hoc status fields and inline
state machines.

- A status transition that requires human approval, assignment, or async
  notification MUST be a BPMN task, not a numeric status string.
- Inline `new Thread()` for async work is forbidden; async work MUST go
  through the engine, the scheduler, or a managed executor.

**Rationale**: The codebase mixes BPMN-driven flows with hand-rolled status
strings and raw `new Thread()` calls, producing inconsistent and unobservable
process behavior.

### VI. Spec Technology-Agnosticism (SPEC-TYPE-01)

Feature specifications (spec.md) MUST be technology-agnostic. A spec MUST NOT
bind a requirement to a concrete framework annotation, class name, or library
identifier (e.g., "use `@Transactional`", "extend `BaseAction`", "call
`SqlMapClientTemplate`"). Specs describe WHAT the system must do and the
qualities it must have, not HOW a specific framework implements it.

- Technology choices are recorded in plan.md (Technical Context), not spec.md.
- Acceptance criteria and success criteria MUST be measurable and
  framework-neutral.
- If a spec must reference an existing capability, it MUST name the capability
  by behavior (e.g., "the persistence layer"), not by class.

**Rationale**: The current system is tightly coupled to Struts2 / iBatis /
Hibernate. Binding specs to those names would freeze the debt and block any
future migration. Specs must remain portable.

### VII. Database Schema As Contract (DATA-REUSE-01)

The existing database table structure is treated as a contract. A new system
or feature MUST reuse existing tables by default. Any change to table
structure (rename, drop, type change, constraint change) or any decision to
NOT reuse an existing table MUST be captured as an explicit spec item with
rationale.

- New features MUST prefer mapping to existing tables over creating new ones.
- Schema-affecting decisions MUST be recorded in the spec's "Key Entities"
  section and revisited during `/clarify`.
- Breaking schema changes require a dedicated migration task in tasks.md.

**Rationale**: Multiple production integrations and reports depend on the
current schema. Treating it as a contract prevents silent breakage and forces
deliberate migration planning.

### VIII. Ambiguity Resolution Discipline (AMBIGUITY-01)

Every ambiguity discovered during specification, planning, or implementation
MUST be recorded during the `/clarify` phase with (a) the question, (b) the
decision taken, and (c) the rationale/evidence supporting the decision.
Unresolved ambiguities MUST NOT be silently resolved in code.

- Ambiguities discovered after `/clarify` MUST trigger a supplementary
  clarification record before implementation proceeds.
- "NEEDS CLARIFICATION" markers in specs MUST be resolved or explicitly
  deferred with a stated owner and due trigger.
- No speculative features: if a requirement is ambiguous, ask; do not assume.

**Rationale**: The reverse-engineering pass surfaced several "待澄清" items
(logging conventions, deployment topology, sharding). Leaving such gaps
unrecorded causes divergent implementations across modules.

## Technical Constraints & Cross-Cutting Standards

### Logging

- Logging facade MUST be SLF4J. Implementation MUST be Log4j2. Direct use of
  `System.out`, `e.printStackTrace()`, or concrete logging implementations in
  business code is forbidden.
- Service and DAO layers MUST log at meaningful boundaries (entry/exit of
  significant operations, caught exceptions). The current sparse logging
  pattern is a known debt to be retired.
- SQL logging in development uses p6spy; production MUST NOT log full SQL at
  INFO level.

### Security

- The three coexisting security frameworks (Spring Security, CAS, Shiro) are
  legacy. New authentication/authorization work MUST target a single chosen
  framework per module and MUST NOT introduce hard-coded credentials
  (e.g., the `admin/admin` test account in `applicationContext-security.xml`
  is forbidden in any non-test artifact).
- User identity MUST be accessed through the `UserContext` abstraction, not
  by reading the HTTP session directly from Service/DAO code.

### Caching

- MyBatis second-level cache is enabled for new mapper modules. Hibernate
  second-level cache (ehcache) is confined to the `pms-activiti` module.
- Redis-backed caching, if reintroduced, MUST be declared at the Service
  layer (not inline in DAOs) and recorded in plan.md.

### Exception Handling

- New code MUST NOT throw raw `RuntimeException` or `Exception`. Business
  errors MUST use a typed exception rooted at a single business-exception
  base (replacing the current ad-hoc `CustomRuntimeException` and misspelled
  module exceptions like `NoMatchedSoftVersionStrategyExecption`).
- The presentation layer is responsible for translating exceptions to user
  messages; the Service layer MUST NOT assemble HTML error fragments.

### Data Access

- iBatis 2.x (`SqlMapClientTemplate`) is legacy; new features MUST use MyBatis
  3.x mapper interfaces. Existing iBatis DAOs are refactor targets, not a
  pattern to extend.
- Temporary-table statistics patterns (create/drop temp tables per query)
  are permitted only when no set-based alternative exists, and MUST be
  documented in plan.md.

### Build & Reproducibility

- `system`-scope dependencies on local JARs (e.g., `Utils-v0.1.jar`) are
  forbidden in new modules. Such dependencies MUST be published to a
  repository or vendored as a proper module.
- Checkstyle is configured but non-blocking (`failOnViolation=false`). New
  modules MAY raise this to blocking once the existing violations are
  retired.

### Known Technical Debt (Refactor Backlog)

The following are recognized debts and explicit refactor targets; they are
NOT to be extended:

- Dual ORM (iBatis 2.x + MyBatis 3.x) coexistence.
- Three coexisting transaction mechanisms.
- Three coexisting security frameworks with hard-coded credentials.
- Struts2 version drift (root 2.5.30 vs module 2.3.35).
- Non-pooled external datasources.
- fastjson 1.2.x (autotype RCE history); fastjson2 migration is pending.
- Service-layer HTML/SQL string assembly.
- Raw `new Thread()` without pool or exception handling.
- Build reproducibility via local `system`-scope JARs.
- Accumulated `_bak` / `.bak` / misspelled legacy files.

## Development Workflow & Quality Gates

### Spec-Driven Flow

All non-trivial work MUST follow the Spec Kit flow: `/speckit-specify` →
`/speckit-clarify` → `/speckit-plan` → `/speckit-tasks` →
`/speckit-implement`. Skipping `/clarify` is forbidden when any ambiguity
exists (per Principle VIII).

### Constitution Check Gate

Every plan.md MUST include a "Constitution Check" gate that verifies
alignment with each applicable principle before Phase 0 research and again
after Phase 1 design. Violations require either a fix or a justified entry
in the plan's Complexity Tracking table.

### Layering & Boundary Review

Code review MUST verify: (a) one-way dependency direction (Principle I),
(b) no web-container access from Service/DAO, (c) single transaction
strategy per module, (d) pooled external datasources, (e) no raw
`new Thread()`.

### Schema Change Review

Any task touching a table MUST reference the spec item that authorized the
schema change (per Principle VII). Unreferenced schema changes are blocked
at review.

### Tech-Agnostic Spec Review

Spec review MUST reject any spec that names a concrete framework annotation,
class, or library as a binding requirement (per Principle VI).

## Governance

- This Constitution supersedes all other practices for the PMS project. Where
  a practice conflicts with a Principle, the Principle prevails.
- Amendments require: (a) a written proposal, (b) recorded rationale, (c)
  approval, and (d) a migration plan for any code already non-compliant.
- Versioning follows semantic versioning: MAJOR for principle removal or
  incompatible redefinition, MINOR for new/expanded principle or section,
  PATCH for clarification and typo fixes.
- All plans, specs, and reviews MUST verify compliance with this
  Constitution. Complexity beyond the principles MUST be justified in the
  plan's Complexity Tracking table.
- Runtime development guidance lives in the Spec Kit templates under
  `.specify/templates/`; this Constitution is the normative source.

**Version**: 1.0.0 | **Ratified**: 2026-07-09 | **Last Amended**: 2026-07-09
