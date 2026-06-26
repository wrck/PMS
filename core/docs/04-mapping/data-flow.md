# core 模块 — 数据流向图

> 本文档描述 core 模块关键场景的端到端数据流向，包括用户登录、权限校验、菜单加载、数据字典加载、多数据源切换、操作日志、文件上传、定时任务等场景的组件交互、数据转换规则与异常处理路径。
> 与 [crud-matrix.md](crud-matrix.md) 互补：CRUD 矩阵呈现"组件×表"的静态映射，本文档呈现"请求→响应"的动态流向。

---

## 1. 用户登录认证数据流

### 1.1 本地账号登录（ShiroRealm）

```mermaid
sequenceDiagram
    autonumber
    participant U as 用户浏览器
    participant LC as LoginController
    participant Shiro as SecurityManager
    participant SR as ShiroRealm
    participant SS as IShiroService
    participant DB as 数据库(t_user/t_user_info)
    participant EH as EhCache(授权缓存)
    participant LOG as t_sys_log
    participant REC as t_user_login_record

    U->>LC: POST /login (username,password,captcha)
    LC->>Shiro: subject.login(token)
    Shiro->>SR: doGetAuthenticationInfo(token)
    SR->>SR: 校验验证码(captcha)
    alt 验证码错误
        SR-->>Shiro: CaptchaException
        Shiro-->>LC: AuthenticationException
        LC-->>U: 返回错误提示
    end
    SR->>SS: queryUserByName(username)
    SS->>DB: SELECT t_user WHERE user_name=?
    DB-->>SS: User(status,password,isSysUser,compId)
    SS-->>SR: User 对象
    alt 用户不存在
        SR-->>Shiro: UnknownAccountException
    else 状态=2(锁定)
        SR-->>Shiro: LockedAccountException
    else 状态=0(禁用)
        SR-->>Shiro: DisabledAccountException
    end
    SR->>SR: PasswordUtil.encryptMD5Password(pwd,username,1024)
    SR->>SR: 比对密码哈希
    alt 密码不匹配
        SR->>DB: UPDATE t_user SET loginErrorCount=loginErrorCount+1
        SR-->>Shiro: IncorrectCredentialsException
    else 密码正确
        SR->>REC: INSERT 登录记录(IP,userId,loginTime)
        SR->>LOG: INSERT 登录日志(type=login)
        SR->>DB: UPDATE t_user SET loginErrorCount=0, lastLoginTime=now
        SR-->>Shiro: SimpleAuthenticationInfo(principal,credentials,salt)
        Shiro->>EH: 缓存 Principal
        Shiro-->>LC: 认证成功
        LC-->>U: 302 重定向到首页
    end
```

### 1.2 CAS 单点登录（CasRealm）

```mermaid
sequenceDiagram
    autonumber
    participant U as 用户浏览器
    participant CF as CasFilter
    participant CS as CAS Server
    participant CR as CasRealm
    participant SS as IShiroService
    participant DB as 数据库(t_user)
    participant SM as SessionMappingStorage

    U->>CF: 访问受保护资源(未登录)
    CF->>CS: 302 重定向到 CAS 登录页(service=url)
    U->>CS: 输入 CAS 账号密码
    CS->>CS: 校验通过,生成 Service Ticket(ST)
    CS->>U: 302 重定向回应用 + ticket=ST
    U->>CF: 携带 ticket 访问回调 URL
    CF->>CR: CasRealm.doGetAuthenticationInfo(token)
    CR->>CS: 向 CAS 校验 ST(service ticket)
    CS-->>CR: 返回用户身份(username)
    CR->>SS: queryUserByName(username)
    SS->>DB: SELECT t_user WHERE user_name=?
    DB-->>CR: User 对象
    alt 本地用户存在且启用
        CR->>SM: 记录 sessionId ⇄ ST 映射
        CR-->>CF: SimpleAuthenticationInfo(principal)
        CF-->>U: 登录成功,写 Session,重定向首页
    else 本地用户不存在
        CR-->>CF: UnknownAccountException
        CF-->>U: 提示"未授权访问"
    end
```

### 1.3 登录数据转换规则

| 输入字段 | 转换规则 | 输出字段 | 落库表 |
|----------|----------|----------|--------|
| `username` | 直接使用 | `user_name` | t_user |
| `password` | MD5(username + password) 迭代 1024 次 | `password` | t_user（仅比对，不存储明文） |
| `captcha` | 与 Session 中 `captcha` 比对 | — | — |
| 客户端 IP | 从 `request.getRemoteAddr()` | `login_ip` | t_user_login_record |
| 当前时间 | `new Date()` | `login_time` | t_user_login_record |
| `isSysUser` | 1→compId=-1（全公司）；0→自身 compId | `compId` | Principal |

### 1.4 登录异常处理路径

| 异常类型 | 触发条件 | 用户提示 | 数据副作用 |
|----------|----------|----------|------------|
| `CaptchaException` | 验证码错误或过期 | "验证码错误" | 无 |
| `UnknownAccountException` | 用户名不存在 | "用户名或密码错误" | 无（不暴露用户是否存在） |
| `LockedAccountException` | t_user.status=2 | "账号已锁定，请联系管理员" | 无 |
| `DisabledAccountException` | t_user.status=0 | "账号已禁用" | 无 |
| `IncorrectCredentialsException` | 密码比对失败 | "用户名或密码错误" | loginErrorCount+1 |
| `ExcessiveAttemptsException` | 失败次数超阈值 | "登录失败次数过多，请稍后再试" | 锁定账号 |

---

## 2. 权限校验数据流

### 2.1 授权信息加载流程

```mermaid
flowchart TD
    A[用户已认证<br/>Principal 在 Session] --> B[访问受 @RequiresPermissions 资源]
    B --> C{授权缓存命中?}
    C -->|命中| D[直接返回 AuthorizationInfo]
    C -->|未命中| E[ShiroRealm.doGetAuthorizationInfo]
    E --> F{isSysUser?}
    F -->|是| G[compId=-1<br/>全公司权限]
    F -->|否| H[compId=用户公司ID<br/>本公司权限]
    G --> I[IShiroService.queryUserRoleByNameAndCompId]
    H --> I
    I --> J[SELECT t_user_role JOIN t_role<br/>WHERE user_name AND comp_id]
    J --> K[角色字符串集合 Set&lt;String&gt;]
    K --> L[IShiroService.queryPermissionByUsernameAndCompId]
    L --> M[SELECT t_role_permission JOIN t_permission<br/>WHERE role_id IN ...]
    M --> N[权限字符串集合 Set&lt;String&gt;]
    N --> O[SimpleAuthorizationInfo<br/>roles + permissions]
    O --> P[写入 EhCache<br/>key=principal, ttl=10min]
    P --> D
    D --> Q{权限判断}
    Q -->|通过| R[执行业务方法]
    Q -->|拒绝| S[抛出 UnauthorizedException]
    S --> T[ExceptionController 处理<br/>返回 403 或错误页]
```

### 2.2 权限字符串格式

| 权限类型 | 格式 | 示例 | 含义 |
|----------|------|------|------|
| 菜单权限 | `menu:menuCode` | `menu:user-mgmt` | 访问某菜单页面 |
| 操作权限 | `module:action` | `user:create`、`user:delete` | 模块操作权限 |
| 角色 | `ROLE_XXX` | `ROLE_ADMIN`、`ROLE_SM` | 角色标识（角色字符串） |

### 2.3 公司隔离规则

```mermaid
flowchart LR
    U[用户登录] --> P{isSysUser 字段}
    P -->|isSysUser=1| S[系统用户]
    P -->|isSysUser=0| N[普通用户]
    S --> SC[compId=-1<br/>查询所有公司数据]
    N --> NC[compId=用户所属公司<br/>仅查询本公司数据]
    SC --> Q1[SQL 条件: comp_id=-1<br/>或无 comp_id 过滤]
    NC --> Q2[SQL 条件: comp_id=用户compId]
```

> **避坑**：`isSysUser` 字段决定数据范围，新增业务表必须包含 `comp_id` 字段以支持公司隔离，否则系统用户与普通用户看到的数据相同。

---

## 3. 菜单加载数据流

### 3.1 菜单渲染流程

```mermaid
sequenceDiagram
    autonumber
    participant U as 用户浏览器
    participant LMT as LeftMenuTag
    participant SS as IShiroService
    participant MU as MenuUtil
    participant DB as 数据库(t_menu/t_role_menu)
    participant EH as EhCache

    U->>LMT: 加载首页(含 leftMenu 标签)
    LMT->>SS: queryUserMenuByUsername(username)
    SS->>DB: SELECT t_menu JOIN t_role_menu<br/>JOIN t_user_role WHERE user_name=?
    DB-->>SS: List&lt;Menu&gt;(扁平结构)
    SS-->>LMT: 用户菜单列表
    LMT->>MU: buildMenuTree(扁平列表)
    MU->>MU: 按 parentId 递归构建树
    MU->>MU: 按 sortOrder 排序
    MU-->>LMT: 菜单树根节点列表
    LMT->>LMT: 渲染 HTML(ul/li 嵌套)
    LMT-->>U: 返回带菜单的页面 HTML
```

### 3.2 菜单数据结构转换

**数据库扁平结构 → 内存树结构**：

```
数据库 t_menu（扁平）:
  menuId | parentId | menuName | menuUrl | sortOrder | menuType
  1      | 0        | 系统管理  | #       | 1         | 1
  11     | 1        | 用户管理  | /user   | 1         | 2
  12     | 1        | 角色管理  | /role   | 2         | 2

内存树结构（MenuUtil.buildMenuTree）:
  系统管理(menuId=1)
    ├── 用户管理(menuId=11)
    └── 角色管理(menuId=12)
```

| 字段 | 来源 | 用途 |
|------|------|------|
| `menuId` | t_menu.menu_id | 唯一标识 |
| `parentId` | t_menu.parent_id | 构建父子关系，0=根 |
| `menuName` | t_menu.menu_name | 显示文本 |
| `menuUrl` | t_menu.menu_url | 点击跳转 URL |
| `sortOrder` | t_menu.sort_order | 同级排序 |
| `menuType` | t_menu.menu_type | 1=目录 2=菜单 3=按钮 |

### 3.3 菜单权限过滤规则

```mermaid
flowchart TD
    A[所有启用菜单<br/>t_menu WHERE status=1] --> B[JOIN t_role_menu<br/>获取角色关联菜单]
    B --> C[JOIN t_user_role<br/>过滤当前用户角色]
    C --> D[用户可见菜单集合]
    D --> E{menuType=1 目录?}
    E -->|是| F[递归检查子菜单<br/>无可见子菜单则隐藏]
    E -->|否| G[直接显示]
    F --> H[渲染]
    G --> H
```

> **避坑**：菜单数据每次页面加载都会查询数据库（无缓存），高并发场景建议在 `LeftMenuTag` 中加入 Session 级缓存，避免重复查询。

---

## 4. 数据字典加载数据流

### 4.1 字典加载流程

```mermaid
sequenceDiagram
    autonumber
    participant JSP as JSP 页面
    participant DS as IDictionaryService
    participant DM as DictionaryMapper
    participant DB as 数据库(t_dictionary/t_dictionary_type)
    participant SC as SystemConfig

    JSP->>DS: selectByDicTypeId(dicTypeId)
    DS->>DM: selectByDicTypeId
    DM->>DB: SELECT t_dictionary WHERE dic_type_id=? AND status=1 ORDER BY sort_order
    DB-->>DM: List&lt;Dictionary&gt;
    DM-->>DS: 字典项列表
    DS-->>JSP: 字典数据
    JSP->>JSP: 渲染下拉框/单选/多选
```

### 4.2 字典数据结构

```
t_dictionary_type（字典类型）:
  dicTypeId | dicTypeName | dicTypeCode
  1         | 性别        | gender
  2         | 项目状态    | projectState

t_dictionary（字典项）:
  dicId | dicTypeId | dicName | dicValue | sortOrder | status
  101   | 1         | 男      | 1        | 1         | 1
  102   | 1         | 女      | 2        | 2         | 1
  201   | 2         | 待启动  | 10       | 1         | 1
  202   | 2         | 进行中  | 30       | 2         | 1
```

### 4.3 字典加载场景

| 场景 | 触发时机 | 加载方式 | 性能影响 |
|------|----------|----------|----------|
| 表单下拉框 | 页面渲染 | 同步查询 | 每个下拉框一次 DB 查询 |
| 列表翻译 | 数据展示 | Service 层批量翻译 | 一次查询翻译全部 |
| 系统参数 | 应用启动 | SystemConfig 一次性加载 | 启动时加载，运行时读内存 |

> **避坑**：`t_dictionary` 查询无缓存，列表页含多个字典字段时会产生 N+1 查询问题。建议在 Service 层增加 Map 缓存或使用 `selectByDicTypeId` 批量预加载。

---

## 5. 多数据源切换数据流

### 5.1 注解驱动的数据源切换

```mermaid
sequenceDiagram
    autonumber
    participant C as Controller
    participant A as DataSourceAspect
    participant H as DataSourceHolder
    participant S as Service
    participant M as Mapper
    participant R as RoutingDataSource
    participant T as 目标数据源

    C->>S: 调用 Service 方法
    Note over S: @DataSource("sap") 标注
    S->>A: AOP 拦截(@Before)
    A->>H: setDataSource("sap")
    H->>H: ThreadLocal.put("sap")
    S->>M: 执行 SQL
    M->>R: getConnection()
    R->>H: getDataSource()
    H-->>R: "sap"
    R->>T: determineTargetDataSource("sap")
    T-->>R: SAP 数据源连接
    R-->>M: Connection
    M-->>S: SQL 执行结果
    S->>A: AOP 拦截(@After)
    A->>H: clearDataSource()
    H->>H: ThreadLocal.remove()
    S-->>C: 返回结果
```

### 5.2 数据源切换规则

| 注解位置 | 作用范围 | 优先级 | 示例 |
|----------|----------|--------|------|
| 类级 `@DataSource("sap")` | 类所有方法 | 低 | 整个 Service 走 SAP 库 |
| 方法级 `@DataSource("d365")` | 单个方法 | 高（覆盖类级） | 单个方法走 D365 库 |
| 无注解 | 默认数据源 | — | 走 `defaultTargetDataSource`（mysql） |

### 5.3 线程池场景的上下文传递

```mermaid
flowchart LR
    M[主线程<br/>ThreadLocal=sap] --> E[RequestThreadPoolExecutor]
    E --> D[ContextCopyingDecorator]
    D -->|复制 ThreadLocal| S1[子线程1<br/>ThreadLocal=sap]
    D -->|复制 ThreadLocal| S2[子线程2<br/>ThreadLocal=sap]
    S1 --> R1[正确访问 SAP 库]
    S2 --> R2[正确访问 SAP 库]
    S1 --> C1[执行完毕<br/>清理 ThreadLocal]
    S2 --> C2[执行完毕<br/>清理 ThreadLocal]
```

> **避坑**：异步任务必须使用 `ContextCopyingDecorator.decorate(runnable)` 包装，否则子线程 ThreadLocal 为空，会回退到默认数据源，导致查错库。

---

## 6. 操作日志写入数据流

### 6.1 AOP 自动日志流程

```mermaid
sequenceDiagram
    autonumber
    participant C as Controller
    participant A as SystemLogAspect
    participant LOG as t_sys_log
    participant EH as EhCache

    C->>A: 调用 @SystemControllerLog 方法
    A->>A: @Before 记录开始时间
    A->>C: 执行业务方法
    C-->>A: 返回结果/抛异常
    A->>A: @AfterReturning / @AfterThrowing
    A->>A: 构建 SysLog 对象
    A->>LOG: INSERT 日志记录
    A->>EH: 清除相关缓存(可选)
    A-->>C: 返回结果
```

### 6.2 日志字段映射

| SysLog 字段 | 数据来源 | 说明 |
|-------------|----------|------|
| `description` | `@SystemControllerLog.description` | 注解描述 |
| `method` | `joinPoint.getSignature()` | 方法签名 |
| `params` | `joinPoint.getArgs()` 序列化 | 入参 JSON |
| `result` | 返回值序列化 | 出参 JSON（成功时） |
| `exception` | 异常堆栈 | 失败时记录 |
| `userId` | Principal | 当前用户 ID |
| `userName` | Principal | 当前用户名 |
| `ip` | request.getRemoteAddr | 客户端 IP |
| `operationTime` | System.currentTimeMillis | 操作时间戳 |
| `costTime` | 结束-开始 | 耗时(ms) |

---

## 7. 文件上传数据流

### 7.1 上传流程

```mermaid
sequenceDiagram
    autonumber
    participant U as 用户浏览器
    participant UC as UploaderController
    participant FS as IFileInfoService
    participant FT as FileType 校验
    participant DIS as 磁盘存储
    participant DB as t_file

    U->>UC: POST /upload (multipart/form-data)
    UC->>FT: 校验文件类型(typeCode)
    FT->>FS: selectFileTypeByCode(typeCode)
    FS->>DB: SELECT t_file_type WHERE type_code=?
    DB-->>FS: FileType(extensions,maxSize)
    FS-->>UC: FileType
    UC->>UC: 校验扩展名 + 大小
    alt 校验失败
        UC-->>U: Result.fail(类型/大小不符)
    end
    UC->>DIS: 保存文件到磁盘(path=配置目录)
    DIS-->>UC: 文件路径
    UC->>FS: insertFileInfo(FileInfo, userName)
    FS->>DB: INSERT t_file(originalName, path, size, type, uploadBy, uploadTime)
    DB-->>FS: fileId
    FS-->>UC: fileId
    UC-->>U: Result.success(fileId)
```

### 7.2 文件下载流程

```mermaid
sequenceDiagram
    autonumber
    participant U as 用户浏览器
    participant UC as UploaderController
    participant FS as IFileInfoService
    participant DB as t_file
    participant DIS as 磁盘存储
    participant DL as t_file_download_log

    U->>UC: GET /download?fileId=xxx
    UC->>FS: selectFileInfoById(fileId)
    FS->>DB: SELECT t_file WHERE file_id=?
    DB-->>FS: FileInfo(path, originalName)
    FS-->>UC: FileInfo
    UC->>DIS: 读取文件流
    DIS-->>UC: InputStream
    UC->>DL: insertdownlog(fileIds, remoteAddr, user)
    DL->>DB: INSERT t_file_download_log
    UC-->>U: 文件流(响应头 Content-Disposition)
```

---

## 8. 定时任务数据流

### 8.1 邮件发送任务（MailerJob）

```mermaid
flowchart TD
    T[Quartz 触发<br/>Cron 表达式] --> J[MailerJob.execute]
    J --> Q[SELECT t_mails WHERE status=0<br/>待发送邮件]
    Q --> L{有待发邮件?}
    L -->|否| E[任务结束]
    L -->|是| S[SMTP 发送邮件]
    S --> R{发送成功?}
    R -->|是| U1[UPDATE t_mails SET status=1, sendTime=now]
    R -->|否| U2[UPDATE t_mails SET status=2, errorMsg=...]
    U1 --> L
    U2 --> L
```

### 8.2 数据同步任务（SynchronizeJob）

```mermaid
sequenceDiagram
    autonumber
    participant Q as Quartz
    participant SJ as SynchronizeJob
    participant EXT as 外部系统(SAP/D365/EHR)
    participant DB as 数据库(t_sync_log/t_sync_state)
    participant BIZ as 业务表

    Q->>SJ: 触发同步任务
    SJ->>DB: INSERT t_sync_log(status=running, startTime=now)
    SJ->>EXT: 拉取增量数据(根据 lastSyncTime)
    EXT-->>SJ: 数据列表
    SJ->>SJ: 数据转换 + 校验
    alt 转换成功
        SJ->>BIZ: UPSERT 业务表
        SJ->>DB: UPDATE t_sync_state SET lastSyncTime=now
        SJ->>DB: UPDATE t_sync_log SET status=success, endTime=now, count=N
    else 转换/写入失败
        SJ->>DB: UPDATE t_sync_log SET status=fail, errorMsg=...
    end
```

### 8.3 同步状态机

```mermaid
stateDiagram-v2
    [*] --> running: 任务启动
    running --> success: 全部数据同步成功
    running --> partial: 部分数据失败
    running --> fail: 全部失败/异常
    success --> [*]
    partial --> [*]
    fail --> [*]
```

---

## 9. 系统启动初始化数据流

### 9.1 启动加载流程

```mermaid
flowchart TD
    S[Spring 容器启动] --> SC[SystemConfig.init]
    SC --> Q1[SELECT t_sys_variable<br/>加载所有系统参数]
    Q1 --> M1[内存 Map&lt;String,String&gt;<br/>sysVariableMap]
    M1 --> SH[ShiroFilterFactoryBean<br/>构建过滤器链]
    SH --> Q2[SELECT t_resource<br/>WHERE status=1]
    Q2 --> CH[FilterChainDefinitionMap<br/>URL → 过滤器]
    CH --> RD[RoutingDataSource<br/>初始化多数据源]
    RD --> Q3[读取 jdbc.properties<br/>+ spring.xml 数据源配置]
    Q3 --> DS[初始化 Druid 连接池<br/>mysql/sap/d365/ehr/sms]
    DS --> EH[EhCache 初始化<br/>shiro-cache.xml]
    EH --> QZ[Quartz 调度器启动<br/>加载 beans-quartz.xml]
    QZ --> RG[注册定时任务<br/>MailerJob/SynchronizeJob]
    RG --> R[系统就绪]
```

### 9.2 启动加载的关键数据

| 组件 | 数据来源 | 加载时机 | 缓存位置 |
|------|----------|----------|----------|
| SystemConfig | t_sys_variable | 应用启动（一次） | 内存 Map |
| Shiro 过滤器链 | t_resource | 应用启动（一次） | FilterChainDefinitionMap |
| 多数据源 | jdbc.properties + spring.xml | 应用启动（一次） | RoutingDataSource |
| EhCache 配置 | ehcache.xml | 应用启动（一次） | CacheManager |
| Quartz 任务 | beans-quartz.xml | 应用启动（一次） | Scheduler |

> **避坑**：`t_resource` 表变更后必须重启应用才能生效（无热加载），生产环境调整 URL 权限需规划重启窗口。

---

## 10. 异步请求上下文传递数据流

### 10.1 异步任务执行流程

```mermaid
sequenceDiagram
    autonumber
    participant C as Controller(主线程)
    participant TP as RequestThreadPoolExecutor
    participant CD as ContextCopyingDecorator
    participant W as Worker 线程
    participant S as Service
    participant DB as 数据库

    C->>TP: submit(task)
    TP->>CD: decorate(task)
    CD->>CD: 复制主线程 ThreadLocal<br/>(UserContext, DataSource)
    CD->>W: 提交装饰后的 Runnable
    W->>W: 设置 ThreadLocal=主线程副本
    W->>S: 调用 Service
    S->>DB: 执行 SQL(数据源正确)
    DB-->>W: 结果
    W->>W: 清理 ThreadLocal
    W-->>TP: 任务完成
    TP-->>C: Future
```

### 10.2 上下文传递的关键字段

| ThreadLocal 字段 | 来源 | 用途 |
|------------------|------|------|
| `UserContext` | Session | 用户身份、权限、公司 |
| `DataSourceHolder.dataSource` | `@DataSource` 注解 | 数据源路由 |
| `RequestAttributes` | RequestContext | 请求属性 |
| `Locale` | Request | 国际化语言 |

> **避坑**：未使用 `ContextCopyingDecorator` 的普通线程池（如 `Executors.newFixedThreadPool`）无法传递上下文，子线程会丢失用户身份和数据源，导致权限校验失败或查错库。

---

## 11. 数据流异常处理汇总

### 11.1 异常处理链路

```mermaid
flowchart TD
    B[业务异常] --> T{异常类型}
    T -->|Shiro 认证异常| S1[ShiroFilter 捕获<br/>重定向登录页]
    T -->|Shiro 授权异常| S2[ExceptionController 捕获<br/>返回 403]
    T -->|业务校验异常| B1[Service 抛 BusinessException<br/>Controller 捕获返回 Result.fail]
    T -->|数据源异常| D1[DataSourceAspect @AfterThrowing<br/>清理 ThreadLocal]
    T -->|SQL 异常| M1[MyBatis 抛 DataAccessException<br/>ExceptionAspect 捕获]
    T -->|系统未捕获异常| G1[GlobalExceptionHandler<br/>记录 t_sys_log + 返回 500]
```

### 11.2 异常处理优先级

| 优先级 | 处理器 | 异常类型 | 处理方式 |
|--------|--------|----------|----------|
| 1 | ShiroFilter | AuthenticationException | 重定向登录页 |
| 2 | ExceptionController | UnauthorizedException | 返回 403 页面 |
| 3 | ExceptionAspect | DataAccessException | 记录日志 + 包装为 BusinessException |
| 4 | Controller | BusinessException | 返回 Result.fail(code, msg) |
| 5 | GlobalExceptionHandler | Throwable | 记录 t_sys_log + 返回 500 页面 |

---

## 12. 相关文档

- [CRUD 矩阵](crud-matrix.md) — 组件×表静态 CRUD 映射
- [系统架构](../01-architecture/system-architecture.md) — 分层架构与多数据源机制
- [Shiro 架构](../01-architecture/shiro-architecture.md) — 认证授权详细流程
- [多数据源](../01-architecture/multi-datasource.md) — 数据源切换原理
- [用户管理](../02-modules/user-management.md) — 用户/登录相关组件
- [菜单管理](../02-modules/menu-management.md) — 菜单树构建与渲染
- [系统日志](../02-modules/system-log.md) — 日志组件与 AOP
- [文件管理](../02-modules/file-management.md) — 文件上传下载
- [故障排查](../05-standards/troubleshooting.md) — 数据流相关故障案例
