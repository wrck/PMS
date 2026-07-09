# Feature Specification: 008-external-integration(外部集成)

**Feature Branch**: `008-external-integration`

**Created**: 2026-07-09

**Status**: Draft

**Source**: 逆向反推自 PMS-struts job + ext 模块

## User Scenarios & Testing *(mandatory)*

<!--
  本域覆盖 PMS 与外部系统(SAP/D365/CRM/OA/EHR/ITR/SMS/License/FP)的双向数据同步:
  - 拉取(Gain*):从外部系统视图/表同步至本地镜像表,刷新项目主表/成员/设备清单等业务表。
  - 推送(Push*):从本地业务表推送至外部系统 API(D365 采购单/收货/合同验收、FP 发票查验)。
  优先级原则:P1=项目交付基石数据(订单/人员/项目属性);P2=补充业务能力(售前/设备/市场/收款/ITR/License);P3=外部系统双向推送(扩展模块)。
-->

### User Story 1 - 自动从 SAP/ERP 同步销售订单与退货订单 (Priority: P1)

**As a** 项目经理,**I want** 系统自动从 SAP/ERP 同步销售订单和退货订单数据,**so that** 项目交付基于最新订单信息。

- 证据:`PMS-struts/src/com/dp/plat/job/GainOrderBySAP.java:34`、`GainOrderByERP.java:17`
- 说明:涵盖销售订单(SO)与退货订单(RMA),按账套(company_code)区分;`GainOrderBySAP` 已废弃合并至 `GainOrderByERP`(见 `GainOrderBySAP.java:29-33` `@Deprecated` 注释)。

**Why this priority**:订单是项目交付的源头数据,所有项目设备清单、发货状态、合同验收均依赖订单同步,属于 MVP 必备能力。

**Independent Test**:可独立验证——给定 SAP 视图 `DP_V_SO_ORDER_4_PMS` / `DP_V_RMA_ORDER_4_PMS` 中存在 N 条订单,执行同步后,本地 `pm_order_data_from_erp_sap` 应包含这 N 条订单,且 `orderExecNumber` 中的 `J` 已被替换为 `X`。

**Acceptance Scenarios**:

1. **Given** SAP 视图 `DP_V_SO_ORDER_4_PMS` 含 100 条销售订单且含 5 条执行单号带 `J` 的订单,**When** 订单同步任务执行一次,**Then** 本地 `pm_order_data_from_erp_sap` 含 100 条记录,且 5 条记录的 `orderExecNumber` 中 `J` 已被替换为 `X`,`orderType=0`。
2. **Given** SAP 视图 `DP_V_RMA_ORDER_4_PMS` 含 20 条退货订单,**When** 订单同步任务执行一次,**Then** 本地 `pm_order_data_from_erp_sap` 含对应 20 条 `orderType=1` 的记录。
3. **Given** SAP 同步过程中数据库异常,**When** 同步任务执行,**Then** 本地表数据回滚至同步前状态,`fnd_data_refresh_log` 写入一条 `refreshState` 为空的失败记录且 `refreshException` 含完整堆栈。
4. **Given** `UpdateShipmentState` 子任务异常,**When** 主任务 `GainOrderByERP` 执行,**Then** 订单同步主流程不中断,异常仅记录日志。

---

### User Story 2 - 自动从 D365 同步订单数据 (Priority: P1)

**As a** 项目经理,**I want** 系统自动从 D365 同步订单和订单行数据,**so that** 多 ERP 来源的订单在 PMS 中合并可用。

- 证据:`GainOrderByERP.java:75-90`(`syncOrderFormD365`)、`sql-map-refresh-data-d365-config.xml:6-43`
- 说明:D365 来源订单独立存储于 `pm_order_data_from_erp_d365`,后续在 `selectOrderInfoFromERP` 中与 SAP 来源做 UNION ALL 合并。

**Why this priority**:D365 是另一主 ERP 来源,与 SAP 订单合并后才能形成完整的订单视图,支撑后续项目设备清单重建。

**Independent Test**:可独立验证——给定 D365 视图含 N 条订单,执行同步后,`pm_order_data_from_erp_d365` 含 N 条记录,且 `pm_order_data_from_erp_source` 中 `source='D365'` 的记录数为 N。

**Acceptance Scenarios**:

1. **Given** D365 视图 `DPtech_V_SO_SALES_ORDER_4_PMS` 含 50 条订单,**When** D365 同步子任务执行,**Then** `pm_order_data_from_erp_d365` 含 50 条记录,且 `customInfo` 字段保留 D365 扩展信息。
2. **Given** SAP 与 D365 各有订单已同步至本地镜像表,**When** 合并视图 `selectOrderInfoFromERP` 执行,**Then** `pm_order_data_from_erp_source` 包含两源 UNION ALL 结果,每条记录带 `source` 标记(`SAP`/`D365`)和 `orderExecNumberShort`(去版本号执行单号)。
3. **Given** D365 订单行含 `realOrderExecNumber` 字段,**When** 同步完成,**Then** `pm_order_line_from_erp_d365` 保留该字段,合并视图 `pm_order_line_from_erp_source` 也保留该字段。

---

### User Story 3 - 自动从 OA/EHR 同步员工信息 (Priority: P1)

**As a** 项目经理,**I want** 系统自动从 EHR 同步有效/失效员工信息,**so that** 项目成员和销售关联的工号保持最新;离职销售关联的项目成员自动失效。

- 证据:`GainPersonByEHR.java:22`、`GainPersonByOA.java:30`(`@Deprecated`,已迁移至 EHR)
- 说明:同步有效员工(`staff_status=1`)覆盖本地人员表;同步失效员工(`staff_status=0`)→ 临时表批量更新项目成员的 `effectiveTo`,把离职销售从项目成员中失效。

**Why this priority**:项目成员和销售责任人是项目主数据的核心组成,离职销售自动失效直接影响项目权限与责任人跟进。

**Independent Test**:可独立验证——给定 EHR 视图含 200 条有效员工和 5 条失效员工,同步后 `pm_person_from_oa` 含 200 条记录,且 5 名离职销售关联的 `pm_project_member.effectiveTo` 被置为当前时间。

**Acceptance Scenarios**:

1. **Given** EHR 视图 `view_person_info_4_pms` 中 `staff_status=1` 的有效员工 200 条,**When** 人员同步任务执行,**Then** `pm_person_from_oa` 被 `truncate` 后插入这 200 条记录。
2. **Given** EHR 视图中 `staff_status=0` 的失效员工 5 条,且这 5 名员工在 `pm_project_member` 中作为销售关联了 12 个项目,**When** 同步完成,**Then** 这 12 条项目成员记录的 `effectiveTo` 被置为当前时间。
3. **Given** EHR 视图为空,**When** 同步执行,**Then** `pm_person_from_oa` 被清空但同步日志状态为成功。

---

### User Story 4 - 自动从 SMS 同步售前借货信息 (Priority: P2)

**As a** 项目经理,**I want** 系统自动从 SMS 同步售前测试借货信息(借货主信息、产品配置、借转销、核销、收发日期),**so that** 售前测试项目的核销状态可追踪。

- 证据:`GainPresalesInfoBySMS.java:28`
- 说明:支持双路径——传统数据库视图同步(`dataSource()`)与新版 CRM API 同步(`api()` 调用 `GainDataFromCRM`),由 `SystemContext.enableCrm()` 切换。

**Why this priority**:售前借货是售前项目追踪的补充能力,核销状态影响售前项目结项,但不阻塞主流程订单交付。

**Independent Test**:可独立验证——给定 SMS 视图含 M 条借货主信息和 N 条借货产品,同步后 `pm_presales_lend_info_from_sms` 含 M 条、`pm_presales_lend_product_from_sms` 含 N 条,且新 lendInfoId 被插入 `pm_presales_project_header`。

**Acceptance Scenarios**:

1. **Given** SMS 视图 `v_lend_info_4_pms` 含 30 条借货主信息,其中 5 条 lendInfoId 不在 `pm_presales_project_header` 中,**When** 同步执行,**Then** `pm_presales_lend_info_from_sms` 含 30 条,且 `pm_presales_project_header` 新增 5 条记录。
2. **Given** 借转销视图 `v_lend_products_tosale_4_pms` 含数据,**When** 同步完成,**Then** `pm_presales_project_header.hasTransfer` 被正确更新为借转销标记。
3. **Given** `SystemContext.enableCrm()` 返回 true,**When** 同步任务执行,**Then** 走 CRM API 路径而非 SMS 数据库视图路径。

---

### User Story 5 - 自动从 OA 同步临时授权信息 (Priority: P2)

**As a** 项目经理,**I want** 系统自动从 OA 同步临时授权主信息和明细行,并据此创建售前测试项目主表,**so that** 临时授权流程转化为可追踪的售前项目。

- 证据:`GainPresalesInfoFromOA.java:14`、`sql-map-refresh-data-oa-config.xml:6-179`
- 说明:从 OA 视图 `V_DP_TEMP_AUTH_INFO`、`V_DP_TEMP_AUTH_DETAIL` 拉取,落入 `pm_presales_lend_info_from_oa`、`pm_presales_lend_detail_from_oa`,再通过 `insertPresalesHeaderFormOA` 将 OA 来源数据合并插入 `pm_presales_project_header`。

**Why this priority**:OA 临时授权是售前项目的另一来源,与 SMS 借货互补,共同构成售前项目主表。

**Independent Test**:可独立验证——给定 OA 视图含 K 条临时授权主信息,同步后 `pm_presales_lend_info_from_oa` 含 K 条,且合并插入 `pm_presales_project_header` 时源标记 `source='OA'`。

**Acceptance Scenarios**:

1. **Given** OA 视图 `V_DP_TEMP_AUTH_INFO` 含 10 条临时授权主信息,**When** 同步执行,**Then** `pm_presales_lend_info_from_oa` 含 10 条,`pm_presales_lend_detail_from_oa` 含对应明细行。
2. **Given** OA 临时授权信息含 `processStartTime`、`applyDate`、附件路径字段,**When** 合并插入 `pm_presales_project_header`,**Then** 优先级 `pls > pps > plio` 生效,源标记 `source='OA'`。

---

### User Story 6 - 自动从 SMS 同步项目属性与销售信息 (Priority: P1)

**As a** 项目经理,**I want** 系统自动从 SMS 同步项目属性(市场、系统、行业、办事处、服务类型、渠道、销售责任人、最终客户、代理、项目级别等),**so that** 项目主表的服务类型/渠道/客户名/销售/公司等字段随 SMS 改单实时更新。

- 证据:`GainPrjPropertyBySMS.java:29`、`sql-map-refresh-data-sms-config.xml:40-104`
- 说明:支持 SMS 视图与 CRM API 双路径;同步后会执行多步 SQL 更新 `pm_project_header` 的 `column012/column013/customerProjectName/majorProjectLevel/compId`、`pm_project_related_party` 的代理信息、`pm_project_member` 的销售变更。

**Why this priority**:项目属性(销售、客户、市场维度)是项目主表的核心字段,直接影响项目权限、统计与责任人跟进,属于基石数据。

**Independent Test**:可独立验证——给定 SMS 视图含 K 条项目属性,同步后 `pm_project_property_from_sms` 含 K 条,且 `pm_project_header` 的服务类型/最终客户/销售字段被更新。

**Acceptance Scenarios**:

1. **Given** SMS 视图 `v_prj_property_4_pm` 含 80 条项目属性,**When** 同步执行,**Then** `pm_project_property_from_sms` 含 80 条,执行单号 `J`→`X` 转换生效。
2. **Given** SMS 中某项目的销售工号发生变化,**When** 同步完成,**Then** `pm_project_member` 中旧销售记录的 `effectiveTo` 被置当前时间,新销售记录被插入。
3. **Given** `pm_project_property_from_sms` 表缺少 `serviceTypeName` 字段,**When** 同步任务首次执行,**Then** 系统动态 `ALTER TABLE ADD` 添加 `serviceTypeName varchar(10)` 和 `channelName varchar(255)` 字段后再同步。

---

### User Story 7 - 自动从 SMS 同步项目真实设备清单 (Priority: P2)

**As a** 项目经理,**I want** 系统自动从 SMS 同步项目真实订单设备清单并匹配到本地项目,**so that** 项目实际发货设备清单可追溯。

- 证据:`GainPrjRealProjectLineBySMS.java:28`、`sql-map-refresh-data-common-config.xml:477-590`
- 说明:同步后通过执行单号(去版本、忽略 X/J/Y 类型)三步匹配插入 `pm_project_product_line_real`。

**Why this priority**:真实设备清单是项目交付追溯的补充能力,不阻塞主流程,但对售后/ License 管理有价值。

**Independent Test**:可独立验证——给定 SMS 视图 `view_refer_product` 含数据,同步后 `pm_project_product_line_real` 通过三步匹配规则插入对应记录。

**Acceptance Scenarios**:

1. **Given** SMS 视图 `view_refer_product` 含 200 条设备记录,**When** 同步执行,**Then** `pm_project_real_product_line_from_sms` 含 200 条,执行单号 `J`→`X` 转换生效。
2. **Given** 同步完成且本地存在 `pm_order_data_from_sap`、`pm_project_contract` 等关联表,**When** 三步匹配规则执行,**Then** `pm_project_product_line_real` 按"完全匹配 → 去版本号匹配 → 去版本号且忽略 X/J/Y 类型匹配"顺序插入。

---

### User Story 8 - 自动从 SMS 同步市场关系维度 (Priority: P2)

**As a** 项目经理,**I want** 系统自动从 SMS 同步市场/系统/支出/行业四维联动关系,**so that** 项目维度选择有最新选项。

- 证据:`GainMarketRelationsBySMS.java:29`、`sql-map-refresh-data-sms-config.xml:356-379`

**Why this priority**:市场关系维度是项目维度选择的配置数据,影响新建项目时的可选项,但不阻塞已有项目交付。

**Independent Test**:可独立验证——给定 SMS 视图 `view_market_system_expend_industry` 含数据,同步后 `pm_project_market_relations_from_sms` 全量刷新。

**Acceptance Scenarios**:

1. **Given** SMS 视图 `view_market_system_expend_industry` 含 50 条四维关系,**When** 同步执行,**Then** `pm_project_market_relations_from_sms` 被 `truncate` 后插入这 50 条记录,字段含 marketCode/Name、systemCode/Name、expendCode/Name、industryCode/Name。

---

### User Story 9 - 自动从 SMS 同步收款计划 (Priority: P2)

**As a** 财务人员,**I want** 系统自动从 SMS 同步合同收款计划,**so that** 项目任务表的计划完成时间随财务事件更新。

- 证据:`PlanGetBySMS.java:22`、`PlanGetBySMS.java:59-110`
- 说明:从 SMS 视图 `v_sms_pb_plan` 同步至 `pm_pb_plan_from_sms`,并按事件名匹配 `fnd_basic_data` 后更新 `pm_project_task` 的 `eventPlanHappenDate`。

**Why this priority**:收款计划影响项目任务的时间节点,但属于财务/任务管理层面的补充数据,不阻塞订单交付主流程。

**Independent Test**:可独立验证——给定 SMS 视图 `v_sms_pb_plan` 含 K 条收款计划,同步后 `pm_pb_plan_from_sms` 含 K 条,且 `pm_project_task.eventPlanHappenDate` 被按事件名匹配更新。

**Acceptance Scenarios**:

1. **Given** SMS 视图 `v_sms_pb_plan` 含 40 条收款计划,**When** 同步执行,**Then** `pm_pb_plan_from_sms` 含 40 条记录,`createBy/updateBy='admin'`、`effectiveFrom='2015-05-01'`。
2. **Given** 收款计划的 `referenceEventName` 在 `fnd_basic_data` 中匹配到事件,**When** 同步完成,**Then** 对应 `pm_project_task.eventPlanHappenDate` 被更新,`visibleFlag=1`。

---

### User Story 10 - 自动从 ITR 同步问题单 (Priority: P2)

**As a** 客服主管,**I want** 系统自动从 ITR 同步问题单(incident)信息,**so that** 项目可关联展示 ITR 问题单的受理/处理/解决进展。

- 证据:`GainDataFromITR.java:14`、`sql-map-refresh-data-itr-config.xml:6-84`
- 说明:同步源表 `pms_incident`,落入 `pm_project_incident_table_from_itr`,字段含工单状态、责任人、受理人、处理人、问题级别、RMA 单号、Case 类型等 30+ 维度。

**Why this priority**:ITR 问题单是项目售后维度的补充数据,影响项目健康度展示,但不阻塞主流程。

**Independent Test**:可独立验证——给定 ITR 表 `pms_incident` 含 N 条问题单,同步后 `pm_project_incident_table_from_itr` 含 N 条,UUID 字段转字符串。

**Acceptance Scenarios**:

1. **Given** ITR 表 `pms_incident` 含 60 条问题单,**When** 同步执行,**Then** `pm_project_incident_table_from_itr` 全量刷新为 60 条,`incidentId`、`accidentNo` 等 UUID 字段转为字符串。
2. **Given** ITR 表中某问题单的 `new_rma_idname` 关联 RMA 单号,**When** 同步完成,**Then** 本地表 `rmaNo` 字段保留该 RMA 单号,且 `url` 字段拼接为 `/t/dptech/?mainNavName=xrm##/vform/incident/{incidentId}`。

---

### User Story 11 - 自动从 License 同步授权信息 (Priority: P2)

**As a** 项目经理,**I want** 系统自动从 License 系统同步授权码与设备序列号、合同关联,**so that** 项目可查询到 License 授权状态。

- 证据:`GainDataFromLicense.java:14`、`sql-map-refresh-data-license-config.xml:6-37`
- 说明:源表 `dptech_v_liccode_info`,落入 `pm_project_license_info_from_license`,字段含 licenseCode、sn、specModel、contract、contractType、item、status。

**Why this priority**:License 授权是项目交付后的资产管理数据,影响客户合规性,但不阻塞项目交付主流程。

**Independent Test**:可独立验证——给定 License 表含 K 条授权,同步后 `pm_project_license_info_from_license` 含 K 条。

**Acceptance Scenarios**:

1. **Given** License 表 `dptech_v_liccode_info` 含 120 条授权,**When** 同步执行,**Then** `pm_project_license_info_from_license` 全量刷新为 120 条,字段含 licenseCode、sn、specModel、contract、contractType、item、status。
2. **Given** License 表中按 contractNoList 过滤,**When** 同步执行,**Then** 仅同步指定合同号列表内的授权记录。

---

### User Story 12 - 自动向 D365 推送合同验收交付节点 (Priority: P3)

**As a** 财务人员,**I want** 系统定期把 30 天内变化的合同交付件信息按账套+合同号分组推送至 D365,**so that** D365 侧能同步更新合同收款计划的验收交付节点。

- 证据:`PushContractAcceptanceDeliveryJob.java:37-139`、`pms-ext-d365/.../d365/util/D365Api.java:368-381`
- 说明:由 `sys.d365.api.config.enablePushContractAcceptanceDelivery` 开关控制;按账套(`dataAreaId`)和合同号(`contractNo`)分组,失败合同号附加错误信息记录到同步日志的 `refreshException` 字段(JSON 形式 `{success, error}`)。

**Why this priority**:这是 PMS 向 D365 的反向推送,属于扩展模块,影响 D365 侧合同收款节点更新,但不阻塞 PMS 主流程。

**Independent Test**:可独立验证——给定本地有 5 个 30 天内变化的合同交付件,且 `enablePushContractAcceptanceDelivery=true`,执行后 D365 `paymentSchedUrl` 接收到 5 条推送,日志记录 `{success, error}` JSON。

**Acceptance Scenarios**:

1. **Given** `sys.d365.api.config.enablePushContractAcceptanceDelivery=false`,**When** 推送任务执行,**Then** 任务直接 return,不调用 D365 API。
2. **Given** 本地有 10 条 30 天内变化的合同交付件,分属 3 个账套 5 个合同号,**When** 推送执行,**Then** D365 `paymentSchedUrl` 被调用 5 次(按 dataAreaId+contractNo 分组)。
3. **Given** 推送过程中某合同号失败,**When** 推送完成,**Then** 其他合同号不受影响,`refreshException` 字段写入 `{success: [...], error: [...]}` JSON 结构。

---

### User Story 13 - 自动向 D365 推送采购订单与采购收货 (Priority: P3)

**As a** 转包管理人员,**I want** 系统按 D365 API 创建采购订单和采购收货单并回填 purchId/inventTransId,**so that** 转包合同与 D365 采购单双向关联。

- 证据:`pms-ext-d365/.../d365/util/D365Api.java:176-358`、`PurchaseHeader.java:13-196`
- 说明:支持 OAuth2 client_credentials 模式获取 token(缓存复用,过期重取);推送后回填 `purchId`、`purchIds`、`inventTransId`、`inventTransIds` 等关键字段至本地实体 `customInfo`。

**Why this priority**:转包采购是扩展业务流程,影响 D365 采购单与 PMS 转包合同的关联,但不属于 PMS 项目交付主流程。

**Independent Test**:可独立验证——给定一个 PurchaseHeader + 2 行 PurchaseLine,推送后 D365 返回 purchId 和 inventTransId,本地实体 `customInfo` 字段被回填。

**Acceptance Scenarios**:

1. **Given** 一个 PurchaseHeader(vendAccount、purchName、purchPoolId 等字段完整)+ 2 行 PurchaseLine,**When** 推送采购订单,**Then** D365 `createPOUrl` 接收请求,响应含 `purchId` 和 `inventTransId`,本地 `customInfo` 回填这些字段。
2. **Given** 一个 PurchaseReceiptHeader + 2 行 PurchaseReceiptLine,**When** 推送采购收货,**Then** D365 `receiptPOUrl` 接收请求,响应含 `receiptId/purchId/inventTransId`,本地实体回填。
3. **Given** D365 接口返回 `success=false`,**When** 推送执行,**Then** 抛出业务异常,`customInfo` 仍保留 purchId/purchIds/inventTransId/inventTransIds 字段。

---

### User Story 14 - 自动向 FP 系统推送发票查验 (Priority: P3)

**As a** 财务人员,**I want** 系统调用 FP(发票平台)API 批量查验电子发票,**so that** 发票真伪和明细可被项目付款流程使用。

- 证据:`pms-ext-fp/.../fp/util/FPApi.java:97-1859`
- 说明:支持单/多文件批量提交、限流(MINUTE/SINGLE/MULTIPLE 三种模式)、Token 多种认证方式(bearer/header/query/cookie)、连接池(可切换不同 HTTP 客户端实现)、失败重试。

**Why this priority**:发票查验是财务付款流程的扩展能力,影响发票真伪核验,但不属于 PMS 项目交付主流程。

**Independent Test**:可独立验证——给定 5 张发票文件 + sourceList,配置 `sys.fp.api`,执行查验后返回 `List<Response<ElectronicInvoiceModel>>`,失败发票被包装为 `Response.failure(message)`。

**Acceptance Scenarios**:

1. **Given** 5 张发票文件 + sourceList + `dataType` + `dataId`,**When** 调用 FP `archiveUrl`,**Then** 返回 5 条 `Response<ElectronicInvoiceModel>`,每条含真伪与明细信息。
2. **Given** 限流模式为 `MULTIPLE`,**When** 提交批量查验,**Then** 线程池并发 10 线程提交,超时时间 = `delay × list.size × 20`(最低 30 秒)。
3. **Given** FP 接口返回异常且 `enableRetry=true`,**When** 请求失败,**Then** 清除 token 缓存后重新请求一次,`retried=true` 标记防止无限递归。

---

### Edge Cases

- **同一执行单号在 SAP 与 D365 中均存在**:经 `selectOrderInfoFromERP` UNION ALL 合并后,`pm_order_data_from_erp_source` 中会出现两条记录(分别带 `source='SAP'` 和 `source='D365'`),需通过 `orderExecNumberShort` 进行去重/匹配处理。
- **执行单号含版本字符 J/Y/X**:跨系统对接时,`J` 需统一替换为 `X`,并生成 `orderExecNumberShort`(去版本号:`CONCAT(LEFT(,12), SUBSTR(,14))`)用于模糊匹配;`Y` 类型在第三步匹配中忽略。
- **同步过程中外部系统视图返回空结果**:应视为合法状态,`truncate` 本地表后插入 0 条,同步日志状态为成功而非失败。
- **同步任务并发触发**:部分任务(`PushContractAcceptanceDeliveryJob`、`GainPresalesInfoBySMS`、`GainMarketRelationsBySMS`、`PlanGetBySMS`)声明并发保护,防止并发执行导致数据错乱;其他任务并发风险待评估。
- **D365/FP token 过期且并发刷新**:并发场景下只允许一个线程刷新 token,其他线程等待;D365 通过 `volatile` 缓存,FP 通过读写锁双重检查。
- **FP 批量查验中部分发票失败**:失败的发票应被包装为 `Response.failure(message)` 返回,不抛出异常,不阻断其他发票的查验。
- **多步同步任务前序子步骤失败**:`GainOrderByERP` 的 5 个子步骤各自独立事务,前序失败不阻断后序,可能导致数据状态不一致(如订单同步失败但发货状态更新成功);是否需要补偿机制见 SC-012。
- **SMS 表结构向后兼容字段新增**:仅对向后兼容的字段添加生效;DDL 操作不放入事务;字段类型需明确指定(如 `varchar(10)`、`varchar(255)`)。
- **D365/FP API 配置变更**:配置通过 `sys.d365.api.config`、`sys.fp.api` 系统参数表动态读取,变更后应能动态生效,无需重启应用。
- **临时表残留**:同步过程创建的临时表(如 `temp_needUpdate_project`、`temp_project_sales_change`、`pm_person_from_oa_temp`)在异常时可能未被清理,需在下次同步前确保清理。
- **UUID 字段跨数据库类型转换**:ITR 表 `pms_incident` 中的 UUID 字段(`incidentid`、`dptech_accidentlist_id`)经类型处理器转字符串后落入本地 VARCHAR 字段,需保证转换无损。

## Requirements *(mandatory)*

### Functional Requirements

<!--
  本域功能需求按同步任务分节:FR-01~FR-10 为拉取类(Gain*,定时调度触发),
  FR-11~FR-12 为推送类(Push*/API 调用,由业务流程触发)。
  每节保留 FR 编号、触发条件、输入、处理规则、输出本地表、异常处理、批量大小。
  证据引用(代码路径)作为逆向反推来源标记保留。
  按 DATA-REUSE-01 原则,所有本地表沿用既有结构,不新建重复表。
-->

#### FR-01 订单同步(GainOrderByERP / GainOrderBySAP)

- **FR-01a**:系统 MUST 通过定时调度触发订单同步任务,从 SAP 视图同步销售订单(SO)与退货订单(RMA),按账套(`company_code`)区分。
- **FR-01b**:系统 MUST 在同步 SAP 订单时将执行单号 `orderExecNumber` 中的 `J` 替换为 `X`。
- **FR-01c**:系统 MUST 通过定时调度从 D365 视图同步订单与订单行,订单行额外保留 `realOrderExecNumber` 与 `customInfo` 字段。
- **FR-01d**:系统 MUST 通过 `selectOrderInfoFromERP` / `selectOrderLineFromERP` 将 SAP 与 D365 来源订单 UNION ALL 合并至 `pm_order_data_from_erp_source`、`pm_order_line_from_erp_source`,标记 `source` 字段(`SAP`/`D365`),生成 `orderExecNumberShort`(去版本号执行单号)。
- **FR-01e**:系统 MUST 在订单合并后调用存储过程 `splitSoleAgentLendOrderInfo()` 拆分总代借货订单,然后调用 `projectService.updateSoleAgentLendProject()`。
- **FR-01f**:系统 MUST 基于 SAP 订单行重建 `pm_project_product_line`:创建/删除临时表 `temp_needUpdate_project`、删除旧产品行、重置自增 ID、按 `orderType=0` 的销售订单重新插入。
- **FR-01g**:系统 MUST 在订单同步后调用 `UpdateShipmentState.work()` 更新项目发货状态;该子任务异常仅记录日志,不中断主流程。
- **FR-01h**:系统 MUST 在同步失败时回滚事务,调用 `syncDataFail` 写入 `refreshException`(完整堆栈)到 `fnd_data_refresh_log`;单步失败不阻断其他子步骤(每个子任务独立 `try/catch`)。
- **批量大小**:默认 2000 条/批。

- 证据:`GainOrderByERP.java:17`、`GainOrderBySAP.java:34`、`AbstractSynchronizeTask.java:41`、`sql-map-refresh-data-sap-config.xml`、`sql-map-refresh-data-d365-config.xml`、`sql-map-refresh-data-common-config.xml:1022-1081`。
- **输出(本地表)**:`pm_order_data_from_erp_sap`、`pm_order_line_from_erp_sap`、`pm_order_data_from_erp_d365`、`pm_order_line_from_erp_d365`、`pm_order_data_from_erp_source`、`pm_order_line_from_erp_source`、`pm_project_product_line`。

#### FR-02 人员同步(GainPersonByEHR / GainPersonByOA)

- **FR-02a**:系统 MUST 通过定时调度从 EHR 视图 `view_person_info_4_pms` 同步员工,参数 `staff_status` 1=有效 / 0=失效。
- **FR-02b**:系统 MUST 在同步有效员工时 `truncate` `pm_person_from_oa` 后批量插入(批 1000 条)。
- **FR-02c**:系统 MUST 在同步失效员工时落入临时表 `pm_person_from_oa_temp`(批 1000 条),执行 `invalidQuitProjectQuitSalesMan` 把关联项目成员的 `effectiveTo` 置为当前时间,然后删除临时表。
- **FR-02d**:系统 MUST 在失败时回滚事务 + 日志记录(同 FR-01h)。
- **批量大小**:1000 条/批。

- 证据:`GainPersonByEHR.java:22`、`GainPersonByOA.java:30`(`@Deprecated`)、`sql-map-refresh-data-common-config.xml:194-265`。
- **输出(本地表)**:`pm_person_from_oa`、`pm_project_member.effectiveTo`(变更)。

#### FR-03 售前信息同步(GainPresalesInfoBySMS / GainPresalesInfoFromOA)

- **FR-03a**:系统 MUST 通过定时调度从 SMS 同步 6 张售前借货相关表至本地镜像表:`v_lend_info_4_pms` → `pm_presales_lend_info_from_sms`、`v_lend_products_4_pms` → `pm_presales_lend_product_from_sms`、`v_lend_products_sale_4_pm` → `pm_presales_lend_order_from_sms`、`v_lend_products_tosale_4_pms` → `pm_presales_lend_2_sale_from_sms`、`v_lend_products_torma_4_pms` → `pm_presales_lend_2_rma_from_sms`、`DP_V_SO_DELIVERY_OFF_4_PMS`(收发日期,来自 SAP) → `pm_presales_lend_2_delivery_off_from_sap`。
- **FR-03b**:系统 MUST 在 SMS 同步后去重插入售前测试项目主表:`pm_presales_lend_info_from_sms` 中 lendInfoId 不在 `pm_presales_project_header` 中的,插入到 `pm_presales_project_header`。
- **FR-03c**:系统 MUST 创建核销汇总表 `pm_presales_project_rma_info`,按合同+物料维度汇总 `orderQty/dlvQty/rmaQty/deliveryDate/rmaDate`。
- **FR-03d**:系统 MUST 更新售前测试借转销/未核销标记 `pm_presales_project_header.hasTransfer/hasRma`。
- **FR-03e**:系统 MUST 从 OA 同步 `pm_presales_lend_info_from_oa`、`pm_presales_lend_detail_from_oa`,再通过 `insertPresalesHeaderFormOA` 把 OA 临时授权信息(包含 processStartTime、applyDate、附件路径等)合并到 `pm_presales_project_header`,优先级 `pls > pps > plio`,源标记 `source='OA'`。
- **FR-03f**:系统 MUST 支持双路径切换:`if (SystemContext.enableCrm()) api(); else dataSource();`,用于 SMS 系统迁移到 CRM 后的对接。
- **FR-03g**:系统 MUST 在失败时回滚 + 日志记录(同 FR-01h)。
- **批量大小**:1000 条/批(`BATCH_SIZE`)。

- 证据:`GainPresalesInfoBySMS.java:28-194`、`GainPresalesInfoFromOA.java:14`、`sql-map-refresh-data-sms-config.xml:106-159`、`sql-map-refresh-data-oa-config.xml:6-179`、`sql-map-refresh-data-common-config.xml:337-410`。
- **输出(本地表)**:`pm_presales_lend_info_from_sms`、`pm_presales_lend_product_from_sms`、`pm_presales_lend_order_from_sms`、`pm_presales_lend_2_sale_from_sms`、`pm_presales_lend_2_rma_from_sms`、`pm_presales_lend_2_delivery_off_from_sap`、`pm_presales_project_header`、`pm_presales_project_product_line`、`pm_presales_project_rma_info`、`pm_presales_lend_info_from_oa`、`pm_presales_lend_detail_from_oa`。

#### FR-04 项目属性同步(GainPrjPropertyBySMS)

- **FR-04a**:系统 MUST 在首次执行前自检 `pm_project_property_from_sms` 是否有 `serviceTypeName`、`channelName` 字段,无则动态 `ALTER TABLE ADD`(`serviceTypeName varchar(10)`、`channelName varchar(255)`)。
- **FR-04b**:系统 MUST 从 SMS 视图 `v_prj_property_4_pm` 同步项目属性至 `pm_project_property_from_sms`(批 2000),执行单号 `J`→`X` 替换。
- **FR-04c**:系统 MUST 从 SMS 视图 `v_soleagent_lend_4_pms` 同步总代借货至 `pm_project_soleagent_lend_from_sms`(批 2000),额外生成 `orderExecNumberShort`。
- **FR-04d**:系统 MUST 创建多张临时表(`temp_max_ppfsId/temp_max_ppfs/temp_max_prpId/temp_not_ppfs/serviceType_and_channelName_table`),按 `LEFT(ph.projectCode, 15)` 匹配,更新 `pm_project_header.column012/columno12_readonly`(服务类型)、`pm_project_related_party.partyName`(代理渠道)、`pm_project_header.compId/customerProjectName/majorProjectLevel/column013`(最终客户名)等字段。
- **FR-04e**:系统 MUST 处理销售变更:创建 `temp_project_sales_change`,失效旧销售(`invalid_project_invalid_sales`),插入新销售(`insert_changed_project_sales`)到 `pm_project_member`。
- **FR-04f**:系统 MUST 在失败时回滚 + 日志记录(同 FR-01h)。
- **批量大小**:2000 条/批。

- 证据:`GainPrjPropertyBySMS.java:29-103`、`sql-map-refresh-data-sms-config.xml:40-104`、`sql-map-refresh-data-common-config.xml:858-1020`。
- **输出(本地表)**:`pm_project_property_from_sms`、`pm_project_soleagent_lend_from_sms`、`pm_project_header`、`pm_project_related_party`、`pm_project_member`。

#### FR-05 项目真实设备清单同步(GainPrjRealProjectLineBySMS)

- **FR-05a**:系统 MUST 从 SMS 视图 `view_refer_product` 同步至 `pm_project_real_product_line_from_sms`(批 2000),执行单号 `J`→`X` 替换。
- **FR-05b**:系统 MUST 通过 `insert_pm_project_product_line_real` 三步匹配插入 `pm_project_product_line_real`:1) 执行单号完全匹配;2) 去版本号匹配;3) 去版本号且忽略 X/J/Y 类型匹配。关联 `pm_order_data_from_sap`、`pm_project_contract`、`pm_project_group_relationship`、`pm_project_header`。
- **FR-05c**:系统 MUST 在失败时回滚 + 日志记录(同 FR-01h)。

- 证据:`GainPrjRealProjectLineBySMS.java:28`、`sql-map-refresh-data-common-config.xml:477-590`。
- **输出(本地表)**:`pm_project_real_product_line_from_sms`、`pm_project_product_line_real`。

#### FR-06 市场关系维度同步(GainMarketRelationsBySMS)

- **FR-06a**:系统 MUST 从 SMS 视图 `view_market_system_expend_industry` 全量清空 `pm_project_market_relations_from_sms` 后批量插入(批 2000),字段含 marketCode/Name、systemCode/Name、expendCode/Name、industryCode/Name 4 维。
- **FR-06b**:系统 MUST 在失败时回滚 + 日志记录(同 FR-01h)。

- 证据:`GainMarketRelationsBySMS.java:29`、`sql-map-refresh-data-sms-config.xml:356-379`。
- **输出(本地表)**:`pm_project_market_relations_from_sms`。

#### FR-07 收款计划同步(PlanGetBySMS)

- **FR-07a**:系统 MUST 从 SMS 视图 `v_sms_pb_plan` 同步至 `pm_pb_plan_from_sms`,字段含 `contract_num, batch_code, money_item_name, reference_event_name, event_plan_happen_date, after_days_num, event_actual_finish_date, marketing_feedback`。
- **FR-07b**:系统 MUST 在同步时设置 `createBy/updateBy='admin'`、`effectiveFrom='2015-05-01'`、`createTime/updateTime=NOW()`。
- **FR-07c**:系统 MUST 按事件名查 `fnd_basic_data`,取得 `dataTypeCode/basicDataId` 后批量更新 `pm_project_task.eventPlanHappenDate` 与 `visibleFlag=1`。
- **FR-07d**:系统 MUST NOT 在新系统中使用字符串拼接 SQL;新系统 MUST 使用参数化查询或 ORM 替代原始 JDBC,避免 SQL 注入风险。
- **特殊说明**:本任务在旧版采用原始 JDBC 而非 SQL-map,SQL 字符串拼接存在注入风险(`PlanGetBySMS.java:85-90`),新系统必须改造。

- 证据:`PlanGetBySMS.java:22-110`。
- **输出(本地表)**:`pm_pb_plan_from_sms`、`pm_project_task.eventPlanHappenDate`、`pm_project_task.visibleFlag`。

#### FR-08 ITR 问题单同步(GainDataFromITR)

- **FR-08a**:系统 MUST 通过 `AbstractSynchronizeTask.syncData("ProblemTicketFormITR", "ITR", params)` 通用流程从 ITR 表 `pms_incident` 同步问题单,可按 projectCode、contractNo、contractNoList 过滤。
- **FR-08b**:系统 MUST 将 UUID 字段(`incidentId`、`accidentNo`)经 `UUIDToStringHandler` 转字符串。
- **FR-08c**:系统 MUST 全量清空 `pm_project_incident_table_from_itr` 后插入,字段含 incidentId、ticketNo、status/statusName、caseTopic、memo、principal/principalName、accepter/accepterName、processor/processorName、supplied、questionType、questionLevel、title、acceptTime、productType、productModel、progress、questionReason、solutionType、solutions、rmaNo、accidentNo、caseType、reasonFstType、reasonSndType、projectCode、contractNo、barcode、bulletinNo、bugNo、productLine、url、customInfo。
- **FR-08d**:系统 MUST 在失败时回滚 + 日志记录(同 FR-01h)。

- 证据:`GainDataFromITR.java:14`、`sql-map-refresh-data-itr-config.xml:6-84`。
- **输出(本地表)**:`pm_project_incident_table_from_itr`。

#### FR-09 License 授权信息同步(GainDataFromLicense)

- **FR-09a**:系统 MUST 从 License 表 `dptech_v_liccode_info` 同步授权信息,可按 contract 或 contractNoList 过滤。
- **FR-09b**:系统 MUST 全量清空 `pm_project_license_info_from_license` 后插入 licenseCode、sn、specModel、contract、contractType、item、status。
- **FR-09c**:系统 MUST 在失败时回滚 + 日志记录(同 FR-01h)。

- 证据:`GainDataFromLicense.java:14`、`sql-map-refresh-data-license-config.xml:6-37`。
- **输出(本地表)**:`pm_project_license_info_from_license`。

#### FR-10 合同验收交付推送(PushContractAcceptanceDeliveryJob)

- **FR-10a**:系统 MUST 通过定时调度触发推送任务,从 `pm_project` 查询 30 天内变化的合同交付件信息(参数 `dateDiff` 默认 30 天,可配置 `sys.d365.api.config.acceptanceDeliveryDiff`)。
- **FR-10b**:系统 MUST 读取 `sys.d365.api.config` 系统参数(JSON),解析 `enablePushContractAcceptanceDelivery` 开关,未开启直接 return。
- **FR-10c**:系统 MUST 按 `dataAreaId`(账套)+ `contractNo`(合同号)二级分组。
- **FR-10d**:系统 MUST 调用 `D365Api.pushContractAcceptanceDeliveryInfo(dataAreaId, contractNo, lines, config)`,POST 至 D365 `paymentSchedUrl`。
- **FR-10e**:系统 MUST 将成功/失败结果分别归入 `successMap`/`errorMap`,失败合同号附加错误信息,最终结果以 JSON 形式 `{success, error}` 写入同步日志的 `refreshException` 字段。
- **FR-10f**:系统 MUST 在总体异常时回滚 + `refreshException` 记录堆栈;单合同失败不影响其他合同推送。

- 证据:`PushContractAcceptanceDeliveryJob.java:37-139`、`D365Api.java:368-381`。
- **输出**:外部系统 D365 接口 `/api/services/.../paymentSched`(URL 由 `sys.d365.api.config.paymentSchedUrl` 提供)。

#### FR-11 D365 采购订单/收货推送(D365Api 扩展模块)

- **FR-11a**:系统 MUST 在转包业务流程触发时(非定时,由 service 层调用)推送采购订单至 D365 `createPOUrl`(默认 `/api/services/IWS_InterfaceInboundServiceGroup/CreatePurchTable/create`)。
- **FR-11b**:系统 MUST 在推送采购订单时提交 `PurchaseHeader`(订单头:vendAccount、purchName、purchPoolId、purContract、salesContract、contractAmount、workerPurchPlacer、applicant、inventLocationId、deliveryDate、dlvMode、dlvTerm、payment、paymMode、remark、otherSysNum、projectName、projectProgress、subcontractType、subcontStartDate、subcontEndDate)+ `List<PurchaseLine>`(订单行)。
- **FR-11c**:系统 MUST 解析响应回填 `purchId`、`inventTransId`,插入本地 `pm_purchase`/`pm_purchase_line`(或对应实体表)。
- **FR-11d**:系统 MUST 推送采购收货至 D365 `receiptPOUrl`(默认 `/api/services/IWS_InterfaceInboundServiceGroup/CreatePurchPackingSlip/create`),请求体含 `dataAreaId`、`packingSlipId`、`lines`,响应回填 `receiptId/purchId/inventTransId`。
- **FR-11e**:系统 MUST 在接口返回 `success=false` 时抛出业务异常;统一回填 `customInfo` 字段保留 purchId/purchIds/inventTransId/inventTransIds。
- **FR-11f**:系统 MUST 通过 OAuth2 client_credentials 模式获取 token(缓存复用至过期)。

- 证据:`D365Api.java:122-358`、`PurchaseHeader.java:13-196`。
- **输出**:外部系统 D365;本地 `Purchase`、`PurchaseLine`、`PurchaseReceipt`、`PurchaseReceiptLine` 实体表。

#### FR-12 FP 发票查验推送(FPApi 扩展模块)

- **FR-12a**:系统 MUST 在发票业务流程触发时(非定时)调用 FP `archiveUrl` 批量查验电子发票,请求体含 `dataType`、`dataId`、`files[]`、`sourceList`(原发票数据)、`async`(是否异步)、`openId`。
- **FR-12b**:系统 MUST 通过 `sys.fp.api` 配置初始化(token、archiveUrl 等)。
- **FR-12c**:系统 MUST 支持 Token 多种认证方式:`bearer/header/query/cookie`。
- **FR-12d**:系统 MUST 支持三种限流模式:`MINUTE`(每分钟一次 List 提交)、`SINGLE`(按 rateLimit 秒级限流单提交)、`MULTIPLE`(线程池并发提交,固定 10 线程)。
- **FR-12e**:系统 MUST 支持 HTTP 客户端可切换(默认使用连接池实现)。
- **FR-12f**:系统 MUST 在 `enableRetry=true` 时,失败后清除 token 缓存重新请求一次,通过 `retried=true` 标记防止无限递归。
- **FR-12g**:系统 MUST 将异常包装为 `Response.failure(message)` 返回,不抛出。
- **FR-12h**:系统 MUST 在应用销毁时关闭线程池(`shutdownNow`)。

- 证据:`FPApi.java:97-1859`。
- **输出**:外部系统 FP `archiveUrl`;返回 `List<Response<ElectronicInvoiceModel>>`。

### Key Entities *(include if feature involves data)*

<!--
  本域数据契约分两类:
  - 本地域(16 张表):同步日志 + 各业务镜像表。分级标记:I=Identifying(主键/唯一键/枚举),
    D=Descriptive(描述性属性),C=Cardinal(基数约束)。本域本地表以 I/D 为主,C 标记用于业务不变量。
  - 外部系统视图/表/API(9 类):标注为"外部契约"(EC),新系统需对接但非本地表。
  按 DATA-REUSE-01 原则,所有本地表沿用既有结构,不新建重复表。
-->

#### 数据契约 - 本地域

##### 表 `fnd_data_refresh_log`(同步日志,旧版 struts 模块使用)

> 证据:`sql-map-refresh-data-common-config.xml:159-185`

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | INTEGER(自增) | 否 | 主键,通过 `last_insert_id()` 回填 | 唯一 | I |
| refreshTaskName | VARCHAR | 否 | 任务类全名(如 `GainOrderByERP.OrderInfoFromD365`) | 非空 | I |
| handleUser | VARCHAR | 否 | 操作用户,定时任务固定为 `system` | =`system` for jobs | I |
| dataFrom | VARCHAR | 是 | 数据来源系统名(SAP/D365/OA/EHR/ITR/SMS/License/PMS) | 枚举值 | I |
| dataTo | VARCHAR | 是 | 数据目标系统名(PMS/D365 等) | 枚举值 | I |
| refreshFrom | TIMESTAMP | 否 | 同步开始时间(由 SQL `now()` 写入) | ≤ refreshTo | I |
| refreshTo | TIMESTAMP | 是 | 同步结束时间(成功/失败均写入) | ≥ refreshFrom | I |
| refreshState | INTEGER | 是 | 同步状态:1=成功;空=失败 | 0/1/null | I |
| refreshException | LONGTEXT | 是 | 失败堆栈或成功结果 JSON(`PushContractAcceptanceDeliveryJob` 写入 `{success,error}` 结构) | 长 text | D |

> 业务不变量:同一 `refreshTaskName`+`refreshFrom` 组合唯一(逻辑约束,非 DB 约束)。

##### 表 `t_sync_log`(同步日志,新版 core 模块使用)

> 证据:`SyncLogMapper.xml:4-19`、`SynchronizeMapper.xml:71-149`、`SyncLog.java:15-167`

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | INTEGER(自增) | 否 | 主键 | 唯一 | I |
| targetMethod | CHAR | 是 | 目标方法名 | - | I |
| tableObject | CHAR | 是 | 同步表对象名 | - | I |
| dataFrom | CHAR | 是 | 数据来源系统名 | 枚举值 | I |
| dataTo | CHAR | 是 | 数据目标系统名 | 枚举值 | I |
| syncParams | VARCHAR | 是 | 同步参数(JSON) | - | D |
| syncStartTime | TIMESTAMP | 是 | 同步开始时间 | ≤ syncEndTime | I |
| syncEndTime | TIMESTAMP | 是 | 同步结束时间 | ≥ syncStartTime | I |
| isSuccess | BIT | 是 | 是否成功(0/1) | - | I |
| dataCount | INTEGER | 是 | 同步数据条数 | ≥0 | D |
| syncType | SMALLINT | 是 | 同步类型:1=FULL_SYNC(全量同步), 2=INCREM_SYNC(增量同步)(见 `SyncType.java`) | - | I |
| exception | LONGTEXT | 是 | 异常堆栈 | - | D |

##### 表 `t_sync_state`(增量同步状态)

> 证据:`SynchronizeMapper.xml:5-69`

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | INTEGER(自增) | 是 | 主键 | 唯一 | I |
| tableObject | VARCHAR | 否 | 同步表对象名(唯一键) | 唯一 | I |
| lastId | VARCHAR | 是 | 上次同步的最后 ID(增量游标) | - | D |
| lastSyncTime | TIMESTAMP | 是 | 上次同步时间 | - | D |
| offset | INTEGER | 是 | 偏移量(分页游标) | ≥0 | D |

> 业务不变量:`tableObject` 唯一;`ON DUPLICATE KEY UPDATE`。

##### 表 `pm_order_data_from_erp_sap`(SAP 订单镜像)

> 证据:`sql-map-refresh-data-sap-config.xml:45-56`

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| orderNumber | VARCHAR | 否 | 订单号 | 联合唯一键(订单号+合同号+执行单号) | I |
| contractNo | VARCHAR | 否 | 合同号 | - | I |
| orderExecNumber | VARCHAR | 否 | 订单执行单号(J→X 转换后) | - | I |
| orderCreateTime | DATETIME | 是 | 订单创建时间 | - | D |
| customerRequireTime | DATETIME | 是 | 客户需求时间 | - | D |
| customerCode | VARCHAR | 是 | 客户编码 | - | D |
| customerName | VARCHAR | 是 | 客户名称 | - | D |
| projectName | VARCHAR | 是 | 项目名称 | - | D |
| orderComment | VARCHAR | 是 | 订单备注 | - | D |
| orderType | INTEGER | 否 | 订单类型:0=销售订单,1=退货订单 | 0/1 | I |
| compCode | VARCHAR | 否 | 公司/账套编码(默认 `0`) | - | I |
| salesType | VARCHAR | 是 | 销售类型 | - | D |

##### 表 `pm_order_line_from_erp_sap`(SAP 订单行镜像)

> 证据:`sql-map-refresh-data-sap-config.xml:90-102`

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| orderNumber | VARCHAR | 否 | 订单号 | 关联 pm_order_data_from_erp_sap | I |
| lineNum | VARCHAR | 否 | 行号 | - | I |
| itemCode | VARCHAR | 是 | 物料编码 | - | D |
| itemDesc | VARCHAR | 是 | 物料描述 | - | D |
| orderQuantity | DECIMAL | 否 | 订单数量(默认 0) | ≥0 | D |
| openQuantity | DECIMAL | 否 | 未清数量(默认 0) | ≥0 | D |
| bundleCode | VARCHAR | 是 | 捆绑父物料编码 | - | D |
| warrantyMonth | INTEGER | 否 | 保修月数(默认 0) | ≥0 | D |
| lineType | INTEGER | 否 | 行类型:0=销售,1=退货 | 0/1 | I |
| compCode | VARCHAR | 否 | 公司/账套编码 | - | I |
| profitCenter | VARCHAR | 是 | 利润中心 | - | D |

##### 表 `pm_order_data_from_erp_d365`(D365 订单镜像)

> 证据:`sql-map-refresh-data-d365-config.xml:11-22`

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| (同 SAP 表字段) | - | - | - | - | I |
| customInfo | LONGTEXT | 是 | D365 订单的扩展信息(JSON) | - | D |

##### 表 `pm_order_line_from_erp_d365`(D365 订单行镜像)

> 证据:`sql-map-refresh-data-d365-config.xml:29-40`

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| (同 SAP 表字段) | - | - | - | - | I |
| realOrderExecNumber | VARCHAR | 是 | 真实订单执行单号 | - | D |
| customInfo | LONGTEXT | 是 | D365 订单行的扩展信息(JSON) | - | D |

##### 表 `pm_order_data_from_erp_source`(SAP+D365 合并视图)

> 证据:`sql-map-refresh-data-common-config.xml:1022-1047`

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| (同 SAP 表字段) | - | - | - | - | I |
| orderExecNumberShort | VARCHAR | 是 | 去版本号执行单号(CONCAT(LEFT,12)+SUBSTR(,14)) | - | D |
| source | VARCHAR | 否 | 来源标记:`SAP` / `D365` | 枚举 | I |
| customInfo | LONGTEXT | 是 | D365 来源扩展信息(SAP 来源为 NULL) | - | D |
| syncTime | DATETIME | 是 | 同步时间(D365 来源,SAP 来源为 NULL) | - | D |

##### 表 `pm_person_from_oa`(员工镜像,OA/EHR 同步后共用)

> 证据:`sql-map-refresh-data-common-config.xml:219-233`

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| salesmanCode | VARCHAR | 否 | 工号(work_card_number) | 唯一 | I |
| salesmanName | VARCHAR | 是 | 员工姓名(staff_name) | - | D |
| salesmanMail | VARCHAR | 是 | 公司邮箱(company_mail) | - | D |
| salesmanTel | VARCHAR | 是 | 电话(phone) | - | D |

##### 表 `pm_project_property_from_sms`(项目属性镜像)

> 证据:`sql-map-refresh-data-sms-config.xml:85-104`

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| orderExecNumber | VARCHAR | 否 | 执行单号(J→X 转换后) | - | I |
| projectCode | VARCHAR | 否 | 项目编码 | - | I |
| projectName | VARCHAR | 是 | 项目名称 | - | D |
| salesManCode | VARCHAR | 是 | 销售工号 | - | D |
| salesManName | VARCHAR | 是 | 销售姓名 | - | D |
| marketCode/Name | VARCHAR | 是 | 市场部编码/名称 | - | D |
| systemId/Name | VARCHAR | 是 | 系统编码/名称 | - | D |
| expendId/Name | VARCHAR | 是 | 支出编码/名称 | - | D |
| industryId/Name | VARCHAR | 是 | 行业编码/名称 | - | D |
| officeCode/Name | VARCHAR | 是 | 办事处编码/名称 | - | D |
| serviceTypeName | VARCHAR(10) | 是 | 服务类型(动态添加字段) | - | D |
| channelName | VARCHAR(255) | 是 | 渠道名称(动态添加字段) | - | D |
| engineeFee | DECIMAL | 是 | 工程费 | - | D |
| objId | VARCHAR | 是 | 对象 ID | - | D |
| applyType | VARCHAR | 是 | 申请类型 | - | D |
| corporationCode | VARCHAR | 否 | 公司法人编码(默认 `01`) | - | I |
| customerProjectName | VARCHAR | 是 | 客户项目名 | - | D |
| finalCustomerName | VARCHAR | 是 | 最终客户名 | - | D |
| agentName | VARCHAR | 是 | 代理名 | - | D |
| majorProjectLevel | VARCHAR | 是 | 重大项目级别 | - | D |
| projectMoney | DECIMAL | 是 | 项目金额 | - | D |
| submitTime | DATETIME | 是 | 提交时间 | - | D |
| predBidDate | DATETIME | 是 | 预计中标日期 | - | D |
| linkmanName/Tel | VARCHAR | 是 | 联系人姓名/电话 | - | D |
| dataSource | VARCHAR | 是 | 数据来源(默认 `SMS`) | 枚举 | I |

##### 表 `pm_project_incident_table_from_itr`(ITR 问题单镜像)

> 证据:`sql-map-refresh-data-itr-config.xml:56-81`

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| incidentId | VARCHAR | 否 | 问题单 ID(UUID 转字符串) | 唯一 | I |
| ticketNo | VARCHAR | 是 | 工单号 | - | D |
| status/statusName | VARCHAR | 是 | 工单状态/名称 | - | D |
| caseTopic | VARCHAR | 是 | 问题单主题 | - | D |
| memo | LONGTEXT | 是 | 描述 | - | D |
| principal/principalName | VARCHAR | 是 | 责任人/名称 | - | D |
| accepter/accepterName | VARCHAR | 是 | 受理人/名称 | - | D |
| processor/processorName | VARCHAR | 是 | 处理人/名称 | - | D |
| supplied | VARCHAR | 是 | 是否上报 | - | D |
| questionType | VARCHAR | 是 | 服务类型 | - | D |
| questionLevel | VARCHAR | 是 | 问题级别 | - | D |
| title | VARCHAR | 是 | 工单标题 | - | D |
| acceptTime | DATETIME | 是 | 受理时间 | - | D |
| productType/productModel | VARCHAR | 是 | 设备类型/型号 | - | D |
| progress | LONGTEXT | 是 | 处理进展 | - | D |
| questionReason | LONGTEXT | 是 | 问题根因 | - | D |
| solutionType/solutions | LONGTEXT | 是 | 解决方式/方案 | - | D |
| rmaNo | VARCHAR | 是 | RMA 单号 | - | D |
| accidentNo | VARCHAR | 是 | 事故单号(UUID 转字符串) | - | D |
| caseType | VARCHAR | 是 | Case 类型 | - | D |
| reasonFstType/reasonSndType | VARCHAR | 是 | 原因大类/小类 | - | D |
| projectCode | VARCHAR | 是 | 项目编码 | - | D |
| contractNo | VARCHAR | 是 | 合同号 | - | D |
| barcode | VARCHAR | 是 | 序列号 | - | D |
| bulletinNo | VARCHAR | 是 | 技术公告编号 | - | D |
| bugNo | VARCHAR | 是 | Bug 单号 | - | D |
| productLine | VARCHAR | 是 | 产品线 | - | D |
| url | VARCHAR | 是 | 问题单 URL(拼接 `/t/dptech/?mainNavName=xrm##/vform/incident/{incidentId}`) | - | D |
| customInfo | LONGTEXT | 是 | 扩展信息 | - | D |

##### 表 `pm_project_license_info_from_license`(License 授权镜像)

> 证据:`sql-map-refresh-data-license-config.xml:24-33`

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| licenseCode | VARCHAR | 否 | License 编码 | 唯一 | I |
| sn | VARCHAR | 是 | 设备序列号 | - | D |
| specModel | VARCHAR | 是 | 规格型号 | - | D |
| contract | VARCHAR | 是 | 合同号 | - | D |
| contractType | VARCHAR | 是 | 合同类型 | - | D |
| item | VARCHAR | 是 | 物料编码 | - | D |
| status | VARCHAR | 是 | 授权状态 | - | D |

##### 表 `pm_presales_lend_info_from_sms`(SMS 借货主信息镜像)

> 证据:`sql-map-refresh-data-sms-config.xml:197-220`

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| projectCode | VARCHAR | 否 | 项目编码 | - | I |
| projectName | VARCHAR | 是 | 项目名称 | - | D |
| marketName/systemName/expendName/industryName | VARCHAR | 是 | 市场/系统/支出/行业 | - | D |
| officeCode | VARCHAR | 是 | 办事处编码 | - | D |
| dutyName | VARCHAR | 是 | 责任人姓名 | - | D |
| pspm | VARCHAR | 是 | 产品经理 | - | D |
| dutyContactWay | VARCHAR | 是 | 责任人联系方式 | - | D |
| lendInfoId | VARCHAR | 否 | 借货单 ID | 唯一 | I |
| decPath | VARCHAR | 是 | 文件路径 | - | D |
| dataSource | VARCHAR | 是 | 数据来源(默认 `SMS`) | 枚举 | I |

##### 表 `pm_presales_lend_detail_from_oa`(OA 临时授权明细镜像)

> 证据:`sql-map-refresh-data-oa-config.xml:49-62`

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | VARCHAR | 否 | 明细 ID | 唯一 | I |
| infoId | VARCHAR | 否 | 关联 lendInfoId | - | I |
| contractNum | VARCHAR | 是 | 合同号 | - | D |
| deviceSerialnum | VARCHAR | 是 | 设备序列号 | - | D |
| modelNum | VARCHAR | 是 | 型号 | - | D |
| applyCount | INTEGER | 是 | 申请数量 | ≥0 | D |
| isSoftware | VARCHAR | 是 | 是否软件 | - | D |
| customInfo | LONGTEXT | 是 | 扩展信息 | - | D |

##### 表 `pm_presales_lend_info_from_oa`(OA 临时授权主信息镜像)

> 证据:`sql-map-refresh-data-oa-config.xml:14-36`

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| projectCode | VARCHAR | 是 | 项目编码 | - | D |
| processStartTime | DATETIME | 是 | 流程开始时间 | - | D |
| lendInfoId | VARCHAR | 否 | 借货单 ID | 唯一 | I |
| processOrderNum | VARCHAR | 是 | 流程单号 | - | D |
| applyUserCode/Name | VARCHAR | 是 | 申请人编码/姓名 | - | D |
| marketName/systemName/expendName/industryName | VARCHAR | 是 | 市场/系统/支出/行业 | - | D |
| applyDeptCode/Name | VARCHAR | 是 | 申请部门编码/名称 | - | D |
| applyDate | DATE | 是 | 申请日期 | - | D |
| projectName | VARCHAR | 是 | 项目名称 | - | D |
| applyType/Name | VARCHAR | 是 | 申请类型/名称 | - | D |
| salesUserCode/Name | VARCHAR | 是 | 销售编码/姓名 | - | D |
| salesUserMobile | VARCHAR | 是 | 销售手机 | - | D |
| productLine/Name | VARCHAR | 是 | 产品线/名称 | - | D |
| applyCause | LONGTEXT | 是 | 申请原因 | - | D |
| followUpPlan | LONGTEXT | 是 | 后续计划 | - | D |
| testStartTime/EndTime | DATE | 是 | 测试开始/结束时间 | - | D |
| authPlanDate/authDate | DATE | 是 | 计划授权/实际授权日期 | - | D |
| resellSuccessfully | VARCHAR | 是 | 是否成功转销(`是`/`否`) | - | D |
| useDays | INTEGER | 是 | 使用天数 | ≥0 | D |
| resaleCertificateFile/provideAuthFile/infoFile | LONGTEXT | 是 | 文件路径(转销证明/授权文件/信息文件) | - | D |
| customInfo | LONGTEXT | 是 | 扩展信息 | - | D |

##### 表 `pm_pb_plan_from_sms`(SMS 收款计划镜像)

> 证据:`PlanGetBySMS.java:85-90`

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| contractNo | VARCHAR | 是 | 合同号 | - | D |
| batchCode | VARCHAR | 是 | 批次编码 | - | D |
| basicDataName | VARCHAR | 是 | 款项名称 | - | D |
| referenceEventName | VARCHAR | 是 | 关联事件名称 | - | D |
| eventPlanHappenDate | DATE | 是 | 事件计划发生日期 | - | D |
| afterDaysNum | INTEGER | 是 | 后置天数 | - | D |
| eventActualFinishDate | DATE | 是 | 事件实际完成日期 | - | D |
| marketingFeedback | VARCHAR | 是 | 市场反馈 | - | D |
| createBy/updateBy | VARCHAR | 否 | 创建/更新人(固定 `admin`) | =`admin` | I |
| createTime/updateTime | TIMESTAMP | 否 | 创建/更新时间(同步时 `NOW()`) | - | D |
| effectiveFrom | DATE | 否 | 生效起始(固定 `2015-05-01`) | - | I |
| effectiveTo | DATE | 是 | 生效结束 | - | D |

> 注:`PlanGetBySMS` 不通过 `AbstractSynchronizeTask`,直接 JDBC,SQL 拼接存在注入风险。

#### 数据契约 - 外部系统视图/表/API(外部契约,标注 EC)

##### SAP 订单视图(外部契约)

> 证据:`sql-map-refresh-data-sap-config.xml:7-19, 26-38, 62-73`

- 视图 `dp_reports.DP_V_SO_ORDER_4_PMS`(销售订单):

| 字段(外部) | 类型 | 语义说明 | 分级 |
|---|---|---|---|
| order_number | VARCHAR | 订单号 | EC |
| contract_number | VARCHAR | 合同号 | EC |
| order_exec_number | VARCHAR | 执行单号(含 J 类型,需替换为 X) | EC |
| order_creation_date | DATETIME | 订单创建时间 | EC |
| customer_require_date | DATETIME | 客户需求时间 | EC |
| customer_code/customer_name | VARCHAR | 客户编码/名称 | EC |
| project_name | VARCHAR | 项目名称 | EC |
| comment | VARCHAR | 备注 | EC |
| company_code | VARCHAR | 公司/账套编码(默认 0) | EC |
| u_sordertype | VARCHAR | 销售类型 | EC |

- 视图 `dp_reports.DP_V_RMA_ORDER_4_PMS`(退货订单):字段同上,主键字段为 `rma_order_number`。
- 视图 `dp_reports.DP_V_SO_LINE_4_PMS` / `DP_V_RMA_LINE_4_PMS`(订单行/退货行):

| 字段(外部) | 类型 | 语义说明 | 分级 |
|---|---|---|---|
| order_number | VARCHAR | 订单号 | EC |
| LineNum | VARCHAR | 行号 | EC |
| item_code/item_description | VARCHAR | 物料编码/描述 | EC |
| order_quantity/open_quantity | DECIMAL | 订单数量/未清数量(默认 0) | EC |
| bundle_parent_item_code | VARCHAR | 捆绑父物料编码 | EC |
| warranty_by_month | INTEGER | 保修月数(默认 0) | EC |
| company_code | VARCHAR | 公司/账套编码 | EC |
| profitcenter | VARCHAR | 利润中心 | EC |

- 视图 `dp_reports.DP_V_SO_DELIVERY_OFF_4_PMS`(收发日期):

| 字段(外部) | 类型 | 语义说明 | 分级 |
|---|---|---|---|
| orderNumber | VARCHAR | 订单号 | EC |
| linenum | VARCHAR | 行号 | EC |
| itemcode | VARCHAR | 物料编码 | EC |
| salesorder | VARCHAR | 销售订单(对应本地 ppliCode) | EC |
| contract | VARCHAR | 合同号 | EC |
| outdate | DATE | 发货日期(对应本地 deliveryDate) | EC |
| indate | DATE | 退货日期(对应本地 rmaDate) | EC |

##### D365 订单视图与推送 API(外部契约)

> 证据:`sql-map-refresh-data-d365-config.xml:6-10, 26-28`、`D365Api.java:140-381`

- 视图 `DPtech_V_SO_SALES_ORDER_4_PMS`:字段含 orderNumber、contractNo、orderExecNumber、orderCreateTime、customerRequireTime、customerCode、customerName、projectName、orderComment、orderType、compCode、salesType、customInfo(全字段 `SELECT *`,具体外部字段名未在 SQL-map 显式列出)。
- 视图 `DPtech_V_SO_SALES_LINE_4_PMS`:字段含 orderNumber、lineNum、itemCode、itemDesc、orderQuantity、openQuantity、bundleCode、warrantyMonth、lineType、compCode、profitCenter、realOrderExecNumber、customInfo。
- D365 推送 API(写入):
  - `createPOUrl` (默认 `/api/services/IWS_InterfaceInboundServiceGroup/CreatePurchTable/create`):请求体含 `dataAreaId`、`purchTable`(PurchaseHeader)、`purchLine`(List<PurchaseLine>);响应含 `purchId`、`inventTransId`(`D365Api.java:176-257`)。
  - `receiptPOUrl` (默认 `/api/services/IWS_InterfaceInboundServiceGroup/CreatePurchPackingSlip/create`):请求体含 `dataAreaId`、`packingSlipId`、`lines`;响应含 `purchId`、`inventTransId`(`D365Api.java:264-358`)。
  - `paymentSchedUrl`:合同验收交付节点推送,请求体 `{dataAreaId, contract, line}`(`D365Api.java:368-381`)。
  - OAuth2 token 接口:`tokenUrl`(默认 `https://login.microsoftonline.com/{appId}/oauth2/token`),请求参数 `client_id`、`client_secret`、`resource`、`grant_type=client_credentials`(`D365Api.java:140-154`)。

##### OA 人员视图(外部契约)

> 证据:`sql-map-refresh-data-common-config.xml:194-198`

- 视图 `view_person_info_4_pms`:

| 字段(外部) | 类型 | 语义说明 | 分级 |
|---|---|---|---|
| work_card_number | VARCHAR | 工号 | EC |
| staff_name | VARCHAR | 员工姓名 | EC |
| company_mail | VARCHAR | 公司邮箱 | EC |
| phone | VARCHAR | 电话 | EC |
| staff_status | INTEGER | 员工状态(1=有效, 0=失效) | EC |

##### OA 临时授权视图(外部契约)

> 证据:`sql-map-refresh-data-oa-config.xml:6-12, 41-47`

- 视图 `V_DP_TEMP_AUTH_INFO`:字段含 processOrderNum、lendInfoId、projectCode、processStartTime、applyUserCode/Name、marketName/systemName/expendName/industryName、applyDeptCode/Name、applyDate、projectName、applyType/Name、salesUserCode/Name、salesUserMobile、productLine/Name、applyCause、followUpPlan、testStartTime/EndTime、authPlanDate、authDate、resellSuccessfully、useDays、resaleCertificateFile、provideAuthFile、infoFile。
- 视图 `V_DP_TEMP_AUTH_DETAIL`:字段含 id、infoId、contractNum、deviceSerialnum、modelNum、applyCount、isSoftware。

##### SMS 项目属性视图(外部契约)

> 证据:`sql-map-refresh-data-sms-config.xml:40-76`

- 视图 `v_prj_property_4_pm`:

| 字段(外部) | 类型 | 语义说明 | 分级 |
|---|---|---|---|
| orderCodeReal | VARCHAR | 执行单号(映射到本地 orderExecNumber,J→X) | EC |
| projectCode | VARCHAR | 项目编码 | EC |
| projectName | VARCHAR | 项目名称 | EC |
| usernamec | VARCHAR | 销售工号(映射到 salesManCode) | EC |
| realName | VARCHAR | 销售姓名(映射到 salesManName) | EC |
| marketCode/marketName | VARCHAR | 市场部编码/名称 | EC |
| systemid/systemName | VARCHAR | 系统编码/名称 | EC |
| expendId/expendName | VARCHAR | 支出编码/名称 | EC |
| industryid/industryName | VARCHAR | 行业编码/名称 | EC |
| officeCode/officeName | VARCHAR | 办事处编码/名称 | EC |
| serviceTypeName | VARCHAR | 服务类型 | EC |
| channelName | VARCHAR | 渠道名称 | EC |
| engineeFee | DECIMAL | 工程费 | EC |
| objId | VARCHAR | 对象 ID | EC |
| applyType | VARCHAR | 申请类型 | EC |
| corporationCode | VARCHAR | 公司法人编码(默认 `01`) | EC |
| customerProjectName | VARCHAR | 客户项目名 | EC |
| finalCustomerName | VARCHAR | 最终客户名 | EC |
| agentName | VARCHAR | 代理名 | EC |
| majorProjectLevel | VARCHAR | 重大项目级别 | EC |
| projectMoney | DECIMAL | 项目金额 | EC |
| submitTime | DATETIME | 提交时间 | EC |
| predBidDate | DATETIME | 预计中标日期 | EC |
| linkmanName/linkmanTel | VARCHAR | 联系人姓名/电话 | EC |

##### SMS 售前借货视图(外部契约)

> 证据:`sql-map-refresh-data-sms-config.xml:106-159`

- 视图 `v_lend_info_4_pms`:lendInfoId、projectCode、projectName、dutyName、dutyContactWay、decPath、officeCode、marketName、systemName、expendName、industryName、pspm。
- 视图 `v_lend_products_4_pms`:lendInfoId、productfirstName、productName、productsubCode、productSubModel、productSubName、lendNum、memo、orderQty、dlvQty、rmaQty、borrowNum。
- 视图 `v_lend_products_sale_4_pm`:id、orderNumber、ppliCode、orderType、contract、customer、projectName、businessunit、office、dutyperson、itemcode、description、orderQty、dlvQty、rmaQty、lineStatus、createDate、lineId、systemId、canceled、discountVersion、borrowNum。
- 视图 `v_lend_products_tosale_4_pms`:productfirstName、productName、projectCode、productSubCode、productSubModel、productSubName、num、borrowNum、contract、memo。
- 视图 `v_lend_products_torma_4_pms`:同 `v_lend_products_sale_4_pm` 字段 + `productfirstName`、`productName`。
- 视图 `view_refer_product`:projectCode、orderExecNumber、productFirstName、productName、productSubCode、productSubModel、productSubName、num、memo。
- 视图 `v_soleagent_lend_4_pms`:soleAgentLendId、orderExecNumber、orderCodes、contract、projectName、soleAgent、profitCenter。
- 视图 `view_market_system_expend_industry`:marketCode/Name、systemCode/Name、expendCode/Name、industryCode/Name。
- 视图 `v_sms_pb_plan`(收款计划):见 `PlanGetBySMS.java:59-61`。

##### ITR 问题单表(外部契约)

> 证据:`sql-map-refresh-data-itr-config.xml:6-53`

- 表 `pms_incident`(SQL Server,UUID 字段):

| 字段(外部) | 类型 | 语义说明 | 分级 |
|---|---|---|---|
| incidentid | UUID | 问题单 ID | EC |
| ticketnumber | VARCHAR | 工单号 | EC |
| statuscode/statuscodetext | VARCHAR | 工单状态/名称 | EC |
| dptech_casetopic | VARCHAR | 问题单主题 | EC |
| new_memo | LONGTEXT | 描述 | EC |
| ownerid_domainname/createdbyname | VARCHAR | 责任人域名/名称 | EC |
| accepter_domainname/new_accepter_idname | VARCHAR | 受理人域名/名称 | EC |
| processor_domainname/new_processor_idname | VARCHAR | 处理人域名/名称 | EC |
| dptech_supplyornot | VARCHAR | 是否上报 | EC |
| new_typetext | VARCHAR | 服务类型 | EC |
| dptech_questionleveltext | VARCHAR | 问题级别 | EC |
| title | VARCHAR | 工单标题 | EC |
| new_accepttime | DATETIME | 受理时间 | EC |
| dptech_productmodelname/dptech_plantmodelname | VARCHAR | 设备类型/型号 | EC |
| dptech_processing | LONGTEXT | 处理进展 | EC |
| dptech_questionreason | LONGTEXT | 问题根因 | EC |
| dptech_resolvesolutionstext/dptech_solutions | LONGTEXT | 解决方式/方案 | EC |
| new_rma_idname | VARCHAR | RMA 单号 | EC |
| dptech_accidentlist_id | UUID | 事故单号 | EC |
| dptech_casetypetext | VARCHAR | Case 类型 | EC |
| dptech_firstreason/dptech_secondreason | VARCHAR | 原因大类/小类 | EC |
| dptech_projectnumber/dptech_contractno | VARCHAR | 项目编码/合同号 | EC |
| dptech_productsn | VARCHAR | 序列号 | EC |
| dptech_bulletinno/dptech_bugorder | VARCHAR | 技术公告/Bug 单 | EC |
| dptech_productgroupname | VARCHAR | 产品线 | EC |

##### License 授权视图(外部契约)

> 证据:`sql-map-refresh-data-license-config.xml:6-22`

- 视图 `dptech_v_liccode_info`:

| 字段(外部) | 类型 | 语义说明 | 分级 |
|---|---|---|---|
| licenseCode | VARCHAR | License 编码 | EC |
| sn | VARCHAR | 设备序列号 | EC |
| specModel | VARCHAR | 规格型号 | EC |
| contract | VARCHAR | 合同号 | EC |
| contract_type | VARCHAR | 合同类型 | EC |
| item | VARCHAR | 物料编码 | EC |
| status | VARCHAR | 授权状态 | EC |

##### FP 发票平台 API(外部契约)

> 证据:`pms-ext-fp/.../fp/util/FPApi.java:335-453`

- API `archiveUrl`:批量发票查验(multipart/form-data),请求体含 `dataType`、`dataId`、`files[]`、`sourceList`(原发票数据)、`async`(是否异步)、`openId`。
- API `tokenUrl`:Token 获取,参数 `provider`、`openId`、`nickName`,认证方式支持 `bearer/header/query/cookie` 四种。
- API `ssoUrl`:SSO 单点登录(具体使用方式未在 `FPApi` 中显式展开)。

## Success Criteria *(mandatory)*

<!--
  本域成功标准由 NFR-01~NFR-13 转为可测量指标。
  按 SPEC-TYPE-01 原则,标准不绑定具体技术栈(如 Java/Spring/Struts),
  仅描述可测量的运行时行为与约束;外部系统名 SAP/D365/CRM/OA/EHR/ITR/SMS/License/FP 作为业务概念保留。
-->

### Measurable Outcomes

- **SC-001**(源自 NFR-01 多数据源动态切换):系统 MUST 能同时连接 10 个数据源(Local、SAP、D365、CRM、OA、EHR、ITR、SMS、SSE、License),运行时按需切换;同一事务内 MUST NOT 跨数据源操作,违反时事务必须失败。可测量:配置 10 个数据源后,任意同步任务能在源数据源查询、目标数据源插入,事务边界清晰。
- **SC-002**(源自 NFR-02 定时调度):系统 MUST 通过外置调度配置触发同步任务,cron 表达式不在代码中硬编码;`work()` 异常 MUST 被捕获仅记录日志,不抛出,调度器后续触发不被中断。可测量:任意单次任务异常后,下一次 cron 触发仍能正常执行。声明并发保护的任务并发触发时 MUST 串行执行。具体 cron 表达式配置在 Spring/Quartz XML 配置文件(如 `applicationContext-quartz.xml` 或独立 properties 文件)中,代码中不硬编码 [暂定决策:具体配置文件路径待新系统调研 `applicationContext*.xml`/`quartz*.xml`/`scheduler*.xml` 后确认]。
- **SC-003**(源自 NFR-03 同步失败处理与事务一致性):系统 MUST 在同步前插入"进行中"日志;查询、清理、批量插入 MUST 在目标库同一事务内;失败时 MUST 回滚事务并写入完整堆栈到 `refreshException`;同步后(无论成功/失败)MUST 更新 `refreshTo` 与状态字段。可测量:任意同步任务失败后,本地目标表数据保持同步前状态,日志含完整堆栈。
- **SC-004**(源自 NFR-03 批量大小):系统默认批量大小 MUST 为 2000 条/批;`GainPresalesInfoBySMS`、`GainPersonByEHR` MUST 为 1000 条/批。可测量:配置批量大小后,单批插入条数符合预期。
- **SC-005**(源自 NFR-04 双路径同步):SMS/CRM 来源同步任务 MUST 支持两种数据通道,通过 `SystemContext.enableCrm()` 切换;旧路径直连 SMS 数据库视图(只读),新路径调用 CRM API。可测量:切换开关后,任务分别走视图或 API 路径,结果一致。`enableCrm` 切换条件:读取系统参数表 `sys.crm.api.config` 的 `enable` 字段(布尔,默认 false),true 时走 CRM API 路径,false 时走 SMS 数据库视图路径。
- **SC-006**(源自 NFR-05 配置外置化):D365/FP API 配置(tokenUrl、appId、clientSecret、serviceUrl、各业务接口 URL 等)MUST 通过系统参数表 `sys.d365.api.config`、`sys.fp.api` 动态读取;配置变更后 MUST 能动态生效,无需重启。敏感字段(clientSecret)MUST 使用可逆加密存储(如 AES/JASYPT),应用读取时解密,密钥通过独立密钥管理流程分发。
- **SC-007**(源自 NFR-06 接口认证与 Token 缓存):D365/FP API 调用前 MUST 获取 OAuth2 token;token MUST 缓存复用至过期;并发场景下只允许一个线程刷新 token。可测量:并发 10 次请求 D365 API,token 仅被获取 1 次(未过期时)。
- **SC-008**(源自 NFR-07 HTTP 连接池与并发限流):FP 批量发票查验 MUST 使用连接池;限流模式 MUST 支持 `MINUTE`、`SINGLE`、`MULTIPLE` 三种;并发任务超时时间 = `delay × list.size × 20`,最低 30 秒;线程池 MUST 在应用销毁时关闭。可测量:`MULTIPLE` 模式下并发线程数固定为 10,任务超时时间符合公式。
- **SC-009**(源自 NFR-08 失败重试):FP API 请求失败(响应为空或异常)时,若 `enableRetry=true`,MUST 清除 token 缓存后重新请求一次;MUST 通过 `retried=true` 标记防止无限递归;MUST NOT 重试超过一次。可测量:模拟首次失败,系统重试一次后停止。
- **SC-010**(源自 NFR-09 执行单号规范化):跨系统对接时,执行单号 `orderExecNumber` 中的 `J` MUST 统一替换为 `X`;本地匹配阶段 MUST 生成 `orderExecNumberShort`(去版本号:`CONCAT(LEFT(,12), SUBSTR(,14))`)。可测量:SAP/SMS 同步后,本地镜像表 `orderExecNumber` 不含 `J`,`orderExecNumberShort` 长度为原长度减 1。
- **SC-011**(源自 NFR-10 动态表结构演进):部分 SMS 同步表字段新增时,系统 MUST 在运行时检测并自动 `ALTER TABLE ADD`;字段类型 MUST 明确指定(如 `varchar(10)`、`varchar(255)`);DDL 操作 MUST NOT 放入事务。可测量:`pm_project_property_from_sms` 缺少 `serviceTypeName` 字段时,首次同步前自动添加。动态 ALTER TABLE 自检仅适用于 `pm_project_property_from_sms`(由 `GainPrjPropertyBySMS` 触发),其他镜像表字段变更通过手工迁移。
- **SC-012**(源自 NFR-11 数据一致性保障):系统 MUST 在同步前清理目标表(全量 `truncate` 或按 `dataSource` 条件 `delete`);同步过程 MUST 在单事务内,失败回滚;多步同步任务(如 `GainOrderByERP` 的 5 个子步骤)每个子步骤 MUST 独立事务,前序失败不影响后序执行(只记录日志)。可测量:多步任务中第 2 步失败后,第 3、4、5 步仍执行,各步日志独立记录。多步任务前序失败可能导致数据不一致(如订单同步失败但发货状态更新成功);新系统 MUST 沿用现状(各子步骤独立事务,前序失败不阻断后序),SHOULD 提供补偿任务手动重试失败步骤,不强制全任务级自动补偿机制。
- **SC-013**(源自 NFR-12 同步日志可观测性):每次同步任务执行后,`fnd_data_refresh_log`(旧版)或 `t_sync_log`(新版)MUST 记录开始时间、结束时间、状态、异常堆栈;MUST 支持按 `targetMethod`、`tableObject`、`dataFrom`、`dataTo` 模糊查询分页;日志表读写分离(查询不影响同步事务);异常堆栈需完整保留(`LONGTEXT`/`LONGVARCHAR`)。可测量:任意任务执行后,日志表新增一条记录,含完整开始/结束时间与状态。
- **SC-014**(源自 NFR-13 SMS 视图同步的 SQL 注入风险):新系统 MUST 使用参数化查询或 ORM,不得直接拼接用户可控字段;`PlanGetBySMS` 旧实现的字符串拼接 SQL MUST 在新系统中改造。可测量:代码审计无字符串拼接 SQL;`contractNo` 等用户可控字段均通过参数绑定传入。
- **SC-015**(数据一致性,源自 DATA-REUSE-01):所有本地表 MUST 沿用既有结构,不新建重复表;同步任务 MUST 复用既有镜像表(16 张)与既有业务表(`pm_project_header`、`pm_project_member`、`pm_project_task`、`pm_project_product_line` 等),不新建同类替代表。可测量:与既有 schema 比对,无新增重复表。

## Assumptions

- **外部系统可用性**:假设外部系统(SAP/D365/CRM/OA/EHR/ITR/SMS/License/FP)的视图、表、API 在同步窗口期内可用;如不可用,同步任务按失败处理并记录日志,不影响其他任务。
- **外部系统视图稳定性**:假设外部系统视图/表结构稳定,字段不随意变更;若字段新增,部分表(如 `pm_project_property_from_sms`)支持动态 `ALTER TABLE ADD`,其他表需手工迁移。
- **网络连通性**:假设 PMS 与各外部系统网络连通,防火墙放行所需端口;D365/FP API 假设可通过公网或专线访问,OAuth2 token 接口可达。
- **调度器外置**:假设定时调度器外置,所有同步任务的 cron 表达式由调度配置文件管理,代码中不硬编码;具体 cron 表达式配置位置见 SC-002。
- **配置参数表存在**:假设系统参数表 `sys.d365.api.config`、`sys.fp.api` 已初始化,含 tokenUrl、appId、clientSecret、serviceUrl、各业务接口 URL 等字段;敏感字段加密存储要求见 SC-006。
- **本地基础表存在**:假设 `pm_project_header`、`pm_project_member`、`pm_project_task`、`pm_project_product_line`、`pm_project_contract`、`pm_project_related_party`、`pm_project_group_relationship`、`fnd_basic_data` 等业务基础表已存在并被其他域维护。
- **双路径切换开关**:假设 `SystemContext.enableCrm()` 的切换逻辑由系统配置决定;切换条件见 SC-005。
- **日志体系过渡**:假设旧版 `fnd_data_refresh_log` 与新版 `t_sync_log` 在过渡期并存;新系统统一采用 `t_sync_log` 模型,`fnd_data_refresh_log` 标记为废弃只读保留(不再写入),SC-013 适用表为 `t_sync_log`。
- **`syncType` 枚举定义**:`t_sync_log.syncType` 类型为 SMALLINT,枚举值已定义:1=FULL_SYNC(全量同步), 2=INCREM_SYNC(增量同步)(见 `core/src/main/java/com/dp/plat/core/schedule/SyncType.java`)。
- **多步任务补偿假设**:假设多步同步任务前序失败不阻断后序是业务可接受的;沿用现状,不强制全局补偿机制;新系统 SHOULD 提供补偿任务手动重试失败步骤。
- **UUID 类型转换无损**:假设 ITR 表 `pms_incident` 中的 UUID 字段经类型处理器转字符串后落入本地 VARCHAR 字段无损,字符串长度足够。
- **OAuth2 token 有效期**:假设 D365/FP 的 OAuth2 token 有效期足够长(分钟级以上),不会在单次同步任务中过期多次;若过期,token 缓存机制能自动重取。
- **FP 发票文件可读**:假设 FP 发票查验的发票文件可读,multipart/form-data 上传成功;若文件损坏,失败发票包装为 `Response.failure(message)` 返回。
