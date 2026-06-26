# 类参考文档

> 本文档提供 pms-ext-fp 模块全部 14 个 Java 源文件的完整清单与方法签名参考。
>
> 源码根目录：`src/main/java/com/dp/plat/pms/extend/fp/`

---

## 1. 类清单总览

| 序号 | 包 | 类名 | 类型 | 行数 | 说明 |
|------|-----|------|------|------|------|
| 1 | `util` | `FPApi` | 工具类 | ~2007 | FP 平台 API 调用核心 |
| 2 | `util` | `InvoiceUtil` | 工具类 | ~157 | 发票识别与判断 |
| 3 | `util` | `MultipartBodyBuilder` | 工具类 | ~247 | multipart 表单构建器 |
| 4 | `entity` | `BaseEntity` | 实体类 | ~139 | 基础实体 |
| 5 | `entity` | `InvoiceProviderInfo` | 实体类 | ~490 | 发票提供者信息 |
| 6 | `model` | `Request` | 模型类 | ~133 | 通用请求 |
| 7 | `model` | `RequestBody` | 模型类 | ~108 | 请求体 |
| 8 | `model` | `Response` | 模型类 | ~295 | 通用响应 |
| 9 | `model` | `TokenRequest` | 模型类 | ~117 | Token 请求 |
| 10 | `model` | `TokenResponse` | 模型类 | ~331 | Token 响应 |
| 11 | `model` | `ElectronicInvoiceModel` | 模型类 | ~371 | 电子发票模型 |
| 12 | `model` | `ElectronicInvoiceResponse` | 模型类 | ~12 | 电子发票响应 |
| 13 | `model` | `ElectronicInvoiceIdentifyAndVerifyResponse` | 模型类 | ~12 | 发票验真响应 |
| 14 | `model` | `MsgResponse` | 模型类 | ~12 | 消息响应 |

> 另有测试类 `FPApiTest`（位于 `src/test/java/com/dp/plat/erms/util/`，注意包名与主代码不同），以及 FPApi 的 4 个内部类和 MultipartBodyBuilder 的 3 个内部类。

---

## 2. 工具类（util）

### 2.1 FPApi

```java
@Component("fpApi")
public class FPApi implements DisposableBean
```

**内部类**：
- `HttpClientPool` — Apache HttpClient 连接池管理（单例）
- `OkHttpPool` — OkHttp 连接池管理（单例）

**静态字段**：

| 字段 | 类型 | 修饰符 | 说明 |
|------|------|--------|------|
| `logger` | Logger | `static final` | 日志器 |
| `SYS_FP_API` | String | `static final` | `"sys.fp.api"` |
| `MINUTE` | String | `static final` | `"MINUTE"` |
| `SINGLE` | String | `static final` | `"SINGLE"` |
| `MULTIPLE` | String | `static final` | `"MULTIPLE"` |
| `DEFAULT_TYPE` | TypeReference | `static final` | Map 类型引用 |
| `scheduler` | ScheduledExecutorService | `static final` | 单线程调度池 |
| `fixedExecutor` | ExecutorService | `static final` | 10 线程固定池 |
| `lock` | ReentrantReadWriteLock | `static final` | 公平读写锁 |
| `cachedToken` | TokenResponse | `static volatile` | 缓存 Token |
| `config` | ConcurrentHashMap | `static volatile` | 配置存储 |
| `configSupplier` | Supplier | `static volatile` | 配置供应器 |
| `configFunction` | Function | `static volatile` | 配置函数 |
| `configKey` | String | `static volatile` | 配置键 |

**配置字段**（反射注入，均为 `static` String）：`authType`、`authKey`、`authValue`、`enableCookie`、`cookieKey`、`cookieValue`、`appId`、`clientSecret`、`clientId`、`resource`、`grantType`、`serviceUrl`、`tokenUrl`、`archiveUrl`、`ssoUrl`

**公共方法签名**：

```java
// 配置管理
public static Map<String, Object> getConfig()
public static Map<String, Object> initConfig(Supplier<ConcurrentHashMap<String, Object>> configSupplier)
public static Map<String, Object> initConfig(Function<String, ConcurrentHashMap<String, Object>> configFunction, String key)
public static Map<String, Object> initConfig(Map<String, Object> config)

// Token 管理
public static TokenResponse getToken()

// 发票查验
public static List<Response<ElectronicInvoiceModel>> postElectronicInvoice(String dataType, String dataId, List<File> files, List<Object> sourceList, Map<String, Object> config)
public static List<Response<ElectronicInvoiceModel>> postElectronicInvoice(String dataType, String dataId, List<File> files, List<Object> sourceList, Map<String, Object> config, Map<String, Object> options)
public static <T> ElectronicInvoiceResponse postElectronicInvoice(T data)
public static <T> ElectronicInvoiceResponse postElectronicInvoice(T data, Map<String, Object> config)
public static <T> ElectronicInvoiceResponse postElectronicInvoice(T data, Map<String, Object> config, Map<String, Object> options)
public static <T> List<Response<T>> postElectronicInvoice(List<T> list)
public static <T> List<Response<T>> postElectronicInvoice(List<T> list, Map<String, Object> config)
public static <T> List<Response<T>> postElectronicInvoice(List<T> list, Map<String, Object> config, Map<String, Object> options)

// 数据推送
public static <T> List<Response<T>> pushListData(List<T> list, String syncUrl, Integer rateLimit, Map<String, Object> config, Map<String, Object> options)
public static <T> List<Response<T>> pushListData(List<T> list, String syncUrl, Integer rateLimit, Map<String, Object> config, String limitType, Map<String, Object> options)
public static <T> List<Response<T>> pushSingleData(List<T> list, String syncUrl, Integer rateLimit, Map<String, Object> config, String limitType, Map<String, Object> options)
public static <T> List<Response<T>> pushData(List<T> list, String syncUrl, Integer rateLimit, Map<String, Object> config, String limitType, boolean splitToList, Map<String, Object> options)

// HTTP 请求
public static <T extends Response<E>, E> T postForm(String url, Request<T> params)
public static <T extends Response<E>, E> T postForm(String url, Request<T> params, boolean needAuth)
public static <T extends Response<E>, E> T postForm(String url, Request<T> params, boolean needAuth, Map<String, Object> options)
public static <T extends Response<E>, E> T postBody(String url, Request<T> params)
public static <T extends Response<E>, E> T postBody(String url, Request<T> params, Map<String, Object> options)
public static <T extends Response<E>, E> T postBody(String url, Request<T> params, boolean needAuth)
public static <T extends Response<E>, E> T postBody(String url, Request<T> params, boolean needAuth, Map<String, Object> options)
public static <T extends Response<E>, E> T post(String url, Request<T> request, boolean isForm, boolean needAuth)
public static <T extends Response<E>, E> T post(String url, Request<T> request, boolean isForm, boolean needAuth, Map<String, Object> options)
public static <T extends Response<E>, E> T get(String url, Request<T> request)
public static <T extends Response<E>, E> T get(String url, Request<T> request, boolean needAuth)
public static <T extends Response<E>, E> T request(String method, String url, Request<T> request, boolean isForm, boolean needAuth)
public static <T extends Response<E>, E> T request(String method, String url, Request<T> request, boolean isForm, boolean needAuth, Map<String, Object> options)

// HTTP 客户端实现
public static <T extends Response<E>, E> T requestWithHutool(String method, String url, Request<T> request, boolean isForm, boolean needAuth, Map<String, Object> options)
public static <T extends Response<E>, E> T requestWithPool(String method, String url, Request<T> request, boolean isForm, boolean needAuth, Map<String, Object> options)
public static <T extends Response<E>, E> T requestWithOkHttp(String method, String url, Request<T> request, boolean isForm, boolean needAuth, Map<String, Object> options)
public static <T extends Response<E>, E> T retryRequest(String method, String url, Request<T> request, boolean isForm, boolean needAuth, Map<String, Object> options)

// 认证注入
@Deprecated public static void initAuthorization(Request<?> request)
public static void initAuthorization(HttpRequest httpRequest)
public static String initAuthorization(String url, URI uri)
public static void initAuthorization(HttpRequestBase httpRequest)
public static void initAuthorization(okhttp3.Request.Builder requestBuilder)
public static void initApikey(HttpRequest httpRequest)
public static String initCookie(String existingCookie, String key, String value)
public static String getCookieValue(Response response, String name)

// 工具方法
public static Collection<Object> wrapCollection(Object target)
public static <T> Collection<T> filterCollection(Collection<T> collection)
public static Map<String, Object> beanToMap(Object object)
public static Map toJSONMap(Object object)
public static String toJSONString(Object object, SerializerFeature... serializerFeatures)

// 日志
public static void logInfo(String format, Object... arguments)
public static void logDebug(String format, Object... arguments)
public static void log(String format, Object... arguments)
public static void log(String format, boolean isDebug, Object... arguments)

// 生命周期
@PostConstruct public void init()
public void destroy() throws Exception
```

**私有方法**：

```java
private static Map<String, Object> initConfig()
private static void clearToken()
private static <T> List<Response<T>> schedulePushData(List<Object> list, String syncUrl, Map<String, Object> config, int delay, TimeUnit timeUnit, Map<String, Object> options)
private static <T> List<Response<T>> multiplePushData(List<Object> list, String syncUrl, Map<String, Object> config, int delay, TimeUnit timeUnit, Map<String, Object> options)
private static <T> Response<T> pushListData(List<T> list, String syncUrl, Map<String, Object> config)
private static <T> Response<T> pushListData(List<T> list, String syncUrl, Map<String, Object> config, Map<String, Object> options)
private static <T> Response<T> pushSingleData(T data, String syncUrl, Map<String, Object> config)
private static <T> Response<T> pushSingleData(T data, String syncUrl, Map<String, Object> config, Map<String, Object> options)
private static <T> Response<T> pushData(Object data, String syncUrl, Map<String, Object> config)
private static <T> Response<T> pushData(Object data, String syncUrl, Map<String, Object> config, Map<String, Object> options)
private static String sanitizeValue(Object value)
private static boolean containsChinese(String value)
```

### 2.2 InvoiceUtil

```java
public class InvoiceUtil
```

**字段**：

| 字段 | 类型 | 修饰符 | 说明 |
|------|------|--------|------|
| `configSupplier` | Supplier<Map<String, Object>> | `private static` | 配置供应器 |

**方法签名**：

```java
public synchronized static void initConfig(Supplier<Map<String, Object>> configSupplier)
public static Map<String, Object> getConfig()
public static String getUniqueInvoiceNumber(Map<String, Object> invoice)
public static <T> T getFileInvoiceType(T defalutValue)
public static <T> T getFileInvoiceType(Map<String, Object> config, T defalutValue)
public static <T> T getFileInspectionType(T defalutValue)
public static <T> T getFileInspectionType(Map<String, Object> config, T defalutValue)
public static boolean checkFileInvoiceType(Map<String, Object> invoice)
public static boolean checkFileInvoiceType(Map<String, Object> invoice, Map<String, Object> config)
public static boolean checkFileInvoiceStatus(Map<String, Object> invoice)
public static boolean checkFileInvoiceStatus(Map<String, Object> invoice, Map<String, Object> config)
```

### 2.3 MultipartBodyBuilder

```java
public class MultipartBodyBuilder
```

**内部类**：
- `MultipartFormItem`（private static）— 表单项
- `StreamPart`（private static）— 流部分
- `InputStreamRequestBody`（private static，继承 `okhttp3.RequestBody`）— OkHttp 流请求体

**字段**：

| 字段 | 类型 | 修饰符 | 说明 |
|------|------|--------|------|
| `formItems` | List<MultipartFormItem> | `private final` | 表单项列表 |

**方法签名**：

```java
public MultipartBodyBuilder form(Map<String, Object> form)
public MultipartBodyBuilder form(String name, Object value)
public MultipartBodyBuilder form(String name, File file)
public MultipartBodyBuilder form(String name, File[] files)
public MultipartBodyBuilder form(String name, String filename, InputStream inputStream, String contentType)
public MultipartBody.Builder buildOkHttp() throws IOException
public MultipartEntityBuilder buildHttp()
```

**私有方法**：

```java
private MultipartBodyBuilder putToForm(String name, String value)
private String guessContentType(File file)
```

---

## 3. 实体类（entity）

### 3.1 BaseEntity

```java
public class BaseEntity implements Serializable
```

**字段**：`id`(Integer)、`createBy`(String)、`createTime`(Date)、`updateBy`(String)、`updateTime`(Date)、`customInfo`(Map<String,Object>)

**方法签名**：

```java
public Integer getId() / public void setId(Integer id)
public String getCreateBy() / public void setCreateBy(String createBy)
public Date getCreateTime() / public void setCreateTime(Date createTime)
public String getUpdateBy() / public void setUpdateBy(String updateBy)
public Date getUpdateTime() / public void setUpdateTime(Date updateTime)
public Map<String, Object> getCustomInfo() / public void setCustomInfo(Map<String, Object> customInfo)
public Object getCustomInfoByKey(String key)
public Object getCustomInfoByKey(String key, Object defaultValue)
public void setCustomInfoByKey(String key, Object value)
protected String toIndentedString(Object o)
```

### 3.2 InvoiceProviderInfo

```java
public class InvoiceProviderInfo extends BaseEntity
```

**字段**：`invoiceId`、`provider`、`openId`、`electricHash`、`eSignature`、`eDocModified`、`eSignDate`、`fileSize`、`fileExt`、`downloadPath`、`uploadPath`、`sourceUrl`、`status`、`createBy`、`createTime`、`updateBy`、`updateTime`、`signatureInfo`、`query`(ElectronicInvoiceModel)、`info`(Map<String,Object>)

**构造方法**：5 个（见 entity-model-reference.md）

**特殊方法**：

```java
@JSONField(name = "query", deserialize = true)
public void setQuery(String query)  // 从 JSON 字符串反序列化

@JSONField(name = "info", deserialize = true)
public void setInfo(String info)    // 从 JSON 字符串反序列化
```

---

## 4. 模型类（model）

### 4.1 Request<T>

```java
public class Request<T> implements Serializable
```

**字段**：`responseType`(Type)、`headers`(Map<String,String>)、`request`(Object)

**静态字段**：`classTypeCache`(ConcurrentMap<Type, Type>)

**方法签名**：

```java
public Request<T> request(RequestBody request)
public Object getRequest() / public void setRequest(Object request)
public Type getResponseType() / public void setResponseType(Type responseType)
public Map<String, String> getHeaders() / public void setHeaders(Map<String, String> headers)
```

### 4.2 RequestBody

```java
public class RequestBody
```

**字段**：`func`(String)、`data`(List<?>)

**方法签名**：

```java
public RequestBody func(String func)
@NotNull public String getFunc() / public void setFunc(String func)
public RequestBody data(List<?> data)
public List<?> getData() / public void setData(List<?> data)
```

### 4.3 Response<T>

```java
public class Response<T> implements Serializable
```

**常量**：`SUCCESS_CODE = 0`（单个 Integer 值，非数组）

**字段**：`request`(Request<T>)、`code`(Integer)、`message`(String)、`data`(List<T>)、`extend`(Map<String,Object>)、`isSuccess`(Boolean)、`headers`(Map<String,List<String>>)

**静态方法**：

```java
public static <T> Response<T> failure(String message)
public static <T, R extends Response<T>> R failure(String message, Type responseType)
public static <T, R extends Response<T>> R failure(String message, Class<R> responseClass)
```

**实例方法**：

```java
public Response code(Integer code) / public Integer getCode() / public void setCode(Integer code)
public Response message(String message) / public String getMessage() / public void setMessage(String message)
public Response data(List<T> data) / public List<T> getData() / public void setData(List<T> data)
public Response addData(T dataItem)
public List<T> getList() / public void setList(List<T> data)
public Request getRequest() / public void setRequest(Request request)
public Map<String, List<String>> getHeaders() / public void setHeaders(Map<String, List<String>> headers)
public Response extend(Map<String, Object> extend) / public Map<String, Object> getExtend() / public void setExtend(Map<String, Object> extend)
public boolean isSuccess()
public Boolean getIsSuccess() / public void setIsSuccess(Boolean isSuccess)
public long getDataSize()
```

### 4.4 TokenRequest

```java
public class TokenRequest extends Request<TokenResponse> implements Serializable
```

**字段**：`oauthType`(String)、`code`(String)、`nickName`(String)

**方法**：链式 setter + getter（oauthType/code/nickName）

### 4.5 TokenResponse

```java
public class TokenResponse extends Response<Object> implements Serializable
```

**字段**：`tokenType`、`expiresIn`、`extExpiresIn`、`expiresOn`、`expireTime`、`notBefore`、`resource`、`accessToken`、`error`、`errorDescription`、`errorCodes`(List<Object>)、`timestamp`、`traceId`、`correlationId`、`errorUri`

**方法**：链式 setter + getter（14 个字段）

### 4.6 ElectronicInvoiceModel

```java
@lombok.Data @lombok.Builder @lombok.NoArgsConstructor @lombok.AllArgsConstructor
public class ElectronicInvoiceModel extends InvoiceProviderInfo
```

**字段**：`async`(boolean)、`dataType`(String)、`dataId`(String)、`invoiceList`(List<ElectronicInvoiceModel>)、`sourceList`(List<Object>)、`files`(File[])、`jsonData`(String)、`invoiceCode`(String)、`invoiceDate`(String)、`invoiceNumber`(String)

**方法**：由 Lombok 生成（getter/setter/builder/equals/hashCode/canEqual/toString）

### 4.7 ElectronicInvoiceResponse

```java
public class ElectronicInvoiceResponse extends Response<InvoiceProviderInfo> implements Serializable
```

无额外字段和方法，继承 Response 全部能力。

### 4.8 ElectronicInvoiceIdentifyAndVerifyResponse

```java
public class ElectronicInvoiceIdentifyAndVerifyResponse extends Response<InvoiceProviderInfo> implements Serializable
```

无额外字段和方法，结构同 ElectronicInvoiceResponse。

### 4.9 MsgResponse

```java
public class MsgResponse extends Response<InvoiceProviderInfo> implements Serializable
```

无额外字段和方法，结构同 ElectronicInvoiceResponse。

---

## 5. 测试类

### 5.1 FPApiTest

> 源码：`src/test/java/com/dp/plat/erms/util/FPApiTest.java`
> 注意：测试包名为 `com.dp.plat.erms.util`，与主代码包名 `com.dp.plat.pms.extend.fp.util` 不同

**内部类**：`FPApiTest$1`、`FPApiTest$2`、`FPApiTest$3`（匿名内部类，可能为测试用 Callable/Predicate）

> 测试用例较少，非完整测试套件。

---

## 6. 内部类清单

### 6.1 FPApi 内部类

| 内部类 | 说明 |
|--------|------|
| `HttpClientPool` | Apache HttpClient 连接池管理（双重检查锁定单例） |
| `OkHttpPool` | OkHttp 连接池管理（双重检查锁定单例） |

### 6.2 MultipartBodyBuilder 内部类

| 内部类 | 继承 | 说明 |
|--------|------|------|
| `MultipartFormItem` | - | 表单项封装（name + value） |
| `StreamPart` | - | 流文件部分封装（filename + inputStream + contentType） |
| `InputStreamRequestBody` | `okhttp3.RequestBody` | OkHttp InputStream 请求体适配器 |

---

## 7. 不存在的类（澄清）

以下类在现有文档中被提及，但**实际源码中并不存在**：

| 类名 | 文档中出现位置 | 实际情况 |
|------|---------------|----------|
| `FPException` | fp-integration.md、coding-standards.md、code-examples.md | **不存在**，错误处理通过 `Response.failure()` 实现 |
| `TokenManager` | system-architecture.md | **不存在**，Token 管理内置于 `FPApi.getToken()` |

> 这些虚构内容将在 [审计文档](../audit/audit-modules.md) 中详细记录。
