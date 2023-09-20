#!/bin/bash

# 로컬 빌드 디렉토리
build_dir="./build/libs"
# 빌드된 JAR 파일 이름
jar_file="authentication-0.0.1-SNAPSHOT.jar"
# 원격 서버 정보
remote_user="root"
remote_host="49.50.167.217"
remote_dir="/home/appdoc"

# Gradle 빌드 실행
./gradlew clean build

# 원격 서버에 JAR 파일 복사
scp "$build_dir/$jar_file" "$remote_user@$remote_host:$remote_dir/"

# 복사 완료 메시지 출력
echo "$jar_file 파일을 원격 서버의 $remote_dir 디렉토리에 복사했습니다."
