# docker push
cd ./authentication
./gradlew clean build
docker build --tag cualestunombre/back:authentication -f Dockerfile.dev .
docker build --tag cualestunombre/local:authentication -f Dockerfile.arm.dev .
docker push cualestunombre/back:authentication
docker push cualestunombre/local:authentication
cd ..


cd ./community
./gradlew clean build
docker build --tag cualestunombre/back:community -f Dockerfile.dev .
docker build --tag cualestunombre/local:community -f Dockerfile.arm.dev .
docker push cualestunombre/back:community
docker push cualestunombre/local:community
cd ..

cd ./nginx
docker build --tag cualestunombre/back:nginx  .
docker build --tag cualestunombre/local:nginx -f Dockerfile.arm.dev .
docker push cualestunombre/back:nginx
docker push cualestunombre/local:nginx
cd ..

cd ./mysql
docker build --tag cualestunombre/back:mysql .
docker build --tag cualestunombre/local:mysql -f Dockerfile.arm.dev  .
docker push cualestunombre/local:mysql
docker push cualestunombre/back:mysql
cd ..

cd ./redis
docker build --tag cualestunombre/back:redis .
docker build --tag cualestunombre/local:redis -f Dockerfile.arm.dev  .
docker push cualestunombre/local:redis
docker push cualestunombre/back:redis
cd ..

cd ./socket
docker build --tag cualestunombre/back:socket .
docker build --tag cualestunombre/local:socket -f Dockerfile.arm.dev  .
docker push cualestunombre/local:socket
docker push cualestunombre/back:socket
cd ..

cd ./mongodb
docker build --tag cualestunombre/back:mongodb .
docker build --tag cualestunombre/local:mongodb -f Dockerfile.arm.dev .
docker push cualestunombre/local:mongodb
docker push cualestunombre/back:mongodb
cd ..