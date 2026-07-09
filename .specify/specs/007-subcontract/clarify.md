# 007-subcontract 域歧义澄清决策(Clarify)

> 日期: 2026-07-09
> 来源: `docs/reverse-engineering/007-subcontract/ambiguities.md`(AMB-007-01 ~ AMB-007-15)
> 处理方式: 逐条采纳各 AMB 的"建议决策"作为最终决策;标注 [暂定决策] 的为待代码/业务最终确认的工作假设,标注 [采纳] 的为可立即固化的明确决策。
> 每条字段: 决策结论 / 依据 / 影响范围 / 回滚提示

---

## AMB-007-01: 转包审批意见的物理存储表归属未确认

- **决策结论**: [暂定决策] 审批意见复用流程引擎通用评论表(推测为 Activiti `act_comment`),通过 `taskId`/`processInstanceId` 关联转包流程实例;`SubcontractComment` 为视图投影(VO),无独立转包评论物理表。作为工作假设并标记为高风险项,待业务/数据架构最终确认物理表名与字段映射。
- **依据**: spec 与 draft 均明确"继承自通用评论,无独立表";`querySubcontractCommentList` 返回 `List<Map<String,Object>>` 非强类型,符合 VO 投影特征。
- **影响范围**: FR-2.6.1/2.6.2、Key Entities 3.8、SC-004 流程可追溯、Edge Case"回访不通过回到付款申请后再次流转"的历史意见保留。
- **回滚提示**: 若业务确认存在独立转包评论表,需在 spec 3.8 补录 DDL 与字段映射,将 FR-2.6.2 注释改为引用独立表,并迁移已存评论数据。

---

## AMB-007-02: 两套主流程与回访流程定义(Subcontract.bpmn vs Subcontract2.bpmn)的启用条件与差异未明确

- **决策结论**: [暂定决策] 以无后缀版本 `Subcontract.bpmn` 与 `SubcontractCallBack.bpmn` 为基准生效流程;`Subcontract2.bpmn`/`SubcontractCallBack2.bpmn` 视为待业务确认的并行/灰度/历史版本,本规格不纳入其差异细节。代码常量 `PROCESS_SUBCONTRACT_KEY="Subcontract"`、`PROCESS_CALLBACK_KEY="SubcontractCallBack"` 指向无后缀版本,印证基准选择。
- **依据**: 代码常量指向无后缀版本,为现行生效定义;`Subcontract2` 的存在意图未文档化。
- **影响范围**: SC-010、SC-004 流程一致性、状态码与 result 映射的通用性、运维排障、未来流程升级路径。
- **回滚提示**: 若业务确认 `Subcontract2` 在用,需补录分流规则与差异矩阵,并将受影响 FR 标注分流条件。

---

## AMB-007-03: 独立的转包验收流程(SubcontractInspection)与主流程 ACCEPTANCE_TASK 的关系未明确

- **决策结论**: [暂定决策] 验收流程(`SubcontractInspection`/`SubcontractInspectionListener`)与主流程 `ACCEPTANCE_TASK` 的关系待业务确认,本规格暂不纳入验收流程细节;仅记录其存在,不影响主流程 FR。
- **依据**: spec 与 draft 均未说明联动关系;在未确认前纳入会引入未验证假设。
- **影响范围**: SC-010、验收环节状态一致性、验收数据归属、与付款/闭环的时序关系。
- **回滚提示**: 若确认为子流程,在 spec 第 2 章补录验收子流程章节;若为独立流程,补录触发条件与数据回写机制。

---

## AMB-007-04: 转包→采购联动监听器(Subcontract2PurchaseListener)的触发条件与传递数据未明确

- **决策结论**: [暂定决策] 转包→采购联动(`Subcontract2PurchaseListener`)的触发时机、传递数据、同步/异步模式与失败策略待业务确认,本规格暂不纳入联动细节;仅记录监听器存在。
- **依据**: spec 与 draft 均未说明联动契约;纳入未验证假设有跨域一致性风险。
- **影响范围**: SC-011、跨域数据一致性、采购域的转包联动可用性、主流程性能与可靠性。
- **回滚提示**: 若为异步通知,补录事件主题与 payload 字段;若为同步调用,补录接口契约与失败回滚策略。

---

## AMB-007-05: 转包主表 state 状态码 40 与 -100 未定义

- **决策结论**: [暂定决策] 状态码字典暂以已明确部分为准:0=草稿、10=待审批、15=受益部门服务经理通过、-15=驳回、20=工程管理部主管通过、-20=驳回、30=办事处主任通过、-30=驳回、100=已闭环。40 与 -100 语义待代码反查 `SubcontractConstant.SubcontractStatus` 补全;暂推测 40=已生成合同号(进入付款阶段)、-100=已终止/已作废。
- **依据**: draft 已明确 8 个状态码;40/-100 仅在字段注释列出,无 FR/常量引用。
- **影响范围**: Key Entities 3.1 state 字段、FR-2.1.9 按状态查询与导出、报表统计、状态流转图、与 callbackState 正交性。
- **回滚提示**: 代码反查后若 40/-100 语义与推测不符,需修正 state 字段注释并更新状态流转图。

---

## AMB-007-06: 办事处主任审批触发条件表述不一致(涉及工程服务费 vs 超过阈值)

- **决策结论**: [采纳] 办事处主任审批触发条件为"转包涉及工程服务费 且 办事处在配置项 `subcontract.areaLeader.auditEngineeFee.offices` 列表中",不存在金额阈值;draft US-1.4 中"超过工程服务费阈值"为口语化表述,统一删除。
- **依据**: spec FR-2.1.5 与 draft FR-2.1.5 一致,配置项名仅含 offices 列表,无金额阈值字段。
- **影响范围**: FR-2.1.5、User Story 1.4、SC-003 权限与可见性、zrApproveTime 写入时机。
- **回滚提示**: 若代码反查 `approveZRTask` 触发表达式证实存在金额阈值,需补录阈值配置项并修订触发条件。

---

## AMB-007-07: 转包被驳回后重新提交的流程语义未明确(新建流程 vs 恢复,历史意见保留机制)

- **决策结论**: [采纳] 驳回=终止原流程实例;重新提交=新建流程实例,`state` 回退到 0(草稿)后由提交动作置为 10(待审批);历史审批意见按 `subcontractId` 累积,跨流程实例关联可追溯。
- **依据**: FR-2.1.3/2.1.4/2.1.5 明确"驳回=终止流程";User Story 7.1 要求重新编辑提交且历史意见可追溯;按 subcontractId 累积意见可实现跨实例追溯。
- **影响范围**: User Story 7.1、Edge Case"转包被驳回后重新提交"、FR-2.1.8、SC-004、state 状态机、selectRejectedSubcontractProjectList 查询逻辑。
- **回滚提示**: 若代码证实复用原流程实例(state 回退到 10),需修订 state 流转与意见保留机制描述。

---

## AMB-007-08: 回访问卷"每次保存/提交重新生成一份问卷数据"的语义未明确(新增 vs 更新)

- **决策结论**: [采纳] 每次保存/提交 INSERT 新记录到 `pm_subcontract_project_callback`,`quesnaireVersion` 递增;查询时按版本号取最新(`selectMaxSubcontractCallback`);旧记录保留用于审计,不做逻辑删除。
- **依据**: `insertSubcontractQuesnaire` 与 `selectMaxSubcontractCallback` 命名暗示"插入新记录 + 取最新版本"机制。
- **影响范围**: FR-2.5.2、User Story 3.3、pm_subcontract_project_callback 数据增长、quesnaireVersion 语义、回访历史追溯、customInfo 增量合并基准记录选择。
- **回滚提示**: 若代码证实为 UPDATE 现有记录(仅版本号递增),需修订问卷版本管理策略描述。

---

## AMB-007-09: customInfo "增量合并不覆盖历史键" 与 JSON_MERGE_PATCH 语义差异及并发安全未明确

- **决策结论**: [采纳] customInfo 增量合并的精确定义为"同 key 覆盖、不同 key 保留",通过 `JSON_MERGE_PATCH(IFNULL(customInfo,'{}'), ...)` 实现;原"不覆盖历史键"表述修正为该定义。并发场景依赖应用层串行化(单转包编辑串行),不引入额外乐观锁。
- **依据**: MySQL `JSON_MERGE_PATCH` 语义为"同 key 覆盖、新 key 追加";原"不覆盖历史键"字面表述与实际语义存在歧义。
- **影响范围**: Key Entities 3.1/3.5/3.6 customInfo 字段、Edge Case"扩展信息并发更新时"、SC-002 数据完整性。
- **回滚提示**: 若代码证实存在乐观锁(updateTime/版本号 CAS),需补录并发控制策略;若并发风险升级,引入应用层串行化或乐观锁。

---

## AMB-007-10: 发票号去重统计时同号异金额的冲突解决策略未明确

- **决策结论**: [采纳] 同发票号取最新上传(按 uploadTime/id 倒序)的金额计入统计,并在 UI 提示存在重复发票号。
- **依据**: "去重"要求唯一计数,取最新上传避免重复计数且符合业务直觉。
- **影响范围**: FR-2.4.4、User Story 4.1、已识别发票金额合计准确性(SC-002 相关)、付款合规与财务对账。
- **回滚提示**: 若代码反查 `querySubcontractPayment` 去重逻辑证实为其他策略(取第一条/报错),需修订处理规则。

---

## AMB-007-11: 发票识别与查验的执行方未明确(本地 OCR vs 外部服务)

- **决策结论**: [暂定决策] 发票识别与查验的执行方待代码反查 `verifySubcontractPaymentDeliver` 实现确认;暂以"PMS 通过 `verifySubcontractPaymentDeliver` 调用外部查验服务并回写 customInfo 状态(identify/needVerify/verified_status)"为工作假设,列入外部系统集成(SC-007)。
- **依据**: spec FR-2.4.4 从 customInfo 读取状态,暗示结果由外部写入;`verifySubcontractPaymentDeliver` 命名暗示主动调用外部服务。
- **影响范围**: FR-2.4.4、User Story 4.2、SC-007 外部集成可用性、外部依赖项识别。
- **回滚提示**: 若代码证实为人工录入/本地 OCR,需修订执行方描述并调整外部集成清单。

---

## AMB-007-12: FR-2.1.7 转包闭环 与 FR-2.4.5 工程管理部付款闭环 的关系(是否重复定义)

- **决策结论**: [采纳] FR-2.1.7 与 FR-2.4.5 描述同一节点(`approvePaymentTask` 后置网关 `exclusivegateway7`);FR-2.1.7 为状态结果(state=100),FR-2.4.5 为操作选项。合并描述,FR-2.1.7 改为引用 FR-2.4.5,明确"闭环操作发生在该网关,选择闭环时 state 置为 100(已闭环),流程结束"。
- **依据**: 两条 FR 描述同一网关的闭环行为,属同义重复,易误导读者认为存在两个闭环点。
- **影响范围**: FR-2.1.7、FR-2.4.5、SC-004 流程可追溯、闭环状态码 100 触发路径唯一性、spec 可读性。
- **回滚提示**: 若代码证实存在两个独立闭环入口(主流程与替代流程),需恢复独立描述并补录分流条件。

---

## AMB-007-13: 审批意见状态"2 可闭环 / -2 不可闭环"与流程 result 的映射缺失

- **决策结论**: [暂定决策] `-2 不可闭环` 视为评论状态(非流程 result),由审批人在评论中手动选择,不影响流程路由;流程网关 `exclusivegateway7` 仅以 `result=2`(闭环)/`5`(余款未清)/`-1`(驳回)路由。待代码反查 `CommentStatus.CANNOT_CLOSE` 写入点最终确认。
- **依据**: BPMN 网关仅出现 result=2/5/-1,未见 result=-2 路由;2/-2 更符合评论状态语义而非流程路由 result。
- **影响范围**: FR-2.6.2 审批状态枚举、闭环路由正确性、SC-004 流程可追溯。
- **回滚提示**: 若代码证实 -2 由 `normalApproveTask` 产生并影响路由,需补录产生场景与路由目标。

---

## AMB-007-14: isAccrued(是否已计提)与 isInvoiced(是否已开票)字段的维护时机与责任方未明确

- **决策结论**: [暂定决策] `isAccrued`(是否已计提)与 `isInvoiced`(是否已开票)由外部财务系统(SSE/FP)通过定时任务回写;列入外部系统集成(SC-007)。待代码反查写入 SQL 最终确认维护方与时机。
- **依据**: 字段分级为 D(系统派生/流程写入),全部 FR(2.1-2.6)与 NFR 无引用,符合外部回写特征;NFR-4.6 提及发票同步至 FP。
- **影响范围**: Key Entities 3.1、FR-2.1.9 列表查询与导出(是否含计提/开票状态)、与 FP 数据一致性、SC-007。
- **回滚提示**: 若代码证实为人工勾选或未文档化接口维护,需修订维护时机与责任方,并新增对应 FR。

---

## AMB-007-15: 单独发起回访流程与主流程内回访节点的数据区分与场景边界未明确

- **决策结论**: [采纳] 主流程内回访节点(`callbackTask`)为常规路径;独立回访流程(`SubcontractCallBack`)仅用于主流程已闭环后补回访;两种路径产生的 `pm_subcontract_project_callback` 记录通过 `taskKey` 区分;独立流程完成后回写 `callbackState`,按 `subcontractId` 关联与权限校验。
- **依据**: 主流程内回访为正向链路常规环节;独立流程存在但使用场景需限定,避免 callbackState 写入路径分裂。
- **影响范围**: FR-2.5.1、回访数据一致性、callbackState 写入路径唯一性、回访历史查询、SC-004 流程可追溯。
- **回滚提示**: 若代码反查 `startCallBackFlow` 调用入口证实独立流程为常规路径或与主流程等价,需修订两种路径的触发条件与区分规则。

---

## 摘要

- **AMB 决策数**: 15(其中 [采纳] 7 条:AMB-006/007/008/009/010/012/015;[暂定决策] 8 条:AMB-001/002/003/004/005/011/013/014)
- **[待澄清] 清零**: spec_updated.md 中 `[待澄清]` 出现 0 次(原 4 处已全部替换为决策结论或 [暂定决策:...])。
- **高风险暂定项(待代码/业务最终确认)**:
  1. **AMB-007-01**(审批意见物理存储表归属):影响 SC-004 流程可追溯与历史意见保留,为 spec 内部原 [待澄清] 项。
  2. **AMB-007-02**(两套主流程定义 Subcontract vs Subcontract2):影响流程一致性与状态机基础,若 Subcontract2 在用则所有 FR 可能只对一半转包生效。
  3. **AMB-007-05**(state 状态码 40 与 -100 未定义):状态机字典不完整,影响列表查询、导出与报表。
