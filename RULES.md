# 工作规则（硬性强制）

> 本规则适用于所有在主仓库 `/workspace` 及其子目录工作的代理（主代理与子代理）。
> 任何违反本规则导致的代码丢失、提交错乱、数据不一致，均视为重大事故。
> 本规则与 `AGENTS.md` 并列，共同构成上下文约束；冲突时以本规则为准。
>
> **方法论核心 = 四支柱**：Worktree 物理隔离 + 验证脚本化/结构化 Commit + 共享文件锁/队列 + 标准化恢复 SOP。
> 所有具体规则围绕这四支柱展开。
>
> **运维底线**：Worktree 不泄漏磁盘、命名空间不污染；验证脚本不可被篡改；共享文件无死锁。

---

## 支柱一：Worktree 支持 — 物理隔离，真并行

> 让多个子代理真正并行工作，互不干扰，同时保证可合并回主仓库。
> 运维底线：磁盘不泄漏、命名空间不污染、僵尸 worktree 不过夜。

### 1.1 仓库与 Worktree 拓扑

- **主仓库**：`/workspace/.git`（普通仓库，非 bare）。主代理在此工作。
- **Worktree 根目录**：`/workspace/wt/`，每个子代理一个子目录。
- **子代理工作目录**：子代理的 `cwd` 必须设为 `/workspace/wt/<branch-name>`，所有文件读写和 git 操作在该 worktree 内进行。
- **禁止子代理直接操作主仓库 `/workspace` 工作树**：避免与主代理或其他并行 worktree 冲突。
- **禁止 `git clone` 创建独立仓库**：所有工作必须基于主仓库的 worktree。

### 1.2 分支命名规范（强制）

所有 worktree 分支必须遵循统一命名格式，确保可追溯、可审计、可自动识别。**无此规范，Worktree 机制在多代理下极易混乱**。

```
wt/<批次>-<任务组>-<代理编号>-<简述>
```

| 组成 | 说明 | 示例 |
|------|------|------|
| `wt/` | 固定前缀，标识 worktree 分支 | `wt/` |
| `<批次>` | 批次号 | `b4`、`b5`、`b6` |
| `<任务组>` | 任务组缩写 | `engine`、`frontend`、`security`、`test` |
| `<代理编号>` | 并行代理序号（两位数） | `01`、`02` |
| `<简述>` | 简短描述（kebab-case） | `mq-connectors` |

**完整示例**：`wt/b4-engine-01-mq-connectors`、`wt/b6-frontend-02-i18n-dark`

- **主分支**：`lowcode`（当前开发分支）或 `main`/`master`（保护分支，禁止直接提交）。
- **Worktree 分支生命周期**：创建→合并→删除，**禁止长期保留**（超过 2 小时未合并视为僵尸，详见 §1.3）。
- **命名校验**：主代理合并后执行 `git branch | grep 'wt/'` 确认无残留分支。
- **分支名唯一性**：禁止复用已存在的分支名，每次创建 worktree 必须用新分支名（即使同名任务也加序号区分）。

### 1.3 Worktree 生命周期管理（防磁盘泄漏 + 命名空间污染）

Worktree 经历完整的生命周期，每个阶段有明确的操作和状态校验。**核心运维目标：磁盘零泄漏、命名空间零污染**。

```
创建(CREATE) → 激活(ACTIVE) → 提交(COMMITTED) → 合并(MERGED) → 清理(CLEANED) → 归档(ARCHIVED)
                                                    ↓ 失败
                                              恢复(RECOVER) → 重试
```

#### 阶段 1：创建（CREATE）

```bash
# 主代理创建 worktree + 分支
cd /workspace && git worktree add /workspace/wt/<branch-name> -b <branch-name>

# 校验创建成功
test -d /workspace/wt/<branch-name>/.git || { echo "worktree 创建失败"; exit 1; }
git worktree list | grep <branch-name> || { echo "worktree 未注册"; exit 1; }

# 记录到注册表
echo "$(date +%s),<branch-name>,CREATE,$(du -sm /workspace/wt/<branch-name> | cut -f1)" >> /workspace/.worktree-registry.csv
```

#### 阶段 2：激活（ACTIVE）

子代理在 worktree 内工作。主代理记录激活时间：
```bash
echo "$(date +%s),<branch-name>,ACTIVE" >> /workspace/.worktree-registry.csv
```

#### 阶段 3：提交（COMMITTED）

子代理在 worktree 内提交后，主代理验证提交存在：
```bash
cd /workspace/wt/<branch-name> && git log --oneline -1
# 记录 commit hash 以备验证
echo "$(date +%s),<branch-name>,COMMITTED,<commit-hash>" >> /workspace/.worktree-registry.csv
```

#### 阶段 4：合并（MERGED）

主代理执行合并（见 §1.4 合并协议）。合并成功后更新注册表：
```bash
echo "$(date +%s),<branch-name>,MERGED" >> /workspace/.worktree-registry.csv
```

#### 阶段 5：清理（CLEANED）

**此阶段是防磁盘泄漏的关键**——必须完整清理 worktree 目录 + 分支 + 锁文件 + 补丁片段：

```bash
# 1. 移除 worktree（--force 确保即使有未跟踪文件也清理）
git worktree remove /workspace/wt/<branch-name> --force

# 2. 删除分支
git branch -D <branch-name>

# 3. 清理该 worktree 可能遗留的锁文件
find /workspace/.file-locks/ -name "*.lock" -exec grep -l "<branch-name>" {} \; -delete 2>/dev/null || true

# 4. 清理该 worktree 可能遗留的补丁片段
rm -f /workspace/patches/*-<branch-name>*.ts 2>/dev/null || true

# 5. 校验清理成功（防磁盘泄漏）
test ! -d /workspace/wt/<branch-name> || { echo "FAIL: worktree 目录残留（磁盘泄漏）"; exit 1; }
git worktree list | grep <branch-name> && { echo "FAIL: worktree 仍注册（命名空间污染）"; exit 1; } || true
git branch | grep <branch-name> && { echo "FAIL: 分支残留（命名空间污染）"; exit 1; } || true

echo "$(date +%s),<branch-name>,CLEANED" >> /workspace/.worktree-registry.csv
```

#### 阶段 6：归档（ARCHIVED）

合并记录保留在 `.worktree-registry.csv`，用于事后审计。注册表格式：
```
timestamp,branch-name,state[,extra]
1783500000,wt/b4-engine-01-mq-connectors,CREATE,125
1783500600,wt/b4-engine-01-mq-connectors,ACTIVE
1783500900,wt/b4-engine-01-mq-connectors,COMMITTED,abc1234
1783500905,wt/b4-engine-01-mq-connectors,MERGED
1783500910,wt/b4-engine-01-mq-connectors,CLEANED
```

#### 异常：恢复（RECOVER）

合并失败或验证失败时，worktree 进入恢复状态（见支柱四 §4.3）：
```bash
echo "$(date +%s),<branch-name>,RECOVER" >> /workspace/.worktree-registry.csv
# 按 SOP 处理后重新进入 MERGED 或重新创建
```

### 1.4 磁盘泄漏与命名空间污染防护（运维底线）

**这是运维底线，违反即视为 P1 事故**：

| 风险 | 防护机制 | 检查频率 |
|------|---------|---------|
| Worktree 目录未清理（磁盘泄漏） | §1.3 阶段 5 强制校验 `test ! -d` | 每次合并后 |
| Worktree 分支未删除（命名空间污染） | §1.3 阶段 5 强制校验 `git branch grep` | 每次合并后 |
| 僵尸 worktree（创建超 2h 未合并） | §1.5 超时清理机制 | 每 30 分钟扫描 |
| 锁文件残留 | 阶段 5 清理 + 批次末 `cleanup_stale_locks` | 每次合并 + 批次末 |
| 补丁片段残留 | 阶段 5 清理 + 批次末校验 patches/ 为空 | 每次合并 + 批次末 |
| 注册表与实际不一致 | 批次末 `git worktree list` 对比注册表 | 每批次末 |

**批次末强制巡检脚本**（主代理每批次结束执行）：

```bash
worktree_patrol() {
    echo "==[Worktree 巡检]=="
    local issues=0

    # 1. 检查残留 worktree 目录
    for d in /workspace/wt/*/; do
        [ -d "$d" ] && { echo "ISSUE: 残留 worktree 目录 $d"; issues=$((issues+1)); }
    done

    # 2. 检查残留 wt/ 分支
    local stale_branches
    stale_branches=$(git branch | grep 'wt/' || true)
    [ -z "$stale_branches" ] || { echo "ISSUE: 残留分支 $stale_branches"; issues=$((issues+1)); }

    # 3. 检查 worktree 注册与实际一致性
    local wt_list_count wt_registry_count
    wt_list_count=$(git worktree list | wc -l)
    wt_registry_count=$(grep -c ',CREATE,' /workspace/.worktree-registry.csv 2>/dev/null || echo 0)
    local cleaned_count
    cleaned_count=$(grep -c ',CLEANED,' /workspace/.worktree-registry.csv 2>/dev/null || echo 0)
    local pending=$((wt_registry_count - cleaned_count))
    [ "$pending" -le 1 ] || { echo "ISSUE: 注册表有 $pending 个未清理 worktree"; issues=$((issues+1)); }

    # 4. 检查残留锁文件
    local lock_count
    lock_count=$(ls /workspace/.file-locks/*.lock 2>/dev/null | wc -l)
    [ "$lock_count" -eq 0 ] || { echo "ISSUE: 残留 $lock_count 个锁文件"; issues=$((issues+1)); }

    # 5. 检查残留补丁片段
    local patch_count
    patch_count=$(ls /workspace/patches/*.ts 2>/dev/null | wc -l)
    [ "$patch_count" -eq 0 ] || { echo "ISSUE: 残留 $patch_count 个补丁片段"; issues=$((issues+1)); }

    [ "$issues" -eq 0 ] && echo "PATROL: ALL CLEAN" || { echo "PATROL: $issues ISSUES FOUND"; return 1; }
}
```

### 1.5 Worktree 合并协议（主代理负责）

子代理在 worktree 内提交后，**主代理负责执行合并**（非子代理自身）：

```bash
# 1. 主代理切换到主工作树
cd /workspace

# 2. 主代理侧完整验证（见 §2.7 双重门禁）
bash scripts/verify-commit.sh <commit-hash>

# 3. 验证通过后合并（--no-ff 保留分支历史，便于审计）
git merge --no-ff <branch-name> -m "merge: <branch-name> 完成批次X"

# 4. 合并后跑全量集成验证
bash scripts/verify.sh all

# 5. 验证通过后清理 worktree（见 §1.3 阶段 5 + §1.4 巡检）
```

- 如果 §2.7 验证失败：根据退出码处理（1=编译失败要求子代理修复，2=文件越权要求子代理回退越权文件，3=Hash 丢失按 §4.1 SOP 恢复）。
- 如果合并产生冲突：按支柱四 §4.2 并发冲突恢复 SOP 处理。
- 如果合并后验证失败：`git reset --merge` 回退合并，要求子代理修复后重新提交。

### 1.6 Worktree 监控与超时清理

- 主代理每批次结束时执行 `git worktree list` 确认无残留 worktree。
- 残留 worktree 视为 P3 事故，立即 `git worktree prune` + 手动清理。
- Worktree 数量上限：同时不超过 5 个（防止资源耗尽）。
- **僵尸 worktree 清理**：worktree 创建超过 2 小时仍未合并，视为僵尸（P2 事故），主代理强制清理：
  ```bash
  # 僵尸 worktree 强制清理
  cleanup_zombie_worktrees() {
      local now threshold
      now=$(date +%s)
      threshold=$((now - 7200))  # 2 小时
      while IFS=, read -r ts branch state extra; do
          if [ "$state" = "CREATE" ] || [ "$state" = "ACTIVE" ]; then
              [ "$ts" -lt "$threshold" ] && {
                  echo "ZOMBIE: 强制清理僵尸 worktree $branch (创建于 $(date -d @$ts))"
                  git worktree remove /workspace/wt/$branch --force 2>/dev/null || true
                  git branch -D $branch 2>/dev/null || true
                  echo "$now,$branch,ZOMBIE_CLEANED" >> /workspace/.worktree-registry.csv
              }
          fi
      done < /workspace/.worktree-registry.csv
  }
  ```
- **注册表审计**：批次结束时检查 `.worktree-registry.csv`，确认所有 worktree 都达到 CLEANED 状态。

### 1.7 子代理任务描述规范

主代理派发子代理时，任务描述**必须包含**：

```
# 工作目录（Worktree，禁止 clone）
/workspace/wt/<branch-name>
所有文件读写和 git 操作必须在此目录内进行。

# 分支信息
当前 worktree 分支：<branch-name>（命名遵循 §1.2 规范）

# 提交前验证（强制）
提交前必须在 worktree 内运行：bash /workspace/scripts/verify-commit.sh --pre
仅当返回 0 时才能提交，并在 commit message 末尾加 [VERIFIED] 标签。
若返回非 0，根据退出码自动修复并重试，禁止直接上报主代理。

# 提交要求
在 worktree 内提交，commit message 遵循结构化模板（见 §2.4）。
不要 push，不要合并到主分支——主代理会负责合并。
不要修改共享文件（router/index.ts 等），将改动写入 patches/ 目录（见 §3.3）。
不要修改 scripts/verify.sh 和 scripts/verify-commit.sh（受保护资产，见 §2.3.3）。
```

---

## 支柱二：验证脚本化 + 结构化 Commit — 自动化验证，快速审计

> 消除"人工假设验证通过"的风险，提交信息可机器解析、可快速审计。
> 核心原则：**将验证协议脚本化，减少主代理的认知负载**。

### 2.1 两套验证脚本的分工

主仓库提供**两套**标准化验证脚本，子代理和主代理均通过调用脚本完成验证，**禁止手动执行零散命令**：

| 脚本 | 路径 | 职责 | 调用方 |
|------|------|------|--------|
| `verify.sh` | `scripts/verify.sh` | 全量集成验证（编译+类型+Lint+Git 干净） | 主代理合并后 |
| `verify-commit.sh` | `scripts/verify-commit.sh` | 提交级验证（Hash+文件范围+编译+类型+Lint） | 子代理 `--pre` + 主代理 `<hash>` |

### 2.2 verify.sh 全量集成验证

- **职责**：主代理合并所有 worktree 后，在主仓库主分支上跑全量集成验证。
- **前置安全检查**（pre-flight）：见 §2.2.1。
- **用法**：`bash scripts/verify.sh [backend|frontend|all]`
- **通过标准**：输出 `ALL CHECKS PASSED`，exit 0。

#### 2.2.1 前置安全检查

`scripts/verify.sh` 在执行实际编译/类型检查前，**必须先完成前置安全检查**，防止在错误环境运行导致误判：

| # | 检查项 | 失败处理 |
|---|--------|---------|
| 1 | 确认在 `/workspace` 主仓库根目录 | 提示切换目录，exit 1 |
| 2 | 确认在主分支（lowcode/main）运行 | 提示 worktree 分支用 verify-commit.sh --pre |
| 3 | 确认 mvn 命令可用 | 提示安装 Maven，exit 1 |
| 4 | 确认 npx 命令可用 | 提示安装 Node.js，exit 1 |
| 5 | 确认关键目录结构完整 | 提示仓库异常，exit 1 |
| 6 | 确认无残留 worktree | WARN 警告（不阻断） |
| 7 | 确认磁盘空间 >1GB | 提示清理磁盘，exit 1 |

### 2.3 verify-commit.sh 提交级验证协议（双重门禁）

**这是减少主代理认知负载的核心机制**。主仓库必须提供标准化脚本 `scripts/verify-commit.sh`，子代理提交前、主代理集成后，均通过调用此脚本完成验证。

#### 2.3.1 脚本职责

`verify-commit.sh` 执行以下检查（按顺序）：

1. **Hash 存在性检查**：验证 commit hash 在当前仓库存在（主代理侧）或在 worktree 分支存在（子代理侧）。
2. **变更文件范围校验**：检查本次提交涉及的文件是否越权（修改了非任务范围的文件，如 `scripts/verify*.sh`、其他模块文件）。
3. **编译/类型检查**：后端 `mvn compile`，前端 `vue-tsc --noEmit`。
4. **Lint 检查**：前端 `eslint`（如有配置）。

#### 2.3.2 退出码约定

| 退出码 | 含义 | 处理方 |
|--------|------|--------|
| 0 | 通过 | 可提交/可合并 |
| 1 | 编译失败 | 子代理自动修复重试 |
| 2 | 文件越权 | 子代理回退越权文件后重试 |
| 3 | Hash 丢失 | 主代理按 §4.1 SOP 恢复 |

#### 2.3.3 双重验证门禁

**子代理侧（--pre 提交前验证）**：

```bash
# 子代理在 worktree 内提交前必须运行
bash /workspace/scripts/verify-commit.sh --pre
```

- **作用域限制（关键）**：`--pre` 仅检查**语法、类型和静态分析**，**不进行需要完整主仓库环境的集成测试**。
- 集成测试（全量 mvn compile 跨模块、worktree 间依赖验证）仅在主代理侧执行。
- 这样减少子代理侧在提交前运行 `--pre` 验证的误报，提升单次提交成功率。
- 通过后子代理在 commit message 末尾打上 `[VERIFIED]` 标签再提交。

**主代理侧（hash 完整验证）**：

```bash
# 主代理收到子代理报告的 commit hash 后必须运行
bash scripts/verify-commit.sh <commit-hash>
```

- 执行完整验证（含集成测试所需的 Hash 存在性 + 文件范围 + 编译 + Lint）。
- **仅当返回 0 时，才视为任务完成**，方可执行合并。
- 返回非 0 时按 §2.3.2 退出码处理。

#### 2.3.4 失败处理（子代理自动修复）

若 `--pre` 验证失败，**子代理必须根据脚本输出的错误码自动修复并重试，而非直接上报主代理**：

| 退出码 | 子代理自动修复动作 |
|--------|-------------------|
| 1（编译失败） | 读取 verify-commit.sh 输出的错误日志，定位编译错误，修复后重新运行 `--pre` |
| 2（文件越权） | `git reset HEAD <越权文件>` + `git checkout -- <越权文件>`，回退越权文件后重新运行 `--pre` |
| 3（Hash 丢失） | 此情况仅主代理侧出现，子代理侧不会触发 |

- **重试上限**：子代理最多自动重试 3 次。3 次仍失败，才上报主代理（附完整错误日志）。
- **禁止**：子代理在 `--pre` 失败后直接提交并上报——必须先修复至通过。

### 2.4 验证脚本的版本控制与完整性保护（受保护资产）

验证脚本是规则执行的基石，**属受保护核心资产**，必须防止被篡改。

#### 2.4.1 版本号管理

- `scripts/verify.sh` 和 `scripts/verify-commit.sh` 头部声明版本号：`VERIFY_VERSION="x.y.z"`。
- 每次修改必须递增版本号（语义化版本：MAJOR.MINOR.PATCH）。
- commit message 必须包含 `Verify-Version: <version>` trailer。

#### 2.4.2 完整性校验

- 仓库根维护 `scripts/verify.sha256` + `scripts/verify-commit.sha256`，存储两脚本的 SHA-256 摘要。
- 脚本启动时自校验（防止运行被篡改的版本导致误判通过）：
  ```bash
  self_check() {
      local script_path="$0" expected actual
      expected=$(awk '{print $1}' "${0%.sh}.sha256" 2>/dev/null)
      actual=$(sha256sum "$script_path" | awk '{print $1}')
      if [ "$expected" != "$actual" ]; then
          echo "FAIL: $script_path 完整性校验失败（可能被篡改）"
          exit 1
      fi
  }
  ```
- **更新摘要**：修改脚本后必须重新生成 sha256，脚本 + sha256 一起提交。

#### 2.4.3 修改协议（受保护资产，禁止子代理修改）

**验证脚本是受保护的核心资产，禁止子代理在常规任务中修改**：

- **禁止子代理修改**：`scripts/verify.sh`、`scripts/verify-commit.sh`、`scripts/*.sha256` 列入子代理黑名单，子代理任务描述必须明确声明"禁止修改 scripts/ 目录下任何文件"。
- **主代理修改需用户确认**：主代理如需修改验证脚本，**必须经过用户确认或特殊审批流程**：
  - 主代理向用户说明修改原因、影响范围、版本号变更。
  - 用户明确同意后，主代理方可修改。
  - 修改后必须重新生成 sha256 + 递增版本号 + 通过自校验。
- **修改提交规范**：必须通过结构化 commit（type=`chore`，scope=`verify`），commit message 必须包含：
  - `Verify-Version: <old> → <new>`
  - 修改原因
  - 用户批准记录（如"Approved-by: user on 2026-07-08"）
- **完整性校验失败时**：脚本拒绝执行，视为 P1 事故，主代理立即停止工作并告知用户。

### 2.5 结构化 Commit 模板

每个 commit 必须遵循 Conventional Commits + 结构化 trailer：

```
<type>(<scope>): <subject>

<body: 任务内容 + 实现方式 + 测试结果>

Task-ID: <批次-任务号，如 b4-t5>
Files: <新增N/修改M/删除K>
Verify: <verify-commit.sh --pre: pass (exit 0) | verify.sh all: pass>
Worktree: <wt/branch-name 或 main>
Reviewed-by: <主代理>
[VERIFIED]
```

- **type**：feat/fix/docs/refactor/test/chore
- **scope**：模块名（lowcode/lowcode-frontend/system 等）
- **`[VERIFIED]` 标签**：子代理提交必须带此标签，表示已通过 `--pre` 验证。主代理合并前检查此标签存在。
- **禁止模糊提交信息**：如 "update"、"fix bug"、"wip" 等一律禁止。
- **一个 commit 一个逻辑单元**：禁止把多个不相关 Task 塞进一个 commit。

### 2.6 提交后复核（强制）

每个提交完成后，主代理必须执行复核：

```bash
git log --oneline -1          # 确认提交在 HEAD
git show --stat HEAD           # 确认文件列表符合预期
git status --short             # 确认工作区状态
```

- 如果 `git show --stat HEAD` 的文件列表与预期不符（多了或少了），必须立即修正。
- 检查 commit message 是否包含 `[VERIFIED]` 标签（子代理提交）。

---

## 支柱三：共享文件锁/队列机制 — 消除串行瓶颈，防调度失误

> 多个并行子代理需要修改同一文件时，用锁/队列协调，避免覆盖丢失。
> 核心原则：**防单点故障拖垮整体并行效率**。

### 3.1 共享文件识别

主代理在规划并行任务时，**必须识别共享文件**（多个子代理都需要修改的文件），典型共享文件：
- `pms-frontend/src/router/index.ts`
- `pms-frontend/src/api/lowcode.ts`
- `pms-admin/src/main/resources/application.yml`
- 任何 `pom.xml`
- 任何 `SecurityConfig.java` / `main.ts` / `App.vue`

### 3.2 共享文件锁机制（适用低竞争）

- **锁文件**：`/workspace/.file-locks/<file-path-hash>.lock`，内容为持有者 branch-name + 时间戳。
- **获取锁**（带超时，防死锁等待）：
  ```bash
  LOCK=/workspace/.file-locks/$(echo "<file-path>" | md5sum | cut -d' ' -f1).lock
  WAIT=0
  while [ -f "$LOCK" ] && [ $WAIT -lt 300 ]; do sleep 2; WAIT=$((WAIT+2)); done
  if [ -f "$LOCK" ]; then
      LOCK_AGE=$(( $(date +%s) - $(cut -d: -f2 "$LOCK") ))
      [ $LOCK_AGE -gt 300 ] && { echo "WARN: 锁超时 ${LOCK_AGE}s，强制接管"; rm -f "$LOCK"; } || { echo "FAIL: 获取锁超时"; exit 1; }
  fi
  echo "<branch-name>:$(date +%s)" > "$LOCK"
  ```
- **释放锁**：修改完成并提交后 `rm -f "$LOCK"`。
- **锁超时**：持有超过 300 秒视为死锁，其他代理可强制接管（记录告警日志）。

#### 锁死锁预防机制

| 风险 | 预防机制 |
|------|---------|
| 持有者崩溃不释放锁 | 锁文件含时间戳，超时 300s 后其他代理可强制接管 |
| 等待者无限等待 | 获取锁带 `WAIT` 计数器，超 300s 报错退出 |
| 多代理同时强制接管 | 接管前二次检查锁年龄，竞争窗口极小 |
| 锁文件残留堆积 | 主代理每批次结束扫描清理（见 §1.4 巡检） |

### 3.3 共享文件队列模式（推荐，适用高竞争）

对于高竞争共享文件（如 `router/index.ts`），**优先采用队列模式而非锁**：

1. 子代理任务描述声明："需要新增路由 X，但**不要直接修改 router/index.ts**，将路由片段写入 `patches/router-<branch-name>.ts`"。
2. 子代理在 worktree 内创建补丁片段文件，不碰共享文件。
3. 所有子代理完成后，主代理一次性合并所有 `patches/router-*.ts` 到 `router/index.ts` 并提交。

#### 队列死锁预防机制

队列模式本身无锁，但存在另一种"死锁"——**补丁片段缺失或子代理卡住导致主代理合并卡住**：

| 风险 | 预防机制 |
|------|---------|
| 子代理未生成补丁片段就崩溃 | 主代理合并前校验：`ls patches/router-*.ts` 必须覆盖所有声明的子代理 |
| 补丁片段语法错误导致合并失败 | 主代理合并后跑 verify.sh frontend，失败则回退 + 标记该子代理为需修复 |
| 补丁片段冲突（两个子代理改同一行） | 主代理按 branch-name 排序顺序合并，冲突时后者追加而非覆盖 |
| 补丁片段残留（已合并但未清理） | 合并成功后 `rm -f patches/router-*.ts`，批次末校验 patches/ 为空 |

### 3.4 超时熔断机制（防单点故障拖垮并行效率）

**核心规则**：若某个子代理对共享文件的修改在 N 分钟内无变化或未通过验证，主代理应**强制终止该代理，回滚其变更，并将任务重新入队或降级为人工介入**。

#### 熔断触发条件

| 条件 | 阈值（N） | 触发动作 |
|------|----------|---------|
| 子代理持有共享文件锁超时 | 300 秒（5 分钟） | 强制接管锁 + 标记子代理为 STALLED |
| 子代理 worktree 创建后无任何提交 | 1800 秒（30 分钟） | 强制清理 worktree + 任务重新入队 |
| 子代理提交后 `--pre` 验证连续失败 | 3 次 | 任务降级为人工介入 |
| 子代理补丁片段生成后主代理合并失败 | 600 秒（10 分钟）内未修复 | 回滚补丁 + 任务重新入队 |
| 子代理无响应（无心跳） | 900 秒（15 分钟） | 强制终止 + worktree 清理 + 任务重新入队 |

#### 熔断执行流程

```bash
# 主代理熔断检查（每批次中执行）
circuit_breaker_check() {
    local now threshold
    now=$(date +%s)

    # 1. 检查锁超时
    for lock in /workspace/.file-locks/*.lock; do
        [ -f "$lock" ] || continue
        local holder ts age
        holder=$(cut -d: -f1 "$lock")
        ts=$(cut -d: -f2 "$lock")
        age=$((now - ts))
        if [ $age -gt 300 ]; then
            echo "BREAKER: 锁超时 $holder ($age s)，强制接管"
            rm -f "$lock"
            # 标记 holder 为 STALLED
            echo "$now,$holder,STALLED,LOCK_TIMEOUT" >> /workspace/.circuit-breaker.log
            # 触发任务重新入队（主代理记录，后续重新派发）
        fi
    done

    # 2. 检查僵尸 worktree（无提交超 30 分钟）
    while IFS=, read -r ts branch state extra; do
        if [ "$state" = "ACTIVE" ]; then
            local active_age=$((now - ts))
            if [ $active_age -gt 1800 ]; then
                echo "BREAKER: worktree $branch 无提交超 30 分钟，强制清理"
                git worktree remove /workspace/wt/$branch --force 2>/dev/null || true
                git branch -D $branch 2>/dev/null || true
                echo "$now,$branch,BROKEN,NO_COMMIT_TIMEOUT" >> /workspace/.circuit-breaker.log
                # 任务重新入队
            fi
        fi
    done < /workspace/.worktree-registry.csv
}
```

#### 熔断后任务处理

| 熔断原因 | 任务处理 |
|---------|---------|
| 锁超时 | 锁释放，任务重新入队，由新子代理接续 |
| 无提交超时 | worktree 清理，任务重新入队，从头开始 |
| `--pre` 连续失败 3 次 | 任务降级为人工介入，主代理告知用户 |
| 无心跳 | worktree 清理，任务重新入队 |
| 补丁合并失败超时 | 补丁回滚，任务重新入队 |

**熔断日志**：所有熔断事件记录到 `/workspace/.circuit-breaker.log`，批次末汇报给用户。

### 3.5 提交原子性

- 每个子代理只能提交归属于自己任务的文件，**禁止 `git add -A` 或 `git add .`**。
- 必须按文件路径精确 `git add <file1> <file2> ...`。
- 合并时如遇冲突，按支柱四 §4.2 SOP 解决。

---

## 支柱四：标准化恢复 SOP — 降低故障恢复时间

> 发生提交丢失、冲突、worktree 损坏等故障时，按标准操作流程快速恢复。

### 4.1 提交丢失恢复 SOP

**症状**：子代理自报 commit hash，但主仓库 `git log` 中找不到。对应 `verify-commit.sh <hash>` 退出码 3。

**恢复步骤**：
1. **立即停止**派发新子代理。
2. 检查所有 worktree：`git worktree list`，进入每个 worktree 执行 `git log --oneline -5`。
3. 如果在某个 worktree 找到提交：`cd /workspace && git cherry-pick <hash>` 合并回主分支。
4. 如果所有 worktree 都没有：`git fsck --lost-found` 寻找 dangling commit。
5. 如果仍找不到：确认数据不可恢复，记录事故（P0），告知用户，重新实施。

### 4.2 并发冲突恢复 SOP

**症状**：`git commit` 报错 "Your local changes would be overwritten" 或 `git add` 发现文件已被其他代理 staged。

**恢复步骤**：
1. `git status` 查看冲突文件。
2. `git stash push -m "conflict-rescue-<timestamp>"` 暂存自己的改动。
3. 等待其他代理提交完成，或 `git pull --rebase`。
4. `git stash pop` 恢复改动，手动解决冲突标记。
5. 冲突解决后重新验证（支柱二）+ 提交。

### 4.3 Worktree 损坏恢复 SOP

**症状**：worktree 目录被误删或 `git worktree` 状态异常。

**恢复步骤**：
1. `git worktree prune` 清理无效引用。
2. 如果 worktree 内有未合并的提交：`git fsck --lost-found` 在主仓库寻找 dangling commit。
3. 重新创建 worktree：`git worktree add /workspace/wt/<branch-name> -b <branch-name>`。
4. 重新实施丢失的工作。

### 4.4 事故分级与上报

| 级别 | 定义 | 处理 | 上报 |
|------|------|------|------|
| P0 | 整批提交丢失，数据不可恢复 | 立即停止所有工作，重新实施 | 立即告知用户 |
| P1 | 单个提交丢失 / 验证脚本完整性校验失败 | cherry-pick 恢复 / 停止工作告知用户 | 记录日志，批次末汇报 |
| P2 | 并发冲突 / 僵尸 worktree / 熔断触发 | 按 SOP 解决 | 记录日志，批次末汇报 |
| P3 | Worktree 残留 / 锁残留 / 补丁残留 | prune 清理 | 无需上报 |

### 4.5 分支与 HEAD 保护

- **禁止**对主分支执行 `git reset --hard`、`git checkout .`、`git clean -f`（除非用户明确要求）。
- **禁止** `git push --force`（除非用户明确要求）。
- 每完成一个重要里程碑（如一个批次），**必须打 git tag** 作为检查点：`git tag m{里程碑}-batch{批次}-{描述}`。
- 打 tag 后验证：`git tag -l | grep <tag-name>` 确认已创建。

---

## 附则

- 本规则优先级高于 `AGENTS.md` 中的任何冲突条款。
- 本规则随项目演进持续更新，每次更新需在提交信息中注明规则变更内容。
- 所有代理（主代理与子代理）在开始工作前必须读取并遵守本规则。
- 四支柱（Worktree/验证脚本化/共享文件锁/恢复 SOP）是方法论核心，具体规则是其落地实现。
- **运维底线**：Worktree 磁盘零泄漏、命名空间零污染、验证脚本零篡改、共享文件零死锁。违反底线即视为重大事故。
- 用户指定的两条原始规则已融入支柱一（§1.5 合并协议）和支柱二（§2.5 结构化 Commit + §2.6 提交后复核），继续有效。
