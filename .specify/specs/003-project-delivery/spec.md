# Feature Specification: 003-project-delivery(项目交付)

**Feature Branch**: `003-project-delivery`

**Created**: 2026-07-09

**Status**: Draft(已澄清,基于 clarify.md 正向固化)

**Source**: 逆向反推自 PMS-struts project 代码

> 本文档为 spec.md 经歧义澄清(ambiguities.md → clarify.md)后的更新版本。所有原待澄清标记已替换为决策结论或 [暂定决策]。

## User Scenarios & Testing

> 域职责:项目全生命周期——创建/成员/合同/发货/周报/通知/验货/版本/文件。
> 角色体系:工程管理部、管理员、财务、项目管理员(全量权限);服务经理、项目经理、普通用户、项目查阅(按权限受限)。

### User Story 1 - 项目基本信息与生命周期管理 (Priority: P1)

作为工程管理部/管理员,我希望基于合同号创建项目并贯穿其完整生命周期:查询项目列表、创建项目(含串货项目)、按角色分权修改项目、编辑工程计划节点、回退项目状态、批量导入/清理项目、转移设备;系统校验合同唯一性、生成项目编码、写入主表/合同/分组/成员并发送立项通知。

**Why this priority**: 项目创建与生命周期管理是项目交付域的核心入口,没有项目主数据则其他子域(成员、合同、周报、验货等)均无法运作,是 MVP 的必要组成部分。

**Independent Test**: 可通过"基于合同号创建一个项目并查询列表出现该项目"独立验证,交付项目主数据与立项通知能力。

**Acceptance Scenarios**:

1. **Given** 工程管理部用户已登录,**When** 基于未创建过项目的合同号提交创建项目(含项目分类/类别/实施方式/最终客户/公司/销售类型),**Then** 系统校验合同号唯一性通过,生成项目编码,写入主表/合同/分组/成员,发送立项通知邮件,返回项目主键。
2. **Given** 已存在合同号已创建项目,**When** 再次以该合同号创建项目,**Then** 系统拒绝并提示"该合同号已创建项目"。
3. **Given** 财务角色用户进入项目列表且未指定状态,**When** 查询项目列表,**Then** 默认返回已闭环项目;状态为"已创建(30)"时自动扩展为 30/31/32 三种状态查询(21 创建中不参与扩展)。
4. **Given** 服务经理/项目经理/普通用户/项目查阅角色,**When** 查询项目列表,**Then** 仅返回其有权限的项目。
5. **Given** 工程管理部用户在项目维护页提交修改,**When** 当前项目处于已闭环或不予跟踪且操作者为服务/项目经理,**Then** 仅允许更新渠道与实施方式;工程管理部则可全字段更新。
6. **Given** 有权限用户回退项目状态,**When** 提交目标状态与回退说明,**Then** 系统记录回退状态与说明,项目回到上一步。
7. **Given** 管理员上传 Excel 批量清理项目,**When** 选择物理删除或失效模式,**Then** 系统按模式批量处理对应项目;非管理员/工程管理部被拒绝。
8. **Given** 用户发起设备转移,**When** 选择目标项目与序列号并执行转移,**Then** 系统转移设备、维护转移合同关系,并更新两个项目的最后刷新时间;总代借货(salesType=14)按利润中心过滤并按借货特殊逻辑处理合同号后缀。

---

### User Story 2 - 项目成员管理 (Priority: P1)

作为项目相关方,我希望为项目添加成员(角色/姓名/电话/邮箱/生效时间)、结束成员任期(失效)、批量变更服务/项目经理(联动终止其手中的闭环与回访流程)、为设备批量保存安装地址、更新项目实施状态;成员变更触发动态/固定通知。

**Why this priority**: 成员(服务经理/项目经理/销售)是项目状态流转的驱动者(指定服务经理→指定项目经理),且批量变更需联动工作流,是项目得以推进的必要能力。

**Independent Test**: 可通过"为新建项目添加一名项目经理并触发通知"独立验证,交付成员管理与通知能力。

**Acceptance Scenarios**:

1. **Given** 用户在成员管理页,**When** 添加成员(角色/用户名/姓名/电话/邮箱/生效时间),**Then** 系统去除电话空白、记录创建人与时间、插入成员记录、更新项目最后刷新时间、触发动态通知(带角色名称),返回成员 ID。
2. **Given** 用户结束某成员任期,**When** 设置生效止时间,**Then** 系统更新成员失效时间、更新项目最后刷新时间、触发固定通知。
3. **Given** 工程管理部/管理员批量变更,**When** 指定部门、旧成员、新成员及变更类型(服务/项目/两者),**Then** 系统查询该部门下相关项目,逐项目变更成员,变更项目经理时终止其闭环申请与回访流程(回访状态置为驳回),返回服务/项目经理变更计数。
4. **Given** 用户为选中设备保存安装地址,**When** 提交序列号列表与安装地址,**Then** 系统从发货视图查询设备、批量插入发货安装记录、更新项目最后刷新时间、触发固定通知;总代借货按利润中心过滤。
5. **Given** 用户更新项目实施状态,**When** 提交项目 ID 与实施状态,**Then** 系统更新项目实施状态,返回成功标识。

---

### User Story 3 - 合同与订单管理 (Priority: P1)

作为项目相关方,我希望合并多个合同到当前项目(预检→合并)、基于当前项目拆分出新项目、查询关联合同的订单数据汇总/明细(含 RMA 退货)、查询实际发货清单、查询租赁配置与产品配置关系。

**Why this priority**: 合同与订单是项目交付物的来源,合并/拆分直接影响项目范围与产品清单,是交付执行的必要前置。

**Independent Test**: 可通过"合并一个合同到当前项目并验证产品清单与计划任务被复制"独立验证,交付合同合并能力。

**Acceptance Scenarios**:

1. **Given** 用户输入合并合同号,**When** 执行合同合并预检,**Then** 系统按合同号查询项目合同数量;若仅 1 条返回无需合并标识,否则返回可合并合同列表。
2. **Given** 用户选择若干合同执行合并,**When** 未选择任何合同,**Then** 系统提示"请至少选择一条合同数据";**When** 已选择合同,**Then** 系统插入合同关联、产品清单(来自订单行)、合并项目计划,并在 pm_project_group_relationship.mergeBranchMark 写入 "MERGE"。
3. **Given** 用户执行项目拆分,**When** 提交新项目编码与产品拆分数量及合并/拆分标记,**Then** 系统复制项目主表(新编码)、复制成员、按拆分数量批量插入产品清单、复制分组关系(mergeBranchMark 写入 "BRANCH"),重定向到新项目维护页。
4. **Given** 用户查询订单数据,**When** 查看项目关联合同订单,**Then** 系统返回订单数据汇总、RMA 退货数据与明细列表。
5. **Given** 用户查询实际发货清单,**When** 查看项目实际发货,**Then** 系统返回序列号维度的实际发货清单及数量。
6. **Given** 用户查询租赁配置/配置关系,**When** 按项目编码查询,**Then** 系统返回租赁配置清单与产品配置关系。

---

### User Story 4 - 发货与设备管理 (Priority: P2)

作为项目相关方,我希望按合同号查询发货设备序列号清单(总代借货按利润中心过滤、排除已转出设备)、按序列号删除发货安装信息、转移设备并按总代借货特殊逻辑处理。

**Why this priority**: 发货设备是验货、安装、版本管理的数据基础,但依赖项目与合同主数据先就绪,优先级次于核心主数据。

**Independent Test**: 可通过"按合同号查询发货序列号清单并验证已转出设备被排除"独立验证,交付发货查询能力。

**Acceptance Scenarios**:

1. **Given** 用户查看发货序列号,**When** 按合同号查询,**Then** 系统先查询历史发货数量,再查询项目简化信息;总代借货(salesType=14)按利润中心过滤,否则按合同号查询并排除已转出设备(转移标记≠0,即 transferFlag=0 未转移的设备保留),返回发货信息列表。
2. **Given** 用户删除发货安装信息,**When** 提交序列号列表,**Then** 系统按合同号+序列号关联删除发货安装记录,返回成功标识。
3. **Given** 用户转移设备,**When** 选择目标项目与序列号并执行转移,**Then** 系统按转移标记(转销/退货)处理,总代借货按借货特殊逻辑处理合同号后缀,插入转移记录与转移合同关系,更新双方项目刷新时间。

---

### User Story 5 - 项目周报管理 (Priority: P2)

作为周报创建人,我希望创建周报时按周计算起止时间并继承上期内容(任务偏差/备注/各项工作内容)、保存草稿、提交周报(生成 Excel 附件并向团队/抄送人/服务经理发送邮件并写入通知)、编辑周报按类型分组查询内容、对接收周报进行回复反馈。

**Why this priority**: 周报是项目过程跟踪与沟通机制,重要但可在核心交付流程就绪后启用。

**Independent Test**: 可通过"创建一份周报并提交,验证邮件发送与通知写入"独立验证,交付周报能力。

**Acceptance Scenarios**:

1. **Given** 周报创建人创建周报,**When** 选择项目并新建周报,**Then** 系统按当前日期回退到周一 0:00:00 作为开始、加 6 天到周日 23:59:59 作为结束,查询项目计划任务,查询上期周报并继承任务偏差、备注及各项工作/风险/计划/帮助/进展/抄送内容。
2. **Given** 周报创建人保存草稿,**When** 提交周报内容并选择保存草稿,**Then** 系统以草稿状态(0)保存,不发送邮件,更新项目最后刷新时间,返回周报 ID。
3. **Given** 周报创建人提交周报,**When** 提交周报内容,**Then** 系统以已提交状态(1)保存,生成周报 Excel 附件,收集抄送邮箱(抄送人+当前用户+系统配置抄送邮箱+项目成员邮箱)按模板发送邮件,触发固定通知,更新项目最后刷新时间。
4. **Given** 周报创建人编辑周报,**When** 查看周报,**Then** 系统返回周报主信息及按类型分组(工作/风险/帮助/进展/计划/附件/抄送)的内容列表;已提交周报额外返回回复列表。
5. **Given** 周报接收人回复周报,**When** 提交回复内容,**Then** 系统插入回复记录(周报 ID/内容/当前用户/当前时间),返回成功标识。

---

### User Story 6 - 通知与批示 (Priority: P2)

作为项目相关方,我希望查询项目系统通知列表、保存项目批示(可针对已有批示反馈)、查询批示列表并拼接为文本、查询项目关联的 ITR 问题工单(按项目编号优先、合同号回退)、按合同号/项目编号查询 License 授权信息。

**Why this priority**: 通知与批示是项目协同沟通渠道,依赖项目主数据,优先级中等。

**Independent Test**: 可通过"保存一条项目批示并查询拼接文本"独立验证,交付批示能力。

**Acceptance Scenarios**:

1. **Given** 用户查询项目通知,**When** 选择项目,**Then** 系统返回通知及对应通知对象的状态列表。
2. **Given** 有权限用户保存批示,**When** 提交批示内容(可指定原批示 ID 表示反馈,前端参数 instructionId 映射到数据库字段 instructionsId),**Then** 系统插入批示记录(批示类型=0;反馈时类型=1 并关联原批示 id),返回成功标识。
3. **Given** 用户查询批示,**When** 查看项目批示,**Then** 系统返回批示列表(类型=0)并按换行拼接为文本。
4. **Given** 用户查询问题工单,**When** 查看项目工单,**Then** 系统取项目编码(按 "-" 分割取首段去后缀)优先按项目编号查询 ITR 工单,查不到时按合同号回退查询,加载 ITR 基础地址(系统配置),返回工单列表与基础地址。
5. **Given** 用户查询 License 授权,**When** 查看项目 License,**Then** 系统构造合同号列表(合同号+项目编码)查询 License 授权信息,返回授权列表。

---

### User Story 7 - 文件管理 (Priority: P2)

作为项目相关方,我希望上传项目文件(周报附件/交付件,按白名单校验扩展名并自动重命名落盘)、按路径下载附件(支持编码回退)、按 ID 删除周报附件、按 ID 软删除(失效)交付件。

**Why this priority**: 文件是周报与交付件的载体,支撑周报与验收闭环,优先级中等。

**Independent Test**: 可通过"上传一个周报附件并下载验证内容一致"独立验证,交付文件上传下载能力。

**Acceptance Scenarios**:

1. **Given** 用户上传文件,**When** 选择文件并上传(周报附件或交付件),**Then** 系统创建上传目录,加载扩展名白名单逐文件校验,重命名并落盘;周报附件构造内容记录(原名/路径)入库,交付件构造交付件记录入库;非法扩展名被拒绝。
2. **Given** 用户下载文件,**When** 提交路径与文件名,**Then** 系统按上传前缀补全路径查找,失败时按编码回退解码路径重试,返回文件流。
3. **Given** 用户删除周报附件,**When** 提交附件 ID,**Then** 系统删除该附件(物理删除,属临时附件豁免软删除),返回成功/失败标识。
4. **Given** 用户删除交付件,**When** 提交交付件 ID,**Then** 系统软删除(设置失效时间),返回成功/失败标识。

---

### User Story 8 - 现场验货 (Priority: P3)

作为项目相关方,我希望按项目导出现场验货单(聚合发货设备并忽略指定物料)、导出设备过保提醒函;作为管理员/工程管理部,我希望导入现场验货忽略物料列表(覆盖式);作为系统,定时统计各办事处验货节点超期情况并发送汇总邮件。

**Why this priority**: 验货单导出与超期提醒属运营辅助能力,依赖发货与计划数据,优先级较低。

**Independent Test**: 可通过"导出一个项目的现场验货单并验证忽略物料被排除"独立验证,交付验货单导出能力。

**Acceptance Scenarios**:

1. **Given** 用户导出现场验货单,**When** 选择项目,**Then** 系统从发货视图按合同号+物料编码聚合设备(排除已转出设备,左关联忽略物料表排除指定物料),基于模板生成文件流式下载。
2. **Given** 用户导出过保提醒函,**When** 选择项目,**Then** 系统查询过保提醒清单并基于模板生成提醒函。
3. **Given** 管理员/工程管理部导入验货忽略项,**When** 上传 Excel(物料编码/型号/名称),**Then** 系统校验角色,清空忽略项表后批量插入(配置类豁免软删除),返回导入条数;非授权角色被拒绝。
4. **Given** 定时任务触发(每日/每周),**When** 系统扫描所有需验货项目,**Then** 系统按办事处分组计算到货验收/安装/初验/终验四节点超期情况(按阈值),向办事处领导/服务经理/项目经理/销售/验收小组发送汇总邮件;全国汇总邮件延迟 30 分钟发送;到货验收超期项目单独发送邮件。

---

### User Story 9 - 软件版本更新 (Priority: P3)

作为项目相关方,我希望按合同号查询发货设备软件版本(conp/cpld/boot/pcb)及受影响技术公告、批量更新设备软件版本(失效旧版本/插入新版本/写变更日志)、查询版本变更历史与出厂版本(V0)。

**Why this priority**: 软件版本管理属设备运维辅助能力,依赖发货设备数据,优先级较低。

**Independent Test**: 可通过"查询一个合同号的设备软件版本并验证版本字段返回"独立验证,交付版本查询能力。

**Acceptance Scenarios**:

1. **Given** 用户查询设备软件版本,**When** 按合同号查询(可选查询受影响技术公告),**Then** 系统查询项目简化信息,总代借货按利润中心过滤,返回设备软件版本(conp/cpld/boot/pcb 及备份/变更标记);启用受影响公告查询时关联返回技术公告。
2. **Given** 用户更新软件版本,**When** 提交版本 JSON(含版本列表与变更日志),**Then** 系统失效旧版本、批量插入新版本(customInfo 整条覆盖)、失效旧日志(latest=0)、插入新日志(latest=1),返回成功标识。
3. **Given** 用户查询版本变更历史,**When** 查看项目变更历史,**Then** 系统返回变更日志列表(按时间倒序,追加出厂版本 V0);指定日志 ID 时返回该次变更的版本列表与日志详情。

---

### Edge Cases

- **合同号已创建项目**:创建/串货创建时合同号已存在 → 拒绝并提示,不写入任何数据。
- **合同合并预检仅 1 条记录**:合并合同号对应项目合同数量为 1 → 返回无需合并标识(404),不进入合并流程。
- **合并未选择合同**:执行合并时未选择任何合同 → 提示"请至少选择一条合同数据"。
- **项目修改无权限**:非授权角色访问项目修改 → 返回"没有权限访问!"。
- **闭环条件不满足**:必传交付件未齐全 / 最终客户或渠道缺失 / 安装数量(= pm_project_shipment 中 installAddress 非空的记录数)≠发货数量 / 存在正在回访流程 → 不可发起闭环申请(isToCloseProject≠1)。
- **总代借货(salesType=14)特殊逻辑**:发货查询、安装地址保存、设备转移、软件版本查询均按利润中心(column001 在 salesType=14 时复用为 profitCenter)过滤;设备转移时合同号加 "-C" 后缀处理。
- **周报上期继承无上期**:新建周报时无上期周报 → 不继承,使用空值初始化。
- **文件下载路径解码回退**:按上传前缀补全路径查找失败 → 按 ISO8859-1→UTF-8 解码路径重试;流为空时打印警告。
- **文件上传非法扩展名**:扩展名不在系统配置白名单 → 拒绝上传并返回错误。
- **设备转移已转出设备**:查询可转移设备时排除已转出设备(转移标记≠0,即 transferFlag=1 转销或 2 退货);历史发货数量查询包含转销/退货设备。
- **问题工单查询回退**:按项目编号(去 "-" 后缀取首段)查询 ITR 工单无结果 → 按合同号列表回退查询。
- **项目状态自动扩展**:列表查询状态为"已创建(30)" → 自动扩展为 30/31/32 三种状态(21 创建中不参与扩展)。
- **pm_project 与 pm_project_header 命名**:pm_project 为 pm_project_header 的同义词/视图,语义等价;实现层统一使用 pm_project_header 表名(见 Assumptions)。
- **验货忽略项覆盖式导入**:导入忽略项采用先清空后批量插入模式,无主键与生效时间字段,属配置类豁免软删除,历史不可追溯。
- **批量变更项目经理联动**:批量变更项目经理时必须终止其手中所有闭环申请与回访流程,回访状态置为驳回,避免流程悬挂。
- **projectCode 后缀规则**:projectCode 可含 "-" 后缀,后缀标识派生关系;-C 表示总代借货转移派生(chContractNo 同源),-B 表示项目拆分派生;去后缀逻辑为按 "-" 分割取首段。

## Requirements

### Functional Requirements

> 子域组织:项目基本信息 / 项目成员 / 合同管理 / 发货与设备 / 项目周报 / 通知批示 / 现场验货 / 软件版本更新 / 文件管理。

#### 项目基本信息

- **FR-PROJ-01 项目列表查询**:系统 MUST 按角色分流提供项目列表查询——工程管理部/管理员/财务/项目管理员查询全部,服务经理/项目经理/普通用户/项目查阅按权限查询;状态为"已创建(30)"时自动扩展为 30/31/32(21 创建中不参与扩展);财务角色且状态为空时默认查询已闭环项目;支持按状态、办事处、人员、时间类型、起止时间、项目编号/名称/合同号、序列号筛选与分页排序。

- **FR-PROJ-02 项目创建**:系统 MUST 支持工程管理部/管理员基于合同号创建项目——参数为空时按合同号查询 SAP 同步订单数据生成项目编码并返回创建页(此时项目状态为 21 创建中,提交后转为 30 已创建);参数非空时校验合同号唯一性(已存在则报错),写入项目主表/合同/分组关系/成员(服务经理/项目经理/销售),按系统参数映射重大项目级别到项目类别,发送立项通知邮件(普通类/工程类模板),返回项目主键(Base64 编码)。

- **FR-PROJ-03 串货项目创建**:系统 MUST 支持工程管理人员手动创建串货(CH)项目,流程与普通创建一致但合同号来源不依赖 SAP 同步;合同号已存在时返回错误。

- **FR-PROJ-04 项目修改(分权)**:系统 MUST 按角色与项目状态分权更新项目——非管理员/工程管理部角色在单项目会话期内权限判断仅执行一次并缓存;查看模式查询项目全貌(状态/周报/财务验收计划/产品/工程计划/事件节点/交付件/批示/成员/回访流程)并计算闭环条件;工程管理部未指定服务经理时视为撤销立项,设置 effectiveTo=NOW 失效项目,否则全字段更新;已闭环或不予跟踪项目且为服务/项目经理时仅更新渠道与实施方式(column012Readonly=-1 时可改,可选值 0/1/3/4,2 已废弃不可选);服务经理在状态 30/32/34 时指定项目经理;项目经理在状态 30/32/34 时更新渠道与实施方式;无权限返回错误,状态不满足返回提示。

- **FR-PROJ-05 工程计划编辑**:系统 MUST 支持编辑项目工程计划事件节点(初验/终验等到货验收时间)——失效旧任务记录(失效时间置为当前),插入新记录,异常返回输入页。

- **FR-PROJ-06 项目状态回退**:系统 MUST 支持有权限用户回退项目状态——更新回退状态字段与回退说明字段,返回 JSON 结果。

- **FR-PROJ-07 项目清理(批量失效/删除)**:系统 MUST 支持管理员/工程管理部上传 Excel 批量清理项目——角色校验通过后按模式(物理删除/失效)批量处理(清理类批量操作豁免软删除原则,允许物理删除);非授权角色返回授权错误。

- **FR-PROJ-08 项目批量导入**:系统 MUST 支持管理员上传 Excel 批量创建项目——按批次函数(直接闭环/指定服务经理/指定项目经理+服务经理)解析对应 Sheet,跳过已存在合同号,批量保存;非管理员返回失败标识。

- **FR-PROJ-09 设备转移**:系统 MUST 支持设备序列号转移——按项目编码查询可转移项目列表,按合同号查询可转移设备(排除已转出 transferFlag≠0,总代借货按利润中心 column001 过滤),执行转移(总代借货按借货特殊逻辑处理合同号后缀 -C),插入转移合同关系,更新双方项目最后刷新时间。

#### 项目成员

- **FR-MEMBER-01 添加项目成员**:系统 MUST 支持为项目添加成员(角色/用户名/姓名/电话/邮箱/生效时间)——电话去除空白,默认项目类型为售后项目,记录创建人与时间,插入成员记录,更新项目最后刷新时间(projectRefreshTime),触发动态通知(带角色名称)。

- **FR-MEMBER-02 更新成员(失效)**:系统 MUST 支持结束成员任期——更新成员失效时间,更新项目最后刷新时间(projectRefreshTime),触发固定通知。

- **FR-MEMBER-03 批量变更服务/项目经理**:系统 MUST 支持工程管理部/管理员按部门批量变更——查询该部门下状态 30/31/32 且指定旧成员的项目(状态 34 项目不参与批量变更,因已进入渠道填写阶段,批量变更可能影响已推进项目);变更服务经理时逐项目更新、记录闭环流程任务、重指派工作流任务;变更项目经理时逐项目更新、终止其闭环申请与回访流程(回访状态置驳回);返回服务/项目经理变更计数。

- **FR-MEMBER-04 保存安装地址**:系统 MUST 支持为选中设备批量保存安装地址——查询项目获取合同号与销售类型,总代借货(salesType=14)按利润中心(column001 复用为 profitCenter)过滤,从发货视图查询设备批量插入发货安装记录,更新项目最后刷新时间(projectRefreshTime),触发固定通知。

- **FR-MEMBER-05 更新项目实施状态**:系统 MUST 支持更新项目实施状态——项目 ID 与实施状态非空时更新项目状态表的实施状态字段。

#### 合同管理

- **FR-CONTRACT-01 合同合并预检**:系统 MUST 支持输入合并合同号查询可合并合同——按合同号查询项目合同数量,数量为 1 时返回无需合并标识,否则返回合同列表。

- **FR-CONTRACT-02 合同合并**:系统 MUST 支持把选中合同合并到当前项目——未选择合同时提示"请至少选择一条合同数据";对每个选中合同插入合同关联、产品清单(来自订单行)、合并项目计划,并在 pm_project_group_relationship.mergeBranchMark 写入 "MERGE";重定向到项目维护页。

- **FR-CONTRACT-03 项目拆分**:系统 MUST 支持基于当前项目拆分出新项目——复制项目主表(新项目编码)、复制成员、按拆分数量批量插入产品清单、复制分组关系(mergeBranchMark 写入 "BRANCH"),重定向到新项目维护页。

- **FR-CONTRACT-04 查询订单数据**:系统 MUST 支持查询项目关联合同的订单数据汇总与明细(含 RMA 退货)。

- **FR-CONTRACT-05 查询实际发货清单**:系统 MUST 支持查询项目实际发货清单(序列号维度)及数量。

- **FR-CONTRACT-06 查询租赁配置/配置关系**:系统 MUST 支持按项目编码查询租赁配置清单与产品配置关系。

#### 发货与设备

- **FR-SHIP-01 查询发货序列号**:系统 MUST 支持按合同号查询发货设备序列号清单——先查询历史发货数量,再查询项目简化信息;总代借货(salesType=14)按利润中心(column001 复用为 profitCenter)过滤,否则按合同号查询并排除已转出设备(转移标记≠0,即 transferFlag=0 未转移的保留)。

- **FR-SHIP-02 删除发货安装信息**:系统 MUST 支持按序列号列表删除发货安装信息(按合同号+序列号关联删除)。

- **FR-SHIP-03 设备转移**:详见 FR-PROJ-09。

#### 项目周报

- **FR-WEEKLY-01 周报起止时间计算**:系统 MUST 按周计算周报起止时间——取当前日期回退到周一 0:00:00 作为开始,加 6 天到周日 23:59:59 作为结束。

- **FR-WEEKLY-02 创建周报(继承上期)**:系统 MUST 支持创建周报时继承上期内容——设置本周起止时间,查询项目计划任务列表,查询上期周报并继承任务偏差、备注及按类型分组的各项工作/风险/计划/帮助/进展/抄送内容。

- **FR-WEEKLY-03 保存周报(草稿/提交)**:系统 MUST 支持保存草稿(状态=0,不发邮件)与提交(状态=1)——提交时生成周报 Excel 附件,收集抄送邮箱(抄送人+当前用户+系统配置抄送邮箱+项目成员邮箱)按模板发送邮件,触发固定通知,更新项目最后刷新时间(projectRefreshTime)。

- **FR-WEEKLY-04 编辑周报**:系统 MUST 支持查看周报并按类型分组查询各项工作/风险/帮助/进展/计划/附件/抄送内容——已提交周报额外查询回复列表,更新项目最后刷新时间(projectRefreshTime)。

- **FR-WEEKLY-05 周报回复**:系统 MUST 支持接收人对已提交周报回复反馈——插入回复记录(周报 ID/内容/当前用户/当前时间)。

#### 通知批示

- **FR-NOTIFY-01 查询项目通知**:系统 MUST 支持查询项目系统通知列表(关联通知对象状态)。

- **FR-NOTIFY-02 保存批示**:系统 MUST 支持有权限用户保存项目批示(可针对已有批示反馈)——插入批示记录(批示类型=0;反馈时类型=1 并关联原批示 id);前端参数 instructionId 映射到数据库字段 instructionsId。

- **FR-NOTIFY-03 查询批示**:系统 MUST 支持查询项目批示列表(类型=0)并按换行拼接为文本。

- **FR-NOTIFY-04 问题工单查询**:系统 MUST 支持查询项目关联 ITR 工单——取项目编码(按 "-" 分割取首段去后缀)优先按项目编号查询,查不到时按合同号回退查询,加载 ITR 基础地址(系统配置)。

- **FR-NOTIFY-05 License 授权查询**:系统 MUST 支持按合同号/项目编号查询项目 License 授权信息——构造合同号列表(合同号+项目编码)查询。

#### 现场验货

- **FR-INSPECT-01 现场验货单导出**:系统 MUST 支持按项目导出现场验货单——从发货视图按合同号+物料编码聚合设备(排除已转出设备 transferFlag≠0,左关联忽略物料表排除指定物料),基于模板生成文件流式下载。

- **FR-INSPECT-02 过保提醒函导出**:系统 MUST 支持按项目导出设备过保提醒函——基于模板生成。

- **FR-INSPECT-03 验货忽略项导入**:系统 MUST 支持管理员/工程管理部导入验货忽略物料列表(覆盖式)——角色校验后清空忽略项表批量插入(配置类豁免软删除,允许 truncate);非授权角色返回授权错误。

- **FR-INSPECT-04 验货状态定时提醒**:系统 MUST 支持定时(每日/每周)扫描验货节点超期——查询所有需验货项目(到货验收/安装/初验/终验四节点的计划与实际时间),按办事处分组计算超期(按阈值:到货验收后 2 月、安装 5 月、初验 9 月、终验按初验后超期计算),向办事处领导/服务经理/项目经理/销售/验收小组发送汇总邮件;全国汇总邮件延迟 30 分钟发送;到货验收超期项目单独发送邮件。

#### 软件版本更新

- **FR-SOFT-01 查询设备软件版本**:系统 MUST 支持按合同号查询发货设备软件版本(conp/cpld/boot/pcb 及备份/变更标记)——总代借货(salesType=14)按利润中心(column001 复用为 profitCenter)过滤;可选查询受影响技术公告(关联聚合)。

- **FR-SOFT-02 更新软件版本**:系统 MUST 支持批量更新设备软件版本——失效旧版本(数据状态=0)、批量插入新版本(customInfo 整条覆盖,非增量合并)、失效旧日志(最新=0)、插入新日志(最新=1)。

- **FR-SOFT-03 查询版本变更历史**:系统 MUST 支持查询项目软件版本变更历史(按时间倒序,追加出厂版本 V0)——指定日志 ID 时返回该次变更的版本列表与日志详情。

#### 文件管理

- **FR-FILE-01 文件上传(周报附件/交付件)**:系统 MUST 支持上传项目文件——创建上传目录,加载扩展名白名单(系统配置)逐文件校验,重命名并落盘;周报附件构造内容记录(原名/路径)入库,交付件构造交付件记录入库;非法扩展名返回错误,目录创建失败提示。

- **FR-FILE-02 文件下载**:系统 MUST 支持按路径下载附件——先按上传前缀补全路径查找,失败时按编码回退(ISO8859-1→UTF-8)解码路径重试,返回文件流。

- **FR-FILE-03 文件删除**:系统 MUST 支持按 ID 删除周报附件(临时附件豁免软删除,物理删除),返回成功/失败标识。

- **FR-FILE-04 交付件删除**:系统 MUST 支持按 ID 软删除(设置失效时间)交付件,返回成功/失败标识。

### Key Entities

> 分级说明:C=契约字段(对外/跨域稳定接口);I=内部字段(实现细节);D=废弃/历史字段。

#### 数据契约

##### 3.1 pm_project_header(项目主表)

> 声明:pm_project 为 pm_project_header 的同义词/视图,语义等价;实现层统一使用 pm_project_header 表名,旧语句迁移至 pm_project_header。

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| projectId | int | 否 | 项目主键,自增 | 全局唯一 | C |
| projectCode | varchar | 是 | 项目编码(可含 "-" 后缀,后缀用于区分派生关系;-C=总代借货转移派生,-B=项目拆分派生;去后缀按 "-" 分割取首段) | 项目组内可重复,全串唯一 | C |
| smsProjectCode | varchar | 是 | SMS 系统项目编码 | 来自 SMS 同步 | C |
| projectName | varchar | 是 | 项目名称 | - | C |
| projectState | varchar | 是 | 项目状态码(21=创建中[中间态,提交后转 30],30=已创建,31=指定服务经理,32=指定项目经理,34=填写渠道,20=已闭环,100=不予跟踪) | 状态机驱动(21→30→31→32→34→20/100) | C |
| isback | varchar | 是 | 回退状态码(同 projectState 语义,用于回退场景) | 与 projectState 互补 | I |
| column001 | varchar | 是 | 办事处编码(officeCode);salesType=14(总代借货)时复用为利润中心(profitCenter) | 关联部门基础数据 | C |
| column002 | varchar | 是 | 客户编码(customerCode) | - | C |
| column003 | varchar | 是 | 客户名称(customerName) | - | C |
| column004 | varchar | 是 | 市场人员名称(marketName) | - | C |
| column005 | varchar | 是 | 系统部名称(systemName) | - | C |
| column006 | varchar | 是 | 拓展人员名称(expendName) | - | C |
| column007 | varchar | 是 | 行业名称(industryName) | - | C |
| column008 | varchar | 是 | 保留字段(服务经理可改) | - | I |
| column009 | date | 是 | 订单创建时间(orderCreateTime) | 来自 SAP | C |
| column010 | varchar | 是 | 项目类别(10=普通类,20=工程类) | 关联基础数据 | C |
| column011 | varchar | 是 | 项目分类(10=直签类,20=非直签类) | 关联基础数据 | C |
| column012 | varchar | 是 | 实施方式(0=原厂直服,1=代理商自服,2=废弃[原代理商直服,已合并],3=代理商集成,4=原厂集成) | 只读标记=-1 时可改,可选值 0/1/3/4 | C |
| columno12_readonly | int | 是 | 实施方式只读标记(-1=可改,其他=不可改,从 SMS 刷新) | - | I |
| column013 | varchar | 是 | 最终客户名称 | - | C |
| column014 | varchar | 是 | 不予跟踪/回退说明(backCause) | - | I |
| salesType | varchar | 是 | 销售类型(01=正常,02=借转销,14=销售类借货/总代借货) | 影响利润中心过滤 | C |
| customerProjectName | varchar | 是 | 客户项目名 | - | C |
| majorProjectLevel | varchar | 是 | 重大项目级别 | 映射到 column010 | C |
| compId | varchar | 是 | 公司主表ID | 关联公司基础数据 | C |
| customInfo | json | 是 | 自定义信息(服务经理/项目经理/项目经理B/销售编码等) | 增量合并更新(JSON_MERGE_PATCH) | C |
| projectRefreshTime | datetime | 是 | 项目最后刷新时间 | 默认 NOW(),成员变更/安装地址保存/周报保存提交等操作更新 | C |
| createTime | datetime | 否 | 创建时间 | 默认当前时间 | C |
| createBy | varchar | 否 | 创建人(用户名) | - | C |
| updateTime | datetime | 是 | 修改时间 | - | C |
| updateBy | varchar | 是 | 修改人 | - | C |
| effectiveFrom | datetime | 否 | 生效时间_起 | 默认当前时间 | C |
| effectiveTo | datetime | 是 | 生效时间_止(失效时设置) | 失效项目时设为当前时间 | C |

##### 3.2 pm_project_member(项目成员)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键,自增 | 全局唯一 | C |
| projectId | int | 否 | 项目ID | 关联 pm_project_header | C |
| projectType | varchar | 是 | 项目类型(默认售后) | - | I |
| memberRole | varchar | 否 | 成员角色(10=项目经理,20=服务经理,30=销售,40/70/71=团队成员等) | 关联基础数据 | C |
| memberCode | varchar | 否 | 成员用户名 | 关联用户信息 | C |
| memberName | varchar | 是 | 成员姓名 | - | C |
| phoneNum | varchar | 是 | 电话(去除空白) | - | C |
| email | varchar | 是 | 邮箱 | - | C |
| fromFlag | varchar | 是 | 来源标记(来自项目/来自成员) | - | I |
| createTime | datetime | 否 | 创建时间 | 默认当前时间 | C |
| createBy | varchar | 否 | 创建人 | - | C |
| effectiveFrom | datetime | 否 | 生效时间_起 | 默认当前时间 | C |
| effectiveTo | datetime | 是 | 生效时间_止 | 失效时设置 | C |

##### 3.3 pm_project_contract(项目合同)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键,自增 | 全局唯一 | C |
| projectId | int | 否 | 项目ID | 关联 pm_project_header | C |
| contractNo | varchar | 否 | 合同号 | 关联 SAP 订单 | C |
| projectGroupCode | varchar | 否 | 项目组编码 | 关联 pm_project_group | C |
| createTime | datetime | 否 | 创建时间 | 默认当前时间 | C |
| createBy | varchar | 否 | 创建人 | - | C |
| effectiveFrom | datetime | 否 | 生效时间_起 | 默认当前时间 | C |
| effectiveTo | datetime | 是 | 生效时间_止 | 失效时设置(软删除) | C |

> 决策结论(AMB-003-02):表含自增主键 id、projectId 字段及 effectiveFrom/effectiveTo 软删除字段,符合 NFR-CON-02 软删除模式。

##### 3.4 pm_project_group(项目组)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键,自增 | 全局唯一 | C |
| projectGroupCode | varchar | 否 | 项目组编码 | 全局唯一 | C |
| projectGroupName | varchar | 是 | 项目组名称 | - | C |
| projectType | varchar | 是 | 项目类型 | - | I |
| createTime | datetime | 否 | 创建时间 | - | C |
| createBy | varchar | 否 | 创建人 | - | C |

##### 3.5 pm_project_group_relationship(项目分组关系)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| projectGroupCode | varchar | 否 | 项目组编码 | 关联 pm_project_group | C |
| projectCode | varchar | 否 | 项目编码 | 关联 pm_project_header | C |
| smsProjectCode | varchar | 是 | SMS 项目编码 | - | C |
| mergeBranchMark | varchar | 是 | 合并/拆分标记(MERGE=合并,BRANCH=拆分) | FR-CONTRACT-02 写入 MERGE,FR-CONTRACT-03 写入 BRANCH | I |
| createTime | datetime | 否 | 创建时间 | 默认当前时间 | C |
| createBy | varchar | 否 | 创建人 | - | C |

##### 3.6 pm_project_related_party(项目相关方/渠道)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| projectId | int | 否 | 项目ID | 关联 pm_project_header | C |
| partyRole | varchar | 否 | 角色类型(交付渠道/服务渠道/代理商渠道/合作伙伴渠道) | - | C |
| partyCode | varchar | 是 | 渠道编码 | - | C |
| partyName | varchar | 是 | 渠道名称 | - | C |
| createTime | datetime | 否 | 创建时间 | 默认当前时间 | C |
| createBy | varchar | 否 | 创建人 | - | C |
| updateTime | datetime | 是 | 修改时间 | - | C |
| updateBy | varchar | 是 | 修改人 | - | C |
| effectiveFrom | datetime | 否 | 生效时间_起 | 默认当前时间 | C |
| effectiveTo | datetime | 是 | 生效时间_止 | 失效时设置 | C |

##### 3.7 pm_project_state(项目状态)

> 决策结论(AMB-003-21):每项目仅一条状态记录,projectId 为唯一逻辑键;projectPlanState 为汇总状态字段(取最新节点)。

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| projectId | int | 否 | 项目ID(唯一逻辑键) | 关联 pm_project_header,每项目仅一条 | C |
| projectPlanState | varchar | 是 | 工程计划状态(43=到货验收,44=安装,45=初验,46=终验,48=项目闭环) | 关联基础数据 | C |
| projectplanTime | datetime | 是 | 计划状态时间 | 默认当前时间 | I |
| shipmentState | int | 是 | 发货状态(-1=全到货,1=未发货,2=部分发货) | 由产品线计算 | C |
| shipmentTime | datetime | 是 | 发货状态时间 | - | I |
| executionState | varchar | 是 | 工程实施状态 | 关联基础数据 | C |
| executionStateTime | datetime | 是 | 实施状态时间 | - | I |
| closeProcessState | varchar | 是 | 闭环流程状态(15=闭环申请) | 关联基础数据 | C |
| closeProcessStateTime | datetime | 是 | 闭环流程状态时间 | - | I |

> 编码映射表(AMB-003-11):taskTypeId ↔ projectPlanState 映射关系:30↔43(到货验收)、50↔44(安装)、60↔45(初验)、61↔46(终验)。

##### 3.8 pm_project_task(项目任务/工程计划)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| taskId | int | 否 | 主键,自增 | 全局唯一 | C |
| projectId | int | 否 | 项目ID | 关联 pm_project_header | C |
| projectType | varchar | 是 | 项目类型 | - | I |
| contractNo | varchar | 是 | 合同号 | - | C |
| taskTypeCode | varchar | 否 | 任务类型编码 | 关联基础数据 | C |
| taskTypeId | varchar | 否 | 任务类型ID(30=到货验收,50=安装,60=初验,61=终验) | 关联基础数据,与 projectPlanState 映射见 3.7 | C |
| eventPlanHappenDate | date | 是 | 计划发生日期 | - | C |
| eventPlanHappenDateENG | date | 是 | 计划发生日期(ENG) | - | I |
| eventActualFinishDate | date | 是 | 实际完成日期 | - | C |
| visibleFlag | int | 是 | 可见标记 | - | I |
| createTime | datetime | 否 | 创建时间 | 默认当前时间 | C |
| createBy | varchar | 否 | 创建人 | - | C |
| updateTime | datetime | 是 | 修改时间 | - | C |
| updateBy | varchar | 是 | 修改人 | - | C |
| effectiveFrom | datetime | 否 | 生效时间_起 | 默认当前时间 | C |
| effectiveTo | datetime | 是 | 生效时间_止 | 失效时设置 | C |

##### 3.9 pm_project_product_line(项目产品线)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键,自增 | 全局唯一 | C |
| projectId | int | 否 | 项目ID | 关联 pm_project_header | C |
| contractNo | varchar | 是 | 合同号 | - | C |
| itemCode | varchar | 是 | 物料编码 | 关联物料基础数据 | C |
| itemName | varchar | 是 | 物料名称/描述 | - | C |
| projectQuantity | int | 是 | 项目数量(拆分时设置,否则=订单数量) | - | C |
| orderQuantity | int | 是 | 订单数量 | 来自 SAP | C |
| deliverQuantity | int | 是 | 已发货数量 | = 订单数量 - 未发货数量 | C |
| openQuantity | int | 是 | 未发货数量 | 来自 SAP | C |
| orderNumber | varchar | 是 | 订单号 | 关联 SAP 订单数据 | C |
| lineNum | varchar | 是 | 行号 | 关联 SAP 订单行 | C |

##### 3.10 pm_project_shipment(项目发货安装)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键,自增 | 全局唯一 | C |
| projectId | int | 否 | 项目ID | 关联 pm_project_header | C |
| barcode | varchar | 否 | 设备序列号 | 项目内唯一 | C |
| itemCode | varchar | 是 | 物料编码 | - | C |
| itemName | varchar | 是 | 物料名称 | - | C |
| receiveName | varchar | 是 | 收货人 | 来自 EMS 视图 | C |
| emsNum | varchar | 是 | EMS 单号 | - | C |
| emsCompany | varchar | 是 | EMS 公司 | - | C |
| packdate | date | 是 | 发货日期 | - | C |
| contractNo | varchar | 是 | 合同号 | - | C |
| installAddress | varchar | 是 | 安装地址(非空表示已安装,用于安装数量计数) | - | C |
| chProjectId | int | 是 | 串货项目ID(转移用) | - | I |
| chContractNo | varchar | 是 | 串货合同号(转移用,可能含 -C 后缀) | - | I |
| transferProjectId | int | 是 | 转入项目ID | - | I |
| transferContractNo | varchar | 是 | 转入合同号 | - | I |
| transferFlag | int | 是 | 转移标记(0=未转移[默认],1=已转出[转销],2=已转出[退货];排除已转出设备条件:转移标记≠0) | - | C |
| createTime | datetime | 否 | 创建时间 | 默认当前时间 | C |
| createBy | varchar | 否 | 创建人 | - | C |

##### 3.11 pm_project_weekly(项目周报)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| weeklyId | int | 否 | 主键,自增 | 全局唯一 | C |
| projectId | int | 否 | 项目ID | 关联 pm_project_header | C |
| currentTask | varchar | 是 | 当前任务 | - | C |
| taskStartTime | date | 是 | 任务开始时间 | - | C |
| taskEndTime | date | 是 | 任务结束时间 | - | C |
| taskDeviation | varchar | 是 | 任务偏差 | 继承上期 | C |
| remark | varchar | 是 | 备注 | 继承上期 | C |
| weeklyStartTime | datetime | 是 | 周报开始时间(周一 0:00:00) | 由周计算得出 | C |
| weeklyEndTime | datetime | 是 | 周报结束时间(周日 23:59:59) | 由周计算得出 | C |
| weeklyState | int | 否 | 周报状态(0=草稿,1=已提交) | - | C |
| createTime | datetime | 否 | 创建时间 | - | C |
| createBy | varchar | 否 | 创建人 | - | C |
| updateTime | datetime | 是 | 修改时间 | - | C |
| updateBy | varchar | 是 | 修改人 | - | C |

##### 3.12 pm_project_weekly_content(周报内容)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键,自增 | 全局唯一 | C |
| weeklyId | int | 否 | 周报ID | 关联 pm_project_weekly | C |
| optionDesc001 | varchar | 是 | 选项描述1(文件名/内容) | - | C |
| optionDesc002 | varchar | 是 | 选项描述2(文件路径/邮箱) | - | C |
| optionType | varchar | 否 | 选项类型(WORK=工作,RISK=风险,HELP=帮助,PROGRESS=进展,PLAN=计划,FILE=附件,MAIL=抄送) | - | C |
| createTime | datetime | 否 | 创建时间 | 默认当前时间 | C |
| createBy | varchar | 否 | 创建人 | - | C |
| effectiveFrom | datetime | 否 | 生效时间_起 | 默认当前时间 | C |
| effectiveTo | datetime | 是 | 生效时间_止 | - | I |

##### 3.13 pm_project_weekly_feedback(周报回复)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键,自增 | 全局唯一 | C |
| weeklyId | int | 否 | 周报ID | 关联 pm_project_weekly | C |
| feedback | varchar | 是 | 回复内容 | - | C |
| feedbacker | varchar | 是 | 回复人(用户名) | 关联用户信息 | C |
| feedbackTime | datetime | 否 | 回复时间 | 默认当前时间 | C |

##### 3.14 pm_project_instruction(项目批示)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键,自增 | 全局唯一 | C |
| projectId | int | 否 | 项目ID | 关联 pm_project_header | C |
| instructionsInfo | varchar | 是 | 批示内容 | - | C |
| instructionsTime | datetime | 是 | 批示时间 | - | C |
| instructionsUser | varchar | 是 | 批示人(用户名) | 关联用户信息 | C |
| dataType | int | 是 | 数据类型(0=批示,1=对批示的反馈) | - | C |
| instructionsId | int | 是 | 关联批示ID(反馈时指向原批示 id) | - | I |
| createTime | datetime | 否 | 创建时间 | - | C |
| createBy | varchar | 否 | 创建人 | - | C |

> 决策结论(AMB-003-03):表含自增主键 id,INSERT 语句未显式写入(数据库自增),instructionsId 引用该 id;FR-NOTIFY-02 前端参数 instructionId 映射到 instructionsId 字段。

##### 3.15 pm_project_notification(项目通知)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| notifyId | int | 否 | 主键,自增 | 全局唯一 | C |
| notifySubject | varchar | 是 | 通知主题 | - | C |
| notifyContent | varchar | 是 | 通知内容 | - | C |
| projectId | int | 否 | 项目ID | 关联 pm_project_header | C |
| createBy | varchar | 否 | 创建人 | - | C |
| createTime | datetime | 否 | 创建时间 | 默认当前时间 | C |

##### 3.16 pm_project_notification_state(通知状态/对象)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| notifyId | int | 否 | 通知ID | 关联 pm_project_notification | C |
| notifyObject | varchar | 否 | 通知对象(用户名) | 关联用户信息 | C |
| notifyState | int | 否 | 通知状态(0=未读,1=已读) | 默认 0 | C |
| createTime | datetime | 否 | 创建时间 | 默认当前时间 | C |
| createBy | varchar | 否 | 创建人 | - | C |

##### 3.17 pm_project_soft_version(项目软件版本)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | - | C |
| projectId | int | 否 | 项目ID | 关联 pm_project_header | C |
| logId | int | 否 | 变更日志ID | 关联 pm_project_soft_change_logs | C |
| barcode | varchar | 否 | 设备序列号 | - | C |
| conp | varchar | 是 | CONP 版本 | - | C |
| conpBak | varchar | 是 | CONP 备份版本 | - | C |
| conpChange | int | 是 | CONP 变更标记(0=未变,1=已变) | 默认 0 | C |
| cpld | varchar | 是 | CPLD 版本 | - | C |
| cpldBak | varchar | 是 | CPLD 备份版本 | - | C |
| cpldChange | int | 是 | CPLD 变更标记 | - | C |
| boot | varchar | 是 | BOOT 版本 | - | C |
| bootBak | varchar | 是 | BOOT 备份版本 | - | C |
| bootChange | int | 是 | BOOT 变更标记 | - | C |
| pcb | varchar | 是 | PCB 版本 | - | C |
| pcbBak | varchar | 是 | PCB 备份版本 | - | C |
| pcbChange | int | 是 | PCB 变更标记 | - | C |
| executeTime | datetime | 是 | 执行时间 | 默认当前时间 | C |
| datastate | int | 是 | 数据状态(0=失效,1=有效) | 默认 1 | C |
| contractNo | varchar | 是 | 合同号 | - | C |
| itemCode | varchar | 是 | 物料编码 | - | C |
| conpType | varchar | 是 | CONP 类型 | 关联软件基础数据 | C |
| conpSeries | varchar | 是 | CONP 系列 | 关联软件基础数据 | C |
| conpMark | varchar | 是 | CONP 标记(在起止标记范围内) | 关联软件基础数据 | C |
| customInfo | json | 是 | 自定义信息(设备级,每次更新整条覆盖,非增量合并) | 与 pm_project_header.customInfo 策略不同 | I |
| createTime | datetime | 否 | 创建时间 | 默认当前时间 | C |
| createBy | varchar | 否 | 创建人 | - | C |
| updateTime | datetime | 是 | 修改时间 | - | C |
| updateBy | varchar | 是 | 修改人 | - | C |

##### 3.18 pm_project_soft_change_logs(软件变更日志)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键,自增 | 全局唯一 | C |
| projectId | int | 否 | 项目ID | 关联 pm_project_header | C |
| changeVersion | varchar | 是 | 变更版本号 | - | C |
| changeRemark | varchar | 是 | 变更备注 | - | C |
| latest | int | 否 | 是否最新(0=否,1=是) | 项目内仅一条 latest=1 | C |
| createBy | varchar | 否 | 创建人 | - | C |
| createTime | datetime | 否 | 创建时间 | 默认当前时间 | C |
| updateTime | datetime | 是 | 修改时间 | - | C |
| updateBy | varchar | 是 | 修改人 | - | C |

##### 3.19 pm_project_spot_check_ignore_item(现场验货忽略项)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| itemCode | varchar | 否 | 物料编码 | - | C |
| itemModel | varchar | 是 | 物料型号 | - | C |
| itemName | varchar | 是 | 物料名称 | - | C |
| createTime | datetime | 是 | 创建时间(记录最近一次导入) | 默认当前时间 | I |
| createBy | varchar | 是 | 创建人(记录最近一次导入人) | - | I |

> 决策结论(AMB-003-04):表为全局配置表,无主键,truncate 覆盖式导入属配置类豁免软删除(NFR-CON-02 豁免清单);补加 createTime/createBy 记录最近一次导入信息。

##### 3.20 pm_basic_prj_deliver(交付件模板)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键,自增 | 全局唯一 | C |
| column010 | varchar | 是 | 项目类别(10/20) | - | C |
| column011 | varchar | 是 | 项目分类(10/20) | - | C |
| dataTypeCode | varchar | 否 | 事件类型编码 | 关联基础数据 | C |
| basicDataId | varchar | 否 | 事件基础数据ID | 关联基础数据 | C |
| dataTypeCodeSon | varchar | 否 | 交付件类型编码 | 关联基础数据 | C |
| basicDataIdSon | varchar | 否 | 交付件基础数据ID | 关联基础数据 | C |
| isNeed | int | 是 | 是否必传(0=否,1=是) | - | C |
| effectiveFrom | datetime | 否 | 生效时间_起 | - | C |
| effectiveTo | datetime | 是 | 生效时间_止 | 失效时设置 | C |

##### 3.21 pm_basic_deliver_detail(交付件明细/实际交付件)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键,自增 | 全局唯一 | C |
| projectId | int | 否 | 项目ID | 关联 pm_project_header | C |
| projectType | varchar | 是 | 项目类型 | - | I |
| taskId | int | 是 | 任务ID | - | I |
| contractNo | varchar | 是 | 合同号 | - | C |
| deliverId | int | 否 | 交付件模板ID | 关联 pm_basic_prj_deliver | C |
| deliverableName | varchar | 是 | 交付件文件名 | - | C |
| deliverablePath | varchar | 是 | 交付件文件路径 | - | C |
| deliverableType | varchar | 是 | 交付件类型 | - | I |
| uploadUser | varchar | 否 | 上传人(用户名) | 关联用户信息 | C |
| uploadTime | datetime | 否 | 上传时间 | 默认当前时间 | C |
| effectiveFrom | datetime | 否 | 生效时间_起 | 默认当前时间 | C |
| effectiveTo | datetime | 是 | 生效时间_止 | 软删除时设置 | C |

##### 3.22 pm_project_incident_table_from_itr(工单记录-来自 ITR)

> 外部同步表,字段未在源码中显式定义,SELECT * 查询。
> [暂定决策:字段待查询 GainDataFromITR 同步任务定义补全;本域实际消费的契约字段(分级 C)为 projectCode、contractNo、barCode 等,其余为外部内部字段。在补全前保持 SELECT * 但仅关键字段为稳定契约。]

##### 3.23 pm_project_license_info_from_license(License 授权-来自 License)

> 外部同步表,字段未在源码中显式定义,SELECT * 查询。
> [暂定决策:字段待查询 GainDataFromLicense 同步任务定义补全;本域实际消费的契约字段(分级 C)为 projectCode、contractNo 等,其余为外部内部字段。在补全前保持 SELECT * 但仅关键字段为稳定契约。]

##### 3.24 关联外部表(引用,非本域所有)

| 表名 | 用途 | 关联键 | 分级 |
|---|---|---|---|
| pm_order_data_from_sap | 订单数据(SAP 同步) | contractNo、orderNumber、compCode | C(外部契约) |
| pm_order_line_from_sap | 订单行(SAP 同步) | orderNumber、lineNum、lineType、compCode | C(外部契约) |
| pm_project_property_from_sms | 项目属性(SMS 同步) | systemName | C(外部契约) |
| pm_project_product_lease_line_from_crm | 租赁配置(CRM 同步) | projectCode | C(外部契约) |
| pm_project_product_config_level_info_from_crm | 配置关系(CRM 同步) | smsProjectCode | C(外部契约) |
| pm_person_from_oa | 人员(OA 同步) | username | C(外部契约) |
| fnd_user_info | 用户信息 | username | C(基础数据) |
| fnd_basic_data | 基础数据 | dataTypeCode、basicDataId | C(基础数据) |
| fnd_department | 部门 | departmentNum | C(基础数据) |
| fnd_company | 公司 | code、compId | C(基础数据) |
| fb_items | 物料 | item、itemname、describe_ | C(基础数据) |
| view_shipment_info_4_pm | 发货信息视图 | contract_code、barcode、packId | C(视图契约) |
| view_shipment_ems_4_pm | 发货 EMS 视图 | contract_code、packId | C(视图契约) |
| pm_notification_template | 通知模板 | templateCode | C(基础数据) |
| pm_cl_evaluation_header | 闭环评估头(引用) | projectId、projectCode | C(跨域引用) |
| pm_cl_callback | 闭环回访(引用) | projectId | C(跨域引用) |

## Success Criteria

> 由非功能需求转换为可测量标准。所有标准均可通过业务行为验证,不依赖具体技术实现。

### Measurable Outcomes

- **SC-001 角色分权**:100% 的项目列表与修改操作按角色分权控制——工程管理部/管理员/财务/项目管理员可访问全量,其余角色仅可访问授权项目,未授权访问 0 成功。
- **SC-002 权限缓存**:同一项目会话期内权限判断仅执行一次并缓存,重复访问不产生重复权限计算开销。
- **SC-003 防误操作确认标记**:所有项目操作需通过 validateFlag(MD5('success'))防误操作确认标记校验(语义为用户已确认 success,非安全防护),未通过校验的操作 0 发生。
- **SC-004 批量操作角色限制**:项目清理、批量导入、验货忽略项导入仅管理员/工程管理部可执行,非授权角色 100% 被拒绝。
- **SC-005 上传扩展名白名单**:文件上传 100% 校验扩展名白名单(系统配置),非法扩展名上传 0 成功。
- **SC-006 项目状态机**:项目状态迁移 100% 遵循状态机(21→30→31→32→34→20/100,其中 21 为创建中中间态),非法迁移被拒绝,回退通过回退字段记录。
- **SC-007 软删除模式**:关键业务数据(项目主表、成员、合同、交付件、相关方)采用失效时间软删除模式,历史数据可追溯;豁免清单:清理类批量操作(FR-PROJ-07 项目清理)、配置类(FR-INSPECT-03 忽略项)、临时附件(FR-FILE-03 周报附件)允许物理删除。
- **SC-008 闭环条件校验**:项目可发起闭环申请的前提 100% 满足四项——必传交付件齐全、最终客户与渠道维护、安装数量(= pm_project_shipment 中 installAddress 非空的记录数)=发货数量、无正在回访流程。
- **SC-009 合同唯一性**:重复合同号创建项目 100% 被拦截,不允许同合同号重复立项。
- **SC-010 软件版本最新标记**:每个项目软件变更日志仅保留一条最新标记(latest=1),更新前先失效旧记录。
- **SC-011 自定义信息增量合并**:项目自定义信息(pm_project_header.customInfo)更新采用增量合并(JSON_MERGE_PATCH),历史字段 0 丢失;pm_project_soft_version.customInfo 为设备级信息,每次更新整条覆盖(非增量合并)。
- **SC-012 邮件通知**:立项、周报提交、批量变更成员、验货超期等场景 100% 触发邮件通知,模板来自通知模板表。
- **SC-013 系统通知**:成员变更、安装地址保存、周报提交等 100% 写入系统通知(通知表+通知状态表)。
- **SC-014 外部数据同步**:订单(SAP)、项目属性(SMS)、人员(OA)、租赁/配置(CRM)、工单(ITR)、License 等外部数据按定时任务同步,保证交付域数据时效性。
- **SC-015 工作流联动**:批量变更项目经理时 100% 联动终止其手中的闭环申请与回访流程,避免流程悬挂。
- **SC-016 验货超期提醒**:每日/每周按时扫描验货节点(到货验收/安装/初验/终验四节点)超期情况并按办事处发送汇总邮件,提醒送达率 100%。
- **SC-017 全国汇总邮件延迟发送**:全国汇总邮件延迟 30 分钟发送,避免与分办邮件冲突。
- **SC-018 项目列表查询性能**:项目列表复杂查询在万级项目数据量下响应时间合理,终端用户可接受。
- **SC-019 项目最后刷新时间**:成员变更、安装地址保存、周报保存/提交等关键操作 100% 更新项目最后刷新时间(projectRefreshTime),可反映项目活跃度。
- **SC-020 操作日志**:项目操作 100% 记录操作日志,可审计追溯。
- **SC-021 历史设备保留**:查询发货设备默认排除已转出设备(转移标记≠0,即 transferFlag=1 转销或 2 退货),历史发货数量查询包含转销/退货设备,既保证当前视图准确又保留历史可追溯。

## Assumptions

- **外部系统可用性**:SAP/SMS/OA/CRM/ITR/License/EMS 等外部系统的同步任务与视图可正常提供数据,本域消费其同步结果,不负责外部系统内部逻辑。
- **基础数据就绪**:用户信息、基础数据、部门、公司、物料、通知模板等基础数据由其他域维护并可用。
- **工作流引擎可联动**:批量变更项目经理时,闭环与回访流程所在工作流引擎支持任务重指派与终止。
- **角色体系稳定**:工程管理部/管理员/财务/项目管理员/服务经理/项目经理/普通用户/项目查阅等角色及其权限边界由权限域定义,本域据此分权。
- **项目状态机驱动**:项目状态(21/30/31/32/34/20/100)由角色与操作驱动流转,本域遵循该状态机(21→30→31→32→34→20/100,21 为创建中中间态)。
- **pm_project 与 pm_project_header 命名**:pm_project 为 pm_project_header 的同义词/视图,语义等价;实现层统一使用 pm_project_header 表名,旧语句迁移;数据契约以 pm_project_header 为准。
- **编码回退**:文件下载支持 ISO8859-1→UTF-8 编码回退,假定文件名与路径可能存在多编码场景。
- **总代借货特殊逻辑**:销售类型 14(总代借货)在发货、安装、转移、版本查询中按利润中心(column001 在 salesType=14 时复用为 profitCenter)过滤,转移时合同号加 -C 后缀,该后缀语义由业务约定。
- **周报周计算**:周报起止时间按周一 0:00:00 至周日 23:59:59 计算,假定遵循 ISO 周历。
- **验货超期阈值**:到货验收后 2 月、安装 5 月、初验 9 月、终验按初验后超期计算(四节点:到货验收/安装/初验/终验),假定阈值由业务约定并可由系统配置调整。
- **外部同步表字段待补**:ITR 工单表与 License 授权表字段未在源码显式定义,完整字段需查询对应同步任务定义(GainDataFromITR/GainDataFromLicense)补充;补全前本域仅将 projectCode/contractNo/barCode 等关键字段作为稳定契约。
- **批量导入格式**:项目清理/批量导入/验货忽略项导入使用 Excel 文件,假定模板格式由业务约定。
- **projectCode 后缀规则**:projectCode 可含 "-" 后缀,后缀标识派生关系;-C 表示总代借货转移派生(chContractNo 同源),-B 表示项目拆分派生;去后缀逻辑为按 "-" 分割取首段。
- **软删除豁免清单**:清理类批量操作(项目清理)、配置类(忽略项)、临时附件(周报附件)豁免软删除原则,允许物理删除/truncate。
