# ==================================
# DocManager - API Gateway
# ==================================

FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY pom.xml ./pom.xml
COPY docmanager-gateway/pom.xml ./docmanager-gateway/pom.xml
COPY docmanager-common/pom.xml ./docmanager-common/pom.xml
COPY docmanager-document-service/pom.xml ./docmanager-document-service/pom.xml
COPY docmanager-storage-service/pom.xml ./docmanager-storage-service/pom.xml
COPY docmanager-metadata-service/pom.xml ./docmanager-metadata-service/pom.xml
COPY docmanager-version-service/pom.xml ./docmanager-version-service/pom.xml

RUN apk add --no-cache maven

RUN mvn dependency:go-offline -pl docmanager-gateway -B || true

COPY docmanager-gateway/src ./docmanager-gateway/src

RUN mvn clean package -pl docmanager-gateway -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S docmanager && adduser -S docmanager -G docmanager

COPY --from=builder /app/docmanager-gateway/target/*.jar app.jar

RUN chown -R docmanager:docmanager /app
USER docmanager

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
