# RoofSense — Agent Guide

Java 21, Gradle (Kotlin DSL), JavaFX 21, JPA/Hibernate 7 + H2 (PostgreSQL mode), DI with Google Guice 7.

## Architecture

Hexagonal (Ports & Adapters):
- `entities/` — domain model (`Roof` is the sole JPA entity, mapped to `roofs` table)
- `usecases/` — `RoofsManager` (business logic), `ports/` (`Repository<E>`, `RoofRepository`, `UnitOfWork`)
- `adapters/persistence/` — `AbstractJPARepository`, `JPARoofRepository`, `JPAUnitOfWork` (also implements `EntityManagerProvider`)
- `adapters/ui/` — `RoofFormNode`, `RoofsRegistryNode`, `Stages`

Entrypoint: `roofsense.Main::main` — currently a stub (only has `//Guice.createInjector(new RoofSenseModule())` commented out). Real injector is `GuiceInjector::get` (eager singleton). DI wiring in `RoofSenseModule.java`.

Persistence: `persistence.xml` only exists at `src/test/resources/META-INF/` (in-memory H2, `drop-and-create`, PostgreSQL mode `MODE=PostgreSQL`). No production `persistence.xml` yet.

## Commands

| Command | What it does |
|---|---|
| `./gradlew test` | Unit + integration tests (excludes `show-node`-tagged UI tests) |
| `./gradlew runShowNodeTest` | Runs only `show-node`-tagged tests (JavaFX visual display) |
| `./gradlew check` | Checkstyle + PMD + SpotBugs + test + JaCoCo coverage (min 70%) |
| `./gradlew clean check` | Full clean build + verification (use when build artifacts might be stale) |
| `./gradlew test --tests "*ClassName*"` | Run a specific test class |

All tests run headless (`-Djava.awt.headless=true -Dtestfx.robot=glass -Dtestfx.headless=true -Dprism.order=sw`). No display needed.

## Testing

- JUnit 5, TestFX 4 (UI), Mockito 5 (with agent config for inline mocking — see `mockitoAgent` configuration in `build.gradle.kts`)
- DB tests use custom `JPAExtension` — creates fresh H2 in-memory per test, injects `@TestEntityManager` field via reflection
- UI tests extend `AbstractNodeTest` (TestFX `ApplicationExtension`), start real Guice injector per test, tagged `@Tag("show-node")` for visual-only
- `RoofFormNodeTest` / `RoofsRegistryNodeTest` use `FxRobot` for robot-style interaction
- JaCoCo 70% line coverage enforced via `jacocoTestCoverageVerification`

## Linting

Enforced by `org.danilopianini.gradle-java-qa` plugin (Checkstyle, PMD, SpotBugs). No local config files — all rules come from the plugin. Run `./gradlew check` to verify all.

## Pre-commit hook

`.githooks/pre-commit` runs `./gradlew check` on `main`, `develop`, and `release/*` branches. Install: `git config core.hooksPath .githooks`.

## Conventions

- Package: `roofsense.*` (not reverse-domain)
- `final` on all class/method/parameter declarations
- Tests use reflection-based test class access (package-private)
- JPA constraint violations → `IllegalArgumentException`, unexpected errors → `IllegalStateException`
- Validation messages in `ValidationMessages.properties`
- Code format: `R-XX` pattern validated by `@ValidCode` annotation (alphanumeric + dashes)
- Test method naming: third-person singular, camelCase with `Test` suffix (e.g. `validateNullShouldFailTest`)
- `@SuppressWarnings("PMD.LinguisticNaming")` commonly used on test classes that violate PMD naming rules
- Project report: `doc/report.qmd` (Quarto format)
