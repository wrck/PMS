#!/bin/bash
# =============================================================================
# PMS 恢复脚本（从备份归档恢复 MySQL + Redis）
# 用法:
#   ./restore.sh <backup_file.tar.gz> [--skip-redis] [--skip-mysql] [--yes]
# 流程:
#   1. 参数校验 + 备份文件存在性校验
#   2. md5 完整性校验
#   3. 解压到临时目录
#   4. 停止应用（可选，避免数据冲突）
#   5. MySQL 恢复（全量 → binlog 重放）
#   6. Redis 恢复（停 Redis → 替换 dump.rdb → 启 Redis）
#   7. 恢复后校验（关键表行数）
# =============================================================================
set -euo pipefail

# ----------------------------------------------------------------------------
# 默认配置（可通过环境变量覆盖）
# ----------------------------------------------------------------------------
BACKUP_DIR="${BACKUP_DIR:-/data/backups/pms}"
BACKEND_CONTAINER="${BACKEND_CONTAINER:-pms-backend}"
REDIS_CONTAINER="${REDIS_CONTAINER:-pms-redis}"

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
REDIS_RDB_PATH_IN_CONTAINER="${REDIS_RDB_PATH_IN_CONTAINER:-/data/dump.rdb}"

WEBHOOK_URL="${WEBHOOK_URL:-}"
LOG_FILE="/tmp/pms-restore_$(date +%Y%m%d_%H%M%S).log"

# 恢复选项
SKIP_REDIS=false
SKIP_MYSQL=false
SKIP_APP_STOP=false
ASSUME_YES=false

# ----------------------------------------------------------------------------
# 工具函数
# ----------------------------------------------------------------------------
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
            -d "{\"event\":\"restore\",\"status\":\"${status}\",\"message\":\"$(echo "${message}" | sed 's/"/\\"/g')\"}" \
            >/dev/null 2>&1 || true
    fi
}

die() {
    error "$*"
    notify "恢复失败: $*" "error"
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

# ----------------------------------------------------------------------------
# 参数解析
# ----------------------------------------------------------------------------
BACKUP_FILE=""
while [[ $# -gt 0 ]]; do
    case "$1" in
        --skip-redis) SKIP_REDIS=true; shift ;;
        --skip-mysql) SKIP_MYSQL=true; shift ;;
        --skip-app-stop) SKIP_APP_STOP=true; shift ;;
        --yes|-y) ASSUME_YES=true; shift ;;
        --help|-h)
            cat <<EOF
用法: $0 <backup_file.tar.gz> [选项]
选项:
  --skip-redis     跳过 Redis 恢复
  --skip-mysql     跳过 MySQL 恢复
  --skip-app-stop  跳过停止应用（不推荐）
  --yes, -y        跳过确认提示
EOF
            exit 0
            ;;
        -*)
            die "未知选项: $1"
            ;;
        *)
            if [[ -z "${BACKUP_FILE}" ]]; then
                BACKUP_FILE="$1"
            else
                die "参数过多: $1（仅需要备份文件路径）"
            fi
            shift
            ;;
    esac
done

[[ -n "${BACKUP_FILE}" ]] || die "缺少备份文件参数。用法: $0 <backup_file.tar.gz> [选项]"

# 备份文件路径解析（支持相对路径）
if [[ ! -f "${BACKUP_FILE}" ]]; then
    # 尝试在 BACKUP_DIR 下查找
    if [[ -f "${BACKUP_DIR}/${BACKUP_FILE}" ]]; then
        BACKUP_FILE="${BACKUP_DIR}/${BACKUP_FILE}"
    else
        die "备份文件不存在: ${BACKUP_FILE}"
    fi
fi

notify "开始恢复流程: ${BACKUP_FILE}"

# ----------------------------------------------------------------------------
# Step 1: md5 完整性校验
# ----------------------------------------------------------------------------
log "Step 1: md5 完整性校验..."
BACKUP_DIR_NAME=$(dirname "${BACKUP_FILE}")
BACKUP_BASENAME=$(basename "${BACKUP_FILE}")

if [[ -f "${BACKUP_FILE}.md5" ]]; then
    # md5 文件内容形如: "<md5>  <filename>"
    # md5sum -c 需在与 md5 文件相同的目录执行（或文件名能正确解析）
    if (cd "${BACKUP_DIR_NAME}" && md5sum -c "${BACKUP_BASENAME}.md5" >/dev/null 2>&1); then
        log "md5 校验通过"
    else
        die "md5 校验失败: 备份文件可能已损坏"
    fi
else
    warn "未找到 ${BACKUP_FILE}.md5，跳过 md5 校验（不推荐）"
    if [[ "${ASSUME_YES}" != "true" ]]; then
        read -p "未找到 md5 校验文件，是否继续？[y/N] " confirm
        [[ "${confirm}" =~ ^[Yy]$ ]] || die "用户取消"
    fi
fi

# ----------------------------------------------------------------------------
# Step 2: 解压备份
# ----------------------------------------------------------------------------
TEMP_DIR=$(mktemp -d -t pms-restore-XXXXXX)
log "Step 2: 解压备份到临时目录: ${TEMP_DIR}"
tar -xzf "${BACKUP_FILE}" -C "${TEMP_DIR}" || die "解压失败"

# 备份内部结构应为 pms_YYYYMMDD_HHMMSS/<files>
BACKUP_NAME_DIR=$(ls "${TEMP_DIR}" | head -1)
EXTRACT_DIR="${TEMP_DIR}/${BACKUP_NAME_DIR}"
[[ -d "${EXTRACT_DIR}" ]] || die "解压后未找到备份目录: ${BACKUP_NAME_DIR}"
log "解压完成: ${EXTRACT_DIR}"

# 显示备份元数据
if [[ -f "${EXTRACT_DIR}/manifest.txt" ]]; then
    log "备份元数据:"
    sed 's/^/    /' "${EXTRACT_DIR}/manifest.txt" | tee -a "${LOG_FILE}"
fi

# 读取备份类型（用于决定是否重放 binlog）
BACKUP_TYPE_FROM_MANIFEST="unknown"
if [[ -f "${EXTRACT_DIR}/manifest.txt" ]]; then
    BACKUP_TYPE_FROM_MANIFEST=$(grep '^backup_type=' "${EXTRACT_DIR}/manifest.txt" | cut -d= -f2 || echo "unknown")
fi

# cleanup 临时目录
cleanup() {
    local rc=$?
    log "清理临时目录: ${TEMP_DIR}"
    rm -rf "${TEMP_DIR}"
    exit ${rc}
}
trap cleanup EXIT

# 确认恢复（破坏性操作，需用户确认）
if [[ "${ASSUME_YES}" != "true" ]]; then
    cat <<EOF

============================================================
  警告: 即将执行恢复操作，会覆盖现有数据！
  MySQL 数据库: ${MYSQL_DATABASE}
  Redis: ${REDIS_HOST}:${REDIS_PORT}
  Docker 模式: ${USE_DOCKER}
============================================================
EOF
    read -p "确认执行恢复？输入 YES 继续: " confirm
    [[ "${confirm}" == "YES" ]] || die "用户取消恢复"
fi

# ----------------------------------------------------------------------------
# Step 3: 停止应用（避免恢复期间数据写入冲突）
# ----------------------------------------------------------------------------
if [[ "${SKIP_APP_STOP}" != "true" ]]; then
    log "Step 3: 停止应用容器（避免数据冲突）..."
    if [[ "${USE_DOCKER}" == "true" ]]; then
        if docker ps --format '{{.Names}}' | grep -q "^${BACKEND_CONTAINER}$"; then
            docker stop "${BACKEND_CONTAINER}" 2>>"${LOG_FILE}" || warn "停止 ${BACKEND_CONTAINER} 失败"
            APP_STOPPED=true
            log "已停止 ${BACKEND_CONTAINER}"
        else
            warn "容器 ${BACKEND_CONTAINER} 未运行"
        fi
    else
        warn "非 Docker 模式：请手动停止应用进程后再恢复（--skip-app-stop 跳过此提示）"
    fi
else
    warn "已跳过停止应用步骤（注意数据冲突风险）"
fi

# ----------------------------------------------------------------------------
# Step 4: MySQL 恢复
# ----------------------------------------------------------------------------
if [[ "${SKIP_MYSQL}" != "true" ]]; then
    if [[ -f "${EXTRACT_DIR}/mysql_full.sql" ]]; then
        log "Step 4: MySQL 恢复..."
        log "导入全量备份 mysql_full.sql..."

        # 通过 stdin 重定向避免 -p 警告写入日志
        if [[ "${USE_DOCKER}" == "true" ]]; then
            docker exec -i "${MYSQL_CONTAINER}" mysql \
                -h "${MYSQL_HOST}" -P "${MYSQL_PORT}" \
                -u "${MYSQL_USER}" -p"${MYSQL_PASSWORD}" \
                < "${EXTRACT_DIR}/mysql_full.sql" 2>>"${LOG_FILE}" \
                || die "MySQL 全量恢复失败"
        else
            mysql -h "${MYSQL_HOST}" -P "${MYSQL_PORT}" \
                -u "${MYSQL_USER}" -p"${MYSQL_PASSWORD}" \
                < "${EXTRACT_DIR}/mysql_full.sql" 2>>"${LOG_FILE}" \
                || die "MySQL 全量恢复失败"
        fi
        log "MySQL 全量恢复完成"

        # binlog 重放（仅对增量备份有意义）
        # 注意：全量备份的 mysqldump --single-transaction 已经包含一致快照
        #       重放同一时刻的 binlog 会导致重复键错误。因此仅在增量备份时重放。
        BINLOG_FILES=$(find "${EXTRACT_DIR}" -maxdepth 1 -name "mysql-bin.*" -type f 2>/dev/null || true)
        if [[ -n "${BINLOG_FILES}" ]]; then
            if [[ "${BACKUP_TYPE_FROM_MANIFEST}" == "incremental" ]]; then
                log "增量备份模式，开始重放 binlog..."
                for binlog_file in ${BINLOG_FILES}; do
                    log "重放 binlog: $(basename "${binlog_file}")"
                    if [[ "${USE_DOCKER}" == "true" ]]; then
                        # 将 binlog 文件复制到 MySQL 容器内，再在容器内 mysqlbinlog | mysql
                        CONTAINER_BINLOG="/tmp/$(basename "${binlog_file}")"
                        docker cp "${binlog_file}" "${MYSQL_CONTAINER}:${CONTAINER_BINLOG}" 2>>"${LOG_FILE}" \
                            || { warn "复制 binlog 到容器失败，跳过: $(basename "${binlog_file}")"; continue; }
                        docker exec -i "${MYSQL_CONTAINER}" bash -c \
                            "mysqlbinlog '${CONTAINER_BINLOG}' 2>/dev/null | mysql -h '${MYSQL_HOST}' -P '${MYSQL_PORT}' -u '${MYSQL_USER}' -p'${MYSQL_PASSWORD}'" \
                            2>>"${LOG_FILE}" \
                            || warn "binlog 重放失败（不影响全量恢复结果）: $(basename "${binlog_file}")"
                        docker exec -i "${MYSQL_CONTAINER}" rm -f "${CONTAINER_BINLOG}" 2>/dev/null || true
                    else
                        mysqlbinlog "${binlog_file}" 2>/dev/null \
                            | mysql -h "${MYSQL_HOST}" -P "${MYSQL_PORT}" \
                                -u "${MYSQL_USER}" -p"${MYSQL_PASSWORD}" 2>>"${LOG_FILE}" \
                            || warn "binlog 重放失败（不影响全量恢复结果）: $(basename "${binlog_file}")"
                    fi
                done
                log "binlog 重放完成"
            else
                log "全量备份模式，跳过 binlog 重放（dump 已含一致快照，重放会导致重复键）"
                log "如需基于此 binlog 做时间点恢复，请手动执行:"
                log "  mysqlbinlog --start-position=<P> <binlog_file> | mysql ..."
            fi
        else
            log "未发现 binlog 文件，跳过 binlog 重放"
        fi
    else
        warn "备份中未找到 mysql_full.sql，跳过 MySQL 恢复"
    fi
else
    log "Step 4: 跳过 MySQL 恢复（--skip-mysql）"
fi

# ----------------------------------------------------------------------------
# Step 5: Redis 恢复
# ----------------------------------------------------------------------------
if [[ "${SKIP_REDIS}" != "true" ]]; then
    if [[ -f "${EXTRACT_DIR}/redis_dump.rdb" ]]; then
        log "Step 5: Redis 恢复..."
        if [[ "${USE_DOCKER}" == "true" ]]; then
            # 停 Redis → 替换 dump.rdb → 启 Redis
            log "停止 Redis 容器..."
            docker stop "${REDIS_CONTAINER}" 2>>"${LOG_FILE}" || warn "停止 Redis 失败"

            log "替换 Redis RDB 文件..."
            docker cp "${EXTRACT_DIR}/redis_dump.rdb" \
                "${REDIS_CONTAINER}:${REDIS_RDB_PATH_IN_CONTAINER}" 2>>"${LOG_FILE}" \
                || die "替换 Redis RDB 失败"

            log "启动 Redis 容器..."
            docker start "${REDIS_CONTAINER}" 2>>"${LOG_FILE}" || die "启动 Redis 失败"

            # 等待 Redis 就绪
            log "等待 Redis 就绪..."
            for i in {1..30}; do
                if redis_cli PING 2>/dev/null | grep -q PONG; then
                    log "Redis 已就绪"
                    break
                fi
                sleep 1
                [[ ${i} -eq 30 ]] && warn "Redis 启动超时"
            done
        else
            warn "非 Docker 模式：Redis 恢复需手动操作"
            log "请手动执行:"
            log "  1. 停止 Redis 服务 (systemctl stop redis)"
            log "  2. 替换 RDB: cp ${EXTRACT_DIR}/redis_dump.rdb <redis-data-dir>/dump.rdb"
            log "  3. 启动 Redis: systemctl start redis"
        fi
        log "Redis 恢复完成"
    else
        warn "备份中未找到 redis_dump.rdb，跳过 Redis 恢复"
    fi
else
    log "Step 5: 跳过 Redis 恢复（--skip-redis）"
fi

# ----------------------------------------------------------------------------
# Step 6: 启动应用
# ----------------------------------------------------------------------------
if [[ "${SKIP_APP_STOP}" != "true" && "${APP_STOPPED:-false}" == "true" ]]; then
    log "Step 6: 启动应用容器..."
    if [[ "${USE_DOCKER}" == "true" ]]; then
        docker start "${BACKEND_CONTAINER}" 2>>"${LOG_FILE}" || warn "启动 ${BACKEND_CONTAINER} 失败（请手动检查）"
        log "已启动 ${BACKEND_CONTAINER}"
    fi
else
    log "Step 6: 跳过启动应用（应用未停止或非 Docker 模式）"
fi

# ----------------------------------------------------------------------------
# Step 7: 恢复后校验
# ----------------------------------------------------------------------------
log "Step 7: 恢复后数据校验..."

# MySQL 关键表行数校验
if [[ "${SKIP_MYSQL}" != "true" ]]; then
    log "MySQL 关键表行数:"
    for table in pms_project pms_asset pms_impl_task pms_settlement pms_user sys_user; do
        # 表可能不存在，单独捕获错误
        COUNT=$(mysql_cli -N -e "SELECT COUNT(*) FROM \`${MYSQL_DATABASE}\`.\`${table}\`" 2>/dev/null || echo "N/A")
        log "  ${table}: ${COUNT}"
    done

    # 数据库连接性校验
    if mysql_cli -e "SELECT 1" >/dev/null 2>&1; then
        log "MySQL 连接正常"
    else
        warn "MySQL 连接异常"
    fi
fi

# Redis 校验
if [[ "${SKIP_REDIS}" != "true" ]]; then
    if redis_cli PING 2>/dev/null | grep -q PONG; then
        log "Redis 连接正常"
        # 显示 Redis key 总数
        DBSIZE=$(redis_cli DBSIZE 2>/dev/null | awk '{print $2}')
        log "Redis key 总数: ${DBSIZE:-N/A}"
    else
        warn "Redis 连接异常"
    fi
fi

# ----------------------------------------------------------------------------
# 完成
# ----------------------------------------------------------------------------
trap - EXIT
notify "恢复完成: ${BACKUP_FILE}" "success"
log "恢复流程结束"
log "临时目录 ${TEMP_DIR} 将被清理"
rm -rf "${TEMP_DIR}"

exit 0
