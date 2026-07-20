# 项目管理增强设计文档

> **版本**：v1.0
> **日期**：2026-07-17
> **状态**：待评审
> **作者**：项目团队
> **关联**：6 个 P1 用户故事（项目管理模块）

---

## 1. 概述

### 1.1 设计目标

为现有 PMS 平台补齐项目管理核心能力，覆盖 6 个 P1 用户故事：

1. **项目模板与项目创建** — 模板维护、从模板创建项目、版本快照
2. **项目生命周期与主子项目** — 阶段推进、退出条件、主子项目汇总
3. **任务、检查项与团队协作** — 父子任务、检查项、评论、附件、活动记录
4. **里程碑、依赖和计划基线** — 循环依赖检测、基线快照、偏差分析
5. **交付件全生命周期** — 7 态状态流转、版本管理、归档
6. **统一审批中心** — 跨业务统一审批、敏感字段权限、历史记录

### 1.2 范围

- **单一 spec 覆盖 6 个故事**（耦合度高，一致性优先）
- 后端：5 个模块（pms-project / pms-implementation / pms-deliverable / pms-baseline / pms-workflow）
- 前端：18 个新页面 + 11 个新组件 + 路由全量重构
- 数据库：9 个 Flyway 迁移脚本（V64~V72）

### 1.3 关键设计决策

| # | 决策点 | 选择 | 依据 |
|---|---|---|---|
| 1 | Phase 模型 | 模板驱动的独立实体 | Story 1 "自动获得阶段" + Story 2 "阶段退出条件" |
| 2 | 模板版本快照 | 创建项目时深拷贝模板内容 | Story 1 验收 2 "存量项目保持原版本快照" |
| 3 | 任务嵌套深度 | **无限制嵌套**（邻接表 + 物化路径） | 用户明确要求 |
| 4 | 检查项结构 | 独立表 `pms_task_checklist`，含 `mandatory` 标志 | Story 3 验收 1 "强制检查项"需结构化 |
| 5 | 依赖类型 | 4 类：FS / FF / SS / SF + lag 天数 | 行业标准 CPM 网络 |
| 6 | 基线管理 | 单一活跃基线 + 历史基线归档；快照所有任务计划日期 | Story 4 验收 2 |
| 7 | 交付件状态机 | 7 态：DRAFT→SUBMITTED→REVIEWED→SIGNED→PUBLISHED→REFERENCED→ARCHIVED | Story 5 |
| 8 | 审批中心引擎 | 复用 Flowable + 新增统一审批记录层 | 现有 5 个 BPMN + 横切审批记录需求 |
| 9 | 敏感字段权限 | 字段级权限表 + 序列化时脱敏 | Story 6 验收 1 |
| 10 | 模块组织 | 新建 pms-deliverable + pms-baseline 模块 | 单一职责 |
| 11 | 主子项目汇总 | **无限嵌套** + **权重自定义** | 用户明确要求 |
| 12 | 循环依赖检测 | DFS 拓扑排序，返回闭环路径 | Story 4 验收 1 |
| 13 | JSON 字段处理 | MySQL JSON 类型 + 自定义 JacksonTypeHandler 子类 | 用户明确要求 |
| 14 | 路由方案 | **嵌套路由全量重构** | 可维护性优先，不留技术债 |
| 15 | 依赖图渲染 | **AntV G6 v5** | DAG 布局、节点交互、闭环高亮 |
| 16 | 阈值配置 | 多层级可配置（项目级 > 模板级 > 系统默认） | 用户明确要求 |

---

## 2. 数据模型与 ER 关系

### 2.1 实体总览（16 个新增/扩展）

```
ProjectTemplate + ProjectTemplateVersion     【新增】模板与版本
Project                                        【扩展】主子项目、模板关联、阶段关联
ProjectPhase                                   【新增】阶段
ProjectMember                                  【新增】成员
ProjectConfig                                  【新增】多层级配置
ImplTask                                       【扩展】层级、优先级、工时
TaskChecklist                                  【新增】检查项
TaskDependency                                 【新增】任务依赖
TaskComment                                    【新增】任务评论
TaskActivity                                   【新增】任务活动记录
BaselineSnapshot                              【新增】计划基线快照
Deliverable                                    【扩展】7 态状态机
DeliverableVersion                             【新增】交付件版本
DeliverableSignature                           【新增】交付件签名
DeliverableReference                           【新增】交付件引用关系
ApprovalRecord + ApprovalNode + ApprovalHistory + ApprovalFieldPermission  【新增】统一审批中心
```

### 2.2 核心实体字段

#### Project（扩展）

```java
// 现有字段保留
private Long parentProjectId;          // 主子项目，NULL=顶层
private String projectPath;            // 物化路径 "/1/5/"
private Integer depth;                 // 深度冗余
private BigDecimal weight;             // 自定义权重，默认 1.00
private Long templateId;               // 来源模板
private String templateVersion;        // 模板版本快照
private Long currentPhaseId;           // 当前阶段
private String projectObjective;       // 项目目标
private String projectScope;           // 项目范围
```

#### ProjectTemplate + ProjectTemplateVersion（新增）

```java
// ProjectTemplate
private String templateCode;
private String templateName;
private String category;               // IMPLEMENT / MAINTENANCE / CONSULTING
private String description;
private String status;                 // DRAFT / PUBLISHED / DEPRECATED

// ProjectTemplateVersion
private Long templateId;
private String version;                // 语义化版本 v1.0.0
private TemplateSnapshot snapshotJson; // 完整模板内容快照（JSON）
private String changeLog;
private String status;                 // DRAFT / PUBLISHED / ARCHIVED
private LocalDateTime publishedAt;
private Long publishedBy;
```

**版本快照策略**：模板内容以 JSON 文档存入 `snapshot_json`。创建项目时反序列化并深拷贝到项目相关表，保证存量项目不受模板后续修改影响。

#### ProjectPhase（新增）

```java
private Long projectId;
private Long templatePhaseId;          // 来源模板阶段 ID（追溯用）
private String phaseName;
private String phaseCode;              // PREPARE/PLAN/DESIGN/IMPLEMENT/OPERATE 或自定义
private Integer sortOrder;
private PhaseCriteria entryCriteria;   // 进入条件（JSON）
private PhaseExitGate exitCriteria;    // 退出条件（JSON）
private String status;                 // NOT_STARTED / IN_PROGRESS / COMPLETED / SKIPPED
private LocalDate plannedStartDate;
private LocalDate plannedEndDate;
private LocalDate actualStartDate;
private LocalDate actualEndDate;
```

#### ImplTask（扩展）

```java
// 现有字段保留
private Long parentTaskId;             // 父任务，NULL=顶层
private String taskPath;               // "/12/45/78/"
private Integer depth;                 // 0=顶层
private String priority;               // LOW / MEDIUM / HIGH / CRITICAL
private BigDecimal actualHours;        // 实际工时
private BigDecimal remainingHours;     // 剩余工时
private Long phaseId;                  // 关联阶段
private BigDecimal taskWeight;         // 自定义汇总权重（配置开启时生效）
```

#### TaskChecklist（新增）

```java
private Long taskId;
private String title;
private String description;
private Boolean mandatory;             // 强制检查项
private Boolean checked;
private Long checkedBy;
private LocalDateTime checkedAt;
private Integer sortOrder;
```

#### TaskDependency（新增）

```java
private Long projectId;
private Long predecessorTaskId;        // 前置任务
private Long successorTaskId;          // 后续任务
private String dependencyType;         // FS / FF / SS / SF
private Integer lagDays;               // 滞后天数（可负）
```

#### BaselineSnapshot（新增）

```java
private Long projectId;
private String baselineName;
private String status;                 // DRAFT / APPROVED / SUPERSEDED
private List<TaskPlanSnapshot> snapshotJson;  // 全部任务计划快照（JSON）
private String changeReason;
private Long approvalRecordId;
private LocalDateTime approvedAt;
private Long approvedBy;
```

**单一活跃基线**：项目同时只有一条 `APPROVED` 状态基线；新建基线时将前一条置为 `SUPERSEDED`。

#### Deliverable（扩展为 7 态状态机）

```java
// 现有字段保留
private Long phaseId;                  // 所属阶段
private String status;                 // DRAFT/SUBMITTED/REVIEWED/SIGNED/PUBLISHED/REFERENCED/ARCHIVED
private Integer currentVersion;        // 当前版本号，从 1 开始
private Boolean mandatory;             // 必需交付件（影响阶段退出）
private String approverRole;           // 签核角色
private LocalDateTime publishedAt;
private LocalDateTime archivedAt;
```

#### DeliverableVersion（新增）

```java
private Long deliverableId;
private Integer versionNo;             // 1, 2, 3...
private String filePath;
private String fileChecksum;
private Long uploadedBy;
private LocalDateTime uploadedAt;
private String changeLog;
private String status;                 // 该版本流转状态
```

**版本不可变**：已发布版本的 `filePath` 不允许覆盖；修订时新建 `versionNo + 1` 的记录。

#### ApprovalRecord（新增 - 统一审批中心）

```java
private String approvalType;           // PROJECT/TASK/DELIVERABLE/RISK/ISSUE/CHANGE/RESOURCE/COST/PHASE_EXIT/BASELINE_CHANGE
private Long businessId;
private String businessCode;
private Long projectId;
private String processInstanceId;      // Flowable 实例 ID
private String title;
private Long submitterId;
private String submitterName;
private String currentNodeName;
private String status;                 // PENDING / APPROVED / REJECTED / WITHDRAWN / TIMEOUT
private Integer round;                 // 第几轮
private LocalDateTime submittedAt;
private LocalDateTime completedAt;
private LocalDateTime timeoutAt;
private Boolean escalated;
```

#### ApprovalFieldPermission（新增 - 敏感字段）

```java
private Long approvalNodeId;
private String entityType;             // 业务实体类名
private String fieldName;
private String permission;             // VISIBLE / MASKED / HIDDEN
private String maskPattern;            // phone-mask / amount-mask / email-mask / custom
private String customPattern;          // 自定义正则
```

### 2.3 关键约束与索引

| 实体 | 唯一约束 | 索引 |
|---|---|---|
| Project | `uk_project_code` | `idx_parent_project_id`, `idx_project_path`, `idx_template_id` |
| ProjectTemplateVersion | `uk_template_version` | `idx_template_id_status` |
| ProjectPhase | `uk_project_phase_code` | `idx_project_id_sort` |
| ImplTask | - | `idx_parent_task_id`, `idx_task_path`, `idx_project_id_phase` |
| TaskDependency | `uk_pred_succ_type` | `idx_successor_task_id`, `idx_predecessor_task_id` |
| BaselineSnapshot | - | `idx_project_id_status` |
| DeliverableVersion | `uk_deliverable_version` | `idx_deliverable_id` |
| ApprovalRecord | - | `idx_business_type_id`, `idx_project_id_status`, `idx_submitter_status` |

### 2.4 无限嵌套实现（邻接表 + 物化路径）

```sql
parent_id    BIGINT NULL,            -- 邻接表，便于单点更新
path         VARCHAR(500) NOT NULL,  -- 物化路径如 "/12/45/78/"，便于祖先/后代查询
depth        INT NOT NULL DEFAULT 0  -- 深度冗余，用于排序与展示
```

**理由**：项目场景下任务数通常 < 10K，物化路径插入/移动成本可接受；汇总查询用 `LIKE '/12/%'` 比递归 CTE 更直观，且支持任意深度。维护 `path` 由服务层在 `parentId` 变更时同步更新。

### 2.5 主子项目递归汇总

```sql
-- 递归查询某主项目下所有子孙项目
WITH RECURSIVE project_tree AS (
  SELECT id, parent_project_id, progress, weight, project_path
  FROM pms_project WHERE id = #{rootProjectId}
  UNION ALL
  SELECT p.id, p.parent_project_id, p.progress, p.weight, p.project_path
  FROM pms_project p
  JOIN project_tree t ON p.parent_project_id = t.id
  WHERE p.deleted = 0
)
-- 加权平均进度
SELECT
  SUM(CASE WHEN parent_project_id IS NULL THEN 0 ELSE progress * weight END) /
  NULLIF(SUM(CASE WHEN parent_project_id IS NULL THEN 0 ELSE weight END), 0) AS aggregated_progress
FROM project_tree;
```

任务层级汇总同理，用 `task_path LIKE '/12/%'` 查询所有子孙任务，按子任务完成率加权（默认按 plannedHours 加权，可配置为 taskWeight）。

---

## 3. 状态机与业务规则

### 3.1 项目生命周期状态机

```
        创建        启动审批通过        全部阶段完成 + 关闭审批通过
   ──────────► [PLANNING] ──────► [EXECUTING] ──────► [CLOSING] ──────► [CLOSED]
                   (规划)            (执行)             (收尾)           (已关闭)
                                      ▲  │
                                      │  │ 阶段推进循环
                                      └──┘

   异常分支：
   PLANNING/EXECUTING → SUSPENDED（暂停） → 可恢复
   任意状态 → CANCELLED（取消，需审批）
```

### 3.2 阶段状态机

```
[NOT_STARTED] ──进入条件满足──► [IN_PROGRESS] ──退出条件满足──► [COMPLETED]
                                    │
                                    │ 审批跳过
                                    ▼
                               [SKIPPED]
```

#### 阶段退出条件（PhaseExitGate，结构化 JSON）

```json
{
  "requiredDeliverables": [
    {"deliverableId": 101, "requiredStatus": "PUBLISHED"}
  ],
  "requiredTasks": [
    {"phaseId": 5, "allCompleted": true}
  ],
  "requiredMilestones": [
    {"milestoneId": 88, "mustReached": true}
  ],
  "requiredApprovals": [
    {"approvalType": "PHASE_EXIT", "mustApproved": true}
  ]
}
```

**Story 2 验收 1 实现**：调用 `advancePhase(projectId, phaseId)` 时：
1. 加载当前阶段 `exitCriteria` JSON
2. 逐项校验：交付件状态、任务完成、里程碑达成、审批通过
3. 若任一项未满足，**阻止推进**并返回 `{gate, missingItems[], reason}`

**Story 2 验收 2 实现**：调用 `closeProject(projectId)` 时：
1. 递归查询所有子孙项目（CTE）
2. 校验所有子项目 `status IN (CLOSED, CANCELLED)`
3. 若存在未关闭子项目，**拒绝关闭**并返回 `{uncompletedSubProjects[]}`

### 3.3 任务状态机

```
[PENDING] ──分配──► [ASSIGNED] ──开始──► [IN_PROGRESS] ──提交──► [REVIEW]
                                                   │              │
                                                   │              ├─ 通过 ─► [COMPLETED]
                                                   │              ├─ 退回 ─► [IN_PROGRESS]
                                                   │              └─ 强制检查项未通过 ─► [BLOCKED]
                                                   │
                                                   └── 阻塞 ─► [BLOCKED] ──恢复──► [IN_PROGRESS]
```

#### Story 3 验收 1：强制检查项拦截

```java
public Result<TaskCompleteResult> submitForReview(Long taskId, Long operatorId) {
    // 1. 校验强制检查项
    List<TaskChecklist> unchecked = checklistMapper.selectList(
        new LambdaQueryWrapper<TaskChecklist>()
            .eq(TaskChecklist::getTaskId, taskId)
            .eq(TaskChecklist::getMandatory, true)
            .eq(TaskChecklist::getChecked, false));
    
    if (!unchecked.isEmpty()) {
        return Result.fail("TASK_CHECKLIST_REQUIRED", "存在未完成的强制检查项", unchecked);
    }
    
    // 2. 流转到 REVIEW 状态
    task.setStatus("REVIEW");
    taskMapper.updateById(task);
    
    // 3. 记录活动
    activityService.record(taskId, "TASK_SUBMIT_REVIEW", operatorId);
    return Result.ok(TaskCompleteResult.builder()
        .success(true)
        .taskStatus("REVIEW")
        .build());
}
```

#### Story 3 验收 2：任务进度汇总

**汇总规则**：父任务进度 = Σ(子任务 progress × weight) / Σ(weight)，权重默认为 `plannedHours`，可配置为 `taskWeight`。

**触发时机**：子任务 `progress` 或 `status` 变更后，异步任务（Spring Event）触发父链路汇总，避免阻塞主流程。

### 3.4 交付件状态机（7 态）

```
        创建        提交         审核           签核          发布
   ────────► [DRAFT] ──► [SUBMITTED] ──► [REVIEWED] ──► [SIGNED] ──► [PUBLISHED]
                 ▲                                    │              │
                 │ 退回                                │              │ 引用
                 │                                    │              ▼
                 │                                    │         [REFERENCED]
                 │ 修订（新建版本）                    │              │
                 │                                    │              │ 归档
                 └────────────────────────────────────┘              ▼
                                                                 [ARCHIVED]
```

#### 状态流转规则

| 当前状态 | 允许的下一状态 | 操作角色 | 副作用 |
|---|---|---|---|
| DRAFT | SUBMITTED | 提交人 | 创建 ApprovalRecord(DELIVERABLE) |
| SUBMITTED | REVIEWED / DRAFT | 审核人 | - |
| REVIEWED | SIGNED / DRAFT | 签核人 | 记录 DeliverableSignature |
| SIGNED | PUBLISHED | 项目经理 | 写入 `publishedAt`，版本固化 |
| PUBLISHED | REFERENCED | 任何引用方 | 创建 DeliverableReference |
| PUBLISHED | DRAFT | 编辑人 | **新建版本 v(n+1)**，旧版本保留 |
| REFERENCED | ARCHIVED | 项目经理 | 写入 `archivedAt` |
| ARCHIVED | （终态） | - | 只读 |

**Story 5 验收 1 实现**：调用 `reviseDeliverable(deliverableId, newFilePath, changeLog)` 时：
1. 校验当前状态为 PUBLISHED 或 REFERENCED
2. 新建 `DeliverableVersion`，`versionNo = currentVersion + 1`，`status = DRAFT`
3. 更新 `Deliverable.currentVersion` 和 `status = DRAFT`
4. **旧版本记录保留不变**

**Story 5 验收 2 实现**：阶段退出校验时，`PhaseExitGate.requiredDeliverables` 检查必需交付件是否达到 PUBLISHED/REFERENCED/ARCHIVED（即已批准），若未达到则阻止阶段完成。

### 3.5 审批中心统一规则

#### ApprovalRecord 状态机

```
[DRAFT] ──提交──► [PENDING] ──通过──► [APPROVED]
                     │
        ┌────────────┼────────────┐
        ▼            ▼            ▼
   [REJECTED]   [WITHDRAWN]   [TIMEOUT]
        │ 重新提交
        ▼
   [PENDING] round = round + 1
```

#### Story 6 验收 1：敏感字段脱敏

审批人打开审批详情时，后端根据 `ApprovalFieldPermission` 配置脱敏：

```java
public Result<ApprovalDetailVO> getDetail(Long recordId, Long userId) {
    ApprovalRecord record = approvalRecordMapper.selectById(recordId);
    
    // 1. 获取当前用户在当前节点的字段权限
    List<ApprovalFieldPermission> perms = fieldPermMapper.selectList(
        new LambdaQueryWrapper<ApprovalFieldPermission>()
            .eq(ApprovalFieldPermission::getApprovalNodeId, record.getCurrentNodeId()));
    
    // 2. 加载业务对象
    Object businessEntity = businessLoader.load(record.getApprovalType(), record.getBusinessId());
    
    // 3. 按权限脱敏后返回
    ApprovalDetailVO vo = ApprovalDetailVO.builder()
        .record(record)
        .businessData(sensitiveFieldMasker.mask(businessEntity, perms))
        .history(approvalHistoryMapper.findByRecordId(recordId))
        .build();
    return Result.ok(vo);
}
```

**脱敏示例**：金额 `12345.67` → `12***.67`，手机号 `13812345678` → `138****5678`。

#### Story 6 验收 2：审批历史保留

- `ApprovalRecord.round` 字段记录轮次（1, 2, 3...）
- `ApprovalHistory` 子表记录每轮每次操作（节点、操作人、动作、意见、时间戳）
- 退回后重新提交：`round += 1`，**复用原 ApprovalRecord**（不新建），但 ApprovalHistory 追加新行

```sql
-- 查询某审批全部历史（含所有轮次）
SELECT round, node_name, operator_id, action, opinion, operated_at
FROM pms_approval_history
WHERE record_id = #{recordId}
ORDER BY round ASC, operated_at ASC;
```

#### 审批触发规则矩阵

| 业务事件 | 审批类型 | 触发条件 | 通过后动作 |
|---|---|---|---|
| 创建项目（从模板） | PROJECT_CREATE | 配置开启 | 项目 status: PLANNING → EXECUTING |
| 项目启动 | PROJECT_START | 配置开启 | 同上 |
| 项目关闭 | PROJECT_CLOSE | 必审批 | 项目 status: CLOSING → CLOSED |
| 项目取消 | PROJECT_CANCEL | 必审批 | 项目 status → CANCELLED |
| 阶段跳过 | PHASE_SKIP | 配置开启 | Phase.status → SKIPPED |
| 阶段退出 | PHASE_EXIT | 配置开启 | Phase.status → COMPLETED |
| 任务完成验收 | TASK_COMPLETE | 配置开启 | Task.status → COMPLETED |
| 交付件提交/发布 | DELIVERABLE | 必审批 | Deliverable 流转下一态 |
| 基线变更 | BASELINE_CHANGE | 必审批 | 旧基线 SUPERSEDED + 新基线 APPROVED |
| 风险/问题/变更 | RISK/ISSUE/CHANGE | 配置开启 | 各自业务状态流转 |

### 3.6 依赖与基线规则

#### 循环依赖检测（Story 4 验收 1）

```java
public Result<?> saveDependency(TaskDependency dep) {
    // 1. 自环检测
    if (dep.getPredecessorTaskId().equals(dep.getSuccessorTaskId())) {
        return Result.fail("SELF_DEPENDENCY", "任务不能依赖自身");
    }
    
    // 2. 闭环检测：检查 successor → predecessor 是否存在路径
    List<Long> cyclePath = detectCycle(dep.getSuccessorTaskId(), dep.getPredecessorTaskId(), dep.getProjectId());
    if (!cyclePath.isEmpty()) {
        return Result.fail("CYCLE_DETECTED", "形成循环依赖，闭环路径: " + cyclePath, cyclePath);
    }
    
    dependencyMapper.insert(dep);
    return Result.ok(dep);
}
```

#### 计划基线偏差分析（Story 4 验收 2）

```java
public boolean needsBaselineChangeApproval(Long projectId, TaskDiff diff) {
    int daysThreshold = configService.getInt(projectId, "baseline.variance.days.threshold");
    int percentThreshold = configService.getInt(projectId, "baseline.variance.percent.threshold");
    
    long daysVar = Math.abs(diff.getEndVariance());
    long duration = ChronoUnit.DAYS.between(diff.getBaselineStart(), diff.getBaselineEnd());
    double percentVar = duration > 0 ? (double) Math.abs(diff.getEndVariance()) / duration * 100 : 0;
    
    return daysVar > daysThreshold || percentVar > percentThreshold;  // 双阈值 OR
}
```

**关键日期变更触发审批**：当偏差超阈值时，自动发起 `BASELINE_CHANGE` 审批，记录 `changeReason`，审批通过后才能保存新基线。

---

## 4. 服务层架构与模块边界

### 4.1 模块物理布局

```
network-equipment-pms/
├── pms-project/                    【扩展】项目骨架
│   ├── entity/
│   │   ├── Project.java            【扩展】
│   │   ├── ProjectTemplate.java    【新增】
│   │   ├── ProjectTemplateVersion.java  【新增】
│   │   ├── ProjectPhase.java       【新增】
│   │   ├── ProjectMember.java      【新增】
│   │   └── ProjectConfig.java      【新增】
│   ├── service/
│   │   ├── IProjectTemplateService.java  【新增】
│   │   ├── IProjectPhaseService.java     【新增】
│   │   ├── IProjectMemberService.java    【新增】
│   │   └── ProjectConfigService.java     【新增】
│   └── controller/
│       ├── ProjectTemplateController.java  【新增】
│       ├── ProjectPhaseController.java     【新增】
│       └── ProjectMemberController.java    【新增】
│
├── pms-implementation/             【扩展】任务执行
│   ├── entity/
│   │   ├── ImplTask.java           【扩展】
│   │   ├── TaskChecklist.java      【新增】
│   │   ├── TaskDependency.java     【新增】
│   │   ├── TaskComment.java        【新增】
│   │   └── TaskActivity.java       【新增】
│   ├── service/
│   │   ├── ITaskChecklistService.java    【新增】
│   │   ├── ITaskDependencyService.java   【新增】
│   │   ├── ITaskCommentService.java      【新增】
│   │   ├── ITaskActivityService.java     【新增】
│   │   └── TaskRollupService.java        【新增】
│   └── controller/...
│
├── pms-deliverable/                【新建模块】交付件全生命周期
│   ├── entity/
│   │   ├── Deliverable.java        【迁移+扩展】
│   │   ├── DeliverableVersion.java
│   │   ├── DeliverableSignature.java
│   │   └── DeliverableReference.java
│   ├── service/...
│   └── controller/...
│
├── pms-baseline/                   【新建模块】基线与依赖
│   ├── entity/
│   │   ├── BaselineSnapshot.java
│   │   └── TaskDependency.java     （或合并到 pms-implementation）
│   ├── service/
│   │   ├── IBaselineService.java
│   │   └── BaselineDiffService.java
│   └── controller/...
│
├── pms-workflow/                   【扩展】统一审批中心
│   ├── entity/
│   │   ├── ApprovalRecord.java     【新增】
│   │   ├── ApprovalNode.java       【新增】
│   │   ├── ApprovalHistory.java    【新增】
│   │   └── ApprovalFieldPermission.java  【新增】
│   ├── service/
│   │   ├── IApprovalRecordService.java       【新增】
│   │   ├── IApprovalFieldPermissionService.java  【新增】
│   │   ├── ApprovalTimeoutScheduler.java    【新增】
│   │   ├── SensitiveFieldMasker.java        【新增】
│   │   └── ApprovalDispatcher.java          【新增】
│   └── controller/
│       └── ApprovalCenterController.java    【新增】
│
├── pms-governance/                 【保留】BaselineHistory 作为审计流水
│
└── pms-common/                     【扩展】
    ├── handler/
    │   └── JsonTypeHandlers.java   【新增】JSON TypeHandler 子类集合
    └── annotation/
        └── SensitiveField.java     【新增】字段级脱敏注解
```

### 4.2 跨模块依赖关系

```
pms-common ◄── 所有模块
   │
   ▼
pms-workflow ◄── pms-project, pms-implementation, pms-deliverable, pms-baseline
   ▲                │                  │                       │                │
   │                │                  │                       │                │
   │  ApprovalDispatcher 监听业务事件    │                       │                │
   │                ▼                  ▼                       ▼                ▼
   │         ProjectService     ImplTaskService        DeliverableService   BaselineService
   │                │                  │                       │                │
   │                └──────────────────┴───────────┬───────────┴────────────────┘
   │                                               │
   │                                               ▼
   └─────────────  Spring ApplicationEvent  ◄── PhaseAdvanceEvent
                                                   TaskProgressChangedEvent
                                                   DeliverableSubmitEvent
                                                   BaselineChangeEvent
```

### 4.3 关键服务接口契约

```java
// Story 1
public interface IProjectTemplateService {
    ProjectTemplateVersion publishVersion(Long templateId, String version, TemplateSnapshot snapshot, String changeLog);
    Project createProjectFromTemplate(Long templateId, Long versionId, ProjectCreateFromTemplateDTO dto);
}

// Story 2
public interface IProjectPhaseService {
    Result<PhaseAdvanceResult> advancePhase(Long projectId, Long phaseId, Long operatorId);
    Result<?> closeProject(Long projectId, Long operatorId);
    Result<List<GateViolation>> validateExitGates(Long phaseId);
    Result<List<Project>> validateSubProjectsClosed(Long projectId);
}

// Story 3
public interface IImplTaskService {
    Result<TaskCompleteResult> submitForReview(Long taskId, Long operatorId);
    Result<TaskCompleteResult> approveTask(Long taskId, Long operatorId, String opinion);
    ImplTask moveTask(Long taskId, Long newParentId);
    List<ImplTask> getSubtree(Long taskId);
}

// Story 4
public interface ITaskDependencyService {
    Result<?> saveDependency(TaskDependency dep);
    List<Long> detectCycle(Long startTaskId, Long targetTaskId, Long projectId);
}

public interface IBaselineService {
    BaselineSnapshot saveBaseline(Long projectId, String name, Long operatorId);
    BaselineDiffVO compareWithBaseline(Long projectId);
    Result<?> requestBaselineChange(Long projectId, List<TaskDiff> diffs, String reason);
}

// Story 5
public interface IDeliverableService {
    Result<Deliverable> submit(Long id, Long operatorId);
    Result<Deliverable> review(Long id, boolean passed, String opinion, Long operatorId);
    Result<Deliverable> sign(Long id, Long signerId);
    Result<Deliverable> publish(Long id, Long operatorId);
    Result<Deliverable> archive(Long id, Long operatorId);
    Result<DeliverableVersion> revise(Long id, String newFilePath, String changeLog, Long operatorId);
    Result<List<Deliverable>> validateMandatoryDeliverables(Long phaseId);
}

// Story 6
public interface IApprovalRecordService {
    Result<ApprovalDetailVO> getDetail(Long recordId, Long userId);
    Result<?> approve(Long recordId, String opinion, Long approverId);
    Result<?> reject(Long recordId, String opinion, Long approverId);
    Result<?> resubmit(Long recordId, Long submitterId);
    Page<ApprovalRecord> listMyPending(Long approverId, Pageable page);
}
```

### 4.4 跨模块事件清单

| 事件 | 发布者 | 监听者 |
|---|---|---|
| `ProjectCreatedEvent` | ProjectTemplateService | ProjectMemberService（初始化成员） |
| `PhaseAdvanceEvent` | ProjectPhaseService | ApprovalDispatcher、TaskActivityService |
| `TaskProgressChangedEvent` | ImplTaskService | TaskRollupService（异步汇总） |
| `TaskSubmittedEvent` | ImplTaskService | ApprovalDispatcher（若开启任务审批） |
| `DeliverableSubmitEvent` | DeliverableService | ApprovalDispatcher（必审批） |
| `BaselineChangeEvent` | BaselineService | ApprovalDispatcher（必审批） |
| `ApprovalCompletedEvent` | ApprovalCenterService | 各业务 Service（回写状态） |

### 4.5 事务边界与一致性策略

| 场景 | 事务策略 |
|---|---|
| 模板创建项目 | 单事务，深拷贝所有相关表，失败整体回滚 |
| 阶段推进 + 审批创建 | 单事务：阶段状态流转 + 创建 ApprovalRecord + 启动 Flowable |
| 任务完成 + 强制检查项校验 | 单事务：状态流转 + 活动记录 |
| 任务进度汇总（异步） | **独立事务**，每层祖先单独提交，避免长事务 |
| 交付件修订（新建版本） | 单事务：新建 DeliverableVersion + 更新 Deliverable |
| 基线变更审批 | 两阶段：1) 创建审批（事务A） 2) 审批通过后应用变更（事务B） |
| 主子项目进度汇总 | 独立事务，递归 CTE 查询 + 单点更新主项目 |

### 4.6 权限模型

#### Shiro 权限码

```
project:template:list         查询模板列表
project:template:add          新增模板
project:template:publish      发布模板版本
project:template:use          从模板创建项目
project:create                创建项目
project:edit                  编辑项目
project:phase:advance         推进阶段
project:close                 关闭项目
project:subproject:manage     管理子项目
project:task:add              新增任务
project:task:edit             编辑任务
project:task:complete         完成任务
project:task:approve          验收任务
project:task:move             移动任务
project:deliverable:add       新增交付件
project:deliverable:submit    提交交付件
project:deliverable:review    审核交付件
project:deliverable:sign      签核交付件
project:deliverable:publish   发布交付件
project:deliverable:archive   归档交付件
project:baseline:save         保存基线
project:baseline:change       申请基线变更
workflow:approval:handle      处理审批
workflow:approval:config      配置审批规则
workflow:approval:field:perm  配置敏感字段权限
```

#### 项目成员角色

| 角色 | 权限范围 |
|---|---|
| PROJECT_MANAGER | 全部项目操作 + 审批提交 |
| PROJECT_MEMBER | 任务执行 + 交付件提交 |
| APPROVER | 审批处理（按 ApprovalFieldPermission 控制可见字段） |
| VIEWER | 只读 |
| CUSTOMER | 客户方只读（可选） |

---

## 5. API 设计与接口契约

### 5.1 API 路由总览

```
/api/project/template/...              项目模板
/api/project/...                       项目生命周期（扩展已有）
/api/project/phase/...                 阶段管理
/api/project/member/...                项目成员
/api/project/config/...                项目配置
/api/implementation/task/...           任务管理（扩展已有）
/api/implementation/task/checklist/... 检查项
/api/implementation/task/dependency/... 任务依赖
/api/implementation/task/comment/...   任务评论
/api/implementation/task/activity/...  活动记录
/api/deliverable/...                   交付件
/api/baseline/...                      计划基线
/api/workflow/approval/...             统一审批中心
/api/workflow/approval/field-perm/...  敏感字段权限
```

### 5.2 项目模板 API（Story 1）

| Method | Path | 描述 | 权限 |
|---|---|---|---|
| GET | `/api/project/template/list` | 分页查询模板 | `project:template:list` |
| GET | `/api/project/template/{id}` | 模板详情 | `project:template:list` |
| POST | `/api/project/template` | 新建模板 | `project:template:add` |
| PUT | `/api/project/template` | 更新模板 | `project:template:add` |
| DELETE | `/api/project/template/{id}` | 删除模板（DRAFT 状态） | `project:template:add` |
| GET | `/api/project/template/{id}/versions` | 模板版本列表 | `project:template:list` |
| POST | `/api/project/template/{id}/publish` | 发布新版本 | `project:template:publish` |
| GET | `/api/project/template/{id}/published-version` | 获取已发布版本 | `project:template:list` |
| POST | `/api/project/template/create-project` | **从模板创建项目** | `project:template:use` |

#### Story 1 验收 1：从模板创建项目

请求体 `ProjectCreateFromTemplateDTO`：

```json
{
  "templateId": 1,
  "versionId": 3,
  "projectCode": "IMPL-2026-001",
  "projectName": "XX 客户网络设备实施",
  "customerName": "XX 省电信",
  "contractNo": "HT-2026-001",
  "contractAmount": 500000.00,
  "planStartDate": "2026-07-01",
  "planEndDate": "2026-12-31",
  "projectManagerId": 100,
  "projectObjective": "完成全省网络设备升级",
  "projectScope": "全省 10 个地市分公司",
  "members": [
    {"userId": 101, "role": "PROJECT_MEMBER"}
  ],
  "configOverrides": {
    "approval.timeout.hours": "72"
  }
}
```

响应（含完整默认计划）：

```json
{
  "code": 200,
  "data": {
    "id": 1001,
    "projectCode": "IMPL-2026-001",
    "projectName": "XX 客户网络设备实施",
    "templateId": 5,
    "templateVersion": "v1.2.0",
    "status": "PLANNING",
    "phases": [...],
    "tasks": [...],
    "milestones": [...],
    "deliverables": [...],
    "approvalPlans": [...]
  }
}
```

### 5.3 项目生命周期 API（Story 2）

| Method | Path | 描述 | 权限 |
|---|---|---|---|
| POST | `/api/project` | 创建项目（无模板） | `project:create` |
| PUT | `/api/project` | 更新项目 | `project:edit` |
| GET | `/api/project/{id}/tree` | **主子项目树** | - |
| POST | `/api/project/{id}/subproject` | 创建子项目 | `project:subproject:manage` |
| POST | `/api/project/{id}/close` | **关闭主项目**（含子项目校验） | `project:close` |
| POST | `/api/project/{id}/cancel` | 取消项目 | `project:close` |
| GET | `/api/project/{id}/progress` | 项目进度汇总（含子项目） | - |
| POST | `/api/project/phase/{phaseId}/advance` | **推进阶段** | `project:phase:advance` |

#### Story 2 验收 1：阶段推进被阻止

```json
{
  "code": 200,
  "data": {
    "success": false,
    "errorCode": "PHASE_EXIT_GATE_FAILED",
    "errorMessage": "当前阶段退出条件未满足",
    "violations": [
      {
        "gateType": "DELIVERABLE",
        "message": "必需交付件未批准",
        "businessId": 2001,
        "businessName": "实施方案",
        "expectedStatus": "PUBLISHED",
        "actualStatus": "REVIEWED"
      }
    ]
  }
}
```

#### Story 2 验收 2：关闭主项目被拒绝

```json
{
  "code": 200,
  "data": {
    "success": false,
    "errorCode": "SUBPROJECT_NOT_CLOSED",
    "errorMessage": "子项目未全部关闭",
    "uncompletedSubProjects": [
      {"id": 1002, "projectName": "XX 省北部子项目", "status": "EXECUTING"}
    ]
  }
}
```

### 5.4 任务管理 API（Story 3）

| Method | Path | 描述 | 权限 |
|---|---|---|---|
| GET | `/api/implementation/task/list` | 分页查询任务 | - |
| GET | `/api/implementation/task/{id}/subtree` | 查询任务子树 | - |
| POST | `/api/implementation/task/{id}/move` | 移动任务（变更父任务） | `project:task:move` |
| POST | `/api/implementation/task/{id}/submit-review` | **提交评审（含强制检查项校验）** | `project:task:complete` |
| POST | `/api/implementation/task/{id}/approve` | 验收任务 | `project:task:approve` |
| GET | `/api/implementation/task/{id}/progress` | 任务进度（含子任务汇总） | - |

#### 检查项 API

| Method | Path | 描述 |
|---|---|---|
| GET | `/api/implementation/task/checklist/{taskId}` | 任务检查项列表 |
| POST | `/api/implementation/task/checklist` | 新增检查项 |
| PUT | `/api/implementation/task/checklist` | 更新检查项 |
| POST | `/api/implementation/task/checklist/{id}/check` | 勾选/取消勾选 |
| DELETE | `/api/implementation/task/checklist/{id}` | 删除检查项 |

#### Story 3 验收 1：强制检查项拦截

```json
{
  "code": 200,
  "data": {
    "success": false,
    "errorCode": "TASK_CHECKLIST_REQUIRED",
    "errorMessage": "存在未完成的强制检查项",
    "uncheckedMandatoryItems": [
      {"id": 9001, "title": "现场照片上传", "mandatory": true, "checked": false},
      {"id": 9002, "title": "客户签字确认", "mandatory": true, "checked": false}
    ],
    "taskStatus": "IN_PROGRESS"
  }
}
```

#### Story 3 验收 2：进度汇总

```json
{
  "code": 200,
  "data": {
    "taskId": 8001,
    "taskName": "项目启动会",
    "selfProgress": 0,
    "rolledUpProgress": 75,
    "totalSubtasks": 2,
    "completedSubtasks": 1,
    "children": [...]
  }
}
```

### 5.5 依赖与基线 API（Story 4）

| Method | Path | 描述 | 权限 |
|---|---|---|---|
| POST | `/api/implementation/task/dependency` | **保存依赖（含循环检测）** | `project:baseline:save` |
| DELETE | `/api/implementation/task/dependency/{id}` | 删除依赖 | `project:baseline:save` |
| GET | `/api/baseline/list` | 项目基线列表 | - |
| POST | `/api/baseline/save` | 保存基线 | `project:baseline:save` |
| POST | `/api/baseline/{id}/request-change` | **申请基线变更** | `project:baseline:change` |
| GET | `/api/baseline/diff` | **偏差分析** | - |

#### Story 4 验收 1：循环依赖被拒绝

```json
{
  "code": 200,
  "data": {
    "success": false,
    "errorCode": "CYCLE_DETECTED",
    "errorMessage": "形成循环依赖，闭环路径: 任务A → 任务B → 任务C → 任务A",
    "cyclePath": [
      {"taskId": 101, "taskName": "任务A"},
      {"taskId": 102, "taskName": "任务B"},
      {"taskId": 103, "taskName": "任务C"},
      {"taskId": 101, "taskName": "任务A"}
    ]
  }
}
```

#### Story 4 验收 2：基线偏差分析

```json
{
  "code": 200,
  "data": {
    "baseline": {
      "id": 7001,
      "baselineName": "初始基线",
      "status": "APPROVED",
      "approvedAt": "2026-07-01T10:00:00"
    },
    "diffs": [
      {
        "taskId": 8005,
        "taskName": "设备安装",
        "baselineStart": "2026-07-10",
        "currentStart": "2026-07-15",
        "startVariance": 5,
        "baselineEnd": "2026-07-20",
        "currentEnd": "2026-07-25",
        "endVariance": 5,
        "percentVariance": 25.0
      }
    ],
    "totalVarianced": 1,
    "needsApproval": true,
    "approvalReason": "偏差超过阈值（5 天 / 10%）"
  }
}
```

### 5.6 交付件 API（Story 5）

| Method | Path | 描述 | 权限 |
|---|---|---|---|
| POST | `/api/deliverable` | 新建交付件 | `project:deliverable:add` |
| POST | `/api/deliverable/{id}/submit` | **提交**（DRAFT → SUBMITTED） | `project:deliverable:submit` |
| POST | `/api/deliverable/{id}/review` | **审核**（SUBMITTED → REVIEWED/DRAFT） | `project:deliverable:review` |
| POST | `/api/deliverable/{id}/sign` | **签核**（REVIEWED → SIGNED） | `project:deliverable:sign` |
| POST | `/api/deliverable/{id}/publish` | **发布**（SIGNED → PUBLISHED） | `project:deliverable:publish` |
| POST | `/api/deliverable/{id}/archive` | **归档**（REFERENCED → ARCHIVED） | `project:deliverable:archive` |
| POST | `/api/deliverable/{id}/revise` | **修订（新建版本）** | `project:deliverable:add` |
| GET | `/api/deliverable/{id}/versions` | 版本历史 | - |
| GET | `/api/deliverable/phase/{phaseId}/validate` | **阶段必需交付件校验** | - |

#### Story 5 验收 1：修订创建新版本

```json
{
  "code": 200,
  "data": {
    "id": 12002,
    "deliverableId": 2001,
    "versionNo": 2,
    "filePath": "/files/impl-plan-v2.docx",
    "fileChecksum": "sha256:abc123...",
    "uploadedBy": 100,
    "uploadedAt": "2026-07-17T15:30:00",
    "changeLog": "增加备件清单章节",
    "status": "DRAFT"
  }
}
```

**说明**：v1 版本记录保留不变，`Deliverable.currentVersion` 更新为 2，`status` 重置为 DRAFT。

#### Story 5 验收 2：阶段必需交付件校验

```json
{
  "code": 200,
  "data": {
    "allApproved": false,
    "items": [
      {
        "deliverableId": 2001,
        "deliverableName": "实施方案",
        "mandatory": true,
        "expectedStatus": "PUBLISHED",
        "actualStatus": "REVIEWED",
        "approved": false
      }
    ]
  }
}
```

### 5.7 统一审批中心 API（Story 6）

| Method | Path | 描述 | 权限 |
|---|---|---|---|
| GET | `/api/workflow/approval/pending` | **我的待办** | `workflow:approval:handle` |
| GET | `/api/workflow/approval/submitted` | **我提交的** | - |
| GET | `/api/workflow/approval/project/{projectId}` | 项目维度审批列表 | - |
| GET | `/api/workflow/approval/{id}` | **审批详情（含字段脱敏）** | `workflow:approval:handle` |
| GET | `/api/workflow/approval/{id}/history` | **审批历史（含所有轮次）** | - |
| POST | `/api/workflow/approval/{id}/approve` | 通过 | `workflow:approval:handle` |
| POST | `/api/workflow/approval/{id}/reject` | 退回 | `workflow:approval:handle` |
| POST | `/api/workflow/approval/{id}/withdraw` | 撤回 | - |
| POST | `/api/workflow/approval/{id}/resubmit` | **重新提交（保留历史）** | - |

#### Story 6 验收 1：审批详情含字段脱敏

```json
{
  "code": 200,
  "data": {
    "record": {
      "id": 9001,
      "approvalType": "DELIVERABLE",
      "title": "交付件审批：实施方案",
      "status": "PENDING",
      "round": 1,
      "currentNodeName": "技术审核"
    },
    "businessData": {
      "deliverableId": 2001,
      "deliverableName": "实施方案",
      "contractAmount": "12***.00",
      "customerContact": "138****5678"
    },
    "maskedFields": [
      {"fieldName": "contractAmount", "permission": "MASKED", "maskedValue": "12***.00", "maskPattern": "amount-mask"},
      {"fieldName": "customerContact", "permission": "MASKED", "maskedValue": "138****5678", "maskPattern": "phone-mask"}
    ],
    "history": [...]
  }
}
```

#### Story 6 验收 2：审批历史保留（含多轮记录）

```json
{
  "code": 200,
  "data": [
    {
      "id": 1, "recordId": 9001, "round": 1, "nodeName": "技术审核",
      "operatorId": 100, "operatorName": "张工", "action": "REJECT",
      "opinion": "方案需补充备件清单", "operatedAt": "2026-07-15T10:00:00"
    },
    {
      "id": 2, "recordId": 9001, "round": 1, "nodeName": "提交人",
      "operatorId": 200, "operatorName": "李经理", "action": "RESUBMIT",
      "opinion": "已补充备件清单", "operatedAt": "2026-07-16T09:00:00"
    },
    {
      "id": 3, "recordId": 9001, "round": 2, "nodeName": "技术审核",
      "operatorId": 100, "operatorName": "张工", "action": "APPROVE",
      "opinion": "通过", "operatedAt": "2026-07-17T14:00:00"
    }
  ]
}
```

### 5.8 统一响应格式与错误码

#### 响应格式（沿用现有 `Result<T>`）

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... }
}
```

#### 错误码定义

| 错误码 | HTTP | 含义 |
|---|---|---|
| `PROJECT_CODE_DUPLICATE` | 200 | 项目编码重复 |
| `TEMPLATE_VERSION_NOT_FOUND` | 200 | 模板版本不存在 |
| `PHASE_EXIT_GATE_FAILED` | 200 | 阶段退出条件未满足 |
| `SUBPROJECT_NOT_CLOSED` | 200 | 子项目未全部关闭 |
| `TASK_CHECKLIST_REQUIRED` | 200 | 强制检查项未完成 |
| `CYCLE_DETECTED` | 200 | 形成循环依赖 |
| `NO_BASELINE` | 200 | 项目无活跃基线 |
| `BASELINE_VARIANCE_EXCEEDED` | 200 | 基线偏差超阈值 |
| `DELIVERABLE_STATUS_INVALID` | 200 | 交付件状态流转非法 |
| `DELIVERABLE_VERSION_EXISTS` | 200 | 交付件版本已存在 |
| `MANDATORY_DELIVERABLE_NOT_APPROVED` | 200 | 必需交付件未批准 |
| `APPROVAL_TIMEOUT` | 200 | 审批超时 |
| `APPROVAL_NO_PERMISSION` | 200 | 无审批权限 |
| `APPROVAL_FIELD_NO_PERMISSION` | 200 | 无字段访问权限 |
| `APPROVAL_ROUND_LIMIT_EXCEEDED` | 200 | 审批轮次超限（默认 5 轮） |

### 5.9 分页规范

统一使用 MyBatis Plus `Page<T>` 序列化结构：

```json
{
  "records": [...],
  "total": 100,
  "size": 10,
  "current": 1,
  "pages": 10
}
```

查询参数：`page`（从 1 开始）、`size`（默认 10，最大 100）。

---

## 6. 数据库迁移脚本与 Flyway 策略

### 6.1 迁移脚本规划

当前数据库已应用至 **V63**。本设计新增 **V64 ~ V72** 共 9 个迁移脚本：

| 版本 | 文件名 | 内容 | 关联故事 |
|---|---|---|---|
| V64 | `V64__create_project_template_tables.sql` | 项目模板 + 模板版本 | Story 1 |
| V65 | `V65__alter_project_for_subproject.sql` | Project 扩展（主子项目、模板关联、阶段关联） | Story 1, 2 |
| V66 | `V66__create_project_phase_member_config.sql` | Phase + Member + Config + 默认配置 | Story 1, 2 |
| V67 | `V67__alter_task_for_hierarchy.sql` | 任务层级 + 检查项 + 评论 + 活动 | Story 3 |
| V68 | `V68__create_task_dependency.sql` | 任务依赖 + milestone ALTER | Story 4 |
| V69 | `V69__create_baseline_snapshot.sql` | 基线快照表 | Story 4 |
| V70 | `V70__create_deliverable_full_lifecycle.sql` | 交付件扩展 + 版本 + 签名 + 引用 | Story 5 |
| V71 | `V71__create_unified_approval_center.sql` | 审批记录 + 节点 + 字段权限 + 历史 | Story 6 |
| V72 | `V72__seed_demo_and_permissions.sql` | 演示数据 + 权限菜单 + 字典数据 | 全部 |

**迁移脚本路径**：`/workspace/network-equipment-pms/pms-admin/src/main/resources/db/migration/`

### 6.2 V64：项目模板表

```sql
-- V64__create_project_template_tables.sql
-- 项目模板与版本管理（Story 1）

CREATE TABLE pms_project_template (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    template_code   VARCHAR(64)  NOT NULL COMMENT '模板编码',
    template_name   VARCHAR(128) NOT NULL COMMENT '模板名称',
    category        VARCHAR(32)  NOT NULL DEFAULT 'IMPLEMENT' COMMENT '类别：IMPLEMENT/MAINTENANCE/CONSULTING',
    description     VARCHAR(500) COMMENT '描述',
    status          VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT/PUBLISHED/DEPRECATED',
    create_by       BIGINT       COMMENT '创建人',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by       BIGINT       COMMENT '更新人',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT(1)   NOT NULL DEFAULT 0,
    version         INT          NOT NULL DEFAULT 0 COMMENT '乐观锁',
    PRIMARY KEY (id),
    UNIQUE KEY uk_template_code (template_code),
    KEY idx_status_category (status, category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目模板';

CREATE TABLE pms_project_template_version (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    template_id     BIGINT       NOT NULL COMMENT '模板ID',
    version         VARCHAR(32)  NOT NULL COMMENT '语义化版本 v1.0.0',
    snapshot_json   JSON         NOT NULL COMMENT '模板内容快照JSON（phases/tasks/milestones/deliverables/dependencies/approvalPlans/assigneeRules）',
    change_log      VARCHAR(500) COMMENT '版本变更说明',
    status          VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/PUBLISHED/ARCHIVED',
    published_at    DATETIME     COMMENT '发布时间',
    published_by    BIGINT       COMMENT '发布人',
    create_by       BIGINT,
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by       BIGINT,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT(1)   NOT NULL DEFAULT 0,
    version_lock    INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_template_version (template_id, version),
    KEY idx_template_status (template_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目模板版本';
```

### 6.3 V65：Project 表扩展

```sql
-- V65__alter_project_for_subproject.sql
ALTER TABLE pms_project
    ADD COLUMN parent_project_id  BIGINT       NULL COMMENT '父项目ID（NULL=顶层）',
    ADD COLUMN project_path       VARCHAR(500) NOT NULL DEFAULT '/' COMMENT '物化路径 /1/5/',
    ADD COLUMN depth              INT          NOT NULL DEFAULT 0 COMMENT '深度（0=顶层）',
    ADD COLUMN weight             DECIMAL(5,2) NOT NULL DEFAULT 1.00 COMMENT '汇总权重',
    ADD COLUMN template_id        BIGINT       NULL COMMENT '来源模板ID',
    ADD COLUMN template_version   VARCHAR(32)  NULL COMMENT '模板版本快照',
    ADD COLUMN current_phase_id   BIGINT       NULL COMMENT '当前阶段ID',
    ADD COLUMN project_objective  VARCHAR(500) NULL COMMENT '项目目标',
    ADD COLUMN project_scope      VARCHAR(1000) NULL COMMENT '项目范围';

CREATE INDEX idx_project_path ON pms_project (project_path);
CREATE INDEX idx_parent_project_id ON pms_project (parent_project_id);
CREATE INDEX idx_template_id ON pms_project (template_id);

-- 回填存量项目路径
UPDATE pms_project SET project_path = CONCAT('/', id, '/') WHERE project_path = '/' OR project_path IS NULL;
```

### 6.4 V66：阶段、成员、配置表

```sql
-- V66__create_project_phase_member_config.sql
CREATE TABLE pms_project_phase (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    project_id          BIGINT       NOT NULL COMMENT '项目ID',
    template_phase_id   BIGINT       NULL COMMENT '模板阶段ID（追溯）',
    phase_name          VARCHAR(64)  NOT NULL,
    phase_code          VARCHAR(32)  NOT NULL COMMENT 'PREPARE/PLAN/DESIGN/IMPLEMENT/OPERATE 或自定义',
    sort_order          INT          NOT NULL DEFAULT 0,
    entry_criteria      JSON         NULL COMMENT '进入条件（结构化 JSON）',
    exit_criteria       JSON         NULL COMMENT '退出条件 JSON：{requiredDeliverables,requiredTasks,requiredMilestones,requiredApprovals}',
    status              VARCHAR(20)  NOT NULL DEFAULT 'NOT_STARTED' COMMENT 'NOT_STARTED/IN_PROGRESS/COMPLETED/SKIPPED',
    planned_start_date  DATE         NULL,
    planned_end_date    DATE         NULL,
    actual_start_date   DATE         NULL,
    actual_end_date     DATE         NULL,
    create_by           BIGINT,
    create_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by           BIGINT,
    update_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted             TINYINT(1)   NOT NULL DEFAULT 0,
    version             INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_project_phase_code (project_id, phase_code),
    KEY idx_project_sort (project_id, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目阶段';

CREATE TABLE pms_project_member (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    project_id      BIGINT       NOT NULL,
    user_id         BIGINT       NOT NULL,
    user_name       VARCHAR(64)  NULL COMMENT '冗余',
    role            VARCHAR(32)  NOT NULL DEFAULT 'PROJECT_MEMBER' COMMENT 'PROJECT_MANAGER/PROJECT_MEMBER/APPROVER/VIEWER/CUSTOMER',
    join_date       DATE         NULL,
    leave_date      DATE         NULL,
    create_by       BIGINT,
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by       BIGINT,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT(1)   NOT NULL DEFAULT 0,
    version         INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_project_user (project_id, user_id),
    KEY idx_user_role (user_id, role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目成员';

CREATE TABLE pms_project_config (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    project_id      BIGINT       NULL COMMENT 'NULL=系统级默认',
    template_id     BIGINT       NULL COMMENT 'NULL=非模板配置',
    config_key      VARCHAR(100) NOT NULL,
    config_value    VARCHAR(500) NOT NULL,
    description     VARCHAR(255) NULL,
    create_by       BIGINT,
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by       BIGINT,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT(1)   NOT NULL DEFAULT 0,
    version         INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_proj_tpl_key (project_id, template_id, config_key),
    KEY idx_template_key (template_id, config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目配置';

-- 初始化系统默认配置
INSERT INTO pms_project_config (project_id, template_id, config_key, config_value, description) VALUES
(NULL, NULL, 'baseline.variance.days.threshold', '5', '基线偏差天数阈值'),
(NULL, NULL, 'baseline.variance.percent.threshold', '10', '基线偏差百分比阈值'),
(NULL, NULL, 'approval.timeout.hours', '48', '审批超时小时数'),
(NULL, NULL, 'approval.escalate.hours', '24', '审批升级小时数'),
(NULL, NULL, 'approval.reminder.hours', '12', '审批提醒小时数'),
(NULL, NULL, 'approval.timeout.action', 'ESCALATE', '超时动作：ESCALATE/AUTO_APPROVE/AUTO_REJECT'),
(NULL, NULL, 'task.rollup.weight.field', 'PLANNED_HOURS', '任务汇总权重字段'),
(NULL, NULL, 'phase.exit.check.approval', 'true', '阶段退出是否强制审批'),
(NULL, NULL, 'approval.max.rounds', '5', '审批最大轮次');
```

### 6.5 V67：任务层级、检查项、评论、活动

```sql
-- V67__alter_task_for_hierarchy.sql
ALTER TABLE pms_impl_task
    ADD COLUMN parent_task_id   BIGINT        NULL COMMENT '父任务ID',
    ADD COLUMN task_path        VARCHAR(500)  NOT NULL DEFAULT '/' COMMENT '物化路径 /12/45/78/',
    ADD COLUMN depth            INT           NOT NULL DEFAULT 0,
    ADD COLUMN priority         VARCHAR(20)   NOT NULL DEFAULT 'MEDIUM' COMMENT 'LOW/MEDIUM/HIGH/CRITICAL',
    ADD COLUMN actual_hours     DECIMAL(8,2)  NULL COMMENT '实际工时',
    ADD COLUMN remaining_hours  DECIMAL(8,2)  NULL COMMENT '剩余工时',
    ADD COLUMN phase_id         BIGINT        NULL COMMENT '关联阶段ID',
    ADD COLUMN task_weight      DECIMAL(5,2)  NOT NULL DEFAULT 1.00 COMMENT '自定义汇总权重';

CREATE INDEX idx_parent_task_id ON pms_impl_task (parent_task_id);
CREATE INDEX idx_task_path ON pms_impl_task (task_path);
CREATE INDEX idx_project_phase ON pms_impl_task (project_id, phase_id);

-- 回填存量任务路径
UPDATE pms_impl_task SET task_path = CONCAT('/', id, '/') WHERE task_path = '/' OR task_path IS NULL;

CREATE TABLE pms_task_checklist (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    task_id         BIGINT       NOT NULL,
    title           VARCHAR(128) NOT NULL,
    description     VARCHAR(500) NULL,
    mandatory       TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '强制检查项',
    checked         TINYINT(1)   NOT NULL DEFAULT 0,
    checked_by      BIGINT       NULL,
    checked_at      DATETIME     NULL,
    sort_order      INT          NOT NULL DEFAULT 0,
    create_by       BIGINT,
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by       BIGINT,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT(1)   NOT NULL DEFAULT 0,
    version         INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_task_mandatory (task_id, mandatory)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务检查项';

CREATE TABLE pms_task_comment (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    task_id           BIGINT       NOT NULL,
    parent_comment_id BIGINT       NULL COMMENT '评论回复（二级）',
    content           TEXT         NOT NULL,
    author_id         BIGINT       NOT NULL,
    author_name       VARCHAR(64)  NULL,
    create_time       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted           TINYINT(1)   NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_task_parent (task_id, parent_comment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务评论';

CREATE TABLE pms_task_activity (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    task_id         BIGINT       NOT NULL,
    activity_type   VARCHAR(50)  NOT NULL COMMENT 'CREATE/UPDATE/STATUS_CHANGE/SUBMIT_REVIEW/APPROVE/REJECT/CHECKLIST_CHECK/COMMENT/PROGRESS_CHANGE/ASSIGN/MOVE',
    description     VARCHAR(500) NULL,
    old_value       VARCHAR(500) NULL,
    new_value       VARCHAR(500) NULL,
    operator_id     BIGINT       NOT NULL,
    operator_name   VARCHAR(64)  NULL,
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_task_type_time (task_id, activity_type, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务活动记录';
```

### 6.6 V68：任务依赖表

```sql
-- V68__create_task_dependency.sql
CREATE TABLE pms_task_dependency (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    project_id          BIGINT       NOT NULL,
    predecessor_task_id BIGINT       NOT NULL COMMENT '前置任务',
    successor_task_id   BIGINT       NOT NULL COMMENT '后续任务',
    dependency_type     VARCHAR(4)   NOT NULL DEFAULT 'FS' COMMENT 'FS/FF/SS/SF',
    lag_days            INT          NOT NULL DEFAULT 0 COMMENT '滞后天数（可负）',
    create_by           BIGINT,
    create_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by           BIGINT,
    update_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted             TINYINT(1)   NOT NULL DEFAULT 0,
    version             INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pred_succ_type (predecessor_task_id, successor_task_id, dependency_type),
    KEY idx_successor (successor_task_id),
    KEY idx_predecessor (predecessor_task_id),
    KEY idx_project (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务依赖';

-- milestone 表已在 V2 创建、V10 扩展（milestone_type 字段已存在），此处仅补充 phase_id 关联字段
ALTER TABLE pms_milestone
    ADD COLUMN phase_id BIGINT NULL COMMENT '关联阶段ID' AFTER project_id,
    ADD INDEX idx_project_phase (project_id, phase_id);
```

### 6.7 V69：基线快照表

```sql
-- V69__create_baseline_snapshot.sql
CREATE TABLE pms_baseline_snapshot (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    project_id          BIGINT       NOT NULL,
    baseline_name       VARCHAR(128) NOT NULL COMMENT '基线名称',
    status              VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/APPROVED/SUPERSEDED',
    snapshot_json       JSON         NOT NULL COMMENT '快照JSON：[{taskId,taskName,plannedStart,plannedEnd,duration,plannedHours}]',
    change_reason       VARCHAR(500) NULL COMMENT '变更原因（关联审批）',
    approval_record_id  BIGINT       NULL COMMENT '关联审批记录ID',
    approved_at         DATETIME     NULL,
    approved_by         BIGINT       NULL,
    create_by           BIGINT,
    create_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by           BIGINT,
    update_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted             TINYINT(1)   NOT NULL DEFAULT 0,
    version             INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_project_status (project_id, status, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计划基线快照';

-- BaselineHistory 表已有，作为审计流水保留；新增 baseline_id 关联字段
ALTER TABLE pms_baseline_history
    ADD COLUMN baseline_snapshot_id BIGINT NULL COMMENT '关联基线快照ID' AFTER project_id;
```

### 6.8 V70：交付件全生命周期

```sql
-- V70__create_deliverable_full_lifecycle.sql
ALTER TABLE pms_deliverable
    ADD COLUMN phase_id          BIGINT       NULL COMMENT '所属阶段ID',
    ADD COLUMN current_version   INT          NOT NULL DEFAULT 1 COMMENT '当前版本号',
    ADD COLUMN mandatory         TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '必需交付件（影响阶段退出）',
    ADD COLUMN approver_role     VARCHAR(32)  NULL COMMENT '签核角色',
    ADD COLUMN published_at      DATETIME     NULL,
    ADD COLUMN archived_at       DATETIME     NULL;

-- 状态字段升级为 7 态：DRAFT/SUBMITTED/REVIEWED/SIGNED/PUBLISHED/REFERENCED/ARCHIVED
UPDATE pms_deliverable SET status = 'DRAFT' WHERE status = 'PENDING';
UPDATE pms_deliverable SET status = 'PUBLISHED' WHERE status = 'CONFIRMED';

ALTER TABLE pms_deliverable
    ADD INDEX idx_phase_mandatory (phase_id, mandatory),
    ADD INDEX idx_project_status (project_id, status);

CREATE TABLE pms_deliverable_version (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    deliverable_id  BIGINT       NOT NULL,
    version_no      INT          NOT NULL COMMENT '版本号 1,2,3...',
    file_path       VARCHAR(500) NOT NULL,
    file_checksum   VARCHAR(128) NULL COMMENT 'SHA256 校验和',
    uploaded_by     BIGINT       NOT NULL,
    uploaded_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    change_log      VARCHAR(500) NULL COMMENT '版本说明',
    status          VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT '该版本流转状态',
    create_by       BIGINT,
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by       BIGINT,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT(1)   NOT NULL DEFAULT 0,
    version_lock    INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_deliverable_version (deliverable_id, version_no),
    KEY idx_deliverable (deliverable_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交付件版本';

CREATE TABLE pms_deliverable_signature (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    deliverable_id  BIGINT       NOT NULL,
    version_no      INT          NOT NULL COMMENT '签名对应的版本',
    signer_id       BIGINT       NOT NULL,
    signer_name     VARCHAR(64)  NULL,
    signer_role     VARCHAR(32)  NULL COMMENT '签核角色',
    signature_type  VARCHAR(20)  NOT NULL DEFAULT 'ELECTRONIC' COMMENT 'ELECTRONIC/STAMP/DIGITAL',
    signature_data  VARCHAR(500) NULL COMMENT '签名数据（证书指纹/印章图URL）',
    signed_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_deliverable_version (deliverable_id, version_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交付件签名';

CREATE TABLE pms_deliverable_reference (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    deliverable_id      BIGINT       NOT NULL COMMENT '被引用的交付件',
    deliverable_version INT          NULL COMMENT '引用的具体版本（NULL=最新）',
    referenced_by_type  VARCHAR(32)  NOT NULL COMMENT '引用方业务类型：TASK/PHASE/PROJECT/DELIVERABLE/REPORT',
    referenced_by_id    BIGINT       NOT NULL COMMENT '引用方业务ID',
    referenced_by_name  VARCHAR(128) NULL COMMENT '冗余',
    created_by          BIGINT       NOT NULL,
    create_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_deliverable (deliverable_id),
    KEY idx_ref_by (referenced_by_type, referenced_by_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交付件引用关系';
```

### 6.9 V71：统一审批中心

```sql
-- V71__create_unified_approval_center.sql
CREATE TABLE pms_approval_record (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    approval_type       VARCHAR(32)  NOT NULL COMMENT 'PROJECT/TASK/DELIVERABLE/RISK/ISSUE/CHANGE/RESOURCE/COST/PHASE_EXIT/BASELINE_CHANGE',
    business_id         BIGINT       NOT NULL COMMENT '业务对象ID',
    business_code       VARCHAR(64)  NULL COMMENT '业务编码冗余',
    project_id          BIGINT       NULL COMMENT '项目维度',
    process_instance_id VARCHAR(64)  NULL COMMENT 'Flowable流程实例ID',
    title               VARCHAR(200) NOT NULL,
    submitter_id        BIGINT       NOT NULL,
    submitter_name      VARCHAR(64)  NULL,
    current_node_id     VARCHAR(64)  NULL COMMENT '当前节点ID（Flowable）',
    current_node_name   VARCHAR(64)  NULL,
    status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED/WITHDRAWN/TIMEOUT',
    round               INT          NOT NULL DEFAULT 1 COMMENT '审批轮次',
    submitted_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at        DATETIME     NULL,
    timeout_at          DATETIME     NULL COMMENT '超时时间点',
    escalated           TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否已升级',
    create_by           BIGINT,
    create_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by           BIGINT,
    update_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted             TINYINT(1)   NOT NULL DEFAULT 0,
    version             INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_business_type_id (approval_type, business_id),
    KEY idx_project_status (project_id, status),
    KEY idx_submitter_status (submitter_id, status),
    KEY idx_status_timeout (status, timeout_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='统一审批记录';

CREATE TABLE pms_approval_node (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    record_id           BIGINT       NOT NULL,
    node_name           VARCHAR(64)  NOT NULL,
    node_order          INT          NOT NULL COMMENT '节点顺序',
    approver_id         BIGINT       NULL COMMENT '指定审批人',
    approver_role       VARCHAR(32)  NULL COMMENT '审批角色（多选一）',
    status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED',
    approver_actual_id  BIGINT       NULL COMMENT '实际处理人',
    opinion             VARCHAR(500) NULL,
    operated_at         DATETIME     NULL,
    timeout_at          DATETIME     NULL,
    PRIMARY KEY (id),
    KEY idx_record_order (record_id, node_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批节点';

CREATE TABLE pms_approval_history (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    record_id       BIGINT       NOT NULL,
    round           INT          NOT NULL,
    node_name       VARCHAR(64)  NOT NULL,
    operator_id     BIGINT       NOT NULL,
    operator_name   VARCHAR(64)  NULL,
    action          VARCHAR(20)  NOT NULL COMMENT 'SUBMIT/APPROVE/REJECT/WITHDRAW/RESUBMIT/ESCALATE/TIMEOUT',
    opinion         VARCHAR(500) NULL,
    operated_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_record_round_time (record_id, round, operated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批历史';

CREATE TABLE pms_approval_field_permission (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    approval_node_id BIGINT      NOT NULL COMMENT '关联审批节点（或节点模板）',
    entity_type     VARCHAR(128) NOT NULL COMMENT '业务实体类名',
    field_name      VARCHAR(64)  NOT NULL,
    permission      VARCHAR(20)  NOT NULL DEFAULT 'VISIBLE' COMMENT 'VISIBLE/MASKED/HIDDEN',
    mask_pattern    VARCHAR(64)  NULL COMMENT '脱敏规则：phone-mask/amount-mask/email-mask/custom',
    custom_pattern  VARCHAR(128) NULL COMMENT '自定义正则（当 mask_pattern=custom）',
    create_by       BIGINT,
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by       BIGINT,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT(1)   NOT NULL DEFAULT 0,
    version         INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_node_entity (approval_node_id, entity_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批敏感字段权限';
```

### 6.10 V72：演示数据与权限种子

```sql
-- V72__seed_demo_and_permissions.sql
-- 完整种子数据包括：
-- 1. 权限菜单（pms_menu + pms_permission）
-- 2. 字典数据（pms_dict_type + pms_dict_data，覆盖 9 个字典类型 50+ 项）
-- 3. 项目模板（3 个模板 + 5 个版本快照）
-- 4. 项目与主子项目（1 主项目 + 2 子项目，含物化路径）
-- 5. 项目阶段（6 个阶段，含退出条件 JSON）
-- 6. 项目成员（5 个成员，3 种角色）
-- 7. 任务与检查项（5 个任务含父子层级 + 4 个检查项含强制标志）
-- 8. 任务依赖（3 条 FS 依赖关系）
-- 9. 交付件与版本（3 个交付件 + 2 个版本记录 + 1 个签名）
-- 10. 基线快照（1 个 APPROVED 基线，含 5 个任务计划）
-- 11. 统一审批记录（2 个 PENDING 审批 + 历史记录）
-- 12. 敏感字段权限配置（5 条规则：金额/电话脱敏，文件路径隐藏）
-- 完整 INSERT 语句示例（项目模板/项目/阶段/任务/检查项/依赖/交付件/基线/审批/字典/权限菜单/字段权限）：
INSERT INTO pms_project_template (id, template_code, template_name, category, description, status, create_by, create_time) VALUES
(1, 'TPL-IMPL-STD', '标准网络设备实施模板', 'IMPLEMENT', '5 阶段标准实施流程', 'PUBLISHED', 1, NOW()),
(2, 'TPL-IMPL-FAST', '快速实施模板', 'IMPLEMENT', '3 阶段精简流程', 'PUBLISHED', 1, NOW()),
(3, 'TPL-MAINT-STD', '标准维护服务模板', 'MAINTENANCE', '4 阶段维护流程', 'PUBLISHED', 1, NOW());

INSERT INTO pms_project (id, project_code, project_name, project_type, status, customer_name, contract_no,
  contract_amount, plan_start_date, plan_end_date, project_manager_id, project_manager_name,
  parent_project_id, project_path, depth, weight, template_id, template_version, current_phase_id,
  project_objective, project_scope, create_time) VALUES
(1001, 'IMPL-2026-001', 'XX 省网络设备实施主项目', 'IMPLEMENT', 'EXECUTING', 'XX 省电信', 'HT-2026-001',
  500000.00, '2026-07-01', '2026-12-31', 100, '张经理', NULL, '/1001/', 0, 1.00, 1, 'v1.2.0', 5001,
  '完成全省网络设备升级', '全省 10 个地市分公司', NOW()),
(1002, 'IMPL-2026-001-N', 'XX 省北部子项目', 'IMPLEMENT', 'EXECUTING', 'XX 省电信', 'HT-2026-001',
  200000.00, '2026-07-01', '2026-11-30', 101, '李经理', 1001, '/1001/1002/', 1, 0.40, 1, 'v1.2.0', 5005,
  '北部 4 市实施', '北部 4 个地市', NOW()),
(1003, 'IMPL-2026-001-S', 'XX 省南部子项目', 'IMPLEMENT', 'EXECUTING', 'XX 省电信', 'HT-2026-001',
  300000.00, '2026-07-01', '2026-12-15', 102, '王经理', 1001, '/1001/1003/', 1, 0.60, 1, 'v1.2.0', 5008,
  '南部 6 市实施', '南部 6 个地市', NOW());

INSERT INTO pms_project_phase (id, project_id, template_phase_id, phase_name, phase_code, sort_order,
  entry_criteria, exit_criteria, status, planned_start_date, planned_end_date, create_time) VALUES
(5001, 1001, NULL, '准备阶段', 'PREPARE', 1, NULL,
  JSON_OBJECT('requiredDeliverables', JSON_ARRAY(JSON_OBJECT('deliverableName','实施方案','requiredStatus','PUBLISHED')),
              'requiredTasks', JSON_ARRAY(JSON_OBJECT('phaseId',5001,'allCompleted',true))),
  'COMPLETED', '2026-07-01', '2026-07-15', NOW()),
(5002, 1001, NULL, '规划阶段', 'PLAN', 2, NULL,
  JSON_OBJECT('requiredDeliverables', JSON_ARRAY(JSON_OBJECT('deliverableName','规划报告','requiredStatus','PUBLISHED'))),
  'IN_PROGRESS', '2026-07-16', '2026-08-15', NOW());

INSERT INTO pms_impl_task (id, project_id, task_name, task_type, plan_start_date, plan_end_date, status,
  progress, parent_task_id, task_path, depth, priority, planned_hours, phase_id, create_time) VALUES
(8001, 1001, '项目启动会', 'OEM', '2026-07-01', '2026-07-02', 'COMPLETED', 100, NULL, '/8001/', 0, 'HIGH', 4, 5001, NOW()),
(8002, 1001, '需求确认', 'OEM', '2026-07-03', '2026-07-07', 'COMPLETED', 100, 8001, '/8001/8002/', 1, 'HIGH', 16, 5001, NOW()),
(8003, 1001, '现场勘查', 'AGENT', '2026-07-08', '2026-07-12', 'IN_PROGRESS', 60, 8001, '/8001/8003/', 1, 'MEDIUM', 24, 5001, NOW()),
(8004, 1001, '方案编写', 'OEM', '2026-07-13', '2026-07-20', 'PENDING', 0, NULL, '/8004/', 0, 'HIGH', 40, 5002, NOW());

INSERT INTO pms_task_checklist (id, task_id, title, description, mandatory, checked, sort_order, create_time) VALUES
(9001, 8003, '现场照片上传', '上传至少 3 张现场环境照片', 1, 1, 1, NOW()),
(9002, 8003, '客户签字确认', '获取客户现场确认签字', 1, 0, 2, NOW());

INSERT INTO pms_task_dependency (id, project_id, predecessor_task_id, successor_task_id, dependency_type, lag_days, create_time) VALUES
(1, 1001, 8002, 8003, 'FS', 1, NOW()),
(2, 1001, 8003, 8004, 'FS', 1, NOW());

INSERT INTO pms_deliverable (id, project_id, deliverable_name, deliverable_type, file_path, status,
  phase_id, current_version, mandatory, approver_role, published_at, create_time) VALUES
(2001, 1001, '实施方案', 'DOCUMENT', '/files/impl-plan-v1.docx', 'PUBLISHED', 5001, 1, 1, 'TECH_LEAD', '2026-07-15 10:00:00', NOW()),
(2002, 1001, '现场勘查报告', 'REPORT', '/files/site-survey.docx', 'REVIEWED', 5001, 1, 1, 'PROJECT_MANAGER', NULL, NOW());

INSERT INTO pms_deliverable_version (id, deliverable_id, version_no, file_path, file_checksum, uploaded_by,
  uploaded_at, change_log, status, create_time) VALUES
(12001, 2001, 1, '/files/impl-plan-v1.docx', 'sha256:abc123', 101, '2026-07-10 10:00:00', '初始版本', 'PUBLISHED', NOW());

INSERT INTO pms_deliverable_signature (id, deliverable_id, version_no, signer_id, signer_name, signer_role,
  signature_type, signed_at, create_time) VALUES
(30001, 2001, 1, 200, '审批员A', 'TECH_LEAD', 'ELECTRONIC', '2026-07-14 15:00:00', NOW());

INSERT INTO pms_baseline_snapshot (id, project_id, baseline_name, status, snapshot_json, change_reason,
  approved_at, approved_by, create_time) VALUES
(7001, 1001, '初始基线', 'APPROVED',
  JSON_ARRAY(
    JSON_OBJECT('taskId',8001,'taskName','项目启动会','plannedStart','2026-07-01','plannedEnd','2026-07-02','duration',2,'plannedHours',4),
    JSON_OBJECT('taskId',8002,'taskName','需求确认','plannedStart','2026-07-03','plannedEnd','2026-07-07','duration',5,'plannedHours',16),
    JSON_OBJECT('taskId',8003,'taskName','现场勘查','plannedStart','2026-07-08','plannedEnd','2026-07-12','duration',5,'plannedHours',24),
    JSON_OBJECT('taskId',8004,'taskName','方案编写','plannedStart','2026-07-13','plannedEnd','2026-07-20','duration',8,'plannedHours',40)
  ),
  '项目启动基线', '2026-07-01 10:00:00', 100, NOW());

INSERT INTO pms_approval_record (id, approval_type, business_id, business_code, project_id, title,
  submitter_id, submitter_name, current_node_name, status, round, submitted_at, timeout_at, create_time) VALUES
(9001, 'DELIVERABLE', 2002, 'DLV-001', 1001, '交付件审批：现场勘查报告', 101, '李经理', '项目经理审核',
  'PENDING', 1, '2026-07-12 10:00:00', '2026-07-14 10:00:00', NOW());

INSERT INTO pms_approval_history (id, record_id, round, node_name, operator_id, operator_name, action, opinion, operated_at) VALUES
(1, 9001, 1, '提交人', 101, '李经理', 'SUBMIT', '请审核现场勘查报告', '2026-07-12 10:00:00');

INSERT INTO pms_approval_field_permission (id, approval_node_id, entity_type, field_name, permission, mask_pattern, create_time) VALUES
(1, 1, 'Deliverable', 'contractAmount', 'MASKED', 'amount-mask', NOW()),
(2, 1, 'Deliverable', 'customerContact', 'MASKED', 'phone-mask', NOW()),
(3, 1, 'Deliverable', 'filePath', 'HIDDEN', NULL, NOW());
```

### 6.11 JSON TypeHandler 设计

#### TypeHandler 子类集合

```java
// /workspace/network-equipment-pms/pms-common/src/main/java/com/dp/plat/common/handler/JsonTypeHandlers.java
package com.dp.plat.common.handler;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.dp.plat.project.dto.PhaseExitGate;
import com.dp.plat.project.dto.PhaseCriteria;
import com.dp.plat.project.dto.TaskPlanSnapshot;
import com.dp.plat.project.dto.TemplateSnapshot;
import java.util.List;

public final class JsonTypeHandlers {
    private JsonTypeHandlers() {}

    public static class PhaseCriteriaHandler extends JacksonTypeHandler {
        public PhaseCriteriaHandler() { super(PhaseCriteria.class); }
    }

    public static class PhaseExitGateHandler extends JacksonTypeHandler {
        public PhaseExitGateHandler() { super(PhaseExitGate.class); }
    }

    public static class TaskPlanSnapshotListHandler extends JacksonTypeHandler {
        public TaskPlanSnapshotListHandler() { super(List.class); }
    }

    public static class TemplateSnapshotHandler extends JacksonTypeHandler {
        public TemplateSnapshotHandler() { super(TemplateSnapshot.class); }
    }
}
```

#### 实体使用方式

```java
@TableName(value = "pms_project_template_version", autoResultMap = true)
public class ProjectTemplateVersion extends BaseEntity {
    @TableField(typeHandler = JsonTypeHandlers.TemplateSnapshotHandler.class)
    private TemplateSnapshot snapshotJson;
}

@TableName(value = "pms_baseline_snapshot", autoResultMap = true)
public class BaselineSnapshot extends BaseEntity {
    @TableField(typeHandler = JsonTypeHandlers.TaskPlanSnapshotListHandler.class)
    private List<TaskPlanSnapshot> snapshotJson;
}
```

**注意事项**：
- `@TableName(autoResultMap = true)` 必须开启，否则 `@TableField(typeHandler=...)` 在 BaseMapper 方法中不生效
- JSON 列约束：MySQL 8 的 JSON 类型不能有非 NULL 默认值，由应用层保证非空
- 空对象写入：用 `'{}'` 或 `'[]'` 显式 INSERT，避免 NULL

### 6.12 迁移执行顺序与回滚策略

```
V64 (模板表) → V65 (Project 扩展) → V66 (Phase/Member/Config) 
   → V67 (任务层级) → V68 (任务依赖) → V69 (基线快照) 
   → V70 (交付件) → V71 (审批中心) → V72 (种子数据)
```

**回滚策略**：
- 单脚本执行失败：Flyway 自动停止，修复脚本后重启应用
- 上线后发现数据问题：手动编写 `V73__rollback_xxx.sql` 反向操作
- 大规模回滚：完全重置数据库（`DROP DATABASE pms; CREATE DATABASE...`），所有 V1~V72 重新执行

### 6.13 迁移脚本质量检查清单

- [ ] 所有表使用 InnoDB + utf8mb4
- [ ] 所有表含 `create_time`/`update_time`/`deleted`/`version`（审计与乐观锁）
- [ ] 字段名使用 snake_case
- [ ] 外键关系用索引覆盖（不创建物理外键，遵循 V25 策略）
- [ ] 状态字段使用 VARCHAR 而非 ENUM（便于扩展）
- [ ] JSON 字段使用 JSON 类型（MySQL 8）+ 自定义 TypeHandler
- [ ] 金额字段使用 DECIMAL 而非 FLOAT/DOUBLE
- [ ] 索引覆盖高频查询（路径前缀、父子关系、状态过滤）
- [ ] INSERT 种子数据使用 `INSERT ... VALUES` 批量写法
- [ ] 不含真实凭据

---

## 7. 前端模块与页面设计

### 7.1 设计原则与现有模式

- Vue 3 + TypeScript + Element Plus + Vite（不引入新框架）
- API 文件按业务域拆分 `src/api/<domain>.ts`
- 视图按 `src/views/<domain>/<sub>/index.vue` 组织
- 列表统一复用 `MobileListCard` 组件
- 请求封装统一 `@/utils/request` 的 `get/post/put/del`
- **路由全量重构为嵌套模式**（不留技术债）

### 7.2 前端目录结构（新增/扩展）

```
pms-frontend/src/
├── api/
│   ├── project.ts                      【扩展】+ 主子项目、生命周期
│   ├── project-template.ts             【新增】Story 1
│   ├── project-phase.ts                【新增】Story 1, 2
│   ├── project-member.ts               【新增】Story 2
│   ├── project-config.ts               【新增】Story 3, 4（阈值配置）
│   ├── implementation.ts               【扩展】+ 任务层级、完成验收
│   ├── task-checklist.ts               【新增】Story 3
│   ├── task-dependency.ts              【新增】Story 4
│   ├── task-comment.ts                 【新增】Story 3
│   ├── task-activity.ts                【新增】Story 3
│   ├── deliverable.ts                  【扩展】7 态流转、版本
│   ├── deliverable-version.ts          【新增】Story 5
│   ├── deliverable-signature.ts        【新增】Story 5
│   ├── baseline.ts                     【新增】Story 4
│   ├── approval-center.ts              【新增】Story 6
│   └── approval-field-perm.ts          【新增】Story 6
├── views/
│   ├── project/
│   │   ├── list/index.vue              【扩展】+ 从模板创建按钮
│   │   ├── detail/index.vue            【扩展】+ 主子项目树、阶段、成员、基线 Tab
│   │   ├── kanban/index.vue            【保留】
│   │   ├── tree/index.vue              【新增】主子项目树视图
│   │   └── template/
│   │       ├── index.vue               【新增】模板列表
│   │       ├── form.vue                【新增】模板编辑（含快照构建器）
│   │       └── version.vue             【新增】模板版本管理
│   ├── phase/index.vue                 【新增】阶段管理（含退出条件编辑器）
│   ├── task/
│   │   ├── list/index.vue              【新增】任务列表（树形）
│   │   ├── detail/index.vue            【新增】任务详情（含检查项/评论/活动/依赖 Tab）
│   │   └── dependency/index.vue        【新增】依赖关系图
│   ├── deliverable/
│   │   ├── index.vue                   【扩展】7 态列表
│   │   └── detail/index.vue            【新增】交付件详情（含版本历史）
│   ├── baseline/
│   │   ├── index.vue                   【新增】基线列表与偏差分析
│   │   └── diff.vue                    【新增】偏差对比页
│   ├── workflow/
│   │   ├── todo/index.vue              【保留】Flowable 待办
│   │   ├── approval-center/index.vue   【新增】统一审批中心
│   │   ├── approval-detail/index.vue   【新增】审批详情（含脱敏展示）
│   │   ├── approval-history/index.vue  【新增】审批历史（多轮次）
│   │   └── field-perm/index.vue        【新增】敏感字段权限配置
│   └── project-config/index.vue        【新增】项目阈值配置
└── components/
    ├── ProjectTemplateSelector.vue     【新增】模板选择器
    ├── PhaseExitGateEditor.vue         【新增】阶段退出条件编辑器
    ├── TaskTree.vue                    【新增】任务树组件（递归）
    ├── TaskChecklist.vue               【新增】检查项组件
    ├── DependencyGraph.vue             【新增】AntV G6 依赖关系图
    ├── BaselineDiffTable.vue           【新增】基线偏差对比表
    ├── DeliverableStatusFlow.vue       【新增】7 态状态流转可视化
    ├── DeliverableVersionList.vue      【新增】版本历史列表
    ├── ApprovalTimeline.vue            【新增】审批时间轴（多轮次）
    ├── SensitiveFieldDisplay.vue       【新增】脱敏字段展示
    └── SubProjectTree.vue              【新增】主子项目树
```

### 7.3 路由全量重构（嵌套模式）

```typescript
// src/router/index.ts
const Layout = () => import('@/layout/index.vue')

export const routes: RouteRecordRaw[] = [
  { path: '/login', name: 'Login', component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', hidden: true } },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '仪表盘', icon: 'Odometer' } },
    ]
  },
  // ============ 项目管理 ============
  {
    path: '/project',
    component: Layout,
    redirect: '/project/list',
    meta: { title: '项目管理', icon: 'Folder' },
    children: [
      { path: 'list', name: 'ProjectList', component: () => import('@/views/project/list/index.vue'),
        meta: { title: '项目列表', icon: 'Folder' } },
      { path: 'detail/:id', name: 'ProjectDetail', component: () => import('@/views/project/detail/index.vue'),
        meta: { title: '项目详情', hidden: true } },
      { path: 'tree', name: 'ProjectTree', component: () => import('@/views/project/tree/index.vue'),
        meta: { title: '项目树', icon: 'Share' } },
      { path: 'kanban', name: 'ProjectKanban', component: () => import('@/views/project/kanban/index.vue'),
        meta: { title: '交付看板', icon: 'Grid' } },
      { path: 'template', name: 'ProjectTemplate', component: () => import('@/views/project/template/index.vue'),
        meta: { title: '项目模板', icon: 'Files', perms: 'project:template:list' } },
      { path: 'template/form/:id?', name: 'ProjectTemplateForm', component: () => import('@/views/project/template/form.vue'),
        meta: { title: '模板编辑', hidden: true } },
      { path: 'template/version/:id', name: 'ProjectTemplateVersion', component: () => import('@/views/project/template/version.vue'),
        meta: { title: '版本管理', hidden: true } },
    ]
  },
  // ============ 阶段管理 ============
  {
    path: '/phase',
    component: Layout,
    meta: { hidden: true },
    children: [
      { path: ':projectId', name: 'ProjectPhase', component: () => import('@/views/phase/index.vue'),
        meta: { title: '阶段管理', hidden: true } },
    ]
  },
  // ============ 任务管理 ============
  {
    path: '/task',
    component: Layout,
    redirect: '/task/list',
    meta: { title: '任务管理', icon: 'Tickets' },
    children: [
      { path: 'list', name: 'TaskList', component: () => import('@/views/task/list/index.vue'),
        meta: { title: '任务列表', icon: 'Tickets' } },
      { path: 'detail/:id', name: 'TaskDetail', component: () => import('@/views/task/detail/index.vue'),
        meta: { title: '任务详情', hidden: true } },
      { path: 'dependency/:projectId', name: 'TaskDependency', component: () => import('@/views/task/dependency/index.vue'),
        meta: { title: '依赖关系', hidden: true } },
    ]
  },
  // ============ 交付件 ============
  {
    path: '/deliverable',
    component: Layout,
    redirect: '/deliverable/list',
    meta: { title: '交付件', icon: 'Document' },
    children: [
      { path: 'list', name: 'DeliverableList', component: () => import('@/views/deliverable/index.vue'),
        meta: { title: '交付件列表', icon: 'Document' } },
      { path: 'detail/:id', name: 'DeliverableDetail', component: () => import('@/views/deliverable/detail/index.vue'),
        meta: { title: '交付件详情', hidden: true } },
    ]
  },
  // ============ 基线 ============
  {
    path: '/baseline',
    component: Layout,
    redirect: '/baseline/list',
    meta: { title: '计划基线', icon: 'Histogram' },
    children: [
      { path: 'list', name: 'BaselineList', component: () => import('@/views/baseline/index.vue'),
        meta: { title: '基线列表', icon: 'Histogram' } },
      { path: 'diff/:projectId', name: 'BaselineDiff', component: () => import('@/views/baseline/diff.vue'),
        meta: { title: '偏差分析', hidden: true } },
    ]
  },
  // ============ 审批中心 ============
  {
    path: '/workflow',
    component: Layout,
    redirect: '/workflow/approval-center',
    meta: { title: '审批中心', icon: 'CheckFilled' },
    children: [
      { path: 'todo', name: 'WorkflowTodo', component: () => import('@/views/workflow/todo/index.vue'),
        meta: { title: 'Flowable 待办', icon: 'Bell' } },
      { path: 'approval-center', name: 'ApprovalCenter', component: () => import('@/views/workflow/approval-center/index.vue'),
        meta: { title: '统一审批中心', icon: 'CheckFilled', perms: 'workflow:approval:handle' } },
      { path: 'approval-detail/:id', name: 'ApprovalDetail', component: () => import('@/views/workflow/approval-detail/index.vue'),
        meta: { title: '审批详情', hidden: true } },
      { path: 'approval-history/:id', name: 'ApprovalHistory', component: () => import('@/views/workflow/approval-history/index.vue'),
        meta: { title: '审批历史', hidden: true } },
      { path: 'field-perm', name: 'ApprovalFieldPerm', component: () => import('@/views/workflow/field-perm/index.vue'),
        meta: { title: '字段权限配置', icon: 'Lock', perms: 'workflow:approval:field:perm' } },
    ]
  },
  // ============ 项目配置 ============
  {
    path: '/project-config',
    component: Layout,
    meta: { hidden: true },
    children: [
      { path: ':projectId?', name: 'ProjectConfig', component: () => import('@/views/project-config/index.vue'),
        meta: { title: '项目配置', hidden: true } },
    ]
  },
  // ============ 既有路由按相同嵌套模式重组 ============
  // 以下业务域在实现阶段一并重构为嵌套结构（URL 路径保持不变，仅组织方式调整）：
  //   - 资产管理：/asset/category, /asset/model, /asset/list
  //   - 实施管理：/implementation/agent, /implementation/settlement
  //     （注：/implementation/task 已迁移到 /task/list，需添加重定向）
  //   - 问题/风险/变更：/issue, /risk, /change-request
  //   - Punch List：/punch-list
  //   - RMA/质保：/rma, /warranty
  //   - 报表：/report
  //   - 系统管理：/system/user, /system/role, /system/menu, /system/dict,
  //              /system/cache, /system/schedule, /system/audit
  //   - 集成/通知/帮助：/integration-health, /notification, /help, /system-status
  //   - 低代码平台：/lowcode/*（22 个子路由）

  // URL 重定向（兼容旧路径）
  { path: '/implementation/task', redirect: '/task/list' }
]

### 7.4 AntV G6 依赖关系图

```vue
<!-- components/DependencyGraph.vue -->
<script setup lang="ts">
import { onMounted, onUnmounted, ref, watch } from 'vue'
import { Graph, type NodeData, type EdgeData } from '@antv/g6'
import { listDependencies, type TaskDependency } from '@/api/task-dependency'

interface Props {
  projectId: number
  highlightCycle?: number[]
}

const props = defineProps<Props>()
const emit = defineEmits<{ 'select-task': [number] }>()

const containerRef = ref<HTMLDivElement>()
let graph: Graph | null = null

onMounted(() => {
  if (containerRef.value) {
    graph = new Graph({
      container: containerRef.value,
      width: containerRef.value.offsetWidth,
      height: 600,
      layout: { type: 'dagre', rankdir: 'LR', nodesep: 40, ranksep: 80 },
      node: {
        type: 'rect',
        style: {
          size: [140, 40], radius: 6,
          labelText: (d: NodeData) => d.data?.taskName,
          labelFill: '#333', fill: '#e6f7ff', stroke: '#1890ff',
        },
        state: {
          cycle: { fill: '#fff1f0', stroke: '#ff4d4f' },
          critical: { fill: '#fff7e6', stroke: '#fa8c16' },
        },
      },
      edge: {
        type: 'quadratic',
        style: {
          labelText: (d: EdgeData) => {
            const data = d.data as TaskDependency
            return `${data.dependencyType}${data.lagDays ? '+' + data.lagDays : ''}`
          },
          endArrow: true,
        },
        state: { cycle: { stroke: '#ff4d4f', lineWidth: 2 } },
      },
      behaviors: ['drag-canvas', 'zoom-canvas', 'drag-element'],
      autoFit: 'view',
    })
    graph.on('node:click', (evt: any) => emit('select-task', evt.target.id))
  }
  refresh()
})

watch(() => props.projectId, refresh)
watch(() => props.highlightCycle, applyCycleHighlight)

async function refresh() {
  if (!graph || !props.projectId) return
  const deps = await listDependencies(props.projectId)
  const tasks = await listTasksByProject(props.projectId)
  const nodes: NodeData[] = tasks.map(t => ({ id: String(t.id), data: { taskName: t.taskName, status: t.status } }))
  const edges: EdgeData[] = deps.map(d => ({
    source: String(d.predecessorTaskId), target: String(d.successorTaskId), data: d,
  }))
  graph.setData({ nodes, edges })
  graph.render()
}

function applyCycleHighlight(cycleIds?: number[]) {
  if (!graph || !cycleIds?.length) return
  const idStrs = cycleIds.map(String)
  graph.setElementState(idStrs, 'cycle')
  for (let i = 0; i < cycleIds.length - 1; i++) {
    const edgeId = `${cycleIds[i]}-${cycleIds[i+1]}`
    graph.setElementState(edgeId, 'cycle')
  }
}

onUnmounted(() => { graph?.destroy(); graph = null })
</script>

<template>
  <div ref="containerRef" class="dependency-graph" />
</template>

<style scoped>
.dependency-graph {
  width: 100%; height: 600px;
  border: 1px solid #e8e8e8; border-radius: 4px;
}
</style>
```

### 7.5 关键交互设计

#### Story 1 验收 1：从模板创建项目

`views/project/list/index.vue` 顶部新增 `[+ 从模板创建]` 按钮 → 弹出 `ProjectTemplateSelector.vue` → 选择模板版本 → 填写项目信息 → 创建成功跳转 `project/detail/:id` 展示完整默认计划。

#### Story 2 验收 1：阶段推进被阻止

```typescript
async function advancePhase(phaseId: number) {
  const res = await advancePhaseApi(phaseId, currentUserId.value)
  if (!res.success) {
    if (res.errorCode === 'PHASE_EXIT_GATE_FAILED') {
      ElMessageBox.alert(
        renderGateViolations(res.violations),
        '阶段退出条件未满足',
        { dangerouslyUseHTMLString: true, type: 'warning' }
      )
    }
  } else {
    ElMessage.success('阶段已推进')
    refreshPhases()
  }
}
```

#### Story 3 验收 1：强制检查项拦截

点击"提交评审"按钮，调用 `/submit-review` 接口，若返回 `TASK_CHECKLIST_REQUIRED` 错误，弹出未完成检查项清单。

#### Story 4 验收 1：循环依赖高亮

```typescript
async function saveDependency() {
  try {
    await saveDependencyApi(form)
    ElMessage.success('依赖保存成功')
    refreshGraph()
  } catch (err: any) {
    if (err.data?.errorCode === 'CYCLE_DETECTED') {
      const cyclePath = err.data.cyclePath
      ElMessageBox.alert(
        `形成循环依赖！\n闭环路径:\n${cyclePath.map((t: any) => t.taskName).join(' → ')}`,
        '操作失败', { type: 'error' }
      )
      highlightCycleOnGraph(cyclePath)
    }
  }
}
```

#### Story 5 验收 1：修订创建新版本

`views/deliverable/detail/index.vue` 顶部 `[修订]` 按钮 → 弹出 `DeliverableRevisionDialog.vue`（填写 `changeLog` + 上传新文件）→ 调用 `POST /api/deliverable/{id}/revise` → 成功后版本号 +1，状态重置为 `DRAFT`，`DeliverableVersionList.vue` 自动追加新版本行并标记"v(n) 修订自 v(n-1)"。旧版本只读，"对比差异"按钮调用 `/api/deliverable/{id}/diff?from=v(n-1)&to=v(n)` 渲染 `BaselineDiffTable.vue`（复用基线差异组件）。

#### Story 6 验收 1：审批详情含字段脱敏

`views/workflow/approval-detail/index.vue` 加载时调用 `GET /api/workflow/approval/{id}`，响应中的 `formData` 已按当前用户字段权限脱敏（如 `***` 或隐藏字段）。`SensitiveFieldDisplay.vue` 组件读取 `fieldMeta.sensitive = true` 标记，在字段右上角显示锁图标，鼠标悬停 tooltip 显示"该字段已脱敏，请联系审批管理员"。无权限字段直接不渲染（`v-if="field.visible"`）。

```typescript
// SensitiveFieldDisplay.vue 关键逻辑
const displayValue = computed(() => {
  if (!props.field.visible) return null          // 无权限：不渲染
  if (props.field.masked) return props.field.maskedValue   // 脱敏：显示掩码
  return props.field.value                        // 正常显示
})
```

### 7.6 前端模块依赖图

```
api/project-template.ts ──► views/project/template/index.vue
                        ├─► views/project/template/form.vue
                        ├─► views/project/template/version.vue
                        └─► components/ProjectTemplateSelector.vue ──► views/project/list/index.vue

api/project-phase.ts ───► views/phase/index.vue
                     ├─► views/project/detail/index.vue (阶段 Tab)
                     └─► components/PhaseExitGateEditor.vue

api/implementation.ts ──► views/task/list/index.vue
                      ├─► views/task/detail/index.vue
                      ├─► components/TaskTree.vue
                      ├─► components/TaskChecklist.vue
                      └─► api/task-checklist.ts, task-comment.ts, task-activity.ts

api/task-dependency.ts ─► views/task/dependency/index.vue
                       └─► components/DependencyGraph.vue (AntV G6)

api/baseline.ts ────────► views/baseline/index.vue
                  ├─► views/baseline/diff.vue
                  └─► components/BaselineDiffTable.vue

api/deliverable.ts ─────► views/deliverable/index.vue
                    ├─► views/deliverable/detail/index.vue
                    ├─► api/deliverable-version.ts, deliverable-signature.ts
                    ├─► components/DeliverableStatusFlow.vue
                    └─► components/DeliverableVersionList.vue

api/approval-center.ts ─► views/workflow/approval-center/index.vue
                       ├─► views/workflow/approval-detail/index.vue
                       ├─► views/workflow/approval-history/index.vue
                       ├─► components/ApprovalTimeline.vue
                       ├─► components/SensitiveFieldDisplay.vue
                       └─► api/approval-field-perm.ts ──► views/workflow/field-perm/index.vue

api/project-config.ts ──► views/project-config/index.vue
```

### 7.7 前端测试策略

| 类型 | 工具 | 覆盖范围 |
|---|---|---|
| 单元测试 | Vitest | API 封装函数、关键组件（TaskTree 递归、SensitiveFieldDisplay 脱敏） |
| 组件测试 | Vue Test Utils | PhaseExitGateEditor 增删、ApprovalTimeline 多轮次渲染 |
| 类型检查 | `vue-tsc --noEmit` | 所有新增 .ts/.vue 文件 |
| E2E 验收 | 手动 | 6 个故事的 Given-When-Then 场景 |
| 视觉回归 | 手动对比 | 关键页面截图对比 |

**验收场景自动化映射**：

| 用户故事 | 验收场景 | 前端测试入口 |
|---|---|---|
| Story 1 | 从模板创建项目 | `views/project/list/index.vue` → 点击"从模板创建" → 验证跳转到详情页含完整计划 |
| Story 2 | 阶段推进被阻止 | `views/project/detail/index.vue` 阶段 Tab → 点击"推进阶段" → 验证弹出未完成事项 |
| Story 2 | 关闭主项目被拒绝 | `views/project/detail/index.vue` 子项目 Tab → 点击"关闭主项目" → 验证弹出未关闭子项目 |
| Story 3 | 强制检查项拦截 | `views/task/detail/index.vue` → 点击"提交评审" → 验证弹出未完成检查项 |
| Story 3 | 进度汇总 | `views/task/list/index.vue` → 修改子任务进度 → 验证父任务进度自动更新 |
| Story 4 | 循环依赖被拒 | `views/task/dependency/index.vue` → 创建闭环依赖 → 验证弹出闭环路径 |
| Story 4 | 基线偏差审批 | `views/baseline/diff.vue` → 修改关键日期 → 验证弹出偏差超阈值提示 |
| Story 5 | 修订创建新版本 | `views/deliverable/detail/index.vue` → 点击"修订" → 验证版本历史出现 v2 |
| Story 5 | 必需交付件校验 | `views/project/detail/index.vue` 阶段 Tab → 推进阶段 → 验证提示交付件未批准 |
| Story 6 | 字段脱敏 | `views/workflow/approval-detail/index.vue` → 验证敏感字段显示脱敏值 |
| Story 6 | 历史保留 | `views/workflow/approval-history/index.vue` → 验证多轮次历史展示 |

### 7.8 新增前端依赖

```json
{
  "dependencies": {
    "@antv/g6": "^5.0.0"
  }
}
```

---

## 8. 实现计划与里程碑

### 8.1 实现阶段划分

```
Phase 1: 基础设施层（V64~V66 迁移 + 实体 + 路由重构）
    ↓
Phase 2: 项目模板与创建（Story 1）
    ↓
Phase 3: 项目生命周期与主子项目（Story 2）
    ↓
Phase 4: 任务体系与团队协作（Story 3）
    ↓
Phase 5: 依赖与基线（Story 4）
    ↓
Phase 6: 交付件全生命周期（Story 5）
    ↓
Phase 7: 统一审批中心（Story 6）
    ↓
Phase 8: 联调测试与验收
```

### 8.2 详细任务分解

#### Phase 1：基础设施层

| # | 任务 | 模块 | 验证 |
|---|---|---|---|
| 1.1 | 编写 V64 迁移脚本（项目模板表） | pms-admin/migration | `flyway info` 显示 V64 待执行 |
| 1.2 | 编写 V65 迁移脚本（Project 扩展字段） | pms-admin/migration | Project 表新增 9 字段 |
| 1.3 | 编写 V66 迁移脚本（Phase/Member/Config + 默认配置） | pms-admin/migration | 3 张新表 + 9 条默认配置 |
| 1.4 | 创建 `JsonTypeHandlers.java`（4 个 TypeHandler 子类） | pms-common | 编译通过 |
| 1.5 | 创建 `ProjectTemplate`/`ProjectTemplateVersion` 实体 | pms-project | 编译通过 |
| 1.6 | 扩展 `Project` 实体（+9 字段） | pms-project | 编译通过 |
| 1.7 | 创建 `ProjectPhase`/`ProjectMember`/`ProjectConfig` 实体 | pms-project | 编译通过 |
| 1.8 | 创建对应 Mapper + XML | pms-project | 编译通过 |
| 1.9 | 创建 `ProjectConfigService` 多层级配置读取 | pms-project | 单元测试：项目级 > 模板级 > 系统默认 |
| 1.10 | 前端：路由全量重构为嵌套模式 | pms-frontend/router | 全部页面可访问，面包屑正确 |
| 1.11 | 前端：安装 `@antv/g6@^5.0.0` | pms-frontend | `npm ls @antv/g6` 显示已安装 |
| 1.12 | 启动后端，验证 V64~V66 迁移成功 | 沙盒 | `flyway_schema_history` 含 3 条新记录 |

**Phase 1 里程碑**：基础设施就位，迁移成功，前端可访问。

#### Phase 2：项目模板与项目创建（Story 1）

| # | 任务 | 模块 | 验证 |
|---|---|---|---|
| 2.1 | 编写 V67 迁移脚本（任务层级字段） | pms-admin/migration | ImplTask 表新增 8 字段 |
| 2.2 | 扩展 `ImplTask` 实体（+parentTaskId/taskPath/depth/priority 等） | pms-implementation | 编译通过 |
| 2.3 | 实现 `IProjectTemplateService`（CRUD + 版本发布） | pms-project | 单元测试：发布版本后状态为 PUBLISHED |
| 2.4 | 实现 `createProjectFromTemplate` 深拷贝逻辑 | pms-project | 单元测试：从模板创建项目，验证阶段/任务/交付件全部复制 |
| 2.5 | 创建 `ProjectTemplateController`（10 个端点） | pms-project | Postman 调用全部端点 |
| 2.6 | 前端：`api/project-template.ts` | pms-frontend/api | TypeScript 类型检查通过 |
| 2.7 | 前端：`views/project/template/index.vue` 模板列表 | pms-frontend/views | 页面渲染，分页正常 |
| 2.8 | 前端：`views/project/template/form.vue` 模板编辑器 | pms-frontend/views | 可添加/删除阶段和任务 |
| 2.9 | 前端：`views/project/template/version.vue` 版本管理 | pms-frontend/views | 可发布新版本 |
| 2.10 | 前端：`components/ProjectTemplateSelector.vue` 模板选择器 | pms-frontend/components | 弹窗选择模板版本 |
| 2.11 | 前端：`views/project/list/index.vue` 集成"从模板创建"按钮 | pms-frontend/views | 点击后弹出选择器，创建成功跳转详情 |
| 2.12 | **Story 1 验收测试** | 全栈 | 见验收场景 1.1 + 1.2 |

**Phase 2 里程碑**：Story 1 两个验收场景全部通过。

#### Phase 3：项目生命周期与主子项目（Story 2）

| # | 任务 | 模块 | 验证 |
|---|---|---|---|
| 3.1 | 扩展 `ProjectController`（主子项目、生命周期端点） | pms-project | 端点可访问 |
| 3.2 | 实现 `IProjectPhaseService.advancePhase`（含退出条件校验） | pms-project | 单元测试：退出条件未满足时返回 violations |
| 3.3 | 实现 `IProjectPhaseService.closeProject`（含子项目校验） | pms-project | 单元测试：子项目未关闭时拒绝 |
| 3.4 | 实现主子项目递归汇总（CTE 查询） | pms-project | 单元测试：主项目进度 = 子项目加权平均 |
| 3.5 | 创建 `ProjectPhaseController`（阶段管理端点） | pms-project | 端点可访问 |
| 3.6 | 创建 `ProjectMemberController`（成员管理端点） | pms-project | 端点可访问 |
| 3.7 | 前端：`api/project-phase.ts`、`api/project-member.ts` | pms-frontend/api | 类型检查通过 |
| 3.8 | 前端：`views/project/detail/index.vue` 改造为 Tab 布局 | pms-frontend/views | 8 个 Tab 切换正常 |
| 3.9 | 前端：`views/project/tree/index.vue` 主子项目树 | pms-frontend/views | 树形展示，含进度汇总 |
| 3.10 | 前端：`components/SubProjectTree.vue` | pms-frontend/components | 递归渲染，支持无限嵌套 |
| 3.11 | 前端：`views/phase/index.vue` 阶段管理 | pms-frontend/views | 阶段流水线展示，推进按钮 |
| 3.12 | 前端：`components/PhaseExitGateEditor.vue` 退出条件编辑器 | pms-frontend/components | 可编辑 4 类退出条件 |
| 3.13 | **Story 2 验收测试** | 全栈 | 见验收场景 2.1 + 2.2 |

**Phase 3 里程碑**：Story 2 两个验收场景全部通过。

#### Phase 4：任务体系与团队协作（Story 3）

| # | 任务 | 模块 | 验证 |
|---|---|---|---|
| 4.1 | 编写 V67 补充（检查项/评论/活动表） | pms-admin/migration | 3 张新表创建成功 |
| 4.2 | 创建 `TaskChecklist`/`TaskComment`/`TaskActivity` 实体 + Mapper | pms-implementation | 编译通过 |
| 4.3 | 实现 `TaskChecklistService`（CRUD + 勾选） | pms-implementation | 单元测试：勾选/取消勾选 |
| 4.4 | 实现 `ImplTaskService.submitForReview`（含强制检查项校验） | pms-implementation | 单元测试：强制检查项未完成时返回 TASK_CHECKLIST_REQUIRED |
| 4.5 | 实现 `TaskRollupService`（异步进度汇总） | pms-implementation | 单元测试：子任务进度变更后父任务自动汇总 |
| 4.6 | 实现任务移动（`moveTask`，同步更新 taskPath） | pms-implementation | 单元测试：移动后 taskPath 正确 |
| 4.7 | 创建任务相关 Controller（任务/检查项/评论/活动） | pms-implementation | 端点可访问 |
| 4.8 | 前端：`api/implementation.ts` 扩展 + `api/task-checklist.ts` 等 | pms-frontend/api | 类型检查通过 |
| 4.9 | 前端：`views/task/list/index.vue` 任务树列表 | pms-frontend/views | 树形展开，进度汇总显示 |
| 4.10 | 前端：`views/task/detail/index.vue` 任务详情（6 Tab） | pms-frontend/views | Tab 切换正常 |
| 4.11 | 前端：`components/TaskTree.vue` 递归任务树 | pms-frontend/components | 支持无限层级 |
| 4.12 | 前端：`components/TaskChecklist.vue` 检查项组件 | pms-frontend/components | 强制检查项标红 |
| 4.13 | 前端：`views/project-config/index.vue` 项目阈值配置 | pms-frontend/views | 可修改配置项 |
| 4.14 | **Story 3 验收测试** | 全栈 | 见验收场景 3.1 + 3.2 |

**Phase 4 里程碑**：Story 3 两个验收场景全部通过。

#### Phase 5：依赖与基线（Story 4）

| # | 任务 | 模块 | 验证 |
|---|---|---|---|
| 5.1 | 编写 V68 迁移脚本（任务依赖 + milestone ALTER） | pms-admin/migration | 新表创建，milestone 加字段 |
| 5.2 | 编写 V69 迁移脚本（基线快照表） | pms-admin/migration | 新表创建成功 |
| 5.3 | 创建 `TaskDependency`/`BaselineSnapshot` 实体 + Mapper | pms-baseline (新模块) | 编译通过 |
| 5.4 | 实现 `TaskDependencyService.saveDependency`（含 DFS 循环检测） | pms-baseline | 单元测试：形成闭环时返回 CYCLE_DETECTED + 路径 |
| 5.5 | 实现 `BaselineService.saveBaseline`（快照所有任务） | pms-baseline | 单元测试：快照 JSON 含全部任务计划 |
| 5.6 | 实现 `BaselineService.compareWithBaseline`（偏差分析） | pms-baseline | 单元测试：偏差计算正确 |
| 5.7 | 实现基线变更审批触发（双阈值 OR 逻辑） | pms-baseline | 单元测试：超阈值时触发审批 |
| 5.8 | 创建 `TaskDependencyController`、`BaselineController` | pms-baseline | 端点可访问 |
| 5.9 | 前端：`api/task-dependency.ts`、`api/baseline.ts` | pms-frontend/api | 类型检查通过 |
| 5.10 | 前端：`views/task/dependency/index.vue` 依赖关系图（G6） | pms-frontend/views | G6 渲染 DAG，节点可点击 |
| 5.11 | 前端：`components/DependencyGraph.vue` G6 组件 | pms-frontend/components | 闭环路径高亮 |
| 5.12 | 前端：`views/baseline/index.vue` 基线列表 | pms-frontend/views | 显示基线状态 |
| 5.13 | 前端：`views/baseline/diff.vue` 偏差分析 | pms-frontend/views | 偏差表 + 超阈值提示 |
| 5.14 | 前端：`components/BaselineDiffTable.vue` | pms-frontend/components | 偏差行高亮 |
| 5.15 | **Story 4 验收测试** | 全栈 | 见验收场景 4.1 + 4.2 |

**Phase 5 里程碑**：Story 4 两个验收场景全部通过。

#### Phase 6：交付件全生命周期（Story 5）

| # | 任务 | 模块 | 验证 |
|---|---|---|---|
| 6.1 | 编写 V70 迁移脚本（Deliverable 扩展 + 版本/签名/引用表） | pms-admin/migration | 4 张表创建/扩展 |
| 6.2 | 创建 `pms-deliverable` 新模块（pom.xml + 实体 + Mapper） | pms-deliverable (新模块) | 编译通过 |
| 6.3 | 扩展 `Deliverable` 实体（7 态状态机 + 版本字段） | pms-deliverable | 编译通过 |
| 6.4 | 创建 `DeliverableVersion`/`DeliverableSignature`/`DeliverableReference` 实体 | pms-deliverable | 编译通过 |
| 6.5 | 实现 `DeliverableService` 7 态流转 | pms-deliverable | 单元测试：状态流转合法/非法 |
| 6.6 | 实现 `DeliverableService.revise`（新建版本，不覆盖旧版本） | pms-deliverable | 单元测试：v1 保留，v2 创建 |
| 6.7 | 实现 `validateMandatoryDeliverables`（阶段退出校验） | pms-deliverable | 单元测试：未批准交付件返回 |
| 6.8 | 创建 `DeliverableController`（14 个端点） | pms-deliverable | 端点可访问 |
| 6.9 | 前端：`api/deliverable.ts` 扩展 + `api/deliverable-version.ts` 等 | pms-frontend/api | 类型检查通过 |
| 6.10 | 前端：`views/deliverable/index.vue` 7 态列表 | pms-frontend/views | 状态过滤正常 |
| 6.11 | 前端：`views/deliverable/detail/index.vue` 交付件详情（4 Tab） | pms-frontend/views | Tab 切换正常 |
| 6.12 | 前端：`components/DeliverableStatusFlow.vue` 7 态流转可视化 | pms-frontend/components | 当前状态高亮 |
| 6.13 | 前端：`components/DeliverableVersionList.vue` 版本历史 | pms-frontend/components | 版本列表展示 |
| 6.14 | **Story 5 验收测试** | 全栈 | 见验收场景 5.1 + 5.2 |

**Phase 6 里程碑**：Story 5 两个验收场景全部通过。

#### Phase 7：统一审批中心（Story 6）

| # | 任务 | 模块 | 验证 |
|---|---|---|---|
| 7.1 | 编写 V71 迁移脚本（审批记录/节点/历史/字段权限 4 表） | pms-admin/migration | 4 张新表创建 |
| 7.2 | 创建 `ApprovalRecord`/`ApprovalNode`/`ApprovalHistory`/`ApprovalFieldPermission` 实体 | pms-workflow | 编译通过 |
| 7.3 | 实现 `ApprovalCenterService`（创建/通过/退回/撤回/重新提交） | pms-workflow | 单元测试：退回后重新提交 round+1，历史保留 |
| 7.4 | 实现 `SensitiveFieldMasker`（脱敏逻辑 + 脱敏规则） | pms-workflow | 单元测试：金额/手机号/邮箱脱敏正确 |
| 7.5 | 实现 `ApprovalDispatcher`（监听 Spring Event 创建审批） | pms-workflow | 单元测试：阶段推进事件触发审批创建 |
| 7.6 | 实现 `ApprovalTimeoutScheduler`（定时扫描超时审批） | pms-workflow | 单元测试：超时后按配置动作处理 |
| 7.7 | 集成 Flowable：审批创建时启动 BPMN 流程实例 | pms-workflow | 集成测试：Flowable 任务列表同步 |
| 7.8 | 创建 `ApprovalCenterController`（11 个端点） | pms-workflow | 端点可访问 |
| 7.9 | 创建 `ApprovalFieldPermissionController`（4 个端点） | pms-workflow | 端点可访问 |
| 7.10 | 前端：`api/approval-center.ts` + `api/approval-field-perm.ts` | pms-frontend/api | 类型检查通过 |
| 7.11 | 前端：`views/workflow/approval-center/index.vue` 统一审批中心 | pms-frontend/views | 待办/已办/项目维度切换 |
| 7.12 | 前端：`views/workflow/approval-detail/index.vue` 审批详情 | pms-frontend/views | 敏感字段脱敏展示 |
| 7.13 | 前端：`views/workflow/approval-history/index.vue` 审批历史 | pms-frontend/views | 多轮次时间轴展示 |
| 7.14 | 前端：`views/workflow/field-perm/index.vue` 字段权限配置 | pms-frontend/views | 可配置字段可见/脱敏/隐藏 |
| 7.15 | 前端：`components/ApprovalTimeline.vue` 审批时间轴 | pms-frontend/components | 多轮次分组渲染 |
| 7.16 | 前端：`components/SensitiveFieldDisplay.vue` 脱敏展示 | pms-frontend/components | 脱敏值带 ⓘ 提示 |
| 7.17 | **Story 6 验收测试** | 全栈 | 见验收场景 6.1 + 6.2 |

**Phase 7 里程碑**：Story 6 两个验收场景全部通过。

#### Phase 8：联调测试与验收

| # | 任务 | 模块 | 验证 |
|---|---|---|---|
| 8.1 | 编写 V72 迁移脚本（演示数据 + 权限菜单 + 字典数据） | pms-admin/migration | 数据插入成功 |
| 8.2 | 重置数据库，全量执行 V1~V72 迁移 | 沙盒 | 72 条迁移记录全部成功 |
| 8.3 | 后端全量编译 + 打包 | pms-admin | `mvn clean package` 成功 |
| 8.4 | 前端 TypeScript 类型检查 | pms-frontend | `vue-tsc --noEmit` 无错误 |
| 8.5 | 前端构建 | pms-frontend | `npm run build` 成功 |
| 8.6 | 端到端验收场景 1（Story 1）：从模板创建项目 | 全栈 | 验证 1.1 + 1.2 |
| 8.7 | 端到端验收场景 2（Story 2）：阶段推进 + 关闭主项目 | 全栈 | 验证 2.1 + 2.2 |
| 8.8 | 端到端验收场景 3（Story 3）：强制检查项 + 进度汇总 | 全栈 | 验证 3.1 + 3.2 |
| 8.9 | 端到端验收场景 4（Story 4）：循环依赖 + 基线偏差 | 全栈 | 验证 4.1 + 4.2 |
| 8.10 | 端到端验收场景 5（Story 5）：交付件修订 + 阶段校验 | 全栈 | 验证 5.1 + 5.2 |
| 8.11 | 端到端验收场景 6（Story 6）：字段脱敏 + 历史保留 | 全栈 | 验证 6.1 + 6.2 |
| 8.12 | 接口连通性全量验证（新增端点 ~70 个） | 全栈 | 全部返回 200 |
| 8.13 | SPA 路由全量验证（50 个路由） | pms-frontend | 全部页面可达 |
| 8.14 | 权限菜单与字典数据验证 | 全栈 | 菜单显示正确，字典渲染正常 |
| 8.15 | 演示数据完整性验证 | 全栈 | 各表数据 ≥ 预期条数 |

**Phase 8 里程碑**：6 个故事 12 个验收场景全部通过，系统可交付。

### 8.3 里程碑汇总

| Phase | 内容 | 验收场景 | 状态门槛 |
|---|---|---|---|
| 1 | 基础设施 + 路由重构 | - | 迁移成功，前端可访问 |
| 2 | 项目模板与创建 | Story 1 (1.1, 1.2) | 模板创建项目，含完整默认计划 |
| 3 | 项目生命周期与主子项目 | Story 2 (2.1, 2.2) | 阶段推进拦截 + 关闭主项目拦截 |
| 4 | 任务体系与团队协作 | Story 3 (3.1, 3.2) | 强制检查项拦截 + 进度汇总 |
| 5 | 依赖与基线 | Story 4 (4.1, 4.2) | 循环依赖检测 + 基线偏差审批 |
| 6 | 交付件全生命周期 | Story 5 (5.1, 5.2) | 修订新版本 + 阶段必需交付件校验 |
| 7 | 统一审批中心 | Story 6 (6.1, 6.2) | 字段脱敏 + 历史保留 |
| 8 | 联调测试与验收 | 全部 12 个场景 | 全栈通过 |

### 8.4 风险与缓解

| 风险 | 概率 | 影响 | 缓解措施 |
|---|---|---|---|
| Flyway checksum 不匹配 | 中 | 高 | V64~V72 全新脚本，开发期可重置数据库 |
| 递归 CTE 在大数据量下性能差 | 低 | 中 | 物化路径 `task_path` 索引优化，`LIKE '/12/%'` 查询 |
| G6 v5 API 变更风险 | 中 | 低 | 锁定 `@antv/g6@^5.0.0`，参考官方文档 |
| 嵌套路由重构影响既有页面 | 中 | 高 | 添加 URL 重定向，全量页面可达性测试 |
| Spring Event 异步汇总丢消息 | 低 | 中 | 关键事件同步发布 + 重试机制 |
| 7 态状态机流转逻辑错误 | 中 | 高 | 单元测试覆盖所有合法/非法转换 |
| Flowable 集成冲突 | 低 | 高 | 复用现有 BPMN 配置，仅扩展业务层 |

### 8.5 测试策略

| 层次 | 工具 | 覆盖目标 |
|---|---|---|
| **单元测试** | JUnit 4 + Mockito | 服务层核心逻辑（循环检测、状态机、汇总算法） |
| **集成测试** | Spring Boot Test | Controller 端点连通性、事务边界 |
| **前端单元测试** | Vitest | API 封装、关键组件（TaskTree、SensitiveFieldDisplay） |
| **类型检查** | `vue-tsc --noEmit` | 全部 .ts/.vue 文件 |
| **端到端验收** | 手动 + 脚本 | 12 个 Given-When-Then 场景 |
| **接口连通性** | curl 脚本 | 新增 ~70 个端点 |
| **SPA 路由** | curl HTML 检查 | 50 个路由页面可达 |

### 8.6 交付物清单

| 交付物 | 路径 | 说明 |
|---|---|---|
| 设计文档 | `docs/superpowers/specs/2026-07-17-project-management-enhancement-design.md` | 本设计文档 |
| 实现计划 | `docs/superpowers/plans/2026-07-17-project-management-enhancement-plan.md` | writing-plans skill 生成 |
| 迁移脚本 | `pms-admin/src/main/resources/db/migration/V64~V72` | 9 个 SQL 文件 |
| 后端代码 | `pms-project`、`pms-implementation`、`pms-deliverable`、`pms-baseline`、`pms-workflow` | 新增/扩展模块 |
| 前端代码 | `pms-frontend/src/api/*.ts`、`pms-frontend/src/views/**/*.vue`、`pms-frontend/src/components/*.vue` | API + 视图 + 组件 |
| 路由重构 | `pms-frontend/src/router/index.ts` | 嵌套路由全量重构 |
| 权限菜单 | `pms_menu`、`pms_permission` 表种子数据 | V72 含 |
| 字典数据 | `pms_dict_type`、`pms_dict_data` 表种子数据 | V72 含 |
| 演示数据 | 8 类业务演示数据 | V72 含 |

---

## 9. 附录

### 9.1 验收场景对照表

| 用户故事 | 验收场景 | 关键 API | 关键页面 |
|---|---|---|---|
| Story 1 - 验收 1 | 从模板创建项目，验证完整默认计划 | `POST /api/project/template/create-project` | `project/list` → `ProjectTemplateSelector` → `project/detail` |
| Story 1 - 验收 2 | 模板新版本不影响存量项目 | 模板版本表 `snapshot_json` 深拷贝 | `project/template/version` |
| Story 2 - 验收 1 | 阶段推进被阻止，列出未完成事项 | `POST /api/project/phase/{id}/advance` | `project/detail` (阶段 Tab) |
| Story 2 - 验收 2 | 关闭主项目被拒绝，展示子项目 | `POST /api/project/{id}/close` | `project/detail` (子项目 Tab) |
| Story 3 - 验收 1 | 强制检查项拦截 | `POST /api/implementation/task/{id}/submit-review` | `task/detail` (检查项 Tab) |
| Story 3 - 验收 2 | 子任务进度变化，父任务/项目汇总 | `GET /api/implementation/task/{id}/progress` | `task/list` (树形) |
| Story 4 - 验收 1 | 循环依赖被拒，指出闭环路径 | `POST /api/implementation/task/dependency` | `task/dependency` (G6 图) |
| Story 4 - 验收 2 | 关键日期变更展示差异，记录原因 | `POST /api/baseline/{id}/request-change` | `baseline/diff` |
| Story 5 - 验收 1 | 修订创建新版本，不覆盖原版本 | `POST /api/deliverable/{id}/revise` | `deliverable/detail` (版本历史 Tab) |
| Story 5 - 验收 2 | 必需交付件未批准，阻止阶段完成 | `GET /api/deliverable/phase/{phaseId}/validate` | `project/detail` (阶段 Tab) |
| Story 6 - 验收 1 | 无敏感字段权限，仅展示授权信息 | `GET /api/workflow/approval/{id}` | `workflow/approval-detail` |
| Story 6 - 验收 2 | 退回后重新提交，保留全部前序记录 | `POST /api/workflow/approval/{id}/resubmit` | `workflow/approval-history` |

### 9.2 术语表

| 术语 | 含义 |
|---|---|
| Phase | 项目阶段，模板驱动的独立实体 |
| PhaseExitGate | 阶段退出条件，结构化 JSON |
| BaselineSnapshot | 计划基线快照，含全部任务计划日期 |
| TaskDependency | 任务依赖关系（FS/FF/SS/SF + lag） |
| DeliverableVersion | 交付件版本，不可变 |
| ApprovalRecord | 统一审批记录，含轮次字段 |
| ApprovalFieldPermission | 审批敏感字段权限 |
| task_path / project_path | 物化路径，用于无限嵌套查询 |
| Spring Event | 跨模块事件机制 |
| Flowable | 现有工作流引擎 |

### 9.3 参考资料

- 现有项目结构：`/workspace/network-equipment-pms/`
- Flyway 迁移历史：`pms-admin/src/main/resources/db/migration/V1~V63`
- BaseEntity 公共字段：`com.dp.plat.common.entity.BaseEntity`
- 通用附件表：`pms_attachment`（V21，biz_type + biz_id 模式）
- Flowable 工作流：`pms-workflow` 模块，5 个 BPMN 流程定义
- MyBatis Plus JacksonTypeHandler：`com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler`

---

**文档结束**
