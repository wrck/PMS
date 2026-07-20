# Phase 8 技术债清单汇总

| 项 | 值 |
|---|---|
| 汇总阶段 | Phase 8 · Task 13（8.13） |
| 技术债总数 | 16 个（TD-P8-001 至 TD-P8-016） |
| 优先级分布 | 高 5 / 中 5 / 低 6 |
| 来源 | Phase 8 Task 3-12 端到端验收与连通性校验 |
| 汇总日期 | 2026-07-17 |

---

## 1. 技术债总览

| 编号 | 标题 | 优先级 | 发现任务 | 状态 |
|---|---|---|---|---|
| TD-P8-001 | pms-project ↔ pms-workflow 双向依赖环 | 高 | Task 3 | 待修复 |
| TD-P8-002 | 前端权限码 `workflow:*` vs 后端 `approval:center:*` 命名不一致 | 中 | Task 5 | 已修复 |
| TD-P8-003 | `createProjectFromTemplate` 仅深拷贝阶段，未含任务/交付件/依赖 | 高 | Task 6 | 待修复 |
| TD-P8-004 | 权限码 `project:advance:phase` vs `project:phase:advance` 不一致 | 中 | Task 7 | 已修复 |
| TD-P8-005 | TASK/APPROVAL 两类阶段退出条件未实现 | 中 | Task 7 | 待修复 |
| TD-P8-006 | 前端 `api/project.ts` 未封装 subproject/close/cancel 三个端点 | 高 | Task 7/12 | 待修复 |
| TD-P8-007 | 异步进度汇总失败仅日志记录不重试 | 低 | Task 8 | 可接受 |
| TD-P8-008 | BASELINE_CHANGE 审批流程未实际触发 | 高 | Task 9 | 待修复 |
| TD-P8-009 | `detectCycle` 全量加载邻接表性能隐患 | 低 | Task 9 | 可接受 |
| TD-P8-010 | 设计文档响应结构描述与实现偏差（文档勘误） | 低 | Task 9 | 待修复（文档） |
| TD-P8-011 | `validateExitGate` DELIVERABLE 分支精确匹配，与设计「已批准集合」语义不符 | 高 | Task 10 | 待修复 |
| TD-P8-012 | `validateMandatoryDeliverables` 未被 `advancePhase` 复用 + 前端无视图调用 | 中 | Task 10 | 待修复 |
| TD-P8-013 | `buildMaskedFields` 对 MASKED 字段重复脱敏计算 | 低 | Task 11 | 可接受 |
| TD-P8-014 | HIDDEN 字段在 `maskedFields` 元数据中冗余 | 低 | Task 11 | 可接受 |
| TD-P8-015 | 任务管理模块路径命名不一致（`impl` vs `implementation`） | 中 | Task 12 | 待修复 |
| TD-P8-016 | 设计文档 §5.4 路径与实现不符（文档勘误） | 低 | Task 12 | 待修复（文档） |

---

## 2. 高优先级技术债（5 个）

### TD-P8-001：pms-project ↔ pms-workflow 双向依赖环

- **优先级**：高
- **发现任务**：Task 3（后端模块依赖关系校验）
- **位置**：`pms-project/pom.xml`、`pms-workflow/pom.xml`
- **现象**：`pms-project` 依赖 `pms-workflow`（项目模块调用审批中心创建审批记录），同时 `pms-workflow` 依赖 `pms-project`（审批中心加载项目业务数据），形成 Maven 模块双向依赖环。
- **影响**：违反 Maven 模块单向依赖原则，构建时可能产生循环依赖错误；阻碍模块独立编译与测试。
- **建议**：将共享的业务数据加载接口（`BusinessDataLoader`）下沉到 `pms-common` 或新建 `pms-approval-api` 模块，打破环依赖。`pms-project` 实现 `BusinessDataLoader` 并通过 SPI/配置注入 `pms-workflow`。

### TD-P8-003：`createProjectFromTemplate` 仅深拷贝阶段，未含任务/交付件/依赖

- **优先级**：高
- **发现任务**：Task 6（Story 1 端到端验收）
- **位置**：`pms-project/.../service/impl/ProjectTemplateServiceImpl.java` → `createProjectFromTemplate` 方法
- **现象**：从模板创建项目时，仅深拷贝模板的阶段（Phase）数据，未拷贝模板快照中的任务（Task）、交付件（Deliverable）、依赖（Dependency）、里程碑（Milestone）、审批计划（ApprovalPlan）等结构。设计文档 §5.2 行 835-851 响应示例包含 `phases/tasks/milestones/deliverables/approvalPlans` 完整默认计划。
- **影响**：Story 1 验收 1（从模板创建项目含完整默认计划）仅部分通过，创建的项目缺少任务/交付件/依赖，需人工补录。
- **建议**：扩展 `createProjectFromTemplate`，遍历 `TemplateSnapshot` 中的 tasks/milestones/deliverables/dependencies/approvalPlans，批量插入到新项目。

### TD-P8-006：前端 `api/project.ts` 未封装 subproject/close/cancel 三个端点

- **优先级**：高
- **发现任务**：Task 7（Story 2 验收）/ Task 12（接口连通性校验）
- **位置**：`pms-frontend/src/api/project.ts`
- **现象**：设计文档 §5.3 定义了 3 个项目生命周期端点，后端 `ProjectController` 均已实现（行 108/119/128），但前端 `project.ts` 未提供封装函数：
  - `POST /api/project/{id}/subproject`（创建子项目）— Task 12 新发现
  - `POST /api/project/{id}/close`（关闭主项目）— Task 7 已记录
  - `POST /api/project/{id}/cancel`（取消项目）— Task 7 已记录
- **影响**：前端无法调用创建子项目、关闭项目、取消项目功能。Story 2 验收 2（关闭主项目被拒绝）无法在前端演示。
- **建议**：在 `project.ts` 补充 `createSubproject(id, data)`、`closeProject(id)`、`cancelProject(id)` 三个封装函数。

### TD-P8-008：BASELINE_CHANGE 审批流程未实际触发

- **优先级**：高
- **发现任务**：Task 9（Story 4 端到端验收）
- **位置**：`pms-baseline/.../service/impl/BaselineServiceImpl.java` 行 238-241 `requestBaselineChange` 方法
- **现象**：基线偏差超过阈值时，`requestBaselineChange` 方法仅 `log.warn` + 保存 `changeReason`，**未调用** `approvalRecordService.create("BASELINE_CHANGE", ...)`，未启动 Flowable 流程，未回填 `baseline.approvalRecordId`。代码中存在 TODO 注释。
- **根因**：`pms-baseline` 模块未依赖 `pms-workflow`（与 TD-P8-001 相关），无法直接调用审批中心。
- **影响**：设计文档 §3.5 行 498「基线变更 | BASELINE_CHANGE | 必审批 | 旧基线 SUPERSEDED + 新基线 APPROVED」未实现。Story 4 验收 2 仅部分通过。
- **建议**：解决 TD-P8-001 后，在 `requestBaselineChange` 超阈值分支调用 `approvalCenterService.createApproval("BASELINE_CHANGE", baseline.getId(), ...)`，启动审批流程并回填 `approvalRecordId`。

### TD-P8-011：`validateExitGate` DELIVERABLE 分支精确匹配，与设计「已批准集合」语义不符

- **优先级**：高
- **发现任务**：Task 10（Story 5 端到端验收）
- **位置**：`pms-project/.../service/impl/ProjectPhaseServiceImpl.java` 行 157-158 `validateExitGate` 方法 DELIVERABLE 分支
- **现象**：`validateExitGate` 对 DELIVERABLE 类退出条件按 `req.getRequiredStatus().equals(d.getStatus())` **精确匹配**。而设计文档 §3.4 行 427 描述「达到 PUBLISHED/REFERENCED/ARCHIVED（即已批准）」，`DeliverableStatus.isApproved()` 也是集合判断。当 `requiredStatus=PUBLISHED` 而交付件已流转到 `REFERENCED`/`ARCHIVED` 时，会错误阻止阶段推进。
- **影响**：已批准但状态流转的交付件被误判为未满足退出条件，阶段无法推进。Story 5 验收 2 仅部分通过。
- **建议**：将 DELIVERABLE 分支改为集合判断：若 `requiredStatus` 属于已批准集合（`PUBLISHED`/`REFERENCED`/`ARCHIVED`），则检查 `d.getStatus()` 是否也在已批准集合中，而非精确匹配。或直接调用 `DeliverableStatus.valueOf(d.getStatus()).isApproved()`。

---

## 3. 中优先级技术债（6 个）

### TD-P8-002：前端权限码 `workflow:*` vs 后端 `approval:center:*` 命名不一致

- **优先级**：中
- **发现任务**：Task 5（前端路由完整性校验）
- **位置**：前端路由/菜单权限码 vs 后端 `ApprovalCenterController` `@PreAuthorize`
- **现象**：前端使用 `workflow:*` 系列权限码（如 `workflow:approval:handle`），后端 Controller 实际使用 `approval:center:*` 命名风格存在差异。
- **影响**：权限码不匹配可能导致前端菜单/按钮权限校验与后端接口权限校验不一致。
- **建议**：统一权限码命名规范，建议以后端 `@PreAuthorize` 实际值为准，更新前端权限码配置。
- **状态**：已修复（Phase 9 / 批次 B）。经核查，后端 `ApprovalCenterController` / `ApprovalFieldPermissionController` 实际 `@PreAuthorize` 使用的是 `workflow:approval:handle` / `workflow:field:perm`（与前端一致），不一致点实际在 V72 数据库迁移脚本误用 `approval:center:*` 命名空间。已统一 V72 中 4 个权限码为 `workflow:approval:list` / `workflow:approval:handle` / `workflow:approval:resubmit` / `workflow:field:perm`，并同步更新 `docs/superpowers/architecture/frontend-routes.md` §5。

### TD-P8-004：权限码 `project:advance:phase` vs `project:phase:advance` 不一致

- **优先级**：中
- **发现任务**：Task 7（Story 2 端到端验收）
- **位置**：设计文档 §5.3 行 865 vs 后端 `ProjectPhaseController` `@PreAuthorize`
- **现象**：设计文档 §5.3 定义推进阶段权限码为 `project:advance:phase`，后端实际为 `project:phase:advance`（语序不同）。
- **影响**：权限码不一致可能导致权限配置混乱。
- **建议**：统一为一种语序（建议 `project:phase:advance`，与资源层级一致），更新设计文档或后端注解。
- **状态**：已修复（Phase 9 / 批次 B）。经核查，后端 `ProjectPhaseController` 实际使用 `project:advance:phase`，V72 数据库迁移脚本使用 `project:phase:advance`。已统一为 `project:phase:advance`（与 V72 + 资源层级一致）：
  - `pms-project/.../controller/ProjectPhaseController.java` 4 处 `@RequiresPermissions` 改为 `project:phase:advance`
  - 设计文档 §5.3 行 738、865 改为 `project:phase:advance`
  - `docs/superpowers/acceptance/story2-acceptance.md` 同步更新
  - `docs/superpowers/plans/2026-07-17-pm-phase1-infrastructure-and-story1-template.md` 同步更新

### TD-P8-005：TASK/APPROVAL 两类阶段退出条件未实现

- **优先级**：中
- **发现任务**：Task 7（Story 2 端到端验收）
- **位置**：`pms-project/.../service/impl/ProjectPhaseServiceImpl.java` `validateExitGate` 方法
- **现象**：设计文档 §3.4 定义了 4 类阶段退出条件（DELIVERABLE/TASK/APPROVAL/CUSTOM），后端 `validateExitGate` 仅实现了 DELIVERABLE 和 CUSTOM 两类，TASK（任务完成率）和 APPROVAL（关联审批通过）两类未实现。
- **影响**：TASK/APPROVAL 类退出条件配置后不生效，阶段推进时不会校验。
- **建议**：补充 TASK 分支（检查阶段内任务完成率是否达阈值）和 APPROVAL 分支（检查关联审批记录是否已通过）。

### TD-P8-012：`validateMandatoryDeliverables` 未被 `advancePhase` 复用 + 前端无视图调用

- **优先级**：中
- **发现任务**：Task 10（Story 5 端到端验收）
- **位置**：`pms-deliverable/.../service/impl/DeliverableServiceImpl.java` `validateMandatoryDeliverables` 方法
- **现象**：`DeliverableController` 提供了独立的 `GET /api/deliverable/phase/{phaseId}/validate` 端点（`validateMandatoryDeliverables`），语义正确（已批准集合判断）。但 `advancePhase` 的 `validateExitGate` DELIVERABLE 分支使用了另一套精确匹配逻辑（TD-P8-011），未复用此方法。前端也无任何视图调用该独立 API。
- **影响**：两套并行逻辑，维护成本高；独立 API 成为死代码。
- **建议**：修复 TD-P8-011 后，让 `validateExitGate` DELIVERABLE 分支复用 `validateMandatoryDeliverables`；前端在阶段推进前调用独立 API 预校验并展示未满足项。

### TD-P8-015：任务管理模块路径命名不一致（`impl` vs `implementation`）

- **优先级**：中
- **发现任务**：Task 12（接口连通性校验）
- **位置**：
  - `ImplTaskController` 类级 `/api/impl/task`（短路径）
  - `TaskChecklistController` 类级 `/api/impl/task/checklist`（短路径）
  - `TaskCommentController` 类级 `/api/impl/task/comment`（短路径）
  - `TaskActivityController` 类级 `/api/impl/task/activity`（短路径）
  - `TaskDependencyController` 类级 `/api/implementation/task/dependency`（长路径）
- **现象**：同一实施模块下，任务/检查项/评论/活动使用短路径 `/api/impl/task/...`，而任务依赖使用长路径 `/api/implementation/task/dependency`。前端跟随后端，前后端连通正常。
- **根因**：`TaskDependencyController` 位于 `pms-baseline` 模块（非 `pms-implementation`），跨模块开发时路径前缀选择不一致。
- **影响**：API 路径风格不一致，增加维护认知成本。
- **建议**：统一任务相关端点路径前缀为 `/api/implementation/task/...`（与设计文档一致），或全部统一为 `/api/impl/task/...`。需同步更新前端封装与设计文档。

---

## 4. 低优先级技术债（5 个）

### TD-P8-007：异步进度汇总失败仅日志记录不重试

- **优先级**：低
- **发现任务**：Task 8（Story 3 端到端验收）
- **位置**：`pms-implementation/.../service/impl/ImplTaskServiceImpl.java` 异步进度汇总方法
- **现象**：`@Async` 进度汇总任务失败时仅 `log.error` 记录，不重试也不通知。
- **影响**：进度汇总偶发失败时数据不更新，需手动触发。
- **建议**：可接受现状。如需增强，可引入 Spring Retry 或消息队列重试机制。优先级低。

### TD-P8-009：`detectCycle` 全量加载邻接表性能隐患

- **优先级**：低
- **发现任务**：Task 9（Story 4 端到端验收）
- **位置**：`pms-baseline/.../service/impl/TaskDependencyServiceImpl.java` `detectCycle` 方法
- **现象**：循环依赖检测时全量加载项目所有任务依赖到内存构建邻接表，再执行 DFS。任务量大时（>1000）可能内存与性能压力。
- **影响**：大规模项目下检测延迟增加，但当前项目规模可接受。
- **建议**：可接受现状。如需优化，可改为按需加载邻接节点或增量检测。优先级低。

### TD-P8-010：设计文档响应结构描述与实现偏差（文档勘误）

- **优先级**：低
- **发现任务**：Task 9（Story 4 端到端验收）
- **位置**：设计文档 §5.5 行 973-990（循环依赖响应示例）
- **现象**：设计文档中循环依赖响应结构的字段命名/嵌套与实际实现（`DependencyCycleResult` / `CycleNode`）存在细微偏差。
- **影响**：文档与实现不符，可能误导开发者。
- **建议**：更新设计文档响应示例，与实际 DTO 字段对齐。优先级低。

### TD-P8-013：`buildMaskedFields` 对 MASKED 字段重复脱敏计算

- **优先级**：低
- **发现任务**：Task 11（Story 6 端到端验收）
- **位置**：`pms-workflow/.../controller/ApprovalCenterController.java` 行 140 + 行 271-273
- **现象**：`detail` 方法行 140 已通过 `maskMap` 对 `businessData` 整体脱敏，行 143 `buildMaskedFields` 又对同一批 MASKED 字段调用 `sensitiveFieldMasker.mask` 重新脱敏（基于原始 `businessData`）。两次脱敏结果一致（幂等），但重复计算。
- **影响**：轻微性能浪费，无功能正确性问题。
- **建议**：可接受现状。如需优化，让 `buildMaskedFields` 复用 `maskedData` 中已脱敏的值。优先级低。

### TD-P8-014：HIDDEN 字段在 `maskedFields` 元数据中冗余

- **优先级**：低
- **发现任务**：Task 11（Story 6 端到端验收）
- **位置**：`pms-workflow/.../controller/ApprovalCenterController.java` 行 263-269
- **现象**：`buildMaskedFields` 为 HIDDEN 字段生成 `MaskedFieldVO{permission=HIDDEN, maskedValue=null}` 元数据项。但 `maskMap` 已将 HIDDEN 字段从 `businessData` 移除，前端 `v-for` 遍历 `businessData` 不会渲染 HIDDEN 字段，故 `maskedFields` 中的 HIDDEN 项无消费者。
- **影响**：响应体冗余字段，无功能正确性问题。
- **建议**：可接受现状。如需精简，在 `buildMaskedFields` 中跳过 HIDDEN 字段。优先级低。

### TD-P8-016：设计文档 §5.4 路径与实现不符（文档勘误）

- **优先级**：低
- **发现任务**：Task 12（接口连通性校验）
- **位置**：设计文档 §5.4 行 906-925
- **现象**：设计文档 §5.4 任务管理 API 表格中所有路径使用 `/api/implementation/task/...`（长路径），但后端 `ImplTaskController` 实际实现为 `/api/impl/task/...`（短路径）。前端跟随后端使用短路径，前后端连通正常。
- **影响**：文档与实现不符，可能误导开发者。
- **建议**：更新设计文档 §5.4 路径为 `/api/impl/task/...`，或与 TD-P8-015 联动统一为长路径。优先级低。

---

## 5. 技术债统计与分析

### 5.1 按优先级分布

| 优先级 | 数量 | 编号 |
|---|---|---|
| 高 | 5 | TD-P8-001, 003, 006, 008, 011 |
| 中 | 5 | TD-P8-002, 004, 005, 012, 015 |
| 低 | 6 | TD-P8-007, 009, 010, 013, 014, 016 |
| **合计** | **16** | — |

### 5.2 按类型分布

| 类型 | 数量 | 编号 |
|---|---|---|
| 模块依赖/架构 | 2 | TD-P8-001, 008 |
| 功能缺失/未实现 | 4 | TD-P8-003, 005, 006, 008 |
| 语义偏差/逻辑错误 | 1 | TD-P8-011 |
| 命名/路径不一致 | 3 | TD-P8-002, 004, 015 |
| 代码冗余/性能 | 3 | TD-P8-007, 009, 013 |
| 死代码/未复用 | 2 | TD-P8-012, 014 |
| 文档勘误 | 2 | TD-P8-010, 016 |

### 5.3 按发现任务分布

| 任务 | 发现数量 | 编号 |
|---|---|---|
| Task 3（模块依赖校验） | 1 | TD-P8-001 |
| Task 5（前端路由校验） | 1 | TD-P8-002 |
| Task 6（Story 1 验收） | 1 | TD-P8-003 |
| Task 7（Story 2 验收） | 3 | TD-P8-004, 005, 006 |
| Task 8（Story 3 验收） | 1 | TD-P8-007 |
| Task 9（Story 4 验收） | 3 | TD-P8-008, 009, 010 |
| Task 10（Story 5 验收） | 2 | TD-P8-011, 012 |
| Task 11（Story 6 验收） | 2 | TD-P8-013, 014 |
| Task 12（接口连通性校验） | 2 | TD-P8-015, 016 |

### 5.4 修复建议优先级排序

**建议在 Phase 9 优先修复的高优先级技术债**（按影响范围排序）：

1. **TD-P8-001**（模块依赖环）— 阻碍独立编译，是 TD-P8-008 的前置依赖
2. **TD-P8-008**（BASELINE_CHANGE 审批未触发）— 依赖 TD-P8-001 修复
3. **TD-P8-011**（DELIVERABLE 退出条件精确匹配）— 影响阶段推进核心流程
4. **TD-P8-003**（模板创建缺任务/交付件）— 影响 Story 1 完整性
5. **TD-P8-006**（前端缺 3 个端点封装）— 影响 Story 2 前端可用性

---

## 6. 结论

Phase 8 联调测试与验收阶段共发现 **16 个技术债**，其中：
- **5 个高优先级**需在 Phase 9 优先修复（模块依赖环、功能缺失、语义偏差）
- **5 个中优先级**建议在 Phase 9 修复（命名不一致、功能未复用）
- **6 个低优先级**可接受现状或择机修复（性能优化、文档勘误、代码冗余）

所有技术债均不影响已交付功能的核心正确性，高优先级问题主要集中于「设计已定义但实现未完成」的部分功能（模板深拷贝、基线审批触发、阶段退出条件语义），建议在 Phase 9 集中修复。
