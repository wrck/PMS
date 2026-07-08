#!/bin/bash
# Plugin: flyway-version-check
# Version: 1.0.0
# Author: 主代理
# Description: 检查 Flyway 迁移版本号是否有冲突（同模块内 Vxx 重复）
# Usage: bash 30-flyway-version-check.sh [--pre|<hash>] [hash]
# 检查规则：扫描所有模块的 src/main/resources/db/migration/V*.sql，提取版本号，同模块内不允许重复
set -uo pipefail

mode="${1:---pre}"
hash="${2:-}"

# 确定要检查的文件列表
if [ "$mode" = "--pre" ]; then
    # 子代理侧：检查工作区+暂存区中新增/修改的 V*.sql
    files=$(git status --porcelain | grep -E '\.sql$' | awk '{print $2}')
else
    # 主代理侧：检查该 commit 涉及的 V*.sql
    files=$(git diff-tree --no-commit-id --name-only -r "$hash" 2>/dev/null | grep -E '\.sql$')
fi

if [ -z "$files" ]; then
    echo "PLUGIN 30-flyway: 无 SQL 迁移文件变更，跳过"
    exit 0
fi

# 检查每个涉及的模块是否有版本号冲突
errors=0
while IFS= read -r f; do
    [ -z "$f" ] && continue
    # 提取版本号（如 V45__xxx.sql → 45）
    version=$(basename "$f" | grep -oP '^V\K\d+' || echo "")
    [ -z "$version" ] && continue
    # 找到该文件所属的 migration 目录
    dir=$(dirname "$f")
    # 检查同目录下是否有其他同版本号文件
    duplicates=$(find "$dir" -name "V${version}__*.sql" -type f 2>/dev/null | wc -l)
    if [ "$duplicates" -gt 1 ]; then
        echo "PLUGIN 30-flyway: FAIL - 版本号 V${version} 在 $dir 有 $duplicates 个文件（冲突）"
        find "$dir" -name "V${version}__*.sql" -type f | while read -r dup; do
            echo "  - $dup"
        done
        errors=$((errors+1))
    fi
done <<< "$files"

if [ $errors -gt 0 ]; then
    echo "PLUGIN 30-flyway: FAIL ($errors 个版本号冲突)"
    exit 1
fi

echo "PLUGIN 30-flyway: PASS (无版本号冲突)"
exit 0
