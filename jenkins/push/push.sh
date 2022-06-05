#!/bin/bash

echo "****************************"
echo "*** Ready to push Image ****"
echo "****************************"

IMAGE="mogakco-back"

echo "** Logging in Docker hub ***"
docker login -u jeidiiy -p $PASSWORD

echo "****** Tagging image *******"
docker tag $IMAGE:$BUILD_NUMBER jeidiiy/$IMAGE:$BUILD_NUMBER

echo "****** Pushing image *******"
docker push jeidiiy/$IMAGE:$BUILD_NUMBER