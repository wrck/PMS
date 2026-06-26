# PMS-activiti 工作流引擎模块完整文档

> 本文档深度分析 PMS-activiti 工作流引擎模块的所有 Controller、Service、Command、Listener 类。

---

## 1. 模块概述

PMS-activiti 是 PMS 系统的工作流引擎模块，基于 Activiti 5.23.0 实现。

### 涉及的 Controller 类列表

| Controller 类 | 职责 |
|---------------|------|
| `ProcessDefinitionController` | 流程定义管理 |
| `ProcessInstanceController` | 流程实例管理 |
| `TaskController` | 任务管理 |
| `ModelController` | 模型设计 |
| `WorkFlowSubModalController` | 工作流子模态框 |

### 涉及的 Service 类列表

| Service 接口 | 实现类 | 职责 |
|-------------|--------|------|
| `IWorkflowService` | `WorkflowService` | 工作流核心服务 |
| `IProcessService` | `ProcessService` | 流程管理服务 |
| `IActUserTaskService` | `ActUserTaskService` | 用户任务服务 |
| `ITraceService` | `TraceService` | 追踪服务 |
| `IVacationService` | `VacationService` | 请假服务 |
| `IPerformanceService` | `PerformanceService` | 绩效服务 |
| `IRuntimePageService` | `RuntimePageService` | 运行时页面服务 |

### 涉及的命令类列表

| Command 类 | 职责 |
|------------|------|
| `WithdrawTaskCmd` | 任务撤回 |
| `RevokeTaskCmd` | 任务驳回 |
| `JumpTaskCmdService` | 任务跳转 |
| `DeleteActiveTaskCmd` | 删除活动任务 |
| `StartActivityCmd` | 启动活动 |

### 涉及的监听器类列表

| Listener 类 | 职责 |
|------------|------|
| `UserTaskListener` | 用户任务监听器 |
| `AfterModifyApplyProcessor` | 修改申请后处理器 |

---

## 2. Controller 方法详细说明

### 2.1 ProcessDefinitionController

#### `list(Model model)`
- **URL**: `/processDefinition/list`
- **HTTP 方法**: GET
- **功能**: 流程定义列表
- **返回值**: 视图名称

#### `deploy(MultipartFile file)`
- **URL**: `/processDefinition/deploy`
- **HTTP 方法**: POST
- **功能**: 部署流程定义
- **参数**: `file` - BPMN 文件
- **返回值**: JSON 结果

#### `delete(String deploymentId)`
- **URL**: `/processDefinition/delete`
- **HTTP 方法**: POST
- **功能**: 删除流程定义
- **参数**: `deploymentId` - 部署ID
- **返回值**: JSON 结果

### 2.2 TaskController

#### `myTask(Model model)`
- **URL**: `/task/myTask`
- **HTTP 方法**: GET
- **功能**: 我的任务列表
- **返回值**: 视图名称

#### `complete(String taskId, String action, Model model)`
- **URL**: `/task/complete`
- **HTTP 方法**: POST
- **功能**: 完成任务
- **参数**: `taskId` - 任务ID, `action` - 操作
- **返回值**: JSON 结果

#### `claim(String taskId)`
- **URL**: `/task/claim`
- **HTTP 方法**: POST
- **功能**: 签收任务
- **参数**: `taskId` - 任务ID
- **返回值**: JSON 结果

#### `withdraw(String instanceId)`
- **URL**: `/task/withdraw/{instanceId}`
- **HTTP 方法**: POST
- **功能**: 撤回任务
- **参数**: `instanceId` - 流程实例ID
- **返回值**: JSON 结果

#### `revoke(String taskId, String comment)`
- **URL**: `/task/revoke`
- **HTTP 方法**: POST
- **功能**: 驳回任务
- **参数**: `taskId` - 任务ID, `comment` - 驳回意见
- **返回值**: JSON 结果

---

## 3. 命令类详细说明

### 3.1 WithdrawTaskCmd

```java
public class WithdrawTaskCmd implements Command<Void> {
    private String taskId;
    private String userId;
    
    @Override
    public Void execute(CommandContext commandContext) {
        TaskEntity task = commandContext.getTaskEntityManager()
            .findTaskById(taskId);
        
        if (!task.getAssignee().equals(userId)) {
            throw new BusinessException("只有任务处理人才能撤回任务");
        }
        
        task.setAssignee(null);
        commandContext.getTaskEntityManager().saveTask(task);
        
        return null;
    }
}
```

### 3.2 RevokeTaskCmd

```java
public class RevokeTaskCmd implements Command<Void> {
    private String taskId;
    private String userId;
    private String comment;
    
    @Override
    public Void execute(CommandContext commandContext) {
        TaskEntity task = commandContext.getTaskEntityManager()
            .findTaskById(taskId);
        
        CommentEntity commentEntity = commandContext.getCommentEntityManager()
            .newComment();
        commentEntity.setMessage(comment);
        commentEntity.setTaskId(taskId);
        commandContext.getCommentEntityManager().insertComment(commentEntity);
        
        ExecutionEntity execution = commandContext.getExecutionEntityManager()
            .findExecutionById(task.getExecutionId());
        execution.setActivityId(getPreviousActivityId(execution));
        
        return null;
    }
}
```

### 3.3 JumpTaskCmdService

```java
public class JumpTaskCmdService implements Command<Void> {
    private String taskId;
    private String targetActivityId;
    private String userId;
    
    @Override
    public Void execute(CommandContext commandContext) {
        TaskEntity task = commandContext.getTaskEntityManager()
            .findTaskById(taskId);
        
        ExecutionEntity execution = commandContext.getExecutionEntityManager()
            .findExecutionById(task.getExecutionId());
        execution.setActivityId(targetActivityId);
        
        commandContext.getTaskEntityManager().deleteTask(task, false);
        
        return null;
    }
}
```

---

## 4. 监听器详细说明

### 4.1 UserTaskListener

```java
public class UserTaskListener implements TaskListener {
    
    @Override
    public void notify(DelegateTask delegateTask) {
        String eventName = delegateTask.getEventName();
        
        if (TaskListener.EVENTNAME_CREATE.equals(eventName)) {
            handleTaskCreate(delegateTask);
        } else if (TaskListener.EVENTNAME_COMPLETE.equals(eventName)) {
            handleTaskComplete(delegateTask);
        }
    }
    
    private void handleTaskCreate(DelegateTask delegateTask) {
        delegateTask.setVariable("taskCreateTime", new Date());
    }
    
    private void handleTaskComplete(DelegateTask delegateTask) {
        delegateTask.setVariable("taskCompleteTime", new Date());
    }
}
```

---

## 5. 数据库表详细说明

### 5.1 ACT_RE_DEPLOYMENT（部署信息表）

| 字段名 | 类型 | 约束 | 业务含义 |
|--------|------|------|----------|
| `ID_` | VARCHAR(64) | PK | 部署ID |
| `NAME_` | VARCHAR(255) | - | 部署名称 |
| `CATEGORY_` | VARCHAR(255) | - | 分类 |
| `KEY_` | VARCHAR(255) | - | 部署键 |
| `DEPLOY_TIME_` | TIMESTAMP | - | 部署时间 |
| `ENGINE_VERSION_` | VARCHAR(255) | - | 引擎版本 |

### 5.2 ACT_RU_TASK（运行时任务表）

| 字段名 | 类型 | 约束 | 业务含义 |
|--------|------|------|----------|
| `ID_` | VARCHAR(64) | PK | 任务ID |
| `EXECUTION_ID_` | VARCHAR(64) | - | 执行实例ID |
| `PROC_INSTANCE_ID_` | VARCHAR(64) | - | 流程实例ID |
| `PROC_DEF_ID_` | VARCHAR(64) | - | 流程定义ID |
| `NAME_` | VARCHAR(255) | - | 任务名称 |
| `OWNER_` | VARCHAR(255) | - | 任务拥有者 |
| `ASSIGNEE_` | VARCHAR(255) | - | 任务 assignee |
| `PRIORITY_` | INT | - | 优先级 |
| `CREATE_TIME_` | TIMESTAMP | - | 创建时间 |
| `DUE_DATE_` | TIMESTAMP | - | 截止时间 |

---

## 6. 配置说明

### 6.1 Activiti 引擎配置

```xml
<bean id="processEngineConfiguration" 
      class="org.activiti.spring.SpringProcessEngineConfiguration">
    <property name="dataSource" ref="dataSource"/>
    <property name="transactionManager" ref="transactionManager"/>
    <property name="databaseSchema" value="ACT"/>
    <property name="databaseSchemaUpdate" value="true"/>
    <property name="jobExecutorActivate" value="true"/>
</bean>
```

### 6.2 服务 Bean 配置

```xml
<bean id="repositoryService" factory-bean="processEngine" factory-method="getRepositoryService"/>
<bean id="runtimeService" factory-bean="processEngine" factory-method="getRuntimeService"/>
<bean id="taskService" factory-bean="processEngine" factory-method="getTaskService"/>
<bean id="historyService" factory-bean="processEngine" factory-method="getHistoryService"/>
<bean id="managementService" factory-bean="processEngine" factory-method="getManagementService"/>
```
