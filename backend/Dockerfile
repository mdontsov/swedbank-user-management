FROM eclipse-temurin:26-jdk AS build
WORKDIR /workspace
COPY gradlew gradlew
COPY gradle gradle
COPY settings.gradle.kts build.gradle.kts ./
COPY backend/build.gradle.kts backend/build.gradle.kts
RUN chmod +x gradlew && ./gradlew :backend:dependencies --no-daemon
COPY backend/src backend/src
RUN ./gradlew :backend:bootJar --no-daemon

FROM eclipse-temurin:26-jre
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /workspace/backend/build/libs/backend-1.0.0.jar app.jar
EXPOSE 8080
USER 10001
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

