#!/bin/bash

echo "**********************"
echo "**** Building jar ****"
echo "**********************"

echo $PWD
export JAVA_IMAGE=openjdk:11

docker run -d --rm -v $PWD:/app -w /app $JAVA_IMAGE "$@"