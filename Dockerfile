# ───────────── 1단계: Gradle로 JAR 빌드 ─────────────
FROM gradle:8.5-jdk17-alpine AS builder
WORKDIR /workspace
COPY . .
RUN gradle clean bootJar --no-daemon

# ───────────── 2단계: 경량 런타임 이미지 ─────────────
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar","--spring.profiles.active=prod"]
