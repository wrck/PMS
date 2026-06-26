# DPPMS_D365 数据库字典 - 第三部分：历史迁移表、Activiti引擎表、临时表与视图

> 生成时间：2026-06-13 | 数据库：dppms_d365 | 基准来源：生产数据库实际结构
> 格式标准：与第一部分、第二部分保持完全一致（7列字段表、4列索引表、属性表格）

---

## 目录索引

### 一、Activiti工作流引擎表（act_*）- 26张

[act_evt_log](#act_evt_log) | [act_ge_bytearray](#act_ge_bytearray) | [act_ge_property](#act_ge_property) | [act_hi_actinst](#act_hi_actinst) | [act_hi_attachment](#act_hi_attachment) | [act_hi_comment](#act_hi_comment) | [act_hi_detail](#act_hi_detail) | [act_hi_identitylink](#act_hi_identitylink) | [act_hi_procinst](#act_hi_procinst) | [act_hi_taskinst](#act_hi_taskinst) | [act_hi_varinst](#act_hi_varinst) | [act_id_group](#act_id_group) | [act_id_info](#act_id_info) | [act_id_membership](#act_id_membership) | [act_id_user](#act_id_user) | [act_procdef_info](#act_procdef_info) | [act_re_deployment](#act_re_deployment) | [act_re_model](#act_re_model) | [act_re_procdef](#act_re_procdef) | [act_ru_event_subscr](#act_ru_event_subscr) | [act_ru_execution](#act_ru_execution) | [act_ru_identitylink](#act_ru_identitylink) | [act_ru_job](#act_ru_job) | [act_ru_task](#act_ru_task) | [act_ru_task_callback_task_w04649](#act_ru_task_callback_task_w04649) | [act_ru_variable](#act_ru_variable)

### 二、Firebird迁移表（fb_*）- 15张

[fb_contract](#fb_contract) | [fb_ft_result1](#fb_ft_result1) | [fb_ft_result2](#fb_ft_result2) | [fb_items](#fb_items) | [fb_items2](#fb_items2) | [fb_market_system](#fb_market_system) | [fb_office_relationship](#fb_office_relationship) | [fb_service](#fb_service) | [fb_shipment](#fb_shipment) | [fb_shipment_barcode](#fb_shipment_barcode) | [fb_shipment_barcode_change_log](#fb_shipment_barcode_change_log) | [fb_shipment_barcode_order_line](#fb_shipment_barcode_order_line) | [fb_shipment_barcode_relation](#fb_shipment_barcode_relation) | [fb_soft_version](#fb_soft_version) | [fb_warranty_grade](#fb_warranty_grade)

### 三、RMA/备件/仓库等业务表 - 约45张

[addressee_info](#addressee_info) | [af_industry_asset](#af_industry_asset) | [af_industry_asset_leak_relation](#af_industry_asset_leak_relation) | [af_industry_asset_project_relation](#af_industry_asset_project_relation) | [af_industry_leak](#af_industry_leak) | [af_industry_leak_warning](#af_industry_leak_warning) | [agent_info](#agent_info) | [app_accessory_info](#app_accessory_info) | [app_comment](#app_comment) | [app_spare_part](#app_spare_part) | [back_type](#back_type) | [bar](#bar) | [brw_app_info](#brw_app_info) | [brw_spare_info](#brw_spare_info) | [department](#department) | [dp_erp_purchase_order_header](#dp_erp_purchase_order_header) | [dp_erp_purchase_order_line](#dp_erp_purchase_order_line) | [dp_erp_purchase_receipt_header](#dp_erp_purchase_receipt_header) | [dp_erp_purchase_receipt_line](#dp_erp_purchase_receipt_line) | [fnd_company](#fnd_company) | [firebird_operation_log](#firebird_operation_log) | [rma_applicant](#rma_applicant) | [rma_app_info](#rma_app_info) | [rma_bar](#rma_bar) | [rma_info2mes_result](#rma_info2mes_result) | [rma_repair_report_from_mes](#rma_repair_report_from_mes) | [rma_spare_info](#rma_spare_info) | [role](#role) | [serve_type](#serve_type) | [spare_parts](#spare_parts) | [spare_parts_applicant](#spare_parts_applicant) | [sys_state_or_type](#sys_state_or_type) | [tain_type](#tain_type) | [tb_sys_log](#tb_sys_log) | [tx_info](#tx_info) | [user](#user) | [user_info](#user_info) | [user_team](#user_team) | [warehouse](#warehouse) | [warehouse_info](#warehouse_info) | [warehouse_info_detail](#warehouse_info_detail) | [warranty_change_logs](#warranty_change_logs) | [warranty_info](#warranty_info) | [workflow_info](#workflow_info)

### 四、临时表（temp_*/tmp_*）- 11张

[temp_contract_market_system](#temp_contract_market_system) | [temp_max_ppfs](#temp_max_ppfs) | [temp_project_sales_change](#temp_project_sales_change) | [temp_query_shipment](#temp_query_shipment) | [temp_query_shipment_barcode](#temp_query_shipment_barcode) | [tmp_tb_contract_shipment](#tmp_tb_contract_shipment) | [tmp_tb_project_contract](#tmp_tb_project_contract) | [tmp_tb_project_filtered](#tmp_tb_project_filtered) | [tmp_tb_project_shipment](#tmp_tb_project_shipment) | [tmp_tb_view_shipment_ems_4_pm](#tmp_tb_view_shipment_ems_4_pm) | [tmp_tb_view_shipment_info_4_pm](#tmp_tb_view_shipment_info_4_pm)

### 五、视图（VIEW）- 39个


---

# 一、Activiti工作流引擎表（act_*）

> Activiti引擎标准表，支撑PMS系统的审批工作流。表结构遵循Activiti 5.x/6.x标准设计。
> 外键关系概览：act_ge_bytearray -> act_re_deployment; act_id_membership -> act_id_group, act_id_user; act_re_model -> act_ge_bytearray, act_re_deployment; act_ru_execution -> act_re_procdef; act_ru_identitylink -> act_ru_execution, act_ru_task, act_re_procdef; act_ru_task -> act_ru_execution, act_re_procdef; act_ru_variable -> act_ge_bytearray, act_ru_execution

---

### 1 act_evt_log -- Activiti事件日志表，记录流程引擎产生的事件日志

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti事件日志表，记录流程引擎产生的事件日志 |
| 数据量 | ~6398 行 |
| 数据大小 | 17.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| LOG_NR_ | bigint(20) | NO | - | PRI, auto_increment |  | 日志编号（自增主键） |
| TYPE_ | varchar(64) | YES | NULL | - |  | 事件类型（如process-start, task-create等） |
| PROC_DEF_ID_ | varchar(64) | YES | NULL | - |  | 流程定义ID，关联act_re_procdef |
| PROC_INST_ID_ | varchar(64) | YES | NULL | - |  | 流程实例ID，关联act_ru_execution |
| EXECUTION_ID_ | varchar(64) | YES | NULL | - |  | 执行实例ID，关联act_ru_execution |
| TASK_ID_ | varchar(64) | YES | NULL | - |  | 任务ID，关联act_ru_task |
| TIME_STAMP_ | timestamp(3) | NO | CURRENT_TIMESTAMP(3) | - |  | 事件时间戳 |
| USER_ID_ | varchar(255) | YES | NULL | - |  | 用户ID |
| DATA_ | longblob | YES | NULL | - |  | 事件数据（二进制序列化） |
| LOCK_OWNER_ | varchar(255) | YES | NULL | - |  | 事件处理锁拥有者 |
| LOCK_TIME_ | timestamp(3) | YES | NULL | - |  | 事件处理锁获取时间 |
| IS_PROCESSED_ | tinyint(4) | YES | 0 | - |  | 是否已处理（0:未处理 1:已处理） |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | LOG_NR_ |

---

### 2 act_ge_bytearray -- Activiti通用字节数组表，存储流程定义资源文件、序列化数据等二进制内容

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti通用字节数组表，存储流程定义资源文件、序列化数据等二进制内容 |
| 数据量 | ~1210 行 |
| 数据大小 | 6.55 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | '' | PRI |  | 主键ID |
| REV_ | int(11) | YES | NULL | - |  | 乐观锁版本号，用于并发控制 |
| NAME_ | varchar(255) | YES | NULL | - |  | 名称 |
| DEPLOYMENT_ID_ | varchar(64) | YES | NULL | MUL, FK |  | 部署ID，关联act_re_deployment |
| BYTES_ | longblob | YES | NULL | - |  | 二进制数据内容 |
| GENERATED_ | tinyint(4) | YES | NULL | - |  | 是否自动生成（0:否 1:是） |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |
| ACT_FK_BYTEARR_DEPL | BTREE | NON-UNIQUE | DEPLOYMENT_ID_ |

**外键列表**

| 外键名 | 本表字段 | 引用表 | 引用字段 |
|--------|----------|--------|----------|
| ACT_FK_BYTEARR_DEPL | DEPLOYMENT_ID_ | act_re_deployment | ID_ |

---

### 3 act_ge_property -- Activiti通用属性表，存储引擎级别的属性键值对（如版本号等）

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti通用属性表，存储引擎级别的属性键值对（如版本号等） |
| 数据量 | ~3 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| NAME_ | varchar(64) | NO | '' | PRI |  | 名称 |
| VALUE_ | varchar(300) | YES | NULL | - |  | 值 |
| REV_ | int(11) | YES | NULL | - |  | 乐观锁版本号，用于并发控制 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | NAME_ |

---

### 4 act_hi_actinst -- Activiti历史活动实例表，记录流程中每个活动（节点）的执行历史

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti历史活动实例表，记录流程中每个活动（节点）的执行历史 |
| 数据量 | ~155861 行 |
| 数据大小 | 39.64 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  | 主键ID |
| PROC_DEF_ID_ | varchar(64) | NO | - | - |  | 流程定义ID，关联act_re_procdef |
| PROC_INST_ID_ | varchar(64) | NO | - | MUL |  | 流程实例ID，关联act_ru_execution |
| EXECUTION_ID_ | varchar(64) | NO | - | MUL |  | 执行实例ID，关联act_ru_execution |
| ACT_ID_ | varchar(255) | NO | - | - |  | 活动节点ID（BPMN中的节点标识） |
| TASK_ID_ | varchar(64) | YES | NULL | - |  | 任务ID，关联act_ru_task |
| CALL_PROC_INST_ID_ | varchar(64) | YES | NULL | - |  | 调用子流程实例ID |
| ACT_NAME_ | varchar(255) | YES | NULL | - |  | 活动节点名称 |
| ACT_TYPE_ | varchar(255) | NO | - | - |  | 活动节点类型（如userTask, startEvent等） |
| ASSIGNEE_ | varchar(255) | YES | NULL | - |  | 任务受理人/办理人 |
| DURATION_ | bigint(20) | YES | NULL | - |  | 持续时间（毫秒） |
| TENANT_ID_ | varchar(255) | YES | '' | - |  | 租户ID，多租户隔离 |
| START_TIME_ | datetime(3) | NO | - | - |  | 开始时间 |
| END_TIME_ | datetime(3) | YES | NULL | - |  | 结束时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |
| ACT_IDX_HI_ACT_INST_PROCINST | BTREE | NON-UNIQUE | PROC_INST_ID_, ACT_ID_ |
| ACT_IDX_HI_ACT_INST_EXEC | BTREE | NON-UNIQUE | EXECUTION_ID_, ACT_ID_ |

---

### 5 act_hi_attachment -- Activiti历史附件表，记录流程实例或任务的附件信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti历史附件表，记录流程实例或任务的附件信息 |
| 数据量 | ~0 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  | 主键ID |
| REV_ | int(11) | YES | NULL | - |  | 乐观锁版本号，用于并发控制 |
| USER_ID_ | varchar(255) | YES | NULL | - |  | 用户ID |
| NAME_ | varchar(255) | YES | NULL | - |  | 名称 |
| DESCRIPTION_ | varchar(4000) | YES | NULL | - |  | 描述 |
| TYPE_ | varchar(255) | YES | NULL | - |  | 附件类型 |
| TASK_ID_ | varchar(64) | YES | NULL | - |  | 任务ID，关联act_ru_task |
| PROC_INST_ID_ | varchar(64) | YES | NULL | - |  | 流程实例ID，关联act_ru_execution |
| URL_ | varchar(4000) | YES | NULL | - |  | 附件URL地址 |
| CONTENT_ID_ | varchar(64) | YES | NULL | - |  | 内容ID，关联act_ge_bytearray |
| TIME_ | datetime(3) | YES | NULL | - |  | 附件创建时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 6 act_hi_comment -- Activiti历史评论表，记录流程实例或任务的审批意见/评论

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti历史评论表，记录流程实例或任务的审批意见/评论 |
| 数据量 | ~66552 行 |
| 数据大小 | 7.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  | 主键ID |
| TYPE_ | varchar(255) | YES | NULL | - |  | 评论类型（如comment, event等） |
| USER_ID_ | varchar(255) | YES | NULL | - |  | 用户ID |
| TASK_ID_ | varchar(64) | YES | NULL | - |  | 任务ID，关联act_ru_task |
| PROC_INST_ID_ | varchar(64) | YES | NULL | - |  | 流程实例ID，关联act_ru_execution |
| ACTION_ | varchar(255) | YES | NULL | - |  | 评论动作（如AddComment, AddAttachment等） |
| MESSAGE_ | varchar(4000) | YES | NULL | - |  | 评论内容 |
| FULL_MSG_ | longblob | YES | NULL | - |  | 完整消息内容（二进制） |
| TIME_ | datetime(3) | NO | - | - |  | 评论时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 7 act_hi_detail -- Activiti历史详情表，记录流程变量的变更历史详情

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti历史详情表，记录流程变量的变更历史详情 |
| 数据量 | ~0 行 |
| 数据大小 | 80.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  | 主键ID |
| TYPE_ | varchar(255) | NO | - | - |  | 详情类型（如VariableUpdate, FormProperty等） |
| PROC_INST_ID_ | varchar(64) | YES | NULL | MUL |  | 流程实例ID，关联act_ru_execution |
| EXECUTION_ID_ | varchar(64) | YES | NULL | - |  | 执行实例ID，关联act_ru_execution |
| TASK_ID_ | varchar(64) | YES | NULL | MUL |  | 任务ID，关联act_ru_task |
| ACT_INST_ID_ | varchar(64) | YES | NULL | MUL |  | 活动实例ID，关联act_hi_actinst |
| NAME_ | varchar(255) | NO | - | MUL |  | 名称 |
| VAR_TYPE_ | varchar(255) | YES | NULL | - |  | 变量类型 |
| REV_ | int(11) | YES | NULL | - |  | 乐观锁版本号，用于并发控制 |
| BYTEARRAY_ID_ | varchar(64) | YES | NULL | - |  | 字节数组ID，关联act_ge_bytearray |
| DOUBLE_ | double | YES | NULL | - |  | 双精度浮点值 |
| LONG_ | bigint(20) | YES | NULL | - |  | 长整型值 |
| TEXT_ | varchar(4000) | YES | NULL | - |  | 文本值 |
| TEXT2_ | varchar(4000) | YES | NULL | - |  | 文本值2（存储长文本的第二部分） |
| TIME_ | datetime(3) | NO | - | - |  | 变量变更时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |
| ACT_IDX_HI_DETAIL_PROC_INST | BTREE | NON-UNIQUE | PROC_INST_ID_ |
| ACT_IDX_HI_DETAIL_ACT_INST | BTREE | NON-UNIQUE | ACT_INST_ID_ |
| ACT_IDX_HI_DETAIL_NAME | BTREE | NON-UNIQUE | NAME_ |
| ACT_IDX_HI_DETAIL_TASK_ID | BTREE | NON-UNIQUE | TASK_ID_ |

---

### 8 act_hi_identitylink -- Activiti历史参与者关系表，记录流程实例或任务的历史参与者信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti历史参与者关系表，记录流程实例或任务的历史参与者信息 |
| 数据量 | ~143081 行 |
| 数据大小 | 21.06 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | '' | PRI |  | 主键ID |
| GROUP_ID_ | varchar(255) | YES | NULL | - |  | 组ID |
| TYPE_ | varchar(255) | YES | NULL | - |  | 参与者关系类型（如candidate, participant, assignee等） |
| USER_ID_ | varchar(255) | YES | NULL | MUL |  | 用户ID |
| TASK_ID_ | varchar(64) | YES | NULL | MUL |  | 任务ID，关联act_ru_task |
| PROC_INST_ID_ | varchar(64) | YES | NULL | MUL |  | 流程实例ID，关联act_ru_execution |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |
| ACT_IDX_HI_IDENT_LNK_USER | BTREE | NON-UNIQUE | USER_ID_ |
| ACT_IDX_HI_IDENT_LNK_TASK | BTREE | NON-UNIQUE | TASK_ID_ |
| ACT_IDX_HI_IDENT_LNK_PROCINST | BTREE | NON-UNIQUE | PROC_INST_ID_ |

---

### 9 act_hi_procinst -- Activiti历史流程实例表，记录已完成的流程实例信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti历史流程实例表，记录已完成的流程实例信息 |
| 数据量 | ~18833 行 |
| 数据大小 | 4.41 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  | 主键ID |
| PROC_INST_ID_ | varchar(64) | NO | - | UNI |  | 流程实例ID，关联act_ru_execution |
| BUSINESS_KEY_ | varchar(255) | YES | NULL | MUL |  | 业务主键 |
| PROC_DEF_ID_ | varchar(64) | NO | - | - |  | 流程定义ID，关联act_re_procdef |
| DURATION_ | bigint(20) | YES | NULL | - |  | 持续时间（毫秒） |
| START_USER_ID_ | varchar(255) | YES | NULL | - |  | 流程发起人ID |
| START_ACT_ID_ | varchar(255) | YES | NULL | - |  | 开始活动节点ID |
| END_ACT_ID_ | varchar(255) | YES | NULL | - |  | 结束活动节点ID |
| SUPER_PROCESS_INSTANCE_ID_ | varchar(64) | YES | NULL | - |  | 父流程实例ID |
| DELETE_REASON_ | varchar(4000) | YES | NULL | - |  | 删除原因 |
| TENANT_ID_ | varchar(255) | YES | '' | - |  | 租户ID，多租户隔离 |
| START_TIME_ | datetime(3) | NO | - | - |  | 开始时间 |
| END_TIME_ | datetime(3) | YES | NULL | - |  | 结束时间 |
| NAME_ | varchar(255) | YES | NULL | - |  | 名称 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |
| PROC_INST_ID_ | BTREE | UNIQUE | PROC_INST_ID_ |
| ACT_IDX_HI_PRO_I_BUSKEY | BTREE | NON-UNIQUE | BUSINESS_KEY_ |

---

### 10 act_hi_taskinst -- Activiti历史任务实例表，记录已完成/已删除的任务实例信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti历史任务实例表，记录已完成/已删除的任务实例信息 |
| 数据量 | ~67984 行 |
| 数据大小 | 13.03 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  | 主键ID |
| PROC_DEF_ID_ | varchar(64) | YES | NULL | - |  | 流程定义ID，关联act_re_procdef |
| TASK_DEF_KEY_ | varchar(255) | YES | NULL | - |  | 任务定义Key（BPMN中的任务标识） |
| PROC_INST_ID_ | varchar(64) | YES | NULL | MUL |  | 流程实例ID，关联act_ru_execution |
| EXECUTION_ID_ | varchar(64) | YES | NULL | - |  | 执行实例ID，关联act_ru_execution |
| NAME_ | varchar(255) | YES | NULL | - |  | 名称 |
| PARENT_TASK_ID_ | varchar(64) | YES | NULL | - |  | 父任务ID |
| DESCRIPTION_ | varchar(4000) | YES | NULL | - |  | 描述 |
| OWNER_ | varchar(255) | YES | NULL | - |  | 任务拥有者（委托前的原受理人） |
| ASSIGNEE_ | varchar(255) | YES | NULL | - |  | 任务受理人/办理人 |
| DURATION_ | bigint(20) | YES | NULL | - |  | 持续时间（毫秒） |
| DELETE_REASON_ | varchar(4000) | YES | NULL | - |  | 删除原因 |
| PRIORITY_ | int(11) | YES | NULL | - |  | 优先级 |
| FORM_KEY_ | varchar(255) | YES | NULL | - |  | 表单Key，关联表单标识 |
| CATEGORY_ | varchar(255) | YES | NULL | - |  | 分类 |
| TENANT_ID_ | varchar(255) | YES | '' | - |  | 租户ID，多租户隔离 |
| START_TIME_ | datetime(3) | NO | - | - |  | 开始时间 |
| CLAIM_TIME_ | datetime(3) | YES | NULL | - |  | 任务签收/认领时间 |
| END_TIME_ | datetime(3) | YES | NULL | - |  | 结束时间 |
| DUE_DATE_ | datetime(3) | YES | NULL | - |  | 到期日期 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |
| ACT_IDX_HI_TASK_INST_PROCINST | BTREE | NON-UNIQUE | PROC_INST_ID_ |

---

### 11 act_hi_varinst -- Activiti历史变量实例表，记录流程变量的历史值

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti历史变量实例表，记录流程变量的历史值 |
| 数据量 | ~204674 行 |
| 数据大小 | 41.12 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  | 主键ID |
| PROC_INST_ID_ | varchar(64) | YES | NULL | MUL |  | 流程实例ID，关联act_ru_execution |
| EXECUTION_ID_ | varchar(64) | YES | NULL | - |  | 执行实例ID，关联act_ru_execution |
| TASK_ID_ | varchar(64) | YES | NULL | MUL |  | 任务ID，关联act_ru_task |
| NAME_ | varchar(255) | NO | - | MUL |  | 名称 |
| VAR_TYPE_ | varchar(100) | YES | NULL | - |  | 变量类型 |
| REV_ | int(11) | YES | NULL | - |  | 乐观锁版本号，用于并发控制 |
| BYTEARRAY_ID_ | varchar(64) | YES | NULL | - |  | 字节数组ID，关联act_ge_bytearray |
| DOUBLE_ | double | YES | NULL | - |  | 双精度浮点值 |
| LONG_ | bigint(20) | YES | NULL | - |  | 长整型值 |
| TEXT_ | varchar(4000) | YES | NULL | - |  | 文本值 |
| TEXT2_ | varchar(4000) | YES | NULL | - |  | 文本值2（存储长文本的第二部分） |
| CREATE_TIME_ | datetime(3) | YES | NULL | - |  | 创建时间 |
| LAST_UPDATED_TIME_ | datetime(3) | YES | NULL | - |  | 最后更新时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |
| ACT_IDX_HI_PROCVAR_PROC_INST | BTREE | NON-UNIQUE | PROC_INST_ID_ |
| ACT_IDX_HI_PROCVAR_NAME_TYPE | BTREE | NON-UNIQUE | NAME_, VAR_TYPE_ |
| ACT_IDX_HI_PROCVAR_TASK_ID | BTREE | NON-UNIQUE | TASK_ID_ |

---

### 12 act_id_group -- Activiti身份-组表，存储用户组（角色）信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti身份-组表，存储用户组（角色）信息 |
| 数据量 | ~12 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | '' | PRI |  | 主键ID |
| REV_ | int(11) | YES | NULL | - |  | 乐观锁版本号，用于并发控制 |
| NAME_ | varchar(255) | YES | NULL | - |  | 名称 |
| TYPE_ | varchar(255) | YES | NULL | - |  | 组类型（如assignment, security-role等） |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 13 act_id_info -- Activiti身份-信息表，存储用户的扩展信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti身份-信息表，存储用户的扩展信息 |
| 数据量 | ~0 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | '' | PRI |  | 主键ID |
| REV_ | int(11) | YES | NULL | - |  | 乐观锁版本号，用于并发控制 |
| USER_ID_ | varchar(64) | YES | NULL | - |  | 用户ID |
| TYPE_ | varchar(64) | YES | NULL | - |  | 信息类型（如account, userinfo等） |
| KEY_ | varchar(255) | YES | NULL | - |  | 信息键名 |
| VALUE_ | varchar(255) | YES | NULL | - |  | 值 |
| PASSWORD_ | longblob | YES | NULL | - |  | 密码 |
| PARENT_ID_ | varchar(255) | YES | NULL | - |  | 父信息ID |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 14 act_id_membership -- Activiti身份-成员关系表，存储用户与组的关联关系

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti身份-成员关系表，存储用户与组的关联关系 |
| 数据量 | ~548 行 |
| 数据大小 | 64.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| USER_ID_ | varchar(64) | NO | '' | PRI, FK |  | 用户ID |
| GROUP_ID_ | varchar(64) | NO | '' | PRI, FK |  | 组ID |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | USER_ID_, GROUP_ID_ |
| ACT_FK_MEMB_GROUP | BTREE | NON-UNIQUE | GROUP_ID_ |

**外键列表**

| 外键名 | 本表字段 | 引用表 | 引用字段 |
|--------|----------|--------|----------|
| ACT_FK_MEMB_GROUP | GROUP_ID_ | act_id_group | ID_ |
| ACT_FK_MEMB_USER | USER_ID_ | act_id_user | ID_ |

---

### 15 act_id_user -- Activiti身份-用户表，存储用户基本信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti身份-用户表，存储用户基本信息 |
| 数据量 | ~201 行 |
| 数据大小 | 48.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | '' | PRI |  | 主键ID |
| REV_ | int(11) | YES | NULL | - |  | 乐观锁版本号，用于并发控制 |
| FIRST_ | varchar(255) | YES | NULL | - |  | 名 |
| LAST_ | varchar(255) | YES | NULL | - |  | 姓 |
| EMAIL_ | varchar(255) | YES | NULL | - |  | 邮箱 |
| PWD_ | varchar(255) | YES | NULL | - |  | 用户密码 |
| PICTURE_ID_ | varchar(64) | YES | NULL | - |  | 头像资源ID，关联act_ge_bytearray |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 16 act_procdef_info -- Activiti流程定义信息表，存储流程定义的动态信息（如版本更新状态）

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti流程定义信息表，存储流程定义的动态信息（如版本更新状态） |
| 数据量 | ~0 行 |
| 数据大小 | 64.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  | 主键ID |
| PROC_DEF_ID_ | varchar(64) | NO | - | UNI, FK |  | 流程定义ID，关联act_re_procdef |
| REV_ | int(11) | YES | NULL | - |  | 乐观锁版本号，用于并发控制 |
| INFO_JSON_ID_ | varchar(64) | YES | NULL | MUL, FK |  | 信息JSON数据ID，关联act_ge_bytearray |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |
| ACT_UNIQ_INFO_PROCDEF | BTREE | UNIQUE | PROC_DEF_ID_ |
| ACT_IDX_INFO_PROCDEF | BTREE | NON-UNIQUE | PROC_DEF_ID_ |
| ACT_FK_INFO_JSON_BA | BTREE | NON-UNIQUE | INFO_JSON_ID_ |

**外键列表**

| 外键名 | 本表字段 | 引用表 | 引用字段 |
|--------|----------|--------|----------|
| ACT_FK_INFO_JSON_BA | INFO_JSON_ID_ | act_ge_bytearray | ID_ |
| ACT_FK_INFO_PROCDEF | PROC_DEF_ID_ | act_re_procdef | ID_ |

---

### 17 act_re_deployment -- Activiti仓库-部署表，记录流程部署操作信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti仓库-部署表，记录流程部署操作信息 |
| 数据量 | ~27 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | '' | PRI |  | 主键ID |
| NAME_ | varchar(255) | YES | NULL | - |  | 名称 |
| CATEGORY_ | varchar(255) | YES | NULL | - |  | 分类 |
| TENANT_ID_ | varchar(255) | YES | '' | - |  | 租户ID，多租户隔离 |
| DEPLOY_TIME_ | timestamp(3) | YES | NULL | - |  | 部署时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 18 act_re_model -- Activiti仓库-模型表，存储流程模型设计器中创建的模型信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti仓库-模型表，存储流程模型设计器中创建的模型信息 |
| 数据量 | ~6 行 |
| 数据大小 | 64.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  | 主键ID |
| REV_ | int(11) | YES | NULL | - |  | 乐观锁版本号，用于并发控制 |
| NAME_ | varchar(255) | YES | NULL | - |  | 名称 |
| KEY_ | varchar(255) | YES | NULL | - |  | 模型标识Key |
| CATEGORY_ | varchar(255) | YES | NULL | - |  | 分类 |
| VERSION_ | int(11) | YES | NULL | - |  | 版本号 |
| META_INFO_ | varchar(4000) | YES | NULL | - |  | 元信息（JSON格式） |
| DEPLOYMENT_ID_ | varchar(64) | YES | NULL | MUL, FK |  | 部署ID，关联act_re_deployment |
| EDITOR_SOURCE_VALUE_ID_ | varchar(64) | YES | NULL | MUL, FK |  | 编辑器源数据ID，关联act_ge_bytearray |
| EDITOR_SOURCE_EXTRA_VALUE_ID_ | varchar(64) | YES | NULL | MUL, FK |  | 编辑器扩展源数据ID，关联act_ge_bytearray |
| TENANT_ID_ | varchar(255) | YES | '' | - |  | 租户ID，多租户隔离 |
| CREATE_TIME_ | timestamp(3) | YES | NULL | - |  | 创建时间 |
| LAST_UPDATE_TIME_ | timestamp(3) | YES | NULL | - |  | 最后更新时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |
| ACT_FK_MODEL_SOURCE | BTREE | NON-UNIQUE | EDITOR_SOURCE_VALUE_ID_ |
| ACT_FK_MODEL_SOURCE_EXTRA | BTREE | NON-UNIQUE | EDITOR_SOURCE_EXTRA_VALUE_ID_ |
| ACT_FK_MODEL_DEPLOYMENT | BTREE | NON-UNIQUE | DEPLOYMENT_ID_ |

**外键列表**

| 外键名 | 本表字段 | 引用表 | 引用字段 |
|--------|----------|--------|----------|
| ACT_FK_MODEL_DEPLOYMENT | DEPLOYMENT_ID_ | act_re_deployment | ID_ |
| ACT_FK_MODEL_SOURCE | EDITOR_SOURCE_VALUE_ID_ | act_ge_bytearray | ID_ |
| ACT_FK_MODEL_SOURCE_EXTRA | EDITOR_SOURCE_EXTRA_VALUE_ID_ | act_ge_bytearray | ID_ |

---

### 19 act_re_procdef -- Activiti仓库-流程定义表，存储已部署的流程定义信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti仓库-流程定义表，存储已部署的流程定义信息 |
| 数据量 | ~27 行 |
| 数据大小 | 32.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  | 主键ID |
| REV_ | int(11) | YES | NULL | - |  | 乐观锁版本号，用于并发控制 |
| CATEGORY_ | varchar(255) | YES | NULL | - |  | 分类 |
| NAME_ | varchar(255) | YES | NULL | - |  | 名称 |
| KEY_ | varchar(255) | NO | - | MUL |  | 流程定义Key（BPMN中的process id） |
| VERSION_ | int(11) | NO | - | - |  | 版本号 |
| DEPLOYMENT_ID_ | varchar(64) | YES | NULL | - |  | 部署ID，关联act_re_deployment |
| RESOURCE_NAME_ | varchar(4000) | YES | NULL | - |  | 资源文件名 |
| DGRM_RESOURCE_NAME_ | varchar(4000) | YES | NULL | - |  | 流程图资源文件名 |
| DESCRIPTION_ | varchar(4000) | YES | NULL | - |  | 描述 |
| HAS_START_FORM_KEY_ | tinyint(4) | YES | NULL | - |  | 是否有开始表单Key（0:否 1:是） |
| SUSPENSION_STATE_ | int(11) | YES | NULL | - |  | 挂起状态（1:激活 2:挂起） |
| TENANT_ID_ | varchar(255) | YES | '' | - |  | 租户ID，多租户隔离 |
| HAS_GRAPHICAL_NOTATION_ | tinyint(4) | YES | NULL | - |  | 是否有图形化标记（0:否 1:是） |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |
| ACT_UNIQ_PROCDEF | BTREE | UNIQUE | KEY_, VERSION_, TENANT_ID_ |

---

### 20 act_ru_event_subscr -- Activiti运行时-事件订阅表，记录运行时的事件订阅（如信号/消息事件）

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti运行时-事件订阅表，记录运行时的事件订阅（如信号/消息事件） |
| 数据量 | ~0 行 |
| 数据大小 | 48.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  | 主键ID |
| REV_ | int(11) | YES | NULL | - |  | 乐观锁版本号，用于并发控制 |
| EVENT_TYPE_ | varchar(255) | NO | - | - |  | 事件类型（如message, signal等） |
| EVENT_NAME_ | varchar(255) | YES | NULL | - |  | 事件名称 |
| EXECUTION_ID_ | varchar(64) | YES | NULL | MUL, FK |  | 执行实例ID，关联act_ru_execution |
| PROC_INST_ID_ | varchar(64) | YES | NULL | - |  | 流程实例ID，关联act_ru_execution |
| ACTIVITY_ID_ | varchar(64) | YES | NULL | - |  | 关联活动节点ID |
| CONFIGURATION_ | varchar(255) | YES | NULL | MUL |  | 配置信息 |
| TENANT_ID_ | varchar(255) | YES | '' | - |  | 租户ID，多租户隔离 |
| PROC_DEF_ID_ | varchar(64) | YES | NULL | - |  | 流程定义ID，关联act_re_procdef |
| CREATED_ | timestamp(3) | NO | CURRENT_TIMESTAMP(3) | - |  | 订阅创建时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |
| ACT_IDX_EVENT_SUBSCR_CONFIG_ | BTREE | NON-UNIQUE | CONFIGURATION_ |
| ACT_FK_EVENT_EXEC | BTREE | NON-UNIQUE | EXECUTION_ID_ |

**外键列表**

| 外键名 | 本表字段 | 引用表 | 引用字段 |
|--------|----------|--------|----------|
| ACT_FK_EVENT_EXEC | EXECUTION_ID_ | act_ru_execution | ID_ |

---

### 21 act_ru_execution -- Activiti运行时-执行实例表，记录流程执行路径信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti运行时-执行实例表，记录流程执行路径信息 |
| 数据量 | ~3554 行 |
| 数据大小 | 1.09 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | '' | PRI |  | 主键ID |
| REV_ | int(11) | YES | NULL | - |  | 乐观锁版本号，用于并发控制 |
| PROC_INST_ID_ | varchar(64) | YES | NULL | MUL, FK |  | 流程实例ID，关联act_ru_execution |
| BUSINESS_KEY_ | varchar(255) | YES | NULL | MUL |  | 业务主键 |
| PARENT_ID_ | varchar(64) | YES | NULL | MUL, FK |  | 父执行实例ID |
| PROC_DEF_ID_ | varchar(64) | YES | NULL | MUL, FK |  | 流程定义ID，关联act_re_procdef |
| SUPER_EXEC_ | varchar(64) | YES | NULL | MUL, FK |  | 父流程执行实例ID |
| ACT_ID_ | varchar(255) | YES | NULL | - |  | 当前活动节点ID |
| IS_ACTIVE_ | tinyint(4) | YES | NULL | - |  | 是否激活（0:否 1:是） |
| IS_CONCURRENT_ | tinyint(4) | YES | NULL | - |  | 是否并发（0:否 1:是） |
| IS_SCOPE_ | tinyint(4) | YES | NULL | - |  | 是否作用域（0:否 1:是） |
| IS_EVENT_SCOPE_ | tinyint(4) | YES | NULL | - |  | 是否事件作用域（0:否 1:是） |
| SUSPENSION_STATE_ | int(11) | YES | NULL | - |  | 挂起状态（1:激活 2:挂起） |
| CACHED_ENT_STATE_ | int(11) | YES | NULL | - |  | 缓存实体状态位掩码 |
| TENANT_ID_ | varchar(255) | YES | '' | - |  | 租户ID，多租户隔离 |
| NAME_ | varchar(255) | YES | NULL | - |  | 名称 |
| LOCK_TIME_ | timestamp(3) | YES | NULL | - |  | 流程实例锁定时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |
| ACT_IDX_EXEC_BUSKEY | BTREE | NON-UNIQUE | BUSINESS_KEY_ |
| ACT_FK_EXE_PROCINST | BTREE | NON-UNIQUE | PROC_INST_ID_ |
| ACT_FK_EXE_PARENT | BTREE | NON-UNIQUE | PARENT_ID_ |
| ACT_FK_EXE_SUPER | BTREE | NON-UNIQUE | SUPER_EXEC_ |
| ACT_FK_EXE_PROCDEF | BTREE | NON-UNIQUE | PROC_DEF_ID_ |

**外键列表**

| 外键名 | 本表字段 | 引用表 | 引用字段 |
|--------|----------|--------|----------|
| ACT_FK_EXE_PARENT | PARENT_ID_ | act_ru_execution | ID_ |
| ACT_FK_EXE_PROCDEF | PROC_DEF_ID_ | act_re_procdef | ID_ |
| ACT_FK_EXE_PROCINST | PROC_INST_ID_ | act_ru_execution | ID_ |
| ACT_FK_EXE_SUPER | SUPER_EXEC_ | act_ru_execution | ID_ |

---

### 22 act_ru_identitylink -- Activiti运行时-参与者关系表，记录当前运行流程/任务的参与者

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti运行时-参与者关系表，记录当前运行流程/任务的参与者 |
| 数据量 | ~23219 行 |
| 数据大小 | 5.62 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | '' | PRI |  | 主键ID |
| REV_ | int(11) | YES | NULL | - |  | 乐观锁版本号，用于并发控制 |
| GROUP_ID_ | varchar(255) | YES | NULL | MUL |  | 组ID |
| TYPE_ | varchar(255) | YES | NULL | - |  | 参与者关系类型（如candidate, participant, assignee等） |
| USER_ID_ | varchar(255) | YES | NULL | MUL |  | 用户ID |
| TASK_ID_ | varchar(64) | YES | NULL | MUL, FK |  | 任务ID，关联act_ru_task |
| PROC_INST_ID_ | varchar(64) | YES | NULL | MUL, FK |  | 流程实例ID，关联act_ru_execution |
| PROC_DEF_ID_ | varchar(64) | YES | NULL | MUL, FK |  | 流程定义ID，关联act_re_procdef |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |
| ACT_IDX_IDENT_LNK_USER | BTREE | NON-UNIQUE | USER_ID_ |
| ACT_IDX_IDENT_LNK_GROUP | BTREE | NON-UNIQUE | GROUP_ID_ |
| ACT_IDX_ATHRZ_PROCEDEF | BTREE | NON-UNIQUE | PROC_DEF_ID_ |
| ACT_FK_TSKASS_TASK | BTREE | NON-UNIQUE | TASK_ID_ |
| ACT_FK_IDL_PROCINST | BTREE | NON-UNIQUE | PROC_INST_ID_ |

**外键列表**

| 外键名 | 本表字段 | 引用表 | 引用字段 |
|--------|----------|--------|----------|
| ACT_FK_ATHRZ_PROCEDEF | PROC_DEF_ID_ | act_re_procdef | ID_ |
| ACT_FK_IDL_PROCINST | PROC_INST_ID_ | act_ru_execution | ID_ |
| ACT_FK_TSKASS_TASK | TASK_ID_ | act_ru_task | ID_ |

---

### 23 act_ru_job -- Activiti运行时-作业表，记录定时器、异步操作等作业信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti运行时-作业表，记录定时器、异步操作等作业信息 |
| 数据量 | ~0 行 |
| 数据大小 | 32.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  | 主键ID |
| REV_ | int(11) | YES | NULL | - |  | 乐观锁版本号，用于并发控制 |
| TYPE_ | varchar(255) | NO | - | - |  | 作业类型（如timer, message等） |
| LOCK_OWNER_ | varchar(255) | YES | NULL | - |  | 锁拥有者 |
| EXCLUSIVE_ | tinyint(1) | YES | NULL | - |  | 是否独占执行（0:否 1:是） |
| EXECUTION_ID_ | varchar(64) | YES | NULL | - |  | 执行实例ID，关联act_ru_execution |
| PROCESS_INSTANCE_ID_ | varchar(64) | YES | NULL | - |  | 流程实例ID |
| PROC_DEF_ID_ | varchar(64) | YES | NULL | - |  | 流程定义ID，关联act_re_procdef |
| RETRIES_ | int(11) | YES | NULL | - |  | 重试次数 |
| EXCEPTION_STACK_ID_ | varchar(64) | YES | NULL | MUL, FK |  | 异常堆栈ID，关联act_ge_bytearray |
| EXCEPTION_MSG_ | varchar(4000) | YES | NULL | - |  | 异常消息 |
| REPEAT_ | varchar(255) | YES | NULL | - |  | 重复执行表达式（如定时器的cron表达式） |
| HANDLER_TYPE_ | varchar(255) | YES | NULL | - |  | 处理器类型 |
| HANDLER_CFG_ | varchar(4000) | YES | NULL | - |  | 处理器配置 |
| TENANT_ID_ | varchar(255) | YES | '' | - |  | 租户ID，多租户隔离 |
| LOCK_EXP_TIME_ | timestamp(3) | YES | NULL | - |  | 锁过期时间 |
| DUEDATE_ | timestamp(3) | YES | NULL | - |  | 到期执行时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |
| ACT_FK_JOB_EXCEPTION | BTREE | NON-UNIQUE | EXCEPTION_STACK_ID_ |

**外键列表**

| 外键名 | 本表字段 | 引用表 | 引用字段 |
|--------|----------|--------|----------|
| ACT_FK_JOB_EXCEPTION | EXCEPTION_STACK_ID_ | act_ge_bytearray | ID_ |

---

### 24 act_ru_task -- Activiti运行时-任务表，记录当前待办任务信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti运行时-任务表，记录当前待办任务信息 |
| 数据量 | ~3400 行 |
| 数据大小 | 1.00 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | '' | PRI |  | 主键ID |
| REV_ | int(11) | YES | NULL | - |  | 乐观锁版本号，用于并发控制 |
| EXECUTION_ID_ | varchar(64) | YES | NULL | MUL, FK |  | 执行实例ID，关联act_ru_execution |
| PROC_INST_ID_ | varchar(64) | YES | NULL | MUL, FK |  | 流程实例ID，关联act_ru_execution |
| PROC_DEF_ID_ | varchar(64) | YES | NULL | MUL, FK |  | 流程定义ID，关联act_re_procdef |
| NAME_ | varchar(255) | YES | NULL | - |  | 名称 |
| PARENT_TASK_ID_ | varchar(64) | YES | NULL | - |  | 父任务ID |
| DESCRIPTION_ | varchar(4000) | YES | NULL | - |  | 描述 |
| TASK_DEF_KEY_ | varchar(255) | YES | NULL | - |  | 任务定义Key（BPMN中的任务标识） |
| OWNER_ | varchar(255) | YES | NULL | - |  | 任务拥有者（委托前的原受理人） |
| ASSIGNEE_ | varchar(255) | YES | NULL | MUL |  | 任务受理人/办理人 |
| DELEGATION_ | varchar(64) | YES | NULL | - |  | 委托状态（PENDING:待委托 RESOLVED:已委托） |
| PRIORITY_ | int(11) | YES | NULL | - |  | 优先级 |
| SUSPENSION_STATE_ | int(11) | YES | NULL | - |  | 挂起状态（1:激活 2:挂起） |
| CATEGORY_ | varchar(255) | YES | NULL | - |  | 分类 |
| TENANT_ID_ | varchar(255) | YES | '' | - |  | 租户ID，多租户隔离 |
| CREATE_TIME_ | timestamp(3) | YES | NULL | - |  | 创建时间 |
| DUE_DATE_ | datetime(3) | YES | NULL | - |  | 到期日期 |
| FORM_KEY_ | varchar(255) | YES | NULL | - |  | 表单Key，关联表单标识 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |
| ACT_FK_TASK_EXE | BTREE | NON-UNIQUE | EXECUTION_ID_ |
| ACT_FK_TASK_PROCINST | BTREE | NON-UNIQUE | PROC_INST_ID_ |
| ACT_FK_TASK_PROCDEF | BTREE | NON-UNIQUE | PROC_DEF_ID_ |
| ACT_IDX_ASSIGNEE | BTREE | NON-UNIQUE | ASSIGNEE_ |

**外键列表**

| 外键名 | 本表字段 | 引用表 | 引用字段 |
|--------|----------|--------|----------|
| ACT_FK_TASK_EXE | EXECUTION_ID_ | act_ru_execution | ID_ |
| ACT_FK_TASK_PROCDEF | PROC_DEF_ID_ | act_re_procdef | ID_ |
| ACT_FK_TASK_PROCINST | PROC_INST_ID_ | act_ru_execution | ID_ |

---

### 25 act_ru_task_callback_task_w04649 -- Activiti运行时-任务回调扩展表，自定义扩展的任务回调信息表（业务定制）

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti运行时-任务回调扩展表，自定义扩展的任务回调信息表（业务定制） |
| 数据量 | ~0 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | '' | - |  | 主键ID |
| REV_ | int(11) | YES | NULL | - |  | 乐观锁版本号，用于并发控制 |
| EXECUTION_ID_ | varchar(64) | YES | NULL | - |  | 执行实例ID，关联act_ru_execution |
| PROC_INST_ID_ | varchar(64) | YES | NULL | - |  | 流程实例ID，关联act_ru_execution |
| PROC_DEF_ID_ | varchar(64) | YES | NULL | - |  | 流程定义ID，关联act_re_procdef |
| NAME_ | varchar(255) | YES | NULL | - |  | 名称 |
| PARENT_TASK_ID_ | varchar(64) | YES | NULL | - |  | 父任务ID |
| DESCRIPTION_ | varchar(4000) | YES | NULL | - |  | 描述 |
| TASK_DEF_KEY_ | varchar(255) | YES | NULL | - |  | 任务定义Key（BPMN中的任务标识） |
| OWNER_ | varchar(255) | YES | NULL | - |  | 任务拥有者（委托前的原受理人） |
| ASSIGNEE_ | varchar(255) | YES | NULL | - |  | 任务受理人/办理人 |
| DELEGATION_ | varchar(64) | YES | NULL | - |  | 委托状态（PENDING:待委托 RESOLVED:已委托） |
| PRIORITY_ | int(11) | YES | NULL | - |  | 优先级 |
| SUSPENSION_STATE_ | int(11) | YES | NULL | - |  | 挂起状态（1:激活 2:挂起） |
| CATEGORY_ | varchar(255) | YES | NULL | - |  | 分类 |
| TENANT_ID_ | varchar(255) | YES | '' | - |  | 租户ID，多租户隔离 |
| CREATE_TIME_ | timestamp(3) | YES | NULL | - |  | 创建时间 |
| DUE_DATE_ | datetime(3) | YES | NULL | - |  | 到期日期 |
| FORM_KEY_ | varchar(255) | YES | NULL | - |  | 表单Key，关联表单标识 |
| varId | varchar(64) | YES | NULL | - |  | 变量ID（自定义扩展字段） |
| linkId | varchar(64) | YES | '' | - |  | 关联ID（自定义扩展字段） |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| - | - | - | - |

---

### 26 act_ru_variable -- Activiti运行时-变量表，记录当前运行流程实例的变量值

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti运行时-变量表，记录当前运行流程实例的变量值 |
| 数据量 | ~42152 行 |
| 数据大小 | 12.53 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  | 主键ID |
| REV_ | int(11) | YES | NULL | - |  | 乐观锁版本号，用于并发控制 |
| TYPE_ | varchar(255) | NO | - | - |  | 变量类型（如string, integer, boolean等） |
| NAME_ | varchar(255) | NO | - | - |  | 名称 |
| EXECUTION_ID_ | varchar(64) | YES | NULL | MUL, FK |  | 执行实例ID，关联act_ru_execution |
| PROC_INST_ID_ | varchar(64) | YES | NULL | MUL, FK |  | 流程实例ID，关联act_ru_execution |
| TASK_ID_ | varchar(64) | YES | NULL | MUL |  | 任务ID，关联act_ru_task |
| BYTEARRAY_ID_ | varchar(64) | YES | NULL | MUL, FK |  | 字节数组ID，关联act_ge_bytearray |
| DOUBLE_ | double | YES | NULL | - |  | 双精度浮点值 |
| LONG_ | bigint(20) | YES | NULL | - |  | 长整型值 |
| TEXT_ | varchar(4000) | YES | NULL | - |  | 文本值 |
| TEXT2_ | varchar(4000) | YES | NULL | - |  | 文本值2（存储长文本的第二部分） |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |
| ACT_IDX_VARIABLE_TASK_ID | BTREE | NON-UNIQUE | TASK_ID_ |
| ACT_FK_VAR_EXE | BTREE | NON-UNIQUE | EXECUTION_ID_ |
| ACT_FK_VAR_PROCINST | BTREE | NON-UNIQUE | PROC_INST_ID_ |
| ACT_FK_VAR_BYTEARRAY | BTREE | NON-UNIQUE | BYTEARRAY_ID_ |

**外键列表**

| 外键名 | 本表字段 | 引用表 | 引用字段 |
|--------|----------|--------|----------|
| ACT_FK_VAR_BYTEARRAY | BYTEARRAY_ID_ | act_ge_bytearray | ID_ |
| ACT_FK_VAR_EXE | EXECUTION_ID_ | act_ru_execution | ID_ |
| ACT_FK_VAR_PROCINST | PROC_INST_ID_ | act_ru_execution | ID_ |

---



# 二、Firebird迁移表（fb_*）

> 从Firebird发货系统迁移的数据表，包含合同、发货、条码、物料、维保等历史业务数据。
> fb_shipment_barcode 相关三张表合计约1.5GB，是全库数据量最大的部分。

---

### 1 fb_contract -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 从Firebird旧系统迁移的合同数据表，存储历史合同完整信息 |
| 数据量 | ~104743 行 |
| 数据大小 | 23.56 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contract_id | varchar(64) | YES | - | MUL | - | 合同内部ID（Firebird系统主键） |
| contract_code | varchar(25) | YES | - | MUL | - | 合同号（业务编号） |
| office_code | varchar(15) | YES | - | MUL | - | 办事处编码 |
| contract_type | int(11) | YES | - | MUL | - | 合同类型（0销售合同/11维修合同等） |
| customer_name | varchar(512) | YES | - | - | - | 客户名称 |
| project_name | varchar(512) | YES | - | - | - | 项目名称 |
| warranty | varchar(2) | YES | - | - | - | 维保年限 |
| marketCode | varchar(10) | YES | - | - | - | 市场编码 |
| marketName | varchar(15) | YES | - | - | - | 市场名称 |
| systemId | int(11) | YES | - | - | - | 体系ID |
| systemName | varchar(15) | YES | - | - | - | 体系名称 |
| remark | varchar(4096) | YES | - | - | - | 备注 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contract_code | BTREE | NON-UNIQUE | contract_code |
| contract_type | BTREE | NON-UNIQUE | contract_type |
| fb_contract_contract_id_IDX | BTREE | NON-UNIQUE | contract_id,office_code |
| office_code_IDX | BTREE | NON-UNIQUE | office_code,contract_id |

---

---
### 2 fb_ft_result1 -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 从Firebird迁移的出厂测试结果一级数据 |
| 数据量 | ~193752 行 |
| 数据大小 | 10.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| item_id | int(11) | YES | - | MUL | - | 测试项ID |
| serial_number | varchar(100) | YES | - | - | - | 设备序列号 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| item_id | BTREE | NON-UNIQUE | item_id |

---

---
### 3 fb_ft_result2 -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 从Firebird迁移的出厂测试结果二级数据，含软件版本信息 |
| 数据量 | ~496626 行 |
| 数据大小 | 298.83 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| result1_id | int(11) | YES | - | MUL | - | 关联一级测试结果ID |
| result_desc | text | YES | - | - | - | 测试结果描述（含release/cpld/boot/pcb版本信息） |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| result1_id | BTREE | NON-UNIQUE | result1_id |

---

---
### 4 fb_items -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 从Firebird迁移的物料主数据表 |
| 数据量 | ~32188 行 |
| 数据大小 | 3.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | YES | - | - | - | 主键ID |
| item | varchar(25) | YES | - | MUL | - | 物料编码 |
| describe_ | varchar(255) | YES | - | - | - | 物料描述 |
| itemname | varchar(255) | YES | - | MUL | - | 物料型号名称 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| cover_index | BTREE | NON-UNIQUE | item,itemname,describe_ |
| itemname | BTREE | NON-UNIQUE | itemname |

---

---
### 5 fb_items2 -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 从Firebird迁移的物料编码对照表（母子公司物料编码映射） |
| 数据量 | ~19357 行 |
| 数据大小 | 2.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | YES | - | - | - | 主键ID |
| item | varchar(15) | YES | - | - | - | 子公司物料编码 |
| describe_ | varchar(150) | YES | - | - | - | 物料描述 |
| itemname | varchar(255) | YES | - | - | - | 物料型号名称 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|

---

---
### 6 fb_market_system -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 从Firebird迁移的市场体系配置表 |
| 数据量 | ~14 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| marketCode | varchar(10) | YES | - | - | - | 市场编码 |
| marketName | varchar(15) | YES | - | - | - | 市场名称 |
| systemId | int(11) | YES | - | - | - | 体系ID |
| systemName | varchar(15) | YES | - | - | - | 体系名称 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|

---

---
### 7 fb_office_relationship -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 从Firebird迁移的合同-办事处关系表 |
| 数据量 | ~659 行 |
| 数据大小 | 64.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 表记录历史数据中合同号与办事处的关系 | 表记录历史数据中合同号与办事处的关系 |
| contractNo | varchar(100) | YES | - | MUL | 合同号 | 合同号 |
| officeCode | varchar(25) | YES | - | - | 办事处编码 | 办事处编码 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | contractNo |
| PRIMARY | BTREE | UNIQUE | id |

---

---
### 8 fb_service -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 从Firebird迁移的维保服务记录表，记录设备延保/续保信息 |
| 数据量 | ~95878 行 |
| 数据大小 | 12.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | varchar(64) | YES | - | MUL | - | 主键ID |
| con_xb | varchar(25) | YES | - | MUL | - | 续保合同编号 |
| barcode | varchar(50) | YES | - | MUL | - | 设备序列号 |
| grade | varchar(15) | YES | - | - | - | 维保等级编码 |
| begin_date | datetime | YES | - | - | - | 服务开始日期 |
| end_date | datetime | YES | - | - | - | 服务结束日期 |
| warranty | char(1) | YES | - | - | - | 维保标识 |
| remark | text | YES | - | - | - | 备注 |
| isyb | int(11) | YES | 1 | - | 1 延保 0 其他数据 | 1 延保 0 其他数据 |
| state | int(11) | YES | - | - | 针对多条续保，保留最新数据 | 针对多条续保，保留最新数据 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barcode_ | BTREE | NON-UNIQUE | barcode |
| con_xb | BTREE | NON-UNIQUE | con_xb |
| id | BTREE | NON-UNIQUE | id |

---

---
### 9 fb_shipment -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 从Firebird迁移的发货单表，记录装箱单发货信息 |
| 数据量 | ~140962 行 |
| 数据大小 | 17.55 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| packlist_id | varchar(64) | YES | - | MUL | - | 装箱单ID |
| con_id | varchar(64) | YES | - | MUL | - | 关联合同ID |
| packdate | datetime | YES | - | MUL | - | 发货日期 |
| warrantyStartTime | datetime | YES | - | - | - | 维保开始时间 |
| warrantyEndTime | datetime | YES | - | - | - | 维保结束时间 |
| receiveName | text | YES | - | - | 收件人 | 收件人 |
| emsNum | text | YES | - | - | 快递单号 | 快递单号 |
| emsCompany | text | YES | - | - | 快递公司 | 快递公司 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| fb_shipment_con_id_IDX | BTREE | NON-UNIQUE | con_id,packlist_id |
| fb_shipment_packdate_IDX | BTREE | NON-UNIQUE | packdate,con_id,packlist_id |
| fb_shipment_packlist_id_IDX | BTREE | NON-UNIQUE | packlist_id,con_id |

---

---
### 10 fb_shipment_barcode -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 从Firebird迁移的发货条码明细表，维保计算核心数据源 |
| 数据量 | ~3541100 行 |
| 数据大小 | 612.00 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | bigint(20) | NO | - | PRI, auto_increment | - | 主键ID |
| pack_id | varchar(64) | YES | - | MUL | - | 装箱单ID |
| item | varchar(16) | YES | - | MUL | - | 物料编码 |
| barcode | varchar(50) | YES | - | MUL | - | 设备序列号 |
| com_barcode | varchar(50) | YES | - | - | - | 公司内部序列号 |
| rma_no | varchar(64) | YES | - | - | - | RMA单号 |
| isRMA | int(11) | YES | - | - | 标注是RMA替换添加的记录 | 标注是RMA替换添加的记录 |
| item2 | varchar(16) | YES | - | MUL | 母子公司发货物料编码对应关系 | 母子公司发货物料编码对应关系 |
| barcode2 | varchar(50) | YES | - | MUL | 母子公司发货序列号对应关系 | 母子公司发货序列号对应关系 |
| orderNumber | varchar(32) | YES | - | - | 订单号 | 订单号 |
| lineNum | int(11) | YES | - | - | 订单行号 | 订单行号 |
| profitCenter | varchar(32) | YES | - | - | 利润中心 | 利润中心 |
| soleAgentSuffix | varchar(32) | YES | - | - | 总代orderNumber后缀 | 总代orderNumber后缀 |
| warrantyStartDate | date | YES | - | - | 维保开始时间，为空默认装箱单发货日期+90天 | 维保开始时间，为空默认装箱单发货日期+90天 |
| warrantyMonth | int(11) | YES | - | - | 维保月数 | 维保月数 |
| rmaBarcode | varchar(50) | YES | - | - | RMA逆向序列号（维保替换的序列号） | RMA逆向序列号（维保替换的序列号） |
| updateTime | datetime | YES | - | - | - | 更新时间 |
| syncTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 同步时间 |
| uuid | varchar(64) | YES | - | UNI | - | 唯一标识UUID |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barcode2 | BTREE | NON-UNIQUE | barcode2 |
| barcode_pack_rma_IDX | BTREE | NON-UNIQUE | barcode,pack_id,rma_no |
| barcode_rma_pack_IDX | BTREE | NON-UNIQUE | barcode,rma_no,pack_id |
| item | BTREE | NON-UNIQUE | item |
| item2 | BTREE | NON-UNIQUE | item2 |
| pack_barcode_IDX | BTREE | NON-UNIQUE | pack_id,barcode,rma_no |
| pack_item_IDX | BTREE | NON-UNIQUE | pack_id,item,rma_no |
| PRIMARY | BTREE | UNIQUE | id |
| uuid | BTREE | UNIQUE | uuid |

---

---
### 11 fb_shipment_barcode_change_log -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 从Firebird迁移的发货条码变更日志表 |
| 数据量 | ~528607 行 |
| 数据大小 | 547.00 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| logID | bigint(20) | NO | - | PRI, auto_increment | - | 日志ID |
| tableName | varchar(128) | YES | - | - | - | 变更表名 |
| operation | varchar(50) | YES | - | - | - | 操作类型（INSERT/UPDATE/DELETE） |
| changedBy | varchar(128) | YES | - | - | - | 变更操作人 |
| changeTime | datetime | YES | - | - | - | 变更时间 |
| dataId | varchar(128) | YES | - | MUL | - | 数据记录ID |
| barCode | varchar(128) | YES | - | - | - | 设备序列号 |
| lasted | smallint(6) | YES | - | MUL | - | 是否最新记录标记 |
| oldValues | longtext | YES | - | - | - | 变更前值（JSON） |
| newValues | longtext | YES | - | - | - | 变更后值（JSON） |
| syncTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 同步时间 |
| syncFlag | smallint(6) | YES | 0 | MUL | 同步标记，0待处理，1已处理，主要用于数据同步后更新发货记录使用 | 同步标记，0待处理，1已处理，主要用于数据同步后更新发货记录使用 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| idx_dataid_lasted_logid | BTREE | NON-UNIQUE | dataId,lasted,logID |
| idx_lasted_dataid_logid | BTREE | NON-UNIQUE | lasted,dataId,logID |
| idx_syncFlag_lasted | BTREE | NON-UNIQUE | syncFlag,lasted |
| PRIMARY | BTREE | UNIQUE | logID |

---

---
### 12 fb_shipment_barcode_order_line -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 从Firebird迁移的发货条码订单行关联表 |
| 数据量 | ~2576429 行 |
| 数据大小 | 372.94 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| pack_id | varchar(64) | YES | - | MUL | - | 装箱单ID |
| packlist_no | varchar(64) | YES | - | - | - | 装箱单编号 |
| barcode | varchar(50) | YES | - | MUL | - | 设备序列号 |
| contractNo | varchar(50) | YES | - | - | - | 合同号 |
| orderNumber | varchar(32) | YES | - | MUL | - | SAP订单号 |
| lineNum | int(11) | YES | - | - | - | 订单行号 |
| orderQty | int(11) | YES | - | - | - | 订单数量 |
| deliveredQty | int(11) | YES | - | - | - | 已发货数量 |
| profitCenter | varchar(32) | YES | - | - | 利润中心 | 利润中心 |
| orderExecNumber | varchar(50) | YES | - | - | 执行单号 | 执行单号 |
| soleAgentSuffix | varchar(32) | YES | - | - | 总代orderNumber后缀 | 总代orderNumber后缀 |
| warrantyMonth | int(11) | YES | 0 | - | 维保月限 | 维保月限 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barcode | BTREE | NON-UNIQUE | barcode |
| orderNumber | BTREE | NON-UNIQUE | orderNumber,lineNum |
| pack_id | BTREE | NON-UNIQUE | pack_id,barcode |

---

---
### 13 fb_shipment_barcode_relation -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 从Firebird迁移的发货条码关联关系表 |
| 数据量 | ~55130 行 |
| 数据大小 | 7.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI | - | 主键ID |
| sn1 | varchar(50) | YES | - | MUL | - | 序列号1 |
| item1 | varchar(15) | YES | - | MUL | - | 物料编码1 |
| sn2 | varchar(50) | YES | - | MUL | - | 序列号2 |
| item2 | varchar(15) | YES | - | MUL | - | 物料编码2 |
| contract | varchar(25) | YES | - | MUL | - | 合同号 |
| createtime | varchar(50) | YES | - | - | - | 创建时间 |
| updatetime | varchar(50) | YES | - | - | - | 更新时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contract_sn1_IDX | BTREE | NON-UNIQUE | contract,sn1 |
| item1 | BTREE | NON-UNIQUE | item1 |
| item2 | BTREE | NON-UNIQUE | item2 |
| PRIMARY | BTREE | UNIQUE | id |
| sn1 | BTREE | NON-UNIQUE | sn1 |
| sn2 | BTREE | NON-UNIQUE | sn2 |

---

---
### 14 fb_soft_version -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 从Firebird迁移的设备软件版本表 |
| 数据量 | ~161015 行 |
| 数据大小 | 13.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| serial_number | varchar(100) | YES | - | MUL | - | 设备序列号 |
| conp | varchar(100) | YES | - | MUL | - | 主程序版本（release版本） |
| cpld | varchar(100) | YES | - | MUL | - | CPLD版本 |
| boot | varchar(100) | YES | - | MUL | - | BOOT版本 |
| pcb | varchar(100) | YES | - | MUL | - | PCB版本 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| boot | BTREE | NON-UNIQUE | boot |
| conp | BTREE | NON-UNIQUE | conp |
| cpld | BTREE | NON-UNIQUE | cpld |
| pcb | BTREE | NON-UNIQUE | pcb |
| serial_number | BTREE | NON-UNIQUE | serial_number |

---

---
### 15 fb_warranty_grade -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 从Firebird迁移的维保等级配置表 |
| 数据量 | ~11 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| gradecode | varchar(25) | YES | - | MUL | - | 等级编码 |
| gradename | varchar(125) | YES | - | - | - | 等级名称 |
| gradestatus | int(11) | YES | 0 | - | - | 等级状态 |
| sort | int(3) | YES | 0 | - | - | 排序 |
| effectiveFrom | datetime | YES | - | - | - | 生效开始时间 |
| effectiveTo | datetime | YES | - | - | - | 生效结束时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| gradecode | BTREE | NON-UNIQUE | gradecode |
| PRIMARY | BTREE | UNIQUE | id |

---

---


# 三、RMA/备件/仓库等业务表

> 包含RMA返修、备件管理、仓库管理、维保信息、系统日志等业务表。
> 部分表（如user、role、department）为旧版系统遗留表，与fnd_*、t_*系列表存在功能重叠。

---

### 1 addressee_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 收件人信息表 |
| 数据量 | ~2935 行 |
| 数据大小 | 416.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| addre_id | int(11) | NO | - | PRI, auto_increment | ID | ID |
| username | varchar(25) | NO | - | - | 关联用户账号 | 关联用户账号 |
| addre_name | varchar(64) | YES | - | - | 收件人姓名 | 收件人姓名 |
| addre_tel | varchar(64) | YES | - | - | 收件人电话 | 收件人电话 |
| addre_mail | varchar(64) | YES | - | - | 收件人邮箱 | 收件人邮箱 |
| addr | varchar(1024) | YES | - | - | 地址/where | 地址/where |
| zip_code | varchar(10) | YES | - | - | 邮编 | 邮编 |
| company | varchar(64) | YES | - | - | 公司 | 公司 |
| depName | varchar(25) | YES | - | - | 部门 | 部门 |
| remark | text | YES | - | - | 备注 | 备注 |
| state | int(11) | YES | 1 | - | 状态（生效或失效） | 状态（生效或失效） |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | addre_id |

---

---
### 2 af_industry_asset -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 行业资产表，记录工控安全领域资产信息 |
| 数据量 | ~658 行 |
| 数据大小 | 192.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| assetNum | varchar(255) | YES | - | - | 资产编号 | 资产编号 |
| assetName | varchar(255) | YES | - | - | 资产名称 | 资产名称 |
| assetCategory | varchar(25) | YES | - | - | 资产分类 | 资产分类 |
| assetType | varchar(25) | NO | - | - | 资产类型 | 资产类型 |
| assetHost | varchar(255) | YES | - | - | IP/URL地址/域名 | IP/URL地址/域名 |
| assetOpenPorts | varchar(255) | YES | - | - | 开放端口情况 | 开放端口情况 |
| assetDeployInfo | varchar(1024) | YES | - | - | 部署应用情况 | 部署应用情况 |
| assetUsage | varchar(255) | YES | - | - | 资产用途 | 资产用途 |
| customerName | varchar(255) | YES | - | - | 单位名称 | 单位名称 |
| industryCode | varchar(25) | YES | - | - | 所属行业 | 所属行业 |
| assetAS | varchar(25) | YES | - | - | 应用系统 | 应用系统 |
| assetASVersion | varchar(25) | YES | - | - | 应用系统版本号 | 应用系统版本号 |
| assetASIdentify | varchar(1024) | YES | - | - | 应用系统识别途径 | 应用系统识别途径 |
| assetASFramework | varchar(25) | YES | - | - | 应用系统架构 | 应用系统架构 |
| middlewareName | varchar(255) | YES | - | - | 中间件名称 | 中间件名称 |
| middlewareVersion | varchar(255) | YES | - | - | 中间件版本 | 中间件版本 |
| developerBrand | varchar(255) | YES | - | - | 开发商品牌 | 开发商品牌 |
| assetOS | varchar(25) | YES | - | - | 操作系统 | 操作系统 |
| assetOSVersion | varchar(255) | YES | - | - | 操作系统版本 | 操作系统版本 |
| assetDB | varchar(25) | YES | - | - | 数据库类型 | 数据库类型 |
| assetDBVersion | varchar(255) | YES | - | - | 数据库版本 | 数据库版本 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |
| status | varchar(25) | YES | 0 | - | 状态 | 状态 |
| trackStatus | int(1) | YES | 0 | - | 入库状态 | 入库状态 |
| trackedTime | datetime | YES | - | - | 入库时间 | 入库时间 |
| disabled | bit(1) | YES | b'0' | - | 删除状态 | 删除状态 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 创建时间 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 更新时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

---
### 3 af_industry_asset_leak_relation -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 行业资产与漏洞关联表 |
| 数据量 | ~23 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| projectId | int(11) | NO | 0 | MUL | 项目ID | 项目ID |
| assetId | int(11) | NO | - | MUL | 资产ID | 资产ID |
| leakId | int(11) | NO | - | - | 漏洞ID | 漏洞ID |
| effectiveFrom | datetime | YES | - | - | 生效时间 | 生效时间 |
| effectiveTo | datetime | YES | - | - | 失效时间 | 失效时间 |
| disabled | bit(1) | NO | b'0' | - | 删除标准 | 删除标准 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| assetId | BTREE | NON-UNIQUE | assetId |
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

---
### 4 af_industry_asset_project_relation -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 行业资产与项目关联表 |
| 数据量 | ~658 行 |
| 数据大小 | 64.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| projectId | int(11) | NO | - | MUL | 项目ID | 项目ID |
| assetId | int(11) | NO | - | MUL | 资产ID | 资产ID |
| effectiveFrom | datetime | YES | - | - | 生效时间 | 生效时间 |
| effectiveTo | datetime | YES | - | - | 失效时间 | 失效时间 |
| disabled | bit(1) | NO | b'0' | - | 删除标准 | 删除标准 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| assetId | BTREE | NON-UNIQUE | assetId |
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

---
### 5 af_industry_leak -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 行业漏洞表 |
| 数据量 | ~5 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| leakCode | varchar(255) | YES | - | - | 漏洞编号 | 漏洞编号 |
| leakName | varchar(255) | YES | - | - | 漏洞名称 | 漏洞名称 |
| leakType | varchar(25) | NO |  | - | 漏洞类型 | 漏洞类型 |
| leakLevel | varchar(25) | YES | - | - | 漏洞级别 | 漏洞级别 |
| leakDesc | varchar(1024) | YES | - | - | 漏洞描述 | 漏洞描述 |
| industryCode | varchar(25) | YES | - | - | 所属行业 | 所属行业 |
| leakSourceInfo | varchar(1024) | YES | - | - | 漏洞原始数据 | 漏洞原始数据 |
| remark | varchar(1024) | YES | - | - | 备注 | 备注 |
| status | varchar(25) | YES | 0 | - | 状态 | 状态 |
| trackStatus | int(1) | YES | 0 | - | 入库状态 | 入库状态 |
| trackedTime | datetime | YES | - | - | 入库时间 | 入库时间 |
| disabled | bit(1) | YES | b'0' | - | 删除状态 | 删除状态 |
| assetIds | varchar(255) | YES | - | - | 关联的资产ID | 关联的资产ID |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 创建时间 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 更新时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

---
### 6 af_industry_leak_warning -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 行业漏洞预警表 |
| 数据量 | ~2 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| leakName | varchar(255) | YES | - | - | 漏洞名称 | 漏洞名称 |
| assetAS | varchar(25) | YES | - | - | 应用系统 | 应用系统 |
| assetASVersion | varchar(25) | YES | - | - | 应用系统版本号 | 应用系统版本号 |
| assetASIdentify | varchar(1024) | YES | - | - | 应用系统识别途径 | 应用系统识别途径 |
| assetASFramework | varchar(25) | YES | - | - | 应用系统架构 | 应用系统架构 |
| middlewareName | varchar(255) | YES | - | - | 中间件名称 | 中间件名称 |
| middlewareVersion | varchar(255) | YES | - | - | 中间件版本 | 中间件版本 |
| developerBrand | varchar(255) | YES | - | - | 开发商品牌 | 开发商品牌 |
| assetOS | varchar(25) | YES | - | - | 操作系统 | 操作系统 |
| assetOSVersion | varchar(255) | YES | - | - | 操作系统版本 | 操作系统版本 |
| assetDB | varchar(25) | YES | - | - | 数据库类型 | 数据库类型 |
| assetDBVersion | varchar(255) | YES | - | - | 数据库版本 | 数据库版本 |
| ports | varchar(255) | YES | - | - | 风险端口 | 风险端口 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |
| status | int(3) | YES | - | - | 状态 | 状态 |
| trackStatus | int(1) | YES | 0 | - | 入库状态 | 入库状态 |
| trackedTime | datetime | YES | - | - | 入库时间 | 入库时间 |
| disabled | bit(1) | YES | b'0' | - | 删除状态 | 删除状态 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 创建时间 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 更新时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

---
### 7 agent_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 代理商信息表 |
| 数据量 | ~35204 行 |
| 数据大小 | 3.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| agent_id | int(10) | NO | - | PRI, auto_increment | - | 业务含义待确认 |
| id | varchar(16) | NO | - | MUL | - | 主键ID |
| name | varchar(64) | NO | - | - | - | 名称 |
| type | int(8) | NO | - | - | - | 类型 |
| level | varchar(64) | YES | - | - | - | 业务含义待确认 |
| enable | int(8) | NO | 1 | - | - | 业务含义待确认 |
| agent_version | int(8) | NO | 0 | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| index_id | BTREE | NON-UNIQUE | id |
| PRIMARY | BTREE | UNIQUE | agent_id |

---

---
### 8 app_accessory_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 配件信息表 |
| 数据量 | ~680 行 |
| 数据大小 | 144.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| sheetID | varchar(25) | YES | - | - | 流水号 | 流水号 |
| accessoryName | varchar(255) | YES | - | - | 附件名称 | 附件名称 |
| uploader | varchar(10) | YES | - | - | 上传者 | 上传者 |
| uploadTime | datetime | YES | - | - | 上传时间 | 上传时间 |
| accessoryType | int(11) | YES | - | - | 附件类型  1 发货信息  -1 坏件返回信息 | 附件类型  1 发货信息  -1 坏件返回信息 |
| accessoryPath | varchar(100) | YES | - | - | 上传路径 | 上传路径 |
| data_creater | varchar(10) | YES | - | - | - | 业务含义待确认 |
| data_creatime | datetime | YES | - | - | - | 业务含义待确认 |
| data_updater | varchar(10) | YES | - | - | - | 业务含义待确认 |
| data_updatime | datetime | YES | - | - | - | 业务含义待确认 |
| data_from | datetime | YES | - | - | - | 业务含义待确认 |
| data_to | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

---
### 9 app_comment -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 审批意见表 |
| 数据量 | ~23016 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| sheetID | varchar(10) | YES | - | MUL | 单据代码 | 单据代码 |
| is_pass | varchar(2) | YES | - | - | 是否通过审批(0为未处理，1为通过，2为未通过 , -1 作废处理)  | 是否通过审批(0为未处理，1为通过，2为未通过 , -1 作废处理)  |
| opinion | text | YES | - | - | 意见 | 意见 |
| approve_time | datetime | YES | - | - | 审批时间 | 审批时间 |
| approver | varchar(10) | YES | - | - | 审批人 | 审批人 |
| state | char(1) | YES | - | - | (1:为最新审批结果；0：为旧审批结果) | (1:为最新审批结果；0：为旧审批结果) |
| take_place | varchar(15) | YES | 0 | - | 0:未选择 1:供应链 2：库存 | 0:未选择 1:供应链 2：库存 |
| isUnion | int(11) | YES | - | - | 是否联合供应链发货 | 是否联合供应链发货 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| sheetID | BTREE | NON-UNIQUE | sheetID |

---

---
### 10 app_spare_part -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 备件申请明细表 |
| 数据量 | ~47262 行 |
| 数据大小 | 6.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| tx_id | int(11) | YES | - | UNI | - | 关联tx_info的tx_id |
| action_time | datetime | YES | - | - | 操作时间 | 操作时间 |
| isOK | char(1) | YES | - | - | 是否核销(是否核销，0为未核销，1为核销) | 是否核销(是否核销，0为未核销，1为核销) |
| hexiao_time | datetime | YES | - | - | 核销时间 | 核销时间 |
| hexiao_remark | text | YES | - | - | 核销说明 | 核销说明 |
| isNew | char(1) | YES | - | - | 数据状态(0:历史数据；1：最新数据 2:转移申请被转移状态) | 数据状态(0:历史数据；1：最新数据 2:转移申请被转移状态) |
| contract_sub_type | char(1) | YES | - | - | 发货类型（0为RMA ,1为项目保障,2为库存,null:转移申请 3:借用申请) | 发货类型（0为RMA ,1为项目保障,2为库存,null:转移申请 3:借用申请) |
| item_code | varchar(25) | YES | - | - | 物料号 | 物料号 |
| item_name | varchar(255) | YES | - | - | 物料名称 | 物料名称 |
| tain_process | varchar(255) | YES | - | - | 检测报告 | 检测报告 |
| isReceive | char(1) | YES | 0 | - | 0：已发货待确认接收 1：已确认接货 2：待发货确认 | 0：已发货待确认接收 1：已确认接货 2：待发货确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| tx_id | BTREE | UNIQUE | tx_id |

---

---
### 11 back_type -- 返回类型

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 返回类型 |
| 数据量 | ~8 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 主键 | 主键 |
| back | varchar(10) | NO | - | MUL | - | 退回类型编码（主键） |
| back_type | varchar(50) | YES | - | - | - | 退回类型名称 |
| back_state | varchar(200) | YES | - | - | - | 业务含义待确认 |
| remark | text | YES | - | - | 备注 | 备注 |
| status | int(11) | YES | 1 | - | 有效状态0失效 1有效 | 有效状态0失效 1有效 |
| updateTime | datetime | YES | - | - | 更新时间 | 更新时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| back_where_index | BTREE | NON-UNIQUE | back |
| PRIMARY | BTREE | UNIQUE | id |

---

---
### 12 bar -- PPS设备

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | PPS设备 |
| 数据量 | ~1349 行 |
| 数据大小 | 192.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(10) | NO | - | PRI, auto_increment | 主键 | 主键 |
| spare_id | int(10) | NO | - | MUL | pps的主键 | pps的主键 |
| bar_code | varchar(50) | YES | - | - | 设备编码 | 设备编码 |
| bar_model | varchar(1000) | YES | - | - | 设备型号 | 设备型号 |
| bar_num | varchar(50) | YES | - | - | 数量 | 数量 |
| remark | text | YES | - | - | 备注 | 备注 |
| serial_number | varchar(50) | YES | - | - | 序列号 | 序列号 |
| spare_code | varchar(15) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| spare_id | BTREE | NON-UNIQUE | spare_id |

---

---
### 13 brw_app_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 借用申请信息表 |
| 数据量 | ~16207 行 |
| 数据大小 | 4.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| sheetID | varchar(15) | YES | - | UNI | 单据代码 | 单据代码 |
| applicant | varchar(25) | YES | - | - | 申请人 | 申请人 |
| app_time | datetime | YES | - | - | 申请时间 | 申请时间 |
| app_dptNo | varchar(10) | YES | - | - | 申请办事处名称 | 申请办事处名称 |
| contractNo | varchar(25) | YES | - | - | 合同号 | 合同号 |
| prt_name | varchar(255) | YES | - | - | 项目名称 | 项目名称 |
| app_reason | text | YES | - | - | 申请原因 | 申请原因 |
| duty_person | varchar(10) | YES | - | - | 负责人 | 负责人 |
| start_use_time | datetime | YES | - | - | 开始使用时间 | 开始使用时间 |
| kept_place | varchar(10) | YES | - | - | 备件存放地 | 备件存放地 |
| promise_returntime | datetime | YES | - | - | 承诺备件归还时间 | 承诺备件归还时间 |
| extend_returntime | datetime | YES | - | - | 延长归还时间 | 延长归还时间 |
| demand_type | varchar(25) | YES | - | - | 需求类型（维护在sys_state_or_type） | 需求类型（维护在sys_state_or_type） |
| trade_classify | varchar(100) | YES | - | - | 行业分类（手动填写） | 行业分类（手动填写） |
| signing_state | char(1) | YES | - | - | 签单状态（0：已签单；1：未签单） 废弃字段 | 签单状态（0：已签单；1：未签单） 废弃字段 |
| app_type | char(1) | YES | - | - | 申请类型（0：借用申请；1：转移申请;2:历史数据） | 申请类型（0：借用申请；1：转移申请;2:历史数据） |
| addre_id | int(11) | YES | - | - | 关联收件人表ID | 关联收件人表ID |
| his_addre | varchar(64) | YES | - | - | 收件人 | 收件人 |
| his_addre_tel | varchar(64) | YES | - | - | 联系电话 | 联系电话 |
| his_addr | varchar(1024) | YES | - | - | 地址/where | 地址/where |
| his_zipCode | varchar(25) | YES | - | - | 邮编 | 邮编 |
| ischange_duty | char(1) | YES | - | - | 是否转移责任人（0:转移；1：不转移） | 是否转移责任人（0:转移；1：不转移） |
| isQuit | char(1) | YES | - | - | 是否为离职原因导致责任人变更，0：否，1：是 | 是否为离职原因导致责任人变更，0：否，1：是 |
| change_type | char(1) | YES | - | - | 转移类型 | 转移类型 |
| remark | text | YES | - | - | 备注 | 备注 |
| data_state | char(1) | YES | - | - | 数据状态（0：历史；1：最新） | 数据状态（0：历史；1：最新） |
| isSend | char(1) | YES | - | - | 是否发货(0:待发货确认 1：待收货确认) | 是否发货(0:待发货确认 1：待收货确认) |
| isReceive | char(1) | YES | 0 | - | 是否收货(1:已接受 0：未接受) | 是否收货(1:已接受 0：未接受) |
| beforeChange_sheetID | varchar(15) | YES | - | - | 发起转移申请之前的备件所涉及的单据号，此字段是为了保持备件经过转移流转后涉及单据号不变 | 发起转移申请之前的备件所涉及的单据号，此字段是为了保持备件经过转移流转后涉及单据号不变 |
| change_time | datetime | YES | - | - | 备件转移时间 | 备件转移时间 |
| version_no | int(11) | YES | 0 | - | 库存发货配置的版本号 | 库存发货配置的版本号 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| sheetID | BTREE | UNIQUE | sheetID |

---

---
### 14 brw_spare_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 借用备件明细表 |
| 数据量 | ~5846 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| sheetID | varchar(10) | YES | - | MUL | 单据代码 | 单据代码 |
| item_code | varchar(10) | YES | - | - | 物料编码 | 物料编码 |
| item_name | varchar(255) | YES | - | - | 物料名称 | 物料名称 |
| quantity | int(11) | YES | - | - | 数量 | 数量 |
| remark | text | YES | - | - | 备注 | 备注 |
| state | char(1) | YES | 1 | - | 状态（0：历史数据；1：有效数据） | 状态（0：历史数据；1：有效数据） |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| sheetID | BTREE | NON-UNIQUE | sheetID |

---

---
### 15 department -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 旧版部门信息表（Firebird迁移），与fnd_department有重叠 |
| 数据量 | ~122 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| ocrCode | varchar(25) | NO | - | MUL | - | 办事处编码（主键） |
| ocrName | varchar(25) | NO | - | - | - | 办事处名称 |
| isparam | int(11) | YES | - | - | - | 是否参数化 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ocrCode | BTREE | NON-UNIQUE | ocrCode |
| PRIMARY | BTREE | UNIQUE | id |

---

---
### 16 dp_erp_purchase_order_header -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | ERP采购订单头表（D365同步） |
| 数据量 | ~150 行 |
| 数据大小 | 96.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| sourceType | varchar(25) | YES | - | - | 源数据类型 | 源数据类型 |
| sourceId | int(11) | YES | - | - | 源数据ID | 源数据ID |
| purchPoolId | varchar(25) | YES | - | - | 采购订单池 | 采购订单池 |
| purchId | varchar(25) | YES | - | - | 采购订单号 | 采购订单号 |
| vendAccount | varchar(25) | YES | - | - | 供应商账号 | 供应商账号 |
| purchName | varchar(255) | YES | - | - | 采购事项 | 采购事项 |
| purContract | varchar(25) | YES | - | - | 采购合同号 | 采购合同号 |
| salesContract | varchar(2048) | YES | - | - | 销售合同号 | 销售合同号 |
| contractAmount | varchar(25) | YES | - | - | 总金额 | 总金额 |
| workerPurchPlacer | varchar(25) | YES | - | - | 订货人 | 订货人 |
| applicant | varchar(25) | YES | - | - | 申请人 | 申请人 |
| inventLocationId | varchar(25) | YES | - | - | 仓库 | 仓库 |
| deliveryDate | date | YES | - | - | 交货日期 | 交货日期 |
| dlvMode | varchar(25) | YES | - | - | 交货模式 | 交货模式 |
| dlvTerm | varchar(25) | YES | - | - | 交货条款 | 交货条款 |
| payment | varchar(255) | YES | - | - | 付款条款 | 付款条款 |
| paymMode | varchar(25) | YES | - | - | 付款方式 | 付款方式 |
| remark | varchar(4096) | YES | - | - | 整单备注 | 整单备注 |
| otherSysNum | varchar(25) | YES | - | - | 外部系统编号 | 外部系统编号 |
| projectName | varchar(255) | YES | - | - | 项目名称 | 项目名称 |
| projectProgress | varchar(25) | YES | - | - | 项目进度 | 项目进度 |
| subcontractType | varchar(25) | YES | - | - | 转包类型 | 转包类型 |
| subcontStartDate | varchar(25) | YES | - | - | 转包开始日期 | 转包开始日期 |
| subcontEndDate | varchar(25) | YES | - | - | 转包结束日期 | 转包结束日期 |
| dataAreaId | varchar(25) | YES | - | - | 账套 | 账套 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

---
### 17 dp_erp_purchase_order_line -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | ERP采购订单行表（D365同步） |
| 数据量 | ~150 行 |
| 数据大小 | 64.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| headerId | int(11) | YES | - | - | 采购订单HeaderId | 采购订单HeaderId |
| purchId | varchar(25) | YES | - | - | 采购订单号 | 采购订单号 |
| lineNum | varchar(25) | YES | - | - | 采购订单行号（可指定） | 采购订单行号（可指定） |
| itemId | varchar(25) | YES | - | - | 物料编码 | 物料编码 |
| purchQty | decimal(25,2) | YES | - | - | 采购数量 | 采购数量 |
| purchPrice | decimal(25,2) | YES | - | - | 采购价 | 采购价 |
| taxItemGroup | varchar(25) | YES | - | - | 税收组 | 税收组 |
| inventSerialId | varchar(25) | YES | - | - | 厂商型号（复用D365序列号字段） | 厂商型号（复用D365序列号字段） |
| inventSiteId | varchar(25) | YES | - | - | 站点 | 站点 |
| inventLocationId | varchar(25) | YES | - | - | 仓库 | 仓库 |
| wmsLocationId | varchar(25) | YES | - | - | 库位 | 库位 |
| inventTransId | varchar(25) | YES | - | - | 批次号 | 批次号 |
| officeCode | varchar(25) | YES | - | - | 办事处 | 办事处 |
| deliveryDate | date | YES | - | - | 交货日期 | 交货日期 |
| remark | varchar(4096) | YES | - | - | 行备注 | 行备注 |
| multiDimID | varchar(25) | YES | - | - | 行多维度ID | 行多维度ID |
| investmentProject | varchar(255) | YES | - | - | 募投项目 | 募投项目 |
| dimBankAccount | varchar(25) | YES | - | - | 维度-银行账户 | 维度-银行账户 |
| dimCustomer | varchar(25) | YES | - | - | 维度-客户 | 维度-客户 |
| dimVendor | varchar(25) | YES | - | - | 维度-供应商 | 维度-供应商 |
| dimEmployee | varchar(25) | YES | - | - | 维度-员工 | 维度-员工 |
| dimContract | varchar(25) | YES | - | - | 维度-合同号 | 维度-合同号 |
| dimDepartment | varchar(25) | YES | - | - | 维度-部门 | 维度-部门 |
| dimBU | varchar(25) | YES | - | - | 维度-BU | 维度-BU |
| dimProductLine | varchar(25) | YES | - | - | 维度-产品线 | 维度-产品线 |
| dimTerritory | varchar(25) | YES | - | - | 维度-区域 | 维度-区域 |
| dimIndustry | varchar(25) | YES | - | - | 维度-行业 | 维度-行业 |
| dimMultiDimID | varchar(25) | YES | - | - | 维度-多维度ID | 维度-多维度ID |
| dataAreaId | varchar(25) | YES | - | - | 账套 | 账套 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

---
### 18 dp_erp_purchase_receipt_header -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | ERP采购收货头/行表（D365同步） |
| 数据量 | ~36 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| sourceOrderType | varchar(25) | YES | - | - | 订单源数据类型（Subcontract,Dispatch） | 订单源数据类型（Subcontract,Dispatch） |
| sourceOrderId | int(11) | YES | - | - | 订单源数据ID | 订单源数据ID |
| sourceReceiptType | varchar(25) | YES | - | - | 订单源收货类型（SubcontractPayment, DispatchSettlement） | 订单源收货类型（SubcontractPayment, DispatchSettlement） |
| sourceReceiptId | int(11) | YES | - | - | 订单源收货ID | 订单源收货ID |
| purchId | varchar(25) | YES | - | - | 采购订单号 | 采购订单号 |
| deliveryDate | date | YES | - | - | 交货日期 | 交货日期 |
| documentDate | date | YES | - | - | - | 业务含义待确认 |
| packingSlipId | varchar(512) | YES | - | - | 采购收货单号 | 采购收货单号 |
| packingSlipRemark | varchar(1024) | YES | - | - | 采购收货备注 | 采购收货备注 |
| projectProgress | varchar(1024) | YES | - | - | 项目进度 | 项目进度 |
| dataAreaId | varchar(1024) | YES | - | - | 账套 | 账套 |
| customInfo | json | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

---
### 19 dp_erp_purchase_receipt_line -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~36 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| receiptId | int(11) | YES | - | - | 采购订单收货ID | 采购订单收货ID |
| purchId | varchar(25) | YES | - | - | 采购订单号 | 采购订单号 |
| inventSiteId | varchar(25) | YES | - | - | 站点 | 站点 |
| inventLocationId | varchar(25) | YES | - | - | 仓库 | 仓库 |
| wmsLocationId | varchar(25) | YES | - | - | 库位 | 库位 |
| inventTransId | varchar(25) | YES | - | - | 批次号 | 批次号 |
| lineNum | varchar(25) | YES | - | - | 采购订单行号（与批次号二选一，有批次号按批次号收货） | 采购订单行号（与批次号二选一，有批次号按批次号收货） |
| qty | decimal(25,2) | YES | - | - | 收货数量 | 收货数量 |
| price | decimal(25,2) | YES | - | - | 收货单价 | 收货单价 |
| amount | decimal(25,2) | YES | - | - | 收货金额 | 收货金额 |
| dataAreaId | varchar(25) | YES | - | - | 账套 | 账套 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

---
### 20 fnd_company -- 组织机构表

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 组织机构表 |
| 数据量 | ~3 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| pid | int(11) | NO | - | MUL | 父组织机构ID | 父组织机构ID |
| name | varchar(128) | NO | - | - | 组织机构全名 | 组织机构全名 |
| abbr | varchar(64) | NO | - | - | 组织机构简写 | 组织机构简写 |
| website | varchar(128) | YES | - | - | 组织机构网址 | 组织机构网址 |
| code | varchar(25) | YES | 0 | MUL | 组织机构代码 | 组织机构代码 |
| account | varchar(25) | YES |  | - | 组织机构账套 | 组织机构账套 |
| status | smallint(1) | NO | 1 | - | 有效性（1-有效，0-失效），默认有效 | 有效性（1-有效，0-失效），默认有效 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(32) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(32) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| code | BTREE | NON-UNIQUE | code |
| pid | BTREE | NON-UNIQUE | pid |
| PRIMARY | BTREE | UNIQUE | id |

---

---
### 21 firebird_operation_log -- 发货系统Firebird数据库更改日志

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 发货系统Firebird数据库更改日志 |
| 数据量 | ~72066 行 |
| 数据大小 | 29.56 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(10) unsigned | NO | - | PRI, auto_increment | ID | ID |
| sheetId | varchar(25) | YES |  | MUL | 流水号SheetId | 流水号SheetId |
| txId | int(11) | YES | - | - | tx_info的tx_id | tx_info的tx_id |
| contractNo | varchar(45) | YES |  | MUL | 合同号 | 合同号 |
| barCode | varchar(25) | YES |  | MUL | 设备序列号 | 设备序列号 |
| insteadOfNum | varchar(25) | YES |  | MUL | RMA申请被替代的设备序列号 | RMA申请被替代的设备序列号 |
| changeTable | varchar(45) | YES |  | - | 操作的表 | 操作的表 |
| operatTime | timestamp | NO | CURRENT_TIMESTAMP | - | 操作时间 | 操作时间 |
| sqlText | text | YES | - | - | 操作表的sql语句 | 操作表的sql语句 |
| remark | varchar(45) | YES |  | - | 备注 | 备注 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barCode | BTREE | NON-UNIQUE | barCode |
| contractNo | BTREE | NON-UNIQUE | contractNo |
| insteadOfNum | BTREE | NON-UNIQUE | insteadOfNum |
| PRIMARY | BTREE | UNIQUE | id |
| sheetId | BTREE | NON-UNIQUE | sheetId |

---

---
### 22 rma_applicant -- RMA申请

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | RMA申请 |
| 数据量 | ~858 行 |
| 数据大小 | 256.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| spare_code | varchar(8) | YES | - | UNI | 流水号 | 流水号 |
| product_code | varchar(50) | YES | - | - | 产品号 | 产品号 |
| product_name | varchar(200) | YES | - | - | 产品名称 | 产品名称 |
| username | varchar(50) | YES | - | - | 用户名称 | 用户名称 |
| project_name | varchar(200) | YES | - | - | 项目名称/申请原因 | 项目名称/申请原因 |
| old_bar_code | varchar(20) | YES | - | - | 旧设备序列号 | 旧设备序列号 |
| user_linkman | varchar(50) | YES | - | - | 用户联系人 | 用户联系人 |
| back_type | varchar(1) | YES | - | - | 返回类型('1'为"开坏箱",'2'为"开局坏",'3'为“网上运行坏”,'4'为"备件发货坏",'5'为"其他") | 返回类型('1'为"开坏箱",'2'为"开局坏",'3'为“网上运行坏”,'4'为"备件发货坏",'5'为"其他") |
| back_state | varchar(200) | YES | - | - | 返回类型说明 | 返回类型说明 |
| back_num | varchar(11) | YES | - | - | 返回数量 | 返回数量 |
| user_linkman_telephone | varchar(50) | YES | - | - | 用户联系人电话 | 用户联系人电话 |
| applicant_time | datetime | YES | - | - | 申请时间 | 申请时间 |
| problem_description | text | YES | - | - | 问题描述 | 问题描述 |
| analysis_process | varchar(1000) | YES | - | - | 现场分析过程(上传) | 现场分析过程(上传) |
| duty_person | varchar(50) | YES | - | - | 代理公司和负责人 | 代理公司和负责人 |
| start_first_time | varchar(50) | YES | - | - | 初次运行时间 | 初次运行时间 |
| problem_first_time | varchar(50) | YES | - | - | 故障发生时间 | 故障发生时间 |
| applicant_person | varchar(50) | YES | - | - | 申请人 | 申请人 |
| take_place | varchar(1) | YES | 1 | - | 取处(1为供应链部门，2为库存) | 取处(1为供应链部门，2为库存) |
| os_id | varchar(1000) | YES |  | - | 库存id | 库存id |
| address | text | YES | - | - | 地址 | 地址 |
| zip_code | varchar(50) | YES | - | - | 邮政编码 | 邮政编码 |
| tain_type | varchar(1) | YES | - | - | 维保类型(‘1’为“服务合同”,'2'为"项目订单") | 维保类型(‘1’为“服务合同”,'2'为"项目订单") |
| project_code | varchar(50) | YES | - | - | 项目订单号(针对维保类型为项目订单) | 项目订单号(针对维保类型为项目订单) |
| serve_type | varchar(1) | YES | - | - | 服务类型('1'为“坏件先退”,'2'为“好件先行”) | 服务类型('1'为“坏件先退”,'2'为“好件先行”) |
| remark | text | YES | - | - | 备注 | 备注 |
| isPass | varchar(1) | YES | 0 | - | 是否通过(0为未处理，1为通过，2为未通过) | 是否通过(0为未处理，1为通过，2为未通过) |
| isSend | varchar(1) | YES | 0 | - | 是否发货(0为未发货，1为已发货) | 是否发货(0为未发货，1为已发货) |
| rma_type | varchar(1) | YES | 0 | - | 备件类型(0为显示，1为不显示) | 备件类型(0为显示，1为不显示) |
| isNew | varchar(1) | YES | y | - | - | 业务含义待确认 |
| isChange_duty | varchar(1) | YES | n | - | - | 业务含义待确认 |
| opinion | text | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| id | BTREE | UNIQUE | id |
| PRIMARY | BTREE | UNIQUE | id |
| spare_code | BTREE | UNIQUE | spare_code |

---

---
### 23 rma_app_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | RMA申请信息表 |
| 数据量 | ~5507 行 |
| 数据大小 | 2.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| sheetID | varchar(25) | NO | - | UNI | RMA申请单据代码 | RMA申请单据代码 |
| applicant | varchar(10) | YES | - | - | 申请发起人 | 申请发起人 |
| officeCode | varchar(25) | YES | - | - | 办事处或部门编码 | 办事处或部门编码 |
| customer_name | varchar(255) | YES | - | - | 客户名称 | 客户名称 |
| project_name | varchar(255) | YES | - | - | 项目名称 | 项目名称 |
| addreID | int(11) | YES | - | - | 收件人ID，关联addressee_info表 | 收件人ID，关联addressee_info表 |
| application_time | datetime | YES | - | - | 申请发起时间 | 申请发起时间 |
| back | varchar(10) | YES | - | - | 返回类型 | 返回类型 |
| tain | varchar(10) | YES | - | - | 维保类型 | 维保类型 |
| serve | varchar(10) | YES | - | - | 服务类型 | 服务类型 |
| duty_person | varchar(10) | YES | - | - | 负责人 | 负责人 |
| isSend | char(1) | YES | 0 | - | 申请备件状态（0：未发货；1：已发货 2：已接货） | 申请备件状态（0：未发货；1：已发货 2：已接货） |
| isReceive | char(1) | YES | 0 | - | 是否接收(0:未接受 1：已接收) | 是否接收(0:未接受 1：已接收) |
| take_place | char(1) | YES | 0 | - | 备件出处(0:未选择 1:供应链；2：库存) 此时的备件出处只是记录临时的状态，但也可算真实的状态，根据系统设定已不可更改，只需等审批确定后 | 备件出处(0:未选择 1:供应链；2：库存) 此时的备件出处只是记录临时的状态，但也可算真实的状态，根据系统设定已不可更改，只需等审批确定后 |
| isUnion | int(11) | YES | - | - | 是否联合供应链发货 | 是否联合供应链发货 |
| remark | text | YES | - | - | 备注 | 备注 |
| data_state | char(1) | YES | 0 | - | 数据状态（0：最新；1：历史数据） | 数据状态（0：最新；1：历史数据） |
| his_addre | varchar(64) | YES | - | - | 处理历史数据 | 处理历史数据 |
| his_zipCode | varchar(10) | YES | - | - | 处理历史数据 | 处理历史数据 |
| his_addr | varchar(1024) | YES | - | - | 处理历史数据 | 处理历史数据 |
| his_addre_tel | varchar(25) | YES | - | - | 处理历史数据 | 处理历史数据 |
| version_no | int(11) | YES | 0 | - | 发货配置版本号 | 发货配置版本号 |
| insteadState | int(11) | YES | 0 | - | - | 业务含义待确认 |
| rma_back_time | datetime | YES | - | - | 技服执行坏件返回时间 | 技服执行坏件返回时间 |
| rmaRoleIsPass | int(11) | YES | - | - | 故障审核是否通过 0否1是 | 故障审核是否通过 0否1是 |
| rmaRoleOpinion | varchar(255) | YES | - | - | 故障审核审批意见 | 故障审核审批意见 |
| rmaRoleAuditTime | datetime | YES | - | - | 故障审核时间 | 故障审核时间 |
| rmaRoleAuditUser | varchar(25) | YES | - | - | 故障审核用户 | 故障审核用户 |
| qaRoleIsPass | int(11) | YES | - | - | 质量审核是否通过 | 质量审核是否通过 |
| qaRoleOpinion | varchar(255) | YES | - | - | 质量审核审批意见 | 质量审核审批意见 |
| qaRoleAuditTime | datetime | YES | - | - | 质量审核时间 | 质量审核时间 |
| qaRoleAuditUser | varchar(25) | YES | - | - | 质量审核用户 | 质量审核用户 |
| insteadLicense | int(11) | YES | 0 | - | 授权License变更，1:需要，-1:不需要 | 授权License变更，1:需要，-1:不需要 |
| insteadLicenseMail | varchar(255) | YES | - | - | 授权License接收邮箱 | 授权License接收邮箱 |
| licenseMailTime | datetime | YES | - | - | 授权License邮件发送时间 | 授权License邮件发送时间 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| sheetID | BTREE | UNIQUE | sheetID |

---

---
### 24 rma_bar -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~1454 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(10) | NO | - | PRI, auto_increment | - | 主键ID |
| rma_id | int(10) | YES | - | MUL | - | 业务含义待确认 |
| old_bar_code | varchar(50) | YES | - | - | - | 业务含义待确认 |
| item_code | varchar(50) | YES | - | - | - | 业务含义待确认 |
| item_name | varchar(1000) | YES | - | - | - | 业务含义待确认 |
| project_code | varchar(50) | YES | - | - | - | 业务含义待确认 |
| project_name | varchar(50) | YES | - | - | - | 业务含义待确认 |
| problem_description | text | YES | - | - | - | 业务含义待确认 |
| back_state | varchar(50) | YES | - | - | - | 业务含义待确认 |
| start_first_time | varchar(50) | YES | - | - | - | 业务含义待确认 |
| problem_first_time | varchar(50) | YES | - | - | - | 业务含义待确认 |
| analysis_process | varchar(200) | YES | - | - | - | 业务含义待确认 |
| tain_process | varchar(200) | YES | - | - | - | 业务含义待确认 |
| isOK | varchar(1) | YES | 0 | - | 是否核销(0为未核销 1为已核销) | 是否核销(0为未核销 1为已核销) |
| hexiao_time | datetime | YES | - | - | 核销时间 | 核销时间 |
| isBack | varchar(1) | YES | 0 | - | 是否返回(0为未返回1为已返回) | 是否返回(0为未返回1为已返回) |
| back_time | datetime | YES | - | - | 返回时间 | 返回时间 |
| EMS | varchar(20) | YES |  | - | 快递单号 | 快递单号 |
| EMS_company | varchar(20) | YES |  | - | 快递公司 | 快递公司 |
| receive_person | varchar(10) | YES |  | - | 收件人 | 收件人 |
| back_type | varchar(50) | YES | - | - | - | 业务含义待确认 |
| tain_type | varchar(50) | YES | - | - | - | 业务含义待确认 |
| serve_type | varchar(50) | YES | - | - | - | 业务含义待确认 |
| spare_code | varchar(15) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| rma_id | BTREE | NON-UNIQUE | rma_id |

---

---
### 25 rma_info2mes_result -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~3020 行 |
| 数据大小 | 6.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| sheetID | varchar(15) | NO | - | MUL | RMA申请流水号，多个合同号_n | RMA申请流水号，多个合同号_n |
| type | varchar(1) | NO | - | - | 接口上传结果，S、E | 接口上传结果，S、E |
| message | varchar(255) | NO | - | - | 上传结果信息 | 上传结果信息 |
| xmlStr | text | YES | - | - | 上传的xml：rmaInfoHeader | 上传的xml：rmaInfoHeader |
| xmlStr1 | text | YES | - | - | 上传的xml：rmaInfoDeatil | 上传的xml：rmaInfoDeatil |
| createTime | datetime | YES | - | - | 上传时间 | 上传时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| sheetID | BTREE | NON-UNIQUE | sheetID |

---

---
### 26 rma_repair_report_from_mes -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~2166 行 |
| 数据大小 | 480.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| sheetId | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| barCode | varchar(50) | YES | - | - | - | 业务含义待确认 |
| contractNo | varchar(25) | YES | - | - | - | 业务含义待确认 |
| result | tinytext | YES | - | - | - | 业务含义待确认 |
| path | varchar(255) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| sheetId_where_index | BTREE | NON-UNIQUE | sheetId |

---

---
### 27 rma_spare_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | RMA备件明细表 |
| 数据量 | ~13881 行 |
| 数据大小 | 4.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| tx_id | int(11) | YES | - | MUL | 交易号(关联application_transInfo表） | 交易号(关联application_transInfo表） |
| item_code | varchar(15) | YES | - | - | 物料号 | 物料号 |
| item_name | varchar(255) | YES | - | - | 物料名称 | 物料名称 |
| contractNo | varchar(25) | YES | - | - | 合同号 | 合同号 |
| contractRemark | varchar(4096) | YES | - | - | 合同备注 | 合同备注 |
| project_name | varchar(255) | YES | - | - | 项目名称 | 项目名称 |
| problem_desc | text | YES | - | - | 问题描述 | 问题描述 |
| first_working_time | varchar(25) | YES | - | - | 第一次运行时间 | 第一次运行时间 |
| conk_out_time | varchar(25) | YES | - | - | 故障发生时间 | 故障发生时间 |
| doa_path | varchar(100) | YES | - | - | doa故障分析单（下载路径） | doa故障分析单（下载路径） |
| check_path | varchar(100) | YES | - | - | 检测报告(下载路径) | 检测报告(下载路径) |
| repair_state | char(1) | YES | - | - | 维修状态（保留字段） | 维修状态（保留字段） |
| isBack | char(1) | YES | 0 | - | 坏件是否返回（0：未返回;1:已返回） | 坏件是否返回（0：未返回;1:已返回） |
| back_time | datetime | YES | - | - | 返回时间 | 返回时间 |
| isOK | char(1) | YES | 0 | - | 核销状态(0:未核销；1:已核销) | 核销状态(0:未核销；1:已核销) |
| hexiao_time | datetime | YES | - | - | 核销时间 | 核销时间 |
| analysis_state | int(11) | YES | - | - | 坏件故障分析状态  -1 未分析  1 已分析 | 坏件故障分析状态  -1 未分析  1 已分析 |
| insteadLicense | int(11) | YES | 0 | - | 授权License变更，1:需要，-1:不需要 | 授权License变更，1:需要，-1:不需要 |
| insteadLicenseMail | varchar(255) | YES | - | - | 授权License接收邮箱 | 授权License接收邮箱 |
| licenseMailTime | datetime | YES | - | - | 授权License邮件发送时间 | 授权License邮件发送时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| tx_id | BTREE | NON-UNIQUE | tx_id |

---

---
### 28 role -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 旧版角色表（已被t_role替代） |
| 数据量 | ~7 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| roleId | int(11) | NO | - | - | - | 业务含义待确认 |
| roleName | varchar(10) | NO | - | - | - | 业务含义待确认 |
| status | int(11) | YES | - | - | - | 状态 |
| mark | text | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

---
### 29 serve_type -- 服务类型

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 服务类型 |
| 数据量 | ~4 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| serve | varchar(10) | NO | - | MUL | - | 服务编码 |
| serve_type | varchar(10) | YES | - | - | - | 服务类型名称 |
| remark | text | YES | - | - | 备注 | 备注 |
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| serve_where_index | BTREE | NON-UNIQUE | serve |

---

---
### 30 spare_parts -- 备件 contract_sub_type(0RMA 1保障 2库存)

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 备件 contract_sub_type(0RMA 1保障 2库存) |
| 数据量 | ~6282 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(10) | NO | - | PRI, auto_increment | - | 主键ID |
| bar_code | varchar(25) | YES | - | - | 备件序列号 | 备件序列号 |
| spare_code | varchar(50) | YES | - | MUL | 流水号 | 流水号 |
| action_time | varchar(50) | NO | - | - | 操作时间 | 操作时间 |
| isOK | char(1) | YES | 0 | - | 设备状态(是否核销，0为未核销，1为核销) | 设备状态(是否核销，0为未核销，1为核销) |
| isNew | char(1) | YES | y | - | 数据状态(是否是最新的数据) | 数据状态(是否是最新的数据) |
| in_time | varchar(50) | YES | - | - | 收货时间 | 收货时间 |
| out_time | varchar(50) | YES | - | - | 发货时间 | 发货时间 |
| contract_sub_type | varchar(5) | YES | - | - | 类型(0为RMA ,1为项目保障,2为库存) | 类型(0为RMA ,1为项目保障,2为库存) |
| EMS | varchar(50) | YES | - | - | 快递单号 | 快递单号 |
| EMS_company | varchar(50) | YES | - | - | 快递公司 | 快递公司 |
| item_code | varchar(50) | YES | - | - | 物料号 | 物料号 |
| item_name | varchar(200) | YES | - | - | 物料名称 | 物料名称 |
| tain_process | varchar(200) | YES |  | - | 检测报告 | 检测报告 |
| isSure | varchar(1) | YES | 0 | - | 确认(1待确认,2以确认) | 确认(1待确认,2以确认) |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| spare_code | BTREE | NON-UNIQUE | spare_code |

---

---
### 31 spare_parts_applicant -- 项目保障备件申请

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目保障备件申请 |
| 数据量 | ~4126 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(10) | NO | - | PRI, auto_increment | 主键 | 主键 |
| applicant_person | varchar(50) | YES | - | - | 申请人 | 申请人 |
| applicant_time | datetime | YES | - | - | 申请时间 | 申请时间 |
| applicant_department | varchar(50) | YES | - | - | 申请部门 | 申请部门 |
| spare_code | varchar(50) | YES | - | UNI | 流水号 | 流水号 |
| applicant_reason | varchar(500) | YES | - | - | 申请原因 | 申请原因 |
| remark | text | YES | - | - | 备注 | 备注 |
| zip_code | varchar(50) | YES | - | - | 邮政编码 | 邮政编码 |
| isPass | varchar(1) | YES | 0 | - | 是否通过(0为未处理，1为通过，2为未通过) | 是否通过(0为未处理，1为通过，2为未通过) |
| isSend | varchar(1) | NO | 0 | - | 是否通过(0为未发货，1为已发货) | 是否通过(0为未发货，1为已发货) |
| address | varchar(200) | YES | - | - | 地址 | 地址 |
| receive_person | varchar(200) | YES | - | - | 收件人 | 收件人 |
| receive_person_tel | varchar(200) | YES | - | - | 收件人电话 | 收件人电话 |
| spare_parts_type | varchar(1) | YES | - | - | 备件类型(0为项目保障，1为库存) | 备件类型(0为项目保障，1为库存) |
| duty_person | varchar(10) | YES | - | - | 责任人 | 责任人 |
| applicant_type | varchar(1) | YES | 0 | - | 申请类型(0为普通申请，1为转移申请) | 申请类型(0为普通申请，1为转移申请) |
| isChange_duty | varchar(1) | YES | 1 | - | 转移类型(0为转移责任人，1为不转移责任人) | 转移类型(0为转移责任人，1为不转移责任人) |
| isQuit | char(1) | YES | - | - | 是否为离职原因导致责任人变更，0：否，1：是 | 是否为离职原因导致责任人变更，0：否，1：是 |
| isReceive | varchar(1) | YES | 0 | - | 是否收到(0为未收到，1为收到) | 是否收到(0为未收到，1为收到) |
| transfer_time | datetime | YES | - | - | 转移时间 | 转移时间 |
| applicant_project | varchar(255) | YES | - | - | - | 业务含义待确认 |
| start_time | date | YES | - | - | - | 业务含义待确认 |
| promise_returntime | date | YES | - | - | - | 业务含义待确认 |
| kept_place | varchar(255) | YES | - | - | - | 业务含义待确认 |
| beforeChange_spareCode | varchar(50) | YES | - | - | - | 业务含义待确认 |
| change_type | char(1) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| spare_code | BTREE | UNIQUE | spare_code |

---

---
### 32 sys_state_or_type -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 系统状态/类型字典表 |
| 数据量 | ~30 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| stCode | varchar(25) | YES | - | MUL | - | 状态/类型分类编码 |
| stName | varchar(25) | YES | - | - | - | 业务含义待确认 |
| resolveCode | varchar(10) | YES | - | MUL | - | 解析编码 |
| resolveName | varchar(25) | YES | - | - | - | 解析名称 |
| validity | int(11) | YES | 1 | - | 1有效 0 无效 | 1有效 0 无效 |
| remark | varchar(100) | YES | - | - | 说明 | 说明 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| st | BTREE | NON-UNIQUE | resolveCode |
| stCode | BTREE | NON-UNIQUE | stCode |

---

---
### 33 tain_type -- 维保类型

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 维保类型 |
| 数据量 | ~3 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| tain | varchar(10) | NO | - | MUL | - | 维修编码（主键） |
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| tain_type | varchar(50) | YES | - | - | - | 维修类型名称 |
| remark | text | YES | - | - | 备注 | 备注 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| tain_where_index | BTREE | NON-UNIQUE | tain |

---

---
### 34 tb_sys_log -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~3183362 行 |
| 数据大小 | 164.25 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID | int(32) unsigned | NO | - | PRI, auto_increment | - | 主键ID |
| USER_NAME | varchar(80) | NO |  | MUL | - | 业务含义待确认 |
| IP | char(20) | NO | 0 | - | - | 业务含义待确认 |
| ACTION | varchar(1024) | NO |  | - | - | 业务含义待确认 |
| RESULT | int(32) unsigned | NO | 0 | - | - | 业务含义待确认 |
| INFO | varchar(20000) | NO |  | - | - | 业务含义待确认 |
| TIME | int(32) | NO | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID |
| USER_NAME | BTREE | NON-UNIQUE | USER_NAME |

---

---
### 35 tx_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 物流/调拨信息表 |
| 数据量 | ~60939 行 |
| 数据大小 | 7.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| tx_id | int(11) | NO | - | PRI, auto_increment | - | 调拨ID（主键） |
| sheetID | varchar(10) | YES | - | MUL | 单据代码 | 单据代码 |
| tx_type | int(1) | YES | - | - | 单据类型(0:RMA单据;1:借用申请 2：转移) | 单据类型(0:RMA单据;1:借用申请 2：转移) |
| spare_serialNum | varchar(50) | YES | - | - | 备件序列号 | 备件序列号 |
| sendout_place | char(1) | YES | - | - | 历史记录（1：供应链；2：库存） | 历史记录（1：供应链；2：库存） |
| sendout_whsCode | varchar(10) | YES | - | - | 备件发出库房 | 备件发出库房 |
| send_time | datetime | YES | - | - | 出库时间 | 出库时间 |
| receving_place | varchar(50) | YES | - | - | 备件接受地 | 备件接受地 |
| receving_whsCode | varchar(10) | YES | - | - | 备件接收库房 | 备件接收库房 |
| receive_time | datetime | YES | - | - | 收货时间 | 收货时间 |
| quantity | int(11) | YES | 1 | - | 数量 | 数量 |
| EMS_num | varchar(255) | YES | - | - | 快递单号 | 快递单号 |
| EMS_company | varchar(255) | YES | - | - | 快递公司 | 快递公司 |
| addressee | varchar(25) | YES | - | - | 收件人 | 收件人 |
| isRMA | char(1) | YES | 0 | - | 是否是RMA的坏件返回（1：是;0:好件） | 是否是RMA的坏件返回（1：是;0:好件） |
| version_no | int(11) | YES | 0 | - | 版本号  -1时为历史选择的数据 | 版本号  -1时为历史选择的数据 |
| detail_id | int(11) | YES | - | - | 库存表中的id | 库存表中的id |
| instead_of_num | varchar(25) | YES | - | - | 好件替换坏件关系 | 好件替换坏件关系 |
| shiftimes | int(11) | YES | - | - | 备件经过转移次数 | 备件经过转移次数 |
| turnovertimes | int(11) | YES | - | - | - | 翻转次数 |
| allottimes | int(11) | YES | - | - | - | 分配次数 |
| instead_time | datetime | YES | - | - | - | 业务含义待确认 |
| datastate | int(1) | YES | 1 | - | 保持历史数据有效性 0 失效 1 有效 | 保持历史数据有效性 0 失效 1 有效 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | tx_id |
| sheetID | BTREE | NON-UNIQUE | sheetID |

---

---
### 36 user -- 用户

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 用户 |
| 数据量 | ~72 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(8) | NO | - | PRI, auto_increment | - | 主键ID |
| username | varchar(20) | YES | - | UNI | 工号 | 工号 |
| password | varchar(32) | YES | - | - | 密码 | 密码 |
| role | int(1) | YES | - | - | 4：超级管理员；3管理员；1：普通用户 | 4：超级管理员；3管理员；1：普通用户 |
| mail | varchar(100) | YES | - | - | 邮箱 | 邮箱 |
| lastLogin | datetime | YES | - | - | 上次登陆时间 | 上次登陆时间 |
| department | varchar(50) | YES | - | - | 部门 | 部门 |
| name | varchar(50) | YES | - | - | 姓名 | 姓名 |
| tel | varchar(50) | YES | - | - | 电话 | 电话 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| username | BTREE | UNIQUE | username |

---

---
### 37 user_info -- 用户

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 用户 |
| 数据量 | ~190 行 |
| 数据大小 | 80.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(8) | NO | - | PRI, auto_increment | - | 主键ID |
| username | varchar(20) | YES | - | UNI | 工号 | 工号 |
| password | varchar(32) | YES | - | - | 密码 | 密码 |
| role | int(1) | YES | - | - | 1：普通用户；2：07库；3：技服；4：管理员 5：供应链 | 1：普通用户；2：07库；3：技服；4：管理员 5：供应链 |
| mail | varchar(100) | YES | - | - | 邮箱 | 邮箱 |
| lastLogin | datetime | YES | - | - | 上次登陆时间 | 上次登陆时间 |
| department | varchar(50) | YES | - | - | 部门 | 部门 |
| realname | varchar(50) | YES | - | - | 姓名 | 姓名 |
| tel | varchar(50) | YES | - | - | 手机 | 手机 |
| state | int(1) | YES | 1 | - | 用户有效性 | 用户有效性 |
| title | varchar(25) | YES | - | - | 职称 | 职称 |
| office | varchar(25) | YES | - | - | 所在区域办事处 | 所在区域办事处 |
| office_addr | text | YES | - | - | 办事处地址 | 办事处地址 |
| guhua | varchar(15) | YES | - | - | 固话 | 固话 |
| fax | varchar(25) | YES | - | - | 传真 | 传真 |
| whs_code | varchar(255) | YES | - | - | 库房信息 | 库房信息 |
| pwd_over_due_date | datetime | YES | - | - | 密码修改记录 | 密码修改记录 |
| teams | varchar(255) | YES | - | - | 所属团队 | 所属团队 |
| teamRole | varchar(255) | YES | - | - | 团队角色 | 团队角色 |
| province | varchar(255) | YES | - | - | 省份/直辖市 | 省份/直辖市 |
| city | varchar(255) | YES | - | - | 市 | 市 |
| district | varchar(255) | YES | - | - | 区 | 区 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| username | BTREE | UNIQUE | username |

---

---
### 38 user_team -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~34 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| realname | varchar(255) | YES | - | - | - | 业务含义待确认 |
| teamRole | varchar(255) | YES | - | - | - | 业务含义待确认 |
| mail | varchar(255) | YES | - | - | - | 业务含义待确认 |
| tel | varchar(255) | YES | - | - | - | 业务含义待确认 |
| department | varchar(255) | YES | - | - | - | 业务含义待确认 |
| province | varchar(255) | YES | - | - | - | 业务含义待确认 |
| addr | varchar(255) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|

---

---
### 39 warehouse -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 仓库信息表/仓库明细表 |
| 数据量 | ~36 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| whs_id | int(11) | NO | - | PRI, auto_increment | - | 业务含义待确认 |
| whs_code | varchar(10) | YES | - | MUL | 库房编码 | 库房编码 |
| whs_name | varchar(25) | YES | - | - | 库房名称 | 库房名称 |
| whs_addr | varchar(255) | YES | - | - | 库房地址 | 库房地址 |
| username | varchar(10) | YES | - | - | 负责人工号 | 负责人工号 |
| department | varchar(25) | YES | - | - | - | 业务含义待确认 |
| contact_tel | varchar(15) | YES | - | - | 联系电话 | 联系电话 |
| contact_mail | varchar(50) | YES | - | - | 联系邮箱 | 联系邮箱 |
| remark | text | YES | - | - | 备注 | 备注 |
| whs_state | char(1) | YES | 1 | - | 1:有效 | 1:有效 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | whs_id |
| whs_code | BTREE | NON-UNIQUE | whs_code |

---

---
### 40 warehouse_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~11561 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| info_id | int(11) | NO | - | PRI, auto_increment | - | 业务含义待确认 |
| item_code | varchar(10) | YES | - | - | - | 业务含义待确认 |
| item_name | varchar(100) | YES | - | - | - | 业务含义待确认 |
| whs_code | varchar(10) | YES | - | - | - | 业务含义待确认 |
| quantity | int(11) | YES | - | - | - | 业务含义待确认 |
| item_state | char(1) | YES | - | - | 0:坏件 1：好件 | 0:坏件 1：好件 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | info_id |

---

---
### 41 warehouse_info_detail -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~58033 行 |
| 数据大小 | 4.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| info_id | int(11) | YES | - | MUL | - | 业务含义待确认 |
| spare_serialNum | varchar(25) | YES | - | - | - | 业务含义待确认 |
| demand_type | varchar(25) | YES | - | - | 状态维护在sys_state_or_type | 状态维护在sys_state_or_type |
| tx_id | int(11) | YES | - | - | - | 业务含义待确认 |
| state | varchar(2) | YES | - | - | 1：在库 2：客户 3：被申请 | 1：在库 2：客户 3：被申请 |
| data_state | char(1) | YES | 1 | - | 0:历史 1：最新 | 0:历史 1：最新 |
| in_time | datetime | YES | - | - | 入库时间 | 入库时间 |
| finance_in_time | datetime | YES | - | - | 财务入库时间 | 财务入库时间 |
| analyse_in_time | datetime | YES | - | - | - | 业务含义待确认 |
| analyse_out_time | datetime | YES | - | - | - | 业务含义待确认 |
| gaizhi_in_time | datetime | YES | - | - | - | 业务含义待确认 |
| gaizhi_out_time | datetime | YES | - | - | - | 业务含义待确认 |
| remark | text | YES | - | - | - | 备注 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| info_id | BTREE | NON-UNIQUE | info_id |
| PRIMARY | BTREE | UNIQUE | id |

---

---
### 42 warranty_change_logs -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 维保变更日志表/维保信息表 |
| 数据量 | ~55 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| username | varchar(10) | NO | - | - | - | 业务含义待确认 |
| updateType | int(11) | YES | - | - | - | 业务含义待确认 |
| barcode | varchar(20) | YES | - | - | - | 业务含义待确认 |
| warrantyStartTime | datetime | YES | - | - | - | 业务含义待确认 |
| warrantyEndTime | datetime | YES | - | - | - | 业务含义待确认 |
| warrantyTimes | int(11) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

---
### 43 warranty_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~0 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| packlistId | int(11) | YES | - | - | 标识已经同步 | 标识已经同步 |
| contractId | int(11) | YES | - | - | 标识已经同步 | 标识已经同步 |
| barCode | varchar(25) | YES | - | - | 序列号 | 序列号 |
| officeCode | varchar(25) | YES | - | - | 办事处 | 办事处 |
| projectName | varchar(255) | YES | - | - | 项目名称 | 项目名称 |
| contractNo | varchar(25) | YES | - | - | 合同号 | 合同号 |
| contractType | int(11) | YES | - | - | 合同类型 | 合同类型 |
| customerName | varchar(255) | YES | - | - | 客户名称 | 客户名称 |
| itemCode | varchar(8) | YES | - | - | 物料编码 | 物料编码 |
| itemName | varchar(255) | YES | - | - | 物料描述 | 物料描述 |
| warrantyLevel | varchar(8) | YES | - | - | 维保级别 | 维保级别 |
| warrantyStartTime | datetime | YES | - | - | 维保开始时间 | 维保开始时间 |
| warrantyEndTime | datetime | YES | - | - | 维保结束时间 | 维保结束时间 |
| warrantyLimit | int(11) | YES | - | - | 维保年限 | 维保年限 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

---
### 44 workflow_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 工作流信息表，关联Activiti流程与业务单据 |
| 数据量 | ~37771 行 |
| 数据大小 | 2.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| sheetID | varchar(10) | NO | - | - | - | 业务含义待确认 |
| sheet_type | char(1) | YES | - | - | 单据类型（0：RMA 1：借用 2：转移） | 单据类型（0：RMA 1：借用 2：转移） |
| workflow_action | char(1) | NO | - | - | 所需做的操作(1:审批选择备件 ；2：发货确认 ；3：接货确认 5：重新审批 6：RMA申请坏件替换关系确认 4：坏件返回确认 ，7.坏件核销 8 技服执行坏件返回确认) | 所需做的操作(1:审批选择备件 ；2：发货确认 ；3：接货确认 5：重新审批 6：RMA申请坏件替换关系确认 4：坏件返回确认 ，7.坏件核销 8 技服执行坏件返回确认) |
| action_people | varchar(10) | NO | - | - | 操作的用户 | 操作的用户 |
| action_state | char(1) | NO | - | - | 1:待完成 2：已完成 | 1:待完成 2：已完成 |
| node | int(11) | YES | - | - | 节点 | 节点 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

---


# 四、临时表（temp_*/tmp_*）

> 查询优化用临时表，用于报表查询性能提升。部分临时表是对应视图的数据物化缓存。
> 注意：临时表应定期清理，避免占用过多存储空间。

---

### 1 temp_contract_market_system -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 临时合同-市场体系关联表 |
| 数据量 | ~59320 行 |
| 数据大小 | 4.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contract_code | varchar(25) | YES | - | MUL | - | 合同号 |
| marketCode | varchar(10) | YES | - | - | - | 市场编码 |
| marketName | varchar(15) | YES | - | - | - | 市场名称 |
| systemId | int(11) | YES | - | - | - | 体系ID |
| systemName | varchar(15) | YES | - | - | - | 体系名称 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contract_code | BTREE | NON-UNIQUE | contract_code |

---

---
### 2 temp_max_ppfs -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 临时项目属性最大值表 |
| 数据量 | ~41086 行 |
| 数据大小 | 8.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | 0 | MUL | - | 主键ID |
| projectCode | varchar(25) | YES | - | MUL | - | 项目编码 |
| orderExecNumber | varchar(25) | YES | - | - | - | 订单执行单号 |
| serviceTypeName | varchar(10) | YES | - | - | - | 业务含义待确认 |
| channelName | varchar(255) | YES | - | - | 出货代理商名称 | 出货代理商名称 |
| agentName | varchar(500) | YES | - | - | 代理商名称 | 代理商名称 |
| salesManCode | varchar(10) | YES | - | - | - | 销售人员编码 |
| salesManName | varchar(10) | YES | - | - | - | 销售人员姓名 |
| corporationCode | varchar(25) | YES | 01 | - | 公司编码 | 公司编码 |
| customerProjectName | varchar(255) | YES | - | - | 客户项目名称 | 客户项目名称 |
| finalCustomerName | varchar(255) | YES | - | - | 最终客户名称 | 最终客户名称 |
| majorProjectLevel | varchar(255) | YES | - | - | 重大项目级别 | 重大项目级别 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| id | BTREE | NON-UNIQUE | id |
| projectCode | BTREE | NON-UNIQUE | projectCode |

---

---
### 3 temp_project_sales_change -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 临时项目销售变更表 |
| 数据量 | ~191073 行 |
| 数据大小 | 10.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectId | varchar(11) | NO | 0 | - | 项目头信息主键,跟项目其他具体信息关联 | 项目头信息主键,跟项目其他具体信息关联 |
| memberRole | varchar(2) | NO | 0 | - | - | 业务含义待确认 |
| memberCode | varchar(10) | YES | - | - | - | 业务含义待确认 |
| memberName | varchar(10) | YES | - | - | - | 业务含义待确认 |
| phoneNum | varchar(45) | YES | - | - | - | 业务含义待确认 |
| email | varchar(100) | YES | - | - | - | 业务含义待确认 |
| fromFlag | int(1) | NO | 0 | - | - | 业务含义待确认 |
| createTime | datetime | NO | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | NO | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|

---

---
### 4 temp_query_shipment -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 临时发货查询结果表 |
| 数据量 | ~145128 行 |
| 数据大小 | 13.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| packlist_id | varchar(64) | YES | - | - | - | 装箱单ID |
| con_id | varchar(64) | YES | - | MUL | - | 合同ID |
| period | varchar(6) | YES | - | MUL | - | 期间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| period | BTREE | NON-UNIQUE | period,con_id,packlist_id |
| period2 | BTREE | NON-UNIQUE | con_id,packlist_id,period |

---

---
### 5 temp_query_shipment_barcode -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 临时发货条码查询结果表 |
| 数据量 | ~335062 行 |
| 数据大小 | 28.56 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| pack_id | varchar(64) | YES | - | MUL | - | 装箱单ID |
| item | varchar(16) | YES | - | - | - | 物料编码 |
| count | bigint(21) | NO | 0 | - | - | 数量 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| packItem | BTREE | NON-UNIQUE | pack_id,item,count |

---

---
### 6 tmp_tb_contract_shipment -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 临时合同发货汇总表 |
| 数据量 | ~406052 行 |
| 数据大小 | 161.72 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectId | int(11) | NO | 0 | - | 项目头信息主键,跟项目其他具体信息关联 | 项目头信息主键,跟项目其他具体信息关联 |
| projectName | varchar(246) | YES | - | - | - | 业务含义待确认 |
| contractNos | text | YES | - | - | - | 业务含义待确认 |
| officeName | varchar(20) | YES | - | - | - | 业务含义待确认 |
| ssfsName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| rank | varchar(255) | YES | - | - | - | 业务含义待确认 |
| majorProjectLevel | varchar(255) | YES | - | - | 重大项目级别 | 重大项目级别 |
| salesManName | varchar(45) | YES | - | - | 人员名称 | 人员名称 |
| serviceManagerName | varchar(45) | YES | - | - | 人员名称 | 人员名称 |
| projectStartTime | datetime | YES | - | - | 项目开始实施时间 | 项目开始实施时间 |
| executionStateName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| closeProcessStateName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| shipmentStateName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| finalCustomerName | varchar(255) | YES | - | - | 最终客户名称 | 最终客户名称 |
| contractNo | varchar(25) | YES | - | MUL | - | 合同号 |
| barCode | varchar(50) | YES | - | - | - | 业务含义待确认 |
| itemCode | varchar(25) | YES | - | - | - | 业务含义待确认 |
| itemModel | varchar(255) | YES | - | - | - | 业务含义待确认 |
| itemName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| receiveName | text | YES | - | - | 收件人 | 收件人 |
| emsNum | text | YES | - | - | 快递单号 | 快递单号 |
| packdate | datetime | YES | - | - | - | 业务含义待确认 |
| emsCompany | varchar(255) | YES | - | - | - | 业务含义待确认 |
| installAddress | binary(0) | YES | - | - | - | 业务含义待确认 |
| chProjectId | binary(0) | YES | - | - | - | 业务含义待确认 |
| chContractNo | binary(0) | YES | - | - | - | 业务含义待确认 |
| transferProjectId | binary(0) | YES | - | - | - | 业务含义待确认 |
| transferContractNo | binary(0) | YES | - | - | - | 业务含义待确认 |
| transferFlag | int(2) | NO | 0 | - | - | 业务含义待确认 |
| barCode2 | varchar(50) | YES | - | - | 母子公司发货序列号对应关系 | 母子公司发货序列号对应关系 |
| itemCode2 | varchar(25) | YES | - | - | - | 业务含义待确认 |
| itemModel2 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| itemName2 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| profitCenter | varchar(32) | YES | - | - | 利润中心 | 利润中心 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | contractNo,projectId |

---

---
### 7 tmp_tb_project_contract -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 临时项目合同关联表 |
| 数据量 | ~27312 行 |
| 数据大小 | 15.55 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectId | int(11) | NO | 0 | - | 项目头信息主键,跟项目其他具体信息关联 | 项目头信息主键,跟项目其他具体信息关联 |
| projectCode | varchar(45) | NO | - | - | 项目名称 | 项目名称 |
| rank | varchar(255) | YES | - | - | - | 业务含义待确认 |
| projectName | varchar(246) | YES | - | - | - | 业务含义待确认 |
| majorProjectLevel | varchar(255) | YES | - | - | 重大项目级别 | 重大项目级别 |
| projectStateName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| projectState | varchar(11) | YES | - | - | 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态 | 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态 |
| contractNos | text | YES | - | - | - | 业务含义待确认 |
| systemName | varchar(255) | YES | - | - | 系统部ID | 系统部ID |
| compId | int(2) | YES | - | - | 公司ID | 公司ID |
| officeCode | varchar(255) | YES | - | - | 办事处编码 | 办事处编码 |
| officeName | varchar(20) | YES | - | - | - | 业务含义待确认 |
| salesManCode | varchar(45) | YES | - | - | 人员编码,外部人员为空 | 人员编码,外部人员为空 |
| salesManName | varchar(45) | YES | - | - | 人员名称 | 人员名称 |
| serviceManager | varchar(45) | YES | - | - | 人员编码,外部人员为空 | 人员编码,外部人员为空 |
| serviceManagerName | varchar(45) | YES | - | - | 人员名称 | 人员名称 |
| projectManager | varchar(45) | YES | - | - | 人员编码,外部人员为空 | 人员编码,外部人员为空 |
| projectManagerName | varchar(45) | YES | - | - | 人员名称 | 人员名称 |
| groupMember | varchar(45) | YES | - | - | 人员编码,外部人员为空 | 人员编码,外部人员为空 |
| groupMemberName | varchar(45) | YES | - | - | 人员名称 | 人员名称 |
| teamMemberCodes | text | YES | - | - | - | 业务含义待确认 |
| teamMemberNames | text | YES | - | - | - | 业务含义待确认 |
| shipmentStateName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| shipmentState | varchar(11) | YES | - | - | 项目发货状态 -1 已发货 1 未发货 2部分发货 | 项目发货状态 -1 已发货 1 未发货 2部分发货 |
| planStateName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| projectPlanState | varchar(10) | YES | - | - | 工程计划状态 | 工程计划状态 |
| executionStateName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| executionState | varchar(45) | YES | 5 | - | 实施状态 | 实施状态 |
| closeProcessStateName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| closeProcessState | varchar(45) | YES | 10 | - | 闭环流程状态 | 闭环流程状态 |
| projectStartTime | datetime | YES | - | - | 项目开始实施时间 | 项目开始实施时间 |
| projectCreateTime | datetime | YES | - | - | 记录数据创建时间 | 记录数据创建时间 |
| projectRefreshTime | datetime | YES | - | - | 项目相关数据最后编辑时间 | 项目相关数据最后编辑时间 |
| projectCloseTime | datetime | YES | - | - | 项目闭环时间点 | 项目闭环时间点 |
| ssfs | varchar(11) | YES | - | - | - | 业务含义待确认 |
| ssfsName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| partnerChannel | varchar(91) | YES | - | - | - | 业务含义待确认 |
| serviceChannel | varchar(45) | YES | - | - | - | 业务含义待确认 |
| agentChannel | varchar(45) | YES | - | - | - | 业务含义待确认 |
| warrantyStatus | binary(0) | YES | - | - | - | 业务含义待确认 |
| warrantyStatusName | binary(0) | YES | - | - | - | 业务含义待确认 |
| warrantyGrade | binary(0) | YES | - | - | - | 业务含义待确认 |
| warrantyGradeName | binary(0) | YES | - | - | - | 业务含义待确认 |
| wafService | binary(0) | YES | - | - | - | 业务含义待确认 |
| wafServiceName | binary(0) | YES | - | - | - | 业务含义待确认 |
| finalCustomerName | varchar(255) | YES | - | - | 最终客户名称 | 最终客户名称 |
| salesType | varchar(25) | YES | - | - | 销售类型 | 销售类型 |
| compName | varchar(64) | YES | - | - | 组织机构简写 | 组织机构简写 |
| smsProjectCode | varchar(45) | YES | - | - | 原SMS项目编码 | 原SMS项目编码 |
| contractNo | varchar(45) | YES | - | MUL | 合同号 | 合同号 |
| sourceContractNo | varchar(45) | YES | - | MUL | - | 来源合同号 |
| profitCenter | varchar(255) | YES | - | - | 办事处编码 | 办事处编码 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | contractNo,projectId |
| sourceContractNo | BTREE | NON-UNIQUE | sourceContractNo,projectId |

---

---
### 8 tmp_tb_project_filtered -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 临时项目过滤结果表 |
| 数据量 | ~27778 行 |
| 数据大小 | 13.55 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectId | int(11) | NO | 0 | - | 项目头信息主键,跟项目其他具体信息关联 | 项目头信息主键,跟项目其他具体信息关联 |
| projectCode | varchar(45) | NO | - | - | 项目名称 | 项目名称 |
| rank | varchar(255) | YES | - | - | - | 业务含义待确认 |
| projectName | varchar(246) | YES | - | - | - | 项目名称 |
| majorProjectLevel | varchar(255) | YES | - | - | 重大项目级别 | 重大项目级别 |
| projectStateName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| projectState | varchar(11) | YES | - | - | 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态 | 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态 |
| contractNos | text | YES | - | - | - | 业务含义待确认 |
| systemName | varchar(255) | YES | - | - | 系统部ID | 系统部ID |
| compId | int(2) | YES | - | - | 公司ID | 公司ID |
| officeCode | varchar(255) | YES | - | - | 办事处编码 | 办事处编码 |
| officeName | varchar(20) | YES | - | - | - | 业务含义待确认 |
| salesManCode | varchar(45) | YES | - | - | 人员编码,外部人员为空 | 人员编码,外部人员为空 |
| salesManName | varchar(45) | YES | - | - | 人员名称 | 人员名称 |
| serviceManager | varchar(45) | YES | - | - | 人员编码,外部人员为空 | 人员编码,外部人员为空 |
| serviceManagerName | varchar(45) | YES | - | - | 人员名称 | 人员名称 |
| projectManager | varchar(45) | YES | - | - | 人员编码,外部人员为空 | 人员编码,外部人员为空 |
| projectManagerName | varchar(45) | YES | - | - | 人员名称 | 人员名称 |
| groupMember | varchar(45) | YES | - | - | 人员编码,外部人员为空 | 人员编码,外部人员为空 |
| groupMemberName | varchar(45) | YES | - | - | 人员名称 | 人员名称 |
| teamMemberCodes | text | YES | - | - | - | 业务含义待确认 |
| teamMemberNames | text | YES | - | - | - | 业务含义待确认 |
| shipmentStateName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| shipmentState | varchar(11) | YES | - | - | 项目发货状态 -1 已发货 1 未发货 2部分发货 | 项目发货状态 -1 已发货 1 未发货 2部分发货 |
| planStateName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| projectPlanState | varchar(10) | YES | - | - | 工程计划状态 | 工程计划状态 |
| executionStateName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| executionState | varchar(45) | YES | 5 | - | 实施状态 | 实施状态 |
| closeProcessStateName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| closeProcessState | varchar(45) | YES | 10 | - | 闭环流程状态 | 闭环流程状态 |
| projectStartTime | datetime | YES | - | - | 项目开始实施时间 | 项目开始实施时间 |
| projectCreateTime | datetime | YES | - | - | 记录数据创建时间 | 记录数据创建时间 |
| projectRefreshTime | datetime | YES | - | - | 项目相关数据最后编辑时间 | 项目相关数据最后编辑时间 |
| projectCloseTime | datetime | YES | - | - | 项目闭环时间点 | 项目闭环时间点 |
| ssfs | varchar(11) | YES | - | - | - | 业务含义待确认 |
| ssfsName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| partnerChannel | varchar(91) | YES | - | - | - | 业务含义待确认 |
| serviceChannel | varchar(45) | YES | - | - | - | 业务含义待确认 |
| agentChannel | varchar(45) | YES | - | - | - | 业务含义待确认 |
| warrantyStatus | binary(0) | YES | - | - | - | 业务含义待确认 |
| warrantyStatusName | binary(0) | YES | - | - | - | 业务含义待确认 |
| warrantyGrade | binary(0) | YES | - | - | - | 业务含义待确认 |
| warrantyGradeName | binary(0) | YES | - | - | - | 业务含义待确认 |
| wafService | binary(0) | YES | - | - | - | 业务含义待确认 |
| wafServiceName | binary(0) | YES | - | - | - | 业务含义待确认 |
| finalCustomerName | varchar(255) | YES | - | - | 最终客户名称 | 最终客户名称 |
| salesType | varchar(25) | YES | - | - | 销售类型 | 销售类型 |
| compName | varchar(64) | YES | - | - | 组织机构简写 | 组织机构简写 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|

---

---
### 9 tmp_tb_project_shipment -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 临时项目发货汇总表 |
| 数据量 | ~410536 行 |
| 数据大小 | 167.73 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectId | int(11) | NO | 0 | - | - | 业务含义待确认 |
| 项目名称 | varchar(246) | YES | - | - | - | 业务含义待确认 |
| 合同号 | mediumtext | YES | - | - | - | 业务含义待确认 |
| 办事处 | varchar(20) | YES | - | - | - | 业务含义待确认 |
| 实施方式 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| 项目类型 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| 重大项目级别 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| 销售代表 | varchar(45) | YES | - | - | - | 业务含义待确认 |
| 服务经理 | varchar(45) | YES | - | - | - | 业务含义待确认 |
| 项目开始时间 | datetime | YES | - | - | - | 业务含义待确认 |
| 实施状态 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| 流程状态 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| 发货状态 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| 最终客户单位 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| 发货合同号 | varchar(50) | YES | - | MUL | - | 业务含义待确认 |
| 序列号 | varchar(50) | YES | - | - | - | 设备序列号 |
| 产品编码 | varchar(25) | YES | - | - | - | 业务含义待确认 |
| 产品型号 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| 产品名称 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| 收件人 | mediumtext | YES | - | - | - | 业务含义待确认 |
| 快递单号 | mediumtext | YES | - | - | - | 业务含义待确认 |
| 发货时间 | datetime | YES | - | - | - | 业务含义待确认 |
| 快递公司 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| 安装地址 | mediumtext | YES | - | - | - | 业务含义待确认 |
| 序列号2 | varchar(50) | YES | - | - | - | 业务含义待确认 |
| 产品编码2 | varchar(25) | YES | - | - | - | 业务含义待确认 |
| 产品型号2 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| 产品名称2 | varchar(255) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | 发货合同号,序列号 |

---

---
### 10 tmp_tb_view_shipment_ems_4_pm -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 临时发货快递信息表（PM用） |
| 数据量 | ~92528 行 |
| 数据大小 | 8.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contract_code | varchar(25) | YES | - | MUL | - | 合同号 |
| receiveName | text | YES | - | - | 收件人 | 收件人 |
| emsNum | text | YES | - | - | 快递单号 | 快递单号 |
| packdate | datetime | YES | - | - | - | 发货日期 |
| emsCompany | mediumtext | YES | - | - | - | 快递公司 |
| packId | varchar(64) | YES | - | - | - | 装箱单ID |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | contract_code,packId |

---

---
### 11 tmp_tb_view_shipment_info_4_pm -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 临时发货明细信息表（PM用） |
| 数据量 | ~386195 行 |
| 数据大小 | 311.98 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contract_code | varchar(25) | YES | - | MUL | - | 合同号 |
| itemCode | varchar(16) | YES | - | - | - | 物料编码 |
| itemModel | varchar(255) | YES | - | - | - | 业务含义待确认 |
| itemName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| barcode | varchar(50) | YES | - | - | - | 设备序列号 |
| comBarcode | varchar(50) | YES | - | - | - | 业务含义待确认 |
| packId | varchar(64) | YES | - | - | - | 装箱单ID |
| itemCode2 | varchar(16) | YES | - | - | 母子公司发货物料编码对应关系 | 母子公司发货物料编码对应关系 |
| itemModel2 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| itemName2 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| barcode2 | varchar(50) | YES | - | - | 母子公司发货序列号对应关系 | 母子公司发货序列号对应关系 |
| profitCenter | varchar(32) | YES | - | - | 利润中心 | 利润中心 |
| projectId | int(11) | NO | 0 | - | 项目头信息主键,跟项目其他具体信息关联 | 项目头信息主键,跟项目其他具体信息关联 |
| projectCode | varchar(45) | NO | - | - | 项目名称 | 项目名称 |
| rank | varchar(255) | YES | - | - | - | 业务含义待确认 |
| projectName | varchar(246) | YES | - | - | - | 业务含义待确认 |
| majorProjectLevel | varchar(255) | YES | - | - | 重大项目级别 | 重大项目级别 |
| projectStateName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| projectState | varchar(11) | YES | - | - | 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态 | 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态 |
| contractNos | text | YES | - | - | - | 业务含义待确认 |
| systemName | varchar(255) | YES | - | - | 系统部ID | 系统部ID |
| compId | int(2) | YES | - | - | 公司ID | 公司ID |
| officeCode | varchar(255) | YES | - | - | 办事处编码 | 办事处编码 |
| officeName | varchar(20) | YES | - | - | - | 业务含义待确认 |
| salesManCode | varchar(45) | YES | - | - | 人员编码,外部人员为空 | 人员编码,外部人员为空 |
| salesManName | varchar(45) | YES | - | - | 人员名称 | 人员名称 |
| serviceManager | varchar(45) | YES | - | - | 人员编码,外部人员为空 | 人员编码,外部人员为空 |
| serviceManagerName | varchar(45) | YES | - | - | 人员名称 | 人员名称 |
| projectManager | varchar(45) | YES | - | - | 人员编码,外部人员为空 | 人员编码,外部人员为空 |
| projectManagerName | varchar(45) | YES | - | - | 人员名称 | 人员名称 |
| groupMember | varchar(45) | YES | - | - | 人员编码,外部人员为空 | 人员编码,外部人员为空 |
| groupMemberName | varchar(45) | YES | - | - | 人员名称 | 人员名称 |
| teamMemberCodes | text | YES | - | - | - | 业务含义待确认 |
| teamMemberNames | text | YES | - | - | - | 业务含义待确认 |
| shipmentStateName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| shipmentState | varchar(11) | YES | - | - | 项目发货状态 -1 已发货 1 未发货 2部分发货 | 项目发货状态 -1 已发货 1 未发货 2部分发货 |
| planStateName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| projectPlanState | varchar(10) | YES | - | - | 工程计划状态 | 工程计划状态 |
| executionStateName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| executionState | varchar(45) | YES | 5 | - | 实施状态 | 实施状态 |
| closeProcessStateName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| closeProcessState | varchar(45) | YES | 10 | - | 闭环流程状态 | 闭环流程状态 |
| projectStartTime | datetime | YES | - | - | 项目开始实施时间 | 项目开始实施时间 |
| projectCreateTime | datetime | YES | - | - | 记录数据创建时间 | 记录数据创建时间 |
| projectRefreshTime | datetime | YES | - | - | 项目相关数据最后编辑时间 | 项目相关数据最后编辑时间 |
| projectCloseTime | datetime | YES | - | - | 项目闭环时间点 | 项目闭环时间点 |
| ssfs | varchar(11) | YES | - | - | - | 业务含义待确认 |
| ssfsName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| partnerChannel | varchar(91) | YES | - | - | - | 业务含义待确认 |
| serviceChannel | varchar(45) | YES | - | - | - | 业务含义待确认 |
| agentChannel | varchar(45) | YES | - | - | - | 业务含义待确认 |
| warrantyStatus | binary(0) | YES | - | - | - | 业务含义待确认 |
| warrantyStatusName | binary(0) | YES | - | - | - | 业务含义待确认 |
| warrantyGrade | binary(0) | YES | - | - | - | 业务含义待确认 |
| warrantyGradeName | binary(0) | YES | - | - | - | 业务含义待确认 |
| wafService | binary(0) | YES | - | - | - | 业务含义待确认 |
| wafServiceName | binary(0) | YES | - | - | - | 业务含义待确认 |
| finalCustomerName | varchar(255) | YES | - | - | 最终客户名称 | 最终客户名称 |
| compName | varchar(64) | YES | - | - | 组织机构简写 | 组织机构简写 |
| smsProjectCode | varchar(45) | YES | - | - | 原SMS项目编码 | 原SMS项目编码 |
| contractNo | varchar(45) | YES | - | - | 合同号 | 合同号 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | contract_code,packId |

---

---


# 五、视图（VIEW）

> 数据库视图，提供对基础表的查询封装，简化业务查询逻辑。
> 视图字段的可空、默认值、约束、字段描述列以"-"填充（视图无物理约束）。

---

### 1 dp_v_spms_department

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | SPMS部门视图，从department表提取办事处编码、名称、参数化标识 |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ocrCode | varchar(25) | - | - | - | - | 办事处编码 |
| ocrName | varchar(25) | - | - | - | - | 办事处名称 |
| isparam | int(11) | - | - | - | - | 是否参数化 |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `dp_v_spms_department` AS (select `department`.`ocrCode` AS `ocrCode`,`department`.`ocrName` AS `ocrName`,`department`.`isparam` AS `isparam` from `department`)
```
---
### 2 dp_v_spms_item_basic_info

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | SPMS物料基本信息视图，从fb_items提取物料编码和名称 |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| itemCode | varchar(25) | - | - | - | - | 物料编码 |
| itemName | varchar(255) | - | - | - | - | 物料描述 |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `dp_v_spms_item_basic_info` AS (select `fb_items`.`item` AS `itemCode`,`fb_items`.`describe_` AS `itemName` from `fb_items`)
```
---
### 3 dp_v_spms_rma_remind

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | RMA提醒视图，查询昨日审批通过的RMA备件归还提醒 |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| spare_serialNum | varchar(50) | - | - | - | - | 备件序列号 |
| sheetID | varchar(25) | - | - | - | - | RMA申请单据代码 |
| back_type | varchar(50) | - | - | - | - | 退回类型 |
| item_name | varchar(255) | - | - | - | - | 物料名称 |
| project_name | varchar(255) | - | - | - | - | 项目名称 |
| problem_desc | text | - | - | - | - | 故障描述 |
| conk_out_time | varchar(25) | - | - | - | - | 故障时间 |
| approve_time | datetime | - | - | - | - | 审批时间 |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`g01339`@`10.102.0.201` SQL SECURITY DEFINER VIEW `dp_v_spms_rma_remind` AS select `t1`.`spare_serialNum` AS `spare_serialNum`,`t2`.`sheetID` AS `sheetID`,`t5`.`back_type` AS `back_type`,`t3`.`item_name` AS `item_name`,`t3`.`project_name` AS `project_name`,`t3`.`problem_desc` AS `problem_desc`,`t3`.`conk_out_time` AS `conk_out_time`,`t4`.`approve_time` AS `approve_time` from ((((`tx_info` `t1` join `rma_spare_info` `t3` on((`t1`.`tx_id` = `t3`.`tx_id`))) left join `rma_app_info` `t2` on((`t1`.`sheetID` = `t2`.`sheetID`))) left join `app_comment` `t4` on((`t2`.`sheetID` = `t4`.`sheetID`))) left join `back_type` `t5` on((`t2`.`back` = `t5`.`back`))) where (((to_days(now()) - to_days(`t4`.`approve_time`)) = 1) and (`t1`.`datastate` = 1) and (`t4`.`is_pass` = 1))
```
---
### 4 pm_order_data_from_sap

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | SAP订单数据视图（含来源区分），从pm_order_data_from_erp表查询 |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | - | - | - | - | - |
| orderNumber | varchar(25) | - | - | - | - | - |
| contractNo | varchar(50) | - | - | - | - | - |
| orderExecNumber | varchar(50) | - | - | - | - | - |
| orderCreateTime | datetime | - | - | - | - | - |
| customerRequireTime | datetime | - | - | - | - | - |
| customerCode | varchar(55) | - | - | - | - | - |
| customerName | varchar(255) | - | - | - | - | - |
| projectName | varchar(255) | - | - | - | - | - |
| orderComment | varchar(2048) | - | - | - | - | - |
| orderType | int(11) | - | - | - | - | - |
| compCode | varchar(25) | - | - | - | - | - |
| salesType | varchar(25) | - | - | - | - | - |
| source | varchar(25) | - | - | - | - | - |
| customInfo | json | - | - | - | - | - |
| syncTime | datetime | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `pm_order_data_from_sap` AS select `pod`.`id` AS `id`,`pod`.`orderNumber` AS `orderNumber`,`pod`.`contractNo` AS `contractNo`,`pod`.`orderExecNumber` AS `orderExecNumber`,`pod`.`orderCreateTime` AS `orderCreateTime`,`pod`.`customerRequireTime` AS `customerRequireTime`,`pod`.`customerCode` AS `customerCode`,`pod`.`customerName` AS `customerName`,`pod`.`projectName` AS `projectName`,`pod`.`orderComment` AS `orderComment`,`pod`.`orderType` AS `orderType`,`pod`.`compCode` AS `compCode`,`pod`.`salesType` AS `salesType`,`pod`.`source` AS `source`,`pod`.`customInfo` AS `customInfo`,`pod`.`syncTime` AS `syncTime` from `pm_order_data_from_erp` `pod`
```
---
### 5 pm_order_data_from_sap_source

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 |  |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | - | - | - | - | - |
| orderNumber | varchar(25) | - | - | - | - | - |
| contractNo | varchar(50) | - | - | - | - | - |
| orderExecNumber | varchar(50) | - | - | - | - | - |
| orderExecNumberShort | varchar(50) | - | - | - | - | - |
| orderCreateTime | datetime | - | - | - | - | - |
| customerRequireTime | datetime | - | - | - | - | - |
| customerCode | varchar(55) | - | - | - | - | - |
| customerName | varchar(255) | - | - | - | - | - |
| projectName | varchar(255) | - | - | - | - | - |
| orderComment | varchar(2048) | - | - | - | - | - |
| orderType | int(11) | - | - | - | - | - |
| compCode | varchar(25) | - | - | - | - | - |
| salesType | varchar(25) | - | - | - | - | - |
| source | varchar(25) | - | - | - | - | - |
| customInfo | json | - | - | - | - | - |
| syncTime | datetime | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `pm_order_data_from_sap_source` AS select `pol`.`id` AS `id`,`pol`.`orderNumber` AS `orderNumber`,`pol`.`contractNo` AS `contractNo`,`pol`.`orderExecNumber` AS `orderExecNumber`,`pol`.`orderExecNumberShort` AS `orderExecNumberShort`,`pol`.`orderCreateTime` AS `orderCreateTime`,`pol`.`customerRequireTime` AS `customerRequireTime`,`pol`.`customerCode` AS `customerCode`,`pol`.`customerName` AS `customerName`,`pol`.`projectName` AS `projectName`,`pol`.`orderComment` AS `orderComment`,`pol`.`orderType` AS `orderType`,`pol`.`compCode` AS `compCode`,`pol`.`salesType` AS `salesType`,`pol`.`source` AS `source`,`pol`.`customInfo` AS `customInfo`,`pol`.`syncTime` AS `syncTime` from `pm_order_data_from_erp_source` `pol`
```
---
### 6 pm_order_line_from_sap

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | SAP订单行视图（含来源区分），从pm_order_line_from_erp表查询 |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | - | - | - | - | - |
| orderNumber | varchar(25) | - | - | - | - | - |
| lineNum | varchar(25) | - | - | - | - | - |
| itemCode | varchar(25) | - | - | - | - | - |
| itemDesc | varchar(255) | - | - | - | - | - |
| orderQuantity | int(11) | - | - | - | - | - |
| openQuantity | int(11) | - | - | - | - | - |
| bundleCode | varchar(25) | - | - | - | - | - |
| warrantyMonth | int(11) | - | - | - | - | - |
| lineType | int(11) | - | - | - | - | - |
| compCode | varchar(25) | - | - | - | - | - |
| profitCenter | varchar(25) | - | - | - | - | - |
| realOrderExecNumber | varchar(25) | - | - | - | - | - |
| source | varchar(25) | - | - | - | - | - |
| customInfo | json | - | - | - | - | - |
| syncTime | datetime | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `pm_order_line_from_sap` AS select `pol`.`id` AS `id`,`pol`.`orderNumber` AS `orderNumber`,`pol`.`lineNum` AS `lineNum`,`pol`.`itemCode` AS `itemCode`,`pol`.`itemDesc` AS `itemDesc`,`pol`.`orderQuantity` AS `orderQuantity`,`pol`.`openQuantity` AS `openQuantity`,`pol`.`bundleCode` AS `bundleCode`,`pol`.`warrantyMonth` AS `warrantyMonth`,`pol`.`lineType` AS `lineType`,`pol`.`compCode` AS `compCode`,`pol`.`profitCenter` AS `profitCenter`,`pol`.`realOrderExecNumber` AS `realOrderExecNumber`,`pol`.`source` AS `source`,`pol`.`customInfo` AS `customInfo`,`pol`.`syncTime` AS `syncTime` from `pm_order_line_from_erp` `pol`
```
---
### 7 pm_order_line_from_sap_source

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 |  |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | - | - | - | - | - |
| orderNumber | varchar(25) | - | - | - | - | - |
| lineNum | varchar(25) | - | - | - | - | - |
| itemCode | varchar(25) | - | - | - | - | - |
| itemDesc | varchar(255) | - | - | - | - | - |
| orderQuantity | int(11) | - | - | - | - | - |
| openQuantity | int(11) | - | - | - | - | - |
| bundleCode | varchar(25) | - | - | - | - | - |
| warrantyMonth | int(11) | - | - | - | - | - |
| lineType | int(11) | - | - | - | - | - |
| compCode | varchar(25) | - | - | - | - | - |
| profitCenter | varchar(25) | - | - | - | - | - |
| realOrderExecNumber | varchar(25) | - | - | - | - | - |
| source | varchar(25) | - | - | - | - | - |
| customInfo | json | - | - | - | - | - |
| syncTime | datetime | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `pm_order_line_from_sap_source` AS select `pod`.`id` AS `id`,`pod`.`orderNumber` AS `orderNumber`,`pod`.`lineNum` AS `lineNum`,`pod`.`itemCode` AS `itemCode`,`pod`.`itemDesc` AS `itemDesc`,`pod`.`orderQuantity` AS `orderQuantity`,`pod`.`openQuantity` AS `openQuantity`,`pod`.`bundleCode` AS `bundleCode`,`pod`.`warrantyMonth` AS `warrantyMonth`,`pod`.`lineType` AS `lineType`,`pod`.`compCode` AS `compCode`,`pod`.`profitCenter` AS `profitCenter`,`pod`.`realOrderExecNumber` AS `realOrderExecNumber`,`pod`.`source` AS `source`,`pod`.`customInfo` AS `customInfo`,`pod`.`syncTime` AS `syncTime` from `pm_order_line_from_erp_source` `pod`
```
---
### 8 pm_project_header

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | 项目头视图，筛选projectType='10'的售后项目 |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectId | int(11) | - | - | - | - | - |
| projectType | varchar(45) | - | - | - | - | - |
| projectCode | varchar(45) | - | - | - | - | - |
| projectName | varchar(200) | - | - | - | - | - |
| projectState | varchar(11) | - | - | - | - | - |
| isback | varchar(11) | - | - | - | - | - |
| column001 | varchar(255) | - | - | - | - | - |
| column002 | varchar(255) | - | - | - | - | - |
| column003 | varchar(255) | - | - | - | - | - |
| column004 | varchar(255) | - | - | - | - | - |
| column005 | varchar(255) | - | - | - | - | - |
| column006 | varchar(255) | - | - | - | - | - |
| column007 | varchar(255) | - | - | - | - | - |
| column008 | varchar(255) | - | - | - | - | - |
| column009 | datetime | - | - | - | - | - |
| column010 | varchar(10) | - | - | - | - | - |
| column011 | varchar(10) | - | - | - | - | - |
| column012 | varchar(2) | - | - | - | - | - |
| columno12_readonly | int(2) | - | - | - | - | - |
| column013 | varchar(255) | - | - | - | - | - |
| column014 | text | - | - | - | - | - |
| customerProjectName | varchar(255) | - | - | - | - | - |
| salesType | varchar(25) | - | - | - | - | - |
| majorProjectLevel | varchar(255) | - | - | - | - | - |
| compId | int(2) | - | - | - | - | - |
| createTime | datetime | - | - | - | - | - |
| createBy | varchar(45) | - | - | - | - | - |
| updateTime | datetime | - | - | - | - | - |
| updateBy | varchar(45) | - | - | - | - | - |
| effectiveFrom | datetime | - | - | - | - | - |
| effectiveTo | datetime | - | - | - | - | - |
| disabled | bit(1) | - | - | - | - | - |
| projectStartTime | datetime | - | - | - | - | - |
| projectRefreshTime | datetime | - | - | - | - | - |
| projectCloseTime | datetime | - | - | - | - | - |
| customInfo | json | - | - | - | - | - |
| customConfig | json | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`g01339`@`10.102.0.201` SQL SECURITY DEFINER VIEW `pm_project_header` AS select `pm_project`.`projectId` AS `projectId`,`pm_project`.`projectType` AS `projectType`,`pm_project`.`projectCode` AS `projectCode`,`pm_project`.`projectName` AS `projectName`,`pm_project`.`projectState` AS `projectState`,`pm_project`.`isback` AS `isback`,`pm_project`.`column001` AS `column001`,`pm_project`.`column002` AS `column002`,`pm_project`.`column003` AS `column003`,`pm_project`.`column004` AS `column004`,`pm_project`.`column005` AS `column005`,`pm_project`.`column006` AS `column006`,`pm_project`.`column007` AS `column007`,`pm_project`.`column008` AS `column008`,`pm_project`.`column009` AS `column009`,`pm_project`.`column010` AS `column010`,`pm_project`.`column011` AS `column011`,`pm_project`.`column012` AS `column012`,`pm_project`.`columno12_readonly` AS `columno12_readonly`,`pm_project`.`column013` AS `column013`,`pm_project`.`column014` AS `column014`,`pm_project`.`customerProjectName` AS `customerProjectName`,`pm_project`.`salesType` AS `salesType`,`pm_project`.`majorProjectLevel` AS `majorProjectLevel`,`pm_project`.`compId` AS `compId`,`pm_project`.`createTime` AS `createTime`,`pm_project`.`createBy` AS `createBy`,`pm_project`.`updateTime` AS `updateTime`,`pm_project`.`updateBy` AS `updateBy`,`pm_project`.`effectiveFrom` AS `effectiveFrom`,`pm_project`.`effectiveTo` AS `effectiveTo`,`pm_project`.`disabled` AS `disabled`,`pm_project`.`projectStartTime` AS `projectStartTime`,`pm_project`.`projectRefreshTime` AS `projectRefreshTime`,`pm_project`.`projectCloseTime` AS `projectCloseTime`,`pm_project`.`customInfo` AS `customInfo`,`pm_project`.`customConfig` AS `customConfig` from `pm_project` where (`pm_project`.`projectType` = '10')
```
---
### 9 view_contract_collection_plan_4_crm

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | CRM合同回款计划视图，关联项目任务/合同/基础数据，查询回款事件及实际完成日期 |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contractNo | varchar(45) | - | - | - | - | - |
| referenceEventName | varchar(255) | - | - | - | - | - |
| referenceEvent | varchar(255) | - | - | - | - | - |
| eventPlanHappenDate | datetime | - | - | - | - | - |
| eventPlanHappenDateENG | datetime | - | - | - | - | - |
| eventActualFinishDate | datetime | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_contract_collection_plan_4_crm` AS select `pc`.`contractNo` AS `contractNo`,`bd`.`basicDataName` AS `referenceEventName`,`bd`.`basicDataAttri1` AS `referenceEvent`,`pt`.`eventPlanHappenDate` AS `eventPlanHappenDate`,`pt`.`eventPlanHappenDateENG` AS `eventPlanHappenDateENG`,min(`pt`.`eventActualFinishDate`) AS `eventActualFinishDate` from (((((`pm_project_task` `pt` left join `pm_project_header` `ph` on(((`pt`.`projectId` = `ph`.`projectId`) and (`pt`.`projectType` = `ph`.`projectType`) and isnull(`ph`.`effectiveTo`)))) left join `pm_project_group_relationship` `pgr` on((`pgr`.`projectCode` = `ph`.`projectCode`))) left join `pm_project_group` `pg` on(((`pg`.`projectGroupCode` = `pgr`.`projectGroupCode`) and (`pg`.`projectType` = `ph`.`projectType`)))) left join `pm_project_contract` `pc` on((`pc`.`projectGroupCode` = `pg`.`projectGroupCode`))) join `fnd_basic_data` `bd` on(((`bd`.`dataTypeCode` = 'crmReferenceEvent') and (`bd`.`basicDataId` = `pt`.`taskTypeId`)))) where (isnull(`pt`.`effectiveTo`) and isnull(`ph`.`effectiveTo`) and (`pt`.`eventPlanHappenDate` is not null) and (`pt`.`eventActualFinishDate` is not null) and (`pc`.`contractNo` is not null)) group by `pc`.`contractNo`,`referenceEvent`
```
---
### 10 view_current_task

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | 当前任务视图，查询每个项目当前待办的最小任务类型ID |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| taskTypeId | varchar(25) | - | - | - | - | - |
| projectId | int(11) | - | - | - | - | - |
| taskTypeCode | varchar(45) | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`g01339`@`10.102.0.201` SQL SECURITY DEFINER VIEW `view_current_task` AS (select min(`pm_project_task`.`taskTypeId`) AS `taskTypeId`,`pm_project_task`.`projectId` AS `projectId`,`pm_project_task`.`taskTypeCode` AS `taskTypeCode` from `pm_project_task` where ((`pm_project_task`.`effectiveFrom` < now()) and (isnull(`pm_project_task`.`effectiveTo`) or (`pm_project_task`.`effectiveTo` > now())) and isnull(`pm_project_task`.`eventActualFinishDate`)) group by `pm_project_task`.`projectId` order by min(`pm_project_task`.`taskTypeId`))
```
---
### 11 view_distinct_contract

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | 去重合同视图，按合同号分组取最新订单创建时间 |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contractNo | varchar(50) | - | - | - | - | - |
| orderCreateTime | datetime | - | - | - | - | - |
| id | int(11) | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`g01339`@`10.102.0.201` SQL SECURITY DEFINER VIEW `view_distinct_contract` AS (select `pm_order_data_from_sap`.`contractNo` AS `contractNo`,max(`pm_order_data_from_sap`.`orderCreateTime`) AS `orderCreateTime`,max(`pm_order_data_from_sap`.`id`) AS `id` from `pm_order_data_from_sap` where ((`pm_order_data_from_sap`.`contractNo` is not null) and (`pm_order_data_from_sap`.`orderType` = 0)) group by `pm_order_data_from_sap`.`contractNo`)
```
---
### 12 view_ehr_department

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | EHR部门/部门结构/员工视图，关联ehr_department/ehr_company/ehr_employee/ehr_job表，展开部门层级结构 |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| depID | int(11) | - | - | - | - | - |
| depCode | varchar(20) | - | - | - | - | - |
| depName | varchar(100) | - | - | - | - | - |
| depAbbr | varchar(100) | - | - | - | - | - |
| compID | int(11) | - | - | - | - | - |
| adminID | int(11) | - | - | - | - | - |
| depGrade | int(11) | - | - | - | - | - |
| depType | int(11) | - | - | - | - | - |
| depProperty | int(11) | - | - | - | - | - |
| depCost | int(11) | - | - | - | - | - |
| director | int(11) | - | - | - | - | - |
| director2 | int(11) | - | - | - | - | - |
| depEmp | int(11) | - | - | - | - | - |
| depNum | int(11) | - | - | - | - | - |
| effectDate | datetime | - | - | - | - | - |
| xOrder | varchar(20) | - | - | - | - | - |
| isDisabled | bit(1) | - | - | - | - | - |
| disabledDate | datetime | - | - | - | - | - |
| remark | varchar(500) | - | - | - | - | - |
| depCustom1 | int(11) | - | - | - | - | - |
| depCustom2 | int(11) | - | - | - | - | - |
| depCustom3 | int(11) | - | - | - | - | - |
| depCustom4 | int(11) | - | - | - | - | - |
| depCustom5 | int(11) | - | - | - | - | - |
| directorWorkNo | varchar(100) | - | - | - | - | - |
| directorName | varchar(200) | - | - | - | - | - |
| directorJobID | int(11) | - | - | - | - | - |
| directorWorkNo2 | varchar(100) | - | - | - | - | - |
| directorName2 | varchar(200) | - | - | - | - | - |
| directorJobID2 | int(11) | - | - | - | - | - |
| depLV1ID | bigint(11) | - | - | - | - | - |
| depLV1Code | varchar(20) | - | - | - | - | - |
| depLV1Name | varchar(100) | - | - | - | - | - |
| depLV2ID | bigint(11) | - | - | - | - | - |
| depLV2Code | varchar(20) | - | - | - | - | - |
| depLV2Name | varchar(100) | - | - | - | - | - |
| depLV3ID | bigint(11) | - | - | - | - | - |
| depLV3Code | varchar(20) | - | - | - | - | - |
| depLV3Name | varchar(100) | - | - | - | - | - |
| depAllName | varchar(304) | - | - | - | - | - |
| compName | varchar(100) | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`g01339`@`10.102.0.201` SQL SECURITY DEFINER VIEW `view_ehr_department` AS select `dep`.`depID` AS `depID`,`dep`.`depCode` AS `depCode`,`dep`.`depName` AS `depName`,`dep`.`depAbbr` AS `depAbbr`,`dep`.`compID` AS `compID`,`dep`.`adminID` AS `adminID`,`dep`.`depGrade` AS `depGrade`,`dep`.`depType` AS `depType`,`dep`.`depProperty` AS `depProperty`,`dep`.`depCost` AS `depCost`,`dep`.`director` AS `director`,`dep`.`director2` AS `director2`,`dep`.`depEmp` AS `depEmp`,`dep`.`depNum` AS `depNum`,`dep`.`effectDate` AS `effectDate`,`dep`.`xOrder` AS `xOrder`,`dep`.`isDisabled` AS `isDisabled`,`dep`.`disabledDate` AS `disabledDate`,`dep`.`remark` AS `remark`,`dep`.`depCustom1` AS `depCustom1`,`dep`.`depCustom2` AS `depCustom2`,`dep`.`depCustom3` AS `depCustom3`,`dep`.`depCustom4` AS `depCustom4`,`dep`.`depCustom5` AS `depCustom5`,`emp1`.`workNo` AS `directorWorkNo`,`emp1`.`name` AS `directorName`,`emp1`.`jobID` AS `directorJobID`,`emp2`.`workNo` AS `directorWorkNo2`,`emp2`.`name` AS `directorName2`,`emp2`.`jobID` AS `directorJobID2`,(case when (`dep`.`depGrade` = 1) then `dep`.`depID` when (`dep1`.`depGrade` = 1) then `dep1`.`depID` else `dep2`.`depID` end) AS `depLV1ID`,(case when (`dep`.`depGrade` = 1) then `dep`.`depCode` when (`dep1`.`depGrade` = 1) then `dep1`.`depCode` else `dep2`.`depCode` end) AS `depLV1Code`,(case when (`dep`.`depGrade` = 1) then `dep`.`depName` when (`dep1`.`depGrade` = 1) then `dep1`.`depName` else `dep2`.`depName` end) AS `depLV1Name`,(case when (`dep`.`depGrade` = 2) then `dep`.`depID` when (`dep1`.`depGrade` = 2) then `dep1`.`depID` else `dep2`.`depID` end) AS `depLV2ID`,(case when (`dep`.`depGrade` = 2) then `dep`.`depCode` when (`dep1`.`depGrade` = 2) then `dep1`.`depCode` else `dep2`.`depCode` end) AS `depLV2Code`,(case when (`dep`.`depGrade` = 2) then `dep`.`depName` when (`dep1`.`depGrade` = 2) then `dep1`.`depName` else `dep2`.`depName` end) AS `depLV2Name`,(case when (`dep`.`depGrade` = 3) then `dep`.`depID` when (`dep1`.`depGrade` = 3) then `dep1`.`depID` else `dep2`.`depID` end) AS `depLV3ID`,(case when (`dep`.`depGrade` = 3) then `dep`.`depCode` when (`dep1`.`depGrade` = 2) then `dep1`.`depCode` else `dep2`.`depCode` end) AS `depLV3Code`,(case when (`dep`.`depGrade` = 3) then `dep`.`depName` when (`dep1`.`depGrade` = 3) then `dep1`.`depName` else `dep2`.`depName` end) AS `depLV3Name`,(case when (`dep2`.`depName` is not null) then concat(`dep2`.`depName`,'--',`dep1`.`depName`,'--',`dep`.`depName`) when (`dep1`.`depName` is not null) then concat(`dep1`.`depName`,'--',`dep`.`depName`) else `dep`.`depName` end) AS `depAllName`,`comp`.`compName` AS `compName` from (((((`ehr_department` `dep` left join `ehr_company` `comp` on((`dep`.`compID` = `comp`.`compID`))) left join `ehr_employee` `emp1` on((`dep`.`director` = `emp1`.`empID`))) left join `ehr_employee` `emp2` on((`dep`.`director2` = `emp2`.`empID`))) left join `ehr_department` `dep1` on((`dep`.`adminID` = `dep1`.`depID`))) left join `ehr_department` `dep2` on((`dep1`.`adminID` = `dep2`.`depID`)))
```
---
### 13 view_ehr_department_struct

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 |  |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| depID | int(11) | - | - | - | - | - |
| depName | varchar(100) | - | - | - | - | - |
| depGrade | int(11) | - | - | - | - | - |
| depLV1ID | bigint(11) | - | - | - | - | - |
| depLV1Code | varchar(20) | - | - | - | - | - |
| depLV1Name | varchar(100) | - | - | - | - | - |
| depLV2ID | bigint(11) | - | - | - | - | - |
| depLV2Code | varchar(20) | - | - | - | - | - |
| depLV2Name | varchar(100) | - | - | - | - | - |
| depLV3ID | bigint(11) | - | - | - | - | - |
| depLV3Code | varchar(20) | - | - | - | - | - |
| depLV3Name | varchar(100) | - | - | - | - | - |
| depLV4ID | bigint(11) | - | - | - | - | - |
| depLV4Code | varchar(20) | - | - | - | - | - |
| depLV4Name | varchar(100) | - | - | - | - | - |
| depAllName | varchar(406) | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_ehr_department_struct` AS select `dep`.`depID` AS `depID`,`dep`.`depName` AS `depName`,`dep`.`depGrade` AS `depGrade`,(case when (`dep`.`depGrade` = 1) then `dep`.`depID` when (`dep1`.`depGrade` = 1) then `dep1`.`depID` when (`dep2`.`depGrade` = 1) then `dep2`.`depID` else `dep3`.`depID` end) AS `depLV1ID`,(case when (`dep`.`depGrade` = 1) then `dep`.`depCode` when (`dep1`.`depGrade` = 1) then `dep1`.`depCode` when (`dep2`.`depGrade` = 1) then `dep2`.`depCode` else `dep3`.`depCode` end) AS `depLV1Code`,(case when (`dep`.`depGrade` = 1) then `dep`.`depName` when (`dep1`.`depGrade` = 1) then `dep1`.`depName` when (`dep2`.`depGrade` = 1) then `dep2`.`depName` else `dep3`.`depName` end) AS `depLV1Name`,(case when (`dep`.`depGrade` = 2) then `dep`.`depID` when (`dep1`.`depGrade` = 2) then `dep1`.`depID` when (`dep2`.`depGrade` = 2) then `dep2`.`depID` else `dep3`.`depID` end) AS `depLV2ID`,(case when (`dep`.`depGrade` = 2) then `dep`.`depCode` when (`dep1`.`depGrade` = 2) then `dep1`.`depCode` when (`dep2`.`depGrade` = 2) then `dep2`.`depCode` else `dep3`.`depCode` end) AS `depLV2Code`,(case when (`dep`.`depGrade` = 2) then `dep`.`depName` when (`dep1`.`depGrade` = 2) then `dep1`.`depName` when (`dep2`.`depGrade` = 2) then `dep2`.`depName` else `dep3`.`depName` end) AS `depLV2Name`,(case when (`dep`.`depGrade` = 3) then `dep`.`depID` when (`dep1`.`depGrade` = 3) then `dep1`.`depID` when (`dep2`.`depGrade` = 3) then `dep2`.`depID` else `dep3`.`depID` end) AS `depLV3ID`,(case when (`dep`.`depGrade` = 3) then `dep`.`depCode` when (`dep1`.`depGrade` = 3) then `dep1`.`depCode` when (`dep2`.`depGrade` = 3) then `dep2`.`depCode` else `dep3`.`depCode` end) AS `depLV3Code`,(case when (`dep`.`depGrade` = 3) then `dep`.`depName` when (`dep1`.`depGrade` = 3) then `dep1`.`depName` when (`dep2`.`depGrade` = 3) then `dep2`.`depName` else `dep3`.`depName` end) AS `depLV3Name`,(case when (`dep`.`depGrade` = 4) then `dep`.`depID` when (`dep1`.`depGrade` = 4) then `dep1`.`depID` when (`dep2`.`depGrade` = 4) then `dep2`.`depID` else `dep3`.`depID` end) AS `depLV4ID`,(case when (`dep`.`depGrade` = 4) then `dep`.`depCode` when (`dep1`.`depGrade` = 4) then `dep1`.`depCode` when (`dep2`.`depGrade` = 4) then `dep2`.`depCode` else `dep3`.`depCode` end) AS `depLV4Code`,(case when (`dep`.`depGrade` = 4) then `dep`.`depName` when (`dep1`.`depGrade` = 4) then `dep1`.`depName` when (`dep2`.`depGrade` = 4) then `dep2`.`depName` else `dep3`.`depName` end) AS `depLV4Name`,(case when (`dep3`.`depName` is not null) then concat(`dep3`.`depName`,'--',`dep2`.`depName`,'--',`dep1`.`depName`,'--',`dep`.`depName`) when (`dep2`.`depName` is not null) then concat(`dep2`.`depName`,'--',`dep1`.`depName`,'--',`dep`.`depName`) when (`dep1`.`depName` is not null) then concat(`dep1`.`depName`,'--',`dep`.`depName`) else `dep`.`depName` end) AS `depAllName` from (((`ehr_department` `dep` left join `ehr_department` `dep1` on((`dep`.`adminID` = `dep1`.`depID`))) left join `ehr_department` `dep2` on((`dep1`.`adminID` = `dep2`.`depID`))) left join `ehr_department` `dep3` on((`dep2`.`adminID` = `dep3`.`depID`)))
```
---
### 14 view_ehr_employee

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 |  |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| empID | int(11) | - | - | - | - | - |
| workNo | varchar(100) | - | - | - | - | - |
| name | varchar(200) | - | - | - | - | - |
| eName | varchar(200) | - | - | - | - | - |
| compID | int(11) | - | - | - | - | - |
| depID | int(11) | - | - | - | - | - |
| jobID | int(11) | - | - | - | - | - |
| reportTo | int(11) | - | - | - | - | - |
| wfreportTo | int(11) | - | - | - | - | - |
| empStatus | int(11) | - | - | - | - | - |
| jobStatus | int(11) | - | - | - | - | - |
| empType | int(11) | - | - | - | - | - |
| joinDate | datetime | - | - | - | - | - |
| workBeginDate | datetime | - | - | - | - | - |
| jobBeginDate | datetime | - | - | - | - | - |
| pracBeginDate | datetime | - | - | - | - | - |
| pracEndDate | datetime | - | - | - | - | - |
| probBeginDate | datetime | - | - | - | - | - |
| probEndDate | datetime | - | - | - | - | - |
| leaveDate | datetime | - | - | - | - | - |
| gender | int(11) | - | - | - | - | - |
| email | varchar(500) | - | - | - | - | - |
| mobile | varchar(50) | - | - | - | - | - |
| officePhone | varchar(50) | - | - | - | - | - |
| remark | varchar(100) | - | - | - | - | - |
| disabled | int(11) | - | - | - | - | - |
| empCustom1 | int(11) | - | - | - | - | - |
| empCustom2 | int(11) | - | - | - | - | - |
| empCustom3 | int(11) | - | - | - | - | - |
| empCustom4 | varchar(50) | - | - | - | - | - |
| empCustom5 | int(11) | - | - | - | - | - |
| reportToWorkNo | varchar(100) | - | - | - | - | - |
| reportToName | varchar(200) | - | - | - | - | - |
| wfreportToWorkNo | varchar(100) | - | - | - | - | - |
| wfreportToName | varchar(200) | - | - | - | - | - |
| depGrade | int(11) | - | - | - | - | - |
| depCode | varchar(20) | - | - | - | - | - |
| depName | varchar(100) | - | - | - | - | - |
| jobCode | varchar(10) | - | - | - | - | - |
| jobName | varchar(100) | - | - | - | - | - |
| depLV1ID | bigint(11) | - | - | - | - | - |
| depLV1Code | varchar(20) | - | - | - | - | - |
| depLV1Name | varchar(100) | - | - | - | - | - |
| depLV2ID | bigint(11) | - | - | - | - | - |
| depLV2Code | varchar(20) | - | - | - | - | - |
| depLV2Name | varchar(100) | - | - | - | - | - |
| depLV3ID | bigint(11) | - | - | - | - | - |
| depLV3Code | varchar(20) | - | - | - | - | - |
| depLV3Name | varchar(100) | - | - | - | - | - |
| depAllName | varchar(304) | - | - | - | - | - |
| compName | varchar(100) | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`g01339`@`10.102.0.201` SQL SECURITY DEFINER VIEW `view_ehr_employee` AS select `emp`.`empID` AS `empID`,`emp`.`workNo` AS `workNo`,`emp`.`name` AS `name`,`emp`.`eName` AS `eName`,`emp`.`compID` AS `compID`,`emp`.`depID` AS `depID`,`emp`.`jobID` AS `jobID`,`emp`.`reportTo` AS `reportTo`,`emp`.`wfreportTo` AS `wfreportTo`,`emp`.`empStatus` AS `empStatus`,`emp`.`jobStatus` AS `jobStatus`,`emp`.`empType` AS `empType`,`emp`.`joinDate` AS `joinDate`,`emp`.`workBeginDate` AS `workBeginDate`,`emp`.`jobBeginDate` AS `jobBeginDate`,`emp`.`pracBeginDate` AS `pracBeginDate`,`emp`.`pracEndDate` AS `pracEndDate`,`emp`.`probBeginDate` AS `probBeginDate`,`emp`.`probEndDate` AS `probEndDate`,`emp`.`leaveDate` AS `leaveDate`,`emp`.`gender` AS `gender`,`emp`.`email` AS `email`,`emp`.`mobile` AS `mobile`,`emp`.`officePhone` AS `officePhone`,`emp`.`remark` AS `remark`,`emp`.`disabled` AS `disabled`,`emp`.`empCustom1` AS `empCustom1`,`emp`.`empCustom2` AS `empCustom2`,`emp`.`empCustom3` AS `empCustom3`,`emp`.`empCustom4` AS `empCustom4`,`emp`.`empCustom5` AS `empCustom5`,`emp1`.`workNo` AS `reportToWorkNo`,`emp1`.`name` AS `reportToName`,`emp2`.`workNo` AS `wfreportToWorkNo`,`emp2`.`name` AS `wfreportToName`,`dep`.`depGrade` AS `depGrade`,`dep`.`depCode` AS `depCode`,`dep`.`depName` AS `depName`,`job`.`jobCode` AS `jobCode`,`job`.`jobName` AS `jobName`,(case when (`dep`.`depGrade` = 1) then `dep`.`depID` when (`dep1`.`depGrade` = 1) then `dep1`.`depID` else `dep2`.`depID` end) AS `depLV1ID`,(case when (`dep`.`depGrade` = 1) then `dep`.`depCode` when (`dep1`.`depGrade` = 1) then `dep1`.`depCode` else `dep2`.`depCode` end) AS `depLV1Code`,(case when (`dep`.`depGrade` = 1) then `dep`.`depName` when (`dep1`.`depGrade` = 1) then `dep1`.`depName` else `dep2`.`depName` end) AS `depLV1Name`,(case when (`dep`.`depGrade` = 2) then `dep`.`depID` when (`dep1`.`depGrade` = 2) then `dep1`.`depID` else `dep2`.`depID` end) AS `depLV2ID`,(case when (`dep`.`depGrade` = 2) then `dep`.`depCode` when (`dep1`.`depGrade` = 2) then `dep1`.`depCode` else `dep2`.`depCode` end) AS `depLV2Code`,(case when (`dep`.`depGrade` = 2) then `dep`.`depName` when (`dep1`.`depGrade` = 2) then `dep1`.`depName` else `dep2`.`depName` end) AS `depLV2Name`,(case when (`dep`.`depGrade` = 3) then `dep`.`depID` when (`dep1`.`depGrade` = 3) then `dep1`.`depID` else `dep2`.`depID` end) AS `depLV3ID`,(case when (`dep`.`depGrade` = 3) then `dep`.`depCode` when (`dep1`.`depGrade` = 2) then `dep1`.`depCode` else `dep2`.`depCode` end) AS `depLV3Code`,(case when (`dep`.`depGrade` = 3) then `dep`.`depName` when (`dep1`.`depGrade` = 3) then `dep1`.`depName` else `dep2`.`depName` end) AS `depLV3Name`,(case when (`dep2`.`depName` is not null) then concat(`dep2`.`depName`,'--',`dep1`.`depName`,'--',`dep`.`depName`) when (`dep1`.`depName` is not null) then concat(`dep1`.`depName`,'--',`dep`.`depName`) else `dep`.`depName` end) AS `depAllName`,`comp`.`compName` AS `compName` from (((((((`ehr_employee` `emp` left join `ehr_department` `dep` on((`emp`.`depID` = `dep`.`depID`))) left join `ehr_department` `dep1` on((`dep1`.`depID` = `dep`.`adminID`))) left join `ehr_department` `dep2` on((`dep2`.`depID` = `dep1`.`adminID`))) left join `ehr_company` `comp` on((`emp`.`compID` = `comp`.`compID`))) left join `ehr_job` `job` on((`emp`.`jobID` = `job`.`jobID`))) left join `ehr_employee` `emp1` on((`emp`.`reportTo` = `emp1`.`empID`))) left join `ehr_employee` `emp2` on((`emp`.`wfreportTo` = `emp2`.`empID`)))
```
---
### 15 view_ems_info_4_pm

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | PM快递信息视图，关联合同和发货表获取快递信息 |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contract_code | varchar(25) | - | - | - | - | 合同号 |
| emsNum | mediumtext | - | - | - | - | 快递单号 |
| receiveName | mediumtext | - | - | - | - | 收件人 |
| emsCompany | mediumtext | - | - | - | - | 快递公司 |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`g01339`@`10.102.0.201` SQL SECURITY DEFINER VIEW `view_ems_info_4_pm` AS (select `c`.`contract_code` AS `contract_code`,(case when (`s`.`emsNum` = 'null') then NULL else `s`.`emsNum` end) AS `emsNum`,(case when (`s`.`receiveName` = 'null') then NULL else `s`.`receiveName` end) AS `receiveName`,(case when (`s`.`emsCompany` = 'null') then NULL else `s`.`emsCompany` end) AS `emsCompany` from (`fb_contract` `c` left join `fb_shipment` `s` on((`c`.`contract_id` = `s`.`con_id`))))
```
---
### 16 view_pm_deliverable_4_sms

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | SMS交付物视图，关联交付明细/项目交付/基础数据/用户信息 |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contractNo | varchar(25) | - | - | - | - | - |
| deliverableName | varchar(255) | - | - | - | - | - |
| deliverablePath | varchar(255) | - | - | - | - | - |
| smsDeliverType | varchar(255) | - | - | - | - | - |
| uploadUser | varchar(174) | - | - | - | - | - |
| uploadTime | datetime | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`g01339`@`10.102.0.201` SQL SECURITY DEFINER VIEW `view_pm_deliverable_4_sms` AS (select `a`.`contractNo` AS `contractNo`,`a`.`deliverableName` AS `deliverableName`,`a`.`deliverablePath` AS `deliverablePath`,`d`.`basicDataName` AS `smsDeliverType`,concat(`a`.`uploadUser`,'-',`e`.`realName`) AS `uploadUser`,`a`.`uploadTime` AS `uploadTime` from ((((`pm_basic_deliver_detail` `a` left join `pm_basic_prj_deliver` `b` on((`a`.`deliverId` = `b`.`id`))) left join `fnd_basic_data` `c` on(((`b`.`dataTypeCodeSon` = `c`.`dataTypeCode`) and (`b`.`basicDataIdSon` = `c`.`basicDataId`)))) join `fnd_basic_data` `d` on(((`d`.`dataTypeCode` = '19') and (`b`.`basicDataIdSon` = `d`.`basicDataId`)))) left join `fnd_user_info` `e` on((`e`.`username` = `a`.`uploadUser`))) where (`a`.`contractNo` is not null))
```
---
### 17 view_presales_project_duration

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | 售前项目时长视图，计算售前项目各阶段（申请/服务/方案/测试/回访/审批）的时长 |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| presalesId | int(11) | - | - | - | - | - |
| instId | varchar(64) | - | - | - | - | - |
| applyDuration | varchar(128) | - | - | - | - | - |
| totalDuration | varchar(216) | - | - | - | - | - |
| serviceDuration | varchar(216) | - | - | - | - | - |
| programDuration | varchar(216) | - | - | - | - | - |
| testDuration | varchar(216) | - | - | - | - | - |
| callbackDuration | varchar(216) | - | - | - | - | - |
| serviceApproveDuration | varchar(216) | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_presales_project_duration` AS select `t`.`presalesId` AS `presalesId`,`t`.`instId` AS `instId`,replace(replace(replace(replace(concat('已',truncate((`t`.`applyDuration` / 86400000),0),'天',truncate(((`t`.`applyDuration` - (truncate((`t`.`applyDuration` / 86400000),0) * 86400000)) / 3600000),0),'时',truncate(((`t`.`applyDuration` - (truncate((`t`.`applyDuration` / 3600000),0) * 3600000)) / 60000),0),'分',truncate(((`t`.`applyDuration` - (truncate((`t`.`applyDuration` / 60000),0) * 60000)) / 1000),0),'秒'),'已0天0时0分',''),'已0天0时',''),'已0天',''),'已','') AS `applyDuration`,replace(replace(replace(replace(ifnull(concat('已',truncate((`t`.`totalDuration` / 86400000),0),'天',truncate(((`t`.`totalDuration` - (truncate((`t`.`totalDuration` / 86400000),0) * 86400000)) / 3600000),0),'时',truncate(((`t`.`totalDuration` - (truncate((`t`.`totalDuration` / 3600000),0) * 3600000)) / 60000),0),'分',truncate(((`t`.`totalDuration` - (truncate((`t`.`totalDuration` / 60000),0) * 60000)) / 1000),0),'秒'),concat('已',truncate((`t`.`allDuration` / 86400000),0),'天',truncate(((`t`.`allDuration` - (truncate((`t`.`allDuration` / 86400000),0) * 86400000)) / 3600000),0),'时',truncate(((`t`.`allDuration` - (truncate((`t`.`allDuration` / 3600000),0) * 3600000)) / 60000),0),'分',truncate(((`t`.`allDuration` - (truncate((`t`.`allDuration` / 60000),0) * 60000)) / 1000),0),'秒')),'已0天0时0分',''),'已0天0时',''),'已0天',''),'已','') AS `totalDuration`,replace(replace(replace(replace(concat('已',truncate((`t`.`serviceDuration` / 86400000),0),'天',truncate(((`t`.`serviceDuration` - (truncate((`t`.`serviceDuration` / 86400000),0) * 86400000)) / 3600000),0),'时',truncate(((`t`.`serviceDuration` - (truncate((`t`.`serviceDuration` / 3600000),0) * 3600000)) / 60000),0),'分',truncate(((`t`.`serviceDuration` - (truncate((`t`.`serviceDuration` / 60000),0) * 60000)) / 1000),0),'秒'),'已0天0时0分',''),'已0天0时',''),'已0天',''),'已','') AS `serviceDuration`,replace(replace(replace(replace(concat('已',truncate((`t`.`programDuration` / 86400000),0),'天',truncate(((`t`.`programDuration` - (truncate((`t`.`programDuration` / 86400000),0) * 86400000)) / 3600000),0),'时',truncate(((`t`.`programDuration` - (truncate((`t`.`programDuration` / 3600000),0) * 3600000)) / 60000),0),'分',truncate(((`t`.`programDuration` - (truncate((`t`.`programDuration` / 60000),0) * 60000)) / 1000),0),'秒'),'已0天0时0分',''),'已0天0时',''),'已0天',''),'已','') AS `programDuration`,replace(replace(replace(replace(concat('已',truncate((`t`.`testDuration` / 86400000),0),'天',truncate(((`t`.`testDuration` - (truncate((`t`.`testDuration` / 86400000),0) * 86400000)) / 3600000),0),'时',truncate(((`t`.`testDuration` - (truncate((`t`.`testDuration` / 3600000),0) * 3600000)) / 60000),0),'分',truncate(((`t`.`testDuration` - (truncate((`t`.`testDuration` / 60000),0) * 60000)) / 1000),0),'秒'),'已0天0时0分',''),'已0天0时',''),'已0天',''),'已','') AS `testDuration`,replace(replace(replace(replace(concat('已',truncate((`t`.`callbackDuration` / 86400000),0),'天',truncate(((`t`.`callbackDuration` - (truncate((`t`.`callbackDuration` / 86400000),0) * 86400000)) / 3600000),0),'时',truncate(((`t`.`callbackDuration` - (truncate((`t`.`callbackDuration` / 3600000),0) * 3600000)) / 60000),0),'分',truncate(((`t`.`callbackDuration` - (truncate((`t`.`callbackDuration` / 60000),0) * 60000)) / 1000),0),'秒'),'已0天0时0分',''),'已0天0时',''),'已0天',''),'已','') AS `callbackDuration`,replace(replace(replace(replace(concat('已',truncate((`t`.`serviceApproveDuration` / 86400000),0),'天',truncate(((`t`.`serviceApproveDuration` - (truncate((`t`.`serviceApproveDuration` / 86400000),0) * 86400000)) / 3600000),0),'时',truncate(((`t`.`serviceApproveDuration` - (truncate((`t`.`serviceApproveDuration` / 3600000),0) * 3600000)) / 60000),0),'分',truncate(((`t`.`serviceApproveDuration` - (truncate((`t`.`serviceApproveDuration` / 60000),0) * 60000)) / 1000),0),'秒'),'已0天0时0分',''),'已0天0时',''),'已0天',''),'已','') AS `serviceApproveDuration` from `view_presales_project_duration_temp` `t`
```
---
### 18 view_presales_project_duration_temp

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 |  |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| presalesId | int(11) | - | - | - | - | - |
| instId | varchar(64) | - | - | - | - | - |
| applyDuration | bigint(25) | - | - | - | - | - |
| totalDuration | bigint(26) | - | - | - | - | - |
| allDuration | decimal(46,0) | - | - | - | - | - |
| serviceDuration | decimal(46,0) | - | - | - | - | - |
| programDuration | decimal(46,0) | - | - | - | - | - |
| testDuration | decimal(46,0) | - | - | - | - | - |
| callbackDuration | decimal(46,0) | - | - | - | - | - |
| serviceApproveDuration | decimal(46,0) | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_presales_project_duration_temp` AS select `pph`.`presalesId` AS `presalesId`,`pph`.`instId` AS `instId`,(timestampdiff(SECOND,if((cast(`pph`.`effectiveFrom` as date) = cast(`pph`.`applyTime` as date)),if((timestampdiff(MINUTE,cast(`pph`.`effectiveFrom` as date),`pph`.`effectiveFrom`) < ((8 * 60) + 30)),(cast(`pph`.`effectiveFrom` as date) + interval ((8 * 60) + 30) minute),`pph`.`effectiveFrom`),if((timestampdiff(MINUTE,cast(`pph`.`effectiveFrom` as date),`pph`.`effectiveFrom`) > (18 * 60)),(cast(`pph`.`effectiveFrom` as date) + interval ((32 * 60) + 30) minute),if((timestampdiff(MINUTE,cast(`pph`.`effectiveFrom` as date),`pph`.`effectiveFrom`) < ((8 * 60) + 30)),(cast(`pph`.`effectiveFrom` as date) + interval ((8 * 60) + 30) minute),`pph`.`effectiveFrom`))),`pph`.`applyTime`) * 1000) AS `applyDuration`,((if((`pph`.`applyTime` > `pph`.`endTime`),-(1),1) * timestampdiff(SECOND,`pph`.`applyTime`,ifnull(`pph`.`endTime`,now()))) * 1000) AS `totalDuration`,sum(ifnull(`aht`.`DURATION_`,(timestampdiff(SECOND,`aht`.`START_TIME_`,now()) * 1000))) AS `allDuration`,sum(if((`aht`.`TASK_DEF_KEY_` = 'usertask1'),ifnull(`aht`.`DURATION_`,(timestampdiff(SECOND,`aht`.`START_TIME_`,now()) * 1000)),NULL)) AS `serviceDuration`,sum(if((`aht`.`TASK_DEF_KEY_` = 'usertask2'),ifnull(`aht`.`DURATION_`,(timestampdiff(SECOND,`aht`.`START_TIME_`,now()) * 1000)),NULL)) AS `programDuration`,sum(if((`aht`.`TASK_DEF_KEY_` = 'usertask3'),ifnull(`aht`.`DURATION_`,(timestampdiff(SECOND,`aht`.`START_TIME_`,now()) * 1000)),NULL)) AS `testDuration`,sum(if((`aht`.`TASK_DEF_KEY_` = 'usertask4'),ifnull(`aht`.`DURATION_`,(timestampdiff(SECOND,`aht`.`START_TIME_`,now()) * 1000)),NULL)) AS `callbackDuration`,sum(if((`aht`.`TASK_DEF_KEY_` = 'serviceApprove'),ifnull(`aht`.`DURATION_`,(timestampdiff(SECOND,`aht`.`START_TIME_`,now()) * 1000)),NULL)) AS `serviceApproveDuration` from (`pm_presales_project_header` `pph` left join `act_hi_taskinst` `aht` on((`aht`.`PROC_INST_ID_` = `pph`.`instId`))) where (`pph`.`instId` is not null) group by `pph`.`presalesId`
```
---
### 19 view_prj_is_has_plan

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | 项目是否有计划视图，查询已制定计划的项目ID |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectId | int(11) | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`g01339`@`10.102.0.201` SQL SECURITY DEFINER VIEW `view_prj_is_has_plan` AS (select `pm_project_task`.`projectId` AS `projectId` from `pm_project_task` where ((`pm_project_task`.`effectiveFrom` <= now()) and (isnull(`pm_project_task`.`effectiveTo`) or (`pm_project_task`.`effectiveTo` > now()))) group by `pm_project_task`.`projectId`)
```
---
### 20 view_project_created_list

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | 项目创建列表/项目信息列表/项目等待列表视图，综合查询项目信息、合同、成员、当前任务等 |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectId | int(11) | - | - | - | - | - |
| projectCode | varchar(45) | - | - | - | - | - |
| rank | varchar(255) | - | - | - | - | - |
| projectName | varchar(246) | - | - | - | - | - |
| projectStateName | varchar(255) | - | - | - | - | - |
| projectState | varchar(11) | - | - | - | - | - |
| contractNo | text | - | - | - | - | - |
| officeCode | varchar(255) | - | - | - | - | - |
| officeName | varchar(20) | - | - | - | - | - |
| salesManCode | varchar(45) | - | - | - | - | - |
| salesManName | varchar(45) | - | - | - | - | - |
| orderCreateTime | datetime | - | - | - | - | - |
| serviceManager | varchar(45) | - | - | - | - | - |
| serviceManagerName | varchar(45) | - | - | - | - | - |
| projectManager | varchar(45) | - | - | - | - | - |
| projectManagerName | varchar(45) | - | - | - | - | - |
| currentTask | varchar(255) | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`g01339`@`10.102.0.201` SQL SECURITY DEFINER VIEW `view_project_created_list` AS (select `ph`.`projectId` AS `projectId`,`ph`.`projectCode` AS `projectCode`,`rank`.`basicDataName` AS `rank`,(case when (`pr`.`mergeBranchMark` is not null) then concat(`ph`.`projectName`,'-',`pr`.`mergeBranchMark`) else `ph`.`projectName` end) AS `projectName`,`fd`.`basicDataName` AS `projectStateName`,`ph`.`projectState` AS `projectState`,group_concat(`pc`.`contractNo` separator ',') AS `contractNo`,`ph`.`column001` AS `officeCode`,`d`.`departmentName` AS `officeName`,`pm`.`memberCode` AS `salesManCode`,`pm`.`memberName` AS `salesManName`,`pm3`.`createTime` AS `orderCreateTime`,`pm2`.`memberCode` AS `serviceManager`,`pm2`.`memberName` AS `serviceManagerName`,`pm3`.`memberCode` AS `projectManager`,`pm3`.`memberName` AS `projectManagerName`,(case when (`plan`.`projectId` is not null) then (case when isnull(`t`.`projectId`) then (case when (`ph`.`projectState` = '100') then '项目闭环' else '闭环申请' end) else `task`.`basicDataName` end) else '尚未制定计划' end) AS `currentTask` from ((((((((((((`pm_project_header` `ph` left join `pm_project_group_relationship` `pr` on((`ph`.`projectCode` = `pr`.`projectCode`))) left join `pm_project_contract` `pc` on((`pr`.`projectGroupCode` = `pc`.`projectGroupCode`))) left join `pm_project_group` `pg` on(((`pr`.`projectGroupCode` = `pg`.`projectGroupCode`) and (`pg`.`projectType` = '10')))) left join `fnd_basic_data` `fd` on(((`fd`.`basicDataId` = `ph`.`projectState`) and (`fd`.`dataTypeCode` = '02') and (`fd`.`effectiveFrom` < now()) and ((`fd`.`effectiveTo` > now()) or isnull(`fd`.`effectiveTo`))))) left join `fnd_basic_data` `rank` on(((`ph`.`column010` = `rank`.`basicDataId`) and (`rank`.`dataTypeCode` = '05') and (`rank`.`effectiveFrom` <= now()) and ((`rank`.`effectiveTo` > now()) or isnull(`rank`.`effectiveTo`))))) left join `fnd_department` `d` on((`d`.`departmentNum` = `ph`.`column001`))) left join `pm_project_member` `pm` on(((`ph`.`projectId` = `pm`.`projectId`) and (`pm`.`memberRole` = '10') and (`pm`.`effectiveFrom` < now()) and ((`pm`.`effectiveTo` > now()) or isnull(`pm`.`effectiveTo`))))) left join `pm_project_member` `pm2` on(((`ph`.`projectId` = `pm2`.`projectId`) and (`pm2`.`memberRole` = '20') and (`pm2`.`effectiveFrom` < now()) and ((`pm2`.`effectiveTo` > now()) or isnull(`pm2`.`effectiveTo`))))) left join `pm_project_member` `pm3` on(((`ph`.`projectId` = `pm3`.`projectId`) and (`pm3`.`memberRole` = '30') and (`pm3`.`effectiveFrom` < now()) and ((`pm3`.`effectiveTo` > now()) or isnull(`pm3`.`effectiveTo`))))) left join `view_current_task` `t` on((`t`.`projectId` = `ph`.`projectId`))) left join `fnd_basic_data` `task` on(((`t`.`taskTypeId` = `task`.`basicDataId`) and (`task`.`dataTypeCode` = `t`.`taskTypeCode`)))) left join `view_prj_is_has_plan` `plan` on((`ph`.`projectId` = `plan`.`projectId`))) where ((`ph`.`projectState` is not null) and (`ph`.`effectiveFrom` <= now()) and ((`ph`.`effectiveTo` > now()) or isnull(`ph`.`effectiveTo`))) group by `pc`.`projectGroupCode`,`ph`.`projectCode`)
```
---
### 21 view_project_info_4_ts

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | TS项目信息视图，联合查询售后项目和售前项目的综合信息 |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectCode | varchar(64) | - | - | - | - | - |
| projectName | varchar(255) | - | - | - | - | - |
| contractNo | varchar(341) | - | - | - | - | - |
| officeName | varchar(20) | - | - | - | - | - |
| customerName | varchar(255) | - | - | - | - | - |
| marketName | varchar(255) | - | - | - | - | - |
| systemName | varchar(255) | - | - | - | - | - |
| expendName | varchar(255) | - | - | - | - | - |
| industryName | varchar(255) | - | - | - | - | - |
| salesManCode | varchar(45) | - | - | - | - | - |
| salesManName | varchar(68) | - | - | - | - | - |
| salesManTel | varchar(45) | - | - | - | - | - |
| salesManMail | varchar(100) | - | - | - | - | - |
| smCode | varchar(45) | - | - | - | - | - |
| smName | varchar(45) | - | - | - | - | - |
| pmCode1 | varchar(45) | - | - | - | - | - |
| pmName1 | varchar(45) | - | - | - | - | - |
| pmCode2 | varchar(45) | - | - | - | - | - |
| pmName2 | varchar(45) | - | - | - | - | - |
| compId | int(11) | - | - | - | - | - |
| compName | varchar(128) | - | - | - | - | - |
| ssfsName | varchar(255) | - | - | - | - | - |
| partnerChannel | varchar(45) | - | - | - | - | - |
| projectType | varchar(4) | - | - | - | - | - |
| finalCustomerName | varchar(255) | - | - | - | - | - |
| customerProjectName | varchar(255) | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_project_info_4_ts` AS select `ph`.`projectCode` AS `projectCode`,`ph`.`projectName` AS `projectName`,group_concat(distinct `pc`.`contractNo` separator ',') AS `contractNo`,`fd`.`departmentName` AS `officeName`,`ph`.`column003` AS `customerName`,`ph`.`column004` AS `marketName`,`ph`.`column005` AS `systemName`,`ph`.`column006` AS `expendName`,`ph`.`column007` AS `industryName`,ifnull(`s`.`memberCode`,`oa`.`salesmanCode`) AS `salesManCode`,ifnull(`s`.`memberName`,`oa`.`salesmanName`) AS `salesManName`,ifnull(`s`.`phoneNum`,`oa`.`salesmanTel`) AS `salesManTel`,ifnull(`s`.`email`,`oa`.`salesmanMail`) AS `salesManMail`,`sm`.`memberCode` AS `smCode`,`sm`.`memberName` AS `smName`,`pm1`.`memberCode` AS `pmCode1`,`pm1`.`memberName` AS `pmName1`,`pm2`.`memberCode` AS `pmCode2`,`pm2`.`memberName` AS `pmName2`,`ph`.`compId` AS `compId`,`c`.`name` AS `compName`,(case when (isnull(`ph`.`column012`) and (`ph`.`columno12_readonly` = -(1))) then '未指定' when (`ph`.`column012` is not null) then `ssfs1`.`basicDataName` else `ssfs2`.`basicDataName` end) AS `ssfsName`,(case when ((case when (isnull(`ph`.`column012`) and (`ph`.`columno12_readonly` = -(1))) then `ph`.`column012` when (`ph`.`column012` is not null) then `ph`.`column012` else `ph`.`columno12_readonly` end) in (0,4)) then `prp1`.`partyName` else (case when ((case when (isnull(`ph`.`column012`) and (`ph`.`columno12_readonly` = -(1))) then `ph`.`column012` when (`ph`.`column012` is not null) then `ph`.`column012` else `ph`.`columno12_readonly` end) in (1,3)) then `prp2`.`partyName` else NULL end) end) AS `partnerChannel`,'售后项目' AS `projectType`,`ph`.`column013` AS `finalCustomerName`,`ph`.`customerProjectName` AS `customerProjectName` from ((((((((((((((`pm_project_header` `ph` left join `pm_project_group_relationship` `pgr` on((`ph`.`projectCode` = `pgr`.`projectCode`))) left join `pm_project_contract` `pc` on((`pgr`.`projectGroupCode` = `pc`.`projectGroupCode`))) left join `pm_project_member` `s` on(((`s`.`projectId` = `ph`.`projectId`) and (`s`.`memberRole` = '10') and isnull(`s`.`effectiveTo`)))) left join `pm_project_member` `sm` on(((`sm`.`projectId` = `ph`.`projectId`) and (`sm`.`memberRole` = '20') and isnull(`sm`.`effectiveTo`)))) left join `pm_project_member` `pm1` on(((`pm1`.`projectId` = `ph`.`projectId`) and (`pm1`.`memberRole` = '30') and isnull(`pm1`.`effectiveTo`) and (`pm1`.`fromFlag` = 1)))) left join `pm_project_member` `pm2` on(((`pm2`.`projectId` = `ph`.`projectId`) and (`pm2`.`memberRole` = '30') and isnull(`pm2`.`effectiveTo`) and (`pm2`.`fromFlag` = 2)))) left join `fnd_department` `fd` on((`fd`.`departmentNum` = `ph`.`column001`))) left join `pm_project_property_from_sms` `ppfs` on((`ppfs`.`projectCode` = `pgr`.`smsProjectCode`))) left join `pm_project_related_party` `prp1` on(((`prp1`.`partyRole` = '20') and (`prp1`.`projectId` = `ph`.`projectId`) and isnull(`prp1`.`effectiveTo`)))) left join `pm_project_related_party` `prp2` on(((`prp2`.`partyRole` = '30') and (`prp2`.`projectId` = `ph`.`projectId`) and isnull(`prp2`.`effectiveTo`)))) left join `fnd_basic_data` `ssfs1` on(((`ssfs1`.`dataTypeCode` = '15') and (`ssfs1`.`basicDataId` = `ph`.`column012`) and isnull(`ssfs1`.`effectiveTo`)))) left join `fnd_basic_data` `ssfs2` on(((`ssfs2`.`dataTypeCode` = '15') and (`ssfs2`.`basicDataId` = `ph`.`columno12_readonly`) and isnull(`ssfs2`.`effectiveTo`)))) left join `pm_person_from_oa` `oa` on((`oa`.`salesmanCode` = right(`ppfs`.`salesManCode`,5)))) left join `fnd_company` `c` on((`c`.`id` = `ph`.`compId`))) where isnull(`ph`.`effectiveTo`) group by `ph`.`projectCode` union select `ph`.`projectCode` AS `projectCode`,`ph`.`projectName` AS `projectName`,`ph`.`projectCode` AS `contractNo`,`fd`.`departmentName` AS `officeName`,NULL AS `customerName`,`ph`.`marketName` AS `marketName`,`ph`.`systemName` AS `systemName`,`ph`.`expendName` AS `expendName`,`ph`.`industryName` AS `industryName`,ifnull(`oa`.`salesmanCode`,substr(`ph`.`salesman`,2,5)) AS `salesManCode`,ifnull(`oa`.`salesmanName`,substr(`ph`.`salesman`,8)) AS `salesManName`,ifnull(`s`.`phoneNum`,`oa`.`salesmanTel`) AS `salesManTel`,ifnull(`s`.`email`,`oa`.`salesmanMail`) AS `salesManMail`,`sm`.`memberCode` AS `smCode`,`sm`.`memberName` AS `smName`,`pm1`.`memberCode` AS `pmCode1`,`pm1`.`memberName` AS `pmName1`,`pm2`.`memberCode` AS `pmCode2`,`pm2`.`memberName` AS `pmName2`,NULL AS `compId`,NULL AS `compName`,NULL AS `ssfsName`,NULL AS `partnerChannel`,'售前测试' AS `projectType`,NULL AS `finalCustomerName`,NULL AS `customerProjectName` from ((((((`pm_presales_project_header` `ph` left join `fnd_department` `fd` on((`fd`.`departmentNum` = `ph`.`officeCode`))) left join `pm_project_member` `s` on(((`s`.`projectId` = `ph`.`presalesId`) and (`s`.`memberRole` = '10') and isnull(`s`.`effectiveTo`)))) left join `pm_project_member` `sm` on(((`sm`.`projectId` = `ph`.`presalesId`) and (`sm`.`memberRole` = '20') and isnull(`sm`.`effectiveTo`)))) left join `pm_project_member` `pm1` on(((`pm1`.`projectId` = `ph`.`presalesId`) and (`pm1`.`memberRole` = '30') and isnull(`pm1`.`effectiveTo`) and (`pm1`.`fromFlag` = 1)))) left join `pm_project_member` `pm2` on(((`pm2`.`projectId` = `ph`.`presalesId`) and (`pm2`.`memberRole` = '30') and isnull(`pm2`.`effectiveTo`) and (`pm2`.`fromFlag` = 2)))) left join `pm_person_from_oa` `oa` on((`oa`.`salesmanCode` = substr(`ph`.`salesman`,2,5)))) where (`ph`.`projectCode` > '')
```
---
### 22 view_project_info_list

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 |  |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectId | int(11) | - | - | - | - | - |
| projectCode | varchar(45) | - | - | - | - | - |
| rank | varchar(255) | - | - | - | - | - |
| projectName | varchar(255) | - | - | - | - | - |
| projectStateName | varchar(255) | - | - | - | - | - |
| projectState | varchar(255) | - | - | - | - | - |
| contractNo | varchar(341) | - | - | - | - | - |
| officeCode | varchar(255) | - | - | - | - | - |
| officeName | varchar(20) | - | - | - | - | - |
| salesManCode | varchar(45) | - | - | - | - | - |
| salesManName | varchar(45) | - | - | - | - | - |
| orderCreateTime | datetime | - | - | - | - | - |
| systemName | varchar(255) | - | - | - | - | - |
| serviceManager | varchar(45) | - | - | - | - | - |
| serviceManagerName | varchar(45) | - | - | - | - | - |
| projectManager | varchar(45) | - | - | - | - | - |
| projectManagerName | varchar(45) | - | - | - | - | - |
| currentTask | varchar(255) | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`g01339`@`10.102.0.201` SQL SECURITY DEFINER VIEW `view_project_info_list` AS select `ph`.`projectId` AS `projectId`,`ph`.`projectCode` AS `projectCode`,`rank`.`basicDataName` AS `rank`,(case when (`pr`.`mergeBranchMark` is not null) then concat(`ph`.`projectName`,'-',`pr`.`mergeBranchMark`) else `ph`.`projectName` end) AS `projectName`,`fd`.`basicDataName` AS `projectStateName`,`ph`.`projectState` AS `projectState`,group_concat(`pc`.`contractNo` separator ',') AS `contractNo`,`ph`.`column001` AS `officeCode`,`d`.`departmentName` AS `officeName`,`pm`.`memberCode` AS `salesManCode`,`pm`.`memberName` AS `salesManName`,`pm3`.`createTime` AS `orderCreateTime`,`ph`.`column005` AS `systemName`,`pm2`.`memberCode` AS `serviceManager`,`pm2`.`memberName` AS `serviceManagerName`,`pm3`.`memberCode` AS `projectManager`,`pm3`.`memberName` AS `projectManagerName`,(case when (`plan`.`projectId` is not null) then (case when isnull(`t`.`projectId`) then (case when (`ph`.`projectState` = '100') then '项目闭环' else '闭环申请' end) else `task`.`basicDataName` end) else '尚未制定计划' end) AS `currentTask` from ((((((((((((`pm_project_header` `ph` left join `pm_project_group_relationship` `pr` on((`ph`.`projectCode` = `pr`.`projectCode`))) left join `pm_project_contract` `pc` on((`pr`.`projectGroupCode` = `pc`.`projectGroupCode`))) left join `pm_project_group` `pg` on(((`pr`.`projectGroupCode` = `pg`.`projectGroupCode`) and (`pg`.`projectType` = '10')))) left join `fnd_basic_data` `fd` on(((`fd`.`basicDataId` = `ph`.`projectState`) and (`fd`.`dataTypeCode` = '02') and (`fd`.`effectiveFrom` < now()) and ((`fd`.`effectiveTo` > now()) or isnull(`fd`.`effectiveTo`))))) left join `fnd_basic_data` `rank` on(((`ph`.`column010` = `rank`.`basicDataId`) and (`rank`.`dataTypeCode` = '05') and (`rank`.`effectiveFrom` <= now()) and ((`rank`.`effectiveTo` > now()) or isnull(`rank`.`effectiveTo`))))) left join `fnd_department` `d` on((`d`.`departmentNum` = `ph`.`column001`))) left join `pm_project_member` `pm` on(((`ph`.`projectId` = `pm`.`projectId`) and (`pm`.`memberRole` = '10') and (`pm`.`effectiveFrom` < now()) and ((`pm`.`effectiveTo` > now()) or isnull(`pm`.`effectiveTo`))))) left join `pm_project_member` `pm2` on(((`ph`.`projectId` = `pm2`.`projectId`) and (`pm2`.`memberRole` = '20') and (`pm2`.`effectiveFrom` < now()) and ((`pm2`.`effectiveTo` > now()) or isnull(`pm2`.`effectiveTo`))))) left join `pm_project_member` `pm3` on(((`ph`.`projectId` = `pm3`.`projectId`) and (`pm3`.`memberRole` = '30') and (`pm3`.`effectiveFrom` < now()) and ((`pm3`.`effectiveTo` > now()) or isnull(`pm3`.`effectiveTo`))))) left join `view_current_task` `t` on((`t`.`projectId` = `ph`.`projectId`))) left join `fnd_basic_data` `task` on(((`t`.`taskTypeId` = `task`.`basicDataId`) and (`task`.`dataTypeCode` = `t`.`taskTypeCode`)))) left join `view_prj_is_has_plan` `plan` on((`ph`.`projectId` = `plan`.`projectId`))) where ((`ph`.`projectState` is not null) and (`ph`.`effectiveFrom` <= now()) and ((`ph`.`effectiveTo` > now()) or isnull(`ph`.`effectiveTo`))) group by `pc`.`projectGroupCode`,`ph`.`projectCode` union all select distinct NULL AS `projectId`,NULL AS `projectCode`,NULL AS `rank`,`p`.`projectName` AS `projectName`,`fd`.`basicDataName` AS `projectStateName`,`fd`.`basicDataId` AS `projectState`,`p`.`contractNo` AS `contractNo`,`pp`.`officeCode` AS `officeCode`,`d`.`departmentName` AS `officeName`,`pp`.`salesManCode` AS `salesManCode`,`pp`.`salesManName` AS `salesManName`,`v`.`orderCreateTime` AS `orderCreateTime`,`pp`.`systemName` AS `systemName`,NULL AS `serviceManager`,NULL AS `serviceManagerName`,NULL AS `projectManager`,NULL AS `projectManagerName`,NULL AS `currentTask` from (`view_distinct_contract` `v` left join (((`pm_order_data_from_sap` `p` left join `pm_project_property_from_sms` `pp` on((`p`.`orderExecNumber` = `pp`.`orderExecNumber`))) left join `fnd_department` `d` on((`pp`.`officeCode` = `d`.`departmentNum`))) left join `fnd_basic_data` `fd` on(((`fd`.`basicDataId` = '10') and (`fd`.`dataTypeCode` = '02') and (`fd`.`effectiveFrom` < now()) and ((`fd`.`effectiveTo` > now()) or isnull(`fd`.`effectiveTo`))))) on((`v`.`id` = `p`.`id`))) where (not(exists(select 1 from ((`pm_project_contract` `t1` join `pm_project_group_relationship` `t2` on((`t1`.`projectGroupCode` = `t2`.`projectGroupCode`))) join `pm_project_header` `t3` on(((`t2`.`projectCode` = `t3`.`projectCode`) and (`t3`.`effectiveFrom` <= now()) and ((`t3`.`effectiveTo` > now()) or isnull(`t3`.`effectiveTo`))))) where (`p`.`contractNo` = `t1`.`contractNo`)))) order by `orderCreateTime` desc
```
---
### 23 view_project_maintenance_4_ts

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | TS项目维护视图，映射pm_project_maintenance_view |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | - | - | - | - | - |
| projectId | int(11) | - | - | - | - | - |
| projectCode | varchar(45) | - | - | - | - | - |
| projectName | varchar(200) | - | - | - | - | - |
| projectType | int(11) | - | - | - | - | - |
| projectExecutionState | varchar(45) | - | - | - | - | - |
| contractNo | varchar(255) | - | - | - | - | - |
| officeCode | varchar(25) | - | - | - | - | - |
| type | varchar(45) | - | - | - | - | - |
| category | varchar(45) | - | - | - | - | - |
| subCategory | varchar(45) | - | - | - | - | - |
| processTime | datetime | - | - | - | - | - |
| processDesc | varchar(1024) | - | - | - | - | - |
| processStep | varchar(1024) | - | - | - | - | - |
| remainProblem | varchar(1024) | - | - | - | - | - |
| transitHour | float | - | - | - | - | - |
| processHour | float | - | - | - | - | - |
| itemModel | varchar(255) | - | - | - | - | - |
| softVersion | varchar(255) | - | - | - | - | - |
| enabledFeatures | varchar(255) | - | - | - | - | - |
| customTos | varchar(512) | - | - | - | - | - |
| customCcs | varchar(512) | - | - | - | - | - |
| hasReport | bit(1) | - | - | - | - | - |
| quesnaireId | int(11) | - | - | - | - | - |
| deliverFileIds | varchar(255) | - | - | - | - | - |
| warrantyStatus | varchar(25) | - | - | - | - | - |
| industryName | varchar(25) | - | - | - | - | - |
| userOffice | varchar(25) | - | - | - | - | - |
| remark | varchar(2048) | - | - | - | - | - |
| createTime | datetime | - | - | - | - | - |
| createBy | varchar(45) | - | - | - | - | - |
| updateTime | datetime | - | - | - | - | - |
| updateBy | varchar(45) | - | - | - | - | - |
| officeName | varchar(20) | - | - | - | - | - |
| userOfficeName | varchar(20) | - | - | - | - | - |
| serviceManager | varchar(45) | - | - | - | - | - |
| programManagerA | varchar(45) | - | - | - | - | - |
| programManagerB | varchar(45) | - | - | - | - | - |
| createUser | varchar(174) | - | - | - | - | - |
| typeName | varchar(255) | - | - | - | - | - |
| projectExecutionStateName | varchar(255) | - | - | - | - | - |
| categoryName | varchar(258) | - | - | - | - | - |
| subCategoryName | varchar(255) | - | - | - | - | - |
| marketName | varchar(255) | - | - | - | - | - |
| systemName | varchar(255) | - | - | - | - | - |
| expendName | varchar(255) | - | - | - | - | - |
| industryNameN | varchar(255) | - | - | - | - | - |
| finalCustomerName | varchar(255) | - | - | - | - | - |
| salerName | varchar(91) | - | - | - | - | - |
| quesnaireResultHeaderId | int(11) | - | - | - | - | - |
| 工程师技术能力 | longtext | - | - | - | - | - |
| 服务水平及规范性 | longtext | - | - | - | - | - |
| 服务及时性 | longtext | - | - | - | - | - |
| warrantyStatusName | varchar(4) | - | - | - | - | - |
| syncTime | datetime | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`g01339`@`10.102.0.201` SQL SECURITY DEFINER VIEW `view_project_maintenance_4_ts` AS select `pm_project_maintenance_view`.`id` AS `id`,`pm_project_maintenance_view`.`projectId` AS `projectId`,`pm_project_maintenance_view`.`projectCode` AS `projectCode`,`pm_project_maintenance_view`.`projectName` AS `projectName`,`pm_project_maintenance_view`.`projectType` AS `projectType`,`pm_project_maintenance_view`.`projectExecutionState` AS `projectExecutionState`,`pm_project_maintenance_view`.`contractNo` AS `contractNo`,`pm_project_maintenance_view`.`officeCode` AS `officeCode`,`pm_project_maintenance_view`.`type` AS `type`,`pm_project_maintenance_view`.`category` AS `category`,`pm_project_maintenance_view`.`subCategory` AS `subCategory`,`pm_project_maintenance_view`.`processTime` AS `processTime`,`pm_project_maintenance_view`.`processDesc` AS `processDesc`,`pm_project_maintenance_view`.`processStep` AS `processStep`,`pm_project_maintenance_view`.`remainProblem` AS `remainProblem`,`pm_project_maintenance_view`.`transitHour` AS `transitHour`,`pm_project_maintenance_view`.`processHour` AS `processHour`,`pm_project_maintenance_view`.`itemModel` AS `itemModel`,`pm_project_maintenance_view`.`softVersion` AS `softVersion`,`pm_project_maintenance_view`.`enabledFeatures` AS `enabledFeatures`,`pm_project_maintenance_view`.`customTos` AS `customTos`,`pm_project_maintenance_view`.`customCcs` AS `customCcs`,`pm_project_maintenance_view`.`hasReport` AS `hasReport`,`pm_project_maintenance_view`.`quesnaireId` AS `quesnaireId`,`pm_project_maintenance_view`.`deliverFileIds` AS `deliverFileIds`,`pm_project_maintenance_view`.`warrantyStatus` AS `warrantyStatus`,`pm_project_maintenance_view`.`industryName` AS `industryName`,`pm_project_maintenance_view`.`userOffice` AS `userOffice`,`pm_project_maintenance_view`.`remark` AS `remark`,`pm_project_maintenance_view`.`createTime` AS `createTime`,`pm_project_maintenance_view`.`createBy` AS `createBy`,`pm_project_maintenance_view`.`updateTime` AS `updateTime`,`pm_project_maintenance_view`.`updateBy` AS `updateBy`,`pm_project_maintenance_view`.`officeName` AS `officeName`,`pm_project_maintenance_view`.`userOfficeName` AS `userOfficeName`,`pm_project_maintenance_view`.`serviceManager` AS `serviceManager`,`pm_project_maintenance_view`.`programManagerA` AS `programManagerA`,`pm_project_maintenance_view`.`programManagerB` AS `programManagerB`,`pm_project_maintenance_view`.`createUser` AS `createUser`,`pm_project_maintenance_view`.`typeName` AS `typeName`,`pm_project_maintenance_view`.`projectExecutionStateName` AS `projectExecutionStateName`,`pm_project_maintenance_view`.`categoryName` AS `categoryName`,`pm_project_maintenance_view`.`subCategoryName` AS `subCategoryName`,`pm_project_maintenance_view`.`marketName` AS `marketName`,`pm_project_maintenance_view`.`systemName` AS `systemName`,`pm_project_maintenance_view`.`expendName` AS `expendName`,`pm_project_maintenance_view`.`industryNameN` AS `industryNameN`,`pm_project_maintenance_view`.`finalCustomerName` AS `finalCustomerName`,`pm_project_maintenance_view`.`salerName` AS `salerName`,`pm_project_maintenance_view`.`quesnaireResultHeaderId` AS `quesnaireResultHeaderId`,`pm_project_maintenance_view`.`工程师技术能力` AS `工程师技术能力`,`pm_project_maintenance_view`.`服务水平及规范性` AS `服务水平及规范性`,`pm_project_maintenance_view`.`服务及时性` AS `服务及时性`,`pm_project_maintenance_view`.`warrantyStatusName` AS `warrantyStatusName`,`pm_project_maintenance_view`.`syncTime` AS `syncTime` from `pm_project_maintenance_view`
```
---
### 24 view_project_shipment_4_license

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | 授权项目发货视图，查询5301开头的设备发货信息 |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| barcode | varchar(50) | - | - | - | - | - |
| contract_code | varchar(25) | - | - | - | - | - |
| contract_type | varchar(25) | - | - | - | - | - |
| project_num | varchar(25) | - | - | - | - | - |
| project_name | varchar(512) | - | - | - | - | - |
| custom_name | varchar(512) | - | - | - | - | - |
| final_customer | varchar(512) | - | - | - | - | - |
| office_name | varchar(25) | - | - | - | - | - |
| delivery_time | bigint(11) | - | - | - | - | - |
| order_num | varchar(32) | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`dpdba`@`172.31.0.130` SQL SECURITY DEFINER VIEW `view_project_shipment_4_license` AS select distinct `sb`.`barcode` AS `barcode`,`c`.`contract_code` AS `contract_code`,`st`.`resolveName` AS `contract_type`,ifnull(`ppfs`.`projectCode`,`c`.`contract_code`) AS `project_num`,ifnull(`ppfs`.`projectName`,`c`.`project_name`) AS `project_name`,`c`.`customer_name` AS `custom_name`,ifnull(`ppfs`.`finalCustomerName`,`c`.`customer_name`) AS `final_customer`,`d`.`ocrName` AS `office_name`,unix_timestamp(`s`.`packdate`) AS `delivery_time`,`sb`.`orderNumber` AS `order_num` from ((((((((((select `sb`.`barcode` AS `barcode`,max(`s`.`packdate`) AS `packdate`,max(ifnull(`sb`.`orderNumber`,0)) AS `orderNumber` from (`dpspms`.`fb_shipment_barcode` `sb` left join `dpspms`.`fb_shipment` `s` on((`s`.`packlist_id` = `sb`.`pack_id`))) where ((`sb`.`barcode` is not null) and ((`sb`.`barcode` like '5301%') or (`sb`.`barcode` like '995301%')) and (isnull(`sb`.`rma_no`) or (`sb`.`rma_no` = '') or (`sb`.`rma_no` = 'null'))) group by `sb`.`barcode` order by NULL)) `msb` left join `dpspms`.`fb_shipment_barcode` `sb` on(((`msb`.`barcode` = `sb`.`barcode`) and (`msb`.`orderNumber` = ifnull(`sb`.`orderNumber`,0))))) left join `dpspms`.`fb_shipment` `s` on(((`s`.`packlist_id` = `sb`.`pack_id`) and (`msb`.`packdate` = `s`.`packdate`)))) left join `dpspms`.`fb_contract` `c` on((`c`.`contract_id` = `s`.`con_id`))) left join `dpspms`.`pm_project_contract` `pc` on((`pc`.`contractNo` = `c`.`contract_code`))) left join `dpspms`.`pm_project_group_relationship` `pgr` on((`pgr`.`projectGroupCode` = `pc`.`projectGroupCode`))) left join (select `ppfs`.`id` AS `id`,`ppfs`.`orderExecNumber` AS `orderExecNumber`,`ppfs`.`projectCode` AS `projectCode`,`ppfs`.`projectName` AS `projectName`,`ppfs`.`salesManCode` AS `salesManCode`,`ppfs`.`salesManName` AS `salesManName`,`ppfs`.`marketCode` AS `marketCode`,`ppfs`.`marketName` AS `marketName`,`ppfs`.`systemId` AS `systemId`,`ppfs`.`systemName` AS `systemName`,`ppfs`.`expendId` AS `expendId`,`ppfs`.`expendName` AS `expendName`,`ppfs`.`industryId` AS `industryId`,`ppfs`.`industryName` AS `industryName`,`ppfs`.`officeCode` AS `officeCode`,`ppfs`.`officeName` AS `officeName`,`ppfs`.`serviceTypeName` AS `serviceTypeName`,`ppfs`.`channelName` AS `channelName`,`ppfs`.`engineeFee` AS `engineeFee`,`ppfs`.`objId` AS `objId`,`ppfs`.`applyType` AS `applyType`,`ppfs`.`corporationCode` AS `corporationCode`,`ppfs`.`customerProjectName` AS `customerProjectName`,`ppfs`.`finalCustomerName` AS `finalCustomerName`,`ppfs`.`agentName` AS `agentName`,`ppfs`.`projectMoney` AS `projectMoney`,`ppfs`.`submitTime` AS `submitTime`,`ppfs`.`majorProjectLevel` AS `majorProjectLevel`,`ppfs`.`predBidDate` AS `predBidDate`,`ppfs`.`linkmanName` AS `linkmanName`,`ppfs`.`linkmanTel` AS `linkmanTel` from (`dpspms`.`pm_project_property_from_sms` `ppfs` join (select `dpspms`.`pm_project_property_from_sms`.`projectCode` AS `projectCode`,max(`dpspms`.`pm_project_property_from_sms`.`orderExecNumber`) AS `orderExecNumber` from `dpspms`.`pm_project_property_from_sms` group by `dpspms`.`pm_project_property_from_sms`.`projectCode`) `mppfs` on(((`mppfs`.`projectCode` = `ppfs`.`projectCode`) and (`mppfs`.`orderExecNumber` = `ppfs`.`orderExecNumber`))))) `ppfs` on((`ppfs`.`projectCode` = `pgr`.`smsProjectCode`))) left join `dpspms`.`department` `d` on((`d`.`ocrCode` = `c`.`office_code`))) left join `dpspms`.`sys_state_or_type` `st` on(((`st`.`resolveCode` = `c`.`contract_type`) and (`st`.`stCode` = '01')))) where ((`sb`.`barcode` is not null) and ((`sb`.`barcode` like '5301%') or (`sb`.`barcode` like '995301%')) and (isnull(`sb`.`rma_no`) or (`sb`.`rma_no` = '') or (`sb`.`rma_no` = 'null')))
```
---
### 25 view_project_task_4_oss

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | OSS项目任务视图/默认任务视图，查询项目任务节点信息 |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| taskId | int(11) | - | - | - | - | - |
| projectCode | varchar(45) | - | - | - | - | - |
| executeId | binary(0) | - | - | - | - | - |
| contractId | varchar(45) | - | - | - | - | - |
| nodeTypeCode | varchar(25) | - | - | - | - | - |
| nodeBeginTime | datetime | - | - | - | - | - |
| nodeEndTime | datetime | - | - | - | - | - |
| dataUpdateTime | binary(0) | - | - | - | - | - |
| nodeAttached | varchar(11) | - | - | - | - | - |
| nodeRemark | varchar(255) | - | - | - | - | - |
| updateTime | datetime | - | - | - | - | - |
| effectiveTo | datetime | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`g01339`@`10.102.0.201` SQL SECURITY DEFINER VIEW `view_project_task_4_oss` AS select distinct `pt`.`taskId` AS `taskId`,`ph`.`projectCode` AS `projectCode`,NULL AS `executeId`,`pc`.`contractNo` AS `contractId`,`pt`.`taskTypeId` AS `nodeTypeCode`,`pt`.`eventPlanHappenDateENG` AS `nodeBeginTime`,`pt`.`eventActualFinishDate` AS `nodeEndTime`,NULL AS `dataUpdateTime`,`ph`.`projectState` AS `nodeAttached`,`ph`.`column008` AS `nodeRemark`,`pt`.`updateTime` AS `updateTime`,(case when (`ph`.`effectiveTo` is not null) then `ph`.`effectiveTo` else `pt`.`effectiveTo` end) AS `effectiveTo` from (`pm_project_task` `pt` left join (((`pm_project_contract` `pc` left join `pm_project_group` `pg` on((`pc`.`projectGroupCode` = `pg`.`projectGroupCode`))) left join `pm_project_group_relationship` `pgr` on((`pg`.`projectGroupCode` = `pgr`.`projectGroupCode`))) left join `pm_project_header` `ph` on((`ph`.`projectCode` = `pgr`.`projectCode`))) on(((`ph`.`projectId` = `pt`.`projectId`) and (`pc`.`contractNo` = `pt`.`contractNo`))))
```
---
### 26 view_project_task_default_4_oss

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 |  |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| taskId | binary(0) | - | - | - | - | - |
| projectCode | varchar(45) | - | - | - | - | - |
| executeId | binary(0) | - | - | - | - | - |
| contractId | varchar(45) | - | - | - | - | - |
| nodeTypeCode | varchar(11) | - | - | - | - | - |
| nodeBeginTime | binary(0) | - | - | - | - | - |
| nodeEndTime | binary(0) | - | - | - | - | - |
| dataUpdateTime | binary(0) | - | - | - | - | - |
| nodeAttached | varchar(11) | - | - | - | - | - |
| nodeRemark | varchar(255) | - | - | - | - | - |
| updateTime | binary(0) | - | - | - | - | - |
| effectiveTo | binary(0) | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`g01339`@`10.102.0.201` SQL SECURITY DEFINER VIEW `view_project_task_default_4_oss` AS select distinct NULL AS `taskId`,`ph`.`projectCode` AS `projectCode`,NULL AS `executeId`,`pc`.`contractNo` AS `contractId`,`fbp`.`basicDataId` AS `nodeTypeCode`,NULL AS `nodeBeginTime`,NULL AS `nodeEndTime`,NULL AS `dataUpdateTime`,`ph`.`projectState` AS `nodeAttached`,`ph`.`column008` AS `nodeRemark`,NULL AS `updateTime`,NULL AS `effectiveTo` from (((((`pm_project_contract` `pc` left join `pm_project_group` `pg` on((`pc`.`projectGroupCode` = `pg`.`projectGroupCode`))) left join `pm_project_group_relationship` `pgr` on((`pg`.`projectGroupCode` = `pgr`.`projectGroupCode`))) left join `pm_project_header` `ph` on((`ph`.`projectCode` = `pgr`.`projectCode`))) left join `fnd_basic_prjstate` `fbp` on(((`ph`.`column010` = `fbp`.`column010`) and (`ph`.`column011` = `fbp`.`column011`)))) left join `fnd_basic_data` `fbd` on(((`fbd`.`dataTypeCode` = `fbp`.`dataTypeCode`) and (`fbd`.`basicDataId` = `fbp`.`basicDataId`)))) where ((`fbd`.`effectiveFrom` <= now()) and ((`fbd`.`effectiveTo` > now()) or isnull(`fbd`.`effectiveTo`)) and (not(`ph`.`projectId` in (select `pt`.`projectId` from `pm_project_task` `pt` where isnull(`pt`.`effectiveTo`)))))
```
---
### 27 view_project_waiting_list

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 |  |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectName | varchar(255) | - | - | - | - | - |
| projectStateName | varchar(255) | - | - | - | - | - |
| projectState | varchar(255) | - | - | - | - | - |
| contractNo | varchar(50) | - | - | - | - | - |
| officeCode | varchar(15) | - | - | - | - | - |
| officeName | varchar(20) | - | - | - | - | - |
| salesManCode | varchar(45) | - | - | - | - | - |
| salesManName | varchar(45) | - | - | - | - | - |
| orderCreateTime | datetime | - | - | - | - | - |
| systemName | varchar(255) | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`g01339`@`10.102.0.201` SQL SECURITY DEFINER VIEW `view_project_waiting_list` AS (select distinct `p`.`projectName` AS `projectName`,`fd`.`basicDataName` AS `projectStateName`,`fd`.`basicDataId` AS `projectState`,`p`.`contractNo` AS `contractNo`,`pp`.`officeCode` AS `officeCode`,`d`.`departmentName` AS `officeName`,`pp`.`salesManCode` AS `salesManCode`,`pp`.`salesManName` AS `salesManName`,`v`.`orderCreateTime` AS `orderCreateTime`,`pp`.`systemName` AS `systemName` from (`view_distinct_contract` `v` left join (((`pm_order_data_from_sap` `p` left join `pm_project_property_from_sms` `pp` on((`p`.`orderExecNumber` = `pp`.`orderExecNumber`))) left join `fnd_department` `d` on((`pp`.`officeCode` = `d`.`departmentNum`))) left join `fnd_basic_data` `fd` on(((`fd`.`basicDataId` = '10') and (`fd`.`dataTypeCode` = '02') and (`fd`.`effectiveFrom` < now()) and ((`fd`.`effectiveTo` > now()) or isnull(`fd`.`effectiveTo`))))) on((`v`.`id` = `p`.`id`))) where (not(exists(select 1 from ((`pm_project_contract` `t1` join `pm_project_group_relationship` `t2` on((`t1`.`projectGroupCode` = `t2`.`projectGroupCode`))) join `pm_project_header` `t3` on(((`t2`.`projectCode` = `t3`.`projectCode`) and (`t3`.`effectiveFrom` <= now()) and ((`t3`.`effectiveTo` > now()) or isnull(`t3`.`effectiveTo`))))) where (`p`.`contractNo` = `t1`.`contractNo`)))) order by `v`.`orderCreateTime` desc)
```
---
### 28 view_relation4contractno_marketcode

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | 合同号与市场编码关系视图，关联SAP订单和SMS项目属性 |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contractNo | varchar(50) | - | - | - | - | - |
| marketCode | varchar(64) | - | - | - | - | - |
| marketName | varchar(255) | - | - | - | - | - |
| systemId | varchar(64) | - | - | - | - | - |
| systemName | varchar(255) | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`g01339`@`10.102.0.201` SQL SECURITY DEFINER VIEW `view_relation4contractno_marketcode` AS select distinct `sap`.`contractNo` AS `contractNo`,`sms`.`marketCode` AS `marketCode`,`sms`.`marketName` AS `marketName`,`sms`.`systemId` AS `systemId`,`sms`.`systemName` AS `systemName` from (`pm_order_data_from_sap` `sap` join `pm_project_property_from_sms` `sms` on((`sms`.`orderExecNumber` = `sap`.`orderExecNumber`)))
```
---
### 29 view_rma_txinfo

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | RMA物流信息视图，关联tx_info/rma_spare_info/rma_app_info/app_comment/user_info |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| sheetID | varchar(25) | - | - | - | - | - |
| spare_serialNum | varchar(50) | - | - | - | - | - |
| instead_of_num | varchar(25) | - | - | - | - | - |
| item_code | varchar(15) | - | - | - | - | - |
| item_name | varchar(255) | - | - | - | - | - |
| customer_name | varchar(255) | - | - | - | - | - |
| contractNo | varchar(25) | - | - | - | - | - |
| contractRemark | varchar(4096) | - | - | - | - | - |
| project_name | varchar(255) | - | - | - | - | - |
| back | varchar(10) | - | - | - | - | - |
| serve | varchar(10) | - | - | - | - | - |
| tain | varchar(10) | - | - | - | - | - |
| data_state | char(1) | - | - | - | - | - |
| department | varchar(50) | - | - | - | - | - |
| application_time | datetime | - | - | - | - | - |
| approve_time | datetime | - | - | - | - | - |
| is_pass | varchar(2) | - | - | - | - | - |
| applicant | varchar(10) | - | - | - | - | - |
| EMS_num | varchar(255) | - | - | - | - | - |
| EMS_company | varchar(255) | - | - | - | - | - |
| addressee | varchar(25) | - | - | - | - | - |
| send_time | datetime | - | - | - | - | - |
| isBack | char(1) | - | - | - | - | - |
| back_time | datetime | - | - | - | - | - |
| doa_path | varchar(100) | - | - | - | - | - |
| check_path | varchar(100) | - | - | - | - | - |
| duty_person | varchar(10) | - | - | - | - | - |
| isOK | char(1) | - | - | - | - | - |
| hexiao_time | datetime | - | - | - | - | - |
| problem_desc | text | - | - | - | - | - |
| tx_id | int(11) | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`g01339`@`10.102.0.201` SQL SECURITY DEFINER VIEW `view_rma_txinfo` AS (select `rai`.`sheetID` AS `sheetID`,`ti`.`spare_serialNum` AS `spare_serialNum`,`ti`.`instead_of_num` AS `instead_of_num`,`rsi`.`item_code` AS `item_code`,`rsi`.`item_name` AS `item_name`,`rai`.`customer_name` AS `customer_name`,`rsi`.`contractNo` AS `contractNo`,`rsi`.`contractRemark` AS `contractRemark`,`rsi`.`project_name` AS `project_name`,`rai`.`back` AS `back`,`rai`.`serve` AS `serve`,`rai`.`tain` AS `tain`,`rai`.`data_state` AS `data_state`,(case when isnull(`rai`.`officeCode`) then `ui`.`department` else `rai`.`officeCode` end) AS `department`,`rai`.`application_time` AS `application_time`,`ac`.`approve_time` AS `approve_time`,`ac`.`is_pass` AS `is_pass`,`rai`.`applicant` AS `applicant`,`ti`.`EMS_num` AS `EMS_num`,`ti`.`EMS_company` AS `EMS_company`,`ti`.`addressee` AS `addressee`,`ti`.`send_time` AS `send_time`,`rsi`.`isBack` AS `isBack`,`rsi`.`back_time` AS `back_time`,`rsi`.`doa_path` AS `doa_path`,`rsi`.`check_path` AS `check_path`,`rai`.`duty_person` AS `duty_person`,`rsi`.`isOK` AS `isOK`,`rsi`.`hexiao_time` AS `hexiao_time`,`rsi`.`problem_desc` AS `problem_desc`,`ti`.`tx_id` AS `tx_id` from ((((`tx_info` `ti` join `rma_spare_info` `rsi`) join `rma_app_info` `rai`) join `app_comment` `ac`) join `user_info` `ui`) where ((`ti`.`tx_id` = `rsi`.`tx_id`) and (`ti`.`sheetID` = `rai`.`sheetID`) and (`rai`.`sheetID` = `ac`.`sheetID`) and (`rai`.`applicant` = `ui`.`username`)))
```
---
### 30 view_service

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | 维保服务视图/最大结束日期视图，查询每个序列号最新的维保服务记录 |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | varchar(64) | - | - | - | - | - |
| barcode | varchar(50) | - | - | - | - | - |
| end_date | datetime | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`g01339`@`10.102.0.201` SQL SECURITY DEFINER VIEW `view_service` AS (select `s`.`id` AS `id`,`s`.`barcode` AS `barcode`,`s`.`end_date` AS `end_date` from (`fb_service` `s` join `view_service_max` `t` on(((`s`.`barcode` = `t`.`barcode`) and (`t`.`maxEndDate` = `s`.`end_date`)))))
```
---
### 31 view_service_max

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 |  |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| barcode | varchar(50) | - | - | - | - | - |
| maxEndDate | datetime | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`g01339`@`10.102.0.201` SQL SECURITY DEFINER VIEW `view_service_max` AS (select `fb_service`.`barcode` AS `barcode`,max(`fb_service`.`end_date`) AS `maxEndDate` from `fb_service` group by `fb_service`.`barcode`)
```
---
### 32 view_shipment_4_sms

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | SMS发货视图，查询销售合同（contract_type=0）的发货条码信息 |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contract_code | varchar(25) | - | - | - | - | - |
| itemCode | varchar(16) | - | - | - | - | - |
| barcode | varchar(50) | - | - | - | - | - |
| itemCode2 | varchar(16) | - | - | - | - | - |
| barcode2 | varchar(50) | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`g01339`@`10.102.0.201` SQL SECURITY DEFINER VIEW `view_shipment_4_sms` AS select `c`.`contract_code` AS `contract_code`,`sb`.`item` AS `itemCode`,`sb`.`barcode` AS `barcode`,`sb`.`item2` AS `itemCode2`,`sb`.`barcode2` AS `barcode2` from ((`fb_contract` `c` left join `fb_shipment` `s` on((`c`.`contract_id` = `s`.`con_id`))) left join `fb_shipment_barcode` `sb` on((`s`.`packlist_id` = `sb`.`pack_id`))) where ((((`sb`.`barcode` is not null) and isnull(`sb`.`rma_no`)) or (`sb`.`rma_no` = '') or (`sb`.`rma_no` = 'null')) and (`c`.`contract_type` = 0))
```
---
### 33 view_shipment_ems_4_pm

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | PM发货快递视图/发货明细视图，关联合同/发货/条码/物料表 |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contract_code | varchar(25) | - | - | - | - | - |
| receiveName | text | - | - | - | - | - |
| emsNum | text | - | - | - | - | - |
| packdate | datetime | - | - | - | - | - |
| emsCompany | mediumtext | - | - | - | - | - |
| packId | varchar(64) | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`g01339`@`10.102.0.201` SQL SECURITY DEFINER VIEW `view_shipment_ems_4_pm` AS (select `fc`.`contract_code` AS `contract_code`,`fs`.`receiveName` AS `receiveName`,`fs`.`emsNum` AS `emsNum`,`fs`.`packdate` AS `packdate`,(case when isnull(`fd`.`basicDataName`) then `fs`.`emsCompany` else `fd`.`basicDataName` end) AS `emsCompany`,`fs`.`packlist_id` AS `packId` from ((`fb_contract` `fc` left join `fb_shipment` `fs` on((`fc`.`contract_id` = `fs`.`con_id`))) left join `fnd_basic_data` `fd` on(((`fs`.`emsCompany` = `fd`.`basicDataId`) and (`fd`.`dataTypeCode` = '01')))))
```
---
### 34 view_shipment_info_4_pm

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 |  |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contract_code | varchar(25) | - | - | - | - | - |
| itemCode | varchar(16) | - | - | - | - | - |
| itemModel | varchar(255) | - | - | - | - | - |
| itemName | varchar(255) | - | - | - | - | - |
| barcode | varchar(50) | - | - | - | - | - |
| comBarcode | varchar(50) | - | - | - | - | - |
| packId | varchar(64) | - | - | - | - | - |
| itemCode2 | varchar(16) | - | - | - | - | - |
| itemModel2 | varchar(255) | - | - | - | - | - |
| itemName2 | varchar(255) | - | - | - | - | - |
| barcode2 | varchar(50) | - | - | - | - | - |
| profitCenter | varchar(32) | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_shipment_info_4_pm` AS select `c`.`contract_code` AS `contract_code`,`sb`.`item` AS `itemCode`,`fi`.`itemname` AS `itemModel`,`fi`.`describe_` AS `itemName`,`sb`.`barcode` AS `barcode`,`sb`.`com_barcode` AS `comBarcode`,`s`.`packlist_id` AS `packId`,`sb`.`item2` AS `itemCode2`,`fi2`.`itemname` AS `itemModel2`,`fi2`.`describe_` AS `itemName2`,`sb`.`barcode2` AS `barcode2`,`sb`.`profitCenter` AS `profitCenter` from ((((`fb_contract` `c` left join `fb_shipment` `s` on((`c`.`contract_id` = `s`.`con_id`))) left join `fb_shipment_barcode` `sb` on((`s`.`packlist_id` = `sb`.`pack_id`))) left join `fb_items` `fi` on((`sb`.`item` = `fi`.`item`))) left join `fb_items` `fi2` on((`sb`.`item2` = `fi2`.`item`))) where ((`sb`.`barcode` is not null) and (isnull(`sb`.`rma_no`) or (`sb`.`rma_no` = '') or (`sb`.`rma_no` = 'null')))
```
---
### 35 view_soft_version

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | 软件版本视图，从fb_ft_result1/fb_ft_result2解析出CONP/CPLD/BOOT/PCB版本 |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| serial_number | varchar(100) | - | - | - | - | - |
| conp | mediumtext | - | - | - | - | - |
| cpld | mediumtext | - | - | - | - | - |
| boot | mediumtext | - | - | - | - | - |
| pcb | mediumtext | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`g01339`@`10.102.0.201` SQL SECURITY DEFINER VIEW `view_soft_version` AS (select `t2`.`serial_number` AS `serial_number`,substr(`t1`.`result_desc`,(locate('release',`t1`.`result_desc`) + 7),((locate('\r',`t1`.`result_desc`,locate('release',`t1`.`result_desc`)) - locate('release',`t1`.`result_desc`)) - 7)) AS `conp`,substr(`t1`.`result_desc`,(locate('cpldversion',`t1`.`result_desc`) + 12),((locate('\r',`t1`.`result_desc`,locate('cpldversion',`t1`.`result_desc`)) - locate('cpldversion',`t1`.`result_desc`)) - 12)) AS `cpld`,(case when (locate('BOOTVERSIONIS',`t1`.`result_desc`) <> 0) then substr(`t1`.`result_desc`,(locate('BOOTVERSIONIS',`t1`.`result_desc`) + 14),((locate('\r',`t1`.`result_desc`,locate('BOOTVERSIONIS',`t1`.`result_desc`)) - locate('BOOTVERSIONIS',`t1`.`result_desc`)) - 14)) else substr(`t1`.`result_desc`,(locate('BOOTVERSION',`t1`.`result_desc`) + 12),((locate('\r',`t1`.`result_desc`,locate('BOOTVERSION',`t1`.`result_desc`)) - locate('BOOTVERSION',`t1`.`result_desc`)) - 12)) end) AS `boot`,(case when (locate('PCBVERSIONIS',`t1`.`result_desc`) <> 0) then substr(`t1`.`result_desc`,(locate('PCBVERSIONIS',`t1`.`result_desc`) + 13),((locate('\r',`t1`.`result_desc`,locate('PCBVERSIONIS',`t1`.`result_desc`)) - locate('PCBVERSIONIS',`t1`.`result_desc`)) - 13)) else substr(`t1`.`result_desc`,(locate('PCBVERSION',`t1`.`result_desc`) + 11),((locate('\r',`t1`.`result_desc`,locate('PCBVERSION',`t1`.`result_desc`)) - locate('PCBVERSION',`t1`.`result_desc`)) - 11)) end) AS `pcb` from (`fb_ft_result2` `t1` left join `fb_ft_result1` `t2` on((`t1`.`result1_id` = `t2`.`item_id`))) where ((locate('release',`t1`.`result_desc`) <> 0) and (`t2`.`serial_number` is not null)))
```
---
### 36 view_subcontract_project_4_sse

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | 分包项目视图，查询分包项目的详细信息及回访状态 |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | - | - | - | - | - |
| subcontractName | varchar(512) | - | - | - | - | - |
| subcontractNo | varchar(64) | - | - | - | - | - |
| contractNos | varchar(2048) | - | - | - | - | - |
| projectIds | varchar(1024) | - | - | - | - | - |
| type | int(11) | - | - | - | - | - |
| state | int(11) | - | - | - | - | - |
| callbackState | int(11) | - | - | - | - | - |
| facilitatorId | int(11) | - | - | - | - | - |
| facilitatorName | varchar(64) | - | - | - | - | - |
| bankInfo | varchar(255) | - | - | - | - | - |
| bankAccount | varchar(64) | - | - | - | - | - |
| officeCode | varchar(25) | - | - | - | - | - |
| profitDepCode | varchar(25) | - | - | - | - | - |
| isAccrued | bit(1) | - | - | - | - | - |
| isInvoiced | bit(1) | - | - | - | - | - |
| subcontractAmount | varchar(25) | - | - | - | - | - |
| reason | varchar(512) | - | - | - | - | - |
| remark | varchar(512) | - | - | - | - | - |
| effectiveForm | datetime | - | - | - | - | - |
| effectiveTo | datetime | - | - | - | - | - |
| zrApproveTime | datetime | - | - | - | - | - |
| createBy | varchar(25) | - | - | - | - | - |
| createTime | datetime | - | - | - | - | - |
| updateBy | varchar(25) | - | - | - | - | - |
| updateTime | datetime | - | - | - | - | - |
| stateName | varchar(255) | - | - | - | - | - |
| callbackStateName | varchar(255) | - | - | - | - | - |
| createName | varchar(154) | - | - | - | - | - |
| officeName | varchar(20) | - | - | - | - | - |
| profitDepName | varchar(20) | - | - | - | - | - |
| typeName | varchar(255) | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`g01339`@`10.102.0.201` SQL SECURITY DEFINER VIEW `view_subcontract_project_4_sse` AS select `sph`.`id` AS `id`,`sph`.`subcontractName` AS `subcontractName`,`sph`.`subcontractNo` AS `subcontractNo`,`sph`.`contractNos` AS `contractNos`,`sph`.`projectIds` AS `projectIds`,`sph`.`type` AS `type`,`sph`.`state` AS `state`,`sph`.`callbackState` AS `callbackState`,`sph`.`facilitatorId` AS `facilitatorId`,`sph`.`facilitatorName` AS `facilitatorName`,`sph`.`bankInfo` AS `bankInfo`,`sph`.`bankAccount` AS `bankAccount`,`sph`.`officeCode` AS `officeCode`,`sph`.`profitDepCode` AS `profitDepCode`,`sph`.`isAccrued` AS `isAccrued`,`sph`.`isInvoiced` AS `isInvoiced`,`sph`.`subcontractAmount` AS `subcontractAmount`,`sph`.`reason` AS `reason`,`sph`.`remark` AS `remark`,`sph`.`effectiveFrom` AS `effectiveForm`,`sph`.`effectiveTo` AS `effectiveTo`,`sph`.`zrApproveTime` AS `zrApproveTime`,`sph`.`createBy` AS `createBy`,`sph`.`createTime` AS `createTime`,`sph`.`updateBy` AS `updateBy`,`sph`.`updateTime` AS `updateTime`,`bd`.`basicDataName` AS `stateName`,ifnull(if((`sph`.`type` = 30),`bd1`.`basicDataName`,convert(if((`sph`.`type` = 10),ifnull(`t`.`callbackStateName`,`tt`.`callbackStateName`),'无需回访') using utf8)),'未回访') AS `callbackStateName`,concat(`sph`.`createBy`,'-',`ui`.`realName`) AS `createName`,`d`.`departmentName` AS `officeName`,`d2`.`departmentName` AS `profitDepName`,`bd2`.`basicDataName` AS `typeName` from ((((((((`dpspms`.`pm_subcontract_project_header` `sph` left join (select `a`.`subcontractNo` AS `subcontractNo`,if((`aa`.`evaluationResult` = 1),'回访通过',if((`aa`.`evaluationResult` = -(1)),'回访不通过','未回访')) AS `callbackStateName`,`a`.`times` AS `times` from (((select `sph`.`subcontractNo` AS `subcontractNo`,max(`aa`.`id`) AS `id`,count(`aa`.`id`) AS `times` from (`dpspms`.`pm_subcontract_project_header` `sph` left join `dpspms`.`pm_cl_evaluation_header` `aa` on(find_in_set(`aa`.`projectId`,`sph`.`projectIds`))) where ((`aa`.`evaluationType` = 3) and (`aa`.`status` = 1) and (`sph`.`subcontractNo` > '') and (`sph`.`type` = 10)) group by `sph`.`subcontractNo`)) `a` left join `dpspms`.`pm_cl_evaluation_header` `aa` on((`aa`.`id` = `a`.`id`)))) `t` on(((`t`.`subcontractNo` = `sph`.`subcontractNo`) and (`sph`.`type` = 10)))) left join (select `a`.`subcontractNo` AS `subcontractNo`,if((`a`.`applyState` = 2),'回访通过',if((`a`.`applyState` = -(2)),'回访不通过','未回访')) AS `callbackStateName`,`a`.`times` AS `times` from (((select `sph`.`subcontractNo` AS `subcontractNo`,`cb`.`projectId` AS `projectId`,`cb`.`applyState` AS `applyState`,`cb`.`instId` AS `instId`,`cbq`.`id` AS `id`,`cbq`.`callBackId` AS `callBackId`,`cbq`.`taskId` AS `taskId`,`cbq`.`quesnaireId` AS `quesnaireId`,`cbq`.`quesnaireVersion` AS `quesnaireVersion`,`cbq`.`quesnaireState` AS `quesnaireState`,`cbq`.`createBy` AS `createBy`,`cbq`.`createTime` AS `createTime`,`cbq`.`updateBy` AS `updateBy`,`cbq`.`updateTime` AS `updateTime`,`cbq`.`effectiveFrom` AS `effectiveFrom`,`cbq`.`effectiveTo` AS `effectiveTo`,`cbq`.`quesnaireVersion` AS `times` from ((`dpspms`.`pm_subcontract_project_header` `sph` left join `dpspms`.`pm_cl_callback` `cb` on(find_in_set(`cb`.`projectId`,`sph`.`projectIds`))) left join (select `dpspms`.`pm_cl_callback_quesnaire`.`id` AS `id`,`dpspms`.`pm_cl_callback_quesnaire`.`callBackId` AS `callBackId`,`dpspms`.`pm_cl_callback_quesnaire`.`taskId` AS `taskId`,`dpspms`.`pm_cl_callback_quesnaire`.`quesnaireId` AS `quesnaireId`,`dpspms`.`pm_cl_callback_quesnaire`.`quesnaireVersion` AS `quesnaireVersion`,`dpspms`.`pm_cl_callback_quesnaire`.`quesnaireState` AS `quesnaireState`,`dpspms`.`pm_cl_callback_quesnaire`.`createBy` AS `createBy`,`dpspms`.`pm_cl_callback_quesnaire`.`createTime` AS `createTime`,`dpspms`.`pm_cl_callback_quesnaire`.`updateBy` AS `updateBy`,`dpspms`.`pm_cl_callback_quesnaire`.`updateTime` AS `updateTime`,`dpspms`.`pm_cl_callback_quesnaire`.`effectiveFrom` AS `effectiveFrom`,`dpspms`.`pm_cl_callback_quesnaire`.`effectiveTo` AS `effectiveTo` from `dpspms`.`pm_cl_callback_quesnaire` where ((`dpspms`.`pm_cl_callback_quesnaire`.`quesnaireState` = 1) and `dpspms`.`pm_cl_callback_quesnaire`.`id` in (select max(`dpspms`.`pm_cl_callback_quesnaire`.`id`) from `dpspms`.`pm_cl_callback_quesnaire` group by `dpspms`.`pm_cl_callback_quesnaire`.`callBackId`))) `cbq` on(((`cb`.`id` = `cbq`.`callBackId`) and (`cbq`.`quesnaireState` = 1)))) where ((`cb`.`applyState` <> -(1)) and (`cbq`.`quesnaireId` is not null) and (not(`cb`.`projectId` in (select distinct `dpspms`.`pm_cl_evaluation_header`.`projectId` from `dpspms`.`pm_cl_evaluation_header`)))) order by `cbq`.`createTime` desc)) `a` join (select `sph`.`subcontractNo` AS `subcontractNo`,`cb`.`projectId` AS `projectId`,max(`cbq`.`createTime`) AS `createTime` from ((`dpspms`.`pm_subcontract_project_header` `sph` left join `dpspms`.`pm_cl_callback` `cb` on(find_in_set(`cb`.`projectId`,`sph`.`projectIds`))) left join (select `dpspms`.`pm_cl_callback_quesnaire`.`id` AS `id`,`dpspms`.`pm_cl_callback_quesnaire`.`callBackId` AS `callBackId`,`dpspms`.`pm_cl_callback_quesnaire`.`taskId` AS `taskId`,`dpspms`.`pm_cl_callback_quesnaire`.`quesnaireId` AS `quesnaireId`,`dpspms`.`pm_cl_callback_quesnaire`.`quesnaireVersion` AS `quesnaireVersion`,`dpspms`.`pm_cl_callback_quesnaire`.`quesnaireState` AS `quesnaireState`,`dpspms`.`pm_cl_callback_quesnaire`.`createBy` AS `createBy`,`dpspms`.`pm_cl_callback_quesnaire`.`createTime` AS `createTime`,`dpspms`.`pm_cl_callback_quesnaire`.`updateBy` AS `updateBy`,`dpspms`.`pm_cl_callback_quesnaire`.`updateTime` AS `updateTime`,`dpspms`.`pm_cl_callback_quesnaire`.`effectiveFrom` AS `effectiveFrom`,`dpspms`.`pm_cl_callback_quesnaire`.`effectiveTo` AS `effectiveTo` from `dpspms`.`pm_cl_callback_quesnaire` where ((`dpspms`.`pm_cl_callback_quesnaire`.`quesnaireState` = 1) and `dpspms`.`pm_cl_callback_quesnaire`.`id` in (select max(`dpspms`.`pm_cl_callback_quesnaire`.`id`) from `dpspms`.`pm_cl_callback_quesnaire` group by `dpspms`.`pm_cl_callback_quesnaire`.`callBackId`))) `cbq` on(((`cb`.`id` = `cbq`.`callBackId`) and (`cbq`.`quesnaireState` = 1)))) where ((`cb`.`applyState` <> -(1)) and (`cbq`.`quesnaireId` is not null) and (not(`cb`.`projectId` in (select distinct `dpspms`.`pm_cl_evaluation_header`.`projectId` from `dpspms`.`pm_cl_evaluation_header`)))) group by `sph`.`subcontractNo`) `ma` on(((`ma`.`createTime` = `a`.`createTime`) and (`ma`.`subcontractNo` = `a`.`subcontractNo`))))) `tt` on(((`tt`.`subcontractNo` = `sph`.`subcontractNo`) and (`sph`.`type` = 10)))) left join `dpspms`.`fnd_department` `d` on((`sph`.`officeCode` = `d`.`departmentNum`))) left join `dpspms`.`fnd_department` `d2` on((`sph`.`profitDepCode` = `d2`.`departmentNum`))) left join `dpspms`.`fnd_basic_data` `bd` on(((`bd`.`basicDataId` = `sph`.`state`) and (`bd`.`dataTypeCode` = 'subcontractState')))) left join `dpspms`.`fnd_basic_data` `bd1` on(((`bd1`.`basicDataId` = `sph`.`callbackState`) and (`bd1`.`dataTypeCode` = 'subcontractCbState')))) left join `dpspms`.`fnd_basic_data` `bd2` on(((`sph`.`type` = `bd2`.`basicDataId`) and (`bd2`.`dataTypeCode` = 'subcontractType')))) left join `dpspms`.`fnd_user_info` `ui` on((`ui`.`username` = `sph`.`createBy`))) where (`sph`.`subcontractNo` > '')
```
---
### 37 view_txinfo

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | 借用物流信息视图，关联tx_info/app_spare_part/brw_app_info/app_comment/user_info |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| sheetID | varchar(15) | - | - | - | - | - |
| beforeChange_sheetID | varchar(15) | - | - | - | - | - |
| applicant | varchar(25) | - | - | - | - | - |
| app_time | datetime | - | - | - | - | - |
| app_dptNo | varchar(10) | - | - | - | - | - |
| prt_name | varchar(255) | - | - | - | - | - |
| app_reason | text | - | - | - | - | - |
| promise_returntime | datetime | - | - | - | - | - |
| trade_classify | varchar(100) | - | - | - | - | - |
| signing_state | char(1) | - | - | - | - | - |
| kept_place | varchar(10) | - | - | - | - | - |
| demand_type | varchar(8) | - | - | - | - | - |
| his_zipCode | varchar(25) | - | - | - | - | - |
| his_addr | varchar(1024) | - | - | - | - | - |
| addre_id | int(11) | - | - | - | - | - |
| duty_person | varchar(10) | - | - | - | - | - |
| spare_serialNum | varchar(50) | - | - | - | - | - |
| start_use_time | datetime | - | - | - | - | - |
| send_time | datetime | - | - | - | - | - |
| EMS_num | varchar(255) | - | - | - | - | - |
| EMS_company | varchar(255) | - | - | - | - | - |
| item_code | varchar(25) | - | - | - | - | - |
| item_name | varchar(255) | - | - | - | - | - |
| isOK | char(1) | - | - | - | - | - |
| remark | text | - | - | - | - | - |
| tx_id | int(11) | - | - | - | - | - |
| action_time | datetime | - | - | - | - | - |
| shiftimes | int(11) | - | - | - | - | - |
| turnovertimes | int(11) | - | - | - | - | - |
| allottimes | int(11) | - | - | - | - | - |
| take_place | varchar(15) | - | - | - | - | - |
| approve_time | datetime | - | - | - | - | - |
| receive_time | datetime | - | - | - | - | - |
| sendout_whsCode | varchar(10) | - | - | - | - | - |
| isNew | char(1) | - | - | - | - | - |
| extend_returntime | datetime | - | - | - | - | - |
| hexiao_time | datetime | - | - | - | - | - |
| isUnion | int(11) | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`g01339`@`10.102.0.201` SQL SECURITY DEFINER VIEW `view_txinfo` AS (select `bai`.`sheetID` AS `sheetID`,`bai`.`beforeChange_sheetID` AS `beforeChange_sheetID`,`bai`.`applicant` AS `applicant`,`bai`.`app_time` AS `app_time`,`bai`.`app_dptNo` AS `app_dptNo`,`bai`.`prt_name` AS `prt_name`,`bai`.`app_reason` AS `app_reason`,`bai`.`promise_returntime` AS `promise_returntime`,`bai`.`trade_classify` AS `trade_classify`,`bai`.`signing_state` AS `signing_state`,`bai`.`kept_place` AS `kept_place`,(case when (`bai`.`demand_type` = '1') then '储备申请' when (`bai`.`demand_type` = '2') then '补库申请' when (`bai`.`demand_type` = '3') then '其他' when (`bai`.`demand_type` = '4') then '开局保障' when (`bai`.`demand_type` = '5') then '问题跟踪保障' when (`bai`.`demand_type` = '6') then '高端价值客户保障' end) AS `demand_type`,`bai`.`his_zipCode` AS `his_zipCode`,`bai`.`his_addr` AS `his_addr`,`bai`.`addre_id` AS `addre_id`,`bai`.`duty_person` AS `duty_person`,`ti`.`spare_serialNum` AS `spare_serialNum`,`bai`.`start_use_time` AS `start_use_time`,`ti`.`send_time` AS `send_time`,`ti`.`EMS_num` AS `EMS_num`,`ti`.`EMS_company` AS `EMS_company`,`asp`.`item_code` AS `item_code`,`asp`.`item_name` AS `item_name`,`asp`.`isOK` AS `isOK`,`bai`.`remark` AS `remark`,`ti`.`tx_id` AS `tx_id`,`asp`.`action_time` AS `action_time`,`ti`.`shiftimes` AS `shiftimes`,`ti`.`turnovertimes` AS `turnovertimes`,`ti`.`allottimes` AS `allottimes`,`ac`.`take_place` AS `take_place`,`ac`.`approve_time` AS `approve_time`,`ti`.`receive_time` AS `receive_time`,`ti`.`sendout_whsCode` AS `sendout_whsCode`,`asp`.`isNew` AS `isNew`,`bai`.`extend_returntime` AS `extend_returntime`,`asp`.`hexiao_time` AS `hexiao_time`,`ac`.`isUnion` AS `isUnion` from ((((`tx_info` `ti` join `app_spare_part` `asp`) join `brw_app_info` `bai`) join `app_comment` `ac`) join `user_info` `u`) where ((`ti`.`tx_id` = `asp`.`tx_id`) and (`ti`.`sheetID` = `bai`.`sheetID`) and (`bai`.`sheetID` = `ac`.`sheetID`) and (`bai`.`applicant` = `u`.`username`) and (`ti`.`version_no` = `bai`.`version_no`) and (`ac`.`is_pass` = '1') and ((`ti`.`tx_type` = 1) or (`ti`.`tx_type` = 2))))
```
---
### 38 view_warranty_info_4_ts

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | TS维保信息视图，映射view_warranty的维保数据 |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | bigint(20) | - | - | - | - | - |
| projectName | varchar(512) | - | - | - | - | - |
| contractNo | varchar(25) | - | - | - | - | - |
| barcode | varchar(50) | - | - | - | - | - |
| item | varchar(16) | - | - | - | - | - |
| itemName | varchar(255) | - | - | - | - | - |
| itemDesc | varchar(255) | - | - | - | - | - |
| barcode2 | varchar(50) | - | - | - | - | - |
| item2 | varchar(16) | - | - | - | - | - |
| itemName2 | varchar(255) | - | - | - | - | - |
| itemDesc2 | varchar(255) | - | - | - | - | - |
| gradeName | varchar(125) | - | - | - | - | - |
| warranty | varchar(2) | - | - | - | - | - |
| warrantyStartTime | datetime | - | - | - | - | - |
| warrantyEndTime | datetime | - | - | - | - | - |
| diff | int(7) | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_warranty_info_4_ts` AS select `view_warranty`.`id` AS `id`,`view_warranty`.`project_name` AS `projectName`,`view_warranty`.`contract_code` AS `contractNo`,`view_warranty`.`barcode` AS `barcode`,`view_warranty`.`item` AS `item`,`view_warranty`.`itemName` AS `itemName`,`view_warranty`.`describe_` AS `itemDesc`,`view_warranty`.`barcode2` AS `barcode2`,`view_warranty`.`item2` AS `item2`,`view_warranty`.`itemName2` AS `itemName2`,`view_warranty`.`describe_2` AS `itemDesc2`,`view_warranty`.`gradeName` AS `gradeName`,`view_warranty`.`warranty` AS `warranty`,`view_warranty`.`warrantyStartTime` AS `warrantyStartTime`,`view_warranty`.`warrantyEndTime` AS `warrantyEndTime`,`view_warranty`.`diff` AS `diff` from `view_warranty`
```
---
### 39 view_warranty_source

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | 维保来源视图，综合计算设备维保起止时间，关联合同/发货/条码/物料/服务/等级/部门/状态表 |
| 数据量 | - |
| 数据大小 | - |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | bigint(20) | - | - | - | - | - |
| barcode | varchar(50) | - | - | - | - | - |
| comBarCode | varchar(50) | - | - | - | - | - |
| old_warrantyEndTime | datetime | - | - | - | - | - |
| warrantyEndTime | datetime | - | - | - | - | - |
| old_diff | int(7) | - | - | - | - | - |
| diff | int(7) | - | - | - | - | - |
| warrantyStartTime | datetime | - | - | - | - | - |
| old_warrantyStartTime | datetime | - | - | - | - | - |
| item | varchar(16) | - | - | - | - | - |
| describe_ | varchar(255) | - | - | - | - | - |
| itemName | varchar(255) | - | - | - | - | - |
| gradeName | varchar(125) | - | - | - | - | - |
| gradeCode | varchar(25) | - | - | - | - | - |
| packdate | datetime | - | - | - | - | - |
| contract_code | varchar(25) | - | - | - | - | - |
| contract_type | int(11) | - | - | - | - | - |
| contract_type_name | varchar(25) | - | - | - | - | - |
| project_name | varchar(512) | - | - | - | - | - |
| customer_name | varchar(512) | - | - | - | - | - |
| office_code | varchar(25) | - | - | - | - | - |
| office_name | varchar(25) | - | - | - | - | - |
| marketCode | varchar(10) | - | - | - | - | - |
| marketName | varchar(15) | - | - | - | - | - |
| systemId | int(11) | - | - | - | - | - |
| systemName | varchar(15) | - | - | - | - | - |
| warranty | varchar(2) | - | - | - | - | - |
| warrantyMonth | double | - | - | - | - | - |
| barcode2 | varchar(50) | - | - | - | - | - |
| item2 | varchar(16) | - | - | - | - | - |
| syncTime | datetime | - | - | - | - | - |

**查询逻辑**

```sql
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_warranty_source` AS select `sb`.`id` AS `id`,`sb`.`barcode` AS `barcode`,`sb`.`com_barcode` AS `comBarCode`,(case when isnull(`fs`.`end_date`) then (`s`.`warrantyEndTime` - interval 1 day) else `fs`.`end_date` end) AS `old_warrantyEndTime`,(case when isnull(`fs`.`end_date`) then (ifnull((ifnull(`sb`.`warrantyStartDate`,`s`.`warrantyStartTime`) + interval if((ifnull(`sb`.`warrantyMonth`,0) = 0),(`c`.`warranty` * 12),`sb`.`warrantyMonth`) month),`s`.`warrantyEndTime`) - interval 1 day) else `fs`.`end_date` end) AS `warrantyEndTime`,(to_days(now()) - to_days((case when isnull(`fs`.`end_date`) then (`s`.`warrantyEndTime` - interval 1 day) else `fs`.`end_date` end))) AS `old_diff`,(to_days(now()) - to_days((case when isnull(`fs`.`end_date`) then (ifnull((ifnull(`sb`.`warrantyStartDate`,`s`.`warrantyStartTime`) + interval if((ifnull(`sb`.`warrantyMonth`,0) = 0),(`c`.`warranty` * 12),`sb`.`warrantyMonth`) month),`s`.`warrantyEndTime`) - interval 1 day) else `fs`.`end_date` end))) AS `diff`,ifnull(`sb`.`warrantyStartDate`,`s`.`warrantyStartTime`) AS `warrantyStartTime`,`s`.`warrantyStartTime` AS `old_warrantyStartTime`,`sb`.`item` AS `item`,`f`.`describe_` AS `describe_`,`f`.`itemname` AS `itemName`,(case when isnull(`grade`.`gradename`) then 'DPtech 维保服务,基本维保 5x10xNBD发出' else `grade`.`gradename` end) AS `gradeName`,(case when isnull(`grade`.`gradecode`) then '53030001' else `grade`.`gradecode` end) AS `gradeCode`,`s`.`packdate` AS `packdate`,`c`.`contract_code` AS `contract_code`,`c`.`contract_type` AS `contract_type`,`st`.`resolveName` AS `contract_type_name`,`c`.`project_name` AS `project_name`,`c`.`customer_name` AS `customer_name`,(case when (`t`.`officeCode` is not null) then `t`.`officeCode` else `c`.`office_code` end) AS `office_code`,(case when (`t`.`officeCode` is not null) then `t1`.`ocrName` else `d`.`ocrName` end) AS `office_name`,`c`.`marketCode` AS `marketCode`,`c`.`marketName` AS `marketName`,`c`.`systemId` AS `systemId`,`c`.`systemName` AS `systemName`,`c`.`warranty` AS `warranty`,if((ifnull(`sb`.`warrantyMonth`,0) > 0),`sb`.`warrantyMonth`,(`c`.`warranty` * 12)) AS `warrantyMonth`,`sb`.`barcode2` AS `barcode2`,`sb`.`item2` AS `item2`,`sb`.`syncTime` AS `syncTime` from (((((((((`fb_contract` `c` left join `fb_office_relationship` `t` on((`c`.`contract_code` = `t`.`contractNo`))) left join `department` `t1` on((`t`.`officeCode` = `t1`.`ocrCode`))) left join `fb_shipment` `s` on((`c`.`contract_id` = `s`.`con_id`))) left join `fb_shipment_barcode` `sb` on((`s`.`packlist_id` = `sb`.`pack_id`))) left join `fb_items` `f` on((`sb`.`item` = `f`.`item`))) left join `fb_service` `fs` on(((`sb`.`barcode` = `fs`.`barcode`) and (`s`.`packdate` < `fs`.`end_date`) and (`fs`.`state` = 1)))) left join `fb_warranty_grade` `grade` on((`fs`.`grade` = `grade`.`gradecode`))) left join `department` `d` on((`d`.`ocrCode` = `c`.`office_code`))) left join `sys_state_or_type` `st` on(((`st`.`resolveCode` = `c`.`contract_type`) and (`st`.`stCode` = '01')))) where ((`c`.`contract_type` in (0,11)) and (`sb`.`barcode` is not null) and (isnull(`sb`.`rma_no`) or (`sb`.`rma_no` = '') or (`sb`.`rma_no` = 'null')))
```
---