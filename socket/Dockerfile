FROM --platform=linux/amd64 node

# 운영  환경용 Dockerfile
WORKDIR /usr/src/app/chat

COPY package*.json ./

# 앱 의존성 설치
RUN npm install

# 소스 코드 복사 (node_modules는 .dockerignore에 기록된 패턴에 맞게 자동으로 제외됨)
COPY . .

# 환경 변수 설정
ENV NODE_ENV=dev

# 컨테이너 실행 시 실행할 명령어 설정
CMD ["node", "./app.js"]