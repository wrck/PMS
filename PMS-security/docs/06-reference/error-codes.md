# 错误码

## 1. 概述

PMS-security 模块定义的异常/错误码较少，主要为 CSRF 校验失败异常。其他组件（ASEUtil、SQLParser 等）通过返回 null 或空集合处理异常，不抛出业务异常。

---

## 2. 异常类

### 2.1 CsrfValidateFailedException

| 项 | 值 |
|----|-----|
| 类名 | `com.dp.plat.security.csrf.CsrfValidateFailedException` |
| 父类 | `java.lang.RuntimeException` |
| serialVersionUID | `1L` |
| 抛出位置 | `CsrfInterceptor.preHandle()` |
| 触发条件 | POST/PUT/DELETE 请求的客户端 Token 与 Session Token 不匹配 |

#### 类定义

```java
public class CsrfValidateFailedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CsrfValidateFailedException(String message) {
        super();  // 注意：未调用 super(message)
        this.message = message;
    }
}
```

#### 异常消息

| 消息 | 抛出位置 |
|------|---------|
| `csrf token validate failed` | `CsrfInterceptor.preHandle()` |

> ⚠️ 注意：构造函数调用 `super()`（无参），未传递 message 给父类 `RuntimeException`。因此 `super.getMessage()` 返回 null，但覆盖的 `getMessage()` 返回自定义字段。

---

## 3. 非异常错误处理

### 3.1 CsrfFilter 失败处理

CsrfFilter 校验失败时**不抛出异常**，而是 forward 到 `/404.jsp`：

```java
request.getRequestDispatcher("/404.jsp").forward(request, response);
```

| 行为 | 说明 |
|------|------|
| HTTP 状态码 | 404（由 /404.jsp 决定） |
| 客户端可见 | 404 页面 |
| 无异常堆栈 | 静默失败 |

### 3.2 ASEUtil 失败处理

加密/解密失败时返回 `null`：

```java
public static String encrypt(String content, String password) {
    // ...
    try {
        // ...
    } catch (Exception ex) {
        Logger.getLogger(ASEUtil.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;  // 失败返回 null
}
```

| 行为 | 说明 |
|------|------|
| 返回值 | `null` |
| 日志 | `java.util.logging.Logger` SEVERE 级别 |
| 调用方风险 | 未检查 null 可能导致 NPE |

### 3.3 SQLParser 失败处理

SQL 解析失败时 Druid 会抛出 `ParserException`，但本模块未捕获：

```java
public static List<SQLStatement> parseStatements(String sql, DbType dbType) {
    String result = SQLUtils.format(sql, dbType);  // 可能抛出异常
    return SQLUtils.parseStatements(result, dbType);  // 可能抛出异常
}
```

> 调用方需自行 try-catch。

### 3.4 HttpContext 失败处理

`getCurrentRequest()` 在非请求线程调用时返回 null：

```java
public static HttpServletRequest getCurrentRequest() {
    try {
        // ...
    } catch (Throwable e) {
        // 静默吞掉异常
    }
    return null;
}
```

---

## 4. 错误码清单

| 错误标识 | 类型 | 来源 | 处理方式 |
|---------|------|------|---------|
| `csrf token validate failed` | 异常消息 | CsrfInterceptor | 抛出 CsrfValidateFailedException |
| forward `/404.jsp` | HTTP 转发 | CsrfFilter | RequestDispatcher.forward |
| 返回 `null` | 返回值 | ASEUtil | 加密/解密失败 |
| 返回 `null` | 返回值 | HttpContext | 非请求线程 |
| 返回 `""` | 返回值 | HttpContext.getCurrentIp | 无请求时 |
| 返回 `null` | 返回值 | SQLParser.getSecretKey | NoSuchAlgorithmException |
| 抛出 `ParserException` | 异常 | Druid SQLUtils | SQL 语法错误 |

---

## 5. 相关文档

| 文档 | 说明 |
|------|------|
| [../02-modules/csrf-filter.md](../02-modules/csrf-filter.md) | CSRF 组件 |
| [../05-standards/troubleshooting.md](../05-standards/troubleshooting.md) | 故障排查 |
