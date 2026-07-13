#!/bin/bash
# =============================================================
# start.sh — 启动所有服务（后端 + 前端）
# 前提: 已运行过 setup.sh
# 用法: bash /workspace/.env/scripts/start.sh
# =============================================================
set -e

PMS_ROOT="/workspace/network-equipment-pms"
LOG_DIR="/workspace/.env/logs"
mkdir -p "$LOG_DIR"

# 加载持久化密钥
export JWT_SECRET=$(cat /workspace/.env/secrets/jwt-secret.txt)
export APP_ENCRYPT_KEY=$(cat /workspace/.env/secrets/app-encrypt-key.txt)

# 数据源配置
export SPRING_DATASOURCE_URL='jdbc:mysql://127.0.0.1:3306/pms?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true'
export MYSQL_USER='pms'
export MYSQL_PASSWORD='pmspassword'

# 加密密钥（连接器/低代码模块用）
export APP_CONNECTOR_ENCRYPT_KEY='test-connector-key-32b-padding!'
export LOWCODE_ENCRYPTION_KEY='test-lowcode-encryption-key-32b!'

# 禁用 OpenTelemetry（版本冲突）
export OTEL_SDK_DISABLED='true'

echo "=== [1/4] 确认 MySQL/Redis 运行中 ==="
if ! mysqladmin -uroot ping 2>/dev/null; then
  echo "⚠ MySQL 未运行，请先执行 setup.sh"
  exit 1
fi
if ! redis-cli ping 2>/dev/null | grep -q PONG; then
  echo "⚠ Redis 未运行，请先执行 setup.sh"
  exit 1
fi
echo "MySQL/Redis 正常"

echo "=== [2/4] 启动后端（8080）==="
# 停止旧进程
pkill -f "pms-admin-1.0.0-SNAPSHOT.jar" 2>/dev/null || true
sleep 1

JAR="$PMS_ROOT/pms-admin/target/pms-admin-1.0.0-SNAPSHOT.jar"
if [ ! -f "$JAR" ]; then
  echo "⚠ 后端 jar 不存在: $JAR"
  echo "请先编译: cd $PMS_ROOT && mvn -pl pms-admin -am -DskipTests package"
  exit 1
fi

# 查找 JDK 17
if [ -x "/root/.local/share/mise/installs/java/17.0.2/bin/java" ]; then
  JAVA_BIN="/root/.local/share/mise/installs/java/17.0.2/bin/java"
elif command -v java &>/dev/null && java -version 2>&1 | grep -q 'version "17'; then
  JAVA_BIN="java"
else
  echo "⚠ 未找到 JDK 17，尝试 mise install java@17"
  mise install java@17 2>/dev/null || true
  JAVA_BIN=$(mise which java 2>/dev/null || echo "java")
fi

nohup "$JAVA_BIN" -Xms512m -Xmx1g -XX:+UseG1GC \
  -Djava.security.egd=file:/dev/./urandom \
  -Dspring.autoconfigure.exclude=io.opentelemetry.instrumentation.spring.autoconfigure.OpenTelemetryAutoConfiguration \
  -jar "$JAR" \
  > "$LOG_DIR/backend.log" 2>&1 &
BACKEND_PID=$!
echo "后端启动中 PID=$BACKEND_PID（日志: $LOG_DIR/backend.log）"

# 等待后端就绪（最多 90 秒）
echo -n "等待后端健康检查"
for i in $(seq 1 45); do
  if curl -sS -o /dev/null -w "%{http_code}" http://127.0.0.1:8080/actuator/health 2>/dev/null | grep -q 200; then
    echo " ✅ (${i}*2s)"
    break
  fi
  echo -n "."
  sleep 2
done
HEALTH=$(curl -sS -o /dev/null -w "%{http_code}" http://127.0.0.1:8080/actuator/health 2>/dev/null)
if [ "$HEALTH" != "200" ]; then
  echo " ❌ 后端未就绪 (health=$HEALTH)"
  echo "查看日志: tail -50 $LOG_DIR/backend.log"
  exit 1
fi

echo "=== [3/4] 启动前端（3000）==="
# 停止旧进程
pkill -f "vite" 2>/dev/null || true
sleep 1

cd "$PMS_ROOT/pms-frontend"
if [ ! -d node_modules ]; then
  echo "⚠ node_modules 不存在，正在安装..."
  npm install --legacy-peer-deps 2>&1 | tail -5
fi

nohup npm exec vite -- --host 0.0.0.0 > "$LOG_DIR/frontend.log" 2>&1 &
FRONTEND_PID=$!
echo "前端启动中 PID=$FRONTEND_PID（日志: $LOG_DIR/frontend.log）"

# 等待前端就绪
echo -n "等待前端就绪"
for i in $(seq 1 15); do
  if curl -sS -o /dev/null -w "%{http_code}" http://127.0.0.1:3000/ 2>/dev/null | grep -q 200; then
    echo " ✅ (${i}*2s)"
    break
  fi
  echo -n "."
  sleep 2
done

echo "=== [4/4] 服务状态 ==="
echo ""
echo "============================================"
echo "✅ 全部服务已启动"
echo "============================================"
echo "后端:  http://127.0.0.1:8080  (PID=$BACKEND_PID)"
echo "前端:  http://127.0.0.1:3000  (PID=$FRONTEND_PID)"
echo "MySQL: 127.0.0.1:3306  库=pms"
echo "Redis: 127.0.0.1:6379"
echo ""
echo "登录: admin / admin123"
echo "日志: $LOG_DIR/{backend,frontend}.log"
echo ""
echo "停止服务: bash /workspace/.env/scripts/stop.sh"
