# 002-presales-product 域歧义澄清记录(Clarify)

> 日期: 2026-07-09
> 来源: docs/reverse-engineering/002-presales-product/ambiguities.md
> 歧义总数: 13(AMB-002-01 ~ AMB-002-13)
> 处理原则: 采纳 ambiguities.md 中每条"建议决策"作为最终决策;对建议"保留 [待澄清]"的条目,以 [暂定决策:...] 形式固化,确保 spec 中 [待澄清] 清零。
> 输出: clarify.md(本文件)+ spec_updated.md(清除全部 [待澄清])

---

## AMB-002-01: serviceApprove 流程节点在 BPMN 定义中缺失

- **决策结论**: 标记为已知歧义,以 [暂定决策] 固化。新系统实现时需在以下二选一中明确:若保留服务经理审批环节,需在流程定义中显式定义 serviceApprove 节点;若废弃,应移除 serviceApproveDuration 字段及对应 SUM(IF(TASK_DEF_KEY_='serviceApprove',DURATION_,NULL)) 统计逻辑。在新系统决策落地前,spec 保留该字段定义但标注为"待业务决策"。
- **依据**: Presales.bpmn 完整流程定义仅含 usertask1~usertask4 与 endevent1,不存在 id="serviceApprove" 的 userTask 节点;但 sql-map-presales-config.xml:806 引用该 taskDefKey 进行时长统计。三种候选解释(动态注入/历史遗留/未纳入仓库的流程定义)均无法在代码层确证,属业务决策范畴。
- **影响范围**: 高——User Story 2 场景 8、FR-PRESALES-03 处理规则 5、FR-PRESALES-06、pm_presales_project_duration.serviceApproveDuration 字段、SC-023 均受影响。新系统若按字面复用统计逻辑,serviceApproveDuration 将恒为 NULL。
- **回滚提示**: 若后续业务确认保留服务经理审批环节并动态注入节点,需在 BPMN 中显式定义 serviceApprove userTask,并验证 act_hi_taskinst 写入;若确认废弃,需同步移除 spec 中 serviceApproveDuration 字段、FR-PRESALES-06 中该节点的 DURATION 求和项、User Story 2 场景 8。

---

## AMB-002-02: assigneeRole 角色常量值实际已可确定

- **决策结论**: 消解 [待澄清]。prob_restore.assigneeRole 取值明确为:0=指定人、11=服务经理(ROLE_SERVICEMANAGER)。发布任务时指派人非空则 assigneeRole=0,指派人为空则 assigneeRole=11。更新指派人时(update_prob_restore_assignee)assigneeRole 固定重置为 0。
- **依据**: MessageUtil.java:225 明确定义 `public static final int ROLE_SERVICEMANAGER = 11`;ProbManageAction.java:480-484 的条件逻辑(空→服务经理角色,非空→0)与之一致;sql-map-prob-config.xml:1023 的更新路径固定设为 0 也已核实。
- **影响范围**: 低——prob_restore 表 assigneeRole 字段语义、FR-PROB-06 处理规则 6、FR-PROB-07 更新指派人路径。角色值明确后数据迁移与权限校验可直接引用。
- **回滚提示**: 无需回滚;若 MessageUtil 角色常量重编号,需同步更新 assigneeRole=11 的取值与数据迁移映射。

---

## AMB-002-03: 设备日志解析策略归属错误(两套解析子系统混淆)

- **决策结论**: 拆分为两条独立解析路径。(1) 手工录入版本解析走 VersionParserFactory,注册 LegacyVersionParserStrategy / NewVersionParserStrategy,按 getPattern().matcher(input).find() 选择策略(对应 LegacyVersionUtil.PATTERN / SoftNewVersionUtil.PATTERN);(2) 设备日志解析走 DeviceLogParserFacade → DeviceVersionLogParser.parse()/matches() 独立组件,与 VersionParserFactory 无关。spec 不再将二者混为一谈。
- **依据**: VersionParserFactory.java:8-11 用于 parserSoftVersion(手工录入);DeviceLogParserFacade.java:8-14 用于设备日志解析,两条路径代码独立。原 spec 将设备日志解析归因于 VersionParserFactory 系逆向误合并。
- **影响范围**: 高——NFR-VER-04、SC-020、spec 附录 [待澄清] #4。新系统实现时必须分别实现两个组件,否则将遗漏设备日志解析能力。
- **回滚提示**: 若后续验证 DeviceVersionLogParser 内部间接调用 VersionParserFactory,需重新合并描述;但在确证前应保持拆分以避免遗漏独立组件。

---

## AMB-002-04: projectState 取值字典不完整且存在隐含语义

- **决策结论**: 以 [暂定决策] 固化已知线索:20 与 PresalesClose20TaskHandler 关联(疑似直接/驳回闭环)、100 疑似正常闭环(审批通过闭环),二者触发 allDuration 全时长统计;30~33 为各审批阶段;10=待开始。完整状态机与基础数据 dataTypeCode=27 的精确映射需核对 fnd_basic_data 实际数据后最终消解。
- **依据**: sql-map-presales-config.xml:801 `IF(pph.projectState IN (20,100), SUM(DURATION_), NULL)` 表明 20/100 为终态;Presales.bpmn 中 PresalesClose20TaskHandler(exclusivegateway1 result==-1 触发)命名中的"20"对应直接闭环;FR-PRESALES-01 默认状态过滤(工程管理部看"待创建",其他看 30/31/32/33)佐证阶段划分。
- **影响范围**: 中——售前列表默认状态过滤(FR-PRESALES-01)、时长统计 allDuration 计算、流程闭环处理器选择。状态机不完整可能导致列表过滤遗漏或时长统计偏差。
- **回滚提示**: 一旦取得 fnd_basic_data dataTypeCode=27 实际数据,应将 [暂定决策] 替换为完整状态机定义;若 20/100 语义与暂定结论不符,需同步修正 allDuration 统计条件与闭环处理器映射。

---

## AMB-002-05: 版本正则 BinExt 捕获组未纳入 spec 的"10 段"描述

- **决策结论**: 在 FR-VER-02 补注:正则另含可选 BinExt 扩展名捕获组 `(?<BinExt>\\.[A-z]{1,})?`,匹配二进制文件扩展名(如 .app/.bin),不计入 10 段版本结构,解析时单独处理。
- **依据**: SoftVersionStrategy.java:18 正则末尾存在第 11 个命名捕获组 BinExt,但 spec 的"10 段"描述未提及;BinExt 为文件扩展名附属信息,不影响版本范围匹配。
- **影响范围**: 低——BinExt 不影响版本范围 BETWEEN 匹配;但新系统复用正则时需注意该组存在,避免解析异常。
- **回滚提示**: 无需回滚;若新系统决定不处理文件扩展名,可在正则中移除 BinExt 组。

---

## AMB-002-06: LATCHxx 与 MATCHxx 命名组实际匹配文本与命名不符

- **决策结论**: 在 FR-VER-02 补注:LATCHxx 为语义标识(定制版本 Lxx 之后的补丁),正则匹配 `PATCH\d{2,3}`,无 LATCH 字面量前缀;MATCHxx 为一个或多个 `PATCH\d{2,3}` 序列(用于多版本补丁匹配),无 MATCH 字面量前缀。两者均复用 PATCH 格式。
- **依据**: SoftVersionStrategy.java:18,72-76 正则定义:LATCHxx=`(?<LATCHxx>PATCH\\d{2,3})?`、MATCHxx=`(?<MATCHxx>(?:PATCH\\d{2,3})+)?`;代码注释称"LATCH:补丁版本标识"但正则无 LATCH 字面量。
- **影响范围**: 低——spec 描述与正则实际行为一致,仅命名易引起"存在 LATCH/MATCH 字面量前缀"的误解。
- **回滚提示**: 无需回滚;若新系统重命名捕获组,需同步更新 FR-VER-02 描述。

---

## AMB-002-07: restoreStatus 字段物理归属与 Bean 映射不一致

- **决策结论**: 明确 restoreStatus 不在 prob_restore 物理表,所有 restoreStatus 读写均通过 processId 关联 prob_restore_process。写入:创建流转过程记录(prob_restore_process)时写入 restoreStatus,非 prob_restore 主表;查询:通过 processId LEFT JOIN prob_restore_process 获取(ProbRestoreMap resultMap 中 restoreStatus 为查询时 join 填充的瞬态属性)。
- **依据**: sql-map-prob-config.xml:836 resultMap 将 restoreStatus 映射为 Bean 属性(查询时 LEFT JOIN a4 填充);line 1002-1003 insert 写入 prob_restore_process;line 785-786,817-818 查询通过 a4.restoreStatus 过滤。prob_restore 物理表无 restoreStatus 列。
- **影响范围**: 中——FR-PROB-06 处理规则 7、FR-PROB-07、prob_restore 表定义。新系统实现时若误将 restoreStatus 写入 prob_restore 主表将导致数据不一致。
- **回滚提示**: 无需回滚;若新系统决定在 prob_restore 主表冗余存储 restoreStatus,需明确标注为反范式冗余并保持与 prob_restore_process 同步。

---

## AMB-002-08: affectedType 过滤逻辑在不同查询中不一致

- **决策结论**: 统一过滤语义:affectedType=0 表示"影响所有设备类型",在受影响设备匹配时不限设备类型(盒式+框式均匹配)。各查询统一采用 `affectedType IN (#affectedType#, 0)` 语义(指定类型 OR 0=所有),避免 prob_softwares 单表查询(line 530)使用精确等号导致 affectedType=0 时漏匹配。
- **依据**: sql-map-prob-config.xml:34,52,71 公告列表查询用 `affectedType in (#affectedType#, 0)`(含 0);line 530 prob_softwares 单表查询用 `ps.affectedType = #affectedType#`(精确,不含 0);spec 表定义"0 所有、1 盒式、2 框式"暗示 0 应匹配所有。统一为 IN 语义以避免漏匹配。
- **影响范围**: 中——FR-PROB-01 公告列表过滤、FR-PROB-06 受影响设备匹配。affectedType=0 的公告在受影响设备检索时需匹配所有设备类型。
- **回滚提示**: 若业务确认 prob_softwares 单表查询为按特定类型精确筛选(与列表查询语义不同),需在 FR-PROB-06 中分别标注两种查询的过滤语义;但默认按统一 IN 语义实现。

---

## AMB-002-09: User Story 2 场景 7 与场景 3 驳回逻辑矛盾

- **决策结论**: 修正场景 7 为"除 usertask1 外的审批节点(usertask2/usertask3/usertask4)驳回→回退到上一节点;usertask1 驳回→直接闭环(见场景 3)"。即场景 7 的"任一审批节点"排除首节点 usertask1。
- **依据**: Presales.bpmn 确认:usertask1 reject→闭环(endevent1 via PresalesClose20TaskHandler);usertask2 reject→回退 usertask1;usertask3 reject→回退 usertask2;usertask4 reject→驳回 usertask3。场景 7 字面"任一审批节点"与场景 3"usertask1 驳回直接闭环"矛盾,首节点无上一节点。
- **影响范围**: 中——User Story 2 场景 7、FR-PRESALES-03 处理规则。按场景 7 字面实现会导致 usertask1 驳回时错误回退。
- **回滚提示**: 无需回滚;若业务确认所有节点(含首节点)驳回均回退,需同步修改 BPMN 与场景 3,但当前以 BPMN 为准。

---

## AMB-002-10: 时长字段命名与流程节点语义错位

- **决策结论**: 在 pm_presales_project_duration 表定义中补注:serviceDuration = usertask1 阶段耗时(该阶段由工程管理部指派服务经理);programDuration = usertask2 阶段耗时(该阶段由服务经理指定项目经理)。字段名取自阶段产出(被指派角色)而非操作人。
- **依据**: sql-map-presales-config.xml:802-805 明确映射 serviceDuration→usertask1、programDuration→usertask2;BPMN 中 usertask1 名称"工程管理部指派服务经理"、usertask2 名称"服务经理指定项目经理"。字段命名取自产出(服务经理/项目经理被指派)而非操作人。
- **影响范围**: 低——代码行为一致,仅命名易误解为"操作人在该阶段的耗时"。
- **回滚提示**: 无需回滚;若新系统重命名字段为 operatorDuration 风格,需同步更新 FR-PRESALES-06 与表定义。

---

## AMB-002-11: 软件版本检索三表 UNION 性能 [待澄清] 未消解

- **决策结论**: 以 [暂定决策] 固化:新系统实现时需评估三表 UNION 查询(fb_soft_version / pm_project_soft_version / prob_soft_version)的查询计划与预估数据规模,视情况对 pm_project_soft_version.conp/cpld/boot/pcb 列加索引或引入缓存。当前代码无显式索引优化或缓存策略。
- **依据**: sql-map-prob-config.xml:416-420 确认为三表 UNION 去重查询,无显式索引优化;pm_project_soft_version 为设备级记录,数据量可能较大,存在性能风险。spec 无法确定生产数据规模,故以暂定决策固化评估要求。
- **影响范围**: 中——NFR-PERF-04、SC-004、User Story 6 场景 8。数据量增长后可能影响版本检索响应时间。
- **回滚提示**: 一旦取得生产数据规模与查询计划评估结果,应将 [暂定决策] 替换为具体索引/缓存方案;若评估表明性能可接受,可标注"无需优化"。

---

## AMB-002-12: allDuration 中间字段计算后未持久化

- **决策结论**: allDuration 为历史遗留的冗余中间计算值,仅在 SQL 子查询中存在但未被外层引用或持久化。spec 无需修改(表定义中本就无 allDuration 字段);新系统实现时不应包含 allDuration 计算。
- **依据**: sql-map-presales-config.xml:801 计算 `IF(pph.projectState IN (20,100), SUM(DURATION_), NULL) AS allDuration`,但 line 766 INSERT 列列表未包含该字段;spec 的 pm_presales_project_duration 表定义中也无 allDuration 字段。
- **影响范围**: 低——不影响功能,仅代码存在无效计算。
- **回滚提示**: 无需回滚;若新系统决定持久化 allDuration(作为终态项目总耗时),需在表定义中新增字段并补充 FR-PRESALES-06 输出说明。

---

## AMB-002-13: 角色常量数值映射未在 spec 中记录

- **决策结论**: spec 主体不记录角色数值常量(遵循技术栈无关原则,角色编码属实现细节 I 级)。在 spec 附录补录角色常量映射表供数据迁移与权限初始化参考:ROLE_ADMIN=1、ROLE_PROJECT_VIEWER=6、ROLE_ENGINEEMANAGER_LEADER=10、ROLE_SERVICEMANAGER=11、ROLE_ENGINEEMANAGER=13、ROLE_PRESALES_STAFF=17、ROLE_PROB_ADMIN=18、ROLE_PROB_SUPPORTER=19、ROLE_PROB_RD=20、ROLE_COMPONENT_ADMIN=22。注意角色编码非连续(存在跳号),迁移时需精确映射。
- **依据**: MessageUtil.java:201-269 明确定义上述常量;spec 原则技术栈无关故省略数值,但数据迁移与权限校验需角色编码映射。
- **影响范围**: 低——不影响 spec 契约;数据迁移与权限初始化时需引用附录映射表。
- **回滚提示**: 无需回滚;若角色体系重构,需同步更新附录映射表与数据迁移脚本。

---

## 汇总

| 编号 | 决策类型 | 处理结果 |
|---|---|---|
| AMB-002-01 | 暂定决策 | [暂定决策] 固化,二选一待业务决策 |
| AMB-002-02 | 已消解 | assigneeRole=0/11,更新时固定重置为 0 |
| AMB-002-03 | 已消解 | 拆分两条解析路径(手工录入/设备日志) |
| AMB-002-04 | 暂定决策 | [暂定决策] 固化已知线索,待核对基础数据 27 |
| AMB-002-05 | 已消解 | 补注 BinExt 捕获组 |
| AMB-002-06 | 已消解 | 补注 LATCHxx/MATCHxx 命名语义 |
| AMB-002-07 | 已消解 | restoreStatus 归属 prob_restore_process |
| AMB-002-08 | 暂定决策 | 统一 affectedType IN 语义,0=所有 |
| AMB-002-09 | 已消解 | 场景 7 排除首节点 usertask1 |
| AMB-002-10 | 已消解 | 补注时长字段命名取自产出 |
| AMB-002-11 | 暂定决策 | [暂定决策] 固化评估要求 |
| AMB-002-12 | 已消解 | allDuration 为冗余,新系统不含 |
| AMB-002-13 | 已消解 | 附录补录角色常量映射表 |

> spec_updated.md 中 [待澄清] 出现次数:0(已全部替换为决策结论或 [暂定决策:...])。
