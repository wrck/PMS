# core 模块 — 技术术语表

> 本文档对 core 模块及 PMS 系统相关的技术术语、业务概念、技术缩写、数据库术语进行详细解释，便于开发、运维、新成员理解文档与代码。
> 与 [PMS-struts 术语表](../../PMS-struts/docs/06-reference/glossary.md) 互补：本文档聚焦 core 框架层术语，PMS-struts 聚焦业务层术语。

---

## 一、core 模块专用术语

### core（核心基础框架模块）

PMS 系统的底层基础框架模块（artifactId: `pms-mvc-core`），为上层所有业务模块提供认证授权、多数据源、AOP、工具类、统一返回等横切基础设施。core 不含业务逻辑，仅提供地基能力，被 PMS-activiti、PMS-springmvc、PMS-security、PMS-ext-d365、pms-rules 等模块依赖。

### pms-mvc-core

core 模块的 Maven artifactId，强调其基于 Spring MVC 而非 Struts2（区别于 PMS-struts 模块）。

### AbstractBaseService

core 提供的泛型 Service 基类，继承 `IAbstractBaseService<T>` 接口。新实体 Service 继承后自动获得 `insert`/`select`/`update`/`delete`/`count`/分页 等标准 CRUD 方法，避免重复编码。

### AbstractBaseMapper

core 提供的泛型 Mapper 基类，定义标准 CRUD 方法签名，配合 XML 实现零样板 CRUD。新实体 Mapper 继承后无需手写基础 CRUD SQL。

### BaseEntity

core 实体基类，包含审计字段（`id`、`createBy`、`createTime`、`updateBy`、`updateTime`、`orgId`）和扩展字段（`customInfo`）。业务实体继承后自动获得审计能力。

### customInfo

core 实体的 JSON 扩展字段模式，存储在 `custom_info` 列（JSON 字符串）。业务实体可通过 `customInfo` 动态存取属性，避免频繁加列。`custom1`-`custom5` 是预留的独立列，用于高频访问的扩展字段（如 `areaPower`、`officeCode`）。

### Result

core 统一返回对象，封装 `code`（错误码）、`message`（消息）、`data`（数据）。所有 Controller 接口返回 `Result`，保证前端契约一致。

### ResultCode

core 错误码常量枚举，定义成功、参数错误、认证失败、无权限、系统异常等标准错误码。业务模块可继承扩展自有错误码。详见 [错误码定义](error-codes.md)。

### PageParam

core 分页参数对象，封装 `pageNum`（页码）、`pageSize`（每页大小）、`orderBy`（排序）、`param`（查询条件）。Service 层接收 `PageParam<T>` 返回 `PageResult<T>`。

### PageResult

core 分页结果对象，封装 `list`（数据列表）、`total`（总数）、`pageNum`、`pageSize`、`totalPages`。前端据此渲染分页组件。

---

## 二、认证授权术语

### Shiro

Apache Shiro，Java 认证授权框架。core 使用 Shiro 1.8.0 实现认证授权，通过自定义 Realm（`ShiroRealm`、`CasRealm`）连接数据源。详见 [Shiro 架构](../01-architecture/shiro-architecture.md)。

### Realm

Shiro 中连接数据源的认证授权组件。core 提供两种 Realm：
- `ShiroRealm`：本地账号密码认证 + 角色/权限授权
- `CasRealm`：CAS 单点登录认证

### Principal

认证主体，登录成功后封装用户信息（userId、userName、compId、isSysUser、roles、permissions）的核心对象，存入 Session 供后续授权使用。

### AuthenticationToken

认证令牌，封装用户提交的认证信息。core 扩展为 `UsernamePasswordCaptchaToken`，增加 `captcha`（验证码）字段。

### AuthenticationInfo

认证信息，Realm 认证成功后返回，包含 Principal、credentials（密码哈希）、salt（盐值）。Shiro 据此比对密码。

### AuthorizationInfo

授权信息，Realm 授权时返回，包含 roles（角色集合）和 permissions（权限集合）。Shiro 据此进行权限判断。

### Credentials

凭证，即密码。core 使用 MD5 + 用户名盐 + 1024 次迭代加密，存储在 `t_user.password` 列。

### Salt

盐值，密码加密时附加的随机数据。core 使用用户名（`username`）作为盐值，防止彩虹表攻击。

### CAS（Central Authentication Service）

中央认证服务，单点登录（SSO）协议。core 支持 CAS 3.2.2，通过 `CasRealm` 实现 PMS 与其他系统的单点登录。详见 [Shiro 架构 §CAS](../01-architecture/shiro-architecture.md)。

### ST（Service Ticket）

服务票据，CAS 认证成功后生成的一次性凭证，有效期约 10 秒，用于应用向 CAS Server 校验用户身份。

### TGT（Ticket Granting Ticket）

票据授权票据，CAS Server 上的会话凭证，有效期 2-8 小时，用于生成多个 ST。

### SSO（Single Sign-On）

单点登录，用户一次登录即可访问所有互信系统。core 通过 CAS 实现 SSO。

### Single Sign-Out

单点登出，CAS Server 登出后通知所有应用销毁本地 Session。core 通过 `MySingleSignOutFilter` 实现。

### SessionMappingStorage

会话映射存储，维护 ST ⇄ 本地 Session 的映射关系。core 默认使用 `HashMapBackedSessionMappingStorage`（内存），集群部署需替换为 Redis。

### PasswordUtil

core 密码工具类，提供 `encryptMD5Password(password, salt, iterations)` 方法，使用 MD5 + 盐 + 迭代加密密码。

### PasswordInterceptor

密码拦截器，校验密码强度与过期时间，强制用户定期修改密码。

### Captcha

验证码，core 在登录时校验图形验证码，防止暴力破解。验证码存入 Session，登录时比对。

### isSysUser

系统用户标记字段（`t_user.is_sys_user`）。值为 1 表示系统用户，授权时 `compId=-1`（全公司权限）；值为 0 表示普通用户，授权时使用自身 `compId`（本公司权限）。

### compId

公司 ID，用于多公司数据隔离。`t_user_role` 表包含 `comp_id` 字段，实现"同一用户在不同公司有不同角色"。

### orgId

组织 ID，`BaseEntity` 的字段，用于数据范围控制。与 `compId` 配合实现多维度数据隔离。

### areaPower

区域权限，存储在 `t_user_info.custom5`，逗号分隔的 officeCode 列表，控制用户可管辖的办事处范围。

### officeCode

办事处编码，存储在 `t_user_info.custom3`，关联 `fnd_department` 表，标识用户所属办事处。

---

## 三、多数据源术语

### RoutingDataSource

core 动态数据源路由类，继承 `AbstractRoutingDataSource`，根据 ThreadLocal 中的 Key 动态选择目标数据源。详见 [多数据源架构](../01-architecture/multi-datasource.md)。

### DataSourceHolder

数据源持有者，使用 ThreadLocal 存储当前线程的数据源 Key。提供 `setDataSource`、`getDataSource`、`clearDataSource` 方法。

### DataSourceAspect

数据源切面，AOP 拦截 `@DataSource` 注解，在方法执行前设置 ThreadLocal，方法执行后清理 ThreadLocal。

### @DataSource

数据源注解，标注在 Service 类或方法上，指定使用的数据源 Key。如 `@DataSource("sap")` 表示走 SAP 数据源。

### AbstractRoutingDataSource

Spring 提供的动态数据源抽象类，`RoutingDataSource` 的父类。通过 `determineCurrentLookupKey()` 方法返回当前数据源 Key。

### ThreadLocal

线程局部变量，core 用其持有当前线程的数据源 Key、用户上下文等信息。必须在线程池场景下正确传递和清理，避免串号。

### ContextCopyingDecorator

上下文复制装饰器，core 提供的工具类，用于异步任务场景下复制主线程的 ThreadLocal 到子线程，确保上下文一致。

### RequestThreadPoolExecutor

请求线程池执行器，core 提供的线程池封装，支持上下文传递，用于异步任务执行。

### Druid

阿里巴巴数据库连接池，core 使用 Druid 1.2.8（区别于 PMS-struts 的 DBCP）。提供监控、SQL 防火墙、连接池管理能力。

---

## 四、AOP 与日志术语

### AOP（Aspect-Oriented Programming）

面向切面编程，core 用于实现横切关注点：日志、数据源切换、异常处理。通过 Spring AOP 实现。

### SystemLogAspect

系统日志切面，AOP 拦截 `@SystemControllerLog` 注解，自动记录操作日志到 `t_sys_log` 表。

### @SystemControllerLog

系统日志注解，标注在 Controller 方法上，声明操作描述。切面自动记录方法、参数、返回值、异常、耗时。

### DataSourceAspect

数据源切面，详见"多数据源术语"。

### ExceptionAspect

异常切面，AOP 捕获 Service 层异常，包装为 `BusinessException` 并记录日志。

### SysLog

系统日志实体，对应 `t_sys_log` 表。记录操作描述、方法、参数、返回值、异常、用户、IP、时间、耗时。

### SyncLog

同步日志实体，对应 `t_sync_log` 表。记录数据同步任务的状态、开始时间、结束时间、同步数量、错误信息。

---

## 五、定时任务术语

### Quartz

企业级任务调度框架，core 使用 Quartz 集成定时任务。配置在 `beans-quartz.xml`。详见 [Quartz 配置](../01-architecture/quartz-configuration.md)。

### Cron Expression

Cron 表达式，定义任务执行时间规则。如 `0 0 2 * * ?` 表示每天 2:00 执行。

### Job

Quartz 任务接口，core 实现了 `MailerJob`（邮件发送）和 `SynchronizeJob`（数据同步）。

### Trigger

Quartz 触发器，定义任务执行时机。core 使用 `CronTriggerFactoryBean` 配置 Cron 触发器。

### Scheduler

Quartz 调度器，管理任务与触发器，负责按计划执行任务。

### MailerJob

邮件发送任务，扫描 `t_mails` 表中待发送邮件（status=0），通过 SMTP 发送，更新状态。

### SynchronizeJob

数据同步任务，从外部系统（SAP/D365/EHR/SMS）拉取数据，写入本地业务表，记录 `t_sync_log`。

---

## 六、缓存术语

### EhCache

Java 本地缓存框架，core 使用 EhCache 缓存 Shiro 会话与授权信息。配置在 `ehcache.xml`。

### shiro-activeSessionCache

Shiro 活跃会话缓存，存储已登录用户的 Session，TTL=30min，LRU 淘汰策略。

### shiro-authorizationCache

Shiro 授权缓存，存储用户的角色与权限信息，TTL=10min，避免重复查询数据库。

### shiro-authenticationCache

Shiro 认证缓存，存储用户的认证信息，TTL=10min。

### LRU（Least Recently Used）

最近最少使用，EhCache 默认的缓存淘汰策略。当缓存满时，淘汰最久未访问的项。

### TTL（Time To Live）

存活时间，缓存项从创建到过期的总时间。core 授权缓存 TTL=10min。

### TTI（Time To Idle）

空闲时间，缓存项两次访问之间的最大间隔。超过 TTI 则过期。

### SystemConfig

系统配置类，应用启动时一次性加载 `t_sys_variable` 表到内存 Map，运行时直接读内存，无 DB 查询。

---

## 七、数据库术语

### t_ 前缀表

core 管辖的系统支撑域表，使用 `t_` 前缀。包括用户（`t_user`）、角色（`t_role`）、权限（`t_permission`）、菜单（`t_menu`）、组织（`t_company`、`t_department`）、日志（`t_sys_log`、`t_sync_log`）、文件（`t_file`、`t_file_type`）等。

### fnd_ 前缀表

基础数据域表，使用 `fnd_` 前缀。包括基础数据（`fnd_basic_data`）、部门（`fnd_department`）等。core 部分功能依赖 fnd_ 表。

### pm_ 前缀表

项目管理域表，使用 `pm_` 前缀。属于 PMS-struts/PMS-springmvc 业务模块，core 不直接管辖。

### core 主数据源

core 模块的主数据库名由 `jdbc.properties` 的 `jdbc.url` 配置：开发环境为 `dppms_d365`，生产环境为 `dppms_d365`。core 管辖的 `t_*` 系统支撑域表位于此库。MySQL 8.0.16。

> **注意**：早期文档曾误用 `dppms_d365` 作为 PMS 主库名。经源码验证，core 主数据源实际由 `jdbc.url` 指定（dev=`dppms_d365`，release=`dppms_d365`），PMS-struts 历史主干使用 `dppms_d365`（见 `pms.url`）。`dppms_d365` 并非真实存在的数据库名。

### 多公司隔离

通过 `comp_id` 字段实现多公司数据隔离。系统用户（`isSysUser=1`）可访问全公司数据（`compId=-1`），普通用户仅访问本公司数据。

### 软删除

通过 `effective_to` 字段标记数据失效，而非物理删除。查询时加 `WHERE effective_to IS NULL` 过滤有效数据。

### 审计字段

`BaseEntity` 包含的审计字段：`create_by`（创建人）、`create_time`（创建时间）、`update_by`（更新人）、`update_time`（更新时间）。

---

## 八、Spring 与 Web 术语

### Spring IoC

控制反转，Spring 容器管理 Bean 的创建与依赖注入。core 使用 XML 配置 + 注解混合方式。

### Spring AOP

Spring 面向切面编程，core 用于日志、数据源、异常切面。基于动态代理实现。

### Spring MVC

Spring Web 框架，core 使用 Spring MVC 5.3.19 作为 Web 层（区别于 PMS-struts 使用 Struts2）。

### Root Context

根容器，Spring 父容器，加载 Service、DAO、数据源等非 Web Bean。配置在 `spring.xml`。

### Child Context

子容器，Spring MVC 子容器，加载 Controller、ViewResolver 等 Web Bean。配置在 `spring-mvc.xml`。

### DispatcherServlet

前端控制器，Spring MVC 入口，分发请求到 Controller。

### ContextLoaderListener

Spring 容器启动监听器，启动时加载 Root Context。

### @Controller

Spring MVC 控制器注解，标识类为 Web 控制器。

### @RequestMapping

请求映射注解，定义 URL 与方法的映射关系。

### @ResponseBody

响应体注解，将方法返回值直接作为 HTTP 响应体（通常序列化为 JSON）。

### @Transactional

事务注解，声明方法的事务属性。core 使用声明式事务管理。

### @Service

Service 层注解，标识类为业务服务。

### @Repository

DAO 层注解，标识类为数据访问对象。MyBatis Mapper 通常不标注，通过 `MapperScannerConfigurer` 自动扫描。

### @Component

通用组件注解，标识类为 Spring Bean。

### @Autowired

自动注入注解，Spring 按类型注入依赖。

### @Resource

JSR-250 注解，按名称注入依赖。core 代码中常用 `@Resource` 替代 `@Autowired`。

---

## 九、MyBatis 术语

### MyBatis

Java 持久层框架，core 使用 MyBatis 3.5.9。通过 XML 映射文件定义 SQL，自动映射结果集。详见 [MyBatis 配置](../01-architecture/mybatis-configuration.md)。

### Mapper

MyBatis 映射接口，定义数据访问方法。core Mapper 位于 `com.dp.plat.core.dao` 包，XML 位于 `com.dp.plat.core.mapping` 包（与接口同包）。

### resultMap

结果映射，定义数据库列与 Java 属性的对应关系。core 表列存在驼峰与下划线混用，必须使用 resultMap 显式映射。

### #

MyBatis 参数化占位符，使用预编译参数，防止 SQL 注入。如 `#{userName}`。

### $

MyBatis 字符串拼接占位符，直接替换为字符串，有 SQL 注入风险。仅用于动态表名、列名等场景，需白名单校验。

### SqlSessionFactory

MyBatis 会话工厂，创建 SqlSession。core 通过 `SqlSessionFactoryBean` 配置。

### MapperScannerConfigurer

Mapper 扫描器，自动扫描指定包下的 Mapper 接口并注册为 Bean。core 配置扫描 `com.dp.plat.**.dao`。

### TypeHandler

类型处理器，处理 Java 类型与 JDBC 类型的转换。core 可自定义 TypeHandler 处理 JSON 字段等。

---

## 十、外部系统术语

### SAP（Systems Applications and Products）

SAP ERP 系统，PMS 通过 `sap` 数据源连接，同步销售订单、退货订单数据。逐步被 D365 替代。

### D365（Dynamics 365）

微软 Dynamics 365 ERP 系统，替代 SAP。PMS 通过 `d365` 数据源连接，同步订单、合同等业务数据。

### EHR（Electronic Human Resources）

电子人力资源系统，PMS 通过 `ehr` 数据源每日 22:30 同步人员组织架构信息。

### SMS（Sales Management System）

销售管理系统，PMS 通过 `sms` 数据源每日 23:30 同步项目属性数据。

### OA（Office Automation）

办公自动化系统，PMS 早期通过 `oa` 数据源同步人员信息，已切换至 EHR。

### MES（Manufacturing Execution System）

制造执行系统，PMS 通过 `mes` 数据源（SQL Server `R2EMES5SQL`）同步生产数据。

### ITR（IT Service Request）

IT 服务请求系统，PMS 通过配置 ITR 基础 URL，在项目详情页提供工单查询跳转。

---

## 十一、其他技术术语

### RBAC（Role-Based Access Control）

基于角色的访问控制，core 采用 User-Role-Permission-Menu 四层模型。用户关联角色，角色关联权限，权限控制菜单与操作。

### MD5（Message Digest Algorithm 5）

消息摘要算法，core 用于密码加密。MD5 + 用户名盐 + 1024 次迭代，增加破解成本。

### HTTPS（HTTP Secure）

安全 HTTP 协议，CAS 配置建议使用 HTTPS，防止票据被窃取。

### JWT（JSON Web Token）

JSON Web 令牌，core 当前未使用，但 CAS 集成可考虑 JWT 替代 Session。

### RESTful

REST 风格 API，core 接口部分遵循 RESTful 风格，但未严格遵循（保留传统 Spring MVC 风格）。

### JSON（JavaScript Object Notation）

JavaScript 对象表示法，core 接口返回 JSON 格式的 `Result` 对象，`customInfo` 字段存储 JSON 字符串。

### POJO（Plain Old Java Object）

普通 Java 对象，core 实体类为 POJO，包含属性与 getter/setter。

### DAO（Data Access Object）

数据访问对象，core 的 Mapper 即 DAO 层。

### DTO（Data Transfer Object）

数据传输对象，core 的 VO（如 `UserInfoVO`）用于 Service 与 Controller 间传输数据。

### VO（Value Object）

值对象，core 用于封装查询结果，如 `UserInfoVO` 包含用户与用户信息表的关联数据。

### IDE（Integrated Development Environment）

集成开发环境，PMS 项目推荐使用 Eclipse（PMS-struts 为 Eclipse 项目）或 IntelliJ IDEA。

### JDK（Java Development Kit）

Java 开发工具包，PMS 使用 JDK 1.8，SPMS 使用 JavaSE-1.7。

### Maven

Java 项目管理工具，PMS 使用 Maven 多模块管理（core 为其中一个模块）。SPMS 为非 Maven 项目。

---

## 十二、术语速查表

### 按字母排序

| 术语 | 类别 | 简要说明 |
|------|------|----------|
| @DataSource | 多数据源 | 数据源切换注解 |
| @SystemControllerLog | AOP | 操作日志注解 |
| AbstractBaseMapper | core | 泛型 Mapper 基类 |
| AbstractBaseService | core | 泛型 Service 基类 |
| AOP | 技术 | 面向切面编程 |
| areaPower | 业务 | 区域权限 |
| AuthInfo | 认证 | 认证/授权信息 |
| BaseEntity | core | 实体基类 |
| CAS | 认证 | 单点登录协议 |
| compId | 业务 | 公司 ID |
| ContextCopyingDecorator | 多数据源 | 上下文复制装饰器 |
| customInfo | core | JSON 扩展字段 |
| D365 | 外部系统 | Dynamics 365 ERP |
| DataSourceAspect | 多数据源 | 数据源切面 |
| DataSourceHolder | 多数据源 | ThreadLocal 持有者 |
| Druid | 数据库 | 数据库连接池 |
| EHR | 外部系统 | 人力资源系统 |
| EhCache | 缓存 | 本地缓存框架 |
| isSysUser | 业务 | 系统用户标记 |
| Job | Quartz | 任务接口 |
| MD5 | 加密 | 消息摘要算法 |
| MyBatis | ORM | 持久层框架 |
| officeCode | 业务 | 办事处编码 |
| orgId | 业务 | 组织 ID |
| PageParam | core | 分页参数对象 |
| PageResult | core | 分页结果对象 |
| PasswordUtil | 认证 | 密码工具类 |
| Principal | 认证 | 认证主体 |
| Quartz | 定时任务 | 任务调度框架 |
| Realm | 认证 | Shiro 认证组件 |
| Result | core | 统一返回对象 |
| ResultCode | core | 错误码常量 |
| RBAC | 认证 | 基于角色的访问控制 |
| RoutingDataSource | 多数据源 | 动态数据源路由 |
| Shiro | 认证 | 认证授权框架 |
| SMS | 外部系统 | 销售管理系统 |
| SSO | 认证 | 单点登录 |
| ST | 认证 | CAS 服务票据 |
| SystemConfig | core | 系统配置类 |
| SystemLogAspect | AOP | 系统日志切面 |
| t_ 前缀 | 数据库 | 系统支撑域表 |
| ThreadLocal | 多数据源 | 线程局部变量 |
| TGT | 认证 | CAS 票据授权票据 |

---

## 十三、相关文档

- [代码示例](code-examples.md) — 术语对应的代码示例
- [错误码定义](error-codes.md) — 错误码相关术语
- [接口模板](interface-template.md) — 接口文档术语
- [系统架构](../01-architecture/system-architecture.md) — 架构术语详解
- [Shiro 架构](../01-architecture/shiro-architecture.md) — 认证授权术语
- [多数据源](../01-architecture/multi-datasource.md) — 数据源术语
- [PMS-struts 术语表](../../PMS-struts/docs/06-reference/glossary.md) — 业务层术语
