#!/bin/bash

echo "**********************"
echo "***** Deploying ******"
echo "**********************"

echo mogakco-back >/tmp/.auth
echo $BUILD_TAG >>/tmp/.auth

scp -i ~/.ssh/id_rsa /tmp/.auth ubuntu@ec2-3-36-75-245.ap-northeast-2.compute.amazonaws.com:/tmp/.auth

export IMAGE=$(sed -n '1p' /tmp/.auth)
export TAG=$(sed -n '2p' /tmp/.auth)

containerIdOfMogakcoBack=$(docker ps -aq -f name="$IMAGE")
if [ -n "$containerIdOfMogakcoBack" ]; then isRunning=true; else isRunning=false; fi

if [ -n "$isRunning" ]; then
  docker stop "$containerIdOfMogakcoBack"
  docker rm "$containerIdOfMogakcoBack"
fi

cd jenkins/deploy && docker compose up -d
