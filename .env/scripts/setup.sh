#!/bin/bash
# =============================================================
# setup.sh — 沙盒环境初始化脚本
# 每次沙盒重置后运行一次，安装 MySQL/Redis 并配置数据库
# 用法: bash /workspace/.env/scripts/setup.sh
# =============================================================
set -e

echo "=== [1/5] 安装 MySQL + Redis（如未安装）==="
if ! command -v mysqld &>/dev/null; then
  DEBIAN_FRONTEND=noninteractive apt-get update -qq
  DEBIAN_FRONTEND=noninteractive apt-get install -y -qq mysql-server redis-server
  echo "MySQL + Redis 安装完成"
else
  echo "MySQL + Redis 已安装，跳过"
fi

echo "=== [2/5] 启动 MySQL ==="
mkdir -p /var/run/mysqld
chown mysql:mysql /var/run/mysqld
if ! mysqladmin -uroot ping 2>/dev/null; then
  mysqld --user=mysql --daemonize
  sleep 3
fi
mysqladmin -uroot ping 2>&1

echo "=== [3/5] 创建数据库 pms 和用户 ==="
mysql -uroot <<'SQL'
CREATE DATABASE IF NOT EXISTS pms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'pms'@'%' IDENTIFIED BY 'pmspassword';
CREATE USER IF NOT EXISTS 'pms'@'localhost' IDENTIFIED BY 'pmspassword';
GRANT ALL PRIVILEGES ON pms.* TO 'pms'@'%';
GRANT ALL PRIVILEGES ON pms.* TO 'pms'@'localhost';
FLUSH PRIVILEGES;
SQL
echo "数据库 pms 和用户 pms/pmspassword 就绪"

echo "=== [4/5] 启动 Redis ==="
if ! redis-cli ping 2>/dev/null | grep -q PONG; then
  redis-server --daemonize yes --port 6379
  sleep 1
fi
redis-cli ping

echo "=== [5/5] 验证密钥文件 ==="
if [ ! -f /workspace/.env/secrets/jwt-secret.txt ]; then
  echo "⚠ JWT 密钥缺失，重新生成"
  mkdir -p /workspace/.env/secrets
  openssl rand -base64 32 > /workspace/.env/secrets/jwt-secret.txt
  openssl rand -base64 32 > /workspace/.env/secrets/app-encrypt-key.txt
fi
echo "JWT_SECRET: $(cat /workspace/.env/secrets/jwt-secret.txt)"

echo ""
echo "============================================"
echo "✅ 环境初始化完成"
echo "============================================"
echo "MySQL: 127.0.0.1:3306  库=pms  用户=pms/pmspassword"
echo "Redis: 127.0.0.1:6379"
echo ""
echo "下一步: bash /workspace/.env/scripts/start.sh"
