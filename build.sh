cd ./authentication
./gradlew clean build
docker build --tag cualestunombre/operation:authentication -f Dockerfile .
docker build --tag cualestunombre/dev:authentication -f Dockerfile.dev .
docker push cualestunombre/operation:authentication
docker push cualestunombre/dev:authentication
cd ..


cd ./community
./gradlew clean build
docker build --tag cualestunombre/operation:community -f Dockerfile .
docker build --tag cualestunombre/dev:community -f Dockerfile.dev .
docker push cualestunombre/operation:community
docker push cualestunombre/dev:community
cd ..

cd ./nginx
docker build --tag cualestunombre/operation:nginx  -f Dockerfile .
docker build --tag cualestunombre/dev:nginx -f Dockerfile.dev .
docker push cualestunombre/operation:nginx
docker push cualestunombre/dev:nginx
cd ..

cd ./mysql
docker build --tag cualestunombre/operation:mysql -f Dockerfile .
docker build --tag cualestunombre/dev:mysql -f Dockerfile.dev  .
docker push cualestunombre/operation:mysql
docker push cualestunombre/dev:mysql
cd ..

cd ./redis
docker build --tag cualestunombre/operation:redis -f Dockerfile .
docker build --tag cualestunombre/dev:redis -f Dockerfile.dev  .
docker push cualestunombre/operation:redis
docker push cualestunombre/dev:redis
cd ..

cd ./socket
docker build --tag cualestunombre/operation:socket -f Dockerfile .
docker build --tag cualestunombre/dev:socket -f Dockerfile.dev  .
docker push cualestunombre/operation:socket
docker push cualestunombre/dev:socket
cd ..

cd ./mongodb
docker build --tag cualestunombre/operation:mongodb -f Dockerfile .
docker build --tag cualestunombre/dev:mongodb -f Dockerfile.dev .
docker push cualestunombre/operation:mongodb
docker push cualestunombre/dev:mongodb
cd ..

cd ./rabbit
docker build --tag cualestunombre/operation:rabbit -f Dockerfile .
docker build --tag cualestunombre/dev:rabbit -f Dockerfile.dev .
docker push cualestunombre/operation:rabbit
docker push cualestunombre/dev:rabbit
cd ..