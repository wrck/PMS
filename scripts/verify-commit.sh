#!/bin/bash
# 提交级验证脚本（双重门禁）— RULES.md 支柱二 §2.3
# 版本：1.1.0（新增 [CHANGES] 标签自动注入 §2.5）
#
# 用法：
#   子代理侧（提交前）：bash scripts/verify-commit.sh --pre
#   主代理侧（合并前）：bash scripts/verify-commit.sh <commit-hash>
#   生成 [CHANGES] 标签：bash scripts/verify-commit.sh --changes [commit-hash]
#
# 退出码约定（RULES.md §2.3.2）：
#   0 = 通过
#   1 = 编译失败（子代理自动修复重试）
#   2 = 文件越权（子代理回退越权文件后重试）
#   3 = Hash 丢失（主代理按 §4.1 SOP 恢复）
set -uo pipefail
# 注意：不使用 -e，因为我们要捕获各步骤退出码并统一返回约定码

VERIFY_VERSION="1.1.0"
LOG_DIR="scripts/logs"
mkdir -p "$LOG_DIR" 2>/dev/null || true
TIMESTAMP=$(date +%Y%m%d-%H%M%S)
LOG_FILE="$LOG_DIR/verify-commit-${TIMESTAMP}.log"

# 受保护文件黑名单（子代理禁止修改）
PROTECTED_PATHS=(
    "scripts/verify.sh"
    "scripts/verify-commit.sh"
    "scripts/verify.sha256"
    "scripts/verify-commit.sha256"
    "RULES.md"
    "AGENTS.md"
)

log() { echo "[$(date +%H:%M:%S)] $*" | tee -a "$LOG_FILE"; }

# === 完整性自校验（RULES.md §2.4.2）===
self_check() {
    local script_path="$0" expected actual
    local sha_file="${script_path%.sh}.sha256"
    if [ -f "$sha_file" ]; then
        expected=$(awk '{print $1}' "$sha_file")
        actual=$(sha256sum "$script_path" | awk '{print $1}')
        if [ "$expected" != "$actual" ]; then
            log "FAIL: verify-commit.sh 完整性校验失败（可能被篡改）"
            log "Expected: $expected"
            log "Actual:   $actual"
            exit 1
        fi
        log "完整性校验: PASS"
    else
        log "WARN: sha256 文件不存在，跳过完整性校验（首次运行或开发模式）"
    fi
}

# === 检查 1：Hash 存在性（主代理侧）===
check_hash() {
    local hash="$1"
    log "==[Check 1] Hash 存在性检查=="
    if [ -z "$hash" ] || [ "$hash" = "--pre" ]; then
        log "SKIP: 子代理 --pre 模式，跳过 Hash 检查"
        return 0
    fi
    if git cat-file -t "$hash" >/dev/null 2>&1; then
        log "Hash $hash 存在: PASS"
        return 0
    else
        log "FAIL: Hash $hash 不存在（Hash 丢失）"
        return 3
    fi
}

# === 检查 2：变更文件范围校验（防越权）===
check_file_scope() {
    local mode="$1" hash="$2"
    log "==[Check 2] 变更文件范围校验=="

    local files=""
    if [ "$mode" = "--pre" ]; then
        # 子代理侧：检查暂存区 + 工作区的变更
        files=$(git status --porcelain | awk '{print $2}')
    else
        # 主代理侧：检查该 commit 相对其父的变更
        files=$(git diff-tree --no-commit-id --name-only -r "$hash" 2>/dev/null)
    fi

    if [ -z "$files" ]; then
        log "WARN: 未检测到变更文件"
        return 0
    fi

    local violations=""
    while IFS= read -r f; do
        [ -z "$f" ] && continue
        for protected in "${PROTECTED_PATHS[@]}"; do
            if [ "$f" = "$protected" ]; then
                violations="$violations\n  - $f（受保护资产）"
            fi
        done
    done <<< "$files"

    if [ -n "$violations" ]; then
        log "FAIL: 检测到越权文件修改："
        log -e "$violations"
        log "修复：git reset HEAD <越权文件> && git checkout -- <越权文件>"
        return 2
    fi
    log "文件范围校验: PASS"
    return 0
}

# === 检查 3：编译/类型检查 ===
check_compile() {
    local mode="$1"
    log "==[Check 3] 编译/类型检查=="

    # 确定工作目录
    local wt_root
    wt_root=$(git rev-parse --show-toplevel 2>/dev/null)
    if [ -z "$wt_root" ]; then
        log "FAIL: 不在 git 仓库内"
        return 1
    fi

    # 后端编译（仅当存在 Java 改动时）
    local has_java=0
    if [ "$mode" = "--pre" ]; then
        git status --porcelain | grep -E '\.(java|xml)$' >/dev/null 2>&1 && has_java=1
    else
        git diff-tree --no-commit-id --name-only -r "$2" 2>/dev/null | grep -E '\.(java|xml)$' >/dev/null 2>&1 && has_java=1
    fi

    if [ "$has_java" = "1" ]; then
        log "检测到 Java/XML 改动，运行 mvn compile..."
        if [ -f "$wt_root/network-equipment-pms/pom.xml" ]; then
            (cd "$wt_root/network-equipment-pms" && mvn compile -q 2>&1) | tee -a "$LOG_FILE"
            if [ "${PIPESTATUS[0]}" -ne 0 ]; then
                log "FAIL: 后端编译失败"
                return 1
            fi
            log "后端编译: PASS"
        else
            log "WARN: 未找到 network-equipment-pms/pom.xml，跳过后端编译"
        fi
    else
        log "无 Java/XML 改动，跳过后端编译"
    fi

    # 前端类型检查（仅当存在 TS/Vue 改动时）
    local has_frontend=0
    if [ "$mode" = "--pre" ]; then
        git status --porcelain | grep -E '\.(ts|vue|tsx)$' >/dev/null 2>&1 && has_frontend=1
    else
        git diff-tree --no-commit-id --name-only -r "$2" 2>/dev/null | grep -E '\.(ts|vue|tsx)$' >/dev/null 2>&1 && has_frontend=1
    fi

    if [ "$has_frontend" = "1" ]; then
        log "检测到 TS/Vue 改动，运行 vue-tsc --noEmit..."
        if [ -d "$wt_root/network-equipment-pms/pms-frontend" ]; then
            (cd "$wt_root/network-equipment-pms/pms-frontend" && npx vue-tsc --noEmit 2>&1) | tee -a "$LOG_FILE"
            if [ "${PIPESTATUS[0]}" -ne 0 ]; then
                log "FAIL: 前端类型检查失败"
                return 1
            fi
            log "前端类型检查: PASS"
        else
            log "WARN: 未找到 pms-frontend 目录，跳过前端类型检查"
        fi
    else
        log "无 TS/Vue 改动，跳过前端类型检查"
    fi

    return 0
}

# === 检查 4：Lint 检查（可选，仅前端有 eslint 配置时）===
check_lint() {
    local mode="$1"
    log "==[Check 4] Lint 检查=="

    local wt_root
    wt_root=$(git rev-parse --show-toplevel 2>/dev/null)
    local eslint_config="$wt_root/network-equipment-pms/pms-frontend/.eslintrc.*"
    if ! ls $eslint_config >/dev/null 2>&1; then
        log "无 eslint 配置，跳过 Lint 检查"
        return 0
    fi

    local has_frontend=0
    if [ "$mode" = "--pre" ]; then
        git status --porcelain | grep -E '\.(ts|vue|tsx)$' >/dev/null 2>&1 && has_frontend=1
    else
        git diff-tree --no-commit-id --name-only -r "$2" 2>/dev/null | grep -E '\.(ts|vue|tsx)$' >/dev/null 2>&1 && has_frontend=1
    fi

    if [ "$has_frontend" = "1" ]; then
        log "运行 eslint..."
        (cd "$wt_root/network-equipment-pms/pms-frontend" && npx eslint --max-warnings 0 src/ 2>&1) | tee -a "$LOG_FILE" || {
            log "WARN: eslint 检测到警告/错误（不阻断，仅提示）"
        }
        log "Lint 检查: 完成（警告不阻断）"
    else
        log "无 TS/Vue 改动，跳过 Lint 检查"
    fi
    return 0
}

# === 生成 [CHANGES] 标签（RULES.md §2.5 自动注入）===
# 用法：generate_changes_tag [--pre | <commit-hash>]
# 输出：[CHANGES: 5 files, +123 -45]
generate_changes_tag() {
    local mode="$1" hash="${2:-}"
    local file_count add del

    if [ "$mode" = "--pre" ]; then
        # 子代理侧：统计暂存区 + 工作区的变更
        local stats
        stats=$(git diff --shortstat 2>/dev/null; git diff --cached --shortstat 2>/dev/null)
        # 解析 "X files changed, Y insertions(+), Z deletions(-)"
        file_count=$(echo "$stats" | grep -oP '\d+(?= file)' | awk '{s+=$1} END{print s+0}')
        add=$(echo "$stats" | grep -oP '\d+(?= insertion)' | awk '{s+=$1} END{print s+0}')
        del=$(echo "$stats" | grep -oP '\d+(?= deletion)' | awk '{s+=$1} END{print s+0}')
    else
        # 主代理侧：统计该 commit 相对其父的变更
        local stats
        stats=$(git diff --shortstat "$hash^..$hash" 2>/dev/null || git show --shortstat "$hash" 2>/dev/null | grep 'file')
        file_count=$(echo "$stats" | grep -oP '\d+(?= file)' | head -1)
        add=$(echo "$stats" | grep -oP '\d+(?= insertion)' | head -1)
        del=$(echo "$stats" | grep -oP '\d+(?= deletion)' | head -1)
    fi

    file_count=${file_count:-0}
    add=${add:-0}
    del=${del:-0}
    echo "[CHANGES: ${file_count} files, +${add} -${del}]"
}

# === 主流程 ===
main() {
    local mode="${1:---pre}"
    local hash="${2:-}"

    log "========================================"
    log "verify-commit.sh v${VERIFY_VERSION}"
    log "Mode: $mode, Hash: ${hash:-N/A}"
    log "Log: $LOG_FILE"
    log "========================================"

    # 独立模式：仅生成 [CHANGES] 标签（RULES.md §2.5 自动注入）
    if [ "$mode" = "--changes" ]; then
        log "生成 [CHANGES] 标签..."
        # 如果提供了 hash，用 hash 模式；否则用 --pre 模式
        if [ -n "$hash" ]; then
            generate_changes_tag "hash" "$hash"
        else
            generate_changes_tag "--pre"
        fi
        exit 0
    fi

    self_check

    # 检查 1：Hash 存在性（仅主代理侧）
    check_hash "$hash"
    local rc=$?
    [ $rc -eq 3 ] && { log "RESULT: FAIL (exit 3 - Hash 丢失)"; exit 3; }

    # 检查 2：文件范围校验
    check_file_scope "$mode" "$hash"
    rc=$?
    [ $rc -eq 2 ] && { log "RESULT: FAIL (exit 2 - 文件越权)"; exit 2; }

    # 检查 3：编译/类型检查
    check_compile "$mode" "$hash"
    rc=$?
    [ $rc -eq 1 ] && { log "RESULT: FAIL (exit 1 - 编译失败)"; exit 1; }

    # 检查 4：Lint 检查（可选，不阻断）
    check_lint "$mode" "$hash"

    # 生成 [CHANGES] 标签（--pre 通过后自动输出，供子代理注入 commit message）
    local changes_tag
    changes_tag=$(generate_changes_tag "$mode" "$hash")
    log "CHANGES_TAG: $changes_tag"

    log "========================================"
    log "RESULT: ALL CHECKS PASSED (exit 0)"
    log "$changes_tag"
    log "========================================"
    exit 0
}

main "$@"
