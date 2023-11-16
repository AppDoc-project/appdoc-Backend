cd ./authentication
./gradlew clean build
cd ..
cd ./community
./gradlew clean build

docker-compose up --build