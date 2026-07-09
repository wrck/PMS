# Feature Specification: 002-presales-product(售前与产品)

**Feature Branch**: `002-presales-product`

**Created**: 2026-07-09

**Status**: Draft

**Source**: 逆向反推自 PMS-struts prob 包

> 域职责:售前测试项目、技术公告(Prob)、产品组件、软件/设备版本匹配。
> 逆向来源基线(仅作溯源凭证,不约束新系统技术栈):struts-sys.xml 第 217-296 行路由配置、sql-map-prob-config.xml、sql-map-presales-config.xml、prob 包源码、Presales.bpmn;数据契约见下文"Key Entities > 数据契约"。
> 文中"证据"行均为逆向溯源引用,描述的是"系统做什么",不规定"代码怎么做"。
> 本文件为 spec.md 经 clarify.md 澄清后的更新版本,原所有待澄清标记已替换为决策结论或 [暂定决策:...]。

---

## User Scenarios & Testing

> 用户故事按重要性排序并赋予优先级(P1 最关键)。每个故事可独立测试,单独实现其一即可构成可用 MVP 切片。
> 验收场景以 Given/When/Then 表达,由功能需求反推。

### User Story 1 - 售前测试项目列表查询 (Priority: P1)

As a 工程管理部/服务经理/项目经理, I want 按状态、办事处、人员、时间筛选售前测试项目列表, so that 我能跟踪项目进展。

**Why this priority**: 列表查询是所有售前业务入口,无列表则无法进入后续流程。

**Independent Test**: 用具备售前权限的账号进入列表页,按状态/办事处筛选,可返回分页项目列表。

**Acceptance Scenarios**:
1. **Given** 工程管理人员/售前专员进入列表页,**When** 不显式指定状态,**Then** 默认只看待创建状态项目。
2. **Given** 其他售前角色进入列表页,**When** 不显式指定状态,**Then** 默认查看状态为 30/31/32/33 的项目。
3. **Given** 用户输入项目编码,**When** 提交查询,**Then** 同时匹配 presalesCode 与 projectCode 两个字段。
4. **Given** 用户选择多个项目状态(逗号分隔),**When** 提交查询,**Then** 使用多值匹配返回符合任一状态的项目。
5. **Given** 用户选择时间类型为测试完成时间/回访问卷时间/回访完成时间,**When** 提交查询,**Then** 关联活动历史任务表并按项目 ID 去重,避免重复行。
6. **Given** 列表查询返回,**When** 默认排序,**Then** 按申请时间倒序排列并强制分页。

---

### User Story 2 - 售前流程申请与审批流转 (Priority: P1)

As a 工程管理人员, I want 发起售前测试流程并经多级审批推进, so that 项目经服务经理指派、项目经理跟踪、工程管理部回访后闭环。

**Why this priority**: 审批流转是售前项目生命周期核心,驱动指派、跟踪、回访全链路。

**Independent Test**: 提交申请启动流程后,流程实例推进,各节点办理人变更,可逐级审批直至闭环或驳回。

**Acceptance Scenarios**:
1. **Given** 工程管理人员无已存在任务,**When** 提交申请,**Then** 启动新流程并写入项目主表,按基础数据初始化计划任务。
2. **Given** 流程到达工程管理部指派服务经理节点,**When** 审批结果为通过,**Then** 流程推进至服务经理指定项目经理节点。
3. **Given** 流程到达工程管理部指派节点,**When** 审批结果为驳回,**Then** 直接闭环结束流程。
4. **Given** 流程到达服务经理节点,**When** 服务经理指定项目经理并提交,**Then** 流程推进至项目经理跟踪节点。
5. **Given** 流程到达项目经理跟踪节点,**When** 项目经理提交跟踪结果,**Then** 流程推进至工程管理部回访节点。
6. **Given** 流程到达回访节点,**When** 回访人员提交回访问卷通过,**Then** 流程闭环结束。
7. **Given** 除 usertask1 外的审批节点(usertask2/usertask3/usertask4),**When** 审批结果为驳回,**Then** 流程回退到上一节点;usertask1 驳回→直接闭环(见场景 3)。[已澄清 AMB-002-09]
8. **Given** 代码中存在 serviceApprove 节点键用于服务经理审批时长统计(见已澄清事项 AMB-002-01),**When** 统计时长,**Then** 按 serviceApprove 节点 DURATION 求和计入 serviceApproveDuration。

---

### User Story 3 - 技术公告创建与发布修复任务 (Priority: P1)

As a 技术公告员, I want 创建技术公告并检索受影响设备后批量发布修复任务, so that 受影响设备能被指派办理人完成修复。

**Why this priority**: 技术公告到修复任务发布是 prob 域核心价值链,驱动设备修复闭环。

**Independent Test**: 创建公告 → 检索受影响设备 → 批量发布修复任务,可独立完成并生成修复任务记录。

**Acceptance Scenarios**:
1. **Given** 技术公告员新建公告,**When** 提交保存,**Then** 自动生成公告编号(格式 SP.yyyyMMddHHmm),状态默认为"1"。
2. **Given** 公告含受影响软件版本与关联产品型号,**When** 保存,**Then** 同步写入软件版本子表与产品型号子表。
3. **Given** 公告已创建,**When** 技术公告员检索受影响设备,**When** 匹配规则为受影响版本通过版本类型/系列关联项目设备版本且 mark 在范围内、产品物料编码匹配且有效、项目有效、未生成修复任务,**Then** 返回受影响设备列表。
4. **Given** 受影响设备列表已勾选,**When** 发布修复任务,**Then** 批量插入修复任务记录;若指派人为空则角色设为服务经理(ROLE_SERVICEMANAGER=11),否则角色为指定人(0);状态为 0 时置为开始流程(10)。
5. **Given** 技术公告列表查询,**When** 软删除过滤生效,**Then** 仅返回失效时间为空的公告。
6. **Given** 公告设置可见范围,**When** visibleRange=-1,**Then** 全部用户可见;否则仅跟踪用户本人可见。

---

### User Story 4 - 技术公告修复任务办理与统计 (Priority: P2)

As a 办理人/管理员, I want 管理指派给我的修复任务并更新状态,以及查看统计报表, so that 修复任务能闭环并量化公告影响。

**Why this priority**: 修复任务办理依赖任务已发布,统计依赖任务数据,属二级价值。

**Independent Test**: 办理人查看个人任务并更新状态,管理员查看全部任务并按状态分流,可进入统计页查看各维度报表。

**Acceptance Scenarios**:
1. **Given** 非管理员办理人,**When** 查询任务,**Then** 仅可见自己/办事处权限内任务(areapower 注入)。
2. **Given** 个人任务列表,**When** 查询,**Then** 从状态 10(已发布接受)起查。
3. **Given** 管理员查询任务,**When** 按状态分流,**Then** 31=闭环走分页查询,20=办事处返回,30=待闭环。
4. **Given** 办理人更新任务状态,**When** 提交,**Then** 创建流转过程记录(prob_restore_process)并回写 processId 到修复任务主表。
5. **Given** 办理人上传周报,**When** 提交附件,**Then** 写入周报记录并关联附件文件。
6. **Given** 进入统计页,**When** 选择 tabIndex,**Then** 按季度/受影响项目/合同发货/受影响设备维度返回统计明细与图表。
7. **Given** 统计使用临时表,**When** 统计完成,**Then** 显式删除临时表释放资源。

---

### User Story 5 - 售前项目数据查询与导出 (Priority: P2)

As a 项目相关方, I want 查询发货/借转销/核销信息并按维度导出售前数据, so that 我能掌握项目交付与财务状态。

**Why this priority**: 数据查询与导出支撑业务决策,但依赖核心流程已产生数据。

**Independent Test**: 按项目编码查询发货/借转销/核销明细,按项目/设备/回访维度导出数据。

**Acceptance Scenarios**:
1. **Given** 项目编码已知,**When** 查询发货信息,**Then** 返回关联合同/发货/条码/物料明细,默认排除退货;勾选包含退货时 UNION 退货记录。
2. **Given** 项目编码已知,**When** 查询借转销,**Then** 返回借转销明细列表。
3. **Given** 项目编码已知,**When** 查询核销,**Then** UNION 合同与 ppliCode 两个匹配条件返回核销明细。
4. **Given** 用户选择导出维度(项目/设备/回访),**When** 触发导出,**Then** 按维度生成导出数据。
5. **Given** 项目经理更新计划任务,**When** 提交完成时间晚于当前时间,**Then** 系统修正为当前时间。
6. **Given** 项目经理上传交付件,**When** 提交,**Then** 交付件 ID 追加到交付件 ID 串(逗号拼接)。
7. **Given** 删除交付件,**When** 按 fileId 移除,**Then** 按逗号边界精确移除,避免误删相邻 ID。

---

### User Story 6 - 软件版本解析与匹配 (Priority: P2)

As a 技术公告员/技术支持, I want 检索软件版本并将手工录入版本范围解析为结构化范围, so that 技术公告能精确匹配受影响设备。

**Why this priority**: 版本解析支撑受影响设备匹配,属 prob 域关键能力但依赖公告已存在。

**Independent Test**: 按 conp/cpld/boot/pcb 检索版本;将手工录入文本解析为结构化 markStart/markEnd 范围;批量重解析历史版本;批量导入版本字典。

**Acceptance Scenarios**:
1. **Given** 技术公告员输入 conp/cpld/boot/pcb 值与条件(between/regexp/like),**When** 检索,**Then** 从出厂版本表、项目设备版本表、公告版本字典三表 UNION 去重返回版本组合。
2. **Given** 技术公告员输入手工录入版本文本与平台类型,**When** 解析,**Then** 按版本正则(10 段顺序结构)解析为结构化范围,含 type/series/version/mark。
3. **Given** 范围两端缺省产品类型/产品版本/平台分支,**When** 解析,**Then** 从前一端继承固定前缀。
4. **Given** 数字部分长度不足,**When** 解析,**Then** 补零至 3 位以保证字典序与数值序一致。
5. **Given** platformType="other",**When** 解析,**Then** 直接以 entryStart/entryEnd 作为起止,不走标准解析。
6. **Given** 无匹配版本格式,**When** 解析,**Then** 抛出无匹配版本异常。
7. **Given** 技术支持触发批量重解析,**When** 处理,**Then** 按手工录入子项分组生成唯一 groupId(时间戳递增),解析失败仍写入空记录,按公告 ID 分组批量更新。
8. **Given** 三表 UNION 查询数据量大时性能需评估(见已澄清事项 AMB-002-11),**When** 检索,**Then** 需评估索引优化或缓存策略。

---

### User Story 7 - 产品与产品组件管理 (Priority: P3)

As a 产品组件管理员/技术公告员, I want 维护产品组件树与技术公告关联产品型号, so that 技术公告能关联产品并匹配受影响设备。

**Why this priority**: 主数据管理支撑业务,但属配置类能力,变更低频。

**Independent Test**: 维护产品组件树(分组/名称/版本/父节点)并支持 Excel 导入;维护公告产品型号并支持导入;按物料编码检索产品项。

**Acceptance Scenarios**:
1. **Given** 产品组件管理员新建组件(id 为空),**When** 提交,**Then** 走插入;有 id 走按主键选择性更新。
2. **Given** 管理员编辑组件 customInfo,**When** 提交,**Then** 使用 JSON 合并语义更新,避免覆盖已有键。
3. **Given** 管理员导入 Excel,**When** 解析后逐条 upsert,**Then** 按主键冲突时更新已有记录。
4. **Given** 技术公告员维护公告产品型号,**When** 查询,**Then** 支持模糊查询、模糊搜索、分页。
5. **Given** 技术公告员按 itemCode/itemModel 检索产品项,**When** 查询,**Then** 从物料表返回 item/itemName/描述,支持前缀匹配与多字段 OR 组合过滤。

---

### Edge Cases

> 由功能需求的异常处理与边界条件反推。

- **首次进入受影响项目页不查询**: checkProject 首次打开页面不查询,避免数据量过大(firstCheck 标志)。
- **附件 ID 串维护边界**: confirmFileIds/deliverFileIds 的追加与删除按逗号边界精确处理(前/中/后三种位置),避免误删相邻 ID。
- **阅读状态回退**: prob_read_log 的 status 取 GREATEST(旧值, 新值),防止已确认被回退为未确认。
- **流程终止容错**: terminate2Close 失败时返回异常消息但不中断,保证批量操作部分成功。
- **软件版本无匹配格式**: 手工录入版本无匹配正则时抛出 NoMatchedSoftVersionStrategyExecption。
- **受影响设备已生成修复任务**: 排除已存在修复任务的设备(NOT EXISTS),避免重复发布。
- **历史项目排除**: 受影响匹配要求项目有效(effectiveTo IS NULL 且非 %his 历史项目)。
- **统计临时表残留**: 统计使用临时表后必须显式 DROP,异常中断可能导致残留。
- **统计 GROUP_CONCAT 截断**: 统计前需 SET group_concat_max_len 以避免聚合字符串截断。
- **软删除统一**: 售前项目主表与技术公告主表均采用 effectiveTo 软删除,查询统一过滤 effectiveTo IS NULL。
- **时长计算幂等**: 项目时长表使用 ON DUPLICATE KEY UPDATE,重复计算不产生重复行。
- **位掩码双向同步**: 关联场景/规避方案/解决方案操作类型的逗号串与 mark 位掩码需保持一致。

#### 已澄清事项(Resolved Questions)

> 以下为原 spec 待澄清项经 ambiguities.md 澄清后的决策结论。详见 clarify.md。

- **[已澄清 AMB-002-04] projectState 取值字典**: 已知线索——10=待开始、20 关联 PresalesClose20TaskHandler(疑似直接/驳回闭环)、100 疑似正常闭环(审批通过闭环),20 与 100 触发 allDuration 全时长统计;30~33 为各审批阶段。[暂定决策:完整状态机与基础数据 dataTypeCode=27 的精确映射需核对 fnd_basic_data 实际数据后最终消解]
- **[已澄清 AMB-002-01] serviceApprove 节点**: Presales.bpmn 中不存在 id="serviceApprove" 的 userTask 节点,但 sql-map-presales-config.xml:806 引用该 taskDefKey 进行时长统计。[暂定决策:已知歧义,新系统实现时二选一——保留则需在流程定义中显式定义 serviceApprove 节点;废弃则移除 serviceApproveDuration 字段及统计逻辑]
- **[已澄清 AMB-002-11] 软件版本检索性能**: 三表 UNION 查询(fb_soft_version / pm_project_soft_version / prob_soft_version)无显式索引优化或缓存策略。[暂定决策:新系统实现时需评估查询计划与预估数据规模,视情况对 pm_project_soft_version.conp/cpld/boot/pcb 列加索引或引入缓存]
- **[已澄清 AMB-002-03] 设备日志解析策略**: 拆分为两条独立路径——(1) 手工录入版本解析走 VersionParserFactory(Legacy/New 策略,按 getPattern().matcher(input).find() 选择,对应 LegacyVersionUtil.PATTERN / SoftNewVersionUtil.PATTERN);(2) 设备日志解析走 DeviceLogParserFacade → DeviceVersionLogParser 独立组件,与 VersionParserFactory 无关。
- **[已澄清 AMB-002-02] assigneeRole 角色值**: prob_restore.assigneeRole=0(指定人)、11(服务经理 ROLE_SERVICEMANAGER,MessageUtil.java:225);更新指派人时 assigneeRole 固定重置为 0。

---

## Requirements

### Functional Requirements

> 处理规则以行为描述,不涉及技术实现细节。证据行仅为逆向溯源引用。

#### 售前测试项目管理

- **FR-PRESALES-01: 售前项目列表查询**
  - **触发条件**: 用户进入售前项目列表页。
  - **输入**: 项目编码(匹配 presalesCode 或 projectCode)、项目名称(模糊)、项目类型、办事处(单选/多选)、项目状态(单值或多值逗号分隔)、服务经理、项目经理、是否借转销、是否未核销、时间类型(申请/结束/测试完成/回访问卷/回访完成时间)+ 起止时间、分页参数。
  - **处理规则**:
    1. 工程管理人员/售前专员默认只看待创建状态;其他角色默认看 30/31/32/33 状态(证据:PresalesAction.java:170-175)。
    2. 项目编码同时匹配 presalesCode 与 projectCode(证据:sql-map-presales-config.xml:241-243)。
    3. 服务经理条件匹配 memberRole='20' 的成员或 memberRole='30' 的项目经理(证据:sql-map-presales-config.xml:235-239)。
    4. 多状态匹配使用 FIND_IN_SET(证据:sql-map-presales-config.xml:257-259)。
    5. 当时间类型为测试完成/回访问卷/回访完成时间时,关联活动历史任务表并 GROUP BY presalesId 去重(证据:sql-map-presales-config.xml:276-290)。
    6. 默认按申请时间倒序排序。
  - **输出**: 项目列表(含项目编码、名称、状态、类型、办事处、服务经理、项目经理、当前任务办理人等)。
  - **异常**: 权限不足返回错误页;查询异常返回错误页。

- **FR-PRESALES-02: 售前流程申请/重申请**
  - **触发条件**: 工程管理人员提交申请。
  - **输入**: 项目主信息(presalesCode/projectName/projectType/officeCode/salesman/productManager 等)、产品线列表、评论参数。
  - **处理规则**:
    1. 无 taskId 时启动新流程;有 taskId 时提交重新申请(证据:PresalesAction.java:242-246)。
    2. 启动流程后写入项目主表,并根据基础数据初始化计划任务(taskTypeCode 来自基础数据,证据:sql-map-presales-config.xml:381-388)。
  - **输出**: 跳转至列表页。
  - **异常**: 启动失败返回错误页并提示。

- **FR-PRESALES-03: 售前审批流转(基于 BPMN 流程)**
  - **触发条件**: 流程到达各审批节点。
  - **处理规则**(流程定义见 Presales.bpmn):
    1. usertask1 工程管理部指派服务经理:办理人 ${applyBy};分支 result==1 → usertask2;result==-1 → 直接闭环;result==2 → usertask3(同时指定服务和项目经理)。
    2. usertask2 服务经理指定项目经理:办理人 ${sm};result==1 → usertask3;result==-1 → 返回 usertask1。
    3. usertask3 项目经理跟踪项目:办理人 ${pm};result==1 → usertask4;result==-1 → 返回 usertask2。
    4. usertask4 工程管理部回访销售:办理人 ${em},候选组 ${emRole};result==1 → 闭环;result==-1 → 驳回 usertask3。
    5. 代码中存在 serviceApprove 节点键(用于服务经理审批时长统计,证据:sql-map-presales-config.xml:806),与 usertask2 关联 [暂定决策:已知歧义,见已澄清事项 AMB-002-01——新系统实现时二选一:保留则显式定义 serviceApprove 节点;废弃则移除 serviceApproveDuration 字段及统计逻辑]。
  - **输出**: 流程实例推进,任务办理人变更。
  - **异常**: 驳回后回退到上一节点(usertask1 驳回→直接闭环,见 AMB-002-09);终止流程批量闭环(terminate2Close)。

- **FR-PRESALES-04: 项目计划任务管理**
  - **触发条件**: 项目经理更新任务完成时间或交付件。
  - **输入**: presalesTaskId、taskFinshedTime(完成时间,不得晚于当前时间)、remark、fileIds。
  - **处理规则**:
    1. 完成时间若晚于当前时间,则修正为当前时间(证据:PresalesAction.java:391-393)。
    2. 交付件 ID 追加到 deliverFileIds(逗号拼接,证据:sql-map-presales-config.xml:412-416)。
    3. 删除交付件时按逗号边界精确移除 fileId(证据:sql-map-presales-config.xml:432-456)。
    4. 现场测试确认单 confirmFileIds 同样支持追加/删除(证据:sql-map-presales-config.xml:425-477)。
  - **输出**: 任务/主表附件字段更新。

- **FR-PRESALES-05: 回访问卷**
  - **触发条件**: 回访节点办理问卷。
  - **输入**: 问卷头信息(状态 1=已提交/-1=草稿)、问卷结果行列表。
  - **处理规则**:
    1. 状态=1 时计算分数:逐题按选项得分累加,拼接答案串(格式 `题类:序号-行号|选项,`)(证据:PresalesAction.java:646-684)。
    2. 按问卷的 markIndexs 规则计算评价结果(通过/驳回)(证据:PresalesAction.java:691-723)。
    3. 每次保存/提交都生成一份新的问卷结果数据(证据:PresalesAction.java:451)。
    4. 问卷结果写入 pm_presales_project_callback(证据:sql-map-presales-config.xml:331-339)。
  - **输出**: 问卷结果头/行更新;流程推进。

- **FR-PRESALES-06: 项目时长统计**
  - **触发条件**: 需要展示/导出项目各阶段耗时。
  - **处理规则**: 按流程节点(usertask1/usertask2/usertask3/usertask4/serviceApprove)的 DURATION 求和,换算为"天时分秒"字符串,空段去除前导"0天0时0分"(证据:sql-map-presales-config.xml:764-821)。写入 pm_presales_project_duration(ON DUPLICATE KEY UPDATE)。
  - **输出**: 各阶段时长字段(applyDuration/totalDuration/serviceDuration/programDuration/testDuration/callbackDuration/serviceApproveDuration)。
  - **注**: SQL 子查询中计算的 allDuration 为历史遗留冗余中间值,INSERT 列列表未包含、表定义中无该字段,新系统实现时不应包含 allDuration 计算(见 AMB-002-12)。

- **FR-PRESALES-07: 发货/借转销/RMA 查询**
  - **触发条件**: 按 projectCode 查询发货、借转销、核销。
  - **处理规则**:
    1. 发货信息关联合同/发货/条码/物料,默认排除退货(rma_no 为空);containRma=true 时 UNION 退货记录(证据:sql-map-presales-config.xml:479-523)。
    2. 借转销查 pm_presales_lend_2_sale_from_sms(证据:sql-map-presales-config.xml:525-528)。
    3. 核销查 pm_presales_lend_2_rma_from_sms,UNION contract 与 ppliCode 两个匹配条件(证据:sql-map-presales-config.xml:530-549)。
  - **输出**: 发货/借转销/核销明细列表。

#### 技术公告管理

- **FR-PROB-01: 技术公告列表查询**
  - **触发条件**: 用户进入公告列表页。
  - **输入**: probNum(模糊)、watch(跟踪)、theme(模糊)、status(单值或多值)、productType(模糊)、desc(模糊)、affectedType、affectedVersion、visibleRange、trackingUser(含 customInfo.trackingUserSearch)、relatedSceneTypesMark(位运算)、mitigationActionTypesMark(位运算)、solutionActionTypesMark(位运算)、probTicketNo(模糊)、checkSoft(是否关联软件版本)、分页/排序。
  - **处理规则**:
    1. 软删除过滤 effectiveTo IS NULL(证据:sql-map-prob-config.xml:206)。
    2. 当 checkSoft=true 时,左联 prob_softwares 聚合受影响版本串(GROUP_CONCAT 格式 `conp:x cpld:y boot:z pcb:w manual:m`)(证据:sql-map-prob-config.xml:185-205)。
    3. 位掩码匹配用 `& mark > 0`(包含任意一个,OR 语义)(证据:sql-map-prob-config.xml:152,155,156)。
    4. 可见范围:visibleRange=-1 时不过滤;否则 `visibleRange = #visibleRange# OR trackingUser = #trackingUser#`(证据:sql-map-prob-config.xml:141-146)。
    5. 默认按 createTime DESC 排序(证据:sql-map-prob-config.xml:256-258)。
    6. affectedType 过滤统一采用 `affectedType IN (#affectedType#, 0)` 语义(指定类型 OR 0=所有),见 AMB-002-08。
  - **输出**: 公告列表(含 probId/probNum/theme/watch/status/priority/trackingUser/影响版本串/各类 mark/customInfo)。

- **FR-PROB-02: 技术公告创建/编辑**
  - **触发条件**: 用户新建或编辑公告。
  - **输入**: 主题、描述、解决方案、状态(默认"1")、跟踪、优先级、产品类型、起止日期、可见范围、关联场景类型、规避/解决方案操作类型、工单号、附件、受影响软件版本列表、关联产品型号列表。
  - **处理规则**:
    1. 新建时自动生成 probNum,格式 SP.yyyyMMddHHmm(证据:ProbManageAction.java:254-255)。
    2. 关联场景/操作类型同时存"逗号分隔串"与"位掩码 mark"(证据:Prob.java:261-299)。设置 mark 时按 64 位遍历反推成员(证据:Prob.java:288-298)。
    3. 描述/解决方案模板可从配置取(prob.info.desc.template/prob.info.solution.template)(证据:ProbManageAction.java:248-249)。
    4. 编辑时回显附件名、受影响版本(序列化为 affectedVersion JSON)、关联产品型号列表(写入 customInfo.probProductList)(证据:ProbManageAction.java:261-275)。
    5. customInfo 字段使用 JSON_MERGE_PATCH(IFNULL(customInfo,"{}"), #customInfo:JSON#) 合并(证据:sql-map-prob-config.xml:339-342)。
  - **输出**: 公告主表与子表数据。

- **FR-PROB-03: 技术公告保存/更新**
  - **触发条件**: 用户提交保存。
  - **输入**: Prob 主对象、softVersionList、附件上传文件。
  - **处理规则**:
    1. 上传附件后调用文件服务写入 fnd_files,返回 fileIds(证据:ProbManageAction.java:642-646)。
    2. 状态为"0"时保持,否则强制为"1"(证据:ProbManageAction.java:648)。
    3. 保存时同步软件版本与产品型号;继续编辑(isContinue=1)返回编辑页(证据:ProbManageAction.java:665-668)。
    4. 更新时 attachments 采用追加(concat(attachments,',',#attachments#))(证据:sql-map-prob-config.xml:335)。
  - **输出**: 公告 ID;失败返回错误页。

- **FR-PROB-04: 技术公告审批**
  - **触发条件**: 技术支持人员审批。
  - **处理规则**: 先更新软件版本(updateProbSoftVersion),再更新公告状态(updateProbStatus)(证据:ProbManageAction.java:710-711)。
  - **输出**: 成功/异常消息。

- **FR-PROB-05: 技术公告删除**
  - **处理规则**: 软删除,设置 effectiveTo=NOW()(证据:sql-map-prob-config.xml:1031-1035)。
  - **输出**: 跳转列表页。

- **FR-PROB-06: 受影响项目检索与修复任务发布**
  - **触发条件**: 技术公告员检索受影响设备并发布修复任务。
  - **输入**: probId、过滤条件(serialNum/itemModel/projectName/contractNo/officeCode/marketCode/systemCode/expendCode/industryCode/areapower)。
  - **处理规则**:
    1. 受影响设备匹配规则:技术公告受影响版本(prob_softwares)通过 entryType/entrySeries 与项目设备版本(pm_project_soft_version)关联,且 conpMark BETWEEN sw.markStart AND sw.markEnd(证据:sql-map-prob-config.xml:659-682)。
    2. 同时要求 prob_product.itemCode = sv.itemCode AND prob_product.status = 1(证据:sql-map-prob-config.xml:667)。
    3. 项目必须有效(effectiveTo IS NULL 且非 %his 历史项目)(证据:sql-map-prob-config.xml:675,681)。
    4. 排除已生成修复任务的设备(NOT EXISTS prob_restore)(证据:sql-map-prob-config.xml:686)。
    5. 发布任务:批量插入 prob_restore,受 ischecked=1 标记控制(证据:sql-map-prob-config.xml:736-750)。
    6. 若指派人为空,则 assigneeRole=服务经理角色(11=ROLE_SERVICEMANAGER);否则 assigneeRole=0(指定人)(证据:ProbManageAction.java:480-484)。[已澄清 AMB-002-02]
    7. restoreStatus=0 时设为 10(开始流程),写入 prob_restore_process 表(创建流转过程记录时写入),非 prob_restore 主表(证据:ProbManageAction.java:485-486;sql-map-prob-config.xml:1002-1003)。[已澄清 AMB-002-07]
    8. affectedType=0(所有)时不限设备类型(盒式+框式均匹配),各查询统一采用 `affectedType IN (#affectedType#, 0)` 语义(见 AMB-002-08)。
  - **输出**: 修复任务记录;异常返回错误页。

- **FR-PROB-07: 修复任务管理(个人/管理员)**
  - **处理规则**:
    1. 非管理员只能看自己/办事处权限内任务(areapower 注入)(证据:ProbManageAction.java:355-371)。
    2. 个人任务:restoreStatus=10(已发布接受)起查,通过 processId LEFT JOIN prob_restore_process 获取 restoreStatus(证据:ProbManageAction.java:524;sql-map-prob-config.xml:785-786,817-818)。[已澄清 AMB-002-07]
    3. 管理员任务:按 restoreStatus 分流(31=闭环走分页查询;20=办事处返回;30=待闭环)(证据:ProbManageAction.java:596-606)。
    4. 任务列表会通过 JSON 提取受影响公告信息(JSON_EXTRACT 子查询)(证据:sql-map-prob-config.xml:874-877)。
    5. 更新任务时,创建流转过程记录(prob_restore_process)并回写 processId 到 prob_restore(证据:sql-map-prob-config.xml:1000-1019)。
    6. 更新指派人时(update_prob_restore_assignee)assigneeRole 固定重置为 0(指定人),无论原值如何(证据:sql-map-prob-config.xml:1023)。[已澄清 AMB-002-02]
  - **输出**: 任务列表;状态变更结果。

- **FR-PROB-08: 阅读确认与阅读记录**
  - **处理规则**:
    1. 进入编辑页自动记录阅读(status=0,未确认)(证据:ProbManageAction.java:390)。
    2. 阅读确认写入 status=1;同一读者同公告取 GREATEST(IFNULL(MAX(status),#status#),#status#) 保证状态单调递增(证据:sql-map-prob-config.xml:1940-1943)。
    3. 首次阅读时间取 MIN(readTime),确认时间取 MAX(commitTime)(证据:sql-map-prob-config.xml:1944-1945)。
  - **输出**: 阅读记录列表(含读者姓名、首次时间、确认时间)。

- **FR-PROB-09: 技术公告统计**
  - **触发条件**: 进入统计页(tabIndex 0-4)。
  - **处理规则**:
    1. tabIndex<2:按季度(默认当季度,可 autoAdjust 调整)统计软件版本变更明细。先建临时表 softChangeLog_tempTable(聚合设备版本变更历史),再建 statistic_tempTable(关联项目/成员/办事处)(证据:sql-map-prob-config.xml:1450-1588)。
    2. tabIndex==2:统计受影响项目列表(临时表 projectTempTable)(证据:sql-map-prob-config.xml:1721-1776)。
    3. tabIndex==3:统计合同发货软件版本明细(关联 view_shipment_info_4_pm)(证据:sql-map-prob-config.xml:1835-1938)。
    4. tabIndex==4:受影响设备明细(复用 query_prob_restore_list)。
    5. 报表按办事处分组统计受影响/总数占比(证据:sql-map-prob-config.xml:1708-1719)。
    6. 临时表使用后显式 DROP(证据:sql-map-prob-config.xml:1700-1706,1778-1780)。
  - **输出**: 统计明细列表 + 图表 HTML。

#### 软件版本解析与匹配

- **FR-VER-01: 软件版本检索**
  - **输入**: conp/cpld/boot/pcb 各自的值与条件(between/regexp/like 缺省)。
  - **处理规则**: 从 fb_soft_version、pm_project_soft_version、prob_soft_version 三表 UNION 去重查询(证据:sql-map-prob-config.xml:416-420)。between 时按 ~ 拆分为起止值(证据:SoftVersion.java:166-176)。
  - **输出**: 版本组合列表。
  - **注**: 三表 UNION 性能评估见 AMB-002-11(新系统实现时需评估查询计划与数据规模,视情况加索引或缓存)。

- **FR-VER-02: 手工录入版本范围解析**
  - **输入**: manualEntry(手工录入文本)、platformType、softVersionTypes(releaseType/architectureType/branchType 组合)、entryStart/entryEnd。
  - **处理规则**(版本解析规则,证据 SoftVersionStrategy.java:18-116):
    1. 版本由 10 个可选部分顺序组成,正则按命名捕获组解析:Hvvv(产品类型)、Evvv(产品版本,E=S/B/A 发布类型+3 位 VRB 号)、Fxxx(平台分支,C=Conplat/S=Smartplat+3 位编号)、Dxxx(官网版本阶段,D+3 位流水)、Pxx(阶段版本,P+2-3 位流水)、PATCHxx(补丁,PATCH+2-3 位流水)、Txx(内部测试版本,T+2-3 位流水)、Lxx(定制版本,L+2-3 位流水)、LATCHxx(定制补丁)、MATCHxx(多版本补丁匹配)。
       - **[已澄清 AMB-002-05]**: 正则另含可选 BinExt 扩展名捕获组 `(?<BinExt>\\.[A-z]{1,})?`,匹配二进制文件扩展名(如 .app/.bin),不计入 10 段版本结构,解析时单独处理。
       - **[已澄清 AMB-002-06]**: LATCHxx 为语义标识(定制版本 Lxx 之后的补丁),正则匹配 `PATCH\d{2,3}`,无 LATCH 字面量前缀;MATCHxx 为一个或多个 `PATCH\d{2,3}` 序列(用于多版本补丁匹配),无 MATCH 字面量前缀。两者均复用 PATCH 格式。
    2. 范围表达式由"版本 + 分隔符(~ 及之前/及之后/前后) + 版本"构成,支持中文括号"包含/不含"修饰(证据:SoftVersionStrategy.java:21-28)。
    3. 范围继承:范围两端若缺省 Hvvv/Evvv/Fxxx,则从前一端继承(证据:SoftVersionStrategy.java:39, rangeInheritParts)。
    4. 缺省值:起始用 indexMarkMapStart(如 Evvv 起始"0000",Dxxx 起始"D000"),结束用 indexMarkMapEnd(如 Evvv 结束"Z999",Dxxx 结束"D999")(证据:SoftVersionStrategy.java:86-112)。
    5. 数字部分补零至 3 位以保障字典序比较(证据:SoftVersionStrategy.java:164-180)。
    6. platformType="other" 时直接以 entryStart/entryEnd 作为起止,不走标准解析(证据:ProbManageAction.java:881-887)。
  - **输出**: 结构化解析结果 Map<manualEntry, Map<manualEntrySub, [startParser, endParser]>>,每个 parser 含 type/series/version/mark。
  - **异常**: 无匹配版本格式时抛 NoMatchedSoftVersionStrategyExecption(证据:SoftVersionParserFactory.java:21)。

- **FR-VER-03: 批量重解析历史版本**
  - **处理规则**(证据:ProbManageAction.java:900-976):
    1. 查询所有版本(含 splited=0 的未拆解项)。
    2. 对有 manualEntry 的项重新解析,按 manualEntry 子项分组,每组生成唯一 groupId(基于时间戳,递增防重)。
    3. 解析失败(子项为空)时仍写入空记录(manualEntrySub/entryType 等为空)。
    4. 无 manualEntry 的项 groupId=0,原样保留。
    5. 按 probId 分组批量更新(updateProbSoftVersion)。

- **FR-VER-04: 设备日志版本解析(独立路径)**
  - **触发条件**: 解析设备日志中的版本信息。
  - **处理规则**: 设备日志解析走独立组件 DeviceLogParserFacade → DeviceVersionLogParser.parse()/matches(),与 VersionParserFactory 无关(证据:DeviceLogParserFacade.java:8-14)。[已澄清 AMB-002-03]
  - **注**: 手工录入版本解析(走 VersionParserFactory,Legacy/New 策略按 getPattern().matcher(input).find() 选择)与设备日志解析为两条独立路径,不可混淆。

#### 产品与产品组件管理

- **FR-COMP-01: 产品组件 CRUD**
  - **权限**: input/save/import 需 ROLE_ADMIN 或 ROLE_COMPONENT_ADMIN(证据:ProbManageAction.java:1301,1315,1336)。
  - **处理规则**:
    1. 新建:id 为空走 insert;有 id 走 updateByIdSelective(证据:ProbManageAction.java:1319-1323)。
    2. customInfo 合并:JSON_MERGE_PATCH(IFNULL(customInfo,"{}"), #customInfo:JSON#)(证据:sql-map-prob-config.xml:2168,2182-2185)。
    3. 查询支持模糊(type/name 模糊,version/parentId/state 精确)与 fuzzySearch(三字段 OR 模糊)(证据:sql-map-prob-config.xml:2208-2246)。
    4. 导入:Excel 解析后逐条 upsert(insertOrUpdateProductComponentSelective ON DUPLICATE KEY UPDATE)(证据:sql-map-prob-config.xml:2128-2173)。
  - **输出**: 列表/分页/操作结果。

- **FR-PROBPROD-01: 技术公告产品 CRUD**
  - **权限**: input 需 ROLE_ADMIN/ROLE_PROB_ADMIN/ROLE_PROB_RD;save/import 需 ROLE_ADMIN/ROLE_COMPONENT_ADMIN(证据:ProbManageAction.java:1215,1229,1246)。
  - **处理规则**: 与产品组件类似,支持模糊查询、fuzzySearch、分页、Excel 导入(前缀 probProduct.info.)(证据:sql-map-prob-config.xml:2290-2539)。
  - **输出**: 列表/分页/操作结果。

- **FR-PRODITEM-01: 产品项检索**
  - **处理规则**: 从 fb_items 查询 item/itemName/describe_,支持 itemCode/itemModel 前缀匹配与 itemGroups/itemFilters OR 组合过滤(证据:sql-map-prob-config.xml:2541-2577)。result="json" 时直接返回 JSON(证据:ProbManageAction.java:1168-1171)。

### Key Entities

#### 数据契约

> 字段分级说明:C=契约字段(对外稳定);I=内部字段(实现细节);D=废弃字段。
> 表名按实际物理表名;别名/视图单独标注。
> 以下表结构为 DATA-REUSE-01 表结构契约,新系统应复用现有表结构与字段语义。

##### 表 pm_presales_project_header(售前项目主表)

> 证据:sql-map-presales-config.xml:9-55(resultMap)、296-308(update)、341-355(update_state)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| presalesId | int | 否 | 业务主键 | 自增 | C |
| instId | varchar | 是 | 流程实例 ID | 启动流程后非空 | C |
| applyState | int | 是 | 申请状态:-1 草稿、1 审批中、2 审批通过 | nullValue=-1 | C |
| applyBy | varchar | 是 | 申请人 | 创建时写入 | C |
| applyTime | datetime | 是 | 申请时间 | 创建时写入 | C |
| endTime | datetime | 是 | 结束时间 | 闭环时写入 | C |
| presalesCode | varchar | 是 | 项目编码(正式) | 闭环时 CONCAT(projectCode,'-',num) | C |
| projectCode | varchar | 是 | 原项目编码(借货/SMS) | 来源 SMS | C |
| projectName | varchar | 是 | 项目名称 | - | C |
| projectState | varchar | 是 | 项目状态(基础数据 dataTypeCode=27) | 10=待开始等 | C |
| projectType | varchar | 是 | 项目类型(基础数据 dataTypeCode='presalesType') | - | C |
| marketName | varchar | 是 | 市场部名称 | - | C |
| systemName | varchar | 是 | 系统部名称 | - | C |
| expendName | varchar | 是 | 拓展部名称 | - | C |
| industryName | varchar | 是 | 行业名称 | - | C |
| officeCode | varchar | 是 | 办事处编码(关联 fnd_department.departmentNum) | - | C |
| salesman | varchar | 是 | 销售人员 | - | C |
| salesmanLink | varchar | 是 | 联系方式 | - | C |
| productManager | varchar | 是 | 产品经理 | - | C |
| confirmFileIds | varchar | 是 | 现场测试确认单文件 ID 串(逗号分隔) | 追加/删除式维护 | C |
| closeRemark | varchar | 是 | 关闭备注 | 闭环时写入 | C |
| customInfo | json | 是 | 自定义信息(JSON) | JSON_MERGE_PATCH 合并 | C |
| source | varchar | 是 | 数据来源 | - | C |
| hasTransfer | varchar | 是 | 是否存在借转销数据 | 0/1 | C |
| hasRma | varchar | 是 | 是否存在未核销数据 | 0/1 | C |
| createBy | varchar | 是 | 创建人 | 审计字段 | I |
| createTime | datetime | 是 | 创建时间 | 审计字段 | I |
| updateBy | varchar | 是 | 更新人 | 审计字段 | I |
| updateTime | datetime | 是 | 更新时间 | IFNULL(updateTime,now()) | I |
| effectiveFrom | datetime | 是 | 生效起始 | 默认 now() | I |
| effectiveTo | datetime | 是 | 失效时间 | 软删除标记 | I |

> 注:projectState 取值见基础数据 27;常见值 10(待开始)、20(关联 PresalesClose20TaskHandler,疑似直接/驳回闭环)、30/31/32/33(各审批阶段)、100(疑似正常闭环) [暂定决策:见已澄清事项 AMB-002-04——完整状态机需核对 fnd_basic_data dataTypeCode=27 实际数据]。

##### 表 pm_presales_project_product_line(售前项目产品线)

> 证据:sql-map-presales-config.xml:77-96

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| productLineId | int | 否 | 主键 | 自增 | C |
| presalesId | int | 是 | 关联售前项目 | nullValue=0 | C |
| lendInfoId | varchar | 是 | SMS 借货主键 ID | 与 presalesId 二选一关联 | C |
| productFirstName | varchar | 是 | 产品大类名称 | - | C |
| productTypeName | varchar | 是 | 产品类型名称 | - | C |
| itemCode | varchar | 是 | 物料编码 | 关联 fb_items.item | C |
| itemModel | varchar | 是 | 物料型号 | - | C |
| itemDesc | varchar | 是 | 物料描述 | - | C |
| productNum | int | 是 | 产品数量 | - | C |
| transferNum | int | 是 | 借转销数量 | - | C |
| hexiaoNum | int | 是 | 核销数量 | - | C |
| remark | varchar | 是 | 备注 | - | C |
| effectiveFrom | datetime | 是 | 生效起始 | - | I |
| effectiveTo | datetime | 是 | 失效时间 | 查询过滤 IS NULL | I |

> 业务不变量:产品线通过 presalesId 或 lendInfoId 关联;update_presales_product 会按 lendInfoId 回填 presalesId(证据:sql-map-presales-config.xml:370-375)。

##### 表 pm_presales_project_callback(售前回访问卷)

> 证据:sql-map-presales-config.xml:310-339

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| presalesId | int | 否 | 关联售前项目 | - | C |
| taskId | varchar | 否 | 关联流程任务 ID | 联合唯一(presalesId+taskId) | C |
| quesnaireId | int | 是 | 问卷结果头 ID | - | C |
| quesnaireVersion | int | 是 | 问卷版本 | - | C |
| quesnaireState | int | 是 | 问卷状态:1 已提交、-1 草稿 | - | C |
| createBy | varchar | 是 | 创建人 | - | I |
| createTime | datetime | 是 | 创建时间 | now() | I |
| updateBy | varchar | 是 | 更新人 | - | I |
| updateTime | datetime | 是 | 更新时间 | - | I |
| effectiveFrom | datetime | 是 | 生效起始 | now() | I |

##### 表 pm_presales_project_duration(售前项目时长)

> 证据:sql-map-presales-config.xml:764-821
> 命名说明 [已澄清 AMB-002-10]:serviceDuration = usertask1 阶段耗时(该阶段由工程管理部指派服务经理);programDuration = usertask2 阶段耗时(该阶段由服务经理指定项目经理)。字段名取自阶段产出(被指派角色)而非操作人。

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| presalesId | int | 否 | 关联售前项目(唯一) | ON DUPLICATE KEY UPDATE | C |
| instId | varchar | 是 | 流程实例 ID | - | C |
| totalDuration | varchar | 是 | 总耗时(天时分秒串) | TIMESTAMPDIFF*1000 换算 | C |
| serviceDuration | varchar | 是 | 服务经理指派耗时(usertask1 DURATION 求和;阶段产出=服务经理被指派) | - | C |
| programDuration | varchar | 是 | 项目经理指派耗时(usertask2;阶段产出=项目经理被指派) | - | C |
| testDuration | varchar | 是 | 测试跟踪耗时(usertask3) | - | C |
| callbackDuration | varchar | 是 | 回访耗时(usertask4) | - | C |
| serviceApproveDuration | varchar | 是 | 服务经理审批耗时(serviceApprove,见 AMB-002-01 暂定决策) | - | C |
| applyDuration | varchar | 是 | 项目同步到开始的耗时 | 视图 view_presales_project_duration | C |

> 注:allDuration 为历史遗留冗余中间计算值(SQL 子查询计算但未持久化),新系统实现时不应包含(见 AMB-002-12)。

##### 表 prob_main(技术公告主表)

> 证据:sql-map-prob-config.xml:14-30(insert)、159-294(resultMap)、322-380(update)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键(列名为 id,映射 probId) | 自增,last_insert_id | C |
| probNum | varchar | 否 | 公告编号 | 格式 SP.yyyyMMddHHmm | C |
| probTicketNo | varchar | 是 | 工单号 | - | C |
| watch | varchar | 是 | 跟踪(基础数据 dataTypeCode='30') | - | C |
| theme | varchar | 是 | 主题 | - | C |
| desc | text | 是 | 公告描述(富文本) | 列名 desc 为保留字,需反引号 | C |
| solution | text | 是 | 解决方案(富文本) | - | C |
| status | varchar | 是 | 状态(基础数据 dataTypeCode='31') | "0"/"1" 等 | C |
| startdate | datetime | 是 | 开始日期 | - | C |
| duedate | datetime | 是 | 计划完成日期 | - | C |
| attachments | varchar | 是 | 附件文件 ID 串(逗号分隔) | 追加式 concat | C |
| priority | varchar | 是 | 严重级别(基础数据 dataTypeCode='32') | - | C |
| productType | varchar | 是 | 产品类型(模糊匹配) | - | C |
| trackingUser | varchar | 是 | 跟踪用户(username) | - | C |
| visibleRange | int | 是 | 可见范围 | -1=全部,否则受限 | C |
| relatedSceneTypes | varchar | 是 | 关联场景类型(逗号分隔) | 与 mark 双向同步 | C |
| relatedSceneTypesMark | bigint | 是 | 关联场景类型位掩码 | 按 64 位遍历反推 | C |
| mitigationActionTypes | varchar | 是 | 规避方案操作类型(逗号分隔) | 与 mark 双向同步 | C |
| mitigationActionTypesMark | bigint | 是 | 规避方案操作类型位掩码 | - | C |
| solutionActionTypes | varchar | 是 | 解决方案操作类型(逗号分隔) | 与 mark 双向同步 | C |
| solutionActionTypesMark | bigint | 是 | 解决方案操作类型位掩码 | - | C |
| customInfo | json | 是 | 自定义信息(JSON) | JSON_MERGE_PATCH 合并 | C |
| remark | varchar | 是 | 备注 | - | C |
| createBy | varchar | 是 | 创建人 | 审计字段 | I |
| createTime | datetime | 是 | 创建时间 | now() | I |
| updateBy | varchar | 是 | 更新人 | 审计字段 | I |
| updateTime | datetime | 是 | 更新时间 | now() | I |
| effectiveFrom | datetime | 是 | 生效起始 | now() | I |
| effectiveTo | datetime | 是 | 失效时间 | 软删除标记,IS NULL 过滤 | I |

##### 表 prob_softwares(技术公告受影响软件版本)

> 证据:sql-map-prob-config.xml:486-516(insert)、517-531(query)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| probId | int | 否 | 关联技术公告 | - | C |
| conp | varchar | 是 | 主程序版本 | 与 entryType 等组合匹配 | C |
| cpld | varchar | 是 | CPLD 版本 | - | C |
| boot | varchar | 是 | Boot 版本 | - | C |
| pcb | varchar | 是 | PCB 版本 | - | C |
| manualEntry | varchar | 是 | 手工录入文本 | 与 conp/cpld/boot/pcb 互斥使用 | C |
| manualEntrySub | varchar | 是 | 手工录入子项(解析后) | - | C |
| entryType | varchar | 是 | 版本类型(解析后,对应 pm_project_soft_version.conpType) | - | C |
| entrySeries | varchar | 是 | 版本系列(解析后,对应 conpSeries) | - | C |
| entryStart | varchar | 是 | 版本范围起始(原始) | - | C |
| entryEnd | varchar | 是 | 版本范围结束(原始) | - | C |
| markStart | varchar | 是 | 缺省补充后的起始 mark | 用于 BETWEEN 匹配 | C |
| markEnd | varchar | 是 | 缺省补充后的结束 mark | 用于 BETWEEN 匹配 | C |
| affectedType | int | 是 | 影响版本类型:0 所有、1 盒式、2 框式(基础数据 'sofeVersionAffectedType');0=所有时匹配盒式+框式(见 AMB-002-08) | - | C |
| groupId | bigint | 是 | 分组 ID(同 manualEntry 子项共享) | 时间戳递增 | C |
| splited | int | 是 | 是否已拆解:0 否、1 是 | - | C |
| datastate | int | 是 | 数据状态:1 有效、0 失效 | 失效时 datastate=0 | C |
| customInfo | json | 是 | 自定义信息(JSON) | - | C |
| createBy | varchar | 是 | 创建人 | - | I |
| createTime | datetime | 是 | 创建时间 | now() | I |
| updateBy | varchar | 是 | 更新人 | - | I |
| updateTime | datetime | 是 | 更新时间 | now() | I |

##### 表 prob_soft_version(技术公告软件版本字典)

> 证据:sql-map-prob-config.xml:1212-1219(batch_add)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| conp | varchar | 是 | 主程序版本 | INSERT IGNORE 去重 | C |
| cpld | varchar | 是 | CPLD 版本 | - | C |
| boot | varchar | 是 | Boot 版本 | - | C |
| pcb | varchar | 是 | PCB 版本 | - | C |
| createdBy | varchar | 是 | 创建人 | - | I |

##### 表 prob_restore(技术公告修复任务)

> 证据:sql-map-prob-config.xml:736-750(insert)、821-860(resultMap)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| probId | int | 否 | 关联技术公告 | - | C |
| serialNum | varchar | 否 | 设备序列号 | 关联 pm_project_soft_version.barcode | C |
| itemModel | varchar | 是 | 设备型号 | - | C |
| processId | int | 是 | 关联流转过程 ID | 关联 prob_restore_process.id | C |
| officeCode | varchar | 是 | 办事处编码 | - | C |
| conp | varchar | 是 | 主程序版本(发布时) | - | C |
| boot | varchar | 是 | Boot 版本(发布时) | - | C |
| cpld | varchar | 是 | CPLD 版本(发布时) | - | C |
| pcb | varchar | 是 | PCB 版本(发布时) | - | C |
| projectId | int | 是 | 关联项目 | - | C |
| projectName | varchar | 是 | 项目名称 | - | C |
| contractNo | varchar | 是 | 合同号 | - | C |
| assignee | varchar | 是 | 办理人(username) | - | C |
| assigneeRole | int | 是 | 办理角色:0=指定人、11=服务经理(ROLE_SERVICEMANAGER);更新指派人时固定重置为 0(见 AMB-002-02) | - | C |
| createBy | varchar | 是 | 创建人 | - | I |
| createTime | datetime | 是 | 创建时间 | now() | I |
| updateBy | varchar | 是 | 更新人 | - | I |
| updateTime | datetime | 是 | 更新时间 | now() | I |

> 注:restoreStatus/restoreRemark 不在此表,而在 prob_restore_process(通过 processId 关联)(证据:sql-map-prob-config.xml:836-838)。restoreStatus 写入 prob_restore_process 表(创建流转过程记录时写入),查询通过 processId LEFT JOIN prob_restore_process 获取(见 AMB-002-07)。

##### 表 prob_restore_process(技术公告修复流转过程)

> 证据:sql-map-prob-config.xml:1000-1008(insert)、1026-1030(count)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增,last_insert_id | C |
| probId | int | 否 | 关联技术公告 | - | C |
| restoreStatus | int | 是 | 修复状态(基础数据 dataTypeCode='33') | 10=开始流程、20=办事处返回、30=待闭环、31=闭环 | C |
| restoreRemark | varchar | 是 | 流转备注说明 | - | C |
| createBy | varchar | 是 | 创建人 | - | I |
| createTime | datetime | 是 | 创建时间 | now() | I |

##### 表 prob_restore_weekly(技术公告修复进展周报)

> 证据:sql-map-prob-config.xml:1045-1050(insert)、1051-1067(query)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| probId | int | 否 | 关联技术公告 | - | C |
| fileId | int | 否 | 关联附件文件(fnd_files.id) | - | C |
| createBy | varchar | 是 | 上传人 | - | I |
| createTime | datetime | 是 | 上传时间 | now() | I |

> 查询时联表 fnd_files 取 fileName/uploadTime,联表 fnd_user_info 取 uploadUser(格式 username-realName)(证据:sql-map-prob-config.xml:1057-1067)。

##### 表 prob_read_log(技术公告阅读记录)

> 证据:sql-map-prob-config.xml:1940-1953(insert)、1954-1979(query)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| probId | int | 否 | 关联技术公告 | - | C |
| reader | varchar | 否 | 读者(username) | 联合(probId+reader) | C |
| readTime | datetime | 是 | 阅读时间 | NOW() | C |
| status | int | 是 | 状态:0 未确认、1 已确认 | GREATEST 单调递增 | C |
| firstTime | datetime | 是 | 首次阅读时间 | MIN(readTime) | C |
| commitTime | datetime | 是 | 确认时间 | status=1 时 NOW() | C |

> insert 语义:对已存在(probId+reader)记录则更新,status=GREATEST(IFNULL(MAX(status),#status#),#status#)、firstTime=IFNULL(MIN(readTime),now())、commitTime=IFNULL(MAX(commitTime), IF(#status#=1,now(),null))(证据:sql-map-prob-config.xml:1940-1953)。

##### 表 prob_product(技术公告关联产品)

> 证据:sql-map-prob-config.xml:2290-2308(resultMap)、2319-2333(insert)、2368-2405(update)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| probId | int | 是 | 关联技术公告 | - | C |
| productCode | varchar | 是 | 产品大类 | - | C |
| productSubCode | varchar | 是 | 产品小类 | - | C |
| itemCode | varchar | 是 | 物料编码 | 关联 fb_items.item,受影响设备匹配键 | C |
| itemModel | varchar | 是 | 物料型号 | - | C |
| itemDesc | varchar | 是 | 物料描述 | - | C |
| status | int | 是 | 状态:0 失效、1 有效 | 受影响匹配要求 status=1 | C |
| customInfo | json | 是 | 自定义信息(JSON) | JSON_MERGE_PATCH 合并 | C |
| createBy | varchar | 是 | 创建人 | - | I |
| createTime | datetime | 是 | 创建时间 | - | I |
| updateBy | varchar | 是 | 更新人 | - | I |
| updateTime | datetime | 是 | 更新时间 | - | I |

##### 表 prob_product_component(产品组件)

> 证据:sql-map-prob-config.xml:2052-2064(resultMap)、2085-2127(insert)、2174-2207(update)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| type | varchar | 是 | 分组 | 模糊查询 | C |
| name | varchar | 是 | 名称 | 模糊查询 | C |
| version | varchar | 是 | 版本 | 精确查询 | C |
| parentId | int | 是 | 父节点 ID(树结构) | - | C |
| state | bit | 是 | 状态(布尔) | - | C |
| customInfo | json | 是 | 自定义信息(JSON) | JSON_MERGE_PATCH 合并 | C |
| createBy | varchar | 是 | 创建人 | - | I |
| createTime | datetime | 是 | 创建时间 | - | I |
| updateBy | varchar | 是 | 更新人 | - | I |
| updateTime | datetime | 是 | 更新时间 | - | I |

##### 表 pm_project_soft_version(项目设备软件版本)— 跨域引用

> 证据:sql-map-prob-config.xml:630-658(sqlColumn)、1082-1100(resultMap)
> 说明:此表属于项目域,但本域通过受影响匹配与统计大量引用,故列为引用契约。

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | - | C(引用) |
| projectId | int | 是 | 关联项目 | - | C(引用) |
| logId | int | 是 | 关联变更日志 | - | C(引用) |
| contractNo | varchar | 是 | 合同号 | - | C(引用) |
| itemCode | varchar | 是 | 物料编码 | - | C(引用) |
| datastate | int | 是 | 数据状态:1 有效 | 匹配时要求 =1 | C(引用) |
| barCode | varchar | 是 | 设备序列号(barcode) | 关联 fb_soft_version.serial_number | C(引用) |
| conp | varchar | 是 | 当前主程序版本 | - | C(引用) |
| conpType | varchar | 是 | 主程序版本类型 | 与 prob_softwares.entryType 匹配 | C(引用) |
| conpSeries | varchar | 是 | 主程序版本系列 | 与 entrySeries 匹配 | C(引用) |
| conpMark | varchar | 是 | 主程序版本 mark | BETWEEN markStart AND markEnd | C(引用) |
| conpBak | varchar | 是 | 变更前主程序版本 | - | C(引用) |
| conpChange | int | 是 | 是否变更:0/1 | nullValue=0 | C(引用) |
| cpld | varchar | 是 | 当前 CPLD 版本 | - | C(引用) |
| cpldBak | varchar | 是 | 变更前 CPLD | - | C(引用) |
| cpldChange | int | 是 | 是否变更 | - | C(引用) |
| boot | varchar | 是 | 当前 Boot 版本 | - | C(引用) |
| bootBak | varchar | 是 | 变更前 Boot | - | C(引用) |
| bootChange | int | 是 | 是否变更 | - | C(引用) |
| pcb | varchar | 是 | 当前 PCB 版本 | - | C(引用) |
| pcbBak | varchar | 是 | 变更前 PCB | - | C(引用) |
| pcbChange | int | 是 | 是否变更 | - | C(引用) |
| executeTime | datetime | 是 | 执行更新时间 | 统计时间过滤键 | C(引用) |
| customInfo | json | 是 | 自定义信息 | - | C(引用) |
| createBy/createTime/updateBy/updateTime | - | 是 | 审计字段 | - | I(引用) |

##### 表 fb_soft_version(出厂软件版本)— 跨域引用

> 证据:sql-map-prob-config.xml:416、579
> 说明:出厂版本字典,本域检索与受影响匹配时引用。

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| serial_number | varchar | 否 | 设备序列号(主键) | - | C(引用) |
| conp | varchar | 是 | 出厂主程序版本 | - | C(引用) |
| cpld | varchar | 是 | 出厂 CPLD | - | C(引用) |
| boot | varchar | 是 | 出厂 Boot | - | C(引用) |
| pcb | varchar | 是 | 出厂 PCB | - | C(引用) |

##### 视图/临时表(非持久化契约)

| 名称 | 类型 | 语义说明 | 证据 |
|---|---|---|---|
| view_presales_project_duration | 视图 | 售前项目时长(含 applyDuration) | sql-map-presales-config.xml:73 |
| view_presales_project_duration_temp | 视图 | 售前项目时长(导出用) | sql-map-presales-config.xml:638 |
| view_shipment_info_4_pm | 视图 | 发货信息(供 PMS 用) | sql-map-prob-config.xml:1856 |
| view_shipment_ems_4_pm | 视图 | 发货 EMS 信息 | sql-map-prob-config.xml:1857 |
| temp_presales_product_line | 临时表 | 售前产品线(导出用) | sql-map-presales-config.xml:739-762 |
| statistic_tempTable | 临时表 | 技术公告统计中间表 | sql-map-prob-config.xml:1547 |
| softChangeLog_tempTable | 临时表 | 软件变更日志中间表 | sql-map-prob-config.xml:1450 |
| projectTempTable | 临时表 | 受影响项目中间表 | sql-map-prob-config.xml:1721 |
| presales_tempQuesnaireResultTable | 临时表 | 问卷结果(导出用) | sql-map-presales-config.xml:669 |

##### 关于"prob_file"与"prob_statistic"

- **prob_file**:代码中存在 ProbFile Bean,但实际附件存储在通用 fnd_files 表,prob_main.attachments 仅存文件 ID 串。无独立 prob_file 物理表。证据:query_prob_file_map 查 fnd_files(证据:sql-map-prob-config.xml:314-320)、query_prob_file_list 查 fnd_files(证据:sql-map-prob-config.xml:1040-1044)。fnd_files 字段:id/fileName/filePath/uploadBy/uploadTime。
- **prob_statistic**:ProbStatistic Bean 为查询结果对象,无独立持久化表。统计使用临时表 statistic_tempTable,查询后显式 DROP。

---

## Success Criteria

> 由非功能需求转为可测量标准。技术栈无关,可量化验证。

### Measurable Outcomes

- **SC-001(源自 NFR-PERF-01 列表分页)**: 售前列表与技术公告列表均强制分页,任何列表查询必须在单页内返回,禁止全量返回;分页参数(offset/pagesize)必须被尊重。
- **SC-002(源自 NFR-PERF-02 去重)**: 当时间类型为测试完成/回访问卷/回访完成时间时,售前列表查询结果不得因关联活动历史任务表而产生重复行(按项目 ID 去重)。
- **SC-003(源自 NFR-PERF-03 统计临时表)**: 技术公告统计涉及大表 JOIN 时采用"先建临时表再分页查询"模式;统计完成后必须显式删除临时表(无残留);统计前设置 group_concat_max_len 以避免聚合字符串截断。
- **SC-004(源自 NFR-PERF-04 UNION 去重)**: 软件版本检索从三表(出厂版本/项目设备版本/公告版本字典)UNION 去重查询;数据量大时的索引优化与缓存策略需评估 [暂定决策:见已澄清事项 AMB-002-11——新系统实现时需评估查询计划与数据规模,视情况对 pm_project_soft_version.conp/cpld/boot/pcb 列加索引或引入缓存]。
- **SC-005(源自 NFR-PERF-05 首次不查询)**: checkProject 首次打开页面不查询,避免首次加载数据量过大。
- **SC-006(源自 NFR-CONS-01 软删除统一)**: 售前项目主表与技术公告主表均采用 effectiveTo 软删除;所有查询统一过滤 effectiveTo IS NULL,失效记录不得出现在业务查询结果中。
- **SC-007(源自 NFR-CONS-02 JSON 合并)**: prob_main/prob_product/prob_product_component/售前项目主表的 customInfo 字段统一使用 JSON_MERGE_PATCH 合并更新,不得覆盖已有键。
- **SC-008(源自 NFR-CONS-03 位掩码同步)**: 关联场景/规避方案/解决方案操作类型的逗号串与 mark 位掩码必须双向同步一致,任一变更另一随之更新。
- **SC-009(源自 NFR-CONS-04 阅读单调)**: prob_read_log 的 status 取 GREATEST(旧值, 新值),已确认状态不得被回退为未确认。
- **SC-010(源自 NFR-CONS-05 任务流转一致)**: 发布/更新修复任务时,先创建流转过程记录取得 processId,再回写修复任务主表;批量操作使用 FIND_IN_SET 定位。
- **SC-011(源自 NFR-CONS-06 附件原子性)**: confirmFileIds/deliverFileIds 的追加与删除按逗号边界精确处理(前/中/后三种位置),不得误删相邻 ID。
- **SC-012(源自 NFR-CONS-07 时长幂等)**: 项目时长表使用 ON DUPLICATE KEY UPDATE,重复计算不得产生重复行。
- **SC-013(源自 NFR-AVAIL-01 权限分级)**: 技术公告角色(技术公告员/技术支持/研发/组件管理员/管理员)与售前角色(工程管理部/售前专员/项目查看者/服务经理)权限分级必须被强制;导入软件版本需技术支持权限,产品组件维护需组件管理员权限,公告产品录入需技术公告员/研发权限。
- **SC-014(源自 NFR-AVAIL-02 办事处数据权限)**: 非管理员查询修复任务时按 areapower(用户所属办事处权限)注入过滤;管理员/技术支持可见全部。
- **SC-015(源自 NFR-AVAIL-03 可见范围)**: 技术公告 visibleRange=-1 时全部可见,否则仅 trackingUser 本人可见;受限公告不得对其他用户暴露。
- **SC-016(源自 NFR-AVAIL-04 流程终止容错)**: terminate2Close 单条失败时返回异常消息但不中断批量操作,保证部分成功。
- **SC-017(源自 NFR-VER-01 版本正则)**: 软件版本必须符合 10 段顺序结构正则(另含可选 BinExt 扩展名捕获组,见 AMB-002-05);不匹配时按兜底策略处理。
- **SC-018(源自 NFR-VER-02 字典序)**: 版本范围匹配使用字符串 BETWEEN,数字部分补零至 3 位保证字典序与数值序一致。
- **SC-019(源自 NFR-VER-03 范围继承)**: 当固定前缀与范围两端缺省同时存在时,固定前缀优先继承。
- **SC-020(源自 NFR-VER-04 解析路径拆分)**: [已澄清 AMB-002-03] 版本解析拆分为两条独立路径——(1) 手工录入版本解析走 VersionParserFactory(Legacy/New 策略,按 getPattern().matcher(input).find() 选择,对应 LegacyVersionUtil.PATTERN / SoftNewVersionUtil.PATTERN);(2) 设备日志解析走 DeviceLogParserFacade 独立组件。二者不可混淆。
- **SC-021(源自 NFR-INT-01 外部同步)**: 售前项目数据来源于 SMS(借货)、OA(借货明细)、SAP(发货核销),通过定时任务同步;同步表包括借转销/核销/借货明细/发货核销四张外部数据表。
- **SC-022(源自 NFR-INT-02 基础数据)**: 状态/类型/办事处等依赖基础数据表(dataTypeCode: 27=售前项目状态、presalesType=售前类型、30=跟踪、31=状态、32=优先级、33=修复状态、34=选项卡、relatedSceneType/mitigationActionType/solutionActionType/sofeVersionAffectedType、29=交付件类型)与部门表、用户信息表。
- **SC-023(源自 NFR-INT-03 流程引擎)**: 售前流程依赖流程引擎运行时任务与历史任务表,通过 instId 关联;时长统计依赖历史任务表的 DURATION 字段(serviceApprove 节点见 AMB-002-01 暂定决策)。

---

## Assumptions

> 基于逆向反推过程中采用的合理默认假设。

- **流程引擎可用**: 售前审批流转依赖外部流程引擎(Activiti),假设流程引擎服务持续可用且 act_ru_task/act_hi_taskinst 数据可访问。
- **外部数据源同步稳定**: SMS/OA/SAP 数据通过定时任务同步,假设外部系统接口稳定且同步任务按预期调度执行。
- **基础数据已预置**: 状态/类型/办事处等基础数据(dataTypeCode 27-34 等)已在基础数据表中预置完整,业务表单下拉选项可动态加载。
- **文件服务可用**: 附件上传/删除依赖通用文件服务(fnd_files),假设文件服务持续可用。
- **软删除为唯一删除方式**: 售前项目主表与技术公告主表均采用 effectiveTo 软删除,假设不存在物理删除路径。
- **版本解析规则稳定**: 10 段顺序结构正则为版本解析主策略,NewSoftVersionStrategy 为兜底策略,假设版本格式约定不变。
- **位掩码 64 位约定**: 关联场景/操作类型位掩码按 64 位遍历反推,假设成员数量不超过 64。
- **办事处权限(areapower)已初始化**: 用户所属办事处权限已在会话/上下文中初始化,数据权限过滤可直接注入。
- **导出为离线操作**: 售前项目与技术公告导出生成 Excel,假设导出为同步离线操作,不要求异步任务。

---

## 附录:角色常量映射表(实现细节,供数据迁移参考)

> [已澄清 AMB-002-13] 角色编码属实现细节(I 级),spec 主体技术栈无关故不记录数值;此附录供数据迁移与权限初始化参考。注意角色编码非连续(存在跳号),迁移时需精确映射。证据:MessageUtil.java:201-269。

| 常量名 | 数值 | 角色语义 |
|---|---|---|
| ROLE_ADMIN | 1 | 管理员 |
| ROLE_PROJECT_VIEWER | 6 | 项目查看者 |
| ROLE_ENGINEEMANAGER_LEADER | 10 | 工程管理部主管 |
| ROLE_SERVICEMANAGER | 11 | 服务经理 |
| ROLE_ENGINEEMANAGER | 13 | 工程管理部 |
| ROLE_PRESALES_STAFF | 17 | 售前专员 |
| ROLE_PROB_ADMIN | 18 | 技术公告管理员 |
| ROLE_PROB_SUPPORTER | 19 | 技术支持 |
| ROLE_PROB_RD | 20 | 研发 |
| ROLE_COMPONENT_ADMIN | 22 | 组件管理员 |
