#!/bin/bash

echo "**********************"
echo "****** Testing *******"
echo "**********************"

export GRADLE_IMAGE="gradle:7.4.2-jdk11-alpine"

docker run -d --rm -v $PWD/mogakco-back:/app -w /app $GRADLE_IMAGE "$@"