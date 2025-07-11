# — ① 어떤 JDK 이미지를 쓸 것인가
FROM eclipse-temurin:17-jdk-alpine
# JDK 17, Alpine(경량)

# — ② 컨테이너 안에서 작업할 디렉터리
WORKDIR /app

# — ③ 로컬에서 빌드한 JAR 복사
#    • Gradle → ./gradlew clean build 뒤에 build/libs/ 아래 *.jar 생성되는지 확인
#    • JAR 이름을 고정하려면 build.gradle 에
#        bootJar { archiveFileName = "app.jar" }
#      를 추가하면 *.jar 대신 app.jar 로 복사 가능합니다.
COPY build/libs/*.jar app.jar

# — ④ 컨테이너 기동 시 실행할 명령
ENTRYPOINT ["java","-jar","/app/app.jar","--spring.profiles.active=prod"]

# — ⑤ 내부 8080 포트를 외부에 노출
EXPOSE 8080
