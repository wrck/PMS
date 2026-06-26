# core 模块 — 公共工具类

> 本文档详解 core 模块的公共工具类，涵盖 DateUtil、IpUtil、JsoupUtil、SQLParser、ExcelView 等。
> 源码基准：`com.dp.plat.core.util`、`com.dp.plat.core.view`。

---

## 1. 工具类总览

core 提供 19 个工具类，覆盖日期、文件、安全、SQL、导出等场景。

| 工具类 | 包 | 职责 |
|--------|----|------|
| `DateUtil` | `core.util` | 日期格式化/计算 |
| `IpUtil` | `core.util` | IP 解析 |
| `JsoupUtil` | `core.util` | HTML 清洗（XSS 过滤） |
| `SQLParser` | `core.util` | SQL 解析（防注入/分页改写） |
| `FileUtil` | `core.util` | 文件读写 |
| `UploadUtils` | `core.util` | 文件上传 |
| `DownloadUtils` | `core.util` | 文件下载 |
| `ExportUtils` | `core.util` | Excel 导出组装 |
| `PasswordUtil` | `core.util` | 密码 MD5 加密 |
| `DESSecurityUtils` | `core.util` | DES 对称加解密 |
| `UUIDGenerator` | `core.util` | UUID 生成 |
| `MenuUtil` | `core.util` | 菜单树构建 |
| `AviatorUtils` | `core.util` | Aviator 表达式引擎 |
| `MessageUtils` | `core.util` | 国际化消息 |
| `PropertyUtil` | `core.util` | 配置文件读取 |
| `SystemLogUtil` | `core.util` | 日志工具 |
| `LinkedHashMapSort` | `core.util` | Map 排序 |
| `JdbcConnectionUtil` | `core.util` | 原生 JDBC 连接 |
| `JDBCPropertiesUtil` | `core.util` | JDBC 配置读取 |

---

## 2. DateUtil 日期工具

### 2.1 核心方法

| 方法 | 说明 |
|------|------|
| `format(Date, pattern)` | 格式化日期为字符串 |
| `parse(String, pattern)` | 解析字符串为日期 |
| `addDays(Date, int)` | 日期加减天数 |
| `addMonths(Date, int)` | 日期加减月数 |
| `diffDays(Date, Date)` | 计算天数差 |
| `getCurrentTime()` | 获取当前时间字符串 |
| `getFirstDayOfMonth(Date)` | 获取月首 |
| `getLastDayOfMonth(Date)` | 获取月末 |

### 2.2 使用示例

```java
// 格式化日期
String dateStr = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");

// 解析日期
Date date = DateUtil.parse("2026-06-25", "yyyy-MM-dd");

// 日期计算
Date tomorrow = DateUtil.addDays(new Date(), 1);
```

---

## 3. IpUtil IP 工具

### 3.1 核心方法

| 方法 | 说明 |
|------|------|
| `getIp(HttpServletRequest)` | 获取客户端真实 IP |
| `isIpValid(String)` | 校验 IP 格式 |
| `isIpInRange(String, String)` | 判断 IP 是否在范围内 |

### 3.2 IP 获取逻辑

```java
public static String getIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getRemoteAddr();
    }
    return ip;
}
```

- 依次检查代理头：`X-Forwarded-For` → `Proxy-Client-IP` → `WL-Proxy-Client-IP` → `RemoteAddr`；
- 配合 `HostFilter` 实现 IP 访问控制。

---

## 4. JsoupUtil HTML 清洗工具

### 4.1 核心方法

| 方法 | 说明 |
|------|------|
| `clean(String html)` | 清洗 HTML，移除危险标签 |
| `cleanRichText(String html)` | 富文本清洗（保留安全标签） |

### 4.2 XSS 防护

```java
public static String clean(String html) {
    // 使用 Jsoup 清洗 HTML
    // 移除 <script>、<iframe>、on* 事件属性等危险内容
    return Jsoup.clean(html, Safelist.basic());
}
```

- 基于 Jsoup 库实现 HTML 清洗；
- 移除 `<script>`、`<iframe>`、`on*` 事件属性等 XSS 载荷；
- 富文本场景保留安全标签（`<p>`、`<b>`、`<img>` 等）。

> **使用场景**：富文本编辑器内容（技术公告、通知模板）入库前清洗。

---

## 5. SQLParser SQL 解析工具

### 5.1 核心方法

| 方法 | 说明 |
|------|------|
| `parsePage(String sql, int pageNum, int pageSize)` | SQL 分页改写 |
| `parseCount(String sql)` | SQL 计数改写 |
| `validateSql(String sql)` | SQL 注入校验 |

### 5.2 分页改写

```java
// 原 SQL
String sql = "SELECT * FROM t_user WHERE status = 1";

// 分页改写
String pageSql = SQLParser.parsePage(sql, 0, 15);
// 结果: SELECT * FROM t_user WHERE status = 1 LIMIT 0, 15
```

### 5.3 SQL 注入防护

- 校验 SQL 是否包含危险关键字（`DROP`、`DELETE`、`UNION` 等）；
- 配合 MyBatis 参数化查询，双重防护。

---

## 6. ExcelView 导出视图

### 6.1 类说明

| 类 | 说明 |
|----|------|
| `ExcelView` | 基于 Apache POI 渲染 Excel（.xls） |
| `ExcelView4XLSX` | 基于 Apache POI 渲染 Excel（.xlsx） |
| `AbstractExcelView` | Excel 视图基类 |
| `ExportUtils` | 导出数据组装工具 |

### 6.2 导出流程

```mermaid
flowchart LR
    CTRL[Controller 返回 ModelAndView] --> VR[ContentNegotiatingViewResolver]
    VR -->|根据 .excel 扩展名| EV[ExcelView4XLSX]
    EV --> BUILD[构建 Workbook]
    BUILD --> HEADER[写表头]
    HEADER --> DATA[写数据行]
    DATA --> OUTPUT[输出到响应流]
```

### 6.3 使用示例

```java
@RequestMapping("/export")
public ModelAndView export() {
    List<User> users = userService.selectAllUser();
    Map<String, Object> model = new HashMap<>();
    model.put("users", users);
    model.put("fileName", "用户列表");
    return new ModelAndView("excelView", model);
}
```

---

## 7. PasswordUtil 密码工具

### 7.1 核心方法

| 方法 | 说明 |
|------|------|
| `encryptMD5Password(String plain, String salt, int iterations)` | MD5 加密（盐+迭代） |

### 7.2 加密算法

```java
public static String encryptMD5Password(String plainPassword, String salt, int iterations) {
    // MD5(plainPassword + salt) 迭代 1024 次
    // 盐值 = 用户名
}
```

| 项 | 值 |
|----|-----|
| 算法 | MD5 |
| 盐值 | 用户名 |
| 迭代次数 | 1024 |

---

## 8. DESSecurityUtils 加解密工具

### 8.1 核心方法

| 方法 | 说明 |
|------|------|
| `encrypt(String plain)` | DES 加密 |
| `decrypt(String cipher)` | DES 解密 |

### 8.2 使用场景

- 敏感数据传输加密（如 URL 参数中的 ID）；
- 数据库连接密码加密存储。

---

## 9. MenuUtil 菜单工具

### 9.1 核心方法

| 方法 | 说明 |
|------|------|
| `buildMenuTree(List<Menu>)` | 构建菜单树 |
| `drow(List<Menu>, String contextPath)` | 渲染菜单 HTML |

详见 [菜单管理](menu-management.md)。

---

## 10. AviatorUtils 表达式引擎

### 10.1 核心方法

| 方法 | 说明 |
|------|------|
| `eval(String expression, Map<String, Object> env)` | 执行表达式 |
| `compile(String expression)` | 编译表达式 |

### 10.2 使用场景

- 配合 pms-rules 模块实现动态规则计算；
- 业务规则引擎表达式执行。

```java
// 示例：计算规则表达式
Map<String, Object> env = new HashMap<>();
env.put("amount", 1000);
env.put("rate", 0.1);
Object result = AviatorUtils.eval("amount * rate", env);
// result = 100.0
```

---

## 11. MessageUtils 国际化工具

### 11.1 核心方法

| 方法 | 说明 |
|------|------|
| `getMessage(String key)` | 获取国际化消息 |
| `getMessage(String key, Object[] args)` | 获取带参数的国际化消息 |

### 11.2 国际化配置

- 配置文件：`messages_zh_CN.properties`、`messages_en_US.properties`；
- 通过 `?lang=xx` 参数切换语言（`LocaleChangeInterceptor`）。

---

## 12. 其他工具类

### 12.1 UUIDGenerator

```java
String uuid = UUIDGenerator.generate();  // 生成唯一 UUID
```

### 12.2 PropertyUtil

```java
String value = PropertyUtil.getProperty("config.properties", "key");
```

### 12.3 LinkedHashMapSort

```java
Map<String, String> sorted = LinkedHashMapSort.sortByValue(map);
```

### 12.4 JdbcConnectionUtil

- 原生 JDBC 连接工具（绕过连接池）；
- 用于外部系统直连场景。

---

## 13. 相关文档

- [02-modules 公共组件](common-components.md) — 工具类清单
- [file-management 文件管理](file-management.md) — FileUtil/UploadUtils
- [menu-management 菜单管理](menu-management.md) — MenuUtil
- [05-standards 安全实践](../05-standards/security-practices.md) — JsoupUtil/SQLParser
