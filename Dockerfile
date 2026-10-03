FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends \
    libxtst6 \
    libxrender1 \
    libxi6 \
    libxext6 \
    libx11-6 \
    fontconfig \
    && rm -rf /var/lib/dpkg/archives/* /var/lib/apt/lists/*

COPY --from=build /app/target/agenda-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
