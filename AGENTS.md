# RoofSense — Agent Guide

Java 21, Gradle (Kotlin DSL), JavaFX 21, JPA/Hibernate 7 + H2 (PostgreSQL mode), DI with Avaje Inject 12.6 (compile-time, annotation-processing).

## Architecture

Hexagonal (Ports & Adapters):
- `entities/` — domain model (`Roof` is the sole JPA entity, mapped to `roofs` table)
- `usecases/` — `RoofsManager` (business logic), `ports/` (`Repository<E>`, `RoofRepository`, `UnitOfWork`)
- `adapters/persistence/` — `AbstractJPARepository`, `JPARoofRepository`, `JPAUnitOfWork` (also implements `EntityManagerProvider`), `JPAFactory` (`@Factory` producing `EntityManagerFactory`)
- `adapters/ui/` — `RoofFormNode`, `RoofsRegistryNode`, `Stages`

Entrypoint: `roofsense.Main::main` — currently a no-op (empty method body). No manual injector bootstrap; Avaje Inject resolves beans via compile-time annotation processing.

DI annotations in use: `@Singleton`, `@Prototype` (new instance per lookup), `@Primary` (disambiguates implementations), `@Factory` + `@Bean` (factory-produced beans). Avaje generates `*$DI.class` files excluded from SpotBugs.

Persistence: `src/main/resources/META-INF/persistence.xml` (persistence unit `RoofSense`). Uses `${db.driver}`, `${db.url}`, etc. placeholders resolved from JPA system properties set in `build.gradle.kts`. DB config for tests: in-memory H2, `drop-and-create`, PostgreSQL mode (`MODE=PostgreSQL`).

## Commands

| Command                                | What it does                                                              |
|----------------------------------------|---------------------------------------------------------------------------|
| `./gradlew test`                       | Unit + integration tests (excludes `showNodeTest`-tagged UI tests)        |
| `./gradlew runShowNodeTest`            | Runs only `showNodeTest`-tagged tests (JavaFX visual display)             |
| `./gradlew check`                      | Checkstyle + PMD + SpotBugs + test + JaCoCo coverage (min 70%)            |
| `./gradlew clean check`                | Full clean build + verification (use when build artifacts might be stale) |
| `./gradlew test --tests "*ClassName*"` | Run a specific test class                                                 |

When writing code, an agent must NOT run the `check` task to verify its work — only `./gradlew test` (or a scoped test run). The results of `check` are validated manually by the programmer afterwards.

All tests run headless (`-Djava.awt.headless=true -Dtestfx.robot=glass -Dtestfx.headless=true -Dprism.order=sw`). No display needed.

## Testing

- JUnit 5, TestFX 4 (UI), Mockito 5 (with agent config for inline mocking — see `mockitoAgent` configuration in `build.gradle.kts`)
- DB tests use custom `JPAExtension` — creates fresh H2 in-memory per test, injects `@TestEntityManager` field via reflection
- UI tests extend `AbstractNodeTest` (TestFX `ApplicationExtension`), start a real Avaje `BeanScope` per test via `BeanScope.builder().build()`, tagged `@Tag("showNodeTest")` for visual-only
- `RoofFormNodeTest` / `RoofsRegistryNodeTest` use `FxRobot` for robot-style interaction
- JaCoCo 70% line coverage enforced via `jacocoTestCoverageVerification`

## Linting

Enforced by `org.danilopianini.gradle-java-qa` plugin (Checkstyle, PMD, SpotBugs). No local config files — all rules come from the plugin. Run `./gradlew check` to verify all.

For test files, the following Checkstyle suppressions are permitted via inline comments:

- `// CHECKSTYLE: MultipleStringLiterals OFF` — repeated assertion messages
- `// CHECKSTYLE: MagicNumber OFF` — numeric test literals

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
