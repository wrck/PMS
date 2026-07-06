# API 规范

> 网络设备工程项目管理系统（network-equipment-pms）后端 API 规范。
> 涵盖鉴权、响应格式、分页、错误处理、限流、幂等、字段加密与各模块接口示例。

## 目录

1. [API 总览](#1-api-总览)
2. [鉴权机制](#2-鉴权机制)
3. [通用响应格式](#3-通用响应格式)
4. [分页规范](#4-分页规范)
5. [错误规范](#5-错误规范)
6. [限流规范](#6-限流规范)
7. [幂等性规范](#7-幂等性规范)
8. [字段加密规范](#8-字段加密规范)
9. [关键接口示例](#9-关键接口示例)
10. [SpringDoc / Swagger UI](#10-springdoc--swagger-ui)
11. [API 版本管理策略](#11-api-版本管理策略)

---

## 1. API 总览

所有业务接口统一以 `/api` 为前缀，按模块分组。基础信息：

| 项 | 值 |
|----|-----|
| Base URL | `https://pms.example.com/api` |
| 协议 | HTTPS（TLS 1.2/1.3） |
| 数据格式 | `application/json; charset=UTF-8` |
| 鉴权 | JWT Bearer Token |
| 时区 | Asia/Shanghai（UTC+8） |
| 时间格式 | `yyyy-MM-dd HH:mm:ss` 或 ISO-8601 |

### 1.1 模块分组

| 分组 | 路径前缀 | 模块 | 说明 |
|------|----------|------|------|
| system | `/api/auth`、`/api/system`、`/api/menu`、`/api/role`、`/api/user`、`/api/dept` | pms-system | 用户、角色、菜单、部门、登录鉴权 |
| project | `/api/project`、`/api/project-member`、`/api/project-milestone` | pms-project | 项目、成员、里程碑 |
| asset | `/api/asset`、`/api/asset-transfer`、`/api/rma`、`/api/warranty` | pms-asset | 资产、调拨、RMA、质保 |
| implementation | `/api/implementation`、`/api/task`、`/api/schedule` | pms-implementation | 实施任务、进度、排程 |
| workflow | `/api/workflow`、`/api/process` | pms-workflow | Flowable 工作流 |
| integration | `/api/integration/d365`、`/api/integration/fp`、`/api/integration/oa` | pms-integration | D365/FP/OA 集成 |
| file | `/api/file` | pms-file | 文件上传下载 |
| notification | `/api/notification` | pms-notification | 通知、消息 |
| governance | `/api/governance`、`/api/audit`、`/api/config` | pms-governance | 审计、配置治理 |
| lowcode | `/api/lowcode/form`、`/api/lowcode/list`、`/api/lowcode/tab`、`/api/lowcode/related-page`、`/api/lowcode/permission` | pms-lowcode | 低代码页面配置与权限 |
| ops | `/api/ops/alert` | pms-admin | 运维告警接收（Alertmanager webhook） |

### 1.2 通用请求头

| Header | 必填 | 说明 |
|--------|------|------|
| `Authorization` | 是（除登录/验证码） | `Bearer <JWT>` |
| `Content-Type` | 是（POST/PUT） | `application/json` |
| `X-Idempotent-Key` | 否 | 幂等键（幂等接口建议携带），见 [§7](#7-幂等性规范) |
| `X-Trace-Id` | 否 | 链路追踪 ID（缺失则后端生成） |
| `Accept-Language` | 否 | 国际化（zh-CN / en-US） |

---

## 2. 鉴权机制

### 2.1 JWT Bearer Token

- **算法**：HS256
- **密钥**：`JWT_SECRET`（环境变量，≥32 字符）
- **有效期**：24 小时（`jwt.expiration=86400000`）
- **载荷（Claims）**：`userId`、`username`、`roles`、`iat`、`exp`

### 2.2 登录接口

```bash
curl -X POST https://pms.example.com/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "admin",
    "password": "<明文密码>",
    "captcha": "a1b2",
    "captchaKey": "uuid-from-captcha-api"
  }'
```

**响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJyb2xlcyI6WyJhZG1pbiJdLCJpYXQiOjE3Nzc4MDAwMDAsImV4cCI6MTc3Nzg4NjQwMH0.signature",
    "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4...",
    "expiresIn": 86400000,
    "userInfo": {
      "userId": 1,
      "username": "admin",
      "nickname": "管理员",
      "roles": ["admin"],
      "permissions": ["*:*:*"]
    }
  }
}
```

### 2.3 获取验证码

```bash
curl https://pms.example.com/api/auth/captcha
```

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "key": "550e8400-e29b-41d4-a716-446655440000",
    "image": "data:image/png;base64,iVBORw0KGgo..."
  }
}
```

### 2.4 Token 刷新

```bash
curl -X POST https://pms.example.com/api/auth/refresh \
  -H 'Content-Type: application/json' \
  -d '{"refreshToken": "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4..."}'
```

### 2.5 Token 失效场景

| 场景 | HTTP | Result.code | 处理 |
|------|------|-------------|------|
| 未携带 Token | 401 | 401 | 跳转登录 |
| Token 签名错误 | 401 | 1002 | 跳转登录 |
| Token 已过期 | 401 | 1003 | 用 refreshToken 刷新，失败则跳转登录 |
| Token 用户被禁用 | 200 | 1005 | 联系管理员 |

> **注意**：`JWT_SECRET` 轮换会使所有已签发 Token 失效（含有效期内），用户需重新登录。

---

## 3. 通用响应格式

### 3.1 Result<T> 结构

来源：`com.dp.plat.common.result.Result`

```java
public class Result<T> implements Serializable {
    public static final int SUCCESS_CODE = 200;
    public static final int ERROR_CODE = 500;

    private int code;        // 业务码（200 成功，其他失败）
    private String message;  // 提示信息
    private T data;          // 业务数据

    public boolean isSuccess() { return this.code == SUCCESS_CODE; }
}
```

### 3.2 成功响应

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { /* 业务数据 */ }
}
```

### 3.3 失败响应

```json
{
  "code": 1001,
  "message": "项目编号已存在",
  "data": null
}
```

### 3.4 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | int | 业务码：200 成功；4xx/5xx HTTP 错误；1xxx 业务错误 |
| `message` | string | 面向用户的提示信息（i18n 友好） |
| `data` | T \| null | 业务数据，失败时为 null |

### 3.5 客户端校验建议

客户端应同时检查：
1. HTTP 状态码（网络/网关层错误）
2. `Result.code === 200`（业务成功）
3. `Result.data`（业务数据）

```javascript
// 前端拦截器示例
axios.interceptors.response.use(res => {
  const result = res.data;
  if (result.code === 200) return result.data;
  if (result.code === 1003) { /* Token 过期，刷新 */ }
  ElMessage.error(result.message);
  return Promise.reject(result);
});
```

---

## 4. 分页规范

### 4.1 请求参数（PageQuery）

分页查询接口统一接收以下 query 参数：

| 参数 | 类型 | 默认 | 说明 |
|------|------|------|------|
| `current` | long | 1 | 当前页码（从 1 开始） |
| `size` | long | 10 | 每页条数 |
| `orderBy` | string | （空） | 排序字段，如 `create_time desc` |

示例：

```
GET /api/project?current=2&size=20&orderBy=create_time desc&projectName=网络
```

### 4.2 响应结构（PageResult）

使用 MyBatis-Plus `IPage<T>`：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      { "id": 1, "projectCode": "P2026001", "projectName": "网络改造项目" },
      { "id": 2, "projectCode": "P2026002", "projectName": "数据中心建设" }
    ],
    "total": 1280,
    "size": 20,
    "current": 2,
    "pages": 64
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `records` | array | 当前页数据列表 |
| `total` | long | 总记录数 |
| `size` | long | 每页条数 |
| `current` | long | 当前页码 |
| `pages` | long | 总页数 |

### 4.3 分页安全限制

- 单页最大 `size=100`，超出按 100 处理
- `current` 必须 ≥ 1
- `orderBy` 字段需在白名单内，防 SQL 注入

---

## 5. 错误规范

### 5.1 错误分类与处理

来源：`com.dp.plat.common.exception.GlobalExceptionHandler`

| 错误类型 | 异常 | HTTP | Result.code | 触发场景 |
|----------|------|------|-------------|----------|
| 业务异常 | `BusinessException` | 200 | 自定义 | 业务校验失败 |
| 校验异常 | `MethodArgumentNotValidException` / `BindException` / `ConstraintViolationException` | 400 | 400 | `@Valid` 校验失败 |
| 集成异常 | `IntegrationException` | 503 | 503 | D365/FP/OA 调用失败或熔断 |
| 乐观锁冲突 | `OptimisticLockingFailureException` | 409 | 409 | MyBatis-Plus version 不匹配 |
| 限流 | `RateLimitExceededException` | 429 | 429 | `@RateLimit` 超限（含 `Retry-After` 头） |
| 权限 | `AccessDeniedException` | 403 | 403 | `@PreAuthorize` 不通过 |
| 方法不支持 | `HttpRequestMethodNotSupportedException` | 405 | 405 | HTTP 方法错误 |
| 兜底 | `Exception` | 500 | 500 | 未捕获异常 |

### 5.2 业务异常示例

```java
// Service 层抛出
if (projectMapper.selectByCode(req.getProjectCode()) != null) {
    throw new BusinessException("项目编号已存在：" + req.getProjectCode());
}
// 或用 ResultCode
throw new BusinessException(ResultCode.BUSINESS_ERROR, "项目编号已存在");
// 或自定义 code
throw new BusinessException(1001, "项目编号已存在");
```

响应：

```json
{
  "code": 1001,
  "message": "项目编号已存在：P2026001",
  "data": null
}
```

### 5.3 校验异常示例

请求：

```bash
curl -X POST https://pms.example.com/api/project \
  -H 'Authorization: Bearer <token>' \
  -H 'Content-Type: application/json' \
  -d '{"projectName": ""}'
```

响应（400）：

```json
{
  "code": 400,
  "message": "项目编号不能为空; 项目名称不能为空",
  "data": null
}
```

> 多个字段校验失败时，message 以 `; ` 拼接所有错误。

### 5.4 集成异常示例

```json
HTTP 503
{
  "code": 503,
  "message": "D365 集成服务暂不可用：熔断器已开启",
  "data": null
}
```

### 5.5 乐观锁冲突示例

```json
HTTP 409
{
  "code": 409,
  "message": "数据已被其他用户修改，请刷新后重试",
  "data": null
}
```

### 5.6 完整错误码表

详见 [故障排查手册 - 错误码对照表](./troubleshooting.md#2-错误码对照表)。

---

## 6. 限流规范

### 6.1 @RateLimit 注解

来源：`com.dp.plat.common.annotation.RateLimit`

基于 Bucket4j 令牌桶 + Redis 分布式存储实现。

```java
@RateLimit(
    key = "#request.projectId",  // SpEL，留空则用「方法签名+参数哈希」
    capacity = 10,                // 令牌桶容量（最大突发）
    refillTokens = 10,            // 每周期补充令牌数
    refillPeriodSeconds = 60,     // 补充周期（秒）
    message = "请求过于频繁，请稍后再试"
)
@PostMapping
public Result<Project> create(@RequestBody ProjectCreateRequest request) { ... }
```

| 属性 | 默认 | 说明 |
|------|------|------|
| `key` | `""` | SpEL 表达式，支持 `#userId`、`#request.xxx`、`#id` |
| `capacity` | 100 | 令牌桶容量 |
| `refillTokens` | 100 | 每周期补充令牌数 |
| `refillPeriodSeconds` | 60 | 补充周期 |
| `message` | `请求过于频繁，请稍后再试` | 触发时提示 |

### 6.2 限流响应

触发限流时返回 HTTP 429 + `Retry-After` 头：

```http
HTTP/1.1 429 Too Many Requests
Content-Type: application/json
Retry-After: 30

{
  "code": 429,
  "message": "请求过于频繁，请稍后再试",
  "data": null
}
```

### 6.3 客户端处理

```javascript
// 按 Retry-After 退避重试
if (error.response.status === 429) {
  const retryAfter = error.response.headers['retry-after'] || 30;
  setTimeout(() => retryRequest(), retryAfter * 1000);
}
```

### 6.4 限流维度建议

| 接口类型 | 限流维度 | 建议 |
|----------|----------|------|
| 登录 | IP / username | 5 次/分钟，防爆破 |
| 验证码 | IP | 10 次/分钟 |
| 文件上传 | userId | 30 次/分钟 |
| 查询类 | userId | 100 次/分钟 |
| 集成触发 | system | 50 次/秒（与 Resilience4j RateLimiter 协同） |

---

## 7. 幂等性规范

### 7.1 @Idempotent 注解

来源：`com.dp.plat.common.annotation.Idempotent`

基于 Redis SETNX（`SET key value NX EX ttl`）实现。

```java
@Idempotent                          // 默认：从 X-Idempotent-Key 头读取，TTL 60s，REJECT
@PostMapping
public Result<Project> create(@RequestBody ProjectCreateRequest request) { ... }

@Idempotent(ttl = 120, policy = Idempotent.Policy.RETURN_FIRST_RESULT)
@PostMapping("/expensive")
public Result<?> expensiveAction(@RequestBody ActionRequest request) { ... }

@Idempotent(key = "#request.projectId + ':' + #request.action")
@PostMapping("/act")
public Result<?> act(@RequestBody ActionRequest request) { ... }
```

| 属性 | 默认 | 说明 |
|------|------|------|
| `key` | `""` | SpEL；留空则从 `X-Idempotent-Key` 请求头读取 |
| `ttl` | 60 | 幂等键 TTL（秒），应略大于业务方法最大执行时间 |
| `policy` | `REJECT` | 重复请求策略：`REJECT`（拒绝 409）/ `RETURN_FIRST_RESULT`（返回首次结果） |
| `message` | `请勿重复提交` | REJECT 策略的提示 |

### 7.2 客户端使用

```javascript
// 前端拦截器：为 POST/PUT 请求自动生成幂等键
axios.interceptors.request.use(config => {
  if (['post', 'put'].includes(config.method)) {
    config.headers['X-Idempotent-Key'] = crypto.randomUUID();
  }
  return config;
});
```

### 7.3 重复提交响应

**REJECT 策略**：

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "code": 409,
  "message": "请勿重复提交",
  "data": null
}
```

> 注意：幂等冲突返回 HTTP 200，业务码 409，区别于乐观锁冲突（HTTP 409）。

**RETURN_FIRST_RESULT 策略**：直接返回首次执行的完整 `Result`。

### 7.4 幂等行为

| 请求序号 | 行为 |
|----------|------|
| 首次 | 抢占 Redis 锁 → 执行业务 → 保存/更新键值 → 返回结果 |
| 重复（业务执行中） | 抢占失败 → REJECT 抛 409 / RETURN_FIRST_RESULT 返回首次结果（如已保存） |
| 重复（业务已完成） | 同上，RETURN_FIRST_RESULT 返回首次结果 |
| 业务异常 | 删除幂等键，允许客户端重试 |

### 7.5 适用接口

| 接口 | 是否幂等 | 说明 |
|------|----------|------|
| 创建（POST） | 建议 | 防止网络重试导致重复创建 |
| 更新（PUT） | 视情况 | 乐观锁已防并发，幂等防网络重试 |
| 删除（DELETE） | 天然幂等 | 一般不需 |
| 查询（GET） | 天然幂等 | 不需 |
| 支付/审批等副作用 | 必须 | 防止重复扣款/重复审批 |

---

## 8. 字段加密规范

### 8.1 @FieldEncrypt 注解

来源：`com.dp.plat.common.annotation.FieldEncrypt`

```java
@Entity
public class SysUser {
    @FieldEncrypt                    // 默认 AES/GCM/NoPadding
    private String phone;

    @FieldEncrypt
    private String idCard;
}
```

| 属性 | 默认 | 说明 |
|------|------|------|
| `algorithm` | `AES/GCM/NoPadding` | 加密算法（AES-256-GCM） |
| `key` | `app.security.encrypt-key` | application.yml 中的密钥配置项名 |

### 8.2 加解密机制

- **写入数据库**：MyBatis `EncryptTypeHandler` 自动加密
- **读取数据库**：MyBatis `EncryptTypeHandler` 自动解密
- **兜底**：`FieldEncryptAspect` 对未经过 TypeHandler 的返回对象（如跨服务调用）执行解密

### 8.3 密文格式

```
Base64( IV(12B) || ciphertext + GCM tag(16B) )
```

- 每次加密生成随机 IV（12 字节）
- 相同明文每次密文不同，防频率分析
- GCM 模式自带完整性校验

### 8.4 密钥

- **配置**：`APP_ENCRYPT_KEY` 环境变量，Base64 编码的 32 字节（256 位）
- **生成**：`openssl rand -base64 32`
- **⚠️ 不可更改**：配置后绝不可更换，否则历史加密数据无法解密
- **轮换**：必须编写迁移脚本（旧密钥解密 → 新密钥加密）

### 8.5 接口表现

```bash
# 写入
curl -X POST https://pms.example.com/api/user \
  -H 'Authorization: Bearer <token>' \
  -d '{"username": "zhangsan", "phone": "13800138000", "idCard": "110101199001011234"}'

# 数据库存储（密文）
# phone: 'ZGlkjF8x...='  (Base64)
# idCard: 'c2VjcmV0...='  (Base64)

# 读取（自动解密）
curl https://pms.example.com/api/user/1
# 响应：phone: "13800138000", idCard: "110101199001011234"
```

> **安全提示**：加密字段在 API 响应中返回明文。如需对前端也隐藏，需额外做权限控制（`@JsonSerialize` 脱敏）。

---

## 9. 关键接口示例

### 9.1 system 模块 — 用户登录

详见 [§2.2](#22-登录接口)。

### 9.2 project 模块 — 创建项目

```bash
curl -X POST https://pms.example.com/api/project \
  -H 'Authorization: Bearer <token>' \
  -H 'Content-Type: application/json' \
  -H 'X-Idempotent-Key: 550e8400-e29b-41d4-a716-446655440000' \
  -d '{
    "projectCode": "P2026001",
    "projectName": "网络改造项目",
    "projectType": "NETWORK_DEVICE",
    "customerName": "某公司",
    "planStartDate": "2026-07-01",
    "planEndDate": "2026-12-31",
    "contractAmount": 1000000,
    "priority": 3,
    "description": "数据中心网络改造"
  }'
```

**响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1281,
    "projectCode": "P2026001",
    "projectName": "网络改造项目",
    "projectType": "NETWORK_DEVICE",
    "customerName": "某公司",
    "planStartDate": "2026-07-01",
    "planEndDate": "2026-12-31",
    "contractAmount": 1000000,
    "priority": 3,
    "status": "DRAFT",
    "createTime": "2026-07-06 10:30:00",
    "createBy": "admin"
  }
}
```

### 9.3 asset 模块 — 资产列表查询

```bash
curl -X GET 'https://pms.example.com/api/asset?current=1&size=20&projectId=1281&assetName=路由器' \
  -H 'Authorization: Bearer <token>'
```

**响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 15681,
        "assetCode": "A20260001",
        "assetName": "核心路由器",
        "projectId": 1281,
        "status": "IN_STOCK",
        "serialNumber": "SN001"
      }
    ],
    "total": 15680,
    "size": 20,
    "current": 1,
    "pages": 784
  }
}
```

### 9.4 implementation 模块 — 任务办理

```bash
curl -X POST https://pms.example.com/api/task/12345/complete \
  -H 'Authorization: Bearer <token>' \
  -H 'Content-Type: application/json' \
  -d '{
    "taskId": 12345,
    "comment": "已完成现场勘察",
    "variables": {
      "surveyResult": "PASS",
      "surveyNote": "符合部署条件"
    }
  }'
```

**响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "taskId": 12345,
    "status": "COMPLETED",
    "nextTaskId": 12346,
    "nextAssignee": "lisi",
    "processInstanceId": "PI20260706001"
  }
}
```

### 9.5 workflow 模块 — 启动流程

```bash
curl -X POST https://pms.example.com/api/workflow/start \
  -H 'Authorization: Bearer <token>' \
  -H 'Content-Type: application/json' \
  -d '{
    "processDefinitionKey": "project_approval",
    "businessKey": "P2026001",
    "variables": {
      "projectId": 1281,
      "projectManager": "zhangsan",
      "amount": 1000000
    }
  }'
```

**响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "processInstanceId": "PI20260706001",
    "currentTask": {
      "taskId": 12345,
      "taskName": "项目经理审批",
      "assignee": "zhangsan"
    }
  }
}
```

### 9.6 integration 模块 — D365 同步项目

```bash
curl -X POST https://pms.example.com/api/integration/d365/sync-project \
  -H 'Authorization: Bearer <token>' \
  -H 'Content-Type: application/json' \
  -d '{"projectId": 1281}'
```

**成功响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "projectId": 1281,
    "d365ProjectId": "D365-PROJ-001",
    "syncStatus": "SUCCESS",
    "syncTime": "2026-07-06 10:35:00"
  }
}
```

**集成失败响应（503）**：

```json
{
  "code": 503,
  "message": "D365 集成服务暂不可用：熔断器已开启",
  "data": null
}
```

### 9.7 file 模块 — 文件上传

```bash
curl -X POST https://pms.example.com/api/file/upload \
  -H 'Authorization: Bearer <token>' \
  -F 'file=@/path/to/document.pdf' \
  -F 'businessType=PROJECT' \
  -F 'businessId=1281'
```

**响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "fileId": "f_550e8400-e29b-41d4-a716-446655440000",
    "fileName": "document.pdf",
    "fileSize": 1048576,
    "fileType": "application/pdf",
    "url": "/api/file/f_550e8400-e29b-41d4-a716-446655440000/download"
  }
}
```

> 文件大小受 Nginx `client_max_body_size`（默认 100m）与 Spring `spring.servlet.multipart.max-file-size` 限制。

### 9.8 notification 模块 — 发送通知

```bash
curl -X POST https://pms.example.com/api/notification/send \
  -H 'Authorization: Bearer <token>' \
  -H 'Content-Type: application/json' \
  -d '{
    "channel": "WEB",
    "receiverIds": [1, 2, 3],
    "title": "项目待审批",
    "content": "项目 P2026001 待您审批",
    "businessType": "PROJECT_APPROVAL",
    "businessId": "1281"
  }'
```

### 9.9 governance 模块 — 审计日志查询

```bash
curl -X GET 'https://pms.example.com/api/audit?current=1&size=20&module=PROJECT&operation=CREATE&startTime=2026-07-01&endTime=2026-07-06' \
  -H 'Authorization: Bearer <token>'
```

**响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "module": "PROJECT",
        "operation": "CREATE",
        "method": "POST",
        "requestUrl": "/api/project",
        "requestParams": "{\"projectCode\":\"P2026001\"}",
        "userId": 1,
        "username": "admin",
        "ip": "10.0.0.1",
        "costTime": 120,
        "operateTime": "2026-07-06 10:30:00"
      }
    ],
    "total": 50,
    "size": 20,
    "current": 1,
    "pages": 3
  }
}
```

### 9.10 lowcode 模块 — 创建表单配置

```bash
curl -X POST https://pms.example.com/api/lowcode/form \
  -H 'Authorization: Bearer <token>' \
  -H 'Content-Type: application/json' \
  -d '{
    "code": "project_create_form",
    "name": "项目创建表单",
    "description": "用于创建网络设备工程项目",
    "bizType": "PROJECT",
    "status": "DRAFT",
    "version": 1,
    "formConfig": {
      "title": "项目创建",
      "labelWidth": 110,
      "labelPosition": "right",
      "size": "default",
      "fields": [
        {
          "id": "field_1",
          "type": "input",
          "label": "项目编号",
          "prop": "projectCode",
          "required": true,
          "span": 12,
          "props": { "maxlength": 32 }
        }
      ],
      "layout": { "type": "grid", "gutter": 16 }
    }
  }'
```

> 低代码模块完整接口详见 [低代码使用指南](./lowcode-guide.md)。

### 9.11 ops 模块 — Alertmanager webhook

```bash
curl -X POST https://pms.example.com/api/ops/alert/webhook \
  -H 'Content-Type: application/json' \
  -d '{
    "version": "4",
    "groupKey": "{}:{alertname=\"APIErrorRateHigh\"}",
    "status": "firing",
    "alerts": [
      {
        "status": "firing",
        "labels": {
          "alertname": "APIErrorRateHigh",
          "severity": "critical",
          "application": "pms"
        },
        "annotations": {
          "summary": "API 错误率过高",
          "description": "5xx 错误率 8% 超过 5%"
        },
        "startsAt": "2026-07-06T10:30:00Z",
        "fingerprint": "a1b2c3d4"
      }
    ]
  }'
```

> 此接口由 Alertmanager 调用，后端分发到邮件/钉钉/企业微信。

---

## 10. SpringDoc / Swagger UI

### 10.1 访问方式

通过 `springdoc-openapi-starter-webmvc-ui` 集成：

| 环境 | Swagger UI | OpenAPI JSON |
|------|-----------|--------------|
| dev / test | `http://localhost:8080/swagger-ui.html` | `http://localhost:8080/v3/api-docs` |
| prod | **已禁用**（`springdoc.swagger-ui.enabled=false`） | **已禁用** |

> 生产环境关闭 Swagger 防止接口泄露。如需临时启用，设 `springdoc.swagger-ui.enabled=true` 后重启。

### 10.2 配置

`application.yml`：

```yaml
springdoc:
  swagger-ui:
    path: /swagger-ui.html
    enabled: true              # prod profile 中设为 false
  api-docs:
    path: /v3/api-docs
    enabled: true              # prod profile 中设为 false
```

### 10.3 注解使用

```java
@Tag(name = "项目管理", description = "Project management APIs")
@RestController
@RequestMapping("/api/project")
public class ProjectController {

    @Operation(summary = "分页查询项目")
    @GetMapping
    public Result<IPage<Project>> page(...) { ... }

    @Operation(summary = "创建项目")
    @PostMapping
    public Result<Project> create(@RequestBody @Valid ProjectCreateRequest request) { ... }
}
```

### 10.4 前端 Nginx 代理

`nginx.conf` 已配置 Swagger UI 反代（开发调试用）：

```nginx
location /swagger-ui/ {
    proxy_pass http://backend:8080;
    proxy_set_header Host $host;
}
location /v3/api-docs {
    proxy_pass http://backend:8080;
    proxy_set_header Host $host;
}
```

---

## 11. API 版本管理策略

### 11.1 当前策略

项目当前采用**无版本号 URL**策略，所有接口以 `/api/<module>` 为前缀。

### 11.2 兼容性承诺

| 变更类型 | 是否兼容 | 处理 |
|----------|----------|------|
| 新增接口 | ✅ 兼容 | 直接新增 |
| 新增响应字段 | ✅ 兼容 | 客户端忽略未知字段 |
| 新增可选请求字段 | ✅ 兼容 | 默认值保证旧行为 |
| 修改字段语义 | ❌ 破坏 | 需新接口或版本号 |
| 删除字段 | ❌ 破坏 | 需新接口或版本号 |
| 改变 HTTP 方法 | ❌ 破坏 | 需新接口 |

### 11.3 破坏性变更处理

当必须做破坏性变更时，采用以下策略之一：

**策略 A：新增接口（推荐）**

```
旧：POST /api/project
新：POST /api/project/v2   （仅破坏性变更时）
```

**策略 B：URL 版本前缀**

```
旧：/api/project
新：/api/v2/project
```

**策略 C：Header 版本（不推荐）**

```
X-API-Version: 2
```

### 11.4 弃用流程

1. 在旧接口标注 `@Deprecated`，Swagger 显示弃用标记
2. 通知所有客户端迁移
3. 观察旧接口调用量（通过审计日志）
4. 调用量归零后下线（建议保留 2 个版本周期）

### 11.5 变更记录

建议在 `docs/CHANGELOG.md`（或 release notes）记录每次 API 变更：

```markdown
## v1.2.0 (2026-07-06)

### Added
- POST /api/project/{id}/members 批量添加项目成员
- GET /api/asset/warranty/check 质保检查

### Changed
- GET /api/project 响应新增 `priority` 字段（兼容）

### Deprecated
- GET /api/project/list （改用 GET /api/project，保留至 v1.3.0）

### Removed
- 无
```

---

## 相关文件

| 文件 | 说明 |
|------|------|
| `pms-common/.../result/Result.java` | 统一响应包装 |
| `pms-common/.../result/ResultCode.java` | 错误码枚举 |
| `pms-common/.../exception/GlobalExceptionHandler.java` | 全局异常处理 |
| `pms-common/.../exception/BusinessException.java` | 业务异常 |
| `pms-common/.../exception/IntegrationException.java` | 集成异常 |
| `pms-common/.../annotation/RateLimit.java` | 限流注解 |
| `pms-common/.../annotation/Idempotent.java` | 幂等注解 |
| `pms-common/.../annotation/FieldEncrypt.java` | 字段加密注解 |
| `pms-common/.../crypto/AesGcmEncryptor.java` | AES-256-GCM 加密器 |
| `pms-common/.../aspect/RateLimitAspect.java` | 限流切面 |
| `pms-common/.../aspect/IdempotentAspect.java` | 幂等切面 |
| `pms-common/.../aspect/FieldEncryptAspect.java` | 字段加密切面 |
| `pms-admin/.../application.yml` | SpringDoc 配置 |

> **相关文档**：[部署指南](./deployment.md) | [运维手册](./operations.md) | [故障排查手册](./troubleshooting.md) | [低代码使用指南](./lowcode-guide.md)
