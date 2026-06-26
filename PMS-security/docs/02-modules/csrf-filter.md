# CSRF 防护组件

## 1. 组件清单

| 类 | 文件 | 说明 |
|----|------|------|
| `CSRFTokenManager` | `csrf/CSRFTokenManager.java` | Token 生成/存储/提取（final 类，私有构造） |
| `CsrfFilter` | `csrf/CsrfFilter.java` | Servlet Filter（Struts2 环境） |
| `CsrfInterceptor` | `csrf/CsrfInterceptor.java` | Spring MVC Interceptor |
| `CsrfValidateFailedException` | `csrf/CsrfValidateFailedException.java` | 校验失败异常 |

---

## 2. CSRFTokenManager

### 2.1 类定义

```java
public final class CSRFTokenManager {
    private CSRFTokenManager() {}
    private CSRFTokenManager(String csrfTokenName) { ... }
}
```

- `final` 类，不可继承
- 私有构造，纯静态方法工具类

### 2.2 常量

| 常量 | 值 | 说明 |
|------|-----|------|
| `CSRF_PARAM_NAME_DEFAULT` | `__RequestVerificationToken` | 默认 Token 参数名 |
| `CSRF_TOKEN_FOR_SESSION_ATTR_NAME` | `com.dp.plat.security.csrf.CSRFTokenManager.tokenval` | Session 属性名 |
| `CSRF_TOKEN_PARAM_NAME` | `CSRF_TOKEN` | Cookie/Header 标识名 |

### 2.3 方法签名

| 方法 | 返回值 | 说明 |
|------|--------|------|
| `generateToken()` | `String` | 生成 UUID Token |
| `getTokenForSession(HttpSession)` | `String` | 获取/生成会话 Token（synchronized） |
| `getTokenFromRequest(HttpServletRequest)` | `String` | 三通道提取 Token（参数→头→Cookie） |
| `getTokenName()` | `String` | 获取 Token 参数名 |
| `setCsrfTokenName(String)` | `void` | 设置 Token 参数名（静态字段） |

### 2.4 Token 生成实现

```java
public static String generateToken() {
    return UUID.randomUUID().toString();
}
```

### 2.5 会话存储实现

```java
public static String getTokenForSession(HttpSession session) {
    synchronized (session) {
        token = (String) session.getAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME);
        if (null == token) {
            token = generateToken();
            session.setAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME, token);
        }
    }
    return token;
}
```

### 2.6 三通道提取实现

```java
public static String getTokenFromRequest(HttpServletRequest request) {
    String csrfToken = request.getParameter(getTokenName());  // 1. 参数
    if (StringUtils.isEmpty(csrfToken)) {
        csrfToken = request.getHeader(getTokenName());        // 2. 头
    }
    if (StringUtils.isEmpty(csrfToken)) {
        Cookie[] cookies = request.getCookies();              // 3. Cookie
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (getTokenName().equals(cookie.getName())) {
                    csrfToken = cookie.getValue();
                    break;
                }
            }
        }
    }
    return csrfToken;
}
```

> 使用 `org.apache.commons.lang3.StringUtils.isEmpty()`

---

## 3. CsrfFilter

### 3.1 类定义

```java
public class CsrfFilter implements Filter {
    FilterConfig filterConfig = null;
    public void init(FilterConfig filterConfig) { ... }
    public void destroy() { ... }
    public void doFilter(ServletRequest, ServletResponse, FilterChain) { ... }
    public boolean isValid(HttpServletRequest, HttpServletResponse) { ... }
    private boolean isNeedValidatorCsrfToken(String method) { ... }
}
```

### 3.2 doFilter 流程

```java
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
    String servletPath = httpRequest.getServletPath();
    // 1. 排除路径检查
    String excludePattern = filterConfig.getInitParameter("excludePattern");
    if (StringUtils.isNotBlank(excludePattern) && servletPath.matches(excludePattern)) {
        chain.doFilter(request, response);
        return;
    }
    // 2. 校验
    if (isValid(httpRequest, httpResponse)) {
        // 3. 下发 Token
        String token = CSRFTokenManager.getTokenForSession(httpRequest.getSession());
        String tokenName = CSRFTokenManager.getTokenName();
        httpResponse.addHeader("CSRF_TOKEN", tokenName);
        httpResponse.addHeader(tokenName, token);
        // 4. 下发 Cookie
        Cookie cookie = new Cookie("CSRF_TOKEN", tokenName);
        cookie.setPath(contextPath);
        cookie.setHttpOnly(true);
        httpResponse.addCookie(cookie);
        cookie = new Cookie(tokenName, token);
        cookie.setPath(contextPath);
        cookie.setHttpOnly(true);
        httpResponse.addCookie(cookie);
        chain.doFilter(request, response);
        return;
    }
    // 5. 校验失败
    request.getRequestDispatcher("/404.jsp").forward(request, response);
}
```

### 3.3 isValid 校验

```java
public boolean isValid(HttpServletRequest request, HttpServletResponse response) {
    String method = request.getMethod();
    HttpSession session = request.getSession();
    String serverCsrfToken = (String) session.getAttribute(CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME);
    if (StringUtils.isEmpty(serverCsrfToken)) {
        CSRFTokenManager.getTokenForSession(session);  // 首次生成
    } else {
        if (isNeedValidatorCsrfToken(method)) {
            String clientCsrfToken = CSRFTokenManager.getTokenFromRequest(request);
            if (StringUtils.isEmpty(clientCsrfToken) || !clientCsrfToken.equals(serverCsrfToken)) {
                return false;
            }
        }
    }
    return true;
}
```

### 3.4 方法过滤

```java
private boolean isNeedValidatorCsrfToken(String method) {
    return true;  // 所有方法都校验
    // return "POST".equals(method) || "DELETE".equals(method) || "PUT".equals(method);
}
```

> ⚠️ POST/DELETE/PUT 过滤被注释，**所有方法（含 GET）都校验**。

---

## 4. CsrfInterceptor

### 4.1 类定义

```java
public class CsrfInterceptor implements AsyncHandlerInterceptor {
    public boolean preHandle(HttpServletRequest, HttpServletResponse, Object handler) { ... }
    public void postHandle(HttpServletRequest, HttpServletResponse, Object handler, ModelAndView) { ... }
    private boolean isNeedValidatorCsrfToken(String method) { ... }
}
```

> 实现 `AsyncHandlerInterceptor`（继承 `HandlerInterceptor`），支持异步请求。

### 4.2 preHandle

```java
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String method = request.getMethod();
    HttpSession session = request.getSession();
    String serverCsrfToken = (String) session.getAttribute(CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME);
    if (StringUtils.isEmpty(serverCsrfToken)) {
        CSRFTokenManager.getTokenForSession(session);
    } else {
        if (isNeedValidatorCsrfToken(method)) {
            String clientCsrfToken = CSRFTokenManager.getTokenFromRequest(request);
            if (StringUtils.isEmpty(clientCsrfToken) || !clientCsrfToken.equals(serverCsrfToken)) {
                throw new CsrfValidateFailedException("csrf token validate failed");
            }
        }
    }
    return true;
}
```

> 使用 `org.springframework.util.StringUtils.isEmpty()`（非 commons-lang3）

### 4.3 postHandle

```java
public void postHandle(HttpServletRequest request, HttpServletResponse response, 
                       Object handler, ModelAndView modelAndView) {
    if (modelAndView != null) {
        Map<String, Object> model = modelAndView.getModel();
        String token = CSRFTokenManager.getTokenForSession(request.getSession());
        model.put(CSRFTokenManager.getTokenName(), token);
        response.addHeader(CSRFTokenManager.getTokenName(), token);
    }
}
```

### 4.4 方法过滤

```java
private boolean isNeedValidatorCsrfToken(String method) {
    return "POST".equals(method) || "DELETE".equals(method) || "PUT".equals(method);
}
```

> 仅 POST/PUT/DELETE 校验，GET 等放行。

---

## 5. CsrfValidateFailedException

```java
public class CsrfValidateFailedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String message;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public CsrfValidateFailedException(String message) {
        super();
        this.message = message;
    }
}
```

> ⚠️ 注意：构造函数调用 `super()`（无参），未传递 message 给父类。`getMessage()` 返回自定义字段，而非 `super.getMessage()`。

---

## 6. 配置示例

### 6.1 PMS-struts（dev）web.xml

```xml
<filter>
    <filter-name>CsrfFilter</filter-name>
    <filter-class>com.dp.plat.security.csrf.CsrfFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>CsrfFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

### 6.2 PMS-springmvc spring-mvc.xml

```xml
<mvc:interceptor>
    <mvc:mapping path="/**"/>
    <mvc:exclude-mapping path="/sys/login.json"/>
    <bean id="csrfInterceptor" class="com.dp.plat.security.csrf.CsrfInterceptor"/>
</mvc:interceptor>
```

---

## 7. 相关文档

| 文档 | 说明 |
|------|------|
| [../01-architecture/csrf-architecture.md](../01-architecture/csrf-architecture.md) | CSRF 架构 |
| [../06-reference/error-codes.md](../06-reference/error-codes.md) | 异常错误码 |
