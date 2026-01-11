FROM eclipse-temurin:21-jdk-alpine AS builder

# Install dos2unix to fix Windows line endings
RUN apk add --no-cache dos2unix

WORKDIR /app

# Copy build files (cached layer)
COPY gradle/ gradle/
COPY gradlew gradlew.bat settings.gradle.kts gradle.properties ./
COPY buildSrc/ buildSrc/

# Copy module build files
COPY campaign-app/build.gradle.kts campaign-app/
COPY campaign-core/build.gradle.kts campaign-core/
COPY campaign-detail/build.gradle.kts campaign-detail/

# Fix line endings and make executable
RUN dos2unix gradlew && chmod +x gradlew

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY campaign-app/src campaign-app/src
COPY campaign-core/src campaign-core/src
COPY campaign-detail/src campaign-detail/src

# Build
RUN ./gradlew :campaign-app:bootJar --no-daemon

# ============================================
# STAGE 2: RUNTIME
# ============================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Security: non-root user
RUN addgroup -g 1001 appgroup && adduser -u 1001 -G appgroup -D appuser

# Download OpenTelemetry Java Agent
ADD --chmod=644 https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.10.0/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# Copy only the JAR
COPY --from=builder /app/campaign-app/build/libs/*.jar app.jar

RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "app.jar"]
