##
## 1단계 ─ Gradle로 JAR 빌드
##
FROM gradle:8.5-jdk17-alpine AS builder          # Gradle 8.x + JDK 17
WORKDIR /workspace

# 종속성 캐시를 위해 스크립트 먼저 복사
COPY settings.gradle* build.gradle* gradle ./    # kts 도 포함되도록 * 사용
# 필요하면 gradle.properties, gradle-wrapper.properties 등 추가
RUN gradle clean bootJar --no-daemon

# 실제 소스 복사 후 재빌드
COPY src ./src
RUN gradle clean bootJar --no-daemon

##
## 2단계 ─ 경량 런타임 이미지
##
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=prod"]
