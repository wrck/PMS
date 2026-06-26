# PMS-activiti 代码示例与参考

---

## 1. 流程定义管理示例

### 1.1 部署流程定义

```java
// 从文件部署
repositoryService.createDeployment()
    .addClasspathResource("processes/project-workflow.bpmn20.xml")
    .deploy();

// 从输入流部署
InputStream inputStream = new FileInputStream("process.bpmn20.xml");
repositoryService.createDeployment()
    .addInputStream("process.bpmn20.xml", inputStream)
    .deploy();
```

### 1.2 查询流程定义

```java
// 查询所有流程定义
List<ProcessDefinition> definitions = repositoryService
    .createProcessDefinitionQuery()
    .list();

// 按 Key 查询
ProcessDefinition definition = repositoryService
    .createProcessDefinitionQuery()
    .processDefinitionKey("projectWorkflow")
    .latestVersion()
    .singleResult();
```

---

## 2. 流程实例管理示例

### 2.1 启动流程实例

```java
// 按 Key 启动
Map<String, Object> variables = new HashMap<>();
variables.put("projectId", 123);
variables.put("userId", "admin");

ProcessInstance instance = runtimeService
    .startProcessInstanceByKey("projectWorkflow", variables);

// 按业务 Key 启动
ProcessInstance instance = runtimeService
    .startProcessInstanceByProcessDefinitionKey("projectWorkflow", "businessKey", variables);
```

### 2.2 查询流程实例

```java
// 查询运行中的实例
List<ProcessInstance> instances = runtimeService
    .createProcessInstanceQuery()
    .processDefinitionKey("projectWorkflow")
    .list();

// 按业务 Key 查询
ProcessInstance instance = runtimeService
    .createProcessInstanceQuery()
    .processInstanceBusinessKey("businessKey")
    .singleResult();
```

---

## 3. 任务管理示例

### 3.1 查询任务

```java
// 查询待办任务
List<Task> tasks = taskService
    .createTaskQuery()
    .taskAssignee("admin")
    .list();

// 按流程实例查询
List<Task> tasks = taskService
    .createTaskQuery()
    .processInstanceId(processInstanceId)
    .list();
```

### 3.2 完成任务

```java
// 完成任务
Map<String, Object> variables = new HashMap<>();
variables.put("action", "approve");
variables.put("comment", "同意");

taskService.complete(taskId, variables);
```

### 3.3 签收任务

```java
// 签收任务
taskService.claim(taskId, "admin");
```

### 3.4 委派任务

```java
// 委派任务
taskService.delegateTask(taskId, "user1");
```

---

## 4. 历史数据查询示例

### 4.1 查询历史流程实例

```java
List<HistoricProcessInstance> instances = historyService
    .createHistoricProcessInstanceQuery()
    .processDefinitionKey("projectWorkflow")
    .finished()
    .list();
```

### 4.2 查询历史任务

```java
List<HistoricTaskInstance> tasks = historyService
    .createHistoricTaskInstanceQuery()
    .taskAssignee("admin")
    .finished()
    .list();
```

---

## 5. 流程图生成示例

```java
// 生成流程图
InputStream diagramStream = managementService
    .getProcessDiagram(processInstanceId);

// 保存为文件
Files.copy(diagramStream, Paths.get("process.png"));
```

---

## 6. 自定义命令示例

```java
// 定义命令
public class WithdrawTaskCmd implements Command<Void> {
    private String taskId;
    private String userId;
    
    public WithdrawTaskCmd(String taskId, String userId) {
        this.taskId = taskId;
        this.userId = userId;
    }
    
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

// 执行命令
managementService.executeCommand(new WithdrawTaskCmd(taskId, userId));
```
