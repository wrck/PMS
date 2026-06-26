# 安全实践

## 1. CSRF Token 管理

### 1.1 Token 生成

- **算法**：`UUID.randomUUID()`（128 位，密码学安全）
- **格式**：`xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`
- **生命周期**：绑定 HttpSession，会话级有效

### 1.2 Token 存储

- **位置**：`HttpSession` 属性 `CSRFTokenManager.class.getName() + ".tokenval"`
- **线程安全**：`synchronized(session)` 防止并发重复生成
- **懒加载**：首次访问时生成，不预创建

### 1.3 Token 传输

**客户端 → 服务端**（三通道，优先级递减）：

| 通道 | 方式 | 适用场景 |
|------|------|---------|
| 请求参数 | `__RequestVerificationToken=xxx` | 表单提交 |
| 请求头 | `__RequestVerificationToken: xxx` | AJAX 请求 |
| Cookie | `__RequestVerificationToken=xxx` | 自动携带 |

**服务端 → 客户端**：

| 通道 | 方式 | 组件 |
|------|------|------|
| Response Header | `__RequestVerificationToken: token` | CsrfFilter / CsrfInterceptor |
| Cookie | `__RequestVerificationToken=token; HttpOnly` | CsrfFilter |
| ModelAndView | `${__RequestVerificationToken}` | CsrfInterceptor.postHandle |

### 1.4 Token 校验最佳实践

- **GET 请求**：CsrfInterceptor 放行（安全方法）；CsrfFilter 校验（⚠️ 当前实现校验所有方法）
- **POST/PUT/DELETE**：必须校验 Token
- **登录接口**：豁免（Session 尚未建立 Token）
- **失败处理**：CsrfFilter forward `/404.jsp`；CsrfInterceptor 抛出 `CsrfValidateFailedException`

### 1.5 已知风险

1. **CsrfFilter 校验所有方法**：GET 请求也需要 Token，可能导致首屏加载失败
2. **Cookie 传输 Token**：虽设置 HttpOnly，但 Cookie 可被 JS 读取（非 SameSite=Strict）
3. **Token 不轮换**：整个会话期间 Token 不变，被窃取后可重放

---

## 2. XSS 白名单清理

### 2.1 双策略机制

| 策略 | 方法 | 适用场景 | 安全性 |
|------|------|---------|--------|
| HTML 编码 | `JsoupUtil.xssEncode()` | 普通表单 | 高（全量转义） |
| HTML 清理 | `JsoupUtil.clean()` | 富文本 | 中（白名单过滤） |

### 2.2 默认 Safelist

```java
Safelist.relaxed()
    .addAttributes(":all", "style", "title", "width", "height", "align", "valign")
    .addAttributes("table", "cellpadding", "cellspacing", "rule", "border")
    .preserveRelativeLinks(true)
```

**允许的标签**（Safelist.relaxed 默认）：
- 结构：`b`, `em`, `i`, `strong`, `u`
- 段落：`p`, `br`, `blockquote`
- 标题：`h1`-`h6`
- 列表：`ul`, `ol`, `li`
- 链接：`a`（含 href）
- 图片：`img`（含 src, alt）
- 表格：`table`, `thead`, `tbody`, `tr`, `th`, `td`
- 其他：`code`, `pre`, `hr`, `div`, `span`

**禁止的标签**：`script`, `iframe`, `object`, `embed`, `applet`, `form`, `input` 等

### 2.3 表单 Safelist

```java
Safelist.relaxed()
    .addTags("input", "select", "label")  // 额外允许表单标签
    .addAttributes("input", "type", "name", "placeholder", ...)
    .addAttributes("select", "type", "name", "placeholder", ...)
```

> ⚠️ 允许 `input` 标签存在风险，攻击者可构造恶意 input。仅在确实需要保留表单 HTML 时使用。

### 2.4 escapeHtml 转义规则

| 字符 | 转义结果 | 说明 |
|------|---------|------|
| `<` | `&lt;` | HTML 实体 |
| `>` | `&gt;` | HTML 实体 |
| `&` | `＆` | **全角**字符（非 `&amp;`） |

> ⚠️ `&` 转义为全角 `＆`（U+FF06）是非标准做法，可能导致：
> - 数据库存储异常字符
> - 下游系统不兼容
> - JsoupUtil.unescape 反转义时需匹配

### 2.5 password 字段豁免

所有 XSS 包装器对名为 `password` 的参数跳过转义：

```java
if ("password".equals(parameter)) {
    return value;  // 原值返回
}
```

**原因**：密码可能含 `<`、`>`、`&` 等字符，转义会破坏原文。
**风险**：若 password 参数名被用于非密码字段，该字段将无 XSS 防护。

---

## 3. SQL 注入防护

### 3.1 SQLParser 的定位

`SQLParser` **不是** SQL 注入防护工具，而是 SQL **分析工具**：

| 功能 | 用途 | 是否防注入 |
|------|------|-----------|
| `parseTables()` | 提取表名 | ❌ |
| `matcherAll()` | 表名正则匹配 | ✅ 间接（白名单校验） |
| `fillSqlParams()` | 变量填充 | ⚠️ 有风险 |

### 3.2 fillSqlParams 的注入风险

```java
public static String fillSqlParams(String sql, Map<String, Object> values) {
    // ...
    sql = sql.replaceAll(valueRegx, value.toString());  // 字符串替换
}
```

**风险**：`fillSqlParams` 通过字符串替换填充变量，**不是参数化查询**。若 `value` 含 SQL 特殊字符（如 `'`、`;`），可能导致注入。

**quote 机制**：

| 分隔符 | quote | 行为 |
|--------|-------|------|
| `${...}` | false | 不加引号（高风险） |
| `#{...}` | `'` | 加单引号（中风险，未转义内部 `'`） |
| `$...$` | false | 不加引号 |
| `#...#` | `'` | 加单引号 |

> ⚠️ 即使加引号，也未对 value 中的 `'` 进行转义（如 `''`），存在注入风险。

### 3.3 推荐做法

- **优先使用 MyBatis 参数化查询**：`#{param}` 由 MyBatis 处理为 PreparedStatement
- **避免使用 `fillSqlParams`**：除非 value 来自可信源
- **表名白名单校验**：使用 `matcherAll(sql, "pm_.*")` 确保只查询允许的表

---

## 4. AES 加密最佳实践

### 4.1 当前实现

- **算法**：`AES/ECB/PKCS5Padding`
- **密钥派生**：`KeyGenerator` + `SHA1PRNG`（从 password 派生 128 位密钥）
- **编码**：UTF-8
- **Base64**：Spring `Base64Utils`

### 4.2 已知问题

| 问题 | 风险 | 建议 |
|------|------|------|
| ECB 模式 | 相同明文块产生相同密文块 | 改用 CBC/GCM + 随机 IV |
| SHA1PRNG 跨平台 | 不同 JDK 可能派生不同密钥 | 使用固定密钥字节或 PBKDF2 |
| 默认密码 `DP_SECRET` | 弱密码 | 始终传入强密码 |
| 异常返回 null | 调用方未检查 null 可能 NPE | 抛出运行时异常 |

### 4.3 推荐改进

```java
// 推荐：AES-GCM + 随机 IV
public static String encryptGCM(String content, byte[] key) {
    byte[] iv = new byte[12];
    SecureRandom.getInstanceStrong().nextBytes(iv);
    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    cipher.init(Cipher.ENCRYPT_MODE, 
        new SecretKeySpec(key, "AES"),
        new GCMParameterSpec(128, iv));
    byte[] encrypted = cipher.doFinal(content.getBytes(UTF_8));
    // 拼接 iv + encrypted
    return Base64.getEncoder().encodeToString(
        ByteBuffer.allocate(iv.length + encrypted.length)
            .put(iv).put(encrypted).array());
}
```

---

## 5. 验证码安全

### 5.1 当前实现

- **字符池**：`123456789ABCDEFGHIJKLMNPQRSTUVWXYZ`（34 字符，排除 0/O）
- **长度**：4 位
- **随机数**：`SecureRandom`（密码学安全）
- **干扰线**：50 条
- **存储**：HttpSession

### 5.2 安全建议

| 项 | 当前 | 建议 |
|----|------|------|
| 字符池大小 | 34 | 可接受 |
| 验证码长度 | 4 | 建议增至 5-6 位 |
| 过期时间 | Session 级 | 建议设置 5 分钟过期 |
| 失败次数限制 | 无 | 建议限制 5 次失败后刷新 |
| 大小写敏感 | `equalsIgnoreCase` | 可接受 |

---

## 6. IP 获取安全

### 6.1 代理头伪造风险

```java
// HttpContext.getCurrentIp() 依次检查代理头
ip = request.getHeader("x-forwarded-for");
ip = request.getHeader("Proxy-Client-IP");
// ...
```

**风险**：这些头可被客户端伪造。

**建议**：
- 仅信任已知代理服务器设置的头
- 从右向左解析 `X-Forwarded-For`（最右侧是最近的可信代理）
- 配置可信代理 IP 白名单

---

## 7. 相关文档

| 文档 | 说明 |
|------|------|
| [performance-optimization.md](performance-optimization.md) | 性能优化 |
| [troubleshooting.md](troubleshooting.md) | 故障排查 |
| [../02-modules/data-encryption.md](../02-modules/data-encryption.md) | 加密组件 |
