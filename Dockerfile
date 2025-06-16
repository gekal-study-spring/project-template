FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY . .

RUN ./gradlew clean migration:shadowJar app:bootJar --no-daemon

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY start.sh .
RUN chmod +x start.sh

COPY --from=build /app/migration/build/libs/*.jar migration.jar
COPY --from=build /app/app/build/libs/*.jar app.jar

ENTRYPOINT ["/app/start.sh"]
