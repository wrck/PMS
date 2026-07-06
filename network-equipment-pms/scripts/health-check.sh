#!/bin/bash
# ============================================================================
# health-check.sh — PMS 综合健康检查
# Task 23.3
#
# 检查项：
#   1. Actuator health 端点
#   2. 业务接口（登录验证码、项目列表）
#   3. MySQL 连接 + 关键表行数
#   4. Redis 连接
#   5. 磁盘空间
#   6. 内存使用
#
# 用法：
#   ./scripts/health-check.sh
#   HEALTH_CHECK_URL=http://10.0.0.1:8081/actuator/health ./scripts/health-check.sh
#
# 退出码：
#   0 — 全部检查通过
#   1 — 至少一项检查失败
# ============================================================================
set -euo pipefail

# ---------------------------- 配置（支持环境变量覆盖） ----------------------------
HEALTH_CHECK_URL="${HEALTH_CHECK_URL:-http://localhost:8080/actuator/health}"
API_BASE_URL="${API_BASE_URL:-http://localhost:8080}"

MYSQL_HOST="${MYSQL_HOST:-127.0.0.1}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_USER="${MYSQL_USER:-pms}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-pmspassword}"
MYSQL_DATABASE="${MYSQL_DATABASE:-pms}"

REDIS_HOST="${REDIS_HOST:-127.0.0.1}"
REDIS_PORT="${REDIS_PORT:-6379}"
REDIS_PASSWORD="${REDIS_PASSWORD:-redispassword}"

# 磁盘使用率告警阈值（%）
DISK_THRESHOLD="${DISK_THRESHOLD:-90}"
# 内存使用率告警阈值（%）
MEM_THRESHOLD="${MEM_THRESHOLD:-90}"

# 关键业务表（行数为 0 不算失败，仅展示）
KEY_TABLES=("pms_project" "pms_asset" "sys_user")

# ---------------------------- 颜色与计数器 ----------------------------
if [[ -t 1 ]]; then
    COLOR_OK=$'\033[32m'
    COLOR_FAIL=$'\033[31m'
    COLOR_WARN=$'\033[33m'
    COLOR_RESET=$'\033[0m'
else
    COLOR_OK=""
    COLOR_FAIL=""
    COLOR_WARN=""
    COLOR_RESET=""
fi

PASS_COUNT=0
FAIL_COUNT=0

# ---------------------------- 工具函数 ----------------------------
log() { echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*"; }

check() {
    local name=$1
    local cmd=$2
    if eval "${cmd}" > /dev/null 2>&1; then
        echo "${COLOR_OK}[OK]${COLOR_RESET}   ${name}"
        PASS_COUNT=$((PASS_COUNT + 1))
        return 0
    else
        echo "${COLOR_FAIL}[FAIL]${COLOR_RESET} ${name}"
        FAIL_COUNT=$((FAIL_COUNT + 1))
        return 1
    fi
}

# mysql 客户端可用性检测
have_mysql() { command -v mysql > /dev/null 2>&1; }
have_redis() { command -v redis-cli > /dev/null 2>&1; }
have_curl()  { command -v curl > /dev/null 2>&1; }

# 构造 mysql 命令（避免密码出现在 ps 输出，使用 MYSQL_PWD）
mysql_cmd() {
    MYSQL_PWD="${MYSQL_PASSWORD}" mysql -h "${MYSQL_HOST}" -P "${MYSQL_PORT}" -u "${MYSQL_USER}" "$@"
}

# 构造 redis-cli 命令
redis_cmd() {
    if [[ -n "${REDIS_PASSWORD}" ]]; then
        redis-cli -h "${REDIS_HOST}" -p "${REDIS_PORT}" -a "${REDIS_PASSWORD}" --no-auth-warning "$@"
    else
        redis-cli -h "${REDIS_HOST}" -p "${REDIS_PORT}" "$@"
    fi
}

# ---------------------------- 执行检查 ----------------------------
echo "============================================================"
echo " PMS 健康检查  $(date '+%Y-%m-%d %H:%M:%S')"
echo "------------------------------------------------------------"
echo " Backend:   ${HEALTH_CHECK_URL}"
echo " API Base:  ${API_BASE_URL}"
echo " MySQL:     ${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}"
echo " Redis:     ${REDIS_HOST}:${REDIS_PORT}"
echo "============================================================"
echo ""

# ---------- 1. Actuator health ----------
if have_curl; then
    check "Actuator health 端点" "curl -fsS --max-time 10 '${HEALTH_CHECK_URL}'"
    # 详细状态（仅展示，失败不影响）
    actuator_detail=$(curl -fsS --max-time 10 "${HEALTH_CHECK_URL}" 2>/dev/null || echo '{"status":"unreachable"}')
    echo "    -> ${actuator_detail}"
else
    echo "${COLOR_WARN}[SKIP]${COLOR_RESET} curl 未安装，跳过 HTTP 检查"
    FAIL_COUNT=$((FAIL_COUNT + 1))
fi
echo ""

# ---------- 2. 业务接口 ----------
if have_curl; then
    check "登录验证码接口 (/api/auth/captcha)" \
        "curl -fsS --max-time 10 '${API_BASE_URL}/api/auth/captcha'"
    # 项目列表需要鉴权，未带 token 时预期 401/403（说明服务在线即可）
    project_status=$(curl -s -o /dev/null -w '%{http_code}' --max-time 10 \
        "${API_BASE_URL}/api/project?page=1&size=1" 2>/dev/null || echo "000")
    if [[ "${project_status}" =~ ^(200|401|403)$ ]]; then
        echo "${COLOR_OK}[OK]${COLOR_RESET}   项目列表接口（HTTP ${project_status}）"
        PASS_COUNT=$((PASS_COUNT + 1))
    else
        echo "${COLOR_FAIL}[FAIL]${COLOR_RESET} 项目列表接口（HTTP ${project_status}）"
        FAIL_COUNT=$((FAIL_COUNT + 1))
    fi
fi
echo ""

# ---------- 3. MySQL ----------
if have_mysql; then
    if check "MySQL 连接" "mysql_cmd -e 'SELECT 1' -N"; then
        # MySQL 版本
        mysql_version=$(mysql_cmd -e "SELECT VERSION();" -N 2>/dev/null || echo "unknown")
        echo "    -> MySQL 版本: ${mysql_version}"

        # 关键表行数
        echo "    关键表行数:"
        for table in "${KEY_TABLES[@]}"; do
            count=$(mysql_cmd -e "SELECT COUNT(*) FROM \`${MYSQL_DATABASE}\`.\`${table}\`" -N 2>/dev/null || echo "N/A")
            printf "      %-20s %s\n" "${table}" "${count}"
        done
    fi
else
    echo "${COLOR_WARN}[SKIP]${COLOR_RESET} mysql 客户端未安装，跳过数据库检查"
    FAIL_COUNT=$((FAIL_COUNT + 1))
fi
echo ""

# ---------- 4. Redis ----------
if have_redis; then
    if check "Redis 连接 (PING)"; then
        redis_cmd ping > /dev/null 2>&1
        # Redis 信息
        redis_version=$(redis_cmd INFO server 2>/dev/null | grep '^redis_version:' | cut -d: -f2 | tr -d '[:space:]' || echo "unknown")
        echo "    -> Redis 版本: ${redis_version}"
        connected=$(redis_cmd INFO clients 2>/dev/null | grep '^connected_clients:' | cut -d: -f2 | tr -d '[:space:]' || echo "unknown")
        echo "    -> 已连接客户端: ${connected}"
    fi
else
    echo "${COLOR_WARN}[SKIP]${COLOR_RESET} redis-cli 未安装，跳过 Redis 检查"
    FAIL_COUNT=$((FAIL_COUNT + 1))
fi
echo ""

# ---------- 5. 磁盘空间 ----------
echo "磁盘空间:"
disk_failed=0
while read -r line; do
    if [[ -z "${line}" ]]; then continue; fi
    usage=$(echo "${line}" | awk '{print $5}' | tr -d '%')
    mount=$(echo "${line}" | awk '{print $6}')
    if [[ "${usage}" =~ ^[0-9]+$ ]] && (( usage > DISK_THRESHOLD )); then
        echo "${COLOR_FAIL}[FAIL]${COLOR_RESET} ${mount} 使用率 ${usage}% (阈值 ${DISK_THRESHOLD}%)"
        disk_failed=1
        FAIL_COUNT=$((FAIL_COUNT + 1))
    else
        echo "${COLOR_OK}[OK]${COLOR_RESET}   ${line}"
        PASS_COUNT=$((PASS_COUNT + 1))
    fi
done < <(df -h | awk 'NR==1 || $NF=="/" || $NF=="/data" || $NF ~ /^\/data\//')
if [[ ${disk_failed} -eq 1 ]]; then
    echo "    -> 建议清理日志/备份：docker system prune; rm -rf /data/logs/*.gz"
fi
echo ""

# ---------- 6. 内存使用 ----------
echo "内存使用:"
free -h
mem_total=$(free -m | awk '/^Mem:/ {print $2}')
mem_used=$(free -m | awk '/^Mem:/ {print $3}')
if [[ "${mem_total}" =~ ^[0-9]+$ ]] && [[ "${mem_used}" =~ ^[0-9]+$ ]] && (( mem_total > 0 )); then
    mem_pct=$(( mem_used * 100 / mem_total ))
    if (( mem_pct > MEM_THRESHOLD )); then
        echo "${COLOR_FAIL}[FAIL]${COLOR_RESET} 内存使用率 ${mem_pct}% (阈值 ${MEM_THRESHOLD}%)"
        FAIL_COUNT=$((FAIL_COUNT + 1))
    else
        echo "${COLOR_OK}[OK]${COLOR_RESET}   内存使用率 ${mem_pct}% (阈值 ${MEM_THRESHOLD}%)"
        PASS_COUNT=$((PASS_COUNT + 1))
    fi
fi
echo ""

# ---------------------------- 汇总 ----------------------------
echo "============================================================"
echo " 汇总：${COLOR_OK}通过 ${PASS_COUNT}${COLOR_RESET} / ${COLOR_FAIL}失败 ${FAIL_COUNT}${COLOR_RESET}"
echo "============================================================"

if (( FAIL_COUNT > 0 )); then
    exit 1
fi
exit 0
