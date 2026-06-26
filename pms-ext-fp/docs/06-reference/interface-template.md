# 接口模板文档

> 本文档提供 pms-ext-fp 模块的接口调用模板，包括配置初始化、发票推送、发票识别等典型场景的代码模板。

---

## 1. 配置初始化模板

### 1.1 FPApi 配置初始化

```java
/**
 * FPApi 配置初始化模板
 * 在 Spring 容器启动后调用（如 @PostConstruct）
 */
@PostConstruct
public void initFpApiConfig() {
    FPApi.initConfig(() -> {
        Map<String, Object> config = new ConcurrentHashMap<>();
        
        // ===== 服务地址 =====
        config.put("serviceUrl", "https://fp.example.com/");
        config.put("tokenUrl", "/oauth/token?appId=%s");  // %s 会被 appId 替换
        config.put("archiveUrl", "/api/invoice/archive");
        config.put("ssoUrl", "/sso/login");
        
        // ===== 认证配置 =====
        config.put("authType", "header");           // 认证方式: bearer/header/query/cookie
        config.put("authKey", "Authorization");      // 认证键名
        config.put("enableCookie", "false");         // 是否启用 Cookie
        config.put("cookieKey", "");                 // Cookie 键名
        
        // ===== 应用凭据 =====
        config.put("appId", "your_app_id");
        config.put("clientSecret", "your_client_secret");
        config.put("clientId", "your_client_id");
        config.put("resource", "your_resource");
        config.put("grantType", "authorization_code");
        
        // ===== Token 请求参数 =====
        config.put("provider", "fp");                // oauthType
        config.put("openId", "user_open_id");        // code
        config.put("nickName", "user_nickname");     // nickName
        
        // ===== 限流配置 =====
        config.put("rateLimit", 30);                 // 每分钟请求限制
        config.put("enableRetry", "true");           // 启用重试
        config.put("postByForm", "false");           // 是否表单提交
        
        // ===== HTTP 客户端配置 =====
        Map<String, Object> httpClientConfig = new HashMap<>();
        httpClientConfig.put("maxTotal", 100);           // 连接池最大连接数
        httpClientConfig.put("maxPerRoute", 20);         // 每路由最大连接数
        httpClientConfig.put("connectTimeout", 10000);   // 连接超时(ms)
        httpClientConfig.put("readTimeout", 60000);      // 读取超时(ms)
        httpClientConfig.put("keepAliveMinutes", 5);     // 保活时间(分钟)
        httpClientConfig.put("followRedirects", true);   // 跟随重定向
        config.put("httpClient", httpClientConfig);
        
        // ===== 日志配置 =====
        config.put("debug", "false");                 // 生产环境关闭
        
        return config;
    });
}
```

### 1.2 InvoiceUtil 配置初始化

```java
/**
 * InvoiceUtil 配置初始化模板
 * 在 Spring 容器启动后调用
 */
@PostConstruct
public void initInvoiceUtilConfig() {
    InvoiceUtil.initConfig(() -> {
        Map<String, Object> config = new HashMap<>();
        
        // ===== Aviator 表达式 =====
        // 发票类型判断表达式（env: {entity: {entity: invoice}}）
        config.put("invoiceTypeCondition", 
            "entity.entity.invoice_number != nil && string.length(entity.entity.invoice_number) > 0");
        
        // 发票状态判断表达式
        config.put("invoiceStatusCondition", 
            "entity.entity.identify == true && (entity.entity.needVerify == false || entity.entity.verified_status == true)");
        
        // ===== 交付件类型 =====
        config.put("invoiceType", "01");              // 发票原件类型
        config.put("inspectionType", "02");           // 验收材料类型
        
        return config;
    });
}
```

---

## 2. 发票推送模板

### 2.1 单条发票推送

```java
/**
 * 单条发票推送模板
 */
public ElectronicInvoiceResponse pushSingleInvoice(File invoiceFile, Map<String, Object> sourceData) {
    // 构建 ElectronicInvoiceModel
    ElectronicInvoiceModel model = ElectronicInvoiceModel.builder()
        .async(false)                              // 是否异步
        .dataType("invoice")                       // 数据类型
        .dataId("INV-" + System.currentTimeMillis())  // 数据 ID
        .files(new File[] { invoiceFile })         // 发票文件
        .build();
    
    // 设置 openId
    model.setOpenId("user_open_id");
    
    // 构建 options
    Map<String, Object> options = new HashMap<>();
    options.put("async", false);
    options.put("openId", "user_open_id");
    
    // 调用 FPApi
    ElectronicInvoiceResponse response = FPApi.postElectronicInvoice(model, null, options);
    
    // 处理响应
    if (response.isSuccess()) {
        System.out.println("推送成功: " + response.getData());
    } else {
        System.err.println("推送失败: code=" + response.getCode() 
            + ", message=" + response.getMessage());
    }
    
    return response;
}
```

### 2.2 批量发票推送（MULTIPLE 模式）

```java
/**
 * 批量发票推送模板（MULTIPLE 模式，10 线程并发）
 */
public List<Response<ElectronicInvoiceModel>> batchPushInvoices(
        List<File> files, List<Object> sourceList) {
    
    Map<String, Object> options = new HashMap<>();
    options.put("rateLimit", 30);    // 每分钟 30 次
    options.put("async", false);
    
    // 文件列表批量查验（自动构建 ElectronicInvoiceModel）
    List<Response<ElectronicInvoiceModel>> responses = FPApi.postElectronicInvoice(
        "invoice",                              // dataType
        "BATCH-" + System.currentTimeMillis(),  // dataId
        files,                                  // 文件列表
        sourceList,                             // 源数据列表
        null,                                   // config（使用 Supplier）
        options                                 // 选项
    );
    
    // 统计结果
    int success = 0, failure = 0;
    for (int i = 0; i < responses.size(); i++) {
        Response<ElectronicInvoiceModel> resp = responses.get(i);
        if (resp.isSuccess()) {
            success++;
        } else {
            failure++;
            System.err.printf("第%d条失败: code=%s, message=%s%n", 
                i + 1, resp.getCode(), resp.getMessage());
        }
    }
    System.out.printf("批量推送完成: 成功%d条, 失败%d条%n", success, failure);
    
    return responses;
}
```

### 2.3 列表数据推送（自定义 URL）

```java
/**
 * 列表数据推送到自定义 URL 模板
 */
public <T> List<Response<T>> pushToCustomUrl(List<T> list, String syncUrl) {
    Map<String, Object> options = new HashMap<>();
    options.put("responseType", Response.class);
    
    // 使用 pushListData（List 参数提交，SINGLE 模式）
    return FPApi.pushListData(list, syncUrl, 30, null, FPApi.SINGLE, options);
    
    // 或使用 pushSingleData（逐个提交，MULTIPLE 模式）
    // return FPApi.pushSingleData(list, syncUrl, 30, null, FPApi.MULTIPLE, options);
}
```

---

## 3. 发票识别模板

### 3.1 发票类型判断

```java
/**
 * 发票类型判断模板
 */
public boolean checkInvoiceType(Map<String, Object> deliverable) {
    // 方式1：使用默认配置
    boolean isInvoice = InvoiceUtil.checkFileInvoiceType(deliverable);
    
    // 方式2：指定配置
    Map<String, Object> config = new HashMap<>();
    config.put("invoiceTypeCondition", "entity.entity.invoice_number != nil");
    boolean isInvoiceWithConfig = InvoiceUtil.checkFileInvoiceType(deliverable, config);
    
    return isInvoice;
}
```

### 3.2 发票状态判断

```java
/**
 * 发票状态判断模板
 */
public boolean checkInvoiceStatus(Map<String, Object> invoice) {
    // 判断发票状态是否有效
    boolean isValid = InvoiceUtil.checkFileInvoiceStatus(invoice);
    
    // 兜底逻辑：identify=true && (!needVerify || verified_status=true)
    if (!isValid) {
        Boolean identify = MapUtil.getBool(invoice, "identify", false);
        Boolean needVerify = MapUtil.getBool(invoice, "needVerify", false);
        Boolean verified = MapUtil.getBool(invoice, "verified_status", false);
        System.out.printf("状态详情: identify=%s, needVerify=%s, verified=%s%n", 
            identify, needVerify, verified);
    }
    
    return isValid;
}
```

### 3.3 获取发票编号

```java
/**
 * 获取发票唯一编号模板
 */
public String getInvoiceNumber(Map<String, Object> invoice) {
    String uniqueNumber = InvoiceUtil.getUniqueInvoiceNumber(invoice);
    
    if (uniqueNumber == null) {
        System.out.println("无法生成发票编号：invoice_code 和 invoice_number 均为空");
    } else {
        System.out.println("发票编号: " + uniqueNumber);
        // 示例输出：310000000-12345678
    }
    
    return uniqueNumber;
}
```

### 3.4 获取交付件类型

```java
/**
 * 获取交付件类型模板
 */
public void getDeliverableTypes() {
    // 获取发票原件类型（默认 "01"）
    String invoiceType = InvoiceUtil.getFileInvoiceType("01");
    System.out.println("发票原件类型: " + invoiceType);
    
    // 获取验收材料类型（默认 "02"）
    String inspectionType = InvoiceUtil.getFileInspectionType("02");
    System.out.println("验收材料类型: " + inspectionType);
}
```

---

## 4. HTTP 请求模板

### 4.1 GET 请求

```java
/**
 * GET 请求模板
 */
public <T extends Response<E>, E> T getRequest(String url, Object params, Class<T> responseType) {
    Request<T> request = new Request<>();
    request.setResponseType(responseType);
    request.setRequest(params);
    
    // GET 请求，需要认证
    return FPApi.get(url, request, true);
}
```

### 4.2 POST 表单请求

```java
/**
 * POST 表单请求模板
 */
public <T extends Response<E>, E> T postFormRequest(String url, Object params, Class<T> responseType) {
    Request<T> request = new Request<>();
    request.setResponseType(responseType);
    request.setRequest(params);
    
    // POST 表单，需要认证
    return FPApi.postForm(url, request, true);
}
```

### 4.3 POST JSON Body 请求

```java
/**
 * POST JSON Body 请求模板
 */
public <T extends Response<E>, E> T postBodyRequest(String url, Object params, Class<T> responseType) {
    Request<T> request = new Request<>();
    request.setResponseType(responseType);
    request.setRequest(params);
    
    // POST JSON Body，需要认证
    return FPApi.postBody(url, request, true);
}
```

---

## 5. 表单构建模板

### 5.1 OkHttp 表单构建

```java
/**
 * OkHttp multipart 表单构建模板
 */
public MultipartBody buildOkHttpForm(File invoiceFile, String dataType, String dataId) {
    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    
    // 添加文本字段
    builder.form("dataType", dataType)
           .form("dataId", dataId)
           .form("async", "false");
    
    // 添加文件
    builder.form("files", invoiceFile);
    
    // 添加多个文件（同名字段）
    builder.form("files", new File[] { 
        new File("/tmp/file1.pdf"), 
        new File("/tmp/file2.pdf") 
    });
    
    // 添加 InputStream
    try (InputStream is = new FileInputStream(invoiceFile)) {
        builder.form("stream", "invoice.pdf", is, "application/pdf");
    }
    
    // 构建 OkHttp MultipartBody
    return builder.buildOkHttp().build();
}
```

### 5.2 Apache HttpClient 表单构建

```java
/**
 * Apache HttpClient multipart 表单构建模板
 */
public HttpEntity buildApacheForm(File invoiceFile, String dataType, String dataId) {
    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    builder.form("dataType", dataType)
           .form("dataId", dataId)
           .form("files", invoiceFile);
    
    // 构建 Apache HttpClient HttpEntity
    return builder.buildHttp().build();
}
```

---

## 6. 完整业务流程模板

### 6.1 发票推送完整流程

```java
/**
 * 发票推送完整业务流程模板
 */
public void completeInvoicePushFlow(List<File> invoiceFiles, Map<String, Object> businessData) {
    // 1. 判断交付件是否为发票
    boolean isInvoice = InvoiceUtil.checkFileInvoiceType(businessData);
    if (!isInvoice) {
        System.out.println("交付件非发票类型，跳过推送");
        return;
    }
    
    // 2. 判断发票状态是否有效
    boolean isValid = InvoiceUtil.checkFileInvoiceStatus(businessData);
    if (!isValid) {
        System.out.println("发票状态无效，跳过推送");
        return;
    }
    
    // 3. 获取发票编号
    String invoiceNumber = InvoiceUtil.getUniqueInvoiceNumber(businessData);
    System.out.println("推送发票编号: " + invoiceNumber);
    
    // 4. 构建源数据列表
    List<Object> sourceList = new ArrayList<>();
    for (int i = 0; i < invoiceFiles.size(); i++) {
        sourceList.add(businessData);
    }
    
    // 5. 批量推送到 FP 平台
    Map<String, Object> options = new HashMap<>();
    options.put("rateLimit", 30);
    options.put("async", false);
    options.put("openId", businessData.get("openId"));
    
    List<Response<ElectronicInvoiceModel>> responses = FPApi.postElectronicInvoice(
        "invoice", "BATCH-" + invoiceNumber, 
        invoiceFiles, sourceList, null, options);
    
    // 6. 处理推送结果
    for (int i = 0; i < responses.size(); i++) {
        Response<ElectronicInvoiceModel> resp = responses.get(i);
        if (resp.isSuccess()) {
            System.out.printf("文件 %d 推送成功%n", i + 1);
            // 处理返回的发票信息
            List<ElectronicInvoiceModel> data = resp.getData();
            if (data != null && !data.isEmpty()) {
                ElectronicInvoiceModel result = data.get(0);
                System.out.println("  发票来源: " + result.getProvider());
                System.out.println("  下载地址: " + result.getDownloadPath());
            }
        } else {
            System.err.printf("文件 %d 推送失败: %s%n", i + 1, resp.getMessage());
        }
    }
}
```

---

## 7. 错误处理模板

### 7.1 统一错误处理

```java
/**
 * 统一错误处理模板
 */
public <T> void handleResponse(Response<T> response, String operation) {
    if (response == null) {
        throw new RuntimeException(operation + " 返回 null");
    }
    
    if (!response.isSuccess()) {
        String errorMsg = String.format("%s 失败: code=%s, message=%s", 
            operation, response.getCode(), response.getMessage());
        throw new RuntimeException(errorMsg);
    }
    
    // 成功
    System.out.println(operation + " 成功");
}

// 使用示例
ElectronicInvoiceResponse resp = FPApi.postElectronicInvoice(model, null, null);
handleResponse(resp, "发票推送");
```

### 7.2 批量错误收集

```java
/**
 * 批量错误收集模板
 */
public <T> List<String> collectErrors(List<Response<T>> responses) {
    List<String> errors = new ArrayList<>();
    for (int i = 0; i < responses.size(); i++) {
        Response<T> resp = responses.get(i);
        if (!resp.isSuccess()) {
            errors.add(String.format("第%d条: code=%s, message=%s", 
                i + 1, resp.getCode(), resp.getMessage()));
        }
    }
    return errors;
}
```

---

## 8. 注意事项

1. **配置初始化顺序**：确保 `FPApi.initConfig()` 和 `InvoiceUtil.initConfig()` 在业务调用前完成
2. **config 参数传 null**：使用 Supplier 配置时，业务方法中 config 参数传 null 即可
3. **rateLimit 合理设置**：根据 FP 平台的实际限制设置，避免触发限流
4. **文件存在性检查**：`MultipartBodyBuilder.form(String, File)` 会检查 `file.exists()`，不存在的文件被忽略
5. **Response null 检查**：虽然 FPApi 通常返回非 null 的 Response，但调用方仍应做 null 检查
6. **不使用 FPException**：模块不存在 FPException，错误通过 `Response.isSuccess()` 和 `Response.getMessage()` 判断
