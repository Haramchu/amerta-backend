# ---- STAGE 1: Build the JAR ----
FROM gradle:8.5-jdk21-alpine as builder

WORKDIR /app
COPY . .

RUN gradle bootJar

# ---- STAGE 2: Run in lightweight container ----
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Let Cloud Run inject the PORT env var
ENV PORT=8080

COPY --from=builder /app/build/libs/*.jar app.jar

# Use the PORT env var Spring Boot needs
ENTRYPOINT ["sh", "-c", "java -Dserver.port=$PORT -jar app.jar"]
