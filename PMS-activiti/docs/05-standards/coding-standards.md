# PMS-activiti 编码规范文档

---

## 1. Controller 层规范

### 1.1 URL 映射规范

```java
@Controller
@RequestMapping("/processDefinition")
public class ProcessDefinitionController {
    
    @RequestMapping("/list")
    public String list(Model model) { ... }
    
    @RequestMapping("/deploy")
    @ResponseBody
    public Result<?> deploy(@RequestParam MultipartFile file) { ... }
}
```

### 1.2 返回值规范

- 页面跳转：返回 JSP 视图名称
- JSON 数据：使用 `@ResponseBody` 注解
- 统一结果：使用 `Result<?>` 封装

---

## 2. Service 层规范

### 2.1 接口与实现分离

```java
// 接口
public interface IWorkflowService {
    void startProcess(String processKey, Map<String, Object> variables);
    void completeTask(String taskId, Map<String, Object> variables);
    List<TaskVO> getTodoTasks(String userId);
}

// 实现
@Service
public class WorkflowServiceImpl implements IWorkflowService {
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;
    
    @Override
    public void startProcess(String processKey, Map<String, Object> variables) {
        runtimeService.startProcessInstanceByKey(processKey, variables);
    }
}
```

### 2.2 事务管理

```java
@Service
public class WorkflowServiceImpl implements IWorkflowService {
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeTask(String taskId, Map<String, Object> variables) {
        taskService.complete(taskId, variables);
    }
}
```

---

## 3. 命令模式规范

### 3.1 自定义命令

```java
public class WithdrawTaskCmd implements Command<Void> {
    private String taskId;
    private String userId;
    
    @Override
    public Void execute(CommandContext commandContext) {
        // 业务逻辑
        return null;
    }
}
```

### 3.2 命令调用

```java
// 通过 ManagementService 执行命令
managementService.executeCommand(new WithdrawTaskCmd(taskId, userId));
```

---

## 4. 监听器规范

### 4.1 任务监听器

```java
public class UserTaskListener implements TaskListener {
    
    @Override
    public void notify(DelegateTask delegateTask) {
        String eventName = delegateTask.getEventName();
        
        if (TaskListener.EVENTNAME_CREATE.equals(eventName)) {
            // 任务创建时的处理
        } else if (TaskListener.EVENTNAME_COMPLETE.equals(eventName)) {
            // 任务完成时的处理
        }
    }
}
```

### 4.2 执行监听器

```java
public class ExecutionListener implements org.activiti.engine.delegate.ExecutionListener {
    
    @Override
    public void notify(DelegateExecution execution) {
        String eventName = execution.getEventName();
        // 处理执行事件
    }
}
```

---

## 5. 流程变量规范

### 5.1 变量命名

- 使用驼峰命名：`userId`, `projectId`
- 布尔变量：`isApproved`, `isComplete`
- 时间变量：`startTime`, `endTime`

### 5.2 变量类型

```java
// 字符串
variables.put("userId", "admin");

// 整数
variables.put("projectId", 123);

// 日期
variables.put("startTime", new Date());

// JSON 对象
variables.put("formData", JSONObject);
```

---

## 6. 日志规范

```java
// 使用 SLF4J
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(WorkflowServiceImpl.class);
    
    public void startProcess(String processKey, Map<String, Object> variables) {
        log.info("启动流程: processKey={}, variables={}", processKey, variables);
        runtimeService.startProcessInstanceByKey(processKey, variables);
        log.info("流程启动成功");
    }
}
```
