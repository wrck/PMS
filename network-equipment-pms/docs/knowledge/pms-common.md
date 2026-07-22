# pms-common 模块知识库

> 源码路径：`/workspace/network-equipment-pms/pms-common`
> 基础包名：`com.dp.plat.common`
> 父项目：`com.dp.plat:network-equipment-pms:1.0.0-SNAPSHOT`

---

## 模块概述

**定位**：`pms-common` 是 `network-equipment-pms`（网络设备项目管理系统）多模块工程的最底层公共基础模块，承担"横切关注点 + 跨模块解耦 + 基础设施"三类职责。

**核心职责**：

1. **统一返回与异常体系**：`Result<T>` / `ResultCode` / `BusinessException` / `GlobalExceptionHandler`，所有业务模块复用同一套 API 返回结构。
2. **横切关注点组件**：加密（AES-256-GCM 字段级加密）、限流（Bucket4j + Redis 分布式令牌桶）、幂等（Redis SETNX）、XSS 防护、安全响应头、链路追踪（MDC）、慢 SQL 监控、业务指标采集、编程式重试、Saga 协调器。
3. **SPI 解耦**：12 个跨模块调用接口（`ApprovalTrigger` / `BusinessDataLoader` / `TaskBatchCreator` 等），消除 `pms-project ↔ pms-workflow` 等模块间的双向依赖环。
4. **MyBatis-Plus 基础设施**：`BaseEntity`、分页/乐观锁拦截器、审计字段自动填充、JSON TypeHandler、慢 SQL 拦截器。
5. **Excel 工具与 DTO 契约**：基于 EasyExcel 的导入导出工具；模板快照、阶段退出条件等跨模块 DTO 契约。

**打包类型**：默认 `jar`（pom.xml 未显式声明 packaging，Maven 默认 `jar`）。

**artifactId / name**：`pms-common`。

**在依赖图中的位置**：被 13 个上层模块依赖，自身不依赖任何其他内部模块（位于依赖图最底层）。

---

## 包结构

```
com.dp.plat.common
├── annotation         # 自定义注解：@RateLimit / @Idempotent / @FieldEncrypt / @DataScope / @OperLog
├── aspect             # AOP 切面：限流、幂等、字段加密兜底解密；幂等键透传拦截器
├── config             # Spring 配置：AOP 启用 + Bucket4j ProxyManager 注册、MyBatis-Plus 拦截器、Web MVC 拦截器
├── constant           # 全局常量：CommonConstants（Authorization 头、分页默认值、菜单类型等）
├── crypto             # 加密：AesGcmEncryptor（AES-256-GCM）+ EncryptTypeHandler（MyBatis TypeHandler）
├── dto                # 跨模块 DTO 契约：模板快照、阶段退出条件、各类违规项、文件上传返回值
├── entity             # 基础实体：BaseEntity（id + 审计字段 + 逻辑删除）
├── enums              # 通用枚举接口：CommonEnum（getCode/getDescription）
├── excel              # EasyExcel 封装：ExcelUtils + ExcelImportResult + ExcelImportError
├── exception          # 异常体系：BusinessException / IntegrationException / RateLimitExceededException + GlobalExceptionHandler
├── filter             # Servlet Filter：XSS 清洗、安全响应头、敏感端点 IP 限流
├── handler            # MyBatis TypeHandler 集合：JsonTypeHandlers（解决泛型擦除）
├── metrics            # 业务指标采集：BusinessMetrics（项目计数/资产状态/结算金额）
├── mybatis            # MyBatis 拦截器：SlowSqlInterceptor（慢 SQL 监控 + Micrometer 指标）
├── result             # 统一返回：Result<T> + ResultCode 枚举
├── retry              # 编程式重试：RetryService + RetryConfig（指数退避）
├── saga               # Saga 协调器：SagaCoordinator + SagaContext + SagaStep + SagaResult
├── spi                # SPI 接口（12 个）：跨模块调用契约，业务模块通过 @Autowired(required=false) 注入
├── trace              # 链路追踪 Filter：TraceIdFilter + UserContextFilter（MDC 注入）
└── util               # 工具类：SecurityUtils（从 Spring Security 上下文读取当前用户）
```

---

## 核心基础类

| 类名 | 路径 | 职责 | 关键方法/字段 |
|------|------|------|---------------|
| `BaseEntity` | `entity/BaseEntity.java` | 所有实体的抽象基类，统一审计字段与逻辑删除 | `id`（`@TableId(AUTO)`）、`createTime`/`updateTime`/`createBy`/`updateBy`（`@TableField(fill=...)` 自动填充）、`deleted`（`@TableLogic` 逻辑删除） |
| `Result<T>` | `result/Result.java` | 统一 API 响应包装 | 静态工厂 `ok()` / `ok(data)` / `ok(msg,data)` / `fail(msg)` / `fail(code,msg)` / `fail(ResultCode)`；字段 `code` / `message` / `data`；常量 `SUCCESS_CODE=200` / `ERROR_CODE=500`；方法 `isSuccess()` |
| `ResultCode` | `result/ResultCode.java` | 标准错误码枚举 | `SUCCESS(200)` / `PARAM_ERROR(400)` / `UNAUTHORIZED(401)` / `FORBIDDEN(403)` / `NOT_FOUND(404)` / `METHOD_NOT_ALLOWED(405)` / `REQUEST_TIMEOUT(408)` / `CONFLICT(409)` / `TOO_MANY_REQUESTS(429)` / `INTERNAL_SERVER_ERROR(500)` / `SERVICE_UNAVAILABLE(503)` / `INTEGRATION_FAILURE(503)` / `BUSINESS_ERROR(1001)` / `TOKEN_INVALID(1002)` / `TOKEN_EXPIRED(1003)` / `ACCOUNT_LOCKED(1004)` / `ACCOUNT_DISABLED(1005)` / `USERNAME_OR_PASSWORD_ERROR(1006)` |
| `CommonConstants` | `constant/CommonConstants.java` | 全局静态常量 | `AUTHORIZATION_HEADER` / `TOKEN_PREFIX="Bearer "` / `DEFAULT_PAGE_NUM=1` / `DEFAULT_PAGE_SIZE=10` / `UTF_8` / `STATUS_NORMAL="0"` / `STATUS_DISABLE="1"` / `SUPER_ADMIN_ROLE="admin"` / `LOGIN_USER_KEY="login_user:"` / `MENU_TYPE_DIR="M"` / `MENU_TYPE_MENU="C"` / `MENU_TYPE_BUTTON="F"` |
| `CommonEnum` | `enums/CommonEnum.java` | 枚举统一契约接口 | `int getCode()` / `String getDescription()` |

---

## SPI 接口清单

SPI 模式用于打破模块间双向依赖：接口下沉到 `pms-common`，由具体业务模块实现并注册为 Spring Bean，调用方通过 `@Autowired(required=false)` 注入；模块未加载时调用方应 `log.warn` 跳过，不阻断主流程。

| 接口名 | 方法签名 | 用途 | 实现模块 |
|--------|----------|------|----------|
| `ApprovalPlanBatchCreator` | `void batchCreateApprovalPlans(Long projectId, Map<String,Long> phaseCodeToIdMap, List<ApprovalPlanDef> approvalPlanDefs)` | 模板深拷贝时跨模块批量注册审批计划（按阶段触发审批） | `pms-workflow` |
| `ApprovalStatusChecker` | `List<ApprovalViolation> findApprovalViolations(Long projectId, String approvalType, boolean mustApproved)` | 阶段退出条件 APPROVAL 分支：跨模块校验关联审批是否已通过 | `pms-workflow` |
| `ApprovalTrigger` | `Long triggerApproval(String approvalType, Long businessId, Long projectId, String title, String reason)` | 跨模块触发审批流程（如 BASELINE_CHANGE），返回审批记录 ID | `pms-workflow` |
| `BusinessDataLoader` | `Map<String,Object> load(String approvalType, Long businessId)` + `String supportedType()` | 审批中心按审批类型加载业务字段用于脱敏展示；按 `supportedType()` 路由 | 各业务模块（交付件/风险/变更/项目等） |
| `BusinessFileStorage` | `StoredBusinessFile upload(MultipartFile file, String businessType, Long businessId)` + `void delete(Long attachmentId)` | 跨模块业务文件存储端口，避免领域模块直接依赖 `pms-file` | `pms-file` |
| `DeliverableBatchCreator` | `void batchCreateDeliverables(Long projectId, Long phaseId, List<DeliverableDef> deliverableDefs)` | 模板深拷贝时跨模块批量创建交付件（DRAFT 状态，currentVersion=1） | `pms-deliverable` |
| `DependencyBatchCreator` | `void batchCreateDependencies(Long projectId, List<DependencyDef> dependencyDefs)` | 模板深拷贝时跨模块批量创建任务依赖（按任务名解析为 ID） | `pms-baseline` |
| `MandatoryDeliverableValidator` | `List<DeliverableViolation> findMandatoryDeliverableViolations(Long phaseId)` | 阶段退出条件 DELIVERABLE 分支：复用交付件模块的必需交付件校验 | `pms-deliverable` |
| `ProjectConfigProvider` | `String get(Long projectId, Long templateId, String key)` | 跨模块读取项目级配置（读取顺序：项目级 > 模板级 > 系统默认） | `pms-project` |
| `ProjectPhaseLookup` | `Long findProjectId(Long phaseId)` | 由阶段 ID 反查所属项目 ID | `pms-project` |
| `TaskBatchCreator` | `void batchCreateTasks(Long projectId, Long phaseId, List<TaskDef> taskDefs)` | 模板深拷贝时跨模块批量创建任务（处理 parentTaskName 解析） | `pms-implementation` |
| `TaskCompletionChecker` | `List<TaskCompletionViolation> findUncompletedTasks(Long phaseId)` | 阶段退出条件 TASK 分支：跨模块查询阶段下未完成任务 | `pms-implementation` |

> 配套 DTO（位于 `com.dp.plat.common.dto`）：`TemplateSnapshot`（含 `PhaseDef`/`TaskDef`/`MilestoneDef`/`DeliverableDef`/`DependencyDef`/`ApprovalPlanDef`/`AssigneeRule` 7 个内部静态类）、`PhaseCriteria`、`PhaseExitGate`（含 4 类 `Required*` 子结构）、`ApprovalViolation`、`DeliverableViolation`、`TaskCompletionViolation`、`TaskPlanSnapshot`、`StoredBusinessFile`。

---

## 横切关注点组件

### 安全类

| 组件 | 类型 | 说明 |
|------|------|------|
| `AesGcmEncryptor` | `@Component` | AES-256-GCM 字段级加密器。密钥从 `app.security.encrypt-key` 读取（Base64 解码后必须 32 字节）；每次加密生成随机 12 字节 IV；输出格式 `Base64(IV ‖ ciphertext+tag)`，相同明文密文不同（语义安全）；提供 `getInstance()` 静态访问供非 Spring 管理的 TypeHandler 使用 |
| `EncryptTypeHandler` | MyBatis `BaseTypeHandler<String>` | 写入数据库时自动加密、读取时自动解密。**故意不标 `@Component`/`@MappedTypes`**，避免被注册为所有 String 字段默认 TypeHandler。通过 `AesGcmEncryptor.getInstance()` 懒加载，规避 MyBatis mapper 解析阶段 bean 未初始化的时序问题 |
| `FieldEncryptAspect` | `@Aspect` `@Component` | 字段加密**兜底解密**切面。`@AfterReturning` 拦截 `com.dp.plat..service..*` 与 `com.dp.plat..controller..*` 方法返回值，反射查找 `@FieldEncrypt` 字段并解密；容错策略——解密失败说明已是明文，跳过不抛异常。支持 `Result<T>` 自动解包、`Collection` 批量处理、父类字段继承 |
| `XssFilter` | `@WebFilter("/*")` `@Component` | XSS 清洗过滤器，将 `HttpServletRequest` 包装为 `XssHttpServletRequestWrapper`；排除路径 `/api/file/upload`（避免破坏 multipart 二进制内容） |
| `XssHttpServletRequestWrapper` | `HttpServletRequestWrapper` | 基于 Jsoup `Safelist.none()` 移除所有 HTML 标签；对 `<script>`/`<style>` 的 DataNode 先转 TextNode 保留可见文本；递归清洗 JSON 请求体的字符串节点；富文本白名单字段（`richcontent`/`description`/`content`/`remark`/`notice`/`html`）保留原始内容；缓存清洗后字节以支持 `getInputStream()` 重复读取 |
| `SecurityHeadersFilter` | `@Component` `Filter` | 注入 7 个标准安全响应头：`X-Content-Type-Options: nosniff` / `X-Frame-Options: SAMEORIGIN` / `X-XSS-Protection: 1; mode=block` / `Strict-Transport-Security: max-age=31536000; includeSubDomains` / `Content-Security-Policy: default-src 'self'; ...` / `Referrer-Policy: strict-origin-when-cross-origin` / `Permissions-Policy: geolocation=(), microphone=(), camera=()` |
| `SecurityUtils` | 工具类（final，私有构造） | 从 `SecurityContextHolder` 读取当前用户：`getAuthentication()` / `getCurrentUsername()`（优先从 `Authentication.details` Map 取 `username`，回退到 `UserDetails.username`，未认证返回 `"system"`）/ `getCurrentUserId()`（解析为 `Long`）/ `isAuthenticated()` |

### 性能类

| 组件 | 类型 | 说明 |
|------|------|------|
| `@RateLimit` + `RateLimitAspect` | 注解 + `@Aspect` | 基于 **Bucket4j 令牌桶 + Redis 分布式存储**的接口限流。`@RateLimit(key="#request.projectId", capacity=10, refillTokens=10, refillPeriodSeconds=60)`；Key 支持 SpEL（可引用 `#userId`/`#username`/方法参数）；默认 Key = `类名.方法名:userId:参数哈希`；超限抛 `RateLimitExceededException`（携带 `Retry-After` 秒数） |
| `RateLimitFilter` | `@Component` `OncePerRequestFilter` | **本地令牌桶**（`ConcurrentHashMap` 按 IP 隔离）针对敏感端点（`/api/auth/login`、`/api/auth/captcha`、`/api/auth/register`、`/api/auth/forgot-password`、`/api/auth/reset-password`）执行 IP 维度限流；默认 10 次/分钟/IP；超限直接写 HTTP 429 + `Retry-After` 头 + JSON 错误体；IP 提取优先级 `X-Forwarded-For → X-Real-IP → Proxy-Client-IP → WL-Proxy-Client-IP → RemoteAddr` |
| `SlowSqlInterceptor` | MyBatis `@Interceptor`（`StatementHandler.prepare`）`@Component` | 慢 SQL 监控。耗时 > 1s：WARN 日志 + `pms_slow_sql_total{threshold="warn"}` +1；耗时 > 5s：ERROR 日志 + `pms_slow_sql_total{threshold="error"}` +1；通过 `Timer pms_sql_duration_seconds{method=select/insert/update/delete}` 记录全部 SQL 耗时分布。**注意**：为 MyBatis 原生 Interceptor，不能加入 `MybatisPlusInterceptor`，由 `@Component` 自动发现注册，与 `MybatisPlusInterceptor` 平行生效 |
| `MyBatisPlusConfig` | `@Configuration` | 注册 `MybatisPlusInterceptor`（含 `PaginationInnerInterceptor` + `OptimisticLockerInnerInterceptor` + 容器内自定义 `InnerInterceptor`）；注册 `MetaObjectHandler` 自动填充 `createTime`/`updateTime`/`createBy`/`updateBy`/`deleted` |

### 可靠性类

| 组件 | 类型 | 说明 |
|------|------|------|
| `@Idempotent` + `IdempotentAspect` | 注解 + `@Aspect` | 基于 **Redis SETNX**（`SET key value NX EX ttl`）的接口幂等保护。`@Idempotent(key="#request.projectId + ':' + #action", ttl=120, policy=RETURN_FIRST_RESULT)`；Key 留空时从 `X-Idempotent-Key` 请求头读取；策略 `REJECT`（默认，抛 `BusinessException(code=409)`）或 `RETURN_FIRST_RESULT`（首次结果 JSON 反序列化返回）；用 `"PROCESSING"` 标记区分处理中与已完成；业务异常时主动 `delete` 释放键允许重试 |
| `IdempotentKeyInterceptor` | `@Component` `HandlerInterceptor` | 从 `X-Idempotent-Key` 请求头读取幂等键写入 request attribute（`idempotentKey`），为 SpEL 与下游组件提供备用访问方式；由 `WebMvcConfig` 注册到 `/**` |
| `RetryService` + `RetryConfig` | `@Service` + `@Builder` DTO | 编程式指数退避重试（与 Resilience4j 声明式 `@Retry` 互补）。`RetryConfig`：`maxAttempts=3` / `initialDelayMs=1000` / `multiplier=2.0` / `maxDelayMs=10000` / `retryExceptions`（为空时所有 `RuntimeException` 触发）；`executeWithRetry(name, Supplier)` 或 `executeWithRetry(name, Supplier, config)`；指标 `pms_retry_total{name,outcome=success/retry/exhausted}` + `pms_retry_attempts{name}` |
| `SagaCoordinator` + `SagaContext` | `@Component` + 基类 | 通用 Saga 协调器：管理步骤顺序执行与反向补偿。`SagaStep<T>`（`name` + `Function<T,Boolean> action` + `Consumer<T> compensation`）；action 返回 false 或抛异常触发补偿；补偿按反向顺序执行，**单个补偿失败不阻断后续补偿**（仅日志）；补偿动作应设计为幂等；不管理事务，每个步骤 action/compensation 内部自行控制事务；返回 `SagaResult<T>`（含 `success`/`errorMessage`/`executedSteps`/`compensatedSteps`） |
| `IntegrationException` | `BusinessException` 子类 | 集成服务（D365/FP/OA）调用失败时抛出，携带 `systemName`（`d365`/`fp`/`oa`）便于按系统聚合告警；由 `GlobalExceptionHandler` 转 HTTP 503 |

### 可观测性类

| 组件 | 类型 | 说明 |
|------|------|------|
| `TraceIdFilter` | `@Component` `Filter` `@Order(HIGHEST_PRECEDENCE)` | 链路追踪 ID 注入。优先从 `X-Trace-Id` 请求头沿用上游 traceId，缺失则生成 32 位无连字符 UUID；写入 MDC（key `traceId`）；回写响应头 `X-Trace-Id`；`finally` 清理 MDC 避免线程池串扰 |
| `UserContextFilter` | `@Component` `Filter` `@Order(HIGHEST_PRECEDENCE+10)` | 用户上下文注入。从 Spring Security 提取 `userId`/`username` 写入 MDC；同时注入 `requestUri`/`method` 便于按路径检索日志；`finally` 清理 MDC |
| `BusinessMetrics` | `@Component` | 业务指标采集。三类指标：`pms_project_created_total{type}`（Counter，项目创建计数）、`pms_asset_status{status}`（Gauge，资产状态分布，AtomicReference 持有可变值）、`pms_settlement_amount{currency}`（DistributionSummary，结算金额分布）。`MeterRegistry` 实现由 `pms-admin` 模块的 `micrometer-registry-prometheus` 提供 |
| `SlowSqlInterceptor` | 见性能类 | 同时承担可观测性职责：`pms_sql_duration_seconds` / `pms_slow_sql_total` 指标 |

---

## 自定义注解

| 注解 | 作用目标 | 作用 | 属性 |
|------|----------|------|------|
| `@RateLimit` | `ElementType.METHOD` | 接口限流（Bucket4j 令牌桶 + Redis 分布式存储）。由 `RateLimitAspect` 拦截，超限抛 `RateLimitExceededException`（HTTP 429 + `Retry-After`） | `key`（SpEL，默认空→`类名.方法名:userId:参数哈希`）/ `capacity`（默认 100）/ `refillTokens`（默认 100）/ `refillPeriodSeconds`（默认 60）/ `message`（默认"请求过于频繁，请稍后再试"） |
| `@Idempotent` | `ElementType.METHOD` | 接口幂等（Redis SETNX）。由 `IdempotentAspect` 拦截，重复请求按策略拒绝或返回首次结果 | `key`（SpEL，默认空→从 `X-Idempotent-Key` 头读取）/ `ttl`（默认 60 秒）/ `policy`（`REJECT` 默认 / `RETURN_FIRST_RESULT`）/ `message`（默认"请勿重复提交"） |
| `@FieldEncrypt` | `ElementType.FIELD` | 字段级加密（AES-256-GCM）。两层机制：MyBatis `EncryptTypeHandler` 在 DB 读写时加解密；`FieldEncryptAspect` 兜底解密未走 TypeHandler 的返回对象 | `algorithm`（默认 `AES/GCM/NoPadding`）/ `key`（配置项名，默认 `app.security.encrypt-key`） |
| `@DataScope` | `ElementType.METHOD` | 数据权限过滤标记。Mapper 方法标注后由 `DataPermissionInterceptor` 追加 SQL 过滤条件，限制结果集为当前用户可见行；管理员跳过 | `deptAlias`（保留，部门别名）/ `userAlias`（保留，用户别名） |
| `@OperLog` | `ElementType.METHOD` | 操作日志标记。由 `com.dp.plat.system.aop.OperLogAspect`（在 `pms-system` 模块）切面统一记录。放置于 `pms-common` 便于所有业务模块共享，避免业务模块反向依赖 `pms-system` | `title`（模块标题）/ `businessType`（1=新增/2=修改/3=删除/4=导出/5=导入/其他=查询，默认 0）/ `isSaveRequestData`（默认 true）/ `isSaveResponseData`（默认 true） |

---

## 全局异常与统一返回

### 异常体系

```
RuntimeException
└── BusinessException                       # 业务异常基类（@Getter）
    ├── code: int                           # 错误码（默认 ResultCode.BUSINESS_ERROR.getCode()=1001）
    ├── IntegrationException                # 集成异常：D365/FP/OA 调用失败
    │   └── systemName: String              # 外部系统标识
    └── RateLimitExceededException          # 限流超限
        └── retryAfterSeconds: long         # 写入 Retry-After 响应头
```

构造器：
- `BusinessException(String message)` / `BusinessException(int code, String message)` / `BusinessException(ResultCode resultCode)` / `BusinessException(ResultCode resultCode, String message)`
- `IntegrationException(String systemName, String message)` / `IntegrationException(String systemName, String message, Throwable cause)`
- `RateLimitExceededException(String message, long retryAfterSeconds)`（内部调用 `super(ResultCode.TOO_MANY_REQUESTS, message)`）

### GlobalExceptionHandler

`@RestControllerAdvice` 全局异常处理器，统一转换为 `Result<Void>` 响应：

| 异常类型 | HTTP 状态码 | ResultCode | 处理逻辑 |
|----------|-------------|------------|----------|
| `BusinessException` | 200（默认） | `e.getCode()` | `log.warn` + `Result.fail(code, msg)` |
| `IntegrationException` | 503 | `INTEGRATION_FAILURE(503)` | `log.error`（含 systemName） |
| `RateLimitExceededException` | 429 | `TOO_MANY_REQUESTS(429)` | 写 `Retry-After` 头 + 设置 status=429 |
| `MethodArgumentNotValidException` | 400 | `PARAM_ERROR(400)` | 拼接所有 FieldError 的 defaultMessage |
| `BindException` | 400 | `PARAM_ERROR(400)` | 拼接所有 FieldError 的 defaultMessage |
| `ConstraintViolationException` | 400 | `PARAM_ERROR(400)` | 拼接所有 ConstraintViolation 的 message |
| `AccessDeniedException` | 403 | `FORBIDDEN(403)` | Spring Security 权限拒绝 |
| `OptimisticLockingFailureException` | 409 | `CONFLICT(409)` | MyBatis-Plus 乐观锁冲突，提示"数据已被其他用户修改，请刷新后重试" |
| `HttpRequestMethodNotSupportedException` | 405 | `METHOD_NOT_ALLOWED(405)` | |
| `Exception`（兜底） | 500 | `INTERNAL_SERVER_ERROR(500)` | `log.error`（含 URI 与完整堆栈） |

### Result 结构

```java
@Data
public class Result<T> implements Serializable {
    public static final int SUCCESS_CODE = 200;
    public static final int ERROR_CODE = 500;
    private int code;
    private String message;
    private T data;
    // 静态工厂：ok() / ok(data) / ok(message,data) / fail(message) / fail(code,message) / fail(ResultCode)
    public boolean isSuccess() { return this.code == SUCCESS_CODE; }
}
```

### 错误码分段约定

- `2xx`：成功（仅 200）
- `4xx`：客户端错误（400 参数 / 401 未认证 / 403 无权限 / 404 不存在 / 405 方法不支持 / 408 超时 / 409 冲突 / 429 限流）
- `5xx`：服务端错误（500 内部错误 / 503 服务不可用 / 503 集成失败）
- `1xxx`：业务错误（1001 业务异常 / 1002 Token 无效 / 1003 Token 过期 / 1004 账号锁定 / 1005 账号禁用 / 1006 用户名或密码错误）

---

## 工具类

| 类名 | 路径 | 用途 | 关键方法 |
|------|------|------|----------|
| `SecurityUtils` | `util/SecurityUtils.java` | 从 Spring Security 上下文读取当前用户信息 | `getAuthentication()` / `getCurrentUsername()`（未认证返回 `"system"`）/ `getCurrentUserId()`（解析为 `Long`，失败返回 null）/ `isAuthenticated()` |
| `ExcelUtils` | `excel/ExcelUtils.java` | EasyExcel 封装，支持 HTTP 导出/导入/带校验导入 | `export(response, fileName, sheetName, head, data)` / `exportTemplate(response, fileName, sheetName, head)` / `importExcel(file, head)` / `importWithValidation(file, head, validator)` |
| `ExcelImportResult<T>` | `excel/ExcelImportResult.java` | Excel 导入聚合结果 | `successList` / `errors` / `getSuccessCount()` / `getErrorCount()` |
| `ExcelImportError` | `excel/ExcelImportError.java` | 单行校验错误描述 | `rowIndex`（1-based，不含表头）/ `rowData`（逗号分隔的字段值）/ `errorMessage` |
| `JsonTypeHandlers` | `handler/JsonTypeHandlers.java` | MyBatis-Plus `JacksonTypeHandler` 子类集合，每个具体泛型类型一个子类，解决泛型擦除导致元素退化为 `LinkedHashMap` 的问题 | `PhaseCriteriaHandler` / `PhaseExitGateHandler` / `TaskPlanSnapshotListHandler`（重写 `parse` 用 `TypeReference<List<TaskPlanSnapshot>>`）/ `TemplateSnapshotHandler` |

> `JsonTypeHandlers` 使用要求：实体类需 `@TableName(autoResultMap = true)`，否则字段级 typeHandler 在 BaseMapper 方法中不生效。

---

## 配置项

### application.yml 必须配置

| 配置项 | 用途 | 示例 |
|--------|------|------|
| `app.security.encrypt-key` | AES-256-GCM 字段加密密钥（Base64 编码的 32 字节）。未配置或长度不合法时 `AesGcmEncryptor.init()` 抛 `IllegalStateException` 启动失败 | `app.security.encrypt-key: "BASE64_ENCODED_32_BYTES_KEY"` |

### 隐式依赖配置（由 Spring Boot 自动配置）

| 配置项 | 用途 |
|--------|------|
| `spring.data.redis.*` | Spring Data Redis（Lettuce）。供 `IdempotentAspect`（`StringRedisTemplate`）与 `AspectConfig`（独立 `RedisClient` for Bucket4j）使用 |
| `spring.datasource.*` | MyBatis-Plus 数据源 |
| `mybatis-plus.configuration.*` | MyBatis-Plus 配置 |

### HTTP 请求头约定

| 请求头 | 用途 | 由谁消费 |
|--------|------|----------|
| `X-Trace-Id` | 链路追踪 ID（沿用上游或自动生成）；同时回写到响应头 | `TraceIdFilter` |
| `X-Idempotent-Key` | 幂等键（前端拦截器自动生成 UUID）；`IdempotentKeyInterceptor` 写入 request attribute | `IdempotentAspect` / `IdempotentKeyInterceptor` |
| `Authorization: Bearer <token>` | JWT 认证令牌 | Spring Security（约定见 `CommonConstants.AUTHORIZATION_HEADER` / `TOKEN_PREFIX`） |

---

## 模块依赖关系

### 内部模块依赖

**pms-common 不依赖任何其他内部模块**（位于依赖图最底层）。

### 被哪些模块依赖（13 个）

通过 grep `pms-common` 在各模块 `pom.xml` 中确认，以下模块均声明依赖 `com.dp.plat:pms-common`：

| 模块 | 用途 |
|------|------|
| `pms-system` | 用户/角色/菜单/部门/字典/操作日志（实现 `OperLogAspect`） |
| `pms-project` | 项目/阶段/模板/退出条件校验（消费大量 SPI） |
| `pms-asset` | 资产管理（使用 `BaseEntity`/`Result`/`BusinessMetrics`） |
| `pms-implementation` | 任务/里程碑（实现 `TaskBatchCreator`/`TaskCompletionChecker`） |
| `pms-workflow` | 审批中心/Flowable（实现 `ApprovalTrigger`/`ApprovalStatusChecker`/`ApprovalPlanBatchCreator`/`BusinessDataLoader`） |
| `pms-integration` | D365/FP/OA 集成（使用 `IntegrationException`/`RetryService`） |
| `pms-governance` | 风险/问题/变更治理 |
| `pms-notification` | 通知中心 |
| `pms-file` | 文件存储（实现 `BusinessFileStorage`） |
| `pms-lowcode` | 低代码平台 |
| `pms-baseline` | 基线管理（实现 `DependencyBatchCreator`，使用 `TaskPlanSnapshot`/`JsonTypeHandlers`） |
| `pms-deliverable` | 交付件（实现 `DeliverableBatchCreator`/`MandatoryDeliverableValidator`） |
| `pms-admin` | 启动模块（聚合所有模块；提供 `MeterRegistry` 运行时实现：`micrometer-registry-prometheus`） |

### 关键 Maven 依赖（pom.xml）

| 依赖 | 用途 |
|------|------|
| `spring-boot-starter-web` | Web 层基础（`@RestControllerAdvice`/`HandlerInterceptor`/`Filter`） |
| `spring-boot-starter-security` | `SecurityUtils` 读取 `SecurityContextHolder`；`AccessDeniedException` 处理 |
| `spring-boot-starter-aop` | 限流/幂等/字段加密切面 |
| `spring-boot-starter-data-redis` | `StringRedisTemplate`（幂等）+ `LettuceConnectionFactory`（Bucket4j） |
| `mybatis-plus-spring-boot3-starter` | `BaseEntity`/`MyBatisPlusInterceptor`/`JacksonTypeHandler` |
| `springdoc-openapi-starter-webmvc-ui` | OpenAPI 文档 |
| `spring-boot-starter-validation` | JSR-380 校验（`@Valid`/`ConstraintViolationException` 处理） |
| `io.micrometer:micrometer-core` | 业务指标与慢 SQL 指标采集（`MeterRegistry` 实现由 `pms-admin` 提供） |
| `com.alibaba:easyexcel` | `ExcelUtils` |
| `org.jsoup:jsoup:1.17.2` | XSS 过滤器 HTML 标签清洗 |
| `com.bucket4j:bucket4j-core:8.10.1` + `bucket4j-redis:8.10.1` | 分布式令牌桶限流 |
| `spring-boot-starter-test`（test） | 单元测试 |

---

## 关键技术点

### 1. AES-256-GCM 字段级加密（双层防护）

- **TypeHandler 层**（`EncryptTypeHandler`）：在 MyBatis 读写数据库时自动加解密，对业务代码透明。
- **Aspect 兜底层**（`FieldEncryptAspect`）：处理跨服务调用返回的实体或手写 SQL 查询结果（未经 TypeHandler），反射解密 `@FieldEncrypt` 字段，容错策略避免对明文重复解密报错。
- **密文格式**：`Base64(IV(12B) ‖ ciphertext+tag(16B))`，每次 IV 随机，相同明文密文不同。
- **静态实例访问**：`AesGcmEncryptor` 在 `@PostConstruct` 阶段将自身赋值给静态 `instance`，供非 Spring 管理的 `EncryptTypeHandler` 通过 `getInstance()` 获取，规避 MyBatis mapper 解析阶段 bean 未初始化的时序问题。
- **历史数据兼容**：解密失败时 `EncryptTypeHandler.decryptSafely` 原样返回，兼容历史明文数据。

### 2. Bucket4j 分布式令牌桶限流

- **独立 RedisClient**：`AspectConfig` 从 `LettuceConnectionFactory` 读取连接参数，构建独立的 `RedisClient`（与 Spring Data Redis 共享配置但独立连接），避免与共享连接竞争。
- **过期策略**：`ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofHours(1))`，桶长时间未访问后自动过期，避免 Redis 内存泄漏。
- **Key 隔离**：Redis Key = `rate_limit:{方法签名}:{SpEL 解析值}`，默认 `rate_limit:{类名.方法名}:{userId}:{参数哈希}`，按用户隔离且可识别重复提交。
- **SpEL 上下文**：注入 `#userId`/`#username` 内置变量，可引用方法参数（依赖 `-parameters` 编译选项）。
- **Retry-After 计算**：根据 `ConsumptionProbe.getNanosToWaitForRefill()` 向上取整，最小 1 秒。

### 3. Redis SETNX 幂等保护

- **PROCESSING 标记**：首次请求抢占成功后写入 `"PROCESSING"`，业务完成后按策略更新（`RETURN_FIRST_RESULT` 写入结果 JSON；`REJECT` 保持 PROCESSING 防止 TTL 窗口内重复提交）。
- **失败回滚**：业务方法抛异常时主动 `delete` 释放键，允许客户端重试。
- **反序列化降级**：`RETURN_FIRST_RESULT` 策略下，若 Redis 中仍是 `PROCESSING`（首次请求未完成）或反序列化失败，降级为 `REJECT` 行为。
- **SpEL 上下文**：注入 `#request`（`HttpServletRequest`），可调用 `getHeader`/`getAttribute`。

### 4. SPI 解耦模式

- **下沉接口**：跨模块调用接口（如 `ApprovalTrigger`）下沉到 `pms-common`，避免模块间直接依赖。
- **可选注入**：调用方使用 `@Autowired(required=false)`，模块未加载时 bean 为 null，调用方应 `log.warn` 跳过。
- **典型场景**：`pms-project` 的 `validateExitGate` 通过 `ApprovalStatusChecker`/`MandatoryDeliverableValidator`/`TaskCompletionChecker` 跨模块校验阶段退出条件，模块未加载时跳过对应分支校验。
- **DTO 契约**：跨模块传递的违规项（`ApprovalViolation`/`DeliverableViolation`/`TaskCompletionViolation`）与模板快照（`TemplateSnapshot`）也下沉到 `pms-common.dto`。

### 5. Saga 反向补偿协调器

- **事务边界**：协调器不管理事务，每个步骤的 action/compensation 内部自行控制独立事务，确保步骤间数据相互可见且补偿能看到已提交数据。
- **补偿容错**：单个补偿动作失败仅记录日志，不中断后续补偿（避免一个补偿失败导致其他已执行步骤无法回滚）。
- **幂等要求**：补偿动作应设计为幂等（相同补偿可能被多次调用，如人工重试）。
- **内部异常区分**：`SagaExecutionException` 区分步骤返回 false 与抛异常两种失败路径。

### 6. Micrometer 业务指标与可观测性

- **指标分层**：`pms-common` 仅依赖 `micrometer-core`，`MeterRegistry` 的运行时实现（`PrometheusMeterRegistry`）由 `pms-admin` 模块提供。组件扫描由 `PmsApplication` 的 `scanBasePackages = "com.dp.plat"` 覆盖。
- **业务指标**：`pms_project_created_total` / `pms_asset_status` / `pms_settlement_amount`（Counter/Gauge/DistributionSummary 三类）。
- **基础设施指标**：`pms_sql_duration_seconds` / `pms_slow_sql_total`（慢 SQL）/ `pms_retry_total` / `pms_retry_attempts`（重试）。
- **Gauge 实现**：`BusinessMetrics.registerAssetStatusGauge` 使用 `AtomicReference<Double>` 持有可变值，Gauge 绑定引用对象，Prometheus 抓取时读取最新值。

### 7. MDC 链路追踪与用户上下文

- **TraceIdFilter**（`HIGHEST_PRECEDENCE`）：最早执行，确保后续所有过滤器/拦截器的日志都携带 `traceId`。
- **UserContextFilter**（`HIGHEST_PRECEDENCE+10`）：晚于 TraceIdFilter，注入 `userId`/`username`/`requestUri`/`method`。
- **MDC 清理**：两个过滤器均在 `finally` 中 `MDC.remove`，避免线程池复用时上下文串扰。
- **异步线程传播**：通过 `DelegatingSecurityContextExecutor` 传播 SecurityContext，`MDCContextTaskDecorator` 复制 MDC。

### 8. JSON TypeHandler 子类化解决泛型擦除

- **问题**：MyBatis-Plus 的 `JacksonTypeHandler` 在泛型类型（如 `List<TaskPlanSnapshot>`）上因泛型擦除导致元素退化为 `LinkedHashMap`。
- **方案**：为每个具体泛型类型创建子类（如 `TaskPlanSnapshotListHandler`），在子类中通过 `TypeReference` 指定完整泛型类型，并重写 `parse` 方法使用自包含的 `ObjectMapper` 反序列化。
- **使用要求**：实体类需 `@TableName(autoResultMap = true)`，否则字段级 typeHandler 在 BaseMapper 方法中不生效。

### 9. MyBatis-Plus 审计字段自动填充

- **MetaObjectHandler** 在 `insertFill` 时填充 `createTime`/`updateTime`/`createBy`/`updateBy`/`deleted=0`；在 `updateFill` 时填充 `updateTime`/`updateBy`。
- **用户名来源**：`SecurityUtils.getCurrentUsername()`，未认证时返回 `"system"`。
- **逻辑删除**：`BaseEntity.deleted` 标注 `@TableLogic`，MyBatis-Plus 自动过滤 `deleted=1` 的记录。

### 10. 切面注册与条件化装配

- `AspectConfig` 通过 `@EnableAspectJAutoProxy(exposeProxy = true)` 启用 AspectJ 注解驱动（暴露代理对象支持同类方法互调）。
- Bucket4j 相关 Bean 使用 `@ConditionalOnClass({RedisClient.class, LettuceConnectionFactory.class})` 守卫，Classpath 不存在 Lettuce/Redis 时不创建。
- `RateLimitAspect` / `IdempotentAspect` 使用 `@ConditionalOnMissingBean` 守卫，允许业务模块覆盖。
- `SlowSqlInterceptor` 为 MyBatis 原生 `Interceptor`（非 MyBatis-Plus `InnerInterceptor`），通过 `@Component` 自动发现注册，**不能**通过 `MybatisPlusInterceptor.addInnerInterceptor` 注册。
