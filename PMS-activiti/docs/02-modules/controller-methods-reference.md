# Controller 方法参考

> 本文档汇总 PMS-activiti 模块所有 Controller 的方法签名、URL 映射、参数与返回值。
> 用于快速查找接口信息。

---

## 1. Controller 清单

| Controller 类 | URL 前缀 | 职责 |
|---------------|----------|------|
| `ModelController` | `/workflow/model` | 模型管理 |
| `ProcessDefinitionController` | `/workflow/definition` | 流程定义管理 |
| `ProcessInstanceController` | `/workflow/instance` | 流程实例管理 |
| `TaskController` | `/workflow/task` | 任务管理 |
| `WorkFlowSubModalController` | `/workflow/modals` | 工作流模态框 |

> **注意**：URL 前缀由 `Consts.URLPath.WORKFLOW_MANAGER` 定义，此处假设为 `/workflow/`。

---

## 2. ModelController

### 2.1 类信息

- **包名**：`com.dp.plat.activiti.controller`
- **注解**：`@Controller`、`@RequestMapping(Consts.URLPath.WORKFLOW_MANAGER + "model")`
- **依赖**：`RepositoryService`、`RuntimeService`、`TaskService`、`ManagementService`

### 2.2 方法列表

| 方法签名 | URL | HTTP | 功能 | 返回值 |
|----------|-----|------|------|--------|
| `String toListModel()` | `/model` | GET | 跳转模型列表页 | `workflow/model_list` |
| `String findAll(PageParam<?> pageParam, Model model)` | `/model/list` | GET | 查询模型列表 | `workflow/model_list` |
| `String findOne(String modelId, Model model)` | `/{modelId}` | GET | 跳转模型编辑器 | `redirect:/modeler.html` |
| `String toCreateModel()` | `/create` | GET | 跳转创建模型页面 | `workflow/add_model` |
| `void create(String name, String key, String description, HttpServletRequest, HttpServletResponse)` | `/create` | POST | 创建模型 | 重定向 |
| `String deploy(String modelId, Model model)` | `/{modelId}` | PATCH | 部署模型 | `workflow/model_list` |
| `String delete(String modelId, Model model)` | `/{modelId}` | DELETE | 删除模型 | `workflow/model_list` |

---

## 3. ProcessDefinitionController

### 3.1 类信息

- **包名**：`com.dp.plat.activiti.controller`
- **注解**：`@Controller`、`@RequestMapping(Consts.URLPath.WORKFLOW_MANAGER + "definition")`
- **依赖**：`RepositoryService`、`IProcessService`

### 3.2 方法列表

| 方法签名 | URL | HTTP | 功能 | 返回值 |
|----------|-----|------|------|--------|
| `String list()` | `/definition` | GET | 跳转流程定义列表页 | `workflow/process_list` |
| `String listProcess(PageParam<BaseVO> pageParam, Model model)` | `/definition/list` | GET | 查询流程定义列表 | `workflow/process_list` |
| `void deploy(MultipartFile file, HttpServletRequest, Model model)` | `/definition/deploy` | POST | 上传部署流程 | void（JSON） |
| `void delete(String deploymentId, Model model)` | `/definition/{deploymentId}` | DELETE | 删除部署 | void（JSON） |
| `void convertToModel(String processDefinitionId, Model model)` | `/definition/model/{processDefinitionId}` | GET | 流程定义转模型 | void（JSON） |
| `void loadByDeployment(String processDefinitionId, String resourceType, HttpServletResponse)` | `/definition/{resourceType}/{processDefinitionId}` | GET | 加载流程图/XML | void（流） |
| `void updateProcessStatusByProDefinitionId(String status, String processDefinitionId, Model model)` | `/definition/{status}/{processDefinitionId}` | POST | 激活/挂起流程定义 | void（JSON） |

---

## 4. ProcessInstanceController

### 4.1 类信息

- **包名**：`com.dp.plat.activiti.controller`
- **注解**：`@Controller`、`@RequestMapping(Consts.URLPath.WORKFLOW_MANAGER + "instance")`
- **依赖**：`IUserService`、`IProcessService`、`RepositoryService`、`HistoryService`、`IdentityService`、`IRuntimePageService`

### 4.2 方法列表

| 方法签名 | URL | HTTP | 功能 | 返回值 |
|----------|-----|------|------|--------|
| `String list()` | `/instance` | GET | 跳转运行中流程列表页 | `workflow/running_process_list` |
| `void showDiagram(String processInstanceId, HttpServletResponse)` | `/instance/diagram/{processInstanceId}` | GET | 显示流程图（带跟踪） | void（图片流） |
| `void loadByProcessInstance(String resourceType, String processInstanceId, HttpServletResponse)` | `/instance/{resourceType}/{processInstanceId}` | GET | 加载流程图/XML | void（流） |
| `void showInfo(String processInstanceId, Model model)` | `/instance/info/{processInstanceId}/list` | POST | 显示流程明细 | void（JSON） |
| `String toListProcessRunning()` | `/instance/toListProcessManager` | GET | 跳转流程管理页面 | `workflow/list_process_manager` |
| `String listRuningProcess(PageParam<ProcessInstanceEntity> pageParam, Model model)` | `/instance/runningProcess` | GET | 查询运行中流程 | `workflow/running_process_list` |
| `String findFinishedProcessInstances(PageParam<BaseVO> pageParam, Model model)` | `/instance/finishedProcess` | GET | 查询已结束流程 | `workflow/process/finishedProcess` |
| `void updateProcessStatusByProInstanceId(String status, String processInstanceId, Model model)` | `/instance/{status}/{processInstanceId}` | POST | 激活/挂起流程实例 | void（JSON） |
| `void deleteProcessByProInstanceId(String processInstanceId, String deleteReason, Model model)` | `/instance/delete/{processInstanceId}` | POST | 删除流程实例 | void（JSON） |
| `String toListApply()` | `/instance/toListApply` | GET | 跳转申请列表 | `apply/list_apply` |

---

## 5. TaskController

### 5.1 类信息

- **包名**：`com.dp.plat.activiti.controller`
- **注解**：`@Controller`、`@RequestMapping(URLPath.WORKFLOW_MANAGER + "task")`
- **依赖**：`IUserService`、`IProcessService`、`IdentityService`

### 5.2 方法列表

| 方法签名 | URL | HTTP | 功能 | 返回值 |
|----------|-----|------|------|--------|
| `String list()` | `/task` | GET | 跳转任务列表页 | `workflow/task_list` |
| `String todoTask(PageParam<BaseVO> pageParam, Model model)` | `/task/todoTask` | GET | 查询待办任务 | `workflow/todoTask` |
| `String findFinishedTaskInstances(PageParam<BaseVO> pageParam, Model model)` | `/task/endTask` | GET | 查询已完成任务 | `workflow/endTask` |
| `void claim(String taskId, Model model)` | `/task/claim/{taskId}` | GET/POST | 签收任务 | void（JSON） |
| `void unclaim(String taskId, Model model)` | `/task/unclaim/{taskId}` | GET/POST | 取消签收 | void（JSON） |
| `void delegateTask(String taskId, String userId, Model model)` | `/task/delegate/{taskId}` | POST | 委派任务 | void（JSON） |
| `void transferTask(String taskId, String userId, Model model)` | `/task/transfer/{taskId}` | POST | 转办任务 | void（JSON） |
| `void revoke(String taskId, String processInstanceId, Model model)` | `/task/revoke/{processInstanceId}/{taskId}` | GET/POST | 撤销任务 | void（JSON） |
| `void jumpTargetTask(String taskId, String taskDefinitionKey, Model model)` | `/task/jump` | POST | 任务跳转 | void（JSON） |
| `void withdrawTask(String instanceId, String userId, Model model)` | `/task/withdraw/{instanceId}/{userId}` | POST | 撤回任务 | void（JSON） |

---

## 6. WorkFlowSubModalController

### 6.1 类信息

- **包名**：`com.dp.plat.activiti.controller`
- **注解**：`@Controller`、`@RequestMapping(Consts.URLPath.WORKFLOW_MANAGER + "modals")`
- **职责**：系统模态框页面控制器

### 6.2 方法列表

| 方法签名 | URL | HTTP | 功能 | 返回值 |
|----------|-----|------|------|--------|
| `String showDefinition(String processDefinitionId, Model model)` | `/modals/definition/{processDefinitionId}` | GET | 显示流程定义详情 | `workflow/modals/showDefinition` |
| `String showInstance(String processInstanceId, Model model)` | `/modals/instance/{processInstanceId}` | GET | 显示流程实例详情 | `workflow/modals/showInstance` |
| `String completeTask(String taskId, String processInstanceId, String taskType, String businessKey, String taskDefKey, Model model)` | `/modals/task/{taskType}/{processInstanceId}/{taskId}` | GET | 完成任务弹窗 | `workflow/modals/completeTask` |

---

## 7. 返回值约定

### 7.1 视图名称

| 视图名称 | 路径 | 说明 |
|----------|------|------|
| `workflow/model_list` | `/WEB-INF/workflow/model_list.jsp` | 模型列表 |
| `workflow/process_list` | `/WEB-INF/workflow/process_list.jsp` | 流程定义列表 |
| `workflow/running_process_list` | `/WEB-INF/workflow/running_process_list.jsp` | 运行中流程列表 |
| `workflow/task_list` | `/WEB-INF/workflow/task_list.jsp` | 任务列表 |
| `workflow/todoTask` | `/WEB-INF/workflow/todoTask.jsp` | 待办任务 |
| `workflow/endTask` | `/WEB-INF/workflow/endTask.jsp` | 已完成任务 |
| `workflow/modals/showDefinition` | `/WEB-INF/workflow/modals/showDefinition.jsp` | 流程定义详情弹窗 |
| `workflow/modals/showInstance` | `/WEB-INF/workflow/modals/showInstance.jsp` | 流程实例详情弹窗 |
| `workflow/modals/completeTask` | `/WEB-INF/workflow/modals/completeTask.jsp` | 完成任务弹窗 |

### 7.2 JSON 返回

返回 `void` 的方法通过 `Model model.addAttribute()` 返回 JSON：

| 属性 | 类型 | 说明 |
|------|------|------|
| `status` | Boolean | 操作状态（true=成功, false=失败） |
| `message` | String | 提示信息 |
| `data` | Object/List | 数据 |

### 7.3 流返回

返回 `void` 的方法通过 `HttpServletResponse.getOutputStream()` 输出流：

| 方法 | 内容类型 | 说明 |
|------|----------|------|
| `showDiagram()` | `image/png` | 流程图 PNG |
| `loadByDeployment()` | `image/png` 或 `application/xml` | 流程图或 XML |
| `loadByProcessInstance()` | `image/png` 或 `application/xml` | 流程图或 XML |

---

## 8. 异常处理约定

### 8.1 异常处理方式

Controller 方法通过 try-catch 处理异常，并将异常信息记录到 `ExceptionHandler`：

```java
@RequestMapping("/claim/{taskId}")
public void claim(@PathVariable("taskId") String taskId, Model model) {
    try {
        User user = UserContext.getCurrentUser();
        this.processService.claim(user, taskId);
        model.addAttribute("status", Boolean.TRUE);
        model.addAttribute("message", "任务签收成功！");
    } catch (ActivitiObjectNotFoundException e) {
        model.addAttribute("status", Boolean.FALSE);
        model.addAttribute("message", "此任务不存在！任务签收失败！");
        ExceptionHandler.insertException(e);
    } catch (ActivitiTaskAlreadyClaimedException e) {
        model.addAttribute("status", Boolean.FALSE);
        model.addAttribute("message", "此任务已被其他组成员签收！请刷新页面重新查看！");
        ExceptionHandler.insertException(e);
    } catch (Exception e) {
        model.addAttribute("status", Boolean.FALSE);
        model.addAttribute("message", "任务签收失败！请联系管理员！");
        ExceptionHandler.insertException(e);
    }
}
```

### 8.2 常见异常类

| 异常类 | 说明 | 处理方式 |
|--------|------|----------|
| `ActivitiObjectNotFoundException` | 对象不存在 | 返回"不存在"提示 |
| `ActivitiTaskAlreadyClaimedException` | 任务已被签收 | 返回"已被签收"提示 |
| `ActivitiIllegalArgumentException` | 参数非法 | 返回错误信息 |
| `Exception` | 其他异常 | 返回"系统错误"提示 |

---

## 9. 相关文档

- [流程定义管理](process-definition-management.md) — 流程定义管理详解
- [任务管理](task-management.md) — 任务管理详解
- [流程实例管理](process-instance-management.md) — 流程实例管理详解
- [运行时页面](runtime-page.md) — 运行时页面服务
- [service-methods-reference.md](service-methods-reference.md) — Service 方法参考
