# 性能优化

## 1. XSS 过滤性能

### 1.1 POST Body 缓存机制

`XssRequestBodyHttpServletRequestWrapper` 在构造时一次性读取 InputStream 并缓存为 `byte[]`，避免多次读取导致的流关闭问题。但存在以下性能考量：

| 操作 | 性能影响 | 优化建议 |
|------|---------|---------|
| `StreamUtils.copyToByteArray()` | 大 Body 占用堆内存 | 限制请求体大小（如 10MB） |
| `escapeHtml()` 字符遍历 | O(n) 时间复杂度 | 可接受，避免正则 |
| `JSONValidator.from(temp).validate()` | FastJSON 解析开销 | 仅 POST 且 Body 非空时触发 |
| `processParameters()` 手动解析 | 模拟 Tomcat 参数解析 | 比 `request.getParameterMap()` 慢 |

### 1.2 multipart 处理性能

版本 1 的 multipart 处理使用 `ByteBuffer.allocateDirect()`（堆外内存）+ KMP 算法查找边界：

```java
ByteBuffer builder = ByteBuffer.allocateDirect(getContentLength());
// KMP 查找 Content-Disposition 头位置
int itemOffset = ByteUtils.indexOf(requestBody, currentHeader);
```

- **优点**：堆外内存减少 GC 压力
- **缺点**：DirectByteBuffer 分配/释放开销大，`getContentLength()` 可能不准确导致扩容

### 1.3 三个版本的性能差异

| 版本 | JSON 校验 | multipart | 性能特征 |
|------|-----------|-----------|---------|
| 版本 1 | `JSONValidator`（轻量） | DirectByteBuffer + KMP | 内存效率高，但 DirectByteBuffer 开销 |
| 版本 2 | `JSON.parseObject`（完整解析） | 分离处理 | 解析开销大，但逻辑清晰 |
| 版本 3 | `JSON.parseObject` | 简化（仅参数） | 最快，但不重建 Body |

> 当前 `XssFilter` 装配版本 1。

---

## 2. JsoupUtil 缓存策略

### 2.1 Safelist 创建开销

```java
// 每次调用都创建新的 Safelist
public static String clean(String html, String baseUri) {
    return clean(html, baseUri,
        Safelist.relaxed()
            .addAttributes(":all", "style", "title", "width", "height", "align", "valign")
            .addAttributes("table", "cellpadding", "cellspacing", "rule", "border")
            .preserveRelativeLinks(true));
}
```

**问题**：`Safelist.relaxed()` 及链式调用每次都创建新对象，未缓存。

**优化建议**：

```java
// 建议改为静态字段缓存
private static final Safelist DEFAULT_SAFELIST = Safelist.relaxed()
    .addAttributes(":all", "style", "title", "width", "height", "align", "valign")
    .addAttributes("table", "cellpadding", "cellspacing", "rule", "border")
    .preserveRelativeLinks(true);

private static final Safelist FORM_SAFELIST = getFormSafelist();
```

### 2.2 getFormSafelist 调用

`XssStrutsInterceptor` 在 cleanUrls 模式下对 String 类型参数调用 `JsoupUtil.clean(param, JsoupUtil.getFormSafelist())`，每次都创建新 Safelist。

---

## 3. 正则预编译

### 3.1 已预编译的正则

| 类 | 正则字段 | 预编译状态 |
|----|---------|-----------|
| `SQLParser` | `parserSqlTablePattern` | ✅ `Pattern.compile()` 静态字段 |
| `XssStrutsInterceptor` | URL 匹配 | ❌ 每次调用 `Pattern.compile("^" + pattern)` |

### 3.2 未预编译的正则

```java
// XssStrutsInterceptor.isMatch() - 每次请求都重新编译
for (String pattern : paths) {
    Pattern p = Pattern.compile("^" + pattern);  // 未缓存
    Matcher m = p.matcher(url);
    if (m.find()) return true;
}
```

**优化建议**：在 `init()` 时预编译所有 pattern 为 `List<Pattern>`。

### 3.3 SQLParser.matcherAll

```java
public static boolean matcherAll(Set<String> tables, String regex) {
    Pattern compile = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  // 每次编译
    // ...
}
```

**优化建议**：对常用 regex 做缓存（如 `ConcurrentHashMap<String, Pattern>`）。

---

## 4. CSRF Token 同步开销

```java
public static String getTokenForSession(HttpSession session) {
    synchronized (session) {  // 对 Session 加锁
        // ...
    }
}
```

- **影响**：同一 Session 的并发请求会串行化
- **评估**：Token 仅在首次访问时生成，后续直接读取，锁竞争时间极短
- **无需优化**：synchronized 块内仅一次属性读写

---

## 5. SQLParser 性能

### 5.1 Druid SQLUtils 解析开销

```java
public static List<SQLStatement> parseStatements(String sql, DbType dbType) {
    String result = SQLUtils.format(sql, dbType);  // 格式化
    return SQLUtils.parseStatements(result, dbType);  // 解析
}
```

- `SQLUtils.format()` 会重新格式化 SQL，有额外开销
- `parseStatements()` 构建 AST，复杂 SQL 解析较慢

**优化建议**：对相同 SQL 做结果缓存。

### 5.2 Stream 操作

```java
List<String> names = keySet.stream().map(Name::getName).collect(Collectors.toList());
```

表名提取使用 Stream，对小数据集（表数量通常 < 20）开销可忽略。

---

## 6. ByteUtils KMP 算法

```java
public static int indexOf(byte[] text, byte[] pattern) {
    int[] lps = computeLPSArray(pattern);  // 每次都重新计算 LPS
    // ...
}
```

**优化建议**：对相同 pattern 缓存 LPS 数组。当前用于 multipart 边界查找，pattern（Content-Disposition 头）每次不同，缓存意义不大。

---

## 7. DirectByteBuffer 扩容

```java
public static ByteBuffer append(ByteBuffer builder, byte[] bytes) {
    if (builder.remaining() < bytes.length) {
        builder = expandDirectByteBuffer(builder, bytes.length - builder.remaining());
    }
    builder.put(bytes);
    return builder;
}
```

- 扩容策略：`Math.max(capacity * 2, capacity + additionalCapacity)`
- **问题**：扩容时需复制全部数据，且依赖 GC 回收旧 DirectByteBuffer

**优化建议**：初始容量使用更准确的估算（如 `requestBody.length` 而非 `getContentLength()`）。

---

## 8. 性能优化清单

| 优先级 | 优化项 | 预期收益 |
|--------|--------|---------|
| 高 | JsoupUtil Safelist 静态缓存 | 减少 GC，提升吞吐 |
| 高 | XssStrutsInterceptor pattern 预编译 | 减少每请求编译开销 |
| 中 | SQLParser 结果缓存 | 减少 SQL 重复解析 |
| 中 | 限制 POST Body 大小 | 防止 OOM |
| 低 | ByteUtils LPS 缓存 | 边际收益小 |
| 低 | CSRF Token 锁优化 | 当前已足够快 |

---

## 9. 相关文档

| 文档 | 说明 |
|------|------|
| [security-practices.md](security-practices.md) | 安全实践 |
| [troubleshooting.md](troubleshooting.md) | 故障排查 |
