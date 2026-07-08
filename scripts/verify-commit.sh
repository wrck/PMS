#!/bin/bash
# 提交级验证脚本（双重门禁）— RULES.md 支柱二 §2.3
# 版本：1.3.0（插件校验和校验 + 超时隔离 + criticality/on_timeout 策略）
#
# 用法：
#   子代理侧（提交前）：bash scripts/verify-commit.sh --pre
#   主代理侧（合并前）：bash scripts/verify-commit.sh <commit-hash>
#   生成 [CHANGES] 标签：bash scripts/verify-commit.sh --changes [commit-hash]
#   调试模式（跳过插件）：bash scripts/verify-commit.sh --pre --skip-plugins
#
# 退出码约定（RULES.md §2.3.2）：
#   0 = 通过
#   1 = 编译失败 / critical 插件失败或超时（子代理自动修复重试）
#   2 = 文件越权（子代理回退越权文件后重试）
#   3 = Hash 丢失（主代理按 §4.1 SOP 恢复）
set -uo pipefail
# 注意：不使用 -e，因为我们要捕获各步骤退出码并统一返回约定码

VERIFY_VERSION="1.3.0"
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

# === 从 PLUGIN_REGISTRY.md 解析插件配置（RULES.md §5.6）===
# 用法：get_plugin_config <plugin-name> <field>
# field: timeout_sec | on_timeout | criticality | checksum
# 返回：配置值（字符串），未找到返回空
get_plugin_config() {
    local plugin_name="$1" field="$2"
    local registry="scripts/verify-plugins/PLUGIN_REGISTRY.md"

    [ -f "$registry" ] || return 0

    # 解析表格行：| `filename` | version | author | desc | date | timeout_sec | on_timeout | criticality | checksum |
    local line
    line=$(grep -E "^\| \`$plugin_name\`" "$registry" 2>/dev/null | head -1)
    [ -z "$line" ] && return 0

    # 按字段位置提取
    case "$field" in
        timeout_sec) echo "$line" | awk -F'|' '{gsub(/ /,"",$7); print $7}' ;;
        on_timeout)  echo "$line" | awk -F'|' '{gsub(/ /,"",$8); print $8}' ;;
        criticality) echo "$line" | awk -F'|' '{gsub(/ /,"",$9); print $9}' ;;
        checksum)    echo "$line" | awk -F'|' '{gsub(/ /,"",$10); print $10}' ;;
    esac
}

# === 插件加载（RULES.md 支柱五 §5.1 + §5.6 校验和 + §5.7 超时隔离）===
# 用法：run_plugins <mode> <hash> <skip_flag>
# 返回：0=全部通过/无插件/仅 optional 失败，1=有 critical 插件失败或超时
run_plugins() {
    local mode="$1" hash="$2" skip="$3"
    local plugin_dir="scripts/verify-plugins"

    log "==[Plugins] 扩展验证插件=="

    # 调试模式跳过插件
    if [ "$skip" = "true" ]; then
        log "SKIP: --skip-plugins 已启用（仅限调试，正式提交禁止使用）"
        return 0
    fi

    # 插件目录不存在或为空：不阻断核心验证
    if [ ! -d "$plugin_dir" ]; then
        log "无插件目录，跳过插件验证"
        return 0
    fi

    local plugins=()
    while IFS= read -r p; do
        plugins+=("$p")
    done < <(find "$plugin_dir" -name '*.sh' -type f | sort)

    if [ ${#plugins[@]} -eq 0 ]; then
        log "无插件，跳过"
        return 0
    fi

    local critical_failed=0
    local warnings=""

    for plugin in "${plugins[@]}"; do
        local name=$(basename "$plugin")

        # 1. 校验和校验（RULES.md §5.6）
        local expected_checksum actual_checksum
        expected_checksum=$(get_plugin_config "$name" "checksum")
        if [ -n "$expected_checksum" ]; then
            actual_checksum=$(sha256sum "$plugin" | awk '{print $1}')
            if [ "$expected_checksum" != "$actual_checksum" ]; then
                log "PLUGIN $name: FAIL (校验和不匹配)"
                log "  期望值: $expected_checksum"
                log "  实际值: $actual_checksum"
                log "  修复: 重新生成校验和并原子性更新 PLUGIN_REGISTRY.md（见 §5.6）"
                critical_failed=1
                continue
            fi
            log "PLUGIN $name 校验和: PASS"
        else
            log "WARN: $name 在 PLUGIN_REGISTRY.md 中无校验和记录，跳过校验和校验"
        fi

        # 2. 解析配置
        local timeout_sec on_timeout criticality
        timeout_sec=$(get_plugin_config "$name" "timeout_sec")
        on_timeout=$(get_plugin_config "$name" "on_timeout")
        criticality=$(get_plugin_config "$name" "criticality")
        timeout_sec=${timeout_sec:-30}
        on_timeout=${on_timeout:-fail}
        criticality=${criticality:-critical}

        # 3. 执行插件（带超时）
        log "RUN PLUGIN: $name (timeout=${timeout_sec}s, on_timeout=${on_timeout}, criticality=${criticality})"
        local plugin_output
        local exit_code=0
        plugin_output=$(timeout "$timeout_sec" bash "$plugin" "$mode" "$hash" 2>&1) || exit_code=$?
        echo "$plugin_output" | tee -a "$LOG_FILE" >/dev/null

        if [ $exit_code -eq 0 ]; then
            log "PLUGIN $name: PASS"
        elif [ $exit_code -eq 124 ]; then
            # 超时
            log "PLUGIN $name: TIMEOUT (超过 ${timeout_sec}s)"
            if [ "$on_timeout" = "fail" ]; then
                if [ "$criticality" = "critical" ]; then
                    log "  → critical + on_timeout=fail: 中断验证"
                    critical_failed=1
                else
                    log "  → optional + on_timeout=fail: 中断验证"
                    critical_failed=1
                fi
            else
                # on_timeout=warn
                log "  → on_timeout=warn: 降级警告（继续执行）"
                warnings="$warnings $name(TIMEOUT)"
            fi
        else
            # 插件失败
            log "PLUGIN $name: FAIL (exit $exit_code)"
            if [ "$criticality" = "critical" ]; then
                log "  → criticality=critical: 中断验证"
                critical_failed=1
            else
                log "  → criticality=optional: 降级警告（继续执行）"
                warnings="$warnings $name(FAIL)"
            fi
        fi
    done

    # 汇总
    if [ -n "$warnings" ]; then
        log "PLUGINS WARNINGS:$warnings（optional 插件，已降级警告）"
    fi

    if [ $critical_failed -ne 0 ]; then
        log "SOME CRITICAL PLUGINS FAILED"
        return 1
    elif [ -n "$warnings" ]; then
        log "ALL CRITICAL PLUGINS PASSED (with warnings)"
        return 0
    else
        log "ALL PLUGINS PASSED"
        return 0
    fi
}

# === 主流程 ===
main() {
    # 解析参数：支持 --pre / <hash> / --changes / --skip-plugins
    local mode="${1:---pre}"
    local hash=""
    local skip_plugins="false"

    # 解析可选参数
    shift || true
    while [ $# -gt 0 ]; do
        case "$1" in
            --skip-plugins)
                skip_plugins="true"
                shift
                ;;
            *)
                if [ -z "$hash" ] && [ "$mode" != "--changes" ]; then
                    hash="$1"
                fi
                shift
                ;;
        esac
    done

    log "========================================"
    log "verify-commit.sh v${VERIFY_VERSION}"
    log "Mode: $mode, Hash: ${hash:-N/A}, SkipPlugins: $skip_plugins"
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

    # 检查 5：插件验证（支柱五，任一失败则整体失败）
    run_plugins "$mode" "$hash" "$skip_plugins"
    rc=$?
    [ $rc -ne 0 ] && { log "RESULT: FAIL (exit 1 - 插件验证失败)"; exit 1; }

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
