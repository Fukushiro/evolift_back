FROM gradle:8.14.5-jdk17-alpine AS build

WORKDIR /app
COPY . .
RUN gradle --no-daemon bootJar

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=build --chown=10001:10001 /app/build/libs/Evolift-0.0.1-SNAPSHOT.jar app.jar
USER 10001:10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
