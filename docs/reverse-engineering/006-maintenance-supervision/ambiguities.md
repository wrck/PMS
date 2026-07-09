# 006-maintenance-supervision 域歧义清单(Ambiguities)
> 日期: 2026-07-09
> 歧义总数: 11
> 比对范围: spec(`.specify/specs/006-maintenance-supervision/spec.md`)、草稿(`docs/reverse-engineering/006-maintenance-supervision/spec-draft.md`)、代码(`PMS-struts/`)与配置(`config/`、`config-ibaits/`)
> 比对维度: 代码 vs 文档、代码 vs 代码、spec 内部 [待澄清] 展开

---

## AMB-006-01: 借用维保额度子域 Action 实现类源文件缺失
- **位置**:FR-2.4.1 ~ FR-2.4.5;`struts-sys.xml:928-962`;spec 附录待澄清点 1
- **现象**:`struts-sys.xml` 在 928-962 行注册了 `LendMaintenanceAction`(class="LendMaintenanceAction"),含 `addQuota`/`isQuotaRepeat`/`seeQuota`/`updateQuota`/`deleteQuota` 五个方法,分别对应 5 个 FR。但全代码库 grep `LendMaintenance|addQuota|isQuotaRepeat|seeQuota|updateQuota|deleteQuota` 仅命中 `struts-sys.xml` 自身与 `docs/domain-map.md` 两处,**未找到 Action 实现类源文件,也未找到对应 Spring Bean 定义**。
- **候选解释**:
  1. 功能已废弃但路由未清理(spec 推测"已废弃、未实现");
  2. Action 由 Struts2 别名机制或 `BeanNameAutoProxyCreator` 解析,源文件名与 class 名不一致,实际由其他类承担;
  3. 源文件被 `.gitignore` 排除或在私有分支中;
  4. 运行时由容器动态代理注入。
- **影响面**:FR-2.4.1 ~ FR-2.4.5 全部子域;借用维保额度列表/新增/校验/查看/更新/删除六个操作均无实现可验证;对应数据库表 `lend_maintenance`/`lend_quota` 也无 SQL 映射命中(见 AMB-006-02);反推测试用例无法构造。
- **建议决策**:在 spec 中标记为"实现缺失,运行时可用性存疑,MVP 范围内不计入验收";如必须支持,需向项目维护方索取该 Action 源文件或确认是否已废弃;若确认废弃,应将 FR-2.4.x 全部移除或标记为 Deprecated。

---

## AMB-006-02: 借用维保额度数据库表(lend_maintenance/lend_quota/maintenance_daily_report)未在 SQL-map 中命中
- **位置**:实体"借用维保额度相关表 [待澄清]";spec 附录待澄清点 2
- **现象**:`domain-map.md` 列出三张表 `lend_maintenance`/`lend_quota`/`maintenance_daily_report`,但全 `PMS-struts/config-ibaits/` 下均未找到对应 resultMap/insert/select。仅找到 `pm_project_soleagent_lend_from_sms`(代理商借用从 SMS 同步落地表,字段含 soleAgentLendId/orderExecNumber 等),与"借用维保额度"概念命名差异大。
- **候选解释**:
  1. 三张表确属废弃,`domain-map.md` 残留;
  2. `pm_project_soleagent_lend_from_sms` 即"借用维保额度"实际表,只是命名不同;
  3. 表存在但 SQL 直接写在 Action/JSP 中,未走 iBATIS SQL-map;
  4. 表已迁移到其他模块的 SQL-map 但未在文档中标注。
- **影响面**:FR-2.4.x 全部子域的数据契约无法补全;`pm_project_soleagent_lend_from_sms` 字段是否承担借用维保额度业务无定论;若误用,会导致 MVP 数据迁移与回归测试方向错误。
- **建议决策**:与运维/DBA 确认 `lend_maintenance`/`lend_quota` 是否真实存在数据库;若不存在,在 spec 中移除该实体表并标注"借用维保额度子域因数据层缺失不计入 MVP";若存在,补全 SQL-map 与字段契约。

---

## AMB-006-03: 督查数据映射双份并存,运行时加载哪份未明确
- **位置**:实体 `pm_project_supervision` 来源说明;spec 附录待澄清点 3
- **现象**:`pm_project_supervision` 的 resultMap 与 CRUD(insert/list/count/insertOrUpdate/mapList)同时存在于:
  - `sql-map-project-config.xml`(行 5575/5590/5632/5637/5689/5740/5802/5818);
  - `sql-map-project-config2.xml`(行 4798/4813/4855/4860/4912/4963/5025/5041)。
  两份均包含 `selectProjectSupervisionMapList`(分别在 5802 行与 5025 行)。spec 仅标注"两者是否完全一致、运行时加载哪份需澄清",未给出结论。(注:`sql-map-work-config.xml:491` 仅 1 处引用 `pm_project_supervision ps`,为其他模块查询,非数据映射,不计入并存)
- **候选解释**:
  1. 两份为不同 iBATIS 加载顺序下的覆盖关系(后加载者覆盖前者),需查 `sql-map-config.xml` 总配置文件的 `<sqlMap>` 引入顺序;
  2. 两份对应不同 Action 调用入口(分别由 SupervisionAction/其他模块调用),各取所需;
  3. 历史遗留,第二份为重构残骸,运行时仅一份生效;
  4. 两份字段定义存在细微差异(如 `updateBy` 是否回写),需逐字段比对。
- **影响面**:FR-2.3.x 督查子域所有写入与查询的"事实来源"无法确定;若两份有字段差异,会出现"开发环境与生产环境行为不一致";SC-007 软删除规则验证依赖准确 SQL,需选取权威配置。
- **建议决策**:打开 `sql-map-config.xml` 确认实际加载的 sqlMap 文件清单;若两份均被加载,需逐字段比对 resultMap/insert/insertOrUpdate 的差异;若仅一份生效,在 spec 中标注权威路径并删除冗余引用。

---

## AMB-006-04: cron 表达式实际存在但 spec 标记为 [待澄清]
- **位置**:NFR-4.1.1 / NFR-4.1.3;Assumptions"维保日报调度的具体 cron 配置 [待澄清]";spec 附录待澄清点 5
- **现象**:spec/草稿均称"cron 表达式配置未在代码中找到 [待澄清]",但实际 `PMS-struts/config/beans-quartz.xml` 明确配置:
  - `MaintenanceDailyReportMailerTrigger`(303-312 行):cron = `00 00 05 * * ?`(每日 05:00:00);
  - `MaintenanceServiceQuarterMailerTrigger`(325-334 行):cron = `00 00 06 1 2/3 ?`(每季度中间月初 06:00,即 2/5/8/11 月 1 号);
  - `MaintenanceDepartmentSectaryJob`:`beans-quartz.xml` 中**无独立 trigger**,仅由 `MaintenanceDailyReportMailer.execute()` 内部调用 + `main` 方法(`MaintenanceDepartmentSectaryJob.java:44-46`)手动触发。
  另:存在多 profile 副本(`config/profiles/dev/`、`test/`、`release/`、`yfpms/` 各一份 `beans-quartz.xml`),cron 可能因 profile 不同而异。
- **候选解释**:
  1. spec 起草时未扫描 `config/beans-quartz.xml`(在 `config/` 而非 `config-ibaits/`),仅扫描了 SQL-map;
  2. 多 profile 副本 cron 可能不同,spec 无法判定哪份生效;
  3. spec 编写者认为调度配置属于"运维层"不属于"代码层",故标记 [待澄清]。
- **影响面**:NFR-4.1.1/4.1.3 的"触发"段落无法验收;SC-010 季度交付提醒的"调度执行后"无法定位具体时间点;若 yfpms profile 的 cron 与 release 不同,生产调度时点与文档描述会出现偏差。
- **建议决策**:从 spec 中移除 cron 缺失的 [待澄清] 标记;在 NFR-4.1.1 注明 release profile cron = 每日 05:00;在 NFR-4.1.3 注明每季度中间月初 06:00;在 NFR-4.1.2 注明"无独立 cron,由日报调度前调用,可由 main 手动触发";并核对四份 profile 是否一致。

---

## AMB-006-05: temp_maintenance_contractNo 临时表名拼写不一致(代码驼峰,文档全下划线)
- **位置**:实体 `temp_maintenance_contract_no` 来源说明;`MaintenanceDailyReportMailer.java:81/103`;`sql-map-maintenance-config.xml:1467-1487`
- **现象**:spec 中实体命名为 `temp_maintenance_contract_no`(全下划线),但 `sql-map-maintenance-config.xml:1467-1484` 的 DDL 实际创建表名为 `temp_maintenance_contractNo`(驼峰,`No` 大写、无下划线);`deleteTempMaintenanceContractNoTable`(1486-1487)同样引用驼峰名。代码与文档表名不一致。
- **候选解释**:
  1. 文档起草者按命名规范统一改成下划线,实际数据库为驼峰;
  2. 文档笔误,应为 `temp_maintenance_contractNo`;
  3. 实际有两张表,一份下划线一份驼峰,但 SQL-map 中只命中驼峰一份。
- **影响面**:实体定义与代码事实不符,后续根据 spec 写测试或迁移脚本时会用错表名;MySQL 在 Linux 下 `lower_case_table_names=0` 时 `temp_maintenance_contract_no` 与 `temp_maintenance_contractNo` 是两张不同表,SQL 会失败;SC-011 数据固化的依赖临时表正确性受影响。
- **建议决策**:将 spec 实体名改为 `temp_maintenance_contractNo` 与代码对齐;或要求 DBA 确认生产数据库的 `lower_case_table_names` 设置,统一命名规范。

---

## AMB-006-06: temp_project_warranty_state 字段定义来自子查询,spec 称"字段明细需查看 createTempProjectWarrantyStateTable 语句"但语句本身无字段定义
- **位置**:实体 `temp_project_warranty_state` 来源说明;`sql-map-maintenance-config.xml:1561-1571`
- **现象**:spec 称"字段明细需查看 `createTempProjectWarrantyStateTable` 语句",但 `sql-map-maintenance-config.xml:1561-1571` 的 DDL 为:
  ```sql
  CREATE TEMPORARY TABLE IF NOT EXISTS temp_project_warranty_state (
      KEY projectId (projectId),
      KEY warrantyStatus(warrantyStatus),
      KEY warrantyGrade(warrantyGrade),
      KEY wafService(wafService)
  ) AS
  <include refid="selectProjectWarrantyState"/>
  ...
  GROUP BY ph.projectCode;
  ```
  仅定义索引,字段集合来自 `selectProjectWarrantyState` 子查询动态派生,DDL 本身无静态字段定义。
- **候选解释**:
  1. 字段契约需展开 `selectProjectWarrantyState` SQL 片段才能确定;
  2. 字段集随 `selectProjectWarrantyState` 修改而变,运行时无固定契约,文档无法静态描述;
  3. spec 应改为引用 `selectProjectWarrantyState` 子查询输出列而非 `createTempProjectWarrantyStateTable`。
- **影响面**:FR-2.2.3/2.2.4 服务交付查询与季度提醒依赖该表字段(如 `<serviceCode>Enable` 标志),无法基于 spec 字段表编写测试;`MaintenanceServiceQuarterMailer.java:101` 引用 `serviceProject.get(serviceCode + "Enable")`,若 `selectProjectWarrantyState` 输出列变动,运行时 `NullPointerException`。
- **建议决策**:展开 `selectProjectWarrantyState` 子查询,在 spec 中补充"字段来源 = `selectProjectWarrantyState` 输出列 + projectId/warrantyStatus/warrantyGrade/wafService 索引列";或标注该表为"动态派生表,无静态字段契约,字段以子查询输出为准"。

---

## AMB-006-07: 数据固化流程 spec 用"数据固化流程"模糊表述,草稿明确为存储过程 queryProjectMaintenanceInfo(1)
- **位置**:FR-2.2.1 末段;SC-011;NFR-4.1.1 步骤 6
- **现象**:spec FR-2.2.1 与 SC-011 均称"调用数据固化流程进行每日数据固化",未指明载体;草稿 NFR-4.1.1 明确"调用存储过程 `queryProjectMaintenanceInfo(1)` 进行每日数据固化";`MaintenanceDailyReportMailer.java:113` 实际调用 `Call queryProjectMaintenanceInfo(1);`。**spec 文档比草稿更模糊,丢失了存储过程名**。
- **候选解释**:
  1. spec 编写者认为存储过程名是实现细节,刻意抽象为"数据固化流程";
  2. spec 编写者未读完代码,未捕获存储过程名;
  3. 该存储过程可能由 DBA 维护,代码侧无法验证,故未在 spec 中固化名称。
- **影响面**:SC-011"100% 调用数据固化流程"无法基于 spec 定位验证点;运维或测试需知道存储过程名才能监控执行;若存储过程存在但不生效,spec 无法描述排错路径。
- **建议决策**:在 spec FR-2.2.1 末段明确"调用存储过程 `queryProjectMaintenanceInfo(1)` 进行每日数据固化";并备注"存储过程 DDL 未纳入代码库,需向 DBA 索取";统一草稿与 spec 的措辞。

---

## AMB-006-08: SC-011"100% 调用数据固化"与代码 try-catch 包裹失败仅打印日志不符
- **位置**:SC-011;`MaintenanceDailyReportMailer.java:108-117`
- **现象**:SC-011 表述"调度完成后 MUST 100% 调用数据固化流程完成每日数据固化",但 `MaintenanceDailyReportMailer.java:110-117` 的数据固化调用被 try-catch 包裹,catch 块仅 `e.printStackTrace()`,**无重试、无告警、无状态标记**。固化失败时调度仍正常结束,且失败不阻断主流程。
- **候选解释**:
  1. SC-011 描述的是"调用"语义(尝试调用即算 100%),而非"固化成功";spec 措辞不严谨;
  2. SC-011 应改为"100% 触发数据固化调用",与代码语义一致;
  3. SC-011 期望"100% 固化成功",但代码未保证,属于实现与规格不符的缺陷。
- **影响面**:验收时若按 SC-011 字面"100% 完成"判定,会出现规格要求与实现不符的伪 bug;运维若依赖 SC-011 监控固化成功率,会漏报失败。
- **建议决策**:将 SC-011 改为"调度完成后 MUST 触发 `queryProjectMaintenanceInfo(1)` 调用;调用失败 MUST 记录异常日志但不阻断调度(失败重试策略待澄清)";或在 spec 中新增"固化失败告警"待澄清点。

---

## AMB-006-09: wsCount/wafCount 与 wsYearCount/wafYearCount 的启用条件不一致,spec 笼统称"按启用条件赋默认"
- **位置**:实体 `pm_project_maintenance` 字段 `wsCount`/`wafCount`/`wsYearCount`/`wafYearCount` 说明;NFR-4.4.2;Edge Cases
- **现象**:spec NFR-4.4.2 与 Edge Cases 均称"按维保级别/增值服务启用条件赋默认",但 `ProjectMaintenanceVO.initWarrantyExtParams`(`ProjectMaintenanceVO.java:374-391`)中:
  - `wsYearCount = warrantyGradeServiceEnable ? wsYearCount : 0`(**受** `warrantyGradeEnable` 控制);
  - `wafYearCount = wafServiceEnable ? wafYearCount : 0`(**受** `wafServiceEnable` 控制);
  - `wsCount = warrantyState.get("wsCount")`(**直接取,不受** enable 控制);
  - `wafCount = warrantyState.get("wafCount")`(**直接取,不受** enable 控制)。
  spec 未区分两组字段启用条件的差异。
- **候选解释**:
  1. spec 表述是概括,实际"次数"字段分两类:年次数受启用控制,当前次数不受;
  2. 代码 bug,`wsCount`/`wafCount` 应同样受 enable 控制;
  3. 业务上"当前次数"反映已发生事实(无论是否启用都应记录),"年次数"反映配额(未启用即配额为 0)。
- **影响面**:Edge Case"维保状态查询失败时 wsCount/wafCount 如何赋值"的预期答案与实际行为可能不一致;若按 spec 字面"按启用条件赋默认"实施,`wsCount`/`wafCount` 会在未启用时被错误清零;FR-2.2.3 季度交付提醒基于 count 字段比对 yearCount,逻辑误判风险。
- **建议决策**:在 spec 字段说明中区分两组:"`wsCount`/`wafCount` = 直接取维保状态查询结果(不受启用条件控制)";"`wsYearCount`/`wafYearCount` = 受 `warrantyGradeEnable`/`wafServiceEnable` 控制,未启用时为 0";并在 NFR-4.4.2 中明确该差异。

---

## AMB-006-10: pm_project_maintenance_view 视图 DDL 缺失,草稿与 spec 对维护方表述不一
- **位置**:实体 `pm_project_maintenance_view` 来源说明;FR-2.2.1
- **现象**:spec/草稿均称视图 DDL 未在配置中找到,**草稿**推断"可能由存储过程 `queryProjectMaintenanceInfo` 维护",**spec** 改为"可能由数据固化流程维护"。两份文档措辞略有差异,且均为推测无证据。grep `pm_project_maintenance_view` 在 SQL-map 中仅命中 `select * from pm_project_maintenance_view m`,**无 CREATE VIEW 语句**。
- **候选解释**:
  1. 视图由 DBA 在数据库层手动创建,DDL 不在代码库;
  2. 视图由存储过程 `queryProjectMaintenanceInfo` 内 `CREATE OR REPLACE VIEW` 动态维护;
  3. 视图与 `pm_project_maintenance` 同结构,仅做读优化或脱敏;
  4. 视图实际为物化视图或快照表,由数据固化流程刷新。
- **影响面**:FR-2.2.1 日报列表查询依赖该视图,字段契约无法基于 spec 静态描述;若视图字段与 `pm_project_maintenance` 不完全一致(例如包含 `projectMember`/`officeName` 等关联字段),日报邮件正文渲染会出错;草稿与 spec 措辞不一致导致文档自身歧义。
- **建议决策**:在 spec 中明确"视图 DDL 由 DBA 维护,不在代码库;字段契约以 `select *` 输出为准,与 `pm_project_maintenance` 基本一致";并向 DBA 索取视图 DDL 补全字段表;同时统一草稿与 spec 的措辞。

---

## AMB-006-11: 售前项目维保允许但督查 FR-2.3.5 称"售前督查不涉及",代码入口未显式拒绝售前
- **位置**:FR-2.1.2 / FR-2.3.2 / FR-2.3.5;实体 `pm_project_maintenance.projectId` 说明;`SupervisionAction.createProjectSupervision`
- **现象**:实体 `pm_project_maintenance.projectId` 说明为"售后=projectId,售前=presalesId,非业务/自定义=-1",即售前项目支持维保记录。但 FR-2.3.5 称"售前督查不涉及,仅售后项目";`SupervisionAction.createProjectSupervision`(`SupervisionAction.java:157-162`)入口仅校验 `project.getProjectId() == 0` 后返回 ERROR,**无 `projectType` 校验**。售前项目若用 presalesId 调用,理论上也能进入督查录入流程。
- **候选解释**:
  1. SupervisionAction 后续逻辑(录入页/督查问卷类型)对售前项目隐式失败,但 spec 未明示;
  2. 售前项目虽无督查,但路由层未拦截,UI 层不展示入口即可,后端不校验;
  3. spec 表述不严谨,督查对所有项目类型可录入,只是"售前督查"业务上不常用。
- **影响面**:FR-2.3.x 督查子域权限边界模糊;测试用例无法判定售前项目调用 `createProjectSupervision` 应返回什么;与 FR-2.1.2 维保允许售前的语义不对称。
- **建议决策**:打开 `SupervisionAction.createProjectSupervision` 全文确认是否有 `projectType` 校验;若有,在 FR-2.3.2 补充"仅售后项目(projectType=10)可录入,其他类型拒绝";若无,在 FR-2.3.5 中移除"售前督查不涉及"表述,或补一条"建议后端补 projectType 校验"的待澄清点。

---

## 附:歧义分布统计
| 子域 | 歧义数 | 编号 |
|---|---|---|
| 借用维保额度 | 2 | AMB-006-01, AMB-006-02 |
| 督查 | 2 | AMB-006-03, AMB-006-11 |
| 维保报表(定时调度) | 4 | AMB-006-04, AMB-006-05, AMB-006-07, AMB-006-08 |
| 数据契约(临时表/视图) | 2 | AMB-006-06, AMB-006-10 |
| 维保次数派生 | 1 | AMB-006-09 |

| 严重度 | 编号 |
|---|---|
| 阻塞性(影响 MVP 验收) | AMB-006-01, AMB-006-02, AMB-006-04 |
| 重要(影响测试用例编写) | AMB-006-03, AMB-006-05, AMB-006-08, AMB-006-09, AMB-006-11 |
| 一般(措辞/文档一致性) | AMB-006-06, AMB-006-07, AMB-006-10 |
