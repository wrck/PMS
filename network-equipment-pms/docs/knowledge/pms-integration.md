# pms-integration 模块知识库

> 源码路径：`/workspace/network-equipment-pms/pms-integration`
> 基础包名：`com.dp.plat.integration`
> 父项目：`com.dp.plat:network-equipment-pms:1.0.0-SNAPSHOT`
> 模块描述（pom.xml）：`External system integration (D365, FP)`

---

## 模块概述

**定位**：`pms-integration` 是 `network-equipment-pms` 多模块工程中的**外部系统集成领域模块**，负责 PMS 与企业内外部第三方系统的双向对接。所有跨越 PMS 边界的对外调用（OAuth2 鉴权、REST 请求、健康探测、失败重试、熔断降级）均收敛于本模块，使上层业务模块（`pms-implementation`、`pms-workflow`）无需感知外部系统的协议、地址、凭据与容错细节。

**核心职责**：

1. **多系统适配**：为 Microsoft Dynamics 365（D365，ERP）、Financial Platform（FP，财务平台）、致远 OA（Seeyon OA，待办协同）三套外部系统提供统一的客户端适配层，封装各自的 OAuth2 鉴权、REST 端点与业务报文格式。
2. **分布式 OAuth2 Token 管理**：通过 `OAuthTokenCache`（基于 Redis Hash）实现跨实例共享的 Token 缓存，配合 `TokenRefreshLock`（Redis SETNX + Lua 解锁）实现单飞刷新、提前续期、失败告警，避免多实例并发刷新导致 Token 端点雪崩。
3. **弹性容错**：基于 Resilience4j 2.2.0 为每个外部系统独立配置熔断器（CircuitBreaker）、隔离舱（Bulkhead）、限流器（RateLimiter）、重试器（Retry）四层保护，由 `IntegrationConfig` 在启动时统一注册事件监听器输出业务级日志。
4. **可观测集成日志**：所有外部调用均落库到 `pms_integration_log`，记录请求 URL、请求体、响应状态、响应体、错误信息、重试次数与下次重试时间，支持按日志 ID 手动重试与定时调度重试。
5. **健康检查聚合**：`IntegrationHealthController` 聚合 D365 / FP / OA 三个适配器的健康快照，输出 `HEALTHY` / `DEGRADED` / `DOWN` 总体状态，供运维大盘与告警系统消费。
6. **跨模块解耦**：模块仅依赖 `pms-common`，对 `pms-implementation` 的资产 / 结算 Mapper 通过 `ApplicationContext` 反射查找（`lookupMapper`）规避模块依赖环。

**打包类型**：默认 `jar`（pom.xml 未显式声明 packaging，Maven 默认 `jar`）。

**artifactId / name**：`pms-integration`。

**在依赖图中的位置**：底层领域模块，仅依赖 `pms-common`；被 `pms-implementation`、`pms-workflow`、`pms-admin` 三个上层模块依赖。

---

## 包结构

模块基础包 `com.dp.plat.integration`，源码目录 `src/main/java/com/dp/plat/integration/`，按职责拆分为 12 个子包：

```
com.dp.plat.integration
├── config/         # 配置类：外部系统属性绑定 + Resilience4j 事件监听
├── constant/       # 常量：日志类型、业务类型、状态枚举
├── controller/     # REST API 端点（D365 / FP / OA / 通用管理）
├── d365/
│   ├── entity/     # D365 持久化实体（发票、采购收货）
│   └── mapper/     # D365 MyBatis-Plus Mapper
├── dto/            # 跨层数据传输对象（健康快照、OCR 结果、支付回调）
├── entity/         # 通用集成日志实体
├── health/         # 聚合健康检查 Controller
├── mapper/         # 集成日志 Mapper
├── model/          # 外部系统报文模型（请求 / 响应体）
│   ├── d365/       # D365 报文：采购单、采购收货、Token
│   ├── fp/         # FP 报文：结算推送、Token、通用响应
│   └── oa/         # OA 报文：待办推送、Token
├── oauth/          # 分布式 OAuth2 Token 缓存与刷新锁
├── service/        # Service 接口
│   └── impl/       # Service 实现
└── package-info.java
```

测试源码位于 `src/test/java/com/dp/plat/integration/`，包含 `D365CircuitBreakerTest`（验证熔断器状态机：CLOSED → OPEN → HALF_OPEN → CLOSED）、`OaRetryTest`（验证 OA 重试行为）、`oauth/RedisOAuthTokenCacheTest`、`oauth/TokenRefreshLockTest`，并通过 `src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker` 启用 `mockito-inline` 以支持 final / static 方法 mock。

---

## 集成的外部系统清单

| 系统名 | 系统全称 | 集成方式 | 鉴权 | 用途 | 模块内对应常量 |
|--------|----------|----------|------|------|----------------|
| **D365** | Microsoft Dynamics 365（ERP） | REST API（`RestTemplate` HTTP GET / POST） | OAuth2 `client_credentials` | 采购单 / 采购收货 / 资产序列号 / 发票的双向同步与推送 | `LOG_TYPE_D365 = "D365"` |
| **FP** | Financial Platform（财务平台） | REST API（JSON POST + multipart 上传） | OAuth2 `client_credentials` | 结算单推送、发票图片 OCR 识别、支付回调接收 | `LOG_TYPE_FP = "FP"` |
| **OA** | 致远 OA（Seeyon OA） | REST API（JSON POST / PUT） | OAuth2 `client_credentials` | Flowable 用户任务的待办推送 / 完成 / 转办同步 | `LOG_TYPE_OA = "OA"` |
| **SMS** | 短信系统 | （预留） | — | 常量已声明（`LOG_TYPE_SMS`），暂未实现适配器 | `LOG_TYPE_SMS = "SMS"` |
| **EHR** | 人力资源系统 | （预留） | — | 常量已声明（`LOG_TYPE_EHR`），暂未实现适配器 | `LOG_TYPE_EHR = "EHR"` |

> 备注：仓库内提供 `mock-d365`、`mock-fp`、`mock-oa` 三个独立 Mock 服务模块（各自带 Dockerfile 与 `application.yml`），用于本地与 CI 环境模拟外部系统响应。

**配置前缀**（绑定到 `application.yml`）：

| 配置前缀 | 属性类 | 关键字段 |
|----------|--------|----------|
| `d365.*` | `D365Properties` | `baseUrl` / `tokenUrl` / `clientId` / `clientSecret` / `scope` / `grantType`（默认 `client_credentials`） |
| `fp.*` | `FpProperties` | `baseUrl` / `tokenUrl` / `clientId` / `clientSecret` |
| `integration.oa.*` | `OaProperties` | `baseUrl` / `tokenUrl` / `clientId` / `clientSecret` |
| `integration.retry.*` | `IntegrationProperties` | `interval`（默认 300000ms / 5 分钟）/ `maxRetry`（默认 3）/ `backoffMultiplier`（默认 2） |

---

## 集成客户端/适配器

模块未采用独立的 `XxxClient` 类，而是将客户端职责与业务编排合并到 `XxxIntegrationServiceImpl` 中。每个 Service 实现类即一个外部系统的适配器，通过 `RestTemplate integrationRestTemplate`（由 `IntegrationConfig` 统一注入，连接超时 10s，读取超时 30s）发起 HTTP 调用。

| 类名 | 目标系统 | 关键方法（public） | 弹性保护 |
|------|----------|---------------------|----------|
| `D365IntegrationServiceImpl` | D365 | `getAccessToken()` — 获取 OAuth2 Token（委托 `OAuthTokenCache`）<br>`pushPurchaseReceipt(PurchaseReceiptHeader)` — 推送采购收货<br>`pushPurchaseOrder(PurchaseHeader)` — 推送采购单<br>`syncPurchaseOrders()` — 同步采购单到本地（upsert `d365_purchase_receipt`）<br>`syncPurchaseReceipts()` — 同步采购收货并回填 `push_status=PUSHED`<br>`syncAssetSerialNumbers()` — 同步资产序列号（反射查找 `assetMapper` 更新 `pms_asset.serial_no`）<br>`syncInvoices()` — 同步发票（upsert `d365_invoice` + 反射更新 `pms_settlement.invoice_no`）<br>`retry(Long logId)` — 按日志 ID 重试<br>`healthCheck()` — 健康快照 | `@CircuitBreaker(d365CircuitBreaker)` + `@Bulkhead(d365Bulkhead)` + `@Retry(d365Retry)`，6 个 fallback 方法 |
| `FpIntegrationServiceImpl` | FP | `getAccessToken()` — 获取 OAuth2 Token<br>`pushSettlement(SettlementPushRequest)` — 推送结算单（首调同步 + 后台指数退避重试 1/2/4/8/16 分钟，最多 5 次）<br>`pushSettlementOnce(SettlementPushRequest)` — 单次推送（供调度器与重试调用）<br>`ocrInvoice(MultipartFile)` — 推送发票图片到 FP OCR，回写 `d365_invoice` 的 `ocr_status=RECOGNIZED`<br>`handlePaymentCallback(PaymentCallbackDto)` — 处理 FP 支付回调，更新 `pms_settlement.payment_status`<br>`retry(Long logId)` — 按日志 ID 重试<br>`healthCheck()` — 健康快照 | `@CircuitBreaker(fpCircuitBreaker)` + `@Bulkhead(fpBulkhead)` + `@Retry(fpRetry)`，3 个 fallback 方法 |
| `OaIntegrationServiceImpl` | 致远 OA | `getAccessToken()` — 获取 OAuth2 Token（剩余 < 5 分钟自动续期）<br>`pushTodo(OaTodoRequest)` — 推送待办（POST `/todo/push`）<br>`completeTodo(String businessKey)` — 完成待办（PUT `/todo/complete`）<br>`transferTask(String businessKey, String newHandlerUserId)` — 转办待办（PUT `/todo/transfer`）<br>`retry(Long logId)` — 按日志 ID 重试（按 `businessType` 推断 HTTP method）<br>`healthCheck()` — 健康快照 | `@CircuitBreaker(oaCircuitBreaker)` + `@Bulkhead(oaBulkhead)` + `@Retry(oaRetry)`，3 个 fallback 方法 |
| `RetryServiceImpl` | 跨系统 | `scheduledRetry()` — 定时调度（`@Scheduled(fixedDelayString = "${integration.retry.interval:300000}")`）扫描 `pms_integration_log` 中失败且到期的日志，按 `logType` 路由到对应适配器<br>`retryLog(Long logId)` — 手动重试单条日志 | 无注解（避免与适配器内的 `@Retry` 叠加） |
| `IntegrationLogServiceImpl` | 通用 | `log(IntegrationLog)` — 落库日志<br>`markSuccess(Long, String)` — 标记成功<br>`markFailed(Long, String)` — 标记失败并按指数退避计算 `nextRetryTime`<br>`getPendingRetryLogs()` — 查询待重试日志<br>`incrementRetryCount(Long)` — 递增重试计数<br>`list(int, int, IntegrationLog)` — 分页查询 | — |

> **统一约定**：所有 `XxxIntegrationServiceImpl.retry(Long logId)` 方法**不加** `@Retry` / `@CircuitBreaker` / `@Bulkhead` 注解，避免在调度重试之上叠加额外的 Resilience4j 重试层（参见 `OaIntegrationServiceImpl.retry` 的 Javadoc 说明）。

---

## 实体模型

模块内 3 张数据库表（均继承 `com.dp.plat.common.entity.BaseEntity`，含 `id` / `createTime` / `updateTime` / `createBy` / `updateBy` / `deleted` 通用字段）：

### 1. `pms_integration_log` — 集成调用日志

实体类：`com.dp.plat.integration.entity.IntegrationLog`，表名 `pms_integration_log`。

| 字段 | 类型 | 说明 |
|------|------|------|
| `logType` | `String` | 外部系统类型：`D365` / `FP` / `OA` / `SMS` / `EHR` |
| `businessType` | `String` | 业务类型：`PURCHASE_ORDER` / `PURCHASE_RECEIPT` / `SETTLEMENT` / `INVOICE` / `TODO_PUSH` / `TODO_COMPLETE` / `TODO_TRANSFER` / `OCR_INVOICE` / `PAYMENT_CALLBACK` |
| `businessId` | `String` | 关联业务记录 ID（如采购单号、结算单号、待办 businessKey） |
| `requestUrl` | `String` | 请求 URL（重试时复用） |
| `requestBody` | `String` | 请求体 JSON（重试时复用） |
| `responseStatus` | `String` | 响应状态：`SUCCESS` / `FAILED` / `PENDING` |
| `responseBody` | `String` | 响应体 JSON |
| `errorMessage` | `String` | 失败错误信息（截断至 1000 字符） |
| `retryCount` | `Integer` | 当前重试次数 |
| `maxRetry` | `Integer` | 最大重试次数（默认 3） |
| `nextRetryTime` | `LocalDateTime` | 下次重试时间（指数退避：`(retryCount + 1) * backoffMultiplier` 分钟后） |

Mapper：`IntegrationLogMapper extends BaseMapper<IntegrationLog>`。

### 2. `d365_invoice` — D365 发票

实体类：`com.dp.plat.integration.d365.entity.D365Invoice`，表名 `d365_invoice`。

| 字段 | 类型 | 说明 |
|------|------|------|
| `invoiceNo` | `String` | 发票号 |
| `settlementNo` | `String` | 关联结算单号 |
| `amount` | `BigDecimal` | 金额（不含税） |
| `taxAmount` | `BigDecimal` | 税额 |
| `totalAmount` | `BigDecimal` | 价税合计 |
| `invoiceDate` | `LocalDateTime` | 发票日期 |
| `vendorName` | `String` | 供应商名称 |
| `pushStatus` | `String` | 推送 D365 状态：`PENDING` / `PUSHED` / `FAILED` |
| `pushedAt` | `LocalDateTime` | 最后成功推送时间 |
| `d365InvoiceId` | `String` | D365 返回的发票标识 |
| `ocrStatus` | `String` | OCR 识别状态：`PENDING` / `RECOGNIZED` / `FAILED` |

Mapper：`D365InvoiceMapper extends BaseMapper<D365Invoice>`。被 `D365IntegrationServiceImpl.syncInvoices()` 与 `FpIntegrationServiceImpl.ocrInvoice()` 共同写入。

### 3. `d365_purchase_receipt` — D365 采购收货

实体类：`com.dp.plat.integration.d365.entity.D365PurchaseReceipt`，表名 `d365_purchase_receipt`。

| 字段 | 类型 | 说明 |
|------|------|------|
| `receiptNo` | `String` | 本地收货单号 |
| `poNo` | `String` | 关联采购单号 |
| `assetId` | `Long` | 关联资产 ID |
| `sn` | `String` | 资产序列号 |
| `quantity` | `BigDecimal` | 收货数量 |
| `receivedDate` | `LocalDateTime` | 收货日期 |
| `pushStatus` | `String` | 推送 D365 状态：`PENDING` / `PUSHED` / `FAILED` |
| `pushedAt` | `LocalDateTime` | 最后成功推送时间 |
| `d365ReceiptId` | `String` | D365 返回的收货标识 |

Mapper：`D365PurchaseReceiptMapper extends BaseMapper<D365PurchaseReceipt>`。由 `D365IntegrationServiceImpl.syncPurchaseOrders()` / `syncPurchaseReceipts()` 维护。

### 外部系统报文模型（非持久化，位于 `model/` 包）

| 包 | 类 | 用途 |
|----|------|------|
| `model.d365` | `PurchaseHeader` / `PurchaseLine` | 推送 D365 采购单的请求体（含采购单号、供应商、行项目） |
| `model.d365` | `PurchaseReceiptHeader` / `PurchaseReceiptLine` | 推送 D365 采购收货的请求体 |
| `model.d365` | `TokenRequest` / `TokenResponse` | D365 OAuth2 Token 请求 / 响应（`access_token` / `token_type` / `expires_in`） |
| `model.fp` | `SettlementPushRequest` / `SettlementPushDetail` | 推送 FP 结算单的请求体（结算单号、经办人、金额、明细列表） |
| `model.fp` | `FpResponse<T>` | FP 通用响应（`code` / `message` / `data`，`code="0"` 或 `"200"` 视为成功） |
| `model.fp` | `FpTokenResponse` | FP OAuth2 Token 响应 |
| `model.oa` | `OaTodoRequest` | OA 待办推送请求体（标题、内容、处理人、流程实例 ID、businessKey、businessType、流程 URL） |
| `model.oa` | `OaTokenResponse` | OA OAuth2 Token 响应 |

### DTO（位于 `dto/` 包）

| 类 | 用途 |
|----|------|
| `IntegrationHealthDto` | 聚合健康快照（D365 + FP + OA），含 `overallStatus`（`HEALTHY` / `DEGRADED` / `DOWN`）与 `lastCheckTime` |
| `D365HealthDto` / `FpHealthDto` / `OaHealthDto` | 各适配器健康快照：`connected` / `tokenValid` / `recentPushCount`（24h）/ `recentFailCount`（24h）/ `recentLogs`（最近 10 条） |
| `InvoiceOcrResult` | FP OCR 解析结果（发票号、金额、税额、价税合计、供应商名、原始响应） |
| `PaymentCallbackDto` | FP 支付回调载荷（结算单号、支付状态、支付时间、支付金额） |

---

## Service 层与 API 端点

### Service 接口清单

| 接口 | 实现类 | 职责 |
|------|--------|------|
| `D365IntegrationService` | `D365IntegrationServiceImpl` | D365 全部集成操作（推送 + 同步 + 重试 + 健康检查） |
| `FpIntegrationService` | `FpIntegrationServiceImpl` | FP 结算推送 / OCR / 支付回调 + 后台重试调度 + 健康检查 |
| `OaIntegrationService` | `OaIntegrationServiceImpl` | OA 待办推送 / 完成 / 转办 + 重试 + 健康检查 |
| `RetryService` | `RetryServiceImpl` | 定时调度重试 + 手动重试路由 |
| `IIntegrationLogService` | `IntegrationLogServiceImpl` | 集成日志 CRUD + 重试调度辅助 |

### REST API 端点清单

所有 Controller 使用 Spring MVC + Spring Security `@PreAuthorize` 权限控制 + `@OperLog` 操作日志注解 + Swagger `@Tag` / `@Operation` 文档。

#### D365 集成（`D365IntegrationController`，前缀 `/api/integration/d365`）

| HTTP | 路径 | 方法 | 权限 | 说明 |
|------|------|------|------|------|
| GET | `/health` | `health()` | — | D365 健康检查 |
| POST | `/push-receipt` | `pushReceipt(PurchaseReceiptHeader)` | `integration:d365:push` | 手动推送采购收货 |
| POST | `/sync/purchase-orders` | `syncPurchaseOrders()` | `integration:d365:sync` | 触发采购单同步 |
| POST | `/sync/purchase-receipts` | `syncPurchaseReceipts()` | `integration:d365:sync` | 触发采购收货同步 |
| POST | `/sync/asset-serial-numbers` | `syncAssetSerialNumbers()` | `integration:d365:sync` | 触发资产序列号同步 |
| POST | `/sync/invoices` | `syncInvoices()` | `integration:d365:sync` | 触发发票同步 |

#### FP 集成（`FpIntegrationController`，前缀 `/api/integration/fp`）

| HTTP | 路径 | 方法 | 权限 | 说明 |
|------|------|------|------|------|
| GET | `/health` | `health()` | — | FP 健康检查 |
| POST | `/push-settlement` | `pushSettlement(SettlementPushRequest)` | `integration:fp:push` | 手动推送结算单 |
| POST | `/ocr-invoice`（multipart） | `ocrInvoice(MultipartFile)` | `integration:fp:ocr` | 推送发票图片 OCR |
| POST | `/payment-callback` | `paymentCallback(PaymentCallbackDto)` | — | 接收 FP 支付回调（不记录响应数据） |

#### OA 集成（`OaIntegrationController`，前缀 `/api/integration/oa`）

| HTTP | 路径 | 方法 | 权限 | 说明 |
|------|------|------|------|------|
| GET | `/health` | `health()` | — | OA 健康检查 |
| POST | `/todo/push` | `pushTodo(OaTodoRequest)` | `integration:oa:push` | 手动推送待办 |
| PUT | `/todo/complete` | `completeTodo(businessKey)` | `integration:oa:process` | 手动完成待办 |
| PUT | `/todo/transfer` | `transferTask(businessKey, newHandlerUserId)` | `integration:oa:process` | 手动转办待办 |

#### 集成管理（`IntegrationController`，前缀 `/api/integration`）

| HTTP | 路径 | 方法 | 权限 | 说明 |
|------|------|------|------|------|
| GET | `/log/list` | `list(page, size, filters)` | — | 分页查询集成日志（支持 `logType` / `businessType` / `businessId` / `responseStatus` 过滤） |
| GET | `/log/{id}` | `get(id)` | — | 获取日志详情 |
| POST | `/log/{id}/retry` | `retry(id)` | `integration:log:retry` | 手动重试单条日志 |

#### 聚合健康（`IntegrationHealthController`，前缀 `/api/integration`）

| HTTP | 路径 | 方法 | 说明 |
|------|------|------|------|
| GET | `/health` | `health()` | 聚合 D365 + FP + OA 健康状态，3 个全连通 = `HEALTHY`，部分 = `DEGRADED`，全断 = `DOWN`。每个子检查在 `try-catch` 中隔离，单系统异常不影响整体响应 |

---

## 异常处理与容错

### 1. 异常类型

| 异常类 | 位置 | 触发场景 | HTTP 响应 |
|--------|------|----------|-----------|
| `IntegrationException` | `pms-common` 的 `com.dp.plat.common.exception.IntegrationException` | 1. Resilience4j 熔断器 OPEN 时由 fallback 方法包装抛出<br>2. OAuth2 Token 获取失败<br>3. HTTP 调用超时 / 连接失败<br>4. 响应解析失败 | `GlobalExceptionHandler.handleIntegrationException` 统一返回 **HTTP 503 SERVICE_UNAVAILABLE**，响应体 `Result.fail(ResultCode.INTEGRATION_FAILURE.getCode(), message)` |
| `BusinessException` | `pms-common` | 1. 集成日志不存在（`"集成日志不存在"`）<br>2. 请求体序列化失败<br>3. 发票图片为空 / OCR 未识别到发票号<br>4. 支付回调缺少结算单号 | 由 `GlobalExceptionHandler` 统一处理 |

`IntegrationException` 继承 `BusinessException`，额外携带 `systemName` 字段（`d365` / `fp` / `oa`），便于按系统维度聚合告警与统计失败率。

### 2. Resilience4j 四层弹性保护

`IntegrationConfig` 通过 `@EnableConfigurationProperties` 启用四个属性绑定，并注册四个 `ApplicationRunner` 在启动后为所有 CircuitBreaker / Retry / Bulkhead / RateLimiter 实例挂载事件监听器，将弹性组件事件转为业务日志（DEBUG / INFO / WARN / ERROR）。

具体配置（来自 `pms-admin/src/main/resources/application.yml`）：

| 组件 | 配置（default） | 实例 |
|------|------------------|------|
| **CircuitBreaker** | 计数滑动窗口 20，最少 10 次调用，失败率 ≥ 50% 触发熔断；OPEN 持续 30s 后自动转 HALF_OPEN；HALF_OPEN 允许 5 次试探；`record-exceptions`：`IOException` / `TimeoutException` / `IntegrationException` | `d365CircuitBreaker` / `fpCircuitBreaker` / `oaCircuitBreaker` |
| **Bulkhead** | 信号量隔离，单实例最大并发 10，等待获取信号量最长 5s | `d365Bulkhead` / `fpBulkhead` / `oaBulkhead` |
| **RateLimiter** | 每秒限流 50 次，超过则等待 10s | `d365RateLimiter` / `fpRateLimiter` / `oaRateLimiter` |
| **Retry** | 最多 3 次尝试（含首调），1s 起步指数退避（2x，上限 16s），仅对 `IOException` / `TimeoutException` 重试（业务异常不重试） | `d365Retry` / `fpRetry` / `oaRetry` |

**Fallback 约定**：所有 fallback 方法均包装抛出 `IntegrationException`，由 `GlobalExceptionHandler` 统一返回 HTTP 503。`healthCheck()` 方法**不加**任何 Resilience4j 注解，避免熔断 OPEN 时健康端点无法探测恢复。

### 3. 分布式 OAuth2 Token 缓存（`oauth` 包）

#### `OAuthTokenCache`（接口）

为 D365 / FP / OA 提供统一 Token 缓存能力，核心方法 `getToken(String systemName, Supplier<TokenInfo> tokenSupplier)`：命中缓存且未临近过期时直接返回；否则加锁刷新后返回新 Token。

#### `RedisOAuthTokenCache`（实现，`@Component`）

基于 Redis Hash 的分布式实现：

- **缓存结构**：Key `oauth:token:{systemName}`，Hash Fields：`accessToken` / `expiresAt`（Unix 秒）/ `tokenType`，TTL = `expiresAt - now + 60s`。
- **提前续期**：`REFRESH_AHEAD_SECONDS = 300`（5 分钟），Token 距过期不足 5 分钟即触发刷新。
- **单飞刷新**：通过 `TokenRefreshLock.tryLock` 保证同一系统同一时刻仅一个线程调用 `tokenSupplier`，其余线程轮询缓存（每 200ms 一次，最多 5s）。
- **双重检查**：持锁后再次读取缓存，避免锁等待期间已被其他线程刷新。
- **失败计数与告警**：`tokenSupplier` 抛异常时递增 Redis 计数器（Key `oauth:failcount:{systemName}`，TTL 5 分钟），连续 ≥ 3 次记录 ERROR 日志并递增 Micrometer Counter `pms_oauth_failure_total{system=...}`；成功后计数器归零。
- **可观测性**：`MeterRegistry` 通过 `ObjectProvider` 注入，未配置 actuator 时跳过指标记录不影响核心功能。

#### `TokenRefreshLock`（`@Component`）

基于 Redis SETNX 的分布式互斥锁：

- **加锁**：`SET key value NX EX 30`（`setIfAbsent`），`value` 为 UUID。
- **解锁**：Lua 脚本 `if get(key)==value then del(key) end`，原子性比对 + 删除，避免误删其他线程的锁。
- **TTL 防死锁**：30 秒 TTL，持锁进程崩溃也能自动过期。
- **ThreadLocal 持有者**：加锁时 UUID 存入 `ThreadLocal`（实例字段，非 static，避免测试上下文重建时泄漏），`finally` 中必须清理。
- **非可重入**：同线程已持锁时 `tryLock` 返回 `false`；`tryLock` 立即返回不阻塞。

### 4. 重试机制（双层）

模块实现两套互补的重试机制：

1. **Resilience4j `@Retry`**（方法级，针对瞬时故障）：仅对 `IOException` / `TimeoutException` 重试，最多 3 次，1s 起步指数退避。
2. **基于 `pms_integration_log` 的调度重试**（业务级，针对任意失败）：
   - `IntegrationLogServiceImpl.markFailed` 按 `(retryCount + 1) * backoffMultiplier` 分钟计算 `nextRetryTime`，到达 `maxRetry` 后停止调度。
   - `RetryServiceImpl.scheduledRetry` 由 `@Scheduled(fixedDelayString = "${integration.retry.interval:300000}")` 每 5 分钟扫描一次 `getPendingRetryLogs()`（条件：`status=FAILED` AND `retry_count < max_retry` AND `next_retry_time <= now()`），按 `logType` 路由到对应适配器的 `retry(Long logId)` 方法。
   - 支持通过 `POST /api/integration/log/{id}/retry` 手动触发，无视当前重试次数。
3. **FP 结算推送的独立后台重试**（`FpIntegrationServiceImpl`）：首调失败后由 `ScheduledExecutorService`（daemon 线程池，2 个线程，名为 `fp-settlement-retry`）按 `BACKOFF_MINUTES = {1, 2, 4, 8, 16}` 分钟调度，最多 5 次重试。`@PreDestroy` 时 `shutdownNow` 释放线程池。

---

## 模块依赖关系

### Maven 依赖

```
pms-common ← pms-integration ← pms-implementation
                             ← pms-workflow
                             ← pms-admin
```

| 依赖方向 | 说明 |
|----------|------|
| `pms-integration` → `pms-common` | 复用 `Result` / `ResultCode` / `BaseEntity` / `BusinessException` / `IntegrationException` / `GlobalExceptionHandler` / `OperLog` 注解 / Spring Boot Data Redis |
| `pms-integration` → `resilience4j-spring-boot3` 2.2.0 | 熔断 / 隔离 / 限流 / 重试 + Spring Boot 自动装配 |
| `pms-integration` → `resilience4j-reactor` 2.2.0 | 为后续响应式外部调用预留 |
| `pms-integration` → `spring-boot-starter-aop` | Resilience4j 注解依赖 AOP 代理生效 |
| `pms-integration` → `spring-boot-starter-test`（test） | Mockito + Spring Boot Test 验证熔断流程 |
| `pms-implementation` → `pms-integration` | `SettlementServiceImpl` 与 `SettlementSaga` 调用 `FpIntegrationService.pushSettlement` / `OaIntegrationService.pushTodo` 等编排结算单提交 Saga |
| `pms-workflow` → `pms-integration` | `OaTaskListener`（Flowable `TaskListener`）在任务 create / complete 事件中调用 `OaIntegrationService.pushTodo` / `completeTodo` 同步 OA 待办 |
| `pms-admin` → `pms-integration` | WAR 启动模块聚合所有模块，对外暴露集成 REST 端点 |

### 跨模块解耦策略

为避免 `pms-integration → pms-implementation → pms-workflow → pms-integration` 的依赖环，模块对 `pms-implementation` 的 Mapper（`assetMapper` / `settlementMapper`）采用**反射查找**：

```java
// D365IntegrationServiceImpl.lookupMapper / FpIntegrationServiceImpl.lookupMapper
private BaseMapper<?> lookupMapper(String beanName) {
    try {
        Object bean = applicationContext.getBean(beanName);
        return bean instanceof BaseMapper ? (BaseMapper) bean : null;
    } catch (Exception e) {
        log.debug("Mapper bean '{}' not available: {}", beanName, e.getMessage());
        return null;
    }
}
```

具体使用场景：
- `D365IntegrationServiceImpl.syncAssetSerialNumbers` 反射调用 `assetMapper.update(...)` 更新 `pms_asset.serial_no`。
- `D365IntegrationServiceImpl.syncInvoices` / `updateSettlementInvoiceNo` 反射调用 `settlementMapper.update(...)` 更新 `pms_settlement.invoice_no`。
- `FpIntegrationServiceImpl.handlePaymentCallback` 反射调用 `settlementMapper.update(...)` 更新 `pms_settlement.payment_status`。

若 Bean 不可用（如独立单元测试环境），方法记 WARN 日志后跳过，不抛异常。

### 与 pms-workflow 的协作（`OaTaskListener`）

`pms-workflow` 模块的 `com.dp.plat.workflow.listener.OaTaskListener`（Flowable `TaskListener`，BPMN 中通过 `delegateExpression="${oaTaskListener}"` 注册）：

- 任务 `create` 事件 → 调用 `oaIntegrationService.pushTodo(OaTodoRequest)` 推送 OA 待办。
- 任务 `complete` 事件 → 调用 `oaIntegrationService.completeTodo(taskId)` 完成 OA 待办。
- **事务隔离**：`notify` 方法标注 `@Transactional(propagation = REQUIRES_NEW)`，确保 OA 集成（含 `IntegrationLog` 写入）在独立事务中执行，OA 调用失败不影响 Flowable 主流程事务。
- **异常策略**：所有异常（`IntegrationException` / HTTP 错误 / Token 失败）在 `notify` 内 catch 吞掉，仅记录 WARN 日志，**绝不向 Flowable 引擎上抛**——OA 集成是 best-effort，瞬时故障不应阻塞工作流。

### 与 pms-implementation 的协作（`SettlementSaga`）

`pms-implementation` 模块的 `SettlementSaga`（结算单提交 Saga 协调器）编排以下与集成相关的步骤：

1. `pushToFp` — 调用 `FpIntegrationService.pushSettlement(SettlementPushRequest)` 推送结算单到 FP（补偿：标记推送失败）。
2. `pushOaTodo` — 调用 `OaIntegrationService.pushTodo(OaTodoRequest)` 推送 OA 待办（补偿：删除 OA 待办）。

任一步骤失败时按反向顺序补偿已成功步骤，所有补偿动作设计为幂等。

---

## 关键技术点

### 1. Resilience4j 事件监听统一注册

`IntegrationConfig` 通过四个 `ApplicationRunner` Bean 在启动后为所有 CircuitBreaker / Retry / Bulkhead / RateLimiter 实例挂载事件监听器，并监听 Registry 的 `EntryAdded` 事件为后续动态创建的实例也注册监听器。Micrometer 指标（`resilience4j.circuitbreaker.calls` / `state`、`resilience4j.bulkhead.*`、`resilience4j.ratelimiter.*`、`resilience4j.retry.calls`）由 starter 自动绑定到 `MeterRegistry`，本类仅补充业务级日志（INFO / WARN），便于运维通过日志而非仅靠指标排查问题。CircuitBreaker 的 `onStateTransition` 事件以 WARN 级别记录（CLOSED→OPEN / OPEN→HALF_OPEN / HALF_OPEN→CLOSED），便于告警系统基于日志关键词触发告警。

### 2. 单飞 Token 刷新防击穿

`RedisOAuthTokenCache.getToken` 的核心流程：
1. **快速路径**：无锁读缓存，命中且未临近过期（剩余 > 5 分钟）直接返回。
2. **加锁刷新**：缓存未命中或即将过期 → `TokenRefreshLock.tryLock`（Redis SETNX，TTL 30s）。
   - 持锁成功 → 双重检查缓存 → 调用 `tokenSupplier` 获取新 Token → 写入缓存（TTL = `expiresAt - now + 60s`）→ 失败计数归零 → 返回。
   - 持锁失败 → 轮询等待（每 200ms 读缓存，最多 5s），超时抛 `IntegrationException`。
3. **失败计数**：`tokenSupplier` 抛异常时递增 Redis 计数器（Key `oauth:failcount:{systemName}`），≥ 3 次记 ERROR + Micrometer Counter。

### 3. 集成日志驱动重试

`IntegrationLog` 既用于审计，也是重试的数据载体。每次外部调用前先 `log()` 落库（status=PENDING），调用结束 `markSuccess` / `markFailed`。`markFailed` 按 `(retryCount + 1) * backoffMultiplier` 分钟计算 `nextRetryTime`，到达 `maxRetry` 后停止调度。`RetryServiceImpl.scheduledRetry` 由 `@Scheduled` 每 5 分钟扫描到期日志，按 `logType` 路由到对应适配器的 `retry(Long logId)` 方法，从日志中取出 `requestUrl` + `requestBody` 重新发送。`OaIntegrationServiceImpl.retry` 还会按 `businessType` 推断 HTTP method（`TODO_PUSH` → POST，`TODO_COMPLETE` / `TODO_TRANSFER` → PUT）。

### 4. 健康检查不叠加弹性注解

所有 `healthCheck()` 方法**不加** `@CircuitBreaker` / `@Bulkhead` / `@Retry` 注解。设计意图：熔断器 OPEN 时仍能通过健康端点探测外部系统是否恢复，否则熔断后健康端点永久返回失败，无法触发 HALF_OPEN → CLOSED 转换。`IntegrationHealthController` 进一步在 `try-catch` 中隔离每个子检查，单系统异常不影响整体响应，并通过 `connected` 计数推导 `overallStatus`（3 全连通 = `HEALTHY`，部分 = `DEGRADED`，全断 = `DOWN`）。

### 5. 跨模块 Mapper 反射查找

为避免 `pms-integration` 显式依赖 `pms-implementation`（会形成 `integration → implementation → workflow → integration` 环），对 `assetMapper` / `settlementMapper` 通过 `ApplicationContext.getBean(beanName)` 反射查找，运行时由 `pms-admin` 的 Spring 容器提供。Bean 不可用时记 WARN 跳过，不抛异常，保证模块独立可测试。

### 6. OA 集成的事务隔离与异常吞咽

`OaTaskListener.notify` 标注 `@Transactional(propagation = REQUIRES_NEW)`：暂停当前 Flowable 事务，开启新事务执行 OA 集成。无论 OA 调用成功或失败，新事务都会提交（确保 `IntegrationLog` 持久化），然后恢复原事务。所有异常在 `notify` 内 catch 吞掉，仅记录 WARN 日志，**绝不向 Flowable 引擎上抛**——OA 集成是 best-effort，瞬时故障不应阻塞工作流主流程。Flowable 通过 `delegateExpression` 从 Spring 容器获取的是 CGLIB 代理对象，因此 `@Transactional` 注解能正常生效。

### 7. FP 结算推送的双层重试

`FpIntegrationServiceImpl.pushSettlement` 首调同步执行（带 `@CircuitBreaker` / `@Bulkhead` / `@Retry`），失败后由独立的 `ScheduledExecutorService`（daemon 线程池 `fp-settlement-retry`）按 `BACKOFF_MINUTES = {1, 2, 4, 8, 16}` 分钟调度最多 5 次后台重试，每次重试通过 `pushSettlementOnce`（无注解，避免双重代理）执行。即使熔断器 OPEN，后台重试调度器仍在 circuit 外运行，后续重试会通过 `pushSettlementOnce` 直接执行，不再走熔断器。
