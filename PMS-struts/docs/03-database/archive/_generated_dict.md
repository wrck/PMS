### act_evt_log

**业务含义**：Activiti事件日志表 - 记录流程引擎的事件日志

**数据量**：约6,398行 | 数据大小：17.5MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| LOG_NR_ | bigint(20) | NO |  | PRI |  |
| TYPE_ | varchar(64) | YES |  |  |  |
| PROC_DEF_ID_ | varchar(64) | YES |  |  |  |
| PROC_INST_ID_ | varchar(64) | YES |  |  |  |
| EXECUTION_ID_ | varchar(64) | YES |  |  |  |
| TASK_ID_ | varchar(64) | YES |  |  |  |
| TIME_STAMP_ | timestamp(3) | NO | CURRENT_TIMESTAMP(3) |  |  |
| USER_ID_ | varchar(255) | YES |  |  |  |
| DATA_ | longblob | YES |  |  |  |
| LOCK_OWNER_ | varchar(255) | YES |  |  |  |
| LOCK_TIME_ | timestamp(3) | YES |  |  |  |
| IS_PROCESSED_ | tinyint(4) | YES | 0 |  |  |

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
| ID_ | varchar(64) | NO |  | PRI |  |
| REV_ | int(11) | YES |  |  |  |
| NAME_ | varchar(255) | YES |  |  |  |
| DEPLOYMENT_ID_ | varchar(64) | YES |  | MUL |  |
| BYTES_ | longblob | YES |  |  |  |
| GENERATED_ | tinyint(4) | YES |  |  |  |

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
| NAME_ | varchar(64) | NO |  | PRI |  |
| VALUE_ | varchar(300) | YES |  |  |  |
| REV_ | int(11) | YES |  |  |  |

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
| ID_ | varchar(64) | NO |  | PRI |  |
| PROC_DEF_ID_ | varchar(64) | NO |  |  |  |
| PROC_INST_ID_ | varchar(64) | NO |  | MUL |  |
| EXECUTION_ID_ | varchar(64) | NO |  | MUL |  |
| ACT_ID_ | varchar(255) | NO |  |  |  |
| TASK_ID_ | varchar(64) | YES |  |  |  |
| CALL_PROC_INST_ID_ | varchar(64) | YES |  |  |  |
| ACT_NAME_ | varchar(255) | YES |  |  |  |
| ACT_TYPE_ | varchar(255) | NO |  |  |  |
| ASSIGNEE_ | varchar(255) | YES |  |  |  |
| DURATION_ | bigint(20) | YES |  |  |  |
| TENANT_ID_ | varchar(255) | YES |  |  |  |
| START_TIME_ | datetime(3) | NO |  |  |  |
| END_TIME_ | datetime(3) | YES |  |  |  |

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
| ID_ | varchar(64) | NO |  | PRI |  |
| REV_ | int(11) | YES |  |  |  |
| USER_ID_ | varchar(255) | YES |  |  |  |
| NAME_ | varchar(255) | YES |  |  |  |
| DESCRIPTION_ | varchar(4000) | YES |  |  |  |
| TYPE_ | varchar(255) | YES |  |  |  |
| TASK_ID_ | varchar(64) | YES |  |  |  |
| PROC_INST_ID_ | varchar(64) | YES |  |  |  |
| URL_ | varchar(4000) | YES |  |  |  |
| CONTENT_ID_ | varchar(64) | YES |  |  |  |
| TIME_ | datetime(3) | YES |  |  |  |

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
| ID_ | varchar(64) | NO |  | PRI |  |
| TYPE_ | varchar(255) | YES |  |  |  |
| USER_ID_ | varchar(255) | YES |  |  |  |
| TASK_ID_ | varchar(64) | YES |  |  |  |
| PROC_INST_ID_ | varchar(64) | YES |  |  |  |
| ACTION_ | varchar(255) | YES |  |  |  |
| MESSAGE_ | varchar(4000) | YES |  |  |  |
| FULL_MSG_ | longblob | YES |  |  |  |
| TIME_ | datetime(3) | NO |  |  |  |

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
| ID_ | varchar(64) | NO |  | PRI |  |
| TYPE_ | varchar(255) | NO |  |  |  |
| PROC_INST_ID_ | varchar(64) | YES |  | MUL |  |
| EXECUTION_ID_ | varchar(64) | YES |  |  |  |
| TASK_ID_ | varchar(64) | YES |  | MUL |  |
| ACT_INST_ID_ | varchar(64) | YES |  | MUL |  |
| NAME_ | varchar(255) | NO |  | MUL |  |
| VAR_TYPE_ | varchar(255) | YES |  |  |  |
| REV_ | int(11) | YES |  |  |  |
| BYTEARRAY_ID_ | varchar(64) | YES |  |  |  |
| DOUBLE_ | double | YES |  |  |  |
| LONG_ | bigint(20) | YES |  |  |  |
| TEXT_ | varchar(4000) | YES |  |  |  |
| TEXT2_ | varchar(4000) | YES |  |  |  |
| TIME_ | datetime(3) | NO |  |  |  |

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
| ID_ | varchar(64) | NO |  | PRI |  |
| GROUP_ID_ | varchar(255) | YES |  |  |  |
| TYPE_ | varchar(255) | YES |  |  |  |
| USER_ID_ | varchar(255) | YES |  | MUL |  |
| TASK_ID_ | varchar(64) | YES |  | MUL |  |
| PROC_INST_ID_ | varchar(64) | YES |  | MUL |  |

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
| ID_ | varchar(64) | NO |  | PRI |  |
| PROC_INST_ID_ | varchar(64) | NO |  | UNI |  |
| BUSINESS_KEY_ | varchar(255) | YES |  | MUL |  |
| PROC_DEF_ID_ | varchar(64) | NO |  |  |  |
| DURATION_ | bigint(20) | YES |  |  |  |
| START_USER_ID_ | varchar(255) | YES |  |  |  |
| START_ACT_ID_ | varchar(255) | YES |  |  |  |
| END_ACT_ID_ | varchar(255) | YES |  |  |  |
| SUPER_PROCESS_INSTANCE_ID_ | varchar(64) | YES |  |  |  |
| DELETE_REASON_ | varchar(4000) | YES |  |  |  |
| TENANT_ID_ | varchar(255) | YES |  |  |  |
| START_TIME_ | datetime(3) | NO |  |  |  |
| END_TIME_ | datetime(3) | YES |  |  |  |
| NAME_ | varchar(255) | YES |  |  |  |

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
| ID_ | varchar(64) | NO |  | PRI |  |
| PROC_DEF_ID_ | varchar(64) | YES |  |  |  |
| TASK_DEF_KEY_ | varchar(255) | YES |  |  |  |
| PROC_INST_ID_ | varchar(64) | YES |  | MUL |  |
| EXECUTION_ID_ | varchar(64) | YES |  |  |  |
| NAME_ | varchar(255) | YES |  |  |  |
| PARENT_TASK_ID_ | varchar(64) | YES |  |  |  |
| DESCRIPTION_ | varchar(4000) | YES |  |  |  |
| OWNER_ | varchar(255) | YES |  |  |  |
| ASSIGNEE_ | varchar(255) | YES |  |  |  |
| DURATION_ | bigint(20) | YES |  |  |  |
| DELETE_REASON_ | varchar(4000) | YES |  |  |  |
| PRIORITY_ | int(11) | YES |  |  |  |
| FORM_KEY_ | varchar(255) | YES |  |  |  |
| CATEGORY_ | varchar(255) | YES |  |  |  |
| TENANT_ID_ | varchar(255) | YES |  |  |  |
| START_TIME_ | datetime(3) | NO |  |  |  |
| CLAIM_TIME_ | datetime(3) | YES |  |  |  |
| END_TIME_ | datetime(3) | YES |  |  |  |
| DUE_DATE_ | datetime(3) | YES |  |  |  |

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
| ID_ | varchar(64) | NO |  | PRI |  |
| PROC_INST_ID_ | varchar(64) | YES |  | MUL |  |
| EXECUTION_ID_ | varchar(64) | YES |  |  |  |
| TASK_ID_ | varchar(64) | YES |  | MUL |  |
| NAME_ | varchar(255) | NO |  | MUL |  |
| VAR_TYPE_ | varchar(100) | YES |  |  |  |
| REV_ | int(11) | YES |  |  |  |
| BYTEARRAY_ID_ | varchar(64) | YES |  |  |  |
| DOUBLE_ | double | YES |  |  |  |
| LONG_ | bigint(20) | YES |  |  |  |
| TEXT_ | varchar(4000) | YES |  |  |  |
| TEXT2_ | varchar(4000) | YES |  |  |  |
| CREATE_TIME_ | datetime(3) | YES |  |  |  |
| LAST_UPDATED_TIME_ | datetime(3) | YES |  |  |  |

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
| ID_ | varchar(64) | NO |  | PRI |  |
| REV_ | int(11) | YES |  |  |  |
| NAME_ | varchar(255) | YES |  |  |  |
| TYPE_ | varchar(255) | YES |  |  |  |

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
| ID_ | varchar(64) | NO |  | PRI |  |
| REV_ | int(11) | YES |  |  |  |
| USER_ID_ | varchar(64) | YES |  |  |  |
| TYPE_ | varchar(64) | YES |  |  |  |
| KEY_ | varchar(255) | YES |  |  |  |
| VALUE_ | varchar(255) | YES |  |  |  |
| PASSWORD_ | longblob | YES |  |  |  |
| PARENT_ID_ | varchar(255) | YES |  |  |  |

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
| USER_ID_ | varchar(64) | NO |  | PRI |  |
| GROUP_ID_ | varchar(64) | NO |  | PRI |  |

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
| ID_ | varchar(64) | NO |  | PRI |  |
| REV_ | int(11) | YES |  |  |  |
| FIRST_ | varchar(255) | YES |  |  |  |
| LAST_ | varchar(255) | YES |  |  |  |
| EMAIL_ | varchar(255) | YES |  |  |  |
| PWD_ | varchar(255) | YES |  |  |  |
| PICTURE_ID_ | varchar(64) | YES |  |  |  |

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
| ID_ | varchar(64) | NO |  | PRI |  |
| PROC_DEF_ID_ | varchar(64) | NO |  | UNI |  |
| REV_ | int(11) | YES |  |  |  |
| INFO_JSON_ID_ | varchar(64) | YES |  | MUL |  |

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
| ID_ | varchar(64) | NO |  | PRI |  |
| NAME_ | varchar(255) | YES |  |  |  |
| CATEGORY_ | varchar(255) | YES |  |  |  |
| TENANT_ID_ | varchar(255) | YES |  |  |  |
| DEPLOY_TIME_ | timestamp(3) | YES |  |  |  |

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
| ID_ | varchar(64) | NO |  | PRI |  |
| REV_ | int(11) | YES |  |  |  |
| NAME_ | varchar(255) | YES |  |  |  |
| KEY_ | varchar(255) | YES |  |  |  |
| CATEGORY_ | varchar(255) | YES |  |  |  |
| VERSION_ | int(11) | YES |  |  |  |
| META_INFO_ | varchar(4000) | YES |  |  |  |
| DEPLOYMENT_ID_ | varchar(64) | YES |  | MUL |  |
| EDITOR_SOURCE_VALUE_ID_ | varchar(64) | YES |  | MUL |  |
| EDITOR_SOURCE_EXTRA_VALUE_ID_ | varchar(64) | YES |  | MUL |  |
| TENANT_ID_ | varchar(255) | YES |  |  |  |
| CREATE_TIME_ | timestamp(3) | YES |  |  |  |
| LAST_UPDATE_TIME_ | timestamp(3) | YES |  |  |  |

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
| ID_ | varchar(64) | NO |  | PRI |  |
| REV_ | int(11) | YES |  |  |  |
| CATEGORY_ | varchar(255) | YES |  |  |  |
| NAME_ | varchar(255) | YES |  |  |  |
| KEY_ | varchar(255) | NO |  | MUL |  |
| VERSION_ | int(11) | NO |  |  |  |
| DEPLOYMENT_ID_ | varchar(64) | YES |  |  |  |
| RESOURCE_NAME_ | varchar(4000) | YES |  |  |  |
| DGRM_RESOURCE_NAME_ | varchar(4000) | YES |  |  |  |
| DESCRIPTION_ | varchar(4000) | YES |  |  |  |
| HAS_START_FORM_KEY_ | tinyint(4) | YES |  |  |  |
| SUSPENSION_STATE_ | int(11) | YES |  |  |  |
| TENANT_ID_ | varchar(255) | YES |  |  |  |
| HAS_GRAPHICAL_NOTATION_ | tinyint(4) | YES |  |  |  |

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
| ID_ | varchar(64) | NO |  | PRI |  |
| REV_ | int(11) | YES |  |  |  |
| EVENT_TYPE_ | varchar(255) | NO |  |  |  |
| EVENT_NAME_ | varchar(255) | YES |  |  |  |
| EXECUTION_ID_ | varchar(64) | YES |  | MUL |  |
| PROC_INST_ID_ | varchar(64) | YES |  |  |  |
| ACTIVITY_ID_ | varchar(64) | YES |  |  |  |
| CONFIGURATION_ | varchar(255) | YES |  | MUL |  |
| TENANT_ID_ | varchar(255) | YES |  |  |  |
| PROC_DEF_ID_ | varchar(64) | YES |  |  |  |
| CREATED_ | timestamp(3) | NO | CURRENT_TIMESTAMP(3) |  |  |

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
| ID_ | varchar(64) | NO |  | PRI |  |
| REV_ | int(11) | YES |  |  |  |
| PROC_INST_ID_ | varchar(64) | YES |  | MUL |  |
| BUSINESS_KEY_ | varchar(255) | YES |  | MUL |  |
| PARENT_ID_ | varchar(64) | YES |  | MUL |  |
| PROC_DEF_ID_ | varchar(64) | YES |  | MUL |  |
| SUPER_EXEC_ | varchar(64) | YES |  | MUL |  |
| ACT_ID_ | varchar(255) | YES |  |  |  |
| IS_ACTIVE_ | tinyint(4) | YES |  |  |  |
| IS_CONCURRENT_ | tinyint(4) | YES |  |  |  |
| IS_SCOPE_ | tinyint(4) | YES |  |  |  |
| IS_EVENT_SCOPE_ | tinyint(4) | YES |  |  |  |
| SUSPENSION_STATE_ | int(11) | YES |  |  |  |
| CACHED_ENT_STATE_ | int(11) | YES |  |  |  |
| TENANT_ID_ | varchar(255) | YES |  |  |  |
| NAME_ | varchar(255) | YES |  |  |  |
| LOCK_TIME_ | timestamp(3) | YES |  |  |  |

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
| ID_ | varchar(64) | NO |  | PRI |  |
| REV_ | int(11) | YES |  |  |  |
| GROUP_ID_ | varchar(255) | YES |  | MUL |  |
| TYPE_ | varchar(255) | YES |  |  |  |
| USER_ID_ | varchar(255) | YES |  | MUL |  |
| TASK_ID_ | varchar(64) | YES |  | MUL |  |
| PROC_INST_ID_ | varchar(64) | YES |  | MUL |  |
| PROC_DEF_ID_ | varchar(64) | YES |  | MUL |  |

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
| ID_ | varchar(64) | NO |  | PRI |  |
| REV_ | int(11) | YES |  |  |  |
| TYPE_ | varchar(255) | NO |  |  |  |
| LOCK_OWNER_ | varchar(255) | YES |  |  |  |
| EXCLUSIVE_ | tinyint(1) | YES |  |  |  |
| EXECUTION_ID_ | varchar(64) | YES |  |  |  |
| PROCESS_INSTANCE_ID_ | varchar(64) | YES |  |  |  |
| PROC_DEF_ID_ | varchar(64) | YES |  |  |  |
| RETRIES_ | int(11) | YES |  |  |  |
| EXCEPTION_STACK_ID_ | varchar(64) | YES |  | MUL |  |
| EXCEPTION_MSG_ | varchar(4000) | YES |  |  |  |
| REPEAT_ | varchar(255) | YES |  |  |  |
| HANDLER_TYPE_ | varchar(255) | YES |  |  |  |
| HANDLER_CFG_ | varchar(4000) | YES |  |  |  |
| TENANT_ID_ | varchar(255) | YES |  |  |  |
| LOCK_EXP_TIME_ | timestamp(3) | YES |  |  |  |
| DUEDATE_ | timestamp(3) | YES |  |  |  |

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
| ID_ | varchar(64) | NO |  | PRI |  |
| REV_ | int(11) | YES |  |  |  |
| EXECUTION_ID_ | varchar(64) | YES |  | MUL |  |
| PROC_INST_ID_ | varchar(64) | YES |  | MUL |  |
| PROC_DEF_ID_ | varchar(64) | YES |  | MUL |  |
| NAME_ | varchar(255) | YES |  |  |  |
| PARENT_TASK_ID_ | varchar(64) | YES |  |  |  |
| DESCRIPTION_ | varchar(4000) | YES |  |  |  |
| TASK_DEF_KEY_ | varchar(255) | YES |  |  |  |
| OWNER_ | varchar(255) | YES |  |  |  |
| ASSIGNEE_ | varchar(255) | YES |  | MUL |  |
| DELEGATION_ | varchar(64) | YES |  |  |  |
| PRIORITY_ | int(11) | YES |  |  |  |
| SUSPENSION_STATE_ | int(11) | YES |  |  |  |
| CATEGORY_ | varchar(255) | YES |  |  |  |
| TENANT_ID_ | varchar(255) | YES |  |  |  |
| CREATE_TIME_ | timestamp(3) | YES |  |  |  |
| DUE_DATE_ | datetime(3) | YES |  |  |  |
| FORM_KEY_ | varchar(255) | YES |  |  |  |

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
| ID_ | varchar(64) | NO |  | PRI |  |
| REV_ | int(11) | YES |  |  |  |
| TYPE_ | varchar(255) | NO |  |  |  |
| NAME_ | varchar(255) | NO |  |  |  |
| EXECUTION_ID_ | varchar(64) | YES |  | MUL |  |
| PROC_INST_ID_ | varchar(64) | YES |  | MUL |  |
| TASK_ID_ | varchar(64) | YES |  | MUL |  |
| BYTEARRAY_ID_ | varchar(64) | YES |  | MUL |  |
| DOUBLE_ | double | YES |  |  |  |
| LONG_ | bigint(20) | YES |  |  |  |
| TEXT_ | varchar(4000) | YES |  |  |  |
| TEXT2_ | varchar(4000) | YES |  |  |  |

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
| ocrCode | varchar(25) | NO |  |  |  |
| ocrName | varchar(25) | NO |  |  |  |
| isparam | int(11) | YES |  |  |  |

---

### dp_v_spms_item_basic_info

**对象类型**：VIEW

**业务含义**：数据平台SPMS物料基本信息视图 - 提供物料基本信息的统一查询视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| itemCode | varchar(25) | YES |  |  |  |
| itemName | varchar(255) | YES |  |  |  |

---

### dp_v_spms_rma_remind

**对象类型**：VIEW

**业务含义**：数据平台SPMS RMA提醒视图 - 提供RMA退货提醒信息的统一查询视图

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| spare_serialNum | varchar(50) | YES |  |  |  |
| sheetID | varchar(25) | YES |  |  |  |
| back_type | varchar(50) | YES |  |  |  |
| item_name | varchar(255) | YES |  |  |  |
| project_name | varchar(255) | YES |  |  |  |
| problem_desc | text | YES |  |  |  |
| conk_out_time | varchar(25) | YES |  |  |  |
| approve_time | datetime | YES |  |  |  |

---

### ehr_company

**业务含义**：EHR公司表 - 存储公司组织信息

**数据量**：约3行 | 数据大小：0MB | 索引大小：0MB

**字段列表**：

| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |
|--------|------|------|--------|-----|----------|
| compID | int(11) | NO |  | PRI |  |
| compCode | varchar(10) | YES |  |  |  |
| compName | varchar(100) | YES |  |  |  |
| compAbbr | varchar(100) | YES |  |  |  |
| adminID | int(11) | YES |  | MUL |  |
| compGrade | int(11) | YES |  |  |  |
| compType | int(11) | YES |  |  |  |
| compArea | int(11) | YES |  |  |  |
| effectDate | datetime | YES |  |  |  |
| lawyer | varchar(50) | YES |  |  |  |
| address | varchar(200) | YES |  |  |  |
| regAddress | varchar(200) | YES |  |  |  |
| tel | varchar(50) | YES |  |  |  |
| fax | varchar(50) | YES |  |  |  |
| postCode | varchar(50) | YES |  |  |  |
| webSite | varchar(100) | YES |  |  |  |
| isDisabled | bit(1) | YES |  |  |  |
| disabledDate | datetime | YES |  |  |  |
| remark | varchar(500) | YES |  |  |  |

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
| depID | int(11) | NO |  | PRI |  |
| depCode | varchar(20) | YES |  |  |  |
| depName | varchar(100) | YES |  |  |  |
| depAbbr | varchar(100) | YES |  |  |  |
| compID | int(11) | YES |  | MUL |  |
| adminID | int(11) | YES |  | MUL |  |
| depGrade | int(11) | YES |  |  |  |
| depType | int(11) | YES |  |  |  |
| depProperty | int(11) | YES |  |  |  |
| depCost | int(11) | YES |  |  |  |
| director | int(11) | YES |  | MUL |  |
| director2 | int(11) | YES |  | MUL |  |
| depEmp | int(11) | YES |  |  |  |
| depNum | int(11) | YES |  |  |  |
| effectDate | datetime | YES |  |  |  |
| xOrder | varchar(20) | YES |  |  |  |
| isDisabled | bit(1) | YES |  |  |  |
| disabledDate | datetime | YES |  |  |  |
| remark | varchar(500) | YES |  |  |  |
| depCustom1 | int(11) | YES |  |  |  |
| depCustom2 | int(11) | YES |  |  |  |
| depCustom3 | int(11) | YES |  |  |  |
| depCustom4 | int(11) | YES |  |  |  |
| depCustom5 | int(11) | YES |  |  |  |

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
| empID | int(11) | NO |  | PRI |  |
| workNo | varchar(100) | NO |  | MUL |  |
| name | varchar(200) | YES |  |  |  |
| eName | varchar(200) | YES |  |  |  |
| compID | int(11) | NO |  | MUL |  |
| depID | int(11) | NO |  | MUL |  |
| jobID | int(11) | NO |  | MUL |  |
| reportTo | int(11) | YES |  | MUL |  |
| wfreportTo | int(11) | YES |  | MUL |  |
| empStatus | int(11) | NO |  |  |  |
| jobStatus | int(11) | YES |  |  |  |
| empType | int(11) | YES |  |  |  |
| joinDate | datetime | YES |  |  |  |
| workBeginDate | datetime | YES |  |  |  |
| jobBeginDate | datetime | YES |  |  |  |
| pracBeginDate | datetime | YES |  |  |  |
| pracEndDate | datetime | YES |  |  |  |
| probBeginDate | datetime | YES |  |  |  |
| probEndDate | datetime | YES |  |  |  |
| leaveDate | datetime | YES |  |  |  |
| gender | int(11) | YES |  |  |  |
| email | varchar(500) | YES |  |  |  |
| mobile | varchar(50) | YES |  |  |  |
| officePhone | varchar(50) | YES |  |  |  |
| remark | varchar(100) | YES |  |  |  |
| disabled | int(11) | YES | 0 |  |  |
| empCustom1 | int(11) | YES |  |  |  |
| empCustom2 | int(11) | YES |  |  |  |
| empCustom3 | int(11) | YES |  |  |  |
| empCustom4 | varchar(50) | YES |  |  |  |
| empCustom5 | int(11) | YES |  |  |  |

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
| id | int(11) | NO |  | PRI |  |
| empID | int(11) | NO |  | UNI |  |
| workNo | varchar(25) | NO |  | MUL |  |
| compID | int(11) | NO |  |  |  |
| depIDs | varchar(4096) | NO |  |  |  |
| extraDepIDs | varchar(4096) | NO |  |  |  |
| adminDepIDs | varchar(4096) | NO |  |  |  |
| empIDs | varchar(4096) | NO |  |  |  |
| extraEmpIDs | varchar(4096) | NO |  |  |  |
| state | bit(1) | NO | b'1' |  |  |
| createBy | varchar(25) | NO |  |  |  |
| createTime | datetime | NO | CURRENT_TIMESTAMP |  |  |
| updateBy | varchar(25) | YES |  |  |  |
| updateTime | datetime | YES |  |  |  |

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
| jobID | int(11) | NO |  | PRI |  |
| jobCode | varchar(10) | YES |  |  |  |
| jobName | varchar(100) | YES |  |  |  |
| jobAbbr | varchar(100) | YES |  |  |  |
| depID | int(11) | YES |  | MUL |  |
| adminID | int(11) | YES |  | MUL |  |
| jobGrage | int(11) | YES |  |  |  |
| jobType | int(11) | YES |  |  |  |
| jobProperty | int(11) | YES |  |  |  |
| jobNum | int(11) | YES |  |  |  |
| isCore | bit(1) | YES | b'0' |  |  |
| effectDate | datetime | NO |  |  |  |
| xorder | varchar(20) | YES |  |  |  |
| isDisabled | bit(1) | YES | b'0' |  |  |
| disabledDate | datetime | YES |  |  |  |
| remark | varchar(500) | YES |  |  |  |
| xType | int(11) | YES |  |  |  |
| jobCustom1 | int(11) | YES |  |  |  |
| jobCustom2 | int(11) | YES |  |  |  |
| jobCustom3 | int(11) | YES |  |  |  |
| jobCustom4 | int(11) | YES |  |  |  |
| jobCustom5 | int(11) | YES |  |  |  |

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
| id | int(11) | NO |  | PRI |  |
| title | varchar(255) | YES |  |  |  |
| account | varchar(255) | YES |  |  |  |
| empID | int(11) | YES |  |  |  |
| workNo | varchar(255) | YES |  |  |  |
| name | varchar(255) | YES |  |  |  |
| isDisabled | int(11) | YES |  |  |  |

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
| contract_id | varchar(64) | YES |  | MUL |  |
| contract_code | varchar(25) | YES |  | MUL |  |
| office_code | varchar(15) | YES |  | MUL |  |
| contract_type | int(11) | YES |  | MUL |  |
| customer_name | varchar(512) | YES |  |  |  |
| project_name | varchar(512) | YES |  |  |  |
| warranty | varchar(2) | YES |  |  |  |
| marketCode | varchar(10) | YES |  |  |  |
| marketName | varchar(15) | YES |  |  |  |
| systemId | int(11) | YES |  |  |  |
| systemName | varchar(15) | YES |  |  |  |
| remark | varchar(4096) | YES |  |  |  |

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
| item_id | int(11) | YES |  | MUL |  |
| serial_number | varchar(100) | YES |  |  |  |

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
| result1_id | int(11) | YES |  | MUL |  |
| result_desc | text | YES |  |  |  |

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
| id | int(11) | YES |  |  |  |
| item | varchar(25) | YES |  | MUL |  |
| describe_ | varchar(255) | YES |  |  |  |
| itemname | varchar(255) | YES |  | MUL |  |

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
| id | int(11) | YES |  |  |  |
| item | varchar(15) | YES |  |  |  |
| describe_ | varchar(150) | YES |  |  |  |
| itemname | varchar(255) | YES |  |  |  |

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
| marketCode | varchar(10) | YES |  |  |  |
| marketName | varchar(15) | YES |  |  |  |
| systemId | int(11) | YES |  |  |  |
| systemName | varchar(15) | YES |  |  |  |

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
| id | int(11) | NO |  | PRI |  |
| contractNo | varchar(100) | YES |  | MUL |  |
| officeCode | varchar(25) | YES |  |  |  |

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
| id | varchar(64) | YES |  | MUL |  |
| con_xb | varchar(25) | YES |  | MUL |  |
| barcode | varchar(50) | YES |  | MUL |  |
| grade | varchar(15) | YES |  |  |  |
| begin_date | datetime | YES |  |  |  |
| end_date | datetime | YES |  |  |  |
| warranty | char(1) | YES |  |  |  |
| remark | text | YES |  |  |  |
| isyb | int(11) | YES | 1 |  |  |
| state | int(11) | YES |  |  |  |

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
| packlist_id | varchar(64) | YES |  | MUL |  |
| con_id | varchar(64) | YES |  | MUL |  |
| packdate | datetime | YES |  | MUL |  |
| warrantyStartTime | datetime | YES |  |  |  |
| warrantyEndTime | datetime | YES |  |  |  |
| receiveName | text | YES |  |  |  |
| emsNum | text | YES |  |  |  |
| emsCompany | text | YES |  |  |  |

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
| id | bigint(20) | NO |  | PRI |  |
| pack_id | varchar(64) | YES |  | MUL |  |
| item | varchar(16) | YES |  | MUL |  |
| barcode | varchar(50) | YES |  | MUL |  |
| com_barcode | varchar(50) | YES |  |  |  |
| rma_no | varchar(64) | YES |  |  |  |
| isRMA | int(11) | YES |  |  |  |
| item2 | varchar(16) | YES |  | MUL |  |
| barcode2 | varchar(50) | YES |  | MUL |  |
| orderNumber | varchar(32) | YES |  |  |  |
| lineNum | int(11) | YES |  |  |  |
| profitCenter | varchar(32) | YES |  |  |  |
| soleAgentSuffix | varchar(32) | YES |  |  |  |
| warrantyStartDate | date | YES |  |  |  |
| warrantyMonth | int(11) | YES |  |  |  |
| rmaBarcode | varchar(50) | YES |  |  |  |
| updateTime | datetime | YES |  |  |  |
| syncTime | datetime | YES | CURRENT_TIMESTAMP |  |  |
| uuid | varchar(64) | YES |  | UNI |  |

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
| logID | bigint(20) | NO |  | PRI |  |
| tableName | varchar(128) | YES |  |  |  |
| operation | varchar(50) | YES |  |  |  |
| changedBy | varchar(128) | YES |  |  |  |
| changeTime | datetime | YES |  |  |  |
| dataId | varchar(128) | YES |  | MUL |  |
| barCode | varchar(128) | YES |  |  |  |
| lasted | smallint(6) | YES |  | MUL |  |
| oldValues | longtext | YES |  |  |  |
| newValues | longtext | YES |  |  |  |
| syncTime | datetime | YES | CURRENT_TIMESTAMP |  |  |
| syncFlag | smallint(6) | YES | 0 | MUL |  |

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
| pack_id | varchar(64) | YES |  | MUL |  |
| packlist_no | varchar(64) | YES |  |  |  |
| barcode | varchar(50) | YES |  | MUL |  |
| contractNo | varchar(50) | YES |  |  |  |
| orderNumber | varchar(32) | YES |  | MUL |  |
| lineNum | int(11) | YES |  |  |  |
| orderQty | int(11) | YES |  |  |  |
| deliveredQty | int(11) | YES |  |  |  |
| profitCenter | varchar(32) | YES |  |  |  |
| orderExecNumber | varchar(50) | YES |  |  |  |
| soleAgentSuffix | varchar(32) | YES |  |  |  |
| warrantyMonth | int(11) | YES | 0 |  |  |

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
| id | int(11) | NO |  | PRI |  |
| sn1 | varchar(50) | YES |  | MUL |  |
| item1 | varchar(15) | YES |  | MUL |  |
| sn2 | varchar(50) | YES |  | MUL |  |
| item2 | varchar(15) | YES |  | MUL |  |
| contract | varchar(25) | YES |  | MUL |  |
| createtime | varchar(50) | YES |  |  |  |
| updatetime | varchar(50) | YES |  |  |  |

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
| serial_number | varchar(100) | YES |  | MUL |  |
| conp | varchar(100) | YES |  | MUL |  |
| cpld | varchar(100) | YES |  | MUL |  |
| boot | varchar(100) | YES |  | MUL |  |
| pcb | varchar(100) | YES |  | MUL |  |

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
| id | int(11) | NO |  | PRI |  |
| gradecode | varchar(25) | YES |  | MUL |  |
| gradename | varchar(125) | YES |  |  |  |
| gradestatus | int(11) | YES | 0 |  |  |
| sort | int(3) | YES | 0 |  |  |
| effectiveFrom | datetime | YES |  |  |  |
| effectiveTo | datetime | YES |  |  |  |

**索引列表**：

| 索引名 | 列 | 唯一性 | 索引类型 |
|--------|-----|--------|----------|
| gradecode | gradecode | 否 | BTREE |
| PRIMARY | id | 是 | BTREE |

---
