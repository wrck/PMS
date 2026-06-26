# 故障排查与避坑指南

> 本文档基于 PMS 与 SPMS 项目实际运行经验，汇总常见技术问题、故障排查方法及解决方案案例库，所有案例均来自真实代码与配置分析，并标注避坑指南与最佳实践。

---

## 目录

1. [常见技术问题](#1-常见技术问题)
2. [故障排查方法](#2-故障排查方法)
3. [解决方案案例库](#3-解决方案案例库)
4. [避坑指南与最佳实践](#4-避坑指南与最佳实践)

---

## 1. 常见技术问题

### 1.1 PMS-struts 与 PMS-springmvc Struts2 版本不一致

**问题描述**：PMS-struts 使用 Struts2 2.3.35，PMS-springmvc 使用 Struts2 2.5.30，两个模块的 Struts2 版本不一致。

**影响**：
- API 不兼容：2.3.x 和 2.5.x 部分类和方法签名不同
- 安全漏洞：2.3.35 存在已知漏洞（S2-057 等）
- 配置差异：2.5.x 默认禁用 DMI，需显式启用

**解决方案**：
1. 跨模块调用时确认 Struts2 版本，不混用 API
2. PMS-struts 升级至 2.5.x（需评估兼容性，特别是 DMI 和拦截器配置）
3. PMS-security 模块编译时依赖 2.3.35，需确认与 2.5.30 的兼容性

> **避坑指南**：不要假设两个模块的 Struts2 版本一致。修改 PMS-security 的 Struts2 集成类时，需同时考虑两个版本的兼容性。

### 1.2 PMS-springboot 模块不存在导致构建失败

**问题描述**：PMS 根 `pom.xml` 中列出了 `PMS-springboot` 模块，但磁盘上无此目录，执行 `mvn clean package` 时构建失败。

**错误信息**：

```
Could not find the module com.dp.plat:PMS-springboot
```

**解决方案**：
1. 从根 `pom.xml` 的 `<modules>` 中移除 `PMS-springboot`
2. 或使用 `-pl` 参数指定构建模块，跳过不存在的模块：

```bash
mvn clean package -pl core,PMS-struts,PMS-springmvc -am
```

> **避坑指南**：构建前检查 `pom.xml` 中的模块列表与磁盘目录是否一致。使用 `-pl`（projects list）和 `-am`（also make）可精确控制构建范围。

### 1.3 SPMS 非 Maven 项目无法用 mvn 构建

**问题描述**：SPMS 是传统 Eclipse 项目（非 Maven），无法使用 `mvn` 命令构建。

**影响**：
- 无法通过 Maven 管理依赖
- 无法使用 Maven Profile 管理多环境配置
- 依赖 JAR 手动管理（`WebContent/WEB-INF/lib/`）

**解决方案**：
1. 通过 Eclipse Export WAR 导出
2. 或使用 Ant 脚本构建
3. 依赖冲突需手动排查（`WebContent/WEB-INF/lib/` 下存在多版本 JAR）

**SPMS 依赖冲突示例**：

| 库 | 冲突版本 |
|----|----------|
| commons-logging | 1.0.4 / 1.1.1 |
| commons-lang3 | 3.1 / 3.6 |
| activation | 1.1 / 1.1.1 |

> **避坑指南**：SPMS 使用 JDK 1.7，不可使用 JDK 1.8+ 特性（Lambda、Stream、`java.time`）。引入新依赖时需确认兼容 JDK 1.7。

### 1.4 iBATIS 与 MyBatis 混用问题

**问题描述**：PMS-struts 和 SPMS 使用 iBATIS 2.x，PMS-springmvc 和 core 使用 MyBatis 3.x，两者语法不兼容。

**语法差异**：

| 特性 | iBATIS 2.x（PMS-struts/SPMS） | MyBatis 3.x（PMS-springmvc/core） |
|------|-------------------------------|-----------------------------------|
| 配置文件 | `sql-map-config.xml` + `<sqlMap>` | `mybatis-config.xml` + `<mapper>` |
| 映射根元素 | `<sqlMap>` | `<mapper>` |
| 参数属性 | `parameterClass` | `parameterType` |
| 结果属性 | `resultClass` / `resultMap` | `resultType` / `resultMap` |
| 占位符 | `#var#` | `#{var}` |
| 字符串拼接 | `$var$` | `${var}` |
| Spring 集成 | `SqlMapClientTemplate` | `SqlSessionTemplate` |

**解决方案**：
1. 修改 SQL 映射时确认所属 ORM 框架
2. PMS-struts/SPMS 使用 iBATIS 语法（`#var#`、`parameterClass`）
3. PMS-springmvc/core 使用 MyBatis 语法（`#{var}`、`parameterType`）

> **避坑指南**：iBATIS 的 `useStatementNamespaces` 配置不一致（主配置 `false`，独立配置 `true`），SQL ID 查找规则不同。SPMS 配置文件双份（`config-ibaits/` 与 `build/classes/`），修改时需同步。

### 1.5 system 作用域依赖问题

**问题描述**：PMS-struts 有 system 作用域依赖 `echarts-utils`，位于 `WebContent/WEB-INF/lib/Utils-v0.1.jar`。

**影响**：
- 构建环境依赖：system 作用域依赖不从 Maven 仓库下载，需本地存在
- CI/CD 问题：构建服务器需有对应 JAR 文件
- 依赖传递：system 作用域依赖不传递

**解决方案**：
1. 将 JAR 安装到本地 Maven 仓库：

```bash
mvn install:install-file -Dfile=WebContent/WEB-INF/lib/Utils-v0.1.jar \
    -DgroupId=com.dp.plat -DartifactId=echarts-utils -Dversion=0.1 -Dpackaging=jar
```

2. 或在 pom.xml 中配置 system 作用域：

```xml
<dependency>
    <groupId>com.dp.plat</groupId>
    <artifactId>echarts-utils</artifactId>
    <version>0.1</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/WebContent/WEB-INF/lib/Utils-v0.1.jar</systemPath>
</dependency>
```

> **避坑指南**：system 作用域依赖是 Maven 的反模式，建议将 JAR 安装到私有 Maven 仓库（Nexus/Artifactory），改为 compile 作用域。

---

## 2. 故障排查方法

### 2.1 多数据源问题排查（ThreadLocal 清理）

**故障现象**：请求返回错误数据源的数据，或抛出 "表不存在" 异常。

**排查步骤**：

1. **检查 ThreadLocal 清理**：确认 `DataSourceHolder.setDataSourceType()` 后是否有对应的 `clearDataSourceType()`（在 `finally` 块中）

```java
DataSourceHolder.setDataSourceType("SMS");
try {
    // SMS 数据库操作
} finally {
    // ⚠️ 必须清除，否则线程池复用导致数据源串扰
    DataSourceHolder.clearDataSourceType();
}
```

2. **检查线程池配置**：线程池复用线程时，ThreadLocal 残留会导致数据源串扰
3. **检查事务范围**：跨数据源操作不在同一事务中，外部数据源操作不回滚

**SPMS 多数据源排查**：

SPMS 通过 `BaseDao` 提供多个 `SqlMapClientTemplate`，检查是否使用了正确的模板：

| 数据源 | 模板方法 | 数据库 |
|--------|----------|--------|
| 主库 | `getSqlMapClientTemplate()` | MySQL (dppms_d365) |
| SAP | `getSqlMapClientTemplateSAP()` | SQL Server (DIPULive) |
| D365 | `getSqlMapClientTemplateERP()` | SQL Server (AXDB) |
| SSE | `getSqlMapClientTemplateSSE()` | - |

> **避坑指南**：SPMS 配置了 3 个事务管理器（`transactionManager`、`liveactionManager`、`transactionManagerMes`），跨数据源无分布式事务。`firebird` 和 `mesDataSource` 都指向 `R2EMES5SQL`，可能造成事务冲突。

### 2.2 Shiro 权限缓存不更新排查

**故障现象**：管理员修改用户角色或权限后，用户权限未生效。

**排查步骤**：

1. **确认缓存配置**：检查 `ehcache.xml` 中 `org.apache.shiro.realm.SimpleAccountRealm.authorization` 的 `timeToLiveSeconds`（默认 600 秒 = 10 分钟）

2. **手动清除缓存**：

```java
// 清除指定用户权限缓存
Cache<Object, AuthorizationInfo> cache = cacheManager.getCache("org.apache.shiro.realm.SimpleAccountRealm.authorization");
cache.clear();
```

3. **检查 Realm 实现**：确认 `ShiroRealm.doGetAuthorizationInfo()` 是否正确查询最新权限

4. **等待缓存过期**：临时方案是等待 10 分钟缓存自动过期

> **避坑指南**：修改用户权限后，应在代码中主动清除 Shiro 权限缓存，而非依赖缓存过期。

### 2.3 工作流流程回退问题排查

**故障现象**：Activiti 流程无法回退，或回退后任务分配异常。

**排查步骤**：

1. **查询当前任务**：

```sql
SELECT * FROM ACT_RU_TASK WHERE PROC_INST_ID_ = 'xxx';
```

2. **查询流程变量**：

```sql
SELECT * FROM ACT_RU_VARIABLE WHERE PROC_INST_ID_ = 'xxx';
```

3. **查询历史活动**：

```sql
SELECT * FROM ACT_HI_ACTINST WHERE PROC_INST_ID_ = 'xxx' ORDER BY START_TIME_;
```

4. **检查统一任务表**：

```sql
SELECT * FROM dp_act_unify_task WHERE instId = 'xxx';
```

5. **检查任务监听器**：确认 `AbstractUnifyTaskListener` 的实现逻辑

**常见原因**：
- 流程定义中未配置回退节点
- 任务监听器中 assignee 设置逻辑错误
- `dp_act_unify_task` 推送失败
- 流程实例被悬挂（suspended）

**解决方案**：

```java
// 激活悬挂的流程实例
runtimeService.activateProcessInstanceById(processInstanceId);

// 回退到指定节点
runtimeService.createChangeActivityStateBuilder()
    .processInstanceId(processInstanceId)
    .moveActivityIdTo(currentActivityId, targetActivityId)
    .changeState();
```

### 2.4 D365 接口调用失败排查

**故障现象**：转包流程在 GENERATE_CON 节点调用 D365 API 失败，流程卡住。

**排查步骤**：

1. **检查 D365 API 日志**：确认返回的错误码和消息
2. **验证认证 Token**：检查 Token 是否过期
3. **检查请求参数**：amount、facilitatorId 等是否合规
4. **检查网络连通性**：`telnet <d365_host> <d365_port>`
5. **检查数据源配置**：`jdbc.properties` 中 D365 数据源连接参数

**常见原因**：

| 原因 | 排查方法 | 解决方案 |
|------|----------|----------|
| Token 过期 | 检查 Token 缓存 | 重新获取 Token |
| 网络超时 | telnet 测试 | 增加超时配置 |
| 参数格式错误 | 检查请求日志 | 修正参数 |
| D365 维护中 | 联系 D365 团队 | 等待恢复 |

> **避坑指南**：D365 接口调用应增加重试机制（最多 3 次，间隔递增）和详细日志记录（请求参数、响应结果、耗时）。

### 2.5 Quartz 任务不执行排查

**故障现象**：定时任务未按预期执行。

**排查步骤**：

1. **检查任务配置**：确认 `quartz-job.xml`（PMS-springmvc）或 `beans-quartz.xml`（PMS-struts）中任务是否在 `SchedulerFactoryBean` 的 `triggers` 列表中

```xml
<!-- PMS-struts: 检查 triggers 列表 -->
<bean class="org.springframework.scheduling.quartz.SchedulerFactoryBean">
    <property name="triggers">
        <list>
            <ref bean="ReportTaskTrigger" />
            <ref bean="TaskBySmsTrigger" />
            <!-- 确认新任务已添加 -->
        </list>
    </property>
</bean>
```

2. **检查 Cron 表达式**：确认 Cron 表达式正确

| Cron 表达式 | 含义 |
|-------------|------|
| `0 0/5 * * * ?` | 每 5 分钟 |
| `0 30 23 * * ?` | 每天 23:30 |
| `0 0 14 ? * SUN` | 每周日 14:00 |
| `59 10 23 L * ?` | 每月最后一天 23:10:59 |

3. **检查任务开关**：SPMS 的 `system.properties` 中配置任务默认启动开关

```properties
debug.develop.datatask.default.start=false    # 测试环境默认不启动
release.develop.datatask.default.start=true   # 正式环境默认启动
```

4. **检查 concurrent 配置**：`concurrent=false` 的任务，如果上一次未执行完，下一次不会启动

5. **检查任务类**：PMS-struts 任务类需实现 Quartz `Job` 接口，PMS-springmvc 使用 `MethodInvokingJobDetailFactoryBean` 调用普通 Bean 方法

> **避坑指南**：PMS-springmvc 和 PMS-struts 的 Quartz 配置方式不同。PMS-springmvc 用 `MethodInvokingJobDetailFactoryBean`（`concurrent=false`），PMS-struts 用 `JobDetailFactoryBean`（任务类实现 Job 接口）。

---

## 3. 解决方案案例库

### 案例1：用户登录失败 - 密码加密方式不匹配

**问题描述**：用户使用正确密码登录，系统提示"用户名或密码错误"。

**原因分析**：PMS-struts 存在新旧两种密码加密方式：
- 旧版：`Md5Util.getMD5()`（无盐值，单次 MD5）
- 新版：`PasswordUtil.encryptPassword()`（SHA1 + MD5 加盐迭代）

两种加密结果不同，导致密码比对失败。

**解决方案**：
1. 查询数据库确认密码加密格式：`SELECT username, password FROM fnd_user_info WHERE username = 'xxx'`
2. 使用旧版 MD5 加密输入密码，比对是否匹配
3. 如匹配旧版，更新为新版加密：

```sql
UPDATE fnd_user_info
SET password = PasswordUtil.encryptPassword(username, inputPassword)
WHERE username = 'xxx'
```

**验证步骤**：用户使用原密码重新登录，确认认证成功。

**预防措施**：登录逻辑中增加兼容性判断，旧版密码登录成功后自动升级。

### 案例2：多数据源 ThreadLocal 未清除导致数据串扰

**问题描述**：用户 A 查询 SMS 数据后，用户 B 的请求返回了 SMS 数据源的数据。

**原因分析**：`DataSourceHolder.setDataSourceType("SMS")` 后未在 `finally` 块中调用 `clearDataSourceType()`，线程池复用时 ThreadLocal 残留。

**解决方案**：

```java
DataSourceHolder.setDataSourceType("SMS");
try {
    // SMS 数据库操作
    return smsDao.queryData();
} finally {
    // 必须清除数据源标识
    DataSourceHolder.clearDataSourceType();
}
```

**验证步骤**：并发测试，确认不同用户的数据源隔离正确。

**预防措施**：代码审查所有 `setDataSourceType` 调用，确保都有对应的 `clearDataSourceType`。

### 案例3：Shiro 权限缓存不更新

**问题描述**：管理员给用户添加新角色后，用户重新登录仍无新权限。

**原因分析**：Shiro 权限缓存（EhCache，10 分钟过期）未及时刷新。

**解决方案**：

```java
// 修改用户权限后，主动清除 Shiro 权限缓存
Cache<Object, AuthorizationInfo> cache = cacheManager
    .getCache("org.apache.shiro.realm.SimpleAccountRealm.authorization");
cache.clear();
```

**验证步骤**：修改权限后立即清除缓存，用户重新登录确认权限生效。

**预防措施**：在用户权限变更的 Service 方法中，自动调用缓存清除逻辑。

### 案例4：Quartz 定时任务不执行

**问题描述**：新增的定时任务未按 Cron 表达式执行。

**原因分析**：PMS-struts 的 `beans-quartz.xml` 中，新任务的 Trigger 未添加到 `SchedulerFactoryBean` 的 `triggers` 列表。

**解决方案**：

```xml
<bean class="org.springframework.scheduling.quartz.SchedulerFactoryBean">
    <property name="triggers">
        <list>
            <ref bean="ReportTaskTrigger" />
            <!-- 添加新任务的 Trigger -->
            <ref bean="NewTaskTrigger" />
        </list>
    </property>
</bean>
```

**验证步骤**：重启应用，查看日志确认任务按预期执行。

**预防措施**：新增定时任务时，检查 Trigger 是否已注册到 SchedulerFactoryBean。

### 案例5：Activiti 流程任务分配异常

**问题描述**：流程流转后任务未分配给预期用户，审批人看不到待办任务。

**原因分析**：
1. 任务监听器（TaskListener）中 assignee 设置逻辑错误
2. 候选人/候选组配置与实际用户不匹配
3. `dp_act_unify_task` 推送失败

**解决方案**：

1. 查询任务实际分配人：

```sql
SELECT * FROM ACT_RU_TASK WHERE PROC_INST_ID_ = 'xxx';
SELECT * FROM ACT_RU_IDENTITYLINK WHERE TASK_ID_ = 'xxx';
```

2. 检查统一任务表：

```sql
SELECT * FROM dp_act_unify_task WHERE instId = 'xxx';
```

3. 手动重新分配任务或重置流程节点

**验证步骤**：确认审批人待办列表中出现该任务。

**预防措施**：流程定义中使用动态候选人（基于角色而非固定用户），任务分配后验证 assignee。

### 案例6：D365 接口调用失败

**问题描述**：转包流程调用 D365 API 生成采购订单失败，流程卡住。

**原因分析**：D365 API 认证 Token 过期。

**解决方案**：
1. 检查 D365 API 日志，确认返回的错误码
2. 验证认证 Token 是否有效
3. 手动重试接口调用

**验证步骤**：流程节点成功流转，采购订单生成。

**预防措施**：增加 D365 API 调用的重试机制（最多 3 次，间隔递增）和详细日志记录。

### 案例7：iBatis 缓存数据不一致

**问题描述**：修改数据后查询结果未更新，仍然返回旧数据。

**原因分析**：iBATIS 的 `CopyLRU` 缓存未及时刷新，`flushOnExecute` 未覆盖所有修改该数据的 SQL。

**解决方案**：
1. 检查 `cacheModel` 的 `flushOnExecute` 配置是否完整
2. 手动触发缓存刷新：执行 `refreshCacheData` SQL

```sql
UPDATE fnd_sys_arg SET var = NOW() WHERE code = 'sys.cache.latest.refreshTime';
```

3. 临时方案：重启 Tomcat 清空所有缓存

**验证步骤**：修改数据后查询，确认返回最新数据。

**预防措施**：新增写操作 SQL 时同步更新 `flushOnExecute` 配置；实时性要求高的查询不使用缓存。

### 案例8：SPMS 配置文件修改不生效

**问题描述**：修改 SPMS 的 Struts2 或 Spring 配置后，重启应用修改未生效。

**原因分析**：SPMS 配置文件存在双份（`config-ibaits/`、`config-spring/` 与 `build/classes/`），仅修改了一份。

**解决方案**：同步修改两处配置文件，或清理 `build/classes/` 目录后重新编译。

**验证步骤**：重启应用，确认配置生效。

**预防措施**：建立配置文件同步检查机制，或使用构建脚本自动同步。

### 案例9：数据库连接池耗尽

**问题描述**：应用日志出现 `Cannot get a connection, pool exhausted`，系统响应缓慢。

**原因分析**：慢查询占用连接时间过长，或代码中存在连接泄漏。

**解决方案**：

1. 查看当前数据库连接数：

```sql
SHOW PROCESSLIST;
SELECT * FROM information_schema.processlist WHERE TIME > 60 ORDER BY TIME DESC;
```

2. 检查连接池配置（`maxActive=300`）
3. 开启 `logAbandoned=true` 定位连接泄漏代码
4. 临时增加 `maxActive` 缓解问题

**验证步骤**：连接池使用率恢复正常，系统响应正常。

**预防措施**：定期优化慢查询；代码审查确保所有连接获取都在 `finally` 中关闭；监控连接池使用率，超过 80% 时告警。

### 案例10：PMS-springboot 模块构建失败

**问题描述**：执行 `mvn clean package` 构建失败，提示找不到 `PMS-springboot` 模块。

**原因分析**：根 `pom.xml` 列出了 `PMS-springboot` 模块，但磁盘上无此目录。

**解决方案**：

```bash
# 方案一：指定构建模块
mvn clean package -pl core,PMS-struts,PMS-springmvc,PMS-activiti,PMS-ext-d365,PMS-security,pms-rules,pms-ext-fp -am

# 方案二：从 pom.xml 移除 PMS-springboot 模块声明
```

**验证步骤**：构建成功，生成各模块 WAR/JAR 包。

**预防措施**：构建前检查 `pom.xml` 模块列表与磁盘目录一致性。

---

## 4. 避坑指南与最佳实践

### 4.1 避坑指南

| 陷阱 | 影响 | 解决方案 |
|------|------|----------|
| Struts2 版本不一致 | API 不兼容 | 确认模块版本，不混用 API |
| PMS-springboot 模块不存在 | 构建失败 | 从 pom.xml 移除或用 `-pl` 指定模块 |
| SPMS 非 Maven 项目 | 无法 mvn 构建 | 使用 Eclipse Export WAR 或 Ant |
| iBATIS/MyBatis 混用 | SQL 语法错误 | 确认 ORM 框架，使用对应语法 |
| system 作用域依赖 | CI/CD 问题 | 安装到 Maven 仓库，改 compile 作用域 |
| ThreadLocal 未清除 | 数据源串扰 | `finally` 块中清除 |
| Shiro 缓存不更新 | 权限不生效 | 主动清除缓存 |
| Quartz 任务未注册 | 任务不执行 | 检查 triggers 列表 |
| iBatis 缓存脏数据 | 查询旧数据 | 补全 flushOnExecute |
| SPMS 配置双份 | 修改不生效 | 同步两处配置 |
| 连接池耗尽 | 系统不可用 | 优化慢查询，监控使用率 |
| DMI 安全风险 | 任意方法调用 | 限制方法白名单 |
| SPMS AuthCheckInterceptor 失效 | 权限缺失 | 恢复权限校验 |
| 跨数据源无事务 | 数据不一致 | 使用补偿机制 |

### 4.2 最佳实践

1. **构建管理**：构建前检查 pom.xml 模块列表；使用 `-pl` 精确控制构建范围；SPMS 使用 Eclipse/Ant 构建
2. **多数据源**：ThreadLocal 必须在 `finally` 中清除；跨数据源操作使用补偿机制；外部数据源引入连接池
3. **缓存管理**：修改数据后主动清除缓存；`flushOnExecute` 配置完整；实时性要求高的查询不缓存
4. **定时任务**：新任务 Trigger 注册到 SchedulerFactoryBean；`concurrent=false` 防止并发；检查 Cron 表达式
5. **工作流**：流程定义使用动态候选人；任务分配后验证 assignee；`dp_act_unify_task` 推送失败重试
6. **接口调用**：增加重试机制和详细日志；检查 Token 有效性；网络超时配置
7. **连接池**：监控使用率；优化慢查询；`logAbandoned=true` 定位泄漏
8. **配置管理**：SPMS 配置双份同步；PMS 使用 Maven Profile 管理多环境；不提交真实凭据
9. **安全加固**：恢复 SPMS AuthCheckInterceptor；限制 DMI 方法白名单；Struts2 升级至 2.5.x
10. **日志分析**：关注 `Cannot get a connection`、`Deadlock`、`OgnlException` 等错误模式；定期审计 `fnd_operate_log` 和 `fnd_data_refresh_log`
