#!/bin/bash

echo "**********************"
echo "**** Building jar ****"
echo "**********************"

export JAVA_HOME=openjdk:11

docker run -d --rm -v $PWD:/app -w /app $JAVA_IMAGE $@