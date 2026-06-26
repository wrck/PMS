# pms-ext-fp 编码规范文档

---

## 1. API 调用规范

### 1.1 Token 管理

```java
// 获取 Token（无参，凭据来自 initConfig 注入的配置）
TokenResponse token = FPApi.getToken();

// 检查 Token 是否有效
if (token != null && token.getAccessToken() != null) {
    // Token 有效，可发起后续请求
}
```

### 1.2 HTTP 请求规范

```java
// 初始化配置（系统启动时调用一次）
FPApi.initConfig(() -> {
    Map<String, Object> config = new ConcurrentHashMap<>();
    config.put("serviceUrl", "https://fp.example.com/");
    config.put("tokenUrl", "/oauth/token?appId=%s");
    config.put("archiveUrl", "/api/invoice/archive");
    config.put("authType", "header");
    config.put("authKey", "Authorization");
    config.put("appId", "myAppId");
    return config;
});

// 推送单条发票
ElectronicInvoiceResponse response = FPApi.postElectronicInvoice(data);

// 批量推送发票
List<Response<T>> responses = FPApi.postElectronicInvoice(list);
```

---

## 2. 发票操作规范

### 2.1 推送电子发票

```java
// 构建 ElectronicInvoiceModel（使用 Builder 模式）
ElectronicInvoiceModel model = ElectronicInvoiceModel.builder()
    .dataType("invoice")
    .dataId("INV-001")
    .files(new File[] { new File("/tmp/invoice.pdf") })
    .build();

// 推送单条发票
ElectronicInvoiceResponse response = FPApi.postElectronicInvoice(model, config, options);
```

### 2.2 发票类型判断

```java
// 检查交付件是否为发票类型（使用 Aviator 表达式）
boolean isInvoice = InvoiceUtil.checkFileInvoiceType(invoice);

// 检查发票状态是否有效
boolean isValid = InvoiceUtil.checkFileInvoiceStatus(invoice);
```

### 2.3 获取发票编号

```java
// 获取发票唯一编号（invoice_code-invoice_number 拼接）
String uniqueNumber = InvoiceUtil.getUniqueInvoiceNumber(invoice);
```

---

## 3. 错误处理规范

pms-ext-fp 模块**不存在自定义异常类**（无 FPException），所有错误通过 `Response` 对象返回：

```java
// 错误处理：检查 Response 状态
ElectronicInvoiceResponse response = FPApi.postElectronicInvoice(data);
if (response == null || !response.isSuccess()) {
    String errorMsg = response != null ? response.getMessage() : "响应为空";
    log.error("FP API 调用失败: {}", errorMsg);
    // 由调用方决定是否抛出 RuntimeException
    throw new RuntimeException(errorMsg);
}

// 处理成功响应
List<InvoiceProviderInfo> data = response.getData();
```

> **重要**：FPApi 内部不抛出自定义异常，所有错误场景（URL 为空、响应解析失败、请求超时等）统一通过 `Response.failure()` 静态工厂方法构建失败响应。详见 [错误码文档](../06-reference/error-codes.md)。

---

## 4. 日志规范

```java
// 使用 SLF4J（FPApi 内部已定义 logger）
private static final Logger log = LoggerFactory.getLogger(FPApi.class);

// FPApi 的 log() 方法受 config.debug 控制
FPApi.logInfo("请求URL: {}", url);
FPApi.logDebug("响应体: {}", body);
```
