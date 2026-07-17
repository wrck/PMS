# Story 6 端到端验收报告 — 统一审批中心（字段脱敏 + 历史保留）

| 项 | 值 |
|---|---|
| 验收阶段 | Phase 8 · Task 11（8.11） |
| 验收对象 | Story 6 — 统一审批中心 |
| 验收场景 | 6.1 敏感字段脱敏 / 6.2 审批历史保留（含多轮次） |
| 验收方式 | 静态代码审查（沙箱无公网，无法运行 mvn test / npm run dev） |
| 关联设计文档 | §3.5 审批中心统一规则（行 429-500）、§5.7 统一审批中心 API（行 1080-1147）、§6.9（行 1565-1648） |
| 关联迁移脚本 | `pms-admin/src/main/resources/db/migration/V71__create_approval_center.sql` |
| 涉及模块 | `pms-workflow`（后端）、`pms-frontend`（前端）、`pms-admin`（DDL） |
| 验收日期 | 2026-07-17 |

---

## 1. 验收范围与链路总览

Story 6 对应设计文档 §3.5 与 §5.7，提供统一审批中心 11 个 REST 端点，覆盖审批全生命周期。本次验收聚焦设计文档 §9.1 验收场景对照表中 Story 6 的两个核心验收点：

| 验收场景 | 设计文档定位 | 验收链路 |
|---|---|---|
| 6.1 敏感字段脱敏 | §3.5 行 444-470 / §5.7 行 1094-1121 | `GET /api/workflow/approval/{id}` → 5 步脱敏 → `ApprovalDetailVO` → 前端 `SensitiveFieldDisplay` 展示脱敏提示图标 |
| 6.2 审批历史保留 | §3.5 行 472-484 / §5.7 行 1123-1146 | `POST /api/workflow/approval/{id}/resubmit` → 复用原记录 `round+1` → `GET /api/workflow/approval/{id}/history` → 前端 `ApprovalTimeline` 多轮次分组展示 |

**11 个 API 端点清单**（挂载 `/api/workflow/approval`，权限码 `workflow:approval:handle`）：

| # | Method | Path | 设计文档行 | 前端封装 |
|---|---|---|---|---|
| 1 | GET | `/pending` | 1084 | `getPendingApprovals` |
| 2 | GET | `/submitted` | 1085 | `getSubmittedApprovals` |
| 3 | GET | `/project/{projectId}` | 1086 | `getApprovalsByProject` |
| 4 | GET | `/list` | - | `listApprovals` |
| 5 | GET | `/statistics` | - | `getApprovalStatistics` |
| 6 | GET | `/{id}` | 1087 | `getApprovalDetail` |
| 7 | GET | `/{id}/history` | 1088 | `getApprovalHistory` |
| 8 | POST | `/{id}/approve` | 1089 | `approveApproval` |
| 9 | POST | `/{id}/reject` | 1090 | `rejectApproval` |
| 10 | POST | `/{id}/withdraw` | 1091 | `withdrawApproval` |
| 11 | POST | `/{id}/resubmit` | 1092 | `resubmitApproval` |

前后端 API 对应关系完整，11/11 全覆盖。前端封装文件 `pms-frontend/src/api/approval-center.ts`（164 行）定义了与后端一致的 TypeScript 接口（`ApprovalRecord` / `ApprovalHistory` / `MaskedField` / `ApprovalDetailVO` / `ApprovalStatistics`）。

---

## 2. 场景 6.1：敏感字段脱敏

### 2.1 验收链路

```
审批人打开详情
  ↓
GET /api/workflow/approval/{id}
  ↓
ApprovalCenterController.detail (行 117-155)
  ├─ 1. findCurrentPendingNode → 加载当前节点字段权限 ApprovalFieldPermission
  ├─ 2. loadBusinessData → 按审批类型路由 BusinessDataLoader 加载业务数据 Map
  ├─ 3. SensitiveFieldMasker.maskMap → 脱敏业务数据（HIDDEN 移除、MASKED 脱敏、VISIBLE 原值）
  ├─ 4. buildMaskedFields → 构建脱敏字段元数据 List<MaskedFieldVO>
  └─ 5. listHistory → 加载审批历史
  ↓
ApprovalDetailVO { record, businessData(脱敏), maskedFields, history }
  ↓
前端 approval-detail/index.vue
  └─ SensitiveFieldDisplay 组件：MASKED 字段显示 ⓘ 图标 + Tooltip 说明脱敏规则
```

### 2.2 后端实现审查

#### 2.2.1 Controller 脱敏 5 步流程（`ApprovalCenterController.java` 行 120-155）

```java
public Result<ApprovalDetailVO> detail(@PathVariable Long id) {
    ApprovalRecord record = approvalCenterService.getById(id);
    // 1. 加载当前节点的字段权限
    List<ApprovalFieldPermission> perms = Collections.emptyList();
    ApprovalNode currentNode = findCurrentPendingNode(record.getId());
    Long nodeId = currentNode != null ? currentNode.getId() : null;
    if (nodeId != null) {
        perms = fieldPermissionMapper.selectList(...eq(ApprovalFieldPermission::getApprovalNodeId, nodeId));
    }
    // 2. 加载业务数据（按审批类型路由加载器）
    Map<String, Object> businessData = loadBusinessData(record.getApprovalType(), record.getBusinessId());
    // 3. 脱敏业务数据
    Map<String, Object> maskedData = sensitiveFieldMasker.maskMap(businessData, perms);
    // 4. 构建脱敏字段元数据
    List<MaskedFieldVO> maskedFields = buildMaskedFields(businessData, perms);
    // 5. 加载历史
    List<ApprovalHistory> history = approvalCenterService.listHistory(id);
    ApprovalDetailVO vo = ApprovalDetailVO.builder()
        .record(record).businessData(maskedData).maskedFields(maskedFields).history(history).build();
    return Result.ok(vo);
}
```

**审查结论**：5 步脱敏流程与设计文档 §3.5 行 449-467 伪代码完全对应。字段权限按「当前 PENDING 节点」维度加载（行 128 `findCurrentPendingNode`），符合设计 §3.5「按当前用户在当前节点的字段权限」语义。

#### 2.2.2 SensitiveFieldMasker 4 种脱敏规则（`SensitiveFieldMasker.java` 行 98-162）

| maskPattern | 实现方法 | 规则 | 设计示例 | 实现验证 |
|---|---|---|---|---|
| `phone-mask` | `maskPhone`（行 119-124） | 保留前 3 后 4，中间 `****` | `13812345678` → `138****5678` | ✅ 长度 ≤7 时返回 `****` 兜底 |
| `amount-mask` | `maskAmount`（行 127-135） | 保留前 2 位整数，余部 `***`，保留小数 | `12345.67` → `12***.67` | ✅ 整数部分 ≤2 位时返回 `***` + 小数 |
| `email-mask` | `maskEmail`（行 138-149） | 本地部分保留首字符，余部 `***`，保留域名 | `alice@example.com` → `a***@example.com` | ✅ 无 `@` 或本地部分 ≤1 时兜底 |
| `custom` | `maskCustom`（行 152-162） | `customPattern` 正则匹配部分替换为 `***` | - | ✅ 正则无效时 log.warn + 返回原值 |

**审查结论**：4 种脱敏规则全部实现，与设计文档 §3.5 行 470 示例（金额 `12345.67` → `12***.67`，手机号 `13812345678` → `138****5678`）完全一致。每种规则均有边界兜底（短字符串、无分隔符、无效正则），健壮性良好。

#### 2.2.3 三态权限处理（`SensitiveFieldMasker.maskMap` 行 76-94）

| 权限 | 处理方式 | 验证 |
|---|---|---|
| `VISIBLE` | 原值返回 | ✅ |
| `MASKED` | 调用 `maskValue` 脱敏后返回 | ✅ |
| `HIDDEN` | 从 `businessData` Map 中**移除**该字段（不返回 null） | ✅ 符合设计 §5.7 行 1108 注释「HIDDEN 字段不会出现」 |

#### 2.2.4 脱敏字段元数据构建（`buildMaskedFields` 行 253-283）

仅对 `MASKED` 与 `HIDDEN` 字段生成 `MaskedFieldVO` 元数据项：
- `HIDDEN`：`maskedValue=null`，前端据此知道字段被隐藏
- `MASKED`：`maskedValue=脱敏后字符串`，前端展示脱敏值 + ⓘ 图标

### 2.3 前端实现审查

#### 2.3.1 `SensitiveFieldDisplay.vue`（92 行）

- 接收 `fieldName` / `value` / `maskedFields` 三个 prop
- `maskedMeta` 计算属性从 `maskedFields` 中查找当前字段元数据（行 23-25）
- `isMasked` / `isHidden` 计算属性判断展示样式（行 28-31）
- `maskPatternLabel` 将 4 种 maskPattern 映射为中文说明（行 33-46）：
  - `phone-mask` → 「手机号脱敏（保留前 3 后 4）」
  - `amount-mask` → 「金额脱敏（保留前 2 位整数与小数）」
  - `email-mask` → 「邮箱脱敏（本地部分保留首字符）」
  - `custom` → 「自定义正则脱敏」
- 模板（行 60-68）：脱敏字段值用 warning 色 + 斜体，尾部渲染 `el-tooltip` + `InfoFilled` 图标，悬浮展示「该字段已脱敏：{规则说明}」
- 隐藏字段显示「（已隐藏）」灰色占位（行 54）

**审查结论**：前端脱敏提示交互完整，符合设计 §3.5「审批人打开审批详情时...按权限脱敏后返回」+ 前端展示脱敏提示图标的预期。

#### 2.3.2 `approval-detail/index.vue`（282 行）

- 行 42 `getApprovalDetail(recordId.value)` 调用详情 API
- 行 204-216 `v-for` 遍历 `detail.businessData` 渲染业务字段，每个字段用 `SensitiveFieldDisplay` 组件展示，传入 `maskedFields` 元数据
- 行 220-226 `ApprovalTimeline` 组件渲染审批历史
- 行 171 描述区展示「第 N 轮」（`record.round`），体现轮次信息

### 2.4 数据库支持

`V71__create_approval_center.sql` 行 98-114 创建 `pms_approval_field_permission` 表：

| 字段 | 类型 | 说明 |
|---|---|---|
| `approval_node_id` | BIGINT | 关联审批节点 |
| `entity_type` | VARCHAR(128) | 业务实体类名（如 `Deliverable`） |
| `field_name` | VARCHAR(64) | 字段名 |
| `permission` | VARCHAR(20) | `VISIBLE`/`MASKED`/`HIDDEN`，默认 `VISIBLE` |
| `mask_pattern` | VARCHAR(64) | `phone-mask`/`amount-mask`/`email-mask`/`custom` |
| `custom_pattern` | VARCHAR(128) | 自定义正则（`mask_pattern=custom` 时使用） |

索引 `idx_node_entity (approval_node_id, entity_type)` 支持按节点 + 实体高效查询权限配置。

### 2.5 场景 6.1 验收结论

**✅ PASS** — 敏感字段脱敏链路完整且与设计文档一致：

1. 5 步脱敏流程（权限加载 → 业务数据加载 → maskMap 脱敏 → buildMaskedFields 元数据 → 历史加载）与设计 §3.5 伪代码逐行对应
2. 4 种脱敏规则（phone-mask / amount-mask / email-mask / custom）实现正确，边界兜底健壮，示例值验证通过
3. 三态权限（VISIBLE/MASKED/HIDDEN）处理符合设计：HIDDEN 字段从 businessData 移除，MASKED 字段脱敏后返回并附带元数据
4. 前端 `SensitiveFieldDisplay` 组件正确展示脱敏提示图标（ⓘ）与规则说明 Tooltip
5. 数据表 `pms_approval_field_permission` 结构支持按节点 + 实体 + 字段维度配置

---

## 3. 场景 6.2：审批历史保留（含多轮次）

### 3.1 验收链路

```
退回/撤回后重新提交
  ↓
POST /api/workflow/approval/{id}/resubmit
  ↓
ApprovalCenterServiceImpl.resubmit (行 202-240)
  ├─ 1. 校验状态（仅 REJECTED/WITHDRAWN 可重新提交）
  ├─ 2. 复用原记录：round+1，status 回 PENDING，重置 submittedAt/completedAt
  ├─ 3. 重置所有节点为 PENDING，currentNodeName 指向第一个节点
  └─ 4. recordHistory 追加 RESUBMIT 历史（newRound）
  ↓
查询历史
  ↓
GET /api/workflow/approval/{id}/history
  ↓
ApprovalCenterServiceImpl.listHistory (行 274-279)
  └─ ORDER BY round ASC, operatedAt ASC（多轮次全部返回）
  ↓
前端 approval-history/index.vue + ApprovalTimeline.vue
  └─ 按 round 分组渲染，每组内按 operatedAt 升序
```

### 3.2 后端实现审查

#### 3.2.1 `resubmit` 复用原记录（`ApprovalCenterServiceImpl.java` 行 202-240）

```java
@Transactional(rollbackFor = Exception.class)
public ApprovalRecord resubmit(Long recordId, String comment) {
    ApprovalRecord record = mustGetRecord(recordId);
    // 仅 REJECTED/WITHDRAWN 状态可重新提交
    if (!"REJECTED".equals(record.getStatus()) && !"WITHDRAWN".equals(record.getStatus())) {
        throw new BusinessException("仅退回或撤回状态的审批可重新提交，当前状态：" + record.getStatus());
    }
    // 复用原记录：round+1，状态回 PENDING
    int newRound = (record.getRound() == null ? 1 : record.getRound()) + 1;
    record.setRound(newRound);
    record.setStatus("PENDING");
    record.setSubmittedAt(LocalDateTime.now());
    record.setCompletedAt(null);
    this.updateById(record);  // ✅ 复用原记录（updateById），非新建
    // 重置所有节点为 PENDING
    List<ApprovalNode> nodes = listNodes(record.getId());
    for (ApprovalNode n : nodes) {
        n.setStatus("PENDING"); n.setOpinion(null);
        n.setApproverActualId(null); n.setOperatedAt(null);
        approvalNodeMapper.updateById(n);
    }
    // 当前节点指向第一个节点
    if (!nodes.isEmpty()) {
        ApprovalNode firstNode = nodes.stream().min(...getNodeOrder()).orElse(null);
        if (firstNode != null) {
            record.setCurrentNodeName(firstNode.getNodeName());
            this.updateById(record);
        }
    }
    recordHistory(record.getId(), newRound, "提交人", record.getSubmitterId(),
            record.getSubmitterName(), "RESUBMIT", comment);  // ✅ 追加 RESUBMIT 历史
    return record;
}
```

**审查结论**：与设计 §3.5 行 472-476「`round += 1`，**复用原 ApprovalRecord**（不新建），但 ApprovalHistory 追加新行」完全一致：
- `this.updateById(record)` 复用原记录（非 insert 新记录）
- `round+1`、`status` 回 `PENDING`、`completedAt` 清空
- 所有节点重置为 `PENDING`，`currentNodeName` 重新指向第一个节点（符合「重新从第一个节点开始」语义）
- `recordHistory` 追加 `RESUBMIT` 动作历史，`round=newRound`

#### 3.2.2 `recordHistory` 历史追加（行 372-385）

每次操作（SUBMIT/APPROVE/REJECT/WITHDRAW/RESUBMIT/ESCALATE/TIMEOUT）均调用 `recordHistory` 追加一行 `ApprovalHistory`，记录 `recordId`/`round`/`nodeName`/`operatorId`/`operatorName`/`action`/`opinion`/`operatedAt`。历史表只追加不更新，保证多轮次完整追溯。

#### 3.2.3 `listHistory` 多轮次查询（行 274-279）

```java
public List<ApprovalHistory> listHistory(Long recordId) {
    return approvalHistoryMapper.selectList(new LambdaQueryWrapper<ApprovalHistory>()
            .eq(ApprovalHistory::getRecordId, recordId)
            .orderByAsc(ApprovalHistory::getRound)        // ✅ round 升序
            .orderByAsc(ApprovalHistory::getOperatedAt)); // ✅ operatedAt 升序
}
```

**审查结论**：与设计 §3.5 行 478-483 SQL `ORDER BY round ASC, operatedAt ASC` 完全一致，返回全部轮次历史。

#### 3.2.4 状态机完整性（`ApprovalCenterServiceImpl` 行 57-240）

| 操作 | 方法 | 状态变化 | round 变化 | 历史动作 |
|---|---|---|---|---|
| 创建审批 | `createApproval`（行 57-78） | → PENDING | round=1 | SUBMIT |
| 通过 | `approve`（行 120-153） | 当前节点 APPROVED → 下一节点或最终 APPROVED | 不变 | APPROVE |
| 退回 | `reject`（行 157-177） | 当前节点 REJECTED | 不变 | REJECT |
| 撤回 | `withdraw`（行 181-198） | → WITHDRAWN | 不变 | WITHDRAW |
| 重新提交 | `resubmit`（行 202-240） | → PENDING | **round+1** | RESUBMIT |

状态机与设计 §3.5 行 433-442 状态图完全一致。

### 3.3 前端实现审查

#### 3.3.1 `ApprovalTimeline.vue`（176 行）— 多轮次时间轴

- `groupedRounds` 计算属性（行 24-42）：按 `round` 分组，每组内按 `operatedAt` 升序排序，组间按 `round` 升序
- 模板（行 103-128）：每个轮次一个 `round-block`，头部展示「第 N 轮 · M 条记录」标签，内部 `el-timeline` 渲染该轮次所有操作
- 7 种 action 标签颜色映射（行 44-63）：APPROVE=success / REJECT=danger / WITHDRAW=info / RESUBMIT=primary / SUBMIT=warning / TIMEOUT=danger / ESCALATE=danger

**审查结论**：多轮次分组渲染逻辑正确，退回后重新提交的审批会在新轮次 block 下展示，体现多轮次追溯，符合设计 §5.7 行 1123-1146 示例（round 1 REJECT → round 1 RESUBMIT → round 2 APPROVE）。

#### 3.3.2 `approval-history/index.vue`（164 行）— 历史明细页

- `roundSummary` 计算属性（行 16-25）：按轮次统计记录数，展示「第 N 轮 · M 条记录」概览
- 明细表格（行 116-134）：展示轮次/节点/操作人/动作/意见/操作时间六列
- 复用 `ApprovalTimeline` 组件渲染时间轴

#### 3.3.3 `approval-detail/index.vue` 轮次展示

- 行 171 描述区「第 {{ record.round || 1 }} 轮」展示当前轮次
- 行 35-37 `canResubmit` 计算属性：仅 `REJECTED` 或 `WITHDRAWN` 状态可重新提交，与后端 `resubmit` 方法行 206 状态校验一致

### 3.4 数据库支持

`V71__create_approval_center.sql`：

- `pms_approval_record` 表 `round` 字段（行 35）：`INT NOT NULL DEFAULT 1 COMMENT '审批轮次'`
- `pms_approval_history` 表（行 79-91）：
  - `round` 字段记录轮次
  - `action` 字段 7 种动作（SUBMIT/APPROVE/REJECT/WITHDRAW/RESUBMIT/ESCALATE/TIMEOUT）
  - 索引 `idx_record_round_time (record_id, round, operated_at)` 支持按记录 + 轮次 + 时间高效查询

### 3.5 场景 6.2 验收结论

**✅ PASS** — 审批历史保留（含多轮次）链路完整且与设计文档一致：

1. `resubmit` 复用原 `ApprovalRecord`（`updateById` 非 insert），`round+1`，状态回 `PENDING`，符合设计「复用原记录，不新建」
2. 所有节点重置为 `PENDING`，`currentNodeName` 指向第一个节点，符合「重新从第一个节点开始」
3. `recordHistory` 追加 `RESUBMIT` 动作历史，`round=newRound`，历史表只追加不更新
4. `listHistory` 按 `round ASC, operatedAt ASC` 返回全部轮次历史，与设计 SQL 一致
5. 前端 `ApprovalTimeline` 按 `round` 分组渲染，多轮次 block 分隔展示，符合设计 §5.7 示例
6. 状态机 5 态（PENDING/APPROVED/REJECTED/WITHDRAWN/TIMEOUT）+ 7 种历史动作完整实现

---

## 4. 发现的技术债

### TD-P8-013（低）：`buildMaskedFields` 对 MASKED 字段重复脱敏计算

- **位置**：`ApprovalCenterController.java` 行 140 + 行 271-273
- **现象**：`detail` 方法行 140 已通过 `maskMap` 对 `businessData` 整体脱敏得到 `maskedData`，行 143 `buildMaskedFields` 又对同一批 MASKED 字段调用 `sensitiveFieldMasker.mask` 重新脱敏一次（基于原始 `businessData`）。两次脱敏结果一致（脱敏对原始值是幂等的），但存在重复计算。
- **影响**：轻微性能浪费（每个 MASKED 字段脱敏两次），无功能正确性问题。
- **建议**：可让 `buildMaskedFields` 直接复用 `maskedData` 中已脱敏的值，避免重复计算。优先级低。

### TD-P8-014（低）：HIDDEN 字段在 `maskedFields` 元数据中冗余

- **位置**：`ApprovalCenterController.java` 行 263-269
- **现象**：`buildMaskedFields` 为 HIDDEN 字段生成 `MaskedFieldVO{permission=HIDDEN, maskedValue=null}` 元数据项。但 `maskMap`（行 76-94）已将 HIDDEN 字段从 `businessData` Map 中移除，前端 `approval-detail/index.vue` 行 204-216 通过 `v-for` 遍历 `businessData` 渲染，HIDDEN 字段不会显示。因此 `maskedFields` 中的 HIDDEN 项实际无消费者。
- **影响**：响应体冗余字段（每个 HIDDEN 字段多一个 `MaskedFieldVO` 对象），无功能正确性问题。
- **建议**：可在 `buildMaskedFields` 中跳过 HIDDEN 字段（前端 `SensitiveFieldDisplay` 已有 `isHidden` 兜底显示「（已隐藏）」）。优先级低。

---

## 5. 总体验收结论

| 验收场景 | 结论 | 说明 |
|---|---|---|
| 6.1 敏感字段脱敏 | **✅ PASS** | 5 步脱敏链路完整，4 种 maskPattern 实现正确，三态权限处理符合设计，前端脱敏提示交互完整 |
| 6.2 审批历史保留 | **✅ PASS** | resubmit 复用原记录 round+1，历史表只追加不更新，listHistory 多轮次升序返回，前端按轮次分组渲染 |

**Story 6 总体验收：✅ PASS**

- 11 个 API 端点前后端全覆盖，权限码 `workflow:approval:handle` 配置正确
- 字段脱敏（场景 6.1）与历史保留（场景 6.2）两个核心验收点全部通过
- 发现 2 项低优先级技术债（TD-P8-013 重复脱敏计算、TD-P8-014 HIDDEN 元数据冗余），均无功能正确性影响
- 状态机 5 态 + 7 种历史动作完整实现，与设计 §3.5 状态图一致
- 4 种脱敏规则示例值验证全部通过（`13812345678`→`138****5678`、`12345.67`→`12***.67`、`alice@example.com`→`a***@example.com`）

**注**：审批触发规则矩阵（设计 §3.5 行 486-499，10 类业务事件触发审批）属于跨 Story 集成问题，其中 `BASELINE_CHANGE` 审批未实际触发已记录为 TD-P8-008（Task 9 发现），不在 Story 6 单体验收范围内。

---

## 6. 审查文件清单

### 后端（pms-workflow）

| 文件 | 行数 | 用途 |
|---|---|---|
| `controller/ApprovalCenterController.java` | 284 | 11 个 API 端点 + detail 5 步脱敏 + buildMaskedFields |
| `service/SensitiveFieldMasker.java` | 163 | 4 种脱敏规则 + maskMap 三态处理 |
| `service/impl/ApprovalCenterServiceImpl.java` | 386 | resubmit 复用原记录 + listHistory 多轮次 + 状态机 |
| `entity/ApprovalRecord.java` | 103 | 审批记录实体（round 字段） |
| `entity/ApprovalHistory.java` | 76 | 审批历史实体（7 种 action） |
| `entity/ApprovalFieldPermission.java` | 67 | 字段权限实体（3 态 permission + 4 种 maskPattern） |
| `vo/ApprovalDetailVO.java` | 39 | 详情 VO（record + businessData + maskedFields + history） |
| `vo/MaskedFieldVO.java` | 35 | 脱敏字段元数据 VO |

### 前端（pms-frontend）

| 文件 | 行数 | 用途 |
|---|---|---|
| `src/api/approval-center.ts` | 164 | 11 个 API 封装 + TypeScript 接口 |
| `src/views/workflow/approval-detail/index.vue` | 282 | 审批详情页（脱敏展示 + 操作按钮 + 历史） |
| `src/views/workflow/approval-history/index.vue` | 164 | 历史明细页（轮次概览 + 时间轴 + 表格） |
| `src/components/SensitiveFieldDisplay.vue` | 92 | 脱敏字段展示组件（ⓘ 图标 + Tooltip） |
| `src/components/ApprovalTimeline.vue` | 176 | 多轮次时间轴组件（按 round 分组） |

### 数据库（pms-admin）

| 文件 | 行数 | 用途 |
|---|---|---|
| `src/main/resources/db/migration/V71__create_approval_center.sql` | 114 | 4 张表（record/node/history/field_permission） |

### 设计文档

| 章节 | 行号 | 内容 |
|---|---|---|
| §3.5 | 429-500 | 审批中心统一规则（状态机 + 脱敏 + 历史保留 + 触发矩阵） |
| §5.7 | 1080-1147 | 统一审批中心 API（11 端点 + 验收 1/2 响应示例） |
| §9.1 | 2553-2554 | 验收场景对照表 Story 6 行 |
