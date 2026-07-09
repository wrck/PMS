# 008-external-integration 域歧义正向固化(Clarify)

> 日期: 2026-07-09
> 固化范围: `008-external-integration` 分支 spec.md 中的 16 条 AMB 歧义
> 固化原则: 采纳每条 AMB 的"建议决策"作为最终决策,清除 spec 中所有 `[待澄清]` 标记
> 决策类型说明:
> - **最终决策**:代码证据充分,可立即落地的决策
> - **暂定决策**:方向已定,但需业务确认或新系统调研后落地的决策,spec 中以 `[暂定决策:...]` 标注

---

## AMB-008-01: GainOrderByERP 中 SAP 同步调用被注释禁用,与 FR-01a 冲突

- **决策结论**: [暂定决策] 保留 SAP 同步能力为条件性需求。新系统通过独立 SAP 同步 Job 实现(非 GainOrderByERP 内注释路径),FR-01a 维持 MUST 但标注为"条件性需求:依赖 SAP 系统在线状态"。若业务确认 SAP 已下线,则降级移除 FR-01a 及相关 Acceptance。默认假设:SAP 仍需同步。
- **依据**: 代码 `GainOrderByERP.java:25-26` 注释禁用 `syncOrderFormSAP`,但 spec User Story 1 / FR-01a / Key Entities 中 SAP 视图契约完整存在,贸然移除将破坏外部契约文档完整性;`GainOrderBySAP.java` 标注 `@Deprecated` 但保留完整逻辑,表明 SAP 同步能力仍有保留意图。
- **影响范围**: FR-01a、FR-01d、User Story 1 Acceptance 1/2、SC-015、`pm_order_data_from_erp_sap` 镜像表、`selectOrderInfoFromERP` 合并视图。
- **回滚提示**: 若业务确认 SAP 已下线,需将 FR-01a 降级移除,FR-01d 改为"D365 单源 + 历史 SAP 数据保留",并标注 `pm_order_data_from_erp_sap` 为只读历史表。

---

## AMB-008-02: selectOrderInfoFromERP 依赖 pm_order_data_from_erp_sap,SAP 同步禁用导致合并视图数据陈旧

- **决策结论**: [暂定决策] 与 AMB-008-01 一致。FR-01d 维持"SAP 与 D365 来源订单 UNION ALL 合并"语义,但 SAP 来源数据刷新依赖独立 SAP 同步 Job(见 AMB-008-01 决策)。若 SAP 同步 Job 未启用,`pm_order_data_from_erp_source` 中 `source='SAP'` 分支为历史快照数据,需在合并视图注释中标注。
- **依据**: `sql-map-refresh-data-common-config.xml:1022-1035` 的 UNION ALL 设计明确包含 SAP 分支;SAP 数据陈旧风险源于同步 Job 禁用而非合并逻辑缺陷。
- **影响范围**: FR-01d、`pm_order_data_from_erp_source`、`pm_project_product_line` 重建、`pm_project_product_line_real` 三步匹配、发货状态更新。
- **回滚提示**: 若 SAP 不再同步,FR-01d 改为 D365 单源,并明确 `pm_order_data_from_erp_sap` 的清理策略(保留历史或 truncate 清空)。

---

## AMB-008-03: GainOrderBySAP 日志 dataFrom="ERP" 与 spec 枚举值 SAP 不一致

- **决策结论**: 最终决策。统一采用 spec 枚举值,新系统日志 `dataFrom` 字段 MUST 使用 `"SAP"`(而非代码中的 `"ERP"`)。spec 枚举保持 SAP/D365/OA/EHR/ITR/SMS/License/PMS,不补充 "ERP"。
- **依据**: spec `fnd_data_refresh_log.dataFrom` 枚举已明确定义不含 "ERP";`GainOrderBySAP.java:72` 硬编码 `"ERP"` 为历史命名不一致;SC-013 要求按 dataFrom 模糊查询,枚举统一是验收前提。
- **影响范围**: `fnd_data_refresh_log.dataFrom` 枚举、SC-013 日志可观测性、`GainOrderBySAP` 新系统实现。
- **回滚提示**: 无需回滚;若业务确认 SAP 与 ERP 为不同物理系统且需区分,再在枚举中补充 "ERP"。

---

## AMB-008-04: 执行单号 J→X 替换使用 String.replace 全局替换,可能误伤非版本位字符

- **决策结论**: 最终决策。新系统实现 MUST 改为按版本位(第 13 位)精确替换:`substring(0,12) + "X" + substring(13)`,不得使用 `String.replace("J", "X")` 全局替换。spec NFR-09/SC-010 明确"J 替换仅作用于第 13 位版本字符"。
- **依据**: SC-010 的 `orderExecNumberShort = CONCAT(LEFT(,12), SUBSTR(,14))` 表明版本字符位于第 13 位;`String.replace` 会误伤项目编码段中合法的 "J" 字符;代码 `GainOrderBySAP.java:86`、`GainPrjPropertyBySMS.java:76`、`GainPrjRealProjectLineBySMS.java:60` 均使用全局替换,存在匹配错乱风险。
- **影响范围**: NFR-09、SC-010、FR-01b、FR-04b、FR-05a、Edge Cases 执行单号含版本字符。
- **回滚提示**: 若业务确认执行单号除第 13 位外不会出现 "J",可回退为全局替换,但需在 Assumptions 中补充该前提。

---

## AMB-008-05: PlanGetBySMS 不继承 AbstractSynchronizeTask,SC-003/SC-013 失败日志与回滚要求无法满足

- **决策结论**: 最终决策。spec FR-07 明确"PlanGetBySMS 新系统实现 MUST 接入统一同步日志框架(继承 AbstractSynchronizeTask 或 SynchronizeJob),并补齐事务回滚";SC-003 MUST 适用于所有同步任务无例外(含 PlanGetBySMS)。
- **依据**: `PlanGetBySMS.java:22-124` 直接使用原始 JDBC,catch 块仅 `e.printStackTrace()`,无 `conn.rollback()`、无 `fnd_data_refresh_log` 写入;spec SC-003/SC-013 为强制约束,不应有例外;Key Entities 注释已指出该问题,新系统必须改造。
- **影响范围**: FR-07、SC-003、SC-013、NFR-03、`pm_pb_plan_from_sms`、`pm_project_task.eventPlanHappenDate` 一致性。
- **回滚提示**: 无需回滚;新系统必须改造,不允许保留原始 JDBC 无日志无回滚的实现。

---

## AMB-008-06: PlanGetBySMS Statement 混用 addBatch 与 executeQuery,执行顺序与事务边界不清

- **决策结论**: 最终决策。新系统实现 MUST 改为参数化查询 + 批量更新,明确事务边界与回滚。spec FR-07c 补充实现约束:"查询与更新解耦,先收集事件映射再批量 update `pm_project_task.eventPlanHappenDate`"。
- **依据**: `PlanGetBySMS.java:69-113` 循环内 `addBatch`(insert)与 `executeQuery`(select)混用,ResultSet 资源泄漏,catch 块无 rollback;与 SC-003 事务一致性矛盾。
- **影响范围**: FR-07c、SC-003、SC-014(SQL 注入风险)。
- **回滚提示**: 无需回滚;新系统必须采用参数化查询 + 批量更新模式。

---

## AMB-008-07: SystemContext.enableCrm() 实现已明确(读 sys.crm.api.config.enable),spec 仍标 [待澄清]

- **决策结论**: 最终决策。解除 [待澄清]。`enableCrm()` 读取系统参数表 `sys.crm.api.config` 的 `enable` 字段(布尔,默认 false),true 时走 CRM API 路径,false 时走 SMS 数据库视图路径。
- **依据**: `SystemContext.java:178-180` 代码明确:`Boolean.parseBoolean(String.valueOf(getCrmConfig().getOrDefault("enable", false)))`;这是配置开关,非动态判定。
- **影响范围**: SC-005、NFR-04、FR-03f、Assumptions 双路径切换开关。Key Entities 补充 `sys.crm.api.config` 参数契约。
- **回滚提示**: 无需回滚;代码证据确凿。

---

## AMB-008-08: t_sync_log.syncType 枚举实际已定义(SyncType.FULL_SYNC=1/INCREM_SYNC=2),spec 误标 [待澄清]

- **决策结论**: 最终决策。解除 [待澄清]。`t_sync_log.syncType` 枚举值:1=FULL_SYNC(全量同步),2=INCREM_SYNC(增量同步),见 `core/src/main/java/com/dp/plat/core/schedule/SyncType.java:3-5`。
- **依据**: `SyncType.java` 明确定义 `FULL_SYNC((short) 1, "full_sync", "全量同步")`、`INCREM_SYNC((short) 2, "increm_sync", "增量同步")`;`SynchronizeJob.java:124` 写入 SyncLog;`SyncLog.syncType` 为 `Short` 类型与 SMALLINT 一致。
- **影响范围**: Key Entities t_sync_log 表、Assumptions syncType 枚举定义、SC-013 日志语义化查询。
- **回滚提示**: 无需回滚;代码证据确凿。

---

## AMB-008-09: fnd_data_refresh_log vs t_sync_log 双日志体系并存,字段语义不一致且无下线计划

- **决策结论**: 最终决策。新系统统一采用 `t_sync_log` 模型;`fnd_data_refresh_log` 标记为"废弃,只读保留"。SC-013 适用表明确为 `t_sync_log`。旧表保留只读用于历史日志查询,不再写入。
- **依据**: 新版 core 模块 `SynchronizeJob` 写 `t_sync_log`,字段更完整(targetMethod/tableObject/syncType/isSuccess/dataCount);旧版 struts 模块 `AbstractSynchronizeTask` 写 `fnd_data_refresh_log`,字段命名与状态语义不一致;过渡期并存但新系统应统一到 t_sync_log。
- **影响范围**: SC-013、Key Entities fnd_data_refresh_log / t_sync_log、Assumptions 日志体系过渡。
- **回滚提示**: 若新系统仍需兼容旧 struts 任务,需保留 fnd_data_refresh_log 写入能力,但验收以 t_sync_log 为准。

---

## AMB-008-10: 多步任务前序失败无补偿机制,SC-012 标注 [待澄清] 但代码已确认无补偿

- **决策结论**: 最终决策。解除 [待澄清]。新系统 MUST 沿用现状(各子步骤独立事务,前序失败不阻断后序),SHOULD 提供补偿任务手动重试失败步骤。不强制全任务级自动补偿机制。
- **依据**: `GainOrderByERP.java:23-50` 的 5 个子步骤各自独立 try/catch,无补偿/回滚前序逻辑;代码行为已明确无补偿;业务可接受前序失败的不一致(影响交付追溯但不阻塞主流程)。
- **影响范围**: SC-012、NFR-11、Edge Cases 多步同步任务前序子步骤失败、Assumptions 多步任务补偿假设。
- **回滚提示**: 若业务不可接受不一致,需提升为 MUST 并设计补偿机制(重试前序失败步骤、标记脏数据)。

---

## AMB-008-11: 动态 ALTER TABLE 自检逻辑仅 GainPrjPropertyBySMS 实现,其他任务是否需要 [待澄清]

- **决策结论**: 最终决策。解除 [待澄清]。动态 ALTER TABLE 自检仅适用于 SMS 项目属性表 `pm_project_property_from_sms`(由 `GainPrjPropertyBySMS` 触发,检查 `serviceTypeName`/`channelName` 字段);其他镜像表字段变更通过手工迁移,不要求通用自检逻辑。
- **依据**: `GainPrjPropertyBySMS.java:46-61` 仅检查两个字段;其他 Gain* 任务(GainOrderByERP、GainPersonByEHR、GainDataFromITR 等)未发现类似逻辑;SMS 项目属性表存在字段演进需求,其他表结构稳定。
- **影响范围**: NFR-10、SC-011、FR-04a、Assumptions 外部系统视图稳定性。
- **回滚提示**: 若其他外部系统视图频繁新增字段,可提升为通用要求并补充各任务的字段清单。

---

## AMB-008-12: cron 表达式配置位置未在 spec 中明确,[待澄清] 阻塞 SC-002 验收

- **决策结论**: [暂定决策] cron 表达式配置在 Spring/Quartz XML 配置文件(如 `applicationContext-quartz.xml` 或独立 properties 文件)中,代码中不硬编码(已确认 job 目录无 `@Scheduled`/cron 匹配)。新系统 MUST 明确调度器选型(内置 Quartz 或外置调度器),并将 cron 表达式外置到独立配置文件。具体配置文件路径待新系统调研 `applicationContext*.xml` / `quartz*.xml` / `scheduler*.xml` 后确认。
- **依据**: 所有 Gain*/Push* 类实现 `org.quartz.Job` 接口,无 `@Scheduled` 注解;struts job 目录 Grep "cron|@Scheduled|scheduler" 无匹配,确认配置在代码外;但具体 XML 文件未在本次扫描范围内定位。
- **影响范围**: SC-002、NFR-02、Assumptions 调度器外置。
- **回滚提示**: 新系统调研确认具体配置文件路径后,将 [暂定决策] 升级为最终决策并补充文件路径示例。

---

## AMB-008-13: D365/FP clientSecret 加密存储要求标 [待澄清],代码未见加密实现

- **决策结论**: 最终决策。解除 [待澄清]。`clientSecret` MUST 使用可逆加密存储(如 AES/JASYPT),应用读取时解密;密钥通过独立密钥管理流程分发,不得与配置同库明文存储。
- **依据**: `D365Api.java` 与 `FPApi.java` 通过 `SystemContext.getConfig` 读取 JSON 配置直接使用 clientSecret,未见解密逻辑,当前为明文存储;存在凭据泄露风险;SC-006 安全验收要求加密。
- **影响范围**: SC-006、NFR-05、Assumptions 配置参数表存在、`sys.d365.api.config`、`sys.fp.api` 参数契约。
- **回滚提示**: 若加密方案影响配置动态生效(需重启解密),需评估 JASYPT 透明加密方案;密钥管理流程需与运维确认。

---

## AMB-008-14: SAP 视图字段 u_sordertype 拼写疑似错误(sordertype vs sortertype)

- **决策结论**: [暂定决策] spec 外部契约表中标注"字段名以 SAP 视图实际定义为准,可能为 `u_sordertype` 或 `u_sortertype`"。spec 与代码当前一致使用 `u_sordertype`,新系统对接前 MUST 与 SAP 外部系统管理员确认 `DP_V_SO_ORDER_4_PMS` 实际字段名。
- **依据**: `sql-map-refresh-data-sap-config.xml:18` 使用 `u_sordertype`,spec 与代码一致;但语义为"销售类型"(sales type),常见拼写应为 `u_sortertype`,存在外部系统命名歧义。
- **影响范围**: Key Entities SAP 订单视图(外部契约)表、`sql-map-refresh-data-sap-config.xml`。
- **回滚提示**: 与 SAP 管理员确认后,若实际为 `u_sortertype`,需同步更新 spec 与 SQL-map;若为 `u_sordertype`,维持现状。

---

## AMB-008-15: GainOrderBySAP 标 @Deprecated 但保留完整 SAP 同步逻辑,新系统 SAP 能力边界不清

- **决策结论**: [暂定决策] 与 AMB-008-01 一致。保留 SAP 同步能力,新系统通过独立 SAP 同步 Job 实现并清理 `GainOrderBySAP` 废弃类。`GainOrderBySAP` 在新系统中 MUST NOT 保留,其逻辑迁移至独立 Job。
- **依据**: `GainOrderBySAP.java:29-33` 标注 `@Deprecated` 但保留完整 work() 实现;`GainOrderByERP` 注释禁用 SAP 路径;两者并存导致能力边界不清。
- **影响范围**: User Story 1、FR-01、NFR-09、`GainOrderBySAP` 类清理。
- **回滚提示**: 若业务确认 SAP 已下线,移除 SAP 相关 FR/Acceptance,不保留任何 SAP 同步类。

---

## AMB-008-16: SAP 订单行镜像表无 lineType 字段,但 FR-01 要求按 orderType/lineType 区分销售与退货

- **决策结论**: [暂定决策] 与 AMB-008-01/02 一致。保留 SAP 同步能力时,`lineType` 由 `selectOrderLineFromSAP` 的 UNION ALL 赋值(SO 分支 `0 AS lineType`,RMA 分支 `1 AS lineType`)。spec Key Entities 表 `pm_order_line_from_erp_sap` 的 `lineType` 字段(0=销售,1=退货)维持。
- **依据**: `sql-map-refresh-data-sap-config.xml:90-102` 的 `insert_pm_order_line` 含 lineType 列;`GainOrderBySAP.java:105/151` 硬编码 lineType=0/1;`selectOrderLineFromSAP`(line 173-188)通过 UNION ALL 合并 SO(0)与 RMA(1)。
- **影响范围**: Key Entities pm_order_line_from_erp_sap 表、FR-01、`pm_order_line_from_erp_source` 合并视图。
- **回滚提示**: 若 SAP 不再同步,标注 `pm_order_line_from_erp_sap` 为只读历史表,lineType 字段为历史数据。

---

## 汇总

| ID | 决策类型 | 关联 [待澄清] | 关键结论 |
|---|---|---|---|
| AMB-008-01 | 暂定决策 | - | 保留 SAP 同步为条件性需求,独立 Job 实现 |
| AMB-008-02 | 暂定决策 | - | FR-01d 维持 UNION ALL,SAP 来源依赖独立 Job |
| AMB-008-03 | 最终决策 | - | dataFrom 统一使用 "SAP" |
| AMB-008-04 | 最终决策 | - | J→X 按第 13 位版本位精确替换 |
| AMB-008-05 | 最终决策 | - | PlanGetBySMS MUST 接入统一日志框架 + 回滚 |
| AMB-008-06 | 最终决策 | - | 参数化查询 + 批量更新,查询与更新解耦 |
| AMB-008-07 | 最终决策 | SC-005 enableCrm | 解除:读 sys.crm.api.config.enable |
| AMB-008-08 | 最终决策 | syncType 枚举 | 解除:1=FULL_SYNC, 2=INCREM_SYNC |
| AMB-008-09 | 最终决策 | 双日志下线 | 解除:统一 t_sync_log,fnd_data_refresh_log 废弃只读 |
| AMB-008-10 | 最终决策 | SC-012 多步补偿 | 解除:沿用现状 + SHOULD 补偿任务 |
| AMB-008-11 | 最终决策 | NFR-10 动态 ALTER | 解除:仅 pm_project_property_from_sms 自检 |
| AMB-008-12 | 暂定决策 | SC-002 cron 配置 | Spring/Quartz XML 外置,路径待调研 |
| AMB-008-13 | 最终决策 | SC-006 clientSecret | 解除:MUST 可逆加密(AES/JASYPT) |
| AMB-008-14 | 暂定决策 | - | 字段名以 SAP 视图实际定义为准 |
| AMB-008-15 | 暂定决策 | - | 清理 GainOrderBySAP,逻辑迁移至独立 Job |
| AMB-008-16 | 暂定决策 | - | lineType 由 UNION ALL 赋值,维持 spec |

> 最终决策:9 条(AMB-008-03/04/05/06/07/08/09/10/11/13)
> 暂定决策:7 条(AMB-008-01/02/12/14/15/16,及 AMB-008-01 关联)
> spec [待澄清] 清除:9 处(SC-002、SC-005、SC-006、SC-011、SC-012、t_sync_log syncType、Assumptions 日志体系过渡、Assumptions syncType 枚举、Assumptions 多步任务补偿)
