# XSS 防护组件

## 1. 组件清单

| 类 | 文件 | 类型 |
|----|------|------|
| `XssFilter` | `xss/XssFilter.java` | Servlet Filter |
| `XssHttpServletRequestWrapper` | `xss/XssHttpServletRequestWrapper.java` | 简单 Request Wrapper |
| `XssRequestBodyHttpServletRequestWrapper` | `xss/XssRequestBodyHttpServletRequestWrapper.java` | POST Body 缓存 Wrapper（版本 1） |
| `XssRequestBodyHttpServletRequestWrapper2` | `xss/XssRequestBodyHttpServletRequestWrapper2.java` | 版本 2 |
| `XssRequestBodyHttpServletRequestWrapper3` | `xss/XssRequestBodyHttpServletRequestWrapper3.java` | 版本 3 |
| `XssStrutsInterceptor` | `xss/struts/XssStrutsInterceptor.java` | Struts2 Interceptor |
| `MDispatcher` | `xss/struts/MDispatcher.java` | Dispatcher 替换 |
| `MStrutsRequestWrapper` | `xss/struts/MStrutsRequestWrapper.java` | StrutsRequestWrapper 替换 |
| `MMultiPartRequestWrapper` | `xss/struts/MMultiPartRequestWrapper.java` | MultiPartRequestWrapper 替换 |
| `MStrutsPrepareAndExecuteFilter` | `xss/struts/MStrutsPrepareAndExecuteFilter.java` | StrutsPrepareAndExecuteFilter 替换 |

---

## 2. XssFilter

### 2.1 类定义

```java
public class XssFilter implements Filter {
    FilterConfig filterConfig = null;
    public void init(FilterConfig filterConfig) { this.filterConfig = filterConfig; }
    public void destroy() { this.filterConfig = null; }
    public void doFilter(ServletRequest, ServletResponse, FilterChain) { ... }
}
```

### 2.2 doFilter 实现

```java
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
    String servletPath = ((HttpServletRequest) request).getServletPath();
    // excludePattern 排除
    if (filterConfig != null) {
        String excludePattern = filterConfig.getInitParameter("excludePattern");
        if (StringUtils.isNotBlank(excludePattern) && servletPath.matches(excludePattern)) {
            chain.doFilter(request, response);
            return;
        }
    }
    // 装配 XssRequestBodyHttpServletRequestWrapper
    HttpServletRequest httpRequest = ((HttpServletRequest) request);
    request = new XssRequestBodyHttpServletRequestWrapper(httpRequest);
    chain.doFilter(request, response);
}
```

> ⚠️ 装配的是 `XssRequestBodyHttpServletRequestWrapper`（版本 1），`XssHttpServletRequestWrapper` 的使用已被注释。

---

## 3. XssHttpServletRequestWrapper（简单包装器）

> 当前未被 XssFilter 使用，保留作为轻量级包装器。

### 3.1 方法

| 方法 | 处理 |
|------|------|
| `getHeader(name)` | `JsoupUtil.clean()` |
| `getParameter(name)` | `JsoupUtil.clean()` |
| `getParameterValues(name)` | `JsoupUtil.clean()` + `trim()`，记录 debug 日志 |

### 3.2 日志

```java
private static final Log logger = LogFactory.getLog(XssHttpServletRequestWrapper.class);
// 过滤前后不一致时记录
logger.debug("xss字符串过滤前：" + values[i] + "\r\n" + "过滤后：" + escapseValues[i]);
```

---

## 4. XssRequestBodyHttpServletRequestWrapper（版本 1）

### 4.1 核心字段

| 字段 | 类型 | 说明 |
|------|------|------|
| `multipartResolver` | `CommonsMultipartResolver` | multipart 解析器 |
| `orginRequest` | `HttpServletRequest` | 原始请求 |
| `isMultipart` | `boolean` | 是否 multipart |
| `multipartRequest` | `MultipartHttpServletRequest` | 解析后的 multipart 请求 |
| `requestBody` | `byte[]` | 缓存的 POST Body |
| `charSet` | `Charset` | 字符集 |
| `paramHashValues` | `Map<String, ArrayList<String>>` | 手动解析的参数 |
| `parameterMap` | `Map<String, String[]>` | 懒加载的参数 Map |

### 4.2 构造函数流程

```java
public XssRequestBodyHttpServletRequestWrapper(HttpServletRequest request) {
    super(request);
    orginRequest = request;
    isMultipart = multipartResolver.isMultipart(request);
    String method = request.getMethod().toUpperCase();
    if ("POST".equals(method)) {
        // 1. 读取并缓存 Body
        requestBodyStr = getRequestBody(request);  // StreamUtils.copyToByteArray
        // 2. escapeHtml 转义
        String temp = escapeHtml(requestBodyStr);
        // 3. JSON 校验（JSONValidator）
        if (JSONValidator.from(temp).validate()) {
            requestBody = temp.getBytes(getCharset());
        }
        // 4. 解析参数
        processParameters(requestBody, 0, getContentLength(), getCharset());
    }
    // 5. 解析查询参数
    String queryParams = request.getQueryString();
    if (null != queryParams && "" != queryParams) {
        byte[] bytes = queryParams.getBytes(getCharset());
        processParameters(bytes, 0, bytes.length, getCharset());
    }
}
```

### 4.3 escapeHtml 方法

```java
public static String escapeHtml(String s) {
    if (s == null || s.isEmpty()) return "";
    StringBuilder sb = new StringBuilder("");
    for (int i = 0; i < s.length(); i++) {
        char c = s.charAt(i);
        switch (c) {
            case '>': sb.append("&gt;"); break;
            case '<': sb.append("&lt;"); break;
            case '&': sb.append('＆'); break;  // 全角 &
            default: sb.append(c);
        }
    }
    return sb.toString();
}
```

### 4.4 password 字段豁免

```java
// getParameter()
if ("password".equals(parameter)) {
    return value;  // 不转义
}
return escapeHtml(value);

// getParameterValues()
if ("password".equals(parameter)) {
    return values.toArray(new String[values.size()]);  // 不转义
}
```

### 4.5 multipart 处理

```java
// processParameters() isMultipart 分支
multipartResolver.setDefaultEncoding(DEFAULT_CHARSET);
multipartRequest = multipartResolver.resolveMultipart(this);
Map<String, String[]> parameters = multipartRequest.getParameterMap();
for (Entry<String, String[]> entry : parameters.entrySet()) {
    paramHashValues.put(entry.getKey(), new ArrayList<>(Arrays.asList(entry.getValue())));
}
// 手动解析 FileItem，对 formField 进行 escapeHtml
List<FileItem> parseRequest = ((ServletFileUpload) multipartResolver.getFileUpload()).parseRequest(this);
ByteBuffer builder = ByteBuffer.allocateDirect(getContentLength());
for (int i = 0; i < parseRequest.size(); i++) {
    FileItem currentItem = parseRequest.get(i);
    String currentHeader = currentItem.getHeaders().getHeader(ServletFileUpload.CONTENT_DISPOSITION);
    int itemOffset = ByteUtils.indexOf(requestBody, currentHeader);
    // ... 计算偏移量
    byte[] itemContent = Arrays.copyOfRange(requestBody, itemOffset, nextItemOffset);
    if (currentItem.isFormField()) {
        itemContent = escapeHtml(new String(itemContent, getCharset())).getBytes();
    }
    builder = ByteUtils.append(builder, prev);
    builder = ByteUtils.append(builder, itemContent);
}
requestBody = ByteUtils.readBytes(builder);
```

### 4.6 getInputStream / getReader

```java
public ServletInputStream getInputStream() throws IOException {
    if ((isMultipart && requestBody == null) || requestBody == null) {
        return super.getInputStream();
    }
    final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestBody);
    return new ServletInputStream() {
        @Override public int read() { return byteArrayInputStream.read(); }
        @Override public boolean isFinished() { return false; }
        @Override public boolean isReady() { return false; }
        @Override public void setReadListener(ReadListener readListener) {}
    };
}
```

### 4.7 getOrginRequest / getOrgRequest

```java
public HttpServletRequest getOrginRequest() { return orginRequest; }

public static HttpServletRequest getOrgRequest(HttpServletRequest req) {
    if (req instanceof XssRequestBodyHttpServletRequestWrapper) {
        return ((XssRequestBodyHttpServletRequestWrapper) req).getOrginRequest();
    }
    return req;
}
```

---

## 5. XssRequestBodyHttpServletRequestWrapper2（版本 2）

### 5.1 与版本 1 的区别

| 维度 | 版本 1 | 版本 2 |
|------|--------|--------|
| JSON 校验 | `JSONValidator.from(temp).validate()` | `JSON.parseObject(temp)` |
| multipart 解析器构造 | `new CommonsMultipartResolver(getServletContext())` | `new CommonsMultipartResolver()` |
| multipart 处理 | 在 `processParameters` 中处理 | 在构造函数中分离处理 |
| getRequestBody | `StreamUtils.copyToByteArray` → `byte[]` | `StreamUtils.copyToString` → `String` |
| isUpload 字段 | `isMultipart` | `isUpload` |
| escapeHtml 可见性 | `public static` | `private static` |

### 5.2 multipart 分离处理

```java
// 构造函数中
if (null != contentType) {
    isUpload = multipartResolver.isMultipart(request);
    if (isUpload) {
        multipartResolver.setDefaultEncoding("UTF-8");
        MultipartHttpServletRequest resolveMultipart = multipartResolver.resolveMultipart(request);
        super.setRequest(resolveMultipart);  // 替换底层请求
    }
}
```

---

## 6. XssRequestBodyHttpServletRequestWrapper3（版本 3）

### 6.1 与版本 1/2 的区别

| 维度 | 版本 3 |
|------|--------|
| JSON 校验 | `JSON.parseObject(temp)`（同版本 2） |
| multipart 解析器 | `new CommonsMultipartResolver(getServletContext())`（同版本 1） |
| multipart 处理 | 在 `processParameters` 中简化处理（仅填参数，不重建 Body） |
| getRequestBody | `StreamUtils.copyToString` → `String`（同版本 2） |
| escapeHtml | `public static`（同版本 1） |
| 异常处理 | catch 中对非 multipart 的 `requestBodyStr` 也进行 `escapeHtml` |

### 6.2 简化的 multipart 处理

```java
// processParameters() isMultipart 分支
multipartResolver.setDefaultEncoding(DEFAULT_CHARSET);
multipartRequest = multipartResolver.resolveMultipart(this);
Map<String, String[]> parameters = multipartRequest.getParameterMap();
for (Entry<String, String[]> entry : parameters.entrySet()) {
    paramHashValues.put(entry.getKey(), new ArrayList<>(Arrays.asList(entry.getValue())));
}
return;  // 不重建 requestBody
```

---

## 7. XssStrutsInterceptor

### 7.1 类定义

```java
public class XssStrutsInterceptor extends AbstractInterceptor {
    private String excludes;
    private Set<String> excludeUrls;
    private String encodes;
    private Set<String> encodeUrls;
    private String cleans;
    private Set<String> cleanUrls;
    private String enable;
    private boolean enabled;
}
```

### 7.2 init 方法

```java
public void init() {
    super.init();
    if (StringUtils.hasText(excludes)) {
        String[] urls = excludes.split(",");
        excludeUrls = new LinkedHashSet<>();
        for (String url : urls) excludeUrls.add(url);
    }
    // encodes/cleans 同理
    if (StringUtils.hasText(enable)) {
        enabled = Boolean.valueOf(enable);
    }
}
```

> 使用 `org.springframework.util.StringUtils.hasText()`

### 7.3 intercept 方法

```java
public String intercept(ActionInvocation invocation) throws Exception {
    ActionContext actionContext = invocation.getInvocationContext();
    HttpServletRequest servletRequest = (HttpServletRequest) actionContext.get(
        "com.opensymphony.xwork2.dispatcher.HttpServletRequest");
    String servletPath = servletRequest.getServletPath();
    
    if (isExcludeUrl(servletPath)) {
        return invocation.invoke();
    }
    boolean isClean = isMatch(servletPath, this.cleanUrls);
    
    // Struts 2.3 参数模型
    Map<String, Object> httpParameters = actionContext.getParameters();
    for (Entry<String, Object> entry : httpParameters.entrySet()) {
        Object parameter = entry.getValue();
        if (parameter instanceof String[]) {
            String[] strArr = (String[]) parameter;
            for (int i = 0; i < strArr.length; i++) {
                strArr[i] = isClean ? JsoupUtil.clean(strArr[i]) : JsoupUtil.xssEncode(strArr[i]);
            }
            entry.setValue(strArr);
        } else if (parameter instanceof String) {
            String param = parameter.toString();
            param = isClean ? JsoupUtil.clean(param, JsoupUtil.getFormSafelist()) : JsoupUtil.xssEncode(param);
            entry.setValue(param);
        }
    }
    return invocation.invoke();
}
```

### 7.4 isMatch 方法

```java
private boolean isMatch(String urlPath, Set<String> paths) {
    if (!enabled) {
        return true;  // 未启用时始终返回 true
    }
    if (paths == null || paths.isEmpty() || !StringUtils.hasText(urlPath)) {
        return false;
    }
    for (String pattern : paths) {
        Pattern p = Pattern.compile("^" + pattern);
        Matcher m = p.matcher(urlPath);
        if (m.find()) {
            return true;
        }
    }
    return false;
}
```

> ⚠️ `enabled` 默认 false，未配置 `enable=true` 时所有 URL 都匹配 cleanUrls。

### 7.5 Setter 方法

| Setter | 说明 |
|--------|------|
| `setExcludes(String)` | 设置原始 excludes 字符串 |
| `setExcludeUrls(String)` | 使用 `TextParseUtil.commaDelimitedStringToSet` 解析 |
| `setEncodeUrls(String)` | 同上 |
| `setCleanUrls(String)` | 同上 |
| `setEnable(String)` | 设置 enable 字符串 |
| `setEnabled(boolean)` | 直接设置 enabled 布尔值 |

---

## 8. MDispatcher

### 8.1 类定义

```java
public class MDispatcher extends Dispatcher {
    public MDispatcher(ServletContext servletContext, Map<String, String> initParams) { ... }
    @Override
    public HttpServletRequest wrapRequest(HttpServletRequest request, ServletContext servletContext) { ... }
    private String getSaveDir(ServletContext servletContext) { ... }
    @Override @Inject(StrutsConstants.STRUTS_MULTIPART_SAVEDIR)
    public void setMultipartSaveDir(String val) { ... }
    @Override @Inject(StrutsConstants.STRUTS_DEVMODE)
    public void setDevMode(String mode) { ... }
}
```

### 8.2 wrapRequest 实现

```java
public HttpServletRequest wrapRequest(HttpServletRequest request, ServletContext servletContext) {
    if (request instanceof StrutsRequestWrapper) {
        return request;  // 不重复包装
    }
    String content_type = request.getContentType();
    if (content_type != null && content_type.contains("multipart/form-data")) {
        MultiPartRequest mpr = getMultiPartRequest();
        LocaleProvider provider = getContainer().getInstance(LocaleProvider.class);
        request = new MMultiPartRequestWrapper(mpr, request, getSaveDir(servletContext), provider);
    } else {
        request = new MStrutsRequestWrapper(request, disableRequestAttributeValueStackLookup);
    }
    return request;
}
```

---

## 9. MStrutsRequestWrapper / MMultiPartRequestWrapper

### 9.1 MStrutsRequestWrapper

```java
public class MStrutsRequestWrapper extends StrutsRequestWrapper {
    public MStrutsRequestWrapper(HttpServletRequest req) { super(req); }
    public MStrutsRequestWrapper(HttpServletRequest req, boolean bool) { super(req, bool); }
    
    @Override
    public String getParameter(String name) {
        name = JsoupUtil.escape(name);
        return JsoupUtil.escape(super.getParameter(name));
    }
    
    @Override
    public String[] getParameterValues(String name) {
        name = JsoupUtil.escape(name);
        String[] values = super.getParameterValues(name);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                values[i] = JsoupUtil.escape(values[i]);
            }
        }
        return values;
    }
    
    @Override
    public Enumeration<String> getParameterNames() {
        return super.getParameterNames();  // 未处理
    }
}
```

### 9.2 MMultiPartRequestWrapper

```java
public class MMultiPartRequestWrapper extends MultiPartRequestWrapper {
    public MMultiPartRequestWrapper(MultiPartRequest multiPartRequest, HttpServletRequest request, 
                                    String saveDir, LocaleProvider provider) {
        super(multiPartRequest, request, saveDir, provider);
    }
    // getParameter/getParameterValues 同 MStrutsRequestWrapper
}
```

> 两个类的 getParameter/getParameterValues 实现完全相同，均调用 `JsoupUtil.escape()`。

---

## 10. MStrutsPrepareAndExecuteFilter

```java
public class MStrutsPrepareAndExecuteFilter extends StrutsPrepareAndExecuteFilter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException { ... }
    public Dispatcher initDispatcher(HostConfig filterConfig) { ... }
    private Dispatcher createDispatcher(HostConfig filterConfig) {
        // 收集 init-params
        Map<String, String> params = new HashMap<>();
        // ...
        return new MDispatcher(filterConfig.getServletContext(), params);  // 返回 MDispatcher
    }
}
```

> 通过覆盖 `createDispatcher` 方法，使 Struts2 使用 `MDispatcher` 而非原生 `Dispatcher`。

---

## 11. 相关文档

| 文档 | 说明 |
|------|------|
| [../01-architecture/xss-architecture.md](../01-architecture/xss-architecture.md) | XSS 架构 |
| [../05-standards/security-practices.md](../05-standards/security-practices.md) | XSS 白名单清理实践 |
| [../05-standards/troubleshooting.md](../05-standards/troubleshooting.md) | XSS 误杀排查 |
