# PMS主数据库 dppms_d365 完整数据字典

> 生成时间：2026-05-21 | 数据库：dppms_d365 | 排除表前缀：temp_*, tmp_*, *_temp, *_tmp

## 目录

- [1. 数据库概览](#1-数据库概览)
- [2. 表结构详细说明](#2-表结构详细说明)
- [3. 枚举值汇总](#3-枚举值汇总)
- [4. 字段映射关系](#4-字段映射关系)
- [5. 基础数据类型](#5-基础数据类型)
- [6. 外键关系](#6-外键关系)
- [7. 索引有效性分析](#7-索引有效性分析)

## 1. 数据库概览

- **对象总数**：297（排除temp_*/tmp_/*_temp/*_tmp前缀）
  - **BASE TABLE**：259（含25个act_工作流表、15个fb_迁移表、6个ehr_人事表、3个view_前缀实际为BASE TABLE的表等）
  - **VIEW**：38（含3个dp_v_数据平台视图、5个pm_前缀VIEW、30个view_前缀VIEW）
- **总数据行数（估算）**：30,333,951
- **外键约束数**：4

### 1.1 数据量TOP 20表

| 表名 | 业务含义 | 估算行数 | 数据大小 | 索引大小 |
|------|----------|----------|----------|----------|
| pm_presales_project_product_line | 售前项目产品线表 | 4,358,185 | 879.0 MB | 130.2 MB |
| fb_shipment_barcode | Firebird迁移发货条码表 | 3,541,100 | 612.0 MB | 2369.0 MB |
| tb_sys_log | 系统日志表 | 3,182,907 | 164.2 MB | 45.5 MB |
| view_warranty_with_presales | 维保含售前信息表 | 2,857,552 | 1124.4 MB | 188.6 MB |
| view_warranty | 维保信息表 | 2,857,552 | 1124.4 MB | 217.7 MB |
| fb_shipment_barcode_order_line | Firebird迁移发货条码订单行表 | 2,576,429 | 373.0 MB | 568.0 MB |
| mes_oqc_info | MES出货检验信息表 | 1,411,475 | 164.7 MB | 74.7 MB |
| pm_project_soft_version_history | 项目软件版本历史表 | 1,055,447 | 317.8 MB | 232.8 MB |
| pm_project_soft_version | 项目软件版本表 | 532,125 | 327.8 MB | 185.3 MB |
| fb_shipment_barcode_change_log | Firebird迁移发货条码变更日志表 | 528,607 | 547.0 MB | 120.0 MB |
| fb_ft_result2 | Firebird迁移功能测试结果2表 | 496,626 | 298.8 MB | 17.5 MB |
| pm_project_shipment | 项目发货表 | 458,802 | 117.6 MB | 43.6 MB |
| pm_cl_quesnaire_result_line | 问卷结果明细表 | 454,519 | 28.6 MB | 38.0 MB |
| pm_project_member | 项目成员表 | 302,424 | 32.6 MB | 37.1 MB |
| shipment_barcode_from_spms_unique | 发货条码唯一表 | 233,076 | 17.5 MB | 0 MB |
| pm_order_line_from_erp | ERP订单行表 | 219,652 | 26.6 MB | 11.0 MB |
| pm_order_line_from_erp_sap | ERP订单行SAP表 | 208,448 | 24.5 MB | 16.0 MB |
| pm_order_line_from_erp_source | ERP订单行源表 | 205,968 | 26.6 MB | 15.0 MB |
| act_hi_varinst | Activiti历史变量表 | 204,606 | 18.5 MB | 22.6 MB |
| fb_ft_result1 | Firebird迁移功能测试结果1表 | 193,752 | 10.5 MB | 6.5 MB |

## 2. 表结构详细说明

### act_evt_log

**业务含义**：Activiti事件日志表 - 记录流程引擎的事件日志

**数据量**：约6,398行 | 数据大小：17.5MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| LOG_NR_ | bigint(20) | NO |  | PRI | 日志编号 |
| TYPE_ | varchar(64) | YES |  |  | 事件类型 |
| PROC_DEF_ID_ | varchar(64) | YES |  |  | 流程定义ID |
| PROC_INST_ID_ | varchar(64) | YES |  |  | 流程实例ID |
| EXECUTION_ID_ | varchar(64) | YES |  |  | 执行ID |
| TASK_ID_ | varchar(64) | YES |  |  | 任务ID |
| TIME_STAMP_ | timestamp(3) | NO | CURRENT_TIMESTAMP(3) |  | 时间戳 |
| USER_ID_ | varchar(255) | YES |  |  | 用户ID |
| DATA_ | longblob | YES |  |  | 事件数据 |
| LOCK_OWNER_ | varchar(255) | YES |  |  | 锁持有者 |
| LOCK_TIME_ | timestamp(3) | YES |  |  | 锁获取时间 |
| IS_PROCESSED_ | tinyint(4) | YES | 0 |  | 是否已处理 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | LOG_NR_ | 是 | BTREE |

---

### act_ge_bytearray

**业务含义**：Activiti通用字节数组表 - 存储流程定义、资源文件等二进制数据

**数据量**：约1,210行 | 数据大小：6.5MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| ID_ | varchar(64) | NO |  | PRI | ID标识 |
| REV_ | int(11) | YES |  |  | 版本号 |
| NAME_ | varchar(255) | YES |  |  | 资源名称 |
| DEPLOYMENT_ID_ | varchar(64) | YES |  | MUL | 部署ID |
| BYTES_ | longblob | YES |  |  | 字节数据 |
| GENERATED_ | tinyint(4) | YES |  |  | 是否自动生成 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| ACT_FK_BYTEARR_DEPL | DEPLOYMENT_ID_ | 否 | BTREE |
| PRIMARY | ID_ | 是 | BTREE |

---

### act_ge_property

**业务含义**：Activiti通用属性表 - 存储流程引擎的属性配置

**数据量**：约3行 | 数据大小：0MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| NAME_ | varchar(64) | NO |  | PRI | 资源名称 |
| VALUE_ | varchar(300) | YES |  |  | 属性值 |
| REV_ | int(11) | YES |  |  | 版本号 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | NAME_ | 是 | BTREE |

---

### act_hi_actinst

**业务含义**：Activiti历史活动实例表 - 记录流程中所有活动节点的历史执行信息

**数据量**：约155,827行 | 数据大小：22.5MB | 索引大小：17.1MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| ID_ | varchar(64) | NO |  | PRI | ID标识 |
| PROC_DEF_ID_ | varchar(64) | NO |  |  | 流程定义ID |
| PROC_INST_ID_ | varchar(64) | NO |  | MUL | 流程实例ID |
| EXECUTION_ID_ | varchar(64) | NO |  | MUL | 执行ID |
| ACT_ID_ | varchar(255) | NO |  |  | 活动节点ID |
| TASK_ID_ | varchar(64) | YES |  |  | 任务ID |
| CALL_PROC_INST_ID_ | varchar(64) | YES |  |  | 调用流程实例ID |
| ACT_NAME_ | varchar(255) | YES |  |  | 活动名称 |
| ACT_TYPE_ | varchar(255) | NO |  |  | 活动类型 |
| ASSIGNEE_ | varchar(255) | YES |  |  | 办理人 |
| DURATION_ | bigint(20) | YES |  |  | 耗时 |
| TENANT_ID_ | varchar(255) | YES |  |  | 租户ID |
| START_TIME_ | datetime(3) | NO |  |  | 开始时间 |
| END_TIME_ | datetime(3) | YES |  |  | 结束时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| ACT_IDX_HI_ACT_INST_EXEC | EXECUTION_ID_, ACT_ID_ | 否 | BTREE |
| ACT_IDX_HI_ACT_INST_PROCINST | PROC_INST_ID_, ACT_ID_ | 否 | BTREE |
| PRIMARY | ID_ | 是 | BTREE |

---

### act_hi_attachment

**业务含义**：Activiti历史附件表 - 记录流程实例的附件信息

**数据量**：约0行 | 数据大小：0MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| ID_ | varchar(64) | NO |  | PRI | ID标识 |
| REV_ | int(11) | YES |  |  | 版本号 |
| USER_ID_ | varchar(255) | YES |  |  | 用户ID |
| NAME_ | varchar(255) | YES |  |  | 资源名称 |
| DESCRIPTION_ | varchar(4000) | YES |  |  | 描述 |
| TYPE_ | varchar(255) | YES |  |  | 事件类型 |
| TASK_ID_ | varchar(64) | YES |  |  | 任务ID |
| PROC_INST_ID_ | varchar(64) | YES |  |  | 流程实例ID |
| URL_ | varchar(4000) | YES |  |  | 附件URL |
| CONTENT_ID_ | varchar(64) | YES |  |  | 内容ID |
| TIME_ | datetime(3) | YES |  |  | 时间戳 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | ID_ | 是 | BTREE |

---

### act_hi_comment

**业务含义**：Activiti历史评论表 - 记录流程实例的审批意见和评论

**数据量**：约66,552行 | 数据大小：7.5MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| ID_ | varchar(64) | NO |  | PRI | ID标识 |
| TYPE_ | varchar(255) | YES |  |  | 事件类型 |
| USER_ID_ | varchar(255) | YES |  |  | 用户ID |
| TASK_ID_ | varchar(64) | YES |  |  | 任务ID |
| PROC_INST_ID_ | varchar(64) | YES |  |  | 流程实例ID |
| ACTION_ | varchar(255) | YES |  |  | 操作 |
| MESSAGE_ | varchar(4000) | YES |  |  | 评论内容 |
| FULL_MSG_ | longblob | YES |  |  | 完整消息 |
| TIME_ | datetime(3) | NO |  |  | 时间戳 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | ID_ | 是 | BTREE |

---

### act_hi_detail

**业务含义**：Activiti历史详情表 - 记录流程变量的变更详情

**数据量**：约0行 | 数据大小：0MB | 索引大小：0.1MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| ID_ | varchar(64) | NO |  | PRI | ID标识 |
| TYPE_ | varchar(255) | NO |  |  | 事件类型 |
| PROC_INST_ID_ | varchar(64) | YES |  | MUL | 流程实例ID |
| EXECUTION_ID_ | varchar(64) | YES |  |  | 执行ID |
| TASK_ID_ | varchar(64) | YES |  | MUL | 任务ID |
| ACT_INST_ID_ | varchar(64) | YES |  | MUL | 活动实例ID |
| NAME_ | varchar(255) | NO |  | MUL | 资源名称 |
| VAR_TYPE_ | varchar(255) | YES |  |  | 变量类型 |
| REV_ | int(11) | YES |  |  | 版本号 |
| BYTEARRAY_ID_ | varchar(64) | YES |  |  | 字节数组ID |
| DOUBLE_ | double | YES |  |  | 双精度值 |
| LONG_ | bigint(20) | YES |  |  | 长整型值 |
| TEXT_ | varchar(4000) | YES |  |  | 文本值 |
| TEXT2_ | varchar(4000) | YES |  |  | 文本值2 |
| TIME_ | datetime(3) | NO |  |  | 时间戳 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| ACT_IDX_HI_DETAIL_ACT_INST | ACT_INST_ID_ | 否 | BTREE |
| ACT_IDX_HI_DETAIL_NAME | NAME_ | 否 | BTREE |
| ACT_IDX_HI_DETAIL_PROC_INST | PROC_INST_ID_ | 否 | BTREE |
| ACT_IDX_HI_DETAIL_TASK_ID | TASK_ID_ | 否 | BTREE |
| PRIMARY | ID_ | 是 | BTREE |

---

### act_hi_identitylink

**业务含义**：Activiti历史身份关联表 - 记录流程中参与者与流程实例的历史关联

**数据量**：约143,081行 | 数据大小：8.5MB | 索引大小：12.5MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| ID_ | varchar(64) | NO |  | PRI | ID标识 |
| GROUP_ID_ | varchar(255) | YES |  |  | 组ID |
| TYPE_ | varchar(255) | YES |  |  | 事件类型 |
| USER_ID_ | varchar(255) | YES |  | MUL | 用户ID |
| TASK_ID_ | varchar(64) | YES |  | MUL | 任务ID |
| PROC_INST_ID_ | varchar(64) | YES |  | MUL | 流程实例ID |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| ACT_IDX_HI_IDENT_LNK_PROCINST | PROC_INST_ID_ | 否 | BTREE |
| ACT_IDX_HI_IDENT_LNK_TASK | TASK_ID_ | 否 | BTREE |
| ACT_IDX_HI_IDENT_LNK_USER | USER_ID_ | 否 | BTREE |
| PRIMARY | ID_ | 是 | BTREE |

---

### act_hi_procinst

**业务含义**：Activiti历史流程实例表 - 记录流程实例的历史执行信息

**数据量**：约18,833行 | 数据大小：2.5MB | 索引大小：1.9MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| ID_ | varchar(64) | NO |  | PRI | ID标识 |
| PROC_INST_ID_ | varchar(64) | NO |  | UNI | 流程实例ID |
| BUSINESS_KEY_ | varchar(255) | YES |  | MUL | 业务键 |
| PROC_DEF_ID_ | varchar(64) | NO |  |  | 流程定义ID |
| DURATION_ | bigint(20) | YES |  |  | 耗时 |
| START_USER_ID_ | varchar(255) | YES |  |  | 发起人ID |
| START_ACT_ID_ | varchar(255) | YES |  |  | 开始活动ID |
| END_ACT_ID_ | varchar(255) | YES |  |  | 结束活动ID |
| SUPER_PROCESS_INSTANCE_ID_ | varchar(64) | YES |  |  | 父流程实例ID |
| DELETE_REASON_ | varchar(4000) | YES |  |  | 删除原因 |
| TENANT_ID_ | varchar(255) | YES |  |  | 租户ID |
| START_TIME_ | datetime(3) | NO |  |  | 开始时间 |
| END_TIME_ | datetime(3) | YES |  |  | 结束时间 |
| NAME_ | varchar(255) | YES |  |  | 资源名称 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| ACT_IDX_HI_PRO_I_BUSKEY | BUSINESS_KEY_ | 否 | BTREE |
| PRIMARY | ID_ | 是 | BTREE |
| PROC_INST_ID_ | PROC_INST_ID_ | 是 | BTREE |

---

### act_hi_taskinst

**业务含义**：Activiti历史任务实例表 - 记录任务的历史执行信息

**数据量**：约67,971行 | 数据大小：10.5MB | 索引大小：2.5MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| ID_ | varchar(64) | NO |  | PRI | ID标识 |
| PROC_DEF_ID_ | varchar(64) | YES |  |  | 流程定义ID |
| TASK_DEF_KEY_ | varchar(255) | YES |  |  | 任务定义键 |
| PROC_INST_ID_ | varchar(64) | YES |  | MUL | 流程实例ID |
| EXECUTION_ID_ | varchar(64) | YES |  |  | 执行ID |
| NAME_ | varchar(255) | YES |  |  | 资源名称 |
| PARENT_TASK_ID_ | varchar(64) | YES |  |  | 父任务ID |
| DESCRIPTION_ | varchar(4000) | YES |  |  | 描述 |
| OWNER_ | varchar(255) | YES |  |  | 任务拥有者 |
| ASSIGNEE_ | varchar(255) | YES |  |  | 办理人 |
| DURATION_ | bigint(20) | YES |  |  | 耗时 |
| DELETE_REASON_ | varchar(4000) | YES |  |  | 删除原因 |
| PRIORITY_ | int(11) | YES |  |  | 优先级 |
| FORM_KEY_ | varchar(255) | YES |  |  | 表单键 |
| CATEGORY_ | varchar(255) | YES |  |  | 分类 |
| TENANT_ID_ | varchar(255) | YES |  |  | 租户ID |
| START_TIME_ | datetime(3) | NO |  |  | 开始时间 |
| CLAIM_TIME_ | datetime(3) | YES |  |  | 认领时间 |
| END_TIME_ | datetime(3) | YES |  |  | 结束时间 |
| DUE_DATE_ | datetime(3) | YES |  |  | 到期日期 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| ACT_IDX_HI_TASK_INST_PROCINST | PROC_INST_ID_ | 否 | BTREE |
| PRIMARY | ID_ | 是 | BTREE |

---

### act_hi_varinst

**业务含义**：Activiti历史变量实例表 - 记录流程变量的历史值

**数据量**：约204,606行 | 数据大小：18.5MB | 索引大小：22.6MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| ID_ | varchar(64) | NO |  | PRI | ID标识 |
| PROC_INST_ID_ | varchar(64) | YES |  | MUL | 流程实例ID |
| EXECUTION_ID_ | varchar(64) | YES |  |  | 执行ID |
| TASK_ID_ | varchar(64) | YES |  | MUL | 任务ID |
| NAME_ | varchar(255) | NO |  | MUL | 资源名称 |
| VAR_TYPE_ | varchar(100) | YES |  |  | 变量类型 |
| REV_ | int(11) | YES |  |  | 版本号 |
| BYTEARRAY_ID_ | varchar(64) | YES |  |  | 字节数组ID |
| DOUBLE_ | double | YES |  |  | 双精度值 |
| LONG_ | bigint(20) | YES |  |  | 长整型值 |
| TEXT_ | varchar(4000) | YES |  |  | 文本值 |
| TEXT2_ | varchar(4000) | YES |  |  | 文本值2 |
| CREATE_TIME_ | datetime(3) | YES |  |  | 创建时间 |
| LAST_UPDATED_TIME_ | datetime(3) | YES |  |  | 最后更新时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| ACT_IDX_HI_PROCVAR_NAME_TYPE | NAME_, VAR_TYPE_ | 否 | BTREE |
| ACT_IDX_HI_PROCVAR_PROC_INST | PROC_INST_ID_ | 否 | BTREE |
| ACT_IDX_HI_PROCVAR_TASK_ID | TASK_ID_ | 否 | BTREE |
| PRIMARY | ID_ | 是 | BTREE |

---

### act_id_group

**业务含义**：Activiti身份组表 - 存储用户组信息

**数据量**：约12行 | 数据大小：0MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| ID_ | varchar(64) | NO |  | PRI | ID标识 |
| REV_ | int(11) | YES |  |  | 版本号 |
| NAME_ | varchar(255) | YES |  |  | 资源名称 |
| TYPE_ | varchar(255) | YES |  |  | 事件类型 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | ID_ | 是 | BTREE |

---

### act_id_info

**业务含义**：Activiti身份信息表 - 存储用户扩展信息

**数据量**：约0行 | 数据大小：0MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| ID_ | varchar(64) | NO |  | PRI | ID标识 |
| REV_ | int(11) | YES |  |  | 版本号 |
| USER_ID_ | varchar(64) | YES |  |  | 用户ID |
| TYPE_ | varchar(64) | YES |  |  | 事件类型 |
| KEY_ | varchar(255) | YES |  |  | 部署标识 |
| VALUE_ | varchar(255) | YES |  |  | 属性值 |
| PASSWORD_ | longblob | YES |  |  | 密码 |
| PARENT_ID_ | varchar(255) | YES |  |  | 父执行ID |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | ID_ | 是 | BTREE |

---

### act_id_membership

**业务含义**：Activiti身份成员关系表 - 存储用户与组的关联关系

**数据量**：约548行 | 数据大小：0MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| USER_ID_ | varchar(64) | NO |  | PRI | 用户ID |
| GROUP_ID_ | varchar(64) | NO |  | PRI | 组ID |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| ACT_FK_MEMB_GROUP | GROUP_ID_ | 否 | BTREE |
| PRIMARY | USER_ID_, GROUP_ID_ | 是 | BTREE |

---

### act_id_user

**业务含义**：Activiti身份用户表 - 存储用户基本信息

**数据量**：约201行 | 数据大小：0MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| ID_ | varchar(64) | NO |  | PRI | ID标识 |
| REV_ | int(11) | YES |  |  | 版本号 |
| FIRST_ | varchar(255) | YES |  |  | 是否第一个 |
| LAST_ | varchar(255) | YES |  |  | 姓氏 |
| EMAIL_ | varchar(255) | YES |  |  | 邮箱 |
| PWD_ | varchar(255) | YES |  |  | 密码 |
| PICTURE_ID_ | varchar(64) | YES |  |  | 图片ID |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | ID_ | 是 | BTREE |

---

### act_procdef_info

**业务含义**：Activiti流程定义信息表 - 存储流程定义的额外信息

**数据量**：约0行 | 数据大小：0MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| ID_ | varchar(64) | NO |  | PRI | ID标识 |
| PROC_DEF_ID_ | varchar(64) | NO |  | UNI | 流程定义ID |
| REV_ | int(11) | YES |  |  | 版本号 |
| INFO_JSON_ID_ | varchar(64) | YES |  | MUL | 信息JSON ID |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| ACT_FK_INFO_JSON_BA | INFO_JSON_ID_ | 否 | BTREE |
| ACT_IDX_INFO_PROCDEF | PROC_DEF_ID_ | 否 | BTREE |
| ACT_UNIQ_INFO_PROCDEF | PROC_DEF_ID_ | 是 | BTREE |
| PRIMARY | ID_ | 是 | BTREE |

---

### act_re_deployment

**业务含义**：Activiti部署表 - 记录流程部署信息

**数据量**：约27行 | 数据大小：0MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| ID_ | varchar(64) | NO |  | PRI | ID标识 |
| NAME_ | varchar(255) | YES |  |  | 资源名称 |
| CATEGORY_ | varchar(255) | YES |  |  | 分类 |
| TENANT_ID_ | varchar(255) | YES |  |  | 租户ID |
| DEPLOY_TIME_ | timestamp(3) | YES |  |  | 部署时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | ID_ | 是 | BTREE |

---

### act_re_model

**业务含义**：Activiti模型表 - 存储流程模型信息

**数据量**：约6行 | 数据大小：0MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| ID_ | varchar(64) | NO |  | PRI | ID标识 |
| REV_ | int(11) | YES |  |  | 版本号 |
| NAME_ | varchar(255) | YES |  |  | 资源名称 |
| KEY_ | varchar(255) | YES |  |  | 部署标识 |
| CATEGORY_ | varchar(255) | YES |  |  | 分类 |
| VERSION_ | int(11) | YES |  |  | 版本号 |
| META_INFO_ | varchar(4000) | YES |  |  | 元信息 |
| DEPLOYMENT_ID_ | varchar(64) | YES |  | MUL | 部署ID |
| EDITOR_SOURCE_VALUE_ID_ | varchar(64) | YES |  | MUL | 编辑器源值ID |
| EDITOR_SOURCE_EXTRA_VALUE_ID_ | varchar(64) | YES |  | MUL | 编辑器扩展源值ID |
| TENANT_ID_ | varchar(255) | YES |  |  | 租户ID |
| CREATE_TIME_ | timestamp(3) | YES |  |  | 创建时间 |
| LAST_UPDATE_TIME_ | timestamp(3) | YES |  |  | 最后更新时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| ACT_FK_MODEL_DEPLOYMENT | DEPLOYMENT_ID_ | 否 | BTREE |
| ACT_FK_MODEL_SOURCE | EDITOR_SOURCE_VALUE_ID_ | 否 | BTREE |
| ACT_FK_MODEL_SOURCE_EXTRA | EDITOR_SOURCE_EXTRA_VALUE_ID_ | 否 | BTREE |
| PRIMARY | ID_ | 是 | BTREE |

---

### act_re_procdef

**业务含义**：Activiti流程定义表 - 存储流程定义信息

**数据量**：约27行 | 数据大小：0MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| ID_ | varchar(64) | NO |  | PRI | ID标识 |
| REV_ | int(11) | YES |  |  | 版本号 |
| CATEGORY_ | varchar(255) | YES |  |  | 分类 |
| NAME_ | varchar(255) | YES |  |  | 资源名称 |
| KEY_ | varchar(255) | NO |  | MUL | 部署标识 |
| VERSION_ | int(11) | NO |  |  | 版本号 |
| DEPLOYMENT_ID_ | varchar(64) | YES |  |  | 部署ID |
| RESOURCE_NAME_ | varchar(4000) | YES |  |  | 资源文件名 |
| DGRM_RESOURCE_NAME_ | varchar(4000) | YES |  |  | 流程图资源文件名 |
| DESCRIPTION_ | varchar(4000) | YES |  |  | 描述 |
| HAS_START_FORM_KEY_ | tinyint(4) | YES |  |  | 是否有开始表单键 |
| SUSPENSION_STATE_ | int(11) | YES |  |  | 挂起状态 |
| TENANT_ID_ | varchar(255) | YES |  |  | 租户ID |
| HAS_GRAPHICAL_NOTATION_ | tinyint(4) | YES |  |  | 是否有图形化标记 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| ACT_UNIQ_PROCDEF | KEY_, VERSION_, TENANT_ID_ | 是 | BTREE |
| PRIMARY | ID_ | 是 | BTREE |

---

### act_ru_event_subscr

**业务含义**：Activiti运行时事件订阅表 - 记录运行时的事件订阅信息

**数据量**：约0行 | 数据大小：0MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| ID_ | varchar(64) | NO |  | PRI | ID标识 |
| REV_ | int(11) | YES |  |  | 版本号 |
| EVENT_TYPE_ | varchar(255) | NO |  |  | 事件类型 |
| EVENT_NAME_ | varchar(255) | YES |  |  | 事件名称 |
| EXECUTION_ID_ | varchar(64) | YES |  | MUL | 执行ID |
| PROC_INST_ID_ | varchar(64) | YES |  |  | 流程实例ID |
| ACTIVITY_ID_ | varchar(64) | YES |  |  | 活动ID |
| CONFIGURATION_ | varchar(255) | YES |  | MUL | 配置 |
| TENANT_ID_ | varchar(255) | YES |  |  | 租户ID |
| PROC_DEF_ID_ | varchar(64) | YES |  |  | 流程定义ID |
| CREATED_ | timestamp(3) | NO | CURRENT_TIMESTAMP(3) |  | 创建时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| ACT_FK_EVENT_EXEC | EXECUTION_ID_ | 否 | BTREE |
| ACT_IDX_EVENT_SUBSCR_CONFIG_ | CONFIGURATION_ | 否 | BTREE |
| PRIMARY | ID_ | 是 | BTREE |

---

### act_ru_execution

**业务含义**：Activiti运行时执行实例表 - 记录流程的运行时执行路径

**数据量**：约3,554行 | 数据大小：0.4MB | 索引大小：0.7MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| ID_ | varchar(64) | NO |  | PRI | ID标识 |
| REV_ | int(11) | YES |  |  | 版本号 |
| PROC_INST_ID_ | varchar(64) | YES |  | MUL | 流程实例ID |
| BUSINESS_KEY_ | varchar(255) | YES |  | MUL | 业务键 |
| PARENT_ID_ | varchar(64) | YES |  | MUL | 父执行ID |
| PROC_DEF_ID_ | varchar(64) | YES |  | MUL | 流程定义ID |
| SUPER_EXEC_ | varchar(64) | YES |  | MUL | 父执行ID |
| ACT_ID_ | varchar(255) | YES |  |  | 活动节点ID |
| IS_ACTIVE_ | tinyint(4) | YES |  |  | 是否活跃 |
| IS_CONCURRENT_ | tinyint(4) | YES |  |  | 是否并发 |
| IS_SCOPE_ | tinyint(4) | YES |  |  | 是否作用域 |
| IS_EVENT_SCOPE_ | tinyint(4) | YES |  |  | 是否事件作用域 |
| SUSPENSION_STATE_ | int(11) | YES |  |  | 挂起状态 |
| CACHED_ENT_STATE_ | int(11) | YES |  |  | 缓存实体状态 |
| TENANT_ID_ | varchar(255) | YES |  |  | 租户ID |
| NAME_ | varchar(255) | YES |  |  | 资源名称 |
| LOCK_TIME_ | timestamp(3) | YES |  |  | 锁获取时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| ACT_FK_EXE_PARENT | PARENT_ID_ | 否 | BTREE |
| ACT_FK_EXE_PROCDEF | PROC_DEF_ID_ | 否 | BTREE |
| ACT_FK_EXE_PROCINST | PROC_INST_ID_ | 否 | BTREE |
| ACT_FK_EXE_SUPER | SUPER_EXEC_ | 否 | BTREE |
| ACT_IDX_EXEC_BUSKEY | BUSINESS_KEY_ | 否 | BTREE |
| PRIMARY | ID_ | 是 | BTREE |

---

### act_ru_identitylink

**业务含义**：Activiti运行时身份关联表 - 记录运行时参与者与任务的关联

**数据量**：约23,196行 | 数据大小：1.5MB | 索引大小：4.1MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| ID_ | varchar(64) | NO |  | PRI | ID标识 |
| REV_ | int(11) | YES |  |  | 版本号 |
| GROUP_ID_ | varchar(255) | YES |  | MUL | 组ID |
| TYPE_ | varchar(255) | YES |  |  | 事件类型 |
| USER_ID_ | varchar(255) | YES |  | MUL | 用户ID |
| TASK_ID_ | varchar(64) | YES |  | MUL | 任务ID |
| PROC_INST_ID_ | varchar(64) | YES |  | MUL | 流程实例ID |
| PROC_DEF_ID_ | varchar(64) | YES |  | MUL | 流程定义ID |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| ACT_FK_IDL_PROCINST | PROC_INST_ID_ | 否 | BTREE |
| ACT_FK_TSKASS_TASK | TASK_ID_ | 否 | BTREE |
| ACT_IDX_ATHRZ_PROCEDEF | PROC_DEF_ID_ | 否 | BTREE |
| ACT_IDX_IDENT_LNK_GROUP | GROUP_ID_ | 否 | BTREE |
| ACT_IDX_IDENT_LNK_USER | USER_ID_ | 否 | BTREE |
| PRIMARY | ID_ | 是 | BTREE |

---

### act_ru_job

**业务含义**：Activiti运行时作业表 - 记录定时器和异步作业

**数据量**：约0行 | 数据大小：0MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| ID_ | varchar(64) | NO |  | PRI | ID标识 |
| REV_ | int(11) | YES |  |  | 版本号 |
| TYPE_ | varchar(255) | NO |  |  | 事件类型 |
| LOCK_OWNER_ | varchar(255) | YES |  |  | 锁持有者 |
| EXCLUSIVE_ | tinyint(1) | YES |  |  | 是否排他 |
| EXECUTION_ID_ | varchar(64) | YES |  |  | 执行ID |
| PROCESS_INSTANCE_ID_ | varchar(64) | YES |  |  | 流程实例ID |
| PROC_DEF_ID_ | varchar(64) | YES |  |  | 流程定义ID |
| RETRIES_ | int(11) | YES |  |  | 重试次数 |
| EXCEPTION_STACK_ID_ | varchar(64) | YES |  | MUL | 异常堆栈ID |
| EXCEPTION_MSG_ | varchar(4000) | YES |  |  | 异常消息 |
| REPEAT_ | varchar(255) | YES |  |  | 重复表达式 |
| HANDLER_TYPE_ | varchar(255) | YES |  |  | 处理器类型 |
| HANDLER_CFG_ | varchar(4000) | YES |  |  | 处理器配置 |
| TENANT_ID_ | varchar(255) | YES |  |  | 租户ID |
| LOCK_EXP_TIME_ | timestamp(3) | YES |  |  | 锁过期时间 |
| DUEDATE_ | timestamp(3) | YES |  |  | 到期时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| ACT_FK_JOB_EXCEPTION | EXCEPTION_STACK_ID_ | 否 | BTREE |
| PRIMARY | ID_ | 是 | BTREE |

---

### act_ru_task

**业务含义**：Activiti运行时任务表 - 记录当前待办任务

**数据量**：约3,400行 | 数据大小：0.5MB | 索引大小：0.5MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| ID_ | varchar(64) | NO |  | PRI | ID标识 |
| REV_ | int(11) | YES |  |  | 版本号 |
| EXECUTION_ID_ | varchar(64) | YES |  | MUL | 执行ID |
| PROC_INST_ID_ | varchar(64) | YES |  | MUL | 流程实例ID |
| PROC_DEF_ID_ | varchar(64) | YES |  | MUL | 流程定义ID |
| NAME_ | varchar(255) | YES |  |  | 资源名称 |
| PARENT_TASK_ID_ | varchar(64) | YES |  |  | 父任务ID |
| DESCRIPTION_ | varchar(4000) | YES |  |  | 描述 |
| TASK_DEF_KEY_ | varchar(255) | YES |  |  | 任务定义键 |
| OWNER_ | varchar(255) | YES |  |  | 任务拥有者 |
| ASSIGNEE_ | varchar(255) | YES |  | MUL | 办理人 |
| DELEGATION_ | varchar(64) | YES |  |  | 委托状态 |
| PRIORITY_ | int(11) | YES |  |  | 优先级 |
| SUSPENSION_STATE_ | int(11) | YES |  |  | 挂起状态 |
| CATEGORY_ | varchar(255) | YES |  |  | 分类 |
| TENANT_ID_ | varchar(255) | YES |  |  | 租户ID |
| CREATE_TIME_ | timestamp(3) | YES |  |  | 创建时间 |
| DUE_DATE_ | datetime(3) | YES |  |  | 到期日期 |
| FORM_KEY_ | varchar(255) | YES |  |  | 表单键 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| ACT_FK_TASK_EXE | EXECUTION_ID_ | 否 | BTREE |
| ACT_FK_TASK_PROCDEF | PROC_DEF_ID_ | 否 | BTREE |
| ACT_FK_TASK_PROCINST | PROC_INST_ID_ | 否 | BTREE |
| ACT_IDX_ASSIGNEE | ASSIGNEE_ | 否 | BTREE |
| PRIMARY | ID_ | 是 | BTREE |

---

### act_ru_variable

**业务含义**：Activiti运行时变量表 - 记录流程的运行时变量

**数据量**：约42,146行 | 数据大小：4.5MB | 索引大小：8.0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| ID_ | varchar(64) | NO |  | PRI | ID标识 |
| REV_ | int(11) | YES |  |  | 版本号 |
| TYPE_ | varchar(255) | NO |  |  | 事件类型 |
| NAME_ | varchar(255) | NO |  |  | 资源名称 |
| EXECUTION_ID_ | varchar(64) | YES |  | MUL | 执行ID |
| PROC_INST_ID_ | varchar(64) | YES |  | MUL | 流程实例ID |
| TASK_ID_ | varchar(64) | YES |  | MUL | 任务ID |
| BYTEARRAY_ID_ | varchar(64) | YES |  | MUL | 字节数组ID |
| DOUBLE_ | double | YES |  |  | 双精度值 |
| LONG_ | bigint(20) | YES |  |  | 长整型值 |
| TEXT_ | varchar(4000) | YES |  |  | 文本值 |
| TEXT2_ | varchar(4000) | YES |  |  | 文本值2 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| ACT_FK_VAR_BYTEARRAY | BYTEARRAY_ID_ | 否 | BTREE |
| ACT_FK_VAR_EXE | EXECUTION_ID_ | 否 | BTREE |
| ACT_FK_VAR_PROCINST | PROC_INST_ID_ | 否 | BTREE |
| ACT_IDX_VARIABLE_TASK_ID | TASK_ID_ | 否 | BTREE |
| PRIMARY | ID_ | 是 | BTREE |

---

### dp_v_spms_department

**对象类型**：VIEW

**业务含义**：数据平台SPMS部门视图 - 提供部门信息的统一查询视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| ocrCode | varchar(25) | NO |  |  | OCR编码 |
| ocrName | varchar(25) | NO |  |  | OCR名称 |
| isparam | int(11) | YES |  |  | 是否参数 |

---

### dp_v_spms_item_basic_info

**对象类型**：VIEW

**业务含义**：数据平台SPMS物料基本信息视图 - 提供物料基本信息的统一查询视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| itemCode | varchar(25) | YES |  |  | 项目编码 |
| itemName | varchar(255) | YES |  |  | 项目名称 |

---

### dp_v_spms_rma_remind

**对象类型**：VIEW

**业务含义**：数据平台SPMS RMA提醒视图 - 提供RMA退货提醒信息的统一查询视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| spare_serialNum | varchar(50) | YES |  |  | 备件序列号 |
| sheetID | varchar(25) | YES |  |  | 工单ID |
| back_type | varchar(50) | YES |  |  | back类型 |
| item_name | varchar(255) | YES |  |  | 项目名称 |
| project_name | varchar(255) | YES |  |  | 项目名称 |
| problem_desc | text | YES |  |  | 问题描述 |
| conk_out_time | varchar(25) | YES |  |  | conk_out时间 |
| approve_time | datetime | YES |  |  | 审批通过时间 |

---

### ehr_company

**业务含义**：EHR公司表 - 存储公司组织信息

**数据量**：约3行 | 数据大小：0MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| compID | int(11) | NO |  | PRI | 公司ID |
| compCode | varchar(10) | YES |  |  | 公司编码 |
| compName | varchar(100) | YES |  |  | 公司名称 |
| compAbbr | varchar(100) | YES |  |  | 公司简称 |
| adminID | int(11) | YES |  | MUL | 管理员ID |
| compGrade | int(11) | YES |  |  | 公司等级 |
| compType | int(11) | YES |  |  | 公司类型 |
| compArea | int(11) | YES |  |  | 公司区域 |
| effectDate | datetime | YES |  |  | 生效日期 |
| lawyer | varchar(50) | YES |  |  | 律师 |
| address | varchar(200) | YES |  |  | 地址 |
| regAddress | varchar(200) | YES |  |  | 注册地址 |
| tel | varchar(50) | YES |  |  | 电话 |
| fax | varchar(50) | YES |  |  | 传真 |
| postCode | varchar(50) | YES |  |  | 邮政编码 |
| webSite | varchar(100) | YES |  |  | 网站地址 |
| isDisabled | bit(1) | YES |  |  | 是否禁用 |
| disabledDate | datetime | YES |  |  | 禁用日期 |
| remark | varchar(500) | YES |  |  | 备注 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| adminID | adminID | 否 | BTREE |
| PRIMARY | compID | 是 | BTREE |

---

### ehr_department

**业务含义**：EHR部门表 - 存储部门组织架构信息

**数据量**：约517行 | 数据大小：0.1MB | 索引大小：0.1MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| depID | int(11) | NO |  | PRI | 部门ID |
| depCode | varchar(20) | YES |  |  | 部门编码 |
| depName | varchar(100) | YES |  |  | 部门名称 |
| depAbbr | varchar(100) | YES |  |  | 部门简称 |
| compID | int(11) | YES |  | MUL | 公司ID |
| adminID | int(11) | YES |  | MUL | 管理员ID |
| depGrade | int(11) | YES |  |  | 部门等级 |
| depType | int(11) | YES |  |  | 部门类型 |
| depProperty | int(11) | YES |  |  | 部门属性 |
| depCost | int(11) | YES |  |  | 部门成本中心 |
| director | int(11) | YES |  | MUL | 总监 |
| director2 | int(11) | YES |  | MUL | 副主管 |
| depEmp | int(11) | YES |  |  | 部门人数 |
| depNum | int(11) | YES |  |  | 部门编号 |
| effectDate | datetime | YES |  |  | 生效日期 |
| xOrder | varchar(20) | YES |  |  | 排序 |
| isDisabled | bit(1) | YES |  |  | 是否禁用 |
| disabledDate | datetime | YES |  |  | 禁用日期 |
| remark | varchar(500) | YES |  |  | 备注 |
| depCustom1 | int(11) | YES |  |  | 部门自定义1 |
| depCustom2 | int(11) | YES |  |  | 部门自定义2 |
| depCustom3 | int(11) | YES |  |  | 部门自定义3 |
| depCustom4 | int(11) | YES |  |  | 部门自定义4 |
| depCustom5 | int(11) | YES |  |  | 部门自定义5 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| adminID | adminID | 否 | BTREE |
| compID | compID | 否 | BTREE |
| director | director | 否 | BTREE |
| director2 | director2 | 否 | BTREE |
| PRIMARY | depID | 是 | BTREE |

---

### ehr_employee

**业务含义**：EHR员工表 - 存储员工基本信息

**数据量**：约4,831行 | 数据大小：1.5MB | 索引大小：0.7MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| empID | int(11) | NO |  | PRI | 员工ID |
| workNo | varchar(100) | NO |  | MUL | 工号 |
| name | varchar(200) | YES |  |  | 名称 |
| eName | varchar(200) | YES |  |  | 英文名称 |
| compID | int(11) | NO |  | MUL | 公司ID |
| depID | int(11) | NO |  | MUL | 部门ID |
| jobID | int(11) | NO |  | MUL | 岗位ID |
| reportTo | int(11) | YES |  | MUL | 汇报对象 |
| wfreportTo | int(11) | YES |  | MUL | 流程汇报对象 |
| empStatus | int(11) | NO |  |  | 员工状态 |
| jobStatus | int(11) | YES |  |  | 岗位状态 |
| empType | int(11) | YES |  |  | 员工类型 |
| joinDate | datetime | YES |  |  | 入职日期 |
| workBeginDate | datetime | YES |  |  | 工作开始日期 |
| jobBeginDate | datetime | YES |  |  | 岗位开始日期 |
| pracBeginDate | datetime | YES |  |  | 实习开始日期 |
| pracEndDate | datetime | YES |  |  | 实习结束日期 |
| probBeginDate | datetime | YES |  |  | 试用期开始日期 |
| probEndDate | datetime | YES |  |  | 试用期结束日期 |
| leaveDate | datetime | YES |  |  | 离职日期 |
| gender | int(11) | YES |  |  | 性别 |
| email | varchar(500) | YES |  |  | 邮箱 |
| mobile | varchar(50) | YES |  |  | 手机 |
| officePhone | varchar(50) | YES |  |  | 办公电话 |
| remark | varchar(100) | YES |  |  | 备注 |
| disabled | int(11) | YES | 0 |  | 是否禁用 |
| empCustom1 | int(11) | YES |  |  | 员工自定义1 |
| empCustom2 | int(11) | YES |  |  | 员工自定义2 |
| empCustom3 | int(11) | YES |  |  | 员工自定义3 |
| empCustom4 | varchar(50) | YES |  |  | 员工自定义4 |
| empCustom5 | int(11) | YES |  |  | 员工自定义5 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| compID | compID | 否 | BTREE |
| depID | depID | 否 | BTREE |
| jobID | jobID | 否 | BTREE |
| PRIMARY | empID | 是 | BTREE |
| reportTo | reportTo | 否 | BTREE |
| wfreportTo | wfreportTo | 否 | BTREE |
| workNo | workNo | 否 | BTREE |

---

### ehr_emp_power

**业务含义**：EHR员工权限表 - 存储员工系统权限配置

**数据量**：约127行 | 数据大小：0.1MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识 |
| empID | int(11) | NO |  | UNI | 员工ID |
| workNo | varchar(25) | NO |  | MUL | 工号 |
| compID | int(11) | NO |  |  | 公司ID |
| depIDs | varchar(4096) | NO |  |  | 部门ID列表 |
| extraDepIDs | varchar(4096) | NO |  |  | 额外部门ID列表 |
| adminDepIDs | varchar(4096) | NO |  |  | 管理部门ID列表 |
| empIDs | varchar(4096) | NO |  |  | 员工ID列表 |
| extraEmpIDs | varchar(4096) | NO |  |  | 额外员工ID列表 |
| state | bit(1) | NO | b'1' |  | 状态 |
| createBy | varchar(25) | NO |  |  | 创建人 |
| createTime | datetime | NO | CURRENT_TIMESTAMP |  | 创建时间 |
| updateBy | varchar(25) | YES |  |  | 更新人 |
| updateTime | datetime | YES |  |  | 更新时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| empID | empID | 是 | BTREE |
| PRIMARY | id | 是 | BTREE |
| workNo | workNo | 否 | BTREE |

---

### ehr_job

**业务含义**：EHR岗位表 - 存储岗位信息

**数据量**：约245行 | 数据大小：0MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| jobID | int(11) | NO |  | PRI | 岗位ID |
| jobCode | varchar(10) | YES |  |  | 岗位编码 |
| jobName | varchar(100) | YES |  |  | 岗位名称 |
| jobAbbr | varchar(100) | YES |  |  | 岗位简称 |
| depID | int(11) | YES |  | MUL | 部门ID |
| adminID | int(11) | YES |  | MUL | 管理员ID |
| jobGrage | int(11) | YES |  |  | 岗位等级 |
| jobType | int(11) | YES |  |  | 岗位类型 |
| jobProperty | int(11) | YES |  |  | 岗位属性 |
| jobNum | int(11) | YES |  |  | 岗位数量 |
| isCore | bit(1) | YES | b'0' |  | 是否Core |
| effectDate | datetime | NO |  |  | 生效日期 |
| xorder | varchar(20) | YES |  |  | 排序 |
| isDisabled | bit(1) | YES | b'0' |  | 是否禁用 |
| disabledDate | datetime | YES |  |  | 禁用日期 |
| remark | varchar(500) | YES |  |  | 备注 |
| xType | int(11) | YES |  |  | x类型 |
| jobCustom1 | int(11) | YES |  |  | 岗位自定义1 |
| jobCustom2 | int(11) | YES |  |  | 岗位自定义2 |
| jobCustom3 | int(11) | YES |  |  | 岗位自定义3 |
| jobCustom4 | int(11) | YES |  |  | 岗位自定义4 |
| jobCustom5 | int(11) | YES |  |  | 岗位自定义5 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| adminID | adminID | 否 | BTREE |
| depID | depID | 否 | BTREE |
| PRIMARY | jobID | 是 | BTREE |

---

### ehr_login

**业务含义**：EHR登录表 - 存储用户登录信息

**数据量**：约3,224行 | 数据大小：0.3MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识 |
| title | varchar(255) | YES |  |  | 头衔 |
| account | varchar(255) | YES |  |  | 账户 |
| empID | int(11) | YES |  |  | 员工ID |
| workNo | varchar(255) | YES |  |  | 工号 |
| name | varchar(255) | YES |  |  | 名称 |
| isDisabled | int(11) | YES |  |  | 是否禁用 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### fb_contract

**业务含义**：Firebird迁移合同表 - 从Firebird迁移的合同数据

**数据量**：约104,743行 | 数据大小：23.5MB | 索引大小：29.1MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| contract_id | varchar(64) | YES |  | MUL | 合同ID |
| contract_code | varchar(25) | YES |  | MUL | 合同编码 |
| office_code | varchar(15) | YES |  | MUL | office编码 |
| contract_type | int(11) | YES |  | MUL | 合同类型 |
| customer_name | varchar(512) | YES |  |  | 客户名称 |
| project_name | varchar(512) | YES |  |  | 项目名称 |
| warranty | varchar(2) | YES |  |  | 质保 |
| marketCode | varchar(10) | YES |  |  | market编码 |
| marketName | varchar(15) | YES |  |  | market名称 |
| systemId | int(11) | YES |  |  | systemID |
| systemName | varchar(15) | YES |  |  | system名称 |
| remark | varchar(4096) | YES |  |  | 备注 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| contract_code | contract_code | 否 | BTREE |
| contract_type | contract_type | 否 | BTREE |
| fb_contract_contract_id_IDX | contract_id, office_code | 否 | BTREE |
| office_code_IDX | office_code, contract_id | 否 | BTREE |

---

### fb_ft_result1

**业务含义**：Firebird迁移FT结果1表 - 从Firebird迁移的FT测试结果数据1

**数据量**：约193,752行 | 数据大小：10.5MB | 索引大小：6.5MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| item_id | int(11) | YES |  | MUL | 项目ID |
| serial_number | varchar(100) | YES |  |  | 序列号number |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| item_id | item_id | 否 | BTREE |

---

### fb_ft_result2

**业务含义**：Firebird迁移FT结果2表 - 从Firebird迁移的FT测试结果数据2

**数据量**：约496,626行 | 数据大小：298.8MB | 索引大小：17.5MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| result1_id | int(11) | YES |  | MUL | result1ID |
| result_desc | text | YES |  |  | 结果描述 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| result1_id | result1_id | 否 | BTREE |

---

### fb_items

**业务含义**：Firebird迁移物料表 - 从Firebird迁移的物料数据

**数据量**：约32,188行 | 数据大小：3.5MB | 索引大小：6.6MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | YES |  |  | ID标识 |
| item | varchar(25) | YES |  | MUL | 项目 |
| describe_ | varchar(255) | YES |  |  | describe |
| itemname | varchar(255) | YES |  | MUL | 项目名称 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| cover_index | item, itemname, describe_ | 否 | BTREE |
| itemname | itemname | 否 | BTREE |

---

### fb_items2

**业务含义**：Firebird迁移物料2表 - 从Firebird迁移的物料补充数据

**数据量**：约19,357行 | 数据大小：2.5MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | YES |  |  | ID标识 |
| item | varchar(15) | YES |  |  | 项目 |
| describe_ | varchar(150) | YES |  |  | describe |
| itemname | varchar(255) | YES |  |  | 项目名称 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|

---

### fb_market_system

**业务含义**：Firebird迁移市场体系表 - 从Firebird迁移的市场体系数据

**数据量**：约14行 | 数据大小：0MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| marketCode | varchar(10) | YES |  |  | market编码 |
| marketName | varchar(15) | YES |  |  | market名称 |
| systemId | int(11) | YES |  |  | systemID |
| systemName | varchar(15) | YES |  |  | system名称 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|

---

### fb_office_relationship

**业务含义**：Firebird迁移办事处关系表 - 从Firebird迁移的办事处关系数据

**数据量**：约659行 | 数据大小：0.1MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识 |
| contractNo | varchar(100) | YES |  | MUL | 合同编号 |
| officeCode | varchar(25) | YES |  |  | 办公编码 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| contractNo | contractNo | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### fb_service

**业务含义**：Firebird迁移服务表 - 从Firebird迁移的服务数据

**数据量**：约95,878行 | 数据大小：12.5MB | 索引大小：18.6MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | varchar(64) | YES |  | MUL | ID标识 |
| con_xb | varchar(25) | YES |  | MUL | conxb |
| barcode | varchar(50) | YES |  | MUL | 条码 |
| grade | varchar(15) | YES |  |  | 等级 |
| begin_date | datetime | YES |  |  | begin日期 |
| end_date | datetime | YES |  |  | end日期 |
| warranty | char(1) | YES |  |  | 质保 |
| remark | text | YES |  |  | 备注 |
| isyb | int(11) | YES | 1 |  | 是否yb |
| state | int(11) | YES |  |  | 状态 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| barcode_ | barcode | 否 | BTREE |
| con_xb | con_xb | 否 | BTREE |
| id | id | 否 | BTREE |

---

### fb_shipment

**业务含义**：Firebird迁移发货表 - 从Firebird迁移的发货数据

**数据量**：约140,962行 | 数据大小：17.5MB | 索引大小：64.4MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| packlist_id | varchar(64) | YES |  | MUL | packlistID |
| con_id | varchar(64) | YES |  | MUL | conID |
| packdate | datetime | YES |  | MUL | 包装日期 |
| warrantyStartTime | datetime | YES |  |  | warrantyStart时间 |
| warrantyEndTime | datetime | YES |  |  | warranty结束时间 |
| receiveName | text | YES |  |  | receive名称 |
| emsNum | text | YES |  |  | ems数量 |
| emsCompany | text | YES |  |  | emsCompany |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| fb_shipment_con_id_IDX | con_id, packlist_id | 否 | BTREE |
| fb_shipment_packdate_IDX | packdate, con_id, packlist_id | 否 | BTREE |
| fb_shipment_packlist_id_IDX | packlist_id, con_id | 否 | BTREE |

---

### fb_shipment_barcode

**业务含义**：Firebird迁移发货条码表 - 从Firebird迁移的发货条码数据

**数据量**：约3,541,100行 | 数据大小：612.0MB | 索引大小：2369.0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | bigint(20) | NO |  | PRI | ID标识 |
| pack_id | varchar(64) | YES |  | MUL | packID |
| item | varchar(16) | YES |  | MUL | 项目 |
| barcode | varchar(50) | YES |  | MUL | 条码 |
| com_barcode | varchar(50) | YES |  |  | combarcode |
| rma_no | varchar(64) | YES |  |  | rma编号 |
| isRMA | int(11) | YES |  |  | 是否RMA |
| item2 | varchar(16) | YES |  | MUL | 项目2 |
| barcode2 | varchar(50) | YES |  | MUL | 条码2 |
| orderNumber | varchar(32) | YES |  |  | 排序Number |
| lineNum | int(11) | YES |  |  | line数量 |
| profitCenter | varchar(32) | YES |  |  | profitCenter |
| soleAgentSuffix | varchar(32) | YES |  |  | soleAgentSuffix |
| warrantyStartDate | date | YES |  |  | 质保开始日期 |
| warrantyMonth | int(11) | YES |  |  | warrantyMonth |
| rmaBarcode | varchar(50) | YES |  |  | rmaBarcode |
| updateTime | datetime | YES |  |  | 更新时间 |
| syncTime | datetime | YES | CURRENT_TIMESTAMP |  | sync时间 |
| uuid | varchar(64) | YES |  | UNI | UUID标识 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| barcode2 | barcode2 | 否 | BTREE |
| barcode_pack_rma_IDX | barcode, pack_id, rma_no | 否 | BTREE |
| barcode_rma_pack_IDX | barcode, rma_no, pack_id | 否 | BTREE |
| item | item | 否 | BTREE |
| item2 | item2 | 否 | BTREE |
| pack_barcode_IDX | pack_id, barcode, rma_no | 否 | BTREE |
| pack_item_IDX | pack_id, item, rma_no | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| uuid | uuid | 是 | BTREE |

---

### fb_shipment_barcode_change_log

**业务含义**：Firebird迁移发货条码变更日志表 - 从Firebird迁移的条码变更日志

**数据量**：约528,607行 | 数据大小：547.0MB | 索引大小：120.0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| logID | bigint(20) | NO |  | PRI | logID |
| tableName | varchar(128) | YES |  |  | 表名 |
| operation | varchar(50) | YES |  |  | 操作 |
| changedBy | varchar(128) | YES |  |  | changedBy |
| changeTime | datetime | YES |  |  | change时间 |
| dataId | varchar(128) | YES |  | MUL | dataID |
| barCode | varchar(128) | YES |  |  | bar编码 |
| lasted | smallint(6) | YES |  | MUL | 持续时间 |
| oldValues | longtext | YES |  |  | oldValues |
| newValues | longtext | YES |  |  | newValues |
| syncTime | datetime | YES | CURRENT_TIMESTAMP |  | sync时间 |
| syncFlag | smallint(6) | YES | 0 | MUL | sync标记 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| idx_dataid_lasted_logid | dataId, lasted, logID | 否 | BTREE |
| idx_lasted_dataid_logid | lasted, dataId, logID | 否 | BTREE |
| idx_syncFlag_lasted | syncFlag, lasted | 否 | BTREE |
| PRIMARY | logID | 是 | BTREE |

---

### fb_shipment_barcode_order_line

**业务含义**：Firebird迁移发货条码订单行表 - 从Firebird迁移的条码订单行数据

**数据量**：约2,576,429行 | 数据大小：373.0MB | 索引大小：568.0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| pack_id | varchar(64) | YES |  | MUL | packID |
| packlist_no | varchar(64) | YES |  |  | packlist编号 |
| barcode | varchar(50) | YES |  | MUL | 条码 |
| contractNo | varchar(50) | YES |  |  | 合同编号 |
| orderNumber | varchar(32) | YES |  | MUL | 排序Number |
| lineNum | int(11) | YES |  |  | line数量 |
| orderQty | int(11) | YES |  |  | 排序Qty |
| deliveredQty | int(11) | YES |  |  | deliveredQty |
| profitCenter | varchar(32) | YES |  |  | profitCenter |
| orderExecNumber | varchar(50) | YES |  |  | 排序ExecNumber |
| soleAgentSuffix | varchar(32) | YES |  |  | soleAgentSuffix |
| warrantyMonth | int(11) | YES | 0 |  | warrantyMonth |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| barcode | barcode | 否 | BTREE |
| orderNumber | orderNumber, lineNum | 否 | BTREE |
| pack_id | pack_id, barcode | 否 | BTREE |

---

### fb_shipment_barcode_relation

**业务含义**：Firebird迁移发货条码关联表 - 从Firebird迁移的条码关联数据

**数据量**：约55,130行 | 数据大小：7.5MB | 索引大小：19.1MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识 |
| sn1 | varchar(50) | YES |  | MUL | 序列号1 |
| item1 | varchar(15) | YES |  | MUL | 项目1 |
| sn2 | varchar(50) | YES |  | MUL | 序列号2 |
| item2 | varchar(15) | YES |  | MUL | 项目2 |
| contract | varchar(25) | YES |  | MUL | 合同 |
| createtime | varchar(50) | YES |  |  | 创建时间 |
| updatetime | varchar(50) | YES |  |  | 更新时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| contract_sn1_IDX | contract, sn1 | 否 | BTREE |
| item1 | item1 | 否 | BTREE |
| item2 | item2 | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| sn1 | sn1 | 否 | BTREE |
| sn2 | sn2 | 否 | BTREE |

---

### fb_soft_version

**业务含义**：Firebird迁移软件版本表 - 从Firebird迁移的软件版本数据

**数据量**：约161,015行 | 数据大小：13.5MB | 索引大小：31.6MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| serial_number | varchar(100) | YES |  | MUL | 序列号number |
| conp | varchar(100) | YES |  | MUL | CONP版本 |
| cpld | varchar(100) | YES |  | MUL | CPLD版本 |
| boot | varchar(100) | YES |  | MUL | BOOT版本 |
| pcb | varchar(100) | YES |  | MUL | PCB版本 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| boot | boot | 否 | BTREE |
| conp | conp | 否 | BTREE |
| cpld | cpld | 否 | BTREE |
| pcb | pcb | 否 | BTREE |
| serial_number | serial_number | 否 | BTREE |

---

### fb_warranty_grade

**业务含义**：Firebird迁移维保等级表 - 从Firebird迁移的维保等级数据

**数据量**：约11行 | 数据大小：0MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识 |
| gradecode | varchar(25) | YES |  | MUL | 等级编码 |
| gradename | varchar(125) | YES |  |  | 等级名称 |
| gradestatus | int(11) | YES | 0 |  | 等级状态 |
| sort | int(3) | YES | 0 |  | 排序 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| gradecode | gradecode | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### addressee_info

**业务含义**：收件人信息表

**数据量**：约 2,935 行 | 数据大小：0.4 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| addre_id | int(11) | NO |  | PRI | ID（自增） |
| username | varchar(25) | NO |  |  | 关联用户账号 |
| addre_name | varchar(64) | YES |  |  | 收件人姓名 |
| addre_tel | varchar(64) | YES |  |  | 收件人电话 |
| addre_mail | varchar(64) | YES |  |  | 收件人邮箱 |
| addr | varchar(1024) | YES |  |  | 地址/where |
| zip_code | varchar(10) | YES |  |  | 邮编 |
| company | varchar(64) | YES |  |  | 公司 |
| depName | varchar(25) | YES |  |  | 部门 |
| remark | text | YES |  |  | 备注 |
| state | int(11) | YES | 1 |  | 状态（生效或失效） |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | addre_id | 是 | BTREE |

---

### af_industry_asset

**业务含义**：行业资产表

**数据量**：约 658 行 | 数据大小：0.2 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| assetNum | varchar(255) | YES |  |  | 资产编号 |
| assetName | varchar(255) | YES |  |  | 资产名称 |
| assetCategory | varchar(25) | YES |  |  | 资产分类 |
| assetType | varchar(25) | NO |  |  | 资产类型 |
| assetHost | varchar(255) | YES |  |  | IP/URL地址/域名 |
| assetOpenPorts | varchar(255) | YES |  |  | 开放端口情况 |
| assetDeployInfo | varchar(1024) | YES |  |  | 部署应用情况 |
| assetUsage | varchar(255) | YES |  |  | 资产用途 |
| customerName | varchar(255) | YES |  |  | 单位名称 |
| industryCode | varchar(25) | YES |  |  | 所属行业 |
| assetAS | varchar(25) | YES |  |  | 应用系统 |
| assetASVersion | varchar(25) | YES |  |  | 应用系统版本号 |
| assetASIdentify | varchar(1024) | YES |  |  | 应用系统识别途径 |
| assetASFramework | varchar(25) | YES |  |  | 应用系统架构 |
| middlewareName | varchar(255) | YES |  |  | 中间件名称 |
| middlewareVersion | varchar(255) | YES |  |  | 中间件版本 |
| developerBrand | varchar(255) | YES |  |  | 开发商品牌 |
| assetOS | varchar(25) | YES |  |  | 操作系统 |
| assetOSVersion | varchar(255) | YES |  |  | 操作系统版本 |
| assetDB | varchar(25) | YES |  |  | 数据库类型 |
| assetDBVersion | varchar(255) | YES |  |  | 数据库版本 |
| customInfo | json | YES |  |  | 自定义信息 |
| status | varchar(25) | YES | 0 |  | 状态 |
| trackStatus | int(1) | YES | 0 |  | 入库状态 |
| trackedTime | datetime | YES |  |  | 入库时间 |
| disabled | bit(1) | YES | b'0' |  | 删除状态 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### af_industry_asset_leak_relation

**业务含义**：行业资产漏洞关联表

**数据量**：约 23 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| projectId | int(11) | NO | 0 | MUL | 项目ID |
| assetId | int(11) | NO |  | MUL | 资产ID |
| leakId | int(11) | NO |  |  | 漏洞ID |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |
| disabled | bit(1) | NO | b'0' |  | 删除标准 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| assetId | assetId | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| projectId | projectId | 否 | BTREE |

---

### af_industry_asset_project_relation

**业务含义**：行业资产项目关联表

**数据量**：约 658 行 | 数据大小：0.1 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| projectId | int(11) | NO |  | MUL | 项目ID |
| assetId | int(11) | NO |  | MUL | 资产ID |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |
| disabled | bit(1) | NO | b'0' |  | 删除标准 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| assetId | assetId | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| projectId | projectId | 否 | BTREE |

---

### af_industry_leak

**业务含义**：行业漏洞表

**数据量**：约 5 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| leakCode | varchar(255) | YES |  |  | 漏洞编号 |
| leakName | varchar(255) | YES |  |  | 漏洞名称 |
| leakType | varchar(25) | NO |  |  | 漏洞类型 |
| leakLevel | varchar(25) | YES |  |  | 漏洞级别 |
| leakDesc | varchar(1024) | YES |  |  | 漏洞描述 |
| industryCode | varchar(25) | YES |  |  | 所属行业 |
| leakSourceInfo | varchar(1024) | YES |  |  | 漏洞原始数据 |
| remark | varchar(1024) | YES |  |  | 备注 |
| status | varchar(25) | YES | 0 |  | 状态 |
| trackStatus | int(1) | YES | 0 |  | 入库状态 |
| trackedTime | datetime | YES |  |  | 入库时间 |
| disabled | bit(1) | YES | b'0' |  | 删除状态 |
| assetIds | varchar(255) | YES |  |  | 关联的资产ID |
| customInfo | json | YES |  |  | 自定义信息 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### af_industry_leak_warning

**业务含义**：行业漏洞预警表

**数据量**：约 2 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| leakName | varchar(255) | YES |  |  | 漏洞名称 |
| assetAS | varchar(25) | YES |  |  | 应用系统 |
| assetASVersion | varchar(25) | YES |  |  | 应用系统版本号 |
| assetASIdentify | varchar(1024) | YES |  |  | 应用系统识别途径 |
| assetASFramework | varchar(25) | YES |  |  | 应用系统架构 |
| middlewareName | varchar(255) | YES |  |  | 中间件名称 |
| middlewareVersion | varchar(255) | YES |  |  | 中间件版本 |
| developerBrand | varchar(255) | YES |  |  | 开发商品牌 |
| assetOS | varchar(25) | YES |  |  | 操作系统 |
| assetOSVersion | varchar(255) | YES |  |  | 操作系统版本 |
| assetDB | varchar(25) | YES |  |  | 数据库类型 |
| assetDBVersion | varchar(255) | YES |  |  | 数据库版本 |
| ports | varchar(255) | YES |  |  | 风险端口 |
| customInfo | json | YES |  |  | 自定义信息 |
| status | int(3) | YES |  |  | 状态 |
| trackStatus | int(1) | YES | 0 |  | 入库状态 |
| trackedTime | datetime | YES |  |  | 入库时间 |
| disabled | bit(1) | YES | b'0' |  | 删除状态 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### agent_info

**业务含义**：代理商信息表

**数据量**：约 35,204 行 | 数据大小：3.5 MB | 索引大小：1.5 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| agent_id | int(10) | NO |  | PRI | ID标识（自增） |
| id | varchar(16) | NO |  | MUL | ID标识 |
| name | varchar(64) | NO |  |  | 名称 |
| type | int(8) | NO |  |  | 类型 |
| level | varchar(64) | YES |  |  | 级别 |
| enable | int(8) | NO | 1 |  | 是否启用 |
| agent_version | int(8) | NO | 0 |  | agentversion |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| index_id | id | 否 | BTREE |
| PRIMARY | agent_id | 是 | BTREE |

---

### app_accessory_info

**业务含义**：附件信息表

**数据量**：约 680 行 | 数据大小：0.1 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| sheetID | varchar(25) | YES |  |  | 流水号 |
| accessoryName | varchar(255) | YES |  |  | 附件名称 |
| uploader | varchar(10) | YES |  |  | 上传者 |
| uploadTime | datetime | YES |  |  | 上传时间 |
| accessoryType | int(11) | YES |  |  | 附件类型  1 发货信息  -1 坏件返回信息 |
| accessoryPath | varchar(100) | YES |  |  | 上传路径 |
| data_creater | varchar(10) | YES |  |  | 创建信息 |
| data_creatime | datetime | YES |  |  | 时间 |
| data_updater | varchar(10) | YES |  |  | 更新信息 |
| data_updatime | datetime | YES |  |  | 时间 |
| data_from | datetime | YES |  |  | datafrom |
| data_to | datetime | YES |  |  | datato |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### app_comment

**业务含义**：审批评论表

**数据量**：约 23,016 行 | 数据大小：1.5 MB | 索引大小：0.4 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| sheetID | varchar(10) | YES |  | MUL | 单据代码 |
| is_pass | varchar(2) | YES |  |  | 是否通过审批(0为未处理，1为通过，2为未通过 , -1 作废处理)  |
| opinion | text | YES |  |  | 意见 |
| approve_time | datetime | YES |  |  | 审批时间 |
| approver | varchar(10) | YES |  |  | 审批人 |
| state | char(1) | YES |  |  | (1:为最新审批结果；0：为旧审批结果) |
| take_place | varchar(15) | YES | 0 |  | 0:未选择 1:供应链 2：库存 |
| isUnion | int(11) | YES |  |  | 是否联合供应链发货 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| sheetID | sheetID | 否 | BTREE |

---

### app_spare_part

**业务含义**：（待补充）

**数据量**：约 47,262 行 | 数据大小：6.5 MB | 索引大小：1.5 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| tx_id | int(11) | YES |  | UNI | ID标识 |
| action_time | datetime | YES |  |  | 操作时间 |
| isOK | char(1) | YES |  |  | 是否核销(是否核销，0为未核销，1为核销) |
| hexiao_time | datetime | YES |  |  | 核销时间 |
| hexiao_remark | text | YES |  |  | 核销说明 |
| isNew | char(1) | YES |  |  | 数据状态(0:历史数据；1：最新数据 2:转移申请被转移状态) |
| contract_sub_type | char(1) | YES |  |  | 发货类型（0为RMA ,1为项目保障,2为库存,null:转移申请 3:借用申请) |
| item_code | varchar(25) | YES |  |  | 物料号 |
| item_name | varchar(255) | YES |  |  | 物料名称 |
| tain_process | varchar(255) | YES |  |  | 检测报告 |
| isReceive | char(1) | YES | 0 |  | 0：已发货待确认接收 1：已确认接货 2：待发货确认 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| tx_id | tx_id | 是 | BTREE |

---

### back_type

**业务含义**：（待补充）

**数据量**：约 8 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 主键（自增） |
| back | varchar(10) | NO |  | MUL | 返回标识 |
| back_type | varchar(50) | YES |  |  | back类型 |
| back_state | varchar(200) | YES |  |  | backstate |
| remark | text | YES |  |  | 备注 |
| status | int(11) | YES | 1 |  | 有效状态0失效 1有效 |
| updateTime | datetime | YES |  |  | 更新时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| back_where_index | back | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### bar

**业务含义**：（待补充）

**数据量**：约 1,349 行 | 数据大小：0.2 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(10) | NO |  | PRI | 主键（自增） |
| spare_id | int(10) | NO |  | MUL | pps的主键 |
| bar_code | varchar(50) | YES |  |  | 设备编码 |
| bar_model | varchar(1000) | YES |  |  | 设备型号 |
| bar_num | varchar(50) | YES |  |  | 数量 |
| remark | text | YES |  |  | 备注 |
| serial_number | varchar(50) | YES |  |  | 序列号 |
| spare_code | varchar(15) | YES |  |  | 编码 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| spare_id | spare_id | 否 | BTREE |

---

### brw_app_info

**业务含义**：（待补充）

**数据量**：约 16,207 行 | 数据大小：4.5 MB | 索引大小：0.3 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| sheetID | varchar(15) | YES |  | UNI | 单据代码 |
| applicant | varchar(25) | YES |  |  | 申请人 |
| app_time | datetime | YES |  |  | 申请时间 |
| app_dptNo | varchar(10) | YES |  |  | 申请办事处名称 |
| contractNo | varchar(25) | YES |  |  | 合同号 |
| prt_name | varchar(255) | YES |  |  | 项目名称 |
| app_reason | text | YES |  |  | 申请原因 |
| duty_person | varchar(10) | YES |  |  | 负责人 |
| start_use_time | datetime | YES |  |  | 开始使用时间 |
| kept_place | varchar(10) | YES |  |  | 备件存放地 |
| promise_returntime | datetime | YES |  |  | 承诺备件归还时间 |
| extend_returntime | datetime | YES |  |  | 延长归还时间 |
| demand_type | varchar(25) | YES |  |  | 需求类型（维护在sys_state_or_type） |
| trade_classify | varchar(100) | YES |  |  | 行业分类（手动填写） |
| signing_state | char(1) | YES |  |  | 签单状态（0：已签单；1：未签单） 废弃字段 |
| app_type | char(1) | YES |  |  | 申请类型（0：借用申请；1：转移申请;2:历史数据） |
| addre_id | int(11) | YES |  |  | 关联收件人表ID |
| his_addre | varchar(64) | YES |  |  | 收件人 |
| his_addre_tel | varchar(64) | YES |  |  | 联系电话 |
| his_addr | varchar(1024) | YES |  |  | 地址/where |
| his_zipCode | varchar(25) | YES |  |  | 邮编 |
| ischange_duty | char(1) | YES |  |  | 是否转移责任人（0:转移；1：不转移） |
| isQuit | char(1) | YES |  |  | 是否为离职原因导致责任人变更，0：否，1：是 |
| change_type | char(1) | YES |  |  | 转移类型 |
| remark | text | YES |  |  | 备注 |
| data_state | char(1) | YES |  |  | 数据状态（0：历史；1：最新） |
| isSend | char(1) | YES |  |  | 是否发货(0:待发货确认 1：待收货确认) |
| isReceive | char(1) | YES | 0 |  | 是否收货(1:已接受 0：未接受) |
| beforeChange_sheetID | varchar(15) | YES |  |  | 发起转移申请之前的备件所涉及的单据号，此字段是为了保持备件经过转移流转后涉及单据号不变 |
| change_time | datetime | YES |  |  | 备件转移时间 |
| version_no | int(11) | YES | 0 |  | 库存发货配置的版本号 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| sheetID | sheetID | 是 | BTREE |

---

### brw_spare_info

**业务含义**：（待补充）

**数据量**：约 5,846 行 | 数据大小：1.5 MB | 索引大小：0.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| sheetID | varchar(10) | YES |  | MUL | 单据代码 |
| item_code | varchar(10) | YES |  |  | 物料编码 |
| item_name | varchar(255) | YES |  |  | 物料名称 |
| quantity | int(11) | YES |  |  | 数量 |
| remark | text | YES |  |  | 备注 |
| state | char(1) | YES | 1 |  | 状态（0：历史数据；1：有效数据） |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| sheetID | sheetID | 否 | BTREE |

---

### data_field_relation

**业务含义**：（待补充）

**数据量**：约 962 行 | 数据大小：0.4 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| dataName | varchar(255) | NO |  |  | 数据名 |
| dataType | varchar(255) | NO |  |  | 数据类型 |
| dataId | int(11) | YES | 0 |  | 数据实例ID |
| field | varchar(128) | NO |  |  | 字段 |
| alias | varchar(128) | YES |  |  | 字段别名 |
| name | varchar(128) | NO |  |  | 字段名 |
| title | varchar(255) | YES |  |  | 字段标题 |
| titleKey | varchar(255) | YES |  |  | 字段标题Key |
| cssId | varchar(255) | YES |  |  | 字段CSS id |
| cssClass | varchar(255) | YES |  |  | 字段CSS class |
| cssStyle | varchar(255) | YES |  |  | 字段CSS style |
| type | varchar(255) | YES |  |  | 字段类型 |
| render | varchar(4096) | YES |  |  | 字段处理 |
| sort | int(11) | YES | 0 |  | 排序 |
| orderable | bit(1) | YES | b'1' |  | 允许排序 |
| searchable | bit(1) | YES | b'0' |  | 允许搜索 |
| visible | bit(1) | YES | b'1' |  | 允许可见 |
| required | bit(1) | YES | b'0' |  | 必填 |
| readonly | bit(1) | YES | b'0' |  | 只读 |
| disabled | bit(1) | YES | b'0' |  | 组件失效 |
| extData | varchar(8192) | YES |  |  | 外部数据 |
| extKey | varchar(255) | YES |  |  | 外部数据key |
| extValue | varchar(255) | YES |  |  | 外部数据value |
| media | varchar(255) | YES |  |  | 传播媒介 |
| clazzName | varchar(255) | YES |  |  | 类名 |
| superData | varchar(255) | YES |  |  | 父类dataName |
| status | int(1) | YES | 1 |  | 状态 |
| compId | int(11) | YES |  |  | 公司ID |
| isSystemField | bit(1) | YES | b'1' |  | 是否为系统字段 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### department

**业务含义**：（待补充）

**数据量**：约 122 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| ocrCode | varchar(25) | NO |  | MUL | 编码 |
| ocrName | varchar(25) | NO |  |  | 名称 |
| isparam | int(11) | YES |  |  | 是否参数 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| ocrCode | ocrCode | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### dp_act_unify_task

**业务含义**：（待补充）

**数据量**：约 43,326 行 | 数据大小：58.6 MB | 索引大小：7.5 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| taskId | varchar(64) | NO |  | MUL | 统一待办任务Id |
| originTaskId | varchar(32) | NO |  | MUL | Activiti源TaskId |
| procInstId | varchar(64) | NO |  | MUL | 流程实例ID |
| processKey | varchar(255) | NO |  |  | 流程定义key |
| taskKey | varchar(255) | NO |  |  | 任务Key |
| taskName | varchar(255) | YES |  |  | 任务名 |
| eventType | varchar(255) | NO |  |  | 事件类型 |
| title | varchar(255) | YES |  |  | 任务标题 |
| assignee | varchar(255) | NO |  |  | 办理人 |
| formUrl | varchar(255) | YES |  |  | 待办链接地址 |
| beginTime | datetime | YES |  |  | 开始时间 |
| endTime | datetime | YES |  |  | 结束时间 |
| dueTime | datetime | YES |  |  | 过期时间 |
| state | varchar(25) | YES |  |  | 办理状态 |
| subState | varchar(25) | YES |  |  | 办理子状态 |
| success | bit(1) | NO | b'0' |  | 推送结果 |
| message | varchar(255) | YES |  |  | 推送消息 |
| latest | bit(1) | NO | b'1' |  | 是否最新 |
| pushSender | varchar(255) | YES |  |  | 推送发送实体类 |
| pushData | varchar(4096) | YES |  |  | 推送JSON内容 |
| createBy | varchar(45) | NO |  |  | 操作人 |
| createTime | timestamp | NO | CURRENT_TIMESTAMP |  | 时间 |
| updateBy | varchar(45) | NO |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| originTaskId | originTaskId | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| procInstId | procInstId | 否 | BTREE |
| taskId | taskId | 否 | BTREE |

---

### dp_erp_purchase_order_header

**业务含义**：（待补充）

**数据量**：约 150 行 | 数据大小：0.1 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| sourceType | varchar(25) | YES |  |  | 源数据类型 |
| sourceId | int(11) | YES |  |  | 源数据ID |
| purchPoolId | varchar(25) | YES |  |  | 采购订单池 |
| purchId | varchar(25) | YES |  |  | 采购订单号 |
| vendAccount | varchar(25) | YES |  |  | 供应商账号 |
| purchName | varchar(255) | YES |  |  | 采购事项 |
| purContract | varchar(25) | YES |  |  | 采购合同号 |
| salesContract | varchar(2048) | YES |  |  | 销售合同号 |
| contractAmount | varchar(25) | YES |  |  | 总金额 |
| workerPurchPlacer | varchar(25) | YES |  |  | 订货人 |
| applicant | varchar(25) | YES |  |  | 申请人 |
| inventLocationId | varchar(25) | YES |  |  | 仓库 |
| deliveryDate | date | YES |  |  | 交货日期 |
| dlvMode | varchar(25) | YES |  |  | 交货模式 |
| dlvTerm | varchar(25) | YES |  |  | 交货条款 |
| payment | varchar(255) | YES |  |  | 付款条款 |
| paymMode | varchar(25) | YES |  |  | 付款方式 |
| remark | varchar(4096) | YES |  |  | 整单备注 |
| otherSysNum | varchar(25) | YES |  |  | 外部系统编号 |
| projectName | varchar(255) | YES |  |  | 项目名称 |
| projectProgress | varchar(25) | YES |  |  | 项目进度 |
| subcontractType | varchar(25) | YES |  |  | 转包类型 |
| subcontStartDate | varchar(25) | YES |  |  | 转包开始日期 |
| subcontEndDate | varchar(25) | YES |  |  | 转包结束日期 |
| dataAreaId | varchar(25) | YES |  |  | 账套 |
| customInfo | json | YES |  |  | 自定义信息 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### dp_erp_purchase_order_line

**业务含义**：（待补充）

**数据量**：约 150 行 | 数据大小：0.1 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| headerId | int(11) | YES |  |  | 采购订单HeaderId |
| purchId | varchar(25) | YES |  |  | 采购订单号 |
| lineNum | varchar(25) | YES |  |  | 采购订单行号（可指定） |
| itemId | varchar(25) | YES |  |  | 物料编码 |
| purchQty | decimal(25,2) | YES |  |  | 采购数量 |
| purchPrice | decimal(25,2) | YES |  |  | 采购价 |
| taxItemGroup | varchar(25) | YES |  |  | 税收组 |
| inventSerialId | varchar(25) | YES |  |  | 厂商型号（复用D365序列号字段） |
| inventSiteId | varchar(25) | YES |  |  | 站点 |
| inventLocationId | varchar(25) | YES |  |  | 仓库 |
| wmsLocationId | varchar(25) | YES |  |  | 库位 |
| inventTransId | varchar(25) | YES |  |  | 批次号 |
| officeCode | varchar(25) | YES |  |  | 办事处 |
| deliveryDate | date | YES |  |  | 交货日期 |
| remark | varchar(4096) | YES |  |  | 行备注 |
| multiDimID | varchar(25) | YES |  |  | 行多维度ID |
| investmentProject | varchar(255) | YES |  |  | 募投项目 |
| dimBankAccount | varchar(25) | YES |  |  | 维度-银行账户 |
| dimCustomer | varchar(25) | YES |  |  | 维度-客户 |
| dimVendor | varchar(25) | YES |  |  | 维度-供应商 |
| dimEmployee | varchar(25) | YES |  |  | 维度-员工 |
| dimContract | varchar(25) | YES |  |  | 维度-合同号 |
| dimDepartment | varchar(25) | YES |  |  | 维度-部门 |
| dimBU | varchar(25) | YES |  |  | 维度-BU |
| dimProductLine | varchar(25) | YES |  |  | 维度-产品线 |
| dimTerritory | varchar(25) | YES |  |  | 维度-区域 |
| dimIndustry | varchar(25) | YES |  |  | 维度-行业 |
| dimMultiDimID | varchar(25) | YES |  |  | 维度-多维度ID |
| dataAreaId | varchar(25) | YES |  |  | 账套 |
| customInfo | json | YES |  |  | 自定义信息 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### dp_erp_purchase_receipt_header

**业务含义**：（待补充）

**数据量**：约 36 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| sourceOrderType | varchar(25) | YES |  |  | 订单源数据类型（Subcontract,Dispatch） |
| sourceOrderId | int(11) | YES |  |  | 订单源数据ID |
| sourceReceiptType | varchar(25) | YES |  |  | 订单源收货类型（SubcontractPayment, DispatchSettlement） |
| sourceReceiptId | int(11) | YES |  |  | 订单源收货ID |
| purchId | varchar(25) | YES |  |  | 采购订单号 |
| deliveryDate | date | YES |  |  | 交货日期 |
| documentDate | date | YES |  |  | 时间 |
| packingSlipId | varchar(512) | YES |  |  | 采购收货单号 |
| packingSlipRemark | varchar(1024) | YES |  |  | 采购收货备注 |
| projectProgress | varchar(1024) | YES |  |  | 项目进度 |
| dataAreaId | varchar(1024) | YES |  |  | 账套 |
| customInfo | json | YES |  |  | customInfo |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### dp_erp_purchase_receipt_line

**业务含义**：（待补充）

**数据量**：约 36 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| receiptId | int(11) | YES |  |  | 采购订单收货ID |
| purchId | varchar(25) | YES |  |  | 采购订单号 |
| inventSiteId | varchar(25) | YES |  |  | 站点 |
| inventLocationId | varchar(25) | YES |  |  | 仓库 |
| wmsLocationId | varchar(25) | YES |  |  | 库位 |
| inventTransId | varchar(25) | YES |  |  | 批次号 |
| lineNum | varchar(25) | YES |  |  | 采购订单行号（与批次号二选一，有批次号按批次号收货） |
| qty | decimal(25,2) | YES |  |  | 收货数量 |
| price | decimal(25,2) | YES |  |  | 收货单价 |
| amount | decimal(25,2) | YES |  |  | 收货金额 |
| dataAreaId | varchar(25) | YES |  |  | 账套 |
| customInfo | json | YES |  |  | 自定义信息 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### dptech_v_project_product_config_level_info

**业务含义**：（待补充）

**数据量**：约 0 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识 |
| projectCode | varchar(100) | YES |  |  | 编码 |
| itemGroup | decimal(23,10) | YES |  |  | 项目Group |
| itemCode | varchar(100) | YES |  |  | 编码 |
| parentCode | varchar(1000) | YES |  |  | 编码 |
| quantity | decimal(23,10) | YES |  |  | 数量 |
| bomPaths | varchar(1000) | YES |  |  | bomPaths |
| itemModel | varchar(100) | YES |  |  | 项目Model |
| itemDesc | varchar(500) | YES |  |  | 项目描述 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### find_in_set_help

**业务含义**：（待补充）

**数据量**：约 101 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | where中FIND_IN_SET函数替代方法 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### firebird_operation_log

**业务含义**：（待补充）

**数据量**：约 72,066 行 | 数据大小：29.6 MB | 索引大小：14.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(10) unsigned | NO |  | PRI | ID（自增） |
| sheetId | varchar(25) | YES |  | MUL | 流水号SheetId |
| txId | int(11) | YES |  |  | tx_info的tx_id |
| contractNo | varchar(45) | YES |  | MUL | 合同号 |
| barCode | varchar(25) | YES |  | MUL | 设备序列号 |
| insteadOfNum | varchar(25) | YES |  | MUL | RMA申请被替代的设备序列号 |
| changeTable | varchar(45) | YES |  |  | 操作的表 |
| operatTime | timestamp | NO | CURRENT_TIMESTAMP |  | 操作时间 |
| sqlText | text | YES |  |  | 操作表的sql语句 |
| remark | varchar(45) | YES |  |  | 备注 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| barCode | barCode | 否 | BTREE |
| contractNo | contractNo | 否 | BTREE |
| insteadOfNum | insteadOfNum | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| sheetId | sheetId | 否 | BTREE |

---

### fnd_act_hi_comment

**业务含义**：（待补充）

**数据量**：约 36,819 行 | 数据大小：4.5 MB | 索引大小：6.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | activity 审批意见表（自增） |
| objId | int(11) | YES |  | MUL | 业务ID |
| procdefKey | varchar(50) | YES |  |  | 流程类型 |
| taskKey | varchar(50) | YES |  |  | 任务Key |
| taskId | varchar(25) | YES |  | MUL | activity任务ID |
| instId | varchar(25) | YES |  | MUL | 流程ID |
| assignee | varchar(25) | YES |  | MUL | 办理人 |
| assigneeTime | datetime | YES |  |  | 办理时间 |
| nextAssignee | varchar(25) | YES |  |  | 下一步办理人 |
| nextAssigneeName | varchar(64) | YES |  |  | 下一步办理人姓名 |
| result | int(11) | YES |  |  | 审批结果 |
| message | text | YES |  |  | 审批意见 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| assignee | assignee, procdefKey | 否 | BTREE |
| instId | instId | 否 | BTREE |
| objId | objId, procdefKey | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| taskId | taskId | 否 | BTREE |

---

### fnd_basic_data

**业务含义**：基础数据表 - 存储系统枚举值明细

**数据量**：约 480 行 | 数据大小：0.1 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| dataTypeCode | varchar(45) | YES |  | MUL | 编码 |
| basicDataId | varchar(255) | YES |  | MUL | ID标识 |
| basicDataName | varchar(255) | YES |  |  | 名称 |
| basicDataAttri1 | varchar(255) | YES |  |  | 字段属性1 |
| sortId | int(11) | YES |  |  | 查询排序字段数值越大越在前 |
| createTime | datetime | YES |  |  | 时间 |
| createBy | varchar(45) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(45) | YES |  |  | 操作人 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| basicDataId | basicDataId | 否 | BTREE |
| basicDataId_dataTypeCode | dataTypeCode, basicDataId | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### fnd_basic_data_type

**业务含义**：基础数据类型表 - 定义系统枚举值分类

**数据量**：约 30 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| dataTypeCode | varchar(45) | YES |  |  | 编码 |
| dataTypeName | varchar(45) | YES |  |  | 名称 |
| status | int(11) | YES |  |  | 是否需要放在前台管理 |
| createTime | datetime | YES |  |  | 时间 |
| createBy | varchar(45) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(45) | YES |  |  | 操作人 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### fnd_basic_prjstate

**业务含义**：（待补充）

**数据量**：约 40 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| dataTypeCode | varchar(45) | YES |  | MUL | 数据类型编码，对应fnd_basic_data |
| basicDataId | varchar(11) | YES |  |  | 基础数据ID，对应fnd_basic_data |
| column010 | varchar(10) | YES |  |  | 项目类型，对应pm_project_header |
| column011 | varchar(10) | YES |  |  | 项目类别，对应pm_project_header |
| createTime | datetime | YES |  |  | 记录数据创建时间 |
| createBy | varchar(45) | YES |  |  | 记录数据创建用户 |
| updateTime | datetime | YES |  |  | 记录数据最新更新时间 |
| updateBy | varchar(45) | YES |  |  | 记录数据最新更新用户 |
| effectiveFrom | datetime | YES |  |  | 数据有效性开始时间 |
| effectiveTo | datetime | YES |  |  | 数据有效性结束时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| dataTypeCode | dataTypeCode, basicDataId | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### fnd_company

**业务含义**：公司信息表

**数据量**：约 3 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| pid | int(11) | NO |  | MUL | 父组织机构ID |
| name | varchar(128) | NO |  |  | 组织机构全名 |
| abbr | varchar(64) | NO |  |  | 组织机构简写 |
| website | varchar(128) | YES |  |  | 组织机构网址 |
| code | varchar(25) | YES | 0 | MUL | 组织机构代码 |
| account | varchar(25) | YES |  |  | 组织机构账套 |
| status | smallint(1) | NO | 1 |  | 有效性（1-有效，0-失效），默认有效 |
| createTime | datetime | YES |  |  | 时间 |
| createBy | varchar(32) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(32) | YES |  |  | 操作人 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| code | code | 否 | BTREE |
| pid | pid | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### fnd_data_refresh_log

**业务含义**：（待补充）

**数据量**：约 16,540 行 | 数据大小：3.5 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| refreshTaskName | varchar(100) | YES |  |  | 名称 |
| handleUser | varchar(15) | YES |  |  | handleUser |
| dataFrom | varchar(25) | YES |  |  | dataFrom |
| dataTo | varchar(25) | YES |  |  | dataTo |
| refreshFrom | datetime | YES |  |  | 刷新开始时间 |
| refreshTo | datetime | YES |  |  | 结束时间 |
| refreshState | int(11) | YES | 0 |  | 刷新成功或失败 0失败 1 成功 |
| refreshException | mediumtext | YES |  |  | refreshException |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### fnd_department

**业务含义**：部门信息表 - 组织架构部门信息

**数据量**：约 137 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| departmentNum | varchar(20) | NO |  | UNI | 部门数量 |
| departmentName | varchar(20) | NO |  |  | 名称 |
| isparam | int(11) | YES | 0 |  | 是否参数化部门：0=否, 1=是 |
| status | int(11) | NO | 1 |  | 状态 |
| createTime | datetime | YES |  |  | 时间 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| deparmentNum | departmentNum | 是 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### fnd_files

**业务含义**：（待补充）

**数据量**：约 9,096 行 | 数据大小：2.5 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 系统上传文件信息（自增） |
| fileName | varchar(255) | YES |  |  | 文件名称 |
| filePath | varchar(255) | YES |  |  | 文件路径 |
| fileType | varchar(255) | YES |  |  | 文件分类 |
| uploadBy | varchar(25) | YES |  |  | 上传用户 |
| uploadTime | datetime | YES |  |  | 上传时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### fnd_mails

**业务含义**：（待补充）

**数据量**：约 146,157 行 | 数据大小：440.8 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| mailSubject | varchar(255) | NO |  |  | 邮件主题 |
| mailContent | longtext | NO |  |  | 邮件正文 |
| mailTos | text | YES |  |  | 邮件主送 |
| mailCcs | text | YES |  |  | 邮件抄送 |
| mailBcc | text | YES |  |  | 邮件密送 |
| mailAttachFiles | text | YES |  |  | 邮件附件 以特殊符号间隔多个文件 |
| mailSendTime | datetime | YES |  |  | 邮件实际发送时间 |
| mailExpectSendTime | datetime | YES |  |  | 邮件期望发送时间 |
| mailServerPort | varchar(25) | YES |  |  | mailServerPort |
| mailServerHost | varchar(25) | YES |  |  | mailServerHost |
| mailUsername | varchar(25) | YES |  |  | 名称 |
| mailPassword | varchar(25) | YES |  |  | mailPassword |
| mailFromaddress | varchar(25) | YES |  |  | mailFromaddress |
| sendFlag | int(11) | YES | 0 |  | 邮件是否发送 1 为已发送 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updatteTime | datetime | YES |  |  | 时间 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### fnd_menus

**业务含义**：（待补充）

**数据量**：约 22 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| menuCode | varchar(50) | YES |  |  | 菜单编码 |
| menuName | varchar(25) | YES |  |  | 菜单名称 |
| menuLevel | int(1) | YES |  |  | 菜单级别 |
| superId | int(11) | YES |  |  | 父菜单ID |
| path | varchar(200) | YES |  |  | 访问路径 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### fnd_role_menus

**业务含义**：（待补充）

**数据量**：约 58 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| roleId | int(11) | NO |  |  | ID标识 |
| menuId | int(11) | NO |  |  | ID标识 |
| menuPower | varchar(20) | NO |  |  | 各菜单增删改权限 |
| createTime | datetime | YES |  |  | 时间 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### fnd_roles

**业务含义**：（待补充）

**数据量**：约 16 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(6) | NO |  | PRI | ID标识（自增） |
| roleName | varchar(64) | NO |  | UNI | 名称 |
| defaultPage | varchar(255) | YES |  |  | 该角色登录的默认首页 |
| status | int(1) | NO |  |  | 状态 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| roleRemark | varchar(200) | YES |  |  | roleRemark |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| roleName | roleName | 是 | BTREE |

---

### fnd_spms_arg

**业务含义**：（待补充）

**数据量**：约 5 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 备件系统一些特殊参数控制 如邮件等（自增） |
| code | varchar(25) | YES |  |  | 编码 |
| var | text | YES |  |  | 变量值 |
| mark | varchar(255) | YES |  |  | 标记 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### fnd_sys_arg

**业务含义**：（待补充）

**数据量**：约 45 行 | 数据大小：0.1 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 系统变量（自增） |
| code | varchar(64) | YES |  |  | 编码 |
| var | text | YES |  |  | 变量值 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### fnd_user_info

**业务含义**：用户信息基础表

**数据量**：约 459 行 | 数据大小：0.1 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(8) | NO |  | PRI | ID标识（自增） |
| username | varchar(128) | NO |  | MUL | 名称 |
| password | varchar(32) | NO | 5416d7cd6ef195a0f7622a9c56b55e84 |  | 密码 |
| email | varchar(128) | NO |  |  | 邮箱 |
| dpNo | varchar(25) | YES |  |  | dp编号 |
| realName | varchar(128) | NO |  |  | 名称 |
| roleIds | varchar(64) | YES |  |  | 用户角色，支持多角色 |
| isemail | int(11) | YES |  |  | 是否email |
| status | int(1) | YES |  |  | 状态 |
| defaultPage | varchar(255) | YES |  |  | 该用户登录首页 |
| pwdoverdue | datetime | YES |  |  | 密码过期时间 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |
| customInfo | json | YES |  |  | 自定义信息 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| username | username | 否 | BTREE |

**样例数据**：

| id | username | password | email | dpNo | realName | roleIds | isemail | status | defaultPage | pwdoverdue | createBy | createTime | updateBy | updateTime | effectiveFrom | effectiveTo | customInfo |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | l00476 | 9bb9cecd65c0d45232ba93f9c16dca62 | linyisheng@dptech.com | 162023 | 林以升 | ;12;,;11; | 1 | 0 | module/Workspace.action | 2015-06-27 16:11:17 | admin | 2015-06-27 16:11:17 | None | 2022-12-19 18:11:28 | 2015-06-27 16:11:17 | 2022-12-19 18:11:28 | None |
| 2 | x01095 | 34651d632dee68cef598ffbd81020692 | xiexiaolin@dptech.com | 162025 | 谢小林 | ;12; | 1 | 0 | module/Workspace.action | 2015-06-27 16:11:17 | admin | 2015-06-27 16:11:17 | None | 2020-05-28 14:10:15 | 2015-06-27 16:11:17 | 2020-05-28 14:10:18 | None |
| 3 | c00719 | b4455a5767f5484ba137492761c6d47e | chenjunxu@dptech.com | 162025 | 陈俊旭 | ;12; | 1 | 0 | module/Workspace.action | 2015-06-27 16:11:17 | admin | 2015-06-27 16:11:17 | None | 2018-06-19 16:15:35 | 2015-06-27 16:11:17 | 2018-06-19 16:15:36 | None |

---

### fnd_user_menus

**业务含义**：（待补充）

**数据量**：约 3,035 行 | 数据大小：0.3 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| fnd_user_id | int(11) | YES |  |  | ID标识 |
| username | varchar(128) | YES |  |  | 名称 |
| menuCode | varchar(50) | YES |  |  | 编码 |
| menuValue | int(1) | YES |  |  | menuValue |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |
| createdBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### fnd_user_power

**业务含义**：（待补充）

**数据量**：约 442 行 | 数据大小：0.1 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| fndUserId | int(11) | YES |  |  | ID标识 |
| username | varchar(25) | YES |  |  | 名称 |
| areapower | varchar(4096) | YES |  |  | 区域权限 |
| createTime | datetime | YES |  |  | 时间 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### hexiao

**业务含义**：（待补充）

**数据量**：约 83 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| 单据编号 | double | YES |  |  | 单据编号 |
| 过帐日期 | timestamp | YES |  |  | 过帐日期 |
| 物料代码 | varchar(255) | YES |  |  | 物料代码 |
| 物料/服务描述 | varchar(255) | YES |  |  | 物料/服务描述 |
| 未核销数量 | double | YES |  |  | 未核销数量 |
| 设备序列号 | varchar(255) | YES |  |  | 设备序列号 |
| 注释 | varchar(255) | YES |  |  | 注释 |
| 合同号 | varchar(255) | YES |  |  | 合同号 |
| 责任部门 | varchar(255) | YES |  |  | 责任部门 |

---

### mes_oqc_info

**业务含义**：MES出货检验信息表

**数据量**：约 1,411,475 行 | 数据大小：164.7 MB | 索引大小：74.7 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| packNo | varchar(64) | YES |  |  | 装箱单号 |
| contractNo | varchar(64) | YES |  |  | 合同号 |
| itemCode | varchar(25) | YES |  |  | 物料号 |
| barcode | varchar(64) | YES |  | MUL | 设备序列号 |
| itemNo | varchar(25) | YES |  |  | 装箱销售明细行号 |
| workNo | varchar(25) | YES |  |  | 工号 |
| inspectUser | varchar(25) | YES |  |  | 检验人 |
| inspectTime | datetime | YES |  |  | 检验时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| barcode | barcode | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### mes_seal_info

**业务含义**：（待补充）

**数据量**：约 60 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | varchar(255) | YES |  |  | ID标识 |
| name | varchar(255) | YES |  |  | 印章名称 |
| info | varchar(255) | YES |  |  | 印记 |
| description | varchar(255) | YES |  |  | 用途 |
| user | varchar(255) | YES |  | MUL | 领用人 |
| takeTime | datetime | YES |  |  | 领用时间 |
| backTime | datetime | YES |  |  | 归还时间 |
| remark | varchar(255) | YES |  |  | 备注 |
| uploadBy | varchar(255) | YES |  |  | 上传人 |
| uploadTime | datetime | YES |  |  | 上传时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| user | user | 否 | BTREE |

---

### pm_basic_deliver_detail

**业务含义**：（待补充）

**数据量**：约 68,845 行 | 数据大小：13.5 MB | 索引大小：9.5 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| projectId | int(11) | YES |  | MUL | 项目ID |
| projectType | varchar(25) | YES | 10 | MUL | 项目类型 |
| contractNo | varchar(25) | YES |  |  | 合同编号 |
| taskId | int(11) | YES |  |  | TaskId |
| deliverId | int(11) | YES |  | MUL | 对应pm_basic_prj_deliver主键 |
| deliverableName | varchar(255) | YES |  |  | 交付件名称 |
| deliverablePath | varchar(255) | YES |  |  | 交付件路径 |
| deliverableType | varchar(45) | YES |  |  | 交付件类型 |
| uploadUser | varchar(45) | YES |  |  | 上传者 |
| uploadTime | datetime | YES |  |  | 上传时间 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| deliverId | deliverId | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| projectId | projectId | 否 | BTREE |
| projectType_projectId_deliverType | projectType, projectId, deliverableType | 否 | BTREE |

---

### pm_basic_prj_deliver

**业务含义**：（待补充）

**数据量**：约 86 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| column010 | varchar(25) | YES |  |  | 对应pm_project_header |
| column011 | varchar(25) | YES |  |  | 对应pm_project_header |
| dataTypeCode | varchar(45) | YES |  | MUL | 活动节点，对应fnd_basic_data表 |
| basicDataId | varchar(45) | YES |  |  | 活动节点，对应fnd_basic_data表 |
| dataTypeCodeSon | varchar(45) | YES |  | MUL | 交付件节点，对应fnd_basic_data表 |
| basicDataIdSon | varchar(45) | YES |  |  | 交付件节点，对应fnd_basic_data表 |
| isNeed | int(11) | YES | 0 |  | 是否必须，1表示必须，2表示分情况确定 |
| createTime | datetime | YES |  |  | 时间 |
| createBy | varchar(45) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(45) | YES |  |  | 操作人 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| dataTypeCode | dataTypeCode, basicDataId | 否 | BTREE |
| dataTypeCodeSon | dataTypeCodeSon, basicDataIdSon | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### pm_cl_callback

**业务含义**：售后回访表 - 记录售后回访信息

**数据量**：约 2,727 行 | 数据大小：0.4 MB | 索引大小：0.2 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 运营商直签项目回访申请主表（自增） |
| projectId | int(11) | YES |  | MUL | 项目ID |
| instId | varchar(25) | YES |  | MUL | 流程ID |
| remark | text | YES |  |  | 回访申请备注 |
| applyState | int(11) | YES |  |  | -1草稿 1 审批中 2审批通过 |
| applyBy | varchar(25) | YES |  |  | 申请人 |
| applyTime | datetime | YES |  |  | 申请时间 |
| createTime | timestamp | YES |  |  | 时间 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| instId | instId | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| projectId | projectId | 否 | BTREE |

**样例数据**：

| id | projectId | instId | remark | applyState | applyBy | applyTime | createTime | createBy | updateTime | updateBy | effectiveFrom | effectiveTo |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | 9907 | 9032 | 运营商直签项目系统自动发起回访流程 | 2 | x01623 | 2016-01-06 15:34:00 | 2016-01-06 15:34:00 | x01623 | 2016-01-07 15:25:48 | p01537 | 2016-01-06 15:34:00 | None |
| 4 | 7163 | 9468 | 交付件已提供，此项目我们负责维护，可以闭环。 | 2 | c00494 | 2016-01-18 16:15:45 | 2016-01-18 16:15:45 | c00494 | 2016-04-27 10:09:34 | p01537 | 2016-01-18 16:15:45 | None |
| 5 | 6881 | 9822 | 已给用户培训，目前项目改项目已经完成终验。 | 2 | c00491 | 2016-01-26 10:45:13 | 2016-01-26 10:45:13 | c00491 | 2017-12-06 15:36:25 | p01537 | 2016-01-26 10:45:13 | None |

---

### pm_cl_callback_quesnaire

**业务含义**：（待补充）

**数据量**：约 2,852 行 | 数据大小：0.2 MB | 索引大小：0.2 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| callBackId | int(11) | YES |  | MUL | 回访主键主表 |
| taskId | varchar(25) | YES |  |  | 对应任务ID |
| quesnaireId | int(11) | YES |  | MUL | 对应pm_cl_quesnaire_result_header主键 |
| quesnaireVersion | int(11) | YES |  |  | 版本号 |
| quesnaireState | int(11) | YES |  |  | -1 草稿 1已提交 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| callBackId | callBackId | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| quesnaireId | quesnaireId | 否 | BTREE |

---

### pm_cl_evaluation_header

**业务含义**：（待补充）

**数据量**：约 25,900 行 | 数据大小：5.5 MB | 索引大小：3.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) unsigned | NO |  | PRI | ID标识（自增） |
| projectCode | varchar(45) | NO |  | MUL | 评测项目编码 |
| projectId | int(11) | NO | 0 | MUL | 项目ID |
| projectName | varchar(120) | YES |  |  | 项目名称 |
| evaluationTime | datetime | YES | 0000-00-00 00:00:00 |  | 审核时间 |
| evaluationPeopleName | varchar(45) | YES |  |  | 审核人员姓名 |
| evaluationScore | double | NO | 0 |  | 评测总分数 |
| evaluationResult | int(11) | NO | 0 |  | 评测结果（通过/未通过） |
| evaluationComment | text | YES |  |  | 项目评价（驳回时为驳回原因） |
| evaluationType | int(11) | NO | 0 |  | 400回访/项目组总分评定 |
| status | int(11) | NO | 0 |  | 状态 |
| createdTime | datetime | YES | 0000-00-00 00:00:00 |  | 时间 |
| createdPerson | varchar(25) | YES |  |  | 创建信息 |
| updatedTime | datetime | YES | 0000-00-00 00:00:00 |  | 时间 |
| updatedPerson | varchar(25) | YES |  |  | 更新信息 |
| nextAcceptPerson | varchar(25) | YES |  |  | 下一个接收申请的人员 |
| evaluationPeopleId | varchar(25) | YES |  |  | 审核人员用户名 |
| nextAcceptPersonName | varchar(25) | YES |  |  | 名称 |
| applyHeaderId | int(11) | NO | 0 |  | 申请表Id |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| projectCode_index | projectCode | 否 | BTREE |
| projectId | projectId | 否 | BTREE |

---

### pm_cl_quesnaire_result_header

**业务含义**：（待补充）

**数据量**：约 103,357 行 | 数据大小：6.5 MB | 索引大小：2.5 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) unsigned | NO |  | PRI | ID标识（自增） |
| evaluationHeaderId | int(11) | NO |  | MUL | 测评记录Id |
| quesnaireTemplateHeaderId | int(11) | YES |  |  | 问卷模板Id |
| quesMarkScore | double | YES | 0 |  | 问卷得分 |
| createdTime | datetime | YES |  |  | 时间 |
| createdPerson | varchar(25) | YES |  |  | 创建信息 |
| updatedTime | datetime | YES |  |  | 时间 |
| updatedPerson | varchar(25) | YES |  |  | 更新信息 |
| quesMarkResult | int(11) | YES |  |  | 评分结果 |
| status | int(11) | NO | 0 |  | 状态 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| index_evaluationHeaderId | evaluationHeaderId | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### pm_cl_quesnaire_result_line

**业务含义**：问卷结果明细表 - 记录回访问卷的调查结果

**数据量**：约 454,519 行 | 数据大小：28.6 MB | 索引大小：38.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) unsigned | NO |  | PRI | ID标识（自增） |
| quesnaireTemplateHeaderId | int(11) | NO |  | MUL | 回访问卷Id |
| quesnaireTemplateLineId | int(11) | NO |  | MUL | 问卷中问题的Id |
| questionTemplateOptId | int(11) | YES |  |  | 选中的选项id |
| questionAnswer | text | YES |  |  | questionAnswer |
| questionScore | double | NO | 0 |  | 问题得分 |
| quesnaireResultHeaderId | int(11) | NO |  | MUL | 回访结果头信息Id |
| createdTime | datetime | YES |  |  | 时间 |
| createdPerson | varchar(25) | YES |  |  | 创建信息 |
| updatedTime | datetime | YES |  |  | 时间 |
| updatedPerson | varchar(25) | YES |  |  | 更新信息 |
| quesTypeForCB | varchar(10) | YES |  |  | 问题回访类型 |
| quesEvaResult | int(11) | YES |  |  | 选项是否为不同选项 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| quesnaireResultHeaderId | quesnaireResultHeaderId, quesTypeForCB | 否 | BTREE |
| quesnaireTemplateHeaderId | quesnaireTemplateHeaderId, quesnaireTemplateLineId | 否 | BTREE |
| quesnaireTemplateLineId | quesnaireTemplateLineId, questionTemplateOptId | 否 | BTREE |

---

### pm_cl_quesnaire_template_header

**业务含义**：（待补充）

**数据量**：约 13 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(10) unsigned | NO |  | PRI | ID标识（自增） |
| questionnaireTemplateNum | varchar(45) | NO |  |  | 问卷模板编号 |
| questionnaireTemplateName | varchar(200) | NO |  |  | 问卷模板名称 |
| questionnaireScore | double | NO | 0 |  | 问卷总分数 |
| questionnairePassScore | double | NO | 0 |  | 问卷达标分数 |
| questionnaireStatus | int(11) | NO | 0 |  | 问卷状态 |
| effectiveStartTime | datetime | YES |  |  | 时间 |
| effectiveEndTime | datetime | YES |  |  | 时间 |
| createdTime | datetime | YES |  |  | 时间 |
| updatedTime | datetime | YES |  |  | 时间 |
| createdPerson | varchar(25) | YES |  |  | 创建信息 |
| updatedPerson | varchar(25) | YES |  |  | 更新信息 |
| quesType | varchar(25) | YES |  | MUL | ques类型 |
| markIndexs | varchar(45) | YES |  |  | 问卷计分规则的index |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| quesType | quesType | 否 | BTREE |

---

### pm_cl_quesnaire_template_line

**业务含义**：（待补充）

**数据量**：约 80 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) unsigned | NO |  | PRI | ID标识（自增） |
| questionContent | varchar(200) | NO |  |  | 题目内容 |
| questionType | int(11) | NO |  |  | 题目类型,如:多选\单选 |
| questionScore | double | NO | 0 |  | 题目分数 |
| questionRemark | varchar(200) | YES |  |  | 题目备注 |
| questionNum | int(11) | NO | 0 |  | 问题编号,表示了问卷中问题的顺序 |
| quesnaireTemplateHeaderId | int(11) | NO | 0 | MUL | 问卷模板Id |
| questionStatus | int(11) | YES | 0 |  | question状态 |
| effectiveStartTime | datetime | YES |  |  | 时间 |
| effectiveEndTime | datetime | YES |  |  | 时间 |
| createdTime | datetime | YES |  |  | 时间 |
| updatedTime | datetime | YES |  |  | 时间 |
| createdPerson | varchar(25) | YES |  |  | 创建信息 |
| updatedPerson | varchar(25) | YES |  |  | 更新信息 |
| questionTypeForCB | varchar(10) | YES |  |  | 回访问题类型 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| id_UNIQUE | id | 是 | BTREE |
| PRIMARY | id | 是 | BTREE |
| quesnaireTemplateHeaderId | quesnaireTemplateHeaderId | 否 | BTREE |

---

### pm_cl_quesnaire_template_options

**业务含义**：（待补充）

**数据量**：约 231 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) unsigned | NO |  | PRI | ID标识（自增） |
| questionId | int(11) | NO | 0 |  | 题目Id |
| questionOptionNum | int(11) | NO | 0 |  | 选项编号 |
| questionOptionsContent | varchar(200) | NO |  |  | 选项内容 |
| questionOptionScore | double | YES | 0 |  | 选项分数 |
| quesnaireTemplateHeaderId | int(11) | NO | 0 |  | 问卷模板Id |
| effectiveStartTime | datetime | YES |  |  | 时间 |
| effectiveEndTime | datetime | YES |  |  | 时间 |
| createdTime | datetime | YES |  |  | 时间 |
| updatedTime | datetime | YES |  |  | 时间 |
| createdPerson | varchar(25) | YES |  |  | 创建信息 |
| updatedPerson | varchar(25) | YES |  |  | 更新信息 |
| quesLineType | varchar(10) | YES |  |  | 问题类型 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| id_UNIQUE | id | 是 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### pm_column_of_relationship

**业务含义**：项目字段映射关系表 - 定义不同项目类型的动态字段映射

**数据量**：约 14 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| projectType | int(11) | YES |  |  | project类型 |
| columnCode | varchar(45) | YES |  |  | 编码 |
| colemnName | varchar(45) | YES |  |  | 名称 |
| columnDesc | varchar(45) | YES |  |  | column描述 |
| createTime | datetime | YES |  |  | 时间 |
| createBy | varchar(45) | YES |  |  | 操作人 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### pm_common_related_data

**业务含义**：（待补充）

**数据量**：约 572 行 | 数据大小：4.5 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| type | varchar(64) | NO |  | MUL | 数据类型 |
| objType | varchar(64) | NO |  |  | 主数据类型 |
| objId | int(11) | NO | 0 |  | 主数据Id |
| field1 | varchar(255) | YES |  |  | 扩展字段1 |
| field2 | varchar(255) | YES |  |  | 扩展字段2 |
| field3 | varchar(255) | YES |  |  | 扩展字段3 |
| field4 | varchar(255) | YES |  |  | 扩展字段4 |
| field5 | varchar(255) | YES |  |  | 扩展字段5 |
| field6 | varchar(255) | YES |  |  | 扩展字段6 |
| field7 | varchar(255) | YES |  |  | 扩展字段7 |
| field8 | varchar(255) | YES |  |  | 扩展字段8 |
| field9 | varchar(255) | YES |  |  | 扩展字段9 |
| field10 | varchar(255) | YES |  |  | 扩展字段10 |
| disabled | bit(1) | NO | b'0' |  | 是否禁用 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| customInfo | json | YES |  |  | customInfo |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| type | type, objType, objId | 否 | BTREE |

---

### pm_daily_report

**业务含义**：（待补充）

**数据量**：约 11,221 行 | 数据大小：109.6 MB | 索引大小：6.7 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| projectId | int(11) | NO | -1 | MUL | 项目头信息主键 |
| projectType | varchar(45) | NO |  | MUL | 项目类型，售前:20/售后:10 |
| projectCode | varchar(45) | NO |  | MUL | 项目名称 |
| projectName | varchar(200) | YES |  |  | 项目名称 |
| contractNo | varchar(255) | YES |  |  | 合同号 |
| officeCode | varchar(25) | YES |  | MUL | 办事处编码 |
| type | varchar(45) | YES |  | MUL | 任务性质 |
| category | varchar(45) | YES |  | MUL | 任务分类 |
| subCategory | varchar(45) | YES |  | MUL | 任务小类 |
| processTime | datetime | YES |  |  | 处理时间 |
| processDesc | varchar(1024) | YES |  |  | 事项描述 |
| processStep | varchar(1024) | YES |  |  | 解决进展 |
| remainProblem | varchar(1024) | YES |  |  | 遗留问题 |
| customerInteraction | varchar(1024) | YES |  |  | 客户互动情况 |
| transitHour | float | YES | 0 |  | 在途耗时(h) |
| processHour | float | YES | 0 |  | 处理耗时(h) |
| itemModel | varchar(255) | YES |  |  | 产品型号 |
| softVersion | varchar(255) | YES |  |  | 在网版本 |
| enabledFeatures | varchar(255) | YES |  |  | 启用功能 |
| customTos | varchar(255) | YES |  |  | 自定义主送 |
| customCcs | varchar(255) | YES |  |  | 自定义抄送 |
| projectExecutionState | varchar(45) | YES |  |  | 项目实施状态 |
| hasReport | bit(1) | NO | b'0' |  | 是否有巡检报告 |
| quesnaireId | int(11) | YES |  |  | 问卷ID |
| deliverFileIds | varchar(255) | YES |  |  | 交付件，fnd_files id |
| remark | varchar(1024) | YES |  |  | 备注 |
| isReported | bit(1) | YES | b'0' |  | 已上报 |
| qualityFactor | float | YES | 0 |  | 质量系数 |
| customInfo | json | YES |  |  | 自定义信息 |
| status | varchar(25) | YES |  |  | 状态 |
| disabled | bit(1) | YES | b'0' |  | 失效标记 |
| createTime | datetime | YES |  | MUL | 创建时间 |
| createBy | varchar(45) | YES |  | MUL | 创建用户 |
| updateTime | datetime | YES |  |  | 最新更新时间 |
| updateBy | varchar(45) | YES |  |  | 最新更新用户 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| category | category, subCategory | 否 | BTREE |
| createBy | createBy | 否 | BTREE |
| createTime | createTime | 否 | BTREE |
| officeCode | officeCode | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| projectCode | projectCode | 否 | BTREE |
| projectId | projectId | 否 | BTREE |
| projectType | projectType | 否 | BTREE |
| subCategory | subCategory | 否 | BTREE |
| type | type | 否 | BTREE |

---

### pm_dispatch_project_header

**业务含义**：（待补充）

**数据量**：约 328 行 | 数据大小：0.4 MB | 索引大小：0.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| dispatchName | varchar(512) | YES |  |  | 外派名称 |
| dispatchNo | varchar(64) | YES |  | MUL | 外派合同号 |
| dispatchSeq | varchar(64) | YES |  | UNI | 外派编号 |
| contractNos | varchar(2048) | YES |  |  | 项目合同号 |
| projectIds | varchar(1024) | YES |  |  | 外派的项目ID |
| type | varchar(25) | YES |  |  | 外派类型 |
| state | int(11) | NO | 0 |  | 外派状态 |
| peopleNum | int(11) | YES | 0 |  | 外派人数 |
| callbackState | int(11) | YES |  |  | 回访状态 |
| facilitatorId | int(11) | YES |  |  | 服务商ID |
| facilitatorCode | varchar(25) | YES |  | MUL | 服务商编码 |
| facilitatorName | varchar(64) | YES |  |  | 服务商名 |
| bankInfo | varchar(255) | YES |  |  | 服务商开户地址 |
| bankAccount | varchar(64) | YES |  |  | 服务商收款账户 |
| officeCode | varchar(25) | YES |  | MUL | 办事处部门 |
| profitDepCode | varchar(25) | YES |  | MUL | 收益部门 |
| dutyPerson | varchar(25) | YES |  |  | 项目总接口人 |
| officeDutyPerson | varchar(25) | YES |  |  | 办事处接口人 |
| isAccrued | bit(1) | YES |  |  | 是否计提 |
| isInvoiced | bit(1) | YES |  |  | 是否提供发票 |
| dispatchAmount | varchar(25) | YES |  |  | 外派价 |
| prepaidInfo | varchar(255) | YES |  |  | 预付信息（比例、金额） |
| prepaidRule | varchar(255) | YES |  |  | 预付遵循原则 |
| acceptanceInfo | varchar(255) | YES |  |  | 验收要求 |
| reason | varchar(512) | YES |  |  | 外派原因 |
| remark | varchar(512) | YES |  |  | 备注 |
| dispatchTime | datetime | YES |  |  | 派单时间 |
| smsProjectCode | varchar(255) | YES |  | MUL | SMS项目编码 |
| smsSubmitTime | datetime | YES |  |  | SMS项目提交时间 |
| smsProjectAmount | varchar(25) | YES |  |  | SMS项目金额 |
| smsAfProjectAmount | varchar(25) | YES |  |  | 安服项目金额 |
| effectiveFrom | datetime | YES |  |  | 有效开始时间 |
| effectiveTo | datetime | YES |  |  | 有效结束时间 |
| disabled | bit(1) | YES | b'0' |  | 删除状态 |
| dispatched | bit(1) | YES | b'0' |  | 派单状态 |
| settled | bit(1) | YES | b'0' |  | 结算状态 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| customInfo | json | YES |  |  | 自定义信息 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| facilitatorId | facilitatorCode | 否 | BTREE |
| officeCode | officeCode | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| profitDepCode | profitDepCode | 否 | BTREE |
| smsProjectCode | smsProjectCode | 否 | BTREE |
| subcontractNo | dispatchNo | 否 | BTREE |
| UNIQUE_dispatchSeq | dispatchSeq | 是 | BTREE |

---

### pm_dispatch_project_settlement

**业务含义**：（待补充）

**数据量**：约 72 行 | 数据大小：0.1 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| settleSeq | varchar(512) | YES |  |  | 结算编号 |
| dispatchId | int(11) | NO |  | MUL | 派单Id |
| dispatchSeq | varchar(25) | NO |  | MUL | 派单编号 |
| progressDesc | varchar(1024) | YES |  |  | 实施进展 |
| progressRatio | float(3,2) | YES |  |  | 实施比例 |
| acceptanceDesc | varchar(1024) | YES |  |  | 验收进度 |
| acceptanceRatio | varchar(10) | YES |  |  | 验收比例 |
| ratio | varchar(10) | YES |  |  | 此次付款比例 |
| amount | varchar(25) | YES |  |  | 此次付款金额 |
| memo | varchar(512) | YES |  |  | 此次付款说明 |
| confirmTime | datetime | YES |  |  | 提交时间 |
| paymentTime | datetime | YES |  |  | 付款时间 |
| remark | varchar(512) | YES |  |  | 备注 |
| state | int(1) | YES | 0 |  | 状态 |
| disabled | bit(1) | YES | b'0' |  | 删除标记 |
| quarter | int(4) | YES |  |  | 结算季度 |
| month | int(2) | YES |  |  | 结算月份 |
| customInfo | json | YES |  |  | 自定义信息 |
| sseId | bigint(20) | YES | -1 |  | sse报销单审批行ID,0：会进行匹配跟新 |
| createTime | datetime | YES |  |  | 时间 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| settled | bit(1) | YES | b'0' |  | 结算状态 |
| year | int(4) | YES |  |  | 结算年份 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| dispatchId | dispatchId | 否 | BTREE |
| dispatchSeq | dispatchSeq | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### pm_dispatch_project_settlement_from_d365

**业务含义**：（待补充）

**数据量**：约 44 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| slipId | bigint(20) | YES |  | MUL | ID标识 |
| inventTransId | varchar(20) | YES |  |  | ID标识 |
| vendAccount | varchar(20) | YES |  |  | vendAccount |
| innerInvoiceId | varchar(20) | YES |  |  | ID标识 |
| invoiceDate | timestamp | YES |  |  | 时间 |
| invoiceId | varchar(20) | YES |  |  | ID标识 |
| purchId | varchar(20) | YES |  | MUL | ID标识 |
| purchName | varchar(255) | YES |  |  | 名称 |
| purchPoolId | varchar(10) | YES |  |  | ID标识 |
| packingSlipId | varchar(20) | YES |  | MUL | ID标识 |
| packingSlipRemark | varchar(255) | YES |  |  | packingSlipRemark |
| slipQty | decimal(32,6) | YES |  |  | slipQty |
| receiveQty | decimal(32,6) | YES |  |  | receiveQty |
| invoiceQty | decimal(32,6) | YES |  |  | invoiceQty |
| price | decimal(32,6) | YES |  |  | 价格 |
| invoicePrice | decimal(32,6) | YES |  |  | invoicePrice |
| receiveAmount | decimal(38,6) | YES |  |  | receive金额 |
| invoiceAmount | decimal(38,6) | YES |  |  | 发票金额 |
| settleQty | decimal(38,6) | YES |  |  | settleQty |
| lineAmount | decimal(38,6) | YES |  |  | line金额 |
| invoiceAmountTotal | decimal(32,6) | YES |  |  | invoice金额Total |
| settleAmountTotal | decimal(38,6) | YES |  |  | settle金额Total |
| settleAmount | decimal(38,6) | YES |  |  | settle金额 |
| confirmTime | timestamp | YES |  |  | 时间 |
| settleTime | timestamp | YES |  |  | 时间 |
| projectProgress | varchar(64) | YES |  |  | projectProgress |
| otherSysNum | varchar(20) | YES |  |  | otherSys数量 |
| partition | bigint(20) | YES |  |  | 分区 |
| dataAreaId | varchar(4) | YES |  |  | ID标识 |
| settleId | bigint(20) | YES |  |  | ID标识 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| packingSlipId | packingSlipId | 否 | BTREE |
| purchId | purchId | 否 | BTREE |
| slipId | slipId | 否 | BTREE |

---

### pm_facilitator

**业务含义**：（待补充）

**数据量**：约 24 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| code | varchar(25) | YES |  | UNI | 服务商编号 |
| account | varchar(25) | YES |  | MUL | 服务商账号 |
| name | varchar(64) | YES |  |  | 服务商名 |
| type | varchar(64) | YES |  |  | 合作类型 |
| bankInfo | varchar(255) | YES |  |  | 开户行信息 |
| bankAccount | varchar(64) | YES |  |  | 收款账户 |
| cnapsCode | varchar(25) | YES |  |  | 联行号 |
| contacts | varchar(64) | YES |  |  | 联系人 |
| tel | varchar(64) | YES |  |  | 联系电话 |
| email | varchar(64) | YES |  |  | 联系邮箱 |
| state | bit(1) | YES | b'1' |  | 状态 |
| needApprove | bit(1) | YES | b'0' |  | 是否评审 |
| approveStatus | int(1) | YES | 0 |  | 审批结果 |
| deliveryIds | varchar(25) | YES |  |  | 附件材料 |
| relateType | varchar(25) | YES |  |  | 关联类型 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |
| createBy | varchar(45) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(45) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| customInfo | json | YES |  |  | 自定义信息 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| account | account, state | 是 | BTREE |
| code | code | 是 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### pm_facilitator_form_d365

**业务含义**：（待补充）

**数据量**：约 761 行 | 数据大小：0.2 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| code | varchar(25) | YES |  | UNI | 服务商编号 |
| account | varchar(25) | YES |  | MUL | 服务商账号 |
| name | varchar(64) | YES |  |  | 服务商名 |
| type | varchar(64) | YES |  |  | 合作类型 |
| bankInfo | varchar(255) | YES |  |  | 开户行信息 |
| bankAccount | varchar(64) | YES |  |  | 收款账户 |
| cnapsCode | varchar(25) | YES |  |  | 联行号 |
| contacts | varchar(64) | YES |  |  | 联系人 |
| tel | varchar(64) | YES |  |  | 联系电话 |
| email | varchar(64) | YES |  |  | 联系邮箱 |
| state | bit(1) | YES | b'1' |  | 状态 |
| needApprove | bit(1) | YES | b'0' |  | 是否评审 |
| approveStatus | int(1) | YES | 0 |  | 审批结果 |
| deliveryIds | varchar(25) | YES |  |  | 附件材料 |
| relateType | varchar(25) | YES |  |  | 关联类型 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |
| createBy | varchar(45) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(45) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| customInfo | json | YES |  |  | 自定义信息 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| account | account, code | 是 | BTREE |
| code | code | 是 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### pm_notification_template

**业务含义**：（待补充）

**数据量**：约 66 行 | 数据大小：0.1 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| templateCode | varchar(45) | YES |  |  | 编码 |
| notificationObject | varchar(45) | YES |  |  | 主题 |
| notificationContent | text | YES |  |  | 内容 |
| createTime | datetime | YES |  |  | 时间 |
| createBy | varchar(45) | YES |  |  | 操作人 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### pm_order_data_from_erp

**业务含义**：（待补充）

**数据量**：约 52,940 行 | 数据大小：12.5 MB | 索引大小：10.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| orderNumber | varchar(25) | YES |  | MUL | 排序Number |
| contractNo | varchar(50) | YES |  | MUL | 合同编号 |
| orderExecNumber | varchar(50) | YES |  | MUL | 排序ExecNumber |
| orderCreateTime | datetime | YES |  |  | 时间 |
| customerRequireTime | datetime | YES |  |  | 时间 |
| customerCode | varchar(55) | YES |  |  | 编码 |
| customerName | varchar(255) | YES |  |  | 名称 |
| projectName | varchar(255) | YES |  |  | 名称 |
| orderComment | varchar(2048) | YES |  |  | 排序Comment |
| orderType | int(11) | YES | 0 | MUL | 0 正常销售 1 退货 |
| compCode | varchar(25) | YES | 0 |  | 公司编码 |
| salesType | varchar(25) | YES | 01 |  | 销售类型,01:正常合同，02:借转销，14:销售类借货 |
| source | varchar(25) | YES | SAP |  | 数据来源，默认:SAP,D365,总代借货:SMS |
| customInfo | json | YES |  |  | 自定义字段 |
| syncTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| contractNo | contractNo | 否 | BTREE |
| orderExecNumber | orderExecNumber | 否 | BTREE |
| orderNumber | orderNumber | 否 | BTREE |
| orderType | orderType, salesType | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### pm_order_data_from_erp_d365

**业务含义**：（待补充）

**数据量**：约 1,790 行 | 数据大小：0.4 MB | 索引大小：0.2 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| orderNumber | varchar(25) | YES |  | MUL | 排序Number |
| contractNo | varchar(50) | YES |  | MUL | 合同编号 |
| orderExecNumber | varchar(50) | YES |  | MUL | 排序ExecNumber |
| orderCreateTime | datetime | YES |  |  | 时间 |
| customerRequireTime | datetime | YES |  |  | 时间 |
| customerCode | varchar(55) | YES |  |  | 编码 |
| customerName | varchar(255) | YES |  |  | 名称 |
| projectName | varchar(255) | YES |  |  | 名称 |
| orderComment | varchar(2048) | YES |  |  | 排序Comment |
| orderType | int(11) | YES | 0 |  | 0 正常销售 1 退货 |
| compCode | varchar(25) | YES | 0 |  | 公司编码 |
| salesType | varchar(25) | YES | 01 |  | 销售类型,01:正常合同，02:借转销，14:销售类借货 |
| customInfo | json | YES |  |  | 自定义字段 |
| syncTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| contractNo | contractNo | 否 | BTREE |
| orderExecNumber | orderExecNumber | 否 | BTREE |
| orderNumber | orderNumber | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### pm_order_data_from_erp_sap

**业务含义**：（待补充）

**数据量**：约 47,708 行 | 数据大小：10.5 MB | 索引大小：8.5 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| orderNumber | varchar(25) | YES |  | MUL | 排序Number |
| contractNo | varchar(50) | YES |  | MUL | 合同编号 |
| orderExecNumber | varchar(50) | YES |  | MUL | 排序ExecNumber |
| orderCreateTime | datetime | YES |  |  | 时间 |
| customerRequireTime | datetime | YES |  |  | 时间 |
| customerCode | varchar(55) | YES |  |  | 编码 |
| customerName | varchar(255) | YES |  |  | 名称 |
| projectName | varchar(255) | YES |  |  | 名称 |
| orderComment | varchar(255) | YES |  |  | 排序Comment |
| orderType | int(11) | YES | 0 |  | 0 正常销售 1 退货 |
| compCode | varchar(25) | YES | 0 |  | 公司编码 |
| salesType | varchar(25) | YES | 01 |  | 销售类型,01:正常合同，02:借转销，14:销售类借货 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| contractNo | contractNo | 否 | BTREE |
| orderExecNumber | orderExecNumber | 否 | BTREE |
| orderNumber | orderNumber | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### pm_order_data_from_erp_source

**业务含义**：ERP订单数据源表 - 从ERP系统同步的订单数据

**数据量**：约 49,054 行 | 数据大小：12.5 MB | 索引大小：11.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| orderNumber | varchar(25) | YES |  | MUL | 排序Number |
| contractNo | varchar(50) | YES |  | MUL | 合同编号 |
| orderExecNumber | varchar(50) | YES |  | MUL | 排序ExecNumber |
| orderExecNumberShort | varchar(50) | YES |  |  | 排序ExecNumberShort |
| orderCreateTime | datetime | YES |  |  | 时间 |
| customerRequireTime | datetime | YES |  |  | 时间 |
| customerCode | varchar(55) | YES |  |  | 编码 |
| customerName | varchar(255) | YES |  |  | 名称 |
| projectName | varchar(255) | YES |  |  | 名称 |
| orderComment | varchar(2048) | YES |  |  | 排序Comment |
| orderType | int(11) | YES | 0 | MUL | 0 正常销售 1 退货 |
| compCode | varchar(25) | YES | 0 |  | 公司编码 |
| salesType | varchar(25) | YES | 01 |  | 销售类型,01:正常合同，02:借转销，14:销售类借货 |
| source | varchar(25) | YES | SAP |  | 数据来源，默认:SAP,D365,总代借货:SMS |
| customInfo | json | YES |  |  | 自定义字段 |
| syncTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| contractNo | contractNo | 否 | BTREE |
| orderExecNumber | orderExecNumber | 否 | BTREE |
| orderNumber | orderNumber | 否 | BTREE |
| orderType | orderType, salesType | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

**样例数据**：

| id | orderNumber | contractNo | orderExecNumber | orderExecNumberShort | orderCreateTime | customerRequireTime | customerCode | customerName | projectName | orderComment | orderType | compCode | salesType | source | customInfo | syncTime |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | SO036343 | 31020190911A26 | 1620201909111X305 | 162020190911X305 | 2022-09-01 15:34:09 | 2020-08-31 00:00:00 | R00810 | 国电南瑞科技股份有限公司 | SHDL 100上海电力三层工业交换机协议库存（国网上海市南电力永福10KV线路新建工程项目） | 02050514发V3或V3以下版本的产品
4台02050429改制02050514 V3.00出货 | 0 | 01 | 01 | D365 | None | 2022-11-24 15:33:44 |
| 2 | SO041728 | 31120200708A22 | 1620011806213X301 | 162001180621X301 | 2022-09-01 15:34:37 | 2020-07-11 00:00:00 | C00013 | 齐普生信息科技南京有限公司 | 云南昭通中心城市文化体育产业新区项目后勤服务中心 |  | 0 | 01 | 02 | D365 | None | 2022-11-24 15:33:44 |
| 3 | SO044381 | 31020201109A06 | 1620222011041X301 | 162022201104X301 | 2022-09-01 15:34:10 | 2021-06-10 00:00:00 | C00015 | 重庆新科佳都科技有限公司 | 杭州地铁10号线警用通信安全增补 | 99020529根据研发临技加工发货 | 0 | 01 | 01 | D365 | None | 2022-11-24 15:33:44 |

---

### pm_order_data_from_sap

**对象类型**：VIEW

**数据量**：约 0 行 | 数据大小：0 MB | 索引大小：0 MB

**业务含义**：SAP订单数据视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO | 0 |  | ID标识 |
| orderNumber | varchar(25) | YES |  |  | 排序Number |
| contractNo | varchar(50) | YES |  |  | 合同编号 |
| orderExecNumber | varchar(50) | YES |  |  | 排序ExecNumber |
| orderCreateTime | datetime | YES |  |  | 时间 |
| customerRequireTime | datetime | YES |  |  | 时间 |
| customerCode | varchar(55) | YES |  |  | 编码 |
| customerName | varchar(255) | YES |  |  | 名称 |
| projectName | varchar(255) | YES |  |  | 名称 |
| orderComment | varchar(2048) | YES |  |  | 排序Comment |
| orderType | int(11) | YES | 0 |  | 0 正常销售 1 退货 |
| compCode | varchar(25) | YES | 0 |  | 公司编码 |
| salesType | varchar(25) | YES | 01 |  | 销售类型,01:正常合同，02:借转销，14:销售类借货 |
| source | varchar(25) | YES | SAP |  | 数据来源，默认:SAP,D365,总代借货:SMS |
| customInfo | json | YES |  |  | 自定义字段 |
| syncTime | datetime | YES |  |  | 时间 |

---

### pm_order_data_from_sap_source

**对象类型**：VIEW

**数据量**：约 0 行 | 数据大小：0 MB | 索引大小：0 MB

**业务含义**：SAP订单数据来源视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO | 0 |  | ID标识 |
| orderNumber | varchar(25) | YES |  |  | 排序Number |
| contractNo | varchar(50) | YES |  |  | 合同编号 |
| orderExecNumber | varchar(50) | YES |  |  | 排序ExecNumber |
| orderExecNumberShort | varchar(50) | YES |  |  | 排序ExecNumberShort |
| orderCreateTime | datetime | YES |  |  | 时间 |
| customerRequireTime | datetime | YES |  |  | 时间 |
| customerCode | varchar(55) | YES |  |  | 编码 |
| customerName | varchar(255) | YES |  |  | 名称 |
| projectName | varchar(255) | YES |  |  | 名称 |
| orderComment | varchar(2048) | YES |  |  | 排序Comment |
| orderType | int(11) | YES | 0 |  | 0 正常销售 1 退货 |
| compCode | varchar(25) | YES | 0 |  | 公司编码 |
| salesType | varchar(25) | YES | 01 |  | 销售类型,01:正常合同，02:借转销，14:销售类借货 |
| source | varchar(25) | YES | SAP |  | 数据来源，默认:SAP,D365,总代借货:SMS |
| customInfo | json | YES |  |  | 自定义字段 |
| syncTime | datetime | YES |  |  | 时间 |

---

### pm_order_line_from_erp

**业务含义**：ERP订单行表 - 从ERP同步的订单行明细

**数据量**：约 219,652 行 | 数据大小：26.6 MB | 索引大小：11.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| orderNumber | varchar(25) | YES |  | MUL | 排序Number |
| lineNum | varchar(25) | YES |  |  | line数量 |
| itemCode | varchar(25) | YES |  | MUL | 编码 |
| itemDesc | varchar(255) | YES |  |  | 项目描述 |
| orderQuantity | int(11) | YES |  |  | 排序Quantity |
| openQuantity | int(11) | YES |  |  | openQuantity |
| bundleCode | varchar(25) | YES |  |  | 编码 |
| warrantyMonth | int(11) | YES |  |  | warrantyMonth |
| lineType | int(11) | YES | 0 |  | line类型 |
| compCode | varchar(25) | YES | 0 |  | 公司编码 |
| profitCenter | varchar(25) | YES |  |  | 利润中心 |
| realOrderExecNumber | varchar(25) | YES |  |  | 真实执行单号 |
| source | varchar(25) | YES | SAP |  | 数据来源，默认:SAP,D365,总代借货:SMS |
| customInfo | json | YES |  |  | 自定义字段 |
| syncTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| itemCode | itemCode | 否 | BTREE |
| orderNumber | orderNumber | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### pm_order_line_from_erp_d365

**业务含义**：（待补充）

**数据量**：约 7,839 行 | 数据大小：1.5 MB | 索引大小：0.5 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| orderNumber | varchar(25) | YES |  | MUL | 排序Number |
| lineNum | varchar(25) | YES |  |  | line数量 |
| itemCode | varchar(25) | YES |  | MUL | 编码 |
| itemDesc | varchar(255) | YES |  |  | 项目描述 |
| orderQuantity | int(11) | YES |  |  | 排序Quantity |
| openQuantity | int(11) | YES |  |  | openQuantity |
| bundleCode | varchar(25) | YES |  |  | 编码 |
| warrantyMonth | int(11) | YES |  |  | warrantyMonth |
| lineType | int(11) | YES | 0 |  | line类型 |
| compCode | varchar(25) | YES | 0 |  | 公司编码 |
| profitCenter | varchar(25) | YES |  |  | 利润中心 |
| realOrderExecNumber | varchar(25) | YES |  |  | 真实执行单号 |
| customInfo | json | YES |  |  | 自定义字段 |
| syncTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| itemCode | itemCode | 否 | BTREE |
| orderNumber | orderNumber, lineNum | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### pm_order_line_from_erp_sap

**业务含义**：（待补充）

**数据量**：约 208,448 行 | 数据大小：24.5 MB | 索引大小：16.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| orderNumber | varchar(25) | YES |  | MUL | 排序Number |
| lineNum | int(11) | YES |  |  | line数量 |
| itemCode | varchar(25) | YES |  | MUL | 编码 |
| itemDesc | varchar(255) | YES |  |  | 项目描述 |
| orderQuantity | int(11) | YES |  |  | 排序Quantity |
| openQuantity | int(11) | YES |  |  | openQuantity |
| bundleCode | varchar(25) | YES |  |  | 编码 |
| warrantyMonth | int(11) | YES |  |  | warrantyMonth |
| lineType | int(11) | YES | 0 |  | line类型 |
| compCode | varchar(25) | YES | 0 |  | 公司编码 |
| profitCenter | varchar(25) | YES |  |  | 利润中心 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| itemCode | itemCode | 否 | BTREE |
| orderNumber | orderNumber, lineNum | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### pm_order_line_from_erp_source

**业务含义**：（待补充）

**数据量**：约 205,968 行 | 数据大小：26.6 MB | 索引大小：15.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| orderNumber | varchar(25) | YES |  | MUL | 排序Number |
| lineNum | varchar(25) | YES |  |  | line数量 |
| itemCode | varchar(25) | YES |  | MUL | 编码 |
| itemDesc | varchar(255) | YES |  |  | 项目描述 |
| orderQuantity | int(11) | YES |  |  | 排序Quantity |
| openQuantity | int(11) | YES |  |  | openQuantity |
| bundleCode | varchar(25) | YES |  |  | 编码 |
| warrantyMonth | int(11) | YES |  |  | warrantyMonth |
| lineType | int(11) | YES | 0 |  | line类型 |
| compCode | varchar(25) | YES | 0 |  | 公司编码 |
| profitCenter | varchar(25) | YES |  |  | 利润中心 |
| realOrderExecNumber | varchar(25) | YES |  |  | 真实执行单号 |
| source | varchar(25) | YES | SAP |  | 数据来源，默认:SAP,D365,总代借货:SMS |
| customInfo | json | YES |  |  | 自定义字段 |
| syncTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| itemCode | itemCode | 否 | BTREE |
| orderNumber | orderNumber, lineNum | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### pm_order_line_from_sap

**对象类型**：VIEW

**数据量**：约 0 行 | 数据大小：0 MB | 索引大小：0 MB

**业务含义**：SAP订单行视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO | 0 |  | ID标识 |
| orderNumber | varchar(25) | YES |  |  | 排序Number |
| lineNum | varchar(25) | YES |  |  | line数量 |
| itemCode | varchar(25) | YES |  |  | 编码 |
| itemDesc | varchar(255) | YES |  |  | 项目描述 |
| orderQuantity | int(11) | YES |  |  | 排序Quantity |
| openQuantity | int(11) | YES |  |  | openQuantity |
| bundleCode | varchar(25) | YES |  |  | 编码 |
| warrantyMonth | int(11) | YES |  |  | warrantyMonth |
| lineType | int(11) | YES | 0 |  | line类型 |
| compCode | varchar(25) | YES | 0 |  | 公司编码 |
| profitCenter | varchar(25) | YES |  |  | 利润中心 |
| realOrderExecNumber | varchar(25) | YES |  |  | 真实执行单号 |
| source | varchar(25) | YES | SAP |  | 数据来源，默认:SAP,D365,总代借货:SMS |
| customInfo | json | YES |  |  | 自定义字段 |
| syncTime | datetime | YES |  |  | 时间 |

---

### pm_order_line_from_sap_source

**对象类型**：VIEW

**数据量**：约 0 行 | 数据大小：0 MB | 索引大小：0 MB

**业务含义**：SAP订单行来源视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO | 0 |  | ID标识 |
| orderNumber | varchar(25) | YES |  |  | 排序Number |
| lineNum | varchar(25) | YES |  |  | line数量 |
| itemCode | varchar(25) | YES |  |  | 编码 |
| itemDesc | varchar(255) | YES |  |  | 项目描述 |
| orderQuantity | int(11) | YES |  |  | 排序Quantity |
| openQuantity | int(11) | YES |  |  | openQuantity |
| bundleCode | varchar(25) | YES |  |  | 编码 |
| warrantyMonth | int(11) | YES |  |  | warrantyMonth |
| lineType | int(11) | YES | 0 |  | line类型 |
| compCode | varchar(25) | YES | 0 |  | 公司编码 |
| profitCenter | varchar(25) | YES |  |  | 利润中心 |
| realOrderExecNumber | varchar(25) | YES |  |  | 真实执行单号 |
| source | varchar(25) | YES | SAP |  | 数据来源，默认:SAP,D365,总代借货:SMS |
| customInfo | json | YES |  |  | 自定义字段 |
| syncTime | datetime | YES |  |  | 时间 |

---

### pm_pb_plan_from_sms

**业务含义**：（待补充）

**数据量**：约 43,912 行 | 数据大小：6.0 MB | 索引大小：2.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 主键（自增） |
| contractNo | varchar(25) | YES |  | MUL | 合同号 |
| batchCode | varchar(10) | YES |  |  | 批次信息 |
| basicDataName | varchar(20) | YES |  |  | 活动（款项名称） |
| referenceEventName | varchar(20) | YES |  |  | 参照事件名称 |
| eventPlanHappenDate | datetime | YES |  |  | 事件计划发生日期 |
| afterDaysNum | int(11) | YES | 0 |  | 后推天数 |
| eventActualFinishDate | datetime | YES |  |  | 事件实际完成日期 |
| marketingFeedback | varchar(2000) | YES |  |  | 销售反馈 |
| createBy | varchar(45) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(45) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |
| dataSource | varchar(25) | YES | SMS |  | dataSource |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| contractNo_IDX | contractNo | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### pm_pb_plan_from_sms_history

**业务含义**：（待补充）

**数据量**：约 16,133 行 | 数据大小：2.5 MB | 索引大小：0.4 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 主键（自增） |
| contractNo | varchar(25) | YES |  | MUL | 合同号 |
| batchCode | varchar(10) | YES |  |  | 批次信息 |
| basicDataName | varchar(20) | YES |  |  | 活动（款项名称） |
| referenceEventName | varchar(20) | YES |  |  | 参照事件名称 |
| eventPlanHappenDate | datetime | YES |  |  | 事件计划发生日期 |
| afterDaysNum | int(11) | YES | 0 |  | 后推天数 |
| eventActualFinishDate | datetime | YES |  |  | 事件实际完成日期 |
| marketingFeedback | varchar(2000) | YES |  |  | 销售反馈 |
| createBy | varchar(10) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(10) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |
| dataSource | varchar(25) | YES | SMS |  | dataSource |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| contractNo_IDX | contractNo | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### pm_person_from_oa

**业务含义**：（待补充）

**数据量**：约 1,480 行 | 数据大小：0.1 MB | 索引大小：0.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| salesmanCode | varchar(45) | YES |  | MUL | 编码 |
| salesmanTel | varchar(45) | YES |  |  | salesmanTel |
| salesmanName | varchar(45) | YES |  |  | 名称 |
| salesmanMail | varchar(100) | YES |  |  | salesmanMail |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| salesmanCode1 | salesmanCode | 否 | BTREE |

---

### pm_presales_lend_2_delivery_off_from_sap

**业务含义**：（待补充）

**数据量**：约 44,530 行 | 数据大小：4.5 MB | 索引大小：6.5 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| orderNumber | varchar(11) | YES |  | MUL | 排序Number |
| lineId | int(11) | YES |  |  | ID标识 |
| itemCode | varchar(10) | YES |  |  | 编码 |
| ppliCode | varchar(25) | YES |  | MUL | 借货执行单号 |
| contract | varchar(25) | YES |  | MUL | 合同 |
| deliveryDate | date | YES |  |  | 发货时间 |
| rmaDate | date | YES |  |  | 退货时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| contract | contract | 否 | BTREE |
| orderNumber | orderNumber, lineId | 否 | BTREE |
| ppliCode | ppliCode | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### pm_presales_lend_2_rma_from_sms

**业务含义**：（待补充）

**数据量**：约 0 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO | 0 |  | ID标识 |
| orderNumber | varchar(11) | YES |  | MUL | 排序Number |
| ppliCode | varchar(25) | YES |  | MUL | 借货执行单号 |
| orderType | varchar(10) | YES |  |  | 订单类型 |
| contract | varchar(25) | YES |  | MUL | 合同 |
| customer | varchar(255) | YES |  |  | 客户 |
| projectName | varchar(255) | YES |  |  | 名称 |
| businessunit | varchar(50) | YES |  |  | 业务单元 |
| office | varchar(20) | YES |  |  | 办事处 |
| dutyperson | varchar(10) | YES |  |  | 责任人 |
| itemcode | varchar(10) | YES |  |  | 编码 |
| description | varchar(255) | YES |  |  | 备注/描述 |
| productfirstName | varchar(255) | YES |  |  | 名称 |
| productName | varchar(255) | YES |  |  | 名称 |
| orderQty | int(11) | YES |  |  | 排序Qty |
| dlvQty | int(11) | YES |  |  | dlvQty |
| rmaQty | int(11) | YES |  |  | rmaQty |
| lineStatus | varchar(5) | YES |  |  | line状态 |
| createDate | date | YES |  |  | 时间 |
| lineId | int(11) | YES |  |  | ID标识 |
| systemId | int(11) | YES |  |  | ID标识 |
| canceled | char(1) | YES |  |  | 是否取消 |
| dataSource | varchar(25) | YES | SMS |  | dataSource |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| contract | contract, itemcode | 否 | BTREE |
| orderNumber | orderNumber, lineId | 否 | BTREE |
| ppliCode | ppliCode, itemcode | 否 | BTREE |

---

### pm_presales_lend_2_rma_from_sms_history

**业务含义**：（待补充）

**数据量**：约 35,449 行 | 数据大小：14.5 MB | 索引大小：4.5 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO | 0 |  | ID标识 |
| orderNumber | varchar(11) | YES |  | MUL | 排序Number |
| ppliCode | varchar(25) | YES |  | MUL | 借货执行单号 |
| orderType | varchar(10) | YES |  |  | 订单类型 |
| contract | varchar(25) | YES |  | MUL | 合同 |
| customer | varchar(255) | YES |  |  | 客户 |
| projectName | varchar(255) | YES |  |  | 名称 |
| businessunit | varchar(50) | YES |  |  | 业务单元 |
| office | varchar(20) | YES |  |  | 办事处 |
| dutyperson | varchar(10) | YES |  |  | 责任人 |
| itemcode | varchar(10) | YES |  |  | 编码 |
| description | varchar(255) | YES |  |  | 备注/描述 |
| productfirstName | varchar(255) | YES |  |  | 名称 |
| productName | varchar(255) | YES |  |  | 名称 |
| orderQty | int(11) | YES |  |  | 排序Qty |
| dlvQty | int(11) | YES |  |  | dlvQty |
| rmaQty | int(11) | YES |  |  | rmaQty |
| lineStatus | varchar(5) | YES |  |  | line状态 |
| createDate | date | YES |  |  | 时间 |
| lineId | int(11) | YES |  |  | ID标识 |
| systemId | int(11) | YES |  |  | ID标识 |
| canceled | char(1) | YES |  |  | 是否取消 |
| dataSource | varchar(25) | YES | SMS |  | dataSource |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| contract | contract, itemcode | 否 | BTREE |
| orderNumber | orderNumber, lineId | 否 | BTREE |
| ppliCode | ppliCode, itemcode | 否 | BTREE |

---

### pm_presales_lend_2_sale_from_sms

**业务含义**：（待补充）

**数据量**：约 0 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| productfirstName | varchar(255) | YES |  |  | 名称 |
| productName | varchar(128) | YES |  |  | 名称 |
| projectCode | varchar(255) | NO |  | MUL | 编码 |
| productSubCode | varchar(255) | NO |  |  | 编码 |
| productSubModel | varchar(255) | YES |  |  | productSubModel |
| productSubName | varchar(255) | YES |  |  | 名称 |
| num | int(11) | NO |  |  | 数量 |
| borrowNum | int(11) | YES |  |  | borrow数量 |
| contract | varchar(255) | YES |  |  | 合同 |
| memo | text | YES |  |  | 备注 |
| dataSource | varchar(25) | YES | SMS |  | dataSource |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| projectCode | projectCode | 否 | BTREE |

---

### pm_presales_lend_2_sale_from_sms_history

**业务含义**：（待补充）

**数据量**：约 13,535 行 | 数据大小：3.5 MB | 索引大小：0.4 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| productfirstName | varchar(255) | YES |  |  | 名称 |
| productName | varchar(128) | YES |  |  | 名称 |
| projectCode | varchar(255) | NO |  | MUL | 编码 |
| productSubCode | varchar(255) | NO |  |  | 编码 |
| productSubModel | varchar(255) | YES |  |  | productSubModel |
| productSubName | varchar(255) | YES |  |  | 名称 |
| num | int(11) | NO |  |  | 数量 |
| borrowNum | int(11) | YES |  |  | borrow数量 |
| contract | varchar(255) | YES |  |  | 合同 |
| memo | text | YES |  |  | 备注 |
| dataSource | varchar(25) | YES | SMS |  | dataSource |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| projectCode | projectCode | 否 | BTREE |

---

### pm_presales_lend_detail_from_oa

**业务含义**：（待补充）

**数据量**：约 3,024 行 | 数据大小：0.3 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | bigint(20) | NO |  |  | ID标识 |
| infoId | varchar(64) | YES |  |  | ID标识 |
| contractNum | varchar(100) | YES |  |  | contract数量 |
| deviceSerialnum | varchar(100) | YES |  |  | deviceSerialnum |
| modelNum | varchar(100) | YES |  |  | model数量 |
| applyCount | int(11) | YES |  |  | apply计数 |
| isSoftware | varchar(255) | YES |  |  | 是否Software |
| customInfo | json | YES |  |  | customInfo |

---

### pm_presales_lend_info_from_crm

**业务含义**：（待补充）

**数据量**：约 0 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(1) | NO |  | PRI | ID标识（自增） |
| lendInfoId | varchar(64) | NO | 0 |  | ID标识 |
| projectCode | varchar(64) | YES |  |  | 编码 |
| projectName | varchar(765) | YES |  |  | 名称 |
| dutyName | varchar(189) | YES |  |  | 名称 |
| dutyContactWay | varchar(300) | YES |  |  | dutyContactWay |
| decPath | varchar(765) | YES |  |  | decPath |
| officeCode | varchar(765) | YES |  |  | 编码 |
| marketName | varchar(255) | YES |  |  | 名称 |
| systemName | varchar(128) | YES |  |  | 名称 |
| expendName | varchar(255) | YES |  |  | 名称 |
| industryName | varchar(128) | YES |  |  | 名称 |
| pspm | varchar(257) | YES |  |  | PSPM编号 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### pm_presales_lend_info_from_oa

**业务含义**：（待补充）

**数据量**：约 1,963 行 | 数据大小：1.5 MB | 索引大小：0.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| projectCode | varchar(100) | YES |  |  | 编码 |
| processStartTime | datetime | YES |  |  | 时间 |
| lendInfoId | varchar(64) | NO |  | MUL | ID标识 |
| processOrderNum | varchar(100) | YES |  |  | process排序数量 |
| applyUserCode | varchar(25) | YES |  |  | 编码 |
| applyUserName | varchar(25) | YES |  |  | 名称 |
| applyDeptCode | varchar(25) | YES |  |  | 编码 |
| applyDeptName | varchar(25) | YES |  |  | 名称 |
| applyDate | datetime | YES |  |  | 时间 |
| projectName | varchar(100) | YES |  |  | 名称 |
| applyType | bigint(20) | YES |  |  | apply类型 |
| applyTypeName | varchar(25) | YES |  |  | 名称 |
| salesUserCode | varchar(25) | YES |  |  | 编码 |
| salesUserName | varchar(25) | YES |  |  | 名称 |
| salesUserMobile | varchar(100) | YES |  |  | salesUserMobile |
| productLine | bigint(20) | YES |  |  | 产品线 |
| productLineName | varchar(255) | YES |  |  | 名称 |
| marketName | varchar(255) | YES |  |  | 名称 |
| systemName | varchar(255) | YES |  |  | 名称 |
| expendName | varchar(255) | YES |  |  | 名称 |
| industryName | varchar(255) | YES |  |  | 名称 |
| applyCause | varchar(255) | YES |  |  | applyCause |
| followUpPlan | varchar(255) | YES |  |  | followUpPlan |
| testStartTime | datetime | YES |  |  | 时间 |
| testEndTime | datetime | YES |  |  | 时间 |
| authPlanDate | datetime | YES |  |  | 时间 |
| authDate | datetime | YES |  |  | 时间 |
| resellSuccessfully | varchar(255) | YES |  |  | resellSuccessfully |
| useDays | int(11) | YES |  |  | useDays |
| resaleCertificateFile | varchar(2048) | YES |  |  | resaleCertificateFile |
| provideAuthFile | varchar(2048) | YES |  |  | provideAuthFile |
| infoFile | varchar(2048) | YES |  |  | infoFile |
| customInfo | json | YES |  |  | 自定义信息 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| lendInfoId | lendInfoId | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### pm_presales_lend_info_from_sms

**业务含义**：（待补充）

**数据量**：约 145 行 | 数据大小：0.1 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(1) | NO |  | PRI | ID标识（自增） |
| lendInfoId | varchar(64) | NO | 0 |  | ID标识 |
| projectCode | varchar(64) | YES |  |  | 编码 |
| projectName | varchar(765) | YES |  |  | 名称 |
| dutyName | varchar(189) | YES |  |  | 名称 |
| dutyContactWay | varchar(300) | YES |  |  | dutyContactWay |
| decPath | varchar(765) | YES |  |  | decPath |
| officeCode | varchar(765) | YES |  |  | 编码 |
| marketName | varchar(255) | YES |  |  | 名称 |
| systemName | varchar(128) | YES |  |  | 名称 |
| expendName | varchar(255) | YES |  |  | 名称 |
| industryName | varchar(128) | YES |  |  | 名称 |
| pspm | varchar(257) | YES |  |  | PSPM编号 |
| dataSource | varchar(25) | YES | SMS |  | dataSource |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### pm_presales_lend_info_from_sms_history

**业务含义**：（待补充）

**数据量**：约 0 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(1) | NO |  | PRI | ID标识（自增） |
| lendInfoId | varchar(64) | NO | 0 |  | ID标识 |
| projectCode | varchar(64) | YES |  |  | 编码 |
| projectName | varchar(765) | YES |  |  | 名称 |
| dutyName | varchar(189) | YES |  |  | 名称 |
| dutyContactWay | varchar(300) | YES |  |  | dutyContactWay |
| decPath | varchar(765) | YES |  |  | decPath |
| officeCode | varchar(765) | YES |  |  | 编码 |
| marketName | varchar(255) | YES |  |  | 名称 |
| systemName | varchar(128) | YES |  |  | 名称 |
| expendName | varchar(255) | YES |  |  | 名称 |
| industryName | varchar(128) | YES |  |  | 名称 |
| pspm | varchar(257) | YES |  |  | PSPM编号 |
| dataSource | varchar(25) | YES | SMS |  | dataSource |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### pm_presales_lend_order_from_sms

**业务含义**：（待补充）

**数据量**：约 0 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO | 0 |  | ID标识 |
| orderNumber | varchar(11) | YES |  |  | 排序Number |
| ppliCode | varchar(25) | YES |  |  | 借货执行单号 |
| orderType | varchar(10) | YES |  |  | 订单类型 |
| contract | varchar(25) | YES |  |  | 合同 |
| customer | varchar(255) | YES |  |  | 客户 |
| projectName | varchar(255) | YES |  |  | 名称 |
| businessunit | varchar(50) | YES |  |  | 业务单元 |
| office | varchar(10) | YES |  |  | 办事处 |
| dutyperson | varchar(10) | YES |  |  | 责任人 |
| itemcode | varchar(10) | YES |  |  | 编码 |
| description | varchar(255) | YES |  |  | 备注/描述 |
| orderQty | int(11) | YES |  |  | 排序Qty |
| dlvQty | int(11) | YES |  |  | dlvQty |
| rmaQty | int(11) | YES |  |  | rmaQty |
| lineStatus | varchar(5) | YES |  |  | line状态 |
| createDate | date | YES |  |  | 时间 |
| lineId | int(11) | YES |  |  | ID标识 |
| systemId | int(11) | YES |  |  | ID标识 |
| canceled | char(1) | YES |  |  | 是否取消 |
| discountVersion | varchar(255) | YES |  |  | discountVersion |
| borrowNum | bigint(12) | YES |  |  | borrow数量 |
| dataSource | varchar(25) | YES | SMS |  | dataSource |

---

### pm_presales_lend_order_from_sms_history

**业务含义**：（待补充）

**数据量**：约 20,196 行 | 数据大小：7.5 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO | 0 |  | ID标识 |
| orderNumber | varchar(11) | YES |  |  | 排序Number |
| ppliCode | varchar(25) | YES |  |  | 借货执行单号 |
| orderType | varchar(10) | YES |  |  | 订单类型 |
| contract | varchar(25) | YES |  |  | 合同 |
| customer | varchar(255) | YES |  |  | 客户 |
| projectName | varchar(255) | YES |  |  | 名称 |
| businessunit | varchar(50) | YES |  |  | 业务单元 |
| office | varchar(10) | YES |  |  | 办事处 |
| dutyperson | varchar(10) | YES |  |  | 责任人 |
| itemcode | varchar(10) | YES |  |  | 编码 |
| description | varchar(255) | YES |  |  | 备注/描述 |
| orderQty | int(11) | YES |  |  | 排序Qty |
| dlvQty | int(11) | YES |  |  | dlvQty |
| rmaQty | int(11) | YES |  |  | rmaQty |
| lineStatus | varchar(5) | YES |  |  | line状态 |
| createDate | date | YES |  |  | 时间 |
| lineId | int(11) | YES |  |  | ID标识 |
| systemId | int(11) | YES |  |  | ID标识 |
| canceled | char(1) | YES |  |  | 是否取消 |
| discountVersion | varchar(255) | YES |  |  | discountVersion |
| borrowNum | bigint(12) | YES |  |  | borrow数量 |
| dataSource | varchar(25) | YES | SMS |  | dataSource |

---

### pm_presales_lend_product_from_sms

**业务含义**：（待补充）

**数据量**：约 478 行 | 数据大小：0.1 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| lendInfoId | varchar(64) | NO |  |  | ID标识 |
| productfirstName | varchar(255) | YES |  |  | 名称 |
| productName | varchar(128) | YES |  |  | 名称 |
| productsubCode | varchar(765) | YES |  |  | 编码 |
| productSubModel | varchar(765) | YES |  |  | productSubModel |
| productSubName | varchar(765) | YES |  |  | 名称 |
| lendNum | int(11) | YES |  |  | lend数量 |
| memo | text | YES |  |  | 备注 |
| dataSource | varchar(25) | YES | SMS |  | dataSource |
| productfirstCode | varchar(64) | YES |  |  | 编码 |
| productCode | varchar(64) | YES |  |  | 编码 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### pm_presales_lend_product_from_sms_history

**业务含义**：（待补充）

**数据量**：约 4 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| lendInfoId | varchar(64) | NO |  |  | ID标识 |
| productfirstName | varchar(255) | YES |  |  | 名称 |
| productName | varchar(128) | YES |  |  | 名称 |
| productsubCode | varchar(765) | YES |  |  | 编码 |
| productSubModel | varchar(765) | YES |  |  | productSubModel |
| productSubName | varchar(765) | YES |  |  | 名称 |
| lendNum | int(11) | YES |  |  | lend数量 |
| memo | text | YES |  |  | 备注 |
| dataSource | varchar(25) | YES | SMS |  | dataSource |
| productfirstCode | varchar(64) | YES |  |  | 编码 |
| productCode | varchar(64) | YES |  |  | 编码 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### pm_presales_project_callback

**业务含义**：（待补充）

**数据量**：约 1,865 行 | 数据大小：0.2 MB | 索引大小：0.2 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 售前回访问卷表（自增） |
| presalesId | int(11) | YES |  | MUL | 售前项目ID |
| taskId | varchar(25) | YES |  | MUL | 任务ID |
| quesnaireId | int(11) | YES |  | MUL | 问卷ID |
| quesnaireVersion | int(11) | YES |  |  | 问卷版本 |
| quesnaireState | int(11) | YES |  |  | 状态 -1 草稿 1已提交 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| presalesId | presalesId | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| quesnaireId | quesnaireId | 否 | BTREE |
| taskId | taskId | 否 | BTREE |

---

### pm_presales_project_duration

**业务含义**：售前项目工期表 - 记录售前项目工期信息

**数据量**：约 4,320 行 | 数据大小：0.4 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| presalesId | int(11) | NO |  | PRI | ID标识 |
| instId | int(11) | YES |  |  | 流程实例ID |
| totalDuration | varchar(20) | YES |  |  | 开始时间 |
| serviceDuration | varchar(20) | YES |  |  | 指派服务经理时间 |
| programDuration | varchar(20) | YES |  |  | 指派项目经理时间 |
| testDuration | varchar(20) | YES |  |  | 测试开始时间 |
| callbackDuration | varchar(20) | YES |  |  | 回访开始时间 |
| serviceApproveDuration | varchar(100) | YES |  |  | 服务经理审批时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | presalesId | 是 | BTREE |

**外键关系**：

- `presalesId` → `pm_presales_project_header.presalesId`（约束名：pm_presales_project_duration_ibfk_1）

---

### pm_presales_project_header

**业务含义**：售前项目主表 - 存储售前项目申请信息

**数据量**：约 16,660 行 | 数据大小：6.5 MB | 索引大小：2.3 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| presalesId | int(11) | NO |  | PRI | 售前项目主表（自增） |
| instId | varchar(64) | YES |  | MUL | activity工作流流程ID |
| applyState | int(11) | YES |  |  | 申请状态：1=待审批, 2=已审批 |
| applyBy | varchar(25) | YES |  |  | 申请人 |
| applyTime | datetime | YES |  |  | 申请时间 |
| endTime | datetime | YES |  |  | 申请结束时间 |
| projectState | varchar(25) | YES | 10 |  | 项目状态 ，同售后项目状态  10 未创建 20 直接闭环 30 已创建 31待指派项目经理 32 项目经理跟踪 33工程管理部回访 100闭环 |
| presalesCode | varchar(64) | YES |  |  | 售前项目编码 |
| projectCode | varchar(64) | YES |  | MUL | 项目编码 |
| projectName | varchar(255) | YES |  |  | 项目名称 |
| projectType | varchar(25) | YES |  |  | project类型 |
| marketName | varchar(25) | YES |  |  | 市场部名称 |
| systemName | varchar(25) | YES |  |  | 系统部名称 |
| expendName | varchar(25) | YES |  |  | 拓展部名称 |
| industryName | varchar(25) | YES |  |  | 子行业名称 |
| officeCode | varchar(25) | YES |  |  | 办事处编码 |
| salesman | varchar(25) | YES |  |  | 销售人员 |
| productManager | varchar(25) | YES |  |  | 产品经理 |
| salesmanLink | varchar(125) | YES |  |  | 销售人员联系方式 |
| lendInfoId | varchar(64) | YES |  | MUL | SMS系统测试类借货申请主键，标识存在则不再刷新过来 |
| lendfiles | varchar(2048) | YES |  |  | 借货交付件 从SMS中同步过来 |
| confirmFileIds | varchar(2048) | YES |  |  | 现场测试服务确认单 |
| hasRma | int(1) | YES | 0 |  | 是否有未核销数据 |
| hasTransfer | int(1) | YES | 0 |  | 是否发生借转销 |
| closeRemark | varchar(512) | YES |  |  | 闭环备注 |
| createBy | varchar(25) | YES |  |  | 数据创建人 |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  | 数据创建时间 |
| updateBy | varchar(25) | YES |  |  | 数据更新人 |
| updateTime | datetime | YES |  |  | 数据更新时间 |
| effectiveFrom | datetime | YES |  |  | 数据有效开始时间 |
| effectiveTo | datetime | YES |  |  | 数据有效结束时间 |
| source | varchar(25) | NO | SMS |  | 数据来源 |
| customInfo | json | YES |  |  | 自定义信息 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| instId | instId | 否 | BTREE |
| lendInfoId | lendInfoId | 否 | BTREE |
| PRIMARY | presalesId | 是 | BTREE |
| projectCode | projectCode | 否 | BTREE |

**样例数据**：

| presalesId | instId | applyState | applyBy | applyTime | endTime | projectState | presalesCode | projectCode | projectName | projectType | marketName | systemName | expendName | industryName | officeCode | salesman | productManager | salesmanLink | lendInfoId | lendfiles | confirmFileIds | hasRma | hasTransfer | closeRemark | createBy | createTime | updateBy | updateTime | effectiveFrom | effectiveTo | source | customInfo |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1000000 | None | 2 | None | None | None | 100 | 16100014051401N-0 | 16100014051401N | 全军集采入围 | None | 专网营销部 | 专网 | 专网 | 专网 | 161000 | s00790-苏国晓 | None | None | 1 | None | None | 0 | 0 | None | None | None | None | None | 2016-01-05 23:35:00 | None | SMS | None |
| 1000001 | None | 2 | None | None | None | 100 | 16100014081803N-0 | 16100014081803N | 某工程一期预研项目功能验证 | None | 专网营销部 | 专网 | 专网 | 专网 | 161000 | z01250-张玉龙 | x01495-许彦营 | None | 2 | None | None | 0 | 0 | None | None | None | None | None | 2016-01-05 23:35:00 | None | SMS | None |
| 1000002 | None | 2 | None | None | None | 100 | 16110014011311N-0 | 16110014011311N | 墨西哥运营商防火墙采购 | None | 战略合作部 | 战略 | 战略 | 战略 | 161100 | z00413-周律 | None | None | 3 | None | None | 0 | 0 | None | None | None | None | None | 2016-01-05 23:35:00 | None | SMS | None |

---

### pm_presales_project_product_line

**业务含义**：售前项目产品线表 - 记录售前项目关联的产品线

**数据量**：约 4,358,185 行 | 数据大小：879.0 MB | 索引大小：130.2 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| productLineId | int(11) | NO |  | PRI | ID标识（自增） |
| presalesId | int(11) | YES |  | MUL | 售前项目ID |
| lendInfoId | varchar(64) | YES |  | MUL | 借货主表主键 |
| productFirstName | varchar(255) | YES |  |  | 产品一级 |
| productTypeName | varchar(255) | YES |  |  | 产品类别 |
| itemCode | varchar(255) | YES |  |  | item编码 |
| itemModel | varchar(255) | YES |  |  | item型号 |
| itemDesc | text | YES |  |  | item描述 |
| price | double | YES |  |  | 目录价 |
| productNum | int(11) | NO | 0 |  | 产品数量 |
| orderNum | int(11) | NO | 0 |  | 下单数量 |
| deliverNum | int(11) | NO | 0 |  | 发货数量 |
| hexiaoNum | int(11) | NO | 0 |  | 核销数量 |
| transferNum | int(11) | NO | 0 |  | 转销数量 |
| remark | text | YES |  |  | 备注 |
| effectiveFrom | datetime | YES |  |  | 数据有效开始时间 |
| effectiveTo | datetime | YES |  |  | 数据有效结束时间 |
| source | varchar(25) | YES | SMS |  | 来源 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| lendInfoId | lendInfoId | 否 | BTREE |
| presalesId | presalesId | 否 | BTREE |
| PRIMARY | productLineId | 是 | BTREE |

---

### pm_presales_project_rma_info

**业务含义**：（待补充）

**数据量**：约 62,906 行 | 数据大小：14.5 MB | 索引大小：6.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| orderNumber | varchar(11) | YES |  |  | 排序Number |
| ppliCode | varchar(25) | YES |  |  | 编码 |
| orderType | varchar(10) | YES |  |  | 订单类型 |
| contract | varchar(25) | YES |  | MUL | 合同 |
| itemcode | varchar(10) | YES |  | MUL | 编码 |
| itemModel | varchar(255) | YES |  |  | 项目Model |
| description | varchar(255) | YES |  |  | 备注/描述 |
| productfirstName | varchar(255) | YES |  |  | 名称 |
| productName | varchar(255) | YES |  |  | 名称 |
| orderQty | decimal(32,0) | YES |  |  | 排序Qty |
| dlvQty | decimal(32,0) | YES |  |  | dlvQty |
| rmaQty | decimal(32,0) | YES |  |  | rmaQty |
| createDate | date | YES |  |  | 时间 |
| canceled | char(1) | YES |  |  | 是否取消 |
| deliveryDate | date | YES |  |  | 时间 |
| rmaDate | date | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| contract | contract | 否 | BTREE |
| itemcode | itemcode | 否 | BTREE |

---

### pm_product_info_from_crm

**业务含义**：（待补充）

**数据量**：约 8,469 行 | 数据大小：2.5 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| itemCode | varchar(100) | YES |  |  | item编码 |
| productCode | varchar(100) | YES |  |  | 产品大类 |
| productSubCode | varchar(100) | YES |  |  | 产品小类 |
| itemModel | varchar(100) | YES |  |  | 产品型号 |
| itemDesc | varchar(500) | YES |  |  | 产品描述 |
| remark | text | YES |  |  | 备注 |
| status | int(11) | YES |  |  | 状态 |
| BU | varchar(100) | YES |  |  | 业务单元 |
| productLine | varchar(100) | YES |  |  | 产品线 |
| orgId | int(11) | NO |  |  | ID标识 |
| createTime | datetime | YES |  |  | 时间 |
| updateTime | datetime | YES |  |  | 时间 |
| statecode | int(11) | NO |  |  | 编码 |
| statuscode | int(11) | YES |  |  | 编码 |
| productStage | int(11) | YES |  |  | productStage |
| endOfSaleDate | datetime | YES |  |  | 停止销售时间 |
| endOfSupportDate | datetime | YES |  |  | 停止支持时间 |
| endOfLifeDate | datetime | YES |  |  | 停止生产时间 |
| lastRenewalDate | datetime | YES |  |  | 停止续保时间 |
| dataSource | varchar(100) | YES |  |  | 数据来源 |

---

### pm_project

**业务含义**：项目主表 - 存储项目基本信息，包括项目类型、状态、编码、名称等

**数据量**：约 70,370 行 | 数据大小：45.6 MB | 索引大小：11.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| projectId | int(11) | NO |  | PRI | 项目ID（主键）（自增） |
| projectType | varchar(45) | NO | 10 | MUL | 项目类型：10=实施类, afss=安全服务, afxx=安全营销 |
| projectCode | varchar(45) | NO |  | MUL | 项目编码 |
| projectName | varchar(200) | YES |  |  | 项目名称 |
| projectState | varchar(11) | YES |  |  | 项目状态：10=待确认, 20=进行中, 30=已暂停, 31=暂停待确认, 32=暂停中, 40=待关闭, 50=已关闭, 100=已完成 |
| isback | varchar(11) | YES | 30 |  | 回退状态 |
| column001 | varchar(255) | YES |  | MUL | 办事处编码 |
| column002 | varchar(255) | YES |  |  | 客户编码 |
| column003 | varchar(255) | YES |  |  | 客户名称 |
| column004 | varchar(255) | YES |  |  | 市场部编码 |
| column005 | varchar(255) | YES |  |  | 系统部/行业 |
| column006 | varchar(255) | YES |  |  | 拓展部/客户类型 |
| column007 | varchar(255) | YES |  |  | 子行业 |
| column008 | varchar(255) | YES |  |  | 不予跟踪原因 |
| column009 | datetime | YES |  |  | 订单创建时间 |
| column010 | varchar(10) | YES |  |  | 项目阶段 |
| column011 | varchar(10) | YES |  |  | 项目子阶段 |
| column012 | varchar(2) | YES |  |  | 项目标记 |
| columno12_readonly | int(2) | YES | -1 |  | column012只读标记 |
| column013 | varchar(255) | YES |  |  | 最终用户 |
| column014 | text | YES |  |  | 备注 |
| customerProjectName | varchar(255) | YES |  |  | 客户项目名称 |
| salesType | varchar(25) | YES | 01 |  | 销售类型：01=直销, 02=渠道 |
| majorProjectLevel | varchar(255) | YES |  |  | 重大项目级别 |
| compId | int(2) | YES | 0 |  | 公司ID |
| createTime | datetime | YES |  |  | 创建时间 |
| createBy | varchar(45) | YES |  |  | 创建人 |
| updateTime | datetime | YES |  |  | 更新时间 |
| updateBy | varchar(45) | YES |  |  | 更新人 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |
| disabled | bit(1) | YES | b'0' |  | 是否禁用 |
| projectStartTime | datetime | YES |  |  | 项目开始时间 |
| projectRefreshTime | datetime | YES |  |  | 项目刷新时间 |
| projectCloseTime | datetime | YES |  |  | 项目关闭时间 |
| customInfo | json | YES |  |  | 自定义信息 |
| customConfig | json | YES |  |  | 自定义配置 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| department | column001 | 否 | BTREE |
| PRIMARY | projectId | 是 | BTREE |
| projectCode_index | projectCode, projectType | 否 | BTREE |
| projectType_projectId_IDX | projectType, projectId | 否 | BTREE |

**样例数据**：

| projectId | projectType | projectCode | projectName | projectState | isback | column001 | column002 | column003 | column004 | column005 | column006 | column007 | column008 | column009 | column010 | column011 | column012 | columno12_readonly | column013 | column014 | customerProjectName | salesType | majorProjectLevel | compId | createTime | createBy | updateTime | updateBy | effectiveFrom | effectiveTo | disabled | projectStartTime | projectRefreshTime | projectCloseTime | customInfo | customConfig |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | 10 | 16202215060901N-0 | 弱电项目网络设备采购（浙江广信地块） | 30 | 30 | 162022 | C00000014 | 北京方正通用信息系统有限公司 | 企业网市场部 | 政府 | 综合保障 | 其他 | None | 2015-06-26 00:00:00 | 20 | 20 | 0 | -1 | 西宁第九中学 |  | None | 01 | 无 | 1 | 2015-06-29 09:36:07 | x01861 | 2015-06-29 09:36:06 | x01861 | 2015-06-29 09:36:06 | 2015-07-09 11:13:36 | b'\x00' | None | None | None | None | None |
| 2 | 10 | 16201514102701N-0 | 西安北方光电涉密信息系统防护升级项目 | 100 | None | 162015 | C00000014 | 北京方正通用信息系统有限公司 | 企业网市场部 | 综合 | 大企业 | 军工 | None | 2015-02-10 00:00:00 | None | None | None | -1 | 西光厂 | None | None | 01 | 无 | 1 | 2015-06-29 10:52:43 | None | 2015-06-29 10:52:43 | None | 2015-06-29 10:52:43 | None | b'\x00' | None | None | None | None | None |
| 3 | 10 | 16201515012201N-0 | SGDD13国网陕西省电力公司铜川供电公司35千伏楼村变增容改造工程 | 100 | None | 162015 | U00000041 | 国网陕西省电力公司 | 企业网市场部 | 电力能源 | 电力 | 电力调度 | None | 2015-01-29 00:00:00 | None | None | None | -1 | 国网陕西省电力公司 | None | None | 01 | 无 | 1 | 2015-06-29 10:52:44 | None | 2015-06-29 10:52:44 | None | 2015-06-29 10:52:44 | None | b'\x00' | None | None | None | None | None |

---

### pm_project_contract

**业务含义**：（待补充）

**数据量**：约 79,021 行 | 数据大小：5.5 MB | 索引大小：15.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| contractNo | varchar(45) | NO |  | MUL | 合同号 |
| projectGroupCode | varchar(45) | NO |  | MUL | 项目组编码 |
| createTime | datetime | YES |  |  | 时间 |
| createBy | varchar(45) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(45) | YES |  |  | 操作人 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| contract_projectGroupCode_IDX | contractNo, projectGroupCode | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| projectGroupCode_contract_IDX | projectGroupCode, contractNo | 否 | BTREE |

---

### pm_project_group

**业务含义**：（待补充）

**数据量**：约 77,958 行 | 数据大小：4.5 MB | 索引大小：3.5 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| projectGroupCode | varchar(45) | NO |  | UNI | 项目组组编码 |
| projectGroupName | varchar(45) | YES |  |  | 项目组名称 |
| projectType | varchar(25) | YES | 10 |  | 项目类型  默认10 为工程管理售后项目 |
| createTime | datetime | YES |  |  | 时间 |
| createBy | varchar(15) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(15) | YES |  |  | 操作人 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| projectGroupCode_UNIQUE | projectGroupCode | 是 | BTREE |

---

### pm_project_group_relationship

**业务含义**：（待补充）

**数据量**：约 77,456 行 | 数据大小：6.5 MB | 索引大小：12.5 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| projectGroupCode | varchar(45) | NO |  | MUL | 项目组编码 |
| projectCode | varchar(45) | YES |  | MUL | 项目编码 |
| mergeBranchMark | varchar(45) | YES |  |  | 项目拆分合并 |
| smsProjectCode | varchar(45) | YES |  | MUL | 原SMS项目编码 |
| createTime | datetime | YES |  |  | 时间 |
| createBy | varchar(45) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(45) | YES |  |  | 操作人 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| projectCode | projectCode | 否 | BTREE |
| projectGroupCode | projectGroupCode | 否 | BTREE |
| smsProjectCode | smsProjectCode | 否 | BTREE |

---

### pm_project_header

**对象类型**：VIEW

**业务含义**：项目头信息视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| projectId | int(11) | NO | 0 |  | 项目头信息主键,跟项目其他具体信息关联 |
| projectType | varchar(45) | NO | 10 |  | 项目类型，用服售后:10，安服售后:afss，安服先行:afxx |
| projectCode | varchar(45) | NO |  |  | 项目名称 |
| projectName | varchar(200) | YES |  |  | 项目名称 |
| projectState | varchar(11) | YES |  |  | 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态 |
| isback | varchar(11) | YES | 30 |  | 30表示创建项目，32表示指定项目经理，34表示填写渠道信息 ,40表示工程管理部不予跟踪处理 ，42 表示项目经理选择不予跟踪 |
| column001 | varchar(255) | YES |  |  | 办事处编码 |
| column002 | varchar(255) | YES |  |  | 客户编码--ERP |
| column003 | varchar(255) | YES |  |  | 客户名称--ERP |
| column004 | varchar(255) | YES |  |  | 市场部编码 |
| column005 | varchar(255) | YES |  |  | 系统部ID |
| column006 | varchar(255) | YES |  |  | 拓展部ID |
| column007 | varchar(255) | YES |  |  | 子行业ID |
| column008 | varchar(255) | YES |  |  | 不予跟踪原因 notGrantTailCause |
| column009 | datetime | YES |  |  | 订单创建时间 |
| column010 | varchar(10) | YES |  |  | 项目类型 |
| column011 | varchar(10) | YES |  |  | 项目分类 |
| column012 | varchar(2) | YES |  |  | 项目实施方式 |
| columno12_readonly | int(2) | YES | -1 |  | 项目实施方式是否可以修改 -1表示可以改 其他值表示readonly |
| column013 | varchar(255) | YES |  |  | 最终客户名称 |
| column014 | text | YES |  |  | 回退说明 |
| customerProjectName | varchar(255) | YES |  |  | 客户项目名称 |
| salesType | varchar(25) | YES | 01 |  | 销售类型 |
| majorProjectLevel | varchar(255) | YES |  |  | 重大项目级别 |
| compId | int(2) | YES | 0 |  | 公司ID |
| createTime | datetime | YES |  |  | 记录数据创建时间 |
| createBy | varchar(45) | YES |  |  | 记录数据创建用户 |
| updateTime | datetime | YES |  |  | 记录数据最新更新时间 |
| updateBy | varchar(45) | YES |  |  | 记录数据最新更新用户 |
| effectiveFrom | datetime | YES |  |  | 数据有效性开始时间 |
| effectiveTo | datetime | YES |  |  | 数据有效性结束时间 |
| disabled | bit(1) | YES | b'0' |  | 数据是否失效 |
| projectStartTime | datetime | YES |  |  | 项目开始实施时间 |
| projectRefreshTime | datetime | YES |  |  | 项目相关数据最后编辑时间 |
| projectCloseTime | datetime | YES |  |  | 项目闭环时间点 |
| customInfo | json | YES |  |  | 自定义信息 |
| customConfig | json | YES |  |  | 自定义配置 |

---

### pm_project_header_view_cache

**业务含义**：（待补充）

**数据量**：约 71,993 行 | 数据大小：31.6 MB | 索引大小：10.5 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| projectCode | varchar(45) | YES |  | MUL | 原SMS项目编码 |
| subProjectCode | varchar(45) | NO |  |  | 项目名称 |
| projectName | varchar(200) | YES |  |  | 项目名称 |
| contractNo | varchar(45) | YES |  | MUL | 合同号 |
| majorProjectLevel | varchar(255) | YES |  |  | 重大项目级别 |
| officeName | varchar(20) | YES |  |  | 名称 |
| customerName | varchar(255) | YES |  |  | 客户名称--ERP |
| marketName | varchar(255) | YES |  |  | 市场部编码 |
| systemName | varchar(255) | YES |  |  | 系统部ID |
| expendName | varchar(255) | YES |  |  | 拓展部ID |
| industryName | varchar(255) | YES |  |  | 子行业ID |
| salesManCode | varchar(45) | YES |  |  | 编码 |
| salesManName | varchar(45) | YES |  |  | 名称 |
| salesManTel | varchar(45) | YES |  |  | salesManTel |
| salesManMail | varchar(100) | YES |  |  | salesManMail |
| smCode | varchar(45) | YES |  |  | 人员编码,外部人员为空 |
| smName | varchar(45) | YES |  |  | 人员名称 |
| pmCode1 | varchar(45) | YES |  |  | 人员编码,外部人员为空 |
| pmName1 | varchar(45) | YES |  |  | 人员名称 |
| pmCode2 | varchar(45) | YES |  |  | 人员编码,外部人员为空 |
| pmName2 | varchar(45) | YES |  |  | 人员名称 |
| compId | int(2) | YES |  |  | 公司ID |
| compName | varchar(128) | YES |  |  | 组织机构全名 |
| ssfsName | varchar(255) | YES |  |  | 名称 |
| partnerChannel | varchar(45) | YES |  |  | partnerChannel |
| projectType | varchar(4) | NO |  | MUL | project类型 |
| finalCustomerName | varchar(255) | YES |  |  | 最终客户名称 |
| customerProjectName | varchar(255) | YES |  |  | 客户项目名称 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| contractNo | contractNo | 否 | BTREE |
| projectCode | projectCode | 否 | BTREE |
| projectType | projectType | 否 | BTREE |

---

### pm_project_incident_table_from_itr

**业务含义**：（待补充）

**数据量**：约 142 行 | 数据大小：0.1 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| incidentId | varchar(255) | YES |  |  | 工单ID |
| ticketNo | varchar(255) | YES |  |  | 问题单号 |
| STATUS | varchar(255) | YES |  |  | 工单状态 |
| statusName | varchar(255) | YES |  |  | 工单状态名称 |
| caseTopic | varchar(255) | YES |  |  | 问题单主题 |
| memo | text | YES |  |  | 描述 |
| principal | varchar(255) | YES |  |  | 责任人 |
| principalName | varchar(255) | YES |  |  | 责任人名称 |
| accepter | varchar(255) | YES |  |  | 受理人 |
| accepterName | varchar(255) | YES |  |  | 受理人名称 |
| processor | varchar(255) | YES |  |  | 处理人 |
| processorName | varchar(255) | YES |  |  | 处理人名称 |
| supplied | varchar(255) | YES |  |  | 是否上报 |
| questionType | varchar(255) | YES |  |  | 问题类型 |
| questionLevel | varchar(255) | YES |  |  | 问题级别 |
| title | varchar(255) | YES |  |  | 工单标题 |
| acceptTime | varchar(255) | YES |  |  | 受理时间 |
| productType | varchar(255) | YES |  |  | 设备类型 |
| productModel | varchar(255) | YES |  |  | 设备型号 |
| progress | varchar(255) | YES |  |  | 处理进展 |
| questionReason | varchar(2048) | YES |  |  | 问题根因 |
| solutionType | varchar(255) | YES |  |  | 解决方式 |
| solutions | varchar(2048) | YES |  |  | 解决方案 |
| rmaNo | varchar(255) | YES |  |  | RMA单号 |
| accidentNo | varchar(255) | YES |  |  | 事故单号 |
| caseType | varchar(255) | YES |  |  | Case类型 |
| reasonFstType | varchar(255) | YES |  |  | 原因大类 |
| reasonSndType | varchar(255) | YES |  |  | 原因小类 |
| projectCode | varchar(255) | YES |  |  | 项目编码 |
| contractNo | varchar(255) | YES |  |  | 合同号 |
| barcode | varchar(255) | YES |  |  | 序列号 |
| bulletinNo | varchar(255) | YES |  |  | 技术公告编号 |
| bugNo | varchar(255) | YES |  |  | Bug单编号 |
| productLine | varchar(255) | YES |  |  | 产品线 |
| customInfo | json | YES |  |  | 自定义信息 |
| url | varchar(255) | YES |  |  | URL |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### pm_project_instruction

**业务含义**：（待补充）

**数据量**：约 127 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| projectId | int(11) | YES |  | MUL | 项目头关联主键 |
| instructionsInfo | text | YES |  |  | 批示内容或反馈内容 |
| instructionsTime | datetime | YES |  |  | 批示时间或反馈时间 |
| instructionsUser | varchar(45) | YES |  |  | 批示用户或反馈用户 |
| dataType | int(11) | YES | 0 |  | 数据类型  0 批示信息 1 批示反馈 |
| instructionsId | int(11) | YES |  |  | 批示ID 针对批示反馈的信息 |
| createTime | datetime | YES |  |  | 时间 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| projectId | projectId | 否 | BTREE |

---

### pm_project_log

**业务含义**：（待补充）

**数据量**：约 6,411 行 | 数据大小：0.5 MB | 索引大小：0.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| projectId | int(11) | YES |  | MUL | ID标识 |
| handleName | varchar(255) | YES |  |  | 操作名称 |
| handleDesc | varchar(255) | YES |  |  | 操作描述或原因 |
| handleUser | varchar(45) | YES |  |  | 操作用户 |
| taskStartTime | datetime | YES |  |  | 操作开始时间 |
| handleEndTime | datetime | YES |  |  | 操作结束时间 |
| handleState | int(11) | YES |  |  | 有无通知用户 0 无 1 有 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| projectId | projectId | 否 | BTREE |

---

### pm_project_maintenance

**业务含义**：维保项目表 - 记录维保项目信息

**数据量**：约 184,753 行 | 数据大小：110.6 MB | 索引大小：49.2 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| projectId | int(11) | NO |  | MUL | 项目头信息主键 |
| projectCode | varchar(45) | NO |  | MUL | 项目名称 |
| projectName | varchar(200) | YES |  |  | 项目名称 |
| projectType | int(11) | NO | 10 | MUL | 项目类型，售前:20/售后:10 |
| projectExecutionState | varchar(45) | YES |  |  | 项目实施状态 |
| contractNo | varchar(255) | YES |  |  | 合同号 |
| officeCode | varchar(25) | YES |  | MUL | 办事处编码 |
| compId | int(2) | YES | 1 |  | 所属公司 |
| type | varchar(45) | YES |  | MUL | 任务性质 |
| category | varchar(45) | YES |  | MUL | 任务分类 |
| subCategory | varchar(45) | YES |  | MUL | 任务小类 |
| processTime | datetime | YES |  | MUL | 处理时间 |
| processDesc | varchar(1024) | YES |  |  | 事项描述 |
| processStep | varchar(1024) | YES |  |  | 解决进展 |
| remainProblem | varchar(1024) | YES |  |  | 遗留问题 |
| transitHour | float | YES | 0 |  | 在途耗时(h) |
| processHour | float | YES | 0 |  | 处理耗时(h) |
| itemModel | varchar(255) | YES |  |  | 产品型号 |
| softVersion | varchar(255) | YES |  |  | 在网版本 |
| enabledFeatures | varchar(255) | YES |  |  | 启用功能 |
| customTos | varchar(512) | YES |  |  | 自定义主送 |
| customCcs | varchar(512) | YES |  |  | 自定义抄送 |
| hasReport | bit(1) | NO | b'0' |  | 是否有巡检报告 |
| quesnaireId | int(11) | YES |  |  | 问卷ID |
| deliverFileIds | varchar(255) | YES |  |  | 交付件，fnd_files id |
| warrantyStatus | varchar(25) | YES |  |  | 维保状态 |
| industryName | varchar(25) | YES |  |  | 行业 |
| userOffice | varchar(25) | YES |  |  | 用户办事处 |
| year | int(4) | YES |  |  | 所属年度 |
| quarter | int(1) | YES |  |  | 所属季度 |
| month | int(2) | YES |  |  | 所属月份 |
| wsCount | int(2) | YES |  |  | 当前维保服务次数 |
| wafCount | int(2) | YES |  |  | 当前其他服务次数 |
| wsYearCount | int(2) | YES |  |  | 维保服务年次数 |
| wafYearCount | int(2) | YES |  |  | 其他服务年次数 |
| warrantyInfo | varchar(4096) | YES |  |  | 维保信息 |
| serviceInfo | varchar(2048) | YES |  |  | 其他服务信息 |
| remark | varchar(2048) | YES |  |  | 备注 |
| createTime | datetime | YES |  | MUL | 创建时间 |
| createBy | varchar(45) | YES |  | MUL | 创建用户 |
| updateTime | datetime | YES |  |  | 最新更新时间 |
| updateBy | varchar(45) | YES |  |  | 最新更新用户 |
| customInfo | json | YES |  |  | 自定义信息 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| category | category, subCategory | 否 | BTREE |
| createBy | createBy | 否 | BTREE |
| createTime | createTime | 否 | BTREE |
| officeCode | officeCode | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| processTime_IDX | processTime | 否 | BTREE |
| projectCode | projectCode | 否 | BTREE |
| projectId | projectId | 否 | BTREE |
| projectType | projectType | 否 | BTREE |
| subCategory | subCategory | 否 | BTREE |
| type | type | 否 | BTREE |

**样例数据**：

| id | projectId | projectCode | projectName | projectType | projectExecutionState | contractNo | officeCode | compId | type | category | subCategory | processTime | processDesc | processStep | remainProblem | transitHour | processHour | itemModel | softVersion | enabledFeatures | customTos | customCcs | hasReport | quesnaireId | deliverFileIds | warrantyStatus | industryName | userOffice | year | quarter | month | wsCount | wafCount | wsYearCount | wafYearCount | warrantyInfo | serviceInfo | remark | createTime | createBy | updateTime | updateBy | customInfo |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | 1582 | 16200613072601N-0 | <大连理工大学互联网出口项目> | 10 | 待确认 |

2、新增链路透明串接，两块UAG板卡更新为云板卡。 | 2019-04-12 09:50:13 | h00965 | None | None | None |
| 2 | 20030 | 16200617060701N-0 | 法库县职教中心智慧校园建设 | 10 | 待确认 |

2、该项目原自服代理商“沈阳鹏鑫威达”未安排工 | 2019-04-12 09:54:38 | h00965 | None | None | None |
| 3 | 22526 | 16202816041802N-0 | 深圳市戒毒所弱电智能化项目 | 10 | 待确认 |

---

### pm_project_maintenance_sectary_from_sse

**业务含义**：（待补充）

**数据量**：约 160 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| depNum | varchar(10) | NO |  | UNI | 同步自EHR系统 |
| depName | varchar(20) | NO |  |  | 名称 |
| pDepNum | varchar(10) | YES |  |  | p部门数量 |
| pDepName | varchar(20) | YES |  |  | 名称 |
| sectary | varchar(10) | YES |  | MUL | 秘书工号 |
| sectaryName | varchar(255) | YES |  |  | 秘书姓名 |
| sectaryEmail | varchar(255) | YES |  |  | 秘书邮箱 |
| sectaryPhone | varchar(255) | YES |  |  | 秘书电话 |
| status | int(4) | YES | 1 |  | 有效状态 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| depNum | depNum | 是 | BTREE |
| PRIMARY | id | 是 | BTREE |
| sectary | sectary | 否 | BTREE |

---

### pm_project_maintenance_service_delivery

**业务含义**：（待补充）

**数据量**：约 66 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| maintenanceId | int(11) | NO |  | MUL | ID标识 |
| projectId | int(11) | YES |  | MUL | ID标识 |
| projectType | varchar(25) | YES | 10 |  | project类型 |
| serviceType | varchar(25) | YES |  | MUL | 服务类型 |
| processTime | datetime | YES |  |  | 时间 |
| year | int(4) | YES |  |  | 年份 |
| quarter | int(2) | YES |  |  | 季度 |
| month | int(2) | YES |  |  | 月份 |
| deliveried | int(1) | YES | 0 |  | 是否已交付 |
| startDate | date | YES |  |  | 时间 |
| endDate | date | YES |  |  | 时间 |
| count | int(2) | YES | 0 |  | 计数 |
| yearCount | int(2) | YES | 0 |  | year计数 |
| remark | varchar(2048) | YES |  |  | 备注/描述 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| maintenanceId | maintenanceId | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| projectId | projectId, projectType | 否 | BTREE |
| serviceType | serviceType | 否 | BTREE |

---

### pm_project_maintenance_view

**业务含义**：（待补充）

**数据量**：约 183,607 行 | 数据大小：122.7 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO | 0 |  | ID标识 |
| projectId | int(11) | NO |  |  | 项目头信息主键 |
| projectCode | varchar(45) | NO |  |  | 项目名称 |
| projectName | varchar(200) | YES |  |  | 项目名称 |
| projectType | int(11) | NO | 10 |  | 项目类型，售前:20/售后:10 |
| projectExecutionState | varchar(45) | YES |  |  | 项目实施状态 |
| contractNo | varchar(255) | YES |  |  | 合同号 |
| officeCode | varchar(25) | YES |  |  | 办事处编码 |
| compId | int(2) | YES | 1 |  | 所属公司 |
| type | varchar(45) | YES |  |  | 任务性质 |
| category | varchar(45) | YES |  |  | 任务分类 |
| subCategory | varchar(45) | YES |  |  | 任务小类 |
| processTime | datetime | YES |  |  | 处理时间 |
| processDesc | varchar(1024) | YES |  |  | 事项描述 |
| processStep | varchar(1024) | YES |  |  | 解决进展 |
| remainProblem | varchar(1024) | YES |  |  | 遗留问题 |
| transitHour | float | YES | 0 |  | 在途耗时(h) |
| processHour | float | YES | 0 |  | 处理耗时(h) |
| itemModel | varchar(255) | YES |  |  | 产品型号 |
| softVersion | varchar(255) | YES |  |  | 在网版本 |
| enabledFeatures | varchar(255) | YES |  |  | 启用功能 |
| customTos | varchar(512) | YES |  |  | 自定义主送 |
| customCcs | varchar(512) | YES |  |  | 自定义抄送 |
| hasReport | bit(1) | NO | b'0' |  | 是否有巡检报告 |
| quesnaireId | int(11) | YES |  |  | 问卷ID |
| deliverFileIds | varchar(255) | YES |  |  | 交付件，fnd_files id |
| warrantyStatus | varchar(25) | YES |  |  | 维保状态 |
| industryName | varchar(25) | YES |  |  | 行业 |
| userOffice | varchar(25) | YES |  |  | 用户办事处 |
| year | int(4) | YES |  |  | 所属年度 |
| quarter | int(1) | YES |  |  | 所属季度 |
| month | int(2) | YES |  |  | 所属月份 |
| wsCount | int(2) | YES |  |  | 当前维保服务次数 |
| wafCount | int(2) | YES |  |  | 当前其他服务次数 |
| wsYearCount | int(2) | YES |  |  | 维保服务年次数 |
| wafYearCount | int(2) | YES |  |  | 其他服务年次数 |
| warrantyInfo | varchar(4096) | YES |  |  | 维保信息 |
| serviceInfo | varchar(2048) | YES |  |  | 其他服务信息 |
| remark | varchar(2048) | YES |  |  | 备注 |
| createTime | datetime | YES |  |  | 创建时间 |
| createBy | varchar(45) | YES |  |  | 创建用户 |
| updateTime | datetime | YES |  |  | 最新更新时间 |
| updateBy | varchar(45) | YES |  |  | 最新更新用户 |
| customInfo | json | YES |  |  | 自定义信息 |
| officeName | varchar(20) | YES |  |  | 名称 |
| userOfficeName | varchar(20) | YES |  |  | 名称 |
| serviceManager | varchar(45) | YES |  |  | 人员名称 |
| programManagerA | varchar(45) | YES |  |  | 人员名称 |
| programManagerB | varchar(45) | YES |  |  | 人员名称 |
| createUser | varchar(174) | YES |  |  | 创建信息 |
| typeName | varchar(255) | YES |  |  | 名称 |
| projectExecutionStateName | varchar(255) | YES |  |  | 名称 |
| categoryName | varchar(258) | YES |  |  | 名称 |
| subCategoryName | varchar(255) | YES |  |  | 名称 |
| marketName | varchar(255) | YES |  |  | 名称 |
| systemName | varchar(255) | YES |  |  | 名称 |
| expendName | varchar(255) | YES |  |  | 名称 |
| industryNameN | varchar(255) | YES |  |  | industry名称N |
| finalCustomerName | varchar(255) | YES |  |  | 最终客户名称 |
| salerName | varchar(91) | YES |  |  | 名称 |
| quesnaireResultHeaderId | int(11) | YES |  |  | 回访结果头信息Id |
| 工程师技术能力 | longtext | YES |  |  | 工程师技术能力 |
| 服务及时性 | longtext | YES |  |  | 服务及时性 |
| 服务水平及规范性 | longtext | YES |  |  | 服务水平及规范性 |
| warrantyStatusName | varchar(4) | YES |  |  | 名称 |
| syncTime | datetime | NO |  |  | 时间 |

---

### pm_project_market_relations_from_sms

**业务含义**：（待补充）

**数据量**：约 528 行 | 数据大小：0.1 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| marketCode | varchar(64) | YES |  |  | 编码 |
| marketName | varchar(255) | YES |  |  | 名称 |
| systemCode | varchar(64) | YES |  |  | 编码 |
| systemName | varchar(255) | YES |  |  | 名称 |
| expendCode | varchar(64) | YES |  |  | 编码 |
| expendName | varchar(255) | YES |  |  | 名称 |
| industryCode | varchar(64) | YES |  |  | 编码 |
| industryName | varchar(255) | YES |  |  | 名称 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### pm_project_member

**业务含义**：项目成员表 - 记录项目参与人员及角色

**数据量**：约 302,424 行 | 数据大小：32.6 MB | 索引大小：37.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| projectId | int(11) | YES |  | MUL | ID标识 |
| projectType | varchar(25) | YES | 10 |  | 项目类型 售后10 或售前 20 详见fnd_basic_data |
| memberRole | varchar(45) | YES |  |  | 成员角色：10=项目经理, 15=副项目经理, 20=项目成员, 30=技术负责人, 40=质量负责人, 50=安全负责人, 60=远程支持, 71=驻场工程师, 80=其他 |
| memberCode | varchar(45) | YES |  | MUL | 人员编码,外部人员为空 |
| memberName | varchar(45) | YES |  |  | 人员名称 |
| phoneNum | varchar(20) | YES |  |  | 电话 |
| email | varchar(45) | YES |  |  | 邮箱 |
| fromFlag | varchar(2) | YES | 0 |  | 信息来源，1表示来源于项目信息，2表示来源于成员信息 |
| createTime | datetime | YES |  |  | 时间 |
| createBy | varchar(45) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(15) | YES |  |  | 操作人 |
| effectiveTo | datetime | YES |  |  | 有效结束时间 |
| effectiveFrom | datetime | YES |  |  | 有效开始时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| memberCode_IDX | memberCode, projectId, projectType | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| projectId_role | projectId, memberRole | 否 | BTREE |
| projectId_type | projectId, projectType | 否 | BTREE |

**样例数据**：

| id | projectId | projectType | memberRole | memberCode | memberName | phoneNum | email | fromFlag | createTime | createBy | updateTime | updateBy | effectiveTo | effectiveFrom |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | 1 | 10 | 20 | y00439 | y00439-岳伟刚 | 18606506873 | yueweigang@dptech.com | 1 | 2015-06-29 09:36:07 | x01861 | 2015-06-29 09:36:07 | x01861 | None | 2015-06-29 09:36:07 |
| 2 | 1 | 10 | 10 | y01577 | 余建军 | 13588086468 | yujianjun@dptech.com | 1 | 2015-06-29 09:36:07 | x01861 | 2015-06-29 09:36:07 | x01861 | None | 2015-06-29 09:36:07 |
| 3 | 2 | 10 | 10 | w01524 | 王兴 | 13891928286 | wangxing@dptech.com | 1 | 2015-06-29 10:52:43 | None | 2015-06-29 10:52:43 | None | 2018-01-25 23:32:18 | 2015-06-29 10:52:43 |

---

### pm_project_notification

**业务含义**：（待补充）

**数据量**：约 152,156 行 | 数据大小：13.5 MB | 索引大小：4.5 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| notifySubject | varchar(255) | YES |  |  | 通知标题 |
| notifyContent | text | YES |  |  | 通知内容 |
| projectId | int(11) | YES |  | MUL | 相关项目ID |
| createTime | datetime | YES |  |  | 创建时间 |
| createBy | varchar(25) | YES |  |  | 创建用户 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| projectId | projectId | 否 | BTREE |

---

### pm_project_notification_state

**业务含义**：（待补充）

**数据量**：约 9,905 行 | 数据大小：1.5 MB | 索引大小：0.2 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| notifyId | int(11) | YES |  | MUL | ID标识 |
| notifyObject | varchar(25) | YES |  |  | 通知主题，系统用户 |
| notifyState | int(11) | YES |  |  | 通知状态，有无通知 0 无 1 有 |
| checkTime | datetime | YES |  |  | 用户查看通知时间 |
| createTime | datetime | YES |  |  | 时间 |
| createBy | varchar(25) | YES |  |  | 操作人 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| notifyId | notifyId | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### pm_project_product_af_from_sms

**业务含义**：（待补充）

**数据量**：约 21,608 行 | 数据大小：6.5 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | varchar(64) | NO | 0 |  | ID标识 |
| projectCode | varchar(255) | NO |  |  | 编码 |
| orderExecNumber | varchar(255) | YES |  |  | 排序ExecNumber |
| corporationCode | varchar(50) | YES |  |  | 公司编码 |
| ssfrId | varchar(64) | YES |  |  | 安全服务先行核销ID |
| productCode | varchar(255) | YES |  |  | 编码 |
| productfirstCode | varchar(255) | YES |  |  | 编码 |
| productName | varchar(128) | YES |  |  | 名称 |
| productfirstName | varchar(255) | YES |  |  | 名称 |
| productsubCode | varchar(255) | YES |  |  | 编码 |
| productSubModel | varchar(255) | YES |  |  | productSubModel |
| productSubName | varchar(255) | YES |  |  | 名称 |
| num | int(11) | YES |  |  | 数量 |
| borrowNum | int(11) | YES |  |  | borrow数量 |
| price | decimal(19,6) | YES |  |  | 价格 |
| purchaseDiscount | decimal(19,6) | YES |  |  | purchaseDiscount |
| purchasePrice | decimal(29,2) | YES |  |  | purchasePrice |
| dataSource | varchar(25) | YES | CRM |  | dataSource |
| lineType | varchar(25) | YES | orderLine |  | 行类型，orderLine:订单行，leaseLine:租赁行 |
| customInfo | json | YES |  |  | 自定义信息 |

---

### pm_project_product_af_from_sms_history

**业务含义**：（待补充）

**数据量**：约 18,054 行 | 数据大小：5.5 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO | 0 |  | ID标识 |
| projectCode | varchar(255) | NO |  |  | 编码 |
| orderExecNumber | varchar(255) | YES |  |  | 排序ExecNumber |
| corporationCode | varchar(50) | YES |  |  | 公司编码 |
| ssfrId | varchar(64) | YES |  |  | 安全服务先行核销ID |
| productCode | varchar(255) | YES |  |  | 编码 |
| productfirstCode | varchar(255) | YES |  |  | 编码 |
| productName | varchar(128) | YES |  |  | 名称 |
| productfirstName | varchar(255) | YES |  |  | 名称 |
| productsubCode | varchar(255) | YES |  |  | 编码 |
| productSubModel | varchar(255) | YES |  |  | productSubModel |
| productSubName | varchar(255) | YES |  |  | 名称 |
| num | int(11) | YES |  |  | 数量 |
| borrowNum | int(11) | YES |  |  | borrow数量 |
| price | decimal(19,6) | YES |  |  | 价格 |
| purchaseDiscount | decimal(19,6) | YES |  |  | purchaseDiscount |
| purchasePrice | decimal(29,2) | YES |  |  | purchasePrice |
| dataSource | varchar(25) | YES | SMS |  | dataSource |
| lineType | varchar(25) | YES | orderLine |  | 行类型，orderLine:订单行，leaseLine:租赁行 |
| customInfo | json | YES |  |  | 自定义信息 |

---

### pm_project_product_config_level_info_from_crm

**业务含义**：（待补充）

**数据量**：约 45 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| projectCode | varchar(100) | YES |  |  | 编码 |
| orderExecNumber | varchar(255) | YES |  |  | 排序ExecNumber |
| itemGroup | int(11) | YES |  |  | 项目Group |
| itemCode | varchar(100) | YES |  |  | 编码 |
| parentCode | varchar(1000) | YES |  |  | 编码 |
| quantity | int(11) | YES |  |  | 数量 |
| bomPaths | varchar(1000) | YES |  |  | bomPaths |
| itemModel | varchar(100) | YES |  |  | 项目Model |
| itemDesc | varchar(500) | YES |  |  | 项目描述 |
| level | int(11) | YES | 0 |  | 级别 |
| dataSource | varchar(25) | YES | CRM |  | dataSource |

---

### pm_project_product_lease_line_from_crm

**业务含义**：（待补充）

**数据量**：约 0 行 | 数据大小：40.1 MB | 索引大小：109.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| projectCode | varchar(255) | NO |  | MUL | 项目编码 |
| orderExecNumber | varchar(255) | YES |  |  | 执行单号 |
| productFirstName | varchar(255) | NO |  |  | 产品类型 |
| productName | varchar(128) | NO |  |  | 产品名 |
| productSubCode | varchar(255) | NO |  |  | item编码 |
| productSubModel | varchar(255) | YES |  |  | item类型 |
| productSubName | varchar(255) | YES |  |  | item描述 |
| num | int(11) | NO | 0 |  | 订单数量 |
| memo | mediumtext | YES |  |  | 备注 |
| leaseDuration | decimal(16,2) | YES |  |  | 租赁月数 |
| dataSource | varchar(25) | YES | CRM |  | dataSource |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| projectCode_IDX | projectCode, orderExecNumber | 否 | BTREE |

---

### pm_project_product_line

**业务含义**：（待补充）

**数据量**：约 185,819 行 | 数据大小：25.1 MB | 索引大小：28.4 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | MUL | ID标识（自增） |
| projectId | int(11) | YES |  | MUL | 关联主表 |
| contractNo | varchar(45) | YES |  | MUL | 合同号 |
| itemCode | varchar(15) | YES |  | MUL | 产品编码 |
| itemName | varchar(255) | YES |  |  | 产品名称 |
| projectQuantity | int(11) | YES |  |  | 项目产品数量 |
| orderQuantity | int(11) | YES |  |  | 产品订单数量 |
| deliverQuantity | int(11) | YES |  |  | 已发货数量 |
| openQuantity | int(11) | YES |  |  | 未发货数量 |
| orderNumber | varchar(25) | YES |  |  | 排序Number |
| lineNum | varchar(25) | YES |  |  | line数量 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| contractNo | contractNo | 否 | BTREE |
| id | id | 否 | BTREE |
| itemCode | itemCode | 否 | BTREE |
| projectId | projectId | 否 | BTREE |

---

### pm_project_product_line_real

**业务含义**：（待补充）

**数据量**：约 3,812 行 | 数据大小：1.5 MB | 索引大小：0.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| projectId | int(11) | NO |  | MUL | ph.projectId,外键 |
| contractNo | varchar(25) | NO |  |  | 合同号 |
| projectCode | varchar(255) | NO |  |  | 项目编码 |
| orderExecNumber | varchar(255) | NO |  |  | 执行单号 |
| productFirstName | varchar(255) | YES |  |  | 产品分类？ |
| productName | varchar(128) | YES |  |  | 产品名 |
| productSubCode | varchar(255) | NO |  |  | item编码 |
| productSubModel | varchar(255) | NO |  |  | item类型 |
| productSubName | varchar(255) | NO |  |  | item名 |
| num | int(11) | YES | 0 |  | 订单数量 |
| memo | mediumtext | YES |  |  | 备注 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| projectId | projectId | 否 | BTREE |

---

### pm_project_property_af_from_sms

**业务含义**：（待补充）

**数据量**：约 190 行 | 数据大小：0.2 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO | 0 |  | ID标识 |
| orderExecNumber | varchar(60) | YES |  |  | 排序ExecNumber |
| projectCode | varchar(255) | NO |  |  | 编码 |
| projectName | varchar(765) | YES |  |  | 名称 |
| marketCode | varchar(64) | YES |  |  | 编码 |
| officeCode | varchar(765) | YES |  |  | 编码 |
| expendId | varchar(64) | YES |  |  | ID标识 |
| marketName | varchar(255) | YES |  |  | 名称 |
| officeName | varchar(128) | YES |  |  | 名称 |
| systemName | varchar(255) | YES |  |  | 名称 |
| salesManCode | varchar(60) | YES |  |  | 编码 |
| systemId | varchar(64) | YES |  |  | ID标识 |
| industryId | varchar(64) | YES |  |  | ID标识 |
| salesManName | varchar(128) | YES |  |  | 名称 |
| expendName | varchar(255) | YES |  |  | 名称 |
| industryName | varchar(255) | YES |  |  | 名称 |
| serviceTypeName | varchar(128) | YES |  |  | 名称 |
| channelName | varchar(765) | YES |  |  | 名称 |
| engineeFee | decimal(19,2) | YES |  |  | 安全服务先行类借货有值，表示出货价 |
| objId | varchar(64) | YES |  |  | 参数1 |
| applyType | varchar(60) | YES |  |  | apply类型 |
| corporationCode | varchar(10) | YES |  |  | 公司编码 |
| customerProjectName | varchar(765) | YES |  |  | 名称 |
| finalCustomerName | varchar(765) | YES |  |  | 名称 |
| agentName | varchar(765) | YES |  |  | 名称 |
| pspm | varchar(765) | YES |  |  | PSPM编号 |
| pspmName | varchar(257) | YES |  |  | 名称 |
| salesMenTel | varchar(300) | YES |  |  | salesMenTel |
| decPath | varchar(765) | YES |  |  | decPath |
| requireInDate | date | YES |  |  | 时间 |
| receiveMen | varchar(450) | YES |  |  | receiveMen |
| reveiveContactWay | varchar(300) | YES |  |  | reveiveContactWay |
| receiveAddress | varchar(765) | YES |  |  | receive地址 |
| lendCause | text | YES |  |  | lendCause |
| projectType | varchar(4) | NO |  |  | project类型 |
| projectMoney | decimal(16,2) | YES | 0.00 |  | 出货价 |
| afProjectMoney | decimal(16,2) | YES | 0.00 |  | 安服出货价 |
| submitTime | datetime | YES |  |  | 提交时间 |
| predBidDate | datetime | YES |  |  | 项目投标时间 |
| linkmanName | varchar(255) | YES |  |  | 客户联系人 |
| linkmanTel | varchar(64) | YES |  |  | 客户联系方式 |
| customInfo | json | YES |  |  | 自定义信息 |
| dataSource | varchar(25) | YES | CRM |  | dataSource |

---

### pm_project_property_af_from_sms_history

**业务含义**：（待补充）

**数据量**：约 129 行 | 数据大小：0.1 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO | 0 |  | ID标识 |
| orderExecNumber | varchar(60) | YES |  |  | 排序ExecNumber |
| projectCode | varchar(255) | NO |  |  | 编码 |
| projectName | varchar(765) | YES |  |  | 名称 |
| marketCode | varchar(64) | YES |  |  | 编码 |
| officeCode | varchar(765) | YES |  |  | 编码 |
| expendId | varchar(64) | YES |  |  | ID标识 |
| marketName | varchar(255) | YES |  |  | 名称 |
| officeName | varchar(128) | YES |  |  | 名称 |
| systemName | varchar(255) | YES |  |  | 名称 |
| salesManCode | varchar(60) | YES |  |  | 编码 |
| systemId | varchar(64) | YES |  |  | ID标识 |
| industryId | varchar(64) | YES |  |  | ID标识 |
| salesManName | varchar(128) | YES |  |  | 名称 |
| expendName | varchar(255) | YES |  |  | 名称 |
| industryName | varchar(255) | YES |  |  | 名称 |
| serviceTypeName | varchar(128) | YES |  |  | 名称 |
| channelName | varchar(765) | YES |  |  | 名称 |
| engineeFee | decimal(19,2) | YES |  |  | 安全服务先行类借货有值，表示出货价 |
| objId | varchar(64) | YES |  |  | 参数1 |
| applyType | varchar(60) | YES |  |  | apply类型 |
| corporationCode | varchar(10) | YES |  |  | 公司编码 |
| customerProjectName | varchar(765) | YES |  |  | 名称 |
| finalCustomerName | varchar(765) | YES |  |  | 名称 |
| agentName | varchar(765) | YES |  |  | 名称 |
| pspm | varchar(765) | YES |  |  | PSPM编号 |
| pspmName | varchar(257) | YES |  |  | 名称 |
| salesMenTel | varchar(300) | YES |  |  | salesMenTel |
| decPath | varchar(765) | YES |  |  | decPath |
| requireInDate | date | YES |  |  | 时间 |
| receiveMen | varchar(450) | YES |  |  | receiveMen |
| reveiveContactWay | varchar(300) | YES |  |  | reveiveContactWay |
| receiveAddress | varchar(765) | YES |  |  | receive地址 |
| lendCause | text | YES |  |  | lendCause |
| projectType | varchar(4) | NO |  |  | project类型 |
| projectMoney | decimal(16,2) | YES | 0.00 |  | 出货价 |
| afProjectMoney | decimal(16,2) | YES | 0.00 |  | 安服出货价 |
| submitTime | datetime | YES |  |  | 提交时间 |
| predBidDate | datetime | YES |  |  | 项目投标时间 |
| linkmanName | varchar(255) | YES |  |  | 客户联系人 |
| linkmanTel | varchar(64) | YES |  |  | 客户联系方式 |
| customInfo | json | YES |  |  | 自定义信息 |
| dataSource | varchar(25) | YES | SMS |  | dataSource |

---

### pm_project_property_from_sms

**业务含义**：（待补充）

**数据量**：约 612 行 | 数据大小：0.3 MB | 索引大小：0.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| orderExecNumber | varchar(25) | YES |  | MUL | 排序ExecNumber |
| projectCode | varchar(25) | YES |  | MUL | 编码 |
| projectName | varchar(255) | YES |  |  | 名称 |
| salesManCode | varchar(45) | YES |  |  | 编码 |
| salesManName | varchar(45) | YES |  |  | 名称 |
| marketCode | varchar(64) | YES |  |  | 编码 |
| marketName | varchar(255) | YES |  |  | 名称 |
| systemId | varchar(64) | YES |  |  | ID标识 |
| systemName | varchar(255) | YES |  |  | 名称 |
| expendId | varchar(64) | YES |  |  | ID标识 |
| expendName | varchar(255) | YES |  |  | 名称 |
| industryId | varchar(64) | YES |  |  | ID标识 |
| industryName | varchar(255) | YES |  |  | 名称 |
| officeCode | varchar(15) | YES |  |  | 编码 |
| officeName | varchar(15) | YES |  |  | 名称 |
| serviceTypeName | varchar(10) | YES |  |  | 名称 |
| channelName | varchar(255) | YES |  |  | 出货代理商名称 |
| engineeFee | varchar(25) | YES |  |  | 工程服务费 |
| objId | varchar(64) | YES |  |  | 参数1 |
| applyType | varchar(25) | YES |  |  | 参数2 |
| corporationCode | varchar(25) | YES | 01 |  | 公司编码 |
| customerProjectName | varchar(255) | YES |  |  | 客户项目名称 |
| finalCustomerName | varchar(255) | YES |  |  | 最终客户名称 |
| agentName | varchar(500) | YES |  |  | 代理商名称 |
| projectMoney | decimal(16,2) | YES | 0.00 |  | 出货价 |
| submitTime | datetime | YES |  |  | 项目创建时间 |
| majorProjectLevel | varchar(255) | YES |  |  | 重大项目级别 |
| predBidDate | datetime | YES |  |  | 项目投标时间 |
| linkmanName | varchar(255) | YES |  |  | 客户联系人 |
| linkmanTel | varchar(64) | YES |  |  | 客户联系方式 |
| dataSource | varchar(25) | YES | SMS |  | dataSource |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| orderExecNum | orderExecNumber | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| projectCode | projectCode | 否 | BTREE |

---

### pm_project_property_from_sms_history

**业务含义**：（待补充）

**数据量**：约 47,550 行 | 数据大小：18.5 MB | 索引大小：7.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| orderExecNumber | varchar(25) | YES |  | MUL | 排序ExecNumber |
| projectCode | varchar(25) | YES |  | MUL | 编码 |
| projectName | varchar(255) | YES |  |  | 名称 |
| salesManCode | varchar(10) | YES |  |  | 编码 |
| salesManName | varchar(10) | YES |  |  | 名称 |
| marketCode | varchar(64) | YES |  |  | 编码 |
| marketName | varchar(255) | YES |  |  | 名称 |
| systemId | varchar(64) | YES |  |  | ID标识 |
| systemName | varchar(255) | YES |  |  | 名称 |
| expendId | varchar(64) | YES |  |  | ID标识 |
| expendName | varchar(255) | YES |  |  | 名称 |
| industryId | varchar(64) | YES |  |  | ID标识 |
| industryName | varchar(255) | YES |  |  | 名称 |
| officeCode | varchar(15) | YES |  |  | 编码 |
| officeName | varchar(15) | YES |  |  | 名称 |
| serviceTypeName | varchar(10) | YES |  |  | 名称 |
| channelName | varchar(255) | YES |  |  | 出货代理商名称 |
| engineeFee | varchar(25) | YES |  |  | 工程服务费 |
| objId | varchar(64) | YES |  |  | 参数1 |
| applyType | varchar(25) | YES |  |  | 参数2 |
| corporationCode | varchar(25) | YES | 01 |  | 公司编码 |
| customerProjectName | varchar(255) | YES |  |  | 客户项目名称 |
| finalCustomerName | varchar(255) | YES |  |  | 最终客户名称 |
| agentName | varchar(500) | YES |  |  | 代理商名称 |
| projectMoney | decimal(16,2) | YES | 0.00 |  | 出货价 |
| submitTime | datetime | YES |  |  | 项目创建时间 |
| majorProjectLevel | varchar(255) | YES |  |  | 重大项目级别 |
| predBidDate | datetime | YES |  |  | 项目投标时间 |
| linkmanName | varchar(255) | YES |  |  | 客户联系人 |
| linkmanTel | varchar(64) | YES |  |  | 客户联系方式 |
| dataSource | varchar(25) | YES | SMS |  | dataSource |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| orderExecNum | orderExecNumber | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| projectCode | projectCode | 否 | BTREE |

---

### pm_project_property_from_sms_history_bak

**业务含义**：（待补充）

**数据量**：约 46,985 行 | 数据大小：19.5 MB | 索引大小：6.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| orderExecNumber | varchar(25) | YES |  | MUL | 排序ExecNumber |
| projectCode | varchar(25) | YES |  | MUL | 编码 |
| projectName | varchar(255) | YES |  |  | 名称 |
| salesManCode | varchar(10) | YES |  |  | 编码 |
| salesManName | varchar(10) | YES |  |  | 名称 |
| marketCode | varchar(64) | YES |  |  | 编码 |
| marketName | varchar(255) | YES |  |  | 名称 |
| systemId | varchar(64) | YES |  |  | ID标识 |
| systemName | varchar(255) | YES |  |  | 名称 |
| expendId | varchar(64) | YES |  |  | ID标识 |
| expendName | varchar(255) | YES |  |  | 名称 |
| industryId | varchar(64) | YES |  |  | ID标识 |
| industryName | varchar(255) | YES |  |  | 名称 |
| officeCode | varchar(15) | YES |  |  | 编码 |
| officeName | varchar(15) | YES |  |  | 名称 |
| serviceTypeName | varchar(10) | YES |  |  | 名称 |
| channelName | varchar(255) | YES |  |  | 出货代理商名称 |
| engineeFee | varchar(25) | YES |  |  | 工程服务费 |
| objId | varchar(64) | YES |  |  | 参数1 |
| applyType | varchar(25) | YES |  |  | 参数2 |
| corporationCode | varchar(25) | YES | 01 |  | 公司编码 |
| customerProjectName | varchar(255) | YES |  |  | 客户项目名称 |
| finalCustomerName | varchar(255) | YES |  |  | 最终客户名称 |
| agentName | varchar(500) | YES |  |  | 代理商名称 |
| projectMoney | decimal(16,2) | YES | 0.00 |  | 出货价 |
| submitTime | datetime | YES |  |  | 项目创建时间 |
| majorProjectLevel | varchar(255) | YES |  |  | 重大项目级别 |
| predBidDate | datetime | YES |  |  | 项目投标时间 |
| linkmanName | varchar(255) | YES |  |  | 客户联系人 |
| linkmanTel | varchar(64) | YES |  |  | 客户联系方式 |
| dataSource | varchar(25) | YES | SMS |  | dataSource |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| orderExecNum | orderExecNumber | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| projectCode | projectCode | 否 | BTREE |

---

### pm_project_real_product_line_from_sms

**业务含义**：（待补充）

**数据量**：约 16,140 行 | 数据大小：3.5 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| projectCode | varchar(255) | NO |  |  | 项目编码 |
| orderExecNumber | varchar(255) | YES |  |  | 执行单号 |
| productFirstName | varchar(255) | NO |  |  | 产品类型 |
| productName | varchar(128) | NO |  |  | 产品名 |
| productSubCode | varchar(255) | NO |  |  | item编码 |
| productSubModel | varchar(255) | YES |  |  | item类型 |
| productSubName | varchar(255) | YES |  |  | item描述 |
| num | int(11) | NO | 0 |  | 订单数量 |
| memo | mediumtext | YES |  |  | 备注 |
| dataSource | varchar(25) | YES | SMS |  | dataSource |

---

### pm_project_real_product_line_from_sms_history

**业务含义**：（待补充）

**数据量**：约 5,563 行 | 数据大小：1.5 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| projectCode | varchar(255) | NO |  |  | 项目编码 |
| orderExecNumber | varchar(255) | YES |  |  | 执行单号 |
| productFirstName | varchar(255) | NO |  |  | 产品类型 |
| productName | varchar(128) | NO |  |  | 产品名 |
| productSubCode | varchar(255) | NO |  |  | item编码 |
| productSubModel | varchar(255) | YES |  |  | item类型 |
| productSubName | varchar(255) | YES |  |  | item描述 |
| num | int(11) | NO | 0 |  | 订单数量 |
| memo | mediumtext | YES |  |  | 备注 |
| dataSource | varchar(25) | YES | SMS |  | dataSource |

---

### pm_project_related_party

**业务含义**：（待补充）

**数据量**：约 126,860 行 | 数据大小：12.5 MB | 索引大小：8.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| projectId | int(11) | YES |  | MUL | ID标识 |
| partyRole | varchar(45) | YES |  | MUL | partyRole |
| partyCode | varchar(45) | YES |  |  | 编码 |
| partyName | varchar(45) | YES |  |  | 名称 |
| createTime | datetime | YES |  |  | 时间 |
| createBy | varchar(45) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 更新时间 |
| updateBy | varchar(45) | YES |  |  | 更新人 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| partyRole_parojectId | partyRole, projectId | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| projectId | projectId | 否 | BTREE |

---

### pm_project_shipment

**业务含义**：项目发货表 - 记录项目发货信息

**数据量**：约 458,802 行 | 数据大小：117.6 MB | 索引大小：43.6 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| projectId | int(11) | YES |  | MUL | ID标识 |
| barcode | varchar(25) | YES |  | MUL | 编码 |
| itemCode | varchar(25) | YES |  |  | 编码 |
| itemModel | varchar(255) | YES |  |  | 项目Model |
| itemName | varchar(255) | YES |  |  | 名称 |
| receiveName | varchar(255) | YES |  |  | 名称 |
| emsNum | varchar(255) | YES |  |  | ems数量 |
| emsCompany | varchar(15) | YES |  |  | emsCompany |
| packdate | datetime | YES |  |  | 时间 |
| contractNo | varchar(50) | YES |  | MUL | 合同编号 |
| installAddress | text | YES |  |  | install地址 |
| chProjectId | int(11) | YES |  |  | 串货转移之前的projectId |
| chContractNo | varchar(50) | YES |  |  | 串货转移之前的contractNo |
| transferProjectId | int(11) | YES |  |  | 串货转移之后的projectId |
| transferContractNo | varchar(50) | YES |  |  | 串货转移之后的projectId |
| transferFlag | varchar(2) | YES | -1 |  | 转移标识，默认:-1,转出:1，转入:0 |
| createTime | datetime | YES |  |  | 时间 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| barcode | barcode | 否 | BTREE |
| contractNo | contractNo, barcode | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| projectId | projectId | 否 | BTREE |

---

### pm_project_soft_change_logs

**业务含义**：（待补充）

**数据量**：约 13,648 行 | 数据大小：1.5 MB | 索引大小：0.3 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 记录版本变更日志（自增） |
| projectId | int(11) | YES |  | MUL | 项目ID |
| changeVersion | varchar(10) | YES |  |  | V0001 |
| changeRemark | varchar(255) | YES |  |  | 版本变更说明 |
| latest | int(11) | YES |  |  | 0 后 1 是 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| projectId | projectId | 否 | BTREE |

---

### pm_project_soft_version

**业务含义**：项目软件版本表 - 记录项目软件版本信息

**数据量**：约 532,125 行 | 数据大小：327.8 MB | 索引大小：185.3 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 项目软件版本表（自增） |
| projectId | int(11) | YES |  | MUL | 项目ID |
| logId | int(11) | YES |  |  | 软件版本变更记录 |
| contractNo | varchar(100) | YES |  |  | 合同号 |
| itemCode | varchar(25) | YES |  |  | 产品编码 |
| barCode | varchar(25) | YES |  | MUL | 设备序列号 |
| conp | varchar(100) | YES |  | MUL | CONP版本 |
| conpType | varchar(100) | YES |  |  | 版本类型 |
| conpSeries | varchar(100) | YES |  |  | 版本系列 |
| conpMark | varchar(255) | YES |  |  | 软件版本掩码 |
| conpBak | varchar(255) | YES |  |  | 备份变更之前的版本 |
| conpChange | int(11) | YES |  |  | 0无更新 1有更新 |
| cpld | varchar(100) | YES |  |  | CPLD版本 |
| cpldBak | varchar(255) | YES |  |  | cpldBak |
| cpldChange | int(11) | YES |  |  | cpldChange |
| boot | varchar(100) | YES |  |  | BOOT版本 |
| bootBak | varchar(255) | YES |  |  | bootBak |
| bootChange | int(11) | YES |  |  | bootChange |
| pcb | varchar(100) | YES |  |  | PCB版本 |
| pcbBak | varchar(255) | YES |  |  | pcbBak |
| pcbChange | int(11) | YES |  |  | pcbChange |
| executeTime | date | YES |  |  | 若有更新的情况下为执行更新时间，否则没有实际意义 |
| datastate | int(11) | YES |  | MUL | 数据状态 0 失效 1 有效 |
| createTime | datetime | YES |  |  | 时间 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| customInfo | json | YES |  |  | 自定义信息 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| barcode | barCode | 否 | BTREE |
| idx_conp_item_query | datastate, conpType, conpSeries, conpMark, itemCode, projectId | 否 | BTREE |
| pm_project_soft_version_conp_IDX | conp | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| projectBarcodeValid | projectId, barCode, datastate | 否 | BTREE |

---

### pm_project_soft_version_history

**业务含义**：项目软件版本历史表

**数据量**：约 1,055,447 行 | 数据大小：317.8 MB | 索引大小：232.8 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 项目软件版本表（自增） |
| projectId | int(11) | YES |  | MUL | 项目ID |
| logId | int(11) | YES |  |  | 软件版本变更记录 |
| contractNo | varchar(100) | YES |  |  | 合同号 |
| itemCode | varchar(25) | YES |  |  | 产品编码 |
| barCode | varchar(25) | YES |  | MUL | 设备序列号 |
| conp | varchar(100) | YES |  | MUL | CONP版本 |
| conpType | varchar(100) | YES |  |  | 版本类型 |
| conpSeries | varchar(100) | YES |  |  | 版本系列 |
| conpMark | varchar(255) | YES |  |  | 软件版本掩码 |
| conpBak | varchar(255) | YES |  |  | 备份变更之前的版本 |
| conpChange | int(11) | YES |  |  | 0无更新 1有更新 |
| cpld | varchar(100) | YES |  |  | CPLD版本 |
| cpldBak | varchar(255) | YES |  |  | cpldBak |
| cpldChange | int(11) | YES |  |  | cpldChange |
| boot | varchar(100) | YES |  |  | BOOT版本 |
| bootBak | varchar(255) | YES |  |  | bootBak |
| bootChange | int(11) | YES |  |  | bootChange |
| pcb | varchar(100) | YES |  |  | PCB版本 |
| pcbBak | varchar(255) | YES |  |  | pcbBak |
| pcbChange | int(11) | YES |  |  | pcbChange |
| executeTime | date | YES |  |  | 若有更新的情况下为执行更新时间，否则没有实际意义 |
| datastate | int(11) | YES |  | MUL | 数据状态 0 失效 1 有效 |
| createTime | datetime | YES |  |  | 时间 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| customInfo | json | YES |  |  | 自定义信息 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| barcode | barCode | 否 | BTREE |
| idx_conp_item_query | datastate, conpType, conpSeries, conpMark, itemCode, projectId | 否 | BTREE |
| pm_project_soft_version_conp_IDX | conp | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| projectBarcodeValid | projectId, barCode, datastate | 否 | BTREE |

---

### pm_project_soleagent_lend_from_sms

**业务含义**：（待补充）

**数据量**：约 2,583 行 | 数据大小：1.5 MB | 索引大小：0.2 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| soleAgentLendId | int(11) | NO | 0 |  | 总代借货跟踪 |
| orderExecNumber | varchar(255) | YES |  |  | 执行单号 |
| orderExecNumberShort | varchar(255) | YES |  |  | 忽略版本执行单号 |
| orderCodes | varchar(255) | YES |  |  | 合并的执行单号 |
| contract | varchar(25) | YES |  | MUL | 合同号 |
| projectName | varchar(255) | YES |  |  | 由商务输入 |
| soleAgent | varchar(25) | YES |  |  | 总代名称 |
| profitCenter | varchar(6) | YES |  |  | profitCenter |
| dataSource | varchar(25) | YES | SMS |  | dataSource |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| contract | contract, profitCenter | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### pm_project_soleagent_lend_from_sms_history

**业务含义**：（待补充）

**数据量**：约 1,136 行 | 数据大小：0.2 MB | 索引大小：0.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| soleAgentLendId | int(11) | NO | 0 |  | 总代借货跟踪 |
| orderExecNumber | varchar(255) | YES |  |  | 执行单号 |
| orderExecNumberShort | varchar(255) | YES |  |  | 忽略版本执行单号 |
| orderCodes | varchar(255) | YES |  |  | 合并的执行单号 |
| contract | varchar(25) | YES |  | MUL | 合同号 |
| projectName | varchar(255) | YES |  |  | 由商务输入 |
| soleAgent | varchar(25) | YES |  |  | 总代名称 |
| profitCenter | varchar(6) | YES |  |  | profitCenter |
| dataSource | varchar(25) | YES | SMS |  | dataSource |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| contract | contract, profitCenter | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### pm_project_spot_check_ignore_item

**业务含义**：（待补充）

**数据量**：约 0 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| itemCode | varchar(25) | YES |  | MUL | 编码 |
| itemModel | varchar(64) | YES |  |  | 项目Model |
| itemName | varchar(255) | YES |  |  | 名称 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| itemCode | itemCode | 否 | BTREE |

---

### pm_project_state

**业务含义**：项目状态流转记录表 - 记录项目状态变更历史

**数据量**：约 45,915 行 | 数据大小：2.5 MB | 索引大小：3.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| projectId | int(11) | NO |  | PRI | ID标识 |
| projectPlanState | varchar(10) | YES |  | MUL | 工程计划状态 |
| projectplanTime | datetime | YES |  |  | 工程计划状态更新时间 |
| shipmentState | varchar(11) | YES |  | MUL | 项目发货状态 -1 已发货 1 未发货 2部分发货 |
| shipmentTime | datetime | YES |  |  | 发货状态更新时间戳 |
| executionState | varchar(45) | YES | 5 |  | 实施状态 |
| executionStateTime | datetime | YES |  |  | 实施状态更新时间 |
| closeProcessState | varchar(45) | YES | 10 |  | 闭环流程状态 |
| closeProcessStateTime | datetime | YES |  |  | 闭环流程状态更新时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| index_projectId | projectId | 是 | BTREE |
| projectPlanState | projectPlanState | 否 | BTREE |
| shipmentState | shipmentState | 否 | BTREE |

**样例数据**：

| projectId | projectPlanState | projectplanTime | shipmentState | shipmentTime | executionState | executionStateTime | closeProcessState | closeProcessStateTime |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 0 | None | None | 0 | 2021-12-24 15:52:26 | 5 | None | 15 | 2025-08-15 18:57:07 |
| 2 | 40 | None | -1 | 2022-07-14 00:03:25 | 90 | 2020-07-28 23:12:15 | 50 | None |
| 3 | 40 | None | -1 | 2022-07-14 00:03:25 | 80 | None | 50 | None |

---

### pm_project_supervision

**业务含义**：（待补充）

**数据量**：约 818 行 | 数据大小：0.2 MB | 索引大小：0.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| projectId | int(11) | NO |  |  | 项目头信息主键 |
| projectCode | varchar(45) | NO |  | MUL | 项目名称 |
| projectName | varchar(200) | YES |  |  | 项目名称 |
| channel | varchar(64) | YES |  |  | 代理商/服务商 |
| officeCode | varchar(25) | YES |  | MUL | 办事处编码 |
| type | varchar(25) | YES |  |  | 任务性质 |
| processTime | datetime | YES |  |  | 处理时间 |
| state | bit(1) | NO | b'0' |  | 是否完成 |
| isDelete | bit(1) | NO | b'0' |  | 是否删除 |
| quesnaireId | int(11) | YES |  |  | 问卷ID |
| deliverFileIds | varchar(255) | YES |  |  | 交付件，fnd_files id |
| remark | text | YES |  |  | 备注 |
| createTime | datetime | YES |  |  | 创建时间 |
| createBy | varchar(45) | YES |  |  | 创建用户 |
| updateTime | datetime | YES |  |  | 最新更新时间 |
| updateBy | varchar(45) | YES |  |  | 最新更新用户 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| department | officeCode | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| projectCode_index | projectCode | 否 | BTREE |

---

### pm_project_task

**业务含义**：（待补充）

**数据量**：约 59,042 行 | 数据大小：8.5 MB | 索引大小：8.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| taskId | int(11) | NO |  | PRI | 任务ID（自增） |
| projectId | int(11) | YES |  | MUL | ID标识 |
| projectType | varchar(25) | YES | 10 | MUL | 项目类型 默认售后项目10 售前测试20 详见fnd_basic_data |
| contractNo | varchar(45) | YES |  |  | 合同号 |
| taskTypeCode | varchar(45) | YES |  | MUL | 任务类型code，关联基础数据表 |
| taskTypeId | varchar(25) | YES |  |  | 任务类型id，关联基础数据表 |
| taskName | varchar(255) | YES |  |  | 任务名 |
| eventPlanHappenDate | datetime | YES |  |  | 款项计划发生日期 |
| eventPlanHappenDateENG | datetime | YES |  |  | 工程计划发生日期 |
| planStartTime | datetime | YES |  |  | 计划开始日期 |
| planEndTime | datetime | YES |  |  | 计划结束日期 |
| actualStartTime | datetime | YES |  |  | 实际开始日期 |
| eventActualFinishDate | datetime | YES |  |  | 实际完成日期 |
| priority | varchar(25) | YES |  |  | 优先级 |
| progress | int(3) | YES | 0 |  | 进度百分比 |
| progressDesc | varchar(255) | YES |  |  | 进度描述 |
| status | varchar(25) | YES | 0 |  | 状态 |
| parentId | int(11) | YES |  |  | 父级任务 |
| remark | text | YES |  |  | 备注 |
| createTime | datetime | YES |  |  | 记录数据创建时间 |
| createBy | varchar(45) | YES |  |  | 记录数据创建用户 |
| updateTime | datetime | YES |  |  | 记录数据最新更新时间 |
| updateBy | varchar(45) | YES |  |  | 记录数据最新更新用户 |
| effectiveFrom | datetime | YES |  |  | 数据有效性开始时间 |
| effectiveTo | datetime | YES |  |  | 数据有效性结束时间 |
| visibleFlag | varchar(2) | YES | 1 |  | 是否可见，1表示可见，2表示不可见 |
| deliverFileIds | varchar(255) | YES |  |  | 上传的交付件 |
| customInfo | json | YES |  |  | 自定义信息 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | taskId | 是 | BTREE |
| projectId | projectId, projectType | 否 | BTREE |
| projectType | projectType, projectId | 否 | BTREE |
| taskTypeCode_Id | taskTypeCode, taskTypeId | 否 | BTREE |

---

### pm_project_warranty_callback

**业务含义**：（待补充）

**数据量**：约 5,588 行 | 数据大小：2.5 MB | 索引大小：0.4 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 项目维保回访问卷表（自增） |
| projectId | int(11) | YES |  | MUL | 项目ID |
| projectCode | varchar(45) | YES |  | MUL | 项目编码 |
| officeCode | varchar(25) | YES |  |  | 办事处 |
| contractNos | varchar(255) | YES |  |  | 合同号 |
| projectIds | varchar(255) | YES |  |  | 关联的项目 |
| projectName | varchar(255) | YES |  |  | 项目名称 |
| serviceImpl | varchar(25) | YES |  |  | 实施方式 |
| industryName | varchar(25) | YES |  |  | 行业 |
| agentChannel | varchar(255) | YES |  |  | 下单代理商 |
| finalCustomerName | varchar(255) | YES |  |  | 最终客户单位 |
| customer1 | tinytext | YES |  |  | 客户联系人1 |
| customerContact1 | tinytext | YES |  |  | 客户联系方式1 |
| customer2 | tinytext | YES |  |  | 客户联系人2 |
| customerContact2 | tinytext | YES |  |  | 客户联系方式2 |
| warrantyStartTime | date | YES |  |  | 维保开始日期 |
| warrantyEndTime | date | YES |  |  | 维保结束日期 |
| renewalIntention | int(1) | YES |  |  | 续保意向,0:无,1:有,2:待定 |
| callbackTime | datetime | YES |  |  | 回访时间 |
| nextCallbackTime | datetime | YES |  |  | 下次回访时间 |
| taskId | varchar(25) | YES |  |  | 任务ID |
| quesnaireId | int(11) | YES |  |  | 问卷ID |
| quesnaireVersion | int(11) | YES |  |  | 问卷版本 |
| quesnaireState | int(11) | YES |  |  | 状态 -1 草稿 1已提交 |
| isDelete | bit(1) | YES | b'0' |  | 删除标记 |
| remark | varchar(255) | YES |  |  | 备注 |
| compId | int(2) | YES | 0 |  | 所属公司 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| customInfo | json | YES |  |  | customInfo |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| projectCode | projectCode | 否 | BTREE |
| projectId | projectId | 否 | BTREE |

---

### pm_project_weekly

**业务含义**：（待补充）

**数据量**：约 932 行 | 数据大小：0.2 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| weeklyId | int(11) | NO |  | PRI | ID标识（自增） |
| projectId | int(11) | YES |  | MUL | 项目信息头ID |
| currentTask | varchar(100) | YES |  |  | 当前工程阶段 |
| taskStartTime | datetime | YES |  |  | 阶段开始时间 |
| taskEndTime | datetime | YES |  |  | 阶段结束时间 |
| taskDeviation | text | YES |  |  | 偏差 |
| remark | text | YES |  |  | 备注 |
| createTime | datetime | YES |  |  | 时间 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| weeklyStartTime | datetime | YES |  |  | 报告开始时间 |
| weeklyEndTime | datetime | YES |  |  | 报告结束时间 |
| weeklyState | int(11) | YES | 0 |  | 周报状态 0 草稿 1提交 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | weeklyId | 是 | BTREE |
| projectId | projectId | 否 | BTREE |

---

### pm_project_weekly_content

**业务含义**：（待补充）

**数据量**：约 12,979 行 | 数据大小：1.5 MB | 索引大小：0.2 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| weeklyId | int(11) | YES |  | MUL | ID标识 |
| optionDesc001 | text | YES |  |  | option描述 |
| optionDesc002 | text | YES |  |  | option描述 |
| optionType | int(11) | YES |  |  | option对应周报的部分 |
| createTime | datetime | YES |  |  | 时间 |
| createBy | varchar(15) | YES |  |  | 操作人 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| weeklyId | weeklyId | 否 | BTREE |

---

### pm_project_weekly_feedback

**业务含义**：（待补充）

**数据量**：约 20 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| weeklyId | int(11) | YES |  | MUL | ID标识 |
| feedback | text | YES |  |  | 反馈 |
| feedbacker | varchar(25) | YES |  |  | 反馈人 |
| feedbackTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| weeklyId | weeklyId | 否 | BTREE |

---

### pm_report_line_data

**业务含义**：（待补充）

**数据量**：约 11,315 行 | 数据大小：1.5 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 报表趋势图数据集合（自增） |
| dataTypeCode | varchar(15) | YES |  |  | 区分统计的哪种数据 |
| officeCode | varchar(25) | YES |  |  | 办事处 |
| conditionValue | varchar(25) | YES |  |  | 条件值 |
| totalValue | varchar(25) | YES |  |  | 总值 |
| specificValue | varchar(25) | YES |  |  | 比值 |
| settingTime | datetime | YES |  |  | 数据固化时间 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### pm_subcontract_deliver_files

**业务含义**：（待补充）

**数据量**：约 3,823 行 | 数据大小：1.5 MB | 索引大小：0.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| subcontractId | int(11) | YES |  | MUL | 转包项目ID |
| paymentId | int(11) | YES |  |  | 转包付款ID |
| fileName | varchar(255) | YES |  |  | 交付件名称 |
| filePath | varchar(255) | YES |  |  | 交付件路径 |
| type | varchar(45) | YES |  |  | 交付件类型,0:用服交付合同，1：用服服务单，2：工程合同 |
| uploadBy | varchar(45) | YES |  |  | 上传者 |
| uploadTime | datetime | YES |  |  | 上传时间 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |
| customInfo | json | YES |  |  | 自定义信息 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| subcontractId | subcontractId | 否 | BTREE |

---

### pm_subcontract_facilitator

**业务含义**：（待补充）

**数据量**：约 174 行 | 数据大小：0.1 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| name | varchar(64) | YES |  |  | 服务商名 |
| code | varchar(64) | YES |  |  | 服务商编号 |
| account | varchar(64) | YES |  |  | 服务商账户 |
| bankInfo | varchar(255) | YES |  |  | 开户行信息 |
| bankAccount | varchar(64) | YES |  |  | 收款账户 |
| receiver | varchar(64) | YES |  |  | 邮箱收件人 |
| cnapsCode | varchar(64) | YES |  |  | 联行号 |
| contacts | varchar(64) | YES |  |  | 联系人 |
| tel | varchar(64) | YES |  |  | 联系电话 |
| email | varchar(64) | YES |  |  | 邮箱账号 |
| state | bit(1) | YES | b'1' |  | 状态 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |
| createBy | varchar(45) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(45) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| relateType | varchar(45) | YES |  |  | 关联类型 |
| customInfo | json | YES |  |  | 自定义信息 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### pm_subcontract_project_callback

**业务含义**：（待补充）

**数据量**：约 416 行 | 数据大小：0.1 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 项目转包回访问卷表（自增） |
| subcontractId | int(11) | YES |  |  | 项目转包ID |
| taskKey | varchar(25) | YES |  |  | 任务类型 |
| taskId | varchar(25) | YES |  |  | 任务ID |
| quesnaireId | int(11) | YES |  |  | 问卷ID |
| quesnaireVersion | int(11) | YES |  |  | 问卷版本 |
| quesnaireState | int(11) | YES |  |  | 状态 -1 草稿 1已提交 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |
| customInfo | json | YES |  |  | 自定义信息 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### pm_subcontract_project_header

**业务含义**：分包项目主表 - 存储分包项目信息

**数据量**：约 3,220 行 | 数据大小：1.5 MB | 索引大小：0.5 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| subcontractName | varchar(512) | YES |  |  | 转包名称 |
| subcontractNo | varchar(64) | YES |  | MUL | 转包合同号 |
| contractNos | varchar(2048) | YES |  |  | 项目合同号 |
| projectIds | varchar(1024) | YES |  |  | 转包的项目ID |
| type | int(11) | YES |  |  | 转包类型 |
| state | int(11) | NO | 0 |  | 分包状态：-100=已拒绝, -30=已撤回, -20=已退回, -15=待修改, 0=草稿, 10=待审批, 15=审批中, 20=已通过, 30=执行中, 40=已完成 |
| callbackState | int(11) | YES |  |  | 回访状态 |
| facilitatorId | int(11) | YES |  | MUL | 服务商表ID |
| facilitatorName | varchar(64) | YES |  |  | 服务商名 |
| bankInfo | varchar(255) | YES |  |  | 服务商开户地址 |
| bankAccount | varchar(64) | YES |  |  | 服务商收款账户 |
| officeCode | varchar(25) | YES |  | MUL | 办事处部门 |
| profitDepCode | varchar(25) | YES |  | MUL | 收益部门 |
| isAccrued | bit(1) | YES |  |  | 是否计提 |
| isInvoiced | bit(1) | YES |  |  | 是否提供发票 |
| subcontractAmount | varchar(25) | YES |  |  | 转包价 |
| reason | varchar(512) | YES |  |  | 转包原因 |
| remark | varchar(512) | YES |  |  | 备注 |
| effectiveFrom | datetime | YES |  |  | 有效开始时间 |
| effectiveTo | datetime | YES |  |  | 有效结束时间 |
| zrApproveTime | datetime | YES |  |  | 最新主任审批通过时间 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| orgId | int(2) | YES | 1 |  | 所属公司 |
| customInfo | json | YES |  |  | 自定义信息 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| facilitatorId | facilitatorId | 否 | BTREE |
| officeCode | officeCode | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| profitDepCode | profitDepCode | 否 | BTREE |
| subcontractNo | subcontractNo | 否 | BTREE |

**样例数据**：

| id | subcontractName | subcontractNo | contractNos | projectIds | type | state | callbackState | facilitatorId | facilitatorName | bankInfo | bankAccount | officeCode | profitDepCode | isAccrued | isInvoiced | subcontractAmount | reason | remark | effectiveFrom | effectiveTo | zrApproveTime | createBy | createTime | updateBy | updateTime | orgId | customInfo |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | 【翟东华】合作工程师采购协议V1.2 | TS201710310001 |  |  | None | 40 | None | 1 | 杭州德昌隆信息技术有限公司 | 杭州银行市府大楼支行 | 7820 8100 0348 36 | 162020 | 162020 | b'\x01' | b'\x01' | 200000 | 系统上线导入数据系统上线导入数据系统上线导入数据系统上线导入数据系统上线导入数据系统上线导入数据系统 | 系统上线导入数据 | None | None | None |  | None | w02799 | 2018-07-18 08:46:04 | 1 | None |
| 2 | 舟山市综合保税区空港分区智能化项目 

 | TS201711070006 |  |  | None | 40 | None | 1 | 杭州德昌隆信息技术有限公司 | 杭州银行市府大楼支行 | 7820 8100 0348 36 | 162022 | 162022 | b'\x01' | b'\x00' | 29900 | 系统上线导入数据 | 系统上线导入数据 | None | None | 2017-11-06 00:00:00 |  | None |  | None | 1 | None |
| 3 | 舟山市嵊泗县智能公路交通管理设备采购项目 | TS201702160003 |  |  | None | 40 | None | 1 | 杭州德昌隆信息技术有限公司 | 杭州银行市府大楼支行 | 7820 8100 0348 36 | 162022 | 162022 | b'\x01' | b'\x01' | 2900 | 系统上线导入数据 | 系统上线导入数据 | None | None | 2017-02-04 00:00:00 |  | None |  | None | 1 | None |

---

### pm_subcontract_project_line

**业务含义**：（待补充）

**数据量**：约 51,088 行 | 数据大小：8.5 MB | 索引大小：15.6 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| subcontractId | int(11) | NO |  | MUL | 转包项目Id |
| projectId | int(11) | YES |  | MUL | 原项目Id |
| barcode | varchar(25) | YES |  | MUL | 设备序列号 |
| itemCode | varchar(25) | YES |  | MUL | 设备编码 |
| itemModel | varchar(255) | YES |  |  | 设备型号 |
| itemName | varchar(255) | YES |  |  | 设备名称 |
| contractNo | varchar(50) | YES |  | MUL | 合同号 |
| createTime | datetime | YES |  |  | 时间 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| barcode | barcode | 否 | BTREE |
| contractNo | contractNo | 否 | BTREE |
| itemCode | itemCode | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| projectId | projectId | 否 | BTREE |
| unique_index | subcontractId, barcode | 是 | BTREE |

---

### pm_subcontract_project_payment

**业务含义**：（待补充）

**数据量**：约 3,351 行 | 数据大小：0.4 MB | 索引大小：0.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| subcontractId | int(11) | NO |  | MUL | 转包项目Id |
| ratio | varchar(10) | YES |  |  | 比例 |
| amount | varchar(25) | YES |  |  | 付款金额 |
| confirmTime | datetime | YES |  |  | 提交时间 |
| paymentTime | datetime | YES |  |  | 付款时间 |
| remark | varchar(512) | YES |  |  | 备注 |
| sseId | bigint(20) | YES | -1 |  | sse报销单审批行ID,0：会进行匹配跟新 |
| createTime | datetime | YES |  |  | 时间 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| customInfo | json | YES |  |  | 自定义信息 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| subcontractId | subcontractId | 否 | BTREE |

---

### pm_subcontract_project_payment_sse

**业务含义**：（待补充）

**数据量**：约 3,608 行 | 数据大小：1.5 MB | 索引大小：0.4 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) unsigned | YES | 0 | MUL | ID标识 |
| workNo | varchar(10) | YES |  | MUL | 工号 |
| name | varchar(10) | YES |  |  | 姓名 |
| offerNum | varchar(20) | YES |  |  | 申请单号 |
| applyAmount | decimal(16,2) | YES |  |  | 申请金额 |
| receiver | varchar(255) | YES |  |  | 收款人 |
| bank | varchar(80) | YES |  |  | 开户行 |
| bankAccount | varchar(255) | YES |  |  | 银行账号 |
| useage | varchar(512) | YES |  |  | 汇款用途 |
| paystate | varchar(25) | YES |  |  | 付款状态 |
| confirmTime | datetime | YES |  |  | 提交时间 |
| paymentTime | datetime | YES |  |  | 付款时间 |
| approveState | varchar(25) | NO |  |  | 审批状态 |
| type | varchar(255) | YES |  |  | 费用类别 |
| approveAmount | decimal(16,2) | YES |  |  | 权签金额 |
| remark | text | YES |  |  | 说明 |
| subcontractNo | varchar(255) | YES |  | MUL | 服务合同号 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| id | id | 否 | BTREE |
| subcontractNo | subcontractNo | 否 | BTREE |
| workNo | workNo | 否 | BTREE |

---

### pm_subcontract_project_price

**业务含义**：（待补充）

**数据量**：约 5,176 行 | 数据大小：1.5 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| subcontractId | int(11) | NO |  |  | 转包项目Id |
| contractNo | varchar(50) | YES |  |  | 合同号 |
| orderExecNumber | varchar(25) | YES |  |  | 执行单号 |
| projectCode | varchar(25) | YES |  |  | 项目编码 |
| engineeFee | varchar(25) | YES |  |  | 工程服务价 |
| objId | varchar(64) | YES |  |  | SMS链接参数1 |
| procType | varchar(25) | YES |  |  | SMS链接参数2 |
| price | varchar(25) | YES |  |  | 合同转包价 |
| createTime | datetime | YES |  |  | 时间 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### pm_workflow

**业务含义**：（待补充）

**数据量**：约 180 行 | 数据大小：1.5 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| processKey | varchar(25) | NO |  |  | 流程定义key |
| taskKey | varchar(25) | YES |  |  | 任务Key |
| applyTime | datetime | YES |  |  | 申请时间 |
| beginTime | datetime | YES |  |  | 开始时间 |
| endTime | datetime | YES |  |  | 结束时间 |
| dueTime | datetime | YES |  |  | 过期时间 |
| procInstId | varchar(64) | YES |  | MUL | 流程实例ID |
| message | varchar(255) | YES |  |  | 处理消息 |
| status | varchar(255) | NO | PENDING |  | 状态 |
| userId | int(11) | NO | 0 |  | userinfo表ID |
| objType | varchar(25) | NO |  | MUL | 对象类型 |
| objId | int(11) | NO | 0 | MUL | 对象Id |
| dataType | varchar(25) | NO |  |  | 数据类型 |
| dataId | int(11) | NO | 0 |  | 数据Id |
| createBy | varchar(45) | NO |  |  | 操作人 |
| createTime | datetime | NO | CURRENT_TIMESTAMP |  | 时间 |
| updateBy | varchar(45) | NO |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| orgId | int(2) | YES | 0 |  | 组织ID |
| customInfo | json | YES |  |  | 自定义信息 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| objDataKey | objType, objId, dataType, dataId | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| procInstId | procInstId | 否 | BTREE |
| worfFlow_objId | objId | 否 | BTREE |

---

### prob_main

**业务含义**：问题跟踪主表 - 记录项目问题/故障信息

**数据量**：约 1,080 行 | 数据大小：3.5 MB | 索引大小：0.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| probNum | varchar(25) | YES |  | MUL | 编码 |
| watch | varchar(10) | YES |  |  | 跟踪 |
| theme | varchar(255) | YES |  |  | 主题 |
| desc | text | YES |  |  | 问题描述 |
| solution | text | YES |  |  | 解决方案 |
| status | varchar(10) | YES |  |  | 问题状态：0=草稿, 1=待处理, 4=已解决, 5=已关闭, 6=已验证, 8=处理中 |
| startdate | date | YES |  |  | 开始日期 |
| duedate | date | YES |  |  | 计划完成日期 |
| attachments | varchar(255) | YES |  |  | 文件 |
| priority | varchar(10) | YES |  |  | 严重级别 |
| productType | text | YES |  |  | 产品类型 |
| trackingUser | varchar(10) | YES |  |  | 跟踪用户 |
| visibleRange | int(1) | NO | 0 |  | 可见范围，0:All, 1:对内 |
| createBy | varchar(15) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |
| remark | text | YES |  |  | 审批意见 |
| customInfo | json | YES |  |  | 自定义信息 |
| probTicketNo | varchar(255) | YES |  |  | 网上问题单号 |
| relatedSceneTypes | varchar(255) | YES |  |  | relatedSceneTypes |
| relatedSceneTypesMark | bigint(20) | YES |  |  | relatedSceneTypes的bitmark |
| mitigationActionTypes | varchar(255) | YES |  |  | mitigationActionTypes |
| mitigationActionTypesMark | bigint(20) | YES |  |  | mitigationActionTypes的bitmark |
| solutionActionTypes | varchar(255) | YES |  |  | solutionActionTypes |
| solutionActionTypesMark | bigint(20) | YES |  |  | solutionActionTypes的bitmark |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| probNum_IDX | probNum, id | 否 | BTREE |

**样例数据**：

| id | probNum | watch | theme | desc | solution | status | startdate | duedate | attachments | priority | productType | trackingUser | visibleRange | createBy | createTime | updateBy | updateTime | effectiveFrom | effectiveTo | remark | customInfo | probTicketNo | relatedSceneTypes | relatedSceneTypesMark | mitigationActionTypes | mitigationActionTypesMark | solutionActionTypes | solutionActionTypesMark |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | SP.201705241029 | 15 | 神州三号C011D001P05~C011D002P05版本存在内存泄漏 | <font size="3" face="宋体">



</font><p style="marg | <span style='font-family: "微软雅黑","sans-serif"; fon | 4 | 2017-05-24 | 2017-05-31 |  | 3 | 除盒式交换机外的所有产品 | m02547 | 0 | m02547 | 2017-05-24 10:40:59 | None | 2017-05-24 10:43:47 | 2017-05-24 10:40:59 | None | 同意。 | None | None | None | None | None | None | None | None |
| 2 | SP.201705240928 | 14 | 2.1代板使用神州二号版本可能导致板卡逻辑挂死 | <p>某局点框式设备配置了FW1000-BLADE-EI板卡，当设备收到分片报文的分片偏移量（off | 如使用了神州二号版本（S211C008系列），并且部署了FW1000-Blade-EI，请尽快申请神 | 6 | 2017-05-24 | 2017-05-31 |  | 1 | FW1000-Blade-EI | z02544 | 0 | z02544 | 2017-05-24 11:22:26 | l00673 | 2017-06-09 14:43:40 | 2017-05-24 11:22:26 | 2017-06-09 14:43:40 | 技术公告由研发编写发出，请研发用自己的账号进行编写申请发布。 | None | None | None | None | None | None | None | None |
| 3 | SP.201706081347 | 15 | 防火墙产品(包含带防火墙插卡的DPX产品)升级神州三号版本，使用命令行变更配置,需write fil | <p class="MsoNormalCxSpFirst" style="line-height:  | 不涉及 | 4 | 2017-06-08 | 2017-07-08 | 117 | 2 | 全系列防火墙产品（包含带防火墙插卡的DPX产品）、UTM | d00537 | 0 | d00537 | 2017-06-08 14:01:15 | None | 2017-06-09 14:44:14 | 2017-06-08 14:01:15 | None | None | None | None | None | None | None | None | None | None |

---

### prob_product

**业务含义**：（待补充）

**数据量**：约 31,823 行 | 数据大小：4.5 MB | 索引大小：3.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| probId | int(11) | YES | 0 | MUL | ProbId |
| productCode | varchar(255) | YES |  |  | 产品大类 |
| productSubCode | varchar(255) | YES |  |  | 产品小类 |
| itemCode | varchar(255) | NO |  |  | item编码 |
| itemModel | varchar(255) | YES |  |  | item类型 |
| itemDesc | varchar(255) | YES |  |  | item描述 |
| status | int(11) | YES | 1 |  | 0 失效 1 有效 |
| customInfo | json | YES |  |  | 自定义信息 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| probId_Status_IDX | probId, status | 否 | BTREE |
| probId_status_item_IDX | probId, status, itemCode | 否 | BTREE |

---

### prob_product_component

**业务含义**：（待补充）

**数据量**：约 66 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| type | varchar(100) | YES |  |  | 分组 |
| name | varchar(100) | YES |  |  | 名称 |
| version | varchar(100) | YES |  |  | 版本 |
| parentId | int(11) | YES |  |  | 父节点 |
| state | bit(1) | YES | b'1' |  | 状态 |
| customInfo | json | YES |  |  | 自定义信息 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### prob_read_log

**业务含义**：（待补充）

**数据量**：约 43,284 行 | 数据大小：2.5 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| probId | int(11) | NO |  |  | ID标识 |
| reader | varchar(25) | NO |  |  | 查阅人 |
| readTime | datetime | NO |  |  | 查阅时间 |
| status | int(1) | NO | 0 |  | 是否已经确认查阅 |
| firstTime | datetime | YES |  |  | 第一次查阅时间 |
| commitTime | datetime | YES |  |  | 确认时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### prob_restore

**业务含义**：（待补充）

**数据量**：约 1,269 行 | 数据大小：0.3 MB | 索引大小：0.4 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 问题修复数据对象（自增） |
| probId | int(11) | YES | 0 | MUL | 涉及到的问题ID |
| serialNum | varchar(50) | YES |  | MUL | 序列号 |
| itemModel | varchar(50) | YES |  | MUL | 设备类型 |
| processId | int(11) | YES | 0 | MUL | 记录任务流程过程中的相关信息 |
| officeCode | varchar(25) | YES |  |  | 办事处编码 |
| conp | varchar(255) | YES |  |  | 任务发布时的软件版本 |
| boot | varchar(100) | YES |  |  | BOOT版本 |
| cpld | varchar(100) | YES |  |  | CPLD版本 |
| pcb | varchar(100) | YES |  |  | PCB版本 |
| projectId | int(11) | YES | 0 | MUL | 涉及到的项目ID |
| projectName | varchar(255) | YES |  |  | 项目名称 |
| contractNo | varchar(255) | YES |  |  | 合同号 |
| assignee | varchar(25) | YES |  |  | 办理用户 |
| assigneeRole | int(11) | YES | 0 |  | 办理角色 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| itemModel | itemModel | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| probId_serialNum_IDX | probId, serialNum | 否 | BTREE |
| processId | processId | 否 | BTREE |
| projectId | projectId | 否 | BTREE |
| serialNum | serialNum | 否 | BTREE |

---

### prob_restore_process

**业务含义**：（待补充）

**数据量**：约 9 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 记录问题修复的流程流转过程（自增） |
| probId | int(11) | YES |  | MUL | 问题ID |
| restoreStatus | int(11) | YES |  |  | 修复任务流转状态 |
| restoreRemark | text | YES |  |  | 流转备注说明 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| probId | probId, restoreStatus | 否 | BTREE |

---

### prob_restore_weekly

**业务含义**：（待补充）

**数据量**：约 0 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 任务进展周报（自增） |
| probId | int(11) | YES |  | MUL | 问题主键 |
| fileId | int(11) | YES |  |  | 附件ID |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| probId | probId | 否 | BTREE |

---

### prob_soft_version

**业务含义**：（待补充）

**数据量**：约 0 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| conp | varchar(100) | YES |  | MUL | CONP版本 |
| cpld | varchar(100) | YES |  |  | CPLD版本 |
| boot | varchar(100) | YES |  |  | BOOT版本 |
| pcb | varchar(100) | YES |  |  | PCB版本 |
| createdBy | varchar(25) | YES |  |  | 操作人 |
| createdTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| conp | conp, cpld, boot, pcb | 是 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### prob_softwares

**业务含义**：（待补充）

**数据量**：约 11,456 行 | 数据大小：11.5 MB | 索引大小：1.3 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 已知问题影响的软件版本表（自增） |
| probId | int(11) | YES | 0 | MUL | 问题ID |
| conp | varchar(100) | YES |  | MUL | CONP版本 |
| cpld | varchar(100) | YES |  | MUL | CPLD版本 |
| boot | varchar(100) | YES |  | MUL | BOOT版本 |
| pcb | varchar(100) | YES |  | MUL | PCB版本 |
| manualEntry | varchar(2048) | YES |  |  | 手工录入 |
| manualEntrySub | varchar(2048) | YES |  |  | 手工录入拆解 |
| entryType | varchar(100) | YES |  |  | 版本类型 |
| entrySeries | varchar(100) | YES |  |  | 版本系列 |
| entryStart | varchar(255) | YES |  |  | 版本范围开始 |
| entryEnd | varchar(255) | YES |  |  | 版本范围结束 |
| markStart | varchar(255) | YES |  |  | 缺省补充版本范围开始 |
| markEnd | varchar(255) | YES |  |  | 缺省补充版本范围结束 |
| affectedType | int(11) | YES | 0 | MUL | 影响类型，0：所有系列，1：盒式系列，2：框式系列 |
| groupId | bigint(11) | YES | 0 |  | 分组ID |
| splited | int(11) | YES | 0 |  | 是否拆解 |
| datastate | int(11) | YES | 1 | MUL | 0 失效 1 有效 |
| createBy | varchar(10) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(10) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| customInfo | json | YES |  |  | 自定义信息 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| affectedType | affectedType | 否 | BTREE |
| boot | boot | 否 | BTREE |
| conp | conp | 否 | BTREE |
| cpld | cpld | 否 | BTREE |
| datastate_entry_probId_IDX | datastate, entryType, entrySeries, probId | 否 | BTREE |
| pcb | pcb | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| probId_datastate_IDX | probId, datastate | 否 | BTREE |

---

### project_info_from_sms

**业务含义**：（待补充）

**数据量**：约 3,190 行 | 数据大小：1.5 MB | 索引大小：0.2 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| smsId | bigint(11) | NO | 0 |  | ID标识 |
| orderCode | varchar(25) | NO |  | MUL | 编码 |
| predBidDate | datetime | YES |  |  | 时间 |
| projectName | varchar(255) | YES |  |  | 名称 |
| firstChannelCode | varchar(255) | YES |  |  | 编码 |
| firstChannelName | varchar(100) | YES |  |  | 名称 |
| channelCode | varchar(255) | YES |  |  | 编码 |
| channelName | varchar(255) | YES |  |  | 名称 |
| contractNo | varchar(25) | YES |  |  | 合同编号 |
| marketName | varchar(25) | YES |  |  | 名称 |
| systemName | varchar(25) | YES |  |  | 名称 |
| expendName | varchar(25) | YES |  |  | 名称 |
| industryName | varchar(25) | YES |  |  | 名称 |
| industryNewName | varchar(25) | YES |  |  | 对应的子行业 |
| totaljine | decimal(12,2) | YES |  |  | 总金额 |
| salesName | varchar(25) | YES |  |  | 名称 |
| officeName | varchar(25) | YES |  |  | 名称 |
| solutionname | varchar(1000) | YES |  |  | 名称 |
| projectpropertyName | varchar(20) | YES |  |  | 名称 |
| customerProjectCode | varchar(255) | YES |  |  | 客户项目编码 |
| customerProjectName | varchar(255) | YES |  |  | 客户项目名称 |
| username | varchar(255) | YES |  |  | 名称 |
| realname | varchar(128) | YES |  |  | 名称 |
| officeCode | varchar(255) | YES |  |  | 编码 |
| org_id | int(11) | YES |  | MUL | ID标识 |
| customInfo | json | YES |  |  | 自定义信息 |
| source | varchar(25) | YES | SMS |  | 数据来源 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| orderCode | orderCode, org_id | 否 | BTREE |
| org_id | org_id | 否 | BTREE |

---

### rma_app_info

**业务含义**：（待补充）

**数据量**：约 5,507 行 | 数据大小：2.5 MB | 索引大小：0.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| sheetID | varchar(25) | NO |  | UNI | RMA申请单据代码 |
| applicant | varchar(10) | YES |  |  | 申请发起人 |
| officeCode | varchar(25) | YES |  |  | 办事处或部门编码 |
| customer_name | varchar(255) | YES |  |  | 客户名称 |
| project_name | varchar(255) | YES |  |  | 项目名称 |
| addreID | int(11) | YES |  |  | 收件人ID，关联addressee_info表 |
| application_time | datetime | YES |  |  | 申请发起时间 |
| back | varchar(10) | YES |  |  | 返回类型 |
| tain | varchar(10) | YES |  |  | 维保类型 |
| serve | varchar(10) | YES |  |  | 服务类型 |
| duty_person | varchar(10) | YES |  |  | 负责人 |
| isSend | char(1) | YES | 0 |  | 申请备件状态（0：未发货；1：已发货 2：已接货） |
| isReceive | char(1) | YES | 0 |  | 是否接收(0:未接受 1：已接收) |
| take_place | char(1) | YES | 0 |  | 备件出处(0:未选择 1:供应链；2：库存) 此时的备件出处只是记录临时的状态，但也可算真实的状态，根据系统设定已不可更改，只需等审批确定后 |
| isUnion | int(11) | YES |  |  | 是否联合供应链发货 |
| remark | text | YES |  |  | 备注 |
| data_state | char(1) | YES | 0 |  | 数据状态（0：最新；1：历史数据） |
| his_addre | varchar(64) | YES |  |  | 处理历史数据 |
| his_zipCode | varchar(10) | YES |  |  | 处理历史数据 |
| his_addr | varchar(1024) | YES |  |  | 处理历史数据 |
| his_addre_tel | varchar(25) | YES |  |  | 处理历史数据 |
| version_no | int(11) | YES | 0 |  | 发货配置版本号 |
| insteadState | int(11) | YES | 0 |  | insteadState |
| rma_back_time | datetime | YES |  |  | 技服执行坏件返回时间 |
| rmaRoleIsPass | int(11) | YES |  |  | 故障审核是否通过 0否1是 |
| rmaRoleOpinion | varchar(255) | YES |  |  | 故障审核审批意见 |
| rmaRoleAuditTime | datetime | YES |  |  | 故障审核时间 |
| rmaRoleAuditUser | varchar(25) | YES |  |  | 故障审核用户 |
| qaRoleIsPass | int(11) | YES |  |  | 质量审核是否通过 |
| qaRoleOpinion | varchar(255) | YES |  |  | 质量审核审批意见 |
| qaRoleAuditTime | datetime | YES |  |  | 质量审核时间 |
| qaRoleAuditUser | varchar(25) | YES |  |  | 质量审核用户 |
| insteadLicense | int(11) | YES | 0 |  | 授权License变更，1:需要，-1:不需要 |
| insteadLicenseMail | varchar(255) | YES |  |  | 授权License接收邮箱 |
| licenseMailTime | datetime | YES |  |  | 授权License邮件发送时间 |
| customInfo | json | YES |  |  | 自定义信息 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| sheetID | sheetID | 是 | BTREE |

---

### rma_applicant

**业务含义**：（待补充）

**数据量**：约 858 行 | 数据大小：0.2 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| spare_code | varchar(8) | YES |  | UNI | 流水号 |
| product_code | varchar(50) | YES |  |  | 产品号 |
| product_name | varchar(200) | YES |  |  | 产品名称 |
| username | varchar(50) | YES |  |  | 用户名称 |
| project_name | varchar(200) | YES |  |  | 项目名称/申请原因 |
| old_bar_code | varchar(20) | YES |  |  | 旧设备序列号 |
| user_linkman | varchar(50) | YES |  |  | 用户联系人 |
| back_type | varchar(1) | YES |  |  | 返回类型('1'为"开坏箱",'2'为"开局坏",'3'为“网上运行坏”,'4'为"备件发货坏",'5'为"其他") |
| back_state | varchar(200) | YES |  |  | 返回类型说明 |
| back_num | varchar(11) | YES |  |  | 返回数量 |
| user_linkman_telephone | varchar(50) | YES |  |  | 用户联系人电话 |
| applicant_time | datetime | YES |  |  | 申请时间 |
| problem_description | text | YES |  |  | 问题描述 |
| analysis_process | varchar(1000) | YES |  |  | 现场分析过程(上传) |
| duty_person | varchar(50) | YES |  |  | 代理公司和负责人 |
| start_first_time | varchar(50) | YES |  |  | 初次运行时间 |
| problem_first_time | varchar(50) | YES |  |  | 故障发生时间 |
| applicant_person | varchar(50) | YES |  |  | 申请人 |
| take_place | varchar(1) | YES | 1 |  | 取处(1为供应链部门，2为库存) |
| os_id | varchar(1000) | YES |  |  | 库存id |
| address | text | YES |  |  | 地址 |
| zip_code | varchar(50) | YES |  |  | 邮政编码 |
| tain_type | varchar(1) | YES |  |  | 维保类型(‘1’为“服务合同”,'2'为"项目订单") |
| project_code | varchar(50) | YES |  |  | 项目订单号(针对维保类型为项目订单) |
| serve_type | varchar(1) | YES |  |  | 服务类型('1'为“坏件先退”,'2'为“好件先行”) |
| remark | text | YES |  |  | 备注 |
| isPass | varchar(1) | YES | 0 |  | 是否通过(0为未处理，1为通过，2为未通过) |
| isSend | varchar(1) | YES | 0 |  | 是否发货(0为未发货，1为已发货) |
| rma_type | varchar(1) | YES | 0 |  | 备件类型(0为显示，1为不显示) |
| isNew | varchar(1) | YES | y |  | 是否新增 |
| isChange_duty | varchar(1) | YES | n |  | 是否Change_duty |
| opinion | text | YES |  |  | 意见 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| id | id | 是 | BTREE |
| PRIMARY | id | 是 | BTREE |
| spare_code | spare_code | 是 | BTREE |

---

### rma_bar

**业务含义**：（待补充）

**数据量**：约 1,454 行 | 数据大小：1.5 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(10) | NO |  | PRI | ID标识（自增） |
| rma_id | int(10) | YES |  | MUL | ID标识 |
| old_bar_code | varchar(50) | YES |  |  | 编码 |
| item_code | varchar(50) | YES |  |  | 编码 |
| item_name | varchar(1000) | YES |  |  | 名称 |
| project_code | varchar(50) | YES |  |  | 编码 |
| project_name | varchar(50) | YES |  |  | 名称 |
| problem_description | text | YES |  |  | problemdescription |
| back_state | varchar(50) | YES |  |  | backstate |
| start_first_time | varchar(50) | YES |  |  | 时间 |
| problem_first_time | varchar(50) | YES |  |  | 时间 |
| analysis_process | varchar(200) | YES |  |  | analysisprocess |
| tain_process | varchar(200) | YES |  |  | tainprocess |
| isOK | varchar(1) | YES | 0 |  | 是否核销(0为未核销 1为已核销) |
| hexiao_time | datetime | YES |  |  | 核销时间 |
| isBack | varchar(1) | YES | 0 |  | 是否返回(0为未返回1为已返回) |
| back_time | datetime | YES |  |  | 返回时间 |
| EMS | varchar(20) | YES |  |  | 快递单号 |
| EMS_company | varchar(20) | YES |  |  | 快递公司 |
| receive_person | varchar(10) | YES |  |  | 收件人 |
| back_type | varchar(50) | YES |  |  | back类型 |
| tain_type | varchar(50) | YES |  |  | tain类型 |
| serve_type | varchar(50) | YES |  |  | serve类型 |
| spare_code | varchar(15) | YES |  |  | 编码 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| rma_id | rma_id | 否 | BTREE |

---

### rma_info2mes_result

**业务含义**：（待补充）

**数据量**：约 3,020 行 | 数据大小：6.5 MB | 索引大小：0.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| sheetID | varchar(15) | NO |  | MUL | RMA申请流水号，多个合同号_n |
| type | varchar(1) | NO |  |  | 接口上传结果，S、E |
| message | varchar(255) | NO |  |  | 上传结果信息 |
| xmlStr | text | YES |  |  | 上传的xml：rmaInfoHeader |
| xmlStr1 | text | YES |  |  | 上传的xml：rmaInfoDeatil |
| createTime | datetime | YES |  |  | 上传时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| sheetID | sheetID | 否 | BTREE |

---

### rma_repair_report_from_mes

**业务含义**：（待补充）

**数据量**：约 2,166 行 | 数据大小：0.5 MB | 索引大小：0.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| sheetId | varchar(25) | YES |  | MUL | ID标识 |
| barCode | varchar(50) | YES |  |  | 编码 |
| contractNo | varchar(25) | YES |  |  | 合同编号 |
| result | tinytext | YES |  |  | 结果 |
| path | varchar(255) | YES |  |  | 路径 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| sheetId_where_index | sheetId | 否 | BTREE |

---

### rma_spare_info

**业务含义**：（待补充）

**数据量**：约 13,881 行 | 数据大小：4.5 MB | 索引大小：0.2 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| tx_id | int(11) | YES |  | MUL | 交易号(关联application_transInfo表） |
| item_code | varchar(15) | YES |  |  | 物料号 |
| item_name | varchar(255) | YES |  |  | 物料名称 |
| contractNo | varchar(25) | YES |  |  | 合同号 |
| contractRemark | varchar(4096) | YES |  |  | 合同备注 |
| project_name | varchar(255) | YES |  |  | 项目名称 |
| problem_desc | text | YES |  |  | 问题描述 |
| first_working_time | varchar(25) | YES |  |  | 第一次运行时间 |
| conk_out_time | varchar(25) | YES |  |  | 故障发生时间 |
| doa_path | varchar(100) | YES |  |  | doa故障分析单（下载路径） |
| check_path | varchar(100) | YES |  |  | 检测报告(下载路径) |
| repair_state | char(1) | YES |  |  | 维修状态（保留字段） |
| isBack | char(1) | YES | 0 |  | 坏件是否返回（0：未返回;1:已返回） |
| back_time | datetime | YES |  |  | 返回时间 |
| isOK | char(1) | YES | 0 |  | 核销状态(0:未核销；1:已核销) |
| hexiao_time | datetime | YES |  |  | 核销时间 |
| analysis_state | int(11) | YES |  |  | 坏件故障分析状态  -1 未分析  1 已分析 |
| insteadLicense | int(11) | YES | 0 |  | 授权License变更，1:需要，-1:不需要 |
| insteadLicenseMail | varchar(255) | YES |  |  | 授权License接收邮箱 |
| licenseMailTime | datetime | YES |  |  | 授权License邮件发送时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| tx_id | tx_id | 否 | BTREE |

---

### role

**业务含义**：（待补充）

**数据量**：约 7 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| roleId | int(11) | NO |  |  | ID标识 |
| roleName | varchar(10) | NO |  |  | 名称 |
| status | int(11) | YES |  |  | 状态 |
| mark | text | YES |  |  | 标记 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### serve_type

**业务含义**：（待补充）

**数据量**：约 4 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| serve | varchar(10) | NO |  | MUL | 服务标识 |
| serve_type | varchar(10) | YES |  |  | serve类型 |
| remark | text | YES |  |  | 备注 |
| id | int(11) | NO |  | PRI | ID标识（自增） |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| serve_where_index | serve | 否 | BTREE |

---

### shipment_barcode_from_spms_unique

**业务含义**：发货条码唯一表

**数据量**：约 233,076 行 | 数据大小：17.5 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| contract_code | varchar(25) | YES |  |  | 编码 |
| barcode | varchar(50) | YES |  |  | 编码 |
| itemCode | varchar(16) | YES |  |  | 编码 |
| barcode2 | varchar(50) | YES |  |  | 条码2 |
| itemCode2 | varchar(16) | YES |  |  | 项目编码 |
| rmaState | int(1) | NO | 0 |  | rmaState |

---

### sms_ofst_contract_head_sap

**业务含义**：（待补充）

**数据量**：约 60,264 行 | 数据大小：22.6 MB | 索引大小：3.5 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | bigint(20) | NO |  | PRI | 表信息：从SAP刷新的合同头信息，此表信息定时刷新（自增） |
| contract_num | varchar(45) | YES |  | MUL | contract数量 |
| batch_code | varchar(10) | YES |  |  | 编码 |
| project_name | varchar(200) | YES |  |  | 名称 |
| order_num | varchar(25) | YES |  |  | 排序数量 |
| client_supplier_code | varchar(20) | YES |  |  | 编码 |
| client_supplier_name | varchar(200) | YES |  |  | 名称 |
| contract_money_amount | decimal(20,2) | NO |  |  | contract_money金额 |
| delivered_money_amount | decimal(20,2) | NO |  |  | delivered_money金额 |
| collected_money_amount | decimal(20,2) | NO |  |  | collected_money金额 |
| collected_money_ratio | double | YES | 0 |  | collectedmoneyratio |
| receivables_money_amount | decimal(20,2) | YES |  |  | receivables_money金额 |
| over_due_money_amount | decimal(20,2) | YES |  |  | over_due_money金额 |
| maketing_department_name | varchar(40) | YES |  |  | 名称 |
| office_name | varchar(20) | YES |  |  | 名称 |
| industry_name | varchar(40) | YES |  |  | 名称 |
| marketing_representative_name | varchar(20) | YES |  |  | 名称 |
| currency_name | varchar(25) | YES |  |  | 币种 |
| create_by | varchar(20) | YES |  |  | 操作人 |
| create_time | datetime | YES |  |  | 时间 |
| update_by | varchar(20) | YES |  |  | 操作人 |
| update_time | datetime | YES |  |  | 时间 |
| effective_from | datetime | YES |  |  | 生效时间 |
| effective_to | datetime | YES |  |  | 失效时间 |
| import_batch_num | varchar(12) | YES |  |  | importbatch数量 |
| contract_create_date | datetime | YES |  |  | SAP合同创建日期 |
| projectCode | varchar(80) | YES |  |  | 编码 |
| marketCode | varchar(80) | YES |  |  | 编码 |
| systemId | int(11) | YES |  |  | ID标识 |
| industryId | int(11) | YES |  |  | ID标识 |
| officeCode | varchar(80) | YES |  |  | 编码 |
| expendId | int(11) | YES |  |  | ID标识 |
| usernamec | varchar(10) | YES |  |  | 销售用户账号 |
| latest_ship_date | datetime | YES |  |  | 交货日期 |
| usernamec2 | varchar(10) | YES |  |  | 用户名2 |
| systemid_o | int(11) | YES |  |  | systemido |
| expendid_o | int(11) | YES |  |  | expendido |
| industry_name_o | varchar(40) | YES |  |  | industry名称o |
| dataSource | varchar(25) | YES | CRM |  | dataSource |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| index_contract_num | contract_num | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### sms_ofst_contract_head_sap_history

**业务含义**：（待补充）

**数据量**：约 45,355 行 | 数据大小：17.5 MB | 索引大小：1.5 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | bigint(20) | NO |  | PRI | 表信息：从SAP刷新的合同头信息，此表信息定时刷新（自增） |
| contract_num | varchar(45) | YES |  | MUL | contract数量 |
| batch_code | varchar(10) | YES |  |  | 编码 |
| project_name | varchar(200) | YES |  |  | 名称 |
| order_num | varchar(25) | YES |  |  | 排序数量 |
| client_supplier_code | varchar(20) | YES |  |  | 编码 |
| client_supplier_name | varchar(200) | YES |  |  | 名称 |
| contract_money_amount | decimal(20,2) | NO |  |  | contract_money金额 |
| delivered_money_amount | decimal(20,2) | NO |  |  | delivered_money金额 |
| collected_money_amount | decimal(20,2) | NO |  |  | collected_money金额 |
| collected_money_ratio | double | YES | 0 |  | collectedmoneyratio |
| receivables_money_amount | decimal(20,2) | YES |  |  | receivables_money金额 |
| over_due_money_amount | decimal(20,2) | YES |  |  | over_due_money金额 |
| maketing_department_name | varchar(40) | YES |  |  | 名称 |
| office_name | varchar(20) | YES |  |  | 名称 |
| industry_name | varchar(40) | YES |  |  | 名称 |
| marketing_representative_name | varchar(20) | YES |  |  | 名称 |
| currency_name | varchar(25) | YES |  |  | 币种 |
| create_by | varchar(20) | YES |  |  | 操作人 |
| create_time | datetime | YES |  |  | 时间 |
| update_by | varchar(20) | YES |  |  | 操作人 |
| update_time | datetime | YES |  |  | 时间 |
| effective_from | datetime | YES |  |  | 生效时间 |
| effective_to | datetime | YES |  |  | 失效时间 |
| import_batch_num | varchar(12) | YES |  |  | importbatch数量 |
| contract_create_date | datetime | YES |  |  | SAP合同创建日期 |
| projectCode | varchar(80) | YES |  |  | 编码 |
| marketCode | varchar(80) | YES |  |  | 编码 |
| systemId | int(11) | YES |  |  | ID标识 |
| industryId | int(11) | YES |  |  | ID标识 |
| officeCode | varchar(80) | YES |  |  | 编码 |
| expendId | int(11) | YES |  |  | ID标识 |
| usernamec | varchar(10) | YES |  |  | 销售用户账号 |
| latest_ship_date | datetime | YES |  |  | 交货日期 |
| usernamec2 | varchar(10) | YES |  |  | 用户名2 |
| systemid_o | int(11) | YES |  |  | systemido |
| expendid_o | int(11) | YES |  |  | expendido |
| industry_name_o | varchar(40) | YES |  |  | industry名称o |
| dataSource | varchar(25) | YES | SMS |  | dataSource |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| index_contract_num | contract_num | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### spare_parts

**业务含义**：（待补充）

**数据量**：约 6,282 行 | 数据大小：1.5 MB | 索引大小：0.2 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(10) | NO |  | PRI | ID标识（自增） |
| bar_code | varchar(25) | YES |  |  | 备件序列号 |
| spare_code | varchar(50) | YES |  | MUL | 流水号 |
| action_time | varchar(50) | NO |  |  | 操作时间 |
| isOK | char(1) | YES | 0 |  | 设备状态(是否核销，0为未核销，1为核销) |
| isNew | char(1) | YES | y |  | 数据状态(是否是最新的数据) |
| in_time | varchar(50) | YES |  |  | 收货时间 |
| out_time | varchar(50) | YES |  |  | 发货时间 |
| contract_sub_type | varchar(5) | YES |  |  | 类型(0为RMA ,1为项目保障,2为库存) |
| EMS | varchar(50) | YES |  |  | 快递单号 |
| EMS_company | varchar(50) | YES |  |  | 快递公司 |
| item_code | varchar(50) | YES |  |  | 物料号 |
| item_name | varchar(200) | YES |  |  | 物料名称 |
| tain_process | varchar(200) | YES |  |  | 检测报告 |
| isSure | varchar(1) | YES | 0 |  | 确认(1待确认,2以确认) |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| spare_code | spare_code | 否 | BTREE |

---

### spare_parts_applicant

**业务含义**：（待补充）

**数据量**：约 4,126 行 | 数据大小：1.5 MB | 索引大小：0.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(10) | NO |  | PRI | 主键（自增） |
| applicant_person | varchar(50) | YES |  |  | 申请人 |
| applicant_time | datetime | YES |  |  | 申请时间 |
| applicant_department | varchar(50) | YES |  |  | 申请部门 |
| spare_code | varchar(50) | YES |  | UNI | 流水号 |
| applicant_reason | varchar(500) | YES |  |  | 申请原因 |
| remark | text | YES |  |  | 备注 |
| zip_code | varchar(50) | YES |  |  | 邮政编码 |
| isPass | varchar(1) | YES | 0 |  | 是否通过(0为未处理，1为通过，2为未通过) |
| isSend | varchar(1) | NO | 0 |  | 是否通过(0为未发货，1为已发货) |
| address | varchar(200) | YES |  |  | 地址 |
| receive_person | varchar(200) | YES |  |  | 收件人 |
| receive_person_tel | varchar(200) | YES |  |  | 收件人电话 |
| spare_parts_type | varchar(1) | YES |  |  | 备件类型(0为项目保障，1为库存) |
| duty_person | varchar(10) | YES |  |  | 责任人 |
| applicant_type | varchar(1) | YES | 0 |  | 申请类型(0为普通申请，1为转移申请) |
| isChange_duty | varchar(1) | YES | 1 |  | 转移类型(0为转移责任人，1为不转移责任人) |
| isQuit | char(1) | YES |  |  | 是否为离职原因导致责任人变更，0：否，1：是 |
| isReceive | varchar(1) | YES | 0 |  | 是否收到(0为未收到，1为收到) |
| transfer_time | datetime | YES |  |  | 转移时间 |
| applicant_project | varchar(255) | YES |  |  | applicantproject |
| start_time | date | YES |  |  | 时间 |
| promise_returntime | date | YES |  |  | 时间 |
| kept_place | varchar(255) | YES |  |  | keptplace |
| beforeChange_spareCode | varchar(50) | YES |  |  | 编码 |
| change_type | char(1) | YES |  |  | 变更类型 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| spare_code | spare_code | 是 | BTREE |

---

### sys_state_or_type

**业务含义**：（待补充）

**数据量**：约 30 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| stCode | varchar(25) | YES |  | MUL | 编码 |
| stName | varchar(25) | YES |  |  | 名称 |
| resolveCode | varchar(10) | YES |  | MUL | 编码 |
| resolveName | varchar(25) | YES |  |  | 名称 |
| validity | int(11) | YES | 1 |  | 1有效 0 无效 |
| remark | varchar(100) | YES |  |  | 说明 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| st | resolveCode | 否 | BTREE |
| stCode | stCode | 否 | BTREE |

---

### t_company

**业务含义**：（待补充）

**数据量**：约 3 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 公司ID，关联表外键 |
| compCode | varchar(10) | NO |  | MUL | 公司编号 |
| compName | varchar(100) | NO |  |  | 公司名称 |
| compAbbr | varchar(100) | YES |  |  | 公司简称 |
| compAccount | varchar(10) | YES |  |  | 公司账套 |
| adminID | int(11) | NO | 0 | MUL | 上级ID |
| compGrade | int(11) | YES | 1 |  | 公司级别 |
| lawyer | varchar(50) | YES |  |  | 法人 |
| address | varchar(200) | YES |  |  | 地址 |
| regAddress | varchar(200) | YES |  |  | 注册地址 |
| tel | varchar(50) | YES |  |  | 电话 |
| fax | varchar(50) | YES |  |  | 传真 |
| postCode | varchar(50) | YES |  |  | 邮编 |
| webSite | varchar(100) | YES |  |  | 网站 |
| state | bit(1) | NO | b'1' |  | 失效状态 |
| effectiveFrom | datetime | NO | CURRENT_TIMESTAMP |  | 成立时间 |
| effectiveTo | datetime | YES |  |  | 结束时间 |
| disabledTime | datetime | YES |  |  | 失效时间 |
| remark | varchar(500) | YES |  |  | 备注 |
| createBy | varchar(25) | NO |  |  | 操作人 |
| createTime | datetime | NO | CURRENT_TIMESTAMP |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| adminID | adminID | 否 | BTREE |
| compCode | compCode | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### t_data_field_relation

**业务含义**：（待补充）

**数据量**：约 0 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| dataName | varchar(255) | NO |  |  | 数据名 |
| dataType | varchar(255) | NO |  |  | 数据类型 |
| dataId | int(11) | YES | 0 |  | 数据实例ID |
| field | varchar(128) | NO |  |  | 字段 |
| alias | varchar(128) | YES |  |  | 字段别名 |
| name | varchar(128) | NO |  |  | 字段名 |
| title | varchar(255) | YES |  |  | 字段标题 |
| titleKey | varchar(255) | YES |  |  | 字段标题Key |
| cssId | varchar(255) | YES |  |  | 字段CSS id |
| cssClass | varchar(255) | YES |  |  | 字段CSS class |
| cssStyle | varchar(255) | YES |  |  | 字段CSS style |
| type | varchar(255) | YES |  |  | 字段类型 |
| render | varchar(4096) | YES |  |  | 字段处理 |
| sort | int(11) | YES | 0 |  | 排序 |
| orderable | bit(1) | YES | b'1' |  | 允许排序 |
| searchable | bit(1) | YES | b'0' |  | 允许搜索 |
| visible | bit(1) | YES | b'1' |  | 允许可见 |
| required | bit(1) | YES | b'0' |  | 必填 |
| readonly | bit(1) | YES | b'0' |  | 只读 |
| disabled | bit(1) | YES | b'0' |  | 组件失效 |
| extData | varchar(8192) | YES |  |  | 外部数据 |
| extKey | varchar(255) | YES |  |  | 外部数据key |
| extValue | varchar(255) | YES |  |  | 外部数据value |
| media | varchar(255) | YES |  |  | 传播媒介 |
| clazzName | varchar(255) | YES |  |  | 类名 |
| superData | varchar(255) | YES |  |  | 父类dataName |
| status | int(1) | YES | 1 |  | 状态 |
| compId | int(11) | YES |  |  | 公司ID |
| isSystemField | bit(1) | YES | b'1' |  | 是否为系统字段 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### t_data_operation

**业务含义**：（待补充）

**数据量**：约 14 行 | 数据大小：0.1 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| name | varchar(25) | NO |  |  | 操作名 |
| description | varchar(64) | NO |  |  | 操作描述 |
| type | int(11) | NO | -1 |  | 操作类型，导入:1，导出:0 |
| clazz | varchar(255) | NO |  |  | 操作所在类 |
| method | varchar(64) | NO |  |  | 操作类的方法 |
| parameterTypes | varchar(512) | NO |  |  | 方法参数类型 |
| formHtml | text | YES |  |  | 额外表单内容 |
| script | text | YES |  |  | 导入时的js，导出时的sql |
| columns | varchar(4096) | NO |  |  | 导出时的列 |
| empPower | varchar(4096) | NO |  |  | 员工权限 |
| depPower | varchar(4096) | NO |  |  | 部门权限 |
| state | bit(1) | NO | b'1' |  | 状态 |
| effectiveFrom | datetime | NO | CURRENT_TIMESTAMP |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |
| createBy | varchar(25) | NO |  |  | 操作人 |
| createTime | datetime | NO | CURRENT_TIMESTAMP |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### t_dictionary

**业务含义**：（待补充）

**数据量**：约 4 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(16) | NO |  | PRI | ID标识（自增） |
| dic_type_id | int(16) | NO |  |  | 字典类型id |
| dic_type_name | varchar(32) | NO |  |  | 字典类型 |
| dic_key | varchar(32) | NO |  |  | 字典key |
| dic_value | varchar(32) | NO |  |  | 字典value |
| customInfo | varchar(1024) | YES |  |  | 自定义属性 |
| sort | int(11) | NO | 0 |  | 排序 |
| status | int(1) | YES | 1 |  | 有效标志（1-有效，0-无效） |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |
| updateTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### t_down_log

**业务含义**：（待补充）

**数据量**：约 33 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 文件下载日志（自增） |
| fileIds | varchar(100) | YES |  |  | 文件对应ID |
| ip | varchar(25) | YES |  |  | 请求的IP地址 |
| timeline | int(11) | YES |  |  | 时间戳 |
| downloadTime | datetime | YES |  |  | 下载时间 |
| user | varchar(25) | YES |  |  | 用户名 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### t_file

**业务含义**：（待补充）

**数据量**：约 291 行 | 数据大小：1.5 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 系统上传文件描述（自增） |
| typeId | int(11) | YES |  |  | 对应file_type表的主键 |
| name | varchar(255) | YES |  |  | 文件名称 |
| path | varchar(500) | YES |  |  | 文件存储路径 |
| ext | varchar(50) | YES |  |  | 文件名后缀 |
| size | int(11) | YES |  |  | 文件大小 |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |
| createBy | varchar(15) | YES |  |  | 操作人 |
| downloadKey | varchar(255) | YES |  |  | downloadKey |
| dataType | varchar(64) | YES |  |  | 关联数据类型 |
| dataId | int(11) | YES |  |  | 关联数据ID |
| customInfo | json | YES |  |  | 自定义信息 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### t_file_type

**业务含义**：（待补充）

**数据量**：约 9 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 根据系统业务类型划分上传的文件分类（自增） |
| name | varchar(25) | YES |  |  | 分类名称 |
| limitSize | int(11) | YES |  |  | 大小限制 |
| allowType | varchar(255) | YES |  |  | 文件类型限制 |
| rename | tinyint(1) | YES |  |  | 是否进行重命名 |
| cut | tinyint(1) | YES |  |  | 是否进行压缩 |
| thumbnail | tinyint(1) | YES |  |  | 是否生成缩略图 |
| dir | varchar(255) | YES |  |  | 服务器保存的相对路径 |
| uploadUrl | varchar(255) | YES |  |  | 文件上传URL |
| code | varchar(64) | YES |  |  | 前端或后端调用代码 |
| createTime | datetime | YES |  |  | 时间 |
| createBy | varchar(10) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(10) | YES |  |  | 操作人 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### t_mails

**业务含义**：（待补充）

**数据量**：约 10,817 行 | 数据大小：32.0 MB | 索引大小：0.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| subject | varchar(255) | NO |  |  | 邮件主题 |
| content | text | NO |  |  | 邮件正文 |
| tos | text | YES |  |  | 邮件主送 |
| ccs | text | YES |  |  | 邮件抄送 |
| bccs | text | YES |  |  | 邮件密送 |
| actualSendAddress | text | YES |  |  | 实际邮件发送地址 |
| attachFiles | text | YES |  |  | 邮件附件 以特殊符号间隔多个文件 |
| isInner | bit(1) | YES | b'0' |  | 是否为内部邮箱 |
| sendTime | datetime | YES |  |  | 邮件实际发送时间 |
| expectSendTime | datetime | YES |  |  | 邮件期望发送时间 |
| sendFlag | bit(1) | YES | b'0' |  | 邮件是否发送 1 为已发送 |
| failedCount | int(2) | YES | 0 |  | 发送失败次数 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### t_menu

**业务含义**：（待补充）

**数据量**：约 37 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 用户菜单定义（自增） |
| pid | int(11) | NO | 0 |  | 父菜单ID |
| name | varchar(100) | YES |  |  | 菜单名称 |
| url | varchar(100) | YES |  |  | 超链接 |
| icon | varchar(64) | YES |  |  | 菜单对应的class样式，会影响菜单的显示效果 |
| sort | int(11) | YES | 0 |  | 子菜单排序 |
| status | bit(1) | YES | b'1' |  | 是否有效，1：有效，0：失效 |
| target | varchar(15) | YES |  |  | 指标 |
| remark | varchar(255) | YES |  |  | 备注说明 |
| create_by | varchar(25) | YES |  |  | 操作人 |
| crate_time | datetime | YES |  |  | 时间 |
| update_by | varchar(25) | YES |  |  | 操作人 |
| update_time | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### t_notify_template

**业务含义**：（待补充）

**数据量**：约 11 行 | 数据大小：0.1 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| templateCode | varchar(64) | YES |  | UNI | 编码 |
| subject | varchar(64) | YES |  |  | 主题 |
| content | text | YES |  |  | 内容 |
| createBy | varchar(45) | YES |  |  | 操作人 |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |
| updateBy | varchar(45) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |
| effectiveFrom | datetime | NO | CURRENT_TIMESTAMP |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| templateCode | templateCode | 是 | BTREE |

---

### t_permission

**业务含义**：（待补充）

**数据量**：约 115 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| permission_id | int(11) | NO |  | PRI | 权限ID（自增） |
| permission_name | varchar(100) | YES |  | MUL | 权限字符串 |
| create_by | varchar(25) | YES |  |  | 操作人 |
| create_time | datetime | YES |  |  | 时间 |
| update_by | varchar(25) | YES |  |  | 操作人 |
| update_time | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| permission_name | permission_name | 否 | BTREE |
| PRIMARY | permission_id | 是 | BTREE |

---

### t_resource

**业务含义**：（待补充）

**数据量**：约 36 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 系统资源需要的权限定义（自增） |
| url | varchar(100) | YES |  |  | 资源请求地址 |
| authc | varchar(255) | YES |  |  | 需要的权限控制 ， 类似于 authc,roles[admin],perms[admin:create] |
| priority | int(11) | YES | 0 |  | 访问资源权限排序，越低越往后排 |
| remark | varchar(255) | YES |  |  | 关于资源定义的备注说明 |
| status | int(11) | YES | 1 |  | 数据有效性0 失效 1 有效 |
| create_by | varchar(25) | YES |  |  | 操作人 |
| create_time | datetime | YES |  |  | 时间 |
| update_by | varchar(25) | YES |  |  | 操作人 |
| update_time | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### t_role

**业务含义**：角色表

**数据量**：约 12 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| role_id | int(11) | NO |  | PRI | 角色ID（自增） |
| role_name | varchar(100) | YES |  | MUL | 角色名称 |
| role_name_zn | varchar(100) | YES |  |  | 中文别名 |
| home_page | varchar(100) | YES |  |  | 角色默认主页 |
| priority | int(11) | YES | 100 |  | 角色优先级，默认100，优先级最高 |
| status | smallint(1) | YES | 1 |  | 角色有效性，1有效，0无效 |
| create_by | varchar(25) | YES |  |  | 操作人 |
| create_time | datetime | YES |  |  | 时间 |
| update_by | varchar(25) | YES |  |  | 操作人 |
| update_time | datetime | YES |  |  | 时间 |
| remark | varchar(255) | YES |  |  | 备注说明 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | role_id | 是 | BTREE |
| role_name | role_name | 否 | BTREE |

---

### t_role_menu

**业务含义**：（待补充）

**数据量**：约 122 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 角色菜单权限（自增） |
| role_id | int(11) | YES |  |  | 角色ID |
| menu_id | int(11) | YES |  |  | 菜单ID |
| create_time | datetime | YES |  |  | 创建时间 |
| create_by | varchar(25) | YES |  |  | 创建用户 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### t_role_permission

**业务含义**：（待补充）

**数据量**：约 613 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 角色-权限 一对多（自增） |
| role_id | int(11) | YES |  | MUL | 角色ID |
| permission_id | int(11) | YES |  | MUL | 权限ID |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| permission_id | permission_id | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| role_id | role_id, permission_id | 是 | BTREE |

---

### t_sync_log

**业务含义**：（待补充）

**数据量**：约 7,023 行 | 数据大小：2.5 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| targetMethod | varchar(255) | YES |  |  | 同步触发方法 |
| tableObject | varchar(64) | NO |  |  | 同步的表实体 |
| dataFrom | varchar(50) | YES |  |  | 同步数据源 |
| dataTo | varchar(50) | YES |  |  | 同步目标数据源 |
| syncParams | varchar(2048) | YES |  |  | 增量同步时的参数 |
| syncStartTime | datetime | YES |  |  | 同步开始时间 |
| syncEndTime | datetime | YES | CURRENT_TIMESTAMP |  | 同步结束时间 |
| isSuccess | tinyint(1) | NO | 0 |  | 同步成功与否 |
| dataCount | int(11) | YES | 0 |  | 同步记录数 |
| exception | text | YES |  |  | 同步失败异常信息 |
| syncType | smallint(1) | NO | 0 |  | 0：复制，1：全量，2：增量 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### t_sync_state

**业务含义**：（待补充）

**数据量**：约 5 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| tableObject | varchar(25) | NO |  | UNI | 表的对象 |
| lastId | varchar(25) | NO |  |  | 上一次同步的最后一个主键 |
| lastSyncTime | datetime | YES |  |  | 上一次同步时间 |
| offset | int(11) | NO | 0 |  | 上一次同步的记录数 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| tableObject | tableObject | 是 | BTREE |

---

### t_sys_log

**业务含义**：（待补充）

**数据量**：约 61,733 行 | 数据大小：354.2 MB | 索引大小：3.8 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(32) unsigned | NO |  | PRI | ID标识（自增） |
| description | varchar(8000) | YES |  | MUL | 日志描述 |
| method | varchar(200) | YES |  |  | 调用的方法 |
| type | varchar(25) | YES |  | MUL | 0-正常日志，1-异常 |
| request_ip | varchar(256) | YES |  |  | 请求者IP |
| exception_code | varchar(256) | YES |  |  | 异常编码 |
| exception_detail | mediumtext | YES |  |  | 异常详情 |
| params | mediumtext | YES |  |  | 参数（json格式） |
| create_by | varchar(256) | YES |  | MUL | 操作人 |
| create_date | datetime | YES |  | MUL | 操作日期 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| create_by | create_by | 否 | BTREE |
| create_date | create_date, description | 否 | BTREE |
| description | description | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| type | type, create_by | 否 | BTREE |

---

### t_sys_variable

**业务含义**：（待补充）

**数据量**：约 51 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | MUL | ID标识（自增） |
| code | varchar(64) | NO |  | PRI | 系统参数编码 |
| var | varchar(4096) | YES |  |  | 系统参数值 |
| remark | varchar(255) | YES |  |  | 备注 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES | CURRENT_TIMESTAMP |  | 时间 |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| id | id | 否 | BTREE |
| PRIMARY | code | 是 | BTREE |

---

### t_user

**业务含义**：用户认证表

**数据量**：约 189 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| user_id | int(11) | NO |  | PRI | 用户ID（自增） |
| user_name | varchar(25) | YES |  | UNI | 用户名称 |
| password | varchar(100) | YES |  |  | 密码 |
| create_by | varchar(25) | YES |  |  | 操作人 |
| create_time | datetime | NO | CURRENT_TIMESTAMP |  | 时间 |
| update_by | varchar(25) | YES |  |  | 操作人 |
| update_time | datetime | YES |  |  | 时间 |
| status | smallint(1) | NO | 1 |  | 用户状态，0：失效，1有效，2：锁定 |
| needChangePwd | bit(1) | NO | b'1' |  | 用户创建后需要修改密码判断 |
| loginErrorCount | int(1) | NO | 0 |  | 用户密码输入错误次数 |
| isSysUser | smallint(1) | NO | 0 |  | 是否为系统用户,0为普通用户 |
| userCustom1 | varchar(50) | YES |  |  | 用户自定义字段1 |
| userCustom2 | varchar(50) | YES |  |  | 用户自定义字段2 |
| userCustom3 | varchar(50) | YES |  |  | 用户自定义字段3 |
| userCustom4 | int(11) | YES | 0 |  | 用户自定义字段4 |
| userCustom5 | int(11) | YES | 0 |  | 用户自定义字段5 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | user_id | 是 | BTREE |
| unique_username | user_name | 是 | BTREE |

---

### t_user_info

**业务含义**：用户详细信息表

**数据量**：约 189 行 | 数据大小：0.1 MB | 索引大小：0.1 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 员工ID，外键（自增） |
| workNo | varchar(25) | NO |  | MUL | 工号 |
| realName | varchar(50) | NO |  |  | 姓名 |
| eName | varchar(50) | YES |  |  | 英文名 |
| compID | int(11) | NO | 0 | MUL | 公司ID |
| depID | int(11) | NO | 0 | MUL | 部门ID |
| jobID | int(11) | NO | 0 | MUL | 岗位ID |
| reportTo | int(11) | YES |  | MUL | 直接上级 |
| wfreportTo | int(11) | YES |  | MUL | 职能上级 |
| empStatus | int(11) | NO | 1 |  | 员工状态，1：在职，2：离职 |
| jobStatus | int(11) | YES |  |  | 岗位状态 |
| empType | int(11) | YES |  |  | 聘用类型：1：正式，3：实习生 |
| sex | smallint(1) | YES |  |  | 性别：1：男，0：女 |
| birthday | date | YES |  |  | 生日 |
| email | varchar(50) | YES |  |  | 邮箱 |
| mobile | varchar(50) | YES |  |  | 手机 |
| telphone | varchar(50) | YES |  |  | 座机 |
| avatar | varchar(500) | YES |  |  | 头像 |
| remark | varchar(100) | YES |  |  | 备注 |
| state | int(11) | YES | 1 |  | 状态 |
| user_id | int(11) | YES |  | MUL | userId |
| custom1 | int(11) | YES |  |  | 预留字段1 |
| custom2 | int(11) | YES |  |  | 预留字段2 |
| custom3 | varchar(50) | YES |  |  | 预留字段3 officeCode |
| custom4 | varchar(50) | YES |  |  | 预留字段4 projectTypes |
| custom5 | varchar(4096) | YES |  |  | 预留字段5 areaPower |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| compID | compID | 否 | BTREE |
| depID | depID | 否 | BTREE |
| fk_userInfo_userId | user_id, compID | 是 | BTREE |
| jobID | jobID | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |
| reportTo | reportTo | 否 | BTREE |
| wfreportTo | wfreportTo | 否 | BTREE |
| workNo | workNo | 否 | BTREE |

**外键关系**：

- `user_id` → `t_user.user_id`（约束名：fk_userInfo_userId）

---

### t_user_login_record

**业务含义**：（待补充）

**数据量**：约 18,952 行 | 数据大小：1.5 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| loginName | varchar(64) | YES |  |  | 登录用户名 |
| loginTime | datetime | NO | CURRENT_TIMESTAMP |  | 登录时间 |
| loginIP | varchar(64) | YES |  |  | 登录IP |
| logoutTime | datetime | YES |  |  | 登出时间 |
| logoutIP | varchar(64) | YES |  |  | 登出IP |
| loginSuccess | tinyint(1) | NO | 0 |  | 登录状态 |
| logoutSuccess | tinyint(1) | YES |  |  | 登出状态 |
| userId | int(11) | YES |  |  | user表id |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### t_user_role

**业务含义**：用户角色关联表

**数据量**：约 551 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | 用户-角色  一对多（自增） |
| user_id | int(11) | NO |  | MUL | 用户ID |
| role_id | int(11) | NO |  | MUL | 角色ID |
| comp_id | int(11) | YES |  |  | 公司ID |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| t_user_role_ibfk_2 | role_id | 否 | BTREE |
| unique_userId_roleId | user_id, role_id, comp_id | 是 | BTREE |
| user_id | user_id | 否 | BTREE |

**外键关系**：

- `user_id` → `t_user.user_id`（约束名：t_user_role_ibfk_1）
- `role_id` → `t_role.role_id`（约束名：t_user_role_ibfk_2）

---

### tain_type

**业务含义**：（待补充）

**数据量**：约 3 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| tain | varchar(10) | NO |  | MUL | 培训标识 |
| id | int(11) | NO |  | PRI | ID标识（自增） |
| tain_type | varchar(50) | YES |  |  | tain类型 |
| remark | text | YES |  |  | 备注 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| tain_where_index | tain | 否 | BTREE |

---

### tb_sys_log

**业务含义**：系统日志表

**数据量**：约 3,182,907 行 | 数据大小：164.2 MB | 索引大小：45.5 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| ID | int(32) unsigned | NO |  | PRI | ID标识（自增） |
| USER_NAME | varchar(80) | NO |  | MUL | 名称 |
| IP | char(20) | NO | 0 |  | IP地址 |
| ACTION | varchar(1024) | NO |  |  | 操作 |
| RESULT | int(32) unsigned | NO | 0 |  | 结果 |
| INFO | varchar(20000) | NO |  |  | 信息 |
| TIME | int(32) | NO |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | ID | 是 | BTREE |
| USER_NAME | USER_NAME | 否 | BTREE |

---

### transnum

**业务含义**：（待补充）

**数据量**：约 0 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| transNum | int(50) | YES |  |  | trans数量 |

---

### tx_info

**业务含义**：（待补充）

**数据量**：约 60,939 行 | 数据大小：7.5 MB | 索引大小：2.5 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| tx_id | int(11) | NO |  | PRI | ID标识（自增） |
| sheetID | varchar(10) | YES |  | MUL | 单据代码 |
| tx_type | int(1) | YES |  |  | 单据类型(0:RMA单据;1:借用申请 2：转移) |
| spare_serialNum | varchar(50) | YES |  |  | 备件序列号 |
| sendout_place | char(1) | YES |  |  | 历史记录（1：供应链；2：库存） |
| sendout_whsCode | varchar(10) | YES |  |  | 备件发出库房 |
| send_time | datetime | YES |  |  | 出库时间 |
| receving_place | varchar(50) | YES |  |  | 备件接受地 |
| receving_whsCode | varchar(10) | YES |  |  | 备件接收库房 |
| receive_time | datetime | YES |  |  | 收货时间 |
| quantity | int(11) | YES | 1 |  | 数量 |
| EMS_num | varchar(255) | YES |  |  | 快递单号 |
| EMS_company | varchar(255) | YES |  |  | 快递公司 |
| addressee | varchar(25) | YES |  |  | 收件人 |
| isRMA | char(1) | YES | 0 |  | 是否是RMA的坏件返回（1：是;0:好件） |
| version_no | int(11) | YES | 0 |  | 版本号  -1时为历史选择的数据 |
| detail_id | int(11) | YES |  |  | 库存表中的id |
| instead_of_num | varchar(25) | YES |  |  | 好件替换坏件关系 |
| shiftimes | int(11) | YES |  |  | 备件经过转移次数 |
| turnovertimes | int(11) | YES |  |  | 周转次数 |
| allottimes | int(11) | YES |  |  | 分配次数 |
| instead_time | datetime | YES |  |  | 时间 |
| datastate | int(1) | YES | 1 |  | 保持历史数据有效性 0 失效 1 有效 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | tx_id | 是 | BTREE |
| sheetID | sheetID | 否 | BTREE |

---

### user

**业务含义**：（待补充）

**数据量**：约 72 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(8) | NO |  | PRI | ID标识（自增） |
| username | varchar(20) | YES |  | UNI | 工号 |
| password | varchar(32) | YES |  |  | 密码 |
| role | int(1) | YES |  |  | 4：超级管理员；3管理员；1：普通用户 |
| mail | varchar(100) | YES |  |  | 邮箱 |
| lastLogin | datetime | YES |  |  | 上次登陆时间 |
| department | varchar(50) | YES |  |  | 部门 |
| name | varchar(50) | YES |  |  | 姓名 |
| tel | varchar(50) | YES |  |  | 电话 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| username | username | 是 | BTREE |

---

### user_info

**业务含义**：（待补充）

**数据量**：约 190 行 | 数据大小：0.1 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(8) | NO |  | PRI | ID标识（自增） |
| username | varchar(20) | YES |  | UNI | 工号 |
| password | varchar(32) | YES |  |  | 密码 |
| role | int(1) | YES |  |  | 1：普通用户；2：07库；3：技服；4：管理员 5：供应链 |
| mail | varchar(100) | YES |  |  | 邮箱 |
| lastLogin | datetime | YES |  |  | 上次登陆时间 |
| department | varchar(50) | YES |  |  | 部门 |
| realname | varchar(50) | YES |  |  | 姓名 |
| tel | varchar(50) | YES |  |  | 手机 |
| state | int(1) | YES | 1 |  | 用户有效性 |
| title | varchar(25) | YES |  |  | 职称 |
| office | varchar(25) | YES |  |  | 所在区域办事处 |
| office_addr | text | YES |  |  | 办事处地址 |
| guhua | varchar(15) | YES |  |  | 固话 |
| fax | varchar(25) | YES |  |  | 传真 |
| whs_code | varchar(255) | YES |  |  | 库房信息 |
| pwd_over_due_date | datetime | YES |  |  | 密码修改记录 |
| teams | varchar(255) | YES |  |  | 所属团队 |
| teamRole | varchar(255) | YES |  |  | 团队角色 |
| province | varchar(255) | YES |  |  | 省份/直辖市 |
| city | varchar(255) | YES |  |  | 市 |
| district | varchar(255) | YES |  |  | 区 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |
| username | username | 是 | BTREE |

---

### user_modules

**业务含义**：（待补充）

**数据量**：约 23 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| menuCode | varchar(50) | YES |  |  | 编码 |
| menuName | varchar(50) | YES |  |  | 名称 |
| menuLevel | int(11) | YES |  |  | menuLevel |
| superId | int(11) | YES |  |  | 父菜单ID |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |
| createBy | varchar(25) | YES |  |  | 操作人 |
| createTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### user_permissions

**业务含义**：（待补充）

**数据量**：约 2,744 行 | 数据大小：0.2 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| username | varchar(10) | YES |  |  | 名称 |
| permissionKey | varchar(50) | YES |  |  | permissionKey |
| permissionValue | int(1) | YES |  |  | permissionValue |
| menuName | varchar(50) | YES |  |  | 名称 |
| menuLevel | int(1) | YES |  |  | menuLevel |
| effectiveFrom | datetime | YES |  |  | 生效时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |
| createdBy | varchar(25) | YES |  |  | 操作人 |
| createdTime | datetime | YES |  |  | 时间 |
| updateBy | varchar(25) | YES |  |  | 操作人 |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### user_team

**业务含义**：（待补充）

**数据量**：约 34 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| realname | varchar(255) | YES |  |  | 名称 |
| teamRole | varchar(255) | YES |  |  | teamRole |
| mail | varchar(255) | YES |  |  | 邮箱 |
| tel | varchar(255) | YES |  |  | 电话 |
| department | varchar(255) | YES |  |  | 部门 |
| province | varchar(255) | YES |  |  | 省份 |
| addr | varchar(255) | YES |  |  | 地址 |

---

### view_contract_collection_plan_4_crm

**对象类型**：VIEW

**业务含义**：合同回款计划视图（供CRM系统使用）

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| contractNo | varchar(45) | YES |  |  | 合同号 |
| referenceEventName | varchar(255) | YES |  |  | referenceEvent名称 |
| referenceEvent | varchar(255) | YES |  |  | 字段属性1 |
| eventPlanHappenDate | datetime | YES |  |  | 款项计划发生日期 |
| eventPlanHappenDateENG | datetime | YES |  |  | 工程计划发生日期 |
| eventActualFinishDate | datetime | YES |  |  | 实际完成日期 |

---

### view_current_task

**对象类型**：VIEW

**业务含义**：当前任务视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| taskTypeId | varchar(25) | YES |  |  | 任务类型id，关联基础数据表 |
| projectId | int(11) | YES |  |  | 项目ID |
| taskTypeCode | varchar(45) | YES |  |  | 任务类型code，关联基础数据表 |

---

### view_distinct_contract

**对象类型**：VIEW

**业务含义**：去重合同视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| contractNo | varchar(50) | YES |  |  | 合同编号 |
| orderCreateTime | datetime | YES |  |  | 排序Create时间 |
| id | int(11) | YES |  |  | ID标识 |

---

### view_ehr_department

**对象类型**：VIEW

**业务含义**：EHR部门信息视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| depID | int(11) | NO |  |  | 部门ID，关联外键 |
| depCode | varchar(20) | YES |  |  | 部门编码 |
| depName | varchar(100) | YES |  |  | 部门名称 |
| depAbbr | varchar(100) | YES |  |  | 部门简称 |
| compID | int(11) | YES |  |  | 公司ID，外键 |
| adminID | int(11) | YES |  |  | 上级ID |
| depGrade | int(11) | YES |  |  | 部门级别 |
| depType | int(11) | YES |  |  | 部门类型 |
| depProperty | int(11) | YES |  |  | 部门属性 |
| depCost | int(11) | YES |  |  | 存在部门内分级计数用 |
| director | int(11) | YES |  |  | 主管 |
| director2 | int(11) | YES |  |  | 分管领导 |
| depEmp | int(11) | YES |  |  | 部门人数 |
| depNum | int(11) | YES |  |  | 部门编号 |
| effectDate | datetime | YES |  |  | 生效时间 |
| xOrder | varchar(20) | YES |  |  | 排序 |
| isDisabled | bit(1) | YES |  |  | 失效状态 |
| disabledDate | datetime | YES |  |  | 失效时间 |
| remark | varchar(500) | YES |  |  | 备注 |
| depCustom1 | int(11) | YES |  |  | 保留字段1 |
| depCustom2 | int(11) | YES |  |  | 保留字段2、部门秘书 |
| depCustom3 | int(11) | YES |  |  | 保留字段3 |
| depCustom4 | int(11) | YES |  |  | 保留字段4 |
| depCustom5 | int(11) | YES |  |  | 保留字段5 |
| directorWorkNo | varchar(100) | YES |  |  | 工号 |
| directorName | varchar(200) | YES |  |  | 姓名 |
| directorJobID | int(11) | YES |  |  | 岗位ID |
| directorWorkNo2 | varchar(100) | YES |  |  | 工号 |
| directorName2 | varchar(200) | YES |  |  | 姓名 |
| directorJobID2 | int(11) | YES |  |  | 岗位ID |
| depLV1ID | bigint(11) | YES |  |  | 部门ID |
| depLV1Code | varchar(20) | YES |  |  | 部门编码 |
| depLV1Name | varchar(100) | YES |  |  | 部门名称 |
| depLV2ID | bigint(11) | YES |  |  | 部门ID |
| depLV2Code | varchar(20) | YES |  |  | 部门编码 |
| depLV2Name | varchar(100) | YES |  |  | 部门名称 |
| depLV3ID | bigint(11) | YES |  |  | 部门ID |
| depLV3Code | varchar(20) | YES |  |  | 部门编码 |
| depLV3Name | varchar(100) | YES |  |  | 部门名称 |
| depAllName | varchar(304) | YES |  |  | 部门名称 |
| compName | varchar(100) | YES |  |  | 公司名称 |

---

### view_ehr_department_struct

**对象类型**：VIEW

**业务含义**：EHR部门层级结构视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| depID | int(11) | NO |  |  | 部门ID，关联外键 |
| depGrade | int(11) | YES |  |  | 部门级别 |
| depLV1ID | bigint(11) | YES |  |  | 部门ID |
| depLV1Code | varchar(20) | YES |  |  | 部门编码 |
| depLV1Name | varchar(100) | YES |  |  | 部门名称 |
| depLV2ID | bigint(11) | YES |  |  | 部门ID |
| depLV2Code | varchar(20) | YES |  |  | 部门编码 |
| depLV2Name | varchar(100) | YES |  |  | 部门名称 |
| depLV3ID | bigint(11) | YES |  |  | 部门ID |
| depLV3Code | varchar(20) | YES |  |  | 部门编码 |
| depLV3Name | varchar(100) | YES |  |  | 部门名称 |
| depLV4ID | bigint(11) | YES |  |  | 部门ID |
| depLV4Code | varchar(20) | YES |  |  | 部门编码 |
| depLV4Name | varchar(100) | YES |  |  | 部门名称 |
| depAllName | varchar(406) | YES |  |  | 部门名称 |

---

### view_ehr_employee

**对象类型**：VIEW

**业务含义**：EHR员工信息视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| empID | int(11) | NO |  |  | 员工ID，外键 |
| workNo | varchar(100) | NO |  |  | 工号 |
| name | varchar(200) | YES |  |  | 姓名 |
| eName | varchar(200) | YES |  |  | 英文名 |
| compID | int(11) | NO |  |  | 公司ID |
| depID | int(11) | NO |  |  | 部门ID |
| jobID | int(11) | NO |  |  | 岗位ID |
| reportTo | int(11) | YES |  |  | 直接上级 |
| wfreportTo | int(11) | YES |  |  | 职能上级 |
| empStatus | int(11) | NO |  |  | 员工状态，1：在职，2：离职 |
| jobStatus | int(11) | YES |  |  | 岗位状态 |
| empType | int(11) | YES |  |  | 聘用类型：1：正式，3：实习生 |
| joinDate | datetime | YES |  |  | 加入公司日期 |
| workBeginDate | datetime | YES |  |  | 工作开始日期 |
| jobBeginDate | datetime | YES |  |  | 加入公司日期（未知） |
| pracBeginDate | datetime | YES |  |  | 实习开始时间 |
| pracEndDate | datetime | YES |  |  | 实习结束时间 |
| probBeginDate | datetime | YES |  |  | 试用期开始日期 |
| probEndDate | datetime | YES |  |  | 试用期结束日期 |
| leaveDate | datetime | YES |  |  | 离职时间 |
| gender | int(11) | YES |  |  | 性别：1：男，2：女 |
| email | varchar(500) | YES |  |  | 邮箱 |
| mobile | varchar(50) | YES |  |  | 手机 |
| officePhone | varchar(50) | YES |  |  | 座机 |
| remark | varchar(100) | YES |  |  | 备注 |
| disabled | int(11) | YES | 0 |  | 失效 |
| empCustom1 | int(11) | YES |  |  | 预留字段1 |
| empCustom2 | int(11) | YES |  |  | 预留字段2 |
| empCustom3 | int(11) | YES |  |  | 预留字段3 |
| empCustom4 | varchar(50) | YES |  |  | 预留字段4 |
| empCustom5 | int(11) | YES |  |  | 预留字段5 |
| reportToWorkNo | varchar(100) | YES |  |  | 工号 |
| reportToName | varchar(200) | YES |  |  | 姓名 |
| wfreportToWorkNo | varchar(100) | YES |  |  | 工号 |
| wfreportToName | varchar(200) | YES |  |  | 姓名 |
| depGrade | int(11) | YES |  |  | 部门级别 |
| depCode | varchar(20) | YES |  |  | 部门编码 |
| depName | varchar(100) | YES |  |  | 部门名称 |
| jobCode | varchar(10) | YES |  |  | 岗位编码 |
| jobName | varchar(100) | YES |  |  | 岗位名称 |
| depLV1ID | bigint(11) | YES |  |  | 部门ID |
| depLV1Code | varchar(20) | YES |  |  | 部门编码 |
| depLV1Name | varchar(100) | YES |  |  | 部门名称 |
| depLV2ID | bigint(11) | YES |  |  | 部门ID |
| depLV2Code | varchar(20) | YES |  |  | 部门编码 |
| depLV2Name | varchar(100) | YES |  |  | 部门名称 |
| depLV3ID | bigint(11) | YES |  |  | 部门ID |
| depLV3Code | varchar(20) | YES |  |  | 部门编码 |
| depLV3Name | varchar(100) | YES |  |  | 部门名称 |
| depAllName | varchar(304) | YES |  |  | 部门名称 |
| compName | varchar(100) | YES |  |  | 公司名称 |

---

### view_ems_info_4_pm

**对象类型**：VIEW

**业务含义**：EMS快递信息视图（供PMS使用）

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| contract_code | varchar(25) | YES |  |  | 合同编码 |
| emsNum | mediumtext | YES |  |  | ems数量 |
| receiveName | mediumtext | YES |  |  | receive名称 |
| emsCompany | mediumtext | YES |  |  | emsCompany |

---

### view_pm_deliverable_4_sms

**对象类型**：VIEW

**业务含义**：项目交付物视图（供SMS系统使用）

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| contractNo | varchar(25) | YES |  |  | 合同编号 |
| deliverableName | varchar(255) | YES |  |  | 交付件名称 |
| deliverablePath | varchar(255) | YES |  |  | 交付件路径 |
| smsDeliverType | varchar(255) | YES |  |  | smsDeliver类型 |
| uploadUser | varchar(174) | YES |  |  | uploadUser |
| uploadTime | datetime | YES |  |  | 上传时间 |

---

### view_presales_project_duration

**对象类型**：VIEW

**业务含义**：售前项目周期视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| presalesId | int(11) | NO | 0 |  | 售前项目主表 |
| instId | varchar(64) | YES |  |  | activity工作流流程ID |
| applyDuration | varchar(128) | YES |  |  | applyDuration |
| totalDuration | varchar(216) | YES |  |  | totalDuration |
| serviceDuration | varchar(216) | YES |  |  | serviceDuration |
| programDuration | varchar(216) | YES |  |  | programDuration |
| testDuration | varchar(216) | YES |  |  | testDuration |
| callbackDuration | varchar(216) | YES |  |  | callbackDuration |
| serviceApproveDuration | varchar(216) | YES |  |  | serviceApproveDuration |

---

### view_prj_is_has_plan

**对象类型**：VIEW

**业务含义**：项目是否有计划视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| projectId | int(11) | YES |  |  | 项目ID |

---

### view_project_created_list

**对象类型**：VIEW

**业务含义**：已创建项目列表视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| projectId | int(11) | NO | 0 |  | 项目头信息主键,跟项目其他具体信息关联 |
| projectCode | varchar(45) | NO |  |  | 项目名称 |
| rank | varchar(255) | YES |  |  | 职级 |
| projectName | varchar(246) | YES |  |  | 项目名称 |
| projectStateName | varchar(255) | YES |  |  | projectState名称 |
| projectState | varchar(11) | YES |  |  | 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态 |
| contractNo | text | YES |  |  | 合同编号 |
| officeCode | varchar(255) | YES |  |  | 办事处编码 |
| officeName | varchar(20) | YES |  |  | 办公名称 |
| salesManCode | varchar(45) | YES |  |  | 人员编码,外部人员为空 |
| salesManName | varchar(45) | YES |  |  | 人员名称 |
| orderCreateTime | datetime | YES |  |  | 排序Create时间 |
| serviceManager | varchar(45) | YES |  |  | 人员编码,外部人员为空 |
| serviceManagerName | varchar(45) | YES |  |  | 人员名称 |
| projectManager | varchar(45) | YES |  |  | 人员编码,外部人员为空 |
| projectManagerName | varchar(45) | YES |  |  | 人员名称 |
| currentTask | varchar(255) | YES |  |  | currentTask |

---

### view_project_info_4_ts

**对象类型**：VIEW

**业务含义**：项目信息视图（供TS系统使用）

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| projectCode | varchar(64) | YES |  |  | 项目编码 |
| projectName | varchar(255) | YES |  |  | 项目名称 |
| contractNo | varchar(341) | YES |  |  | 合同编号 |
| officeName | varchar(20) | YES |  |  | 办公名称 |
| customerName | varchar(255) | YES |  |  | 客户名称 |
| marketName | varchar(255) | YES |  |  | market名称 |
| systemName | varchar(255) | YES |  |  | system名称 |
| expendName | varchar(255) | YES |  |  | expend名称 |
| industryName | varchar(255) | YES |  |  | industry名称 |
| salesManCode | varchar(45) | YES |  |  | salesMan编码 |
| salesManName | varchar(68) | YES |  |  | salesMan名称 |
| salesManTel | varchar(45) | YES |  |  | salesManTel |
| salesManMail | varchar(100) | YES |  |  | salesManMail |
| smCode | varchar(45) | YES |  |  | sm编码 |
| smName | varchar(45) | YES |  |  | sm名称 |
| pmCode1 | varchar(45) | YES |  |  | pm编码 |
| pmName1 | varchar(45) | YES |  |  | pm名称 |
| pmCode2 | varchar(45) | YES |  |  | pm编码 |
| pmName2 | varchar(45) | YES |  |  | pm名称 |
| compId | int(11) | YES |  |  | 公司ID |
| compName | varchar(128) | YES |  |  | 公司名称 |
| ssfsName | varchar(255) | YES |  |  | ssfs名称 |
| partnerChannel | varchar(45) | YES |  |  | partnerChannel |
| projectType | varchar(4) | NO |  |  | project类型 |
| finalCustomerName | varchar(255) | YES |  |  | finalCustomer名称 |
| customerProjectName | varchar(255) | YES |  |  | customerProject名称 |

---

### view_project_info_list

**对象类型**：VIEW

**业务含义**：项目信息列表视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| projectId | int(11) | YES |  |  | 项目ID |
| projectCode | varchar(45) | YES |  |  | 项目编码 |
| rank | varchar(255) | YES |  |  | 职级 |
| projectName | varchar(255) | YES |  |  | 项目名称 |
| projectStateName | varchar(255) | YES |  |  | projectState名称 |
| projectState | varchar(255) | YES |  |  | projectState |
| contractNo | varchar(341) | YES |  |  | 合同编号 |
| officeCode | varchar(255) | YES |  |  | 办公编码 |
| officeName | varchar(20) | YES |  |  | 办公名称 |
| salesManCode | varchar(45) | YES |  |  | salesMan编码 |
| salesManName | varchar(45) | YES |  |  | salesMan名称 |
| orderCreateTime | datetime | YES |  |  | 排序Create时间 |
| systemName | varchar(255) | YES |  |  | system名称 |
| serviceManager | varchar(45) | YES |  |  | serviceManager |
| serviceManagerName | varchar(45) | YES |  |  | serviceManager名称 |
| projectManager | varchar(45) | YES |  |  | projectManager |
| projectManagerName | varchar(45) | YES |  |  | projectManager名称 |
| currentTask | varchar(255) | YES |  |  | currentTask |

---

### view_project_maintenance_4_ts

**对象类型**：VIEW

**业务含义**：项目维保信息视图（供TS系统使用）

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO | 0 |  | ID标识 |
| projectId | int(11) | NO |  |  | 项目头信息主键 |
| projectCode | varchar(45) | NO |  |  | 项目名称 |
| projectName | varchar(200) | YES |  |  | 项目名称 |
| projectType | int(11) | NO | 10 |  | 项目类型，售前:20/售后:10 |
| projectExecutionState | varchar(45) | YES |  |  | 项目实施状态 |
| contractNo | varchar(255) | YES |  |  | 合同号 |
| officeCode | varchar(25) | YES |  |  | 办事处编码 |
| type | varchar(45) | YES |  |  | 任务性质 |
| category | varchar(45) | YES |  |  | 任务分类 |
| subCategory | varchar(45) | YES |  |  | 任务小类 |
| processTime | datetime | YES |  |  | 处理时间 |
| processDesc | varchar(1024) | YES |  |  | 事项描述 |
| processStep | varchar(1024) | YES |  |  | 解决进展 |
| remainProblem | varchar(1024) | YES |  |  | 遗留问题 |
| transitHour | float | YES | 0 |  | 在途耗时(h) |
| processHour | float | YES | 0 |  | 处理耗时(h) |
| itemModel | varchar(255) | YES |  |  | 产品型号 |
| softVersion | varchar(255) | YES |  |  | 在网版本 |
| enabledFeatures | varchar(255) | YES |  |  | 启用功能 |
| customTos | varchar(512) | YES |  |  | 自定义主送 |
| customCcs | varchar(512) | YES |  |  | 自定义抄送 |
| hasReport | bit(1) | NO | b'0' |  | 是否有巡检报告 |
| quesnaireId | int(11) | YES |  |  | 问卷ID |
| deliverFileIds | varchar(255) | YES |  |  | 交付件，fnd_files id |
| warrantyStatus | varchar(25) | YES |  |  | 维保状态 |
| industryName | varchar(25) | YES |  |  | 行业 |
| userOffice | varchar(25) | YES |  |  | 用户办事处 |
| remark | varchar(2048) | YES |  |  | 备注 |
| createTime | datetime | YES |  |  | 创建时间 |
| createBy | varchar(45) | YES |  |  | 创建用户 |
| updateTime | datetime | YES |  |  | 最新更新时间 |
| updateBy | varchar(45) | YES |  |  | 最新更新用户 |
| officeName | varchar(20) | YES |  |  | 办公名称 |
| userOfficeName | varchar(20) | YES |  |  | user办公名称 |
| serviceManager | varchar(45) | YES |  |  | 人员名称 |
| programManagerA | varchar(45) | YES |  |  | 人员名称 |
| programManagerB | varchar(45) | YES |  |  | 人员名称 |
| createUser | varchar(174) | YES |  |  | createUser |
| typeName | varchar(255) | YES |  |  | 类型名称 |
| projectExecutionStateName | varchar(255) | YES |  |  | projectExecutionState名称 |
| categoryName | varchar(258) | YES |  |  | category名称 |
| subCategoryName | varchar(255) | YES |  |  | subCategory名称 |
| marketName | varchar(255) | YES |  |  | market名称 |
| systemName | varchar(255) | YES |  |  | system名称 |
| expendName | varchar(255) | YES |  |  | expend名称 |
| industryNameN | varchar(255) | YES |  |  | industry名称N |
| finalCustomerName | varchar(255) | YES |  |  | 最终客户名称 |
| salerName | varchar(91) | YES |  |  | saler名称 |
| quesnaireResultHeaderId | int(11) | YES |  |  | 回访结果头信息Id |
| 工程师技术能力 | longtext | YES |  |  | 工程师技术能力 |
| 服务水平及规范性 | longtext | YES |  |  | 服务水平及规范性 |
| 服务及时性 | longtext | YES |  |  | 服务及时性 |
| warrantyStatusName | varchar(4) | YES |  |  | warranty状态名称 |
| syncTime | datetime | NO |  |  | sync时间 |

---

### view_project_shipment_4_license

**对象类型**：VIEW

**业务含义**：项目发货授权视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| barcode | varchar(50) | YES |  |  | 条码 |
| contract_code | varchar(25) | YES |  |  | 合同编码 |
| contract_type | varchar(25) | YES |  |  | 合同类型 |
| project_num | varchar(25) | YES |  |  | project数量 |
| project_name | varchar(512) | YES |  |  | 项目名称 |
| custom_name | varchar(512) | YES |  |  | 习俗名称 |
| final_customer | varchar(512) | YES |  |  | finalcustomer |
| office_name | varchar(25) | YES |  |  | office名称 |
| delivery_time | bigint(11) | YES |  |  | 交付时间 |
| order_num | varchar(32) | YES |  |  | 订单号 |

---

### view_project_task_4_oss

**对象类型**：VIEW

**业务含义**：项目任务视图（供OSS系统使用）

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| taskId | int(11) | NO | 0 |  | 任务ID |
| projectCode | varchar(45) | YES |  |  | 项目名称 |
| executeId | binary(0) | YES |  |  | executeID |
| contractId | varchar(45) | YES |  |  | 合同号 |
| nodeTypeCode | varchar(25) | YES |  |  | 任务类型id，关联基础数据表 |
| nodeBeginTime | datetime | YES |  |  | 工程计划发生日期 |
| nodeEndTime | datetime | YES |  |  | 实际完成日期 |
| dataUpdateTime | binary(0) | YES |  |  | dataUpdate时间 |
| nodeAttached | varchar(11) | YES |  |  | 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态 |
| nodeRemark | varchar(255) | YES |  |  | 不予跟踪原因 notGrantTailCause |
| updateTime | datetime | YES |  |  | 记录数据最新更新时间 |
| effectiveTo | datetime | YES |  |  | 失效时间 |

---

### view_project_task_default_4_oss

**对象类型**：VIEW

**业务含义**：项目默认任务视图（供OSS系统使用）

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| taskId | binary(0) | YES |  |  | taskID |
| projectCode | varchar(45) | YES |  |  | 项目名称 |
| executeId | binary(0) | YES |  |  | executeID |
| contractId | varchar(45) | NO |  |  | 合同号 |
| nodeTypeCode | varchar(11) | YES |  |  | 基础数据ID，对应fnd_basic_data |
| nodeBeginTime | binary(0) | YES |  |  | node开始时间 |
| nodeEndTime | binary(0) | YES |  |  | node结束时间 |
| dataUpdateTime | binary(0) | YES |  |  | dataUpdate时间 |
| nodeAttached | varchar(11) | YES |  |  | 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态 |
| nodeRemark | varchar(255) | YES |  |  | 不予跟踪原因 notGrantTailCause |
| updateTime | binary(0) | YES |  |  | 更新时间 |
| effectiveTo | binary(0) | YES |  |  | 失效时间 |

---

### view_project_waiting_list

**对象类型**：VIEW

**业务含义**：待处理项目列表视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| projectName | varchar(255) | YES |  |  | 项目名称 |
| projectStateName | varchar(255) | YES |  |  | projectState名称 |
| projectState | varchar(255) | YES |  |  | projectState |
| contractNo | varchar(50) | YES |  |  | 合同编号 |
| officeCode | varchar(15) | YES |  |  | 办公编码 |
| officeName | varchar(20) | YES |  |  | 办公名称 |
| salesManCode | varchar(45) | YES |  |  | salesMan编码 |
| salesManName | varchar(45) | YES |  |  | salesMan名称 |
| orderCreateTime | datetime | YES |  |  | 排序Create时间 |
| systemName | varchar(255) | YES |  |  | system名称 |

---

### view_relation4contractno_marketcode

**对象类型**：VIEW

**业务含义**：合同编号与市场编码关系视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| contractNo | varchar(50) | YES |  |  | 合同编号 |
| marketCode | varchar(64) | YES |  |  | market编码 |
| marketName | varchar(255) | YES |  |  | market名称 |
| systemId | varchar(64) | YES |  |  | systemID |
| systemName | varchar(255) | YES |  |  | system名称 |

---

### view_rma_txinfo

**对象类型**：VIEW

**业务含义**：RMA退货换货信息视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| sheetID | varchar(25) | NO |  |  | RMA申请单据代码 |
| spare_serialNum | varchar(50) | YES |  |  | 备件序列号 |
| instead_of_num | varchar(25) | YES |  |  | 好件替换坏件关系 |
| item_code | varchar(15) | YES |  |  | 物料号 |
| item_name | varchar(255) | YES |  |  | 物料名称 |
| customer_name | varchar(255) | YES |  |  | 客户名称 |
| contractNo | varchar(25) | YES |  |  | 合同号 |
| contractRemark | varchar(4096) | YES |  |  | 合同备注 |
| project_name | varchar(255) | YES |  |  | 项目名称 |
| back | varchar(10) | YES |  |  | 返回类型 |
| serve | varchar(10) | YES |  |  | 服务类型 |
| tain | varchar(10) | YES |  |  | 维保类型 |
| data_state | char(1) | YES | 0 |  | 数据状态（0：最新；1：历史数据） |
| department | varchar(50) | YES |  |  | 部门 |
| application_time | datetime | YES |  |  | 申请发起时间 |
| approve_time | datetime | YES |  |  | 审批时间 |
| is_pass | varchar(2) | YES |  |  | 是否通过审批(0为未处理，1为通过，2为未通过 , -1 作废处理)  |
| applicant | varchar(10) | YES |  |  | 申请发起人 |
| EMS_num | varchar(255) | YES |  |  | 快递单号 |
| EMS_company | varchar(255) | YES |  |  | 快递公司 |
| addressee | varchar(25) | YES |  |  | 收件人 |
| send_time | datetime | YES |  |  | 出库时间 |
| isBack | char(1) | YES | 0 |  | 坏件是否返回（0：未返回;1:已返回） |
| back_time | datetime | YES |  |  | 返回时间 |
| doa_path | varchar(100) | YES |  |  | doa故障分析单（下载路径） |
| check_path | varchar(100) | YES |  |  | 检测报告(下载路径) |
| duty_person | varchar(10) | YES |  |  | 负责人 |
| isOK | char(1) | YES | 0 |  | 核销状态(0:未核销；1:已核销) |
| hexiao_time | datetime | YES |  |  | 核销时间 |
| problem_desc | text | YES |  |  | 问题描述 |
| tx_id | int(11) | NO | 0 |  | txID |

---

### view_service

**对象类型**：VIEW

**业务含义**：服务信息视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | varchar(64) | YES |  |  | ID标识 |
| barcode | varchar(50) | YES |  |  | 条码 |
| end_date | datetime | YES |  |  | end日期 |

---

### view_service_max

**对象类型**：VIEW

**业务含义**：服务最大结束日期视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| barcode | varchar(50) | YES |  |  | 条码 |
| maxEndDate | datetime | YES |  |  | max结束日期 |

---

### view_shipment_4_sms

**对象类型**：VIEW

**业务含义**：发货信息视图（供SMS系统使用）

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| contract_code | varchar(25) | YES |  |  | 合同编码 |
| itemCode | varchar(16) | YES |  |  | 项目编码 |
| barcode | varchar(50) | YES |  |  | 条码 |
| itemCode2 | varchar(16) | YES |  |  | 母子公司发货物料编码对应关系 |
| barcode2 | varchar(50) | YES |  |  | 母子公司发货序列号对应关系 |

---

### view_shipment_ems_4_pm

**对象类型**：VIEW

**业务含义**：发货EMS信息视图（供PMS使用）

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| contract_code | varchar(25) | YES |  |  | 合同编码 |
| receiveName | text | YES |  |  | 收件人 |
| emsNum | text | YES |  |  | 快递单号 |
| packdate | datetime | YES |  |  | 包装日期 |
| emsCompany | mediumtext | YES |  |  | emsCompany |
| packId | varchar(64) | YES |  |  | packID |

---

### view_shipment_info_4_pm

**对象类型**：VIEW

**业务含义**：发货详细信息视图（供PMS使用）

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| contract_code | varchar(25) | YES |  |  | 合同编码 |
| itemCode | varchar(16) | YES |  |  | 项目编码 |
| itemModel | varchar(255) | YES |  |  | 项目Model |
| itemName | varchar(255) | YES |  |  | 项目名称 |
| barcode | varchar(50) | YES |  |  | 条码 |
| comBarcode | varchar(50) | YES |  |  | comBarcode |
| packId | varchar(64) | YES |  |  | packID |
| itemCode2 | varchar(16) | YES |  |  | 母子公司发货物料编码对应关系 |
| itemModel2 | varchar(255) | YES |  |  | 项目Model |
| itemName2 | varchar(255) | YES |  |  | 项目名称 |
| barcode2 | varchar(50) | YES |  |  | 母子公司发货序列号对应关系 |
| profitCenter | varchar(32) | YES |  |  | 利润中心 |

---

### view_soft_version

**对象类型**：VIEW

**业务含义**：软件版本视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| serial_number | varchar(100) | YES |  |  | 序列号number |
| conp | mediumtext | YES |  |  | CONP版本 |
| cpld | mediumtext | YES |  |  | CPLD版本 |
| boot | mediumtext | YES |  |  | BOOT版本 |
| pcb | mediumtext | YES |  |  | PCB版本 |

---

### view_subcontract_project_4_sse

**对象类型**：VIEW

**业务含义**：分包项目视图（供SSE系统使用）

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO | 0 |  | ID标识 |
| subcontractName | varchar(512) | YES |  |  | 转包名称 |
| subcontractNo | varchar(64) | YES |  |  | 转包合同号 |
| contractNos | varchar(2048) | YES |  |  | 项目合同号 |
| projectIds | varchar(1024) | YES |  |  | 转包的项目ID |
| type | int(11) | YES |  |  | 转包类型 |
| state | int(11) | NO | 0 |  | 转包状态 |
| callbackState | int(11) | YES |  |  | 回访状态 |
| facilitatorId | int(11) | YES |  |  | 服务商表ID |
| facilitatorName | varchar(64) | YES |  |  | 服务商名 |
| bankInfo | varchar(255) | YES |  |  | 服务商开户地址 |
| bankAccount | varchar(64) | YES |  |  | 服务商收款账户 |
| officeCode | varchar(25) | YES |  |  | 办事处部门 |
| profitDepCode | varchar(25) | YES |  |  | 收益部门 |
| isAccrued | bit(1) | YES |  |  | 是否计提 |
| isInvoiced | bit(1) | YES |  |  | 是否提供发票 |
| subcontractAmount | varchar(25) | YES |  |  | 转包价 |
| reason | varchar(512) | YES |  |  | 转包原因 |
| remark | varchar(512) | YES |  |  | 备注 |
| effectiveForm | datetime | YES |  |  | 有效开始时间 |
| effectiveTo | datetime | YES |  |  | 有效结束时间 |
| zrApproveTime | datetime | YES |  |  | 最新主任审批通过时间 |
| createBy | varchar(25) | YES |  |  | 创建人 |
| createTime | datetime | YES |  |  | 创建时间 |
| updateBy | varchar(25) | YES |  |  | 更新人 |
| updateTime | datetime | YES |  |  | 更新时间 |
| stateName | varchar(255) | YES |  |  | state名称 |
| callbackStateName | varchar(255) | NO |  |  | callbackState名称 |
| createName | varchar(154) | YES |  |  | create名称 |
| officeName | varchar(20) | YES |  |  | 办公名称 |
| profitDepName | varchar(20) | YES |  |  | profit部门名称 |
| typeName | varchar(255) | YES |  |  | 类型名称 |

---

### view_txinfo

**对象类型**：VIEW

**业务含义**：退货换货信息视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| sheetID | varchar(15) | YES |  |  | 单据代码 |
| beforeChange_sheetID | varchar(15) | YES |  |  | 发起转移申请之前的备件所涉及的单据号，此字段是为了保持备件经过转移流转后涉及单据号不变 |
| applicant | varchar(25) | YES |  |  | 申请人 |
| app_time | datetime | YES |  |  | 申请时间 |
| app_dptNo | varchar(10) | YES |  |  | 申请办事处名称 |
| prt_name | varchar(255) | YES |  |  | 项目名称 |
| app_reason | text | YES |  |  | 申请原因 |
| promise_returntime | datetime | YES |  |  | 承诺备件归还时间 |
| trade_classify | varchar(100) | YES |  |  | 行业分类（手动填写） |
| signing_state | char(1) | YES |  |  | 签单状态（0：已签单；1：未签单） 废弃字段 |
| kept_place | varchar(10) | YES |  |  | 备件存放地 |
| demand_type | varchar(8) | YES |  |  | 要求类型 |
| his_zipCode | varchar(25) | YES |  |  | 邮编 |
| his_addr | varchar(1024) | YES |  |  | 地址/where |
| addre_id | int(11) | YES |  |  | 关联收件人表ID |
| duty_person | varchar(10) | YES |  |  | 负责人 |
| spare_serialNum | varchar(50) | YES |  |  | 备件序列号 |
| start_use_time | datetime | YES |  |  | 开始使用时间 |
| send_time | datetime | YES |  |  | 出库时间 |
| EMS_num | varchar(255) | YES |  |  | 快递单号 |
| EMS_company | varchar(255) | YES |  |  | 快递公司 |
| item_code | varchar(25) | YES |  |  | 物料号 |
| item_name | varchar(255) | YES |  |  | 物料名称 |
| isOK | char(1) | YES |  |  | 是否核销(是否核销，0为未核销，1为核销) |
| remark | text | YES |  |  | 备注 |
| tx_id | int(11) | NO | 0 |  | txID |
| action_time | datetime | YES |  |  | 操作时间 |
| shiftimes | int(11) | YES |  |  | 备件经过转移次数 |
| turnovertimes | int(11) | YES |  |  | 周转次数 |
| allottimes | int(11) | YES |  |  | 分配次数 |
| take_place | varchar(15) | YES | 0 |  | 0:未选择 1:供应链 2：库存 |
| approve_time | datetime | YES |  |  | 审批时间 |
| receive_time | datetime | YES |  |  | 收货时间 |
| sendout_whsCode | varchar(10) | YES |  |  | 备件发出库房 |
| isNew | char(1) | YES |  |  | 数据状态(0:历史数据；1：最新数据 2:转移申请被转移状态) |
| extend_returntime | datetime | YES |  |  | 延长归还时间 |
| hexiao_time | datetime | YES |  |  | 核销时间 |
| isUnion | int(11) | YES |  |  | 是否联合供应链发货 |

---

### view_warranty

**对象类型**：BASE TABLE（虽以view_开头但实际为BASE TABLE）

**业务含义**：维保信息表

**数据量**：约 2,857,552 行 | 数据大小：1124.4 MB | 索引大小：217.7 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| newId | bigint(20) | NO |  | PRI | ID标识（自增） |
| id | bigint(20) | YES |  |  | 原始ID |
| barcode | varchar(50) | YES |  | MUL | 条码 |
| comBarCode | varchar(50) | YES |  | MUL | 组合条码 |
| old_warrantyEndTime | datetime | YES |  |  | 原维保结束时间 |
| warrantyEndTime | datetime | YES |  | MUL | 维保结束时间 |
| old_diff | int(7) | YES |  |  | 原剩余天数 |
| diff | int(7) | YES |  | MUL | 剩余天数 |
| warrantyStartTime | datetime | YES |  |  | 维保开始时间 |
| old_warrantyStartTime | datetime | YES |  |  | 原维保开始时间 |
| item | varchar(16) | YES |  | MUL | 物料编码 |
| describe_ | varchar(255) | YES |  |  | 描述 |
| itemName | varchar(255) | YES |  |  | 物料名称 |
| gradeName | varchar(125) | YES |  |  | 等级名称 |
| gradeCode | varchar(25) | YES |  | MUL | 等级编码 |
| packdate | datetime | YES |  |  | 出厂日期 |
| contract_code | varchar(25) | YES |  | MUL | 合同编码 |
| contract_type | int(11) | YES |  |  | 合同类型 |
| contract_type_name | varchar(25) | YES |  |  | 合同类型名称 |
| project_name | varchar(512) | YES |  |  | 项目名称 |
| customer_name | varchar(512) | YES |  |  | 客户名称 |
| office_code | varchar(25) | YES |  | MUL | 办事处编码 |
| office_name | varchar(25) | YES |  |  | 办事处名称 |
| marketCode | varchar(10) | YES |  | MUL | 市场编码 |
| marketName | varchar(15) | YES |  |  | 市场名称 |
| systemId | int(11) | YES |  |  | 体系ID |
| systemName | varchar(15) | YES |  |  | 体系名称 |
| warranty | varchar(2) | YES |  |  | 维保状态 |
| warrantyMonth | double | YES |  |  | 维保月数 |
| barcode2 | varchar(50) | YES |  | MUL | 条码2 |
| item2 | varchar(16) | YES |  |  | 物料编码2 |
| describe_2 | varchar(255) | YES |  |  | 描述2 |
| itemName2 | varchar(255) | YES |  |  | 物料名称2 |
| agentName | varchar(500) | YES |  |  | 代理商名称 |
| finalCustomerName | varchar(255) | YES |  |  | 最终客户名称 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | newId | 否 | BTREE |
| barcode | barcode | 是 | BTREE |
| barcode2 | barcode2 | 是 | BTREE |
| comBarCode | comBarCode | 是 | BTREE |
| contract_barcode_IDX | contract_code,barcode | 是 | BTREE |
| contract_code | contract_code | 是 | BTREE |
| diff | diff | 是 | BTREE |
| gradeCode | gradeCode | 是 | BTREE |
| item | item | 是 | BTREE |
| marketCode_systemId | marketCode,systemId | 是 | BTREE |
| office_code_marketCode_systemId | office_code,marketCode,systemId | 是 | BTREE |
| warrantyEndTime | warrantyEndTime | 是 | BTREE |

---

### view_warranty_contract_state

**对象类型**：BASE TABLE（虽以view_开头但实际为BASE TABLE）

**业务含义**：维保合同状态表

**数据量**：约 66,447 行 | 数据大小：23.6 MB | 索引大小：2.5 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| contractNo | varchar(25) | YES |  | MUL | 合同编号 |
| diff | decimal(23,0) | YES |  |  | 剩余天数 |
| warrantyStatusName | varchar(4) | YES |  |  | 维保状态名称 |
| warrantyStartTime | datetime | YES |  |  | 维保开始时间 |
| warrantyEndTime | datetime | YES |  |  | 维保结束时间 |
| warrantyGrade | int(3) | YES |  |  | 维保等级 |
| warrantyGradeStartTime | datetime | YES |  |  | 等级开始时间 |
| warrantyGradeEndTime | datetime | YES |  |  | 等级结束时间 |
| gradecodes | mediumtext | YES |  |  | 等级编码列表 |
| gradenames | mediumtext | YES |  |  | 等级名称列表 |
| gradedesc | mediumtext | YES |  |  | 等级描述列表 |
| hasRenewal | int(1) | NO | 0 |  | 是否有续保 |
| renewalDesc | mediumtext | YES |  |  | 续保描述 |
| hasLiscense | bigint(1) | YES |  |  | 是否有授权 |
| liscenseCodes | mediumtext | YES |  |  | 授权编码列表 |
| liscenseDesc | mediumtext | YES |  |  | 授权描述 |
| wafService | bigint(1) | YES |  |  | WAF服务 |
| wafServiceStartTime | datetime | YES |  |  | WAF服务开始时间 |
| wafServiceEndTime | datetime | YES |  |  | WAF服务结束时间 |
| itemCode | mediumtext | YES |  |  | 物料编码 |
| itemDesc | mediumtext | YES |  |  | 物料描述 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| contractNo | contractNo | 是 | BTREE |

---

### view_warranty_info_4_ts

**对象类型**：VIEW

**业务含义**：维保信息视图（供TS系统使用）

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | bigint(20) | YES |  |  | ID标识 |
| projectName | varchar(512) | YES |  |  | 项目名称 |
| contractNo | varchar(25) | YES |  |  | 合同编号 |
| barcode | varchar(50) | YES |  |  | 条码 |
| item | varchar(16) | YES |  |  | 项目 |
| itemName | varchar(255) | YES |  |  | 项目名称 |
| itemDesc | varchar(255) | YES |  |  | 项目描述 |
| barcode2 | varchar(50) | YES |  |  | 母子公司发货序列号对应关系 |
| item2 | varchar(16) | YES |  |  | 母子公司发货物料编码对应关系 |
| itemName2 | varchar(255) | YES |  |  | 项目名称 |
| itemDesc2 | varchar(255) | YES |  |  | 项目描述 |
| gradeName | varchar(125) | YES |  |  | 等级名称 |
| warranty | varchar(2) | YES |  |  | 质保 |
| warrantyStartTime | datetime | YES |  |  | warrantyStart时间 |
| warrantyEndTime | datetime | YES |  |  | warranty结束时间 |
| diff | int(7) | YES |  |  | 差异 |

---

### view_warranty_source

**对象类型**：VIEW

**业务含义**：维保数据来源视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | bigint(20) | YES | 0 |  | ID标识 |
| barcode | varchar(50) | YES |  |  | 条码 |
| comBarCode | varchar(50) | YES |  |  | comBar编码 |
| old_warrantyEndTime | datetime | YES |  |  | old_warrantyEnd时间 |
| warrantyEndTime | datetime | YES |  |  | warranty结束时间 |
| old_diff | int(7) | YES |  |  | olddiff |
| diff | int(7) | YES |  |  | 差异 |
| warrantyStartTime | datetime | YES |  |  | warrantyStart时间 |
| old_warrantyStartTime | datetime | YES |  |  | old_warrantyStart时间 |
| item | varchar(16) | YES |  |  | 项目 |
| describe_ | varchar(255) | YES |  |  | describe |
| itemName | varchar(255) | YES |  |  | 项目名称 |
| gradeName | varchar(125) | YES |  |  | 等级名称 |
| gradeCode | varchar(25) | YES |  |  | 等级编码 |
| packdate | datetime | YES |  |  | 包装日期 |
| contract_code | varchar(25) | YES |  |  | 合同编码 |
| contract_type | int(11) | YES |  |  | 合同类型 |
| contract_type_name | varchar(25) | YES |  |  | 合同类型名称 |
| project_name | varchar(512) | YES |  |  | 项目名称 |
| customer_name | varchar(512) | YES |  |  | 客户名称 |
| office_code | varchar(25) | YES |  |  | office编码 |
| office_name | varchar(25) | YES |  |  | office名称 |
| marketCode | varchar(10) | YES |  |  | market编码 |
| marketName | varchar(15) | YES |  |  | market名称 |
| systemId | int(11) | YES |  |  | systemID |
| systemName | varchar(15) | YES |  |  | system名称 |
| warranty | varchar(2) | YES |  |  | 质保 |
| warrantyMonth | double | YES |  |  | warrantyMonth |
| barcode2 | varchar(50) | YES |  |  | 母子公司发货序列号对应关系 |
| item2 | varchar(16) | YES |  |  | 母子公司发货物料编码对应关系 |
| syncTime | datetime | YES |  |  | sync时间 |

---

### view_warranty_with_presales

**对象类型**：BASE TABLE（虽以view_开头但实际为BASE TABLE）

**业务含义**：维保含售前信息表

**数据量**：约 2,857,552 行 | 数据大小：1124.4 MB | 索引大小：188.6 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| newId | bigint(20) | NO |  | PRI | ID标识（自增） |
| id | bigint(20) | YES |  |  | 原始ID |
| barcode | varchar(50) | YES |  | MUL | 条码 |
| comBarCode | varchar(50) | YES |  | MUL | 组合条码 |
| old_warrantyEndTime | datetime | YES |  |  | 原维保结束时间 |
| warrantyEndTime | datetime | YES |  | MUL | 维保结束时间 |
| old_diff | int(7) | YES |  |  | 原剩余天数 |
| diff | int(7) | YES |  | MUL | 剩余天数 |
| warrantyStartTime | datetime | YES |  |  | 维保开始时间 |
| old_warrantyStartTime | datetime | YES |  |  | 原维保开始时间 |
| item | varchar(16) | YES |  | MUL | 物料编码 |
| describe_ | varchar(255) | YES |  |  | 描述 |
| itemName | varchar(255) | YES |  |  | 物料名称 |
| gradeName | varchar(125) | YES |  |  | 等级名称 |
| gradeCode | varchar(25) | YES |  | MUL | 等级编码 |
| packdate | datetime | YES |  |  | 出厂日期 |
| contract_code | varchar(25) | YES |  | MUL | 合同编码 |
| contract_type | int(11) | YES |  |  | 合同类型 |
| contract_type_name | varchar(25) | YES |  |  | 合同类型名称 |
| project_name | varchar(512) | YES |  |  | 项目名称 |
| customer_name | varchar(512) | YES |  |  | 客户名称 |
| office_code | varchar(25) | YES |  | MUL | 办事处编码 |
| office_name | varchar(25) | YES |  |  | 办事处名称 |
| marketCode | varchar(10) | YES |  | MUL | 市场编码 |
| marketName | varchar(15) | YES |  |  | 市场名称 |
| systemId | int(11) | YES |  |  | 体系ID |
| systemName | varchar(15) | YES |  |  | 体系名称 |
| warranty | varchar(2) | YES |  |  | 维保状态 |
| warrantyMonth | double | YES |  |  | 维保月数 |
| barcode2 | varchar(50) | YES |  | MUL | 条码2 |
| item2 | varchar(16) | YES |  |  | 物料编码2 |
| describe_2 | varchar(255) | YES |  |  | 描述2 |
| itemName2 | varchar(255) | YES |  |  | 物料名称2 |
| agentName | varchar(500) | YES |  |  | 代理商名称 |
| finalCustomerName | varchar(255) | YES |  |  | 最终客户名称 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | newId | 否 | BTREE |
| barcode | barcode | 是 | BTREE |
| barcode2 | barcode2 | 是 | BTREE |
| comBarCode | comBarCode | 是 | BTREE |
| contract_code | contract_code | 是 | BTREE |
| diff | diff | 是 | BTREE |
| gradeCode | gradeCode | 是 | BTREE |
| item | item | 是 | BTREE |
| marketCode_systemId | marketCode,systemId | 是 | BTREE |
| office_code_marketCode_systemId | office_code,marketCode,systemId | 是 | BTREE |
| warrantyEndTime | warrantyEndTime | 是 | BTREE |

---

### warehouse

**业务含义**：（待补充）

**数据量**：约 36 行 | 数据大小：0.0 MB | 索引大小：0.0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| whs_id | int(11) | NO |  | PRI | ID标识（自增） |
| whs_code | varchar(10) | YES |  | MUL | 库房编码 |
| whs_name | varchar(25) | YES |  |  | 库房名称 |
| whs_addr | varchar(255) | YES |  |  | 库房地址 |
| username | varchar(10) | YES |  |  | 负责人工号 |
| department | varchar(25) | YES |  |  | 部门 |
| contact_tel | varchar(15) | YES |  |  | 联系电话 |
| contact_mail | varchar(50) | YES |  |  | 联系邮箱 |
| remark | text | YES |  |  | 备注 |
| whs_state | char(1) | YES | 1 |  | 1:有效 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | whs_id | 是 | BTREE |
| whs_code | whs_code | 否 | BTREE |

---

### warehouse_info

**业务含义**：（待补充）

**数据量**：约 11,561 行 | 数据大小：1.5 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| info_id | int(11) | NO |  | PRI | ID标识（自增） |
| item_code | varchar(10) | YES |  |  | 编码 |
| item_name | varchar(100) | YES |  |  | 名称 |
| whs_code | varchar(10) | YES |  |  | 编码 |
| quantity | int(11) | YES |  |  | 数量 |
| item_state | char(1) | YES |  |  | 0:坏件 1：好件 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | info_id | 是 | BTREE |

---

### warehouse_info_detail

**业务含义**：（待补充）

**数据量**：约 58,033 行 | 数据大小：4.5 MB | 索引大小：1.5 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| info_id | int(11) | YES |  | MUL | ID标识 |
| spare_serialNum | varchar(25) | YES |  |  | 备件序列号 |
| demand_type | varchar(25) | YES |  |  | 状态维护在sys_state_or_type |
| tx_id | int(11) | YES |  |  | ID标识 |
| state | varchar(2) | YES |  |  | 1：在库 2：客户 3：被申请 |
| data_state | char(1) | YES | 1 |  | 0:历史 1：最新 |
| in_time | datetime | YES |  |  | 入库时间 |
| finance_in_time | datetime | YES |  |  | 财务入库时间 |
| analyse_in_time | datetime | YES |  |  | 时间 |
| analyse_out_time | datetime | YES |  |  | 时间 |
| gaizhi_in_time | datetime | YES |  |  | 时间 |
| gaizhi_out_time | datetime | YES |  |  | 时间 |
| remark | text | YES |  |  | 备注/描述 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| info_id | info_id | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---

### warranty_change_logs

**业务含义**：（待补充）

**数据量**：约 55 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| username | varchar(10) | NO |  |  | 名称 |
| updateType | int(11) | YES |  |  | 更新信息 |
| barcode | varchar(20) | YES |  |  | 编码 |
| warrantyStartTime | datetime | YES |  |  | 时间 |
| warrantyEndTime | datetime | YES |  |  | 时间 |
| warrantyTimes | int(11) | YES |  |  | warrantyTimes |
| updateTime | datetime | YES |  |  | 时间 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### warranty_info

**业务含义**：（待补充）

**数据量**：约 0 行 | 数据大小：0.0 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| packlistId | int(11) | YES |  |  | 标识已经同步 |
| contractId | int(11) | YES |  |  | 标识已经同步 |
| barCode | varchar(25) | YES |  |  | 序列号 |
| officeCode | varchar(25) | YES |  |  | 办事处 |
| projectName | varchar(255) | YES |  |  | 项目名称 |
| contractNo | varchar(25) | YES |  |  | 合同号 |
| contractType | int(11) | YES |  |  | 合同类型 |
| customerName | varchar(255) | YES |  |  | 客户名称 |
| itemCode | varchar(8) | YES |  |  | 物料编码 |
| itemName | varchar(255) | YES |  |  | 物料描述 |
| warrantyLevel | varchar(8) | YES |  |  | 维保级别 |
| warrantyStartTime | datetime | YES |  |  | 维保开始时间 |
| warrantyEndTime | datetime | YES |  |  | 维保结束时间 |
| warrantyLimit | int(11) | YES |  |  | 维保年限 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

### workflow_info

**业务含义**：（待补充）

**数据量**：约 37,771 行 | 数据大小：2.5 MB | 索引大小：0 MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| id | int(11) | NO |  | PRI | ID标识（自增） |
| sheetID | varchar(10) | NO |  |  | ID标识 |
| sheet_type | char(1) | YES |  |  | 单据类型（0：RMA 1：借用 2：转移） |
| workflow_action | char(1) | NO |  |  | 所需做的操作(1:审批选择备件 ；2：发货确认 ；3：接货确认 5：重新审批 6：RMA申请坏件替换关系确认 4：坏件返回确认 ，7.坏件核销 8 技服执行坏件返回确认) |
| action_people | varchar(10) | NO |  |  | 操作的用户 |
| action_state | char(1) | NO |  |  | 1:待完成 2：已完成 |
| node | int(11) | YES |  |  | 节点 |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| PRIMARY | id | 是 | BTREE |

---

## 3. 枚举值汇总

| 字段名 | 枚举值 | 推断含义 | 数据分布数量 |
|--------|--------|----------|-------------|
| projectType | 10 | 实施类项目 | 73,117 |
| projectType | afss | 安全服务项目 | 2,046 |
| projectType | afxx | 安全营销项目 | 541 |
| projectState | 10 | 待确认 | 2 |
| projectState | 100 | 已完成 | 19,903 |
| projectState | 20 | 进行中 | 47,552 |
| projectState | 30 | 已暂停 | 38 |
| projectState | 31 | 暂停待确认 | 2,465 |
| projectState | 32 | 暂停中 | 5,152 |
| projectState | 40 | 待关闭 | 367 |
| projectState | 50 | 已关闭 | 225 |
| memberRole | 10 | 项目经理 | 72,014 |
| memberRole | 15 | 副项目经理 | 471 |
| memberRole | 20 | 项目成员 | 152,543 |
| memberRole | 30 | 技术负责人 | 55,467 |
| memberRole | 40 | 质量负责人 | 1,285 |
| memberRole | 50 | 安全负责人 | 1,647 |
| memberRole | 60 | 远程支持 | 20,966 |
| memberRole | 71 | 驻场工程师 | 142 |
| memberRole | 80 | 其他 | 337 |
| memberRole | other | 其他 | 1 |
| applyState_presales | NULL |  | 11,909 |
| applyState_presales | 1 | 待审批 | 1,287 |
| applyState_presales | 2 | 已审批 | 5,264 |
| status_prob | 0 | 草稿 | 3 |
| status_prob | 1 | 待处理 | 146 |
| status_prob | 4 | 已解决 | 974 |
| status_prob | 5 | 已关闭 | 4 |
| status_prob | 6 | 已验证 | 12 |
| status_prob | 8 | 处理中 | 128 |
| state_subcontract | -100 | 已拒绝 | 118 |
| state_subcontract | -30 | 已撤回 | 12 |
| state_subcontract | -20 | 已退回 | 87 |
| state_subcontract | -15 | 待修改 | 2 |
| state_subcontract | 0 | 草稿 | 38 |
| state_subcontract | 10 | 待审批 | 1 |
| state_subcontract | 15 | 审批中 | 7 |
| state_subcontract | 20 | 已通过 | 11 |
| state_subcontract | 30 | 执行中 | 540 |
| state_subcontract | 40 | 已完成 | 2,410 |
| state_subcontract | 100 |  | 40 |
| source_order | D365 |  | 1,793 |
| source_order | SAP |  | 48,720 |
| orderType | 0 |  | 46,674 |
| orderType | 1 |  | 3,839 |
| isparam_dept | 0 | 否 | 102 |
| isparam_dept | 1 | 是 | 37 |
| salesType | 01 | 直销 | 73,417 |
| salesType | 02 | 渠道 | 931 |
| salesType | 14 |  | 59 |
| salesType | afss |  | 789 |
| salesType | afxx |  | 508 |

## 4. 字段映射关系

pm_column_of_relationship 定义了不同项目类型下动态字段（column001~column014）的业务含义映射。

| ID | 项目类型 | 字段编码 | 字段名 | 字段描述 | 生效时间 | 失效时间 |
|-----|----------|----------|--------|----------|----------|----------|
| 1 | 0 | column001 | officeCode | 办事处编码 | 2015-05-22 11:03:45 | 永久 |
| 2 | 0 | column002 | customerCode | 客户编码 | 2015-05-22 11:03:41 | 永久 |
| 3 | 0 | column003 | customerName | 客户名称 | 2015-05-22 11:03:38 | 永久 |
| 4 | 0 | column004 | marketCode | 市场部编码 | 2015-05-22 11:03:35 | 永久 |
| 5 | 0 | column005 | systemId | 系统部ID | 2015-05-22 11:03:33 | 永久 |
| 6 | 0 | column006 | expendId | 拓展部ID | 2015-05-22 11:03:31 | 永久 |
| 7 | 0 | column007 | industryId | 子行业ID | 2015-05-22 11:03:29 | 永久 |
| 8 | 0 | column008 | notGrantTailCause | 不予跟踪原因 | 2015-05-22 11:03:27 | 永久 |
| 9 | 0 | column009 | orderCreateTime | 订单创建时间 | 2015-05-22 11:03:24 | 永久 |
| 10 | 0 | column010 | None | 项目类型 | 2015-05-25 17:39:44 | 永久 |
| 11 | 0 | column011 | None | 项目类别 | 2015-05-25 17:39:43 | 永久 |
| 13 | 0 | column013 | finalCustomerName | 最终客户名称 | 2015-06-08 11:18:16 | 永久 |
| 14 | 0 | column014 | backReason | 回退说明 | 2015-06-18 17:21:45 | 永久 |
| 12 | 0 | colunm012 | implement | 项目实施方式 | 2015-06-03 16:12:25 | 永久 |

## 5. 基础数据类型

fnd_basic_data_type 定义了系统中所有枚举值分类。

| ID | 类型编码 | 类型名称 | 状态 | 生效时间 | 失效时间 |
|-----|----------|----------|------|----------|----------|
| 1 | 01 | 快递公司 | 启用 | 2015-05-22 17:21:08 | 永久 |
| 2 | 02 | 项目状态 | 启用 | 2015-05-22 17:51:34 | 永久 |
| 3 | 03 | 项目成员角色 | 启用 | 2015-05-23 14:22:55 | 永久 |
| 4 | 04 | 项目分类 | 启用 | 2015-05-23 14:38:09 | 永久 |
| 5 | 05 | 项目类型 | 启用 | 2015-05-25 17:35:11 | 永久 |
| 6 | 06 | 项目类别 | 启用 | 2015-05-25 17:35:12 | 永久 |
| 7 | 07 | 项目渠道类型 | 启用 | 2015-06-01 11:29:03 | 永久 |
| 8 | 08 | 项目操作状态 | None | 2015-06-01 11:29:05 | 永久 |
| 9 | 09 | 项目任务节点 | 启用 | 2015-06-01 11:29:08 | 永久 |
| 10 | 10 | 项目维护页面TAB选项卡 | 启用 | 2015-06-01 11:29:41 | 永久 |
| 11 | 12 | 待办事项页面TAB选项卡 | 启用 | 2015-06-08 15:47:45 | 永久 |
| 12 | 13 | 测评问卷类型 | 启用 | 2015-06-09 14:54:48 | 永久 |
| 13 | 14 | 测评问卷题目类型 | 启用 | 2015-06-09 14:54:48 | 永久 |
| 14 | 15 | 项目实施方式 | 启用 | 2015-06-10 11:06:41 | 永久 |
| 15 | 16 | 合同/拆分TAB选项卡 | None | 2015-06-11 13:39:25 | 永久 |
| 16 | 17 | 项目闭环流程节点名称 | 启用 | 2015-06-12 10:57:20 | 永久 |
| 17 | 18 | 数据统计页面选项卡 | 启用 | 2015-07-21 17:32:05 | 永久 |
| 18 | 21 | SMS实施方式与PMS实施方式关联关系 | 启用 | 2015-09-12 10:28:58 | 永久 |
| 19 | subcontractType | 项目转包类型 | 启用 | 2015-09-12 10:28:58 | 永久 |
| 20 | maintenanceType | 巡检任务性质 | 启用 | 2015-09-12 10:28:58 | 永久 |
| 21 | maintenanceDeliverFileType | 巡检任务交付件类型 | 启用 | 2015-09-12 10:28:58 | 永久 |
| 22 | 11 | 项目任务交付件类型 | 启用 | 2015-06-01 11:29:41 | 永久 |
| 23 | 29 | 售前任务节点 | 启用 | 2015-06-01 11:29:08 | 永久 |
| 24 | presalesType | 售前项目类型 | 启用 | 2015-09-12 10:28:58 | 永久 |
| 25 | presalesDeliverFileType | 售前项目交付件类型 | 启用 | 2015-09-12 10:28:58 | 永久 |
| 26 | supervisionType | 项目督查任务分类 | 启用 | 2015-09-12 10:28:58 | 永久 |
| 27 | maintenanceCategory | 巡检任务大类 | 启用 | 2015-09-12 10:28:58 | 永久 |
| 28 | maintenanceSubCategory | 巡检任务小类 | 启用 | 2015-09-12 10:28:58 | 永久 |
| 29 | projectExecutionState | 项目实施状态 | 启用 | 2015-09-12 10:28:58 | 永久 |
| 30 | projectCloseProcessState | 项目闭环流程状态 | 启用 | 2015-09-12 10:28:58 | 永久 |
| 31 | 27 | 售前项目状态 | 启用 | 2015-06-01 11:29:08 | 永久 |

## 6. 外键关系

| 约束名 | 表名 | 列名 | 引用表 | 引用列 |
|--------|------|------|--------|--------|
| pm_presales_project_duration_ibfk_1 | pm_presales_project_duration | presalesId | pm_presales_project_header | presalesId |
| fk_userInfo_userId | t_user_info | user_id | t_user | user_id |
| t_user_role_ibfk_1 | t_user_role | user_id | t_user | user_id |
| t_user_role_ibfk_2 | t_user_role | role_id | t_role | role_id |

## 7. 索引有效性分析

### 7.1 可能的冗余索引

**pm_cl_quesnaire_template_line**：
- 索引 `id_UNIQUE` 可能被索引 `PRIMARY` 覆盖（前缀列相同）

**pm_cl_quesnaire_template_options**：
- 索引 `id_UNIQUE` 可能被索引 `PRIMARY` 覆盖（前缀列相同）

**prob_product**：
- 索引 `probId_Status_IDX` 可能被索引 `probId_status_item_IDX` 覆盖（前缀列相同）

**rma_applicant**：
- 索引 `id` 可能被索引 `PRIMARY` 覆盖（前缀列相同）


### 7.2 缺失索引建议

基于表数据量和常见查询模式，建议关注以下索引：

**大表但仅有主键索引的表**：

- `shipment_barcode_from_spms_unique`（233,076 行）- 建议根据查询条件添加合适索引
- `pm_project_maintenance_view`（183,607 行）- 建议根据查询条件添加合适索引
- `fnd_mails`（146,157 行）- 建议根据查询条件添加合适索引
- `prob_read_log`（43,284 行）- 建议根据查询条件添加合适索引
- `workflow_info`（37,771 行）- 建议根据查询条件添加合适索引
- `pm_project_product_af_from_sms`（21,608 行）- 建议根据查询条件添加合适索引
- `pm_presales_lend_order_from_sms_history`（20,196 行）- 建议根据查询条件添加合适索引
- `t_user_login_record`（18,952 行）- 建议根据查询条件添加合适索引
- `pm_project_product_af_from_sms_history`（18,054 行）- 建议根据查询条件添加合适索引
- `fnd_data_refresh_log`（16,540 行）- 建议根据查询条件添加合适索引
- `pm_project_real_product_line_from_sms`（16,140 行）- 建议根据查询条件添加合适索引
- `warehouse_info`（11,561 行）- 建议根据查询条件添加合适索引
- `pm_report_line_data`（11,315 行）- 建议根据查询条件添加合适索引
- `t_mails`（10,817 行）- 建议根据查询条件添加合适索引

**常见查询字段索引建议**：

- `pm_project.projectState` - 项目状态是高频查询条件，建议确认索引覆盖
- `pm_project.projectType` - 项目类型是高频查询条件
- `pm_project_member.projectId` - 按项目查成员是核心查询
- `pm_project.createTime` - 时间范围查询频繁
- `pm_order_data_from_erp_source.source` - 订单来源筛选
