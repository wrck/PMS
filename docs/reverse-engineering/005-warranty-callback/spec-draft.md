# 005-warranty-callback 域规格草稿(Spec Reverse-Draft)

> 来源:逆向反推自 PMS-struts warrantyCallback 包及相关回访/闭环模块,日期 2026-07-09
> 域职责:质保回访、维保回访、PM 闭环评估、闭环问卷

---

## 第1章 用户故事

### US-1 质保回访执行(项目维保回访)
作为**质保回访员/工程经理**,我希望对进入维保期的项目登记一次回访记录(含客户接听状态、续保意向、问卷作答),以便跟踪客户满意度并触发续保商机。
- **证据**:`PMS-struts/src/com/dp/plat/warrantyCallback/action/WarrantyCallbackAction.java:46,126,220,305`;`entity/ProjectWarrantyCallback.java:9`
- **角色**:质保回访员、回访人员、工程经理、工程经理负责人、区域负责人、项目经理、服务经理、管理员

### US-2 维保/项目回访统计
作为**回访负责人**,我希望按项目维度、客户维度统计回访覆盖率、续保意向分布、最近回访时间,以便制定下一轮回访计划。
- **证据**:`WarrantyCallbackAction.java:322,374`;`config-ibaits/sql-map-warrantyCallback-config.xml:561,778`

### US-3 维保回访流程驱动(独立回访工作流)
作为**项目经理**,我希望对一个项目发起回访申请,由回访人员执行回访并填写问卷,系统按"通过/不通过/无法回访"驱动流程流转,以便把回访纳入正式审批。
- **证据**:`PMS-struts/src/com/dp/plat/action/CallBackAction.java:33,84,97,111,216`;`bpmn/CallBack.bpmn:3`
- **流程节点**:项目经理发起回访申请 → 回访人员进行回访 → 排他网关(result=1通过 / -1不通过回退 / -2无法回访回退)

### US-4 PM 闭环评估(项目交付后闭环)
作为**项目经理**,我希望在项目交付后发起闭环申请,经服务经理审核、回访人员回访、工程管理人员评分后完成闭环,以便对项目全生命周期做交付后评估。
- **证据**:`PMS-struts/src/com/dp/plat/action/PmClosedLoopAction.java:35,68,439,488,533,645,686`;`bpmn/PmClosedLoop.bpmn:3`
- **流程节点**:项目经理发起闭环申请 → 服务经理审核 → 回访人员进行回访 → 工程管理人员评分 → 结束

### US-5 闭环问卷模板管理
作为**工程管理人员**,我希望维护闭环/回访使用的问卷模板(题目、选项、分值、评分规则),并对模板执行生效/失效,以便问卷可被回访流程引用并自动计分。
- **证据**:`PMS-struts/src/com/dp/plat/action/PmClosedLoopQuesnaireAction.java:19,46,81,100,138,205,212,307`;`util/PmClosedLoopMark.java:10`;`util/PmClosedLoopMarkFactory.java:6`

### US-6 问卷自动评分
作为**回访人员/工程人员**,我希望提交问卷后系统按模板配置的评分规则自动计算总分、各题型得分及通过/不通过结论,以便减少人工计算。
- **证据**:`CallBackAction.java:344,355`;`PmClosedLoopAction.java:847`;`util/QuestionnarieUtil.java:37,52`

---

## 第2章 功能需求

### 2.1 质保回访(项目维保回访)

| 编号 | 需求 | 证据 |
|------|------|------|
| FR-2.1.1 | 列表查询:支持按项目编码/名称、办事处、服务类型、续保意向、接听状态、维保结束时间区间、回访时间区间、下次回访时间区间、客户联系人/联系方式、销售人筛选,默认按 id 倒序 | `WarrantyCallbackAction.java:143,170`;`sql-map-warrantyCallback-config.xml:234,403` |
| FR-2.1.2 | 访问权限:仅项目经理/服务经理/管理员/工程经理/工程经理负责人/回访人员/质保回访员/区域负责人可访问;非特权角色自动叠加区域权限与用户权限过滤 | `WarrantyCallbackAction.java:145,196,200` |
| FR-2.1.3 | 新建/编辑回访:进入表单时自动填充项目及维保基础信息(办事处、最终客户、维保起止、维保状态、维保级别、增值服务);可填写问卷并保存草稿或提交 | `WarrantyCallbackAction.java:220,255,266,284,299` |
| FR-2.1.4 | 问卷引用:回访表单从"生效中"的 `projectWarrantyCallback` 类型问卷模板中取首份,并加载其题目/选项/评分规则 | `WarrantyCallbackAction.java:266,270,277`;`util/QuestionnarieUtil.java:148` |
| FR-2.1.5 | 续保意向取值:0=否、1=有、2=待定;另以 -1 表示"未填写"用于查询 | `entity/ProjectWarrantyCallback.java:65`;`sql-map-warrantyCallback-config.xml:682` |
| FR-2.1.6 | 接听状态:以 `projectWarrantyCallback_phoneAnswerState` 基础数据为准,存于 `customInfo` JSON 字段 | `WarrantyCallbackAction.java:214,368`;`sql-map-warrantyCallback-config.xml:36,267` |
| FR-2.1.7 | 软删除:仅创建人或工程经理/负责人可删除,删除置 `isDelete=true` | `WarrantyCallbackAction.java:305,310,314` |
| FR-2.1.8 | 维保统计视图:按项目维度聚合最近一次回访、回访次数、续保意向次数、维保内外状态、维保级别、续保/可续采软件;使用临时表加速分页与计数 | `WarrantyCallbackAction.java:322,347`;`sql-map-warrantyCallback-config.xml:561,735,755,764` |
| FR-2.1.9 | 客户维度统计:按最终客户名称聚合项目数、回访项目数、回访次数、续保意向数、续保数、可续采软件数、最近回访/下次回访时间 | `WarrantyCallbackAction.java:374,393`;`sql-map-warrantyCallback-config.xml:778` |
| FR-2.1.10 | customInfo 合并写入:更新时以 `JSON_MERGE_PATCH` 合并既有 JSON,而非整体覆盖 | `sql-map-warrantyCallback-config.xml:186,225` |
| FR-2.1.11 | 可编辑判定:`canEdit()` 当前实现恒返回 false(原意为"续保状态=3 或接听状态以 - 开头时可编辑",已被注释禁用) | `vo/ProjectWarrantyCallbackVO.java:367,377` |

### 2.2 维保回访(独立回访工作流)

| 编号 | 需求 | 证据 |
|------|------|------|
| FR-2.2.1 | 发起申请:项目经理对项目发起回访申请,写入申请备注,启动回访工作流并回写流程实例ID与申请状态 | `CallBackAction.java:84,97`;`config-ibaits/sql-map-callback-config.xml:8,18` |
| FR-2.2.2 | 申请状态:-1=草稿、1=审批中、2=审批通过 | `data/bean/CallBack.java:15`;`sql-map-callback-config.xml:121` |
| FR-2.2.3 | 查看回访:回访人员/查看者可读取项目信息、最终客户成员、回访流程信息及历史审批意见 | `CallBackAction.java:111,122` |
| FR-2.2.4 | 审批意见:审批意见取自活动历史评论表,`procdefKey=CallBack`,结果名称映射自基础数据 `dataTypeCode=26` | `sql-map-callback-config.xml:98,101` |
| FR-2.2.5 | 问卷草稿/提交:回访人员可保存问卷草稿或提交;每次保存/提交都重新生成一份问卷结果数据;提交时计算问卷分数 | `CallBackAction.java:216,218,222,224` |
| FR-2.2.6 | 问卷与任务绑定:同一回访单在不同任务节点可挂不同问卷,以 `callBackId + taskId` 唯一确定一份 `CallBackQuesnaire` | `sql-map-callback-config.xml:44,66,68` |
| FR-2.2.7 | 驳回重提:驳回后项目经理可重新提交申请 | `CallBackAction.java:191,194` |
| FR-2.2.8 | 流程流转条件:result=1 通过(结束)、result=-1 不通过(回退项目经理)、result=-2 无法回访(回退项目经理) | `bpmn/CallBack.bpmn:15,21,25` |
| FR-2.2.9 | 问卷模板生效约束:仅取状态为"生效"的问卷模板供选择 | `CallBackAction.java:436,438` |

### 2.3 PM 闭环(项目交付后闭环评估)

| 编号 | 需求 | 证据 |
|------|------|------|
| FR-2.3.1 | 项目经理发起闭环申请:校验项目经理角色(本人或具备项目经理角色),并确认服务经理账号有效后启动闭环流程 | `PmClosedLoopAction.java:439,449,455,475` |
| FR-2.3.2 | 服务经理审核:校验服务经理角色;若驳回则需确认项目经理账号有效(含 B 角) | `PmClosedLoopAction.java:488,499,504,506` |
| FR-2.3.3 | 回访人员提交回访:`pmClosedLoopResultType=1` 提交回访问卷并计分,`pmClosedLoopResultType=2` 基于已有问卷结果完成回访节点;草稿态仅保存不流转 | `PmClosedLoopAction.java:533,545,553,596,604` |
| FR-2.3.4 | 无法回访:回访人员可标记"无法回访",需填写评价意见,结果置为 -3,流程回退服务经理 | `PmClosedLoopAction.java:645,676,681`;`bpmn/PmClosedLoop.bpmn:57` |
| FR-2.3.5 | 工程人员闭环:`pmClosedLoopResultType=1` 提交闭环建议问卷并计分,转入看分页;`pmClosedLoopResultType=2` 完成闭环节点 | `PmClosedLoopAction.java:686,698,729,744` |
| FR-2.3.6 | 评测类型(evaluationType):1=项目经理闭环申请、2=服务经理回访申请、3=回访人员回访、4=工程人员闭环、5=闭环结束 | `util/PmClosedLoopConstant.java:69,74,79,84,89` |
| FR-2.3.7 | 评测结果(evaluationResult):1=同意、-1=不同意、-3=无法回访 | `util/PmClosedLoopConstant.java:95,100,105` |
| FR-2.3.8 | 问卷状态(status):1=生效(SUBMIT)、-2=失效(ENDEFFEC)、-1=草稿(DRAFT)、2=提交问卷(SUBMITQUES) | `util/PmClosedLoopConstant.java:48,53,58,63` |
| FR-2.3.9 | 闭环流程变量:projectManager / serviceManager / callBackPerson / projectManageEmp 为各节点办理人;evaluationResult 为网关判断;projectProcessStatus 为项目闭环状态 | `util/PmClosedLoopConstant.java:18,23,28,33,38,43`;`bpmn/PmClosedLoop.bpmn:5,6,7,29` |
| FR-2.3.10 | 闭环流程网关分支:evaluationResult=1 服务经理审核通过→回访;=-1 驳回→结束;=2 已通过回访→工程评分;=3 服务经理与项目经理一致且通过回访→工程评分;=-2 驳回任务办理→结束;回访后 =1 达标→工程评分,=-1 不达标→结束,=-3 无法回访→服务经理 | `bpmn/PmClosedLoop.bpmn:15,18,23,26,33,36,39,47,50,54,57` |
| FR-2.3.11 | 申请头关联:回访/闭环评测头通过 `applyHeaderId` 关联到项目经理发起的闭环申请头(PM 类型) | `PmClosedLoopAction.java:588,675,727,838`;`sql-map-project-config.xml:1741` |
| FR-2.3.12 | 历史回访/闭环展示:按时间倒序取已提交的评测头,分别展示最新一条闭环建议、最新一条回访,以及项目经理申请头;问卷结果头/行/模板一并装配 | `PmClosedLoopAction.java:159,167,177,191,202,209` |
| FR-2.3.13 | 项目经理查看回访问卷:项目经理可查看指定评测头对应的回访问卷结果与模板 | `PmClosedLoopAction.java:785,791,806,818` |
| FR-2.3.14 | 删除草稿评测:回访人员"无法回访"前若存在草稿态回访评测,递归删除其评测头/问卷结果头/问卷结果行 | `service/PmClosedLoopService.java:146`;`sql-map-project-config.xml:1676,1683,1695` |
| FR-2.3.15 | 下一审批人维护:支持按项目集合批量更新评测头的下一审批人与下一审批人名称(用于人员变更后转移) | `sql-map-callback-config.xml:129,131`;`service/PmClosedLoopService.java:152` |
| FR-2.3.16 | 邮件通知:8 类邮件模板覆盖服务经理驳回/回访通过/回访不通过/同意闭环/不同意闭环/无法回访/服务经理同意闭环/项目经理发起闭环申请 | `util/PmClosedLoopConstant.java:170,175,180,185,190,195,200,205` |
| FR-2.3.17 | 问卷计分:提交时按选项分值累加总分,拼接答案串 `题目类型:序号-题号|选项,;`,再按模板 `markIndexs` 配置的评分规则链判定通过/驳回,并回写 `quesMarkResult` 与 `evaluationResult` | `CallBackAction.java:355,388,417`;`PmClosedLoopAction.java:847,879,909`;`util/QuestionnarieUtil.java:52,91` |
| FR-2.3.18 | 各题型得分汇总:按 `quesTypeForCB`(10工程项目类/20设备类/30工程师类/40其他)分组汇总行得分,展示题型得分列表 | `util/PmClosedLoopConstant.java:145,150,155,160`;`CallBackAction.java:317,330`;`PmClosedLoopAction.java:383` |
| FR-2.3.19 | 流程图查看:`pmClosedLoopResultType=0` 时查询流程当前任务与项目闭环状态,并取最新流程定义用于展示流程图 | `PmClosedLoopAction.java:140,142,147,154` |

### 2.4 闭环问卷(模板管理)

| 编号 | 需求 | 证据 |
|------|------|------|
| FR-2.4.1 | 问卷模板列表:支持按模板名称/类型/状态筛选,每页 50 条 | `PmClosedLoopQuesnaireAction.java:37,40,42` |
| FR-2.4.2 | 新增问卷头:必填模板名称与问卷类型,可选总分/达标分/评分规则;保存后状态置草稿,跳转编辑页继续维护题目 | `PmClosedLoopQuesnaireAction.java:81,87,90,96`;`struts-sys.xml:187` |
| FR-2.4.3 | 题目类型(questionType):1=单选、2=多选、3=问答、4=评分;问答型无需选项 | `util/PmClosedLoopConstant.java:110,115,120,125`;`PmClosedLoopQuesnaireAction.java:144` |
| FR-2.4.4 | 题目回访类型(questionTypeForCB):10=工程项目类、20=设备类、30=工程师类、40=其他(基础数据 dataTypeCode=14) | `util/PmClosedLoopConstant.java:145,150,155,160,135` |
| FR-2.4.5 | 问卷类型(quesType):30=闭环建议类(基础数据 dataTypeCode=13);另存在 `projectWarrantyCallback` 类型供维保回访引用 | `util/PmClosedLoopConstant.java:130,140`;`WarrantyCallbackAction.java:267` |
| FR-2.4.6 | 选项与分值:每个选项含编号、内容、分值;选项分值不得超过所属题目分值 | `PmClosedLoopQuesnaireAction.java:148,150` |
| FR-2.4.7 | 题目分值总和校验:已添加题目分值之和不得超过问卷总分,新增/编辑时校验 | `PmClosedLoopQuesnaireAction.java:120,156,294,320,340,355` |
| FR-2.4.8 | 题目编号自增:同模板下按已有最大题号 +1 | `PmClosedLoopQuesnaireAction.java:124,128,131` |
| FR-2.4.9 | 问卷生效:置状态为生效、记录生效开始时间;生效后方可被回访/闭环流程引用 | `PmClosedLoopQuesnaireAction.java:212,217,219` |
| FR-2.4.10 | 问卷失效:置状态为失效、记录失效结束时间 | `PmClosedLoopQuesnaireAction.java:307,311,313` |
| FR-2.4.11 | 评分规则:规则以接口形式扩展,当前内置 A/B/C/D 四种,模板通过 `markIndexs`(逗号分隔索引)选用;规则返回 "pass"/索引/-1/-2 | `util/PmClosedLoopMark.java:10,21`;`util/PmClosedLoopMarkFactory.java:6,10,19,35` |
| FR-2.4.12 | 评分规则说明展示:模板编辑/查看页展示规则说明列表 | `PmClosedLoopQuesnaireAction.java:48,59,235`;`PmClosedLoopMarkFactory.java:35,51` |
| FR-2.4.13 | 模板头更新:更新名称/总分/达标分/类型/评分规则,达标分不得超过总分,状态置草稿 | `PmClosedLoopQuesnaireAction.java:185,191,198,200` |
| FR-2.4.14 | 删除题目:按题目 ID 删除,删除后返回模板编辑页 | `PmClosedLoopQuesnaireAction.java:256,265,273` |
| FR-2.4.15 | 模板编号生成:规则为 `CH` + 年月日 + 三位流水号 | `util/PmClosedLoopConstant.java:7`;`util/PmClosedLoopUtil.java:13,17` |

---

## 第3章 数据契约【最关键】

> 字段分级:C=写入(创建/更新)、I=查询输入、D=展示输出。兼具者并列标注。
> 表名取自 SQL-map 实际 DML;模板三表(pmclosed_loop_quesnaire*)表名为推断,见 [待澄清]。

### 3.1 pm_project_warranty_callback(项目维保回访记录)

证据:`config-ibaits/sql-map-warrantyCallback-config.xml:4,38,56,152,192`

| 字段 | 类型 | 分级 | 说明 |
|------|------|------|------|
| id | INTEGER | C/D | 主键,自增 |
| projectId | INTEGER | C/I/D | 项目ID |
| projectCode | VARCHAR | C/I/D | 项目编码(模糊查询) |
| officeCode | VARCHAR | C/I/D | 办事处编码 |
| contractNos | VARCHAR | C/I/D | 合同号(模糊查询) |
| projectIds | VARCHAR | C/I | 关联的项目 |
| projectName | VARCHAR | C/I/D | 项目名称(模糊查询) |
| serviceImpl | VARCHAR | C/I/D | 实施方式/服务类型(基础数据 dataTypeCode=15) |
| industryName | VARCHAR | C/I/D | 行业(模糊查询) |
| agentChannel | VARCHAR | C/I/D | 下单代理商(模糊查询) |
| finalCustomerName | VARCHAR | C/I/D | 最终客户单位(模糊查询) |
| customer1 | VARCHAR | C/I/D | 客户联系人1(模糊查询) |
| customerContact1 | VARCHAR | C/I/D | 客户联系方式1(模糊查询) |
| customer2 | VARCHAR | C/I/D | 客户联系人2(模糊查询) |
| customerContact2 | VARCHAR | C/I/D | 客户联系方式2(模糊查询) |
| warrantyStartTime | DATE | C/I/D | 维保开始日期 |
| warrantyEndTime | DATE | C/I/D | 维保结束日期(支持区间查询) |
| renewalIntention | INTEGER | C/I/D | 续保意向:0否/1有/2待定;-1=未填写(查询用) |
| callbackTime | TIMESTAMP | C/I/D | 回访时间(支持区间查询) |
| nextCallbackTime | TIMESTAMP | C/I/D | 下次回访时间(支持区间查询) |
| taskId | VARCHAR | C/I/D | 任务ID |
| quesnaireId | INTEGER | C/I/D | 问卷结果头ID(关联 pm_cl_quesnaire_result_header.id) |
| quesnaireVersion | INTEGER | C/D | 问卷版本 |
| quesnaireState | INTEGER | C/D | 问卷状态:-1草稿/1已提交 |
| isDelete | BIT | C/I/D | 删除标记(软删除) |
| remark | VARCHAR | C/D | 备注 |
| compId | INTEGER | C/I/D | 所属公司ID |
| createBy | VARCHAR | C/D | 创建人 |
| createTime | TIMESTAMP | C/D | 创建时间 |
| updateBy | VARCHAR | C/D | 更新人 |
| updateTime | TIMESTAMP | C/D | 更新时间 |
| customInfo | JSON | C/I/D | 扩展信息;含 phoneAnswerState/phoneAnswerStateName;更新用 JSON_MERGE_PATCH 合并 |

### 3.2 pm_cl_callback(回访申请单 - 独立回访工作流)

证据:`config-ibaits/sql-map-callback-config.xml:8,18,24,107`

| 字段 | 类型 | 分级 | 说明 |
|------|------|------|------|
| id | INTEGER | C/D | 主键(代码中以 callBackId 引用) |
| projectId | INTEGER | C/I/D | 项目ID |
| instId | VARCHAR | C/D | 工作流流程实例ID |
| remark | VARCHAR | C/I/D | 申请备注 |
| applyState | INTEGER | C/I/D | 申请状态:-1草稿/1审批中/2审批通过 |
| applyBy | VARCHAR | C/D | 申请人 |
| applyTime | TIMESTAMP | C/D | 申请时间 |
| createTime | TIMESTAMP | C/D | 创建时间 |
| createBy | VARCHAR | C/D | 创建人 |
| effectiveFrom | DATE | C/D | 生效日期 |
| updateTime | TIMESTAMP | C/D | 更新时间 |
| updateBy | VARCHAR | C/D | 更新人 |

### 3.3 pm_cl_callback_quesnaire(回访任务问卷关联)

证据:`config-ibaits/sql-map-callback-config.xml:44,56,71,117`

| 字段 | 类型 | 分级 | 说明 |
|------|------|------|------|
| id | INTEGER | C/D | 主键 |
| callBackId | INTEGER | C/I/D | 回访申请单ID |
| taskId | VARCHAR | C/I/D | 工作流任务ID |
| quesnaireId | INTEGER | C/I/D | 问卷结果头ID |
| quesnaireVersion | INTEGER | C/D | 问卷版本 |
| quesnaireState | INTEGER | C/D | 问卷状态:1已提交 |
| createBy | VARCHAR | C/D | 创建人 |
| createTime | TIMESTAMP | C/D | 创建时间 |
| effectiveFrom | DATE | C/D | 生效日期 |

### 3.4 pm_cl_evaluation_header(闭环评测头 - PM 闭环流程)

证据:`config-ibaits/sql-map-project-config.xml:1600,1627,1710,1735`

| 字段 | 类型 | 分级 | 说明 |
|------|------|------|------|
| id | INTEGER | C/D | 主键,自增 |
| projectCode | VARCHAR | C/I/D | 项目编码 |
| projectId | INTEGER | C/I/D | 项目ID |
| projectName | VARCHAR | C/D | 项目名称 |
| status | INTEGER | C/I/D | 状态:1生效/-1草稿/-2失效/2提交问卷 |
| evaluationType | INTEGER | C/I/D | 评测类型:1PM申请/2SM回访申请/3回访/4工程闭环/5结束 |
| evaluationResult | INTEGER | C/I/D | 评测结果:1同意/-1不同意/-3无法回访 |
| evaluationScore | DOUBLE | C/D | 评测得分 |
| evaluationComment | VARCHAR | C/D | 评价意见 |
| evaluationTime | TIMESTAMP | C/D | 评测时间(空则取 now) |
| evaluationPeopleId | VARCHAR | C/I/D | 评测人ID |
| evaluationPeopleName | VARCHAR | C/D | 评测人姓名 |
| nextAcceptPerson | VARCHAR | C/D | 下一审批人 |
| nextAcceptPersonName | VARCHAR | C/D | 下一审批人名称 |
| applyHeaderId | INTEGER | C/I/D | 关联的PM申请头ID(自引用) |
| createdPerson | VARCHAR | C/D | 创建人 |
| createdTime | TIMESTAMP | C/D | 创建时间 |
| updatedPerson | VARCHAR | C/D | 更新人 |
| updatedTime | TIMESTAMP | C/D | 更新时间 |

### 3.5 pm_cl_quesnaire_result_header(问卷结果头)

证据:`config-ibaits/sql-map-project-config.xml:1648,1940`;`data/bean/PmClQuesnaireResultHeader.java:6`

| 字段 | 类型 | 分级 | 说明 |
|------|------|------|------|
| id | INTEGER | C/D | 主键,自增 |
| evaluationHeaderId | INTEGER | C/I/D | 评测头ID |
| quesnaireTemplateHeaderId | INTEGER | C/I/D | 问卷模板头ID |
| quesTotalScore | DOUBLE | C/D | 问卷总分(取自模板) |
| quesMarkScore | DOUBLE | C/D | 问卷得分 |
| quesPassScore | DOUBLE | C/D | 达标分(取自模板) |
| quesAnw | VARCHAR | C/D | 答案串,格式 `题目类型:序号-题号|选项,;` |
| quesMarkResult | INTEGER | C/D | 评分结果:1通过/-1驳回 |
| status | INTEGER | C/I/D | 状态:1已提交/-1草稿 |
| createdPerson | VARCHAR | C/D | 创建人 |
| createdTime | TIMESTAMP | C/D | 创建时间 |
| updatedPerson | VARCHAR | C/D | 更新人 |
| updatedTime | TIMESTAMP | C/D | 更新时间 |

### 3.6 pm_cl_quesnaire_result_line(问卷结果行)

证据:`config-ibaits/sql-map-project-config.xml:1661,1695,1972`;`data/bean/PmClQuesnaireResultLine.java:5`

| 字段 | 类型 | 分级 | 说明 |
|------|------|------|------|
| id | INTEGER | C/D | 主键 |
| quesnaireResultHeaderId | INTEGER | C/I/D | 问卷结果头ID |
| quesnaireTemplateHeaderId | INTEGER | C/I/D | 问卷模板头ID |
| quesnaireTemplateLineId | INTEGER | C/I/D | 问卷模板行ID(题目ID) |
| quesTemplateLineNum | INTEGER | D | 模板行题号(展示用) |
| quesTypeForCB | VARCHAR | C/I/D | 题目回访类型:10/20/30/40 |
| questionAnswer | VARCHAR | C/D | 作答内容 |
| questionTemplateOptId | INTEGER | C/D | 选中的模板选项ID |
| questionScore | DOUBLE | C/D | 该题得分(取自选项分值) |
| quesEvaResult | INTEGER | C/D | 行级评测结果:-1标记 |
| createdTime | TIMESTAMP | C/D | 创建时间 |

### 3.7 pm_closed_loop_quesnaire(问卷模板头)[表名为推断,待澄清]

证据:`data/bean/PmClosedLoopQuesnaire.java:8`;`PmClosedLoopQuesnaireAction.java:91,200,209,219,313`

| 字段 | 类型 | 分级 | 说明 |
|------|------|------|------|
| id | INTEGER | C/D | 主键 |
| questionnaireTemplateNum | VARCHAR | C/D | 模板编号(CH+年月日+三位流水) |
| questionnaireTemplateName | VARCHAR | C/I/D | 模板名称 |
| questionnaireScore | DOUBLE | C/I/D | 问卷总分 |
| questionnairePassScore | DOUBLE | C/I/D | 达标分(≤总分) |
| questionnaireStatus | INTEGER | C/I/D | 状态:1生效/-1草稿/-2失效 |
| quesType | VARCHAR | C/I/D | 问卷类型:30闭环建议类/projectWarrantyCallback维保回访 |
| markIndexs | VARCHAR | C/I/D | 评分规则索引串(逗号分隔,引用 A/B/C/D) |
| effectiveStartTime | DATE | C/D | 生效开始时间 |
| effectiveEndTime | DATE | C/D | 生效结束时间 |
| createdPerson | VARCHAR | C/D | 创建人 |
| createdTime | TIMESTAMP | C/D | 创建时间 |
| updatedPerson | VARCHAR | C/D | 更新人 |
| updatedTime | TIMESTAMP | C/D | 更新时间 |

### 3.8 pm_closed_loop_quesnaire_line(问卷模板行/题目)[表名为推断,待澄清]

证据:`data/bean/PmClosedLoopQuesnaireLine.java:5`;`PmClosedLoopQuesnaireAction.java:140,168`

| 字段 | 类型 | 分级 | 说明 |
|------|------|------|------|
| id | INTEGER | C/D | 主键 |
| quesnaireTemplateHeaderId | INTEGER | C/I/D | 模板头ID |
| questionNum | INTEGER | C/D | 题号(同模板内自增) |
| questionType | INTEGER | C/I/D | 题型:1单选/2多选/3问答/4评分 |
| questionTypeForCB | VARCHAR | C/I/D | 题目回访类型:10/20/30/40 |
| questionContent | VARCHAR | C/D | 题干 |
| questionRemark | VARCHAR | C/D | 题目备注 |
| questionScore | DOUBLE | C/I/D | 题目分值 |
| questionStatus | INTEGER | C/D | 题目状态 |
| effectiveStartTime | DATE | C/D | 生效开始时间 |
| effectiveEndTime | DATE | C/D | 生效结束时间 |
| createdPerson | VARCHAR | C/D | 创建人 |
| createdTime | TIMESTAMP | C/D | 创建时间 |
| updatedPerson | VARCHAR | C/D | 更新人 |
| updatedTime | TIMESTAMP | C/D | 更新时间 |

### 3.9 pm_closed_loop_quesnaire_opt(问卷模板选项)[表名为推断,待澄清]

证据:`data/bean/PmClosedLoopQuesnaireOpt.java:5`;`PmClosedLoopQuesnaireAction.java:148,168`

| 字段 | 类型 | 分级 | 说明 |
|------|------|------|------|
| id | INTEGER | C/D | 主键 |
| quesnaireTemplateHeaderId | INTEGER | C/I/D | 模板头ID |
| questionId | INTEGER | C/I/D | 题目行ID |
| questionOptionNum | INTEGER | C/D | 选项序号(1→A,2→B…) |
| questionOptionsContent | VARCHAR | C/D | 选项内容 |
| questionOptionScore | DOUBLE | C/I/D | 选项分值(≤题目分值) |
| effectiveStartTime | DATE | C/D | 生效开始时间 |
| effectiveEndTime | DATE | C/D | 生效结束时间 |
| createdPerson | VARCHAR | C/D | 创建人 |
| createdTime | TIMESTAMP | C/D | 创建时间 |
| updatedPerson | VARCHAR | C/D | 更新人 |
| updatedTime | TIMESTAMP | C/D | 更新时间 |

### 3.10 关联引用表(外部域,仅列引用点)

| 表 | 引用点 | 证据 |
|----|--------|------|
| fnd_basic_data | 服务类型(dataTypeCode=15)、回访结果(dataTypeCode=26)、问卷类型(13)、题目类型(14)、流程节点(17)、接听状态(projectWarrantyCallback_phoneAnswerState) | `sql-map-callback-config.xml:101`;`PmClosedLoopConstant.java:130,135,165` |
| fnd_user_info | 评测人/申请人姓名、审批人姓名 | `sql-map-callback-config.xml:102`;`sql-map-project-config.xml` |
| fnd_department | 办事处名称 | `sql-map-warrantyCallback-config.xml:530`;`sql-map-project-config.xml:1743` |
| fnd_company | 公司名称/简称 | `sql-map-warrantyCallback-config.xml:532` |
| fnd_act_hi_comment | 回访审批意见(procdefKey=CallBack) | `sql-map-callback-config.xml:99,104` |
| pm_project_header | 项目主表(column001办事处、column012服务类型、column013最终客户等) | `sql-map-warrantyCallback-config.xml:512,563` |
| pm_project_member | 项目成员(销售人 memberRole=10、客户联系人 memberRole=60) | `sql-map-warrantyCallback-config.xml:521,640,645` |
| view_warranty_contract_state | 维保合同状态视图(维保内外/级别/续保/可续采软件) | `sql-map-warrantyCallback-config.xml:518,628` |
| pm_project_contract / pm_project_group_relationship | 合同与项目组关系(用于聚合维保) | `sql-map-warrantyCallback-config.xml:514,516,622,626` |

---

## 第4章 非功能需求

| 编号 | 需求 | 证据 |
|------|------|------|
| NFR-1 权限模型 | 角色矩阵:项目经理、服务经理、管理员、工程经理、工程经理负责人、回访人员、质保回访员、区域负责人、项目管理员、项目查看者、财务人员;闭环各节点按角色 + 项目本人 + 区域权限组合校验(OR/AND 两种模式) | `WarrantyCallbackAction.java:145`;`PmClosedLoopAction.java:409,419,449,499,541,657,694`;`util/MessageUtil(ROLE_*)` |
| NFR-2 数据权限过滤 | 非特权角色查询自动叠加区域权限(areapower)与用户权限(userPower);特权角色(管理员/工程经理/回访人员/质保回访员)不叠加 | `WarrantyCallbackAction.java:196,200` |
| NFR-3 软删除 | 维保回访记录采用 isDelete 软删除,查询默认过滤 isDelete=false | `WarrantyCallbackAction.java:203,314`;`sql-map-warrantyCallback-config.xml:259` |
| NFR-4 多公司隔离 | 通过 compId 区分所属公司,查询与展示均带公司维度 | `sql-map-warrantyCallback-config.xml:532,666` |
| NFR-5 审计字段 | 全表具备 createBy/createTime/updateBy/updateTime;评测头额外记录评测人与评测时间 | 各表 DML |
| NFR-6 工作流驱动 | 回访与闭环均基于 BPMN 流程驱动,流程变量决定办理人与分支;流程实例 ID 回写业务表 | `bpmn/CallBack.bpmn`;`bpmn/PmClosedLoop.bpmn`;`sql-map-callback-config.xml:18` |
| NFR-7 邮件通知 | 闭环流程 8 类节点事件触发邮件(模板 ID 20-28),邮件含主题/内容/收件人/抄送/预期发送时间/发送标志 | `util/PmClosedLoopConstant.java:170-205`;`util/PmClosedLoopUtil.java:29` |
| NFR-8 流水号生成 | 问卷模板编号规则:`CH` + yyyyMMdd + 三位流水(基于当日最大号 +1) | `util/PmClosedLoopUtil.java:13,17,21` |
| NFR-9 大数据量统计 | 维保统计使用临时表 `temp_project_warranty_$tempSuffix$` 加速分页与计数,统计后删除 | `sql-map-warrantyCallback-config.xml:735,750,760,765` |
| NFR-10 JSON 扩展字段 | customInfo 以 MySQL JSON 类型存储扩展属性(如接听状态),更新采用 JSON_MERGE_PATCH 增量合并,查询用 `->>` 提取 | `sql-map-warrantyCallback-config.xml:36,186,225,267,608` |
| NFR-11 问卷计分一致性 | 提交问卷时计分逻辑在回访(独立流程)与闭环流程中各有一份实现,需保持结果一致;另存在 `QuestionnarieUtil` 统一封装供维保回访复用 | `CallBackAction.java:344`;`PmClosedLoopAction.java:847`;`util/QuestionnarieUtil.java:52` |
| NFR-12 人员有效性校验 | 闭环驳回/无法回访前需校验被回退节点办理人(服务经理/项目经理,含 B 角)账号有效,避免流程挂起 | `PmClosedLoopAction.java:455,506,621,662,762` |
| NFR-13 问卷模板生命周期 | 模板仅"生效"态可被业务流程引用;草稿态可编辑题目/选项,生效/失效带时间戳 | `PmClosedLoopQuesnaireAction.java:212,307`;`CallBackAction.java:438`;`util/QuestionnarieUtil.java:143` |
| NFR-14 题目分值约束 | 问卷各题目分值之和不得超过问卷总分;选项分值不得超过所属题目分值,提交时强校验 | `PmClosedLoopQuesnaireAction.java:120,150,156,320,340` |

---

## 附录:待澄清事项

1. **问卷模板三表表名**:`pm_closed_loop_quesnaire` / `pm_closed_loop_quesnaire_line` / `pm_closed_loop_quesnaire_opt` 的精确表名未在已读 SQL-map 中直接出现(Bean 与 Service 引用存在),标注为推断。[待澄清]
2. **两套回访体系关系**:`pm_cl_callback`(独立回访工作流 CallBack.bpmn)与 `pm_cl_evaluation_header`(PmClosedLoop.bpmn 内嵌回访节点 evaluationType=3)两套回访数据是否并存、是否迁移、字段映射关系不明。[待澄清]
3. **续保意向取值不一致**:`ProjectWarrantyCallback.renewalIntention` 注释为 0否/1有/2待定,但 `ProjectWarrantyCallbackVO.canEdit` 注释提及"续保状态为3"可编辑(当前实现已禁用恒返回 false)。值 3 的语义不明。[待澄清]
4. **闭环网关 evaluationResult=2/3 语义**:`PmClosedLoop.bpmn` 中 flow20(evaluationResult==2 "已通过回访")、flow26(==2 "服务经理和项目经理一致")、flow27(==3 "服务经理与项目经理一致且通过回访")存在同一值 2 在不同网关的不同含义,且与常量定义(1/-1/-3)不完全对应,完整流转语义需结合服务实现确认。[待澄清]
5. **pmClosedLoopResultType 完整语义**:Action 注释列出 30/40/41/42/50/51,但代码中实际使用 0/1/2/30/40,41/42/50/51 的触发路径与界面映射不明。[待澄清]
