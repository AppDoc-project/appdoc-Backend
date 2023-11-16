FROM openjdk:17

# 개발 환경용 Dockerfile
WORKDIR /usr/src/app/community

COPY ./build/libs/community-0.0.1-SNAPSHOT.jar ./

# 빌드 후 실행
CMD ["java", "-jar", "-Dspring.profiles.active=operation", "./community-0.0.1-SNAPSHOT.jar"]

