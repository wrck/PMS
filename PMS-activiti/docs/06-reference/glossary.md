# PMS-activiti 术语表

> 本文档汇总 PMS-activiti 模块涉及的工作流、Activiti、BPMN 相关术语，便于开发者理解。
> 数据来源：Activiti 5.23.0 官方文档、BPMN 2.0 规范、PMS-activiti 源码。

---

## 1. Activiti 核心术语

### 1.1 引擎与服务

| 术语 | 英文 | 说明 |
|------|------|------|
| 流程引擎 | ProcessEngine | Activiti 的核心引擎，所有服务的入口 |
| 流程引擎配置 | ProcessEngineConfiguration | 引擎配置类，配置数据源、事务、历史级别等 |
| 存储库服务 | RepositoryService | 管理流程定义、部署（静态数据） |
| 运行时服务 | RuntimeService | 管理流程实例、运行时变量（运行时数据） |
| 任务服务 | TaskService | 管理用户任务（签收、办理、委托） |
| 历史服务 | HistoryService | 查询历史数据（已完成流程、任务） |
| 管理服务 | ManagementService | 引擎管理（作业、数据库操作） |
| 身份服务 | IdentityService | 管理用户、用户组（Activiti 自带身份） |
| 表单服务 | FormService | 表单相关操作（PMS-activiti 未使用） |
| 命令服务 | ManagementService.executeCommand | 执行自定义命令（Command 模式） |

### 1.2 流程定义相关

| 术语 | 英文 | 说明 |
|------|------|------|
| 流程定义 | ProcessDefinition | BPMN 文件部署后的静态定义，对应 `ACT_RE_PROCDEF` |
| 流程定义键 | ProcessDefinitionKey | 流程定义的唯一标识，对应 BPMN 中 `<process id>` |
| 流程定义版本 | ProcessDefinitionVersion | 同一 Key 的版本号，每次部署自增 |
| 部署 | Deployment | 一次 BPMN 文件部署操作，对应 `ACT_RE_DEPLOYMENT` |
| 模型 | Model | 流程设计器中的模型，对应 `ACT_RE_MODEL` |
| 挂起 | Suspend | 流程定义/实例暂停运行，`SUSPENSION_STATE_ = 2` |
| 激活 | Activate | 恢复挂起的流程定义/实例，`SUSPENSION_STATE_ = 1` |

### 1.3 流程实例相关

| 术语 | 英文 | 说明 |
|------|------|------|
| 流程实例 | ProcessInstance | 流程定义的一次运行实例，对应 `ACT_RU_EXECUTION` |
| 执行实例 | Execution | 流程实例的执行路径，可嵌套（父子关系） |
| 业务键 | BusinessKey | 流程实例与业务数据的关联键，如项目编号 |
| 流程变量 | ProcessVariable | 流程运行中的数据，对应 `ACT_RU_VARIABLE` |
| 父执行 | ParentExecution | 执行实例的父级，构成执行树 |
| 根执行 | RootExecution | 流程实例本身（`PROC_INSTANCE_ID_ = ID_`） |

### 1.4 任务相关

| 术语 | 英文 | 说明 |
|------|------|------|
| 用户任务 | UserTask | 需要人工办理的任务，对应 `ACT_RU_TASK` |
| 任务定义键 | TaskDefinitionKey | BPMN 中 `userTask` 的 `id` 属性 |
| 办理人 | Assignee | 任务的直接办理人，`ACT_RU_TASK.ASSIGNEE_` |
| 候选用户 | CandidateUser | 任务的候选办理人，需签收后才能办理 |
| 候选组 | CandidateGroup | 任务的候选用户组，组内成员均可签收 |
| 拥有者 | Owner | 任务的拥有者（通常是委托前的办理人） |
| 签收 | Claim | 候选人认领任务，成为 assignee |
| 办理 | Complete | 完成任务，流程流转到下一节点 |
| 委托 | Delegate | 将任务临时交给他人办理，办理后归还 |
| 转办 | Transfer | 将任务永久转给他人办理 |
| 身份关联 | IdentityLink | 任务与用户/组的关联，对应 `ACT_RU_IDENTITYLINK` |

### 1.5 历史相关

| 术语 | 英文 | 说明 |
|------|------|------|
| 历史流程实例 | HistoricProcessInstance | 已完成或运行中的流程实例归档，对应 `ACT_HI_PROCINST` |
| 历史任务实例 | HistoricTaskInstance | 已完成任务归档，对应 `ACT_HI_TASKINST` |
| 历史活动实例 | HistoricActivityInstance | 流程经过的所有活动，对应 `ACT_HI_ACTINST` |
| 历史变量实例 | HistoricVariableInstance | 流程变量归档，对应 `ACT_HI_VARINST` |
| 历史详情 | HistoricDetail | 变量变更明细，对应 `ACT_HI_DETAIL` |
| 历史评论 | Comment | 审批意见，对应 `ACT_HI_COMMENT` |
| 历史级别 | HistoryLevel | 历史记录级别：none/activity/audit/full |

---

## 2. BPMN 2.0 术语

### 2.1 流程元素

| 术语 | 英文 | 说明 |
|------|------|------|
| 流程 | Process | BPMN 根元素，描述一个完整业务流程 |
| 开始事件 | StartEvent | 流程的起点 |
| 结束事件 | EndEvent | 流程的终点 |
| 终止结束事件 | TerminateEndEvent | 终止整个流程的结束事件 |
| 用户任务 | UserTask | 需要人工办理的任务 |
| 服务任务 | ServiceTask | 自动执行的任务（调用 Java 代码） |
| 脚本任务 | ScriptTask | 执行脚本的任务（PMS-activiti 禁用） |
| 网关 | Gateway | 流程分支/合并节点 |
| 排他网关 | ExclusiveGateway | 基于条件选择一条路径 |
| 并行网关 | ParallelGateway | 并行执行多条路径 |
| 包容网关 | InclusiveGateway | 基于条件并行执行多条路径 |
| 事件网关 | EventBasedGateway | 基于事件选择路径 |
| 序列流 | SequenceFlow | 连接两个流程元素的线 |
| 条件流 | ConditionalSequenceFlow | 带条件的序列流 |

### 2.2 事件

| 术语 | 英文 | 说明 |
|------|------|------|
| 定时事件 | TimerEvent | 基于时间触发的事件 |
| 错误事件 | ErrorEvent | 处理错误的事件 |
| 信号事件 | SignalEvent | 全局信号触发的事件 |
| 消息事件 | MessageEvent | 接收消息触发的事件 |
| 边界事件 | BoundaryEvent | 附加在活动上的事件 |
| 中间事件 | IntermediateEvent | 流程中间的事件 |

### 2.3 子流程

| 术语 | 英文 | 说明 |
|------|------|------|
| 子流程 | SubProcess | 嵌套的流程 |
| 调用活动 | CallActivity | 调用另一个流程定义 |
| 事件子流程 | EventSubProcess | 由事件触发的子流程 |
| 事务子流程 | TransactionSubProcess | 事务性子流程 |

### 2.4 多实例

| 术语 | 英文 | 说明 |
|------|------|------|
| 多实例 | MultiInstance | 一个任务多次执行（如多人会签） |
| 串行多实例 | SequentialMultiInstance | 串行执行多次 |
| 并行多实例 | ParallelMultiInstance | 并行执行多次 |
| 实例数 | nrOfInstances | 多实例总数 |
| 活跃实例数 | nrOfActiveInstances | 当前活跃实例数 |
| 已完成实例数 | nrOfCompletedInstances | 已完成实例数 |

---

## 3. PMS-activiti 自定义术语

### 3.1 命令模式

| 术语 | 英文 | 说明 |
|------|------|------|
| 撤回 | Revoke | 当前办理人从已办理任务中撤回，回到上一节点 |
| 撤销 | Withdraw | 上一节点办理人从当前办理人处撤销任务 |
| 跳转 | Jump / MoveTo | 自由跳转到指定节点 |
| 删除活动任务 | DeleteActiveTask | 删除当前运行中的任务 |
| 启动活动 | StartActivity | 启动指定活动 |
| 命令上下文 | CommandContext | 命令执行的上下文环境 |

### 3.2 监听器

| 术语 | 英文 | 说明 |
|------|------|------|
| 任务监听器 | TaskListener | 任务创建/完成/签收时触发 |
| 执行监听器 | ExecutionListener | 活动开始/结束时触发 |
| 用户任务监听器 | UserTaskListener | PMS-activiti 自定义，动态分配任务 |
| 修改申请处理器 | AfterModifyApplyProcessor | 修改申请后的处理器（已注释） |

### 3.3 统一任务表

| 术语 | 英文 | 说明 |
|------|------|------|
| 统一任务表 | dp_act_unify_task | PMS-activiti 自定义的任务分配配置表 |
| 任务类型 | TaskType | assignee/candidateUser/candidateGroup/modify |
| 候选人ID | CandidateIds | 候选人ID列表，逗号分隔 |
| 流程定义键 | ProcDefKey | 流程定义的 Key |
| 任务定义键 | TaskDefKey | BPMN 中节点的 ID |

### 3.4 业务流程

| 术语 | 英文 | 说明 |
|------|------|------|
| 回访流程 | CallBack | 客户回访审批流程 |
| 闭环流程 | PmClosedLoop | 项目闭环审批流程 |
| 售前流程 | Presales | 售前项目审批流程 |
| 转包流程 | Subcontract | 项目转包审批流程 |
| 审批结果 | result | 流程变量，1=通过，-1=驳回，2=闭环，-2=无法回访 |
| 流程状态 | flowState | 流程业务状态 |

---

## 4. 数据库表前缀

| 前缀 | 英文 | 说明 |
|------|------|------|
| `ACT_RE_` | Repository | 存储库表，存储流程定义等静态数据 |
| `ACT_RU_` | Runtime | 运行时表，存储运行中的数据 |
| `ACT_HI_` | History | 历史表，存储已完成的数据 |
| `ACT_ID_` | Identity | 身份表，存储用户与用户组 |
| `ACT_GE_` | General | 通用表，存储字节数组与属性 |

---

## 5. Spring 集成术语

| 术语 | 英文 | 说明 |
|------|------|------|
| 流程引擎配置 | SpringProcessEngineConfiguration | Spring 集成的引擎配置 |
| 流程引擎工厂 | ProcessEngineFactoryBean | Spring Bean 工厂，创建 ProcessEngine |
| 事务管理器 | DataSourceTransactionManager | Spring 事务管理器 |
| 数据源 | DataSource | 数据库连接池（commons-dbcp） |
| 组件扫描 | ComponentScan | Spring 自动扫描 `@Component` 注解 |
| 委托表达式 | delegateExpression | BPMN 中通过 Spring Bean 引用监听器 |

---

## 6. 设计模式术语

| 术语 | 英文 | 说明 |
|------|------|------|
| 命令模式 | Command Pattern | 封装操作为对象，PMS-activiti 用于自定义命令 |
| 监听器模式 | Listener Pattern | 事件触发回调，PMS-activiti 用于任务分配 |
| 工厂模式 | Factory Pattern | 创建对象，ProcessEngineFactoryBean |
| 代理模式 | Proxy Pattern | Spring AOP 事务代理 |
| 模板方法 | Template Method | AbstractBaseService 定义骨架 |
| 依赖注入 | Dependency Injection | Spring IoC 注入依赖 |

---

## 7. 性能与运维术语

| 术语 | 英文 | 说明 |
|------|------|------|
| 连接池 | Connection Pool | 数据库连接复用池 |
| 慢查询 | Slow Query | 执行时间超过阈值的 SQL |
| 死锁 | Deadlock | 两个事务互相等待对方释放锁 |
| 索引 | Index | 加速查询的数据结构 |
| 唯一索引 | Unique Index | 强制唯一性的索引 |
| 复合索引 | Composite Index | 多字段组合索引 |
| 历史清理 | History Cleanup | 定期清理历史数据 |
| 流程图生成器 | ProcessDiagramGenerator | 生成流程图 PNG 的工具类 |

---

## 8. 缩写对照表

| 缩写 | 全称 | 说明 |
|------|------|------|
| BPMN | Business Process Model and Notation | 业务流程建模标记法 |
| BPM | Business Process Management | 业务流程管理 |
| WF | Workflow | 工作流 |
| API | Application Programming Interface | 应用编程接口 |
| CRUD | Create Read Update Delete | 增删改查 |
| ER | Entity-Relationship | 实体关系 |
| DDL | Data Definition Language | 数据定义语言 |
| DML | Data Manipulation Language | 数据操作语言 |
| TPS | Transactions Per Second | 每秒事务数 |
| GC | Garbage Collection | 垃圾回收 |
| OOM | Out Of Memory | 内存溢出 |
| JSP | JavaServer Pages | Java 服务器页面 |
| MVC | Model-View-Controller | 模型-视图-控制器 |
| IoC | Inversion of Control | 控制反转 |
| AOP | Aspect-Oriented Programming | 面向切面编程 |
| ORM | Object-Relational Mapping | 对象关系映射 |
| DBCP | Database Connection Pool | 数据库连接池 |
| PNG | Portable Network Graphics | 便携式网络图形 |
| XML | eXtensible Markup Language | 可扩展标记语言 |
| JSON | JavaScript Object Notation | JavaScript 对象表示法 |

---

## 9. 相关文档

- [error-codes.md](error-codes.md) — 错误码
- [interface-template.md](interface-template.md) — 接口模板
- [code-examples.md](code-examples.md) — 代码示例
- [../01-architecture/system-architecture.md](../01-architecture/system-architecture.md) — 系统架构
- [../03-database/database-overview.md](../03-database/database-overview.md) — 数据库概览
