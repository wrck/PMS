#!/bin/bash
# =============================================================================
# PMS 备份清理脚本
#
# 清理过期备份文件，保留策略:
#   - 默认清理 ${RETENTION_DAYS} 天前的备份
#   - 保留每周一的备份（周备份）
#   - 保留每月 1 号的备份（月备份）
#   - 始终保留最近 N 天的备份（不论星期）
#
# 安全检查:
#   - 清理前校验至少有一个近期备份（避免误删所有备份）
#   - dry-run 模式（--dry-run）只打印不删除
# =============================================================================
set -euo pipefail

# ----------------------------------------------------------------------------
# 默认配置
# ----------------------------------------------------------------------------
BACKUP_DIR="${BACKUP_DIR:-/data/backups/pms}"
RETENTION_DAYS="${RETENTION_DAYS:-30}"
# 最近多少天内的备份不参与清理（即使不是周一/月初也保留）
KEEP_RECENT_DAYS="${KEEP_RECENT_DAYS:-7}"
WEBHOOK_URL="${WEBHOOK_URL:-}"
LOG_FILE="${BACKUP_DIR}/cleanup_$(date +%Y%m%d_%H%M%S).log"
DRY_RUN=false

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
    log "${message}"
    if [[ -n "${WEBHOOK_URL}" ]]; then
        curl -s -X POST "${WEBHOOK_URL}" \
            -H "Content-Type: application/json" \
            -d "{\"event\":\"backup_cleanup\",\"message\":\"$(echo "${message}" | sed 's/"/\\"/g')\"}" \
            >/dev/null 2>&1 || true
    fi
}

die() {
    error "$*"
    notify "备份清理失败: $*"
    exit 1
}

# 参数解析
while [[ $# -gt 0 ]]; do
    case "$1" in
        --dry-run) DRY_RUN=true; shift ;;
        --help|-h)
            cat <<EOF
用法: $0 [选项]
选项:
  --dry-run   仅打印将删除的文件，不实际删除
  --help      显示帮助
环境变量:
  BACKUP_DIR         备份目录 (默认 /data/backups/pms)
  RETENTION_DAYS     过期天数 (默认 30)
  KEEP_RECENT_DAYS   最近保留天数 (默认 7)
EOF
            exit 0
            ;;
        *) die "未知参数: $1" ;;
    esac
done

notify "开始备份清理 (目录=${BACKUP_DIR}, 过期=${RETENTION_DAYS}天, 保留最近=${KEEP_RECENT_DAYS}天, dry-run=${DRY_RUN})"

# ----------------------------------------------------------------------------
# 安全检查 1: 校验备份目录存在
# ----------------------------------------------------------------------------
if [[ ! -d "${BACKUP_DIR}" ]]; then
    die "备份目录不存在: ${BACKUP_DIR}"
fi

# ----------------------------------------------------------------------------
# 安全检查 2: 校验至少有一个近期备份（避免误删所有备份）
# ----------------------------------------------------------------------------
log "校验备份目录中是否有近期备份..."
# 注意：glob 无匹配时 ls 返回非零退出码，配合 set -e + pipefail 会中断脚本
# 使用 || true 兜底，再通过 -z 判断
# 按文件名排序（文件名含 YYYYMMDD 日期，字母序=时间序），比 mtime 更可靠
LATEST_BACKUP=$(ls -1 "${BACKUP_DIR}"/pms_*.tar.gz 2>/dev/null | sort -r | head -1 || true)
if [[ -z "${LATEST_BACKUP}" ]]; then
    die "未找到任何备份文件，跳过清理（避免误操作）"
fi

LATEST_BACKUP_FILENAME=$(basename "${LATEST_BACKUP}")
# 提取日期 pms_YYYYMMDD_HHMMSS.tar.gz
if [[ "${LATEST_BACKUP_FILENAME}" =~ pms_([0-9]{8})_[0-9]{6}\.tar\.gz ]]; then
    LATEST_DATE_STR="${BASH_REMATCH[1]}"
    LATEST_DATE="${LATEST_DATE_STR:0:4}-${LATEST_DATE_STR:4:2}-${LATEST_DATE_STR:6:2}"
    LATEST_TIMESTAMP=$(date -d "${LATEST_DATE}" +%s 2>/dev/null || echo 0)
    NOW_TIMESTAMP=$(date +%s)
    AGE_DAYS=$(( (NOW_TIMESTAMP - LATEST_TIMESTAMP) / 86400 ))
    log "最新备份: ${LATEST_BACKUP_FILENAME} (距今 ${AGE_DAYS} 天)"

    # 如果最新备份超过 7 天，发出警告
    if (( AGE_DAYS > 7 )); then
        warn "警告: 最新备份已超过 7 天 (${AGE_DAYS} 天)，备份任务可能异常！"
        warn "为安全起见，本次清理将仅清理 60 天前的备份"
        RETENTION_DAYS=60
    fi
else
    warn "无法解析备份文件名中的日期: ${LATEST_BACKUP_FILENAME}"
fi

# 统计变量
TOTAL_FILES=0
DELETED_COUNT=0
KEPT_COUNT=0
KEPT_RECENT=0
KEPT_WEEKLY=0
KEPT_MONTHLY=0
DELETED_SIZE=0

# ----------------------------------------------------------------------------
# 遍历备份文件
# ----------------------------------------------------------------------------
log "开始扫描备份文件..."
NOW_TIMESTAMP=$(date +%s)
CUTOFF_TIMESTAMP=$(( NOW_TIMESTAMP - RETENTION_DAYS * 86400 ))
RECENT_CUTOFF_TIMESTAMP=$(( NOW_TIMESTAMP - KEEP_RECENT_DAYS * 86400 ))

for file in "${BACKUP_DIR}"/pms_*.tar.gz; do
    [[ -f "${file}" ]] || continue   # 处理 glob 无匹配的情况
    TOTAL_FILES=$((TOTAL_FILES + 1))

    filename=$(basename "${file}")

    # 提取日期 pms_YYYYMMDD_HHMMSS.tar.gz
    if [[ ! "${filename}" =~ pms_([0-9]{8})_[0-9]{6}\.tar\.gz ]]; then
        warn "无法解析日期，跳过: ${filename}"
        continue
    fi

    date_str="${BASH_REMATCH[1]}"
    # 转换为 YYYY-MM-DD 格式
    year="${date_str:0:4}"
    month="${date_str:4:2}"
    day="${date_str:6:2}"
    iso_date="${year}-${month}-${day}"

    # 计算时间戳
    file_timestamp=$(date -d "${iso_date}" +%s 2>/dev/null || echo 0)
    if [[ "${file_timestamp}" == "0" ]]; then
        warn "日期解析失败，保留: ${filename}"
        KEPT_COUNT=$((KEPT_COUNT + 1))
        continue
    fi

    # 规则 1: 最近 KEEP_RECENT_DAYS 天内的备份始终保留
    if (( file_timestamp >= RECENT_CUTOFF_TIMESTAMP )); then
        log "保留 (近期): ${filename} (日期=${iso_date})"
        KEPT_RECENT=$((KEPT_RECENT + 1))
        KEPT_COUNT=$((KEPT_COUNT + 1))
        continue
    fi

    # 规则 2: 未过期的备份保留
    if (( file_timestamp >= CUTOFF_TIMESTAMP )); then
        log "保留 (未过期): ${filename} (日期=${iso_date})"
        KEPT_COUNT=$((KEPT_COUNT + 1))
        continue
    fi

    # 规则 3: 已过期，但检查是否为周一或月初
    day_of_week=$(date -d "${iso_date}" +%u 2>/dev/null || echo "0")
    day_of_month=$(date -d "${iso_date}" +%d 2>/dev/null || echo "00")

    if [[ "${day_of_week}" == "1" ]]; then
        log "保留 (周一): ${filename} (日期=${iso_date})"
        KEPT_WEEKLY=$((KEPT_WEEKLY + 1))
        KEPT_COUNT=$((KEPT_COUNT + 1))
        continue
    fi

    if [[ "${day_of_month}" == "01" ]]; then
        log "保留 (月初): ${filename} (日期=${iso_date})"
        KEPT_MONTHLY=$((KEPT_MONTHLY + 1))
        KEPT_COUNT=$((KEPT_COUNT + 1))
        continue
    fi

    # 规则 4: 已过期且非周一/月初 → 删除
    file_size=$(stat -c %s "${file}" 2>/dev/null || stat -f %z "${file}" 2>/dev/null || echo 0)
    DELETED_SIZE=$((DELETED_SIZE + file_size))

    if [[ "${DRY_RUN}" == "true" ]]; then
        log "[DRY-RUN] 将删除: ${filename} (日期=${iso_date}, 大小=$((file_size / 1024 / 1024))MB)"
    else
        log "删除: ${filename} (日期=${iso_date}, 大小=$((file_size / 1024 / 1024))MB)"
        rm -f "${file}" "${file}.md5"
        # 同时清理对应的备份日志文件
        rm -f "${BACKUP_DIR}/backup_pms_${date_str}_$(echo "${filename}" | sed -n 's/.*_\([0-9]*\)\.tar\.gz/\1/p').log" 2>/dev/null || true
    fi
    DELETED_COUNT=$((DELETED_COUNT + 1))
done

# ----------------------------------------------------------------------------
# 清理孤立的 .md5 和 .log 文件（无对应 .tar.gz）
# ----------------------------------------------------------------------------
log "清理孤立文件..."
ORPHAN_MD5=0
ORPHAN_LOG=0
for md5_file in "${BACKUP_DIR}"/pms_*.tar.gz.md5; do
    [[ -f "${md5_file}" ]] || continue
    tar_file="${md5_file%.md5}"
    if [[ ! -f "${tar_file}" ]]; then
        if [[ "${DRY_RUN}" == "true" ]]; then
            log "[DRY-RUN] 将删除孤立 md5: $(basename "${md5_file}")"
        else
            rm -f "${md5_file}"
        fi
        ORPHAN_MD5=$((ORPHAN_MD5 + 1))
    fi
done
for log_file in "${BACKUP_DIR}"/backup_pms_*.log; do
    [[ -f "${log_file}" ]] || continue
    # 检查是否有对应的 tar.gz
    log_basename=$(basename "${log_file}" .log)   # backup_pms_YYYYMMDD_HHMMSS
    backup_name="${log_basename#backup_}"          # pms_YYYYMMDD_HHMMSS
    if [[ ! -f "${BACKUP_DIR}/${backup_name}.tar.gz" ]]; then
        if [[ "${DRY_RUN}" == "true" ]]; then
            log "[DRY-RUN] 将删除孤立日志: $(basename "${log_file}")"
        else
            rm -f "${log_file}"
        fi
        ORPHAN_LOG=$((ORPHAN_LOG + 1))
    fi
done

# ----------------------------------------------------------------------------
# 汇总
# ----------------------------------------------------------------------------
DELETED_SIZE_MB=$(( DELETED_SIZE / 1024 / 1024 ))
SUMMARY="清理完成: 扫描=${TOTAL_FILES} 删除=${DELETED_COUNT} 保留=${KEPT_COUNT} (近期=${KEPT_RECENT}, 周一=${KEPT_WEEKLY}, 月初=${KEPT_MONTHLY}) 释放=${DELETED_SIZE_MB}MB 孤立md5=${ORPHAN_MD5} 孤立log=${ORPHAN_LOG} dry-run=${DRY_RUN}"
notify "${SUMMARY}"

# 显示当前剩余备份列表（按文件名倒序，最新的在前）
log "剩余备份文件:"
REMAINING=$(ls -1 "${BACKUP_DIR}"/pms_*.tar.gz 2>/dev/null | sort -r || true)
if [[ -n "${REMAINING}" ]]; then
    echo "${REMAINING}" | sed 's/^/  /' | tee -a "${LOG_FILE}"
else
    log "  (无)"
fi

exit 0
