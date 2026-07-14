FROM openjdk:21-jdk-slim AS build
WORKDIR /app
COPY --from=build /app/ewm-stats-service/stats-server/target/stats-server-*.jar stats-server.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "stats-server.jar"]