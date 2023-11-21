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


