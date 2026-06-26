# dppms_d365 数据库索引分析报告

> 基于生产环境 MySQL 数据库 dppms_d365 实际结构生成
> 引擎: MySQL 8.0.16 | 核心业务表: 151 张 | 索引总数: 639 | 索引类型: BTREE

---

## 一、索引统计总览

### 1.1 索引类型分布

| 索引类型 | 数量 | 占比 | 说明 |
|----------|------|------|------|
| 主键索引 (PRIMARY) | 146 | 22.8% | 每表一个，聚簇索引 |
| 唯一索引 (UNIQUE) | 28 | 4.4% | 唯一约束，含复合唯一索引 |
| 普通索引 (INDEX) | 465 | 72.8% | 非唯一索引，加速查询 |

### 1.2 索引数量分布

| 索引数量区间 | 表数量 | 占比 | 代表表 |
|--------------|--------|------|--------|
| 1 个（仅主键） | 52 | 34.4% | addressee_info, agent_info, app_accessory_info 等 |
| 2-5 个 | 58 | 38.4% | t_user, t_role, pm_project, fb_contract 等 |
| 6-10 个 | 27 | 17.9% | pm_project_maintenance, pm_project_member, prob_softwares 等 |
| 10 个以上 | 14 | 9.3% | fb_shipment_barcode, pm_project_soft_version, pm_daily_report 等 |

### 1.3 索引最多的表 TOP 10

| 表名 | 索引数量 | 说明 |
|------|----------|------|
| pm_daily_report | 12 | 日报表，多维度查询 |
| fb_shipment_barcode | 11 | 发货条码，多维度查询 |
| pm_project_maintenance | 11 | 维护记录，多维度查询 |
| pm_project_soft_version | 8 | 软件版本，多维度查询 |
| pm_project_soft_version_history | 8 | 软件版本历史 |
| pm_project_member | 6 | 项目成员 |
| pm_project_task | 6 | 项目任务 |
| fb_shipment_barcode_change_log | 6 | 条码变更日志 |
| pm_cl_quesnaire_result_line | 6 | 问卷结果行 |
| pm_dispatch_project_header | 6 | 外派项目头 |

### 1.4 无索引表（仅主键）

以下 52 张表仅有主键索引，无任何二级索引，可能存在查询性能问题：

```
addressee_info, agent_info, app_accessory_info, app_comment, back_type, bar,
brw_app_info, brw_spare_info, data_field_relation, department,
dp_erp_purchase_order_header, dp_erp_purchase_order_line,
dp_erp_purchase_receipt_header, dp_erp_purchase_receipt_line,
ehr_company, ehr_job, ehr_login, fb_ft_result1, fb_ft_result2, fb_items2,
fb_market_system, fb_soft_version, fnd_basic_data_type, fnd_data_refresh_log,
fnd_department, fnd_files, fnd_mails, fnd_menus, fnd_roles, fnd_role_menus,
fnd_spms_arg, fnd_sys_arg, fnd_user_info, fnd_user_menus, fnd_user_power,
mes_oqc_info, mes_seal_info, pm_basic_prj_deliver, pm_column_of_relationship,
pm_common_related_data, pm_facilitator, pm_notification_template,
pm_project_incident_table_from_itr, pm_project_license_info_from_license,
pm_project_maintenance_sectary_from_sse, pm_project_market_relations_from_sms,
pm_project_spot_check_ignore_item, pm_report_line_data, pm_subcontract_deliver_files,
pm_subcontract_facilitator, pm_subcontract_project_callback, pm_subcontract_project_price,
prob_main, prob_product_component, prob_read_log, prob_restore_process,
prob_restore_weekly, prob_soft_version, project_info_from_sms, rma_applicant,
rma_app_info, rma_bar, rma_info2mes_result, rma_repair_report_from_mes,
rma_spare_info, role, serve_type, spare_parts, spare_parts_applicant,
sys_state_or_type, tain_type, t_company, t_data_field_relation, t_data_operation,
t_dictionary, t_down_log, t_file, t_file_type, t_mails, t_notify_template,
t_sync_log, t_sync_state, t_sys_variable, t_user_login_record, user, user_info,
user_modules, user_permissions, warehouse, warehouse_info, warranty_change_logs,
warranty_info, workflow_info
```

---

## 二、逐表索引分析

### 2.1 用户权限模块

#### t_user（用户表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | user_id | 主键 | 有效 | 聚簇索引 |
| unique_username | user_name | 唯一 | 有效 | 用户名唯一约束 |

**评估**: 索引设计合理，主键 + 用户名唯一索引覆盖主要查询场景。

#### t_user_info（用户信息表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | id | 主键 | 有效 | 聚簇索引 |
| fk_userInfo_userId | user_id, compID | 唯一 | 有效 | 用户ID+公司唯一 |
| compID | compID | 普通 | 有效 | 按公司查询 |
| depID | depID | 普通 | 有效 | 按部门查询 |
| jobID | jobID | 普通 | 有效 | 按岗位查询 |
| reportTo | reportTo | 普通 | 有效 | 按上级查询 |
| wfreportTo | wfreportTo | 普通 | 有效 | 按职能上级查询 |
| workNo | workNo | 普通 | 有效 | 按工号查询 |

**评估**: 索引覆盖全面，支持按公司、部门、岗位、上级、工号等多维度查询。`fk_userInfo_userId` 复合唯一索引同时满足外键约束和唯一性校验。

#### t_role（角色表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | role_id | 主键 | 有效 | 聚簇索引 |
| role_name | role_name | 普通 | 有效 | 按角色名查询 |

**评估**: 索引设计合理。`role_name` 虽然语义上应唯一，但使用普通索引而非唯一索引，可能存在历史原因。

#### t_user_role（用户角色关联表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | id | 主键 | 有效 | 聚簇索引 |
| unique_userId_roleId | user_id, role_id, comp_id | 唯一 | 有效 | 用户+角色+公司唯一 |
| user_id | user_id | 普通 | 有效 | 按用户查询角色 |
| t_user_role_ibfk_2 | role_id | 普通 | 有效 | 按角色查询用户（外键索引） |

**评估**: 索引设计优秀。三字段复合唯一索引防止重复授权，单字段索引支持反向查询。`t_user_role_ibfk_2` 是外键自动生成的索引。

#### t_role_permission（角色权限关联表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | id | 主键 | 有效 | 聚簇索引 |
| role_id | role_id, permission_id | 唯一 | 有效 | 角色+权限唯一 |
| permission_id | permission_id | 普通 | 有效 | 按权限查询角色 |
| role_id_2 | role_id | 普通 | 冗余 | 与复合索引前缀重复 |

**评估**: `role_id_2` 索引与 `role_id` 复合索引的前缀重复，属于冗余索引，建议删除。

### 2.2 基础平台模块

#### fnd_basic_data（基础数据表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | id | 主键 | 有效 | 聚簇索引 |
| basicDataId | basicDataId | 普通 | 有效 | 按数据ID查询 |
| basicDataId_dataTypeCode | dataTypeCode, basicDataId | 普通 | 有效 | 按类型+ID查询 |

**评估**: 索引设计合理。`basicDataId` 单字段索引与复合索引 `basicDataId_dataTypeCode` 的字段顺序不同（前者以 basicDataId 开头，后者以 dataTypeCode 开头），不构成冗余。

#### fnd_basic_prjstate（项目状态基础数据表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | id | 主键 | 有效 | 聚簇索引 |
| dataTypeCode | dataTypeCode, basicDataId | 普通 | 有效 | 按类型+ID查询 |

**评估**: 索引设计合理。

#### fnd_company（公司表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | id | 主键 | 有效 | 聚簇索引 |
| code | code | 普通 | 有效 | 按编码查询 |
| pid | pid | 普通 | 有效 | 按父机构查询 |

**评估**: 索引设计合理，支持按编码和父机构查询组织树。

### 2.3 项目管理模块

#### pm_project（项目主表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | projectId | 主键 | 有效 | 聚簇索引 |
| projectCode_index | projectCode, projectType | 普通 | 有效 | 按编码+类型查询 |
| projectType_projectId_IDX | projectType, projectId | 普通 | 有效 | 按类型+ID查询 |
| department | column001 | 普通 | 有效 | 按办事处查询 |

**评估**: 索引设计合理。两个复合索引字段顺序不同，分别支持不同查询场景。`column001` 是办事处编码的泛化字段，索引命名不够直观。

#### pm_project_state（项目状态表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| index_projectId | projectId | 唯一 | 有效 | 项目ID唯一（1:1关系） |
| projectPlanState | projectPlanState | 普通 | 有效 | 按计划状态查询 |
| shipmentState | shipmentState | 普通 | 有效 | 按发货状态查询 |

**评估**: 索引设计合理。`index_projectId` 唯一索引确保与 pm_project 的 1:1 关系。

#### pm_project_maintenance（项目维护表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | id | 主键 | 有效 | 聚簇索引 |
| projectId | projectId | 普通 | 有效 | 按项目查询 |
| projectCode | projectCode | 普通 | 有效 | 按项目编码查询 |
| projectType | projectType | 普通 | 有效 | 按项目类型查询 |
| officeCode | officeCode | 普通 | 有效 | 按办事处查询 |
| type | type | 普通 | 有效 | 按任务性质查询 |
| category | category, subCategory | 普通 | 有效 | 按分类+小类查询 |
| subCategory | subCategory | 普通 | 冗余 | 与复合索引前缀重复 |
| createBy | createBy | 普通 | 有效 | 按创建人查询 |
| createTime | createTime | 普通 | 有效 | 按创建时间查询 |
| processTime_IDX | processTime | 普通 | 有效 | 按处理时间查询 |

**评估**: `subCategory` 单字段索引与 `category` 复合索引的第二字段重复，但如果经常单独按 subCategory 查询（不带 category），则该索引仍有价值。建议根据实际查询模式评估是否删除。索引数量较多（11个），需关注写入性能。

#### pm_project_member（项目成员表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | id | 主键 | 有效 | 聚簇索引 |
| projectId_role | projectId, memberRole | 普通 | 有效 | 按项目+角色查询 |
| projectId_type | projectId, projectType | 普通 | 有效 | 按项目+类型查询 |
| memberCode_IDX | memberCode, projectId, projectType | 普通 | 有效 | 按人员查询参与项目 |

**评估**: 索引设计优秀。三个复合索引分别支持按项目查成员、按项目类型查成员、按人员查项目三种核心查询场景。

#### pm_project_soft_version（项目软件版本表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | id | 主键 | 有效 | 聚簇索引 |
| barcode | barCode | 普通 | 有效 | 按条码查询 |
| pm_project_soft_version_conp_IDX | conp | 普通 | 有效 | 按组件版本查询 |
| projectBarcodeValid | projectId, barCode, datastate | 普通 | 有效 | 按项目+条码+状态查询 |
| idx_conp_item_query | datastate, conpType, conpSeries, conpMark, itemCode, projectId | 普通 | 有效 | 六字段复合查询 |

**评估**: 索引设计专业。`idx_conp_item_query` 六字段复合索引覆盖了复杂的版本查询场景，字段顺序遵循最左前缀原则。`projectBarcodeValid` 复合索引支持按项目查询有效条码的版本信息。

#### pm_project_task（项目任务表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | taskId | 主键 | 有效 | 聚簇索引 |
| projectId | projectId, projectType | 普通 | 有效 | 按项目+类型查询 |
| projectType | projectType, projectId | 普通 | 冗余 | 与上一个索引字段相同顺序相反 |
| taskTypeCode_Id | taskTypeCode, taskTypeId | 普通 | 有效 | 按任务类型查询 |

**评估**: `projectType` 索引与 `projectId` 索引字段相同但顺序相反，如果查询场景同时需要按 projectId 和 projectType 查询，MySQL 优化器可能选择不同索引。建议评估是否可以合并。

### 2.4 售前管理模块

#### pm_presales_project_header（售前项目头表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | presalesId | 主键 | 有效 | 聚簇索引 |
| projectCode | projectCode | 普通 | 有效 | 按项目编码查询 |
| instId | instId | 普通 | 有效 | 按流程实例查询 |
| lendInfoId | lendInfoId | 普通 | 有效 | 按借货信息查询 |

**评估**: 索引设计合理，覆盖主要查询场景。

#### pm_presales_project_callback（售前项目回访表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | id | 主键 | 有效 | 聚簇索引 |
| presalesId | presalesId | 普通 | 有效 | 按售前项目查询 |
| taskId | taskId | 普通 | 有效 | 按任务查询 |
| quesnaireId | quesnaireId | 普通 | 有效 | 按问卷查询 |

**评估**: 索引设计合理。

### 2.5 转包管理模块

#### pm_subcontract_project_line（转包项目行表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | id | 主键 | 有效 | 聚簇索引 |
| unique_index | subcontractId, barcode | 唯一 | 有效 | 转包+条码唯一 |
| projectId | projectId | 普通 | 有效 | 按主项目查询 |
| contractNo | contractNo | 普通 | 有效 | 按合同号查询 |
| itemCode | itemCode | 普通 | 有效 | 按物料编码查询 |
| barcode | barcode | 普通 | 有效 | 按条码查询 |

**评估**: 索引设计优秀。复合唯一索引防止重复，单字段索引支持多维度查询。

#### pm_dispatch_project_header（外派项目头表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | id | 主键 | 有效 | 聚簇索引 |
| UNIQUE_dispatchSeq | dispatchSeq | 唯一 | 有效 | 外派编号唯一 |
| subcontractNo | dispatchNo | 普通 | 有效 | 按外派合同号查询 |
| facilitatorId | facilitatorCode | 普通 | 有效 | 按服务商查询 |
| officeCode | officeCode | 普通 | 有效 | 按办事处查询 |
| profitDepCode | profitDepCode | 普通 | 有效 | 按收益部门查询 |
| smsProjectCode | smsProjectCode | 普通 | 有效 | 按SMS项目编码查询 |

**评估**: 索引设计全面，覆盖外派项目的多维度查询场景。

### 2.6 回访管理模块

#### pm_cl_quesnaire_result_line（问卷结果行表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | id | 主键 | 有效 | 聚簇索引 |
| quesnaireResultHeaderId | quesnaireResultHeaderId, quesTypeForCB | 普通 | 有效 | 按结果头+类型查询 |
| quesnaireTemplateHeaderId | quesnaireTemplateHeaderId, quesnaireTemplateLineId | 普通 | 有效 | 按模板头+行查询 |
| quesnaireTemplateLineId | quesnaireTemplateLineId, questionTemplateOptId | 普通 | 有效 | 按模板行+选项查询 |

**评估**: 索引设计专业，三个复合索引分别支持按结果头、模板头、模板行查询，字段顺序合理。

### 2.7 问题管理模块

#### prob_product（问题产品表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | id | 主键 | 有效 | 聚簇索引 |
| probId_Status_IDX | probId, status | 普通 | 有效 | 按问题+状态查询 |
| probId_status_item_IDX | probId, status, itemCode | 普通 | 有效 | 按问题+状态+物料查询 |

**评估**: 两个复合索引字段有重叠，`probId_status_item_IDX` 包含 `probId_Status_IDX` 的前两个字段。如果经常需要按 probId + status + itemCode 查询，则 `probId_Status_IDX` 可能冗余。建议评估是否删除 `probId_Status_IDX`。

#### prob_softwares（问题软件表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | id | 主键 | 有效 | 聚簇索引 |
| probId_datastate_IDX | probId, datastate | 普通 | 有效 | 按问题+状态查询 |
| datastate_entry_probId_IDX | datastate, entryType, entrySeries, probId | 普通 | 有效 | 按状态+类型+系列+问题查询 |
| conp | conp | 普通 | 有效 | 按组件版本查询 |
| cpld | cpld | 普通 | 有效 | 按CPLD版本查询 |
| boot | boot | 普通 | 有效 | 按BOOT版本查询 |
| pcb | pcb | 普通 | 有效 | 按PCB版本查询 |
| affectedType | affectedType | 普通 | 有效 | 按影响类型查询 |

**评估**: 索引设计全面，支持按问题、状态、版本号、影响类型等多维度查询。`datastate_entry_probId_IDX` 四字段复合索引覆盖了复杂的筛选场景。

### 2.8 发货反馈模块

#### fb_shipment_barcode（发货条码表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | id | 主键 | 有效 | 聚簇索引 |
| uuid | uuid | 唯一 | 有效 | UUID唯一约束 |
| barcode_pack_rma_IDX | barcode, pack_id, rma_no | 普通 | 有效 | 按条码+发货+RMA查询 |
| barcode_rma_pack_IDX | barcode, rma_no, pack_id | 普通 | 冗余 | 字段相同顺序不同 |
| pack_barcode_IDX | pack_id, barcode, rma_no | 普通 | 有效 | 按发货+条码+RMA查询 |
| pack_item_IDX | pack_id, item, rma_no | 普通 | 有效 | 按发货+物料+RMA查询 |
| orderNumber_barcode_IDX | barcode, orderNumber | 普通 | 有效 | 按条码+订单查询 |
| barcode2 | barcode2 | 普通 | 有效 | 按母公司条码查询 |
| item | item | 普通 | 有效 | 按物料编码查询 |
| item2 | item2 | 普通 | 有效 | 按母公司物料查询 |

**评估**: `barcode_rma_pack_IDX` 与 `barcode_pack_rma_IDX` 字段相同但顺序不同，属于冗余索引，建议删除其中一个。索引数量较多（11个），需关注写入性能。

#### fb_shipment_barcode_change_log（条码变更日志表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | logID | 主键 | 有效 | 聚簇索引 |
| idx_dataid_lasted_logid | dataId, lasted, logID | 普通 | 有效 | 按数据+最新+日志查询 |
| idx_lasted_dataid_logid | lasted, dataId, logID | 普通 | 有效 | 按最新+数据+日志查询 |
| idx_syncFlag_lasted | syncFlag, lasted | 普通 | 有效 | 按同步+最新查询 |

**评估**: 索引设计专业。前两个复合索引字段相同但顺序不同，分别支持以 dataId 优先和以 lasted 优先的查询场景，不构成冗余。

### 2.9 ERP 集成模块

#### pm_order_data_from_erp（ERP订单数据表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | id | 主键 | 有效 | 聚簇索引 |
| contractNo | contractNo | 普通 | 有效 | 按合同号查询 |
| orderNumber | orderNumber | 普通 | 有效 | 按订单号查询 |
| orderExecNumber | orderExecNumber | 普通 | 有效 | 按执行号查询 |
| orderType | orderType, salesType | 普通 | 有效 | 按类型+销售类型查询 |

**评估**: 索引设计合理，覆盖按合同号、订单号、执行号、类型等多维度查询。

### 2.10 EHR 模块

#### ehr_employee（员工表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | empID | 主键 | 有效 | 聚簇索引 |
| compID | compID | 普通 | 有效 | 按公司查询 |
| depID | depID | 普通 | 有效 | 按部门查询 |
| jobID | jobID | 普通 | 有效 | 按岗位查询 |
| reportTo | reportTo | 普通 | 有效 | 按上级查询 |
| wfreportTo | wfreportTo | 普通 | 有效 | 按职能上级查询 |
| workNo | workNo | 普通 | 有效 | 按工号查询 |

**评估**: 索引设计全面，支持按公司、部门、岗位、上级、工号等多维度查询。`workNo` 语义上应唯一，建议考虑改为唯一索引。

### 2.11 工作流模块

#### pm_workflow（工作流业务关联表）
| 索引名 | 字段 | 类型 | 有效性 | 说明 |
|--------|------|------|--------|------|
| PRIMARY | id | 主键 | 有效 | 聚簇索引 |
| objDataKey | objType, objId, dataType, dataId | 普通 | 有效 | 按业务对象查询 |
| worfFlow_objId | objId | 普通 | 冗余 | 与复合索引前缀重复 |
| procInstId | procInstId | 普通 | 有效 | 按流程实例查询 |

**评估**: `worfFlow_objId` 索引与 `objDataKey` 复合索引的第二字段重复（objDataKey 以 objType 开头，不以 objId 开头），因此 `worfFlow_objId` 不算冗余，支持单独按 objId 查询。索引设计合理。

---

## 三、索引有效性评估

### 3.1 有效索引（推荐保留）

以下索引设计合理，覆盖核心查询场景，建议保留：

| 表名 | 索引名 | 评估理由 |
|------|--------|----------|
| t_user_info | fk_userInfo_userId | 复合唯一索引，满足外键+唯一约束 |
| t_user_role | unique_userId_roleId | 三字段复合唯一，防止重复授权 |
| pm_project | projectCode_index | 支持按编码+类型查询 |
| pm_project_member | memberCode_IDX | 支持按人员查询参与项目 |
| pm_project_soft_version | idx_conp_item_query | 六字段复合索引，覆盖复杂查询 |
| pm_subcontract_project_line | unique_index | 复合唯一，防止重复条码 |
| pm_cl_quesnaire_result_line | 三个复合索引 | 分别支持按结果头、模板头、模板行查询 |
| fb_shipment_barcode_change_log | idx_dataid_lasted_logid | 支持查询某数据的最新变更 |
| pm_workflow | objDataKey | 四字段复合索引，精确定位业务数据 |

### 3.2 冗余索引（建议删除）

以下索引与已有复合索引的前缀重复，建议删除以减少写入开销：

| 表名 | 冗余索引 | 重复原因 | 建议 |
|------|----------|----------|------|
| t_role_permission | role_id_2 (role_id) | 与 role_id (role_id, permission_id) 前缀重复 | 删除 role_id_2 |
| fb_shipment_barcode | barcode_rma_pack_IDX (barcode, rma_no, pack_id) | 与 barcode_pack_rma_IDX (barcode, pack_id, rma_no) 字段相同顺序不同 | 根据查询模式保留一个 |
| pm_project_maintenance | subCategory (subCategory) | 与 category (category, subCategory) 第二字段重复 | 评估单独按 subCategory 查询频率后决定 |

### 3.3 缺失索引（建议新增）

以下表的高频查询字段缺少索引，建议新增：

| 表名 | 建议索引字段 | 理由 |
|------|--------------|------|
| fnd_mails | sendFlag | 按发送状态查询待发邮件 |
| fnd_files | fileType | 按文件分类查询 |
| t_sys_log | type, create_date | 按类型+时间查询日志（已有复合索引 create_date+description，但字段不匹配） |
| pm_project_instruction | dataType | 按数据类型（批示/反馈）查询 |
| pm_project_log | handleUser | 按操作用户查询 |
| pm_project_weekly | projectId 已有，建议增加 weeklyId 的子表索引 | 确认子表已有索引 |
| prob_main | 建议增加 status 字段索引 | 按问题状态查询 |
| fb_contract | customer_name | 按客户名查询 |
| ehr_employee | empName | 按员工名查询（当前只有 workNo 索引） |

### 3.4 索引设计问题

#### 问题1: 索引命名不规范

部分索引命名不一致，存在以下问题：
- 部分索引使用 `index_xxx` 格式（如 `index_id`）
- 部分索引使用 `xxx_IDX` 格式（如 `processTime_IDX`）
- 部分索引使用 `xxx_index` 格式（如 `projectCode_index`）
- 部分索引使用字段名作为索引名（如 `projectId`、`contractNo`）
- 外键自动生成的索引使用 `xxx_ibfk_N` 格式

**建议**: 统一索引命名规范，建议格式：`idx_<表名简写>_<字段简写>` 或 `uk_<表名简写>_<字段简写>`（唯一索引）。

#### 问题2: 泛化字段索引命名不直观

`pm_project` 表的 `column001` 字段索引名为 `department`，虽然语义清晰但与字段名不一致，容易混淆。

**建议**: 在索引注释中标注泛化字段的真实含义。

#### 问题3: 唯一约束使用普通索引

以下字段的语义应为唯一，但使用了普通索引：

| 表名 | 字段 | 当前索引类型 | 建议 |
|------|------|--------------|------|
| t_role | role_name | 普通 | 考虑改为唯一索引 |
| ehr_employee | workNo | 普通 | 考虑改为唯一索引 |
| fnd_department | departmentNum | 唯一 | 已正确 |

---

## 四、索引优化建议

### 4.1 短期优化（低风险）

1. **删除冗余索引**
   - 删除 `t_role_permission.role_id_2` 索引
   - 评估并删除 `fb_shipment_barcode.barcode_rma_pack_IDX` 或 `barcode_pack_rma_IDX` 之一
   - 评估 `pm_project_maintenance.subCategory` 索引的必要性

2. **补充缺失索引**
   - 为 `fnd_mails.sendFlag` 添加索引（待发邮件查询）
   - 为 `fnd_files.fileType` 添加索引（文件分类查询）
   - 为 `pm_project_instruction.dataType` 添加索引（批示类型查询）
   - 为 `prob_main` 添加状态字段索引（如存在状态字段）

3. **统一索引命名**
   - 制定索引命名规范文档
   - 逐步重命名不规范索引（需在低峰期执行）

### 4.2 中期优化（中等风险）

1. **评估复合索引字段顺序**
   - 分析 `pm_project_task` 表的 `projectId` 和 `projectType` 两个索引的实际使用情况
   - 分析 `pm_project_maintenance` 表 11 个索引的使用频率，考虑合并或删除低频索引

2. **优化唯一约束**
   - 将 `t_role.role_name` 改为唯一索引（需先确认无重复数据）
   - 将 `ehr_employee.workNo` 改为唯一索引（需先确认无重复数据）

3. **添加覆盖索引**
   - 对于高频查询场景，考虑添加覆盖索引避免回表
   - 例如 `pm_project` 表按 `projectCode` 查询时，可考虑覆盖 `projectName`、`projectState` 等字段

### 4.3 长期优化（需充分测试）

1. **索引碎片整理**
   - 定期执行 `ANALYZE TABLE` 更新索引统计信息
   - 对高频写入表定期执行 `OPTIMIZE TABLE` 整理碎片

2. **查询计划分析**
   - 使用 `EXPLAIN` 分析慢查询的执行计划
   - 根据实际查询模式调整索引设计

3. **索引监控**
   - 开启 MySQL 慢查询日志，识别未使用索引的查询
   - 使用 `performance_schema` 监控索引使用情况
   - 定期清理未使用的索引

---

## 五、索引与数据量关系分析

### 5.1 大表索引评估

以下表数据量较大（根据 table_summary.csv），需特别关注索引效率：

| 表名 | 行数 | 索引数量 | 评估 |
|------|------|----------|------|
| act_hi_actinst | 155827 | 多个 | Activiti 历史活动表，索引由引擎管理 |
| act_hi_identitylink | 143081 | 多个 | Activiti 历史身份链接表 |
| act_hi_comment | 66552 | 多个 | Activiti 历史批注表 |
| act_hi_procinst | 18833 | 多个 | Activiti 历史流程实例表 |
| act_evt_log | 6398 | 1 | 事件日志表，仅主键索引，查询性能可能受影响 |

### 5.2 小表索引评估

以下表数据量很小（<100行），索引对查询性能影响有限：

| 表名 | 行数 | 索引数量 | 评估 |
|------|------|----------|------|
| find_in_set_help | 1 | 1 | 辅助表，无需额外索引 |
| fb_ft_result1 | 2 | 1 | 数据量极小 |
| fb_ft_result2 | 2 | 1 | 数据量极小 |
| fb_market_system | 4 | 0 | 数据量极小，无需索引 |
| fb_items | 4 | 2 | 数据量极小，索引可考虑删除 |
| department | 4 | 2 | 数据量极小 |
| fb_items2 | 4 | 0 | 数据量极小 |

**建议**: 对于数据量极小（<100行）且增长缓慢的表，可考虑删除非主键索引以减少维护开销。

---

## 六、总结

### 6.1 索引设计整体评价

本数据库的索引设计整体质量**中等偏上**，具有以下特点：

**优点：**
- 核心业务表（如 pm_project、pm_project_maintenance、pm_project_soft_version）索引设计专业，复合索引字段顺序合理
- 关联表（如 t_user_role、pm_subcontract_project_line）正确使用复合唯一索引防止重复
- 工作流相关表（pm_workflow、dp_act_unify_task）索引覆盖业务查询场景

**不足：**
- 52 张表仅有主键索引，部分表存在查询性能风险
- 存在 3-5 个冗余索引，增加写入开销
- 索引命名不规范，缺乏统一标准
- 部分唯一语义字段使用普通索引

### 6.2 优化优先级

| 优先级 | 优化项 | 预期收益 | 风险 |
|--------|--------|----------|------|
| P0 | 删除冗余索引 | 减少写入开销 | 低 |
| P0 | 补充缺失索引 | 提升查询性能 | 低 |
| P1 | 统一索引命名 | 提升可维护性 | 低 |
| P1 | 优化唯一约束 | 保证数据一致性 | 中（需确认无重复） |
| P2 | 评估复合索引顺序 | 优化查询效率 | 中（需测试） |
| P2 | 添加覆盖索引 | 减少回表 IO | 中（需测试） |
| P3 | 索引碎片整理 | 提升索引效率 | 低 |
| P3 | 索引监控体系 | 持续优化基础 | 低 |

### 6.3 后续建议

1. **建立索引审计机制**：定期审查索引使用情况，清理冗余索引，补充缺失索引
2. **制定索引设计规范**：明确索引命名、字段顺序、复合索引设计原则
3. **集成慢查询监控**：将慢查询分析与索引优化纳入日常运维
4. **建立索引变更流程**：索引变更需经过测试环境验证，并在低峰期执行
