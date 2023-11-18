FROM openjdk:17

# 운영  환경용 Dockerfile
WORKDIR /usr/src/app/community

COPY ./build/libs/community-0.0.1-SNAPSHOT.jar ./

CMD ["java", "-jar", "-Dspring.profiles.active=operation", "./community-0.0.1-SNAPSHOT.jar"]

