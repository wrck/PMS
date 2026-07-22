# pms-workflow 模块知识库

> 本文基于 `network-equipment-pms/pms-workflow` 模块源码（`com.dp.plat.workflow`）整理，记录工作流引擎集成与统一审批中心（Story 6）的实体模型、Flowable 配置、审批流转机制、字段脱敏、超时调度、OA 任务镜像、跨模块 SPI 协作等核心机制。

## 模块概述

`pms-workflow` 是网络设备 PMS 平台的**工作流基础设施模块**，承担两项核心职责：

1. **Flowable 工作流引擎集成** — 基于 Flowable 7.0.1 提供 BPMN 流程定义部署、流程实例生命周期管理、任务办理/转办/撤回、流程图渲染、流程历史查询等通用工作流能力，对外暴露统一 REST API。
2. **统一审批中心（Story 6）** — 通过自建审批记录/节点/历史/字段权限四张表，为项目/任务/交付件/风险/问题/变更/资源/成本/阶段退出/基线变更等 10 类业务对象提供统一的审批流转（提交/通过/退回/撤回/重新提交）、敏感字段脱敏、超时调度、致远 OA 待办镜像能力。

- **Maven 坐标**：`com.dp.plat:pms-workflow:1.0.0-SNAPSHOT`，父工程为 `com.dp.plat:network-equipment-pms`。
- **artifactId / name**：`pms-workflow`，`<description>` 为 `Flowable workflow engine & 统一审批中心（Story 6）`。
- **基础包名**：`com.dp.plat.workflow`（`package-info.java` 标注为 Flowable workflow engine module）。
- **核心定位**：平台的"流程中枢" —— 上层承载业务侧统一审批中心（自建表，不与 Flowable 表混用），下层封装 Flowable 引擎细节；通过 `ApprovalTriggerEvent` Spring 事件 + `ApprovalTrigger`/`ApprovalStatusChecker`/`ApprovalPlanBatchCreator` 三个 SPI 接口与 `pms-project`/`pms-baseline` 等业务模块解耦协作。
- **关键设计**：审批中心与 Flowable 引擎**双轨并存** —— 审批流转主路径走自建表 `pms_approval_record/node/history`，Flowable 仅作为可选的流程引擎增强（启动 BPMN 实例、OA 镜像、流程图）。Flowable 引擎不可用时不阻断审批创建（best-effort）。

## 包结构

```
com.dp.plat.workflow
├── config/                         # Flowable 引擎配置
│   └── WorkflowConfig.java
├── controller/                     # 3 个 REST 控制器
│   ├── ApprovalCenterController.java
│   ├── ApprovalFieldPermissionController.java
│   └── WorkflowController.java
├── dto/                            # 5 个数据传输对象
│   ├── CompleteTaskRequest.java
│   ├── ProcessDefinitionDTO.java
│   ├── ProcessInstanceDTO.java
│   ├── StartProcessRequest.java
│   └── TaskDTO.java
├── entity/                         # 4 个实体（@TableName 持久化）
│   ├── ApprovalFieldPermission.java
│   ├── ApprovalHistory.java
│   ├── ApprovalNode.java
│   └── ApprovalRecord.java
├── event/                          # Spring 事件
│   └── ApprovalTriggerEvent.java
├── listener/                       # Flowable 任务监听器
│   └── OaTaskListener.java
├── mapper/                         # 4 个 MyBatis-Plus Mapper
│   ├── ApprovalFieldPermissionMapper.java
│   ├── ApprovalHistoryMapper.java
│   ├── ApprovalNodeMapper.java
│   └── ApprovalRecordMapper.java
├── service/                        # Service 接口与实现
│   ├── ApprovalCenterService.java
│   ├── ApprovalDispatcher.java
│   ├── ApprovalTimeoutScheduler.java
│   ├── SensitiveFieldMasker.java
│   ├── WorkflowService.java
│   └── impl/
│       ├── ApprovalCenterServiceImpl.java
│       └── WorkflowServiceImpl.java
├── spi/                            # 3 个跨模块 SPI 实现
│   ├── ApprovalPlanBatchCreatorImpl.java
│   ├── ApprovalStatusCheckerImpl.java
│   └── ApprovalTriggerImpl.java
├── vo/                             # 3 个视图对象
│   ├── ApprovalDetailVO.java
│   ├── ApprovalStatisticsVO.java
│   └── MaskedFieldVO.java
└── package-info.java
```

各包职责说明：

| 包 | 主要类型 | 职责 |
|----|----------|------|
| `config` | `WorkflowConfig` | 注册 `ProcessEngineConfigurationConfigurer`，开启 Flowable schema 自动更新 + `HistoryLevel.FULL` |
| `controller` | 3 个 `@RestController` | 暴露工作流管理（`/api/workflow`）、统一审批中心（`/api/workflow/approval`）、字段权限配置（`/api/workflow/field-perm`）REST API |
| `dto` | 5 个 DTO | Flowable 流程定义/实例/任务 + 启动流程/完成任务请求 |
| `entity` | 4 个 `@TableName` 实体 | 统一审批中心持久化模型（部分含 `@Version` 乐观锁） |
| `event` | `ApprovalTriggerEvent` | 业务模块发布的 Spring `ApplicationEvent`，由 `ApprovalDispatcher` 异步消费 |
| `listener` | `OaTaskListener` | Flowable `TaskListener` 实现，将任务 create/complete 事件镜像到致远 OA |
| `mapper` | 4 个 `BaseMapper` 子接口 | MyBatis-Plus 标准 CRUD，无自定义 SQL |
| `service` / `service.impl` | 4 接口/组件 + 2 实现 | 业务逻辑层（工作流引擎封装、审批中心、审批分发、超时调度、字段脱敏） |
| `spi` | 3 个 SPI 实现 | 向 `pms-project`/`pms-baseline` 暴露的审批触发/状态校验/计划注册扩展点 |
| `vo` | 3 个 VO | 审批详情（含脱敏数据与历史）、统计看板、脱敏字段元数据 |

> **已知问题**：`controller/ApprovalCenterController.java` 文件的 `package` 声明为 `com.dp.plat.workflow.service`（与目录路径不一致），但 Spring 仍可扫描注册为 Bean。该包名错位属于历史遗留，未修复。

## 核心实体模型

模块共 4 个持久化实体。`ApprovalRecord` 与 `ApprovalFieldPermission` 继承 `com.dp.plat.common.entity.BaseEntity`（公共字段：`id`、`createTime`、`updateTime`、`createBy`、`updateBy`、`deleted`（`@TableLogic` 逻辑删除））；`ApprovalNode` 与 `ApprovalHistory` 直接实现 `Serializable`（无审计字段）。

### 实体清单

| 实体 | 表名 | 中文含义 | 乐观锁 | 继承 BaseEntity | 关键关系 |
|------|------|----------|--------|-----------------|----------|
| `ApprovalRecord` | `pms_approval_record` | 统一审批记录 | `@Version` | 是 | N:1 → `Project`（projectId，可空）；1:N → `ApprovalNode`（recordId）；1:N → `ApprovalHistory`（recordId）；可选关联 `processInstanceId` → Flowable `act_ru_execution` |
| `ApprovalNode` | `pms_approval_node` | 审批节点 | 无 | 否（仅 `Serializable`） | N:1 → `ApprovalRecord`（recordId）；1:N → `ApprovalFieldPermission`（approvalNodeId） |
| `ApprovalHistory` | `pms_approval_history` | 审批历史 | 无 | 否（仅 `Serializable`） | N:1 → `ApprovalRecord`（recordId），按 `round` 区分轮次 |
| `ApprovalFieldPermission` | `pms_approval_field_permission` | 审批字段权限 | `@Version` | 是 | N:1 → `ApprovalNode`（approvalNodeId，或节点模板） |

### ApprovalRecord 字段详解

`ApprovalRecord` 是审批中心核心表，对应表 `pms_approval_record`。承载所有业务对象的审批流转，审批退回后重新提交复用原记录、`round` 递增。

| 字段 | 类型 | 校验 | 说明 |
|------|------|------|------|
| `approvalType` | `String` | `@NotBlank` `@Size(max=32)` | 审批类型：`PROJECT`/`TASK`/`DELIVERABLE`/`RISK`/`ISSUE`/`CHANGE`/`RESOURCE`/`COST`/`PHASE_EXIT`/`BASELINE_CHANGE` |
| `businessId` | `Long` | `@NotNull` | 业务对象ID |
| `businessCode` | `String` | `@Size(max=64)` | 业务编码冗余 |
| `projectId` | `Long` | — | 项目维度（可空，用于项目维度审批列表） |
| `processInstanceId` | `String` | `@Size(max=64)` | Flowable 流程实例ID（best-effort 填充） |
| `title` | `String` | `@NotBlank` `@Size(max=200)` | 审批标题 |
| `submitterId` | `Long` | `@NotNull` | 提交人ID |
| `submitterName` | `String` | `@Size(max=64)` | 提交人姓名（冗余） |
| `currentNodeId` | `String` | `@Size(max=64)` | 当前节点ID（Flowable 任务ID） |
| `currentNodeName` | `String` | `@Size(max=64)` | 当前节点名称 |
| `status` | `String` | `@Builder.Default("PENDING")` | 状态：`PENDING`/`APPROVED`/`REJECTED`/`WITHDRAWN`/`TIMEOUT` |
| `round` | `Integer` | `@Builder.Default(1)` | 审批轮次（退回后重新提交 +1） |
| `submittedAt` | `LocalDateTime` | — | 提交时间 |
| `completedAt` | `LocalDateTime` | — | 完成时间（通过/退回/撤回/超时后填充） |
| `timeoutAt` | `LocalDateTime` | — | 超时时间点（超时扫描依据） |
| `escalated` | `Boolean` | `@Builder.Default(false)` | 是否已升级 |
| `version` | `Integer` | `@Version` | 乐观锁版本号（MyBatis-Plus） |

**状态机**：`[DRAFT] → [PENDING] → [APPROVED]`；PENDING 可流转至 `REJECTED` / `WITHDRAWN` / `TIMEOUT`；`REJECTED` 重新提交后 `round+1` 回到 `PENDING`（复用原记录）。

### ApprovalNode 字段详解

`ApprovalNode` 对应表 `pms_approval_node`，一条审批记录下挂多个按 `nodeOrder` 顺序流转的审批节点。

| 字段 | 类型 | 校验 | 说明 |
|------|------|------|------|
| `id` | `Long` | `@TableId(IdType.AUTO)` | 主键 |
| `recordId` | `Long` | `@NotNull` | 所属审批记录ID |
| `nodeName` | `String` | `@NotBlank` `@Size(max=64)` | 节点名称 |
| `nodeOrder` | `Integer` | `@NotNull` | 节点顺序（从 1 开始） |
| `approverId` | `Long` | — | 指定审批人ID（与 `approverRole` 二选一） |
| `approverRole` | `String` | `@Size(max=32)` | 审批角色（多选一时使用） |
| `status` | `String` | `@Builder.Default("PENDING")` | 节点状态：`PENDING`/`APPROVED`/`REJECTED` |
| `approverActualId` | `Long` | — | 实际处理人ID（支持转办场景） |
| `opinion` | `String` | `@Size(max=500)` | 审批意见 |
| `operatedAt` | `LocalDateTime` | — | 处理时间 |
| `timeoutAt` | `LocalDateTime` | — | 节点超时时间点 |

> 注：本表无审计字段（`createBy`/`updateTime` 等），不继承 `BaseEntity`。

### ApprovalHistory 字段详解

`ApprovalHistory` 对应表 `pms_approval_history`，记录每轮每次操作（节点、操作人、动作、意见、时间戳），支持多轮次追溯。

| 字段 | 类型 | 校验 | 说明 |
|------|------|------|------|
| `id` | `Long` | `@TableId(IdType.AUTO)` | 主键 |
| `recordId` | `Long` | `@NotNull` | 所属审批记录ID |
| `round` | `Integer` | `@NotNull` | 审批轮次 |
| `nodeName` | `String` | `@NotBlank` `@Size(max=64)` | 节点名称 |
| `operatorId` | `Long` | `@NotNull` | 操作人ID |
| `operatorName` | `String` | `@Size(max=64)` | 操作人姓名（冗余） |
| `action` | `String` | `@NotBlank` `@Size(max=20)` | 动作：`SUBMIT`/`APPROVE`/`REJECT`/`WITHDRAW`/`RESUBMIT`/`ESCALATE`/`TIMEOUT` |
| `opinion` | `String` | `@Size(max=500)` | 操作意见 |
| `operatedAt` | `LocalDateTime` | — | 操作时间 |

审批退回后重新提交时复用原审批记录，但本表追加新的历史行（`round` 字段区分轮次）。

### ApprovalFieldPermission 字段详解

`ApprovalFieldPermission` 对应表 `pms_approval_field_permission`，按审批节点 + 业务实体 + 字段维度配置字段可见性。

| 字段 | 类型 | 校验 | 说明 |
|------|------|------|------|
| `approvalNodeId` | `Long` | `@NotNull` | 关联审批节点ID（或节点模板） |
| `entityType` | `String` | `@NotBlank` `@Size(max=128)` | 业务实体类名（如 `Deliverable`） |
| `fieldName` | `String` | `@NotBlank` `@Size(max=64)` | 字段名 |
| `permission` | `String` | `@Builder.Default("VISIBLE")` | 权限：`VISIBLE`/`MASKED`/`HIDDEN` |
| `maskPattern` | `String` | `@Size(max=64)` | 脱敏规则：`phone-mask`/`amount-mask`/`email-mask`/`custom` |
| `customPattern` | `String` | `@Size(max=128)` | 自定义正则（当 `maskPattern=custom` 时使用） |
| `version` | `Integer` | `@Version` | 乐观锁版本号 |

权限语义：

- `VISIBLE`：原值返回
- `HIDDEN`：返回 `null`（字段不出现在详情中）
- `MASKED`：按 `maskPattern` 脱敏后返回

## 工作流引擎集成（Flowable 配置、流程定义、部署机制）

### Flowable 版本与依赖

- **Flowable 版本**：`7.0.1`（父 POM `<flowable.version>7.0.1</flowable.version>` 统一管理），通过 `flowable-spring-boot-starter` 引入。
- **Spring Boot 版本**：`3.2.5`（父 POM），Java 17。
- **数据库**：与主业务库共用 MySQL（`network_equipment_pms`），Flowable 自动维护 `act_*` 系列表。

### WorkflowConfig 配置

`config/WorkflowConfig.java` 注册 `ProcessEngineConfigurationConfigurer` Bean，对 Flowable Spring Boot 自动配置创建的 `SpringProcessEngineConfiguration` 做两项定制：

```java
configuration.setDatabaseSchemaUpdate("true");     // 启动时自动创建/更新 act_* 表
configuration.setHistoryLevel(HistoryLevel.FULL);  // 完整历史级别（支持审计与撤回）
```

`HistoryLevel.FULL` 确保完整流程执行轨迹可用于审计与 `withdrawTask` 撤回操作（撤回依赖 `HistoricActivityInstance` 查询上一节点）。

### application.yml 配置

`pms-admin/src/main/resources/application.yml` 中的 Flowable 配置：

```yaml
flowable:
  database-schema-update: true      # 启动时自动创建/更新 act_* 表结构
  async-executor-activate: false    # 关闭异步执行器（定时任务由 pms-workflow 自调度）
  process-definition-cache-limit: 100
```

### BPMN 流程定义文件位置

> **重要**：BPMN 流程定义文件**不在 pms-workflow 模块内**，而是按业务域分散在以下模块的 `src/main/resources/processes/` 目录，由 Spring Boot 自动部署或通过 `/api/workflow/deploy` 接口手动部署：

| BPMN 文件 | 所在模块 | 流程 Key (`<process id>`) | 流程名称 | 节点结构 |
|-----------|----------|---------------------------|----------|----------|
| `project-approval.bpmn20.xml` | `pms-admin` | `projectApproval` | 项目审批流程 | 开始 → PM审核 → 部门经理审核 → 结束/驳回终止 |
| `asset-transfer.bpmn20.xml` | `pms-admin` | `assetTransfer` | 资产转移流程 | 开始 → 源PM审核 → 目标PM审核 → 结束/驳回终止 |
| `settlement-approval.bpmn20.xml` | `pms-admin` | `settlementApproval` | 结算审批流程 | 开始 → PM审核 → 财务审核 → 结束/驳回终止 |
| `final-acceptance.bpmn20.xml` | `pms-admin` | `finalAcceptance` | 最终验收流程 | 开始 → 客户确认 → PM审核 → 结束/驳回终止 |
| `network-cutover.bpmn20.xml` | `pms-admin` | `demo_network_cutover` | 网络割接流程 | 开始 → 风险审核 → 割接窗口确认 → 实施割接 → 业务验证 → (回退/复盘归档) → 完成/驳回 |
| `change-request-approval.bpmn20.xml` | `pms-governance` | `changeRequestApproval` | 变更请求CCB审批流程 | 请求人提交 → CCB审核 → 通过/驳回终止 |

### BPMN 流程关键特性

1. **SkipExpression 自动跳过**：`projectApproval`/`assetTransfer`/`settlementApproval`/`finalAcceptance`/`changeRequestApproval` 五个流程的 UserTask 均配置 `flowable:skipExpression="${assignee == initiator}"`，当任务办理人等于流程发起人时自动跳过该节点。`WorkflowServiceImpl.startProcess` 在启动流程时注入两个流程变量：
   - `_FLOWABLE_SKIP_EXPRESSION_ENABLED=true`：全局开启 SkipExpression 求值
   - `initiator=currentUserId`：发起人ID，供 SkipExpression 比较
2. **OA 任务监听器镜像**：前四个流程的每个 UserTask 通过 `<extensionElements>` 配置 `delegateExpression="${oaTaskListener}"`，绑定 `create` 与 `complete` 两个事件，由 `OaTaskListener` 同步致远 OA 待办。
3. **排他网关 + 终止结束事件**：每个审核节点后接 `<exclusiveGateway>`，根据 `${approved}` 流程变量路由到下一审核节点或 `<endEvent><terminateEventDefinition/></endEvent>`（驳回终止，终止整个流程实例）。
4. **候选组分配**：`network-cutover` 流程使用 `flowable:candidateGroups="network_manager/change_manager/network_engineer/business_owner"` 按角色组分配任务，未配置 SkipExpression 与 OA 监听器。

### 流程部署机制

`WorkflowServiceImpl.deployProcess(MultipartFile file)` 提供运行时部署能力：

```java
DeploymentBuilder builder = repositoryService.createDeployment().name(resourceName);
try (InputStream in = file.getInputStream()) {
    builder.addInputStream(resourceName, in);
}
Deployment deployment = builder.deploy();
```

返回部署 ID、名称、时间、category、tenantId。低代码流程设计器（`pms-lowcode` 的 `LowCodeProcessController`）通过 `WorkflowService.deployProcess` 与 `getProcessDefinitionBpmnXml` 复用本模块能力，实现 BPMN XML 的部署与只读预览。

## 统一审批中心（审批记录、节点、历史、字段权限）

### 架构定位

统一审批中心（Story 6）是 `pms-workflow` 的核心业务能力，**与 Flowable 引擎双轨并存**：

- **主路径（自建表）**：审批记录/节点/历史/字段权限存储在 `pms_approval_*` 四张表，由 `ApprovalCenterServiceImpl` 维护状态机与历史，不依赖 Flowable 引擎。
- **增强路径（Flowable）**：`createApproval` 时 best-effort 启动 BPMN 流程实例（流程 key = `approvalType.toLowerCase()`），写入 `processInstanceId`。Flowable 不可用或流程定义未部署时仅记录日志不阻断审批创建。

### ApprovalCenterService 接口

`service/ApprovalCenterService.java` 继承 MyBatis-Plus `IService<ApprovalRecord>`，定义 8 个业务方法：

| 方法 | 说明 | 状态流转 | 历史动作 |
|------|------|----------|----------|
| `createApproval(ApprovalRecord)` | 创建审批（状态 PENDING，round=1） | DRAFT → PENDING | SUBMIT |
| `approve(Long nodeId, String comment, Long operatorId)` | 通过当前节点，激活下一节点；最后节点则记录 APPROVED | PENDING → APPROVED（最后节点） | APPROVE |
| `reject(Long nodeId, String comment, Long operatorId)` | 退回当前节点，记录 REJECTED（round 不变） | PENDING → REJECTED | REJECT |
| `withdraw(Long recordId, Long operatorId)` | 撤回（仅提交人） | PENDING → WITHDRAWN | WITHDRAW |
| `resubmit(Long recordId, String comment)` | 重新提交（复用原记录，round+1，重置节点状态） | REJECTED/WITHDRAWN → PENDING | RESUBMIT |
| `listPending(Long userId)` | 我的待办（用户作为审批人的 PENDING 节点对应记录） | — | — |
| `listSubmitted(Long userId)` | 我提交的审批记录 | — | — |
| `listByProject(Long projectId)` | 项目维度审批列表 | — | — |
| `listHistory(Long recordId)` | 审批历史（含所有轮次，按 round、operatedAt 升序） | — | — |
| `statistics(Long userId)` | 审批统计（按状态聚合：pending/approved/rejected/withdrawn/timeout/total） | — | — |

### ApprovalCenterServiceImpl 关键实现

`service/impl/ApprovalCenterServiceImpl.java` 继承 `ServiceImpl<ApprovalRecordMapper, ApprovalRecord>`，关键逻辑：

1. **createApproval**：
   - 校验 `approvalType`/`businessId`/`title`/`submitterId` 非空
   - 设置 `status=PENDING`、`round=1`、`submittedAt=now()`、`escalated=false`
   - 调用 `this.save(record)` 落库
   - 调用 `recordHistory` 追加 SUBMIT 历史
   - 调用 `startFlowableProcess(record)` best-effort 启动 Flowable 流程实例
2. **startFlowableProcess**：
   - 流程 key = `approvalType.toLowerCase()`（如 `BASELINE_CHANGE` → `baseline_change`）
   - 流程变量：`approvalRecordId`、`approvalType`、`businessId`、`submitterId`、`round`、`projectId`
   - `businessKey` = `record.getId()`
   - 启动成功后回写 `processInstanceId` 并 `updateById`
   - 失败时仅 `log.warn`，不抛异常（不阻断审批创建）
3. **approve**：
   - 校验审批记录状态为 PENDING
   - 当前节点置 APPROVED，记录 `opinion`/`approverActualId`/`operatedAt`
   - 查找下一节点（`nodeOrder` 升序第一个 PENDING）
   - 无下一节点 → 审批记录置 APPROVED，清空 currentNodeName/currentNodeId
   - 有下一节点 → 激活下一节点（置 PENDING），更新 currentNodeName
   - 追加 APPROVE 历史
4. **reject**：
   - 当前节点置 REJECTED，审批记录置 REJECTED（round 不变）
   - 追加 REJECT 历史
5. **withdraw**：
   - 校验 `submitterId == operatorId`（仅提交人可撤回）
   - 审批记录置 WITHDRAWN
   - 追加 WITHDRAW 历史
6. **resubmit**：
   - 仅 REJECTED/WITHDRAWN 状态可重新提交
   - 复用原记录：`round+1`，状态回 PENDING，清空 `completedAt`
   - 重置所有节点为 PENDING，清空 `opinion`/`approverActualId`/`operatedAt`
   - currentNodeName 指向第一个节点（`nodeOrder` 最小）
   - 追加 RESUBMIT 历史

所有写操作标注 `@Transactional(rollbackFor = Exception.class)`，每次操作都通过 `recordHistory` 追加 `ApprovalHistory` 行，保证多轮次可追溯。

### 审批详情与字段脱敏

`ApprovalCenterController.detail(Long id)` 是审批中心最复杂的端点，执行 5 步流程：

1. 加载审批记录 `ApprovalRecord`（不存在抛 `BusinessException`）
2. 查找当前 PENDING 节点（`ApprovalNode`，按 `nodeOrder` 升序取第一个），加载该节点的字段权限 `List<ApprovalFieldPermission>`
3. 按 `approvalType` 路由 `BusinessDataLoader` SPI 实现加载业务数据 `Map<String, Object>`
4. 调用 `SensitiveFieldMasker.maskMap(businessData, perms)` 对业务数据脱敏
5. 构建脱敏字段元数据 `List<MaskedFieldVO>`（仅 MASKED 字段，TD-P8-013 修复：复用已脱敏值避免重复脱敏；TD-P8-014 修复：跳过 HIDDEN 字段不生成冗余元数据）

返回 `ApprovalDetailVO`（含 record + businessData + maskedFields + history）。

### BusinessDataLoader SPI 路由

`ApprovalCenterController` 注入 `List<BusinessDataLoader> businessDataLoaders`，按 `approvalType` 匹配 `loader.supportedType()` 路由加载业务数据。`BusinessDataLoader` 接口原位于 `pms-workflow`，因 TD-P8-001 双向依赖环下沉到 `pms-common`，各业务模块（交付件/风险/变更/项目等）可实现各自的加载器无需依赖 `pms-workflow`。

### 字段权限配置 API

`ApprovalFieldPermissionController`（`/api/workflow/field-perm`）提供 4 个端点管理字段权限配置：

- `GET /list`：按 `approvalNodeId` + 可选 `entityType` 查询，按 `entityType`、`fieldName` 升序
- `POST`：新增（校验 `approvalNodeId`/`entityType`/`fieldName` 非空，`permission` 必须为 `VISIBLE`/`MASKED`/`HIDDEN`，`MASKED` 必须配置 `maskPattern`，`custom` 必须配置 `customPattern` 正则）
- `PUT`：更新（`id` 必填）
- `DELETE /{id}`：删除

## 审批流转机制（提交/审核/驳回/跳过）

### 双轨流转模型

`pms-workflow` 同时支持两种审批流转模型，可根据场景选择：

#### 模型一：自建审批中心流转（ApprovalCenterService）

通过 `pms_approval_record/node/history` 三表维护状态机，适用于需要多轮次追溯、字段脱敏、超时调度的业务审批（项目/任务/交付件/风险/问题/变更/资源/成本/阶段退出/基线变更）。

**流转动作**：

| 动作 | 方法 | 前置状态 | 后置状态 | round 变化 | 节点处理 |
|------|------|----------|----------|-----------|----------|
| 提交 | `createApproval` | — | PENDING | =1 | 创建记录，启动 Flowable 实例（best-effort） |
| 通过 | `approve` | PENDING | APPROVED（最后节点）或 PENDING（中间节点） | 不变 | 当前节点 APPROVED，激活下一节点 |
| 退回 | `reject` | PENDING | REJECTED | 不变 | 当前节点 REJECTED |
| 撤回 | `withdraw` | PENDING | WITHDRAWN | 不变 | 仅提交人可操作 |
| 重新提交 | `resubmit` | REJECTED/WITHDRAWN | PENDING | +1 | 重置所有节点为 PENDING |
| 超时 | `ApprovalTimeoutScheduler.scanTimeout` | PENDING | APPROVED/REJECTED（按配置）或保持 PENDING（仅通知） | 不变 | 标记 escalated |

每次操作都追加 `ApprovalHistory`（action 字段区分动作类型），支持多轮次追溯。

#### 模型二：Flowable 原生流转（WorkflowService）

通过 Flowable 引擎原生 API 维护 `act_ru_task` 等表，适用于结算审批、资产转移、最终验收、变更请求等独立工作流场景（由 `pms-implementation`/`pms-asset`/`pms-governance` 等业务模块直接调用 `WorkflowService.startProcess` 启动）。

**SkipExpression 自动跳过机制**：

`WorkflowServiceImpl.startProcess` 启动流程时注入两个流程变量：

```java
variables.put(VAR_SKIP_EXPRESSION_ENABLED, Boolean.TRUE);  // _FLOWABLE_SKIP_EXPRESSION_ENABLED
variables.put(VAR_INITIATOR, currentUserId);                // initiator
```

BPMN 中 UserTask 配置 `flowable:skipExpression="${assignee == initiator}"`，当任务办理人（`${pmUserId}` 等流程变量）等于发起人时，Flowable 引擎自动跳过该节点，避免发起人审批自己提交的流程。例如 PM 提交项目审批后，若 PM 本人就是 PM 审核节点的办理人，则该节点自动跳过，直接进入部门经理审核。

**撤回机制**（`withdrawTask`）：

```java
runtimeService.createChangeActivityStateBuilder()
    .processInstanceId(processInstanceId)
    .moveActivityIdTo(currentActivityId, previous.getActivityId())
    .changeState();
```

通过 Flowable `ChangeActivityStateBuilder` 将当前活动节点回退到上一已完成的 UserTask 节点（按 `endTime` 取最新一个 `finished` 的 `HistoricActivityInstance`）。

### 审批触发机制

#### Spring 事件驱动（ApprovalTriggerEvent + ApprovalDispatcher）

业务模块在需要审批时发布 `ApprovalTriggerEvent`（继承 `ApplicationEvent`），携带 `approvalType`/`businessId`/`businessCode`/`projectId`/`title`/`submitterId`/`submitterName`。

`ApprovalDispatcher`（`@Component`）通过 `@EventListener` + `@Async` 异步监听该事件，调用 `ApprovalCenterService.createApproval` 创建审批记录。创建失败仅 `log.error` 不回滚业务事务（审批记录可后续补偿创建）。

`ApprovalTriggerEvent.toRecord()` 方法将事件转换为 `ApprovalRecord` 实体（不含 ID 与审计字段，由服务层填充）。

#### SPI 直接触发（ApprovalTrigger）

`pms-baseline` 等模块通过 `ApprovalTrigger` SPI（`spi/ApprovalTriggerImpl.java`）跨模块同步触发审批，避免直接依赖 `pms-workflow` 内部 API。触发流程：

1. 从 `SecurityUtils.getCurrentUserId()` 获取提交人ID（无用户上下文使用 `0L` 系统用户）
2. 构造 `ApprovalRecord`（approvalType/businessId/projectId/title/submitterId，初始 PENDING）
3. 调用 `ApprovalCenterService.createApproval` 落库（同时记录 SUBMIT 历史）
4. 返回审批记录ID供调用方回填 `baseline.approvalRecordId`

#### 审批触发规则矩阵

设计文档 §3.5（行 486-499）定义的审批触发规则矩阵：

| 业务事件 | 审批类型 |
|----------|----------|
| 项目创建/启动/关闭/取消 | `PROJECT_CREATE` / `PROJECT_START` / `PROJECT_CLOSE` / `PROJECT_CANCEL` |
| 阶段跳过/退出 | `PHASE_SKIP` / `PHASE_EXIT` |
| 任务完成验收 | `TASK_COMPLETE` |
| 交付件提交/发布 | `DELIVERABLE` |
| 基线变更 | `BASELINE_CHANGE` |
| 风险/问题/变更 | `RISK` / `ISSUE` / `CHANGE` |

## 审批超时调度

### ApprovalTimeoutScheduler

`service/ApprovalTimeoutScheduler.java`（`@Component`）是审批中心的超时扫描器，基于 Spring `@Scheduled` 实现定时调度。

**调度配置**：

```java
@Scheduled(cron = "0 0 * * * ?")  // 每小时整点执行
public void scanTimeout() { ... }
```

> 需 `@EnableScheduling`（`pms-admin` 已启用）。

**扫描逻辑**：

1. 查询 `pms_approval_record` 中 `status=PENDING AND timeoutAt < now()` 的所有超时审批记录
2. 对每条记录调用 `handleTimeout(record)`：
   - 通过 `ProjectConfigProvider` SPI 读取项目级超时动作配置（配置键 `approval.timeout.action`）
   - 根据配置动作处理：
     - `AUTO_APPROVE`：记录状态置 APPROVED，completedAt 填充
     - `AUTO_REJECT`：记录状态置 REJECTED，completedAt 填充
     - `NOTIFY_ONLY`（默认）：仅标记 `escalated=true`，状态保持 PENDING，等待人工处理
   - 每次处理都追加 `ApprovalHistory`（action=TIMEOUT，operatorName="系统超时扫描"）

### ProjectConfigProvider SPI 解耦（TD-P8-001）

`ApprovalTimeoutScheduler` 原直接依赖 `pms-project` 模块的 `ProjectConfigService`，TD-P8-001 修复后改为通过 `ProjectConfigProvider` SPI（位于 `pms-common`）解耦：

```java
@Autowired(required = false)
private ProjectConfigProvider projectConfigProvider;

private String readTimeoutAction(Long projectId) {
    if (projectId == null || projectConfigProvider == null) {
        return DEFAULT_TIMEOUT_ACTION;  // NOTIFY_ONLY
    }
    String value = projectConfigProvider.get(projectId, null, CFG_TIMEOUT_ACTION);
    return (value == null || value.isBlank()) ? DEFAULT_TIMEOUT_ACTION : value.toUpperCase();
}
```

`ProjectConfigProvider` 由 `pms-project` 模块实现并注册为 Spring Bean，配置读取顺序：项目级 > 模板级 > 系统默认。`pms-workflow` 通过 `@Autowired(required=false)` 注入，若 `pms-project` 模块未加载则回退默认值 `NOTIFY_ONLY`。

### 常量定义

| 常量 | 值 | 说明 |
|------|----|------|
| `CFG_TIMEOUT_ACTION` | `approval.timeout.action` | 配置键：超时动作 |
| `DEFAULT_TIMEOUT_ACTION` | `NOTIFY_ONLY` | 默认动作（仅通知） |

## 任务监听器（OA 镜像）

### OaTaskListener

`listener/OaTaskListener.java`（`@Component("oaTaskListener")`）实现 Flowable `TaskListener` 接口，将 UserTask 生命周期事件镜像到致远 OA 待办系统。

**注册方式**：BPMN 文件中通过 `delegateExpression="${oaTaskListener}"` 引用 Spring Bean 名，绑定 `create` 与 `complete` 两个事件。

**事件处理**：

| 事件 | 动作 |
|------|------|
| `create` | 构造 `OaTodoRequest`（title/content/handlerUserId/processInstanceId/businessKey/processUrl/businessType），调用 `OaIntegrationService.pushTodo` 推送 OA 待办 |
| `complete` | 以 Flowable 任务ID 作为 OA 业务Key，调用 `OaIntegrationService.completeTodo` 完成 OA 待办 |
| 其他 | 不处理 |

**事务隔离（Task 19.4）**：

`notify(DelegateTask)` 方法标注 `@Transactional(propagation = Propagation.REQUIRES_NEW)`，确保 OA 集成（含 `IntegrationLog` 写入）在独立事务中执行，与 Flowable 工作流主流程事务隔离：

- OA 调用成功 → IntegrationLog 记录 SUCCESS，新事务提交，主流程继续
- OA 调用失败 → IntegrationLog 记录 FAILED，新事务提交（日志需保留），异常被 try-catch 吞掉，主流程不受影响
- 主流程后续回滚 → 不影响已提交的 OA 集成日志（REQUIRES_NEW 已提交）

**异常策略**：所有异常（`IntegrationException` / HTTP 错误 / token 获取失败）均在 `notify` 内 catch，仅记录 WARN 日志，**绝不向 Flowable 引擎上抛**。OA 集成是 best-effort，瞬时故障不应阻塞工作流。

**代理生效前提**：Flowable 通过 `delegateExpression` 从 Spring 容器获取本 Bean，获取的是 Spring 代理对象（CGLIB），因此 `@Transactional` 注解能正常生效。

**业务Key 解析**：`resolveBusinessKey` 优先从流程变量 `businessKey` 获取，无则回退 Flowable 任务ID。`resolveProcessUrl` 从流程变量 `processUrl` 获取流程详情页 URL。

### 与 pms-integration 的关系

`OaTaskListener` 依赖 `pms-integration` 模块的 `OaIntegrationService` 接口，该接口提供：

- `getAccessToken()`：OAuth2 token 缓存（提前 5 分钟自动续期）
- `pushTodo(OaTodoRequest)`：推送 OA 待办（带 `@CircuitBreaker` + `@Bulkhead` + `@Retry` 三层 Resilience4j 保护）
- `completeTodo(String businessKey)`：完成 OA 待办
- `transferTask(String businessKey, String newHandlerUserId)`：转办 OA 待办
- `retry(Long logId)`：按日志ID重试失败的 OA 调用
- `healthCheck()`：OA 适配器健康检查

所有 OA 调用记录在 `IntegrationLog`（`logType="OA"`），失败可由 `RetryService.scheduledRetry()` 定时重试。

## Service 层与 API 端点

### Service 接口清单

| Service | 实现 | 职责 |
|---------|------|------|
| `WorkflowService` | `WorkflowServiceImpl` | Flowable 引擎封装：流程部署/查询/删除、流程实例启动/查询、任务办理/撤回/转办、待办/已办查询、流程图生成、流程历史 |
| `ApprovalCenterService` | `ApprovalCenterServiceImpl` | 统一审批中心：审批记录 CRUD、流转（通过/退回/撤回/重新提交）、查询（待办/已提交/项目维度/历史/统计） |
| `ApprovalDispatcher` | （自身即 `@Component`） | Spring 事件监听器：异步监听 `ApprovalTriggerEvent` 创建审批记录 |
| `ApprovalTimeoutScheduler` | （自身即 `@Component`） | 定时调度器：每小时扫描超时审批，按配置动作处理 |
| `SensitiveFieldMasker` | （自身即 `@Component`） | 字段脱敏器：按 `ApprovalFieldPermission` 配置对业务字段脱敏（VISIBLE/HIDDEN/MASKED + phone/amount/email/custom 四种规则） |

### SPI 实现清单

| SPI 接口（位于 pms-common） | 实现 | 调用方 | 用途 |
|----------------------------|------|--------|------|
| `ApprovalTrigger` | `ApprovalTriggerImpl` | `pms-baseline` | 跨模块同步触发 BASELINE_CHANGE 审批，返回审批记录ID |
| `ApprovalStatusChecker` | `ApprovalStatusCheckerImpl` | `pms-project` | `validateExitGate` APPROVAL 分支校验关联审批是否已通过，返回 `List<ApprovalViolation>` |
| `ApprovalPlanBatchCreator` | `ApprovalPlanBatchCreatorImpl` | `pms-project` | 模板深拷贝时批量注册审批计划（当项目进入某阶段时触发某类型审批），当前仅记录日志 |
| `BusinessDataLoader` | （由各业务模块实现） | `ApprovalCenterController` | 按 `approvalType` 路由加载业务数据用于脱敏展示 |
| `ProjectConfigProvider` | （由 `pms-project` 实现） | `ApprovalTimeoutScheduler` | 读取项目级超时动作配置（`approval.timeout.action`） |

### API 端点清单

#### WorkflowController（`/api/workflow`）— 工作流管理

| 方法 | 路径 | 权限码 | 说明 |
|------|------|--------|------|
| POST | `/deploy` | `workflow:definition:deploy` | 部署 BPMN 流程定义文件 |
| GET | `/definition/list` | — | 分页查询流程定义（latestVersion + active） |
| DELETE | `/deployment/{deploymentId}` | `workflow:definition:remove` | 级联删除部署 |
| POST | `/start` | `workflow:instance:start` | 按 key 启动流程实例（注入 SkipExpression 变量） |
| POST | `/task/complete` | `workflow:task:complete` | 完成任务（可选变量 + 审批意见） |
| POST | `/task/withdraw` | `workflow:task:withdraw` | 撤回任务（回退到上一 UserTask） |
| POST | `/task/transfer` | `workflow:task:transfer` | 转办任务 |
| GET | `/task/todo` | — | 分页查询当前用户待办（`taskCandidateOrAssigned`） |
| GET | `/task/done` | — | 分页查询当前用户已办（`finished`） |
| GET | `/instance/{processInstanceId}` | — | 查询流程实例详情（含当前任务名） |
| GET | `/diagram/{processInstanceId}` | — | 获取流程图 PNG 图片字节 |
| GET | `/history/{processInstanceId}` | — | 查询流程历史活动列表 |

#### ApprovalCenterController（`/api/workflow/approval`）— 统一审批中心

| 方法 | 路径 | 权限码 | 说明 |
|------|------|--------|------|
| GET | `/pending` | `workflow:approval:handle` | 我的待办审批 |
| GET | `/submitted` | — | 我提交的审批 |
| GET | `/project/{projectId}` | — | 项目维度审批列表 |
| GET | `/list` | — | 通用审批列表（按 status/approvalType/projectId 过滤） |
| GET | `/statistics` | — | 审批统计（按状态聚合） |
| GET | `/{id}` | `workflow:approval:handle` | 审批详情（含字段脱敏） |
| GET | `/{id}/history` | — | 审批历史（含所有轮次） |
| POST | `/{id}/approve` | `workflow:approval:handle` | 通过当前节点 |
| POST | `/{id}/reject` | `workflow:approval:handle` | 退回当前节点 |
| POST | `/{id}/withdraw` | — | 撤回审批（仅提交人） |
| POST | `/{id}/resubmit` | — | 重新提交（round+1） |

#### ApprovalFieldPermissionController（`/api/workflow/field-perm`）— 字段权限配置

| 方法 | 路径 | 权限码 | 说明 |
|------|------|--------|------|
| GET | `/list` | `workflow:field:perm` | 查询字段权限列表（按节点 + 实体类型过滤） |
| POST | `` | `workflow:field:perm` | 新增字段权限 |
| PUT | `` | `workflow:field:perm` | 更新字段权限 |
| DELETE | `/{id}` | `workflow:field:perm` | 删除字段权限 |

> 所有写操作均标注 `@OperLog` 注解记录操作日志，权限校验采用 Spring Security `@PreAuthorize`（与 `pms-baseline` 模块一致）。

### SensitiveFieldMasker 脱敏规则

`service/SensitiveFieldMasker.java` 提供 `mask` 与 `maskMap` 两个方法，支持四种脱敏规则：

| maskPattern | 脱敏逻辑 | 示例 |
|-------------|----------|------|
| `phone-mask` | 保留前 3 后 4，中间用 `****` 占位 | `13812345678` → `138****5678` |
| `amount-mask` | 保留前 2 位整数，整数余部用 `***` 替换，保留小数 | `12345.67` → `12***.67` |
| `email-mask` | 本地部分保留首字符，余部用 `***`，保留域名 | `alice@example.com` → `a***@example.com` |
| `custom` | 使用 `customPattern` 正则匹配，替换为 `***` | 自定义正则 |

`maskMap` 方法返回新 Map（不修改入参），HIDDEN 字段会从 Map 中移除（不返回），未配置的字段保持原值（VISIBLE 语义）。

## 模块依赖关系（含循环依赖说明）

### Maven 依赖

`pms-workflow/pom.xml` 声明的依赖：

| 依赖 | 用途 |
|------|------|
| `pms-common` | 公共实体（`BaseEntity`）、SPI 接口（`ProjectConfigProvider`/`BusinessDataLoader`/`ApprovalTrigger`/`ApprovalStatusChecker`/`ApprovalPlanBatchCreator`）、工具类（`SecurityUtils`）、异常（`BusinessException`/`IntegrationException`）、结果封装（`Result`）、注解（`OperLog`）、DTO（`TemplateSnapshot.ApprovalPlanDef`/`ApprovalViolation`） |
| `pms-integration` | OA 集成服务（`OaIntegrationService`/`OaTodoRequest`），供 `OaTaskListener` 镜像致远 OA 待办 |
| `flowable-spring-boot-starter` | Flowable 7.0.1 工作流引擎 |
| `spring-boot-starter-web` | REST Controller |
| `spring-boot-starter-validation` | Jakarta Validation（`@NotBlank`/`@NotNull`/`@Size`） |
| `mybatis-plus-spring-boot3-starter` | MyBatis-Plus 3.5.5 ORM |
| `h2`（test） | 单元测试内存数据库 |
| `spring-boot-starter-test`（test） | 单元测试 |

### 与其他模块的关系

```
pms-common ──────────┐
                     │
pms-integration ─────┼──→ pms-workflow ──→ pms-project ──→ pms-baseline
                     │         │              │
                     │         │              └──→ pms-notification
                     │         │
                     │         ├──→ pms-lowcode（复用 WorkflowService）
                     │         ├──→ pms-implementation（SettlementService 调用 startProcess）
                     │         └──→ pms-asset（AssetTransferService 调用 startProcess）
                     │
                     └── SPI 实现：
                          ├── ProjectConfigProvider ← pms-project 实现
                          ├── BusinessDataLoader ← 各业务模块实现
                          ├── ApprovalTrigger → pms-baseline 调用
                          ├── ApprovalStatusChecker → pms-project 调用
                          └── ApprovalPlanBatchCreator → pms-project 调用
```

| 模块 | 关系类型 | 协作内容 |
|------|----------|----------|
| `pms-common` | 编译期依赖 | 提供 SPI 接口、公共实体、工具类、异常、结果封装 |
| `pms-integration` | 编译期依赖 | 提供 `OaIntegrationService`，供 `OaTaskListener` 镜像 OA 待办 |
| `pms-project` | 单向被依赖（`pms-project` → `pms-workflow`） | `pms-project` 依赖 `pms-workflow` 获取 SPI 实现（`ApprovalStatusChecker`/`ApprovalPlanBatchCreator`）；`pms-workflow` 通过 `ProjectConfigProvider` SPI 反向读取项目配置 |
| `pms-baseline` | 通过 SPI 协作 | `pms-baseline` 通过 `ApprovalTrigger` SPI 触发 `BASELINE_CHANGE` 审批，不直接依赖 `pms-workflow` |
| `pms-lowcode` | 编译期依赖（`pms-lowcode` → `pms-workflow`） | `LowCodeProcessController` 复用 `WorkflowService.deployProcess`/`getProcessDefinitionBpmnXml` 部署与读取 BPMN XML |
| `pms-implementation` | 编译期依赖（`pms-implementation` → `pms-workflow`） | `SettlementServiceImpl` 调用 `WorkflowService.startProcess` 启动结算审批流程（流程 key `settlementApproval`） |
| `pms-asset` | 编译期依赖（`pms-asset` → `pms-workflow`） | `AssetTransferServiceImpl` 调用 `WorkflowService.startProcess` 启动资产转移流程（流程 key `assetTransfer`） |
| `pms-admin` | 运行期聚合 | BPMN 流程定义文件位于 `pms-admin/src/main/resources/processes/`，由 Spring Boot 自动部署；`application.yml` 配置 Flowable 引擎参数 |

### TD-P8-001 循环依赖修复

**问题**：原 `pms-workflow` 直接依赖 `pms-project` 模块的 `ProjectConfigService`（用于审批超时调度读取项目级配置），而 `pms-project` 又依赖 `pms-workflow`（用于审批触发与状态校验），形成**双向编译期依赖环**，导致 Maven 构建失败。

**修复方案**：

1. **移除 `pms-workflow` 对 `pms-project` 的 Maven 依赖**（`pom.xml` 中删除 `pms-project` 依赖声明，注释保留 TD-P8-001 标识）。
2. **下沉 SPI 接口到 `pms-common`**：
   - `ProjectConfigProvider`：项目配置读取 SPI，由 `pms-project` 实现并注册为 Spring Bean
   - `BusinessDataLoader`：业务数据加载器 SPI，原位于 `pms-workflow`，下沉到 `pms-common`，各业务模块实现
3. **`pms-workflow` 通过 `@Autowired(required=false)` 注入 SPI**：若 `pms-project` 模块未加载，SPI Bean 为 null，回退默认值（如超时动作回退 `NOTIFY_ONLY`），不阻断审批中心核心功能。

**修复后依赖关系**：

- `pms-workflow` → `pms-common`（编译期）
- `pms-workflow` → `pms-integration`（编译期）
- `pms-project` → `pms-workflow`（编译期，单向）
- `pms-workflow` ⇄ `pms-project`（运行期通过 SPI 解耦，无编译期环）

**配套修复的 SPI 清单**：

| SPI | 修复编号 | 用途 |
|-----|----------|------|
| `ProjectConfigProvider` | TD-P8-001 | 审批超时调度读取项目级配置 |
| `BusinessDataLoader` | TD-P8-001 | 审批详情按类型加载业务数据 |
| `ApprovalStatusChecker` | TD-P8-005 | `pms-project` 校验关联审批是否已通过 |
| `ApprovalTrigger` | TD-P8-008 | `pms-baseline` 触发 BASELINE_CHANGE 审批 |
| `ApprovalPlanBatchCreator` | TD-P8-003 | `pms-project` 模板深拷贝时批量注册审批计划 |

## 关键技术点

### 1. 双轨并存架构

审批中心（自建表）与 Flowable 引擎双轨并存：

- **自建表主路径**：`pms_approval_record/node/history` 维护状态机与历史，不依赖 Flowable 引擎，保证审批中心在 Flowable 不可用时仍可工作。
- **Flowable 增强路径**：`createApproval` 时 best-effort 启动 BPMN 实例（流程 key = `approvalType.toLowerCase()`），失败不阻断。Flowable 提供 OA 镜像、流程图渲染、SkipExpression 自动跳过等增强能力。
- **解耦点**：`processInstanceId` 字段可空，Flowable 实例启动失败时留空，审批流转不受影响。

### 2. SkipExpression 自动跳过

`WorkflowServiceImpl.startProcess` 启动流程时注入 `_FLOWABLE_SKIP_EXPRESSION_ENABLED=true` 与 `initiator=currentUserId` 两个流程变量，BPMN 中 UserTask 配置 `flowable:skipExpression="${assignee == initiator}"`，当任务办理人等于发起人时自动跳过该节点，避免发起人审批自己提交的流程。

### 3. 多轮次审批追溯

审批退回后重新提交**复用原审批记录**（不新建记录），`round` 字段递增：

- `ApprovalRecord.round`：当前轮次
- `ApprovalHistory.round`：历史行所属轮次
- 重新提交时重置所有节点为 PENDING，从第一个节点重新开始
- 历史查询按 `round`、`operatedAt` 升序，可完整追溯每轮每次操作

### 4. 字段脱敏三态模型

`ApprovalFieldPermission` 定义字段三态：

- `VISIBLE`：原值返回
- `HIDDEN`：从返回 Map 中移除（前端不渲染）
- `MASKED`：按 `maskPattern` 脱敏（phone/amount/email/custom 四种规则）

脱敏在 `ApprovalCenterController.detail` 端点执行，按当前 PENDING 节点的字段权限配置对业务数据脱敏后返回，同时构建 `MaskedFieldVO` 元数据供前端展示脱敏提示图标。

### 5. OA 镜像事务隔离

`OaTaskListener.notify` 标注 `@Transactional(propagation = REQUIRES_NEW)`，OA 集成在独立事务中执行：

- OA 调用失败时 `IntegrationLog` 仍持久化（FAILED 记录），主流程不受影响
- 主流程回滚不影响已提交的 OA 集成日志
- 异常被 try-catch 吞掉，绝不向 Flowable 引擎上抛

### 6. SPI 解耦模式

通过 `pms-common` 中的 SPI 接口实现跨模块解耦：

- `pms-workflow` 实现 `ApprovalTrigger`/`ApprovalStatusChecker`/`ApprovalPlanBatchCreator` 供业务模块调用
- `pms-project` 实现 `ProjectConfigProvider` 供 `pms-workflow` 调用
- 各业务模块实现 `BusinessDataLoader` 供 `pms-workflow` 路由加载业务数据
- 所有 SPI 注入使用 `@Autowired(required=false)`，模块未加载时回退默认行为

### 7. Flowable 撤回机制

`WorkflowServiceImpl.withdrawTask` 通过 `runtimeService.createChangeActivityStateBuilder().moveActivityIdTo(currentActivityId, previous.getActivityId()).changeState()` 实现任务撤回，依赖 `HistoryLevel.FULL` 记录的 `HistoricActivityInstance` 查询上一已完成的 UserTask 节点（按 `endTime` 取最新一个 `finished`）。

### 8. 审批触发双通道

- **异步事件通道**：`ApprovalTriggerEvent` + `ApprovalDispatcher`（`@Async` + `@EventListener`），适用于业务模块解耦触发，失败不阻断业务事务
- **同步 SPI 通道**：`ApprovalTrigger` SPI（`ApprovalTriggerImpl`），适用于需要立即获取审批记录ID回填业务对象的场景（如 `pms-baseline` 触发 BASELINE_CHANGE 审批后回填 `baseline.approvalRecordId`）

### 9. 乐观锁并发控制

`ApprovalRecord` 与 `ApprovalFieldPermission` 标注 `@Version` 乐观锁（MyBatis-Plus），防止并发流转导致的状态覆盖。审批中心高并发场景下（如多审批人同时操作同一记录），乐观锁保证只有一个操作成功，其他操作抛出 `OptimisticLockingFailureException`。

### 10. Flowable 引擎可插拔

`ApprovalCenterServiceImpl` 中 `RuntimeService` 通过 `@Autowired(required=false)` 注入：

```java
@Autowired(required = false)
private RuntimeService runtimeService;
```

Flowable 引擎未启动时 `runtimeService` 为 null，`startFlowableProcess` 仅记录日志跳过流程实例启动，审批中心核心功能（状态机、历史、字段脱敏、超时调度）不受影响。这使得 `pms-workflow` 可在轻量部署场景下关闭 Flowable 引擎，仅使用自建审批中心。
