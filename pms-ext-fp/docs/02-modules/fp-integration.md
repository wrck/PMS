# pms-ext-fp FP集成模块详细文档

> 本文档深度分析 pms-ext-fp FP集成模块的所有工具类、模型类、实体类。
>
> **重要纠正**：本文档已基于实际源码重写，移除了此前版本中虚构的 `FPException` 自定义异常类、`InvoiceUtil.verifyInvoice()`/`getInvoiceInfo()` 等不存在的方法，以及 `ElectronicInvoiceModel` 中不存在的字段。详见 [审计报告](../audit/audit-modules.md)。

---

## 1. 模块概述

pms-ext-fp 是 PMS 系统的 FP（财务平台）集成扩展模块，提供电子发票管理能力。

### 涉及的工具类列表

| 工具类 | 职责 |
|--------|------|
| `FPApi` | FP 平台 API 调用核心（Token 管理、HTTP 请求、限流推送） |
| `InvoiceUtil` | 发票识别与判断（类型判断、状态判断、编号生成） |
| `MultipartBodyBuilder` | multipart/form-data 表单构建器（支持 OkHttp 和 Apache HttpClient） |

### 涉及的模型类列表

| Model 类 | 说明 |
|----------|------|
| `ElectronicInvoiceModel` | 电子发票模型（继承 InvoiceProviderInfo） |
| `ElectronicInvoiceResponse` | 电子发票响应（继承 Response<InvoiceProviderInfo>） |
| `ElectronicInvoiceIdentifyAndVerifyResponse` | 发票验真响应（继承 Response<InvoiceProviderInfo>） |
| `TokenRequest` | Token 请求（继承 Request<TokenResponse>） |
| `TokenResponse` | Token 响应（继承 Response<Object>） |
| `Request<T>` | 通用请求模型 |
| `Response<T>` | 通用响应模型 |
| `RequestBody` | 请求体模型 |
| `MsgResponse` | 消息响应（继承 Response<InvoiceProviderInfo>） |

### 涉及的实体类列表

| Entity 类 | 说明 |
|-----------|------|
| `BaseEntity` | 基础实体（id/createBy/createTime/customInfo） |
| `InvoiceProviderInfo` | 发票提供者信息（继承 BaseEntity） |

---

## 2. 工具类详细说明

### 2.1 FPApi

**职责**：封装 FP 平台 API 调用，包括 Token 管理、HTTP 请求、限流推送。

**核心方法**：

#### `TokenResponse getToken()`
- **功能**：获取 FP 平台访问令牌（带缓存）
- **返回值**：TokenResponse - Token 响应
- **缓存**：使用 `volatile` + `ReentrantReadWriteLock` 实现读写锁缓存，自动刷新过期 Token
- **注意**：无参数版本，凭据来自 `initConfig` 注入的配置

#### `ElectronicInvoiceResponse postElectronicInvoice(T data)`
- **功能**：推送单条发票到 FP 平台
- **参数**：`data` - 发票数据（通常为 ElectronicInvoiceModel）
- **返回值**：ElectronicInvoiceResponse - 发票响应
- **请求格式**：multipart/form-data

#### `List<Response<T>> postElectronicInvoice(List<T> list)`
- **功能**：批量推送发票到 FP 平台（MULTIPLE 模式，10 线程并发）
- **参数**：`list` - 发票列表
- **返回值**：List<Response<T>> - 响应列表（保持顺序）
- **限流**：默认 rateLimit=30，可通过 options 覆盖

#### `List<Response<ElectronicInvoiceModel>> postElectronicInvoice(String dataType, String dataId, List<File> files, List<Object> sourceList, Map config)`
- **功能**：文件列表批量查验
- **参数**：`dataType` - 数据类型，`dataId` - 数据 ID，`files` - 文件列表，`sourceList` - 源数据列表，`config` - 配置
- **返回值**：List<Response<ElectronicInvoiceModel>> - 响应列表

> 完整方法清单详见 [FPApi 工具类详解](fp-api.md)。

### 2.2 InvoiceUtil

**职责**：发票识别与判断工具，通过 Aviator 表达式引擎实现可配置的规则判断。

**核心方法**：

#### `String getUniqueInvoiceNumber(Map<String, Object> invoice)`
- **功能**：获取发票唯一编号
- **参数**：`invoice` - 发票数据 Map
- **返回值**：String - 唯一编号（invoice_code-invoice_number 拼接，或 uniqueInvoiceNumber 字段）

#### `boolean checkFileInvoiceType(Map<String, Object> invoice)`
- **功能**：检查交付件是否为发票类型
- **参数**：`invoice` - 发票数据 Map
- **返回值**：boolean - true 表示是发票类型
- **规则**：优先使用 config.invoiceTypeCondition Aviator 表达式，无表达式时走兜底逻辑

#### `boolean checkFileInvoiceStatus(Map<String, Object> invoice)`
- **功能**：检查发票状态是否有效
- **参数**：`invoice` - 发票数据 Map
- **返回值**：boolean - true 表示状态有效
- **规则**：优先使用 config.invoiceStatusCondition Aviator 表达式，无表达式时走兜底逻辑（identify && (!needVerify || verified)）

#### `T getFileInvoiceType(T defalutValue)`
- **功能**：获取交付件发票原件类型
- **返回值**：泛型 T（通常为 String），来自 config.invoiceType

#### `T getFileInspectionType(T defalutValue)`
- **功能**：获取交付件验收材料类型
- **返回值**：泛型 T（通常为 String），来自 config.inspectionType

> 完整方法清单详见 [InvoiceUtil 发票工具详解](invoice-util.md)。

### 2.3 MultipartBodyBuilder

**职责**：multipart/form-data 表单构建器，支持 OkHttp 和 Apache HttpClient 双客户端。

**核心方法**：

#### `MultipartBodyBuilder form(Map<String, Object> form)`
- **功能**：批量添加表单数据
- **支持类型**：File、File[]、Iterable、数组、String、InputStream

#### `MultipartBody.Builder buildOkHttp()`
- **功能**：构建 OkHttp MultipartBody.Builder
- **返回值**：需再调用 `.build()` 获取 MultipartBody

#### `MultipartEntityBuilder buildHttp()`
- **功能**：构建 Apache HttpClient MultipartEntityBuilder
- **返回值**：需再调用 `.build()` 获取 HttpEntity

> 完整方法清单详见 [MultipartBodyBuilder 表单构建器详解](multipart-builder.md)。

---

## 3. 模型类详细说明

### 3.1 ElectronicInvoiceModel

继承 InvoiceProviderInfo，使用 Lombok 注解（@Data、@Builder、@NoArgsConstructor、@AllArgsConstructor）。

**自身字段**：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `async` | boolean | 是否异步 |
| `dataType` | String | 数据类型 |
| `dataId` | String | 数据 ID |
| `invoiceList` | List<ElectronicInvoiceModel> | 发票信息传递列表 |
| `sourceList` | List<Object> | 发票信息传递列表 |
| `files` | File[] | 附件（可多文件） |
| `jsonData` | String | JSON 格式传参数据 |
| `invoiceCode` | String | 发票编码 |
| `invoiceDate` | String | 发票日期 |
| `invoiceNumber` | String | 发票号码 |

**继承字段**（来自 InvoiceProviderInfo 和 BaseEntity）：invoiceId、provider、openId、electricHash、eSignature、eDocModified、eSignDate、fileSize、fileExt、downloadPath、uploadPath、sourceUrl、status、signatureInfo、query、info、id、createBy、createTime、updateBy、updateTime、customInfo

> 完整字段清单详见 [实体与模型参考](entity-model-reference.md)。

### 3.2 TokenResponse

继承 Response<Object>，包含 Token 相关字段：

| 字段名 | 类型 | JSON 名称 | 说明 |
|--------|------|-----------|------|
| `tokenType` | String | `token_type` | 令牌类型 |
| `expiresIn` | String | `expires_in` | 有效期（秒） |
| `expiresOn` | String | `expires_on` | 过期时间戳（秒） |
| `expireTime` | String | `expireTime` | 过期时间（字符串） |
| `accessToken` | String | `__RequestVerificationToken` | 访问令牌 |
| `error` | String | `error` | 错误标识 |
| `errorDescription` | String | `error_description` | 错误描述 |
| `timestamp` | String | `timestamp` | 时间戳 |

> 完整字段清单详见 [实体与模型参考](entity-model-reference.md)。

---

## 4. 错误处理机制

### 4.1 Response.failure 静态工厂方法

pms-ext-fp 模块**不存在自定义异常类**（无 FPException），所有错误通过 `Response.failure()` 静态工厂方法处理：

```java
// Response.java 中的错误处理
public static <T> Response<T> failure(String message) {
    return new Response<T>().message(message);
}

public static <T, R extends Response<T>> R failure(String message, Class<R> responseClass) {
    Response<T> response = null;
    if (responseClass != null && Response.class.isAssignableFrom(responseClass)) {
        try {
            response = responseClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            response = JSON.parseObject("{}", responseClass);
        }
    } else {
        response = new Response<T>();
    }
    return (R) response.message(message);
}
```

### 4.2 错误处理场景

| 错误场景 | 处理方式 | 错误消息 |
|----------|----------|----------|
| URL 为空 | `Response.message(...)` | `没有指定URL！` |
| 响应解析为 null | `retryRequest` → `Response.message(...)` | `响应内容为空！` |
| 响应非 JSON | `Response.message(...)` | `响应内容不是Json格式！...` |
| 请求超时 | `Response.failure(...)` | `请求超时` |
| 线程池队列满 | `Response.failure(...)` | `当前系统繁忙，请稍候再试！` |
| 推送异常 | `Response.failure(...)` | `e.getMessage()` |

### 4.3 调用方错误处理示例

```java
ElectronicInvoiceResponse response = FPApi.postElectronicInvoice(model, null, null);

if (response == null) {
    // 处理 null 响应
    throw new RuntimeException("发票推送返回 null");
}

if (!response.isSuccess()) {
    // 处理失败响应（不抛出 FPException，直接获取错误信息）
    String errorMsg = String.format("发票推送失败: code=%s, message=%s", 
        response.getCode(), response.getMessage());
    throw new RuntimeException(errorMsg);
}

// 处理成功响应
List<InvoiceProviderInfo> data = response.getData();
```

> 完整错误码清单详见 [错误码文档](../06-reference/error-codes.md)。

---

## 5. 相关文档

| 文档 | 说明 |
|------|------|
| [FPApi 工具类详解](fp-api.md) | FPApi 完整方法清单与使用说明 |
| [InvoiceUtil 发票工具详解](invoice-util.md) | InvoiceUtil 完整方法清单与 Aviator 集成 |
| [MultipartBodyBuilder 表单构建器详解](multipart-builder.md) | 表单构建器使用说明 |
| [实体与模型参考](entity-model-reference.md) | 全部实体与模型类字段定义 |
| [类参考](class-reference.md) | 14 个 Java 类完整清单与方法签名 |
| [FP API 架构](../01-architecture/fp-api-architecture.md) | Token 缓存、HTTP 客户端、限流机制 |
| [发票识别架构](../01-architecture/invoice-recognition.md) | InvoiceUtil 与 AviatorUtils 集成 |
| [FP 调用矩阵](../04-mapping/fp-call-matrix.md) | 调用点、方法、参数矩阵 |
| [错误码](../06-reference/error-codes.md) | Response.failure 错误消息清单 |
| [审计报告](../audit/audit-modules.md) | 文档审计与虚构内容纠正记录 |
