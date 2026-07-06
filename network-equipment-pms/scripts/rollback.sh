#!/bin/bash
# ============================================================================
# rollback.sh — PMS 蓝绿回滚脚本
# Task 23.2
#
# 回滚策略：
#   1. 默认回滚到上一次活跃环境（蓝绿互换，秒级回滚，零额外构建）
#      要求 deploy.sh 停止旧环境时保留容器（默认行为）
#   2. 指定镜像 tag 回滚：rollback.sh <image_tag>
#      会拉取指定镜像并以 inactive 环境重新部署
#
# 流程：
#   1. 读取 state 文件，确定当前活跃环境与上一次环境
#   2. 启动 inactive 环境（旧容器）
#   3. 健康检查
#   4. 切换流量回 inactive 环境
#   5. 停止当前活跃环境（除非 --keep-current）
#
# 用法：
#   ./scripts/rollback.sh                    # 回滚到上一次活跃环境
#   ./scripts/rollback.sh v1.0.5             # 回滚到指定镜像 tag
#   ./scripts/rollback.sh --keep-current     # 回滚后保留当前容器（调试用）
#   ./scripts/rollback.sh --no-stop-current  # 仅切换流量，不停止当前（deploy.sh 失败时调用）
#   ./scripts/rollback.sh --dry-run          # 演练，仅打印动作
# ============================================================================
set -euo pipefail

# ---------------------------- 配置 ----------------------------
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

REGISTRY="${REGISTRY:-ghcr.io}"
IMAGE_REPO="${IMAGE_REPO:-${REGISTRY}/network-equipment-pms/pms-backend}"

BLUE_CONTAINER="${BLUE_CONTAINER:-pms-backend-blue}"
GREEN_CONTAINER="${GREEN_CONTAINER:-pms-backend-green}"
BLUE_PORT="${BLUE_PORT:-8081}"
GREEN_PORT="${GREEN_PORT:-8082}"

STATE_DIR="${STATE_DIR:-/var/lib/pms}"
STATE_FILE="${STATE_FILE:-${STATE_DIR}/active-env}"

NGINX_UPSTREAM_CONF="${NGINX_UPSTREAM_CONF:-/etc/nginx/conf.d/pms-upstream.conf}"
NGINX_CONTAINER="${NGINX_CONTAINER:-}"
NGINX_UPSTREAM_NAME="${NGINX_UPSTREAM_NAME:-pms_backend}"

HEALTH_CHECK_TIMEOUT="${HEALTH_CHECK_TIMEOUT:-300}"
HEALTH_CHECK_INTERVAL="${HEALTH_CHECK_INTERVAL:-5}"
TRAFFIC_SWITCH_WAIT="${TRAFFIC_SWITCH_WAIT:-30}"
HEALTH_CHECK_SCRIPT="${HEALTH_CHECK_SCRIPT:-${SCRIPT_DIR}/health-check.sh}"

# 数据库 / Redis（仅当回滚到指定镜像 tag 需要重新创建容器时使用）
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
DOCKER_NETWORK="${DOCKER_NETWORK:-}"

NOTIFY_WEBHOOK="${NOTIFY_WEBHOOK:-}"

# ---------------------------- 颜色 ----------------------------
if [[ -t 1 ]]; then
    C_INFO=$'\033[36m'; C_OK=$'\033[32m'; C_WARN=$'\033[33m'; C_ERR=$'\033[31m'; C_RST=$'\033[0m'
else
    C_INFO=""; C_OK=""; C_WARN=""; C_ERR=""; C_RST=""
fi

# ---------------------------- 工具函数（与 deploy.sh 一致） ----------------------------
log()     { echo "${C_INFO}[$(date '+%Y-%m-%d %H:%M:%S')] [INFO]${C_RST} $*"; }
log_ok()  { echo "${C_OK}[$(date '+%Y-%m-%d %H:%M:%S')] [OK]${C_RST} $*"; }
log_warn(){ echo "${C_WARN}[$(date '+%Y-%m-%d %H:%M:%S')] [WARN]${C_RST} $*"; }
log_err() { echo "${C_ERR}[$(date '+%Y-%m-%d %H:%M:%S')] [ERR]${C_RST} $*" >&2; }
die() { log_err "$*"; exit 1; }

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

get_inactive_env() {
    case "$1" in
        blue)  echo "green" ;;
        green) echo "blue" ;;
        none)  echo "blue" ;;
        *)     echo "blue" ;;
    esac
}

is_container_running() {
    docker ps --format '{{.Names}}' --filter "status=running" | grep -qx "$1"
}

container_exists() {
    docker ps -a --format '{{.Names}}' | grep -qx "$1"
}

read_state_field() {
    local field=$1
    if [[ ! -f "${STATE_FILE}" ]]; then
        echo "none"
        return
    fi
    local val
    val=$(grep "^${field}=" "${STATE_FILE}" 2>/dev/null | cut -d= -f2- | tr -d '[:space:]')
    echo "${val:-none}"
}

write_state() {
    local active=$1 previous=$2 active_tag=$3 previous_tag=$4
    if [[ ! -d "${STATE_DIR}" ]]; then
        sudo mkdir -p "${STATE_DIR}" 2> /dev/null || mkdir -p "${STATE_DIR}"
    fi
    local tmp
    tmp=$(mktemp)
    cat > "${tmp}" <<EOF
# PMS 蓝绿部署状态文件（由 rollback.sh 维护，请勿手动编辑）
updated=$(date -u +%FT%TZ)
active=${active}
previous=${previous}
active_image=${active_tag}
previous_image=${previous_tag}
EOF
    sudo mv "${tmp}" "${STATE_FILE}" 2> /dev/null || mv "${tmp}" "${STATE_FILE}"
    log "状态已更新：active=${active} previous=${previous}"
}

detect_network() {
    if [[ -n "${DOCKER_NETWORK}" ]]; then return; fi
    local net
    net=$(docker network ls --format '{{.Name}}' | grep -E 'pms-network$|_pms-network$' | head -n1 || true)
    if [[ -z "${net}" ]]; then
        net="network-equipment-pms_pms-network"
    fi
    DOCKER_NETWORK="${net}"
}

# 启动容器（与 deploy.sh start_env 等价，用于回滚到指定镜像 tag 时）
# $1 = 目标环境, $2 = 镜像
start_env_with_image() {
    local target_env=$1 image=$2
    local container port
    container=$(container_for_env "${target_env}")
    port=$(port_for_env "${target_env}")

    log "启动 ${target_env} 环境：容器=${container} 端口=${port} 镜像=${image}"

    if container_exists "${container}"; then
        if is_container_running "${container}"; then
            docker stop "${container}" > /dev/null
        fi
        docker rm "${container}" > /dev/null
    fi

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
        "${image}" > /dev/null

    log_ok "容器 ${container} 已启动"
}

# ---------------------------- 健康检查 ----------------------------
wait_for_health() {
    local target_env=$1 timeout=$2
    local port url container
    port=$(port_for_env "${target_env}")
    url="http://localhost:${port}/actuator/health"
    container=$(container_for_env "${target_env}")

    log "等待 ${target_env} 健康检查通过：${url}（超时 ${timeout}s）"
    local start now elapsed
    start=$(date +%s)

    while true; do
        now=$(date +%s)
        elapsed=$((now - start))
        if (( elapsed > timeout )); then
            log_err "健康检查超时（${elapsed}s > ${timeout}s）"
            return 1
        fi
        if ! is_container_running "${container}"; then
            log_err "容器 ${container} 未在运行，最近 50 行日志："
            docker logs --tail 50 "${container}" 2>&1 | sed 's/^/    /' >&2 || true
            return 1
        fi
        if curl -fsS --max-time 10 "${url}" > /dev/null 2>&1; then
            log_ok "actuator 健康检查通过"
            return 0
        fi
        sleep "${HEALTH_CHECK_INTERVAL}"
    done
}

# ---------------------------- 流量切换 ----------------------------
generate_upstream_conf() {
    local target_env=$1 port
    port=$(port_for_env "${target_env}")
    cat <<EOF
# 由 rollback.sh 自动生成，请勿手动编辑
# 活跃环境：${target_env}
# 更新时间：$(date '+%Y-%m-%d %H:%M:%S')
upstream ${NGINX_UPSTREAM_NAME} {
    server 127.0.0.1:${port} max_fails=3 fail_timeout=30s;
    keepalive 32;
}
EOF
}

switch_traffic() {
    local target_env=$1
    local port
    port=$(port_for_env "${target_env}")

    if [[ "${DRY_RUN}" == "true" ]]; then
        log "[DRY-RUN] 将切换流量到 ${target_env}（127.0.0.1:${port}）"
        return
    fi

    log "切换流量到 ${target_env}（127.0.0.1:${port}）"
    local conf_content
    conf_content=$(generate_upstream_conf "${target_env}")

    if [[ -n "${NGINX_CONTAINER}" ]]; then
        docker exec "${NGINX_CONTAINER}" sh -c "cat > '${NGINX_UPSTREAM_CONF}'" <<< "${conf_content}"
        docker exec "${NGINX_CONTAINER}" nginx -t 2>&1 | sed 's/^/    /'
        docker exec "${NGINX_CONTAINER}" nginx -s reload 2>&1 | sed 's/^/    /' || true
    elif [[ -d "$(dirname "${NGINX_UPSTREAM_CONF}")" ]] || sudo mkdir -p "$(dirname "${NGINX_UPSTREAM_CONF}")" 2> /dev/null; then
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
        log_warn "无法写入 ${NGINX_UPSTREAM_CONF}，仅切换 state 文件"
    fi

    log "等待 ${TRAFFIC_SWITCH_WAIT}s 让流量切换生效..."
    sleep "${TRAFFIC_SWITCH_WAIT}"

    if curl -fsS --max-time 10 "http://localhost:${port}/actuator/health" > /dev/null 2>&1; then
        log_ok "目标环境 ${target_env} 已接管流量"
    else
        log_warn "目标环境 ${target_env} 端口 ${port} 健康检查未通过，请立即检查"
    fi
}

# ---------------------------- 参数解析 ----------------------------
KEEP_CURRENT=false
NO_STOP_CURRENT=false
DRY_RUN=false
ROLLBACK_IMAGE_TAG=""

while [[ $# -gt 0 ]]; do
    case "$1" in
        --keep-current)    KEEP_CURRENT=true; shift ;;
        --no-stop-current) NO_STOP_CURRENT=true; shift ;;
        --dry-run)         DRY_RUN=true; shift ;;
        -h|--help)
            sed -n '2,30p' "${BASH_SOURCE[0]}"
            exit 0
            ;;
        *)
            ROLLBACK_IMAGE_TAG="$1"
            shift
            ;;
    esac
done

# ---------------------------- 主流程 ----------------------------
main() {
    log "==================== PMS 回滚开始 ===================="

    command -v docker > /dev/null 2>&1 || die "docker 未安装"
    docker info > /dev/null 2>&1 || die "docker daemon 不可达"
    command -v curl > /dev/null 2>&1 || die "curl 未安装"
    detect_network

    local active_env previous_env target_env
    active_env=$(read_state_field active)
    previous_env=$(read_state_field previous)
    target_env=$(get_inactive_env "${active_env}")

    log "当前活跃：${active_env}"
    log "上一次活跃：${previous_env}"
    log "回滚目标：${target_env}"

    if [[ "${active_env}" == "none" ]]; then
        die "无活跃环境记录（state 文件缺失或为 none），无法回滚。请使用 deploy.sh 重新部署。"
    fi

    if [[ "${target_env}" == "${active_env}" ]]; then
        die "回滚目标与当前活跃环境相同（${active_env}），无意义回滚。"
    fi

    local target_container
    target_container=$(container_for_env "${target_env}")

    # ----- 决定回滚镜像 -----
    local rollback_image=""
    if [[ -n "${ROLLBACK_IMAGE_TAG}" ]]; then
        # 指定镜像 tag 回滚
        rollback_image="${IMAGE_REPO}:${ROLLBACK_IMAGE_TAG}"
        log "回滚到指定镜像：${rollback_image}"
        if ! docker image inspect "${rollback_image}" > /dev/null 2>&1; then
            log "本地未找到镜像 ${rollback_image}，尝试拉取..."
            if ! docker pull "${rollback_image}"; then
                die "拉取镜像失败：${rollback_image}"
            fi
        fi
    else
        # 蓝绿互换：使用已停止的旧容器
        if ! container_exists "${target_container}"; then
            # 旧容器不存在，尝试从 state 文件读取 previous_image 重新拉起
            local prev_image
            prev_image=$(read_state_field previous_image)
            if [[ "${prev_image}" != "none" && -n "${prev_image}" ]]; then
                log "旧容器 ${target_container} 不存在，使用 previous_image=${prev_image} 重新创建"
                rollback_image="${prev_image}"
                if ! docker image inspect "${rollback_image}" > /dev/null 2>&1; then
                    log "本地未找到镜像 ${rollback_image}，尝试拉取..."
                    docker pull "${rollback_image}" || die "拉取镜像失败：${rollback_image}"
                fi
            else
                die "旧容器 ${target_container} 不存在且 state 文件未记录 previous_image，无法回滚。请指定镜像 tag：rollback.sh <tag>"
            fi
        fi
    fi

    # ----- 启动目标环境 -----
    if [[ -n "${rollback_image}" ]]; then
        if [[ "${DRY_RUN}" == "true" ]]; then
            log "[DRY-RUN] 将启动 ${target_env} 环境，镜像 ${rollback_image}"
        else
            start_env_with_image "${target_env}" "${rollback_image}"
        fi
    else
        # 蓝绿互换：启动已停止的旧容器
        if [[ "${DRY_RUN}" == "true" ]]; then
            log "[DRY-RUN] 将启动已停止的容器 ${target_container}"
        else
            log "启动已停止的容器 ${target_container}"
            docker start "${target_container}" > /dev/null
            log_ok "容器 ${target_container} 已启动"
        fi
    fi

    # ----- 健康检查 -----
    if [[ "${DRY_RUN}" == "true" ]]; then
        log "[DRY-RUN] 跳过健康检查（演练模式）"
    else
        if ! wait_for_health "${target_env}" "${HEALTH_CHECK_TIMEOUT}"; then
            log_err "回滚目标 ${target_env} 健康检查失败"
            notify error "PMS 回滚失败：${target_env} 健康检查未通过"
            # 回滚失败是严重事故：保留当前活跃环境不动
            log_err "当前活跃环境 ${active_env} 未受影响，仍在线服务"
            exit 1
        fi
    fi

    # ----- 切换流量 -----
    switch_traffic "${target_env}"

    # ----- 综合健康检查 -----
    if [[ "${DRY_RUN}" != "true" ]] && [[ -x "${HEALTH_CHECK_SCRIPT}" ]]; then
        local target_port
        target_port=$(port_for_env "${target_env}")
        log "执行综合健康检查..."
        HEALTH_CHECK_URL="http://localhost:${target_port}/actuator/health" \
        API_BASE_URL="http://localhost:${target_port}" \
        "${HEALTH_CHECK_SCRIPT}" || log_warn "综合健康检查存在告警项"
    fi

    # ----- 停止当前活跃环境 -----
    if [[ "${NO_STOP_CURRENT}" == "true" ]]; then
        log "NO_STOP_CURRENT=true，保留 ${active_env} 容器运行（不停止）"
    elif [[ "${KEEP_CURRENT}" == "true" ]]; then
        log "KEEP_CURRENT=true，保留 ${active_env} 容器运行（不停止）"
    else
        if [[ "${DRY_RUN}" == "true" ]]; then
            log "[DRY-RUN] 将停止 ${active_env} 环境"
        else
            local active_container
            active_container=$(container_for_env "${active_env}")
            log "停止原活跃环境 ${active_env}（容器 ${active_container}，保留用于再次回滚）"
            if is_container_running "${active_container}"; then
                docker stop -t 30 "${active_container}" > /dev/null
            fi
            log_ok "原活跃环境 ${active_env} 已停止（容器保留）"
        fi
    fi

    # ----- 更新状态 -----
    if [[ "${DRY_RUN}" != "true" ]]; then
        local active_image_tag
        if [[ -n "${rollback_image}" ]]; then
            active_image_tag="${rollback_image}"
        else
            active_image_tag=$(read_state_field previous_image)
            [[ "${active_image_tag}" == "none" ]] && active_image_tag=""
        fi
        write_state "${target_env}" "${active_env}" "${active_image_tag}" ""
    fi

    log_ok "==================== 回滚完成 ===================="
    log_ok "活跃环境：${target_env}"
    if [[ -n "${rollback_image}" ]]; then
        log_ok "镜像：${rollback_image}"
    fi
    notify warn "PMS 已回滚：${active_env} -> ${target_env}"
}

main
