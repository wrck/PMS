# PMS-activiti 故障排查指南

> 本文档汇总 PMS-activiti 模块运行中的常见问题、故障现象、根因分析和解决方案。
> 数据来源：`d:\常规软件\QoderCode\workspace\PMS\PMS-activiti\src\` 源码与运维经验。

---

## 1. 问题分类索引

### 1.1 按场景分类

| 场景 | 常见问题 | 严重程度 | 参见章节 |
|------|----------|----------|----------|
| 流程部署 | BPMN 解析失败、部署后流程不可见 | 中 | 2.1、2.2 |
| 流程启动 | 启动失败、变量缺失 | 高 | 2.3、2.4 |
| 任务分配 | ASSIGNEE 为空、分配错误 | 高 | 2.5、2.6、2.7 |
| 任务办理 | 办理失败、流程卡住 | 高 | 2.8、2.9 |
| 任务撤回 | 撤回失败、撤回后流程异常 | 高 | 2.10、2.11 |
| 任务跳转 | 跳转失败、跳转后状态不一致 | 高 | 2.12 |
| 流程图 | 中文乱码、图片不显示 | 低 | 2.13、2.14 |
| 历史查询 | 查询慢、数据缺失 | 中 | 2.15、2.16 |
| 数据库 | 连接池耗尽、锁表 | 高 | 2.17、2.18 |

### 1.2 按异常类型分类

| 异常类型 | 典型异常类 | 常见场景 | 排查方向 |
|----------|------------|----------|----------|
| 流程异常 | `ActivitiException` | 流程定义错误、任务分配失败 | 检查 BPMN、ACT_RU_TASK |
| 任务异常 | `ActivitiTaskAlreadyClaimedException` | 任务已被签收 | 检查 ACT_RU_TASK.ASSIGNEE_ |
| 流程定义异常 | `ActivitiObjectNotFoundException` | 流程定义不存在 | 检查 ACT_RE_PROCDEF |
| 状态异常 | `ActivitiIllegalStateException` | 流程已挂起/已结束 | 检查 SUSPENSION_STATE_ |
| 数据库异常 | `SQLException` | 连接池耗尽、死锁 | 检查连接池、慢查询 |
| 空指针 | `NullPointerException` | 任务不存在、变量为空 | 检查参数、查询结果 |

---

## 2. 常见问题与解决方案

### 2.1 流程部署失败 - BPMN 解析错误

**问题现象**：上传 BPMN 文件部署时，报错 `org.activiti.bpmn.exceptions.XMLException` 或 `org.activiti.engine.ActivitiException: Error parsing XML`。

**根因分析**：
1. BPMN XML 格式不合法（标签未闭合、属性缺失）
2. BPMN 命名空间错误
3. 流程定义 ID 包含特殊字符
4. BPMN 文件编码非 UTF-8

**排查步骤**：
1. 使用 BPMN 编辑器（Activiti Modeler）打开文件，检查格式
2. 检查 XML 头部命名空间：
   ```xml
   <definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"
                xmlns:activiti="http://activiti.org/bpmn"
                targetNamespace="http://www.activiti.org/processdef">
   ```
3. 检查文件编码：`file -i CallBack.bpmn` 应为 UTF-8
4. 查看 Tomcat 日志中的完整异常堆栈

**解决方案**：
- 修正 BPMN XML 格式错误
- 重新导出 BPMN 文件（使用 Activiti Modeler）
- 确保文件编码为 UTF-8

### 2.2 部署后流程定义不可见

**问题现象**：部署成功但流程定义列表中看不到新流程。

**根因分析**：
1. 流程定义被挂起（`SUSPENSION_STATE_ = 2`）
2. 查询条件过滤（如 `latestVersion()` 过滤）
3. 部署到错误的租户

**排查 SQL**：
```sql
-- 检查流程定义
SELECT ID_, KEY_, VERSION_, SUSPENSION_STATE_, TENANT_ID_, DEPLOYMENT_ID_
FROM ACT_RE_PROCDEF
WHERE KEY_ = 'CallBack'
ORDER BY VERSION_ DESC;

-- 检查部署记录
SELECT * FROM ACT_RE_DEPLOYMENT
WHERE ID_ = (SELECT DEPLOYMENT_ID_ FROM ACT_RE_PROCDEF WHERE KEY_ = 'CallBack' LIMIT 1);
```

**解决方案**：
- 激活流程定义：`repositoryService.activateProcessDefinitionById(procDefId);`
- 检查查询条件是否包含 `latestVersion()`

### 2.3 流程启动失败 - 流程定义不存在

**问题现象**：调用 `runtimeService.startProcessInstanceByKey("CallBack")` 报错 `ActivitiObjectNotFoundException: no processes deployed with key`。

**根因分析**：
1. 流程定义 Key 拼写错误（区分大小写）
2. 流程定义未部署
3. 流程定义已挂起

**排查步骤**：
```sql
-- 确认流程定义存在
SELECT ID_, KEY_, VERSION_, SUSPENSION_STATE_
FROM ACT_RE_PROCDEF
WHERE KEY_ = 'CallBack';
```

**解决方案**：
- 核对流程定义 Key（BPMN 中 `<process id="CallBack">` 的 `id` 属性）
- 重新部署流程
- 激活挂起的流程定义

### 2.4 流程启动失败 - 变量缺失

**问题现象**：启动流程时报错 `Cannot find variable: entity` 或 `NullPointerException`。

**根因分析**：
1. BPMN 中配置了 `UserTaskListener`，`TASK_TYPE=modify`，但启动时未传入 `entity` 变量
2. BPMN 表达式 `${serviceManager}` 引用的变量未设置

**排查步骤**：
1. 检查 BPMN 中的表达式与监听器配置
2. 检查 `dp_act_unify_task` 中是否有 `TASK_TYPE=modify` 的配置
3. 检查启动代码是否设置了所有必需变量

**解决方案**：
```java
// 确保传入所有必需变量
Map<String, Object> variables = new HashMap<>();
BaseVO entity = new BaseVO();
entity.setUserId(currentUserId());
variables.put("entity", entity);
variables.put("serviceManager", serviceManagerId);
// 其他 BPMN 中引用的变量
runtimeService.startProcessInstanceByKey("CallBack", businessKey, variables);
```

### 2.5 任务创建后 ASSIGNEE 为空

**问题现象**：流程启动或任务流转后，`ACT_RU_TASK.ASSIGNEE_` 为 NULL，待办列表中看不到任务。

**根因分析**：
1. BPMN 中未配置 `UserTaskListener`
2. `dp_act_unify_task` 中无对应 `PROC_DEF_KEY` + `TASK_DEF_KEY` 的记录
3. `TASK_TYPE` 取值错误（大小写敏感，必须小写）
4. `UserTaskListener` 抛出异常被 catch 吞掉

**排查步骤**：
```sql
-- 1. 检查任务实际 assignee
SELECT ID_, NAME_, ASSIGNEE_, PROC_DEF_ID_, TASK_DEF_KEY_
FROM ACT_RU_TASK
WHERE PROC_INSTANCE_ID_ = 'xxx';

-- 2. 检查 dp_act_unify_task 配置
SELECT * FROM dp_act_unify_task
WHERE PROC_DEF_KEY = 'CallBack'
  AND TASK_DEF_KEY = 'approveTask';

-- 3. 检查 BPMN 是否配置了监听器
-- 查看 BPMN 文件中 userTask 节点是否有 <activiti:taskListener>
```

**解决方案**：
- 在 BPMN 中为 `userTask` 添加 `UserTaskListener`：
  ```xml
  <userTask id="approveTask" name="主管审批">
      <extensionElements>
          <activiti:taskListener event="create" delegateExpression="${userTaskListener}"/>
      </extensionElements>
  </userTask>
  ```
- 在 `dp_act_unify_task` 中添加配置
- 确保 `TASK_TYPE` 为小写：`assignee` / `candidateUser` / `candidateGroup` / `modify`

### 2.6 任务分配给错误用户

**问题现象**：任务分配给了非预期的用户。

**根因分析**：
1. `dp_act_unify_task.CANDIDATE_IDS` 配置错误
2. `candidateUser` 单人时直接 `setAssignee`（设计如此）
3. 用户 ID 与用户名混淆

**排查步骤**：
```sql
-- 检查配置
SELECT TASK_DEF_KEY, TASK_TYPE, CANDIDATE_IDS
FROM dp_act_unify_task
WHERE PROC_DEF_KEY = 'CallBack';
```

**解决方案**：
- 核对 `CANDIDATE_IDS` 中的用户 ID
- 注意 `candidateUser` 单人时直接分配，多人时才进入候选人待办

### 2.7 modify 类型报 NullPointerException

**问题现象**：任务创建时报错 `NullPointerException`，日志指向 `UserTaskListener`。

**根因分析**：`TASK_TYPE=modify` 时，从流程变量 `entity` 读取 `userId`，但：
1. 流程变量中未设置 `entity`
2. `entity` 中 `userId` 为 null

**解决方案**：
```java
// 启动流程时必须设置 entity 变量
BaseVO entity = new BaseVO();
entity.setUserId(currentUserId());  // 确保 userId 非空
Map<String, Object> variables = new HashMap<>();
variables.put("entity", entity);
runtimeService.startProcessInstanceByKey("CallBack", businessKey, variables);
```

### 2.8 任务办理失败 - 任务不存在

**问题现象**：调用 `taskService.complete(taskId)` 报错 `ActivitiObjectNotFoundException: Cannot find task with id`。

**根因分析**：
1. `taskId` 拼写错误
2. 任务已被他人办理（并发）
3. 任务已被撤回

**排查步骤**：
```sql
-- 检查运行时任务
SELECT * FROM ACT_RU_TASK WHERE ID_ = 'xxx';

-- 检查历史任务（确认是否已被办理）
SELECT ID_, END_TIME_, DELETE_REASON_, ASSIGNEE_
FROM ACT_HI_TASKINST
WHERE ID_ = 'xxx';
```

**解决方案**：
- 前端刷新待办列表，避免操作已失效的任务
- 后端添加乐观锁校验

### 2.9 流程卡住不流转

**问题现象**：任务办理后，流程未流转到下一节点，`ACT_RU_TASK` 无新记录。

**根因分析**：
1. 网关条件不满足（如 `result` 变量值与 BPMN 条件不匹配）
2. 下一节点为 `userTask` 但 `UserTaskListener` 抛异常
3. 流程已结束（到达 `endEvent`）

**排查步骤**：
```sql
-- 1. 检查流程实例是否还在运行
SELECT * FROM ACT_RU_EXECUTION WHERE PROC_INSTANCE_ID_ = 'xxx';

-- 2. 检查历史活动，确认流程走到哪里
SELECT ACT_ID_, ACT_NAME_, ACT_TYPE_, START_TIME_, END_TIME_
FROM ACT_HI_ACTINST
WHERE PROC_INSTANCE_ID_ = 'xxx'
ORDER BY START_TIME_;

-- 3. 检查流程变量
SELECT NAME_, TEXT_ FROM ACT_RU_VARIABLE WHERE PROC_INSTANCE_ID_ = 'xxx';
```

**解决方案**：
- 核对 BPMN 网关条件与传入的 `result` 变量值
- 检查 `UserTaskListener` 是否抛异常（查看日志）
- 确认流程是否已正常结束

### 2.10 撤回失败 - 返回 0

**问题现象**：调用 `revoke()` 返回 0，撤回失败。

**根因分析**：`RevokeTaskCmd` 返回 0 表示：
1. 上一任务不存在（流程刚启动，无历史任务）
2. 当前任务已结束

**排查步骤**：
```sql
-- 检查历史任务
SELECT ID_, NAME_, ASSIGNEE_, START_TIME_, END_TIME_, DELETE_REASON_
FROM ACT_HI_TASKINST
WHERE PROC_INSTANCE_ID_ = 'xxx'
ORDER BY START_TIME_ DESC;

-- 检查当前任务
SELECT ID_, NAME_, ASSIGNEE_ FROM ACT_RU_TASK WHERE PROC_INSTANCE_ID_ = 'xxx';
```

**解决方案**：
- 确认流程有历史任务（至少办理过一步）
- 确认当前任务未结束

### 2.11 撤回失败 - 返回 2

**问题现象**：调用 `revoke()` 返回 2，撤回失败。

**根因分析**：`RevokeTaskCmd` 返回 2 表示不满足撤回条件（如已无下一任务或当前任务已结束）。

**解决方案**：
- 检查流程是否已流转多步（撤回仅支持回退到上一节点）
- 检查当前任务是否已被办理

### 2.12 任务跳转失败

**问题现象**：调用 `moveTo()` 跳转失败。

**根因分析**：
1. 目标 `activityId` 不存在
2. 当前任务已被办理
3. 多实例任务跳转逻辑复杂

**排查步骤**：
```sql
-- 检查 BPMN 中的活动节点
SELECT * FROM ACT_RE_PROCDEF WHERE KEY_ = 'CallBack';

-- 检查当前任务
SELECT ID_, TASK_DEF_KEY_, NAME_ FROM ACT_RU_TASK WHERE ID_ = 'taskId';
```

**解决方案**：
- 核对目标 `activityId` 与 BPMN 中的节点 ID
- 确认当前任务未结束
- 多实例任务跳转需特殊处理

### 2.13 流程图中文乱码

**问题现象**：生成的流程图 PNG 中，中文显示为方框或乱码。

**根因分析**：
1. 服务器未安装中文字体
2. `ProcessDiagramGenerator` 字体配置错误

**配置位置**：`d:\常规软件\QoderCode\workspace\PMS\PMS-activiti\src\main\resources\engine.properties`

**解决方案**：

1. **安装中文字体**（Linux 服务器）：
   ```bash
   # 安装宋体
   mkdir -p /usr/share/fonts/chinese
   cp simsun.ttc /usr/share/fonts/chinese/
   fc-cache -fv
   ```

2. **配置字体**（`engine.properties`）：
   ```properties
   activiti.diagram.activityFontName=宋体
   activiti.diagram.labelFontName=宋体
   ```

3. **JVM 参数**：
   ```bash
   -Dfile.encoding=UTF-8
   -Dsun.jnu.encoding=UTF-8
   ```

### 2.14 流程图不显示

**问题现象**：页面中流程图图片不显示，HTTP 404 或 500。

**根因分析**：
1. 流程定义未部署流程图 PNG（`ACT_GE_BYTEARRAY` 中无图片）
2. `ProcessDiagramGenerator` 生成失败
3. 流程实例已结束，运行时数据被清理

**排查步骤**：
```sql
-- 检查流程图资源
SELECT * FROM ACT_GE_BYTEARRAY
WHERE DEPLOYMENT_ID_ = (
    SELECT DEPLOYMENT_ID_ FROM ACT_RE_PROCDEF WHERE ID_ = 'xxx'
);

-- 检查流程实例是否在运行
SELECT * FROM ACT_RU_EXECUTION WHERE PROC_INSTANCE_ID_ = 'xxx';
```

**解决方案**：
- 流程实例已结束时，使用历史数据生成流程图：
  ```java
  // 使用 HistoricProcessInstance 查询
  HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery()
      .processInstanceId(processInstanceId)
      .singleResult();
  ```

### 2.15 历史查询慢

**问题现象**：历史任务查询响应慢，超过 2 秒。

**根因分析**：
1. `ACT_HI_TASKINST` 数据量大（百万级）
2. `ASSIGNEE_` 字段无索引
3. 查询未限定时间范围

**解决方案**：

1. **添加索引**：
   ```sql
   CREATE INDEX idx_act_hi_taskinst_assignee ON ACT_HI_TASKINST(ASSIGNEE_);
   CREATE INDEX idx_act_hi_taskinst_assignee_end ON ACT_HI_TASKINST(ASSIGNEE_, END_TIME_);
   ```

2. **限定时间范围**：
   ```java
   historyService.createHistoricTaskInstanceQuery()
       .taskAssignee(userId)
       .finished()
       .taskCompletedAfter(startDate)  // 限定时间
       .orderByHistoricTaskInstanceEndTime().desc()
       .listPage(firstResult, maxResults);
   ```

3. **定期清理历史数据**（参见 [performance-optimization.md](performance-optimization.md)）

### 2.16 历史数据缺失

**问题现象**：查询历史任务时，部分数据缺失。

**根因分析**：
1. `history.level` 配置为 `none` 或 `activity`，未记录任务历史
2. 历史数据被清理脚本删除
3. 流程被强制删除（`deleteProcessInstance` 不记录历史）

**排查步骤**：
```sql
-- 检查历史级别配置
SELECT * FROM ACT_GE_PROPERTY WHERE NAME_ = 'history.level';

-- 检查历史流程实例
SELECT * FROM ACT_HI_PROCINST WHERE ID_ = 'xxx';

-- 检查历史任务
SELECT * FROM ACT_HI_TASKINST WHERE PROC_INSTANCE_ID_ = 'xxx';
```

**解决方案**：
- 确认 `history.level=full` 或 `audit`
- 避免使用 `deleteProcessInstance` 删除流程（改用终止）

### 2.17 数据库连接池耗尽

**问题现象**：日志报错 `Cannot get a connection, pool exhausted` 或 `Could not get JDBC Connection`。

**根因分析**：
1. 慢查询占用连接时间过长
2. 代码中存在连接泄漏
3. 并发量突增超过连接池容量

**排查步骤**：
```sql
-- 查看当前数据库连接数
SHOW PROCESSLIST;

-- 查找长时间运行的查询
SELECT * FROM information_schema.processlist WHERE TIME > 60 ORDER BY TIME DESC;
```

**解决方案**：
- 优化慢查询（添加索引）
- 增加连接池 `maxActive`
- 开启 `removeAbandoned=true` 检测连接泄漏
- 重启应用释放连接

### 2.18 数据库死锁

**问题现象**：日志报错 `Deadlock found when trying to get lock`。

**根因分析**：
1. 并发更新同一流程实例
2. 事务范围过大
3. 多实例任务并发办理

**排查步骤**：
```sql
-- 查看最近死锁
SHOW ENGINE INNODB STATUS;

-- 查看当前锁
SELECT * FROM information_schema.INNODB_LOCKS;
SELECT * FROM information_schema.INNODB_LOCK_WAITS;
```

**解决方案**：
- 避免并发办理同一流程实例的任务
- 缩小事务范围
- 添加乐观锁校验

---

## 3. 日志分析指南

### 3.1 关键日志位置

| 日志类型 | 位置 | 查看方式 |
|----------|------|----------|
| 应用日志 | `{TOMCAT_HOME}/logs/catalina.out` | `tail -f catalina.out` |
| Activiti 日志 | 应用日志中搜索 `org.activiti` | `grep "org.activiti" catalina.out` |
| 业务日志 | `{TOMCAT_HOME}/logs/pms.log` | `tail -f pms.log` |
| 错误日志 | 应用日志中搜索 `ERROR` | `grep "ERROR" catalina.out` |

### 3.2 常见错误日志模式

| 错误日志模式 | 含义 | 排查方向 |
|-------------|------|----------|
| `ActivitiObjectNotFoundException` | 对象不存在 | 检查 ID 是否正确 |
| `ActivitiException: Error parsing XML` | BPMN 解析失败 | 检查 BPMN 格式 |
| `ActivitiIllegalStateException` | 状态异常 | 检查流程/任务状态 |
| `ActivitiTaskAlreadyClaimedException` | 任务已被签收 | 检查 ASSIGNEE_ |
| `Cannot get a connection, pool exhausted` | 连接池耗尽 | 检查连接池配置 |
| `Deadlock found when trying to get lock` | 数据库死锁 | 检查并发操作 |
| `NullPointerException` | 空指针 | 检查参数与查询结果 |

### 3.3 日志级别建议

```xml
<!-- 生产环境 -->
<logger name="org.activiti">
    <level value="WARN" />  <!-- Activiti 日志级别 -->
</logger>
<logger name="com.dp.plat.activiti">
    <level value="INFO" />  <!-- 业务日志级别 -->
</logger>

<!-- 排查问题时临时开启 -->
<logger name="org.activiti.engine.impl.persistence">
    <level value="DEBUG" />  <!-- 查看 SQL -->
</logger>
```

---

## 4. 诊断工具

### 4.1 数据库诊断 SQL

```sql
-- 1. 流程定义统计
SELECT KEY_, VERSION_, COUNT(*) as 部署次数, 
       SUM(CASE WHEN SUSPENSION_STATE_=1 THEN 1 ELSE 0 END) as 激活数
FROM ACT_RE_PROCDEF
GROUP BY KEY_, VERSION_
ORDER BY KEY_, VERSION_ DESC;

-- 2. 运行中流程实例统计
SELECT PROC_DEF_ID_, COUNT(*) as 运行中实例数
FROM ACT_RU_EXECUTION
WHERE PARENT_ID_ IS NULL
GROUP BY PROC_DEF_ID_;

-- 3. 待办任务统计
SELECT ASSIGNEE_, COUNT(*) as 待办数
FROM ACT_RU_TASK
WHERE ASSIGNEE_ IS NOT NULL
GROUP BY ASSIGNEE_
ORDER BY 待办数 DESC
LIMIT 20;

-- 4. 历史任务表数据量
SELECT 
    DATE(START_TIME_) as 日期,
    COUNT(*) as 任务数
FROM ACT_HI_TASKINST
WHERE START_TIME_ > DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY DATE(START_TIME_)
ORDER BY 日期 DESC;

-- 5. 慢查询（MySQL 慢查询日志）
SELECT * FROM mysql.slow_log
WHERE start_time > DATE_SUB(NOW(), INTERVAL 1 DAY)
ORDER BY query_time DESC
LIMIT 20;
```

### 4.2 Arthas 在线诊断

```bash
# 查看 ProcessService 方法耗时
trace com.dp.plat.activiti.service.impl.ProcessService complete

# 查看任务分配逻辑
watch com.dp.plat.activiti.process.listener.UserTaskListener notify "{params, returnObj, throwExp}" -x 2

# 查看数据库连接
dashboard
```

---

## 5. 应急处理

### 5.1 流程卡死应急处理

**场景**：流程实例卡住，无法流转。

**处理步骤**：
1. 确认流程实例状态：
   ```sql
   SELECT * FROM ACT_RU_EXECUTION WHERE PROC_INSTANCE_ID_ = 'xxx';
   ```

2. 如流程实例存在但无任务：
   ```sql
   SELECT * FROM ACT_RU_TASK WHERE PROC_INSTANCE_ID_ = 'xxx';
   ```

3. 如需手动跳转，使用 `JumpTaskCmdService`：
   ```java
   processService.moveTo(taskId, "targetActivityId");
   ```

4. 如需强制终止：
   ```java
   runtimeService.deleteProcessInstance(processInstanceId, "应急终止");
   ```

### 5.2 数据库连接耗尽应急处理

**处理步骤**：
1. 查看连接数：
   ```sql
   SHOW PROCESSLIST;
   ```

2. 杀掉长时间运行的连接：
   ```sql
   KILL <connection_id>;
   ```

3. 临时增加连接池上限
4. 重启应用

### 5.3 历史数据膨胀应急处理

**处理步骤**：
1. 查看数据量：
   ```sql
   SELECT 
       (SELECT COUNT(*) FROM ACT_HI_PROCINST) as 历史实例数,
       (SELECT COUNT(*) FROM ACT_HI_TASKINST) as 历史任务数,
       (SELECT COUNT(*) FROM ACT_HI_ACTINST) as 历史活动数;
   ```

2. 紧急清理 1 年前的数据（参见 [performance-optimization.md](performance-optimization.md) 第 3 节）

3. 降级历史级别为 `audit`

---

## 6. 相关文档

- [coding-standards.md](coding-standards.md) — 编码规范
- [performance-optimization.md](performance-optimization.md) — 性能优化
- [security-practices.md](security-practices.md) — 安全实践
- [../06-reference/error-codes.md](../06-reference/error-codes.md) — 错误码
- [../03-database/index-analysis.md](../03-database/index-analysis.md) — 索引分析
- [../02-modules/custom-commands.md](../02-modules/custom-commands.md) — 自定义命令
