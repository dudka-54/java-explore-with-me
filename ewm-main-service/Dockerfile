FROM openjdk:21-jdk-slim AS build
WORKDIR /app
COPY --from=build /app/ewm-main-service/main-server/target/main-server-*.jar main-server.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "main-server.jar"]