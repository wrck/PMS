# 常见问题与故障排查文档

本文档汇总 PMS 系统运行中的常见问题、故障现象、根因分析和解决方案，涵盖多数据源事务、Activiti 流程、数据同步、连接池、Struts2 和 iBatis 等方面，并提供问题分类索引、解决方案案例库和日志分析指南。

---

## 1. 问题分类索引

### 1.1 按模块分类

| 模块 | 常见问题 | 严重程度 | 参见章节 |
|------|----------|----------|----------|
| 系统管理 | 用户登录失败、密码过期、权限异常 | 高 | 案例1、案例2 |
| 项目管理 | 项目创建失败、状态流转异常、成员变更错误 | 高 | 案例3、案例4 |
| 售前测试 | 售前流程卡住、SMS同步失败 | 中 | 案例5 |
| 闭环回访 | 闭环状态不一致、问卷提交失败 | 中 | 案例6 |
| 技术公告 | 富文本XSS、公告审核异常 | 低 | 案例7 |
| 项目转包 | D365接口调用失败、付款状态不同步 | 高 | 案例8 |
| 数据同步 | SAP/D365/OA连接超时、数据不一致 | 高 | 案例9 |
| 工作流 | 流程悬挂、任务分配异常 | 中 | 案例10 |
| 基础架构 | 连接池耗尽、内存溢出、缓存不一致 | 高 | 案例11、案例12 |

### 1.2 按异常类型分类

| 异常类型 | 典型异常类 | 常见场景 | 排查方向 |
|----------|------------|----------|----------|
| 数据库异常 | `SQLException`, `DataAccessException` | 连接池耗尽、SQL语法错误、死锁 | 检查连接池配置、SQL日志、数据库状态 |
| 事务异常 | `UnexpectedRollbackException` | 跨数据源操作、事务嵌套 | 检查ServiceAgent配置、事务方法前缀 |
| 流程异常 | `ActivitiException` | 流程定义错误、任务分配失败 | 检查ACT_RU_TASK、流程定义XML |
| 网络异常 | `SocketTimeoutException`, `ConnectException` | 外部系统连接超时 | 检查网络连通性、防火墙、超时配置 |
| 序列化异常 | `JsonProcessingException` | customInfo JSON解析失败 | 检查JSON格式、TypeHandler配置 |
| 权限异常 | `CustomRuntimeException` | 无操作权限、用户未登录 | 检查UserContext、菜单权限配置 |
| 并发异常 | `ConcurrentModificationException` | 多线程操作共享数据 | 检查synchronized、DAO scope配置 |
| OGNL异常 | `OgnlException` | Struts2参数绑定失败 | 检查Action属性、参数类型匹配 |

### 1.3 按影响范围分类

| 影响范围 | 典型问题 | 处理优先级 | 处理方式 |
|----------|----------|------------|----------|
| 全系统不可用 | 数据库连接池耗尽、内存溢出 | P0-紧急 | 立即重启+根因分析 |
| 单模块不可用 | 数据同步失败、工作流异常 | P1-高 | 2小时内修复 |
| 单功能异常 | 项目创建失败、文件上传失败 | P2-中 | 1个工作日内修复 |
| 体验问题 | 页面加载慢、缓存不一致 | P3-低 | 排入迭代修复 |
| 安全隐患 | XSS漏洞、SQL注入风险 | P1-高 | 立即修复 |

---

## 2. 解决方案案例库

### 案例1：用户登录失败 - 密码加密方式不匹配

**问题现象**：用户使用正确密码登录，系统提示"用户名或密码错误"。

**根因分析**：旧版用户使用 `Md5Util.getMD5()` 加密密码（无盐值），新版使用 `PasswordUtil.encryptPassword()` 加密（SHA1+MD5加盐迭代），两种加密结果不同，导致密码比对失败。

**解决步骤**：
1. 查询数据库确认密码加密格式：`SELECT username, password FROM fnd_user_info WHERE username = 'xxx'`
2. 判断密码长度：32位十六进制可能是旧版MD5或新版加密
3. 使用旧版MD5加密输入密码，比对是否匹配
4. 如匹配旧版，更新为新版加密：`UPDATE fnd_user_info SET password = PasswordUtil.encryptPassword(username, inputPassword) WHERE username = 'xxx'`

**预防措施**：
- 新创建用户统一使用 `PasswordUtil.encryptPassword()`
- 在登录逻辑中增加兼容性判断：先尝试新版加密比对，失败后尝试旧版MD5比对
- 旧版密码登录成功后自动升级为新版加密

### 案例2：密码过期重定向循环

**问题现象**：用户登录后被反复重定向到密码修改页面，无法正常使用系统。

**根因分析**：`UserCheckFilter` 检查密码过期后重定向到密码修改页面，但密码修改页面的URL未加入排除规则，导致修改页面也被拦截，形成重定向循环。

**解决步骤**：
1. 检查 `sys.change.password.redirect.excludeUrls` 配置
2. 确认密码修改页面URL已加入排除列表
3. 检查 `pwdoverdue` 字段是否正确更新

**预防措施**：
- 密码修改成功后立即更新 `pwdoverdue` 字段
- 新增页面URL时检查是否需要加入排除规则
- 对重定向逻辑增加循环检测（最多3次重定向）

### 案例3：项目创建失败 - 合同号重复

**问题现象**：用户创建项目时系统报错"合同号已创建项目"，但项目列表中找不到对应项目。

**根因分析**：
1. 项目可能已被创建但处于"不予跟踪"状态（STATE_20），在默认列表中不显示
2. 项目可能已被删除（effectiveTo非空），但仍占用合同号
3. 并发创建导致同一合同号被两个请求同时使用

**解决步骤**：
1. 查询包含已删除和不跟踪的项目：`SELECT * FROM pm_project_header WHERE projectCode IN (SELECT projectCode FROM pm_project_contract WHERE contractNo = 'xxx')`
2. 检查项目状态：`SELECT * FROM pm_project_state WHERE projectId = 'xxx'`
3. 如果项目已删除，可恢复项目（将effectiveTo设为NULL）
4. 如果项目为"不予跟踪"状态，可重新激活

**预防措施**：
- 项目列表增加"包含已删除"和"包含不予跟踪"的筛选选项
- 合同号查询时同时检查已删除的项目
- 创建项目时使用数据库唯一索引防止并发重复

### 案例4：项目状态流转异常

**问题现象**：项目成员变更后，项目状态未按预期更新。例如指派SM后状态仍为STATE_30。

**根因分析**：状态机计算逻辑依赖当前有效成员的角色分布，以下情况可能导致状态不更新：
1. 新成员已创建但旧成员未失效（effectiveTo未设置）
2. 成员角色编码不在预期范围内（非10/20/30/40/50/60/70）
3. 状态更新SQL的WHERE条件未匹配到记录

**解决步骤**：
1. 查询项目当前有效成员：`SELECT * FROM pm_project_member WHERE projectId = 'xxx' AND effectiveTo IS NULL`
2. 检查成员角色编码是否正确
3. 检查pm_project_state当前值
4. 手动修正状态或重新执行成员变更操作

**预防措施**：
- 成员变更时确保先失效旧成员再创建新成员（在同一事务中）
- 状态机计算后验证状态值是否合理
- 增加状态变更日志，便于追踪状态流转历史

### 案例5：售前项目SMS同步失败

**问题现象**：SMS系统数据更新后，PMS中售前项目信息未同步更新。

**根因分析**：
1. SMS数据库视图结构变更，SQL映射中引用的列名不存在
2. SMS数据库连接超时或不可用
3. 同步任务未按预期执行（Quartz调度异常）

**解决步骤**：
1. 检查 `fnd_data_refresh_log` 中SMS同步记录：`SELECT * FROM fnd_data_refresh_log WHERE syncType = 'SMS' ORDER BY createTime DESC LIMIT 5`
2. 检查SMS数据库连接：在PMS服务器上 `telnet <sms_host> <sms_port>`
3. 检查Quartz调度状态：查看定时任务执行日志
4. 手动触发同步任务进行验证

**预防措施**：
- 建立与SMS团队的变更通知机制
- 同步任务增加异常告警（连续3次失败发送邮件通知）
- SQL映射中使用列别名增加容错

### 案例6：闭环流程状态不一致

**问题现象**：闭环评价表(pm_cl_evaluation_header)的processStatus与项目状态表(pm_project_state)的closeProcessState不一致。

**根因分析**：闭环操作更新了pm_cl_evaluation_header但未同步更新pm_project_state，可能原因：
1. 更新pm_project_state的SQL执行失败但未抛出异常
2. 两个更新操作不在同一事务中
3. 并发操作导致其中一个更新被覆盖

**解决步骤**：
1. 查询两个表的状态值：
   ```sql
   SELECT h.processStatus, s.closeProcessState
   FROM pm_cl_evaluation_header h
   JOIN pm_project_state s ON h.projectId = s.projectId
   WHERE h.projectId = 'xxx'
   ```
2. 根据processStatus修正closeProcessState（processStatus × 10 = closeProcessState）
3. 检查代码中两个更新是否在同一事务中

**预防措施**：
- 确保闭环操作的两个更新在同一Service方法中（事务保护）
- 增加状态一致性校验定时任务
- 考虑使用数据库触发器保证状态同步

### 案例7：技术公告富文本XSS过滤

**问题现象**：技术公告编辑时保存的HTML内容被XSS过滤器转义，导致页面显示HTML源码而非渲染结果。

**根因分析**：技术公告的URL未正确配置在 `cleanUrls` 中，被 `encodeUrls` 的全量编码策略处理，HTML标签被转义为实体。

**解决步骤**：
1. 检查 `XssStrutsInterceptor` 配置中 `cleanUrls` 是否包含技术公告URL
2. 确认URL Pattern匹配：`/module/prob_*`, `/probAudit.*`, `/probAjax_*.*`
3. 如果新增了富文本模块，将对应URL添加到 `cleanUrls`

**预防措施**：
- 新增富文本模块时，必须在 `cleanUrls` 中添加对应URL
- `basepackage` 和 `defaultJson` 两个抽象包的XSS配置需同步更新
- 编写集成测试验证富文本内容保存后可正确渲染

### 案例8：D365采购订单接口调用失败

**问题现象**：转包流程在GENERATE_CON节点调用D365 API生成采购订单失败，流程卡住无法继续。

**根因分析**：
1. D365 API认证token过期
2. 网络超时（D365响应慢）
3. 请求参数格式不符合D365 API要求
4. D365系统维护中

**解决步骤**：
1. 检查D365 API日志，确认返回的错误码和消息
2. 验证认证token是否有效
3. 检查请求参数：amount、facilitatorId等是否合规
4. 手动重试接口调用

**预防措施**：
- 增加D365 API调用的重试机制（最多3次，间隔递增）
- 增加接口调用的详细日志记录（请求参数、响应结果、耗时）
- 流程节点增加异常处理分支（调用失败时通知操作人）
- 建立D365系统维护通知机制

### 案例9：SAP数据同步连接超时

**问题现象**：SAP数据同步任务执行失败，`fnd_data_refresh_log` 记录 status=FAIL，errorMessage 提示连接超时。

**根因分析**：
1. SAP服务器网络不稳定
2. SAP RFC连接数达到上限
3. PMS使用 `DriverManagerDataSource`（无连接池），每次创建新连接开销大
4. 查询数据量过大，执行时间超过超时限制

**解决步骤**：
1. 检查 `fnd_data_refresh_log`：`SELECT * FROM fnd_data_refresh_log WHERE syncType = 'SAP' ORDER BY createTime DESC LIMIT 5`
2. 检查SAP服务器连通性：`telnet <sap_host> <sap_port>`
3. 检查SAP端RFC连接数限制
4. 考虑为SAP数据源引入连接池

**预防措施**：
- 对高频访问的外部数据源引入DBCP连接池
- 增加查询超时配置和重试机制
- 大数据量同步采用分批处理
- 监控同步任务执行时间，超过阈值告警

### 案例10：Activiti流程任务分配异常

**问题现象**：流程流转后任务未分配给预期用户，审批人看不到待办任务。

**根因分析**：
1. 任务监听器（TaskListener）中assignee设置逻辑错误
2. 候选人/候选组配置与实际用户不匹配
3. `dp_act_unify_task` 推送失败
4. 用户角色变更后流程定义中的候选人规则未更新

**解决步骤**：
1. 查询任务实际分配人：`SELECT * FROM ACT_RU_TASK WHERE PROC_INST_ID_ = 'xxx'`
2. 查询任务候选人：`SELECT * FROM ACT_RU_IDENTITYLINK WHERE TASK_ID_ = 'xxx'`
3. 检查统一任务表：`SELECT * FROM dp_act_unify_task WHERE instId = 'xxx'`
4. 手动重新分配任务或重置流程节点

**预防措施**：
- 流程定义中使用动态候选人（基于角色而非固定用户）
- 任务分配后验证assignee是否为预期用户
- 增加 `dp_act_unify_task` 推送失败的重试机制

### 案例11：数据库连接池耗尽

**问题现象**：应用日志出现 `Cannot get a connection, pool exhausted` 或 `Could not get JDBC Connection`，系统响应缓慢或不可用。

**根因分析**：
1. 慢查询占用连接时间过长
2. 代码中存在连接泄漏（获取连接未释放）
3. 并发量突增超过连接池容量
4. `removeAbandoned` 配置不当

**解决步骤**：
1. 查看当前数据库连接数：`SHOW PROCESSLIST;`
2. 查找长时间运行的查询：`SELECT * FROM information_schema.processlist WHERE TIME > 60 ORDER BY TIME DESC;`
3. 检查连接池配置：`main.database.maxActive=300`
4. 开启 `logAbandoned=true` 定位连接泄漏代码
5. 临时增加 `maxActive` 缓解问题

**预防措施**：
- 定期优化慢查询
- 代码审查确保所有连接获取都在finally中关闭
- 监控连接池使用率，超过80%时告警
- 使用 `SqlMapClientTemplate` 而非手动获取Connection

### 案例12：iBatis缓存数据不一致

**问题现象**：修改数据后查询结果未更新，仍然返回旧数据。

**根因分析**：iBatis的LRU缓存未及时刷新，可能原因：
1. `flushOnExecute` 未覆盖所有修改该数据的SQL
2. `flushInterval` 设置过长
3. 集群环境下各节点缓存独立，其他节点未刷新
4. `readOnly="true"` 缓存返回的是同一对象引用，被外部修改

**解决步骤**：
1. 检查cacheModel的 `flushOnExecute` 配置是否完整
2. 手动触发缓存刷新：执行 `refreshCacheData` SQL
3. 临时方案：重启Tomcat清空所有缓存
4. 检查是否使用了 `CopyLRU` 缓存控制器（深拷贝防篡改）

**预防措施**：
- 新增写操作SQL时同步更新 `flushOnExecute` 配置
- 实时性要求高的查询不使用缓存
- 集群环境对缓存数据设置较短的 `flushInterval`
- 使用 `CopyLRU` 替代标准 `LRU`，避免缓存对象被外部修改

---

## 3. 日志分析指南

### 3.1 关键日志位置

| 日志类型 | 日志位置 | 内容说明 | 查看方式 |
|----------|----------|----------|----------|
| 应用日志 | `{TOMCAT_HOME}/logs/catalina.out` | 应用运行日志、异常堆栈 | `tail -f catalina.out` |
| 应用日志(Daily) | `{TOMCAT_HOME}/logs/pms.log` | Log4j输出的业务日志 | `tail -f pms.log` |
| 访问日志 | `{TOMCAT_HOME}/logs/localhost_access_log.*` | HTTP请求访问记录 | 按日期查看 |
| 操作日志(数据库) | `fnd_operate_log` 表 | 用户操作记录 | SQL查询 |
| 同步日志(数据库) | `fnd_data_refresh_log` 表 | 数据同步状态记录 | SQL查询 |
| 连接泄漏日志 | 应用日志中搜索 "DBCP object created" | 连接获取堆栈 | `grep "DBCP object created" pms.log` |
| Activiti日志 | 应用日志中搜索 "org.activiti" | 流程引擎日志 | `grep "org.activiti" pms.log` |
| 性能日志 | 应用日志中搜索 "PreformanceThresholdInterceptor" | Service方法执行耗时 | `grep "Preformance" pms.log` |

### 3.2 日志级别说明

| 级别 | 说明 | 使用场景 | 生产环境配置 |
|------|------|----------|-------------|
| ERROR | 错误事件，可能导致功能异常 | 未捕获异常、数据库连接失败、外部系统调用失败 | 保留 |
| WARN | 警告事件，不影响主要功能 | 数据校验失败、缓存未命中、配置缺失 | 保留 |
| INFO | 重要业务信息 | 用户登录、关键操作、数据同步结果 | 保留 |
| DEBUG | 调试信息 | SQL参数、方法调用链、中间变量 | 关闭（排查问题时临时开启） |
| TRACE | 详细追踪信息 | 框架内部调用、SQL执行细节 | 关闭 |

**Log4j配置参考**：

```xml
<logger name="com.dp.plat">
    <level value="INFO" />
</logger>
<logger name="org.activiti">
    <level value="WARN" />
</logger>
<logger name="org.springframework">
    <level value="WARN" />
</logger>
<logger name="java.sql">
    <level value="DEBUG" />  <!-- 排查SQL问题时开启 -->
</logger>
```

### 3.3 常见错误日志模式

| 错误日志模式 | 含义 | 可能原因 | 排查方向 |
|-------------|------|----------|----------|
| `Cannot get a connection, pool exhausted` | 连接池耗尽 | 慢查询、连接泄漏、并发过高 | 检查连接池配置、慢查询 |
| `Deadlock found when trying to get lock` | 数据库死锁 | 并发更新同一行、事务嵌套 | 检查事务范围、锁顺序 |
| `Duplicate entry 'xxx' for key 'uk_xxx'` | 唯一约束冲突 | 重复插入、并发创建 | 检查业务逻辑、增加唯一性校验 |
| `Data truncation: Data too long for column` | 字段长度超限 | 输入数据超过字段定义长度 | 检查字段长度、前端校验 |
| `Invalid column name 'xxx'` | 列名不存在 | SQL映射引用了不存在的列 | 检查表结构变更、SQL映射文件 |
| `Connection timed out: connect` | 网络连接超时 | 外部系统不可用、防火墙阻断 | 检查网络连通性 |
| `NullPointerException` | 空指针异常 | 对象未初始化、查询结果为空 | 检查空值判断 |
| `CustomRuntimeException: 无权限` | 权限不足 | 用户无操作权限 | 检查用户角色、菜单权限 |
| `ognl.OgnlException` | OGNL表达式异常 | Struts2参数绑定失败 | 检查Action属性、参数类型 |
| `JsonProcessingException` | JSON解析失败 | customInfo字段格式错误 | 检查JSON格式、TypeHandler |
| `the request was rejected because its size exceeds` | 上传文件超限 | 文件大小超过配置限制 | 检查struts.multipart.maxSize |
| `There is no Action mapped for namespace` | Action未找到 | URL配置错误、Bean未注册 | 检查struts配置、Spring配置 |

---

## 4. 多数据源事务问题

### 4.1 跨数据源事务不回滚

**现象**：在 Service 方法中同时操作主库和外部数据源（SAP/D365），主库操作失败但外部数据源操作已提交，导致数据不一致。

**根因**：Spring 的 `DataSourceTransactionManager` 仅管理主库 `dataSource` 的事务，外部数据源（SAP/ERP/SSE）使用独立的 `DriverManagerDataSource`，不受 Spring 事务管理。

```java
public void syncDataFromSAP() {
    getSqlMapClientTemplateSAP().queryForList("findSAPData", param);
    getSqlMapClientTemplate().update("updateLocalData", localData);
}
```

**解决方案**：

1. **避免在同一事务中写入多个数据源**：外部数据源仅用于只读查询
2. **使用补偿机制**：主库操作失败时，记录补偿日志，由定时任务重试
3. **最终一致性方案**：通过 `fnd_data_refresh_log` 表记录同步状态，定时任务对账

```java
public void syncDataFromSAP() {
    List sapData = getSqlMapClientTemplateSAP().queryForList("findSAPData", param);
    try {
        getSqlMapClientTemplate().update("updateLocalData", localData);
    } catch (Exception e) {
        logSyncFailure("SAP", "updateLocalData", e.getMessage());
        throw e;
    }
}
```

### 4.2 外部数据源只读查询异常

**现象**：查询 SAP/D365 数据源时抛出连接超时或权限不足异常。

**排查步骤**：

1. 检查 `jdbc.properties` 中外部数据源连接参数是否正确
2. 检查网络连通性：`telnet <host> <port>`
3. 检查数据库账号权限：外部系统通常只提供只读账号
4. 检查 `DriverManagerDataSource` 是否在频繁创建连接（无连接池）

**解决方案**：

- 对高频访问的外部数据源，考虑引入连接池（如 DBCP）替代 `DriverManagerDataSource`
- 增加查询超时配置和重试机制
- 在 iBatis 独立配置中使用 `SIMPLE` 数据源类型，由 iBatis 管理简单连接池

### 4.3 SqlMapClientTemplate 线程安全

**现象**：高并发下出现 `ConcurrentModificationException` 或查询结果串线程。

**根因**：`SqlMapClientTemplate` 本身是线程安全的，但 DAO Bean 如果配置为单例（默认 scope），且内部持有可变状态，则可能引发线程安全问题。

**解决方案**：

- DAO Bean 必须配置 `scope="prototype"`

```xml
<bean id="projectDao" class="com.dp.plat.dao.ProjectDaoImpl"
    scope="prototype" parent="baseDao" />
```

- 避免在 DAO 中使用实例变量存储请求级数据
- `UserContext` 通过 `SpringContext.getBean("userContext")` 获取，为 Session 级别

---

## 5. Activiti 流程异常

### 5.1 流程定义部署失败

**现象**：上传 `.bpmn` 或 `.zip` 流程文件后，部署失败，日志提示解析错误。

**常见原因**：

1. BPMN XML 格式不合法（标签未闭合、属性缺失）
2. 流程定义 ID 包含特殊字符
3. 流程文件过大（含嵌入图片）
4. 数据库 `ACT_RE_DEPLOYMENT` 表空间不足

**排查步骤**：

1. 使用 BPMN 编辑器（如 Activiti Modeler）验证流程定义
2. 检查 Tomcat 日志中的异常堆栈
3. 检查数据库磁盘空间

### 5.2 任务分配异常

**现象**：流程流转后任务未分配给预期用户，或任务列表为空。

**排查步骤**：

1. 查询 `ACT_RU_TASK` 表确认任务的实际 `ASSIGNEE_`
2. 检查 `AbstractUnifyTaskListener` 的实现逻辑
3. 检查 OA 待办推送接口返回值

```sql
SELECT * FROM ACT_RU_TASK WHERE PROC_INST_ID_ = 'xxx';
SELECT * FROM ACT_RU_IDENTITYLINK WHERE TASK_ID_ = 'xxx';
```

### 5.3 流程实例悬挂

**现象**：流程实例状态变为 `suspended`，无法继续流转。

**解决方案**：

```java
runtimeService.activateProcessInstanceById(processInstanceId);
```

### 5.4 历史数据清理

**现象**：`ACT_HI_*` 表数据量持续增长，影响查询性能。

**解决方案**：

1. 定期执行历史数据清理（保留最近 N 个月）
2. 配置历史级别为 `AUDIT`
3. 手动清理已完成的历史流程实例

```sql
DELETE FROM ACT_HI_VARINST WHERE PROC_INST_ID_ IN (
    SELECT PROC_INST_ID_ FROM ACT_HI_PROCINST
    WHERE END_TIME_ < DATE_SUB(NOW(), INTERVAL 6 MONTH)
);
```

---

## 6. 数据同步失败排查

### 6.1 SAP/D365 连接超时

**现象**：定时数据同步任务执行失败，日志提示连接超时。

**排查步骤**：

1. 检查 `fnd_data_refresh_log` 表中的同步状态和错误信息

```sql
SELECT * FROM fnd_data_refresh_log
WHERE syncType = 'SAP' AND status = 'FAIL'
ORDER BY createTime DESC LIMIT 10;
```

2. 检查网络连通性和防火墙规则
3. 检查外部数据库负载和连接数限制
4. 检查 `DriverManagerDataSource` 连接参数

**解决方案**：

- 增加连接超时配置
- 对大数据量同步采用分批处理
- 配置同步失败重试机制（最多 3 次）

### 6.2 CRM 视图变更

**现象**：CRM 数据同步突然失败，SQL 执行异常。

**根因**：CRM 系统升级或维护时修改了视图结构，导致 PMS 的 SQL 映射中引用的列名或表名不存在。

**解决方案**：

- 更新 iBatis SQL 映射文件，适配新的视图结构
- 建立与 CRM 团队的变更通知机制
- 在 SQL 中使用 `IFNULL` 或 `COALESCE` 增加容错

### 6.3 SMS 数据格式不一致

**现象**：SMS 系统同步的数据在 PMS 中解析失败。

**常见原因**：

1. SMS 系统字段类型变更（如 VARCHAR → INT）
2. 日期格式不一致（SMS 使用 `yyyyMMdd`，PMS 期望 `yyyy-MM-dd`）
3. 字符编码问题（GBK vs UTF-8）

**解决方案**：

- 在 iBatis TypeHandler 中增加格式转换逻辑
- 使用 `DateTimeTypeHandler` 统一日期处理
- 在 SQL 层面使用 `DATE_FORMAT` / `STR_TO_DATE` 转换

### 6.4 OA/EHR 人员编码变更

**现象**：OA 或 EHR 系统中人员编码变更后，PMS 中关联的用户数据失效。

**根因**：PMS 通过 `username`（工号）关联外部系统人员数据，外部系统修改工号后 PMS 侧未同步更新。

**解决方案**：

- 在数据同步任务中增加人员编码变更检测逻辑
- 使用不可变的人员 ID（而非工号）作为关联键
- 建立人员编码变更通知机制

---

## 7. 连接池耗尽处理

### 7.1 maxActive=300 配置

**现象**：应用日志出现 `Cannot get a connection, pool exhausted` 或 `Could not get JDBC Connection`。

**当前配置**：

```properties
main.database.initialSize=2
main.database.maxActive=300
main.database.maxIdle=50
main.database.minIdle=3
main.database.maxWait=60000
```

**排查步骤**：

1. 查看当前连接池状态

```sql
SHOW PROCESSLIST;
SHOW STATUS LIKE 'Threads_connected';
```

2. 检查是否有长时间运行的查询

```sql
SELECT * FROM information_schema.processlist
WHERE TIME > 60 ORDER BY TIME DESC;
```

3. 检查是否有未关闭的连接（连接泄漏）

**解决方案**：

- 适当增加 `maxActive`（但不超过数据库 `max_connections`）
- 优化慢查询，减少连接占用时间
- 检查代码中是否有手动获取连接未释放的情况

### 7.2 removeAbandoned 回收

**当前配置**：

```properties
main.database.removeAbandoned=true
main.database.removeAbandonedTimeout=180
main.database.logAbandoned=true
```

**注意事项**：

- `removeAbandoned` 仅作为安全网，不应依赖它来管理连接
- 生产环境建议 `removeAbandoned=false`，避免在事务执行中被误回收
- 开发/测试环境建议 `removeAbandoned=true`，便于发现连接泄漏

### 7.3 连接泄漏检测

**检测方法**：

1. 启用 `logAbandoned=true`，查看日志中的泄漏堆栈
2. 监控连接池使用率：`(activeConnections / maxActive) * 100%`
3. 定期检查 `SHOW PROCESSLIST` 中 Sleep 状态的连接

**常见泄漏场景**：

1. 手动获取 `Connection` 未在 `finally` 中关闭
2. 使用 `SqlMapClientTemplate` 的事务方法未正确提交/回滚
3. 外部数据源 `DriverManagerDataSource` 每次创建新连接未释放

**修复示例**：

```java
Connection conn = null;
try {
    conn = getSqlMapClientTemplate().getDataSource().getConnection();
    // 业务操作
} finally {
    if (conn != null) {
        try { conn.close(); } catch (SQLException e) { }
    }
}
```

---

## 8. Struts2 常见异常

### 8.1 OGNL 表达式注入

**现象**：日志中出现 `ognl.OgnlException` 或安全扫描报告 OGNL 注入漏洞。

**防护措施**：

1. `XssStrutsInterceptor` 对请求参数进行 XSS 过滤
2. Struts2 升级到安全版本（当前使用 2.5.x）
3. 配置 `global-allowed-methods` 限制可调用的方法

> **建议**：生产环境应配置具体的方法白名单，而非 `regex:.*`。

### 8.2 文件上传大小超限

**现象**：上传文件时返回错误页面，日志提示 `the request was rejected because its size exceeds the configured maximum`。

**当前配置**：

```xml
<constant name="struts.multipart.maxSize" value="209715200" />

<interceptor-ref name="fileUpload">
    <param name="maximumSize">209715200</param>
    <param name="allowedTypes"></param>
</interceptor-ref>
```

- `struts.multipart.maxSize`：全局最大上传大小（200MB）
- `fileUpload.maximumSize`：单个文件最大大小（200MB）
- `allowedTypes`：允许的 MIME 类型（空表示不限制）

### 8.3 Action 找不到

**现象**：访问 URL 返回 404 或 `There is no Action mapped for namespace /xxx and action name xxx`。

**常见原因**：

1. Action 未在 `struts-sys.xml` 中配置
2. Action Bean 未在 Spring 中注册
3. URL 命名空间不匹配
4. `struts.enable.DynamicMethodInvocation=true` 但方法未在 `global-allowed-methods` 中声明

**排查步骤**：

1. 检查 Struts2 配置文件中是否存在对应的 Action 定义
2. 检查 Spring 配置中 Action Bean 的 ID 是否与 Struts2 中的 `class` 属性一致
3. 检查 URL 的命名空间是否与 package 的 `namespace` 匹配
4. 开启 Struts2 开发模式查看详细错误信息

---

## 9. iBatis 常见问题

### 9.1 SQL 映射文件加载失败

**现象**：应用启动时抛出 `IOException` 或 `SqlMapException`，提示找不到 SQL 映射文件。

**常见原因**：

1. `sql-map-config.xml` 中 `sqlMap` 的 `resource` 路径错误
2. SQL 映射文件存在 XML 语法错误
3. 文件编码不是 UTF-8

**排查步骤**：

1. 检查 `sql-map-config.xml` 中的 `resource` 路径
2. 使用 XML 验证工具检查映射文件格式
3. 检查 DTD 声明是否正确

### 9.2 TypeHandler 注册

**现象**：查询结果中 JSON 字段解析失败，或自定义类型转换异常。

**当前注册的 TypeHandler**：

```xml
<typeAlias type="com.dp.plat.util.DateTimeTypeHandler" alias="DateTimeHandler" />
<typeAlias type="com.dp.plat.ibatis.cache.LRUCacheController" alias="CopyLRU" />
<typeAlias type="com.dp.plat.ibatis.handler.FastjsonTypeHandler" alias="JsonTypeHandler" />

<typeHandler javaType="com.dp.plat.type.DateTime" callback="DateTimeHandler" jdbcType="int" />
<typeHandler jdbcType="JSON" javaType="com.dp.plat.data.bean.JsonCustomInfo"
    callback="JsonTypeHandler" />
<typeHandler jdbcType="OTHER" javaType="com.dp.plat.data.bean.JsonCustomInfo"
    callback="JsonTypeHandler" />
```

**常见问题**：

1. 新增自定义类型未在 `sql-map-config.xml` 中注册
2. `jdbcType` 与数据库实际类型不匹配
3. `FastjsonTypeHandler` 反序列化时目标类不匹配

**解决方案**：

- 新增自定义类型时，同步更新 `sql-map-config.xml`
- JSON 字段同时注册 `jdbcType="JSON"` 和 `jdbcType="OTHER"` 两个 TypeHandler
- 确保 `FastjsonTypeHandler` 的泛型类型与目标类一致

### 9.3 缓存一致性问题

**现象**：修改数据后查询结果未更新，仍然返回旧数据。

**根因**：iBatis 的 `CopyLRU` 缓存未及时刷新。

**排查步骤**：

1. 检查 `flushOnExecute` 是否覆盖了所有修改该数据的 SQL 语句
2. 检查 `flushInterval` 是否过长
3. 检查是否跨 SqlMapClient 操作（不同数据源的缓存独立）

**解决方案**：

1. 补充缺失的 `flushOnExecute` 语句
2. 手动触发缓存刷新：调用 `refreshCacheData` SQL
3. 缩短 `flushInterval` 或对实时性要求高的查询禁用缓存

**CopyLRU 缓存特性**：

`LRUCacheController` 继承 iBatis 的 `LruCacheController`，在 `getObject` 和 `putObject` 时对缓存对象进行深拷贝，避免缓存对象被外部修改导致数据不一致。
