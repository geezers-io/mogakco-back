#!/bin/bash

echo "*******************************"
echo "**** Building Docker Image ****"
echo "*******************************"

export BUILD_TAG=1

cd $PWD/jenkins/build/ && docker compose -f docker-compose-build.yml build