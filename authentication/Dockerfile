FROM openjdk:17

# 운영 환경용 Dockerfile
WORKDIR /usr/src/app/authentication

COPY ./build/libs/authentication-0.0.1-SNAPSHOT.jar ./

# 빌드 후 실행
CMD ["java", "-jar", "-Dspring.profiles.active=opeation", "./authentication-0.0.1-SNAPSHOT.jar"]

