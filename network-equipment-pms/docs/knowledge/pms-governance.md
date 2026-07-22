# pms-governance 模块知识库

> 本文基于 `network-equipment-pms/pms-governance` 模块源码（`com.dp.plat.governance`）整理，记录项目治理"三本账"领域（变更请求 / 风险登记册 / 问题日志）的实体模型、状态机、三账联动机制、CCB 审批工作流集成、5×5 风险矩阵、基线变更审计等核心机制。

## 模块概述

`pms-governance` 是网络设备 PMS 平台的**项目治理领域模块**，落地 PMBOK 经典的"三本账"（governance three-books）治理实践，承担三项核心职责：

1. **变更请求管理（Change Request Book）** — 维护项目正式变更请求的全生命周期（提交 → CCB 审核 → 批准/驳回 → 实施 → 关闭），审批通过时自动记录三维度基线变更（进度 / 成本 / 范围）历史，并通过 BPMN 流程驱动 CCB 审核。
2. **风险登记册管理（Risk Register）** — 维护项目已识别风险，按"可能性 × 影响"自动计算风险评分（1-25）与优先级（LOW / MEDIUM / HIGH），提供 5×5 风险矩阵视图，支持风险已发生转化为问题、风险升级为变更请求。
3. **问题日志管理（Issue Log）** — 维护项目执行过程中产生的问题，支持分配处理人 → 解决 → 关闭的流转，并支持问题升级为变更请求。

- **Maven 坐标**：`com.dp.plat:pms-governance:1.0.0-SNAPSHOT`，父工程为 `com.dp.plat:network-equipment-pms`。
- **artifactId / name**：`pms-governance`，`<description>` 为 `Governance three-books: change request, risk register, issue log`。
- **基础包名**：`com.dp.plat.governance`。
- **核心定位**：治理域的"三本账 + 一张矩阵" —— `change` 子包驱动 CCB 审批工作流并审计基线变更，`risk` 子包计算风险评分并暴露矩阵视图，`issue` 子包记录执行问题。三账通过显式调用形成闭环联动：风险 → 问题、风险 → 变更请求、问题 → 变更请求。

## 包结构

```
com.dp.plat.governance
├── change/                            # 变更请求子域（变更请求 + 基线变更历史）
│   ├── controller/
│   │   └── ChangeRequestController.java
│   ├── entity/
│   │   ├── ChangeRequest.java         # @TableName("pms_change_request")
│   │   └── BaselineHistory.java       # @TableName("pms_baseline_history")
│   ├── mapper/
│   │   ├── ChangeRequestMapper.java
│   │   └── BaselineHistoryMapper.java
│   └── service/
│       ├── IChangeRequestService.java
│       ├── IBaselineHistoryService.java
│       └── impl/
│           ├── ChangeRequestServiceImpl.java
│           └── BaselineHistoryServiceImpl.java
├── issue/                             # 问题日志子域
│   ├── controller/
│   │   └── IssueController.java
│   ├── entity/
│   │   └── Issue.java                 # @TableName("pms_issue")
│   ├── mapper/
│   │   └── IssueMapper.java
│   └── service/
│       ├── IIssueService.java
│       └── impl/
│           └── IssueServiceImpl.java
└── risk/                              # 风险登记册子域
    ├── controller/
    │   └── RiskController.java
    ├── dto/
    │   └── RiskMatrixDto.java         # 5×5 风险矩阵响应 DTO
    ├── entity/
    │   └── Risk.java                  # @TableName("pms_risk")
    ├── mapper/
    │   └── RiskMapper.java
    └── service/
        ├── IRiskService.java
        └── impl/
            └── RiskServiceImpl.java
```

各包职责说明：

| 包 | 主要类型 | 职责 |
|----|----------|------|
| `change.controller` | `ChangeRequestController` | 暴露变更请求管理 REST API（`/api/governance/change-request`），含 CCB 审批端点 |
| `change.entity` | `ChangeRequest` / `BaselineHistory` | 变更请求主实体 + 基线变更审计实体 |
| `change.mapper` | 2 个 `BaseMapper` 子接口 | MyBatis-Plus 标准 CRUD，无自定义 SQL |
| `change.service` / `impl` | 2 接口 + 2 实现 | 变更请求生命周期管理 + 基线变更审计记录 |
| `issue.controller` | `IssueController` | 暴露问题日志管理 REST API（`/api/governance/issue`） |
| `issue.entity` | `Issue` | 问题日志实体，支持来源风险 / 来源变更的双向追溯字段 |
| `issue.mapper` | `IssueMapper` | MyBatis-Plus 标准 CRUD |
| `issue.service` / `impl` | 1 接口 + 1 实现 | 问题日志生命周期管理 + 升级为变更请求 |
| `risk.controller` | `RiskController` | 暴露风险登记册管理 REST API（`/api/governance/risk`），含 5×5 矩阵端点 |
| `risk.dto` | `RiskMatrixDto` | 5×5 风险矩阵响应结构 |
| `risk.entity` | `Risk` | 风险实体，含 likelihood / impact / score / priority / mitigation / status |
| `risk.mapper` | `RiskMapper` | MyBatis-Plus 标准 CRUD |
| `risk.service` / `impl` | 1 接口 + 1 实现 | 风险评分自动计算 + 三账联动（转化为问题、升级为变更请求）+ 矩阵构建 |

资源目录：

```
src/main/resources/
└── processes/
    └── change-request-approval.bpmn20.xml   # CCB 审批 BPMN 流程定义
```

## 核心实体模型（表格）

模块共 4 个持久化实体，全部继承 `com.dp.plat.common.entity.BaseEntity`（公共字段：`id`、`createTime`、`updateTime`、`createBy`、`updateBy`、`deleted`（`@TableLogic` 逻辑删除））。

### 实体清单

| 实体 | 表名 | 中文含义 | 乐观锁 | 关键关系 |
|------|------|----------|--------|----------|
| `ChangeRequest` | `pms_change_request` | 变更请求 | `@Version` | N:1 → `Project`（projectId）；包含 `processInstanceId` 关联 Flowable 流程实例 |
| `BaselineHistory` | `pms_baseline_history` | 基线变更历史 | — | N:1 → `Project`（projectId）；N:1 → `ChangeRequest`（changeRequestId / crNo 冗余） |
| `Issue` | `pms_issue` | 问题日志 | — | N:1 → `Project`（projectId）；可追溯 `sourceRiskId` / `sourceRiskNo`（来自风险）或 `sourceChangeId` / `sourceCrNo`（来自变更） |
| `Risk` | `pms_risk` | 风险 | — | N:1 → `Project`（projectId）；可追溯 `sourceIssueId`（由问题转化） |

### ChangeRequest 字段详解

`ChangeRequest` 对应表 `pms_change_request`，记录正式变更请求。

| 字段 | 类型 | 校验 | 说明 |
|------|------|------|------|
| `crNo` | `String` | `@NotBlank` `@Size(max=50)` | 变更单号，格式 `CR-YYYY-XXXX`，由 `generateCrNo()` 自动生成 |
| `projectId` | `Long` | `@NotNull("项目ID不能为空")` | 所属项目ID |
| `projectName` | `String` | `@Size(max=200)` | 项目名称（冗余字段，便于展示） |
| `title` | `String` | `@NotBlank` `@Size(max=200)` | 变更标题 |
| `description` | `String` | `@NotBlank` `@Size(max=2000)` | 变更描述 |
| `requesterId` | `Long` | — | 请求人用户ID |
| `requesterName` | `String` | `@Size(max=50)` | 请求人姓名（冗余） |
| `requestDate` | `LocalDate` | — | 请求日期，创建时缺省为当天 |
| `impactScope` | `String` | `@Size(max=2000)` | 影响范围（TEXT） |
| `impactSchedule` | `String` | `@Size(max=500)` | 进度影响 |
| `impactCost` | `String` | `@Size(max=500)` | 成本影响 |
| `impactQuality` | `String` | `@Size(max=500)` | 质量影响 |
| `priority` | `String` | `@NotBlank` `@Size(max=20)` | 优先级：`LOW` / `MEDIUM` / `HIGH` / `CRITICAL`，创建时缺省 `MEDIUM` |
| `status` | `String` | `@Size(max=50)` | 状态机：`SUBMITTED` / `UNDER_REVIEW` / `CCB_APPROVED` / `CCB_REJECTED` / `IMPLEMENTING` / `CLOSED` |
| `approverId` | `Long` | — | 审批人用户ID |
| `approverName` | `String` | `@Size(max=50)` | 审批人姓名 |
| `processInstanceId` | `String` | — | 工作流流程实例ID（关联 Flowable `ACT_RU_EXECUTION`） |
| `baselineUpdated` | `Boolean` | `@Builder.Default(false)` | 项目基线是否已更新（CCB 审批通过后置 `true`） |
| `approvedAt` | `LocalDateTime` | — | 审批时间（批准或驳回时填充） |
| `closedAt` | `LocalDateTime` | — | 关闭时间 |
| `version` | `Integer` | `@Version` | 乐观锁版本号（MyBatis-Plus） |

### BaselineHistory 字段详解

`BaselineHistory` 对应表 `pms_baseline_history`，记录由变更请求审批触发的字段级基线变更审计。

| 字段 | 类型 | 说明 |
|------|------|------|
| `projectId` | `Long` | 所属项目ID |
| `changeRequestId` | `Long` | 触发基线变更的变更请求ID |
| `crNo` | `String` | 变更单号（冗余，便于追溯） |
| `changeType` | `String` | 变更类型：`SCHEDULE` / `COST` / `SCOPE` |
| `fieldName` | `String` | 变更字段名（如 `impactSchedule` / `impactCost` / `impactScope`） |
| `oldValue` | `String` | 变更前的旧值 |
| `newValue` | `String` | 变更后的新值 |
| `description` | `String` | 变更描述（自动拼接为 `"<fieldName> 由 <old> 变更为 <new>"`） |
| `changedAt` | `LocalDateTime` | 基线变更时间 |
| `changedBy` | `String` | 变更操作人（来自 `SecurityUtils.getCurrentUsername()`） |

### Issue 字段详解

`Issue` 对应表 `pms_issue`，记录项目执行问题。

| 字段 | 类型 | 校验 | 说明 |
|------|------|------|------|
| `issueNo` | `String` | `@NotBlank` `@Size(max=50)` | 问题编号，格式 `ISSUE-YYYY-XXXX`，由 `generateIssueNo()` 自动生成 |
| `projectId` | `Long` | `@NotNull` | 所属项目ID |
| `description` | `String` | `@NotBlank` `@Size(max=2000)` | 问题描述 |
| `raisedBy` | `Long` | — | 提出人用户ID |
| `raisedByName` | `String` | `@Size(max=50)` | 提出人姓名 |
| `assigneeId` | `Long` | — | 处理人用户ID |
| `assigneeName` | `String` | `@Size(max=50)` | 处理人姓名 |
| `priority` | `String` | `@NotBlank` `@Size(max=20)` | 优先级：`LOW` / `MEDIUM` / `HIGH` / `CRITICAL`，缺省 `MEDIUM` |
| `targetResolveDate` | `LocalDate` | — | 目标解决日期，创建时缺省为当天 +7 天 |
| `status` | `String` | `@Size(max=50)` | 状态机：`OPEN` / `IN_PROGRESS` / `RESOLVED` / `CLOSED` |
| `sourceRiskId` | `Long` | — | 来源风险ID（由风险转化时填充） |
| `sourceRiskNo` | `String` | `@Size(max=50)` | 来源风险编号 |
| `sourceChangeId` | `Long` | — | 来源变更请求ID（追溯字段） |
| `sourceCrNo` | `String` | `@Size(max=50)` | 来源变更单号 |
| `resolvedAt` | `LocalDateTime` | — | 解决时间 |
| `closedAt` | `LocalDateTime` | — | 关闭时间 |
| `resolution` | `String` | `@Size(max=2000)` | 解决方案（TEXT） |

### Risk 字段详解

`Risk` 对应表 `pms_risk`，记录项目已识别风险。

| 字段 | 类型 | 校验 | 说明 |
|------|------|------|------|
| `riskNo` | `String` | `@NotBlank` `@Size(max=50)` | 风险编号，格式 `RISK-YYYY-XXXX`，由 `generateRiskNo()` 自动生成 |
| `projectId` | `Long` | `@NotNull` | 所属项目ID |
| `description` | `String` | `@NotBlank` `@Size(max=2000)` | 风险描述 |
| `category` | `String` | `@Size(max=50)` | 风险类别：`TECHNICAL` / `EXTERNAL` / `ORGANIZATIONAL` / `PM` |
| `likelihood` | `Integer` | `@NotNull` `@Min(1)` `@Max(5)` | 可能性评分（1-5） |
| `impact` | `Integer` | `@NotNull` `@Min(1)` `@Max(5)` | 影响评分（1-5） |
| `score` | `Integer` | `@Min(1)` `@Max(25)` | 风险评分（`likelihood * impact`，由 `computeScore` 自动计算） |
| `priority` | `String` | `@Size(max=20)` | 优先级：`LOW` / `MEDIUM` / `HIGH`（由评分自动分档） |
| `mitigation` | `String` | `@Size(max=50)` | 缓解策略：`AVOID` / `MITIGATE` / `TRANSFER` / `ACCEPT` |
| `contingencyPlan` | `String` | `@Size(max=2000)` | 应急预案（TEXT） |
| `ownerId` | `Long` | — | 风险负责人用户ID |
| `ownerName` | `String` | `@Size(max=50)` | 风险负责人姓名 |
| `status` | `String` | `@Size(max=50)` | 状态机：`OPEN` / `IN_PROGRESS` / `CLOSED` / `ESCALATED` |
| `reviewDate` | `LocalDate` | — | 下次复审日期 |
| `sourceIssueId` | `Long` | — | 来源问题ID（由问题转化时填充，反向追溯） |
| `identifiedAt` | `LocalDateTime` | — | 风险识别时间 |
| `closedAt` | `LocalDateTime` | — | 风险关闭时间 |

## 治理功能

模块按"三本账"组织为三个子域，每个子域有独立的状态机与编号生成器，并通过显式调用实现联动。

### 1. 变更请求治理（Change Request）

#### 1.1 状态机

变更请求遵循 CCB（Change Control Board，变更控制委员会）审批流程的六态状态机：

```
              create                       submit
              ──────►  SUBMITTED  ──────────────►  UNDER_REVIEW
                       │                            │
                       │                            │ approve
                       │                            ▼
                       │                       CCB_APPROVED
                       │                            │
                       │                            │ implement
                       │                            ▼
                       │                       IMPLEMENTING
                       │                            │
                       │                            │ close
                       │                            ▼
                       │                          CLOSED
                       │                            ▲
                       │ reject                     │
                       └─────────►  CCB_REJECTED  ──┘ (close 可从任意状态转入)
```

**关键状态转换校验**（`ChangeRequestServiceImpl`）：

| 操作 | 起始状态 | 目标状态 | 校验 |
|------|----------|----------|------|
| `create` | — | `SUBMITTED` | 自动填充 `crNo`、`priority=MEDIUM`（缺省）、`requestDate=今天`（缺省）、`baselineUpdated=false`（缺省） |
| `submit` | `SUBMITTED` | `UNDER_REVIEW` | 非 `SUBMITTED` 抛 `BusinessException("当前变更请求状态不允许提交")`；同时启动 CCB 审批工作流 |
| `approve` | `UNDER_REVIEW` | `CCB_APPROVED` | 非 `UNDER_REVIEW` 抛异常；同时记录三维度基线变更 + 完成工作流审核任务（`approved=true`） |
| `reject` | `UNDER_REVIEW` | `CCB_REJECTED` | 非 `UNDER_REVIEW` 抛异常；同时完成工作流审核任务（`approved=false`，含驳回原因） |
| `implement` | `CCB_APPROVED` | `IMPLEMENTING` | 非 `CCB_APPROVED` 抛异常 |
| `close` | 任意 | `CLOSED` | 无状态校验，填充 `closedAt` |

#### 1.2 CCB 审批与基线变更审计

`approve(id, approverName)` 在审批通过时执行三步联动：

1. 状态置 `CCB_APPROVED`，填充 `approverName` 与 `approvedAt`。
2. **三维度基线变更审计** — 调用 `recordBaselineChanges(cr, currentUser)`，对 `impactSchedule` / `impactCost` / `impactScope` 三个非空字段分别记录一条 `BaselineHistory`：
   - `changeType` 分别为 `SCHEDULE` / `COST` / `SCOPE`
   - `fieldName` 分别为 `impactSchedule` / `impactCost` / `impactScope`
   - `oldValue` 固定为 `"原进度基线"` / `"原成本基线"` / `"原范围基线"`
   - `newValue` 为对应字段值
   - `changedBy` 来自 `SecurityUtils.getCurrentUsername()`
   - 字段为空时跳过该维度（不记录审计）
3. `baselineUpdated` 置 `true`，调用 `updateById` 持久化。
4. **完成工作流审核任务** — 调用 `completeReviewTask(cr, true, "CCB审批通过")`。

#### 1.3 编号生成

`generateCrNo()` 生成格式 `CR-YYYY-XXXX`：
- `prefix = "CR-" + year + "-"`
- `count = this.count(LambdaQueryWrapper.likeRight(crNo, prefix))` —— 按前缀右模糊查询当年已有变更请求总数
- `sequence = count + 1`
- 返回 `prefix + String.format("%04d", sequence)` —— 4 位零填充序号

> 该实现存在并发风险：两个并发请求可能读到同一 `count` 值生成重复编号。生产环境建议加分布式锁或数据库唯一索引约束。

### 2. 风险治理（Risk Register）

#### 2.1 状态机

风险状态机为四态：

```
            create                  markOccurred
            ──────►  OPEN  ──────────────────►  CLOSED
                     │                              ▲
                     │ escalate                     │
                     ▼                              │
                 ESCALATED                          │
                     │                              │
                     │ (escalate 内部不主动 close)   │
                     └─────────────►  变更请求  ────┘
```

**关键状态转换**（`RiskServiceImpl`）：

| 操作 | 起始状态 | 目标状态 | 说明 |
|------|----------|----------|------|
| `create` | — | `OPEN` | 自动填充 `riskNo`、`score`、`priority`、`identifiedAt` |
| `update` | 任意 | 不变 | 重新计算 `score` / `priority` |
| `markOccurred` | 任意 | `CLOSED` | 转化为新 `Issue` 并关闭风险，填充 `closedAt` |
| `escalate` | 任意 | `ESCALATED` | 创建新 `ChangeRequest` 并标记风险为已升级 |

#### 2.2 风险评分与优先级自动计算

`computeScore(Risk risk)` 在 `create` / `update` 时自动调用：

```
int l = clamp(likelihood);    // 限制到 [1, 5]
int i = clamp(impact);        // 限制到 [1, 5]
int score = l * i;            // 1-25
risk.setScore(score);
risk.setPriority(
    score <= 6  ? "LOW" :     // 1-6
    score <= 12 ? "MEDIUM" :  // 7-12
                 "HIGH"       // 13-25
);
```

`clamp(value)` 将越界值钳制到 `[1, 5]` 区间（防御性处理，配合实体上的 `@Min(1)` / `@Max(5)` 校验形成双层保护）。

> 评分阈值在 `RiskServiceImpl` 中以常量形式硬编码：`PRIORITY_LOW` 阈值 6、`PRIORITY_MEDIUM` 阈值 12、`PRIORITY_HIGH` 上限 25。如需配置化，可参考 `pms-baseline` 模块通过 `ProjectConfigService` 读取阈值的做法。

#### 2.3 5×5 风险矩阵

`riskMatrix(Long projectId)` 构建项目级 5×5 风险矩阵视图：

1. 查询项目下全部风险（`projectId == null` 时查询全部）。
2. 构造 5×5 二维 `List<List<Integer>>`，索引方式 `matrix[likelihood-1][impact-1] = count`。
3. 遍历风险，对每个有 `likelihood` / `impact` 的风险：
   - 在矩阵对应单元格 `+1`
   - 若 `likelihood * impact > 12` 或 `priority == "HIGH"`，`highPriorityCount++`
4. 构造 `RiskMatrixDto` 返回。

> 兼容性设计：`highPriorityCount` 统计同时考虑评分阈值（`likelihood * impact > 12`）与显式 `HIGH` 优先级，兼容历史数据中 `priority` 未按最新评分回填的场景。

#### 2.4 编号生成

`generateRiskNo()` 生成格式 `RISK-YYYY-XXXX`，算法与 `generateCrNo()` 一致。

### 3. 问题日志治理（Issue Log）

#### 3.1 状态机

问题状态机为四态：

```
        create              assign                resolve              close
        ──────►  OPEN  ──────────►  IN_PROGRESS  ──────────►  RESOLVED  ──────────►  CLOSED
                 │                                                            ▲
                 │ escalate                                                   │
                 └────────────────────►  变更请求  ───────────────────────────┘
```

**关键状态转换**（`IssueServiceImpl`）：

| 操作 | 起始状态 | 目标状态 | 说明 |
|------|----------|----------|------|
| `create` | — | `OPEN` | 自动填充 `issueNo`、`priority=MEDIUM`（缺省）、`targetResolveDate=今天+7天`（缺省） |
| `assign` | `OPEN` | `IN_PROGRESS` | 自动转换；非 `OPEN` 状态仅更新 `assigneeId/Name` |
| `resolve` | 任意 | `RESOLVED` | 填充 `resolution` 与 `resolvedAt` |
| `close` | 任意 | `CLOSED` | 填充 `closedAt` |
| `escalate` | 不变 | 不变 | 创建新 `ChangeRequest`，问题本身状态不改变 |

> `assign` 是隐式状态转换：分配处理人时若问题处于 `OPEN`，自动转为 `IN_PROGRESS`，无需单独调用状态推进接口。

#### 3.2 编号生成

`generateIssueNo()` 生成格式 `ISSUE-YYYY-XXXX`，算法与 `generateCrNo()` 一致。

### 4. 三账联动机制

三本账之间通过显式 Service 调用形成单向无环依赖链：

```
   Risk ─── markOccurred ──► Issue
     │                          
     ├── escalate ──► ChangeRequest
     │                    ▲
     │                    │
   Issue ── escalate ─────┘
```

依赖图（`RiskServiceImpl` 注入 `IIssueService` 与 `IChangeRequestService`，`IssueServiceImpl` 注入 `IChangeRequestService`）：

```
risk → issue → change request
       └─────────────────────► change request
```

**联动操作明细**：

| 源 | 操作 | 目标 | 实现细节 |
|----|------|------|----------|
| `Risk` | `markOccurred(id)` | 创建新 `Issue` + 关闭 `Risk` | `Issue` 的 `description` 拼接为 `"由风险 <riskNo> 转化: <riskDescription>"`，`sourceRiskId` / `sourceRiskNo` 回填；`Risk.status = CLOSED`、`closedAt = now` |
| `Risk` | `escalate(id)` | 创建新 `ChangeRequest` + `Risk` 标记 `ESCALATED` | `ChangeRequest` 的 `title = "由<riskNo>升级的变更请求"`，`description = "由风险 <riskNo> 升级: <riskDescription>"`，`requesterId/Name` 取自 `Risk.ownerId/Name`；`Risk.status = ESCALATED` |
| `Issue` | `escalate(id)` | 创建新 `ChangeRequest` | `ChangeRequest` 的 `title = "由<issueNo>升级的变更请求"`，`description = "由问题 <issueNo> 升级: <issueDescription>"`，`requesterId/Name` 取自 `Issue.raisedBy/Name` |

**循环依赖规避**：依赖图为 `risk → issue → change request` 的单向链，`ChangeRequestServiceImpl` 不反向依赖 `Issue` / `Risk`，因此无循环依赖。

## BPMN 流程

模块在 `src/main/resources/processes/` 下定义了 1 个 BPMN 流程，供 `pms-workflow` 模块的 Flowable 引擎自动部署与执行。

### change-request-approval.bpmn20.xml

- **流程 ID**：`changeRequestApproval`
- **流程名称**：`变更请求CCB审批流程`
- **targetNamespace**：`http://flowable.org/bpmn`
- **isExecutable**：`true`

**节点结构**：

| 节点 ID | 类型 | 名称 | 说明 |
|---------|------|------|------|
| `startEvent` | startEvent | 请求人提交 | 流程入口 |
| `ccbReview` | userTask | CCB审核 | `flowable:assignee="${ccbApproverId}"`，由流程变量 `ccbApproverId` 指定审批人；`flowable:skipExpression="${assignee == initiator}"` 跳过审批人与发起人相同的情况 |
| `ccbDecisionGateway` | exclusiveGateway | CCB审批决定 | 排他网关，根据 `approved` 变量路由 |
| `approvedEnd` | endEvent | CCB审批通过 | 正常结束事件 |
| `rejectedEnd` | endEvent | CCB驳回终止 | `terminateEventDefinition` 终止结束事件，立即终止流程实例所有分支 |

**控制流**：
- `flow1`：`startEvent` → `ccbReview`
- `flow2`：`ccbReview` → `ccbDecisionGateway`
- `approveFlow`：`ccbDecisionGateway` → `approvedEnd`，条件 `${approved}`
- `rejectFlow`：`ccbDecisionGateway` → `rejectedEnd`，条件 `${!approved}`

**流程变量**（由 `ChangeRequestServiceImpl.startApprovalWorkflow` 设置）：

| 变量名 | 类型 | 来源 | 用途 |
|--------|------|------|------|
| `ccbApproverId` | Long | `ChangeRequest.approverId` | `userTask` 的 `assignee` |
| `requesterId` | Long | `ChangeRequest.requesterId` | 业务追溯 |
| `projectId` | Long | `ChangeRequest.projectId` | 业务追溯 |
| `approved` | Boolean | `completeReviewTask` 设置 | 网关路由条件，`approve` 时 `true`，`reject` 时 `false` |

**业务键**：`crNo`（变更单号），由 `StartProcessRequest.businessKey` 传入，便于工作流侧按业务键反查流程实例。

### 工作流集成模式

`ChangeRequestServiceImpl` 通过 `ObjectProvider<WorkflowService>` 注入工作流服务，实现模块解耦：

```java
private final ObjectProvider<WorkflowService> workflowServiceProvider;
```

**关键设计**：

1. **`ObjectProvider` 而非直接 `@Autowired`** —— 当 `pms-workflow` 模块未加载或 `WorkflowService` Bean 未注册时，`getIfAvailable()` 返回 `null` 而不抛 `NoSuchBeanDefinitionException`，治理模块可独立运行。
2. **工作流失败不阻断业务** —— `startApprovalWorkflow` 与 `completeReviewTask` 均以 `try-catch` 包裹，异常时 `log.error` 记录但事务不回滚，变更请求的状态推进不受影响。
3. **`processInstanceId` 缺失则跳过任务完成** —— `completeReviewTask` 在 `processInstanceId` 为空时直接 `return`，避免无流程实例时调用工作流 API。

**工作流交互方法**：

| 治理侧方法 | 调用工作流 API | 用途 |
|------------|----------------|------|
| `startApprovalWorkflow` | `WorkflowService.startProcess(StartProcessRequest)` | 启动 CCB 审批流程，回填 `processInstanceId` |
| `completeReviewTask` | `WorkflowService.getTodoTasks(1, 200)` → `WorkflowService.completeTask(CompleteTaskRequest)` | 查找当前待办任务并完成（含 `approved` 变量与 comment） |

**待办任务查找算法**（`findCurrentTaskId`）：
1. 调用 `workflowService.getTodoTasks(1, 200)` 获取当前用户待办任务（页大小 200）。
2. 从返回的 `records` 列表中查找 `TaskDTO.processInstanceId` 等于当前变更请求 `processInstanceId` 的任务。
3. 返回该任务 ID；未找到时返回 `null`，`completeReviewTask` 跳过完成操作并 `log.warn`。

> 当前实现存在局限：`getTodoTasks` 返回的是"当前登录用户的待办"，而 CCB 审批人未必是当前登录用户。在审批人 ≠ 操作人场景下可能找不到任务。生产环境建议改为按 `processInstanceId` 直接查询活动任务（`TaskService.createTaskQuery().processInstanceId(...).singleResult()`）。

## Service 层与 API 端点

### IChangeRequestService 接口

`IChangeRequestService extends IService<ChangeRequest>`（MyBatis-Plus 标准 CRUD）：

| 方法 | 签名 | 说明 |
|------|------|------|
| `create` | `Result<ChangeRequest> create(ChangeRequest)` | 创建变更请求（`SUBMITTED` 状态），自动生成 `crNo`、缺省 `priority`/`requestDate`/`baselineUpdated` |
| `update` | `Result<?> update(ChangeRequest)` | 更新变更请求，存在性校验 |
| `delete` | `Result<?> delete(Long id)` | 删除变更请求（逻辑删除），存在性校验 |
| `listAll` | `Result<List<ChangeRequest>> listAll()` | 查询全部变更请求（按 `createTime` 倒序） |
| `getById` | `Result<ChangeRequest> getById(Long id)` | 按ID查询，不存在抛 `BusinessException` |
| `listByProject` | `Result<List<ChangeRequest>> listByProject(Long projectId)` | 按项目ID查询，`projectId` 为 `null` 时返回空列表 |
| `submit` | `Result<ChangeRequest> submit(Long id)` | 提交审批：`SUBMITTED` → `UNDER_REVIEW`，启动 CCB 工作流 |
| `approve` | `Result<ChangeRequest> approve(Long id, String approverName)` | CCB 审批通过：`UNDER_REVIEW` → `CCB_APPROVED`，记录基线变更 + 完成工作流任务 |
| `reject` | `Result<ChangeRequest> reject(Long id, String reason)` | CCB 驳回：`UNDER_REVIEW` → `CCB_REJECTED`，完成工作流任务（含驳回原因） |
| `implement` | `Result<ChangeRequest> implement(Long id)` | 开始实施：`CCB_APPROVED` → `IMPLEMENTING` |
| `close` | `Result<ChangeRequest> close(Long id)` | 关闭：任意 → `CLOSED`，填充 `closedAt` |
| `generateCrNo` | `String generateCrNo()` | 生成变更单号 `CR-YYYY-XXXX` |

### IBaselineHistoryService 接口

`IBaselineHistoryService extends IService<BaselineHistory>`：

| 方法 | 签名 | 说明 |
|------|------|------|
| `recordBaselineChange` | `BaselineHistory recordBaselineChange(Long projectId, Long changeRequestId, String crNo, String changeType, String fieldName, String oldValue, String newValue, String changedBy)` | 记录一条基线变更审计，`oldValue`/`newValue` 为空时存储为 `"空"`，`description` 自动拼接为 `"<fieldName> 由 <old> 变更为 <new>"` |
| `listByProject` | `List<BaselineHistory> listByProject(Long projectId)` | 按项目ID查询基线变更历史（按 `changedAt` 倒序），`projectId` 为 `null` 返回空列表 |

### IIssueService 接口

`IIssueService extends IService<Issue>`：

| 方法 | 签名 | 说明 |
|------|------|------|
| `create` | `Result<Issue> create(Issue)` | 创建问题（`OPEN` 状态），自动生成 `issueNo`、缺省 `priority`/`targetResolveDate` |
| `update` | `Result<?> update(Issue)` | 更新问题，存在性校验 |
| `delete` | `Result<?> delete(Long id)` | 删除问题（逻辑删除） |
| `listAll` | `Result<List<Issue>> listAll()` | 查询全部问题（按 `createTime` 倒序） |
| `getById` | `Result<Issue> getById(Long id)` | 按ID查询 |
| `listByProject` | `Result<List<Issue>> listByProject(Long projectId)` | 按项目ID查询 |
| `assign` | `Result<Issue> assign(Long id, Long assigneeId, String assigneeName)` | 分配处理人，`OPEN` 状态自动转 `IN_PROGRESS` |
| `resolve` | `Result<Issue> resolve(Long id, String resolution)` | 解决问题：任意 → `RESOLVED`，填充 `resolution`/`resolvedAt` |
| `close` | `Result<Issue> close(Long id)` | 关闭问题：任意 → `CLOSED`，填充 `closedAt` |
| `escalate` | `Result<?> escalate(Long id)` | 升级为变更请求，返回创建的 `ChangeRequest` |
| `generateIssueNo` | `String generateIssueNo()` | 生成问题编号 `ISSUE-YYYY-XXXX` |

### IRiskService 接口

`IRiskService extends IService<Risk>`：

| 方法 | 签名 | 说明 |
|------|------|------|
| `create` | `Result<Risk> create(Risk)` | 创建风险（`OPEN` 状态），自动生成 `riskNo`、计算 `score`/`priority`、填充 `identifiedAt` |
| `update` | `Result<?> update(Risk)` | 更新风险，重新计算 `score`/`priority` |
| `delete` | `Result<?> delete(Long id)` | 删除风险（逻辑删除） |
| `listAll` | `Result<List<Risk>> listAll()` | 查询全部风险（按 `createTime` 倒序） |
| `getById` | `Result<Risk> getById(Long id)` | 按ID查询 |
| `listByProject` | `Result<List<Risk>> listByProject(Long projectId)` | 按项目ID查询 |
| `computeScore` | `void computeScore(Risk)` | 计算 `score = likelihood * impact` 并设置 `priority`（1-6 LOW / 7-12 MEDIUM / 13-25 HIGH） |
| `markOccurred` | `Result<?> markOccurred(Long id)` | 风险已发生：转化为新 `Issue` 并关闭风险 |
| `escalate` | `Result<?> escalate(Long id)` | 升级为变更请求：创建 `ChangeRequest` 并标记 `ESCALATED` |
| `generateRiskNo` | `String generateRiskNo()` | 生成风险编号 `RISK-YYYY-XXXX` |
| `riskMatrix` | `Result<RiskMatrixDto> riskMatrix(Long projectId)` | 构建 5×5 风险矩阵，`projectId` 为 `null` 时统计全部风险 |

### REST API 端点

#### ChangeRequestController（`/api/governance/change-request`）

| HTTP | 路径 | 方法 | 权限码 | OperLog | 幂等 | 说明 |
|------|------|------|--------|---------|------|------|
| `POST` | `/api/governance/change-request` | `create` | `governance:changeRequest:add` | 变更请求管理 businessType=1 | `@Idempotent` | 创建变更请求（`@Valid @RequestBody`） |
| `PUT` | `/api/governance/change-request` | `update` | `governance:changeRequest:edit` | 变更请求管理 businessType=2 | — | 更新变更请求 |
| `DELETE` | `/api/governance/change-request/{id}` | `delete` | `governance:changeRequest:remove` | 变更请求管理 businessType=3 | — | 删除变更请求 |
| `GET` | `/api/governance/change-request` | `list` | — | — | — | 查询全部变更请求 |
| `GET` | `/api/governance/change-request/{id}` | `getById` | — | — | — | 按ID查询 |
| `GET` | `/api/governance/change-request/project/{projectId}` | `listByProject` | — | — | — | 按项目ID查询 |
| `POST` | `/api/governance/change-request/{id}/submit` | `submit` | `governance:changeRequest:process` | 变更请求管理 businessType=2 | — | 提交 CCB 审批 |
| `POST` | `/api/governance/change-request/{id}/approve` | `approve` | `governance:changeRequest:process` | 变更请求管理 businessType=2 | — | CCB 审批通过（`?approverName=`） |
| `POST` | `/api/governance/change-request/{id}/reject` | `reject` | `governance:changeRequest:process` | 变更请求管理 businessType=2 | — | CCB 驳回（`?reason=`） |
| `POST` | `/api/governance/change-request/{id}/implement` | `implement` | `governance:changeRequest:process` | 变更请求管理 businessType=2 | — | 开始实施 |
| `POST` | `/api/governance/change-request/{id}/close` | `close` | `governance:changeRequest:process` | 变更请求管理 businessType=2 | — | 关闭变更请求 |

#### IssueController（`/api/governance/issue`）

| HTTP | 路径 | 方法 | 权限码 | OperLog | 说明 |
|------|------|------|--------|---------|------|
| `POST` | `/api/governance/issue` | `create` | `governance:issue:add` | 问题管理 businessType=1 | 创建问题 |
| `PUT` | `/api/governance/issue` | `update` | `governance:issue:edit` | 问题管理 businessType=2 | 更新问题 |
| `DELETE` | `/api/governance/issue/{id}` | `delete` | `governance:issue:remove` | 问题管理 businessType=3 | 删除问题 |
| `GET` | `/api/governance/issue` | `list` | — | — | 查询全部问题 |
| `GET` | `/api/governance/issue/{id}` | `getById` | — | — | 按ID查询 |
| `GET` | `/api/governance/issue/project/{projectId}` | `listByProject` | — | — | 按项目ID查询 |
| `POST` | `/api/governance/issue/{id}/assign` | `assign` | `governance:issue:process` | 问题管理 businessType=2 | 分配处理人（`?assigneeId=&assigneeName=`） |
| `POST` | `/api/governance/issue/{id}/resolve` | `resolve` | `governance:issue:process` | 问题管理 businessType=2 | 解决问题（`?resolution=`） |
| `POST` | `/api/governance/issue/{id}/close` | `close` | `governance:issue:process` | 问题管理 businessType=2 | 关闭问题 |
| `POST` | `/api/governance/issue/{id}/escalate` | `escalate` | `governance:issue:process` | 问题管理 businessType=2 | 升级为变更请求 |

#### RiskController（`/api/governance/risk`）

| HTTP | 路径 | 方法 | 权限码 | OperLog | 说明 |
|------|------|------|--------|---------|------|
| `POST` | `/api/governance/risk` | `create` | `governance:risk:add` | 风险管理 businessType=1 | 创建风险 |
| `PUT` | `/api/governance/risk` | `update` | `governance:risk:edit` | 风险管理 businessType=2 | 更新风险 |
| `DELETE` | `/api/governance/risk/{id}` | `delete` | `governance:risk:remove` | 风险管理 businessType=3 | 删除风险 |
| `GET` | `/api/governance/risk` | `list` | — | — | 查询全部风险 |
| `GET` | `/api/governance/risk/{id}` | `getById` | — | — | 按ID查询 |
| `GET` | `/api/governance/risk/project/{projectId}` | `listByProject` | — | — | 按项目ID查询 |
| `POST` | `/api/governance/risk/{id}/mark-occurred` | `markOccurred` | `governance:risk:process` | 风险管理 businessType=2 | 标记风险已发生并转化为问题 |
| `POST` | `/api/governance/risk/{id}/escalate` | `escalate` | `governance:risk:process` | 风险管理 businessType=2 | 升级为变更请求 |
| `GET` | `/api/governance/risk/matrix` | `riskMatrix` | — | — | 获取 5×5 风险矩阵（`?projectId=`） |

> 权限注解统一采用 Spring Security `@PreAuthorize`，权限码采用 `governance:<resource>:<action>` 命名约定（`add` / `edit` / `remove` / `process`）。`@OperLog`（`com.dp.plat.common.annotation.OperLog`）记录操作日志，`businessType` 取值：1=新增、2=修改、3=删除。`@Idempotent`（`com.dp.plat.common.annotation.Idempotent`）仅标注在变更请求创建接口，防止重复提交。

### RiskMatrixDto 结构

| 字段 | 类型 | Schema 描述 | 说明 |
|------|------|-------------|------|
| `matrix` | `List<List<Integer>>` | `5x5风险矩阵，matrix[likelihood-1][impact-1]为该象限风险数量` | 二维 5×5 矩阵 |
| `risks` | `List<Risk>` | `项目下全部风险列表` | 风险明细列表 |
| `totalRisks` | `int` | `风险总数` | 风险总数 |
| `highPriorityCount` | `int` | `高优先级风险数量` | 评分 > 12 或 priority == HIGH 的风险数 |

## 模块依赖关系

### Maven 依赖（pom.xml）

```xml
<dependencies>
    <dependency>
        <groupId>com.dp.plat</groupId>
        <artifactId>pms-common</artifactId>
    </dependency>
    <dependency>
        <groupId>com.dp.plat</groupId>
        <artifactId>pms-workflow</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 依赖关系图

```
                    ┌──────────────────┐
                    │   pms-common     │  ◄── BaseEntity / Result / OperLog / Idempotent
                    │                  │      BusinessException / SecurityUtils
                    └────────┬─────────┘
                             │
                             │
                             ▼
                    ┌──────────────────┐
                    │  pms-governance  │  (本模块)
                    │   三本账治理      │
                    └────────┬─────────┘
                             │
                             │ ObjectProvider<WorkflowService>
                             │ (运行时按需获取，未加载则降级)
                             ▼
                    ┌──────────────────┐
                    │  pms-workflow    │  ◄── WorkflowService / StartProcessRequest
                    │  (Flowable 引擎) │      CompleteTaskRequest / ProcessInstanceDTO / TaskDTO
                    └──────────────────┘
```

### 跨模块协作点

| 协作方向 | 协作对象 | 用途 | 机制 |
|----------|----------|------|------|
| `pms-governance` → `pms-common` | `BaseEntity` / `Result` / `BusinessException` / `SecurityUtils` / `@OperLog` / `@Idempotent` | 公共基础能力 | 直接 Maven 依赖 |
| `pms-governance` → `pms-workflow` | `WorkflowService` / `StartProcessRequest` / `CompleteTaskRequest` / `ProcessInstanceDTO` / `TaskDTO` | 启动 CCB 审批流程 + 完成审批任务 | `ObjectProvider` 注入（运行时按需获取，未加载时优雅降级） |
| `pms-governance`（内部） | `Risk` → `Issue` → `ChangeRequest` | 三账联动 | 模块内 Service 显式调用，依赖图无环 |

> **与 `docs/superpowers/architecture/module-dependencies.md` 的差异**：模块依赖文档（校验日期 2026-07-17）记录 `pms-governance → pms-common`，但实际 `pom.xml` 同时声明了对 `pms-workflow` 的依赖。这是后续为支持 CCB 审批工作流集成而新增的依赖，模块依赖文档需同步更新。

### 内部依赖图（三账联动）

```
   ┌─────────────────────────────────────────────────────┐
   │              pms-governance 内部                     │
   │                                                      │
   │   RiskServiceImpl ─────► IIssueService               │
   │      │                 (markOccurred)                │
   │      │                                                │
   │      └────► IChangeRequestService                    │
   │            (escalate)                                │
   │                                                      │
   │   IssueServiceImpl ────► IChangeRequestService       │
   │                         (escalate)                   │
   │                                                      │
   │   ChangeRequestServiceImpl ──► IBaselineHistoryService│
   │                              (approve 时记录基线变更) │
   │                                                      │
   │   ChangeRequestServiceImpl ──► WorkflowService       │
   │                              (ObjectProvider)        │
   └─────────────────────────────────────────────────────┘
```

**无循环依赖证明**：
- `RiskServiceImpl` 依赖 `IIssueService` + `IChangeRequestService`
- `IssueServiceImpl` 依赖 `IChangeRequestService`
- `ChangeRequestServiceImpl` 依赖 `IBaselineHistoryService` + `ObjectProvider<WorkflowService>`
- `BaselineHistoryServiceImpl` 无外部依赖

依赖图为 `risk → issue → change → baseline_history` 的单向链，无环。

## 关键技术点

### 1. ObjectProvider 实现工作流解耦

`ChangeRequestServiceImpl` 通过 `ObjectProvider<WorkflowService>` 而非直接 `@Autowired` 注入工作流服务，实现治理模块与工作流引擎的运行时解耦：

- `pms-workflow` 模块未加载时，`workflowServiceProvider.getIfAvailable()` 返回 `null`，治理模块仍可独立运行（变更请求状态推进不受影响，仅不启动 CCB 流程）。
- `startApprovalWorkflow` 与 `completeReviewTask` 在工作流服务为 `null` 时 `log.warn` 跳过，不抛异常。
- 工作流 API 调用异常时 `try-catch` 包裹，`log.error` 记录但事务不回滚。

这种模式与 `pms-baseline` 模块通过 `@Autowired(required=false)` 注入 `ApprovalTrigger` SPI 的设计思想一致，是平台跨模块解耦的统一约定（参考 TD-P8-001）。

### 2. 三账联动的单向无环依赖

三本账（Risk / Issue / ChangeRequest）通过显式 Service 调用实现联动，依赖图严格保持单向无环：

```
risk → issue → change request
       └─────────────────────► change request
```

- `RiskServiceImpl` 构造器注入 `IIssueService` + `IChangeRequestService`
- `IssueServiceImpl` 构造器注入 `IChangeRequestService`
- `ChangeRequestServiceImpl` 不反向依赖 `Issue` / `Risk`，避免循环

依赖方向遵循"高治理级别 → 低治理级别"的语义：风险（潜在问题）→ 问题（已发生）→ 变更请求（正式审批）。

### 3. 风险评分的双层校验

`Risk` 实体的 `likelihood` / `impact` 字段使用 Bean Validation（`@Min(1)` / `@Max(5)`）进行入口校验，`RiskServiceImpl.computeScore` 内部再通过 `clamp(value)` 钳制到 `[1, 5]`，形成双层保护：

- 入口层：`@Valid` 注解在 Controller 层拦截非法值，返回 400 错误
- 服务层：`clamp` 防御性处理历史数据或直接调用 Service 时的越界值

`score` 与 `priority` 由 `computeScore` 自动计算并回填，用户无需也不应手动设置：

| 评分区间 | 优先级 |
|----------|--------|
| 1-6 | `LOW` |
| 7-12 | `MEDIUM` |
| 13-25 | `HIGH` |

### 4. 基线变更审计的字段级粒度

`BaselineHistory` 以"字段级"粒度记录基线变更，每条记录对应一个字段的一次变更。`ChangeRequestServiceImpl.recordBaselineChanges` 在审批通过时，对 `impactSchedule` / `impactCost` / `impactScope` 三个非空维度分别记录一条审计：

- `changeType` 区分维度：`SCHEDULE` / `COST` / `SCOPE`
- `fieldName` 记录变更字段名
- `oldValue` 固定为基线占位（`"原进度基线"` / `"原成本基线"` / `"原范围基线"`），因当前实现未读取真实基线快照对比
- `newValue` 为变更请求中填写的影响描述
- `description` 由 `BaselineHistoryServiceImpl` 自动拼接为 `"<fieldName> 由 <old> 变更为 <new>"`

> **设计局限**：当前 `oldValue` 为固定占位字符串而非真实基线值。如需精确对比，应集成 `pms-baseline` 模块读取 `BaselineSnapshot` 快照，但这会引入 `pms-governance → pms-baseline` 的模块依赖，需评估必要性。

### 5. 编号生成器的并发风险

`generateCrNo` / `generateIssueNo` / `generateRiskNo` 三个方法采用相同算法：

```java
String prefix = "<TYPE>-" + year + "-";
long count = this.count(LambdaQueryWrapper.likeRight(no, prefix));
long sequence = count + 1;
return prefix + String.format("%04d", sequence);
```

该实现存在并发风险：两个并发请求可能读到同一 `count` 值生成重复编号。生产环境建议：
- 数据库层面为 `<type>_no` 字段添加唯一索引
- 或使用分布式锁（Redis）/ 数据库序列

### 6. CCB 工作流的 BPMN 设计

`change-request-approval.bpmn20.xml` 的关键设计：

- **`flowable:assignee="${ccbApproverId}"`** —— 审批人由流程变量动态指定，支持不同变更请求指定不同 CCB 审批人。
- **`flowable:skipExpression="${assignee == initiator}"`** —— 当审批人与流程发起人相同时跳过任务，避免自审批。
- **`terminateEventDefinition`** —— 驳回路径使用终止结束事件，立即终止流程实例所有分支，与正常结束事件（`approvedEnd`）形成对比。
- **排他网关 + 布尔变量** —— `${approved}` / `${!approved}` 的二选一路由，简单清晰。

### 7. 乐观锁与逻辑删除

- **乐观锁**：`ChangeRequest` 实体使用 `@Version` 注解的 `version` 字段，MyBatis-Plus 在 `updateById` 时自动附加 `WHERE version = ?` 条件，并发更新冲突时抛 `OptimisticLockingFailureException`。`BaselineHistory` / `Issue` / `Risk` 未使用乐观锁（变更频率较低或追加型数据）。
- **逻辑删除**：所有实体继承 `BaseEntity.deleted` 字段（`@TableLogic`），MyBatis-Plus 自动过滤 `deleted = 1` 的记录，所有查询与删除均为逻辑操作。

### 8. 模块级测试覆盖

模块在 `src/test/java/` 下为三个 Service 实现提供了完整的单元测试：

| 测试类 | 覆盖范围 |
|--------|----------|
| `ChangeRequestServiceImplTest` | `create` / `update` / `delete` / `listAll` / `getById` / `listByProject` / `submit` / `approve` / `reject` / `implement` / `close` / `generateCrNo` —— 含状态机校验、缺省值填充、基线变更审计三维度记录、工作流降级等场景 |
| `IssueServiceImplTest` | `create` / `update` / `delete` / `listAll` / `getById` / `listByProject` / `assign` / `resolve` / `close` / `escalate` / `generateIssueNo` —— 含 `OPEN → IN_PROGRESS` 自动转换、升级为变更请求等场景 |
| `RiskServiceImplTest` | `create` / `update` / `delete` / `listAll` / `getById` / `listByProject` / `computeScore` / `markOccurred` / `escalate` / `generateRiskNo` / `riskMatrix` —— 含评分计算、优先级分档、三账联动、矩阵构建等场景 |

测试采用 JUnit 5 + Mockito（`mockito-inline`），通过 `ReflectionTestUtils.setField` 注入 `baseMapper`，`Mockito.spy` 包装 Service 实现以 mock 父类方法（`save` / `updateById` / `removeById`）。测试资源目录 `src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker` 注册了 `mock-maker-inline`，支持 mock `final` 方法与 `ServiceImpl` 父类方法。

### 9. 与设计文档的对照

模块实现对照 PMBOK 治理三本账（Three Books）实践：
- **变更请求登记册**（Change Request Log）—— `ChangeRequest` 实体 + CCB 审批状态机 + BPMN 流程
- **风险登记册**（Risk Register）—— `Risk` 实体 + 5×5 矩阵 + 评分自动计算
- **问题日志**（Issue Log）—— `Issue` 实体 + 分配/解决/关闭流转

三本账通过显式联动操作形成闭环：风险已发生 → 问题、风险升级 → 变更请求、问题升级 → 变更请求，覆盖了 PMBOK 中"风险物质化为问题、问题升级为变更"的典型治理场景。
