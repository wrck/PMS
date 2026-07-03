# PMS-activiti 错误码参考

> 本文档汇总 PMS-activiti 模块的错误码、异常类、错误信息和处理建议。
> 数据来源：`d:\常规软件\QoderCode\workspace\PMS\PMS-activiti\src\` 源码与 Activiti 5.23.0 官方文档。

> ⚠️ **重要声明**（2026-07-01 审查）：以下 ACT-XXX 错误码分类体系为**教学化示例**，PMS-activiti 源码中**不存在**任何 ACT-XXX 错误码定义。源码实际异常处理方式：Controller 层使用 try-catch + `ExceptionHandler.insertException(e)` 捕获 Activiti 原生异常（如 `ActivitiObjectNotFoundException`、`ActivitiTaskAlreadyClaimedException`）。PMS-activiti 自定义异常类为 `CustomActivitiException`（继承 `ActivitiException`），**不使用** `CustomRuntimeException`（该类存在于 core 模块但 PMS-activiti 未引用）。本文档中的错误码和异常用法仅作为设计参考，不代表源码实际实现。

---

## 1. 错误码分类

| 错误码范围 | 分类 | 说明 |
|-----------|------|------|
| `ACT-001` ~ `ACT-099` | 流程定义错误 | 部署、查询、挂起相关 |
| `ACT-100` ~ `ACT-199` | 流程实例错误 | 启动、查询、终止相关 |
| `ACT-200` ~ `ACT-299` | 任务错误 | 签收、办理、委托、转办相关 |
| `ACT-300` ~ `ACT-399` | 撤回/撤销/跳转错误 | 自定义命令相关 |
| `ACT-400` ~ `ACT-499` | 历史数据错误 | 历史查询相关 |
| `ACT-500` ~ `ACT-599` | 数据库错误 | 连接池、死锁相关 |
| `ACT-600` ~ `ACT-699` | 配置错误 | Spring 配置、监听器配置相关 |
| `ACT-900` ~ `ACT-999` | 系统错误 | 未知异常 |

---

## 2. 流程定义错误（ACT-001 ~ ACT-099）

### ACT-001 流程定义不存在

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-001 |
| 异常类 | `org.activiti.engine.ActivitiObjectNotFoundException` |
| 错误信息 | `no processes deployed with key '{key}'` |
| 触发场景 | `runtimeService.startProcessInstanceByKey(key)` 时流程定义未部署 |
| 排查方向 | 检查 `ACT_RE_PROCDEF` 表是否存在 `KEY_ = {key}` 的记录 |
| 解决方案 | 部署流程定义或核对流程定义 Key |

### ACT-002 BPMN 解析失败

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-002 |
| 异常类 | `org.activiti.bpmn.exceptions.XMLException` |
| 错误信息 | `Error parsing XML: {detail}` |
| 触发场景 | 部署 BPMN 文件时 XML 格式错误 |
| 排查方向 | 使用 BPMN 编辑器校验文件格式 |
| 解决方案 | 修正 BPMN XML 格式错误 |

### ACT-003 流程定义已挂起

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-003 |
| 异常类 | `org.activiti.engine.ActivitiException` |
| 错误信息 | `Cannot start process instance. ProcessDefinition {id} is suspended` |
| 触发场景 | 启动已挂起的流程定义 |
| 排查方向 | 检查 `ACT_RE_PROCDEF.SUSPENSION_STATE_ = 2` |
| 解决方案 | `repositoryService.activateProcessDefinitionById(id)` |

### ACT-004 部署失败 - 资源过大

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-004 |
| 异常类 | `org.activiti.engine.ActivitiException` |
| 错误信息 | `Resource too large` |
| 触发场景 | BPMN 文件含嵌入图片，超过数据库字段限制 |
| 排查方向 | 检查 BPMN 文件大小 |
| 解决方案 | 移除嵌入图片，使用外部图片引用 |

---

## 3. 流程实例错误（ACT-100 ~ ACT-199）

### ACT-101 流程实例不存在

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-101 |
| 异常类 | `org.activiti.engine.ActivitiObjectNotFoundException` |
| 错误信息 | `No process instance found for id '{id}'` |
| 触发场景 | 查询或操作不存在的流程实例 |
| 排查方向 | 检查 `ACT_RU_EXECUTION` 和 `ACT_HI_PROCINST` |
| 解决方案 | 核对流程实例 ID |

### ACT-102 流程实例已结束

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-102 |
| 异常类 | `org.activiti.engine.ActivitiException` |
| 错误信息 | `process instance is already ended` |
| 触发场景 | 对已结束的流程实例执行操作 |
| 排查方向 | 检查 `ACT_HI_PROCINST.END_TIME_` 是否非空 |
| 解决方案 | 使用历史查询代替运行时查询 |

### ACT-103 流程变量缺失

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-103 |
| 异常类 | `org.activiti.engine.ActivitiException` |
| 错误信息 | `Unknown property used in expression: {expression}` |
| 触发场景 | BPMN 表达式引用的变量未设置 |
| 排查方向 | 检查 `ACT_RU_VARIABLE` 是否包含所需变量 |
| 解决方案 | 启动流程时传入所有必需变量 |

### ACT-104 业务键重复

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-104 |
| 异常类 | `org.activiti.engine.ActivitiException` |
| 错误信息 | `Duplicate business key` |
| 触发场景 | 同一业务键启动多个流程实例（唯一约束） |
| 排查方向 | 检查 `ACT_RU_EXECUTION.BUSINESS_KEY_` |
| 解决方案 | 使用不同的业务键或移除唯一约束 |

---

## 4. 任务错误（ACT-200 ~ ACT-299）

### ACT-201 任务不存在

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-201 |
| 异常类 | `org.activiti.engine.ActivitiObjectNotFoundException` |
| 错误信息 | `Cannot find task with id '{id}'` |
| 触发场景 | `taskService.complete(taskId)` 时任务不存在 |
| 排查方向 | 检查 `ACT_RU_TASK`，确认任务是否已被办理 |
| 解决方案 | 刷新待办列表，避免操作已失效任务 |

### ACT-202 任务已被签收

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-202 |
| 异常类 | `org.activiti.engine.ActivitiTaskAlreadyClaimedException` |
| 错误信息 | `Task '{id}' is already claimed by someone else` |
| 触发场景 | 并发签收同一任务 |
| 排查方向 | 检查 `ACT_RU_TASK.ASSIGNEE_` |
| 解决方案 | 提示用户任务已被他人签收 |

### ACT-203 任务办理人无权限

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-203 |
| 异常类 | `com.dp.plat.core.exception.CustomRuntimeException` |
| 错误信息 | `无权办理此任务` |
| 触发场景 | 非任务办理人尝试办理任务 |
| 排查方向 | 检查当前用户是否为 `ASSIGNEE_` 或候选人 |
| 解决方案 | 确认用户权限或重新分配任务 |

### ACT-204 任务 ASSIGNEE 为空

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-204 |
| 异常类 | 无（业务异常） |
| 错误信息 | `任务创建后 ASSIGNEE_ 为空` |
| 触发场景 | `UserTaskListener` 未正确分配任务 |
| 排查方向 | 检查 `dp_act_unify_task` 配置与 BPMN 监听器 |
| 解决方案 | 参见 [troubleshooting.md](../05-standards/troubleshooting.md) 2.5 节 |

### ACT-205 委托失败

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-205 |
| 异常类 | `org.activiti.engine.ActivitiException` |
| 错误信息 | `Delegate failed` |
| 触发场景 | 委托任务时目标用户不存在 |
| 排查方向 | 检查目标用户 ID |
| 解决方案 | 确认目标用户存在 |

---

## 5. 撤回/撤销/跳转错误（ACT-300 ~ ACT-399）

### ACT-301 撤回失败 - 返回 0

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-301 |
| 异常类 | 无（返回值） |
| 错误信息 | `RevokeTaskCmd 返回 0` |
| 触发场景 | 上一任务不存在或当前任务已结束 |
| 排查方向 | 检查 `ACT_HI_TASKINST` 是否有历史任务 |
| 解决方案 | 确认流程至少办理过一步 |

### ACT-302 撤回失败 - 返回 2

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-302 |
| 异常类 | 无（返回值） |
| 错误信息 | `RevokeTaskCmd 返回 2` |
| 触发场景 | 不满足撤回条件 |
| 排查方向 | 检查当前任务是否已结束 |
| 解决方案 | 确认当前任务未办理 |

### ACT-303 撤销失败 - 多实例任务

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-303 |
| 异常类 | `org.activiti.engine.ActivitiException` |
| 错误信息 | `Withdraw multi-instance task failed` |
| 触发场景 | 撤销多实例任务时逻辑错误 |
| 排查方向 | 检查 `SequentialMultiInstanceBehavior` |
| 解决方案 | 参见 [custom-commands.md](../02-modules/custom-commands.md) |

### ACT-304 跳转失败 - 目标节点不存在

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-304 |
| 异常类 | `org.activiti.engine.ActivitiException` |
| 错误信息 | `Target activity '{id}' not found` |
| 触发场景 | 跳转的目标节点 ID 不存在 |
| 排查方向 | 检查 BPMN 中的节点 ID |
| 解决方案 | 核对目标 `activityId` |

### ACT-305 跳转失败 - 当前任务已结束

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-305 |
| 异常类 | `org.activiti.engine.ActivitiException` |
| 错误信息 | `Task already ended` |
| 触发场景 | 跳转时当前任务已被办理 |
| 排查方向 | 检查 `ACT_RU_TASK` 是否存在 |
| 解决方案 | 确认任务未结束 |

---

## 6. 历史数据错误（ACT-400 ~ ACT-499）

### ACT-401 历史数据缺失

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-401 |
| 异常类 | 无（查询结果为空） |
| 错误信息 | `历史数据缺失` |
| 触发场景 | 查询历史任务时无记录 |
| 排查方向 | 检查 `history.level` 配置 |
| 解决方案 | 确认 `history.level=full` 或 `audit` |

### ACT-402 历史查询超时

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-402 |
| 异常类 | `java.sql.SQLException` |
| 错误信息 | `Query timeout` |
| 触发场景 | 历史表数据量大，查询超时 |
| 排查方向 | 检查 `ACT_HI_*` 表数据量 |
| 解决方案 | 添加索引、限定时间范围、定期清理 |

---

## 7. 数据库错误（ACT-500 ~ ACT-599）

### ACT-501 连接池耗尽

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-501 |
| 异常类 | `org.apache.commons.dbcp.SQLNestedException` |
| 错误信息 | `Cannot get a connection, pool exhausted` |
| 触发场景 | 连接池无可用连接 |
| 排查方向 | 检查 `SHOW PROCESSLIST`、慢查询 |
| 解决方案 | 增加连接池上限、优化慢查询、重启应用 |

### ACT-502 数据库死锁

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-502 |
| 异常类 | `java.sql.SQLException` |
| 错误信息 | `Deadlock found when trying to get lock` |
| 触发场景 | 并发更新同一流程实例 |
| 排查方向 | `SHOW ENGINE INNODB STATUS` |
| 解决方案 | 避免并发操作、缩小事务范围 |

### ACT-503 唯一约束冲突

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-503 |
| 异常类 | `java.sql.SQLIntegrityConstraintViolationException` |
| 错误信息 | `Duplicate entry '{value}' for key '{key}'` |
| 触发场景 | 重复部署流程定义或重复业务键 |
| 排查方向 | 检查唯一索引 |
| 解决方案 | 使用不同 Key 或版本号 |

### ACT-504 字段长度超限

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-504 |
| 异常类 | `java.sql.SQLException` |
| 错误信息 | `Data truncation: Data too long for column '{column}'` |
| 触发场景 | 输入数据超过字段定义长度 |
| 排查方向 | 检查字段长度定义 |
| 解决方案 | 增加字段长度或前端校验 |

---

## 8. 配置错误（ACT-600 ~ ACT-699）

### ACT-601 Spring Bean 注入失败

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-601 |
| 异常类 | `org.springframework.beans.factory.NoSuchBeanDefinitionException` |
| 错误信息 | `No bean named '{name}' available` |
| 触发场景 | `UserTaskListener` 未注册为 Spring Bean |
| 排查方向 | 检查 `@Component` 注解与组件扫描 |
| 解决方案 | 确认 `spring-activiti-mvc.xml` 配置了 `<context:component-scan>` |

### ACT-602 数据源配置错误

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-602 |
| 异常类 | `org.springframework.beans.factory.BeanCreationException` |
| 错误信息 | `Error creating bean with name 'dataSource'` |
| 触发场景 | `db.properties` 配置错误 |
| 排查方向 | 检查数据库连接参数 |
| 解决方案 | 核对 `db.url`, `db.username`, `db.password` |

### ACT-603 事务管理器配置错误

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-603 |
| 异常类 | `org.springframework.transaction.CannotCreateTransactionException` |
| 错误信息 | `Could not open JDBC Connection for transaction` |
| 触发场景 | 事务管理器与数据源不匹配 |
| 排查方向 | 检查 `spring-activiti.xml` 中 `transactionManager` 配置 |
| 解决方案 | 确认 `DataSourceTransactionManager` 引用正确的 `dataSource` |

### ACT-604 字体配置错误

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-604 |
| 异常类 | 无（显示异常） |
| 错误信息 | `流程图中文乱码` |
| 触发场景 | 服务器未安装中文字体 |
| 排查方向 | 检查 `engine.properties` 字体配置 |
| 解决方案 | 安装中文字体、配置 `activityFontName=宋体` |

---

## 9. 系统错误（ACT-900 ~ ACT-999）

### ACT-901 空指针异常

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-901 |
| 异常类 | `java.lang.NullPointerException` |
| 错误信息 | `NullPointerException at {location}` |
| 触发场景 | 对象未初始化或查询结果为空 |
| 排查方向 | 查看异常堆栈定位空指针位置 |
| 解决方案 | 添加空值判断 |

### ACT-902 类转换异常

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-902 |
| 异常类 | `java.lang.ClassCastException` |
| 错误信息 | `Cannot cast {from} to {to}` |
| 触发场景 | 流程变量类型转换错误 |
| 排查方向 | 检查 `ACT_RU_VARIABLE.TYPE_` |
| 解决方案 | 确保变量类型一致 |

### ACT-903 序列化异常

| 属性 | 值 |
|------|-----|
| 错误码 | ACT-903 |
| 异常类 | `java.io.NotSerializableException` |
| 错误信息 | `{class} is not serializable` |
| 触发场景 | 流程变量对象未实现 `Serializable` |
| 排查方向 | 检查 `BaseVO` 是否实现 `Serializable` |
| 解决方案 | 实现 `Serializable` 接口 |

---

## 10. 错误码快速查询表

| 错误码 | 异常类 | 简述 | 严重程度 |
|--------|--------|------|----------|
| ACT-001 | `ActivitiObjectNotFoundException` | 流程定义不存在 | 高 |
| ACT-002 | `XMLException` | BPMN 解析失败 | 中 |
| ACT-003 | `ActivitiException` | 流程定义已挂起 | 中 |
| ACT-101 | `ActivitiObjectNotFoundException` | 流程实例不存在 | 高 |
| ACT-102 | `ActivitiException` | 流程实例已结束 | 中 |
| ACT-103 | `ActivitiException` | 流程变量缺失 | 高 |
| ACT-201 | `ActivitiObjectNotFoundException` | 任务不存在 | 高 |
| ACT-202 | `ActivitiTaskAlreadyClaimedException` | 任务已被签收 | 中 |
| ACT-203 | `CustomRuntimeException` | 任务办理人无权限 | 高 |
| ACT-204 | 无 | 任务 ASSIGNEE 为空 | 高 |
| ACT-301 | 无（返回值） | 撤回失败-返回0 | 中 |
| ACT-302 | 无（返回值） | 撤回失败-返回2 | 中 |
| ACT-304 | `ActivitiException` | 跳转目标节点不存在 | 高 |
| ACT-401 | 无 | 历史数据缺失 | 中 |
| ACT-402 | `SQLException` | 历史查询超时 | 中 |
| ACT-501 | `SQLNestedException` | 连接池耗尽 | 高 |
| ACT-502 | `SQLException` | 数据库死锁 | 高 |
| ACT-503 | `SQLIntegrityConstraintViolationException` | 唯一约束冲突 | 中 |
| ACT-601 | `NoSuchBeanDefinitionException` | Bean 注入失败 | 高 |
| ACT-604 | 无 | 字体配置错误 | 低 |
| ACT-901 | `NullPointerException` | 空指针异常 | 高 |
| ACT-902 | `ClassCastException` | 类转换异常 | 高 |
| ACT-903 | `NotSerializableException` | 序列化异常 | 中 |

---

## 11. 异常处理最佳实践

### 11.1 Controller 层异常处理

```java
@Controller
public class TaskController {
    
    @RequestMapping("/complete")
    @ResponseBody
    public Result<?> complete(String taskId, Integer result, String comment) {
        try {
            processService.complete(taskId, variables, comment);
            return Result.success();
        } catch (ActivitiObjectNotFoundException e) {
            logger.error("任务不存在: taskId={}", taskId, e);
            return Result.fail("ACT-201", "任务不存在或已被办理");
        } catch (ActivitiTaskAlreadyClaimedException e) {
            logger.error("任务已被签收: taskId={}", taskId, e);
            return Result.fail("ACT-202", "任务已被他人签收");
        } catch (CustomRuntimeException e) {
            logger.error("任务办理失败: taskId={}", taskId, e);
            return Result.fail("ACT-203", e.getMessage());
        } catch (Exception e) {
            logger.error("任务办理异常: taskId={}", taskId, e);
            return Result.fail("ACT-900", "系统异常，请联系管理员");
        }
    }
}
```

### 11.2 全局异常处理

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ActivitiObjectNotFoundException.class)
    @ResponseBody
    public Result<?> handleNotFound(ActivitiObjectNotFoundException e) {
        return Result.fail("ACT-001", "对象不存在: " + e.getMessage());
    }
    
    @ExceptionHandler(ActivitiException.class)
    @ResponseBody
    public Result<?> handleActivitiException(ActivitiException e) {
        return Result.fail("ACT-900", "流程异常: " + e.getMessage());
    }
}
```

---

## 12. 相关文档

- [code-examples.md](code-examples.md) — 代码示例
- [glossary.md](glossary.md) — 术语表
- [interface-template.md](interface-template.md) — 接口模板
- [../05-standards/troubleshooting.md](../05-standards/troubleshooting.md) — 故障排查
