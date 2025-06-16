#!/bin/bash
set -e

# 변수 설정
IMAGE_NAME="allright-springboot-app"
DOCKERHUB_USERNAME="rlarbals9907"
DOCKERHUB_REPO="${DOCKERHUB_USERNAME}/${IMAGE_NAME}"

echo "============================"
echo "Gradle 빌드 시작"
./gradlew clean build -x test -Pprofile=prod
echo "빌드 완료"
echo "============================"

echo "============================"
echo "Docker 이미지 빌드 시작"
echo "이미지: ${DOCKERHUB_REPO}:latest"
echo "============================"

# Docker 이미지 빌드 (현재 디렉토리의 Dockerfile 사용)
docker build --platform=linux/amd64 -t ${DOCKERHUB_REPO}:latest .

echo "============================"
echo "Docker Hub Push Start"
echo "============================"

# Docker Hub에 이미지 푸시 (latest 태그)
docker push ${DOCKERHUB_REPO}:latest

echo "============================"
echo "작업 완료"
echo "============================"
