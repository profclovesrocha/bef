# ==================================
# DocManager - Version Service
# ==================================

FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY pom.xml ./pom.xml
COPY docmanager-common/pom.xml ./docmanager-common/pom.xml
COPY docmanager-version-service/pom.xml ./docmanager-version-service/pom.xml
COPY docmanager-document-service/pom.xml ./docmanager-document-service/pom.xml
COPY docmanager-storage-service/pom.xml ./docmanager-storage-service/pom.xml
COPY docmanager-metadata-service/pom.xml ./docmanager-metadata-service/pom.xml

RUN apk add --no-cache maven

RUN mvn dependency:go-offline -pl docmanager-common,docmanager-version-service -am -B || true

COPY docmanager-common/src ./docmanager-common/src
COPY docmanager-version-service/src ./docmanager-version-service/src

RUN mvn clean package -pl docmanager-common,docmanager-version-service -am -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S docmanager && adduser -S docmanager -G docmanager

COPY --from=builder /app/docmanager-version-service/target/*.jar app.jar

RUN chown -R docmanager:docmanager /app
USER docmanager

EXPOSE 8084

HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
    CMD wget -qO- http://localhost:8084/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
