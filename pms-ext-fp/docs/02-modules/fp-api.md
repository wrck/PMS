# FPApi 工具类详解

> 本文档详细说明 pms-ext-fp 模块中 `FPApi` 工具类的方法清单、Token 管理、HTTP 客户端选择与限流模式。
>
> 源码位置：`src/main/java/com/dp/plat/pms/extend/fp/util/FPApi.java`

---

## 1. 类定义

```java
@Component("fpApi")
public class FPApi implements DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(FPApi.class);
    // ...
}
```

- **Spring Bean 名称**：`fpApi`
- **生命周期**：实现 `DisposableBean`，`destroy()` 方法回收线程池与连接池
- **注解**：`@PostConstruct` 标注的 `init()` 方法将 Spring 实例赋值给静态字段 `FPApi`
- **泛型约束**：HTTP 方法使用 `<T extends Response<E>, E>` 约束，确保返回类型继承 `Response`

---

## 2. 常量定义

| 常量 | 值 | 用途 |
|------|-----|------|
| `SYS_FP_API` | `"sys.fp.api"` | 系统配置键名 |
| `MINUTE` | `"MINUTE"` | 限流模式：分钟调度 |
| `SINGLE` | `"SINGLE"` | 限流模式：单次同步 |
| `MULTIPLE` | `"MULTIPLE"` | 限流模式：多并发 |
| `DEFAULT_TYPE` | `TypeReference<Map<String, Object>>` | 默认 JSON 反序列化类型 |

---

## 3. 方法清单

### 3.1 配置管理方法

| 方法签名 | 说明 |
|----------|------|
| `static Map<String, Object> getConfig()` | 获取当前配置（优先 Supplier，其次 Function） |
| `static Map<String, Object> initConfig(Supplier<ConcurrentHashMap<String, Object>> configSupplier)` | 通过 Supplier 初始化配置 |
| `static Map<String, Object> initConfig(Function<String, ConcurrentHashMap<String, Object>> configFunction, String key)` | 通过 Function + key 初始化配置 |
| `static Map<String, Object> initConfig(Map<String, Object> config)` | 通过 Map 初始化配置（反射注入字段） |

**initConfig(Map) 内部逻辑**：
1. 若 config 为空，调用 `getConfig()` 动态获取
2. 若 config 与当前 `FPApi.config` 是同一对象，跳过（避免重复初始化）
3. 否则 `putAll` 合并配置
4. 反射遍历所有 `String` 类型、非 `final`/`volatile`/`transient` 字段，从 config 中按字段名取值注入
5. 对 `tokenUrl` 执行 `String.format(tokenUrl, appId)`（支持 `%s` 占位符）
6. 调用 `clearToken()` 清除旧 Token

### 3.2 Token 管理方法

| 方法签名 | 说明 |
|----------|------|
| `static TokenResponse getToken()` | 获取 Token（带缓存，读写锁保护） |
| `private static void clearToken()` | 清除缓存的 Token 和 Cookie（写锁保护） |

**getToken() 流程**：
1. 读锁检查 `cachedToken` 是否存在且未过期
2. 未过期直接返回；过期或不存在，释放读锁
3. 尝试获取写锁（`tryLock`），失败则递归调用 `getToken()`（等待其他线程刷新）
4. 获取写锁后：`clearToken()` → 构建 `TokenRequest` → `get(tokenUrl, request, false)` 获取新 Token
5. Token 有效则缓存，并按需提取 Cookie；无效则 `clearToken()`

### 3.3 认证注入方法

| 方法签名 | 适用客户端 | 说明 |
|----------|------------|------|
| `@Deprecated static void initAuthorization(Request<?> request)` | 通用 | 已废弃，认证逻辑已内联 |
| `static void initAuthorization(HttpRequest httpRequest)` | Hutool | 按 authType 设置 form/header/cookie |
| `static String initAuthorization(String url, URI uri)` | 通用 URL | 返回拼接 query 参数的 URL |
| `static void initAuthorization(HttpRequestBase httpRequest)` | Apache HttpClient | 按 authType 设置 header/URI/Cookie |
| `static void initAuthorization(okhttp3.Request.Builder requestBuilder)` | OkHttp | 按 authType 设置 header/URL/Cookie |
| `static void initApikey(HttpRequest httpRequest)` | Hutool | 设置 API Key（authKey + appId） |
| `static String initCookie(String existingCookie, String key, String value)` | 通用 | 合并 Cookie，避免重复 |
| `static String getCookieValue(Response response, String name)` | 通用 | 从响应头 Set-Cookie 提取指定 cookie |

**authType 认证方式**：

| authType | Hutool 注入方式 | Apache HttpClient 注入方式 | OkHttp 注入方式 |
|----------|-----------------|---------------------------|----------------|
| `bearer` | `httpRequest.form(authKey, token)` | `httpRequest.setHeader(authKey, token)` | `requestBuilder.header(authKey, token)` |
| `header` | `httpRequest.header(authKey, token)` | `httpRequest.setHeader(authKey, token)` | `requestBuilder.header(authKey, token)` |
| `query` | `httpRequest.header(authKey, token)` | `URIBuilder.addParameter(authKey, token)` | `HttpUrl.Builder.addQueryParameter(authKey, token)` |
| `cookie` | `httpRequest.cookie(new HttpCookie(...))` | `httpRequest.setHeader("Cookie", ...)` | `requestBuilder.header("Cookie", ...)` |

### 3.4 发票查验方法（postElectronicInvoice）

| 方法签名 | 说明 |
|----------|------|
| `static List<Response<ElectronicInvoiceModel>> postElectronicInvoice(String dataType, String dataId, List<File> files, List<Object> sourceList, Map<String, Object> config)` | 文件列表批量查验（5参数） |
| `static List<Response<ElectronicInvoiceModel>> postElectronicInvoice(String dataType, String dataId, List<File> files, List<Object> sourceList, Map<String, Object> config, Map<String, Object> options)` | 文件列表批量查验（6参数，带选项） |
| `static <T> ElectronicInvoiceResponse postElectronicInvoice(T data)` | 单条查验 |
| `static <T> ElectronicInvoiceResponse postElectronicInvoice(T data, Map<String, Object> config)` | 单条查验（带配置） |
| `static <T> ElectronicInvoiceResponse postElectronicInvoice(T data, Map<String, Object> config, Map<String, Object> options)` | 单条查验（带配置和选项） |
| `static <T> List<Response<T>> postElectronicInvoice(List<T> list)` | 列表批量查验 |
| `static <T> List<Response<T>> postElectronicInvoice(List<T> list, Map<String, Object> config)` | 列表批量查验（带配置） |
| `static <T> List<Response<T>> postElectronicInvoice(List<T> list, Map<String, Object> config, Map<String, Object> options)` | 列表批量查验（带配置和选项） |

**文件列表批量查验流程**（5/6参数版本）：
1. 校验 files 非空
2. 遍历文件，为每个文件构建 `ElectronicInvoiceModel`：
   - 设置 `async`（来自 options）、`files`（单文件数组）、`dataType`、`dataId`
   - 从 sourceList 取对应索引的源数据（超出索引取最后一个）
   - 合并 options 到 jsonData
3. 调用列表版 `postElectronicInvoice(List, config, options)`

**单条查验流程**：
1. `initConfig(config)` 初始化配置
2. 设置 `responseType`/`responseClass` 为 `ElectronicInvoiceResponse.class`
3. 设置 `Content-Type: multipart/form-data`
4. 调用 `pushSingleData(data, archiveUrl, config, options)`

**列表批量查验流程**：
1. 同单条的配置初始化
2. 从 options/config 获取 `rateLimit`（默认 30）
3. 调用 `pushSingleData(list, archiveUrl, rateLimit, config, MULTIPLE, options)`

### 3.5 数据推送方法

| 方法签名 | 说明 |
|----------|------|
| `static <T> List<Response<T>> pushListData(List<T> list, String syncUrl, Integer rateLimit, Map<String, Object> config, Map<String, Object> options)` | 推送列表数据（List 参数，SINGLE 模式） |
| `static <T> List<Response<T>> pushListData(List<T> list, String syncUrl, Integer rateLimit, Map<String, Object> config, String limitType, Map<String, Object> options)` | 推送列表数据（指定限流模式） |
| `static <T> List<Response<T>> pushSingleData(List<T> list, String syncUrl, Integer rateLimit, Map<String, Object> config, String limitType, Map<String, Object> options)` | 推送单条数据（逐个发送） |
| `static <T> List<Response<T>> pushData(List<T> list, String syncUrl, Integer rateLimit, Map<String, Object> config, String limitType, boolean splitToList, Map<String, Object> options)` | 核心推送方法（拆分+限流） |
| `private static <T> Response<T> pushData(Object data, String syncUrl, Map<String, Object> config, Map<String, Object> options)` | 推送单条数据（核心） |

**pushData(list, ...) 核心逻辑**：
1. 按 `splitToList` 决定是否将 list 按 `rateLimit` 大小拆分为子列表
2. 按 `limitType` 选择发送策略：
   - `MINUTE`：`schedulePushData()` 调度池定时发送
   - `MULTIPLE`：`multiplePushData()` 线程池并发发送
   - `SINGLE`（默认）：逐个同步 `pushData(data, ...)`

**pushData(data, ...) 单条推送逻辑**：
1. 从 options 获取 `responseType`
2. 构建 `Request<Response<T>>`，设置 responseType、headers、request
3. 根据 `postByForm` 配置选择 `postForm()` 或 `postBody()`
4. 设置 `response.setRequest(request)` 便于追溯

### 3.6 HTTP 请求方法

| 方法签名 | 说明 |
|----------|------|
| `static <T extends Response<E>, E> T postForm(String url, Request<T> params)` | 表单 POST（需认证） |
| `static <T extends Response<E>, E> T postForm(String url, Request<T> params, boolean needAuth)` | 表单 POST（指定认证） |
| `static <T extends Response<E>, E> T postForm(String url, Request<T> params, boolean needAuth, Map<String, Object> options)` | 表单 POST（带选项） |
| `static <T extends Response<E>, E> T postBody(String url, Request<T> params)` | JSON Body POST（需认证） |
| `static <T extends Response<E>, E> T postBody(String url, Request<T> params, Map<String, Object> options)` | JSON Body POST（带选项） |
| `static <T extends Response<E>, E> T postBody(String url, Request<T> params, boolean needAuth)` | JSON Body POST（指定认证） |
| `static <T extends Response<E>, E> T postBody(String url, Request<T> params, boolean needAuth, Map<String, Object> options)` | JSON Body POST（完整参数） |
| `static <T extends Response<E>, E> T post(String url, Request<T> request, boolean isForm, boolean needAuth)` | 通用 POST |
| `static <T extends Response<E>, E> T post(String url, Request<T> request, boolean isForm, boolean needAuth, Map<String, Object> options)` | 通用 POST（带选项） |
| `static <T extends Response<E>, E> T get(String url, Request<T> request)` | GET（需认证） |
| `static <T extends Response<E>, E> T get(String url, Request<T> request, boolean needAuth)` | GET（指定认证） |
| `static <T extends Response<E>, E> T request(String method, String url, Request<T> request, boolean isForm, boolean needAuth)` | 通用请求入口 |
| `static <T extends Response<E>, E> T request(String method, String url, Request<T> request, boolean isForm, boolean needAuth, Map<String, Object> options)` | 通用请求入口（带选项） |

### 3.7 HTTP 客户端实现方法

| 方法签名 | 说明 |
|----------|------|
| `static <T extends Response<E>, E> T requestWithHutool(...)` | Hutool 实现（基于 HttpURLConnection） |
| `static <T extends Response<E>, E> T requestWithPool(...)` | Apache HttpClient 连接池实现 |
| `static <T extends Response<E>, E> T requestWithOkHttp(...)` | OkHttp 连接池实现（**当前默认**） |
| `static <T extends Response<E>, E> T retryRequest(...)` | 重试请求（清 Token + 递归） |

**request() 方法当前实现**：
```java
public static <T extends Response<E>, E> T request(...) {
    return requestWithOkHttp(method, url, request, isForm, needAuth, options);
}
```

### 3.8 工具方法

| 方法签名 | 说明 |
|----------|------|
| `static Collection<Object> wrapCollection(Object target)` | 包装集合（Collection 则 addAll，否则 add） |
| `static <T> Collection<T> filterCollection(Collection<T> collection)` | 过滤 null 和空字符串 |
| `static Map<String, Object> beanToMap(Object object)` | Bean 转 Map（Hutool BeanUtil，不忽略 null） |
| `static Map toJSONMap(Object object)` | 转 JSON Map（保持字段顺序） |
| `static String toJSONString(Object object, SerializerFeature... serializerFeatures)` | 转 JSON 字符串（禁用字母排序） |
| `static void logInfo(String format, Object... arguments)` | INFO 级别日志 |
| `static void logDebug(String format, Object... arguments)` | DEBUG 级别日志 |
| `static void log(String format, Object... arguments)` | 按 config.debug 输出 |
| `static void log(String format, boolean isDebug, Object... arguments)` | 按级别输出日志 |

### 3.9 生命周期方法

| 方法签名 | 说明 |
|----------|------|
| `@PostConstruct void init()` | Spring 初始化回调，`FPApi = this` |
| `void destroy()` | Spring 销毁回调，回收线程池与连接池 |

**destroy() 回收顺序**：
1. `FPApi = null`
2. `scheduler.shutdownNow()` — 调度线程池
3. `fixedExecutor.shutdownNow()` — 并发线程池
4. `HttpClientPool.close()` — Apache HttpClient 连接池
5. `OkHttpPool.close()` — OkHttp 连接池

---

## 4. 内部类

### 4.1 HttpClientPool（Apache HttpClient 连接池）

```java
public static class HttpClientPool {
    private static final PoolingHttpClientConnectionManager connManager = ...;
    private static volatile CloseableHttpClient httpClient;
    
    public static CloseableHttpClient getHttpClient();  // 双重检查锁定单例
    public static void close();                          // 关闭连接池
}
```

### 4.2 OkHttpPool（OkHttp 连接池）

```java
public static class OkHttpPool {
    private static volatile OkHttpClient httpClient;
    
    public static OkHttpClient get();    // 双重检查锁定单例
    public static void close();          // 关闭连接池
}
```

---

## 5. 静态字段清单

### 5.1 线程池

| 字段 | 类型 | 说明 |
|------|------|------|
| `scheduler` | `ScheduledExecutorService` | 单线程调度池（MINUTE 模式） |
| `fixedExecutor` | `ExecutorService` | 10 线程固定池（MULTIPLE 模式） |
| `lock` | `ReentrantReadWriteLock(true)` | 公平读写锁（Token 缓存保护） |

### 5.2 配置字段（反射注入）

| 字段 | 类型 | 说明 |
|------|------|------|
| `authType` | String | 认证类型 |
| `authKey` | String | 认证键名 |
| `authValue` | String | 认证值 |
| `enableCookie` | String | 是否启用 Cookie |
| `cookieKey` | String | Cookie 键名 |
| `cookieValue` | String | Cookie 值（运行时） |
| `appId` | String | 应用 ID |
| `clientSecret` | String | 客户端密钥 |
| `clientId` | String | 客户端 ID |
| `resource` | String | 资源标识 |
| `grantType` | String | 授权类型 |
| `serviceUrl` | String | 服务基础地址 |
| `tokenUrl` | String | Token 地址 |
| `archiveUrl` | String | 归档地址 |
| `ssoUrl` | String | SSO 地址 |

### 5.3 状态字段

| 字段 | 类型 | 修饰符 | 说明 |
|------|------|--------|------|
| `cachedToken` | TokenResponse | `volatile` | 缓存的 Token |
| `config` | ConcurrentHashMap | `volatile` | 配置存储 |
| `configSupplier` | Supplier | `volatile` | 配置供应器 |
| `configFunction` | Function | `volatile` | 配置函数 |
| `configKey` | String | `volatile` | 配置键 |

---

## 6. 使用示例

### 6.1 初始化配置

```java
// 方式1：Supplier 动态配置
FPApi.initConfig(() -> {
    Map<String, Object> config = new ConcurrentHashMap<>();
    config.put("serviceUrl", "https://fp.example.com/");
    config.put("tokenUrl", "/oauth/token?appId=%s");
    config.put("archiveUrl", "/api/invoice/archive");
    config.put("authType", "header");
    config.put("authKey", "Authorization");
    config.put("appId", "myAppId");
    config.put("enableCookie", "false");
    config.put("rateLimit", 30);
    config.put("enableRetry", "true");
    return config;
});

// 方式2：Map 静态配置
Map<String, Object> config = new HashMap<>();
config.put("serviceUrl", "https://fp.example.com/");
FPApi.initConfig(config);
```

### 6.2 推送单条发票

```java
ElectronicInvoiceModel model = ElectronicInvoiceModel.builder()
    .dataType("invoice")
    .dataId("INV-001")
    .files(new File[] { new File("/tmp/invoice.pdf") })
    .build();

Map<String, Object> options = new HashMap<>();
options.put("async", false);
options.put("openId", "user123");

ElectronicInvoiceResponse response = FPApi.postElectronicInvoice(model, config, options);
if (response.isSuccess()) {
    // 处理成功
} else {
    // 处理失败，response.getMessage() 获取错误信息
}
```

### 6.3 批量推送发票

```java
List<ElectronicInvoiceModel> list = Arrays.asList(model1, model2, model3);
Map<String, Object> options = new HashMap<>();
options.put("rateLimit", 10);  // 每分钟 10 次

List<Response<ElectronicInvoiceModel>> responses = 
    FPApi.postElectronicInvoice(list, config, options);
```

### 6.4 文件列表批量查验

```java
List<File> files = Arrays.asList(
    new File("/tmp/invoice1.pdf"),
    new File("/tmp/invoice2.pdf")
);
List<Object> sourceList = Arrays.asList(sourceMap1, sourceMap2);

List<Response<ElectronicInvoiceModel>> responses = FPApi.postElectronicInvoice(
    "invoice", "BATCH-001", files, sourceList, config);
```

---

## 7. 错误处理机制

FPApi **不抛出自定义异常**，所有错误通过 `Response` 对象返回：

| 错误场景 | 处理方式 | Response 内容 |
|----------|----------|---------------|
| URL 为空 | 返回空 Response | `message="没有指定URL！"` |
| 响应解析为 null | 调用 `retryRequest` | 重试或 `message="响应内容为空！"` |
| 响应非 JSON | 返回空 Response | `message="响应内容不是Json格式！..."` |
| 反序列化异常 | 返回空 Response | `message="反序列化发生异常！错误信息：..."` |
| 请求超时（MULTIPLE） | `Response.failure` | `message="请求超时"` |
| 队列已满（MULTIPLE） | `Response.failure` | `message="当前系统繁忙，请稍候再试！"` |
| 推送异常（MULTIPLE） | `Response.failure` | `message=e.getMessage()` |

> **重要**：模块中**不存在** `FPException` 自定义异常类。错误处理统一通过 `Response.failure()` 静态工厂方法构建失败响应。详见 [错误码文档](../06-reference/error-codes.md)。

---

## 8. 注意事项

1. **Token 递归风险**：`getToken()` 写锁获取失败时递归调用自身，极端高并发下可能栈溢出
2. **配置变更清 Token**：`initConfig(Map)` 会调用 `clearToken()`，配置变更后需重新获取 Token
3. **rateLimit 默认值**：列表推送时 `rateLimit` 默认 30，可通过 options 或 config 覆盖
4. **Content-Type 覆盖**：`postElectronicInvoice` 会强制设置 `Content-Type: multipart/form-data`，覆盖原有 headers
5. **日志受 config.debug 控制**：`log()` 方法读取 `config.debug`，默认 false 时不输出 DEBUG 日志
