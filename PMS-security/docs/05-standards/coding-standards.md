# 编码规范

## 1. XSS 防护规范

### 1.1 Filter 配置

```xml
<filter>
    <filter-name>XssFilter</filter-name>
    <filter-class>com.dp.plat.security.xss.XssFilter</filter-class>
    <init-param>
        <param-name>excludePattern</param-name>
        <param-value>/sys/notifyTemplate/.*\..*</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>XssFilter</filter-name>
    <url-pattern>*.html</url-pattern>
    <url-pattern>*.json</url-pattern>
</filter-mapping>
```

### 1.2 输入清理

```java
// 使用 JsoupUtil 清理 HTML（白名单）
String cleanHtml = JsoupUtil.clean(userInput);

// 使用 JsoupUtil 清理 HTML（表单白名单，含 input/select）
String cleanForm = JsoupUtil.clean(userInput, JsoupUtil.getFormSafelist());

// 使用 JsoupUtil.xssEncode 进行 HTML 实体编码
String encoded = JsoupUtil.xssEncode(userInput);

// 使用 JsoupUtil.escape 进行 HTML 转义（M 系列使用）
String escaped = JsoupUtil.escape(userInput);
```

### 1.3 Struts2 拦截器配置

```xml
<interceptor name="XssStrutsInterceptor" 
             class="com.dp.plat.security.xss.struts.XssStrutsInterceptor">
    <param name="enable">true</param>
    <param name="excludeUrls">/base/executeSql.*</param>
    <param name="cleanUrls">/module/prob_*,/probAudit.*,/probAjax_*.*</param>
    <param name="encodeUrls">/*</param>
</interceptor>
```

> ⚠️ 必须设置 `enable=true`，否则所有 URL 都走 cleanUrls 模式。

---

## 2. CSRF 防护规范

### 2.1 Filter 配置（Struts2）

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

### 2.2 Interceptor 配置（Spring MVC）

```xml
<mvc:interceptor>
    <mvc:mapping path="/**"/>
    <mvc:exclude-mapping path="/sys/login.json"/>
    <bean class="com.dp.plat.security.csrf.CsrfInterceptor"/>
</mvc:interceptor>
```

### 2.3 表单集成

```jsp
<!-- 从 Model 获取 Token（CsrfInterceptor 注入） -->
<input type="hidden" name="__RequestVerificationToken" 
       value="${__RequestVerificationToken}"/>
```

### 2.4 AJAX 集成

```javascript
// 从 Cookie 或 Response Header 获取 Token
var token = getCookie("__RequestVerificationToken");
$.ajaxSetup({
    beforeSend: function(xhr) {
        xhr.setRequestHeader("__RequestVerificationToken", token);
    }
});
```

> ⚠️ Token 参数名为 `__RequestVerificationToken`（非 `_csrf`）。

---

## 3. 数据加密规范

### 3.1 AES 加密

```java
// 加密（双参方法）
String encrypted = ASEUtil.encrypt(plainText, "my-secret-password");

// 解密
String decrypted = ASEUtil.decrypt(encrypted, "my-secret-password");

// null 安全
ASEUtil.encrypt(null, "key");   // 返回 null
ASEUtil.decrypt(null, "key");   // 返回 null
```

> ⚠️ 方法签名为 `encrypt(String content, String password)`，**没有单参版本**。password 为 null 时使用默认密码 `DP_SECRET`。

### 3.2 加密注意事项

- **算法**：AES/ECB/PKCS5Padding
- **密钥派生**：KeyGenerator + SHA1PRNG（128 位）
- **跨平台**：SHA1PRNG 在不同 JDK 间可能不一致，确保加解密使用相同 JDK
- **异常处理**：加密/解密失败返回 null，调用方需检查

---

## 4. SQL 解析规范

### 4.1 表名提取

```java
// 提取 SQL 中的表名
Set<String> tables = SQLParser.parseTables(sql, DbType.mysql);

// 不指定 dbType（自动判断）
Set<String> tables = SQLParser.parseTables(sql);
```

### 4.2 表名白名单校验

```java
// 校验所有表名匹配白名单正则
boolean valid = SQLParser.matcherAll(sql, "pm_.*", DbType.mysql);

// 获取不匹配的表名
SqlParserResult result = SQLParser.matcherSqlTables(sql, "pm_.*", DbType.mysql);
if (!result.isValid()) {
    Set<String> invalidTables = result.getMatchTables();
    // 处理非法表名
}
```

### 4.3 SQL 变量填充

```java
// 填充 ${}、#{}、$$、## 变量
Map<String, Object> values = new HashMap<>();
values.put("userId", "w02611");
values.put("projectId", 123456);
String filled = SQLParser.fillSqlParams(sql, values);
```

> ⚠️ `fillSqlParams` 使用字符串替换，**不是参数化查询**。仅用于可信值的填充，不要用于用户输入。

---

## 5. HTTP 上下文规范

### 5.1 获取请求信息

```java
// 获取当前请求
HttpServletRequest request = HttpContext.getCurrentRequest();

// 获取当前会话
HttpSession session = HttpContext.getCurrentSession();

// 获取基础 URI
String baseUri = HttpContext.baseUri();

// 获取客户端 IP
String ip = HttpContext.getCurrentIp();
```

### 5.2 请求类型判断

```java
if (HttpContext.isAjax()) {
    // AJAX 请求（accept: application/json 或 X-Requested-With: XMLHttpRequest）
}
if (HttpContext.isJSON()) {
    // JSON 请求（accept: application/json 或 .json 结尾）
}
if (HttpContext.isHTML()) {
    // HTML 请求（accept: text/plain 或 .html/.htm 结尾或无扩展名）
}
if (HttpContext.isExcel()) {
    // Excel 请求（.xlsx 或 .xls 结尾）
}
```

---

## 6. 验证码规范

### 6.1 生成验证码

```java
// 在 Controller/Action 中
CaptchaUtil.responseCaptcha(request, response, "captchaKey");
// 验证码已存入 session.getAttribute("captchaKey")
```

### 6.2 校验验证码

```java
String inputCode = request.getParameter("captcha");
String sessionCode = (String) request.getSession().getAttribute("captchaKey");
if (inputCode != null && inputCode.equalsIgnoreCase(sessionCode)) {
    // 验证通过
}
```

---

## 7. 密码拦截器规范

### 7.1 实现子类

```java
public class MyPasswordInterceptor extends PasswordInterceptor {
    @Override
    public boolean isNeedRedirect(HttpServletRequest request) {
        // 实现密码过期检查逻辑
        // 返回 true 则重定向到 redirect 属性指定的 URL
        return false;
    }
}
```

### 7.2 配置

```xml
<mvc:interceptor>
    <mvc:mapping path="/**"/>
    <mvc:exclude-mapping path="/password.*"/>
    <mvc:exclude-mapping path="/modifyPassword.*"/>
    <bean class="com.dp.plat.core.interceptor.PasswordInterceptor">
        <property name="redirect" value="/password.html?needChangePwd=true"/>
    </bean>
</mvc:interceptor>
```

---

## 8. 相关文档

| 文档 | 说明 |
|------|------|
| [security-practices.md](security-practices.md) | 安全最佳实践 |
| [performance-optimization.md](performance-optimization.md) | 性能优化 |
| [troubleshooting.md](troubleshooting.md) | 故障排查 |
