# Copilot Instructions for Spring Boot Project Template

This document guides Copilot through the architecture, build system, and key conventions of this Spring Boot project template.

## Project Overview

A Spring Boot 4.0.2 (Java 21) template implementing a 4-layer architecture (presentation, application, domain, infrastructure) with DDD principles. Designed for microservices and small-to-medium backend applications.

**Key Technologies:**
- Spring Boot 3.4+ with Spring Security (JWT/OAuth 2.0)
- MyBatis for data persistence, PostgreSQL database
- Flyway for migrations (separate module)
- OpenAPI 3 / Swagger UI
- Docker & Docker Compose for containerization
- Spotless (Google Java Format) for code formatting

## Build and Test Commands

### Core Build Commands
```bash
# Build entire project (both app and migration modules)
./gradlew build

# Format code with Spotless (Google Java Format) - MUST be run before commits
./gradlew spotlessApply

# Format check (verify without modifying)
./gradlew spotlessCheck

# Show formatting diff
./gradlew spotlessDiagnose
```

### Testing

```bash
# Run all tests across all modules
./gradlew test

# Run tests for app module only
./gradlew app:test

# Run tests for migration module only
./gradlew migration:test

# Run a single test class
./gradlew test --tests UserApiTest

# Run a single test method
./gradlew test --tests UserApiTest.testGetUserById

# Run tests with debug output
./gradlew test --debug
```

### Application Execution

```bash
# Start all services (DB, migration, app) via Docker Compose
docker compose up --build

# Stop all services
docker compose down

# View service logs
docker compose logs -f app
docker compose logs -f migration
docker compose logs -f postgres

# Run app locally (requires Java 21 + PostgreSQL running)
./gradlew app:bootRun
```

### Database Migrations

```bash
# Migrate database to latest version (dev environment)
./gradlew migration:flywayMigrate

# Migrate for specific environment
ENV=stg ./gradlew migration:flywayMigrate

# Check migration status
./gradlew migration:flywayInfo

# Clean database (WARNING: deletes all data)
./gradlew migration:flywayClean

# Build migration JAR for CI/CD
./gradlew migration:clean migration:shadowJar
```

## Project Structure and Architecture

The application follows a layered architecture with clear separation of concerns:

### 1. **Presentation Layer** (`presentation/`)
- REST API controllers (`api/UserApi.java`)
- Request/Response DTOs (`UserRequest.java`, `UserResponse.java`)
- Global exception handling (`GlobalExceptionHandler.java`)
- Responsibility: Validate requests, format responses, handle HTTP concerns

### 2. **Application Layer** (`application/`)
- Service classes coordinating business logic (`service/UserService.java`)
- Command/Query objects for use case encapsulation
- Transaction management via `@Transactional`
- Retry logic via `@Retryable` for resilience (handles DB connection errors)
- Responsibility: Orchestrate domain objects, implement use cases

### 3. **Domain Layer** (`domain/`)
- Pure business logic models (`domain/model/User.java`)
- Repository interfaces defining persistence contracts
- Domain services (`UserDomainService.java`)
- Domain exceptions (`UserNotFoundException.java`)
- Responsibility: Encapsulate business rules, independent of frameworks

### 4. **Infrastructure Layer** (`infrastructure/`)
- Repository implementations (`repository/UserDatasource.java`)
- MyBatis mappers (`mapper/UserMapper.java`)
- Spring Security configuration (`config/SecurityConfig.java`)
- OpenAPI configuration (`config/OpenApiConfig.java`)
- Custom type handlers for UUID, LocalDateTime mappings
- Responsibility: Implement technical details (DB, external APIs, frameworks)

### Database and Migrations

The `migration/` module (separate from `app/`) manages database schema and data:
- **`schema/`**: DDL scripts (table definitions) - e.g., `V1_0_0__init.sql`
- **`data/dev/`**: DML scripts for development environment - e.g., `V1_0_1__init_data.sql`
- **`data/stg/`**: DML scripts for staging environment
- Flyway naming convention: `V<Version>__<Description>.sql`

## Key Conventions

### 1. **Package Structure**
```
cn.gekal.spring.template
├── presentation
│   ├── api              # REST API controllers and DTOs
│   └── GlobalExceptionHandler
├── application
│   ├── service          # Application services with @Transactional
│   ├── command          # Command objects for write operations
│   └── query            # Query objects for read operations
├── domain
│   ├── model            # Entities and value objects
│   ├── repository       # Repository interfaces (contracts)
│   ├── service          # Domain services with business logic
│   └── <DomainName>Exception  # Domain-specific exceptions
└── infrastructure
    ├── repository       # Repository implementations (datasources)
    ├── config           # Spring beans and configurations
    ├── mapper           # MyBatis mapper files
    └── <Mapper>Handler  # Custom type handlers
```

### 2. **Security & Authentication**
- JWT-based authentication via Spring Security (OAuth 2.0 Resource Server)
- Public key at `app/src/main/resources/jwt/public-key.pem`
- Scope-based authorization: `users::read`, `users::create`, `users::update`, `users::delete`
- Generate test tokens: `./generate-jwt.sh`
- Authorization header: `Authorization: Bearer <token>`

### 3. **Exception Handling**
- Domain exceptions inherit from custom base (e.g., `UserNotFoundException`)
- Caught and translated to HTTP responses by `GlobalExceptionHandler`
- Use meaningful exception names following `<Entity><Action>Exception` pattern

### 4. **Testing Strategy**
- **Unit tests** in `app/src/test/`: test individual components in isolation
- **Repository tests** use `@MybatisTest` to validate SQL mappings
- **API tests** use `MockMvc` to test controllers with mocked services
- **Retry tests** validate resilience logic for connection failures
- Mock dependencies with Mockito (`@Mock`, `@InjectMocks`)
- Use custom Jackson ObjectMapper config for test serialization

### 5. **MyBatis Configuration**
- Mapper files in `app/src/main/resources/mybatis/mapper/`
- Type aliases enabled: domain models resolve without package prefix
- Underscore-to-camel-case mapping enabled for SQL columns
- Custom type handlers for UUID and LocalDateTime in `infrastructure/config/`

### 6. **Code Formatting & Quality**
- **Mandatory:** Run `./gradlew spotlessApply` before committing
- Format: Google Java Format (via Spotless plugin)
- Applies to all `.java`, `.gradle`, and `.md` files
- CI will fail if code is not formatted

### 7. **Configuration**
- App config: `app/src/main/resources/application.yaml`
- Database connection: Configurable via environment variables (dev default: localhost:15432)
- AWS API Gateway integration: Configure `aws.apigateway` section with VPC Link ID and ALB URI
- Swagger UI available at `/swagger-ui.html` when running

### 8. **Resilience**
- `UserService` has `@Retryable` for automatic retry on DB connection errors
- Prevents transient failures from propagating to clients

## Common Development Tasks

### Adding a New API Endpoint
1. Create domain model in `domain/model/`
2. Create repository interface in `domain/repository/`
3. Implement repository in `infrastructure/repository/` with MyBatis mapper
4. Create application service in `application/service/`
5. Create REST controller in `presentation/api/`
6. Add test for each layer (repository, service, API)
7. Run `./gradlew spotlessApply` before committing

### Adding Database Migration
1. Create SQL file in `migration/src/main/resources/db/migration/schema/` with naming `V<Version>__<Description>.sql`
2. For initial data: create file in `migration/src/main/resources/db/migration/data/dev/`
3. Run `./gradlew migration:flywayMigrate` to verify locally
4. Commit both migration and app code

### Running Against Docker Compose
```bash
# Full stack (postgres, migration, app)
docker compose up --build

# API endpoint: http://localhost:18080
# Swagger: http://localhost:18080/swagger-ui.html
# Health: http://localhost:18080/actuator/health
```

## MCP Servers

This project benefits from the following MCP servers for enhanced development experience:

### PostgreSQL MCP
Query and manage the development database directly from Copilot sessions.
- **Database:** template
- **Host:** localhost:15432 (when running locally)
- **Credentials:** user=`myuser`, password=`secret`
- Use to: Inspect schema, run ad-hoc queries, verify data during development

### Docker MCP
Manage containers, images, and Docker Compose services.
- Use to: Check running containers, view logs, manage images, interact with Docker Compose
- Commands: `docker compose up`, `docker compose logs`, `docker ps`, etc.

### Bash/Shell MCP
Enhanced shell command automation and context-aware execution.
- Use to: Run Gradle tasks, manage git operations, execute scripts more efficiently

## Notes

- **Java Version:** Must use Java 21 (see `build.gradle` toolchain config)
- **Spring Boot Version:** 4.0.2 (Spring Boot 3.4+)
- **Rewrite Plugin:** Configured to auto-upgrade to Spring Boot 4.0 recipes
- **Development Tools:** Spring Boot DevTools and Docker Compose enabled in dev
