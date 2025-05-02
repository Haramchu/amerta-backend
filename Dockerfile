# ---- STAGE 1: Build the JAR ----
FROM gradle:8.5-jdk21-alpine AS builder

WORKDIR /app
COPY . .
RUN gradle bootJar
    
# ---- STAGE 2: Run the app ----
FROM eclipse-temurin:21-jdk-alpine
    
WORKDIR /app
ENV PORT=8080
    
COPY --from=builder /app/build/libs/*.jar app.jar
    
ENTRYPOINT ["sh", "-c", "java -Dserver.port=$PORT -jar app.jar"]
    