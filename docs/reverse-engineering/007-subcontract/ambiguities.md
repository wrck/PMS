# 007-subcontract 域歧义清单(Ambiguities)

> 日期: 2026-07-09
> 歧义总数: 15
> 比对来源: `.specify/specs/007-subcontract/spec.md`(下称 spec)、`docs/reverse-engineering/007-subcontract/spec-draft.md`(下称 draft)、PMS-struts subcontract 代码反推证据
> 比对维度: ① 代码 vs 文档 ② 代码 vs 代码 ③ spec 内部 [待澄清] 展开

---

## AMB-007-01: 转包审批意见的物理存储表归属未确认

- **位置**: spec FR-2.6.2、Key Entities 3.8 [待澄清];draft FR-2.6.2、3.8 [待澄清]
- **现象**: spec 与 draft 均明确"审批意见存储于流程引擎通用评论表(继承自通用评论),无独立转包评论表",但未确认具体物理表名(推测为 `act_comment` 或类似 Activiti 通用评论表),也未确认 `SubcontractComment` VO 与通用评论表的字段映射关系。`SubcontractService.querySubcontractCommentList` 返回 `List<Map<String,Object>>`(draft L452),非强类型,字段语义靠约定。
- **候选解释**:
  1. 审批意见复用 Activiti `act_comment` 表,通过 `taskId`/`processInstanceId` 关联转包流程实例;
  2. 存在独立的转包评论表但代码中未显式映射(通过 VO 投影);
  3. 评论数据分散在流程变量与评论表两处。
- **影响面**: 审批意见的可追溯性(SC-004)、历史意见保留(Edge Case"回访不通过回到付款申请后再次流转")、数据迁移与跨流程引擎迁移、报表查询性能。
- **建议决策**: 由业务/数据架构确认物理表归属与字段映射;若复用 `act_comment`,在 spec 中补录表名与关联键;若存在独立表,补录 DDL。在未确认前,暂以"复用流程引擎通用评论表"为工作假设,但标记为高风险项。

---

## AMB-007-02: 两套主流程与回访流程定义(Subcontract.bpmn vs Subcontract2.bpmn)的启用条件与差异未明确

- **位置**: spec SC-010 [待澄清];draft NFR-4.10 [待澄清]
- **现象**: bpmn 目录同时存在 `Subcontract.bpmn` 与 `Subcontract2.bpmn`、`SubcontractCallBack.bpmn` 与 `SubcontractCallBack2.bpmn` 两套流程定义。spec 与 draft 均未说明二者的启用条件、差异点(节点/网关/路由)、何时切换、是否共存。代码中 `PROCESS_SUBCONTRACT_KEY="Subcontract"`、`PROCESS_CALLBACK_KEY="SubcontractCallBack"`(draft L77、L82)指向无后缀版本,但 `Subcontract2` 的存在暗示存在并行或替代路径。
- **候选解释**:
  1. `Subcontract2` 是新版本流程,处于灰度/未启用状态;
  2. 二者按组织/业务线分流(如不同公司用不同流程);
  3. `Subcontract2` 是历史遗留或回退方案;
  4. 二者节点差异显著(如 `Subcontract2` 取消了办事处主任审批或调整了回访分支)。
- **影响面**: 流程一致性(SC-004)、流程可追溯、状态码与 result 映射的通用性、运维排障、未来流程升级路径。
- **建议决策**: 由业务确认 `Subcontract2`/`SubcontractCallBack2` 的现状(启用/废弃/灰度);若废弃,在 spec 中明确"仅 `Subcontract.bpmn` 生效"并清理 `Subcontract2`;若并行,补录分流规则与差异矩阵。在未确认前,spec 应明确"以无后缀版本为基准"。

---

## AMB-007-03: 独立的转包验收流程(SubcontractInspection)与主流程 ACCEPTANCE_TASK 的关系未明确

- **位置**: spec SC-010 [待澄清];draft NFR-4.10 [待澄清]
- **现象**: 存在 `SubcontractInspectionListener` 与 `PROCESS_INSPECTION_KEY="SubcontractInspection"`(独立转包验收流程),同时主流程存在 `ACCEPTANCE_TASK` 任务节点(draft TaskKey L228)。spec 与 draft 均未说明:① 验收流程是主流程的子流程还是独立流程;② `ACCEPTANCE_TASK` 由谁触发、与 `SubcontractInspection` 如何联动;③ `SubcontractInspectionListener` 监听的事件源与触发时机。
- **候选解释**:
  1. `SubcontractInspection` 是主流程 `ACCEPTANCE_TASK` 的子流程,完成后回写主流程;
  2. 二者完全独立,`SubcontractInspectionListener` 监听主流程事件并启动独立验收流程;
  3. `ACCEPTANCE_TASK` 已废弃,被 `SubcontractInspection` 替代。
- **影响面**: 验收环节的状态一致性、流程可追溯(SC-004)、验收数据归属、与付款/闭环的时序关系。
- **建议决策**: 由业务确认验收流程与主流程的关系;若为子流程,在 spec 第 2 章补录验收子流程章节;若为独立流程,补录触发条件与数据回写机制。在未确认前,不在 spec 中纳入验收流程细节,仅标记为待澄清。

---

## AMB-007-04: 转包→采购联动监听器(Subcontract2PurchaseListener)的触发条件与传递数据未明确

- **位置**: spec SC-011 [待澄清];draft NFR-4.11 [待澄清]
- **现象**: 存在 `Subcontract2PurchaseListener` 监听器(draft listener/ 目录),spec 与 draft 均未说明:① 监听的转包事件(创建/审批通过/生成合同号/闭环);② 向采购域传递的数据字段;③ 是否阻塞主流程(同步)还是异步通知;④ 失败处理策略。
- **候选解释**:
  1. 转包生成合同号后触发,向采购系统下发采购订单;
  2. 转包创建即触发,预占采购预算;
  3. 异步事件通知,不阻塞主流程,失败仅告警。
- **影响面**: 跨域数据一致性、采购域的转包联动可用性、主流程性能与可靠性。
- **建议决策**: 由业务确认联动时机与数据契约;若为异步通知,在 spec 中补录事件主题与 payload 字段;若为同步调用,补录接口契约与失败回滚策略。在未确认前,spec 不纳入联动细节,仅标记为待澄清。

---

## AMB-007-05: 转包主表 state 状态码 40 与 -100 未定义

- **位置**: spec Key Entities 3.1 `state` 字段注释(列出 0/10/15/-15/20/-20/30/40/100/-100);draft 3.1 同;draft FR-2.1.x 引用 `SubcontractStatus`(L283-328)
- **现象**: spec 与 draft 在 `state` 字段注释中列出 10 个状态码,但 draft 仅明确:草稿=0、待审批=10、PROFIT_SM_AGREE=15/REJECT=-15、工程管理部通过=20/REJECT=-20、AREA_AGREE=30/REJECT=-30、CLOSED=100。`40` 与 `-100` 两个状态码无任何 FR 或常量引用说明其语义。
- **候选解释**:
  1. `40` = 已生成合同号(进入付款阶段);`-100` = 已终止/已作废;
  2. `40` = 付款中;`-100` = 闭环后撤销;
  3. `40` = 回访中;`-100` = 系统强制终止。
- **影响面**: 状态机完整性、列表查询与导出(FR-2.1.9 按状态查询)、报表统计、状态流转图绘制、与 `callbackState` 的正交性。
- **建议决策**: 由代码反查 `SubcontractConstant.SubcontractStatus` 常量定义补全 40 与 -100 的语义;在 spec 中补录完整状态码字典与状态流转图。在未确认前,状态码字典不完整,影响状态相关查询与报表。

---

## AMB-007-06: 办事处主任审批触发条件表述不一致(涉及工程服务费 vs 超过阈值)

- **位置**: spec FR-2.1.5、User Story 1.4;draft US-1.4、FR-2.1.5
- **现象**: 三处表述不一致:
  - spec FR-2.1.5:"当转包涉及工程服务费且其办事处在配置的'需主任审批办事处列表'中时触发";
  - spec User Story 1.4:"转包涉及工程服务费且其办事处在'需主任审批'的配置列表中";
  - draft US-1.4:"对**超过工程服务费阈值**的转包申请进行最终审批";
  - draft FR-2.1.5:"转包涉及工程服务费且办事处在配置项 `subcontract.areaLeader.auditEngineeFee.offices` 列表中"。
  draft US-1.4 引入了"阈值"概念,但 spec 与 draft FR 均未提及阈值,配置项名 `auditEngineeFee.offices` 也仅是办事处列表,无金额阈值。
- **候选解释**:
  1. "阈值"是 US-1.4 的口语化表述,实际无金额阈值,只要"涉及工程服务费"且办事处在列表中即触发;
  2. 存在未文档化的金额阈值配置,超过阈值才触发主任审批;
  3. 办事处列表本身隐含阈值(不同办事处阈值不同)。
- **影响面**: 主任审批节点的触发准确性、配置项完整性、SC-003 权限与可见性、与 `zrApproveTime` 的写入时机。
- **建议决策**: 由代码反查 `Subcontract.bpmn` 中 `approveZRTask` 的触发表达式与 `AREA_LEADER_AUDIT_ENGINEE_FEE_OFFICES` 配置用法,确认是否存在金额阈值;统一 spec 与 draft 表述,删除"阈值"或补录阈值配置项。建议以"涉及工程服务费 + 办事处在配置列表"为准,除非代码证实存在阈值。

---

## AMB-007-07: 转包被驳回后重新提交的流程语义未明确(新建流程 vs 恢复,历史意见保留机制)

- **位置**: spec User Story 7.1、Edge Case"转包被驳回后重新提交";draft US-1.12、FR-2.1.8
- **现象**: spec FR-2.1.3/2.1.4/2.1.5 明确"驳回=终止流程",FR-2.1.8"流程终止(含审批意见记录)"。但 User Story 7.1 与 Edge Case"转包被驳回后重新提交:状态需正确回退到申请态,审批意见链完整保留"要求驳回后可"重新编辑提交,转包重新进入审批流且保留历史审批意见可追溯"。spec 与 draft 均未说明:① 重新提交是新建流程实例还是恢复原流程;② `state` 如何从 -15/-20/-30"回退到申请态"(回退到 10 还是 0);③ 历史审批意见如何"保留"(同一 `subcontractId` 下累积,还是迁移到新流程实例)。
- **候选解释**:
  1. 驳回后 `state` 回退到 10(待审批),复用原流程实例的待办,历史意见保留在同一流程实例的评论表;
  2. 驳回后 `state` 回退到 0(草稿),重新提交时新建流程实例,历史意见靠 `subcontractId` 跨流程实例关联;
  3. 驳回即作废,需新建转包。
- **影响面**: 驳回复审主链路(User Story 7)、审批意见可追溯(SC-004)、`state` 状态机、流程实例数据归档、`selectRejectedSubcontractProjectList`(draft L530)的查询逻辑。
- **建议决策**: 由代码反查 `selectRejectedSubcontractProjectList` 与重新提交入口(推测 `apply` 或 `audit`),确认 `state` 回退目标与流程实例处理方式;在 spec 中补录驳回重提交的状态流转与意见保留机制。建议明确"驳回=终止原流程,重新提交=新建流程实例,意见按 `subcontractId` 累积"。

---

## AMB-007-08: 回访问卷"每次保存/提交重新生成一份问卷数据"的语义未明确(新增 vs 更新)

- **位置**: spec FR-2.5.2、User Story 3.3;draft FR-2.5.2
- **现象**: spec FR-2.5.2:"每次保存/提交重新生成一份问卷数据,提交时计算问卷得分";draft FR-2.5.2:"每次保存/提交重新生成一份问卷数据;提交时计算问卷得分"。证据引用 `insertSubcontractQuesnaire`(draft L437)与 `selectMaxSubcontractCallback`(draft L450)。但二者均未说明:① "重新生成"是否每次 INSERT 新记录到 `pm_subcontract_project_callback`,导致同一转包存在多份问卷记录;② 保存草稿与提交是否都生成新记录;③ 查询时以哪份为准(`selectMaxSubcontractCallback` 暗示取最新版本);④ "重新生成"是否清理旧记录。
- **候选解释**:
  1. 每次 INSERT 新记录,按 `id` 或 `createTime` 取最新,旧记录保留用于审计;
  2. 每次 INSERT 新记录并逻辑删除旧记录(`effectiveTo` 置为当前时间);
  3. "重新生成"是 UPDATE 现有记录,仅版本号递增。
- **影响面**: `pm_subcontract_project_callback` 数据增长、问卷版本管理、`quesnaireVersion` 字段语义、回访历史追溯、`customInfo` 增量合并的基准记录选择。
- **建议决策**: 由代码反查 `insertSubcontractQuesnaire` 与 `selectMaxSubcontractCallback`,确认是否每次 INSERT 及旧记录处理方式;在 spec 中补录问卷版本管理策略。建议明确"每次保存/提交 INSERT 新记录,按版本号取最新,旧记录保留用于审计"。

---

## AMB-007-09: customInfo "增量合并不覆盖历史键" 与 JSON_MERGE_PATCH 语义差异及并发安全未明确

- **位置**: spec Key Entities 3.1/3.5/3.6 `customInfo` 字段注释;draft NFR-4.2、3.1/3.5/3.6;spec Edge Case"扩展信息并发更新时"
- **现象**: spec 与 draft 均要求 customInfo"更新时增量合并(不覆盖历史键)",draft NFR-4.2 明确使用 `JSON_MERGE_PATCH(IFNULL(customInfo, "{}"), ...)`。但:① MySQL `JSON_MERGE_PATCH` 语义为"对相同 key,新值覆盖旧值;对新 key,追加",这与"不覆盖历史键"的字面表述存在歧义——同 key 仍会被覆盖,只是不删除其他 key;② spec Edge Case 要求"并发更新时必须采用增量合并更新,避免覆盖历史键",但 `JSON_MERGE_PATCH` 在并发场景下并非原子操作(读-改-写),若无乐观锁,两个并发事务仍可能丢失对方新增的 key;③ draft 未提及乐观锁或 CAS 机制。
- **候选解释**:
  1. "不覆盖历史键"指不删除其他 key,同 key 覆盖可接受,并发由应用层串行化保证;
  2. 存在乐观锁(`updateTime` 或版本号)未文档化;
  3. 并发场景罕见,依赖 `JSON_MERGE_PATCH` 的最终一致性即可。
- **影响面**: SC-002 数据完整性("扩展信息更新后历史键 0 丢失")、并发场景的数据正确性、多用户同时编辑付款/回访扩展信息。
- **建议决策**: 明确"增量合并"的精确定义(同 key 覆盖、不同 key 保留);由代码反查是否对 customInfo 更新加乐观锁;若并发风险高,在 spec 中补录并发控制策略(乐观锁或应用层串行化)。建议在 spec 中将"不覆盖历史键"修正为"同 key 覆盖、不同 key 保留,通过 JSON_MERGE_PATCH 实现"。

---

## AMB-007-10: 发票号去重统计时同号异金额的冲突解决策略未明确

- **位置**: spec FR-2.4.4、User Story 4.1;draft FR-2.4.4
- **现象**: spec FR-2.4.4:"按发票号去重统计已识别发票数量与发票总金额";draft FR-2.4.4:"按发票号去重统计已识别发票数量与发票总金额(total_amount)"。二者均未说明:当两个附件的 `customInfo.invoice_number` 相同但 `total_amount` 不同时,以哪个金额计入统计(取第一条、取最大、取最新、求和、报错)。
- **候选解释**:
  1. 取第一条(按附件 id 升序);
  2. 取最新上传的;
  3. 求和(可能导致重复计数,与"去重"矛盾);
  4. 视为数据异常,提示用户修正。
- **影响面**: 已识别发票金额合计的准确性(SC-002 相关)、付款合规(User Story 4)、财务对账。
- **建议决策**: 由代码反查 `querySubcontractPayment`(draft L834-875)的去重逻辑,确认冲突解决策略;在 spec 中补录同号异金额的处理规则。建议明确"同发票号取最新上传的金额,并在 UI 提示存在重复发票号"。

---

## AMB-007-11: 发票识别与查验的执行方未明确(本地 OCR vs 外部服务)

- **位置**: spec FR-2.4.4、User Story 4.2;draft FR-2.4.4、NFR-4.6
- **现象**: spec FR-2.4.4 要求"对类型为发票的附件,从扩展信息读取识别状态、是否需查验、查验状态等字段",暗示识别与查验结果已存在于 `customInfo`(identify、needVerify、verified_status)。但 spec 与 draft 均未说明:① 发票识别(OCR 提取发票号/金额)由谁执行——是 PMS 本地能力、外部 OCR 服务,还是人工录入;② 查验(向税务系统校验发票真伪)由谁执行;③ `verifyPaymentDeliver`/`verifySubcontractPaymentDeliver`(draft L1069、L612)是触发识别/查验,还是仅更新状态。
- **候选解释**:
  1. 识别与查验均由外部服务完成,PMS 仅存储状态;
  2. `verifySubcontractPaymentDeliver` 主动调用外部查验接口并回写状态;
  3. 识别靠人工录入,查验靠定时任务批量调用。
- **影响面**: 发票合规流程的完整性(User Story 4)、外部依赖项识别、SC-007 外部集成可用性。
- **建议决策**: 由代码反查 `verifySubcontractPaymentDeliver` 的实现,确认是否调用外部服务;在 spec 中补录识别与查验的执行方与接口契约(若为外部服务,列入 NFR-4.7 外部系统集成)。

---

## AMB-007-12: FR-2.1.7 转包闭环 与 FR-2.4.5 工程管理部付款闭环 的关系(是否重复定义)

- **位置**: spec FR-2.1.7、FR-2.4.5;draft FR-2.1.7、FR-2.4.5
- **现象**: spec FR-2.1.7:"系统必须在付款审批后置网关选择闭环时,将转包状态置为已闭环";FR-2.4.5:"系统必须在付款审批节点支持:闭环(流程结束)、付款完成余款未清(回到付款申请节点)、驳回(终止)"。两条 FR 均描述同一节点(`approvePaymentTask` 后置 `exclusivegateway7`)的闭环行为,FR-2.1.7 强调状态置 100,FR-2.4.5 强调操作选项。读者易混淆二者是两个独立闭环点还是一个。
- **候选解释**:
  1. 二者描述同一节点,FR-2.1.7 是状态结果,FR-2.4.5 是操作选项,属同义重复;
  2. FR-2.1.7 指付款闭环,FR-2.4.5 指其他闭环(如合同闭环);
  3. 存在两个闭环入口(主流程与替代流程)。
- **影响面**: spec 可读性、闭环状态码 100 的触发路径唯一性、SC-004 流程可追溯。
- **建议决策**: 合并 FR-2.1.7 与 FR-2.4.5 的闭环描述,明确"闭环操作发生在 `approvePaymentTask` 后置网关,选择闭环时 `state` 置为 100(已闭环),流程结束";在 FR-2.1.7 中删除重复描述或改为引用 FR-2.4.5。

---

## AMB-007-13: 审批意见状态"2 可闭环 / -2 不可闭环"与流程 result 的映射缺失

- **位置**: spec FR-2.6.2 审批状态枚举;draft FR-2.6.2、`CommentStatus`(L238-275)
- **现象**: spec FR-2.6.2 列出审批状态含"2 可闭环 / -2 不可闭环",draft `CommentStatus` 同。但 draft BPMN 流程定义中,`approvePaymentTask` 后置网关 `exclusivegateway7` 仅出现 `result=2`(闭环)、`result=5`(余款未清)、`result=-1`(驳回),未见 `result=-2`(不可闭环)对应的流程路由。`-2 不可闭环` 的产生节点、路由目标、与 `2 可闭环` 的区别均未说明。
- **候选解释**:
  1. `-2 不可闭环` 是历史状态,当前流程已废弃;
  2. `-2` 由 `normalApproveTask`(通用审批节点)产生,与 `approvePaymentTask` 不同;
  3. `2`/`-2` 是评论状态而非流程 result,由审批人在评论中手动选择,不影响流程路由。
- **影响面**: 审批状态字典完整性、闭环路由的正确性、SC-004 流程可追溯。
- **建议决策**: 由代码反查 `CommentStatus.CAN_CLOSE=2`/`CANNOT_CLOSE=-2` 的写入点,确认产生节点;在 spec 中补录 `-2` 的产生场景与路由,或标记为废弃状态。

---

## AMB-007-14: isAccrued(是否已计提)与 isInvoiced(是否已开票)字段的维护时机与责任方未明确

- **位置**: spec Key Entities 3.1;draft 3.1
- **现象**: spec 与 draft 在 `pm_subcontract_project_header` 表中列出 `isAccrued`(BIT,D,是否已计提)与 `isInvoiced`(BIT,D,是否已开票)字段,分级为 D(系统派生/流程写入),但全部 FR(2.1-2.6)与 NFR 均无任何条目引用这两个字段——无写入时机、无写入方、无查询用途。draft NFR-4.6 仅提到"发票同步到 FP 系统",未涉及 `isInvoiced` 的本地维护。
- **候选解释**:
  1. 由外部财务系统(如 SSE/FP)通过定时任务回写;
  2. 由人工在转包详情页手动勾选;
  3. 由未文档化的接口/监听器维护(如 `Subcontract2PurchaseListener` 或财务模块)。
- **影响面**: 财务相关字段的准确性、转包列表查询与导出(FR-2.1.9 是否含计提/开票状态)、与发票系统(FP)的数据一致性。
- **建议决策**: 由代码反查 `isAccrued`/`isInvoiced` 的写入 SQL,确认维护方;在 spec 中补录维护时机与责任方,或在 FR 中新增计提/开票状态维护需求。若确为外部回写,列入 NFR-4.7 外部系统集成。

---

## AMB-007-15: 单独发起回访流程与主流程内回访节点的数据区分与场景边界未明确

- **位置**: spec FR-2.5.1;draft FR-2.5.1
- **现象**: spec FR-2.5.1:"系统必须支持单独发起回访流程,或在主流程内进入回访节点";draft FR-2.5.1 引用 `PROCESS_CALLBACK_KEY="SubcontractCallBack"`(独立流程)与主流程内 `callbackTask` 节点。但二者均未说明:① 何时单独发起独立回访流程、何时走主流程内回访节点——是否所有转包都走主流程内回访,独立流程仅用于补回访或历史转包;② 两种路径产生的 `pm_subcontract_project_callback` 记录如何区分(`taskKey` 字段是否不同);③ 独立回访流程完成后如何回写主流程状态(如 `callbackState`);④ 独立流程的 `subcontractId` 关联与权限校验。
- **候选解释**:
  1. 主流程内回访是常规路径,独立流程用于主流程已闭环但需补回访的场景;
  2. 二者完全等价,独立流程是历史遗留;
  3. 独立流程用于跨流程实例的回访(如主流程已终止)。
- **影响面**: 回访数据的一致性、`callbackState` 的写入路径唯一性、回访历史查询、SC-004 流程可追溯。
- **建议决策**: 由代码反查 `startCallBackFlow`(draft L335)的调用入口与前置条件,确认独立流程的使用场景;在 spec 中补录两种路径的触发条件与数据区分规则。建议明确"主流程内回访为常规路径,独立流程仅用于补回访,二者通过 `taskKey` 区分"。

---

## 摘要

- **AMB 总数**: 15
- **关键歧义(按风险排序,最多 3 条)**:
  1. **AMB-007-02**(两套主流程与回访流程定义):直接影响流程一致性与状态机基础,若 `Subcontract2` 在用而 spec 仅描述 `Subcontract`,所有 FR 与状态码可能只对一半转包生效,风险最高。
  2. **AMB-007-05**(state 状态码 40 与 -100 未定义):状态机字典不完整,影响列表查询、导出、报表与状态流转图,是数据契约层面的硬缺口。
  3. **AMB-007-01**(转包审批意见物理存储表归属未确认):影响 SC-004 流程可追溯与历史意见保留,且为 spec 内部 [待澄清] 项,阻碍审批意见相关 FR 的可实现性确认。
