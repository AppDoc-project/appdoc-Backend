FROM --platform=linux/amd64 openjdk:17 

# 운영  환경용 Dockerfile
WORKDIR /Users/woo/src/app/community

COPY ./build/libs/community-0.0.1-SNAPSHOT.jar ./

COPY ./agent ./agent

# ENTRYPOINT  ["java", "-jar", "-javaagent:./agent/pinpoint-bootstrap-2.5.3.jar", "-Dpinpoint.agentId=agent1","-Dpinpoint.applicationName=beatmate-community","-Dpinpoint.config=./agent/pinpoint-root.config","-Dspring.profiles.active=operation", "./community-0.0.1-SNAPSHOT.jar"]

ENTRYPOINT  ["java", "-jar", "-Dspring.profiles.active=operation", "./community-0.0.1-SNAPSHOT.jar"]