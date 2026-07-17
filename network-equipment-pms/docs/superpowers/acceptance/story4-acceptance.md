# Story 4 端到端验收报告 — 循环依赖检测 + 基线偏差分析

> Phase 8 / Task 8.9 — 端到端验收场景 4（Story 4）代码审查
> 验收场景：
>   4.1 循环依赖被拒，指出闭环路径（保存任务依赖时 DFS 检测闭环）
>   4.2 关键日期变更展示差异，记录原因（基线偏差分析 + 双阈值触发审批）
> 关联设计文档：§9.1（行 2549-2550）、§3.6（行 501-538）、§5.5（行 962-1022）、§6.6/§6.7
> 校验方式：静态代码审查
> 校验日期：2026-07-17

## 0. 验收范围与依据

| 维度 | 说明 |
|---|---|
| 后端模块 | `pms-baseline`（新建模块：基线快照 + 任务依赖 + 偏差分析） |
| 数据库迁移 | V68 `pms_task_dependency`、V69 `pms_baseline_snapshot` |
| 涉及实体 | `TaskDependency`、`BaselineSnapshot`、`TaskPlanSnapshot`、`TaskDiff`、`CycleNode`、`DependencyCycleResult`、`BaselineDiffResult` |
| 前端模块 | `api/baseline.ts`、`api/task-dependency.ts`、`views/baseline/{index,diff}.vue`、`views/task/dependency/index.vue`、`components/{BaselineDiffTable,DependencyGraph}.vue` |
| 设计验收点 | §9.1 行 2549-2550；§5.5 行 973-1022 响应结构样例 |

---

## 1. 验收场景 4.1 — 循环依赖被拒，指出闭环路径

### 1.1 验收步骤

```
Given  项目 P1001 下已有依赖：
       - 任务A(101) → 任务B(102)  (FS)
       - 任务B(102) → 任务C(103)  (FS)
When   用户在「任务依赖关系图」页新增依赖：任务C(103) → 任务A(101)  (FS)
Then   系统拦截保存，返回 HTTP 200 + data.success=false / errorCode=CYCLE_DETECTED
       data.cyclePath = [
         {taskId:101, taskName:"任务A"},
         {taskId:102, taskName:"任务B"},
         {taskId:103, taskName:"任务C"},
         {taskId:101, taskName:"任务A"}   // 首尾闭合
       ]
       前端展示红色 alert「检测到循环依赖」+ 闭环路径文本 + G6 图高亮闭环节点/边
       依赖关系不持久化（事务回滚）
```

### 1.2 链路代码审查

#### (1) 后端 Controller

**文件**：`pms-baseline/.../controller/TaskDependencyController.java` 行 50-56

```java
@Operation(summary = "保存任务依赖（含循环检测）")
@PostMapping
@PreAuthorize("hasAuthority('project:baseline:save')")
@OperLog(title = "任务依赖", businessType = 1)
public Result<TaskDependency> save(@Valid @RequestBody TaskDependency dependency) {
    return Result.ok(taskDependencyService.saveDependency(dependency));
}
```

**校验**：
- API 路径 `POST /api/implementation/task/dependency` 与设计 §5.5 行 966 一致 ✅
- 权限码 `project:baseline:save` 与设计 §5.5 一致 ✅
- 注：设计文档原文标注 `@RequiresPermissions`（Shiro），实际采用 Spring Security `@PreAuthorize`（与 pms-implementation 一致），权限码不变，符合统一约定 ✅

#### (2) 后端 Service — saveDependency（5 步校验 + DFS 闭环检测）

**文件**：`pms-baseline/.../service/impl/TaskDependencyServiceImpl.java` 行 42-92

```java
@Override
@Transactional(rollbackFor = Exception.class)
public TaskDependency saveDependency(TaskDependency dependency) {
    // 1. 依赖类型校验（FS/FF/SS/SF）
    if (dependency.getDependencyType() == null
            || !VALID_TYPES.contains(dependency.getDependencyType())) {
        throw new BusinessException("依赖类型非法，仅支持 FS/FF/SS/SF");
    }
    // 2. 自环检测：predecessor == successor
    if (predecessorId.equals(successorId)) {
        throw new BusinessException("任务不能依赖自身");
    }
    // 3. 校验前置/后续任务存在
    String predName = implTaskMapper.selectTaskNameById(predecessorId);
    String succName = implTaskMapper.selectTaskNameById(successorId);
    // 4. 闭环检测：从 successor 沿 predecessor→successor 边 DFS，若能回到 predecessor 则闭环
    List<Long> cycleIds = detectCycle(successorId, predecessorId, dependency.getProjectId());
    if (!cycleIds.isEmpty()) {
        // 拼装闭环路径节点（含首尾闭合节点）
        List<CycleNode> cyclePath = new ArrayList<>();
        for (Long taskId : cycleIds) {
            String name = implTaskMapper.selectTaskNameById(taskId);
            cyclePath.add(CycleNode.builder().taskId(taskId).taskName(name).build());
        }
        throw new CycleDetectedException(cyclePath);
    }
    // 5. 无闭环 → 保存
    this.save(dependency);
    return dependency;
}
```

**校验**：
- 5 步校验顺序与设计 §3.6 行 505-520 伪代码一致（自环 → 闭环 → 插入）✅
- 自环检测先于闭环检测，避免 DFS 退化 ✅
- `@Transactional(rollbackFor = Exception.class)` 保证检测失败时无脏数据 ✅
- 闭环路径节点通过 `implTaskMapper.selectTaskNameById` 回填任务名，符合前端展示需求 ✅

#### (3) 后端 Service — detectCycle + dfs（DFS 闭环路径回溯）

**文件**：`TaskDependencyServiceImpl.java` 行 121-163

```java
private List<Long> detectCycle(Long start, Long target, Long projectId) {
    // 构建邻接表：predecessor -> [successor...]
    List<TaskDependency> deps = this.list(new LambdaQueryWrapper<TaskDependency>()
            .eq(TaskDependency::getProjectId, projectId));
    Map<Long, List<Long>> adjacency = new LinkedHashMap<>();
    for (TaskDependency dep : deps) {
        adjacency.computeIfAbsent(dep.getPredecessorTaskId(), k -> new ArrayList<>())
                .add(dep.getSuccessorTaskId());
    }
    Set<Long> visited = new HashSet<>();
    List<Long> path = new ArrayList<>();
    if (dfs(start, target, adjacency, visited, path)) {
        // path 为 start...target，追加 start 闭合
        path.add(start);
        return path;
    }
    return Collections.emptyList();
}

private boolean dfs(Long current, Long target, Map<Long, List<Long>> adjacency,
                    Set<Long> visited, List<Long> path) {
    path.add(current);
    if (current.equals(target)) {
        return true;
    }
    visited.add(current);
    for (Long next : adjacency.getOrDefault(current, Collections.emptyList())) {
        if (!visited.contains(next)) {
            if (dfs(next, target, adjacency, visited, path)) {
                return true;
            }
        }
    }
    // 回溯：当前分支未命中，移除 current
    path.remove(path.size() - 1);
    return false;
}
```

**校验**：
- 算法选型 DFS（设计决策点 12「DFS 拓扑排序，返回闭环路径」一致）✅
- 起点 = 新增边的 successor，目标 = 新增边的 predecessor，方向正确（沿 predecessor→successor 边）✅
- 闭环路径首尾追加 start 形成闭合（A→B→C→A），符合设计 §5.5 行 982-988 样例 ✅
- 回溯逻辑正确：未命中目标时移除当前节点，保证 path 仅含真实路径 ✅
- `visited` 集合避免重复访问，防止指数级爆炸 ✅

#### (4) 异常类 + DTO

**文件**：
- `pms-baseline/.../exception/CycleDetectedException.java` 行 18-46
- `pms-baseline/.../dto/CycleNode.java` 行 21-31
- `pms-baseline/.../dto/DependencyCycleResult.java` 行 23-39

```java
@Getter
public class CycleDetectedException extends RuntimeException {
    public static final String ERROR_CODE = "CYCLE_DETECTED";
    private final List<CycleNode> cyclePath;  // 闭环路径节点（含首尾闭合节点）

    public CycleDetectedException(List<CycleNode> cyclePath) {
        super(buildMessage(cyclePath));  // "形成循环依赖，闭环路径: 任务A → 任务B → ..."
        this.cyclePath = cyclePath;
    }
}

// CycleNode: { taskId, taskName }
// DependencyCycleResult: { success, errorCode, errorMessage, cyclePath }
```

**校验**：
- `ERROR_CODE = "CYCLE_DETECTED"` 与设计 §5.5 行 980 一致 ✅
- `errorMessage` 通过 `buildMessage` 拼装「任务A → 任务B → 任务C → 任务A」，与设计 §5.5 行 981 一致 ✅
- `cyclePath` 结构 `[{taskId, taskName}]` 与设计 §5.5 行 982-988 一致 ✅

#### (5) 异常处理器 — 转换为 HTTP 200 + 结构化失败数据

**文件**：`pms-baseline/.../advice/BaselineExceptionHandler.java` 行 22-35

```java
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class BaselineExceptionHandler {

    @ExceptionHandler(CycleDetectedException.class)
    public Result<DependencyCycleResult> handleCycleDetected(CycleDetectedException e) {
        log.warn("检测到循环依赖，拦截保存依赖：{}", e.getMessage());
        DependencyCycleResult result = DependencyCycleResult.builder()
                .success(false)
                .errorCode(CycleDetectedException.ERROR_CODE)
                .errorMessage(e.getMessage())
                .cyclePath(e.getCyclePath())
                .build();
        // 设计要求 code=200 + data.success=false，便于前端按业务结果分支处理
        return Result.ok(result);
    }
}
```

**校验**：
- `@Order(Ordered.HIGHEST_PRECEDENCE)` 保证优先于全局异常处理器捕获 ✅
- 返回 `Result.ok(result)` 即 HTTP 200 + `data.success=false`，与设计 §5.5 行 975-990 响应样例完全一致 ✅
- 与设计 §3.6 行 515 伪代码 `Result.fail("CYCLE_DETECTED", ...)` 字面描述有偏差，但实现采用的 `Result.ok(success=false)` 更符合前端响应拦截器统一约定（响应拦截器按 `code` 判断成功，业务结果按 `data.success` 判断），属合理实现偏差（见技术债 TD-P8-010）

#### (6) 前端 API 封装

**文件**：`pms-frontend/src/api/task-dependency.ts` 行 56-70

```typescript
/**
 * 保存任务依赖（含 DFS 循环检测）。
 * 检测到循环依赖时，Promise 会 resolve（非 reject）一个 DependencyCycleResult
 * （success=false），调用方需判断返回值的 success 字段以区分两种业务结果。
 */
export function saveDependency(
  data: TaskDependency
): Promise<TaskDependency | DependencyCycleResult> {
  return post<TaskDependency | DependencyCycleResult>(
    '/api/implementation/task/dependency',
    data
  )
}
```

**校验**：
- 返回类型联合类型 `TaskDependency | DependencyCycleResult` 明确表达两种业务结果 ✅
- 注释清晰说明「Promise resolve 而非 reject」，避免调用方误用 try/catch ✅
- `DependencyCycleResult` 接口（行 44-49）`success: false` / `errorCode: 'CYCLE_DETECTED'` 与后端 DTO 字段对齐 ✅

#### (7) 前端视图 — 闭环路径展示 + G6 高亮

**文件**：`pms-frontend/src/views/task/dependency/index.vue` 行 84-114、190-214

```vue
<script setup lang="ts">
const cycleResult = ref<DependencyCycleResult | null>(null)

/** 闭环路径任务ID列表（首尾相同），传给图组件高亮 */
const highlightCycle = computed<number[]>(() =>
  cycleResult.value ? cycleResult.value.cyclePath.map((n) => n.taskId) : []
)

/** 闭环路径文本：任务A → 任务B → ... → 任务A */
const cyclePathText = computed(() =>
  cycleResult.value
    ? cycleResult.value.cyclePath.map((n) => n.taskName ?? `#${n.taskId}`).join(' → ')
    : ''
)

async function handleSaveDependency() {
  // ...
  const result = await saveDependency({...})
  // 检测到循环依赖：后端返回 code=200 + data.success=false（Promise resolve）
  if ((result as DependencyCycleResult).success === false) {
    cycleResult.value = result as DependencyCycleResult
    ElMessage.warning('检测到循环依赖，已拦截保存')
    return
  }
  ElMessage.success('保存依赖成功')
  cycleResult.value = null
  dialogVisible.value = false
  await loadDependencies()
  graphRef.value?.refresh()
}
</script>

<template>
  <el-alert
    v-if="cycleResult"
    type="error"
    :closable="true"
    show-icon
    :title="`检测到循环依赖：${cycleResult.errorMessage}`"
    @close="clearCycle"
  >
    <div class="cycle-path">闭环路径：{{ cyclePathText }}</div>
  </el-alert>

  <DependencyGraph
    ref="graphRef"
    :project-id="projectId"
    :highlight-cycle="highlightCycle"
    @select-task="onSelectTask"
  />
</template>
```

**校验**：
- 行 99-103 通过 `(result as DependencyCycleResult).success === false` 判断业务结果，与 API 注释约定一致 ✅
- `cyclePathText` 拼装「任务A → 任务B → ... → 任务A」，与后端 errorMessage 一致 ✅
- `el-alert type="error"` 红色告警 + 闭环路径文本展示，符合验收「指出闭环路径」要求 ✅
- `highlightCycle` 传给 `DependencyGraph` 组件做节点/边高亮（设计决策点 15 AntV G6 v5）✅
- 检测到循环依赖时不关闭 dialog、不刷新列表，依赖关系保持原状 ✅

#### (8) G6 图组件 — 闭环高亮

**文件**：`pms-frontend/src/components/DependencyGraph.vue` 行 12、18、73、93-96

```vue
/*
 * 通过 Props.highlightCycle 高亮闭环路径上的节点与边（cycle 状态）。
 */
interface Props {
  highlightCycle?: number[]
}

watch(() => props.highlightCycle, applyCycleHighlight)

// 重新渲染后清除高亮记忆，若 highlightCycle 仍存在则重新应用
if (props.highlightCycle?.length) {
  applyCycleHighlight(props.highlightCycle)
}
```

**校验**：`highlightCycle` prop 响应式 watch + 重新渲染后重新应用，闭环高亮逻辑完备 ✅

#### (9) 数据库 Schema

**文件**：`pms-admin/src/main/resources/db/migration/V68__create_task_dependency.sql` 行 10-28

```sql
CREATE TABLE pms_task_dependency (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    project_id          BIGINT       NOT NULL COMMENT '项目ID',
    predecessor_task_id BIGINT       NOT NULL COMMENT '前置任务ID',
    successor_task_id   BIGINT       NOT NULL COMMENT '后续任务ID',
    dependency_type     VARCHAR(4)   NOT NULL DEFAULT 'FS' COMMENT 'FS/FF/SS/SF',
    lag_days            INT          NOT NULL DEFAULT 0 COMMENT '滞后天数（可负）',
    -- ... 审计字段 ...
    version             INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pred_succ_type (predecessor_task_id, successor_task_id, dependency_type),
    KEY idx_successor_task_id (successor_task_id),
    KEY idx_predecessor_task_id (predecessor_task_id),
    KEY idx_project_id (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务依赖';
```

**校验**：
- `dependency_type` VARCHAR(4) 与实体 `@Size(max=4)` 一致 ✅
- 唯一键 `uk_pred_succ_type` 防止重复依赖 ✅
- 三个索引覆盖 DFS 邻接表构建（按 project_id 查询）与反向查询 ✅
- `version` 乐观锁字段与实体 `@Version` 一致 ✅

### 1.3 场景 4.1 验收结论

| 检查项 | 结果 |
|---|---|
| 后端 5 步校验链路（类型/自环/存在/闭环/保存）完整 | ✅ PASS |
| DFS 闭环检测算法正确，返回首尾闭合路径 | ✅ PASS |
| `CycleDetectedException` 携带 cyclePath（taskId + taskName） | ✅ PASS |
| `BaselineExceptionHandler` 转换为 HTTP 200 + success=false + errorCode=CYCLE_DETECTED | ✅ PASS |
| 响应结构与设计 §5.5 行 975-990 样例完全一致 | ✅ PASS |
| 前端 API 联合类型返回 + success 字段判断分支 | ✅ PASS |
| 前端 alert 展示闭环路径文本 + G6 图高亮闭环节点/边 | ✅ PASS |
| 数据库 schema 支持（V68 唯一键 + 索引） | ✅ PASS |
| 事务回滚保证检测失败无脏数据 | ✅ PASS |

**场景 4.1 验收结论：✅ PASS**

---

## 2. 验收场景 4.2 — 关键日期变更展示差异，记录原因

### 2.1 验收步骤

```
Given  项目 P1001 已有 APPROVED 基线 B7001（快照 5 个任务计划日期）
       任务 T8005(设备安装) 基线计划 2026-07-10 ~ 2026-07-20（工期 10 天）
       当前任务计划已改为 2026-07-15 ~ 2026-07-25（结束延后 5 天）
       项目配置 baseline.variance.days.threshold=5, percent.threshold=10
When   用户在「基线列表」点击 B7001 的「申请变更」，填写 changeReason="客户现场延期"
       调用 POST /api/baseline/{7001}/request-change?changeReason=...
Then   系统返回 BaselineDiffResult：
       - baseline.status = APPROVED（或保持 DRAFT 待审批，见技术债 TD-P8-008）
       - diffs[0] = { taskId:8005, endVariance:5, percentVariance:50.0, ... }
       - totalVarianced = 1
       - needsApproval = true（|endVariance|=5 > daysThreshold=5 不成立，
         但 percentVariance=50% > percentThreshold=10% 成立 → 双阈值 OR 触发）
       - approvalReason = "偏差超过阈值（5 天 / 10%）"
       前端「基线偏差分析」页展示对比表 + 红色行高亮延迟 + 黄色 alert 提示需审批
```

### 2.2 链路代码审查

#### (1) 后端 Controller

**文件**：`pms-baseline/.../controller/BaselineController.java` 行 55-68

```java
@Operation(summary = "申请基线变更（双阈值 OR 触发审批）")
@PostMapping("/{id}/request-change")
@PreAuthorize("hasAuthority('project:baseline:change')")
@OperLog(title = "计划基线-申请变更", businessType = 2)
public Result<BaselineDiffResult> requestChange(@PathVariable Long id,
                                                @RequestParam(required = false) String changeReason) {
    return Result.ok(baselineService.requestBaselineChange(id, changeReason));
}

@Operation(summary = "基线偏差分析（逐任务对比当前计划与基线快照）")
@GetMapping("/diff")
public Result<BaselineDiffResult> diff(@RequestParam Long baselineId) {
    return Result.ok(baselineService.compareWithBaseline(baselineId));
}
```

**校验**：
- API 路径 `POST /api/baseline/{id}/request-change` 与设计 §5.5 行 970 一致 ✅
- API 路径 `GET /api/baseline/diff` 与设计 §5.5 行 971 一致 ✅
- 权限码 `project:baseline:change`（申请变更）与设计 §5.5 一致 ✅
- `diff` 只读不鉴权，与设计 §5.5「偏差分析为只读」一致 ✅

#### (2) 后端 Service — saveBaseline（单一活跃基线规则）

**文件**：`pms-baseline/.../service/impl/BaselineServiceImpl.java` 行 47-96

```java
@Override
@Transactional(rollbackFor = Exception.class)
public BaselineSnapshot saveBaseline(Long projectId, String baselineName) {
    // 1. 查询项目下全部任务（@TableLogic 自动过滤已删除）
    List<ImplTask> tasks = implTaskMapper.selectList(
            new LambdaQueryWrapper<ImplTask>().eq(ImplTask::getProjectId, projectId));
    if (tasks.isEmpty()) {
        throw new BusinessException("项目下无任务，无法保存基线：projectId=" + projectId);
    }
    // 2. 组装任务计划快照列表
    List<TaskPlanSnapshot> snapshots = new ArrayList<>(tasks.size());
    for (ImplTask task : tasks) {
        snapshots.add(TaskPlanSnapshot.builder()
                .taskId(task.getId())
                .taskName(task.getTaskName())
                .plannedStart(toStr(task.getPlanStartDate()))
                .plannedEnd(toStr(task.getPlanEndDate()))
                .duration(durationBetween(task.getPlanStartDate(), task.getPlanEndDate()))
                .plannedHours(task.getPlannedHours())
                .taskType(task.getTaskType())
                .build());
    }
    // 3. 若项目已有 APPROVED 基线 → 置为 SUPERSEDED（单一活跃基线）
    List<BaselineSnapshot> approved = this.list(new LambdaQueryWrapper<BaselineSnapshot>()
            .eq(BaselineSnapshot::getProjectId, projectId)
            .eq(BaselineSnapshot::getStatus, "APPROVED"));
    for (BaselineSnapshot old : approved) {
        old.setStatus("SUPERSEDED");
        this.updateById(old);
    }
    // 4. 创建新基线（status=DRAFT）
    BaselineSnapshot baseline = BaselineSnapshot.builder()
            .projectId(projectId)
            .baselineName(...)
            .status("DRAFT")
            .snapshotJson(snapshots)
            .build();
    this.save(baseline);
    return baseline;
}
```

**校验**：
- 单一活跃基线规则：新建前将已有 APPROVED 置为 SUPERSEDED（设计决策点 6「单一活跃基线 + 历史基线归档」）✅
- 新建基线状态 DRAFT，符合状态机 DRAFT→APPROVED→SUPERSEDED ✅
- 快照字段完整：taskId/taskName/plannedStart/plannedEnd/duration/plannedHours/taskType ✅
- 日期转 ISO 字符串存储，规避 JacksonTypeHandler JavaTimeModule 问题（TaskPlanSnapshot 注释说明）✅

#### (3) 后端 Service — compareWithBaseline（逐任务偏差 + 双阈值）

**文件**：`BaselineServiceImpl.java` 行 106-191

```java
@Override
public BaselineDiffResult compareWithBaseline(Long baselineId) {
    BaselineSnapshot baseline = this.getById(baselineId);
    // 1. 加载基线快照 + 当前任务（按 taskId 索引）
    List<TaskPlanSnapshot> snapshots = baseline.getSnapshotJson();
    List<ImplTask> currentTasks = implTaskMapper.selectList(
            new LambdaQueryWrapper<ImplTask>().eq(ImplTask::getProjectId, baseline.getProjectId()));
    Map<Long, ImplTask> currentMap = new HashMap<>();
    for (ImplTask t : currentTasks) currentMap.put(t.getId(), t);

    // 2. 读取双阈值（项目级 > 系统默认，缺省 5 天 / 10%）
    int daysThreshold = readIntConfig(baseline.getProjectId(),
            "baseline.variance.days.threshold", 5);
    int percentThreshold = readIntConfig(baseline.getProjectId(),
            "baseline.variance.percent.threshold", 10);

    // 3. 逐任务计算偏差
    List<TaskDiff> diffs = new ArrayList<>();
    int totalVarianced = 0;
    boolean needsApproval = false;
    for (TaskPlanSnapshot snap : snapshots) {
        ImplTask current = currentMap.get(snap.getTaskId());
        Integer startVariance = daysBetween(snap.getPlannedStart(), currentStart);
        Integer endVariance = daysBetween(snap.getPlannedEnd(), currentEnd);
        long baselineDuration = daysBetweenLong(snap.getPlannedStart(), snap.getPlannedEnd());
        Double percentVariance = null;
        if (endVariance != null && baselineDuration > 0) {
            percentVariance = Math.round(
                    Math.abs(endVariance) * 10000.0 / baselineDuration) / 100.0;
        }
        // ... 构建 TaskDiff ...
        if ((startVariance != null && startVariance != 0)
                || (endVariance != null && endVariance != 0)) {
            totalVarianced++;
        }
        // 双阈值 OR：|结束偏差| > 天数阈值 OR 偏差百分比 > 百分比阈值
        if (endVariance != null) {
            long daysVar = Math.abs(endVariance);
            double percentVar = (baselineDuration > 0)
                    ? (double) daysVar / baselineDuration * 100 : 0;
            if (daysVar > daysThreshold || percentVar > percentThreshold) {
                needsApproval = true;
            }
        }
    }
    // ... 组装 BaselineDiffResult ...
}
```

**校验**：
- 偏差计算公式 `current - baseline`（正=延迟，负=提前），与 TaskDiff 注释一致 ✅
- 偏差百分比 = `|endVariance| / baselineDuration * 100`，与设计 §3.6 行 532 一致 ✅
- 双阈值 OR 逻辑 `daysVar > daysThreshold || percentVar > percentThreshold`，与设计 §3.6 行 534 一致 ✅
- 多层级配置 `readIntConfig`（项目级 > 系统默认 5/10），符合设计决策点 16「多层级可配置」✅
- `percentVariance` 四舍五入到 2 位小数（`Math.round(x * 10000.0) / 100.0`），与设计 §5.5 行 1014 `25.0` 样例一致 ✅
- `totalVarianced` 统计开始或结束偏差非 0 的任务数 ✅

#### (4) 后端 Service — requestBaselineChange（双阈值 OR + count 阈值 + 审批触发）

**文件**：`BaselineServiceImpl.java` 行 195-260

```java
@Override
@Transactional(rollbackFor = Exception.class)
public BaselineDiffResult requestBaselineChange(Long baselineId, String changeReason) {
    BaselineSnapshot baseline = this.getById(baselineId);
    if (!"DRAFT".equals(baseline.getStatus())) {
        throw new BusinessException("仅 DRAFT 状态基线可申请变更，当前状态：" + baseline.getStatus());
    }
    // 1. 偏差分析（天数/百分比双阈值，已计算 needsApproval）
    BaselineDiffResult result = compareWithBaseline(baselineId);
    // 2. count 阈值（偏差任务数）
    int countThreshold = readIntConfig(baseline.getProjectId(),
            "baseline.variance.threshold.count", 3);
    int variancedCount = result.getTotalVarianced() == null ? 0 : result.getTotalVarianced();
    boolean countExceeded = variancedCount > countThreshold;
    // 3. 双阈值 OR：days/percent OR count
    boolean daysOrPercentExceeded = Boolean.TRUE.equals(result.getNeedsApproval());
    boolean needsApproval = daysOrPercentExceeded || countExceeded;
    // 4. 合并审批原因
    String approvalReason = null;
    if (needsApproval) {
        StringBuilder sb = new StringBuilder();
        if (daysOrPercentExceeded) sb.append("偏差超过阈值");
        if (countExceeded) {
            if (sb.length() > 0) sb.append("；");
            sb.append("偏差任务数 ").append(variancedCount).append(" 超过阈值 ").append(countThreshold);
        }
        approvalReason = sb.toString();
    }
    result.setNeedsApproval(needsApproval);
    result.setApprovalReason(approvalReason);

    if (needsApproval) {
        // 触发 BASELINE_CHANGE 审批：Phase 7 实现具体审批流程与 ApprovalRecord 落库。
        // TODO(Phase 7): approvalRecordService.create("BASELINE_CHANGE", ...)
        baseline.setChangeReason(changeReason);
        this.updateById(baseline);
        log.warn("基线 {} 偏差超阈值，需触发 BASELINE_CHANGE 审批（Phase 7 实现）。reason={}",
                baselineId, changeReason);
    } else {
        // 未超阈值 → 直接 APPROVED
        baseline.setStatus("APPROVED");
        baseline.setApprovedAt(LocalDateTime.now());
        baseline.setChangeReason(changeReason);
        this.updateById(baseline);
    }
    return result;
}
```

**校验**：
- 三阈值 OR 逻辑（days/percent/count），比设计 §3.6 双阈值更严格（增加 count 阈值），属合理增强 ✅
- 审批原因合并文本「偏差超过阈值；偏差任务数 X 超过阈值 Y」，信息完整 ✅
- 未超阈值直接 APPROVED + 记录 changeReason，符合「记录原因」要求 ✅
- **技术债 TD-P8-008**：超阈值分支仅记日志 + 保存 changeReason，**未实际创建 ApprovalRecord / 触发 Flowable 审批流程**（行 238-241 TODO），基线保持 DRAFT 但无审批单可跟进。需 Phase 7 审批中心补全 `approvalRecordService.create("BASELINE_CHANGE", ...)` 并回填 `baseline.approvalRecordId`。

#### (5) DTO 结构

**文件**：
- `pms-baseline/.../dto/BaselineDiffResult.java` 行 23-58
- `pms-baseline/.../dto/TaskDiff.java` 行 21-52

```java
// BaselineDiffResult: { baseline(BaselineInfo), diffs(List<TaskDiff>),
//                       totalVarianced, needsApproval, approvalReason }
// TaskDiff: { taskId, taskName, baselineStart, currentStart, startVariance,
//             baselineEnd, currentEnd, endVariance, percentVariance }
// BaselineInfo: { id, baselineName, status, approvedAt }
```

**校验**：DTO 字段与设计 §5.5 行 994-1021 响应样例完全对齐 ✅

#### (6) 实体 + JSON TypeHandler

**文件**：`pms-baseline/.../entity/BaselineSnapshot.java` 行 34-55

```java
@TableName(value = "pms_baseline_snapshot", autoResultMap = true)
public class BaselineSnapshot extends BaseEntity {
    // ...
    @TableField(typeHandler = JsonTypeHandlers.TaskPlanSnapshotListHandler.class)
    private List<TaskPlanSnapshot> snapshotJson;
    // ...
}
```

**校验**：
- `autoResultMap = true` 开启，字段级 typeHandler 在 BaseMapper 方法生效（设计 §6.11 强制要求）✅
- `TaskPlanSnapshotListHandler` 自定义 TypeHandler 子类（Phase 8 Task 4 已校验合规）✅

#### (7) 前端 API 封装

**文件**：`pms-frontend/src/api/baseline.ts` 行 94-130

```typescript
export function listBaselines(projectId: number): Promise<BaselineSnapshot[]> {...}
export function saveBaseline(projectId: number, baselineName?: string): Promise<BaselineSnapshot> {...}
export function requestBaselineChange(
  baselineId: number, changeReason?: string
): Promise<BaselineDiffResult> {...}
export function diffBaseline(baselineId: number): Promise<BaselineDiffResult> {...}
```

**校验**：4 个 API 全部封装，路径与后端 Controller 一致 ✅

#### (8) 前端视图 — 偏差分析页

**文件**：`pms-frontend/src/views/baseline/diff.vue` 行 52-77、105-143

```vue
<script setup lang="ts">
async function loadDiff() {
  result.value = await diffBaseline(baselineId.value)
}
</script>

<template>
  <el-card>
    <template #header>
      <span>偏差对比明细</span>
      <span v-if="result" class="card-summary">
        偏差任务 {{ result.totalVarianced ?? 0 }} / {{ diffs.length }} 个
        <el-tag v-if="result.needsApproval" type="warning" size="small">需审批</el-tag>
        <el-tag v-else type="success" size="small">无需审批</el-tag>
      </span>
    </template>
    <BaselineDiffTable
      :diffs="diffs"
      :total-varianced="result.totalVarianced"
      :needs-approval="result.needsApproval"
      :approval-reason="result.approvalReason"
    />
  </el-card>
</template>
```

**校验**：页面展示偏差任务数、需审批标签、调用 BaselineDiffTable 渲染明细 ✅

#### (9) 前端组件 — 偏差对比表

**文件**：`pms-frontend/src/components/BaselineDiffTable.vue` 行 25-50、57-115

```vue
<script setup lang="ts">
/** 行样式：结束偏差非 0 时高亮 */
function rowClassName({ row }: { row: TaskDiff }): string {
  const v = row.endVariance
  if (v == null) return ''
  if (v > 0) return 'row-delayed'   // 延迟红色
  if (v < 0) return 'row-early'     // 提前绿色
  return ''
}
</script>

<template>
  <el-alert v-if="needsApproval" type="warning"
    :title="`偏差超阈值，需要审批：${approvalReason ?? ''}`" />
  <el-alert v-else type="success" title="偏差未超阈值，无需审批" />
  <el-table :data="diffs" :row-class-name="rowClassName" border>
    <!-- 任务名称/基线开始/当前开始/开始偏差/基线结束/当前结束/结束偏差/偏差百分比 -->
  </el-table>
</template>
```

**校验**：
- 8 列对比表（任务名 + 基线/当前开始/结束 + 开始/结束偏差 + 偏差百分比），字段完整 ✅
- 行高亮：延迟红色（`row-delayed`）、提前绿色（`row-early`），符合「展示差异」要求 ✅
- alert 展示需审批 + 审批原因，符合「记录原因」要求 ✅

#### (10) 数据库 Schema

**文件**：`pms-admin/src/main/resources/db/migration/V69__create_baseline_snapshot.sql` 行 11-29

```sql
CREATE TABLE pms_baseline_snapshot (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    project_id          BIGINT       NOT NULL,
    baseline_name       VARCHAR(128) NOT NULL,
    status              VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/APPROVED/SUPERSEDED',
    snapshot_json       JSON         NOT NULL COMMENT '快照JSON：任务计划列表',
    change_reason       VARCHAR(500) NULL COMMENT '变更原因（关联审批）',
    approval_record_id  BIGINT       NULL COMMENT '关联审批记录ID',
    approved_at         DATETIME     NULL,
    approved_by         BIGINT       NULL,
    -- ... 审计字段 ...
    version             INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_project_id_status (project_id, status, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计划基线快照';
```

**校验**：
- `snapshot_json` JSON 列存储 `List<TaskPlanSnapshot>` ✅
- `status` 三态 DRAFT/APPROVED/SUPERSEDED 与实体/服务一致 ✅
- `change_reason` VARCHAR(500) 与实体 `@Size(max=500)` 一致 ✅
- `approval_record_id` 预留审批关联字段（待 TD-P8-008 补全后回填）✅
- 索引 `idx_project_id_status` 支持单一活跃基线查询（按 project_id + status=APPROVED）✅

### 2.3 场景 4.2 验收结论

| 检查项 | 结果 |
|---|---|
| 后端 saveBaseline 单一活跃基线规则 + 快照全部任务 | ✅ PASS |
| 后端 compareWithBaseline 逐任务偏差 + 双阈值 OR | ✅ PASS |
| 后端 requestBaselineChange 三阈值 OR（days/percent/count）+ 审批原因合并 | ✅ PASS |
| 偏差计算公式（current - baseline）与设计 §3.6 一致 | ✅ PASS |
| 偏差百分比四舍五入 2 位小数，与设计 §5.5 样例一致 | ✅ PASS |
| 多层级阈值配置（项目级 > 系统默认 5/10/3） | ✅ PASS |
| BaselineSnapshot autoResultMap=true + TaskPlanSnapshotListHandler | ✅ PASS |
| DTO 字段与设计 §5.5 响应样例完全对齐 | ✅ PASS |
| 前端 4 个 API 封装 + 偏差分析页 + 对比表组件 | ✅ PASS |
| 前端行高亮（延迟红/提前绿）+ alert 展示审批原因 | ✅ PASS |
| 数据库 schema（V69 JSON 列 + 三态 status + 审批关联字段） | ✅ PASS |
| **BASELINE_CHANGE 审批流程实际触发**（ApprovalRecord 落库 + Flowable 启动） | ⚠️ **PARTIAL** — 见 TD-P8-008 |
| 超阈值分支基线状态流转（应进入审批中态，当前保持 DRAFT 无审批单） | ⚠️ **PARTIAL** — 依赖 TD-P8-008 |

**场景 4.2 验收结论：⚠️ PARTIAL PASS**

- 偏差分析 + 双阈值判定 + 差异展示 + 原因记录：**完整实现，PASS**
- BASELINE_CHANGE 审批流程实际触发：**未实现**（`requestBaselineChange` 行 238-241 TODO，仅记日志 + 保存 changeReason，未调用 `approvalRecordService.create`，未回填 `baseline.approvalRecordId`）。基线保持 DRAFT 但无审批单可跟进，需 Phase 7 审批中心补全（见技术债 TD-P8-008）。

---

## 3. 技术债清单

### TD-P8-008：BASELINE_CHANGE 审批流程未实际触发

| 项 | 内容 |
|---|---|
| 发现位置 | `BaselineServiceImpl.requestBaselineChange` 行 238-241 |
| 问题描述 | 超阈值分支仅 `log.warn` + `baseline.setChangeReason(changeReason)` + `updateById`，**未调用 `approvalRecordService.create("BASELINE_CHANGE", ...)`，未启动 Flowable 流程，未回填 `baseline.approvalRecordId`**。基线保持 DRAFT 状态但无审批单可跟进，用户无法在审批中心看到待办。 |
| 影响 | 验收 4.2「关键日期变更触发审批」部分 FAIL：偏差分析、原因记录、差异展示均正常，但「触发审批」这一关键动作缺失，超阈值基线无法走完审批闭环。 |
| 代码佐证 | `BaselineServiceImpl.java` 行 238-245：`// TODO(Phase 7): approvalRecordService.create("BASELINE_CHANGE", ...)` + `log.warn("基线 {} 偏差超阈值，需触发 BASELINE_CHANGE 审批（Phase 7 实现）。reason={}", baselineId, changeReason)` |
| 根因 | `pms-baseline` 模块未依赖 `pms-workflow`（审批中心），且 Phase 7 审批中心实现时未回填此处 TODO。模块依赖图：`pms-baseline → pms-implementation → pms-project`，无 `pms-baseline → pms-workflow` 边。 |
| 修复建议 | 方案 A（推荐）：通过 Spring Event 解耦 —— `pms-baseline` 发布 `BaselineChangeApprovalNeededEvent`，`pms-workflow` 监听并创建 ApprovalRecord + 启动 Flowable，避免引入模块硬依赖。方案 B：`pms-baseline` 直接依赖 `pms-workflow`（与 `pms-project → pms-workflow` 一致），调用 `approvalRecordService.create(...)` 后回填 `baseline.approvalRecordId`。 |
| 优先级 | 高 — 影响验收 4.2 完整闭环 |
| 关联 | 设计 §3.6 行 538「关键日期变更触发审批」、§3.5 行 498「基线变更 BASELINE_CHANGE 必审批」 |

### TD-P8-009：detectCycle 全量加载邻接表性能隐患

| 项 | 内容 |
|---|---|
| 发现位置 | `TaskDependencyServiceImpl.detectCycle` 行 123-129 |
| 问题描述 | 每次保存依赖都 `this.list(...)` 全量加载项目下所有 `pms_task_dependency` 记录构建邻接表，项目依赖关系多时（如数百条）每次保存都全表扫描 + 内存构建 Map，性能下降。 |
| 影响 | 大项目（依赖关系数百条）下保存依赖的延迟可能从毫秒级升到数十毫秒。当前项目规模下可接受，但属于已知性能债。 |
| 代码佐证 | `TaskDependencyServiceImpl.java` 行 123-124：`List<TaskDependency> deps = this.list(new LambdaQueryWrapper<TaskDependency>().eq(TaskDependency::getProjectId, projectId));` |
| 修复建议 | 方案 A：缓存邻接表（Caffeine，按 projectId 缓存 + 保存/删除时失效）。方案 B：增量检测 —— 仅查询从 successor 可达的子图（递归 SQL 或 BFS 逐层查询），避免全量加载。方案 C：递归 CTE 一次性查询 successor→predecessor 路径（MySQL 8 支持）。 |
| 优先级 | 低 — 当前项目规模可接受 |
| 关联 | 设计决策点 12「DFS 拓扑排序」 |

### TD-P8-010：设计文档响应结构描述与实现偏差

| 项 | 内容 |
|---|---|
| 发现位置 | 设计 §3.6 行 515 vs `BaselineExceptionHandler` 行 34 |
| 问题描述 | 设计文档伪代码 `Result.fail("CYCLE_DETECTED", "形成循环依赖，闭环路径: " + cyclePath, cyclePath)` 暗示返回失败码（非 200），但实际实现采用 `Result.ok(DependencyCycleResult{success=false})` 即 HTTP 200 + `data.success=false`。 |
| 影响 | 无功能影响 —— 实现方案更符合前端响应拦截器统一约定（响应拦截器按 `code` 判断 HTTP 成功，业务结果按 `data.success` 判断），且与设计 §5.5 行 975-990 响应样例（`code:200 + data.success:false`）一致。偏差仅在设计 §3.6 伪代码描述层面。 |
| 代码佐证 | `BaselineExceptionHandler.java` 行 34：`return Result.ok(result);` 注释「设计要求 code=200 + data.success=false，便于前端按业务结果分支处理」 |
| 修复建议 | 更新设计文档 §3.6 行 515 伪代码，与 §5.5 响应样例 + 实现保持一致（`Result.ok(DependencyCycleResult{success=false, ...})`）。属文档勘误，非代码缺陷。 |
| 优先级 | 低 — 文档勘误 |
| 关联 | 设计 §3.6 行 505-520、§5.5 行 973-990 |

---

## 4. 端到端验收总结

| 验收场景 | 关键 API | 后端实现 | 前端实现 | DB Schema | 结论 |
|---|---|---|---|---|---|
| 4.1 循环依赖被拒，指出闭环路径 | `POST /api/implementation/task/dependency` | ✅ 5 步校验 + DFS 闭环检测 + CycleDetectedException + 异常处理器 | ✅ 联合类型返回 + success 判断 + alert + G6 高亮 | ✅ V68 唯一键+索引 | **✅ PASS** |
| 4.2 关键日期变更展示差异，记录原因 | `POST /api/baseline/{id}/request-change` | ✅ 偏差分析+双阈值+三阈值 OR+原因合并；⚠️ 审批触发未实现 | ✅ 偏差分析页+对比表+行高亮+审批 alert | ✅ V69 JSON 列+三态+审批关联字段 | **⚠️ PARTIAL PASS**（TD-P8-008） |

### 4.1 整体结论

- **场景 4.1（循环依赖检测）**：完整实现，DFS 闭环检测算法正确，响应结构与设计样例完全一致，前端 alert + G6 高亮闭环路径，**验收通过**。
- **场景 4.2（基线偏差分析）**：偏差分析、双阈值判定、差异展示、原因记录均完整实现，**验收通过**；但「关键日期变更触发审批」这一关键动作未实现（TD-P8-008），超阈值基线无法走完审批闭环，**部分验收通过**，需 Phase 7 审批中心补全 `approvalRecordService.create("BASELINE_CHANGE", ...)` 并回填 `baseline.approvalRecordId`。

### 4.2 技术债统计

| 编号 | 标题 | 优先级 | 影响验收 |
|---|---|---|---|
| TD-P8-008 | BASELINE_CHANGE 审批流程未实际触发 | 高 | 4.2 部分FAIL |
| TD-P8-009 | detectCycle 全量加载邻接表性能隐患 | 低 | 无 |
| TD-P8-010 | 设计文档响应结构描述与实现偏差（文档勘误） | 低 | 无 |

### 4.3 与设计文档符合度

| 设计要求 | 实现状态 |
|---|---|
| §3.6 循环依赖检测（DFS + 闭环路径） | ✅ 完全符合 |
| §3.6 双阈值 OR（days/percent） | ✅ 完全符合（增强为三阈值 OR：days/percent/count） |
| §3.6 关键日期变更触发 BASELINE_CHANGE 审批 | ⚠️ 偏差分析完整，审批触发未实现（TD-P8-008） |
| §5.5 6 个 API 端点（依赖 2 + 基线 4） | ✅ 完全符合 |
| §5.5 验收 1 响应样例（code:200 + success:false + cyclePath） | ✅ 完全符合 |
| §5.5 验收 2 响应样例（baseline + diffs + needsApproval + approvalReason） | ✅ 完全符合 |
| §2.2 BaselineSnapshot 单一活跃基线 + 历史归档 | ✅ 完全符合（SUPERSEDED 流转） |
| §2.2 TaskDependency FS/FF/SS/SF + lag_days | ✅ 完全符合 |
| §6.11 BaselineSnapshot autoResultMap=true | ✅ 完全符合（Phase 8 Task 4 已校验） |
| 决策点 12 DFS 拓扑排序返回闭环路径 | ✅ 完全符合 |
| 决策点 15 AntV G6 v5 闭环高亮 | ✅ 完全符合（DependencyGraph highlightCycle） |
| 决策点 16 多层级阈值配置 | ✅ 完全符合（readIntConfig 项目级 > 系统默认） |

---

## 5. 关键文件清单

### 后端（pms-baseline 模块）

| 文件 | 行数 | 用途 |
|---|---|---|
| `controller/TaskDependencyController.java` | 66 | 依赖管理 API（保存/删除/查询） |
| `controller/BaselineController.java` | 68 | 基线管理 API（列表/保存/变更/偏差） |
| `service/TaskDependencyService.java` | 42 | 依赖服务接口 |
| `service/BaselineService.java` | 65 | 基线服务接口 |
| `service/impl/TaskDependencyServiceImpl.java` | 164 | DFS 闭环检测实现 |
| `service/impl/BaselineServiceImpl.java` | 316 | 基线快照 + 偏差分析 + 变更审批实现 |
| `entity/TaskDependency.java` | 53 | 依赖实体（FS/FF/SS/SF + lag） |
| `entity/BaselineSnapshot.java` | 73 | 基线快照实体（autoResultMap + JSON TypeHandler） |
| `dto/CycleNode.java` | 31 | 闭环路径节点 |
| `dto/DependencyCycleResult.java` | 39 | 循环依赖检测结果 |
| `dto/TaskDiff.java` | 52 | 单任务偏差 |
| `dto/BaselineDiffResult.java` | 58 | 基线偏差分析结果 |
| `exception/CycleDetectedException.java` | 46 | 循环依赖异常 |
| `advice/BaselineExceptionHandler.java` | 35 | 异常处理器（HTTP 200 + success=false） |

### 前端

| 文件 | 用途 |
|---|---|
| `src/api/task-dependency.ts` | 依赖 API（saveDependency 联合类型返回） |
| `src/api/baseline.ts` | 基线 API（4 个端点） |
| `src/views/task/dependency/index.vue` | 依赖关系图页（闭环 alert + G6 高亮） |
| `src/views/baseline/diff.vue` | 基线偏差分析页 |
| `src/components/DependencyGraph.vue` | G6 图组件（highlightCycle 高亮） |
| `src/components/BaselineDiffTable.vue` | 偏差对比表（行高亮 + 审批 alert） |

### 数据库迁移

| 文件 | 用途 |
|---|---|
| `V68__create_task_dependency.sql` | pms_task_dependency 表 + milestone 扩展 |
| `V69__create_baseline_snapshot.sql` | pms_baseline_snapshot 表 + history 关联 |

### 公共模块

| 文件 | 用途 |
|---|---|
| `pms-common/.../dto/TaskPlanSnapshot.java` | 任务计划快照 DTO（基线快照元素） |
| `pms-common/.../handler/JsonTypeHandlers.java` | TaskPlanSnapshotListHandler（Phase 8 Task 4 已校验） |
