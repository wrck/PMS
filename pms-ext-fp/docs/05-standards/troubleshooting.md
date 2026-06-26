# 故障排查文档

> 本文档汇总 pms-ext-fp 模块的常见问题、故障现象、根因分析和解决方案，涵盖 Token 过期、接口调用失败、限流触发、发票识别错误等方面。

---

## 1. 问题分类索引

### 1.1 按场景分类

| 场景 | 常见问题 | 严重程度 | 参见章节 |
|------|----------|----------|----------|
| Token 管理 | Token 过期、获取失败、缓存失效 | 高 | 2.1、2.2 |
| 接口调用 | HTTP 超时、连接拒绝、响应解析失败 | 高 | 3.1、3.2、3.3 |
| 限流推送 | 限流触发、队列满、超时 | 中 | 4.1、4.2 |
| 发票识别 | Aviator 表达式错误、判断结果异常 | 中 | 5.1、5.2 |
| 配置问题 | 配置未初始化、字段缺失 | 高 | 6.1、6.2 |
| 连接池 | 连接耗尽、连接泄漏 | 高 | 7.1、7.2 |

### 1.2 按错误信息分类

| 错误信息 | 根因 | 排查方向 |
|----------|------|----------|
| `没有指定URL！` | URL 为空 | 检查 archiveUrl/serviceUrl 配置 |
| `响应内容为空！` | 响应解析为 null | 检查 FP 平台返回内容 |
| `响应内容不是Json格式！...` | 响应非 JSON | 检查 FP 平台返回的 HTML 错误页 |
| `反序列化发生异常！错误信息：...` | JSON 反序列化失败 | 检查 responseType 配置 |
| `请求超时` | HTTP 请求超时 | 检查网络和 FP 平台可用性 |
| `当前系统繁忙，请稍候再试！` | 线程池队列满 | 降低并发量或增大线程池 |
| `Error pushing data` | 调度推送异常 | 检查 FP 平台连接 |

---

## 2. Token 管理问题

### 2.1 案例1：Token 频繁获取（缓存失效）

**问题现象**：日志中频繁出现 Token 获取请求，每次发票推送都触发 `getToken()` 的 HTTP 调用。

**根因分析**：
1. `cachedToken` 被意外清除
2. `initConfig(Map)` 被频繁调用，每次调用都会 `clearToken()`
3. Token 过期时间计算错误，导致未过期的 Token 被判定为过期

**排查步骤**：
1. 检查调用方是否频繁调用 `FPApi.initConfig(Map)`
2. 检查 Token 响应中是否包含 `expiresOn`/`expiresIn`/`expireTime` 字段
3. 检查 `config.debug=true` 时的日志，确认 Token 获取频率

**解决方案**：
```java
// ❌ 错误：每次调用都 initConfig
public void pushInvoice(ElectronicInvoiceModel model) {
    Map<String, Object> config = loadConfig();  // 每次加载
    FPApi.initConfig(config);                    // 每次初始化（会 clearToken）
    FPApi.postElectronicInvoice(model, config);
}

// ✅ 正确：使用 Supplier 一次性初始化
FPApi.initConfig(() -> loadConfig());  // 启动时初始化一次
public void pushInvoice(ElectronicInvoiceModel model) {
    FPApi.postElectronicInvoice(model, null);  // config 传 null，使用 Supplier
}
```

### 2.2 案例2：Token 获取失败

**问题现象**：`getToken()` 返回的 TokenResponse 中 `error` 非空或 `accessToken` 为空。

**根因分析**：
1. FP 平台认证凭据过期（appId/clientSecret 等）
2. `tokenUrl` 配置错误或未格式化 appId
3. FP 平台服务不可用
4. `provider`/`openId`/`nickName` 配置缺失

**排查步骤**：
1. 检查 `tokenUrl` 是否包含 `%s` 占位符且 `appId` 非空
2. 检查 TokenResponse 的 `error` 和 `errorDescription` 字段
3. 手动调用 FP 平台 Token 接口验证凭据

**解决方案**：
```java
TokenResponse token = FPApi.getToken();
if (token.getError() != null) {
    // Token 获取失败
    log.error("Token 获取失败: error={}, description={}", 
              token.getError(), token.getErrorDescription());
    // 检查配置
    Map<String, Object> config = FPApi.getConfig();
    log.info("tokenUrl={}, appId={}, provider={}, openId={}", 
             config.get("tokenUrl"), config.get("appId"),
             config.get("provider"), config.get("openId"));
}
```

---

## 3. 接口调用问题

### 3.1 案例3：HTTP 请求超时

**问题现象**：批量推送发票时，部分请求返回 `Response.failure("请求超时")`。

**根因分析**：
1. FP 平台响应慢
2. `readTimeout` 配置过短
3. 网络延迟高
4. MULTIPLE 模式下 `fixedExecutor` 线程池满，任务排队等待

**排查步骤**：
1. 检查 `httpClient.readTimeout` 配置（默认 60000ms）
2. 检查 FP 平台响应时间（通过 `config.debug=true` 查看日志）
3. 检查网络延迟（ping FP 平台地址）
4. 检查 `fixedExecutor` 线程池队列长度

**解决方案**：
```java
// 增大超时时间
Map<String, Object> httpClientConfig = new HashMap<>();
httpClientConfig.put("readTimeout", 120000);  // 120 秒
config.put("httpClient", httpClientConfig);

// 或降低并发量
options.put("rateLimit", 10);  // 降低到 10 次/分钟
```

### 3.2 案例4：响应解析失败（非 JSON）

**问题现象**：返回 `Response.message("响应内容不是Json格式！...")`，message 中包含 HTML 片段。

**根因分析**：
1. FP 平台返回 HTML 错误页（如 404、500 错误页）
2. FP 平台地址错误，请求到了其他服务
3. Token 过期，FP 平台返回 401 跳转登录页
4. `serviceUrl` 配置错误

**排查步骤**：
1. 查看 message 中的 HTML 片段，判断错误类型
2. 检查 `serviceUrl` 和 `archiveUrl` 配置
3. 检查 Token 是否有效
4. 手动调用 FP 平台接口验证

**解决方案**：
```java
// 启用重试机制
config.put("enableRetry", "true");  // Token 过期时会自动重试

// 检查 URL 配置
Map<String, Object> config = FPApi.getConfig();
log.info("serviceUrl={}, archiveUrl={}", 
         config.get("serviceUrl"), config.get("archiveUrl"));
```

### 3.3 案例5：连接被拒绝

**问题现象**：`requestWithOkHttp` 抛出 `ConnectException: Connection refused`。

**根因分析**：
1. FP 平台服务未启动
2. 防火墙阻断连接
3. `serviceUrl` 配置的地址或端口错误
4. DNS 解析失败

**排查步骤**：
1. 使用 `telnet <host> <port>` 测试连通性
2. 检查 `serviceUrl` 配置
3. 检查 DNS 解析（`nslookup <host>`）

---

## 4. 限流推送问题

### 4.1 案例6：MULTIPLE 模式队列满

**问题现象**：批量推送时返回 `Response.failure("当前系统繁忙，请稍候再试！")`。

**根因分析**：
`fixedExecutor` 是 `newFixedThreadPool(10)`，使用 `LinkedBlockingQueue`（无界队列）。但 `RejectedExecutionException` 通常在以下情况触发：
- 线程池已关闭（`shutdownNow` 后仍提交任务）
- 任务被显式拒绝

**排查步骤**：
1. 检查 FPApi 是否已被销毁（`destroy()` 被调用）
2. 检查 Spring 容器是否正在关闭
3. 检查并发量是否过高

**解决方案**：
```java
// 降低批量大小
List<ElectronicInvoiceModel> batch = list.subList(0, Math.min(50, list.size()));
FPApi.postElectronicInvoice(batch, config, options);

// 或使用 MINUTE 模式（单线程调度，不会队列满）
FPApi.pushListData(list, archiveUrl, 30, config, FPApi.MINUTE, options);
```

### 4.2 案例7：MINUTE 模式超时

**问题现象**：`schedulePushData` 抛出 `RuntimeException("Error pushing data")`。

**根因分析**：
1. `CountDownLatch.await(timeout)` 超时，部分任务未完成
2. 调度任务中 `pushData` 抛出异常

**排查步骤**：
1. 检查批量大小和 `delay` 计算
2. 检查 FP 平台响应时间
3. 检查 `config.debug=true` 时的日志

**解决方案**：
```java
// 超时计算公式：delay * list.size() * 20
// 100 条数据，delay=1 分钟：1 * 100 * 20 = 2000 分钟超时
// 若 FP 平台响应慢，可能需要增大超时或减少批量

// 减少批量大小
for (List<ElectronicInvoiceModel> batch : partition(list, 50)) {
    FPApi.postElectronicInvoice(batch, config, options);
}
```

---

## 5. 发票识别问题

### 5.1 案例8：Aviator 表达式执行异常

**问题现象**：`checkFileInvoiceType` 或 `checkFileInvoiceStatus` 返回兜底结果，`e.printStackTrace()` 输出异常堆栈。

**根因分析**：
1. 表达式语法错误
2. 表达式引用了不存在的字段
3. 表达式返回非 Boolean 类型
4. env 结构不正确（`entity.entity.<字段>`）

**排查步骤**：
1. 查看 `e.printStackTrace()` 的异常堆栈
2. 检查表达式语法（使用 Aviator 文档）
3. 检查表达式中引用的字段是否存在于 invoice Map 中
4. 验证 env 结构：`{entity: {entity: invoice}}`

**解决方案**：
```aviator
# ❌ 错误：字段名不匹配
entity.entity.invoiceNumber != nil
# ElectronicInvoiceModel 用 invoiceNumber，但 Map 用 invoice_number

# ✅ 正确：使用 Map 中的字段名
entity.entity.invoice_number != nil

# ❌ 错误：env 路径错误
invoice.invoice_number != nil

# ✅ 正确：双层嵌套
entity.entity.invoice_number != nil

# ❌ 错误：返回非 Boolean
entity.entity.invoice_number

# ✅ 正确：明确返回 Boolean
entity.entity.invoice_number != nil
```

### 5.2 案例9：发票判断结果不符合预期

**问题现象**：`checkFileInvoiceType` 返回 true，但实际不是发票；或返回 false，但实际是发票。

**根因分析**：
1. 未配置 `invoiceTypeCondition`，走了兜底逻辑
2. 兜底逻辑不适用当前业务场景
3. `configSupplier` 未初始化，`getConfig()` 返回空 Map

**排查步骤**：
1. 检查 `InvoiceUtil.initConfig()` 是否被调用
2. 检查配置中是否包含 `invoiceTypeCondition`
3. 检查兜底逻辑是否符合预期

**解决方案**：
```java
// 确保初始化
InvoiceUtil.initConfig(() -> {
    Map<String, Object> config = new HashMap<>();
    config.put("invoiceTypeCondition", "entity.entity.invoice_number != nil && string.length(entity.entity.invoice_number) > 0");
    config.put("invoiceStatusCondition", "entity.entity.identify == true && (entity.entity.needVerify == false || entity.entity.verified_status == true)");
    return config;
});

// 兜底逻辑说明：
// checkFileInvoiceType 兜底：needVerify=false && invoice_number 为空 → false（非发票）
// checkFileInvoiceStatus 兜底：identify && (!needVerify || verified) → true（有效）
```

---

## 6. 配置问题

### 6.1 案例10：配置未初始化

**问题现象**：`FPApi.getConfig()` 返回空 Map，所有配置字段为 null。

**根因分析**：
1. `initConfig()` 未被调用
2. `configSupplier`/`configFunction` 抛出异常
3. Spring 容器未完成初始化

**排查步骤**：
1. 检查调用方是否调用了 `FPApi.initConfig()`
2. 检查 `configSupplier` 是否抛出异常（`getConfig` 会捕获并返回空 Map）
3. 检查 Spring 初始化顺序

**解决方案**：
```java
// 在 Spring 容器启动后初始化
@PostConstruct
public void init() {
    FPApi.initConfig(() -> configService.loadFpConfig());
    InvoiceUtil.initConfig(() -> configService.loadInvoiceConfig());
}
```

### 6.2 案例11：tokenUrl 格式化失败

**问题现象**：`tokenUrl` 仍包含 `%s` 占位符，Token 获取请求 URL 错误。

**根因分析**：
1. `appId` 为 null
2. `tokenUrl` 不包含 `%s` 占位符
3. `String.format(tokenUrl, appId)` 抛出异常被静默捕获

**排查步骤**：
1. 检查 `appId` 配置是否非空
2. 检查 `tokenUrl` 是否包含 `%s`
3. 检查 `initConfig` 中的 `try-catch` 块

**解决方案**：
```java
// 确保 tokenUrl 包含 %s 且 appId 非空
config.put("tokenUrl", "/oauth/token?appId=%s");
config.put("appId", "myAppId");
// initConfig 会执行 String.format(tokenUrl, appId) → "/oauth/token?appId=myAppId"
```

---

## 7. 连接池问题

### 7.1 案例12：连接池耗尽

**问题现象**：HTTP 请求长时间阻塞，最终超时。

**根因分析**：
1. `maxTotal` 配置过小
2. 连接未正确释放（`httpRequest.releaseConnection()` 未调用）
3. 长连接保活时间过短

**排查步骤**：
1. 检查 `httpClient.maxTotal` 配置
2. 通过 JMX 监控连接池使用情况
3. 检查 `requestWithPool` 中的 `finally` 块是否执行

**解决方案**：
```java
// 增大连接池
Map<String, Object> httpClientConfig = new HashMap<>();
httpClientConfig.put("maxTotal", 200);
httpClientConfig.put("maxPerRoute", 50);
config.put("httpClient", httpClientConfig);
```

### 7.2 案例13：连接泄漏

**问题现象**：应用运行一段时间后，HTTP 请求全部超时，重启后恢复。

**根因分析**：
1. `FPApi.destroy()` 未被调用，连接池未关闭
2. `okhttp3.Response` 未使用 try-with-resources
3. `fixedExecutor`/`scheduler` 线程池未关闭

**排查步骤**：
1. 检查 Spring 容器关闭日志，确认 `destroy()` 被调用
2. 检查 `requestWithOkHttp` 中的 `try (okhttp3.Response okResponse = ...)` 是否正确
3. 使用 `jstack` 查看线程状态

**解决方案**：
- 确保 Spring 容器正常关闭（`registerShutdownHook()`）
- OkHttp 实现已使用 try-with-resources，无需额外处理
- Apache HttpClient 实现中 `httpRequest.releaseConnection()` 在 finally 块中调用

---

## 8. 调试技巧

### 8.1 开启调试日志

```java
Map<String, Object> config = new HashMap<>();
config.put("debug", "true");  // 开启 DEBUG 日志
FPApi.initConfig(() -> config);
```

开启后，FPApi 会输出以下日志：
- `请求URL: ...`（INFO 级别）
- `请求：...`（DEBUG 级别，包含 request 对象）
- `表单数据: ...` / `请求体: ...`（DEBUG 级别）
- `响应体: ...`（DEBUG 级别）
- `请求异常: ...`（DEBUG 级别）
- `尝试重新请求: ...`（DEBUG 级别）

### 8.2 检查配置

```java
// 打印当前配置
Map<String, Object> config = FPApi.getConfig();
config.forEach((k, v) -> {
    if (!"clientSecret".equals(k) && !"cookieValue".equals(k)) {
        System.out.println(k + " = " + v);  // 脱敏输出
    }
});

// 检查 Token 状态
TokenResponse token = FPApi.getToken();
System.out.println("Token type: " + token.getTokenType());
System.out.println("Expires on: " + token.getExpiresOn());
System.out.println("Error: " + token.getError());
```

### 8.3 线程 dump 分析

```bash
# 获取线程 dump
jstack <pid> > thread_dump.txt

# 查找 FPApi 相关线程
grep "FPApi-" thread_dump.txt
# FPApi-ScheduledPool-Thread-1（调度池）
# FPApi-FixedPool-Thread-1~10（并发池）
```

---

## 9. 问题预防

| 预防措施 | 实施方式 | 检查频率 |
|----------|----------|----------|
| 配置初始化检查 | 启动后调用 `getConfig()` 验证 | 每次部署 |
| Token 有效性检查 | 启动后调用 `getToken()` 验证 | 每次部署 |
| FP 平台连通性检查 | 发送测试请求 | 每日 |
| 连接池监控 | JMX 监控连接池使用率 | 实时 |
| 日志监控 | 监控 ERROR 级别日志 | 实时 |
| 重试率监控 | 统计 `retryRequest` 调用次数 | 每日 |
