#!/bin/bash
# 提交级验证脚本（双重门禁）— RULES.md 支柱二 §2.3
# 版本：1.4.0（插件依赖拓扑排序 + JSON 标准化输出 + 合并验证报告）
#
# 用法：
#   子代理侧（提交前）：bash scripts/verify-commit.sh --pre
#   主代理侧（合并前）：bash scripts/verify-commit.sh <commit-hash>
#   生成 [CHANGES] 标签：bash scripts/verify-commit.sh --changes [commit-hash]
#   调试模式（跳过插件）：bash scripts/verify-commit.sh --pre --skip-plugins
#
# 退出码约定（RULES.md §2.3.2）：
#   0 = 通过
#   1 = 编译失败 / critical 插件失败或超时 / 循环依赖（子代理自动修复重试）
#   2 = 文件越权（子代理回退越权文件后重试）
#   3 = Hash 丢失（主代理按 §4.1 SOP 恢复）
set -uo pipefail
# 注意：不使用 -e，因为我们要捕获各步骤退出码并统一返回约定码

VERIFY_VERSION="1.4.0"
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

# === 从 PLUGIN_REGISTRY.md 解析插件配置（RULES.md §5.6 + §5.9）===
# 用法：get_plugin_config <plugin-name> <field>
# field: version | timeout_sec | on_timeout | criticality | depends_on | checksum
# 返回：配置值（字符串），未找到返回空
#
# 表格列布局（PLUGIN_REGISTRY.md）：
#   $2=文件名 $3=版本 $4=作者 $5=用途 $6=批准日期
#   $7=timeout_sec $8=on_timeout $9=criticality $10=depends_on $11=checksum
get_plugin_config() {
    local plugin_name="$1" field="$2"
    local registry="scripts/verify-plugins/PLUGIN_REGISTRY.md"

    [ -f "$registry" ] || return 0

    local line
    line=$(grep -E "^\| \`$plugin_name\`" "$registry" 2>/dev/null | head -1)
    [ -z "$line" ] && return 0

    case "$field" in
        version)     echo "$line" | awk -F'|' '{gsub(/^ +| +$/,"",$3); print $3}' ;;
        timeout_sec) echo "$line" | awk -F'|' '{gsub(/^ +| +$/,"",$7); print $7}' ;;
        on_timeout)  echo "$line" | awk -F'|' '{gsub(/^ +| +$/,"",$8); print $8}' ;;
        criticality) echo "$line" | awk -F'|' '{gsub(/^ +| +$/,"",$9); print $9}' ;;
        depends_on)  echo "$line" | awk -F'|' '{gsub(/^ +| +$/,"",$10); print $10}' ;;
        checksum)    echo "$line" | awk -F'|' '{gsub(/^ +| +$/,"",$11); print $11}' ;;
    esac
}

# === 拓扑排序 + 循环依赖检测（RULES.md §5.9.2 + §5.9.3）===
# 输入：插件名列表（位置参数）
# 输出：拓扑排序后的插件名（每行一个）到 stdout
# 失败：输出 "CYCLE: <空格分隔的循环节点>" 到 stdout，返回 1
# 算法：Kahn 算法 — 入度为 0 的节点先入队，逐步移除并降低依赖者入度
#       同层（入度同为 0）按文件名字母序处理，保证稳定
topo_sort_plugins() {
    local plugins=("$@")
    [ ${#plugins[@]} -eq 0 ] && return 0

    declare -A indeg deps_str

    # 初始化：解析每个插件的 depends_on
    for p in "${plugins[@]}"; do
        local d
        d=$(get_plugin_config "$p" "depends_on")
        d=${d:-"-"}
        deps_str[$p]="$d"
        indeg[$p]=0
    done

    # 计算入度：仅统计依赖中确实存在于插件列表的项
    for p in "${plugins[@]}"; do
        [ "${deps_str[$p]}" = "-" ] && continue
        IFS=',' read -ra dep_arr <<< "${deps_str[$p]}"
        for d in "${dep_arr[@]}"; do
            d=$(echo "$d" | xargs)  # 去首尾空白
            [ -z "$d" ] && continue
            for q in "${plugins[@]}"; do
                if [ "$d" = "$q" ]; then
                    indeg[$p]=$((indeg[$p] + 1))
                    break
                fi
            done
        done
    done

    # Kahn 算法
    local queue=()
    for p in "${plugins[@]}"; do
        [ "${indeg[$p]}" -eq 0 ] && queue+=("$p")
    done

    local sorted=()
    while [ ${#queue[@]} -gt 0 ]; do
        # 同层按字母序取第一个（稳定性）
        IFS=$'\n' local sorted_q=($(printf '%s\n' "${queue[@]}" | sort))
        unset IFS
        local node="${sorted_q[0]}"
        sorted+=("$node")
        # 从队列移除
        queue=("${sorted_q[@]:1}")

        # 降低依赖该节点的插件入度
        for p in "${plugins[@]}"; do
            [ "${deps_str[$p]}" = "-" ] && continue
            IFS=',' read -ra dep_arr <<< "${deps_str[$p]}"
            for d in "${dep_arr[@]}"; do
                d=$(echo "$d" | xargs)
                if [ "$d" = "$node" ]; then
                    indeg[$p]=$((indeg[$p] - 1))
                    [ "${indeg[$p]}" -eq 0 ] && queue+=("$p")
                    break
                fi
            done
        done
    done

    # 循环依赖检测：排序后节点数少于原始数则存在环
    if [ ${#sorted[@]} -ne ${#plugins[@]} ]; then
        local cyclic=()
        for p in "${plugins[@]}"; do
            local found=0
            for s in "${sorted[@]}"; do
                [ "$p" = "$s" ] && { found=1; break; }
            done
            [ "$found" -eq 0 ] && cyclic+=("$p")
        done
        echo "CYCLE: ${cyclic[*]}"
        return 1
    fi

    printf '%s\n' "${sorted[@]}"
    return 0
}

# === 解析插件 JSON 输出（RULES.md §5.10.1 + §5.10.3 兼容性）===
# 输入：$1=插件完整 stdout，$2=插件名，$3=退出码
# 输出：合法 JSON 对象到 stdout（始终单行）
# 行为：优先提取插件输出的最后一行以 `{` 开头的 JSON；非法或缺失时按退出码自动生成
parse_plugin_json() {
    local output="$1" plugin_name="$2" exit_code="$3"

    # 尝试提取最后一行 JSON
    local json_line
    json_line=$(echo "$output" | grep -E '^[[:space:]]*\{' | tail -1 | sed -E 's/^[[:space:]]+//')

    if [ -n "$json_line" ]; then
        # 校验 JSON 合法性
        if echo "$json_line" | jq -e . >/dev/null 2>&1; then
            # 确保必要字段存在
            echo "$json_line" | jq -c --arg p "$plugin_name" \
                '{plugin: (.plugin // $p), status: .status, message: (.message // ""), metrics: (.metrics // {})}'
            return 0
        fi
    fi

    # 兼容模式：插件未输出合法 JSON，按退出码自动生成
    local status message
    case "$exit_code" in
        0)   status="pass";    message="验证通过" ;;
        1)   status="fail";    message="验证失败" ;;
        124) status="timeout"; message="执行超时" ;;
        *)   status="fail";    message="异常退出 (exit $exit_code)" ;;
    esac
    jq -c -n \
        --arg p "$plugin_name" \
        --arg s "$status" \
        --arg m "$message" \
        --argjson ec "$exit_code" \
        '{plugin: $p, status: $s, message: $m, metrics: {exit_code: $ec}}'
}

# === 生成合并验证报告（RULES.md §5.10.4 + §5.10.5）===
# 输入：$1=报告文件路径，$2=mode，$3=hash，$4=插件结果 JSONL 文件
# 输出：写入合并报告 JSON 到文件
generate_verify_report() {
    local report_file="$1" mode="$2" hash="$3" results_file="$4"

    # 统计各状态计数（grep -c 无匹配时输出 "0" 并退出 1，故用 wc -l 方式避免双重输出）
    local total=0 passed=0 failed=0 warned=0 timeout=0 skipped=0
    if [ -f "$results_file" ]; then
        total=$(wc -l < "$results_file" | awk '{print $1}')
        passed=$(grep '"status":"pass"' "$results_file" 2>/dev/null | wc -l)
        failed=$(grep '"status":"fail"' "$results_file" 2>/dev/null | wc -l)
        warned=$(grep '"status":"warn"' "$results_file" 2>/dev/null | wc -l)
        timeout=$(grep '"status":"timeout"' "$results_file" 2>/dev/null | wc -l)
        skipped=$(grep '"status":"skipped"' "$results_file" 2>/dev/null | wc -l)
    fi

    # 整体状态判定：fail 或 timeout 出现即整体 fail
    local overall="pass" exit_code=0
    if [ "$failed" -gt 0 ] || [ "$timeout" -gt 0 ]; then
        overall="fail"
        exit_code=1
    fi

    # 构建 plugins 数组（每个 JSON 对象作为数组元素）
    local plugins_json="[]"
    if [ -f "$results_file" ] && [ -s "$results_file" ]; then
        # 将每行 JSON 合并为数组
        plugins_json=$(jq -s '.' "$results_file" 2>/dev/null || echo '[]')
    fi

    # 生成报告
    jq -n \
        --arg report_version "1.0.0" \
        --arg timestamp "$(date -u +%Y-%m-%dT%H:%M:%SZ)" \
        --arg verify_version "$VERIFY_VERSION" \
        --arg mode "$mode" \
        --arg hash "$hash" \
        --argjson total "$total" \
        --argjson passed "$passed" \
        --argjson failed "$failed" \
        --argjson warned "$warned" \
        --argjson timeout "$timeout" \
        --argjson skipped "$skipped" \
        --arg overall "$overall" \
        --argjson exit_code "$exit_code" \
        --argjson plugins "$plugins_json" \
        '{
            report_version: $report_version,
            timestamp: $timestamp,
            verify_version: $verify_version,
            mode: $mode,
            commit_hash: (if $hash == "" then null else $hash end),
            summary: {
                total_plugins: $total,
                passed: $passed,
                failed: $failed,
                warned: $warned,
                timeout: $timeout,
                skipped: $skipped
            },
            plugins: $plugins,
            overall_status: $overall,
            exit_code: $exit_code
        }' > "$report_file"
}

# === 插件加载（支柱五 §5.1 + §5.6 校验和 + §5.7 超时隔离 + §5.9 拓扑排序 + §5.10 JSON 报告）===
# 用法：run_plugins <mode> <hash> <skip_flag>
# 返回：0=全部通过/无插件/仅 optional 失败，1=有 critical 插件失败/超时/循环依赖
run_plugins() {
    local mode="$1" hash="$2" skip="$3"
    local plugin_dir="scripts/verify-plugins"

    log "==[Plugins] 扩展验证插件（v1.4.0 拓扑排序+JSON报告）=="

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

    # 收集插件文件名（按字母序）
    local plugin_names=()
    while IFS= read -r p; do
        [ -n "$p" ] && plugin_names+=("$(basename "$p")")
    done < <(find "$plugin_dir" -name '*.sh' -type f | sort)

    if [ ${#plugin_names[@]} -eq 0 ]; then
        log "无插件，跳过"
        return 0
    fi

    # === 拓扑排序 + 循环依赖检测（RULES.md §5.9.2 + §5.9.3）===
    log "拓扑排序插件依赖..."
    local sorted_output
    sorted_output=$(topo_sort_plugins "${plugin_names[@]}")
    local topo_rc=$?

    if [ $topo_rc -ne 0 ]; then
        log "PLUGIN 依赖图检测: FAIL (循环依赖)"
        log "  $sorted_output"
        log "  修复: 检查 PLUGIN_REGISTRY.md 中 depends_on 声明，消除循环引用"
        log "RESULT: FAIL (exit 1 - 循环依赖)"
        return 1
    fi

    # 转为数组
    local sorted_plugins=()
    while IFS= read -r p; do
        [ -n "$p" ] && sorted_plugins+=("$p")
    done <<< "$sorted_output"

    log "执行顺序: ${sorted_plugins[*]}"

    # === 准备 JSON 结果收集文件（RULES.md §5.10.4）===
    local plugin_results_file="$LOG_DIR/plugin-results-${TIMESTAMP}.jsonl"
    : > "$plugin_results_file"

    # 状态跟踪（用于依赖失败传播）
    declare -A plugin_status plugin_critical_failed

    local critical_failed=0
    local warnings=""

    for name in "${sorted_plugins[@]}"; do
        local plugin_path="$plugin_dir/$name"

        # === 依赖失败传播检查（RULES.md §5.9.4）===
        local deps_str
        deps_str=$(get_plugin_config "$name" "depends_on")
        deps_str=${deps_str:-"-"}

        if [ "$deps_str" != "-" ]; then
            local skip_this=0
            local skip_dep=""
            IFS=',' read -ra dep_arr <<< "$deps_str"
            for d in "${dep_arr[@]}"; do
                d=$(echo "$d" | xargs)
                [ -z "$d" ] && continue
                local dep_critical="${plugin_critical_failed[$d]:-0}"
                if [ "$dep_critical" = "1" ]; then
                    skip_this=1
                    skip_dep="$d"
                    break
                fi
            done
            if [ "$skip_this" = "1" ]; then
                log "PLUGIN $name: SKIPPED (dependency $skip_dep failed/timeout)"
                local json
                json=$(jq -c -n \
                    --arg p "$name" \
                    --arg dep "$skip_dep" \
                    '{plugin: $p, status: "skipped", message: ("依赖插件 " + $dep + " 失败，跳过执行"), metrics: {dependency: $dep}}')
                echo "$json" >> "$plugin_results_file"
                plugin_status[$name]="skipped"
                plugin_critical_failed[$name]=0
                continue
            fi
        fi

        # === 校验和校验（RULES.md §5.6）===
        local expected_checksum actual_checksum
        expected_checksum=$(get_plugin_config "$name" "checksum")
        if [ -n "$expected_checksum" ]; then
            actual_checksum=$(sha256sum "$plugin_path" | awk '{print $1}')
            if [ "$expected_checksum" != "$actual_checksum" ]; then
                log "PLUGIN $name: FAIL (校验和不匹配)"
                log "  期望值: $expected_checksum"
                log "  实际值: $actual_checksum"
                log "  修复: 重新生成校验和并原子性更新 PLUGIN_REGISTRY.md（见 §5.6）"
                local json
                json=$(jq -c -n \
                    --arg p "$name" \
                    --arg exp "$expected_checksum" \
                    --arg act "$actual_checksum" \
                    '{plugin: $p, status: "fail", message: "校验和不匹配（插件可能被篡改）", metrics: {expected: $exp, actual: $act}}')
                echo "$json" >> "$plugin_results_file"
                plugin_status[$name]="fail"
                plugin_critical_failed[$name]=1
                critical_failed=1
                continue
            fi
            log "PLUGIN $name 校验和: PASS"
        else
            log "WARN: $name 在 PLUGIN_REGISTRY.md 中无校验和记录，跳过校验和校验"
        fi

        # === 解析配置 ===
        local timeout_sec on_timeout criticality version
        timeout_sec=$(get_plugin_config "$name" "timeout_sec")
        on_timeout=$(get_plugin_config "$name" "on_timeout")
        criticality=$(get_plugin_config "$name" "criticality")
        version=$(get_plugin_config "$name" "version")
        timeout_sec=${timeout_sec:-30}
        on_timeout=${on_timeout:-fail}
        criticality=${criticality:-critical}
        version=${version:-"unknown"}

        # === 执行插件（带超时）===
        log "RUN PLUGIN: $name (timeout=${timeout_sec}s, on_timeout=${on_timeout}, criticality=${criticality})"
        local plugin_output
        local exit_code=0
        local start_ms end_ms duration_ms
        start_ms=$(date +%s%3N 2>/dev/null || echo "$(date +%s)000")
        plugin_output=$(timeout "$timeout_sec" bash "$plugin_path" "$mode" "$hash" 2>&1) || exit_code=$?
        end_ms=$(date +%s%3N 2>/dev/null || echo "$(date +%s)000")
        duration_ms=$((end_ms - start_ms))

        # 插件输出写入日志
        echo "$plugin_output" >> "$LOG_FILE"

        # === 解析 JSON 输出（RULES.md §5.10.1 + §5.10.3 兼容性）===
        local json_result raw_status effective_status
        json_result=$(parse_plugin_json "$plugin_output" "$name" "$exit_code")
        raw_status=$(echo "$json_result" | jq -r '.status')
        effective_status="$raw_status"

        # 注入 version 和 duration_ms
        json_result=$(echo "$json_result" | jq -c \
            --arg v "$version" \
            --argjson d "$duration_ms" \
            '.version = (.version // $v) | .metrics = (.metrics // {}) | .metrics.duration_ms = (.metrics.duration_ms // $d)')

        # === criticality / on_timeout 策略判定（RULES.md §5.7 + §5.9.4）===
        # 计算是否为 critical 失败（用于依赖传播）
        local is_critical_fail=0

        if [ "$raw_status" = "pass" ]; then
            log "PLUGIN $name: PASS"
        elif [ "$raw_status" = "timeout" ]; then
            log "PLUGIN $name: TIMEOUT (超过 ${timeout_sec}s)"
            if [ "$on_timeout" = "fail" ]; then
                log "  → on_timeout=fail: 中断验证"
                is_critical_fail=1
            else
                log "  → on_timeout=warn: 降级警告（继续执行）"
                effective_status="warn"
                warnings="$warnings $name(TIMEOUT)"
            fi
        elif [ "$raw_status" = "fail" ]; then
            log "PLUGIN $name: FAIL (exit $exit_code)"
            if [ "$criticality" = "critical" ]; then
                log "  → criticality=critical: 中断验证"
                is_critical_fail=1
            else
                log "  → criticality=optional: 降级警告（继续执行）"
                effective_status="warn"
                warnings="$warnings $name(FAIL)"
            fi
        fi

        # 如果 effective_status 与 raw_status 不同，覆盖 JSON 中的 status
        if [ "$effective_status" != "$raw_status" ]; then
            json_result=$(echo "$json_result" | jq -c --arg s "$effective_status" '.status = $s')
        fi

        echo "$json_result" >> "$plugin_results_file"
        plugin_status[$name]="$effective_status"
        plugin_critical_failed[$name]="$is_critical_fail"
        [ "$is_critical_fail" = "1" ] && critical_failed=1
    done

    # === 生成合并验证报告（RULES.md §5.10.4 + §5.10.5）===
    local report_file="$LOG_DIR/verify-report-${TIMESTAMP}.json"
    generate_verify_report "$report_file" "$mode" "$hash" "$plugin_results_file"
    log "合并验证报告: $report_file"

    # 清理临时 JSONL 文件（保留合并报告即可）
    rm -f "$plugin_results_file"

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
