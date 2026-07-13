#!/bin/bash
# =============================================================
# stop.sh — 停止所有服务
# 用法: bash /workspace/.env/scripts/stop.sh
# =============================================================
echo "停止服务..."

# 停止后端
pkill -f "pms-admin-1.0.0-SNAPSHOT.jar" 2>/dev/null && echo "✅ 后端已停止" || echo "ℹ 后端未运行"

# 停止前端
pkill -f "vite" 2>/dev/null && echo "✅ 前端已停止" || echo "ℹ 前端未运行"

# 停止 MySQL（可选，通常保持运行）
# pkill -f "mysqld" 2>/dev/null && echo "✅ MySQL 已停止" || echo "ℹ MySQL 未运行"

# 停止 Redis（可选，通常保持运行）
# redis-cli shutdown 2>/dev/null && echo "✅ Redis 已停止" || echo "ℹ Redis 未运行"

echo "完成"
