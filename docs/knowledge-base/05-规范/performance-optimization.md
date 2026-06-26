# 性能优化文档

> 本文档基于 PMS 与 SPMS 项目实际代码与配置，提炼缓存机制、并发控制、SQL 优化及代码复用策略，所有内容均来自真实实现，并标注避坑指南与最佳实践。

---

## 目录

1. [缓存机制](#1-缓存机制)
2. [并发控制](#2-并发控制)
3. [SQL 优化](#3-sql-优化)
4. [代码复用策略](#4-代码复用策略)
5. [避坑指南与最佳实践](#5-避坑指南与最佳实践)

---

## 1. 缓存机制

### 1.1 EhCache 配置与使用（PMS-springmvc / core）

PMS-springmvc 使用 EhCache 作为 Shiro 的缓存管理器，配置于 `spring-shiro-cas.xml`：

```xml
<bean id="cacheManager" class="org.apache.shiro.cache.ehcache.EhCacheManager">
    <property name="cacheManagerConfigFile" value="classpath:ehcache.xml"/>
</bean>
```

**EhCache 配置文件**（`ehcache.xml`）关键缓存：

| 缓存名 | 用途 | maxEntriesLocalHeap | eternal | timeToLiveSeconds | overflowToDisk |
|--------|------|---------------------|---------|-------------------|----------------|
| `shiro-activeSessionCache` | Shiro 活跃会话 | 10000 | true | - | true |
| `org.apache.shiro.realm.SimpleAccountRealm.authorization` | Shiro 权限缓存 | 10000 | false | 600（10 分钟） | false |
| `defaultCache` | 默认缓存 | 10000 | false | 120（2 分钟） | false |

**会话缓存特性**：
- `shiro-activeSessionCache` 设置 `eternal="true"`，由 Shiro 显式管理会话过期，避免 EhCache 提前淘汰
- `diskPersistent="true"`，支持 JVM 重启后恢复会话

> **避坑指南**：不要为 `shiro-activeSessionCache` 设置 `timeToIdle` 或 `timeToLive`，否则 EhCache 会在 Shiro 不知情的情况下淘汰会话，导致 "会话超时" 异常。

### 1.2 Shiro 权限缓存

Shiro 的权限缓存通过 `EhCacheManager` 自动管理，缓存于 `org.apache.shiro.realm.SimpleAccountRealm.authorization` 缓存区：

- **缓存内容**：用户的角色（`SimpleAuthorizationInfo.getRoles()`）和权限（`SimpleAuthorizationInfo.getStringPermissions()`）
- **缓存时长**：10 分钟（`timeToLiveSeconds=600`）
- **缓存淘汰**：LRU 策略，最大 10000 条

**权限缓存刷新问题**：

当管理员修改用户角色或权限后，Shiro 缓存不会自动更新，需手动清除：

```java
// 清除指定用户的权限缓存
Subject subject = SecurityUtils.getSubject();
subject.getSession().removeAttribute("org.apache.shiro.subject.support.DefaultSubjectContext_AUTHORIZATION_CACHE");

// 或清除所有缓存
Cache<Object, AuthorizationInfo> cache = cacheManager.getCache("org.apache.shiro.realm.SimpleAccountRealm.authorization");
cache.clear();
```

> **避坑指南**：修改用户权限后若发现权限未生效，首先排查 Shiro 权限缓存。临时方案是等待 10 分钟缓存过期，或重启应用。

### 1.3 SystemConfig 系统变量缓存

PMS-springmvc / core 通过 `SystemConfig` 类缓存系统变量，避免每次读取都查询数据库：

**实现类**：`com.dp.plat.core.config.SystemConfig`

```java
@Order(1)
@Configuration
public class SystemConfig {
    public static HashMap<String, String> systemVariables;

    @Autowired
    private ISystemVariableService systemVariableService;

    @Bean(name = "systemVariables")
    public HashMap<String, String> getSystemVariables() {
        systemVariables = systemVariableService.querySystemVariables();
        return systemVariables;
    }
}
```

**缓存加载**：
- 应用启动时，`@Configuration @Order(1)` 确保最先执行
- 从 `t_sys_variable` 表查询所有有效变量（`effectiveFrom < now() AND now() < IFNULL(effectiveTo, ...)`）
- 存入静态 `HashMap<String, String> systemVariables`

**缓存自动刷新**：

通过 AOP 切面 `SystemCoreFunctionAspect` 监听系统变量的增删改操作，自动刷新缓存：

```java
@After("(target(ISystemVariableService) && (execution(* insert*(..)) || execution(* update*(..)) || execution(* delete*(..))))")
public void updateSystemVariables(JoinPoint point) {
    SystemConfig.systemVariables = systemVariableService.querySystemVariables();
}
```

**使用方式**：

```java
// 读取系统变量
String envArg = SystemConfig.systemVariables.get("sys.envirment.argu");
String checkCaptcha = SystemConfig.systemVariables.getOrDefault("sys.login.check.captcha", "1");
String casMode = SystemConfig.systemVariables.get("sys.cas");
```

**关键系统变量**：

| Key | 含义 | 取值 |
|-----|------|------|
| `sys.envirment.argu` | 环境参数 | "0":开发, "1"/"2":生产 |
| `sys.login.check.captcha` | 验证码开关 | "0":关闭, "1":开启 |
| `sys.adAuth` | AD 域认证 | "0":关闭, "1":开启 |
| `sys.cas` | CAS 单点登录 | "0":关闭, "1":开启 |
| `sys.fp.api.config` | FP API 配置 | JSON 字符串 |

> **避坑指南**：`SystemConfig.systemVariables` 是静态 `HashMap`，非线程安全的 `ConcurrentHashMap`。多线程并发读通常安全，但 AOP 刷新时存在短暂的引用替换，高并发场景建议使用局部变量快照。

### 1.4 iBatis CopyLRU 缓存（PMS-struts / SPMS）

PMS-struts 和 SPMS 使用 iBATIS 自定义的 `CopyLRU` 缓存控制器，继承 iBATIS 的 `LruCacheController`，增加深拷贝功能：

```xml
<typeAlias type="com.dp.plat.ibatis.cache.LRUCacheController" alias="CopyLRU" />
```

**CopyLRU 特性**：
- `getObject`：从缓存取值时进行深拷贝，避免外部修改影响缓存
- `putObject`：存入缓存时进行深拷贝，避免原始对象修改影响缓存
- 深拷贝策略：优先使用 `ObjectUtils.clone()`，失败时使用 Fastjson 序列化/反序列化

**缓存配置示例**：

```xml
<cacheModel id="basicDataCache" type="CopyLRU" readOnly="true">
    <flushInterval hours="1" />
    <property name="size" value="128" />
    <flushOnExecute statement="insertBasicData" />
    <flushOnExecute statement="updateBasicData" />
    <flushOnExecute statement="deleteBasicData" />
</cacheModel>
```

### 1.5 ShipmentCache 发运缓存（SPMS）

SPMS 发运管理模块使用 iBATIS `cacheModel` 缓存发货查询结果：

**配置文件**：`sql-map-shipment-config.xml`

```xml
<cacheModel type="CopyLRU" id="ShipmentCache" readOnly="true">
    <flushInterval minutes="5"/>  <!-- 5分钟自动刷新 -->
    <flushOnExecute statement="insertContract"/>
    <flushOnExecute statement="insertShipment"/>
    <flushOnExecute statement="insertShipmentBarcode"/>
    <flushOnExecute statement="updateContractByContractId"/>
    <flushOnExecute statement="updateShipmentByPacklistId"/>
    <flushOnExecute statement="deleteShipmentBarcodeByIds"/>
    <!-- ... 其他写操作 ... -->
    <property name="size" value="100"/>
</cacheModel>
```

**缓存策略**：

| 参数 | 值 | 说明 |
|------|----|------|
| `type` | CopyLRU | 深拷贝 LRU 缓存 |
| `readOnly` | true | 只读缓存，性能优先 |
| `flushInterval` | 5 分钟 | 定时刷新 |
| `size` | 100 | 缓存 100 个查询结果 |

> **避坑指南**：`ShipmentCache` 缓存 5 分钟，修改合同/发货/条码数据后会自动刷新缓存，但跨服务器实例时需注意缓存一致性。集群环境下各节点缓存独立，可能出现短暂不一致。

### 1.6 缓存策略对比

| 缓存类型 | 项目 | 技术 | 缓存内容 | 刷新机制 | 适用场景 |
|----------|------|------|----------|----------|----------|
| EhCache | PMS-springmvc | Shiro + EhCache | 会话、权限 | TTL 过期 | 认证授权 |
| SystemConfig | PMS-springmvc/core | 静态 HashMap | 系统变量 | AOP 自动刷新 | 全局配置 |
| CopyLRU | PMS-struts | iBATIS cacheModel | 查询结果 | 定时 + 写操作触发 | 基础数据 |
| ShipmentCache | SPMS | iBATIS cacheModel | 发运查询 | 5 分钟 + 写操作触发 | 发货查询 |
| Aviator LRU | pms-rules | 自定义 LRU | 编译表达式 | MD5 Key 命中 | 规则引擎 |

---

## 2. 并发控制

### 2.1 多数据源 ThreadLocal 使用注意事项

PMS-springmvc / core 通过 `RoutingDataSource`（继承 `AbstractRoutingDataSource`）实现多数据源动态路由，使用 `ThreadLocal` 保存当前数据源标识：

**核心类**：

| 类名 | 路径 | 职责 |
|------|------|------|
| `RoutingDataSource` | `com.dp.plat.core.config` | 动态选择数据源 |
| `DataSourceHolder` | `com.dp.plat.core.config` | ThreadLocal 持有数据源标识 |

**使用方式**：

```java
// 设置数据源
DataSourceHolder.setDataSourceType("SMS");
try {
    // 执行 SMS 数据库操作
} finally {
    // 必须清除数据源标识，防止线程池复用导致数据源串扰
    DataSourceHolder.clearDataSourceType();
}
```

**支持的数据源**：

| 标识 | 用途 | 数据库 |
|------|------|--------|
| `Local` | PMS 主数据库 | MySQL (dppms_d365) |
| `SMS` | SMS 系统 | MySQL (dpsms) |
| `SAP` | SAP 系统 | SQL Server (DIPULive) |
| `OFS` | OFS 系统 | - |
| `PMS` | PMS 旧系统 | - |
| `TMS` | TMS 系统 | - |

> **避坑指南（关键）**：`ThreadLocal` 必须在 `finally` 块中清除，否则线程池复用时会导致后续请求使用错误的数据源。这是多数据源最常见的故障来源。

**SPMS 多数据源**：

SPMS 通过 `BaseDao` 提供多个 `SqlMapClientTemplate`，不使用 ThreadLocal 路由：

```java
public class BaseDao {
    private SqlMapClientTemplate sqlMapClientTemplate;       // 主库 MySQL
    private SqlMapClientTemplate sqlMapClientTemplateSAP;    // SAP SQL Server
    private SqlMapClientTemplate sqlMapClientTemplateERP;    // D365 SQL Server
    private SqlMapClientTemplate sqlMapClientTemplateSSE;    // SSE
}
```

> **避坑指南**：SPMS 的外部数据源（SAP/ERP/SSE）操作不在 Spring 事务管理范围内，跨数据源操作不会回滚。跨数据源写入应使用补偿机制。

### 2.2 Quartz 任务并发控制（concurrent=false）

PMS-springmvc 的 Quartz 定时任务通过 `MethodInvokingJobDetailFactoryBean` 配置 `concurrent=false`，确保任务串行执行：

**配置文件**：`src/main/resources/quartz-job.xml`

```xml
<bean id="mailTask"
    class="org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean">
    <property name="targetObject"><ref bean="mailJob" /></property>
    <property name="targetMethod"><value>execute</value></property>
    <!-- 控制定时任务单线程执行，防止上次同步未执行完，本次又开始执行 -->
    <property name="concurrent" value="false" />
</bean>
```

**`concurrent=false` 的作用**：

- 防止上一次任务执行未完成时，下一次定时触发启动新任务实例
- 避免数据同步任务的并发执行导致数据重复或冲突
- 所有 PMS-springmvc 的 Quartz 任务均配置为 `concurrent=false`

**PMS-struts 的 Quartz 配置**：

PMS-struts 使用 `JobDetailFactoryBean`（非 `MethodInvokingJobDetailFactoryBean`），不设置 `concurrent` 属性，任务类需实现 Quartz 的 `Job` 接口：

```xml
<bean id="Mailer" class="org.springframework.scheduling.quartz.JobDetailFactoryBean">
    <property name="jobClass" value="com.dp.plat.job.Mailer" />
</bean>
```

> **避坑指南**：PMS-springmvc 和 PMS-struts 的 Quartz 配置方式不同。PMS-springmvc 使用 `MethodInvokingJobDetailFactoryBean`（调用普通 Bean 方法），PMS-struts 使用 `JobDetailFactoryBean`（任务类实现 Job 接口）。新增定时任务时需确认所属模块的配置方式。

### 2.3 ReentrantReadWriteLock（FP Token 缓存）

pms-ext-fp 模块的 `FPApi` 类使用 `ReentrantReadWriteLock` 实现 FP 平台 OAuth Token 的缓存与刷新，采用双重检查锁定模式：

**实现逻辑**：

```
getToken() 调用
    │
    ▼ 获取读锁
  Token 已缓存且未过期？ ── 是 ──▶ 返回缓存 Token（读锁释放）
    │ 否
    ▼ 释放读锁，获取写锁
  再次检查 Token（双重检查） ── 已被其他线程刷新 ──▶ 返回缓存 Token
    │ 仍未刷新
    ▼
  clearToken() 清除旧 Token
  调用 FP API 获取新 Token
  存入缓存
  释放写锁
  返回新 Token
```

**关键设计**：
- **读锁**：多个线程可同时读取缓存 Token，无阻塞
- **写锁**：仅一个线程刷新 Token，其他线程等待
- **双重检查**：获取写锁后再次检查缓存，避免多个线程重复刷新

**Token 过期判定**：支持三种过期字段：
- `expiresOn`：绝对过期时间
- `expiresIn`：相对过期秒数
- `expireTime`：过期时间

> **最佳实践**：`getToken()` 内部已实现完整的读写锁机制，业务层无需手动管理 Token。直接调用 `getToken()` 即可获取有效 Token。

### 2.4 synchronized 使用规范

PMS-struts 中部分操作使用 `synchronized` 保证线程安全：

```java
// ProjectServiceImpl.java - 项目组编码生成
public synchronized String queryProjectGroupCode() {
    String maxCode = projectDao.queryMaxProjectGroupCode();
    // 生成新编码...
    return newCode;
}
```

**synchronized 使用规则**：

| 规则 | 说明 |
|------|------|
| 锁对象明确 | 优先锁定具体对象，而非整个方法 |
| 锁粒度最小化 | 仅锁定必要的临界区代码 |
| 避免嵌套锁 | 嵌套 synchronized 容易死锁 |
| 超时保护 | 长时间持锁操作需设置超时 |

> **避坑指南**：`synchronized` 仅在单个 JVM 内有效。PMS 如果部署为集群（多台 Tomcat），`synchronized` 无法保证跨 JVM 互斥。集群环境应使用数据库唯一索引、分布式锁（Redis）或编码预留方案。

### 2.5 乐观锁策略

PMS 系统通过业务逻辑实现类似乐观锁的效果（无 version 字段）：

```sql
-- 项目状态更新：WHERE 条件包含当前状态值
UPDATE pm_project_state SET state=#newState#
WHERE projectId=#projectId# AND state=#oldState#

-- 成员变更：WHERE 条件包含 effectiveTo IS NULL
UPDATE pm_project_member SET effectiveTo=NOW()
WHERE projectId=#projectId# AND memberRole=#memberRole# AND effectiveTo IS NULL
```

---

## 3. SQL 优化

### 3.1 索引优化建议

参考 `PMS-struts/docs/03-database/index-analysis.md` 的索引分析，关键优化建议：

**effectiveTo 软删除索引**：

```sql
-- 所有含 effectiveTo 的表建议建立复合索引
ALTER TABLE pm_project_header ADD INDEX idx_effective_state (effectiveTo, projectState);
ALTER TABLE fnd_user_info ADD INDEX idx_user_effective (effectiveFrom, effectiveTo);
```

**统一使用 `effectiveTo IS NULL`**（索引友好）：

```sql
-- 推荐（索引利用率高）
SELECT * FROM pm_project_header WHERE effectiveTo IS NULL;

-- 不推荐（索引利用率低）
SELECT * FROM pm_project_header WHERE effectiveTo > NOW() OR effectiveTo IS NULL;
```

### 3.2 MyBatis / iBATIS SQL 映射优化

#### 3.2.1 参数化查询（#）vs 字符串拼接（$）

| 符号 | iBATIS | MyBatis | 安全性 | 使用场景 |
|------|--------|---------|--------|----------|
| 参数化 | `#var#` | `#{var}` | **安全**（PreparedStatement） | 所有用户输入值 |
| 拼接 | `$var$` | `${var}` | **不安全**（SQL 注入） | 表名、列名、ORDER BY |

**安全用法**：

```xml
<!-- iBATIS -->
<select id="findProjectList" parameterClass="map" resultClass="hashmap">
    SELECT * FROM pm_project_header
    WHERE projectCode LIKE concat('%', #projectCode#, '%')
    AND effectiveTo IS NULL
</select>

<!-- MyBatis -->
<select id="querySystemVariables" resultType="java.util.HashMap">
    SELECT code, var FROM t_sys_variable
    WHERE effectiveFrom < now() AND now() &lt; IFNULL(effectiveTo, '9999-12-31 23:59:59')
</select>
```

**$ 的必要场景（必须白名单校验）**：

```xml
<!-- 动态排序 -->
<isNotEmpty prepend="ORDER BY" property="sortField">
    $sortField$ $sortOrder$
</isNotEmpty>
```

> **避坑指南**：使用 `$` 时必须确保参数来源可信（系统内部枚举值），绝不能直接拼接用户输入。Java 代码中应对动态表名、列名进行白名单校验。

#### 3.2.2 动态 SQL 优化

**iBATIS 动态标签**：

```xml
<dynamic prepend="WHERE">
    <isNotEmpty prepend="AND" property="projectCode">
        projectCode LIKE concat('%', #projectCode#, '%')
    </isNotEmpty>
    <isNotEmpty prepend="AND" property="officeCode">
        officeCode = #officeCode#
    </isNotEmpty>
</dynamic>
```

**MyBatis 动态标签**：

```xml
<where>
    <if test="code != null">
        AND code = #{code,jdbcType=VARCHAR}
    </if>
    <if test="var != null">
        AND var = #{var,jdbcType=VARCHAR}
    </if>
</where>
```

### 3.3 分页查询优化

**问题**：深分页（如 `LIMIT 10000, 15`）查询缓慢。

**方案一：游标分页（推荐）**

```sql
SELECT * FROM pm_project_header
WHERE effectiveTo IS NULL AND projectId > #lastId#
ORDER BY projectId ASC LIMIT 15;
```

**方案二：延迟关联**

```sql
SELECT h.* FROM pm_project_header h
INNER JOIN (
    SELECT projectId FROM pm_project_header
    WHERE effectiveTo IS NULL
    ORDER BY createTime DESC
    LIMIT 10000, 15
) t ON h.projectId = t.projectId;
```

**方案三：外部分页（DisplayTag）**

PMS-springmvc 的 `AbstractController` 支持外部分页，由 DAO 层完成分页查询：

```java
@RequestMapping("/list")
public String list(PageParam pageParam, T v, Model model) {
    // DAO 层分页查询，仅返回当前页数据
    Result result = service.findList(pageParam, v);
    model.mergeAttributes(result.getMap());
    return getRealViewNameSpace() + "list";
}
```

### 3.4 大表查询优化

#### 3.4.1 fb_shipment_barcode（384 万行）

SPMS 的 `fb_shipment_barcode` 表数据量达 384 万行，查询优化策略：

1. **索引优化**：为常用查询条件（`barcode`、`pack_id`）建立索引
2. **分批查询**：大数据量同步采用分批处理
3. **缓存利用**：使用 `ShipmentCache`（5 分钟刷新）缓存查询结果
4. **SQL Server 语法**：使用 `TOP 1`、`OFFSET FETCH` 等 SQL Server 分页语法

```xml
<!-- SPMS 发运条码查询（SQL Server） -->
<select id="findShipmentBarcode" parameterClass="map" resultClass="hashmap" cacheModel="ShipmentCache">
    SELECT TOP 1 * FROM SHIPMENT_BARCODE
    WHERE BARCODE = #barCode#
</select>
```

#### 3.4.2 项目列表查询（10+ 表 JOIN）

**问题**：项目列表查询涉及 `pm_project_header`、`pm_project_group`、`pm_project_contract`、`pm_project_member`、`fnd_basic_data` 等 10+ 张表 LEFT JOIN。

**优化方案**：

1. **分步查询替代大 JOIN**：先查主表获取 ID 列表，再批量查关联数据
2. **冗余字段减少 JOIN**：在 `pm_project_header` 冗余存储 `officeName`、`projectStateName`
3. **覆盖索引 + 延迟关联**：先通过覆盖索引获取 ID，再查完整数据

#### 3.4.3 FIND_IN_SET 替代方案

**问题**：`FIND_IN_SET(wcs.officeCode, #areaPower#)` 无法使用索引，导致全表扫描。

**优化方案**：拆分为关联表或使用 IN 查询：

```xml
<!-- 优化前 -->
<isNotEmpty prepend="OR" property="areaPower">
    FIND_IN_SET(wcs.officeCode, #areaPower#)
</isNotEmpty>

<!-- 优化后：使用 iterate 构建 IN 条件 -->
<iterate property="areaPowerList" open="AND officeCode IN (" close=")" conjunction=",">
    #areaPowerList[]#
</iterate>
```

---

## 4. 代码复用策略

### 4.1 core 模块共享工具类

core 模块作为依赖图的根，为所有 Web 模块提供共享框架和工具类：

| 工具类/组件 | 路径 | 复用能力 |
|-------------|------|----------|
| `SystemConfig` | `com.dp.plat.core.config` | 系统变量缓存 |
| `RoutingDataSource` | `com.dp.plat.core.config` | 多数据源路由 |
| `UserContext` | `com.dp.plat.core.context` | 当前登录用户上下文 |
| `HttpContext` | `com.dp.plat.core.context` | HTTP 请求上下文 |
| `ShiroRealm` | `com.dp.plat.core.realms` | Shiro 认证授权 |
| `CasRealm` | `com.dp.plat.core.realms` | CAS 单点登录 |
| `FilterChainDefinitionMapBuilder` | `com.dp.plat.core.factory` | 动态权限过滤链 |
| `IAbstractBaseService` | `com.dp.plat.core.service` | Service 基类接口 |

### 4.2 AbstractController / AbstractAction 基类复用

#### 4.2.1 AbstractController（PMS-springmvc）

`AbstractController<Service extends IAbstractBaseService<T>, T, V>` 提供泛型 CRUD 模板：

| 复用能力 | 方法 | 说明 |
|----------|------|------|
| 首页 | `home(Model)` | 默认首页视图 |
| 列表查询 | `list(PageParam, T, Model)` | 分页列表 |
| 详情查询 | `findOne(Integer, Model)` / `detail(V, Model)` | 单条详情 |
| 保存 | `save(T, Model)` | 新增/更新 |
| 删除 | `delete(Integer, Model)` | 删除 |
| 导入预览 | `importPreview(V, String, Model)` | Excel 导入预览 |
| 导入提交 | `importSubmit(V, String, Model)` | Excel 导入提交 |
| 权限检查 | `checkPermission(V, Model, String)` | 统一权限校验 |

**使用示例**：

```java
@Controller
@RequestMapping(ProjectConstant.URLPath.PROJECT_MANAGER + "member")
public class ProjectMemberController extends AbstractController<IProjectMemberService, ProjectMember, MemberVO> {

    @PostConstruct
    public void init() {
        this.setViewModel("projectMember");
        this.setUseTemplate(true);
    }
}
```

#### 4.2.2 BaseAction（PMS-struts）

`BaseAction` 提供 Struts2 Action 通用模板：

| 复用能力 | 方法 | 说明 |
|----------|------|------|
| 错误信息收集 | `setErrmsg(BaseService)` | 从 Service 收集错误/警告 |
| 页面初始化 | `start()` | 默认返回 INPUT |
| Servlet API | `getServletRequest()` / `getServletResponse()` | 自动注入 |
| 文本资源 | `getText(String)` | Struts2 国际化 |

#### 4.2.3 BaseServiceImpl（PMS-struts / SPMS）

| 复用能力 | 方法 | 说明 |
|----------|------|------|
| 错误信息传递 | `addErrmsg()` / `getErrmsg()` | Service → Action 错误通道 |
| 警告信息传递 | `addWarnmsg()` / `getWarnmsg()` | Service → Action 警告通道 |
| 操作日志 | `log(String action)` | AOP 自动记录到 `fnd_operate_log` |
| 用户上下文 | `getUserContext()` / `getLoginName()` | 当前登录用户 |

#### 4.2.4 BaseDao（PMS-struts / SPMS）

| 复用能力 | 方法/属性 | 说明 |
|----------|-----------|------|
| 主库操作 | `getSqlMapClientTemplate()` | 默认数据源 |
| SAP 数据源 | `getSqlMapClientTemplateSAP()` | SAP RFC |
| ERP 数据源 | `getSqlMapClientTemplateERP()` | D365 |
| SSE 数据源 | `getSqlMapClientTemplateSSE()` | SSE |
| 当前用户 | `getCurrUsername()` | 当前操作用户名 |

### 4.3 Service 代理模式（SPMS ServiceAgent）

SPMS 和 PMS-struts 采用 `*Service` + `*ServiceAgent` 事务代理模式：

**配置示例**：

```xml
<!-- 真实 Service Bean -->
<bean id="projectService" class="com.dp.plat.service.ProjectServiceImpl"
    lazy-init="false" parent="baseServce">
    <property name="projectDao" ref="projectDao" />
</bean>

<!-- 事务代理 Bean -->
<bean id="projectServiceAgent" parent="transactionBaseService">
    <property name="target" ref="projectService" />
</bean>
```

**事务代理配置**（`transactionBaseService`）：

```xml
<bean id="transactionBaseService" abstract="true"
    class="org.springframework.transaction.interceptor.TransactionProxyFactoryBean">
    <property name="transactionManager" ref="transactionManager" />
    <property name="transactionAttributes">
        <props>
            <prop key="insert*">PROPAGATION_REQUIRED</prop>
            <prop key="update*">PROPAGATION_REQUIRED</prop>
            <prop key="delete*">PROPAGATION_REQUIRED</prop>
            <prop key="save*">PROPAGATION_REQUIRED</prop>
            <prop key="add*">PROPAGATION_REQUIRED</prop>
            <prop key="do*">PROPAGATION_REQUIRED</prop>
            <prop key="start*">PROPAGATION_REQUIRED</prop>
            <prop key="submit*">PROPAGATION_REQUIRED</prop>
            <prop key="parse*">PROPAGATION_REQUIRED</prop>
            <prop key="keep*">PROPAGATION_REQUIRED</prop>
        </props>
    </property>
</bean>
```

> **关键规则**：Action 层必须注入 `*ServiceAgent`（事务代理），而非 `*Service`。直接注入 `*Service` 会导致事务不生效。

### 4.4 自定义标签复用（PMS-struts）

| 标签 | 复用场景 | 说明 |
|------|----------|------|
| `<dp:base />` | 所有 JSP 页面 | 输出 `<base>` 标签，统一资源路径 |
| `<dp:leftmenu>` | 所有业务页面 | 自动渲染左侧菜单 + 权限校验 |
| `<dp:permission>` | 权限控制按钮/区域 | 页面级权限控制 |
| `<dp:errormsg>` | 表单提交页面 | 统一错误信息展示 |
| `<dp:barpercent>` | 进度/完成率 | 百分比进度条 |
| `<dp:pagesize>` | DisplayTag 列表 | 分页大小选择器 |
| `<dp:script>` | 引入 JS | 支持 SRI 完整性校验和 CSP nonce |
| `<dp:link>` | 引入 CSS | 支持 SRI 完整性校验和 CSP nonce |

### 4.5 FPApi 批量推送复用（pms-ext-fp）

pms-ext-fp 的 `FPApi` 提供可复用的批量推送能力：

| 推送模式 | 常量 | 行为 | 适用场景 |
|----------|------|------|----------|
| 调度推送 | `MINUTE` | `ScheduledExecutorService` 按分钟延迟串行 | 限流场景 |
| 并发推送 | `MULTIPLE` | 10 线程并发提交，按序获取结果 | 大批量推送 |
| 串行推送 | `SINGLE` | 单条串行发送 | 小批量或调试 |

**连接池复用**：

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `httpClient.maxTotal` | 100 | 连接池最大连接数 |
| `httpClient.maxPerRoute` | 20 | 每路由最大连接数 |
| `httpClient.connectTimeout` | 10000 | 连接超时（毫秒） |
| `httpClient.readTimeout` | 60000 | 读取超时（毫秒） |
| `httpClient.keepAliveMinutes` | 5 | 连接保活时间（分钟） |

> **最佳实践**：`FPApi` 实现 `DisposableBean`，Spring 容器关闭时自动回收 `scheduler`、`fixedExecutor`、`HttpClientPool`、`OkHttpPool`，无需手动释放资源。

---

## 5. 避坑指南与最佳实践

### 5.1 避坑指南

| 陷阱 | 影响 | 解决方案 |
|------|------|----------|
| ThreadLocal 未清除 | 数据源串扰 | `finally` 块中调用 `clearDataSourceType()` |
| Shiro 权限缓存不更新 | 权限修改不生效 | 手动清除缓存或等待 10 分钟过期 |
| synchronized 集群失效 | 编码重复生成 | 集群环境使用数据库唯一索引或分布式锁 |
| iBATIS 缓存脏数据 | 查询返回旧数据 | 补全 `flushOnExecute` 配置，使用 `CopyLRU` |
| FIND_IN_SET 全表扫描 | 查询缓慢 | 拆分为关联表或使用 IN 查询 |
| 大 JOIN 查询 | 列表加载慢 | 分步查询或冗余字段 |
| 外部数据源无连接池 | 连接创建开销大 | 引入 DBCP 连接池替代 `DriverManagerDataSource` |
| DisplayTag 内存分页 | OOM 风险 | 使用外部分页 `partialList="true"` |
| SystemConfig 非线程安全 | 高并发读取异常 | 使用局部变量快照 |
| FPApi 全局静态状态 | 多租户配置覆盖 | 使用 `initConfig(Function, key)` 模式 |

### 5.2 最佳实践

1. **缓存使用**：基础数据（变更频率低）使用 CopyLRU 缓存；实时性要求高的数据不缓存；集群环境注意缓存一致性
2. **并发控制**：ThreadLocal 必须在 `finally` 中清除；编码生成使用 `synchronized` 或数据库锁；Token 刷新使用读写锁双重检查
3. **SQL 优化**：统一使用 `effectiveTo IS NULL`；避免 `FIND_IN_SET`；深分页使用游标或延迟关联；大表查询使用分批处理
4. **代码复用**：Controller 继承 `AbstractController`；Action 继承 `BaseAction`；Service 使用 `ServiceAgent` 事务代理；DAO 继承 `BaseDao` 复用多数据源模板
5. **定时任务**：配置 `concurrent=false` 防止并发执行；大数据量同步分批处理；失败重试机制
6. **连接池**：生产环境 `removeAbandoned=false`；监控连接池使用率；外部数据源引入连接池
