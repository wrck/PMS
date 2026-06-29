# PMS-springmvc 工作流管理模块详细文档

> 本文档深度分析 PMS-springmvc 工作流管理模块的所有 Controller、Service、Entity 类。

---

## 1. 模块概述

工作流管理模块基于 Activiti 流程引擎，为 PMS-springmvc 提供统一的流程驱动能力。

### 涉及的 Controller 类列表

| Controller 类 | URL 命名空间 | 职责 | 方法数 |
|---------------|-------------|------|--------|
| `WorkFlowController` | `/workflow` | 工作流管理 | 15 |

### 涉及的 Service 类列表

| Service 接口 | 实现类 | 职责 |
|-------------|--------|------|
| `IPmWorkFlowService` | `PmWorkFlowService` | 工作流服务 |
| `IPmWorkBenchService` | `PmWorkBenchService` | 工作台服务 |

### 涉及的数据库表列表

| 表名 | 说明 |
|------|------|
| `pm_workflow` | 工作流数据表 |

---

## 2. 常量定义

### 2.1 流程类型（ProcessType）

| 常量名 | 值 | 说明 |
|--------|-----|------|
| `QUALITY_APPROVE_TRACK` | `QualityApproveTrack` | 质量审批跟踪流程 |
| `SUBCONTRACT_INSPECTION` | `SubcontractInspection` | 转包检验流程 |

### 2.2 任务类型（TaskType）

| 常量名 | 值 | 说明 |
|--------|-----|------|
| `AF_APPROVE_TASK` | `afApproveTask` | 安服质量审核任务 |
| `YF_APPROVE_TASK` | `yfApproveTask` | 研发质量审核任务 |
| `TRACK_TASK` | `trackTask` | 任务跟踪任务 |
| `ACCEPTANCE_TASK` | `acceptanceTask` | 验收材料审批任务 |
| `END` | `end` | 流程结束 |
| `REJECT` | `reject` | 流程驳回 |

### 2.3 数据类型（DataType）

| 常量名 | 值 | 说明 |
|--------|-----|------|
| `PROJECT` | `project` | 项目 |
| `PROJECT_TASK` | `projectTask` | 项目任务 |
| `PROJECT_OPPORTUNITY` | `projectOpportunity` | 项目机会点 |
| `PROJECT_DISPATCH` | `dispatch` | 项目外派 |
| `DISPATCH_SETTLEMENT` | `settlement` | 项目外派结算 |
| `INDUSTRY_ASSET` | `industryAsset` | 行业资产 |
| `INDUSTRY_LEAK` | `industryLeak` | 行业漏洞 |

---

## 3. Controller 方法详细说明

### 3.1 WorkFlowController

#### `home(Model model)`
- **URL**: `/workflow/`
- **HTTP 方法**: GET
- **功能**: 工作流管理首页
- **返回值**: 视图名称

#### `list(PageParam pageParam, PmWorkFlowVO v, Model model)`
- **URL**: `/workflow/list`
- **HTTP 方法**: GET
- **功能**: 工作流列表查询
- **参数**: 分页参数、查询条件
- **返回值**: 视图名称

#### `info(PmWorkFlowVO v, Model model)`
- **URL**: `/workflow/info/list`
- **HTTP 方法**: GET
- **功能**: 工作流信息列表
- **业务逻辑**:
  1. 权限检查
  2. 查询工作流列表
  3. 收集流程实例ID
  4. 查询流程活动列表
- **返回值**: 视图名称

#### `findOne(Integer id, Model model)`
- **URL**: `/workflow/{id}` 或 `/workflow/modals/{id}`
- **HTTP 方法**: GET
- **功能**: 查询工作流详情
- **业务逻辑**:
  1. 权限检查
  2. 查询工作流记录
  3. 查询当前任务
  4. 获取流程变量 entity
  5. 装饰实体对象
  6. 查询表单字段列表
- **返回值**: 视图名称

#### `complete(Boolean isPass, String content, String data, String taskId, Model model)`
- **URL**: `/workflow/complete/{taskId}`
- **HTTP 方法**: POST
- **功能**: 完成任务
- **参数**:
  - `isPass`: 审批结果（Boolean）
  - `content`: 审批意见（String）
  - `data`: 附加业务数据（String）
  - `taskId`: 任务ID（PathVariable）
- **业务逻辑**:
  1. 查询当前用户
  2. 查询任务并校验权限
  3. 构建流程变量
  4. 调用 processService.complete()
  5. 处理异常
- **返回值**: void（通过 model 设置 status 和 message）

#### `withdrawTask(String instanceId, String userId, Model model)`
- **URL**: `/workflow/withdraw/{instanceId}/{userId}`
- **HTTP 方法**: POST
- **功能**: 撤回任务
- **参数**:
  - `instanceId`: 历史流程节点ID（PathVariable）
  - `userId`: 用户ID（PathVariable）
- **业务逻辑**:
  1. 获取当前用户ID
  2. 调用 processService.withdrawTask()
  3. 返回撤回结果

#### `startProcess(PmWorkFlow pmWorkFlow, Model model)`
- **URL**: `/workflow/startProcess`
- **HTTP 方法**: POST
- **功能**: 启动流程实例
- **参数**: `pmWorkFlow`: 工作流对象
- **业务逻辑**:
  1. 根据 processKey 判断流程类型
  2. 检查操作权限
  3. 查询关联实体
  4. 调用 pmWorkFlowService.startProcess()
  5. 返回 currentTaskId 和 currentProcInstId

---

## 4. Service 方法详细说明

### 4.1 IPmWorkFlowService

| 方法签名 | 功能 | 事务类型 |
|----------|------|----------|
| `List<PmWorkFlow> selectRunTasksByAssigneeAndProcessKeyAndTaskKey(...)` | 查询待办任务 | 无事务 |
| `List<PmWorkFlow> selectFinishedTasksByAssignee(...)` | 查询已办任务 | 无事务 |
| `PmWorkFlow currentParticipantWorkFlow(...)` | 查询当前参与人工作流 | 无事务 |
| `List<String> selectProcInstIdsBySelective(...)` | 查询流程实例ID | 无事务 |
| `void deleteProcess(...)` | 删除流程 | 事务 |
| `void deleteProcessThread(...)` | 线程删除流程 | 事务 |
| `Integer selectParticipantFinallyTask(...)` | 查询最终办理人 | 无事务 |
| `String startProcess(PmWorkFlow, Object)` | 启动流程 | 事务 |
| `void terminateProcess(...)` | 终止流程 | 事务 |
| `List<String> selectActivitiUserMails(...)` | 查询用户邮箱 | 无事务 |
| `PmWorkFlow decoratorEntity(PmWorkFlow)` | 装饰流程变量实体 | 无事务 |

---

## 5. 数据模型

### 5.1 PmWorkFlow 实体

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `id` | Integer | 主键ID |
| `procInstId` | String | 流程实例ID |
| `processKey` | String | 流程定义Key |
| `dataType` | String | 数据类型 |
| `dataId` | Integer | 数据ID |
| `objType` | String | 对象类型 |
| `objId` | Integer | 对象ID |
| `taskId` | String | 任务ID |
| `taskKey` | String | 任务定义Key |
| `title` | String | 标题 |
| `hasTask` | Boolean | 是否有当前任务 |
| `customInfo` | Map | 自定义扩展信息 |

---

## 6. 业务流程

### 6.1 工作流启动流程

```
[用户发起审批] ──> WorkFlowController.startProcess()
      |
      ├── 权限检查
      │   ├── 检查项目编辑权限
      │   └── 检查数据类型权限
      |
      ├── 流程类型判断
      │   ├── QualityApproveTrack: 质量审批跟踪流程
      │   │   ├── PROJECT_TASK: 查询项目任务
      │   │   ├── INDUSTRY_ASSET: 查询行业资产
      │   │   └── INDUSTRY_LEAK: 查询行业漏洞
      │   │
      │   └── SubcontractInspection: 转包检验流程
      │       ├── PROJECT_DISPATCH: 查询发运项目
      │       └── DISPATCH_SETTLEMENT: 查询发运结算
      |
      ├── 启动流程实例
      │   ├── pmWorkFlowService.startProcess(pmWorkFlow, entity)
      │   ├── 设置流程变量 entity
      │   └── 返回 currentTaskId, currentProcInstId
      |
      └── 异常处理
          ├── ActivitiException: 流程未部署
          └── Exception: 系统内部错误
```

### 6.2 任务完成流程

```
[用户办理任务] ──> WorkFlowController.complete()
      |
      ├── 任务校验
      │   ├── 查询当前任务
      │   ├── 校验任务是否属于当前用户
      │   └── 校验任务是否已签收
      |
      ├── 构建流程变量
      │   ├── isPass: 审批结果（true/false）
      │   └── data: 附加业务数据
      |
      ├── 完成任务
      │   └── processService.complete(taskId, content, assigneeID, variables)
      │
      └── 异常处理
          ├── ActivitiObjectNotFoundException: 任务不存在
          ├── ActivitiException: 任务正在协办
          └── Exception: 系统内部错误
```

---

## 7. 异常处理

### 7.1 异常类型

| 异常类 | 触发条件 | 处理方式 |
|--------|----------|----------|
| `ActivitiObjectNotFoundException` | 任务不存在 | 返回错误信息"此任务不存在" |
| `ActivitiException` | 流程异常 | 提取错误信息 |
| `CustomActivitiException` | 自定义 Activiti 异常 | 返回业务错误 |
| `Exception` | 系统内部错误 | 记录日志并返回 |

### 7.2 异常处理示例

```java
try {
    processService.complete(taskId, content, assigneeID, variables);
    model.addAttribute("status", Boolean.TRUE);
    model.addAttribute("message", "任务办理完成！");
} catch (ActivitiObjectNotFoundException e) {
    model.addAttribute("status", Boolean.FALSE);
    model.addAttribute("message", "此任务不存在，请联系管理员！");
    ExceptionHandler.insertException(e);
} catch (ActivitiException e) {
    String errorMsg = extractErrorMessage(e);
    model.addAttribute("status", Boolean.FALSE);
    model.addAttribute("message", errorMsg);
    ExceptionHandler.insertException(e);
} catch (Exception e) {
    model.addAttribute("status", Boolean.FALSE);
    model.addAttribute("message", "任务办理失败，请联系管理员！");
    ExceptionHandler.insertException(e);
}
```

---

## 8. 配置说明

### 8.1 URL 映射

```java
@Controller
@RequestMapping(ProjectConstant.URLPath.WORKFLOW_MANAGER)  // "/workflow"
public class WorkFlowController extends AbstractController<IPmWorkFlowService, PmWorkFlow, PmWorkFlowVO> {
    @PostConstruct
    private void init() {
        this.setUrlNameSpace("/");
        this.setViewModel("workflow");
    }
}
```

### 8.2 权限控制

| 权限编码 | 说明 |
|----------|------|
| `workflow:list` | 查看工作流列表 |
| `workflow:detail` | 查看工作流详情 |
| `workflow:edit` | 编辑工作流 |
| `project:edit` | 编辑项目 |
| `projectTask:edit` | 编辑项目任务 |
| `dispatch:edit` | 编辑发运项目 |
