# PMS-activiti 数据库索引分析

> 本文档分析 Activiti 5.23.0 引擎表的索引设计，结合 PMS-activiti 源码中的常用查询场景，给出索引优化建议。
> 数据来源：Activiti 5.23.0 官方 Schema、`d:\常规软件\QoderCode\workspace\PMS\PMS-activiti\src\main\java\com\dp\plat\activiti\` 源码。

---

## 1. 索引设计原则

Activiti 5.23.0 在 `dbSchemaCreate` 阶段自动创建索引，遵循以下原则：

1. **主键索引**：所有表均以 `ID_` 为主键，自动创建聚簇索引
2. **外键索引**：所有外键字段自动创建普通索引，加速 JOIN 与级联删除
3. **唯一约束**：业务唯一性字段（如流程定义键+版本）创建唯一索引
4. **查询优化索引**：针对引擎内部常用查询条件创建辅助索引

---

## 2. 索引清单

### 2.1 ACT_RE_ 存储库表索引

#### ACT_RE_DEPLOYMENT（部署信息表）

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| `PRIMARY` | `ID_` | 主键 | 部署ID主键 |

#### ACT_RE_PROCDEF（流程定义表）

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| `PRIMARY` | `ID_` | 主键 | 流程定义ID主键 |
| `ACT_UNIQ_PROCDEF` | `KEY_, VERSION_, TENANT_ID_` | 唯一 | 流程定义唯一约束 |

#### ACT_RE_MODEL（模型信息表）

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| `PRIMARY` | `ID_` | 主键 | 模型ID主键 |

### 2.2 ACT_RU_ 运行时表索引

#### ACT_RU_EXECUTION（运行时执行表）

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| `PRIMARY` | `ID_` | 主键 | 执行ID主键 |
| `ACT_UNIQ_EXEC_BUSINESS` | `BUSINESS_KEY_, TENANT_ID_` | 唯一 | 业务键唯一 |
| `ACT_FK_EXEC_PROCDEF` | `PROC_DEF_ID_` | 普通 | 关联流程定义 |
| `ACT_FK_EXEC_PARENT` | `PARENT_ID_` | 普通 | 关联父执行 |
| `ACT_FK_EXEC_SUPER` | `SUPER_EXEC_` | 普通 | 关联父执行实例 |

#### ACT_RU_TASK（运行时任务表）

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| `PRIMARY` | `ID_` | 主键 | 任务ID主键 |
| `ACT_FK_TASK_EXEC` | `EXECUTION_ID_` | 普通 | 关联执行实例 |
| `ACT_FK_TASK_PROCINST` | `PROC_INSTANCE_ID_` | 普通 | 关联流程实例 |
| `ACT_FK_TASK_PROCDEF` | `PROC_DEF_ID_` | 普通 | 关联流程定义 |
| `ACT_IDX_TASK_CREATE` | `CREATE_TIME_` | 普通 | 按创建时间查询 |
| `ACT_IDX_TASK_NAME` | `NAME_` | 普通 | 按任务名称查询 |

#### ACT_RU_VARIABLE（运行时变量表）

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| `PRIMARY` | `ID_` | 主键 | 变量ID主键 |
| `ACT_FK_VAR_EXEC` | `EXECUTION_ID_` | 普通 | 关联执行实例 |
| `ACT_FK_VAR_PROCINST` | `PROC_INSTANCE_ID_` | 普通 | 关联流程实例 |
| `ACT_FK_VAR_TASK` | `TASK_ID_` | 普通 | 关联任务 |
| `ACT_IDX_VARIABLE_TASK_NAME` | `TASK_ID_, NAME_` | 普通 | 按任务和变量名查询 |

#### ACT_RU_IDENTITYLINK（运行时身份关联表）

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| `PRIMARY` | `ID_` | 主键 | 身份关联ID主键 |
| `ACT_FK_IDL_TASK` | `TASK_ID_` | 普通 | 关联任务 |
| `ACT_FK_IDL_PROCINST` | `PROC_INSTANCE_ID_` | 普通 | 关联流程实例 |
| `ACT_IDX_IDENT_LNK_USER` | `USER_ID_` | 普通 | 按用户查询 |
| `ACT_IDX_IDENT_LNK_GROUP` | `GROUP_ID_` | 普通 | 按用户组查询 |

#### ACT_RU_JOB（运行时作业表）

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| `PRIMARY` | `ID_` | 主键 | 作业ID主键 |
| `ACT_FK_JOB_EXECUTION` | `EXECUTION_ID_` | 普通 | 关联执行实例 |
| `ACT_FK_JOB_PROCESS_INSTANCE` | `PROCESS_INSTANCE_ID_` | 普通 | 关联流程实例 |
| `ACT_FK_JOB_PROC_DEF` | `PROC_DEF_ID_` | 普通 | 关联流程定义 |
| `ACT_IDX_JOB_EXCEPTION` | `EXCEPTION_STACK_ID_` | 普通 | 异常堆栈 |

#### ACT_RU_EVENT_SUBSCR（运行时事件订阅表）

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| `PRIMARY` | `ID_` | 主键 | 事件订阅ID主键 |
| `ACT_FK_EVENT_EXEC` | `EXECUTION_ID_` | 普通 | 关联执行实例 |

### 2.3 ACT_HI_ 历史表索引

#### ACT_HI_PROCINST（历史流程实例表）

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| `PRIMARY` | `ID_` | 主键 | 历史实例ID主键 |
| `ACT_IDX_PROCINST_START` | `START_TIME_` | 普通 | 按开始时间查询 |
| `ACT_IDX_PROCINST_END` | `END_TIME_` | 普通 | 按结束时间查询 |
| `ACT_IDX_PROCINST_BUSKEY` | `BUSINESS_KEY_` | 普通 | 按业务键查询 |
| `ACT_IDX_HI_PROCINST_PROC_DEF` | `PROC_DEF_ID_` | 普通 | 按流程定义查询 |
| `ACT_FK_HI_PROCINST_PROCDEF` | `PROC_DEF_ID_` | 外键 | 关联流程定义 |
| `ACT_FK_HI_PROCINST_BUSKEY` | `BUSINESS_KEY_` | 外键 | 关联业务键 |

#### ACT_HI_TASKINST（历史任务实例表）

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| `PRIMARY` | `ID_` | 主键 | 历史任务ID主键 |
| `ACT_IDX_TASKINST_PROCINST` | `PROC_INSTANCE_ID_` | 普通 | 按流程实例查询 |
| `ACT_IDX_TASKINST_EXEC` | `EXECUTION_ID_` | 普通 | 按执行实例查询 |
| `ACT_IDX_TASKINST_TASK_DEF` | `TASK_DEF_KEY_` | 普通 | 按任务定义查询 |
| `ACT_IDX_TASKINST_START` | `START_TIME_` | 普通 | 按开始时间查询 |
| `ACT_IDX_TASKINST_END` | `END_TIME_` | 普通 | 按结束时间查询 |
| `ACT_IDX_TASKINST_NAME` | `NAME_` | 普通 | 按任务名称查询 |

#### ACT_HI_ACTINST（历史活动实例表）

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| `PRIMARY` | `ID_` | 主键 | 历史活动ID主键 |
| `ACT_IDX_ACTINST_EXEC` | `EXECUTION_ID_` | 普通 | 按执行实例查询 |
| `ACT_IDX_ACTINST_PROCINST` | `PROC_INSTANCE_ID_` | 普通 | 按流程实例查询 |
| `ACT_IDX_ACTINST_PROCDEF` | `PROC_DEF_ID_` | 普通 | 按流程定义查询 |
| `ACT_IDX_ACTINST_START` | `START_TIME_` | 普通 | 按开始时间查询 |
| `ACT_IDX_ACTINST_END` | `END_TIME_` | 普通 | 按结束时间查询 |
| `ACT_IDX_ACTINST_ACTID` | `ACT_ID_` | 普通 | 按活动ID查询 |

#### ACT_HI_VARINST（历史变量实例表）

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| `PRIMARY` | `ID_` | 主键 | 历史变量ID主键 |
| `ACT_IDX_VARINST_PROCINST` | `PROC_INSTANCE_ID_` | 普通 | 按流程实例查询 |
| `ACT_IDX_VARINST_EXEC` | `EXECUTION_ID_` | 普通 | 按执行实例查询 |
| `ACT_IDX_VARINST_TASK` | `TASK_ID_` | 普通 | 按任务查询 |
| `ACT_IDX_VARINST_NAME` | `NAME_` | 普通 | 按变量名查询 |

#### ACT_HI_IDENTITYLINK（历史身份关联表）

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| `PRIMARY` | `ID_` | 主键 | 历史身份关联ID主键 |
| `ACT_IDX_HI_IDENT_LNK_USER` | `USER_ID_` | 普通 | 按用户查询 |
| `ACT_IDX_HI_IDENT_LNK_GROUP` | `GROUP_ID_` | 普通 | 按用户组查询 |
| `ACT_IDX_HI_IDENT_LNK_TASK` | `TASK_ID_` | 普通 | 按任务查询 |
| `ACT_IDX_HI_IDENT_LNK_PROCINST` | `PROC_INSTANCE_ID_` | 普通 | 按流程实例查询 |

#### ACT_HI_COMMENT（历史评论表）

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| `PRIMARY` | `ID_` | 主键 | 评论ID主键 |
| `ACT_IDX_COMMENT_TASK` | `TASK_ID_` | 普通 | 按任务查询 |
| `ACT_IDX_COMMENT_PROCINST` | `PROC_INSTANCE_ID_` | 普通 | 按流程实例查询 |

#### ACT_HI_DETAIL（历史详情表）

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| `PRIMARY` | `ID_` | 主键 | 详情ID主键 |
| `ACT_IDX_DETAIL_TASK` | `TASK_ID_` | 普通 | 按任务查询 |
| `ACT_IDX_DETAIL_PROCINST` | `PROC_INSTANCE_ID_` | 普通 | 按流程实例查询 |
| `ACT_IDX_DETAIL_NAME` | `NAME_` | 普通 | 按变量名查询 |
| `ACT_IDX_DETAIL_TIME` | `TIME_` | 普通 | 按时间查询 |

### 2.4 ACT_ID_ 身份表索引

#### ACT_ID_USER（用户信息表）

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| `PRIMARY` | `ID_` | 主键 | 用户ID主键 |

#### ACT_ID_GROUP（用户组信息表）

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| `PRIMARY` | `ID_` | 主键 | 用户组ID主键 |

#### ACT_ID_MEMBERSHIP（用户组成员关联表）

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| `PRIMARY` | `USER_ID_, GROUP_ID_` | 联合主键 | 成员关系主键 |
| `ACT_FK_MEMB_GROUP` | `GROUP_ID_` | 普通 | 关联用户组 |
| `ACT_FK_MEMB_USER` | `USER_ID_` | 普通 | 关联用户 |

### 2.5 ACT_GE_ 通用表索引

#### ACT_GE_BYTEARRAY（通用字节数组表）

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| `PRIMARY` | `ID_` | 主键 | 字节数组ID主键 |
| `ACT_FK_BYTEARR_DEPL` | `DEPLOYMENT_ID_` | 外键 | 关联部署 |

#### ACT_GE_PROPERTY（通用属性表）

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| `PRIMARY` | `NAME_` | 主键 | 属性名主键 |

---

## 3. PMS-activiti 查询场景索引分析

### 3.1 待办任务查询

**源码位置**：`ProcessService.findTodoTask()`

```java
taskService.createTaskQuery()
    .taskCandidateOrAssigned(userId)
    .processDefinitionKeyIn(...)
    .active()
    .orderByTaskCreateTime().desc()
    .listPage(firstResult, maxResults);
```

**使用的索引**：
- `ACT_RU_TASK.ACT_IDX_TASK_CREATE`（按创建时间排序）
- `ACT_RU_IDENTITYLINK.ACT_IDX_IDENT_LNK_USER`（按用户查询候选人）

**性能分析**：待办查询通过 `ACT_RU_IDENTITYLINK` 关联 `ACT_RU_TASK`，需要 JOIN 操作。当任务量大时，建议为 `ASSIGNEE_` 字段添加索引。

### 3.2 流程实例查询

**源码位置**：`ProcessInstanceController.runningProcess()`

```java
runtimeService.createProcessInstanceQuery()
    .processDefinitionKey(processDefinitionKey)
    .active()
    .orderByProcessInstanceId().desc()
    .listPage(firstResult, maxResults);
```

**使用的索引**：
- `ACT_RU_EXECUTION.ACT_FK_EXEC_PROCDEF`（按流程定义查询）

### 3.3 历史任务查询

**源码位置**：`ProcessService` 历史相关方法

```java
historyService.createHistoricTaskInstanceQuery()
    .taskAssignee(userId)
    .finished()
    .orderByHistoricTaskInstanceEndTime().desc()
    .listPage(firstResult, maxResults);
```

**使用的索引**：
- `ACT_HI_TASKINST.ACT_IDX_TASKINST_END`（按结束时间排序）

**性能分析**：历史任务按 `ASSIGNEE_` 查询无索引，数据量大时性能较差。

### 3.4 流程定义查询

**源码位置**：`ProcessDefinitionController`

```java
repositoryService.createProcessDefinitionQuery()
    .latestVersion()
    .orderByProcessDefinitionKey().asc()
    .list();
```

**使用的索引**：
- `ACT_RE_PROCDEF.ACT_UNIQ_PROCDEF`（按 KEY+VERSION 唯一约束）

### 3.5 统一任务表查询

**源码位置**：`UserTaskListener.notify()`

```java
actUserTaskService.selectByProcessDefinitionKey(processDefinitionKey);
```

**对应 SQL**：
```sql
SELECT ID, PROC_DEF_KEY, PROC_DEF_NAME, TASK_DEF_KEY, TASK_NAME, TASK_TYPE, CANDIDATE_NAME, CANDIDATE_IDS
FROM dp_act_unify_task
WHERE PROC_DEF_KEY = #{procDefKey}
```

**性能分析**：`dp_act_unify_task` 表无索引，每次任务创建都会查询。数据量小时无影响，建议为 `PROC_DEF_KEY` 添加索引。

---

## 4. 索引优化建议

### 4.1 高优先级建议

#### 4.1.1 为 ACT_RU_TASK.ASSIGNEE_ 添加索引

**原因**：待办任务查询 `taskCandidateOrAssigned(userId)` 频繁使用 `ASSIGNEE_` 字段过滤。

```sql
CREATE INDEX idx_act_ru_task_assignee ON ACT_RU_TASK(ASSIGNEE_);
```

#### 4.1.2 为 ACT_HI_TASKINST.ASSIGNEE_ 添加索引

**原因**：历史任务查询 `taskAssignee(userId)` 频繁使用，无索引时全表扫描。

```sql
CREATE INDEX idx_act_hi_taskinst_assignee ON ACT_HI_TASKINST(ASSIGNEE_);
```

#### 4.1.3 为 dp_act_unify_task.PROC_DEF_KEY 添加索引

**原因**：`UserTaskListener` 每次任务创建都按 `PROC_DEF_KEY` 查询。

```sql
CREATE INDEX idx_dp_act_unify_task_proc_def_key ON dp_act_unify_task(PROC_DEF_KEY);
```

### 4.2 中优先级建议

#### 4.2.1 复合索引优化待办查询

**原因**：待办查询同时按 `ASSIGNEE_` 和 `CREATE_TIME_` 排序，可创建复合索引。

```sql
CREATE INDEX idx_act_ru_task_assignee_create ON ACT_RU_TASK(ASSIGNEE_, CREATE_TIME_);
```

#### 4.2.2 为 ACT_HI_PROCINST.START_USER_ID_ 添加索引

**原因**：查询用户发起的流程实例时使用。

```sql
CREATE INDEX idx_act_hi_procinst_start_user ON ACT_HI_PROCINST(START_USER_ID_);
```

### 4.3 低优先级建议

#### 4.3.1 历史数据归档后删除索引

**原因**：历史表数据量大，索引维护成本高。可考虑按时间分区后，对旧分区删除索引。

#### 4.3.2 监控索引使用率

**SQL**（MySQL 8.0+）：
```sql
SELECT 
    OBJECT_SCHEMA, OBJECT_NAME, INDEX_NAME,
    COUNT_READ, COUNT_FETCH, COUNT_INSERT, COUNT_UPDATE
FROM performance_schema.table_io_waits_summary_by_index_usage
WHERE OBJECT_SCHEMA = 'activiti'
ORDER BY COUNT_READ DESC;
```

---

## 5. 索引维护

### 5.1 索引碎片整理

定期执行 `ANALYZE TABLE` 更新索引统计信息：

```sql
ANALYZE TABLE ACT_RU_TASK;
ANALYZE TABLE ACT_HI_TASKINST;
ANALYZE TABLE ACT_HI_PROCINST;
ANALYZE TABLE ACT_HI_ACTINST;
```

### 5.2 索引重建

当索引碎片率超过 30% 时重建：

```sql
-- MySQL
ALTER TABLE ACT_RU_TASK ENGINE=InnoDB;
OPTIMIZE TABLE ACT_HI_TASKINST;
```

### 5.3 索引监控指标

| 指标 | 阈值 | 处理方式 |
|------|------|----------|
| 索引碎片率 | > 30% | 重建索引 |
| 索引使用率 | < 5% | 考虑删除 |
| 全表扫描次数 | > 100/天 | 添加索引 |
| 慢查询数 | > 10/小时 | 优化查询或索引 |

---

## 6. 索引与查询性能对照表

| 查询场景 | 涉及表 | 涉及字段 | 当前索引 | 建议索引 |
|----------|--------|----------|----------|----------|
| 待办任务查询 | `ACT_RU_TASK` | `ASSIGNEE_`, `CREATE_TIME_` | `ACT_IDX_TASK_CREATE` | 添加 `idx_act_ru_task_assignee` |
| 候选人查询 | `ACT_RU_IDENTITYLINK` | `USER_ID_`, `TASK_ID_` | `ACT_IDX_IDENT_LNK_USER` | 已有索引，无需优化 |
| 历史任务查询 | `ACT_HI_TASKINST` | `ASSIGNEE_`, `END_TIME_` | `ACT_IDX_TASKINST_END` | 添加 `idx_act_hi_taskinst_assignee` |
| 流程实例查询 | `ACT_RU_EXECUTION` | `PROC_DEF_ID_`, `BUSINESS_KEY_` | `ACT_FK_EXEC_PROCDEF` | 已有索引，无需优化 |
| 流程轨迹查询 | `ACT_HI_ACTINST` | `PROC_INSTANCE_ID_` | `ACT_IDX_ACTINST_PROCINST` | 已有索引，无需优化 |
| 统一任务查询 | `dp_act_unify_task` | `PROC_DEF_KEY` | 无 | 添加 `idx_dp_act_unify_task_proc_def_key` |
| 审批意见查询 | `ACT_HI_COMMENT` | `PROC_INSTANCE_ID_` | `ACT_IDX_COMMENT_PROCINST` | 已有索引，无需优化 |

---

## 7. 相关文档

- [database-overview.md](database-overview.md) — 数据库概览
- [complete-data-dictionary.md](complete-data-dictionary.md) — 完整数据字典
- [er-diagram.md](er-diagram.md) — ER 图
- [unify-task-table.md](unify-task-table.md) — 统一任务表详解
- [../05-standards/performance-optimization.md](../05-standards/performance-optimization.md) — 性能优化
