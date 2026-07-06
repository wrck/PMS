#!/bin/bash
# =============================================================================
# PMS 备份脚本（MySQL 全量 + binlog + Redis RDB）
# 支持 Docker 环境（通过 docker exec 调用容器内命令）和直连模式
# 用法:
#   ./backup.sh                      # 全量备份（默认）
#   ./backup.sh --type incremental   # 增量备份（仅 binlog + Redis）
#   ./backup.sh --type full          # 全量备份
# =============================================================================
set -euo pipefail

# ----------------------------------------------------------------------------
# 默认配置（可通过环境变量覆盖）
# ----------------------------------------------------------------------------
BACKUP_DIR="${BACKUP_DIR:-/data/backups/pms}"
BACKUP_TYPE="${BACKUP_TYPE:-full}"

# MySQL 配置
MYSQL_HOST="${MYSQL_HOST:-127.0.0.1}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-}"
MYSQL_DATABASE="${MYSQL_DATABASE:-pms}"

# Redis 配置
REDIS_HOST="${REDIS_HOST:-127.0.0.1}"
REDIS_PORT="${REDIS_PORT:-6379}"
REDIS_PASSWORD="${REDIS_PASSWORD:-}"

# Docker 模式（USE_DOCKER=true 时通过 docker exec 调用容器内命令）
USE_DOCKER="${USE_DOCKER:-false}"
MYSQL_CONTAINER="${MYSQL_CONTAINER:-pms-mysql}"
REDIS_CONTAINER="${REDIS_CONTAINER:-pms-redis}"
MYSQL_DUMP_DIR_IN_CONTAINER="${MYSQL_DUMP_DIR_IN_CONTAINER:-/tmp/pms-backup}"
REDIS_RDB_PATH_IN_CONTAINER="${REDIS_RDB_PATH_IN_CONTAINER:-/data/dump.rdb}"

# 保留策略 / 通知
RETENTION_DAYS="${RETENTION_DAYS:-30}"
WEBHOOK_URL="${WEBHOOK_URL:-}"
MIN_DISK_FREE_MB="${MIN_DISK_FREE_MB:-2048}"   # 备份前最低可用磁盘 MB

# 备份命名
DATE=$(date +%Y%m%d)
TIME=$(date +%H%M%S)
BACKUP_NAME="pms_${DATE}_${TIME}"
BACKUP_PATH="${BACKUP_DIR}/${BACKUP_NAME}"
LOG_FILE="${BACKUP_DIR}/backup_${BACKUP_NAME}.log"

# 重写 stdout/stderr 到日志（同时保留终端输出）
mkdir -p "${BACKUP_DIR}"

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
        local payload
        payload=$(cat <<EOF
{"event":"backup","status":"${status}","message":"$(echo "${message}" | sed 's/"/\\"/g')","backupName":"${BACKUP_NAME}","host":"$(hostname)","timestamp":"$(date -Iseconds)"}
EOF
)
        curl -s -X POST "${WEBHOOK_URL}" \
            -H "Content-Type: application/json" \
            -d "${payload}" >/dev/null 2>&1 || true
    fi
}

die() {
    error "$*"
    notify "备份失败: $*" "error"
    exit 1
}

# 解析命令行参数
while [[ $# -gt 0 ]]; do
    case "$1" in
        --type)
            BACKUP_TYPE="$2"
            shift 2
            ;;
        --help|-h)
            echo "用法: $0 [--type full|incremental]"
            exit 0
            ;;
        *)
            die "未知参数: $1"
            ;;
    esac
done

# 校验 BACKUP_TYPE
case "${BACKUP_TYPE}" in
    full|incremental) ;;
    *) die "不支持的备份类型: ${BACKUP_TYPE}（应为 full 或 incremental）" ;;
esac

# MySQL 命令封装（根据 USE_DOCKER 切换直连或容器内调用）
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

mysqldump_cli() {
    if [[ "${USE_DOCKER}" == "true" ]]; then
        docker exec -i "${MYSQL_CONTAINER}" mysqldump \
            -h "${MYSQL_HOST}" -P "${MYSQL_PORT}" \
            -u "${MYSQL_USER}" -p"${MYSQL_PASSWORD}" "$@"
    else
        mysqldump -h "${MYSQL_HOST}" -P "${MYSQL_PORT}" \
            -u "${MYSQL_USER}" -p"${MYSQL_PASSWORD}" "$@"
    fi
}

mysqlbinlog_cli() {
    if [[ "${USE_DOCKER}" == "true" ]]; then
        docker exec -i "${MYSQL_CONTAINER}" mysqlbinlog \
            --host="${MYSQL_HOST}" --port="${MYSQL_PORT}" \
            --user="${MYSQL_USER}" --password="${MYSQL_PASSWORD}" "$@"
    else
        mysqlbinlog --host="${MYSQL_HOST}" --port="${MYSQL_PORT}" \
            --user="${MYSQL_USER}" --password="${MYSQL_PASSWORD}" "$@"
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
# 启动备份
# ----------------------------------------------------------------------------
notify "开始 PMS 备份 (类型=${BACKUP_TYPE}, 模式=Docker=${USE_DOCKER})"

# 1. 磁盘空间检查
log "检查磁盘空间 (最低 ${MIN_DISK_FREE_MB} MB)..."
AVAILABLE_MB=$(df -m "${BACKUP_DIR}" | awk 'NR==2 {print $4}')
if [[ -z "${AVAILABLE_MB}" ]]; then
    die "无法获取磁盘空间信息: ${BACKUP_DIR}"
fi
if (( AVAILABLE_MB < MIN_DISK_FREE_MB )); then
    die "磁盘空间不足: 可用 ${AVAILABLE_MB} MB < 最低 ${MIN_DISK_FREE_MB} MB"
fi
log "磁盘空间可用: ${AVAILABLE_MB} MB"

# 2. 依赖检查
log "检查依赖工具..."
for cmd in tar md5sum; do
    command -v "${cmd}" >/dev/null 2>&1 || die "缺少依赖: ${cmd}"
done
if [[ "${USE_DOCKER}" == "true" ]]; then
    command -v docker >/dev/null 2>&1 || die "缺少依赖: docker"
else
    command -v mysql >/dev/null 2>&1 || die "缺少依赖: mysql"
    command -v mysqldump >/dev/null 2>&1 || die "缺少依赖: mysqldump"
    command -v redis-cli >/dev/null 2>&1 || die "缺少依赖: redis-cli"
fi
log "依赖检查通过"

mkdir -p "${BACKUP_PATH}"

# 错误捕获：脚本失败时清理临时目录
cleanup() {
    local rc=$?
    if [[ ${rc} -ne 0 ]]; then
        error "备份过程中断，清理临时目录: ${BACKUP_PATH}"
        rm -rf "${BACKUP_PATH}"
    fi
    exit ${rc}
}
trap cleanup EXIT

# ----------------------------------------------------------------------------
# Step 1: MySQL 全量备份（仅 full 模式）
# ----------------------------------------------------------------------------
if [[ "${BACKUP_TYPE}" == "full" ]]; then
    log "Step 1: MySQL 全量备份 (database=${MYSQL_DATABASE})..."
    # --single-transaction: InnoDB 一致性快照（不锁表）
    # --routines: 备份存储过程/函数
    # --triggers: 备份触发器
    # --master-data=2: 写入 binlog 坐标注释（CHANGE MASTER 语句被注释）
    # --set-gtid-purged=OFF: 避免 GTID 冲突（如未启用 GTID 无影响）
    # --column-statistics=0: 兼容旧版 MySQL 8 服务端
    mysqldump_cli \
        --single-transaction \
        --routines \
        --triggers \
        --master-data=2 \
        --set-gtid-purged=OFF \
        --column-statistics=0 \
        --databases "${MYSQL_DATABASE}" \
        > "${BACKUP_PATH}/mysql_full.sql" 2>>"${LOG_FILE}" \
        || die "MySQL 全量备份失败"

    dump_size=$(stat -c %s "${BACKUP_PATH}/mysql_full.sql" 2>/dev/null || stat -f %z "${BACKUP_PATH}/mysql_full.sql")
    log "MySQL 全量备份完成: mysql_full.sql (${dump_size} bytes)"
else
    log "Step 1: 跳过 MySQL 全量备份（增量模式）"
fi

# ----------------------------------------------------------------------------
# Step 2: MySQL binlog 备份
# ----------------------------------------------------------------------------
log "Step 2: MySQL binlog 备份..."
# 获取当前 binlog 坐标
BINLOG_INFO=$(mysql_cli -N -e "SHOW MASTER STATUS\G" 2>>"${LOG_FILE}" || true)
if [[ -n "${BINLOG_INFO}" ]]; then
    BINLOG_FILE=$(echo "${BINLOG_INFO}" | awk '/File:/ {print $2}')
    BINLOG_POS=$(echo "${BINLOG_INFO}" | awk '/Position:/ {print $2}')
    log "当前 binlog 坐标: file=${BINLOG_FILE} position=${BINLOG_POS}"

    # 写入 binlog 元数据（恢复时使用）
    cat > "${BACKUP_PATH}/mysql_binlog.meta" <<EOF
# MySQL binlog 坐标（备份时刻）
# 备份类型: ${BACKUP_TYPE}
# 备份时间: $(date -Iseconds)
file=${BINLOG_FILE}
position=${BINLOG_POS}
database=${MYSQL_DATABASE}
EOF

    # 尝试通过 mysqlbinlog 拉取当前 binlog 文件
    # 注意：需要 REPLICATION SLAVE 权限。失败时记录告警，不中断主流程
    # 不使用 --stop-never（那是连续流模式，会一直阻塞）
    if [[ -n "${BINLOG_FILE}" ]]; then
        log "拉取 binlog 文件: ${BINLOG_FILE}"
        # --raw: 以原始二进制格式保存（而非解析为 SQL 文本）
        # --read-from-remote-server: 从远程 MySQL 服务器读取 binlog
        if mysqlbinlog_cli --read-from-remote-server --raw \
            --result-file="${BACKUP_PATH}/" \
            "${BINLOG_FILE}" 2>>"${LOG_FILE}" </dev/null; then
            log "binlog 拉取完成"
        else
            warn "binlog 拉取失败（可能缺少 REPLICATION SLAVE 权限），已记录元数据，跳过 binlog 文件复制"
        fi
    fi
else
    warn "无法获取 binlog 坐标（可能未开启 binlog 或权限不足），跳过 binlog 备份"
fi

# ----------------------------------------------------------------------------
# Step 3: Redis RDB 备份
# ----------------------------------------------------------------------------
log "Step 3: Redis RDB 备份..."
# 触发 BGSAVE（异步，立即返回）
if ! redis_cli BGSAVE >/dev/null 2>>"${LOG_FILE}"; then
    warn "Redis BGSAVE 触发失败（Redis 不可达或鉴权失败），跳过 RDB 备份"
else
    # 等待 BGSAVE 完成（轮询 rdb_bgsave_in_progress 字段）
    log "等待 Redis BGSAVE 完成..."
    WAIT_TIMEOUT=120
    WAIT_ELAPSED=0
    while (( WAIT_ELAPSED < WAIT_TIMEOUT )); do
        STATUS=$(redis_cli INFO persistence 2>/dev/null \
            | awk -F: '/^rdb_bgsave_in_progress/ {gsub(/\r/,"",$2); print $2}')
        if [[ "${STATUS}" == "0" ]]; then
            break
        fi
        sleep 2
        WAIT_ELAPSED=$(( WAIT_ELAPSED + 2 ))
    done

    if (( WAIT_ELAPSED >= WAIT_TIMEOUT )); then
        warn "Redis BGSAVE 超时（${WAIT_TIMEOUT}s），跳过 RDB 复制"
    else
        LAST_SAVE=$(redis_cli INFO persistence 2>/dev/null \
            | awk -F: '/^rdb_last_save_time/ {gsub(/\r/,"",$2); print $2}')
        log "Redis BGSAVE 完成 (last_save_time=${LAST_SAVE})"

        # 复制 RDB 文件
        if [[ "${USE_DOCKER}" == "true" ]]; then
            if docker cp "${REDIS_CONTAINER}:${REDIS_RDB_PATH_IN_CONTAINER}" \
                "${BACKUP_PATH}/redis_dump.rdb" 2>>"${LOG_FILE}"; then
                log "Redis RDB 复制完成: redis_dump.rdb"
            else
                warn "docker cp Redis RDB 失败"
            fi
        else
            # 直连模式：尝试常见 RDB 路径
            for rdb_path in /var/lib/redis/dump.rdb /data/dump.rdb ./dump.rdb; do
                if [[ -f "${rdb_path}" ]]; then
                    cp "${rdb_path}" "${BACKUP_PATH}/redis_dump.rdb"
                    log "Redis RDB 复制完成: ${rdb_path} -> redis_dump.rdb"
                    break
                fi
            done
            [[ -f "${BACKUP_PATH}/redis_dump.rdb" ]] || warn "未找到 Redis RDB 文件（直连模式需挂载 Redis 数据目录）"
        fi
    fi
fi

# ----------------------------------------------------------------------------
# Step 4: 写入备份元数据
# ----------------------------------------------------------------------------
cat > "${BACKUP_PATH}/manifest.txt" <<EOF
backup_name=${BACKUP_NAME}
backup_type=${BACKUP_TYPE}
backup_time=$(date -Iseconds)
backup_host=$(hostname)
mysql_host=${MYSQL_HOST}
mysql_port=${MYSQL_PORT}
mysql_database=${MYSQL_DATABASE}
redis_host=${REDIS_HOST}
redis_port=${REDIS_PORT}
use_docker=${USE_DOCKER}
retention_days=${RETENTION_DAYS}
EOF
log "已写入 manifest.txt"

# ----------------------------------------------------------------------------
# Step 5: 打包 + md5 校验
# ----------------------------------------------------------------------------
log "Step 5: 打包备份文件..."
tar -czf "${BACKUP_PATH}.tar.gz" -C "${BACKUP_DIR}" "${BACKUP_NAME}" 2>>"${LOG_FILE}" \
    || die "打包失败"

log "计算 md5 校验和..."
md5sum "${BACKUP_PATH}.tar.gz" > "${BACKUP_PATH}.tar.gz.md5"

ARCHIVE_SIZE=$(stat -c %s "${BACKUP_PATH}.tar.gz" 2>/dev/null || stat -f %z "${BACKUP_PATH}.tar.gz")
ARCHIVE_SIZE_MB=$(( ARCHIVE_SIZE / 1024 / 1024 ))
log "归档完成: ${BACKUP_PATH}.tar.gz (${ARCHIVE_SIZE_MB} MB)"

# ----------------------------------------------------------------------------
# Step 6: 清理临时目录
# ----------------------------------------------------------------------------
rm -rf "${BACKUP_PATH}"

# ----------------------------------------------------------------------------
# 完成
# ----------------------------------------------------------------------------
SUMMARY="备份成功: ${BACKUP_NAME} | 类型=${BACKUP_TYPE} | 大小=${ARCHIVE_SIZE_MB}MB | 磁盘剩余=${AVAILABLE_MB}MB"
notify "${SUMMARY}" "success"

# 移除 trap（避免误清理已完成的归档）
trap - EXIT

exit 0
