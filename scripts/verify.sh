#!/bin/bash
# 全量验证脚本（本地 CI）— RULES.md 支柱二 §2.1
# 用法：bash scripts/verify.sh [backend|frontend|all]
# 退出码：0=全部通过，非0=存在失败
set -euo pipefail

MODE="${1:-all}"
LOG_DIR="scripts/logs"
mkdir -p "$LOG_DIR"
TIMESTAMP=$(date +%Y%m%d-%H%M%S)
LOG_FILE="$LOG_DIR/verify-${TIMESTAMP}.log"

run_backend() {
    echo "==[1/2] Backend compile (mvn compile)=="
    cd /workspace
    if mvn compile -q 2>&1 | tee -a "$LOG_FILE"; then
        echo "BACKEND: PASS"
    else
        echo "BACKEND: FAIL"
        return 1
    fi
}

run_frontend() {
    echo "==[2/2] Frontend typecheck (vue-tsc)=="
    cd /workspace/network-equipment-pms/pms-frontend
    if npx vue-tsc --noEmit 2>&1 | tee -a "$LOG_FILE"; then
        echo "FRONTEND: PASS"
    else
        echo "FRONTEND: FAIL"
        return 1
    fi
}

echo "Verify started at $(date), mode=$MODE, log=$LOG_FILE"

case "$MODE" in
    backend)  run_backend ;;
    frontend) run_frontend ;;
    all)
        run_backend
        run_frontend
        echo "==[Git status clean check]=="
        cd /workspace
        if [ -n "$(git status --porcelain)" ]; then
            echo "GIT: FAIL (工作区不干净)"
            git status --short
            exit 1
        fi
        echo "GIT: PASS"
        ;;
    *) echo "Unknown mode: $MODE"; exit 1 ;;
esac

echo "ALL CHECKS PASSED (log: $LOG_FILE)"
