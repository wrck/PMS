#!/usr/bin/env bash
# =============================================================================
# deploy.sh — PMS 容器化部署辅助脚本
#
# 子命令：
#   build     构建 PMS 应用镜像
#   up        构建并后台启动全部服务（db + app）
#   down      停止并移除容器与网络（保留数据卷）
#   logs      跟踪查看服务日志（Ctrl+C 退出）
#   restart   重启全部服务
#
# 使用前请确保已安装 Docker 与 Docker Compose，并已执行 chmod +x scripts/deploy.sh
# =============================================================================
set -euo pipefail

# 切换到脚本所在目录的上一级（项目根），保证从任意路径调用均可正常工作
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
cd "${PROJECT_DIR}"

# 兼容 docker-compose v1 与 docker compose v2
if command -v docker-compose >/dev/null 2>&1; then
  DC="docker-compose"
elif docker compose version >/dev/null 2>&1; then
  DC="docker compose"
else
  echo "错误：未检测到 docker-compose / docker compose，请先安装 Docker Compose。" >&2
  exit 1
fi

usage() {
  cat <<EOF
用法: $0 <命令>

命令:
  build       构建 PMS 应用镜像（docker-compose build app）
  up          构建并后台启动全部服务（db + app）
  down        停止并移除容器、网络（保留数据卷）
  logs        跟踪查看所有服务日志（Ctrl+C 退出）
  restart     重启全部服务（app 与 db）

环境变量（可选，覆盖默认值）:
  APP_PORT              应用对外端口（默认 8080）
  MYSQL_PORT            数据库对外端口（默认 3306）
  MYSQL_ROOT_PASSWORD   root 密码（默认 root123）
  MYSQL_DATABASE        数据库名（默认 dppms_d365）
  MYSQL_USER/MYSQL_PASSWORD  应用账号（默认 pms/pms123）
  BUILD_PROFILE         产品线 profile（默认 pms2；可选 pms3）
  WAR_NAME              WAR 产物名（默认 PMS2.war；pms3 为 AFPMS3.war）
  ENV_PROFILE           环境 profile（默认 dev；可选 test/release）
  JAVA_OPTS             JVM 参数（默认 -Xms512m -Xmx2048m）

示例:
  $0 build
  $0 up
  $0 logs
  $0 down
  BUILD_PROFILE=pms3 WAR_NAME=AFPMS3.war $0 up
  APP_PORT=9090 $0 up
EOF
}

case "${1:-}" in
  build)
    echo "==> 构建 PMS 应用镜像..."
    $DC build app
    echo "==> 构建完成。"
    ;;

  up)
    echo "==> 启动服务（db + app）..."
    $DC up -d
    echo "==> 服务已启动。"
    echo "    应用地址: http://localhost:${APP_PORT:-8080}/"
    echo "    数据库地址: localhost:${MYSQL_PORT:-3306}/${MYSQL_DATABASE:-dppms_d365}"
    echo
    $DC ps
    ;;

  down)
    echo "==> 停止并移除服务（保留数据卷）..."
    $DC down
    echo "==> 已停止。如需删除数据卷: docker volume rm pms-mysql-data"
    ;;

  logs)
    echo "==> 跟踪日志（Ctrl+C 退出）..."
    $DC logs -f
    ;;

  restart)
    echo "==> 重启全部服务..."
    $DC restart
    echo
    $DC ps
    ;;

  "" | -h | --help | help)
    usage
    ;;

  *)
    echo "错误：未知命令 '$1'" >&2
    echo
    usage
    exit 1
    ;;
esac
