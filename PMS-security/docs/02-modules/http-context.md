# HTTP 上下文组件

## 1. 概述

`HttpContext` 提供 HTTP 请求上下文的静态访问能力，基于 Spring 的 `RequestContextHolder` 获取当前线程的请求对象，支持请求类型判断和 IP 地址获取。

---

## 2. 类定义

```java
package com.dp.plat.security.context;

public class HttpContext {
    // 全部为静态方法
}
```

---

## 3. 方法签名

| 方法 | 返回值 | 说明 |
|------|--------|------|
| `getCurrentRequest()` | `HttpServletRequest` | 获取当前请求 |
| `getCurrentSession()` | `HttpSession` | 获取当前会话 |
| `isAjax()` | `boolean` | 判断是否 AJAX 请求 |
| `isJSON()` | `boolean` | 判断是否 JSON 请求 |
| `isHTML()` | `boolean` | 判断是否 HTML 请求 |
| `isExcel()` | `boolean` | 判断是否 Excel 请求 |
| `baseUri()` | `String` | 获取基础 URI |
| `getCurrentIp(HttpServletRequest)` | `String` | 获取客户端 IP（可传入 request） |
| `getCurrentIp()` | `String` | 获取客户端 IP（自动获取 request） |

---

## 4. 核心实现

### 4.1 getCurrentRequest

```java
public static HttpServletRequest getCurrentRequest() {
    try {
        ServletRequestAttributes servletRequestAttributes = 
            (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        if (servletRequestAttributes != null) {
            return servletRequestAttributes.getRequest();
        }
    } catch (Throwable e) {
        // 静默吞掉异常
    }
    return null;
}
```

- 基于 Spring `RequestContextHolder`，要求请求线程内调用
- 异常时返回 null（不抛出）

### 4.2 getCurrentSession

```java
public static HttpSession getCurrentSession() {
    HttpServletRequest currentRequest = getCurrentRequest();
    HttpSession session = null;
    if (currentRequest != null) {
        session = currentRequest.getSession();
    }
    return session;
}
```

### 4.3 isAjax

```java
public static boolean isAjax() {
    HttpServletRequest request = getCurrentRequest();
    if (request != null) {
        if ((request.getHeader("accept") != null 
             && request.getHeader("accept").indexOf("application/json") > -1)
            || (request.getHeader("X-Requested-With") != null
                && request.getHeader("X-Requested-With").indexOf("XMLHttpRequest") > -1)) {
            return true;
        }
    }
    return false;
}
```

判断条件（满足任一）：
1. `accept` 头包含 `application/json`
2. `X-Requested-With` 头包含 `XMLHttpRequest`

### 4.4 isJSON

```java
public static boolean isJSON() {
    HttpServletRequest request = getCurrentRequest();
    if (request != null) {
        if ((request.getHeader("accept") != null 
             && request.getHeader("accept").indexOf("application/json") > -1)
            || (request.getServletPath() != null
                && request.getServletPath().endsWith(".json"))) {
            return true;
        }
    }
    return false;
}
```

判断条件（满足任一）：
1. `accept` 头包含 `application/json`
2. ServletPath 以 `.json` 结尾

### 4.5 isHTML

```java
public static boolean isHTML() {
    HttpServletRequest request = getCurrentRequest();
    if (request != null) {
        String servletPath = request.getServletPath();
        if ((request.getHeader("accept") != null 
             && request.getHeader("accept").indexOf("text/plain") > -1)
            || (servletPath != null
                && (servletPath.endsWith(".html") || servletPath.endsWith(".htm")
                    || servletPath.indexOf(".") == -1))) {
            return true;
        }
    }
    return false;
}
```

判断条件（满足任一）：
1. `accept` 头包含 `text/plain`
2. ServletPath 以 `.html` 或 `.htm` 结尾
3. ServletPath 不包含 `.`（无扩展名）

### 4.6 isExcel

```java
public static boolean isExcel() {
    HttpServletRequest request = getCurrentRequest();
    if (request != null) {
        String servletPath = request.getServletPath();
        if ((servletPath != null 
             && (servletPath.endsWith(".xlsx") || servletPath.endsWith(".xls")))) {
            return true;
        }
    }
    return false;
}
```

### 4.7 baseUri

```java
public static String baseUri() {
    HttpServletRequest request = getCurrentRequest();
    if (request != null) {
        return request.getScheme() + "://" + request.getServerName() + ":" 
               + request.getServerPort() + request.getContextPath();
    }
    return "";
}
```

示例输出：`http://localhost:8080/pms`

### 4.8 getCurrentIp

```java
public static String getCurrentIp(HttpServletRequest request) {
    if (request == null) {
        request = getCurrentRequest();
    }
    if (request == null) {
        return "";
    }
    String ip = request.getRemoteAddr();
    // 依次尝试代理头
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("x-forwarded-for");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("HTTP_CLIENT_IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getRemoteAddr();
    }
    return ip;
}
```

#### IP 获取优先级

| 顺序 | 来源 | 说明 |
|------|------|------|
| 1 | `request.getRemoteAddr()` | 直接连接 IP |
| 2 | `x-forwarded-for` | 代理转发头 |
| 3 | `Proxy-Client-IP` | 代理客户端 IP |
| 4 | `WL-Proxy-Client-IP` | WebLogic 代理 |
| 5 | `HTTP_CLIENT_IP` | 客户端 IP |
| 6 | `HTTP_X_FORWARDED_FOR` | X-Forwarded-For |
| 7 | `request.getRemoteAddr()` | 回退到直接连接 |

> ⚠️ **安全提示**：代理头可被伪造。注释中提到"从请求头中获取容易被伪造"，但在多层代理环境下仍需依赖这些头。生产环境应配合可信代理白名单使用。

---

## 5. 使用示例

```java
// 获取当前请求
HttpServletRequest request = HttpContext.getCurrentRequest();

// 判断请求类型
if (HttpContext.isAjax()) {
    // 返回 JSON
} else if (HttpContext.isHTML()) {
    // 返回 HTML 视图
}

// 获取基础 URI（用于 JsoupUtil.clean 的 baseUri 参数）
String baseUri = HttpContext.baseUri();

// 获取客户端 IP
String ip = HttpContext.getCurrentIp();
```

---

## 6. 相关文档

| 文档 | 说明 |
|------|------|
| [class-reference.md](class-reference.md) | 类参考清单 |
