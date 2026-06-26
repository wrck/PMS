# core 模块 — 性能优化技巧

> 本文档汇总 core 模块及上层模块复用 core 时的性能优化策略，包括缓存策略、连接池调优、MyBatis 优化、多数据源性能、并发控制、JVM 调优与监控指标。
> core 作为框架模块，其性能直接影响所有上层业务模块。

---

## 1. 缓存策略优化

### 1.1 EhCache 缓存层级

core 使用 EhCache 作为本地缓存，主要服务于 Shiro 会话与授权信息：

| 缓存名 | 用途 | TTL | 容量 | 失效策略 |
|--------|------|-----|------|----------|
| `shiro-activeSessionCache` | 活跃会话 | 30min | 10000 | LRU + 时间过期 |
| `shiro-authorizationCache` | 授权信息 | 10min | 10000 | LRU + 时间过期 |
| `shiro-authenticationCache` | 认证信息 | 10min | 10000 | LRU + 时间过期 |

**配置文件**：`core/src/main/resources/ehcache.xml`

```xml
<cache name="shiro-authorizationCache"
       maxEntriesLocalHeap="10000"
       timeToLiveSeconds="600"
       memoryStoreEvictionPolicy="LRU"/>
```

### 1.2 授权缓存优化

**问题**：默认授权缓存 TTL=10min，权限变更后最多延迟 10 分钟生效，且无主动失效机制。

**优化方案**：

```java
// 方案一：角色/权限变更时主动清除对应用户的授权缓存
@Service
public class RoleServiceImpl implements IRoleService {
    @Autowired
    private CacheManager cacheManager;

    public void updateRolePermissions(Integer roleId, List<Integer> permissionIds) {
        roleMapper.updateRolePermissions(roleId, permissionIds);
        // 主动清除该角色下所有用户的授权缓存
        Cache authzCache = cacheManager.getCache("shiro-authorizationCache");
        for (String username : getUsernamesByRoleId(roleId)) {
            authzCache.remove(username);
        }
    }
}
```

```java
// 方案二：用户重新登录时清除旧授权缓存（避免脏数据）
public Result login(UsernamePasswordCaptchaToken token) {
    Subject subject = SecurityUtils.getSubject();
    // 登录前清除可能的旧授权缓存
    Cache authzCache = cacheManager.getCache("shiro-authorizationCache");
    authzCache.remove(token.getUsername());
    subject.login(token);
    return Result.success();
}
```

### 1.3 SystemConfig 启动加载缓存

**机制**：`SystemConfig` 在应用启动时一次性加载 `t_sys_variable` 全部参数到内存 Map，运行时直接读内存，无 DB 查询。

```java
// 启动加载
@Component
public class SystemConfig {
    private static Map<String, String> sysVariableMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        List<SysVariable> list = sysVariableMapper.selectAll();
        for (SysVariable v : list) {
            sysVariableMap.put(v.getVariableKey(), v.getVariableValue());
        }
    }

    public static String get(String key) {
        return sysVariableMap.get(key);  // 内存读取，无 DB 查询
    }
}
```

**优化建议**：
- 参数变更后需调用 `SystemConfig.refresh()` 重新加载，或提供管理界面触发刷新；
- 避免在循环中调用 `SystemConfig.get()`（虽是内存读取，但建议循环外缓存到局部变量）。

### 1.4 菜单数据缓存（待优化项）

**现状**：`LeftMenuTag` 每次页面加载都查询 `t_menu` + `t_role_menu` + `t_user_role` 三表 JOIN，无缓存。

**优化方案**：

```java
// 方案：Session 级缓存菜单数据
public class LeftMenuTag extends SimpleTagSupport {
    @Override
    public void doTag() {
        HttpSession session = ((HttpServletRequest) pageContext.getRequest()).getSession();
        List<Menu> cachedMenu = (List<Menu>) session.getAttribute("USER_MENU");
        if (cachedMenu == null) {
            cachedMenu = shiroService.queryUserMenuByUsername(getUsername());
            session.setAttribute("USER_MENU", cachedMenu);
        }
        // 渲染菜单...
    }
}
```

> **避坑**：Session 缓存菜单后，管理员调整菜单/角色映射需让用户重新登录或主动清 Session。

---

## 2. 数据库连接池调优

### 2.1 Druid 连接池配置

core 使用 Druid 1.2.8 作为连接池（区别于 PMS-struts 的 DBCP），配置在 `jdbc.properties`：

```properties
# 初始连接数
druid.initialSize=5
# 最大活跃连接数
druid.maxActive=20
# 最小空闲连接数
druid.minIdle=5
# 获取连接超时（ms）
druid.maxWait=60000
# 连接保活检测间隔
druid.timeBetweenEvictionRunsMillis=60000
# 连接最小空闲时间
druid.minEvictableIdleTimeMillis=300000
# 连接最大存活时间
druid.maxEvictableIdleTimeMillis=900000
# 校验 SQL（MySQL）
druid.validationQuery=SELECT 1
# 空闲时检测
druid.testWhileIdle=true
# 借出时不检测（性能优先）
druid.testOnBorrow=false
druid.testOnReturn=false
```

### 2.2 关键参数调优建议

| 参数 | 默认值 | 调优建议 | 说明 |
|------|--------|----------|------|
| `maxActive` | 20 | 生产 50-100 | 根据并发量调整，过高会压垮 DB |
| `minIdle` | 5 | 与 initialSize 一致 | 避免频繁创建连接 |
| `maxWait` | 60000ms | 30000ms | 超时缩短，快速失败 |
| `testWhileIdle` | true | true | 保活检测，避免连接断开 |
| `testOnBorrow` | false | false | 借出检测影响性能，依赖 testWhileIdle |
| `timeBetweenEvictionRunsMillis` | 60000ms | 30000ms | 缩短检测间隔 |

### 2.3 多数据源连接池规划

core 通过 `RoutingDataSource` 管理多个数据源，每个数据源独立配置连接池：

| 数据源 Key | 用途 | 建议 maxActive | 说明 |
|------------|------|----------------|------|
| `Local` | 主库（core 主数据源，dev=dppms_d365/release=dppms_d365） | 50 | 主要业务读写 |
| `SAP` | SAP 系统（只读） | 10 | 定时同步用，低并发 |
| `PMS` | PMS 数据库（dppms_d365，只读） | 10 | PMS-struts 历史主干数据 |
| `SMS` | SMS 系统（只读） | 5 | 每日同步一次 |
| `OFS` | OFS 系统（只读） | 5 | 每日同步一次 |
| `TMS` | TMS 系统（只读） | 5 | 每日同步一次 |

> **说明**：当前 `spring.xml` 中仅 `Local` 数据源启用，其余 Key（SMS/OFS/PMS/TMS/SAP）已在 `targetDataSources` 中注释。数据源 Key 名以 `jdbc.properties` 中的 `jdbc.key1`~`jdbc.key6` 实际取值为准。

> **避坑**：外部系统数据源（sap/d365/ehr/sms）连接数不宜过大，避免压垮源系统；建议为只读数据源配置单独的小连接池。

### 2.4 Druid 监控启用

```xml
<!-- spring.xml 启用 Druid 监控 -->
<bean id="druidStatInterceptor" class="com.alibaba.druid.support.spring.DruidStatInterceptor"/>
<bean id="druidStatViewServlet" class="com.alibaba.druid.support.http.StatViewServlet">
    <property name="loginUsername" value="admin"/>
    <property name="loginPassword" value="admin"/>
</bean>
```

监控页面：`http://host:port/druid/`，可查看：
- SQL 执行统计（慢查询、执行次数、平均耗时）
- 连接池状态（活跃、空闲、等待）
- URI 监控（请求耗时分布）

---

## 3. MyBatis 优化

### 3.1 resultMap 显式映射

core 表列存在驼峰与下划线混用，必须使用 `resultMap` 显式映射，避免自动映射失败：

```xml
<!-- 正确：显式 resultMap -->
<resultMap id="BaseResultMap" type="com.dp.plat.core.pojo.User">
    <id column="user_id" property="userId"/>
    <result column="user_name" property="userName"/>
    <result column="needChangePwd" property="needChangePwd"/>  <!-- 驼峰列 -->
    <result column="isSysUser" property="isSysUser"/>          <!-- 驼峰列 -->
</resultMap>

<!-- 错误：依赖自动映射，驼峰列会映射失败 -->
<select id="selectByPrimaryKey" resultType="User">
    SELECT * FROM t_user WHERE user_id = #{userId}
</select>
```

### 3.2 批量操作优化

**问题**：循环单条 INSERT 性能差。

**优化方案**：使用 MyBatis `foreach` 批量插入：

```xml
<insert id="batchInsertUserRole" parameterType="java.util.List">
    INSERT INTO t_user_role (user_id, role_id, comp_id, create_by, create_time)
    VALUES
    <foreach collection="list" item="item" separator=",">
        (#{item.userId}, #{item.roleId}, #{item.compId}, #{item.createBy}, #{item.createTime})
    </foreach>
</insert>
```

```java
// 批量插入：1 次 SQL 替代 N 次循环
List<UserRole> list = new ArrayList<>();
for (Integer roleId : roleIds) {
    UserRole ur = new UserRole();
    ur.setUserId(userId);
    ur.setRoleId(roleId);
    list.add(ur);
}
userRoleMapper.batchInsertUserRole(list);  // 1 次 SQL（实际方法名为 batchInsertUserRole）
```

### 3.3 分页查询优化

core 使用 `PageParam<T>` 统一分页，配合 MyBatis 分页插件：

```java
// Service 层分页查询
public PageResult<User> selectByPage(PageParam<User> pageParam) {
    long total = userMapper.countBySelective(pageParam);
    List<User> list = userMapper.selectBySelective(pageParam);
    return new PageResult<>(list, total, pageParam.getPageNum(), pageParam.getPageSize());
}
```

**优化建议**：
- 避免深分页（`LIMIT 100000, 10`），改用 `WHERE id > lastId LIMIT 10` 游标分页；
- `count` 查询与 `select` 查询分开，必要时使用估算 count 提速；
- 列表查询只 SELECT 必要字段，避免 `SELECT *`。

### 3.4 延迟加载配置

```xml
<!-- spring-mybatis.xml 启用延迟加载 -->
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
    <property name="configLocation" value="classpath:mybatis-config.xml"/>
</bean>

<!-- mybatis-config.xml -->
<settings>
    <setting name="lazyLoadingEnabled" value="true"/>
    <setting name="aggressiveLazyLoading" value="false"/>
    <setting name="defaultExecutorType" value="REUSE"/>
</settings>
```

---

## 4. 多数据源性能优化

### 4.1 ThreadLocal 清理保障

**问题**：ThreadLocal 未清理会导致线程池复用时数据源串号。

**保障机制**：`DataSourceAspect` 在 `@After` 强制清理：

```java
@Aspect
@Component
public class DataSourceAspect {
    @After("@annotation(dataSource)")
    public void after(JoinPoint point, DataSource dataSource) {
        DataSourceHolder.clearDataSource();  // 强制清理
    }
}
```

**额外保障**：建议在 Servlet 过滤器中增加兜底清理：

```java
public class ThreadLocalCleanupFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
        try {
            chain.doFilter(req, res);
        } finally {
            DataSourceHolder.clearDataSource();  // 兜底清理
        }
    }
}
```

### 4.2 数据源切换耗时分析

| 操作 | 耗时 | 优化点 |
|------|------|--------|
| ThreadLocal 读写 | ~10ns | 无需优化 |
| AOP 拦截 | ~1ms | 可缓存注解解析结果 |
| RoutingDataSource 路由 | ~100ns | 无需优化 |
| 连接获取（池命中） | ~1ms | 调整连接池大小 |
| 连接获取（池未命中，需创建） | ~50ms | 增大 initialSize |

### 4.3 只读数据源优化

```java
// 只读数据源建议加 @Transactional(readOnly = true)
@DataSource("sap")
@Transactional(readOnly = true)
public List<SapOrder> querySapOrders(String orderNo) {
    return sapOrderMapper.selectByOrderNo(orderNo);
}
```

**收益**：
- 只读事务无 undo log，性能提升约 10%；
- Druid 可对只读连接做优化（不开启事务）。

---

## 5. 并发控制优化

### 5.1 线程池配置

core 提供 `RequestThreadPoolExecutor` 用于异步任务，关键配置：

```java
public class RequestThreadPoolExecutor {
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAX_POOL_SIZE = 50;
    private static final int QUEUE_CAPACITY = 200;
    private static final long KEEP_ALIVE_TIME = 60L;

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(
        CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
        TimeUnit.SECONDS, new LinkedBlockingQueue<>(QUEUE_CAPACITY),
        new ThreadPoolExecutor.CallerRunsPolicy()  // 拒绝策略：调用方执行
    );
}
```

**调优建议**：

| 参数 | 默认值 | 调优建议 | 说明 |
|------|--------|----------|------|
| `corePoolSize` | 10 | CPU 密集型=N+1；IO 密集型=2N | 根据 CPU 核数调整 |
| `maxPoolSize` | 50 | IO 密集型=2N×2 | 突发流量时扩容上限 |
| `queueCapacity` | 200 | 100-500 | 过大导致 OOM，过小导致拒绝 |
| `keepAliveTime` | 60s | 60s | 空闲线程回收时间 |
| 拒绝策略 | CallerRunsPolicy | CallerRunsPolicy | 避免任务丢失，回退到主线程 |

### 5.2 上下文传递性能

`ContextCopyingDecorator` 复制 ThreadLocal 的开销：

| 上下文项 | 复制耗时 | 优化建议 |
|----------|----------|----------|
| UserContext | ~1ms | 序列化复制，可改为引用传递 |
| DataSourceHolder | ~10ns | 简单 String，无优化必要 |
| RequestAttributes | ~5ms | 涉及 Map 复制，按需传递 |

**优化方案**：仅传递必要字段，避免全量复制：

```java
// 优化前：全量复制
ContextCopyingDecorator.decorate(() -> { ... });

// 优化后：仅传递必要字段
UserContext ctx = UserContext.getCurrent();
String dataSource = DataSourceHolder.getDataSource();
executor.submit(() -> {
    try {
        UserContext.setCurrent(ctx);
        DataSourceHolder.setDataSource(dataSource);
        // 业务逻辑
    } finally {
        UserContext.clear();
        DataSourceHolder.clearDataSource();
    }
});
```

### 5.3 登录错误计数并发问题

**问题**：`loginErrorCount` 并发自增可能丢失更新。

```java
// 错误：先读后写，并发不安全
User user = userMapper.selectByPrimaryKey(userId);
user.setLoginErrorCount(user.getLoginErrorCount() + 1);
userMapper.updateByPrimaryKey(user);

// 正确：DB 原子更新
userMapper.incrementLoginErrorCount(userId);

// XML
<update id="incrementLoginErrorCount">
    UPDATE t_user SET login_error_count = login_error_count + 1 WHERE user_id = #{userId}
</update>
```

---

## 6. JVM 调优

### 6.1 推荐 JVM 参数

```bash
# 堆内存（生产环境建议 4G-8G）
-Xms4g -Xmx4g
# 年轻代（堆的 1/3-1/2）
-Xmn1536m
# 元空间（替代永久代）
-XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m
# GC 策略（JDK 8 推荐 G1）
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:G1HeapRegionSize=16m
# GC 日志
-XX:+PrintGCDetails
-XX:+PrintGCDateStamps
-Xloggc:/var/log/pms/gc.log
-XX:+UseGCLogFileRotation
-XX:NumberOfGCLogFiles=10
-XX:GCLogFileSize=100M
# OOM 时 dump
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/var/log/pms/heapdump.hprof
```

### 6.2 core 模块内存占用分析

| 组件 | 内存占用 | 说明 |
|------|----------|------|
| SystemConfig Map | ~1MB | t_sys_variable 全量 |
| EhCache（会话+授权） | ~50-200MB | 取决于在线用户数 |
| Druid 连接池 | ~10MB | 连接对象 + 缓存 |
| MyBatis Mapper 注册 | ~5MB | XML 解析结果 |
| Spring 容器 Bean | ~30MB | 单例 Bean |
| Quartz 调度器 | ~5MB | 任务队列 |

### 6.3 Full GC 触发场景与规避

| 场景 | 原因 | 规避方案 |
|------|------|----------|
| EhCache 满触发 LRU | 缓存对象大 | 调大 maxEntriesLocalHeap 或缩短 TTL |
| 大文件上传 | 字节数组占用堆 | 改用流式处理，避免全量读入内存 |
| 批量数据导出 | 一次性加载过多 | 分页查询 + 流式写入 Excel |
| ThreadLocal 内存泄漏 | 未清理 | 使用 try-finally 确保清理 |
| Session 过期未清理 | EhCache 未配置 timeToIdle | 配置 timeToIdleSeconds |

---

## 7. 监控指标

### 7.1 关键性能指标

| 指标 | 阈值 | 监控方式 | 告警动作 |
|------|------|----------|----------|
| 接口平均响应时间 | <500ms | Druid URI 监控 | >1s 告警 |
| 慢 SQL 数量 | <10/min | Druid SQL 监控 | >50/min 告警 |
| 连接池活跃连接 | <maxActive×0.8 | Druid 连接池监控 | >80% 告警 |
| 连接池等待线程 | 0 | Druid 监控 | >0 告警 |
| Full GC 频率 | <1次/小时 | JVM 监控 | >5次/小时告警 |
| 堆内存使用率 | <80% | JVM 监控 | >85% 告警 |
| 登录失败率 | <5% | t_sys_log 统计 | >10% 告警 |
| 文件上传成功率 | >99% | t_file 统计 | <95% 告警 |

### 7.2 自定义监控埋点

```java
// 关键方法耗时埋点
@SystemControllerLog(description = "查询项目列表")
@RequestMapping("/list")
@ResponseBody
public Result list(ProjectQuery query) {
    long start = System.currentTimeMillis();
    try {
        Result result = service.selectByPage(query);
        return result;
    } finally {
        long cost = System.currentTimeMillis() - start;
        if (cost > 1000) {
            log.warn("慢查询: /list 耗时 {}ms, query={}", cost, query);
        }
        // 可上报到监控系统
        Metrics.record("api.list.cost", cost);
    }
}
```

---

## 8. 性能优化检查清单

### 8.1 开发阶段检查

- [ ] 实体继承 `BaseEntity`，避免重复定义审计字段
- [ ] Mapper 继承 `AbstractBaseMapper<T>`，复用 CRUD
- [ ] 复杂查询使用 `resultMap` 显式映射
- [ ] 批量操作使用 `foreach`，避免循环单条
- [ ] 列表查询避免 `SELECT *`，只查必要字段
- [ ] 只读查询加 `@Transactional(readOnly = true)`
- [ ] 外部数据源访问加 `@DataSource` 注解
- [ ] 异步任务使用 `ContextCopyingDecorator` 传递上下文
- [ ] ThreadLocal 在 `finally` 块清理
- [ ] 大文件上传使用流式处理

### 8.2 上线前检查

- [ ] Druid 监控页面配置密码保护
- [ ] 慢 SQL 阈值设置合理（建议 1s）
- [ ] 连接池参数根据压测结果调整
- [ ] JVM 参数根据内存分析调整
- [ ] GC 日志输出路径配置
- [ ] OOM dump 路径配置
- [ ] 监控指标接入告警系统
- [ ] 压测验证 maxActive/maxPoolSize 是否足够

### 8.3 运维阶段检查

- [ ] 每日检查 Druid 慢 SQL Top10
- [ ] 每周检查 GC 日志，分析 Full GC 频率
- [ ] 每月检查堆内存使用趋势
- [ ] 每季度评估连接池参数是否需要调整
- [ ] 定期清理过期 Session（EhCache 自动 + 数据库清理）

---

## 9. 性能优化案例

### 9.1 案例：登录接口慢查询优化

**问题**：登录接口平均耗时 2s，用户反馈卡顿。

**排查**：
1. Druid 监控发现 `SELECT t_user WHERE user_name=?` 耗时 1.5s；
2. 检查 `t_user` 表，`user_name` 字段无索引；
3. 数据量 5 万行，全表扫描。

**优化**：
```sql
-- 添加唯一索引
ALTER TABLE t_user ADD UNIQUE INDEX uk_user_name (user_name);
```

**效果**：登录耗时降至 50ms。

### 9.2 案例：菜单加载 N+1 查询优化

**问题**：首页加载耗时 3s，包含 20+ 次 DB 查询。

**排查**：
1. `LeftMenuTag` 查询菜单列表（1 次）；
2. 循环渲染时，每个菜单查询子菜单（N 次）；
3. 每个菜单查询权限标识（N 次）。

**优化**：
```java
// 优化前：N+1 查询
List<Menu> menus = shiroService.queryUserMenuByUsername(username);
for (Menu m : menus) {
    List<Menu> children = menuMapper.selectByParentId(m.getMenuId());  // N 次
    m.setChildren(children);
}

// 优化后：1 次查询 + 内存构建树
List<Menu> allMenus = shiroService.queryUserMenuByUsername(username);  // 1 次
Map<Integer, List<Menu>> parentIdMap = allMenus.stream()
    .collect(Collectors.groupingBy(Menu::getParentId));
allMenus.forEach(m -> m.setChildren(parentIdMap.get(m.getMenuId())));
```

**效果**：DB 查询从 21 次降至 1 次，首页加载降至 200ms。

### 9.3 案例：ThreadLocal 串号导致查错库

**问题**：偶发性数据查询错误，A 用户看到 B 用户公司的数据。

**排查**：
1. 日志发现 ThreadLocal 中 `DataSourceHolder` 值异常；
2. 定位到异步任务未使用 `ContextCopyingDecorator`，子线程复用主线程 ThreadLocal；
3. 线程池复用线程时，上一个任务的 ThreadLocal 未清理。

**优化**：
```java
// 错误：直接提交任务
executor.submit(() -> service.crossDbQuery());

// 正确：装饰任务
executor.submit(ContextCopyingDecorator.decorate(() -> {
    try {
        service.crossDbQuery();
    } finally {
        DataSourceHolder.clearDataSource();  // 确保清理
    }
}));
```

**效果**：串号问题消失。

---

## 10. 相关文档

- [编码规范](coding-standards.md) — 性能相关编码规范
- [安全实践](security-practices.md) — 安全与性能的平衡
- [故障排查](troubleshooting.md) — 性能问题排查案例
- [多数据源架构](../01-architecture/multi-datasource.md) — 数据源切换原理
- [Spring 配置](../01-architecture/spring-configuration.md) — 连接池与事务配置
- [MyBatis 配置](../01-architecture/mybatis-configuration.md) — Mapper 扫描与执行
