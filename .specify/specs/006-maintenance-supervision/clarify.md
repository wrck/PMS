# 006-maintenance-supervision 域歧义澄清(Clarify)

> 日期: 2026-07-09
> 来源: `docs/reverse-engineering/006-maintenance-supervision/ambiguities.md`(共 11 条 AMB)
> 决策原则: 采纳各 AMB 的"建议决策"作为最终决策
> 应用范围: `.specify/specs/006-maintenance-supervision/spec.md`
> 字段说明: 每条 AMB 含【决策结论 / 依据 / 影响范围 / 回滚提示】四字段

---

## AMB-006-01: 借用维保额度子域 Action 实现类源文件缺失

- **决策结论**: 在 spec 中标记为"实现缺失,运行时可用性存疑,MVP 范围内不计入验收"。如必须支持,需向项目维护方索取该 Action 源文件或确认是否已废弃;若确认废弃,应将 FR-2.4.x 全部标记为 Deprecated。
- **依据**: 全代码库 grep `LendMaintenance|addQuota|isQuotaRepeat|seeQuota|updateQuota|deleteQuota` 仅命中 `struts-sys.xml:928-962` 与 `docs/domain-map.md` 两处,未找到 Action 实现类源文件,也未找到对应 Spring Bean 定义。
- **影响范围**: FR-2.4.1 ~ FR-2.4.5(借用维保额度列表/新增/校验/查看/更新/删除六个操作);User Story 9 Independent Test;附录点 1。
- **回滚提示**: 若运维/项目维护方确认 Action 已实现(如由 Struts2 别名机制或 `BeanNameAutoProxyCreator` 解析)或可补全源文件,需将 FR-2.4.x 移出"不计入 MVP"标记并补充实现与测试用例;若确认废弃,应清理 `struts-sys.xml` 路由残骸。

## AMB-006-02: 借用维保额度数据库表(lend_maintenance/lend_quota/maintenance_daily_report)未在 SQL-map 中命中

- **决策结论**: 在 spec 中移除"借用维保额度相关表"实体定义,并标注"借用维保额度子域因数据层缺失不计入 MVP 验收";`pm_project_soleagent_lend_from_sms` 不与"借用维保额度"混同,保留为代理商借用数据落地表。
- **依据**: `PMS-struts/config-ibaits/` 全量扫描未找到 `lend_maintenance`/`lend_quota`/`maintenance_daily_report` 的 resultMap/insert/select;`pm_project_soleagent_lend_from_sms` 字段(soleAgentLendId/orderExecNumber/orderExecNumberShort/orderCodes 等)与"借用维保额度"概念命名差异大。
- **影响范围**: 实体"借用维保额度相关表";FR-2.4.x 数据契约;附录点 2。
- **回滚提示**: 若 DBA 确认 `lend_maintenance`/`lend_quota`/`maintenance_daily_report` 真实存在数据库,需补全 SQL-map 与字段契约并恢复实体定义;若表存在但 SQL 直接写在 Action/JSP 中,需补全数据映射。

## AMB-006-03: 督查数据映射双份并存,运行时加载哪份未明确

- **决策结论**: 以 `sql-map-project-config2.xml`(行 4778-5060)为权威路径;`sql-map-project-config.xml`(行 5555+)中的同名映射标注为冗余(历史遗留/重构残骸),运行时加载顺序由 `sql-map-config.xml` 决定,如两份均加载则以后加载者覆盖前者。若后续逐字段比对发现差异(如 updateBy 是否回写),需修正权威路径。
- **依据**: spec 现有来源引用统一使用 `sql-map-project-config2.xml` 行号,保持一致性;`sql-map-work-config.xml:491` 仅引用 `pm_project_supervision ps` 非数据映射,不计入并存。
- **影响范围**: 实体 `pm_project_supervision`;FR-2.3.x 督查子域所有写入与查询;SC-007 软删除规则验证。
- **回滚提示**: 若逐字段比对发现两份 resultMap/insert/insertOrUpdate 存在差异且影响行为,需修正权威路径并补充差异说明;若 `sql-map-config.xml` 实际加载顺序与推测不符,需重新选定权威路径。

## AMB-006-04: cron 表达式实际存在但 spec 标记为 [待澄清]

- **决策结论**: 从 spec 中移除 cron 缺失的 [待澄清] 标记,在 Assumptions 中注明:
  - 维保日报调度: release profile cron = `00 00 05 * * ?`(每日 05:00:00),来源 `PMS-struts/config/beans-quartz.xml:303-312`(`MaintenanceDailyReportMailerTrigger`);
  - 维保服务季度交付提醒: release profile cron = `00 00 06 1 2/3 ?`(每季度中间月初 06:00,即 2/5/8/11 月 1 号),来源 `beans-quartz.xml:325-334`(`MaintenanceServiceQuarterMailerTrigger`);
  - 办事处秘书同步: 无独立 cron,由日报调度 `MaintenanceDailyReportMailer.execute()` 内部调用,可由 `MaintenanceDepartmentSectaryJob.main` 手动触发。
- **依据**: `PMS-struts/config/beans-quartz.xml` 明确配置三个 Trigger;多 profile 副本(dev/test/release/yfpms)可能不同,以 release profile 为准。
- **影响范围**: Assumptions 中两条 cron 配置;附录点 5;SC-010 季度交付提醒调度时点;NFR 调度触发段(若存在)。
- **回滚提示**: 需核对四份 profile(dev/test/release/yfpms)的 `beans-quartz.xml` cron 是否一致;若 yfpms 与 release 不同,以生产实际部署 profile 为准并修正 spec。

## AMB-006-05: temp_maintenance_contractNo 临时表名拼写不一致

- **决策结论**: 将 spec 实体名由 `temp_maintenance_contract_no`(全下划线)改为 `temp_maintenance_contractNo`(驼峰,`No` 大写无下划线)与代码对齐。
- **依据**: `sql-map-maintenance-config.xml:1467-1484` DDL 实际创建表名为 `temp_maintenance_contractNo`(驼峰);`deleteTempMaintenanceContractNoTable`(1486-1487)同样引用驼峰名;spec 原命名 `temp_maintenance_contract_no` 为文档笔误。
- **影响范围**: 实体 `temp_maintenance_contract_no`(原)→ `temp_maintenance_contractNo`(新);SC-011 数据固化依赖临时表正确性。
- **回滚提示**: 若 DBA 确认生产数据库 `lower_case_table_names=0` 且实际为下划线命名,需反向修正 spec;若两份命名均存在(驼峰与下划线各一张表),需 DBA 统一命名规范并修正 SQL-map。

## AMB-006-06: temp_project_warranty_state 字段定义来自子查询

- **决策结论**: 在 spec 中标注该表为"动态派生表,无静态字段契约;字段集合以 `selectProjectWarrantyState` 子查询输出列为准,DDL 仅定义 projectId/warrantyStatus/warrantyGrade/wafService 四个索引列"。
- **依据**: `sql-map-maintenance-config.xml:1561-1571` DDL 为 `CREATE TEMPORARY TABLE IF NOT EXISTS temp_project_warranty_state (KEY projectId, KEY warrantyStatus, KEY warrantyGrade, KEY wafService) AS <include refid="selectProjectWarrantyState"/> ... GROUP BY ph.projectCode`,字段集合动态派生,DDL 本身无静态字段定义。
- **影响范围**: 实体 `temp_project_warranty_state`;FR-2.2.3 季度交付提醒(引用 `serviceCode + "Enable"` 输出列,`MaintenanceServiceQuarterMailer.java:101`);FR-2.2.4 服务交付查询。
- **回滚提示**: 若需静态字段契约,需向开发/DBA 索取 `selectProjectWarrantyState` SQL 片段输出列清单并补全字段表;若子查询输出列变动,运行时 `NullPointerException` 需重新评估。

## AMB-006-07: 数据固化流程 spec 用"数据固化流程"模糊表述

- **决策结论**: 在 spec FR-2.2.1 末段明确"调度完成后 MUST 调用存储过程 `queryProjectMaintenanceInfo(1)` 进行每日数据固化";并备注"存储过程 DDL 未纳入代码库,需向 DBA 索取"。
- **依据**: `MaintenanceDailyReportMailer.java:113` 实际调用 `Call queryProjectMaintenanceInfo(1);`;草稿 NFR-4.1.1 已明确该存储过程名,spec 丢失;统一草稿与 spec 措辞。
- **影响范围**: FR-2.2.1 末段;附录点 4;SC-011;User Story 4 验收场景 5。
- **回滚提示**: 若存储过程实际名称不同或由其他载体实现,需修正 spec;若存储过程 DDL 可获取,需补充固化逻辑与目标表说明。

## AMB-006-08: SC-011"100% 调用数据固化"与代码 try-catch 包裹失败仅打印日志不符

- **决策结论**: 将 SC-011 改为"调度完成后 MUST 触发 `queryProjectMaintenanceInfo(1)` 调用进行每日数据固化;调用失败 MUST 记录异常日志但不阻断调度(失败重试策略待 DBA/运维澄清)"。
- **依据**: `MaintenanceDailyReportMailer.java:110-117` 数据固化调用被 try-catch 包裹,catch 块仅 `e.printStackTrace()`,无重试、无告警、无状态标记,失败不阻断主流程;SC-011 原措辞"100% 完成"与实现语义不符。
- **影响范围**: SC-011;User Story 4 验收场景 5。
- **回滚提示**: 若运维要求固化失败告警或重试,需新增告警/重试机制并同步更新 SC-011;若后续实现改为强一致(失败阻断),需重新校验 SC-012 调度时长(30 分钟)约束。

## AMB-006-09: wsCount/wafCount 与 wsYearCount/wafYearCount 的启用条件不一致

- **决策结论**: 在 spec 字段说明中区分两组:
  - `wsCount`/`wafCount`(当前次数)= 直接取维保状态查询结果,**不受**启用条件控制(反映已发生事实);
  - `wsYearCount`/`wafYearCount`(年次数)= 受 `warrantyGradeEnable`/`wafServiceEnable` 控制,未启用时为 0(反映配额);
  - 并在 Edge Cases 中修正"维保状态查询失败时 wsCount/wafCount 如何赋值"为"直接取维保状态查询结果;wsYearCount/wafYearCount 按启用条件归零"。
- **依据**: `ProjectMaintenanceVO.initWarrantyExtParams`(`ProjectMaintenanceVO.java:374-391`):`wsYearCount = warrantyGradeServiceEnable ? wsYearCount : 0`、`wafYearCount = wafServiceEnable ? wafYearCount : 0`,而 `wsCount`/`wafCount` 直接 `warrantyState.get(...)` 不受 enable 控制。
- **影响范围**: 实体 `pm_project_maintenance` 字段 wsCount/wafCount/wsYearCount/wafYearCount 说明;Edge Cases"维保状态查询失败"项;FR-2.2.3 季度交付提醒(count 与 yearCount 比对)。
- **回滚提示**: 若代码逻辑变更(如 `wsCount`/`wafCount` 也加 enable 控制),需重新校验字段说明;若属代码 bug(应统一受 enable 控制),需在缺陷跟踪中标注。

## AMB-006-10: pm_project_maintenance_view 视图 DDL 缺失

- **决策结论**: 在 spec 中明确"视图 DDL 由 DBA 维护,不在代码库;字段契约以 `select * from pm_project_maintenance_view m` 输出为准,与 `pm_project_maintenance` 基本一致";统一草稿与 spec 措辞为此结论。
- **依据**: grep `pm_project_maintenance_view` 在 SQL-map 中仅命中 `select * from pm_project_maintenance_view m`,无 CREATE VIEW 语句;草稿推断"可能由存储过程 `queryProjectMaintenanceInfo` 维护"为推测无证据。
- **影响范围**: 实体 `pm_project_maintenance_view`;FR-2.2.1 日报列表查询。
- **回滚提示**: 若 DBA 索取到视图 DDL,需补全字段表;若视图字段与 `pm_project_maintenance` 不一致(如含 projectMember/officeName 关联字段),需修正字段契约并评估日报渲染影响。

## AMB-006-11: 售前项目维保允许但督查 FR-2.3.5 称"售前督查不涉及",代码入口未显式拒绝售前

- **决策结论**: 在 FR-2.3.2 补充"仅售后项目(projectType=10)可录入督查记录,其他类型 MUST 拒绝"作为目标规格;在 FR-2.3.5 保留"售前督查不涉及,仅售后项目"表述,并新增"实现层提示:`SupervisionAction.createProjectSupervision` 入口当前仅校验 `projectId==0`,建议后端补 `projectType` 校验以确保售前项目被拒绝"。
- **依据**: spec FR-2.3.5 现有"售前督查不涉及"为业务意图;`SupervisionAction.createProjectSupervision`(`SupervisionAction.java:157-162`)入口仅校验 `project.getProjectId() == 0` 后返回 ERROR,无 `projectType` 校验,与 FR-2.1.2 维保允许售前(projectId=presalesId)的语义不对称。
- **影响范围**: FR-2.3.2;FR-2.3.5;User Story 3 督查录入边界。
- **回滚提示**: 若代码后续确认已有 `projectType` 校验(在 162 行之后),需移除"建议补校验"提示并补充校验位置;若确认无校验,需在缺陷跟踪中记录"售前项目可进入督查录入流程"为潜在缺陷。

---

## 决策汇总

| AMB 编号 | 子域 | 严重度 | 决策类型 |
|---|---|---|---|
| AMB-006-01 | 借用维保额度 | 阻塞性 | 不计入 MVP |
| AMB-006-02 | 借用维保额度 | 阻塞性 | 移除实体 |
| AMB-006-03 | 督查 | 重要 | 选定权威路径 |
| AMB-006-04 | 维保报表 | 阻塞性 | 补全 cron |
| AMB-006-05 | 维保报表 | 重要 | 命名对齐代码 |
| AMB-006-06 | 数据契约 | 一般 | 标注动态派生 |
| AMB-006-07 | 维保报表 | 一般 | 明确存储过程名 |
| AMB-006-08 | 维保报表 | 重要 | 修正 SC-011 语义 |
| AMB-006-09 | 维保次数派生 | 重要 | 区分两组字段启用条件 |
| AMB-006-10 | 数据契约 | 一般 | 标注 DBA 维护 |
| AMB-006-11 | 督查 | 重要 | 补 projectType 校验提示 |

> 歧义总数: 11;阻塞性 3(AMB-006-01/02/04);重要 5(AMB-006-03/05/08/09/11);一般 3(AMB-006-06/07/10)。
