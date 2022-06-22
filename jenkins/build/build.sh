#!/bin/bash

echo "**********************************"
echo "**** Testing and Building jar ****"
echo "**********************************"

./gradlew clean build

echo "*******************************"
echo "**** Building Docker Image ****"
echo "*******************************"

cd $PWD/jenkins/build/ && docker compose -f docker-compose-build.yml build