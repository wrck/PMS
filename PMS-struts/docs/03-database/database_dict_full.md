# dppms_d365 数据库完整数据字典

> 生成时间：2026-06-13 | 数据库：dppms_d365 | 包含所有表和视图

---

## 目录

- 第一章 历史迁移与引擎 (77 个对象)
- 第二章 系统支撑 (62 个对象)
- 第三章 视图 (39 个对象)
- 第四章 项目管理 (123 个对象)

---

# 第一章 历史迁移与引擎

### 1.1 act_evt_log

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~6,398 行 |
| 数据大小 | 17.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| LOG_NR_ | bigint(20) | NO | - | PRI, AUTO_INCREMENT |  |  |
| TYPE_ | varchar(64) | YES | - |  |  |  |
| PROC_DEF_ID_ | varchar(64) | YES | - |  |  |  |
| PROC_INST_ID_ | varchar(64) | YES | - |  |  |  |
| EXECUTION_ID_ | varchar(64) | YES | - |  |  |  |
| TASK_ID_ | varchar(64) | YES | - |  |  |  |
| TIME_STAMP_ | timestamp(3) | NO | CURRENT_TIMESTAMP(3) |  |  |  |
| USER_ID_ | varchar(255) | YES | - |  |  |  |
| DATA_ | longblob | YES | - |  |  |  |
| LOCK_OWNER_ | varchar(255) | YES | - |  |  |  |
| LOCK_TIME_ | timestamp(3) | YES | - |  |  |  |
| IS_PROCESSED_ | tinyint(4) | YES | 0 |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | LOG_NR_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | LOG_NR_ |

---

### 1.2 act_ge_bytearray

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~1,210 行 |
| 数据大小 | 6.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO |  | PRI |  |  |
| REV_ | int(11) | YES | - |  |  |  |
| NAME_ | varchar(255) | YES | - |  |  |  |
| DEPLOYMENT_ID_ | varchar(64) | YES | - | MUL |  |  |
| BYTES_ | longblob | YES | - |  |  |  |
| GENERATED_ | tinyint(4) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| ACT_FK_BYTEARR_DEPL | FOREIGN KEY | DEPLOYMENT_ID_ | act_re_deployment | ID_ |
| PRIMARY | PRIMARY KEY | ID_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_FK_BYTEARR_DEPL | BTREE | NON-UNIQUE | DEPLOYMENT_ID_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

**外键列表**

| 外键名 | 本表字段 | 引用表 | 引用字段 |
|--------|----------|--------|----------|
| ACT_FK_BYTEARR_DEPL | DEPLOYMENT_ID_ | act_re_deployment | ID_ |

---

### 1.3 act_ge_property

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~3 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| NAME_ | varchar(64) | NO |  | PRI |  |  |
| VALUE_ | varchar(300) | YES | - |  |  |  |
| REV_ | int(11) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | NAME_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | NAME_ |

---

### 1.4 act_hi_actinst

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~155,827 行 |
| 数据大小 | 39.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  |  |
| PROC_DEF_ID_ | varchar(64) | NO | - |  |  |  |
| PROC_INST_ID_ | varchar(64) | NO | - | MUL |  |  |
| EXECUTION_ID_ | varchar(64) | NO | - | MUL |  |  |
| ACT_ID_ | varchar(255) | NO | - |  |  |  |
| TASK_ID_ | varchar(64) | YES | - |  |  |  |
| CALL_PROC_INST_ID_ | varchar(64) | YES | - |  |  |  |
| ACT_NAME_ | varchar(255) | YES | - |  |  |  |
| ACT_TYPE_ | varchar(255) | NO | - |  |  |  |
| ASSIGNEE_ | varchar(255) | YES | - |  |  |  |
| DURATION_ | bigint(20) | YES | - |  |  |  |
| TENANT_ID_ | varchar(255) | YES |  |  |  |  |
| START_TIME_ | datetime(3) | NO | - |  |  |  |
| END_TIME_ | datetime(3) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | ID_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_IDX_HI_ACT_INST_EXEC | BTREE | NON-UNIQUE | EXECUTION_ID_, ACT_ID_ |
| ACT_IDX_HI_ACT_INST_PROCINST | BTREE | NON-UNIQUE | PROC_INST_ID_, ACT_ID_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 1.5 act_hi_attachment

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  |  |
| REV_ | int(11) | YES | - |  |  |  |
| USER_ID_ | varchar(255) | YES | - |  |  |  |
| NAME_ | varchar(255) | YES | - |  |  |  |
| DESCRIPTION_ | varchar(4000) | YES | - |  |  |  |
| TYPE_ | varchar(255) | YES | - |  |  |  |
| TASK_ID_ | varchar(64) | YES | - |  |  |  |
| PROC_INST_ID_ | varchar(64) | YES | - |  |  |  |
| URL_ | varchar(4000) | YES | - |  |  |  |
| CONTENT_ID_ | varchar(64) | YES | - |  |  |  |
| TIME_ | datetime(3) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | ID_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 1.6 act_hi_comment

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~66,552 行 |
| 数据大小 | 7.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  |  |
| TYPE_ | varchar(255) | YES | - |  |  |  |
| USER_ID_ | varchar(255) | YES | - |  |  |  |
| TASK_ID_ | varchar(64) | YES | - |  |  |  |
| PROC_INST_ID_ | varchar(64) | YES | - |  |  |  |
| ACTION_ | varchar(255) | YES | - |  |  |  |
| MESSAGE_ | varchar(4000) | YES | - |  |  |  |
| FULL_MSG_ | longblob | YES | - |  |  |  |
| TIME_ | datetime(3) | NO | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | ID_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 1.7 act_hi_detail

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据大小 | 80.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  |  |
| TYPE_ | varchar(255) | NO | - |  |  |  |
| PROC_INST_ID_ | varchar(64) | YES | - | MUL |  |  |
| EXECUTION_ID_ | varchar(64) | YES | - |  |  |  |
| TASK_ID_ | varchar(64) | YES | - | MUL |  |  |
| ACT_INST_ID_ | varchar(64) | YES | - | MUL |  |  |
| NAME_ | varchar(255) | NO | - | MUL |  |  |
| VAR_TYPE_ | varchar(255) | YES | - |  |  |  |
| REV_ | int(11) | YES | - |  |  |  |
| BYTEARRAY_ID_ | varchar(64) | YES | - |  |  |  |
| DOUBLE_ | double | YES | - |  |  |  |
| LONG_ | bigint(20) | YES | - |  |  |  |
| TEXT_ | varchar(4000) | YES | - |  |  |  |
| TEXT2_ | varchar(4000) | YES | - |  |  |  |
| TIME_ | datetime(3) | NO | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | ID_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_IDX_HI_DETAIL_ACT_INST | BTREE | NON-UNIQUE | ACT_INST_ID_ |
| ACT_IDX_HI_DETAIL_NAME | BTREE | NON-UNIQUE | NAME_ |
| ACT_IDX_HI_DETAIL_PROC_INST | BTREE | NON-UNIQUE | PROC_INST_ID_ |
| ACT_IDX_HI_DETAIL_TASK_ID | BTREE | NON-UNIQUE | TASK_ID_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 1.8 act_hi_identitylink

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~143,081 行 |
| 数据大小 | 21.1 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO |  | PRI |  |  |
| GROUP_ID_ | varchar(255) | YES | - |  |  |  |
| TYPE_ | varchar(255) | YES | - |  |  |  |
| USER_ID_ | varchar(255) | YES | - | MUL |  |  |
| TASK_ID_ | varchar(64) | YES | - | MUL |  |  |
| PROC_INST_ID_ | varchar(64) | YES | - | MUL |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | ID_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_IDX_HI_IDENT_LNK_PROCINST | BTREE | NON-UNIQUE | PROC_INST_ID_ |
| ACT_IDX_HI_IDENT_LNK_TASK | BTREE | NON-UNIQUE | TASK_ID_ |
| ACT_IDX_HI_IDENT_LNK_USER | BTREE | NON-UNIQUE | USER_ID_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 1.9 act_hi_procinst

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~18,833 行 |
| 数据大小 | 4.4 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  |  |
| PROC_INST_ID_ | varchar(64) | NO | - | UNI |  |  |
| BUSINESS_KEY_ | varchar(255) | YES | - | MUL |  |  |
| PROC_DEF_ID_ | varchar(64) | NO | - |  |  |  |
| DURATION_ | bigint(20) | YES | - |  |  |  |
| START_USER_ID_ | varchar(255) | YES | - |  |  |  |
| START_ACT_ID_ | varchar(255) | YES | - |  |  |  |
| END_ACT_ID_ | varchar(255) | YES | - |  |  |  |
| SUPER_PROCESS_INSTANCE_ID_ | varchar(64) | YES | - |  |  |  |
| DELETE_REASON_ | varchar(4000) | YES | - |  |  |  |
| TENANT_ID_ | varchar(255) | YES |  |  |  |  |
| START_TIME_ | datetime(3) | NO | - |  |  |  |
| END_TIME_ | datetime(3) | YES | - |  |  |  |
| NAME_ | varchar(255) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | ID_ | None | None |
| PROC_INST_ID_ | UNIQUE | PROC_INST_ID_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_IDX_HI_PRO_I_BUSKEY | BTREE | NON-UNIQUE | BUSINESS_KEY_ |
| PRIMARY | BTREE | UNIQUE | ID_ |
| PROC_INST_ID_ | BTREE | UNIQUE | PROC_INST_ID_ |

---

### 1.10 act_hi_taskinst

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~67,984 行 |
| 数据大小 | 13.0 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  |  |
| PROC_DEF_ID_ | varchar(64) | YES | - |  |  |  |
| TASK_DEF_KEY_ | varchar(255) | YES | - |  |  |  |
| PROC_INST_ID_ | varchar(64) | YES | - | MUL |  |  |
| EXECUTION_ID_ | varchar(64) | YES | - |  |  |  |
| NAME_ | varchar(255) | YES | - |  |  |  |
| PARENT_TASK_ID_ | varchar(64) | YES | - |  |  |  |
| DESCRIPTION_ | varchar(4000) | YES | - |  |  |  |
| OWNER_ | varchar(255) | YES | - |  |  |  |
| ASSIGNEE_ | varchar(255) | YES | - |  |  |  |
| DURATION_ | bigint(20) | YES | - |  |  |  |
| DELETE_REASON_ | varchar(4000) | YES | - |  |  |  |
| PRIORITY_ | int(11) | YES | - |  |  |  |
| FORM_KEY_ | varchar(255) | YES | - |  |  |  |
| CATEGORY_ | varchar(255) | YES | - |  |  |  |
| TENANT_ID_ | varchar(255) | YES |  |  |  |  |
| START_TIME_ | datetime(3) | NO | - |  |  |  |
| CLAIM_TIME_ | datetime(3) | YES | - |  |  |  |
| END_TIME_ | datetime(3) | YES | - |  |  |  |
| DUE_DATE_ | datetime(3) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | ID_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_IDX_HI_TASK_INST_PROCINST | BTREE | NON-UNIQUE | PROC_INST_ID_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 1.11 act_hi_varinst

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~204,674 行 |
| 数据大小 | 41.1 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  |  |
| PROC_INST_ID_ | varchar(64) | YES | - | MUL |  |  |
| EXECUTION_ID_ | varchar(64) | YES | - |  |  |  |
| TASK_ID_ | varchar(64) | YES | - | MUL |  |  |
| NAME_ | varchar(255) | NO | - | MUL |  |  |
| VAR_TYPE_ | varchar(100) | YES | - |  |  |  |
| REV_ | int(11) | YES | - |  |  |  |
| BYTEARRAY_ID_ | varchar(64) | YES | - |  |  |  |
| DOUBLE_ | double | YES | - |  |  |  |
| LONG_ | bigint(20) | YES | - |  |  |  |
| TEXT_ | varchar(4000) | YES | - |  |  |  |
| TEXT2_ | varchar(4000) | YES | - |  |  |  |
| CREATE_TIME_ | datetime(3) | YES | - |  |  |  |
| LAST_UPDATED_TIME_ | datetime(3) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | ID_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_IDX_HI_PROCVAR_NAME_TYPE | BTREE | NON-UNIQUE | NAME_, VAR_TYPE_ |
| ACT_IDX_HI_PROCVAR_PROC_INST | BTREE | NON-UNIQUE | PROC_INST_ID_ |
| ACT_IDX_HI_PROCVAR_TASK_ID | BTREE | NON-UNIQUE | TASK_ID_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 1.12 act_id_group

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~12 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO |  | PRI |  |  |
| REV_ | int(11) | YES | - |  |  |  |
| NAME_ | varchar(255) | YES | - |  |  |  |
| TYPE_ | varchar(255) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | ID_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 1.13 act_id_info

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO |  | PRI |  |  |
| REV_ | int(11) | YES | - |  |  |  |
| USER_ID_ | varchar(64) | YES | - |  |  |  |
| TYPE_ | varchar(64) | YES | - |  |  |  |
| KEY_ | varchar(255) | YES | - |  |  |  |
| VALUE_ | varchar(255) | YES | - |  |  |  |
| PASSWORD_ | longblob | YES | - |  |  |  |
| PARENT_ID_ | varchar(255) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | ID_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 1.14 act_id_membership

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~548 行 |
| 数据大小 | 64.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| USER_ID_ | varchar(64) | NO |  | PRI |  |  |
| GROUP_ID_ | varchar(64) | NO |  | PRI |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| ACT_FK_MEMB_GROUP | FOREIGN KEY | GROUP_ID_ | act_id_group | ID_ |
| ACT_FK_MEMB_USER | FOREIGN KEY | USER_ID_ | act_id_user | ID_ |
| PRIMARY | PRIMARY KEY | USER_ID_ | None | None |
| PRIMARY | PRIMARY KEY | GROUP_ID_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_FK_MEMB_GROUP | BTREE | NON-UNIQUE | GROUP_ID_ |
| PRIMARY | BTREE | UNIQUE | USER_ID_, GROUP_ID_ |

**外键列表**

| 外键名 | 本表字段 | 引用表 | 引用字段 |
|--------|----------|--------|----------|
| ACT_FK_MEMB_GROUP | GROUP_ID_ | act_id_group | ID_ |
| ACT_FK_MEMB_USER | USER_ID_ | act_id_user | ID_ |

---

### 1.15 act_id_user

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~201 行 |
| 数据大小 | 48.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO |  | PRI |  |  |
| REV_ | int(11) | YES | - |  |  |  |
| FIRST_ | varchar(255) | YES | - |  |  |  |
| LAST_ | varchar(255) | YES | - |  |  |  |
| EMAIL_ | varchar(255) | YES | - |  |  |  |
| PWD_ | varchar(255) | YES | - |  |  |  |
| PICTURE_ID_ | varchar(64) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | ID_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 1.16 act_procdef_info

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据大小 | 64.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  |  |
| PROC_DEF_ID_ | varchar(64) | NO | - | UNI |  |  |
| REV_ | int(11) | YES | - |  |  |  |
| INFO_JSON_ID_ | varchar(64) | YES | - | MUL |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| ACT_FK_INFO_JSON_BA | FOREIGN KEY | INFO_JSON_ID_ | act_ge_bytearray | ID_ |
| ACT_FK_INFO_PROCDEF | FOREIGN KEY | PROC_DEF_ID_ | act_re_procdef | ID_ |
| PRIMARY | PRIMARY KEY | ID_ | None | None |
| ACT_UNIQ_INFO_PROCDEF | UNIQUE | PROC_DEF_ID_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_FK_INFO_JSON_BA | BTREE | NON-UNIQUE | INFO_JSON_ID_ |
| ACT_IDX_INFO_PROCDEF | BTREE | NON-UNIQUE | PROC_DEF_ID_ |
| ACT_UNIQ_INFO_PROCDEF | BTREE | UNIQUE | PROC_DEF_ID_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

**外键列表**

| 外键名 | 本表字段 | 引用表 | 引用字段 |
|--------|----------|--------|----------|
| ACT_FK_INFO_JSON_BA | INFO_JSON_ID_ | act_ge_bytearray | ID_ |
| ACT_FK_INFO_PROCDEF | PROC_DEF_ID_ | act_re_procdef | ID_ |

---

### 1.17 act_re_deployment

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~27 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO |  | PRI |  |  |
| NAME_ | varchar(255) | YES | - |  |  |  |
| CATEGORY_ | varchar(255) | YES | - |  |  |  |
| TENANT_ID_ | varchar(255) | YES |  |  |  |  |
| DEPLOY_TIME_ | timestamp(3) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | ID_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 1.18 act_re_model

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~6 行 |
| 数据大小 | 64.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  |  |
| REV_ | int(11) | YES | - |  |  |  |
| NAME_ | varchar(255) | YES | - |  |  |  |
| KEY_ | varchar(255) | YES | - |  |  |  |
| CATEGORY_ | varchar(255) | YES | - |  |  |  |
| VERSION_ | int(11) | YES | - |  |  |  |
| META_INFO_ | varchar(4000) | YES | - |  |  |  |
| DEPLOYMENT_ID_ | varchar(64) | YES | - | MUL |  |  |
| EDITOR_SOURCE_VALUE_ID_ | varchar(64) | YES | - | MUL |  |  |
| EDITOR_SOURCE_EXTRA_VALUE_ID_ | varchar(64) | YES | - | MUL |  |  |
| TENANT_ID_ | varchar(255) | YES |  |  |  |  |
| CREATE_TIME_ | timestamp(3) | YES | - |  |  |  |
| LAST_UPDATE_TIME_ | timestamp(3) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| ACT_FK_MODEL_DEPLOYMENT | FOREIGN KEY | DEPLOYMENT_ID_ | act_re_deployment | ID_ |
| ACT_FK_MODEL_SOURCE | FOREIGN KEY | EDITOR_SOURCE_VALUE_ID_ | act_ge_bytearray | ID_ |
| ACT_FK_MODEL_SOURCE_EXTRA | FOREIGN KEY | EDITOR_SOURCE_EXTRA_VALUE_ID_ | act_ge_bytearray | ID_ |
| PRIMARY | PRIMARY KEY | ID_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_FK_MODEL_DEPLOYMENT | BTREE | NON-UNIQUE | DEPLOYMENT_ID_ |
| ACT_FK_MODEL_SOURCE | BTREE | NON-UNIQUE | EDITOR_SOURCE_VALUE_ID_ |
| ACT_FK_MODEL_SOURCE_EXTRA | BTREE | NON-UNIQUE | EDITOR_SOURCE_EXTRA_VALUE_ID_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

**外键列表**

| 外键名 | 本表字段 | 引用表 | 引用字段 |
|--------|----------|--------|----------|
| ACT_FK_MODEL_DEPLOYMENT | DEPLOYMENT_ID_ | act_re_deployment | ID_ |
| ACT_FK_MODEL_SOURCE | EDITOR_SOURCE_VALUE_ID_ | act_ge_bytearray | ID_ |
| ACT_FK_MODEL_SOURCE_EXTRA | EDITOR_SOURCE_EXTRA_VALUE_ID_ | act_ge_bytearray | ID_ |

---

### 1.19 act_re_procdef

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~27 行 |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  |  |
| REV_ | int(11) | YES | - |  |  |  |
| CATEGORY_ | varchar(255) | YES | - |  |  |  |
| NAME_ | varchar(255) | YES | - |  |  |  |
| KEY_ | varchar(255) | NO | - | MUL |  |  |
| VERSION_ | int(11) | NO | - |  |  |  |
| DEPLOYMENT_ID_ | varchar(64) | YES | - |  |  |  |
| RESOURCE_NAME_ | varchar(4000) | YES | - |  |  |  |
| DGRM_RESOURCE_NAME_ | varchar(4000) | YES | - |  |  |  |
| DESCRIPTION_ | varchar(4000) | YES | - |  |  |  |
| HAS_START_FORM_KEY_ | tinyint(4) | YES | - |  |  |  |
| SUSPENSION_STATE_ | int(11) | YES | - |  |  |  |
| TENANT_ID_ | varchar(255) | YES |  |  |  |  |
| HAS_GRAPHICAL_NOTATION_ | tinyint(4) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | ID_ | None | None |
| ACT_UNIQ_PROCDEF | UNIQUE | KEY_ | None | None |
| ACT_UNIQ_PROCDEF | UNIQUE | VERSION_ | None | None |
| ACT_UNIQ_PROCDEF | UNIQUE | TENANT_ID_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_UNIQ_PROCDEF | BTREE | UNIQUE | KEY_, VERSION_, TENANT_ID_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

---

### 1.20 act_ru_event_subscr

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据大小 | 48.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  |  |
| REV_ | int(11) | YES | - |  |  |  |
| EVENT_TYPE_ | varchar(255) | NO | - |  |  |  |
| EVENT_NAME_ | varchar(255) | YES | - |  |  |  |
| EXECUTION_ID_ | varchar(64) | YES | - | MUL |  |  |
| PROC_INST_ID_ | varchar(64) | YES | - |  |  |  |
| ACTIVITY_ID_ | varchar(64) | YES | - |  |  |  |
| CONFIGURATION_ | varchar(255) | YES | - | MUL |  |  |
| TENANT_ID_ | varchar(255) | YES |  |  |  |  |
| PROC_DEF_ID_ | varchar(64) | YES | - |  |  |  |
| CREATED_ | timestamp(3) | NO | CURRENT_TIMESTAMP(3) |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| ACT_FK_EVENT_EXEC | FOREIGN KEY | EXECUTION_ID_ | act_ru_execution | ID_ |
| PRIMARY | PRIMARY KEY | ID_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_FK_EVENT_EXEC | BTREE | NON-UNIQUE | EXECUTION_ID_ |
| ACT_IDX_EVENT_SUBSCR_CONFIG_ | BTREE | NON-UNIQUE | CONFIGURATION_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

**外键列表**

| 外键名 | 本表字段 | 引用表 | 引用字段 |
|--------|----------|--------|----------|
| ACT_FK_EVENT_EXEC | EXECUTION_ID_ | act_ru_execution | ID_ |

---

### 1.21 act_ru_execution

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~3,554 行 |
| 数据大小 | 1.1 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO |  | PRI |  |  |
| REV_ | int(11) | YES | - |  |  |  |
| PROC_INST_ID_ | varchar(64) | YES | - | MUL |  |  |
| BUSINESS_KEY_ | varchar(255) | YES | - | MUL |  |  |
| PARENT_ID_ | varchar(64) | YES | - | MUL |  |  |
| PROC_DEF_ID_ | varchar(64) | YES | - | MUL |  |  |
| SUPER_EXEC_ | varchar(64) | YES | - | MUL |  |  |
| ACT_ID_ | varchar(255) | YES | - |  |  |  |
| IS_ACTIVE_ | tinyint(4) | YES | - |  |  |  |
| IS_CONCURRENT_ | tinyint(4) | YES | - |  |  |  |
| IS_SCOPE_ | tinyint(4) | YES | - |  |  |  |
| IS_EVENT_SCOPE_ | tinyint(4) | YES | - |  |  |  |
| SUSPENSION_STATE_ | int(11) | YES | - |  |  |  |
| CACHED_ENT_STATE_ | int(11) | YES | - |  |  |  |
| TENANT_ID_ | varchar(255) | YES |  |  |  |  |
| NAME_ | varchar(255) | YES | - |  |  |  |
| LOCK_TIME_ | timestamp(3) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| ACT_FK_EXE_PARENT | FOREIGN KEY | PARENT_ID_ | act_ru_execution | ID_ |
| ACT_FK_EXE_PROCDEF | FOREIGN KEY | PROC_DEF_ID_ | act_re_procdef | ID_ |
| ACT_FK_EXE_PROCINST | FOREIGN KEY | PROC_INST_ID_ | act_ru_execution | ID_ |
| ACT_FK_EXE_SUPER | FOREIGN KEY | SUPER_EXEC_ | act_ru_execution | ID_ |
| PRIMARY | PRIMARY KEY | ID_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_FK_EXE_PARENT | BTREE | NON-UNIQUE | PARENT_ID_ |
| ACT_FK_EXE_PROCDEF | BTREE | NON-UNIQUE | PROC_DEF_ID_ |
| ACT_FK_EXE_PROCINST | BTREE | NON-UNIQUE | PROC_INST_ID_ |
| ACT_FK_EXE_SUPER | BTREE | NON-UNIQUE | SUPER_EXEC_ |
| ACT_IDX_EXEC_BUSKEY | BTREE | NON-UNIQUE | BUSINESS_KEY_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

**外键列表**

| 外键名 | 本表字段 | 引用表 | 引用字段 |
|--------|----------|--------|----------|
| ACT_FK_EXE_PARENT | PARENT_ID_ | act_ru_execution | ID_ |
| ACT_FK_EXE_PROCDEF | PROC_DEF_ID_ | act_re_procdef | ID_ |
| ACT_FK_EXE_PROCINST | PROC_INST_ID_ | act_ru_execution | ID_ |
| ACT_FK_EXE_SUPER | SUPER_EXEC_ | act_ru_execution | ID_ |

---

### 1.22 act_ru_identitylink

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~23,219 行 |
| 数据大小 | 5.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO |  | PRI |  |  |
| REV_ | int(11) | YES | - |  |  |  |
| GROUP_ID_ | varchar(255) | YES | - | MUL |  |  |
| TYPE_ | varchar(255) | YES | - |  |  |  |
| USER_ID_ | varchar(255) | YES | - | MUL |  |  |
| TASK_ID_ | varchar(64) | YES | - | MUL |  |  |
| PROC_INST_ID_ | varchar(64) | YES | - | MUL |  |  |
| PROC_DEF_ID_ | varchar(64) | YES | - | MUL |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| ACT_FK_ATHRZ_PROCEDEF | FOREIGN KEY | PROC_DEF_ID_ | act_re_procdef | ID_ |
| ACT_FK_IDL_PROCINST | FOREIGN KEY | PROC_INST_ID_ | act_ru_execution | ID_ |
| ACT_FK_TSKASS_TASK | FOREIGN KEY | TASK_ID_ | act_ru_task | ID_ |
| PRIMARY | PRIMARY KEY | ID_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_FK_IDL_PROCINST | BTREE | NON-UNIQUE | PROC_INST_ID_ |
| ACT_FK_TSKASS_TASK | BTREE | NON-UNIQUE | TASK_ID_ |
| ACT_IDX_ATHRZ_PROCEDEF | BTREE | NON-UNIQUE | PROC_DEF_ID_ |
| ACT_IDX_IDENT_LNK_GROUP | BTREE | NON-UNIQUE | GROUP_ID_ |
| ACT_IDX_IDENT_LNK_USER | BTREE | NON-UNIQUE | USER_ID_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

**外键列表**

| 外键名 | 本表字段 | 引用表 | 引用字段 |
|--------|----------|--------|----------|
| ACT_FK_ATHRZ_PROCEDEF | PROC_DEF_ID_ | act_re_procdef | ID_ |
| ACT_FK_IDL_PROCINST | PROC_INST_ID_ | act_ru_execution | ID_ |
| ACT_FK_TSKASS_TASK | TASK_ID_ | act_ru_task | ID_ |

---

### 1.23 act_ru_job

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  |  |
| REV_ | int(11) | YES | - |  |  |  |
| TYPE_ | varchar(255) | NO | - |  |  |  |
| LOCK_OWNER_ | varchar(255) | YES | - |  |  |  |
| EXCLUSIVE_ | tinyint(1) | YES | - |  |  |  |
| EXECUTION_ID_ | varchar(64) | YES | - |  |  |  |
| PROCESS_INSTANCE_ID_ | varchar(64) | YES | - |  |  |  |
| PROC_DEF_ID_ | varchar(64) | YES | - |  |  |  |
| RETRIES_ | int(11) | YES | - |  |  |  |
| EXCEPTION_STACK_ID_ | varchar(64) | YES | - | MUL |  |  |
| EXCEPTION_MSG_ | varchar(4000) | YES | - |  |  |  |
| REPEAT_ | varchar(255) | YES | - |  |  |  |
| HANDLER_TYPE_ | varchar(255) | YES | - |  |  |  |
| HANDLER_CFG_ | varchar(4000) | YES | - |  |  |  |
| TENANT_ID_ | varchar(255) | YES |  |  |  |  |
| LOCK_EXP_TIME_ | timestamp(3) | YES | - |  |  |  |
| DUEDATE_ | timestamp(3) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| ACT_FK_JOB_EXCEPTION | FOREIGN KEY | EXCEPTION_STACK_ID_ | act_ge_bytearray | ID_ |
| PRIMARY | PRIMARY KEY | ID_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_FK_JOB_EXCEPTION | BTREE | NON-UNIQUE | EXCEPTION_STACK_ID_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

**外键列表**

| 外键名 | 本表字段 | 引用表 | 引用字段 |
|--------|----------|--------|----------|
| ACT_FK_JOB_EXCEPTION | EXCEPTION_STACK_ID_ | act_ge_bytearray | ID_ |

---

### 1.24 act_ru_task

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~3,400 行 |
| 数据大小 | 1024.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO |  | PRI |  |  |
| REV_ | int(11) | YES | - |  |  |  |
| EXECUTION_ID_ | varchar(64) | YES | - | MUL |  |  |
| PROC_INST_ID_ | varchar(64) | YES | - | MUL |  |  |
| PROC_DEF_ID_ | varchar(64) | YES | - | MUL |  |  |
| NAME_ | varchar(255) | YES | - |  |  |  |
| PARENT_TASK_ID_ | varchar(64) | YES | - |  |  |  |
| DESCRIPTION_ | varchar(4000) | YES | - |  |  |  |
| TASK_DEF_KEY_ | varchar(255) | YES | - |  |  |  |
| OWNER_ | varchar(255) | YES | - |  |  |  |
| ASSIGNEE_ | varchar(255) | YES | - | MUL |  |  |
| DELEGATION_ | varchar(64) | YES | - |  |  |  |
| PRIORITY_ | int(11) | YES | - |  |  |  |
| SUSPENSION_STATE_ | int(11) | YES | - |  |  |  |
| CATEGORY_ | varchar(255) | YES | - |  |  |  |
| TENANT_ID_ | varchar(255) | YES |  |  |  |  |
| CREATE_TIME_ | timestamp(3) | YES | - |  |  |  |
| DUE_DATE_ | datetime(3) | YES | - |  |  |  |
| FORM_KEY_ | varchar(255) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| ACT_FK_TASK_EXE | FOREIGN KEY | EXECUTION_ID_ | act_ru_execution | ID_ |
| ACT_FK_TASK_PROCDEF | FOREIGN KEY | PROC_DEF_ID_ | act_re_procdef | ID_ |
| ACT_FK_TASK_PROCINST | FOREIGN KEY | PROC_INST_ID_ | act_ru_execution | ID_ |
| PRIMARY | PRIMARY KEY | ID_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_FK_TASK_EXE | BTREE | NON-UNIQUE | EXECUTION_ID_ |
| ACT_FK_TASK_PROCDEF | BTREE | NON-UNIQUE | PROC_DEF_ID_ |
| ACT_FK_TASK_PROCINST | BTREE | NON-UNIQUE | PROC_INST_ID_ |
| ACT_IDX_ASSIGNEE | BTREE | NON-UNIQUE | ASSIGNEE_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

**外键列表**

| 外键名 | 本表字段 | 引用表 | 引用字段 |
|--------|----------|--------|----------|
| ACT_FK_TASK_EXE | EXECUTION_ID_ | act_ru_execution | ID_ |
| ACT_FK_TASK_PROCDEF | PROC_DEF_ID_ | act_re_procdef | ID_ |
| ACT_FK_TASK_PROCINST | PROC_INST_ID_ | act_ru_execution | ID_ |

---

### 1.25 act_ru_task_callback_task_w04649

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO |  |  |  |  |
| REV_ | int(11) | YES | - |  |  |  |
| EXECUTION_ID_ | varchar(64) | YES | - |  |  |  |
| PROC_INST_ID_ | varchar(64) | YES | - |  |  |  |
| PROC_DEF_ID_ | varchar(64) | YES | - |  |  |  |
| NAME_ | varchar(255) | YES | - |  |  |  |
| PARENT_TASK_ID_ | varchar(64) | YES | - |  |  |  |
| DESCRIPTION_ | varchar(4000) | YES | - |  |  |  |
| TASK_DEF_KEY_ | varchar(255) | YES | - |  |  |  |
| OWNER_ | varchar(255) | YES | - |  |  |  |
| ASSIGNEE_ | varchar(255) | YES | - |  |  |  |
| DELEGATION_ | varchar(64) | YES | - |  |  |  |
| PRIORITY_ | int(11) | YES | - |  |  |  |
| SUSPENSION_STATE_ | int(11) | YES | - |  |  |  |
| CATEGORY_ | varchar(255) | YES | - |  |  |  |
| TENANT_ID_ | varchar(255) | YES |  |  |  |  |
| CREATE_TIME_ | timestamp(3) | YES | - |  |  |  |
| DUE_DATE_ | datetime(3) | YES | - |  |  |  |
| FORM_KEY_ | varchar(255) | YES | - |  |  |  |
| varId | varchar(64) | YES | - |  |  |  |
| linkId | varchar(64) | YES |  |  |  |  |

---

### 1.26 act_ru_variable

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~42,152 行 |
| 数据大小 | 12.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID_ | varchar(64) | NO | - | PRI |  |  |
| REV_ | int(11) | YES | - |  |  |  |
| TYPE_ | varchar(255) | NO | - |  |  |  |
| NAME_ | varchar(255) | NO | - |  |  |  |
| EXECUTION_ID_ | varchar(64) | YES | - | MUL |  |  |
| PROC_INST_ID_ | varchar(64) | YES | - | MUL |  |  |
| TASK_ID_ | varchar(64) | YES | - | MUL |  |  |
| BYTEARRAY_ID_ | varchar(64) | YES | - | MUL |  |  |
| DOUBLE_ | double | YES | - |  |  |  |
| LONG_ | bigint(20) | YES | - |  |  |  |
| TEXT_ | varchar(4000) | YES | - |  |  |  |
| TEXT2_ | varchar(4000) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| ACT_FK_VAR_BYTEARRAY | FOREIGN KEY | BYTEARRAY_ID_ | act_ge_bytearray | ID_ |
| ACT_FK_VAR_EXE | FOREIGN KEY | EXECUTION_ID_ | act_ru_execution | ID_ |
| ACT_FK_VAR_PROCINST | FOREIGN KEY | PROC_INST_ID_ | act_ru_execution | ID_ |
| PRIMARY | PRIMARY KEY | ID_ | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ACT_FK_VAR_BYTEARRAY | BTREE | NON-UNIQUE | BYTEARRAY_ID_ |
| ACT_FK_VAR_EXE | BTREE | NON-UNIQUE | EXECUTION_ID_ |
| ACT_FK_VAR_PROCINST | BTREE | NON-UNIQUE | PROC_INST_ID_ |
| ACT_IDX_VARIABLE_TASK_ID | BTREE | NON-UNIQUE | TASK_ID_ |
| PRIMARY | BTREE | UNIQUE | ID_ |

**外键列表**

| 外键名 | 本表字段 | 引用表 | 引用字段 |
|--------|----------|--------|----------|
| ACT_FK_VAR_BYTEARRAY | BYTEARRAY_ID_ | act_ge_bytearray | ID_ |
| ACT_FK_VAR_EXE | EXECUTION_ID_ | act_ru_execution | ID_ |
| ACT_FK_VAR_PROCINST | PROC_INST_ID_ | act_ru_execution | ID_ |

---

### 1.27 af_industry_asset

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~658 行 |
| 数据大小 | 192.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| assetNum | varchar(255) | YES | - |  | 资产编号 | 资产编号 |
| assetName | varchar(255) | YES | - |  | 资产名称 | 资产名称 |
| assetCategory | varchar(25) | YES | - |  | 资产分类 | 资产分类 |
| assetType | varchar(25) | NO | - |  | 资产类型 | 资产类型 |
| assetHost | varchar(255) | YES | - |  | IP/URL地址/域名 | IP/URL地址/域名 |
| assetOpenPorts | varchar(255) | YES | - |  | 开放端口情况 | 开放端口情况 |
| assetDeployInfo | varchar(1024) | YES | - |  | 部署应用情况 | 部署应用情况 |
| assetUsage | varchar(255) | YES | - |  | 资产用途 | 资产用途 |
| customerName | varchar(255) | YES | - |  | 单位名称 | 单位名称 |
| industryCode | varchar(25) | YES | - |  | 所属行业 | 所属行业 |
| assetAS | varchar(25) | YES | - |  | 应用系统 | 应用系统 |
| assetASVersion | varchar(25) | YES | - |  | 应用系统版本号 | 应用系统版本号 |
| assetASIdentify | varchar(1024) | YES | - |  | 应用系统识别途径 | 应用系统识别途径 |
| assetASFramework | varchar(25) | YES | - |  | 应用系统架构 | 应用系统架构 |
| middlewareName | varchar(255) | YES | - |  | 中间件名称 | 中间件名称 |
| middlewareVersion | varchar(255) | YES | - |  | 中间件版本 | 中间件版本 |
| developerBrand | varchar(255) | YES | - |  | 开发商品牌 | 开发商品牌 |
| assetOS | varchar(25) | YES | - |  | 操作系统 | 操作系统 |
| assetOSVersion | varchar(255) | YES | - |  | 操作系统版本 | 操作系统版本 |
| assetDB | varchar(25) | YES | - |  | 数据库类型 | 数据库类型 |
| assetDBVersion | varchar(255) | YES | - |  | 数据库版本 | 数据库版本 |
| customInfo | json | YES | - |  | 自定义信息 | 自定义信息 |
| status | varchar(25) | YES | 0 |  | 状态 | 状态 |
| trackStatus | int(1) | YES | 0 |  | 入库状态 | 入库状态 |
| trackedTime | datetime | YES | - |  | 入库时间 | 入库时间 |
| disabled | bit(1) | YES | b'0' |  | 删除状态 | 删除状态 |
| createBy | varchar(25) | YES | - |  |  |  |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |
| updateBy | varchar(25) | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 1.28 af_industry_asset_leak_relation

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~23 行 |
| 数据大小 | 48.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| projectId | int(11) | NO | 0 | MUL | 项目ID | 项目ID |
| assetId | int(11) | NO | - | MUL | 资产ID | 资产ID |
| leakId | int(11) | NO | - |  | 漏洞ID | 漏洞ID |
| effectiveFrom | datetime | YES | - |  | 生效时间 | 生效时间 |
| effectiveTo | datetime | YES | - |  | 失效时间 | 失效时间 |
| disabled | bit(1) | NO | b'0' |  | 删除标准 | 删除标准 |
| createBy | varchar(25) | YES | - |  |  |  |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |
| updateBy | varchar(25) | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| assetId | BTREE | NON-UNIQUE | assetId |
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 1.29 af_industry_asset_project_relation

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~658 行 |
| 数据大小 | 96.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| projectId | int(11) | NO | - | MUL | 项目ID | 项目ID |
| assetId | int(11) | NO | - | MUL | 资产ID | 资产ID |
| effectiveFrom | datetime | YES | - |  | 生效时间 | 生效时间 |
| effectiveTo | datetime | YES | - |  | 失效时间 | 失效时间 |
| disabled | bit(1) | NO | b'0' |  | 删除标准 | 删除标准 |
| createBy | varchar(25) | YES | - |  |  |  |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |
| updateBy | varchar(25) | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| assetId | BTREE | NON-UNIQUE | assetId |
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 1.30 af_industry_leak

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~5 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| leakCode | varchar(255) | YES | - |  | 漏洞编号 | 漏洞编号 |
| leakName | varchar(255) | YES | - |  | 漏洞名称 | 漏洞名称 |
| leakType | varchar(25) | NO |  |  | 漏洞类型 | 漏洞类型 |
| leakLevel | varchar(25) | YES | - |  | 漏洞级别 | 漏洞级别 |
| leakDesc | varchar(1024) | YES | - |  | 漏洞描述 | 漏洞描述 |
| industryCode | varchar(25) | YES | - |  | 所属行业 | 所属行业 |
| leakSourceInfo | varchar(1024) | YES | - |  | 漏洞原始数据 | 漏洞原始数据 |
| remark | varchar(1024) | YES | - |  | 备注 | 备注 |
| status | varchar(25) | YES | 0 |  | 状态 | 状态 |
| trackStatus | int(1) | YES | 0 |  | 入库状态 | 入库状态 |
| trackedTime | datetime | YES | - |  | 入库时间 | 入库时间 |
| disabled | bit(1) | YES | b'0' |  | 删除状态 | 删除状态 |
| assetIds | varchar(255) | YES | - |  | 关联的资产ID | 关联的资产ID |
| customInfo | json | YES | - |  | 自定义信息 | 自定义信息 |
| createBy | varchar(25) | YES | - |  |  |  |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |
| updateBy | varchar(25) | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 1.31 af_industry_leak_warning

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~2 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| leakName | varchar(255) | YES | - |  | 漏洞名称 | 漏洞名称 |
| assetAS | varchar(25) | YES | - |  | 应用系统 | 应用系统 |
| assetASVersion | varchar(25) | YES | - |  | 应用系统版本号 | 应用系统版本号 |
| assetASIdentify | varchar(1024) | YES | - |  | 应用系统识别途径 | 应用系统识别途径 |
| assetASFramework | varchar(25) | YES | - |  | 应用系统架构 | 应用系统架构 |
| middlewareName | varchar(255) | YES | - |  | 中间件名称 | 中间件名称 |
| middlewareVersion | varchar(255) | YES | - |  | 中间件版本 | 中间件版本 |
| developerBrand | varchar(255) | YES | - |  | 开发商品牌 | 开发商品牌 |
| assetOS | varchar(25) | YES | - |  | 操作系统 | 操作系统 |
| assetOSVersion | varchar(255) | YES | - |  | 操作系统版本 | 操作系统版本 |
| assetDB | varchar(25) | YES | - |  | 数据库类型 | 数据库类型 |
| assetDBVersion | varchar(255) | YES | - |  | 数据库版本 | 数据库版本 |
| ports | varchar(255) | YES | - |  | 风险端口 | 风险端口 |
| customInfo | json | YES | - |  | 自定义信息 | 自定义信息 |
| status | int(3) | YES | - |  | 状态 | 状态 |
| trackStatus | int(1) | YES | 0 |  | 入库状态 | 入库状态 |
| trackedTime | datetime | YES | - |  | 入库时间 | 入库时间 |
| disabled | bit(1) | YES | b'0' |  | 删除状态 | 删除状态 |
| createBy | varchar(25) | YES | - |  |  |  |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |
| updateBy | varchar(25) | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 1.32 app_accessory_info

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~680 行 |
| 数据大小 | 144.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| sheetID | varchar(25) | YES | - |  | 流水号 | 流水号 |
| accessoryName | varchar(255) | YES | - |  | 附件名称 | 附件名称 |
| uploader | varchar(10) | YES | - |  | 上传者 | 上传者 |
| uploadTime | datetime | YES | - |  | 上传时间 | 上传时间 |
| accessoryType | int(11) | YES | - |  | 附件类型  1 发货信息  -1 坏件返回信息 | 附件类型  1 发货信息  -1 坏件返回信息 |
| accessoryPath | varchar(100) | YES | - |  | 上传路径 | 上传路径 |
| data_creater | varchar(10) | YES | - |  |  |  |
| data_creatime | datetime | YES | - |  |  |  |
| data_updater | varchar(10) | YES | - |  |  |  |
| data_updatime | datetime | YES | - |  |  |  |
| data_from | datetime | YES | - |  |  |  |
| data_to | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 1.33 app_comment

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~23,016 行 |
| 数据大小 | 2.0 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| sheetID | varchar(10) | YES | - | MUL | 单据代码 | 单据代码 |
| is_pass | varchar(2) | YES | - |  | 是否通过审批(0为未处理，1为通过，2为未通过 , -1 作废处理)  | 是否通过审批(0为未处理，1为通过，2为未通过 , -1 作废处理)  |
| opinion | text | YES | - |  | 意见 | 意见 |
| approve_time | datetime | YES | - |  | 审批时间 | 审批时间 |
| approver | varchar(10) | YES | - |  | 审批人 | 审批人 |
| state | char(1) | YES | - |  | (1:为最新审批结果；0：为旧审批结果) | (1:为最新审批结果；0：为旧审批结果) |
| take_place | varchar(15) | YES | 0 |  | 0:未选择 1:供应链 2：库存 | 0:未选择 1:供应链 2：库存 |
| isUnion | int(11) | YES | - |  | 是否联合供应链发货 | 是否联合供应链发货 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| sheetID | BTREE | NON-UNIQUE | sheetID |

---

### 1.34 app_spare_part

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~47,262 行 |
| 数据大小 | 8.0 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| tx_id | int(11) | YES | - | UNI |  |  |
| action_time | datetime | YES | - |  | 操作时间 | 操作时间 |
| isOK | char(1) | YES | - |  | 是否核销(是否核销，0为未核销，1为核销) | 是否核销(是否核销，0为未核销，1为核销) |
| hexiao_time | datetime | YES | - |  | 核销时间 | 核销时间 |
| hexiao_remark | text | YES | - |  | 核销说明 | 核销说明 |
| isNew | char(1) | YES | - |  | 数据状态(0:历史数据；1：最新数据 2:转移申请被转移状态) | 数据状态(0:历史数据；1：最新数据 2:转移申请被转移状态) |
| contract_sub_type | char(1) | YES | - |  | 发货类型（0为RMA ,1为项目保障,2为库存,null:转移申请 3:借用申请) | 发货类型（0为RMA ,1为项目保障,2为库存,null:转移申请 3:借用申请) |
| item_code | varchar(25) | YES | - |  | 物料号 | 物料号 |
| item_name | varchar(255) | YES | - |  | 物料名称 | 物料名称 |
| tain_process | varchar(255) | YES | - |  | 检测报告 | 检测报告 |
| isReceive | char(1) | YES | 0 |  | 0：已发货待确认接收 1：已确认接货 2：待发货确认 | 0：已发货待确认接收 1：已确认接货 2：待发货确认 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |
| tx_id | UNIQUE | tx_id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| tx_id | BTREE | UNIQUE | tx_id |

---

### 1.35 bar -- PPS设备

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | PPS设备 |
| 数据量 | ~1,349 行 |
| 数据大小 | 240.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(10) | NO | - | PRI, AUTO_INCREMENT | 主键 | 主键 |
| spare_id | int(10) | NO | - | MUL | pps的主键 | pps的主键 |
| bar_code | varchar(50) | YES | - |  | 设备编码 | 设备编码 |
| bar_model | varchar(1000) | YES | - |  | 设备型号 | 设备型号 |
| bar_num | varchar(50) | YES | - |  | 数量 | 数量 |
| remark | text | YES | - |  | 备注 | 备注 |
| serial_number | varchar(50) | YES | - |  | 序列号 | 序列号 |
| spare_code | varchar(15) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| spare_id | BTREE | NON-UNIQUE | spare_id |

---

### 1.36 brw_app_info

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~16,207 行 |
| 数据大小 | 4.9 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| sheetID | varchar(15) | YES | - | UNI | 单据代码 | 单据代码 |
| applicant | varchar(25) | YES | - |  | 申请人 | 申请人 |
| app_time | datetime | YES | - |  | 申请时间 | 申请时间 |
| app_dptNo | varchar(10) | YES | - |  | 申请办事处名称 | 申请办事处名称 |
| contractNo | varchar(25) | YES | - |  | 合同号 | 合同号 |
| prt_name | varchar(255) | YES | - |  | 项目名称 | 项目名称 |
| app_reason | text | YES | - |  | 申请原因 | 申请原因 |
| duty_person | varchar(10) | YES | - |  | 负责人 | 负责人 |
| start_use_time | datetime | YES | - |  | 开始使用时间 | 开始使用时间 |
| kept_place | varchar(10) | YES | - |  | 备件存放地 | 备件存放地 |
| promise_returntime | datetime | YES | - |  | 承诺备件归还时间 | 承诺备件归还时间 |
| extend_returntime | datetime | YES | - |  | 延长归还时间 | 延长归还时间 |
| demand_type | varchar(25) | YES | - |  | 需求类型（维护在sys_state_or_type） | 需求类型（维护在sys_state_or_type） |
| trade_classify | varchar(100) | YES | - |  | 行业分类（手动填写） | 行业分类（手动填写） |
| signing_state | char(1) | YES | - |  | 签单状态（0：已签单；1：未签单） 废弃字段 | 签单状态（0：已签单；1：未签单） 废弃字段 |
| app_type | char(1) | YES | - |  | 申请类型（0：借用申请；1：转移申请;2:历史数据） | 申请类型（0：借用申请；1：转移申请;2:历史数据） |
| addre_id | int(11) | YES | - |  | 关联收件人表ID | 关联收件人表ID |
| his_addre | varchar(64) | YES | - |  | 收件人 | 收件人 |
| his_addre_tel | varchar(64) | YES | - |  | 联系电话 | 联系电话 |
| his_addr | varchar(1024) | YES | - |  | 地址/where | 地址/where |
| his_zipCode | varchar(25) | YES | - |  | 邮编 | 邮编 |
| ischange_duty | char(1) | YES | - |  | 是否转移责任人（0:转移；1：不转移） | 是否转移责任人（0:转移；1：不转移） |
| isQuit | char(1) | YES | - |  | 是否为离职原因导致责任人变更，0：否，1：是 | 是否为离职原因导致责任人变更，0：否，1：是 |
| change_type | char(1) | YES | - |  | 转移类型 | 转移类型 |
| remark | text | YES | - |  | 备注 | 备注 |
| data_state | char(1) | YES | - |  | 数据状态（0：历史；1：最新） | 数据状态（0：历史；1：最新） |
| isSend | char(1) | YES | - |  | 是否发货(0:待发货确认 1：待收货确认) | 是否发货(0:待发货确认 1：待收货确认) |
| isReceive | char(1) | YES | 0 |  | 是否收货(1:已接受 0：未接受) | 是否收货(1:已接受 0：未接受) |
| beforeChange_sheetID | varchar(15) | YES | - |  | 发起转移申请之前的备件所涉及的单据号，此字段是为了保持备件经过转移流转后涉及单据号不变 | 发起转移申请之前的备件所涉及的单据号，此字段是为了保持备件经过转移流转后涉及单据号不变 |
| change_time | datetime | YES | - |  | 备件转移时间 | 备件转移时间 |
| version_no | int(11) | YES | 0 |  | 库存发货配置的版本号 | 库存发货配置的版本号 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |
| sheetID | UNIQUE | sheetID | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| sheetID | BTREE | UNIQUE | sheetID |

---

### 1.37 brw_spare_info

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~5,846 行 |
| 数据大小 | 1.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| sheetID | varchar(10) | YES | - | MUL | 单据代码 | 单据代码 |
| item_code | varchar(10) | YES | - |  | 物料编码 | 物料编码 |
| item_name | varchar(255) | YES | - |  | 物料名称 | 物料名称 |
| quantity | int(11) | YES | - |  | 数量 | 数量 |
| remark | text | YES | - |  | 备注 | 备注 |
| state | char(1) | YES | 1 |  | 状态（0：历史数据；1：有效数据） | 状态（0：历史数据；1：有效数据） |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| sheetID | BTREE | NON-UNIQUE | sheetID |

---

### 1.38 department

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~122 行 |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| ocrCode | varchar(25) | NO | - | MUL |  |  |
| ocrName | varchar(25) | NO | - |  |  |  |
| isparam | int(11) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| ocrCode | BTREE | NON-UNIQUE | ocrCode |
| PRIMARY | BTREE | UNIQUE | id |

---

### 1.39 dptech_v_project_product_config_level_info

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI |  |  |
| projectCode | varchar(100) | YES | - |  |  |  |
| itemGroup | decimal(23,10) | YES | - |  |  |  |
| itemCode | varchar(100) | YES | - |  |  |  |
| parentCode | varchar(1000) | YES | - |  |  |  |
| quantity | decimal(23,10) | YES | - |  |  |  |
| bomPaths | varchar(1000) | YES | - |  |  |  |
| itemModel | varchar(100) | YES | - |  |  |  |
| itemDesc | varchar(500) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 1.40 fb_contract

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~104,743 行 |
| 数据大小 | 52.7 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contract_id | varchar(64) | YES | - | MUL |  |  |
| contract_code | varchar(25) | YES | - | MUL |  |  |
| office_code | varchar(15) | YES | - | MUL |  |  |
| contract_type | int(11) | YES | - | MUL |  |  |
| customer_name | varchar(512) | YES | - |  |  |  |
| project_name | varchar(512) | YES | - |  |  |  |
| warranty | varchar(2) | YES | - |  |  |  |
| marketCode | varchar(10) | YES | - |  |  |  |
| marketName | varchar(15) | YES | - |  |  |  |
| systemId | int(11) | YES | - |  |  |  |
| systemName | varchar(15) | YES | - |  |  |  |
| remark | varchar(4096) | YES | - |  |  |  |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contract_code | BTREE | NON-UNIQUE | contract_code |
| contract_type | BTREE | NON-UNIQUE | contract_type |
| fb_contract_contract_id_IDX | BTREE | NON-UNIQUE | contract_id, office_code |
| office_code_IDX | BTREE | NON-UNIQUE | office_code, contract_id |

---

### 1.41 fb_ft_result1

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~193,752 行 |
| 数据大小 | 17.0 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| item_id | int(11) | YES | - | MUL |  |  |
| serial_number | varchar(100) | YES | - |  |  |  |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| item_id | BTREE | NON-UNIQUE | item_id |

---

### 1.42 fb_ft_result2

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~496,626 行 |
| 数据大小 | 316.4 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| result1_id | int(11) | YES | - | MUL |  |  |
| result_desc | text | YES | - |  |  |  |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| result1_id | BTREE | NON-UNIQUE | result1_id |

---

### 1.43 fb_items

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~32,188 行 |
| 数据大小 | 10.1 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | YES | - |  |  |  |
| item | varchar(25) | YES | - | MUL |  |  |
| describe_ | varchar(255) | YES | - |  |  |  |
| itemname | varchar(255) | YES | - | MUL |  |  |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| cover_index | BTREE | NON-UNIQUE | item, itemname, describe_ |
| itemname | BTREE | NON-UNIQUE | itemname |

---

### 1.44 fb_items2

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~19,357 行 |
| 数据大小 | 2.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | YES | - |  |  |  |
| item | varchar(15) | YES | - |  |  |  |
| describe_ | varchar(150) | YES | - |  |  |  |
| itemname | varchar(255) | YES | - |  |  |  |

---

### 1.45 fb_market_system

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~14 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| marketCode | varchar(10) | YES | - |  |  |  |
| marketName | varchar(15) | YES | - |  |  |  |
| systemId | int(11) | YES | - |  |  |  |
| systemName | varchar(15) | YES | - |  |  |  |

---

### 1.46 fb_office_relationship

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~659 行 |
| 数据大小 | 80.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 表记录历史数据中合同号与办事处的关系 | 表记录历史数据中合同号与办事处的关系 |
| contractNo | varchar(100) | YES | - | MUL | 合同号 | 合同号 |
| officeCode | varchar(25) | YES | - |  | 办事处编码 | 办事处编码 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | contractNo |
| PRIMARY | BTREE | UNIQUE | id |

---

### 1.47 fb_service

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~95,878 行 |
| 数据大小 | 31.1 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | varchar(64) | YES | - | MUL |  |  |
| con_xb | varchar(25) | YES | - | MUL |  |  |
| barcode | varchar(50) | YES | - | MUL |  |  |
| grade | varchar(15) | YES | - |  |  |  |
| begin_date | datetime | YES | - |  |  |  |
| end_date | datetime | YES | - |  |  |  |
| warranty | char(1) | YES | - |  |  |  |
| remark | text | YES | - |  |  |  |
| isyb | int(11) | YES | 1 |  | 1 延保 0 其他数据 | 1 延保 0 其他数据 |
| state | int(11) | YES | - |  | 针对多条续保，保留最新数据 | 针对多条续保，保留最新数据 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barcode_ | BTREE | NON-UNIQUE | barcode |
| con_xb | BTREE | NON-UNIQUE | con_xb |
| id | BTREE | NON-UNIQUE | id |

---

### 1.48 fb_shipment

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~140,962 行 |
| 数据大小 | 82.0 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| packlist_id | varchar(64) | YES | - | MUL |  |  |
| con_id | varchar(64) | YES | - | MUL |  |  |
| packdate | datetime | YES | - | MUL |  |  |
| warrantyStartTime | datetime | YES | - |  |  |  |
| warrantyEndTime | datetime | YES | - |  |  |  |
| receiveName | text | YES | - |  | 收件人 | 收件人 |
| emsNum | text | YES | - |  | 快递单号 | 快递单号 |
| emsCompany | text | YES | - |  | 快递公司 | 快递公司 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| fb_shipment_con_id_IDX | BTREE | NON-UNIQUE | con_id, packlist_id |
| fb_shipment_packdate_IDX | BTREE | NON-UNIQUE | packdate, con_id, packlist_id |
| fb_shipment_packlist_id_IDX | BTREE | NON-UNIQUE | packlist_id, con_id |

---

### 1.49 fb_shipment_barcode

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~3,541,100 行 |
| 数据大小 | 2981.1 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | bigint(20) | NO | - | PRI, AUTO_INCREMENT |  |  |
| pack_id | varchar(64) | YES | - | MUL |  |  |
| item | varchar(16) | YES | - | MUL |  |  |
| barcode | varchar(50) | YES | - | MUL |  |  |
| com_barcode | varchar(50) | YES | - |  |  |  |
| rma_no | varchar(64) | YES | - |  |  |  |
| isRMA | int(11) | YES | - |  | 标注是RMA替换添加的记录 | 标注是RMA替换添加的记录 |
| item2 | varchar(16) | YES | - | MUL | 母子公司发货物料编码对应关系 | 母子公司发货物料编码对应关系 |
| barcode2 | varchar(50) | YES | - | MUL | 母子公司发货序列号对应关系 | 母子公司发货序列号对应关系 |
| orderNumber | varchar(32) | YES | - |  | 订单号 | 订单号 |
| lineNum | int(11) | YES | - |  | 订单行号 | 订单行号 |
| profitCenter | varchar(32) | YES | - |  | 利润中心 | 利润中心 |
| soleAgentSuffix | varchar(32) | YES | - |  | 总代orderNumber后缀 | 总代orderNumber后缀 |
| warrantyStartDate | date | YES | - |  | 维保开始时间，为空默认装箱单发货日期+90天 | 维保开始时间，为空默认装箱单发货日期+90天 |
| warrantyMonth | int(11) | YES | - |  | 维保月数 | 维保月数 |
| rmaBarcode | varchar(50) | YES | - |  | RMA逆向序列号（维保替换的序列号） | RMA逆向序列号（维保替换的序列号） |
| updateTime | datetime | YES | - |  |  |  |
| syncTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |
| uuid | varchar(64) | YES | - | UNI |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |
| uuid | UNIQUE | uuid | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barcode2 | BTREE | NON-UNIQUE | barcode2 |
| barcode_pack_rma_IDX | BTREE | NON-UNIQUE | barcode, pack_id, rma_no |
| barcode_rma_pack_IDX | BTREE | NON-UNIQUE | barcode, rma_no, pack_id |
| item | BTREE | NON-UNIQUE | item |
| item2 | BTREE | NON-UNIQUE | item2 |
| pack_barcode_IDX | BTREE | NON-UNIQUE | pack_id, barcode, rma_no |
| pack_item_IDX | BTREE | NON-UNIQUE | pack_id, item, rma_no |
| PRIMARY | BTREE | UNIQUE | id |
| uuid | BTREE | UNIQUE | uuid |

---

### 1.50 fb_shipment_barcode_change_log

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~528,607 行 |
| 数据大小 | 667.0 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| logID | bigint(20) | NO | - | PRI, AUTO_INCREMENT |  |  |
| tableName | varchar(128) | YES | - |  |  |  |
| operation | varchar(50) | YES | - |  |  |  |
| changedBy | varchar(128) | YES | - |  |  |  |
| changeTime | datetime | YES | - |  |  |  |
| dataId | varchar(128) | YES | - | MUL |  |  |
| barCode | varchar(128) | YES | - |  |  |  |
| lasted | smallint(6) | YES | - | MUL |  |  |
| oldValues | longtext | YES | - |  |  |  |
| newValues | longtext | YES | - |  |  |  |
| syncTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |
| syncFlag | smallint(6) | YES | 0 | MUL | 同步标记，0待处理，1已处理，主要用于数据同步后更新发货记录使用 | 同步标记，0待处理，1已处理，主要用于数据同步后更新发货记录使用 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | logID | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| idx_dataid_lasted_logid | BTREE | NON-UNIQUE | dataId, lasted, logID |
| idx_lasted_dataid_logid | BTREE | NON-UNIQUE | lasted, dataId, logID |
| idx_syncFlag_lasted | BTREE | NON-UNIQUE | syncFlag, lasted |
| PRIMARY | BTREE | UNIQUE | logID |

---

### 1.51 fb_shipment_barcode_order_line

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~2,576,429 行 |
| 数据大小 | 940.7 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| pack_id | varchar(64) | YES | - | MUL |  |  |
| packlist_no | varchar(64) | YES | - |  |  |  |
| barcode | varchar(50) | YES | - | MUL |  |  |
| contractNo | varchar(50) | YES | - |  |  |  |
| orderNumber | varchar(32) | YES | - | MUL |  |  |
| lineNum | int(11) | YES | - |  |  |  |
| orderQty | int(11) | YES | - |  |  |  |
| deliveredQty | int(11) | YES | - |  |  |  |
| profitCenter | varchar(32) | YES | - |  | 利润中心 | 利润中心 |
| orderExecNumber | varchar(50) | YES | - |  | 执行单号 | 执行单号 |
| soleAgentSuffix | varchar(32) | YES | - |  | 总代orderNumber后缀 | 总代orderNumber后缀 |
| warrantyMonth | int(11) | YES | 0 |  | 维保月限 | 维保月限 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barcode | BTREE | NON-UNIQUE | barcode |
| orderNumber | BTREE | NON-UNIQUE | orderNumber, lineNum |
| pack_id | BTREE | NON-UNIQUE | pack_id, barcode |

---

### 1.52 fb_shipment_barcode_relation

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~55,130 行 |
| 数据大小 | 26.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI |  |  |
| sn1 | varchar(50) | YES | - | MUL |  |  |
| item1 | varchar(15) | YES | - | MUL |  |  |
| sn2 | varchar(50) | YES | - | MUL |  |  |
| item2 | varchar(15) | YES | - | MUL |  |  |
| contract | varchar(25) | YES | - | MUL |  |  |
| createtime | varchar(50) | YES | - |  |  |  |
| updatetime | varchar(50) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contract_sn1_IDX | BTREE | NON-UNIQUE | contract, sn1 |
| item1 | BTREE | NON-UNIQUE | item1 |
| item2 | BTREE | NON-UNIQUE | item2 |
| PRIMARY | BTREE | UNIQUE | id |
| sn1 | BTREE | NON-UNIQUE | sn1 |
| sn2 | BTREE | NON-UNIQUE | sn2 |

---

### 1.53 fb_soft_version

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~161,015 行 |
| 数据大小 | 45.2 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| serial_number | varchar(100) | YES | - | MUL |  |  |
| conp | varchar(100) | YES | - | MUL |  |  |
| cpld | varchar(100) | YES | - | MUL |  |  |
| boot | varchar(100) | YES | - | MUL |  |  |
| pcb | varchar(100) | YES | - | MUL |  |  |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| boot | BTREE | NON-UNIQUE | boot |
| conp | BTREE | NON-UNIQUE | conp |
| cpld | BTREE | NON-UNIQUE | cpld |
| pcb | BTREE | NON-UNIQUE | pcb |
| serial_number | BTREE | NON-UNIQUE | serial_number |

---

### 1.54 fb_warranty_grade

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~11 行 |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| gradecode | varchar(25) | YES | - | MUL |  |  |
| gradename | varchar(125) | YES | - |  |  |  |
| gradestatus | int(11) | YES | 0 |  |  |  |
| sort | int(3) | YES | 0 |  |  |  |
| effectiveFrom | datetime | YES | - |  |  |  |
| effectiveTo | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| gradecode | BTREE | NON-UNIQUE | gradecode |
| PRIMARY | BTREE | UNIQUE | id |

---

### 1.55 find_in_set_help

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~101 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI | where中FIND_IN_SET函数替代方法 | where中FIND_IN_SET函数替代方法 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 1.56 mes_oqc_info -- OQC检验记录

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | OQC检验记录 |
| 数据量 | ~1,411,475 行 |
| 数据大小 | 239.4 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| packNo | varchar(64) | YES | - |  | 装箱单号 | 装箱单号 |
| contractNo | varchar(64) | YES | - |  | 合同号 | 合同号 |
| itemCode | varchar(25) | YES | - |  | 物料号 | 物料号 |
| barcode | varchar(64) | YES | - | MUL | 设备序列号 | 设备序列号 |
| itemNo | varchar(25) | YES | - |  | 装箱销售明细行号 | 装箱销售明细行号 |
| workNo | varchar(25) | YES | - |  | 工号 | 工号 |
| inspectUser | varchar(25) | YES | - |  | 检验人 | 检验人 |
| inspectTime | datetime | YES | - |  | 检验时间 | 检验时间 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barcode | BTREE | NON-UNIQUE | barcode |
| PRIMARY | BTREE | UNIQUE | id |

---

### 1.57 mes_seal_info -- 印章登记表

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 印章登记表 |
| 数据量 | ~60 行 |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | varchar(255) | YES | - |  |  |  |
| name | varchar(255) | YES | - |  | 印章名称 | 印章名称 |
| info | varchar(255) | YES | - |  | 印记 | 印记 |
| description | varchar(255) | YES | - |  | 用途 | 用途 |
| user | varchar(255) | YES | - | MUL | 领用人 | 领用人 |
| takeTime | datetime | YES | - |  | 领用时间 | 领用时间 |
| backTime | datetime | YES | - |  | 归还时间 | 归还时间 |
| remark | varchar(255) | YES | - |  | 备注 | 备注 |
| uploadBy | varchar(255) | YES | - |  | 上传人 | 上传人 |
| uploadTime | datetime | YES | - |  | 上传时间 | 上传时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| user | BTREE | NON-UNIQUE | user |

---

### 1.58 rma_app_info

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~5,507 行 |
| 数据大小 | 2.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| sheetID | varchar(25) | NO | - | UNI | RMA申请单据代码 | RMA申请单据代码 |
| applicant | varchar(10) | YES | - |  | 申请发起人 | 申请发起人 |
| officeCode | varchar(25) | YES | - |  | 办事处或部门编码 | 办事处或部门编码 |
| customer_name | varchar(255) | YES | - |  | 客户名称 | 客户名称 |
| project_name | varchar(255) | YES | - |  | 项目名称 | 项目名称 |
| addreID | int(11) | YES | - |  | 收件人ID，关联addressee_info表 | 收件人ID，关联addressee_info表 |
| application_time | datetime | YES | - |  | 申请发起时间 | 申请发起时间 |
| back | varchar(10) | YES | - |  | 返回类型 | 返回类型 |
| tain | varchar(10) | YES | - |  | 维保类型 | 维保类型 |
| serve | varchar(10) | YES | - |  | 服务类型 | 服务类型 |
| duty_person | varchar(10) | YES | - |  | 负责人 | 负责人 |
| isSend | char(1) | YES | 0 |  | 申请备件状态（0：未发货；1：已发货 2：已接货） | 申请备件状态（0：未发货；1：已发货 2：已接货） |
| isReceive | char(1) | YES | 0 |  | 是否接收(0:未接受 1：已接收) | 是否接收(0:未接受 1：已接收) |
| take_place | char(1) | YES | 0 |  | 备件出处(0:未选择 1:供应链；2：库存) 此时的备件出处只是记录临时的状态，但也可算真实的状态，根据系统设定已不可更改，只需等审批确定后 | 备件出处(0:未选择 1:供应链；2：库存) 此时的备件出处只是记录临时的状态，但也可算真实的状态，根据系统设定已不可更改，只需等审批确定后 |
| isUnion | int(11) | YES | - |  | 是否联合供应链发货 | 是否联合供应链发货 |
| remark | text | YES | - |  | 备注 | 备注 |
| data_state | char(1) | YES | 0 |  | 数据状态（0：最新；1：历史数据） | 数据状态（0：最新；1：历史数据） |
| his_addre | varchar(64) | YES | - |  | 处理历史数据 | 处理历史数据 |
| his_zipCode | varchar(10) | YES | - |  | 处理历史数据 | 处理历史数据 |
| his_addr | varchar(1024) | YES | - |  | 处理历史数据 | 处理历史数据 |
| his_addre_tel | varchar(25) | YES | - |  | 处理历史数据 | 处理历史数据 |
| version_no | int(11) | YES | 0 |  | 发货配置版本号 | 发货配置版本号 |
| insteadState | int(11) | YES | 0 |  |  |  |
| rma_back_time | datetime | YES | - |  | 技服执行坏件返回时间 | 技服执行坏件返回时间 |
| rmaRoleIsPass | int(11) | YES | - |  | 故障审核是否通过 0否1是 | 故障审核是否通过 0否1是 |
| rmaRoleOpinion | varchar(255) | YES | - |  | 故障审核审批意见 | 故障审核审批意见 |
| rmaRoleAuditTime | datetime | YES | - |  | 故障审核时间 | 故障审核时间 |
| rmaRoleAuditUser | varchar(25) | YES | - |  | 故障审核用户 | 故障审核用户 |
| qaRoleIsPass | int(11) | YES | - |  | 质量审核是否通过 | 质量审核是否通过 |
| qaRoleOpinion | varchar(255) | YES | - |  | 质量审核审批意见 | 质量审核审批意见 |
| qaRoleAuditTime | datetime | YES | - |  | 质量审核时间 | 质量审核时间 |
| qaRoleAuditUser | varchar(25) | YES | - |  | 质量审核用户 | 质量审核用户 |
| insteadLicense | int(11) | YES | 0 |  | 授权License变更，1:需要，-1:不需要 | 授权License变更，1:需要，-1:不需要 |
| insteadLicenseMail | varchar(255) | YES | - |  | 授权License接收邮箱 | 授权License接收邮箱 |
| licenseMailTime | datetime | YES | - |  | 授权License邮件发送时间 | 授权License邮件发送时间 |
| customInfo | json | YES | - |  | 自定义信息 | 自定义信息 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |
| sheetID | UNIQUE | sheetID | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| sheetID | BTREE | UNIQUE | sheetID |

---

### 1.59 rma_applicant -- RMA申请

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | RMA申请 |
| 数据量 | ~858 行 |
| 数据大小 | 288.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| spare_code | varchar(8) | YES | - | UNI | 流水号 | 流水号 |
| product_code | varchar(50) | YES | - |  | 产品号 | 产品号 |
| product_name | varchar(200) | YES | - |  | 产品名称 | 产品名称 |
| username | varchar(50) | YES | - |  | 用户名称 | 用户名称 |
| project_name | varchar(200) | YES | - |  | 项目名称/申请原因 | 项目名称/申请原因 |
| old_bar_code | varchar(20) | YES | - |  | 旧设备序列号 | 旧设备序列号 |
| user_linkman | varchar(50) | YES | - |  | 用户联系人 | 用户联系人 |
| back_type | varchar(1) | YES | - |  | 返回类型('1'为"开坏箱",'2'为"开局坏",'3'为“网上运行坏”,'4'为"备件发货坏",'5'为"其他") | 返回类型('1'为"开坏箱",'2'为"开局坏",'3'为“网上运行坏”,'4'为"备件发货坏",'5'为"其他") |
| back_state | varchar(200) | YES | - |  | 返回类型说明 | 返回类型说明 |
| back_num | varchar(11) | YES | - |  | 返回数量 | 返回数量 |
| user_linkman_telephone | varchar(50) | YES | - |  | 用户联系人电话 | 用户联系人电话 |
| applicant_time | datetime | YES | - |  | 申请时间 | 申请时间 |
| problem_description | text | YES | - |  | 问题描述 | 问题描述 |
| analysis_process | varchar(1000) | YES | - |  | 现场分析过程(上传) | 现场分析过程(上传) |
| duty_person | varchar(50) | YES | - |  | 代理公司和负责人 | 代理公司和负责人 |
| start_first_time | varchar(50) | YES | - |  | 初次运行时间 | 初次运行时间 |
| problem_first_time | varchar(50) | YES | - |  | 故障发生时间 | 故障发生时间 |
| applicant_person | varchar(50) | YES | - |  | 申请人 | 申请人 |
| take_place | varchar(1) | YES | 1 |  | 取处(1为供应链部门，2为库存) | 取处(1为供应链部门，2为库存) |
| os_id | varchar(1000) | YES |  |  | 库存id | 库存id |
| address | text | YES | - |  | 地址 | 地址 |
| zip_code | varchar(50) | YES | - |  | 邮政编码 | 邮政编码 |
| tain_type | varchar(1) | YES | - |  | 维保类型(‘1’为“服务合同”,'2'为"项目订单") | 维保类型(‘1’为“服务合同”,'2'为"项目订单") |
| project_code | varchar(50) | YES | - |  | 项目订单号(针对维保类型为项目订单) | 项目订单号(针对维保类型为项目订单) |
| serve_type | varchar(1) | YES | - |  | 服务类型('1'为“坏件先退”,'2'为“好件先行”) | 服务类型('1'为“坏件先退”,'2'为“好件先行”) |
| remark | text | YES | - |  | 备注 | 备注 |
| isPass | varchar(1) | YES | 0 |  | 是否通过(0为未处理，1为通过，2为未通过) | 是否通过(0为未处理，1为通过，2为未通过) |
| isSend | varchar(1) | YES | 0 |  | 是否发货(0为未发货，1为已发货) | 是否发货(0为未发货，1为已发货) |
| rma_type | varchar(1) | YES | 0 |  | 备件类型(0为显示，1为不显示) | 备件类型(0为显示，1为不显示) |
| isNew | varchar(1) | YES | y |  |  |  |
| isChange_duty | varchar(1) | YES | n |  |  |  |
| opinion | text | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |
| id | UNIQUE | id | None | None |
| spare_code | UNIQUE | spare_code | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| id | BTREE | UNIQUE | id |
| PRIMARY | BTREE | UNIQUE | id |
| spare_code | BTREE | UNIQUE | spare_code |

---

### 1.60 rma_bar

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~1,454 行 |
| 数据大小 | 1.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(10) | NO | - | PRI, AUTO_INCREMENT |  |  |
| rma_id | int(10) | YES | - | MUL |  |  |
| old_bar_code | varchar(50) | YES | - |  |  |  |
| item_code | varchar(50) | YES | - |  |  |  |
| item_name | varchar(1000) | YES | - |  |  |  |
| project_code | varchar(50) | YES | - |  |  |  |
| project_name | varchar(50) | YES | - |  |  |  |
| problem_description | text | YES | - |  |  |  |
| back_state | varchar(50) | YES | - |  |  |  |
| start_first_time | varchar(50) | YES | - |  |  |  |
| problem_first_time | varchar(50) | YES | - |  |  |  |
| analysis_process | varchar(200) | YES | - |  |  |  |
| tain_process | varchar(200) | YES | - |  |  |  |
| isOK | varchar(1) | YES | 0 |  | 是否核销(0为未核销 1为已核销) | 是否核销(0为未核销 1为已核销) |
| hexiao_time | datetime | YES | - |  | 核销时间 | 核销时间 |
| isBack | varchar(1) | YES | 0 |  | 是否返回(0为未返回1为已返回) | 是否返回(0为未返回1为已返回) |
| back_time | datetime | YES | - |  | 返回时间 | 返回时间 |
| EMS | varchar(20) | YES |  |  | 快递单号 | 快递单号 |
| EMS_company | varchar(20) | YES |  |  | 快递公司 | 快递公司 |
| receive_person | varchar(10) | YES |  |  | 收件人 | 收件人 |
| back_type | varchar(50) | YES | - |  |  |  |
| tain_type | varchar(50) | YES | - |  |  |  |
| serve_type | varchar(50) | YES | - |  |  |  |
| spare_code | varchar(15) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| rma_id | BTREE | NON-UNIQUE | rma_id |

---

### 1.61 rma_info2mes_result

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~3,020 行 |
| 数据大小 | 6.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| sheetID | varchar(15) | NO | - | MUL | RMA申请流水号，多个合同号_n | RMA申请流水号，多个合同号_n |
| type | varchar(1) | NO | - |  | 接口上传结果，S、E | 接口上传结果，S、E |
| message | varchar(255) | NO | - |  | 上传结果信息 | 上传结果信息 |
| xmlStr | text | YES | - |  | 上传的xml：rmaInfoHeader | 上传的xml：rmaInfoHeader |
| xmlStr1 | text | YES | - |  | 上传的xml：rmaInfoDeatil | 上传的xml：rmaInfoDeatil |
| createTime | datetime | YES | - |  | 上传时间 | 上传时间 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| sheetID | BTREE | NON-UNIQUE | sheetID |

---

### 1.62 rma_repair_report_from_mes

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~2,166 行 |
| 数据大小 | 560.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| sheetId | varchar(25) | YES | - | MUL |  |  |
| barCode | varchar(50) | YES | - |  |  |  |
| contractNo | varchar(25) | YES | - |  |  |  |
| result | tinytext | YES | - |  |  |  |
| path | varchar(255) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| sheetId_where_index | BTREE | NON-UNIQUE | sheetId |

---

### 1.63 rma_spare_info

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~13,881 行 |
| 数据大小 | 4.8 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| tx_id | int(11) | YES | - | MUL | 交易号(关联application_transInfo表） | 交易号(关联application_transInfo表） |
| item_code | varchar(15) | YES | - |  | 物料号 | 物料号 |
| item_name | varchar(255) | YES | - |  | 物料名称 | 物料名称 |
| contractNo | varchar(25) | YES | - |  | 合同号 | 合同号 |
| contractRemark | varchar(4096) | YES | - |  | 合同备注 | 合同备注 |
| project_name | varchar(255) | YES | - |  | 项目名称 | 项目名称 |
| problem_desc | text | YES | - |  | 问题描述 | 问题描述 |
| first_working_time | varchar(25) | YES | - |  | 第一次运行时间 | 第一次运行时间 |
| conk_out_time | varchar(25) | YES | - |  | 故障发生时间 | 故障发生时间 |
| doa_path | varchar(100) | YES | - |  | doa故障分析单（下载路径） | doa故障分析单（下载路径） |
| check_path | varchar(100) | YES | - |  | 检测报告(下载路径) | 检测报告(下载路径) |
| repair_state | char(1) | YES | - |  | 维修状态（保留字段） | 维修状态（保留字段） |
| isBack | char(1) | YES | 0 |  | 坏件是否返回（0：未返回;1:已返回） | 坏件是否返回（0：未返回;1:已返回） |
| back_time | datetime | YES | - |  | 返回时间 | 返回时间 |
| isOK | char(1) | YES | 0 |  | 核销状态(0:未核销；1:已核销) | 核销状态(0:未核销；1:已核销) |
| hexiao_time | datetime | YES | - |  | 核销时间 | 核销时间 |
| analysis_state | int(11) | YES | - |  | 坏件故障分析状态  -1 未分析  1 已分析 | 坏件故障分析状态  -1 未分析  1 已分析 |
| insteadLicense | int(11) | YES | 0 |  | 授权License变更，1:需要，-1:不需要 | 授权License变更，1:需要，-1:不需要 |
| insteadLicenseMail | varchar(255) | YES | - |  | 授权License接收邮箱 | 授权License接收邮箱 |
| licenseMailTime | datetime | YES | - |  | 授权License邮件发送时间 | 授权License邮件发送时间 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| tx_id | BTREE | NON-UNIQUE | tx_id |

---

### 1.64 shipment_barcode_from_spms_unique

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~233,076 行 |
| 数据大小 | 17.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contract_code | varchar(25) | YES | - |  |  |  |
| barcode | varchar(50) | YES | - |  |  |  |
| itemCode | varchar(16) | YES | - |  |  |  |
| barcode2 | varchar(50) | YES | - |  |  |  |
| itemCode2 | varchar(16) | YES | - |  |  |  |
| rmaState | int(1) | NO | 0 |  |  |  |

---

### 1.65 spare_parts -- 备件 contract_sub_type(0RMA 1保障 2库存)

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 备件 contract_sub_type(0RMA 1保障 2库存) |
| 数据量 | ~6,282 行 |
| 数据大小 | 1.8 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(10) | NO | - | PRI, AUTO_INCREMENT |  |  |
| bar_code | varchar(25) | YES | - |  | 备件序列号 | 备件序列号 |
| spare_code | varchar(50) | YES | - | MUL | 流水号 | 流水号 |
| action_time | varchar(50) | NO | - |  | 操作时间 | 操作时间 |
| isOK | char(1) | YES | 0 |  | 设备状态(是否核销，0为未核销，1为核销) | 设备状态(是否核销，0为未核销，1为核销) |
| isNew | char(1) | YES | y |  | 数据状态(是否是最新的数据) | 数据状态(是否是最新的数据) |
| in_time | varchar(50) | YES | - |  | 收货时间 | 收货时间 |
| out_time | varchar(50) | YES | - |  | 发货时间 | 发货时间 |
| contract_sub_type | varchar(5) | YES | - |  | 类型(0为RMA ,1为项目保障,2为库存) | 类型(0为RMA ,1为项目保障,2为库存) |
| EMS | varchar(50) | YES | - |  | 快递单号 | 快递单号 |
| EMS_company | varchar(50) | YES | - |  | 快递公司 | 快递公司 |
| item_code | varchar(50) | YES | - |  | 物料号 | 物料号 |
| item_name | varchar(200) | YES | - |  | 物料名称 | 物料名称 |
| tain_process | varchar(200) | YES |  |  | 检测报告 | 检测报告 |
| isSure | varchar(1) | YES | 0 |  | 确认(1待确认,2以确认) | 确认(1待确认,2以确认) |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| spare_code | BTREE | NON-UNIQUE | spare_code |

---

### 1.66 spare_parts_applicant -- 项目保障备件申请

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目保障备件申请 |
| 数据量 | ~4,126 行 |
| 数据大小 | 1.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(10) | NO | - | PRI, AUTO_INCREMENT | 主键 | 主键 |
| applicant_person | varchar(50) | YES | - |  | 申请人 | 申请人 |
| applicant_time | datetime | YES | - |  | 申请时间 | 申请时间 |
| applicant_department | varchar(50) | YES | - |  | 申请部门 | 申请部门 |
| spare_code | varchar(50) | YES | - | UNI | 流水号 | 流水号 |
| applicant_reason | varchar(500) | YES | - |  | 申请原因 | 申请原因 |
| remark | text | YES | - |  | 备注 | 备注 |
| zip_code | varchar(50) | YES | - |  | 邮政编码 | 邮政编码 |
| isPass | varchar(1) | YES | 0 |  | 是否通过(0为未处理，1为通过，2为未通过) | 是否通过(0为未处理，1为通过，2为未通过) |
| isSend | varchar(1) | NO | 0 |  | 是否通过(0为未发货，1为已发货) | 是否通过(0为未发货，1为已发货) |
| address | varchar(200) | YES | - |  | 地址 | 地址 |
| receive_person | varchar(200) | YES | - |  | 收件人 | 收件人 |
| receive_person_tel | varchar(200) | YES | - |  | 收件人电话 | 收件人电话 |
| spare_parts_type | varchar(1) | YES | - |  | 备件类型(0为项目保障，1为库存) | 备件类型(0为项目保障，1为库存) |
| duty_person | varchar(10) | YES | - |  | 责任人 | 责任人 |
| applicant_type | varchar(1) | YES | 0 |  | 申请类型(0为普通申请，1为转移申请) | 申请类型(0为普通申请，1为转移申请) |
| isChange_duty | varchar(1) | YES | 1 |  | 转移类型(0为转移责任人，1为不转移责任人) | 转移类型(0为转移责任人，1为不转移责任人) |
| isQuit | char(1) | YES | - |  | 是否为离职原因导致责任人变更，0：否，1：是 | 是否为离职原因导致责任人变更，0：否，1：是 |
| isReceive | varchar(1) | YES | 0 |  | 是否收到(0为未收到，1为收到) | 是否收到(0为未收到，1为收到) |
| transfer_time | datetime | YES | - |  | 转移时间 | 转移时间 |
| applicant_project | varchar(255) | YES | - |  |  |  |
| start_time | date | YES | - |  |  |  |
| promise_returntime | date | YES | - |  |  |  |
| kept_place | varchar(255) | YES | - |  |  |  |
| beforeChange_spareCode | varchar(50) | YES | - |  |  |  |
| change_type | char(1) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |
| spare_code | UNIQUE | spare_code | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| spare_code | BTREE | UNIQUE | spare_code |

---

### 1.67 tx_info

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~60,939 行 |
| 数据大小 | 10.0 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| tx_id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| sheetID | varchar(10) | YES | - | MUL | 单据代码 | 单据代码 |
| tx_type | int(1) | YES | - |  | 单据类型(0:RMA单据;1:借用申请 2：转移) | 单据类型(0:RMA单据;1:借用申请 2：转移) |
| spare_serialNum | varchar(50) | YES | - |  | 备件序列号 | 备件序列号 |
| sendout_place | char(1) | YES | - |  | 历史记录（1：供应链；2：库存） | 历史记录（1：供应链；2：库存） |
| sendout_whsCode | varchar(10) | YES | - |  | 备件发出库房 | 备件发出库房 |
| send_time | datetime | YES | - |  | 出库时间 | 出库时间 |
| receving_place | varchar(50) | YES | - |  | 备件接受地 | 备件接受地 |
| receving_whsCode | varchar(10) | YES | - |  | 备件接收库房 | 备件接收库房 |
| receive_time | datetime | YES | - |  | 收货时间 | 收货时间 |
| quantity | int(11) | YES | 1 |  | 数量 | 数量 |
| EMS_num | varchar(255) | YES | - |  | 快递单号 | 快递单号 |
| EMS_company | varchar(255) | YES | - |  | 快递公司 | 快递公司 |
| addressee | varchar(25) | YES | - |  | 收件人 | 收件人 |
| isRMA | char(1) | YES | 0 |  | 是否是RMA的坏件返回（1：是;0:好件） | 是否是RMA的坏件返回（1：是;0:好件） |
| version_no | int(11) | YES | 0 |  | 版本号  -1时为历史选择的数据 | 版本号  -1时为历史选择的数据 |
| detail_id | int(11) | YES | - |  | 库存表中的id | 库存表中的id |
| instead_of_num | varchar(25) | YES | - |  | 好件替换坏件关系 | 好件替换坏件关系 |
| shiftimes | int(11) | YES | - |  | 备件经过转移次数 | 备件经过转移次数 |
| turnovertimes | int(11) | YES | - |  |  |  |
| allottimes | int(11) | YES | - |  |  |  |
| instead_time | datetime | YES | - |  |  |  |
| datastate | int(1) | YES | 1 |  | 保持历史数据有效性 0 失效 1 有效 | 保持历史数据有效性 0 失效 1 有效 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | tx_id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | tx_id |
| sheetID | BTREE | NON-UNIQUE | sheetID |

---

### 1.68 view_warranty

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | MyISAM |
| 数据量 | ~2,857,552 行 |
| 数据大小 | 1342.1 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| newId | bigint(20) | NO | - | PRI, AUTO_INCREMENT |  |  |
| id | bigint(20) | YES | - |  |  |  |
| barcode | varchar(50) | YES | - | MUL |  |  |
| comBarCode | varchar(50) | YES | - | MUL |  |  |
| old_warrantyEndTime | datetime | YES | - |  |  |  |
| warrantyEndTime | datetime | YES | - | MUL |  |  |
| old_diff | int(7) | YES | - |  |  |  |
| diff | int(7) | YES | - | MUL |  |  |
| warrantyStartTime | datetime | YES | - |  |  |  |
| old_warrantyStartTime | datetime | YES | - |  |  |  |
| item | varchar(16) | YES | - | MUL |  |  |
| describe_ | varchar(255) | YES | - |  |  |  |
| itemName | varchar(255) | YES | - |  |  |  |
| gradeName | varchar(125) | YES | - |  |  |  |
| gradeCode | varchar(25) | YES | - | MUL |  |  |
| packdate | datetime | YES | - |  |  |  |
| contract_code | varchar(25) | YES | - | MUL |  |  |
| contract_type | int(11) | YES | - |  |  |  |
| contract_type_name | varchar(25) | YES | - |  |  |  |
| project_name | varchar(512) | YES | - |  |  |  |
| customer_name | varchar(512) | YES | - |  |  |  |
| office_code | varchar(25) | YES | - | MUL |  |  |
| office_name | varchar(25) | YES | - |  |  |  |
| marketCode | varchar(10) | YES | - | MUL |  |  |
| marketName | varchar(15) | YES | - |  |  |  |
| systemId | int(11) | YES | - |  |  |  |
| systemName | varchar(15) | YES | - |  |  |  |
| warranty | varchar(2) | YES | - |  |  |  |
| warrantyMonth | double | YES | - |  |  |  |
| barcode2 | varchar(50) | YES | - | MUL | 母子公司发货序列号对应关系 | 母子公司发货序列号对应关系 |
| item2 | varchar(16) | YES | - |  | 母子公司发货物料编码对应关系 | 母子公司发货物料编码对应关系 |
| describe_2 | varchar(255) | YES | - |  |  |  |
| itemName2 | varchar(255) | YES | - |  |  |  |
| agentName | varchar(500) | YES | - |  | 代理商名称 | 代理商名称 |
| finalCustomerName | varchar(255) | YES | - |  | 最终客户名称 | 最终客户名称 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | newId | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barcode | BTREE | NON-UNIQUE | barcode |
| barcode2 | BTREE | NON-UNIQUE | barcode2 |
| comBarCode | BTREE | NON-UNIQUE | comBarCode |
| contract_barcode_IDX | BTREE | NON-UNIQUE | contract_code, barcode |
| contract_code | BTREE | NON-UNIQUE | contract_code |
| diff | BTREE | NON-UNIQUE | diff |
| gradeCode | BTREE | NON-UNIQUE | gradeCode |
| item | BTREE | NON-UNIQUE | item |
| marketCode_systemId | BTREE | NON-UNIQUE | marketCode, systemId |
| office_code_marketCode_systemId | BTREE | NON-UNIQUE | office_code, marketCode, systemId |
| PRIMARY | BTREE | UNIQUE | newId |
| warrantyEndTime | BTREE | NON-UNIQUE | warrantyEndTime |

---

### 1.69 view_warranty_contract_state

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~66,447 行 |
| 数据大小 | 26.1 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contractNo | varchar(25) | YES | - | MUL |  |  |
| diff | decimal(23,0) | YES | - |  |  |  |
| warrantyStatusName | varchar(4) | YES | - |  |  |  |
| warrantyStartTime | datetime | YES | - |  |  |  |
| warrantyEndTime | datetime | YES | - |  |  |  |
| warrantyGrade | int(3) | YES | - |  |  |  |
| warrantyGradeStartTime | datetime | YES | - |  |  |  |
| warrantyGradeEndTime | datetime | YES | - |  |  |  |
| gradecodes | mediumtext | YES | - |  |  |  |
| gradenames | mediumtext | YES | - |  |  |  |
| gradedesc | mediumtext | YES | - |  |  |  |
| hasRenewal | int(1) | NO | 0 |  |  |  |
| renewalDesc | mediumtext | YES | - |  |  |  |
| hasLiscense | bigint(1) | YES | - |  |  |  |
| liscenseCodes | mediumtext | YES | - |  |  |  |
| liscenseDesc | mediumtext | YES | - |  |  |  |
| wafService | bigint(1) | YES | - |  |  |  |
| wafServiceStartTime | datetime | YES | - |  |  |  |
| wafServiceEndTime | datetime | YES | - |  |  |  |
| itemCode | mediumtext | YES | - |  |  |  |
| itemDesc | mediumtext | YES | - |  |  |  |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | contractNo |

---

### 1.70 view_warranty_temp

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | MyISAM |
| 数据量 | ~771,000 行 |
| 数据大小 | 435.7 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| newId | bigint(20) | NO | - | PRI, AUTO_INCREMENT |  |  |
| id | bigint(20) | YES | - |  |  |  |
| barcode | varchar(50) | YES | - | MUL |  |  |
| comBarCode | varchar(50) | YES | - | MUL |  |  |
| old_warrantyEndTime | datetime | YES | - |  |  |  |
| warrantyEndTime | datetime | YES | - | MUL |  |  |
| old_diff | int(7) | YES | - |  |  |  |
| diff | int(7) | YES | - | MUL |  |  |
| warrantyStartTime | datetime | YES | - |  |  |  |
| old_warrantyStartTime | datetime | YES | - |  |  |  |
| item | varchar(16) | YES | - | MUL |  |  |
| describe_ | varchar(255) | YES | - |  |  |  |
| itemName | varchar(255) | YES | - |  |  |  |
| gradeName | varchar(125) | YES | - |  |  |  |
| gradeCode | varchar(25) | YES | - | MUL |  |  |
| packdate | datetime | YES | - |  |  |  |
| contract_code | varchar(25) | YES | - | MUL |  |  |
| contract_type | int(11) | YES | - |  |  |  |
| contract_type_name | varchar(25) | YES | - |  |  |  |
| project_name | varchar(512) | YES | - |  |  |  |
| customer_name | varchar(512) | YES | - |  |  |  |
| office_code | varchar(25) | YES | - | MUL |  |  |
| office_name | varchar(25) | YES | - |  |  |  |
| marketCode | varchar(10) | YES | - | MUL |  |  |
| marketName | varchar(15) | YES | - |  |  |  |
| systemId | int(11) | YES | - |  |  |  |
| systemName | varchar(15) | YES | - |  |  |  |
| warranty | varchar(2) | YES | - |  |  |  |
| warrantyMonth | double | YES | - |  |  |  |
| barcode2 | varchar(50) | YES | - | MUL | 母子公司发货序列号对应关系 | 母子公司发货序列号对应关系 |
| item2 | varchar(16) | YES | - |  | 母子公司发货物料编码对应关系 | 母子公司发货物料编码对应关系 |
| syncTime | datetime | YES | - |  |  |  |
| describe_2 | varchar(255) | YES | - |  |  |  |
| itemName2 | varchar(255) | YES | - |  |  |  |
| agentName | varchar(500) | YES | - |  | 代理商名称 | 代理商名称 |
| finalCustomerName | varchar(255) | YES | - |  | 最终客户名称 | 最终客户名称 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | newId | None | None |

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
| marketCode_systemId | BTREE | NON-UNIQUE | marketCode, systemId |
| office_code_marketCode_systemId | BTREE | NON-UNIQUE | office_code, marketCode, systemId |
| PRIMARY | BTREE | UNIQUE | newId |
| warrantyEndTime | BTREE | NON-UNIQUE | warrantyEndTime |

---

### 1.71 view_warranty_with_presales

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | MyISAM |
| 数据量 | ~2,857,552 行 |
| 数据大小 | 1313.1 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| newId | bigint(20) | NO | - | PRI, AUTO_INCREMENT |  |  |
| id | bigint(20) | YES | - |  |  |  |
| barcode | varchar(50) | YES | - | MUL |  |  |
| comBarCode | varchar(50) | YES | - | MUL |  |  |
| old_warrantyEndTime | datetime | YES | - |  |  |  |
| warrantyEndTime | datetime | YES | - | MUL |  |  |
| old_diff | int(7) | YES | - |  |  |  |
| diff | int(7) | YES | - | MUL |  |  |
| warrantyStartTime | datetime | YES | - |  |  |  |
| old_warrantyStartTime | datetime | YES | - |  |  |  |
| item | varchar(16) | YES | - | MUL |  |  |
| describe_ | varchar(255) | YES | - |  |  |  |
| itemName | varchar(255) | YES | - |  |  |  |
| gradeName | varchar(125) | YES | - |  |  |  |
| gradeCode | varchar(25) | YES | - | MUL |  |  |
| packdate | datetime | YES | - |  |  |  |
| contract_code | varchar(25) | YES | - | MUL |  |  |
| contract_type | int(11) | YES | - |  |  |  |
| contract_type_name | varchar(25) | YES | - |  |  |  |
| project_name | varchar(512) | YES | - |  |  |  |
| customer_name | varchar(512) | YES | - |  |  |  |
| office_code | varchar(25) | YES | - | MUL |  |  |
| office_name | varchar(25) | YES | - |  |  |  |
| marketCode | varchar(10) | YES | - | MUL |  |  |
| marketName | varchar(15) | YES | - |  |  |  |
| systemId | int(11) | YES | - |  |  |  |
| systemName | varchar(15) | YES | - |  |  |  |
| warranty | varchar(2) | YES | - |  |  |  |
| warrantyMonth | double | YES | - |  |  |  |
| barcode2 | varchar(50) | YES | - | MUL | 母子公司发货序列号对应关系 | 母子公司发货序列号对应关系 |
| item2 | varchar(16) | YES | - |  | 母子公司发货物料编码对应关系 | 母子公司发货物料编码对应关系 |
| describe_2 | varchar(255) | YES | - |  |  |  |
| itemName2 | varchar(255) | YES | - |  |  |  |
| agentName | varchar(500) | YES | - |  | 代理商名称 | 代理商名称 |
| finalCustomerName | varchar(255) | YES | - |  | 最终客户名称 | 最终客户名称 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | newId | None | None |

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
| marketCode_systemId | BTREE | NON-UNIQUE | marketCode, systemId |
| office_code_marketCode_systemId | BTREE | NON-UNIQUE | office_code, marketCode, systemId |
| PRIMARY | BTREE | UNIQUE | newId |
| warrantyEndTime | BTREE | NON-UNIQUE | warrantyEndTime |

---

### 1.72 warehouse

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~36 行 |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| whs_id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| whs_code | varchar(10) | YES | - | MUL | 库房编码 | 库房编码 |
| whs_name | varchar(25) | YES | - |  | 库房名称 | 库房名称 |
| whs_addr | varchar(255) | YES | - |  | 库房地址 | 库房地址 |
| username | varchar(10) | YES | - |  | 负责人工号 | 负责人工号 |
| department | varchar(25) | YES | - |  |  |  |
| contact_tel | varchar(15) | YES | - |  | 联系电话 | 联系电话 |
| contact_mail | varchar(50) | YES | - |  | 联系邮箱 | 联系邮箱 |
| remark | text | YES | - |  | 备注 | 备注 |
| whs_state | char(1) | YES | 1 |  | 1:有效 | 1:有效 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | whs_id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | whs_id |
| whs_code | BTREE | NON-UNIQUE | whs_code |

---

### 1.73 warehouse_info

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~11,561 行 |
| 数据大小 | 1.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| info_id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| item_code | varchar(10) | YES | - |  |  |  |
| item_name | varchar(100) | YES | - |  |  |  |
| whs_code | varchar(10) | YES | - |  |  |  |
| quantity | int(11) | YES | - |  |  |  |
| item_state | char(1) | YES | - |  | 0:坏件 1：好件 | 0:坏件 1：好件 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | info_id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | info_id |

---

### 1.74 warehouse_info_detail

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~58,033 行 |
| 数据大小 | 6.0 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| info_id | int(11) | YES | - | MUL |  |  |
| spare_serialNum | varchar(25) | YES | - |  |  |  |
| demand_type | varchar(25) | YES | - |  | 状态维护在sys_state_or_type | 状态维护在sys_state_or_type |
| tx_id | int(11) | YES | - |  |  |  |
| state | varchar(2) | YES | - |  | 1：在库 2：客户 3：被申请 | 1：在库 2：客户 3：被申请 |
| data_state | char(1) | YES | 1 |  | 0:历史 1：最新 | 0:历史 1：最新 |
| in_time | datetime | YES | - |  | 入库时间 | 入库时间 |
| finance_in_time | datetime | YES | - |  | 财务入库时间 | 财务入库时间 |
| analyse_in_time | datetime | YES | - |  |  |  |
| analyse_out_time | datetime | YES | - |  |  |  |
| gaizhi_in_time | datetime | YES | - |  |  |  |
| gaizhi_out_time | datetime | YES | - |  |  |  |
| remark | text | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| info_id | BTREE | NON-UNIQUE | info_id |
| PRIMARY | BTREE | UNIQUE | id |

---

### 1.75 warranty_change_logs

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~55 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| username | varchar(10) | NO | - |  |  |  |
| updateType | int(11) | YES | - |  |  |  |
| barcode | varchar(20) | YES | - |  |  |  |
| warrantyStartTime | datetime | YES | - |  |  |  |
| warrantyEndTime | datetime | YES | - |  |  |  |
| warrantyTimes | int(11) | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 1.76 warranty_info

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| packlistId | int(11) | YES | - |  | 标识已经同步 | 标识已经同步 |
| contractId | int(11) | YES | - |  | 标识已经同步 | 标识已经同步 |
| barCode | varchar(25) | YES | - |  | 序列号 | 序列号 |
| officeCode | varchar(25) | YES | - |  | 办事处 | 办事处 |
| projectName | varchar(255) | YES | - |  | 项目名称 | 项目名称 |
| contractNo | varchar(25) | YES | - |  | 合同号 | 合同号 |
| contractType | int(11) | YES | - |  | 合同类型 | 合同类型 |
| customerName | varchar(255) | YES | - |  | 客户名称 | 客户名称 |
| itemCode | varchar(8) | YES | - |  | 物料编码 | 物料编码 |
| itemName | varchar(255) | YES | - |  | 物料描述 | 物料描述 |
| warrantyLevel | varchar(8) | YES | - |  | 维保级别 | 维保级别 |
| warrantyStartTime | datetime | YES | - |  | 维保开始时间 | 维保开始时间 |
| warrantyEndTime | datetime | YES | - |  | 维保结束时间 | 维保结束时间 |
| warrantyLimit | int(11) | YES | - |  | 维保年限 | 维保年限 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 1.77 workflow_info

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~37,771 行 |
| 数据大小 | 2.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| sheetID | varchar(10) | NO | - |  |  |  |
| sheet_type | char(1) | YES | - |  | 单据类型（0：RMA 1：借用 2：转移） | 单据类型（0：RMA 1：借用 2：转移） |
| workflow_action | char(1) | NO | - |  | 所需做的操作(1:审批选择备件 ；2：发货确认 ；3：接货确认 5：重新审批 6：RMA申请坏件替换关系确认 4：坏件返回确认 ，7.坏件核销 8 技服执行坏件返回确认) | 所需做的操作(1:审批选择备件 ；2：发货确认 ；3：接货确认 5：重新审批 6：RMA申请坏件替换关系确认 4：坏件返回确认 ，7.坏件核销 8 技服执行坏件返回确认) |
| action_people | varchar(10) | NO | - |  | 操作的用户 | 操作的用户 |
| action_state | char(1) | NO | - |  | 1:待完成 2：已完成 | 1:待完成 2：已完成 |
| node | int(11) | YES | - |  | 节点 | 节点 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

# 第二章 系统支撑

### 2.1 data_field_relation

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~962 行 |
| 数据大小 | 400.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| dataName | varchar(255) | NO | - |  | 数据名 | 数据名 |
| dataType | varchar(255) | NO | - |  | 数据类型 | 数据类型 |
| dataId | int(11) | YES | 0 |  | 数据实例ID | 数据实例ID |
| field | varchar(128) | NO | - |  | 字段 | 字段 |
| alias | varchar(128) | YES | - |  | 字段别名 | 字段别名 |
| name | varchar(128) | NO | - |  | 字段名 | 字段名 |
| title | varchar(255) | YES | - |  | 字段标题 | 字段标题 |
| titleKey | varchar(255) | YES | - |  | 字段标题Key | 字段标题Key |
| cssId | varchar(255) | YES | - |  | 字段CSS id | 字段CSS id |
| cssClass | varchar(255) | YES | - |  | 字段CSS class | 字段CSS class |
| cssStyle | varchar(255) | YES | - |  | 字段CSS style | 字段CSS style |
| type | varchar(255) | YES | - |  | 字段类型 | 字段类型 |
| render | varchar(4096) | YES | - |  | 字段处理 | 字段处理 |
| sort | int(11) | YES | 0 |  | 排序 | 排序 |
| orderable | bit(1) | YES | b'1' |  | 允许排序 | 允许排序 |
| searchable | bit(1) | YES | b'0' |  | 允许搜索 | 允许搜索 |
| visible | bit(1) | YES | b'1' |  | 允许可见 | 允许可见 |
| required | bit(1) | YES | b'0' |  | 必填 | 必填 |
| readonly | bit(1) | YES | b'0' |  | 只读 | 只读 |
| disabled | bit(1) | YES | b'0' |  | 组件失效 | 组件失效 |
| extData | varchar(8192) | YES | - |  | 外部数据 | 外部数据 |
| extKey | varchar(255) | YES | - |  | 外部数据key | 外部数据key |
| extValue | varchar(255) | YES | - |  | 外部数据value | 外部数据value |
| media | varchar(255) | YES | - |  | 传播媒介 | 传播媒介 |
| clazzName | varchar(255) | YES | - |  | 类名 | 类名 |
| superData | varchar(255) | YES | - |  | 父类dataName | 父类dataName |
| status | int(1) | YES | 1 |  | 状态 | 状态 |
| compId | int(11) | YES | - |  | 公司ID | 公司ID |
| isSystemField | bit(1) | YES | b'1' |  | 是否为系统字段 | 是否为系统字段 |
| createBy | varchar(25) | YES | - |  |  |  |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |
| updateBy | varchar(25) | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.2 dp_act_unify_task

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 工作流统一待办任务表，聚合Activiti工作流任务和业务表单信息 |
| 数据量 | ~43,326 行 |
| 数据大小 | 66.1 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键 |
| taskId | varchar(64) | NO | - | MUL | 统一待办任务Id | 工作流任务唯一标识 |
| originTaskId | varchar(32) | NO | - | MUL | Activiti源TaskId | Activiti引擎原始任务ID |
| procInstId | varchar(64) | NO |  | MUL | 流程实例ID | Activiti流程实例ID |
| processKey | varchar(255) | NO |  |  | 流程定义key | 流程定义标识 |
| taskKey | varchar(255) | NO |  |  | 任务Key | 任务Key |
| taskName | varchar(255) | YES | - |  | 任务名 | 当前任务节点名称 |
| eventType | varchar(255) | NO | - |  | 事件类型 | 事件类型 |
| title | varchar(255) | YES | - |  | 任务标题 | 任务标题 |
| assignee | varchar(255) | NO | - |  | 办理人 | 当前任务办理人工号 |
| formUrl | varchar(255) | YES | - |  | 待办链接地址 | 待办链接地址 |
| beginTime | datetime | YES | - |  | 开始时间 | 开始时间 |
| endTime | datetime | YES | - |  | 结束时间 | 任务完成时间 |
| dueTime | datetime | YES | - |  | 过期时间 | 过期时间 |
| state | varchar(25) | YES | - |  | 办理状态 | 办理状态 |
| subState | varchar(25) | YES | - |  | 办理子状态 | 办理子状态 |
| success | bit(1) | NO | b'0' |  | 推送结果 | 推送结果 |
| message | varchar(255) | YES |  |  | 推送消息 | 推送消息 |
| latest | bit(1) | NO | b'1' |  | 是否最新 | 是否最新 |
| pushSender | varchar(255) | YES | - |  | 推送发送实体类 | 推送发送实体类 |
| pushData | varchar(4096) | YES | - |  | 推送JSON内容 | 推送JSON内容 |
| createBy | varchar(45) | NO |  |  |  |  |
| createTime | timestamp | NO | CURRENT_TIMESTAMP |  |  | 任务创建时间 |
| updateBy | varchar(45) | NO |  |  |  |  |
| updateTime | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| originTaskId | BTREE | NON-UNIQUE | originTaskId |
| PRIMARY | BTREE | UNIQUE | id |
| procInstId | BTREE | NON-UNIQUE | procInstId |
| taskId | BTREE | NON-UNIQUE | taskId |

---

### 2.3 dp_erp_purchase_order_header

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~150 行 |
| 数据大小 | 96.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| sourceType | varchar(25) | YES | - |  | 源数据类型 | 源数据类型 |
| sourceId | int(11) | YES | - |  | 源数据ID | 源数据ID |
| purchPoolId | varchar(25) | YES | - |  | 采购订单池 | 采购订单池 |
| purchId | varchar(25) | YES | - |  | 采购订单号 | 采购订单号 |
| vendAccount | varchar(25) | YES | - |  | 供应商账号 | 供应商账号 |
| purchName | varchar(255) | YES | - |  | 采购事项 | 采购事项 |
| purContract | varchar(25) | YES | - |  | 采购合同号 | 采购合同号 |
| salesContract | varchar(2048) | YES | - |  | 销售合同号 | 销售合同号 |
| contractAmount | varchar(25) | YES | - |  | 总金额 | 总金额 |
| workerPurchPlacer | varchar(25) | YES | - |  | 订货人 | 订货人 |
| applicant | varchar(25) | YES | - |  | 申请人 | 申请人 |
| inventLocationId | varchar(25) | YES | - |  | 仓库 | 仓库 |
| deliveryDate | date | YES | - |  | 交货日期 | 交货日期 |
| dlvMode | varchar(25) | YES | - |  | 交货模式 | 交货模式 |
| dlvTerm | varchar(25) | YES | - |  | 交货条款 | 交货条款 |
| payment | varchar(255) | YES | - |  | 付款条款 | 付款条款 |
| paymMode | varchar(25) | YES | - |  | 付款方式 | 付款方式 |
| remark | varchar(4096) | YES | - |  | 整单备注 | 整单备注 |
| otherSysNum | varchar(25) | YES | - |  | 外部系统编号 | 外部系统编号 |
| projectName | varchar(255) | YES | - |  | 项目名称 | 项目名称 |
| projectProgress | varchar(25) | YES | - |  | 项目进度 | 项目进度 |
| subcontractType | varchar(25) | YES | - |  | 转包类型 | 转包类型 |
| subcontStartDate | varchar(25) | YES | - |  | 转包开始日期 | 转包开始日期 |
| subcontEndDate | varchar(25) | YES | - |  | 转包结束日期 | 转包结束日期 |
| dataAreaId | varchar(25) | YES | - |  | 账套 | 账套 |
| customInfo | json | YES | - |  | 自定义信息 | 自定义信息 |
| createBy | varchar(25) | YES | - |  |  |  |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |
| updateBy | varchar(25) | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.4 dp_erp_purchase_order_line

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~150 行 |
| 数据大小 | 64.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| headerId | int(11) | YES | - |  | 采购订单HeaderId | 采购订单HeaderId |
| purchId | varchar(25) | YES | - |  | 采购订单号 | 采购订单号 |
| lineNum | varchar(25) | YES | - |  | 采购订单行号（可指定） | 采购订单行号（可指定） |
| itemId | varchar(25) | YES | - |  | 物料编码 | 物料编码 |
| purchQty | decimal(25,2) | YES | - |  | 采购数量 | 采购数量 |
| purchPrice | decimal(25,2) | YES | - |  | 采购价 | 采购价 |
| taxItemGroup | varchar(25) | YES | - |  | 税收组 | 税收组 |
| inventSerialId | varchar(25) | YES | - |  | 厂商型号（复用D365序列号字段） | 厂商型号（复用D365序列号字段） |
| inventSiteId | varchar(25) | YES | - |  | 站点 | 站点 |
| inventLocationId | varchar(25) | YES | - |  | 仓库 | 仓库 |
| wmsLocationId | varchar(25) | YES | - |  | 库位 | 库位 |
| inventTransId | varchar(25) | YES | - |  | 批次号 | 批次号 |
| officeCode | varchar(25) | YES | - |  | 办事处 | 办事处 |
| deliveryDate | date | YES | - |  | 交货日期 | 交货日期 |
| remark | varchar(4096) | YES | - |  | 行备注 | 行备注 |
| multiDimID | varchar(25) | YES | - |  | 行多维度ID | 行多维度ID |
| investmentProject | varchar(255) | YES | - |  | 募投项目 | 募投项目 |
| dimBankAccount | varchar(25) | YES | - |  | 维度-银行账户 | 维度-银行账户 |
| dimCustomer | varchar(25) | YES | - |  | 维度-客户 | 维度-客户 |
| dimVendor | varchar(25) | YES | - |  | 维度-供应商 | 维度-供应商 |
| dimEmployee | varchar(25) | YES | - |  | 维度-员工 | 维度-员工 |
| dimContract | varchar(25) | YES | - |  | 维度-合同号 | 维度-合同号 |
| dimDepartment | varchar(25) | YES | - |  | 维度-部门 | 维度-部门 |
| dimBU | varchar(25) | YES | - |  | 维度-BU | 维度-BU |
| dimProductLine | varchar(25) | YES | - |  | 维度-产品线 | 维度-产品线 |
| dimTerritory | varchar(25) | YES | - |  | 维度-区域 | 维度-区域 |
| dimIndustry | varchar(25) | YES | - |  | 维度-行业 | 维度-行业 |
| dimMultiDimID | varchar(25) | YES | - |  | 维度-多维度ID | 维度-多维度ID |
| dataAreaId | varchar(25) | YES | - |  | 账套 | 账套 |
| customInfo | json | YES | - |  | 自定义信息 | 自定义信息 |
| createBy | varchar(25) | YES | - |  |  |  |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |
| updateBy | varchar(25) | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.5 dp_erp_purchase_receipt_header

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~36 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| sourceOrderType | varchar(25) | YES | - |  | 订单源数据类型（Subcontract,Dispatch） | 订单源数据类型（Subcontract,Dispatch） |
| sourceOrderId | int(11) | YES | - |  | 订单源数据ID | 订单源数据ID |
| sourceReceiptType | varchar(25) | YES | - |  | 订单源收货类型（SubcontractPayment, DispatchSettlement） | 订单源收货类型（SubcontractPayment, DispatchSettlement） |
| sourceReceiptId | int(11) | YES | - |  | 订单源收货ID | 订单源收货ID |
| purchId | varchar(25) | YES | - |  | 采购订单号 | 采购订单号 |
| deliveryDate | date | YES | - |  | 交货日期 | 交货日期 |
| documentDate | date | YES | - |  |  |  |
| packingSlipId | varchar(512) | YES | - |  | 采购收货单号 | 采购收货单号 |
| packingSlipRemark | varchar(1024) | YES | - |  | 采购收货备注 | 采购收货备注 |
| projectProgress | varchar(1024) | YES | - |  | 项目进度 | 项目进度 |
| dataAreaId | varchar(1024) | YES | - |  | 账套 | 账套 |
| customInfo | json | YES | - |  |  |  |
| createBy | varchar(25) | YES | - |  |  |  |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |
| updateBy | varchar(25) | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.6 dp_erp_purchase_receipt_line

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~36 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| receiptId | int(11) | YES | - |  | 采购订单收货ID | 采购订单收货ID |
| purchId | varchar(25) | YES | - |  | 采购订单号 | 采购订单号 |
| inventSiteId | varchar(25) | YES | - |  | 站点 | 站点 |
| inventLocationId | varchar(25) | YES | - |  | 仓库 | 仓库 |
| wmsLocationId | varchar(25) | YES | - |  | 库位 | 库位 |
| inventTransId | varchar(25) | YES | - |  | 批次号 | 批次号 |
| lineNum | varchar(25) | YES | - |  | 采购订单行号（与批次号二选一，有批次号按批次号收货） | 采购订单行号（与批次号二选一，有批次号按批次号收货） |
| qty | decimal(25,2) | YES | - |  | 收货数量 | 收货数量 |
| price | decimal(25,2) | YES | - |  | 收货单价 | 收货单价 |
| amount | decimal(25,2) | YES | - |  | 收货金额 | 收货金额 |
| dataAreaId | varchar(25) | YES | - |  | 账套 | 账套 |
| customInfo | json | YES | - |  | 自定义信息 | 自定义信息 |
| createBy | varchar(25) | YES | - |  |  |  |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |
| updateBy | varchar(25) | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.7 ehr_company

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 从EHR系统同步的公司组织信息，是组织架构的顶层实体 |
| 数据量 | ~3 行 |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| compID | int(11) | NO | - | PRI | 公司ID，关联表外键 | 公司唯一标识，逻辑外键 -> ehr_department.compID, ehr_employee.compID, ehr_emp_power.compID |
| compCode | varchar(10) | YES | - |  | 公司编号 | 公司业务编码，如"01"代表总部 |
| compName | varchar(100) | YES | - |  | 公司名称 | 公司全称 |
| compAbbr | varchar(100) | YES | - |  | 公司简称 | 公司缩写名称 |
| adminID | int(11) | YES | - | MUL | 上级ID | 上级公司ID，用于构建公司层级关系，逻辑外键 -> ehr_company.compID |
| compGrade | int(11) | YES | - |  | 公司级别 | 公司层级等级 |
| compType | int(11) | YES | - |  | 公司类别 | 公司类型分类 |
| compArea | int(11) | YES | - |  |  | 公司所在区域编码 |
| effectDate | datetime | YES | - |  | 成立时间 | 公司成立日期 |
| lawyer | varchar(50) | YES | - |  | 法人 | 公司法人代表 |
| address | varchar(200) | YES | - |  | 地址 | 公司办公地址 |
| regAddress | varchar(200) | YES | - |  | 注册地址 | 公司注册地址 |
| tel | varchar(50) | YES | - |  | 电话 | 公司联系电话 |
| fax | varchar(50) | YES | - |  | 传真 | 公司传真号码 |
| postCode | varchar(50) | YES | - |  | 邮编 | 公司邮政编码 |
| webSite | varchar(100) | YES | - |  | 网站 | 公司网站地址 |
| isDisabled | bit(1) | YES | - |  | 失效状态 | 公司是否已失效，0=正常，1=失效 |
| disabledDate | datetime | YES | - |  | 失效时间 | 公司失效的时间戳 |
| remark | varchar(500) | YES | - |  | 备注 | 备注信息 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | compID | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| adminID | BTREE | NON-UNIQUE | adminID |
| PRIMARY | BTREE | UNIQUE | compID |

---

### 2.8 ehr_department

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 从EHR系统同步的部门组织信息，构建公司-部门树形结构 |
| 数据量 | ~517 行 |
| 数据大小 | 144.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| depID | int(11) | NO | - | PRI | 部门ID，关联外键 | 部门唯一标识，逻辑外键 -> ehr_employee.depID, ehr_emp_power.depIDs |
| depCode | varchar(20) | YES | - |  | 部门编码 | 部门业务编码 |
| depName | varchar(100) | YES | - |  | 部门名称 | 部门全称 |
| depAbbr | varchar(100) | YES | - |  | 部门简称 | 部门缩写名称 |
| compID | int(11) | YES | - | MUL | 公司ID，外键 | 所属公司ID，逻辑外键 -> ehr_company.compID |
| adminID | int(11) | YES | - | MUL | 上级ID | 上级部门ID，用于构建部门树，逻辑外键 -> ehr_department.depID |
| depGrade | int(11) | YES | - |  | 部门级别 | 部门层级等级 |
| depType | int(11) | YES | - |  | 部门类型 | 部门类型分类 |
| depProperty | int(11) | YES | - |  | 部门属性 | 部门属性标记 |
| depCost | int(11) | YES | - |  | 存在部门内分级计数用 | 部门内分级计数 |
| director | int(11) | YES | - | MUL | 主管 | 部门主管员工ID，逻辑外键 -> ehr_employee.empID |
| director2 | int(11) | YES | - | MUL | 分管领导 | 分管领导员工ID，逻辑外键 -> ehr_employee.empID |
| depEmp | int(11) | YES | - |  |  | 部门内员工计数 |
| depNum | int(11) | YES | - |  |  | 部门排序编号 |
| effectDate | datetime | YES | - |  | 生效时间 | 部门生效日期 |
| xOrder | varchar(20) | YES | - |  | 排序 | 部门显示排序值 |
| isDisabled | bit(1) | YES | - |  | 失效状态 | 部门是否已失效 |
| disabledDate | datetime | YES | - |  | 失效时间 | 部门失效的时间戳 |
| remark | varchar(500) | YES | - |  | 备注 | 备注信息 |
| depCustom1 | int(11) | YES | - |  | 保留字段1 | 预留扩展字段 |
| depCustom2 | int(11) | YES | - |  | 保留字段2、部门秘书 | 预留扩展字段，也用于存储部门秘书信息 |
| depCustom3 | int(11) | YES | - |  | 保留字段3 | 预留扩展字段 |
| depCustom4 | int(11) | YES | - |  | 保留字段4 | 预留扩展字段 |
| depCustom5 | int(11) | YES | - |  | 保留字段5 | 预留扩展字段 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | depID | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| adminID | BTREE | NON-UNIQUE | adminID |
| compID | BTREE | NON-UNIQUE | compID |
| director | BTREE | NON-UNIQUE | director |
| director2 | BTREE | NON-UNIQUE | director2 |
| PRIMARY | BTREE | UNIQUE | depID |

---

### 2.9 ehr_emp_power

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 员工数据权限配置，控制员工可查看的部门范围 |
| 数据量 | ~127 行 |
| 数据大小 | 96.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键 |
| empID | int(11) | NO | - | UNI | empID | 员工ID，逻辑外键 -> ehr_employee.empID |
| workNo | varchar(25) | NO |  | MUL | 工号 | 员工工号 |
| compID | int(11) | NO | - |  | 公司id | 所属公司ID，逻辑外键 -> ehr_company.compID |
| depIDs | varchar(4096) | NO |  |  | 从ehr同步数据生成的部门权限，固定的 | 员工可访问的部门ID列表，逗号分隔 |
| extraDepIDs | varchar(4096) | NO |  |  | 绩效管理附加的部门权限 | 手动额外授权的部门ID列表 |
| adminDepIDs | varchar(4096) | NO |  |  | 绩效考核管理的部门 | 绩效考核管理的部门 |
| empIDs | varchar(4096) | NO |  |  | 从ehr同步数据生成的下属权限，固定的 | 从ehr同步数据生成的下属权限，固定的 |
| extraEmpIDs | varchar(4096) | NO |  |  | 绩效管理附加的下属权限 | 绩效管理附加的下属权限 |
| state | bit(1) | NO | b'1' |  | 是否生效状态 | 是否生效状态 |
| createBy | varchar(25) | NO |  |  |  |  |
| createTime | datetime | NO | CURRENT_TIMESTAMP |  |  |  |
| updateBy | varchar(25) | YES |  |  |  | 最后修改人 |
| updateTime | datetime | YES | - |  |  | 最后修改时间 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |
| empID | UNIQUE | empID | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| empID | BTREE | UNIQUE | empID |
| PRIMARY | BTREE | UNIQUE | id |
| workNo | BTREE | NON-UNIQUE | workNo |

---

### 2.10 ehr_employee

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 从EHR系统同步的员工基本信息，是PMS系统用户身份识别的基础 |
| 数据量 | ~4,831 行 |
| 数据大小 | 2.2 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| empID | int(11) | NO | - | PRI | 员工ID，外键 | 员工唯一标识，逻辑外键 -> ehr_emp_power.empID, ehr_login.empID |
| workNo | varchar(100) | NO | - | MUL | 工号 | 员工工号，用于系统登录和身份识别 |
| name | varchar(200) | YES | - |  | 姓名 | 员工中文姓名 |
| eName | varchar(200) | YES | - |  | 英文名 | 员工英文名称 |
| compID | int(11) | NO | - | MUL | 公司ID | 所属公司ID，逻辑外键 -> ehr_company.compID |
| depID | int(11) | NO | - | MUL | 部门ID | 所属部门ID，逻辑外键 -> ehr_department.depID |
| jobID | int(11) | NO | - | MUL | 岗位ID | 岗位ID，逻辑外键 -> ehr_job.jobID |
| reportTo | int(11) | YES | - | MUL | 直接上级 | 直属上级员工ID，逻辑外键 -> ehr_employee.empID |
| wfreportTo | int(11) | YES | - | MUL | 职能上级 | 职能上级员工ID，逻辑外键 -> ehr_employee.empID |
| empStatus | int(11) | NO | - |  | 员工状态，1：在职，2：离职 | 1=在职，2=离职 |
| jobStatus | int(11) | YES | - |  | 岗位状态 | 岗位在岗状态 |
| empType | int(11) | YES | - |  | 聘用类型：1：正式，3：实习生 | 1=正式，3=实习生 |
| joinDate | datetime | YES | - |  | 加入公司日期 | 员工入职日期 |
| workBeginDate | datetime | YES | - |  | 工作开始日期 | 员工开始工作日期 |
| jobBeginDate | datetime | YES | - |  | 加入公司日期（未知） | 岗位开始日期 |
| pracBeginDate | datetime | YES | - |  | 实习开始时间 | 实习期开始日期 |
| pracEndDate | datetime | YES | - |  | 实习结束时间 | 实习期结束日期 |
| probBeginDate | datetime | YES | - |  |  | 试用期开始日期（推断） |
| probEndDate | datetime | YES | - |  |  | 试用期结束日期（推断） |
| leaveDate | datetime | YES | - |  | 离职时间 | 员工离职日期 |
| gender | int(11) | YES | - |  | 性别：1：男，2：女 | 1=男，2=女 |
| email | varchar(500) | YES | - |  | 邮箱 | 员工邮箱地址 |
| mobile | varchar(50) | YES | - |  | 手机 | 员工手机号码 |
| officePhone | varchar(50) | YES | - |  | 座机 | 员工办公座机号码 |
| remark | varchar(100) | YES | - |  | 备注 | 备注信息 |
| disabled | int(11) | YES | 0 |  | 失效 | 员工是否失效，0=正常 |
| empCustom1 | int(11) | YES | - |  | 预留字段1 | 预留扩展字段 |
| empCustom2 | int(11) | YES | - |  | 预留字段2 | 预留扩展字段 |
| empCustom3 | int(11) | YES | - |  | 预留字段3 | 预留扩展字段 |
| empCustom4 | varchar(50) | YES | - |  | 预留字段4 | 预留扩展字段 |
| empCustom5 | int(11) | YES | - |  | 预留字段5 | 预留扩展字段 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | empID | None | None |

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

### 2.11 ehr_job

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 从EHR系统同步的岗位信息，定义组织内的岗位体系 |
| 数据量 | ~245 行 |
| 数据大小 | 80.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| jobID | int(11) | NO | - | PRI | 岗位ID，关联表外键 | 岗位唯一标识，逻辑外键 -> ehr_employee.jobID |
| jobCode | varchar(10) | YES | - |  | 岗位编码 | 岗位业务编码 |
| jobName | varchar(100) | YES | - |  | 岗位名称 | 岗位名称 |
| jobAbbr | varchar(100) | YES | - |  | 岗位简称 | 岗位缩写 |
| depID | int(11) | YES | - | MUL | 部门ID | 所属部门ID，逻辑外键 -> ehr_department.depID |
| adminID | int(11) | YES | - | MUL | 上级ID | 上级ID |
| jobGrage | int(11) | YES | - |  | 岗位级别 | 岗位级别 |
| jobType | int(11) | YES | - |  | 岗位类型 | 岗位类型分类 |
| jobProperty | int(11) | YES | - |  | 岗位属性 | 岗位属性 |
| jobNum | int(11) | YES | - |  |  |  |
| isCore | bit(1) | YES | b'0' |  |  |  |
| effectDate | datetime | NO | - |  | 生效时间 | 生效时间 |
| xorder | varchar(20) | YES | - |  | 排序 | 排序 |
| isDisabled | bit(1) | YES | b'0' |  | 失效状态 | 岗位是否已失效 |
| disabledDate | datetime | YES | - |  | 失效时间 | 岗位失效的时间戳 |
| remark | varchar(500) | YES | - |  | 备注 | 备注信息 |
| xType | int(11) | YES | - |  |  |  |
| jobCustom1 | int(11) | YES | - |  | 保留字段1 | 保留字段1 |
| jobCustom2 | int(11) | YES | - |  | 保留字段2 | 保留字段2 |
| jobCustom3 | int(11) | YES | - |  | 保留字段3 | 保留字段3 |
| jobCustom4 | int(11) | YES | - |  | 保留字段4 | 保留字段4 |
| jobCustom5 | int(11) | YES | - |  | 保留字段5 | 保留字段5 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | jobID | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| adminID | BTREE | NON-UNIQUE | adminID |
| depID | BTREE | NON-UNIQUE | depID |
| PRIMARY | BTREE | UNIQUE | jobID |

---

### 2.12 ehr_login

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | EHR员工与PMS系统登录账号的映射关系表 |
| 数据量 | ~3,224 行 |
| 数据大小 | 272.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI |  | 自增主键 |
| title | varchar(255) | YES | - |  |  |  |
| account | varchar(255) | YES | - |  |  |  |
| empID | int(11) | YES | - |  |  | 逻辑外键 -> ehr_employee.empID |
| workNo | varchar(255) | YES | - |  |  | 员工工号 |
| name | varchar(255) | YES | - |  |  |  |
| isDisabled | int(11) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.13 firebird_operation_log -- 发货系统Firebird数据库更改日志

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 发货系统Firebird数据库更改日志 |
| 数据量 | ~72,066 行 |
| 数据大小 | 43.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(10) unsigned | NO | - | PRI, AUTO_INCREMENT | ID | ID |
| sheetId | varchar(25) | YES |  | MUL | 流水号SheetId | 流水号SheetId |
| txId | int(11) | YES | - |  | tx_info的tx_id | tx_info的tx_id |
| contractNo | varchar(45) | YES |  | MUL | 合同号 | 合同号 |
| barCode | varchar(25) | YES |  | MUL | 设备序列号 | 设备序列号 |
| insteadOfNum | varchar(25) | YES |  | MUL | RMA申请被替代的设备序列号 | RMA申请被替代的设备序列号 |
| changeTable | varchar(45) | YES |  |  | 操作的表 | 操作的表 |
| operatTime | timestamp | NO | CURRENT_TIMESTAMP |  | 操作时间 | 操作时间 |
| sqlText | text | YES | - |  | 操作表的sql语句 | 操作表的sql语句 |
| remark | varchar(45) | YES |  |  | 备注 | 备注 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barCode | BTREE | NON-UNIQUE | barCode |
| contractNo | BTREE | NON-UNIQUE | contractNo |
| insteadOfNum | BTREE | NON-UNIQUE | insteadOfNum |
| PRIMARY | BTREE | UNIQUE | id |
| sheetId | BTREE | NON-UNIQUE | sheetId |

---

### 2.14 hexiao

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~83 行 |
| 数据大小 | 48.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| 单据编号 | double | YES | - |  |  |  |
| 过帐日期 | timestamp | YES | - |  |  |  |
| 物料代码 | varchar(255) | YES | - |  |  |  |
| 物料/服务描述 | varchar(255) | YES | - |  |  |  |
| 未核销数量 | double | YES | - |  |  |  |
| 设备序列号 | varchar(255) | YES | - |  |  |  |
| 注释 | varchar(255) | YES | - |  |  |  |
| 合同号 | varchar(255) | YES | - |  |  |  |
| 责任部门 | varchar(255) | YES | - |  |  |  |

---

### 2.15 pm_order_data_from_erp -- 从ERP刷新过来的原始合同信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 从ERP刷新过来的原始合同信息 |
| 数据量 | ~52,940 行 |
| 数据大小 | 22.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| orderNumber | varchar(25) | YES | - | MUL |  |  |
| contractNo | varchar(50) | YES | - | MUL |  |  |
| orderExecNumber | varchar(50) | YES | - | MUL |  |  |
| orderCreateTime | datetime | YES | - |  |  |  |
| customerRequireTime | datetime | YES | - |  |  |  |
| customerCode | varchar(55) | YES | - |  |  |  |
| customerName | varchar(255) | YES | - |  |  |  |
| projectName | varchar(255) | YES | - |  |  |  |
| orderComment | varchar(2048) | YES | - |  |  |  |
| orderType | int(11) | YES | 0 | MUL | 0 正常销售 1 退货 | 0 正常销售 1 退货 |
| compCode | varchar(25) | YES | 0 |  | 公司编码 | 公司编码 |
| salesType | varchar(25) | YES | 01 |  | 销售类型,01:正常合同，02:借转销，14:销售类借货 | 销售类型,01:正常合同，02:借转销，14:销售类借货 |
| source | varchar(25) | YES | SAP |  | 数据来源，默认:SAP,D365,总代借货:SMS | 数据来源，默认:SAP,D365,总代借货:SMS |
| customInfo | json | YES | - |  | 自定义字段 | 自定义字段 |
| syncTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | contractNo |
| orderExecNumber | BTREE | NON-UNIQUE | orderExecNumber |
| orderNumber | BTREE | NON-UNIQUE | orderNumber |
| orderType | BTREE | NON-UNIQUE | orderType, salesType |
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.16 pm_order_data_from_erp_d365 -- 从ERP刷新过来的原始合同信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 从ERP刷新过来的原始合同信息 |
| 数据量 | ~1,790 行 |
| 数据大小 | 672.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| orderNumber | varchar(25) | YES | - | MUL |  |  |
| contractNo | varchar(50) | YES | - | MUL |  |  |
| orderExecNumber | varchar(50) | YES | - | MUL |  |  |
| orderCreateTime | datetime | YES | - |  |  |  |
| customerRequireTime | datetime | YES | - |  |  |  |
| customerCode | varchar(55) | YES | - |  |  |  |
| customerName | varchar(255) | YES | - |  |  |  |
| projectName | varchar(255) | YES | - |  |  |  |
| orderComment | varchar(2048) | YES | - |  |  |  |
| orderType | int(11) | YES | 0 |  | 0 正常销售 1 退货 | 0 正常销售 1 退货 |
| compCode | varchar(25) | YES | 0 |  | 公司编码 | 公司编码 |
| salesType | varchar(25) | YES | 01 |  | 销售类型,01:正常合同，02:借转销，14:销售类借货 | 销售类型,01:正常合同，02:借转销，14:销售类借货 |
| customInfo | json | YES | - |  | 自定义字段 | 自定义字段 |
| syncTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | contractNo |
| orderExecNumber | BTREE | NON-UNIQUE | orderExecNumber |
| orderNumber | BTREE | NON-UNIQUE | orderNumber |
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.17 pm_order_data_from_erp_sap -- 从ERP刷新过来的原始合同信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 从ERP刷新过来的原始合同信息 |
| 数据量 | ~47,708 行 |
| 数据大小 | 19.1 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| orderNumber | varchar(25) | YES | - | MUL |  |  |
| contractNo | varchar(50) | YES | - | MUL |  |  |
| orderExecNumber | varchar(50) | YES | - | MUL |  |  |
| orderCreateTime | datetime | YES | - |  |  |  |
| customerRequireTime | datetime | YES | - |  |  |  |
| customerCode | varchar(55) | YES | - |  |  |  |
| customerName | varchar(255) | YES | - |  |  |  |
| projectName | varchar(255) | YES | - |  |  |  |
| orderComment | varchar(255) | YES | - |  |  |  |
| orderType | int(11) | YES | 0 |  | 0 正常销售 1 退货 | 0 正常销售 1 退货 |
| compCode | varchar(25) | YES | 0 |  | 公司编码 | 公司编码 |
| salesType | varchar(25) | YES | 01 |  | 销售类型,01:正常合同，02:借转销，14:销售类借货 | 销售类型,01:正常合同，02:借转销，14:销售类借货 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | contractNo |
| orderExecNumber | BTREE | NON-UNIQUE | orderExecNumber |
| orderNumber | BTREE | NON-UNIQUE | orderNumber |
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.18 pm_order_data_from_erp_source -- 从ERP刷新过来的原始合同信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 从ERP刷新过来的原始合同信息 |
| 数据量 | ~49,054 行 |
| 数据大小 | 23.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 表主键 |
| orderNumber | varchar(25) | YES | - | MUL |  |  |
| contractNo | varchar(50) | YES | - | MUL |  |  |
| orderExecNumber | varchar(50) | YES | - | MUL |  |  |
| orderExecNumberShort | varchar(50) | YES | - |  |  |  |
| orderCreateTime | datetime | YES | - |  |  |  |
| customerRequireTime | datetime | YES | - |  |  |  |
| customerCode | varchar(55) | YES | - |  |  |  |
| customerName | varchar(255) | YES | - |  |  |  |
| projectName | varchar(255) | YES | - |  |  |  |
| orderComment | varchar(2048) | YES | - |  |  |  |
| orderType | int(11) | YES | 0 | MUL | 0 正常销售 1 退货 | 0 正常销售 1 退货 |
| compCode | varchar(25) | YES | 0 |  | 公司编码 | 公司编码 |
| salesType | varchar(25) | YES | 01 |  | 销售类型,01:正常合同，02:借转销，14:销售类借货 | 销售类型,01:正常合同，02:借转销，14:销售类借货 |
| source | varchar(25) | YES | SAP |  | 数据来源，默认:SAP,D365,总代借货:SMS | 数据来源，默认:SAP,D365,总代借货:SMS |
| customInfo | json | YES | - |  | 自定义字段 | 自定义字段 |
| syncTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | contractNo |
| orderExecNumber | BTREE | NON-UNIQUE | orderExecNumber |
| orderNumber | BTREE | NON-UNIQUE | orderNumber |
| orderType | BTREE | NON-UNIQUE | orderType, salesType |
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.19 pm_order_line_from_erp

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~219,652 行 |
| 数据大小 | 37.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| orderNumber | varchar(25) | YES | - | MUL |  |  |
| lineNum | varchar(25) | YES | - |  |  |  |
| itemCode | varchar(25) | YES | - | MUL |  |  |
| itemDesc | varchar(255) | YES | - |  |  |  |
| orderQuantity | int(11) | YES | - |  |  |  |
| openQuantity | int(11) | YES | - |  |  |  |
| bundleCode | varchar(25) | YES | - |  |  |  |
| warrantyMonth | int(11) | YES | - |  |  |  |
| lineType | int(11) | YES | 0 |  |  |  |
| compCode | varchar(25) | YES | 0 |  | 公司编码 | 公司编码 |
| profitCenter | varchar(25) | YES | - |  | 利润中心 | 利润中心 |
| realOrderExecNumber | varchar(25) | YES | - |  | 真实执行单号 | 真实执行单号 |
| source | varchar(25) | YES | SAP |  | 数据来源，默认:SAP,D365,总代借货:SMS | 数据来源，默认:SAP,D365,总代借货:SMS |
| customInfo | json | YES | - |  | 自定义字段 | 自定义字段 |
| syncTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| itemCode | BTREE | NON-UNIQUE | itemCode |
| orderNumber | BTREE | NON-UNIQUE | orderNumber |
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.20 pm_order_line_from_erp_d365

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~7,839 行 |
| 数据大小 | 2.0 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| orderNumber | varchar(25) | YES | - | MUL |  |  |
| lineNum | varchar(25) | YES | - |  |  |  |
| itemCode | varchar(25) | YES | - | MUL |  |  |
| itemDesc | varchar(255) | YES | - |  |  |  |
| orderQuantity | int(11) | YES | - |  |  |  |
| openQuantity | int(11) | YES | - |  |  |  |
| bundleCode | varchar(25) | YES | - |  |  |  |
| warrantyMonth | int(11) | YES | - |  |  |  |
| lineType | int(11) | YES | 0 |  |  |  |
| compCode | varchar(25) | YES | 0 |  | 公司编码 | 公司编码 |
| profitCenter | varchar(25) | YES | - |  | 利润中心 | 利润中心 |
| realOrderExecNumber | varchar(25) | YES | - |  | 真实执行单号 | 真实执行单号 |
| customInfo | json | YES | - |  | 自定义字段 | 自定义字段 |
| syncTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| itemCode | BTREE | NON-UNIQUE | itemCode |
| orderNumber | BTREE | NON-UNIQUE | orderNumber, lineNum |
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.21 pm_order_line_from_erp_sap

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~208,448 行 |
| 数据大小 | 40.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| orderNumber | varchar(25) | YES | - | MUL |  |  |
| lineNum | int(11) | YES | - |  |  |  |
| itemCode | varchar(25) | YES | - | MUL |  |  |
| itemDesc | varchar(255) | YES | - |  |  |  |
| orderQuantity | int(11) | YES | - |  |  |  |
| openQuantity | int(11) | YES | - |  |  |  |
| bundleCode | varchar(25) | YES | - |  |  |  |
| warrantyMonth | int(11) | YES | - |  |  |  |
| lineType | int(11) | YES | 0 |  |  |  |
| compCode | varchar(25) | YES | 0 |  | 公司编码 | 公司编码 |
| profitCenter | varchar(25) | YES | - |  | 利润中心 | 利润中心 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| itemCode | BTREE | NON-UNIQUE | itemCode |
| orderNumber | BTREE | NON-UNIQUE | orderNumber, lineNum |
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.22 pm_order_line_from_erp_source

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~205,968 行 |
| 数据大小 | 41.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| orderNumber | varchar(25) | YES | - | MUL |  |  |
| lineNum | varchar(25) | YES | - |  |  |  |
| itemCode | varchar(25) | YES | - | MUL |  |  |
| itemDesc | varchar(255) | YES | - |  |  |  |
| orderQuantity | int(11) | YES | - |  |  |  |
| openQuantity | int(11) | YES | - |  |  |  |
| bundleCode | varchar(25) | YES | - |  |  |  |
| warrantyMonth | int(11) | YES | - |  |  |  |
| lineType | int(11) | YES | 0 |  |  |  |
| compCode | varchar(25) | YES | 0 |  | 公司编码 | 公司编码 |
| profitCenter | varchar(25) | YES | - |  | 利润中心 | 利润中心 |
| realOrderExecNumber | varchar(25) | YES | - |  | 真实执行单号 | 真实执行单号 |
| source | varchar(25) | YES | SAP |  | 数据来源，默认:SAP,D365,总代借货:SMS | 数据来源，默认:SAP,D365,总代借货:SMS |
| customInfo | json | YES | - |  | 自定义字段 | 自定义字段 |
| syncTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| itemCode | BTREE | NON-UNIQUE | itemCode |
| orderNumber | BTREE | NON-UNIQUE | orderNumber, lineNum |
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.23 pm_pb_plan_from_sms

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~43,912 行 |
| 数据大小 | 8.0 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 主键 | 主键 |
| contractNo | varchar(25) | YES | - | MUL | 合同号 | 合同号 |
| batchCode | varchar(10) | YES | - |  | 批次信息 | 批次信息 |
| basicDataName | varchar(20) | YES | - |  | 活动（款项名称） | 活动（款项名称） |
| referenceEventName | varchar(20) | YES | - |  | 参照事件名称 | 参照事件名称 |
| eventPlanHappenDate | datetime | YES | - |  | 事件计划发生日期 | 事件计划发生日期 |
| afterDaysNum | int(11) | YES | 0 |  | 后推天数 | 后推天数 |
| eventActualFinishDate | datetime | YES | - |  | 事件实际完成日期 | 事件实际完成日期 |
| marketingFeedback | varchar(2000) | YES | - |  | 销售反馈 | 销售反馈 |
| createBy | varchar(45) | YES | - |  |  |  |
| createTime | datetime | YES | - |  |  |  |
| updateBy | varchar(45) | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |
| effectiveFrom | datetime | YES | - |  |  |  |
| effectiveTo | datetime | YES | - |  |  |  |
| dataSource | varchar(25) | YES | SMS |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo_IDX | BTREE | NON-UNIQUE | contractNo |
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.24 pm_pb_plan_from_sms_history

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~16,133 行 |
| 数据大小 | 2.9 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 主键 | 主键 |
| contractNo | varchar(25) | YES | - | MUL | 合同号 | 合同号 |
| batchCode | varchar(10) | YES | - |  | 批次信息 | 批次信息 |
| basicDataName | varchar(20) | YES | - |  | 活动（款项名称） | 活动（款项名称） |
| referenceEventName | varchar(20) | YES | - |  | 参照事件名称 | 参照事件名称 |
| eventPlanHappenDate | datetime | YES | - |  | 事件计划发生日期 | 事件计划发生日期 |
| afterDaysNum | int(11) | YES | 0 |  | 后推天数 | 后推天数 |
| eventActualFinishDate | datetime | YES | - |  | 事件实际完成日期 | 事件实际完成日期 |
| marketingFeedback | varchar(2000) | YES | - |  | 销售反馈 | 销售反馈 |
| createBy | varchar(10) | YES | - |  |  |  |
| createTime | datetime | YES | - |  |  |  |
| updateBy | varchar(10) | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |
| effectiveFrom | datetime | YES | - |  |  |  |
| effectiveTo | datetime | YES | - |  |  |  |
| dataSource | varchar(25) | YES | SMS |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo_IDX | BTREE | NON-UNIQUE | contractNo |
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.25 pm_person_from_oa -- 销售联系电话信息从OA同步

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 销售联系电话信息从OA同步 |
| 数据量 | ~1,480 行 |
| 数据大小 | 208.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| salesmanCode | varchar(45) | YES | - | MUL |  |  |
| salesmanTel | varchar(45) | YES | - |  |  |  |
| salesmanName | varchar(45) | YES | - |  |  |  |
| salesmanMail | varchar(100) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| salesmanCode1 | BTREE | NON-UNIQUE | salesmanCode |

---

### 2.26 pm_report_line_data

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 统计报表行数据，存储各办事处的指标统计值 |
| 数据量 | ~11,315 行 |
| 数据大小 | 1.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 报表趋势图数据集合 | 自增主键 |
| dataTypeCode | varchar(15) | YES | - |  | 区分统计的哪种数据 | 统计指标编码 |
| officeCode | varchar(25) | YES | - |  | 办事处 | 统计办事处编码 |
| conditionValue | varchar(25) | YES | - |  | 条件值 | 统计条件值 |
| totalValue | varchar(25) | YES | - |  | 总值 | 统计被比值 |
| specificValue | varchar(25) | YES | - |  | 比值 | 统计比例值 |
| settingTime | datetime | YES | - |  | 数据固化时间 | 数据统计时间 |
| createBy | varchar(25) | YES | - |  |  |  |
| createTime | datetime | YES | - |  |  |  |
| effectiveFrom | datetime | YES | - |  |  |  |
| effectiveTo | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.27 pm_workflow

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 工作流定义配置表，存储流程定义与业务表单的映射关系 |
| 数据量 | ~180 行 |
| 数据大小 | 1.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键 |
| processKey | varchar(25) | NO |  |  | 流程定义key | Activiti流程定义标识 |
| taskKey | varchar(25) | YES |  |  | 任务Key | 任务Key |
| applyTime | datetime | YES | - |  | 申请时间 | 申请时间 |
| beginTime | datetime | YES | - |  | 开始时间 | 开始时间 |
| endTime | datetime | YES | - |  | 结束时间 | 结束时间 |
| dueTime | datetime | YES | - |  | 过期时间 | 过期时间 |
| procInstId | varchar(64) | YES |  | MUL | 流程实例ID | 流程实例ID |
| message | varchar(255) | YES |  |  | 处理消息 | 处理消息 |
| status | varchar(255) | NO | PENDING |  | 状态 | 状态 |
| userId | int(11) | NO | 0 |  | userinfo表ID | userinfo表ID |
| objType | varchar(25) | NO |  | MUL | 对象类型 | 对象类型 |
| objId | int(11) | NO | 0 | MUL | 对象Id | 对象Id |
| dataType | varchar(25) | NO |  |  | 数据类型 | 数据类型 |
| dataId | int(11) | NO | 0 |  | 数据Id | 数据Id |
| createBy | varchar(45) | NO |  |  |  |  |
| createTime | datetime | NO | CURRENT_TIMESTAMP |  |  |  |
| updateBy | varchar(45) | NO |  |  |  |  |
| updateTime | datetime | YES | - |  |  |  |
| orgId | int(2) | YES | 0 |  | 组织ID | 组织ID |
| customInfo | json | YES | - |  | 自定义信息 | 自定义信息 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| objDataKey | BTREE | NON-UNIQUE | objType, objId, dataType, dataId |
| PRIMARY | BTREE | UNIQUE | id |
| procInstId | BTREE | NON-UNIQUE | procInstId |
| worfFlow_objId | BTREE | NON-UNIQUE | objId |

---

### 2.28 project_info_from_sms

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~3,190 行 |
| 数据大小 | 1.8 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| smsId | bigint(11) | NO | 0 |  |  |  |
| orderCode | varchar(25) | NO | - | MUL |  |  |
| predBidDate | datetime | YES | - |  |  |  |
| projectName | varchar(255) | YES | - |  |  |  |
| firstChannelCode | varchar(255) | YES | - |  |  |  |
| firstChannelName | varchar(100) | YES | - |  |  |  |
| channelCode | varchar(255) | YES | - |  |  |  |
| channelName | varchar(255) | YES | - |  |  |  |
| contractNo | varchar(25) | YES | - |  |  |  |
| marketName | varchar(25) | YES | - |  |  |  |
| systemName | varchar(25) | YES | - |  |  |  |
| expendName | varchar(25) | YES | - |  |  |  |
| industryName | varchar(25) | YES | - |  |  |  |
| industryNewName | varchar(25) | YES | - |  | 对应的子行业 | 对应的子行业 |
| totaljine | decimal(12,2) | YES | - |  |  |  |
| salesName | varchar(25) | YES | - |  |  |  |
| officeName | varchar(25) | YES | - |  |  |  |
| solutionname | varchar(1000) | YES | - |  |  |  |
| projectpropertyName | varchar(20) | YES | - |  |  |  |
| customerProjectCode | varchar(255) | YES | - |  | 客户项目编码 | 客户项目编码 |
| customerProjectName | varchar(255) | YES | - |  | 客户项目名称 | 客户项目名称 |
| username | varchar(255) | YES | - |  |  |  |
| realname | varchar(128) | YES | - |  |  |  |
| officeCode | varchar(255) | YES | - |  |  |  |
| org_id | int(11) | YES | - | MUL |  |  |
| customInfo | json | YES | - |  | 自定义信息 | 自定义信息 |
| source | varchar(25) | YES | SMS |  | 数据来源 | 数据来源 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| orderCode | BTREE | NON-UNIQUE | orderCode, org_id |
| org_id | BTREE | NON-UNIQUE | org_id |

---

### 2.29 role

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~7 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| roleId | int(11) | NO | - |  |  |  |
| roleName | varchar(10) | NO | - |  |  |  |
| status | int(11) | YES | - |  |  |  |
| mark | text | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.30 sms_ofst_contract_head_sap

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~60,264 行 |
| 数据大小 | 26.1 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | bigint(20) | NO | - | PRI, AUTO_INCREMENT | 表信息：从SAP刷新的合同头信息，此表信息定时刷新 | 表信息：从SAP刷新的合同头信息，此表信息定时刷新 |
| contract_num | varchar(45) | YES | - | MUL |  |  |
| batch_code | varchar(10) | YES | - |  |  |  |
| project_name | varchar(200) | YES | - |  |  |  |
| order_num | varchar(25) | YES | - |  |  |  |
| client_supplier_code | varchar(20) | YES | - |  |  |  |
| client_supplier_name | varchar(200) | YES | - |  |  |  |
| contract_money_amount | decimal(20,2) | NO | - |  |  |  |
| delivered_money_amount | decimal(20,2) | NO | - |  |  |  |
| collected_money_amount | decimal(20,2) | NO | - |  |  |  |
| collected_money_ratio | double | YES | 0 |  |  |  |
| receivables_money_amount | decimal(20,2) | YES | - |  |  |  |
| over_due_money_amount | decimal(20,2) | YES | - |  |  |  |
| maketing_department_name | varchar(40) | YES | - |  |  |  |
| office_name | varchar(20) | YES | - |  |  |  |
| industry_name | varchar(40) | YES | - |  |  |  |
| marketing_representative_name | varchar(20) | YES | - |  |  |  |
| currency_name | varchar(25) | YES | - |  | 币种 | 币种 |
| create_by | varchar(20) | YES | - |  |  |  |
| create_time | datetime | YES | - |  |  |  |
| update_by | varchar(20) | YES | - |  |  |  |
| update_time | datetime | YES | - |  |  |  |
| effective_from | datetime | YES | - |  |  |  |
| effective_to | datetime | YES | - |  |  |  |
| import_batch_num | varchar(12) | YES | - |  |  |  |
| contract_create_date | datetime | YES | - |  | SAP合同创建日期 | SAP合同创建日期 |
| projectCode | varchar(80) | YES | - |  |  |  |
| marketCode | varchar(80) | YES | - |  |  |  |
| systemId | int(11) | YES | - |  |  |  |
| industryId | int(11) | YES | - |  |  |  |
| officeCode | varchar(80) | YES | - |  |  |  |
| expendId | int(11) | YES | - |  |  |  |
| usernamec | varchar(10) | YES | - |  | 销售用户账号 | 销售用户账号 |
| latest_ship_date | datetime | YES | - |  | 交货日期 | 交货日期 |
| usernamec2 | varchar(10) | YES | - |  |  |  |
| systemid_o | int(11) | YES | - |  |  |  |
| expendid_o | int(11) | YES | - |  |  |  |
| industry_name_o | varchar(40) | YES | - |  |  |  |
| dataSource | varchar(25) | YES | CRM |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| index_contract_num | BTREE | NON-UNIQUE | contract_num |
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.31 sms_ofst_contract_head_sap_history

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~45,355 行 |
| 数据大小 | 19.1 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | bigint(20) | NO | - | PRI, AUTO_INCREMENT | 表信息：从SAP刷新的合同头信息，此表信息定时刷新 | 表信息：从SAP刷新的合同头信息，此表信息定时刷新 |
| contract_num | varchar(45) | YES | - | MUL |  |  |
| batch_code | varchar(10) | YES | - |  |  |  |
| project_name | varchar(200) | YES | - |  |  |  |
| order_num | varchar(25) | YES | - |  |  |  |
| client_supplier_code | varchar(20) | YES | - |  |  |  |
| client_supplier_name | varchar(200) | YES | - |  |  |  |
| contract_money_amount | decimal(20,2) | NO | - |  |  |  |
| delivered_money_amount | decimal(20,2) | NO | - |  |  |  |
| collected_money_amount | decimal(20,2) | NO | - |  |  |  |
| collected_money_ratio | double | YES | 0 |  |  |  |
| receivables_money_amount | decimal(20,2) | YES | - |  |  |  |
| over_due_money_amount | decimal(20,2) | YES | - |  |  |  |
| maketing_department_name | varchar(40) | YES | - |  |  |  |
| office_name | varchar(20) | YES | - |  |  |  |
| industry_name | varchar(40) | YES | - |  |  |  |
| marketing_representative_name | varchar(20) | YES | - |  |  |  |
| currency_name | varchar(25) | YES | - |  | 币种 | 币种 |
| create_by | varchar(20) | YES | - |  |  |  |
| create_time | datetime | YES | - |  |  |  |
| update_by | varchar(20) | YES | - |  |  |  |
| update_time | datetime | YES | - |  |  |  |
| effective_from | datetime | YES | - |  |  |  |
| effective_to | datetime | YES | - |  |  |  |
| import_batch_num | varchar(12) | YES | - |  |  |  |
| contract_create_date | datetime | YES | - |  | SAP合同创建日期 | SAP合同创建日期 |
| projectCode | varchar(80) | YES | - |  |  |  |
| marketCode | varchar(80) | YES | - |  |  |  |
| systemId | int(11) | YES | - |  |  |  |
| industryId | int(11) | YES | - |  |  |  |
| officeCode | varchar(80) | YES | - |  |  |  |
| expendId | int(11) | YES | - |  |  |  |
| usernamec | varchar(10) | YES | - |  | 销售用户账号 | 销售用户账号 |
| latest_ship_date | datetime | YES | - |  | 交货日期 | 交货日期 |
| usernamec2 | varchar(10) | YES | - |  |  |  |
| systemid_o | int(11) | YES | - |  |  |  |
| expendid_o | int(11) | YES | - |  |  |  |
| industry_name_o | varchar(40) | YES | - |  |  |  |
| dataSource | varchar(25) | YES | SMS |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| index_contract_num | BTREE | NON-UNIQUE | contract_num |
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.32 sys_state_or_type

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~30 行 |
| 数据大小 | 48.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| stCode | varchar(25) | YES | - | MUL |  |  |
| stName | varchar(25) | YES | - |  |  |  |
| resolveCode | varchar(10) | YES | - | MUL |  |  |
| resolveName | varchar(25) | YES | - |  |  |  |
| validity | int(11) | YES | 1 |  | 1有效 0 无效 | 1有效 0 无效 |
| remark | varchar(100) | YES | - |  | 说明 | 说明 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| st | BTREE | NON-UNIQUE | resolveCode |
| stCode | BTREE | NON-UNIQUE | stCode |

---

### 2.33 t_company

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 权限体系中的公司信息，与ehr_company数据同步 |
| 数据量 | ~3 行 |
| 数据大小 | 48.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI | 公司ID，关联表外键 | 公司唯一标识 |
| compCode | varchar(10) | NO |  | MUL | 公司编号 | 公司业务编码 |
| compName | varchar(100) | NO |  |  | 公司名称 | 公司全称 |
| compAbbr | varchar(100) | YES |  |  | 公司简称 | 公司缩写名称 |
| compAccount | varchar(10) | YES |  |  | 公司账套 | 公司财务账套编码 |
| adminID | int(11) | NO | 0 | MUL | 上级ID | 上级公司ID |
| compGrade | int(11) | YES | 1 |  | 公司级别 | 公司层级等级 |
| lawyer | varchar(50) | YES |  |  | 法人 | 公司法人代表 |
| address | varchar(200) | YES |  |  | 地址 | 公司办公地址 |
| regAddress | varchar(200) | YES |  |  | 注册地址 | 公司注册地址 |
| tel | varchar(50) | YES |  |  | 电话 | 公司联系电话 |
| fax | varchar(50) | YES |  |  | 传真 | 公司传真号码 |
| postCode | varchar(50) | YES |  |  | 邮编 | 公司邮政编码 |
| webSite | varchar(100) | YES |  |  | 网站 | 公司网站地址 |
| state | bit(1) | NO | b'1' |  | 失效状态 | 失效状态 |
| effectiveFrom | datetime | NO | CURRENT_TIMESTAMP |  | 成立时间 | 成立时间 |
| effectiveTo | datetime | YES | - |  | 结束时间 | 结束时间 |
| disabledTime | datetime | YES | - |  | 失效时间 | 失效时间 |
| remark | varchar(500) | YES |  |  | 备注 | 备注信息 |
| createBy | varchar(25) | NO |  |  |  |  |
| createTime | datetime | NO | CURRENT_TIMESTAMP |  |  |  |
| updateBy | varchar(25) | YES |  |  |  |  |
| updateTime | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| adminID | BTREE | NON-UNIQUE | adminID |
| compCode | BTREE | NON-UNIQUE | compCode |
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.34 t_data_field_relation

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 数据字段别名映射关系，用于数据导入导出时的字段映射 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键 |
| dataName | varchar(255) | NO | - |  | 数据名 | 数据对象名称 |
| dataType | varchar(255) | NO | - |  | 数据类型 | 数据对象类型 |
| dataId | int(11) | YES | 0 |  | 数据实例ID | 数据对象实例标识 |
| field | varchar(128) | NO | - |  | 字段 | 字段 |
| alias | varchar(128) | YES | - |  | 字段别名 | 字段别名/映射名 |
| name | varchar(128) | NO | - |  | 字段名 | 字段名 |
| title | varchar(255) | YES | - |  | 字段标题 | 字段标题 |
| titleKey | varchar(255) | YES | - |  | 字段标题Key | 字段标题Key |
| cssId | varchar(255) | YES | - |  | 字段CSS id | 字段CSS id |
| cssClass | varchar(255) | YES | - |  | 字段CSS class | 字段CSS class |
| cssStyle | varchar(255) | YES | - |  | 字段CSS style | 字段CSS style |
| type | varchar(255) | YES | - |  | 字段类型 | 字段类型 |
| render | varchar(4096) | YES | - |  | 字段处理 | 字段处理 |
| sort | int(11) | YES | 0 |  | 排序 | 排序 |
| orderable | bit(1) | YES | b'1' |  | 允许排序 | 允许排序 |
| searchable | bit(1) | YES | b'0' |  | 允许搜索 | 允许搜索 |
| visible | bit(1) | YES | b'1' |  | 允许可见 | 允许可见 |
| required | bit(1) | YES | b'0' |  | 必填 | 必填 |
| readonly | bit(1) | YES | b'0' |  | 只读 | 只读 |
| disabled | bit(1) | YES | b'0' |  | 组件失效 | 组件失效 |
| extData | varchar(8192) | YES | - |  | 外部数据 | 外部数据 |
| extKey | varchar(255) | YES | - |  | 外部数据key | 外部数据key |
| extValue | varchar(255) | YES | - |  | 外部数据value | 外部数据value |
| media | varchar(255) | YES | - |  | 传播媒介 | 传播媒介 |
| clazzName | varchar(255) | YES | - |  | 类名 | 类名 |
| superData | varchar(255) | YES | - |  | 父类dataName | 父类dataName |
| status | int(1) | YES | 1 |  | 状态 | 状态 |
| compId | int(11) | YES | - |  | 公司ID | 公司ID |
| isSystemField | bit(1) | YES | b'1' |  | 是否为系统字段 | 是否为系统字段 |
| createBy | varchar(25) | YES | - |  |  |  |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |
| updateBy | varchar(25) | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.35 t_data_operation -- 数据的导入导出控制表

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 控制数据导入导出操作的配置表 |
| 数据量 | ~14 行 |
| 数据大小 | 80.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键 |
| name | varchar(25) | NO |  |  | 操作名 | 操作名 |
| description | varchar(64) | NO |  |  | 操作描述 | 操作描述信息 |
| type | int(11) | NO | -1 |  | 操作类型，导入:1，导出:0 | 操作类型，导入:1，导出:0 |
| clazz | varchar(255) | NO |  |  | 操作所在类 | 操作所在类 |
| method | varchar(64) | NO |  |  | 操作类的方法 | 操作类的方法 |
| parameterTypes | varchar(512) | NO |  |  | 方法参数类型 | 方法参数类型 |
| formHtml | text | YES | - |  | 额外表单内容 | 额外表单内容 |
| script | text | YES | - |  | 导入时的js，导出时的sql | 导入时的js，导出时的sql |
| columns | varchar(4096) | NO |  |  | 导出时的列 | 导出时的列 |
| empPower | varchar(4096) | NO |  |  | 员工权限 | 员工权限 |
| depPower | varchar(4096) | NO |  |  | 部门权限 | 部门权限 |
| state | bit(1) | NO | b'1' |  | 状态 | 状态 |
| effectiveFrom | datetime | NO | CURRENT_TIMESTAMP |  |  |  |
| effectiveTo | datetime | YES | - |  |  |  |
| createBy | varchar(25) | NO |  |  |  |  |
| createTime | datetime | NO | CURRENT_TIMESTAMP |  |  |  |
| updateBy | varchar(25) | YES |  |  |  |  |
| updateTime | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.36 t_dictionary

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 系统数据字典，存储键值对配置 |
| 数据量 | ~4 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(16) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键 |
| dic_type_id | int(16) | NO | - |  | 字典类型id | 字典类型id |
| dic_type_name | varchar(32) | NO | - |  | 字典类型 | 字典类型 |
| dic_key | varchar(32) | NO | - |  | 字典key | 字典key |
| dic_value | varchar(32) | NO | - |  | 字典value | 字典value |
| customInfo | varchar(1024) | YES | - |  | 自定义属性 | 自定义属性 |
| sort | int(11) | NO | 0 |  | 排序 | 显示排序值 |
| status | int(1) | YES | 1 |  | 有效标志（1-有效，0-无效） | 有效标志（1-有效，0-无效） |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |
| updateTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.37 t_down_log

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 文件下载操作日志记录 |
| 数据量 | ~33 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 文件下载日志 | 自增主键 |
| fileIds | varchar(100) | YES | - |  | 文件对应ID | 文件对应ID |
| ip | varchar(25) | YES | - |  | 请求的IP地址 | 请求的IP地址 |
| timeline | int(11) | YES | - |  | 时间戳 | 时间戳 |
| downloadTime | datetime | YES | - |  | 下载时间 | 下载操作时间 |
| user | varchar(25) | YES | - |  | 用户名 | 用户名 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.38 t_file

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 系统文件存储记录 |
| 数据量 | ~291 行 |
| 数据大小 | 1.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 系统上传文件描述 | 文件唯一标识 |
| typeId | int(11) | YES | - |  | 对应file_type表的主键 | 对应file_type表的主键 |
| name | varchar(255) | YES | - |  | 文件名称 | 文件名称 |
| path | varchar(500) | YES | - |  | 文件存储路径 | 文件存储路径 |
| ext | varchar(50) | YES | - |  | 文件名后缀 | 文件名后缀 |
| size | int(11) | YES | - |  | 文件大小 | 文件大小 |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |
| createBy | varchar(15) | YES | - |  |  |  |
| downloadKey | varchar(255) | YES | - |  |  |  |
| dataType | varchar(64) | YES | - |  | 关联数据类型 | 关联数据类型 |
| dataId | int(11) | YES | - |  | 关联数据ID | 关联数据ID |
| customInfo | json | YES | - |  | 自定义信息 | 自定义信息 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.39 t_file_type

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 文件类型分类定义 |
| 数据量 | ~9 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 根据系统业务类型划分上传的文件分类 | 文件类型唯一标识 |
| name | varchar(25) | YES | - |  | 分类名称 | 分类名称 |
| limitSize | int(11) | YES | - |  | 大小限制 | 大小限制 |
| allowType | varchar(255) | YES | - |  | 文件类型限制 | 文件类型限制 |
| rename | tinyint(1) | YES | - |  | 是否进行重命名 | 是否进行重命名 |
| cut | tinyint(1) | YES | - |  | 是否进行压缩 | 是否进行压缩 |
| thumbnail | tinyint(1) | YES | - |  | 是否生成缩略图 | 是否生成缩略图 |
| dir | varchar(255) | YES | - |  | 服务器保存的相对路径 | 服务器保存的相对路径 |
| uploadUrl | varchar(255) | YES | - |  | 文件上传URL | 文件上传URL |
| code | varchar(64) | YES | - |  | 前端或后端调用代码 | 前端或后端调用代码 |
| createTime | datetime | YES | - |  |  |  |
| createBy | varchar(10) | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |
| updateBy | varchar(10) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.40 t_mails

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | MyISAM |
| 业务含义 | 系统发送的邮件记录 |
| 数据量 | ~10,817 行 |
| 数据大小 | 32.1 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 邮件记录唯一标识 |
| subject | varchar(255) | NO |  |  | 邮件主题 | 邮件主题 |
| content | text | NO | - |  | 邮件正文 | 邮件正文内容 |
| tos | text | YES | - |  | 邮件主送 | 邮件主送 |
| ccs | text | YES | - |  | 邮件抄送 | 邮件抄送 |
| bccs | text | YES | - |  | 邮件密送 | 邮件密送 |
| actualSendAddress | text | YES | - |  | 实际邮件发送地址 | 实际邮件发送地址 |
| attachFiles | text | YES | - |  | 邮件附件 以特殊符号间隔多个文件 | 邮件附件 以特殊符号间隔多个文件 |
| isInner | bit(1) | YES | b'0' |  | 是否为内部邮箱 | 是否为内部邮箱 |
| sendTime | datetime | YES | - |  | 邮件实际发送时间 | 邮件发送时间 |
| expectSendTime | datetime | YES | - |  | 邮件期望发送时间 | 邮件期望发送时间 |
| sendFlag | bit(1) | YES | b'0' |  | 邮件是否发送 1 为已发送 | 邮件是否发送 1 为已发送 |
| failedCount | int(2) | YES | 0 |  | 发送失败次数 | 发送失败次数 |
| createBy | varchar(25) | YES |  |  |  |  |
| createTime | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.41 t_menu

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 系统菜单定义，树形结构 |
| 数据量 | ~37 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 用户菜单定义 | 菜单唯一标识 |
| pid | int(11) | NO | 0 |  | 父菜单ID | 父菜单ID |
| name | varchar(100) | YES |  |  | 菜单名称 | 菜单名称 |
| url | varchar(100) | YES |  |  | 超链接 | 超链接 |
| icon | varchar(64) | YES |  |  | 菜单对应的class样式，会影响菜单的显示效果 | 菜单对应的class样式，会影响菜单的显示效果 |
| sort | int(11) | YES | 0 |  | 子菜单排序 | 子菜单排序 |
| status | bit(1) | YES | b'1' |  | 是否有效，1：有效，0：失效 | 是否有效，1：有效，0：失效 |
| target | varchar(15) | YES | - |  |  |  |
| remark | varchar(255) | YES |  |  | 备注说明 | 备注说明 |
| create_by | varchar(25) | YES |  |  |  |  |
| crate_time | datetime | YES | - |  |  |  |
| update_by | varchar(25) | YES |  |  |  |  |
| update_time | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.42 t_notify_template -- 消息模板（邮件、短信等）

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 邮件和消息通知的内容模板 |
| 数据量 | ~11 行 |
| 数据大小 | 96.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 模板唯一标识 |
| templateCode | varchar(64) | YES | - | UNI |  | 模板业务编码，唯一标识 |
| subject | varchar(64) | YES | - |  | 主题 | 主题 |
| content | text | YES | - |  | 内容 | 内容 |
| createBy | varchar(45) | YES | - |  |  |  |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |
| updateBy | varchar(45) | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |
| effectiveFrom | datetime | NO | CURRENT_TIMESTAMP |  |  | 模板有效期起始 |
| effectiveTo | datetime | YES | - |  |  | 模板有效期截止 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |
| templateCode | UNIQUE | templateCode | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| templateCode | BTREE | UNIQUE | templateCode |

---

### 2.43 t_permission

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 系统权限定义，细粒度操作权限 |
| 数据量 | ~115 行 |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| permission_id | int(11) | NO | - | PRI, AUTO_INCREMENT | 权限ID | 权限ID |
| permission_name | varchar(100) | YES |  | MUL | 权限字符串 | 权限字符串 |
| create_by | varchar(25) | YES |  |  |  |  |
| create_time | datetime | YES | - |  |  |  |
| update_by | varchar(25) | YES |  |  |  |  |
| update_time | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | permission_id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| permission_name | BTREE | NON-UNIQUE | permission_name |
| PRIMARY | BTREE | UNIQUE | permission_id |

---

### 2.44 t_resource

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 系统资源定义，权限控制的资源对象 |
| 数据量 | ~36 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 系统资源需要的权限定义 | 资源唯一标识 |
| url | varchar(100) | YES | - |  | 资源请求地址 | 资源请求地址 |
| authc | varchar(255) | YES | - |  | 需要的权限控制 ， 类似于 authc,roles[admin],perms[admin:create] | 需要的权限控制 ， 类似于 authc,roles[admin],perms[admin:create] |
| priority | int(11) | YES | 0 |  | 访问资源权限排序，越低越往后排 | 访问资源权限排序，越低越往后排 |
| remark | varchar(255) | YES | - |  | 关于资源定义的备注说明 | 关于资源定义的备注说明 |
| status | int(11) | YES | 1 |  | 数据有效性0 失效 1 有效 | 数据有效性0 失效 1 有效 |
| create_by | varchar(25) | YES | - |  |  |  |
| create_time | datetime | YES | - |  |  |  |
| update_by | varchar(25) | YES | - |  |  |  |
| update_time | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.45 t_role

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 系统角色定义，RBAC模型中的角色实体 |
| 数据量 | ~12 行 |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| role_id | int(11) | NO | - | PRI, AUTO_INCREMENT | 角色ID | 角色ID |
| role_name | varchar(100) | YES |  | MUL | 角色名称 | 角色名称 |
| role_name_zn | varchar(100) | YES |  |  | 中文别名 | 中文别名 |
| home_page | varchar(100) | YES |  |  | 角色默认主页 | 角色默认主页 |
| priority | int(11) | YES | 100 |  | 角色优先级，默认100，优先级最高 | 角色优先级，默认100，优先级最高 |
| status | smallint(1) | YES | 1 |  | 角色有效性，1有效，0无效 | 角色状态，1=启用，0=禁用 |
| create_by | varchar(25) | YES |  |  |  |  |
| create_time | datetime | YES | - |  |  |  |
| update_by | varchar(25) | YES |  |  |  |  |
| update_time | datetime | YES | - |  |  |  |
| remark | varchar(255) | YES |  |  | 备注说明 | 角色描述备注 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | role_id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | role_id |
| role_name | BTREE | NON-UNIQUE | role_name |

---

### 2.46 t_role_menu

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 角色与菜单的多对多关联关系 |
| 数据量 | ~122 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 角色菜单权限 | 自增主键 |
| role_id | int(11) | YES | - |  | 角色ID | 角色ID |
| menu_id | int(11) | YES | - |  | 菜单ID | 菜单ID |
| create_time | datetime | YES | - |  | 创建时间 | 创建时间 |
| create_by | varchar(25) | YES |  |  | 创建用户 | 创建用户 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.47 t_role_permission

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 角色与权限的多对多关联关系 |
| 数据量 | ~613 行 |
| 数据大小 | 80.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 角色-权限 一对多 | 自增主键 |
| role_id | int(11) | YES | - | MUL | 角色ID | 角色ID |
| permission_id | int(11) | YES | - | MUL | 权限ID | 权限ID |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |
| role_id | UNIQUE | role_id | None | None |
| role_id | UNIQUE | permission_id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| permission_id | BTREE | NON-UNIQUE | permission_id |
| PRIMARY | BTREE | UNIQUE | id |
| role_id | BTREE | UNIQUE | role_id, permission_id |

---

### 2.48 t_sync_log

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 数据同步操作日志，记录各外部系统数据同步的执行情况 |
| 数据量 | ~7,023 行 |
| 数据大小 | 2.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键 |
| targetMethod | varchar(255) | YES | - |  | 同步触发方法 | 同步触发方法 |
| tableObject | varchar(64) | NO | - |  | 同步的表实体 | 同步的表实体 |
| dataFrom | varchar(50) | YES | - |  | 同步数据源 | 同步数据源 |
| dataTo | varchar(50) | YES | - |  | 同步目标数据源 | 同步目标数据源 |
| syncParams | varchar(2048) | YES | - |  | 增量同步时的参数 | 增量同步时的参数 |
| syncStartTime | datetime | YES | - |  | 同步开始时间 | 同步开始时间 |
| syncEndTime | datetime | YES | CURRENT_TIMESTAMP |  | 同步结束时间 | 同步结束时间 |
| isSuccess | tinyint(1) | NO | 0 |  | 同步成功与否 | 同步成功与否 |
| dataCount | int(11) | YES | 0 |  | 同步记录数 | 同步记录数 |
| exception | text | YES | - |  | 同步失败异常信息 | 同步失败异常信息 |
| syncType | smallint(1) | NO | 0 |  | 0：复制，1：全量，2：增量 | 同步操作类型（如SAP订单同步、SMS项目同步等） |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.49 t_sync_state -- 保存增量同步时的状态，上一次同步时间，id，或者记录数

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 增量同步状态记录，保存上次同步位置信息 |
| 数据量 | ~5 行 |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键 |
| tableObject | varchar(25) | NO | - | UNI | 表的对象 | 表的对象 |
| lastId | varchar(25) | NO |  |  | 上一次同步的最后一个主键 | 上一次同步的最后一个主键 |
| lastSyncTime | datetime | YES | - |  | 上一次同步时间 | 增量同步的起始时间点 |
| offset | int(11) | NO | 0 |  | 上一次同步的记录数 | 上一次同步的记录数 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |
| tableObject | UNIQUE | tableObject | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| tableObject | BTREE | UNIQUE | tableObject |

---

### 2.50 t_sys_log

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | MyISAM |
| 业务含义 | 系统操作日志，记录用户关键操作 |
| 数据量 | ~61,733 行 |
| 数据大小 | 358.1 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(32) unsigned | NO | - | PRI, AUTO_INCREMENT |  | 日志唯一标识 |
| description | varchar(8000) | YES | - | MUL | 日志描述 | 日志描述 |
| method | varchar(200) | YES | - |  | 调用的方法 | 调用的方法 |
| type | varchar(25) | YES | - | MUL | 0-正常日志，1-异常 | 0-正常日志，1-异常 |
| request_ip | varchar(256) | YES | - |  | 请求者IP | 请求者IP |
| exception_code | varchar(256) | YES | - |  | 异常编码 | 异常编码 |
| exception_detail | mediumtext | YES | - |  | 异常详情 | 异常详情 |
| params | mediumtext | YES | - |  | 参数（json格式） | 参数（json格式） |
| create_by | varchar(256) | YES | - | MUL | 操作人 | 操作人 |
| create_date | datetime | YES | - | MUL | 操作日期 | 操作日期 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| create_by | BTREE | NON-UNIQUE | create_by |
| create_date | BTREE | NON-UNIQUE | create_date, description |
| description | BTREE | NON-UNIQUE | description |
| PRIMARY | BTREE | UNIQUE | id |
| type | BTREE | NON-UNIQUE | type, create_by |

---

### 2.51 t_sys_variable

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 系统配置变量，键值对形式存储系统参数 |
| 数据量 | ~51 行 |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | MUL, AUTO_INCREMENT |  | 自增主键 |
| code | varchar(64) | NO | - | PRI | 系统参数编码 | 系统变量编码，如'sys.cache.latest.refreshTime' |
| var | varchar(4096) | YES | - |  | 系统参数值 | 变量的值 |
| remark | varchar(255) | YES | - |  | 备注 | 备注 |
| createBy | varchar(25) | YES | - |  |  |  |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |
| updateBy | varchar(25) | YES | - |  |  |  |
| updateTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |
| effectiveFrom | datetime | YES | - |  |  |  |
| effectiveTo | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | code | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| id | BTREE | NON-UNIQUE | id |
| PRIMARY | BTREE | UNIQUE | code |

---

### 2.52 t_user

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 系统用户账号表，存储用户登录凭证和基本信息 |
| 数据量 | ~189 行 |
| 数据大小 | 64.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| user_id | int(11) | NO | - | PRI, AUTO_INCREMENT | 用户ID | 用户ID |
| user_name | varchar(25) | YES |  | UNI | 用户名称 | 用户名称 |
| password | varchar(100) | YES | - |  | 密码 | 加密后的登录密码 |
| create_by | varchar(25) | YES |  |  |  |  |
| create_time | datetime | NO | CURRENT_TIMESTAMP |  |  |  |
| update_by | varchar(25) | YES |  |  |  |  |
| update_time | datetime | YES | - |  |  |  |
| status | smallint(1) | NO | 1 |  | 用户状态，0：失效，1有效，2：锁定 | 用户状态，1=启用，0=禁用 |
| needChangePwd | bit(1) | NO | b'1' |  | 用户创建后需要修改密码判断 | 用户创建后需要修改密码判断 |
| loginErrorCount | int(1) | NO | 0 |  | 用户密码输入错误次数 | 用户密码输入错误次数 |
| isSysUser | smallint(1) | NO | 0 |  | 是否为系统用户,0为普通用户 | 是否为系统用户,0为普通用户 |
| userCustom1 | varchar(50) | YES |  |  | 用户自定义字段1 | 用户自定义字段1 |
| userCustom2 | varchar(50) | YES |  |  | 用户自定义字段2 | 用户自定义字段2 |
| userCustom3 | varchar(50) | YES |  |  | 用户自定义字段3 | 用户自定义字段3 |
| userCustom4 | int(11) | YES | 0 |  | 用户自定义字段4 | 用户自定义字段4 |
| userCustom5 | int(11) | YES | 0 |  | 用户自定义字段5 | 用户自定义字段5 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | user_id | None | None |
| unique_username | UNIQUE | user_name | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | user_id |
| unique_username | BTREE | UNIQUE | user_name |

---

### 2.53 t_user_info

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 用户扩展信息表，与t_user一对一关联 |
| 数据量 | ~189 行 |
| 数据大小 | 176.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 员工ID，外键 | 逻辑外键 -> t_user.id |
| workNo | varchar(25) | NO |  | MUL | 工号 | 工号 |
| realName | varchar(50) | NO |  |  | 姓名 | 用户真实姓名 |
| eName | varchar(50) | YES |  |  | 英文名 | 英文名 |
| compID | int(11) | NO | 0 | MUL | 公司ID | 公司ID |
| depID | int(11) | NO | 0 | MUL | 部门ID | 部门ID |
| jobID | int(11) | NO | 0 | MUL | 岗位ID | 岗位ID |
| reportTo | int(11) | YES | - | MUL | 直接上级 | 直接上级 |
| wfreportTo | int(11) | YES | - | MUL | 职能上级 | 职能上级 |
| empStatus | int(11) | NO | 1 |  | 员工状态，1：在职，2：离职 | 员工状态，1：在职，2：离职 |
| jobStatus | int(11) | YES | - |  | 岗位状态 | 岗位状态 |
| empType | int(11) | YES | - |  | 聘用类型：1：正式，3：实习生 | 聘用类型：1：正式，3：实习生 |
| sex | smallint(1) | YES | - |  | 性别：1：男，0：女 | 性别：1：男，0：女 |
| birthday | date | YES | - |  | 生日 | 生日 |
| email | varchar(50) | YES | - |  | 邮箱 | 用户邮箱地址 |
| mobile | varchar(50) | YES | - |  | 手机 | 手机 |
| telphone | varchar(50) | YES | - |  | 座机 | 座机 |
| avatar | varchar(500) | YES | - |  | 头像 | 头像 |
| remark | varchar(100) | YES | - |  | 备注 | 备注 |
| state | int(11) | YES | 1 |  | 状态 | 状态 |
| user_id | int(11) | YES | - | MUL | userId | userId |
| custom1 | int(11) | YES | - |  | 预留字段1 | 预留字段1 |
| custom2 | int(11) | YES | - |  | 预留字段2 | 预留字段2 |
| custom3 | varchar(50) | YES | - |  | 预留字段3 officeCode | 预留字段3 officeCode |
| custom4 | varchar(50) | YES | - |  | 预留字段4 projectTypes | 预留字段4 projectTypes |
| custom5 | varchar(4096) | YES | - |  | 预留字段5 areaPower | 预留字段5 areaPower |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| fk_userInfo_userId | FOREIGN KEY | user_id | t_user | user_id |
| fk_userInfo_userId | FOREIGN KEY | user_id | None | None |
| fk_userInfo_userId | FOREIGN KEY | compID | None | None |
| PRIMARY | PRIMARY KEY | id | None | None |
| fk_userInfo_userId | UNIQUE | user_id | t_user | user_id |
| fk_userInfo_userId | UNIQUE | user_id | None | None |
| fk_userInfo_userId | UNIQUE | compID | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| compID | BTREE | NON-UNIQUE | compID |
| depID | BTREE | NON-UNIQUE | depID |
| fk_userInfo_userId | BTREE | UNIQUE | user_id, compID |
| jobID | BTREE | NON-UNIQUE | jobID |
| PRIMARY | BTREE | UNIQUE | id |
| reportTo | BTREE | NON-UNIQUE | reportTo |
| wfreportTo | BTREE | NON-UNIQUE | wfreportTo |
| workNo | BTREE | NON-UNIQUE | workNo |

**外键列表**

| 外键名 | 本表字段 | 引用表 | 引用字段 |
|--------|----------|--------|----------|
| fk_userInfo_userId | user_id | t_user | user_id |

---

### 2.54 t_user_login_record

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 用户登录系统的历史记录 |
| 数据量 | ~18,952 行 |
| 数据大小 | 1.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键 |
| loginName | varchar(64) | YES | - |  | 登录用户名 | 登录用户名 |
| loginTime | datetime | NO | CURRENT_TIMESTAMP |  | 登录时间 | 登录操作时间 |
| loginIP | varchar(64) | YES | - |  | 登录IP | 登录IP |
| logoutTime | datetime | YES | - |  | 登出时间 | 登出操作时间 |
| logoutIP | varchar(64) | YES | - |  | 登出IP | 登出IP |
| loginSuccess | tinyint(1) | NO | 0 |  | 登录状态 | 登录状态 |
| logoutSuccess | tinyint(1) | YES | - |  | 登出状态 | 登出状态 |
| userId | int(11) | YES | - |  | user表id | 逻辑外键 -> t_user.id |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.55 t_user_role

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 用户与角色的多对多关联关系 |
| 数据量 | ~551 行 |
| 数据大小 | 96.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 用户-角色  一对多 | 自增主键 |
| user_id | int(11) | NO | - | MUL | 用户ID | 用户ID |
| role_id | int(11) | NO | - | MUL | 角色ID | 角色ID |
| comp_id | int(11) | YES | - |  | 公司ID | 公司ID |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| t_user_role_ibfk_1 | FOREIGN KEY | user_id | t_user | user_id |
| t_user_role_ibfk_2 | FOREIGN KEY | role_id | t_role | role_id |
| PRIMARY | PRIMARY KEY | id | None | None |
| unique_userId_roleId | UNIQUE | user_id | None | None |
| unique_userId_roleId | UNIQUE | role_id | None | None |
| unique_userId_roleId | UNIQUE | comp_id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| t_user_role_ibfk_2 | BTREE | NON-UNIQUE | role_id |
| unique_userId_roleId | BTREE | UNIQUE | user_id, role_id, comp_id |
| user_id | BTREE | NON-UNIQUE | user_id |

**外键列表**

| 外键名 | 本表字段 | 引用表 | 引用字段 |
|--------|----------|--------|----------|
| t_user_role_ibfk_1 | user_id | t_user | user_id |
| t_user_role_ibfk_2 | role_id | t_role | role_id |

---

### 2.56 tb_sys_log

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | MyISAM |
| 数据量 | ~3,183,362 行 |
| 数据大小 | 209.7 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ID | int(32) unsigned | NO | - | PRI, AUTO_INCREMENT |  |  |
| USER_NAME | varchar(80) | NO |  | MUL |  |  |
| IP | char(20) | NO | 0 |  |  |  |
| ACTION | varchar(1024) | NO |  |  |  |  |
| RESULT | int(32) unsigned | NO | 0 |  |  |  |
| INFO | varchar(20000) | NO |  |  |  |  |
| TIME | int(32) | NO | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | ID | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | ID |
| USER_NAME | BTREE | NON-UNIQUE | USER_NAME |

---

### 2.57 transnum

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| transNum | int(50) | YES | - |  |  |  |

---

### 2.58 user -- 用户

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 用户 |
| 数据量 | ~72 行 |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(8) | NO | - | PRI, AUTO_INCREMENT |  |  |
| username | varchar(20) | YES | - | UNI | 工号 | 工号 |
| password | varchar(32) | YES | - |  | 密码 | 密码 |
| role | int(1) | YES | - |  | 4：超级管理员；3管理员；1：普通用户 | 4：超级管理员；3管理员；1：普通用户 |
| mail | varchar(100) | YES | - |  | 邮箱 | 邮箱 |
| lastLogin | datetime | YES | - |  | 上次登陆时间 | 上次登陆时间 |
| department | varchar(50) | YES | - |  | 部门 | 部门 |
| name | varchar(50) | YES | - |  | 姓名 | 姓名 |
| tel | varchar(50) | YES | - |  | 电话 | 电话 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |
| username | UNIQUE | username | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| username | BTREE | UNIQUE | username |

---

### 2.59 user_info -- 用户

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 用户 |
| 数据量 | ~190 行 |
| 数据大小 | 96.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(8) | NO | - | PRI, AUTO_INCREMENT |  |  |
| username | varchar(20) | YES | - | UNI | 工号 | 工号 |
| password | varchar(32) | YES | - |  | 密码 | 密码 |
| role | int(1) | YES | - |  | 1：普通用户；2：07库；3：技服；4：管理员 5：供应链 | 1：普通用户；2：07库；3：技服；4：管理员 5：供应链 |
| mail | varchar(100) | YES | - |  | 邮箱 | 邮箱 |
| lastLogin | datetime | YES | - |  | 上次登陆时间 | 上次登陆时间 |
| department | varchar(50) | YES | - |  | 部门 | 部门 |
| realname | varchar(50) | YES | - |  | 姓名 | 姓名 |
| tel | varchar(50) | YES | - |  | 手机 | 手机 |
| state | int(1) | YES | 1 |  | 用户有效性 | 用户有效性 |
| title | varchar(25) | YES | - |  | 职称 | 职称 |
| office | varchar(25) | YES | - |  | 所在区域办事处 | 所在区域办事处 |
| office_addr | text | YES | - |  | 办事处地址 | 办事处地址 |
| guhua | varchar(15) | YES | - |  | 固话 | 固话 |
| fax | varchar(25) | YES | - |  | 传真 | 传真 |
| whs_code | varchar(255) | YES | - |  | 库房信息 | 库房信息 |
| pwd_over_due_date | datetime | YES | - |  | 密码修改记录 | 密码修改记录 |
| teams | varchar(255) | YES | - |  | 所属团队 | 所属团队 |
| teamRole | varchar(255) | YES | - |  | 团队角色 | 团队角色 |
| province | varchar(255) | YES | - |  | 省份/直辖市 | 省份/直辖市 |
| city | varchar(255) | YES | - |  | 市 | 市 |
| district | varchar(255) | YES | - |  | 区 | 区 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |
| username | UNIQUE | username | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| username | BTREE | UNIQUE | username |

---

### 2.60 user_modules

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~23 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| menuCode | varchar(50) | YES | - |  |  |  |
| menuName | varchar(50) | YES | - |  |  |  |
| menuLevel | int(11) | YES | - |  |  |  |
| superId | int(11) | YES | - |  | 父菜单ID | 父菜单ID |
| effectiveFrom | datetime | YES | - |  |  |  |
| effectiveTo | datetime | YES | - |  |  |  |
| createBy | varchar(25) | YES | - |  |  |  |
| createTime | datetime | YES | - |  |  |  |
| updateBy | varchar(25) | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.61 user_permissions

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~2,744 行 |
| 数据大小 | 256.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| username | varchar(10) | YES | - |  |  |  |
| permissionKey | varchar(50) | YES | - |  |  |  |
| permissionValue | int(1) | YES | - |  |  |  |
| menuName | varchar(50) | YES | - |  |  |  |
| menuLevel | int(1) | YES | - |  |  |  |
| effectiveFrom | datetime | YES | - |  |  |  |
| effectiveTo | datetime | YES | - |  |  |  |
| createdBy | varchar(25) | YES | - |  |  |  |
| createdTime | datetime | YES | - |  |  |  |
| updateBy | varchar(25) | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.62 user_team

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~34 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| realname | varchar(255) | YES | - |  |  |  |
| teamRole | varchar(255) | YES | - |  |  |  |
| mail | varchar(255) | YES | - |  |  |  |
| tel | varchar(255) | YES | - |  |  |  |
| department | varchar(255) | YES | - |  |  |  |
| province | varchar(255) | YES | - |  |  |  |
| addr | varchar(255) | YES | - |  |  |  |

---

# 第三章 视图

### 3.1 dp_v_spms_department -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| ocrCode | varchar(25) | NO | - |  |  |  |
| ocrName | varchar(25) | NO | - |  |  |  |
| isparam | int(11) | YES | - |  |  |  |

---

### 3.2 dp_v_spms_item_basic_info -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| itemCode | varchar(25) | YES | - |  |  |  |
| itemName | varchar(255) | YES | - |  |  |  |

---

### 3.3 dp_v_spms_rma_remind -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| spare_serialNum | varchar(50) | YES | - |  | 备件序列号 | 备件序列号 |
| sheetID | varchar(25) | YES | - |  | RMA申请单据代码 | RMA申请单据代码 |
| back_type | varchar(50) | YES | - |  |  |  |
| item_name | varchar(255) | YES | - |  | 物料名称 | 物料名称 |
| project_name | varchar(255) | YES | - |  | 项目名称 | 项目名称 |
| problem_desc | text | YES | - |  | 问题描述 | 问题描述 |
| conk_out_time | varchar(25) | YES | - |  | 故障发生时间 | 故障发生时间 |
| approve_time | datetime | YES | - |  | 审批时间 | 审批时间 |

---

### 3.4 pm_order_data_from_sap -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | 0 |  |  |  |
| orderNumber | varchar(25) | YES | - |  |  |  |
| contractNo | varchar(50) | YES | - |  |  |  |
| orderExecNumber | varchar(50) | YES | - |  |  |  |
| orderCreateTime | datetime | YES | - |  |  |  |
| customerRequireTime | datetime | YES | - |  |  |  |
| customerCode | varchar(55) | YES | - |  |  |  |
| customerName | varchar(255) | YES | - |  |  |  |
| projectName | varchar(255) | YES | - |  |  |  |
| orderComment | varchar(2048) | YES | - |  |  |  |
| orderType | int(11) | YES | 0 |  | 0 正常销售 1 退货 | 0 正常销售 1 退货 |
| compCode | varchar(25) | YES | 0 |  | 公司编码 | 公司编码 |
| salesType | varchar(25) | YES | 01 |  | 销售类型,01:正常合同，02:借转销，14:销售类借货 | 销售类型,01:正常合同，02:借转销，14:销售类借货 |
| source | varchar(25) | YES | SAP |  | 数据来源，默认:SAP,D365,总代借货:SMS | 数据来源，默认:SAP,D365,总代借货:SMS |
| customInfo | json | YES | - |  | 自定义字段 | 自定义字段 |
| syncTime | datetime | YES | - |  |  |  |

---

### 3.5 pm_order_data_from_sap_source -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | 0 |  |  |  |
| orderNumber | varchar(25) | YES | - |  |  |  |
| contractNo | varchar(50) | YES | - |  |  |  |
| orderExecNumber | varchar(50) | YES | - |  |  |  |
| orderExecNumberShort | varchar(50) | YES | - |  |  |  |
| orderCreateTime | datetime | YES | - |  |  |  |
| customerRequireTime | datetime | YES | - |  |  |  |
| customerCode | varchar(55) | YES | - |  |  |  |
| customerName | varchar(255) | YES | - |  |  |  |
| projectName | varchar(255) | YES | - |  |  |  |
| orderComment | varchar(2048) | YES | - |  |  |  |
| orderType | int(11) | YES | 0 |  | 0 正常销售 1 退货 | 0 正常销售 1 退货 |
| compCode | varchar(25) | YES | 0 |  | 公司编码 | 公司编码 |
| salesType | varchar(25) | YES | 01 |  | 销售类型,01:正常合同，02:借转销，14:销售类借货 | 销售类型,01:正常合同，02:借转销，14:销售类借货 |
| source | varchar(25) | YES | SAP |  | 数据来源，默认:SAP,D365,总代借货:SMS | 数据来源，默认:SAP,D365,总代借货:SMS |
| customInfo | json | YES | - |  | 自定义字段 | 自定义字段 |
| syncTime | datetime | YES | - |  |  |  |

---

### 3.6 pm_order_line_from_sap -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | 0 |  |  |  |
| orderNumber | varchar(25) | YES | - |  |  |  |
| lineNum | varchar(25) | YES | - |  |  |  |
| itemCode | varchar(25) | YES | - |  |  |  |
| itemDesc | varchar(255) | YES | - |  |  |  |
| orderQuantity | int(11) | YES | - |  |  |  |
| openQuantity | int(11) | YES | - |  |  |  |
| bundleCode | varchar(25) | YES | - |  |  |  |
| warrantyMonth | int(11) | YES | - |  |  |  |
| lineType | int(11) | YES | 0 |  |  |  |
| compCode | varchar(25) | YES | 0 |  | 公司编码 | 公司编码 |
| profitCenter | varchar(25) | YES | - |  | 利润中心 | 利润中心 |
| realOrderExecNumber | varchar(25) | YES | - |  | 真实执行单号 | 真实执行单号 |
| source | varchar(25) | YES | SAP |  | 数据来源，默认:SAP,D365,总代借货:SMS | 数据来源，默认:SAP,D365,总代借货:SMS |
| customInfo | json | YES | - |  | 自定义字段 | 自定义字段 |
| syncTime | datetime | YES | - |  |  |  |

---

### 3.7 pm_order_line_from_sap_source -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | 0 |  |  |  |
| orderNumber | varchar(25) | YES | - |  |  |  |
| lineNum | varchar(25) | YES | - |  |  |  |
| itemCode | varchar(25) | YES | - |  |  |  |
| itemDesc | varchar(255) | YES | - |  |  |  |
| orderQuantity | int(11) | YES | - |  |  |  |
| openQuantity | int(11) | YES | - |  |  |  |
| bundleCode | varchar(25) | YES | - |  |  |  |
| warrantyMonth | int(11) | YES | - |  |  |  |
| lineType | int(11) | YES | 0 |  |  |  |
| compCode | varchar(25) | YES | 0 |  | 公司编码 | 公司编码 |
| profitCenter | varchar(25) | YES | - |  | 利润中心 | 利润中心 |
| realOrderExecNumber | varchar(25) | YES | - |  | 真实执行单号 | 真实执行单号 |
| source | varchar(25) | YES | SAP |  | 数据来源，默认:SAP,D365,总代借货:SMS | 数据来源，默认:SAP,D365,总代借货:SMS |
| customInfo | json | YES | - |  | 自定义字段 | 自定义字段 |
| syncTime | datetime | YES | - |  |  |  |

---

### 3.8 pm_project_header -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectId | int(11) | NO | 0 |  | 项目头信息主键,跟项目其他具体信息关联 | 项目头信息主键,跟项目其他具体信息关联 |
| projectType | varchar(45) | NO | 10 |  | 项目类型，用服售后:10，安服售后:afss，安服先行:afxx | 项目类型，用服售后:10，安服售后:afss，安服先行:afxx |
| projectCode | varchar(45) | NO | - |  | 项目名称 | 项目名称 |
| projectName | varchar(200) | YES | - |  | 项目名称 | 项目名称 |
| projectState | varchar(11) | YES | - |  | 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态 | 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态 |
| isback | varchar(11) | YES | 30 |  | 30表示创建项目，32表示指定项目经理，34表示填写渠道信息 ,40表示工程管理部不予跟踪处理 ，42 表示项目经理选择不予跟踪 | 30表示创建项目，32表示指定项目经理，34表示填写渠道信息 |
| column001 | varchar(255) | YES | - |  | 办事处编码 | 办事处编码 |
| column002 | varchar(255) | YES | - |  | 客户编码--ERP | 客户编码--ERP |
| column003 | varchar(255) | YES | - |  | 客户名称--ERP | 客户名称--ERP |
| column004 | varchar(255) | YES | - |  | 市场部编码 | 市场部编码 |
| column005 | varchar(255) | YES | - |  | 系统部ID | 系统部ID |
| column006 | varchar(255) | YES | - |  | 拓展部ID | 拓展部ID |
| column007 | varchar(255) | YES | - |  | 子行业ID | 子行业ID |
| column008 | varchar(255) | YES | - |  | 不予跟踪原因 notGrantTailCause | 不予跟踪原因 notGrantTailCause |
| column009 | datetime | YES | - |  | 订单创建时间 | 订单创建时间 |
| column010 | varchar(10) | YES | - |  | 项目类型 | 项目类型 |
| column011 | varchar(10) | YES | - |  | 项目分类 | 项目分类 |
| column012 | varchar(2) | YES | - |  | 项目实施方式 | 项目实施方式 |
| columno12_readonly | int(2) | YES | -1 |  | 项目实施方式是否可以修改 -1表示可以改 其他值表示readonly | 控制实施方式是否可以修改 从SMS系统刷过来的不可修改 表现为-1表示可以修改 其他值不可以修改 |
| column013 | varchar(255) | YES | - |  | 最终客户名称 | 最终客户名称 |
| column014 | text | YES | - |  | 回退说明 | 回退说明 |
| customerProjectName | varchar(255) | YES | - |  | 客户项目名称 | 客户项目名称 |
| salesType | varchar(25) | YES | 01 |  | 销售类型 | 销售类型 |
| majorProjectLevel | varchar(255) | YES | - |  | 重大项目级别 | 重大项目级别 |
| compId | int(2) | YES | 0 |  | 公司ID | 公司ID |
| createTime | datetime | YES | - |  | 记录数据创建时间 | 记录数据创建时间 |
| createBy | varchar(45) | YES | - |  | 记录数据创建用户 | 记录数据创建用户 |
| updateTime | datetime | YES | - |  | 记录数据最新更新时间 | 记录数据最新更新时间 |
| updateBy | varchar(45) | YES | - |  | 记录数据最新更新用户 | 记录数据最新更新用户 |
| effectiveFrom | datetime | YES | - |  | 数据有效性开始时间 | 数据有效性开始时间 |
| effectiveTo | datetime | YES | - |  | 数据有效性结束时间 | 数据有效性结束时间 |
| disabled | bit(1) | YES | b'0' |  | 数据是否失效 | 数据是否失效 |
| projectStartTime | datetime | YES | - |  | 项目开始实施时间 | 项目开始实施时间 |
| projectRefreshTime | datetime | YES | - |  | 项目相关数据最后编辑时间 | 项目相关数据最后编辑时间 |
| projectCloseTime | datetime | YES | - |  | 项目闭环时间点 | 项目闭环时间点 |
| customInfo | json | YES | - |  | 自定义信息 | 自定义信息 |
| customConfig | json | YES | - |  | 自定义配置 | 自定义配置 |

---

### 3.9 view_contract_collection_plan_4_crm -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contractNo | varchar(45) | YES | - |  | 合同号 | 合同号 |
| referenceEventName | varchar(255) | YES | - |  |  |  |
| referenceEvent | varchar(255) | YES | - |  | 字段属性1 | 字段属性1 |
| eventPlanHappenDate | datetime | YES | - |  | 款项计划发生日期 | 款项计划发生日期 |
| eventPlanHappenDateENG | datetime | YES | - |  | 工程计划发生日期 | 工程计划发生日期 |
| eventActualFinishDate | datetime | YES | - |  | 实际完成日期 | 实际完成日期 |

---

### 3.10 view_current_task -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| taskTypeId | varchar(25) | YES | - |  | 任务类型id，关联基础数据表 | 任务类型id，关联基础数据表 |
| projectId | int(11) | YES | - |  |  |  |
| taskTypeCode | varchar(45) | YES | - |  | 任务类型code，关联基础数据表 | 任务类型code，关联基础数据表 |

---

### 3.11 view_distinct_contract -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contractNo | varchar(50) | YES | - |  |  |  |
| orderCreateTime | datetime | YES | - |  |  |  |
| id | int(11) | YES | - |  |  |  |

---

### 3.12 view_ehr_department -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| depID | int(11) | NO | - |  | 部门ID，关联外键 | 部门ID，关联外键 |
| depCode | varchar(20) | YES | - |  | 部门编码 | 部门编码 |
| depName | varchar(100) | YES | - |  | 部门名称 | 部门名称 |
| depAbbr | varchar(100) | YES | - |  | 部门简称 | 部门简称 |
| compID | int(11) | YES | - |  | 公司ID，外键 | 公司ID，外键 |
| adminID | int(11) | YES | - |  | 上级ID | 上级ID |
| depGrade | int(11) | YES | - |  | 部门级别 | 部门级别 |
| depType | int(11) | YES | - |  | 部门类型 | 部门类型 |
| depProperty | int(11) | YES | - |  | 部门属性 | 部门属性 |
| depCost | int(11) | YES | - |  | 存在部门内分级计数用 | 存在部门内分级计数用 |
| director | int(11) | YES | - |  | 主管 | 主管 |
| director2 | int(11) | YES | - |  | 分管领导 | 分管领导 |
| depEmp | int(11) | YES | - |  |  |  |
| depNum | int(11) | YES | - |  |  |  |
| effectDate | datetime | YES | - |  | 生效时间 | 生效时间 |
| xOrder | varchar(20) | YES | - |  | 排序 | 排序 |
| isDisabled | bit(1) | YES | - |  | 失效状态 | 失效状态 |
| disabledDate | datetime | YES | - |  | 失效时间 | 失效时间 |
| remark | varchar(500) | YES | - |  | 备注 | 备注 |
| depCustom1 | int(11) | YES | - |  | 保留字段1 | 保留字段1 |
| depCustom2 | int(11) | YES | - |  | 保留字段2、部门秘书 | 保留字段2、部门秘书 |
| depCustom3 | int(11) | YES | - |  | 保留字段3 | 保留字段3 |
| depCustom4 | int(11) | YES | - |  | 保留字段4 | 保留字段4 |
| depCustom5 | int(11) | YES | - |  | 保留字段5 | 保留字段5 |
| directorWorkNo | varchar(100) | YES | - |  | 工号 | 工号 |
| directorName | varchar(200) | YES | - |  | 姓名 | 姓名 |
| directorJobID | int(11) | YES | - |  | 岗位ID | 岗位ID |
| directorWorkNo2 | varchar(100) | YES | - |  | 工号 | 工号 |
| directorName2 | varchar(200) | YES | - |  | 姓名 | 姓名 |
| directorJobID2 | int(11) | YES | - |  | 岗位ID | 岗位ID |
| depLV1ID | bigint(11) | YES | - |  |  |  |
| depLV1Code | varchar(20) | YES | - |  |  |  |
| depLV1Name | varchar(100) | YES | - |  |  |  |
| depLV2ID | bigint(11) | YES | - |  |  |  |
| depLV2Code | varchar(20) | YES | - |  |  |  |
| depLV2Name | varchar(100) | YES | - |  |  |  |
| depLV3ID | bigint(11) | YES | - |  |  |  |
| depLV3Code | varchar(20) | YES | - |  |  |  |
| depLV3Name | varchar(100) | YES | - |  |  |  |
| depAllName | varchar(304) | YES | - |  |  |  |
| compName | varchar(100) | YES | - |  | 公司名称 | 公司名称 |

---

### 3.13 view_ehr_department_struct -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| depID | int(11) | NO | - |  | 部门ID，关联外键 | 部门ID，关联外键 |
| depName | varchar(100) | YES | - |  | 部门名称 | 部门名称 |
| depGrade | int(11) | YES | - |  | 部门级别 | 部门级别 |
| depLV1ID | bigint(11) | YES | - |  |  |  |
| depLV1Code | varchar(20) | YES | - |  |  |  |
| depLV1Name | varchar(100) | YES | - |  |  |  |
| depLV2ID | bigint(11) | YES | - |  |  |  |
| depLV2Code | varchar(20) | YES | - |  |  |  |
| depLV2Name | varchar(100) | YES | - |  |  |  |
| depLV3ID | bigint(11) | YES | - |  |  |  |
| depLV3Code | varchar(20) | YES | - |  |  |  |
| depLV3Name | varchar(100) | YES | - |  |  |  |
| depLV4ID | bigint(11) | YES | - |  |  |  |
| depLV4Code | varchar(20) | YES | - |  |  |  |
| depLV4Name | varchar(100) | YES | - |  |  |  |
| depAllName | varchar(406) | YES | - |  |  |  |

---

### 3.14 view_ehr_employee -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| empID | int(11) | NO | - |  | 员工ID，外键 | 员工ID，外键 |
| workNo | varchar(100) | NO | - |  | 工号 | 工号 |
| name | varchar(200) | YES | - |  | 姓名 | 姓名 |
| eName | varchar(200) | YES | - |  | 英文名 | 英文名 |
| compID | int(11) | NO | - |  | 公司ID | 公司ID |
| depID | int(11) | NO | - |  | 部门ID | 部门ID |
| jobID | int(11) | NO | - |  | 岗位ID | 岗位ID |
| reportTo | int(11) | YES | - |  | 直接上级 | 直接上级 |
| wfreportTo | int(11) | YES | - |  | 职能上级 | 职能上级 |
| empStatus | int(11) | NO | - |  | 员工状态，1：在职，2：离职 | 员工状态，1：在职，2：离职 |
| jobStatus | int(11) | YES | - |  | 岗位状态 | 岗位状态 |
| empType | int(11) | YES | - |  | 聘用类型：1：正式，3：实习生 | 聘用类型：1：正式，3：实习生 |
| joinDate | datetime | YES | - |  | 加入公司日期 | 加入公司日期 |
| workBeginDate | datetime | YES | - |  | 工作开始日期 | 工作开始日期 |
| jobBeginDate | datetime | YES | - |  | 加入公司日期（未知） | 加入公司日期（未知） |
| pracBeginDate | datetime | YES | - |  | 实习开始时间 | 实习开始时间 |
| pracEndDate | datetime | YES | - |  | 实习结束时间 | 实习结束时间 |
| probBeginDate | datetime | YES | - |  |  |  |
| probEndDate | datetime | YES | - |  |  |  |
| leaveDate | datetime | YES | - |  | 离职时间 | 离职时间 |
| gender | int(11) | YES | - |  | 性别：1：男，2：女 | 性别：1：男，2：女 |
| email | varchar(500) | YES | - |  | 邮箱 | 邮箱 |
| mobile | varchar(50) | YES | - |  | 手机 | 手机 |
| officePhone | varchar(50) | YES | - |  | 座机 | 座机 |
| remark | varchar(100) | YES | - |  | 备注 | 备注 |
| disabled | int(11) | YES | 0 |  | 失效 | 失效 |
| empCustom1 | int(11) | YES | - |  | 预留字段1 | 预留字段1 |
| empCustom2 | int(11) | YES | - |  | 预留字段2 | 预留字段2 |
| empCustom3 | int(11) | YES | - |  | 预留字段3 | 预留字段3 |
| empCustom4 | varchar(50) | YES | - |  | 预留字段4 | 预留字段4 |
| empCustom5 | int(11) | YES | - |  | 预留字段5 | 预留字段5 |
| reportToWorkNo | varchar(100) | YES | - |  | 工号 | 工号 |
| reportToName | varchar(200) | YES | - |  | 姓名 | 姓名 |
| wfreportToWorkNo | varchar(100) | YES | - |  | 工号 | 工号 |
| wfreportToName | varchar(200) | YES | - |  | 姓名 | 姓名 |
| depGrade | int(11) | YES | - |  | 部门级别 | 部门级别 |
| depCode | varchar(20) | YES | - |  | 部门编码 | 部门编码 |
| depName | varchar(100) | YES | - |  | 部门名称 | 部门名称 |
| jobCode | varchar(10) | YES | - |  | 岗位编码 | 岗位编码 |
| jobName | varchar(100) | YES | - |  | 岗位名称 | 岗位名称 |
| depLV1ID | bigint(11) | YES | - |  |  |  |
| depLV1Code | varchar(20) | YES | - |  |  |  |
| depLV1Name | varchar(100) | YES | - |  |  |  |
| depLV2ID | bigint(11) | YES | - |  |  |  |
| depLV2Code | varchar(20) | YES | - |  |  |  |
| depLV2Name | varchar(100) | YES | - |  |  |  |
| depLV3ID | bigint(11) | YES | - |  |  |  |
| depLV3Code | varchar(20) | YES | - |  |  |  |
| depLV3Name | varchar(100) | YES | - |  |  |  |
| depAllName | varchar(304) | YES | - |  |  |  |
| compName | varchar(100) | YES | - |  | 公司名称 | 公司名称 |

---

### 3.15 view_ems_info_4_pm -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contract_code | varchar(25) | YES | - |  |  |  |
| emsNum | mediumtext | YES | - |  |  |  |
| receiveName | mediumtext | YES | - |  |  |  |
| emsCompany | mediumtext | YES | - |  |  |  |

---

### 3.16 view_pm_deliverable_4_sms -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contractNo | varchar(25) | YES | - |  |  |  |
| deliverableName | varchar(255) | YES | - |  | 交付件名称 | 交付件名称 |
| deliverablePath | varchar(255) | YES | - |  | 交付件路径 | 交付件路径 |
| smsDeliverType | varchar(255) | YES | - |  |  |  |
| uploadUser | varchar(174) | YES | - |  |  |  |
| uploadTime | datetime | YES | - |  | 上传时间 | 上传时间 |

---

### 3.17 view_presales_project_duration -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| presalesId | int(11) | NO | 0 |  | 售前项目主表 | 售前项目主表 |
| instId | varchar(64) | YES | - |  | activity工作流流程ID | activity工作流流程ID |
| applyDuration | varchar(128) | YES | - |  |  |  |
| totalDuration | varchar(216) | YES | - |  |  |  |
| serviceDuration | varchar(216) | YES | - |  |  |  |
| programDuration | varchar(216) | YES | - |  |  |  |
| testDuration | varchar(216) | YES | - |  |  |  |
| callbackDuration | varchar(216) | YES | - |  |  |  |
| serviceApproveDuration | varchar(216) | YES | - |  |  |  |

---

### 3.18 view_presales_project_duration_temp -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| presalesId | int(11) | NO | 0 |  | 售前项目主表 | 售前项目主表 |
| instId | varchar(64) | YES | - |  | activity工作流流程ID | activity工作流流程ID |
| applyDuration | bigint(25) | YES | - |  |  |  |
| totalDuration | bigint(26) | YES | - |  |  |  |
| allDuration | decimal(46,0) | YES | - |  |  |  |
| serviceDuration | decimal(46,0) | YES | - |  |  |  |
| programDuration | decimal(46,0) | YES | - |  |  |  |
| testDuration | decimal(46,0) | YES | - |  |  |  |
| callbackDuration | decimal(46,0) | YES | - |  |  |  |
| serviceApproveDuration | decimal(46,0) | YES | - |  |  |  |

---

### 3.19 view_prj_is_has_plan -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectId | int(11) | YES | - |  |  |  |

---

### 3.20 view_project_created_list -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectId | int(11) | NO | 0 |  | 项目头信息主键,跟项目其他具体信息关联 | 项目头信息主键,跟项目其他具体信息关联 |
| projectCode | varchar(45) | NO | - |  | 项目名称 | 项目名称 |
| rank | varchar(255) | YES | - |  |  |  |
| projectName | varchar(246) | YES | - |  |  |  |
| projectStateName | varchar(255) | YES | - |  |  |  |
| projectState | varchar(11) | YES | - |  | 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态 | 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态 |
| contractNo | text | YES | - |  |  |  |
| officeCode | varchar(255) | YES | - |  | 办事处编码 | 办事处编码 |
| officeName | varchar(20) | YES | - |  |  |  |
| salesManCode | varchar(45) | YES | - |  | 人员编码,外部人员为空 | 人员编码,外部人员为空 |
| salesManName | varchar(45) | YES | - |  | 人员名称 | 人员名称 |
| orderCreateTime | datetime | YES | - |  |  |  |
| serviceManager | varchar(45) | YES | - |  | 人员编码,外部人员为空 | 人员编码,外部人员为空 |
| serviceManagerName | varchar(45) | YES | - |  | 人员名称 | 人员名称 |
| projectManager | varchar(45) | YES | - |  | 人员编码,外部人员为空 | 人员编码,外部人员为空 |
| projectManagerName | varchar(45) | YES | - |  | 人员名称 | 人员名称 |
| currentTask | varchar(255) | YES | - |  |  |  |

---

### 3.21 view_project_info_4_ts -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectCode | varchar(64) | YES | - |  |  |  |
| projectName | varchar(255) | YES | - |  |  |  |
| contractNo | varchar(341) | YES | - |  |  |  |
| officeName | varchar(20) | YES | - |  |  |  |
| customerName | varchar(255) | YES | - |  |  |  |
| marketName | varchar(255) | YES | - |  |  |  |
| systemName | varchar(255) | YES | - |  |  |  |
| expendName | varchar(255) | YES | - |  |  |  |
| industryName | varchar(255) | YES | - |  |  |  |
| salesManCode | varchar(45) | YES | - |  |  |  |
| salesManName | varchar(68) | YES | - |  |  |  |
| salesManTel | varchar(45) | YES | - |  |  |  |
| salesManMail | varchar(100) | YES | - |  |  |  |
| smCode | varchar(45) | YES | - |  |  |  |
| smName | varchar(45) | YES | - |  |  |  |
| pmCode1 | varchar(45) | YES | - |  |  |  |
| pmName1 | varchar(45) | YES | - |  |  |  |
| pmCode2 | varchar(45) | YES | - |  |  |  |
| pmName2 | varchar(45) | YES | - |  |  |  |
| compId | int(11) | YES | - |  |  |  |
| compName | varchar(128) | YES | - |  |  |  |
| ssfsName | varchar(255) | YES | - |  |  |  |
| partnerChannel | varchar(45) | YES | - |  |  |  |
| projectType | varchar(4) | NO |  |  |  |  |
| finalCustomerName | varchar(255) | YES | - |  |  |  |
| customerProjectName | varchar(255) | YES | - |  |  |  |

---

### 3.22 view_project_info_list -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectId | int(11) | YES | - |  |  |  |
| projectCode | varchar(45) | YES | - |  |  |  |
| rank | varchar(255) | YES | - |  |  |  |
| projectName | varchar(255) | YES | - |  |  |  |
| projectStateName | varchar(255) | YES | - |  |  |  |
| projectState | varchar(255) | YES | - |  |  |  |
| contractNo | varchar(341) | YES | - |  |  |  |
| officeCode | varchar(255) | YES | - |  |  |  |
| officeName | varchar(20) | YES | - |  |  |  |
| salesManCode | varchar(45) | YES | - |  |  |  |
| salesManName | varchar(45) | YES | - |  |  |  |
| orderCreateTime | datetime | YES | - |  |  |  |
| systemName | varchar(255) | YES | - |  |  |  |
| serviceManager | varchar(45) | YES | - |  |  |  |
| serviceManagerName | varchar(45) | YES | - |  |  |  |
| projectManager | varchar(45) | YES | - |  |  |  |
| projectManagerName | varchar(45) | YES | - |  |  |  |
| currentTask | varchar(255) | YES | - |  |  |  |

---

### 3.23 view_project_maintenance_4_ts -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | 0 |  |  |  |
| projectId | int(11) | NO | - |  | 项目头信息主键 | 项目头信息主键 |
| projectCode | varchar(45) | NO |  |  | 项目名称 | 项目名称 |
| projectName | varchar(200) | YES |  |  | 项目名称 | 项目名称 |
| projectType | int(11) | NO | 10 |  | 项目类型，售前:20/售后:10 | 项目类型，售前:20/售后:10 |
| projectExecutionState | varchar(45) | YES |  |  | 项目实施状态 | 项目实施状态 |
| contractNo | varchar(255) | YES |  |  | 合同号 | 合同号 |
| officeCode | varchar(25) | YES | - |  | 办事处编码 | 办事处编码 |
| type | varchar(45) | YES | - |  | 任务性质 | 任务性质 |
| category | varchar(45) | YES | - |  | 任务分类 | 任务分类 |
| subCategory | varchar(45) | YES | - |  | 任务小类 | 任务小类 |
| processTime | datetime | YES | - |  | 处理时间 | 处理时间 |
| processDesc | varchar(1024) | YES | - |  | 事项描述 | 事项描述 |
| processStep | varchar(1024) | YES | - |  | 解决进展 | 解决进展 |
| remainProblem | varchar(1024) | YES | - |  | 遗留问题 | 遗留问题 |
| transitHour | float | YES | 0 |  | 在途耗时(h) | 在途耗时(h) |
| processHour | float | YES | 0 |  | 处理耗时(h) | 处理耗时(h) |
| itemModel | varchar(255) | YES | - |  | 产品型号 | 产品型号 |
| softVersion | varchar(255) | YES | - |  | 在网版本 | 在网版本 |
| enabledFeatures | varchar(255) | YES | - |  | 启用功能 | 启用功能 |
| customTos | varchar(512) | YES | - |  | 自定义主送 | 自定义主送 |
| customCcs | varchar(512) | YES | - |  | 自定义抄送 | 自定义抄送 |
| hasReport | bit(1) | NO | b'0' |  | 是否有巡检报告 | 是否有巡检报告 |
| quesnaireId | int(11) | YES | - |  | 问卷ID | 问卷ID |
| deliverFileIds | varchar(255) | YES |  |  | 交付件，fnd_files id | 交付件，fnd_files id |
| warrantyStatus | varchar(25) | YES | - |  | 维保状态 | 维保状态 |
| industryName | varchar(25) | YES | - |  | 行业 | 行业 |
| userOffice | varchar(25) | YES | - |  | 用户办事处 | 用户办事处 |
| remark | varchar(2048) | YES | - |  | 备注 | 备注 |
| createTime | datetime | YES | - |  | 创建时间 | 创建时间 |
| createBy | varchar(45) | YES | - |  | 创建用户 | 创建用户 |
| updateTime | datetime | YES | - |  | 最新更新时间 | 最新更新时间 |
| updateBy | varchar(45) | YES | - |  | 最新更新用户 | 最新更新用户 |
| officeName | varchar(20) | YES | - |  |  |  |
| userOfficeName | varchar(20) | YES | - |  |  |  |
| serviceManager | varchar(45) | YES | - |  | 人员名称 | 人员名称 |
| programManagerA | varchar(45) | YES | - |  | 人员名称 | 人员名称 |
| programManagerB | varchar(45) | YES | - |  | 人员名称 | 人员名称 |
| createUser | varchar(174) | YES | - |  |  |  |
| typeName | varchar(255) | YES | - |  |  |  |
| projectExecutionStateName | varchar(255) | YES | - |  |  |  |
| categoryName | varchar(258) | YES | - |  |  |  |
| subCategoryName | varchar(255) | YES | - |  |  |  |
| marketName | varchar(255) | YES | - |  |  |  |
| systemName | varchar(255) | YES | - |  |  |  |
| expendName | varchar(255) | YES | - |  |  |  |
| industryNameN | varchar(255) | YES | - |  |  |  |
| finalCustomerName | varchar(255) | YES | - |  | 最终客户名称 | 最终客户名称 |
| salerName | varchar(91) | YES | - |  |  |  |
| quesnaireResultHeaderId | int(11) | YES | - |  | 回访结果头信息Id | 回访结果头信息Id |
| 工程师技术能力 | longtext | YES | - |  |  |  |
| 服务水平及规范性 | longtext | YES | - |  |  |  |
| 服务及时性 | longtext | YES | - |  |  |  |
| warrantyStatusName | varchar(4) | YES | - |  |  |  |
| syncTime | datetime | NO | - |  |  |  |

---

### 3.24 view_project_shipment_4_license -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| barcode | varchar(50) | YES | - |  |  |  |
| contract_code | varchar(25) | YES | - |  |  |  |
| contract_type | varchar(25) | YES | - |  |  |  |
| project_num | varchar(25) | YES | - |  |  |  |
| project_name | varchar(512) | YES | - |  |  |  |
| custom_name | varchar(512) | YES | - |  |  |  |
| final_customer | varchar(512) | YES | - |  |  |  |
| office_name | varchar(25) | YES | - |  |  |  |
| delivery_time | bigint(11) | YES | - |  |  |  |
| order_num | varchar(32) | YES | - |  | 订单号 | 订单号 |

---

### 3.25 view_project_task_4_oss -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| taskId | int(11) | NO | 0 |  | 任务ID | 任务ID |
| projectCode | varchar(45) | YES | - |  | 项目名称 | 项目名称 |
| executeId | binary(0) | YES | - |  |  |  |
| contractId | varchar(45) | YES | - |  | 合同号 | 合同号 |
| nodeTypeCode | varchar(25) | YES | - |  | 任务类型id，关联基础数据表 | 任务类型id，关联基础数据表 |
| nodeBeginTime | datetime | YES | - |  | 工程计划发生日期 | 工程计划发生日期 |
| nodeEndTime | datetime | YES | - |  | 实际完成日期 | 实际完成日期 |
| dataUpdateTime | binary(0) | YES | - |  |  |  |
| nodeAttached | varchar(11) | YES | - |  | 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态 | 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态 |
| nodeRemark | varchar(255) | YES | - |  | 不予跟踪原因 notGrantTailCause | 不予跟踪原因 notGrantTailCause |
| updateTime | datetime | YES | - |  | 记录数据最新更新时间 | 记录数据最新更新时间 |
| effectiveTo | datetime | YES | - |  |  |  |

---

### 3.26 view_project_task_default_4_oss -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| taskId | binary(0) | YES | - |  |  |  |
| projectCode | varchar(45) | YES | - |  | 项目名称 | 项目名称 |
| executeId | binary(0) | YES | - |  |  |  |
| contractId | varchar(45) | NO | - |  | 合同号 | 合同号 |
| nodeTypeCode | varchar(11) | YES | - |  | 基础数据ID，对应fnd_basic_data | 基础数据ID，对应fnd_basic_data |
| nodeBeginTime | binary(0) | YES | - |  |  |  |
| nodeEndTime | binary(0) | YES | - |  |  |  |
| dataUpdateTime | binary(0) | YES | - |  |  |  |
| nodeAttached | varchar(11) | YES | - |  | 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态 | 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态 |
| nodeRemark | varchar(255) | YES | - |  | 不予跟踪原因 notGrantTailCause | 不予跟踪原因 notGrantTailCause |
| updateTime | binary(0) | YES | - |  |  |  |
| effectiveTo | binary(0) | YES | - |  |  |  |

---

### 3.27 view_project_waiting_list -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectName | varchar(255) | YES | - |  |  |  |
| projectStateName | varchar(255) | YES | - |  |  |  |
| projectState | varchar(255) | YES | - |  |  |  |
| contractNo | varchar(50) | YES | - |  |  |  |
| officeCode | varchar(15) | YES | - |  |  |  |
| officeName | varchar(20) | YES | - |  |  |  |
| salesManCode | varchar(45) | YES | - |  |  |  |
| salesManName | varchar(45) | YES | - |  |  |  |
| orderCreateTime | datetime | YES | - |  |  |  |
| systemName | varchar(255) | YES | - |  |  |  |

---

### 3.28 view_relation4contractno_marketcode -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contractNo | varchar(50) | YES | - |  |  |  |
| marketCode | varchar(64) | YES | - |  |  |  |
| marketName | varchar(255) | YES | - |  |  |  |
| systemId | varchar(64) | YES | - |  |  |  |
| systemName | varchar(255) | YES | - |  |  |  |

---

### 3.29 view_rma_txinfo -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| sheetID | varchar(25) | NO | - |  | RMA申请单据代码 | RMA申请单据代码 |
| spare_serialNum | varchar(50) | YES | - |  | 备件序列号 | 备件序列号 |
| instead_of_num | varchar(25) | YES | - |  | 好件替换坏件关系 | 好件替换坏件关系 |
| item_code | varchar(15) | YES | - |  | 物料号 | 物料号 |
| item_name | varchar(255) | YES | - |  | 物料名称 | 物料名称 |
| customer_name | varchar(255) | YES | - |  | 客户名称 | 客户名称 |
| contractNo | varchar(25) | YES | - |  | 合同号 | 合同号 |
| contractRemark | varchar(4096) | YES | - |  | 合同备注 | 合同备注 |
| project_name | varchar(255) | YES | - |  | 项目名称 | 项目名称 |
| back | varchar(10) | YES | - |  | 返回类型 | 返回类型 |
| serve | varchar(10) | YES | - |  | 服务类型 | 服务类型 |
| tain | varchar(10) | YES | - |  | 维保类型 | 维保类型 |
| data_state | char(1) | YES | 0 |  | 数据状态（0：最新；1：历史数据） | 数据状态（0：最新；1：历史数据） |
| department | varchar(50) | YES | - |  |  |  |
| application_time | datetime | YES | - |  | 申请发起时间 | 申请发起时间 |
| approve_time | datetime | YES | - |  | 审批时间 | 审批时间 |
| is_pass | varchar(2) | YES | - |  | 是否通过审批(0为未处理，1为通过，2为未通过 , -1 作废处理)  | 是否通过审批(0为未处理，1为通过，2为未通过 , -1 作废处理)  |
| applicant | varchar(10) | YES | - |  | 申请发起人 | 申请发起人 |
| EMS_num | varchar(255) | YES | - |  | 快递单号 | 快递单号 |
| EMS_company | varchar(255) | YES | - |  | 快递公司 | 快递公司 |
| addressee | varchar(25) | YES | - |  | 收件人 | 收件人 |
| send_time | datetime | YES | - |  | 出库时间 | 出库时间 |
| isBack | char(1) | YES | 0 |  | 坏件是否返回（0：未返回;1:已返回） | 坏件是否返回（0：未返回;1:已返回） |
| back_time | datetime | YES | - |  | 返回时间 | 返回时间 |
| doa_path | varchar(100) | YES | - |  | doa故障分析单（下载路径） | doa故障分析单（下载路径） |
| check_path | varchar(100) | YES | - |  | 检测报告(下载路径) | 检测报告(下载路径) |
| duty_person | varchar(10) | YES | - |  | 负责人 | 负责人 |
| isOK | char(1) | YES | 0 |  | 核销状态(0:未核销；1:已核销) | 核销状态(0:未核销；1:已核销) |
| hexiao_time | datetime | YES | - |  | 核销时间 | 核销时间 |
| problem_desc | text | YES | - |  | 问题描述 | 问题描述 |
| tx_id | int(11) | NO | 0 |  |  |  |

---

### 3.30 view_service -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | varchar(64) | YES | - |  |  |  |
| barcode | varchar(50) | YES | - |  |  |  |
| end_date | datetime | YES | - |  |  |  |

---

### 3.31 view_service_max -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| barcode | varchar(50) | YES | - |  |  |  |
| maxEndDate | datetime | YES | - |  |  |  |

---

### 3.32 view_shipment_4_sms -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contract_code | varchar(25) | YES | - |  |  |  |
| itemCode | varchar(16) | YES | - |  |  |  |
| barcode | varchar(50) | YES | - |  |  |  |
| itemCode2 | varchar(16) | YES | - |  | 母子公司发货物料编码对应关系 | 母子公司发货物料编码对应关系 |
| barcode2 | varchar(50) | YES | - |  | 母子公司发货序列号对应关系 | 母子公司发货序列号对应关系 |

---

### 3.33 view_shipment_ems_4_pm -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contract_code | varchar(25) | YES | - |  |  |  |
| receiveName | text | YES | - |  | 收件人 | 收件人 |
| emsNum | text | YES | - |  | 快递单号 | 快递单号 |
| packdate | datetime | YES | - |  |  |  |
| emsCompany | mediumtext | YES | - |  |  |  |
| packId | varchar(64) | YES | - |  |  |  |

---

### 3.34 view_shipment_info_4_pm -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| contract_code | varchar(25) | YES | - |  |  |  |
| itemCode | varchar(16) | YES | - |  |  |  |
| itemModel | varchar(255) | YES | - |  |  |  |
| itemName | varchar(255) | YES | - |  |  |  |
| barcode | varchar(50) | YES | - |  |  |  |
| comBarcode | varchar(50) | YES | - |  |  |  |
| packId | varchar(64) | YES | - |  |  |  |
| itemCode2 | varchar(16) | YES | - |  | 母子公司发货物料编码对应关系 | 母子公司发货物料编码对应关系 |
| itemModel2 | varchar(255) | YES | - |  |  |  |
| itemName2 | varchar(255) | YES | - |  |  |  |
| barcode2 | varchar(50) | YES | - |  | 母子公司发货序列号对应关系 | 母子公司发货序列号对应关系 |
| profitCenter | varchar(32) | YES | - |  | 利润中心 | 利润中心 |

---

### 3.35 view_soft_version -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| serial_number | varchar(100) | YES | - |  |  |  |
| conp | mediumtext | YES | - |  |  |  |
| cpld | mediumtext | YES | - |  |  |  |
| boot | mediumtext | YES | - |  |  |  |
| pcb | mediumtext | YES | - |  |  |  |

---

### 3.36 view_subcontract_project_4_sse -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | 0 |  |  |  |
| subcontractName | varchar(512) | YES |  |  | 转包名称 | 转包名称 |
| subcontractNo | varchar(64) | YES |  |  | 转包合同号 | 转包合同号 |
| contractNos | varchar(2048) | YES |  |  | 项目合同号 | 项目合同号 |
| projectIds | varchar(1024) | YES |  |  | 转包的项目ID | 转包的项目ID |
| type | int(11) | YES | - |  | 转包类型 | 转包类型 |
| state | int(11) | NO | 0 |  | 转包状态 | 转包状态 |
| callbackState | int(11) | YES | - |  | 回访状态 | 回访状态 |
| facilitatorId | int(11) | YES | - |  | 服务商表ID | 服务商表ID |
| facilitatorName | varchar(64) | YES |  |  | 服务商名 | 服务商名 |
| bankInfo | varchar(255) | YES |  |  | 服务商开户地址 | 服务商开户地址 |
| bankAccount | varchar(64) | YES |  |  | 服务商收款账户 | 服务商收款账户 |
| officeCode | varchar(25) | YES |  |  | 办事处部门 | 办事处部门 |
| profitDepCode | varchar(25) | YES |  |  | 收益部门 | 收益部门 |
| isAccrued | bit(1) | YES | - |  | 是否计提 | 是否计提 |
| isInvoiced | bit(1) | YES | - |  | 是否提供发票 | 是否提供发票 |
| subcontractAmount | varchar(25) | YES |  |  | 转包价 | 转包价 |
| reason | varchar(512) | YES |  |  | 转包原因 | 转包原因 |
| remark | varchar(512) | YES |  |  | 备注 | 备注 |
| effectiveForm | datetime | YES | - |  | 有效开始时间 | 有效开始时间 |
| effectiveTo | datetime | YES | - |  | 有效结束时间 | 有效结束时间 |
| zrApproveTime | datetime | YES | - |  | 最新主任审批通过时间 | 最新主任审批通过时间 |
| createBy | varchar(25) | YES |  |  |  |  |
| createTime | datetime | YES | - |  |  |  |
| updateBy | varchar(25) | YES |  |  |  |  |
| updateTime | datetime | YES | - |  |  |  |
| stateName | varchar(255) | YES | - |  |  |  |
| callbackStateName | varchar(255) | NO |  |  |  |  |
| createName | varchar(154) | YES | - |  |  |  |
| officeName | varchar(20) | YES | - |  |  |  |
| profitDepName | varchar(20) | YES | - |  |  |  |
| typeName | varchar(255) | YES | - |  |  |  |

---

### 3.37 view_txinfo -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| sheetID | varchar(15) | YES | - |  | 单据代码 | 单据代码 |
| beforeChange_sheetID | varchar(15) | YES | - |  | 发起转移申请之前的备件所涉及的单据号，此字段是为了保持备件经过转移流转后涉及单据号不变 | 发起转移申请之前的备件所涉及的单据号，此字段是为了保持备件经过转移流转后涉及单据号不变 |
| applicant | varchar(25) | YES | - |  | 申请人 | 申请人 |
| app_time | datetime | YES | - |  | 申请时间 | 申请时间 |
| app_dptNo | varchar(10) | YES | - |  | 申请办事处名称 | 申请办事处名称 |
| prt_name | varchar(255) | YES | - |  | 项目名称 | 项目名称 |
| app_reason | text | YES | - |  | 申请原因 | 申请原因 |
| promise_returntime | datetime | YES | - |  | 承诺备件归还时间 | 承诺备件归还时间 |
| trade_classify | varchar(100) | YES | - |  | 行业分类（手动填写） | 行业分类（手动填写） |
| signing_state | char(1) | YES | - |  | 签单状态（0：已签单；1：未签单） 废弃字段 | 签单状态（0：已签单；1：未签单） 废弃字段 |
| kept_place | varchar(10) | YES | - |  | 备件存放地 | 备件存放地 |
| demand_type | varchar(8) | YES | - |  |  |  |
| his_zipCode | varchar(25) | YES | - |  | 邮编 | 邮编 |
| his_addr | varchar(1024) | YES | - |  | 地址/where | 地址/where |
| addre_id | int(11) | YES | - |  | 关联收件人表ID | 关联收件人表ID |
| duty_person | varchar(10) | YES | - |  | 负责人 | 负责人 |
| spare_serialNum | varchar(50) | YES | - |  | 备件序列号 | 备件序列号 |
| start_use_time | datetime | YES | - |  | 开始使用时间 | 开始使用时间 |
| send_time | datetime | YES | - |  | 出库时间 | 出库时间 |
| EMS_num | varchar(255) | YES | - |  | 快递单号 | 快递单号 |
| EMS_company | varchar(255) | YES | - |  | 快递公司 | 快递公司 |
| item_code | varchar(25) | YES | - |  | 物料号 | 物料号 |
| item_name | varchar(255) | YES | - |  | 物料名称 | 物料名称 |
| isOK | char(1) | YES | - |  | 是否核销(是否核销，0为未核销，1为核销) | 是否核销(是否核销，0为未核销，1为核销) |
| remark | text | YES | - |  | 备注 | 备注 |
| tx_id | int(11) | NO | 0 |  |  |  |
| action_time | datetime | YES | - |  | 操作时间 | 操作时间 |
| shiftimes | int(11) | YES | - |  | 备件经过转移次数 | 备件经过转移次数 |
| turnovertimes | int(11) | YES | - |  |  |  |
| allottimes | int(11) | YES | - |  |  |  |
| take_place | varchar(15) | YES | 0 |  | 0:未选择 1:供应链 2：库存 | 0:未选择 1:供应链 2：库存 |
| approve_time | datetime | YES | - |  | 审批时间 | 审批时间 |
| receive_time | datetime | YES | - |  | 收货时间 | 收货时间 |
| sendout_whsCode | varchar(10) | YES | - |  | 备件发出库房 | 备件发出库房 |
| isNew | char(1) | YES | - |  | 数据状态(0:历史数据；1：最新数据 2:转移申请被转移状态) | 数据状态(0:历史数据；1：最新数据 2:转移申请被转移状态) |
| extend_returntime | datetime | YES | - |  | 延长归还时间 | 延长归还时间 |
| hexiao_time | datetime | YES | - |  | 核销时间 | 核销时间 |
| isUnion | int(11) | YES | - |  | 是否联合供应链发货 | 是否联合供应链发货 |

---

### 3.38 view_warranty_info_4_ts -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | bigint(20) | YES | - |  |  |  |
| projectName | varchar(512) | YES | - |  |  |  |
| contractNo | varchar(25) | YES | - |  |  |  |
| barcode | varchar(50) | YES | - |  |  |  |
| item | varchar(16) | YES | - |  |  |  |
| itemName | varchar(255) | YES | - |  |  |  |
| itemDesc | varchar(255) | YES | - |  |  |  |
| barcode2 | varchar(50) | YES | - |  | 母子公司发货序列号对应关系 | 母子公司发货序列号对应关系 |
| item2 | varchar(16) | YES | - |  | 母子公司发货物料编码对应关系 | 母子公司发货物料编码对应关系 |
| itemName2 | varchar(255) | YES | - |  |  |  |
| itemDesc2 | varchar(255) | YES | - |  |  |  |
| gradeName | varchar(125) | YES | - |  |  |  |
| warranty | varchar(2) | YES | - |  |  |  |
| warrantyStartTime | datetime | YES | - |  |  |  |
| warrantyEndTime | datetime | YES | - |  |  |  |
| diff | int(7) | YES | - |  |  |  |

---

### 3.39 view_warranty_source -- VIEW

| 属性 | 值 |
|------|-----|
| 对象类型 | VIEW |
| 业务含义 | VIEW |
| 数据大小 | 0 B |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | bigint(20) | YES | 0 |  |  |  |
| barcode | varchar(50) | YES | - |  |  |  |
| comBarCode | varchar(50) | YES | - |  |  |  |
| old_warrantyEndTime | datetime | YES | - |  |  |  |
| warrantyEndTime | datetime | YES | - |  |  |  |
| old_diff | int(7) | YES | - |  |  |  |
| diff | int(7) | YES | - |  |  |  |
| warrantyStartTime | datetime | YES | - |  |  |  |
| old_warrantyStartTime | datetime | YES | - |  |  |  |
| item | varchar(16) | YES | - |  |  |  |
| describe_ | varchar(255) | YES | - |  |  |  |
| itemName | varchar(255) | YES | - |  |  |  |
| gradeName | varchar(125) | YES | - |  |  |  |
| gradeCode | varchar(25) | YES | - |  |  |  |
| packdate | datetime | YES | - |  |  |  |
| contract_code | varchar(25) | YES | - |  |  |  |
| contract_type | int(11) | YES | - |  |  |  |
| contract_type_name | varchar(25) | YES | - |  |  |  |
| project_name | varchar(512) | YES | - |  |  |  |
| customer_name | varchar(512) | YES | - |  |  |  |
| office_code | varchar(25) | YES | - |  |  |  |
| office_name | varchar(25) | YES | - |  |  |  |
| marketCode | varchar(10) | YES | - |  |  |  |
| marketName | varchar(15) | YES | - |  |  |  |
| systemId | int(11) | YES | - |  |  |  |
| systemName | varchar(15) | YES | - |  |  |  |
| warranty | varchar(2) | YES | - |  |  |  |
| warrantyMonth | double | YES | - |  |  |  |
| barcode2 | varchar(50) | YES | - |  | 母子公司发货序列号对应关系 | 母子公司发货序列号对应关系 |
| item2 | varchar(16) | YES | - |  | 母子公司发货物料编码对应关系 | 母子公司发货物料编码对应关系 |
| syncTime | datetime | YES | - |  |  |  |

---

# 第四章 项目管理

### 4.1 addressee_info

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~2,935 行 |
| 数据大小 | 416.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| addre_id | int(11) | NO | - | PRI, AUTO_INCREMENT | ID | ID |
| username | varchar(25) | NO | - |  | 关联用户账号 | 关联用户账号 |
| addre_name | varchar(64) | YES | - |  | 收件人姓名 | 收件人姓名 |
| addre_tel | varchar(64) | YES | - |  | 收件人电话 | 收件人电话 |
| addre_mail | varchar(64) | YES | - |  | 收件人邮箱 | 收件人邮箱 |
| addr | varchar(1024) | YES | - |  | 地址/where | 地址/where |
| zip_code | varchar(10) | YES | - |  | 邮编 | 邮编 |
| company | varchar(64) | YES | - |  | 公司 | 公司 |
| depName | varchar(25) | YES | - |  | 部门 | 部门 |
| remark | text | YES | - |  | 备注 | 备注 |
| state | int(11) | YES | 1 |  | 状态（生效或失效） | 状态（生效或失效） |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | addre_id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | addre_id |

---

### 4.2 agent_info

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~35,204 行 |
| 数据大小 | 5.0 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| agent_id | int(10) | NO | - | PRI, AUTO_INCREMENT |  |  |
| id | varchar(16) | NO | - | MUL |  |  |
| name | varchar(64) | NO | - |  |  |  |
| type | int(8) | NO | - |  |  |  |
| level | varchar(64) | YES | - |  |  |  |
| enable | int(8) | NO | 1 |  |  |  |
| agent_version | int(8) | NO | 0 |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | agent_id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| index_id | BTREE | NON-UNIQUE | id |
| PRIMARY | BTREE | UNIQUE | agent_id |

---

### 4.3 back_type -- 返回类型

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 返回类型 |
| 数据量 | ~8 行 |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 主键 | 主键 |
| back | varchar(10) | NO | - | MUL |  |  |
| back_type | varchar(50) | YES | - |  |  |  |
| back_state | varchar(200) | YES | - |  |  |  |
| remark | text | YES | - |  | 备注 | 备注 |
| status | int(11) | YES | 1 |  | 有效状态0失效 1有效 | 有效状态0失效 1有效 |
| updateTime | datetime | YES | - |  | 更新时间 | 更新时间 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| back_where_index | BTREE | NON-UNIQUE | back |
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.4 fnd_act_hi_comment

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | Activiti工作流审批意见记录，被各业务模块共用 |
| 数据量 | ~36,824 行 |
| 数据大小 | 10.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | activity 审批意见表 | 自增主键，审批意见唯一标识 |
| objId | int(11) | YES | - | MUL | 业务ID | 关联各业务表的主键 |
| procdefKey | varchar(50) | YES | - |  | 流程类型 | 如CallBack、Presales等 |
| taskKey | varchar(50) | YES | - |  | 任务Key | Activiti任务定义Key |
| taskId | varchar(25) | YES | - | MUL | activity任务ID | Activiti任务ID |
| instId | varchar(25) | YES | - | MUL | 流程ID | Activiti流程实例ID |
| assignee | varchar(25) | YES | - | MUL | 办理人 | 逻辑外键 -> fnd_user_info.username |
| assigneeTime | datetime | YES | - |  | 办理时间 | 任务办理时间 |
| nextAssignee | varchar(25) | YES | - |  | 下一步办理人 | 下一步办理人 |
| nextAssigneeName | varchar(64) | YES | - |  | 下一步办理人姓名 | 下一步办理人姓名 |
| result | int(11) | YES | - |  | 审批结果 | 逻辑外键 -> fnd_basic_data(dataTypeCode=26) |
| message | text | YES | - |  | 审批意见 | 审批意见内容 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| assignee | BTREE | NON-UNIQUE | assignee, procdefKey |
| instId | BTREE | NON-UNIQUE | instId |
| objId | BTREE | NON-UNIQUE | objId, procdefKey |
| PRIMARY | BTREE | UNIQUE | id |
| taskId | BTREE | NON-UNIQUE | taskId |

---

### 4.5 fnd_basic_data -- 基础数据

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 系统基础数据字典，通过dataTypeCode区分不同数据类型 |
| 数据量 | ~480 行 |
| 数据大小 | 96.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，基础数据唯一标识 |
| dataTypeCode | varchar(45) | YES | - | MUL |  | 逻辑外键 -> fnd_basic_data_type.dataTypeCode |
| basicDataId | varchar(255) | YES | - | MUL |  | 数据项编码 |
| basicDataName | varchar(255) | YES | - |  |  | 数据项名称 |
| basicDataAttri1 | varchar(255) | YES | - |  | 字段属性1 | 字段属性1 |
| sortId | int(11) | YES | - |  | 查询排序字段数值越大越在前 | 查询排序字段数值越大越在前 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(45) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(45) | YES | - |  |  | 记录最新更新用户编码 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| basicDataId | BTREE | NON-UNIQUE | basicDataId |
| basicDataId_dataTypeCode | BTREE | NON-UNIQUE | dataTypeCode, basicDataId |
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.6 fnd_basic_data_type -- 基础数据类型

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 基础数据类型定义 |
| 数据量 | ~30 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，数据类型唯一标识 |
| dataTypeCode | varchar(45) | YES | - |  |  | 数据类型唯一编码 |
| dataTypeName | varchar(45) | YES | - |  |  | 数据类型名称 |
| status | int(11) | YES | - |  | 是否需要放在前台管理 | 是否需要放在前台管理 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(45) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(45) | YES | - |  |  | 记录最新更新用户编码 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.7 fnd_basic_prjstate

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目状态基础配置 |
| 数据量 | ~40 行 |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，项目状态唯一标识 |
| dataTypeCode | varchar(45) | YES | - | MUL | 数据类型编码，对应fnd_basic_data | 数据类型编码，对应fnd_basic_data |
| basicDataId | varchar(11) | YES | - |  | 基础数据ID，对应fnd_basic_data | 基础数据ID，对应fnd_basic_data |
| column010 | varchar(10) | YES | - |  | 项目类型，对应pm_project_header | 项目类型，对应pm_project_header |
| column011 | varchar(10) | YES | - |  | 项目类别，对应pm_project_header | 项目类别，对应pm_project_header |
| createTime | datetime | YES | - |  | 记录数据创建时间 | 记录创建时间 |
| createBy | varchar(45) | YES | - |  | 记录数据创建用户 | 记录创建用户编码 |
| updateTime | datetime | YES | - |  | 记录数据最新更新时间 | 记录最新更新时间 |
| updateBy | varchar(45) | YES | - |  | 记录数据最新更新用户 | 记录最新更新用户编码 |
| effectiveFrom | datetime | YES | - |  | 数据有效性开始时间 | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  | 数据有效性结束时间 | 数据有效性结束时间，NULL=当前有效 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| dataTypeCode | BTREE | NON-UNIQUE | dataTypeCode, basicDataId |
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.8 fnd_company -- 组织机构表

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 公司/组织机构信息 |
| 数据量 | ~3 行 |
| 数据大小 | 48.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，公司唯一标识 |
| pid | int(11) | NO | - | MUL | 父组织机构ID | 父组织机构ID |
| name | varchar(128) | NO | - |  | 组织机构全名 | 组织机构全名 |
| abbr | varchar(64) | NO | - |  | 组织机构简写 | 组织机构简写 |
| website | varchar(128) | YES | - |  | 组织机构网址 | 组织机构网址 |
| code | varchar(25) | YES | 0 | MUL | 组织机构代码 | 组织机构代码 |
| account | varchar(25) | YES |  |  | 组织机构账套 | 组织机构账套 |
| status | smallint(1) | NO | 1 |  | 有效性（1-有效，0-失效），默认有效 | 有效性（1-有效，0-失效），默认有效 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(32) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(32) | YES | - |  |  | 记录最新更新用户编码 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| code | BTREE | NON-UNIQUE | code |
| pid | BTREE | NON-UNIQUE | pid |
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.9 fnd_data_refresh_log

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 外部系统数据同步刷新日志 |
| 数据量 | ~16,540 行 |
| 数据大小 | 3.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，刷新日志唯一标识 |
| refreshTaskName | varchar(100) | YES | - |  |  | 刷新任务名称（Java类全限定名） |
| handleUser | varchar(15) | YES | - |  |  | 执行刷新操作的用户，通常为system |
| dataFrom | varchar(25) | YES | - |  |  | 数据来源系统标识（如SMS/CRM/Local） |
| dataTo | varchar(25) | YES | - |  |  | 数据目标系统标识 |
| refreshFrom | datetime | YES | - |  | 刷新开始时间 | 刷新开始时间 |
| refreshTo | datetime | YES | - |  | 结束时间 | 结束时间 |
| refreshState | int(11) | YES | 0 |  | 刷新成功或失败 0失败 1 成功 | 0=失败，1=成功 |
| refreshException | mediumtext | YES | - |  |  | 刷新异常堆栈信息，失败时记录完整异常 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.10 fnd_department

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 部门/办事处信息 |
| 数据量 | ~137 行 |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，部门唯一标识 |
| departmentNum | varchar(20) | NO | - | UNI |  | 部门编码，全局唯一 |
| departmentName | varchar(20) | NO | - |  |  | 部门名称 |
| isparam | int(11) | YES | 0 |  |  | 是否为参数部门，1=参数部门（如办事处/市场部），0=非参数部门 |
| status | int(11) | NO | 1 |  |  | 部门状态，1=启用，0=禁用 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |
| deparmentNum | UNIQUE | departmentNum | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| deparmentNum | BTREE | UNIQUE | departmentNum |
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.11 fnd_files

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 文件上传记录 |
| 数据量 | ~9,096 行 |
| 数据大小 | 2.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 系统上传文件信息 | 自增主键，文件唯一标识 |
| fileName | varchar(255) | YES | - |  | 文件名称 | 上传文件原始名 |
| filePath | varchar(255) | YES | - |  | 文件路径 | 文件服务器存储路径 |
| fileType | varchar(255) | YES | - |  | 文件分类 | 文件MIME类型 |
| uploadBy | varchar(25) | YES | - |  | 上传用户 | 上传用户编码 |
| uploadTime | datetime | YES | - |  | 上传时间 | 文件上传时间 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.12 fnd_mails

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 系统发送的邮件记录 |
| 数据量 | ~146,157 行 |
| 数据大小 | 440.8 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，邮件记录唯一标识 |
| mailSubject | varchar(255) | NO | - |  | 邮件主题 | 邮件主题 |
| mailContent | longtext | NO | - |  | 邮件正文 | 邮件正文HTML内容 |
| mailTos | text | YES | - |  | 邮件主送 | 邮件主送 |
| mailCcs | text | YES | - |  | 邮件抄送 | 邮件抄送 |
| mailBcc | text | YES | - |  | 邮件密送 | 邮件密送 |
| mailAttachFiles | text | YES | - |  | 邮件附件 以特殊符号间隔多个文件 | 邮件附件 以特殊符号间隔多个文件 |
| mailSendTime | datetime | YES | - |  | 邮件实际发送时间 | 邮件实际发送时间 |
| mailExpectSendTime | datetime | YES | - |  | 邮件期望发送时间 | 邮件期望发送时间 |
| mailServerPort | varchar(25) | YES | - |  |  | 邮件服务器端口 |
| mailServerHost | varchar(25) | YES | - |  |  | 邮件服务器地址 |
| mailUsername | varchar(25) | YES | - |  |  | 邮件服务器登录用户名 |
| mailPassword | varchar(25) | YES | - |  |  | 邮件服务器登录密码 |
| mailFromaddress | varchar(25) | YES | - |  |  | 邮件发件人地址 |
| sendFlag | int(11) | YES | 0 |  | 邮件是否发送 1 为已发送 | 邮件是否发送 1 为已发送 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| updatteTime | datetime | YES | - |  |  |  |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.13 fnd_menus

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 系统菜单定义 |
| 数据量 | ~22 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，菜单唯一标识 |
| menuCode | varchar(50) | YES | - |  | 菜单编码 | 菜单编码 |
| menuName | varchar(25) | YES | - |  | 菜单名称 | 菜单名称 |
| menuLevel | int(1) | YES | - |  | 菜单级别 | 菜单级别 |
| superId | int(11) | YES | - |  | 父菜单ID | 父菜单ID |
| path | varchar(200) | YES | - |  | 访问路径 | 访问路径 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.14 fnd_role_menus

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 角色与菜单的关联关系 |
| 数据量 | ~58 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，角色菜单关联唯一标识 |
| roleId | int(11) | NO | - |  |  | 关联角色ID，逻辑外键 -> fnd_roles.id |
| menuId | int(11) | NO | - |  |  | 关联菜单ID |
| menuPower | varchar(20) | NO | - |  | 各菜单增删改权限 | 各菜单增删改权限 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.15 fnd_roles

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 系统角色定义 |
| 数据量 | ~16 行 |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(6) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，角色唯一标识 |
| roleName | varchar(64) | NO | - | UNI |  | 角色名称 |
| defaultPage | varchar(255) | YES | - |  | 该角色登录的默认首页 | 该角色登录的默认首页 |
| status | int(1) | NO | - |  |  | 角色状态，1=启用，0=禁用 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| roleRemark | varchar(200) | YES | - |  |  | 角色备注说明 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |
| roleName | UNIQUE | roleName | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| roleName | BTREE | UNIQUE | roleName |

---

### 4.16 fnd_spms_arg

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | SPMS系统参数配置 |
| 数据量 | ~5 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 备件系统一些特殊参数控制 如邮件等 | 自增主键，SPMS参数唯一标识 |
| code | varchar(25) | YES | - |  |  | SPMS系统参数编码（唯一标识） |
| var | text | YES | - |  |  | SPMS系统参数值（支持字符串/JSON等格式） |
| mark | varchar(255) | YES | - |  |  | SPMS系统参数说明/备注 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.17 fnd_sys_arg

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 系统参数配置 |
| 数据量 | ~45 行 |
| 数据大小 | 80.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 系统变量 | 自增主键，参数唯一标识 |
| code | varchar(64) | YES | - |  |  | 系统参数编码（唯一标识，如sys.envirment.argu） |
| var | text | YES | - |  |  | 系统参数值（支持字符串/JSON/数字等多种格式） |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.18 fnd_user_info

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 系统用户信息 |
| 数据量 | ~459 行 |
| 数据大小 | 128.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(8) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，用户唯一标识 |
| username | varchar(128) | NO | - | MUL |  | 用户登录名，全局唯一 |
| password | varchar(32) | NO | 5416d7cd6ef195a0f7622a9c56b55e84 |  |  | 加密后的用户密码 |
| email | varchar(128) | NO | - |  |  | 用户邮箱 |
| dpNo | varchar(25) | YES | - |  |  | 用户所属部门编码，逻辑外键 -> fnd_department.departmentNum |
| realName | varchar(128) | NO | - |  |  | 用户真实姓名 |
| roleIds | varchar(64) | YES | - |  | 用户角色，支持多角色 | 用户角色，支持多角色 |
| isemail | int(11) | YES | - |  |  | 是否接收邮件通知，1=接收，0=不接收 |
| status | int(1) | YES | - |  |  | 用户状态，1=启用，0=禁用 |
| defaultPage | varchar(255) | YES | - |  | 该用户登录首页 | 该用户登录首页 |
| pwdoverdue | datetime | YES | - |  |  | 密码过期时间，超过此时间需强制修改密码 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段，存储动态属性 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| username | BTREE | NON-UNIQUE | username |

---

### 4.19 fnd_user_menus

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 用户与菜单的关联关系 |
| 数据量 | ~3,044 行 |
| 数据大小 | 272.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，用户菜单关联唯一标识 |
| fnd_user_id | int(11) | YES | - |  |  | 关联用户ID，逻辑外键 -> fnd_user_info.id |
| username | varchar(128) | YES | - |  |  | 逻辑外键 -> fnd_user_info.username |
| menuCode | varchar(50) | YES | - |  |  | 逻辑外键 -> fnd_menus.menuCode |
| menuValue | int(1) | YES | - |  |  | 菜单权限值，1=有权限 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |
| createdBy | varchar(25) | YES | - |  |  | 记录创建人用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.20 fnd_user_power

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 用户权限配置 |
| 数据量 | ~442 行 |
| 数据大小 | 64.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，权限记录唯一标识 |
| fndUserId | int(11) | YES | - |  |  | 关联用户ID，逻辑外键 -> fnd_user_info.id |
| username | varchar(25) | YES | - |  |  | 逻辑外键 -> fnd_user_info.username |
| areapower | varchar(4096) | YES | - |  |  | 用户数据权限区域编码，逗号分隔的部门编码列表，-1=全部权限 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.21 pm_basic_deliver_detail

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目交付物明细表，定义各项目类型的交付件模板 |
| 数据量 | ~68,845 行 |
| 数据大小 | 23.1 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键 |
| projectId | int(11) | YES | - | MUL | 项目ID | 项目ID |
| projectType | varchar(25) | YES | 10 | MUL | 项目类型 | 项目类型 |
| contractNo | varchar(25) | YES | - |  |  |  |
| taskId | int(11) | YES | - |  | TaskId | TaskId |
| deliverId | int(11) | YES | - | MUL | 对应pm_basic_prj_deliver主键 | 逻辑外键 -> pm_basic_prj_deliver.id |
| deliverableName | varchar(255) | YES | - |  | 交付件名称 | 交付件名称 |
| deliverablePath | varchar(255) | YES | - |  | 交付件路径 | 交付件路径 |
| deliverableType | varchar(45) | YES | - |  | 交付件类型 | 交付件类型 |
| uploadUser | varchar(45) | YES | - |  | 上传者 | 上传者 |
| uploadTime | datetime | YES | - |  | 上传时间 | 上传时间 |
| effectiveFrom | datetime | YES | - |  |  | 有效期起始 |
| effectiveTo | datetime | YES | - |  |  | 有效期截止 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| deliverId | BTREE | NON-UNIQUE | deliverId |
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |
| projectType_projectId_deliverType | BTREE | NON-UNIQUE | projectType, projectId, deliverableType |

---

### 4.22 pm_basic_prj_deliver

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目交付模板定义，按项目类型配置交付件清单 |
| 数据量 | ~86 行 |
| 数据大小 | 48.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 交付模板唯一标识 |
| column010 | varchar(25) | YES | - |  | 对应pm_project_header | 对应pm_project_header |
| column011 | varchar(25) | YES | - |  | 对应pm_project_header | 对应pm_project_header |
| dataTypeCode | varchar(45) | YES | - | MUL | 活动节点，对应fnd_basic_data表 | 活动节点，对应fnd_basic_data表 |
| basicDataId | varchar(45) | YES | - |  | 活动节点，对应fnd_basic_data表 | 活动节点，对应fnd_basic_data表 |
| dataTypeCodeSon | varchar(45) | YES | - | MUL | 交付件节点，对应fnd_basic_data表 | 交付件节点，对应fnd_basic_data表 |
| basicDataIdSon | varchar(45) | YES | - |  | 交付件节点，对应fnd_basic_data表 | 交付件节点，对应fnd_basic_data表 |
| isNeed | int(11) | YES | 0 |  | 是否必须，1表示必须，2表示分情况确定 | 是否必须，1表示必须，2表示分情况确定 |
| createTime | datetime | YES | - |  |  |  |
| createBy | varchar(45) | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |
| updateBy | varchar(45) | YES | - |  |  |  |
| effectiveFrom | datetime | YES | - |  |  | 有效期起始 |
| effectiveTo | datetime | YES | - |  |  | 有效期截止 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| dataTypeCode | BTREE | NON-UNIQUE | dataTypeCode, basicDataId |
| dataTypeCodeSon | BTREE | NON-UNIQUE | dataTypeCodeSon, basicDataIdSon |
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.23 pm_cl_callback

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 运营商直签项目回访申请主表，关联Activiti工作流 |
| 数据量 | ~2,729 行 |
| 数据大小 | 592.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 运营商直签项目回访申请主表 | 回访申请ID |
| projectId | int(11) | YES | - | MUL | 项目ID | 逻辑外键 -> pm_project.projectId |
| instId | varchar(25) | YES | - | MUL | 流程ID | Activiti流程实例ID |
| remark | text | YES | - |  | 回访申请备注 | 回访申请备注 |
| applyState | int(11) | YES | - |  | -1草稿 1 审批中 2审批通过 | -1=草稿，1=审批中，2=审批通过 |
| applyBy | varchar(25) | YES | - |  | 申请人 | 回访申请人编码 |
| applyTime | datetime | YES | - |  | 申请时间 | 回访申请时间 |
| createTime | timestamp | YES | - |  |  | 记录创建时间 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| instId | BTREE | NON-UNIQUE | instId |
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 4.24 pm_cl_callback_quesnaire

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 回访与问卷的关联表，一次回访可关联多个问卷版本 |
| 数据量 | ~2,855 行 |
| 数据大小 | 384.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，关联记录唯一标识 |
| callBackId | int(11) | YES | - | MUL | 回访主键主表 | 逻辑外键 -> pm_cl_callback.id |
| taskId | varchar(25) | YES | - |  | 对应任务ID | Activiti任务ID |
| quesnaireId | int(11) | YES | - | MUL | 对应pm_cl_quesnaire_result_header主键 | 逻辑外键 -> pm_cl_quesnaire_result_header.id |
| quesnaireVersion | int(11) | YES | - |  | 版本号 | 问卷模板版本号 |
| quesnaireState | int(11) | YES | - |  | -1 草稿 1已提交 | 0=未填写，1=已填写 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| callBackId | BTREE | NON-UNIQUE | callBackId |
| PRIMARY | BTREE | UNIQUE | id |
| quesnaireId | BTREE | NON-UNIQUE | quesnaireId |

---

### 4.25 pm_cl_evaluation_header

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 客户评价表头，记录评价基本信息 |
| 数据量 | ~25,911 行 |
| 数据大小 | 8.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) unsigned | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，评价记录唯一标识 |
| projectCode | varchar(45) | NO | - | MUL | 评测项目编码 | 评测项目编码 |
| projectId | int(11) | NO | 0 | MUL | 项目ID | 支持多项目逗号分隔 |
| projectName | varchar(120) | YES | - |  | 项目名称 | 项目名称 |
| evaluationTime | datetime | YES | 0000-00-00 00:00:00 |  | 审核时间 | 审核时间 |
| evaluationPeopleName | varchar(45) | YES | - |  | 审核人员姓名 | 审核人员姓名 |
| evaluationScore | double | NO | 0 |  | 评测总分数 | 评测总分数 |
| evaluationResult | int(11) | NO | 0 |  | 评测结果（通过/未通过） | 评测结果（通过/未通过） |
| evaluationComment | text | YES | - |  | 项目评价（驳回时为驳回原因） | 项目评价（驳回时为驳回原因） |
| evaluationType | int(11) | NO | 0 |  | 400回访/项目组总分评定 | 400回访/项目组总分评定 |
| status | int(11) | NO | 0 |  |  | 回访申请状态，0=待审核，1=已审核 |
| createdTime | datetime | YES | 0000-00-00 00:00:00 |  |  | 记录创建时间 |
| createdPerson | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updatedTime | datetime | YES | 0000-00-00 00:00:00 |  |  | 记录最新更新时间 |
| updatedPerson | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| nextAcceptPerson | varchar(25) | YES | - |  | 下一个接收申请的人员 | 下一处理人编码 |
| evaluationPeopleId | varchar(25) | YES | - |  | 审核人员用户名 | 审核人员用户名 |
| nextAcceptPersonName | varchar(25) | YES | - |  |  | 下一处理人姓名 |
| applyHeaderId | int(11) | NO | 0 |  | 申请表Id | 申请表Id |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectCode_index | BTREE | NON-UNIQUE | projectCode |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 4.26 pm_cl_quesnaire_result_header

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 问卷结果头表，记录一次问卷填写的结果 |
| 数据量 | ~103,363 行 |
| 数据大小 | 9.0 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) unsigned | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，问卷结果唯一标识 |
| evaluationHeaderId | int(11) | NO | - | MUL | 测评记录Id | 逻辑外键 -> pm_cl_evaluation_header.id |
| quesnaireTemplateHeaderId | int(11) | YES | - |  | 问卷模板Id | 逻辑外键 -> pm_cl_quesnaire_template_header.id |
| quesMarkScore | double | YES | 0 |  | 问卷得分 | 问卷得分 |
| createdTime | datetime | YES | - |  |  | 记录创建时间 |
| createdPerson | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updatedTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updatedPerson | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| quesMarkResult | int(11) | YES | - |  | 评分结果 | 评分结果 |
| status | int(11) | NO | 0 |  |  | 问卷结果状态，0=未完成，1=已完成 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| index_evaluationHeaderId | BTREE | NON-UNIQUE | evaluationHeaderId |
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.27 pm_cl_quesnaire_result_line

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 问卷结果行表，记录每个问题的回答 |
| 数据量 | ~454,563 行 |
| 数据大小 | 66.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) unsigned | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，结果行唯一标识 |
| quesnaireTemplateHeaderId | int(11) | NO | - | MUL | 回访问卷Id | 回访问卷Id |
| quesnaireTemplateLineId | int(11) | NO | - | MUL | 问卷中问题的Id | 逻辑外键 -> pm_cl_quesnaire_template_line.id |
| questionTemplateOptId | int(11) | YES | - |  | 选中的选项id | 选中的选项id |
| questionAnswer | text | YES | - |  |  | 题目答案/正确选项文本 |
| questionScore | double | NO | 0 |  | 问题得分 | 问题得分 |
| quesnaireResultHeaderId | int(11) | NO | - | MUL | 回访结果头信息Id | 逻辑外键 -> pm_cl_quesnaire_result_header.id |
| createdTime | datetime | YES | - |  |  | 记录创建时间 |
| createdPerson | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updatedTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updatedPerson | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| quesTypeForCB | varchar(10) | YES | - |  | 问题回访类型 | 问题回访类型 |
| quesEvaResult | int(11) | YES | - |  | 选项是否为不同选项 | 选项是否为不同选项 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| quesnaireResultHeaderId | BTREE | NON-UNIQUE | quesnaireResultHeaderId, quesTypeForCB |
| quesnaireTemplateHeaderId | BTREE | NON-UNIQUE | quesnaireTemplateHeaderId, quesnaireTemplateLineId |
| quesnaireTemplateLineId | BTREE | NON-UNIQUE | quesnaireTemplateLineId, questionTemplateOptId |

---

### 4.28 pm_cl_quesnaire_template_header

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 问卷模板定义头表 |
| 数据量 | ~13 行 |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(10) unsigned | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，模板唯一标识 |
| questionnaireTemplateNum | varchar(45) | NO | - |  | 问卷模板编号 | 问卷模板编号 |
| questionnaireTemplateName | varchar(200) | NO | - |  | 问卷模板名称 | 问卷模板名称 |
| questionnaireScore | double | NO | 0 |  | 问卷总分数 | 问卷满分 |
| questionnairePassScore | double | NO | 0 |  | 问卷达标分数 | 问卷及格分 |
| questionnaireStatus | int(11) | NO | 0 |  | 问卷状态 | 模板状态，0=禁用，1=启用 |
| effectiveStartTime | datetime | YES | - |  |  | 模板生效开始时间 |
| effectiveEndTime | datetime | YES | - |  |  | 模板生效结束时间 |
| createdTime | datetime | YES | - |  |  | 模板创建时间 |
| updatedTime | datetime | YES | - |  |  | 模板最后修改时间 |
| createdPerson | varchar(25) | YES | - |  |  | 模板创建人 |
| updatedPerson | varchar(25) | YES | - |  |  | 模板最后修改人 |
| quesType | varchar(25) | YES | - | MUL |  | 问卷业务类型 |
| markIndexs | varchar(45) | YES | - |  | 问卷计分规则的index | 标记索引，用于模板配置 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| quesType | BTREE | NON-UNIQUE | quesType |

---

### 4.29 pm_cl_quesnaire_template_line

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 问卷模板题目定义 |
| 数据量 | ~80 行 |
| 数据大小 | 48.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) unsigned | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，题目唯一标识 |
| questionContent | varchar(200) | NO | - |  | 题目内容 | 题目内容 |
| questionType | int(11) | NO | - |  | 题目类型,如:多选\单选 | 题目类型（单选/多选/文本） |
| questionScore | double | NO | 0 |  | 题目分数 | 题目分值 |
| questionRemark | varchar(200) | YES | - |  | 题目备注 | 题目备注 |
| questionNum | int(11) | NO | 0 |  | 问题编号,表示了问卷中问题的顺序 | 问题编号,表示了问卷中问题的顺序 |
| quesnaireTemplateHeaderId | int(11) | NO | 0 | MUL | 问卷模板Id | 逻辑外键 -> pm_cl_quesnaire_template_header.id |
| questionStatus | int(11) | YES | 0 |  |  | 题目状态，0=禁用，1=启用 |
| effectiveStartTime | datetime | YES | - |  |  | 生效开始时间 |
| effectiveEndTime | datetime | YES | - |  |  | 生效结束时间 |
| createdTime | datetime | YES | - |  |  | 记录创建时间 |
| updatedTime | datetime | YES | - |  |  | 记录最新更新时间 |
| createdPerson | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updatedPerson | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| questionTypeForCB | varchar(10) | YES | - |  | 回访问题类型 | 回访问题类型 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |
| id_UNIQUE | UNIQUE | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| id_UNIQUE | BTREE | UNIQUE | id |
| PRIMARY | BTREE | UNIQUE | id |
| quesnaireTemplateHeaderId | BTREE | NON-UNIQUE | quesnaireTemplateHeaderId |

---

### 4.30 pm_cl_quesnaire_template_options

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 问卷模板题目选项 |
| 数据量 | ~231 行 |
| 数据大小 | 64.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) unsigned | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，选项唯一标识 |
| questionId | int(11) | NO | 0 |  | 题目Id | 题目Id |
| questionOptionNum | int(11) | NO | 0 |  | 选项编号 | 选项编号 |
| questionOptionsContent | varchar(200) | NO | - |  | 选项内容 | 选项内容 |
| questionOptionScore | double | YES | 0 |  | 选项分数 | 选项分数 |
| quesnaireTemplateHeaderId | int(11) | NO | 0 |  | 问卷模板Id | 问卷模板Id |
| effectiveStartTime | datetime | YES | - |  |  | 生效开始时间 |
| effectiveEndTime | datetime | YES | - |  |  | 生效结束时间 |
| createdTime | datetime | YES | - |  |  | 记录创建时间 |
| updatedTime | datetime | YES | - |  |  | 记录最新更新时间 |
| createdPerson | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updatedPerson | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| quesLineType | varchar(10) | YES | - |  | 问题类型 | 问题类型 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |
| id_UNIQUE | UNIQUE | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| id_UNIQUE | BTREE | UNIQUE | id |
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.31 pm_column_of_relationship

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 数据库列关系映射配置，用于数据同步时的字段映射 |
| 数据量 | ~14 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键 |
| projectType | int(11) | YES | - |  |  |  |
| columnCode | varchar(45) | YES | - |  |  |  |
| colemnName | varchar(45) | YES | - |  |  |  |
| columnDesc | varchar(45) | YES | - |  |  |  |
| createTime | datetime | YES | - |  |  |  |
| createBy | varchar(45) | YES | - |  |  |  |
| effectiveFrom | datetime | YES | - |  |  |  |
| effectiveTo | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.32 pm_common_related_data

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 通用关联数据表，存储业务对象间的关联关系 |
| 数据量 | ~572 行 |
| 数据大小 | 4.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键 |
| type | varchar(64) | NO | - | MUL | 数据类型 | 数据类型 |
| objType | varchar(64) | NO | - |  | 主数据类型 | 主数据类型 |
| objId | int(11) | NO | 0 |  | 主数据Id | 主数据Id |
| field1 | varchar(255) | YES | - |  |  |  |
| field2 | varchar(255) | YES | - |  |  |  |
| field3 | varchar(255) | YES | - |  |  |  |
| field4 | varchar(255) | YES | - |  |  |  |
| field5 | varchar(255) | YES | - |  |  |  |
| field6 | varchar(255) | YES | - |  |  |  |
| field7 | varchar(255) | YES | - |  |  |  |
| field8 | varchar(255) | YES | - |  |  |  |
| field9 | varchar(255) | YES | - |  |  |  |
| field10 | varchar(255) | YES | - |  |  |  |
| disabled | bit(1) | NO | b'0' |  |  |  |
| effectiveFrom | datetime | YES | - |  |  | 有效期起始 |
| effectiveTo | datetime | YES | - |  |  | 有效期截止 |
| createBy | varchar(25) | YES | - |  |  |  |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  |  |  |
| updateBy | varchar(25) | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |
| customInfo | json | YES | - |  |  | JSON扩展信息 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| type | BTREE | NON-UNIQUE | type, objType, objId |

---

### 4.33 pm_daily_report

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目日报数据表 |
| 数据量 | ~11,221 行 |
| 数据大小 | 116.3 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键 |
| projectId | int(11) | NO | -1 | MUL | 项目头信息主键 | 逻辑外键 -> pm_project.projectId |
| projectType | varchar(45) | NO |  | MUL | 项目类型，售前:20/售后:10 | 项目类型，售前:20/售后:10 |
| projectCode | varchar(45) | NO |  | MUL | 项目名称 | 项目名称 |
| projectName | varchar(200) | YES |  |  | 项目名称 | 项目名称 |
| contractNo | varchar(255) | YES |  |  | 合同号 | 合同号 |
| officeCode | varchar(25) | YES | - | MUL | 办事处编码 | 办事处编码 |
| type | varchar(45) | YES | - | MUL | 任务性质 | 任务性质 |
| category | varchar(45) | YES | - | MUL | 任务分类 | 任务分类 |
| subCategory | varchar(45) | YES | - | MUL | 任务小类 | 任务小类 |
| processTime | datetime | YES | - |  | 处理时间 | 处理时间 |
| processDesc | varchar(1024) | YES | - |  | 事项描述 | 事项描述 |
| processStep | varchar(1024) | YES | - |  | 解决进展 | 解决进展 |
| remainProblem | varchar(1024) | YES | - |  | 遗留问题 | 遗留问题 |
| customerInteraction | varchar(1024) | YES | - |  | 客户互动情况 | 客户互动情况 |
| transitHour | float | YES | 0 |  | 在途耗时(h) | 在途耗时(h) |
| processHour | float | YES | 0 |  | 处理耗时(h) | 处理耗时(h) |
| itemModel | varchar(255) | YES | - |  | 产品型号 | 产品型号 |
| softVersion | varchar(255) | YES | - |  | 在网版本 | 在网版本 |
| enabledFeatures | varchar(255) | YES | - |  | 启用功能 | 启用功能 |
| customTos | varchar(255) | YES | - |  | 自定义主送 | 自定义主送 |
| customCcs | varchar(255) | YES | - |  | 自定义抄送 | 自定义抄送 |
| projectExecutionState | varchar(45) | YES |  |  | 项目实施状态 | 项目实施状态 |
| hasReport | bit(1) | NO | b'0' |  | 是否有巡检报告 | 是否有巡检报告 |
| quesnaireId | int(11) | YES | - |  | 问卷ID | 问卷ID |
| deliverFileIds | varchar(255) | YES |  |  | 交付件，fnd_files id | 交付件，fnd_files id |
| remark | varchar(1024) | YES | - |  | 备注 | 备注 |
| isReported | bit(1) | YES | b'0' |  | 已上报 | 已上报 |
| qualityFactor | float | YES | 0 |  | 质量系数 | 质量系数 |
| customInfo | json | YES | - |  | 自定义信息 | 自定义信息 |
| status | varchar(25) | YES | - |  | 状态 | 状态 |
| disabled | bit(1) | YES | b'0' |  | 失效标记 | 失效标记 |
| createTime | datetime | YES | - | MUL | 创建时间 | 创建时间 |
| createBy | varchar(45) | YES | - | MUL | 创建用户 | 日报创建人 |
| updateTime | datetime | YES | - |  | 最新更新时间 | 最后修改时间 |
| updateBy | varchar(45) | YES | - |  | 最新更新用户 | 最后修改人 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| category | BTREE | NON-UNIQUE | category, subCategory |
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

### 4.34 pm_dispatch_project_header

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 人员派遣项目头信息 |
| 数据量 | ~328 行 |
| 数据大小 | 480.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键 |
| dispatchName | varchar(512) | YES |  |  | 外派名称 | 外派名称 |
| dispatchNo | varchar(64) | YES |  | MUL | 外派合同号 | 外派合同号 |
| dispatchSeq | varchar(64) | YES | - | UNI | 外派编号 | 外派编号 |
| contractNos | varchar(2048) | YES |  |  | 项目合同号 | 项目合同号 |
| projectIds | varchar(1024) | YES |  |  | 外派的项目ID | 外派的项目ID |
| type | varchar(25) | YES | - |  | 外派类型 | 外派类型 |
| state | int(11) | NO | 0 |  | 外派状态 | 外派状态 |
| peopleNum | int(11) | YES | 0 |  | 外派人数 | 外派人数 |
| callbackState | int(11) | YES | - |  | 回访状态 | 回访状态 |
| facilitatorId | int(11) | YES | - |  | 服务商ID | 服务商ID |
| facilitatorCode | varchar(25) | YES | - | MUL | 服务商编码 | 服务商编码 |
| facilitatorName | varchar(64) | YES |  |  | 服务商名 | 服务商名 |
| bankInfo | varchar(255) | YES |  |  | 服务商开户地址 | 服务商开户地址 |
| bankAccount | varchar(64) | YES |  |  | 服务商收款账户 | 服务商收款账户 |
| officeCode | varchar(25) | YES |  | MUL | 办事处部门 | 办事处部门 |
| profitDepCode | varchar(25) | YES |  | MUL | 收益部门 | 收益部门 |
| dutyPerson | varchar(25) | YES | - |  | 项目总接口人 | 项目总接口人 |
| officeDutyPerson | varchar(25) | YES | - |  | 办事处接口人 | 办事处接口人 |
| isAccrued | bit(1) | YES | - |  | 是否计提 | 是否计提 |
| isInvoiced | bit(1) | YES | - |  | 是否提供发票 | 是否提供发票 |
| dispatchAmount | varchar(25) | YES |  |  | 外派价 | 外派价 |
| prepaidInfo | varchar(255) | YES |  |  | 预付信息（比例、金额） | 预付信息（比例、金额） |
| prepaidRule | varchar(255) | YES |  |  | 预付遵循原则 | 预付遵循原则 |
| acceptanceInfo | varchar(255) | YES |  |  | 验收要求 | 验收要求 |
| reason | varchar(512) | YES |  |  | 外派原因 | 外派原因 |
| remark | varchar(512) | YES |  |  | 备注 | 备注 |
| dispatchTime | datetime | YES | - |  | 派单时间 | 派单时间 |
| smsProjectCode | varchar(255) | YES |  | MUL | SMS项目编码 | SMS项目编码 |
| smsSubmitTime | datetime | YES | - |  | SMS项目提交时间 | SMS项目提交时间 |
| smsProjectAmount | varchar(25) | YES |  |  | SMS项目金额 | SMS项目金额 |
| smsAfProjectAmount | varchar(25) | YES |  |  | 安服项目金额 | 安服项目金额 |
| effectiveFrom | datetime | YES | - |  | 有效开始时间 | 有效开始时间 |
| effectiveTo | datetime | YES | - |  | 有效结束时间 | 有效结束时间 |
| disabled | bit(1) | YES | b'0' |  | 删除状态 | 删除状态 |
| dispatched | bit(1) | YES | b'0' |  | 派单状态 | 派单状态 |
| settled | bit(1) | YES | b'0' |  | 结算状态 | 结算状态 |
| createBy | varchar(25) | YES |  |  |  | 创建人 |
| createTime | datetime | YES | - |  |  | 创建时间 |
| updateBy | varchar(25) | YES |  |  |  | 最后修改人 |
| updateTime | datetime | YES | - |  |  | 最后修改时间 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展信息 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |
| UNIQUE_dispatchSeq | UNIQUE | dispatchSeq | None | None |

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

### 4.35 pm_dispatch_project_settlement

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 派遣项目结算信息 |
| 数据量 | ~72 行 |
| 数据大小 | 160.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键 |
| settleSeq | varchar(512) | YES | - |  | 结算编号 | 结算编号 |
| dispatchId | int(11) | NO | - | MUL | 派单Id | 逻辑外键 -> pm_dispatch_project_header.id |
| dispatchSeq | varchar(25) | NO | - | MUL | 派单编号 | 派单编号 |
| progressDesc | varchar(1024) | YES | - |  | 实施进展 | 实施进展 |
| progressRatio | float(3,2) | YES | - |  | 实施比例 | 实施比例 |
| acceptanceDesc | varchar(1024) | YES | - |  | 验收进度 | 验收进度 |
| acceptanceRatio | varchar(10) | YES | - |  | 验收比例 | 验收比例 |
| ratio | varchar(10) | YES | - |  | 此次付款比例 | 此次付款比例 |
| amount | varchar(25) | YES | - |  | 此次付款金额 | 此次付款金额 |
| memo | varchar(512) | YES | - |  | 此次付款说明 | 此次付款说明 |
| confirmTime | datetime | YES | - |  | 提交时间 | 提交时间 |
| paymentTime | datetime | YES | - |  | 付款时间 | 付款时间 |
| remark | varchar(512) | YES | - |  | 备注 | 备注 |
| state | int(1) | YES | 0 |  | 状态 | 状态 |
| disabled | bit(1) | YES | b'0' |  | 删除标记 | 删除标记 |
| quarter | int(4) | YES | - |  | 结算季度 | 结算季度 |
| month | int(2) | YES | - |  | 结算月份 | 结算月份 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展信息 |
| sseId | bigint(20) | YES | -1 |  | sse报销单审批行ID,0：会进行匹配跟新 | sse报销单审批行ID,0：会进行匹配跟新 |
| createTime | datetime | YES | - |  |  | 创建时间 |
| createBy | varchar(25) | YES | - |  |  | 创建人 |
| updateTime | datetime | YES | - |  |  |  |
| updateBy | varchar(25) | YES | - |  |  |  |
| settled | bit(1) | YES | b'0' |  | 结算状态 | 结算状态 |
| year | int(4) | YES | - |  | 结算年份 | 结算年份 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| dispatchId | BTREE | NON-UNIQUE | dispatchId |
| dispatchSeq | BTREE | NON-UNIQUE | dispatchSeq |
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.36 pm_dispatch_project_settlement_from_d365

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 从D365系统同步的派遣项目结算数据 |
| 数据量 | ~44 行 |
| 数据大小 | 96.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| slipId | bigint(20) | YES | - | MUL |  |  |
| inventTransId | varchar(20) | YES | - |  |  |  |
| vendAccount | varchar(20) | YES | - |  |  |  |
| innerInvoiceId | varchar(20) | YES | - |  |  |  |
| invoiceDate | timestamp | YES | - |  |  |  |
| invoiceId | varchar(20) | YES | - |  |  |  |
| purchId | varchar(20) | YES | - | MUL |  |  |
| purchName | varchar(255) | YES | - |  |  |  |
| purchPoolId | varchar(10) | YES | - |  |  |  |
| packingSlipId | varchar(20) | YES | - | MUL |  |  |
| packingSlipRemark | varchar(255) | YES | - |  |  |  |
| slipQty | decimal(32,6) | YES | - |  |  |  |
| receiveQty | decimal(32,6) | YES | - |  |  |  |
| invoiceQty | decimal(32,6) | YES | - |  |  |  |
| price | decimal(32,6) | YES | - |  |  |  |
| invoicePrice | decimal(32,6) | YES | - |  |  |  |
| receiveAmount | decimal(38,6) | YES | - |  |  |  |
| invoiceAmount | decimal(38,6) | YES | - |  |  |  |
| settleQty | decimal(38,6) | YES | - |  |  |  |
| lineAmount | decimal(38,6) | YES | - |  |  |  |
| invoiceAmountTotal | decimal(32,6) | YES | - |  |  |  |
| settleAmountTotal | decimal(38,6) | YES | - |  |  |  |
| settleAmount | decimal(38,6) | YES | - |  |  |  |
| confirmTime | timestamp | YES | - |  |  |  |
| settleTime | timestamp | YES | - |  |  |  |
| projectProgress | varchar(64) | YES | - |  |  |  |
| otherSysNum | varchar(20) | YES | - |  |  |  |
| partition | bigint(20) | YES | - |  |  |  |
| dataAreaId | varchar(4) | YES | - |  |  |  |
| settleId | bigint(20) | YES | - |  |  |  |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| packingSlipId | BTREE | NON-UNIQUE | packingSlipId |
| purchId | BTREE | NON-UNIQUE | purchId |
| slipId | BTREE | NON-UNIQUE | slipId |

---

### 4.37 pm_facilitator

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 外包供应商信息表 |
| 数据量 | ~24 行 |
| 数据大小 | 48.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键 |
| code | varchar(25) | YES | - | UNI | 服务商编号 | 服务商编号 |
| account | varchar(25) | YES | - | MUL | 服务商账号 | 服务商账号 |
| name | varchar(64) | YES | - |  | 服务商名 | 服务商名 |
| type | varchar(64) | YES | - |  | 合作类型 | 合作类型 |
| bankInfo | varchar(255) | YES | - |  | 开户行信息 | 开户行信息 |
| bankAccount | varchar(64) | YES | - |  | 收款账户 | 收款账户 |
| cnapsCode | varchar(25) | YES | - |  | 联行号 | 联行号 |
| contacts | varchar(64) | YES | - |  | 联系人 | 联系人 |
| tel | varchar(64) | YES | - |  | 联系电话 | 联系电话 |
| email | varchar(64) | YES | - |  | 联系邮箱 | 联系邮箱 |
| state | bit(1) | YES | b'1' |  | 状态 | 状态 |
| needApprove | bit(1) | YES | b'0' |  | 是否评审 | 是否评审 |
| approveStatus | int(1) | YES | 0 |  | 审批结果 | 审批结果 |
| deliveryIds | varchar(25) | YES | - |  | 附件材料 | 附件材料 |
| relateType | varchar(25) | YES | - |  | 关联类型 | 关联类型 |
| effectiveFrom | datetime | YES | - |  |  |  |
| effectiveTo | datetime | YES | - |  |  |  |
| createBy | varchar(45) | YES | - |  |  |  |
| createTime | datetime | YES | - |  |  |  |
| updateBy | varchar(45) | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |
| customInfo | json | YES | - |  | 自定义信息 | 自定义信息 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |
| account | UNIQUE | account | None | None |
| account | UNIQUE | state | None | None |
| code | UNIQUE | code | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| account | BTREE | UNIQUE | account, state |
| code | BTREE | UNIQUE | code |
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.38 pm_facilitator_form_d365

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 从D365系统同步的供应商信息 |
| 数据量 | ~761 行 |
| 数据大小 | 240.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键 |
| code | varchar(25) | YES | - | UNI | 服务商编号 | 服务商编号 |
| account | varchar(25) | YES | - | MUL | 服务商账号 | 服务商账号 |
| name | varchar(64) | YES | - |  | 服务商名 | 服务商名 |
| type | varchar(64) | YES | - |  | 合作类型 | 合作类型 |
| bankInfo | varchar(255) | YES | - |  | 开户行信息 | 开户行信息 |
| bankAccount | varchar(64) | YES | - |  | 收款账户 | 银行账号 |
| cnapsCode | varchar(25) | YES | - |  | 联行号 | 联行号 |
| contacts | varchar(64) | YES | - |  | 联系人 | 联系人 |
| tel | varchar(64) | YES | - |  | 联系电话 | 联系电话 |
| email | varchar(64) | YES | - |  | 联系邮箱 | 联系邮箱 |
| state | bit(1) | YES | b'1' |  | 状态 | 状态 |
| needApprove | bit(1) | YES | b'0' |  | 是否评审 | 是否评审 |
| approveStatus | int(1) | YES | 0 |  | 审批结果 | 审批结果 |
| deliveryIds | varchar(25) | YES | - |  | 附件材料 | 附件材料 |
| relateType | varchar(25) | YES | - |  | 关联类型 | 关联类型 |
| effectiveFrom | datetime | YES | - |  |  |  |
| effectiveTo | datetime | YES | - |  |  |  |
| createBy | varchar(45) | YES | - |  |  |  |
| createTime | datetime | YES | - |  |  |  |
| updateBy | varchar(45) | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展信息 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |
| account | UNIQUE | account | None | None |
| account | UNIQUE | code | None | None |
| code | UNIQUE | code | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| account | BTREE | UNIQUE | account, code |
| code | BTREE | UNIQUE | code |
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.39 pm_notification_template -- 消息模板（邮件、短信等）

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目通知消息模板，与t_notify_template功能类似 |
| 数据量 | ~66 行 |
| 数据大小 | 64.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键 |
| templateCode | varchar(45) | YES | - |  |  | 模板业务编码 |
| notificationObject | varchar(45) | YES | - |  | 主题 | 主题 |
| notificationContent | text | YES | - |  | 内容 | 通知正文模板 |
| createTime | datetime | YES | - |  |  |  |
| createBy | varchar(45) | YES | - |  |  |  |
| effectiveFrom | datetime | YES | - |  |  | 有效期起始 |
| effectiveTo | datetime | YES | - |  |  | 有效期截止 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.40 pm_presales_lend_2_delivery_off_from_sap

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~44,530 行 |
| 数据大小 | 11.1 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| orderNumber | varchar(11) | YES | - | MUL |  |  |
| lineId | int(11) | YES | - |  |  |  |
| itemCode | varchar(10) | YES | - |  |  |  |
| ppliCode | varchar(25) | YES | - | MUL | 借货执行单号 | 借货执行单号 |
| contract | varchar(25) | YES | - | MUL |  |  |
| deliveryDate | date | YES | - |  | 发货时间 | 发货时间 |
| rmaDate | date | YES | - |  | 退货时间 | 退货时间 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contract | BTREE | NON-UNIQUE | contract |
| orderNumber | BTREE | NON-UNIQUE | orderNumber, lineId |
| ppliCode | BTREE | NON-UNIQUE | ppliCode |
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.41 pm_presales_lend_2_rma_from_sms

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据大小 | 64.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | 0 |  |  |  |
| orderNumber | varchar(11) | YES | - | MUL |  |  |
| ppliCode | varchar(25) | YES | - | MUL | 借货执行单号 | 借货执行单号 |
| orderType | varchar(10) | YES | - |  |  |  |
| contract | varchar(25) | YES | - | MUL |  |  |
| customer | varchar(255) | YES | - |  |  |  |
| projectName | varchar(255) | YES | - |  |  |  |
| businessunit | varchar(50) | YES | - |  |  |  |
| office | varchar(20) | YES | - |  |  |  |
| dutyperson | varchar(10) | YES | - |  |  |  |
| itemcode | varchar(10) | YES | - |  |  |  |
| description | varchar(255) | YES | - |  |  |  |
| productfirstName | varchar(255) | YES | - |  |  |  |
| productName | varchar(255) | YES | - |  |  |  |
| orderQty | int(11) | YES | - |  |  |  |
| dlvQty | int(11) | YES | - |  |  |  |
| rmaQty | int(11) | YES | - |  |  |  |
| lineStatus | varchar(5) | YES | - |  |  |  |
| createDate | date | YES | - |  |  |  |
| lineId | int(11) | YES | - |  |  |  |
| systemId | int(11) | YES | - |  |  |  |
| canceled | char(1) | YES | - |  |  |  |
| dataSource | varchar(25) | YES | SMS |  |  |  |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contract | BTREE | NON-UNIQUE | contract, itemcode |
| orderNumber | BTREE | NON-UNIQUE | orderNumber, lineId |
| ppliCode | BTREE | NON-UNIQUE | ppliCode, itemcode |

---

### 4.42 pm_presales_lend_2_rma_from_sms_history

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~35,449 行 |
| 数据大小 | 19.1 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | 0 |  |  |  |
| orderNumber | varchar(11) | YES | - | MUL |  |  |
| ppliCode | varchar(25) | YES | - | MUL | 借货执行单号 | 借货执行单号 |
| orderType | varchar(10) | YES | - |  |  |  |
| contract | varchar(25) | YES | - | MUL |  |  |
| customer | varchar(255) | YES | - |  |  |  |
| projectName | varchar(255) | YES | - |  |  |  |
| businessunit | varchar(50) | YES | - |  |  |  |
| office | varchar(20) | YES | - |  |  |  |
| dutyperson | varchar(10) | YES | - |  |  |  |
| itemcode | varchar(10) | YES | - |  |  |  |
| description | varchar(255) | YES | - |  |  |  |
| productfirstName | varchar(255) | YES | - |  |  |  |
| productName | varchar(255) | YES | - |  |  |  |
| orderQty | int(11) | YES | - |  |  |  |
| dlvQty | int(11) | YES | - |  |  |  |
| rmaQty | int(11) | YES | - |  |  |  |
| lineStatus | varchar(5) | YES | - |  |  |  |
| createDate | date | YES | - |  |  |  |
| lineId | int(11) | YES | - |  |  |  |
| systemId | int(11) | YES | - |  |  |  |
| canceled | char(1) | YES | - |  |  |  |
| dataSource | varchar(25) | YES | SMS |  |  |  |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contract | BTREE | NON-UNIQUE | contract, itemcode |
| orderNumber | BTREE | NON-UNIQUE | orderNumber, lineId |
| ppliCode | BTREE | NON-UNIQUE | ppliCode, itemcode |

---

### 4.43 pm_presales_lend_2_sale_from_sms

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| productfirstName | varchar(255) | YES | - |  |  |  |
| productName | varchar(128) | YES | - |  |  |  |
| projectCode | varchar(255) | NO | - | MUL |  |  |
| productSubCode | varchar(255) | NO | - |  |  |  |
| productSubModel | varchar(255) | YES | - |  |  |  |
| productSubName | varchar(255) | YES | - |  |  |  |
| num | int(11) | NO | - |  |  |  |
| borrowNum | int(11) | YES | - |  |  |  |
| contract | varchar(255) | YES | - |  |  |  |
| memo | text | YES | - |  |  |  |
| dataSource | varchar(25) | YES | SMS |  |  |  |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| projectCode | BTREE | NON-UNIQUE | projectCode |

---

### 4.44 pm_presales_lend_2_sale_from_sms_history

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~13,535 行 |
| 数据大小 | 3.9 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| productfirstName | varchar(255) | YES | - |  |  |  |
| productName | varchar(128) | YES | - |  |  |  |
| projectCode | varchar(255) | NO | - | MUL |  |  |
| productSubCode | varchar(255) | NO | - |  |  |  |
| productSubModel | varchar(255) | YES | - |  |  |  |
| productSubName | varchar(255) | YES | - |  |  |  |
| num | int(11) | NO | - |  |  |  |
| borrowNum | int(11) | YES | - |  |  |  |
| contract | varchar(255) | YES | - |  |  |  |
| memo | text | YES | - |  |  |  |
| dataSource | varchar(25) | YES | SMS |  |  |  |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| projectCode | BTREE | NON-UNIQUE | projectCode |

---

### 4.45 pm_presales_lend_detail_from_oa

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~3,024 行 |
| 数据大小 | 336.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | bigint(20) | NO | - |  |  |  |
| infoId | varchar(64) | YES | - |  |  |  |
| contractNum | varchar(100) | YES | - |  |  |  |
| deviceSerialnum | varchar(100) | YES | - |  |  |  |
| modelNum | varchar(100) | YES | - |  |  |  |
| applyCount | int(11) | YES | - |  |  |  |
| isSoftware | varchar(255) | YES | - |  |  |  |
| customInfo | json | YES | - |  |  |  |

---

### 4.46 pm_presales_lend_info_from_crm

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(1) | NO | - | PRI, AUTO_INCREMENT |  |  |
| lendInfoId | varchar(64) | NO | 0 |  |  |  |
| projectCode | varchar(64) | YES | - |  |  |  |
| projectName | varchar(765) | YES | - |  |  |  |
| dutyName | varchar(189) | YES | - |  |  |  |
| dutyContactWay | varchar(300) | YES | - |  |  |  |
| decPath | varchar(765) | YES | - |  |  |  |
| officeCode | varchar(765) | YES | - |  |  |  |
| marketName | varchar(255) | YES | - |  |  |  |
| systemName | varchar(128) | YES | - |  |  |  |
| expendName | varchar(255) | YES | - |  |  |  |
| industryName | varchar(128) | YES | - |  |  |  |
| pspm | varchar(257) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.47 pm_presales_lend_info_from_oa

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~1,963 行 |
| 数据大小 | 1.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| projectCode | varchar(100) | YES | - |  |  |  |
| processStartTime | datetime | YES | - |  |  |  |
| lendInfoId | varchar(64) | NO | - | MUL |  |  |
| processOrderNum | varchar(100) | YES | - |  |  |  |
| applyUserCode | varchar(25) | YES | - |  |  |  |
| applyUserName | varchar(25) | YES | - |  |  |  |
| applyDeptCode | varchar(25) | YES | - |  |  |  |
| applyDeptName | varchar(25) | YES | - |  |  |  |
| applyDate | datetime | YES | - |  |  |  |
| projectName | varchar(100) | YES | - |  |  |  |
| applyType | bigint(20) | YES | - |  |  |  |
| applyTypeName | varchar(25) | YES | - |  |  |  |
| salesUserCode | varchar(25) | YES | - |  |  |  |
| salesUserName | varchar(25) | YES | - |  |  |  |
| salesUserMobile | varchar(100) | YES | - |  |  |  |
| productLine | bigint(20) | YES | - |  |  |  |
| productLineName | varchar(255) | YES | - |  |  |  |
| marketName | varchar(255) | YES | - |  |  |  |
| systemName | varchar(255) | YES | - |  |  |  |
| expendName | varchar(255) | YES | - |  |  |  |
| industryName | varchar(255) | YES | - |  |  |  |
| applyCause | varchar(255) | YES | - |  |  |  |
| followUpPlan | varchar(255) | YES | - |  |  |  |
| testStartTime | datetime | YES | - |  |  |  |
| testEndTime | datetime | YES | - |  |  |  |
| authPlanDate | datetime | YES | - |  |  |  |
| authDate | datetime | YES | - |  |  |  |
| resellSuccessfully | varchar(255) | YES | - |  |  |  |
| useDays | int(11) | YES | - |  |  |  |
| resaleCertificateFile | varchar(2048) | YES | - |  |  |  |
| provideAuthFile | varchar(2048) | YES | - |  |  |  |
| infoFile | varchar(2048) | YES | - |  |  |  |
| customInfo | json | YES | - |  | 自定义信息 | 自定义信息 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| lendInfoId | BTREE | NON-UNIQUE | lendInfoId |
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.48 pm_presales_lend_info_from_sms

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~145 行 |
| 数据大小 | 64.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(1) | NO | - | PRI, AUTO_INCREMENT |  |  |
| lendInfoId | varchar(64) | NO | 0 |  |  |  |
| projectCode | varchar(64) | YES | - |  |  |  |
| projectName | varchar(765) | YES | - |  |  |  |
| dutyName | varchar(189) | YES | - |  |  |  |
| dutyContactWay | varchar(300) | YES | - |  |  |  |
| decPath | varchar(765) | YES | - |  |  |  |
| officeCode | varchar(765) | YES | - |  |  |  |
| marketName | varchar(255) | YES | - |  |  |  |
| systemName | varchar(128) | YES | - |  |  |  |
| expendName | varchar(255) | YES | - |  |  |  |
| industryName | varchar(128) | YES | - |  |  |  |
| pspm | varchar(257) | YES | - |  |  |  |
| dataSource | varchar(25) | YES | SMS |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.49 pm_presales_lend_info_from_sms_history

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(1) | NO | - | PRI, AUTO_INCREMENT |  |  |
| lendInfoId | varchar(64) | NO | 0 |  |  |  |
| projectCode | varchar(64) | YES | - |  |  |  |
| projectName | varchar(765) | YES | - |  |  |  |
| dutyName | varchar(189) | YES | - |  |  |  |
| dutyContactWay | varchar(300) | YES | - |  |  |  |
| decPath | varchar(765) | YES | - |  |  |  |
| officeCode | varchar(765) | YES | - |  |  |  |
| marketName | varchar(255) | YES | - |  |  |  |
| systemName | varchar(128) | YES | - |  |  |  |
| expendName | varchar(255) | YES | - |  |  |  |
| industryName | varchar(128) | YES | - |  |  |  |
| pspm | varchar(257) | YES | - |  |  |  |
| dataSource | varchar(25) | YES | SMS |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.50 pm_presales_lend_order_from_sms

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | 0 |  |  |  |
| orderNumber | varchar(11) | YES | - |  |  |  |
| ppliCode | varchar(25) | YES | - |  | 借货执行单号 | 借货执行单号 |
| orderType | varchar(10) | YES | - |  |  |  |
| contract | varchar(25) | YES | - |  |  |  |
| customer | varchar(255) | YES | - |  |  |  |
| projectName | varchar(255) | YES | - |  |  |  |
| businessunit | varchar(50) | YES | - |  |  |  |
| office | varchar(10) | YES | - |  |  |  |
| dutyperson | varchar(10) | YES | - |  |  |  |
| itemcode | varchar(10) | YES | - |  |  |  |
| description | varchar(255) | YES | - |  |  |  |
| orderQty | int(11) | YES | - |  |  |  |
| dlvQty | int(11) | YES | - |  |  |  |
| rmaQty | int(11) | YES | - |  |  |  |
| lineStatus | varchar(5) | YES | - |  |  |  |
| createDate | date | YES | - |  |  |  |
| lineId | int(11) | YES | - |  |  |  |
| systemId | int(11) | YES | - |  |  |  |
| canceled | char(1) | YES | - |  |  |  |
| discountVersion | varchar(255) | YES | - |  |  |  |
| borrowNum | bigint(12) | YES | - |  |  |  |
| dataSource | varchar(25) | YES | SMS |  |  |  |

---

### 4.51 pm_presales_lend_order_from_sms_history

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~20,196 行 |
| 数据大小 | 7.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | 0 |  |  |  |
| orderNumber | varchar(11) | YES | - |  |  |  |
| ppliCode | varchar(25) | YES | - |  | 借货执行单号 | 借货执行单号 |
| orderType | varchar(10) | YES | - |  |  |  |
| contract | varchar(25) | YES | - |  |  |  |
| customer | varchar(255) | YES | - |  |  |  |
| projectName | varchar(255) | YES | - |  |  |  |
| businessunit | varchar(50) | YES | - |  |  |  |
| office | varchar(10) | YES | - |  |  |  |
| dutyperson | varchar(10) | YES | - |  |  |  |
| itemcode | varchar(10) | YES | - |  |  |  |
| description | varchar(255) | YES | - |  |  |  |
| orderQty | int(11) | YES | - |  |  |  |
| dlvQty | int(11) | YES | - |  |  |  |
| rmaQty | int(11) | YES | - |  |  |  |
| lineStatus | varchar(5) | YES | - |  |  |  |
| createDate | date | YES | - |  |  |  |
| lineId | int(11) | YES | - |  |  |  |
| systemId | int(11) | YES | - |  |  |  |
| canceled | char(1) | YES | - |  |  |  |
| discountVersion | varchar(255) | YES | - |  |  |  |
| borrowNum | bigint(12) | YES | - |  |  |  |
| dataSource | varchar(25) | YES | SMS |  |  |  |

---

### 4.52 pm_presales_lend_product_from_sms

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~478 行 |
| 数据大小 | 112.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| lendInfoId | varchar(64) | NO | - |  |  |  |
| productfirstName | varchar(255) | YES | - |  |  |  |
| productName | varchar(128) | YES | - |  |  |  |
| productsubCode | varchar(765) | YES | - |  |  |  |
| productSubModel | varchar(765) | YES | - |  |  |  |
| productSubName | varchar(765) | YES | - |  |  |  |
| lendNum | int(11) | YES | - |  |  |  |
| memo | text | YES | - |  |  |  |
| dataSource | varchar(25) | YES | SMS |  |  |  |
| productfirstCode | varchar(64) | YES | - |  |  |  |
| productCode | varchar(64) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.53 pm_presales_lend_product_from_sms_history

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~4 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| lendInfoId | varchar(64) | NO | - |  |  |  |
| productfirstName | varchar(255) | YES | - |  |  |  |
| productName | varchar(128) | YES | - |  |  |  |
| productsubCode | varchar(765) | YES | - |  |  |  |
| productSubModel | varchar(765) | YES | - |  |  |  |
| productSubName | varchar(765) | YES | - |  |  |  |
| lendNum | int(11) | YES | - |  |  |  |
| memo | text | YES | - |  |  |  |
| dataSource | varchar(25) | YES | SMS |  |  |  |
| productfirstCode | varchar(64) | YES | - |  |  |  |
| productCode | varchar(64) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.54 pm_presales_project_callback

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 售前项目回访记录 |
| 数据量 | ~1,865 行 |
| 数据大小 | 368.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 售前回访问卷表 | 自增主键，回访记录唯一标识 |
| presalesId | int(11) | YES | - | MUL | 售前项目ID | 逻辑外键 -> pm_presales_project_header.presalesId |
| taskId | varchar(25) | YES | - | MUL | 任务ID | Activiti任务ID |
| quesnaireId | int(11) | YES | - | MUL | 问卷ID | 逻辑外键 -> pm_cl_quesnaire_result_header.id |
| quesnaireVersion | int(11) | YES | - |  | 问卷版本 | 问卷版本 |
| quesnaireState | int(11) | YES | - |  | 状态 -1 草稿 1已提交 | 0=未填写，1=已填写 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| presalesId | BTREE | NON-UNIQUE | presalesId |
| PRIMARY | BTREE | UNIQUE | id |
| quesnaireId | BTREE | NON-UNIQUE | quesnaireId |
| taskId | BTREE | NON-UNIQUE | taskId |

---

### 4.55 pm_presales_project_duration

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 售前项目各阶段耗时统计 |
| 数据量 | ~4,320 行 |
| 数据大小 | 416.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| presalesId | int(11) | NO | - | PRI |  | 逻辑外键 -> pm_presales_project_header.presalesId |
| instId | int(11) | YES | - |  | 流程实例ID | 流程实例ID |
| totalDuration | varchar(20) | YES | - |  | 开始时间 | 项目开始到结束的时间间隔 |
| serviceDuration | varchar(20) | YES | - |  | 指派服务经理时间 | 服务经理指派耗时 |
| programDuration | varchar(20) | YES | - |  | 指派项目经理时间 | 项目经理指派耗时 |
| testDuration | varchar(20) | YES | - |  | 测试开始时间 | 测试跟踪耗时 |
| callbackDuration | varchar(20) | YES | - |  | 回访开始时间 | 回访耗时 |
| serviceApproveDuration | varchar(100) | YES | - |  | 服务经理审批时间 | 服务经理审批耗时 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| pm_presales_project_duration_ibfk_1 | FOREIGN KEY | presalesId | pm_presales_project_header | presalesId |
| PRIMARY | PRIMARY KEY | presalesId | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | presalesId |

**外键列表**

| 外键名 | 本表字段 | 引用表 | 引用字段 |
|--------|----------|--------|----------|
| pm_presales_project_duration_ibfk_1 | presalesId | pm_presales_project_header | presalesId |

---

### 4.56 pm_presales_project_header

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 售前测试项目主表，关联Activiti工作流 |
| 数据量 | ~16,660 行 |
| 数据大小 | 8.8 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| presalesId | int(11) | NO | - | PRI, AUTO_INCREMENT | 售前项目主表 | 售前项目自增主键 |
| instId | varchar(64) | YES | - | MUL | activity工作流流程ID | Activiti流程实例ID |
| applyState | int(11) | YES | - |  | -1草稿 1 审批中 2结束 | -1=草稿，1=审批中，2=审批通过 |
| applyBy | varchar(25) | YES | - |  | 申请人 | 售前申请人编码 |
| applyTime | datetime | YES | - |  | 申请时间 | 售前申请时间 |
| endTime | datetime | YES | - |  | 申请结束时间 | 项目结束时间 |
| projectState | varchar(25) | YES | 10 |  | 项目状态 ，同售后项目状态  10 未创建 20 直接闭环 30 已创建 31待指派项目经理 32 项目经理跟踪 33工程管理部回访 100闭环 | 项目状态编码 |
| presalesCode | varchar(64) | YES | - |  | 售前项目编码 | 售前项目唯一编码 |
| projectCode | varchar(64) | YES | - | MUL | 项目编码 | 关联的原售后项目编码 |
| projectName | varchar(255) | YES | - |  | 项目名称 | 项目名称 |
| projectType | varchar(25) | YES | - |  |  | 逻辑外键 -> fnd_basic_data(dataTypeCode=presalesType) |
| marketName | varchar(25) | YES | - |  | 市场部名称 | 市场部名称 |
| systemName | varchar(25) | YES | - |  | 系统部名称 | 系统部名称 |
| expendName | varchar(25) | YES | - |  | 拓展部名称 | 拓展部名称 |
| industryName | varchar(25) | YES | - |  | 子行业名称 | 行业名称 |
| officeCode | varchar(25) | YES | - |  | 办事处编码 | 逻辑外键 -> fnd_department.departmentNum |
| salesman | varchar(25) | YES | - |  | 销售人员 | 销售人员姓名 |
| productManager | varchar(25) | YES | - |  | 产品经理 | 产品经理姓名 |
| salesmanLink | varchar(125) | YES | - |  | 销售人员联系方式 | 销售人员联系方式 |
| lendInfoId | varchar(64) | YES | - | MUL | SMS系统测试类借货申请主键，标识存在则不再刷新过来 | 逻辑外键 -> pm_presales_lend_info_from_oa |
| lendfiles | varchar(2048) | YES | - |  | 借货交付件 从SMS中同步过来 | 借货交付件信息 |
| confirmFileIds | varchar(2048) | YES | - |  | 现场测试服务确认单 | 逻辑外键 -> fnd_files.id |
| hasRma | int(1) | YES | 0 |  | 是否有未核销数据 | 是否存在未核销RMA数据 |
| hasTransfer | int(1) | YES | 0 |  | 是否发生借转销 | 是否存在借转销数据 |
| closeRemark | varchar(512) | YES | - |  | 闭环备注 | 项目关闭备注 |
| createBy | varchar(25) | YES | - |  | 数据创建人 | 记录创建用户编码 |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  | 数据创建时间 | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  | 数据更新人 | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  | 数据更新时间 | 记录最新更新时间 |
| effectiveFrom | datetime | YES | - |  | 数据有效开始时间 | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  | 数据有效结束时间 | 数据有效性结束时间，NULL=当前有效 |
| source | varchar(25) | NO | SMS |  | 数据来源 | 数据来源标识 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | presalesId | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| instId | BTREE | NON-UNIQUE | instId |
| lendInfoId | BTREE | NON-UNIQUE | lendInfoId |
| PRIMARY | BTREE | UNIQUE | presalesId |
| projectCode | BTREE | NON-UNIQUE | projectCode |

---

### 4.57 pm_presales_project_product_line

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 售前项目产品明细（数据量最大的表） |
| 数据量 | ~4,358,185 行 |
| 数据大小 | 1009.2 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| productLineId | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 产品线自增主键 |
| presalesId | int(11) | YES | - | MUL | 售前项目ID | 逻辑外键 -> pm_presales_project_header.presalesId |
| lendInfoId | varchar(64) | YES | - | MUL | 借货主表主键 | 借货信息ID |
| productFirstName | varchar(255) | YES | - |  | 产品一级 | 产品一级分类名称 |
| productTypeName | varchar(255) | YES | - |  | 产品类别 | 产品类型名称 |
| itemCode | varchar(255) | YES | - |  | item编码 | 产品编码 |
| itemModel | varchar(255) | YES | - |  | item型号 | 产品型号 |
| itemDesc | text | YES | - |  | item描述 | 产品描述 |
| price | double | YES | - |  | 目录价 | 目录价 |
| productNum | int(11) | NO | 0 |  | 产品数量 | 借货产品数量 |
| orderNum | int(11) | NO | 0 |  | 下单数量 | 下单数量 |
| deliverNum | int(11) | NO | 0 |  | 发货数量 | 发货数量 |
| hexiaoNum | int(11) | NO | 0 |  | 核销数量 | 核销数量 |
| transferNum | int(11) | NO | 0 |  | 转销数量 | 借转销数量 |
| remark | text | YES | - |  | 备注 | 备注 |
| effectiveFrom | datetime | YES | - |  | 数据有效开始时间 | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  | 数据有效结束时间 | 数据有效性结束时间，NULL=当前有效 |
| source | varchar(25) | YES | SMS |  |  | 数据来源标识，默认SMS |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | productLineId | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| lendInfoId | BTREE | NON-UNIQUE | lendInfoId |
| presalesId | BTREE | NON-UNIQUE | presalesId |
| PRIMARY | BTREE | UNIQUE | productLineId |

---

### 4.58 pm_presales_project_rma_info

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 售前项目RMA（退货授权）信息 |
| 数据量 | ~62,906 行 |
| 数据大小 | 20.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| orderNumber | varchar(11) | YES | - |  |  | SMS订单号 |
| ppliCode | varchar(25) | YES | - |  |  | 产品线编码 |
| orderType | varchar(10) | YES | - |  |  | 订单类型 |
| contract | varchar(25) | YES | - | MUL |  | 合同编号 |
| itemcode | varchar(10) | YES | - | MUL |  | 产品编码 |
| itemModel | varchar(255) | YES | - |  |  | 产品型号 |
| description | varchar(255) | YES | - |  |  | 产品描述 |
| productfirstName | varchar(255) | YES | - |  |  | 产品一级分类名称 |
| productName | varchar(255) | YES | - |  |  | 产品名称 |
| orderQty | decimal(32,0) | YES | - |  |  | 订单数量 |
| dlvQty | decimal(32,0) | YES | - |  |  | 已发货数量 |
| rmaQty | decimal(32,0) | YES | - |  |  | RMA退货数量 |
| createDate | date | YES | - |  |  | 订单创建日期 |
| canceled | char(1) | YES | - |  |  | 是否取消，Y=已取消，N=未取消 |
| deliveryDate | date | YES | - |  |  | 发货日期 |
| rmaDate | date | YES | - |  |  | RMA退货日期 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contract | BTREE | NON-UNIQUE | contract |
| itemcode | BTREE | NON-UNIQUE | itemcode |

---

### 4.59 pm_product_info_from_crm

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~8,469 行 |
| 数据大小 | 2.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| itemCode | varchar(100) | YES | - |  | item编码 | item编码 |
| productCode | varchar(100) | YES | - |  | 产品大类 | 产品大类 |
| productSubCode | varchar(100) | YES | - |  | 产品小类 | 产品小类 |
| itemModel | varchar(100) | YES | - |  | 产品型号 | 产品型号 |
| itemDesc | varchar(500) | YES | - |  | 产品描述 | 产品描述 |
| remark | text | YES | - |  | 备注 | 备注 |
| status | int(11) | YES | - |  |  |  |
| BU | varchar(100) | YES | - |  |  |  |
| productLine | varchar(100) | YES | - |  |  |  |
| orgId | int(11) | NO | - |  |  |  |
| createTime | datetime | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |
| statecode | int(11) | NO | - |  |  |  |
| statuscode | int(11) | YES | - |  |  |  |
| productStage | int(11) | YES | - |  |  |  |
| endOfSaleDate | datetime | YES | - |  | 停止销售时间 | 停止销售时间 |
| endOfSupportDate | datetime | YES | - |  | 停止支持时间 | 停止支持时间 |
| endOfLifeDate | datetime | YES | - |  | 停止生产时间 | 停止生产时间 |
| lastRenewalDate | datetime | YES | - |  | 停止续保时间 | 停止续保时间 |
| dataSource | varchar(100) | YES | - |  | 数据来源 | 数据来源 |

---

### 4.60 pm_project -- 项目头信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目主表，存储项目核心信息，是整个PMS系统的核心实体 |
| 数据量 | ~70,370 行 |
| 数据大小 | 56.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectId | int(11) | NO | - | PRI, AUTO_INCREMENT | 项目头信息主键,跟项目其他具体信息关联 | 项目唯一标识，关联项目其他子表 |
| projectType | varchar(45) | NO | 10 | MUL | 项目类型，用服售后:10，安服售后:afss，安服先行:afxx | 用服售后:10，安服售后:afss，安服先行:afxx |
| projectCode | varchar(45) | NO | - | MUL | 项目名称 | 项目唯一业务编码，由系统生成 |
| projectName | varchar(200) | YES | - |  | 项目名称 | 项目的业务名称 |
| projectState | varchar(11) | YES | - |  | 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态 | 项目阶段状态，1=初始创建，0=不予跟踪，对应fnd_basic_data(dataTypeCode=02) |
| isback | varchar(11) | YES | 30 |  | 30表示创建项目，32表示指定项目经理，34表示填写渠道信息 ,40表示工程管理部不予跟踪处理 ，42 表示项目经理选择不予跟踪 | 30=创建项目，32=指定项目经理，34=填写渠道信息，40=工程管理部不予跟踪，42=项目经理选择不予跟踪 |
| column001 | varchar(255) | YES | - | MUL | 办事处编码 | 逻辑外键 -> fnd_department.departmentNum |
| column002 | varchar(255) | YES | - |  | 客户编码--ERP | ERP系统中的客户编码 |
| column003 | varchar(255) | YES | - |  | 客户名称--ERP | ERP系统中的客户名称 |
| column004 | varchar(255) | YES | - |  | 市场部编码 | 市场部组织编码 |
| column005 | varchar(255) | YES | - |  | 系统部ID | 系统部组织ID |
| column006 | varchar(255) | YES | - |  | 拓展部ID | 拓展部组织ID |
| column007 | varchar(255) | YES | - |  | 子行业ID | 子行业分类ID |
| column008 | varchar(255) | YES | - |  | 不予跟踪原因 notGrantTailCause | notGrantTailCause，项目不予跟踪的原因说明 |
| column009 | datetime | YES | - |  | 订单创建时间 | 来自SMS系统的订单创建时间 |
| column010 | varchar(10) | YES | - |  | 项目类型 | 逻辑外键 -> fnd_basic_data(dataTypeCode=05)，项目等级分类 |
| column011 | varchar(10) | YES | - |  | 项目分类 | 项目业务分类 |
| column012 | varchar(2) | YES | - |  | 项目实施方式 | 实施方式编码，0/1/2/3/4对应不同实施模式 |
| columno12_readonly | int(2) | YES | -1 |  | 项目实施方式是否可以修改 -1表示可以改 其他值表示readonly | -1=可修改，其他值=只读（来自SMS的不可修改） |
| column013 | varchar(255) | YES | - |  | 最终客户名称 | 最终客户单位名称 |
| column014 | text | YES | - |  | 回退说明 | 项目回退时的说明文字 |
| customerProjectName | varchar(255) | YES | - |  | 客户项目名称 | 客户侧的项目名称 |
| salesType | varchar(25) | YES | 01 |  | 销售类型 | 01=正常，02=借转销，14=销售类借货 |
| majorProjectLevel | varchar(255) | YES | - |  | 重大项目级别 | 重大项目等级标识 |
| compId | int(2) | YES | 0 |  | 公司ID | 逻辑外键 -> fnd_company.id |
| createTime | datetime | YES | - |  | 记录数据创建时间 | 记录创建时间（指定服务经理时间） |
| createBy | varchar(45) | YES | - |  | 记录数据创建用户 | 记录创建用户 |
| updateTime | datetime | YES | - |  | 记录数据最新更新时间 | 记录最新更新时间 |
| updateBy | varchar(45) | YES | - |  | 记录数据最新更新用户 | 记录最新更新用户 |
| effectiveFrom | datetime | YES | - |  | 数据有效性开始时间 | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  | 数据有效性结束时间 | 数据有效性结束时间，NULL=当前有效 |
| disabled | bit(1) | YES | b'0' |  | 数据是否失效 | 0=有效，1=失效 |
| projectStartTime | datetime | YES | - |  | 项目开始实施时间 | 指定项目经理的时间 |
| projectRefreshTime | datetime | YES | - |  | 项目相关数据最后编辑时间 | 项目相关数据最后编辑时间 |
| projectCloseTime | datetime | YES | - |  | 项目闭环时间点 | 项目闭环的时间点 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段，存储serviceManagerCode/programManagerCode/programManagerCodeB等动态属性 |
| customConfig | json | YES | - |  | 自定义配置 | JSON配置字段 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | projectId | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| department | BTREE | NON-UNIQUE | column001 |
| PRIMARY | BTREE | UNIQUE | projectId |
| projectCode_index | BTREE | NON-UNIQUE | projectCode, projectType |
| projectType_projectId_IDX | BTREE | NON-UNIQUE | projectType, projectId |

---

### 4.61 pm_project_contract -- 项目对应的合同（可能多个）

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目对应的合同信息，一个项目组可关联多个合同 |
| 数据量 | ~79,021 行 |
| 数据大小 | 20.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 合同记录唯一标识 |
| contractNo | varchar(45) | NO | - | MUL | 合同号 | 合同编号，逻辑外键 -> pm_project_product_line.contractNo |
| projectGroupCode | varchar(45) | NO | - | MUL | 项目组编码 | 逻辑外键 -> pm_project_group.projectGroupCode |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(45) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(45) | YES | - |  |  | 记录最新更新用户编码 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contract_projectGroupCode_IDX | BTREE | NON-UNIQUE | contractNo, projectGroupCode |
| PRIMARY | BTREE | UNIQUE | id |
| projectGroupCode_contract_IDX | BTREE | NON-UNIQUE | projectGroupCode, contractNo |

---

### 4.62 pm_project_group -- 项目组对应项目

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目组信息，多个项目编码可归入同一项目组 |
| 数据量 | ~77,958 行 |
| 数据大小 | 8.0 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，项目组唯一标识 |
| projectGroupCode | varchar(45) | NO | - | UNI | 项目组组编码 | 项目组唯一编码 |
| projectGroupName | varchar(45) | YES | - |  | 项目组名称 | 项目组的业务名称 |
| projectType | varchar(25) | YES | 10 |  | 项目类型  默认10 为工程管理售后项目 | 默认10=工程管理售后项目 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(15) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(15) | YES | - |  |  | 记录最新更新用户编码 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |
| projectGroupCode_UNIQUE | UNIQUE | projectGroupCode | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectGroupCode_UNIQUE | BTREE | UNIQUE | projectGroupCode |

---

### 4.63 pm_project_group_relationship -- 项目对应对个合同号

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目编码与项目组的关联关系，支持项目拆分合并 |
| 数据量 | ~77,456 行 |
| 数据大小 | 19.1 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，关系记录唯一标识 |
| projectGroupCode | varchar(45) | NO | - | MUL | 项目组编码 | 逻辑外键 -> pm_project_group.projectGroupCode |
| projectCode | varchar(45) | YES | - | MUL | 项目编码 | 逻辑外键 -> pm_project.projectCode |
| mergeBranchMark | varchar(45) | YES | - |  | 项目拆分合并 | 标识项目拆分/合并的业务标记 |
| smsProjectCode | varchar(45) | YES | - | MUL | 原SMS项目编码 | 从SMS系统迁移过来的原始项目编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(45) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(45) | YES | - |  |  | 记录最新更新用户编码 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectCode | BTREE | NON-UNIQUE | projectCode |
| projectGroupCode | BTREE | NON-UNIQUE | projectGroupCode |
| smsProjectCode | BTREE | NON-UNIQUE | smsProjectCode |

---

### 4.64 pm_project_header_view_cache

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | pm_project_header视图的物化缓存表，加速项目列表查询 |
| 数据量 | ~71,993 行 |
| 数据大小 | 42.1 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectCode | varchar(45) | YES | - | MUL | 原SMS项目编码 | 项目编码 |
| subProjectCode | varchar(45) | NO | - |  | 项目名称 | 子项目/合同级别编码 |
| projectName | varchar(200) | YES | - |  | 项目名称 | 项目名称 |
| contractNo | varchar(45) | YES | - | MUL | 合同号 | 合同编号 |
| majorProjectLevel | varchar(255) | YES | - |  | 重大项目级别 | 重大项目级别 |
| officeName | varchar(20) | YES | - |  |  | 办事处名称（冗余） |
| customerName | varchar(255) | YES | - |  | 客户名称--ERP | 客户名称（冗余） |
| marketName | varchar(255) | YES | - |  | 市场部编码 | 市场部名称（冗余） |
| systemName | varchar(255) | YES | - |  | 系统部ID | 系统部名称（冗余） |
| expendName | varchar(255) | YES | - |  | 拓展部ID | 拓展部名称（冗余） |
| industryName | varchar(255) | YES | - |  | 子行业ID | 行业名称（冗余） |
| salesManCode | varchar(45) | YES | - |  |  | 销售人员编码 |
| salesManName | varchar(45) | YES | - |  |  | 销售人员姓名 |
| salesManTel | varchar(45) | YES | - |  |  | 销售人员电话 |
| salesManMail | varchar(100) | YES | - |  |  | 销售人员邮箱 |
| smCode | varchar(45) | YES | - |  | 人员编码,外部人员为空 | 服务经理编码 |
| smName | varchar(45) | YES | - |  | 人员名称 | 服务经理姓名 |
| pmCode1 | varchar(45) | YES | - |  | 人员编码,外部人员为空 | 项目经理1编码 |
| pmName1 | varchar(45) | YES | - |  | 人员名称 | 项目经理1姓名 |
| pmCode2 | varchar(45) | YES | - |  | 人员编码,外部人员为空 | 项目经理2编码 |
| pmName2 | varchar(45) | YES | - |  | 人员名称 | 项目经理2姓名 |
| compId | int(2) | YES | - |  | 公司ID | 公司ID |
| compName | varchar(128) | YES | - |  | 组织机构全名 | 公司名称（冗余） |
| ssfsName | varchar(255) | YES | - |  |  | 实施方式名称 |
| partnerChannel | varchar(45) | YES | - |  |  | 合作伙伴渠道名称 |
| projectType | varchar(4) | NO |  | MUL |  | 项目类型编码 |
| finalCustomerName | varchar(255) | YES | - |  | 最终客户名称 | 最终客户名称 |
| customerProjectName | varchar(255) | YES | - |  | 客户项目名称 | 客户项目名称 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | contractNo |
| projectCode | BTREE | NON-UNIQUE | projectCode |
| projectType | BTREE | NON-UNIQUE | projectType |

---

### 4.65 pm_project_incident_table_from_itr

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~142 行 |
| 数据大小 | 80.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| incidentId | varchar(255) | YES | - |  | 工单ID | 工单ID |
| ticketNo | varchar(255) | YES | - |  | 问题单号 | 问题单号 |
| STATUS | varchar(255) | YES | - |  | 工单状态 | 工单状态 |
| statusName | varchar(255) | YES | - |  | 工单状态名称 | 工单状态名称 |
| caseTopic | varchar(255) | YES | - |  | 问题单主题 | 问题单主题 |
| memo | text | YES | - |  | 描述 | 描述 |
| principal | varchar(255) | YES | - |  | 责任人 | 责任人 |
| principalName | varchar(255) | YES | - |  | 责任人名称 | 责任人名称 |
| accepter | varchar(255) | YES | - |  | 受理人 | 受理人 |
| accepterName | varchar(255) | YES | - |  | 受理人名称 | 受理人名称 |
| processor | varchar(255) | YES | - |  | 处理人 | 处理人 |
| processorName | varchar(255) | YES | - |  | 处理人名称 | 处理人名称 |
| supplied | varchar(255) | YES | - |  | 是否上报 | 是否上报 |
| questionType | varchar(255) | YES | - |  | 问题类型 | 问题类型 |
| questionLevel | varchar(255) | YES | - |  | 问题级别 | 问题级别 |
| title | varchar(255) | YES | - |  | 工单标题 | 工单标题 |
| acceptTime | varchar(255) | YES | - |  | 受理时间 | 受理时间 |
| productType | varchar(255) | YES | - |  | 设备类型 | 设备类型 |
| productModel | varchar(255) | YES | - |  | 设备型号 | 设备型号 |
| progress | varchar(255) | YES | - |  | 处理进展 | 处理进展 |
| questionReason | varchar(2048) | YES | - |  | 问题根因 | 问题根因 |
| solutionType | varchar(255) | YES | - |  | 解决方式 | 解决方式 |
| solutions | varchar(2048) | YES | - |  | 解决方案 | 解决方案 |
| rmaNo | varchar(255) | YES | - |  | RMA单号 | RMA单号 |
| accidentNo | varchar(255) | YES | - |  | 事故单号 | 事故单号 |
| caseType | varchar(255) | YES | - |  | Case类型 | Case类型 |
| reasonFstType | varchar(255) | YES | - |  | 原因大类 | 原因大类 |
| reasonSndType | varchar(255) | YES | - |  | 原因小类 | 原因小类 |
| projectCode | varchar(255) | YES | - |  | 项目编码 | 项目编码 |
| contractNo | varchar(255) | YES | - |  | 合同号 | 合同号 |
| barcode | varchar(255) | YES | - |  | 序列号 | 序列号 |
| bulletinNo | varchar(255) | YES | - |  | 技术公告编号 | 技术公告编号 |
| bugNo | varchar(255) | YES | - |  | Bug单编号 | Bug单编号 |
| productLine | varchar(255) | YES | - |  | 产品线 | 产品线 |
| customInfo | json | YES | - |  | 自定义信息 | 自定义信息 |
| url | varchar(255) | YES | - |  | URL | URL |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.66 pm_project_instruction -- 总部或领导对项目批示

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 总部或领导对项目的批示及反馈 |
| 数据量 | ~127 行 |
| 数据大小 | 64.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，批示记录唯一标识 |
| projectId | int(11) | YES | - | MUL | 项目头关联主键 | 逻辑外键 -> pm_project.projectId |
| instructionsInfo | text | YES | - |  | 批示内容或反馈内容 | 批示/反馈的具体内容 |
| instructionsTime | datetime | YES | - |  | 批示时间或反馈时间 | 批示/反馈的时间 |
| instructionsUser | varchar(45) | YES | - |  | 批示用户或反馈用户 | 批示/反馈的用户编码 |
| dataType | int(11) | YES | 0 |  | 数据类型  0 批示信息 1 批示反馈 | 0=批示信息，1=批示反馈 |
| instructionsId | int(11) | YES | - |  | 批示ID 针对批示反馈的信息 | 反馈对应的原始批示记录ID |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 4.67 pm_project_license_info_from_license -- License授权信息同步表

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | License授权信息同步表 |
| 数据量 | ~71 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| licenseCode | varchar(256) | NO | - |  | 授权码 | 授权码 |
| sn | varchar(255) | YES | - |  | 序列号 | 序列号 |
| specModel | varchar(256) | YES | - |  | 规格型号 | 规格型号 |
| contract | varchar(32) | YES | - |  | 合同号 | 合同号 |
| contractType | varchar(16) | YES | - |  | 合同类型 | 合同类型 |
| item | varchar(255) | YES | - |  | 项目编号 | 项目编号 |
| status | varchar(255) | YES | - |  | 状态 | 状态 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.68 pm_project_log -- 项目主要操作跟踪日志

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目主要操作跟踪日志 |
| 数据量 | ~6,411 行 |
| 数据大小 | 640.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，日志记录唯一标识 |
| projectId | int(11) | YES | - | MUL |  | 逻辑外键 -> pm_project.projectId |
| handleName | varchar(255) | YES | - |  | 操作名称 | 操作名称（如：指定项目经理） |
| handleDesc | varchar(255) | YES | - |  | 操作描述或原因 | 操作描述或原因说明 |
| handleUser | varchar(45) | YES | - |  | 操作用户 | 执行操作的用户编码 |
| taskStartTime | datetime | YES | - |  | 操作开始时间 | 操作开始时间 |
| handleEndTime | datetime | YES | - |  | 操作结束时间 | 操作结束时间 |
| handleState | int(11) | YES | - |  | 有无通知用户 0 无 1 有 | 0=无通知，1=已通知 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 4.69 pm_project_maintenance

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目维护/巡检记录，记录售后服务的详细过程 |
| 数据量 | ~184,753 行 |
| 数据大小 | 159.8 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，维护记录唯一标识 |
| projectId | int(11) | NO | - | MUL | 项目头信息主键 | 逻辑外键 -> pm_project.projectId |
| projectCode | varchar(45) | NO |  | MUL | 项目名称 | 项目编码，冗余存储 |
| projectName | varchar(200) | YES |  |  | 项目名称 | 项目名称，冗余存储 |
| projectType | int(11) | NO | 10 | MUL | 项目类型，售前:20/售后:10 | 售前:20/售后:10 |
| projectExecutionState | varchar(45) | YES |  |  | 项目实施状态 | 项目当前实施状态 |
| contractNo | varchar(255) | YES |  |  | 合同号 | 关联合同编号 |
| officeCode | varchar(25) | YES | - | MUL | 办事处编码 | 逻辑外键 -> fnd_department.departmentNum |
| compId | int(2) | YES | 1 |  | 所属公司 | 逻辑外键 -> fnd_company.id |
| type | varchar(45) | YES | - | MUL | 任务性质 | 服务任务性质编码 |
| category | varchar(45) | YES | - | MUL | 任务分类 | 服务任务分类编码 |
| subCategory | varchar(45) | YES | - | MUL | 任务小类 | 服务任务小类编码 |
| processTime | datetime | YES | - | MUL | 处理时间 | 服务处理时间 |
| processDesc | varchar(1024) | YES | - |  | 事项描述 | 服务事项描述 |
| processStep | varchar(1024) | YES | - |  | 解决进展 | 问题解决进展 |
| remainProblem | varchar(1024) | YES | - |  | 遗留问题 | 遗留问题描述 |
| transitHour | float | YES | 0 |  | 在途耗时(h) | 在途耗时（小时） |
| processHour | float | YES | 0 |  | 处理耗时(h) | 处理耗时（小时） |
| itemModel | varchar(255) | YES | - |  | 产品型号 | 服务产品型号 |
| softVersion | varchar(255) | YES | - |  | 在网版本 | 设备在网软件版本 |
| enabledFeatures | varchar(255) | YES | - |  | 启用功能 | 设备启用功能列表 |
| customTos | varchar(512) | YES | - |  | 自定义主送 | 自定义邮件主送人 |
| customCcs | varchar(512) | YES | - |  | 自定义抄送 | 自定义邮件抄送人 |
| hasReport | bit(1) | NO | b'0' |  | 是否有巡检报告 | 0=无巡检报告，1=有巡检报告 |
| quesnaireId | int(11) | YES | - |  | 问卷ID | 逻辑外键 -> pm_cl_quesnaire_result_header.id |
| deliverFileIds | varchar(255) | YES |  |  | 交付件，fnd_files id | 逻辑外键 -> fnd_files.id |
| warrantyStatus | varchar(25) | YES | - |  | 维保状态 | 项目维保状态 |
| industryName | varchar(25) | YES | - |  | 行业 | 客户所属行业 |
| userOffice | varchar(25) | YES | - |  | 用户办事处 | 用户所属办事处 |
| year | int(4) | YES | - |  | 所属年度 | 服务记录所属年度 |
| quarter | int(1) | YES | - |  | 所属季度 | 服务记录所属季度(1-4) |
| month | int(2) | YES | - |  | 所属月份 | 服务记录所属月份(1-12) |
| wsCount | int(2) | YES | - |  | 当前维保服务次数 | 当前维保服务次数 |
| wafCount | int(2) | YES | - |  | 当前其他服务次数 | 当前其他服务次数 |
| wsYearCount | int(2) | YES | - |  | 维保服务年次数 | 年度维保服务累计次数 |
| wafYearCount | int(2) | YES | - |  | 其他服务年次数 | 年度其他服务累计次数 |
| warrantyInfo | varchar(4096) | YES | - |  | 维保信息 | 维保服务详细信息 |
| serviceInfo | varchar(2048) | YES | - |  | 其他服务信息 | 其他服务详细信息 |
| remark | varchar(2048) | YES | - |  | 备注 | 备注说明 |
| createTime | datetime | YES | - | MUL | 创建时间 | 记录创建时间 |
| createBy | varchar(45) | YES | - | MUL | 创建用户 | 记录创建用户编码 |
| updateTime | datetime | YES | - |  | 最新更新时间 | 记录最新更新时间 |
| updateBy | varchar(45) | YES | - |  | 最新更新用户 | 记录最新更新用户编码 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| category | BTREE | NON-UNIQUE | category, subCategory |
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

### 4.70 pm_project_maintenance_sectary_from_sse

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 从SSE系统同步的维保秘书配置表，记录各部门对应的维保秘书信息（秘书工号、姓名、邮箱、电话），用于维保流程中的通知和分配。数据从SSE系统定期同步 |
| 数据量 | ~160 行 |
| 数据大小 | 48.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 记录唯一标识 |
| depNum | varchar(10) | NO | - | UNI | 同步自EHR系统 | 部门业务编号，唯一标识，逻辑外键 -> fnd_department.departmentNum |
| depName | varchar(20) | NO | - |  |  | 部门名称 |
| pDepNum | varchar(10) | YES | - |  |  | 上级部门编号 |
| pDepName | varchar(20) | YES | - |  |  | 上级部门名称 |
| sectary | varchar(10) | YES | - | MUL | 秘书工号 | 维保秘书工号 |
| sectaryName | varchar(255) | YES | - |  | 秘书姓名 | 维保秘书姓名 |
| sectaryEmail | varchar(255) | YES | - |  | 秘书邮箱 | 维保秘书邮箱地址 |
| sectaryPhone | varchar(255) | YES | - |  | 秘书电话 | 维保秘书联系电话 |
| status | int(4) | YES | 1 |  | 有效状态 | 记录状态，1=有效 |
| updateTime | datetime | YES | - |  |  | 数据最后更新时间（同步时间） |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |
| depNum | UNIQUE | depNum | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| depNum | BTREE | UNIQUE | depNum |
| PRIMARY | BTREE | UNIQUE | id |
| sectary | BTREE | NON-UNIQUE | sectary |

---

### 4.71 pm_project_maintenance_service_delivery

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 维保服务交付记录表，记录每次维保巡检服务的交付情况，包括交付时间、周期、数量等，用于跟踪维保服务的实际交付进度和完成情况 |
| 数据量 | ~66 行 |
| 数据大小 | 64.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| maintenanceId | int(11) | NO | - | MUL |  |  |
| projectId | int(11) | YES | - | MUL |  |  |
| projectType | varchar(25) | YES | 10 |  |  |  |
| serviceType | varchar(25) | YES | - | MUL |  |  |
| processTime | datetime | YES | - |  |  |  |
| year | int(4) | YES | - |  |  |  |
| quarter | int(2) | YES | - |  |  |  |
| month | int(2) | YES | - |  |  |  |
| deliveried | int(1) | YES | 0 |  |  |  |
| startDate | date | YES | - |  |  |  |
| endDate | date | YES | - |  |  |  |
| count | int(2) | YES | 0 |  |  |  |
| yearCount | int(2) | YES | 0 |  |  |  |
| remark | varchar(2048) | YES | - |  |  |  |
| createBy | varchar(25) | YES | - |  |  |  |
| createTime | datetime | YES | - |  |  |  |
| updateBy | varchar(25) | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| maintenanceId | BTREE | NON-UNIQUE | maintenanceId |
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId, projectType |
| serviceType | BTREE | NON-UNIQUE | serviceType |

---

### 4.72 pm_project_maintenance_view

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | MyISAM |
| 业务含义 | 维保视图缓存表，是pm_project_maintenance的宽表物化缓存，冗余了项目、组织、人员、分类等多个维度的名称字段，用于加速维保列表查询，避免多表JOIN。包含中文列名为问卷评分维度字段 |
| 数据量 | ~183,607 行 |
| 数据大小 | 122.7 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | 0 |  |  | 维保记录ID，对应pm_project_maintenance.id |
| projectId | int(11) | NO | - |  | 项目头信息主键 | 逻辑外键 -> pm_project.projectId |
| projectCode | varchar(45) | NO |  |  | 项目名称 | 项目业务编码 |
| projectName | varchar(200) | YES |  |  | 项目名称 | 项目业务名称 |
| projectType | int(11) | NO | 10 |  | 项目类型，售前:20/售后:10 | 10=用服售后，其他值对应不同类型 |
| projectExecutionState | varchar(45) | YES |  |  | 项目实施状态 | 项目当前执行状态编码 |
| contractNo | varchar(255) | YES |  |  | 合同号 | 关联合同编号 |
| officeCode | varchar(25) | YES | - |  | 办事处编码 | 逻辑外键 -> fnd_department.departmentNum |
| compId | int(2) | YES | 1 |  | 所属公司 | 逻辑外键 -> fnd_company.id |
| type | varchar(45) | YES | - |  | 任务性质 | 维保服务类型编码 |
| category | varchar(45) | YES | - |  | 任务分类 | 维保服务分类编码 |
| subCategory | varchar(45) | YES | - |  | 任务小类 | 维保服务子分类编码 |
| processTime | datetime | YES | - |  | 处理时间 | 维保处理时间 |
| processDesc | varchar(1024) | YES | - |  | 事项描述 | 维保处理过程描述 |
| processStep | varchar(1024) | YES | - |  | 解决进展 | 维保处理步骤说明 |
| remainProblem | varchar(1024) | YES | - |  | 遗留问题 | 维保后仍遗留的问题 |
| transitHour | float | YES | 0 |  | 在途耗时(h) | 工程师在途耗时（小时） |
| processHour | float | YES | 0 |  | 处理耗时(h) | 工程师现场处理耗时（小时） |
| itemModel | varchar(255) | YES | - |  | 产品型号 | 维保设备型号 |
| softVersion | varchar(255) | YES | - |  | 在网版本 | 维保设备软件版本 |
| enabledFeatures | varchar(255) | YES | - |  | 启用功能 | 设备已启用的功能特性 |
| customTos | varchar(512) | YES | - |  | 自定义主送 | 客户技术联系人 |
| customCcs | varchar(512) | YES | - |  | 自定义抄送 | 客户抄送联系人 |
| hasReport | bit(1) | NO | b'0' |  | 是否有巡检报告 | 是否上传了维保报告，0=无，1=有 |
| quesnaireId | int(11) | YES | - |  | 问卷ID | 关联回访问卷ID |
| deliverFileIds | varchar(255) | YES |  |  | 交付件，fnd_files id | 交付件文件ID，逗号分隔 |
| warrantyStatus | varchar(25) | YES | - |  | 维保状态 | 维保状态编码 |
| industryName | varchar(25) | YES | - |  | 行业 | 客户所属行业名称（冗余） |
| userOffice | varchar(25) | YES | - |  | 用户办事处 | 维保人员所属办事处编码 |
| year | int(4) | YES | - |  | 所属年度 | 维保所属年份 |
| quarter | int(1) | YES | - |  | 所属季度 | 维保所属季度 |
| month | int(2) | YES | - |  | 所属月份 | 维保所属月份 |
| wsCount | int(2) | YES | - |  | 当前维保服务次数 | 本周期维保服务次数 |
| wafCount | int(2) | YES | - |  | 当前其他服务次数 | 本周期WAF（保修期外收费）次数 |
| wsYearCount | int(2) | YES | - |  | 维保服务年次数 | 年度累计维保服务次数 |
| wafYearCount | int(2) | YES | - |  | 其他服务年次数 | 年度累计WAF次数 |
| warrantyInfo | varchar(4096) | YES | - |  | 维保信息 | 维保详细信息 |
| serviceInfo | varchar(2048) | YES | - |  | 其他服务信息 | 服务详细信息 |
| remark | varchar(2048) | YES | - |  | 备注 | 备注说明 |
| createTime | datetime | YES | - |  | 创建时间 | 记录创建时间 |
| createBy | varchar(45) | YES | - |  | 创建用户 | 记录创建用户 |
| updateTime | datetime | YES | - |  | 最新更新时间 | 记录最后更新时间 |
| updateBy | varchar(45) | YES | - |  | 最新更新用户 | 记录最后更新用户 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段 |
| officeName | varchar(20) | YES | - |  |  | 办事处名称（冗余自fnd_department） |
| userOfficeName | varchar(20) | YES | - |  |  | 维保人员所属办事处名称（冗余） |
| serviceManager | varchar(45) | YES | - |  | 人员名称 | 服务经理姓名（冗余自pm_project） |
| programManagerA | varchar(45) | YES | - |  | 人员名称 | 第一项目经理姓名（冗余） |
| programManagerB | varchar(45) | YES | - |  | 人员名称 | 第二项目经理姓名（冗余） |
| createUser | varchar(174) | YES | - |  |  | 创建人姓名（冗余，含工号+姓名拼接） |
| typeName | varchar(255) | YES | - |  |  | 维保类型中文名称（冗余自fnd_basic_data） |
| projectExecutionStateName | varchar(255) | YES | - |  |  | 项目执行状态中文名称（冗余） |
| categoryName | varchar(258) | YES | - |  |  | 维保分类中文名称（冗余） |
| subCategoryName | varchar(255) | YES | - |  |  | 维保子分类中文名称（冗余） |
| marketName | varchar(255) | YES | - |  |  | 市场部名称（冗余） |
| systemName | varchar(255) | YES | - |  |  | 系统部名称（冗余） |
| expendName | varchar(255) | YES | - |  |  | 拓展部名称（冗余） |
| industryNameN | varchar(255) | YES | - |  |  | 子行业名称（冗余，与industryName区分） |
| finalCustomerName | varchar(255) | YES | - |  | 最终客户名称 | 最终客户名称（冗余） |
| salerName | varchar(91) | YES | - |  |  | 销售人员姓名（冗余） |
| quesnaireResultHeaderId | int(11) | YES | - |  | 回访结果头信息Id | 逻辑外键 -> pm_cl_quesnaire_result_header.id |
| 工程师技术能力 | longtext | YES | - |  |  | 问卷评分维度：工程师技术能力（中文列名） |
| 服务及时性 | longtext | YES | - |  |  | 问卷评分维度：服务及时性（中文列名） |
| 服务水平及规范性 | longtext | YES | - |  |  | 问卷评分维度：服务水平及规范性（中文列名） |
| warrantyStatusName | varchar(4) | YES | - |  |  | 维保状态中文名称（冗余） |
| syncTime | datetime | NO | - |  |  | 缓存数据最后同步刷新时间 |

---

### 4.73 pm_project_market_relations_from_sms

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~528 行 |
| 数据大小 | 80.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| marketCode | varchar(64) | YES | - |  |  |  |
| marketName | varchar(255) | YES | - |  |  |  |
| systemCode | varchar(64) | YES | - |  |  |  |
| systemName | varchar(255) | YES | - |  |  |  |
| expendCode | varchar(64) | YES | - |  |  |  |
| expendName | varchar(255) | YES | - |  |  |  |
| industryCode | varchar(64) | YES | - |  |  |  |
| industryName | varchar(255) | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.74 pm_project_member -- 项目相关人员信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目相关人员信息，通过memberRole区分角色(10=销售,20=服务经理,30=项目经理) |
| 数据量 | ~302,428 行 |
| 数据大小 | 69.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，成员记录唯一标识 |
| projectId | int(11) | YES | - | MUL |  | 逻辑外键 -> pm_project.projectId 或 pm_presales_project_header.presalesId |
| projectType | varchar(25) | YES | 10 |  | 项目类型 售后10 或售前 20 详见fnd_basic_data | 售后10/售前20，详见fnd_basic_data |
| memberRole | varchar(45) | YES | - |  | 人员在项目中所处的角色 | 10=销售人员,20=服务经理,30=项目经理 |
| memberCode | varchar(45) | YES | - | MUL | 人员编码,外部人员为空 | 逻辑外键 -> fnd_user_info.username，外部人员为空 |
| memberName | varchar(45) | YES | - |  | 人员名称 | 项目成员的真实姓名 |
| phoneNum | varchar(20) | YES | - |  | 电话 | 项目成员联系电话 |
| email | varchar(45) | YES | - |  | 邮箱 | 项目成员邮箱地址 |
| fromFlag | varchar(2) | YES | 0 |  | 信息来源，1表示来源于项目信息，2表示来源于成员信息 | 1=来源于项目信息，2=来源于成员信息 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(45) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(15) | YES | - |  |  | 记录最新更新用户编码 |
| effectiveTo | datetime | YES | - |  | 有效结束时间 | NULL=当前有效，非NULL=已失效 |
| effectiveFrom | datetime | YES | - |  | 有效开始时间 | 数据有效性开始时间（软删除模式） |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| memberCode_IDX | BTREE | NON-UNIQUE | memberCode, projectId, projectType |
| PRIMARY | BTREE | UNIQUE | id |
| projectId_role | BTREE | NON-UNIQUE | projectId, memberRole |
| projectId_type | BTREE | NON-UNIQUE | projectId, projectType |

---

### 4.75 pm_project_notification

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目通知信息 |
| 数据量 | ~152,161 行 |
| 数据大小 | 18.0 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，通知记录唯一标识 |
| notifySubject | varchar(255) | YES | - |  | 通知标题 | 通知标题 |
| notifyContent | text | YES | - |  | 通知内容 | 通知正文内容 |
| projectId | int(11) | YES | - | MUL | 相关项目ID | 逻辑外键 -> pm_project.projectId |
| createTime | datetime | YES | - |  | 创建时间 | 记录创建时间 |
| createBy | varchar(25) | YES | - |  | 创建用户 | 记录创建用户编码 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 4.76 pm_project_notification_state

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 通知的阅读状态记录 |
| 数据量 | ~9,905 行 |
| 数据大小 | 1.7 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，状态记录唯一标识 |
| notifyId | int(11) | YES | - | MUL |  | 逻辑外键 -> pm_project_notification.id |
| notifyObject | varchar(25) | YES | - |  | 通知主题，系统用户 | 通知接收用户编码 |
| notifyState | int(11) | YES | - |  | 通知状态，有无通知 0 无 1 有 | 0=未读，1=已读 |
| checkTime | datetime | YES | - |  | 用户查看通知时间 | 用户查看通知的时间 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| notifyId | BTREE | NON-UNIQUE | notifyId |
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.77 pm_project_product_af_from_sms

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~21,608 行 |
| 数据大小 | 6.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | varchar(64) | NO | 0 |  |  |  |
| projectCode | varchar(255) | NO | - |  |  |  |
| orderExecNumber | varchar(255) | YES | - |  |  |  |
| corporationCode | varchar(50) | YES | - |  | 公司编码 | 公司编码 |
| ssfrId | varchar(64) | YES | - |  | 安全服务先行核销ID | 安全服务先行核销ID |
| productCode | varchar(255) | YES | - |  |  |  |
| productfirstCode | varchar(255) | YES | - |  |  |  |
| productName | varchar(128) | YES | - |  |  |  |
| productfirstName | varchar(255) | YES | - |  |  |  |
| productsubCode | varchar(255) | YES | - |  |  |  |
| productSubModel | varchar(255) | YES | - |  |  |  |
| productSubName | varchar(255) | YES | - |  |  |  |
| num | int(11) | YES | - |  |  |  |
| borrowNum | int(11) | YES | - |  |  |  |
| price | decimal(19,6) | YES | - |  |  |  |
| purchaseDiscount | decimal(19,6) | YES | - |  |  |  |
| purchasePrice | decimal(29,2) | YES | - |  |  |  |
| dataSource | varchar(25) | YES | CRM |  |  |  |
| lineType | varchar(25) | YES | orderLine |  | 行类型，orderLine:订单行，leaseLine:租赁行 | 行类型，orderLine:订单行，leaseLine:租赁行 |
| customInfo | json | YES | - |  | 自定义信息 | 自定义信息 |

---

### 4.78 pm_project_product_af_from_sms_history

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~18,054 行 |
| 数据大小 | 5.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | 0 |  |  |  |
| projectCode | varchar(255) | NO | - |  |  |  |
| orderExecNumber | varchar(255) | YES | - |  |  |  |
| corporationCode | varchar(50) | YES | - |  | 公司编码 | 公司编码 |
| ssfrId | varchar(64) | YES | - |  | 安全服务先行核销ID | 安全服务先行核销ID |
| productCode | varchar(255) | YES | - |  |  |  |
| productfirstCode | varchar(255) | YES | - |  |  |  |
| productName | varchar(128) | YES | - |  |  |  |
| productfirstName | varchar(255) | YES | - |  |  |  |
| productsubCode | varchar(255) | YES | - |  |  |  |
| productSubModel | varchar(255) | YES | - |  |  |  |
| productSubName | varchar(255) | YES | - |  |  |  |
| num | int(11) | YES | - |  |  |  |
| borrowNum | int(11) | YES | - |  |  |  |
| price | decimal(19,6) | YES | - |  |  |  |
| purchaseDiscount | decimal(19,6) | YES | - |  |  |  |
| purchasePrice | decimal(29,2) | YES | - |  |  |  |
| dataSource | varchar(25) | YES | SMS |  |  |  |
| lineType | varchar(25) | YES | orderLine |  | 行类型，orderLine:订单行，leaseLine:租赁行 | 行类型，orderLine:订单行，leaseLine:租赁行 |
| customInfo | json | YES | - |  | 自定义信息 | 自定义信息 |

---

### 4.79 pm_project_product_config_level_info_from_crm

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~45 行 |
| 数据大小 | 48.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectCode | varchar(100) | YES | - |  |  |  |
| orderExecNumber | varchar(255) | YES |  |  |  |  |
| itemGroup | int(11) | YES | - |  |  |  |
| itemCode | varchar(100) | YES | - |  |  |  |
| parentCode | varchar(1000) | YES | - |  |  |  |
| quantity | int(11) | YES | - |  |  |  |
| bomPaths | varchar(1000) | YES | - |  |  |  |
| itemModel | varchar(100) | YES | - |  |  |  |
| itemDesc | varchar(500) | YES | - |  |  |  |
| level | int(11) | YES | 0 |  |  |  |
| dataSource | varchar(25) | YES | CRM |  |  |  |

---

### 4.80 pm_project_product_lease_line_from_crm

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据大小 | 149.2 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectCode | varchar(255) | NO |  | MUL | 项目编码 | 项目编码 |
| orderExecNumber | varchar(255) | YES | - |  | 执行单号 | 执行单号 |
| productFirstName | varchar(255) | NO |  |  | 产品类型 | 产品类型 |
| productName | varchar(128) | NO |  |  | 产品名 | 产品名 |
| productSubCode | varchar(255) | NO |  |  | item编码 | item编码 |
| productSubModel | varchar(255) | YES | - |  | item类型 | item类型 |
| productSubName | varchar(255) | YES | - |  | item描述 | item描述 |
| num | int(11) | NO | 0 |  | 订单数量 | 订单数量 |
| memo | mediumtext | YES | - |  | 备注 | 备注 |
| leaseDuration | decimal(16,2) | YES | - |  | 租赁月数 | 租赁月数 |
| dataSource | varchar(25) | YES | CRM |  |  |  |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| projectCode_IDX | BTREE | NON-UNIQUE | projectCode, orderExecNumber |

---

### 4.81 pm_project_product_line -- 订单产品信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 订单产品信息，记录项目下的产品明细 |
| 数据量 | ~185,819 行 |
| 数据大小 | 53.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | MUL, AUTO_INCREMENT |  | 自增主键，产品线记录唯一标识 |
| projectId | int(11) | YES | - | MUL | 关联主表 | 逻辑外键 -> pm_project.projectId |
| contractNo | varchar(45) | YES | - | MUL | 合同号 | 逻辑外键 -> pm_project_contract.contractNo |
| itemCode | varchar(15) | YES | - | MUL | 产品编码 | ERP系统产品编码 |
| itemName | varchar(255) | YES | - |  | 产品名称 | 产品名称 |
| projectQuantity | int(11) | YES | - |  | 项目产品数量 | 项目产品总数量 |
| orderQuantity | int(11) | YES | - |  | 产品订单数量 | 已下单产品数量 |
| deliverQuantity | int(11) | YES | - |  | 已发货数量 | 已发货产品数量 |
| openQuantity | int(11) | YES | - |  | 未发货数量 | 未发货产品数量 |
| orderNumber | varchar(25) | YES | - |  |  | ERP订单号 |
| lineNum | varchar(25) | YES | - |  |  | 订单行号 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | contractNo |
| id | BTREE | NON-UNIQUE | id |
| itemCode | BTREE | NON-UNIQUE | itemCode |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 4.82 pm_project_product_line_real

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目实际产品线明细表，记录项目下真实的产品明细信息，与pm_project_product_line（订单产品）互补，用于区分订单产品与实际交付产品的差异 |
| 数据量 | ~3,812 行 |
| 数据大小 | 1.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 记录唯一标识 |
| projectId | int(11) | NO | - | MUL | ph.projectId,外键 | 逻辑外键 -> pm_project.projectId |
| contractNo | varchar(25) | NO |  |  | 合同号 | 关联合同编号 |
| projectCode | varchar(255) | NO |  |  | 项目编码 | 项目业务编码 |
| orderExecNumber | varchar(255) | NO |  |  | 执行单号 | ERP订单执行行号 |
| productFirstName | varchar(255) | YES |  |  | 产品分类？ | 产品一级分类名称 |
| productName | varchar(128) | YES |  |  | 产品名 | 产品名称 |
| productSubCode | varchar(255) | NO |  |  | item编码 | 产品子类编码 |
| productSubModel | varchar(255) | NO |  |  | item类型 | 产品子类型号 |
| productSubName | varchar(255) | NO |  |  | item名 | 产品子类名称 |
| num | int(11) | YES | 0 |  | 订单数量 | 产品数量 |
| memo | mediumtext | YES | - |  | 备注 | 备注信息 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 4.83 pm_project_property_af_from_sms

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~190 行 |
| 数据大小 | 160.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | 0 |  |  |  |
| orderExecNumber | varchar(60) | YES | - |  |  |  |
| projectCode | varchar(255) | NO | - |  |  |  |
| projectName | varchar(765) | YES | - |  |  |  |
| marketCode | varchar(64) | YES | - |  |  |  |
| officeCode | varchar(765) | YES | - |  |  |  |
| expendId | varchar(64) | YES | - |  |  |  |
| marketName | varchar(255) | YES | - |  |  |  |
| officeName | varchar(128) | YES | - |  |  |  |
| systemName | varchar(255) | YES | - |  |  |  |
| salesManCode | varchar(60) | YES | - |  |  |  |
| systemId | varchar(64) | YES | - |  |  |  |
| industryId | varchar(64) | YES | - |  |  |  |
| salesManName | varchar(128) | YES | - |  |  |  |
| expendName | varchar(255) | YES | - |  |  |  |
| industryName | varchar(255) | YES | - |  |  |  |
| serviceTypeName | varchar(128) | YES | - |  |  |  |
| channelName | varchar(765) | YES | - |  |  |  |
| engineeFee | decimal(19,2) | YES | - |  | 安全服务先行类借货有值，表示出货价 | 安全服务先行类借货有值，表示出货价 |
| objId | varchar(64) | YES | - |  | 参数1 | 参数1 |
| applyType | varchar(60) | YES | - |  |  |  |
| corporationCode | varchar(10) | YES | - |  | 公司编码 | 公司编码 |
| customerProjectName | varchar(765) | YES | - |  |  |  |
| finalCustomerName | varchar(765) | YES | - |  |  |  |
| agentName | varchar(765) | YES | - |  |  |  |
| pspm | varchar(765) | YES | - |  |  |  |
| pspmName | varchar(257) | YES | - |  |  |  |
| salesMenTel | varchar(300) | YES | - |  |  |  |
| decPath | varchar(765) | YES | - |  |  |  |
| requireInDate | date | YES | - |  |  |  |
| receiveMen | varchar(450) | YES | - |  |  |  |
| reveiveContactWay | varchar(300) | YES | - |  |  |  |
| receiveAddress | varchar(765) | YES | - |  |  |  |
| lendCause | text | YES | - |  |  |  |
| projectType | varchar(4) | NO |  |  |  |  |
| projectMoney | decimal(16,2) | YES | 0.00 |  | 出货价 | 出货价 |
| afProjectMoney | decimal(16,2) | YES | 0.00 |  | 安服出货价 | 安服出货价 |
| submitTime | datetime | YES | - |  | 提交时间 | 提交时间 |
| predBidDate | datetime | YES | - |  | 项目投标时间 | 项目投标时间 |
| linkmanName | varchar(255) | YES | - |  | 客户联系人 | 客户联系人 |
| linkmanTel | varchar(64) | YES | - |  | 客户联系方式 | 客户联系方式 |
| customInfo | json | YES | - |  | 自定义信息 | 自定义信息 |
| dataSource | varchar(25) | YES | CRM |  |  |  |

---

### 4.84 pm_project_property_af_from_sms_history

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~129 行 |
| 数据大小 | 112.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | 0 |  |  |  |
| orderExecNumber | varchar(60) | YES | - |  |  |  |
| projectCode | varchar(255) | NO | - |  |  |  |
| projectName | varchar(765) | YES | - |  |  |  |
| marketCode | varchar(64) | YES | - |  |  |  |
| officeCode | varchar(765) | YES | - |  |  |  |
| expendId | varchar(64) | YES | - |  |  |  |
| marketName | varchar(255) | YES | - |  |  |  |
| officeName | varchar(128) | YES | - |  |  |  |
| systemName | varchar(255) | YES | - |  |  |  |
| salesManCode | varchar(60) | YES | - |  |  |  |
| systemId | varchar(64) | YES | - |  |  |  |
| industryId | varchar(64) | YES | - |  |  |  |
| salesManName | varchar(128) | YES | - |  |  |  |
| expendName | varchar(255) | YES | - |  |  |  |
| industryName | varchar(255) | YES | - |  |  |  |
| serviceTypeName | varchar(128) | YES | - |  |  |  |
| channelName | varchar(765) | YES | - |  |  |  |
| engineeFee | decimal(19,2) | YES | - |  | 安全服务先行类借货有值，表示出货价 | 安全服务先行类借货有值，表示出货价 |
| objId | varchar(64) | YES | - |  | 参数1 | 参数1 |
| applyType | varchar(60) | YES | - |  |  |  |
| corporationCode | varchar(10) | YES | - |  | 公司编码 | 公司编码 |
| customerProjectName | varchar(765) | YES | - |  |  |  |
| finalCustomerName | varchar(765) | YES | - |  |  |  |
| agentName | varchar(765) | YES | - |  |  |  |
| pspm | varchar(765) | YES | - |  |  |  |
| pspmName | varchar(257) | YES | - |  |  |  |
| salesMenTel | varchar(300) | YES | - |  |  |  |
| decPath | varchar(765) | YES | - |  |  |  |
| requireInDate | date | YES | - |  |  |  |
| receiveMen | varchar(450) | YES | - |  |  |  |
| reveiveContactWay | varchar(300) | YES | - |  |  |  |
| receiveAddress | varchar(765) | YES | - |  |  |  |
| lendCause | text | YES | - |  |  |  |
| projectType | varchar(4) | NO |  |  |  |  |
| projectMoney | decimal(16,2) | YES | 0.00 |  | 出货价 | 出货价 |
| afProjectMoney | decimal(16,2) | YES | 0.00 |  | 安服出货价 | 安服出货价 |
| submitTime | datetime | YES | - |  | 提交时间 | 提交时间 |
| predBidDate | datetime | YES | - |  | 项目投标时间 | 项目投标时间 |
| linkmanName | varchar(255) | YES | - |  | 客户联系人 | 客户联系人 |
| linkmanTel | varchar(64) | YES | - |  | 客户联系方式 | 客户联系方式 |
| customInfo | json | YES | - |  | 自定义信息 | 自定义信息 |
| dataSource | varchar(25) | YES | SMS |  |  |  |

---

### 4.85 pm_project_property_from_sms

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~612 行 |
| 数据大小 | 336.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| orderExecNumber | varchar(25) | YES | - | MUL |  |  |
| projectCode | varchar(25) | YES | - | MUL |  |  |
| projectName | varchar(255) | YES | - |  |  |  |
| salesManCode | varchar(45) | YES | - |  |  |  |
| salesManName | varchar(45) | YES | - |  |  |  |
| marketCode | varchar(64) | YES | - |  |  |  |
| marketName | varchar(255) | YES | - |  |  |  |
| systemId | varchar(64) | YES | - |  |  |  |
| systemName | varchar(255) | YES | - |  |  |  |
| expendId | varchar(64) | YES | - |  |  |  |
| expendName | varchar(255) | YES | - |  |  |  |
| industryId | varchar(64) | YES | - |  |  |  |
| industryName | varchar(255) | YES | - |  |  |  |
| officeCode | varchar(15) | YES | - |  |  |  |
| officeName | varchar(15) | YES | - |  |  |  |
| serviceTypeName | varchar(10) | YES | - |  |  |  |
| channelName | varchar(255) | YES | - |  | 出货代理商名称 | 出货代理商名称 |
| engineeFee | varchar(25) | YES | - |  | 工程服务费 | 工程服务费 |
| objId | varchar(64) | YES | - |  | 参数1 | 参数1 |
| applyType | varchar(25) | YES | - |  | 参数2 | 参数2 |
| corporationCode | varchar(25) | YES | 01 |  | 公司编码 | 公司编码 |
| customerProjectName | varchar(255) | YES | - |  | 客户项目名称 | 客户项目名称 |
| finalCustomerName | varchar(255) | YES | - |  | 最终客户名称 | 最终客户名称 |
| agentName | varchar(500) | YES | - |  | 代理商名称 | 代理商名称 |
| projectMoney | decimal(16,2) | YES | 0.00 |  | 出货价 | 出货价 |
| submitTime | datetime | YES | - |  | 项目创建时间 | 项目创建时间 |
| majorProjectLevel | varchar(255) | YES | - |  | 重大项目级别 | 重大项目级别 |
| predBidDate | datetime | YES | - |  | 项目投标时间 | 项目投标时间 |
| linkmanName | varchar(255) | YES | - |  | 客户联系人 | 客户联系人 |
| linkmanTel | varchar(64) | YES | - |  | 客户联系方式 | 客户联系方式 |
| dataSource | varchar(25) | YES | SMS |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| orderExecNum | BTREE | NON-UNIQUE | orderExecNumber |
| PRIMARY | BTREE | UNIQUE | id |
| projectCode | BTREE | NON-UNIQUE | projectCode |

---

### 4.86 pm_project_property_from_sms_history

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~47,550 行 |
| 数据大小 | 25.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| orderExecNumber | varchar(25) | YES | - | MUL |  |  |
| projectCode | varchar(25) | YES | - | MUL |  |  |
| projectName | varchar(255) | YES | - |  |  |  |
| salesManCode | varchar(10) | YES | - |  |  |  |
| salesManName | varchar(10) | YES | - |  |  |  |
| marketCode | varchar(64) | YES | - |  |  |  |
| marketName | varchar(255) | YES | - |  |  |  |
| systemId | varchar(64) | YES | - |  |  |  |
| systemName | varchar(255) | YES | - |  |  |  |
| expendId | varchar(64) | YES | - |  |  |  |
| expendName | varchar(255) | YES | - |  |  |  |
| industryId | varchar(64) | YES | - |  |  |  |
| industryName | varchar(255) | YES | - |  |  |  |
| officeCode | varchar(15) | YES | - |  |  |  |
| officeName | varchar(15) | YES | - |  |  |  |
| serviceTypeName | varchar(10) | YES | - |  |  |  |
| channelName | varchar(255) | YES | - |  | 出货代理商名称 | 出货代理商名称 |
| engineeFee | varchar(25) | YES | - |  | 工程服务费 | 工程服务费 |
| objId | varchar(64) | YES | - |  | 参数1 | 参数1 |
| applyType | varchar(25) | YES | - |  | 参数2 | 参数2 |
| corporationCode | varchar(25) | YES | 01 |  | 公司编码 | 公司编码 |
| customerProjectName | varchar(255) | YES | - |  | 客户项目名称 | 客户项目名称 |
| finalCustomerName | varchar(255) | YES | - |  | 最终客户名称 | 最终客户名称 |
| agentName | varchar(500) | YES | - |  | 代理商名称 | 代理商名称 |
| projectMoney | decimal(16,2) | YES | 0.00 |  | 出货价 | 出货价 |
| submitTime | datetime | YES | - |  | 项目创建时间 | 项目创建时间 |
| majorProjectLevel | varchar(255) | YES | - |  | 重大项目级别 | 重大项目级别 |
| predBidDate | datetime | YES | - |  | 项目投标时间 | 项目投标时间 |
| linkmanName | varchar(255) | YES | - |  | 客户联系人 | 客户联系人 |
| linkmanTel | varchar(64) | YES | - |  | 客户联系方式 | 客户联系方式 |
| dataSource | varchar(25) | YES | SMS |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| orderExecNum | BTREE | NON-UNIQUE | orderExecNumber |
| PRIMARY | BTREE | UNIQUE | id |
| projectCode | BTREE | NON-UNIQUE | projectCode |

---

### 4.87 pm_project_property_from_sms_history_bak

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~46,985 行 |
| 数据大小 | 25.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| orderExecNumber | varchar(25) | YES | - | MUL |  |  |
| projectCode | varchar(25) | YES | - | MUL |  |  |
| projectName | varchar(255) | YES | - |  |  |  |
| salesManCode | varchar(10) | YES | - |  |  |  |
| salesManName | varchar(10) | YES | - |  |  |  |
| marketCode | varchar(64) | YES | - |  |  |  |
| marketName | varchar(255) | YES | - |  |  |  |
| systemId | varchar(64) | YES | - |  |  |  |
| systemName | varchar(255) | YES | - |  |  |  |
| expendId | varchar(64) | YES | - |  |  |  |
| expendName | varchar(255) | YES | - |  |  |  |
| industryId | varchar(64) | YES | - |  |  |  |
| industryName | varchar(255) | YES | - |  |  |  |
| officeCode | varchar(15) | YES | - |  |  |  |
| officeName | varchar(15) | YES | - |  |  |  |
| serviceTypeName | varchar(10) | YES | - |  |  |  |
| channelName | varchar(255) | YES | - |  | 出货代理商名称 | 出货代理商名称 |
| engineeFee | varchar(25) | YES | - |  | 工程服务费 | 工程服务费 |
| objId | varchar(64) | YES | - |  | 参数1 | 参数1 |
| applyType | varchar(25) | YES | - |  | 参数2 | 参数2 |
| corporationCode | varchar(25) | YES | 01 |  | 公司编码 | 公司编码 |
| customerProjectName | varchar(255) | YES | - |  | 客户项目名称 | 客户项目名称 |
| finalCustomerName | varchar(255) | YES | - |  | 最终客户名称 | 最终客户名称 |
| agentName | varchar(500) | YES | - |  | 代理商名称 | 代理商名称 |
| projectMoney | decimal(16,2) | YES | 0.00 |  | 出货价 | 出货价 |
| submitTime | datetime | YES | - |  | 项目创建时间 | 项目创建时间 |
| majorProjectLevel | varchar(255) | YES | - |  | 重大项目级别 | 重大项目级别 |
| predBidDate | datetime | YES | - |  | 项目投标时间 | 项目投标时间 |
| linkmanName | varchar(255) | YES | - |  | 客户联系人 | 客户联系人 |
| linkmanTel | varchar(64) | YES | - |  | 客户联系方式 | 客户联系方式 |
| dataSource | varchar(25) | YES | SMS |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| orderExecNum | BTREE | NON-UNIQUE | orderExecNumber |
| PRIMARY | BTREE | UNIQUE | id |
| projectCode | BTREE | NON-UNIQUE | projectCode |

---

### 4.88 pm_project_real_product_line_from_sms

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~16,140 行 |
| 数据大小 | 3.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectCode | varchar(255) | NO |  |  | 项目编码 | 项目编码 |
| orderExecNumber | varchar(255) | YES | - |  | 执行单号 | 执行单号 |
| productFirstName | varchar(255) | NO |  |  | 产品类型 | 产品类型 |
| productName | varchar(128) | NO |  |  | 产品名 | 产品名 |
| productSubCode | varchar(255) | NO |  |  | item编码 | item编码 |
| productSubModel | varchar(255) | YES | - |  | item类型 | item类型 |
| productSubName | varchar(255) | YES | - |  | item描述 | item描述 |
| num | int(11) | NO | 0 |  | 订单数量 | 订单数量 |
| memo | mediumtext | YES | - |  | 备注 | 备注 |
| dataSource | varchar(25) | YES | SMS |  |  |  |

---

### 4.89 pm_project_real_product_line_from_sms_history

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~5,563 行 |
| 数据大小 | 1.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectCode | varchar(255) | NO |  |  | 项目编码 | 项目编码 |
| orderExecNumber | varchar(255) | YES | - |  | 执行单号 | 执行单号 |
| productFirstName | varchar(255) | NO |  |  | 产品类型 | 产品类型 |
| productName | varchar(128) | NO |  |  | 产品名 | 产品名 |
| productSubCode | varchar(255) | NO |  |  | item编码 | item编码 |
| productSubModel | varchar(255) | YES | - |  | item类型 | item类型 |
| productSubName | varchar(255) | YES | - |  | item描述 | item描述 |
| num | int(11) | NO | 0 |  | 订单数量 | 订单数量 |
| memo | mediumtext | YES | - |  | 备注 | 备注 |
| dataSource | varchar(25) | YES | SMS |  |  |  |

---

### 4.90 pm_project_related_party -- 项目相关的团体（渠道等）

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目相关的团体信息（渠道商、代理商、服务商等） |
| 数据量 | ~126,864 行 |
| 数据大小 | 20.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，相关方记录唯一标识 |
| projectId | int(11) | YES | - | MUL |  | 逻辑外键 -> pm_project.projectId |
| partyRole | varchar(45) | YES | - | MUL |  | 0=服务商渠道，1=代理商渠道 |
| partyCode | varchar(45) | YES | - |  |  | 相关方（渠道商/代理商）编码 |
| partyName | varchar(45) | YES | - |  |  | 相关方（渠道商/代理商）名称 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(45) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  | 更新时间 | 记录最新更新时间 |
| updateBy | varchar(45) | YES | - |  | 更新人 | 记录最新更新用户编码 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| partyRole_parojectId | BTREE | NON-UNIQUE | partyRole, projectId |
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 4.91 pm_project_shipment

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目发货记录，支持串货转移 |
| 数据量 | ~460,132 行 |
| 数据大小 | 161.3 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，发货记录唯一标识 |
| projectId | int(11) | YES | - | MUL |  | 逻辑外键 -> pm_project.projectId |
| barcode | varchar(25) | YES | - | MUL |  | 设备序列号/条码 |
| itemCode | varchar(25) | YES | - |  |  | 发货产品编码 |
| itemModel | varchar(255) | YES | - |  |  | 产品型号 |
| itemName | varchar(255) | YES | - |  |  | 产品名称 |
| receiveName | varchar(255) | YES | - |  |  | 收货人姓名 |
| emsNum | varchar(255) | YES | - |  |  | 快递/物流单号 |
| emsCompany | varchar(15) | YES | - |  |  | 快递/物流公司名称 |
| packdate | datetime | YES | - |  |  | 设备打包日期 |
| contractNo | varchar(50) | YES | - | MUL |  | 关联合同编号 |
| installAddress | text | YES | - |  |  | 设备安装地址 |
| chProjectId | int(11) | YES | - |  | 串货转移之前的projectId | 串货转移前所属项目ID |
| chContractNo | varchar(50) | YES | - |  | 串货转移之前的contractNo | 串货转移前合同编号 |
| transferProjectId | int(11) | YES | - |  | 串货转移之后的projectId | 串货转移后目标项目ID |
| transferContractNo | varchar(50) | YES | - |  | 串货转移之后的projectId | 串货转移后合同编号 |
| transferFlag | varchar(2) | YES | -1 |  | 转移标识，默认:-1,转出:1，转入:0 | -1=默认，1=转出，0=转入 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barcode | BTREE | NON-UNIQUE | barcode |
| contractNo | BTREE | NON-UNIQUE | contractNo, barcode |
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 4.92 pm_project_soft_change_logs

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~13,648 行 |
| 数据大小 | 1.8 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 记录版本变更日志 | 记录版本变更日志 |
| projectId | int(11) | YES | - | MUL | 项目ID | 项目ID |
| changeVersion | varchar(10) | YES | - |  | V0001 | V0001 |
| changeRemark | varchar(255) | YES | - |  | 版本变更说明 | 版本变更说明 |
| latest | int(11) | YES | - |  | 0 后 1 是 | 0 后 1 是 |
| createBy | varchar(25) | YES | - |  |  |  |
| createTime | datetime | YES | - |  |  |  |
| updateBy | varchar(25) | YES | - |  |  |  |
| updateTime | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 4.93 pm_project_soft_version

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目设备软件版本信息，记录conp/cpld/boot/pcb等版本 |
| 数据量 | ~532,125 行 |
| 数据大小 | 513.2 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 项目软件版本表 | 自增主键，版本记录唯一标识 |
| projectId | int(11) | YES | - | MUL | 项目ID | 逻辑外键 -> pm_project.projectId |
| logId | int(11) | YES | - |  | 软件版本变更记录 | 逻辑外键 -> pm_project_soft_change_logs.id |
| contractNo | varchar(100) | YES | - |  | 合同号 | 关联合同编号 |
| itemCode | varchar(25) | YES | - |  | 产品编码 | 产品编码 |
| barCode | varchar(25) | YES | - | MUL | 设备序列号 | 设备序列号/条码 |
| conp | varchar(100) | YES | - | MUL |  | CONP软件版本号 |
| conpType | varchar(100) | YES | - |  | 版本类型 | CONP版本类型 |
| conpSeries | varchar(100) | YES | - |  | 版本系列 | CONP版本系列 |
| conpMark | varchar(255) | YES | - |  | 软件版本掩码 | 软件版本掩码，用于版本范围匹配 |
| conpBak | varchar(255) | YES | - |  | 备份变更之前的版本 | CONP变更前备份版本号 |
| conpChange | int(11) | YES | - |  | 0无更新 1有更新 | 0=CONP无更新，1=CONP有更新 |
| cpld | varchar(100) | YES | - |  |  | CPLD版本号 |
| cpldBak | varchar(255) | YES | - |  |  | CPLD变更前备份版本号 |
| cpldChange | int(11) | YES | - |  |  | 0=CPLD无更新，1=CPLD有更新 |
| boot | varchar(100) | YES | - |  |  | Boot版本号 |
| bootBak | varchar(255) | YES | - |  |  | Boot变更前备份版本号 |
| bootChange | int(11) | YES | - |  |  | 0=Boot无更新，1=Boot有更新 |
| pcb | varchar(100) | YES | - |  |  | PCB版本号 |
| pcbBak | varchar(255) | YES | - |  |  | PCB变更前备份版本号 |
| pcbChange | int(11) | YES | - |  |  | 0=PCB无更新，1=PCB有更新 |
| executeTime | date | YES | - |  | 若有更新的情况下为执行更新时间，否则没有实际意义 | 版本更新执行日期 |
| datastate | int(11) | YES | - | MUL | 数据状态 0 失效 1 有效 | 0=失效，1=有效 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barcode | BTREE | NON-UNIQUE | barCode |
| idx_conp_item_query | BTREE | NON-UNIQUE | datastate, conpType, conpSeries, conpMark, itemCode, projectId |
| pm_project_soft_version_conp_IDX | BTREE | NON-UNIQUE | conp |
| PRIMARY | BTREE | UNIQUE | id |
| projectBarcodeValid | BTREE | NON-UNIQUE | projectId, barCode, datastate |

---

### 4.94 pm_project_soft_version_history

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目设备软件版本的历史变更记录表，结构与pm_project_soft_version相同，用于保存版本变更的历史快照，支撑版本回溯和审计追踪 |
| 数据量 | ~1,055,447 行 |
| 数据大小 | 550.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 项目软件版本表 | 历史记录唯一标识 |
| projectId | int(11) | YES | - | MUL | 项目ID | 逻辑外键 -> pm_project.projectId，关联项目 |
| logId | int(11) | YES | - |  | 软件版本变更记录 | 逻辑外键 -> pm_project_log.id，关联操作日志 |
| contractNo | varchar(100) | YES | - |  | 合同号 | 关联合同编号 |
| itemCode | varchar(25) | YES | - |  | 产品编码 | 设备物料编码 |
| barCode | varchar(25) | YES | - | MUL | 设备序列号 | 设备唯一条码标识 |
| conp | varchar(100) | YES | - | MUL |  | 主控（CONP）软件版本号 |
| conpType | varchar(100) | YES | - |  | 版本类型 | 主控组件类型 |
| conpSeries | varchar(100) | YES | - |  | 版本系列 | 主控组件系列 |
| conpMark | varchar(255) | YES | - |  | 软件版本掩码 | 主控版本标记/备注 |
| conpBak | varchar(255) | YES | - |  | 备份变更之前的版本 | 变更前的主控版本（用于对比） |
| conpChange | int(11) | YES | - |  | 0无更新 1有更新 | 主控是否变更，0=未变更，1=已变更 |
| cpld | varchar(100) | YES | - |  |  | CPLD（复杂可编程逻辑器件）版本号 |
| cpldBak | varchar(255) | YES | - |  |  | 变更前的CPLD版本 |
| cpldChange | int(11) | YES | - |  |  | CPLD是否变更 |
| boot | varchar(100) | YES | - |  |  | 引导程序版本号 |
| bootBak | varchar(255) | YES | - |  |  | 变更前的Boot版本 |
| bootChange | int(11) | YES | - |  |  | Boot是否变更 |
| pcb | varchar(100) | YES | - |  |  | 印刷电路板版本号 |
| pcbBak | varchar(255) | YES | - |  |  | 变更前的PCB版本 |
| pcbChange | int(11) | YES | - |  |  | PCB是否变更 |
| executeTime | date | YES | - |  | 若有更新的情况下为执行更新时间，否则没有实际意义 | 版本变更执行日期 |
| datastate | int(11) | YES | - | MUL | 数据状态 0 失效 1 有效 | 数据有效性状态，用于逻辑删除和状态标记 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户 |
| updateTime | datetime | YES | - |  |  | 记录最后更新时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最后更新用户 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barcode | BTREE | NON-UNIQUE | barCode |
| idx_conp_item_query | BTREE | NON-UNIQUE | datastate, conpType, conpSeries, conpMark, itemCode, projectId |
| pm_project_soft_version_conp_IDX | BTREE | NON-UNIQUE | conp |
| PRIMARY | BTREE | UNIQUE | id |
| projectBarcodeValid | BTREE | NON-UNIQUE | projectId, barCode, datastate |

---

### 4.95 pm_project_soleagent_lend_from_sms

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~2,583 行 |
| 数据大小 | 1.7 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| soleAgentLendId | int(11) | NO | 0 |  | 总代借货跟踪 | 总代借货跟踪 |
| orderExecNumber | varchar(255) | YES | - |  | 执行单号 | 执行单号 |
| orderExecNumberShort | varchar(255) | YES | - |  | 忽略版本执行单号 | 忽略版本执行单号 |
| orderCodes | varchar(255) | YES | - |  | 合并的执行单号 | 合并的执行单号 |
| contract | varchar(25) | YES | - | MUL | 合同号 | 合同号 |
| projectName | varchar(255) | YES | - |  | 由商务输入 | 由商务输入 |
| soleAgent | varchar(25) | YES | - |  | 总代名称 | 总代名称 |
| profitCenter | varchar(6) | YES | - |  |  |  |
| dataSource | varchar(25) | YES | SMS |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contract | BTREE | NON-UNIQUE | contract, profitCenter |
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.96 pm_project_soleagent_lend_from_sms_history

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~1,136 行 |
| 数据大小 | 320.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| soleAgentLendId | int(11) | NO | 0 |  | 总代借货跟踪 | 总代借货跟踪 |
| orderExecNumber | varchar(255) | YES | - |  | 执行单号 | 执行单号 |
| orderExecNumberShort | varchar(255) | YES | - |  | 忽略版本执行单号 | 忽略版本执行单号 |
| orderCodes | varchar(255) | YES | - |  | 合并的执行单号 | 合并的执行单号 |
| contract | varchar(25) | YES | - | MUL | 合同号 | 合同号 |
| projectName | varchar(255) | YES | - |  | 由商务输入 | 由商务输入 |
| soleAgent | varchar(25) | YES | - |  | 总代名称 | 总代名称 |
| profitCenter | varchar(6) | YES | - |  |  |  |
| dataSource | varchar(25) | YES | SMS |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contract | BTREE | NON-UNIQUE | contract, profitCenter |
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.97 pm_project_spot_check_ignore_item

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 抽检忽略物料配置表，配置在项目抽检中需要忽略的物料清单，被忽略的物料不参与抽检评分。当前无数据，为预留功能 |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| itemCode | varchar(25) | YES | - | MUL |  | 忽略的物料编码 |
| itemModel | varchar(64) | YES | - |  |  | 忽略的物料型号 |
| itemName | varchar(255) | YES | - |  |  | 忽略的物料名称 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| itemCode | BTREE | NON-UNIQUE | itemCode |

---

### 4.98 pm_project_state

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目各维度状态信息（工程计划/发货/实施/闭环），以projectId为主键 |
| 数据量 | ~45,915 行 |
| 数据大小 | 5.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectId | int(11) | NO | - | PRI |  | 逻辑外键 -> pm_project.projectId |
| projectPlanState | varchar(10) | YES | - | MUL | 工程计划状态 | 逻辑外键 -> fnd_basic_data |
| projectplanTime | datetime | YES | - |  | 工程计划状态更新时间 | 工程计划状态最后变更时间 |
| shipmentState | varchar(11) | YES | - | MUL | 项目发货状态 -1 已发货 1 未发货 2部分发货 | -1=已发货，1=未发货，2=部分发货 |
| shipmentTime | datetime | YES | - |  | 发货状态更新时间戳 | 发货状态最后变更时间 |
| executionState | varchar(45) | YES | 5 |  | 实施状态 | 项目实施阶段状态 |
| executionStateTime | datetime | YES | - |  | 实施状态更新时间 | 实施状态最后变更时间 |
| closeProcessState | varchar(45) | YES | 10 |  | 闭环流程状态 | 项目闭环流程阶段 |
| closeProcessStateTime | datetime | YES | - |  | 闭环流程状态更新时间 | 闭环流程状态最后变更时间 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| index_projectId | UNIQUE | projectId | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| index_projectId | BTREE | UNIQUE | projectId |
| projectPlanState | BTREE | NON-UNIQUE | projectPlanState |
| shipmentState | BTREE | NON-UNIQUE | shipmentState |

---

### 4.99 pm_project_supervision -- 项目督查头信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目督查头信息，记录督查任务 |
| 数据量 | ~818 行 |
| 数据大小 | 256.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，督查记录唯一标识 |
| projectId | int(11) | NO | - |  | 项目头信息主键 | 逻辑外键 -> pm_project.projectId |
| projectCode | varchar(45) | NO | - | MUL | 项目名称 | 项目编码 |
| projectName | varchar(200) | YES | - |  | 项目名称 | 项目名称 |
| channel | varchar(64) | YES | - |  | 代理商/服务商 | 代理商/服务商名称 |
| officeCode | varchar(25) | YES | - | MUL | 办事处编码 | 办事处编码 |
| type | varchar(25) | YES | - |  | 任务性质 | 督查任务性质 |
| processTime | datetime | YES | - |  | 处理时间 | 督查处理时间 |
| state | bit(1) | NO | b'0' |  | 是否完成 | 0=未完成，1=已完成 |
| isDelete | bit(1) | NO | b'0' |  | 是否删除 | 0=未删除，1=已删除 |
| quesnaireId | int(11) | YES | - |  | 问卷ID | 逻辑外键 -> pm_cl_quesnaire_result_header.id |
| deliverFileIds | varchar(255) | YES |  |  | 交付件，fnd_files id | 逻辑外键 -> fnd_files.id |
| remark | text | YES | - |  | 备注 | 备注说明 |
| createTime | datetime | YES | - |  | 创建时间 | 记录创建时间 |
| createBy | varchar(45) | YES | - |  | 创建用户 | 记录创建用户编码 |
| updateTime | datetime | YES | - |  | 最新更新时间 | 记录最新更新时间 |
| updateBy | varchar(45) | YES | - |  | 最新更新用户 | 记录最新更新用户编码 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| department | BTREE | NON-UNIQUE | officeCode |
| PRIMARY | BTREE | UNIQUE | id |
| projectCode_index | BTREE | NON-UNIQUE | projectCode |

---

### 4.100 pm_project_task -- 项目具体任务

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目具体任务，支持树形结构(parentId)，关联Activiti工作流 |
| 数据量 | ~59,042 行 |
| 数据大小 | 16.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| taskId | int(11) | NO | - | PRI, AUTO_INCREMENT | 任务ID | 任务自增主键，任务唯一标识 |
| projectId | int(11) | YES | - | MUL |  | 逻辑外键 -> pm_project.projectId |
| projectType | varchar(25) | YES | 10 | MUL | 项目类型 默认售后项目10 售前测试20 详见fnd_basic_data | 默认10=售后，20=售前测试 |
| contractNo | varchar(45) | YES | - |  | 合同号 | 关联合同编号 |
| taskTypeCode | varchar(45) | YES | - | MUL | 任务类型code，关联基础数据表 | 逻辑外键 -> fnd_basic_data |
| taskTypeId | varchar(25) | YES | - |  | 任务类型id，关联基础数据表 | 如completeTest=完成测试 |
| taskName | varchar(255) | YES | - |  | 任务名 | 任务业务名称 |
| eventPlanHappenDate | datetime | YES | - |  | 款项计划发生日期 | 款项计划发生日期 |
| eventPlanHappenDateENG | datetime | YES | - |  | 工程计划发生日期 | 工程计划发生日期 |
| planStartTime | datetime | YES | - |  | 计划开始日期 | 任务计划开始时间 |
| planEndTime | datetime | YES | - |  | 计划结束日期 | 任务计划结束时间 |
| actualStartTime | datetime | YES | - |  | 实际开始日期 | 任务实际开始时间 |
| eventActualFinishDate | datetime | YES | - |  | 实际完成日期 | 任务实际完成日期 |
| priority | varchar(25) | YES | - |  | 优先级 | 任务优先级 |
| progress | int(3) | YES | 0 |  | 进度百分比 | 0-100 |
| progressDesc | varchar(255) | YES | - |  | 进度描述 | 任务进度文字描述 |
| status | varchar(25) | YES | 0 |  | 状态 | 任务状态，0=未开始 |
| parentId | int(11) | YES | - |  | 父级任务 | 支持树形任务结构 |
| remark | text | YES | - |  | 备注 | 备注说明 |
| createTime | datetime | YES | - |  | 记录数据创建时间 | 记录创建时间 |
| createBy | varchar(45) | YES | - |  | 记录数据创建用户 | 记录创建用户编码 |
| updateTime | datetime | YES | - |  | 记录数据最新更新时间 | 记录最新更新时间 |
| updateBy | varchar(45) | YES | - |  | 记录数据最新更新用户 | 记录最新更新用户编码 |
| effectiveFrom | datetime | YES | - |  | 数据有效性开始时间 | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  | 数据有效性结束时间 | 数据有效性结束时间，NULL=当前有效 |
| visibleFlag | varchar(2) | YES | 1 |  | 是否可见，1表示可见，2表示不可见 | 1=可见，2=不可见 |
| deliverFileIds | varchar(255) | YES | - |  | 上传的交付件 | 逻辑外键 -> fnd_files.id |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | taskId | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | taskId |
| projectId | BTREE | NON-UNIQUE | projectId, projectType |
| projectType | BTREE | NON-UNIQUE | projectType, projectId |
| taskTypeCode_Id | BTREE | NON-UNIQUE | taskTypeCode, taskTypeId |

---

### 4.101 pm_project_warranty_callback

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目维保回访问卷表，记录维保回访详情 |
| 数据量 | ~5,588 行 |
| 数据大小 | 2.9 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 项目维保回访问卷表 | 自增主键，维保回访记录唯一标识 |
| projectId | int(11) | YES | - | MUL | 项目ID | 逻辑外键 -> pm_project.projectId |
| projectCode | varchar(45) | YES | - | MUL | 项目编码 | 项目编码 |
| officeCode | varchar(25) | YES | - |  | 办事处 | 办事处编码 |
| contractNos | varchar(255) | YES | - |  | 合同号 | 关联合同编号（多个逗号分隔） |
| projectIds | varchar(255) | YES | - |  | 关联的项目 | 关联项目ID（多个逗号分隔） |
| projectName | varchar(255) | YES | - |  | 项目名称 | 项目名称 |
| serviceImpl | varchar(25) | YES | - |  | 实施方式 | 项目实施方式编码 |
| industryName | varchar(25) | YES | - |  | 行业 | 客户所属行业 |
| agentChannel | varchar(255) | YES | - |  | 下单代理商 | 下单代理商名称 |
| finalCustomerName | varchar(255) | YES | - |  | 最终客户单位 | 最终客户单位名称 |
| customer1 | tinytext | YES | - |  | 客户联系人1 | 客户联系人1姓名 |
| customerContact1 | tinytext | YES | - |  | 客户联系方式1 | 客户联系人1联系方式 |
| customer2 | tinytext | YES | - |  | 客户联系人2 | 客户联系人2姓名 |
| customerContact2 | tinytext | YES | - |  | 客户联系方式2 | 客户联系人2联系方式 |
| warrantyStartTime | date | YES | - |  | 维保开始日期 | 维保合同开始日期 |
| warrantyEndTime | date | YES | - |  | 维保结束日期 | 维保合同结束日期 |
| renewalIntention | int(1) | YES | - |  | 续保意向,0:无,1:有,2:待定 | 0=无，1=有，2=待定 |
| callbackTime | datetime | YES | - |  | 回访时间 | 回访时间 |
| nextCallbackTime | datetime | YES | - |  | 下次回访时间 | 下次回访时间 |
| taskId | varchar(25) | YES | - |  | 任务ID | Activiti任务ID |
| quesnaireId | int(11) | YES | - |  | 问卷ID | 逻辑外键 -> pm_cl_quesnaire_result_header.id |
| quesnaireVersion | int(11) | YES | - |  | 问卷版本 | 问卷模板版本号 |
| quesnaireState | int(11) | YES | - |  | 状态 -1 草稿 1已提交 | -1=草稿，1=已提交 |
| isDelete | bit(1) | YES | b'0' |  | 删除标记 | 0=未删除，1=已删除 |
| remark | varchar(255) | YES | - |  | 备注 | 备注说明 |
| compId | int(2) | YES | 0 |  | 所属公司 | 所属公司ID |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| customInfo | json | YES | - |  |  | JSON扩展字段 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectCode | BTREE | NON-UNIQUE | projectCode |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 4.102 pm_project_weekly -- 项目周报

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目周报主表 |
| 数据量 | ~932 行 |
| 数据大小 | 224.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| weeklyId | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 周报自增主键 |
| projectId | int(11) | YES | - | MUL | 项目信息头ID | 逻辑外键 -> pm_project.projectId |
| currentTask | varchar(100) | YES | - |  | 当前工程阶段 | 当前工程阶段名称 |
| taskStartTime | datetime | YES | - |  | 阶段开始时间 | 当前阶段开始时间 |
| taskEndTime | datetime | YES | - |  | 阶段结束时间 | 当前阶段结束时间 |
| taskDeviation | text | YES | - |  | 偏差 | 进度偏差说明 |
| remark | text | YES | - |  | 备注 | 备注说明 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| weeklyStartTime | datetime | YES | - |  | 报告开始时间 | 周报统计周期开始时间 |
| weeklyEndTime | datetime | YES | - |  | 报告结束时间 | 周报统计周期结束时间 |
| weeklyState | int(11) | YES | 0 |  | 周报状态 0 草稿 1提交 | 0=草稿，1=已提交 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | weeklyId | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | weeklyId |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 4.103 pm_project_weekly_content

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 项目周报详细内容 |
| 数据量 | ~12,979 行 |
| 数据大小 | 1.8 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，内容记录唯一标识 |
| weeklyId | int(11) | YES | - | MUL |  | 逻辑外键 -> pm_project_weekly.weeklyId |
| optionDesc001 | text | YES | - |  |  | 周报选项描述1（工作内容） |
| optionDesc002 | text | YES | - |  |  | 周报选项描述2（下周计划） |
| optionType | int(11) | YES | - |  | option对应周报的部分 | 选项类型，对应周报不同部分 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(15) | YES | - |  |  | 记录创建用户编码 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| weeklyId | BTREE | NON-UNIQUE | weeklyId |

---

### 4.104 pm_project_weekly_feedback

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 数据量 | ~20 行 |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 项目周报回复内容 |
| weeklyId | int(11) | YES | - | MUL |  |  |
| feedback | text | YES | - |  |  |  |
| feedbacker | varchar(25) | YES | - |  |  |  |
| feedbackTime | datetime | YES | - |  |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| weeklyId | BTREE | NON-UNIQUE | weeklyId |

---

### 4.105 pm_subcontract_deliver_files

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 转包项目交付件文件 |
| 数据量 | ~3,823 行 |
| 数据大小 | 1.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，交付件唯一标识 |
| subcontractId | int(11) | YES | - | MUL | 转包项目ID | 逻辑外键 -> pm_subcontract_project_header.id |
| paymentId | int(11) | YES | - |  | 转包付款ID | 逻辑外键 -> pm_subcontract_project_payment.id |
| fileName | varchar(255) | YES | - |  | 交付件名称 | 交付件文件名 |
| filePath | varchar(255) | YES | - |  | 交付件路径 | 交付件文件存储路径 |
| type | varchar(45) | YES | - |  | 交付件类型,0:用服交付合同，1：用服服务单，2：工程合同 | 0=用服交付合同，1=用服服务单，2=工程合同 |
| uploadBy | varchar(45) | YES | - |  | 上传者 | 文件上传人编码 |
| uploadTime | datetime | YES | - |  | 上传时间 | 文件上传时间 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段，存储动态属性 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| subcontractId | BTREE | NON-UNIQUE | subcontractId |

---

### 4.106 pm_subcontract_facilitator

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 转包服务商信息 |
| 数据量 | ~174 行 |
| 数据大小 | 80.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，服务商唯一标识 |
| name | varchar(64) | YES | - |  | 服务商名 | 服务商名 |
| code | varchar(64) | YES | - |  | 服务商编号 | 服务商编号 |
| account | varchar(64) | YES | - |  | 服务商账户 | 服务商账户 |
| bankInfo | varchar(255) | YES | - |  | 开户行信息 | 开户行信息 |
| bankAccount | varchar(64) | YES | - |  | 收款账户 | 银行账号 |
| receiver | varchar(64) | YES | - |  | 邮箱收件人 | 邮箱收件人 |
| cnapsCode | varchar(64) | YES | - |  | 联行号 | 联行号 |
| contacts | varchar(64) | YES | - |  | 联系人 | 联系人 |
| tel | varchar(64) | YES | - |  | 联系电话 | 联系电话 |
| email | varchar(64) | YES | - |  | 邮箱账号 | 邮箱账号 |
| state | bit(1) | YES | b'1' |  | 状态 | 状态 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |
| createBy | varchar(45) | YES | - |  |  | 记录创建用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(45) | YES | - |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| relateType | varchar(45) | YES | - |  | 关联类型 | 关联类型 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段，存储动态属性 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.107 pm_subcontract_project_callback

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 转包项目回访记录表，记录转包项目的回访流程信息，关联工作流任务和问卷，用于跟踪转包项目的服务质量回访 |
| 数据量 | ~416 行 |
| 数据大小 | 64.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 项目转包回访问卷表 | 记录唯一标识 |
| subcontractId | int(11) | YES | - |  | 项目转包ID | 逻辑外键 -> pm_subcontract_project_header.id，关联转包项目 |
| taskKey | varchar(25) | YES | - |  | 任务类型 | Activiti工作流任务定义Key |
| taskId | varchar(25) | YES | - |  | 任务ID | Activiti工作流任务实例ID |
| quesnaireId | int(11) | YES | - |  | 问卷ID | 关联回访问卷模板ID |
| quesnaireVersion | int(11) | YES | - |  | 问卷版本 | 问卷模板版本号 |
| quesnaireState | int(11) | YES | - |  | 状态 -1 草稿 1已提交 | 问卷填写状态 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最后更新用户 |
| updateTime | datetime | YES | - |  |  | 记录最后更新时间 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.108 pm_subcontract_project_header

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 转包项目主表，记录转包项目基本信息 |
| 数据量 | ~3,220 行 |
| 数据大小 | 2.0 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，转包项目唯一标识 |
| subcontractName | varchar(512) | YES |  |  | 转包名称 | 转包项目名称 |
| subcontractNo | varchar(64) | YES |  | MUL | 转包合同号 | 转包合同编号 |
| contractNos | varchar(2048) | YES |  |  | 项目合同号 | 关联合同编号（多个逗号分隔） |
| projectIds | varchar(1024) | YES |  |  | 转包的项目ID | 逻辑外键 -> pm_project.projectId |
| type | int(11) | YES | - |  | 转包类型 | 转包类型编码 |
| state | int(11) | NO | 0 |  | 转包状态 | 转包项目状态 |
| callbackState | int(11) | YES | - |  | 回访状态 | 回访状态 |
| facilitatorId | int(11) | YES | - | MUL | 服务商表ID | 逻辑外键 -> pm_subcontract_facilitator.id |
| facilitatorName | varchar(64) | YES |  |  | 服务商名 | 服务商名称 |
| bankInfo | varchar(255) | YES |  |  | 服务商开户地址 | 服务商开户行信息 |
| bankAccount | varchar(64) | YES |  |  | 服务商收款账户 | 服务商收款银行账号 |
| officeCode | varchar(25) | YES |  | MUL | 办事处部门 | 逻辑外键 -> fnd_department.departmentNum |
| profitDepCode | varchar(25) | YES |  | MUL | 收益部门 | 收益部门编码 |
| isAccrued | bit(1) | YES | - |  | 是否计提 | 0=未计提，1=已计提 |
| isInvoiced | bit(1) | YES | - |  | 是否提供发票 | 0=未提供发票，1=已提供发票 |
| subcontractAmount | varchar(25) | YES |  |  | 转包价 | 转包合同金额 |
| reason | varchar(512) | YES |  |  | 转包原因 | 转包原因说明 |
| remark | varchar(512) | YES |  |  | 备注 | 备注 |
| effectiveFrom | datetime | YES | - |  | 有效开始时间 | 数据有效性开始时间 |
| effectiveTo | datetime | YES | - |  | 有效结束时间 | 数据有效性结束时间 |
| zrApproveTime | datetime | YES | - |  | 最新主任审批通过时间 | 最新主任审批时间 |
| createBy | varchar(25) | YES |  |  |  | 记录创建用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES |  |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| orgId | int(2) | YES | 1 |  | 所属公司 | 组织ID |
| customInfo | json | YES | - |  | 自定义信息 | 使用JSON_MERGE_PATCH增量更新 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| facilitatorId | BTREE | NON-UNIQUE | facilitatorId |
| officeCode | BTREE | NON-UNIQUE | officeCode |
| PRIMARY | BTREE | UNIQUE | id |
| profitDepCode | BTREE | NON-UNIQUE | profitDepCode |
| subcontractNo | BTREE | NON-UNIQUE | subcontractNo |

---

### 4.109 pm_subcontract_project_line

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 转包项目明细行 |
| 数据量 | ~51,088 行 |
| 数据大小 | 24.1 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，转包明细唯一标识 |
| subcontractId | int(11) | NO | - | MUL | 转包项目Id | 逻辑外键 -> pm_subcontract_project_header.id |
| projectId | int(11) | YES | - | MUL | 原项目Id | 原项目Id |
| barcode | varchar(25) | YES | - | MUL | 设备序列号 | 设备序列号 |
| itemCode | varchar(25) | YES | - | MUL | 设备编码 | 产品编码 |
| itemModel | varchar(255) | YES | - |  | 设备型号 | 产品型号 |
| itemName | varchar(255) | YES | - |  | 设备名称 | 产品名称 |
| contractNo | varchar(50) | YES | - | MUL | 合同号 | 合同号 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |
| unique_index | UNIQUE | subcontractId | None | None |
| unique_index | UNIQUE | barcode | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barcode | BTREE | NON-UNIQUE | barcode |
| contractNo | BTREE | NON-UNIQUE | contractNo |
| itemCode | BTREE | NON-UNIQUE | itemCode |
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |
| unique_index | BTREE | UNIQUE | subcontractId, barcode |

---

### 4.110 pm_subcontract_project_payment

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 转包项目付款记录 |
| 数据量 | ~3,351 行 |
| 数据大小 | 528.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，付款记录唯一标识 |
| subcontractId | int(11) | NO | - | MUL | 转包项目Id | 逻辑外键 -> pm_subcontract_project_header.id |
| ratio | varchar(10) | YES | - |  | 比例 | 比例 |
| amount | varchar(25) | YES | - |  | 付款金额 | 付款金额 |
| confirmTime | datetime | YES | - |  | 提交时间 | 提交时间 |
| paymentTime | datetime | YES | - |  | 付款时间 | 付款时间 |
| remark | varchar(512) | YES | - |  | 备注 | 备注 |
| sseId | bigint(20) | YES | -1 |  | sse报销单审批行ID,0：会进行匹配跟新 | sse报销单审批行ID,0：会进行匹配跟新 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段，存储动态属性 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| subcontractId | BTREE | NON-UNIQUE | subcontractId |

---

### 4.111 pm_subcontract_project_payment_sse

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 从SSE系统同步的转包项目付款信息，记录转包项目的付款申请、审批和支付状态。数据从SSE系统定期同步，用于PMS中查看转包付款进度 |
| 数据量 | ~3,608 行 |
| 数据大小 | 1.9 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) unsigned | YES | 0 | MUL |  | 付款记录ID（非自增，来自SSE系统） |
| workNo | varchar(10) | YES | - | MUL | 工号 | 付款申请人/服务商工号 |
| name | varchar(10) | YES | - |  | 姓名 | 付款申请人/服务商姓名 |
| offerNum | varchar(20) | YES | - |  | 申请单号 | SSE系统中的报价编号 |
| applyAmount | decimal(16,2) | YES | - |  | 申请金额 | 付款申请金额 |
| receiver | varchar(255) | YES | - |  | 收款人 | 收款人名称 |
| bank | varchar(80) | YES | - |  | 开户行 | 收款银行名称 |
| bankAccount | varchar(255) | YES | - |  | 银行账号 | 收款银行账号 |
| useage | varchar(512) | YES | - |  | 汇款用途 | 付款用途说明 |
| paystate | varchar(25) | YES | - |  | 付款状态 | 付款状态编码 |
| confirmTime | datetime | YES | - |  | 提交时间 | 付款确认时间 |
| paymentTime | datetime | YES | - |  | 付款时间 | 实际支付时间 |
| approveState | varchar(25) | NO |  |  | 审批状态 | 审批状态编码 |
| type | varchar(255) | YES | - |  | 费用类别 | 付款类型 |
| approveAmount | decimal(16,2) | YES | - |  | 权签金额 | 审批通过金额 |
| remark | text | YES | - |  | 说明 | 备注说明 |
| subcontractNo | varchar(255) | YES | - | MUL | 服务合同号 | 逻辑外键 -> pm_subcontract_project_header.subcontractNo，关联转包项目 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| id | BTREE | NON-UNIQUE | id |
| subcontractNo | BTREE | NON-UNIQUE | subcontractNo |
| workNo | BTREE | NON-UNIQUE | workNo |

---

### 4.112 pm_subcontract_project_price

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 转包项目价格信息表，记录转包项目各订单行的工程费用和价格信息，用于转包费用核算和成本管理 |
| 数据量 | ~5,176 行 |
| 数据大小 | 1.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 记录唯一标识 |
| subcontractId | int(11) | NO | - |  | 转包项目Id | 逻辑外键 -> pm_subcontract_project_header.id，关联转包项目 |
| contractNo | varchar(50) | YES | - |  | 合同号 | 关联合同编号 |
| orderExecNumber | varchar(25) | YES | - |  | 执行单号 | ERP订单执行行号 |
| projectCode | varchar(25) | YES | - |  | 项目编码 | 项目业务编码 |
| engineeFee | varchar(25) | YES | - |  | 工程服务价 | 工程费用金额 |
| objId | varchar(64) | YES | - |  | SMS链接参数1 | 关联对象标识 |
| procType | varchar(25) | YES | - |  | SMS链接参数2 | 费用处理类型编码 |
| price | varchar(25) | YES | - |  | 合同转包价 | 单价 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户 |
| updateTime | datetime | YES | - |  |  | 记录最后更新时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最后更新用户 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.113 prob_main

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 技术公告/问题主表，使用bitMark位运算进行多值筛选 |
| 数据量 | ~1,080 行 |
| 数据大小 | 3.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | Java Bean中映射为probId |
| probNum | varchar(25) | YES | - | MUL | 编码 | 技术公告编号 |
| watch | varchar(10) | YES | - |  | 跟踪 | 逻辑外键 -> fnd_basic_data(dataTypeCode=30) |
| theme | varchar(255) | YES | - |  | 主题 | 技术公告主题 |
| desc | text | YES | - |  | 问题描述 | 问题描述详细内容 |
| solution | text | YES | - |  | 解决方案 | 问题解决方案 |
| status | varchar(10) | YES | - |  | 状态 | 逻辑外键 -> fnd_basic_data(dataTypeCode=31) |
| startdate | date | YES | - |  | 开始日期 | 问题发现/开始日期 |
| duedate | date | YES | - |  | 计划完成日期 | 问题计划完成日期 |
| attachments | varchar(255) | YES | - |  | 文件 | 附件路径 |
| priority | varchar(10) | YES | - |  | 严重级别 | 逻辑外键 -> fnd_basic_data(dataTypeCode=32) |
| productType | text | YES | - |  | 产品类型 | 产品类型 |
| trackingUser | varchar(10) | YES | - |  | 跟踪用户 | 逻辑外键 -> fnd_user_info.username |
| visibleRange | int(1) | NO | 0 |  | 可见范围，0:All, 1:对内 | 可见范围设置 |
| createBy | varchar(15) | YES | - |  |  | 记录创建用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |
| remark | text | YES | - |  | 审批意见 | 备注 |
| customInfo | json | YES | - |  | 自定义信息 | 存储relatedSceneTypes等动态属性 |
| probTicketNo | varchar(255) | YES | - |  | 网上问题单号 | 关联工单编号 |
| relatedSceneTypes | varchar(255) | YES | - |  | relatedSceneTypes | 逗号分隔的多值 |
| relatedSceneTypesMark | bigint(20) | YES | - |  | relatedSceneTypes的bitmark | 位运算标记，用于高效筛选 |
| mitigationActionTypes | varchar(255) | YES | - |  | mitigationActionTypes | 规避方案操作类型（逗号分隔） |
| mitigationActionTypesMark | bigint(20) | YES | - |  | mitigationActionTypes的bitmark | 规避方案操作类型位运算标记 |
| solutionActionTypes | varchar(255) | YES | - |  | solutionActionTypes | 解决方案操作类型（逗号分隔） |
| solutionActionTypesMark | bigint(20) | YES | - |  | solutionActionTypes的bitmark | 解决方案操作类型位运算标记 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| probNum_IDX | BTREE | NON-UNIQUE | probNum, id |

---

### 4.114 prob_product

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 技术公告关联的产品信息 |
| 数据量 | ~31,823 行 |
| 数据大小 | 7.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，产品关联唯一标识 |
| probId | int(11) | YES | 0 | MUL | ProbId | 逻辑外键 -> prob_main.id |
| productCode | varchar(255) | YES |  |  | 产品大类 | 产品大类 |
| productSubCode | varchar(255) | YES |  |  | 产品小类 | 产品小类 |
| itemCode | varchar(255) | NO |  |  | item编码 | item编码 |
| itemModel | varchar(255) | YES | - |  | item类型 | item类型 |
| itemDesc | varchar(255) | YES | - |  | item描述 | item描述 |
| status | int(11) | YES | 1 |  | 0 失效 1 有效 | 0 失效 1 有效 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| probId_Status_IDX | BTREE | NON-UNIQUE | probId, status |
| probId_status_item_IDX | BTREE | NON-UNIQUE | probId, status, itemCode |

---

### 4.115 prob_product_component

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 技术公告/问题关联的产品组件表，定义产品组件的树形结构（通过parentId），用于技术公告关联到具体的产品组件而非仅到产品级别，实现更精细的组件级问题定位 |
| 数据量 | ~66 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 组件唯一标识 |
| type | varchar(100) | YES | - |  | 分组 | 产品组件类型分类 |
| name | varchar(100) | YES | - |  | 名称 | 产品组件名称 |
| version | varchar(100) | YES | - |  | 版本 | 产品组件版本号 |
| parentId | int(11) | YES | - |  | 父节点 | 逻辑外键 -> prob_product_component.id，构建组件树形结构 |
| state | bit(1) | YES | b'1' |  | 状态 | 组件状态，1=有效，0=失效 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户 |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  |  | 记录创建时间，默认当前时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最后更新用户 |
| updateTime | datetime | YES | - |  |  | 记录最后更新时间，自动更新（on update CURRENT_TIMESTAMP） |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.116 prob_read_log

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 技术公告阅读记录 |
| 数据量 | ~43,284 行 |
| 数据大小 | 2.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 自增主键，阅读记录唯一标识 |
| probId | int(11) | NO | - |  |  | 逻辑外键 -> prob_main.id |
| reader | varchar(25) | NO |  |  | 查阅人 | 阅读用户编码 |
| readTime | datetime | NO | - |  | 查阅时间 | 阅读时间 |
| status | int(1) | NO | 0 |  | 是否已经确认查阅 | 是否已经确认查阅 |
| firstTime | datetime | YES | - |  | 第一次查阅时间 | 第一次查阅时间 |
| commitTime | datetime | YES | - |  | 确认时间 | 确认时间 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.117 prob_restore

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 技术公告恢复/修复方案记录 |
| 数据量 | ~1,269 行 |
| 数据大小 | 672.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 问题修复数据对象 | 自增主键，恢复记录唯一标识 |
| probId | int(11) | YES | 0 | MUL | 涉及到的问题ID | 逻辑外键 -> prob_main.id |
| serialNum | varchar(50) | YES | - | MUL | 序列号 | 序列号 |
| itemModel | varchar(50) | YES | - | MUL | 设备类型 | 设备类型 |
| processId | int(11) | YES | 0 | MUL | 记录任务流程过程中的相关信息 | 记录任务流程过程中的相关信息 |
| officeCode | varchar(25) | YES | - |  | 办事处编码 | 办事处编码 |
| conp | varchar(255) | YES | - |  | 任务发布时的软件版本 | 任务发布时的软件版本 |
| boot | varchar(100) | YES | - |  |  | BOOT引导程序版本号 |
| cpld | varchar(100) | YES | - |  |  | CPLD固件版本号 |
| pcb | varchar(100) | YES | - |  |  | PCB电路板版本号 |
| projectId | int(11) | YES | 0 | MUL | 涉及到的项目ID | 涉及到的项目ID |
| projectName | varchar(255) | YES | - |  | 项目名称 | 项目名称 |
| contractNo | varchar(255) | YES | - |  | 合同号 | 合同号 |
| assignee | varchar(25) | YES | - |  | 办理用户 | 办理用户 |
| assigneeRole | int(11) | YES | 0 |  | 办理角色 | 办理角色 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| itemModel | BTREE | NON-UNIQUE | itemModel |
| PRIMARY | BTREE | UNIQUE | id |
| probId_serialNum_IDX | BTREE | NON-UNIQUE | probId, serialNum |
| processId | BTREE | NON-UNIQUE | processId |
| projectId | BTREE | NON-UNIQUE | projectId |
| serialNum | BTREE | NON-UNIQUE | serialNum |

---

### 4.118 prob_restore_process

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 技术公告/问题的恢复流程记录表，记录问题恢复的流程状态和备注，与prob_restore（恢复方案）互补，侧重于恢复过程的流程跟踪而非具体方案内容 |
| 数据量 | ~9 行 |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 记录问题修复的流程流转过程 | 记录唯一标识 |
| probId | int(11) | YES | - | MUL | 问题ID | 逻辑外键 -> prob_main.id，关联技术公告 |
| restoreStatus | int(11) | YES | - |  | 修复任务流转状态 | 恢复流程状态编码 |
| restoreRemark | text | YES | - |  | 流转备注说明 | 恢复流程备注说明 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最后更新用户 |
| updateTime | datetime | YES | - |  |  | 记录最后更新时间 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| probId | BTREE | NON-UNIQUE | probId, restoreStatus |

---

### 4.119 prob_restore_weekly

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 技术公告/问题的恢复周报附件表，关联问题与周报文件。当前无数据，为预留功能 |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 任务进展周报 | 记录唯一标识 |
| probId | int(11) | YES | - | MUL | 问题主键 | 逻辑外键 -> prob_main.id，关联技术公告 |
| fileId | int(11) | YES | - |  | 附件ID | 逻辑外键 -> fnd_files.id，关联周报文件 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最后更新用户 |
| updateTime | datetime | YES | - |  |  | 记录最后更新时间 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| probId | BTREE | NON-UNIQUE | probId |

---

### 4.120 prob_soft_version

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 技术公告/问题关联的软件版本组合表，定义CONP/CPLD/Boot/PCB四元组版本号，通过唯一约束确保版本组合不重复。当前无数据，为预留功能 |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  | 记录唯一标识 |
| conp | varchar(100) | YES | - | MUL |  | CONP主控软件版本号 |
| cpld | varchar(100) | YES | - |  |  | CPLD复杂可编程逻辑器件版本号 |
| boot | varchar(100) | YES | - |  |  | 引导程序版本号 |
| pcb | varchar(100) | YES | - |  |  | 印刷电路板版本号 |
| createdBy | varchar(25) | YES | - |  |  | 记录创建用户 |
| createdTime | datetime | YES | - |  |  | 记录创建时间 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |
| conp | UNIQUE | conp | None | None |
| conp | UNIQUE | cpld | None | None |
| conp | UNIQUE | boot | None | None |
| conp | UNIQUE | pcb | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| conp | BTREE | UNIQUE | conp, cpld, boot, pcb |
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.121 prob_softwares

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 技术公告关联的软件版本信息，用于版本范围匹配 |
| 数据量 | ~11,456 行 |
| 数据大小 | 12.9 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, AUTO_INCREMENT | 已知问题影响的软件版本表 | 自增主键，软件版本唯一标识 |
| probId | int(11) | YES | 0 | MUL | 问题ID | 逻辑外键 -> prob_main.id |
| conp | varchar(100) | YES | - | MUL |  | 受影响主控版本号 |
| cpld | varchar(100) | YES | - | MUL |  | 受影响CPLD版本号 |
| boot | varchar(100) | YES | - | MUL |  | 受影响Boot版本号 |
| pcb | varchar(100) | YES | - | MUL |  | 受影响PCB版本号 |
| manualEntry | varchar(2048) | YES | - |  | 手工录入 | 手动录入的版本号 |
| manualEntrySub | varchar(2048) | YES | - |  | 手工录入拆解 | 手工录入拆解 |
| entryType | varchar(100) | YES | - |  | 版本类型 | 版本类型 |
| entrySeries | varchar(100) | YES | - |  | 版本系列 | 版本系列 |
| entryStart | varchar(255) | YES | - |  | 版本范围开始 | 版本范围开始 |
| entryEnd | varchar(255) | YES | - |  | 版本范围结束 | 版本范围结束 |
| markStart | varchar(255) | YES | - |  | 缺省补充版本范围开始 | 版本范围起始标记 |
| markEnd | varchar(255) | YES | - |  | 缺省补充版本范围结束 | 版本范围结束标记 |
| affectedType | int(11) | YES | 0 | MUL | 影响类型，0：所有系列，1：盒式系列，2：框式系列 | 1=盒式，2=框式 |
| groupId | bigint(11) | YES | 0 |  | 分组ID | 分组ID |
| splited | int(11) | YES | 0 |  | 是否拆解 | 是否拆解 |
| datastate | int(11) | YES | 1 | MUL | 0 失效 1 有效 | 0=失效，1=有效 |
| createBy | varchar(10) | YES | - |  |  | 记录创建用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(10) | YES | - |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段，存储动态属性 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| affectedType | BTREE | NON-UNIQUE | affectedType |
| boot | BTREE | NON-UNIQUE | boot |
| conp | BTREE | NON-UNIQUE | conp |
| cpld | BTREE | NON-UNIQUE | cpld |
| datastate_entry_probId_IDX | BTREE | NON-UNIQUE | datastate, entryType, entrySeries, probId |
| pcb | BTREE | NON-UNIQUE | pcb |
| PRIMARY | BTREE | UNIQUE | id |
| probId_datastate_IDX | BTREE | NON-UNIQUE | probId, datastate |

---

### 4.122 serve_type -- 服务类型

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 服务类型 |
| 数据量 | ~4 行 |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| serve | varchar(10) | NO | - | MUL |  |  |
| serve_type | varchar(10) | YES | - |  |  |  |
| remark | text | YES | - |  | 备注 | 备注 |
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| serve_where_index | BTREE | NON-UNIQUE | serve |

---

### 4.123 tain_type -- 维保类型

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 存储引擎 | InnoDB |
| 业务含义 | 维保类型 |
| 数据量 | ~3 行 |
| 数据大小 | 32.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| tain | varchar(10) | NO | - | MUL |  |  |
| id | int(11) | NO | - | PRI, AUTO_INCREMENT |  |  |
| tain_type | varchar(50) | YES | - |  |  |  |
| remark | text | YES | - |  | 备注 | 备注 |

**约束列表**

| 约束名 | 约束类型 | 字段 | 引用表 | 引用字段 |
|--------|----------|------|--------|----------|
| PRIMARY | PRIMARY KEY | id | None | None |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| tain_where_index | BTREE | NON-UNIQUE | tain |

---
