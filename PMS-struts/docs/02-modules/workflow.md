# 工作流管理功能说明文档

## 1. 模块概述

工作流管理模块基于Activiti流程引擎，为PMS系统提供统一的流程驱动能力。该模块封装了流程定义部署、流程实例启动、任务分配与完成、流程变量管理等核心功能，被项目立项、售前审批、转包审批、闭环审批等多个业务模块依赖。同时提供统一待办推送（致远OA集成）、流程跟踪、流程历史查询等功能。

### 涉及的Action类列表

| Action类 | 包路径 | 职责 |
|----------|--------|------|
| `WorkFlowAction` | `com.dp.plat.action` | 流程管理（部署/查看/删除/任务办理/流程图/委派） |
| `WorkSpaceAction` | `com.dp.plat.action` | 工作台（待办/已办任务列表、日常跟踪、通知公告） |

### 涉及的Service类列表

| Service类 | 事务代理Bean | 依赖DAO | 依赖Activiti Service |
|-----------|-------------|---------|---------------------|
| `WorkFlowServiceImpl` | `workFlowServiceAgent` | `WorkflowDao` | `RepositoryService`, `RuntimeService`, `TaskService`, `FormService`, `HistoryService` |
| `WorkSpaceServiceImpl` | `workspaceServiceAgent` | `WorkSpaceDao` | - |

### 涉及的监听器/发送器类列表

| 类名 | 包路径 | 职责 |
|------|--------|------|
| `UnifyTaskListener` | `com.dp.plat.plus.unifytask.listener` | Activiti任务事件监听器，统一待办任务推送入口 |
| `UnifyTask2SeeyonSender` | `com.dp.plat.plus.unifytask.sender` | 致远OA统一待办推送发送器 |

### 涉及的数据库表列表

| 表名 | 说明 |
|------|------|
| `ACT_RE_DEPLOYMENT` | Activiti部署表 |
| `ACT_RE_PROCDEF` | Activiti流程定义表 |
| `ACT_RU_EXECUTION` | Activiti执行实例表 |
| `ACT_RU_TASK` | Activiti运行时任务表 |
| `ACT_RU_VARIABLE` | Activiti运行时变量表 |
| `ACT_HI_PROCINST` | Activiti历史流程实例表 |
| `ACT_HI_TASKINST` | Activiti历史任务表 |
| `ACT_HI_VARINST` | Activiti历史变量表 |
| `ACT_HI_COMMENT` | Activiti审批意见表 |
| `fnd_act_hi_comment` | 自定义审批意见表 |
| `dp_act_unify_task` | 统一待办任务表（致远OA集成） |

### 依赖的其他模块

- 系统管理模块（用户信息、角色权限、基础数据）
- 邮件服务模块（任务通知邮件）
- 致远OA系统（统一待办推送）

## 2. 业务流程

### 2.1 流程引擎核心流程

<<<<<<< HEAD
```mermaid
graph LR
    A["流程定义部署<br/>WorkFlowAction.newdeploy()"] --> B["启动流程实例<br/>业务Action.startProcess()"]
    B --> C["任务分配<br/>Activiti自动分配"]
    C --> D["任务完成<br/>业务Action.completeTask()"]
    D --> E["流程结束<br/>Activiti自动结束"]
=======
```
[流程定义部署] ──> [启动流程实例] ──> [任务分配] ──> [任务完成] ──> [流程结束]
      |                  |              |              |              |
 WorkFlowAction    业务Action      Activiti       业务Action      Activiti
 .newdeploy()      .startProcess()  自动分配        .completeTask()  自动结束
>>>>>>> cfb09fe3c09bfc11415a492e8001c97b140fddf0
```

### 2.2 审批任务处理流程

<<<<<<< HEAD
```mermaid
flowchart TD
    A["待办任务列表"] --> B["WorkSpaceAction.task()"]
    B --> C["点击处理"]
    C --> D["WorkFlowAction.viewTaskForm()"]
    D --> E["打开任务表单<br/>根据formKey跳转到业务Action"]
    E --> F{审批通过?}
    F -->|是| G["WorkFlowService.submitTask()<br/>submitSelfTask()"]
    F -->|否| H["退回"]
    H --> I["修改重提"]
    G --> J["下一节点"]
    J --> K{流程结束?}
    K -->|是| L["更新业务状态"]
    K -->|否| M["等待下一审批人"]
=======
```
[待办任务列表] ──> WorkSpaceAction.task()
      |
[点击处理] ──> WorkFlowAction.viewTaskForm()
      |
[打开任务表单] ──> 根据formKey跳转到业务Action
      |
[审批通过?] ──> WorkFlowService.submitTask() / submitSelfTask()
      |              |
  /        \         |
 是         否       |
 |          |        |
[下一节点]  [退回]    |
 |          |        |
[流程结束?]  [修改重提] |
 /    \              |
是     否             |
|      |             |
[更新业务状态] [等待下一审批人]
>>>>>>> cfb09fe3c09bfc11415a492e8001c97b140fddf0
```

### 2.3 统一待办任务推送流程

<<<<<<< HEAD
```mermaid
flowchart TD
    A["Activiti任务创建/完成事件"] --> B["UnifyTaskListener.notify()"]
    B --> C["解析任务事件"]
    C --> D["UnifyTaskListener.createDelegateTask()"]
    D --> E["生成表单URL<br/>generateFormUrl()"]
    D --> F["获取接收人<br/>getReceiverUser()<br/>getProcessTaskFixedAssignees()"]
    D --> G["解析角色映射<br/>getRoleGroupMap()"]
    E --> H["推送前处理<br/>UnifyTaskListener.beforePush()"]
    F --> H
    G --> H
    H --> I["保存到dp_act_unify_task表<br/>unifyTaskService.insertSelective()"]
    I --> J["推送任务<br/>UnifyTask2SeeyonSender.pushUnifyTask()"]
    J --> K["初始化SeeyonTask<br/>initUnifyTask()"]
    K --> L["调用致远OA REST API<br/>createUnifyTask()<br/>updateUnifyTask()"]
    L --> M["推送后处理<br/>UnifyTaskListener.afterPush()"]
    M --> N["更新dp_act_unify_task表<br/>unifyTaskService.updateByPrimaryKeySelective()"]
=======
```
[Activiti任务创建/完成事件] ──> UnifyTaskListener.notify()
      |
[解析任务事件] ──> UnifyTaskListener.createDelegateTask()
      |               |
      |         [生成表单URL] ──> generateFormUrl()
      |               |
      |         [获取接收人] ──> getReceiverUser() / getProcessTaskFixedAssignees()
      |               |
      |         [解析角色映射] ──> getRoleGroupMap()
      |               |
[推送前处理] ──> UnifyTaskListener.beforePush()
      |               |
      |         [保存到dp_act_unify_task表] ──> unifyTaskService.insertSelective()（来自activiti-api-unifytask-patch JAR）
      |               |
[推送任务] ──> UnifyTask2SeeyonSender.pushUnifyTask()
      |               |
      |         [初始化SeeyonTask] ──> initUnifyTask()
      |               |
      |         [调用致远OA REST API] ──> createUnifyTask() / updateUnifyTask()
      |               |
[推送后处理] ──> UnifyTaskListener.afterPush()
      |               |
      |         [更新dp_act_unify_task表] ──> unifyTaskService.updateByPrimaryKeySelective()（来自activiti-api-unifytask-patch JAR）
>>>>>>> cfb09fe3c09bfc11415a492e8001c97b140fddf0
```

### 2.4 致远OA待办同步流程

<<<<<<< HEAD
```mermaid
flowchart TD
    A["PMS流程任务创建"] --> B["UnifyTaskListener<br/>TASK_CREATE事件"]
    B --> C["解析流程配置<br/>initProcessConfig()<br/>读取sys.unify.task.push.url.config"]
    C --> D["生成表单URL<br/>根据processKey和taskKey<br/>匹配配置中的URL模板"]
    D --> E["获取接收人<br/>根据任务assignee/candidate<br/>解析角色和用户"]
    E --> F["构建SeeyonTask<br/>UnifyTask2SeeyonSender.initUnifyTask()"]
    F --> G["获取OA Token<br/>getToken() → POST /seeyon/rest/token"]
    G --> H["推送待办<br/>POST /seeyon/rest/thirdpartyPending/receive"]
    H --> I["任务完成时更新"]
    I --> J["UnifyTaskListener<br/>TASK_COMPLETE事件"]
    J --> K["更新OA待办状态<br/>POST /seeyon/rest/thirdpartyPending/updatePendingState"]
=======
```
[PMS流程任务创建] ──> UnifyTaskListener(TASK_CREATE事件)
      |
[解析流程配置] ──> initProcessConfig() 读取sys.unify.task.push.url.config
      |
[生成表单URL] ──> 根据processKey和taskKey匹配配置中的URL模板
      |
[获取接收人] ──> 根据任务assignee/candidate解析角色和用户
      |
[构建SeeyonTask] ──> UnifyTask2SeeyonSender.initUnifyTask()
      |
[获取OA Token] ──> getToken() → POST /seeyon/rest/token
      |
[推送待办] ──> POST /seeyon/rest/thirdpartyPending/receive
      |
[任务完成时更新] ──> UnifyTaskListener(TASK_COMPLETE事件)
      |
[更新OA待办状态] ──> POST /seeyon/rest/thirdpartyPending/updatePendingState
>>>>>>> cfb09fe3c09bfc11415a492e8001c97b140fddf0
```

## 3. 接口文档

### 3.1 工作台主页

| 项目 | 说明 |
|------|------|
| URL | /work/Workspace.action |
| HTTP方法 | GET |
| 功能描述 | 工作台主页，展示待办任务、通知公告、快捷入口 |
| 权限要求 | 已登录用户 |

**返回结果**：SUCCESS → /work/workspacelist.jsp

### 3.2 流程部署管理

| 项目 | 说明 |
|------|------|
| URL | /work/WorkFlowAction.action |
| HTTP方法 | GET |
| 功能描述 | 查看流程部署信息和流程定义列表 |
| 权限要求 | 管理员 |

**返回结果**：INPUT → 流程管理页面

**处理逻辑**：
1. 查询部署列表 → `workFlowService.listDeployments()`
2. 查询流程定义列表 → `workFlowService.listProcessDefinition()`

### 3.3 发布流程

| 项目 | 说明 |
|------|------|
| URL | /work/WorkFlowNewDeploy.action |
| HTTP方法 | POST（multipart/form-data） |
| 功能描述 | 部署流程定义文件（ZIP格式） |
| 权限要求 | 管理员 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| file | File | 是 | 非空 | 无 | 流程部署文件（ZIP） |
| filename | String | 是 | - | 无 | 文件名称 |

**返回结果**：SUCCESS → 重定向到WorkFlowAction.action / ERROR → 错误页

### 3.4 删除部署

| 项目 | 说明 |
|------|------|
| URL | /work/WorkFlowDelDeployment.action |
| HTTP方法 | POST |
| 功能描述 | 删除流程部署（级联删除流程定义，不级联删除流程实例） |
| 权限要求 | 管理员 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| param.deploymentId | String | 是 | 非空 | 无 | 部署ID |

**返回结果**：SUCCESS → 重定向到WorkFlowAction.action

### 3.5 查看流程图

| 项目 | 说明 |
|------|------|
| URL | /work/WorkFlowViewImage.action |
| HTTP方法 | GET |
| 功能描述 | 查看流程定义图片 |
| 权限要求 | 已登录用户 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| param.deploymentId | String | 是 | 非空 | 无 | 部署ID |
| param.imageName | String | 是 | 非空 | 无 | 图片资源名称 |

**返回结果**：直接输出图片流（return null）

### 3.6 查看流程定义详情

| 项目 | 说明 |
|------|------|
| URL | /work/viewDeployment.action |
| HTTP方法 | GET |
| 功能描述 | 根据流程定义Key查看部署详情 |
| 权限要求 | 已登录用户 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| procdefKey | String | 是 | 非空 | 无 | 流程定义Key |

**返回结果**：SUCCESS → 流程定义详情页

### 3.7 个人任务列表

| 项目 | 说明 |
|------|------|
| URL | /work/WorkFlowSelfTaskManager.action |
| HTTP方法 | GET |
| 功能描述 | 查询当前用户的待办任务列表 |
| 权限要求 | 已登录用户 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| displayParam | DisplayParam | 否 | - | 默认分页 | 分页参数 |

**返回结果**：SUCCESS → 个人任务列表页

### 3.8 打开任务表单

| 项目 | 说明 |
|------|------|
| URL | /work/WorkFlowViewTaskForm.action |
| HTTP方法 | GET |
| 功能描述 | 根据任务ID打开对应的业务表单 |
| 权限要求 | 已登录用户 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| param.taskId | String | 是 | 非空 | 无 | 任务ID |
| param.canSee | String | 否 | - | 无 | 是否只读 |
| dpActProcDesc.procType | String | 否 | - | 无 | 流程类型 |

**返回结果**：SUCCESS → 表单URL页面

**处理逻辑**：
1. 获取任务表单数据 → `workFlowService.getTaskFromData(taskId)`
2. 获取formKey
3. 获取业务对象ID → `workFlowService.getBusinessObjId(taskId)`（从businessKey的"."后部分提取）
4. 拼接表单URL：formKey + "?param.objId=" + objId + "&param.taskId=" + taskId

### 3.9 提交任务

| 项目 | 说明 |
|------|------|
| URL | /work/WorkFlowSubmitTask.action |
| HTTP方法 | POST |
| 功能描述 | 提交审批任务 |
| 权限要求 | 当前任务审批人 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| param.taskId | String | 是 | 非空 | 无 | 任务ID |
| param.outcome | String | 是 | 非空 | 无 | 审批结果(1=同意, 其他=驳回) |
| param.comment | String | 否 | - | 无 | 审批意见 |
| param.issamecustomer | String | 否 | - | 无 | 是否同一客户 |
| param.needleader | String | 否 | - | 无 | 是否需要领导审批 |

**返回结果**："redirect" → 重定向页面

**处理逻辑**：
1. 解析businessKey获取classType和objId
2. 根据classType和outcome更新业务状态
3. 添加审批意见 → `taskService.addComment()`
4. 完成任务 → `taskService.complete(taskId, vars)`
5. 自动处理同一办理人情况（当前任务和下一任务为同一人时自动办理）
6. 发送邮件通知下一审批人

### 3.10 查看已办任务表单

| 项目 | 说明 |
|------|------|
| URL | /work/WorkFlowHisTaskForm.action |
| HTTP方法 | GET |
| 功能描述 | 查看已办理任务的业务表单 |
| 权限要求 | 已登录用户 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| param.instId | String | 是 | 非空 | 无 | 流程实例ID |

**返回结果**：SUCCESS → 历史任务表单URL页面

### 3.11 查看当前流程图（带高亮）

| 项目 | 说明 |
|------|------|
| URL | /work/WorkFlowViewCurrentImage.action |
| HTTP方法 | GET |
| 功能描述 | 查看当前流程实例的流程图（高亮当前节点） |
| 权限要求 | 已登录用户 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| param.taskId | String | 是 | 非空 | 无 | 任务ID |

**返回结果**："image" → 流程图页面（含坐标信息）

### 3.12 委派任务管理

| URL | 方法 | 说明 |
|-----|------|------|
| /work/ProcDefDelegateList.action | GET | 委派任务规则列表 |
| /work/AddProcDefDelegate.action | GET | 添加委派任务规则 |
| /work/EditProcDefDelegate.action | GET | 编辑委派任务规则 |
| /work/UpdateProcDefDelegate.action | POST | 修改委派任务规则 |

## 4. Service层详解

### 4.1 WorkFlowServiceImpl.startProcess(String, String, Map<String, Object>)

- **功能描述**：启动流程实例
- **核心逻辑**：
  1. 设置当前认证用户 → `Authentication.setAuthenticatedUserId(username)`
  2. 调用`runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, vars)`
  3. 清除认证用户
  4. 返回ProcessInstance
- **调用的Activiti API**：`RuntimeService.startProcessInstanceByKey()`

### 4.2 WorkFlowServiceImpl.submitTask(WorkflowCommonParam)

- **功能描述**：提交审批任务（含业务状态更新和自动流转）
- **核心逻辑**：
  1. 解析businessKey获取classType和objId
  2. 根据classType和outcome更新业务审批状态
  3. 构建DpComment审批意见
  4. 处理任务委派
  5. 循环完成任务：添加审批意见 → 设置变量 → 完成任务
  6. 自动处理同一办理人情况（do-while循环）
  7. 发送邮件通知下一审批人
- **调用的Activiti API**：`TaskService.addComment()`, `TaskService.setVariablesLocal()`, `TaskService.complete()`

### 4.3 WorkFlowServiceImpl.submitSelfTask(WorkflowCommonParam, Map<String, Object>)

- **功能描述**：提交自定义审批任务（业务模块调用）
- **核心逻辑**：
  1. 设置当前认证用户
  2. 添加审批意见 → `taskService.addComment()`
  3. 设置任务变量 → `taskService.setVariablesLocal()`
  4. 完成任务 → `taskService.complete()`
- **调用的Activiti API**：`TaskService.addComment()`, `TaskService.setVariablesLocal()`, `TaskService.complete()`

### 4.4 WorkFlowServiceImpl.submitTaskNoComment(WorkflowCommonParam, Map<String, Object>)

- **功能描述**：提交任务（不添加审批意见）
- **核心逻辑**：设置认证用户 → 完成任务
- **调用的Activiti API**：`TaskService.complete()`

### 4.5 WorkFlowServiceImpl.addSelfActComment(...)

- **功能描述**：添加自定义审批意见到fnd_act_hi_comment表
- **核心逻辑**：调用workflowDao.insertActComment()插入审批意见记录
- **调用的DAO方法**：`workflowDao.insertActComment()`

### 4.6 WorkFlowServiceImpl.getProcessComments(String, String)

- **功能描述**：查询流程审批意见列表
- **核心逻辑**：
  1. 根据taskId或instId查询历史活动实例
  2. 遍历每个历史任务，查询Activiti审批意见
  3. 补充审批人真实姓名
- **调用的Activiti API**：`HistoryService.createHistoricActivityInstanceQuery()`, `TaskService.getTaskComments()`

### 4.7 WorkFlowServiceImpl.assigneeTask(String, String, String)

- **功能描述**：转办任务给其他人
- **核心逻辑**：设置任务owner为原办理人，设置新办理人，设置流程变量
- **调用的Activiti API**：`TaskService.saveTask()`, `TaskService.setAssignee()`, `TaskService.setVariable()`

### 4.8 WorkFlowServiceImpl.setVariable(String, String, String, String)

- **功能描述**：更新流程变量值
- **核心逻辑**：通过workflowDao直接更新ACT_RU_VARIABLE表
- **调用的DAO方法**：`workflowDao.updateRunVariableByInstIdAndVariable()`

### 4.9 其他方法

| 方法 | 功能描述 |
|------|----------|
| deployFlow(String, File) | 部署流程定义（ZIP文件） |
| listDeployments() | 查询部署列表 |
| listProcessDefinition() | 查询流程定义列表 |
| delDeployment(String) | 删除部署（不级联删除流程实例） |
| getInputStream(String, String) | 获取流程定义图片流 |
| findPersonalTask(String) | 查询个人待办任务 |
| findPersonalTask(String, String) | 按流程实例查询个人待办任务 |
| getTaskFromData(String) | 获取任务表单数据 |
| getBusinessObjId(String) | 从businessKey提取业务对象ID |
| getHistBusinessObjId(String) | 从历史流程实例提取业务对象ID |
| getFormKey(String) | 获取历史流程实例的表单Key |
| getProcessDefinitionByTaskId(String) | 根据任务ID获取流程定义 |
| getProcessDefinitionByClassType(String) | 根据流程Key获取最新流程定义 |
| getCurrentActivityCoordinates(String) | 获取当前活动节点坐标（流程图高亮） |
| getTaskIdByProcessInstanceId(String, String) | 按流程实例和办理人查询任务 |
| isExistNextNode(String, String) | 判断是否存在指定名称的下一节点 |
| findAllRunTask() | 查询所有运行中任务 |
| findHisProcess() | 查询历史流程实例 |
| getTaskByInstId(String) | 按流程实例查询任务列表 |
| submitTaskSystemAuto(Task) | 系统自动提交任务 |
| doSelfTask(Task, String, String, Map) | 办理自定义任务 |
| findHistoricPersonalTask(String) | 查询个人已办任务 |
| queryCurrentApprover(String) | 查询当前审批人 |
| queryTaskByBussinessKey(String) | 按businessKey查询任务 |
| queryTaskByBussinessKeyUser(String, String) | 按businessKey和用户查询任务 |
| queryPubTaskByBussinessKeyUser(String, String) | 按businessKey查询候选任务 |
| claimTask(String, String) | 认领候选任务 |
| queryAllSelfTaskList(String) | 查询所有个人任务 |
| queryAllPubTaskList(String) | 查询所有候选任务 |
| queryHisProcessInstanceByIds(Set) | 按ID集合查询历史流程实例 |
| getProcdef(Procdef) | 查询流程定义信息 |
| queryActComment(int, String) | 查询审批意见 |
| updateApplytableInfo(String, String, int, String) | 更新申请表信息 |
| deleteProcessInstance(String, String) | 删除流程实例 |
| getWorkFlowCountMap(Map, List, DpActProcDesc, DisplayParam) | 获取工作流待办数量统计 |

## 5. 数据操作

### 5.1 本模块涉及的数据库表及CRUD操作

| 表名 | CREATE | READ | UPDATE | DELETE |
|------|--------|------|--------|--------|
| ACT_RE_DEPLOYMENT | deployFlow() | listDeployments() | - | delDeployment() |
| ACT_RE_PROCDEF | deployFlow() | listProcessDefinition() / getProcessDefinitionByClassType() | - | - |
| ACT_RU_EXECUTION | startProcess() | getCurrentActivityCoordinates() | - | 流程结束时自动删除 |
| ACT_RU_TASK | startProcess() | findPersonalTask() / getTaskByInstId() / queryCurrentApprover() | assigneeTask() / setVariable() | complete()后自动删除 |
| ACT_RU_VARIABLE | startProcess() / complete() | queryProcessVarMap() | setVariable() / updateRunVariableByInstIdAndVariable | - |
| ACT_HI_PROCINST | startProcess() | findHisProcess() / queryHisProcessInstanceByIds() | - | - |
| ACT_HI_TASKINST | complete() | findHistoricPersonalTask() | - | - |
| ACT_HI_VARINST | startProcess() / complete() | - | - | - |
| ACT_HI_COMMENT | addComment() | getProcessComments() | - | - |
| fnd_act_hi_comment | addSelfActComment() | queryActComment() | updateSelfActComment() | - |
| dp_act_unify_task | unifyTaskService.insertSelective() | - | unifyTaskService.updateByPrimaryKeySelective() / updateBySelective() | - |

### 5.2 数据校验规则

| 数据对象 | 校验字段 | 校验规则 | 错误提示 |
|----------|----------|----------|----------|
| ProcessDefinition | file | 非空、ZIP格式 | 流程定义文件格式不正确 |
| Task | taskId | 非空 | 任务ID不能为空 |
| ProcessInstance | processInstanceId | 非空 | 流程实例ID不能为空 |
| businessKey | 格式 | classType.objId | businessKey格式不正确 |

### 5.3 数据生命周期

| 数据对象 | 创建 | 修改 | 归档 | 删除 |
|----------|------|------|------|------|
| ProcessDefinition | 部署时创建 | - | - | 删除部署时级联删除 |
| ProcessInstance | 启动时创建 | 流转时更新变量 | 结束后移入历史表 | deleteProcessInstance() |
| Task | 自动创建 | 完成时更新 | 完成后移入历史表 | complete()后自动删除 |
| UnifyTask | 任务创建时推送 | 任务完成时更新 | 标记latest=false | 不物理删除 |
### 5.4 数据转换规则

| 转换场景 | 源格式 | 目标格式 | 说明 |
|----------|--------|----------|------|
| businessKey | classType.objId | 解析classType和objId | "."分隔，如"Presales.123" |
| 审批结果 | outcome值 | 1=同意, 其他=驳回 | submitTask()中判断 |
| 角色映射 | 流程变量名 | 角色ID | getRoleGroupMap()映射：cbRole→回访员, emRole→工程管理部, smRole→服务经理等 |
| 统一待办状态 | Activiti事件 | 0=未办理, 1=已办理 | UnifyTask2SeeyonSender.statusMap |
| 统一待办子状态 | 审批结果 | 0=同意已办, 1=不同意已办, 2=取消, 3=驳回 | UnifyTask2SeeyonSender常量 |

## 6. 业务规则

| 规则编号 | 规则描述 | 触发条件 | 执行逻辑 |
|----------|----------|----------|----------|
| WF-001 | 任务自动分配 | 流程流转时 | 根据流程定义的分配策略（assignee/candidateUser/candidateGroup）自动分配任务 |
| WF-002 | 统一待办推送 | Activiti任务创建/完成事件 | UnifyTaskListener监听事件，通过UnifyTask2SeeyonSender推送到致远OA |
| WF-003 | 同一办理人自动流转 | 任务完成后下一任务为同一人时 | do-while循环自动办理，添加"与上环节办理人相同，系统默认办理"意见 |
| WF-004 | 任务完成通知 | 任务完成时 | 查询下一任务办理人，发送邮件通知 |
| WF-005 | 流程退回 | 审批不通过时(outcome!=1) | 流程按定义的退回路径流转到发起人节点 |
| WF-006 | 流程历史查询 | 查看流程跟踪时 | 从Activiti历史表查询HistoricActivityInstance和Comment |
| WF-007 | 流程定义版本管理 | 重复部署时 | Activiti自动创建新版本，运行中的实例使用旧版本 |
| WF-008 | 角色解析 | 统一待办推送时 | 通过getRoleGroupMap()将流程变量中的角色名映射为系统角色ID |
| WF-009 | 部门关联角色 | 角色用户查询时 | 根据dpNo(办事处编码)和角色ID查询对应部门的人员，支持区域权限扩展 |
| WF-010 | 流程配置动态加载 | 任务创建时 | 从sys.unify.task.push.url.config系统参数读取流程URL和任务分配配置 |
| WF-011 | 审批状态自动判定 | 统一待办推送时 | 按approveStatus/isPass/result/flowState/evaluationResult/projectProcessStatus顺序检查变量判定审批结果 |
| WF-012 | 致远OA Token缓存 | 推送待办时 | Token获取后缓存在内存中，避免重复请求 |

## 7. 配置项

| 配置项 | 配置Key | 默认值 | 说明 |
|--------|---------|--------|------|
| 统一待办推送配置 | sys.unify.task.push.config | - | JSON格式，配置发送器类名和参数 |
| 统一待办URL配置 | sys.unify.task.push.url.config | {} | JSON格式，配置各流程的表单URL和任务分配规则 |
| 致远OA目标URL | sys.unify.task.push.config → targetUrl | https://oatest.dptech.com/ | 致远OA系统地址 |
| 致远OA REST用户 | sys.unify.task.push.config → restUser | rest | OA REST接口用户名 |
| 致远OA REST密码 | sys.unify.task.push.config → restPassword | - | OA REST接口密码 |
| 致远OA Token路径 | sys.unify.task.push.config → tokenPath | /seeyon/rest/token | 获取Token的路径 |
| 致远OA创建待办路径 | sys.unify.task.push.config → createPath | /seeyon/rest/thirdpartyPending/receive | 创建待办的路径 |
| 致远OA更新待办路径 | sys.unify.task.push.config → updatePath | /seeyon/rest/thirdpartyPending/updatePendingState | 更新待办状态的路径 |
| 致远OA注册编码 | sys.unify.task.push.config → registerCode | 3006 | PMS在OA的系统注册编码 |
| 致远OA超时时间 | sys.unify.task.push.config → timeout | 30000 | HTTP请求超时时间(ms) |
| 待办标题前缀 | sys.unify.task.push.config → taskTitlePrefix | 【PMS】 | 推送到OA的待办标题前缀 |
| 待办任务ID前缀 | sys.unify.task.push.config → taskPrefix | PMS# | 推送到OA的任务ID前缀 |
| PMS系统URL | sys.unify.task.push.config → originUrl | http://pms.dptech.com | PMS系统访问地址（用于拼接待办URL） |
| 默认表单URL | sys.unify.task.push.config → formUrl | /work/Workspace!task.action | 默认待办跳转URL |
