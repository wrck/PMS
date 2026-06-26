# 术语表

## 1. 安全术语

| 术语 | 英文 | 说明 |
|------|------|------|
| CSRF | Cross-Site Request Forgery | 跨站请求伪造，攻击者诱导用户在已登录状态下发送非自愿请求 |
| XSS | Cross-Site Scripting | 跨站脚本攻击，向页面注入恶意客户端脚本 |
| SQL 注入 | SQL Injection | 通过拼接 SQL 语句执行非授权操作 |
| Token | Token | 令牌，用于身份验证或请求校验的随机字符串 |
| CSRF Token | CSRF Token | 用于防御 CSRF 的随机令牌，绑定会话 |
| 白名单 | Whitelist / Safelist | 允许列表，仅允许列表中的元素通过 |
| HTML 清理 | HTML Clean | 移除 HTML 中的危险标签和属性，保留安全内容 |
| HTML 编码 | HTML Encode | 将特殊字符转换为 HTML 实体（如 `<` → `&lt;`） |
| HTML 转义 | HTML Escape | 同 HTML 编码 |
| AES | Advanced Encryption Standard | 高级加密标准，对称加密算法 |
| ECB | Electronic Codebook | 电子密码本模式，AES 的一种加密模式 |
| PKCS5Padding | PKCS5 Padding | PKCS#5 填充方案，用于块加密 |
| SHA1PRNG | SHA-1 Pseudo-Random Number Generator | 基于 SHA-1 的伪随机数生成算法 |
| KeyGenerator | Key Generator | Java 密钥生成器，用于生成对称加密密钥 |
| SecretKeySpec | Secret Key Specification | Java 密钥规范，封装密钥字节 |
| UUID | Universally Unique Identifier | 通用唯一识别码，128 位 |
| HttpOnly | HttpOnly | Cookie 属性，禁止 JavaScript 访问 |
| Safelist | Safelist | Jsoup 的白名单类（原 Whitelist，jsoup 1.14+ 更名） |

---

## 2. 组件术语

| 术语 | 说明 |
|------|------|
| Filter | Servlet 过滤器，在请求到达 Servlet 前拦截 |
| Interceptor | 拦截器，在请求到达 Controller/Action 前拦截 |
| HandlerInterceptor | Spring MVC 拦截器接口 |
| AsyncHandlerInterceptor | 支持异步请求的 HandlerInterceptor 子接口 |
| AbstractInterceptor | Struts2 拦截器抽象基类 |
| HttpServletRequestWrapper | Servlet 请求包装器，可覆盖请求方法 |
| Dispatcher | Struts2 核心调度器，负责请求分发 |
| StrutsRequestWrapper | Struts2 请求包装器，支持 OGNL 属性访问 |
| MultiPartRequestWrapper | Struts2 multipart 请求包装器 |
| StrutsPrepareAndExecuteFilter | Struts2 核心 Filter，负责准备和执行 |
| RequestContextHolder | Spring 请求上下文持有者，线程绑定 |
| ModelAndView | Spring MVC 模型与视图 |

---

## 3. PMS-security 特有术语

| 术语 | 说明 |
|------|------|
| CSRFTokenManager | CSRF Token 管理器，负责生成/存储/提取 Token |
| CsrfFilter | CSRF Servlet 过滤器（Struts2 环境使用） |
| CsrfInterceptor | CSRF Spring MVC 拦截器 |
| XssFilter | XSS Servlet 过滤器 |
| XssStrutsInterceptor | Struts2 XSS 拦截器，三级 URL 策略 |
| XssRequestBodyHttpServletRequestWrapper | POST Body 缓存 + escapeHtml 的请求包装器（3 个版本） |
| MDispatcher | 替换原生 Dispatcher 的自定义实现 |
| MStrutsRequestWrapper | 替换原生 StrutsRequestWrapper |
| MMultiPartRequestWrapper | 替换原生 MultiPartRequestWrapper |
| MStrutsPrepareAndExecuteFilter | 替换原生 StrutsPrepareAndExecuteFilter |
| ASEUtil | AES 加密工具（注意命名：ASE 而非 AES） |
| JsoupUtil | HTML 清理/转义工具 |
| SQLParser | SQL 解析工具（基于 Druid） |
| SqlParserResult | SQL 解析结果（valid + matchTables） |
| ByteUtils | 字节工具（KMP 算法、DirectByteBuffer） |
| CaptchaUtil | 图形验证码工具 |
| HttpContext | HTTP 上下文工具 |
| PasswordInterceptor | 密码过期拦截器抽象类 |

---

## 4. URL 策略术语

| 术语 | 说明 |
|------|------|
| excludeUrls | 排除 URL 列表，不进行 XSS 处理 |
| cleanUrls | 清理 URL 列表，使用 HTML 白名单清理 |
| encodeUrls | 编码 URL 列表，使用 HTML 实体编码 |
| excludePattern | Filter 排除路径正则 |
| 三级 URL 策略 | exclude > clean > encode 的优先级处理 |

---

## 5. SQL 变量术语

| 术语 | 说明 |
|------|------|
| `${...}` | 不加引号的变量填充 |
| `#{...}` | 加单引号的变量填充 |
| `$...$` | 不加引号的变量填充（$ 分隔） |
| `#...#` | 加单引号的变量填充（# 分隔） |
| DbType | Druid 数据库类型枚举 |
| SchemaStatVisitor | Druid SQL 访问者，提取表/列信息 |
| SqlParserResult | 表名匹配结果（valid + matchTables） |

---

## 6. 相关文档

| 文档 | 说明 |
|------|------|
| [../02-modules/class-reference.md](../02-modules/class-reference.md) | 类参考清单 |
| [interface-template.md](interface-template.md) | 接口模板 |
