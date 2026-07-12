#!/bin/bash

set -e

IMAGE_NAME="luql11/blog"

echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
#export DOCKER_USERNAME=luql11
#export DOCKER_PASSWORD=_xxxxxxxxxxxxxxxxx
sleep 2

VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

echo "======================================"
echo "Building ${IMAGE_NAME}:${VERSION}"
echo "======================================"

docker buildx build --platform linux/amd64 --progress=plain -t ${IMAGE_NAME}:${VERSION} --push .

echo "======================================"
echo "Push ${VERSION}"
echo "======================================"

docker push ${IMAGE_NAME}:${VERSION}

echo "======================================"
echo "Push latest"
echo "======================================"

