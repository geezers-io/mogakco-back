#!/bin/bash

echo "**********************"
echo "****** Testing *******"
echo "**********************"

docker run -d --rm -v $PWD/mogakco-back:/app -w /app $GRADLE_IMAGE "$@"