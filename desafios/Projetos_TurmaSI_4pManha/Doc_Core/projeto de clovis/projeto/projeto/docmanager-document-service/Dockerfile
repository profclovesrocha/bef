# ==================================
# DocManager - Document Service
# Multi-stage build para imagem otimizada
# ==================================

# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copia os POMs primeiro para cache de dependências
COPY pom.xml ./pom.xml
COPY docmanager-common/pom.xml ./docmanager-common/pom.xml
COPY docmanager-document-service/pom.xml ./docmanager-document-service/pom.xml
COPY docmanager-storage-service/pom.xml ./docmanager-storage-service/pom.xml
COPY docmanager-metadata-service/pom.xml ./docmanager-metadata-service/pom.xml
COPY docmanager-version-service/pom.xml ./docmanager-version-service/pom.xml

# Instala Maven
RUN apk add --no-cache maven

# Download de dependências (cache layer)
RUN mvn dependency:go-offline -pl docmanager-common,docmanager-document-service -am -B || true

# Copia código fonte
COPY docmanager-common/src ./docmanager-common/src
COPY docmanager-document-service/src ./docmanager-document-service/src

# Build
RUN mvn clean package -pl docmanager-common,docmanager-document-service -am -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S docmanager && adduser -S docmanager -G docmanager

COPY --from=builder /app/docmanager-document-service/target/*.jar app.jar

RUN chown -R docmanager:docmanager /app
USER docmanager

EXPOSE 8081

HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
    CMD wget -qO- http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
