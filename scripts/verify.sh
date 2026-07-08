#!/bin/bash
# 全量验证脚本（本地 CI）— RULES.md 支柱二 §2.1
# 版本：1.1.0（新增前置安全检查 §2.2）
# 用法：bash scripts/verify.sh [backend|frontend|all]
# 退出码：0=全部通过，非0=存在失败
set -euo pipefail

VERIFY_VERSION="1.1.0"
LOG_DIR="scripts/logs"
mkdir -p "$LOG_DIR"
TIMESTAMP=$(date +%Y%m%d-%H%M%S)
LOG_FILE="$LOG_DIR/verify-${TIMESTAMP}.log"

# === 前置安全检查（RULES.md §2.2）===
preflight() {
    echo "==[Pre-flight] 安全前置检查==" | tee -a "$LOG_FILE"

    # 1. 确认在主仓库根目录
    local git_root
    git_root=$(git rev-parse --show-toplevel 2>/dev/null) || { echo "FAIL: 不在 git 仓库内" | tee -a "$LOG_FILE"; exit 1; }
    [ "$git_root" = "/workspace" ] || { echo "FAIL: 必须在 /workspace 运行（当前 $git_root）" | tee -a "$LOG_FILE"; exit 1; }

    # 2. 确认关键目录存在
    [ -f "network-equipment-pms/pom.xml" ] || { echo "FAIL: 找不到 network-equipment-pms/pom.xml" | tee -a "$LOG_FILE"; exit 1; }
    [ -d "network-equipment-pms/pms-frontend" ] || { echo "FAIL: 找不到 pms-frontend 目录" | tee -a "$LOG_FILE"; exit 1; }

    # 3. 确认 Maven 可用
    command -v mvn >/dev/null 2>&1 || { echo "FAIL: mvn 未安装" | tee -a "$LOG_FILE"; exit 1; }

    # 4. 确认 npx 可用
    command -v npx >/dev/null 2>&1 || { echo "FAIL: npx 未安装" | tee -a "$LOG_FILE"; exit 1; }

    # 5. 确认磁盘空间充足（>1GB）
    local free_mb
    free_mb=$(df -m /workspace | awk 'NR==2{print $4}')
    [ "$free_mb" -gt 1024 ] || { echo "FAIL: 磁盘空间不足 1GB（剩余 ${free_mb}MB）" | tee -a "$LOG_FILE"; exit 1; }

    # 6. 残留 worktree 警告（不阻断）
    local wt_count
    wt_count=$(git worktree list 2>/dev/null | wc -l)
    [ "$wt_count" -le 1 ] || echo "WARN: 存在 $((wt_count - 1)) 个残留 worktree" | tee -a "$LOG_FILE"

    echo "Pre-flight: ALL CHECKS PASSED" | tee -a "$LOG_FILE"
}

# === 实际验证逻辑 ===
run_backend() {
    echo "==[Backend] mvn compile==" | tee -a "$LOG_FILE"
    cd /workspace/network-equipment-pms
    if mvn compile -q 2>&1 | tee -a "$LOG_FILE"; then
        echo "BACKEND: PASS" | tee -a "$LOG_FILE"
    else
        echo "BACKEND: FAIL" | tee -a "$LOG_FILE"
        exit 1
    fi
}

run_frontend() {
    echo "==[Frontend] vue-tsc --noEmit==" | tee -a "$LOG_FILE"
    cd /workspace/network-equipment-pms/pms-frontend
    if npx vue-tsc --noEmit 2>&1 | tee -a "$LOG_FILE"; then
        echo "FRONTEND: PASS" | tee -a "$LOG_FILE"
    else
        echo "FRONTEND: FAIL" | tee -a "$LOG_FILE"
        exit 1
    fi
}

MODE="${1:-all}"
echo "Verify v${VERIFY_VERSION} started at $(date), mode=$MODE, log=$LOG_FILE" | tee -a "$LOG_FILE"

preflight

case "$MODE" in
    backend)  run_backend ;;
    frontend) run_frontend ;;
    all)
        run_backend
        run_frontend
        echo "==[Git status clean check]==" | tee -a "$LOG_FILE"
        cd /workspace
        if [ -n "$(git status --porcelain)" ]; then
            echo "GIT: FAIL (工作区不干净)" | tee -a "$LOG_FILE"
            git status --short | tee -a "$LOG_FILE"
            exit 1
        fi
        echo "GIT: PASS" | tee -a "$LOG_FILE"
        ;;
    *) echo "Unknown mode: $MODE"; exit 1 ;;
esac

echo "ALL CHECKS PASSED (v${VERIFY_VERSION}, log: $LOG_FILE)" | tee -a "$LOG_FILE"
