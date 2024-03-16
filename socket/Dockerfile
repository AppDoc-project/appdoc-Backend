# 운영 환경용 Dockerfile
FROM --platform=linux/amd64  node

# 작업 디렉토리 설정
WORKDIR /usr/src/app

# 패키지 파일 복사
COPY package*.json ./

# 앱 의존성 설치
RUN npm install

# 소스 코드 복사
COPY . .

# 환경 변수 설정
ENV NODE_ENV=operation
ENV PINPOINT_COLLECTOR_IP=211.253.26.21
ENV PINPOINT_APPLICATION_NAME=beatmate-socket
ENV PINPOINT_AGENT_ID=agent1

# 컨테이너 실행 시 실행할 명령어 설정
CMD ["node", "./app.js"]
