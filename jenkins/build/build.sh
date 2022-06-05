#!/bin/bash

echo "*******************************"
echo "**** Building Docker Image ****"
echo "*******************************"

cd $PWD/jenkins/build/ && docker compose -f docker-compose-build.yml build