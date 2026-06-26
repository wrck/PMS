# PMS 多数据源架构文档

## 1. 数据源总览

PMS 系统采用多数据源架构，同时连接 9 个不同的数据库，涵盖 MySQL、SQL Server、PostgreSQL 三种数据库类型，用于实现与 SAP、D365、SMS、CRM、OA、EHR、SSE、ITR 等外部系统的数据集成与同步。

| 序号 | 数据源名称 | 数据库类型 | 数据库名 | 服务器地址 | 用途 |
|------|-----------|-----------|---------|-----------|------|
| 1 | PMS 主库 | MySQL | dppms_d365 | 本地 / spmstest.dptech.com:3306 | 系统核心业务数据存储 |
| 2 | SAP | SQL Server | DIPULive | saptest.dptech.com:1433 | SAP ERP 系统数据同步 |
| 3 | D365 | SQL Server | AXDB | 172.17.92.90:1433 | Dynamics 365 ERP 数据同步 |
| 4 | SMS | MySQL | dpsms | smstest.dptech.com:3306 | 售前管理系统数据同步 |
| 5 | CRM | SQL Server | crmtest_MSCRM | 172.17.90.134:1433 | CRM 客户关系管理数据同步 |
| 6 | OA | SQL Server | v71 | 172.17.90.70:1433 | OA 办公系统员工数据同步 |
| 7 | EHR | SQL Server | DP | 172.17.74.5:1433 | EHR 人力资源系统数据同步 |
| 8 | SSE | MySQL | efrs | 本地 / 10.102.0.14:3306 | SSE 服务支持系统数据同步 |
| 9 | ITR | PostgreSQL | dptech | 172.17.90.123:5432 | ITR IT 服务管理系统数据同步 |

---

## 2. 数据源详细配置

### 2.1 PMS 主库（MySQL - dppms_d365）

PMS 主库为系统核心数据存储，使用 Apache DBCP 连接池管理连接。

**配置文件**：`config/profiles/dev/jdbc.properties`

```properties
main.database.driverClassName=com.p6spy.engine.spy.P6SpyDriver
main.database.url=jdbc:p6spy:mysql:///dppms_d365?noAccessToProcedureBodies=true&allowMultiQueries=true&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true&sendFractionalSeconds=false&useSSL=false
main.database.username=root
main.database.password=!Q@W3e4r
```

**Spring Bean 配置**（`applicationContext.xml`）：

```xml
<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
    <property name="driverClassName" value="${main.database.driverClassName}"/>
    <property name="url" value="${main.database.url}"/>
    <property name="username" value="${main.database.username}"/>
    <property name="password" value="${main.database.password}"/>
    <property name="initialSize" value="${main.database.initialSize}"/>
    <property name="maxActive" value="${main.database.maxActive}"/>
    <property name="maxIdle" value="${main.database.maxIdle}"/>
    <property name="minIdle" value="${main.database.minIdle}"/>
    <property name="logAbandoned" value="${main.database.logAbandoned}"/>
    <property name="removeAbandoned" value="${main.database.removeAbandoned}"/>
    <property name="removeAbandonedTimeout" value="${main.database.removeAbandonedTimeout}"/>
    <property name="maxWait" value="${main.database.maxWait}"/>
    <property name="testOnBorrow" value="true"/>
    <property name="validationQuery" value="select 1"/>
</bean>
```

**iBatis 配置**（`sqlMapConfig.xml`）：

```xml
<sqlMapConfig>
    <properties resource="jdbc.properties"/>
    <settings cacheModelsEnabled="true" enhancementEnabled="true"
        lazyLoadingEnabled="true" maxRequests="32" maxSessions="10"
        maxTransactions="5" useStatementNamespaces="true"/>
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

> **说明**：主库的 `sqlMapConfig.xml` 同时加载了所有外部系统的 SQL 映射文件，用于在主库事务中执行跨库数据同步操作。

### 2.2 SAP 数据源（SQL Server - DIPULive）

```properties
sap.database.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
sap.database.url=jdbc:sqlserver://saptest.dptech.com:1433;databaseName=DIPULive
sap.database.username=PMS
sap.database.password=ju56#rd95D9
```

**Spring Bean 配置**：

```xml
<bean id="dataSourceSAP" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
    <property name="driverClassName" value="${sap.database.driverClassName}"/>
    <property name="url" value="${sap.database.url}"/>
    <property name="username" value="${sap.database.username}"/>
    <property name="password" value="${sap.database.password}"/>
</bean>
<bean id="sqlMapClientSAP" class="org.springframework.orm.ibatis.SqlMapClientFactoryBean">
    <property name="configLocation" value="classpath:sql-map-config.xml"/>
    <property name="dataSource" ref="dataSourceSAP"/>
</bean>
<bean id="sqlMapClientTemplateSAP" class="org.springframework.orm.ibatis.SqlMapClientTemplate">
    <property name="sqlMapClient" ref="sqlMapClientSAP"/>
</bean>
```

> **说明**：Spring 管理的 SAP SqlMapClient 的 `configLocation` 指向 `sql-map-config.xml`（主库配置文件），而非 `sqlMapConfigSAP.xml`。这是因为 Spring 管理的 SqlMapClient 主要用于在 Service 层直接查询 SAP 数据库，使用主库的 SQL 映射定义。`sqlMapConfigSAP.xml` 仅用于 Quartz 定时任务的数据刷新场景。

**iBatis 独立配置**（`sqlMapConfigSAP.xml`）：

```xml
<transactionManager type="JDBC">
    <dataSource type="SIMPLE">
        <property name="JDBC.Driver" value="${sap.database.driverClassName}"/>
        <property name="JDBC.ConnectionURL" value="${sap.database.url}"/>
        <property name="JDBC.Username" value="${sap.database.username}"/>
        <property name="JDBC.Password" value="${sap.database.password}"/>
    </dataSource>
</transactionManager>
<sqlMap resource="sql-map-refresh-data-sap-config.xml"/>
```

### 2.3 D365 数据源（SQL Server - AXDB）

```properties
d365.database.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
d365.database.url=jdbc:sqlserver://172.17.92.90:1433;databaseName=AXDB
d365.database.username=SSE
d365.database.password=!q2w3e4r
```

**Spring Bean 配置**：

```xml
<bean id="dataSourceD365" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
    <property name="driverClassName" value="${d365.database.driverClassName}"/>
    <property name="url" value="${d365.database.url}"/>
    <property name="username" value="${d365.database.username}"/>
    <property name="password" value="${d365.database.password}"/>
</bean>
<bean id="sqlMapClientD365" class="org.springframework.orm.ibatis.SqlMapClientFactoryBean">
    <property name="configLocation" value="classpath:sql-map-config.xml"/>
    <property name="dataSource" ref="dataSourceD365"/>
</bean>
<bean id="sqlMapClientTemplateD365" class="org.springframework.orm.ibatis.SqlMapClientTemplate">
    <property name="sqlMapClient" ref="sqlMapClientD365"/>
</bean>
```

> **风险提示**：BaseDao 中 D365 对应的属性名为 `sqlMapClientTemplateERP`，而 Spring Bean ID 为 `sqlMapClientTemplateD365`。由于 `applicationContext-dao.xml` 使用 `default-autowire="byName"`，属性名与 Bean 名不匹配会导致自动注入失败，`sqlMapClientTemplateERP` 在运行时为 `null`。如需在 DAO 中访问 D365 数据源，需显式注入或修改属性名。

**iBatis 独立配置**（`sqlMapConfigD365.xml`）：

```xml
<transactionManager type="JDBC">
    <dataSource type="SIMPLE">
        <property name="JDBC.Driver" value="${d365.database.driverClassName}"/>
        <property name="JDBC.ConnectionURL" value="${d365.database.url}"/>
        <property name="JDBC.Username" value="${d365.database.username}"/>
        <property name="JDBC.Password" value="${d365.database.password}"/>
    </dataSource>
</transactionManager>
<sqlMap resource="sql-map-refresh-data-d365-config.xml"/>
```

### 2.4 SMS 数据源（MySQL - dpsms）

```properties
sms.database.driverClassName=com.mysql.cj.jdbc.Driver
sms.database.url=jdbc:mysql://smstest.dptech.com:3306/dpsms?serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true&sendFractionalSeconds=false&useSSL=false
sms.database.username=sms
sms.database.password=9ijy5rbg3
```

**iBatis 独立配置**（`sqlMapConfigSMS.xml`）：

```xml
<transactionManager type="JDBC">
    <dataSource type="SIMPLE">
        <property name="JDBC.Driver" value="${sms.database.driverClassName}"/>
        <property name="JDBC.ConnectionURL" value="${sms.database.url}"/>
        <property name="JDBC.Username" value="${sms.database.username}"/>
        <property name="JDBC.Password" value="${sms.database.password}"/>
    </dataSource>
</transactionManager>
<sqlMap resource="sql-map-refresh-data-sms-config.xml"/>
```

### 2.5 CRM 数据源（SQL Server - crmtest_MSCRM）

```properties
crm.database.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
crm.database.url=jdbc:sqlserver://172.17.90.134:1433;databaseName=crmtest_MSCRM
crm.database.username=ituser
crm.database.password=UH54#d!34Ds
```

**iBatis 独立配置**（`sqlMapConfigCRM.xml`）：

```xml
<transactionManager type="JDBC">
    <dataSource type="SIMPLE">
        <property name="JDBC.Driver" value="${crm.database.driverClassName}"/>
        <property name="JDBC.ConnectionURL" value="${crm.database.url}"/>
        <property name="JDBC.Username" value="${crm.database.username}"/>
        <property name="JDBC.Password" value="${crm.database.password}"/>
    </dataSource>
</transactionManager>
<sqlMap resource="sql-map-refresh-data-sms-config.xml"/>
<sqlMap resource="sql-map-refresh-data-crm-config.xml"/>
```

### 2.6 OA 数据源（SQL Server - v71）

```properties
oa.database.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
oa.database.url=jdbc:sqlserver://172.17.90.70:1433;databaseName=v71
oa.database.username=pms
oa.database.password=!q2w3e4r
```

**iBatis 独立配置**（`sqlMapConfigOA.xml`）：

```xml
<transactionManager type="JDBC">
    <dataSource type="SIMPLE">
        <property name="JDBC.Driver" value="${oa.database.driverClassName}"/>
        <property name="JDBC.ConnectionURL" value="${oa.database.url}"/>
        <property name="JDBC.Username" value="${oa.database.username}"/>
        <property name="JDBC.Password" value="${oa.database.password}"/>
    </dataSource>
</transactionManager>
<sqlMap resource="sql-map-refresh-data-common-config.xml"/>
<sqlMap resource="sql-map-refresh-data-oa-config.xml"/>
```

### 2.7 EHR 数据源（SQL Server - DP）

```properties
ehr.database.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
ehr.database.url=jdbc:sqlserver://172.17.74.5:1433;databaseName=DP
ehr.database.username=pms
ehr.database.password=5RDfg*4#d)s
```

**iBatis 独立配置**（`sqlMapConfigEHR.xml`）：

```xml
<transactionManager type="JDBC">
    <dataSource type="SIMPLE">
        <property name="JDBC.Driver" value="${ehr.database.driverClassName}"/>
        <property name="JDBC.ConnectionURL" value="${ehr.database.url}"/>
        <property name="JDBC.Username" value="${ehr.database.username}"/>
        <property name="JDBC.Password" value="${ehr.database.password}"/>
    </dataSource>
</transactionManager>
<sqlMap resource="sql-map-refresh-data-common-config.xml"/>
```

### 2.8 SSE 数据源（MySQL - efrs）

```properties
sse.database.driverClassName=com.mysql.cj.jdbc.Driver
sse.database.url=jdbc:mysql:///efrs?serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true&sendFractionalSeconds=false&useSSL=false
sse.database.username=root
sse.database.password=!Q@W3e4r
```

**Spring Bean 配置**：

```xml
<bean id="dataSourceSSE" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
    <property name="driverClassName" value="${sse.database.driverClassName}"/>
    <property name="url" value="${sse.database.url}"/>
    <property name="username" value="${sse.database.username}"/>
    <property name="password" value="${sse.database.password}"/>
</bean>
<bean id="sqlMapClientSSE" class="org.springframework.orm.ibatis.SqlMapClientFactoryBean">
    <property name="configLocation" value="classpath:sql-map-config.xml"/>
    <property name="dataSource" ref="dataSourceSSE"/>
</bean>
<bean id="sqlMapClientTemplateSSE" class="org.springframework.orm.ibatis.SqlMapClientTemplate">
    <property name="sqlMapClient" ref="sqlMapClientSSE"/>
</bean>
```

**iBatis 独立配置**（`sqlMapConfigSSE.xml`）：

```xml
<transactionManager type="JDBC">
    <dataSource type="SIMPLE">
        <property name="JDBC.Driver" value="${sse.database.driverClassName}"/>
        <property name="JDBC.ConnectionURL" value="${sse.database.url}"/>
        <property name="JDBC.Username" value="${sse.database.username}"/>
        <property name="JDBC.Password" value="${sse.database.password}"/>
    </dataSource>
</transactionManager>
<sqlMap resource="sql-map-refresh-data-sse-config.xml"/>
```

### 2.9 ITR 数据源（PostgreSQL - dptech）

```properties
itr.database.driverClassName=org.postgresql.Driver
itr.database.url=jdbc:postgresql://172.17.90.123:5432/dptech
itr.database.username=pms_reader
itr.database.password=v&tFL4raURMD
```

**iBatis 独立配置**（`sqlMapConfigITR.xml`）：

```xml
<transactionManager type="JDBC">
    <dataSource type="SIMPLE">
        <property name="JDBC.Driver" value="${itr.database.driverClassName}"/>
        <property name="JDBC.ConnectionURL" value="${itr.database.url}"/>
        <property name="JDBC.Username" value="${itr.database.username}"/>
        <property name="JDBC.Password" value="${itr.database.password}"/>
    </dataSource>
</transactionManager>
<sqlMap resource="sql-map-refresh-data-itr-config.xml"/>
```

---

## 3. 连接池配置说明

PMS 主库使用 **Apache DBCP**（BasicDataSource）连接池，外部数据源使用 Spring 的 **DriverManagerDataSource**（无连接池）或 iBatis 内置的 **SIMPLE** 数据源。

### 3.1 主库连接池参数

| 参数 | 值 | 说明 |
|------|-----|------|
| `initialSize` | 2 | 池启动时创建的初始连接数量 |
| `maxActive` | 300 | 同一时间可从池分配的最多连接数量，0 表示无限制 |
| `maxIdle` | 50 | 池里不会被释放的最多空闲连接数量，0 表示无限制 |
| `minIdle` | 3 | 在不新建连接的条件下，池中保持空闲的最少连接数 |
| `removeAbandoned` | true | 是否自动回收超时连接 |
| `removeAbandonedTimeout` | 180 | 自动回收超时时间（秒） |
| `logAbandoned` | true | 是否在自动回收超时连接时打印连接的超时错误 |
| `maxWait` | 60000 | 超时等待时间（毫秒），60 秒 |
| `testOnBorrow` | true | 从池中获取连接时是否进行有效性检测 |
| `validationQuery` | select 1 | 连接有效性检测 SQL |

### 3.2 外部数据源连接策略

外部数据源（SAP、D365、SSE）在 Spring 中使用 `DriverManagerDataSource`，每次请求都会创建新连接，适用于低频定时任务场景。在 iBatis 独立配置中使用 `SIMPLE` 数据源类型，由 iBatis 自行管理简单的连接池。

### 3.3 iBatis 全局配置参数

所有 `sqlMapConfig*.xml` 共享以下 iBatis 运行参数：

| 参数 | 值 | 说明 |
|------|-----|------|
| `cacheModelsEnabled` | true | 启用 SQL 映射缓存 |
| `enhancementEnabled` | true | 启用字节码增强 |
| `lazyLoadingEnabled` | true | 启用延迟加载 |
| `maxRequests` | 32 | 最大并发请求数 |
| `maxSessions` | 10 | 最大会话数 |
| `maxTransactions` | 5 | 最大事务数 |
| `useStatementNamespaces` | true | 使用语句命名空间 |

---

## 4. iBatis / MyBatis 双 ORM 共存设计

PMS 系统同时使用 **iBatis 2**（原版）和 **MyBatis**（iBatis 后续版本）两套 ORM 框架，实现渐进式迁移。

### 4.1 iBatis 2（核心业务）

iBatis 2 是系统原有的 ORM 框架，承载所有核心业务的数据访问。

**架构层次**：

```
┌─────────────────────────────────────────────────────┐
│                    Action 层                         │
│            (Struts2 Action 类)                       │
├─────────────────────────────────────────────────────┤
│                   Service 层                         │
│     (Spring 事务代理 + AOP 日志拦截)                  │
├─────────────────────────────────────────────────────┤
│                    DAO 层                            │
│  BaseDao → sqlMapClientTemplate (iBatis 2)          │
│  BaseDao → sqlMapClientTemplateSAP (SAP)            │
│  BaseDao → sqlMapClientTemplateD365 (D365)          │
│  BaseDao → sqlMapClientTemplateSSE (SSE)            │
├─────────────────────────────────────────────────────┤
│              SQL Map 映射文件                        │
│  sql-map-refresh-data-*-config.xml                  │
└─────────────────────────────────────────────────────┘
```

**核心 Bean 定义**：

```xml
<bean id="baseDao" abstract="true">
    <property name="sqlMapClientTemplate" ref="sqlMapClientTemplate"/>
</bean>

<bean id="sqlMapClient" class="org.springframework.orm.ibatis.SqlMapClientFactoryBean">
    <property name="configLocation" value="classpath:sql-map-config.xml"/>
    <property name="dataSource" ref="dataSource"/>
</bean>

<bean id="sqlMapClientTemplate" class="org.springframework.orm.ibatis.SqlMapClientTemplate">
    <property name="sqlMapClient" ref="sqlMapClient"/>
</bean>
```

**DAO 继承体系**：

```
baseDao (abstract)
  ├── opLoggerDao (OpLogDaoImpl)
  ├── baseContextLoggerDao (abstract, 含 opLoggerDao)
  │   └── loginDao (LoginDaoImpl)
  ├── userManageDao (UserManageDaoImpl)
  ├── roleManageDao (RoleManageDaoImpl)
  ├── departmentManageDao (DepartmentManageDaoImpl)
  ├── passwordDao (PasswordDaoImpl)
  ├── projectDao (ProjectDaoImpl)
  ├── basicDataDao (BasicDataDaoImpl)
  └── ... (其他业务 DAO)
```

### 4.2 MyBatis（扩展模块）

MyBatis 用于新增扩展模块，通过 `spring-extend-mybatis.xml` 独立配置，与 iBatis 2 共享主库 `dataSource`。

**配置文件**：`config-spring/spring-extend-mybatis.xml`

```xml
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
    <property name="dataSource" ref="dataSource"/>
    <property name="mapperLocations" value="classpath*:com/dp/plat/**/mapping/*.xml"/>
    <property name="configuration">
        <bean class="org.apache.ibatis.session.Configuration">
            <property name="cacheEnabled" value="true"/>
            <property name="callSettersOnNulls" value="true"/>
            <property name="defaultExecutorType" value="SIMPLE"/>
        </bean>
    </property>
    <property name="typeHandlers">
        <array>
            <bean class="com.dp.plat.ibatis.handler.FastjsonTypeHandler"/>
        </array>
    </property>
</bean>

<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
    <property name="basePackage" value="com.dp.plat.pms.**.dao"/>
    <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
</bean>
```

### 4.3 双 ORM 共存策略

| 维度 | iBatis 2 | MyBatis |
|------|----------|---------|
| **包路径** | `com.dp.plat.dao.*` | `com.dp.plat.pms.**.dao` |
| **映射文件** | `sql-map-*.xml` | `com/dp/plat/**/mapping/*.xml` |
| **DAO 方式** | 继承 `BaseDao`，使用 `SqlMapClientTemplate` | Mapper 接口自动扫描 |
| **事务管理** | `TransactionProxyFactoryBean` 代理 | `@Transactional` 注解驱动 |
| **数据源** | 主库 + 外部库 | 仅主库 |
| **TypeHandler** | iBatis 内置 TypeHandler | 自定义 `FastjsonTypeHandler` 等 |
| **适用场景** | 核心业务模块 | 新增扩展模块 |

---

## 5. 数据源路由策略

### 5.1 主库事务内操作

主库（PMS）的所有写操作和核心读操作在 Spring 事务管理下执行，使用 `DataSourceTransactionManager` 统一管理事务边界。

**事务代理配置**：

```xml
<bean id="transactionManager"
    class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <property name="dataSource" ref="dataSource"/>
</bean>

<bean id="transactionBaseService" abstract="true"
    class="org.springframework.transaction.interceptor.TransactionProxyFactoryBean">
    <property name="transactionManager" ref="transactionManager"/>
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

**事务传播规则**：所有以 `insert*`、`update*`、`delete*`、`add*`、`save*`、`do*`、`submit*` 等前缀命名的 Service 方法自动纳入事务管理，传播级别为 `PROPAGATION_REQUIRED`。

### 5.2 外部库只读查询

外部数据库（SAP、D365、SMS、CRM、OA、EHR、SSE、ITR）的访问采用以下两种策略：

#### 策略一：Spring 管理的 SqlMapClientTemplate（SAP、D365、SSE）

通过 Spring 注入的 `sqlMapClientTemplateSAP`、`sqlMapClientTemplateD365`、`sqlMapClientTemplateSSE` 进行只读查询，不参与主库事务。

```
┌──────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│   Service 层     │────▶│  sqlMapClient    │────▶│  dataSource      │
│  (主库事务内)    │     │  Template        │     │  (PMS 主库)      │
│                  │     ├──────────────────┤     ├──────────────────┤
│                  │────▶│  sqlMapClient    │────▶│  dataSourceSAP   │
│                  │     │  TemplateSAP     │     │  (SAP 只读)      │
│                  │     ├──────────────────┤     ├──────────────────┤
│                  │────▶│  sqlMapClient    │────▶│  dataSourceD365  │
│                  │     │  TemplateD365    │     │  (D365 只读)     │
│                  │     ├──────────────────┤     ├──────────────────┤
│                  │────▶│  sqlMapClient    │────▶│  dataSourceSSE   │
│                  │     │  TemplateSSE     │     │  (SSE 只读)      │
└──────────────────┘     └──────────────────┘     └──────────────────┘
```

#### 策略二：iBatis 独立 SqlMapClient（SMS、CRM、OA、EHR、ITR）

通过 Quartz 定时任务，使用 iBatis 独立的 `sqlMapConfig*.xml` 创建 `SqlMapClient`，直接读取外部数据库数据后写入主库。

```java
Reader readerSap = Resources.getResourceAsReader("sqlMapConfigOA.xml");
SqlMapClient sqlMapSap = SqlMapClientBuilder.buildSqlMapClient(readerSap);

Reader reader = Resources.getResourceAsReader("sqlMapConfig.xml");
SqlMapClient sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
```

**数据同步流程**：

```
┌─────────────┐    定时读取     ┌─────────────┐   写入主库   ┌─────────────┐
│  外部数据库  │───────────────▶│  Quartz Job │────────────▶│  PMS 主库   │
│  (OA/EHR/   │  sqlMapConfig  │  (GainPerson│  sqlMapConfig│  (dppms_    │
│   SMS/CRM/  │  OA.xml 等)    │  ByOA 等)   │  .xml)      │   d365)     │
│   ITR)      │                │             │             │             │
└─────────────┘                └─────────────┘             └─────────────┘
```

### 5.3 路由策略总结

| 数据源 | 访问方式 | 事务管理 | 操作类型 | 典型场景 |
|--------|---------|---------|---------|---------|
| PMS 主库 | Spring DBCP + SqlMapClientTemplate | Spring 事务管理 | 读写 | 核心业务 CRUD |
| SAP | Spring DriverManagerDataSource + SqlMapClientTemplateSAP | 无事务 | 只读 | 项目数据查询 |
| D365 | Spring DriverManagerDataSource + SqlMapClientTemplateD365 | 无事务 | 只读 | D365 数据查询 |
| SSE | Spring DriverManagerDataSource + SqlMapClientTemplateSSE | 无事务 | 只读 | SSE 数据查询 |
| SMS | iBatis 独立 SqlMapClient（Quartz Job） | 无事务 | 只读 | 售前数据同步 |
| CRM | iBatis 独立 SqlMapClient（Quartz Job） | 无事务 | 只读 | 客户数据同步 |
| OA | iBatis 独立 SqlMapClient（Quartz Job） | 无事务 | 只读 | 员工数据同步（已废弃） |
| EHR | iBatis 独立 SqlMapClient（Quartz Job） | 无事务 | 只读 | 员工数据同步 |
| ITR | iBatis 独立 SqlMapClient（Quartz Job） | 无事务 | 只读 | IT 服务数据同步 |

---

## 6. P6Spy SQL 监控配置

### 6.1 概述

开发环境使用 **P6Spy** 对主库 SQL 执行情况进行监控和日志记录。P6Spy 作为 JDBC 代理驱动，拦截应用程序与数据库之间的所有 SQL 通信。

### 6.2 驱动代理配置

在 `jdbc.properties` 中，主库的驱动和 URL 被替换为 P6Spy 代理：

```properties
# 原始配置（被注释）
#main.database.driverClassName=com.mysql.cj.jdbc.Driver
#main.database.url=jdbc:mysql:///dppms_d365?...

# P6Spy 代理配置（开发环境启用）
main.database.driverClassName=com.p6spy.engine.spy.P6SpyDriver
main.database.url=jdbc:p6spy:mysql:///dppms_d365?noAccessToProcedureBodies=true&allowMultiQueries=true&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true&sendFractionalSeconds=false&useSSL=false
```

**URL 格式**：`jdbc:p6spy:<原始JDBC URL>`

### 6.3 P6Spy 配置文件

**配置文件**：`config/profiles/dev/spy.properties`

```properties
module.log=com.p6spy.engine.logging.P6LogFactory
logMessageFormat=com.p6spy.engine.spy.appender.CustomLineFormat
customLogMessageFormat=%(currentTime) | %(executionTime) ms | %(category) | %(sqlSingleLine)
appender=com.p6spy.engine.spy.appender.Slf4JLogger
```

**配置项说明**：

| 配置项 | 值 | 说明 |
|--------|-----|------|
| `module.log` | `com.p6spy.engine.logging.P6LogFactory` | 启用 SQL 日志记录模块 |
| `logMessageFormat` | `CustomLineFormat` | 使用自定义行格式输出 |
| `customLogMessageFormat` | `%(currentTime) \| %(executionTime) ms \| %(category) \| %(sqlSingleLine)` | 日志格式：时间 \| 执行耗时 \| 操作类别 \| SQL 语句（单行） |
| `appender` | `Slf4JLogger` | 通过 SLF4J 输出日志 |

### 6.4 日志输出示例

```
2026-05-19 10:30:45 | 12 ms | statement | SELECT * FROM project WHERE project_id = 123
2026-05-19 10:30:46 | 3 ms | statement | INSERT INTO op_log (user_id, action, create_time) VALUES ('admin', 'LOGIN', '2026-05-19 10:30:46')
2026-05-19 10:30:47 | 156 ms | statement | SELECT p.*, u.real_name FROM project p LEFT JOIN user u ON p.pm_code = u.username WHERE p.status = 1
```

### 6.5 生产环境注意事项

- 生产环境应将 `driverClassName` 切换回 `com.mysql.cj.jdbc.Driver`，URL 去除 `p6spy:` 前缀
- P6Spy 会带来额外的性能开销，仅建议在开发和测试环境使用
- 可通过 `spy.properties` 中的 `exclude` 配置排除不需要监控的 SQL 语句

---

## 7. 配置文件索引

| 文件路径 | 用途 |
|---------|------|
| `config/profiles/dev/jdbc.properties` | 所有数据源连接参数（按环境分离） |
| `config/profiles/dev/spy.properties` | P6Spy SQL 监控配置 |
| `config-spring/applicationContext.xml` | Spring 核心配置，含主库和外部库 DataSource、SqlMapClient、事务管理 |
| `config-spring/spring-extend-mybatis.xml` | MyBatis 扩展模块配置 |
| `config-spring/applicationContext-dao.xml` | DAO 层 Bean 定义 |
| `config-spring/applicationContext-service.xml` | Service 层 Bean 定义及事务代理 |
| `config-ibaits/sqlMapConfig.xml` | 主库 iBatis 配置（含所有外部系统 SQL 映射） |
| `config-ibaits/sqlMapConfigSAP.xml` | SAP 数据源 iBatis 独立配置 |
| `config-ibaits/sqlMapConfigD365.xml` | D365 数据源 iBatis 独立配置 |
| `config-ibaits/sqlMapConfigSMS.xml` | SMS 数据源 iBatis 独立配置 |
| `config-ibaits/sqlMapConfigCRM.xml` | CRM 数据源 iBatis 独立配置 |
| `config-ibaits/sqlMapConfigOA.xml` | OA 数据源 iBatis 独立配置 |
| `config-ibaits/sqlMapConfigEHR.xml` | EHR 数据源 iBatis 独立配置 |
| `config-ibaits/sqlMapConfigSSE.xml` | SSE 数据源 iBatis 独立配置 |
| `config-ibaits/sqlMapConfigITR.xml` | ITR 数据源 iBatis 独立配置 |
