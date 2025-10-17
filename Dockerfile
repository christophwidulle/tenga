# Multi-stage build for Tenga Knowledge Base

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and frontend
COPY src ./src
COPY frontend ./frontend

# Build backend + frontend together
# Frontend will be built by frontend-maven-plugin and placed in src/main/resources/static
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -S tenga && adduser -S tenga -G tenga

# Copy jar from build stage (contains both backend and frontend)
COPY --from=build /app/target/*.jar app.jar

# Change ownership
RUN chown -R tenga:tenga /app

# Switch to non-root user
USER tenga

# Expose port (both API and Web UI on same port)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
