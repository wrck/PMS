# JSON TypeHandler 字段映射

> Phase 8 / Task 8.4 — JSON TypeHandler 注册校验
> 校验目标：所有使用 `@TableField(typeHandler=...)` 的实体，对应 TypeHandler 已在 `JsonTypeHandlers` 中定义；
> 实体类 `@TableName(autoResultMap=true)` 已开启；JSON 字段映射清晰可追溯
> 校验日期：2026-07-17
> 校验人：Phase 8 子代理（基于静态代码审查）
> 关联设计文档：§6.11（行 1749-1805）

## 1. TypeHandler 注册中心

**文件**：`pms-common/src/main/java/com/dp/plat/common/handler/JsonTypeHandlers.java`

`JsonTypeHandlers` 是一个 final 工具类，内部声明 4 个静态内部类，每个对应一种 JSON DTO：

| 内部类 | 父类 | 目标 Java 类型 | 用途 |
|---|---|---|---|
| `PhaseCriteriaHandler`        | `JacksonTypeHandler` | `com.dp.plat.common.dto.PhaseCriteria`         | 阶段进入条件 |
| `PhaseExitGateHandler`        | `JacksonTypeHandler` | `com.dp.plat.common.dto.PhaseExitGate`         | 阶段退出条件（4 类结构化条件） |
| `TaskPlanSnapshotListHandler` | `JacksonTypeHandler` | `java.util.List<TaskPlanSnapshot>`             | 基线快照任务计划列表（重写 parse + TypeReference） |
| `TemplateSnapshotHandler`     | `JacksonTypeHandler` | `com.dp.plat.common.dto.TemplateSnapshot`      | 模板内容快照（phases/tasks/milestones/deliverables/...） |

**特殊实现**：`TaskPlanSnapshotListHandler` 重写了 `parse(String json)` 方法，
通过 `TypeReference<List<TaskPlanSnapshot>>` 解决泛型擦除导致的元素退化为
`LinkedHashMap` 问题。使用自包含 `ObjectMapper`，不依赖父类
`getObjectMapper` 的可见性差异。

## 2. 实体使用情况总览

| 实体类 | 表名 | autoResultMap | JSON 字段 | 字段类型 | TypeHandler | DTO 类型 | 校验 |
|---|---|---|---|---|---|---|---|
| `ProjectTemplateVersion` | `pms_project_template_version` | **true** | `snapshotJson` | `TemplateSnapshot`        | `TemplateSnapshotHandler`     | `com.dp.plat.common.dto.TemplateSnapshot` | OK |
| `ProjectPhase`           | `pms_project_phase`            | **true** | `entryCriteria` | `PhaseCriteria`       | `PhaseCriteriaHandler`        | `com.dp.plat.common.dto.PhaseCriteria`    | OK |
| `ProjectPhase`           | `pms_project_phase`            | **true** | `exitCriteria`  | `PhaseExitGate`       | `PhaseExitGateHandler`        | `com.dp.plat.common.dto.PhaseExitGate`    | OK |
| `BaselineSnapshot`       | `pms_baseline_snapshot`        | **true** | `snapshotJson`  | `List<TaskPlanSnapshot>` | `TaskPlanSnapshotListHandler` | `com.dp.plat.common.dto.TaskPlanSnapshot` | OK |

**结论**：4 个 JSON 字段全部使用了 `JsonTypeHandlers` 中已注册的 TypeHandler，
且 3 个实体类均显式声明 `@TableName(... autoResultMap = true)`，符合设计文档
§6.11 的强制要求（autoResultMap 必须开启，否则字段级 typeHandler 在 BaseMapper
方法中不生效）。

## 3. JSON 字段到 DTO 类型的映射细节

### 3.1 ProjectTemplateVersion.snapshotJson ↔ TemplateSnapshot

**字段路径**：`pms_project_template_version.snapshot_json` (JSON NOT NULL)

**DTO 结构**：`com.dp.plat.common.dto.TemplateSnapshot`

```json
{
  "phases":        [{ "phaseCode": "PREPARE", "phaseName": "准备阶段", "sortOrder": 1, "exitCriteria": {...} }],
  "tasks":         [{ "taskName": "项目启动会", "taskType": "OEM", "priority": "HIGH" }],
  "deliverables":  [{ "deliverableName": "实施方案", "deliverableType": "DOCUMENT", "mandatory": 1, "approverRole": "TECH_LEAD" }],
  "dependencies":  [{ "predecessorTaskName": "需求确认", "successorTaskName": "现场勘查", "dependencyType": "FS", "lagDays": 1 }],
  "milestones":    [...],
  "approvalPlans": [...],
  "assigneeRules": [...]
}
```

**校验**：DTO 字段命名与 V72 种子数据 JSON_OBJECT 调用一致。OK

### 3.2 ProjectPhase.entryCriteria ↔ PhaseCriteria

**字段路径**：`pms_project_phase.entry_criteria` (JSON NULL)

**DTO 结构**：`com.dp.plat.common.dto.PhaseCriteria`（进入条件）

**校验**：V72 演示数据中 `entry_criteria = NULL`，符合"进入条件可选"的设计。
当 Service 写入时通过 `PhaseCriteriaHandler` 序列化。OK

### 3.3 ProjectPhase.exitCriteria ↔ PhaseExitGate

**字段路径**：`pms_project_phase.exit_criteria` (JSON NULL)

**DTO 结构**：`com.dp.plat.common.dto.PhaseExitGate`（退出条件，4 类条件）

```json
{
  "requiredDeliverables": [{ "deliverableName": "实施方案", "requiredStatus": "PUBLISHED" }],
  "requiredTasks":        [{ "phaseId": 5001, "allCompleted": true }],
  "requiredMilestones":   [...],
  "requiredApprovals":    [...]
}
```

**校验**：V72 中阶段 5001/5002 的 exit_criteria JSON 与 PhaseExitGate DTO
字段命名（requiredDeliverables/requiredTasks 等）一致。OK

### 3.4 BaselineSnapshot.snapshotJson ↔ List<TaskPlanSnapshot>

**字段路径**：`pms_baseline_snapshot.snapshot_json` (JSON NOT NULL)

**DTO 结构**：`List<com.dp.plat.common.dto.TaskPlanSnapshot>`

```json
[
  { "taskId": 8001, "taskName": "项目启动会", "plannedStart": "2026-07-01", "plannedEnd": "2026-07-02", "duration": 2, "plannedHours": 4 },
  { "taskId": 8002, "taskName": "需求确认",   "plannedStart": "2026-07-03", "plannedEnd": "2026-07-07", "duration": 5, "plannedHours": 16 }
]
```

**校验**：DTO 字段命名与 V72 基线快照 JSON_ARRAY(JSON_OBJECT(...)) 调用一致。
TypeHandler 重写 parse 以 `TypeReference<List<TaskPlanSnapshot>>` 反序列化，
避免泛型擦除导致元素退化为 `LinkedHashMap`。OK

## 4. 其他 TypeHandler 实体（非 JSON 类）

> Phase 8 校验范围聚焦于 JSON TypeHandler，但为完整起见列出其他 typeHandler 用法。

| 实体类 | 字段 | TypeHandler | 类型 | 校验 |
|---|---|---|---|---|
| `SysUser`   | `email` | `EncryptTypeHandler` | AES-256-GCM 字段级加密 | OK（pms-common/.../crypto/EncryptTypeHandler） |
| `SysUser`   | `phone` | `EncryptTypeHandler` | AES-256-GCM 字段级加密 | OK |

`SysUser` 也开启了 `@TableName(autoResultMap = true)`，符合 EncryptTypeHandler
生效前提。OK

## 5. 表中的 JSON 列 vs 实体字段映射对照

| 表 | JSON 列 | 实体字段 | 是否需 TypeHandler | 当前处理 | 校验 |
|---|---|---|---|---|---|
| `pms_project_template_version` | `snapshot_json`    | `ProjectTemplateVersion.snapshotJson`   | **是**（结构化 DTO） | `TemplateSnapshotHandler`        | OK |
| `pms_project_phase`            | `entry_criteria`   | `ProjectPhase.entryCriteria`            | **是**（结构化 DTO） | `PhaseCriteriaHandler`           | OK |
| `pms_project_phase`            | `exit_criteria`    | `ProjectPhase.exitCriteria`             | **是**（结构化 DTO） | `PhaseExitGateHandler`           | OK |
| `pms_baseline_snapshot`        | `snapshot_json`    | `BaselineSnapshot.snapshotJson`         | **是**（List DTO）   | `TaskPlanSnapshotListHandler`    | OK |
| `pms_task_activity`            | `metadata`         | `TaskActivity.metadata`                 | 否（自由 JSON，String 存储） | 无 typeHandler，String 直接读写 | OK（设计如此） |

## 6. MySQL 8 JSON 列约束注意事项

参考设计文档 §6.11 行 1801-1805：

1. **MySQL 8 的 JSON 类型不能有非 NULL 默认值**：所有 JSON 列的 DEFAULT 子句
   必须为 NULL 或省略。由应用层保证非空（如 `BaselineSnapshot.snapshotJson`
   标注 `@NotNull`，DB 列为 `JSON NOT NULL`）。
2. **空对象写入**：用 `'{}'` 或 `'[]'` 显式 INSERT，避免 NULL。
3. **V64~V72 迁移脚本中 JSON 列均无 DEFAULT 子句**，符合约束。已通过 Task 8.2
   的 `validate-migrations.py` 校验。

## 7. 校验结论

| 项 | 结论 |
|---|---|
| `JsonTypeHandlers` 注册中心完整 | **PASS**（4 个 TypeHandler 子类全部声明） |
| 4 个 JSON 字段全部使用已注册 TypeHandler | **PASS** |
| 3 个实体类 `@TableName(autoResultMap=true)` 全部开启 | **PASS** |
| DTO 字段命名与 V72 种子数据 JSON_OBJECT 调用一致 | **PASS** |
| `TaskPlanSnapshotListHandler` 重写 parse 解决泛型擦除 | **PASS** |
| JSON 列无 DEFAULT 子句（MySQL 8 约束） | **PASS** |
| 自由 JSON 列（pms_task_activity.metadata）使用 String 存储 | **PASS**（设计如此） |
| 其他 typeHandler（EncryptTypeHandler）注册合规 | **PASS** |

**总评**：Phase 1-7 在 JSON 字段处理上完全遵循设计文档 §6.11 的规范，
autoResultMap、TypeHandler 注册、DTO 命名、泛型擦除规避 4 项关键点全部满足。

---

文件路径：`docs/superpowers/architecture/json-typehandler-mapping.md`
