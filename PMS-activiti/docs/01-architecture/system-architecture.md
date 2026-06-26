# PMS-activiti 模块架构文档

## 1. 模块概述

PMS-activiti 是 PMS 系统的工作流引擎模块，基于 Activiti 5.23.0 实现，提供流程定义、流程实例管理、任务管理、流程监控等能力。

- **包名**：`com.dp.plat.activiti`
- **打包类型**：war+jar
- **Activiti 版本**：5.23.0
- **职责**：工作流引擎核心服务

---

## 2. 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| **Activiti** | 5.23.0 | 工作流引擎 |
| **Spring** | 5.3.19 | IoC/DI 容器 |
| **MyBatis** | 3.5.9 | 数据持久化 |
| **MySQL** | 8.0.16 | 流程数据存储 |

---

## 3. 目录结构

```
PMS-activiti/src/main/java/com/dp/plat/activiti/
├── controller/          # 控制器
│   ├── ProcessDefinitionController.java  # 流程定义管理
│   ├── ProcessInstanceController.java    # 流程实例管理
│   ├── TaskController.java              # 任务管理
│   ├── ModelController.java             # 模型设计
│   └── WorkFlowSubModalController.java  # 工作流子模态框
├── converter/           # 转换器
│   ├── BpmnJsonConverter.java           # BPMN JSON 转换
│   └── CallActivityJsonConverter.java    # 调用活动转换
├── dao/                 # 数据访问
│   ├── ActUserTaskMapper.java           # 用户任务 Mapper
│   ├── VacationMapper.java              # 请假 Mapper
│   └── PerformanceMapper.java           # 绩效 Mapper
├── entity/              # 实体类
│   ├── ProcessDefinitionEntity.java     # 流程定义实体
│   ├── ProcessInstanceEntity.java       # 流程实例实体
│   ├── ActUserTask.java                 # 用户任务
│   └── ...
├── process/             # 流程扩展
│   ├── cmd/                           # 命令
│   │   ├── WithdrawTaskCmd.java         # 任务撤回命令
│   │   ├── RevokeTaskCmd.java           # 任务驳回命令
│   │   ├── JumpTaskCmdService.java      # 任务跳转命令
│   │   ├── DeleteActiveTaskCmd.java     # 删除活动任务命令
│   │   └── StartActivityCmd.java        # 启动活动命令
│   ├── behavior/                      # 行为
│   │   └── SequentialMultiInstanceBehavior.java
│   ├── listener/                      # 监听器
│   │   ├── UserTaskListener.java        # 用户任务监听器
│   │   └── AfterModifyApplyProcessor.java
│   └── exception/                      # 异常
│       └── CustomActivitiException.java
├── service/             # 服务层
│   ├── IWorkflowService.java           # 工作流服务接口
│   ├── IProcessService.java            # 流程服务接口
│   ├── IActUserTaskService.java         # 用户任务服务接口
│   ├── ITraceService.java              # 追踪服务接口
│   ├── IVacationService.java            # 请假服务接口
│   ├── IPerformanceService.java         # 绩效服务接口
│   ├── IRuntimePageService.java         # 运行时页面服务接口
│   └── impl/
│       ├── WorkflowService.java         # 工作流服务实现
│       ├── ProcessService.java          # 流程服务实现
│       ├── ActUserTaskService.java       # 用户任务服务实现
│       ├── TraceService.java            # 追踪服务实现
│       ├── VacationService.java          # 请假服务实现
│       ├── PerformanceService.java       # 绩效服务实现
│       └── RuntimePageService.java       # 运行时页面服务实现
├── utils/               # 工具类
│   ├── WorkflowUtils.java              # 工作流工具
│   ├── BeanUtils.java                  # Bean 工具
│   └── ProcessDefinitionCache.java     # 流程定义缓存
└── vo/                  # 值对象
    └── ActivityVo.java                  # 活动视图
```

---

## 4. Spring 配置

### 4.1 Activiti 引擎配置（spring-activiti.xml）

```xml
<bean id="processEngineConfiguration" 
      class="org.activiti.spring.SpringProcessEngineConfiguration">
    <property name="dataSource" ref="dataSource"/>
    <property name="transactionManager" ref="transactionManager"/>
    <property name="databaseSchema" value="ACT"/>
    <property name="databaseSchemaUpdate" value="true"/>
    <property name="jobExecutorActivate" value="true"/>
    <property name="enableDatabaseEventLogging" value="true"/>
    <!-- 生成流程图的字体 -->
    <property name="activityFontName" value="${diagram.activityFontName}"/>
    <property name="labelFontName" value="${diagram.labelFontName}"/>
    <property name="annotationFontName" value="${diagram.annotationFontName}"/>
    <property name="processDiagramGenerator" ref="customerProcessDiagramGenerator"/>
</bean>

<!-- 流程引擎工厂 -->
<bean id="processEngine" class="org.activiti.spring.ProcessEngineFactoryBean">
    <property name="processEngineConfiguration" ref="processEngineConfiguration"/>
</bean>

<!-- Activiti 服务 Bean -->
<bean id="repositoryService" factory-bean="processEngine" factory-method="getRepositoryService"/>
<bean id="runtimeService" factory-bean="processEngine" factory-method="getRuntimeService"/>
<bean id="taskService" factory-bean="processEngine" factory-method="getTaskService"/>
<bean id="historyService" factory-bean="processEngine" factory-method="getHistoryService"/>
<bean id="managementService" factory-bean="processEngine" factory-method="getManagementService"/>
<bean id="identityService" factory-bean="processEngine" factory-method="getIdentityService"/>
<bean id="formService" factory-bean="processEngine" factory-method="getFormService"/>
```

### 4.2 自定义流程图生成器

```xml
<bean id="customerProcessDiagramGenerator" 
      class="com.dp.plat.activiti.service.activiti.CustomProcessDiagramGenerator"/>
```

---

## 5. 核心功能

### 5.1 流程定义管理

**控制器**：`ProcessDefinitionController`

| 方法 | 功能 | URL |
|------|------|-----|
| list | 流程定义列表 | /processDefinition/list |
| deploy | 部署流程定义 | /processDefinition/deploy |
| delete | 删除流程定义 | /processDefinition/delete |
| export | 导出 BPMN 文件 | /processDefinition/export |

### 5.2 流程实例管理

**控制器**：`ProcessInstanceController`

| 方法 | 功能 | URL |
|------|------|-----|
| list | 流程实例列表 | /processInstance/list |
| start | 启动流程实例 | /processInstance/start |
| delete | 删除流程实例 | /processInstance/delete |
| suspend | 挂起流程实例 | /processInstance/suspend |
| activate | 激活流程实例 | /processInstance/activate |

### 5.3 任务管理

**控制器**：`TaskController`

| 方法 | 功能 | URL |
|------|------|-----|
| myTask | 我的任务 | /task/myTask |
| complete | 完成任务 | /task/complete |
| claim | 签收任务 | /task/claim |
| delegate | 委派任务 | /task/delegate |
| withdraw | 撤回任务 | /task/withdraw |
| revoke | 驳回任务 | /task/revoke |
| jump | 跳转任务 | /task/jump |
| comment | 添加评论 | /task/comment |

### 5.4 模型设计

**控制器**：`ModelController`

| 方法 | 功能 | URL |
|------|------|-----|
| list | 模型列表 | /model/list |
| create | 创建模型 | /model/create |
| edit | 编辑模型 | /model/edit |
| delete | 删除模型 | /model/delete |
| deploy | 部署模型 | /model/deploy |

---

## 6. 自定义命令

### 6.1 任务撤回（WithdrawTaskCmd）

允许已提交的任务被撤回，适用于审批人提交后发现错误的场景。

```java
// 使用方式
workflowService.withdrawTask(taskId, userId);
```

### 6.2 任务驳回（RevokeTaskCmd）

将任务驳回到上一个节点，适用于审批不通过的场景。

```java
// 使用方式
workflowService.revokeTask(taskId, userId, comment);
```

### 6.3 任务跳转（JumpTaskCmdService）

将任务跳转到任意节点，适用于流程调整的场景。

```java
// 使用方式
workflowService.jumpTask(taskId, targetActivityId, userId);
```

### 6.4 删除活动任务（DeleteActiveTaskCmd）

删除指定的活动任务，适用于流程异常处理的场景。

---

## 7. 数据库表

Activiti 引擎使用以下表：

| 表名 | 说明 |
|------|------|
| `ACT_RE_DEPLOYMENT` | 部署信息 |
| `ACT_RE_PROCDEF` | 流程定义 |
| `ACT_RU_EXECUTION` | 运行时执行 |
| `ACT_RU_TASK` | 运行时任务 |
| `ACT_RU_VARIABLE` | 运行时变量 |
| `ACT_RU_IDENTITYLINK` | 运行时身份关联 |
| `ACT_HI_PROCINST` | 历史流程实例 |
| `ACT_HI_TASKINST` | 历史任务实例 |
| `ACT_HI_ACTINST` | 历史活动实例 |
| `ACT_HI_VARINST` | 历史变量实例 |
| `ACT_GE_BYTEARRAY` | 通用字节数组 |
| `ACT_GE_PROPERTY` | 通用属性 |

---

## 8. 与其他模块集成

PMS-activiti 通过 Maven 依赖被 PMS-springmvc 引用：

```xml
<dependency>
    <groupId>com.dp.plat</groupId>
    <artifactId>pms-activiti</artifactId>
    <version>${project.version}</version>
</dependency>
```

PMS-struts 通过 API 调用工作流服务：

```java
// 在 PMS-struts 的 WorkFlowService 中
@Autowired
private IWorkflowService workflowService;

public void startProcess(String processKey, Map<String, Object> variables) {
    workflowService.startProcess(processKey, variables);
}
```
