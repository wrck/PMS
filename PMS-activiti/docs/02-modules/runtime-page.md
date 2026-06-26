# 运行时页面

> 本文档说明 PMS-activiti 模块的运行时页面服务，包括流程图展示、流程明细、活动节点查询与候选人解析。
> 核心类：`RuntimePageService`、`IRuntimePageService`

---

## 1. 功能概述

运行时页面服务负责为前端提供流程运行时的可视化数据，包括：

- **流程明细**：展示流程的所有节点状态、办理人、审批意见
- **候选人解析**：解析节点的办理人/候选人/候选组，显示用户名称
- **下一步节点预测**：根据当前流程变量预测下一步可能经过的节点

---

## 2. RuntimePageService

### 2.1 类信息

- **接口**：`com.dp.plat.activiti.service.IRuntimePageService`
- **实现类**：`com.dp.plat.activiti.service.impl.RuntimePageService`
- **注解**：`@Service("runtimePageService")`
- **依赖**：`HistoryService`、`RuntimeService`、`RepositoryService`、`IdentityService`、`TaskService`

### 2.2 方法列表

| 方法 | 功能 |
|------|------|
| `getActivityList(Collection<String> processInstanceIdSet)` | 批量获取活动列表 |
| `getActivityList(String processInstanceId)` | 获取单个流程的活动列表 |
| `getStartUserId(ProcessInstance processInstance)` | 获取流程启动人 |
| `getStartUserId(String taskId)` | 通过任务 ID 获取启动人 |

---

## 3. 流程明细查询

### 3.1 核心方法

`getActivityList(String processInstanceId)` 是核心方法，返回流程的所有节点信息：

```java
public List<ActivityVo> getActivityList(String processInstanceId) {
    // 1. 查询历史活动节点
    List<HistoricActivityInstance> historicActivityInstanceList = historyService
        .createNativeHistoricActivityInstanceQuery()
        .sql("SELECT CASE WHEN TSK.ID_ IS NULL THEN RES.TASK_ID_ ELSE TSK.ID_ END AS TASK_ID_, "
           + "CASE WHEN TSK.ID_ IS NULL THEN RES.ASSIGNEE_ ELSE TSK.ASSIGNEE_ END AS ASSIGNEE_, "
           + "IFNULL(RES.END_TIME_, TSK.END_TIME_) AS END_TIME_, RES.* "
           + "FROM ACT_HI_ACTINST RES "
           + "LEFT JOIN `act_hi_taskinst` TSK ON RES.`ACT_ID_` = TSK.TASK_DEF_KEY_ "
           + "AND RES.`PROC_INST_ID_` = TSK.PROC_INST_ID_ "
           + "AND RES.`EXECUTION_ID_` = TSK.EXECUTION_ID_ "
           + "AND RES.ID_ + 1 = TSK.ID_ "
           + "WHERE RES.PROC_INST_ID_ = #{procInstId} ORDER BY START_TIME_ ASC ")
        .parameter("procInstId", processInstanceId).list();
    
    // 2. 获取活动节点 ID
    List<String> activeIds = new ArrayList<>();
    ProcessDefinitionEntity processDefinition = null;
    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
        .processInstanceId(processInstanceId).singleResult();
    if (processInstance != null) {
        activeIds = runtimeService.getActiveActivityIds(processInstanceId);
        processDefinition = (ProcessDefinitionEntity) repositoryService
            .getProcessDefinition(processInstance.getProcessDefinitionId());
    }
    
    // 3. 构建活动 VO 列表
    List<ActivityVo> voList = new ArrayList<>();
    for (HistoricActivityInstance hai : historicActivityInstanceList) {
        // 过滤非用户任务
        if (!hai.getActivityType().equals("userTask") && 
            !hai.getActivityType().equals("startEvent")) continue;
        
        ActivityVo vo = new ActivityVo();
        BeanUtils.copyProperties(hai, vo);
        // 解析办理人名称
        // 设置节点状态
        // 获取审批结果和意见
        voList.add(vo);
    }
    
    // 4. 预测下一步节点
    if (processInstance != null) {
        findNextActivity(voList, processInstanceId, curActivity);
    }
    return voList;
}
```

### 3.2 SQL 说明

历史活动查询 SQL 的关键点：

| JOIN 条件 | 说明 |
|-----------|------|
| `RES.ACT_ID_ = TSK.TASK_DEF_KEY_` | 活动节点 = 任务定义键 |
| `RES.PROC_INST_ID_ = TSK.PROC_INST_ID_` | 同一流程实例 |
| `RES.EXECUTION_ID_ = TSK.EXECUTION_ID_` | 同一执行实例 |
| `RES.ID_ + 1 = TSK.ID_` | ID 相邻（解决驳回时数据重复问题） |

> **注意**：源码中标注了 FIXME，该条件可能导致无审批人时 ASSIGNEE 赋值出错，需评估。

### 3.3 节点状态判断

| 状态 | 判断条件 | 数值 |
|------|----------|------|
| 已执行 | `endTime != null` | `0`（`STATE_DONE`） |
| 执行中 | `endTime == null` 且为当前节点 | `1`（`STATE_DOING`） |
| 未执行 | 预测的下一步节点 | `2`（`STATE_TODO`） |
| 已终止 | 审批结果为 `terminate` | `terminate`（`STATE_TERMINATE`） |

---

## 4. 办理人名称解析

### 4.1 解析逻辑

`getCandidateUserNames()` 方法解析节点的办理人名称，支持多种分配方式：

```java
public String getCandidateUserNames(final ActivityImpl activity, 
                                     final String processInstanceId,
                                     final String executionId, 
                                     final String taskId) {
    return ((RuntimeServiceImpl) runtimeService).getCommandExecutor()
        .execute(new Command<String>() {
            @Override
            public String execute(CommandContext commandContext) {
                // 1. 获取执行实例
                ExecutionEntity execution = ...;
                TaskDefinition taskDefinition = (TaskDefinition) 
                    activity.getProperties().get("taskDefinition");
                
                // 2. 解析 assignee（办理人）
                if (taskDefinition.getAssigneeExpression() != null) {
                    String assignee = (String) taskDefinition.getAssigneeExpression()
                        .getValue(execution);
                    return getUserNamesByUserIds(assignee);
                }
                
                // 3. 解析 owner（委托人）
                if (taskDefinition.getOwnerExpression() != null) {
                    // ...
                }
                
                // 4. 解析候选组
                if (!taskDefinition.getCandidateGroupIdExpressions().isEmpty()) {
                    // 解析组 ID，查询组内用户
                    return getUserNamesByGroupIds(groupIds);
                }
                
                // 5. 解析候选人
                if (!taskDefinition.getCandidateUserIdExpressions().isEmpty()) {
                    // 解析候选人 ID
                    return getUserNamesByUserIds(userIds);
                }
                
                // 6. 从任务实例获取
                if (execution != null) {
                    List<TaskEntity> tasks = execution.getTasks();
                    // 从 IdentityLink 获取候选组
                }
                
                // 7. 从历史身份链接获取
                if (taskId != null) {
                    List<HistoricIdentityLink> links = historyService
                        .getHistoricIdentityLinksForTask(taskId);
                    // ...
                }
                return "待定";
            }
        });
}
```

### 4.2 分配方式优先级

| 优先级 | 分配方式 | 字段 | 说明 |
|--------|----------|------|------|
| 1 | 指定办理人 | `assigneeExpression` | `${userId}` |
| 2 | 委托人 | `ownerExpression` | 显示为"办理人（委托人: xxx）" |
| 3 | 候选组 | `candidateGroupIdExpressions` | `${groupId}` |
| 4 | 候选人 | `candidateUserIdExpressions` | `${userId}` |
| 5 | 任务实例 | `IdentityLink` | 从运行时任务获取 |
| 6 | 历史身份链接 | `HistoricIdentityLink` | 从历史任务获取 |

### 4.3 用户名格式

```java
private String getUserNamesByUserIds(String userId) {
    User user = identityService.createUserQuery().userId(userId).singleResult();
    if (user != null) {
        List<String> names = new ArrayList<>(2);
        if (StringUtils.isNotBlank(user.getLastName())) {
            names.add(user.getLastName());
        }
        if (StringUtils.isNotBlank(user.getFirstName())) {
            names.add(user.getFirstName());
        }
        return StringUtils.join(names, "-");  // 格式：姓-名
    }
    return userId;
}
```

---

## 5. 下一步节点预测

### 5.1 预测逻辑

`findNextActivity()` 方法从当前节点出发，预测下一步可能经过的节点：

```java
public void findNextActivity(List<ActivityVo> voList, String processInstanceId, 
                              PvmActivity curActivity) {
    List<PvmTransition> nextTrans = curActivity.getOutgoingTransitions();
    for (PvmTransition nextTran : nextTrans) {
        Object flowName = nextTran.getProperty("name");
        PvmActivity activity = nextTran.getDestination();
        
        if ("userTask".equals(activity.getProperty("type").toString())) {
            // 判断连线名称
            if (flowName != null && isInApprovedText(Constants.APPROVED_PASSED, flowName.toString())) {
                // 同意路径
                addNextActivityVo(voList, processInstanceId, activity);
                findNextActivity(voList, processInstanceId, activity);  // 递归
            } else if (flowName == null || !isInApprovedText(Constants.APPROVED_REJECT, flowName.toString())) {
                // 条件路径：评估表达式
                Object conditionText = nextTran.getProperty("conditionText");
                if (conditionText != null) {
                    boolean targetTask = isTargetTask(conditionText.toString(), processInstanceId, nextTran);
                    if (targetTask) {
                        addNextActivityVo(voList, processInstanceId, activity);
                        findNextActivity(voList, processInstanceId, activity);
                    }
                }
            }
        } else {
            // 非用户任务（网关等），继续递归
            findNextActivity(voList, processInstanceId, activity);
        }
    }
}
```

### 5.2 连线判断规则

| 连线名称 | 判断 | 说明 |
|----------|------|------|
| 包含"同意/通过/批准/提交/保存/暂存" | 同意路径 | 添加为下一步节点 |
| 包含"拒绝/不通过/不同意/退回/整改/终止" | 拒绝路径 | 不添加 |
| 无名称或条件表达式 | 评估表达式 | 动态判断 |

### 5.3 条件表达式评估

```java
public boolean isTargetTask(final String expressionText, String processInstanceId, 
                            final PvmTransition transition) {
    final ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery()
        .executionId(processInstanceId).singleResult();
    Boolean result = ((RuntimeServiceImpl) runtimeService).getCommandExecutor()
        .execute(new Command<Boolean>() {
            @Override
            public Boolean execute(CommandContext commandContext) {
                UelExpressionCondition flowCondition = 
                    (UelExpressionCondition) transition.getProperty("condition");
                return flowCondition.evaluate(transition.getId(), execution);
            }
        });
    return result;
}
```

---

## 6. 审批结果与意见获取

### 6.1 获取逻辑

`getApproveMap()` 方法获取节点的审批结果和意见：

```java
public Map<String, String> getApproveMap(HistoricActivityInstance activityInstance) {
    Map<String, String> map = new HashMap<>();
    if (StringUtils.isEmpty(activityInstance.getTaskId())) return map;
    
    // 1. 查询任务局部变量
    List<HistoricVariableInstance> variableInstances = historyService
        .createHistoricVariableInstanceQuery()
        .processInstanceId(activityInstance.getProcessInstanceId())
        .taskId(activityInstance.getTaskId()).list();
    
    for (HistoricVariableInstance var : variableInstances) {
        if (var.getVariableName().equals("isPass") || 
            Constants.APPROVE_RESULT.equals(var.getVariableName())) {
            map.put(Constants.APPROVE_RESULT, var.getValue().toString());
        }
    }
    
    // 2. 查询审批意见（评论）
    List<Comment> comments = taskService.getTaskComments(
        activityInstance.getTaskId(), Constants.COMMENT_TYPE_COMMENT);
    List<String> suggestions = new ArrayList<>();
    for (Comment comment : comments) {
        suggestions.add(comment.getFullMessage());
    }
    map.put(Constants.APPROVE_SUGGESTION, StringUtils.join(suggestions, "<br>"));
    return map;
}
```

### 6.2 审批结果变量

| 变量名 | 含义 | 值 |
|--------|------|----|
| `isPass` | 是否通过 | `true`/`false` |
| `approved` | 审批结果 | `1`（通过）/`0`（拒绝）/`terminate`（终止） |
| `suggestion` | 审批意见 | 文本内容 |

---

## 7. 流程启动人获取

### 7.1 从流程实例获取

```java
public String getStartUserId(ProcessInstance processInstance) {
    ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) 
        repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());
    // 获取 initiator 变量名（通常为 startUserId）
    String initiator = (String) processDefinition.getProperty(
        BpmnParse.PROPERTYNAME_INITIATOR_VARIABLE_NAME);
    if (initiator != null) {
        return (String) runtimeService.getVariable(
            processInstance.getProcessInstanceId(), initiator);
    }
    return null;
}
```

### 7.2 从任务获取

```java
public String getStartUserId(String taskId) {
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()
        .processInstanceId(task.getProcessInstanceId()).singleResult();
    return hpi.getStartUserId();
}
```

---

## 8. 相关文档

- [流程实例管理](process-instance-management.md) — 流程实例管理
- [任务管理](task-management.md) — 任务查询与审批
- [BPMN 流程定义详解](bpmn-processes.md) — 流程节点与流转条件
- [../03-database/complete-data-dictionary.md](../03-database/complete-data-dictionary.md) — 历史表字段
- [controller-methods-reference.md](controller-methods-reference.md) — Controller 方法参考
