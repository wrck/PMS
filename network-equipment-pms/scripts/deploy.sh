#!/bin/bash
# ============================================================================
# deploy.sh — PMS 蓝绿部署脚本
# Task 23.1
#
# 流程：
#   1. 预检查（docker / nginx / 镜像 / 网络）
#   2. 备份当前状态（写入 state 文件 + 镜像 tag 备份）
#   3. 拉取/构建新镜像（可选，若 IMAGE_TAG 已指定则直接拉取）
#   4. 启动新环境容器（不切换流量）
#   5. 等待新环境健康检查通过（最长 5 分钟）
#   6. 切换流量（更新 nginx upstream + reload）
#   7. 等待流量切换完成
#   8. 停止旧环境容器（保留用于回滚）
#   9. 通知部署结果
#
# 蓝绿环境：
#   blue  -> pms-backend-blue  宿主机端口 ${BLUE_PORT:-8081}
#   green -> pms-backend-green 宿主机端口 ${GREEN_PORT:-8082}
#   流量切换通过更新 nginx upstream 配置文件 + nginx -s reload 实现
#
# 用法：
#   ./scripts/deploy.sh                                # 部署最新镜像
#   IMAGE_TAG=v1.2.3 ./scripts/deploy.sh              # 部署指定 tag
#   ROLLBACK_ON_FAILURE=false ./scripts/deploy.sh     # 失败不自动回滚
# ============================================================================
set -euo pipefail

# ---------------------------- 配置 ----------------------------
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# 镜像配置
REGISTRY="${REGISTRY:-ghcr.io}"
IMAGE_REPO="${IMAGE_REPO:-${REGISTRY}/network-equipment-pms/pms-backend}"
IMAGE_TAG="${IMAGE_TAG:-latest}"
BACKEND_IMAGE="${BACKEND_IMAGE:-${IMAGE_REPO}:${IMAGE_TAG}}"

# 蓝绿容器与端口
BLUE_CONTAINER="${BLUE_CONTAINER:-pms-backend-blue}"
GREEN_CONTAINER="${GREEN_CONTAINER:-pms-backend-green}"
BLUE_PORT="${BLUE_PORT:-8081}"
GREEN_PORT="${GREEN_PORT:-8082}"

# 状态文件（记录当前活跃环境 + 上一次活跃环境 + 镜像 tag，用于回滚）
STATE_DIR="${STATE_DIR:-/var/lib/pms}"
STATE_FILE="${STATE_FILE:-${STATE_DIR}/active-env}"

# Nginx upstream 配置文件路径（流量切换通过更新此文件 + reload 实现）
NGINX_UPSTREAM_CONF="${NGINX_UPSTREAM_CONF:-/etc/nginx/conf.d/pms-upstream.conf}"
NGINX_CONTAINER="${NGINX_CONTAINER:-}"  # 若使用容器化 nginx，填写容器名（如 pms-frontend）
# 后端 upstream 名称（nginx.conf 中 proxy_pass http://pms_backend;
# 若现有 nginx.conf 直接 proxy_pass http://backend:8080，则需额外配置，见 DEPLOYMENT.md）
NGINX_UPSTREAM_NAME="${NGINX_UPSTREAM_NAME:-pms_backend}"

# 健康检查
HEALTH_CHECK_URL="${HEALTH_CHECK_URL:-http://localhost:8080/actuator/health}"
HEALTH_CHECK_TIMEOUT="${HEALTH_CHECK_TIMEOUT:-300}"   # 5 分钟
HEALTH_CHECK_INTERVAL="${HEALTH_CHECK_INTERVAL:-5}"
TRAFFIC_SWITCH_WAIT="${TRAFFIC_SWITCH_WAIT:-30}"

# 回滚策略
ROLLBACK_ON_FAILURE="${ROLLBACK_ON_FAILURE:-true}"
HEALTH_CHECK_SCRIPT="${HEALTH_CHECK_SCRIPT:-${SCRIPT_DIR}/health-check.sh}"

# 数据库 / Redis（传给新容器）
SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-prod}"
MYSQL_HOST="${MYSQL_HOST:-mysql}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_DATABASE="${MYSQL_DATABASE:-pms}"
MYSQL_USER="${MYSQL_USER:-pms}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-pmspassword}"
REDIS_HOST="${REDIS_HOST:-redis}"
REDIS_PORT="${REDIS_PORT:-6379}"
REDIS_PASSWORD="${REDIS_PASSWORD:-redispassword}"
APP_ENCRYPT_KEY="${APP_ENCRYPT_KEY:-}"
OTEL_ENDPOINT="${OTEL_ENDPOINT:-http://jaeger:4317}"
JAVA_OPTS="${JAVA_OPTS:--Xms512m -Xmx2g -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/logs/}"

# Docker 网络（与 docker-compose.infra.yml 的 pms-network 一致）
# compose 默认网络名为 <projectname>_pms-network，projectname 默认为目录名
DOCKER_NETWORK="${DOCKER_NETWORK:-}"

# 通知 webhook（可选，留空则仅 stdout）
NOTIFY_WEBHOOK="${NOTIFY_WEBHOOK:-}"

# ---------------------------- 颜色 ----------------------------
if [[ -t 1 ]]; then
    C_INFO=$'\033[36m'
    C_OK=$'\033[32m'
    C_WARN=$'\033[33m'
    C_ERR=$'\033[31m'
    C_RST=$'\033[0m'
else
    C_INFO=""; C_OK=""; C_WARN=""; C_ERR=""; C_RST=""
fi

# ---------------------------- 工具函数 ----------------------------
log()     { echo "${C_INFO}[$(date '+%Y-%m-%d %H:%M:%S')] [INFO]${C_RST} $*"; }
log_ok()  { echo "${C_OK}[$(date '+%Y-%m-%d %H:%M:%S')] [OK]${C_RST} $*"; }
log_warn(){ echo "${C_WARN}[$(date '+%Y-%m-%d %H:%M:%S')] [WARN]${C_RST} $*"; }
log_err() { echo "${C_ERR}[$(date '+%Y-%m-%d %H:%M:%S')] [ERR]${C_RST} $*" >&2; }

die() { log_err "$*"; exit 1; }

# 通知（webhook + stdout）
notify() {
    local level=$1; shift
    local msg="$*"
    if [[ -n "${NOTIFY_WEBHOOK}" ]] && command -v curl > /dev/null 2>&1; then
        curl -fsS --max-time 5 -X POST "${NOTIFY_WEBHOOK}" \
            -H 'Content-Type: application/json' \
            -d "{\"level\":\"${level}\",\"message\":\"${msg}\",\"timestamp\":\"$(date -u +%FT%TZ)\"}" \
            > /dev/null 2>&1 || true
    fi
    case "${level}" in
        error) log_err "${msg}" ;;
        warn)  log_warn "${msg}" ;;
        *)     log "${msg}" ;;
    esac
}

# ---------------------------- 状态管理 ----------------------------
ensure_state_dir() {
    if [[ ! -d "${STATE_DIR}" ]]; then
        sudo mkdir -p "${STATE_DIR}" 2> /dev/null || mkdir -p "${STATE_DIR}"
    fi
}

# 读取当前活跃环境（blue / green / none）
get_active_env() {
    if [[ ! -f "${STATE_FILE}" ]]; then
        echo "none"
        return
    fi
    local active
    active=$(grep '^active=' "${STATE_FILE}" 2>/dev/null | cut -d= -f2 | tr -d '[:space:]')
    echo "${active:-none}"
}

# 读取上一个活跃环境（用于回滚）
get_previous_env() {
    if [[ ! -f "${STATE_FILE}" ]]; then
        echo "none"
        return
    fi
    local prev
    prev=$(grep '^previous=' "${STATE_FILE}" 2>/dev/null | cut -d= -f2 | tr -d '[:space:]')
    echo "${prev:-none}"
}

# 写入状态文件
# $1 = active env, $2 = previous env, $3 = active image tag, $4 = previous image tag
write_state() {
    local active=$1 previous=$2 active_tag=$3 previous_tag=$4
    ensure_state_dir
    local tmp
    tmp=$(mktemp)
    cat > "${tmp}" <<EOF
# PMS 蓝绿部署状态文件（由 deploy.sh 维护，请勿手动编辑）
updated=$(date -u +%FT%TZ)
active=${active}
previous=${previous}
active_image=${active_tag}
previous_image=${previous_tag}
EOF
    sudo mv "${tmp}" "${STATE_FILE}" 2> /dev/null || mv "${tmp}" "${STATE_FILE}"
    log "状态已更新：active=${active} previous=${previous}"
}

# 备份当前状态（部署前调用，用于回滚）
backup_state() {
    if [[ ! -f "${STATE_FILE}" ]]; then
        log "无历史状态文件，跳过备份"
        return
    fi
    local backup="${STATE_FILE}.bak.$(date +%Y%m%d%H%M%S)"
    sudo cp "${STATE_FILE}" "${backup}" 2> /dev/null || cp "${STATE_FILE}" "${backup}"
    log "已备份当前状态至 ${backup}"
}

# ---------------------------- 容器 / 端口映射 ----------------------------
container_for_env() {
    case "$1" in
        blue)  echo "${BLUE_CONTAINER}" ;;
        green) echo "${GREEN_CONTAINER}" ;;
        *) echo "" ;;
    esac
}

port_for_env() {
    case "$1" in
        blue)  echo "${BLUE_PORT}" ;;
        green) echo "${GREEN_PORT}" ;;
        *) echo "" ;;
    esac
}

# 获取 inactive 环境（蓝绿对位）
get_inactive_env() {
    case "$1" in
        blue)  echo "green" ;;
        green) echo "blue" ;;
        none)  echo "blue" ;;  # 首次部署默认进 blue
        *)     echo "blue" ;;
    esac
}

# 容器是否在运行
is_container_running() {
    docker ps --format '{{.Names}}' --filter "status=running" | grep -qx "$1"
}

# 容器是否存在（含已停止）
container_exists() {
    docker ps -a --format '{{.Names}}' | grep -qx "$1"
}

# ---------------------------- Docker 网络 ----------------------------
detect_network() {
    if [[ -n "${DOCKER_NETWORK}" ]]; then
        return
    fi
    # 优先匹配 infra compose 创建的 pms-network
    local net
    net=$(docker network ls --format '{{.Name}}' | grep -E 'pms-network$|_pms-network$' | head -n1 || true)
    if [[ -z "${net}" ]]; then
        # 兜底：尝试默认 project 名
        net="network-equipment-pms_pms-network"
    fi
    DOCKER_NETWORK="${net}"
    log "使用 Docker 网络：${DOCKER_NETWORK}"
}

# ---------------------------- 预检查 ----------------------------
preflight() {
    log "预检查..."

    command -v docker > /dev/null 2>&1 || die "docker 未安装"
    docker info > /dev/null 2>&1 || die "docker daemon 不可达"

    command -v curl > /dev/null 2>&1 || die "curl 未安装"

    # nginx 检查：宿主 nginx 或容器化 nginx 至少有一种
    if [[ ! -f "${NGINX_UPSTREAM_CONF}" ]] && [[ -z "${NGINX_CONTAINER}" ]]; then
        if ! command -v nginx > /dev/null 2>&1; then
            log_warn "未检测到宿主 nginx，且 NGINX_UPSTREAM_CONF 不存在。流量切换将仅切换 state 文件，请手动配置上游。"
        fi
    fi

    # 镜像检查：本地是否存在，不存在则尝试拉取
    if ! docker image inspect "${BACKEND_IMAGE}" > /dev/null 2>&1; then
        log "本地未找到镜像 ${BACKEND_IMAGE}，尝试拉取..."
        if ! docker pull "${BACKEND_IMAGE}"; then
            die "拉取镜像失败：${BACKEND_IMAGE}"
        fi
    fi
    log_ok "镜像就绪：${BACKEND_IMAGE}"

    detect_network

    # health-check 脚本存在性
    if [[ ! -x "${HEALTH_CHECK_SCRIPT}" ]] && [[ ! -f "${HEALTH_CHECK_SCRIPT}" ]]; then
        log_warn "未找到 ${HEALTH_CHECK_SCRIPT}，将仅使用 actuator 端点做健康检查"
    fi

    log_ok "预检查通过"
}

# ---------------------------- 启动新环境容器 ----------------------------
# $1 = 目标环境（blue / green）
start_env() {
    local target_env=$1
    local container port
    container=$(container_for_env "${target_env}")
    port=$(port_for_env "${target_env}")

    log "启动 ${target_env} 环境：容器=${container} 端口=${port} 镜像=${BACKEND_IMAGE}"

    # 若已存在同名容器，先移除（避免冲突）
    if container_exists "${container}"; then
        if is_container_running "${container}"; then
            log_warn "容器 ${container} 已在运行，先停止"
            docker stop "${container}" > /dev/null
        fi
        docker rm "${container}" > /dev/null
    fi

    # 启动新容器
    docker run -d \
        --name "${container}" \
        --network "${DOCKER_NETWORK}" \
        -p "${port}:8080" \
        -e SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE}" \
        -e SPRING_DATASOURCE_URL="jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai" \
        -e SPRING_DATASOURCE_USERNAME="${MYSQL_USER}" \
        -e SPRING_DATASOURCE_PASSWORD="${MYSQL_PASSWORD}" \
        -e SPRING_REDIS_HOST="${REDIS_HOST}" \
        -e SPRING_REDIS_PORT="${REDIS_PORT}" \
        -e SPRING_REDIS_PASSWORD="${REDIS_PASSWORD}" \
        -e APP_SECURITY_ENCRYPT_KEY="${APP_ENCRYPT_KEY}" \
        -e OTEL_EXPORTER_OTLP_ENDPOINT="${OTEL_ENDPOINT}" \
        -e JAVA_OPTS="${JAVA_OPTS}" \
        --health-cmd="curl -fsS http://localhost:8080/actuator/health || exit 1" \
        --health-interval=15s \
        --health-timeout=10s \
        --health-retries=5 \
        --health-start-period=60s \
        --restart=unless-stopped \
        "${BACKEND_IMAGE}" > /dev/null

    log_ok "容器 ${container} 已启动"
}

# ---------------------------- 健康检查 ----------------------------
# 等待指定环境的健康检查通过
# $1 = 目标环境
# $2 = 超时秒数
wait_for_health() {
    local target_env=$1
    local timeout=$2
    local port
    port=$(port_for_env "${target_env}")
    local url="http://localhost:${port}/actuator/health"
    local container
    container=$(container_for_env "${target_env}")

    log "等待 ${target_env} 健康检查通过：${url}（超时 ${timeout}s）"

    local start now elapsed
    start=$(date +%s)

    # 先等容器进入 running + start_period
    while true; do
        now=$(date +%s)
        elapsed=$((now - start))
        if (( elapsed > timeout )); then
            log_err "健康检查超时（${elapsed}s > ${timeout}s）"
            return 1
        fi

        # 容器是否已退出（启动失败）
        if ! is_container_running "${container}"; then
            log_err "容器 ${container} 未在运行，最近 50 行日志："
            docker logs --tail 50 "${container}" 2>&1 | sed 's/^/    /' >&2 || true
            return 1
        fi

        # 检查 Docker 健康状态
        local health
        health=$(docker inspect --format '{{.State.Health.Status}}' "${container}" 2>/dev/null || echo "none")
        if [[ "${health}" == "healthy" ]]; then
            log_ok "容器健康状态：healthy"
            # 双重确认：直接 curl actuator
            if curl -fsS --max-time 10 "${url}" > /dev/null 2>&1; then
                log_ok "actuator 健康检查通过"
                return 0
            fi
        fi

        # 即便 docker healthcheck 未配置，也直接尝试 curl
        if curl -fsS --max-time 10 "${url}" > /dev/null 2>&1; then
            log_ok "actuator 健康检查通过"
            return 0
        fi

        sleep "${HEALTH_CHECK_INTERVAL}"
    done
}

# ---------------------------- 流量切换 ----------------------------
# 生成 nginx upstream 配置内容
# $1 = 目标环境
generate_upstream_conf() {
    local target_env=$1
    local port
    port=$(port_for_env "${target_env}")
    cat <<EOF
# 由 deploy.sh 自动生成，请勿手动编辑
# 活跃环境：${target_env}
# 更新时间：$(date '+%Y-%m-%d %H:%M:%S')
upstream ${NGINX_UPSTREAM_NAME} {
    server 127.0.0.1:${port} max_fails=3 fail_timeout=30s;
    keepalive 32;
}
EOF
}

# 写入 nginx upstream 配置并 reload
# $1 = 目标环境
switch_traffic() {
    local target_env=$1
    local port
    port=$(port_for_env "${target_env}")

    log "切换流量到 ${target_env}（127.0.0.1:${port}）"

    local conf_content
    conf_content=$(generate_upstream_conf "${target_env}")

    if [[ -n "${NGINX_CONTAINER}" ]]; then
        # 容器化 nginx：将配置写入容器并 reload
        log "通过容器 ${NGINX_CONTAINER} 更新 nginx 配置"
        docker exec "${NGINX_CONTAINER}" sh -c "cat > '${NGINX_UPSTREAM_CONF}'" <<< "${conf_content}"
        docker exec "${NGINX_CONTAINER}" nginx -t 2>&1 | sed 's/^/    /'
        docker exec "${NGINX_CONTAINER}" nginx -s reload 2>&1 | sed 's/^/    /' || true
    elif [[ -d "$(dirname "${NGINX_UPSTREAM_CONF}")" ]] || sudo mkdir -p "$(dirname "${NGINX_UPSTREAM_CONF}")" 2> /dev/null; then
        # 宿主 nginx
        local tmp
        tmp=$(mktemp)
        echo "${conf_content}" > "${tmp}"
        sudo mv "${tmp}" "${NGINX_UPSTREAM_CONF}" 2> /dev/null || mv "${tmp}" "${NGINX_UPSTREAM_CONF}"
        if command -v nginx > /dev/null 2>&1; then
            nginx -t 2>&1 | sed 's/^/    /'
            nginx -s reload 2>&1 | sed 's/^/    /' || true
        else
            log_warn "宿主未安装 nginx 二进制，仅写入配置文件 ${NGINX_UPSTREAM_CONF}，请手动 reload"
        fi
    else
        log_warn "无法写入 ${NGINX_UPSTREAM_CONF}（权限/路径不可用），仅切换 state 文件"
    fi

    # 等待流量切换生效（keepalive 连接 drain）
    log "等待 ${TRAFFIC_SWITCH_WAIT}s 让流量切换生效..."
    sleep "${TRAFFIC_SWITCH_WAIT}"

    # 验证活跃端口健康
    if curl -fsS --max-time 10 "http://localhost:${port}/actuator/health" > /dev/null 2>&1; then
        log_ok "新环境 ${target_env} 已接管流量"
    else
        log_warn "新环境 ${target_env} 端口 ${port} 健康检查未通过，请立即检查"
    fi
}

# ---------------------------- 停止旧环境 ----------------------------
# $1 = 旧环境
stop_env() {
    local old_env=$1
    if [[ "${old_env}" == "none" ]]; then
        log "无旧环境需要停止"
        return
    fi
    local container
    container=$(container_for_env "${old_env}")
    log "停止旧环境 ${old_env}（容器 ${container}，保留用于回滚）"
    if is_container_running "${container}"; then
        docker stop -t 30 "${container}" > /dev/null
    fi
    # 不删除容器，保留用于回滚（rollback.sh 会启动它）
    log_ok "旧环境 ${old_env} 已停止（容器保留）"
}

# ---------------------------- 回滚 ----------------------------
rollback() {
    log_warn "触发自动回滚..."
    if [[ -x "${SCRIPT_DIR}/rollback.sh" ]]; then
        "${SCRIPT_DIR}/rollback.sh" --no-stop-current || true
    else
        log_err "rollback.sh 不存在或不可执行，无法自动回滚"
    fi
}

# ---------------------------- 主流程 ----------------------------
main() {
    log "==================== PMS 蓝绿部署开始 ===================="
    log "镜像：${BACKEND_IMAGE}"
    log "时间：$(date '+%Y-%m-%d %H:%M:%S')"

    # 1. 预检查
    preflight

    # 2. 备份状态
    backup_state

    # 3. 确定当前/目标环境
    local active_env new_env
    active_env=$(get_active_env)
    new_env=$(get_inactive_env "${active_env}")
    log "当前活跃：${active_env}，部署目标：${new_env}"

    # 4. 启动新环境
    start_env "${new_env}"

    # 5. 健康检查
    if ! wait_for_health "${new_env}" "${HEALTH_CHECK_TIMEOUT}"; then
        log_err "新环境 ${new_env} 健康检查失败"
        notify error "PMS 部署失败：${new_env} 健康检查未通过（镜像 ${BACKEND_IMAGE}）"
        # 清理失败的新容器
        local new_container
        new_container=$(container_for_env "${new_env}")
        docker stop "${new_container}" > /dev/null 2>&1 || true
        docker rm "${new_container}" > /dev/null 2>&1 || true
        if [[ "${ROLLBACK_ON_FAILURE}" == "true" ]] && [[ "${active_env}" != "none" ]]; then
            rollback
        fi
        exit 1
    fi

    # 6. 切换流量
    switch_traffic "${new_env}"

    # 7. 综合健康检查（可选，使用 health-check.sh）
    if [[ -x "${HEALTH_CHECK_SCRIPT}" ]]; then
        log "执行综合健康检查..."
        local prev_active="${active_env}"
        # 临时把 HEALTH_CHECK_URL 指向新环境端口
        local new_port
        new_port=$(port_for_env "${new_env}")
        if HEALTH_CHECK_URL="http://localhost:${new_port}/actuator/health" \
           API_BASE_URL="http://localhost:${new_port}" \
           "${HEALTH_CHECK_SCRIPT}"; then
            log_ok "综合健康检查通过"
        else
            log_warn "综合健康检查存在告警项，但新环境 actuator 已通过，继续部署"
        fi
    fi

    # 8. 停止旧环境（保留容器用于回滚）
    stop_env "${active_env}"

    # 9. 更新状态
    write_state "${new_env}" "${active_env}" "${BACKEND_IMAGE}" ""

    log_ok "==================== 部署完成 ===================="
    log_ok "活跃环境：${new_env}"
    log_ok "镜像：${BACKEND_IMAGE}"
    log "如需回滚：./scripts/rollback.sh"
    notify info "PMS 部署成功：${new_env} 环境，镜像 ${BACKEND_IMAGE}"
}

main "$@"
