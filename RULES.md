# 工作规则（硬性强制）

> 本规则适用于所有在主仓库 `/workspace` 及其子目录工作的代理（主代理与子代理）。
> 任何违反本规则导致的代码丢失、提交错乱、数据不一致，均视为重大事故。
> 本规则与 `AGENTS.md` 并列，共同构成上下文约束。

---

## 一、提交与同步规则（用户指定，不可违反）

### 规则 1：子代理工作树必须合并回主仓库

当任何子代理（通过 Task 工具派发）完成其独立工作树内的所有任务和修改后，**必须立即通过标准提交流程将变更合并到主仓库**（`/workspace/.git`），确保代码库的完整性和一致性。

- 子代理不得仅在自身临时工作树内提交后即返回，必须确认提交已落到主仓库 `/workspace/.git`。
- 合并前必须执行本地测试（编译、类型检查），确保没有引入新的错误或冲突。
- 主代理在派发子代理任务时，**必须在任务描述中显式要求子代理将最终提交同步到主仓库路径**，而非子代理的临时路径。

### 规则 2：验证通过后立即提交

任何任务在经过验证确认正确完成后，**必须立即进行提交操作**，不得积压。

- 提交信息必须清晰、规范，包含：任务内容、实现方式、相关测试结果。
- 任务完成标准：功能实现符合需求规格 + 通过所有相关测试用例 + 代码质量符合项目标准。
- 禁止"先做完所有任务再一起提交"的积压模式——每完成一个可验证的功能单元即提交一次。

---

## 二、数据一致性规则（防丢失，强制执行）

### 规则 3：单一仓库原则

- **主仓库唯一路径**：`/workspace/.git`。所有代码修改必须在 `/workspace` 工作树内进行。
- **禁止子代理使用独立 clone**：派发子代理任务时，子代理必须在 `/workspace` 工作树内直接操作（读写文件、git 命令），**不得执行 `git clone` 创建独立仓库**。
- **禁止在 `/tmp`、`/root` 或其他路径创建仓库副本**：所有工作必须在 `/workspace` 内完成。

### 规则 4：子代理提交验证协议

主代理在收到子代理返回的 commit hash 后，**必须立即在主仓库验证**：

```bash
cd /workspace && git log --oneline | grep <commit-hash>
```

- 如果验证失败（commit hash 不在主仓库 log 中），**视为子代理提交丢失**，必须立即重新实施，不得继续后续任务。
- 如果验证成功，方可继续下一批任务。
- **禁止**仅凭子代理自报的 hash 就认为提交成功——必须以主仓库 `git log` 实际显示为准。

### 规则 5：提交原子性与文件隔离

- 每个子代理只能提交归属于自己任务的文件，**不得 `git add -A` 或 `git add .`**（会误提交其他并行代理的文件）。
- 必须按文件路径精确 `git add <file1> <file2> ...`。
- 并行子代理共享的文件（如 `router/index.ts`、`pom.xml`）：最后一个提交的代理负责合并所有并行改动，或主代理在所有子代理完成后统一提交共享文件。

### 规则 6：分支与 HEAD 保护

- **禁止**对主分支执行 `git reset --hard`、`git checkout .`、`git clean -f` 等破坏性操作，除非用户明确要求。
- **禁止** `git push --force` 到远程，除非用户明确要求。
- 每完成一个重要里程碑（如一个批次），**必须打 git tag** 作为检查点，tag 名格式 `m{里程碑}-batch{批次}-{描述}`。
- 打 tag 后验证 `git tag -l | grep <tag-name>` 确认 tag 已创建。

### 规则 7：并发冲突防护

- 多个子代理并行工作时，**禁止同时修改同一个文件**（主代理在派发任务时必须明确划分文件归属）。
- 如果确实需要多个代理修改同一文件（如 `router/index.ts`），采用**串行依赖**模式：前一个代理完成后，主代理验证提交，再派发后一个代理。
- 子代理提交时如遇到 `git add` 冲突（文件已被其他代理 staged），必须 `git reset HEAD -- <冲突文件>` 后重新精确添加自己的文件，**不得强制覆盖**。

---

## 三、测试与验证规则

### 规则 8：提交前强制本地验证

每个提交前必须执行对应的验证命令，且验证必须通过（exit code 0）：

| 变更类型 | 验证命令 | 通过标准 |
|---------|---------|---------|
| 后端 Java | `cd /workspace && mvn -pl <module> -am compile -q` | exit 0，无 ERROR |
| 前端 TS/Vue | `cd /workspace/network-equipment-pms/pms-frontend && npx vue-tsc --noEmit` | exit 0，0 errors |
| Flyway 迁移 | 人工核查 SQL 语法 + 版本号不冲突 | 无重复版本号 |
| 全量改动 | `cd /workspace && mvn compile -q` | exit 0 |

- 验证失败时**禁止提交**，必须先修复至通过。
- 验证命令的输出必须实际查看（不得假设通过），特别关注 `ERROR`、`error TS`、`BUILD FAILURE` 等关键词。

### 规则 9：提交后复核

每个提交完成后，主代理必须执行复核：

```bash
git log --oneline -1          # 确认提交在 HEAD
git show --stat HEAD           # 确认文件列表符合预期
git status --short             # 确认工作区状态（无遗漏的未提交文件，或合理的 untracked）
```

- 如果 `git show --stat HEAD` 的文件列表与预期不符（多了或少了），必须立即修正。

---

## 四、工作流程规则

### 规则 10：子代理任务描述必须包含仓库路径

主代理通过 Task 工具派发子代理时，任务描述中**必须显式声明**：

```
# 工作目录（必须在主仓库工作树内）
/workspace（或 /workspace/network-equipment-pms）
禁止 git clone，禁止在 /tmp 等路径创建副本。
```

### 规则 11：里程碑检查点

每完成一个批次或重要阶段，必须：

1. 执行全量编译验证（后端 + 前端）
2. 打 git tag 作为检查点
3. 验证 tag 已创建：`git tag -l | grep <tag>`
4. 更新 todo 列表标记完成
5. 向用户汇报该里程碑的完成情况

### 规则 12：异常处理与回滚预案

- 如果发现提交丢失（规则 4 验证失败），立即停止后续任务，优先重新实施丢失的部分。
- 如果发现并发冲突导致代码覆盖，使用 `git reflog` 查找历史提交，`git cherry-pick` 恢复。
- 重大事故（如整批提交丢失）必须立即告知用户，不得隐瞒。

---

## 五、核心工程原则（四支柱）

以下四项原则是本规则的工程方法论核心，所有具体规则均围绕其展开。

### 原则 A：Worktree 支持 — 物理隔离，真并行

**目标**：让多个子代理真正并行工作，互不干扰，同时保证可合并回主仓库。

- **主仓库路径**：`/workspace/.git`（bare 不适用，使用普通仓库）。
- **Worktree 创建**：主代理派发并行子代理前，为每个子代理创建独立 worktree：
  ```bash
  cd /workspace && git worktree add /workspace/wt-<agent-name> -b wt/<agent-name>
  ```
- **子代理工作目录**：子代理的 `cwd` 必须设为 `/workspace/wt-<agent-name>`，所有文件读写和 git 操作在该 worktree 内进行。
- **合并回主仓库**：子代理在 worktree 内提交后，**主代理负责执行合并**（非子代理自身），合并协议见下文「原则 B」。
- **Worktree 清理**：合并完成后立即清理：
  ```bash
  git worktree remove /workspace/wt-<agent-name> --force
  git branch -D wt/<agent-name>
  ```
- **Worktree 列表监控**：主代理每批次结束时执行 `git worktree list` 确认无残留 worktree。
- **禁止子代理直接操作主仓库 `/workspace` 工作树**：避免与主代理或其他并行 worktree 冲突。

### 原则 B：验证脚本化 + 结构化 Commit — 自动化验证，快速审计

**目标**：消除"人工假设验证通过"的风险，提交信息可机器解析、可快速审计。

#### B.1 验证脚本化

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
- **提交前必跑**：每个 commit 前必须运行对应验证（后端改动跑 mvn compile，前端改动跑 vue-tsc），全量改动跑 `scripts/verify.sh`。
- **验证输出归档**：验证输出写入 `scripts/verify-<timestamp>.log`，提交信息中引用日志路径。
- **CI 本地化**：验证脚本即本地 CI，任何验证失败禁止提交。

#### B.2 结构化 Commit

- **Commit 信息模板**（Conventional Commits + 结构化 trailer）：
  ```
  <type>(<scope>): <subject>

  <body: 任务内容 + 实现方式 + 测试结果>

  Task-ID: <批次-任务号，如 b4-t5>
  Files: <新增N/修改M/删除K>
  Verify: <mvn compile: pass | vue-tsc: 0 errors>
  Worktree: <wt/agent-name 或 main>
  Reviewed-by: <主代理>
  ```
- **禁止模糊提交信息**：如 "update"、"fix bug"、"wip" 等一律禁止。
- **一个 commit 一个逻辑单元**：禁止把多个不相关 Task 塞进一个 commit。

### 原则 C：共享文件锁/队列机制 — 消除串行瓶颈，防调度失误

**目标**：多个并行子代理需要修改同一文件时，用锁机制协调，避免覆盖丢失。

#### C.1 共享文件识别

主代理在规划并行任务时，必须识别"共享文件"（多个子代理都需要修改的文件），典型共享文件：
- `pms-frontend/src/router/index.ts`
- `pms-frontend/src/api/lowcode.ts`
- `pms-admin/src/main/resources/application.yml`
- 任何 `pom.xml`

#### C.2 共享文件锁机制

- **锁文件**：`/workspace/.file-locks/<file-path-hash>.lock`，内容为当前持有者 agent-name + 时间戳。
- **获取锁**：子代理修改共享文件前必须获取锁：
  ```bash
  LOCK=/workspace/.file-locks/$(echo "<file-path>" | md5sum | cut -d' ' -f1).lock
  while [ -f "$LOCK" ]; do sleep 2; done
  echo "<agent-name>:$(date +%s)" > "$LOCK"
  ```
- **释放锁**：修改完成并提交后释放：
  ```bash
  rm -f "$LOCK"
  ```
- **锁超时**：锁持有超过 300 秒视为死锁，其他代理可强制接管（记录告警日志）。

#### C.3 共享文件队列模式（推荐）

对于高竞争共享文件（如 `router/index.ts`），**优先采用串行队列模式而非锁**：
- 主代理把所有子代理对共享文件的改动收集为"补丁片段"。
- 所有子代理完成后，主代理一次性应用所有补丁片段并提交。
- 子代理任务描述中声明"需要新增路由 X，但不要直接修改 router/index.ts，将路由片段写入 `patches/router-<agent>.ts`"。
- 主代理最后合并所有 `patches/router-*.ts`。

### 原则 D：标准化恢复 SOP — 降低故障恢复时间

**目标**：发生提交丢失、冲突、worktree 损坏等故障时，按标准操作流程快速恢复。

#### D.1 提交丢失恢复 SOP

**症状**：子代理自报 commit hash，但主仓库 `git log` 中找不到。

**恢复步骤**：
1. 立即停止派发新子代理。
2. 检查所有 worktree：`git worktree list`，进入每个 worktree 执行 `git log --oneline -5`。
3. 如果在某个 worktree 找到提交：`git cherry-pick <hash>` 合并回主分支。
4. 如果所有 worktree 都没有：检查 `git fsck --lost-found` 寻找 dangling commit。
5. 如果仍找不到：确认数据不可恢复，记录事故，重新实施。

#### D.2 并发冲突恢复 SOP

**症状**：`git commit` 报错 "Your local changes would be overwritten" 或 `git add` 发现文件已被其他代理 staged。

**恢复步骤**：
1. `git status` 查看冲突文件。
2. `git stash push -m "conflict-rescue-<timestamp>"` 暂存自己的改动。
3. `git pull --rebase` 或等待其他代理提交完成。
4. `git stash pop` 恢复改动，手动解决冲突。
5. 冲突解决后重新验证 + 提交。

#### D.3 Worktree 损坏恢复 SOP

**症状**：worktree 目录被误删或 `git worktree` 状态异常。

**恢复步骤**：
1. `git worktree prune` 清理无效引用。
2. 如果 worktree 内有未合并的提交：`git fsck --lost-found` 在主仓库寻找 dangling commit。
3. 重新创建 worktree：`git worktree add /workspace/wt-<name> -b wt/<name>`。
4. 重新实施丢失的工作。

#### D.4 事故分级与上报

| 级别 | 定义 | 处理 |
|------|------|------|
| P0 | 整批提交丢失，数据不可恢复 | 立即停止所有工作，告知用户，重新实施 |
| P1 | 单个提交丢失，可从 worktree 恢复 | cherry-pick 恢复，继续工作 |
| P2 | 并发冲突，可自动解决 | 按 D.2 SOP 解决，记录日志 |
| P3 | Worktree 残留，无数据丢失 | prune 清理，继续工作 |

---

## 六、附则

- 本规则优先级高于 `AGENTS.md` 中的任何冲突条款。
- 本规则随项目演进持续更新，每次更新需在提交信息中注明规则变更内容。
- 所有代理（主代理与子代理）在开始工作前必须读取并遵守本规则。
- 四支柱原则（Worktree/验证脚本化/共享文件锁/恢复 SOP）是方法论核心，具体规则是其落地实现。
