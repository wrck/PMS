# PMS-security 安全组件模块总览

> 本文档基于实际源码重写，修正了旧版文档中的虚构内容。各组件详细说明见同目录下的专题文档。

---

## 1. 模块概述

PMS-security 是 PMS 系统的安全组件模块，以纯工具库（jar）形式提供 Web 安全横切能力。

- **artifactId**：`pms-security`
- **基础包**：`com.dp.plat.security`
- **打包类型**：jar
- **Struts2 版本**：2.3.35
- **无数据库表**：纯工具库

---

## 2. 组件清单（21 个 Java 类）

### 2.1 CSRF 防护（4 个类）

| 类 | 类型 | 说明 |
|----|------|------|
| `CSRFTokenManager` | 工具类（final，私有构造） | Token 生成（UUID）、会话存储、三通道提取 |
| `CsrfFilter` | Servlet Filter | Struts2 环境使用，所有方法校验，失败 forward /404.jsp |
| `CsrfInterceptor` | Spring MVC Interceptor | springmvc 环境使用，仅 POST/PUT/DELETE 校验，postHandle 注入 Token |
| `CsrfValidateFailedException` | RuntimeException | CSRF 校验失败异常 |

详见：[csrf-filter.md](csrf-filter.md)

### 2.2 XSS 防护（9 个类）

| 类 | 类型 | 说明 |
|----|------|------|
| `XssFilter` | Servlet Filter | 装配 `XssRequestBodyHttpServletRequestWrapper` |
| `XssHttpServletRequestWrapper` | Request Wrapper | 简单包装器（当前未使用） |
| `XssRequestBodyHttpServletRequestWrapper` | Request Wrapper | 版本 1：POST Body 缓存 + JSONValidator + ByteUtils multipart |
| `XssRequestBodyHttpServletRequestWrapper2` | Request Wrapper | 版本 2：JSON.parseObject + upload 分离 |
| `XssRequestBodyHttpServletRequestWrapper3` | Request Wrapper | 版本 3：JSON.parseObject + multipart 简化 |
| `XssStrutsInterceptor` | Struts2 Interceptor | 三级 URL 策略（exclude/clean/encode） |
| `MDispatcher` | Dispatcher 替换 | wrapRequest 使用 M 系列包装器 |
| `MStrutsRequestWrapper` | Request Wrapper 替换 | getParameter 系列调用 JsoupUtil.escape |
| `MMultiPartRequestWrapper` | Request Wrapper 替换 | 同上，用于 multipart 请求 |
| `MStrutsPrepareAndExecuteFilter` | Filter 替换 | createDispatcher 返回 MDispatcher |

详见：[xss-filter.md](xss-filter.md)

### 2.3 SQL 解析（1 个类 + 1 个内部类）

| 类 | 类型 | 说明 |
|----|------|------|
| `SQLParser` | 工具类 | 基于 Druid SQLUtils，表名提取、正则匹配、变量填充 |
| `SQLParser.SqlParserResult` | 内部类 | 匹配结果（valid + matchTables） |

详见：[sql-parser.md](sql-parser.md)

### 2.4 数据加密（1 个类）

| 类 | 类型 | 说明 |
|----|------|------|
| `ASEUtil` | 工具类 | AES/ECB/PKCS5Padding，KeyGenerator + SHA1PRNG 派生密钥 |

详见：[data-encryption.md](data-encryption.md)

### 2.5 验证码（1 个类）

| 类 | 类型 | 说明 |
|----|------|------|
| `CaptchaUtil` | 工具类 | 80×30 PNG 图形验证码，4 位字符，50 干扰线 |

详见：[captcha.md](captcha.md)

### 2.6 HTTP 上下文（1 个类）

| 类 | 类型 | 说明 |
|----|------|------|
| `HttpContext` | 工具类 | 请求/会话获取、请求类型判断（Ajax/JSON/HTML/Excel）、IP 获取 |

详见：[http-context.md](http-context.md)

### 2.7 密码拦截器（1 个类）

| 类 | 类型 | 说明 |
|----|------|------|
| `PasswordInterceptor` | 抽象 Interceptor | 模板方法模式，子类实现 isNeedRedirect |

详见：[password-interceptor.md](password-interceptor.md)

### 2.8 字节工具（1 个类）

| 类 | 类型 | 说明 |
|----|------|------|
| `ByteUtils` | 工具类 | KMP 算法字节查找、DirectByteBuffer 扩容读写 |

---

## 3. 关键设计纠正

> 以下是对旧版文档虚构内容的纠正，详见 [audit/audit-modules.md](../audit/audit-modules.md)

### 3.1 CSRFTokenManager

| 旧文档（虚构） | 实际源码 |
|---------------|---------|
| `generateToken(HttpSession session)` | `generateToken()`（无参，不写 Session） |
| `validateToken(HttpSession, String)` | **不存在**，校验逻辑在 CsrfFilter/CsrfInterceptor 中 |
| Session 属性名 `CSRF_TOKEN` | 实际为 `CSRFTokenManager.class.getName() + ".tokenval"` |
| Token 参数名 `_csrf` | 实际为 `__RequestVerificationToken` |

### 3.2 ASEUtil

| 旧文档（虚构） | 实际源码 |
|---------------|---------|
| `encrypt(String data)` 单参 | `encrypt(String content, String password)` 双参 |
| 硬编码 `KEY = "your-secret-key"` | `KeyGenerator` + `SHA1PRNG` 派生，默认密码 `"DP_SECRET"` |
| 直接 `SecretKeySpec(KEY.getBytes(), "AES")` | 通过 `SecureRandom` 种子生成密钥 |

### 3.3 SQLParser

| 旧文档（虚构） | 实际源码 |
|---------------|---------|
| 使用 JSQLParser（`CCJSqlParserUtil`） | 使用 **Druid SQLUtils** |
| `isValid(String sql)` 方法 | **不存在**，实际是 `parseTables()`、`matcherAll()` 等 |
| 仅校验 SQL 有效性 | 表名提取 + 正则匹配 + 变量填充（`${}`、`#{}`、`$|$`、`#|#`） |

### 3.4 PasswordInterceptor

| 旧文档（虚构） | 实际源码 |
|---------------|---------|
| 具体类，实现 `isPasswordExpired(UserDetail)` | **抽象类**，`isNeedRedirect(HttpServletRequest)` 为抽象方法 |
| 直接检查密码过期 | 模板方法模式，具体逻辑由 core 模块子类实现 |
| 使用 `UserContext.getCurrentUser()` | 不依赖 UserContext |

### 3.5 XssFilter

| 旧文档（虚构） | 实际源码 |
|---------------|---------|
| 装配 `XssHttpServletRequestWrapper` | 装配 `XssRequestBodyHttpServletRequestWrapper` |
| 无 excludePattern | 有 `excludePattern` init-param 支持 |

### 3.6 JsoupUtil

| 旧文档（虚构） | 实际源码 |
|---------------|---------|
| 仅 `clean(String input)` 使用 `Whitelist.relaxed()` | 多个方法，使用 `Safelist`（非 Whitelist） |
| 无 escape/unescape | 有 `escape()`、`unescape()`、`xssEncode()` |
| 无表单 Safelist | 有 `getFormSafelist()` 支持 input/select/label |

---

## 4. 部署矩阵

| 组件 | PMS-struts（dev） | PMS-struts（非 dev） | PMS-springmvc |
|------|-------------------|---------------------|---------------|
| CsrfFilter | ✅ `/*` | ❌（用 CAS） | ❌ |
| CsrfInterceptor | ❌ | ❌ | ✅ `/**`（排除 login） |
| XssFilter | ❌（已注释） | ❌ | ✅ `*.html/*.json` 等 |
| XssStrutsInterceptor | ✅ baseStack 首位 | ✅ | ❌ |
| MStrutsPrepareAndExecuteFilter | ❌（未启用） | ❌ | ❌ |
| PasswordInterceptor | ❌ | ❌ | ✅（core 子类） |

详见：[../04-mapping/filter-interceptor-matrix.md](../04-mapping/filter-interceptor-matrix.md)

---

## 5. 相关文档

| 文档 | 说明 |
|------|------|
| [csrf-filter.md](csrf-filter.md) | CSRF 防护组件 |
| [xss-filter.md](xss-filter.md) | XSS 防护组件 |
| [sql-parser.md](sql-parser.md) | SQL 解析组件 |
| [data-encryption.md](data-encryption.md) | 数据加密组件 |
| [captcha.md](captcha.md) | 验证码组件 |
| [http-context.md](http-context.md) | HTTP 上下文组件 |
| [password-interceptor.md](password-interceptor.md) | 密码拦截器组件 |
| [class-reference.md](class-reference.md) | 类参考清单 |
