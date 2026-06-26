# PMS-activiti 数据流向图

> 本文档详细描述 PMS-activiti 模块在各业务场景下的数据流向，包括流程部署、流程启动、任务办理、任务撤回、任务跳转等核心场景。
> 数据来源：`d:\常规软件\QoderCode\workspace\PMS\PMS-activiti\src\main\java\com\dp\plat\activiti\` 源码。

---

## 1. 数据流向概览

PMS-activiti 的数据流向围绕 Activiti 7 类 Service 展开，所有数据最终落地到 Activiti 引擎表与自定义业务表 `dp_act_unify_task`。

```mermaid
flowchart TB
    subgraph 表现层
        CTL[Controller 层]
    end

    subgraph 业务层
        SVC[ProcessService / WorkflowService / RuntimePageService]
    end

    subgraph Activiti 服务层
        RS[RepositoryService]
        RTS[RuntimeService]
        TS[TaskService]
        HS[HistoryService]
        MS[ManagementService]
        IS[IdentityService]
        FS[FormService]
    end

    subgraph 自定义命令
        CMD[RevokeTaskCmd / WithdrawTaskCmd / JumpTaskCmdService]
    end

    subgraph 监听器
        LST[UserTaskListener]
    end

    subgraph 数据层
        RE[ACT_RE_* 存储库表]
        RU[ACT_RU_* 运行时表]
        HI[ACT_HI_* 历史表]
        ID[ACT_ID_* 身份表]
        GE[ACT_GE_* 通用表]
        UT[dp_act_unify_task 统一任务表]
    end

    CTL --> SVC
    SVC --> RS & RTS & TS & HS & MS
    SVC --> CMD
    RTS & TS --> CMD
    RS --> RE
    RTS --> RU
    TS --> RU
    HS --> HI
    MS --> RU
    IS --> ID
    RS --> GE
    RTS --> LST
    LST --> UT
    RU -->|完成归档| HI
```

---

## 2. 流程部署数据流

### 2.1 BPMN 文件部署

**触发入口**：`ProcessDefinitionController.deploy()` / `ModelController.deploy()`

```mermaid
sequenceDiagram
    participant U as 用户
    participant C as ProcessDefinitionController
    participant RS as RepositoryService
    participant DB as 数据库

    U->>C: 上传 BPMN 文件
    C->>RS: createDeployment().addInputStream().deploy()
    RS->>DB: INSERT INTO ACT_RE_DEPLOYMENT
    RS->>DB: INSERT INTO ACT_RE_PROCDEF (解析 BPMN)
    RS->>DB: INSERT INTO ACT_GE_BYTEARRAY (BPMN 内容)
    RS->>DB: INSERT INTO ACT_GE_BYTEARRAY (流程图 PNG)
    RS-->>C: 返回 Deployment 对象
    C-->>U: 部署成功
```

**涉及表写入**：

| 表名 | 操作 | 字段 | 说明 |
|------|------|------|------|
| `ACT_RE_DEPLOYMENT` | INSERT | `ID_`, `NAME_`, `DEPLOY_TIME_` | 部署记录 |
| `ACT_RE_PROCDEF` | INSERT | `ID_`, `KEY_`, `VERSION_`, `DEPLOYMENT_ID_` | 流程定义 |
| `ACT_GE_BYTEARRAY` | INSERT | `NAME_`, `BYTES_`, `DEPLOYMENT_ID_` | BPMN XML + PNG |
| `ACT_GE_PROPERTY` | UPDATE | `VALUE_` (next.dbid) | 主键生成器 |

### 2.2 模型部署

**触发入口**：`ModelController.deploy()`

```mermaid
sequenceDiagram
    participant U as 用户
    participant C as ModelController
    participant RS as RepositoryService
    participant DB as 数据库

    U->>C: 点击部署按钮（modelId）
    C->>RS: getModel(modelId) 获取模型 JSON
    C->>C: 将 JSON 转换为 BPMN XML
    C->>RS: createDeployment().addString().deploy()
    RS->>DB: INSERT INTO ACT_RE_DEPLOYMENT
    RS->>DB: INSERT INTO ACT_RE_PROCDEF
    RS->>DB: INSERT INTO ACT_GE_BYTEARRAY
    C->>DB: UPDATE ACT_RE_MODEL (部署时间)
    C-->>U: 部署成功
```

---

## 3. 流程启动数据流

### 3.1 启动流程实例

**触发入口**：业务模块调用 `runtimeService.startProcessInstanceByKey()`

```mermaid
sequenceDiagram
    participant BIZ as 业务模块
    participant RTS as RuntimeService
    participant LST as UserTaskListener
    participant UTS as ActUserTaskService
    participant DB as 数据库

    BIZ->>RTS: startProcessInstanceByKey(key, businessKey, variables)
    RTS->>DB: SELECT ACT_RE_PROCDEF WHERE KEY_=key (latest version)
    RTS->>DB: INSERT INTO ACT_RU_EXECUTION (流程实例)
    RTS->>DB: INSERT INTO ACT_HI_PROCINST (历史实例)
    RTS->>DB: INSERT INTO ACT_RU_VARIABLE (流程变量 entity)
    RTS->>DB: INSERT INTO ACT_HI_VARINST (历史变量)
    Note over RTS: 创建第一个 userTask
    RTS->>DB: INSERT INTO ACT_RU_TASK
    RTS->>DB: INSERT INTO ACT_HI_TASKINST
    RTS->>DB: INSERT INTO ACT_HI_ACTINST (开始事件 + 任务节点)
    RTS->>LST: 触发 TaskListener create 事件
    LST->>UTS: selectByProcessDefinitionKey(key)
    UTS->>DB: SELECT FROM dp_act_unify_task WHERE PROC_DEF_KEY=?
    DB-->>UTS: 返回 List<ActUserTask>
    UTS-->>LST: 返回配置
    LST->>LST: 匹配 taskDefKey，根据 taskType 分配
    LST->>DB: UPDATE ACT_RU_TASK SET ASSIGNEE_=?
    LST->>DB: INSERT INTO ACT_RU_IDENTITYLINK (候选人)
    RTS-->>BIZ: 返回 ProcessInstance
```

**涉及表写入**：

| 表名 | 操作 | 说明 |
|------|------|------|
| `ACT_RU_EXECUTION` | INSERT | 创建流程实例与根执行 |
| `ACT_RU_TASK` | INSERT | 创建第一个任务 |
| `ACT_RU_VARIABLE` | INSERT | 写入流程变量（含 `entity`） |
| `ACT_RU_IDENTITYLINK` | INSERT | 任务候选人/办理人 |
| `ACT_HI_PROCINST` | INSERT | 历史流程实例 |
| `ACT_HI_TASKINST` | INSERT | 历史任务 |
| `ACT_HI_ACTINST` | INSERT | 历史活动（开始事件 + 任务） |
| `ACT_HI_VARINST` | INSERT | 历史变量 |
| `dp_act_unify_task` | SELECT | 读取任务分配配置 |

---

## 4. 任务办理数据流

### 4.1 完成任务（complete）

**触发入口**：`TaskController.complete()` → `ProcessService.complete()`

```mermaid
sequenceDiagram
    participant U as 用户
    participant C as TaskController
    participant SVC as ProcessService
    participant TS as TaskService
    participant LST as UserTaskListener
    participant DB as 数据库

    U->>C: 提交审批意见（taskId, result, comment）
    C->>SVC: complete(taskId, variables, comment)
    SVC->>TS: addComment(taskId, processInstanceId, comment)
    TS->>DB: INSERT INTO ACT_HI_COMMENT
    SVC->>TS: complete(taskId, variables)
    TS->>DB: UPDATE ACT_RU_VARIABLE (写入 result 变量)
    TS->>DB: UPDATE ACT_HI_VARINST
    TS->>DB: DELETE FROM ACT_RU_TASK WHERE ID_=taskId
    TS->>DB: UPDATE ACT_HI_TASKINST SET END_TIME_, DURATION_
    TS->>DB: UPDATE ACT_HI_ACTINST SET END_TIME_ (当前节点)
    Note over TS: 流程引擎根据网关条件流转
    TS->>DB: INSERT INTO ACT_RU_TASK (下一任务)
    TS->>DB: INSERT INTO ACT_HI_TASKINST
    TS->>DB: INSERT INTO ACT_HI_ACTINST (下一节点)
    TS->>LST: 触发下一任务 create 事件
    LST->>DB: SELECT dp_act_unify_task
    LST->>DB: UPDATE ACT_RU_TASK SET ASSIGNEE_
    TS-->>SVC: 完成
    SVC-->>C: 返回
    C-->>U: 成功
```

**涉及表操作**：

| 表名 | 操作 | 说明 |
|------|------|------|
| `ACT_HI_COMMENT` | INSERT | 审批意见 |
| `ACT_RU_VARIABLE` | UPDATE | 写入 `result` 变量 |
| `ACT_RU_TASK` | DELETE + INSERT | 删除当前任务，创建下一任务 |
| `ACT_HI_TASKINST` | UPDATE + INSERT | 当前任务归档，下一任务历史记录 |
| `ACT_HI_ACTINST` | UPDATE + INSERT | 当前活动归档，下一活动记录 |
| `ACT_RU_IDENTITYLINK` | DELETE + INSERT | 当前任务身份删除，下一任务身份创建 |
| `dp_act_unify_task` | SELECT | 读取下一任务分配配置 |

### 4.2 签收任务（claim）

**触发入口**：`TaskController.claim()` → `ProcessService.claim()`

```mermaid
sequenceDiagram
    participant U as 用户
    participant C as TaskController
    participant SVC as ProcessService
    participant TS as TaskService
    participant DB as 数据库

    U->>C: 签收任务（taskId, userId）
    C->>SVC: claim(taskId, userId)
    SVC->>TS: claim(taskId, userId)
    TS->>DB: UPDATE ACT_RU_TASK SET ASSIGNEE_=userId, OWNER_=userId
    TS->>DB: DELETE FROM ACT_RU_IDENTITYLINK WHERE TASK_ID_=taskId AND TYPE_=candidate
    TS-->>SVC: 完成
    SVC-->>C: 返回
    C-->>U: 签收成功
```

### 4.3 委托任务（delegate）

**触发入口**：`TaskController.delegate()` → `ProcessService.delegate()`

```mermaid
sequenceDiagram
    participant U as 用户
    participant C as TaskController
    participant SVC as ProcessService
    participant TS as TaskService
    participant DB as 数据库

    U->>C: 委托任务（taskId, toUserId）
    C->>SVC: delegate(taskId, toUserId)
    SVC->>TS: delegateTask(taskId, toUserId)
    TS->>DB: UPDATE ACT_RU_TASK SET OWNER_=当前用户, ASSIGNEE_=toUserId, DELEGATION_=PENDING
    TS-->>SVC: 完成
    SVC-->>C: 返回
    C-->>U: 委托成功
```

### 4.4 转办任务（transfer）

**触发入口**：`TaskController.transfer()` → `ProcessService.transfer()`

```mermaid
sequenceDiagram
    participant U as 用户
    participant C as TaskController
    participant SVC as ProcessService
    participant TS as TaskService
    participant DB as 数据库

    U->>C: 转办任务（taskId, toUserId）
    C->>SVC: transfer(taskId, toUserId)
    SVC->>TS: setAssignee(taskId, toUserId)
    TS->>DB: UPDATE ACT_RU_TASK SET ASSIGNEE_=toUserId, OWNER_=当前用户
    TS->>DB: INSERT INTO ACT_RU_IDENTITYLINK (TYPE_=participant, USER_ID_=toUserId)
    TS-->>SVC: 完成
    SVC-->>C: 返回
    C-->>U: 转办成功
```

---

## 5. 任务撤回数据流

### 5.1 撤回任务（revoke）

**触发入口**：`TaskController.revoke()` → `ProcessService.revoke()` → `RevokeTaskCmd`

撤回是指**当前办理人**从已办理的任务中撤回，让流程回到上一节点。

```mermaid
sequenceDiagram
    participant U as 用户
    participant C as TaskController
    participant SVC as ProcessService
    participant MS as ManagementService
    participant CMD as RevokeTaskCmd
    participant DB as 数据库

    U->>C: 撤回任务（processInstanceId）
    C->>SVC: revoke(processInstanceId)
    SVC->>MS: executeCommand(new RevokeTaskCmd(...))
    MS->>CMD: execute(commandContext)
    CMD->>DB: SELECT ACT_HI_TASKINST WHERE PROC_INSTANCE_ID_=? ORDER BY END_TIME_ DESC
    CMD->>CMD: 获取当前任务与上一任务
    CMD->>CMD: 判断撤回条件
    alt 上一任务已结束 AND 当前任务未结束
        CMD->>DB: DELETE FROM ACT_RU_TASK (当前任务)
        CMD->>DB: DELETE FROM ACT_RU_IDENTITYLINK (当前任务身份)
        CMD->>DB: UPDATE ACT_HI_TASKINST SET DELETE_REASON_='撤回'
        CMD->>DB: INSERT INTO ACT_RU_TASK (恢复上一任务)
        CMD->>DB: INSERT INTO ACT_RU_IDENTITYLINK
        CMD->>DB: UPDATE ACT_RU_EXECUTION SET ACT_ID_=上一节点
        CMD-->>MS: 返回 1（成功）
    else 不满足撤回条件
        CMD-->>MS: 返回 0 或 2（失败）
    end
    MS-->>SVC: 返回结果
    SVC-->>C: 返回
    C-->>U: 撤回结果
```

**RevokeTaskCmd 返回值**：

| 返回值 | 含义 | 说明 |
|--------|------|------|
| 0 | 撤回失败 | 上一任务不存在或当前任务已结束 |
| 1 | 撤回成功 | 已恢复上一任务 |
| 2 | 撤回失败 | 不满足撤回条件（如已无下一任务） |

### 5.2 撤销任务（withdraw）

**触发入口**：`TaskController.withdraw()` → `ProcessService.withdrawTask()` → `WithdrawTaskCmd`

撤销是指**上一节点办理人**从当前办理人处撤销任务，让流程回到自己。

```mermaid
sequenceDiagram
    participant U as 用户
    participant C as TaskController
    participant SVC as ProcessService
    participant MS as ManagementService
    participant CMD as WithdrawTaskCmd
    participant DB as 数据库

    U->>C: 撤销任务（processInstanceId, currentActivityId）
    C->>SVC: withdrawTask(processInstanceId, currentActivityId)
    SVC->>MS: executeCommand(new WithdrawTaskCmd(...))
    MS->>CMD: execute(commandContext)
    CMD->>DB: SELECT ACT_RU_TASK WHERE PROC_INSTANCE_ID_=?
    CMD->>DB: SELECT ACT_HI_TASKINST (查询历史)
    CMD->>CMD: 判断是否多实例任务
    alt 单实例任务
        CMD->>DB: DELETE FROM ACT_RU_TASK (当前任务)
        CMD->>DB: INSERT INTO ACT_RU_TASK (恢复上一任务)
    else 多实例任务 (SequentialMultiInstanceBehavior)
        CMD->>DB: 处理多实例逻辑
        CMD->>DB: DELETE/INSERT ACT_RU_TASK
        CMD->>DB: UPDATE ACT_RU_VARIABLE (nrOfActiveInstances)
    end
    CMD->>DB: UPDATE ACT_RU_EXECUTION
    CMD->>DB: UPDATE ACT_HI_TASKINST/ACT_HI_ACTINST
    CMD-->>MS: 返回
    MS-->>SVC: 返回
    SVC-->>C: 返回
    C-->>U: 撤销结果
```

---

## 6. 任务跳转数据流

### 6.1 自由跳转（jump）

**触发入口**：`TaskController.jump()` → `ProcessService.moveTo()` → `JumpTaskCmdService`

```mermaid
sequenceDiagram
    participant U as 用户
    participant C as TaskController
    participant SVC as ProcessService
    participant MS as ManagementService
    participant CMD as JumpTaskCmdService
    participant DB as 数据库

    U->>C: 跳转任务（taskId, targetActivityId）
    C->>SVC: moveTo(taskId, targetActivityId)
    SVC->>MS: executeCommand(new JumpTaskCmdService(...))
    MS->>CMD: execute(commandContext)
    CMD->>DB: SELECT ACT_RU_TASK WHERE ID_=taskId
    CMD->>DB: SELECT ACT_RU_EXECUTION
    CMD->>CMD: 获取当前活动与目标活动
    CMD->>DB: DELETE FROM ACT_RU_TASK (当前任务)
    CMD->>DB: DELETE FROM ACT_RU_IDENTITYLINK
    CMD->>DB: DELETE FROM ACT_RU_VARIABLE (任务级变量)
    CMD->>DB: UPDATE ACT_HI_TASKINST SET DELETE_REASON_='跳转'
    CMD->>DB: UPDATE ACT_HI_ACTINST SET END_TIME_
    CMD->>DB: INSERT INTO ACT_RU_EXECUTION (目标节点执行)
    CMD->>DB: INSERT INTO ACT_RU_TASK (目标节点任务)
    CMD->>DB: INSERT INTO ACT_HI_ACTINST (目标节点活动)
    CMD->>DB: INSERT INTO ACT_HI_TASKINST
    CMD->>DB: UPDATE ACT_RU_EXECUTION SET ACT_ID_=targetActivityId
    CMD-->>MS: 返回
    MS-->>SVC: 返回
    SVC-->>C: 返回
    C-->>U: 跳转成功
```

### 6.2 终止流程（terminate）

**触发入口**：`ProcessInstanceController.delete()` → `ProcessService.terminateProcess()`

```mermaid
sequenceDiagram
    participant U as 用户
    participant C as ProcessInstanceController
    participant SVC as ProcessService
    participant RTS as RuntimeService
    participant DB as 数据库

    U->>C: 终止流程（processInstanceId, reason）
    C->>SVC: terminateProcess(processInstanceId, reason)
    SVC->>RTS: deleteProcessInstance(processInstanceId, reason)
    RTS->>DB: DELETE FROM ACT_RU_TASK WHERE PROC_INSTANCE_ID_=?
    RTS->>DB: DELETE FROM ACT_RU_EXECUTION WHERE PROC_INSTANCE_ID_=?
    RTS->>DB: DELETE FROM ACT_RU_VARIABLE WHERE PROC_INSTANCE_ID_=?
    RTS->>DB: DELETE FROM ACT_RU_IDENTITYLINK WHERE PROC_INSTANCE_ID_=?
    RTS->>DB: UPDATE ACT_HI_PROCINST SET END_TIME_, DELETE_REASON_
    RTS->>DB: UPDATE ACT_HI_TASKINST SET END_TIME_, DELETE_REASON_
    RTS->>DB: UPDATE ACT_HI_ACTINST SET END_TIME_
    RTS-->>SVC: 完成
    SVC-->>C: 返回
    C-->>U: 终止成功
```

---

## 7. 流程图生成数据流

### 7.1 生成流程图

**触发入口**：`ProcessInstanceController.diagram()`

```mermaid
sequenceDiagram
    participant U as 用户
    participant C as ProcessInstanceController
    participant RTS as RuntimeService
    participant RS as RepositoryService
    participant PDG as ProcessDiagramGenerator
    participant DB as 数据库

    U->>C: 查看流程图（processInstanceId）
    C->>RTS: createProcessInstanceQuery().processInstanceId(id).singleResult()
    RTS->>DB: SELECT ACT_RU_EXECUTION
    C->>RTS: getActiveActivityIds(processInstanceId)
    RTS->>DB: SELECT ACT_RU_EXECUTION WHERE PROC_INSTANCE_ID_=?
    RTS-->>C: 返回当前活动ID列表
    C->>RS: getProcessDefinition(processDefinitionId)
    RS->>DB: SELECT ACT_RE_PROCDEF
    RS-->>C: 返回流程定义
    C->>RS: getProcessModel(processDefinitionId)
    RS->>DB: SELECT ACT_GE_BYTEARRAY
    RS-->>C: 返回 BPMN XML
    C->>PDG: generateDiagram(bpmnModel, activityIds)
    PDG-->>C: 返回 PNG 流（中文宋体）
    C-->>U: 输出图片
```

---

## 8. 待办查询数据流

### 8.1 查询待办任务

**触发入口**：`TaskController.todoList()` → `ProcessService.findTodoTask()`

```mermaid
sequenceDiagram
    participant U as 用户
    participant C as TaskController
    participant SVC as ProcessService
    participant TS as TaskService
    participant HS as HistoryService
    participant DB as 数据库

    U->>C: 查询待办（userId, page）
    C->>SVC: findTodoTask(userId, page)
    SVC->>TS: createTaskQuery().taskCandidateOrAssigned(userId).active().orderByTaskCreateTime().desc()
    TS->>DB: SELECT ACT_RU_TASK JOIN ACT_RU_IDENTITYLINK
    TS-->>SVC: 返回 Task 列表
    SVC->>HS: createHistoricProcessInstanceQuery().processInstanceId(?)
    HS->>DB: SELECT ACT_HI_PROCINST
    HS-->>SVC: 返回流程实例信息
    SVC->>SVC: 组装 BaseVO（task + processInstance + processDefinition）
    SVC-->>C: 返回 Page<BaseVO>
    C-->>U: 返回 JSON
```

**涉及表查询**：

| 表名 | 查询字段 | 说明 |
|------|----------|------|
| `ACT_RU_TASK` | `ASSIGNEE_`, `CREATE_TIME_` | 待办任务 |
| `ACT_RU_IDENTITYLINK` | `USER_ID_`, `TASK_ID_` | 候选人关联 |
| `ACT_HI_PROCINST` | `ID_` | 流程实例信息 |
| `ACT_RE_PROCDEF` | `ID_` | 流程定义信息 |

---

## 9. 运行时页面数据流

### 9.1 获取活动节点列表

**触发入口**：`RuntimePageService.getActivityList()`

```mermaid
sequenceDiagram
    participant C as Controller
    participant SVC as RuntimePageService
    participant RS as RepositoryService
    participant RTS as RuntimeService
    participant DB as 数据库

    C->>SVC: getActivityList(processInstanceId)
    SVC->>RTS: createProcessInstanceQuery().processInstanceId(id)
    RTS->>DB: SELECT ACT_RU_EXECUTION
    SVC->>RS: getProcessDefinition(processDefinitionId)
    RS->>DB: SELECT ACT_RE_PROCDEF
    SVC->>RS: getBpmnModel(processDefinitionId)
    RS->>DB: SELECT ACT_GE_BYTEARRAY
    SVC->>SVC: 解析 BPMN 获取所有 UserTask
    SVC-->>C: 返回活动节点列表
```

### 9.2 查询候选人

**触发入口**：`RuntimePageService.getCandidateUserNames()`

```mermaid
sequenceDiagram
    participant C as Controller
    participant SVC as RuntimePageService
    participant TS as TaskService
    participant DB as 数据库

    C->>SVC: getCandidateUserNames(taskId)
    SVC->>TS: getIdentityLinksForTask(taskId)
    TS->>DB: SELECT ACT_RU_IDENTITYLINK WHERE TASK_ID_=?
    TS-->>SVC: 返回 IdentityLink 列表
    SVC->>SVC: 提取 userId / groupId
    SVC-->>C: 返回候选人名称列表
```

---

## 10. 数据流转规则汇总

### 10.1 运行时 → 历史归档规则

| 触发事件 | 运行时表操作 | 历史表操作 |
|----------|-------------|-----------|
| 流程启动 | INSERT `ACT_RU_EXECUTION` | INSERT `ACT_HI_PROCINST` |
| 任务创建 | INSERT `ACT_RU_TASK` | INSERT `ACT_HI_TASKINST` + `ACT_HI_ACTINST` |
| 任务完成 | DELETE `ACT_RU_TASK` | UPDATE `ACT_HI_TASKINST.END_TIME_` |
| 流程结束 | DELETE `ACT_RU_EXECUTION` | UPDATE `ACT_HI_PROCINST.END_TIME_` |
| 变量写入 | INSERT/UPDATE `ACT_RU_VARIABLE` | INSERT `ACT_HI_VARINST` + `ACT_HI_DETAIL` |
| 添加评论 | - | INSERT `ACT_HI_COMMENT` |

### 10.2 撤回/撤销数据恢复规则

| 操作 | 当前任务 | 上一任务 | 历史记录 |
|------|----------|----------|----------|
| 撤回（revoke） | DELETE `ACT_RU_TASK` | INSERT `ACT_RU_TASK`（恢复） | UPDATE `ACT_HI_TASKINST.DELETE_REASON_='撤回'` |
| 撤销（withdraw） | DELETE `ACT_RU_TASK` | INSERT `ACT_RU_TASK`（恢复） | UPDATE `ACT_HI_TASKINST.DELETE_REASON_='撤销'` |
| 跳转（jump） | DELETE `ACT_RU_TASK` | INSERT `ACT_RU_TASK`（目标节点） | UPDATE `ACT_HI_TASKINST.DELETE_REASON_='跳转'` |

### 10.3 任务分配数据流规则

| TASK_TYPE | 读取来源 | 写入目标 |
|-----------|----------|----------|
| `assignee` | `dp_act_unify_task.CANDIDATE_IDS` | `ACT_RU_TASK.ASSIGNEE_` |
| `candidateUser` | `dp_act_unify_task.CANDIDATE_IDS` | `ACT_RU_TASK.ASSIGNEE_`（单人）或 `ACT_RU_IDENTITYLINK`（多人） |
| `candidateGroup` | `dp_act_unify_task.CANDIDATE_IDS` | `ACT_RU_IDENTITYLINK` (TYPE_=candidate, GROUP_ID_=) |
| `modify` | 流程变量 `entity.userId` | `ACT_RU_TASK.ASSIGNEE_` |

---

## 11. 跨模块数据流

### 11.1 与 PMS-struts 业务模块的交互

PMS-activiti 作为工作流引擎，被 PMS-struts 的业务模块调用：

```mermaid
flowchart LR
    subgraph PMS-struts 业务模块
        CB[CallBackAction]
        PM[PmClosedLoopAction]
        PS[PresalesAction]
        SC[SubcontractAction]
    end

    subgraph PMS-activiti
        RTS[RuntimeService]
        TS[TaskService]
        HS[HistoryService]
    end

    subgraph 数据库
        BIZ[业务表 pm_*]
        ACT[Activiti 表 ACT_*]
    end

    CB -->|startProcessInstanceByKey| RTS
    PM -->|startProcessInstanceByKey| RTS
    PS -->|startProcessInstanceByKey| RTS
    SC -->|startProcessInstanceByKey| RTS

    CB -->|complete| TS
    PM -->|complete| TS

    CB -->|查询历史| HS

    RTS --> ACT
    TS --> ACT
    HS --> ACT

    CB --> BIZ
    PM --> BIZ
```

### 11.2 业务键关联

业务模块启动流程时传入 `businessKey`，作为业务表与流程实例的关联：

```java
// 业务模块代码示例
runtimeService.startProcessInstanceByKey(
    "CallBack",           // 流程定义键
    projectCode,          // businessKey（项目编号）
    variables             // 流程变量
);
```

**关联查询**：
```sql
SELECT 
    p.projectCode, p.projectName,
    h.ID_ as processInstanceId, h.START_TIME_, h.END_TIME_
FROM pm_project_header p
JOIN ACT_HI_PROCINST h ON h.BUSINESS_KEY_ = p.projectCode
WHERE h.PROC_DEF_ID_ LIKE 'CallBack:%';
```

---

## 12. 相关文档

- [crud-matrix.md](crud-matrix.md) — 模块-表 CRUD 矩阵
- [../03-database/er-diagram.md](../03-database/er-diagram.md) — ER 图
- [../03-database/unify-task-table.md](../03-database/unify-task-table.md) — 统一任务表
- [../02-modules/custom-commands.md](../02-modules/custom-commands.md) — 自定义命令
- [../02-modules/task-management.md](../02-modules/task-management.md) — 任务管理
- [../02-modules/process-instance-management.md](../02-modules/process-instance-management.md) — 流程实例管理
