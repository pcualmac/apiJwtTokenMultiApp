# Runtime stage# Build stage
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY ./apiJwtToken/pom.xml .
RUN mvn dependency:go-offline
COPY ./apiJwtToken/src ./src
RUN mvn clean install -DskipTests

# Runtime stage
FROM openjdk:17-jdk-slim
RUN apt-get update && \
    apt-get install -y curl iputils-ping redis-tools && \
    rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /app/target/apiJwtToken-0.0.1-SNAPSHOT.jar /app/apiJwtToken-0.0.1-SNAPSHOT.jar