# 网络设备 PMS 平台 API 接口设计文档

> **文档编号**：05-API-接口设计文档
> **版本**：v1.0.0
> **维护团队**：网络设备 PMS 平台研发组
> **最后更新**：2026-07-22
> **关联文档**：`00-PRD`、`02-HLD`、`03-LLD`、`06-UI-UI设计文档`

---

## 目录

- [1. 引言](#1-引言)
  - [1.1 编写目的](#11-编写目的)
  - [1.2 读者对象](#12-读者对象)
  - [1.3 文档范围](#13-文档范围)
  - [1.4 术语与缩写](#14-术语与缩写)
  - [1.5 参考标准](#15-参考标准)
- [2. 通用规范](#2-通用规范)
  - [2.1 基础 URL 与版本管理](#21-基础-url-与版本管理)
  - [2.2 HTTP 方法约定](#22-http-方法约定)
  - [2.3 JWT 认证机制](#23-jwt-认证机制)
  - [2.4 统一响应包装 Result&lt;T&gt;](#24-统一响应包装-resultt)
  - [2.5 权限码规范](#25-权限码规范)
  - [2.6 限流与幂等](#26-限流与幂等)
  - [2.7 字段加密与脱敏](#27-字段加密与脱敏)
  - [2.8 统一错误响应](#28-统一错误响应)
  - [2.9 公共请求头](#29-公共请求头)
  - [2.10 分页规范](#210-分页规范)
  - [2.11 时间与时区](#211-时间与时区)
- [3. 接口详细定义](#3-接口详细定义)
  - [3.1 认证模块](#31-认证模块)
  - [3.2 系统管理模块](#32-系统管理模块)
  - [3.3 项目管理模块](#33-项目管理模块)
  - [3.4 实施管理模块](#34-实施管理模块)
  - [3.5 交付件管理模块](#35-交付件管理模块)
  - [3.6 资产管理模块](#36-资产管理模块)
  - [3.7 基线管理模块](#37-基线管理模块)
  - [3.8 工作流模块](#38-工作流模块)
  - [3.9 治理模块](#39-治理模块)
  - [3.10 通知模块](#310-通知模块)
  - [3.11 文件模块](#311-文件模块)
  - [3.12 外部集成模块](#312-外部集成模块)
  - [3.13 低代码模块](#313-低代码模块)
  - [3.14 聚合报表模块](#314-聚合报表模块)
- [4. 数据模型](#4-数据模型)
  - [4.1 通用模型](#41-通用模型)
  - [4.2 认证与系统模型](#42-认证与系统模型)
  - [4.3 项目域模型](#43-项目域模型)
  - [4.4 实施域模型](#44-实施域模型)
  - [4.5 交付件域模型](#45-交付件域模型)
  - [4.6 资产域模型](#46-资产域模型)
  - [4.7 基线与依赖模型](#47-基线与依赖模型)
  - [4.8 工作流与审批模型](#48-工作流与审批模型)
  - [4.9 治理域模型](#49-治理域模型)
  - [4.10 通知与文件模型](#410-通知与文件模型)
  - [4.11 集成与低代码模型](#411-集成与低代码模型)
- [5. 错误码总表](#5-错误码总表)
  - [5.1 HTTP 状态码](#51-http-状态码)
  - [5.2 业务错误码](#52-业务错误码)
  - [5.3 异常类型映射](#53-异常类型映射)
- [6. 版本兼容性策略](#6-版本兼容性策略)
  - [6.1 版本号规则](#61-版本号规则)
  - [6.2 兼容性原则](#62-兼容性原则)
  - [6.3 破坏性变更流程](#63-破坏性变更流程)
  - [6.4 弃用流程](#64-弃用流程)
  - [6.5 向后兼容清单](#65-向后兼容清单)

---

## 1. 引言

### 1.1 编写目的

本文档定义网络设备工程项目管理系统（PMS，Project Management System）对外的 RESTful API 接口规范，旨在：

1. **统一前后端契约**：作为后端工程师实现 Controller 与前端工程师封装 `src/api/*` 的共同约定，避免双方对端点路径、方法、参数、响应结构的理解偏差。
2. **指导第三方集成**：为 D365/FP/OA 等外部系统对接、移动端、低代码运行时渲染器以及其他下游消费方提供权威的接口参考。
3. **约束版本演进**：明确兼容性策略、弃用流程与破坏性变更门槛，保障平台长周期可维护性。
4. **沉淀通用规范**：将鉴权、限流、幂等、分页、错误码、字段加密等横切关注点收敛为统一约定，避免各模块各自为政。

### 1.2 读者对象

| 角色 | 关注重点 |
|------|----------|
| 后端开发工程师 | 端点定义、权限码、错误码、状态机、Service 边界 |
| 前端开发工程师 | 请求/响应 JSON 结构、字段命名、枚举值、分页协议 |
| 测试工程师 | 状态码、错误码、幂等键、限流阈值、边界场景用例 |
| 系统集成工程师 | JWT 握手、外部集成回调、Webhook 签名 |
| 运维工程师 | 健康检查端点、Actuator 暴露、限流配置 |
| 架构师 | SPI 解耦、模块依赖、版本演进策略 |

### 1.3 文档范围

本文档覆盖 `network-equipment-pms` 平台全部 14 个后端 Maven 模块对外暴露的 RESTful API，包括：

- **认证与系统管理**（`pms-system`）：登录登出、用户/角色/菜单/字典/配置/审计/缓存/定时任务
- **项目域**（`pms-project`）：项目/阶段/模板/里程碑/终验/Punch List
- **实施域**（`pms-implementation`）：任务/进度/结算/服务商/评分/活动/评论/清单
- **资产域**（`pms-asset`）：资产/分类/型号/调拨/RMA/质保
- **交付件域**（`pms-deliverable`）：7 态全生命周期、版本、签名、引用
- **基线域**（`pms-baseline`）：基线快照、任务依赖、偏差分析
- **工作流域**（`pms-workflow`）：Flowable 引擎、统一审批中心、字段脱敏
- **治理域**（`pms-governance`）：变更请求、风险登记册、问题日志
- **通知域**（`pms-notification`）：站内信、WebSocket、模板
- **文件域**（`pms-file`）：上传/下载/缩略图/业务附件
- **集成域**（`pms-integration`）：D365/FP/OA 适配、健康检查、日志重试
- **低代码域**（`pms-lowcode`）：实体/表单/列表/微流/规则/连接器/触发器/流程绑定/版本/发布
- **聚合域**（`pms-admin`）：跨模块报表、仪表盘、引用实体聚合查询

文档共定义 **80+ 个端点**，覆盖 14 个业务模块，每个端点提供方法、路径、权限码、请求参数、请求体示例、响应示例、错误码与业务说明。

### 1.4 术语与缩写

| 术语 | 全称 | 说明 |
|------|------|------|
| PMS | Project Management System | 网络设备工程项目管理系统 |
| JWT | JSON Web Token | 无状态认证令牌 |
| RBAC | Role-Based Access Control | 基于角色的访问控制 |
| SPI | Service Provider Interface | 服务提供者接口（跨模块解耦） |
| PPDIOO | Prepare/Plan/Design/Implement/Operate/Optimize | Cisco 网络生命周期方法论 |
| CCB | Change Control Board | 变更控制委员会 |
| BPMN | Business Process Model and Notation | 业务流程建模标记法 |
| RMA | Return Merchandise Authorization | 退货授权 |
| OCR | Optical Character Recognition | 光学字符识别 |
| D365 | Microsoft Dynamics 365 | 微软 ERP 系统 |
| FP | Financial Platform | 财务平台 |
| OA | Office Automation | 致远协同办公 |
| SLA | Service Level Agreement | 服务等级协议 |
| DAG | Directed Acyclic Graph | 有向无环图 |
| DFS | Depth-First Search | 深度优先搜索 |
| STOMP | Simple Text Oriented Messaging Protocol | 简单文本消息协议 |

### 1.5 参考标准

- RFC 7231 — HTTP/1.1 Semantics and Content
- RFC 7519 — JSON Web Token (JWT)
- RFC 7807 — Problem Details for HTTP APIs（错误响应参考）
- OpenAPI Specification 3.0（Swagger）
- PMBOK 第 7 版 — 项目管理知识体系指南
- ISO/IEC 25010 — 软件产品质量模型

---

## 2. 通用规范

### 2.1 基础 URL 与版本管理

**生产环境**：`https://pms.example.com/api`
**测试环境**：`https://pms-test.example.com/api`
**开发环境**：`http://localhost:8080/api`

所有 API 路径均以 `/api` 为根前缀。版本管理采用 **URL 路径前缀策略**：

- 当前版本：`/api/{module}/...`（默认 v1，不显式标注）
- 未来版本：`/api/v2/{module}/...`（仅在破坏性变更时启用）

> **约定**：v1 版本默认不显式出现在 URL 中；当出现破坏性变更时引入 v2，并保留 v1 至少 6 个月的弃用过渡期。

### 2.2 HTTP 方法约定

平台严格遵循 RESTful 语义，方法与操作对应关系如下：

| HTTP 方法 | 语义 | 幂等性 | 安全性 | 典型场景 |
|-----------|------|--------|--------|----------|
| `GET` | 查询资源 | 是 | 是 | 列表、详情、导出 |
| `POST` | 创建资源 / 触发动作 | 否 | 否 | 创建、状态机推进、审批提交 |
| `PUT` | 全量更新资源 | 是 | 否 | 整体替换 |
| `DELETE` | 删除资源 | 是 | 否 | 逻辑删除 |
| `PATCH` | 部分更新 | 否 | 否 | 单字段更新（平台暂未广泛使用） |

**动作型端点约定**：状态机推进、审批操作等"动词型"操作采用 `POST /{resource}/{id}/{action}` 模式，例如：

- `POST /api/project/{id}/approve` — 审批通过项目
- `POST /api/governance/change-request/{id}/submit` — 提交 CCB 审批
- `POST /api/implementation/task/{id}/accept` — 任务受理

### 2.3 JWT 认证机制

#### 2.3.1 认证流程

```
┌─────────┐   1. POST /api/auth/login (username, password)
│  Client │ ────────────────────────────────────────────────► ┌──────────┐
│         │                                                   │   PMS    │
│         │   2. 200 OK { token: "xxx", userInfo: {...} }     │  Server  │
│         │ ◄──────────────────────────────────────────────── │          │
│         │                                                   │          │
│         │   3. GET /api/project (Authorization: Bearer xxx) │          │
│         │ ────────────────────────────────────────────────► │          │
│         │                                                   │          │
│         │   4. 200 OK [...]                                 │          │
│         │ ◄──────────────────────────────────────────────── └──────────┘
└─────────┘
```

#### 2.3.2 JWT 结构

JWT 采用 HS256 算法签名，三段式结构：

- **Header**：`{ "alg": "HS256", "typ": "JWT" }`
- **Payload**：`{ "sub": "<userId>", "username": "<loginName>", "roles": [...], "iat": <签发时间>, "exp": <过期时间> }`
- **Signature**：`HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), <jwt.secret>)`

**配置项**：
- `jwt.secret`：Base64 编码的签名密钥（生产环境必须通过 `JWT_SECRET` 环境变量覆盖）
- `jwt.expiration`：86400000 毫秒（24 小时）

#### 2.3.3 Token 传递

所有需认证的接口必须在请求头携带：

```
Authorization: Bearer <token>
```

**例外**：
- `/api/auth/login` — 登录端点本身
- `/api/auth/logout` — 登出（需 token）
- `/api/system/dict/items/{dictType}` — 字典公开查询
- `/api/system/help-content/public/**` — 帮助中心公开内容
- `/actuator/health` — 健康检查端点
- `/swagger-ui/**`、`/v3/api-docs/**` — Swagger 文档

#### 2.3.4 Token 失效与黑名单

- **过期失效**：`exp` 字段过期后服务端拒绝，返回 `401 Unauthorized` + `code=401`
- **主动登出**：`POST /api/auth/logout` 将当前 token 加入 Redis 黑名单，剩余有效期内不可再用
- **密码修改**：管理员重置用户密码后，该用户已签发 token 立即失效

#### 2.3.5 WebSocket 握手鉴权

WebSocket 连接（`/ws` 端点）的 JWT 握手支持两种方式：

1. **请求头**（推荐）：`Authorization: Bearer <token>`
2. **查询参数**（兼容原生 WebSocket 客户端）：`ws://host:8080/ws?token=<token>`

握手拦截器 `JwtHandshakeInterceptor` 解析 token 取得 `userId`，存入 STOMP 会话属性。缺 token 或解析失败拒绝握手。

### 2.4 统一响应包装 Result&lt;T&gt;

所有业务接口（除文件下载、健康检查、Actuator 原生端点外）统一返回 `Result<T>` 包装结构。

#### 2.4.1 Result&lt;T&gt; 结构

```typescript
interface Result<T> {
  code: number;        // 业务码：200=成功，其他=失败
  message: string;     // 提示信息（成功为 "操作成功"，失败为具体原因）
  data: T | null;      // 业务数据载荷，失败时为 null
  timestamp: number;   // 服务端响应时间戳（毫秒）
}
```

#### 2.4.2 成功响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1001,
    "projectCode": "PRJ-2026-0001",
    "projectName": "华东区5G核心网升级"
  },
  "timestamp": 1753132800000
}
```

#### 2.4.3 失败响应示例

```json
{
  "code": 400,
  "message": "项目名称不能为空",
  "data": null,
  "timestamp": 1753132800000
}
```

#### 2.4.4 ResultCode 枚举

| 枚举值 | code | message | HTTP 状态 |
|--------|------|---------|-----------|
| `SUCCESS` | 200 | 操作成功 | 200 |
| `BAD_REQUEST` | 400 | 请求参数错误 | 400 |
| `UNAUTHORIZED` | 401 | 未认证或认证已过期 | 401 |
| `FORBIDDEN` | 403 | 无访问权限 | 403 |
| `NOT_FOUND` | 404 | 资源不存在 | 404 |
| `METHOD_NOT_ALLOWED` | 405 | 请求方法不支持 | 405 |
| `REQUEST_TIMEOUT` | 408 | 请求超时 | 408 |
| `CONFLICT` | 409 | 资源冲突 | 409 |
| `TOO_MANY_REQUESTS` | 429 | 请求过于频繁 | 429 |
| `INTERNAL_ERROR` | 500 | 服务器内部错误 | 500 |
| `SERVICE_UNAVAILABLE` | 503 | 服务暂不可用 | 503 |
| `BUSINESS_ERROR` | 1001 | 业务校验失败 | 200 |
| `INTEGRATION_ERROR` | 1002 | 外部集成调用失败 | 200 |
| `RATE_LIMIT_EXCEEDED` | 1003 | 接口限流 | 429 |
| `IDEIMPOTENT_CONFLICT` | 1004 | 幂等键冲突 | 409 |
| `OPTIMISTIC_LOCK_FAILURE` | 1005 | 数据已被他人修改 | 409 |
| `CYCLE_DETECTED` | 1006 | 检测到循环依赖 | 400 |

> **约定**：`code=200` 仅为业务成功的统一码；HTTP 状态码独立遵循 RFC 7231。例如参数校验失败时 HTTP=400、code=400；业务规则违反时 HTTP=200、code=1001。

### 2.5 权限码规范

#### 2.5.1 权限码格式

权限码采用三段式命名约定：`module:resource:action`

- **module**：模块标识，与 Maven artifactId 一致，如 `system`、`project`、`asset`、`governance`
- **resource**：资源标识，与 Controller 类对应，如 `user`、`role`、`changeRequest`
- **action**：操作动作，统一取值：`list` / `add` / `edit` / `remove` / `process` / `clear` / `push` / `sync` / `export`

#### 2.5.2 权限码示例

| 权限码 | 含义 |
|--------|------|
| `system:user:list` | 查询用户列表 |
| `system:user:add` | 新增用户 |
| `system:user:edit` | 编辑用户 |
| `system:user:remove` | 删除用户 |
| `system:role:process` | 处理角色（分配权限） |
| `system:cache:clear` | 清空缓存 |
| `project:project:add` | 新增项目 |
| `project:project:process` | 项目状态推进 |
| `asset:asset:edit` | 编辑资产 |
| `governance:changeRequest:process` | 变更请求审批处理 |
| `integration:d365:push` | D365 推送 |
| `integration:d365:sync` | D365 同步 |
| `lowcode:form:edit` | 编辑表单配置 |
| `lowcode:data:{entityCode}:create` | 低代码动态实体创建（SpEL 动态拼接） |

#### 2.5.3 注解使用

后端通过 Spring Security `@PreAuthorize` 注解强制校验：

```java
@PreAuthorize("hasAuthority('project:project:add')")
@PostMapping
public Result<Project> create(@Valid @RequestBody Project project) { ... }

@PreAuthorize("hasAuthority('governance:changeRequest:process')")
@PostMapping("/{id}/approve")
public Result<ChangeRequest> approve(@PathVariable Long id, @RequestParam String approverName) { ... }
```

#### 2.5.4 超级管理员

`permissions` 数组包含 `*` 通配符的用户视为超级管理员，拥有全部权限，`hasPermission('*')` 直接返回 `true`。

### 2.6 限流与幂等

#### 2.6.1 限流（@RateLimit）

基于 Bucket4j 实现，注解 `@RateLimit` 标注在 Controller 方法上：

```java
@RateLimit(key = "auth:login", capacity = 5, refillTokens = 5, refillDuration = 60)
@PostMapping("/login")
public Result<LoginResult> login(@Valid @RequestBody LoginParams params) { ... }
```

- `key`：限流键，支持 SpEL（如 `#params.username`）
- `capacity`：令牌桶容量（突发上限）
- `refillTokens`：每 `refillDuration` 秒补充令牌数
- `refillDuration`：补充周期（秒）

**触发限流时**：HTTP 429，响应体：

```json
{
  "code": 429,
  "message": "请求过于频繁，请稍后再试",
  "data": null,
  "timestamp": 1753132800000
}
```

#### 2.6.2 幂等（@Idempotent）

针对写操作（创建订单、提交审批、推送外部系统等），通过 `@Idempotent` 注解 + Redis SETNX 实现幂等保护：

```java
@Idempotent
@PostMapping
public Result<ChangeRequest> create(@Valid @RequestBody ChangeRequest cr) { ... }
```

**机制**：
1. 客户端在写操作请求头携带 `X-Idempotent-Key: <UUID v4>`
2. 服务端 `IdempotentAspect` 拦截，以 `idempotent:{className}:{methodName}:{key}` 为 Redis key
3. SETNX 成功 → 执行业务并缓存响应（默认 10 分钟）
4. SETNX 失败 → 返回缓存的首次响应（`code=1004` 标识为幂等命中）

**前端约定**：前端 `request.ts` 拦截器对 POST/PUT/DELETE/PATCH 自动注入 `X-Idempotent-Key`（UUID v4）。

### 2.7 字段加密与脱敏

#### 2.7.1 字段加密（@FieldEncrypt）

敏感字段（用户手机号、身份证号、银行账号等）在数据库中 AES-256-GCM 加密存储，应用层透明加解密：

```java
@FieldEncrypt
private String phoneNumber;
```

- 加密密钥：`app.security.encrypt-key` 配置项
- 算法：AES-256-GCM
- 透明性：写入数据库前自动加密，读取后自动解密；API 响应已解密

#### 2.7.2 字段脱敏

审批场景下的字段脱敏由 `ApprovalFieldPermission` 表配置，三态权限：

| 权限值 | 行为 |
|--------|------|
| `VISIBLE` | 原值返回 |
| `MASKED` | 按 `maskPattern` 脱敏后返回 |
| `HIDDEN` | 字段不出现在响应中（返回 null） |

**预置脱敏规则**：

| maskPattern | 示例输入 | 示例输出 |
|-------------|----------|----------|
| `phone-mask` | 13812345678 | 138****5678 |
| `amount-mask` | 12345.67 | ****.67 |
| `email-mask` | user@example.com | u***@example.com |
| `custom` | — | 按 `customPattern` 正则 |

### 2.8 统一错误响应

#### 2.8.1 异常处理链

平台通过 `GlobalExceptionHandler`（`@RestControllerAdvice`）统一捕获异常并转换为 `Result<Void>`：

| 异常类型 | HTTP 状态 | code | message 来源 |
|----------|-----------|------|--------------|
| `BusinessException` | 200 | 1001 | `e.getMessage()` |
| `IntegrationException` | 200 | 1002 | `e.getMessage()` |
| `RateLimitExceededException` | 429 | 1003 | "请求过于频繁，请稍后再试" |
| `IdempotentConflictException` | 409 | 1004 | "幂等键冲突，请勿重复提交" |
| `OptimisticLockingFailureException` | 409 | 1005 | "数据已被他人修改，请刷新后重试" |
| `CycleDetectedException` | 400 | 1006 | "检测到循环依赖：" + 路径 |
| `MethodArgumentNotValidException` | 400 | 400 | 字段校验错误聚合 |
| `ConstraintViolationException` | 400 | 400 | 参数校验错误 |
| `HttpMessageNotReadableException` | 400 | 400 | "请求体格式错误" |
| `HttpRequestMethodNotSupportedException` | 405 | 405 | "请求方法不支持" |
| `AccessDeniedException` | 403 | 403 | "无访问权限" |
| `AuthenticationException` | 401 | 401 | "未认证或认证已过期" |
| `NoHandlerFoundException` | 404 | 404 | "资源不存在" |
| `Exception`（兜底） | 500 | 500 | "服务器内部错误" |

#### 2.8.2 参数校验错误示例

```json
{
  "code": 400,
  "message": "项目名称不能为空; 项目编号长度必须在5-50之间; 计划开始时间不能为空",
  "data": null,
  "timestamp": 1753132800000
}
```

### 2.9 公共请求头

| 请求头 | 必填 | 说明 |
|--------|------|------|
| `Authorization` | 是（除公开端点） | `Bearer <JWT>` |
| `X-Idempotent-Key` | 写操作必填 | UUID v4，幂等键 |
| `X-Trace-Id` | 否 | 链路追踪 ID，缺省服务端生成 |
| `Content-Type` | POST/PUT 必填 | `application/json;charset=UTF-8` 或 `multipart/form-data` |
| `Accept` | 否 | 默认 `application/json` |
| `Accept-Language` | 否 | 国际化（当前仅支持 `zh-CN`） |

### 2.10 分页规范

#### 2.10.1 请求参数

分页查询统一使用以下查询参数：

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `page` | int | 1 | 页码（从 1 开始） |
| `size` | int | 10 | 每页条数（最大 200） |
| `sort` | string | — | 排序字段，格式 `field,asc` 或 `field,desc` |

#### 2.10.2 响应结构

分页响应统一采用 MyBatis-Plus `IPage<T>` 结构：

```typescript
interface IPage<T> {
  records: T[];      // 当前页数据
  total: number;     // 总记录数
  size: number;      // 每页条数
  current: number;   // 当前页码
  pages: number;     // 总页数
}
```

**示例**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      { "id": 1, "projectCode": "PRJ-2026-0001" },
      { "id": 2, "projectCode": "PRJ-2026-0002" }
    ],
    "total": 156,
    "size": 10,
    "current": 1,
    "pages": 16
  },
  "timestamp": 1753132800000
}
```

### 2.11 时间与时区

- **传输格式**：ISO 8601 字符串，例如 `2026-07-22T10:30:00`
- **日期**：`yyyy-MM-dd`，例如 `2026-07-22`
- **时区**：服务端统一 Asia/Shanghai（UTC+8），序列化时不含时区后缀
- **时间戳**：毫秒级 Unix 时间戳，例如 `1753132800000`

---

## 3. 接口详细定义

### 3.1 认证模块

#### 3.1.1 用户登录

**端点**：`POST /api/auth/login`

**权限**：无（公开端点）

**限流**：`@RateLimit(capacity=5, refillTokens=5, refillDuration=60)` — 每分钟 5 次

**请求体**：

```json
{
  "username": "admin",
  "password": "encrypted_password_here",
  "captcha": "ABCD",
  "captchaKey": "uuid-from-captcha-api"
}
```

**响应体**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "nickname": "系统管理员",
      "email": "admin@example.com",
      "phoneNumber": "13812345678",
      "avatar": "/avatar/admin.png",
      "deptId": 1,
      "deptName": "研发中心"
    },
    "permissions": ["*", "system:user:list", "project:project:add"]
  },
  "timestamp": 1753132800000
}
```

**错误码**：

| code | message | 场景 |
|------|---------|------|
| 400 | 用户名或密码不能为空 | 参数缺失 |
| 400 | 验证码不能为空 | 未传验证码 |
| 401 | 用户名或密码错误 | 凭据错误 |
| 401 | 验证码错误或已过期 | 验证码校验失败 |
| 401 | 账号已被禁用 | 用户 status=0 |
| 429 | 请求过于频繁，请稍后再试 | 触发限流 |

#### 3.1.2 用户登出

**端点**：`POST /api/auth/logout`

**权限**：需登录

**请求**：无请求体，从 `Authorization` 头解析当前 token

**响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null,
  "timestamp": 1753132800000
}
```

**行为**：将当前 token 加入 Redis 黑名单（剩余有效期），客户端应清除本地 token。

#### 3.1.3 获取当前用户信息

**端点**：`GET /api/auth/info`

**权限**：需登录

**响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "系统管理员",
    "email": "admin@example.com",
    "phoneNumber": "13812345678",
    "avatar": "/avatar/admin.png",
    "deptId": 1,
    "deptName": "研发中心",
    "roles": ["admin", "project_manager"],
    "permissions": ["*", "system:user:list"]
  },
  "timestamp": 1753132800000
}
```

**用途**：前端刷新页面后从 token 恢复用户信息与权限码（`userStore.fetchUserInfo()`）。

### 3.2 系统管理模块

#### 3.2.1 用户管理

##### 3.2.1.1 分页查询用户

**端点**：`GET /api/system/user/page`

**权限**：`system:user:list`

**查询参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `page` | int | 否 | 页码，默认 1 |
| `size` | int | 否 | 每页条数，默认 10 |
| `username` | string | 否 | 用户名模糊查询 |
| `nickname` | string | 否 | 昵称模糊查询 |
| `deptId` | long | 否 | 部门 ID |
| `status` | int | 否 | 状态（0=禁用，1=启用） |

**响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "username": "admin",
        "nickname": "系统管理员",
        "email": "admin@example.com",
        "phoneNumber": "13812345678",
        "deptId": 1,
        "deptName": "研发中心",
        "status": 1,
        "createTime": "2026-01-01T00:00:00"
      }
    ],
    "total": 56,
    "size": 10,
    "current": 1,
    "pages": 6
  },
  "timestamp": 1753132800000
}
```

##### 3.2.1.2 新增用户

**端点**：`POST /api/system/user`

**权限**：`system:user:add`

**幂等**：`@Idempotent`

**请求体**：

```json
{
  "username": "zhangsan",
  "nickname": "张三",
  "email": "zhangsan@example.com",
  "phoneNumber": "13912345678",
  "password": "encrypted_password",
  "deptId": 2,
  "roleIds": [3, 5],
  "status": 1
}
```

**响应**：返回创建后的用户对象（含 id）。

**错误码**：

| code | message | 场景 |
|------|---------|------|
| 400 | 用户名不能为空 | username 缺失 |
| 400 | 用户名长度必须在4-20之间 | 长度越界 |
| 400 | 密码不能为空 | password 缺失 |
| 409 | 用户名已存在 | username 唯一约束冲突 |

##### 3.2.1.3 编辑用户

**端点**：`PUT /api/system/user`

**权限**：`system:user:edit`

**请求体**：同新增，但 `id` 必填，`password` 可选（为空则不修改）。

##### 3.2.1.4 删除用户

**端点**：`DELETE /api/system/user/{id}`

**权限**：`system:user:remove`

**行为**：逻辑删除（`deleted=1`）。

##### 3.2.1.5 重置密码

**端点**：`POST /api/system/user/{id}/reset-password`

**权限**：`system:user:process`

**请求体**：

```json
{
  "newPassword": "encrypted_new_password"
}
```

**行为**：重置密码并使该用户已签发 token 失效。

#### 3.2.2 角色管理

##### 3.2.2.1 分页查询角色

**端点**：`GET /api/system/role/page`

**权限**：`system:role:list`

**查询参数**：`page` / `size` / `roleName` / `roleKey` / `status`

**响应**：返回 `IPage<SysRole>`，每条记录含 `id` / `roleName` / `roleKey` / `status` / `createTime`。

##### 3.2.2.2 新增/编辑/删除角色

**端点**：`POST /api/system/role`、`PUT /api/system/role`、`DELETE /api/system/role/{id}`

**权限**：`system:role:add` / `system:role:edit` / `system:role:remove`

##### 3.2.2.3 分配角色权限

**端点**：`POST /api/system/role/{id}/permissions`

**权限**：`system:role:process`

**请求体**：

```json
{
  "menuIds": [1, 2, 3, 10, 11],
  "permissionCodes": ["system:user:list", "project:project:add"]
}
```

#### 3.2.3 菜单管理

##### 3.2.3.1 查询菜单树

**端点**：`GET /api/system/menu/tree`

**权限**：`system:menu:list`

**响应**：返回嵌套树形结构，每个节点含 `id` / `menuName` / `path` / `icon` / `perms` / `menuType`（M=目录/C=菜单/F=按钮）/ `children`。

##### 3.2.3.2 新增/编辑/删除菜单

**端点**：`POST /api/system/menu`、`PUT /api/system/menu`、`DELETE /api/system/menu/{id}`

**权限**：`system:menu:add` / `system:menu:edit` / `system:menu:remove`

#### 3.2.4 字典管理

##### 3.2.4.1 分页查询字典

**端点**：`GET /api/system/dict/page`

**权限**：`system:dict:list`

##### 3.2.4.2 查询字典项（公开）

**端点**：`GET /api/system/dict/items/{dictType}`

**权限**：无（公开端点，前端启动时加载常用字典）

**路径参数**：`dictType` — 字典类型编码，如 `pms_deliverable_type`、`pms_project_status`、`pms_task_priority`

**响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    { "dictType": "pms_deliverable_type", "itemValue": "DOCUMENT", "itemText": "文档", "sortOrder": 1 },
    { "dictType": "pms_deliverable_type", "itemValue": "CODE", "itemText": "代码", "sortOrder": 2 },
    { "dictType": "pms_deliverable_type", "itemValue": "ENTITY_REF", "itemText": "实体引用", "sortOrder": 3 }
  ],
  "timestamp": 1753132800000
}
```

**缓存**：服务端使用 Redis 命名缓存 `sysDict`（TTL 60min），前端模块级缓存 + 异常降级到兜底常量。

##### 3.2.4.3 新增/编辑/删除字典

**端点**：`POST /api/system/dict`、`PUT /api/system/dict`、`DELETE /api/system/dict/{id}`

**权限**：`system:dict:add` / `system:dict:edit` / `system:dict:remove`

#### 3.2.5 系统配置管理

##### 3.2.5.1 查询配置列表

**端点**：`GET /api/system/config/page`

**权限**：`system:config:list`

##### 3.2.5.2 按 key 查询配置

**端点**：`GET /api/system/config/{configKey}`

**权限**：`system:config:list`

**响应**：返回 `SysConfig` 对象（含 `configKey` / `configValue` / `configType` / `remark`）。

##### 3.2.5.3 新增/编辑/删除配置

**端点**：`POST /api/system/config`、`PUT /api/system/config`、`DELETE /api/system/config/{id}`

**权限**：`system:config:add` / `system:config:edit` / `system:config:remove`

#### 3.2.6 缓存管理

##### 3.2.6.1 查询缓存 keys

**端点**：`GET /api/system/cache/keys`

**权限**：`system:cache:list`

**查询参数**：`pattern` — key 模式（如 `sysDict:*`）

##### 3.2.6.2 清空缓存

**端点**：`DELETE /api/system/cache`

**权限**：`system:cache:clear`

**查询参数**：`pattern` — 缓存模式（缺省清空全部业务缓存）

**响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { "cleared": 156 },
  "timestamp": 1753132800000
}
```

#### 3.2.7 审计日志

##### 3.2.7.1 查询操作日志

**端点**：`GET /api/system/audit/oper/page`

**权限**：`system:audit:list`

**查询参数**：`page` / `size` / `title` / `businessType` / `operName` / `startTime` / `endTime`

**响应**：返回 `IPage<SysOperLog>`，含 `title` / `businessType`（1=新增/2=修改/3=删除）/ `operName` / `operUrl` / `operIp` / `operTime` / `costTime` / `jsonResult`。

##### 3.2.7.2 查询登录日志

**端点**：`GET /api/system/audit/login/page`

**权限**：`system:audit:list`

#### 3.2.8 定时任务管理

##### 3.2.8.1 查询任务列表

**端点**：`GET /api/system/schedule/page`

**权限**：`system:schedule:list`

**响应**：返回 `IPage<SysJob>`，含 `jobName` / `jobGroup` / `cronExpression` / `status`（0=正常/1=暂停）。

##### 3.2.8.2 暂停/恢复/执行任务

**端点**：`POST /api/system/schedule/{id}/pause`、`POST /api/system/schedule/{id}/resume`、`POST /api/system/schedule/{id}/run`

**权限**：`system:schedule:process`

#### 3.2.9 用户反馈与帮助中心

##### 3.2.9.1 提交反馈

**端点**：`POST /api/system/feedback`

**权限**：需登录

**请求体**：

```json
{
  "feedbackType": "BUG",
  "title": "项目列表分页异常",
  "content": "点击第 3 页时数据不刷新",
  "contact": "zhangsan@example.com"
}
```

##### 3.2.9.2 查询帮助内容（公开）

**端点**：`GET /api/system/help-content/public/{category}`

**权限**：无（公开端点）

**路径参数**：`category` — 帮助分类，如 `getting-started`、`faq`、`user-guide`

### 3.3 项目管理模块

#### 3.3.1 项目 CRUD

##### 3.3.1.1 创建项目

**端点**：`POST /api/project`

**权限**：`project:project:add`

**幂等**：`@Idempotent`

**请求体**：

```json
{
  "projectCode": "PRJ-2026-0001",
  "projectName": "华东区5G核心网升级",
  "projectType": "NETWORK_UPGRADE",
  "customerId": 100,
  "customerName": "中国移动华东分公司",
  "projectManagerId": 5,
  "projectManagerName": "李四",
  "planStartDate": "2026-08-01",
  "planEndDate": "2026-12-31",
  "budget": 5000000,
  "description": "5G核心网全量升级改造",
  "parentId": null,
  "templateId": null
}
```

**响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1001,
    "projectCode": "PRJ-2026-0001",
    "projectName": "华东区5G核心网升级",
    "status": "PENDING",
    "projectPath": "/1001",
    "createTime": "2026-07-22T10:30:00"
  },
  "timestamp": 1753132800000
}
```

**错误码**：

| code | message | 场景 |
|------|---------|------|
| 400 | 项目编号不能为空 | projectCode 缺失 |
| 400 | 项目名称长度必须在5-100之间 | 长度越界 |
| 409 | 项目编号已存在 | projectCode 唯一约束冲突 |
| 1001 | 模板不存在 | templateId 无效 |
| 1001 | 父项目不存在 | parentId 无效 |

##### 3.3.1.2 查询项目详情

**端点**：`GET /api/project/{id}`

**权限**：`project:project:list`

**响应**：返回完整 `Project` 对象，含基本信息、状态、阶段、进度等。

##### 3.3.1.3 更新项目

**端点**：`PUT /api/project`

**权限**：`project:project:edit`

##### 3.3.1.4 删除项目

**端点**：`DELETE /api/project/{id}`

**权限**：`project:project:remove`

**行为**：逻辑删除。仅允许删除 `PENDING` / `REJECTED` / `CANCELLED` 状态的项目。

**错误码**：

| code | message | 场景 |
|------|---------|------|
| 1001 | 项目状态不允许删除 | 状态非终态 |
| 1001 | 项目存在子项目，无法删除 | 含未删除子项目 |

##### 3.3.1.5 分页查询项目

**端点**：`GET /api/project/page`

**权限**：`project:project:list`

**查询参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `page` / `size` | int | 分页 |
| `projectName` | string | 模糊查询 |
| `projectCode` | string | 精确查询 |
| `status` | string | 状态过滤 |
| `projectManagerId` | long | 项目经理过滤 |
| `customerId` | long | 客户过滤 |
| `planStartDateFrom` / `planStartDateTo` | date | 计划开始区间 |
| `planEndDateFrom` / `planEndDateTo` | date | 计划结束区间 |

##### 3.3.1.6 项目看板

**端点**：`GET /api/project/dashboard`

**权限**：`project:project:list`

**响应**：按状态分组返回项目计数：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "PENDING": 5,
    "APPROVED": 12,
    "PLANNING": 8,
    "IN_PROGRESS": 23,
    "INITIAL_ACCEPTANCE": 4,
    "FINAL_ACCEPTANCE": 2,
    "CLOSING": 1,
    "COMPLETED": 56,
    "CLOSED": 89,
    "CANCELLED": 3,
    "REJECTED": 1
  },
  "timestamp": 1753132800000
}
```

##### 3.3.1.7 项目树

**端点**：`GET /api/project/tree`

**权限**：`project:project:list`

**响应**：返回主子项目嵌套树，基于 `projectPath` 物化路径构建。

##### 3.3.1.8 子项目列表

**端点**：`GET /api/project/{id}/subproject`

**权限**：`project:project:list`

##### 3.3.1.9 审批项目

**端点**：`POST /api/project/{id}/approve`

**权限**：`project:project:process`

**请求体**：

```json
{
  "approved": true,
  "opinion": "同意立项",
  "nextApproverId": 10
}
```

**状态流转**：`PENDING` → `APPROVED`（通过）或 `REJECTED`（驳回）。审批通过后启动 `projectApproval` BPMN 流程。

##### 3.3.1.10 关闭项目

**端点**：`POST /api/project/{id}/close`

**权限**：`project:project:process`

**前置条件**：项目状态为 `CLOSING`。

##### 3.3.1.11 取消项目

**端点**：`POST /api/project/{id}/cancel`

**权限**：`project:project:process`

**请求体**：

```json
{
  "cancelReason": "客户取消订单"
}
```

##### 3.3.1.12 查询项目进度

**端点**：`GET /api/project/{id}/progress`

**权限**：`project:project:list`

**响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "projectId": 1001,
    "projectName": "华东区5G核心网升级",
    "overallProgress": 65,
    "phaseProgress": [
      { "phaseId": 1, "phaseName": "Prepare", "status": "COMPLETED", "progress": 100 },
      { "phaseId": 2, "phaseName": "Plan", "status": "COMPLETED", "progress": 100 },
      { "phaseId": 3, "phaseName": "Design", "status": "IN_PROGRESS", "progress": 80 },
      { "phaseId": 4, "phaseName": "Implement", "status": "PENDING", "progress": 0 },
      { "phaseId": 5, "phaseName": "Operate", "status": "PENDING", "progress": 0 }
    ],
    "taskProgress": {
      "total": 45,
      "completed": 28,
      "inProgress": 12,
      "pending": 5
    },
    "deliverableProgress": {
      "total": 18,
      "published": 10,
      "draft": 5,
      "submitted": 3
    }
  },
  "timestamp": 1753132800000
}
```

#### 3.3.2 项目阶段

##### 3.3.2.1 查询项目阶段列表

**端点**：`GET /api/project/phase/project/{projectId}`

**权限**：`project:phase:list`

**响应**：返回 PPDIOO 5 阶段列表，每个阶段含 `id` / `phaseName` / `phaseType`（PREPARE/PLAN/DESIGN/IMPLEMENT/OPERATE）/ `status`（PENDING/IN_PROGRESS/COMPLETED）/ `startDate` / `endDate` / `exitGatePassed`。

##### 3.3.2.2 推进阶段

**端点**：`POST /api/project/phase/{phaseId}/advance`

**权限**：`project:phase:process`

**行为**：触发阶段退出闸门校验。4 类条件全部满足方可推进：
- **DELIVERABLE** — 阶段强制交付件全部 PUBLISHED
- **MILESTONE** — 阶段里程碑全部 COMPLETED
- **TASK** — 阶段任务全部 CONFIRMED
- **APPROVAL** — 阶段退出审批通过

**错误码**：

| code | message | 场景 |
|------|---------|------|
| 1001 | 阶段强制交付件未全部发布 | DELIVERABLE 闸门未通过 |
| 1001 | 阶段里程碑未全部完成 | MILESTONE 闸门未通过 |
| 1001 | 阶段任务未全部确认 | TASK 闸门未通过 |
| 1001 | 阶段退出审批未通过 | APPROVAL 闸门未通过 |

#### 3.3.3 项目模板

##### 3.3.3.1 查询模板列表

**端点**：`GET /api/project/template/page`

**权限**：`project:template:list`

##### 3.3.3.2 创建模板

**端点**：`POST /api/project/template`

**权限**：`project:template:add`

**请求体**：

```json
{
  "templateCode": "TPL-5G-UPGRADE",
  "templateName": "5G网络升级模板",
  "templateType": "NETWORK_UPGRADE",
  "description": "5G核心网升级标准模板",
  "phases": [
    { "phaseType": "PREPARE", "phaseName": "准备阶段", "defaultDays": 7 },
    { "phaseType": "PLAN", "phaseName": "规划阶段", "defaultDays": 14 }
  ],
  "milestones": [
    { "milestoneName": "立项评审", "nodeType": "PROJECT_APPROVAL", "defaultDay": 7 }
  ],
  "deliverables": [
    { "name": "项目章程", "natureType": "DOCUMENT", "mandatory": true }
  ]
}
```

##### 3.3.3.3 发布模板

**端点**：`POST /api/project/template/{id}/publish`

**权限**：`project:template:process`

##### 3.3.3.4 从模板创建项目

**端点**：`POST /api/project/template/{id}/instantiate`

**权限**：`project:project:add`

**请求体**：

```json
{
  "projectCode": "PRJ-2026-0099",
  "projectName": "华北区5G升级",
  "customerId": 101,
  "projectManagerId": 6,
  "planStartDate": "2026-09-01"
}
```

**行为**：模板深拷贝 12 步流程，复制阶段、里程碑、交付件配置到新项目。

#### 3.3.4 里程碑

##### 3.3.4.1 查询项目里程碑

**端点**：`GET /api/project/milestone/project/{projectId}`

**权限**：`project:milestone:list`

**响应**：返回 12 节点里程碑列表，含 `milestoneName` / `nodeType` / `planDate` / `actualDate` / `status`（PENDING/IN_PROGRESS/COMPLETED/OVERDUE/CANCELLED）。

##### 3.3.4.2 更新里程碑

**端点**：`PUT /api/project/milestone`

**权限**：`project:milestone:edit`

##### 3.3.4.3 完成里程碑

**端点**：`POST /api/project/milestone/{id}/complete`

**权限**：`project:milestone:process`

**请求体**：

```json
{
  "actualDate": "2026-08-15",
  "remark": "里程碑按期完成"
}
```

#### 3.3.5 终验管理

##### 3.3.5.1 提交终验

**端点**：`POST /api/project/acceptance/{projectId}/submit`

**权限**：`project:acceptance:process`

**行为**：项目状态 `INITIAL_ACCEPTANCE` → `FINAL_ACCEPTANCE`，启动 `finalAcceptance` BPMN 流程。

##### 3.3.5.2 终验通过

**端点**：`POST /api/project/acceptance/{id}/approve`

**权限**：`project:acceptance:process`

#### 3.3.6 Punch List（尾项清单）

##### 3.3.6.1 查询 Punch List

**端点**：`GET /api/project/punch-list/project/{projectId}`

**权限**：`project:punchList:list`

##### 3.3.6.2 创建 Punch 项

**端点**：`POST /api/project/punch-list`

**权限**：`project:punchList:add`

**请求体**：

```json
{
  "projectId": 1001,
  "punchItemName": "机房标签缺失",
  "description": "B栋3楼机房部分设备标签缺失",
  "responsibleId": 8,
  "responsibleName": "王五",
  "deadline": "2026-09-15",
  "priority": "HIGH"
}
```

##### 3.3.6.3 关闭 Punch 项

**端点**：`POST /api/project/punch-list/{id}/close`

**权限**：`project:punchList:process`

### 3.4 实施管理模块

#### 3.4.1 任务管理

##### 3.4.1.1 分页查询任务

**端点**：`GET /api/implementation/task/page`

**权限**：`implementation:task:list`

**查询参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `page` / `size` | int | 分页 |
| `projectId` | long | 项目过滤 |
| `assigneeId` | long | 处理人过滤 |
| `status` | string | 状态过滤（PENDING/ACCEPTED/IN_PROGRESS/REVIEW/COMPLETED/CONFIRMED/REJECTED） |
| `taskType` | string | 任务类型（OEM/AGENT） |
| `priority` | string | 优先级（LOW/MEDIUM/HIGH/URGENT） |
| `planEndDateFrom` / `planEndDateTo` | date | 计划完成区间 |

##### 3.4.1.2 查询任务树

**端点**：`GET /api/implementation/task/tree/{projectId}`

**权限**：`implementation:task:list`

**响应**：返回嵌套任务树，基于 `taskPath` 物化路径构建，含 `taskPath` / `depth` 字段。

##### 3.4.1.3 创建任务

**端点**：`POST /api/implementation/task`

**权限**：`implementation:task:add`

**幂等**：`@Idempotent`

**请求体**：

```json
{
  "projectId": 1001,
  "parentId": null,
  "taskName": "核心网设备安装",
  "taskType": "OEM",
  "assigneeId": 8,
  "assigneeName": "王五",
  "agentId": null,
  "planStartDate": "2026-09-01",
  "planEndDate": "2026-09-15",
  "priority": "HIGH",
  "description": "5G核心网设备现场安装",
  "estimatedHours": 80,
  "checklist": [
    { "itemName": "机房环境检查", "required": true },
    { "itemName": "设备到货核对", "required": true }
  ]
}
```

##### 3.4.1.4 任务详情

**端点**：`GET /api/implementation/task/{id}`

**权限**：`implementation:task:list`

##### 3.4.1.5 更新任务

**端点**：`PUT /api/implementation/task`

**权限**：`implementation:task:edit`

##### 3.4.1.6 删除任务

**端点**：`DELETE /api/implementation/task/{id}`

**权限**：`implementation:task:remove`

##### 3.4.1.7 受理任务

**端点**：`POST /api/implementation/task/{id}/accept`

**权限**：`implementation:task:process`

**状态流转**：`PENDING` → `ACCEPTED`

##### 3.4.1.8 开始任务

**端点**：`POST /api/implementation/task/{id}/start`

**权限**：`implementation:task:process`

**状态流转**：`ACCEPTED` → `IN_PROGRESS`

##### 3.4.1.9 提交任务审核

**端点**：`POST /api/implementation/task/{id}/submit-review`

**权限**：`implementation:task:process`

**状态流转**：`IN_PROGRESS` → `REVIEW`

**前置校验**：强制检查项（`required=true`）必须全部完成，否则抛 `TaskChecklistRequiredException`。

##### 3.4.1.10 审核任务

**端点**：`POST /api/implementation/task/{id}/review`

**权限**：`implementation:task:process`

**请求体**：

```json
{
  "passed": true,
  "opinion": "工作完成符合要求"
}
```

**状态流转**：`REVIEW` → `COMPLETED`（通过）或 `IN_PROGRESS`（驳回）

##### 3.4.1.11 确认任务

**端点**：`POST /api/implementation/task/{id}/confirm`

**权限**：`implementation:task:process`

**状态流转**：`COMPLETED` → `CONFIRMED`

##### 3.4.1.12 转派任务

**端点**：`POST /api/implementation/task/{id}/delegate`

**权限**：`implementation:task:process`

**请求体**：

```json
{
  "newAssigneeId": 12,
  "newAssigneeName": "赵六",
  "reason": "原处理人请假"
}
```

#### 3.4.2 任务进度

##### 3.4.2.1 上报进度

**端点**：`POST /api/impl/progress`

**权限**：`implementation:progress:add`

**请求体**：

```json
{
  "taskId": 5001,
  "progressPercent": 60,
  "actualHours": 48,
  "remainingHours": 32,
  "description": "已完成主设备安装，正在进行调测",
  "reportDate": "2026-09-10"
}
```

##### 3.4.2.2 查询进度历史

**端点**：`GET /api/impl/progress/task/{taskId}`

**权限**：`implementation:progress:list`

#### 3.4.3 结算管理

##### 3.4.3.1 创建结算单

**端点**：`POST /api/impl/settlement`

**权限**：`settlement:settlement:add`

**请求体**：

```json
{
  "settlementNo": "SET-2026-0001",
  "projectId": 1001,
  "agentId": 5,
  "agentName": "华东集成服务商",
  "settlementPeriod": "2026-09",
  "totalAmount": 250000.00,
  "details": [
    { "taskId": 5001, "taskName": "核心网设备安装", "amount": 100000.00 },
    { "taskId": 5002, "taskName": "传输网改造", "amount": 150000.00 }
  ]
}
```

##### 3.4.3.2 提交结算审批

**端点**：`POST /api/impl/settlement/{id}/submit`

**权限**：`settlement:settlement:process`

**行为**：启动 `settlementApproval` BPMN 流程 + Saga 6 步流程：
1. 创建结算单
2. 任务工作量校验
3. 财务审核
4. 推送 FP 财务平台
5. 同步发票
6. 关闭结算单

##### 3.4.3.3 推送 FP

**端点**：`POST /api/integration/fp/push-settlement`

**权限**：`integration:fp:push`

详见 [3.12 外部集成模块](#312-外部集成模块)。

#### 3.4.4 服务商管理

##### 3.4.4.1 查询服务商列表

**端点**：`GET /api/impl/agent/page`

**权限**：`implementation:agent:list`

##### 3.4.4.2 新增服务商

**端点**：`POST /api/impl/agent`

**权限**：`implementation:agent:add`

##### 3.4.4.3 服务商评分

**端点**：`POST /api/impl/agent/{id}/score`

**权限**：`implementation:agent:process`

**请求体**：

```json
{
  "projectId": 1001,
  "taskId": 5001,
  "qualityScore": 90,
  "scheduleScore": 85,
  "serviceScore": 88,
  "overallScore": 87.67,
  "comment": "整体表现良好，进度略滞后"
}
```

### 3.5 交付件管理模块

#### 3.5.1 交付件 CRUD

##### 3.5.1.1 分页查询交付件

**端点**：`GET /api/deliverable/page`

**权限**：`deliverable:deliverable:list`

**查询参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `page` / `size` | int | 分页 |
| `projectId` | long | 项目过滤 |
| `phaseId` | long | 阶段过滤 |
| `natureType` | string | 性质分类（DOCUMENT/CODE/ENTITY_REF/MODEL/CONFIG/DATA/OTHER） |
| `status` | string | 状态过滤 |
| `mandatory` | boolean | 是否强制交付件 |
| `ownerId` | long | 责任人过滤 |

##### 3.5.1.2 创建交付件

**端点**：`POST /api/deliverable`

**权限**：`deliverable:deliverable:add`

**请求体**：

```json
{
  "projectId": 1001,
  "phaseId": 3,
  "name": "网络设计说明书",
  "natureType": "DOCUMENT",
  "description": "5G核心网设计文档",
  "ownerId": 8,
  "ownerName": "王五",
  "mandatory": true,
  "templateId": null,
  "refEntityType": null,
  "refEntityId": null
}
```

**状态**：默认 `DRAFT`。

##### 3.5.1.3 交付件详情

**端点**：`GET /api/deliverable/{id}`

**权限**：`deliverable:deliverable:list`

**响应**：含基本信息、当前版本、签名列表、引用关系、状态流转历史。

##### 3.5.1.4 更新交付件

**端点**：`PUT /api/deliverable`

**权限**：`deliverable:deliverable:edit`

##### 3.5.1.5 删除交付件

**端点**：`DELETE /api/deliverable/{id}`

**权限**：`deliverable:deliverable:remove`

**前置条件**：仅允许删除 `DRAFT` 状态。

#### 3.5.2 交付件状态机

交付件 7 态状态机：`DRAFT → SUBMITTED → REVIEWED → SIGNED → PUBLISHED → REFERENCED → ARCHIVED`

##### 3.5.2.1 提交交付件

**端点**：`POST /api/deliverable/{id}/submit`

**权限**：`deliverable:deliverable:process`

**状态流转**：`DRAFT` → `SUBMITTED`

##### 3.5.2.2 审核交付件

**端点**：`POST /api/deliverable/{id}/review`

**权限**：`deliverable:deliverable:process`

**请求体**：

```json
{
  "passed": true,
  "opinion": "文档质量符合要求"
}
```

**状态流转**：`SUBMITTED` → `REVIEWED`（通过）或 `DRAFT`（驳回）

##### 3.5.2.3 签署交付件

**端点**：`POST /api/deliverable/{id}/sign`

**权限**：`deliverable:deliverable:process`

**请求体**：

```json
{
  "signType": "ELECTRONIC",
  "signerId": 10,
  "signerName": "客户代表",
  "comment": "客户电子签名确认"
}
```

**状态流转**：`REVIEWED` → `SIGNED`

##### 3.5.2.4 发布交付件

**端点**：`POST /api/deliverable/{id}/publish`

**权限**：`deliverable:deliverable:process`

**状态流转**：`SIGNED` → `PUBLISHED`

##### 3.5.2.5 归档交付件

**端点**：`POST /api/deliverable/{id}/archive`

**权限**：`deliverable:deliverable:process`

**状态流转**：`PUBLISHED` 或 `REFERENCED` → `ARCHIVED`

#### 3.5.3 交付件版本管理

##### 3.5.3.1 创建新版本

**端点**：`POST /api/deliverable/{id}/revise`

**权限**：`deliverable:deliverable:edit`

**行为**：新建版本号（不可变历史保留），当前版本变为 `DRAFT`。

##### 3.5.3.2 查询版本历史

**端点**：`GET /api/deliverable/{id}/versions`

**权限**：`deliverable:deliverable:list`

##### 3.5.3.3 回滚到指定版本

**端点**：`POST /api/deliverable/{id}/versions/{versionId}/rollback`

**权限**：`deliverable:deliverable:process`

#### 3.5.4 交付件引用

##### 3.5.4.1 添加引用

**端点**：`POST /api/deliverable/{id}/references`

**权限**：`deliverable:deliverable:edit`

**请求体**：

```json
{
  "refEntityType": "TASK",
  "refEntityId": 5001,
  "refEntityName": "核心网设备安装",
  "relationType": "OUTPUT"
}
```

##### 3.5.4.2 查询引用列表

**端点**：`GET /api/deliverable/{id}/references`

**权限**：`deliverable:deliverable:list`

#### 3.5.5 阶段强制交付件校验

**端点**：`POST /api/deliverable/validate-mandatory/{phaseId}`

**权限**：`deliverable:deliverable:list`

**行为**：校验指定阶段的强制交付件是否全部 `PUBLISHED`，供阶段退出闸门调用。

### 3.6 资产管理模块

#### 3.6.1 资产 CRUD

##### 3.6.1.1 分页查询资产

**端点**：`GET /api/asset/page`

**权限**：`asset:asset:list`

**查询参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `page` / `size` | int | 分页 |
| `assetCode` | string | 资产编码模糊 |
| `assetName` | string | 资产名称模糊 |
| `categoryId` | long | 分类过滤 |
| `modelId` | long | 型号过滤 |
| `status` | string | 状态过滤（ORDERED/IN_TRANSIT/RECEIVED/STAGED/INSTALLED/COMMISSIONED/IN_PRODUCTION/RMA/DECOMMISSIONED） |
| `projectId` | long | 所属项目过滤 |
| `locationId` | long | 库位过滤 |
| `serialNo` | string | 序列号精确 |

##### 3.6.1.2 创建资产

**端点**：`POST /api/asset`

**权限**：`asset:asset:add`

**请求体**：

```json
{
  "assetCode": "AST-2026-00001",
  "assetName": "华为5G核心网交换机",
  "categoryId": 10,
  "categoryName": "核心交换机",
  "modelId": 50,
  "modelName": "Huawei CE12800",
  "serialNo": "SN20260801001",
  "status": "ORDERED",
  "projectId": 1001,
  "locationId": null,
  "purchaseDate": "2026-07-15",
  "purchasePrice": 280000.00,
  "supplierId": 20,
  "supplierName": "华为技术有限公司",
  "warrantyStartDate": null,
  "warrantyEndDate": null,
  "remark": "5G核心网升级主设备"
}
```

##### 3.6.1.3 资产详情

**端点**：`GET /api/asset/{id}`

**权限**：`asset:asset:list`

##### 3.6.1.4 更新资产

**端点**：`PUT /api/asset`

**权限**：`asset:asset:edit`

##### 3.6.1.5 删除资产

**端点**：`DELETE /api/asset/{id}`

**权限**：`asset:asset:remove`

#### 3.6.2 资产状态机

资产 9 态状态机：`ORDERED → IN_TRANSIT → RECEIVED → STAGED → INSTALLED → COMMISSIONED → IN_PRODUCTION`，旁路 `RMA` / `DECOMMISSIONED`。

##### 3.6.2.1 推进资产状态

**端点**：`POST /api/asset/{id}/advance`

**权限**：`asset:asset:process`

**请求体**：

```json
{
  "targetStatus": "INSTALLED",
  "locationId": 30,
  "installedDate": "2026-09-10",
  "remark": "设备已就位安装"
}
```

#### 3.6.3 资产调拨

##### 3.6.3.1 创建调拨申请

**端点**：`POST /api/asset/transfer`

**权限**：`asset:transfer:add`

**请求体**：

```json
{
  "assetId": 2001,
  "assetCode": "AST-2026-00001",
  "assetName": "华为5G核心网交换机",
  "fromProjectId": 1001,
  "fromProjectName": "华东区5G核心网升级",
  "toProjectId": 1002,
  "toProjectName": "华北区5G核心网升级",
  "reason": "项目间设备调剂",
  "applyDate": "2026-09-01"
}
```

**行为**：启动 `assetTransfer` BPMN 流程（源 PM 审核 → 目标 PM 审核）。

##### 3.6.3.2 查询调拨列表

**端点**：`GET /api/asset/transfer/page`

**权限**：`asset:transfer:list`

#### 3.6.4 RMA 管理

RMA 6 步闭环：申请 → 审批 → 发货 → 收货 → 维修返回 → 验收入库

##### 3.6.4.1 创建 RMA

**端点**：`POST /api/asset/rma`

**权限**：`asset:rma:add`

**请求体**：

```json
{
  "assetId": 2001,
  "assetCode": "AST-2026-00001",
  "rmaReason": "设备电源模块故障",
  "faultDescription": "上电后电源模块异响，无法正常启动",
  "priority": "HIGH",
  "applyDate": "2026-09-15",
  "photos": [
    { "fileId": 100, "fileName": "fault_photo_1.jpg" }
  ]
}
```

##### 3.6.4.2 RMA 状态推进

**端点**：`POST /api/asset/rma/{id}/advance`

**权限**：`asset:rma:process`

**请求体**：

```json
{
  "targetStatus": "SHIPPED",
  "trackingNo": "SF1234567890",
  "shippedDate": "2026-09-16",
  "remark": "已发往供应商维修中心"
}
```

#### 3.6.5 质保管理

##### 3.6.5.1 查询质保列表

**端点**：`GET /api/asset/warranty/page`

**权限**：`asset:warranty:list`

**查询参数**：`page` / `size` / `assetId` / `expireInDays`（即将到期天数过滤）

##### 3.6.5.2 质保到期预警

**端点**：`GET /api/asset/warranty/expiring`

**权限**：`asset:warranty:list`

**查询参数**：`days` — 即将到期天数（默认 30/60/90）

**响应**：返回即将到期质保列表，按到期日升序。

### 3.7 基线管理模块

#### 3.7.1 基线快照

##### 3.7.1.1 查询基线列表

**端点**：`GET /api/baseline/list`

**权限**：`baseline:baseline:list`

**查询参数**：`projectId`

##### 3.7.1.2 保存基线快照

**端点**：`POST /api/baseline/save`

**权限**：`baseline:baseline:add`

**请求体**：

```json
{
  "projectId": 1001,
  "baselineName": "基线 v1.0",
  "description": "项目计划首次冻结",
  "snapshotData": {
    "tasks": [
      { "taskId": 5001, "taskName": "核心网设备安装", "planStartDate": "2026-09-01", "planEndDate": "2026-09-15", "duration": 15 }
    ]
  }
}
```

##### 3.7.1.3 基线详情

**端点**：`GET /api/baseline/{id}`

**权限**：`baseline:baseline:list`

##### 3.7.1.4 请求基线变更

**端点**：`POST /api/baseline/{id}/request-change`

**权限**：`baseline:baseline:process`

**行为**：创建变更请求并启动 `changeRequestApproval` BPMN 流程。

##### 3.7.1.5 基线偏差分析

**端点**：`GET /api/baseline/{id}/diff`

**权限**：`baseline:baseline:list`

**响应**：逐任务对比基线 vs 当前计划，三阈值监控：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "baselineId": 501,
    "projectId": 1001,
    "thresholds": {
      "daysThreshold": 5,
      "percentThreshold": 10,
      "countThreshold": 3
    },
    "overallStatus": "WARNING",
    "taskDiffs": [
      {
        "taskId": 5001,
        "taskName": "核心网设备安装",
        "baselineStart": "2026-09-01",
        "baselineEnd": "2026-09-15",
        "currentStart": "2026-09-01",
        "currentEnd": "2026-09-22",
        "daysVariance": 7,
        "percentVariance": 46.67,
        "status": "OVERRUN"
      }
    ],
    "triggeredThresholds": ["DAYS", "PERCENT"]
  },
  "timestamp": 1753132800000
}
```

#### 3.7.2 任务依赖

##### 3.7.2.1 查询任务依赖

**端点**：`GET /api/implementation/task/dependency/task/{taskId}`

**权限**：`implementation:task:list`

##### 3.7.2.2 创建任务依赖

**端点**：`POST /api/implementation/task/dependency`

**权限**：`implementation:task:edit`

**请求体**：

```json
{
  "predecessorTaskId": 5001,
  "successorTaskId": 5002,
  "dependencyType": "FS",
  "lagDays": 0
}
```

**dependencyType**：`FS`（完成-开始）/ `FF`（完成-完成）/ `SS`（开始-开始）/ `SF`（开始-完成）

**行为**：DFS 增量循环检测，发现环抛 `CycleDetectedException`。

**错误码**：

| code | message | 场景 |
|------|---------|------|
| 1006 | 检测到循环依赖：5001 → 5002 → 5001 | 依赖形成环 |

##### 3.7.2.3 删除任务依赖

**端点**：`DELETE /api/implementation/task/dependency/{id}`

**权限**：`implementation:task:edit`

### 3.8 工作流模块

#### 3.8.1 Flowable 工作流

##### 3.8.1.1 部署流程定义

**端点**：`POST /api/workflow/deploy`

**权限**：`workflow:workflow:process`

**请求体**：`multipart/form-data`，含 BPMN XML 文件 + 流程名称

##### 3.8.1.2 查询流程定义列表

**端点**：`GET /api/workflow/definitions`

**权限**：`workflow:workflow:list`

##### 3.8.1.3 启动流程实例

**端点**：`POST /api/workflow/start`

**权限**：`workflow:workflow:process`

**请求体**：

```json
{
  "processDefinitionKey": "projectApproval",
  "businessKey": "PRJ-2026-0001",
  "variables": {
    "projectId": 1001,
    "projectName": "华东区5G核心网升级",
    "initiatorId": 5
  }
}
```

##### 3.8.1.4 查询待办任务

**端点**：`GET /api/workflow/todo`

**权限**：`workflow:workflow:list`

**查询参数**：`page` / `size`

##### 3.8.1.5 完成任务

**端点**：`POST /api/workflow/complete-task`

**权限**：`workflow:workflow:process`

**请求体**：

```json
{
  "taskId": "task-uuid-xxx",
  "approved": true,
  "comment": "同意",
  "variables": {
    "nextApproverId": 10
  }
}
```

##### 3.8.1.6 查询流程图

**端点**：`GET /api/workflow/diagram/{processInstanceId}`

**权限**：`workflow:workflow:list`

**响应**：返回 SVG/PNG 图片流（`Content-Type: image/svg+xml`）

##### 3.8.1.7 查询流程历史

**端点**：`GET /api/workflow/history/{processInstanceId}`

**权限**：`workflow:workflow:list`

#### 3.8.2 统一审批中心

##### 3.8.2.1 提交审批

**端点**：`POST /api/workflow/approval/submit`

**权限**：`workflow:approval:process`

**请求体**：

```json
{
  "approvalType": "PROJECT",
  "businessId": 1001,
  "businessCode": "PRJ-2026-0001",
  "projectId": 1001,
  "title": "项目立项审批 - 华东区5G核心网升级",
  "submitterId": 5,
  "submitterName": "李四",
  "nodes": [
    { "nodeName": "PM审核", "nodeOrder": 1, "approverId": 8 },
    { "nodeName": "部门经理审核", "nodeOrder": 2, "approverId": 10 }
  ],
  "timeoutAt": "2026-08-05T18:00:00"
}
```

**approvalType**：`PROJECT` / `TASK` / `DELIVERABLE` / `RISK` / `ISSUE` / `CHANGE` / `RESOURCE` / `COST` / `PHASE_EXIT` / `BASELINE_CHANGE`

##### 3.8.2.2 审批通过

**端点**：`POST /api/workflow/approval/{recordId}/approve`

**权限**：`workflow:approval:process`

**请求体**：

```json
{
  "opinion": "同意项目立项"
}
```

##### 3.8.2.3 审批退回

**端点**：`POST /api/workflow/approval/{recordId}/reject`

**权限**：`workflow:approval:process`

**请求体**：

```json
{
  "opinion": "预算不合理，请重新评估"
}
```

**行为**：审批退回后重新提交复用原记录，`round+1`。

##### 3.8.2.4 撤回审批

**端点**：`POST /api/workflow/approval/{recordId}/withdraw`

**权限**：`workflow:approval:process`

##### 3.8.2.5 查询审批详情

**端点**：`GET /api/workflow/approval/{recordId}`

**权限**：`workflow:approval:list`

**响应**：含审批记录、当前节点、历史轨迹（按 `round` 分组）、脱敏字段元数据。

##### 3.8.2.6 查询待办审批

**端点**：`GET /api/workflow/approval/todo`

**权限**：`workflow:approval:list`

**查询参数**：`page` / `size` / `approvalType` / `projectId`

##### 3.8.2.7 查询我提交的审批

**端点**：`GET /api/workflow/approval/submitted`

**权限**：`workflow:approval:list`

##### 3.8.2.8 审批统计

**端点**：`GET /api/workflow/approval/statistics`

**权限**：`workflow:approval:list`

**响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "pending": 12,
    "approved": 156,
    "rejected": 8,
    "withdrawn": 3,
    "timeout": 2,
    "byType": {
      "PROJECT": 50,
      "TASK": 80,
      "DELIVERABLE": 30,
      "CHANGE": 15,
      "PHASE_EXIT": 6
    }
  },
  "timestamp": 1753132800000
}
```

#### 3.8.3 字段权限配置

##### 3.8.3.1 查询字段权限

**端点**：`GET /api/workflow/field-perm/page`

**权限**：`workflow:fieldPerm:list`

##### 3.8.3.2 配置字段权限

**端点**：`POST /api/workflow/field-perm`

**权限**：`workflow:fieldPerm:add`

**请求体**：

```json
{
  "approvalNodeId": 200,
  "entityType": "Deliverable",
  "fieldName": "amount",
  "permission": "MASKED",
  "maskPattern": "amount-mask"
}
```

##### 3.8.3.3 更新字段权限

**端点**：`PUT /api/workflow/field-perm`

**权限**：`workflow:fieldPerm:edit`

##### 3.8.3.4 删除字段权限

**端点**：`DELETE /api/workflow/field-perm/{id}`

**权限**：`workflow:fieldPerm:remove`

### 3.9 治理模块

#### 3.9.1 变更请求

##### 3.9.1.1 创建变更请求

**端点**：`POST /api/governance/change-request`

**权限**：`governance:changeRequest:add`

**幂等**：`@Idempotent`

**请求体**：

```json
{
  "projectId": 1001,
  "projectName": "华东区5G核心网升级",
  "title": "增加传输网扩容范围",
  "description": "客户要求在原范围基础上增加3个站点的传输网扩容",
  "requesterId": 5,
  "requesterName": "李四",
  "impactScope": "新增3个站点的传输设备安装与调测",
  "impactSchedule": "项目周期预计延长15天",
  "impactCost": "预算增加约30万元",
  "impactQuality": "不影响核心网质量目标",
  "priority": "HIGH"
}
```

**响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 8001,
    "crNo": "CR-2026-0001",
    "status": "SUBMITTED",
    "priority": "HIGH",
    "requestDate": "2026-07-22",
    "baselineUpdated": false
  },
  "timestamp": 1753132800000
}
```

##### 3.9.1.2 提交 CCB 审批

**端点**：`POST /api/governance/change-request/{id}/submit`

**权限**：`governance:changeRequest:process`

**行为**：状态 `SUBMITTED` → `UNDER_REVIEW`，启动 `changeRequestApproval` BPMN 流程。

##### 3.9.1.3 CCB 审批通过

**端点**：`POST /api/governance/change-request/{id}/approve`

**权限**：`governance:changeRequest:process`

**查询参数**：`approverName` — 审批人姓名

**行为**：
1. 状态 `UNDER_REVIEW` → `CCB_APPROVED`
2. 记录三维度基线变更审计（SCHEDULE/COST/SCOPE）
3. `baselineUpdated=true`
4. 完成工作流审核任务

##### 3.9.1.4 CCB 驳回

**端点**：`POST /api/governance/change-request/{id}/reject`

**权限**：`governance:changeRequest:process`

**查询参数**：`reason` — 驳回原因

##### 3.9.1.5 开始实施

**端点**：`POST /api/governance/change-request/{id}/implement`

**权限**：`governance:changeRequest:process`

**状态流转**：`CCB_APPROVED` → `IMPLEMENTING`

##### 3.9.1.6 关闭变更请求

**端点**：`POST /api/governance/change-request/{id}/close`

**权限**：`governance:changeRequest:process`

##### 3.9.1.7 查询变更请求列表

**端点**：`GET /api/governance/change-request`

**权限**：`governance:changeRequest:list`

##### 3.9.1.8 按项目查询变更请求

**端点**：`GET /api/governance/change-request/project/{projectId}`

**权限**：`governance:changeRequest:list`

#### 3.9.2 风险登记册

##### 3.9.2.1 创建风险

**端点**：`POST /api/governance/risk`

**权限**：`governance:risk:add`

**请求体**：

```json
{
  "projectId": 1001,
  "description": "供应商设备到货延迟风险",
  "category": "EXTERNAL",
  "likelihood": 4,
  "impact": 5,
  "mitigation": "MITIGATE",
  "contingencyPlan": "启用备选供应商，缩短采购周期",
  "ownerId": 8,
  "ownerName": "王五",
  "reviewDate": "2026-08-15"
}
```

**响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 9001,
    "riskNo": "RISK-2026-0001",
    "score": 20,
    "priority": "HIGH",
    "status": "OPEN",
    "identifiedAt": "2026-07-22T10:30:00"
  },
  "timestamp": 1753132800000
}
```

> **注**：`score` = `likelihood * impact`，`priority` 由评分自动分档（1-6=LOW / 7-12=MEDIUM / 13-25=HIGH）。

##### 3.9.2.2 风险已发生（转化为问题）

**端点**：`POST /api/governance/risk/{id}/mark-occurred`

**权限**：`governance:risk:process`

**行为**：
1. 创建新 `Issue`（`description` 拼接为 `"由风险 RISK-2026-0001 转化: <原描述>"`）
2. 风险状态置 `CLOSED`，填充 `closedAt`

##### 3.9.2.3 风险升级为变更请求

**端点**：`POST /api/governance/risk/{id}/escalate`

**权限**：`governance:risk:process`

**行为**：
1. 创建新 `ChangeRequest`（标题 `"由RISK-2026-0001升级的变更请求"`）
2. 风险状态置 `ESCALATED`

##### 3.9.2.4 查询风险矩阵

**端点**：`GET /api/governance/risk/matrix`

**权限**：`governance:risk:list`

**查询参数**：`projectId`（可选，缺省查全部）

**响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "matrix": [
      [0, 0, 1, 0, 2],
      [0, 1, 0, 1, 0],
      [0, 0, 0, 2, 1],
      [0, 0, 1, 0, 3],
      [0, 0, 0, 1, 4]
    ],
    "risks": [...],
    "totalRisks": 18,
    "highPriorityCount": 7
  },
  "timestamp": 1753132800000
}
```

> `matrix[likelihood-1][impact-1]` 为该象限风险数量。

#### 3.9.3 问题日志

##### 3.9.3.1 创建问题

**端点**：`POST /api/governance/issue`

**权限**：`governance:issue:add`

**请求体**：

```json
{
  "projectId": 1001,
  "description": "机房环境不满足设备安装要求",
  "raisedBy": 8,
  "raisedByName": "王五",
  "priority": "HIGH",
  "targetResolveDate": "2026-08-15"
}
```

**响应**：自动生成 `issueNo`（格式 `ISSUE-2026-XXXX`），缺省 `targetResolveDate=今天+7天`。

##### 3.9.3.2 分配问题处理人

**端点**：`POST /api/governance/issue/{id}/assign`

**权限**：`governance:issue:process`

**查询参数**：`assigneeId` / `assigneeName`

**行为**：`OPEN` 状态自动转 `IN_PROGRESS`。

##### 3.9.3.3 解决问题

**端点**：`POST /api/governance/issue/{id}/resolve`

**权限**：`governance:issue:process`

**查询参数**：`resolution` — 解决方案

##### 3.9.3.4 关闭问题

**端点**：`POST /api/governance/issue/{id}/close`

**权限**：`governance:issue:process`

##### 3.9.3.5 问题升级为变更请求

**端点**：`POST /api/governance/issue/{id}/escalate`

**权限**：`governance:issue:process`

### 3.10 通知模块

#### 3.10.1 站内通知

##### 3.10.1.1 分页查询通知

**端点**：`GET /api/notification/page`

**权限**：需登录

**查询参数**：`page` / `size` / `category` / `readStatus`

**category**：`MILESTONE` / `TASK` / `APPROVAL` / `PUNCH_LIST` / `WARRANTY` / `RMA` / `SETTLEMENT`

**响应**：返回当前用户的通知列表（按 `createdAt` 倒序）。

##### 3.10.1.2 查询未读数

**端点**：`GET /api/notification/unread/count`

**权限**：需登录

**响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": 8,
  "timestamp": 1753132800000
}
```

##### 3.10.1.3 标记单条已读

**端点**：`PUT /api/notification/{id}/read`

**权限**：需登录

##### 3.10.1.4 全部标记已读

**端点**：`PUT /api/notification/read/all`

**权限**：需登录

##### 3.10.1.5 手动发送通知（管理员调试）

**端点**：`POST /api/notification/send`

**权限**：`notification:notification:send`

**请求体**：

```json
{
  "userId": 5,
  "title": "系统维护通知",
  "content": "系统将于今晚22:00-24:00进行维护升级",
  "category": "SYSTEM",
  "channels": ["IN_APP", "WS", "EMAIL"]
}
```

**channel**：`IN_APP` / `WS` / `EMAIL` / `OA`

#### 3.10.2 通知模板

##### 3.10.2.1 查询模板列表

**端点**：`GET /api/notification/template/page`

**权限**：`notification:template:list`

##### 3.10.2.2 查询模板详情

**端点**：`GET /api/notification/template/{id}`

**权限**：`notification:template:list`

##### 3.10.2.3 按编码查询模板

**端点**：`GET /api/notification/template/code/{templateCode}`

**权限**：`notification:template:list`

##### 3.10.2.4 新增/编辑/删除模板

**端点**：`POST /api/notification/template`、`PUT /api/notification/template`、`DELETE /api/notification/template/{id}`

**权限**：`notification:template:add` / `notification:template:edit` / `notification:template:remove`

**预置模板（12 个）**：`MILESTONE_OVERDUE` / `TASK_ASSIGNED` / `TASK_DELEGATED` / `APPROVAL_TODO` / `PUNCH_LIST_DEADLINE` / `WARRANTY_EXPIRE_90` / `WARRANTY_EXPIRE_60` / `WARRANTY_EXPIRE_30` / `RMA_STATUS_CHANGE` / `SETTLEMENT_APPROVED` / `CHANGE_REQUEST_CCB` / `RISK_ESCALATED`

#### 3.10.3 WebSocket 实时推送

**端点**：`ws(s)://host:8080/ws`

**握手**：携带 JWT（`Authorization: Bearer <token>` 或 `?token=<token>`）

**订阅频道**：`/topic/notification/{userId}`

**消息格式**：

```json
{
  "id": 5001,
  "userId": 5,
  "title": "任务分派通知",
  "content": "您有新任务「核心网设备安装」待处理，计划完成日 2026-09-15",
  "category": "TASK",
  "bizType": "TASK_ASSIGNED",
  "bizId": 5001,
  "readStatus": "UNREAD",
  "channel": "WS",
  "createdAt": "2026-07-22T10:30:00"
}
```

### 3.11 文件模块

#### 3.11.1 文件上传

**端点**：`POST /api/file/upload`

**权限**：需登录

**请求**：`multipart/form-data`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `file` | File | 是 | 文件 |
| `bizType` | string | 否 | 业务类型 |
| `bizId` | long | 否 | 业务 ID |
| `category` | string | 否 | 文件分类 |

**响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 100,
    "fileName": "design_doc.pdf",
    "filePath": "/2026/07/22/uuid-design_doc.pdf",
    "fileSize": 1048576,
    "fileType": "application/pdf",
    "md5": "d41d8cd98f00b204e9800998ecf8427e",
    "exifGps": "31.2304,121.4737",
    "geoFenceStatus": "INSIDE",
    "uploadTime": "2026-07-22T10:30:00"
  },
  "timestamp": 1753132800000
}
```

> 图片上传时自动提取 EXIF GPS 信息并校验地理围栏。

#### 3.11.2 文件详情

**端点**：`GET /api/file/{id}`

**权限**：需登录

#### 3.11.3 文件下载

**端点**：`GET /api/file/{id}/download`

**权限**：需登录

**响应**：直接返回文件流（`Content-Type: application/octet-stream`），不经过 `Result<T>` 包装。

#### 3.11.4 缩略图

**端点**：`GET /api/file/{id}/thumbnail`

**权限**：需登录

**响应**：图片缩略图流（`Content-Type: image/jpeg`）

#### 3.11.5 业务文件列表

**端点**：`GET /api/file/biz`

**权限**：需登录

**查询参数**：`bizType` / `bizId`

### 3.12 外部集成模块

#### 3.12.1 D365 集成

##### 3.12.1.1 D365 健康检查

**端点**：`GET /api/integration/d365/health`

**权限**：需登录

**响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "connected": true,
    "tokenValid": true,
    "recentPushCount": 23,
    "recentFailCount": 1,
    "recentLogs": [...]
  },
  "timestamp": 1753132800000
}
```

##### 3.12.1.2 推送采购收货

**端点**：`POST /api/integration/d365/push-receipt`

**权限**：`integration:d365:push`

**请求体**：`PurchaseReceiptHeader` 结构

##### 3.12.1.3 同步采购单

**端点**：`POST /api/integration/d365/sync/purchase-orders`

**权限**：`integration:d365:sync`

##### 3.12.1.4 同步采购收货

**端点**：`POST /api/integration/d365/sync/purchase-receipts`

**权限**：`integration:d365:sync`

##### 3.12.1.5 同步资产序列号

**端点**：`POST /api/integration/d365/sync/asset-serial-numbers`

**权限**：`integration:d365:sync`

##### 3.12.1.6 同步发票

**端点**：`POST /api/integration/d365/sync/invoices`

**权限**：`integration:d365:sync`

#### 3.12.2 FP 集成

##### 3.12.2.1 FP 健康检查

**端点**：`GET /api/integration/fp/health`

**权限**：需登录

##### 3.12.2.2 推送结算单

**端点**：`POST /api/integration/fp/push-settlement`

**权限**：`integration:fp:push`

**请求体**：`SettlementPushRequest` 结构

##### 3.12.2.3 发票 OCR 识别

**端点**：`POST /api/integration/fp/ocr-invoice`

**权限**：`integration:fp:ocr`

**请求**：`multipart/form-data`，含发票图片

**响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "invoiceNo": "INV202609001",
    "amount": 100000.00,
    "taxAmount": 13000.00,
    "totalAmount": 113000.00,
    "vendorName": "华为技术有限公司"
  },
  "timestamp": 1753132800000
}
```

##### 3.12.2.4 支付回调

**端点**：`POST /api/integration/fp/payment-callback`

**权限**：无（FP 系统调用，需签名校验）

**请求体**：

```json
{
  "settlementNo": "SET-2026-0001",
  "paymentStatus": "PAID",
  "paymentTime": "2026-09-20T15:30:00",
  "paymentAmount": 250000.00
}
```

#### 3.12.3 OA 集成

##### 3.12.3.1 OA 健康检查

**端点**：`GET /api/integration/oa/health`

**权限**：需登录

##### 3.12.3.2 推送待办

**端点**：`POST /api/integration/oa/todo/push`

**权限**：`integration:oa:push`

**请求体**：`OaTodoRequest` 结构

##### 3.12.3.3 完成待办

**端点**：`PUT /api/integration/oa/todo/complete`

**权限**：`integration:oa:process`

**查询参数**：`businessKey`

##### 3.12.3.4 转办待办

**端点**：`PUT /api/integration/oa/todo/transfer`

**权限**：`integration:oa:process`

**查询参数**：`businessKey` / `newHandlerUserId`

#### 3.12.4 集成日志管理

##### 3.12.4.1 查询集成日志

**端点**：`GET /api/integration/log/list`

**权限**：需登录

**查询参数**：`page` / `size` / `logType`（D365/FP/OA）/ `businessType` / `businessId` / `responseStatus`

##### 3.12.4.2 集成日志详情

**端点**：`GET /api/integration/log/{id}`

**权限**：需登录

##### 3.12.4.3 手动重试

**端点**：`POST /api/integration/log/{id}/retry`

**权限**：`integration:log:retry`

#### 3.12.5 聚合健康检查

**端点**：`GET /api/integration/health`

**权限**：需登录

**响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "overallStatus": "DEGRADED",
    "lastCheckTime": "2026-07-22T10:30:00",
    "d365": { "connected": true, "tokenValid": true, "recentPushCount": 23, "recentFailCount": 1 },
    "fp": { "connected": true, "tokenValid": true, "recentPushCount": 12, "recentFailCount": 0 },
    "oa": { "connected": false, "tokenValid": false, "recentPushCount": 5, "recentFailCount": 8 }
  },
  "timestamp": 1753132800000
}
```

**overallStatus**：`HEALTHY`（3 个全连通）/ `DEGRADED`（部分连通）/ `DOWN`（全断）

### 3.13 低代码模块

#### 3.13.1 实体管理

##### 3.13.1.1 查询实体列表

**端点**：`GET /api/lowcode/entity/list`

**权限**：`lowcode:entity:list`

##### 3.13.1.2 保存实体设计

**端点**：`POST /api/lowcode/entity`

**权限**：`lowcode:entity:add`

**请求体**：`EntityDesignDTO`（含实体 + 字段 + 关联关系）

##### 3.13.1.3 生成 DDL

**端点**：`GET /api/lowcode/entity/{id}/ddl`

**权限**：`lowcode:entity:list`

**响应**：返回 DDL 语句（按方言 MySQL/PostgreSQL/SQLServer）

##### 3.13.1.4 发布实体

**端点**：`POST /api/lowcode/entity/{id}/publish`

**权限**：`lowcode:entity:process`

**行为**：执行 DDL 安全流程（校验 → 备份 → 执行 → 日志）+ 生成版本快照。

#### 3.13.2 动态实体数据

##### 3.13.2.1 分页查询

**端点**：`GET /api/lowcode/data/{entityCode}`

**权限**：`lowcode:data:{entityCode}:list`（SpEL 动态拼接）

##### 3.13.2.2 高级查询

**端点**：`POST /api/lowcode/data/{entityCode}/query`

**权限**：`lowcode:data:{entityCode}:list`

**请求体**：`DynamicQueryRequest`（支持 LIKE/IN/BETWEEN/OR/排序/JOIN）

##### 3.13.2.3 创建记录

**端点**：`POST /api/lowcode/data/{entityCode}`

**权限**：`lowcode:data:{entityCode}:create`

##### 3.13.2.4 更新记录

**端点**：`PUT /api/lowcode/data/{entityCode}/{id}`

**权限**：`lowcode:data:{entityCode}:edit`

##### 3.13.2.5 删除记录

**端点**：`DELETE /api/lowcode/data/{entityCode}/{id}`

**权限**：`lowcode:data:{entityCode}:remove`

#### 3.13.3 表单配置

##### 3.13.3.1 分页查询表单

**端点**：`GET /api/lowcode/form`

**权限**：`lowcode:form:list`

##### 3.13.3.2 创建表单

**端点**：`POST /api/lowcode/form`

**权限**：`lowcode:form:add`

##### 3.13.3.3 发布表单

**端点**：`POST /api/lowcode/form/{id}/publish`

**权限**：`lowcode:form:process`

#### 3.13.4 微流引擎

##### 3.13.4.1 执行微流

**端点**：`POST /api/lowcode/microflow/{code}/execute`

**权限**：`lowcode:microflow:execute`

**请求体**：

```json
{
  "inputs": {
    "projectId": 1001,
    "userId": 5
  }
}
```

**响应**：返回微流执行结果

##### 3.13.4.2 微流图

**端点**：`GET /api/lowcode/microflow/{id}/diagram.svg`、`GET /api/lowcode/microflow/{id}/diagram.png`

**权限**：`lowcode:microflow:list`

##### 3.13.4.3 微流调试

**端点**：`POST /api/lowcode/microflow/{code}/debug/start`、`POST /api/lowcode/microflow/debug/{sessionId}/step`、`POST /api/lowcode/microflow/debug/{sessionId}/continue`

**权限**：`lowcode:microflow:execute`

#### 3.13.5 规则引擎

##### 3.13.5.1 执行规则

**端点**：`POST /api/lowcode/rule/{code}/execute`

**权限**：`lowcode:rule:execute`

#### 3.13.6 连接器

##### 3.13.6.1 测试连接器

**端点**：`POST /api/lowcode/connector/{code}/test`

**权限**：`lowcode:connector:process`

##### 3.13.6.2 执行连接器

**端点**：`POST /api/lowcode/connector/{code}/execute`

**权限**：`lowcode:connector:execute`

#### 3.13.7 版本管理

##### 3.13.7.1 查询版本历史

**端点**：`GET /api/lowcode/version/history`

**权限**：`lowcode:version:list`

##### 3.13.7.2 版本对比

**端点**：`GET /api/lowcode/version/diff`

**权限**：`lowcode:version:list`

**查询参数**：`fromVersionId` / `toVersionId`

##### 3.13.7.3 环境晋升

**端点**：`POST /api/lowcode/version/promote`

**权限**：`lowcode:version:process`

**行为**：DEV → TEST → PROD 配置包导入导出 + 门禁预检 + 冲突检测

#### 3.13.8 发布管理

##### 3.13.8.1 提交发布

**端点**：`POST /api/lowcode/publish/submit`

**权限**：`lowcode:publish:process`

##### 3.13.8.2 审批发布

**端点**：`POST /api/lowcode/publish/{id}/approve`、`POST /api/lowcode/publish/{id}/reject`

**权限**：`lowcode:publish:process`

### 3.14 聚合报表模块

#### 3.14.1 项目交付统计

**端点**：`GET /api/report/delivery`

**权限**：`report:report:list`

**查询参数**：`startDate` / `endDate`

**响应**：按月分组发起数与完成数、进行中/已完成总数、平均交付周期、延期率

#### 3.14.2 设备资产统计

**端点**：`GET /api/report/asset`

**权限**：`report:report:list`

#### 3.14.3 实施效能统计

**端点**：`GET /api/report/implementation`

**权限**：`report:report:list`

#### 3.14.4 仪表盘概要

**端点**：`GET /api/report/dashboard/stats`

**权限**：需登录

**响应**（8 指标）：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalProjects": 156,
    "ongoingProjects": 35,
    "inStockAssets": 2890,
    "todoCount": 18,
    "monthlyDelivered": 12,
    "monthlyNewProjects": 8,
    "monthlyNewAssets": 145,
    "alertCount": 7
  },
  "timestamp": 1753132800000
}
```

#### 3.14.5 项目趋势

**端点**：`GET /api/report/project/trend`

**权限**：`report:report:list`

#### 3.14.6 待办列表

**端点**：`GET /api/report/todo/list`

**权限**：需登录

**查询参数**：`limit`（默认 10）

#### 3.14.7 近期动态

**端点**：`GET /api/report/recent-activities`

**权限**：需登录

**查询参数**：`limit`

#### 3.14.8 交付件引用实体查询

**端点**：`GET /api/deliverable/ref-entity/{refEntityType}/{refEntityId}`

**权限**：`deliverable:deliverable:list`

**路径参数**：`refEntityType`（TASK/ASSET/PHASE/PROJECT/DELIVERABLE/REPORT）

**端点**：`GET /api/deliverable/ref-entity/list`

**查询参数**：`refEntityType` / `projectId`

---

## 4. 数据模型

本节以 TypeScript 接口语法描述各业务域的核心数据模型，供前端工程师封装 API 类型与后端工程师对照实体字段。

### 4.1 通用模型

```typescript
/** 统一响应包装 */
interface Result<T> {
  code: number;
  message: string;
  data: T | null;
  timestamp: number;
}

/** 分页响应（MyBatis-Plus IPage） */
interface IPage<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}

/** 基础实体（继承 BaseEntity 的实体共用） */
interface BaseEntity {
  id: number;
  createTime: string;
  updateTime: string;
  createBy: string;
  updateBy: string;
  deleted: number;  // 0=未删除, 1=已删除
}

/** 分页请求基础参数 */
interface PageParams {
  page?: number;  // 默认 1
  size?: number;  // 默认 10，最大 200
  sort?: string;  // 格式: field,asc 或 field,desc
}
```

### 4.2 认证与系统模型

```typescript
/** 登录请求 */
interface LoginParams {
  username: string;
  password: string;
  captcha?: string;
  captchaKey?: string;
}

/** 登录响应 */
interface LoginResult {
  token: string;
  userInfo: UserInfo;
  permissions: string[];
}

/** 用户信息 */
interface UserInfo {
  id: number;
  username: string;
  nickname: string;
  email: string;
  phoneNumber: string;
  avatar: string;
  deptId: number;
  deptName: string;
  roles?: string[];
}

/** 系统用户 */
interface SysUser extends BaseEntity {
  username: string;
  nickname: string;
  email: string;
  phoneNumber: string;
  deptId: number;
  deptName: string;
  status: number;  // 0=禁用, 1=启用
}

/** 系统角色 */
interface SysRole extends BaseEntity {
  roleName: string;
  roleKey: string;
  status: number;
  remark?: string;
}

/** 系统菜单 */
interface SysMenu extends BaseEntity {
  menuName: string;
  parentId: number;
  path: string;
  component?: string;
  icon?: string;
  perms?: string;
  menuType: 'M' | 'C' | 'F';  // M=目录 C=菜单 F=按钮
  sortOrder: number;
  visible: number;
  status: number;
  children?: SysMenu[];
}

/** 字典 */
interface SysDict extends BaseEntity {
  dictType: string;
  dictName: string;
  status: number;
  remark?: string;
}

/** 字典项 */
interface SysDictItem {
  dictType: string;
  itemValue: string;
  itemText: string;
  sortOrder: number;
}

/** 系统配置 */
interface SysConfig extends BaseEntity {
  configKey: string;
  configValue: string;
  configType: string;
  remark?: string;
}

/** 操作日志 */
interface SysOperLog {
  title: string;
  businessType: number;  // 1=新增 2=修改 3=删除
  operName: string;
  operUrl: string;
  operIp: string;
  operTime: string;
  costTime: number;
  jsonResult?: string;
}

/** 登录日志 */
interface LoginLog {
  username: string;
  loginIp: string;
  loginLocation: string;
  browser: string;
  os: string;
  status: number;
  msg: string;
  loginTime: string;
}

/** 定时任务 */
interface SysJob extends BaseEntity {
  jobName: string;
  jobGroup: string;
  cronExpression: string;
  invokeTarget: string;
  status: number;  // 0=正常 1=暂停
}
```

### 4.3 项目域模型

```typescript
/** 项目状态枚举（11 态） */
type ProjectStatus =
  | 'PENDING'           // 待审批
  | 'APPROVED'          // 已审批
  | 'PLANNING'          // 规划中
  | 'IN_PROGRESS'       // 进行中
  | 'INITIAL_ACCEPTANCE' // 初验
  | 'FINAL_ACCEPTANCE'  // 终验
  | 'CLOSING'           // 关闭中
  | 'COMPLETED'         // 已完成
  | 'CLOSED'            // 已关闭
  | 'CANCELLED'         // 已取消
  | 'REJECTED';         // 已驳回

/** 项目 */
interface Project extends BaseEntity {
  projectCode: string;
  projectName: string;
  projectType: string;
  customerId: number;
  customerName: string;
  projectManagerId: number;
  projectManagerName: string;
  status: ProjectStatus;
  projectPath: string;  // 物化路径，如 /1001/1002
  parentId?: number;
  planStartDate: string;
  planEndDate: string;
  actualStartDate?: string;
  actualEndDate?: string;
  budget?: number;
  description?: string;
  templateId?: number;
  processInstanceId?: string;
}

/** 项目阶段类型（PPDIOO 5 阶段） */
type PhaseType = 'PREPARE' | 'PLAN' | 'DESIGN' | 'IMPLEMENT' | 'OPERATE';

/** 阶段状态（4 态） */
type PhaseStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'SKIPPED';

/** 项目阶段 */
interface ProjectPhase extends BaseEntity {
  projectId: number;
  phaseName: string;
  phaseType: PhaseType;
  status: PhaseStatus;
  sortOrder: number;
  startDate?: string;
  endDate?: string;
  exitGatePassed: boolean;
  exitGateConfig?: ExitGateConfig;
}

/** 阶段退出闸门配置 */
interface ExitGateConfig {
  deliverableRequired: boolean;
  milestoneRequired: boolean;
  taskRequired: boolean;
  approvalRequired: boolean;
}

/** 里程碑节点类型（12 节点） */
type MilestoneNodeType =
  | 'PROJECT_APPROVAL'
  | 'KICKOFF'
  | 'REQUIREMENT_REVIEW'
  | 'DESIGN_REVIEW'
  | 'PROCUREMENT_COMPLETE'
  | 'DELIVERY_COMPLETE'
  | 'INSTALLATION_COMPLETE'
  | 'INTEGRATION_TEST'
  | 'PILOT_RUN'
  | 'INITIAL_ACCEPTANCE'
  | 'FINAL_ACCEPTANCE'
  | 'PROJECT_CLOSURE';

/** 里程碑状态（5 态） */
type MilestoneStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'OVERDUE' | 'CANCELLED';

/** 里程碑 */
interface Milestone extends BaseEntity {
  projectId: number;
  milestoneName: string;
  nodeType: MilestoneNodeType;
  planDate: string;
  actualDate?: string;
  status: MilestoneStatus;
  remark?: string;
}

/** 项目模板 */
interface ProjectTemplate extends BaseEntity {
  templateCode: string;
  templateName: string;
  templateType: string;
  description?: string;
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED';
  version: number;
}

/** Punch List 项 */
interface PunchList extends BaseEntity {
  projectId: number;
  punchItemName: string;
  description?: string;
  responsibleId: number;
  responsibleName: string;
  deadline: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  status: 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';
  resolvedAt?: string;
  resolution?: string;
}

/** 项目进度汇总 */
interface ProjectProgress {
  projectId: number;
  projectName: string;
  overallProgress: number;
  phaseProgress: PhaseProgressItem[];
  taskProgress: TaskProgressSummary;
  deliverableProgress: DeliverableProgressSummary;
}

interface PhaseProgressItem {
  phaseId: number;
  phaseName: string;
  status: PhaseStatus;
  progress: number;
}

interface TaskProgressSummary {
  total: number;
  completed: number;
  inProgress: number;
  pending: number;
}

interface DeliverableProgressSummary {
  total: number;
  published: number;
  draft: number;
  submitted: number;
}
```

### 4.4 实施域模型

```typescript
/** 任务状态枚举（7 态） */
type TaskStatus =
  | 'PENDING'      // 待受理
  | 'ACCEPTED'     // 已受理
  | 'IN_PROGRESS'  // 进行中
  | 'REVIEW'       // 审核中
  | 'COMPLETED'    // 已完成
  | 'CONFIRMED'    // 已确认
  | 'REJECTED';    // 已驳回

/** 任务类型 */
type TaskType = 'OEM' | 'AGENT';

/** 任务优先级 */
type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';

/** 实施任务 */
interface ImplTask extends BaseEntity {
  projectId: number;
  parentId?: number;
  taskPath: string;  // 物化路径，如 /5001/5002
  depth: number;
  taskName: string;
  taskType: TaskType;
  assigneeId: number;
  assigneeName: string;
  agentId?: number;
  agentName?: string;
  status: TaskStatus;
  priority: TaskPriority;
  planStartDate: string;
  planEndDate: string;
  actualStartDate?: string;
  actualEndDate?: string;
  estimatedHours?: number;
  actualHours?: number;
  description?: string;
  processInstanceId?: string;
}

/** 任务进度上报 */
interface ImplProgress extends BaseEntity {
  taskId: number;
  progressPercent: number;
  actualHours: number;
  remainingHours: number;
  description?: string;
  reportDate: string;
}

/** 任务清单项 */
interface TaskChecklist {
  id: number;
  taskId: number;
  itemName: string;
  required: boolean;
  completed: boolean;
  completedAt?: string;
  completedBy?: string;
  sortOrder: number;
}

/** 任务依赖类型 */
type DependencyType = 'FS' | 'FF' | 'SS' | 'SF';
// FS=完成-开始 FF=完成-完成 SS=开始-开始 SF=开始-完成

/** 任务依赖 */
interface TaskDependency {
  id: number;
  predecessorTaskId: number;
  successorTaskId: number;
  dependencyType: DependencyType;
  lagDays: number;
}

/** 结算单 */
interface Settlement extends BaseEntity {
  settlementNo: string;
  projectId: number;
  agentId: number;
  agentName: string;
  settlementPeriod: string;
  totalAmount: number;
  status: 'DRAFT' | 'SUBMITTED' | 'APPROVED' | 'REJECTED' | 'PAID' | 'CLOSED';
  invoiceNo?: string;
  paymentStatus?: 'UNPAID' | 'PARTIAL' | 'PAID';
  processInstanceId?: string;
}

/** 服务商 */
interface Agent extends BaseEntity {
  agentCode: string;
  agentName: string;
  contactPerson: string;
  contactPhone: string;
  email?: string;
  address?: string;
  status: number;
  qualificationLevel?: string;
}

/** 服务商评分 */
interface AgentScore extends BaseEntity {
  agentId: number;
  projectId: number;
  taskId: number;
  qualityScore: number;    // 0-100
  scheduleScore: number;
  serviceScore: number;
  overallScore: number;
  comment?: string;
}
```

### 4.5 交付件域模型

```typescript
/** 交付件性质分类（7 类，字典驱动） */
type DeliverableNatureType =
  | 'DOCUMENT'
  | 'CODE'
  | 'ENTITY_REF'
  | 'MODEL'
  | 'CONFIG'
  | 'DATA'
  | 'OTHER';

/** 交付件状态枚举（7 态） */
type DeliverableStatus =
  | 'DRAFT'       // 草稿
  | 'SUBMITTED'   // 已提交
  | 'REVIEWED'    // 已审核
  | 'SIGNED'      // 已签署
  | 'PUBLISHED'   // 已发布
  | 'REFERENCED'  // 已引用
  | 'ARCHIVED';   // 已归档

/** 引用实体类型 */
type RefEntityType = 'TASK' | 'ASSET' | 'PHASE' | 'PROJECT' | 'DELIVERABLE' | 'REPORT';

/** 交付件 */
interface Deliverable extends BaseEntity {
  projectId: number;
  phaseId?: number;
  name: string;
  natureType: DeliverableNatureType;
  description?: string;
  ownerId: number;
  ownerName: string;
  status: DeliverableStatus;
  mandatory: boolean;
  templateId?: number;
  templateInherited: boolean;
  refEntityType?: RefEntityType;
  refEntityId?: number;
  currentVersion: number;
}

/** 交付件版本（不可变历史） */
interface DeliverableVersion extends BaseEntity {
  deliverableId: number;
  version: number;
  fileId?: number;
  fileName?: string;
  filePath?: string;
  changeLog?: string;
  createdBy: string;
}

/** 签名类型 */
type SignType = 'ELECTRONIC' | 'STAMP' | 'DIGITAL';

/** 交付件签名 */
interface DeliverableSignature extends BaseEntity {
  deliverableId: number;
  version: number;
  signType: SignType;
  signerId: number;
  signerName: string;
  signedAt: string;
  comment?: string;
  signatureFileId?: number;
}

/** 引用关系类型 */
type ReferenceRelationType = 'OUTPUT' | 'INPUT' | 'RELATED';

/** 交付件引用 */
interface DeliverableReference extends BaseEntity {
  deliverableId: number;
  refEntityType: RefEntityType;
  refEntityId: number;
  refEntityName: string;
  relationType: ReferenceRelationType;
}
```

### 4.6 资产域模型

```typescript
/** 资产状态枚举（9 态） */
type AssetStatus =
  | 'ORDERED'         // 已订购
  | 'IN_TRANSIT'      // 在途
  | 'RECEIVED'        // 已收货
  | 'STAGED'          // 已暂存
  | 'INSTALLED'       // 已安装
  | 'COMMISSIONED'    // 已调测
  | 'IN_PRODUCTION'   // 投产
  | 'RMA'             // 退换货
  | 'DECOMMISSIONED'; // 退役

/** 资产 */
interface Asset extends BaseEntity {
  assetCode: string;
  assetName: string;
  categoryId: number;
  categoryName: string;
  modelId: number;
  modelName: string;
  serialNo?: string;
  status: AssetStatus;
  projectId?: number;
  locationId?: number;
  locationName?: string;
  purchaseDate?: string;
  purchasePrice?: number;
  supplierId?: number;
  supplierName?: string;
  warrantyStartDate?: string;
  warrantyEndDate?: string;
  remark?: string;
}

/** 资产分类 */
interface AssetCategory extends BaseEntity {
  categoryCode: string;
  categoryName: string;
  parentId?: number;
  sortOrder: number;
}

/** 资产型号 */
interface AssetModel extends BaseEntity {
  modelCode: string;
  modelName: string;
  categoryId: number;
  brandName?: string;
  standardPrice?: number;
  specifications?: string;
}

/** 资产调拨 */
interface AssetTransfer extends BaseEntity {
  assetId: number;
  assetCode: string;
  assetName: string;
  fromProjectId: number;
  fromProjectName: string;
  toProjectId: number;
  toProjectName: string;
  reason: string;
  applyDate: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'COMPLETED' | 'CANCELLED';
  processInstanceId?: string;
}

/** RMA 状态（6 步闭环） */
type RmaStatus = 'APPLIED' | 'APPROVED' | 'SHIPPED' | 'RECEIVED' | 'RETURNED' | 'VERIFIED';

/** RMA 工单 */
interface Rma extends BaseEntity {
  rmaNo: string;
  assetId: number;
  assetCode: string;
  assetName: string;
  rmaReason: string;
  faultDescription?: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  status: RmaStatus;
  applyDate: string;
  approvedDate?: string;
  shippedDate?: string;
  trackingNo?: string;
  receivedDate?: string;
  returnedDate?: string;
  verifiedDate?: string;
  remark?: string;
}

/** 质保 */
interface Warranty extends BaseEntity {
  assetId: number;
  assetCode: string;
  assetName: string;
  warrantyStartDate: string;
  warrantyEndDate: string;
  warrantyPeriod: number;  // 月
  status: 'ACTIVE' | 'EXPIRING' | 'EXPIRED';
  supplierId?: number;
  supplierName?: string;
}
```

### 4.7 基线与依赖模型

```typescript
/** 基线 */
interface Baseline extends BaseEntity {
  projectId: number;
  baselineName: string;
  description?: string;
  status: 'DRAFT' | 'ACTIVE' | 'SUPERSEDED';
  snapshotData: TaskPlanSnapshot;
  createdBy: string;
}

/** 任务计划快照 */
interface TaskPlanSnapshot {
  tasks: TaskSnapshotItem[];
  capturedAt: string;
}

interface TaskSnapshotItem {
  taskId: number;
  taskName: string;
  planStartDate: string;
  planEndDate: string;
  duration: number;
  assigneeId?: number;
  assigneeName?: string;
}

/** 基线偏差分析结果 */
interface BaselineDiff {
  baselineId: number;
  projectId: number;
  thresholds: BaselineThresholds;
  overallStatus: 'NORMAL' | 'WARNING' | 'OVERRUN';
  taskDiffs: TaskDiffItem[];
  triggeredThresholds: string[];
}

interface BaselineThresholds {
  daysThreshold: number;       // 默认 5
  percentThreshold: number;    // 默认 10
  countThreshold: number;      // 默认 3
}

interface TaskDiffItem {
  taskId: number;
  taskName: string;
  baselineStart: string;
  baselineEnd: string;
  currentStart: string;
  currentEnd: string;
  daysVariance: number;
  percentVariance: number;
  status: 'NORMAL' | 'OVERRUN' | 'AHEAD';
}
```

### 4.8 工作流与审批模型

```typescript
/** 审批类型（10 类） */
type ApprovalType =
  | 'PROJECT'
  | 'TASK'
  | 'DELIVERABLE'
  | 'RISK'
  | 'ISSUE'
  | 'CHANGE'
  | 'RESOURCE'
  | 'COST'
  | 'PHASE_EXIT'
  | 'BASELINE_CHANGE';

/** 审批状态（5 态） */
type ApprovalStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'WITHDRAWN' | 'TIMEOUT';

/** 审批动作 */
type ApprovalAction =
  | 'SUBMIT'
  | 'APPROVE'
  | 'REJECT'
  | 'WITHDRAW'
  | 'RESUBMIT'
  | 'ESCALATE'
  | 'TIMEOUT';

/** 审批记录 */
interface ApprovalRecord extends BaseEntity {
  approvalType: ApprovalType;
  businessId: number;
  businessCode?: string;
  projectId?: number;
  processInstanceId?: string;
  title: string;
  submitterId: number;
  submitterName: string;
  currentNodeId?: string;
  currentNodeName?: string;
  status: ApprovalStatus;
  round: number;  // 审批轮次
  submittedAt: string;
  completedAt?: string;
  timeoutAt?: string;
  escalated: boolean;
  version: number;  // 乐观锁
}

/** 审批节点 */
interface ApprovalNode {
  id: number;
  recordId: number;
  nodeName: string;
  nodeOrder: number;
  approverId?: number;
  approverRole?: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  approverActualId?: number;
  opinion?: string;
  operatedAt?: string;
  timeoutAt?: string;
}

/** 审批历史 */
interface ApprovalHistory {
  id: number;
  recordId: number;
  round: number;
  nodeName: string;
  operatorId: number;
  operatorName: string;
  action: ApprovalAction;
  opinion?: string;
  operatedAt: string;
}

/** 字段权限 */
type FieldPermission = 'VISIBLE' | 'MASKED' | 'HIDDEN';

/** 脱敏规则 */
type MaskPattern = 'phone-mask' | 'amount-mask' | 'email-mask' | 'custom';

/** 审批字段权限配置 */
interface ApprovalFieldPermission extends BaseEntity {
  approvalNodeId: number;
  entityType: string;
  fieldName: string;
  permission: FieldPermission;
  maskPattern?: MaskPattern;
  customPattern?: string;
  version: number;
}

/** Flowable 流程定义 */
interface ProcessDefinition {
  id: string;
  key: string;
  name: string;
  version: number;
  deploymentId: string;
  resourceName: string;
  suspended: boolean;
}

/** Flowable 流程实例 */
interface ProcessInstance {
  id: string;
  processDefinitionKey: string;
  processDefinitionName: string;
  businessKey?: string;
  startUserId: string;
  startTime: string;
  suspended: boolean;
  currentActivityIds?: string[];
}

/** Flowable 任务 */
interface TaskDTO {
  id: string;
  name: string;
  assignee?: string;
  processInstanceId: string;
  processDefinitionId: string;
  createTime: string;
  dueDate?: string;
  variables?: Record<string, any>;
}
```

### 4.9 治理域模型

```typescript
/** 变更请求状态（6 态） */
type ChangeRequestStatus =
  | 'SUBMITTED'      // 已提交
  | 'UNDER_REVIEW'   // 审核中
  | 'CCB_APPROVED'   // CCB 已批准
  | 'CCB_REJECTED'   // CCB 已驳回
  | 'IMPLEMENTING'   // 实施中
  | 'CLOSED';        // 已关闭

/** 变更类型 */
type BaselineChangeType = 'SCHEDULE' | 'COST' | 'SCOPE';

/** 变更请求 */
interface ChangeRequest extends BaseEntity {
  crNo: string;  // CR-YYYY-XXXX
  projectId: number;
  projectName?: string;
  title: string;
  description: string;
  requesterId?: number;
  requesterName?: string;
  requestDate: string;
  impactScope?: string;
  impactSchedule?: string;
  impactCost?: string;
  impactQuality?: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  status: ChangeRequestStatus;
  approverId?: number;
  approverName?: string;
  processInstanceId?: string;
  baselineUpdated: boolean;
  approvedAt?: string;
  closedAt?: string;
  version: number;
}

/** 基线变更历史 */
interface BaselineHistory extends BaseEntity {
  projectId: number;
  changeRequestId: number;
  crNo: string;
  changeType: BaselineChangeType;
  fieldName: string;
  oldValue: string;
  newValue: string;
  description: string;
  changedAt: string;
  changedBy: string;
}

/** 风险类别 */
type RiskCategory = 'TECHNICAL' | 'EXTERNAL' | 'ORGANIZATIONAL' | 'PM';

/** 风险缓解策略 */
type RiskMitigation = 'AVOID' | 'MITIGATE' | 'TRANSFER' | 'ACCEPT';

/** 风险优先级 */
type RiskPriority = 'LOW' | 'MEDIUM' | 'HIGH';

/** 风险状态（4 态） */
type RiskStatus = 'OPEN' | 'IN_PROGRESS' | 'CLOSED' | 'ESCALATED';

/** 风险 */
interface Risk extends BaseEntity {
  riskNo: string;  // RISK-YYYY-XXXX
  projectId: number;
  description: string;
  category?: RiskCategory;
  likelihood: number;  // 1-5
  impact: number;      // 1-5
  score: number;       // likelihood * impact, 1-25
  priority: RiskPriority;
  mitigation?: RiskMitigation;
  contingencyPlan?: string;
  ownerId?: number;
  ownerName?: string;
  status: RiskStatus;
  reviewDate?: string;
  sourceIssueId?: number;
  identifiedAt: string;
  closedAt?: string;
}

/** 风险矩阵 */
interface RiskMatrixDto {
  matrix: number[][];  // 5x5, matrix[likelihood-1][impact-1]
  risks: Risk[];
  totalRisks: number;
  highPriorityCount: number;
}

/** 问题状态（4 态） */
type IssueStatus = 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';

/** 问题 */
interface Issue extends BaseEntity {
  issueNo: string;  // ISSUE-YYYY-XXXX
  projectId: number;
  description: string;
  raisedBy?: number;
  raisedByName?: string;
  assigneeId?: number;
  assigneeName?: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  targetResolveDate: string;
  status: IssueStatus;
  sourceRiskId?: number;
  sourceRiskNo?: string;
  sourceChangeId?: number;
  sourceCrNo?: string;
  resolvedAt?: string;
  closedAt?: string;
  resolution?: string;
}
```

### 4.10 通知与文件模型

```typescript
/** 通知业务分类 */
type NotificationCategory =
  | 'MILESTONE'
  | 'TASK'
  | 'APPROVAL'
  | 'PUNCH_LIST'
  | 'WARRANTY'
  | 'RMA'
  | 'SETTLEMENT'
  | 'SYSTEM';

/** 通知投递通道 */
type NotificationChannel = 'IN_APP' | 'WS' | 'EMAIL' | 'OA';

/** 通知已读状态 */
type ReadStatus = 'UNREAD' | 'READ';

/** 站内通知 */
interface Notification {
  id: number;
  userId: number;
  title: string;
  content: string;
  category: NotificationCategory;
  bizType?: string;
  bizId?: number;
  readStatus: ReadStatus;
  channel: NotificationChannel;
  createdAt: string;
  createdBy?: number;
}

/** 通知模板 */
interface NotificationTemplate {
  id: number;
  templateCode: string;  // 唯一，如 TASK_ASSIGNED
  subject: string;       // 含 ${var} 占位符
  body: string;          // 含 ${var} 占位符
  variables?: string;    // JSON 数组，变量定义
  description?: string;
  createdAt: string;
  updatedAt: string;
}

/** 附件（文件元数据） */
interface Attachment {
  id: number;
  fileName: string;
  filePath: string;
  fileSize: number;
  fileType: string;
  md5?: string;
  bizType?: string;
  bizId?: number;
  category?: string;
  exifGps?: string;         // "lat,lng"
  geoFenceStatus?: 'INSIDE' | 'OUTSIDE' | 'UNKNOWN';
  thumbnailPath?: string;
  uploadTime: string;
  uploadedBy?: number;
}
```

### 4.11 集成与低代码模型

```typescript
/** 集成日志类型 */
type IntegrationLogType = 'D365' | 'FP' | 'OA' | 'SMS' | 'EHR';

/** 集成业务类型 */
type IntegrationBusinessType =
  | 'PURCHASE_ORDER'
  | 'PURCHASE_RECEIPT'
  | 'SETTLEMENT'
  | 'INVOICE'
  | 'TODO_PUSH'
  | 'TODO_COMPLETE'
  | 'TODO_TRANSFER'
  | 'OCR_INVOICE'
  | 'PAYMENT_CALLBACK';

/** 集成响应状态 */
type IntegrationResponseStatus = 'SUCCESS' | 'FAILED' | 'PENDING';

/** 集成日志 */
interface IntegrationLog extends BaseEntity {
  logType: IntegrationLogType;
  businessType: IntegrationBusinessType;
  businessId?: string;
  requestUrl: string;
  requestBody?: string;
  responseStatus: IntegrationResponseStatus;
  responseBody?: string;
  errorMessage?: string;
  retryCount: number;
  maxRetry: number;
  nextRetryTime?: string;
}

/** 集成健康状态 */
type IntegrationOverallStatus = 'HEALTHY' | 'DEGRADED' | 'DOWN';

/** 子系统健康快照 */
interface SubSystemHealth {
  connected: boolean;
  tokenValid: boolean;
  recentPushCount: number;
  recentFailCount: number;
  recentLogs?: IntegrationLog[];
}

/** 聚合健康检查响应 */
interface IntegrationHealthDto {
  overallStatus: IntegrationOverallStatus;
  lastCheckTime: string;
  d365: SubSystemHealth;
  fp: SubSystemHealth;
  oa: SubSystemHealth;
}

/** 低代码实体 */
interface LowCodeEntity extends BaseEntity {
  code: string;
  name: string;
  tableName: string;  // pms_lc_xxx
  description?: string;
  bizType?: string;
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED';
  version: number;
}

/** 低代码字段类型 */
type LowCodeFieldType =
  | 'STRING' | 'INTEGER' | 'DECIMAL' | 'BOOLEAN'
  | 'DATE' | 'DATETIME' | 'TEXT' | 'LONG';

/** 低代码字段 */
interface LowCodeField extends BaseEntity {
  entityId: number;
  name: string;
  label: string;
  fieldType: LowCodeFieldType;
  length?: number;
  scale?: number;
  nullable: boolean;
  primaryKey: boolean;
  indexed: boolean;
  uniqueFlag: boolean;
  defaultValue?: string;
  sortOrder: number;
}

/** 低代码微流节点类型（11 种） */
type MicroflowNodeType =
  | 'START' | 'END'
  | 'ASSIGN' | 'CONDITION' | 'LOOP'
  | 'CALL_SERVICE' | 'CALL_MICROFLOW' | 'CALL_RULE' | 'CALL_CONNECTOR'
  | 'THROW_EXCEPTION' | 'RETURN';

/** 低代码微流 */
interface LowCodeMicroflow extends BaseEntity {
  code: string;
  name: string;
  description?: string;
  definition: string;  // JSON: { nodes: [], edges: [] }
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED';
  version: number;
  bizType?: string;
}

/** 低代码规则类型 */
type LowCodeRuleType = 'DECISION_TABLE' | 'EXPRESSION' | 'LITEFLOW';

/** 低代码规则 */
interface LowCodeRule extends BaseEntity {
  code: string;
  name: string;
  description?: string;
  type: LowCodeRuleType;
  definition: string;
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED';
  version: number;
  bizType?: string;
  ext?: string;
}

/** 低代码连接器类型 */
type LowCodeConnectorType = 'REST' | 'DB' | 'MQ' | 'FILE';

/** 低代码连接器 */
interface LowCodeConnector extends BaseEntity {
  code: string;
  name: string;
  description?: string;
  type: LowCodeConnectorType;
  config: string;  // JSON
  status: 'ACTIVE' | 'INACTIVE';
  version: number;
  bizType?: string;
}

/** 低代码触发器类型 */
type LowCodeTriggerType = 'CRUD' | 'QUARTZ' | 'EVENT';

/** 低代码触发器 */
interface LowCodeTrigger {
  id: number;
  code: string;
  name: string;
  type: LowCodeTriggerType;
  config: string;  // JSON
  targetType: 'MICROFLOW' | 'PROCESS';
  targetCode: string;
  status: 'ACTIVE' | 'INACTIVE';
}

/** 低代码配置版本 */
interface LowCodeConfigVersion extends BaseEntity {
  configType: 'FORM' | 'LIST' | 'TAB' | 'RELATED_PAGE' | 'ENTITY' | 'MICROFLOW' | 'RULE' | 'CONNECTOR';
  configId: number;
  configCode: string;
  version: number;
  snapshot: string;  // JSON 全量
  changeLog?: string;
  status: 'ACTIVE' | 'ARCHIVED';
  environment: 'DEV' | 'TEST' | 'PROD';
  parentVersionId?: number;
  branch: string;  // 默认 "main"
  tags?: string;
}

/** 低代码发布记录状态 */
type LowCodePublishStatus =
  | 'DRAFT' | 'SUBMITTED' | 'APPROVING' | 'APPROVED' | 'REJECTED' | 'PUBLISHED';

/** 低代码发布记录 */
interface LowCodePublishRecord {
  id: number;
  configType: string;
  configId: number;
  configCode: string;
  version: number;
  status: LowCodePublishStatus;
  currentLevel?: number;
  approvalChainId?: number;
  applicantId?: number;
  applicant?: string;
  approverId?: number;
  approver?: string;
  changeLog?: string;
  rejectReason?: string;
  submittedAt?: string;
  approvedAt?: string;
  publishedAt?: string;
}

/** 仪表盘统计（8 指标） */
interface DashboardStats {
  totalProjects: number;
  ongoingProjects: number;
  inStockAssets: number;
  todoCount: number;
  monthlyDelivered: number;
  monthlyNewProjects: number;
  monthlyNewAssets: number;
  alertCount: number;
}

/** 项目趋势数据点 */
interface ProjectTrendItem {
  month: string;
  status: ProjectStatus;
  count: number;
}

/** 待办事项 */
interface TodoItem {
  taskId: number;
  taskName: string;
  projectName: string;
  priority: TaskPriority;
  planEndDate: string;
}

/** 近期动态项 */
interface ActivityItem {
  type: 'OPER_LOG' | 'LOGIN_LOG';
  title: string;
  operator: string;
  operTime: string;
  detail?: string;
}
```

---

## 5. 错误码总表

### 5.1 HTTP 状态码

平台严格遵循 RFC 7231 HTTP 状态码语义：

| HTTP 状态码 | 含义 | 平台使用场景 |
|-------------|------|--------------|
| 200 OK | 请求成功 | 所有业务成功的响应（含 code=200 与 code=1001 业务校验失败） |
| 400 Bad Request | 请求参数错误 | 参数校验失败、请求体格式错误、循环依赖检测 |
| 401 Unauthorized | 未认证 | JWT 缺失/过期/无效/黑名单 |
| 403 Forbidden | 无权限 | `@PreAuthorize` 校验失败 |
| 404 Not Found | 资源不存在 | 实体按 ID 查询不存在、路由未匹配 |
| 405 Method Not Allowed | 方法不支持 | GET 请求 POST 端点等 |
| 408 Request Timeout | 请求超时 | 客户端请求超时 |
| 409 Conflict | 资源冲突 | 唯一约束冲突、幂等键冲突、乐观锁冲突 |
| 429 Too Many Requests | 限流 | `@RateLimit` 触发 |
| 500 Internal Server Error | 服务器内部错误 | 未捕获异常兜底 |
| 503 Service Unavailable | 服务不可用 | 外部集成熔断器 OPEN |

### 5.2 业务错误码

#### 5.2.1 通用业务错误码

| code | 枚举名 | message | HTTP 状态 | 触发场景 |
|------|--------|---------|-----------|----------|
| 200 | SUCCESS | 操作成功 | 200 | 业务成功 |
| 400 | BAD_REQUEST | 请求参数错误 | 400 | 参数校验失败 |
| 401 | UNAUTHORIZED | 未认证或认证已过期 | 401 | JWT 校验失败 |
| 403 | FORBIDDEN | 无访问权限 | 403 | 权限不足 |
| 404 | NOT_FOUND | 资源不存在 | 404 | 资源未找到 |
| 405 | METHOD_NOT_ALLOWED | 请求方法不支持 | 405 | 方法不匹配 |
| 408 | REQUEST_TIMEOUT | 请求超时 | 408 | 请求超时 |
| 409 | CONFLICT | 资源冲突 | 409 | 唯一约束冲突 |
| 429 | TOO_MANY_REQUESTS | 请求过于频繁 | 429 | 限流触发 |
| 500 | INTERNAL_ERROR | 服务器内部错误 | 500 | 未捕获异常 |
| 503 | SERVICE_UNAVAILABLE | 服务暂不可用 | 503 | 熔断器 OPEN |

#### 5.2.2 业务专用错误码

| code | 枚举名 | message | HTTP 状态 | 触发场景 |
|------|--------|---------|-----------|----------|
| 1001 | BUSINESS_ERROR | 业务校验失败 | 200 | `BusinessException` 抛出 |
| 1002 | INTEGRATION_ERROR | 外部集成调用失败 | 200 | `IntegrationException` 抛出 |
| 1003 | RATE_LIMIT_EXCEEDED | 接口限流 | 429 | `RateLimitExceededException` |
| 1004 | IDEIMPOTENT_CONFLICT | 幂等键冲突 | 409 | `IdempotentConflictException` |
| 1005 | OPTIMISTIC_LOCK_FAILURE | 数据已被他人修改 | 409 | `OptimisticLockingFailureException` |
| 1006 | CYCLE_DETECTED | 检测到循环依赖 | 400 | `CycleDetectedException` |

### 5.3 异常类型映射

#### 5.3.1 异常到响应映射表

| Java 异常类 | HTTP 状态 | code | message 来源 | 处理器 |
|-------------|-----------|------|--------------|--------|
| `BusinessException` | 200 | 1001 | `e.getMessage()` | `GlobalExceptionHandler` |
| `IntegrationException` | 200 | 1002 | `e.getMessage()` | `GlobalExceptionHandler` |
| `RateLimitExceededException` | 429 | 1003 | "请求过于频繁，请稍后再试" | `GlobalExceptionHandler` |
| `IdempotentConflictException` | 409 | 1004 | "幂等键冲突，请勿重复提交" | `GlobalExceptionHandler` |
| `OptimisticLockingFailureException` | 409 | 1005 | "数据已被他人修改，请刷新后重试" | `GlobalExceptionHandler` |
| `CycleDetectedException` | 400 | 1006 | "检测到循环依赖：" + 路径 | `GlobalExceptionHandler` |
| `TaskChecklistRequiredException` | 200 | 1001 | "强制检查项未完成：" + 项名 | `GlobalExceptionHandler` |
| `DdlSecurityException` | 200 | 1001 | "DDL 语句被安全校验拦截" | `GlobalExceptionHandler` |
| `MicroflowExecutionException` | 200 | 1001 | "微流执行失败：" + 节点 | `GlobalExceptionHandler` |
| `MethodArgumentNotValidException` | 400 | 400 | 字段错误聚合 | `GlobalExceptionHandler` |
| `ConstraintViolationException` | 400 | 400 | 参数校验错误 | `GlobalExceptionHandler` |
| `HttpMessageNotReadableException` | 400 | 400 | "请求体格式错误" | `GlobalExceptionHandler` |
| `HttpRequestMethodNotSupportedException` | 405 | 405 | "请求方法不支持" | `GlobalExceptionHandler` |
| `AccessDeniedException` | 403 | 403 | "无访问权限" | `GlobalExceptionHandler` |
| `AuthenticationException` | 401 | 401 | "未认证或认证已过期" | `GlobalExceptionHandler` |
| `NoHandlerFoundException` | 404 | 404 | "资源不存在" | `GlobalExceptionHandler` |
| `Exception`（兜底） | 500 | 500 | "服务器内部错误" | `GlobalExceptionHandler` |

#### 5.3.2 参数校验错误聚合

`MethodArgumentNotValidException` 与 `ConstraintViolationException` 的错误信息按字段聚合：

```json
{
  "code": 400,
  "message": "项目名称不能为空; 项目编号长度必须在5-50之间; 计划开始时间不能为空; 预算必须大于0",
  "data": null,
  "timestamp": 1753132800000
}
```

#### 5.3.3 业务校验错误示例

业务规则违反时 HTTP 状态为 200，code 为 1001：

```json
{
  "code": 1001,
  "message": "项目状态不允许删除",
  "data": null,
  "timestamp": 1753132800000
}
```

#### 5.3.4 集成错误示例

外部系统调用失败时：

```json
{
  "code": 1002,
  "message": "D365 OAuth2 Token 获取失败：连接超时",
  "data": null,
  "timestamp": 1753132800000
}
```

#### 5.3.5 循环依赖错误示例

```json
{
  "code": 1006,
  "message": "检测到循环依赖：5001 → 5002 → 5003 → 5001",
  "data": null,
  "timestamp": 1753132800000
}
```

---

## 6. 版本兼容性策略

### 6.1 版本号规则

平台 API 版本号遵循 **语义化版本控制**（Semantic Versioning）规范：

```
MAJOR.MINOR.PATCH
```

- **MAJOR**（主版本号）：破坏性变更，不向后兼容（如删除字段、改变字段语义、改变响应结构）
- **MINOR**（次版本号）：新增功能，向后兼容（如新增字段、新增端点、新增枚举值）
- **PATCH**（修订号）：问题修复，向后兼容（如修复 Bug、性能优化）

当前版本：**v1.0.0**

### 6.2 兼容性原则

#### 6.2.1 向后兼容的变更（MINOR 版本升级）

以下变更视为向后兼容，可直接发布，无需 bump MAJOR 版本：

| 变更类型 | 示例 | 兼容性 |
|----------|------|--------|
| 新增端点 | 新增 `GET /api/project/{id}/members` | ✅ 兼容 |
| 新增请求参数（可选） | 列表查询新增 `customerId` 过滤 | ✅ 兼容 |
| 新增响应字段 | `Project` 响应新增 `estimatedCost` | ✅ 兼容 |
| 新增枚举值 | `ProjectStatus` 新增 `ON_HOLD` | ✅ 兼容（客户端按未知值处理） |
| 字段类型放宽 | `string` 放宽为 `string \| null` | ✅ 兼容 |
| 默认值变更 | `page` 默认从 1 改为 1（无变化） | ✅ 兼容 |

#### 6.2.2 破坏性变更（MAJOR 版本升级）

以下变更视为破坏性，必须 bump MAJOR 版本并保留旧版本过渡期：

| 变更类型 | 示例 | 兼容性 |
|----------|------|--------|
| 删除端点 | 移除 `GET /api/old-projects` | ❌ 不兼容 |
| 删除响应字段 | `Project` 移除 `budget` | ❌ 不兼容 |
| 改变字段语义 | `status` 从字符串改为数字 | ❌ 不兼容 |
| 改变字段类型 | `id` 从 `number` 改为 `string` | ❌ 不兼容 |
| 改变响应结构 | 从 `{ data: T }` 改为 `{ result: T }` | ❌ 不兼容 |
| 必填化可选字段 | `customerId` 从可选改为必填 | ❌ 不兼容 |
| 改变默认值 | `page` 默认从 1 改为 0 | ❌ 不兼容 |

### 6.3 破坏性变更流程

当必须引入破坏性变更时，遵循以下流程：

1. **评估必要性**：架构师评审，确认无向后兼容方案
2. **设计新版本**：`/api/v2/{module}/...`，与 v1 并存
3. **公告与过渡期**：
   - 在 v1 端点响应头添加 `Deprecation: true` 与 `Sunset: <日期>`
   - Swagger 文档标注 `@Deprecated`
   - 至少 6 个月过渡期
4. **客户端迁移**：通知所有消费方（前端、移动端、外部集成）迁移到 v2
5. **监控迁移率**：通过 `X-Trace-Id` 与日志统计 v1 调用量
6. **下线 v1**：迁移率 ≥ 95% 且连续 30 天无 v1 调用后下线

### 6.4 弃用流程

#### 6.4.1 弃用标记

弃用的端点在响应头添加：

```
Deprecation: true
Sunset: Sat, 31 Dec 2026 23:59:59 GMT
Link: <https://pms.example.com/api/v2/project>; rel="successor-version"
```

#### 6.4.2 弃用通知

- Swagger 文档 `@Deprecated` 注解
- 响应头 `Deprecation` 与 `Sunset`
- 月度 API 变更公告
- 集成方邮件通知

#### 6.4.3 弃用时间线

| 阶段 | 时长 | 动作 |
|------|------|------|
| 公告期 | 1 个月 | 通知集成方，提供迁移文档 |
| 过渡期 | 6 个月 | v1 与 v2 并存，v1 响应含弃用头 |
| 警告期 | 1 个月 | v1 响应增加 `Warning: 299` 头 |
| 下线 | — | 移除 v1 路由，返回 410 Gone |

### 6.5 向后兼容清单

平台承诺以下兼容性保证（v1.x.x 内）：

#### 6.5.1 端点路径兼容

- 所有已发布的 `GET /api/{module}/...`、`POST /api/{module}/...` 路径保持不变
- 新增端点使用新路径，不占用已存在路径
- 路径参数（`{id}`）保持 `number` 类型

#### 6.5.2 请求参数兼容

- 已有查询参数保持名称与类型不变
- 新增查询参数必须可选（有默认值或允许为空）
- 请求体字段可新增（客户端忽略未知字段），不可删除或重命名

#### 6.5.3 响应结构兼容

- `Result<T>` 包装结构不变（code/message/data/timestamp 四字段）
- 已有响应字段保持名称与类型不变
- 新增响应字段允许（客户端忽略未知字段）
- 枚举值可新增（客户端按未知值处理，不报错）

#### 6.5.4 错误码兼容

- 已有错误码的 `code` 值与 `message` 语义保持不变
- 新增错误码使用未占用数字（如 1007、1008...）
- HTTP 状态码与 `code` 的映射关系保持不变

#### 6.5.5 枚举值兼容

- 已有枚举值保持不变
- 新增枚举值追加到末尾
- 客户端应按"未知值"处理新增枚举，不报错

#### 6.5.6 分页协议兼容

- `IPage<T>` 结构（records/total/size/current/pages）保持不变
- 分页查询参数（page/size/sort）保持不变
- `page` 从 1 开始的约定保持不变

#### 6.5.7 认证机制兼容

- JWT Bearer Token 认证方式保持不变
- `Authorization: Bearer <token>` 头格式保持不变
- WebSocket 握手（`/ws?token=` 或 `Authorization` 头）保持不变

#### 6.5.8 限流与幂等兼容

- `X-Idempotent-Key` 头约定保持不变
- 限流响应（429 + code=1003）结构保持不变
- 限流阈值可通过配置调整，但不影响协议

---

## 附录 A：完整端点清单（按模块）

| 序号 | 模块 | 端点数 | 主要路径前缀 |
|------|------|--------|--------------|
| 1 | 认证 | 3 | `/api/auth` |
| 2 | 系统管理 | 25+ | `/api/system` |
| 3 | 项目管理 | 30+ | `/api/project` |
| 4 | 实施管理 | 20+ | `/api/implementation/task`、`/api/impl` |
| 5 | 交付件管理 | 16+ | `/api/deliverable` |
| 6 | 资产管理 | 15+ | `/api/asset` |
| 7 | 基线管理 | 7 | `/api/baseline`、`/api/implementation/task/dependency` |
| 8 | 工作流 | 27+ | `/api/workflow` |
| 9 | 治理 | 30+ | `/api/governance` |
| 10 | 通知 | 11+ | `/api/notification` |
| 11 | 文件 | 5 | `/api/file` |
| 12 | 外部集成 | 14 | `/api/integration` |
| 13 | 低代码 | 80+ | `/api/lowcode` |
| 14 | 聚合报表 | 8+ | `/api/report`、`/api/deliverable/ref-entity` |
| **合计** | **14 模块** | **290+** | — |

## 附录 B：权限码总览（按模块）

| 模块 | 权限前缀 | 主要 action |
|------|----------|-------------|
| 认证 | — | 登录/登出无需权限码 |
| 系统 | `system` | `user/role/menu/dict/config/audit/schedule/cache/feedback/help-content` × `list/add/edit/remove/process/clear` |
| 项目 | `project` | `project/phase/template/milestone/acceptance/punch-list` × `list/add/edit/remove/process` |
| 实施 | `implementation` / `settlement` | `task/progress/agent` × `list/add/edit/remove/process` |
| 交付件 | `deliverable` | `deliverable` × `list/add/edit/remove/process` |
| 资产 | `asset` | `asset/category/model/transfer/rma/warranty` × `list/add/edit/remove/process` |
| 基线 | `baseline` | `baseline` × `list/add/remove/process` |
| 工作流 | `workflow` | `workflow/approval/field-perm` × `list/process` |
| 治理 | `governance` | `change-request/risk/issue` × `list/add/edit/remove/process` |
| 通知 | `notification` | `notification/template` × `list/add/edit/remove/send` |
| 文件 | — | 文件操作需登录，无细粒度权限码 |
| 集成 | `integration` | `d365/fp/oa` × `push/sync/ocr/process` + `log:retry` |
| 低代码 | `lowcode` | `entity/form/list/tab/microflow/rule/connector/trigger/process/version/publish` × `list/add/edit/remove/process/execute` + 动态 `data:{entityCode}:{action}` |
| 报表 | `report` | `report:list` |

## 附录 C：状态机速查表

| 业务对象 | 状态数 | 状态枚举 |
|----------|--------|----------|
| 项目 | 11 | PENDING / APPROVED / PLANNING / IN_PROGRESS / INITIAL_ACCEPTANCE / FINAL_ACCEPTANCE / CLOSING / COMPLETED / CLOSED / CANCELLED / REJECTED |
| 阶段 | 4 | PENDING / IN_PROGRESS / COMPLETED / SKIPPED |
| 里程碑 | 5 | PENDING / IN_PROGRESS / COMPLETED / OVERDUE / CANCELLED |
| 任务 | 7 | PENDING / ACCEPTED / IN_PROGRESS / REVIEW / COMPLETED / CONFIRMED / REJECTED |
| 交付件 | 7 | DRAFT / SUBMITTED / REVIEWED / SIGNED / PUBLISHED / REFERENCED / ARCHIVED |
| 资产 | 9 | ORDERED / IN_TRANSIT / RECEIVED / STAGED / INSTALLED / COMMISSIONED / IN_PRODUCTION / RMA / DECOMMISSIONED |
| 变更请求 | 6 | SUBMITTED / UNDER_REVIEW / CCB_APPROVED / CCB_REJECTED / IMPLEMENTING / CLOSED |
| 风险 | 4 | OPEN / IN_PROGRESS / CLOSED / ESCALATED |
| 问题 | 4 | OPEN / IN_PROGRESS / RESOLVED / CLOSED |
| 审批 | 5 | PENDING / APPROVED / REJECTED / WITHDRAWN / TIMEOUT |
| RMA | 6 | APPLIED / APPROVED / SHIPPED / RECEIVED / RETURNED / VERIFIED |
| 资产调拨 | 5 | PENDING / APPROVED / REJECTED / COMPLETED / CANCELLED |
| 结算 | 6 | DRAFT / SUBMITTED / APPROVED / REJECTED / PAID / CLOSED |
| 质保 | 3 | ACTIVE / EXPIRING / EXPIRED |
| 集成日志 | 3 | SUCCESS / FAILED / PENDING |

---

## 附录 D：BPMN 流程定义清单

| 流程 ID | 流程名 | 文件 | 节点结构 |
|---------|--------|------|----------|
| `projectApproval` | 项目审批流程 | `project-approval.bpmn20.xml` | 开始 → PM审核 → 网关 → 部门经理审核 → 网关 → 结束/驳回终止 |
| `assetTransfer` | 资产转移流程 | `asset-transfer.bpmn20.xml` | 开始 → 源PM审核 → 网关 → 目标PM审核 → 网关 → 结束/驳回终止 |
| `finalAcceptance` | 最终验收流程 | `final-acceptance.bpmn20.xml` | 开始 → 客户确认 → 网关 → PM审核 → 网关 → 结束/驳回终止 |
| `settlementApproval` | 结算审批流程 | `settlement-approval.bpmn20.xml` | 开始 → PM审核 → 网关 → 财务审核 → 网关 → 结束/驳回终止 |
| `changeRequestApproval` | 变更请求CCB审批流程 | `change-request-approval.bpmn20.xml` | 开始 → CCB审核 → 网关 → 结束/驳回终止 |
| `demo_network_cutover` | 网络割接流程 | `network-cutover.bpmn20.xml` | 开始 → 风险方案审核 → 割接窗口确认 → 实施割接 → 业务验证 → 网关 → 复盘归档/执行回退 → 结束 |

**通用特征**：
- 所有审批流程使用 `flowable:skipExpression="${assignee == initiator}"` 跳过自审
- 审批任务挂载 `oaTaskListener` 镜像致远 OA
- 驳回走 `terminateEventDefinition` 终止结束事件

---

## 附录 E：SPI 接口清单

平台通过 12 个 SPI 接口实现跨模块解耦：

| SPI 接口 | 提供方 | 消费方 | 用途 |
|----------|--------|--------|------|
| `ApprovalTrigger` | `pms-workflow` | `pms-project` / `pms-baseline` | 触发审批 |
| `ApprovalStatusChecker` | `pms-workflow` | `pms-project` | 查询审批状态 |
| `ApprovalPlanBatchCreator` | `pms-workflow` | `pms-project` | 批量创建阶段审批计划 |
| `MandatoryDeliverableValidator` | `pms-deliverable` | `pms-project` | 阶段退出强制交付件校验 |
| `DeliverableBatchCreator` | `pms-deliverable` | `pms-project` | 模板深拷贝创建交付件 |
| `DependencyBatchCreator` | `pms-baseline` | `pms-project` | 模板深拷贝创建任务依赖 |
| `BusinessFileStorage` | `pms-file` | 多模块 | 业务文件存储抽象 |
| `NotificationService` | `pms-notification` | 多模块 | 通知发送委托 |
| `ProjectProgressProvider` | `pms-project` | `pms-admin` | 项目进度查询 |
| `WorkflowService` | `pms-workflow` | `pms-governance` / `pms-lowcode` | 工作流操作 |
| `IntegrationLogService` | `pms-integration` | 多模块 | 集成日志记录 |
| `OaIntegrationService` | `pms-integration` | `pms-workflow` | OA 待办镜像 |

---

## 修订记录

| 版本 | 日期 | 修订人 | 修订内容 |
|------|------|--------|----------|
| v1.0.0 | 2026-07-22 | PMS 研发组 | 初始版本，覆盖 14 模块 80+ 端点 |

---

**文档结束**