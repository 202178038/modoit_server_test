##
## 1단계 ─ Gradle로 JAR 빌드
##
FROM gradle:8.5-jdk17-alpine AS builder
WORKDIR /workspace

# (1) 의존성 캐시용 최소 파일만 먼저 복사
COPY settings.gradle* build.gradle* gradle ./
RUN gradle clean build -x test --no-daemon

# (2) 프로젝트 전체 복사  ← ★ src, resources 전부 포함
COPY . .

# (3) 실제 JAR 빌드
RUN gradle clean bootJar --no-daemon

##
## 2단계 ─ 경량 런타임 이미지
##
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar","--spring.profiles.active=prod"]
