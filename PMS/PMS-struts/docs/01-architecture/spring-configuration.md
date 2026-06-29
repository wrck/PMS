# Spring 配置架构文档

## 1. 配置文件加载链

PMS 系统采用 Spring XML 配置的模块化拆分策略，通过 `<import>` 逐层导入，形成清晰的层次结构。

### 1.1 导入关系图

```
applicationContext.xml (入口)
│
├── applicationContext-service.xml
│   └── applicationContext-dao.xml
│
├── applicationContext-common.xml
│
├── applicationContext-context.xml
│
├── applicationContext-action.xml
│   ├── applicationContext-service.xml (重复导入，Spring 自动去重)
│   │   └── applicationContext-dao.xml
│   └── applicationContext-context.xml (重复导入，Spring 自动去重)
│
├── activiti-context.xml
│
└── spring-extend-mybatis.xml
```

### 1.2 各配置文件职责

| 配置文件 | 职责 | 关键内容 |
|---------|------|---------|
| `applicationContext.xml` | 根配置，全局基础设施 | 数据源、事务管理、AOP拦截、iBatis SqlMapClient |
| `applicationContext-dao.xml` | DAO 层 Bean 定义 | baseDao 抽象Bean、各业务 DAO 实现 |
| `applicationContext-service.xml` | Service 层 Bean 定义 | baseServce 抽象Bean、Service + ServiceAgent 事务代理对 |
| `applicationContext-action.xml` | Struts2 Action 层 Bean 定义 | Action Bean（scope=prototype）、DisplayParam |
| `applicationContext-common.xml` | 左菜单导航配置 | SysLeftMenu、LeftMenuGroup、LeftMenuLi |
| `applicationContext-context.xml` | 上下文 Bean 定义 | userContext（session范围）、systemContext |
| `activiti-context.xml` | Activiti 工作流引擎配置 | ProcessEngine、全局任务监听器、流程服务 |
| `spring-extend-mybatis.xml` | MyBatis 扩展配置 | SqlSessionFactory、MapperScanner、注解事务 |
| `applicationContext-security.xml` | Spring Security 配置 | URL权限过滤、认证管理器（独立加载） |

### 1.3 加载顺序说明

Spring 容器启动时，`applicationContext.xml` 作为入口按顺序加载：

1. **applicationContext-service.xml** → 最先加载，确保 Service 层 Bean 可用
2. **属性配置器** → 加载 `jdbc.properties`
3. **数据源与 SqlMapClient** → 定义主数据源及外部系统数据源
4. **事务管理器** → 配置 DataSourceTransactionManager
5. **事务代理基类** → 定义 `transactionBaseService` 抽象 Bean
6. **AOP 拦截器** → 性能阈值拦截器与自动代理
7. **applicationContext-common.xml** → 左菜单导航
8. **applicationContext-context.xml** → 用户上下文、系统上下文
9. **applicationContext-action.xml** → Struts2 Action Bean
10. **activiti-context.xml** → Activiti 流程引擎
11. **spring-extend-mybatis.xml** → MyBatis 扩展（最后加载）

> **注意**：`applicationContext-action.xml` 和 `applicationContext-service.xml` 都导入了 `applicationContext-dao.xml`，Spring 容器会自动去重，不会产生重复 Bean 定义冲突。

---

## 2. Bean 命名约定

### 2.1 DAO 层（`applicationContext-dao.xml`）

DAO Bean 遵循 `*Dao` 命名规范，继承自抽象 Bean `baseDao`，统一注入 `sqlMapClientTemplate`。

**命名规则**：`{业务模块}Dao`

```xml
<bean id="baseDao" abstract="true">
    <property name="sqlMapClientTemplate">
        <ref bean="sqlMapClientTemplate" />
    </property>
</bean>

<bean id="loginDao" class="com.dp.plat.dao.LoginDaoImpl"
    scope="prototype" parent="baseDao" />

<bean id="projectDao" class="com.dp.plat.dao.ProjectDaoImpl"
    scope="prototype" parent="baseDao" />
```

**特殊抽象 Bean**：

| Bean ID | 类型 | 说明 |
|---------|------|------|
| `baseDao` | abstract | 注入 `sqlMapClientTemplate`，所有 DAO 的父 Bean |
| `baseContextLoggerDao` | abstract | 继承 `baseDao`，额外注入 `opLoggerDao`，用于需要操作日志的 DAO |

**DAO 继承体系**：

```
baseDao (abstract)
├── baseContextLoggerDao (abstract) + opLoggerDao
│   └── loginDao
├── userManageDao
├── roleManageDao
├── departmentManageDao
├── passwordDao
├── workspaceDao
├── sendMailDao
├── pmClosedLoopDao
├── pmClosedLoopQuesnaireDao
├── projectDao
├── basicDataDao
├── projectPlanDao
├── dataAnalysisDao
├── workflowDao
├── callBackDao
├── reportDao
├── presalesDao
├── probManageDao
├── subcontractDao
├── warrantyCallbackDao (+ projectDao 注入)
└── certificateDao
```

### 2.2 Service 层（`applicationContext-service.xml`）

Service 层采用 **Service + ServiceAgent** 双 Bean 模式，ServiceAgent 作为事务代理包装真实 Service。

**命名规则**：
- 真实 Service：`{业务模块}Service`
- 事务代理：`{业务模块}ServiceAgent`

```xml
<bean id="baseServce" abstract="true">
    <property name="userContext">
        <ref bean="userContext" />
    </property>
</bean>

<!-- 真实 Service Bean -->
<bean id="loginService" class="com.dp.plat.service.LoginServiceImpl"
    lazy-init="false" parent="baseServce">
    <property name="loginDao" ref="loginDao" />
</bean>

<!-- 事务代理 Bean -->
<bean id="loginServiceAgent" parent="transactionBaseService">
    <property name="target" ref="loginService" />
</bean>
```

**关键设计**：
- `baseServce` 抽象 Bean 为所有 Service 注入 `userContext`（当前登录用户信息）
- ServiceAgent 继承 `transactionBaseService`，自动获得事务管理能力
- **Action 层始终引用 `*ServiceAgent`**，而非直接引用 `*Service`，确保事务生效

**Service 与 ServiceAgent 对照表**：

| Service Bean | ServiceAgent Bean | 实现类 |
|-------------|-------------------|--------|
| `loginService` | `loginServiceAgent` | `LoginServiceImpl` |
| `userManageService` | `userManageServiceAgent` | `UserManageServiceImpl` |
| `roleManageService` | `roleManageServiceAgent` | `RoleManageServiceImpl` |
| `departmentManageService` | `departmentManageServiceAgent` | `DepartmentManageServiceImpl` |
| `passwordService` | `passwordServiceAgent` | `PasswordServiceImpl` |
| `opLogService` | `opLogServiceAgent` | `OpLogServiceImpl` |
| `workspaceService` | `workspaceServiceAgent` | `WorkSpaceServiceImpl` |
| `sendMailService` | `sendMailServiceAgent` | `SendMailServiceImpl` |
| `projectService` | `projectServiceAgent` | `ProjectServiceImpl` |
| `basicDataService` | `basicDataServiceAgent` | `BasicDataServiceImpl` |
| `projectPlanService` | `projectPlanServiceAgent` | `ProjectPlanServiceImpl` |
| `pmClosedLoopService` | `pmClosedLoopServiceAgent` | `PmClosedLoopServiceImpl` |
| `pmClosedLoopQuesnaireService` | `pmClosedLoopQuesnaireServiceAgent` | `PmClosedLoopQuesnaireServiceImpl` |
| `callBackService` | `callBackServiceAgent` | `CallBackServiceImpl` |
| `presalesService` | `presalesServiceAgent` | `PresalesServiceImpl` |
| `dataAnalysisService` | `dataAnalysisServiceAgent` | `DataAnalysisServiceImpl` |
| `report` | `reportServiceAgent` | `ReportServiceImpl` |
| `probManage` | `probManageServiceAgent` | `ProbManageServiceImpl` |
| `subcontractService` | `subcontractServiceAgent` | `SubcontractServiceImpl` |
| `warrantyCallbackService` | `warrantyCallbackServiceAgent` | `WarrantyCallbackServiceImpl` |
| `certificateService` | `certificateServiceAgent` | `CertificateServiceImpl` |

> **注意**：`report` 和 `probManage` 的 Service Bean ID 不遵循 `*Service` 后缀约定，但其 ServiceAgent 仍遵循 `*ServiceAgent` 命名。

### 2.3 Action 层（`applicationContext-action.xml`）

Action Bean 是 Struts2 的 Action 实例，由 Spring 管理生命周期。

**命名规则**：Bean ID 即为 Struts2 的 Action 名称，**必须设置 `scope="prototype"`** 以保证每次请求创建新实例。

```xml
<bean id="Login" class="com.dp.plat.action.LoginAction" scope="prototype">
    <property name="user" ref="sysLoginParam" />
    <property name="loginService" ref="loginServiceAgent" />
</bean>

<bean id="ProjectAction" class="com.dp.plat.action.ProjectAction" scope="prototype">
    <property name="projectService" ref="projectServiceAgent" />
    <property name="userManageService" ref="userManageServiceAgent" />
    <property name="basicDataService" ref="basicDataServiceAgent" />
    <property name="projectPlanService" ref="projectPlanServiceAgent" />
    <property name="sendMailService" ref="sendMailServiceAgent" />
</bean>
```

**Action 注入规则**：
- Service 引用一律使用 `*ServiceAgent`（事务代理），而非 `*Service`
- 可选注入 `displayParam`（DisplayParam Bean），用于列表排序和列映射
- `sysLoginParam` 为登录参数 Bean，scope=prototype

**Action Bean 列表**：

| Bean ID | 实现类 | 说明 |
|---------|--------|------|
| `Login` | `LoginAction` | 登录 |
| `UserManageAction` | `UserManageAction` | 用户管理 |
| `RoleManageAction` | `RoleManageAction` | 角色管理 |
| `DepartmentManageAction` | `DepartmentManageAction` | 部门管理 |
| `PasswordGetinfo` | `PasswordGetinfo` | 修改密码 |
| `OperateLogAction` | `OperateLogAction` | 操作日志 |
| `WorkSpaceAction` | `WorkSpaceAction` | 工作台 |
| `WorkFlowAction` | `WorkFlowAction` | 工作流管理 |
| `PmClosedLoopAction` | `PmClosedLoopAction` | 项目闭环 |
| `PmClosedLoopQuesnaireAction` | `PmClosedLoopQuesnaireAction` | 测评试卷维护 |
| `ProjectAction` | `ProjectAction` | 项目管理 |
| `BasicDataManageAction` | `BasicDataManageAction` | 基础数据管理 |
| `DataAnalysisAction` | `DataAnalysisAction` | 数据统计 |
| `ReportAction` | `ReportAction` | 报表统计 |
| `CallBackAction` | `CallBackAction` | 回访流程管理 |
| `PresalesAction` | `PresalesAction` | 售前流程 |
| `ProbManageAction` | `ProbManageAction` | 技术公告管理 |
| `UploadAction` | `UploadAction` | 文件上传 |
| `SubcontractAction` | `SubcontractAction` | 项目转包管理 |
| `Certificate` | `CertificateAction` | 合格证查询 |
| `MaintenanceAction` | `MaintenanceAction` | 项目维护管理 |
| `SupervisionAction` | `SupervisionAction` | 项目督查管理 |
| `WarrantyCallbackAction` | `WarrantyCallbackAction` | 项目维保回访 |

---

## 3. 事务管理

### 3.1 事务管理器

系统使用 Spring 的 `DataSourceTransactionManager`，绑定主数据源：

```xml
<bean id="transactionManager"
    class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <property name="dataSource">
        <ref bean="dataSource" />
    </property>
</bean>
```

同时启用了注解驱动事务：

```xml
<tx:annotation-driven transaction-manager="transactionManager" />
```

### 3.2 transactionBaseService 抽象 Bean

系统通过 `TransactionProxyFactoryBean` 定义事务代理抽象 Bean，所有需要事务的 Service 通过继承此 Bean 自动获得事务管理：

```xml
<bean id="transactionBaseService" abstract="true"
    class="org.springframework.transaction.interceptor.TransactionProxyFactoryBean">
    <property name="transactionManager">
        <ref bean="transactionManager"/>
    </property>
    <property name="transactionAttributes">
        <props>
            <prop key="insert*">PROPAGATION_REQUIRED</prop>
            <prop key="update*">PROPAGATION_REQUIRED</prop>
            <prop key="delete*">PROPAGATION_REQUIRED</prop>
            <prop key="parse*">PROPAGATION_REQUIRED</prop>
            <prop key="add*">PROPAGATION_REQUIRED</prop>
            <prop key="save*">PROPAGATION_REQUIRED</prop>
            <prop key="do*">PROPAGATION_REQUIRED</prop>
            <prop key="keep*">PROPAGATION_REQUIRED</prop>
            <prop key="start*">PROPAGATION_REQUIRED</prop>
            <prop key="submit*">PROPAGATION_REQUIRED</prop>
        </props>
    </property>
</bean>
```

### 3.3 事务方法名前缀规则

只有方法名匹配以下前缀的 Service 方法才会被事务代理拦截，传播行为均为 `PROPAGATION_REQUIRED`：

| 方法名前缀 | 语义 | 示例 |
|-----------|------|------|
| `insert*` | 插入操作 | `insertUser()`, `insertProject()` |
| `update*` | 更新操作 | `updateRole()`, `updateStatus()` |
| `delete*` | 删除操作 | `deleteById()`, `deleteBatch()` |
| `parse*` | 解析并持久化 | `parseExcel()`, `parseData()` |
| `add*` | 添加操作 | `addMember()`, `addRecord()` |
| `save*` | 保存操作 | `saveForm()`, `saveDraft()` |
| `do*` | 执行操作 | `doApprove()`, `doSync()` |
| `keep*` | 维持/保持操作 | `keepAlive()`, `keepSession()` |
| `start*` | 启动操作 | `startProcess()`, `startFlow()` |
| `submit*` | 提交操作 | `submitForm()`, `submitApproval()` |

> **重要**：以 `query*`、`find*`、`get*`、`list*` 等前缀命名的查询方法**不参与事务**，它们以只读方式执行。开发时必须严格遵守方法命名约定，否则事务不会生效。

### 3.4 Service + ServiceAgent 事务代理模式

每个需要事务的 Service 都成对定义：

```
┌─────────────────────────────────────────────┐
│           *ServiceAgent (事务代理)            │
│  ┌───────────────────────────────────────┐   │
│  │  TransactionProxyFactoryBean          │   │
│  │  ┌─────────────────────────────────┐  │   │
│  │  │  *Service (真实业务逻辑)         │  │   │
│  │  │  - 继承 baseServce              │  │   │
│  │  │  - 注入 userContext             │  │   │
│  │  │  - 注入 *Dao                    │  │   │
│  │  └─────────────────────────────────┘  │   │
│  └───────────────────────────────────────┘   │
│  事务拦截: insert*/update*/delete*/...        │
└─────────────────────────────────────────────┘
```

**调用链路**：`Action` → `*ServiceAgent`（事务代理） → `*Service`（真实业务） → `*Dao`（数据访问）

---

## 4. AOP 拦截

### 4.1 性能阈值拦截器

`PreformanceThresholdInterceptor` 实现了 `MethodInterceptor` 接口，用于拦截 Service 方法调用并记录操作日志：

```java
public class PreformanceThresholdInterceptor implements MethodInterceptor {
    private OpLogService opLogService;

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        Object o = mi.proceed();
        try {
            UserContext userContext = (UserContext) SpringContext.getBean("userContext");
            if (!userContext.getOption().equals("")) {
                opLogService.insertLog();
                userContext.setOption("");
            }
        } catch (Exception e) {
        }
        return o;
    }
}
```

**Bean 定义**：

```xml
<bean id="preformanceThresholdInterceptor"
    class="com.dp.plat.interceptor.PreformanceThresholdInterceptor">
    <property name="opLogService" ref="opLogServiceAgent" />
</bean>
```

**工作机制**：
1. 拦截 Service 方法调用
2. 执行真实方法（`mi.proceed()`）
3. 检查当前用户上下文（`userContext`）是否有待记录的操作标记
4. 如有标记，调用 `opLogService.insertLog()` 记录操作日志
5. 清除操作标记

### 4.2 BeanNameAutoProxyCreator 自动代理

通过 `BeanNameAutoProxyCreator` 对所有 `*Service` Bean 自动应用性能拦截器：

```xml
<bean id="performanceThresholdProxyCreator"
    class="org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator">
    <property name="beanNames">
        <list>
            <value>*Service</value>
        </list>
    </property>
    <property name="interceptorNames">
        <value>preformanceThresholdInterceptor</value>
    </property>
</bean>
```

**匹配规则**：
- `beanNames` 使用 `*Service` 通配符，匹配所有以 `Service` 结尾的 Bean
- 包括 `loginService`、`userManageService` 等真实 Service Bean
- `*ServiceAgent` 不匹配此规则（以 `ServiceAgent` 结尾）

### 4.3 AspectJ 自动代理

根配置还启用了 AspectJ 自动代理，支持 `@Aspect` 注解风格的切面：

```xml
<aop:aspectj-autoproxy proxy-target-class="true" />
```

`proxy-target-class="true"` 表示使用 CGLIB 代理（基于类继承），而非 JDK 动态代理（基于接口）。

### 4.4 AOP 代理层次总结

```
Action 调用 Service 时的代理层次：

Action
  │
  ├─ *ServiceAgent (TransactionProxyFactoryBean 代理)
  │    │
  │    ├─ *Service (BeanNameAutoProxyCreator 代理)
  │    │    │
  │    │    └─ PreformanceThresholdInterceptor
  │    │         │
  │    │         └─ 真实 Service 方法
  │    │
  │    └─ 事务拦截 (insert*/update*/delete*/...)
  │
  └─ 事务提交/回滚
```

---

## 5. 数据源配置

### 5.1 数据源总览

系统通过 `jdbc.properties` 配置了 9 个数据源，连接不同的业务系统数据库：

| 序号 | 数据源 | Bean ID | 数据库类型 | 驱动 | 目标系统 |
|------|--------|---------|-----------|------|---------|
| 1 | PMS 主数据源 | `dataSource` | MySQL | `com.mysql.cj.jdbc.Driver` | PMS 项目管理系统 |
| 2 | SAP 数据源 | `dataSourceSAP` | SQL Server | `com.microsoft.sqlserver.jdbc.SQLServerDriver` | SAP ERP 系统 |
| 3 | D365 数据源 | `dataSourceD365` | SQL Server | `com.microsoft.sqlserver.jdbc.SQLServerDriver` | Dynamics 365 |
| 4 | SMS 数据源 | - | MySQL | `com.mysql.cj.jdbc.Driver` | 短信服务系统 |
| 5 | CRM 数据源 | - | SQL Server | `com.microsoft.sqlserver.jdbc.SQLServerDriver` | CRM 客户关系管理 |
| 6 | OA 数据源 | - | SQL Server | `com.microsoft.sqlserver.jdbc.SQLServerDriver` | OA 办公自动化 |
| 7 | EHR 数据源 | - | SQL Server | `com.microsoft.sqlserver.jdbc.SQLServerDriver` | EHR 人力资源 |
| 8 | SSE 数据源 | `dataSourceSSE` | MySQL | `com.mysql.cj.jdbc.Driver` | SSE 系统 |
| 9 | ITR 数据源 | - | PostgreSQL | `org.postgresql.Driver` | ITR 运维系统 |

> **说明**：序号 4~7（SMS、CRM、OA、EHR）及序号 9（ITR）的数据源未在 Spring XML 中定义独立 Bean，而是通过 iBatis 独立 `sqlMapConfig.xml` 配置，由 iBatis 自身的事务管理器管理连接。

### 5.2 主数据源（PMS MySQL）

主数据源使用 Apache DBCP 连接池，具备完善的连接池参数配置：

```xml
<bean id="propertyPlaceholderConfigurer"
    class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
    <property name="location" value="classpath:jdbc.properties" />
</bean>

<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
    <property name="driverClassName" value="${main.database.driverClassName}" />
    <property name="url" value="${main.database.url}" />
    <property name="username" value="${main.database.username}" />
    <property name="password" value="${main.database.password}" />
    <property name="initialSize" value="${main.database.initialSize}" />
    <property name="maxActive" value="${main.database.maxActive}" />
    <property name="maxIdle" value="${main.database.maxIdle}" />
    <property name="minIdle" value="${main.database.minIdle}" />
    <property name="logAbandoned" value="${main.database.logAbandoned}" />
    <property name="removeAbandoned" value="${main.database.removeAbandoned}" />
    <property name="removeAbandonedTimeout" value="${main.database.removeAbandonedTimeout}" />
    <property name="maxWait" value="${main.database.maxWait}" />
    <property name="testOnBorrow" value="true" />
    <property name="validationQuery" value="select 1" />
</bean>
```

**连接池参数说明**：

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `initialSize` | 2 | 池启动时创建的初始连接数 |
| `maxActive` | 300 | 同一时间可分配的最大连接数 |
| `maxIdle` | 50 | 池中最大空闲连接数 |
| `minIdle` | 3 | 池中最小空闲连接数 |
| `maxWait` | 60000ms | 获取连接超时等待时间 |
| `removeAbandoned` | false | 是否自动回收超时连接 |
| `removeAbandonedTimeout` | 1800s | 连接超时回收时间 |
| `logAbandoned` | true | 回收超时连接时是否打印日志 |
| `testOnBorrow` | true | 获取连接时是否验证 |
| `validationQuery` | select 1 | 连接验证 SQL |

### 5.3 外部系统数据源（SAP / D365 / SSE）

外部系统数据源使用 `DriverManagerDataSource`（无连接池），每次请求创建新连接：

```xml
<!-- SAP 数据源 -->
<bean id="dataSourceSAP"
    class="org.springframework.jdbc.datasource.DriverManagerDataSource">
    <property name="driverClassName" value="${sap.database.driverClassName}" />
    <property name="url" value="${sap.database.url}" />
    <property name="username" value="${sap.database.username}" />
    <property name="password" value="${sap.database.password}" />
</bean>

<!-- D365 数据源 -->
<bean id="dataSourceD365"
    class="org.springframework.jdbc.datasource.DriverManagerDataSource">
    <property name="driverClassName" value="${d365.database.driverClassName}" />
    <property name="url" value="${d365.database.url}" />
    <property name="username" value="${d365.database.username}" />
    <property name="password" value="${d365.database.password}" />
</bean>

<!-- SSE 数据源 -->
<bean id="dataSourceSSE"
    class="org.springframework.jdbc.datasource.DriverManagerDataSource">
    <property name="driverClassName" value="${sse.database.driverClassName}" />
    <property name="url" value="${sse.database.url}" />
    <property name="username" value="${sse.database.username}" />
    <property name="password" value="${sse.database.password}" />
</bean>
```

> **注意**：`DriverManagerDataSource` 不提供连接池功能，适用于低频访问的外部系统。对于高频访问场景，建议替换为 DBCP 或 HikariCP 连接池数据源。

### 5.4 BaseDao 多数据源访问

`BaseDao` 类内置了多个 `SqlMapClientTemplate` 属性，支持在同一个 DAO 中访问不同数据源：

```java
public class BaseDao {
    private SqlMapClientTemplate sqlMapClientTemplate;      // 主数据源 (PMS MySQL)
    private SqlMapClientTemplate sqlMapClientTemplateSAP;   // SAP 数据源
    private SqlMapClientTemplate sqlMapClientTemplateERP;   // D365/ERP 数据源
    private SqlMapClientTemplate sqlMapClientTemplateSSE;   // SSE 数据源
    // ... getter/setter
}
```

由于 `applicationContext-dao.xml` 设置了 `default-autowire="byName"`，`BaseDao` 中的 `sqlMapClientTemplateSAP`、`sqlMapClientTemplateERP`、`sqlMapClientTemplateSSE` 属性会按名称自动注入对应的 Bean。

> **重要风险提示**：BaseDao 中 D365 对应的属性名为 `sqlMapClientTemplateERP`，而 Spring 容器中对应的 Bean ID 为 `sqlMapClientTemplateD365`。由于 `byName` 自动装配按属性名匹配 Bean，`sqlMapClientTemplateERP` 无法匹配到 `sqlMapClientTemplateD365`，导致该属性在运行时为 `null`。如需在 DAO 中访问 D365 数据源，必须在 `applicationContext-dao.xml` 中显式注入 `sqlMapClientTemplateD365`，或修改 BaseDao 属性名为 `sqlMapClientTemplateD365`。

---

## 6. iBatis SqlMapClient 配置

### 6.1 主系统 SqlMapClient

主系统使用 `sql-map-config.xml`，由 Spring 管理 SqlMapClient 生命周期：

```xml
<bean id="sqlMapClient" class="org.springframework.orm.ibatis.SqlMapClientFactoryBean">
    <property name="configLocation">
        <value>classpath:sql-map-config.xml</value>
    </property>
    <property name="dataSource" ref="dataSource" />
</bean>

<bean id="sqlMapClientTemplate" class="org.springframework.orm.ibatis.SqlMapClientTemplate">
    <property name="sqlMapClient">
        <ref bean="sqlMapClient" />
    </property>
</bean>
```

**sql-map-config.xml 包含的 SQL 映射文件**：

| 映射文件 | 业务模块 |
|---------|---------|
| `sql-map-admin-config.xml` | 系统管理（用户/角色/部门） |
| `sql-map-project-common-config.xml` | 项目公共 |
| `sql-map-project-config.xml` | 项目管理 |
| `sql-map-work-config.xml` | 工作流 |
| `sql-map-activity-config.xml` | 活动管理 |
| `sql-map-callback-config.xml` | 回访管理 |
| `sql-map-report-config.xml` | 报表统计 |
| `sql-map-presales-config.xml` | 售前管理 |
| `sql-map-prob-config.xml` | 技术公告 |
| `sql-map-subcontract-config.xml` | 项目转包 |
| `sql-map-certificate-config.xml` | 合格证 |
| `sql-map-maintenance-config.xml` | 项目维护 |
| `sql-map-warrantyCallback-config.xml` | 维保回访 |
| `sql-map-unifyTask-config.xml` | 统一任务 |

**sql-map-config.xml 全局配置**：

> **注意**：`sql-map-config.xml`（Spring 管理）的 `useStatementNamespaces="false"`，而 `sqlMapConfig.xml`（iBatis 自管理，数据刷新用）的 `useStatementNamespaces="true"`。两者使用不同的命名空间策略：
> - `useStatementNamespaces="false"`：SQL ID 直接使用短名（如 `saveProb`），无需加命名空间前缀
> - `useStatementNamespaces="true"`：SQL ID 需加命名空间前缀（如 `prob.saveProb`），避免跨映射文件 SQL ID 冲突

```xml
<sqlMapConfig>
    <settings cacheModelsEnabled="true" enhancementEnabled="true"
        lazyLoadingEnabled="true" errorTracingEnabled="true"
        maxRequests="32" maxSessions="10" maxTransactions="5"
        useStatementNamespaces="false" />

    <typeAlias type="com.dp.plat.util.DateTimeTypeHandler" alias="DateTimeHandler" />
    <typeAlias type="com.dp.plat.ibatis.cache.LRUCacheController" alias="CopyLRU" />
    <typeAlias type="com.dp.plat.ibatis.handler.FastjsonTypeHandler" alias="JsonTypeHandler" />

    <typeHandler javaType="com.dp.plat.type.DateTime" callback="DateTimeHandler" jdbcType="int" />
    <!-- JSON 类型处理器 -->
    <typeHandler jdbcType="JSON" javaType="com.dp.plat.subcontract.entity.JsonCustomInfo" callback="JsonTypeHandler"/>
    <typeHandler jdbcType="OTHER" javaType="com.dp.plat.subcontract.entity.JsonCustomInfo" callback="JsonTypeHandler"/>
    <!-- ... -->
</sqlMapConfig>
```

### 6.2 数据刷新 SqlMapClient

`sqlMapConfig.xml`（数据刷新配置）使用 iBatis 自身的事务管理器，独立于 Spring 事务，用于从外部系统抽取数据：

```xml
<sqlMapConfig>
    <properties resource="jdbc.properties"/>
    <settings cacheModelsEnabled="true" enhancementEnabled="true"
        lazyLoadingEnabled="true" maxRequests="32"
        maxSessions="10" maxTransactions="5"
        useStatementNamespaces="true"/>

    <transactionManager type="JDBC">
        <dataSource type="SIMPLE">
            <property name="JDBC.Driver" value="${main.database.driverClassName}"/>
            <property name="JDBC.ConnectionURL" value="${main.database.url}"/>
            <property name="JDBC.Username" value="${main.database.username}"/>
            <property name="JDBC.Password" value="${main.database.password}"/>
            <property name="Pool.PingConnectionsNotUsedFor" value="6000"/>
        </dataSource>
    </transactionManager>

    <sqlMap resource="sql-map-refresh-data-common-config.xml"/>
    <sqlMap resource="sql-map-refresh-data-sap-config.xml"/>
    <sqlMap resource="sql-map-refresh-data-sms-config.xml"/>
    <sqlMap resource="sql-map-refresh-data-sse-config.xml"/>
    <sqlMap resource="sql-map-refresh-data-d365-config.xml"/>
    <sqlMap resource="sql-map-refresh-data-itr-config.xml"/>
    <sqlMap resource="sql-map-refresh-data-oa-config.xml"/>
    <sqlMap resource="sql-map-refresh-data-crm-config.xml"/>
</sqlMapConfig>
```

### 6.3 外部系统独立 SqlMapClient

每个外部系统数据源都配置了独立的 SqlMapClient 三件套（DataSource → SqlMapClient → SqlMapClientTemplate）：

> **重要说明**：SAP/D365/SSE 三个 SqlMapClient 的 `configLocation` 均指向 `classpath:sql-map-config.xml`（主库配置文件），而非各自的独立配置文件（`sqlMapConfigSAP.xml`、`sqlMapConfigD365.xml`、`sqlMapConfigSSE.xml`）。虽然 `config-ibaits` 目录下存在这些独立配置文件，但它们仅用于 Quartz 定时任务的数据刷新场景（通过 iBatis 自身的 `SqlMapClientBuilder` 加载），不被 Spring 管理的 SqlMapClient 引用。

```xml
<!-- SAP SqlMapClient -->
<bean id="sqlMapClientSAP" class="org.springframework.orm.ibatis.SqlMapClientFactoryBean">
    <property name="configLocation">
        <value>classpath:sql-map-config.xml</value>
    </property>
    <property name="dataSource" ref="dataSourceSAP" />
</bean>
<bean id="sqlMapClientTemplateSAP" class="org.springframework.orm.ibatis.SqlMapClientTemplate">
    <property name="sqlMapClient">
        <ref bean="sqlMapClientSAP" />
    </property>
</bean>

<!-- D365 SqlMapClient -->
<bean id="sqlMapClientD365" class="org.springframework.orm.ibatis.SqlMapClientFactoryBean">
    <property name="configLocation">
        <value>classpath:sql-map-config.xml</value>
    </property>
    <property name="dataSource" ref="dataSourceD365" />
</bean>
<bean id="sqlMapClientTemplateD365" class="org.springframework.orm.ibatis.SqlMapClientTemplate">
    <property name="sqlMapClient">
        <ref bean="sqlMapClientD365" />
    </property>
</bean>

<!-- SSE SqlMapClient -->
<bean id="sqlMapClientSSE" class="org.springframework.orm.ibatis.SqlMapClientFactoryBean">
    <property name="configLocation">
        <value>classpath:sql-map-config.xml</value>
    </property>
    <property name="dataSource" ref="dataSourceSSE" />
</bean>
<bean id="sqlMapClientTemplateSSE" class="org.springframework.orm.ibatis.SqlMapClientTemplate">
    <property name="sqlMapClient">
        <ref bean="sqlMapClientSSE" />
    </property>
</bean>
```

**iBatis 配置文件清单**（`config-ibaits/` 目录）：

| 配置文件 | 用途 | 数据源 |
|---------|------|--------|
| `sql-map-config.xml` | 主系统 SQL 映射 | PMS MySQL |
| `sqlMapConfig.xml` | 数据刷新 SQL 映射 | PMS MySQL（iBatis 自管理事务） |
| `sqlMapConfigSAP.xml` | SAP 数据访问 | SAP SQL Server |
| `sqlMapConfigD365.xml` | D365 数据访问 | D365 SQL Server |
| `sqlMapConfigSSE.xml` | SSE 数据访问 | SSE MySQL |
| `sqlMapConfigSMS.xml` | SMS 数据访问 | SMS MySQL |
| `sqlMapConfigCRM.xml` | CRM 数据访问 | CRM SQL Server |
| `sqlMapConfigOA.xml` | OA 数据访问 | OA SQL Server |
| `sqlMapConfigEHR.xml` | EHR 数据访问 | EHR SQL Server |
| `sqlMapConfigITR.xml` | ITR 数据访问 | ITR PostgreSQL |

### 6.4 SqlMapClient 架构总览

```
                    ┌─────────────────────────────────────┐
                    │        Spring 管理的 SqlMapClient     │
                    ├─────────────────────────────────────┤
                    │                                     │
  dataSource ──────►│ sqlMapClient → sqlMapClientTemplate │──► baseDao
  (PMS MySQL)       │                                     │
                    ├─────────────────────────────────────┤
                    │                                     │
  dataSourceSAP ───►│ sqlMapClientSAP → sqlMapClientTemplateSAP │──► baseDao.sqlMapClientTemplateSAP
  (SQL Server)      │                                     │
                    ├─────────────────────────────────────┤
                    │                                     │
  dataSourceD365 ──►│ sqlMapClientD365 → sqlMapClientTemplateD365 │──► baseDao.sqlMapClientTemplateERP
  (SQL Server)      │                                     │
                    ├─────────────────────────────────────┤
                    │                                     │
  dataSourceSSE ───►│ sqlMapClientSSE → sqlMapClientTemplateSSE │──► baseDao.sqlMapClientTemplateSSE
  (MySQL)           │                                     │
                    └─────────────────────────────────────┘

                    ┌─────────────────────────────────────┐
                    │     iBatis 自管理的 SqlMapClient      │
                    ├─────────────────────────────────────┤
                    │  sqlMapConfig.xml (数据刷新)          │
                    │  ├── sql-map-refresh-data-common     │
                    │  ├── sql-map-refresh-data-sap        │
                    │  ├── sql-map-refresh-data-sms        │
                    │  ├── sql-map-refresh-data-sse        │
                    │  ├── sql-map-refresh-data-d365       │
                    │  ├── sql-map-refresh-data-itr        │
                    │  ├── sql-map-refresh-data-oa         │
                    │  └── sql-map-refresh-data-crm        │
                    └─────────────────────────────────────┘
```

---

## 7. MyBatis 扩展配置

`spring-extend-mybatis.xml` 为系统引入了 MyBatis（作为 iBatis 的升级替代），用于扩展模块的持久层访问。

### 7.1 SqlSessionFactory 配置

```xml
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
    <property name="dataSource" ref="dataSource" />
    <property name="mapperLocations" value="classpath*:com/dp/plat/**/mapping/*.xml" />
    <property name="configuration">
        <bean class="org.apache.ibatis.session.Configuration">
            <property name="cacheEnabled" value="true" />
            <property name="callSettersOnNulls" value="true" />
            <property name="defaultExecutorType" value="SIMPLE" />
        </bean>
    </property>
    <property name="typeHandlers">
        <array>
            <bean id="jsonTypeHandler" class="com.dp.plat.ibatis.handler.FastjsonTypeHandler" />
        </array>
    </property>
</bean>
```

**关键配置说明**：

| 配置项 | 值 | 说明 |
|-------|-----|------|
| `dataSource` | `dataSource` | 共享主数据源 |
| `mapperLocations` | `classpath*:com/dp/plat/**/mapping/*.xml` | 自动扫描所有模块下的 mapping XML |
| `cacheEnabled` | `true` | 启用二级缓存 |
| `callSettersOnNulls` | `true` | 查询结果含 null 时仍调用 setter（避免 Map 中缺失字段） |
| `defaultExecutorType` | `SIMPLE` | 简单执行器（每条 SQL 创建新 PreparedStatement） |
| `typeHandlers` | `FastjsonTypeHandler` | JSON 类型处理器（Fastjson 序列化/反序列化） |

### 7.2 Mapper 接口自动扫描

```xml
<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
    <property name="basePackage" value="com.dp.plat.pms.**.dao" />
    <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory" />
</bean>
```

**扫描规则**：
- 扫描 `com.dp.plat.pms.**.dao` 包下的所有接口
- 接口自动注册为 Spring Bean，Bean ID 为接口首字母小写的类名
- 绑定 `sqlSessionFactory`，与主数据源共享连接

### 7.3 组件扫描与注解事务

```xml
<context:component-scan base-package="com.dp.plat.pms" />

<bean id="transactionManager"
    class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <property name="dataSource" ref="dataSource" />
</bean>

<tx:annotation-driven transaction-manager="transactionManager" proxy-target-class="true" />
```

> **注意**：组件扫描的 `base-package` 为 `com.dp.plat.pms`，扫描范围与 MyBatis Mapper 扫描范围（`com.dp.plat.pms.**.dao`）一致，仅包含 PMS 模块下的组件。

> **⚠️ tx:annotation-driven 重复声明**：`applicationContext.xml` 和 `spring-extend-mybatis.xml` 各声明了一处 `<tx:annotation-driven>`。前者无 `proxy-target-class` 属性（默认 `false`，使用 JDK 动态代理），后者设置了 `proxy-target-class="true"`（使用 CGLIB 代理）。由于 Spring 容器中后加载的配置会覆盖前者，最终生效的是 `spring-extend-mybatis.xml` 中的 CGLIB 代理配置。建议统一为一处声明以避免混淆。

**与 iBatis 事务管理的关系**：
- MyBatis 扩展模块使用 `@Transactional` 注解驱动事务
- 与 iBatis 的 `TransactionProxyFactoryBean` 事务代理共存
- 两者共享同一个 `dataSource` 和 `transactionManager`，确保同一数据源的事务一致性

### 7.4 iBatis 与 MyBatis 共存架构

```
┌─────────────────────────────────────────────────────────┐
│                    事务管理器 (共享)                       │
│           DataSourceTransactionManager                   │
│                  └── dataSource                          │
├──────────────────────────┬──────────────────────────────┤
│   iBatis (原有模块)       │   MyBatis (扩展模块)          │
│                          │                              │
│  SqlMapClientFactoryBean │  SqlSessionFactoryBean       │
│       └── sql-map-config │       └── **/mapping/*.xml   │
│                          │                              │
│  SqlMapClientTemplate    │  MapperScannerConfigurer     │
│       └── BaseDao        │       └── com.dp.plat.pms.**.dao │
│                          │                              │
│  TransactionProxy        │  @Transactional             │
│  FactoryBean (*Agent)    │  (注解驱动)                   │
└──────────────────────────┴──────────────────────────────┘
```

---

## 8. Activiti 集成配置

### 8.1 流程引擎配置

`activiti-context.xml` 配置了 Activiti 工作流引擎，与主数据源共享事务：

```xml
<bean id="processEngineConfiguration"
    class="org.activiti.spring.SpringProcessEngineConfiguration">
    <property name="dataSource" ref="dataSource" />
    <property name="transactionManager" ref="transactionManager" />
    <property name="databaseSchemaUpdate" value="true" />
    <property name="mailServerHost" value="mail.dptech.com" />
    <property name="mailServerPort" value="25" />
    <!-- 全局任务监听器 -->
    <property name="customDefaultBpmnParseHandlers">
        <list>
            <bean class="com.dp.plat.activiti.unifytask.handler.UnifyTaskBPMNParserHandler">
                <property name="parsers">
                    <list>
                        <!-- 统一任务监听 -->
                        <bean class="com.dp.plat.activiti.unifytask.handler.UnifyTaskBPMNParser">
                            <property name="implementationType" value="delegateExpression" />
                            <property name="implementation" value="unifyTaskListener" />
                            <property name="events">
                                <list>
                                    <value>create</value>
                                    <value>assignment</value>
                                    <value>complete</value>
                                    <value>delete</value>
                                    <value>ENTITY_ACTIVATED</value>
                                    <value>ENTITY_SUSPENDED</value>
                                </list>
                            </property>
                        </bean>
                        <!-- 项目验收监听 -->
                        <bean class="com.dp.plat.activiti.unifytask.handler.UnifyTaskBPMNParser">
                            <property name="implementationType" value="delegateExpression" />
                            <property name="implementation" value="subcontractInspectionListener" />
                            <property name="events">
                                <list>
                                    <value>create</value>
                                    <value>assignment</value>
                                    <value>complete</value>
                                    <value>delete</value>
                                </list>
                            </property>
                        </bean>
                    </list>
                </property>
            </bean>
        </list>
    </property>
</bean>
```

**关键配置说明**：

| 配置项 | 值 | 说明 |
|-------|-----|------|
| `dataSource` | `dataSource` | 共享主数据源，确保事务一致性 |
| `transactionManager` | `transactionManager` | 共享 Spring 事务管理器 |
| `databaseSchemaUpdate` | `true` | 启动时自动更新/创建表结构 |
| `mailServerHost` | `mail.dptech.com` | 邮件服务器地址 |
| `mailServerPort` | `25` | 邮件服务器端口 |

### 8.2 流程引擎与核心服务

```xml
<bean id="processEngine" class="org.activiti.spring.ProcessEngineFactoryBean">
    <property name="processEngineConfiguration" ref="processEngineConfiguration" />
</bean>

<bean id="repositoryService" factory-bean="processEngine" factory-method="getRepositoryService" />
<bean id="runtimeService" factory-bean="processEngine" factory-method="getRuntimeService" />
<bean id="taskService" factory-bean="processEngine" factory-method="getTaskService" />
<bean id="historyService" factory-bean="processEngine" factory-method="getHistoryService" />
<bean id="formService" factory-bean="processEngine" factory-method="getFormService" />
<bean id="identityService" factory-bean="processEngine" factory-method="getIdentityService" />
```

**Activiti 核心服务 Bean**：

| Bean ID | 服务 | 职责 |
|---------|------|------|
| `repositoryService` | RepositoryService | 流程定义部署、查询 |
| `runtimeService` | RuntimeService | 流程实例启动、管理 |
| `taskService` | TaskService | 任务查询、签收、完成 |
| `historyService` | HistoryService | 历史流程实例查询 |
| `formService` | FormService | 表单数据获取 |
| `identityService` | IdentityService | 用户/组管理 |

### 8.3 全局任务监听器

系统配置了两个全局 BPMN 解析处理器，为所有用户任务自动注入监听器：

**统一任务监听器**（`unifyTaskListener`）：

```xml
<bean id="unifyTaskListener"
    class="com.dp.plat.plus.unifytask.listener.UnifyTaskListener" />
```

监听事件：`create`、`assignment`、`complete`、`delete`、`ENTITY_ACTIVATED`、`ENTITY_SUSPENDED`

**项目验收监听器**（`subcontractInspectionListener`）：

```xml
<bean id="subcontractInspectionListener"
    class="com.dp.plat.subcontract.listener.SubcontractInspectionListener" />
```

监听事件：`create`、`assignment`、`complete`、`delete`

### 8.4 WorkFlowService 业务集成

```xml
<bean id="workFlowService" class="com.dp.plat.service.WorkFlowServiceImpl">
    <property name="repositoryService" ref="repositoryService" />
    <property name="runtimeService" ref="runtimeService" />
    <property name="taskService" ref="taskService" />
    <property name="formService" ref="formService" />
    <property name="historyService" ref="historyService" />
    <property name="userManageService" ref="userManageService" />
    <property name="workflowDao" ref="workflowDao" />
</bean>
```

### 8.5 事务共享机制

Activiti 与 PMS 主系统共享同一数据源和事务管理器，实现事务一致性：

```
┌────────────────────────────────────────────────────┐
│              Spring 事务管理器                       │
│         DataSourceTransactionManager               │
│                    │                               │
│         ┌─────────┴──────────┐                    │
│         │                    │                     │
│    PMS 业务操作          Activiti 流程操作           │
│  (Service + Dao)      (Repository/Runtime/Task)    │
│         │                    │                     │
│         └─────────┬──────────┘                    │
│                   │                                │
│            dataSource (共享)                        │
│            (PMS MySQL)                             │
└────────────────────────────────────────────────────┘
```

**事务共享的好处**：
1. PMS 业务操作与 Activiti 流程操作在同一事务中，保证数据一致性
2. 如果业务操作失败回滚，流程操作也会回滚
3. 无需配置 XA 分布式事务，简化了事务管理

### 8.6 组件扫描

```xml
<context:component-scan base-package="com.dp.plat.*.unifytask" />
```

自动扫描统一任务模块下的 Spring 组件（`@Component`、`@Service` 等注解标注的类）。

---

## 附录：上下文 Bean

### userContext（Session 范围）

```xml
<bean id="userContext" class="com.dp.plat.context.UserContext" scope="session">
    <aop:scoped-proxy />
</bean>
```

- 作用域为 `session`，每个 HTTP 会话一个实例
- 使用 `<aop:scoped-proxy />` 生成作用域代理，允许在 Singleton Bean 中注入
- 存储当前登录用户信息，供 Service 层获取操作人

### systemContext（系统参数）

```xml
<bean id="systemContext" class="com.dp.plat.context.SystemContext"
    init-method="init" lazy-init="true" />
```

- 系统启动时调用 `init()` 方法，从数据库加载系统参数
- 支持运行时 `refresh()` 刷新参数
- 提供 `getTextValue()`、`getIntegerValue()`、`getCacheJsonValue()` 等参数获取方法

### springContext（ApplicationContext 持有者）

```xml
<bean id="springContext" class="com.dp.plat.context.SpringContext" />
```

- 实现 `ApplicationContextAware` 接口
- 提供静态方法 `getBean()`，允许非 Spring 管理的类访问容器中的 Bean
