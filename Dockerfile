# ---------- DETERMINISTIC BUILD STAGE ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build

ENV MAVEN_OPTS="-Djava.util.concurrent.ForkJoinPool.common.parallelism=1"

WORKDIR /app

# Copy only pom first for dependency caching and offline prep
COPY pom.xml .
RUN mvn -B dependency:go-offline --no-snapshot-updates

COPY src src

RUN mvn -B clean package -DskipTests --no-snapshot-updates

# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:17.0.10_7-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

