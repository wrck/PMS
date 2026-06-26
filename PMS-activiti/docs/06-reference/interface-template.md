# PMS-activiti 接口模板

> 本文档提供 PMS-activiti 模块的标准接口模板，供开发新接口时参考。
> 数据来源：`d:\常规软件\QoderCode\workspace\PMS\PMS-activiti\src\main\java\com\dp\plat\activiti\controller\` 源码。

---

## 1. Controller 接口模板

### 1.1 标准 Controller 模板

```java
package com.dp.plat.activiti.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dp.plat.activiti.entity.BaseVO;
import com.dp.plat.activiti.service.ProcessService;
import com.dp.plat.core.entity.Result;
import com.dp.plat.core.util.Page;

/**
 * {功能名称} Controller
 * 
 * @author {作者}
 */
@Controller
@RequestMapping("/{module}")
public class {Module}Controller {

    private static final Logger logger = Logger.getLogger({Module}Controller.class);

    @Autowired
    private ProcessService processService;

    /**
     * 列表页面
     */
    @RequestMapping("/list")
    public String list(Model model) {
        // 查询数据
        List<BaseVO> list = processService.findAll();
        model.addAttribute("list", list);
        return "activiti/{module}/list";
    }

    /**
     * 分页查询（JSON）
     */
    @RequestMapping("/page")
    @ResponseBody
    public Result<?> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int rows,
            @RequestParam(required = false) String processDefinitionKey) {
        try {
            Page<BaseVO> result = processService.findPage(processDefinitionKey, page, rows);
            return Result.success(result);
        } catch (Exception e) {
            logger.error("分页查询失败", e);
            return Result.fail("查询失败: " + e.getMessage());
        }
    }

    /**
     * 详情页面
     */
    @RequestMapping("/detail")
    public String detail(@RequestParam String id, Model model) {
        BaseVO vo = processService.findById(id);
        model.addAttribute("vo", vo);
        return "activiti/{module}/detail";
    }

    /**
     * 新增/编辑页面
     */
    @RequestMapping("/edit")
    public String edit(@RequestParam(required = false) String id, Model model) {
        if (StringUtils.isNotBlank(id)) {
            BaseVO vo = processService.findById(id);
            model.addAttribute("vo", vo);
        }
        return "activiti/{module}/edit";
    }

    /**
     * 保存
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> save(BaseVO vo, HttpServletRequest request) {
        try {
            processService.save(vo);
            return Result.success("保存成功");
        } catch (Exception e) {
            logger.error("保存失败", e);
            return Result.fail("保存失败: " + e.getMessage());
        }
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> delete(@RequestParam String id) {
        try {
            processService.delete(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            logger.error("删除失败", e);
            return Result.fail("删除失败: " + e.getMessage());
        }
    }
}
```

### 1.2 流程定义部署接口模板

```java
/**
 * 部署流程定义
 * 
 * @param file BPMN 文件
 * @return 部署结果
 */
@RequestMapping(value = "/deploy", method = RequestMethod.POST)
@ResponseBody
public Result<?> deploy(@RequestParam("file") MultipartFile file) {
    try {
        // 1. 校验文件
        if (file.isEmpty()) {
            return Result.fail("文件不能为空");
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            return Result.fail("文件大小超过限制（10MB）");
        }
        String filename = file.getOriginalFilename();
        if (!filename.endsWith(".bpmn") && !filename.endsWith(".zip") 
            && !filename.endsWith(".xml")) {
            return Result.fail("仅支持 BPMN/XML/ZIP 文件");
        }
        
        // 2. 部署
        Deployment deployment = repositoryService.createDeployment()
            .name(filename)
            .addInputStream(filename, new ByteArrayInputStream(file.getBytes()))
            .deploy();
        
        logger.info("流程部署成功: " + deployment.getId());
        return Result.success("部署成功");
    } catch (XMLException e) {
        logger.error("BPMN 解析失败", e);
        return Result.fail("BPMN 文件格式错误: " + e.getMessage());
    } catch (Exception e) {
        logger.error("部署失败", e);
        return Result.fail("部署失败: " + e.getMessage());
    }
}
```

### 1.3 流程启动接口模板

```java
/**
 * 启动流程实例
 * 
 * @param processDefinitionKey 流程定义键
 * @param businessKey 业务键
 * @param entity 流程变量实体
 * @return 启动结果
 */
@RequestMapping(value = "/start", method = RequestMethod.POST)
@ResponseBody
public Result<?> startProcess(
        @RequestParam String processDefinitionKey,
        @RequestParam String businessKey,
        @RequestParam BaseVO entity) {
    try {
        // 1. 校验流程定义存在
        ProcessDefinition def = repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey(processDefinitionKey)
            .latestVersion()
            .active()
            .singleResult();
        if (def == null) {
            return Result.fail("流程定义不存在或已挂起");
        }
        
        // 2. 设置流程变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("entity", entity);
        variables.put("startUser", UserContext.getCurrentUserId());
        
        // 3. 启动流程
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
            processDefinitionKey, businessKey, variables);
        
        logger.info("流程启动成功: key={}, businessKey={}, instanceId={}", 
            processDefinitionKey, businessKey, instance.getId());
        return Result.success(instance.getId());
    } catch (ActivitiException e) {
        logger.error("流程启动失败", e);
        return Result.fail("流程启动失败: " + e.getMessage());
    } catch (Exception e) {
        logger.error("系统异常", e);
        return Result.fail("系统异常，请联系管理员");
    }
}
```

### 1.4 任务办理接口模板

```java
/**
 * 办理任务
 * 
 * @param taskId 任务ID
 * @param result 审批结果（1=通过, -1=驳回, 2=闭环）
 * @param comment 审批意见
 * @return 办理结果
 */
@RequestMapping(value = "/complete", method = RequestMethod.POST)
@ResponseBody
public Result<?> complete(
        @RequestParam String taskId,
        @RequestParam Integer result,
        @RequestParam(required = false) String comment) {
    try {
        // 1. 校验参数
        if (StringUtils.isBlank(taskId)) {
            return Result.fail("任务ID不能为空");
        }
        if (result == null) {
            return Result.fail("审批结果不能为空");
        }
        if (comment != null && comment.length() > 1000) {
            return Result.fail("审批意见长度超过限制（1000字符）");
        }
        
        // 2. 校验任务存在
        Task task = taskService.createTaskQuery()
            .taskId(taskId)
            .taskCandidateOrAssigned(UserContext.getCurrentUserId())
            .singleResult();
        if (task == null) {
            return Result.fail("任务不存在或您无权办理");
        }
        
        // 3. 设置流程变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("result", result);
        
        // 4. 办理任务
        processService.complete(taskId, variables, comment);
        
        logger.info("任务办理成功: taskId={}, result={}, user={}", 
            taskId, result, UserContext.getCurrentUserId());
        return Result.success("办理成功");
    } catch (ActivitiObjectNotFoundException e) {
        logger.error("任务不存在: taskId={}", taskId, e);
        return Result.fail("任务不存在或已被办理");
    } catch (Exception e) {
        logger.error("任务办理失败: taskId={}", taskId, e);
        return Result.fail("办理失败: " + e.getMessage());
    }
}
```

### 1.5 任务撤回接口模板

```java
/**
 * 撤回任务
 * 
 * @param processInstanceId 流程实例ID
 * @return 撤回结果
 */
@RequestMapping(value = "/revoke", method = RequestMethod.POST)
@ResponseBody
public Result<?> revoke(@RequestParam String processInstanceId) {
    try {
        int result = processService.revoke(processInstanceId);
        switch (result) {
            case 0:
                return Result.fail("撤回失败：上一任务不存在或当前任务已结束");
            case 1:
                logger.info("撤回成功: processInstanceId={}, user={}", 
                    processInstanceId, UserContext.getCurrentUserId());
                return Result.success("撤回成功");
            case 2:
                return Result.fail("撤回失败：不满足撤回条件");
            default:
                return Result.fail("撤回失败：未知结果");
        }
    } catch (Exception e) {
        logger.error("撤回失败: processInstanceId={}", processInstanceId, e);
        return Result.fail("撤回失败: " + e.getMessage());
    }
}
```

---

## 2. Service 接口模板

### 2.1 Service 接口模板

```java
package com.dp.plat.activiti.service;

import java.util.List;
import java.util.Map;

import com.dp.plat.activiti.entity.BaseVO;
import com.dp.plat.core.util.Page;

/**
 * {功能名称} Service 接口
 * 
 * @author {作者}
 */
public interface I{Module}Service {

    /**
     * 分页查询
     */
    Page<BaseVO> findPage(String processDefinitionKey, int page, int rows);

    /**
     * 查询所有
     */
    List<BaseVO> findAll();

    /**
     * 根据ID查询
     */
    BaseVO findById(String id);

    /**
     * 保存
     */
    void save(BaseVO vo);

    /**
     * 删除
     */
    void delete(String id);
}
```

### 2.2 Service 实现模板

```java
package com.dp.plat.activiti.service.impl;

import java.util.List;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dp.plat.activiti.entity.BaseVO;
import com.dp.plat.activiti.service.I{Module}Service;
import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.util.Page;

/**
 * {功能名称} Service 实现
 * 
 * @author {作者}
 */
@Service("{module}Service")
public class {Module}Service extends AbstractBaseService implements I{Module}Service {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Override
    public Page<BaseVO> findPage(String processDefinitionKey, int page, int rows) {
        // 实现分页查询
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BaseVO> findAll() {
        // 实现查询所有
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public BaseVO findById(String id) {
        // 实现根据ID查询
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(BaseVO vo) {
        // 实现保存
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        // 实现删除
    }
}
```

---

## 3. 自定义命令模板

### 3.1 Command 实现模板

```java
package com.dp.plat.activiti.process.cmd;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.Task;

/**
 * {命令名称} 命令
 * 
 * 使用方式：
 *   managementService.executeCommand(new {CommandName}Cmd(params));
 * 
 * @author {作者}
 */
public class {CommandName}Cmd implements Command<{ReturnType}> {

    private String taskId;

    public {CommandName}Cmd(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public {ReturnType} execute(CommandContext commandContext) {
        // 1. 获取任务
        TaskEntity task = commandContext.getTaskEntityManager()
            .findTaskById(taskId);
        if (task == null) {
            throw new ActivitiException("任务不存在: " + taskId);
        }

        // 2. 业务逻辑
        // ...

        // 3. 返回结果
        return result;
    }
}
```

### 3.2 调用命令模板

```java
@Autowired
private ManagementService managementService;

public {ReturnType} executeCustomCommand(String taskId) {
    return managementService.executeCommand(new {CommandName}Cmd(taskId));
}
```

---

## 4. 监听器模板

### 4.1 TaskListener 模板

```java
package com.dp.plat.activiti.process.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * {监听器名称} 任务监听器
 * 
 * BPMN 配置：
 *   <userTask id="xxx">
 *     <extensionElements>
 *       <activiti:taskListener event="create" delegateExpression="${{listenerName}}"/>
 *     </extensionElements>
 *   </userTask>
 * 
 * @author {作者}
 */
@Component("{listenerName}")
public class {ListenerName}Listener implements TaskListener {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger({ListenerName}Listener.class);

    @Override
    public void notify(DelegateTask delegateTask) {
        String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
        String processInstanceId = delegateTask.getProcessInstanceId();
        
        try {
            // 业务逻辑
            logger.info("任务监听器触发: taskDefKey={}, processInstanceId={}", 
                taskDefinitionKey, processInstanceId);
        } catch (Exception e) {
            logger.error("任务监听器执行失败: taskDefKey=" + taskDefinitionKey, e);
        }
    }
}
```

### 4.2 ExecutionListener 模板

```java
package com.dp.plat.activiti.process.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * {监听器名称} 执行监听器
 * 
 * @author {作者}
 */
@Component("{listenerName}")
public class {ListenerName}ExecutionListener implements ExecutionListener {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger({ListenerName}ExecutionListener.class);

    @Override
    public void notify(DelegateExecution execution) {
        String activityId = execution.getCurrentActivityId();
        String eventName = execution.getEventName();
        
        try {
            logger.info("执行监听器触发: activityId={}, event={}", activityId, eventName);
            // 业务逻辑
        } catch (Exception e) {
            logger.error("执行监听器失败: activityId=" + activityId, e);
        }
    }
}
```

---

## 5. BPMN 流程模板

### 5.1 基础流程模板

```xml
<?xml version="1.0" encoding="UTF-8"?>
<definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xmlns:activiti="http://activiti.org/bpmn"
             targetNamespace="http://www.activiti.org/processdef">

  <process id="{ProcessKey}" name="{流程名称}" isExecutable="true">

    <!-- 开始事件 -->
    <startEvent id="start" name="开始"/>

    <!-- 申请人提交（动态办理人） -->
    <userTask id="applyTask" name="申请人提交">
      <extensionElements>
        <activiti:taskListener event="create" delegateExpression="${userTaskListener}"/>
      </extensionElements>
    </userTask>
    <sequenceFlow id="flow1" sourceRef="start" targetRef="applyTask"/>

    <!-- 主管审批 -->
    <userTask id="approveTask" name="主管审批">
      <extensionElements>
        <activiti:taskListener event="create" delegateExpression="${userTaskListener}"/>
      </extensionElements>
    </userTask>
    <sequenceFlow id="flow2" sourceRef="applyTask" targetRef="approveTask"/>

    <!-- 排他网关 -->
    <exclusiveGateway id="gateway1"/>
    <sequenceFlow id="flow3" sourceRef="approveTask" targetRef="gateway1"/>

    <!-- 通过 -->
    <sequenceFlow id="approvePass" name="通过" sourceRef="gateway1" targetRef="end">
      <conditionExpression xsi:type="tFormalExpression">
        <![CDATA[${result == 1}]]>
      </conditionExpression>
    </sequenceFlow>

    <!-- 驳回 -->
    <sequenceFlow id="approveReject" name="驳回" sourceRef="gateway1" targetRef="applyTask">
      <conditionExpression xsi:type="tFormalExpression">
        <![CDATA[${result == -1}]]>
      </conditionExpression>
    </sequenceFlow>

    <!-- 结束事件 -->
    <endEvent id="end" name="结束"/>

  </process>
</definitions>
```

### 5.2 统一任务表配置模板

```sql
-- 申请人提交节点：动态办理人（modify）
INSERT INTO dp_act_unify_task (PROC_DEF_KEY, PROC_DEF_NAME, TASK_DEF_KEY, TASK_NAME, TASK_TYPE, CANDIDATE_NAME, CANDIDATE_IDS)
VALUES ('{ProcessKey}', '{流程名称}', 'applyTask', '申请人提交', 'modify', NULL, NULL);

-- 主管审批节点：固定办理人（assignee）
INSERT INTO dp_act_unify_task (PROC_DEF_KEY, PROC_DEF_NAME, TASK_DEF_KEY, TASK_NAME, TASK_TYPE, CANDIDATE_NAME, CANDIDATE_IDS)
VALUES ('{ProcessKey}', '{流程名称}', 'approveTask', '主管审批', 'assignee', '{办理人姓名}', '{办理人ID}');

-- 或使用候选用户组（candidateGroup）
-- INSERT INTO dp_act_unify_task (PROC_DEF_KEY, PROC_DEF_NAME, TASK_DEF_KEY, TASK_NAME, TASK_TYPE, CANDIDATE_NAME, CANDIDATE_IDS)
-- VALUES ('{ProcessKey}', '{流程名称}', 'approveTask', '主管审批', 'candidateGroup', '{组名}', '{组ID}');
```

---

## 6. JSP 页面模板

### 6.1 列表页面模板

```jsp
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="container-fluid">
    <!-- 搜索栏 -->
    <div class="row" style="margin-bottom: 10px;">
        <form id="searchForm" class="form-inline">
            <div class="form-group">
                <label>流程定义：</label>
                <select name="processDefinitionKey" class="form-control">
                    <option value="">全部</option>
                    <c:forEach items="${processDefinitions}" var="pd">
                        <option value="${pd.key}">${pd.name}</option>
                    </c:forEach>
                </select>
            </div>
            <button type="button" class="btn btn-primary" onclick="search()">
                <i class="glyphicon glyphicon-search"></i> 查询
            </button>
        </form>
    </div>

    <!-- 数据表格 -->
    <table class="table table-bordered table-striped">
        <thead>
            <tr>
                <th>任务ID</th>
                <th>任务名称</th>
                <th>流程实例</th>
                <th>办理人</th>
                <th>创建时间</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${page.list}" var="vo">
                <tr>
                    <td>${vo.task.id}</td>
                    <td>${vo.task.name}</td>
                    <td>${vo.processInstance.businessKey}</td>
                    <td>${vo.task.assignee}</td>
                    <td>${vo.task.createTime}</td>
                    <td>
                        <a href="javascript:complete('${vo.task.id}')" class="btn btn-xs btn-success">
                            办理
                        </a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <!-- 分页 -->
    <div class="row">
        <div class="col-md-12 text-center">
            <ul class="pagination" id="pagination"></ul>
        </div>
    </div>
</div>

<script>
function search() {
    $('#searchForm').submit();
}

function complete(taskId) {
    window.location.href = '${ctx}/task/complete?taskId=' + taskId;
}
</script>
```

---

## 7. 接口返回值模板

### 7.1 统一返回结果

```java
public class Result<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.success = true;
        result.code = "200";
        result.data = data;
        return result;
    }

    public static <T> Result<T> success(String message) {
        Result<T> result = new Result<>();
        result.success = true;
        result.code = "200";
        result.message = message;
        return result;
    }

    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.success = false;
        result.code = "500";
        result.message = message;
        return result;
    }

    public static <T> Result<T> fail(String code, String message) {
        Result<T> result = new Result<>();
        result.success = false;
        result.code = code;
        result.message = message;
        return result;
    }

    // getter/setter 省略
}
```

### 7.2 JSON 返回格式

```json
// 成功
{
    "success": true,
    "code": "200",
    "message": "操作成功",
    "data": {
        "taskId": "12345",
        "taskName": "主管审批",
        "assignee": "zhangsan"
    }
}

// 失败
{
    "success": false,
    "code": "ACT-201",
    "message": "任务不存在或已被办理",
    "data": null
}
```

---

## 8. 异常处理模板

### 8.1 Controller 异常处理

```java
@RequestMapping(value = "/complete", method = RequestMethod.POST)
@ResponseBody
public Result<?> complete(String taskId, Integer result, String comment) {
    try {
        processService.complete(taskId, variables, comment);
        return Result.success("办理成功");
    } catch (ActivitiObjectNotFoundException e) {
        logger.error("任务不存在: taskId={}", taskId, e);
        return Result.fail("ACT-201", "任务不存在或已被办理");
    } catch (ActivitiTaskAlreadyClaimedException e) {
        logger.error("任务已被签收: taskId={}", taskId, e);
        return Result.fail("ACT-202", "任务已被他人签收");
    } catch (CustomRuntimeException e) {
        logger.error("无权办理: taskId={}", taskId, e);
        return Result.fail("ACT-203", e.getMessage());
    } catch (ActivitiException e) {
        logger.error("流程异常: taskId={}", taskId, e);
        return Result.fail("ACT-900", "流程异常: " + e.getMessage());
    } catch (Exception e) {
        logger.error("系统异常: taskId={}", taskId, e);
        return Result.fail("ACT-999", "系统异常，请联系管理员");
    }
}
```

---

## 9. 相关文档

- [code-examples.md](code-examples.md) — 代码示例
- [error-codes.md](error-codes.md) — 错误码
- [glossary.md](glossary.md) — 术语表
- [../05-standards/coding-standards.md](../05-standards/coding-standards.md) — 编码规范
- [../02-modules/controller-methods-reference.md](../02-modules/controller-methods-reference.md) — Controller 方法参考
