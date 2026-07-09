# 002-presales-product 域歧义清单(Ambiguities)

> 日期: 2026-07-09
> 歧义总数: 13
> 比对维度: 代码 vs 文档(spec.md/spec-draft.md)、代码 vs 代码(内部一致性)、spec 内部 [待澄清] 展开
> 代码基线: PMS-struts 分支 002-presales-product
> 关键证据文件: Presales.bpmn、sql-map-prob-config.xml、sql-map-presales-config.xml、MessageUtil.java、SoftVersionStrategy.java、VersionParserFactory.java、DeviceLogParserFacade.java

---

## AMB-002-01: serviceApprove 流程节点在 BPMN 定义中缺失

- **位置**: FR-PRESALES-03 处理规则 5、FR-PRESALES-06、pm_presales_project_duration 表(serviceApproveDuration 字段)、SC-023、spec 附录 [待澄清] #2、User Story 2 场景 8
- **现象**: spec 多处引用 `serviceApprove` taskDefKey 用于 serviceApproveDuration 时长统计(证据 sql-map-presales-config.xml:806 `SUM(IF(TASK_DEF_KEY_ = 'serviceApprove', DURATION_, NULL)) AS serviceApproveDuration`)。但 Presales.bpmn 完整流程定义中仅包含 usertask1~usertask4 与 endevent1,不存在 id="serviceApprove" 的 userTask 节点。spec 自身已标注 [待澄清]"疑为动态加入或历史遗留"。
- **候选解释**:
  - (a) serviceApprove 为运行时动态注入的任务节点(BPMN 未静态定义,由流程引擎 API 动态创建);
  - (b) 历史遗留代码,act_hi_taskinst 中实际不存在 task_def_key_='serviceApprove' 的记录,SUM 始终返回 NULL,该字段永远为空;
  - (c) 存在另一份未纳入仓库的流程定义文件包含该节点。
- **影响面**: 高——User Story 2 场景 8(按 serviceApprove 节点 DURATION 求和)在新系统中可能无法实现;serviceApproveDuration 字段语义不确定。
- **建议决策**: 标记为已知歧义。新系统实现时需明确:若保留服务经理审批环节,需在流程定义中显式定义 serviceApprove 节点;若废弃,应移除 serviceApproveDuration 字段及统计逻辑。在 spec 中保留 [待澄清] 标注直至决策。

---

## AMB-002-02: assigneeRole 角色常量值实际已可确定

- **位置**: prob_restore 表定义(assigneeRole 字段)、FR-PROB-06 处理规则 6、spec 附录 [待澄清] #5
- **现象**: spec 标注 [待澄清]"prob_restore.assigneeRole 取 0 或 MessageUtil.ROLE_SERVICEMANAGER 值,具体数值常量需核对 MessageUtil"。经核对 MessageUtil.java:225 已明确定义 `public static final int ROLE_SERVICEMANAGER = 11`。该 [待澄清] 实际已可消解:assigneeRole=0 表示指定人,assigneeRole=11 表示服务经理。
- **候选解释**:
  - assigneeRole=0(指定人)——当发布任务时指派人非空(ProbManageAction.java:480-484);
  - assigneeRole=11(服务经理 ROLE_SERVICEMANAGER)——当指派人为空时自动指派服务经理角色;
  - 另:update_prob_restore_assignee(sql-map-prob-config.xml:1023)在更新指派人时始终将 assigneeRole 固定设为 0,与 FR-PROB-06 的条件逻辑(空→11,非空→0)存在路径差异。
- **影响面**: 低(主要已可消解)——角色值明确;但更新路径的固定值行为需在 spec 中补充说明。
- **建议决策**: 消解 [待澄清] #5,在 prob_restore 表定义中补录 assigneeRole 取值"0=指定人、11=服务经理(ROLE_SERVICEMANAGER)";在 FR-PROB-07 中补注"更新指派人时 assigneeRole 固定重置为 0"。

---

## AMB-002-03: 设备日志解析策略归属错误(两套解析子系统混淆)

- **位置**: NFR-VER-04、SC-020、spec 附录 [待澄清] #4
- **现象**: spec 称"设备日志版本解析支持 Legacy 与 New 两种策略(VersionParserFactory.java:8-11),按输入匹配选择 [待澄清]"。但代码验证表明:
  - VersionParserFactory(VersionParserFactory.java:8-11)注册的 LegacyVersionParserStrategy / NewVersionParserStrategy 用于**手工录入版本解析**(parserSoftVersion),匹配逻辑为 `getPattern().matcher(input).find()`;
  - **设备日志解析**走独立路径 DeviceLogParserFacade.java:8-14 → DeviceVersionLogParser.parse()/matches(),与 VersionParserFactory 无关。
  - spec 将两套解析子系统混淆,错误地将设备日志解析归因于 VersionParserFactory。
- **候选解释**:
  - (a) spec 逆向时将两套解析路径误合并;
  - (b) DeviceVersionLogParser 内部可能间接调用 VersionParserFactory(需进一步验证 DeviceVersionLogParser 实现)。
- **影响面**: 高——NFR-VER-04 与 SC-020 描述的解析路径不正确,新系统实现时可能遗漏设备日志解析独立组件(DeviceLogParserFacade / DeviceVersionLogParser)。
- **建议决策**: 拆分为两条 NFR:(1) 手工录入版本解析走 VersionParserFactory(Legacy/New 策略,按正则 matches 选择);(2) 设备日志解析走 DeviceLogParserFacade(独立组件)。Legacy/New 策略的 matches 规则依赖各自 PATTERN(LegacyVersionUtil.PATTERN / SoftNewVersionUtil.PATTERN),需补充正则定义。

---

## AMB-002-04: projectState 取值字典不完整且存在隐含语义

- **位置**: pm_presales_project_header.projectState 字段、FR-PRESALES-01 处理规则 1、spec 附录 [待澄清] #1
- **现象**: spec 标注 [待澄清]"代码出现 10/20/30/31/32/33/100 等值,完整状态机与基础数据 27 的映射需核对"。代码验证揭示更多线索:
  - sql-map-presales-config.xml:801 `IF(pph.projectState IN (20,100), SUM(DURATION_), NULL) AS allDuration`——状态 20 和 100 触发全时长统计,暗示为终态;
  - BPMN 中 PresalesClose20TaskHandler(直接闭环处理器,exclusivegateway1 result==-1 时触发)命名中的"20"可能对应 projectState=20;
  - FR-PRESALES-01 默认状态过滤:工程管理部看"待创建",其他角色看"30/31/32/33"。
- **候选解释**:
  - 10=待开始、20=直接闭环(PresalesClose20TaskHandler)、30~33=各审批阶段、100=正常闭环;
  - 或 20/100 均为终态(20=驳回闭环、100=审批通过闭环)。
- **影响面**: 中——售前列表默认状态过滤、时长统计 allDuration 计算、流程闭环处理器选择均依赖完整状态机。
- **建议决策**: 暂保留 [待澄清] 但补充已知线索:20 与 PresalesClose20TaskHandler 关联(疑似直接/驳回闭环)、100 疑似正常闭环;需核对 fnd_basic_data dataTypeCode=27 实际数据以最终消解。

---

## AMB-002-05: 版本正则 BinExt 捕获组未纳入 spec 的"10 段"描述

- **位置**: FR-VER-02 处理规则 1、SoftVersionStrategy.java:18
- **现象**: spec 称"版本由 10 个可选部分顺序组成",列出 Hvvv/Evvv/Fxxx/Dxxx/Pxx/PATCHxx/Txx/Lxx/LATCHxx/MATCHxx 共 10 段。但实际正则(SoftVersionStrategy.java:18)末尾还有第 11 个命名捕获组 `(?<BinExt>\\.[A-z]{1,})?`,匹配二进制文件扩展名(如 `.app`、`.bin`),spec 完全未提及。
- **候选解释**: BinExt 为文件扩展名而非版本结构组成部分,故不计入 10 段版本结构;但正则中确实存在该捕获组,解析时会捕获。
- **影响面**: 低——BinExt 为可选附属信息,不影响版本范围匹配;但新系统复用正则时需注意该组存在。
- **建议决策**: 在 FR-VER-02 补注"正则另含可选 BinExt 扩展名捕获组(如 .app/.bin),不计入 10 段版本结构,解析时单独处理"。

---

## AMB-002-06: LATCHxx 与 MATCHxx 命名组实际匹配文本与命名不符

- **位置**: FR-VER-02 处理规则 1、SoftVersionStrategy.java:18,72-76
- **现象**:
  - **LATCHxx**:spec 描述"定制补丁(PATCH + 2-3 位流水)",正则为 `(?<LATCHxx>PATCH\\d{2,3})?`——命名 LATCHxx 但匹配文本为 `PATCH\d{2,3}`(无 LATCH 字面量)。代码注释(SoftVersionStrategy.java:73)称"LATCH：补丁版本标识"但正则无 LATCH。
  - **MATCHxx**:spec 描述"多版本补丁匹配",正则为 `(?<MATCHxx>(?:PATCH\\d{2,3})+)?`——命名 MATCHxx 但匹配一个或多个 `PATCH\d{2,3}` 序列(无 MATCH 字面量)。
  - spec 的描述"(PATCH + 2-3 位流水)"对 LATCHxx 是准确的,但未说明 MATCHxx 也是 PATCH 重复模式;命名与匹配文本不符易引起误解。
- **候选解释**: LATCHxx/MATCHxx 为语义命名(分别表示"定制版本后的补丁"和"多版本补丁匹配"),实际均复用 PATCH 格式;LATCHxx 位于 Lxx 之后,MATCHxx 位于最末可重复。
- **影响面**: 低——spec 描述与正则实际行为一致,但命名易引起"存在 LATCH/MATCH 字面量前缀"的误解。
- **建议决策**: 在 FR-VER-02 补注:"LATCHxx 命名为语义标识(定制版本 Lxx 之后的补丁),正则匹配 `PATCH\d{2,3}`;MATCHxx 为一个或多个 `PATCH\d{2,3}` 序列,用于多版本补丁匹配。两者均无 LATCH/MATCH 字面量前缀。"

---

## AMB-002-07: restoreStatus 字段物理归属与 Bean 映射不一致

- **位置**: prob_restore 表定义、prob_restore_process 表定义、FR-PROB-06 处理规则 7、FR-PROB-07、sql-map-prob-config.xml:836,864,880,1002-1003
- **现象**: spec 正确指出"restoreStatus/restoreRemark 不在 prob_restore 表,而在 prob_restore_process(通过 processId 关联)"。但存在以下不一致:
  - ProbRestoreMap resultMap(sql-map-prob-config.xml:836)将 `restoreStatus` 映射为 prob_restore Bean 的属性(查询时通过 LEFT JOIN prob_restore_process a4 填充);
  - FR-PROB-06 处理规则 7 说"restoreStatus=0 时设为 10(开始流程)"——此处 restoreStatus 实际写入 prob_restore_process(line 1002-1003),而非 prob_restore 表;
  - FR-PROB-07 处理规则 2 说"个人任务:restoreStatus=10(已发布接受)起查"——查询时通过 a4.restoreStatus 过滤(line 785-786, 817-818)。
  - spec 在 FR-PROB-06 中未明确标注 restoreStatus 写入的目标表。
- **候选解释**: Bean 层面 restoreStatus 为查询时从 join 填充的瞬态属性;写入时实际目标是 prob_restore_process 表;prob_restore 物理表本身无 restoreStatus 列。
- **影响面**: 中——新系统实现时需明确 restoreStatus 不在 prob_restore 物理表;所有 restoreStatus 读写均通过 processId 关联 prob_restore_process。
- **建议决策**: 在 FR-PROB-06 处理规则 7 显式标注"restoreStatus 写入 prob_restore_process 表(创建流转过程记录时写入),非 prob_restore 主表";在 FR-PROB-07 标注"restoreStatus 查询通过 processId LEFT JOIN prob_restore_process 获取"。

---

## AMB-002-08: affectedType 过滤逻辑在不同查询中不一致

- **位置**: FR-PROB-01 处理规则、FR-PROB-06 受影响设备匹配、sql-map-prob-config.xml:34,52,71 vs 530
- **现象**:
  - 公告列表查询(sql-map-prob-config.xml:34,52,71)使用 `affectedType in (#affectedType#, 0)`——匹配指定类型 **OR** 0(所有),即 affectedType=0 的公告始终匹配;
  - prob_softwares 单表查询(sql-map-prob-config.xml:530)使用 `ps.affectedType = #affectedType#`——精确匹配,不含 0;
  - spec 表定义说"affectedType:0 所有、1 盒式、2 框式",但未说明不同查询场景的过滤语义差异。
- **候选解释**:
  - (a) 列表查询需包含"影响所有类型(0)"的公告故用 IN;子表查询为精确过滤故用等号——但 0 本身也应匹配所有设备类型;
  - (b) prob_softwares 单表查询(line 530)可能是按特定 affectedType 精确筛选版本记录,与列表查询的"公告维度"过滤语义不同。
- **影响面**: 中——当 affectedType=0(所有)时,prob_softwares 单表查询可能漏匹配;受影响设备检索的 affectedType 处理需统一语义。
- **建议决策**: 需确认 affectedType=0 的公告在受影响设备匹配时是否应匹配所有设备类型(盒式+框式);若是,FR-PROB-06 匹配规则需补注"affectedType=0 时不限设备类型";统一各查询的过滤语义。

---

## AMB-002-09: User Story 2 场景 7 与场景 3 驳回逻辑矛盾

- **位置**: User Story 2 Acceptance Scenarios #3 vs #7、FR-PRESALES-03 处理规则 1-4、Presales.bpmn
- **现象**:
  - 场景 3:"Given 流程到达工程管理部指派节点(usertask1),When 审批结果为驳回,Then **直接闭环**结束流程。"——与 BPMN 一致(exclusivegateway1 result==-1 → endevent1 via PresalesClose20TaskHandler)。
  - 场景 7:"Given **任一审批节点**,When 审批结果为驳回,Then 流程**回退到上一节点**。"——称"任一审批节点"均回退。
  - 矛盾:usertask1 是首节点,无上一节点,其驳回行为为"直接闭环"而非"回退"。BPMN 确认:usertask1 reject→闭环;usertask2 reject→回退usertask1;usertask3 reject→回退usertask2;usertask4 reject→驳回usertask3。
- **候选解释**: 场景 7 的"任一审批节点"应排除 usertask1(首节点),或改为"除首节点外的审批节点"。
- **影响面**: 中——流程驳回逻辑实现时需区分首节点(usertask1 驳回→闭环)与其他节点(驳回→回退);按场景 7 字面实现会导致 usertask1 驳回时错误回退。
- **建议决策**: 修正场景 7 为"除 usertask1 外的审批节点驳回→回退到上一节点;usertask1 驳回→直接闭环(见场景 3)"。或在场景 7 补注"首节点例外,见场景 3"。

---

## AMB-002-10: 时长字段命名与流程节点语义错位

- **位置**: pm_presales_project_duration 表定义(serviceDuration/programDuration 字段)、FR-PRESALES-03、FR-PRESALES-06、sql-map-presales-config.xml:802-805
- **现象**:
  - serviceDuration(注释"服务经理指派耗时")映射 usertask1,但 usertask1 的名称是"工程管理部指派服务经理"——操作人是工程管理部,产出是服务经理指派;
  - programDuration(注释"项目经理指派耗时")映射 usertask2,但 usertask2 的名称是"服务经理指定项目经理"——操作人是服务经理,产出是项目经理指派;
  - 字段名暗示的"操作人"与流程节点的实际操作人不一致,易误解为"服务经理在该阶段的耗时"。
- **候选解释**: 字段命名取自该阶段的**产出/结果**而非操作人:serviceDuration="服务经理(被)指派阶段的耗时",programDuration="项目经理(被)指派阶段的耗时"。语义可解释但易混淆。
- **影响面**: 低——代码行为一致(sql-map-presales-config.xml:802-805 明确映射),仅命名易误解。
- **建议决策**: 在 pm_presales_project_duration 表定义中补注:"serviceDuration = usertask1 阶段耗时(该阶段由工程管理部指派服务经理);programDuration = usertask2 阶段耗时(该阶段由服务经理指定项目经理)。字段名取自阶段产出而非操作人。"

---

## AMB-002-11: 软件版本检索三表 UNION 性能 [待澄清] 未消解

- **位置**: NFR-PERF-04、SC-004、User Story 6 场景 8、spec 附录 [待澄清] #3
- **现象**: spec 多处标注三表 UNION 查询(fb_soft_version / pm_project_soft_version / prob_soft_version)"数据量大时的索引优化与缓存策略需评估 [待澄清]"。代码 sql-map-prob-config.xml:416-420 确认为三表 UNION 去重查询,但无显式索引优化或缓存策略。
- **候选解释**:
  - (a) 当前数据量下性能可接受,无需优化;
  - (b) 需评估索引但 spec 无法确定生产数据规模;
  - (c) 三表 UNION 中 pm_project_soft_version 可能数据量大(设备级记录),存在性能风险。
- **影响面**: 中——新系统数据量增长后可能存在性能瓶颈,影响版本检索响应时间。
- **建议决策**: 保留 [待澄清];新系统实现时需评估三表 UNION 查询计划与预估数据规模,视情况对 pm_project_soft_version.conp/cpld/boot/pcb 列加索引或引入缓存。

---

## AMB-002-12: allDuration 中间字段计算后未持久化

- **位置**: FR-PRESALES-06、sql-map-presales-config.xml:801,766
- **现象**: SQL 子查询(sql-map-presales-config.xml:801)计算 `IF(pph.projectState IN (20,100), SUM(DURATION_), NULL) AS allDuration`,但 INSERT 语句列列表(line 766)未包含 allDuration;spec 的 pm_presales_project_duration 表定义中也无 allDuration 字段。allDuration 被计算但从未被引用或持久化。
- **候选解释**: allDuration 为历史遗留的中间计算值,仅在子查询中存在但未被外层引用,属于冗余代码。
- **影响面**: 低——不影响功能,但代码存在无效计算;新系统复用 SQL 时应移除。
- **建议决策**: 无需修改 spec;记录为代码冗余,新系统实现时不应包含 allDuration 计算。

---

## AMB-002-13: 角色常量数值映射未在 spec 中记录

- **位置**: NFR-AVAIL-01、SC-013、MessageUtil.java:201-269
- **现象**: spec 列出角色名称(技术公告员/技术支持/研发/组件管理员/管理员;工程管理部/售前专员/项目查看者/服务经理)但未给出数值常量。MessageUtil.java 已明确定义:ROLE_ADMIN=1、ROLE_PROJECT_VIEWER=6、ROLE_ENGINEEMANAGER_LEADER=10、ROLE_SERVICEMANAGER=11、ROLE_ENGINEEMANAGER=13、ROLE_PRESALES_STAFF=17、ROLE_PROB_ADMIN=18、ROLE_PROB_SUPPORTER=19、ROLE_PROB_RD=20、ROLE_COMPONENT_ADMIN=22。spec 原则为技术栈无关故省略数值,但数据迁移与权限校验时需知道角色编码。
- **候选解释**: spec 有意省略数值常量(遵循技术栈无关原则),角色编码属于实现细节(I 级)。
- **影响面**: 低——不影响 spec 契约;但数据迁移与权限初始化时需角色编码映射。
- **建议决策**: 不修改 spec 主体;可在附录或数据迁移文档中补录角色常量映射表(ROLE_ADMIN=1 等),供数据迁移与权限初始化参考。注意角色编码非连续(存在跳号),迁移时需精确映射。

---

## 汇总统计

| 类别 | 数量 | 编号 |
|---|---|---|
| 代码 vs 文档(spec) | 5 | AMB-002-01, AMB-002-03, AMB-002-05, AMB-002-07, AMB-002-08 |
| 代码 vs 代码(内部一致性) | 3 | AMB-002-06, AMB-002-10, AMB-002-12 |
| spec 内部 [待澄清] 展开 | 5 | AMB-002-01, AMB-002-02, AMB-002-03, AMB-002-04, AMB-002-11 |
| spec 内部一致性 | 2 | AMB-002-09, AMB-002-13 |

> 注:部分歧义跨多个类别(如 AMB-002-01 同时属代码 vs 文档与 [待澄清] 展开)。

### 已消解的 [待澄清]

| 编号 | 原标注 | 消解结论 |
|---|---|---|
| AMB-002-02 | assigneeRole 角色值 | 已消解:0=指定人、11=服务经理(ROLE_SERVICEMANAGER,MessageUtil.java:225) |

### 待保留的 [待澄清]

| 编号 | 内容 | 保留原因 |
|---|---|---|
| AMB-002-01 | serviceApprove 节点 | 需业务决策是否保留服务经理审批环节 |
| AMB-002-04 | projectState 取值字典 | 需核对 fnd_basic_data dataTypeCode=27 实际数据 |
| AMB-002-11 | 软件版本检索性能 | 需评估生产数据规模与查询计划 |
