# Feature Specification: 005-warranty-callback(质保回访与PM闭环)

**Feature Branch**: `005-warranty-callback`

**Created**: 2026-07-09

**Status**: Draft

**Source**: 逆向反推自 PMS-struts warrantyCallback 包

---

## User Scenarios & Testing

### User Story 1 - 质保回访执行(项目维保回访)(Priority: P1)

作为**质保回访员/工程经理**,我希望对进入维保期的项目登记一次回访记录(含客户接听状态、续保意向、问卷作答),以便跟踪客户满意度并触发续保商机。

**涉及角色**:质保回访员、回访人员、工程经理、工程经理负责人、区域负责人、项目经理、服务经理、管理员

**Why this priority**:质保回访是本域最核心的业务入口,直接服务客户满意度跟踪与续保商机挖掘,缺失此能力将导致维保期客户经营断档。

**Independent Test**:可通过对单一项目"新建回访 → 填写问卷 → 提交计分"完整闭环独立验证,无需依赖其他流程。

**Acceptance Scenarios**:

1. **Given** 项目已进入维保期且尚无回访记录,**When** 质保回访员点击"新建回访",**Then** 系统自动填充项目及维保基础信息(办事处、最终客户、维保起止、维保状态、维保级别、增值服务)
2. **Given** 系统存在"生效中"的 `projectWarrantyCallback` 类型问卷模板,**When** 用户进入回访表单,**Then** 取首份生效模板并加载其题目/选项/评分规则
3. **Given** 用户填写客户接听状态(`projectWarrantyCallback_phoneAnswerState` 基础数据)与续保意向(0否/1有/2待定),**When** 提交问卷,**Then** 系统以增量合并方式写入 `customInfo` JSON 字段、保存问卷结果并自动计分
4. **Given** 一条已存在的回访记录,**When** 非创建人且无工程经理/负责人权限的用户尝试删除,**Then** 系统拒绝删除(仅创建人或工程经理/负责人可软删除置 `isDelete=true`)
5. **Given** 用户对项目无区域权限,**When** 访问维保回访列表,**Then** 系统自动叠加区域权限与用户权限过滤,只返回可见记录(特权角色不叠加)

---

### User Story 2 - 维保回访流程驱动(独立回访工作流)(Priority: P1)

作为**项目经理**,我希望对一个项目发起回访申请,由回访人员执行回访并填写问卷,系统按"通过/不通过/无法回访"驱动流程流转,以便把回访纳入正式审批。

**流程节点**:项目经理发起回访申请 → 回访人员进行回访 → 排他网关(result=1 通过 / -1 不通过回退 / -2 无法回访回退)

**Why this priority**:独立回访工作流是把回访行为正式化的关键路径,与质保回访共同构成回访域能力。

**Independent Test**:可由项目经理对一个项目"发起申请 → 回访人员办理 → 提交结果"全流程独立验证,不依赖 PM 闭环流程。

**Acceptance Scenarios**:

1. **Given** 项目经理对项目发起回访申请并填写申请备注,**When** 提交启动回访工作流,**Then** 写入申请单、回写流程实例 ID、申请状态置"审批中"
2. **Given** 回访人员完成回访问卷并提交,result=1,**When** 排他网关判定,**Then** 流程结束(审批通过)
3. **Given** 回访人员提交结果 result=-1(不通过),**When** 网关判定,**Then** 流程回退至项目经理重新提交
4. **Given** 回访人员标记 result=-2(无法回访),**When** 网关判定,**Then** 流程回退至项目经理
5. **Given** 同一回访单在不同任务节点,**When** 回访人员维护问卷,**Then** 以 `callBackId + taskId` 唯一确定一份问卷结果(同一单可挂多份不同任务的问卷)
6. **Given** 用户选择问卷模板,**When** 加载可选模板,**Then** 仅返回状态为"生效"的问卷模板

---

### User Story 3 - PM 闭环评估(项目交付后闭环)(Priority: P1)

作为**项目经理**,我希望在项目交付后发起闭环申请,经服务经理审核、回访人员回访、工程管理人员评分后完成闭环,以便对项目全生命周期做交付后评估。

**流程节点**:项目经理发起闭环申请 → 服务经理审核 → 回访人员进行回访 → 工程管理人员评分 → 结束

**Why this priority**:PM 闭环是项目交付后评估的核心机制,串联多角色协作,是闭环域最高价值场景。

**Independent Test**:可由项目经理对一个已交付项目"发起闭环 → 服务经理审核 → 回访人员回访 → 工程评分 → 闭环结束"全流程独立验证。

**Acceptance Scenarios**:

1. **Given** 项目经理(本人或具备项目经理角色)发起闭环申请且服务经理账号有效,**When** 系统启动闭环流程,**Then** 生成 PM 申请评测头(evaluationType=1),流程转入服务经理审核
2. **Given** 服务经理审核驳回,**When** 系统处理驳回,**Then** 校验项目经理账号(含 B 角)有效后回退至项目经理,并触发邮件通知
3. **Given** 回访人员提交回访问卷且达标(evaluationResult=1),**When** 网关判定,**Then** 流程转入工程评分阶段
4. **Given** 回访人员标记无法回访(evaluationResult=-3),**When** 提交,**Then** 必须填写评价意见,流程回退至服务经理
5. **Given** 回访人员提交回访问卷不达标(evaluationResult=-1),**When** 网关判定,**Then** 流程结束(不进入工程评分)
6. **Given** 工程人员提交闭环建议问卷并计分通过,**When** 完成评分节点,**Then** 评测头置结束(evaluationType=5),项目闭环状态更新
7. **Given** 回访人员标记"无法回访"前存在草稿态回访评测,**When** 触发无法回访,**Then** 系统递归删除草稿评测头/问卷结果头/问卷结果行,再生成无法回访评测
8. **Given** 流程进行中,**When** 查看流程图(pmClosedLoopResultType=0),**Then** 系统返回当前任务与项目闭环状态及最新流程定义

---

### User Story 4 - 问卷自动评分(Priority: P2)

作为**回访人员/工程人员**,我希望提交问卷后系统按模板配置的评分规则自动计算总分、各题型得分及通过/不通过结论,以便减少人工计算。

**Why this priority**:评分自动化是支撑多个 P1 流程的关键能力,但本身不构成独立业务价值,故为 P2。

**Independent Test**:可对一份配置好评阅规则的模板,提交一份已知答案的问卷,验证计分与规则判定结果。

**Acceptance Scenarios**:

1. **Given** 已配置评分规则(`markIndexs` 选用 A/B/C/D 规则链)的问卷模板,**When** 用户提交问卷,**Then** 系统按选项分值累加总分、拼接答案串(格式 `题目类型:序号-题号|选项,;`)、按规则链判定通过/驳回并回写 `quesMarkResult` 与 `evaluationResult`
2. **Given** 问卷含多种题型(quesTypeForCB:10工程项目类/20设备类/30工程师类/40其他),**When** 提交问卷,**Then** 按 quesTypeForCB 分组汇总行得分并展示题型得分列表
3. **Given** 同一问卷模板被回访流程与闭环流程同时引用,**When** 同一答案串在两处提交,**Then** 计分结果一致(计分逻辑需保持一致)

---

### User Story 5 - 闭环问卷模板管理(Priority: P2)

作为**工程管理人员**,我希望维护闭环/回访使用的问卷模板(题目、选项、分值、评分规则),并对模板执行生效/失效,以便问卷可被回访流程引用并自动计分。

**Why this priority**:模板管理是 P1 流程的前置配置能力,但属于支撑性配置,本身不直接产生业务闭环。

**Independent Test**:可独立完成"新增模板头 → 维护题目/选项 → 生效 → 被业务引用"的配置流程验证。

**Acceptance Scenarios**:

1. **Given** 工程管理人员新增问卷头(必填模板名称与问卷类型),**When** 保存,**Then** 状态置草稿,跳转编辑页继续维护题目
2. **Given** 草稿态模板已添加若干题目,**When** 触发生效操作,**Then** 系统置状态为生效、记录生效开始时间,可被业务流程引用
3. **Given** 已生效模板需停用,**When** 触发失效操作,**Then** 置状态为失效、记录失效结束时间
4. **Given** 新增/编辑题目分值,**When** 已添加题目分值之和超过问卷总分,**Then** 系统强校验拦截
5. **Given** 新增/编辑选项分值,**When** 选项分值超过所属题目分值,**Then** 系统强校验拦截
6. **Given** 新增题目,**When** 系统分配题号,**Then** 同模板下按已有最大题号 +1 自增
7. **Given** 新增问卷模板头,**When** 生成模板编号,**Then** 规则为 `CH` + yyyyMMdd + 三位流水号(基于当日最大号 +1)
8. **Given** 更新模板头(名称/总分/达标分/类型/评分规则),**When** 达标分超过总分,**Then** 校验拦截;否则状态置草稿

---

### User Story 6 - 维保/项目回访统计(Priority: P3)

作为**回访负责人**,我希望按项目维度、客户维度统计回访覆盖率、续保意向分布、最近回访时间,以便制定下一轮回访计划。

**Why this priority**:统计报表是经营决策辅助,不阻塞核心业务流程,优先级最低。

**Independent Test**:可对一组已存在回访记录的项目执行统计查询,验证聚合维度与数值。

**Acceptance Scenarios**:

1. **Given** 项目维保回访有多条历史记录,**When** 选择项目维度统计,**Then** 系统按项目聚合最近一次回访时间、回访次数、续保意向次数、维保内外状态、维保级别、续保/可续采软件
2. **Given** 多个项目归属于同一最终客户,**When** 选择客户维度统计,**Then** 按最终客户名称聚合项目数、回访项目数、回访次数、续保意向数、续保数、可续采软件数、最近回访/下次回访时间
3. **Given** 大数据量统计场景(项目数 ≥ 10 万),**When** 触发统计查询,**Then** 系统使用会话级临时表加速分页与计数,统计完成后自动清理临时表

---

### Edge Cases

- 当闭环流程办理人(服务经理/项目经理)账号失效或离职时,驳回/无法回访如何避免流程挂起?(当前通过人员有效性校验前置拦截,需保证 B 角配置完整)
- 当问卷模板处于草稿态被业务流程尝试引用时(应被拦截,仅"生效"态可被引用)
- 当 `customInfo` JSON 字段为 null 时,增量合并写入的行为需明确(应等价于整体写入)
- 当题目分值边界:已添加题目分值之和恰好等于问卷总分时(应通过校验)
- 当回访人员对同一回访单在不同任务节点维护多份问卷时(`callBackId + taskId` 唯一,需保证不互相覆盖)
- 当问卷模板已被生效业务引用后,题目分值/选项被修改时的版本控制与已生成问卷结果的一致性
- 当并发请求同一日生成问卷模板编号时,流水号冲突的处理(三位流水号上限 999/日)
- 当维保回访记录的 `customInfo` 中的 `phoneAnswerState` 基础数据被停用时,历史记录的展示与查询
- 当两套回访体系(独立回访工作流 vs PM 闭环内嵌回访节点)对同一项目并存时的数据归并语义(见 Open Questions 第 2 项)
- 当 `pmClosedLoopResultType` 取值 41/42/50/51 时的触发路径与界面映射(见 Open Questions 第 5 项)
- 当闭环网关在同一 evaluationResult 值(如 2)在不同分支语义不同时的流转正确性(见 Open Questions 第 4 项)

---

## Requirements

### Functional Requirements

#### 2.1 质保回访(项目维保回访)

| 编号 | 需求 |
|------|------|
| FR-2.1.1 | 列表查询:支持按项目编码/名称、办事处、服务类型、续保意向、接听状态、维保结束时间区间、回访时间区间、下次回访时间区间、客户联系人/联系方式、销售人筛选,默认按 id 倒序 |
| FR-2.1.2 | 访问权限:仅项目经理/服务经理/管理员/工程经理/工程经理负责人/回访人员/质保回访员/区域负责人可访问;非特权角色自动叠加区域权限与用户权限过滤 |
| FR-2.1.3 | 新建/编辑回访:进入表单时自动填充项目及维保基础信息(办事处、最终客户、维保起止、维保状态、维保级别、增值服务);可填写问卷并保存草稿或提交 |
| FR-2.1.4 | 问卷引用:回访表单从"生效中"的 `projectWarrantyCallback` 类型问卷模板中取首份,并加载其题目/选项/评分规则 |
| FR-2.1.5 | 续保意向取值:0=否、1=有、2=待定;另以 -1 表示"未填写"用于查询 |
| FR-2.1.6 | 接听状态:以 `projectWarrantyCallback_phoneAnswerState` 基础数据为准,存于 `customInfo` JSON 字段 |
| FR-2.1.7 | 软删除:仅创建人或工程经理/负责人可删除,删除置 `isDelete=true` |
| FR-2.1.8 | 维保统计视图:按项目维度聚合最近一次回访、回访次数、续保意向次数、维保内外状态、维保级别、续保/可续采软件;使用会话级临时表加速分页与计数 |
| FR-2.1.9 | 客户维度统计:按最终客户名称聚合项目数、回访项目数、回访次数、续保意向数、续保数、可续采软件数、最近回访/下次回访时间 |
| FR-2.1.10 | customInfo 合并写入:更新时以增量合并方式合并既有 JSON,而非整体覆盖 |
| FR-2.1.11 | 可编辑判定:`canEdit()` 当前实现恒返回 false(原意为"续保状态=3 或接听状态以 - 开头时可编辑",已被注释禁用) |

#### 2.2 维保回访(独立回访工作流)

| 编号 | 需求 |
|------|------|
| FR-2.2.1 | 发起申请:项目经理对项目发起回访申请,写入申请备注,启动回访工作流并回写流程实例ID与申请状态 |
| FR-2.2.2 | 申请状态:-1=草稿、1=审批中、2=审批通过 |
| FR-2.2.3 | 查看回访:回访人员/查看者可读取项目信息、最终客户成员、回访流程信息及历史审批意见 |
| FR-2.2.4 | 审批意见:审批意见取自活动历史评论表,流程定义标识为 CallBack,结果名称映射自基础数据 dataTypeCode=26 |
| FR-2.2.5 | 问卷草稿/提交:回访人员可保存问卷草稿或提交;每次保存/提交都重新生成一份问卷结果数据;提交时计算问卷分数 |
| FR-2.2.6 | 问卷与任务绑定:同一回访单在不同任务节点可挂不同问卷,以 `callBackId + taskId` 唯一确定一份 `CallBackQuesnaire` |
| FR-2.2.7 | 驳回重提:驳回后项目经理可重新提交申请 |
| FR-2.2.8 | 流程流转条件:result=1 通过(结束)、result=-1 不通过(回退项目经理)、result=-2 无法回访(回退项目经理) |
| FR-2.2.9 | 问卷模板生效约束:仅取状态为"生效"的问卷模板供选择 |

#### 2.3 PM 闭环(项目交付后闭环评估)

| 编号 | 需求 |
|------|------|
| FR-2.3.1 | 项目经理发起闭环申请:校验项目经理角色(本人或具备项目经理角色),并确认服务经理账号有效后启动闭环流程 |
| FR-2.3.2 | 服务经理审核:校验服务经理角色;若驳回则需确认项目经理账号有效(含 B 角) |
| FR-2.3.3 | 回访人员提交回访:`pmClosedLoopResultType=1` 提交回访问卷并计分,`pmClosedLoopResultType=2` 基于已有问卷结果完成回访节点;草稿态仅保存不流转 |
| FR-2.3.4 | 无法回访:回访人员可标记"无法回访",需填写评价意见,结果置为 -3,流程回退服务经理 |
| FR-2.3.5 | 工程人员闭环:`pmClosedLoopResultType=1` 提交闭环建议问卷并计分,转入看分页;`pmClosedLoopResultType=2` 完成闭环节点 |
| FR-2.3.6 | 评测类型(evaluationType):1=项目经理闭环申请、2=服务经理回访申请、3=回访人员回访、4=工程人员闭环、5=闭环结束 |
| FR-2.3.7 | 评测结果(evaluationResult):1=同意、-1=不同意、-3=无法回访 |
| FR-2.3.8 | 问卷状态(status):1=生效(SUBMIT)、-2=失效(ENDEFFEC)、-1=草稿(DRAFT)、2=提交问卷(SUBMITQUES) |
| FR-2.3.9 | 闭环流程变量:projectManager / serviceManager / callBackPerson / projectManageEmp 为各节点办理人;evaluationResult 为网关判断;projectProcessStatus 为项目闭环状态 |
| FR-2.3.10 | 闭环流程网关分支:evaluationResult=1 服务经理审核通过→回访;=-1 驳回→结束;=2 已通过回访→工程评分;=3 服务经理与项目经理一致且通过回访→工程评分;=-2 驳回任务办理→结束;回访后 =1 达标→工程评分,=-1 不达标→结束,=-3 无法回访→服务经理 |
| FR-2.3.11 | 申请头关联:回访/闭环评测头通过 `applyHeaderId` 关联到项目经理发起的闭环申请头(PM 类型) |
| FR-2.3.12 | 历史回访/闭环展示:按时间倒序取已提交的评测头,分别展示最新一条闭环建议、最新一条回访,以及项目经理申请头;问卷结果头/行/模板一并装配 |
| FR-2.3.13 | 项目经理查看回访问卷:项目经理可查看指定评测头对应的回访问卷结果与模板 |
| FR-2.3.14 | 删除草稿评测:回访人员"无法回访"前若存在草稿态回访评测,递归删除其评测头/问卷结果头/问卷结果行 |
| FR-2.3.15 | 下一审批人维护:支持按项目集合批量更新评测头的下一审批人与下一审批人名称(用于人员变更后转移) |
| FR-2.3.16 | 邮件通知:8 类邮件模板覆盖服务经理驳回/回访通过/回访不通过/同意闭环/不同意闭环/无法回访/服务经理同意闭环/项目经理发起闭环申请 |
| FR-2.3.17 | 问卷计分:提交时按选项分值累加总分,拼接答案串 `题目类型:序号-题号|选项,;`,再按模板 `markIndexs` 配置的评分规则链判定通过/驳回,并回写 `quesMarkResult` 与 `evaluationResult` |
| FR-2.3.18 | 各题型得分汇总:按 `quesTypeForCB`(10工程项目类/20设备类/30工程师类/40其他)分组汇总行得分,展示题型得分列表 |
| FR-2.3.19 | 流程图查看:`pmClosedLoopResultType=0` 时查询流程当前任务与项目闭环状态,并取最新流程定义用于展示流程图 |

#### 2.4 闭环问卷(模板管理)

| 编号 | 需求 |
|------|------|
| FR-2.4.1 | 问卷模板列表:支持按模板名称/类型/状态筛选,每页 50 条 |
| FR-2.4.2 | 新增问卷头:必填模板名称与问卷类型,可选总分/达标分/评分规则;保存后状态置草稿,跳转编辑页继续维护题目 |
| FR-2.4.3 | 题目类型(questionType):1=单选、2=多选、3=问答、4=评分;问答型无需选项 |
| FR-2.4.4 | 题目回访类型(questionTypeForCB):10=工程项目类、20=设备类、30=工程师类、40=其他(基础数据 dataTypeCode=14) |
| FR-2.4.5 | 问卷类型(quesType):30=闭环建议类(基础数据 dataTypeCode=13);另存在 `projectWarrantyCallback` 类型供维保回访引用 |
| FR-2.4.6 | 选项与分值:每个选项含编号、内容、分值;选项分值不得超过所属题目分值 |
| FR-2.4.7 | 题目分值总和校验:已添加题目分值之和不得超过问卷总分,新增/编辑时校验 |
| FR-2.4.8 | 题目编号自增:同模板下按已有最大题号 +1 |
| FR-2.4.9 | 问卷生效:置状态为生效、记录生效开始时间;生效后方可被回访/闭环流程引用 |
| FR-2.4.10 | 问卷失效:置状态为失效、记录失效结束时间 |
| FR-2.4.11 | 评分规则:规则以接口形式扩展,当前内置 A/B/C/D 四种,模板通过 `markIndexs`(逗号分隔索引)选用;规则返回 "pass"/索引/-1/-2 |
| FR-2.4.12 | 评分规则说明展示:模板编辑/查看页展示规则说明列表 |
| FR-2.4.13 | 模板头更新:更新名称/总分/达标分/类型/评分规则,达标分不得超过总分,状态置草稿 |
| FR-2.4.14 | 删除题目:按题目 ID 删除,删除后返回模板编辑页 |
| FR-2.4.15 | 模板编号生成:规则为 `CH` + 年月日 + 三位流水号 |

### Key Entities

#### 数据契约

> 字段分级:C=写入(创建/更新)、I=查询输入、D=展示输出。兼具者并列标注。
> 表名取自实际数据访问层 DML;模板三表(pmclosed_loop_quesnaire*)表名为推断,[暂定决策:采纳推断表名为规范表名,后续正向工程前需检索 iBatis sql-map 配置或 DDL 脚本最终确认,若与推断不符则回写 spec,见 AMB-005-01]。
> 本域遵循 DATA-REUSE-01:复用既有数据契约,不新增表;外部域表仅在引用点列出。

##### 3.1 pm_project_warranty_callback(项目维保回访记录)

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
| customInfo | JSON | C/I/D | 扩展信息;含 phoneAnswerState/phoneAnswerStateName;更新时增量合并(非整体覆盖),查询时按 JSON 路径提取 |

##### 3.2 pm_cl_callback(回访申请单 - 独立回访工作流)

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

##### 3.3 pm_cl_callback_quesnaire(回访任务问卷关联)

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

##### 3.4 pm_cl_evaluation_header(闭环评测头 - PM 闭环流程)

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

##### 3.5 pm_cl_quesnaire_result_header(问卷结果头)

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

##### 3.6 pm_cl_quesnaire_result_line(问卷结果行)

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

##### 3.7 pm_closed_loop_quesnaire(问卷模板头)[暂定决策:表名为推断,待 DDL 最终确认,见 AMB-005-01]

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

##### 3.8 pm_closed_loop_quesnaire_line(问卷模板行/题目)[暂定决策:表名为推断,待 DDL 最终确认,见 AMB-005-01]

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

##### 3.9 pm_closed_loop_quesnaire_opt(问卷模板选项)[暂定决策:表名为推断,待 DDL 最终确认,见 AMB-005-01]

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

##### 3.10 关联引用表(外部域,仅列引用点)

| 表 | 引用点 |
|----|--------|
| fnd_basic_data | 服务类型(dataTypeCode=15)、回访结果(dataTypeCode=26)、问卷类型(13)、题目类型(14)、流程节点(17)、接听状态(projectWarrantyCallback_phoneAnswerState) |
| fnd_user_info | 评测人/申请人姓名、审批人姓名 |
| fnd_department | 办事处名称 |
| fnd_company | 公司名称/简称 |
| fnd_act_hi_comment | 回访审批意见(流程定义标识 CallBack) |
| pm_project_header | 项目主表(column001办事处、column012服务类型、column013最终客户等) |
| pm_project_member | 项目成员(销售人 memberRole=10、客户联系人 memberRole=60) |
| view_warranty_contract_state | 维保合同状态视图(维保内外/级别/续保/可续采软件) |
| pm_project_contract / pm_project_group_relationship | 合同与项目组关系(用于聚合维保) |

---

## Success Criteria

### Measurable Outcomes

- **SC-001**(权限模型):100% 的闭环流程节点办理人按"角色 + 项目本人 + 区域权限"组合校验通过方可办理;0 起越权操作事件
- **SC-002**(数据权限过滤):非特权角色列表查询响应时间在 1000 条数据量下 ≤ 2 秒,且返回数据 100% 命中可见范围
- **SC-003**(软删除):删除的维保回访记录在默认查询中 0 条出现,但可通过显式 `isDelete=true` 查询恢复
- **SC-004**(多公司隔离):0 起跨公司数据泄露事件,A 公司用户查询 B 公司数据返回 0 条
- **SC-005**(审计字段):100% 的写入操作具备 createBy/createTime;100% 的更新操作具备 updateBy/updateTime;评测头 100% 记录评测人与评测时间
- **SC-006**(工作流驱动):100% 的回访/闭环流程由工作流引擎驱动,流程实例 ID 与业务表关联完整率 100%
- **SC-007**(邮件通知):8 类节点事件触发邮件的成功投递率 ≥ 99%,邮件含完整字段(主题/内容/收件人/抄送/预期发送时间/发送标志)
- **SC-008**(流水号生成):问卷模板编号规则 `CH + yyyyMMdd + 三位流水号`,在并发 ≤ 999 单/日的情况下 0 冲突
- **SC-009**(大数据量统计):维保统计在 10 万级项目数据量下响应时间 ≤ 5 秒,统计完成后会话级临时表 100% 清理
- **SC-010**(JSON 扩展字段):customInfo 增量合并成功率 100%,按 JSON 路径提取查询响应时间 ≤ 1 秒
- **SC-011**(问卷计分一致性):回访与闭环流程对相同答案串的计分结果一致率 100%
- **SC-012**(人员有效性校验):闭环驳回/无法回访前办理人账号有效性校验覆盖率 100%,流程挂起率 = 0
- **SC-013**(问卷模板生命周期):草稿态模板被业务引用的拦截率 100%;生效/失效时间戳记录完整率 100%
- **SC-014**(题目分值约束):题目分值之和超过问卷总分、选项分值超过题目分值的提交拦截率 100%

---

## Assumptions

- 假设项目维保期数据由外部域(view_warranty_contract_state、pm_project_contract、pm_project_group_relationship 等)维护,本域只读引用,不修改
- 假设工作流引擎与邮件发送基础设施已就绪并稳定可用
- 假设基础数据(fnd_basic_data)中的 dataTypeCode=13/14/15/17/26 及 `projectWarrantyCallback_phoneAnswerState` 由基础数据域维护
- 假设角色与权限矩阵由统一权限域维护,本域通过角色常量引用
- 假设项目主数据(pm_project_header)、项目成员(pm_project_member)、用户(fnd_user_info)、组织(fnd_department/fnd_company)由各自域维护
- 假设数据库支持 JSON 类型与 JSON 增量合并语义(JSON_MERGE_PATCH 等价能力)
- 假设数据库支持会话级临时表,统计后自动清理
- 假设用户具备稳定网络连接与浏览器支持

### Resolved Questions / 已澄清事项(原 Open Questions / 待澄清事项)

> 以下 5 条 spec 内部待澄清事项已正向固化,完整决策记录见 `clarify.md`(共 18 条 AMB,含 B/A 类)。决策结论统一以 [暂定决策:...] 形式标注,后续正向工程确认后可升级为最终决策。

1. **问卷模板三表表名**:`pm_closed_loop_quesnaire` / `pm_closed_loop_quesnaire_line` / `pm_closed_loop_quesnaire_opt` 的精确表名未在已读数据访问层 DML 中直接出现(实体与 Service 引用存在),标注为推断。[暂定决策:采纳当前推断表名作为规范表名;后续正向工程前需检索 `PmClosedLoopQuesnaire*` 对应的 iBatis sql-map 配置或 DDL 脚本最终确认,若与推断不符则回写 spec,见 AMB-005-01]
2. **两套回访体系关系**:`pm_cl_callback`(独立回访工作流)与 `pm_cl_evaluation_header`(PM 闭环内嵌回访节点 evaluationType=3)两套回访数据是否并存、是否迁移、字段映射关系不明。[暂定决策:确认两套体系并存服役,业务边界按"独立回访申请审批流"vs"PM 闭环内嵌回访环节"划分;两套数据不迁移、不归并,统计口径需按数据来源分区统计禁止跨体系混算,同一项目可在两套体系各产生回访记录但统计视图需标注来源避免重复计数,见 AMB-005-02]
3. **续保意向取值不一致**:`ProjectWarrantyCallback.renewalIntention` 注释为 0否/1有/2待定,但 `ProjectWarrantyCallbackVO.canEdit` 注释提及"续保状态为3"可编辑(当前实现已禁用恒返回 false)。值 3 的语义不明。[暂定决策:值 3 显式标注为"已废弃枚举(历史'已续保'语义,当前无写入路径)";spec 枚举仅保留 0否/1有/2待定(-1 未填写仅查询用);`canEdit()` 中"续保状态=3"逻辑已禁用,不再纳入有效取值域,见 AMB-005-03]
4. **闭环网关 evaluationResult=2/3 语义**:PM 闭环流程定义中 flow20(evaluationResult==2 "已通过回访")、flow26(==2 "服务经理和项目经理一致")、flow27(==3 "服务经理与项目经理一致且通过回访")存在同一值 2 在不同网关的不同含义,且与常量定义(1/-1/-3)不完全对应,完整流转语义需结合服务实现确认。[暂定决策:值 2/3/-2 明确标注为"流程内部网关态,非评测结果业务枚举",由 BPMN 网关表达式在流程流转中计算得出,不写入 `pm_cl_evaluation_header.evaluationResult` 业务字段;业务枚举仍以 FR-2.3.7 的 1=同意/-1=不同意/-3=无法回访 为准;网关态语义:2=已通过回访、3=服务经理与项目经理一致且通过回访、-2=驳回任务办理(结束),见 AMB-005-04]
5. **pmClosedLoopResultType 完整语义**:入口注释列出 30/40/41/42/50/51,但代码中实际使用 0/1/2/30/40,41/42/50/51 的触发路径与界面映射不明。[暂定决策:spec 仅记录实际生效取值 0=流程图查看、1=提交问卷并计分、2=基于已有问卷完成节点、30=回访表单(入口取表单/提交后跳看分页)、40=闭环表单(入口取表单/提交后跳看分页);41/42/50/51 标注为"注释残留,代码未实现,视为废弃";=1/=2 语义依赖当前节点角色与流程上下文(回访人员节点 vs 工程人员节点),见 AMB-005-05/07/08]
