# --- Step 1: Gradle 빌드 단계 ---
FROM gradle:8.4.0-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project
RUN gradle build -x test

# --- Step 2: 실행 이미지 ---
FROM openjdk:21
WORKDIR /app
COPY --from=build /home/gradle/project/build/libs/icey-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
