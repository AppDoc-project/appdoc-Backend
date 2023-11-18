# docker push
cd ./authentication
./gradlew clean build
docker push cualestunombre/back:authentication
cd ..


cd ./community
./gradlew clean build
docker push cualestunombre/back:community
cd ..

cd ./nginx
docker push cualestunombre/back:nginx
cd ..

cd ./mysql
docker push cualestunombre/back:mysql
cd ..


