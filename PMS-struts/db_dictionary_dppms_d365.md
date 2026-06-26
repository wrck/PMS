# dppms_d365 数据库完整数据字典

数据库: dppms_d365
表总数: 273
生成时间: 2026-06-13

### 1 act_evt_log -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~6398 行 |
| 数据大小 | 17.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| LOG_NR_ | bigint(20) | NO | - | PRI, auto_increment | - | 业务含义待确认 |
| TYPE_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| PROC_DEF_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| PROC_INST_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| EXECUTION_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| TASK_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| TIME_STAMP_ | timestamp(3) | NO | CURRENT_TIMESTAMP(3) | - | - | 业务含义待确认 |
| USER_ID_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| DATA_ | longblob | YES | - | - | - | 业务含义待确认 |
| LOCK_OWNER_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| LOCK_TIME_ | timestamp(3) | YES | - | - | - | 业务含义待确认 |
| IS_PROCESSED_ | tinyint(4) | YES | 0 | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | LOG_NR_ |

---

### 2 act_ge_bytearray -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~1210 行 |
| 数据大小 | 6.50 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO |  | PRI | - | 业务含义待确认 |
| REV_ | int(11) | YES | - | - | - | 业务含义待确认 |
| NAME_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| DEPLOYMENT_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| BYTES_ | longblob | YES | - | - | - | 业务含义待确认 |
| GENERATED_ | tinyint(4) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_FK_BYTEARR_DEPL | BTREE | NON-UNIQUE | DEPLOYMENT_ID_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 3 act_ge_property -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~3 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| NAME_ | varchar(64) | NO |  | PRI | - | 业务含义待确认 |
| VALUE_ | varchar(300) | YES | - | - | - | 业务含义待确认 |
| REV_ | int(11) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | NAME_ |

---

### 4 act_hi_actinst -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~155861 行 |
| 数据大小 | 22.55 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI | - | 业务含义待确认 |
| PROC_DEF_ID_ | varchar(64) | NO | - | - | - | 业务含义待确认 |
| PROC_INST_ID_ | varchar(64) | NO | - | MUL | - | 业务含义待确认 |
| EXECUTION_ID_ | varchar(64) | NO | - | MUL | - | 业务含义待确认 |
| ACT_ID_ | varchar(255) | NO | - | - | - | 业务含义待确认 |
| TASK_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| CALL_PROC_INST_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| ACT_NAME_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| ACT_TYPE_ | varchar(255) | NO | - | - | - | 业务含义待确认 |
| ASSIGNEE_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| DURATION_ | bigint(20) | YES | - | - | - | 业务含义待确认 |
| TENANT_ID_ | varchar(255) | YES |  | - | - | 业务含义待确认 |
| START_TIME_ | datetime(3) | NO | - | - | - | 业务含义待确认 |
| END_TIME_ | datetime(3) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_IDX_HI_ACT_INST_EXEC | BTREE | NON-UNIQUE | EXECUTION_ID_,ACT_ID_ |
| ACT_IDX_HI_ACT_INST_PROCINST | BTREE | NON-UNIQUE | PROC_INST_ID_,ACT_ID_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 5 act_hi_attachment -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~0 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI | - | 业务含义待确认 |
| REV_ | int(11) | YES | - | - | - | 业务含义待确认 |
| USER_ID_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| NAME_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| DESCRIPTION_ | varchar(4000) | YES | - | - | - | 业务含义待确认 |
| TYPE_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| TASK_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| PROC_INST_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| URL_ | varchar(4000) | YES | - | - | - | 业务含义待确认 |
| CONTENT_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| TIME_ | datetime(3) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 6 act_hi_comment -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~66552 行 |
| 数据大小 | 7.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI | - | 业务含义待确认 |
| TYPE_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| USER_ID_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| TASK_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| PROC_INST_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| ACTION_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| MESSAGE_ | varchar(4000) | YES | - | - | - | 业务含义待确认 |
| FULL_MSG_ | longblob | YES | - | - | - | 业务含义待确认 |
| TIME_ | datetime(3) | NO | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 7 act_hi_detail -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~0 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI | - | 业务含义待确认 |
| TYPE_ | varchar(255) | NO | - | - | - | 业务含义待确认 |
| PROC_INST_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| EXECUTION_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| TASK_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| ACT_INST_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| NAME_ | varchar(255) | NO | - | MUL | - | 业务含义待确认 |
| VAR_TYPE_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| REV_ | int(11) | YES | - | - | - | 业务含义待确认 |
| BYTEARRAY_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| DOUBLE_ | double | YES | - | - | - | 业务含义待确认 |
| LONG_ | bigint(20) | YES | - | - | - | 业务含义待确认 |
| TEXT_ | varchar(4000) | YES | - | - | - | 业务含义待确认 |
| TEXT2_ | varchar(4000) | YES | - | - | - | 业务含义待确认 |
| TIME_ | datetime(3) | NO | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_IDX_HI_DETAIL_ACT_INST | BTREE | NON-UNIQUE | ACT_INST_ID_ |
| ACT_IDX_HI_DETAIL_NAME | BTREE | NON-UNIQUE | NAME_ |
| ACT_IDX_HI_DETAIL_PROC_INST | BTREE | NON-UNIQUE | PROC_INST_ID_ |
| ACT_IDX_HI_DETAIL_TASK_ID | BTREE | NON-UNIQUE | TASK_ID_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 8 act_hi_identitylink -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~143081 行 |
| 数据大小 | 8.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO |  | PRI | - | 业务含义待确认 |
| GROUP_ID_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| TYPE_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| USER_ID_ | varchar(255) | YES | - | MUL | - | 业务含义待确认 |
| TASK_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| PROC_INST_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_IDX_HI_IDENT_LNK_PROCINST | BTREE | NON-UNIQUE | PROC_INST_ID_ |
| ACT_IDX_HI_IDENT_LNK_TASK | BTREE | NON-UNIQUE | TASK_ID_ |
| ACT_IDX_HI_IDENT_LNK_USER | BTREE | NON-UNIQUE | USER_ID_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 9 act_hi_procinst -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~18833 行 |
| 数据大小 | 2.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI | - | 业务含义待确认 |
| PROC_INST_ID_ | varchar(64) | NO | - | UNI | - | 业务含义待确认 |
| BUSINESS_KEY_ | varchar(255) | YES | - | MUL | - | 业务含义待确认 |
| PROC_DEF_ID_ | varchar(64) | NO | - | - | - | 业务含义待确认 |
| DURATION_ | bigint(20) | YES | - | - | - | 业务含义待确认 |
| START_USER_ID_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| START_ACT_ID_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| END_ACT_ID_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| SUPER_PROCESS_INSTANCE_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| DELETE_REASON_ | varchar(4000) | YES | - | - | - | 业务含义待确认 |
| TENANT_ID_ | varchar(255) | YES |  | - | - | 业务含义待确认 |
| START_TIME_ | datetime(3) | NO | - | - | - | 业务含义待确认 |
| END_TIME_ | datetime(3) | YES | - | - | - | 业务含义待确认 |
| NAME_ | varchar(255) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_IDX_HI_PRO_I_BUSKEY | BTREE | NON-UNIQUE | BUSINESS_KEY_ |
| PRIMARY | BTREE | UNIQUE | ID_ |
| PROC_INST_ID_ | BTREE | UNIQUE | PROC_INST_ID_ |

---

### 10 act_hi_taskinst -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~67984 行 |
| 数据大小 | 10.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI | - | 业务含义待确认 |
| PROC_DEF_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| TASK_DEF_KEY_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| PROC_INST_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| EXECUTION_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| NAME_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| PARENT_TASK_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| DESCRIPTION_ | varchar(4000) | YES | - | - | - | 业务含义待确认 |
| OWNER_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| ASSIGNEE_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| DURATION_ | bigint(20) | YES | - | - | - | 业务含义待确认 |
| DELETE_REASON_ | varchar(4000) | YES | - | - | - | 业务含义待确认 |
| PRIORITY_ | int(11) | YES | - | - | - | 业务含义待确认 |
| FORM_KEY_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| CATEGORY_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| TENANT_ID_ | varchar(255) | YES |  | - | - | 业务含义待确认 |
| START_TIME_ | datetime(3) | NO | - | - | - | 业务含义待确认 |
| CLAIM_TIME_ | datetime(3) | YES | - | - | - | 业务含义待确认 |
| END_TIME_ | datetime(3) | YES | - | - | - | 业务含义待确认 |
| DUE_DATE_ | datetime(3) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_IDX_HI_TASK_INST_PROCINST | BTREE | NON-UNIQUE | PROC_INST_ID_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 11 act_hi_varinst -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~204674 行 |
| 数据大小 | 18.55 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI | - | 业务含义待确认 |
| PROC_INST_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| EXECUTION_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| TASK_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| NAME_ | varchar(255) | NO | - | MUL | - | 业务含义待确认 |
| VAR_TYPE_ | varchar(100) | YES | - | - | - | 业务含义待确认 |
| REV_ | int(11) | YES | - | - | - | 业务含义待确认 |
| BYTEARRAY_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| DOUBLE_ | double | YES | - | - | - | 业务含义待确认 |
| LONG_ | bigint(20) | YES | - | - | - | 业务含义待确认 |
| TEXT_ | varchar(4000) | YES | - | - | - | 业务含义待确认 |
| TEXT2_ | varchar(4000) | YES | - | - | - | 业务含义待确认 |
| CREATE_TIME_ | datetime(3) | YES | - | - | - | 业务含义待确认 |
| LAST_UPDATED_TIME_ | datetime(3) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_IDX_HI_PROCVAR_NAME_TYPE | BTREE | NON-UNIQUE | NAME_,VAR_TYPE_ |
| ACT_IDX_HI_PROCVAR_PROC_INST | BTREE | NON-UNIQUE | PROC_INST_ID_ |
| ACT_IDX_HI_PROCVAR_TASK_ID | BTREE | NON-UNIQUE | TASK_ID_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 12 act_id_group -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~12 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO |  | PRI | - | 业务含义待确认 |
| REV_ | int(11) | YES | - | - | - | 业务含义待确认 |
| NAME_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| TYPE_ | varchar(255) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 13 act_id_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~0 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO |  | PRI | - | 业务含义待确认 |
| REV_ | int(11) | YES | - | - | - | 业务含义待确认 |
| USER_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| TYPE_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| KEY_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| VALUE_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| PASSWORD_ | longblob | YES | - | - | - | 业务含义待确认 |
| PARENT_ID_ | varchar(255) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 14 act_id_membership -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~548 行 |
| 数据大小 | 48.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| USER_ID_ | varchar(64) | NO |  | PRI | - | 业务含义待确认 |
| GROUP_ID_ | varchar(64) | NO |  | PRI | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_FK_MEMB_GROUP | BTREE | NON-UNIQUE | GROUP_ID_ |
| PRIMARY | BTREE | UNIQUE | USER_ID_,GROUP_ID_ |

---

### 15 act_id_user -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~201 行 |
| 数据大小 | 48.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO |  | PRI | - | 业务含义待确认 |
| REV_ | int(11) | YES | - | - | - | 业务含义待确认 |
| FIRST_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| LAST_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| EMAIL_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| PWD_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| PICTURE_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 16 act_procdef_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~0 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI | - | 业务含义待确认 |
| PROC_DEF_ID_ | varchar(64) | NO | - | UNI | - | 业务含义待确认 |
| REV_ | int(11) | YES | - | - | - | 业务含义待确认 |
| INFO_JSON_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_FK_INFO_JSON_BA | BTREE | NON-UNIQUE | INFO_JSON_ID_ |
| ACT_IDX_INFO_PROCDEF | BTREE | NON-UNIQUE | PROC_DEF_ID_ |
| ACT_UNIQ_INFO_PROCDEF | BTREE | UNIQUE | PROC_DEF_ID_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 17 act_re_deployment -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~27 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO |  | PRI | - | 业务含义待确认 |
| NAME_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| CATEGORY_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| TENANT_ID_ | varchar(255) | YES |  | - | - | 业务含义待确认 |
| DEPLOY_TIME_ | timestamp(3) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 18 act_re_model -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~6 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI | - | 业务含义待确认 |
| REV_ | int(11) | YES | - | - | - | 业务含义待确认 |
| NAME_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| KEY_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| CATEGORY_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| VERSION_ | int(11) | YES | - | - | - | 业务含义待确认 |
| META_INFO_ | varchar(4000) | YES | - | - | - | 业务含义待确认 |
| DEPLOYMENT_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| EDITOR_SOURCE_VALUE_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| EDITOR_SOURCE_EXTRA_VALUE_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| TENANT_ID_ | varchar(255) | YES |  | - | - | 业务含义待确认 |
| CREATE_TIME_ | timestamp(3) | YES | - | - | - | 业务含义待确认 |
| LAST_UPDATE_TIME_ | timestamp(3) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_FK_MODEL_DEPLOYMENT | BTREE | NON-UNIQUE | DEPLOYMENT_ID_ |
| ACT_FK_MODEL_SOURCE | BTREE | NON-UNIQUE | EDITOR_SOURCE_VALUE_ID_ |
| ACT_FK_MODEL_SOURCE_EXTRA | BTREE | NON-UNIQUE | EDITOR_SOURCE_EXTRA_VALUE_ID_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 19 act_re_procdef -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~27 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI | - | 业务含义待确认 |
| REV_ | int(11) | YES | - | - | - | 业务含义待确认 |
| CATEGORY_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| NAME_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| KEY_ | varchar(255) | NO | - | MUL | - | 业务含义待确认 |
| VERSION_ | int(11) | NO | - | - | - | 业务含义待确认 |
| DEPLOYMENT_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| RESOURCE_NAME_ | varchar(4000) | YES | - | - | - | 业务含义待确认 |
| DGRM_RESOURCE_NAME_ | varchar(4000) | YES | - | - | - | 业务含义待确认 |
| DESCRIPTION_ | varchar(4000) | YES | - | - | - | 业务含义待确认 |
| HAS_START_FORM_KEY_ | tinyint(4) | YES | - | - | - | 业务含义待确认 |
| SUSPENSION_STATE_ | int(11) | YES | - | - | - | 业务含义待确认 |
| TENANT_ID_ | varchar(255) | YES |  | - | - | 业务含义待确认 |
| HAS_GRAPHICAL_NOTATION_ | tinyint(4) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_UNIQ_PROCDEF | BTREE | UNIQUE | KEY_,VERSION_,TENANT_ID_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 20 act_ru_event_subscr -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~0 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI | - | 业务含义待确认 |
| REV_ | int(11) | YES | - | - | - | 业务含义待确认 |
| EVENT_TYPE_ | varchar(255) | NO | - | - | - | 业务含义待确认 |
| EVENT_NAME_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| EXECUTION_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| PROC_INST_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| ACTIVITY_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| CONFIGURATION_ | varchar(255) | YES | - | MUL | - | 业务含义待确认 |
| TENANT_ID_ | varchar(255) | YES |  | - | - | 业务含义待确认 |
| PROC_DEF_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| CREATED_ | timestamp(3) | NO | CURRENT_TIMESTAMP(3) | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_FK_EVENT_EXEC | BTREE | NON-UNIQUE | EXECUTION_ID_ |
| ACT_IDX_EVENT_SUBSCR_CONFIG_ | BTREE | NON-UNIQUE | CONFIGURATION_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 21 act_ru_execution -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~3554 行 |
| 数据大小 | 432.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO |  | PRI | - | 业务含义待确认 |
| REV_ | int(11) | YES | - | - | - | 业务含义待确认 |
| PROC_INST_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| BUSINESS_KEY_ | varchar(255) | YES | - | MUL | - | 业务含义待确认 |
| PARENT_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| PROC_DEF_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| SUPER_EXEC_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| ACT_ID_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| IS_ACTIVE_ | tinyint(4) | YES | - | - | - | 业务含义待确认 |
| IS_CONCURRENT_ | tinyint(4) | YES | - | - | - | 业务含义待确认 |
| IS_SCOPE_ | tinyint(4) | YES | - | - | - | 业务含义待确认 |
| IS_EVENT_SCOPE_ | tinyint(4) | YES | - | - | - | 业务含义待确认 |
| SUSPENSION_STATE_ | int(11) | YES | - | - | - | 业务含义待确认 |
| CACHED_ENT_STATE_ | int(11) | YES | - | - | - | 业务含义待确认 |
| TENANT_ID_ | varchar(255) | YES |  | - | - | 业务含义待确认 |
| NAME_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| LOCK_TIME_ | timestamp(3) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_FK_EXE_PARENT | BTREE | NON-UNIQUE | PARENT_ID_ |
| ACT_FK_EXE_PROCDEF | BTREE | NON-UNIQUE | PROC_DEF_ID_ |
| ACT_FK_EXE_PROCINST | BTREE | NON-UNIQUE | PROC_INST_ID_ |
| ACT_FK_EXE_SUPER | BTREE | NON-UNIQUE | SUPER_EXEC_ |
| ACT_IDX_EXEC_BUSKEY | BTREE | NON-UNIQUE | BUSINESS_KEY_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 22 act_ru_identitylink -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~23219 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO |  | PRI | - | 业务含义待确认 |
| REV_ | int(11) | YES | - | - | - | 业务含义待确认 |
| GROUP_ID_ | varchar(255) | YES | - | MUL | - | 业务含义待确认 |
| TYPE_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| USER_ID_ | varchar(255) | YES | - | MUL | - | 业务含义待确认 |
| TASK_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| PROC_INST_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| PROC_DEF_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_FK_IDL_PROCINST | BTREE | NON-UNIQUE | PROC_INST_ID_ |
| ACT_FK_TSKASS_TASK | BTREE | NON-UNIQUE | TASK_ID_ |
| ACT_IDX_ATHRZ_PROCEDEF | BTREE | NON-UNIQUE | PROC_DEF_ID_ |
| ACT_IDX_IDENT_LNK_GROUP | BTREE | NON-UNIQUE | GROUP_ID_ |
| ACT_IDX_IDENT_LNK_USER | BTREE | NON-UNIQUE | USER_ID_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 23 act_ru_job -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~0 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI | - | 业务含义待确认 |
| REV_ | int(11) | YES | - | - | - | 业务含义待确认 |
| TYPE_ | varchar(255) | NO | - | - | - | 业务含义待确认 |
| LOCK_OWNER_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| EXCLUSIVE_ | tinyint(1) | YES | - | - | - | 业务含义待确认 |
| EXECUTION_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| PROCESS_INSTANCE_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| PROC_DEF_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| RETRIES_ | int(11) | YES | - | - | - | 业务含义待确认 |
| EXCEPTION_STACK_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| EXCEPTION_MSG_ | varchar(4000) | YES | - | - | - | 业务含义待确认 |
| REPEAT_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| HANDLER_TYPE_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| HANDLER_CFG_ | varchar(4000) | YES | - | - | - | 业务含义待确认 |
| TENANT_ID_ | varchar(255) | YES |  | - | - | 业务含义待确认 |
| LOCK_EXP_TIME_ | timestamp(3) | YES | - | - | - | 业务含义待确认 |
| DUEDATE_ | timestamp(3) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_FK_JOB_EXCEPTION | BTREE | NON-UNIQUE | EXCEPTION_STACK_ID_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 24 act_ru_task -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~3400 行 |
| 数据大小 | 480.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO |  | PRI | - | 业务含义待确认 |
| REV_ | int(11) | YES | - | - | - | 业务含义待确认 |
| EXECUTION_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| PROC_INST_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| PROC_DEF_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| NAME_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| PARENT_TASK_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| DESCRIPTION_ | varchar(4000) | YES | - | - | - | 业务含义待确认 |
| TASK_DEF_KEY_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| OWNER_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| ASSIGNEE_ | varchar(255) | YES | - | MUL | - | 业务含义待确认 |
| DELEGATION_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| PRIORITY_ | int(11) | YES | - | - | - | 业务含义待确认 |
| SUSPENSION_STATE_ | int(11) | YES | - | - | - | 业务含义待确认 |
| CATEGORY_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| TENANT_ID_ | varchar(255) | YES |  | - | - | 业务含义待确认 |
| CREATE_TIME_ | timestamp(3) | YES | - | - | - | 业务含义待确认 |
| DUE_DATE_ | datetime(3) | YES | - | - | - | 业务含义待确认 |
| FORM_KEY_ | varchar(255) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_FK_TASK_EXE | BTREE | NON-UNIQUE | EXECUTION_ID_ |
| ACT_FK_TASK_PROCDEF | BTREE | NON-UNIQUE | PROC_DEF_ID_ |
| ACT_FK_TASK_PROCINST | BTREE | NON-UNIQUE | PROC_INST_ID_ |
| ACT_IDX_ASSIGNEE | BTREE | NON-UNIQUE | ASSIGNEE_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 25 act_ru_task_callback_task_w04649 -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~0 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO |  | - | - | 业务含义待确认 |
| REV_ | int(11) | YES | - | - | - | 业务含义待确认 |
| EXECUTION_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| PROC_INST_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| PROC_DEF_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| NAME_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| PARENT_TASK_ID_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| DESCRIPTION_ | varchar(4000) | YES | - | - | - | 业务含义待确认 |
| TASK_DEF_KEY_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| OWNER_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| ASSIGNEE_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| DELEGATION_ | varchar(64) | YES | - | - | - | 业务含义待确认 |
| PRIORITY_ | int(11) | YES | - | - | - | 业务含义待确认 |
| SUSPENSION_STATE_ | int(11) | YES | - | - | - | 业务含义待确认 |
| CATEGORY_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| TENANT_ID_ | varchar(255) | YES |  | - | - | 业务含义待确认 |
| CREATE_TIME_ | timestamp(3) | YES | - | - | - | 业务含义待确认 |
| DUE_DATE_ | datetime(3) | YES | - | - | - | 业务含义待确认 |
| FORM_KEY_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| varId | varchar(64) | YES | - | - | - | 业务含义待确认 |
| linkId | varchar(64) | YES |  | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|

---

### 26 act_ru_variable -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~42152 行 |
| 数据大小 | 4.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI | - | 业务含义待确认 |
| REV_ | int(11) | YES | - | - | - | 业务含义待确认 |
| TYPE_ | varchar(255) | NO | - | - | - | 业务含义待确认 |
| NAME_ | varchar(255) | NO | - | - | - | 业务含义待确认 |
| EXECUTION_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| PROC_INST_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| TASK_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| BYTEARRAY_ID_ | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| DOUBLE_ | double | YES | - | - | - | 业务含义待确认 |
| LONG_ | bigint(20) | YES | - | - | - | 业务含义待确认 |
| TEXT_ | varchar(4000) | YES | - | - | - | 业务含义待确认 |
| TEXT2_ | varchar(4000) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_FK_VAR_BYTEARRAY | BTREE | NON-UNIQUE | BYTEARRAY_ID_ |
| ACT_FK_VAR_EXE | BTREE | NON-UNIQUE | EXECUTION_ID_ |
| ACT_FK_VAR_PROCINST | BTREE | NON-UNIQUE | PROC_INST_ID_ |
| ACT_IDX_VARIABLE_TASK_ID | BTREE | NON-UNIQUE | TASK_ID_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 27 addressee_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
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

### 28 af_industry_asset -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
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
| createTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 29 af_industry_asset_leak_relation -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
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

### 30 af_industry_asset_project_relation -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
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

### 31 af_industry_leak -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
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
| createTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 32 af_industry_leak_warning -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
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
| createTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 33 agent_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
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

### 34 app_accessory_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
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

### 35 app_comment -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
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

### 36 app_spare_part -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~47262 行 |
| 数据大小 | 6.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| tx_id | int(11) | YES | - | UNI | - | 业务含义待确认 |
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

### 37 back_type -- 返回类型

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
| back | varchar(10) | NO | - | MUL | - | 业务含义待确认 |
| back_type | varchar(50) | YES | - | - | - | 业务含义待确认 |
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

### 38 bar -- PPS设备

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

### 39 brw_app_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
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

### 40 brw_spare_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
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

### 41 data_field_relation -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~962 行 |
| 数据大小 | 400.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| dataName | varchar(255) | NO | - | - | 数据名 | 数据名 |
| dataType | varchar(255) | NO | - | - | 数据类型 | 数据类型 |
| dataId | int(11) | YES | 0 | - | 数据实例ID | 数据实例ID |
| field | varchar(128) | NO | - | - | 字段 | 字段 |
| alias | varchar(128) | YES | - | - | 字段别名 | 字段别名 |
| name | varchar(128) | NO | - | - | 字段名 | 字段名 |
| title | varchar(255) | YES | - | - | 字段标题 | 字段标题 |
| titleKey | varchar(255) | YES | - | - | 字段标题Key | 字段标题Key |
| cssId | varchar(255) | YES | - | - | 字段CSS id | 字段CSS id |
| cssClass | varchar(255) | YES | - | - | 字段CSS class | 字段CSS class |
| cssStyle | varchar(255) | YES | - | - | 字段CSS style | 字段CSS style |
| type | varchar(255) | YES | - | - | 字段类型 | 字段类型 |
| render | varchar(4096) | YES | - | - | 字段处理 | 字段处理 |
| sort | int(11) | YES | 0 | - | 排序 | 排序 |
| orderable | bit(1) | YES | b'1' | - | 允许排序 | 允许排序 |
| searchable | bit(1) | YES | b'0' | - | 允许搜索 | 允许搜索 |
| visible | bit(1) | YES | b'1' | - | 允许可见 | 允许可见 |
| required | bit(1) | YES | b'0' | - | 必填 | 必填 |
| readonly | bit(1) | YES | b'0' | - | 只读 | 只读 |
| disabled | bit(1) | YES | b'0' | - | 组件失效 | 组件失效 |
| extData | varchar(8192) | YES | - | - | 外部数据 | 外部数据 |
| extKey | varchar(255) | YES | - | - | 外部数据key | 外部数据key |
| extValue | varchar(255) | YES | - | - | 外部数据value | 外部数据value |
| media | varchar(255) | YES | - | - | 传播媒介 | 传播媒介 |
| clazzName | varchar(255) | YES | - | - | 类名 | 类名 |
| superData | varchar(255) | YES | - | - | 父类dataName | 父类dataName |
| status | int(1) | YES | 1 | - | 状态 | 状态 |
| compId | int(11) | YES | - | - | 公司ID | 公司ID |
| isSystemField | bit(1) | YES | b'1' | - | 是否为系统字段 | 是否为系统字段 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 42 department -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~122 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| ocrCode | varchar(25) | NO | - | MUL | - | 业务含义待确认 |
| ocrName | varchar(25) | NO | - | - | - | 业务含义待确认 |
| isparam | int(11) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ocrCode | BTREE | NON-UNIQUE | ocrCode |
| PRIMARY | BTREE | UNIQUE | id |

---

### 43 dptech_v_project_product_config_level_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~0 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI | - | 主键ID |
| projectCode | varchar(100) | YES | - | - | - | 业务含义待确认 |
| itemGroup | decimal(23,10) | YES | - | - | - | 业务含义待确认 |
| itemCode | varchar(100) | YES | - | - | - | 业务含义待确认 |
| parentCode | varchar(1000) | YES | - | - | - | 业务含义待确认 |
| quantity | decimal(23,10) | YES | - | - | - | 业务含义待确认 |
| bomPaths | varchar(1000) | YES | - | - | - | 业务含义待确认 |
| itemModel | varchar(100) | YES | - | - | - | 业务含义待确认 |
| itemDesc | varchar(500) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 44 dp_act_unify_task -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~43326 行 |
| 数据大小 | 58.59 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| taskId | varchar(64) | NO | - | MUL | 统一待办任务Id | 统一待办任务Id |
| originTaskId | varchar(32) | NO | - | MUL | Activiti源TaskId | Activiti源TaskId |
| procInstId | varchar(64) | NO |  | MUL | 流程实例ID | 流程实例ID |
| processKey | varchar(255) | NO |  | - | 流程定义key | 流程定义key |
| taskKey | varchar(255) | NO |  | - | 任务Key | 任务Key |
| taskName | varchar(255) | YES | - | - | 任务名 | 任务名 |
| eventType | varchar(255) | NO | - | - | 事件类型 | 事件类型 |
| title | varchar(255) | YES | - | - | 任务标题 | 任务标题 |
| assignee | varchar(255) | NO | - | - | 办理人 | 办理人 |
| formUrl | varchar(255) | YES | - | - | 待办链接地址 | 待办链接地址 |
| beginTime | datetime | YES | - | - | 开始时间 | 开始时间 |
| endTime | datetime | YES | - | - | 结束时间 | 结束时间 |
| dueTime | datetime | YES | - | - | 过期时间 | 过期时间 |
| state | varchar(25) | YES | - | - | 办理状态 | 办理状态 |
| subState | varchar(25) | YES | - | - | 办理子状态 | 办理子状态 |
| success | bit(1) | NO | b'0' | - | 推送结果 | 推送结果 |
| message | varchar(255) | YES |  | - | 推送消息 | 推送消息 |
| latest | bit(1) | NO | b'1' | - | 是否最新 | 是否最新 |
| pushSender | varchar(255) | YES | - | - | 推送发送实体类 | 推送发送实体类 |
| pushData | varchar(4096) | YES | - | - | 推送JSON内容 | 推送JSON内容 |
| createBy | varchar(45) | NO |  | - | - | 业务含义待确认 |
| createTime | timestamp | NO | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| updateBy | varchar(45) | NO |  | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| originTaskId | BTREE | NON-UNIQUE | originTaskId |
| PRIMARY | BTREE | UNIQUE | id |
| procInstId | BTREE | NON-UNIQUE | procInstId |
| taskId | BTREE | NON-UNIQUE | taskId |

---

### 45 dp_erp_purchase_order_header -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
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

### 46 dp_erp_purchase_order_line -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
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

### 47 dp_erp_purchase_receipt_header -- 

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

### 48 dp_erp_purchase_receipt_line -- 

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

### 49 ehr_company -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~3 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| compID | int(11) | NO | - | PRI | 公司ID，关联表外键 | 公司ID，关联表外键 |
| compCode | varchar(10) | YES | - | - | 公司编号 | 公司编号 |
| compName | varchar(100) | YES | - | - | 公司名称 | 公司名称 |
| compAbbr | varchar(100) | YES | - | - | 公司简称 | 公司简称 |
| adminID | int(11) | YES | - | MUL | 上级ID | 上级ID |
| compGrade | int(11) | YES | - | - | 公司级别 | 公司级别 |
| compType | int(11) | YES | - | - | 公司类别 | 公司类别 |
| compArea | int(11) | YES | - | - | - | 业务含义待确认 |
| effectDate | datetime | YES | - | - | 成立时间 | 成立时间 |
| lawyer | varchar(50) | YES | - | - | 法人 | 法人 |
| address | varchar(200) | YES | - | - | 地址 | 地址 |
| regAddress | varchar(200) | YES | - | - | 注册地址 | 注册地址 |
| tel | varchar(50) | YES | - | - | 电话 | 电话 |
| fax | varchar(50) | YES | - | - | 传真 | 传真 |
| postCode | varchar(50) | YES | - | - | 邮编 | 邮编 |
| webSite | varchar(100) | YES | - | - | 网站 | 网站 |
| isDisabled | bit(1) | YES | - | - | 失效状态 | 失效状态 |
| disabledDate | datetime | YES | - | - | 失效时间 | 失效时间 |
| remark | varchar(500) | YES | - | - | 备注 | 备注 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| adminID | BTREE | NON-UNIQUE | adminID |
| PRIMARY | BTREE | UNIQUE | compID |

---

### 50 ehr_department -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~517 行 |
| 数据大小 | 80.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| depID | int(11) | NO | - | PRI | 部门ID，关联外键 | 部门ID，关联外键 |
| depCode | varchar(20) | YES | - | - | 部门编码 | 部门编码 |
| depName | varchar(100) | YES | - | - | 部门名称 | 部门名称 |
| depAbbr | varchar(100) | YES | - | - | 部门简称 | 部门简称 |
| compID | int(11) | YES | - | MUL | 公司ID，外键 | 公司ID，外键 |
| adminID | int(11) | YES | - | MUL | 上级ID | 上级ID |
| depGrade | int(11) | YES | - | - | 部门级别 | 部门级别 |
| depType | int(11) | YES | - | - | 部门类型 | 部门类型 |
| depProperty | int(11) | YES | - | - | 部门属性 | 部门属性 |
| depCost | int(11) | YES | - | - | 存在部门内分级计数用 | 存在部门内分级计数用 |
| director | int(11) | YES | - | MUL | 主管 | 主管 |
| director2 | int(11) | YES | - | MUL | 分管领导 | 分管领导 |
| depEmp | int(11) | YES | - | - | - | 业务含义待确认 |
| depNum | int(11) | YES | - | - | - | 业务含义待确认 |
| effectDate | datetime | YES | - | - | 生效时间 | 生效时间 |
| xOrder | varchar(20) | YES | - | - | 排序 | 排序 |
| isDisabled | bit(1) | YES | - | - | 失效状态 | 失效状态 |
| disabledDate | datetime | YES | - | - | 失效时间 | 失效时间 |
| remark | varchar(500) | YES | - | - | 备注 | 备注 |
| depCustom1 | int(11) | YES | - | - | 保留字段1 | 保留字段1 |
| depCustom2 | int(11) | YES | - | - | 保留字段2、部门秘书 | 保留字段2、部门秘书 |
| depCustom3 | int(11) | YES | - | - | 保留字段3 | 保留字段3 |
| depCustom4 | int(11) | YES | - | - | 保留字段4 | 保留字段4 |
| depCustom5 | int(11) | YES | - | - | 保留字段5 | 保留字段5 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| adminID | BTREE | NON-UNIQUE | adminID |
| compID | BTREE | NON-UNIQUE | compID |
| director | BTREE | NON-UNIQUE | director |
| director2 | BTREE | NON-UNIQUE | director2 |
| PRIMARY | BTREE | UNIQUE | depID |

---

### 51 ehr_employee -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~4831 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| empID | int(11) | NO | - | PRI | 员工ID，外键 | 员工ID，外键 |
| workNo | varchar(100) | NO | - | MUL | 工号 | 工号 |
| name | varchar(200) | YES | - | - | 姓名 | 姓名 |
| eName | varchar(200) | YES | - | - | 英文名 | 英文名 |
| compID | int(11) | NO | - | MUL | 公司ID | 公司ID |
| depID | int(11) | NO | - | MUL | 部门ID | 部门ID |
| jobID | int(11) | NO | - | MUL | 岗位ID | 岗位ID |
| reportTo | int(11) | YES | - | MUL | 直接上级 | 直接上级 |
| wfreportTo | int(11) | YES | - | MUL | 职能上级 | 职能上级 |
| empStatus | int(11) | NO | - | - | 员工状态，1：在职，2：离职 | 员工状态，1：在职，2：离职 |
| jobStatus | int(11) | YES | - | - | 岗位状态 | 岗位状态 |
| empType | int(11) | YES | - | - | 聘用类型：1：正式，3：实习生 | 聘用类型：1：正式，3：实习生 |
| joinDate | datetime | YES | - | - | 加入公司日期 | 加入公司日期 |
| workBeginDate | datetime | YES | - | - | 工作开始日期 | 工作开始日期 |
| jobBeginDate | datetime | YES | - | - | 加入公司日期（未知） | 加入公司日期（未知） |
| pracBeginDate | datetime | YES | - | - | 实习开始时间 | 实习开始时间 |
| pracEndDate | datetime | YES | - | - | 实习结束时间 | 实习结束时间 |
| probBeginDate | datetime | YES | - | - | - | 业务含义待确认 |
| probEndDate | datetime | YES | - | - | - | 业务含义待确认 |
| leaveDate | datetime | YES | - | - | 离职时间 | 离职时间 |
| gender | int(11) | YES | - | - | 性别：1：男，2：女 | 性别：1：男，2：女 |
| email | varchar(500) | YES | - | - | 邮箱 | 邮箱 |
| mobile | varchar(50) | YES | - | - | 手机 | 手机 |
| officePhone | varchar(50) | YES | - | - | 座机 | 座机 |
| remark | varchar(100) | YES | - | - | 备注 | 备注 |
| disabled | int(11) | YES | 0 | - | 失效 | 失效 |
| empCustom1 | int(11) | YES | - | - | 预留字段1 | 预留字段1 |
| empCustom2 | int(11) | YES | - | - | 预留字段2 | 预留字段2 |
| empCustom3 | int(11) | YES | - | - | 预留字段3 | 预留字段3 |
| empCustom4 | varchar(50) | YES | - | - | 预留字段4 | 预留字段4 |
| empCustom5 | int(11) | YES | - | - | 预留字段5 | 预留字段5 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| compID | BTREE | NON-UNIQUE | compID |
| depID | BTREE | NON-UNIQUE | depID |
| jobID | BTREE | NON-UNIQUE | jobID |
| PRIMARY | BTREE | UNIQUE | empID |
| reportTo | BTREE | NON-UNIQUE | reportTo |
| wfreportTo | BTREE | NON-UNIQUE | wfreportTo |
| workNo | BTREE | NON-UNIQUE | workNo |

---

### 52 ehr_emp_power -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~127 行 |
| 数据大小 | 64.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| empID | int(11) | NO | - | UNI | empID | empID |
| workNo | varchar(25) | NO |  | MUL | 工号 | 工号 |
| compID | int(11) | NO | - | - | 公司id | 公司id |
| depIDs | varchar(4096) | NO |  | - | 从ehr同步数据生成的部门权限，固定的 | 从ehr同步数据生成的部门权限，固定的 |
| extraDepIDs | varchar(4096) | NO |  | - | 绩效管理附加的部门权限 | 绩效管理附加的部门权限 |
| adminDepIDs | varchar(4096) | NO |  | - | 绩效考核管理的部门 | 绩效考核管理的部门 |
| empIDs | varchar(4096) | NO |  | - | 从ehr同步数据生成的下属权限，固定的 | 从ehr同步数据生成的下属权限，固定的 |
| extraEmpIDs | varchar(4096) | NO |  | - | 绩效管理附加的下属权限 | 绩效管理附加的下属权限 |
| state | bit(1) | NO | b'1' | - | 是否生效状态 | 是否生效状态 |
| createBy | varchar(25) | NO |  | - | - | 业务含义待确认 |
| createTime | datetime | NO | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES |  | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| empID | BTREE | UNIQUE | empID |
| PRIMARY | BTREE | UNIQUE | id |
| workNo | BTREE | NON-UNIQUE | workNo |

---

### 53 ehr_job -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~245 行 |
| 数据大小 | 48.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| jobID | int(11) | NO | - | PRI | 岗位ID，关联表外键 | 岗位ID，关联表外键 |
| jobCode | varchar(10) | YES | - | - | 岗位编码 | 岗位编码 |
| jobName | varchar(100) | YES | - | - | 岗位名称 | 岗位名称 |
| jobAbbr | varchar(100) | YES | - | - | 岗位简称 | 岗位简称 |
| depID | int(11) | YES | - | MUL | 部门ID | 部门ID |
| adminID | int(11) | YES | - | MUL | 上级ID | 上级ID |
| jobGrage | int(11) | YES | - | - | 岗位级别 | 岗位级别 |
| jobType | int(11) | YES | - | - | 岗位类型 | 岗位类型 |
| jobProperty | int(11) | YES | - | - | 岗位属性 | 岗位属性 |
| jobNum | int(11) | YES | - | - | - | 业务含义待确认 |
| isCore | bit(1) | YES | b'0' | - | - | 业务含义待确认 |
| effectDate | datetime | NO | - | - | 生效时间 | 生效时间 |
| xorder | varchar(20) | YES | - | - | 排序 | 排序 |
| isDisabled | bit(1) | YES | b'0' | - | 失效状态 | 失效状态 |
| disabledDate | datetime | YES | - | - | 失效时间 | 失效时间 |
| remark | varchar(500) | YES | - | - | 备注 | 备注 |
| xType | int(11) | YES | - | - | - | 业务含义待确认 |
| jobCustom1 | int(11) | YES | - | - | 保留字段1 | 保留字段1 |
| jobCustom2 | int(11) | YES | - | - | 保留字段2 | 保留字段2 |
| jobCustom3 | int(11) | YES | - | - | 保留字段3 | 保留字段3 |
| jobCustom4 | int(11) | YES | - | - | 保留字段4 | 保留字段4 |
| jobCustom5 | int(11) | YES | - | - | 保留字段5 | 保留字段5 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| adminID | BTREE | NON-UNIQUE | adminID |
| depID | BTREE | NON-UNIQUE | depID |
| PRIMARY | BTREE | UNIQUE | jobID |

---

### 54 ehr_login -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~3224 行 |
| 数据大小 | 272.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI | - | 主键ID |
| title | varchar(255) | YES | - | - | - | 业务含义待确认 |
| account | varchar(255) | YES | - | - | - | 业务含义待确认 |
| empID | int(11) | YES | - | - | - | 业务含义待确认 |
| workNo | varchar(255) | YES | - | - | - | 业务含义待确认 |
| name | varchar(255) | YES | - | - | - | 名称 |
| isDisabled | int(11) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 55 fb_contract -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~104743 行 |
| 数据大小 | 23.56 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contract_id | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| contract_code | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| office_code | varchar(15) | YES | - | MUL | - | 业务含义待确认 |
| contract_type | int(11) | YES | - | MUL | - | 业务含义待确认 |
| customer_name | varchar(512) | YES | - | - | - | 业务含义待确认 |
| project_name | varchar(512) | YES | - | - | - | 业务含义待确认 |
| warranty | varchar(2) | YES | - | - | - | 业务含义待确认 |
| marketCode | varchar(10) | YES | - | - | - | 业务含义待确认 |
| marketName | varchar(15) | YES | - | - | - | 业务含义待确认 |
| systemId | int(11) | YES | - | - | - | 业务含义待确认 |
| systemName | varchar(15) | YES | - | - | - | 业务含义待确认 |
| remark | varchar(4096) | YES | - | - | - | 备注 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contract_code | BTREE | NON-UNIQUE | contract_code |
| contract_type | BTREE | NON-UNIQUE | contract_type |
| fb_contract_contract_id_IDX | BTREE | NON-UNIQUE | contract_id,office_code |
| office_code_IDX | BTREE | NON-UNIQUE | office_code,contract_id |

---

### 56 fb_ft_result1 -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~193752 行 |
| 数据大小 | 10.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| item_id | int(11) | YES | - | MUL | - | 业务含义待确认 |
| serial_number | varchar(100) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| item_id | BTREE | NON-UNIQUE | item_id |

---

### 57 fb_ft_result2 -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~496626 行 |
| 数据大小 | 298.83 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| result1_id | int(11) | YES | - | MUL | - | 业务含义待确认 |
| result_desc | text | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| result1_id | BTREE | NON-UNIQUE | result1_id |

---

### 58 fb_items -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~32188 行 |
| 数据大小 | 3.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | YES | - | - | - | 主键ID |
| item | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| describe_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| itemname | varchar(255) | YES | - | MUL | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| cover_index | BTREE | NON-UNIQUE | item,itemname,describe_ |
| itemname | BTREE | NON-UNIQUE | itemname |

---

### 59 fb_items2 -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~19357 行 |
| 数据大小 | 2.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | YES | - | - | - | 主键ID |
| item | varchar(15) | YES | - | - | - | 业务含义待确认 |
| describe_ | varchar(150) | YES | - | - | - | 业务含义待确认 |
| itemname | varchar(255) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|

---

### 60 fb_market_system -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~14 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| marketCode | varchar(10) | YES | - | - | - | 业务含义待确认 |
| marketName | varchar(15) | YES | - | - | - | 业务含义待确认 |
| systemId | int(11) | YES | - | - | - | 业务含义待确认 |
| systemName | varchar(15) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|

---

### 61 fb_office_relationship -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
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

### 62 fb_service -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~95878 行 |
| 数据大小 | 12.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | varchar(64) | YES | - | MUL | - | 主键ID |
| con_xb | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| barcode | varchar(50) | YES | - | MUL | - | 业务含义待确认 |
| grade | varchar(15) | YES | - | - | - | 业务含义待确认 |
| begin_date | datetime | YES | - | - | - | 业务含义待确认 |
| end_date | datetime | YES | - | - | - | 业务含义待确认 |
| warranty | char(1) | YES | - | - | - | 业务含义待确认 |
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

### 63 fb_shipment -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~140962 行 |
| 数据大小 | 17.55 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| packlist_id | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| con_id | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| packdate | datetime | YES | - | MUL | - | 业务含义待确认 |
| warrantyStartTime | datetime | YES | - | - | - | 业务含义待确认 |
| warrantyEndTime | datetime | YES | - | - | - | 业务含义待确认 |
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

### 64 fb_shipment_barcode -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~3541100 行 |
| 数据大小 | 612.00 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | bigint(20) | NO | - | PRI, auto_increment | - | 主键ID |
| pack_id | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| item | varchar(16) | YES | - | MUL | - | 业务含义待确认 |
| barcode | varchar(50) | YES | - | MUL | - | 业务含义待确认 |
| com_barcode | varchar(50) | YES | - | - | - | 业务含义待确认 |
| rma_no | varchar(64) | YES | - | - | - | 业务含义待确认 |
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
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| syncTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| uuid | varchar(64) | YES | - | UNI | - | 业务含义待确认 |

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

### 65 fb_shipment_barcode_change_log -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~528607 行 |
| 数据大小 | 547.00 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| logID | bigint(20) | NO | - | PRI, auto_increment | - | 业务含义待确认 |
| tableName | varchar(128) | YES | - | - | - | 业务含义待确认 |
| operation | varchar(50) | YES | - | - | - | 业务含义待确认 |
| changedBy | varchar(128) | YES | - | - | - | 业务含义待确认 |
| changeTime | datetime | YES | - | - | - | 业务含义待确认 |
| dataId | varchar(128) | YES | - | MUL | - | 业务含义待确认 |
| barCode | varchar(128) | YES | - | - | - | 业务含义待确认 |
| lasted | smallint(6) | YES | - | MUL | - | 业务含义待确认 |
| oldValues | longtext | YES | - | - | - | 业务含义待确认 |
| newValues | longtext | YES | - | - | - | 业务含义待确认 |
| syncTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| syncFlag | smallint(6) | YES | 0 | MUL | 同步标记，0待处理，1已处理，主要用于数据同步后更新发货记录使用 | 同步标记，0待处理，1已处理，主要用于数据同步后更新发货记录使用 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| idx_dataid_lasted_logid | BTREE | NON-UNIQUE | dataId,lasted,logID |
| idx_lasted_dataid_logid | BTREE | NON-UNIQUE | lasted,dataId,logID |
| idx_syncFlag_lasted | BTREE | NON-UNIQUE | syncFlag,lasted |
| PRIMARY | BTREE | UNIQUE | logID |

---

### 66 fb_shipment_barcode_order_line -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~2576429 行 |
| 数据大小 | 372.94 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| pack_id | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| packlist_no | varchar(64) | YES | - | - | - | 业务含义待确认 |
| barcode | varchar(50) | YES | - | MUL | - | 业务含义待确认 |
| contractNo | varchar(50) | YES | - | - | - | 业务含义待确认 |
| orderNumber | varchar(32) | YES | - | MUL | - | 业务含义待确认 |
| lineNum | int(11) | YES | - | - | - | 业务含义待确认 |
| orderQty | int(11) | YES | - | - | - | 业务含义待确认 |
| deliveredQty | int(11) | YES | - | - | - | 业务含义待确认 |
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

### 67 fb_shipment_barcode_relation -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~55130 行 |
| 数据大小 | 7.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI | - | 主键ID |
| sn1 | varchar(50) | YES | - | MUL | - | 业务含义待确认 |
| item1 | varchar(15) | YES | - | MUL | - | 业务含义待确认 |
| sn2 | varchar(50) | YES | - | MUL | - | 业务含义待确认 |
| item2 | varchar(15) | YES | - | MUL | - | 业务含义待确认 |
| contract | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| createtime | varchar(50) | YES | - | - | - | 业务含义待确认 |
| updatetime | varchar(50) | YES | - | - | - | 业务含义待确认 |

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

### 68 fb_soft_version -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~161015 行 |
| 数据大小 | 13.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| serial_number | varchar(100) | YES | - | MUL | - | 业务含义待确认 |
| conp | varchar(100) | YES | - | MUL | - | 业务含义待确认 |
| cpld | varchar(100) | YES | - | MUL | - | 业务含义待确认 |
| boot | varchar(100) | YES | - | MUL | - | 业务含义待确认 |
| pcb | varchar(100) | YES | - | MUL | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| boot | BTREE | NON-UNIQUE | boot |
| conp | BTREE | NON-UNIQUE | conp |
| cpld | BTREE | NON-UNIQUE | cpld |
| pcb | BTREE | NON-UNIQUE | pcb |
| serial_number | BTREE | NON-UNIQUE | serial_number |

---

### 69 fb_warranty_grade -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~11 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| gradecode | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| gradename | varchar(125) | YES | - | - | - | 业务含义待确认 |
| gradestatus | int(11) | YES | 0 | - | - | 业务含义待确认 |
| sort | int(3) | YES | 0 | - | - | 排序 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| gradecode | BTREE | NON-UNIQUE | gradecode |
| PRIMARY | BTREE | UNIQUE | id |

---

### 70 find_in_set_help -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~101 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI | where中FIND_IN_SET函数替代方法 | where中FIND_IN_SET函数替代方法 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 71 firebird_operation_log -- 发货系统Firebird数据库更改日志

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

### 72 fnd_act_hi_comment -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~36824 行 |
| 数据大小 | 4.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | activity 审批意见表 | activity 审批意见表 |
| objId | int(11) | YES | - | MUL | 业务ID | 业务ID |
| procdefKey | varchar(50) | YES | - | - | 流程类型 | 流程类型 |
| taskKey | varchar(50) | YES | - | - | 任务Key | 任务Key |
| taskId | varchar(25) | YES | - | MUL | activity任务ID | activity任务ID |
| instId | varchar(25) | YES | - | MUL | 流程ID | 流程ID |
| assignee | varchar(25) | YES | - | MUL | 办理人 | 办理人 |
| assigneeTime | datetime | YES | - | - | 办理时间 | 办理时间 |
| nextAssignee | varchar(25) | YES | - | - | 下一步办理人 | 下一步办理人 |
| nextAssigneeName | varchar(64) | YES | - | - | 下一步办理人姓名 | 下一步办理人姓名 |
| result | int(11) | YES | - | - | 审批结果 | 审批结果 |
| message | text | YES | - | - | 审批意见 | 审批意见 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| assignee | BTREE | NON-UNIQUE | assignee,procdefKey |
| instId | BTREE | NON-UNIQUE | instId |
| objId | BTREE | NON-UNIQUE | objId,procdefKey |
| PRIMARY | BTREE | UNIQUE | id |
| taskId | BTREE | NON-UNIQUE | taskId |

---

### 73 fnd_basic_data -- 基础数据

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 基础数据 |
| 数据量 | ~480 行 |
| 数据大小 | 64.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| dataTypeCode | varchar(45) | YES | - | MUL | - | 业务含义待确认 |
| basicDataId | varchar(255) | YES | - | MUL | - | 业务含义待确认 |
| basicDataName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| basicDataAttri1 | varchar(255) | YES | - | - | 字段属性1 | 字段属性1 |
| sortId | int(11) | YES | - | - | 查询排序字段数值越大越在前 | 查询排序字段数值越大越在前 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(45) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(45) | YES | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| basicDataId | BTREE | NON-UNIQUE | basicDataId |
| basicDataId_dataTypeCode | BTREE | NON-UNIQUE | dataTypeCode,basicDataId |
| PRIMARY | BTREE | UNIQUE | id |

---

### 74 fnd_basic_data_type -- 基础数据类型

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 基础数据类型 |
| 数据量 | ~30 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| dataTypeCode | varchar(45) | YES | - | - | - | 业务含义待确认 |
| dataTypeName | varchar(45) | YES | - | - | - | 业务含义待确认 |
| status | int(11) | YES | - | - | 是否需要放在前台管理 | 是否需要放在前台管理 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(45) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(45) | YES | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 75 fnd_basic_prjstate -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~40 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| dataTypeCode | varchar(45) | YES | - | MUL | 数据类型编码，对应fnd_basic_data | 数据类型编码，对应fnd_basic_data |
| basicDataId | varchar(11) | YES | - | - | 基础数据ID，对应fnd_basic_data | 基础数据ID，对应fnd_basic_data |
| column010 | varchar(10) | YES | - | - | 项目类型，对应pm_project_header | 项目类型，对应pm_project_header |
| column011 | varchar(10) | YES | - | - | 项目类别，对应pm_project_header | 项目类别，对应pm_project_header |
| createTime | datetime | YES | - | - | 记录数据创建时间 | 记录数据创建时间 |
| createBy | varchar(45) | YES | - | - | 记录数据创建用户 | 记录数据创建用户 |
| updateTime | datetime | YES | - | - | 记录数据最新更新时间 | 记录数据最新更新时间 |
| updateBy | varchar(45) | YES | - | - | 记录数据最新更新用户 | 记录数据最新更新用户 |
| effectiveFrom | datetime | YES | - | - | 数据有效性开始时间 | 数据有效性开始时间 |
| effectiveTo | datetime | YES | - | - | 数据有效性结束时间 | 数据有效性结束时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| dataTypeCode | BTREE | NON-UNIQUE | dataTypeCode,basicDataId |
| PRIMARY | BTREE | UNIQUE | id |

---

### 76 fnd_company -- 组织机构表

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

### 77 fnd_data_refresh_log -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~16540 行 |
| 数据大小 | 3.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| refreshTaskName | varchar(100) | YES | - | - | - | 业务含义待确认 |
| handleUser | varchar(15) | YES | - | - | - | 业务含义待确认 |
| dataFrom | varchar(25) | YES | - | - | - | 业务含义待确认 |
| dataTo | varchar(25) | YES | - | - | - | 业务含义待确认 |
| refreshFrom | datetime | YES | - | - | 刷新开始时间 | 刷新开始时间 |
| refreshTo | datetime | YES | - | - | 结束时间 | 结束时间 |
| refreshState | int(11) | YES | 0 | - | 刷新成功或失败 0失败 1 成功 | 刷新成功或失败 0失败 1 成功 |
| refreshException | mediumtext | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 78 fnd_department -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~137 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| departmentNum | varchar(20) | NO | - | UNI | - | 业务含义待确认 |
| departmentName | varchar(20) | NO | - | - | - | 业务含义待确认 |
| isparam | int(11) | YES | 0 | - | - | 业务含义待确认 |
| status | int(11) | NO | 1 | - | - | 状态 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| deparmentNum | BTREE | UNIQUE | departmentNum |
| PRIMARY | BTREE | UNIQUE | id |

---

### 79 fnd_files -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~9096 行 |
| 数据大小 | 2.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 系统上传文件信息 | 系统上传文件信息 |
| fileName | varchar(255) | YES | - | - | 文件名称 | 文件名称 |
| filePath | varchar(255) | YES | - | - | 文件路径 | 文件路径 |
| fileType | varchar(255) | YES | - | - | 文件分类 | 文件分类 |
| uploadBy | varchar(25) | YES | - | - | 上传用户 | 上传用户 |
| uploadTime | datetime | YES | - | - | 上传时间 | 上传时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 80 fnd_mails -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~146157 行 |
| 数据大小 | 440.75 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| mailSubject | varchar(255) | NO | - | - | 邮件主题 | 邮件主题 |
| mailContent | longtext | NO | - | - | 邮件正文 | 邮件正文 |
| mailTos | text | YES | - | - | 邮件主送 | 邮件主送 |
| mailCcs | text | YES | - | - | 邮件抄送 | 邮件抄送 |
| mailBcc | text | YES | - | - | 邮件密送 | 邮件密送 |
| mailAttachFiles | text | YES | - | - | 邮件附件 以特殊符号间隔多个文件 | 邮件附件 以特殊符号间隔多个文件 |
| mailSendTime | datetime | YES | - | - | 邮件实际发送时间 | 邮件实际发送时间 |
| mailExpectSendTime | datetime | YES | - | - | 邮件期望发送时间 | 邮件期望发送时间 |
| mailServerPort | varchar(25) | YES | - | - | - | 业务含义待确认 |
| mailServerHost | varchar(25) | YES | - | - | - | 业务含义待确认 |
| mailUsername | varchar(25) | YES | - | - | - | 业务含义待确认 |
| mailPassword | varchar(25) | YES | - | - | - | 业务含义待确认 |
| mailFromaddress | varchar(25) | YES | - | - | - | 业务含义待确认 |
| sendFlag | int(11) | YES | 0 | - | 邮件是否发送 1 为已发送 | 邮件是否发送 1 为已发送 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updatteTime | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 81 fnd_menus -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~22 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| menuCode | varchar(50) | YES | - | - | 菜单编码 | 菜单编码 |
| menuName | varchar(25) | YES | - | - | 菜单名称 | 菜单名称 |
| menuLevel | int(1) | YES | - | - | 菜单级别 | 菜单级别 |
| superId | int(11) | YES | - | - | 父菜单ID | 父菜单ID |
| path | varchar(200) | YES | - | - | 访问路径 | 访问路径 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 82 fnd_roles -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~16 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(6) | NO | - | PRI, auto_increment | - | 主键ID |
| roleName | varchar(64) | NO | - | UNI | - | 业务含义待确认 |
| defaultPage | varchar(255) | YES | - | - | 该角色登录的默认首页 | 该角色登录的默认首页 |
| status | int(1) | NO | - | - | - | 状态 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| roleRemark | varchar(200) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| roleName | BTREE | UNIQUE | roleName |

---

### 83 fnd_role_menus -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~58 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| roleId | int(11) | NO | - | - | - | 业务含义待确认 |
| menuId | int(11) | NO | - | - | - | 业务含义待确认 |
| menuPower | varchar(20) | NO | - | - | 各菜单增删改权限 | 各菜单增删改权限 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 84 fnd_spms_arg -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~5 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 备件系统一些特殊参数控制 如邮件等 | 备件系统一些特殊参数控制 如邮件等 |
| code | varchar(25) | YES | - | - | - | 编码 |
| var | text | YES | - | - | - | 业务含义待确认 |
| mark | varchar(255) | YES | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 85 fnd_sys_arg -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~45 行 |
| 数据大小 | 80.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 系统变量 | 系统变量 |
| code | varchar(64) | YES | - | - | - | 编码 |
| var | text | YES | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 86 fnd_user_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~459 行 |
| 数据大小 | 112.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(8) | NO | - | PRI, auto_increment | - | 主键ID |
| username | varchar(128) | NO | - | MUL | - | 业务含义待确认 |
| password | varchar(32) | NO | 5416d7cd6ef195a0f7622a9c56b55e84 | - | - | 业务含义待确认 |
| email | varchar(128) | NO | - | - | - | 业务含义待确认 |
| dpNo | varchar(25) | YES | - | - | - | 业务含义待确认 |
| realName | varchar(128) | NO | - | - | - | 业务含义待确认 |
| roleIds | varchar(64) | YES | - | - | 用户角色，支持多角色 | 用户角色，支持多角色 |
| isemail | int(11) | YES | - | - | - | 业务含义待确认 |
| status | int(1) | YES | - | - | - | 状态 |
| defaultPage | varchar(255) | YES | - | - | 该用户登录首页 | 该用户登录首页 |
| pwdoverdue | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| username | BTREE | NON-UNIQUE | username |

---

### 87 fnd_user_menus -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~3044 行 |
| 数据大小 | 272.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| fnd_user_id | int(11) | YES | - | - | - | 业务含义待确认 |
| username | varchar(128) | YES | - | - | - | 业务含义待确认 |
| menuCode | varchar(50) | YES | - | - | - | 业务含义待确认 |
| menuValue | int(1) | YES | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |
| createdBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 88 fnd_user_power -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~442 行 |
| 数据大小 | 64.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| fndUserId | int(11) | YES | - | - | - | 业务含义待确认 |
| username | varchar(25) | YES | - | - | - | 业务含义待确认 |
| areapower | varchar(4096) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 89 hexiao -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~83 行 |
| 数据大小 | 48.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| 单据编号 | double | YES | - | - | - | 业务含义待确认 |
| 过帐日期 | timestamp | YES | - | - | - | 业务含义待确认 |
| 物料代码 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| 物料/服务描述 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| 未核销数量 | double | YES | - | - | - | 业务含义待确认 |
| 设备序列号 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| 注释 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| 合同号 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| 责任部门 | varchar(255) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|

---

### 90 mes_oqc_info -- OQC检验记录

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | OQC检验记录 |
| 数据量 | ~1411475 行 |
| 数据大小 | 164.67 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| packNo | varchar(64) | YES | - | - | 装箱单号 | 装箱单号 |
| contractNo | varchar(64) | YES | - | - | 合同号 | 合同号 |
| itemCode | varchar(25) | YES | - | - | 物料号 | 物料号 |
| barcode | varchar(64) | YES | - | MUL | 设备序列号 | 设备序列号 |
| itemNo | varchar(25) | YES | - | - | 装箱销售明细行号 | 装箱销售明细行号 |
| workNo | varchar(25) | YES | - | - | 工号 | 工号 |
| inspectUser | varchar(25) | YES | - | - | 检验人 | 检验人 |
| inspectTime | datetime | YES | - | - | 检验时间 | 检验时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barcode | BTREE | NON-UNIQUE | barcode |
| PRIMARY | BTREE | UNIQUE | id |

---

### 91 mes_seal_info -- 印章登记表

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 印章登记表 |
| 数据量 | ~60 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | varchar(255) | YES | - | - | - | 主键ID |
| name | varchar(255) | YES | - | - | 印章名称 | 印章名称 |
| info | varchar(255) | YES | - | - | 印记 | 印记 |
| description | varchar(255) | YES | - | - | 用途 | 用途 |
| user | varchar(255) | YES | - | MUL | 领用人 | 领用人 |
| takeTime | datetime | YES | - | - | 领用时间 | 领用时间 |
| backTime | datetime | YES | - | - | 归还时间 | 归还时间 |
| remark | varchar(255) | YES | - | - | 备注 | 备注 |
| uploadBy | varchar(255) | YES | - | - | 上传人 | 上传人 |
| uploadTime | datetime | YES | - | - | 上传时间 | 上传时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| user | BTREE | NON-UNIQUE | user |

---

### 92 pm_basic_deliver_detail -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~68845 行 |
| 数据大小 | 13.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| projectId | int(11) | YES | - | MUL | 项目ID | 项目ID |
| projectType | varchar(25) | YES | 10 | MUL | 项目类型 | 项目类型 |
| contractNo | varchar(25) | YES | - | - | - | 业务含义待确认 |
| taskId | int(11) | YES | - | - | TaskId | TaskId |
| deliverId | int(11) | YES | - | MUL | 对应pm_basic_prj_deliver主键 | 对应pm_basic_prj_deliver主键 |
| deliverableName | varchar(255) | YES | - | - | 交付件名称 | 交付件名称 |
| deliverablePath | varchar(255) | YES | - | - | 交付件路径 | 交付件路径 |
| deliverableType | varchar(45) | YES | - | - | 交付件类型 | 交付件类型 |
| uploadUser | varchar(45) | YES | - | - | 上传者 | 上传者 |
| uploadTime | datetime | YES | - | - | 上传时间 | 上传时间 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| deliverId | BTREE | NON-UNIQUE | deliverId |
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |
| projectType_projectId_deliverType | BTREE | NON-UNIQUE | projectType,projectId,deliverableType |

---

### 93 pm_basic_prj_deliver -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~86 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| column010 | varchar(25) | YES | - | - | 对应pm_project_header | 对应pm_project_header |
| column011 | varchar(25) | YES | - | - | 对应pm_project_header | 对应pm_project_header |
| dataTypeCode | varchar(45) | YES | - | MUL | 活动节点，对应fnd_basic_data表 | 活动节点，对应fnd_basic_data表 |
| basicDataId | varchar(45) | YES | - | - | 活动节点，对应fnd_basic_data表 | 活动节点，对应fnd_basic_data表 |
| dataTypeCodeSon | varchar(45) | YES | - | MUL | 交付件节点，对应fnd_basic_data表 | 交付件节点，对应fnd_basic_data表 |
| basicDataIdSon | varchar(45) | YES | - | - | 交付件节点，对应fnd_basic_data表 | 交付件节点，对应fnd_basic_data表 |
| isNeed | int(11) | YES | 0 | - | 是否必须，1表示必须，2表示分情况确定 | 是否必须，1表示必须，2表示分情况确定 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(45) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(45) | YES | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| dataTypeCode | BTREE | NON-UNIQUE | dataTypeCode,basicDataId |
| dataTypeCodeSon | BTREE | NON-UNIQUE | dataTypeCodeSon,basicDataIdSon |
| PRIMARY | BTREE | UNIQUE | id |

---

### 94 pm_cl_callback -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~2729 行 |
| 数据大小 | 384.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 运营商直签项目回访申请主表 | 运营商直签项目回访申请主表 |
| projectId | int(11) | YES | - | MUL | 项目ID | 项目ID |
| instId | varchar(25) | YES | - | MUL | 流程ID | 流程ID |
| remark | text | YES | - | - | 回访申请备注 | 回访申请备注 |
| applyState | int(11) | YES | - | - | -1草稿 1 审批中 2审批通过 | -1草稿 1 审批中 2审批通过 |
| applyBy | varchar(25) | YES | - | - | 申请人 | 申请人 |
| applyTime | datetime | YES | - | - | 申请时间 | 申请时间 |
| createTime | timestamp | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| instId | BTREE | NON-UNIQUE | instId |
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 95 pm_cl_callback_quesnaire -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~2855 行 |
| 数据大小 | 224.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| callBackId | int(11) | YES | - | MUL | 回访主键主表 | 回访主键主表 |
| taskId | varchar(25) | YES | - | - | 对应任务ID | 对应任务ID |
| quesnaireId | int(11) | YES | - | MUL | 对应pm_cl_quesnaire_result_header主键 | 对应pm_cl_quesnaire_result_header主键 |
| quesnaireVersion | int(11) | YES | - | - | 版本号 | 版本号 |
| quesnaireState | int(11) | YES | - | - | -1 草稿 1已提交 | -1 草稿 1已提交 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| callBackId | BTREE | NON-UNIQUE | callBackId |
| PRIMARY | BTREE | UNIQUE | id |
| quesnaireId | BTREE | NON-UNIQUE | quesnaireId |

---

### 96 pm_cl_evaluation_header -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~25911 行 |
| 数据大小 | 5.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) unsigned | NO | - | PRI, auto_increment | - | 主键ID |
| projectCode | varchar(45) | NO | - | MUL | 评测项目编码 | 评测项目编码 |
| projectId | int(11) | NO | 0 | MUL | 项目ID | 项目ID |
| projectName | varchar(120) | YES | - | - | 项目名称 | 项目名称 |
| evaluationTime | datetime | YES | 0000-00-00 00:00:00 | - | 审核时间 | 审核时间 |
| evaluationPeopleName | varchar(45) | YES | - | - | 审核人员姓名 | 审核人员姓名 |
| evaluationScore | double | NO | 0 | - | 评测总分数 | 评测总分数 |
| evaluationResult | int(11) | NO | 0 | - | 评测结果（通过/未通过） | 评测结果（通过/未通过） |
| evaluationComment | text | YES | - | - | 项目评价（驳回时为驳回原因） | 项目评价（驳回时为驳回原因） |
| evaluationType | int(11) | NO | 0 | - | 400回访/项目组总分评定 | 400回访/项目组总分评定 |
| status | int(11) | NO | 0 | - | - | 状态 |
| createdTime | datetime | YES | 0000-00-00 00:00:00 | - | - | 业务含义待确认 |
| createdPerson | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updatedTime | datetime | YES | 0000-00-00 00:00:00 | - | - | 业务含义待确认 |
| updatedPerson | varchar(25) | YES | - | - | - | 业务含义待确认 |
| nextAcceptPerson | varchar(25) | YES | - | - | 下一个接收申请的人员 | 下一个接收申请的人员 |
| evaluationPeopleId | varchar(25) | YES | - | - | 审核人员用户名 | 审核人员用户名 |
| nextAcceptPersonName | varchar(25) | YES | - | - | - | 业务含义待确认 |
| applyHeaderId | int(11) | NO | 0 | - | 申请表Id | 申请表Id |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectCode_index | BTREE | NON-UNIQUE | projectCode |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 97 pm_cl_quesnaire_result_header -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~103363 行 |
| 数据大小 | 6.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) unsigned | NO | - | PRI, auto_increment | - | 主键ID |
| evaluationHeaderId | int(11) | NO | - | MUL | 测评记录Id | 测评记录Id |
| quesnaireTemplateHeaderId | int(11) | YES | - | - | 问卷模板Id | 问卷模板Id |
| quesMarkScore | double | YES | 0 | - | 问卷得分 | 问卷得分 |
| createdTime | datetime | YES | - | - | - | 业务含义待确认 |
| createdPerson | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updatedTime | datetime | YES | - | - | - | 业务含义待确认 |
| updatedPerson | varchar(25) | YES | - | - | - | 业务含义待确认 |
| quesMarkResult | int(11) | YES | - | - | 评分结果 | 评分结果 |
| status | int(11) | NO | 0 | - | - | 状态 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| index_evaluationHeaderId | BTREE | NON-UNIQUE | evaluationHeaderId |
| PRIMARY | BTREE | UNIQUE | id |

---

### 98 pm_cl_quesnaire_result_line -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~454563 行 |
| 数据大小 | 28.56 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) unsigned | NO | - | PRI, auto_increment | - | 主键ID |
| quesnaireTemplateHeaderId | int(11) | NO | - | MUL | 回访问卷Id | 回访问卷Id |
| quesnaireTemplateLineId | int(11) | NO | - | MUL | 问卷中问题的Id | 问卷中问题的Id |
| questionTemplateOptId | int(11) | YES | - | - | 选中的选项id | 选中的选项id |
| questionAnswer | text | YES | - | - | - | 业务含义待确认 |
| questionScore | double | NO | 0 | - | 问题得分 | 问题得分 |
| quesnaireResultHeaderId | int(11) | NO | - | MUL | 回访结果头信息Id | 回访结果头信息Id |
| createdTime | datetime | YES | - | - | - | 业务含义待确认 |
| createdPerson | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updatedTime | datetime | YES | - | - | - | 业务含义待确认 |
| updatedPerson | varchar(25) | YES | - | - | - | 业务含义待确认 |
| quesTypeForCB | varchar(10) | YES | - | - | 问题回访类型 | 问题回访类型 |
| quesEvaResult | int(11) | YES | - | - | 选项是否为不同选项 | 选项是否为不同选项 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| quesnaireResultHeaderId | BTREE | NON-UNIQUE | quesnaireResultHeaderId,quesTypeForCB |
| quesnaireTemplateHeaderId | BTREE | NON-UNIQUE | quesnaireTemplateHeaderId,quesnaireTemplateLineId |
| quesnaireTemplateLineId | BTREE | NON-UNIQUE | quesnaireTemplateLineId,questionTemplateOptId |

---

### 99 pm_cl_quesnaire_template_header -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~13 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(10) unsigned | NO | - | PRI, auto_increment | - | 主键ID |
| questionnaireTemplateNum | varchar(45) | NO | - | - | 问卷模板编号 | 问卷模板编号 |
| questionnaireTemplateName | varchar(200) | NO | - | - | 问卷模板名称 | 问卷模板名称 |
| questionnaireScore | double | NO | 0 | - | 问卷总分数 | 问卷总分数 |
| questionnairePassScore | double | NO | 0 | - | 问卷达标分数 | 问卷达标分数 |
| questionnaireStatus | int(11) | NO | 0 | - | 问卷状态 | 问卷状态 |
| effectiveStartTime | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveEndTime | datetime | YES | - | - | - | 业务含义待确认 |
| createdTime | datetime | YES | - | - | - | 业务含义待确认 |
| updatedTime | datetime | YES | - | - | - | 业务含义待确认 |
| createdPerson | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updatedPerson | varchar(25) | YES | - | - | - | 业务含义待确认 |
| quesType | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| markIndexs | varchar(45) | YES | - | - | 问卷计分规则的index | 问卷计分规则的index |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| quesType | BTREE | NON-UNIQUE | quesType |

---

### 100 pm_cl_quesnaire_template_line -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~80 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) unsigned | NO | - | PRI, auto_increment | - | 主键ID |
| questionContent | varchar(200) | NO | - | - | 题目内容 | 题目内容 |
| questionType | int(11) | NO | - | - | 题目类型,如:多选\单选 | 题目类型,如:多选\单选 |
| questionScore | double | NO | 0 | - | 题目分数 | 题目分数 |
| questionRemark | varchar(200) | YES | - | - | 题目备注 | 题目备注 |
| questionNum | int(11) | NO | 0 | - | 问题编号,表示了问卷中问题的顺序 | 问题编号,表示了问卷中问题的顺序 |
| quesnaireTemplateHeaderId | int(11) | NO | 0 | MUL | 问卷模板Id | 问卷模板Id |
| questionStatus | int(11) | YES | 0 | - | - | 业务含义待确认 |
| effectiveStartTime | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveEndTime | datetime | YES | - | - | - | 业务含义待确认 |
| createdTime | datetime | YES | - | - | - | 业务含义待确认 |
| updatedTime | datetime | YES | - | - | - | 业务含义待确认 |
| createdPerson | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updatedPerson | varchar(25) | YES | - | - | - | 业务含义待确认 |
| questionTypeForCB | varchar(10) | YES | - | - | 回访问题类型 | 回访问题类型 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| id_UNIQUE | BTREE | UNIQUE | id |
| PRIMARY | BTREE | UNIQUE | id |
| quesnaireTemplateHeaderId | BTREE | NON-UNIQUE | quesnaireTemplateHeaderId |

---

### 101 pm_cl_quesnaire_template_options -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~231 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) unsigned | NO | - | PRI, auto_increment | - | 主键ID |
| questionId | int(11) | NO | 0 | - | 题目Id | 题目Id |
| questionOptionNum | int(11) | NO | 0 | - | 选项编号 | 选项编号 |
| questionOptionsContent | varchar(200) | NO | - | - | 选项内容 | 选项内容 |
| questionOptionScore | double | YES | 0 | - | 选项分数 | 选项分数 |
| quesnaireTemplateHeaderId | int(11) | NO | 0 | - | 问卷模板Id | 问卷模板Id |
| effectiveStartTime | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveEndTime | datetime | YES | - | - | - | 业务含义待确认 |
| createdTime | datetime | YES | - | - | - | 业务含义待确认 |
| updatedTime | datetime | YES | - | - | - | 业务含义待确认 |
| createdPerson | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updatedPerson | varchar(25) | YES | - | - | - | 业务含义待确认 |
| quesLineType | varchar(10) | YES | - | - | 问题类型 | 问题类型 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| id_UNIQUE | BTREE | UNIQUE | id |
| PRIMARY | BTREE | UNIQUE | id |

---

### 102 pm_column_of_relationship -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~14 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| projectType | int(11) | YES | - | - | - | 业务含义待确认 |
| columnCode | varchar(45) | YES | - | - | - | 业务含义待确认 |
| colemnName | varchar(45) | YES | - | - | - | 业务含义待确认 |
| columnDesc | varchar(45) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(45) | YES | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 103 pm_common_related_data -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~572 行 |
| 数据大小 | 4.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| type | varchar(64) | NO | - | MUL | 数据类型 | 数据类型 |
| objType | varchar(64) | NO | - | - | 主数据类型 | 主数据类型 |
| objId | int(11) | NO | 0 | - | 主数据Id | 主数据Id |
| field1 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| field2 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| field3 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| field4 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| field5 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| field6 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| field7 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| field8 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| field9 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| field10 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| disabled | bit(1) | NO | b'0' | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| customInfo | json | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| type | BTREE | NON-UNIQUE | type,objType,objId |

---

### 104 pm_daily_report -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~11221 行 |
| 数据大小 | 109.58 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| projectId | int(11) | NO | -1 | MUL | 项目头信息主键 | 项目头信息主键 |
| projectType | varchar(45) | NO |  | MUL | 项目类型，售前:20/售后:10 | 项目类型，售前:20/售后:10 |
| projectCode | varchar(45) | NO |  | MUL | 项目名称 | 项目名称 |
| projectName | varchar(200) | YES |  | - | 项目名称 | 项目名称 |
| contractNo | varchar(255) | YES |  | - | 合同号 | 合同号 |
| officeCode | varchar(25) | YES | - | MUL | 办事处编码 | 办事处编码 |
| type | varchar(45) | YES | - | MUL | 任务性质 | 任务性质 |
| category | varchar(45) | YES | - | MUL | 任务分类 | 任务分类 |
| subCategory | varchar(45) | YES | - | MUL | 任务小类 | 任务小类 |
| processTime | datetime | YES | - | - | 处理时间 | 处理时间 |
| processDesc | varchar(1024) | YES | - | - | 事项描述 | 事项描述 |
| processStep | varchar(1024) | YES | - | - | 解决进展 | 解决进展 |
| remainProblem | varchar(1024) | YES | - | - | 遗留问题 | 遗留问题 |
| customerInteraction | varchar(1024) | YES | - | - | 客户互动情况 | 客户互动情况 |
| transitHour | float | YES | 0 | - | 在途耗时(h) | 在途耗时(h) |
| processHour | float | YES | 0 | - | 处理耗时(h) | 处理耗时(h) |
| itemModel | varchar(255) | YES | - | - | 产品型号 | 产品型号 |
| softVersion | varchar(255) | YES | - | - | 在网版本 | 在网版本 |
| enabledFeatures | varchar(255) | YES | - | - | 启用功能 | 启用功能 |
| customTos | varchar(255) | YES | - | - | 自定义主送 | 自定义主送 |
| customCcs | varchar(255) | YES | - | - | 自定义抄送 | 自定义抄送 |
| projectExecutionState | varchar(45) | YES |  | - | 项目实施状态 | 项目实施状态 |
| hasReport | bit(1) | NO | b'0' | - | 是否有巡检报告 | 是否有巡检报告 |
| quesnaireId | int(11) | YES | - | - | 问卷ID | 问卷ID |
| deliverFileIds | varchar(255) | YES |  | - | 交付件，fnd_files id | 交付件，fnd_files id |
| remark | varchar(1024) | YES | - | - | 备注 | 备注 |
| isReported | bit(1) | YES | b'0' | - | 已上报 | 已上报 |
| qualityFactor | float | YES | 0 | - | 质量系数 | 质量系数 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |
| status | varchar(25) | YES | - | - | 状态 | 状态 |
| disabled | bit(1) | YES | b'0' | - | 失效标记 | 失效标记 |
| createTime | datetime | YES | - | MUL | 创建时间 | 创建时间 |
| createBy | varchar(45) | YES | - | MUL | 创建用户 | 创建用户 |
| updateTime | datetime | YES | - | - | 最新更新时间 | 最新更新时间 |
| updateBy | varchar(45) | YES | - | - | 最新更新用户 | 最新更新用户 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| category | BTREE | NON-UNIQUE | category,subCategory |
| createBy | BTREE | NON-UNIQUE | createBy |
| createTime | BTREE | NON-UNIQUE | createTime |
| officeCode | BTREE | NON-UNIQUE | officeCode |
| PRIMARY | BTREE | UNIQUE | id |
| projectCode | BTREE | NON-UNIQUE | projectCode |
| projectId | BTREE | NON-UNIQUE | projectId |
| projectType | BTREE | NON-UNIQUE | projectType |
| subCategory | BTREE | NON-UNIQUE | subCategory |
| type | BTREE | NON-UNIQUE | type |

---

### 105 pm_dispatch_project_header -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~328 行 |
| 数据大小 | 384.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| dispatchName | varchar(512) | YES |  | - | 外派名称 | 外派名称 |
| dispatchNo | varchar(64) | YES |  | MUL | 外派合同号 | 外派合同号 |
| dispatchSeq | varchar(64) | YES | - | UNI | 外派编号 | 外派编号 |
| contractNos | varchar(2048) | YES |  | - | 项目合同号 | 项目合同号 |
| projectIds | varchar(1024) | YES |  | - | 外派的项目ID | 外派的项目ID |
| type | varchar(25) | YES | - | - | 外派类型 | 外派类型 |
| state | int(11) | NO | 0 | - | 外派状态 | 外派状态 |
| peopleNum | int(11) | YES | 0 | - | 外派人数 | 外派人数 |
| callbackState | int(11) | YES | - | - | 回访状态 | 回访状态 |
| facilitatorId | int(11) | YES | - | - | 服务商ID | 服务商ID |
| facilitatorCode | varchar(25) | YES | - | MUL | 服务商编码 | 服务商编码 |
| facilitatorName | varchar(64) | YES |  | - | 服务商名 | 服务商名 |
| bankInfo | varchar(255) | YES |  | - | 服务商开户地址 | 服务商开户地址 |
| bankAccount | varchar(64) | YES |  | - | 服务商收款账户 | 服务商收款账户 |
| officeCode | varchar(25) | YES |  | MUL | 办事处部门 | 办事处部门 |
| profitDepCode | varchar(25) | YES |  | MUL | 收益部门 | 收益部门 |
| dutyPerson | varchar(25) | YES | - | - | 项目总接口人 | 项目总接口人 |
| officeDutyPerson | varchar(25) | YES | - | - | 办事处接口人 | 办事处接口人 |
| isAccrued | bit(1) | YES | - | - | 是否计提 | 是否计提 |
| isInvoiced | bit(1) | YES | - | - | 是否提供发票 | 是否提供发票 |
| dispatchAmount | varchar(25) | YES |  | - | 外派价 | 外派价 |
| prepaidInfo | varchar(255) | YES |  | - | 预付信息（比例、金额） | 预付信息（比例、金额） |
| prepaidRule | varchar(255) | YES |  | - | 预付遵循原则 | 预付遵循原则 |
| acceptanceInfo | varchar(255) | YES |  | - | 验收要求 | 验收要求 |
| reason | varchar(512) | YES |  | - | 外派原因 | 外派原因 |
| remark | varchar(512) | YES |  | - | 备注 | 备注 |
| dispatchTime | datetime | YES | - | - | 派单时间 | 派单时间 |
| smsProjectCode | varchar(255) | YES |  | MUL | SMS项目编码 | SMS项目编码 |
| smsSubmitTime | datetime | YES | - | - | SMS项目提交时间 | SMS项目提交时间 |
| smsProjectAmount | varchar(25) | YES |  | - | SMS项目金额 | SMS项目金额 |
| smsAfProjectAmount | varchar(25) | YES |  | - | 安服项目金额 | 安服项目金额 |
| effectiveFrom | datetime | YES | - | - | 有效开始时间 | 有效开始时间 |
| effectiveTo | datetime | YES | - | - | 有效结束时间 | 有效结束时间 |
| disabled | bit(1) | YES | b'0' | - | 删除状态 | 删除状态 |
| dispatched | bit(1) | YES | b'0' | - | 派单状态 | 派单状态 |
| settled | bit(1) | YES | b'0' | - | 结算状态 | 结算状态 |
| createBy | varchar(25) | YES |  | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES |  | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| facilitatorId | BTREE | NON-UNIQUE | facilitatorCode |
| officeCode | BTREE | NON-UNIQUE | officeCode |
| PRIMARY | BTREE | UNIQUE | id |
| profitDepCode | BTREE | NON-UNIQUE | profitDepCode |
| smsProjectCode | BTREE | NON-UNIQUE | smsProjectCode |
| subcontractNo | BTREE | NON-UNIQUE | dispatchNo |
| UNIQUE_dispatchSeq | BTREE | UNIQUE | dispatchSeq |

---

### 106 pm_dispatch_project_settlement -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~72 行 |
| 数据大小 | 128.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| settleSeq | varchar(512) | YES | - | - | 结算编号 | 结算编号 |
| dispatchId | int(11) | NO | - | MUL | 派单Id | 派单Id |
| dispatchSeq | varchar(25) | NO | - | MUL | 派单编号 | 派单编号 |
| progressDesc | varchar(1024) | YES | - | - | 实施进展 | 实施进展 |
| progressRatio | float(3,2) | YES | - | - | 实施比例 | 实施比例 |
| acceptanceDesc | varchar(1024) | YES | - | - | 验收进度 | 验收进度 |
| acceptanceRatio | varchar(10) | YES | - | - | 验收比例 | 验收比例 |
| ratio | varchar(10) | YES | - | - | 此次付款比例 | 此次付款比例 |
| amount | varchar(25) | YES | - | - | 此次付款金额 | 此次付款金额 |
| memo | varchar(512) | YES | - | - | 此次付款说明 | 此次付款说明 |
| confirmTime | datetime | YES | - | - | 提交时间 | 提交时间 |
| paymentTime | datetime | YES | - | - | 付款时间 | 付款时间 |
| remark | varchar(512) | YES | - | - | 备注 | 备注 |
| state | int(1) | YES | 0 | - | 状态 | 状态 |
| disabled | bit(1) | YES | b'0' | - | 删除标记 | 删除标记 |
| quarter | int(4) | YES | - | - | 结算季度 | 结算季度 |
| month | int(2) | YES | - | - | 结算月份 | 结算月份 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |
| sseId | bigint(20) | YES | -1 | - | sse报销单审批行ID,0：会进行匹配跟新 | sse报销单审批行ID,0：会进行匹配跟新 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| settled | bit(1) | YES | b'0' | - | 结算状态 | 结算状态 |
| year | int(4) | YES | - | - | 结算年份 | 结算年份 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| dispatchId | BTREE | NON-UNIQUE | dispatchId |
| dispatchSeq | BTREE | NON-UNIQUE | dispatchSeq |
| PRIMARY | BTREE | UNIQUE | id |

---

### 107 pm_dispatch_project_settlement_from_d365 -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~44 行 |
| 数据大小 | 48.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| slipId | bigint(20) | YES | - | MUL | - | 业务含义待确认 |
| inventTransId | varchar(20) | YES | - | - | - | 业务含义待确认 |
| vendAccount | varchar(20) | YES | - | - | - | 业务含义待确认 |
| innerInvoiceId | varchar(20) | YES | - | - | - | 业务含义待确认 |
| invoiceDate | timestamp | YES | - | - | - | 业务含义待确认 |
| invoiceId | varchar(20) | YES | - | - | - | 业务含义待确认 |
| purchId | varchar(20) | YES | - | MUL | - | 业务含义待确认 |
| purchName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| purchPoolId | varchar(10) | YES | - | - | - | 业务含义待确认 |
| packingSlipId | varchar(20) | YES | - | MUL | - | 业务含义待确认 |
| packingSlipRemark | varchar(255) | YES | - | - | - | 业务含义待确认 |
| slipQty | decimal(32,6) | YES | - | - | - | 业务含义待确认 |
| receiveQty | decimal(32,6) | YES | - | - | - | 业务含义待确认 |
| invoiceQty | decimal(32,6) | YES | - | - | - | 业务含义待确认 |
| price | decimal(32,6) | YES | - | - | - | 业务含义待确认 |
| invoicePrice | decimal(32,6) | YES | - | - | - | 业务含义待确认 |
| receiveAmount | decimal(38,6) | YES | - | - | - | 业务含义待确认 |
| invoiceAmount | decimal(38,6) | YES | - | - | - | 业务含义待确认 |
| settleQty | decimal(38,6) | YES | - | - | - | 业务含义待确认 |
| lineAmount | decimal(38,6) | YES | - | - | - | 业务含义待确认 |
| invoiceAmountTotal | decimal(32,6) | YES | - | - | - | 业务含义待确认 |
| settleAmountTotal | decimal(38,6) | YES | - | - | - | 业务含义待确认 |
| settleAmount | decimal(38,6) | YES | - | - | - | 业务含义待确认 |
| confirmTime | timestamp | YES | - | - | - | 业务含义待确认 |
| settleTime | timestamp | YES | - | - | - | 业务含义待确认 |
| projectProgress | varchar(64) | YES | - | - | - | 业务含义待确认 |
| otherSysNum | varchar(20) | YES | - | - | - | 业务含义待确认 |
| partition | bigint(20) | YES | - | - | - | 业务含义待确认 |
| dataAreaId | varchar(4) | YES | - | - | - | 业务含义待确认 |
| settleId | bigint(20) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| packingSlipId | BTREE | NON-UNIQUE | packingSlipId |
| purchId | BTREE | NON-UNIQUE | purchId |
| slipId | BTREE | NON-UNIQUE | slipId |

---

### 108 pm_facilitator -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~24 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| code | varchar(25) | YES | - | UNI | 服务商编号 | 服务商编号 |
| account | varchar(25) | YES | - | MUL | 服务商账号 | 服务商账号 |
| name | varchar(64) | YES | - | - | 服务商名 | 服务商名 |
| type | varchar(64) | YES | - | - | 合作类型 | 合作类型 |
| bankInfo | varchar(255) | YES | - | - | 开户行信息 | 开户行信息 |
| bankAccount | varchar(64) | YES | - | - | 收款账户 | 收款账户 |
| cnapsCode | varchar(25) | YES | - | - | 联行号 | 联行号 |
| contacts | varchar(64) | YES | - | - | 联系人 | 联系人 |
| tel | varchar(64) | YES | - | - | 联系电话 | 联系电话 |
| email | varchar(64) | YES | - | - | 联系邮箱 | 联系邮箱 |
| state | bit(1) | YES | b'1' | - | 状态 | 状态 |
| needApprove | bit(1) | YES | b'0' | - | 是否评审 | 是否评审 |
| approveStatus | int(1) | YES | 0 | - | 审批结果 | 审批结果 |
| deliveryIds | varchar(25) | YES | - | - | 附件材料 | 附件材料 |
| relateType | varchar(25) | YES | - | - | 关联类型 | 关联类型 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(45) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(45) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| account | BTREE | UNIQUE | account,state |
| code | BTREE | UNIQUE | code |
| PRIMARY | BTREE | UNIQUE | id |

---

### 109 pm_facilitator_form_d365 -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~761 行 |
| 数据大小 | 208.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| code | varchar(25) | YES | - | UNI | 服务商编号 | 服务商编号 |
| account | varchar(25) | YES | - | MUL | 服务商账号 | 服务商账号 |
| name | varchar(64) | YES | - | - | 服务商名 | 服务商名 |
| type | varchar(64) | YES | - | - | 合作类型 | 合作类型 |
| bankInfo | varchar(255) | YES | - | - | 开户行信息 | 开户行信息 |
| bankAccount | varchar(64) | YES | - | - | 收款账户 | 收款账户 |
| cnapsCode | varchar(25) | YES | - | - | 联行号 | 联行号 |
| contacts | varchar(64) | YES | - | - | 联系人 | 联系人 |
| tel | varchar(64) | YES | - | - | 联系电话 | 联系电话 |
| email | varchar(64) | YES | - | - | 联系邮箱 | 联系邮箱 |
| state | bit(1) | YES | b'1' | - | 状态 | 状态 |
| needApprove | bit(1) | YES | b'0' | - | 是否评审 | 是否评审 |
| approveStatus | int(1) | YES | 0 | - | 审批结果 | 审批结果 |
| deliveryIds | varchar(25) | YES | - | - | 附件材料 | 附件材料 |
| relateType | varchar(25) | YES | - | - | 关联类型 | 关联类型 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(45) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(45) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| account | BTREE | UNIQUE | account,code |
| code | BTREE | UNIQUE | code |
| PRIMARY | BTREE | UNIQUE | id |

---

### 110 pm_notification_template -- 消息模板（邮件、短信等）

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 消息模板（邮件、短信等） |
| 数据量 | ~66 行 |
| 数据大小 | 64.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| templateCode | varchar(45) | YES | - | - | - | 业务含义待确认 |
| notificationObject | varchar(45) | YES | - | - | 主题 | 主题 |
| notificationContent | text | YES | - | - | 内容 | 内容 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(45) | YES | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 111 pm_order_data_from_erp -- 从ERP刷新过来的原始合同信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 从ERP刷新过来的原始合同信息 |
| 数据量 | ~52940 行 |
| 数据大小 | 12.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| orderNumber | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| contractNo | varchar(50) | YES | - | MUL | - | 业务含义待确认 |
| orderExecNumber | varchar(50) | YES | - | MUL | - | 业务含义待确认 |
| orderCreateTime | datetime | YES | - | - | - | 业务含义待确认 |
| customerRequireTime | datetime | YES | - | - | - | 业务含义待确认 |
| customerCode | varchar(55) | YES | - | - | - | 业务含义待确认 |
| customerName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| projectName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| orderComment | varchar(2048) | YES | - | - | - | 业务含义待确认 |
| orderType | int(11) | YES | 0 | MUL | 0 正常销售 1 退货 | 0 正常销售 1 退货 |
| compCode | varchar(25) | YES | 0 | - | 公司编码 | 公司编码 |
| salesType | varchar(25) | YES | 01 | - | 销售类型,01:正常合同，02:借转销，14:销售类借货 | 销售类型,01:正常合同，02:借转销，14:销售类借货 |
| source | varchar(25) | YES | SAP | - | 数据来源，默认:SAP,D365,总代借货:SMS | 数据来源，默认:SAP,D365,总代借货:SMS |
| customInfo | json | YES | - | - | 自定义字段 | 自定义字段 |
| syncTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | contractNo |
| orderExecNumber | BTREE | NON-UNIQUE | orderExecNumber |
| orderNumber | BTREE | NON-UNIQUE | orderNumber |
| orderType | BTREE | NON-UNIQUE | orderType,salesType |
| PRIMARY | BTREE | UNIQUE | id |

---

### 112 pm_order_data_from_erp_d365 -- 从ERP刷新过来的原始合同信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 从ERP刷新过来的原始合同信息 |
| 数据量 | ~1790 行 |
| 数据大小 | 448.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| orderNumber | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| contractNo | varchar(50) | YES | - | MUL | - | 业务含义待确认 |
| orderExecNumber | varchar(50) | YES | - | MUL | - | 业务含义待确认 |
| orderCreateTime | datetime | YES | - | - | - | 业务含义待确认 |
| customerRequireTime | datetime | YES | - | - | - | 业务含义待确认 |
| customerCode | varchar(55) | YES | - | - | - | 业务含义待确认 |
| customerName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| projectName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| orderComment | varchar(2048) | YES | - | - | - | 业务含义待确认 |
| orderType | int(11) | YES | 0 | - | 0 正常销售 1 退货 | 0 正常销售 1 退货 |
| compCode | varchar(25) | YES | 0 | - | 公司编码 | 公司编码 |
| salesType | varchar(25) | YES | 01 | - | 销售类型,01:正常合同，02:借转销，14:销售类借货 | 销售类型,01:正常合同，02:借转销，14:销售类借货 |
| customInfo | json | YES | - | - | 自定义字段 | 自定义字段 |
| syncTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | contractNo |
| orderExecNumber | BTREE | NON-UNIQUE | orderExecNumber |
| orderNumber | BTREE | NON-UNIQUE | orderNumber |
| PRIMARY | BTREE | UNIQUE | id |

---

### 113 pm_order_data_from_erp_sap -- 从ERP刷新过来的原始合同信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 从ERP刷新过来的原始合同信息 |
| 数据量 | ~47708 行 |
| 数据大小 | 10.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| orderNumber | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| contractNo | varchar(50) | YES | - | MUL | - | 业务含义待确认 |
| orderExecNumber | varchar(50) | YES | - | MUL | - | 业务含义待确认 |
| orderCreateTime | datetime | YES | - | - | - | 业务含义待确认 |
| customerRequireTime | datetime | YES | - | - | - | 业务含义待确认 |
| customerCode | varchar(55) | YES | - | - | - | 业务含义待确认 |
| customerName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| projectName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| orderComment | varchar(255) | YES | - | - | - | 业务含义待确认 |
| orderType | int(11) | YES | 0 | - | 0 正常销售 1 退货 | 0 正常销售 1 退货 |
| compCode | varchar(25) | YES | 0 | - | 公司编码 | 公司编码 |
| salesType | varchar(25) | YES | 01 | - | 销售类型,01:正常合同，02:借转销，14:销售类借货 | 销售类型,01:正常合同，02:借转销，14:销售类借货 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | contractNo |
| orderExecNumber | BTREE | NON-UNIQUE | orderExecNumber |
| orderNumber | BTREE | NON-UNIQUE | orderNumber |
| PRIMARY | BTREE | UNIQUE | id |

---

### 114 pm_order_data_from_erp_source -- 从ERP刷新过来的原始合同信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 从ERP刷新过来的原始合同信息 |
| 数据量 | ~49054 行 |
| 数据大小 | 12.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| orderNumber | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| contractNo | varchar(50) | YES | - | MUL | - | 业务含义待确认 |
| orderExecNumber | varchar(50) | YES | - | MUL | - | 业务含义待确认 |
| orderExecNumberShort | varchar(50) | YES | - | - | - | 业务含义待确认 |
| orderCreateTime | datetime | YES | - | - | - | 业务含义待确认 |
| customerRequireTime | datetime | YES | - | - | - | 业务含义待确认 |
| customerCode | varchar(55) | YES | - | - | - | 业务含义待确认 |
| customerName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| projectName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| orderComment | varchar(2048) | YES | - | - | - | 业务含义待确认 |
| orderType | int(11) | YES | 0 | MUL | 0 正常销售 1 退货 | 0 正常销售 1 退货 |
| compCode | varchar(25) | YES | 0 | - | 公司编码 | 公司编码 |
| salesType | varchar(25) | YES | 01 | - | 销售类型,01:正常合同，02:借转销，14:销售类借货 | 销售类型,01:正常合同，02:借转销，14:销售类借货 |
| source | varchar(25) | YES | SAP | - | 数据来源，默认:SAP,D365,总代借货:SMS | 数据来源，默认:SAP,D365,总代借货:SMS |
| customInfo | json | YES | - | - | 自定义字段 | 自定义字段 |
| syncTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | contractNo |
| orderExecNumber | BTREE | NON-UNIQUE | orderExecNumber |
| orderNumber | BTREE | NON-UNIQUE | orderNumber |
| orderType | BTREE | NON-UNIQUE | orderType,salesType |
| PRIMARY | BTREE | UNIQUE | id |

---

### 115 pm_order_line_from_erp -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~219652 行 |
| 数据大小 | 26.56 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| orderNumber | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| lineNum | varchar(25) | YES | - | - | - | 业务含义待确认 |
| itemCode | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| itemDesc | varchar(255) | YES | - | - | - | 业务含义待确认 |
| orderQuantity | int(11) | YES | - | - | - | 业务含义待确认 |
| openQuantity | int(11) | YES | - | - | - | 业务含义待确认 |
| bundleCode | varchar(25) | YES | - | - | - | 业务含义待确认 |
| warrantyMonth | int(11) | YES | - | - | - | 业务含义待确认 |
| lineType | int(11) | YES | 0 | - | - | 业务含义待确认 |
| compCode | varchar(25) | YES | 0 | - | 公司编码 | 公司编码 |
| profitCenter | varchar(25) | YES | - | - | 利润中心 | 利润中心 |
| realOrderExecNumber | varchar(25) | YES | - | - | 真实执行单号 | 真实执行单号 |
| source | varchar(25) | YES | SAP | - | 数据来源，默认:SAP,D365,总代借货:SMS | 数据来源，默认:SAP,D365,总代借货:SMS |
| customInfo | json | YES | - | - | 自定义字段 | 自定义字段 |
| syncTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| itemCode | BTREE | NON-UNIQUE | itemCode |
| orderNumber | BTREE | NON-UNIQUE | orderNumber |
| PRIMARY | BTREE | UNIQUE | id |

---

### 116 pm_order_line_from_erp_d365 -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~7839 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| orderNumber | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| lineNum | varchar(25) | YES | - | - | - | 业务含义待确认 |
| itemCode | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| itemDesc | varchar(255) | YES | - | - | - | 业务含义待确认 |
| orderQuantity | int(11) | YES | - | - | - | 业务含义待确认 |
| openQuantity | int(11) | YES | - | - | - | 业务含义待确认 |
| bundleCode | varchar(25) | YES | - | - | - | 业务含义待确认 |
| warrantyMonth | int(11) | YES | - | - | - | 业务含义待确认 |
| lineType | int(11) | YES | 0 | - | - | 业务含义待确认 |
| compCode | varchar(25) | YES | 0 | - | 公司编码 | 公司编码 |
| profitCenter | varchar(25) | YES | - | - | 利润中心 | 利润中心 |
| realOrderExecNumber | varchar(25) | YES | - | - | 真实执行单号 | 真实执行单号 |
| customInfo | json | YES | - | - | 自定义字段 | 自定义字段 |
| syncTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| itemCode | BTREE | NON-UNIQUE | itemCode |
| orderNumber | BTREE | NON-UNIQUE | orderNumber,lineNum |
| PRIMARY | BTREE | UNIQUE | id |

---

### 117 pm_order_line_from_erp_sap -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~208448 行 |
| 数据大小 | 24.55 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| orderNumber | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| lineNum | int(11) | YES | - | - | - | 业务含义待确认 |
| itemCode | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| itemDesc | varchar(255) | YES | - | - | - | 业务含义待确认 |
| orderQuantity | int(11) | YES | - | - | - | 业务含义待确认 |
| openQuantity | int(11) | YES | - | - | - | 业务含义待确认 |
| bundleCode | varchar(25) | YES | - | - | - | 业务含义待确认 |
| warrantyMonth | int(11) | YES | - | - | - | 业务含义待确认 |
| lineType | int(11) | YES | 0 | - | - | 业务含义待确认 |
| compCode | varchar(25) | YES | 0 | - | 公司编码 | 公司编码 |
| profitCenter | varchar(25) | YES | - | - | 利润中心 | 利润中心 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| itemCode | BTREE | NON-UNIQUE | itemCode |
| orderNumber | BTREE | NON-UNIQUE | orderNumber,lineNum |
| PRIMARY | BTREE | UNIQUE | id |

---

### 118 pm_order_line_from_erp_source -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~205968 行 |
| 数据大小 | 26.56 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| orderNumber | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| lineNum | varchar(25) | YES | - | - | - | 业务含义待确认 |
| itemCode | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| itemDesc | varchar(255) | YES | - | - | - | 业务含义待确认 |
| orderQuantity | int(11) | YES | - | - | - | 业务含义待确认 |
| openQuantity | int(11) | YES | - | - | - | 业务含义待确认 |
| bundleCode | varchar(25) | YES | - | - | - | 业务含义待确认 |
| warrantyMonth | int(11) | YES | - | - | - | 业务含义待确认 |
| lineType | int(11) | YES | 0 | - | - | 业务含义待确认 |
| compCode | varchar(25) | YES | 0 | - | 公司编码 | 公司编码 |
| profitCenter | varchar(25) | YES | - | - | 利润中心 | 利润中心 |
| realOrderExecNumber | varchar(25) | YES | - | - | 真实执行单号 | 真实执行单号 |
| source | varchar(25) | YES | SAP | - | 数据来源，默认:SAP,D365,总代借货:SMS | 数据来源，默认:SAP,D365,总代借货:SMS |
| customInfo | json | YES | - | - | 自定义字段 | 自定义字段 |
| syncTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| itemCode | BTREE | NON-UNIQUE | itemCode |
| orderNumber | BTREE | NON-UNIQUE | orderNumber,lineNum |
| PRIMARY | BTREE | UNIQUE | id |

---

### 119 pm_pb_plan_from_sms -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~43912 行 |
| 数据大小 | 6.02 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 主键 | 主键 |
| contractNo | varchar(25) | YES | - | MUL | 合同号 | 合同号 |
| batchCode | varchar(10) | YES | - | - | 批次信息 | 批次信息 |
| basicDataName | varchar(20) | YES | - | - | 活动（款项名称） | 活动（款项名称） |
| referenceEventName | varchar(20) | YES | - | - | 参照事件名称 | 参照事件名称 |
| eventPlanHappenDate | datetime | YES | - | - | 事件计划发生日期 | 事件计划发生日期 |
| afterDaysNum | int(11) | YES | 0 | - | 后推天数 | 后推天数 |
| eventActualFinishDate | datetime | YES | - | - | 事件实际完成日期 | 事件实际完成日期 |
| marketingFeedback | varchar(2000) | YES | - | - | 销售反馈 | 销售反馈 |
| createBy | varchar(45) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(45) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |
| dataSource | varchar(25) | YES | SMS | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo_IDX | BTREE | NON-UNIQUE | contractNo |
| PRIMARY | BTREE | UNIQUE | id |

---

### 120 pm_pb_plan_from_sms_history -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~16133 行 |
| 数据大小 | 2.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 主键 | 主键 |
| contractNo | varchar(25) | YES | - | MUL | 合同号 | 合同号 |
| batchCode | varchar(10) | YES | - | - | 批次信息 | 批次信息 |
| basicDataName | varchar(20) | YES | - | - | 活动（款项名称） | 活动（款项名称） |
| referenceEventName | varchar(20) | YES | - | - | 参照事件名称 | 参照事件名称 |
| eventPlanHappenDate | datetime | YES | - | - | 事件计划发生日期 | 事件计划发生日期 |
| afterDaysNum | int(11) | YES | 0 | - | 后推天数 | 后推天数 |
| eventActualFinishDate | datetime | YES | - | - | 事件实际完成日期 | 事件实际完成日期 |
| marketingFeedback | varchar(2000) | YES | - | - | 销售反馈 | 销售反馈 |
| createBy | varchar(10) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(10) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |
| dataSource | varchar(25) | YES | SMS | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo_IDX | BTREE | NON-UNIQUE | contractNo |
| PRIMARY | BTREE | UNIQUE | id |

---

### 121 pm_person_from_oa -- 销售联系电话信息从OA同步

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 销售联系电话信息从OA同步 |
| 数据量 | ~1480 行 |
| 数据大小 | 144.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| salesmanCode | varchar(45) | YES | - | MUL | - | 业务含义待确认 |
| salesmanTel | varchar(45) | YES | - | - | - | 业务含义待确认 |
| salesmanName | varchar(45) | YES | - | - | - | 业务含义待确认 |
| salesmanMail | varchar(100) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| salesmanCode1 | BTREE | NON-UNIQUE | salesmanCode |

---

### 122 pm_presales_lend_2_delivery_off_from_sap -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~44530 行 |
| 数据大小 | 4.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| orderNumber | varchar(11) | YES | - | MUL | - | 业务含义待确认 |
| lineId | int(11) | YES | - | - | - | 业务含义待确认 |
| itemCode | varchar(10) | YES | - | - | - | 业务含义待确认 |
| ppliCode | varchar(25) | YES | - | MUL | 借货执行单号 | 借货执行单号 |
| contract | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| deliveryDate | date | YES | - | - | 发货时间 | 发货时间 |
| rmaDate | date | YES | - | - | 退货时间 | 退货时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contract | BTREE | NON-UNIQUE | contract |
| orderNumber | BTREE | NON-UNIQUE | orderNumber,lineId |
| ppliCode | BTREE | NON-UNIQUE | ppliCode |
| PRIMARY | BTREE | UNIQUE | id |

---

### 123 pm_presales_lend_2_rma_from_sms -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~0 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | 0 | - | - | 主键ID |
| orderNumber | varchar(11) | YES | - | MUL | - | 业务含义待确认 |
| ppliCode | varchar(25) | YES | - | MUL | 借货执行单号 | 借货执行单号 |
| orderType | varchar(10) | YES | - | - | - | 业务含义待确认 |
| contract | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| customer | varchar(255) | YES | - | - | - | 业务含义待确认 |
| projectName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| businessunit | varchar(50) | YES | - | - | - | 业务含义待确认 |
| office | varchar(20) | YES | - | - | - | 业务含义待确认 |
| dutyperson | varchar(10) | YES | - | - | - | 业务含义待确认 |
| itemcode | varchar(10) | YES | - | - | - | 业务含义待确认 |
| description | varchar(255) | YES | - | - | - | 描述 |
| productfirstName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| productName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| orderQty | int(11) | YES | - | - | - | 业务含义待确认 |
| dlvQty | int(11) | YES | - | - | - | 业务含义待确认 |
| rmaQty | int(11) | YES | - | - | - | 业务含义待确认 |
| lineStatus | varchar(5) | YES | - | - | - | 业务含义待确认 |
| createDate | date | YES | - | - | - | 业务含义待确认 |
| lineId | int(11) | YES | - | - | - | 业务含义待确认 |
| systemId | int(11) | YES | - | - | - | 业务含义待确认 |
| canceled | char(1) | YES | - | - | - | 业务含义待确认 |
| dataSource | varchar(25) | YES | SMS | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contract | BTREE | NON-UNIQUE | contract,itemcode |
| orderNumber | BTREE | NON-UNIQUE | orderNumber,lineId |
| ppliCode | BTREE | NON-UNIQUE | ppliCode,itemcode |

---

### 124 pm_presales_lend_2_rma_from_sms_history -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~35449 行 |
| 数据大小 | 14.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | 0 | - | - | 主键ID |
| orderNumber | varchar(11) | YES | - | MUL | - | 业务含义待确认 |
| ppliCode | varchar(25) | YES | - | MUL | 借货执行单号 | 借货执行单号 |
| orderType | varchar(10) | YES | - | - | - | 业务含义待确认 |
| contract | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| customer | varchar(255) | YES | - | - | - | 业务含义待确认 |
| projectName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| businessunit | varchar(50) | YES | - | - | - | 业务含义待确认 |
| office | varchar(20) | YES | - | - | - | 业务含义待确认 |
| dutyperson | varchar(10) | YES | - | - | - | 业务含义待确认 |
| itemcode | varchar(10) | YES | - | - | - | 业务含义待确认 |
| description | varchar(255) | YES | - | - | - | 描述 |
| productfirstName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| productName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| orderQty | int(11) | YES | - | - | - | 业务含义待确认 |
| dlvQty | int(11) | YES | - | - | - | 业务含义待确认 |
| rmaQty | int(11) | YES | - | - | - | 业务含义待确认 |
| lineStatus | varchar(5) | YES | - | - | - | 业务含义待确认 |
| createDate | date | YES | - | - | - | 业务含义待确认 |
| lineId | int(11) | YES | - | - | - | 业务含义待确认 |
| systemId | int(11) | YES | - | - | - | 业务含义待确认 |
| canceled | char(1) | YES | - | - | - | 业务含义待确认 |
| dataSource | varchar(25) | YES | SMS | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contract | BTREE | NON-UNIQUE | contract,itemcode |
| orderNumber | BTREE | NON-UNIQUE | orderNumber,lineId |
| ppliCode | BTREE | NON-UNIQUE | ppliCode,itemcode |

---

### 125 pm_presales_lend_2_sale_from_sms -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~0 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| productfirstName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| productName | varchar(128) | YES | - | - | - | 业务含义待确认 |
| projectCode | varchar(255) | NO | - | MUL | - | 业务含义待确认 |
| productSubCode | varchar(255) | NO | - | - | - | 业务含义待确认 |
| productSubModel | varchar(255) | YES | - | - | - | 业务含义待确认 |
| productSubName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| num | int(11) | NO | - | - | - | 业务含义待确认 |
| borrowNum | int(11) | YES | - | - | - | 业务含义待确认 |
| contract | varchar(255) | YES | - | - | - | 业务含义待确认 |
| memo | text | YES | - | - | - | 业务含义待确认 |
| dataSource | varchar(25) | YES | SMS | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| projectCode | BTREE | NON-UNIQUE | projectCode |

---

### 126 pm_presales_lend_2_sale_from_sms_history -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~13535 行 |
| 数据大小 | 3.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| productfirstName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| productName | varchar(128) | YES | - | - | - | 业务含义待确认 |
| projectCode | varchar(255) | NO | - | MUL | - | 业务含义待确认 |
| productSubCode | varchar(255) | NO | - | - | - | 业务含义待确认 |
| productSubModel | varchar(255) | YES | - | - | - | 业务含义待确认 |
| productSubName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| num | int(11) | NO | - | - | - | 业务含义待确认 |
| borrowNum | int(11) | YES | - | - | - | 业务含义待确认 |
| contract | varchar(255) | YES | - | - | - | 业务含义待确认 |
| memo | text | YES | - | - | - | 业务含义待确认 |
| dataSource | varchar(25) | YES | SMS | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| projectCode | BTREE | NON-UNIQUE | projectCode |

---

### 127 pm_presales_lend_detail_from_oa -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~3024 行 |
| 数据大小 | 336.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | bigint(20) | NO | - | - | - | 主键ID |
| infoId | varchar(64) | YES | - | - | - | 业务含义待确认 |
| contractNum | varchar(100) | YES | - | - | - | 业务含义待确认 |
| deviceSerialnum | varchar(100) | YES | - | - | - | 业务含义待确认 |
| modelNum | varchar(100) | YES | - | - | - | 业务含义待确认 |
| applyCount | int(11) | YES | - | - | - | 业务含义待确认 |
| isSoftware | varchar(255) | YES | - | - | - | 业务含义待确认 |
| customInfo | json | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|

---

### 128 pm_presales_lend_info_from_crm -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~0 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(1) | NO | - | PRI, auto_increment | - | 主键ID |
| lendInfoId | varchar(64) | NO | 0 | - | - | 业务含义待确认 |
| projectCode | varchar(64) | YES | - | - | - | 业务含义待确认 |
| projectName | varchar(765) | YES | - | - | - | 业务含义待确认 |
| dutyName | varchar(189) | YES | - | - | - | 业务含义待确认 |
| dutyContactWay | varchar(300) | YES | - | - | - | 业务含义待确认 |
| decPath | varchar(765) | YES | - | - | - | 业务含义待确认 |
| officeCode | varchar(765) | YES | - | - | - | 业务含义待确认 |
| marketName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| systemName | varchar(128) | YES | - | - | - | 业务含义待确认 |
| expendName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| industryName | varchar(128) | YES | - | - | - | 业务含义待确认 |
| pspm | varchar(257) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 129 pm_presales_lend_info_from_oa -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~1963 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| projectCode | varchar(100) | YES | - | - | - | 业务含义待确认 |
| processStartTime | datetime | YES | - | - | - | 业务含义待确认 |
| lendInfoId | varchar(64) | NO | - | MUL | - | 业务含义待确认 |
| processOrderNum | varchar(100) | YES | - | - | - | 业务含义待确认 |
| applyUserCode | varchar(25) | YES | - | - | - | 业务含义待确认 |
| applyUserName | varchar(25) | YES | - | - | - | 业务含义待确认 |
| applyDeptCode | varchar(25) | YES | - | - | - | 业务含义待确认 |
| applyDeptName | varchar(25) | YES | - | - | - | 业务含义待确认 |
| applyDate | datetime | YES | - | - | - | 业务含义待确认 |
| projectName | varchar(100) | YES | - | - | - | 业务含义待确认 |
| applyType | bigint(20) | YES | - | - | - | 业务含义待确认 |
| applyTypeName | varchar(25) | YES | - | - | - | 业务含义待确认 |
| salesUserCode | varchar(25) | YES | - | - | - | 业务含义待确认 |
| salesUserName | varchar(25) | YES | - | - | - | 业务含义待确认 |
| salesUserMobile | varchar(100) | YES | - | - | - | 业务含义待确认 |
| productLine | bigint(20) | YES | - | - | - | 业务含义待确认 |
| productLineName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| marketName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| systemName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| expendName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| industryName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| applyCause | varchar(255) | YES | - | - | - | 业务含义待确认 |
| followUpPlan | varchar(255) | YES | - | - | - | 业务含义待确认 |
| testStartTime | datetime | YES | - | - | - | 业务含义待确认 |
| testEndTime | datetime | YES | - | - | - | 业务含义待确认 |
| authPlanDate | datetime | YES | - | - | - | 业务含义待确认 |
| authDate | datetime | YES | - | - | - | 业务含义待确认 |
| resellSuccessfully | varchar(255) | YES | - | - | - | 业务含义待确认 |
| useDays | int(11) | YES | - | - | - | 业务含义待确认 |
| resaleCertificateFile | varchar(2048) | YES | - | - | - | 业务含义待确认 |
| provideAuthFile | varchar(2048) | YES | - | - | - | 业务含义待确认 |
| infoFile | varchar(2048) | YES | - | - | - | 业务含义待确认 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| lendInfoId | BTREE | NON-UNIQUE | lendInfoId |
| PRIMARY | BTREE | UNIQUE | id |

---

### 130 pm_presales_lend_info_from_sms -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~145 行 |
| 数据大小 | 64.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(1) | NO | - | PRI, auto_increment | - | 主键ID |
| lendInfoId | varchar(64) | NO | 0 | - | - | 业务含义待确认 |
| projectCode | varchar(64) | YES | - | - | - | 业务含义待确认 |
| projectName | varchar(765) | YES | - | - | - | 业务含义待确认 |
| dutyName | varchar(189) | YES | - | - | - | 业务含义待确认 |
| dutyContactWay | varchar(300) | YES | - | - | - | 业务含义待确认 |
| decPath | varchar(765) | YES | - | - | - | 业务含义待确认 |
| officeCode | varchar(765) | YES | - | - | - | 业务含义待确认 |
| marketName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| systemName | varchar(128) | YES | - | - | - | 业务含义待确认 |
| expendName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| industryName | varchar(128) | YES | - | - | - | 业务含义待确认 |
| pspm | varchar(257) | YES | - | - | - | 业务含义待确认 |
| dataSource | varchar(25) | YES | SMS | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 131 pm_presales_lend_info_from_sms_history -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~0 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(1) | NO | - | PRI, auto_increment | - | 主键ID |
| lendInfoId | varchar(64) | NO | 0 | - | - | 业务含义待确认 |
| projectCode | varchar(64) | YES | - | - | - | 业务含义待确认 |
| projectName | varchar(765) | YES | - | - | - | 业务含义待确认 |
| dutyName | varchar(189) | YES | - | - | - | 业务含义待确认 |
| dutyContactWay | varchar(300) | YES | - | - | - | 业务含义待确认 |
| decPath | varchar(765) | YES | - | - | - | 业务含义待确认 |
| officeCode | varchar(765) | YES | - | - | - | 业务含义待确认 |
| marketName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| systemName | varchar(128) | YES | - | - | - | 业务含义待确认 |
| expendName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| industryName | varchar(128) | YES | - | - | - | 业务含义待确认 |
| pspm | varchar(257) | YES | - | - | - | 业务含义待确认 |
| dataSource | varchar(25) | YES | SMS | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 132 pm_presales_lend_order_from_sms -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~0 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | 0 | - | - | 主键ID |
| orderNumber | varchar(11) | YES | - | - | - | 业务含义待确认 |
| ppliCode | varchar(25) | YES | - | - | 借货执行单号 | 借货执行单号 |
| orderType | varchar(10) | YES | - | - | - | 业务含义待确认 |
| contract | varchar(25) | YES | - | - | - | 业务含义待确认 |
| customer | varchar(255) | YES | - | - | - | 业务含义待确认 |
| projectName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| businessunit | varchar(50) | YES | - | - | - | 业务含义待确认 |
| office | varchar(10) | YES | - | - | - | 业务含义待确认 |
| dutyperson | varchar(10) | YES | - | - | - | 业务含义待确认 |
| itemcode | varchar(10) | YES | - | - | - | 业务含义待确认 |
| description | varchar(255) | YES | - | - | - | 描述 |
| orderQty | int(11) | YES | - | - | - | 业务含义待确认 |
| dlvQty | int(11) | YES | - | - | - | 业务含义待确认 |
| rmaQty | int(11) | YES | - | - | - | 业务含义待确认 |
| lineStatus | varchar(5) | YES | - | - | - | 业务含义待确认 |
| createDate | date | YES | - | - | - | 业务含义待确认 |
| lineId | int(11) | YES | - | - | - | 业务含义待确认 |
| systemId | int(11) | YES | - | - | - | 业务含义待确认 |
| canceled | char(1) | YES | - | - | - | 业务含义待确认 |
| discountVersion | varchar(255) | YES | - | - | - | 业务含义待确认 |
| borrowNum | bigint(12) | YES | - | - | - | 业务含义待确认 |
| dataSource | varchar(25) | YES | SMS | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|

---

### 133 pm_presales_lend_order_from_sms_history -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~20196 行 |
| 数据大小 | 7.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | 0 | - | - | 主键ID |
| orderNumber | varchar(11) | YES | - | - | - | 业务含义待确认 |
| ppliCode | varchar(25) | YES | - | - | 借货执行单号 | 借货执行单号 |
| orderType | varchar(10) | YES | - | - | - | 业务含义待确认 |
| contract | varchar(25) | YES | - | - | - | 业务含义待确认 |
| customer | varchar(255) | YES | - | - | - | 业务含义待确认 |
| projectName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| businessunit | varchar(50) | YES | - | - | - | 业务含义待确认 |
| office | varchar(10) | YES | - | - | - | 业务含义待确认 |
| dutyperson | varchar(10) | YES | - | - | - | 业务含义待确认 |
| itemcode | varchar(10) | YES | - | - | - | 业务含义待确认 |
| description | varchar(255) | YES | - | - | - | 描述 |
| orderQty | int(11) | YES | - | - | - | 业务含义待确认 |
| dlvQty | int(11) | YES | - | - | - | 业务含义待确认 |
| rmaQty | int(11) | YES | - | - | - | 业务含义待确认 |
| lineStatus | varchar(5) | YES | - | - | - | 业务含义待确认 |
| createDate | date | YES | - | - | - | 业务含义待确认 |
| lineId | int(11) | YES | - | - | - | 业务含义待确认 |
| systemId | int(11) | YES | - | - | - | 业务含义待确认 |
| canceled | char(1) | YES | - | - | - | 业务含义待确认 |
| discountVersion | varchar(255) | YES | - | - | - | 业务含义待确认 |
| borrowNum | bigint(12) | YES | - | - | - | 业务含义待确认 |
| dataSource | varchar(25) | YES | SMS | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|

---

### 134 pm_presales_lend_product_from_sms -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~478 行 |
| 数据大小 | 112.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| lendInfoId | varchar(64) | NO | - | - | - | 业务含义待确认 |
| productfirstName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| productName | varchar(128) | YES | - | - | - | 业务含义待确认 |
| productsubCode | varchar(765) | YES | - | - | - | 业务含义待确认 |
| productSubModel | varchar(765) | YES | - | - | - | 业务含义待确认 |
| productSubName | varchar(765) | YES | - | - | - | 业务含义待确认 |
| lendNum | int(11) | YES | - | - | - | 业务含义待确认 |
| memo | text | YES | - | - | - | 业务含义待确认 |
| dataSource | varchar(25) | YES | SMS | - | - | 业务含义待确认 |
| productfirstCode | varchar(64) | YES | - | - | - | 业务含义待确认 |
| productCode | varchar(64) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 135 pm_presales_lend_product_from_sms_history -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~4 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| lendInfoId | varchar(64) | NO | - | - | - | 业务含义待确认 |
| productfirstName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| productName | varchar(128) | YES | - | - | - | 业务含义待确认 |
| productsubCode | varchar(765) | YES | - | - | - | 业务含义待确认 |
| productSubModel | varchar(765) | YES | - | - | - | 业务含义待确认 |
| productSubName | varchar(765) | YES | - | - | - | 业务含义待确认 |
| lendNum | int(11) | YES | - | - | - | 业务含义待确认 |
| memo | text | YES | - | - | - | 业务含义待确认 |
| dataSource | varchar(25) | YES | SMS | - | - | 业务含义待确认 |
| productfirstCode | varchar(64) | YES | - | - | - | 业务含义待确认 |
| productCode | varchar(64) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 136 pm_presales_project_callback -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~1865 行 |
| 数据大小 | 160.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 售前回访问卷表 | 售前回访问卷表 |
| presalesId | int(11) | YES | - | MUL | 售前项目ID | 售前项目ID |
| taskId | varchar(25) | YES | - | MUL | 任务ID | 任务ID |
| quesnaireId | int(11) | YES | - | MUL | 问卷ID | 问卷ID |
| quesnaireVersion | int(11) | YES | - | - | 问卷版本 | 问卷版本 |
| quesnaireState | int(11) | YES | - | - | 状态 -1 草稿 1已提交 | 状态 -1 草稿 1已提交 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| presalesId | BTREE | NON-UNIQUE | presalesId |
| PRIMARY | BTREE | UNIQUE | id |
| quesnaireId | BTREE | NON-UNIQUE | quesnaireId |
| taskId | BTREE | NON-UNIQUE | taskId |

---

### 137 pm_presales_project_duration -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~4320 行 |
| 数据大小 | 416.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| presalesId | int(11) | NO | - | PRI | - | 业务含义待确认 |
| instId | int(11) | YES | - | - | 流程实例ID | 流程实例ID |
| totalDuration | varchar(20) | YES | - | - | 开始时间 | 开始时间 |
| serviceDuration | varchar(20) | YES | - | - | 指派服务经理时间 | 指派服务经理时间 |
| programDuration | varchar(20) | YES | - | - | 指派项目经理时间 | 指派项目经理时间 |
| testDuration | varchar(20) | YES | - | - | 测试开始时间 | 测试开始时间 |
| callbackDuration | varchar(20) | YES | - | - | 回访开始时间 | 回访开始时间 |
| serviceApproveDuration | varchar(100) | YES | - | - | 服务经理审批时间 | 服务经理审批时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | presalesId |

---

### 138 pm_presales_project_header -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~16660 行 |
| 数据大小 | 6.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| presalesId | int(11) | NO | - | PRI, auto_increment | 售前项目主表 | 售前项目主表 |
| instId | varchar(64) | YES | - | MUL | activity工作流流程ID | activity工作流流程ID |
| applyState | int(11) | YES | - | - | -1草稿 1 审批中 2结束 | -1草稿 1 审批中 2结束 |
| applyBy | varchar(25) | YES | - | - | 申请人 | 申请人 |
| applyTime | datetime | YES | - | - | 申请时间 | 申请时间 |
| endTime | datetime | YES | - | - | 申请结束时间 | 申请结束时间 |
| projectState | varchar(25) | YES | 10 | - | 项目状态 ，同售后项目状态  10 未创建 20 直接闭环 30 已创建 31待指派项目经理 32 项目经理跟踪 33工程管理部回访 100闭环 | 项目状态 ，同售后项目状态  10 未创建 20 直接闭环 30 已创建 31待指派项目经理 32 项目经理跟踪 33工程管理部回访 100闭环 |
| presalesCode | varchar(64) | YES | - | - | 售前项目编码 | 售前项目编码 |
| projectCode | varchar(64) | YES | - | MUL | 项目编码 | 项目编码 |
| projectName | varchar(255) | YES | - | - | 项目名称 | 项目名称 |
| projectType | varchar(25) | YES | - | - | - | 业务含义待确认 |
| marketName | varchar(25) | YES | - | - | 市场部名称 | 市场部名称 |
| systemName | varchar(25) | YES | - | - | 系统部名称 | 系统部名称 |
| expendName | varchar(25) | YES | - | - | 拓展部名称 | 拓展部名称 |
| industryName | varchar(25) | YES | - | - | 子行业名称 | 子行业名称 |
| officeCode | varchar(25) | YES | - | - | 办事处编码 | 办事处编码 |
| salesman | varchar(25) | YES | - | - | 销售人员 | 销售人员 |
| productManager | varchar(25) | YES | - | - | 产品经理 | 产品经理 |
| salesmanLink | varchar(125) | YES | - | - | 销售人员联系方式 | 销售人员联系方式 |
| lendInfoId | varchar(64) | YES | - | MUL | SMS系统测试类借货申请主键，标识存在则不再刷新过来 | SMS系统测试类借货申请主键，标识存在则不再刷新过来 |
| lendfiles | varchar(2048) | YES | - | - | 借货交付件 从SMS中同步过来 | 借货交付件 从SMS中同步过来 |
| confirmFileIds | varchar(2048) | YES | - | - | 现场测试服务确认单 | 现场测试服务确认单 |
| hasRma | int(1) | YES | 0 | - | 是否有未核销数据 | 是否有未核销数据 |
| hasTransfer | int(1) | YES | 0 | - | 是否发生借转销 | 是否发生借转销 |
| closeRemark | varchar(512) | YES | - | - | 闭环备注 | 闭环备注 |
| createBy | varchar(25) | YES | - | - | 数据创建人 | 数据创建人 |
| createTime | datetime | YES | CURRENT_TIMESTAMP | - | 数据创建时间 | 数据创建时间 |
| updateBy | varchar(25) | YES | - | - | 数据更新人 | 数据更新人 |
| updateTime | datetime | YES | - | - | 数据更新时间 | 数据更新时间 |
| effectiveFrom | datetime | YES | - | - | 数据有效开始时间 | 数据有效开始时间 |
| effectiveTo | datetime | YES | - | - | 数据有效结束时间 | 数据有效结束时间 |
| source | varchar(25) | NO | SMS | - | 数据来源 | 数据来源 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| instId | BTREE | NON-UNIQUE | instId |
| lendInfoId | BTREE | NON-UNIQUE | lendInfoId |
| PRIMARY | BTREE | UNIQUE | presalesId |
| projectCode | BTREE | NON-UNIQUE | projectCode |

---

### 139 pm_presales_project_product_line -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~4358185 行 |
| 数据大小 | 878.98 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| productLineId | int(11) | NO | - | PRI, auto_increment | - | 业务含义待确认 |
| presalesId | int(11) | YES | - | MUL | 售前项目ID | 售前项目ID |
| lendInfoId | varchar(64) | YES | - | MUL | 借货主表主键 | 借货主表主键 |
| productFirstName | varchar(255) | YES | - | - | 产品一级 | 产品一级 |
| productTypeName | varchar(255) | YES | - | - | 产品类别 | 产品类别 |
| itemCode | varchar(255) | YES | - | - | item编码 | item编码 |
| itemModel | varchar(255) | YES | - | - | item型号 | item型号 |
| itemDesc | text | YES | - | - | item描述 | item描述 |
| price | double | YES | - | - | 目录价 | 目录价 |
| productNum | int(11) | NO | 0 | - | 产品数量 | 产品数量 |
| orderNum | int(11) | NO | 0 | - | 下单数量 | 下单数量 |
| deliverNum | int(11) | NO | 0 | - | 发货数量 | 发货数量 |
| hexiaoNum | int(11) | NO | 0 | - | 核销数量 | 核销数量 |
| transferNum | int(11) | NO | 0 | - | 转销数量 | 转销数量 |
| remark | text | YES | - | - | 备注 | 备注 |
| effectiveFrom | datetime | YES | - | - | 数据有效开始时间 | 数据有效开始时间 |
| effectiveTo | datetime | YES | - | - | 数据有效结束时间 | 数据有效结束时间 |
| source | varchar(25) | YES | SMS | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| lendInfoId | BTREE | NON-UNIQUE | lendInfoId |
| presalesId | BTREE | NON-UNIQUE | presalesId |
| PRIMARY | BTREE | UNIQUE | productLineId |

---

### 140 pm_presales_project_rma_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~62906 行 |
| 数据大小 | 14.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| orderNumber | varchar(11) | YES | - | - | - | 业务含义待确认 |
| ppliCode | varchar(25) | YES | - | - | - | 业务含义待确认 |
| orderType | varchar(10) | YES | - | - | - | 业务含义待确认 |
| contract | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| itemcode | varchar(10) | YES | - | MUL | - | 业务含义待确认 |
| itemModel | varchar(255) | YES | - | - | - | 业务含义待确认 |
| description | varchar(255) | YES | - | - | - | 描述 |
| productfirstName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| productName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| orderQty | decimal(32,0) | YES | - | - | - | 业务含义待确认 |
| dlvQty | decimal(32,0) | YES | - | - | - | 业务含义待确认 |
| rmaQty | decimal(32,0) | YES | - | - | - | 业务含义待确认 |
| createDate | date | YES | - | - | - | 业务含义待确认 |
| canceled | char(1) | YES | - | - | - | 业务含义待确认 |
| deliveryDate | date | YES | - | - | - | 业务含义待确认 |
| rmaDate | date | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contract | BTREE | NON-UNIQUE | contract |
| itemcode | BTREE | NON-UNIQUE | itemcode |

---

### 141 pm_product_info_from_crm -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~8469 行 |
| 数据大小 | 2.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| itemCode | varchar(100) | YES | - | - | item编码 | item编码 |
| productCode | varchar(100) | YES | - | - | 产品大类 | 产品大类 |
| productSubCode | varchar(100) | YES | - | - | 产品小类 | 产品小类 |
| itemModel | varchar(100) | YES | - | - | 产品型号 | 产品型号 |
| itemDesc | varchar(500) | YES | - | - | 产品描述 | 产品描述 |
| remark | text | YES | - | - | 备注 | 备注 |
| status | int(11) | YES | - | - | - | 状态 |
| BU | varchar(100) | YES | - | - | - | 业务含义待确认 |
| productLine | varchar(100) | YES | - | - | - | 业务含义待确认 |
| orgId | int(11) | NO | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| statecode | int(11) | NO | - | - | - | 业务含义待确认 |
| statuscode | int(11) | YES | - | - | - | 业务含义待确认 |
| productStage | int(11) | YES | - | - | - | 业务含义待确认 |
| endOfSaleDate | datetime | YES | - | - | 停止销售时间 | 停止销售时间 |
| endOfSupportDate | datetime | YES | - | - | 停止支持时间 | 停止支持时间 |
| endOfLifeDate | datetime | YES | - | - | 停止生产时间 | 停止生产时间 |
| lastRenewalDate | datetime | YES | - | - | 停止续保时间 | 停止续保时间 |
| dataSource | varchar(100) | YES | - | - | 数据来源 | 数据来源 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|

---

### 142 pm_project -- 项目头信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目头信息 |
| 数据量 | ~70370 行 |
| 数据大小 | 45.58 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectId | int(11) | NO | - | PRI, auto_increment | 项目头信息主键,跟项目其他具体信息关联 | 项目头信息主键,跟项目其他具体信息关联 |
| projectType | varchar(45) | NO | 10 | MUL | 项目类型，用服售后:10，安服售后:afss，安服先行:afxx | 项目类型，用服售后:10，安服售后:afss，安服先行:afxx |
| projectCode | varchar(45) | NO | - | MUL | 项目名称 | 项目名称 |
| projectName | varchar(200) | YES | - | - | 项目名称 | 项目名称 |
| projectState | varchar(11) | YES | - | - | 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态 | 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态 |
| isback | varchar(11) | YES | 30 | - | 30表示创建项目，32表示指定项目经理，34表示填写渠道信息 ,40表示工程管理部不予跟踪处理 ，42 表示项目经理选择不予跟踪 | 30表示创建项目，32表示指定项目经理，34表示填写渠道信息 ,40表示工程管理部不予跟踪处理 ，42 表示项目经理选择不予跟踪 |
| column001 | varchar(255) | YES | - | MUL | 办事处编码 | 办事处编码 |
| column002 | varchar(255) | YES | - | - | 客户编码--ERP | 客户编码--ERP |
| column003 | varchar(255) | YES | - | - | 客户名称--ERP | 客户名称--ERP |
| column004 | varchar(255) | YES | - | - | 市场部编码 | 市场部编码 |
| column005 | varchar(255) | YES | - | - | 系统部ID | 系统部ID |
| column006 | varchar(255) | YES | - | - | 拓展部ID | 拓展部ID |
| column007 | varchar(255) | YES | - | - | 子行业ID | 子行业ID |
| column008 | varchar(255) | YES | - | - | 不予跟踪原因 notGrantTailCause | 不予跟踪原因 notGrantTailCause |
| column009 | datetime | YES | - | - | 订单创建时间 | 订单创建时间 |
| column010 | varchar(10) | YES | - | - | 项目类型 | 项目类型 |
| column011 | varchar(10) | YES | - | - | 项目分类 | 项目分类 |
| column012 | varchar(2) | YES | - | - | 项目实施方式 | 项目实施方式 |
| columno12_readonly | int(2) | YES | -1 | - | 项目实施方式是否可以修改 -1表示可以改 其他值表示readonly | 项目实施方式是否可以修改 -1表示可以改 其他值表示readonly |
| column013 | varchar(255) | YES | - | - | 最终客户名称 | 最终客户名称 |
| column014 | text | YES | - | - | 回退说明 | 回退说明 |
| customerProjectName | varchar(255) | YES | - | - | 客户项目名称 | 客户项目名称 |
| salesType | varchar(25) | YES | 01 | - | 销售类型 | 销售类型 |
| majorProjectLevel | varchar(255) | YES | - | - | 重大项目级别 | 重大项目级别 |
| compId | int(2) | YES | 0 | - | 公司ID | 公司ID |
| createTime | datetime | YES | - | - | 记录数据创建时间 | 记录数据创建时间 |
| createBy | varchar(45) | YES | - | - | 记录数据创建用户 | 记录数据创建用户 |
| updateTime | datetime | YES | - | - | 记录数据最新更新时间 | 记录数据最新更新时间 |
| updateBy | varchar(45) | YES | - | - | 记录数据最新更新用户 | 记录数据最新更新用户 |
| effectiveFrom | datetime | YES | - | - | 数据有效性开始时间 | 数据有效性开始时间 |
| effectiveTo | datetime | YES | - | - | 数据有效性结束时间 | 数据有效性结束时间 |
| disabled | bit(1) | YES | b'0' | - | 数据是否失效 | 数据是否失效 |
| projectStartTime | datetime | YES | - | - | 项目开始实施时间 | 项目开始实施时间 |
| projectRefreshTime | datetime | YES | - | - | 项目相关数据最后编辑时间 | 项目相关数据最后编辑时间 |
| projectCloseTime | datetime | YES | - | - | 项目闭环时间点 | 项目闭环时间点 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |
| customConfig | json | YES | - | - | 自定义配置 | 自定义配置 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| department | BTREE | NON-UNIQUE | column001 |
| PRIMARY | BTREE | UNIQUE | projectId |
| projectCode_index | BTREE | NON-UNIQUE | projectCode,projectType |
| projectType_projectId_IDX | BTREE | NON-UNIQUE | projectType,projectId |

---

### 143 pm_project_contract -- 项目对应的合同（可能多个）

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目对应的合同（可能多个） |
| 数据量 | ~79021 行 |
| 数据大小 | 5.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| contractNo | varchar(45) | NO | - | MUL | 合同号 | 合同号 |
| projectGroupCode | varchar(45) | NO | - | MUL | 项目组编码 | 项目组编码 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(45) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(45) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contract_projectGroupCode_IDX | BTREE | NON-UNIQUE | contractNo,projectGroupCode |
| PRIMARY | BTREE | UNIQUE | id |
| projectGroupCode_contract_IDX | BTREE | NON-UNIQUE | projectGroupCode,contractNo |

---

### 144 pm_project_group -- 项目组对应项目

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目组对应项目 |
| 数据量 | ~77958 行 |
| 数据大小 | 4.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| projectGroupCode | varchar(45) | NO | - | UNI | 项目组组编码 | 项目组组编码 |
| projectGroupName | varchar(45) | YES | - | - | 项目组名称 | 项目组名称 |
| projectType | varchar(25) | YES | 10 | - | 项目类型  默认10 为工程管理售后项目 | 项目类型  默认10 为工程管理售后项目 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(15) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(15) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectGroupCode_UNIQUE | BTREE | UNIQUE | projectGroupCode |

---

### 145 pm_project_group_relationship -- 项目对应对个合同号

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目对应对个合同号 |
| 数据量 | ~77456 行 |
| 数据大小 | 6.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| projectGroupCode | varchar(45) | NO | - | MUL | 项目组编码 | 项目组编码 |
| projectCode | varchar(45) | YES | - | MUL | 项目编码 | 项目编码 |
| mergeBranchMark | varchar(45) | YES | - | - | 项目拆分合并 | 项目拆分合并 |
| smsProjectCode | varchar(45) | YES | - | MUL | 原SMS项目编码 | 原SMS项目编码 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(45) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(45) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectCode | BTREE | NON-UNIQUE | projectCode |
| projectGroupCode | BTREE | NON-UNIQUE | projectGroupCode |
| smsProjectCode | BTREE | NON-UNIQUE | smsProjectCode |

---

### 146 pm_project_header_view_cache -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~71993 行 |
| 数据大小 | 31.56 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectCode | varchar(45) | YES | - | MUL | 原SMS项目编码 | 原SMS项目编码 |
| subProjectCode | varchar(45) | NO | - | - | 项目名称 | 项目名称 |
| projectName | varchar(200) | YES | - | - | 项目名称 | 项目名称 |
| contractNo | varchar(45) | YES | - | MUL | 合同号 | 合同号 |
| majorProjectLevel | varchar(255) | YES | - | - | 重大项目级别 | 重大项目级别 |
| officeName | varchar(20) | YES | - | - | - | 业务含义待确认 |
| customerName | varchar(255) | YES | - | - | 客户名称--ERP | 客户名称--ERP |
| marketName | varchar(255) | YES | - | - | 市场部编码 | 市场部编码 |
| systemName | varchar(255) | YES | - | - | 系统部ID | 系统部ID |
| expendName | varchar(255) | YES | - | - | 拓展部ID | 拓展部ID |
| industryName | varchar(255) | YES | - | - | 子行业ID | 子行业ID |
| salesManCode | varchar(45) | YES | - | - | - | 业务含义待确认 |
| salesManName | varchar(45) | YES | - | - | - | 业务含义待确认 |
| salesManTel | varchar(45) | YES | - | - | - | 业务含义待确认 |
| salesManMail | varchar(100) | YES | - | - | - | 业务含义待确认 |
| smCode | varchar(45) | YES | - | - | 人员编码,外部人员为空 | 人员编码,外部人员为空 |
| smName | varchar(45) | YES | - | - | 人员名称 | 人员名称 |
| pmCode1 | varchar(45) | YES | - | - | 人员编码,外部人员为空 | 人员编码,外部人员为空 |
| pmName1 | varchar(45) | YES | - | - | 人员名称 | 人员名称 |
| pmCode2 | varchar(45) | YES | - | - | 人员编码,外部人员为空 | 人员编码,外部人员为空 |
| pmName2 | varchar(45) | YES | - | - | 人员名称 | 人员名称 |
| compId | int(2) | YES | - | - | 公司ID | 公司ID |
| compName | varchar(128) | YES | - | - | 组织机构全名 | 组织机构全名 |
| ssfsName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| partnerChannel | varchar(45) | YES | - | - | - | 业务含义待确认 |
| projectType | varchar(4) | NO |  | MUL | - | 业务含义待确认 |
| finalCustomerName | varchar(255) | YES | - | - | 最终客户名称 | 最终客户名称 |
| customerProjectName | varchar(255) | YES | - | - | 客户项目名称 | 客户项目名称 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | contractNo |
| projectCode | BTREE | NON-UNIQUE | projectCode |
| projectType | BTREE | NON-UNIQUE | projectType |

---

### 147 pm_project_incident_table_from_itr -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~142 行 |
| 数据大小 | 80.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| incidentId | varchar(255) | YES | - | - | 工单ID | 工单ID |
| ticketNo | varchar(255) | YES | - | - | 问题单号 | 问题单号 |
| STATUS | varchar(255) | YES | - | - | 工单状态 | 工单状态 |
| statusName | varchar(255) | YES | - | - | 工单状态名称 | 工单状态名称 |
| caseTopic | varchar(255) | YES | - | - | 问题单主题 | 问题单主题 |
| memo | text | YES | - | - | 描述 | 描述 |
| principal | varchar(255) | YES | - | - | 责任人 | 责任人 |
| principalName | varchar(255) | YES | - | - | 责任人名称 | 责任人名称 |
| accepter | varchar(255) | YES | - | - | 受理人 | 受理人 |
| accepterName | varchar(255) | YES | - | - | 受理人名称 | 受理人名称 |
| processor | varchar(255) | YES | - | - | 处理人 | 处理人 |
| processorName | varchar(255) | YES | - | - | 处理人名称 | 处理人名称 |
| supplied | varchar(255) | YES | - | - | 是否上报 | 是否上报 |
| questionType | varchar(255) | YES | - | - | 问题类型 | 问题类型 |
| questionLevel | varchar(255) | YES | - | - | 问题级别 | 问题级别 |
| title | varchar(255) | YES | - | - | 工单标题 | 工单标题 |
| acceptTime | varchar(255) | YES | - | - | 受理时间 | 受理时间 |
| productType | varchar(255) | YES | - | - | 设备类型 | 设备类型 |
| productModel | varchar(255) | YES | - | - | 设备型号 | 设备型号 |
| progress | varchar(255) | YES | - | - | 处理进展 | 处理进展 |
| questionReason | varchar(2048) | YES | - | - | 问题根因 | 问题根因 |
| solutionType | varchar(255) | YES | - | - | 解决方式 | 解决方式 |
| solutions | varchar(2048) | YES | - | - | 解决方案 | 解决方案 |
| rmaNo | varchar(255) | YES | - | - | RMA单号 | RMA单号 |
| accidentNo | varchar(255) | YES | - | - | 事故单号 | 事故单号 |
| caseType | varchar(255) | YES | - | - | Case类型 | Case类型 |
| reasonFstType | varchar(255) | YES | - | - | 原因大类 | 原因大类 |
| reasonSndType | varchar(255) | YES | - | - | 原因小类 | 原因小类 |
| projectCode | varchar(255) | YES | - | - | 项目编码 | 项目编码 |
| contractNo | varchar(255) | YES | - | - | 合同号 | 合同号 |
| barcode | varchar(255) | YES | - | - | 序列号 | 序列号 |
| bulletinNo | varchar(255) | YES | - | - | 技术公告编号 | 技术公告编号 |
| bugNo | varchar(255) | YES | - | - | Bug单编号 | Bug单编号 |
| productLine | varchar(255) | YES | - | - | 产品线 | 产品线 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |
| url | varchar(255) | YES | - | - | URL | URL |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 148 pm_project_instruction -- 总部或领导对项目批示

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 总部或领导对项目批示 |
| 数据量 | ~127 行 |
| 数据大小 | 48.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| projectId | int(11) | YES | - | MUL | 项目头关联主键 | 项目头关联主键 |
| instructionsInfo | text | YES | - | - | 批示内容或反馈内容 | 批示内容或反馈内容 |
| instructionsTime | datetime | YES | - | - | 批示时间或反馈时间 | 批示时间或反馈时间 |
| instructionsUser | varchar(45) | YES | - | - | 批示用户或反馈用户 | 批示用户或反馈用户 |
| dataType | int(11) | YES | 0 | - | 数据类型  0 批示信息 1 批示反馈 | 数据类型  0 批示信息 1 批示反馈 |
| instructionsId | int(11) | YES | - | - | 批示ID 针对批示反馈的信息 | 批示ID 针对批示反馈的信息 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 149 pm_project_license_info_from_license -- License授权信息同步表

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | License授权信息同步表 |
| 数据量 | ~71 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| licenseCode | varchar(256) | NO | - | - | 授权码 | 授权码 |
| sn | varchar(255) | YES | - | - | 序列号 | 序列号 |
| specModel | varchar(256) | YES | - | - | 规格型号 | 规格型号 |
| contract | varchar(32) | YES | - | - | 合同号 | 合同号 |
| contractType | varchar(16) | YES | - | - | 合同类型 | 合同类型 |
| item | varchar(255) | YES | - | - | 项目编号 | 项目编号 |
| status | varchar(255) | YES | - | - | 状态 | 状态 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 150 pm_project_log -- 项目主要操作跟踪日志

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目主要操作跟踪日志 |
| 数据量 | ~6411 行 |
| 数据大小 | 496.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| projectId | int(11) | YES | - | MUL | - | 业务含义待确认 |
| handleName | varchar(255) | YES | - | - | 操作名称 | 操作名称 |
| handleDesc | varchar(255) | YES | - | - | 操作描述或原因 | 操作描述或原因 |
| handleUser | varchar(45) | YES | - | - | 操作用户 | 操作用户 |
| taskStartTime | datetime | YES | - | - | 操作开始时间 | 操作开始时间 |
| handleEndTime | datetime | YES | - | - | 操作结束时间 | 操作结束时间 |
| handleState | int(11) | YES | - | - | 有无通知用户 0 无 1 有 | 有无通知用户 0 无 1 有 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 151 pm_project_maintenance -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~184753 行 |
| 数据大小 | 110.64 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| projectId | int(11) | NO | - | MUL | 项目头信息主键 | 项目头信息主键 |
| projectCode | varchar(45) | NO |  | MUL | 项目名称 | 项目名称 |
| projectName | varchar(200) | YES |  | - | 项目名称 | 项目名称 |
| projectType | int(11) | NO | 10 | MUL | 项目类型，售前:20/售后:10 | 项目类型，售前:20/售后:10 |
| projectExecutionState | varchar(45) | YES |  | - | 项目实施状态 | 项目实施状态 |
| contractNo | varchar(255) | YES |  | - | 合同号 | 合同号 |
| officeCode | varchar(25) | YES | - | MUL | 办事处编码 | 办事处编码 |
| compId | int(2) | YES | 1 | - | 所属公司 | 所属公司 |
| type | varchar(45) | YES | - | MUL | 任务性质 | 任务性质 |
| category | varchar(45) | YES | - | MUL | 任务分类 | 任务分类 |
| subCategory | varchar(45) | YES | - | MUL | 任务小类 | 任务小类 |
| processTime | datetime | YES | - | MUL | 处理时间 | 处理时间 |
| processDesc | varchar(1024) | YES | - | - | 事项描述 | 事项描述 |
| processStep | varchar(1024) | YES | - | - | 解决进展 | 解决进展 |
| remainProblem | varchar(1024) | YES | - | - | 遗留问题 | 遗留问题 |
| transitHour | float | YES | 0 | - | 在途耗时(h) | 在途耗时(h) |
| processHour | float | YES | 0 | - | 处理耗时(h) | 处理耗时(h) |
| itemModel | varchar(255) | YES | - | - | 产品型号 | 产品型号 |
| softVersion | varchar(255) | YES | - | - | 在网版本 | 在网版本 |
| enabledFeatures | varchar(255) | YES | - | - | 启用功能 | 启用功能 |
| customTos | varchar(512) | YES | - | - | 自定义主送 | 自定义主送 |
| customCcs | varchar(512) | YES | - | - | 自定义抄送 | 自定义抄送 |
| hasReport | bit(1) | NO | b'0' | - | 是否有巡检报告 | 是否有巡检报告 |
| quesnaireId | int(11) | YES | - | - | 问卷ID | 问卷ID |
| deliverFileIds | varchar(255) | YES |  | - | 交付件，fnd_files id | 交付件，fnd_files id |
| warrantyStatus | varchar(25) | YES | - | - | 维保状态 | 维保状态 |
| industryName | varchar(25) | YES | - | - | 行业 | 行业 |
| userOffice | varchar(25) | YES | - | - | 用户办事处 | 用户办事处 |
| year | int(4) | YES | - | - | 所属年度 | 所属年度 |
| quarter | int(1) | YES | - | - | 所属季度 | 所属季度 |
| month | int(2) | YES | - | - | 所属月份 | 所属月份 |
| wsCount | int(2) | YES | - | - | 当前维保服务次数 | 当前维保服务次数 |
| wafCount | int(2) | YES | - | - | 当前其他服务次数 | 当前其他服务次数 |
| wsYearCount | int(2) | YES | - | - | 维保服务年次数 | 维保服务年次数 |
| wafYearCount | int(2) | YES | - | - | 其他服务年次数 | 其他服务年次数 |
| warrantyInfo | varchar(4096) | YES | - | - | 维保信息 | 维保信息 |
| serviceInfo | varchar(2048) | YES | - | - | 其他服务信息 | 其他服务信息 |
| remark | varchar(2048) | YES | - | - | 备注 | 备注 |
| createTime | datetime | YES | - | MUL | 创建时间 | 创建时间 |
| createBy | varchar(45) | YES | - | MUL | 创建用户 | 创建用户 |
| updateTime | datetime | YES | - | - | 最新更新时间 | 最新更新时间 |
| updateBy | varchar(45) | YES | - | - | 最新更新用户 | 最新更新用户 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| category | BTREE | NON-UNIQUE | category,subCategory |
| createBy | BTREE | NON-UNIQUE | createBy |
| createTime | BTREE | NON-UNIQUE | createTime |
| officeCode | BTREE | NON-UNIQUE | officeCode |
| PRIMARY | BTREE | UNIQUE | id |
| processTime_IDX | BTREE | NON-UNIQUE | processTime |
| projectCode | BTREE | NON-UNIQUE | projectCode |
| projectId | BTREE | NON-UNIQUE | projectId |
| projectType | BTREE | NON-UNIQUE | projectType |
| subCategory | BTREE | NON-UNIQUE | subCategory |
| type | BTREE | NON-UNIQUE | type |

---

### 152 pm_project_maintenance_sectary_from_sse -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~160 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| depNum | varchar(10) | NO | - | UNI | 同步自EHR系统 | 同步自EHR系统 |
| depName | varchar(20) | NO | - | - | - | 业务含义待确认 |
| pDepNum | varchar(10) | YES | - | - | - | 业务含义待确认 |
| pDepName | varchar(20) | YES | - | - | - | 业务含义待确认 |
| sectary | varchar(10) | YES | - | MUL | 秘书工号 | 秘书工号 |
| sectaryName | varchar(255) | YES | - | - | 秘书姓名 | 秘书姓名 |
| sectaryEmail | varchar(255) | YES | - | - | 秘书邮箱 | 秘书邮箱 |
| sectaryPhone | varchar(255) | YES | - | - | 秘书电话 | 秘书电话 |
| status | int(4) | YES | 1 | - | 有效状态 | 有效状态 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| depNum | BTREE | UNIQUE | depNum |
| PRIMARY | BTREE | UNIQUE | id |
| sectary | BTREE | NON-UNIQUE | sectary |

---

### 153 pm_project_maintenance_service_delivery -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~66 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| maintenanceId | int(11) | NO | - | MUL | - | 业务含义待确认 |
| projectId | int(11) | YES | - | MUL | - | 业务含义待确认 |
| projectType | varchar(25) | YES | 10 | - | - | 业务含义待确认 |
| serviceType | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| processTime | datetime | YES | - | - | - | 业务含义待确认 |
| year | int(4) | YES | - | - | - | 业务含义待确认 |
| quarter | int(2) | YES | - | - | - | 业务含义待确认 |
| month | int(2) | YES | - | - | - | 业务含义待确认 |
| deliveried | int(1) | YES | 0 | - | - | 业务含义待确认 |
| startDate | date | YES | - | - | - | 业务含义待确认 |
| endDate | date | YES | - | - | - | 业务含义待确认 |
| count | int(2) | YES | 0 | - | - | 业务含义待确认 |
| yearCount | int(2) | YES | 0 | - | - | 业务含义待确认 |
| remark | varchar(2048) | YES | - | - | - | 备注 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| maintenanceId | BTREE | NON-UNIQUE | maintenanceId |
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId,projectType |
| serviceType | BTREE | NON-UNIQUE | serviceType |

---

### 154 pm_project_maintenance_view -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~183607 行 |
| 数据大小 | 122.69 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | 0 | - | - | 主键ID |
| projectId | int(11) | NO | - | - | 项目头信息主键 | 项目头信息主键 |
| projectCode | varchar(45) | NO |  | - | 项目名称 | 项目名称 |
| projectName | varchar(200) | YES |  | - | 项目名称 | 项目名称 |
| projectType | int(11) | NO | 10 | - | 项目类型，售前:20/售后:10 | 项目类型，售前:20/售后:10 |
| projectExecutionState | varchar(45) | YES |  | - | 项目实施状态 | 项目实施状态 |
| contractNo | varchar(255) | YES |  | - | 合同号 | 合同号 |
| officeCode | varchar(25) | YES | - | - | 办事处编码 | 办事处编码 |
| compId | int(2) | YES | 1 | - | 所属公司 | 所属公司 |
| type | varchar(45) | YES | - | - | 任务性质 | 任务性质 |
| category | varchar(45) | YES | - | - | 任务分类 | 任务分类 |
| subCategory | varchar(45) | YES | - | - | 任务小类 | 任务小类 |
| processTime | datetime | YES | - | - | 处理时间 | 处理时间 |
| processDesc | varchar(1024) | YES | - | - | 事项描述 | 事项描述 |
| processStep | varchar(1024) | YES | - | - | 解决进展 | 解决进展 |
| remainProblem | varchar(1024) | YES | - | - | 遗留问题 | 遗留问题 |
| transitHour | float | YES | 0 | - | 在途耗时(h) | 在途耗时(h) |
| processHour | float | YES | 0 | - | 处理耗时(h) | 处理耗时(h) |
| itemModel | varchar(255) | YES | - | - | 产品型号 | 产品型号 |
| softVersion | varchar(255) | YES | - | - | 在网版本 | 在网版本 |
| enabledFeatures | varchar(255) | YES | - | - | 启用功能 | 启用功能 |
| customTos | varchar(512) | YES | - | - | 自定义主送 | 自定义主送 |
| customCcs | varchar(512) | YES | - | - | 自定义抄送 | 自定义抄送 |
| hasReport | bit(1) | NO | b'0' | - | 是否有巡检报告 | 是否有巡检报告 |
| quesnaireId | int(11) | YES | - | - | 问卷ID | 问卷ID |
| deliverFileIds | varchar(255) | YES |  | - | 交付件，fnd_files id | 交付件，fnd_files id |
| warrantyStatus | varchar(25) | YES | - | - | 维保状态 | 维保状态 |
| industryName | varchar(25) | YES | - | - | 行业 | 行业 |
| userOffice | varchar(25) | YES | - | - | 用户办事处 | 用户办事处 |
| year | int(4) | YES | - | - | 所属年度 | 所属年度 |
| quarter | int(1) | YES | - | - | 所属季度 | 所属季度 |
| month | int(2) | YES | - | - | 所属月份 | 所属月份 |
| wsCount | int(2) | YES | - | - | 当前维保服务次数 | 当前维保服务次数 |
| wafCount | int(2) | YES | - | - | 当前其他服务次数 | 当前其他服务次数 |
| wsYearCount | int(2) | YES | - | - | 维保服务年次数 | 维保服务年次数 |
| wafYearCount | int(2) | YES | - | - | 其他服务年次数 | 其他服务年次数 |
| warrantyInfo | varchar(4096) | YES | - | - | 维保信息 | 维保信息 |
| serviceInfo | varchar(2048) | YES | - | - | 其他服务信息 | 其他服务信息 |
| remark | varchar(2048) | YES | - | - | 备注 | 备注 |
| createTime | datetime | YES | - | - | 创建时间 | 创建时间 |
| createBy | varchar(45) | YES | - | - | 创建用户 | 创建用户 |
| updateTime | datetime | YES | - | - | 最新更新时间 | 最新更新时间 |
| updateBy | varchar(45) | YES | - | - | 最新更新用户 | 最新更新用户 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |
| officeName | varchar(20) | YES | - | - | - | 业务含义待确认 |
| userOfficeName | varchar(20) | YES | - | - | - | 业务含义待确认 |
| serviceManager | varchar(45) | YES | - | - | 人员名称 | 人员名称 |
| programManagerA | varchar(45) | YES | - | - | 人员名称 | 人员名称 |
| programManagerB | varchar(45) | YES | - | - | 人员名称 | 人员名称 |
| createUser | varchar(174) | YES | - | - | - | 业务含义待确认 |
| typeName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| projectExecutionStateName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| categoryName | varchar(258) | YES | - | - | - | 业务含义待确认 |
| subCategoryName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| marketName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| systemName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| expendName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| industryNameN | varchar(255) | YES | - | - | - | 业务含义待确认 |
| finalCustomerName | varchar(255) | YES | - | - | 最终客户名称 | 最终客户名称 |
| salerName | varchar(91) | YES | - | - | - | 业务含义待确认 |
| quesnaireResultHeaderId | int(11) | YES | - | - | 回访结果头信息Id | 回访结果头信息Id |
| 工程师技术能力 | longtext | YES | - | - | - | 业务含义待确认 |
| 服务及时性 | longtext | YES | - | - | - | 业务含义待确认 |
| 服务水平及规范性 | longtext | YES | - | - | - | 业务含义待确认 |
| warrantyStatusName | varchar(4) | YES | - | - | - | 业务含义待确认 |
| syncTime | datetime | NO | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|

---

### 155 pm_project_market_relations_from_sms -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~528 行 |
| 数据大小 | 80.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| marketCode | varchar(64) | YES | - | - | - | 业务含义待确认 |
| marketName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| systemCode | varchar(64) | YES | - | - | - | 业务含义待确认 |
| systemName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| expendCode | varchar(64) | YES | - | - | - | 业务含义待确认 |
| expendName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| industryCode | varchar(64) | YES | - | - | - | 业务含义待确认 |
| industryName | varchar(255) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 156 pm_project_member -- 项目相关人员信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目相关人员信息 |
| 数据量 | ~302428 行 |
| 数据大小 | 32.56 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| projectId | int(11) | YES | - | MUL | - | 业务含义待确认 |
| projectType | varchar(25) | YES | 10 | - | 项目类型 售后10 或售前 20 详见fnd_basic_data | 项目类型 售后10 或售前 20 详见fnd_basic_data |
| memberRole | varchar(45) | YES | - | - | 人员在项目中所处的角色 | 人员在项目中所处的角色 |
| memberCode | varchar(45) | YES | - | MUL | 人员编码,外部人员为空 | 人员编码,外部人员为空 |
| memberName | varchar(45) | YES | - | - | 人员名称 | 人员名称 |
| phoneNum | varchar(20) | YES | - | - | 电话 | 电话 |
| email | varchar(45) | YES | - | - | 邮箱 | 邮箱 |
| fromFlag | varchar(2) | YES | 0 | - | 信息来源，1表示来源于项目信息，2表示来源于成员信息 | 信息来源，1表示来源于项目信息，2表示来源于成员信息 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(45) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(15) | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | 有效结束时间 | 有效结束时间 |
| effectiveFrom | datetime | YES | - | - | 有效开始时间 | 有效开始时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| memberCode_IDX | BTREE | NON-UNIQUE | memberCode,projectId,projectType |
| PRIMARY | BTREE | UNIQUE | id |
| projectId_role | BTREE | NON-UNIQUE | projectId,memberRole |
| projectId_type | BTREE | NON-UNIQUE | projectId,projectType |

---

### 157 pm_project_notification -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~152161 行 |
| 数据大小 | 13.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| notifySubject | varchar(255) | YES | - | - | 通知标题 | 通知标题 |
| notifyContent | text | YES | - | - | 通知内容 | 通知内容 |
| projectId | int(11) | YES | - | MUL | 相关项目ID | 相关项目ID |
| createTime | datetime | YES | - | - | 创建时间 | 创建时间 |
| createBy | varchar(25) | YES | - | - | 创建用户 | 创建用户 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 158 pm_project_notification_state -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~9905 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| notifyId | int(11) | YES | - | MUL | - | 业务含义待确认 |
| notifyObject | varchar(25) | YES | - | - | 通知主题，系统用户 | 通知主题，系统用户 |
| notifyState | int(11) | YES | - | - | 通知状态，有无通知 0 无 1 有 | 通知状态，有无通知 0 无 1 有 |
| checkTime | datetime | YES | - | - | 用户查看通知时间 | 用户查看通知时间 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| notifyId | BTREE | NON-UNIQUE | notifyId |
| PRIMARY | BTREE | UNIQUE | id |

---

### 159 pm_project_product_af_from_sms -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~21608 行 |
| 数据大小 | 6.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | varchar(64) | NO | 0 | - | - | 主键ID |
| projectCode | varchar(255) | NO | - | - | - | 业务含义待确认 |
| orderExecNumber | varchar(255) | YES | - | - | - | 业务含义待确认 |
| corporationCode | varchar(50) | YES | - | - | 公司编码 | 公司编码 |
| ssfrId | varchar(64) | YES | - | - | 安全服务先行核销ID | 安全服务先行核销ID |
| productCode | varchar(255) | YES | - | - | - | 业务含义待确认 |
| productfirstCode | varchar(255) | YES | - | - | - | 业务含义待确认 |
| productName | varchar(128) | YES | - | - | - | 业务含义待确认 |
| productfirstName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| productsubCode | varchar(255) | YES | - | - | - | 业务含义待确认 |
| productSubModel | varchar(255) | YES | - | - | - | 业务含义待确认 |
| productSubName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| num | int(11) | YES | - | - | - | 业务含义待确认 |
| borrowNum | int(11) | YES | - | - | - | 业务含义待确认 |
| price | decimal(19,6) | YES | - | - | - | 业务含义待确认 |
| purchaseDiscount | decimal(19,6) | YES | - | - | - | 业务含义待确认 |
| purchasePrice | decimal(29,2) | YES | - | - | - | 业务含义待确认 |
| dataSource | varchar(25) | YES | CRM | - | - | 业务含义待确认 |
| lineType | varchar(25) | YES | orderLine | - | 行类型，orderLine:订单行，leaseLine:租赁行 | 行类型，orderLine:订单行，leaseLine:租赁行 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|

---

### 160 pm_project_product_af_from_sms_history -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~18054 行 |
| 数据大小 | 5.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | 0 | - | - | 主键ID |
| projectCode | varchar(255) | NO | - | - | - | 业务含义待确认 |
| orderExecNumber | varchar(255) | YES | - | - | - | 业务含义待确认 |
| corporationCode | varchar(50) | YES | - | - | 公司编码 | 公司编码 |
| ssfrId | varchar(64) | YES | - | - | 安全服务先行核销ID | 安全服务先行核销ID |
| productCode | varchar(255) | YES | - | - | - | 业务含义待确认 |
| productfirstCode | varchar(255) | YES | - | - | - | 业务含义待确认 |
| productName | varchar(128) | YES | - | - | - | 业务含义待确认 |
| productfirstName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| productsubCode | varchar(255) | YES | - | - | - | 业务含义待确认 |
| productSubModel | varchar(255) | YES | - | - | - | 业务含义待确认 |
| productSubName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| num | int(11) | YES | - | - | - | 业务含义待确认 |
| borrowNum | int(11) | YES | - | - | - | 业务含义待确认 |
| price | decimal(19,6) | YES | - | - | - | 业务含义待确认 |
| purchaseDiscount | decimal(19,6) | YES | - | - | - | 业务含义待确认 |
| purchasePrice | decimal(29,2) | YES | - | - | - | 业务含义待确认 |
| dataSource | varchar(25) | YES | SMS | - | - | 业务含义待确认 |
| lineType | varchar(25) | YES | orderLine | - | 行类型，orderLine:订单行，leaseLine:租赁行 | 行类型，orderLine:订单行，leaseLine:租赁行 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|

---

### 161 pm_project_product_config_level_info_from_crm -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~45 行 |
| 数据大小 | 48.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectCode | varchar(100) | YES | - | - | - | 业务含义待确认 |
| orderExecNumber | varchar(255) | YES |  | - | - | 业务含义待确认 |
| itemGroup | int(11) | YES | - | - | - | 业务含义待确认 |
| itemCode | varchar(100) | YES | - | - | - | 业务含义待确认 |
| parentCode | varchar(1000) | YES | - | - | - | 业务含义待确认 |
| quantity | int(11) | YES | - | - | - | 业务含义待确认 |
| bomPaths | varchar(1000) | YES | - | - | - | 业务含义待确认 |
| itemModel | varchar(100) | YES | - | - | - | 业务含义待确认 |
| itemDesc | varchar(500) | YES | - | - | - | 业务含义待确认 |
| level | int(11) | YES | 0 | - | - | 业务含义待确认 |
| dataSource | varchar(25) | YES | CRM | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|

---

### 162 pm_project_product_lease_line_from_crm -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~0 行 |
| 数据大小 | 40.08 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectCode | varchar(255) | NO |  | MUL | 项目编码 | 项目编码 |
| orderExecNumber | varchar(255) | YES | - | - | 执行单号 | 执行单号 |
| productFirstName | varchar(255) | NO |  | - | 产品类型 | 产品类型 |
| productName | varchar(128) | NO |  | - | 产品名 | 产品名 |
| productSubCode | varchar(255) | NO |  | - | item编码 | item编码 |
| productSubModel | varchar(255) | YES | - | - | item类型 | item类型 |
| productSubName | varchar(255) | YES | - | - | item描述 | item描述 |
| num | int(11) | NO | 0 | - | 订单数量 | 订单数量 |
| memo | mediumtext | YES | - | - | 备注 | 备注 |
| leaseDuration | decimal(16,2) | YES | - | - | 租赁月数 | 租赁月数 |
| dataSource | varchar(25) | YES | CRM | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| projectCode_IDX | BTREE | NON-UNIQUE | projectCode,orderExecNumber |

---

### 163 pm_project_product_line -- 订单产品信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 订单产品信息 |
| 数据量 | ~185819 行 |
| 数据大小 | 25.06 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | MUL, auto_increment | - | 主键ID |
| projectId | int(11) | YES | - | MUL | 关联主表 | 关联主表 |
| contractNo | varchar(45) | YES | - | MUL | 合同号 | 合同号 |
| itemCode | varchar(15) | YES | - | MUL | 产品编码 | 产品编码 |
| itemName | varchar(255) | YES | - | - | 产品名称 | 产品名称 |
| projectQuantity | int(11) | YES | - | - | 项目产品数量 | 项目产品数量 |
| orderQuantity | int(11) | YES | - | - | 产品订单数量 | 产品订单数量 |
| deliverQuantity | int(11) | YES | - | - | 已发货数量 | 已发货数量 |
| openQuantity | int(11) | YES | - | - | 未发货数量 | 未发货数量 |
| orderNumber | varchar(25) | YES | - | - | - | 业务含义待确认 |
| lineNum | varchar(25) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | contractNo |
| id | BTREE | NON-UNIQUE | id |
| itemCode | BTREE | NON-UNIQUE | itemCode |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 164 pm_project_product_line_real -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~3812 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| projectId | int(11) | NO | - | MUL | ph.projectId,外键 | ph.projectId,外键 |
| contractNo | varchar(25) | NO |  | - | 合同号 | 合同号 |
| projectCode | varchar(255) | NO |  | - | 项目编码 | 项目编码 |
| orderExecNumber | varchar(255) | NO |  | - | 执行单号 | 执行单号 |
| productFirstName | varchar(255) | YES |  | - | 产品分类？ | 产品分类？ |
| productName | varchar(128) | YES |  | - | 产品名 | 产品名 |
| productSubCode | varchar(255) | NO |  | - | item编码 | item编码 |
| productSubModel | varchar(255) | NO |  | - | item类型 | item类型 |
| productSubName | varchar(255) | NO |  | - | item名 | item名 |
| num | int(11) | YES | 0 | - | 订单数量 | 订单数量 |
| memo | mediumtext | YES | - | - | 备注 | 备注 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 165 pm_project_property_af_from_sms -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~190 行 |
| 数据大小 | 160.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | 0 | - | - | 主键ID |
| orderExecNumber | varchar(60) | YES | - | - | - | 业务含义待确认 |
| projectCode | varchar(255) | NO | - | - | - | 业务含义待确认 |
| projectName | varchar(765) | YES | - | - | - | 业务含义待确认 |
| marketCode | varchar(64) | YES | - | - | - | 业务含义待确认 |
| officeCode | varchar(765) | YES | - | - | - | 业务含义待确认 |
| expendId | varchar(64) | YES | - | - | - | 业务含义待确认 |
| marketName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| officeName | varchar(128) | YES | - | - | - | 业务含义待确认 |
| systemName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| salesManCode | varchar(60) | YES | - | - | - | 业务含义待确认 |
| systemId | varchar(64) | YES | - | - | - | 业务含义待确认 |
| industryId | varchar(64) | YES | - | - | - | 业务含义待确认 |
| salesManName | varchar(128) | YES | - | - | - | 业务含义待确认 |
| expendName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| industryName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| serviceTypeName | varchar(128) | YES | - | - | - | 业务含义待确认 |
| channelName | varchar(765) | YES | - | - | - | 业务含义待确认 |
| engineeFee | decimal(19,2) | YES | - | - | 安全服务先行类借货有值，表示出货价 | 安全服务先行类借货有值，表示出货价 |
| objId | varchar(64) | YES | - | - | 参数1 | 参数1 |
| applyType | varchar(60) | YES | - | - | - | 业务含义待确认 |
| corporationCode | varchar(10) | YES | - | - | 公司编码 | 公司编码 |
| customerProjectName | varchar(765) | YES | - | - | - | 业务含义待确认 |
| finalCustomerName | varchar(765) | YES | - | - | - | 业务含义待确认 |
| agentName | varchar(765) | YES | - | - | - | 业务含义待确认 |
| pspm | varchar(765) | YES | - | - | - | 业务含义待确认 |
| pspmName | varchar(257) | YES | - | - | - | 业务含义待确认 |
| salesMenTel | varchar(300) | YES | - | - | - | 业务含义待确认 |
| decPath | varchar(765) | YES | - | - | - | 业务含义待确认 |
| requireInDate | date | YES | - | - | - | 业务含义待确认 |
| receiveMen | varchar(450) | YES | - | - | - | 业务含义待确认 |
| reveiveContactWay | varchar(300) | YES | - | - | - | 业务含义待确认 |
| receiveAddress | varchar(765) | YES | - | - | - | 业务含义待确认 |
| lendCause | text | YES | - | - | - | 业务含义待确认 |
| projectType | varchar(4) | NO |  | - | - | 业务含义待确认 |
| projectMoney | decimal(16,2) | YES | 0.00 | - | 出货价 | 出货价 |
| afProjectMoney | decimal(16,2) | YES | 0.00 | - | 安服出货价 | 安服出货价 |
| submitTime | datetime | YES | - | - | 提交时间 | 提交时间 |
| predBidDate | datetime | YES | - | - | 项目投标时间 | 项目投标时间 |
| linkmanName | varchar(255) | YES | - | - | 客户联系人 | 客户联系人 |
| linkmanTel | varchar(64) | YES | - | - | 客户联系方式 | 客户联系方式 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |
| dataSource | varchar(25) | YES | CRM | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|

---

### 166 pm_project_property_af_from_sms_history -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~129 行 |
| 数据大小 | 112.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | 0 | - | - | 主键ID |
| orderExecNumber | varchar(60) | YES | - | - | - | 业务含义待确认 |
| projectCode | varchar(255) | NO | - | - | - | 业务含义待确认 |
| projectName | varchar(765) | YES | - | - | - | 业务含义待确认 |
| marketCode | varchar(64) | YES | - | - | - | 业务含义待确认 |
| officeCode | varchar(765) | YES | - | - | - | 业务含义待确认 |
| expendId | varchar(64) | YES | - | - | - | 业务含义待确认 |
| marketName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| officeName | varchar(128) | YES | - | - | - | 业务含义待确认 |
| systemName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| salesManCode | varchar(60) | YES | - | - | - | 业务含义待确认 |
| systemId | varchar(64) | YES | - | - | - | 业务含义待确认 |
| industryId | varchar(64) | YES | - | - | - | 业务含义待确认 |
| salesManName | varchar(128) | YES | - | - | - | 业务含义待确认 |
| expendName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| industryName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| serviceTypeName | varchar(128) | YES | - | - | - | 业务含义待确认 |
| channelName | varchar(765) | YES | - | - | - | 业务含义待确认 |
| engineeFee | decimal(19,2) | YES | - | - | 安全服务先行类借货有值，表示出货价 | 安全服务先行类借货有值，表示出货价 |
| objId | varchar(64) | YES | - | - | 参数1 | 参数1 |
| applyType | varchar(60) | YES | - | - | - | 业务含义待确认 |
| corporationCode | varchar(10) | YES | - | - | 公司编码 | 公司编码 |
| customerProjectName | varchar(765) | YES | - | - | - | 业务含义待确认 |
| finalCustomerName | varchar(765) | YES | - | - | - | 业务含义待确认 |
| agentName | varchar(765) | YES | - | - | - | 业务含义待确认 |
| pspm | varchar(765) | YES | - | - | - | 业务含义待确认 |
| pspmName | varchar(257) | YES | - | - | - | 业务含义待确认 |
| salesMenTel | varchar(300) | YES | - | - | - | 业务含义待确认 |
| decPath | varchar(765) | YES | - | - | - | 业务含义待确认 |
| requireInDate | date | YES | - | - | - | 业务含义待确认 |
| receiveMen | varchar(450) | YES | - | - | - | 业务含义待确认 |
| reveiveContactWay | varchar(300) | YES | - | - | - | 业务含义待确认 |
| receiveAddress | varchar(765) | YES | - | - | - | 业务含义待确认 |
| lendCause | text | YES | - | - | - | 业务含义待确认 |
| projectType | varchar(4) | NO |  | - | - | 业务含义待确认 |
| projectMoney | decimal(16,2) | YES | 0.00 | - | 出货价 | 出货价 |
| afProjectMoney | decimal(16,2) | YES | 0.00 | - | 安服出货价 | 安服出货价 |
| submitTime | datetime | YES | - | - | 提交时间 | 提交时间 |
| predBidDate | datetime | YES | - | - | 项目投标时间 | 项目投标时间 |
| linkmanName | varchar(255) | YES | - | - | 客户联系人 | 客户联系人 |
| linkmanTel | varchar(64) | YES | - | - | 客户联系方式 | 客户联系方式 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |
| dataSource | varchar(25) | YES | SMS | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|

---

### 167 pm_project_property_from_sms -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~612 行 |
| 数据大小 | 272.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| orderExecNumber | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| projectCode | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| projectName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| salesManCode | varchar(45) | YES | - | - | - | 业务含义待确认 |
| salesManName | varchar(45) | YES | - | - | - | 业务含义待确认 |
| marketCode | varchar(64) | YES | - | - | - | 业务含义待确认 |
| marketName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| systemId | varchar(64) | YES | - | - | - | 业务含义待确认 |
| systemName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| expendId | varchar(64) | YES | - | - | - | 业务含义待确认 |
| expendName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| industryId | varchar(64) | YES | - | - | - | 业务含义待确认 |
| industryName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| officeCode | varchar(15) | YES | - | - | - | 业务含义待确认 |
| officeName | varchar(15) | YES | - | - | - | 业务含义待确认 |
| serviceTypeName | varchar(10) | YES | - | - | - | 业务含义待确认 |
| channelName | varchar(255) | YES | - | - | 出货代理商名称 | 出货代理商名称 |
| engineeFee | varchar(25) | YES | - | - | 工程服务费 | 工程服务费 |
| objId | varchar(64) | YES | - | - | 参数1 | 参数1 |
| applyType | varchar(25) | YES | - | - | 参数2 | 参数2 |
| corporationCode | varchar(25) | YES | 01 | - | 公司编码 | 公司编码 |
| customerProjectName | varchar(255) | YES | - | - | 客户项目名称 | 客户项目名称 |
| finalCustomerName | varchar(255) | YES | - | - | 最终客户名称 | 最终客户名称 |
| agentName | varchar(500) | YES | - | - | 代理商名称 | 代理商名称 |
| projectMoney | decimal(16,2) | YES | 0.00 | - | 出货价 | 出货价 |
| submitTime | datetime | YES | - | - | 项目创建时间 | 项目创建时间 |
| majorProjectLevel | varchar(255) | YES | - | - | 重大项目级别 | 重大项目级别 |
| predBidDate | datetime | YES | - | - | 项目投标时间 | 项目投标时间 |
| linkmanName | varchar(255) | YES | - | - | 客户联系人 | 客户联系人 |
| linkmanTel | varchar(64) | YES | - | - | 客户联系方式 | 客户联系方式 |
| dataSource | varchar(25) | YES | SMS | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| orderExecNum | BTREE | NON-UNIQUE | orderExecNumber |
| PRIMARY | BTREE | UNIQUE | id |
| projectCode | BTREE | NON-UNIQUE | projectCode |

---

### 168 pm_project_property_from_sms_history -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~47550 行 |
| 数据大小 | 18.55 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| orderExecNumber | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| projectCode | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| projectName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| salesManCode | varchar(10) | YES | - | - | - | 业务含义待确认 |
| salesManName | varchar(10) | YES | - | - | - | 业务含义待确认 |
| marketCode | varchar(64) | YES | - | - | - | 业务含义待确认 |
| marketName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| systemId | varchar(64) | YES | - | - | - | 业务含义待确认 |
| systemName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| expendId | varchar(64) | YES | - | - | - | 业务含义待确认 |
| expendName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| industryId | varchar(64) | YES | - | - | - | 业务含义待确认 |
| industryName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| officeCode | varchar(15) | YES | - | - | - | 业务含义待确认 |
| officeName | varchar(15) | YES | - | - | - | 业务含义待确认 |
| serviceTypeName | varchar(10) | YES | - | - | - | 业务含义待确认 |
| channelName | varchar(255) | YES | - | - | 出货代理商名称 | 出货代理商名称 |
| engineeFee | varchar(25) | YES | - | - | 工程服务费 | 工程服务费 |
| objId | varchar(64) | YES | - | - | 参数1 | 参数1 |
| applyType | varchar(25) | YES | - | - | 参数2 | 参数2 |
| corporationCode | varchar(25) | YES | 01 | - | 公司编码 | 公司编码 |
| customerProjectName | varchar(255) | YES | - | - | 客户项目名称 | 客户项目名称 |
| finalCustomerName | varchar(255) | YES | - | - | 最终客户名称 | 最终客户名称 |
| agentName | varchar(500) | YES | - | - | 代理商名称 | 代理商名称 |
| projectMoney | decimal(16,2) | YES | 0.00 | - | 出货价 | 出货价 |
| submitTime | datetime | YES | - | - | 项目创建时间 | 项目创建时间 |
| majorProjectLevel | varchar(255) | YES | - | - | 重大项目级别 | 重大项目级别 |
| predBidDate | datetime | YES | - | - | 项目投标时间 | 项目投标时间 |
| linkmanName | varchar(255) | YES | - | - | 客户联系人 | 客户联系人 |
| linkmanTel | varchar(64) | YES | - | - | 客户联系方式 | 客户联系方式 |
| dataSource | varchar(25) | YES | SMS | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| orderExecNum | BTREE | NON-UNIQUE | orderExecNumber |
| PRIMARY | BTREE | UNIQUE | id |
| projectCode | BTREE | NON-UNIQUE | projectCode |

---

### 169 pm_project_property_from_sms_history_bak -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~46985 行 |
| 数据大小 | 19.55 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| orderExecNumber | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| projectCode | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| projectName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| salesManCode | varchar(10) | YES | - | - | - | 业务含义待确认 |
| salesManName | varchar(10) | YES | - | - | - | 业务含义待确认 |
| marketCode | varchar(64) | YES | - | - | - | 业务含义待确认 |
| marketName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| systemId | varchar(64) | YES | - | - | - | 业务含义待确认 |
| systemName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| expendId | varchar(64) | YES | - | - | - | 业务含义待确认 |
| expendName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| industryId | varchar(64) | YES | - | - | - | 业务含义待确认 |
| industryName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| officeCode | varchar(15) | YES | - | - | - | 业务含义待确认 |
| officeName | varchar(15) | YES | - | - | - | 业务含义待确认 |
| serviceTypeName | varchar(10) | YES | - | - | - | 业务含义待确认 |
| channelName | varchar(255) | YES | - | - | 出货代理商名称 | 出货代理商名称 |
| engineeFee | varchar(25) | YES | - | - | 工程服务费 | 工程服务费 |
| objId | varchar(64) | YES | - | - | 参数1 | 参数1 |
| applyType | varchar(25) | YES | - | - | 参数2 | 参数2 |
| corporationCode | varchar(25) | YES | 01 | - | 公司编码 | 公司编码 |
| customerProjectName | varchar(255) | YES | - | - | 客户项目名称 | 客户项目名称 |
| finalCustomerName | varchar(255) | YES | - | - | 最终客户名称 | 最终客户名称 |
| agentName | varchar(500) | YES | - | - | 代理商名称 | 代理商名称 |
| projectMoney | decimal(16,2) | YES | 0.00 | - | 出货价 | 出货价 |
| submitTime | datetime | YES | - | - | 项目创建时间 | 项目创建时间 |
| majorProjectLevel | varchar(255) | YES | - | - | 重大项目级别 | 重大项目级别 |
| predBidDate | datetime | YES | - | - | 项目投标时间 | 项目投标时间 |
| linkmanName | varchar(255) | YES | - | - | 客户联系人 | 客户联系人 |
| linkmanTel | varchar(64) | YES | - | - | 客户联系方式 | 客户联系方式 |
| dataSource | varchar(25) | YES | SMS | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| orderExecNum | BTREE | NON-UNIQUE | orderExecNumber |
| PRIMARY | BTREE | UNIQUE | id |
| projectCode | BTREE | NON-UNIQUE | projectCode |

---

### 170 pm_project_real_product_line_from_sms -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~16140 行 |
| 数据大小 | 3.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectCode | varchar(255) | NO |  | - | 项目编码 | 项目编码 |
| orderExecNumber | varchar(255) | YES | - | - | 执行单号 | 执行单号 |
| productFirstName | varchar(255) | NO |  | - | 产品类型 | 产品类型 |
| productName | varchar(128) | NO |  | - | 产品名 | 产品名 |
| productSubCode | varchar(255) | NO |  | - | item编码 | item编码 |
| productSubModel | varchar(255) | YES | - | - | item类型 | item类型 |
| productSubName | varchar(255) | YES | - | - | item描述 | item描述 |
| num | int(11) | NO | 0 | - | 订单数量 | 订单数量 |
| memo | mediumtext | YES | - | - | 备注 | 备注 |
| dataSource | varchar(25) | YES | SMS | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|

---

### 171 pm_project_real_product_line_from_sms_history -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~5563 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectCode | varchar(255) | NO |  | - | 项目编码 | 项目编码 |
| orderExecNumber | varchar(255) | YES | - | - | 执行单号 | 执行单号 |
| productFirstName | varchar(255) | NO |  | - | 产品类型 | 产品类型 |
| productName | varchar(128) | NO |  | - | 产品名 | 产品名 |
| productSubCode | varchar(255) | NO |  | - | item编码 | item编码 |
| productSubModel | varchar(255) | YES | - | - | item类型 | item类型 |
| productSubName | varchar(255) | YES | - | - | item描述 | item描述 |
| num | int(11) | NO | 0 | - | 订单数量 | 订单数量 |
| memo | mediumtext | YES | - | - | 备注 | 备注 |
| dataSource | varchar(25) | YES | SMS | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|

---

### 172 pm_project_related_party -- 项目相关的团体（渠道等）

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目相关的团体（渠道等） |
| 数据量 | ~126864 行 |
| 数据大小 | 12.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| projectId | int(11) | YES | - | MUL | - | 业务含义待确认 |
| partyRole | varchar(45) | YES | - | MUL | - | 业务含义待确认 |
| partyCode | varchar(45) | YES | - | - | - | 业务含义待确认 |
| partyName | varchar(45) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(45) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | 更新时间 | 更新时间 |
| updateBy | varchar(45) | YES | - | - | 更新人 | 更新人 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| partyRole_parojectId | BTREE | NON-UNIQUE | partyRole,projectId |
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 173 pm_project_shipment -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~460132 行 |
| 数据大小 | 117.64 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| projectId | int(11) | YES | - | MUL | - | 业务含义待确认 |
| barcode | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| itemCode | varchar(25) | YES | - | - | - | 业务含义待确认 |
| itemModel | varchar(255) | YES | - | - | - | 业务含义待确认 |
| itemName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| receiveName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| emsNum | varchar(255) | YES | - | - | - | 业务含义待确认 |
| emsCompany | varchar(15) | YES | - | - | - | 业务含义待确认 |
| packdate | datetime | YES | - | - | - | 业务含义待确认 |
| contractNo | varchar(50) | YES | - | MUL | - | 业务含义待确认 |
| installAddress | text | YES | - | - | - | 业务含义待确认 |
| chProjectId | int(11) | YES | - | - | 串货转移之前的projectId | 串货转移之前的projectId |
| chContractNo | varchar(50) | YES | - | - | 串货转移之前的contractNo | 串货转移之前的contractNo |
| transferProjectId | int(11) | YES | - | - | 串货转移之后的projectId | 串货转移之后的projectId |
| transferContractNo | varchar(50) | YES | - | - | 串货转移之后的projectId | 串货转移之后的projectId |
| transferFlag | varchar(2) | YES | -1 | - | 转移标识，默认:-1,转出:1，转入:0 | 转移标识，默认:-1,转出:1，转入:0 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barcode | BTREE | NON-UNIQUE | barcode |
| contractNo | BTREE | NON-UNIQUE | contractNo,barcode |
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 174 pm_project_soft_change_logs -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~13648 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 记录版本变更日志 | 记录版本变更日志 |
| projectId | int(11) | YES | - | MUL | 项目ID | 项目ID |
| changeVersion | varchar(10) | YES | - | - | V0001 | V0001 |
| changeRemark | varchar(255) | YES | - | - | 版本变更说明 | 版本变更说明 |
| latest | int(11) | YES | - | - | 0 后 1 是 | 0 后 1 是 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 175 pm_project_soft_version -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~532125 行 |
| 数据大小 | 327.84 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 项目软件版本表 | 项目软件版本表 |
| projectId | int(11) | YES | - | MUL | 项目ID | 项目ID |
| logId | int(11) | YES | - | - | 软件版本变更记录 | 软件版本变更记录 |
| contractNo | varchar(100) | YES | - | - | 合同号 | 合同号 |
| itemCode | varchar(25) | YES | - | - | 产品编码 | 产品编码 |
| barCode | varchar(25) | YES | - | MUL | 设备序列号 | 设备序列号 |
| conp | varchar(100) | YES | - | MUL | - | 业务含义待确认 |
| conpType | varchar(100) | YES | - | - | 版本类型 | 版本类型 |
| conpSeries | varchar(100) | YES | - | - | 版本系列 | 版本系列 |
| conpMark | varchar(255) | YES | - | - | 软件版本掩码 | 软件版本掩码 |
| conpBak | varchar(255) | YES | - | - | 备份变更之前的版本 | 备份变更之前的版本 |
| conpChange | int(11) | YES | - | - | 0无更新 1有更新 | 0无更新 1有更新 |
| cpld | varchar(100) | YES | - | - | - | 业务含义待确认 |
| cpldBak | varchar(255) | YES | - | - | - | 业务含义待确认 |
| cpldChange | int(11) | YES | - | - | - | 业务含义待确认 |
| boot | varchar(100) | YES | - | - | - | 业务含义待确认 |
| bootBak | varchar(255) | YES | - | - | - | 业务含义待确认 |
| bootChange | int(11) | YES | - | - | - | 业务含义待确认 |
| pcb | varchar(100) | YES | - | - | - | 业务含义待确认 |
| pcbBak | varchar(255) | YES | - | - | - | 业务含义待确认 |
| pcbChange | int(11) | YES | - | - | - | 业务含义待确认 |
| executeTime | date | YES | - | - | 若有更新的情况下为执行更新时间，否则没有实际意义 | 若有更新的情况下为执行更新时间，否则没有实际意义 |
| datastate | int(11) | YES | - | MUL | 数据状态 0 失效 1 有效 | 数据状态 0 失效 1 有效 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barcode | BTREE | NON-UNIQUE | barCode |
| idx_conp_item_query | BTREE | NON-UNIQUE | datastate,conpType,conpSeries,conpMark,itemCode,projectId |
| pm_project_soft_version_conp_IDX | BTREE | NON-UNIQUE | conp |
| PRIMARY | BTREE | UNIQUE | id |
| projectBarcodeValid | BTREE | NON-UNIQUE | projectId,barCode,datastate |

---

### 176 pm_project_soft_version_history -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~1055447 行 |
| 数据大小 | 317.84 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 项目软件版本表 | 项目软件版本表 |
| projectId | int(11) | YES | - | MUL | 项目ID | 项目ID |
| logId | int(11) | YES | - | - | 软件版本变更记录 | 软件版本变更记录 |
| contractNo | varchar(100) | YES | - | - | 合同号 | 合同号 |
| itemCode | varchar(25) | YES | - | - | 产品编码 | 产品编码 |
| barCode | varchar(25) | YES | - | MUL | 设备序列号 | 设备序列号 |
| conp | varchar(100) | YES | - | MUL | - | 业务含义待确认 |
| conpType | varchar(100) | YES | - | - | 版本类型 | 版本类型 |
| conpSeries | varchar(100) | YES | - | - | 版本系列 | 版本系列 |
| conpMark | varchar(255) | YES | - | - | 软件版本掩码 | 软件版本掩码 |
| conpBak | varchar(255) | YES | - | - | 备份变更之前的版本 | 备份变更之前的版本 |
| conpChange | int(11) | YES | - | - | 0无更新 1有更新 | 0无更新 1有更新 |
| cpld | varchar(100) | YES | - | - | - | 业务含义待确认 |
| cpldBak | varchar(255) | YES | - | - | - | 业务含义待确认 |
| cpldChange | int(11) | YES | - | - | - | 业务含义待确认 |
| boot | varchar(100) | YES | - | - | - | 业务含义待确认 |
| bootBak | varchar(255) | YES | - | - | - | 业务含义待确认 |
| bootChange | int(11) | YES | - | - | - | 业务含义待确认 |
| pcb | varchar(100) | YES | - | - | - | 业务含义待确认 |
| pcbBak | varchar(255) | YES | - | - | - | 业务含义待确认 |
| pcbChange | int(11) | YES | - | - | - | 业务含义待确认 |
| executeTime | date | YES | - | - | 若有更新的情况下为执行更新时间，否则没有实际意义 | 若有更新的情况下为执行更新时间，否则没有实际意义 |
| datastate | int(11) | YES | - | MUL | 数据状态 0 失效 1 有效 | 数据状态 0 失效 1 有效 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barcode | BTREE | NON-UNIQUE | barCode |
| idx_conp_item_query | BTREE | NON-UNIQUE | datastate,conpType,conpSeries,conpMark,itemCode,projectId |
| pm_project_soft_version_conp_IDX | BTREE | NON-UNIQUE | conp |
| PRIMARY | BTREE | UNIQUE | id |
| projectBarcodeValid | BTREE | NON-UNIQUE | projectId,barCode,datastate |

---

### 177 pm_project_soleagent_lend_from_sms -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~2583 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| soleAgentLendId | int(11) | NO | 0 | - | 总代借货跟踪 | 总代借货跟踪 |
| orderExecNumber | varchar(255) | YES | - | - | 执行单号 | 执行单号 |
| orderExecNumberShort | varchar(255) | YES | - | - | 忽略版本执行单号 | 忽略版本执行单号 |
| orderCodes | varchar(255) | YES | - | - | 合并的执行单号 | 合并的执行单号 |
| contract | varchar(25) | YES | - | MUL | 合同号 | 合同号 |
| projectName | varchar(255) | YES | - | - | 由商务输入 | 由商务输入 |
| soleAgent | varchar(25) | YES | - | - | 总代名称 | 总代名称 |
| profitCenter | varchar(6) | YES | - | - | - | 业务含义待确认 |
| dataSource | varchar(25) | YES | SMS | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contract | BTREE | NON-UNIQUE | contract,profitCenter |
| PRIMARY | BTREE | UNIQUE | id |

---

### 178 pm_project_soleagent_lend_from_sms_history -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~1136 行 |
| 数据大小 | 256.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| soleAgentLendId | int(11) | NO | 0 | - | 总代借货跟踪 | 总代借货跟踪 |
| orderExecNumber | varchar(255) | YES | - | - | 执行单号 | 执行单号 |
| orderExecNumberShort | varchar(255) | YES | - | - | 忽略版本执行单号 | 忽略版本执行单号 |
| orderCodes | varchar(255) | YES | - | - | 合并的执行单号 | 合并的执行单号 |
| contract | varchar(25) | YES | - | MUL | 合同号 | 合同号 |
| projectName | varchar(255) | YES | - | - | 由商务输入 | 由商务输入 |
| soleAgent | varchar(25) | YES | - | - | 总代名称 | 总代名称 |
| profitCenter | varchar(6) | YES | - | - | - | 业务含义待确认 |
| dataSource | varchar(25) | YES | SMS | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contract | BTREE | NON-UNIQUE | contract,profitCenter |
| PRIMARY | BTREE | UNIQUE | id |

---

### 179 pm_project_spot_check_ignore_item -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~0 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| itemCode | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| itemModel | varchar(64) | YES | - | - | - | 业务含义待确认 |
| itemName | varchar(255) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| itemCode | BTREE | NON-UNIQUE | itemCode |

---

### 180 pm_project_state -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~45915 行 |
| 数据大小 | 2.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectId | int(11) | NO | - | PRI | - | 业务含义待确认 |
| projectPlanState | varchar(10) | YES | - | MUL | 工程计划状态 | 工程计划状态 |
| projectplanTime | datetime | YES | - | - | 工程计划状态更新时间 | 工程计划状态更新时间 |
| shipmentState | varchar(11) | YES | - | MUL | 项目发货状态 -1 已发货 1 未发货 2部分发货 | 项目发货状态 -1 已发货 1 未发货 2部分发货 |
| shipmentTime | datetime | YES | - | - | 发货状态更新时间戳 | 发货状态更新时间戳 |
| executionState | varchar(45) | YES | 5 | - | 实施状态 | 实施状态 |
| executionStateTime | datetime | YES | - | - | 实施状态更新时间 | 实施状态更新时间 |
| closeProcessState | varchar(45) | YES | 10 | - | 闭环流程状态 | 闭环流程状态 |
| closeProcessStateTime | datetime | YES | - | - | 闭环流程状态更新时间 | 闭环流程状态更新时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| index_projectId | BTREE | UNIQUE | projectId |
| projectPlanState | BTREE | NON-UNIQUE | projectPlanState |
| shipmentState | BTREE | NON-UNIQUE | shipmentState |

---

### 181 pm_project_supervision -- 项目督查头信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目督查头信息 |
| 数据量 | ~818 行 |
| 数据大小 | 192.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| projectId | int(11) | NO | - | - | 项目头信息主键 | 项目头信息主键 |
| projectCode | varchar(45) | NO | - | MUL | 项目名称 | 项目名称 |
| projectName | varchar(200) | YES | - | - | 项目名称 | 项目名称 |
| channel | varchar(64) | YES | - | - | 代理商/服务商 | 代理商/服务商 |
| officeCode | varchar(25) | YES | - | MUL | 办事处编码 | 办事处编码 |
| type | varchar(25) | YES | - | - | 任务性质 | 任务性质 |
| processTime | datetime | YES | - | - | 处理时间 | 处理时间 |
| state | bit(1) | NO | b'0' | - | 是否完成 | 是否完成 |
| isDelete | bit(1) | NO | b'0' | - | 是否删除 | 是否删除 |
| quesnaireId | int(11) | YES | - | - | 问卷ID | 问卷ID |
| deliverFileIds | varchar(255) | YES |  | - | 交付件，fnd_files id | 交付件，fnd_files id |
| remark | text | YES | - | - | 备注 | 备注 |
| createTime | datetime | YES | - | - | 创建时间 | 创建时间 |
| createBy | varchar(45) | YES | - | - | 创建用户 | 创建用户 |
| updateTime | datetime | YES | - | - | 最新更新时间 | 最新更新时间 |
| updateBy | varchar(45) | YES | - | - | 最新更新用户 | 最新更新用户 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| department | BTREE | NON-UNIQUE | officeCode |
| PRIMARY | BTREE | UNIQUE | id |
| projectCode_index | BTREE | NON-UNIQUE | projectCode |

---

### 182 pm_project_task -- 项目具体任务

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目具体任务 |
| 数据量 | ~59042 行 |
| 数据大小 | 8.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| taskId | int(11) | NO | - | PRI, auto_increment | 任务ID | 任务ID |
| projectId | int(11) | YES | - | MUL | - | 业务含义待确认 |
| projectType | varchar(25) | YES | 10 | MUL | 项目类型 默认售后项目10 售前测试20 详见fnd_basic_data | 项目类型 默认售后项目10 售前测试20 详见fnd_basic_data |
| contractNo | varchar(45) | YES | - | - | 合同号 | 合同号 |
| taskTypeCode | varchar(45) | YES | - | MUL | 任务类型code，关联基础数据表 | 任务类型code，关联基础数据表 |
| taskTypeId | varchar(25) | YES | - | - | 任务类型id，关联基础数据表 | 任务类型id，关联基础数据表 |
| taskName | varchar(255) | YES | - | - | 任务名 | 任务名 |
| eventPlanHappenDate | datetime | YES | - | - | 款项计划发生日期 | 款项计划发生日期 |
| eventPlanHappenDateENG | datetime | YES | - | - | 工程计划发生日期 | 工程计划发生日期 |
| planStartTime | datetime | YES | - | - | 计划开始日期 | 计划开始日期 |
| planEndTime | datetime | YES | - | - | 计划结束日期 | 计划结束日期 |
| actualStartTime | datetime | YES | - | - | 实际开始日期 | 实际开始日期 |
| eventActualFinishDate | datetime | YES | - | - | 实际完成日期 | 实际完成日期 |
| priority | varchar(25) | YES | - | - | 优先级 | 优先级 |
| progress | int(3) | YES | 0 | - | 进度百分比 | 进度百分比 |
| progressDesc | varchar(255) | YES | - | - | 进度描述 | 进度描述 |
| status | varchar(25) | YES | 0 | - | 状态 | 状态 |
| parentId | int(11) | YES | - | - | 父级任务 | 父级任务 |
| remark | text | YES | - | - | 备注 | 备注 |
| createTime | datetime | YES | - | - | 记录数据创建时间 | 记录数据创建时间 |
| createBy | varchar(45) | YES | - | - | 记录数据创建用户 | 记录数据创建用户 |
| updateTime | datetime | YES | - | - | 记录数据最新更新时间 | 记录数据最新更新时间 |
| updateBy | varchar(45) | YES | - | - | 记录数据最新更新用户 | 记录数据最新更新用户 |
| effectiveFrom | datetime | YES | - | - | 数据有效性开始时间 | 数据有效性开始时间 |
| effectiveTo | datetime | YES | - | - | 数据有效性结束时间 | 数据有效性结束时间 |
| visibleFlag | varchar(2) | YES | 1 | - | 是否可见，1表示可见，2表示不可见 | 是否可见，1表示可见，2表示不可见 |
| deliverFileIds | varchar(255) | YES | - | - | 上传的交付件 | 上传的交付件 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | taskId |
| projectId | BTREE | NON-UNIQUE | projectId,projectType |
| projectType | BTREE | NON-UNIQUE | projectType,projectId |
| taskTypeCode_Id | BTREE | NON-UNIQUE | taskTypeCode,taskTypeId |

---

### 183 pm_project_warranty_callback -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~5588 行 |
| 数据大小 | 2.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 项目维保回访问卷表 | 项目维保回访问卷表 |
| projectId | int(11) | YES | - | MUL | 项目ID | 项目ID |
| projectCode | varchar(45) | YES | - | MUL | 项目编码 | 项目编码 |
| officeCode | varchar(25) | YES | - | - | 办事处 | 办事处 |
| contractNos | varchar(255) | YES | - | - | 合同号 | 合同号 |
| projectIds | varchar(255) | YES | - | - | 关联的项目 | 关联的项目 |
| projectName | varchar(255) | YES | - | - | 项目名称 | 项目名称 |
| serviceImpl | varchar(25) | YES | - | - | 实施方式 | 实施方式 |
| industryName | varchar(25) | YES | - | - | 行业 | 行业 |
| agentChannel | varchar(255) | YES | - | - | 下单代理商 | 下单代理商 |
| finalCustomerName | varchar(255) | YES | - | - | 最终客户单位 | 最终客户单位 |
| customer1 | tinytext | YES | - | - | 客户联系人1 | 客户联系人1 |
| customerContact1 | tinytext | YES | - | - | 客户联系方式1 | 客户联系方式1 |
| customer2 | tinytext | YES | - | - | 客户联系人2 | 客户联系人2 |
| customerContact2 | tinytext | YES | - | - | 客户联系方式2 | 客户联系方式2 |
| warrantyStartTime | date | YES | - | - | 维保开始日期 | 维保开始日期 |
| warrantyEndTime | date | YES | - | - | 维保结束日期 | 维保结束日期 |
| renewalIntention | int(1) | YES | - | - | 续保意向,0:无,1:有,2:待定 | 续保意向,0:无,1:有,2:待定 |
| callbackTime | datetime | YES | - | - | 回访时间 | 回访时间 |
| nextCallbackTime | datetime | YES | - | - | 下次回访时间 | 下次回访时间 |
| taskId | varchar(25) | YES | - | - | 任务ID | 任务ID |
| quesnaireId | int(11) | YES | - | - | 问卷ID | 问卷ID |
| quesnaireVersion | int(11) | YES | - | - | 问卷版本 | 问卷版本 |
| quesnaireState | int(11) | YES | - | - | 状态 -1 草稿 1已提交 | 状态 -1 草稿 1已提交 |
| isDelete | bit(1) | YES | b'0' | - | 删除标记 | 删除标记 |
| remark | varchar(255) | YES | - | - | 备注 | 备注 |
| compId | int(2) | YES | 0 | - | 所属公司 | 所属公司 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| customInfo | json | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectCode | BTREE | NON-UNIQUE | projectCode |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 184 pm_project_weekly -- 项目周报

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目周报 |
| 数据量 | ~932 行 |
| 数据大小 | 208.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| weeklyId | int(11) | NO | - | PRI, auto_increment | - | 业务含义待确认 |
| projectId | int(11) | YES | - | MUL | 项目信息头ID | 项目信息头ID |
| currentTask | varchar(100) | YES | - | - | 当前工程阶段 | 当前工程阶段 |
| taskStartTime | datetime | YES | - | - | 阶段开始时间 | 阶段开始时间 |
| taskEndTime | datetime | YES | - | - | 阶段结束时间 | 阶段结束时间 |
| taskDeviation | text | YES | - | - | 偏差 | 偏差 |
| remark | text | YES | - | - | 备注 | 备注 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| weeklyStartTime | datetime | YES | - | - | 报告开始时间 | 报告开始时间 |
| weeklyEndTime | datetime | YES | - | - | 报告结束时间 | 报告结束时间 |
| weeklyState | int(11) | YES | 0 | - | 周报状态 0 草稿 1提交 | 周报状态 0 草稿 1提交 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | weeklyId |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 185 pm_project_weekly_content -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~12979 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| weeklyId | int(11) | YES | - | MUL | - | 业务含义待确认 |
| optionDesc001 | text | YES | - | - | - | 业务含义待确认 |
| optionDesc002 | text | YES | - | - | - | 业务含义待确认 |
| optionType | int(11) | YES | - | - | option对应周报的部分 | option对应周报的部分 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(15) | YES | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| weeklyId | BTREE | NON-UNIQUE | weeklyId |

---

### 186 pm_project_weekly_feedback -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~20 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| weeklyId | int(11) | YES | - | MUL | - | 业务含义待确认 |
| feedback | text | YES | - | - | - | 业务含义待确认 |
| feedbacker | varchar(25) | YES | - | - | - | 业务含义待确认 |
| feedbackTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| weeklyId | BTREE | NON-UNIQUE | weeklyId |

---

### 187 pm_report_line_data -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~11315 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 报表趋势图数据集合 | 报表趋势图数据集合 |
| dataTypeCode | varchar(15) | YES | - | - | 区分统计的哪种数据 | 区分统计的哪种数据 |
| officeCode | varchar(25) | YES | - | - | 办事处 | 办事处 |
| conditionValue | varchar(25) | YES | - | - | 条件值 | 条件值 |
| totalValue | varchar(25) | YES | - | - | 总值 | 总值 |
| specificValue | varchar(25) | YES | - | - | 比值 | 比值 |
| settingTime | datetime | YES | - | - | 数据固化时间 | 数据固化时间 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 188 pm_subcontract_deliver_files -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~3823 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| subcontractId | int(11) | YES | - | MUL | 转包项目ID | 转包项目ID |
| paymentId | int(11) | YES | - | - | 转包付款ID | 转包付款ID |
| fileName | varchar(255) | YES | - | - | 交付件名称 | 交付件名称 |
| filePath | varchar(255) | YES | - | - | 交付件路径 | 交付件路径 |
| type | varchar(45) | YES | - | - | 交付件类型,0:用服交付合同，1：用服服务单，2：工程合同 | 交付件类型,0:用服交付合同，1：用服服务单，2：工程合同 |
| uploadBy | varchar(45) | YES | - | - | 上传者 | 上传者 |
| uploadTime | datetime | YES | - | - | 上传时间 | 上传时间 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| subcontractId | BTREE | NON-UNIQUE | subcontractId |

---

### 189 pm_subcontract_facilitator -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~174 行 |
| 数据大小 | 80.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| name | varchar(64) | YES | - | - | 服务商名 | 服务商名 |
| code | varchar(64) | YES | - | - | 服务商编号 | 服务商编号 |
| account | varchar(64) | YES | - | - | 服务商账户 | 服务商账户 |
| bankInfo | varchar(255) | YES | - | - | 开户行信息 | 开户行信息 |
| bankAccount | varchar(64) | YES | - | - | 收款账户 | 收款账户 |
| receiver | varchar(64) | YES | - | - | 邮箱收件人 | 邮箱收件人 |
| cnapsCode | varchar(64) | YES | - | - | 联行号 | 联行号 |
| contacts | varchar(64) | YES | - | - | 联系人 | 联系人 |
| tel | varchar(64) | YES | - | - | 联系电话 | 联系电话 |
| email | varchar(64) | YES | - | - | 邮箱账号 | 邮箱账号 |
| state | bit(1) | YES | b'1' | - | 状态 | 状态 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(45) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(45) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| relateType | varchar(45) | YES | - | - | 关联类型 | 关联类型 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 190 pm_subcontract_project_callback -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~416 行 |
| 数据大小 | 64.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 项目转包回访问卷表 | 项目转包回访问卷表 |
| subcontractId | int(11) | YES | - | - | 项目转包ID | 项目转包ID |
| taskKey | varchar(25) | YES | - | - | 任务类型 | 任务类型 |
| taskId | varchar(25) | YES | - | - | 任务ID | 任务ID |
| quesnaireId | int(11) | YES | - | - | 问卷ID | 问卷ID |
| quesnaireVersion | int(11) | YES | - | - | 问卷版本 | 问卷版本 |
| quesnaireState | int(11) | YES | - | - | 状态 -1 草稿 1已提交 | 状态 -1 草稿 1已提交 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 191 pm_subcontract_project_header -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~3220 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| subcontractName | varchar(512) | YES |  | - | 转包名称 | 转包名称 |
| subcontractNo | varchar(64) | YES |  | MUL | 转包合同号 | 转包合同号 |
| contractNos | varchar(2048) | YES |  | - | 项目合同号 | 项目合同号 |
| projectIds | varchar(1024) | YES |  | - | 转包的项目ID | 转包的项目ID |
| type | int(11) | YES | - | - | 转包类型 | 转包类型 |
| state | int(11) | NO | 0 | - | 转包状态 | 转包状态 |
| callbackState | int(11) | YES | - | - | 回访状态 | 回访状态 |
| facilitatorId | int(11) | YES | - | MUL | 服务商表ID | 服务商表ID |
| facilitatorName | varchar(64) | YES |  | - | 服务商名 | 服务商名 |
| bankInfo | varchar(255) | YES |  | - | 服务商开户地址 | 服务商开户地址 |
| bankAccount | varchar(64) | YES |  | - | 服务商收款账户 | 服务商收款账户 |
| officeCode | varchar(25) | YES |  | MUL | 办事处部门 | 办事处部门 |
| profitDepCode | varchar(25) | YES |  | MUL | 收益部门 | 收益部门 |
| isAccrued | bit(1) | YES | - | - | 是否计提 | 是否计提 |
| isInvoiced | bit(1) | YES | - | - | 是否提供发票 | 是否提供发票 |
| subcontractAmount | varchar(25) | YES |  | - | 转包价 | 转包价 |
| reason | varchar(512) | YES |  | - | 转包原因 | 转包原因 |
| remark | varchar(512) | YES |  | - | 备注 | 备注 |
| effectiveFrom | datetime | YES | - | - | 有效开始时间 | 有效开始时间 |
| effectiveTo | datetime | YES | - | - | 有效结束时间 | 有效结束时间 |
| zrApproveTime | datetime | YES | - | - | 最新主任审批通过时间 | 最新主任审批通过时间 |
| createBy | varchar(25) | YES |  | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES |  | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| orgId | int(2) | YES | 1 | - | 所属公司 | 所属公司 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| facilitatorId | BTREE | NON-UNIQUE | facilitatorId |
| officeCode | BTREE | NON-UNIQUE | officeCode |
| PRIMARY | BTREE | UNIQUE | id |
| profitDepCode | BTREE | NON-UNIQUE | profitDepCode |
| subcontractNo | BTREE | NON-UNIQUE | subcontractNo |

---

### 192 pm_subcontract_project_line -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~51088 行 |
| 数据大小 | 8.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| subcontractId | int(11) | NO | - | MUL | 转包项目Id | 转包项目Id |
| projectId | int(11) | YES | - | MUL | 原项目Id | 原项目Id |
| barcode | varchar(25) | YES | - | MUL | 设备序列号 | 设备序列号 |
| itemCode | varchar(25) | YES | - | MUL | 设备编码 | 设备编码 |
| itemModel | varchar(255) | YES | - | - | 设备型号 | 设备型号 |
| itemName | varchar(255) | YES | - | - | 设备名称 | 设备名称 |
| contractNo | varchar(50) | YES | - | MUL | 合同号 | 合同号 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barcode | BTREE | NON-UNIQUE | barcode |
| contractNo | BTREE | NON-UNIQUE | contractNo |
| itemCode | BTREE | NON-UNIQUE | itemCode |
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |
| unique_index | BTREE | UNIQUE | subcontractId,barcode |

---

### 193 pm_subcontract_project_payment -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~3351 行 |
| 数据大小 | 432.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| subcontractId | int(11) | NO | - | MUL | 转包项目Id | 转包项目Id |
| ratio | varchar(10) | YES | - | - | 比例 | 比例 |
| amount | varchar(25) | YES | - | - | 付款金额 | 付款金额 |
| confirmTime | datetime | YES | - | - | 提交时间 | 提交时间 |
| paymentTime | datetime | YES | - | - | 付款时间 | 付款时间 |
| remark | varchar(512) | YES | - | - | 备注 | 备注 |
| sseId | bigint(20) | YES | -1 | - | sse报销单审批行ID,0：会进行匹配跟新 | sse报销单审批行ID,0：会进行匹配跟新 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| subcontractId | BTREE | NON-UNIQUE | subcontractId |

---

### 194 pm_subcontract_project_payment_sse -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~3608 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) unsigned | YES | 0 | MUL | - | 主键ID |
| workNo | varchar(10) | YES | - | MUL | 工号 | 工号 |
| name | varchar(10) | YES | - | - | 姓名 | 姓名 |
| offerNum | varchar(20) | YES | - | - | 申请单号 | 申请单号 |
| applyAmount | decimal(16,2) | YES | - | - | 申请金额 | 申请金额 |
| receiver | varchar(255) | YES | - | - | 收款人 | 收款人 |
| bank | varchar(80) | YES | - | - | 开户行 | 开户行 |
| bankAccount | varchar(255) | YES | - | - | 银行账号 | 银行账号 |
| useage | varchar(512) | YES | - | - | 汇款用途 | 汇款用途 |
| paystate | varchar(25) | YES | - | - | 付款状态 | 付款状态 |
| confirmTime | datetime | YES | - | - | 提交时间 | 提交时间 |
| paymentTime | datetime | YES | - | - | 付款时间 | 付款时间 |
| approveState | varchar(25) | NO |  | - | 审批状态 | 审批状态 |
| type | varchar(255) | YES | - | - | 费用类别 | 费用类别 |
| approveAmount | decimal(16,2) | YES | - | - | 权签金额 | 权签金额 |
| remark | text | YES | - | - | 说明 | 说明 |
| subcontractNo | varchar(255) | YES | - | MUL | 服务合同号 | 服务合同号 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| id | BTREE | NON-UNIQUE | id |
| subcontractNo | BTREE | NON-UNIQUE | subcontractNo |
| workNo | BTREE | NON-UNIQUE | workNo |

---

### 195 pm_subcontract_project_price -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~5176 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| subcontractId | int(11) | NO | - | - | 转包项目Id | 转包项目Id |
| contractNo | varchar(50) | YES | - | - | 合同号 | 合同号 |
| orderExecNumber | varchar(25) | YES | - | - | 执行单号 | 执行单号 |
| projectCode | varchar(25) | YES | - | - | 项目编码 | 项目编码 |
| engineeFee | varchar(25) | YES | - | - | 工程服务价 | 工程服务价 |
| objId | varchar(64) | YES | - | - | SMS链接参数1 | SMS链接参数1 |
| procType | varchar(25) | YES | - | - | SMS链接参数2 | SMS链接参数2 |
| price | varchar(25) | YES | - | - | 合同转包价 | 合同转包价 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 196 pm_workflow -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~180 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| processKey | varchar(25) | NO |  | - | 流程定义key | 流程定义key |
| taskKey | varchar(25) | YES |  | - | 任务Key | 任务Key |
| applyTime | datetime | YES | - | - | 申请时间 | 申请时间 |
| beginTime | datetime | YES | - | - | 开始时间 | 开始时间 |
| endTime | datetime | YES | - | - | 结束时间 | 结束时间 |
| dueTime | datetime | YES | - | - | 过期时间 | 过期时间 |
| procInstId | varchar(64) | YES |  | MUL | 流程实例ID | 流程实例ID |
| message | varchar(255) | YES |  | - | 处理消息 | 处理消息 |
| status | varchar(255) | NO | PENDING | - | 状态 | 状态 |
| userId | int(11) | NO | 0 | - | userinfo表ID | userinfo表ID |
| objType | varchar(25) | NO |  | MUL | 对象类型 | 对象类型 |
| objId | int(11) | NO | 0 | MUL | 对象Id | 对象Id |
| dataType | varchar(25) | NO |  | - | 数据类型 | 数据类型 |
| dataId | int(11) | NO | 0 | - | 数据Id | 数据Id |
| createBy | varchar(45) | NO |  | - | - | 业务含义待确认 |
| createTime | datetime | NO | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| updateBy | varchar(45) | NO |  | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| orgId | int(2) | YES | 0 | - | 组织ID | 组织ID |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| objDataKey | BTREE | NON-UNIQUE | objType,objId,dataType,dataId |
| PRIMARY | BTREE | UNIQUE | id |
| procInstId | BTREE | NON-UNIQUE | procInstId |
| worfFlow_objId | BTREE | NON-UNIQUE | objId |

---

### 197 prob_main -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~1080 行 |
| 数据大小 | 3.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| probNum | varchar(25) | YES | - | MUL | 编码 | 编码 |
| watch | varchar(10) | YES | - | - | 跟踪 | 跟踪 |
| theme | varchar(255) | YES | - | - | 主题 | 主题 |
| desc | text | YES | - | - | 问题描述 | 问题描述 |
| solution | text | YES | - | - | 解决方案 | 解决方案 |
| status | varchar(10) | YES | - | - | 状态 | 状态 |
| startdate | date | YES | - | - | 开始日期 | 开始日期 |
| duedate | date | YES | - | - | 计划完成日期 | 计划完成日期 |
| attachments | varchar(255) | YES | - | - | 文件 | 文件 |
| priority | varchar(10) | YES | - | - | 严重级别 | 严重级别 |
| productType | text | YES | - | - | 产品类型 | 产品类型 |
| trackingUser | varchar(10) | YES | - | - | 跟踪用户 | 跟踪用户 |
| visibleRange | int(1) | NO | 0 | - | 可见范围，0:All, 1:对内 | 可见范围，0:All, 1:对内 |
| createBy | varchar(15) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |
| remark | text | YES | - | - | 审批意见 | 审批意见 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |
| probTicketNo | varchar(255) | YES | - | - | 网上问题单号 | 网上问题单号 |
| relatedSceneTypes | varchar(255) | YES | - | - | relatedSceneTypes | relatedSceneTypes |
| relatedSceneTypesMark | bigint(20) | YES | - | - | relatedSceneTypes的bitmark | relatedSceneTypes的bitmark |
| mitigationActionTypes | varchar(255) | YES | - | - | mitigationActionTypes | mitigationActionTypes |
| mitigationActionTypesMark | bigint(20) | YES | - | - | mitigationActionTypes的bitmark | mitigationActionTypes的bitmark |
| solutionActionTypes | varchar(255) | YES | - | - | solutionActionTypes | solutionActionTypes |
| solutionActionTypesMark | bigint(20) | YES | - | - | solutionActionTypes的bitmark | solutionActionTypes的bitmark |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| probNum_IDX | BTREE | NON-UNIQUE | probNum,id |

---

### 198 prob_product -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~31823 行 |
| 数据大小 | 4.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| probId | int(11) | YES | 0 | MUL | ProbId | ProbId |
| productCode | varchar(255) | YES |  | - | 产品大类 | 产品大类 |
| productSubCode | varchar(255) | YES |  | - | 产品小类 | 产品小类 |
| itemCode | varchar(255) | NO |  | - | item编码 | item编码 |
| itemModel | varchar(255) | YES | - | - | item类型 | item类型 |
| itemDesc | varchar(255) | YES | - | - | item描述 | item描述 |
| status | int(11) | YES | 1 | - | 0 失效 1 有效 | 0 失效 1 有效 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| probId_Status_IDX | BTREE | NON-UNIQUE | probId,status |
| probId_status_item_IDX | BTREE | NON-UNIQUE | probId,status,itemCode |

---

### 199 prob_product_component -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~66 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| type | varchar(100) | YES | - | - | 分组 | 分组 |
| name | varchar(100) | YES | - | - | 名称 | 名称 |
| version | varchar(100) | YES | - | - | 版本 | 版本 |
| parentId | int(11) | YES | - | - | 父节点 | 父节点 |
| state | bit(1) | YES | b'1' | - | 状态 | 状态 |
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

### 200 prob_read_log -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~43284 行 |
| 数据大小 | 2.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| probId | int(11) | NO | - | - | - | 业务含义待确认 |
| reader | varchar(25) | NO |  | - | 查阅人 | 查阅人 |
| readTime | datetime | NO | - | - | 查阅时间 | 查阅时间 |
| status | int(1) | NO | 0 | - | 是否已经确认查阅 | 是否已经确认查阅 |
| firstTime | datetime | YES | - | - | 第一次查阅时间 | 第一次查阅时间 |
| commitTime | datetime | YES | - | - | 确认时间 | 确认时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 201 prob_restore -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~1269 行 |
| 数据大小 | 288.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 问题修复数据对象 | 问题修复数据对象 |
| probId | int(11) | YES | 0 | MUL | 涉及到的问题ID | 涉及到的问题ID |
| serialNum | varchar(50) | YES | - | MUL | 序列号 | 序列号 |
| itemModel | varchar(50) | YES | - | MUL | 设备类型 | 设备类型 |
| processId | int(11) | YES | 0 | MUL | 记录任务流程过程中的相关信息 | 记录任务流程过程中的相关信息 |
| officeCode | varchar(25) | YES | - | - | 办事处编码 | 办事处编码 |
| conp | varchar(255) | YES | - | - | 任务发布时的软件版本 | 任务发布时的软件版本 |
| boot | varchar(100) | YES | - | - | - | 业务含义待确认 |
| cpld | varchar(100) | YES | - | - | - | 业务含义待确认 |
| pcb | varchar(100) | YES | - | - | - | 业务含义待确认 |
| projectId | int(11) | YES | 0 | MUL | 涉及到的项目ID | 涉及到的项目ID |
| projectName | varchar(255) | YES | - | - | 项目名称 | 项目名称 |
| contractNo | varchar(255) | YES | - | - | 合同号 | 合同号 |
| assignee | varchar(25) | YES | - | - | 办理用户 | 办理用户 |
| assigneeRole | int(11) | YES | 0 | - | 办理角色 | 办理角色 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| itemModel | BTREE | NON-UNIQUE | itemModel |
| PRIMARY | BTREE | UNIQUE | id |
| probId_serialNum_IDX | BTREE | NON-UNIQUE | probId,serialNum |
| processId | BTREE | NON-UNIQUE | processId |
| projectId | BTREE | NON-UNIQUE | projectId |
| serialNum | BTREE | NON-UNIQUE | serialNum |

---

### 202 prob_restore_process -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~9 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 记录问题修复的流程流转过程 | 记录问题修复的流程流转过程 |
| probId | int(11) | YES | - | MUL | 问题ID | 问题ID |
| restoreStatus | int(11) | YES | - | - | 修复任务流转状态 | 修复任务流转状态 |
| restoreRemark | text | YES | - | - | 流转备注说明 | 流转备注说明 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| probId | BTREE | NON-UNIQUE | probId,restoreStatus |

---

### 203 prob_restore_weekly -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~0 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 任务进展周报 | 任务进展周报 |
| probId | int(11) | YES | - | MUL | 问题主键 | 问题主键 |
| fileId | int(11) | YES | - | - | 附件ID | 附件ID |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| probId | BTREE | NON-UNIQUE | probId |

---

### 204 prob_softwares -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~11456 行 |
| 数据大小 | 11.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 已知问题影响的软件版本表 | 已知问题影响的软件版本表 |
| probId | int(11) | YES | 0 | MUL | 问题ID | 问题ID |
| conp | varchar(100) | YES | - | MUL | - | 业务含义待确认 |
| cpld | varchar(100) | YES | - | MUL | - | 业务含义待确认 |
| boot | varchar(100) | YES | - | MUL | - | 业务含义待确认 |
| pcb | varchar(100) | YES | - | MUL | - | 业务含义待确认 |
| manualEntry | varchar(2048) | YES | - | - | 手工录入 | 手工录入 |
| manualEntrySub | varchar(2048) | YES | - | - | 手工录入拆解 | 手工录入拆解 |
| entryType | varchar(100) | YES | - | - | 版本类型 | 版本类型 |
| entrySeries | varchar(100) | YES | - | - | 版本系列 | 版本系列 |
| entryStart | varchar(255) | YES | - | - | 版本范围开始 | 版本范围开始 |
| entryEnd | varchar(255) | YES | - | - | 版本范围结束 | 版本范围结束 |
| markStart | varchar(255) | YES | - | - | 缺省补充版本范围开始 | 缺省补充版本范围开始 |
| markEnd | varchar(255) | YES | - | - | 缺省补充版本范围结束 | 缺省补充版本范围结束 |
| affectedType | int(11) | YES | 0 | MUL | 影响类型，0：所有系列，1：盒式系列，2：框式系列 | 影响类型，0：所有系列，1：盒式系列，2：框式系列 |
| groupId | bigint(11) | YES | 0 | - | 分组ID | 分组ID |
| splited | int(11) | YES | 0 | - | 是否拆解 | 是否拆解 |
| datastate | int(11) | YES | 1 | MUL | 0 失效 1 有效 | 0 失效 1 有效 |
| createBy | varchar(10) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(10) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| affectedType | BTREE | NON-UNIQUE | affectedType |
| boot | BTREE | NON-UNIQUE | boot |
| conp | BTREE | NON-UNIQUE | conp |
| cpld | BTREE | NON-UNIQUE | cpld |
| datastate_entry_probId_IDX | BTREE | NON-UNIQUE | datastate,entryType,entrySeries,probId |
| pcb | BTREE | NON-UNIQUE | pcb |
| PRIMARY | BTREE | UNIQUE | id |
| probId_datastate_IDX | BTREE | NON-UNIQUE | probId,datastate |

---

### 205 prob_soft_version -- 

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
| conp | varchar(100) | YES | - | MUL | - | 业务含义待确认 |
| cpld | varchar(100) | YES | - | - | - | 业务含义待确认 |
| boot | varchar(100) | YES | - | - | - | 业务含义待确认 |
| pcb | varchar(100) | YES | - | - | - | 业务含义待确认 |
| createdBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createdTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| conp | BTREE | UNIQUE | conp,cpld,boot,pcb |
| PRIMARY | BTREE | UNIQUE | id |

---

### 206 project_info_from_sms -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~3190 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| smsId | bigint(11) | NO | 0 | - | - | 业务含义待确认 |
| orderCode | varchar(25) | NO | - | MUL | - | 业务含义待确认 |
| predBidDate | datetime | YES | - | - | - | 业务含义待确认 |
| projectName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| firstChannelCode | varchar(255) | YES | - | - | - | 业务含义待确认 |
| firstChannelName | varchar(100) | YES | - | - | - | 业务含义待确认 |
| channelCode | varchar(255) | YES | - | - | - | 业务含义待确认 |
| channelName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| contractNo | varchar(25) | YES | - | - | - | 业务含义待确认 |
| marketName | varchar(25) | YES | - | - | - | 业务含义待确认 |
| systemName | varchar(25) | YES | - | - | - | 业务含义待确认 |
| expendName | varchar(25) | YES | - | - | - | 业务含义待确认 |
| industryName | varchar(25) | YES | - | - | - | 业务含义待确认 |
| industryNewName | varchar(25) | YES | - | - | 对应的子行业 | 对应的子行业 |
| totaljine | decimal(12,2) | YES | - | - | - | 业务含义待确认 |
| salesName | varchar(25) | YES | - | - | - | 业务含义待确认 |
| officeName | varchar(25) | YES | - | - | - | 业务含义待确认 |
| solutionname | varchar(1000) | YES | - | - | - | 业务含义待确认 |
| projectpropertyName | varchar(20) | YES | - | - | - | 业务含义待确认 |
| customerProjectCode | varchar(255) | YES | - | - | 客户项目编码 | 客户项目编码 |
| customerProjectName | varchar(255) | YES | - | - | 客户项目名称 | 客户项目名称 |
| username | varchar(255) | YES | - | - | - | 业务含义待确认 |
| realname | varchar(128) | YES | - | - | - | 业务含义待确认 |
| officeCode | varchar(255) | YES | - | - | - | 业务含义待确认 |
| org_id | int(11) | YES | - | MUL | - | 业务含义待确认 |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |
| source | varchar(25) | YES | SMS | - | 数据来源 | 数据来源 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| orderCode | BTREE | NON-UNIQUE | orderCode,org_id |
| org_id | BTREE | NON-UNIQUE | org_id |

---

### 207 rma_applicant -- RMA申请

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

### 208 rma_app_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
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

### 209 rma_bar -- 

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

### 210 rma_info2mes_result -- 

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

### 211 rma_repair_report_from_mes -- 

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

### 212 rma_spare_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
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

### 213 role -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
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

### 214 serve_type -- 服务类型

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 服务类型 |
| 数据量 | ~4 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| serve | varchar(10) | NO | - | MUL | - | 业务含义待确认 |
| serve_type | varchar(10) | YES | - | - | - | 业务含义待确认 |
| remark | text | YES | - | - | 备注 | 备注 |
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| serve_where_index | BTREE | NON-UNIQUE | serve |

---

### 215 shipment_barcode_from_spms_unique -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~233076 行 |
| 数据大小 | 17.55 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contract_code | varchar(25) | YES | - | - | - | 业务含义待确认 |
| barcode | varchar(50) | YES | - | - | - | 业务含义待确认 |
| itemCode | varchar(16) | YES | - | - | - | 业务含义待确认 |
| barcode2 | varchar(50) | YES | - | - | - | 业务含义待确认 |
| itemCode2 | varchar(16) | YES | - | - | - | 业务含义待确认 |
| rmaState | int(1) | NO | 0 | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|

---

### 216 sms_ofst_contract_head_sap -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~60264 行 |
| 数据大小 | 22.56 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | bigint(20) | NO | - | PRI, auto_increment | 表信息：从SAP刷新的合同头信息，此表信息定时刷新 | 表信息：从SAP刷新的合同头信息，此表信息定时刷新 |
| contract_num | varchar(45) | YES | - | MUL | - | 业务含义待确认 |
| batch_code | varchar(10) | YES | - | - | - | 业务含义待确认 |
| project_name | varchar(200) | YES | - | - | - | 业务含义待确认 |
| order_num | varchar(25) | YES | - | - | - | 业务含义待确认 |
| client_supplier_code | varchar(20) | YES | - | - | - | 业务含义待确认 |
| client_supplier_name | varchar(200) | YES | - | - | - | 业务含义待确认 |
| contract_money_amount | decimal(20,2) | NO | - | - | - | 业务含义待确认 |
| delivered_money_amount | decimal(20,2) | NO | - | - | - | 业务含义待确认 |
| collected_money_amount | decimal(20,2) | NO | - | - | - | 业务含义待确认 |
| collected_money_ratio | double | YES | 0 | - | - | 业务含义待确认 |
| receivables_money_amount | decimal(20,2) | YES | - | - | - | 业务含义待确认 |
| over_due_money_amount | decimal(20,2) | YES | - | - | - | 业务含义待确认 |
| maketing_department_name | varchar(40) | YES | - | - | - | 业务含义待确认 |
| office_name | varchar(20) | YES | - | - | - | 业务含义待确认 |
| industry_name | varchar(40) | YES | - | - | - | 业务含义待确认 |
| marketing_representative_name | varchar(20) | YES | - | - | - | 业务含义待确认 |
| currency_name | varchar(25) | YES | - | - | 币种 | 币种 |
| create_by | varchar(20) | YES | - | - | - | 创建人 |
| create_time | datetime | YES | - | - | - | 创建时间 |
| update_by | varchar(20) | YES | - | - | - | 更新人 |
| update_time | datetime | YES | - | - | - | 更新时间 |
| effective_from | datetime | YES | - | - | - | 业务含义待确认 |
| effective_to | datetime | YES | - | - | - | 业务含义待确认 |
| import_batch_num | varchar(12) | YES | - | - | - | 业务含义待确认 |
| contract_create_date | datetime | YES | - | - | SAP合同创建日期 | SAP合同创建日期 |
| projectCode | varchar(80) | YES | - | - | - | 业务含义待确认 |
| marketCode | varchar(80) | YES | - | - | - | 业务含义待确认 |
| systemId | int(11) | YES | - | - | - | 业务含义待确认 |
| industryId | int(11) | YES | - | - | - | 业务含义待确认 |
| officeCode | varchar(80) | YES | - | - | - | 业务含义待确认 |
| expendId | int(11) | YES | - | - | - | 业务含义待确认 |
| usernamec | varchar(10) | YES | - | - | 销售用户账号 | 销售用户账号 |
| latest_ship_date | datetime | YES | - | - | 交货日期 | 交货日期 |
| usernamec2 | varchar(10) | YES | - | - | - | 业务含义待确认 |
| systemid_o | int(11) | YES | - | - | - | 业务含义待确认 |
| expendid_o | int(11) | YES | - | - | - | 业务含义待确认 |
| industry_name_o | varchar(40) | YES | - | - | - | 业务含义待确认 |
| dataSource | varchar(25) | YES | CRM | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| index_contract_num | BTREE | NON-UNIQUE | contract_num |
| PRIMARY | BTREE | UNIQUE | id |

---

### 217 sms_ofst_contract_head_sap_history -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~45355 行 |
| 数据大小 | 17.55 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | bigint(20) | NO | - | PRI, auto_increment | 表信息：从SAP刷新的合同头信息，此表信息定时刷新 | 表信息：从SAP刷新的合同头信息，此表信息定时刷新 |
| contract_num | varchar(45) | YES | - | MUL | - | 业务含义待确认 |
| batch_code | varchar(10) | YES | - | - | - | 业务含义待确认 |
| project_name | varchar(200) | YES | - | - | - | 业务含义待确认 |
| order_num | varchar(25) | YES | - | - | - | 业务含义待确认 |
| client_supplier_code | varchar(20) | YES | - | - | - | 业务含义待确认 |
| client_supplier_name | varchar(200) | YES | - | - | - | 业务含义待确认 |
| contract_money_amount | decimal(20,2) | NO | - | - | - | 业务含义待确认 |
| delivered_money_amount | decimal(20,2) | NO | - | - | - | 业务含义待确认 |
| collected_money_amount | decimal(20,2) | NO | - | - | - | 业务含义待确认 |
| collected_money_ratio | double | YES | 0 | - | - | 业务含义待确认 |
| receivables_money_amount | decimal(20,2) | YES | - | - | - | 业务含义待确认 |
| over_due_money_amount | decimal(20,2) | YES | - | - | - | 业务含义待确认 |
| maketing_department_name | varchar(40) | YES | - | - | - | 业务含义待确认 |
| office_name | varchar(20) | YES | - | - | - | 业务含义待确认 |
| industry_name | varchar(40) | YES | - | - | - | 业务含义待确认 |
| marketing_representative_name | varchar(20) | YES | - | - | - | 业务含义待确认 |
| currency_name | varchar(25) | YES | - | - | 币种 | 币种 |
| create_by | varchar(20) | YES | - | - | - | 创建人 |
| create_time | datetime | YES | - | - | - | 创建时间 |
| update_by | varchar(20) | YES | - | - | - | 更新人 |
| update_time | datetime | YES | - | - | - | 更新时间 |
| effective_from | datetime | YES | - | - | - | 业务含义待确认 |
| effective_to | datetime | YES | - | - | - | 业务含义待确认 |
| import_batch_num | varchar(12) | YES | - | - | - | 业务含义待确认 |
| contract_create_date | datetime | YES | - | - | SAP合同创建日期 | SAP合同创建日期 |
| projectCode | varchar(80) | YES | - | - | - | 业务含义待确认 |
| marketCode | varchar(80) | YES | - | - | - | 业务含义待确认 |
| systemId | int(11) | YES | - | - | - | 业务含义待确认 |
| industryId | int(11) | YES | - | - | - | 业务含义待确认 |
| officeCode | varchar(80) | YES | - | - | - | 业务含义待确认 |
| expendId | int(11) | YES | - | - | - | 业务含义待确认 |
| usernamec | varchar(10) | YES | - | - | 销售用户账号 | 销售用户账号 |
| latest_ship_date | datetime | YES | - | - | 交货日期 | 交货日期 |
| usernamec2 | varchar(10) | YES | - | - | - | 业务含义待确认 |
| systemid_o | int(11) | YES | - | - | - | 业务含义待确认 |
| expendid_o | int(11) | YES | - | - | - | 业务含义待确认 |
| industry_name_o | varchar(40) | YES | - | - | - | 业务含义待确认 |
| dataSource | varchar(25) | YES | SMS | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| index_contract_num | BTREE | NON-UNIQUE | contract_num |
| PRIMARY | BTREE | UNIQUE | id |

---

### 218 spare_parts -- 备件 contract_sub_type(0RMA 1保障 2库存)

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

### 219 spare_parts_applicant -- 项目保障备件申请

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

### 220 sys_state_or_type -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~30 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| stCode | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| stName | varchar(25) | YES | - | - | - | 业务含义待确认 |
| resolveCode | varchar(10) | YES | - | MUL | - | 业务含义待确认 |
| resolveName | varchar(25) | YES | - | - | - | 业务含义待确认 |
| validity | int(11) | YES | 1 | - | 1有效 0 无效 | 1有效 0 无效 |
| remark | varchar(100) | YES | - | - | 说明 | 说明 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| st | BTREE | NON-UNIQUE | resolveCode |
| stCode | BTREE | NON-UNIQUE | stCode |

---

### 221 tain_type -- 维保类型

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 维保类型 |
| 数据量 | ~3 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| tain | varchar(10) | NO | - | MUL | - | 业务含义待确认 |
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| tain_type | varchar(50) | YES | - | - | - | 业务含义待确认 |
| remark | text | YES | - | - | 备注 | 备注 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| tain_where_index | BTREE | NON-UNIQUE | tain |

---

### 222 tb_sys_log -- 

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

### 223 temp_contract_market_system -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~59320 行 |
| 数据大小 | 4.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contract_code | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| marketCode | varchar(10) | YES | - | - | - | 业务含义待确认 |
| marketName | varchar(15) | YES | - | - | - | 业务含义待确认 |
| systemId | int(11) | YES | - | - | - | 业务含义待确认 |
| systemName | varchar(15) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contract_code | BTREE | NON-UNIQUE | contract_code |

---

### 224 temp_max_ppfs -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~41086 行 |
| 数据大小 | 8.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | 0 | MUL | - | 主键ID |
| projectCode | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| orderExecNumber | varchar(25) | YES | - | - | - | 业务含义待确认 |
| serviceTypeName | varchar(10) | YES | - | - | - | 业务含义待确认 |
| channelName | varchar(255) | YES | - | - | 出货代理商名称 | 出货代理商名称 |
| agentName | varchar(500) | YES | - | - | 代理商名称 | 代理商名称 |
| salesManCode | varchar(10) | YES | - | - | - | 业务含义待确认 |
| salesManName | varchar(10) | YES | - | - | - | 业务含义待确认 |
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

### 225 temp_project_sales_change -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
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

### 226 temp_query_shipment -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~145128 行 |
| 数据大小 | 13.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| packlist_id | varchar(64) | YES | - | - | - | 业务含义待确认 |
| con_id | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| period | varchar(6) | YES | - | MUL | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| period | BTREE | NON-UNIQUE | period,con_id,packlist_id |
| period2 | BTREE | NON-UNIQUE | con_id,packlist_id,period |

---

### 227 temp_query_shipment_barcode -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~335062 行 |
| 数据大小 | 28.56 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| pack_id | varchar(64) | YES | - | MUL | - | 业务含义待确认 |
| item | varchar(16) | YES | - | - | - | 业务含义待确认 |
| count | bigint(21) | NO | 0 | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| packItem | BTREE | NON-UNIQUE | pack_id,item,count |

---

### 228 tmp_tb_contract_shipment -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
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
| contractNo | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
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

### 229 tmp_tb_project_contract -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
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
| sourceContractNo | varchar(45) | YES | - | MUL | - | 业务含义待确认 |
| profitCenter | varchar(255) | YES | - | - | 办事处编码 | 办事处编码 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | contractNo,projectId |
| sourceContractNo | BTREE | NON-UNIQUE | sourceContractNo,projectId |

---

### 230 tmp_tb_project_filtered -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~27778 行 |
| 数据大小 | 13.55 MB |

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

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|

---

### 231 tmp_tb_project_shipment -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
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
| 序列号 | varchar(50) | YES | - | - | - | 业务含义待确认 |
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

### 232 tmp_tb_view_shipment_ems_4_pm -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~92528 行 |
| 数据大小 | 8.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contract_code | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| receiveName | text | YES | - | - | 收件人 | 收件人 |
| emsNum | text | YES | - | - | 快递单号 | 快递单号 |
| packdate | datetime | YES | - | - | - | 业务含义待确认 |
| emsCompany | mediumtext | YES | - | - | - | 业务含义待确认 |
| packId | varchar(64) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | contract_code,packId |

---

### 233 tmp_tb_view_shipment_info_4_pm -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~386195 行 |
| 数据大小 | 311.98 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contract_code | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| itemCode | varchar(16) | YES | - | - | - | 业务含义待确认 |
| itemModel | varchar(255) | YES | - | - | - | 业务含义待确认 |
| itemName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| barcode | varchar(50) | YES | - | - | - | 业务含义待确认 |
| comBarcode | varchar(50) | YES | - | - | - | 业务含义待确认 |
| packId | varchar(64) | YES | - | - | - | 业务含义待确认 |
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

### 234 transnum -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~0 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| transNum | int(50) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|

---

### 235 tx_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~60939 行 |
| 数据大小 | 7.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| tx_id | int(11) | NO | - | PRI, auto_increment | - | 业务含义待确认 |
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
| turnovertimes | int(11) | YES | - | - | - | 业务含义待确认 |
| allottimes | int(11) | YES | - | - | - | 业务含义待确认 |
| instead_time | datetime | YES | - | - | - | 业务含义待确认 |
| datastate | int(1) | YES | 1 | - | 保持历史数据有效性 0 失效 1 有效 | 保持历史数据有效性 0 失效 1 有效 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | tx_id |
| sheetID | BTREE | NON-UNIQUE | sheetID |

---

### 236 t_company -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~3 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI | 公司ID，关联表外键 | 公司ID，关联表外键 |
| compCode | varchar(10) | NO |  | MUL | 公司编号 | 公司编号 |
| compName | varchar(100) | NO |  | - | 公司名称 | 公司名称 |
| compAbbr | varchar(100) | YES |  | - | 公司简称 | 公司简称 |
| compAccount | varchar(10) | YES |  | - | 公司账套 | 公司账套 |
| adminID | int(11) | NO | 0 | MUL | 上级ID | 上级ID |
| compGrade | int(11) | YES | 1 | - | 公司级别 | 公司级别 |
| lawyer | varchar(50) | YES |  | - | 法人 | 法人 |
| address | varchar(200) | YES |  | - | 地址 | 地址 |
| regAddress | varchar(200) | YES |  | - | 注册地址 | 注册地址 |
| tel | varchar(50) | YES |  | - | 电话 | 电话 |
| fax | varchar(50) | YES |  | - | 传真 | 传真 |
| postCode | varchar(50) | YES |  | - | 邮编 | 邮编 |
| webSite | varchar(100) | YES |  | - | 网站 | 网站 |
| state | bit(1) | NO | b'1' | - | 失效状态 | 失效状态 |
| effectiveFrom | datetime | NO | CURRENT_TIMESTAMP | - | 成立时间 | 成立时间 |
| effectiveTo | datetime | YES | - | - | 结束时间 | 结束时间 |
| disabledTime | datetime | YES | - | - | 失效时间 | 失效时间 |
| remark | varchar(500) | YES |  | - | 备注 | 备注 |
| createBy | varchar(25) | NO |  | - | - | 业务含义待确认 |
| createTime | datetime | NO | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES |  | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| adminID | BTREE | NON-UNIQUE | adminID |
| compCode | BTREE | NON-UNIQUE | compCode |
| PRIMARY | BTREE | UNIQUE | id |

---

### 237 t_data_field_relation -- 

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
| dataName | varchar(255) | NO | - | - | 数据名 | 数据名 |
| dataType | varchar(255) | NO | - | - | 数据类型 | 数据类型 |
| dataId | int(11) | YES | 0 | - | 数据实例ID | 数据实例ID |
| field | varchar(128) | NO | - | - | 字段 | 字段 |
| alias | varchar(128) | YES | - | - | 字段别名 | 字段别名 |
| name | varchar(128) | NO | - | - | 字段名 | 字段名 |
| title | varchar(255) | YES | - | - | 字段标题 | 字段标题 |
| titleKey | varchar(255) | YES | - | - | 字段标题Key | 字段标题Key |
| cssId | varchar(255) | YES | - | - | 字段CSS id | 字段CSS id |
| cssClass | varchar(255) | YES | - | - | 字段CSS class | 字段CSS class |
| cssStyle | varchar(255) | YES | - | - | 字段CSS style | 字段CSS style |
| type | varchar(255) | YES | - | - | 字段类型 | 字段类型 |
| render | varchar(4096) | YES | - | - | 字段处理 | 字段处理 |
| sort | int(11) | YES | 0 | - | 排序 | 排序 |
| orderable | bit(1) | YES | b'1' | - | 允许排序 | 允许排序 |
| searchable | bit(1) | YES | b'0' | - | 允许搜索 | 允许搜索 |
| visible | bit(1) | YES | b'1' | - | 允许可见 | 允许可见 |
| required | bit(1) | YES | b'0' | - | 必填 | 必填 |
| readonly | bit(1) | YES | b'0' | - | 只读 | 只读 |
| disabled | bit(1) | YES | b'0' | - | 组件失效 | 组件失效 |
| extData | varchar(8192) | YES | - | - | 外部数据 | 外部数据 |
| extKey | varchar(255) | YES | - | - | 外部数据key | 外部数据key |
| extValue | varchar(255) | YES | - | - | 外部数据value | 外部数据value |
| media | varchar(255) | YES | - | - | 传播媒介 | 传播媒介 |
| clazzName | varchar(255) | YES | - | - | 类名 | 类名 |
| superData | varchar(255) | YES | - | - | 父类dataName | 父类dataName |
| status | int(1) | YES | 1 | - | 状态 | 状态 |
| compId | int(11) | YES | - | - | 公司ID | 公司ID |
| isSystemField | bit(1) | YES | b'1' | - | 是否为系统字段 | 是否为系统字段 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 238 t_data_operation -- 数据的导入导出控制表

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 数据的导入导出控制表 |
| 数据量 | ~14 行 |
| 数据大小 | 80.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| name | varchar(25) | NO |  | - | 操作名 | 操作名 |
| description | varchar(64) | NO |  | - | 操作描述 | 操作描述 |
| type | int(11) | NO | -1 | - | 操作类型，导入:1，导出:0 | 操作类型，导入:1，导出:0 |
| clazz | varchar(255) | NO |  | - | 操作所在类 | 操作所在类 |
| method | varchar(64) | NO |  | - | 操作类的方法 | 操作类的方法 |
| parameterTypes | varchar(512) | NO |  | - | 方法参数类型 | 方法参数类型 |
| formHtml | text | YES | - | - | 额外表单内容 | 额外表单内容 |
| script | text | YES | - | - | 导入时的js，导出时的sql | 导入时的js，导出时的sql |
| columns | varchar(4096) | NO |  | - | 导出时的列 | 导出时的列 |
| empPower | varchar(4096) | NO |  | - | 员工权限 | 员工权限 |
| depPower | varchar(4096) | NO |  | - | 部门权限 | 部门权限 |
| state | bit(1) | NO | b'1' | - | 状态 | 状态 |
| effectiveFrom | datetime | NO | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(25) | NO |  | - | - | 业务含义待确认 |
| createTime | datetime | NO | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES |  | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 239 t_dictionary -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~4 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(16) | NO | - | PRI, auto_increment | - | 主键ID |
| dic_type_id | int(16) | NO | - | - | 字典类型id | 字典类型id |
| dic_type_name | varchar(32) | NO | - | - | 字典类型 | 字典类型 |
| dic_key | varchar(32) | NO | - | - | 字典key | 字典key |
| dic_value | varchar(32) | NO | - | - | 字典value | 字典value |
| customInfo | varchar(1024) | YES | - | - | 自定义属性 | 自定义属性 |
| sort | int(11) | NO | 0 | - | 排序 | 排序 |
| status | int(1) | YES | 1 | - | 有效标志（1-有效，0-无效） | 有效标志（1-有效，0-无效） |
| createTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| updateTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 240 t_down_log -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~33 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 文件下载日志 | 文件下载日志 |
| fileIds | varchar(100) | YES | - | - | 文件对应ID | 文件对应ID |
| ip | varchar(25) | YES | - | - | 请求的IP地址 | 请求的IP地址 |
| timeline | int(11) | YES | - | - | 时间戳 | 时间戳 |
| downloadTime | datetime | YES | - | - | 下载时间 | 下载时间 |
| user | varchar(25) | YES | - | - | 用户名 | 用户名 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 241 t_file -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~291 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 系统上传文件描述 | 系统上传文件描述 |
| typeId | int(11) | YES | - | - | 对应file_type表的主键 | 对应file_type表的主键 |
| name | varchar(255) | YES | - | - | 文件名称 | 文件名称 |
| path | varchar(500) | YES | - | - | 文件存储路径 | 文件存储路径 |
| ext | varchar(50) | YES | - | - | 文件名后缀 | 文件名后缀 |
| size | int(11) | YES | - | - | 文件大小 | 文件大小 |
| createTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| createBy | varchar(15) | YES | - | - | - | 业务含义待确认 |
| downloadKey | varchar(255) | YES | - | - | - | 业务含义待确认 |
| dataType | varchar(64) | YES | - | - | 关联数据类型 | 关联数据类型 |
| dataId | int(11) | YES | - | - | 关联数据ID | 关联数据ID |
| customInfo | json | YES | - | - | 自定义信息 | 自定义信息 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 242 t_file_type -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~9 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 根据系统业务类型划分上传的文件分类 | 根据系统业务类型划分上传的文件分类 |
| name | varchar(25) | YES | - | - | 分类名称 | 分类名称 |
| limitSize | int(11) | YES | - | - | 大小限制 | 大小限制 |
| allowType | varchar(255) | YES | - | - | 文件类型限制 | 文件类型限制 |
| rename | tinyint(1) | YES | - | - | 是否进行重命名 | 是否进行重命名 |
| cut | tinyint(1) | YES | - | - | 是否进行压缩 | 是否进行压缩 |
| thumbnail | tinyint(1) | YES | - | - | 是否生成缩略图 | 是否生成缩略图 |
| dir | varchar(255) | YES | - | - | 服务器保存的相对路径 | 服务器保存的相对路径 |
| uploadUrl | varchar(255) | YES | - | - | 文件上传URL | 文件上传URL |
| code | varchar(64) | YES | - | - | 前端或后端调用代码 | 前端或后端调用代码 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(10) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(10) | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 243 t_mails -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~10817 行 |
| 数据大小 | 32.02 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| subject | varchar(255) | NO |  | - | 邮件主题 | 邮件主题 |
| content | text | NO | - | - | 邮件正文 | 邮件正文 |
| tos | text | YES | - | - | 邮件主送 | 邮件主送 |
| ccs | text | YES | - | - | 邮件抄送 | 邮件抄送 |
| bccs | text | YES | - | - | 邮件密送 | 邮件密送 |
| actualSendAddress | text | YES | - | - | 实际邮件发送地址 | 实际邮件发送地址 |
| attachFiles | text | YES | - | - | 邮件附件 以特殊符号间隔多个文件 | 邮件附件 以特殊符号间隔多个文件 |
| isInner | bit(1) | YES | b'0' | - | 是否为内部邮箱 | 是否为内部邮箱 |
| sendTime | datetime | YES | - | - | 邮件实际发送时间 | 邮件实际发送时间 |
| expectSendTime | datetime | YES | - | - | 邮件期望发送时间 | 邮件期望发送时间 |
| sendFlag | bit(1) | YES | b'0' | - | 邮件是否发送 1 为已发送 | 邮件是否发送 1 为已发送 |
| failedCount | int(2) | YES | 0 | - | 发送失败次数 | 发送失败次数 |
| createBy | varchar(25) | YES |  | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 244 t_menu -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~37 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 用户菜单定义 | 用户菜单定义 |
| pid | int(11) | NO | 0 | - | 父菜单ID | 父菜单ID |
| name | varchar(100) | YES |  | - | 菜单名称 | 菜单名称 |
| url | varchar(100) | YES |  | - | 超链接 | 超链接 |
| icon | varchar(64) | YES |  | - | 菜单对应的class样式，会影响菜单的显示效果 | 菜单对应的class样式，会影响菜单的显示效果 |
| sort | int(11) | YES | 0 | - | 子菜单排序 | 子菜单排序 |
| status | bit(1) | YES | b'1' | - | 是否有效，1：有效，0：失效 | 是否有效，1：有效，0：失效 |
| target | varchar(15) | YES | - | - | - | 业务含义待确认 |
| remark | varchar(255) | YES |  | - | 备注说明 | 备注说明 |
| create_by | varchar(25) | YES |  | - | - | 创建人 |
| crate_time | datetime | YES | - | - | - | 业务含义待确认 |
| update_by | varchar(25) | YES |  | - | - | 更新人 |
| update_time | datetime | YES | - | - | - | 更新时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 245 t_notify_template -- 消息模板（邮件、短信等）

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 消息模板（邮件、短信等） |
| 数据量 | ~11 行 |
| 数据大小 | 80.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| templateCode | varchar(64) | YES | - | UNI | - | 业务含义待确认 |
| subject | varchar(64) | YES | - | - | 主题 | 主题 |
| content | text | YES | - | - | 内容 | 内容 |
| createBy | varchar(45) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| updateBy | varchar(45) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | NO | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| templateCode | BTREE | UNIQUE | templateCode |

---

### 246 t_permission -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~115 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| permission_id | int(11) | NO | - | PRI, auto_increment | 权限ID | 权限ID |
| permission_name | varchar(100) | YES |  | MUL | 权限字符串 | 权限字符串 |
| create_by | varchar(25) | YES |  | - | - | 创建人 |
| create_time | datetime | YES | - | - | - | 创建时间 |
| update_by | varchar(25) | YES |  | - | - | 更新人 |
| update_time | datetime | YES | - | - | - | 更新时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| permission_name | BTREE | NON-UNIQUE | permission_name |
| PRIMARY | BTREE | UNIQUE | permission_id |

---

### 247 t_resource -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~36 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 系统资源需要的权限定义 | 系统资源需要的权限定义 |
| url | varchar(100) | YES | - | - | 资源请求地址 | 资源请求地址 |
| authc | varchar(255) | YES | - | - | 需要的权限控制 ， 类似于 authc,roles[admin],perms[admin:create] | 需要的权限控制 ， 类似于 authc,roles[admin],perms[admin:create] |
| priority | int(11) | YES | 0 | - | 访问资源权限排序，越低越往后排 | 访问资源权限排序，越低越往后排 |
| remark | varchar(255) | YES | - | - | 关于资源定义的备注说明 | 关于资源定义的备注说明 |
| status | int(11) | YES | 1 | - | 数据有效性0 失效 1 有效 | 数据有效性0 失效 1 有效 |
| create_by | varchar(25) | YES | - | - | - | 创建人 |
| create_time | datetime | YES | - | - | - | 创建时间 |
| update_by | varchar(25) | YES | - | - | - | 更新人 |
| update_time | datetime | YES | - | - | - | 更新时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 248 t_role -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~12 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| role_id | int(11) | NO | - | PRI, auto_increment | 角色ID | 角色ID |
| role_name | varchar(100) | YES |  | MUL | 角色名称 | 角色名称 |
| role_name_zn | varchar(100) | YES |  | - | 中文别名 | 中文别名 |
| home_page | varchar(100) | YES |  | - | 角色默认主页 | 角色默认主页 |
| priority | int(11) | YES | 100 | - | 角色优先级，默认100，优先级最高 | 角色优先级，默认100，优先级最高 |
| status | smallint(1) | YES | 1 | - | 角色有效性，1有效，0无效 | 角色有效性，1有效，0无效 |
| create_by | varchar(25) | YES |  | - | - | 创建人 |
| create_time | datetime | YES | - | - | - | 创建时间 |
| update_by | varchar(25) | YES |  | - | - | 更新人 |
| update_time | datetime | YES | - | - | - | 更新时间 |
| remark | varchar(255) | YES |  | - | 备注说明 | 备注说明 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | role_id |
| role_name | BTREE | NON-UNIQUE | role_name |

---

### 249 t_role_menu -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~122 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 角色菜单权限 | 角色菜单权限 |
| role_id | int(11) | YES | - | - | 角色ID | 角色ID |
| menu_id | int(11) | YES | - | - | 菜单ID | 菜单ID |
| create_time | datetime | YES | - | - | 创建时间 | 创建时间 |
| create_by | varchar(25) | YES |  | - | 创建用户 | 创建用户 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 250 t_role_permission -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~613 行 |
| 数据大小 | 48.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 角色-权限 一对多 | 角色-权限 一对多 |
| role_id | int(11) | YES | - | MUL | 角色ID | 角色ID |
| permission_id | int(11) | YES | - | MUL | 权限ID | 权限ID |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| permission_id | BTREE | NON-UNIQUE | permission_id |
| PRIMARY | BTREE | UNIQUE | id |
| role_id | BTREE | UNIQUE | role_id,permission_id |

---

### 251 t_sync_log -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~7023 行 |
| 数据大小 | 2.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| targetMethod | varchar(255) | YES | - | - | 同步触发方法 | 同步触发方法 |
| tableObject | varchar(64) | NO | - | - | 同步的表实体 | 同步的表实体 |
| dataFrom | varchar(50) | YES | - | - | 同步数据源 | 同步数据源 |
| dataTo | varchar(50) | YES | - | - | 同步目标数据源 | 同步目标数据源 |
| syncParams | varchar(2048) | YES | - | - | 增量同步时的参数 | 增量同步时的参数 |
| syncStartTime | datetime | YES | - | - | 同步开始时间 | 同步开始时间 |
| syncEndTime | datetime | YES | CURRENT_TIMESTAMP | - | 同步结束时间 | 同步结束时间 |
| isSuccess | tinyint(1) | NO | 0 | - | 同步成功与否 | 同步成功与否 |
| dataCount | int(11) | YES | 0 | - | 同步记录数 | 同步记录数 |
| exception | text | YES | - | - | 同步失败异常信息 | 同步失败异常信息 |
| syncType | smallint(1) | NO | 0 | - | 0：复制，1：全量，2：增量 | 0：复制，1：全量，2：增量 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 252 t_sync_state -- 保存增量同步时的状态，上一次同步时间，id，或者记录数

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 保存增量同步时的状态，上一次同步时间，id，或者记录数 |
| 数据量 | ~5 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| tableObject | varchar(25) | NO | - | UNI | 表的对象 | 表的对象 |
| lastId | varchar(25) | NO |  | - | 上一次同步的最后一个主键 | 上一次同步的最后一个主键 |
| lastSyncTime | datetime | YES | - | - | 上一次同步时间 | 上一次同步时间 |
| offset | int(11) | NO | 0 | - | 上一次同步的记录数 | 上一次同步的记录数 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| tableObject | BTREE | UNIQUE | tableObject |

---

### 253 t_sys_log -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~61733 行 |
| 数据大小 | 354.23 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(32) unsigned | NO | - | PRI, auto_increment | - | 主键ID |
| description | varchar(8000) | YES | - | MUL | 日志描述 | 日志描述 |
| method | varchar(200) | YES | - | - | 调用的方法 | 调用的方法 |
| type | varchar(25) | YES | - | MUL | 0-正常日志，1-异常 | 0-正常日志，1-异常 |
| request_ip | varchar(256) | YES | - | - | 请求者IP | 请求者IP |
| exception_code | varchar(256) | YES | - | - | 异常编码 | 异常编码 |
| exception_detail | mediumtext | YES | - | - | 异常详情 | 异常详情 |
| params | mediumtext | YES | - | - | 参数（json格式） | 参数（json格式） |
| create_by | varchar(256) | YES | - | MUL | 操作人 | 操作人 |
| create_date | datetime | YES | - | MUL | 操作日期 | 操作日期 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| create_by | BTREE | NON-UNIQUE | create_by |
| create_date | BTREE | NON-UNIQUE | create_date,description |
| description | BTREE | NON-UNIQUE | description |
| PRIMARY | BTREE | UNIQUE | id |
| type | BTREE | NON-UNIQUE | type,create_by |

---

### 254 t_sys_variable -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~51 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | MUL, auto_increment | - | 主键ID |
| code | varchar(64) | NO | - | PRI | 系统参数编码 | 系统参数编码 |
| var | varchar(4096) | YES | - | - | 系统参数值 | 系统参数值 |
| remark | varchar(255) | YES | - | - | 备注 | 备注 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | CURRENT_TIMESTAMP | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| id | BTREE | NON-UNIQUE | id |
| PRIMARY | BTREE | UNIQUE | code |

---

### 255 t_user -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~189 行 |
| 数据大小 | 48.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| user_id | int(11) | NO | - | PRI, auto_increment | 用户ID | 用户ID |
| user_name | varchar(25) | YES |  | UNI | 用户名称 | 用户名称 |
| password | varchar(100) | YES | - | - | 密码 | 密码 |
| create_by | varchar(25) | YES |  | - | - | 创建人 |
| create_time | datetime | NO | CURRENT_TIMESTAMP | - | - | 创建时间 |
| update_by | varchar(25) | YES |  | - | - | 更新人 |
| update_time | datetime | YES | - | - | - | 更新时间 |
| status | smallint(1) | NO | 1 | - | 用户状态，0：失效，1有效，2：锁定 | 用户状态，0：失效，1有效，2：锁定 |
| needChangePwd | bit(1) | NO | b'1' | - | 用户创建后需要修改密码判断 | 用户创建后需要修改密码判断 |
| loginErrorCount | int(1) | NO | 0 | - | 用户密码输入错误次数 | 用户密码输入错误次数 |
| isSysUser | smallint(1) | NO | 0 | - | 是否为系统用户,0为普通用户 | 是否为系统用户,0为普通用户 |
| userCustom1 | varchar(50) | YES |  | - | 用户自定义字段1 | 用户自定义字段1 |
| userCustom2 | varchar(50) | YES |  | - | 用户自定义字段2 | 用户自定义字段2 |
| userCustom3 | varchar(50) | YES |  | - | 用户自定义字段3 | 用户自定义字段3 |
| userCustom4 | int(11) | YES | 0 | - | 用户自定义字段4 | 用户自定义字段4 |
| userCustom5 | int(11) | YES | 0 | - | 用户自定义字段5 | 用户自定义字段5 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | user_id |
| unique_username | BTREE | UNIQUE | user_name |

---

### 256 t_user_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~189 行 |
| 数据大小 | 64.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 员工ID，外键 | 员工ID，外键 |
| workNo | varchar(25) | NO |  | MUL | 工号 | 工号 |
| realName | varchar(50) | NO |  | - | 姓名 | 姓名 |
| eName | varchar(50) | YES |  | - | 英文名 | 英文名 |
| compID | int(11) | NO | 0 | MUL | 公司ID | 公司ID |
| depID | int(11) | NO | 0 | MUL | 部门ID | 部门ID |
| jobID | int(11) | NO | 0 | MUL | 岗位ID | 岗位ID |
| reportTo | int(11) | YES | - | MUL | 直接上级 | 直接上级 |
| wfreportTo | int(11) | YES | - | MUL | 职能上级 | 职能上级 |
| empStatus | int(11) | NO | 1 | - | 员工状态，1：在职，2：离职 | 员工状态，1：在职，2：离职 |
| jobStatus | int(11) | YES | - | - | 岗位状态 | 岗位状态 |
| empType | int(11) | YES | - | - | 聘用类型：1：正式，3：实习生 | 聘用类型：1：正式，3：实习生 |
| sex | smallint(1) | YES | - | - | 性别：1：男，0：女 | 性别：1：男，0：女 |
| birthday | date | YES | - | - | 生日 | 生日 |
| email | varchar(50) | YES | - | - | 邮箱 | 邮箱 |
| mobile | varchar(50) | YES | - | - | 手机 | 手机 |
| telphone | varchar(50) | YES | - | - | 座机 | 座机 |
| avatar | varchar(500) | YES | - | - | 头像 | 头像 |
| remark | varchar(100) | YES | - | - | 备注 | 备注 |
| state | int(11) | YES | 1 | - | 状态 | 状态 |
| user_id | int(11) | YES | - | MUL | userId | userId |
| custom1 | int(11) | YES | - | - | 预留字段1 | 预留字段1 |
| custom2 | int(11) | YES | - | - | 预留字段2 | 预留字段2 |
| custom3 | varchar(50) | YES | - | - | 预留字段3 officeCode | 预留字段3 officeCode |
| custom4 | varchar(50) | YES | - | - | 预留字段4 projectTypes | 预留字段4 projectTypes |
| custom5 | varchar(4096) | YES | - | - | 预留字段5 areaPower | 预留字段5 areaPower |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| compID | BTREE | NON-UNIQUE | compID |
| depID | BTREE | NON-UNIQUE | depID |
| fk_userInfo_userId | BTREE | UNIQUE | user_id,compID |
| jobID | BTREE | NON-UNIQUE | jobID |
| PRIMARY | BTREE | UNIQUE | id |
| reportTo | BTREE | NON-UNIQUE | reportTo |
| wfreportTo | BTREE | NON-UNIQUE | wfreportTo |
| workNo | BTREE | NON-UNIQUE | workNo |

---

### 257 t_user_login_record -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~18952 行 |
| 数据大小 | 1.52 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| loginName | varchar(64) | YES | - | - | 登录用户名 | 登录用户名 |
| loginTime | datetime | NO | CURRENT_TIMESTAMP | - | 登录时间 | 登录时间 |
| loginIP | varchar(64) | YES | - | - | 登录IP | 登录IP |
| logoutTime | datetime | YES | - | - | 登出时间 | 登出时间 |
| logoutIP | varchar(64) | YES | - | - | 登出IP | 登出IP |
| loginSuccess | tinyint(1) | NO | 0 | - | 登录状态 | 登录状态 |
| logoutSuccess | tinyint(1) | YES | - | - | 登出状态 | 登出状态 |
| userId | int(11) | YES | - | - | user表id | user表id |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 258 t_user_role -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~551 行 |
| 数据大小 | 48.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 用户-角色  一对多 | 用户-角色  一对多 |
| user_id | int(11) | NO | - | MUL | 用户ID | 用户ID |
| role_id | int(11) | NO | - | MUL | 角色ID | 角色ID |
| comp_id | int(11) | YES | - | - | 公司ID | 公司ID |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| t_user_role_ibfk_2 | BTREE | NON-UNIQUE | role_id |
| unique_userId_roleId | BTREE | UNIQUE | user_id,role_id,comp_id |
| user_id | BTREE | NON-UNIQUE | user_id |

---

### 259 user -- 用户

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

### 260 user_info -- 用户

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

### 261 user_modules -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~23 行 |
| 数据大小 | 16.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| menuCode | varchar(50) | YES | - | - | - | 业务含义待确认 |
| menuName | varchar(50) | YES | - | - | - | 业务含义待确认 |
| menuLevel | int(11) | YES | - | - | - | 业务含义待确认 |
| superId | int(11) | YES | - | - | 父菜单ID | 父菜单ID |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |
| createBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 262 user_permissions -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~2744 行 |
| 数据大小 | 256.00 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | - | 主键ID |
| username | varchar(10) | YES | - | - | - | 业务含义待确认 |
| permissionKey | varchar(50) | YES | - | - | - | 业务含义待确认 |
| permissionValue | int(1) | YES | - | - | - | 业务含义待确认 |
| menuName | varchar(50) | YES | - | - | - | 业务含义待确认 |
| menuLevel | int(1) | YES | - | - | - | 业务含义待确认 |
| effectiveFrom | datetime | YES | - | - | - | 业务含义待确认 |
| effectiveTo | datetime | YES | - | - | - | 业务含义待确认 |
| createdBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| createdTime | datetime | YES | - | - | - | 业务含义待确认 |
| updateBy | varchar(25) | YES | - | - | - | 业务含义待确认 |
| updateTime | datetime | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 263 user_team -- 

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

### 264 view_warranty -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~2857552 行 |
| 数据大小 | 1.10 GB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| newId | bigint(20) | NO | - | PRI, auto_increment | - | 业务含义待确认 |
| id | bigint(20) | YES | - | - | - | 主键ID |
| barcode | varchar(50) | YES | - | MUL | - | 业务含义待确认 |
| comBarCode | varchar(50) | YES | - | MUL | - | 业务含义待确认 |
| old_warrantyEndTime | datetime | YES | - | - | - | 业务含义待确认 |
| warrantyEndTime | datetime | YES | - | MUL | - | 业务含义待确认 |
| old_diff | int(7) | YES | - | - | - | 业务含义待确认 |
| diff | int(7) | YES | - | MUL | - | 业务含义待确认 |
| warrantyStartTime | datetime | YES | - | - | - | 业务含义待确认 |
| old_warrantyStartTime | datetime | YES | - | - | - | 业务含义待确认 |
| item | varchar(16) | YES | - | MUL | - | 业务含义待确认 |
| describe_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| itemName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| gradeName | varchar(125) | YES | - | - | - | 业务含义待确认 |
| gradeCode | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| packdate | datetime | YES | - | - | - | 业务含义待确认 |
| contract_code | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| contract_type | int(11) | YES | - | - | - | 业务含义待确认 |
| contract_type_name | varchar(25) | YES | - | - | - | 业务含义待确认 |
| project_name | varchar(512) | YES | - | - | - | 业务含义待确认 |
| customer_name | varchar(512) | YES | - | - | - | 业务含义待确认 |
| office_code | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| office_name | varchar(25) | YES | - | - | - | 业务含义待确认 |
| marketCode | varchar(10) | YES | - | MUL | - | 业务含义待确认 |
| marketName | varchar(15) | YES | - | - | - | 业务含义待确认 |
| systemId | int(11) | YES | - | - | - | 业务含义待确认 |
| systemName | varchar(15) | YES | - | - | - | 业务含义待确认 |
| warranty | varchar(2) | YES | - | - | - | 业务含义待确认 |
| warrantyMonth | double | YES | - | - | - | 业务含义待确认 |
| barcode2 | varchar(50) | YES | - | MUL | 母子公司发货序列号对应关系 | 母子公司发货序列号对应关系 |
| item2 | varchar(16) | YES | - | - | 母子公司发货物料编码对应关系 | 母子公司发货物料编码对应关系 |
| describe_2 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| itemName2 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| agentName | varchar(500) | YES | - | - | 代理商名称 | 代理商名称 |
| finalCustomerName | varchar(255) | YES | - | - | 最终客户名称 | 最终客户名称 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barcode | BTREE | NON-UNIQUE | barcode |
| barcode2 | BTREE | NON-UNIQUE | barcode2 |
| comBarCode | BTREE | NON-UNIQUE | comBarCode |
| contract_barcode_IDX | BTREE | NON-UNIQUE | contract_code,barcode |
| contract_code | BTREE | NON-UNIQUE | contract_code |
| diff | BTREE | NON-UNIQUE | diff |
| gradeCode | BTREE | NON-UNIQUE | gradeCode |
| item | BTREE | NON-UNIQUE | item |
| marketCode_systemId | BTREE | NON-UNIQUE | marketCode,systemId |
| office_code_marketCode_systemId | BTREE | NON-UNIQUE | office_code,marketCode,systemId |
| PRIMARY | BTREE | UNIQUE | newId |
| warrantyEndTime | BTREE | NON-UNIQUE | warrantyEndTime |

---

### 265 view_warranty_contract_state -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~66447 行 |
| 数据大小 | 23.56 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contractNo | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| diff | decimal(23,0) | YES | - | - | - | 业务含义待确认 |
| warrantyStatusName | varchar(4) | YES | - | - | - | 业务含义待确认 |
| warrantyStartTime | datetime | YES | - | - | - | 业务含义待确认 |
| warrantyEndTime | datetime | YES | - | - | - | 业务含义待确认 |
| warrantyGrade | int(3) | YES | - | - | - | 业务含义待确认 |
| warrantyGradeStartTime | datetime | YES | - | - | - | 业务含义待确认 |
| warrantyGradeEndTime | datetime | YES | - | - | - | 业务含义待确认 |
| gradecodes | mediumtext | YES | - | - | - | 业务含义待确认 |
| gradenames | mediumtext | YES | - | - | - | 业务含义待确认 |
| gradedesc | mediumtext | YES | - | - | - | 业务含义待确认 |
| hasRenewal | int(1) | NO | 0 | - | - | 业务含义待确认 |
| renewalDesc | mediumtext | YES | - | - | - | 业务含义待确认 |
| hasLiscense | bigint(1) | YES | - | - | - | 业务含义待确认 |
| liscenseCodes | mediumtext | YES | - | - | - | 业务含义待确认 |
| liscenseDesc | mediumtext | YES | - | - | - | 业务含义待确认 |
| wafService | bigint(1) | YES | - | - | - | 业务含义待确认 |
| wafServiceStartTime | datetime | YES | - | - | - | 业务含义待确认 |
| wafServiceEndTime | datetime | YES | - | - | - | 业务含义待确认 |
| itemCode | mediumtext | YES | - | - | - | 业务含义待确认 |
| itemDesc | mediumtext | YES | - | - | - | 业务含义待确认 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | contractNo |

---

### 266 view_warranty_temp -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~771000 行 |
| 数据大小 | 350.13 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| newId | bigint(20) | NO | - | PRI, auto_increment | - | 业务含义待确认 |
| id | bigint(20) | YES | - | - | - | 主键ID |
| barcode | varchar(50) | YES | - | MUL | - | 业务含义待确认 |
| comBarCode | varchar(50) | YES | - | MUL | - | 业务含义待确认 |
| old_warrantyEndTime | datetime | YES | - | - | - | 业务含义待确认 |
| warrantyEndTime | datetime | YES | - | MUL | - | 业务含义待确认 |
| old_diff | int(7) | YES | - | - | - | 业务含义待确认 |
| diff | int(7) | YES | - | MUL | - | 业务含义待确认 |
| warrantyStartTime | datetime | YES | - | - | - | 业务含义待确认 |
| old_warrantyStartTime | datetime | YES | - | - | - | 业务含义待确认 |
| item | varchar(16) | YES | - | MUL | - | 业务含义待确认 |
| describe_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| itemName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| gradeName | varchar(125) | YES | - | - | - | 业务含义待确认 |
| gradeCode | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| packdate | datetime | YES | - | - | - | 业务含义待确认 |
| contract_code | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| contract_type | int(11) | YES | - | - | - | 业务含义待确认 |
| contract_type_name | varchar(25) | YES | - | - | - | 业务含义待确认 |
| project_name | varchar(512) | YES | - | - | - | 业务含义待确认 |
| customer_name | varchar(512) | YES | - | - | - | 业务含义待确认 |
| office_code | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| office_name | varchar(25) | YES | - | - | - | 业务含义待确认 |
| marketCode | varchar(10) | YES | - | MUL | - | 业务含义待确认 |
| marketName | varchar(15) | YES | - | - | - | 业务含义待确认 |
| systemId | int(11) | YES | - | - | - | 业务含义待确认 |
| systemName | varchar(15) | YES | - | - | - | 业务含义待确认 |
| warranty | varchar(2) | YES | - | - | - | 业务含义待确认 |
| warrantyMonth | double | YES | - | - | - | 业务含义待确认 |
| barcode2 | varchar(50) | YES | - | MUL | 母子公司发货序列号对应关系 | 母子公司发货序列号对应关系 |
| item2 | varchar(16) | YES | - | - | 母子公司发货物料编码对应关系 | 母子公司发货物料编码对应关系 |
| syncTime | datetime | YES | - | - | - | 业务含义待确认 |
| describe_2 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| itemName2 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| agentName | varchar(500) | YES | - | - | 代理商名称 | 代理商名称 |
| finalCustomerName | varchar(255) | YES | - | - | 最终客户名称 | 最终客户名称 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barcode | BTREE | NON-UNIQUE | barcode |
| barcode2 | BTREE | NON-UNIQUE | barcode2 |
| comBarCode | BTREE | NON-UNIQUE | comBarCode |
| contract_code | BTREE | NON-UNIQUE | contract_code |
| diff | BTREE | NON-UNIQUE | diff |
| gradeCode | BTREE | NON-UNIQUE | gradeCode |
| item | BTREE | NON-UNIQUE | item |
| marketCode_systemId | BTREE | NON-UNIQUE | marketCode,systemId |
| office_code_marketCode_systemId | BTREE | NON-UNIQUE | office_code,marketCode,systemId |
| PRIMARY | BTREE | UNIQUE | newId |
| warrantyEndTime | BTREE | NON-UNIQUE | warrantyEndTime |

---

### 267 view_warranty_with_presales -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
| 数据量 | ~2857552 行 |
| 数据大小 | 1.10 GB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| newId | bigint(20) | NO | - | PRI, auto_increment | - | 业务含义待确认 |
| id | bigint(20) | YES | - | - | - | 主键ID |
| barcode | varchar(50) | YES | - | MUL | - | 业务含义待确认 |
| comBarCode | varchar(50) | YES | - | MUL | - | 业务含义待确认 |
| old_warrantyEndTime | datetime | YES | - | - | - | 业务含义待确认 |
| warrantyEndTime | datetime | YES | - | MUL | - | 业务含义待确认 |
| old_diff | int(7) | YES | - | - | - | 业务含义待确认 |
| diff | int(7) | YES | - | MUL | - | 业务含义待确认 |
| warrantyStartTime | datetime | YES | - | - | - | 业务含义待确认 |
| old_warrantyStartTime | datetime | YES | - | - | - | 业务含义待确认 |
| item | varchar(16) | YES | - | MUL | - | 业务含义待确认 |
| describe_ | varchar(255) | YES | - | - | - | 业务含义待确认 |
| itemName | varchar(255) | YES | - | - | - | 业务含义待确认 |
| gradeName | varchar(125) | YES | - | - | - | 业务含义待确认 |
| gradeCode | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| packdate | datetime | YES | - | - | - | 业务含义待确认 |
| contract_code | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| contract_type | int(11) | YES | - | - | - | 业务含义待确认 |
| contract_type_name | varchar(25) | YES | - | - | - | 业务含义待确认 |
| project_name | varchar(512) | YES | - | - | - | 业务含义待确认 |
| customer_name | varchar(512) | YES | - | - | - | 业务含义待确认 |
| office_code | varchar(25) | YES | - | MUL | - | 业务含义待确认 |
| office_name | varchar(25) | YES | - | - | - | 业务含义待确认 |
| marketCode | varchar(10) | YES | - | MUL | - | 业务含义待确认 |
| marketName | varchar(15) | YES | - | - | - | 业务含义待确认 |
| systemId | int(11) | YES | - | - | - | 业务含义待确认 |
| systemName | varchar(15) | YES | - | - | - | 业务含义待确认 |
| warranty | varchar(2) | YES | - | - | - | 业务含义待确认 |
| warrantyMonth | double | YES | - | - | - | 业务含义待确认 |
| barcode2 | varchar(50) | YES | - | MUL | 母子公司发货序列号对应关系 | 母子公司发货序列号对应关系 |
| item2 | varchar(16) | YES | - | - | 母子公司发货物料编码对应关系 | 母子公司发货物料编码对应关系 |
| describe_2 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| itemName2 | varchar(255) | YES | - | - | - | 业务含义待确认 |
| agentName | varchar(500) | YES | - | - | 代理商名称 | 代理商名称 |
| finalCustomerName | varchar(255) | YES | - | - | 最终客户名称 | 最终客户名称 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barcode | BTREE | NON-UNIQUE | barcode |
| barcode2 | BTREE | NON-UNIQUE | barcode2 |
| comBarCode | BTREE | NON-UNIQUE | comBarCode |
| contract_code | BTREE | NON-UNIQUE | contract_code |
| diff | BTREE | NON-UNIQUE | diff |
| gradeCode | BTREE | NON-UNIQUE | gradeCode |
| item | BTREE | NON-UNIQUE | item |
| marketCode_systemId | BTREE | NON-UNIQUE | marketCode,systemId |
| office_code_marketCode_systemId | BTREE | NON-UNIQUE | office_code,marketCode,systemId |
| PRIMARY | BTREE | UNIQUE | newId |
| warrantyEndTime | BTREE | NON-UNIQUE | warrantyEndTime |

---

### 268 warehouse -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
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

### 269 warehouse_info -- 

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

### 270 warehouse_info_detail -- 

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

### 271 warranty_change_logs -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
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

### 272 warranty_info -- 

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

### 273 workflow_info -- 

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 业务含义待确认 |
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
