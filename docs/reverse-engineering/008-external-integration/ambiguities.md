# 008-external-integration 域歧义清单(Ambiguities)

> 日期: 2026-07-09
> 歧义总数: 16
> 比对范围: `008-external-integration` 分支 spec.md / spec-draft.md vs 当前工作区代码(PMS-struts / PMS-ext-d365 / core)
> 比对维度: 代码 vs 文档、代码 vs 代码、spec 内部 [待澄清] 展开

---

## AMB-008-01: GainOrderByERP 中 SAP 同步调用被注释禁用,与 FR-01a 直接冲突

- **位置**:
  - spec: `FR-01a`、`User Story 1`、`SC-015`
  - 代码: `PMS-struts/src/com/dp/plat/job/GainOrderByERP.java:23-50`(work() 方法,line 25-26 `syncOrderFormSAP(params)` 被注释)
- **现象**:
  - spec FR-01a 强制要求:"系统 MUST 通过定时调度触发订单同步任务,从 SAP 视图同步销售订单(SO)与退货订单(RMA),按账套(company_code)区分"。
  - spec User Story 1 Acceptance 1/2 假设 SAP 视图数据被同步到 `pm_order_data_from_erp_sap`。
  - 但 `GainOrderByERP.work()` 实际代码中 `syncOrderFormSAP(params)` 调用被注释掉,只执行 D365 同步、ERP 合并、拆分总代、更新设备清单、更新发货状态 5 个子步骤。
  - `GainOrderBySAP` 类虽 `@Deprecated` 但仍保留完整 SAP 同步逻辑,新旧系统行为边界不清。
- **候选解释**:
  1. SAP 系统已下线/迁移至 D365,SAP 同步功能有意禁用,spec 未同步更新。
  2. 临时注释用于调试或灰度切换,正式环境仍需启用 SAP 同步。
  3. SAP 同步改由其他 Job 类(非 GainOrderByERP)触发,spec 未记录。
- **影响面**: 订单合并视图 `pm_order_data_from_erp_source` 中 `source='SAP'` 分支将基于陈旧或空数据;User Story 1 Acceptance 1/2 无法通过;SC-015 数据一致性约束受影响。
- **建议决策**: 明确 SAP 数据源当前是否仍需同步。若已下线,在 spec 中标注 FR-01a 为"条件性需求"并移除相关 Acceptance;若仍需,恢复 `syncOrderFormSAP` 调用或在新系统中独立实现 SAP 同步 Job,并补充调度配置说明。

---

## AMB-008-02: selectOrderInfoFromERP 依赖 pm_order_data_from_erp_sap,但 SAP 同步被禁用导致合并视图数据陈旧

- **位置**:
  - spec: `FR-01d`、`Key Entities - pm_order_data_from_erp_source`
  - 代码: `sql-map-refresh-data-common-config.xml:1022-1035`(`selectOrderInfoFromERP`)、`GainOrderByERP.java:25-26`
- **现象**:
  - `selectOrderInfoFromERP` 通过 `UNION ALL` 合并 `pm_order_data_from_erp_d365`(source='D365')与 `pm_order_data_from_erp_sap`(source='SAP')写入 `pm_order_data_from_erp_source`。
  - FR-01d 要求"将 SAP 与 D365 来源订单 UNION ALL 合并"。
  - 但由于 AMB-008-01 中 SAP 同步被注释,`pm_order_data_from_erp_sap` 不会被刷新,合并视图的 SAP 分支将一直返回历史快照数据,而 `pm_order_data_from_erp_source` 每次 `truncate` 后重新 UNION ALL,导致 SAP 数据"看似被同步"但实为旧数据。
- **候选解释**:
  1. 系统默认依赖外部手工或另一定时任务维护 `pm_order_data_from_erp_sap`,spec 未记录该任务。
  2. 合并视图实际应只包含 D365 数据,SAP 分支为历史遗留,spec 应剔除 SAP 部分。
- **影响面**: 后续 `pm_project_product_line` 重建、`pm_project_product_line_real` 三步匹配、发货状态更新均依赖合并视图,SAP 来源数据陈旧会污染项目设备清单。
- **建议决策**: 与 AMB-008-01 一并决策。若 SAP 不再同步,FR-01d 应改为"D365 单源 + 历史 SAP 数据保留",并明确 `pm_order_data_from_erp_sap` 的清理策略。

---

## AMB-008-03: GainOrderBySAP 日志 dataFrom="ERP" 与 spec 枚举值 SAP 不一致

- **位置**:
  - spec: `Key Entities - fnd_data_refresh_log` 表,dataFrom 字段"枚举值: SAP/D365/OA/EHR/ITR/SMS/License/PMS"
  - 代码: `PMS-struts/src/com/dp/plat/job/GainOrderBySAP.java:72`(`paramMap.put("dataFrom", "ERP")`)
- **现象**:
  - spec 明确 dataFrom 枚举不含 "ERP",但 `GainOrderBySAP` 写入日志时 dataFrom 硬编码为 `"ERP"`。
  - spec FR-01a 描述该任务为"从 SAP 视图同步",但日志标记来源为 ERP,语义错位。
  - SC-013 要求"支持按 dataFrom 模糊查询",若用 "SAP" 过滤将查不到 `GainOrderBySAP` 的日志。
- **候选解释**:
  1. SAP 视图位于 ERP 系统下,开发时按物理来源标记为 ERP,枚举应补充 "ERP"。
  2. 代码 bug,应改为 "SAP"。
  3. SAP 与 ERP 是同一系统不同叫法,spec 与代码命名未统一。
- **影响面**: 同步日志按来源筛选失真;日志可观测性 SC-013 受影响;新系统日志枚举值定义需要决策。
- **建议决策**: 统一来源命名。建议采用 spec 枚举,将代码改为 "SAP",或在 spec 枚举中补充 "ERP" 并明确 SAP/ERP 关系。

---

## AMB-008-04: 执行单号 J→X 替换使用 String.replace 全局替换,可能误伤非版本位字符

- **位置**:
  - spec: `NFR-09`、`SC-010`、`Edge Cases - 执行单号含版本字符 J/Y/X`
  - 代码: `GainOrderBySAP.java:86`(`orderBean.getOrderExecNumber().replace("J", "X")`)、`GainPrjPropertyBySMS.java:76`、`GainPrjRealProjectLineBySMS.java:60`
- **现象**:
  - spec NFR-09 描述"执行单号 orderExecNumber 中的 J MUST 统一替换为 X",结合 SC-010 的 `orderExecNumberShort = CONCAT(LEFT(,12), SUBSTR(,14))` 可推断版本字符位于第 13 位。
  - 但代码使用 `String.replace("J", "X")`,会替换字符串中所有 "J" 字符,而非仅第 13 位版本字符。
  - 若执行单号其他位置(如项目编码段)合法包含 "J",将被误替换为 "X",导致与本地数据匹配失败。
- **候选解释**:
  1. 业务上执行单号除第 13 位外不会出现 "J",全局替换等价于按位替换,代码实现可行。
  2. 代码实现不严谨,应改为按位置替换(如 `substring(0,12) + "X" + substring(13)`)。
- **影响面**: 若执行单号非版本位出现 "J",会导致订单/项目属性/设备清单匹配错乱;SC-010 的 `orderExecNumberShort` 生成也会基于错误数据。
- **建议决策**: 新系统实现时改为按版本位(第 13 位)精确替换,并在 spec 中明确"J 替换仅作用于第 13 位版本字符"。若业务确认其他位置不会出现 J,需在 spec Assumptions 中补充该前提。

---

## AMB-008-05: PlanGetBySMS 不继承 AbstractSynchronizeTask,SC-003/SC-013 失败日志与回滚要求无法满足

- **位置**:
  - spec: `SC-003`、`SC-013`、`NFR-03`、`FR-07`、`Key Entities - pm_pb_plan_from_sms` 注释
  - 代码: `PMS-struts/src/com/dp/plat/job/PlanGetBySMS.java:22-124`
- **现象**:
  - spec SC-003 强制要求:"失败时 MUST 回滚事务并写入完整堆栈到 refreshException;同步后(无论成功/失败)MUST 更新 refreshTo 与状态字段"。
  - spec SC-013 要求每次同步后 `fnd_data_refresh_log` 或 `t_sync_log` 记录开始/结束时间/状态/异常堆栈。
  - 但 `PlanGetBySMS` 类不继承 `AbstractSynchronizeTask`,直接使用原始 JDBC,`catch (ClassNotFoundException e)` 与 `catch (SQLException e)` 仅 `e.printStackTrace()`,既不回滚事务(catch 块无 `conn.rollback()`),也不写入 `fnd_data_refresh_log`。
  - spec Key Entities 表注释虽提到"PlanGetBySMS 不通过 AbstractSynchronizeTask,直接 JDBC",但 FR-07 与 SC-003 的强制约束未对此豁免。
- **候选解释**:
  1. PlanGetBySMS 是历史遗留实现,新系统必须改造为继承 AbstractSynchronizeTask 或等效日志机制(spec NFR-13/SC-014 已暗示需改造)。
  2. PlanGetBySMS 故意豁免日志要求,仅靠 println 调试,spec 应明确豁免。
- **影响面**: 收款计划同步失败时无日志可查,运维无法定位;SC-003/SC-013 可观测性在该任务上失效;`pm_pb_plan_from_sms` 与 `pm_project_task.eventPlanHappenDate` 可能处于不一致状态(部分 insert 已 executeBatch,部分未执行,且无回滚)。
- **建议决策**: 在 spec FR-07 中明确"PlanGetBySMS 新系统实现 MUST 接入统一同步日志框架(继承 AbstractSynchronizeTask 或 SynchronizeJob),并补齐事务回滚";SC-003 应明确适用于所有同步任务无例外。

---

## AMB-008-06: PlanGetBySMS dataSource() 中 Statement 混用 addBatch 与 executeQuery,执行顺序与事务边界不清

- **位置**:
  - spec: `FR-07c`、`SC-003`
  - 代码: `PlanGetBySMS.java:69-113`
- **现象**:
  - 代码中 `Statement cs = conn.createStatement()` 后,在 `while(smsRs.next())` 循环内先 `cs.addBatch(spmssql)`(insert),紧接着 `cs.executeQuery(queryeventname)`(select),再 `cs.addBatch(updatesql)`(update)。
  - `addBatch` 累积的语句直到循环外 `cs.executeBatch()`(line 112)才统一执行,而 `executeQuery` 是立即执行。
  - 这意味着:(a) insert 语句在循环结束时才真正执行,但 `executeQuery` 查询 `fnd_basic_data` 时本地表数据未变(不影响该查询);(b) `executeQuery` 返回的 ResultSet 在下一次循环迭代被覆盖,存在资源泄漏风险;(c) `addBatch` 的 update 语句依赖 `executeQuery` 结果,但 batch 执行顺序与查询时机错配。
  - spec FR-07c 描述"按事件名查 fnd_basic_data,取得 dataTypeCode/basicDataId 后批量更新 pm_project_task.eventPlanHappenDate",与代码"循环内查询 + addBatch update"模式不完全对应。
- **候选解释**:
  1. 代码逻辑可运行:addBatch 累积 insert/update,executeQuery 独立查询,executeBatch 一次性提交,语义上 correct 但低效。
  2. 代码存在 bug:update 语句在 executeBatch 时执行,但其参数已通过字符串拼接固化,与查询结果一致,可工作。
- **影响面**: 性能差(逐条查询);ResultSet 资源泄漏;若中途异常,batch 未执行,但 conn 也未 rollback(catch 块无 rollback),与 SC-003 矛盾。
- **建议决策**: 新系统实现改为参数化查询 + 批量更新,明确事务边界与回滚;spec FR-07c 应补充"查询与更新解耦,先收集事件映射再批量 update"的实现约束。

---

## AMB-008-07: SystemContext.enableCrm() 实现已明确(读 sys.crm.api.config.enable),spec 仍标 [待澄清]

- **位置**:
  - spec: `SC-005`、`NFR-04`、`FR-03f`、`Assumptions - 双路径切换开关`
  - 代码: `PMS-struts/src/com/dp/plat/context/SystemContext.java:178-180`
- **现象**:
  - spec SC-005 标注"`enableCrm` 切换条件 [待澄清]",Assumptions 写"假设 SystemContext.enableCrm() 的切换逻辑由系统配置决定;切换条件见 SC-005"。
  - 实际代码:`enableCrm()` 返回 `Boolean.parseBoolean(String.valueOf(getCrmConfig().getOrDefault("enable", false)))`,即读取 `sys.crm.api.config` 系统参数 JSON 的 `enable` 字段,默认 false。
  - `getCrmConfig()` 还会合并 `sys.crm.api.config.routers` 路由配置。
  - 这是配置开关,不是基于 SMS/CRM 迁移进度的动态判定。
- **候选解释**:
  1. spec 编写时未读取 SystemContext 代码,[待澄清] 可直接解除:切换条件为 `sys.crm.api.config.enable` 配置项。
- **影响面**: spec [待澄清] 阻塞 SC-005 验收;新系统实现者无法判断切换逻辑。
- **建议决策**: 解除 [待澄清]。在 spec SC-005 明确:"enableCrm() 读取系统参数表 `sys.crm.api.config` 的 `enable` 字段(布尔,默认 false),true 时走 CRM API 路径,false 时走 SMS 数据库视图路径";并在 Key Entities 补充 `sys.crm.api.config` 参数契约。

---

## AMB-008-08: t_sync_log.syncType 枚举实际已定义(SyncType.FULL_SYNC=1/INCREM_SYNC=2),spec 误标 [待澄清]

- **位置**:
  - spec: `Key Entities - t_sync_log` 表(syncType 注释"枚举值待澄清")、`Assumptions - syncType 枚举定义`
  - 代码: `core/src/main/java/com/dp/plat/core/schedule/SyncType.java:3-5`、`SynchronizeJob.java:124`(`new SyncLog(className + ".execute", syncType.getCode(), syncType.getType())`)、`SyncLog.java:38`(`private Short syncType`)
- **现象**:
  - spec 明确写"`t_sync_log.syncType` 类型为 SMALLINT,具体枚举值未在代码中显式定义 [待澄清]"。
  - 但 core 模块已定义 `SyncType` 枚举:`FULL_SYNC((short) 1, "full_sync", "全量同步")`、`INCREM_SYNC((short) 2, "increm_sync", "增量同步")`。
  - `SynchronizeJob` 构造时传入 SyncType,写入 SyncLog;`SyncLog.syncType` 为 `Short` 类型,与 SMALLINT 一致。
- **候选解释**:
  1. spec 编写时仅扫描 struts 模块,未覆盖 core 模块的 SyncType 枚举,[待澄清] 可解除。
- **影响面**: 新系统实现者可能重复定义枚举;日志筛选无法按 syncType 语义化查询。
- **建议决策**: 解除 [待澄清]。在 spec Key Entities - t_sync_log 表中补充 syncType 枚举:"1=FULL_SYNC(全量同步), 2=INCREM_SYNC(增量同步)";并引用 `SyncType.java` 作为证据。

---

## AMB-008-09: fnd_data_refresh_log vs t_sync_log 双日志体系并存,字段语义不一致且无下线计划

- **位置**:
  - spec: `SC-013`、`Key Entities - fnd_data_refresh_log / t_sync_log`、`Assumptions - 日志体系过渡`、`spec-draft 附:歧义点汇总 1`
  - 代码: `AbstractSynchronizeTask.java`(写 fnd_data_refresh_log)、`SynchronizeJob.java`(写 t_sync_log)、`SyncLogMapper.xml`、`sql-map-refresh-data-common-config.xml:159-185`
- **现象**:
  - 旧版 struts 任务写 `fnd_data_refresh_log`(字段:refreshTaskName/refreshFrom/refreshTo/refreshState/refreshException),refreshState 语义"1=成功,空=失败"。
  - 新版 core 任务写 `t_sync_log`(字段:targetMethod/tableObject/syncStartTime/syncEndTime/isSuccess/dataCount/syncType/exception),isSuccess 语义"0/1"。
  - 两套日志字段命名、状态语义、时间字段均不一致;SC-013 要求"支持按 targetMethod/tableObject/dataFrom/dataTo 模糊查询分页",但旧表无 targetMethod/tableObject 字段,新表无 dataFrom/dataTo 字段(新表有 dataFrom/dataTo,见 spec 表定义)。
  - spec Assumptions 标注"是否计划下线 fnd_data_refresh_log [待澄清]"。
- **候选解释**:
  1. 过渡期两套并存,新系统最终统一到 t_sync_log,旧表保留只读。
  2. 两套日志服务于不同模块(struts vs core),长期并存,spec 应明确各自适用范围。
- **影响面**: 日志查询需要跨表;SC-013 验收标准对两套表均需满足;新系统日志模型选型未定。
- **建议决策**: 明确下线 fnd_data_refresh_log 的时间表与迁移策略;新系统统一采用 t_sync_log 模型,并在 spec 中标注 fnd_data_refresh_log 为"废弃,只读保留"。SC-013 应明确适用表为 t_sync_log。

---

## AMB-008-10: 多步任务前序失败无补偿机制,SC-012 标注 [待澄清] 但代码已确认无补偿

- **位置**:
  - spec: `SC-012`、`NFR-11`、`Edge Cases - 多步同步任务前序子步骤失败`、`Assumptions - 多步任务补偿假设`
  - 代码: `GainOrderByERP.java:23-50`(work 方法,各子方法独立 try/catch,无补偿)
- **现象**:
  - spec SC-012:"多步任务前序失败可能导致数据不一致(如订单同步失败但发货状态更新成功);是否需要全任务级补偿机制 [待澄清]"。
  - 代码 `GainOrderByERP.work()` 中 5 个子步骤(syncOrderFormD365/syncOrderFormERP/splitSoleAgentLendOrderInfo/updateProjectProductLine/UpdateShipmentState)各自独立 try/catch,前序失败仅记录日志,不阻断后序,也无任何补偿/回滚前序的逻辑。
  - spec Edge Cases 与 Assumptions 均标注 [待澄清],但代码行为已明确:无补偿。
- **候选解释**:
  1. 业务可接受前序失败的不一致,新系统沿用现状,无需补偿。
  2. 新系统需引入补偿机制(如重试前序失败步骤、标记脏数据),spec 应明确要求。
- **影响面**: 订单同步失败但设备清单/发货状态更新成功,会导致项目设备清单与订单数据不一致,影响交付追溯。
- **建议决策**: 解除 [待澄清]。建议在 spec SC-012 中明确"新系统 MUST 沿用现状(各子步骤独立事务,前序失败不阻断后序),并 SHOULD 提供补偿任务手动重试失败步骤";若业务不可接受,需提升为 MUST 并设计补偿机制。

---

## AMB-008-11: 动态 ALTER TABLE 自检逻辑仅 GainPrjPropertyBySMS 实现,其他任务是否需要 [待澄清]

- **位置**:
  - spec: `NFR-10`、`SC-011`、`FR-04a`、`Assumptions - 外部系统视图稳定性`
  - 代码: `GainPrjPropertyBySMS.java:46-61`(仅检查 serviceTypeName/channelName 字段)
- **现象**:
  - spec NFR-10:"是否所有同步任务都有此自检逻辑,还是仅 GainPrjPropertyBySMS [待澄清]"。
  - spec SC-011:"是否所有同步任务都有此自检逻辑,还是仅 GainPrjPropertyBySMS [待澄清]"。
  - 代码中仅 `GainPrjPropertyBySMS` 实现了动态 `ALTER TABLE ADD` 自检,其他 Gain* 任务(如 GainOrderByERP、GainPersonByEHR、GainDataFromITR)未发现类似逻辑。
- **候选解释**:
  1. 仅 SMS 项目属性表存在字段演进需求,其他表结构稳定,无需自检。
  2. 其他任务应补充自检逻辑,spec 应要求所有镜像表同步任务支持动态字段。
- **影响面**: 若其他外部系统视图新增字段,对应镜像表需手工迁移,违反 NFR-10"动态表结构演进"的通用性。
- **建议决策**: 解除 [待澄清]。建议在 spec NFR-10/SC-011 中明确"动态 ALTER TABLE 自检仅适用于 SMS 项目属性表(pm_project_property_from_sms),其他镜像表字段变更通过手工迁移";或提升为通用要求并补充各任务的字段清单。

---

## AMB-008-12: cron 表达式配置位置未在 spec 中明确,[待澄清] 阻塞 SC-002 验收

- **位置**:
  - spec: `SC-002`、`NFR-02`、`Assumptions - 调度器外置`
  - 代码: `PMS-struts/src/com/dp/plat/job/*.java`(实现 Quartz `Job` 接口,无 `@Scheduled` 注解)、`AbstractSynchronizeTask.java:78-85`(execute 入口)
- **现象**:
  - spec SC-002:"具体 cron 表达式在哪里配置 [待澄清]"。
  - 代码中所有 Gain*/Push* 类实现 `org.quartz.Job` 接口,但 cron 表达式未在 Java 代码中硬编码,应在外部 Spring/Quartz 配置文件(如 `applicationContext.xml`、`quartz.properties` 或 JobDetail/CronTrigger bean 定义)中配置。
  - spec Assumptions 假设"定时调度器外置,所有同步任务的 cron 表达式由调度配置文件管理",但未指明文件位置。
  - 当前工作区 struts job 目录下 Grep "cron|@Scheduled|scheduler" 无匹配,确认配置在代码外。
- **候选解释**:
  1. cron 配置在 Spring XML(如 `applicationContext-quartz.xml`)或独立 properties 文件,spec 应引用该文件。
  2. 使用外置调度器(如 Jenkins/Airflow/XXL-Job)通过 HTTP 触发 main 方法,无 Spring 配置。
- **影响面**: SC-002 验收无法落地;新系统调度方案选型(内置 Quartz vs 外置调度器)未定。
- **建议决策**: 调查 `applicationContext*.xml` / `quartz*.xml` / `scheduler*.xml` 配置文件,在 spec SC-002 中明确 cron 配置文件路径与示例;新系统应明确调度器选型。

---

## AMB-008-13: D365/FP clientSecret 加密存储要求标 [待澄清],代码未见加密实现

- **位置**:
  - spec: `SC-006`、`NFR-05`、`Assumptions - 配置参数表存在`
  - 代码: `PMS-ext-d365/src/main/java/com/dp/plat/pms/extend/d365/util/D365Api.java`、`FPApi.java`(从 `sys.d365.api.config` / `sys.fp.api` JSON 直接读取 clientSecret)
- **现象**:
  - spec SC-006:"敏感字段(clientSecret)应做加密存储 [待澄清]"。
  - spec NFR-05:"敏感字段(clientSecret)应做加密存储 [待澄清]"。
  - spec Assumptions:"敏感字段加密存储要求见 SC-006"。
  - 代码中 `D365Api` 与 `FPApi` 通过 `SystemContext.getConfig` 读取 JSON 配置,直接使用 clientSecret 字段,未见解密逻辑(基于本次扫描范围)。
- **候选解释**:
  1. 当前明文存储,新系统必须引入加密(如 JASYPT/AES),spec 应提升为 MUST。
  2. 加密在数据库层或 Arg 实体层处理,API 层透明,需进一步确认。
- **影响面**: clientSecret 明文存储于系统参数表,存在凭据泄露风险;SC-006 安全验收无法通过。
- **建议决策**: 解除 [待澄清]。建议在 spec SC-006 中明确"clientSecret MUST 使用可逆加密存储(如 AES/JASYPT),应用读取时解密";并指定加密方案与密钥管理流程。

---

## AMB-008-14: SAP 视图字段 u_sordertype 拼写疑似错误(sordertype vs sortertype)

- **位置**:
  - spec: `Key Entities - SAP 订单视图(外部契约)` 表(`u_sordertype | VARCHAR | 销售类型 | EC`)
  - 代码: `sql-map-refresh-data-sap-config.xml:18`(`<result property="salesType" column="u_sordertype"/>`)、line 143
- **现象**:
  - spec 与代码均使用 `u_sordertype`(sordertype),但语义为"销售类型"(sales type),常见拼写应为 `u_sortertype`(sortertype)。
  - 该字段为 SAP 视图外部契约字段,spec 与代码一致,但与命名惯例不符。
- **候选解释**:
  1. SAP 视图实际字段名就是 `u_sordertype`(外部系统定义的拼写错误,需保持一致)。
  2. spec 与代码均拼写错误,实际应为 `u_sortertype`,需与 SAP 侧确认。
- **影响面**: 若实际 SAP 视图字段为 `u_sortertype`,则 SQL 查询会失败;若为 `u_sordertype`,则 spec/代码正确但命名歧义。
- **建议决策**: 与 SAP 外部系统管理员确认视图 `DP_V_SO_ORDER_4_PMS` 的实际字段名,在 spec 外部契约表中明确标注"字段名以 SAP 视图实际定义为准,可能为 u_sordertype 或 u_sortertype"。

---

## AMB-008-15: GainOrderBySAP 标 @Deprecated 但保留完整 SAP 同步逻辑,新系统 SAP 能力边界不清

- **位置**:
  - spec: `User Story 1`、`FR-01`、`NFR-09`
  - 代码: `GainOrderBySAP.java:29-33`(`@Deprecated` 注解 + 完整 work() 实现)
- **现象**:
  - `GainOrderBySAP` 类标注 `@Deprecated` 并注释"已经废弃合并至 GainOrderByERP",但 `work()` 方法仍保留完整的 SAP 同步逻辑(query_DP_V_SO_ORDER_4_PMS、insert_pm_order_data、拆分总代、更新设备清单等)。
  - `GainOrderByERP` 虽合并了 SAP 调用入口,但实际注释掉了 `syncOrderFormSAP`,且 `GainOrderByERP` 中无 SAP 同步的 SQL-map 调用(仅调用 `syncData("OrderInfoFromSAP", "SAP", params)`,但该方法也被注释)。
  - spec User Story 1 Acceptance 假设 SAP 同步生效,但代码实际依赖 `GainOrderBySAP`(已废弃)或 `GainOrderByERP`(注释禁用),能力边界不清。
- **候选解释**:
  1. GainOrderBySAP 仍作为独立 Job 被 Quartz 调度,`GainOrderByERP` 是新入口但未启用 SAP 路径,两者并存。
  2. GainOrderBySAP 完全废弃,新系统不再同步 SAP,spec 应移除 SAP 相关需求。
- **影响面**: 新系统是否实现 SAP 同步、采用哪个入口、是否保留 GainOrderBySAP 类,均未明确。
- **建议决策**: 与 AMB-008-01 一并决策。明确 SAP 同步在新系统中的去留:若保留,指定唯一入口并清理废弃类;若移除,更新 spec 删除 SAP 相关 FR/Acceptance。

---

## AMB-008-16: SAP 订单行镜像表无 lineType 字段,但 FR-01 要求按 orderType/lineType 区分销售与退货

- **位置**:
  - spec: `Key Entities - pm_order_line_from_erp_sap` 表(含 lineType 字段,"0=销售,1=退货")、`FR-01`
  - 代码: `sql-map-refresh-data-sap-config.xml:90-102`(`insert_pm_order_line` 含 lineType 字段)、`GainOrderBySAP.java:105/151`(`paramMap.put("lineType", 0/1)`)
- **现象**:
  - spec Key Entities 表 `pm_order_line_from_erp_sap` 明确包含 `lineType` 字段(0=销售,1=退货)。
  - 代码 `insert_pm_order_line` SQL 中确实包含 lineType 列,通过 `paramMap.put("lineType", 0)` 或 `1` 传入。
  - 但 `GainOrderBySAP` 中 SO 与 RMA 分两次同步,每次硬编码 lineType=0 或 1,而 `GainOrderByERP` 中 SAP 同步被注释,新入口下 lineType 如何赋值未明确。
  - 同时 `selectOrderLineFromSAP`(sql-map line 173-188)通过 `UNION ALL` 将 SO(0) 与 RMA(1) 合并,但 `GainOrderByERP` 注释掉 SAP 同步后,该合并逻辑不被调用。
- **候选解释**:
  1. SAP 同步禁用后,`pm_order_line_from_erp_sap` 不再刷新,lineType 字段为历史数据,新系统不依赖。
  2. 新系统应恢复 SAP 同步,lineType 通过 SQL `UNION ALL` 的 `0 AS lineType / 1 AS lineType` 赋值。
- **影响面**: 订单行销售/退货区分依赖 lineType,SAP 部分若不刷新,合并视图 `pm_order_line_from_erp_source` 的 SAP 分支 lineType 失效。
- **建议决策**: 与 AMB-008-01/02 一并决策。若恢复 SAP 同步,明确 lineType 由 `selectOrderLineFromSAP` 的 UNION ALL 赋值;若不恢复,在 spec 中标注 `pm_order_line_from_erp_sap` 为只读历史表。

---

## 汇总

| ID | 类型 | 严重度 | 关联 [待澄清] |
|---|---|---|---|
| AMB-008-01 | 代码 vs 文档 | 高 | - |
| AMB-008-02 | 代码 vs 代码 | 高 | - |
| AMB-008-03 | 代码 vs 文档 | 中 | - |
| AMB-008-04 | 代码 vs 文档 | 中 | - |
| AMB-008-05 | 代码 vs 文档 | 高 | - |
| AMB-008-06 | 代码 vs 代码 | 中 | - |
| AMB-008-07 | spec 内部 | 低 | SC-005 enableCrm |
| AMB-008-08 | spec 内部 | 低 | syncType 枚举 |
| AMB-008-09 | spec 内部 | 中 | 双日志体系下线 |
| AMB-008-10 | spec 内部 | 中 | SC-012 多步补偿 |
| AMB-008-11 | spec 内部 | 低 | NFR-10 动态 ALTER |
| AMB-008-12 | spec 内部 | 中 | SC-002 cron 配置 |
| AMB-008-13 | spec 内部 | 高 | SC-006 clientSecret 加密 |
| AMB-008-14 | 代码 vs 文档 | 低 | - |
| AMB-008-15 | 代码 vs 代码 | 中 | - |
| AMB-008-16 | 代码 vs 代码 | 中 | - |

> 关键歧义(最高优先级):AMB-008-01(SAP 同步禁用与 FR-01a 冲突)、AMB-008-05(PlanGetBySMS 失败日志与回滚缺失)、AMB-008-13(clientSecret 明文存储安全风险)。
> 可立即解除的 [待澄清]:AMB-008-07(enableCrm 配置开关)、AMB-008-08(syncType 枚举已定义)。
