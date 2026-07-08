# 工作规则（硬性强制）

> 本规则适用于所有在主仓库 `/workspace` 及其子目录工作的代理（主代理与子代理）。
> 任何违反本规则导致的代码丢失、提交错乱、数据不一致，均视为重大事故。
> 本规则与 `AGENTS.md` 并列，共同构成上下文约束；冲突时以本规则为准。
>
> **方法论核心 = 四支柱**：Worktree 物理隔离 + 验证脚本化/结构化 Commit + 共享文件锁/队列 + 标准化恢复 SOP。
> 所有具体规则围绕这四支柱展开。

---

## 支柱一：Worktree 支持 — 物理隔离，真并行

> 让多个子代理真正并行工作，互不干扰，同时保证可合并回主仓库。

### 1.1 仓库与 Worktree 拓扑

- **主仓库**：`/workspace/.git`（普通仓库，非 bare）。主代理在此工作。
- **Worktree 根目录**：`/workspace/wt/`，每个子代理一个子目录。
- **子代理工作目录**：子代理的 `cwd` 必须设为 `/workspace/wt/<agent-name>`，所有文件读写和 git 操作在该 worktree 内进行。
- **禁止子代理直接操作主仓库 `/workspace` 工作树**：避免与主代理或其他并行 worktree 冲突。
- **禁止 `git clone` 创建独立仓库**：所有工作必须基于主仓库的 worktree。

### 1.2 分支命名规范

所有 worktree 分支必须遵循统一命名格式，确保可追溯、可审计、可自动识别：

```
wt/<批次>-<任务组>-<代理编号>-<简述>
```

| 组成 | 说明 | 示例 |
|------|------|------|
| `wt/` | 固定前缀，标识 worktree 分支 | `wt/` |
| `<批次>` | 批次号 | `b4`、`b5`、`b6` |
| `<任务组>` | 任务组缩写 | `engine`、`frontend`、`security`、`test` |
| `<代理编号>` | 并行代理序号 | `01`、`02` |
| `<简述>` | 简短描述（kebab-case） | `mq-connectors` |

**完整示例**：`wt/b4-engine-01-mq-connectors`、`wt/b6-frontend-02-i18n-dark`

- **主分支**：`lowcode`（当前开发分支）或 `main`/`master`（保护分支，禁止直接提交）。
- **Worktree 分支生命周期**：创建→合并→删除，**禁止长期保留**（超过 24 小时未合并视为 P3 事故）。
- **命名校验**：主代理合并后执行 `git branch | grep 'wt/'` 确认无残留分支。

### 1.3 Worktree 生命周期管理

Worktree 经历完整的生命周期，每个阶段有明确的操作和状态校验：

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
```

#### 阶段 2：激活（ACTIVE）

子代理在 worktree 内工作。主代理记录：
```bash
# 记录 worktree 元信息（用于审计）
echo "$(date +%s),<branch-name>,ACTIVE" >> /workspace/.worktree-registry.csv
```

#### 阶段 3：提交（COMMITTED）

子代理在 worktree 内提交后，主代理验证提交存在：
```bash
cd /workspace/wt/<branch-name> && git log --oneline -1
# 记录 commit hash 以备验证
```

#### 阶段 4：合并（MERGED）

主代理执行合并（见 §1.4 合并协议）。合并成功后更新注册表：
```bash
echo "$(date +%s),<branch-name>,MERGED" >> /workspace/.worktree-registry.csv
```

#### 阶段 5：清理（CLEANED）

```bash
git worktree remove /workspace/wt/<branch-name> --force
git branch -D <branch-name>

# 校验清理成功
test ! -d /workspace/wt/<branch-name> || { echo "worktree 目录残留"; exit 1; }
git worktree list | grep <branch-name> && { echo "worktree 仍注册"; exit 1; } || true
echo "$(date +%s),<branch-name>,CLEANED" >> /workspace/.worktree-registry.csv
```

#### 阶段 6：归档（ARCHIVED）

合并记录保留在 `.worktree-registry.csv`，用于事后审计。注册表格式：
```
timestamp,branch-name,state
1783500000,wt/b4-engine-01-mq-connectors,CREATE
1783500600,wt/b4-engine-01-mq-connectors,COMMITTED
1783500900,wt/b4-engine-01-mq-connectors,MERGED
1783500905,wt/b4-engine-01-mq-connectors,CLEANED
```

#### 异常：恢复（RECOVER）

合并失败或验证失败时，worktree 进入恢复状态（见支柱四 §4.3）：
```bash
# 记录恢复事件
echo "$(date +%s),<branch-name>,RECOVER" >> /workspace/.worktree-registry.csv
# 按 SOP 处理后重新进入 MERGED 或重新创建
```

### 1.4 Worktree 合并协议（主代理负责）

子代理在 worktree 内提交后，**主代理负责执行合并**（非子代理自身）：

```bash
# 1. 主代理切换到主工作树
cd /workspace

# 2. 合并子代理分支（--no-ff 保留分支历史，便于审计）
git merge --no-ff <branch-name> -m "merge: <branch-name> 完成批次X"

# 3. 合并后立即验证（见支柱二）
bash scripts/verify.sh all

# 4. 验证通过后清理 worktree（见 §1.3 阶段 5）
```

- 如果合并产生冲突：按支柱四 §4.2 并发冲突恢复 SOP 处理。
- 如果合并后验证失败：`git reset --merge` 回退合并，要求子代理修复后重新提交。

### 1.5 Worktree 监控

- 主代理每批次结束时执行 `git worktree list` 确认无残留 worktree。
- 残留 worktree 视为 P3 事故，立即 `git worktree prune` + 手动清理。
- Worktree 数量上限：同时不超过 5 个（防止资源耗尽）。
- **超时清理**：worktree 创建超过 2 小时仍未合并，视为僵尸 worktree，主代理介入检查（P2 事故）。
- **注册表审计**：批次结束时检查 `.worktree-registry.csv`，确认所有 worktree 都达到 CLEANED 状态。

### 1.6 子代理任务描述规范

主代理派发子代理时，任务描述**必须包含**：

```
# 工作目录（Worktree，禁止 clone）
/workspace/wt/<branch-name>
所有文件读写和 git 操作必须在此目录内进行。

# 分支信息
当前 worktree 分支：<branch-name>（命名遵循 §1.2 规范）

# 提交要求
在 worktree 内提交，commit message 遵循结构化模板（见支柱二 §2.2）。
不要 push，不要合并到主分支——主代理会负责合并。
不要修改共享文件（router/index.ts 等），将改动写入 patches/ 目录（见支柱三 §3.3）。
```

---

## 支柱二：验证脚本化 + 结构化 Commit — 自动化验证，快速审计

> 消除"人工假设验证通过"的风险，提交信息可机器解析、可快速审计。

### 2.1 验证脚本化

- **统一验证脚本**：仓库根维护 `scripts/verify.sh`，封装全量验证：
  ```bash
  #!/bin/bash
  set -euo pipefail
  echo "==[1/3] Backend compile=="
  cd /workspace && mvn compile -q
  echo "==[2/3] Frontend typecheck=="
  cd /workspace/network-equipment-pms/pms-frontend && npx vue-tsc --noEmit
  echo "==[3/3] Git status clean check=="
  cd /workspace && test -z "$(git status --porcelain)" || { echo "工作区不干净"; exit 1; }
  echo "ALL CHECKS PASSED"
  ```
- **提交前必跑**：
  | 变更类型 | 验证命令 | 通过标准 |
  |---------|---------|---------|
  | 后端 Java | `mvn -pl <module> -am compile -q` | exit 0，无 ERROR |
  | 前端 TS/Vue | `npx vue-tsc --noEmit` | exit 0，0 errors |
  | 全量改动 | `scripts/verify.sh` | ALL CHECKS PASSED |
- **验证输出归档**：全量验证输出写入 `scripts/logs/verify-<timestamp>.log`，提交信息中引用。
- **验证失败禁止提交**：必须先修复至通过。

### 2.2 验证脚本前置安全检查

`scripts/verify.sh` 在执行实际编译/类型检查前，**必须先完成前置安全检查**，防止在错误环境运行导致误判：

```bash
#!/bin/bash
set -euo pipefail

# === 前置安全检查（pre-flight）===
preflight() {
    echo "==[Pre-flight] 安全前置检查=="

    # 1. 确认在主仓库根目录（防止在 worktree 或错误目录运行全量验证）
    local git_root
    git_root=$(git rev-parse --show-toplevel 2>/dev/null) || { echo "FAIL: 不在 git 仓库内"; exit 1; }
    [ "$git_root" = "/workspace" ] || { echo "FAIL: 必须在 /workspace 运行（当前 $git_root）"; exit 1; }

    # 2. 确认主分支干净（worktree 分支不跑全量验证）
    local branch
    branch=$(git branch --show-current)
    [ "$branch" = "lowcode" ] || [ "$branch" = "main" ] || { echo "FAIL: 全量验证仅在主分支运行（当前 $branch），worktree 分支请用 verify.sh backend/frontend"; exit 1; }

    # 3. 确认 Maven 可用
    command -v mvn >/dev/null 2>&1 || { echo "FAIL: mvn 未安装"; exit 1; }

    # 4. 确认 Node/npx 可用
    command -v npx >/dev/null 2>&1 || { echo "FAIL: npx 未安装"; exit 1; }

    # 5. 确认关键目录存在（防止仓库结构异常）
    [ -f "network-equipment-pms/pom.xml" ] || { echo "FAIL: 找不到 network-equipment-pms/pom.xml"; exit 1; }
    [ -d "network-equipment-pms/pms-frontend" ] || { echo "FAIL: 找不到 pms-frontend 目录"; exit 1; }

    # 6. 确认无残留 worktree（防止遗漏的并行工作未合并）
    local wt_count
    wt_count=$(git worktree list | wc -l)
    [ "$wt_count" -le 1 ] || { echo "WARN: 存在 $((wt_count - 1)) 个残留 worktree，合并后再验证"; }

    # 7. 确认磁盘空间充足（防止编译中途磁盘满）
    local free_mb
    free_mb=$(df -m /workspace | awk 'NR==2{print $4}')
    [ "$free_mb" -gt 1024 ] || { echo "FAIL: 磁盘空间不足 1GB（剩余 ${free_mb}MB）"; exit 1; }

    echo "Pre-flight: ALL CHECKS PASSED"
}

preflight
# === 实际验证逻辑接续 ===
```

**前置检查项清单**：

| # | 检查项 | 失败处理 |
|---|--------|---------|
| 1 | 确认在 `/workspace` 主仓库根目录 | 提示切换目录，exit 1 |
| 2 | 确认在主分支（lowcode/main）运行全量验证 | 提示 worktree 分支用单模块验证 |
| 3 | 确认 mvn 命令可用 | 提示安装 Maven，exit 1 |
| 4 | 确认 npx 命令可用 | 提示安装 Node.js，exit 1 |
| 5 | 确认关键目录结构完整 | 提示仓库异常，exit 1 |
| 6 | 确认无残留 worktree | WARN 警告（不阻断） |
| 7 | 确认磁盘空间 >1GB | 提示清理磁盘，exit 1 |

### 2.3 验证脚本的版本控制与完整性保护

验证脚本本身是规则执行的基石，**必须防止被篡改**：

#### 2.3.1 版本号管理

- `scripts/verify.sh` 头部声明版本号：`VERIFY_VERSION="1.0.0"`。
- 每次修改 verify.sh 必须递增版本号（语义化版本：MAJOR.MINOR.PATCH）。
- commit message 必须包含 `Verify-Version: <version>` trailer。

#### 2.3.2 完整性校验

- 仓库根维护 `scripts/verify.sha256`，存储 verify.sh 的 SHA-256 摘要。
- **生成摘要**：`sha256sum scripts/verify.sh > scripts/verify.sha256`
- **校验摘要**（verify.sh 启动时自校验）：
  ```bash
  # verify.sh 开头自校验
  self_check() {
      local script_path="$0"
      local expected_hash stored_hash actual_hash
      expected_hash=$(awk '{print $1}' scripts/verify.sha256)
      actual_hash=$(sha256sum "$script_path" | awk '{print $1}')
      if [ "$expected_hash" != "$actual_hash" ]; then
          echo "FAIL: verify.sh 完整性校验失败（可能被篡改）"
          echo "Expected: $expected_hash"
          echo "Actual:   $actual_hash"
          exit 1
      fi
  }
  self_check
  ```
- **更新摘要**：修改 verify.sh 后必须重新生成 sha256：`sha256sum scripts/verify.sh > scripts/verify.sha256`，两者一起提交。

#### 2.3.3 修改协议

- 修改 verify.sh 必须**同时**提交 `verify.sh` + `verify.sha256`，禁止单独提交其一。
- 如果 `verify.sha256` 缺失或校验失败，verify.sh 拒绝执行（防止运行被篡改的验证脚本导致误判通过）。
- verify.sh 的修改必须通过结构化 commit（type=`chore`，scope=`verify`），并在 commit message 中说明修改原因。

### 2.4 结构化 Commit 模板

每个 commit 必须遵循 Conventional Commits + 结构化 trailer：

```
<type>(<scope>): <subject>

<body: 任务内容 + 实现方式 + 测试结果>

Task-ID: <批次-任务号，如 b4-t5>
Files: <新增N/修改M/删除K>
Verify: <mvn compile: pass | vue-tsc: 0 errors>
Worktree: <wt/agent-name 或 main>
Reviewed-by: <主代理>
```

- **type**：feat/fix/docs/refactor/test/chore
- **scope**：模块名（lowcode/lowcode-frontend/system 等）
- **禁止模糊提交信息**：如 "update"、"fix bug"、"wip" 等一律禁止。
- **一个 commit 一个逻辑单元**：禁止把多个不相关 Task 塞进一个 commit。

### 2.5 提交后复核（强制）

每个提交完成后，主代理必须执行复核：

```bash
git log --oneline -1          # 确认提交在 HEAD
git show --stat HEAD           # 确认文件列表符合预期
git status --short             # 确认工作区状态
```

- 如果 `git show --stat HEAD` 的文件列表与预期不符（多了或少了），必须立即修正。

### 2.6 提交验证协议（防丢失）

主代理收到子代理返回的 commit hash 后，**必须立即在主仓库验证**：

```bash
cd /workspace && git log --oneline | grep <commit-hash>
```

- 验证失败（hash 不在主仓库 log 中）→ 视为提交丢失，按支柱四 SOP 恢复。
- 验证成功方可继续下一批任务。
- **禁止**仅凭子代理自报 hash 就认为提交成功——以主仓库 `git log` 实际显示为准。

---

## 支柱三：共享文件锁/队列机制 — 消除串行瓶颈，防调度失误

> 多个并行子代理需要修改同一文件时，用锁/队列协调，避免覆盖丢失。

### 3.1 共享文件识别

主代理在规划并行任务时，**必须识别共享文件**（多个子代理都需要修改的文件），典型共享文件：
- `pms-frontend/src/router/index.ts`
- `pms-frontend/src/api/lowcode.ts`
- `pms-admin/src/main/resources/application.yml`
- 任何 `pom.xml`
- 任何 `SecurityConfig.java` / `main.ts` / `App.vue`

### 3.2 共享文件锁机制（适用低竞争）

- **锁文件**：`/workspace/.file-locks/<file-path-hash>.lock`，内容为持有者 agent-name + 时间戳。
- **获取锁**：
  ```bash
  LOCK=/workspace/.file-locks/$(echo "<file-path>" | md5sum | cut -d' ' -f1).lock
  # 带超时的获取（防死锁等待）
  WAIT=0
  while [ -f "$LOCK" ] && [ $WAIT -lt 300 ]; do sleep 2; WAIT=$((WAIT+2)); done
  # 超时检查：锁文件存在但超时，强制接管
  if [ -f "$LOCK" ]; then
      LOCK_AGE=$(( $(date +%s) - $(cut -d: -f2 "$LOCK") ))
      [ $LOCK_AGE -gt 300 ] && { echo "WARN: 锁超时 ${LOCK_AGE}s，强制接管"; rm -f "$LOCK"; } || { echo "FAIL: 获取锁超时"; exit 1; }
  fi
  echo "<agent-name>:$(date +%s)" > "$LOCK"
  ```
- **释放锁**：修改完成并提交后 `rm -f "$LOCK"`。
- **锁超时**：持有超过 300 秒视为死锁，其他代理可强制接管（记录告警日志）。
- **锁原子性**：获取和释放必须配对（try-finally），异常退出时锁可能残留，由超时机制兜底。

#### 锁死锁预防机制

单纯的 `while [ -f "$LOCK" ]` 等待存在死锁风险（持有者崩溃不释放）。预防措施：

| 风险 | 预防机制 |
|------|---------|
| 持有者崩溃不释放锁 | 锁文件含时间戳，超时 300s 后其他代理可强制接管 |
| 等待者无限等待 | 获取锁带 `WAIT` 计数器，超 300s 报错退出（非无限循环） |
| 多代理同时强制接管 | 接管前二次检查锁年龄，且 `rm -f` + `echo` 非原子但竞争窗口极小（可接受） |
| 锁文件残留堆积 | 主代理每批次结束扫描 `/workspace/.file-locks/` 清理超时锁 |

```bash
# 主代理批次末锁清理（扫除所有超时锁）
cleanup_stale_locks() {
    local now lock_age
    now=$(date +%s)
    for lock in /workspace/.file-locks/*.lock; do
        [ -f "$lock" ] || continue
        lock_age=$(( now - $(cut -d: -f2 "$lock") ))
        [ $lock_age -gt 600 ] && { echo "CLEANUP: 删除超时锁 $lock (age ${lock_age}s)"; rm -f "$lock"; }
    done
}
```

### 3.3 共享文件队列模式（推荐，适用高竞争）

对于高竞争共享文件（如 `router/index.ts`），**优先采用队列模式而非锁**：

1. 子代理任务描述声明："需要新增路由 X，但**不要直接修改 router/index.ts**，将路由片段写入 `patches/router-<agent>.ts`"。
2. 子代理在 worktree 内创建补丁片段文件，不碰共享文件。
3. 所有子代理完成后，主代理一次性合并所有 `patches/router-*.ts` 到 `router/index.ts` 并提交。

#### 队列死锁预防机制

队列模式本身无锁，但存在另一种"死锁"——**补丁片段缺失导致主代理合并卡住**：

| 风险 | 预防机制 |
|------|---------|
| 子代理未生成补丁片段就崩溃 | 主代理合并前校验：`ls patches/router-*.ts` 必须覆盖所有声明的子代理 |
| 补丁片段语法错误导致合并失败 | 主代理合并后跑 verify.sh，失败则回退 + 标记该子代理为需修复 |
| 补丁片段冲突（两个子代理改同一行） | 主代理按 agent-name 排序顺序合并，冲突时后者追加而非覆盖 |
| 补丁片段残留（已合并但未清理） | 合并成功后 `rm -f patches/router-*.ts`，批次末校验 patches/ 为空 |

```bash
# 主代理合并补丁片段（带校验）
merge_patches() {
    local target="$1"        # 如 router/index.ts
    local patch_glob="$2"    # 如 patches/router-*.ts

    # 1. 校验补丁片段存在
    local count
    count=$(ls $patch_glob 2>/dev/null | wc -l)
    [ "$count" -gt 0 ] || { echo "FAIL: 无补丁片段 $patch_glob"; return 1; }

    # 2. 逐个追加合并（排序确保可重现）
    for patch in $(ls $patch_glob | sort); do
        echo "MERGE: $patch"
        # 具体合并逻辑取决于补丁格式（TS import 追加 / 路由数组追加）
    done

    # 3. 验证
    bash scripts/verify.sh frontend || { echo "FAIL: 合并后验证失败，回退"; git checkout -- "$target"; return 1; }

    # 4. 清理补丁
    rm -f $patch_glob
    echo "MERGE DONE: $count patches merged into $target"
}
```

### 3.4 提交原子性

- 每个子代理只能提交归属于自己任务的文件，**禁止 `git add -A` 或 `git add .`**。
- 必须按文件路径精确 `git add <file1> <file2> ...`。
- 合并时如遇冲突，按支柱四 §4.2 SOP 解决。

---

## 支柱四：标准化恢复 SOP — 降低故障恢复时间

> 发生提交丢失、冲突、worktree 损坏等故障时，按标准操作流程快速恢复。

### 4.1 提交丢失恢复 SOP

**症状**：子代理自报 commit hash，但主仓库 `git log` 中找不到。

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
3. 重新创建 worktree：`git worktree add /workspace/wt/<name> -b wt/<name>`。
4. 重新实施丢失的工作。

### 4.4 事故分级与上报

| 级别 | 定义 | 处理 | 上报 |
|------|------|------|------|
| P0 | 整批提交丢失，数据不可恢复 | 立即停止所有工作，重新实施 | 立即告知用户 |
| P1 | 单个提交丢失，可从 worktree 恢复 | cherry-pick 恢复，继续工作 | 记录日志，批次末汇报 |
| P2 | 并发冲突，可自动解决 | 按 4.2 SOP 解决 | 记录日志 |
| P3 | Worktree 残留，无数据丢失 | prune 清理，继续工作 | 无需上报 |

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
- 用户指定的两条原始规则已融入支柱一（1.2 合并协议）和支柱二（2.2 结构化 Commit + 2.3 提交后复核），继续有效。
