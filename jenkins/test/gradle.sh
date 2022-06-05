#!/bin/bash

echo "**********************"
echo "****** Testing *******"
echo "**********************"

docker run -d --rm -v $PWD:/app -w /app $JAVA_IMAGE $@