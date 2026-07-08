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
- **子代理 Worktree 创建**（主代理负责）：
  ```bash
  cd /workspace && git worktree add /workspace/wt/<agent-name> -b wt/<agent-name>
  ```
- **子代理工作目录**：子代理的 `cwd` 必须设为 `/workspace/wt/<agent-name>`，所有文件读写和 git 操作在该 worktree 内进行。
- **禁止子代理直接操作主仓库 `/workspace` 工作树**：避免与主代理或其他并行 worktree 冲突。
- **禁止 `git clone` 创建独立仓库**：所有工作必须基于主仓库的 worktree。

### 1.2 Worktree 合并协议（主代理负责）

子代理在 worktree 内提交后，**主代理负责执行合并**（非子代理自身）：

```bash
# 1. 主代理切换到主工作树
cd /workspace

# 2. 合并子代理分支（优先 rebase 保持线性历史）
git merge --no-ff wt/<agent-name> -m "merge: <agent-name> 完成批次X"

# 3. 合并后立即验证（见支柱二）
mvn compile -q && cd network-equipment-pms/pms-frontend && npx vue-tsc --noEmit

# 4. 验证通过后清理 worktree
git worktree remove /workspace/wt/<agent-name> --force
git branch -D wt/<agent-name>
```

### 1.3 Worktree 监控

- 主代理每批次结束时执行 `git worktree list` 确认无残留 worktree。
- 残留 worktree 视为 P3 事故，立即 `git worktree prune` + 手动清理。
- Worktree 数量上限：同时不超过 5 个（防止资源耗尽）。

### 1.4 子代理任务描述规范

主代理派发子代理时，任务描述**必须包含**：

```
# 工作目录（Worktree，禁止 clone）
/workspace/wt/<agent-name>
所有文件读写和 git 操作必须在此目录内进行。

# 提交要求
在 worktree 内提交，commit message 遵循结构化模板（见支柱二）。
不要 push，不要合并到主分支——主代理会负责合并。
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
- **验证输出归档**：全量验证输出写入 `scripts/verify-<timestamp>.log`，提交信息中引用。
- **验证失败禁止提交**：必须先修复至通过。

### 2.2 结构化 Commit 模板

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

### 2.3 提交后复核（强制）

每个提交完成后，主代理必须执行复核：

```bash
git log --oneline -1          # 确认提交在 HEAD
git show --stat HEAD           # 确认文件列表符合预期
git status --short             # 确认工作区状态
```

- 如果 `git show --stat HEAD` 的文件列表与预期不符（多了或少了），必须立即修正。

### 2.4 提交验证协议（防丢失）

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
  while [ -f "$LOCK" ]; do sleep 2; done
  echo "<agent-name>:$(date +%s)" > "$LOCK"
  ```
- **释放锁**：修改完成并提交后 `rm -f "$LOCK"`。
- **锁超时**：持有超过 300 秒视为死锁，其他代理可强制接管（记录告警日志）。

### 3.3 共享文件队列模式（推荐，适用高竞争）

对于高竞争共享文件（如 `router/index.ts`），**优先采用队列模式而非锁**：

1. 子代理任务描述声明："需要新增路由 X，但**不要直接修改 router/index.ts**，将路由片段写入 `patches/router-<agent>.ts`"。
2. 子代理在 worktree 内创建补丁片段文件，不碰共享文件。
3. 所有子代理完成后，主代理一次性合并所有 `patches/router-*.ts` 到 `router/index.ts` 并提交。

### 3.4 提交原子性

- 每个子代理只能提交归属于自己任务的文件，**禁止 `git add -A` 或 `git add .`**。
- 必须按文件路径精确 `git add <file1> <file2> ...`。
- 合并时如遇冲突，按支柱四 D.2 SOP 解决。

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
