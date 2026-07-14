FROM openjdk:21-jdk-slim AS build
WORKDIR /app
COPY ewm-main-server/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "main-server.jar"]