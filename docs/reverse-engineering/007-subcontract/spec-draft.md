# 007-subcontract 域规格草稿(Spec Reverse-Draft)

> 来源:逆向反推自 PMS-struts subcontract 代码,日期 2026-07-09
> 域职责:项目转包全流程——创建/审核/执行/查询/付款/回访/评论
> 核心聚合根:Subcontract(转包单)
> 证据范围:struts-sys.xml(L442-473、L701-778、L1184-1194)、sql-map-subcontract-config.xml、SubcontractAction.java、SubcontractService.java、SubcontractConstant.java、Subcontract.bpmn

---

## 第1章 用户故事

### US-1.1 服务经理发起转包申请
作为**项目服务经理**,我希望能够选择一个或多个项目合同号、勾选需要转包的设备序列号、选择服务商、录入转包金额与事由,提交一份转包申请,以便启动转包审批流程。
证据:SubcontractAction.create/apply(L379-425)、Subcontract.bpmn serviceTask(L5)

### US-1.2 受益部门服务经理审批
作为**受益部门服务经理**,我希望对待审批的转包申请进行审批(通过/驳回),以便确认转包对本部门利益的影响可接受。
证据:Subcontract.bpmn profitServiceTask(L65)、SubcontractAction.audit(L431-458)、SubcontractConstant.SubcontractStatus.PROFIT_SM_AGREE=15(L295)

### US-1.3 工程管理部主管审批并录入转包价
作为**工程管理部主管**,我希望对转包申请进行审批,并在审批时录入每个合同号的转包价(工程服务费),以便确定实际转包成本。
证据:Subcontract.bpmn approveTask(L7)、SubcontractAction.queryContractNoEngineeFee(L752-778)、SubcontractConstant.TaskKey.APPROVE(L193)

### US-1.4 办事处主任审批
作为**办事处主任**,我希望对超过工程服务费阈值的转包申请进行最终审批,以便控制转包成本风险。
证据:Subcontract.bpmn approveZRTask(L13)、SubcontractConstant.AREA_LEADER_AUDIT_ENGINEE_FEE_OFFICES(L381)

### US-1.5 工程管理部人员生成合同
作为**工程管理部人员**,我希望在审批通过后为转包生成正式合同号,以便进入执行阶段。
证据:Subcontract.bpmn generateContractTask(L26)、SubcontractConstant.TaskKey.GENERATE_CONTRACT(L208)

### US-1.6 服务经理提交付款信息
作为**项目服务经理**,我希望在合同执行阶段为转包录入分次付款计划(比例、金额、附件),并提交付款申请,以便工程管理部执行付款。维护类转包需先上传服务单。
证据:Subcontract.bpmn applyPaymentTask(L27)、SubcontractAction.querySubcontractPayment(L784-932)、savePayment(L937-1003)、L890-898 维护类服务单校验

### US-1.7 回访人员回访
作为**回访人员**,我希望对完成首次付款的转包发起回访、填写回访问卷,通过/不通过/无法回访,以便闭环质量评估。
证据:Subcontract.bpmn callbackTask(L30)、SubcontractAction.querySubcontractCallback(L512-559)、SubcontractCallBack.bpmn

### US-1.8 工程管理部付款与闭环
作为**工程管理部人员**,我希望根据付款计划执行付款(部分付款/全部付款),付款完成后对转包进行闭环。
证据:Subcontract.bpmn approvePaymentTask(L35)、exclusivegateway7(L36)、closeFlow(L39)、paymentFlow(L48)

### US-1.9 项目经理查询项目下的转包记录
作为**项目经理**,我希望在项目管理界面查询某个项目下所有的转包记录及状态,以便了解项目转包情况。
证据:SubcontractAction.querySubcontractInfoForProject(L1210)、SubcontractService.querySubcontractInfoForProject(L482)

### US-1.10 管理员维护服务商档案
作为**管理员**,我希望维护转包服务商档案(名称、编码、银行账号、收款人、邮箱、生效期),以便在创建转包时选择。
证据:SubcontractAction.facilitatorEdit/facilitatorList(L1235-1263)、SubcontractService.insertSubcontractFacilitator(L457)

### US-1.11 财务/付款人员查验付款发票附件
作为**付款相关人员**,我希望对付款申请对应的发票附件进行识别与查验(发票号、金额、识别状态、查验状态),以便付款合规。
证据:SubcontractAction.querySubcontractPayment(L834-875)、verifyPaymentDeliver(L1069)、SubcontractService.verifySubcontractPaymentDeliver(L612-619)

### US-1.12 转包申请被驳回后服务经理重新处理
作为**项目服务经理**,我希望在待办中看到被驳回的转包申请并重新编辑提交,以便完成转包流程。
证据:SubcontractService.selectRejectedSubcontractProjectList(L530)、SubcontractConstant.STATUS_SUBMIT/AUDIT(L32-37)

### US-1.13 系统定时催办余款
作为**系统**,我希望定时扫描"付款完成、余款未清"的转包,向付款人员发送下次付款提醒邮件,以便余款不被遗忘。
证据:SubcontractNextPaymentMailer.java、SubcontractConstant.PAYMENT_NEXT_NOTIFY_CODE(L407)、queryNextPaymentTask(L568)

---

## 第2章 功能需求(按子域)

### 2.1 转包创建与审核

#### FR-2.1.1 转包创建(草稿)
- 输入:转包名称、合同号集合(逗号分隔)、项目ID集合、转包类型(10工程实施/20驻场/30维护)、服务商ID+名称、银行信息、办事处代码、受益部门代码、转包金额、转包事由、备注、生效起止时间、组织ID、自定义扩展信息(JSON)。
- 行为:生成转包编号、初始状态=0(草稿)、保存转包设备行与交付件。
- 校验:转包名称唯一性(checkSubcontractName)。
- 证据:SubcontractAction.create(L379-401)、sql-map insertSubcontractProject(L52-68)、SubcontractAction.checkSubcontractName(L730-746)
- 状态码:草稿=0、待审批=10(SubcontractConstant.SubcontractStatus L283-328)

#### FR-2.1.2 转包申请提交(发起流程)
- 行为:保存转包内容后启动转包审批流程,流程实例 key = "Subcontract"。
- 流程首节点:服务经理发起转包申请(serviceTask)。
- 证据:SubcontractAction.apply(L407-425)、SubcontractService.startSubcontractFlow(L267)、Subcontract.bpmn(L3-5)、PROCESS_SUBCONTRACT_KEY="Subcontract"(L77)

#### FR-2.1.3 受益部门服务经理审批
- 触发节点:profitServiceTask。
- 操作:通过(result=1)→ 进入工程管理部主管审批;驳回(result=-1)→ 终止流程。
- 路由:仅当审批人 areaPower 包含受益部门代码(或其办事处映射)时,可见该任务。
- 证据:SubcontractAction.querySubcontractComment(L1141-1153)、Subcontract.bpmn profitServiceTask/exclusivegateway10(L65-73)、SubcontractStatus.PROFIT_SM_AGREE=15/PROFIT_SM_REJECT=-15(L295-299)

#### FR-2.1.4 工程管理部主管审批(录入转包价)
- 触发节点:approveTask(以及 normalApproveTask 通用审批节点)。
- 操作:通过(result=1)→ 进入办事处主任审批;驳回(result=-1)→ 终止。
- 录入:对每个合同号录入转包价(price)、工程服务费(engineeFee)、订单执行号、采购类型等。
- 证据:SubcontractAction.audit(L431-458)、queryContractNoEngineeFee(L752-778)、SubcontractService.auditNormalApproveSubcontractFlow(L605)、Subcontract.bpmn approveTask(L7)
- 状态:工程管理部通过=20、驳回=-20(SubcontractStatus L303-307)

#### FR-2.1.5 办事处主任审批
- 触发节点:approveZRTask。
- 触发条件:转包涉及工程服务费且办事处在配置项 `subcontract.areaLeader.auditEngineeFee.offices` 列表中。
- 操作:通过(result=1)→ 进入生成合同;驳回(result=-1)→ 终止;通过时记录 zrApproveTime。
- 证据:Subcontract.bpmn approveZRTask(L13)、SubcontractConstant.AREA_LEADER_AUDIT_ENGINEE_FEE_OFFICES(L381)、SubcontractStatus.AREA_AGREE=30/AREA_REJECT=-30(L311-315)

#### FR-2.1.6 生成合同号
- 触发节点:generateContractTask(工程管理部人员办理)。
- 行为:为转包生成合同号,流程进入"服务经理提交付款信息"节点。
- 证据:Subcontract.bpmn generateContractTask(L26)、flow6(L28)、SubcontractService.generateContractFlow(L500)、SubcontractConstant.TaskKey.GENERATE_CONTRACT(L208)

#### FR-2.1.7 转包闭环
- 触发节点:approvePaymentTask 后置的 exclusivegateway7,选择 closeFlow(result=2)。
- 行为:转包状态置为 100(已闭环)。
- 证据:Subcontract.bpmn closeFlow(L39-41)、SubcontractAction.close(L464-486)、SubcontractStatus.CLOSED=100(L323)

#### FR-2.1.8 转包流程终止
- 行为:支持根据转包ID终止流程(含审批意见)。
- 证据:SubcontractAction.terminateWorkFlow(L1114)、SubcontractService.terminateWorkFlow(L549-563)

#### FR-2.1.9 转包列表查询与导出
- 查询条件:转包名称、合同号、项目ID、类型、状态、回访状态、服务商、办事处、受益部门、创建人、时间区间(searchTimeType + searchStartTime/searchEndTime,结束日期自动补到当日 23:59:59)。
- 分页:基于 SubcontractPageParam + DisplayParam。
- 导出:导出列表(含已付比例、已付金额、主任审批时间、确认时间、付款时间)。
- 证据:SubcontractAction.list(L218-294)、SubcontractService.selectSubcontractProjectVOListPageable(L543)、querySubcontractExportData(L537)、SubcontractProjectVO(L29-33,112-143)

### 2.2 转包设备序列号选择

#### FR-2.2.1 选择项目
- 行为:输入合同号集合,查询合同号对应的项目清单(含项目编码、项目名)。
- 证据:SubcontractAction.chooseSubcontractProject(L565-585)、SubcontractService.queryProjectList(L85-91)

#### FR-2.2.2 查询可选发货序列号
- 行为:依据合同号+项目ID,从发货视图(view_shipment_info_4_pm)与已存在项目发货(pm_project_shipment)中查询可选设备序列号(含合同号、序列号、物料编码、物料名称、物料型号),过滤"已转出(transferFlag=1)"。
- 排除:已存在于其他转包的序列号(pm_project_shipment 标记)。
- 证据:SubcontractAction.chooseShipmentInfo(L632-666)、sql-map batchInsertSubcontractLine(L822-869)、SubcontractService.queryShipmentinfoByContractNosAndProjectIds(L60-79)

#### FR-2.2.3 保存转包设备行
- 行为:批量插入选中序列号到转包设备行表(去重,按 barCode+subcontractId 唯一),并补全物料型号(itemModel)。
- 行为:支持批量删除不在选中集合内的设备行。
- 证据:sql-map batchInsertSubcontractLine(L822-869,含 ON DUPLICATE KEY UPDATE)、batchDeleteSubcontractLine(L870-877)

#### FR-2.2.4 查询转包设备行
- 行为:按转包ID查询设备行,LEFT JOIN 发货条码关系表(fb_shipment_barcode_relation)与物料表(fb_items)以补全物料型号/名称。
- 证据:sql-map selectSubcontractLineList(L781-803)、SubcontractAction.querySubcontractLine(L667-688)

### 2.3 转包工程费(转包价)

#### FR-2.3.1 查询合同号工程服务费
- 行为:输入合同号集合,查询每个合同的工程服务费及已有转包价(若该转包已录入过)。
- 输出字段:合同号、订单执行号、项目编码、工程服务费、对象ID、采购类型、转包价。
- 证据:SubcontractAction.queryContractNoEngineeFee(L752-778)、SubcontractService.queryContractNoEngineeFeeWithSubPrice(L240)

#### FR-2.3.2 保存转包价
- 行为:审批节点(approveTask / normalApproveTask)时随审批意见一起保存每个合同的转包价。
- 证据:SubcontractAction.audit(L431-441)、SubcontractService.auditNormalApproveSubcontractFlow(L605)、sql-map insertSubcontractPrice/updateSubcontractPriceByIdSelective(L1607-1672)

### 2.4 转包付款

#### FR-2.4.1 维护付款计划
- 行为:服务经理在 applyPaymentTask 节点维护分次付款计划,每条含:付款比例(ratio)、付款金额(amount,自动 FORMAT 2位小数,自动去除千分位逗号)、备注、自定义信息。
- 行为:支持批量保存与按 ID 删除(空付款记录由 deleteEmptySubcontractPayment 清理)。
- 证据:SubcontractAction.savePayment(L937-1003)、sql-map insertSubcontractPayment/insertSubcontractPaymentSelective(L1234-1280)、deleteEmptySubcontractPayment(L2647)

#### FR-2.4.2 付款附件上传
- 行为:每个付款项可上传多个交付件/附件(发票、服务单等),附件含:文件名、文件路径、类型、生效起止、自定义信息(发票号、发票金额、识别状态、查验状态等)。
- 类型:服务单 type="1";发票类型由配置 `subcontract.inspection.delivery.types.invoice` 决定;验收材料类型由 `subcontract.inspection.delivery.types.inspection` 决定。
- 证据:SubcontractAction.savePayment(L954-958)、sql-map SubcontractDeliver resultMap(L1042-1057)、SubcontractConstant L147-166

#### FR-2.4.3 维护类转包服务单前置校验
- 行为:转包类型=30(维护类)时,提交付款申请前必须上传至少一个 type="1"(服务单)附件,否则提示"维护类项目,请先上传服务单!"。
- 证据:SubcontractAction.querySubcontractPayment(L890-898)

#### FR-2.4.4 付款附件发票识别与查验
- 行为:对类型为"发票"的附件,从 customInfo 读取 identify(是否已识别)、needVerify、verified_status 等字段;按发票号去重统计已识别发票数量与发票总金额(total_amount)。
- 行为:提供 verifyPaymentDeliver / verifySubcontractPaymentDeliver 接口校验付款附件。
- 证据:SubcontractAction.querySubcontractPayment(L834-875)、verifyPaymentDeliver(L1069)、SubcontractService.verifySubcontractPaymentDeliver(L612-619)

#### FR-2.4.5 工程管理部付款
- 触发节点:approvePaymentTask。
- 操作:闭环(result=2)→ 流程结束;付款完成余款未清(result=5)→ 回到 applyPaymentTask;驳回(result=-1)→ 终止。
- 证据:Subcontract.bpmn exclusivegateway7(L36)、closeFlow/paymentFlow/closeReject(L39-50)、SubcontractService.approvePaymentFlow(L516)、CommentStatus.PAYMENT=5(L274)

#### FR-2.4.6 付款完成回访分支
- 行为:applyPaymentTask 后置网关 exclusivegateway6,若 result=1 进入回访(callbackTask),若 result=-1 不需回访直接进入付款(approvePaymentTask)。
- 证据:Subcontract.bpmn exclusivegateway6(L29)、callBackFlow/noCallBackFlow(L32-44)

#### FR-2.4.7 已付金额与已付比例统计
- 行为:对每条付款项,根据 customInfo.paid 标志统计已付比例与已付金额合计;同时统计发票已识别金额合计。
- 证据:SubcontractAction.querySubcontractPayment(L808-875)、SubcontractService.querySubcontractPaiedAmount(L399)

#### FR-2.4.8 付款申请打印
- 行为:支持打印付款申请,渲染转包、付款计划、发票附件清单。
- 证据:SubcontractAction.querySubcontractPaymentPrint(L1009-1067)

#### FR-2.4.9 SSE 付款同步与自动更新
- 行为:定时查询 SSE 系统新增的转包付款(querySSESubcontractPaymentList),自动更新付款时间(updateSSESubcontractPaymentTime),自动完成付款(SubcontractPaymentAutoUpdate / SubcontractPaymentAutoComplete)。
- 证据:SubcontractService.querySSESubcontractPaymentList(L574)、sql-map updateSSESubcontractPaymentTime(L2668)、quartz/SubcontractPaymentAutoUpdate.java、quartz/SubcontractPaymentAutoComplete.java

#### FR-2.4.10 付款发票编号回写
- 行为:批量更新付款申请对应的发票编号(updateSubcontractPaymentInvoiceNumber)。
- 证据:SubcontractService.updateSubcontractPaymentInvoiceNumber(L625)

### 2.5 转包回访

#### FR-2.5.1 发起回访流程
- 行为:可单独发起回访流程(PROCESS_CALLBACK_KEY="SubcontractCallBack"),也可在主流程内进入回访节点。
- 证据:SubcontractAction.startCallBackFlow(L492-506)、SubcontractService.startCallBackFlow(L335-342)、PROCESS_CALLBACK_KEY(L82)

#### FR-2.5.2 回访问卷
- 行为:回访人员填写回访问卷(quesnaireId、quesnaireVersion),保存问卷草稿或提交(submitCallBackFlow2)。
- 行为:每次保存/提交重新生成一份问卷数据;提交时计算问卷得分。
- 问卷模板:来源于闭环问卷(PmClosedLoopQuesnaire)。
- 证据:SubcontractAction.querySubcontractCallback(L512-559)、SubcontractService.insertSubcontractQuesnaire(L437-438)、selectMaxSubcontractCallback(L450)

#### FR-2.5.3 回访结果路由
- 通过(result=3)→ 进入付款节点;无法回访(result=4)→ 进入付款节点;回访不通过(result=-3)→ 回到付款申请节点。
- 回访状态(callbackState):10通过/20无法回访/-10不通过。
- 证据:Subcontract.bpmn callBackPass/callBackDisabled/callbackReject(L56-61)、SubcontractConstant.SubcontractCallbackStatus(L353-367)

### 2.6 转包评论(审批意见)

#### FR-2.6.1 查询审批意见列表
- 行为:按转包ID查询审批意见列表(评论人、评论内容、审批状态、时间)。
- 证据:SubcontractAction.querySubcontractComment(L1134-1182)、SubcontractService.querySubcontractCommentList(L452)

#### FR-2.6.2 审批意见数据结构
- 字段:quesnaireId、subcontractId、approveStatus(审批状态:0申请/1通过/-1驳回/2可闭环/-2不可闭环/3回访通过/4无法回访/-3回访不通过/5付款完成余款未清)、comment(意见内容)。
- 证据:SubcontractComment.java(L1-43)、SubcontractConstant.CommentStatus(L238-275)
- 注:审批意见存储于流程引擎通用评论表(继承自 ActComment),无独立 subcontract_comment 表 [待澄清]

---

## 第3章 数据契约【最关键】

> 字段分级说明:
> - **C**(Create):创建/初始化时必填或默认填充
> - **I**(Input):用户在创建或编辑过程中可输入
> - **D**(Derived/Display):系统派生、流程写入或仅展示

### 3.1 pm_subcontract_project_header(转包主表)
证据:sql-map-subcontract-config.xml L7-36、L52-67

| 字段 | 类型 | 分级 | 说明 |
|---|---|---|---|
| id | INTEGER(PK,自增) | C | 主键 |
| subcontractName | VARCHAR | I | 转包名称(唯一) |
| subcontractNo | VARCHAR | D | 转包编号(系统生成) |
| contractNos | VARCHAR | I | 合同号集合(逗号分隔) |
| projectIds | VARCHAR | I | 项目ID集合(逗号分隔) |
| type | INTEGER | I | 类型:10工程实施/20驻场/30维护 |
| state | INTEGER | C/D | 状态:0/10/15/-15/20/-20/30/40/100/-100 |
| callbackState | INTEGER | D | 回访状态:10通过/20无法回访/-10不通过 |
| facilitatorId | INTEGER | I | 服务商ID |
| facilitatorName | VARCHAR | I/D | 服务商名称(冗余) |
| bankInfo | VARCHAR | I | 银行信息 |
| bankAccount | VARCHAR | I | 银行账号 |
| officeCode | VARCHAR | I | 办事处代码 |
| profitDepCode | VARCHAR | I | 受益部门代码 |
| isAccrued | BIT | D | 是否已计提 |
| isInvoiced | BIT | D | 是否已开票 |
| subcontractAmount | VARCHAR | I | 转包金额 |
| reason | VARCHAR | I | 转包事由 |
| remark | VARCHAR | I | 备注 |
| effectiveFrom | TIMESTAMP | I | 生效开始 |
| effectiveTo | TIMESTAMP | I | 生效结束 |
| zrApproveTime | TIMESTAMP | D | 办事处主任审批时间 |
| createBy | VARCHAR | C | 创建人 |
| createTime | TIMESTAMP | C | 创建时间 |
| updateBy | VARCHAR | D | 更新人 |
| updateTime | TIMESTAMP | D | 更新时间 |
| orgId | INTEGER | I | 组织ID |
| customInfo | JSON | I/D | 扩展信息(multiDimInfo 等),更新时 JSON_MERGE_PATCH 合并 |

### 3.2 pm_subcontract_project_line(转包设备序列号行)
证据:sql-map-subcontract-config.xml L676-693、L704-715、L822-869

| 字段 | 类型 | 分级 | 说明 |
|---|---|---|---|
| id | INTEGER(PK,自增) | C | 主键 |
| subcontractId | INTEGER | I | 外键→header.id |
| projectId | INTEGER | I/D | 项目ID(批量插入时由合同-项目关系派生) |
| barCode | VARCHAR | I | 设备序列号(与 subcontractId 联合唯一) |
| itemCode | VARCHAR | I | 物料编码 |
| itemModel | VARCHAR | I/D | 物料型号(可由 fb_items 派生) |
| itemName | VARCHAR | I | 物料名称 |
| contractNo | VARCHAR | I | 合同号 |
| createTime/createBy/updateTime/updateBy | TIMESTAMP/VARCHAR | C/D | 审计字段 |

### 3.3 pm_subcontract_facilitator(转包服务商档案)
证据:sql-map-subcontract-config.xml L879-899、L910-924

| 字段 | 类型 | 分级 | 说明 |
|---|---|---|---|
| id | INTEGER(PK,自增) | C | 主键 |
| name | VARCHAR | I | 服务商名称(模糊查询) |
| code | VARCHAR | I | 服务商编码 |
| account | VARCHAR | I | 账号 |
| bankInfo | VARCHAR | I | 银行信息(模糊查询) |
| bankAccount | VARCHAR | I | 银行账号 |
| receiver | VARCHAR | I | 收款人 |
| email | VARCHAR | I | 邮箱 |
| state | BIT | I | 状态:1启用/0停用 |
| effectiveFrom | TIMESTAMP | I | 生效开始(>=过滤) |
| effectiveTo | TIMESTAMP | I | 生效结束(<=过滤,空视为 9999-12-31) |
| 审计字段 | - | C/D | createBy/createTime/updateBy/updateTime |

### 3.4 pm_subcontract_deliver_files(转包交付件/附件)
证据:sql-map-subcontract-config.xml L1042-1057、L1069-1079

| 字段 | 类型 | 分级 | 说明 |
|---|---|---|---|
| id | INTEGER(PK,自增) | C | 主键 |
| subcontractId | INTEGER | I | 外键→header.id |
| paymentId | INTEGER | I | 关联付款ID(可空) |
| fileName | VARCHAR | I | 文件名 |
| filePath | VARCHAR | I | 文件存储路径 |
| type | VARCHAR | I | 类型:1=服务单;发票类型由配置项决定;验收材料由配置项决定 |
| uploadBy | VARCHAR | C | 上传人 |
| uploadTime | TIMESTAMP | C | 上传时间 |
| effectiveFrom | TIMESTAMP | I | 生效开始 |
| effectiveTo | TIMESTAMP | I/D | 生效结束(查询付款附件时以"当前时间"为上界过滤) |
| customInfo | JSON | I/D | 发票扩展(invoice_number、total_amount、identify、needVerify、verified_status 等) |

### 3.5 pm_subcontract_project_payment(转包付款)
证据:sql-map-subcontract-config.xml L1205-1219、L1234-1246

| 字段 | 类型 | 分级 | 说明 |
|---|---|---|---|
| id | INTEGER(PK,自增) | C | 主键 |
| subcontractId | INTEGER | I | 外键→header.id |
| ratio | VARCHAR | I | 付款比例(数值字符串) |
| amount | VARCHAR | I | 付款金额(入库前 FORMAT 2位小数并去除千分位逗号) |
| confirmTime | TIMESTAMP | D | 付款确认时间 |
| paymentTime | TIMESTAMP | D | 实际付款时间 |
| remark | VARCHAR | I | 备注 |
| sseId | INTEGER | D | SSE 系统付款记录ID |
| 审计字段 | - | C/D | createBy/createTime/updateBy/updateTime |
| customInfo | JSON | I/D | 含 paid(是否已付)等;更新时 JSON_MERGE_PATCH 合并 |

### 3.6 pm_subcontract_project_callback(转包回访)
证据:sql-map-subcontract-config.xml L1354-1369、L1384-1396

| 字段 | 类型 | 分级 | 说明 |
|---|---|---|---|
| id | INTEGER(PK,自增) | C | 主键 |
| subcontractId | INTEGER | I | 外键→header.id |
| taskKey | VARCHAR | I | 流程任务KEY(callbackTask 等) |
| taskId | VARCHAR | I | 流程任务实例ID |
| quesnaireId | INTEGER | I | 问卷模板ID |
| quesnaireVersion | INTEGER | I | 问卷版本 |
| quesnaireState | INTEGER | D | 问卷状态 |
| 审计字段 | - | C/D | createBy/createTime/updateBy/updateTime |
| effectiveFrom | TIMESTAMP | I | 生效开始 |
| effectiveTo | TIMESTAMP | I | 生效结束 |
| customInfo | JSON | I/D | 扩展信息,更新时 JSON_MERGE_PATCH 合并;支持按 JSON key 迭代查询 |

### 3.7 pm_subcontract_project_price(转包价/工程服务费)
证据:sql-map-subcontract-config.xml L1577-1591、L1607-1621

| 字段 | 类型 | 分级 | 说明 |
|---|---|---|---|
| id | INTEGER(PK,自增) | C | 主键 |
| subcontractId | INTEGER | I | 外键→header.id |
| contractNo | VARCHAR | I | 合同号 |
| orderExecNumber | VARCHAR | I | 订单执行号 |
| projectCode | VARCHAR | I | 项目编码 |
| engineeFee | VARCHAR | D | 工程服务费(源自合同) |
| objId | VARCHAR | I | 对象ID |
| procType | VARCHAR | I | 采购类型 |
| price | VARCHAR | I/D | 转包价(审批时录入) |
| 审计字段 | - | C/D | createTime/createBy/updateTime/updateBy |

### 3.8 [待澄清] 转包评论(无独立表)
- 现状:SubcontractComment 为 VO,继承自流程引擎通用评论(ActComment),未发现独立的 subcontract_comment 表。
- 推测:审批意见存储于流程引擎通用评论表(如 act_comment 或类似)。
- 证据:SubcontractComment.java L5 `extends ActComment`、SubcontractService.querySubcontractCommentList 返回 `List<Map<String,Object>>`(L452)

### 3.9 跨表关系图(文字描述)
```
pm_subcontract_project_header (1)
  ├── (1:N) pm_subcontract_project_line        按 subcontractId 关联
  ├── (1:N) pm_subcontract_project_payment      按 subcontractId 关联
  │              └── (1:N) pm_subcontract_deliver_files  按 subcontractId+paymentId 关联
  ├── (1:N) pm_subcontract_deliver_files        按 subcontractId 关联(转包级附件,无 paymentId)
  ├── (1:N) pm_subcontract_project_callback     按 subcontractId 关联
  ├── (1:N) pm_subcontract_project_price        按 subcontractId 关联
  └── (N:1) pm_subcontract_facilitator          按 facilitatorId 关联
```

---

## 第4章 非功能需求

### NFR-4.1 唯一性约束
- 转包名称在系统内唯一(FR-2.1.1 校验);转包设备行按 (subcontractId, barCode) 唯一(batchInsert 使用 ON DUPLICATE KEY UPDATE)。
- 证据:SubcontractAction.checkSubcontractName(L730)、sql-map batchInsertSubcontractLine L866-868

### NFR-4.2 数据完整性
- 付款金额强制 FORMAT 2 位小数并去除千分位逗号(`FORMAT(REPLACE(#amount#, ",", ""), 2)`);ratio 与 amount 以 VARCHAR 存储,程序中按数值解析。
- customInfo(JSON)字段更新统一使用 `JSON_MERGE_PATCH(IFNULL(customInfo, "{}"), ...)` 以避免覆盖历史键。
- 证据:sql-map L1242-1244、L1286-1297、L164、L1297、L1316、L1432、L1453、L1472

### NFR-4.3 权限与可见性
- 受益部门服务经理任务路由:审批人 areaPower 必须包含转包的 profitDepCode 或其办事处映射。
- 一级部门服务经理任务路由:审批人 areaPower 必须包含转包的 parentOfficeCode 或其映射。
- 办事处主任审批:仅当办事处在配置 `subcontract.areaLeader.auditEngineeFee.offices` 内时触发。
- 维护类转包:提交付款前必须上传服务单。
- 角色:服务经理(ROLE_SERVICEMANAGER)、工程管理部(ROLE_ENGINEEMANAGER)、工程管理部主管(ROLE_ENGINEEMANAGER_LEADER)。
- 证据:SubcontractAction.querySubcontractComment(L1141-1169)、SubcontractAction.savePayment(L940-941)、querySubcontractPayment(L890-898)、SubcontractConstant L377-381

### NFR-4.4 流程可追溯
- 每个审批节点产出评论记录(含 approveStatus、comment);状态码与流程 result 双向映射(1/-1/2/-2/3/4/-3/5)。
- 转包状态(state)与回访状态(callbackState)独立维护。
- 证据:SubcontractConstant.CommentStatus(L238-275)、SubcontractStatus(L283-328)、SubcontractCallbackStatus(L353-367)、Subcontract.bpmn result 表达式(L16/21/24/33/40/43/46/49/52/57/60/63/69/72)

### NFR-4.5 通知与提醒
- 关键节点均配置邮件通知模板:审批、审批通过/驳回、生成合同号、付款申请、付款完成、付款下一步提醒、闭环、回访不通过、服务商下单通知。
- 邮件抄送人列表由配置 `subcontract.css.mail` 控制;付款人员由 `subcontract.payment.user` 控制。
- 证据:SubcontractConstant.SubcontractTemplate(L369-424)、SubcontractNextPaymentMailer.java

### NFR-4.6 定时任务
- 余款提醒:定时扫描"付款完成、余款未清"的转包并邮件提醒(SubcontractNextPaymentMailer)。
- SSE 付款同步:定时拉取 SSE 新增付款记录并自动更新付款时间/自动完成(SubcontractPaymentAutoUpdate、SubcontractPaymentAutoComplete)。
- 发票同步:转包付款发票同步到 FP 系统(SubcontractInvoiceToFP)。
- 证据:quartz/SubcontractPaymentAutoUpdate.java、quartz/SubcontractPaymentAutoComplete.java、quartz/SubcontractNextPaymentMailer.java、extend/erms/job/SubcontractInvoiceToFP.java

### NFR-4.7 外部系统集成
- 发货数据:依赖 view_shipment_info_4_pm、view_shipment_ems_4_pm、fb_shipment_barcode_relation、fb_items、pm_project_shipment 等外部视图/表。
- SSE:付款时间回写与状态同步。
- FP(发票系统):发票同步(SubcontractInvoiceToFP)。
- 流程引擎:依赖工作流引擎(Activiti)执行审批,流程定义见 bpmn 目录。
- 证据:sql-map batchInsertSubcontractLine(L822-869)、selectSubcontractLineList(L781-803)、SubcontractService.querySSESubcontractPaymentList(L574)

### NFR-4.8 多公司/多组织支持
- 转包主表含 orgId,查询付款页时按 orgId 查询当前公司信息;公司列表来自 departmentManageService。
- 多维度信息(multiDimInfo):按受益部门(profitDepCode)查询默认多维度信息(缓存 MultiDimCache)。
- 证据:SubcontractAction.querySubcontractPayment(L795-801、L910-922)、SubcontractService.selectDefaultMultiDimByDep(L581-589)、sql-map selectDefaultMultiDimByDep(L2698)

### NFR-4.9 文件下载
- 转包附件支持流式下载(application/octet-stream,缓冲区 4096 字节,文件名 ISO8859-1 编码)。
- 证据:struts-sys.xml L466-471、L771-776;SubcontractAction.downloadFile/getDownloadFile(L1264-1282)

### NFR-4.10 [待澄清] 替代流程
- bpmn 目录存在 Subcontract.bpmn 与 Subcontract2.bpmn、SubcontractCallBack.bpmn 与 SubcontractCallBack2.bpmn 两套流程定义,具体启用条件与差异需业务确认。
- 另存在 SubcontractInspectionListener 与 PROCESS_INSPECTION_KEY="SubcontractInspection"(转包验收流程),与主流程的 ACCEPTANCE_TASK 关系待澄清。
- 证据:bpmn 目录 LS 结果、SubcontractConstant.PROCESS_INSPECTION_KEY(L87)、TaskKey.ACCEPTANCE_TASK(L228)、listener/SubcontractInspectionListener.java

### NFR-4.11 [待澄清] Subcontract2PurchaseListener
- 存在转包→采购的监听器(Subcontract2PurchaseListener),触发条件与传递数据需业务确认。
- 证据:listener/Subcontract2PurchaseListener.java
