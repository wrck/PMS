# pms-baseline 模块知识库

> 本文基于 `network-equipment-pms/pms-baseline` 模块源码（`com.dp.plat.baseline`）整理，记录依赖与基线领域的实体模型、任务依赖 DFS 循环检测、计划基线快照与版本管理、三阈值偏差监控、双 SPI 跨模块协作等核心机制。

## 模块概述

`pms-baseline` 是网络设备 PMS 平台的**依赖与基线领域模块**，承担两项核心职责：

1. **任务依赖管理** — 维护任务间 FS / FF / SS / SF 四种依赖关系，保存时执行 DFS 闭环检测，构造循环路径节点列表并通过 `CycleDetectedException` 结构化返回。
2. **计划基线管理** — 对项目下全部任务计划字段做整体快照存储，提供基线列表、偏差分析、变更审批触发能力，支持"单一活跃基线"规则与三阈值（天数 / 百分比 / 任务数）OR 触发审批。

- **Maven 坐标**：`com.dp.plat:pms-baseline:1.0.0-SNAPSHOT`，父工程为 `com.dp.plat:network-equipment-pms`。
- **artifactId / name**：`pms-baseline`，`<description>` 为 `依赖与基线模块 — 任务依赖循环检测 + 计划基线快照与偏差分析`。
- **基础包名**：`com.dp.plat.baseline`。
- **核心定位**：实施域的"计划守门人" —— 接收 `pms-implementation` 的 `ImplTask` 数据做快照、读取 `pms-project` 的 `ProjectConfigService` 取偏差阈值、通过 `ApprovalTrigger` SPI 触发 `pms-workflow` 的 `BASELINE_CHANGE` 审批、向 `pms-project` 暴露 `DependencyBatchCreator` SPI 支持模板深拷贝。

## 包结构

```
com.dp.plat.baseline
├── advice/                         # 模块级异常处理（BaselineExceptionHandler）
├── controller/                     # 2 个 REST 控制器
│   ├── BaselineController.java
│   └── TaskDependencyController.java
├── dto/                            # 4 个数据传输对象
│   ├── BaselineDiffResult.java
│   ├── CycleNode.java
│   ├── DependencyCycleResult.java
│   └── TaskDiff.java
├── entity/                         # 2 个实体（@TableName 持久化）
│   ├── BaselineSnapshot.java
│   └── TaskDependency.java
├── exception/                      # 业务异常
│   └── CycleDetectedException.java
├── mapper/                         # 2 个 MyBatis-Plus Mapper
│   ├── BaselineSnapshotMapper.java
│   └── TaskDependencyMapper.java
├── service/                        # 2 个 Service 接口
│   ├── BaselineService.java
│   ├── TaskDependencyService.java
│   └── impl/                       # 2 个 Service 实现
│       ├── BaselineServiceImpl.java
│       └── TaskDependencyServiceImpl.java
└── spi/                            # 1 个跨模块 SPI 实现
    └── DependencyBatchCreatorImpl.java
```

各包职责说明：

| 包 | 主要类型 | 职责 |
|----|----------|------|
| `advice` | `BaselineExceptionHandler` | 模块级 `@RestControllerAdvice`（`@Order(HIGHEST_PRECEDENCE)`），将 `CycleDetectedException` 转换为 HTTP 200 + 结构化失败响应 |
| `controller` | 2 个 `@RestController` | 暴露基线管理（`/api/baseline`）与任务依赖管理（`/api/implementation/task/dependency`）REST API |
| `dto` | 4 个 DTO | 偏差分析结果、循环路径节点、循环依赖响应、单任务偏差记录 |
| `entity` | 2 个 `@TableName` 实体 | 持久化模型（均含 `@Version` 乐观锁字段） |
| `exception` | `CycleDetectedException` | 循环依赖异常，携带 `cyclePath`（首尾闭合节点列表） |
| `mapper` | 2 个 `BaseMapper` 子接口 | MyBatis-Plus 标准 CRUD，无自定义 SQL |
| `service` / `service.impl` | 2 接口 + 2 实现 | 业务逻辑层（基线快照/偏差/审批触发、依赖循环检测） |
| `spi` | `DependencyBatchCreatorImpl` | 向 `pms-project` 暴露的模板深拷贝依赖批量创建扩展点 |

## 核心实体模型

模块共 2 个持久化实体，全部继承 `com.dp.plat.common.entity.BaseEntity`（公共字段：`id`、`createTime`、`updateTime`、`createBy`、`updateBy`、`deleted`（`@TableLogic` 逻辑删除））。

### 实体清单

| 实体 | 表名 | 中文含义 | 乐观锁 | 关键关系 |
|------|------|----------|--------|----------|
| `BaselineSnapshot` | `pms_baseline_snapshot` | 计划基线快照 | `@Version` | N:1 → `Project`（projectId）；JSON 列 `snapshotJson` 存储 `List<TaskPlanSnapshot>`；可选关联 `approvalRecordId` → `ApprovalRecord` |
| `TaskDependency` | `pms_task_dependency` | 任务依赖 | `@Version` | N:1 → `Project`（projectId）；`predecessorTaskId` / `successorTaskId` → `pms_impl_task.id` |

### BaselineSnapshot 字段详解

`BaselineSnapshot` 是计划基线核心实体，对应表 `pms_baseline_snapshot`，`@TableName(value = "pms_baseline_snapshot", autoResultMap = true)` —— **`autoResultMap = true` 必须开启**，否则字段级 `typeHandler` 在 `BaseMapper` 方法中不生效。

| 字段 | 类型 | 校验 | 说明 |
|------|------|------|------|
| `projectId` | `Long` | `@NotNull("项目ID不能为空")` | 所属项目ID |
| `baselineName` | `String` | `@NotBlank` `@Size(max=128)` | 基线名称（为空时按 `基线_yyyyMMdd-HHmm` 生成） |
| `status` | `String` | `@Builder.Default("DRAFT")` | 基线状态：`DRAFT` / `APPROVED` / `SUPERSEDED` |
| `snapshotJson` | `List<TaskPlanSnapshot>` | — | 全部任务计划快照（JSON 列，`typeHandler = JsonTypeHandlers.TaskPlanSnapshotListHandler.class`） |
| `changeReason` | `String` | `@Size(max=500)` | 变更原因（关联审批） |
| `approvalRecordId` | `Long` | — | 关联审批记录ID（触发 `BASELINE_CHANGE` 后回填） |
| `approvedAt` | `LocalDateTime` | — | 审批时间（基线转为 `APPROVED` 时填充） |
| `approvedBy` | `Long` | — | 审批人ID |
| `version` | `Integer` | `@Version` | 乐观锁版本号（MyBatis-Plus） |

**单一活跃基线规则**：项目同时只能有一条 `APPROVED` 状态基线。新建基线时，先将项目下所有 `APPROVED` 基线置为 `SUPERSEDED`，再创建新的 `DRAFT` 基线（实现见 `BaselineServiceImpl.saveBaseline`）。

### TaskDependency 字段详解

`TaskDependency` 对应表 `pms_task_dependency`，`@TableName("pms_task_dependency")`，记录任务间依赖关系。

| 字段 | 类型 | 校验 | 说明 |
|------|------|------|------|
| `projectId` | `Long` | `@NotNull("项目ID不能为空")` | 所属项目ID（限定依赖图范围） |
| `predecessorTaskId` | `Long` | `@NotNull("前置任务ID不能为空")` | 前置任务ID（→ `pms_impl_task.id`） |
| `successorTaskId` | `Long` | `@NotNull("后续任务ID不能为空")` | 后续任务ID（→ `pms_impl_task.id`） |
| `dependencyType` | `String` | `@NotBlank` `@Size(max=4)` `@Builder.Default("FS")` | 依赖类型：`FS` / `FF` / `SS` / `SF` |
| `lagDays` | `Integer` | `@Builder.Default(0)` | 滞后天数（可负，表示提前） |
| `version` | `Integer` | `@Version` | 乐观锁版本号（MyBatis-Plus） |

## 基线类型体系

`pms-baseline` 模块当前聚焦于**进度（计划）基线**（Schedule Baseline）这一单一基线维度，通过 `BaselineSnapshot.snapshotJson` 存储全部任务的 `TaskPlanSnapshot` 完整计划字段快照。每个任务计划快照（`com.dp.plat.common.dto.TaskPlanSnapshot`）字段如下：

| 字段 | 类型 | 说明 |
|------|------|------|
| `taskId` | `Long` | 任务ID |
| `taskName` | `String` | 任务名称（冗余，便于历史追溯） |
| `plannedStart` | `String` | 计划开始日期（ISO `yyyy-MM-dd`） |
| `plannedEnd` | `String` | 计划结束日期（ISO `yyyy-MM-dd`） |
| `duration` | `Integer` | 计划工期（天） |
| `plannedHours` | `Integer` | 计划工时（小时） |
| `taskType` | `String` | 任务类型（`OEM` / `AGENT`，冗余） |

> 设计取舍：日期字段使用 `String`（ISO 格式 `yyyy-MM-dd`）而非 `LocalDate`，规避 MyBatis-Plus JSON `TypeHandler` 默认 `ObjectMapper` 缺少 `JavaTimeModule` 的序列化问题，同时与 API 响应中的日期字符串格式保持一致。

**依赖类型四元组**（`TaskDependency.dependencyType`）：

| 代码 | 全称 | 中文 | 含义 |
|------|------|------|------|
| `FS` | Finish-to-Start | 完成-开始 | 前置完成后，后续才能开始（PDM 默认类型） |
| `FF` | Finish-to-Finish | 完成-完成 | 前置完成后，后续才能完成 |
| `SS` | Start-to-Start | 开始-开始 | 前置开始后，后续才能开始 |
| `SF` | Start-to-Finish | 开始-完成 | 前置开始后，后续才能完成（罕见） |

合法类型集合在 `TaskDependencyServiceImpl` 中以 `Set<String> VALID_TYPES = Set.of("FS", "FF", "SS", "SF")` 形式声明，保存前校验。

## 基线快照与版本管理

### 单一活跃基线规则

模块遵循"单一活跃基线"原则（设计文档 §3.6）：同一项目同时只能有一条 `APPROVED` 状态基线。基线状态机如下：

```
       saveBaseline                requestBaselineChange（超阈值）
DRAFT ──────────────►  DRAFT  ───────────────────────────►  DRAFT + approvalRecordId
                        │                                       │
                        │ requestBaselineChange（未超阈值）       │ 审批通过（Phase 7）
                        ▼                                       ▼
                     APPROVED                                APPROVED
                        │
                        │ 新基线 saveBaseline
                        ▼
                     SUPERSEDED
```

### saveBaseline 流程（保存新基线）

`BaselineServiceImpl.saveBaseline(projectId, baselineName)`（`@Transactional(rollbackFor = Exception.class)`）：

1. 校验 `projectId` 非空；查询项目下全部任务（`implTaskMapper.selectList`，`@TableLogic` 自动过滤已删除）。
2. 项目无任务时抛 `BusinessException("项目下无任务，无法保存基线：projectId=...")`。
3. 组装 `List<TaskPlanSnapshot>` —— 逐任务深拷贝 `taskId / taskName / plannedStart / plannedEnd / duration / plannedHours / taskType`，`plannedStart/End` 由 `LocalDate.toString()` 转 ISO 字符串，`duration` 由 `ChronoUnit.DAYS.between(planStartDate, planEndDate)` 计算。
4. 查询项目下所有 `APPROVED` 基线，逐条置 `status = "SUPERSEDED"` 并 `updateById`，`log.info` 记录被取代的基线ID。
5. 构造新基线：`status = "DRAFT"`，`baselineName` 为空时按 `基线_yyyyMMdd-HHmm`（`DateTimeFormatter.ofPattern("yyyyMMdd-HHmm")`）生成，`snapshotJson = snapshots`，`save(baseline)`。

### listByProject 查询

按 `projectId` 过滤、按 `createTime` 倒序返回项目下全部基线（含 `SUPERSEDED` 历史基线），用于前端展示基线版本演进。

## 偏差监控机制

### 三阈值配置

偏差监控通过 `ProjectConfigService.get(projectId, null, key)` 读取三个配置键（`templateId` 传 `null`，按项目级 > 系统默认两级解析；`ProjectConfigService` 内部支持项目级 > 模板级 > 系统默认三级，本项目基线场景下不传模板）：

| 配置键 | 默认值 | 含义 | 使用方法 |
|--------|--------|------|----------|
| `baseline.variance.days.threshold` | `5` | 偏差天数阈值 | `|endVariance| > 5` 触发审批 |
| `baseline.variance.percent.threshold` | `10` | 偏差百分比阈值 | `偏差百分比 > 10` 触发审批 |
| `baseline.variance.threshold.count` | `3` | 偏差任务数阈值 | `偏差任务数 > 3` 触发审批 |

读取实现（`BaselineServiceImpl.readIntConfig`）：异常或空值时返回 `defaultValue`，保证阈值缺失时退化到保守默认。

### compareWithBaseline 偏差分析

`BaselineServiceImpl.compareWithBaseline(baselineId)`：

1. 加载基线快照 + 当前任务（按 `taskId` 索引到 `Map<Long, ImplTask> currentMap`）。
2. 读取双阈值（`daysThreshold` / `percentThreshold`）。
3. 逐任务计算偏差（`TaskDiff`）：
   - `startVariance` = `daysBetween(plannedStart, currentStart)`（`current - baseline`，正值延后、负值提前）
   - `endVariance` = `daysBetween(plannedEnd, currentEnd)`
   - `baselineDuration` = `daysBetweenLong(plannedStart, plannedEnd)`（工期，端点为 null 返回 0）
   - `percentVariance` = `Math.round(|endVariance| * 10000.0 / baselineDuration) / 100.0`（保留两位小数；`baselineDuration <= 0` 时为 `null`）
4. 累计 `totalVarianced`（开始或结束偏差非 0 的任务数）。
5. 双阈值 OR 判定 `needsApproval`：`|endVariance| > daysThreshold OR 偏差百分比 > percentThreshold`。
6. 构造 `BaselineDiffResult`：含基线摘要（`BaselineInfo`：id / baselineName / status / approvedAt）、`diffs` 列表、`totalVarianced`、`needsApproval`、`approvalReason`（超阈值时为 `"偏差超过阈值（X 天 / Y%）"`）。

> 日期解析异常容错：`daysBetween` 在 `LocalDate.parse` 抛异常时返回 `null`（标记为不可比较）；`daysBetweenLong` 返回 `0L`。

### requestBaselineChange 变更申请

`BaselineServiceImpl.requestBaselineChange(baselineId, changeReason)`（`@Transactional(rollbackFor = Exception.class)`）：

1. 校验基线存在，且当前状态必须为 `DRAFT`（否则抛 `BusinessException("仅 DRAFT 状态基线可申请变更...")`）。
2. 调用 `compareWithBaseline` 计算天数/百分比双阈值偏差。
3. 读取 `count` 阈值，计算 `countExceeded = variancedCount > countThreshold`。
4. **三阈值 OR 触发逻辑**：`needsApproval = daysOrPercentExceeded || countExceeded`。
5. 合并 `approvalReason`（天数/百分比超阈值拼 `"偏差超过阈值"`，count 超阈值拼 `"偏差任务数 N 超过阈值 M"`，二者同时命中以分号 `；` 连接）。
6. 分支处理：
   - **超阈值** → 调用 `triggerBaselineChangeApproval` 触发 `BASELINE_CHANGE` 审批流程，回填 `approvalRecordId` 与 `changeReason`，基线保持 `DRAFT`；SPI 未加载或失败时 `log.warn` 跳过审批但保留 `changeReason`（不阻断主流程）。
   - **未超阈值** → 直接 `status = "APPROVED"`、`approvedAt = LocalDateTime.now()`、保存 `changeReason`，并同步更新返回结果中的 `BaselineInfo.status / approvedAt`。

### BASELINE_CHANGE 审批触发（TD-P8-008）

`triggerBaselineChangeApproval(baseline, approvalReason)` 通过 `ApprovalTrigger` SPI 跨模块调用 `pms-workflow` 的审批中心：

| 参数 | 取值 |
|------|------|
| `approvalType` | `"BASELINE_CHANGE"`（常量 `APPROVAL_TYPE_BASELINE_CHANGE`，参见设计文档 §6.9 审批类型表） |
| `businessId` | `baseline.getId()`（基线ID） |
| `projectId` | `baseline.getProjectId()` |
| `title` | `"基线变更审批：" + baselineName` |
| `reason` | `joinReason(changeReason, approvalReason)` —— 偏差说明 + `"变更说明：" + changeReason`，二者以 `；` 连接 |

`ApprovalTrigger` 通过 `@Autowired(required = false)` 注入，`pms-workflow` 模块未加载时为 `null`，调用前判空并 `log.warn` 跳过 —— 这是模块间解耦的关键设计（与 TD-P8-001 一致）。

## Service 层与 API 端点

### BaselineService 接口

`BaselineService extends IService<BaselineSnapshot>`（MyBatis-Plus 标准 CRUD）：

| 方法 | 签名 | 说明 |
|------|------|------|
| `saveBaseline` | `BaselineSnapshot saveBaseline(Long projectId, String baselineName)` | 保存新基线，快照项目全部任务；前一条 APPROVED 置 SUPERSEDED；新基线为 DRAFT |
| `listByProject` | `List<BaselineSnapshot> listByProject(Long projectId)` | 查询项目下全部基线（按 createTime 倒序） |
| `compareWithBaseline` | `BaselineDiffResult compareWithBaseline(Long baselineId)` | 基线偏差分析（天数/百分比双阈值） |
| `requestBaselineChange` | `BaselineDiffResult requestBaselineChange(Long baselineId, String changeReason)` | 申请基线变更（三阈值 OR 触发审批） |

### TaskDependencyService 接口

`TaskDependencyService extends IService<TaskDependency>`：

| 方法 | 签名 | 说明 |
|------|------|------|
| `saveDependency` | `TaskDependency saveDependency(TaskDependency dependency)` | 保存依赖（含 DFS 循环检测，检测到闭环抛 `CycleDetectedException`） |
| `deleteDependency` | `void deleteDependency(Long id)` | 删除依赖（逻辑删除，不存在时抛 `BusinessException`） |
| `listByProject` | `List<TaskDependency> listByProject(Long projectId)` | 查询项目下全部依赖（按 predecessorTaskId、successorTaskId 升序） |

### REST API 端点

#### BaselineController（`/api/baseline`）

| HTTP | 路径 | 方法 | 权限码 | OperLog | 说明 |
|------|------|------|--------|---------|------|
| `GET` | `/api/baseline/list` | `list` | — | — | 查询项目基线列表（`?projectId=`） |
| `POST` | `/api/baseline/save` | `save` | `project:baseline:save` | `计划基线` businessType=1 | 保存基线（`?projectId=&baselineName=`） |
| `POST` | `/api/baseline/{id}/request-change` | `requestChange` | `project:baseline:change` | `计划基线-申请变更` businessType=2 | 申请基线变更（`?changeReason=`） |
| `GET` | `/api/baseline/diff` | `diff` | — | — | 基线偏差分析（`?baselineId=`） |

#### TaskDependencyController（`/api/implementation/task/dependency`）

> 路径前缀复用 `/api/implementation/`，反映"任务依赖是实施任务的一部分"。

| HTTP | 路径 | 方法 | 权限码 | OperLog | 说明 |
|------|------|------|--------|---------|------|
| `GET` | `/api/implementation/task/dependency` | `list` | — | — | 查询项目下全部依赖（`?projectId=`） |
| `POST` | `/api/implementation/task/dependency` | `save` | `project:baseline:save` | `任务依赖` businessType=1 | 保存依赖（`@Valid @RequestBody TaskDependency`），含 DFS 循环检测 |
| `DELETE` | `/api/implementation/task/dependency/{id}` | `delete` | `project:baseline:save` | `任务依赖` businessType=3 | 删除依赖（逻辑删除） |

> 权限注解采用 Spring Security `@PreAuthorize`（与 `pms-implementation` 模块一致）。设计文档原文标注 Shiro `@RequiresPermissions`，但本项目未引入 Shiro 依赖，统一替换为 `@PreAuthorize`，权限码保持不变。`OperLog` 注解（`com.dp.plat.common.annotation.OperLog`）记录操作日志。

### 异常处理与响应结构

`BaselineExceptionHandler`（`@Order(Ordered.HIGHEST_PRECEDENCE)`、`@RestControllerAdvice`）以最高优先级拦截 `CycleDetectedException`，转换为 HTTP 200 + 结构化失败响应（设计要求 `code=200 + data.success=false`，便于前端按业务结果分支处理）：

```java
@ExceptionHandler(CycleDetectedException.class)
public Result<DependencyCycleResult> handleCycleDetected(CycleDetectedException e) {
    DependencyCycleResult result = DependencyCycleResult.builder()
            .success(false)
            .errorCode(CycleDetectedException.ERROR_CODE)   // "CYCLE_DETECTED"
            .errorMessage(e.getMessage())                    // "形成循环依赖，闭环路径: 任务A → 任务B → 任务C → 任务A"
            .cyclePath(e.getCyclePath())                     // List<CycleNode>
            .build();
    return Result.ok(result);
}
```

### DTO 结构

**`TaskDiff`** —— 单任务偏差（`Serializable`）：

| 字段 | 类型 | 说明 |
|------|------|------|
| `taskId` | `Long` | 任务ID |
| `taskName` | `String` | 任务名称 |
| `baselineStart` / `currentStart` | `String` | 基线/当前开始日期（ISO `yyyy-MM-dd`） |
| `startVariance` | `Integer` | 开始偏差天数（`current - baseline`，正值延后） |
| `baselineEnd` / `currentEnd` | `String` | 基线/当前结束日期 |
| `endVariance` | `Integer` | 结束偏差天数 |
| `percentVariance` | `Double` | 偏差百分比（`|endVariance| / baselineDuration * 100`） |

**`BaselineDiffResult`** —— 偏差分析结果（含内部静态类 `BaselineInfo`：`id / baselineName / status / approvedAt`）：

| 字段 | 类型 | 说明 |
|------|------|------|
| `baseline` | `BaselineInfo` | 基线摘要 |
| `diffs` | `List<TaskDiff>` | 逐任务偏差列表 |
| `totalVarianced` | `Integer` | 偏差任务总数（开始或结束偏差非 0） |
| `needsApproval` | `Boolean` | 是否需要审批（三阈值 OR 任一命中） |
| `approvalReason` | `String` | 审批原因（超阈值时填充） |

**`DependencyCycleResult`** —— 循环依赖响应：

| 字段 | 类型 | 说明 |
|------|------|------|
| `success` | `Boolean` | 业务是否成功（检测到循环时为 `false`） |
| `errorCode` | `String` | 错误码（`CYCLE_DETECTED`） |
| `errorMessage` | `String` | 错误描述（含闭环路径任务名） |
| `cyclePath` | `List<CycleNode>` | 闭环路径节点列表（首尾为同一任务） |

**`CycleNode`** —— 闭环路径节点：`taskId` / `taskName`。

## 循环依赖 DFS 检测

### saveDependency 完整流程

`TaskDependencyServiceImpl.saveDependency(dependency)`（`@Transactional(rollbackFor = Exception.class)`）：

1. **依赖类型校验** —— `VALID_TYPES.contains(dependencyType)`，否则抛 `BusinessException("依赖类型非法，仅支持 FS/FF/SS/SF")`。
2. **`lagDays` 默认值** —— 为 `null` 时置 `0`。
3. **`predecessorTaskId / successorTaskId` 非空校验**。
4. **自环检测** —— `predecessorId.equals(successorId)` 时抛 `BusinessException("任务不能依赖自身")`。
5. **任务存在性校验** —— 调用 `implTaskMapper.selectTaskNameById(predecessorId)` / `selectTaskNameById(successorId)`（SQL：`SELECT task_name FROM pms_impl_task WHERE id = #{taskId} AND deleted = 0`），任一为 `null` 抛 `BusinessException`。
6. **闭环检测** —— 调用 `detectCycle(successorId, predecessorId, projectId)`：
   - 若返回非空路径 → 拼装 `List<CycleNode>`（含首尾闭合节点），抛 `CycleDetectedException(cyclePath)`。
   - 若返回空列表 → 无闭环，`this.save(dependency)` 持久化。

### DFS 算法（增量按需加载，TD-P8-009 修复）

```
detectCycle(start=successor, target=predecessor, projectId):
  visited = {}, path = []
  if dfs(start, target, projectId, visited, path):
      path.add(start)   # 追加起点闭合
      return path       # [start...target, start]
  return []

dfs(current, target, projectId, visited, path):
  path.add(current)
  if current == target: return true
  visited.add(current)
  # 按需加载：仅查询当前节点的直接后继
  successors = SELECT * FROM pms_task_dependency
               WHERE project_id = ? AND predecessor_task_id = current
  for dep in successors:
      next = dep.successorTaskId
      if next not in visited:
          if dfs(next, target, projectId, visited, path): return true
  path.removeLast()   # 回溯
  return false
```

**核心设计**：
- **增量加载**（TD-P8-009 修复）—— 仅对 DFS 实际访问到的节点查询其后继列表，不再一次性全量加载项目所有依赖到内存。对于大规模项目（>1000 任务），显著降低内存压力与首屏延迟。
- **方向语义** —— 沿 `predecessor → successor` 方向遍历；新增边为 `predecessor → successor`，若从 `successor` 出发能回到 `predecessor`，则形成闭环。
- **闭合路径** —— `path` 末尾追加 `start` 节点，形成 `A → B → C → A` 的闭合表达，便于前端直接展示。
- **性能注记** —— 当前为逐节点查询，最坏情况下 DFS 访问 N 个节点触发 N 次数据库查询。文档建议超大规模项目可优化为"批量加载"（每次按 50-200 个节点 ID 批量查询后继），在数据库往返次数与单次结果集大小间取得平衡。

### CycleDetectedException

`CycleDetectedException extends RuntimeException`（`@Getter`），常量 `ERROR_CODE = "CYCLE_DETECTED"`：

- 构造时通过 `buildMessage(cyclePath)` 生成消息：`"形成循环依赖，闭环路径: 任务A → 任务B → 任务C → 任务A"`（节点间以 ` → ` 连接）。
- 携带 `List<CycleNode> cyclePath`，由 `BaselineExceptionHandler` 转换为 `DependencyCycleResult` 返回前端。

## 模块依赖关系

### Maven 依赖（pom.xml）

```xml
<dependencies>
    <dependency><groupId>com.dp.plat</groupId><artifactId>pms-common</artifactId></dependency>
    <!-- 跨模块查询任务（ImplTaskMapper.selectTaskNameById / 查询项目任务） -->
    <dependency><groupId>com.dp.plat</groupId><artifactId>pms-implementation</artifactId></dependency>
    <!-- 项目配置（基线偏差阈值） -->
    <dependency><groupId>com.dp.plat</groupId><artifactId>pms-project</artifactId></dependency>
    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-web</artifactId></dependency>
    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-validation</artifactId></dependency>
    <dependency><groupId>com.baomidou</groupId><artifactId>mybatis-plus-spring-boot3-starter</artifactId></dependency>
    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-test</artifactId><scope>test</scope></dependency>
</dependencies>
```

### 依赖关系图

```
                    ┌──────────────────┐
                    │   pms-common     │  ◄── BaseEntity / Result / OperLog / BusinessException
                    │                  │      TaskPlanSnapshot / JsonTypeHandlers
                    │                  │      ApprovalTrigger / DependencyBatchCreator (SPI)
                    └────────┬─────────┘
                             │
   ┌─────────────────────────┼─────────────────────────┐
   │                         │                         │
   ▼                         ▼                         ▼
┌─────────────┐       ┌──────────────┐         ┌──────────────┐
│pms-project  │       │pms-baseline │         │pms-implementation│
│             │       │  (本模块)    │         │              │
│ProjectConfig│◄──────┤              │────────►│ImplTaskMapper│
│  Service    │ 读取阈值│              │ 查询任务 │  .selectTask │
│             │       │              │ 名称     │  NameById    │
└──────┬──────┘       └──────┬───────┘         └──────────────┘
       │                     │
       │                     │ @Autowired(required=false)
       │                     ▼
       │             ┌──────────────┐
       │             │ pms-workflow │  ◄── ApprovalTrigger SPI（触发 BASELINE_CHANGE）
       │             │              │      实现端，未加载时跳过
       │             └──────────────┘
       │
       │ @Autowired(required=false)
       ▼  DependencyBatchCreator SPI
   ┌──────────────┐
   │  pms-project │  调用方：模板深拷贝时批量创建依赖
   │  (调用方)    │  DependencyBatchCreatorImpl 实现在 pms-baseline
   └──────────────┘
```

### 跨模块协作点

| 协作方向 | 协作对象 | 用途 | 机制 |
|----------|----------|------|------|
| `pms-baseline` → `pms-implementation` | `ImplTaskMapper` | 1) 保存基线时 `selectList` 查询项目全部任务；2) `selectTaskNameById` 校验依赖任务存在 + 拼装闭环路径节点名称 | 直接依赖（Maven） |
| `pms-baseline` → `pms-project` | `ProjectConfigService` | 读取三阈值配置：`baseline.variance.days.threshold` / `baseline.variance.percent.threshold` / `baseline.variance.threshold.count` | 直接依赖（Maven） |
| `pms-baseline` → `pms-workflow` | `ApprovalTrigger` SPI | 偏差超阈值时触发 `BASELINE_CHANGE` 审批流程，回填 `approvalRecordId` | SPI（`@Autowired(required=false)`，未加载跳过） |
| `pms-project` → `pms-baseline` | `DependencyBatchCreator` SPI | 模板深拷贝（TD-P8-003）时批量创建任务依赖到 `pms_task_dependency` | SPI（`DependencyBatchCreatorImpl` 实现，`pms-project` 端 `@Autowired(required=false)`） |

### SPI 实现与扩展点

**1. `DependencyBatchCreatorImpl`（本模块 → 暴露给 `pms-project`）**

实现 `com.dp.plat.common.spi.DependencyBatchCreator`，用于 TD-P8-003 模板深拷贝。从模板创建项目时，`pms-project` 通过本 SPI 跨模块调用 `pms-baseline` 批量插入任务依赖记录到 `pms_task_dependency` 表，避免 `pms-project` 直接依赖 `pms-baseline`。

实现策略：
1. 查询项目下全部任务（`implTaskMapper.selectList`），构建 `taskName → taskId` 映射。
2. 遍历 `List<DependencyDef>`，解析 `predecessorTaskName / successorTaskName` 为任务 ID。
3. 名称解析失败（任务被删或重命名）时跳过该依赖并 `log.warn`，记录 `created / skipped` 计数。
4. **不走 `saveDependency` 循环检测** —— 模板内的依赖关系在模板设计阶段已校验过无环，深拷贝无需重复校验，直接 `taskDependencyMapper.insert(dep)`。
5. 默认值：`dependencyType = "FS"`、`lagDays = 0`（当 `DependencyDef` 对应字段为 `null` 时）。

**2. `ApprovalTrigger`（`pms-workflow` 实现，本模块消费）**

`BaselineServiceImpl` 通过 `@Autowired(required = false)` 注入 `ApprovalTrigger`。`pms-workflow` 模块未加载时为 `null`，`triggerBaselineChangeApproval` 内部判空后 `log.warn` 跳过审批触发，但保留 `changeReason` 字段不阻断主流程。`pms-workflow` 实现端应：构造 `ApprovalRecord`（`PENDING`、`round=1`）→ 调用 `ApprovalCenterService.createApproval` 落库 → 由 `ApprovalDispatcher` 启动 Flowable 流程实例并回填 `processInstanceId` → 返回审批记录ID供本模块回填 `baseline.approvalRecordId`。

## 关键技术点

### 1. JSON 列 TypeHandler（`autoResultMap` 必开）

`BaselineSnapshot.snapshotJson` 字段使用 `@TableField(typeHandler = JsonTypeHandlers.TaskPlanSnapshotListHandler.class)`，对应表 `pms_baseline_snapshot` 的 JSON 列。`@TableName` 必须设置 `autoResultMap = true`，否则字段级 `typeHandler` 在 `BaseMapper` 方法（如 `selectById`、`selectList`）中不生效 —— 这是 MyBatis-Plus 的已知行为。

`TaskPlanSnapshotListHandler`（位于 `pms-common` 的 `JsonTypeHandlers` 静态内部类）通过 `TypeReference<List<TaskPlanSnapshot>>` 重写 `parse` 方法，解决泛型擦除导致元素退化为 `LinkedHashMap` 的问题；使用自包含 `ObjectMapper`，不依赖父类 `getObjectMapper` 的可见性差异。

### 2. 单一活跃基线与乐观锁

- **单一活跃基线**：`saveBaseline` 中先将项目下所有 `APPROVED` 基线置为 `SUPERSEDED`，再创建 `DRAFT` 新基线，保证项目内同时只有一条 `APPROVED` 基线。
- **乐观锁**：两个实体均使用 `@Version` 字段（`version`），MyBatis-Plus 自动在 `updateById` 时附加 `WHERE version = ?` 条件，并发更新冲突时抛 `OptimisticLockingFailureException`。

### 3. DFS 增量按需加载（TD-P8-009 修复）

早期实现一次性全量加载项目所有依赖到内存构建邻接表，大规模项目下内存与首屏延迟压力大。当前实现改为按需加载：DFS 每访问一个节点时，仅查询 `WHERE project_id = ? AND predecessor_task_id = current` 的直接后继列表，未访问的节点不查询。

### 4. 三阈值 OR 触发审批

`requestBaselineChange` 采用三阈值 OR 逻辑判定是否触发审批：
- **天数阈值**（`baseline.variance.days.threshold`，默认 5）：`|endVariance| > daysThreshold`
- **百分比阈值**（`baseline.variance.percent.threshold`，默认 10）：`|endVariance| / baselineDuration * 100 > percentThreshold`
- **任务数阈值**（`baseline.variance.threshold.count`，默认 3）：`totalVarianced > countThreshold`

任一阈值命中即触发 `BASELINE_CHANGE` 审批；全部未命中时基线直接转为 `APPROVED`。`approvalReason` 字段动态拼接命中的阈值类型，便于审批人快速定位偏差原因。

### 5. 跨模块解耦（SPI + `@Autowired(required=false)`）

模块通过两个 SPI 实现跨模块解耦：
- **本模块消费** `ApprovalTrigger`（`pms-workflow` 提供）—— 基线变更审批触发。
- **本模块提供** `DependencyBatchCreator`（`pms-project` 消费）—— 模板深拷贝依赖批量创建。

双方均使用 `@Autowired(required = false)` 注入，对方模块未加载时优雅降级（`log.warn` 跳过、保留业务数据不阻断主流程），与 TD-P8-001 模式一致。这种设计允许 `pms-baseline` 在精简部署（不启动 `pms-workflow`）时仍可作为基线快照与依赖管理工具独立工作。

### 6. 日期字符串而非 LocalDate

`TaskPlanSnapshot` 的 `plannedStart / plannedEnd` 字段使用 `String`（ISO `yyyy-MM-dd`）而非 `LocalDate`，原因：
- 规避 MyBatis-Plus JSON `TypeHandler` 默认 `ObjectMapper` 缺少 `JavaTimeModule` 的序列化问题。
- 与 API 响应中的日期字符串格式保持一致，前端无需额外处理。

偏差计算时通过 `LocalDate.parse(string)` 解析回 `LocalDate`，使用 `ChronoUnit.DAYS.between` 计算天数差；解析异常时 `daysBetween` 返回 `null`（标记不可比较），`daysBetweenLong` 返回 `0L`（用于工期计算）。

### 7. 异常处理优先级

`BaselineExceptionHandler` 标注 `@Order(Ordered.HIGHEST_PRECEDENCE)`，确保 `CycleDetectedException` 由本模块处理器优先拦截，而非落入全局异常处理器。响应结构为 `Result.ok(DependencyCycleResult)` —— HTTP 200 + `data.success = false`，前端按 `success` 字段分支处理，与设计文档 §5.5 Story 4 验收 1 的响应结构一致。
