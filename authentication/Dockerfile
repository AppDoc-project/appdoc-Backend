FROM --platform=linux/amd64 openjdk:17

# 운영 환경용 Dockerfile
WORKDIR /Users/woo/src/app/authentication

COPY ./agent ./agent

COPY ./build/libs/authentication-0.0.1-SNAPSHOT.jar ./



# 빌드 후 실행
ENTRYPOINT  ["java", "-jar", "-Dspring.profiles.active=operation", "./authentication-0.0.1-SNAPSHOT.jar"]
# ENTRYPOINT  ["java", "-jar", "-javaagent:./agent/pinpoint-bootstrap-2.5.3.jar", "-Dpinpoint.agentId=agent1","-Dpinpoint.applicationName=beatmate-auth","-Dpinpoint.config=./agent/pinpoint-root.config","-Dspring.profiles.active=operation", "./authentication-0.0.1-SNAPSHOT.jar"]

