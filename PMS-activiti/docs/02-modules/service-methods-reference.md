# PMS-activiti Service 方法级参考文档

> 本文档深度分析 PMS-activiti 所有 Service 接口和实现类的完整方法签名、核心算法、事务逻辑和异常处理机制。
> 所有方法描述均基于实际源码（`com.dp.plat.activiti.service` 包），不包含臆造的方法。

---

## 目录

1. [IWorkflowService — 工作流核心服务](#1-iworkflowservice--工作流核心服务)
2. [IProcessService — 流程管理服务](#2-iprocessservice--流程管理服务)
3. [IActUserTaskService — 用户任务配置服务](#3-iactusertaskservice--用户任务配置服务)
4. [ITraceService — 流程追踪服务](#4-itraceservice--流程追踪服务)
5. [IVacationService — 请假服务](#5-ivacationservice--请假服务)
6. [IPerformanceService — 绩效服务](#6-iperformanceservice--绩效服务)
7. [IRuntimePageService — 运行时页面服务](#7-iruntimepageservice--运行时页面服务)

---

## 1. IWorkflowService — 工作流核心服务

### 类概述
- 接口：`IWorkflowService`（`com.dp.plat.activiti.service.IWorkflowService`）
- 实现类：`WorkflowService`（`@Service("workflowService")`）
- 依赖：`TaskService`

### 方法列表

#### `List<Task> getCurrentTaskInfo(ProcessInstance processInstance)`
- **功能**：获取流程实例当前节点的任务信息
- **事务类型**：无事务
- **参数**：
  | 参数名 | 类型 | 业务含义 | 校验规则 | 取值范围 |
  |--------|------|----------|----------|----------|
  | processInstance | ProcessInstance | 流程实例 | 非空 | `runtimeService.createProcessInstanceQuery()` 查询结果 |
- **返回值**：`List<Task>` - 当前节点任务列表；**异常时返回 null**（非空列表）
- **核心算法**（源码 `WorkflowService.java:31-42`）：
  1. 通过 `PropertyUtils.getProperty(processInstance, "activityId")` 反射获取流程实例当前活动 ID
  2. 通过 `taskService.createTaskQuery().processInstanceId(id).taskDefinitionKey(activitiId).list()` 查询任务
  3. 返回任务列表
- **异常处理**：
  | 异常类型 | 触发条件 | 处理方式 |
  |----------|----------|----------|
  | Exception | 反射获取属性失败（如 processInstance 不含 activityId 属性）或查询失败 | `e.printStackTrace()` 并返回 null |
- **边界条件**：
  - `processInstance == null`：`PropertyUtils.getProperty` 抛 NPE，被 catch 后返回 null
  - **流程已结束**：`processInstance` 为 null 时不应调用此方法（调用方需自行判断）
  - **多实例节点**：同一 activityId 可能对应多个 Task，返回列表大小 > 1
  - **返回值可能为 null**：调用方（如 `RevokeTaskCmd`）需做 null 检查，否则遍历会 NPE

---

## 2. IProcessService — 流程管理服务

### 类概述
- 接口：`IProcessService`（`com.dp.plat.activiti.service.IProcessService`）
- 实现类：`ProcessService`（`@Service("processService")`）
- 依赖：`RuntimeService`, `IdentityService`, `TaskService`, `RepositoryService`, `HistoryService`, `ManagementService`, `IUserService`, `IWorkflowService`, `IVacationService`, `ProcessEngine`, `ProcessEngineFactoryBean`, `ProcessEngineConfiguration`

### 方法列表（共 24 个方法）

#### 2.1 任务办理类

##### `void claim(User user, String taskId) throws Exception`
- **功能**：签收任务
- **参数**：`user` - 用户对象, `taskId` - 任务 ID
- **核心算法**：调用 `taskService.claim(taskId, userId)` 完成签收

##### `void unclaim(String taskId)`
- **功能**：取消签收任务
- **参数**：`taskId` - 任务 ID
- **核心算法**：调用 `taskService.unclaim(taskId)`

##### `void delegateTask(String userId, String taskId) throws Exception`
- **功能**：委派任务（将任务委派给指定用户办理，办理后回到原办理人）
- **参数**：`userId` - 委派目标用户 ID, `taskId` - 任务 ID

##### `void transferTask(String userId, String taskId) throws Exception`
- **功能**：转办任务（将任务转给指定用户，原办理人不再参与）
- **参数**：`userId` - 转办目标用户 ID, `taskId` - 任务 ID

##### `void complete(String taskId, String content, String userid, Map<String, Object> variables) throws Exception`
- **功能**：完成任务（含委派任务解决、审批意见记录、流程变量设置）
- **事务类型**：`@Transactional`（源码 `ProcessService.java:568`）
- **参数**：
  | 参数名 | 类型 | 业务含义 | 校验规则 | 取值范围/默认值 |
  |--------|------|----------|----------|------------------|
  | taskId | String | 任务 ID | 非空 | Activiti 任务 ID（如 "12345" 数字串） |
  | content | String | 审批意见 | 可空 | 为 null 时不调用 addComment；非空时作为流程评论持久化 |
  | userid | String | 办理人 ID | 非空 | 必须为已签收或候选办理人，否则查不到任务 |
  | variables | Map<String, Object> | 流程变量 | 可空 | 传入后会同时调用 setVariablesLocal 和 complete |
- **核心算法**（源码 `ProcessService.java:569-593`）：
  1. 通过 `taskService.createTaskQuery().taskCandidateOrAssigned(userid).taskId(taskId).singleResult()` 查询任务
  2. 任务为 null 时抛 `ActivitiObjectNotFoundException("任务不存在！")`
  3. 查询流程实例 `ProcessInstance`
  4. 调用 `identityService.setAuthenticatedUserId(userid)` 设置评论人（**必须**，否则查看评论时报错）
  5. **仅当 content != null 时**调用 `taskService.addComment(taskId, processInstanceId, content)`
  6. 调用 `taskService.setVariablesLocal(task.getId(), variables)` 设置任务本地变量
  7. **委派任务处理**：若 `DelegationState.PENDING == task.getDelegationState()`，先调用 `taskService.resolveTask(taskId, variables)` 解决委派
  8. 调用 `taskService.setAssignee(taskId, userid)` 强制设置办理人
  9. 调用 `taskService.complete(taskId, variables)` 完成任务
- **异常处理**：
  | 异常类型 | 触发条件 | 处理方式 |
  |----------|----------|----------|
  | ActivitiObjectNotFoundException | 任务不存在（taskId 错误或 userid 不匹配） | 抛出异常，消息"任务不存在！" |
  | 其他 RuntimeException | 流程引擎内部异常 | 向上传播（方法未 catch） |
- **边界条件**：
  - `content == null`：跳过 addComment，其余流程正常执行
  - `variables == null`：setVariablesLocal 和 complete 接收 null（由 Activiti 内部处理）
  - **委派任务**：当任务处于 `DelegationState.PENDING` 时，先 resolveTask 再 complete，确保委派状态正确流转
  - **userid 与任务不匹配**：taskCandidateOrAssigned 查询返回 null，抛出"任务不存在！"
- **使用示例（审批通过流程）**：
  ```java
  // 场景：审批人办理任务，同意审批
  Map<String, Object> variables = new HashMap<>();
  variables.put("isPass", true);           // 排他网关条件变量
  variables.put("approveResult", "同意");   // 自定义审批结果
  processService.complete(
      "10001",                    // taskId - 待办任务 ID
      "同意，材料齐全，予以通过",   // content - 审批意见
      "user001",                   // userid - 当前办理人
      variables                    // variables - 流程变量
  );
  // 执行后：任务完成，评论已记录，流程流转至下一节点
  ```
- **使用示例（驳回流程）**：
  ```java
  Map<String, Object> variables = new HashMap<>();
  variables.put("isPass", false);
  variables.put("approveResult", "驳回");
  processService.complete(taskId, "材料不完整，请补充后重新提交", "user001", variables);
  ```

#### 2.2 任务撤回与跳转类

##### `Integer revoke(String historyTaskId, String processInstanceId) throws Exception`
- **功能**：撤销任务（基于历史任务 ID 撤回已完成的任务，恢复至该历史任务节点）
- **事务类型**：`@Transactional`（源码 `ProcessService.java:667`）
- **参数**：
  | 参数名 | 类型 | 业务含义 | 校验规则 | 取值范围 |
  |--------|------|----------|----------|----------|
  | historyTaskId | String | 历史任务 ID | 非空 | ACT_HI_TASKINST.ID_ 值，必须为已完成任务 |
  | processInstanceId | String | 流程实例 ID | 非空 | ACT_RU_EXECUTION.PROC_INST_ID_ 值 |
- **返回值**：`Integer` - 撤销结果（返回值源自 `RevokeTaskCmd.execute`，源码 `RevokeTaskCmd.java:68`）
  | 返回值 | 含义 | 触发条件 |
  |--------|------|----------|
  | 0 | 撤销成功 | 流程实例存在且下一节点未通过 |
  | 1 | 流程已结束 | `processInstance == null`（流程已完结） |
  | 2 | 下一结点已经通过 | 当前任务 `deleteReason == "completed"` |
- **核心算法**（源码 `ProcessService.java:668-672`）：
  1. 构造 `RevokeTaskCmd(historyTaskId, processInstanceId, runtimeService, workflowService, historyService)`
  2. 调用 `processEngine.getManagementService().executeCommand(cmd)` 执行命令
- **RevokeTaskCmd 内部执行流程**（源码 `RevokeTaskCmd.java:74-121`）：
  1. 查询历史任务实体 `HistoricTaskInstanceEntity`
  2. 通过 `getHistoricActivityInstanceEntity` 查询历史活动节点（使用 JdbcTemplate 直接查询 ACT_HI_ACTINST）
  3. 查询流程实例 `processInstance`：
     - 为 null → 返回 1（流程已结束）
     - 不为 null → 获取当前任务列表 `currentTasks`
  4. 遍历当前任务，检查 `deleteReason`：
     - 若为 "completed" → 返回 2（下一节点已通过）
  5. 删除所有活动中的任务（`deleteActiveTasks`）
  6. 执行 `DeleteActiveTaskCmd` 删除当前任务
  7. 删除历史活动记录（`deleteHistoryActivities`）
  8. 恢复历史任务（`processHistoryTask`）：重置 endTime/duration，重建 TaskEntity，将流程指向任务对应节点
- **边界条件**：
  - `currentTasks` 为空但 `processInstance != null`：for 循环不执行，直接返回 0
  - 历史任务不存在：`findHistoricTaskInstanceById` 返回 null，后续 `historicTaskInstanceEntity` 操作可能 NPE
  - **JdbcTemplate 直接操作**：`RevokeTaskCmd` 通过 `SpringContext.getBean("dataSource")` 获取 `RoutingDataSource`，绕过 Activiti 上下文直接执行 SQL

##### `Object withdrawTask(String instanceId, String userId)`
- **功能**：撤回任务（撤回当前用户已办理的下一个任务，回到当前用户）
- **参数**：`instanceId` - 流程实例 ID, `userId` - 用户 ID
- **返回值**：`Object` - 撤回结果
- **核心算法**：通过 `WithdrawTaskCmd` 执行撤回命令，支持多实例节点撤回

##### `void moveTo(String currentTaskId, String targetTaskDefinitionKey) throws Exception`
- **功能**：跳转（包括回退和向前）至指定活动节点
- **事务类型**：调用私有 `moveTo(TaskEntity, ActivityImpl)`（该方法标注 `@Transactional`，但**注意**：私有方法的 `@Transactional` 不被 Spring AOP 代理拦截，事务可能不生效）
- **参数**：
  | 参数名 | 类型 | 业务含义 | 校验规则 | 取值范围 |
  |--------|------|----------|----------|----------|
  | currentTaskId | String | 当前任务 ID | 非空 | ACT_RU_TASK.ID_ 值 |
  | targetTaskDefinitionKey | String | 目标节点定义键 | 非空 | BPMN 中 userTask 的 id 属性，必须存在于流程定义中 |
- **核心算法**（源码 `ProcessService.java:911-914`）：
  1. 通过 `taskService.createTaskQuery().taskId(currentTaskId).singleResult()` 查询 TaskEntity
  2. 调用 `moveTo(TaskEntity, targetTaskDefinitionKey)` 重载方法
- **边界条件**：
  - `currentTaskId` 不存在：taskEntity 为 null，后续操作 NPE
  - `targetTaskDefinitionKey` 在 BPMN 中不存在：`findActivity` 返回 null，`moveTo(TaskEntity, ActivityImpl)` NPE

##### `void moveTo(TaskEntity currentTaskEntity, String targetTaskDefinitionKey) throws Exception`
- **功能**：跳转（包括回退和向前）至指定活动节点（重载方法，传入 TaskEntity）
- **事务类型**：同上（调用私有 `moveTo`）
- **参数**：
  | 参数名 | 类型 | 业务含义 | 校验规则 |
  |--------|------|----------|----------|
  | currentTaskEntity | TaskEntity | 当前任务实体 | 非空 |
  | targetTaskDefinitionKey | String | 目标节点定义键 | 非空，必须存在于 BPMN 流程定义 |
- **核心算法**（源码 `ProcessService.java:920-926`）：
  1. 通过 `((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition()` 获取 `ProcessDefinitionEntity`
  2. 调用 `processDefinitionEntity.findActivity(targetTaskDefinitionKey)` 获取 `ActivityImpl`
  3. 调用私有 `moveTo(currentTaskEntity, activity)`
- **私有 moveTo 实现**（源码 `ProcessService.java:928-934`）：
  - 构造 `DeleteActiveTaskCmd(currentTaskEntity, "jump", true)` 删除当前活动任务
  - 构造 `StartActivityCmd(executionId, activity)` 启动目标节点
  - 通过 `managementService.executeCommand` 分别执行两个 Command

##### `Result canWithdraw(String processInstanceId, String userId)`
- **功能**：判断流程能否撤回
- **参数**：`processInstanceId` - 流程实例 ID, `userId` - 用户 ID
- **返回值**：`Result` - 是否可撤回

#### 2.3 查询类

##### `List<BaseVO> findTodoTask(User user, PageParam<BaseVO> page) throws Exception`
- **功能**：查询待办任务
- **参数**：`user` - 用户对象, `page` - 分页参数
- **返回值**：`List<BaseVO>` - 待办任务列表
- **核心算法**：根据用户角色区分查询：
  - 管理员：查询所有活动任务，支持按任务描述/名称模糊搜索
  - 普通用户：通过 `taskCandidateOrAssigned(userId)` 查询已签收和候选任务

##### `List<BaseVO> findFinishedProcessInstances(PageParam<BaseVO> page) throws Exception`
- **功能**：读取已结束的流程实例（admin 查看）
- **参数**：`page` - 分页参数
- **返回值**：`List<BaseVO>` - 已结束流程实例列表

##### `List<BaseVO> findFinishedTaskInstances(User user, PageParam<BaseVO> page) throws Exception`
- **功能**：各个审批人员查看自己完成的任务
- **参数**：`user` - 用户对象, `page` - 分页参数
- **返回值**：`List<BaseVO>` - 已完成任务列表

##### `List<BaseVO> listRuningVacation(Vacation vacation, PageParam<Object> page) throws Exception`
- **功能**：查看正在运行的请假流程
- **参数**：`vacation` - 请假实体（查询条件）, `page` - 分页参数
- **返回值**：`List<BaseVO>` - 运行中请假流程列表

##### `List<ProcessInstance> listRuningProcess(PageParam<ProcessInstanceEntity> page) throws Exception`
- **功能**：管理运行中流程
- **参数**：`page` - 分页参数
- **返回值**：`List<ProcessInstance>` - 运行中流程实例列表

##### `List<CommentVO> getComments(String processInstanceId) throws Exception`
- **功能**：获取流程实例的审批评论
- **参数**：`processInstanceId` - 流程实例 ID
- **返回值**：`List<CommentVO>` - 评论列表

#### 2.4 流程实例控制类

##### `void activateProcessInstance(String processInstanceId) throws Exception`
- **功能**：激活流程实例
- **参数**：`processInstanceId` - 流程实例 ID
- **核心算法**：调用 `runtimeService.activateProcessInstanceById(processInstanceId)`

##### `void suspendProcessInstance(String processInstanceId) throws Exception`
- **功能**：挂起流程实例
- **参数**：`processInstanceId` - 流程实例 ID
- **核心算法**：调用 `runtimeService.suspendProcessInstanceById(processInstanceId)`

##### `void deleteProcess(String processInstanceId, String deleteReason)`
- **功能**：删除流程
- **参数**：`processInstanceId` - 流程实例 ID, `deleteReason` - 删除原因
- **核心算法**：调用 `runtimeService.deleteProcessInstance(processInstanceId, deleteReason)`

##### `void terminateProcess(String processInstanceId, String terminateReason)`
- **功能**：终止流程
- **参数**：`processInstanceId` - 流程实例 ID, `terminateReason` - 终止原因

#### 2.5 流程图与动态流程类

##### `InputStream getDiagram(String processInstanceId) throws Exception`
- **功能**：显示流程图（带流程跟踪）
- **参数**：`processInstanceId` - 流程实例 ID
- **返回值**：`InputStream` - 流程图图片流

##### `InputStream getDiagramByProInstanceId_noTrace(String resourceType, String processInstanceId) throws Exception`
- **功能**：显示图片 - 通过流程 ID，不带流程跟踪（无乱码问题）
- **参数**：`resourceType` - 资源类型, `processInstanceId` - 流程实例 ID
- **返回值**：`InputStream` - 流程图图片流

##### `InputStream getDiagramByProDefinitionId_noTrace(String resourceType, String processDefinitionId) throws Exception`
- **功能**：显示图片 - 通过部署 ID，不带流程跟踪（无乱码问题）
- **参数**：`resourceType` - 资源类型, `processDefinitionId` - 流程定义 ID
- **返回值**：`InputStream` - 流程图图片流

##### `void addProcessByDynamic() throws Exception`
- **功能**：测试 - 动态创建流程信息
- **核心算法**：通过 `BpmnModel` 动态构建流程定义（StartEvent → UserTask → ExclusiveGateway → EndEvent），并部署到 Activiti 引擎

### 2.6 实现类辅助方法（非接口方法）

> 以下方法定义于 `ProcessService` 实现类，但未声明于 `IProcessService` 接口，仅供内部或其他类直接调用，不属于接口契约。

| 方法签名 | 功能 | 说明 |
|----------|------|------|
| `void jumpTask(TaskEntity currentTaskEntity, String targetTaskDefinitionKey) throws Exception` | 任务跳转（核心实现） | 通过 `WithdrawTaskCmd` 执行跳转，被 `moveTo` 与 `withdrawTask` 内部调用 |
| `void moveForward(TaskEntity currentTaskEntity) throws Exception` | 向前跳转（下一节点） | 计算下一节点 key 并调用 `moveTo` |
| `void moveForward(String currentTaskId) throws Exception` | 向前跳转（重载） | 通过 taskId 查询 TaskEntity 后调用 `moveForward(TaskEntity)` |
| `void moveBack(TaskEntity currentTaskEntity) throws Exception` | 回退跳转（上一节点） | 计算上一节点 key 并调用 `moveTo` |
| `void moveBack(String currentTaskId) throws Exception` | 回退跳转（重载） | 通过 taskId 查询 TaskEntity 后调用 `moveBack(TaskEntity)` |
| `Result canWithdraw(HistoricProcessInstance processInstance, String userId)` | 判断流程能否撤回（重载） | 与接口方法 `canWithdraw(String, String)` 配合使用，被 `withdrawTask` 内部调用 |
| `Result deleteCurrentTaskInstance(String taskId, HistoricTaskInstance taskInstance)` | 删除当前任务实例 | 撤回时删除当前任务并补全签收人，被 `withdrawTask` 内部调用 |

> 说明：上述辅助方法用于支撑 `withdrawTask`、`moveTo` 等接口方法的内部实现。其中 `jumpTask` 通过 `((RuntimeServiceImpl) runtimeService).getCommandExecutor().execute(new WithdrawTaskCmd(...))` 执行跳转命令；`moveForward`/`moveBack` 通过解析 BPMN 流程定义获取相邻节点 `taskDefinitionKey` 后委托给 `moveTo`。

---

## 3. IActUserTaskService — 用户任务配置服务

### 类概述
- 接口：`IActUserTaskService`
- 实现类：`ActUserTaskService`
- 依赖：`ActUserTaskMapper`
- 说明：用于访问统一任务分配配置表。⚠️ **表名差异**：源码 `ActUserTaskMapper.xml` 使用 `t_act_user_task`，数据库实际表名为 `dp_act_unify_task`（详见 [unify-task-table.md](../03-database/unify-task-table.md)）

### 方法列表

#### `List<ActUserTask> selectByProcessDefinitionKey(String processDefinitionKey)`
- **功能**：根据流程定义 Key 查询任务配置
- **事务类型**：无事务
- **参数**：`processDefinitionKey` - 流程定义 Key
- **返回值**：`List<ActUserTask>` - 任务配置列表
- **用途**：被 `UserTaskListener.notify()` 调用，用于动态分配任务办理人

---

## 4. ITraceService — 流程追踪服务

### 类概述
- 接口：`ITraceService`（`com.dp.plat.activiti.service.ITraceService`）
- 实现类：`TraceService`（`@Service("traceService")`）
- 依赖：无（实现类未注入任何 Activiti 服务）
- **重要说明**：当前实现为占位实现，`traceProcess` 方法直接返回 `null`

### 方法列表

#### `List<Map<String, Object>> traceProcess(String processInstanceId)`
- **功能**：流程追踪（设计意图为获取流程实例的执行轨迹）
- **事务类型**：无事务
- **参数**：`processInstanceId` - 流程实例 ID
- **返回值**：`List<Map<String, Object>>` - 流程轨迹列表
- **当前实现**：直接 `return null`，未实现具体逻辑
- **说明**：该接口预留用于流程轨迹追踪功能，目前流程图追踪通过 `ProcessService.getDiagram()` 实现

---

## 5. IVacationService — 请假服务

### 类概述
- 接口：`IVacationService`（`com.dp.plat.activiti.service.IVacationService`）
- 实现类：`VacationService`（`@Service("vacationService")`）
- 继承关系：
  - 接口：`extends IAbstractBaseService<Vacation>`（**注意：继承自 core 模块的 `com.dp.plat.core.service.IAbstractBaseService`**）
  - 实现类：`extends AbstractBaseService<VacationMapper, Vacation> implements IVacationService`
- 依赖：`VacationMapper`（通过基类注入）
- **重要说明**：接口和实现类均为空，仅继承基类 `IAbstractBaseService<Vacation>` 提供的通用 CRUD 方法，**未定义任何业务方法**

> ⚠️ **跨模块基类注意**：PMS-activiti 的 `IVacationService` 继承的是 **core 模块**的 `com.dp.plat.core.service.IAbstractBaseService`（共 11 个方法），**非** PMS-ext-d365 模块的 `com.dp.plat.pms.extend.d365.service.IAbstractBaseService`（仅 8 个方法）。两者接口名相同但全限定名不同、方法集不同，详见 [跨模块冲突检测](#跨模块基类对比)。

### 方法列表

接口未定义任何自定义方法。可用方法均来自 core 模块的 `IAbstractBaseService<Vacation>`（共 11 个方法），包括：
- 通用增删改查方法（8 个）：`deleteByPrimaryKey`、`insert`、`insertSelective`、`selectByPrimaryKey`、`updateByPrimaryKey`、`updateByPrimaryKeySelective`、`countBySelective`、`selectBySelective`
- **分页查询方法（3 个，core 独有，ext-d365 无此方法）**：
  - `long countBySelectivePageable(PageParam<?> pageParam)` — 分页计数
  - `<V> List<V> selectBySelectivePageable(PageParam<?> pageParam)` — 分页查询
  - （具体方法签名见 `com.dp.plat.core.service.IAbstractBaseService`）

> 注：请假业务逻辑（如启动请假流程）由 `ProcessService` 等其他服务通过 `Vacation` 实体和 `VacationMapper` 实现，不在本接口定义。`ProcessService.listRuningVacation` 实际调用了 `vacationService.countBySelectivePageable` 和 `vacationService.selectBySelectivePageable` 分页方法。

---

## 6. IPerformanceService — 绩效服务

### 类概述
- 接口：`IPerformanceService`（`com.dp.plat.activiti.service.IPerformanceService`）
- 实现类：`PerformanceService`（`@Service("performanceService")`）
- 继承关系：
  - 接口：`extends IAbstractBaseService<Performance>`
  - 实现类：`extends AbstractBaseService<PerformanceMapper, Performance> implements IPerformanceService`
- 依赖：`PerformanceMapper`（通过基类注入）
- **重要说明**：接口和实现类均为空，仅继承基类 `IAbstractBaseService<Performance>` 提供的通用 CRUD 方法，**未定义任何业务方法**

### 方法列表

接口未定义任何自定义方法。可用方法均来自基类 `IAbstractBaseService<Performance>`，包括：
- 通用增删改查方法（具体方法签名见 `com.dp.plat.core.service.IAbstractBaseService`）

> 注：绩效业务逻辑由其他服务通过 `Performance` 实体和 `PerformanceMapper` 实现，不在本接口定义。

---

## 7. IRuntimePageService — 运行时页面服务

### 类概述
- 接口：`IRuntimePageService`（`com.dp.plat.activiti.service.IRuntimePageService`）
- 实现类：`RuntimePageService`
- 依赖：`RuntimeService`, `HistoryService`（用于查询 `ACT_HI_ACTINST` 历史活动表）

### 方法列表（共 4 个方法）

#### `String getStartUserId(ProcessInstance processInstance)`
- **功能**：获取流程启动人 ID
- **参数**：`processInstance` - 流程实例
- **返回值**：`String` - 启动用户 ID

#### `String getStartUserId(String taskId)`
- **功能**：根据任务 ID 获取流程启动人
- **参数**：`taskId` - 任务 ID
- **返回值**：`String` - 启动用户 ID
- **核心算法**：通过任务 ID 查询流程实例，再查询启动人

#### `List<ActivityVo> getActivityList(String processInstanceId)`
- **功能**：获取单个流程实例的活动列表（含已执行节点、当前节点、下一步预测节点）
- **事务类型**：无事务（只读）
- **参数**：
  | 参数名 | 类型 | 业务含义 | 校验规则 | 取值范围 |
  |--------|------|----------|----------|----------|
  | processInstanceId | String | 流程实例 ID | 非空 | ACT_RU_EXECUTION.PROC_INST_ID_ 值 |
- **返回值**：`List<ActivityVo>` - 活动列表，按时间升序排列
- **核心算法**（源码 `RuntimePageService.java:89-220`）：
  1. **原生 SQL 查询**：通过 `createNativeHistoricActivityInstanceQuery` 查询 `ACT_HI_ACTINST`，**LEFT JOIN `act_hi_taskinst`** 关联任务表（条件：`ACT_ID_ = TASK_DEF_KEY_` 且 `PROC_INST_ID_` 且 `EXECUTION_ID_` 且 `RES.ID_ + 1 = TSK.ID_`）
     - FIXME 注释：目标承诺驳回时会数据重复，`RES.ID_ + 1 = TSK.ID_` 条件为临时方案
  2. **过滤节点类型**：仅保留 `userTask` 和 `startEvent` 类型（跳过网关、结束事件等）
  3. **填充审批人名称**：
     - startEvent：使用 `startUserId` 查询用户名
     - userTask：已签收用 `assignee` 查询；未签收用 `getCandidateUserNames` 解析候选组/候选人
  4. **设置节点状态**：`endTime != null` → `STATE_DONE`；否则 → `STATE_DOING`；若 approved 为 `STATE_TERMINATE` → `STATE_TERMINATE`
  5. **获取审批结果**：从 Local 变量读取 `isPass`/`APPROVE_RESULT`，从评论读取 `APPROVE_SUGGESTION`
  6. **预测下一步节点**：调用 `findNextActivity` 递归遍历出边，根据条件表达式判断路径
- **边界条件**：
  - `processInstanceId` 不存在：historicActivityInstanceList 为空，返回空列表
  - **流程已结束**：processInstance 为 null，从 historicProcessInstance 获取 startUserId
  - **候选人解析失败**：getCandidateUserNames 内部 catch 异常返回"待定"

#### `List<ActivityVo> getActivityList(Collection<String> processInstanceIdSet)`
- **功能**：批量获取多个流程实例的活动列表
- **事务类型**：无事务（只读）
- **参数**：
  | 参数名 | 类型 | 业务含义 | 校验规则 |
  |--------|------|----------|----------|
  | processInstanceIdSet | Collection<String> | 流程实例 ID 集合 | 非空集合 |
- **返回值**：`List<ActivityVo>` - 活动列表（合并所有实例的活动）
- **核心算法**（源码 `RuntimePageService.java:79-86`）：
  - 遍历集合，对每个 processInstanceId 调用 `getActivityList(String)` 单参数版本
  - 将所有结果合并到一个列表返回
- **边界条件**：
  - 空集合：返回空列表
  - 集合中某个 ID 不存在：不影响其他 ID 的查询结果

---

## 事务规则总结

### ProcessService 事务方法

ProcessService 中涉及状态变更的方法应配置事务（`@Transactional`）：

| 方法 | 事务需求 | 说明 |
|------|----------|------|
| `claim` | 需要 | 签收任务 |
| `unclaim` | 需要 | 取消签收 |
| `delegateTask` | 需要 | 委派任务 |
| `transferTask` | 需要 | 转办任务 |
| `complete` | 需要 | 完成任务 |
| `revoke` | 需要 | 撤销任务（执行 Command） |
| `withdrawTask` | 需要 | 撤回任务（执行 Command） |
| `moveTo` (2 个重载) | 需要 | 任务跳转（执行 Command） |
| `activateProcessInstance` | 需要 | 激活流程实例 |
| `suspendProcessInstance` | 需要 | 挂起流程实例 |
| `deleteProcess` | 需要 | 删除流程 |
| `terminateProcess` | 需要 | 终止流程 |
| `addProcessByDynamic` | 需要 | 动态创建流程 |

### 非事务方法（查询类）

`find*` | `get*` | `select*` | `list*` | `canWithdraw`

---

## 相关文档

- [Controller 方法参考](controller-methods-reference.md) — Controller 层方法
- [自定义命令](custom-commands.md) — Command 模式实现
- [监听器](listeners.md) — 任务监听器机制
- [任务管理](task-management.md) — 任务操作业务说明
- [流程实例管理](process-instance-management.md) — 流程实例操作
- [运行时页面](runtime-page.md) — RuntimePageService 业务说明
