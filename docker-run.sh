#!/bin/bash
set -e

# 변수 설정: 본인의 Docker Hub 사용자명과 이미지 이름으로 변경하세요.
DOCKERHUB_USERNAME="rlarbals9907"
IMAGE_NAME="allright-springboot-app"
DOCKERHUB_REPO="${DOCKERHUB_USERNAME}/${IMAGE_NAME}"

echo "============================"
echo "Docker Hub에서 이미지 다운로드 시작"
echo "다운로드 받을 이미지: ${DOCKERHUB_REPO}:latest"
echo "============================"

# Docker Hub에서 최신 이미지 pull
sudo docker pull ${DOCKERHUB_REPO}:latest

echo "============================"
echo "애플리케이션 컨테이너 실행 중..."
echo "호스트 포트 8080을 컨테이너의 8080 포트에 매핑합니다."
echo "============================"

# 기존에 같은 이름의 컨테이너가 실행중이면 제거 (선택사항)
if [ "$(sudo docker ps -q -f name=springboot-app)" ]; then
    echo "기존 springboot-app 컨테이너가 실행 중입니다. 컨테이너를 중지합니다."
    sudo docker stop springboot-app
fi

# Docker 컨테이너 실행 (-d: 백그라운드 실행, --rm: 종료 시 컨테이너 자동 삭제)
sudo docker run -d --rm --name springboot-app -p 8080:8080 ${DOCKERHUB_REPO}:latest

echo "============================"
echo "컨테이너 실행 완료! 실행 중인 컨테이너 목록:"
sudo docker ps
echo "============================"
