# syntax=docker/dockerfile:1

# Refercence
# https://docs.spring.io/spring-boot/reference/packaging/container-images/dockerfiles.html

# -----------------------------------------------------------------------------
# Stage 1: Build the application
# -----------------------------------------------------------------------------
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Gradle wrapper and configuration files
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY app/build.gradle app/
COPY migration/build.gradle migration/
COPY gradle/libs.versions.toml gradle/

# Download dependencies
RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon dependencies > /dev/null || true

# Copy source code
COPY . .

# Build the application
RUN --mount=type=cache,target=/root/.gradle ./gradlew migration:shadowJar app:bootJar --no-daemon

# -----------------------------------------------------------------------------
# Stage 2: Extract Layers
# -----------------------------------------------------------------------------
FROM eclipse-temurin:25-jdk AS extractor
WORKDIR /builder

COPY --from=builder /app/migration/build/libs/*.jar migration.jar
COPY --from=builder /app/app/build/libs/*.jar app.jar

RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted

# -----------------------------------------------------------------------------
# Stage 3: Runtime Image
# -----------------------------------------------------------------------------
FROM eclipse-temurin:21-jre

RUN groupadd -r appuser && useradd -r -g appuser appuser  \
    && mkdir -p /app && chown -R appuser:appuser /app

WORKDIR /app

COPY start.sh .
RUN chown -R appuser:appuser /app/start.sh && chmod u+x /app/start.sh

USER appuser

COPY --from=extractor /builder/extracted/dependencies/ ./
COPY --from=extractor /builder/extracted/spring-boot-loader/ ./
COPY --from=extractor /builder/extracted/snapshot-dependencies/ ./
COPY --from=extractor /builder/extracted/application/ ./

COPY --from=extractor /builder/migration.jar migration.jar

ENTRYPOINT ["/app/start.sh"]
