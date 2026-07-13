#!/bin/bash
# 启动后端，完全脱离终端
PMS_ROOT="/workspace/network-equipment-pms"
LOG_DIR="/workspace/.env/logs"
mkdir -p "$LOG_DIR"

# 加载持久化密钥
export JWT_SECRET=$(cat /workspace/.env/secrets/jwt-secret.txt)
export APP_ENCRYPT_KEY=$(cat /workspace/.env/secrets/app-encrypt-key.txt)
export SPRING_DATASOURCE_URL='jdbc:mysql://127.0.0.1:3306/pms?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true'
export MYSQL_USER='pms'
export MYSQL_PASSWORD='pmspassword'
export APP_CONNECTOR_ENCRYPT_KEY='test-connector-key-32b-padding!'
export LOWCODE_ENCRYPTION_KEY='test-lowcode-encryption-key-32b!'
export OTEL_SDK_DISABLED='true'

# 停止旧进程
pkill -f "pms-admin-1.0.0-SNAPSHOT.jar" 2>/dev/null || true
sleep 2

JAR="$PMS_ROOT/pms-admin/target/pms-admin-1.0.0-SNAPSHOT.jar"
JAVA_BIN="/root/.local/share/mise/installs/java/17.0.2/bin/java"

# 启动后端 - 使用 nohup + & 完全后台
nohup "$JAVA_BIN" -Xms512m -Xmx1g -XX:+UseG1GC \
  -Djava.security.egd=file:/dev/./urandom \
  -Dspring.autoconfigure.exclude=io.opentelemetry.instrumentation.spring.autoconfigure.OpenTelemetryAutoConfiguration \
  -jar "$JAR" > "$LOG_DIR/backend.log" 2>&1 &

BACKEND_PID=$!
echo "$BACKEND_PID" > "$LOG_DIR/backend.pid"
echo "Backend started PID=$BACKEND_PID"
