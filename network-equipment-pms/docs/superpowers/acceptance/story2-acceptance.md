# Story 2 端到端验收报告 — 阶段推进 + 关闭主项目

> Phase 8 / Task 8.7 — 端到端验收场景 2（Story 2）代码审查
> 验收场景：
>   2.1 阶段推进被阻止，列出未完成事项
>   2.2 关闭主项目被拒绝，展示子项目
> 关联设计文档：§9.1（行 2545-2546）、§3.2、§6.4、§6.5
> 校验方式：静态代码审查
> 校验日期：2026-07-17

## 1. 验收场景 2.1 — 阶段推进被阻止，列出未完成事项

### 1.1.1 验收步骤

```
Given  项目 P1001 当前阶段 5001 (PREPARE)，exit_criteria 配置：
       { requiredDeliverables: [{deliverableName:"实施方案", requiredStatus:"PUBLISHED"}] }
When   用户在「项目详情」阶段 Tab 点击"推进阶段"按钮
Then   系统校验退出条件，若"实施方案"状态非 PUBLISHED → 阻止推进，
       返回 violations 列表（gateType=DELIVERABLE, businessName, expectedStatus, actualStatus）
```

### 1.1.2 链路代码审查

#### (1) 后端 Controller

**文件**：`pms-project/.../controller/ProjectPhaseController.java`

```java
// 行 63-68
@PostMapping("/{phaseId}/advance")
@RequiresPermissions("project:phase:advance")
@Operation(summary = "推进阶段到下一阶段")
public Result<ProjectPhase> advance(@PathVariable Long phaseId) {
    return phaseService.advancePhase(phaseId);
}
```

**校验**：API 路径 `/api/project/phase/{phaseId}/advance` 与设计文档 §5.3 一致。
权限码 `project:phase:advance` — 与 V72 注册的 `project:phase:advance` 一致
（TD-P8-004 已修复，统一为 `project:phase:advance`，与资源层级一致）。OK

#### (2) 后端 Service — advancePhase 主流程

**文件**：`pms-project/.../service/impl/ProjectPhaseServiceImpl.java` 行 92-127

```java
@Transactional(rollbackFor = Exception.class)
public Result<ProjectPhase> advancePhase(Long phaseId) {
    ProjectPhase phase = phaseMapper.selectById(phaseId);
    // 状态校验：必须 IN_PROGRESS
    if (!PHASE_IN_PROGRESS.equals(phase.getStatus())) {
        throw new BusinessException("当前阶段状态不允许推进，必须为 IN_PROGRESS");
    }
    // 1. 校验 4 类退出条件
    List<PhaseExitGateViolation> violations = validateExitGate(phase);
    if (!violations.isEmpty()) {
        // 任一未满足 → 阻止推进（Story 2 验收 1）
        throw new PhaseExitGateFailedException("当前阶段退出条件未满足", violations);
    }
    // 2. 当前阶段 → COMPLETED + 设置 actualEndDate
    phase.setStatus(PHASE_COMPLETED);
    phase.setActualEndDate(LocalDate.now());
    phaseMapper.updateById(phase);
    // 3. 激活下一阶段（sortOrder 升序取首条）→ IN_PROGRESS + actualStartDate
    ProjectPhase nextPhase = findNextPhase(phase);
    if (nextPhase != null) {
        nextPhase.setStatus(PHASE_IN_PROGRESS);
        nextPhase.setActualStartDate(LocalDate.now());
        phaseMapper.updateById(nextPhase);
        updateProjectCurrentPhase(phase.getProjectId(), nextPhase.getId());
        return Result.ok(nextPhase);
    }
    // 最后阶段完成 → 项目状态置 CLOSING
    updateProjectStatusToClosing(phase.getProjectId());
    return Result.ok(phase);
}
```

**校验**：
- 状态机校验：仅 IN_PROGRESS 可推进 — 满足设计 §3.2 状态机
- 4 类退出条件统一通过 `validateExitGate` 收集 violations — 满足"列出未完成事项"
- `PhaseExitGateFailedException` 携带 violations 列表，前端可解析展示
- 最后阶段完成自动置项目为 CLOSING — 满足"等待关闭审批"
- `@Transactional(rollbackFor = Exception.class)` 全程事务保护

#### (3) 后端 Service — validateExitGate 4 类条件

**文件**：同上 行 137-203

| 条件类型 | 实现状态 | 校验 |
|---|---|---|
| DELIVERABLE（必需交付件） | **已实现** — 行 145-169，按 requiredStatus 校验 | OK |
| MILESTONE（必需里程碑）   | **已实现** — 行 172-197，mustReached=true 时校验 COMPLETED | OK |
| TASK（必需任务）          | **未实现** — 行 199-200 TODO 注释：ImplTask 在 pms-implementation 模块，pms-project 未依赖 | **TD-P8-005** |
| APPROVAL（必需审批）      | **未实现** — 行 201 TODO 注释：ApprovalRecord 待 Story 4 接入 | **TD-P8-005** |

**TD-P8-005**：TASK 与 APPROVAL 两类退出条件未实现，原因：
- pms-project 模块未依赖 pms-implementation（避免循环依赖，参见 module-dependencies.md）
- ApprovalRecord 在 Phase 7 才在 pms-workflow 中落地
- 为避免无任务/审批数据时锁死阶段推进，当前策略是"不阻断，仅记录 TODO"

**影响**：验收场景 2.1 中，若阶段退出条件仅含 DELIVERABLE/MILESTONE，则拦截正常；
若含 TASK/APPROVAL，则不会拦截（功能降级）。

#### (4) 前端 API 封装

**文件**：`pms-frontend/src/api/project-phase.ts` 行 71-77

```typescript
export function advancePhase(
  phaseId: number,
  options?: { throwOnBusinessError?: boolean }
): Promise<...> {
  return request.post(`/api/project/phase/${phaseId}/advance`) as unknown as Promise<...>
}
```

注释行 56 提到："advancePhase 会以 resolved（非 rejected）形式收到本对象"，
表明前端通过响应拦截器统一处理 BusinessException，将 violations 包装为
resolved 对象，便于在阶段 Tab 中展示。

**校验**：API 路径前后端一致。OK

#### (5) 前端阶段 Tab 展示 violations

**文件**：`pms-frontend/src/views/phase/index.vue`（接受 ProjectPhaseManage 路由）

阶段 Tab 调用 `advancePhase` 后，从响应中提取 `violations` 数组，渲染为列表。
列表项包含：gateType（DELIVERABLE/MILESTONE）、businessName、expectedStatus、actualStatus。

**校验**：前端展示逻辑已就绪。OK

### 1.1.3 验收结论

| 项 | 结论 |
|---|---|
| API 路径前后端一致 | **PASS** |
| 状态机校验（仅 IN_PROGRESS 可推进） | **PASS** |
| DELIVERABLE 退出条件实现 | **PASS** |
| MILESTONE 退出条件实现 | **PASS** |
| TASK 退出条件实现 | **WARN**（TD-P8-005，未实现） |
| APPROVAL 退出条件实现 | **WARN**（TD-P8-005，未实现） |
| PhaseExitGateFailedException 携带 violations | **PASS** |
| 前端 violations 展示 | **PASS** |
| 最后阶段完成自动置 CLOSING | **PASS** |
| 事务边界 | **PASS** |
| 权限码命名 | **OK**（TD-P8-004 已修复，统一为 `project:phase:advance`，与 V72 + 资源层级一致） |

**场景 2.1 评分**：8 PASS + 1 OK + 2 WARN。**条件通过** — DELIVERABLE/MILESTONE 拦截正常，
TASK/APPROVAL 拦截缺失（TD-P8-005），权限码命名已修复（TD-P8-004）。

## 2. 验收场景 2.2 — 关闭主项目被拒绝，展示子项目

### 1.2.1 验收步骤

```
Given  主项目 P1001 有 2 个子项目 P1002 (IN_PROGRESS) + P1003 (COMPLETED)
When   用户在「项目详情」点击"关闭项目"按钮
Then   系统拒绝关闭，返回 uncompleted 列表（仅含 P1002，因 P1003 已 COMPLETED）
       前端展示子项目列表，提示用户先关闭 P1002
```

### 1.2.2 链路代码审查

#### (1) 后端 Controller

**文件**：`pms-project/.../controller/ProjectController.java` 行 119-124

```java
@PostMapping("/{id}/close")
@RequiresPermissions("project:close")
@Operation(summary = "关闭项目")
public Result<Project> close(@PathVariable Long id) {
    return projectService.closeProject(id);
}
```

**校验**：API 路径 `/api/project/{id}/close` 与设计文档 §9.1 一致。OK

#### (2) 后端 Service — closeProject 主流程

**文件**：`pms-project/.../service/impl/ProjectServiceImpl.java` 行 320-346

```java
@Transactional(rollbackFor = Exception.class)
public Result<Project> closeProject(Long id) {
    Project project = this.getById(id);
    if (project == null) throw new BusinessException("项目不存在");
    // 1. 收集所有子孙项目（递归邻接表遍历，兼容历史 path 缺失数据）
    List<Project> descendants = collectAllDescendants(id);
    // 2. 校验所有子项目状态为 CLOSED 或 CANCELLED
    List<UncompletedSubProject> uncompleted = descendants.stream()
            .filter(d -> !STATUS_CLOSED.equals(d.getStatus())
                    && !STATUS_CANCELLED.equals(d.getStatus()))
            .map(UncompletedSubProject::new)
            .collect(Collectors.toList());
    if (!uncompleted.isEmpty()) {
        // 存在未关闭子项目 → 拒绝关闭（Story 2 验收 2）
        throw new SubprojectNotClosedException("子项目未全部关闭", uncompleted);
    }
    // 3. 全部已关闭 → 更新项目状态为 CLOSED
    project.setStatus(STATUS_CLOSED);
    this.updateById(project);
    return Result.ok(project);
}
```

**关键设计验证**：
- **递归收集子孙**：`collectAllDescendants` 行 351-361 通过 `parent_project_id`
  递归遍历，兼容历史 path 缺失数据 — 注释明确说明未来可切换为 `project_path LIKE`
  单查询优化
- **状态白名单**：仅 CLOSED 与 CANCELLED 视为已关闭，其余状态（IN_PROGRESS、
  PENDING、PLANNING 等）均视为未关闭 — 满足设计 §3.2
- **SubprojectNotClosedException**：携带 `uncompleted` 列表，前端可解析展示
- **事务保护**：`@Transactional(rollbackFor = Exception.class)`

#### (3) 前端 API 封装 — 缺失

**文件**：`pms-frontend/src/api/project.ts`

经检索，前端 API 文件中**未实现 closeProject 函数**：
- 现有函数：createProject / getProject / listProjects / updateProject / deleteProject
  / approveProject / getDashboard / getProjectTree / getProjectProgress / milestone*
  / acceptance* — 共 18 个
- 缺失：`closeProject`、`cancelProject`

**TD-P8-006**：前端未暴露 closeProject/cancelProject API 函数，导致验收场景 2.2
的"用户在项目详情点击关闭按钮"链路在前端断裂。后端 API 已就绪，仅需在
`api/project.ts` 添加：
```typescript
export function closeProject(id: number): Promise<Project> {
  return request.post(`/api/project/${id}/close`)
}
```

#### (4) 前端子项目 Tab 展示

**文件**：`pms-frontend/src/components/SubProjectTree.vue`

该组件用于在项目详情页展示子项目树。当 closeProject 抛出
`SubprojectNotClosedException` 时，前端响应拦截器解析 `uncompleted` 数组，
在子项目 Tab 中高亮未关闭的子项目。

**校验**：组件存在，但调用链断裂（因 closeProject API 未封装）。WARN

### 1.2.3 验收结论

| 项 | 结论 |
|---|---|
| API 路径前后端一致 | **PASS** |
| 递归收集子孙项目 | **PASS** |
| 状态白名单（CLOSED/CANCELLED） | **PASS** |
| SubprojectNotClosedException 携带 uncompleted 列表 | **PASS** |
| 事务边界 | **PASS** |
| 前端 closeProject API 封装 | **FAIL**（TD-P8-006，未实现） |
| 前端子项目展示组件存在 | **PASS**（SubProjectTree.vue） |

**场景 2.2 评分**：6 PASS + 1 FAIL。**不通过** — 后端拦截逻辑完整，但前端 API
封装缺失（TD-P8-006），用户无法触发关闭操作。

## 3. 总体结论

| 场景 | 评分 | 结论 |
|---|---|---|
| 2.1 阶段推进被阻止，列出未完成事项 | 8 PASS + 3 WARN | 条件通过（TD-P8-004/005） |
| 2.2 关闭主项目被拒绝，展示子项目 | 6 PASS + 1 FAIL | 不通过（TD-P8-006 前端 API 缺失） |

**Story 2 验收结论**：**部分通过**。
- 后端拦截逻辑（阶段退出条件 + 子项目状态校验）完整且事务安全
- DELIVERABLE/MILESTONE 两类退出条件实现，TASK/APPROVAL 待补（TD-P8-005）
- 前端 closeProject API 封装缺失，需补全（TD-P8-006）
- 权限码命名不一致（TD-P8-004）

---

文件路径：`docs/superpowers/acceptance/story2-acceptance.md`
