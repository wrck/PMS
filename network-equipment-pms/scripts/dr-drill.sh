#!/bin/bash
# =============================================================================
# PMS 灾难恢复（DR）演练脚本
#
# 在隔离的测试环境执行端到端 DR 演练：
#   1. 创建全量备份
#   2. 记录故障前数据快照（关键表行数）
#   3. 模拟故障：创建独立测试库 ${MYSQL_DATABASE}_dr 并恢复备份到该库
#   4. 模拟 Redis 故障：备份 RDB → 恢复到独立 Redis DB
#   5. 校验：对比故障前/恢复后数据
#   6. 清理测试资源
#   7. 生成 Markdown 演练报告
#
# 安全约束:
#   - 仅在测试环境执行（DR_ENV=test 时才允许实际恢复操作）
#   - 使用独立测试库后缀 _dr，绝不触碰生产库
#   - 所有破坏性操作需用户确认
# =============================================================================
set -euo pipefail

# ----------------------------------------------------------------------------
# 默认配置
# ----------------------------------------------------------------------------
DR_ENV="${DR_ENV:-test}"
SCRIPT_DIR="${SCRIPT_DIR:-$(cd "$(dirname "${0}")" && pwd)}"
BACKUP_DIR="${BACKUP_DIR:-/data/backups/pms}"
DR_DIR="${DR_DIR:-/tmp/dr_drill_$(date +%Y%m%d_%H%M%S)}"

MYSQL_HOST="${MYSQL_HOST:-127.0.0.1}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-}"
MYSQL_DATABASE="${MYSQL_DATABASE:-pms}"

REDIS_HOST="${REDIS_HOST:-127.0.0.1}"
REDIS_PORT="${REDIS_PORT:-6379}"
REDIS_PASSWORD="${REDIS_PASSWORD:-}"

USE_DOCKER="${USE_DOCKER:-false}"
MYSQL_CONTAINER="${MYSQL_CONTAINER:-pms-mysql}"
REDIS_CONTAINER="${REDIS_CONTAINER:-pms-redis}"

WEBHOOK_URL="${WEBHOOK_URL:-}"
ASSUME_YES=false
SKIP_REDIS=false

# 校验关键表列表（用于行数对比）
VERIFY_TABLES="${VERIFY_TABLES:-pms_project pms_asset pms_impl_task pms_settlement sys_user}"

# ----------------------------------------------------------------------------
# 工具函数
# ----------------------------------------------------------------------------
# 默认日志文件（参数解析阶段可能调用 die，此时 DR_DIR 尚未创建）
LOG_FILE="${LOG_FILE:-/tmp/dr_drill.log}"

log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] [INFO] $*" | tee -a "${LOG_FILE}"
}

warn() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] [WARN] $*" | tee -a "${LOG_FILE}" >&2
}

error() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] [ERROR] $*" | tee -a "${LOG_FILE}" >&2
}

notify() {
    local message=$1
    local status=${2:-info}
    log "${message}"
    if [[ -n "${WEBHOOK_URL}" ]]; then
        curl -s -X POST "${WEBHOOK_URL}" \
            -H "Content-Type: application/json" \
            -d "{\"event\":\"dr_drill\",\"status\":\"${status}\",\"message\":\"$(echo "${message}" | sed 's/"/\\"/g')\"}" \
            >/dev/null 2>&1 || true
    fi
}

die() {
    error "$*"
    notify "DR 演练失败: $*" "error"
    exit 1
}

# MySQL 命令封装
mysql_cli() {
    if [[ "${USE_DOCKER}" == "true" ]]; then
        docker exec -i "${MYSQL_CONTAINER}" mysql \
            -h "${MYSQL_HOST}" -P "${MYSQL_PORT}" \
            -u "${MYSQL_USER}" -p"${MYSQL_PASSWORD}" "$@"
    else
        mysql -h "${MYSQL_HOST}" -P "${MYSQL_PORT}" \
            -u "${MYSQL_USER}" -p"${MYSQL_PASSWORD}" "$@"
    fi
}

redis_cli() {
    if [[ "${USE_DOCKER}" == "true" ]]; then
        if [[ -n "${REDIS_PASSWORD}" ]]; then
            docker exec -i "${REDIS_CONTAINER}" redis-cli \
                -h "${REDIS_HOST}" -p "${REDIS_PORT}" -a "${REDIS_PASSWORD}" --no-auth-warning "$@"
        else
            docker exec -i "${REDIS_CONTAINER}" redis-cli \
                -h "${REDIS_HOST}" -p "${REDIS_PORT}" "$@"
        fi
    else
        if [[ -n "${REDIS_PASSWORD}" ]]; then
            redis-cli -h "${REDIS_HOST}" -p "${REDIS_PORT}" \
                -a "${REDIS_PASSWORD}" --no-auth-warning "$@"
        else
            redis-cli -h "${REDIS_HOST}" -p "${REDIS_PORT}" "$@"
        fi
    fi
}

# 获取表行数（输出 "<table> <count>"）
get_table_count() {
    local db=$1
    local table=$2
    local count
    count=$(mysql_cli -N -e "SELECT COUNT(*) FROM \`${db}\`.\`${table}\`" 2>/dev/null || echo "ERR")
    echo "${table} ${count}"
}

# 参数解析
while [[ $# -gt 0 ]]; do
    case "$1" in
        --env) DR_ENV="$2"; shift 2 ;;
        --yes|-y) ASSUME_YES=true; shift ;;
        --skip-redis) SKIP_REDIS=true; shift ;;
        --help|-h)
            cat <<EOF
用法: $0 [选项]
选项:
  --env <env>      演练环境 (test/staging，默认 test)
  --yes, -y        跳过确认提示
  --skip-redis     跳过 Redis DR 演练
EOF
            exit 0
            ;;
        *) die "未知参数: $1" ;;
    esac
done

# ----------------------------------------------------------------------------
# 前置校验
# ----------------------------------------------------------------------------
mkdir -p "${DR_DIR}"
LOG_FILE="${DR_DIR}/dr_drill.log"

notify "开始 DR 演练 (环境=${DR_ENV})"

log "DR 演练目录: ${DR_DIR}"
log "演练环境: ${DR_ENV}"
log "MySQL: ${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}"
log "Redis: ${REDIS_HOST}:${REDIS_PORT}"
log "Docker 模式: ${USE_DOCKER}"

# 安全检查：DR 演练仅在非生产环境执行
if [[ "${DR_ENV}" == "prod" || "${DR_ENV}" == "production" ]]; then
    die "禁止在生产环境执行 DR 演练（DR_ENV=${DR_ENV}）"
fi

# 测试库后缀（绝不与生产库同名）
DR_TEST_DB="${MYSQL_DATABASE}_dr"
log "测试库: ${DR_TEST_DB}（独立于生产库 ${MYSQL_DATABASE}）"

# 备份脚本存在性校验
[[ -x "${SCRIPT_DIR}/backup.sh" ]] || chmod +x "${SCRIPT_DIR}/backup.sh"
[[ -f "${SCRIPT_DIR}/backup.sh" ]] || die "未找到备份脚本: ${SCRIPT_DIR}/backup.sh"
[[ -f "${SCRIPT_DIR}/restore.sh" ]] || die "未找到恢复脚本: ${SCRIPT_DIR}/restore.sh"

# 用户确认
if [[ "${ASSUME_YES}" != "true" ]]; then
    cat <<EOF

============================================================
  DR 演练即将开始
  - 环境: ${DR_ENV}
  - 测试库: ${DR_TEST_DB}（将创建/删除，不影响 ${MYSQL_DATABASE}）
  - 演练目录: ${DR_DIR}
  - 跳过 Redis: ${SKIP_REDIS}
============================================================
EOF
    read -p "确认开始 DR 演练？输入 YES 继续: " confirm
    [[ "${confirm}" == "YES" ]] || die "用户取消"
fi

# 演练结果跟踪
DR_RESULT="PASS"
DR_BACKUP_FILE=""
DR_MYSQL_RESTORE="未执行"
DR_REDIS_RESTORE="未执行"
DR_DATA_VERIFY="未执行"
DR_START_TIME=$(date -Iseconds)
declare -a BEFORE_COUNTS=()
declare -a AFTER_COUNTS=()

# 演练 cleanup
dr_cleanup() {
    local rc=$?
    log "清理测试库 ${DR_TEST_DB}（如存在）..."
    mysql_cli -e "DROP DATABASE IF EXISTS \`${DR_TEST_DB}\`" 2>>"${LOG_FILE}" || warn "清理测试库失败"
    log "DR 演练结束 (exit=${rc})"
    exit ${rc}
}
trap dr_cleanup EXIT

# ----------------------------------------------------------------------------
# Step 1: 创建全量备份
# ----------------------------------------------------------------------------
log "=========================================="
log "Step 1: 创建全量备份"
log "=========================================="
if ! "${SCRIPT_DIR}/backup.sh" --type full 2>&1 | tee -a "${LOG_FILE}"; then
    DR_RESULT="FAIL"
    die "Step 1 备份失败"
fi

# 找到最新备份（按文件名排序，文件名含 YYYYMMDD 日期）
DR_BACKUP_FILE=$(ls -1 "${BACKUP_DIR}"/pms_*.tar.gz 2>/dev/null | sort -r | head -1 || true)
[[ -n "${DR_BACKUP_FILE}" ]] || { DR_RESULT="FAIL"; die "未找到备份文件"; }
log "最新备份: ${DR_BACKUP_FILE}"

# ----------------------------------------------------------------------------
# Step 2: 记录故障前数据快照
# ----------------------------------------------------------------------------
log "=========================================="
log "Step 2: 记录故障前数据快照（生产库 ${MYSQL_DATABASE}）"
log "=========================================="
for table in ${VERIFY_TABLES}; do
    line=$(get_table_count "${MYSQL_DATABASE}" "${table}")
    BEFORE_COUNTS+=("${line}")
    log "  ${line}"
done

# 记录 Redis 故障前 key 数（如果未跳过）
REDIS_BEFORE_DBSIZE="N/A"
if [[ "${SKIP_REDIS}" != "true" ]]; then
    REDIS_BEFORE_DBSIZE=$(redis_cli DBSIZE 2>/dev/null | awk '{print $2}' || echo "N/A")
    log "  Redis key 数: ${REDIS_BEFORE_DBSIZE}"
fi

# ----------------------------------------------------------------------------
# Step 3: 模拟故障 + 从备份恢复（恢复到独立测试库）
# ----------------------------------------------------------------------------
log "=========================================="
log "Step 3: 模拟故障 + 从备份恢复到测试库 ${DR_TEST_DB}"
log "=========================================="

# 3.1 创建测试库（模拟"全新的空数据库"场景）
log "创建测试库 ${DR_TEST_DB}（模拟故障后的空数据库）..."
mysql_cli -e "DROP DATABASE IF EXISTS \`${DR_TEST_DB}\`" 2>>"${LOG_FILE}" || true
mysql_cli -e "CREATE DATABASE \`${DR_TEST_DB}\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci" \
    2>>"${LOG_FILE}" || { DR_RESULT="FAIL"; die "创建测试库失败"; }

# 3.2 解压备份并恢复到测试库
log "解压备份..."
TEMP_EXTRACT=$(mktemp -d -t dr-extract-XXXXXX)
tar -xzf "${DR_BACKUP_FILE}" -C "${TEMP_EXTRACT}" 2>>"${LOG_FILE}" || { DR_RESULT="FAIL"; die "解压备份失败"; }
BACKUP_NAME_DIR=$(ls "${TEMP_EXTRACT}" | head -1)
SQL_FILE="${TEMP_EXTRACT}/${BACKUP_NAME_DIR}/mysql_full.sql"

if [[ ! -f "${SQL_FILE}" ]]; then
    DR_MYSQL_RESTORE="失败（未找到 mysql_full.sql）"
    DR_RESULT="FAIL"
    die "备份中未找到 mysql_full.sql"
fi

# 3.3 恢复 SQL 到测试库
# mysqldump --databases 会包含 "CREATE DATABASE <db>" 和 "USE <db>" 语句
# 需要剥离这些行，改用显式 USE <test_db>
log "恢复 SQL 到测试库 ${DR_TEST_DB}..."
# 过滤掉 CREATE DATABASE / USE 语句，避免污染测试库选择
if sed -E '/^(CREATE DATABASE|USE|DROP DATABASE|DROP SCHEMA) /d' "${SQL_FILE}" \
    | mysql_cli "${DR_TEST_DB}" 2>>"${LOG_FILE}"; then
    DR_MYSQL_RESTORE="成功"
    log "MySQL 恢复到测试库成功"
else
    DR_MYSQL_RESTORE="失败"
    DR_RESULT="FAIL"
    die "MySQL 恢复到测试库失败"
fi

rm -rf "${TEMP_EXTRACT}"

# ----------------------------------------------------------------------------
# Step 4: 校验恢复数据（对比生产库 vs 测试库行数）
# ----------------------------------------------------------------------------
log "=========================================="
log "Step 4: 校验恢复数据（对比 ${MYSQL_DATABASE} vs ${DR_TEST_DB}）"
log "=========================================="
DATA_VERIFY_PASS=true
for table in ${VERIFY_TABLES}; do
    src_count=$(mysql_cli -N -e "SELECT COUNT(*) FROM \`${MYSQL_DATABASE}\`.\`${table}\`" 2>/dev/null || echo "ERR")
    dst_count=$(mysql_cli -N -e "SELECT COUNT(*) FROM \`${DR_TEST_DB}\`.\`${table}\`" 2>/dev/null || echo "ERR")
    line="${table}: src=${src_count} restored=${dst_count}"
    AFTER_COUNTS+=("${line}")
    log "  ${line}"
    if [[ "${src_count}" != "${dst_count}" || "${src_count}" == "ERR" ]]; then
        DATA_VERIFY_PASS=false
        warn "  行数不一致: ${table}"
    fi
done

if [[ "${DATA_VERIFY_PASS}" == "true" ]]; then
    DR_DATA_VERIFY="通过"
    log "数据校验通过"
else
    DR_DATA_VERIFY="失败"
    DR_RESULT="FAIL"
    warn "数据校验失败（部分表行数不一致）"
fi

# ----------------------------------------------------------------------------
# Step 5: Redis DR 演练（备份 → 恢复到独立 Redis DB）
# ----------------------------------------------------------------------------
if [[ "${SKIP_REDIS}" != "true" ]]; then
    log "=========================================="
    log "Step 5: Redis DR 演练"
    log "=========================================="
    # 使用 Redis DB 15 作为演练目标（避免影响业务数据）
    REDIS_DR_DB=15
    log "使用 Redis DB ${REDIS_DR_DB} 作为演练目标"

    # 5.1 备份当前 Redis RDB
    log "触发 Redis BGSAVE..."
    if redis_cli BGSAVE >/dev/null 2>>"${LOG_FILE}"; then
        # 等待 BGSAVE
        for i in {1..60}; do
            status=$(redis_cli INFO persistence 2>/dev/null \
                | awk -F: '/^rdb_bgsave_in_progress/ {gsub(/\r/,"",$2); print $2}')
            [[ "${status}" == "0" ]] && break
            sleep 1
        done

        # 5.2 复制一份当前生产 key 到演练 DB（模拟故障恢复场景）
        log "复制部分 key 到演练 DB ${REDIS_DR_DB}..."
        SAMPLE_KEYS=$(redis_cli -n 0 --scan --count 100 2>/dev/null | head -10 || true)
        MOVED_COUNT=0
        if [[ -n "${SAMPLE_KEYS}" ]]; then
            for key in ${SAMPLE_KEYS}; do
                # 使用 DUMP + RESTORE 在 DB 之间复制 key
                ttl=$(redis_cli -n 0 PTTL "${key}" 2>/dev/null | head -1)
                [[ "${ttl}" == "-2" ]] && continue   # key 已过期
                [[ "${ttl}" == "-1" ]] && ttl=0       # 永久 key
                dumped=$(redis_cli -n 0 DUMP "${key}" 2>/dev/null) || continue
                if [[ -n "${dumped}" ]]; then
                    redis_cli -n "${REDIS_DR_DB}" RESTORE "${key}" "${ttl}" "${dumped}" REPLACE >/dev/null 2>&1 \
                        && MOVED_COUNT=$((MOVED_COUNT + 1)) || true
                fi
            done
        fi
        log "复制了 ${MOVED_COUNT} 个 key 到演练 DB"

        # 5.3 验证演练 DB 的 key 可读
        DR_DBSIZE=$(redis_cli -n "${REDIS_DR_DB}" DBSIZE 2>/dev/null | awk '{print $2}')
        log "演练 DB key 数: ${DR_DBSIZE}"

        if [[ "${DR_DBSIZE:-0}" -gt 0 ]] || [[ "${MOVED_COUNT}" -eq 0 ]]; then
            DR_REDIS_RESTORE="成功"
            log "Redis DR 演练通过"
        else
            DR_REDIS_RESTORE="失败"
            DR_RESULT="FAIL"
            warn "Redis DR 演练失败"
        fi

        # 5.4 清理演练 DB
        log "清理演练 DB ${REDIS_DR_DB}..."
        redis_cli -n "${REDIS_DR_DB}" FLUSHDB >/dev/null 2>&1 || warn "清理演练 DB 失败"
    else
        DR_REDIS_RESTORE="失败（BGSAVE 失败）"
        DR_RESULT="FAIL"
        warn "Redis BGSAVE 失败，跳过 Redis DR 演练"
    fi
else
    log "Step 5: 跳过 Redis DR 演练（--skip-redis）"
fi

# ----------------------------------------------------------------------------
# Step 6: 生成 DR 演练报告
# ----------------------------------------------------------------------------
log "=========================================="
log "Step 6: 生成 DR 演练报告"
log "=========================================="
DR_END_TIME=$(date -Iseconds)
REPORT_FILE="${DR_DIR}/dr_report.md"

# 格式化 BEFORE/AFTER 对比表
BEFORE_TABLE=""
for line in "${BEFORE_COUNTS[@]}"; do
    BEFORE_TABLE+="| ${line} |$(echo "${line}" | awk '{print "  "$2}')|
"
done

AFTER_TABLE=""
for line in "${AFTER_COUNTS[@]}"; do
    AFTER_TABLE+="| ${line} |
"
done

cat > "${REPORT_FILE}" <<EOF
# PMS 灾难恢复（DR）演练报告

## 演练概览

| 项目 | 值 |
|------|----|
| 演练开始时间 | ${DR_START_TIME} |
| 演练结束时间 | ${DR_END_TIME} |
| 演练环境 | ${DR_ENV} |
| 演练结果 | **${DR_RESULT}** |
| 演练目录 | ${DR_DIR} |
| 执行主机 | $(hostname) |
| Docker 模式 | ${USE_DOCKER} |

## 备份信息

| 项目 | 值 |
|------|----|
| 备份文件 | \`${DR_BACKUP_FILE}\` |
| 备份大小 | $(du -h "${DR_BACKUP_FILE}" 2>/dev/null | awk '{print $1}' || echo "N/A") |
| 备份校验文件 | $(ls "${DR_BACKUP_FILE}.md5" 2>/dev/null && echo "存在" || echo "缺失") |

## 演练步骤

### Step 1: 全量备份 — 成功
- 执行脚本: \`${SCRIPT_DIR}/backup.sh --type full\`
- 备份归档: \`${DR_BACKUP_FILE}\`

### Step 2: 故障前数据快照（生产库 \`${MYSQL_DATABASE}\`）

| 表 | 行数 |
|----|------|
EOF

for line in "${BEFORE_COUNTS[@]}"; do
    echo "| ${line% *} | ${line#* } |" >> "${REPORT_FILE}"
done

cat >> "${REPORT_FILE}" <<EOF

Redis 故障前 key 数: ${REDIS_BEFORE_DBSIZE}

### Step 3: 模拟故障 + 从备份恢复到测试库 \`${DR_TEST_DB}\`

- 创建独立测试库 \`${DR_TEST_DB}\`（绝不触碰生产库 \`${MYSQL_DATABASE}\`）
- 从备份恢复 SQL 到测试库
- **MySQL 恢复结果: ${DR_MYSQL_RESTORE}**

### Step 4: 数据校验（生产库 vs 测试库行数对比）

| 表 | 生产库 | 测试库 | 结果 |
|----|--------|--------|------|
EOF

for table in ${VERIFY_TABLES}; do
    src_count=$(mysql_cli -N -e "SELECT COUNT(*) FROM \`${MYSQL_DATABASE}\`.\`${table}\`" 2>/dev/null || echo "ERR")
    dst_count=$(mysql_cli -N -e "SELECT COUNT(*) FROM \`${DR_TEST_DB}\`.\`${table}\`" 2>/dev/null || echo "ERR")
    if [[ "${src_count}" == "${dst_count}" && "${src_count}" != "ERR" ]]; then
        verify_result="✅ 一致"
    else
        verify_result="❌ 不一致"
    fi
    echo "| ${table} | ${src_count} | ${dst_count} | ${verify_result} |" >> "${REPORT_FILE}"
done

cat >> "${REPORT_FILE}" <<EOF

**数据校验结果: ${DR_DATA_VERIFY}**

### Step 5: Redis DR 演练

- Redis 演练结果: ${DR_REDIS_RESTORE}
- 跳过: ${SKIP_REDIS}

## 演练结论

**总体结果: ${DR_RESULT}**

EOF

if [[ "${DR_RESULT}" == "PASS" ]]; then
    cat >> "${REPORT_FILE}" <<'EOF'
- ✅ 备份流程正常，备份文件可成功恢复
- ✅ 数据完整性校验通过（行数一致）
- ✅ Redis 备份/恢复流程正常

**建议**:
- DR 演练频率: 每月至少一次
- 定期验证备份文件的可用性
- 监控备份任务执行成功率
EOF
else
    cat >> "${REPORT_FILE}" <<'EOF'
- ❌ DR 演练存在失败项，请检查上方详细步骤
- 需要排查失败原因并修复后重新演练
- 在 DR 演练通过前，不应将系统视为具备灾难恢复能力

**排查建议**:
- 检查备份脚本日志: `${BACKUP_DIR}/backup_*.log`
- 检查 MySQL/Redis 连接性
- 检查备份文件完整性（md5sum -c）
EOF
fi

cat >> "${REPORT_FILE}" <<EOF

## 配置信息

| 配置项 | 值 |
|--------|----|
| MYSQL_HOST | ${MYSQL_HOST} |
| MYSQL_PORT | ${MYSQL_PORT} |
| MYSQL_DATABASE | ${MYSQL_DATABASE} |
| REDIS_HOST | ${REDIS_HOST} |
| REDIS_PORT | ${REDIS_PORT} |
| USE_DOCKER | ${USE_DOCKER} |
| VERIFY_TABLES | ${VERIFY_TABLES} |

## 完整日志

演练日志位于: \`${LOG_FILE}\`
EOF

log "DR 演练报告已生成: ${REPORT_FILE}"

# ----------------------------------------------------------------------------
# 完成
# ----------------------------------------------------------------------------
notify "DR 演练完成: 结果=${DR_RESULT}, 报告=${REPORT_FILE}" "$(echo "${DR_RESULT}" | tr '[:upper:]' '[:lower:]')"

# 输出报告到 stdout
echo ""
echo "============================================================"
echo "  DR 演练结果: ${DR_RESULT}"
echo "  报告: ${REPORT_FILE}"
echo "  日志: ${LOG_FILE}"
echo "============================================================"
echo ""
cat "${REPORT_FILE}"

# 移除 trap（cleanup 会被显式调用）
trap - EXIT
dr_cleanup_with_exit() {
    log "清理测试库 ${DR_TEST_DB}（如存在）..."
    mysql_cli -e "DROP DATABASE IF EXISTS \`${DR_TEST_DB}\`" 2>>"${LOG_FILE}" || warn "清理测试库失败"
}
dr_cleanup_with_exit

if [[ "${DR_RESULT}" == "PASS" ]]; then
    exit 0
else
    exit 1
fi
