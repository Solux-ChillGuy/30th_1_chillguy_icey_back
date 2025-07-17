# 1. JDK 21 베이스 이미지 사용
FROM openjdk:21

# 2. 작업 디렉터리 설정 (컨테이너 내부 기준)
WORKDIR /app

# 3. JAR 파일을 컨테이너로 복사
COPY build/libs/moyora-0.0.1-SNAPSHOT.jar app.jar

# 4. 컨테이너 외부와 포트 매핑
EXPOSE 8080

# 5. 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
