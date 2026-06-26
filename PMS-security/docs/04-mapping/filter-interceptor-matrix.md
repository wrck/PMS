# 过滤器/拦截器部署矩阵

## 1. 概述

本文档记录 PMS-security 模块提供的所有 Filter/Interceptor 在 PMS-struts 和 PMS-springmvc 两个 Web 层模块中的启用情况。

---

## 2. 部署矩阵

### 2.1 Servlet Filter

| Filter 类 | PMS-struts（dev） | PMS-struts（test/release/yfpms） | PMS-springmvc |
|-----------|-------------------|----------------------------------|---------------|
| `CsrfFilter` | ✅ `/*` | ❌（使用 CAS） | ❌ |
| `XssFilter` | ❌（web.xml 中已注释） | ❌ | ✅ `*.html`、`*.json`、`*.xlsx`、`*.xls`、`/modals/*` |
| `MStrutsPrepareAndExecuteFilter` | ❌（未启用，使用原生） | ❌ | ❌（不适用） |

### 2.2 Struts2 Interceptor

| Interceptor 类 | PMS-struts | PMS-springmvc |
|----------------|------------|---------------|
| `XssStrutsInterceptor` | ✅ `baseStack` 首位 | ❌（不适用） |

### 2.3 Spring MVC Interceptor

| Interceptor 类 | PMS-struts | PMS-springmvc |
|----------------|------------|---------------|
| `CsrfInterceptor` | ❌ | ✅ `/**`（排除 `/sys/login.json`） |
| `PasswordInterceptor`（本模块抽象类） | ❌ | ❌（直接使用抽象类） |
| `com.dp.plat.core.interceptor.PasswordInterceptor`（core 子类） | ❌ | ✅ `/**`（排除 `/password.*`、`/modifyPassword.*`） |

---

## 3. PMS-struts（dev）详细配置

> 来源：`PMS-struts/config/profiles/dev/web.xml` + `PMS-struts/config/struts.xml`

### 3.1 web.xml Filter 链

| 序号 | Filter | URL Pattern | 状态 |
|------|--------|-------------|------|
| 1 | `encodingFilter` | `/*` | 启用 |
| 2 | `CsrfFilter` | `/*` | 启用 |
| 3 | `XssFilter` | `/*` | **已注释** |
| 4 | `UserCheck` | `*.action` | 启用 |
| 5 | `struts2` | `*.action` | 启用 |

### 3.2 struts.xml Interceptor 栈

```xml
<interceptor-stack name="baseStack">
    <interceptor-ref name="XssStrutsInterceptor">  <!-- 第 1 位 -->
        <param name="enable">true</param>
        <param name="excludeUrls">/base/executeSql.*</param>
        <param name="cleanUrls">/module/prob_*,/probAudit.*,/probAjax_*.*</param>
        <param name="encodeUrls">/*</param>
    </interceptor-ref>
    <interceptor-ref name="MyInterceptor"/>        <!-- 第 2 位 -->
    <interceptor-ref name="fileUpload">...</interceptor-ref>
    <interceptor-ref name="defaultStack"/>          <!-- 第 N 位 -->
</interceptor-stack>
```

---

## 4. PMS-springmvc 详细配置

> 来源：`PMS-springmvc/src/main/webapp/WEB-INF/web.xml` + `PMS-springmvc/src/main/resources/spring-mvc.xml`

### 4.1 web.xml Filter 链

| 序号 | Filter | URL Pattern | 说明 |
|------|--------|-------------|------|
| 1 | `encodingFilter` | `/*` | UTF-8 编码 |
| 2 | `druidWebStatFilter` | `/*` | Druid 监控 |
| 3 | `shiroFilter` | `/*` | Shiro 认证 |
| 4 | `XssFilter` | `*.html`、`*.json`、`*.xlsx`、`*.xls`、`/modals/*` | XSS 防护 |

### 4.2 spring-mvc.xml Interceptor 链

| 序号 | Interceptor | mapping | exclude-mapping |
|------|-------------|---------|-----------------|
| 1 | `LocaleChangeInterceptor` | `/**` | - |
| 2 | `CsrfInterceptor` | `/**` | `/sys/login.json` |
| 3 | `PasswordInterceptor`（core） | `/**` | `/password.*`、`/modifyPassword.*` |

---

## 5. 环境差异说明

### 5.1 PMS-struts dev vs 非 dev

| 组件 | dev | 非 dev（test/release/yfpms） |
|------|-----|------------------------------|
| `CsrfFilter` | ✅ 启用 | ❌ 替换为 CAS Filter 链 |
| `XssFilter` | ❌ 注释 | ❌ 不存在 |
| `XssStrutsInterceptor` | ✅ 启用 | ✅ 启用 |

> dev 环境使用 CsrfFilter 做 CSRF 防护；非 dev 环境使用 CAS SSO，CSRF 防护由 CAS Filter 链替代。

### 5.2 PMS-springmvc 的 XssFilter URL Pattern

PMS-springmvc 的 `XssFilter` 使用**扩展名匹配**而非 `/*`：

```xml
<filter-mapping>
    <filter-name>XssFilter</filter-name>
    <url-pattern>*.html</url-pattern>
    <url-pattern>*.json</url-pattern>
    <url-pattern>*.xlsx</url-pattern>
    <url-pattern>*.xls</url-pattern>
    <url-pattern>/modals/*</url-pattern>
</filter-mapping>
```

> 未覆盖的路径（如 `.do`、无扩展名）不经过 XSS 过滤。

---

## 6. 配置参数矩阵

### 6.1 CsrfFilter

| 参数 | 配置方式 | PMS-struts dev | PMS-springmvc |
|------|---------|----------------|---------------|
| `excludePattern` | `<init-param>` | 未配置 | 不适用 |

### 6.2 XssFilter

| 参数 | 配置方式 | PMS-struts dev | PMS-springmvc |
|------|---------|----------------|---------------|
| `excludePattern` | `<init-param>` | 不适用（已注释） | 注释中可见 `/sys/notifyTemplate/.*\..*` |

### 6.3 XssStrutsInterceptor

| 参数 | PMS-struts 值 |
|------|---------------|
| `enable` | `true` |
| `excludeUrls` | `/base/executeSql.*` |
| `cleanUrls` | `/module/prob_*,/probAudit.*,/probAjax_*.*` |
| `encodeUrls` | `/*` |

### 6.4 CsrfInterceptor

| 参数 | PMS-springmvc 值 |
|------|-------------------|
| mapping | `/**` |
| exclude-mapping | `/sys/login.json` |

### 6.5 PasswordInterceptor（core 子类）

| 参数 | PMS-springmvc 值 |
|------|-------------------|
| mapping | `/**` |
| exclude-mapping | `/password.*`、`/modifyPassword.*` |
| `redirect` | `/password.html?needChangePwd=true` |

---

## 7. 相关文档

| 文档 | 说明 |
|------|------|
| [../01-architecture/security-filter-chain.md](../01-architecture/security-filter-chain.md) | 过滤器链架构 |
| [crud-matrix.md](crud-matrix.md) | CRUD 矩阵（无表） |
