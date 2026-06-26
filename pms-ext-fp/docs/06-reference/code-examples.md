# pms-ext-fp 代码示例与参考

---

## 1. FPApi 工具类示例

### 1.1 获取 Token

```java
@Component("fpApi")
public class FPApi implements DisposableBean {

    private static volatile TokenResponse cachedToken;
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    public static TokenResponse getToken() {
        // 1. 读锁检查缓存 Token 是否存在且未过期
        try {
            lock.readLock().lock();
            if (cachedToken != null) {
                String expiresOn = cachedToken.getExpiresOn();
                // ... 过期时间计算 ...
                long expiresOnTimeInMillis = Long.parseLong(expiresOn) * 1000;
                if (expiresOnTimeInMillis >= Calendar.getInstance().getTimeInMillis()) {
                    return cachedToken;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        // 2. 写锁刷新 Token
        boolean locked = lock.writeLock().tryLock();
        if (!locked) {
            return getToken(); // 递归等待
        }
        try {
            clearToken();
            TokenRequest request = new TokenRequest();
            request.oauthType(MapUtil.getStr(config, "provider"));
            request.code(MapUtil.getStr(config, "openId"));
            TokenResponse tokenResponse = get(tokenUrl, request, false);
            if (tokenResponse != null && tokenResponse.getAccessToken() != null) {
                cachedToken = tokenResponse;
                cachedToken.setTimestamp(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            }
            return tokenResponse;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
```

### 1.2 推送单条发票

```java
public static <T> ElectronicInvoiceResponse postElectronicInvoice(T data) {
    return postElectronicInvoice(data, null);
}

public static <T> ElectronicInvoiceResponse postElectronicInvoice(T data, Map<String, Object> config, Map<String, Object> options) {
    config = FPApi.initConfig(config);
    if (options == null) {
        options = new HashMap<>();
    }
    options.put("responseType", ElectronicInvoiceResponse.class);
    options.put("headers", Collections.singletonMap("Content-Type", "multipart/form-data"));
    return (ElectronicInvoiceResponse) pushSingleData(data, archiveUrl, config, options);
}
```

### 1.3 批量发票查验

```java
public static <T> List<Response<T>> postElectronicInvoice(List<T> list, Map<String, Object> config, Map<String, Object> options) {
    config = FPApi.initConfig(config);
    if (options == null) {
        options = new HashMap<>();
    }
    options.put("responseType", ElectronicInvoiceResponse.class);
    options.put("headers", Collections.singletonMap("Content-Type", "multipart/form-data"));
    return pushSingleData(list, archiveUrl, MapUtil.getInt(options, "rateLimit", 30), config, MULTIPLE, options);
}
```

---

## 2. InvoiceUtil 工具类示例

### 2.1 发票类型判断

```java
public class InvoiceUtil {

    public static boolean checkFileInvoiceType(Map<String, Object> invoice, Map<String, Object> config) {
        if (invoice == null) {
            return false;
        }
        String condition = (String) invoice.get("condition");
        condition = MapUtils.getString(config, "invoiceTypeCondition", condition);
        try {
            if (StringUtils.isNotBlank(condition)) {
                Map<String, Object> env = new HashMap<String, Object>();
                env.put("entity", Collections.singletonMap("entity", invoice));
                return Boolean.TRUE.equals(AviatorUtils.exceute(condition, env));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return !(!MapUtil.getBool(invoice, "needVerify", false)
                 && StringUtils.isBlank(MapUtil.getStr(invoice, "invoice_number")));
    }
}
```

### 2.2 获取发票编号

```java
public static String getUniqueInvoiceNumber(Map<String, Object> invoice) {
    if (invoice == null || invoice.isEmpty()) {
        return null;
    }
    String uniqueInvoiceNumber = MapUtil.getStr(invoice, "uniqueInvoiceNumber");
    if (StringUtils.isNotBlank(uniqueInvoiceNumber)) {
        return uniqueInvoiceNumber;
    }
    String invoiceCode = MapUtil.getStr(invoice, "invoice_code");
    String invoiceNumber = MapUtil.getStr(invoice, "invoice_number");
    List<String> parts = Arrays.asList(invoiceCode, invoiceNumber).stream()
            .filter(s -> s != null && StringUtils.isNotBlank(s))
            .collect(Collectors.toList());
    if (parts.isEmpty()) {
        return null;
    }
    return StringUtils.join(parts, "-");
}
```

---

## 3. Token 管理示例

### 3.1 Token 缓存（读写锁 + volatile）

```java
private static volatile TokenResponse cachedToken;
private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

public static TokenResponse getToken() {
    lock.readLock().lock();
    try {
        if (cachedToken != null && !isTokenExpired(cachedToken)) {
            return cachedToken;
        }
    } finally {
        lock.readLock().unlock();
    }

    boolean locked = lock.writeLock().tryLock();
    if (!locked) {
        return getToken(); // 等待其他线程刷新
    }

    try {
        clearToken();
        TokenRequest request = new TokenRequest();
        request.oauthType(MapUtil.getStr(config, "provider"));
        TokenResponse tokenResponse = get(tokenUrl, request, false);
        if (tokenResponse != null && tokenResponse.getAccessToken() != null) {
            cachedToken = tokenResponse;
        }
        return tokenResponse;
    } finally {
        lock.writeLock().unlock();
    }
}
```

---

## 4. 错误处理示例

pms-ext-fp 模块**不存在自定义异常类**（无 FPException），所有错误通过 `Response` 对象返回：

```java
// 调用 FPApi 并检查响应
ElectronicInvoiceResponse response = FPApi.postElectronicInvoice(data);

if (response == null || !response.isSuccess()) {
    String errorMsg = response != null ? response.getMessage() : "响应为空";
    log.error("FP API 调用失败: {}", errorMsg);
    throw new RuntimeException(errorMsg);
}

// 处理成功响应
List<InvoiceProviderInfo> data = response.getData();
```

### 4.1 Response.failure 静态工厂方法

```java
// Response.java 中的错误处理（非自定义异常）
public static <T> Response<T> failure(String message) {
    return new Response<T>().message(message);
}

// MULTIPLE 模式下的错误处理示例
try {
    return pushData(data, syncUrl, config, options);
} catch (RejectedExecutionException e) {
    return Response.failure("当前系统繁忙，请稍候再试！", responseType);
} catch (Exception e) {
    return Response.failure(e.getMessage(), responseType);
}
```
