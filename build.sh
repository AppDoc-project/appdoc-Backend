# 개발 환경용 docker-compose 파일
cd ./authentication
./gradlew clean build

cd ..
cd ./community
./gradlew clean build


docker-compose up --build