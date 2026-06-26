# PMS-security 代码示例与参考

本文档基于 `com.dp.plat.security` 包下的真实源码整理，提供各组件的实际方法签名、调用示例与典型使用场景。

> ⚠️ **重要说明**：早期版本的本文件曾包含大量虚构内容（如 `CSRFTokenManager.generateToken(HttpSession)`、`ASEUtil.encrypt(String)` 单参版本、`SQLParser` 使用 JSQLParser 等），均与实际源码不符。以下内容已全部基于 `d:\常规软件\QoderCode\workspace\PMS\PMS-security\src\main\java\com\dp\plat\security\` 下的真实源码重写。

---

## 1. CSRF 防护组件示例

### 1.1 CSRFTokenManager（Token 管理器）

**真实方法签名**（`com.dp.plat.security.csrf.CSRFTokenManager`）：

```java
public final class CSRFTokenManager {

    public static final String CSRF_PARAM_NAME_DEFAULT = "__RequestVerificationToken";
    public static final String CSRF_TOKEN_FOR_SESSION_ATTR_NAME = CSRFTokenManager.class.getName() + ".tokenval";
    public static final String CSRF_TOKEN_PARAM_NAME = "CSRF_TOKEN";

    private CSRFTokenManager() {}
    private CSRFTokenManager(String csrfTokenName) {}

    // 无参生成 Token（UUID）
    public static String generateToken();

    // 从 Session 获取（不存在则创建）Token，synchronized 保证并发安全
    public static String getTokenForSession(HttpSession session);

    // 三通道提取客户端 Token：参数 → Header → Cookie
    public static String getTokenFromRequest(HttpServletRequest request);

    public static String getTokenName();
    public static void setCsrfTokenName(String csrfTokenName);
}
```

**使用示例**：

```java
// 1. 获取当前 Session 的 Token（首次访问会自动生成）
HttpSession session = request.getSession();
String token = CSRFTokenManager.getTokenForSession(session);

// 2. 在表单中渲染隐藏字段
// <input type="hidden"
//        name="<%=CSRFTokenManager.getTokenName()%>"
//        value="<%=token%>" />

// 3. 从请求中提取客户端提交的 Token（参数/Header/Cookie 任一通道）
String clientToken = CSRFTokenManager.getTokenFromRequest(request);

// 4. 与 Session 中的 Token 比对
String serverToken = (String) session.getAttribute(CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME);
if (clientToken == null || !clientToken.equals(serverToken)) {
    // 校验失败
}
```

> **关键点**：
> - `generateToken()` 是**无参方法**，仅返回 `UUID.randomUUID().toString()`，不会写入 Session。
> - 写入 Session 的逻辑在 `getTokenForSession(HttpSession)` 中，且使用 `synchronized(session)` 防止并发重复生成。
> - Token 参数名默认为 `__RequestVerificationToken`，可通过 `setCsrfTokenName()` 修改。

### 1.2 CsrfFilter（Servlet Filter，用于 PMS-struts）

**真实方法签名**（`com.dp.plat.security.csrf.CsrfFilter`）：

```java
public class CsrfFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException;
    public void destroy();
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain);

    public boolean isValid(HttpServletRequest request, HttpServletResponse response);
    private boolean isNeedValidatorCsrfToken(String method);  // 当前实现：return true（所有方法都校验）
}
```

**核心逻辑**：

```java
@Override
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
    final HttpServletRequest httpRequest = (HttpServletRequest) request;
    final HttpServletResponse httpResponse = (HttpServletResponse) response;
    String servletPath = httpRequest.getServletPath();

    // 1. excludePattern 排除路径
    if (filterConfig != null) {
        String excludePattern = filterConfig.getInitParameter("excludePattern");
        if (StringUtils.isNotBlank(excludePattern) && servletPath.matches(excludePattern)) {
            chain.doFilter(request, response);
            return;
        }
    }

    // 2. 校验 Token
    if (isValid(httpRequest, httpResponse)) {
        // 3. 校验通过：将 Token 写入 Header 和 Cookie
    String token = CSRFTokenManager.getTokenForSession(httpRequest.getSession());
    String tokenName = CSRFTokenManager.getTokenName();
    httpResponse.addHeader("CSRF_TOKEN", tokenName);
    httpResponse.addHeader(tokenName, token);
    // 双 Cookie：一个存 tokenName 标识，一个存 token 值
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

    // 4. 校验失败：forward 到 /404.jsp
    request.getRequestDispatcher("/404.jsp").forward(request, response);
}
```

> **关键点**：
> - `isNeedValidatorCsrfToken()` 当前实现 `return true`，即**对所有 HTTP 方法都校验**（注释掉的代码显示原计划仅校验 POST/DELETE/PUT）。
> - 校验失败时 forward 到 `/404.jsp`，**不抛异常**。
> - 校验通过后，Token 会同时写入 Response Header 和 Cookie，便于前端 AJAX 读取。

### 1.3 CsrfInterceptor（Spring MVC Interceptor，用于 PMS-springmvc）

**真实方法签名**（`com.dp.plat.security.csrf.CsrfInterceptor`）：

```java
public class CsrfInterceptor implements AsyncHandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler);
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView);
    private boolean isNeedValidatorCsrfToken(String method);  // 仅 POST/PUT/DELETE 校验
}
```

**核心差异**（与 CsrfFilter 对比）：

| 维度 | CsrfFilter | CsrfInterceptor |
|------|-----------|-----------------|
| 容器 | Servlet Filter | Spring MVC Interceptor |
| 校验方法 | **所有方法**（`return true`） | **仅 POST/PUT/DELETE** |
| 失败行为 | forward 到 `/404.jsp` | 抛 `CsrfValidateFailedException` |
| Token 注入 | 写入 Header + Cookie | `postHandle` 写入 Model + Header |

### 1.4 CsrfValidateFailedException

```java
public class CsrfValidateFailedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String message;

    public CsrfValidateFailedException(String message) {
        super();              // ⚠️ 调用 super() 无参，未将 message 传给父类
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;       // 返回自定义的 message 字段
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
```

> ⚠️ **注意**：构造函数调用 `super()`（无参），未将 message 传递给 `RuntimeException` 父类。但 `getMessage()` 被重写为返回自定义字段，因此 `e.getMessage()` 仍能获取到错误信息。但 `e.getLocalizedMessage()`、日志框架的默认输出可能取父类的 `message`（为 null）。

---

## 2. XSS 防护组件示例

### 2.1 XssFilter（Servlet Filter）

**真实实现**（`com.dp.plat.security.xss.XssFilter`）：

```java
public class XssFilter implements Filter {
    FilterConfig filterConfig = null;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String servletPath = ((HttpServletRequest) request).getServletPath();

        // 1. excludePattern 排除路径
        if (filterConfig != null) {
            String excludePattern = filterConfig.getInitParameter("excludePattern");
            if (StringUtils.isNotBlank(excludePattern) && servletPath.matches(excludePattern)) {
                chain.doFilter(request, response);
                return;
            }
        }

        // 2. 实际装配的是 XssRequestBodyHttpServletRequestWrapper（非 XssHttpServletRequestWrapper）
        HttpServletRequest httpRequest = ((HttpServletRequest) request);
        request = new XssRequestBodyHttpServletRequestWrapper(httpRequest);
        chain.doFilter(request, response);
    }
}
```

> **关键纠正**：早期文档误称 XssFilter 装配 `XssHttpServletRequestWrapper`，实际装配的是 `XssRequestBodyHttpServletRequestWrapper`。源码中可见被注释掉的 `XssHttpServletRequestWrapper` 装配代码。

### 2.2 XssHttpServletRequestWrapper（基础包装器）

**真实实现**（`com.dp.plat.security.xss.XssHttpServletRequestWrapper`）：

```java
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    @Override
    public String getHeader(String name) {
        String strHeader = super.getHeader(name);
        if (StringUtils.isEmpty(strHeader)) {
            return strHeader;
        }
        return JsoupUtil.clean(super.getHeader(name));
    }

    @Override
    public String getParameter(String name) {
        String strParameter = super.getParameter(name);
        if (StringUtils.isEmpty(strParameter)) {
            return strParameter;
        }
        return JsoupUtil.clean(super.getParameter(name));
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return values;
        }
        int length = values.length;
        String[] escapseValues = new String[length];
        for (int i = 0; i < length; i++) {
            escapseValues[i] = JsoupUtil.clean(values[i]).trim();  // 注意：会 trim()
            if (!StringUtils.equals(escapseValues[i], values[i])) {
                logger.debug("xss字符串过滤前：" + values[i] + "\r\n" + "过滤后：" + escapseValues[i]);
            }
        }
        return escapseValues;
    }
}
```

### 2.3 XssRequestBodyHttpServletRequestWrapper（POST Body 缓存包装器）

**核心机制**（`com.dp.plat.security.xss.XssRequestBodyHttpServletRequestWrapper`）：

```java
public XssRequestBodyHttpServletRequestWrapper(HttpServletRequest request) {
    super(request);
    orginRequest = request;
    isMultipart = multipartResolver.isMultipart(request);
    String method = request.getMethod().toUpperCase();
    if ("POST".equals(method)) {
        // 1. 读取并缓存 requestBody 字节
        String requestBodyStr = getRequestBody(request);
        if (null != requestBodyStr && !"".equals(requestBodyStr)) {
            // 2. 对 body 做 HTML 转义
            String temp = escapeHtml(requestBodyStr);
            // 3. 仅当转义后仍是合法 JSON 时才替换 body
            if (JSONValidator.from(temp).validate()) {
                requestBody = temp.getBytes(getCharset());
            }
        }
        // 4. 解析参数（含 form-urlencoded 和 query string）
        processParameters(requestBody, 0, getContentLength(), getCharset());
    }
    // 5. 缓存查询参数
    String queryParams = request.getQueryString();
    if (null != queryParams && "" != queryParams) {
        byte[] bytes = queryParams.getBytes(getCharset());
        processParameters(bytes, 0, bytes.length, getCharset());
    }
}
```

> **关键点**：
> - 仅当 `escapeHtml` 后的结果仍是合法 JSON 时，才用转义后的 body 替换原 body，避免破坏非 JSON 的 form 数据。
> - `password` 参数会被豁免转义（详见源码 `processParameters` 中的特判逻辑）。
> - 该类有 3 个版本（XssRequestBodyHttpServletRequestWrapper、XssPostHttpServletRequestWrapper 等），均基于 POST Body 缓存机制。

### 2.4 XssStrutsInterceptor（Struts2 拦截器）

**真实实现**（`com.dp.plat.security.xss.struts.XssStrutsInterceptor`）：

```java
public class XssStrutsInterceptor extends AbstractInterceptor {

    private String excludes;          // 排除 URL 列表（逗号分隔）
    private Set<String> excludeUrls;
    private String cleans;            // 清理 URL 列表
    private Set<String> cleanUrls;
    private String encodes;           // 转义 URL 列表
    private Set<String> encodeUrls;
    private String enable;            // 是否启用
    private boolean enabled;

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ActionContext actionContext = invocation.getInvocationContext();
        HttpServletRequest servletRequest = (HttpServletRequest) actionContext
                .get("com.opensymphony.xwork2.dispatcher.HttpServletRequest");
        String servletPath = servletRequest.getServletPath();

        // 1. excludeUrls 直接放行
        if (isExcludeUrl(servletPath)) {
            return invocation.invoke();
        }

        // 2. 判断是否走 clean 模式（保留安全 HTML）
        boolean isClean = isMatch(servletPath, this.cleanUrls);

        // 3. 遍历 Struts2 参数 Map（Struts 2.3 版本）
        Map<String, Object> httpParameters = actionContext.getParameters();
        for (Entry<String, Object> entry : httpParameters.entrySet()) {
            Object parameter = entry.getValue();
            if (parameter instanceof String[]) {
                String[] strArr = (String[]) parameter;
                for (int i = 0; i < strArr.length; i++) {
                    String param = strArr[i];
                    // clean 模式用 JsoupUtil.clean，否则用 xssEncode
                    strArr[i] = isClean ? JsoupUtil.clean(param) : JsoupUtil.xssEncode(param);
                }
                entry.setValue(strArr);
            } else if (parameter instanceof String) {
                String param = parameter.toString();
                // clean 模式使用 FormSafelist（含 input/select/label）
                param = isClean ? JsoupUtil.clean(param, JsoupUtil.getFormSafelist())
                                : JsoupUtil.xssEncode(param);
                entry.setValue(param);
            }
        }
        return invocation.invoke();
    }
}
```

**struts.xml 配置示例**：

```xml
<interceptor name="XssStrutsInterceptor"
    class="com.dp.plat.security.xss.struts.XssStrutsInterceptor">
    <param name="enable">true</param>
    <param name="excludeUrls">/base/executeSql.*</param>
    <param name="cleanUrls">/module/prob_*,/probAudit.*,/probAjax_*.*</param>
    <param name="encodeUrls">/*</param>
</interceptor>
```

> **关键点**：
> - 三级 URL 策略：`excludeUrls`（放行） > `cleanUrls`（Jsoup 清理） > `encodeUrls`（HTML 编码）。
> - `cleanUrls` 模式对 `String[]` 用默认 Safelist，对 `String` 用 `getFormSafelist()`（含表单元素 input/select/label）。
> - `encodeUrls` 模式调用 `JsoupUtil.xssEncode()`，仅转义 `<`、`>` 和 `%3c`/`%3e` 等 URL 编码形式。

---

## 3. 数据加密组件示例

### 3.1 ASEUtil（AES 加密工具）

**真实方法签名**（`com.dp.plat.security.util.ASEUtil`）：

```java
public class ASEUtil {
    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String DEFAULT_SECRET_PASSWORD = "DP_SECRET";

    // 双参加密方法（content + password）
    public static String encrypt(String content, String password);

    // 双参解密方法
    public static String decrypt(String content, String password);

    // 密钥派生：KeyGenerator + SHA1PRNG
    private static SecretKeySpec getSecretKey(final String password);
}
```

**使用示例**：

```java
// 1. 使用默认密码加密
String encrypted = ASEUtil.encrypt("hello world", null);
// 等价于 ASEUtil.encrypt("hello world", "DP_SECRET")

// 2. 使用自定义密码加密
String encrypted = ASEUtil.encrypt("敏感数据", "my-secret-key");

// 3. 解密
String decrypted = ASEUtil.decrypt(encrypted, "my-secret-key");

// 4. content 为 null 时直接返回 null（不抛异常）
String result = ASEUtil.encrypt(null, "pwd");  // return null
```

**密钥派生流程**：

```java
private static SecretKeySpec getSecretKey(final String password) {
    final String secretKeyPassword = password != null ? password : DEFAULT_SECRET_PASSWORD;
    KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
    // 使用 SHA1PRNG 派生密钥（跨平台一致）
    SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
    secureRandom.setSeed(secretKeyPassword.getBytes());
    kg.init(128, secureRandom);  // AES-128
    SecretKey secretKey = kg.generateKey();
    return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
}
```

> **关键纠正**：
> - 早期文档虚构了 `encrypt(String data)` 单参方法，**实际不存在**。真实方法是 `encrypt(String content, String password)` 双参。
> - 早期文档虚构了使用 `SecretKeySpec(KEY.getBytes(), "AES")` 直接构造密钥，**实际使用** `KeyGenerator` + `SHA1PRNG` 派生密钥。
> - 使用 `SHA1PRNG` 而非直接 `getBytes()` 是为了跨平台密钥一致性（Windows/Linux 的 `SecureRandom` 默认实现不同）。
> - 加密结果通过 `Base64Utils.encodeToString()` 编码，解密通过 `Base64Utils.decodeFromString()` 解码。

---

## 4. SQL 解析组件示例

### 4.1 SQLParser（基于 Druid SQLUtils）

**真实方法签名**（`com.dp.plat.security.util.SQLParser`）：

```java
public class SQLParser {
    // 基础解析方法
    public static List<SQLStatement> parseStatements(String sql, DbType dbType);
    public static SQLStatement parseSingleStatement(String sql, DbType dbType);
    public static List<SchemaStatVisitor> parseStatementsVisitors(String sql, DbType dbType);
    public static SchemaStatVisitor parseStatementsVisitor(String sql, DbType dbType);

    // 表名提取
    public static Set<String> parseTables(String sql, DbType dbType);
    public static Set<String> parseTables(String sql);  // dbType=null

    // 表名正则匹配
    public static boolean matcherAll(String sql, String regex);
    public static boolean matcherAll(String sql, String regex, DbType dbType);
    public static SqlParserResult matcherSqlTables(String sql, String regex);
    public static boolean unMatcherAll(String sql, String regex);
    public static SqlParserResult unMatcherSqlTables(String sql, String regex);

    // 数据库类型识别
    public static DbType getCurrentDbType(DataSource dataSource);

    // SQL 变量解析与填充
    public static Map<String, Map<String, Object>> parseSqlParams(String sql);
    public static String fillSqlParams(String sql, Map<String, Object> values);

    // 工具方法
    public static String quoteSplit(String split);
    public static Object parseObjectValue(Map<String, Object> param, Map<String, Object> values);
    public static String toJSONString(Object obj);
}
```

**使用示例**：

```java
// 1. 提取 SQL 中的所有表名
String sql = "SELECT * FROM pm_project p LEFT JOIN pm_project_member m ON p.projectId = m.projectId";
Set<String> tables = SQLParser.parseTables(sql, DbType.mysql);
// 结果：[pm_project, pm_project_member]

// 2. 校验 SQL 是否只访问允许的表（白名单）
String allowedTablePattern = "pm_project.*|fnd_user.*";
boolean isAllowed = SQLParser.matcherAll(sql, allowedTablePattern, DbType.mysql);
// true：所有表名都匹配白名单正则

// 3. 校验 SQL 是否不包含禁止的表（黑名单）
String forbiddenPattern = "fnd_user_info|fnd_role";
boolean isSafe = SQLParser.unMatcherAll(sql, forbiddenPattern, DbType.mysql);
// true：没有任何表名匹配黑名单

// 4. 获取不匹配的表名（用于错误提示）
SqlParserResult result = SQLParser.matcherSqlTables(sql, "pm_.*");
if (!result.isValid()) {
    Set<String> unmatched = result.getMatchTables();
    // unmatched 包含不匹配 pm_.* 的表名
}

// 5. 自动识别数据库类型
DbType dbType = SQLParser.getCurrentDbType(dataSource);

// 6. 解析 SQL 中的变量占位符
String sqlWithVars = "SELECT * FROM ${tableName} WHERE id = #{id} AND code = $code$";
Map<String, Map<String, Object>> params = SQLParser.parseSqlParams(sqlWithVars);
// 返回：{"${tableName}": {...}, "#{id}": {...}, "$code$": {...}}

// 7. 填充 SQL 变量
Map<String, Object> values = new HashMap<>();
values.put("tableName", "pm_project");
values.put("id", "12345");
values.put("code", "P001");
String filledSql = SQLParser.fillSqlParams(sqlWithVars, values);
// 结果：SELECT * FROM pm_project WHERE id = '12345' AND code = P001
```

**支持的变量占位符格式**：

| 占位符 | before | after | quote | 说明 |
|--------|--------|-------|-------|------|
| `${|}` | `${` | `}` | false（不加引号） | 字符串直接拼接 |
| `#{|}` | `#{` | `}` | `'`（加单引号） | 字符串参数化 |
| `$|$` | `$` | `$` | false（不加引号） | iBatis 风格 |
| `#|#` | `#` | `#` | `'`（加单引号） | iBatis 风格 |

> **关键纠正**：
> - 早期文档虚构了 `SQLParser.isValid(String sql)` 方法使用 JSQLParser（`CCJSqlParserUtil.parse`），**实际不存在**。
> - 真实实现基于 **Druid SQLUtils**（`com.alibaba.druid.sql.SQLUtils`），而非 JSQLParser。
> - 真实方法包括 `parseTables()`、`matcherAll()`、`unMatcherAll()`、`fillSqlParams()` 等，无 `isValid()` 方法。

### 4.2 SqlParserResult（解析结果）

```java
public static class SqlParserResult {
    private boolean valid;          // 是否全部匹配/全部不匹配
    private Set<String> matchTables; // 不匹配/匹配的表名集合

    public SqlParserResult() {}
    public SqlParserResult(boolean valid, Set<String> matchTables);

    public boolean isValid();
    public Set<String> getMatchTables();
    // getter/setter 省略
}
```

---

## 5. JsoupUtil 清理工具示例

**真实方法签名**（`com.dp.plat.security.util.JsoupUtil`）：

```java
public class JsoupUtil {
    // 表单白名单（含 input/select/label）
    public static Safelist getFormSafelist();

    // HTML 转义（& → ＆，再 HtmlUtils.htmlEscape）
    public static String escape(String html);
    public static String unescape(String html);

    // XSS 编码（仅转义 < > 和 %3c/%3e）
    public static String xssEncode(String s);
    public static void processUrlEncoder(StringBuilder sb, String s, int index);

    // Jsoup 清理（使用 Safelist.relaxed）
    public static String clean(String html);
    public static String clean(String html, String baseUri);
    public static String clean(String html, Safelist safelist);
    public static String clean(String html, String baseUri, Safelist safelist);
}
```

**使用示例**：

```java
// 1. 默认清理（使用 Safelist.relaxed + 基础属性）
String clean = JsoupUtil.clean("<script>alert(1)</script><p>hello</p>");
// 结果：<p>hello</p>（script 被移除）

// 2. 表单清理（保留 input/select/label）
String formHtml = "<input type=\"text\" name=\"user\"><script>x</script>";
String result = JsoupUtil.clean(formHtml, JsoupUtil.getFormSafelist());
// 结果：<input type="text" name="user" />（script 被移除）

// 3. XSS 编码（仅转义 < >）
String encoded = JsoupUtil.xssEncode("<script>alert(1)</script>");
// 结果：&lt;script&gt;alert(1)&lt;/script&gt;

// 4. HTML 转义与反转义
String escaped = JsoupUtil.escape("<div>test & data</div>");
String original = JsoupUtil.unescape(escaped);
```

> **关键纠正**：早期文档虚构使用 `Whitelist`，**实际使用 `Safelist`**（Jsoup 1.14+ 的更名）。

---

## 6. CaptchaUtil 验证码示例

**真实方法签名**（`com.dp.plat.security.util.CaptchaUtil`）：

```java
public class CaptchaUtil {
    private static final String RANDOM_STRS = "123456789ABCDEFGHIJKLMNPQRSTUVWXYZ";
    private static final String FONT_NAME = "Fixedsys";
    private static final int FONT_SIZE = 20;

    private int width = 80;       // 图片宽
    private int height = 30;      // 图片高
    private int lineNum = 50;     // 干扰线数量
    private int strNum = 4;       // 字符数量

    public String genRandomCode();
    public BufferedImage genRandomCodeImage(String randomCode);
    public BufferedImage genRandomCodeImage(StringBuffer randomCode);
    public String getRandomString(int num);

    public static void responseCaptcha(HttpServletRequest req, HttpServletResponse resp, String KEY_CAPTCHA);
}
```

**使用示例**：

```java
// 1. 在 Controller 中输出验证码图片
@GetMapping("/captcha")
public void captcha(HttpServletRequest req, HttpServletResponse resp) {
    CaptchaUtil.responseCaptcha(req, resp, "CAPTCHA_KEY");
    // 验证码已写入 Session（key="CAPTCHA_KEY"）并输出 PNG 图片
}

// 2. 校验用户输入
String inputCode = req.getParameter("captcha");
String sessionCode = (String) req.getSession().getAttribute("CAPTCHA_KEY");
if (!inputCode.equalsIgnoreCase(sessionCode)) {
    // 验证码错误
}

// 3. 单独生成验证码图片（不通过 HTTP）
CaptchaUtil tool = new CaptchaUtil();
StringBuffer code = new StringBuffer();
BufferedImage image = tool.genRandomCodeImage(code);
// code.toString() 即为验证码字符串
// image 可用于 ImageIO.write() 输出
```

> **关键点**：
> - 字符集为 `123456789ABCDEFGHIJKLMNPQRSTUVWXYZ`（**不含 O 和 0**，避免混淆）。
> - 使用 `SecureRandom` 而非 `Random`，保证随机性。
> - `responseCaptcha()` 是静态方法，会自动设置 `image/png` 响应类型、no-cache 头，并将验证码写入 Session。

---

## 7. HttpContext 请求上下文示例

**真实方法签名**（`com.dp.plat.security.context.HttpContext`）：

```java
public class HttpContext {
    public static HttpServletRequest getCurrentRequest();
    public static HttpSession getCurrentSession();

    public static boolean isAjax();    // accept: application/json 或 X-Requested-With: XMLHttpRequest
    public static boolean isJSON();    // accept: application/json 或 servletPath 以 .json 结尾
    public static boolean isHTML();    // accept: text/plain 或 servletPath 以 .html/.htm 结尾或无扩展名
    public static boolean isExcel();   // servletPath 以 .xlsx/.xls 结尾

    public static String baseUri();    // scheme://serverName:port/contextPath
    public static String getCurrentIp(HttpServletRequest request);
    public static String getCurrentIp();
}
```

**使用示例**：

```java
// 1. 获取当前请求的 HttpServletRequest（无需方法参数透传）
HttpServletRequest request = HttpContext.getCurrentRequest();

// 2. 判断请求类型
if (HttpContext.isAjax()) {
    // 返回 JSON
} else if (HttpContext.isHTML()) {
    // 返回视图
} else if (HttpContext.isExcel()) {
    // 返回 Excel
}

// 3. 获取客户端真实 IP（穿透代理）
String ip = HttpContext.getCurrentIp();
// 依次尝试：x-forwarded-for → Proxy-Client-IP → WL-Proxy-Client-IP
//          → HTTP_CLIENT_IP → HTTP_X_FORWARDED_FOR → getRemoteAddr()

// 4. 构造基础 URI（用于 JsoupUtil.clean 的 baseUri 参数）
String baseUri = HttpContext.baseUri();
String cleanHtml = JsoupUtil.clean(html, baseUri);
```

> **关键点**：
> - 基于 Spring 的 `RequestContextHolder.currentRequestAttributes()` 获取当前请求，无需在方法签名中透传 `HttpServletRequest`。
> - `getCurrentIp()` 穿透多层代理头，但需注意 `x-forwarded-for` 可被伪造。
> - `baseUri()` 用于 Jsoup 清理时的相对路径解析。

---

## 8. PasswordInterceptor 密码拦截器示例

**真实实现**（`com.dp.plat.security.interceptor.PasswordInterceptor`）：

```java
public abstract class PasswordInterceptor implements AsyncHandlerInterceptor {

    private String redirect;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (isNeedRedirect(request)) {
            response.sendRedirect(request.getContextPath() + redirect);
            return false;
        }
        return true;
    }

    // 抽象方法：由子类实现是否需要重定向的逻辑
    public abstract boolean isNeedRedirect(HttpServletRequest request);

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }
}
```

**子类实现示例**（子类位于 `core` 模块）：

```java
// core 模块中的具体实现（示例，实际子类在 com.dp.plat.core.interceptor 包下）
@Component
public class ConcretePasswordInterceptor extends PasswordInterceptor {

    @Override
    public boolean isNeedRedirect(HttpServletRequest request) {
        // 1. 未登录用户不拦截
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return false;
        }

        // 2. CAS 模式不拦截
        String isCas = SystemConfig.systemVariables.getOrDefault("sys.cas", "0");
        if ("1".equals(isCas)) {
            return false;
        }

        // 3. 检查 Session 中的 needChangePwd 标志
        HttpSession session = request.getSession();
        Object needChangePwd = session.getAttribute("needChangePwd");
        if (Boolean.TRUE.equals(needChangePwd)) {
            String servletPath = request.getServletPath();
            String redirect = getRedirect();
            // 排除密码修改页面本身，避免死循环
            if (redirect == null || redirect.contains(servletPath)) {
                return false;
            }
            return true;
        }
        return false;
    }
}
```

**spring-mvc.xml 配置示例**：

```xml
<mvc:interceptor>
    <mvc:mapping path="/**"/>
    <mvc:exclude-mapping path="/password.*"/>
    <mvc:exclude-mapping path="/modifyPassword.*"/>
    <bean id="pwdInterceptor" class="com.dp.plat.core.interceptor.PasswordInterceptor">
        <property name="redirect" value="/password.html?needChangePwd=true" />
    </bean>
</mvc:interceptor>
```

> **关键纠正**：
> - 早期文档虚构 `PasswordInterceptor` 是具体类，包含 `isPasswordExpired()` 私有方法，**实际是抽象类**。
> - 真实实现采用**模板方法模式**：父类定义 `preHandle` 流程（检查 → 重定向），子类实现 `isNeedRedirect()` 决定是否需要重定向。
> - 具体子类位于 `core` 模块（`com.dp.plat.core.interceptor.PasswordInterceptor`），不在 `security` 模块内。

---

## 9. 完整集成示例

### 9.1 PMS-struts 集成（web.xml）

```xml
<!-- CSRF 防护（启用） -->
<filter>
    <filter-name>CsrfFilter</filter-name>
    <filter-class>com.dp.plat.security.csrf.CsrfFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>CsrfFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>

<!-- XSS 防护（已注释，未启用） -->
<!-- <filter>
    <filter-name>XssFilter</filter-name>
    <filter-class>com.dp.plat.security.xss.XssFilter</filter-class>
</filter> -->
```

### 9.2 PMS-springmvc 集成（spring-mvc.xml）

```xml
<mvc:interceptors>
    <!-- CSRF 拦截器（排除登录接口） -->
    <mvc:interceptor>
        <mvc:mapping path="/**"/>
        <mvc:exclude-mapping path="/sys/login.json"/>
        <bean id="csrfInterceptor" class="com.dp.plat.security.csrf.CsrfInterceptor"/>
    </mvc:interceptor>

    <!-- 密码拦截器（core 子类，排除密码修改接口） -->
    <mvc:interceptor>
        <mvc:mapping path="/**"/>
        <mvc:exclude-mapping path="/password.*"/>
        <mvc:exclude-mapping path="/modifyPassword.*"/>
        <bean id="pwdInterceptor" class="com.dp.plat.core.interceptor.PasswordInterceptor">
            <property name="redirect" value="/password.html?needChangePwd=true" />
        </bean>
    </mvc:interceptor>
</mvc:interceptors>
```

### 9.3 前端表单集成 CSRF Token

```jsp
<%
    // 在 JSP 页面顶部获取 Token
    String csrfToken = com.dp.plat.security.csrf.CSRFTokenManager
        .getTokenForSession(request.getSession());
    String csrfTokenName = com.dp.plat.security.csrf.CSRFTokenManager.getTokenName();
%>

<form action="/sys/save.action" method="post">
    <!-- 隐藏字段携带 CSRF Token -->
    <input type="hidden" name="<%=csrfTokenName%>" value="<%=csrfToken%>" />
    <input type="text" name="username" />
    <button type="submit">提交</button>
</form>

<script>
    // AJAX 请求携带 CSRF Token（从 Cookie 或 Header 读取）
    $.ajaxSetup({
        beforeSend: function(xhr) {
            var token = getCookie("__RequestVerificationToken");
            if (token) {
                xhr.setRequestHeader("__RequestVerificationToken", token);
            }
        }
    });
</script>
```

---

## 10. 关键设计纠正对照表

| 组件 | 早期虚构内容 | 真实实现 |
|------|------------|---------|
| `CSRFTokenManager` | `generateToken(HttpSession)` 写入 Session | `generateToken()` 无参仅返回 UUID；`getTokenForSession(HttpSession)` 写入 Session |
| `CSRFTokenManager` | `validateToken(HttpSession, String)` | 不存在该方法；校验逻辑在 `CsrfFilter.isValid()` / `CsrfInterceptor.preHandle()` 中 |
| `ASEUtil` | `encrypt(String data)` 单参 | `encrypt(String content, String password)` 双参 |
| `ASEUtil` | `SecretKeySpec(KEY.getBytes(), "AES")` 直接构造密钥 | `KeyGenerator` + `SHA1PRNG` 派生密钥 |
| `SQLParser` | 使用 JSQLParser（`CCJSqlParserUtil.parse`） | 使用 Druid SQLUtils（`SQLUtils.parseStatements`） |
| `SQLParser` | `isValid(String sql)` 方法 | 不存在；真实方法为 `parseTables()`、`matcherAll()`、`fillSqlParams()` 等 |
| `PasswordInterceptor` | 具体类，含 `isPasswordExpired()` 私有方法 | **抽象类**，抽象方法 `isNeedRedirect()`，子类在 core 模块 |
| `XssFilter` | 装配 `XssHttpServletRequestWrapper` | 装配 `XssRequestBodyHttpServletRequestWrapper` |
| `JsoupUtil` | 使用 `Whitelist` | 使用 `Safelist`（Jsoup 1.14+ 更名） |
| `CsrfInterceptor` | 仅排除 GET，使用 `_csrf` 参数名 | 仅校验 POST/PUT/DELETE，使用 `__RequestVerificationToken` 参数名 |
| `CsrfInterceptor` | Token 存储在 `CSRF_TOKEN` Session 属性 | 存储在 `CSRFTokenManager.class.getName() + ".tokenval"` |
