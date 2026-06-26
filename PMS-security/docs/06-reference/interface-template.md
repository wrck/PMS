# 接口模板

## 1. 概述

PMS-security 是工具库，不提供 HTTP 接口。本文档提供各组件的使用模板，供调用方参考。

---

## 2. CSRF 防护集成模板

### 2.1 Struts2 环境（CsrfFilter）

**web.xml 配置**：

```xml
<filter>
    <filter-name>CsrfFilter</filter-name>
    <filter-class>com.dp.plat.security.csrf.CsrfFilter</filter-class>
    <init-param>
        <param-name>excludePattern</param-name>
        <param-value>/static/.*</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>CsrfFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

**JSP 表单**：

```jsp
<%-- 从 Cookie 读取 Token --%>
<input type="hidden" name="__RequestVerificationToken" 
       value="${cookie['__RequestVerificationToken'].value}"/>
```

### 2.2 Spring MVC 环境（CsrfInterceptor）

**spring-mvc.xml 配置**：

```xml
<mvc:interceptor>
    <mvc:mapping path="/**"/>
    <mvc:exclude-mapping path="/sys/login.json"/>
    <mvc:exclude-mapping path="/captcha.*"/>
    <bean class="com.dp.plat.security.csrf.CsrfInterceptor"/>
</mvc:interceptor>
```

**JSP 表单**：

```jsp
<%-- CsrfInterceptor.postHandle 注入到 Model --%>
<input type="hidden" name="__RequestVerificationToken" 
       value="${__RequestVerificationToken}"/>
```

**AJAX 请求**：

```javascript
// 全局设置 CSRF Token
var csrfToken = "${__RequestVerificationToken}";
$.ajaxSetup({
    beforeSend: function(xhr) {
        xhr.setRequestHeader("__RequestVerificationToken", csrfToken);
    }
});
```

---

## 3. XSS 防护集成模板

### 3.1 Spring MVC 环境（XssFilter）

**web.xml 配置**：

```xml
<filter>
    <filter-name>XssFilter</filter-name>
    <filter-class>com.dp.plat.security.xss.XssFilter</filter-class>
    <init-param>
        <param-name>excludePattern</param-name>
        <param-value>/api/raw/.*</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>XssFilter</filter-name>
    <url-pattern>*.html</url-pattern>
    <url-pattern>*.json</url-pattern>
</filter-mapping>
```

### 3.2 Struts2 环境（XssStrutsInterceptor）

**struts.xml 配置**：

```xml
<interceptor name="XssStrutsInterceptor" 
             class="com.dp.plat.security.xss.struts.XssStrutsInterceptor">
    <param name="enable">true</param>
    <param name="excludeUrls">/base/executeSql.*</param>
    <param name="cleanUrls">/module/prob_*,/probAudit.*,/probAjax_*.*</param>
    <param name="encodeUrls">/*</param>
</interceptor>

<interceptor-stack name="baseStack">
    <interceptor-ref name="XssStrutsInterceptor"/>
    <interceptor-ref name="defaultStack"/>
</interceptor-stack>
```

### 3.3 Struts2 核心替换（M 系列，可选）

**web.xml 配置**：

```xml
<filter>
    <filter-name>struts2</filter-name>
    <filter-class>com.dp.plat.security.xss.struts.MStrutsPrepareAndExecuteFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>struts2</filter-name>
    <url-pattern>*.action</url-pattern>
</filter-mapping>
```

---

## 4. AES 加密使用模板

```java
public class EncryptionExample {
    private static final String SECRET_KEY = "my-strong-password";
    
    public String encryptSensitiveData(String data) {
        if (data == null) return null;
        String encrypted = ASEUtil.encrypt(data, SECRET_KEY);
        if (encrypted == null) {
            throw new RuntimeException("加密失败");
        }
        return encrypted;
    }
    
    public String decryptSensitiveData(String encrypted) {
        if (encrypted == null) return null;
        String decrypted = ASEUtil.decrypt(encrypted, SECRET_KEY);
        if (decrypted == null) {
            throw new RuntimeException("解密失败");
        }
        return decrypted;
    }
}
```

---

## 5. SQL 解析使用模板

### 5.1 表名白名单校验

```java
public class SqlValidator {
    private static final String ALLOWED_TABLES = "pm_.*|fnd_.*|t_user.*";
    
    public void validateSql(String sql) {
        SqlParserResult result = SQLParser.matcherSqlTables(sql, ALLOWED_TABLES, DbType.mysql);
        if (!result.isValid()) {
            throw new SecurityException("非法表名: " + result.getMatchTables());
        }
    }
}
```

### 5.2 SQL 变量填充

```java
public class SqlBuilder {
    public String buildSql(String template, Map<String, Object> params) {
        return SQLParser.fillSqlParams(template, params);
    }
}

// 使用
String sql = "SELECT * FROM pm_project WHERE createBy = '${userName}'";
Map<String, Object> params = new HashMap<>();
params.put("userName", "w02611");
String finalSql = new SqlBuilder().buildSql(sql, params);
```

---

## 6. 验证码使用模板

### 6.1 生成验证码

```java
@Controller
@RequestMapping("/captcha")
public class CaptchaController {
    
    @GetMapping
    public void captcha(HttpServletRequest request, HttpServletResponse response) {
        CaptchaUtil.responseCaptcha(request, response, "CAPTCHA_KEY");
    }
}
```

### 6.2 校验验证码

```java
public boolean validateCaptcha(HttpServletRequest request, String input) {
    String sessionCode = (String) request.getSession().getAttribute("CAPTCHA_KEY");
    if (sessionCode == null || input == null) {
        return false;
    }
    boolean valid = input.equalsIgnoreCase(sessionCode);
    request.getSession().removeAttribute("CAPTCHA_KEY");  // 一次性
    return valid;
}
```

---

## 7. HTTP 上下文使用模板

```java
public class RequestHelper {
    public void logRequest() {
        HttpServletRequest request = HttpContext.getCurrentRequest();
        if (request == null) return;
        
        String ip = HttpContext.getCurrentIp(request);
        String uri = request.getRequestURI();
        String method = request.getMethod();
        boolean isAjax = HttpContext.isAjax();
        
        logger.info("{} {} {} (Ajax: {}, IP: {})", method, uri, 
                    isAjax ? "AJAX" : "HTTP", isAjax, ip);
    }
    
    public String getBaseUrl() {
        return HttpContext.baseUri();
    }
}
```

---

## 8. 密码拦截器实现模板

```java
public class CustomPasswordInterceptor extends PasswordInterceptor {
    
    @Override
    public boolean isNeedRedirect(HttpServletRequest request) {
        // 1. 检查是否已认证
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return false;
        }
        
        // 2. 检查是否密码修改页面
        String servletPath = request.getServletPath();
        if (getRedirect() != null && getRedirect().contains(servletPath)) {
            return false;
        }
        
        // 3. 检查 Session 缓存
        HttpSession session = request.getSession();
        Object cached = session.getAttribute("needChangePwd");
        if (cached != null) {
            return Boolean.TRUE.equals(cached);
        }
        
        // 4. 查询用户密码状态
        Principal principal = (Principal) subject.getPrincipal();
        boolean needChange = principal.getNeedChangePwd();
        session.setAttribute("needChangePwd", needChange);
        return needChange;
    }
}
```

**配置**：

```xml
<mvc:interceptor>
    <mvc:mapping path="/**"/>
    <mvc:exclude-mapping path="/password.*"/>
    <mvc:exclude-mapping path="/modifyPassword.*"/>
    <mvc:exclude-mapping path="/logout.*"/>
    <bean class="com.example.CustomPasswordInterceptor">
        <property name="redirect" value="/password.html?needChangePwd=true"/>
    </bean>
</mvc:interceptor>
```

---

## 9. 相关文档

| 文档 | 说明 |
|------|------|
| [code-examples.md](code-examples.md) | 代码示例 |
| [../05-standards/coding-standards.md](../05-standards/coding-standards.md) | 编码规范 |
| [glossary.md](glossary.md) | 术语表 |
