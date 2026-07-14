FROM openjdk:21-jdk-slim AS build
WORKDIR /app
COPY ewm-stats-service/target/*.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "stats-server.jar"]