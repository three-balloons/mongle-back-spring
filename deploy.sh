#!/bin/bash

# 현재 PID 확인
CURRENT_PID=$(pgrep -f .jar)
echo "Current PID: $CURRENT_PID"

# 실행 중인 프로세스가 있으면 종료
if [ -z "$CURRENT_PID" ]; then
    echo "No process is currently running."
else
    echo "Killing process: $CURRENT_PID"
    sudo kill -9 $CURRENT_PID
    sleep 3
fi

# 새로운 JAR 파일 경로 설정 (원격 서버 경로에 맞게 수정)
JAR_PATH="/home/ubuntu/bubble/back/build/libs/*.jar"
echo "JAR path: $JAR_PATH"

# JAR 파일에 실행 권한 부여
sudo chmod +x $JAR_PATH

# 새로운 JAR 파일 실행
nohup java -jar $JAR_PATH > /home/ubuntu/bubble/back/build/libs/nohup.out 2>&1 &
echo "JAR file deployed successfully."
