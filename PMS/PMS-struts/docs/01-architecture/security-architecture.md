# PMS 安全架构文档

## 1. CAS SSO 集成

### 1.1 概述

PMS 系统集成了 **CAS（Central Authentication Service）** 单点登录系统（CAS Client 版本 3.2.2），实现与企业统一认证平台的无缝对接。系统支持 CAS SSO 和本地表单登录双模式切换，通过系统参数 `sys.cas` 控制。

### 1.2 CAS 服务器配置

| 配置项 | 值 | 说明 |
|--------|-----|------|
| CAS 服务器地址 | `https://cas2.dptech.com:8443` | 企业 CAS 统一认证服务器 |
| CAS 登录入口 | `https://cas2.dptech.com:8443/login` | CAS 登录页面 URL |
| PMS 服务地址 | `http://10.162.0.141:8083` | PMS 应用服务器地址 |
| 双模式切换参数 | `sys.cas` | 值为 `1` 启用 CAS，值为 `0` 使用本地登录 |

### 1.3 CAS Filter 配置

> ⚠️ **环境差异提示**：以下 CAS Filter 配置仅存在于非 dev 环境（yfpms/test/release）的 `web.xml` 中。**dev 环境（`config/profiles/dev/web.xml`）完全没有 CAS 过滤器**，取而代之的是 `CsrfFilter`（`com.dp.plat.security.csrf.CsrfFilter`，映射 `/*`）⚠️ 但该类在源码中不存在，实际未生效。详见第 9 节"多环境配置差异"。

PMS 在 `web.xml` 中配置了 6 个 CAS 相关的过滤器/监听器，按执行顺序如下：

| 序号 | Filter / Listener | 类名 | URL Pattern | 说明 |
|------|-------------------|------|-------------|------|
| 1 | SingleSignOutHttpSessionListener | `org.jasig.cas.client.session.SingleSignOutHttpSessionListener` | - | 监听 Session 销毁事件，实现单点登出 |
| 2 | CAS Single Sign Out Filter | `org.jasig.cas.client.session.SingleSignOutFilter` | `/*` | 拦截登出请求，销毁本地 Session |
| 3 | CASFilter | `org.jasig.cas.client.authentication.AuthenticationFilter` | `/*` | 负责用户认证，未登录重定向到 CAS 登录页 |
| 4 | CAS Validation Filter | `org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter` | `/*` | 负责 CAS Ticket 校验 |
| 5 | CAS HttpServletRequest Wrapper Filter | `org.jasig.cas.client.util.HttpServletRequestWrapperFilter` | `/*` | 包装 Request，支持 `getRemoteUser()` 获取登录名 |
| 6 | CAS Assertion Thread Local Filter | `org.jasig.cas.client.util.AssertionThreadLocalFilter` | `/*` | 通过 `AssertionHolder.getAssertion().getPrincipal().getName()` 获取用户 |

**web.xml 配置**：

```xml
<listener>
    <listener-class>org.jasig.cas.client.session.SingleSignOutHttpSessionListener</listener-class>
</listener>

<filter>
    <filter-name>CAS Single Sign Out Filter</filter-name>
    <filter-class>org.jasig.cas.client.session.SingleSignOutFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>CAS Single Sign Out Filter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>

<filter>
    <filter-name>CASFilter</filter-name>
    <filter-class>org.jasig.cas.client.authentication.AuthenticationFilter</filter-class>
    <init-param>
        <param-name>casServerLoginUrl</param-name>
        <param-value>https://cas2.dptech.com:8443/login</param-value>
    </init-param>
    <init-param>
        <param-name>serverName</param-name>
        <param-value>http://10.162.0.141:8083</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>CASFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>

<filter>
    <filter-name>CAS Validation Filter</filter-name>
    <filter-class>org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter</filter-class>
    <init-param>
        <param-name>casServerUrlPrefix</param-name>
        <param-value>https://cas2.dptech.com:8443</param-value>
    </init-param>
    <init-param>
        <param-name>serverName</param-name>
        <param-value>http://10.162.0.141:8083</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>CAS Validation Filter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>

<filter>
    <filter-name>CAS HttpServletRequest Wrapper Filter</filter-name>
    <filter-class>org.jasig.cas.client.util.HttpServletRequestWrapperFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>CAS HttpServletRequest Wrapper Filter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>

<filter>
    <filter-name>CAS Assertion Thread Local Filter</filter-name>
    <filter-class>org.jasig.cas.client.util.AssertionThreadLocalFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>CAS Assertion Thread Local Filter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

### 1.4 CAS 认证流程

```
┌──────────┐         ┌──────────┐         ┌──────────┐         ┌──────────┐
│  浏览器   │         │  PMS 应用 │         │ CAS 服务器│         │  PMS 数据 │
│ (Client) │         │ (Server) │         │(cas2.dp) │         │   库      │
└────┬─────┘         └────┬─────┘         └────┬─────┘         └────┬─────┘
     │                    │                    │                    │
     │  1. 访问受保护资源  │                    │                    │
     │───────────────────▶│                    │                    │
     │                    │                    │                    │
     │  2. 302 重定向到    │                    │                    │
     │     CAS 登录页     │                    │                    │
     │◀───────────────────│                    │                    │
     │                    │                    │                    │
     │  3. 提交用户名/密码  │                    │                    │
     │────────────────────────────────────────▶│                    │
     │                    │                    │                    │
     │  4. 302 重定向回    │                    │                    │
     │     PMS + Ticket   │                    │                    │
     │◀────────────────────────────────────────│                    │
     │                    │                    │                    │
     │  5. 携带 Ticket    │                    │                    │
     │     访问 PMS       │                    │                    │
     │───────────────────▶│                    │                    │
     │                    │                    │                    │
     │                    │  6. 后端验证 Ticket │                    │
     │                    │───────────────────▶│                    │
     │                    │                    │                    │
     │                    │  7. 返回用户身份信息 │                    │
     │                    │◀───────────────────│                    │
     │                    │                    │                    │
     │                    │  8. 查询本地用户权限 │                    │
     │                    │───────────────────────────────────────▶│
     │                    │                    │                    │
     │                    │  9. 返回用户角色菜单 │                    │
     │                    │◀───────────────────────────────────────│
     │                    │                    │                    │
     │  10. 登录成功，     │                    │                    │
     │      返回请求资源   │                    │                    │
     │◀───────────────────│                    │                    │
     │                    │                    │                    │
```

### 1.5 双模式切换

系统通过 `sys.cas` 参数控制认证模式：

```java
String casStr = StringEscUtil.getText("sys.cas");
if ("1".equals(casStr)) {
    isCas = true;   // CAS SSO 模式
} else {
    isCas = false;  // 本地表单登录模式
}
```

| 模式 | `sys.cas` 值 | 登录方式 | 密码过期检查 |
|------|-------------|---------|------------|
| CAS SSO | `1` | 重定向到 CAS 登录页 | 跳过（CAS 用户不检查密码过期） |
| 本地登录 | `0` | PMS 本地 login.jsp 表单 | 启用（检查 `pwdoverdue` 字段） |

---

## 2. Spring Security 配置

> ⚠️ **严重事实性警告：Spring Security 实际未激活**
>
> `applicationContext-security.xml` 配置文件虽然存在于 `config-spring/` 目录中，但**从未被 Spring 容器加载**，具体原因如下：
>
> 1. `web.xml` 的 `contextConfigLocation` 仅引用了 `/WEB-INF/classes/applicationContext.xml` 和 `/WEB-INF/classes/beans-quartz.xml`，**未引用** `applicationContext-security.xml`。
> 2. `applicationContext.xml` 的 `<import>` 列表中引入了 `applicationContext-service.xml`、`applicationContext-common.xml`、`applicationContext-context.xml`、`applicationContext-action.xml`、`activiti-context.xml`、`spring-extend-mybatis.xml`，**未引入** `applicationContext-security.xml`。
> 3. `web.xml` 中**没有配置** `springSecurityFilterChain`（即 `DelegatingFilterProxy`），这是 Spring Security 生效的必要条件。没有此过滤器，即使配置文件被加载，Spring Security 的 HTTP 安全规则也不会执行。
>
> **影响**：本文档第 2 节描述的所有 Spring Security 功能（静态资源放行、角色拦截、内置 admin 用户）**均未生效**。第 7.3 节权限验证层次表中将 Spring Security 列为"第 1 层防线"的描述**不符合实际**。当前系统的第一层防线实际上是 `UserCheckFilter`。
>
> **建议**：如需启用 Spring Security，需在 `web.xml` 的 `contextConfigLocation` 中添加 `applicationContext-security.xml`，并配置 `DelegatingFilterProxy` 过滤器。

### 2.1 配置文件

**配置文件**：`config-spring/applicationContext-security.xml`

### 2.2 静态资源不过滤规则

以下 URL Pattern 配置为 `security="none"`，不经过 Spring Security 过滤：

| URL Pattern | 说明 |
|-------------|------|
| `/css/**` | CSS 样式文件 |
| `/images/**` | 图片资源 |
| `/js/**` | JavaScript 脚本 |
| `/static/**` | 静态资源目录 |
| `/plat/**` | 平台公共资源 |
| `/sys/**` | 系统公共资源 |
| `/template/**` | 模板文件 |
| `/work/**` | 工作流公共资源 |
| `/login.jsp` | 登录页面 |
| `/image.jsp` | 验证码图片 |
| `/test/**` | 测试内容 |

**配置代码**：

```xml
<sec:http pattern="/css/**" security="none"></sec:http>
<sec:http pattern="/images/**" security="none"></sec:http>
<sec:http pattern="/js/**" security="none"></sec:http>
<sec:http pattern="/static/**" security="none"></sec:http>
<sec:http pattern="/plat/**" security="none"></sec:http>
<sec:http pattern="/sys/**" security="none"></sec:http>
<sec:http pattern="/template/**" security="none"></sec:http>
<sec:http pattern="/work/**" security="none"></sec:http>
<sec:http pattern="/login.jsp" security="none"></sec:http>
<sec:http pattern="/image.jsp" security="none"/>
<sec:http pattern="/test/**" security="none"></sec:http>
```

### 2.3 安全拦截规则

```xml
<sec:http auto-config="true" access-denied-page="/error403.jsp">
    <sec:intercept-url pattern="/error.jsp" access="ROLE_SERVICE"/>
    <sec:intercept-url pattern="/**" access="ROLE_ADMIN"/>
    <sec:form-login login-page="/login.jsp"
        authentication-failure-url="/login.jsp?error=true"
        default-target-url="/error.jsp"/>
</sec:http>
```

| URL Pattern | 所需角色 | 说明 |
|-------------|---------|------|
| `/error.jsp` | `ROLE_SERVICE` | 错误页面需要 SERVICE 角色 |
| `/**` | `ROLE_ADMIN` | 所有其他请求需要 ADMIN 角色 |

### 2.4 内置管理员用户

```xml
<sec:authentication-manager>
    <sec:authentication-provider>
        <sec:user-service>
            <sec:user name="admin" password="admin" authorities="ROLE_ADMIN"/>
        </sec:user-service>
    </sec:authentication-provider>
</sec:authentication-manager>
```

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| `admin` | `admin` | `ROLE_ADMIN` | Spring Security 内置管理员，用于应急访问 |

> **安全提示**：此内置用户仅作为 Spring Security 的兜底配置，实际生产环境中 PMS 的认证由 CAS SSO 或 `UserCheckFilter` 处理，该内置用户的密码应修改或禁用。

> ⚠️ **注意**：由于 Spring Security 整体未激活（见第 2 节顶部警告），上述所有 Spring Security 配置（包括静态资源放行规则、角色拦截规则、内置 admin 用户）**均未生效**。`applicationContext-security.xml` 中的配置仅作为参考或未来启用的预留，当前运行时不会产生任何安全拦截效果。

---

## 3. XSS 防护

> ⚠️ **严重事实性警告：XSS 防护组件在当前源码中不存在**
>
> 本节描述的 `XssStrutsInterceptor`（`com.dp.plat.security.xss.struts.XssStrutsInterceptor`）及其所在包 `com.dp.plat.security` **在当前代码库中完全不存在**。`struts.xml` 中虽然配置了该拦截器，但由于类文件缺失，运行时将无法加载，XSS 防护功能**实际未生效**。
>
> **影响**：
> - 第 3.2 节描述的拦截器配置仅为设计意图，运行时会因类找不到而报错或跳过
> - 第 3.3 节描述的三级 URL 匹配策略（cleanUrls/encodeUrls/excludeUrls）**从未实际执行**
> - 第 3.4 节描述的拦截器执行顺序中，XssStrutsInterceptor 作为"第一道防线"的描述**不符合实际**
> - 当前系统**没有任何 XSS 防护功能在运行**
>
> **建议**：如需启用 XSS 防护，需先实现 `com.dp.plat.security.xss.struts.XssStrutsInterceptor` 类，或使用其他 XSS 防护方案。

### 3.1 XssStrutsInterceptor 拦截器

PMS 系统通过 Struts2 拦截器 `XssStrutsInterceptor` 实现 XSS（跨站脚本攻击）防护，该拦截器在 `struts.xml` 中配置，作为 `baseStack` 拦截器栈的第一个拦截器执行。

**拦截器类**：`com.dp.plat.security.xss.struts.XssStrutsInterceptor`

### 3.2 拦截器配置

`XssStrutsInterceptor` 在两个 Struts2 Package 中均进行了配置：

#### basepackage（常规请求）

```xml
<package name="basepackage" extends="struts-default" abstract="true">
    <interceptors>
        <interceptor name="XssStrutsInterceptor"
            class="com.dp.plat.security.xss.struts.XssStrutsInterceptor">
            <param name="enable">true</param>
            <param name="excludeUrls">/base/executeSql.*</param>
            <param name="cleanUrls">/module/prob_*,/probAudit.*,/probAjax_*.*</param>
            <param name="encodeUrls">/*</param>
        </interceptor>
        <interceptor-stack name="baseStack">
            <interceptor-ref name="XssStrutsInterceptor"/>
            <interceptor-ref name="MyInterceptor"/>
            <interceptor-ref name="fileUpload">...</interceptor-ref>
            <interceptor-ref name="defaultStack">...</interceptor-ref>
            <interceptor-ref name="paramsPrepareParamsStack"/>
        </interceptor-stack>
    </interceptors>
    <default-interceptor-ref name="baseStack"/>
</package>
```

#### defaultJson（JSON 请求）

```xml
<package name="defaultJson" extends="json-default" abstract="true">
    <interceptors>
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
    </interceptors>
    <default-interceptor-ref name="baseStack"/>
</package>
```

### 3.3 三级 URL 匹配策略

`XssStrutsInterceptor` 采用三级 URL 匹配策略，对不同类型的请求应用不同的 XSS 防护处理：

| 参数 | URL Pattern | 处理方式 | 说明 |
|------|-------------|---------|------|
| `cleanUrls` | `/module/prob_*`, `/probAudit.*`, `/probAjax_*.*` | HTML 清理（移除危险标签） | 技术公告等富文本模块，需要保留部分 HTML 格式，但清除 `<script>`、`onerror` 等危险内容 |
| `encodeUrls` | `/*` | URL 编码（全量转义） | 所有请求的参数值进行 HTML 实体编码，将 `<`、`>`、`"`、`'` 等特殊字符转义 |
| `excludeUrls` | `/base/executeSql.*` | 不处理（排除） | SQL 执行功能，需要保留原始输入，不做 XSS 过滤 |

**处理优先级**：`excludeUrls` > `cleanUrls` > `encodeUrls`

```
┌─────────────────────────────────────────────────────────┐
│                  请求进入 XssStrutsInterceptor           │
└───────────────────────┬─────────────────────────────────┘
                        │
                        ▼
              ┌─────────────────────┐
              │ URL 匹配            │
              │ excludeUrls ?       │──── 是 ──▶ 不处理，直接放行
              └─────────┬───────────┘
                        │ 否
                        ▼
              ┌─────────────────────┐
              │ URL 匹配            │
              │ cleanUrls ?         │──── 是 ──▶ HTML 清理模式
              └─────────┬───────────┘              （移除危险标签，
                        │ 否                          保留安全 HTML）
                        ▼
              ┌─────────────────────┐
              │ URL 匹配            │
              │ encodeUrls ?        │──── 是 ──▶ URL 编码模式
              └─────────┬───────────┘              （全量 HTML 实体转义）
                        │ 否
                        ▼
                   不处理，放行
```

### 3.4 拦截器执行顺序

```
HTTP 请求
    │
    ▼
┌──────────────────────┐
│ XssStrutsInterceptor │  ← 第一道防线：XSS 过滤
├──────────────────────┤
│ MyInterceptor        │  ← 自定义拦截器（当前为透传）
├──────────────────────┤
│ fileUpload           │  ← 文件上传拦截器
├──────────────────────┤
│ defaultStack         │  ← Struts2 默认拦截器栈
├──────────────────────┤
│ paramsPrepareParams  │  ← 参数预处理拦截器栈
└──────────────────────┘
    │
    ▼
  Action 执行
```

---

## 4. 用户检查过滤器

### 4.1 UserCheckFilter

`UserCheckFilter` 是 PMS 自定义的 Servlet Filter，对所有 `*.action` 请求进行登录状态检查和权限验证。

**配置**（`web.xml`）：

```xml
<filter>
    <filter-name>UserCheck</filter-name>
    <filter-class>com.dp.plat.util.UserCheckFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>UserCheck</filter-name>
    <url-pattern>*.action</url-pattern>
</filter-mapping>
```

### 4.2 过滤器执行流程

```
┌──────────────────────────────────────────────────────────┐
│               *.action 请求进入 UserCheckFilter           │
└──────────────────────────┬───────────────────────────────┘
                           │
                           ▼
                 ┌─────────────────────┐
                 │ 检查密码过期重定向    │──── 需要修改密码 ──▶ 302 重定向到修改密码页
                 │ getChangePassword    │
                 │ Redirect()          │
                 └─────────┬───────────┘
                           │ 不需要
                           ▼
                 ┌─────────────────────┐
                 │ 是否为文件下载请求？  │──── 是 ──▶ 直接放行
                 │ /module/DownloadFile │
                 └─────────┬───────────┘
                           │ 否
                           ▼
                 ┌─────────────────────┐
                 │ 记录当前请求 URL     │
                 │ userContext.setUrl() │
                 └─────────┬───────────┘
                           │
                           ▼
                 ┌─────────────────────┐
                 │ 用户是否已登录？      │──── 未登录 ──▶ 检查 CAS 模式
                 │ userContext.isLogin()│               │
                 └─────────┬───────────┘               │
                           │ 已登录                     ▼
                           │              ┌──────────────────────┐
                           │              │ CAS 模式(sys.cas=1)? │
                           │              │ 是 → 重定向 Login.action
                           │              │ 否 → 重定向 index.jsp
                           │              │     (Login Timeout!)  │
                           │              └──────────────────────┘
                           ▼
                 ┌─────────────────────┐
                 │ CAS 模式下访问       │──── 是 ──▶ 重定向 Login.action
                 │ login.jsp/index.jsp?│
                 └─────────┬───────────┘
                           │ 否
                           ▼
                 ┌─────────────────────┐
                 │ 权限验证             │
                 │ checkHandlerMapping │──── 无权限 ──▶ 重定向 404.action
                 └─────────┬───────────┘
                           │ 有权限
                           ▼
                      放行请求
                   chain.doFilter()
```

### 4.3 权限验证机制

`UserCheckFilter.checkHandlerMapping()` 实现了基于菜单 URL 的权限验证：

1. **缓存机制**：使用 `ConcurrentHashMap` 缓存用户的 URL 权限判断结果，避免重复计算
2. **排除规则**：通过 `sys.handler.mapping.check.exclude.mapping` 系统参数配置不需要权限校验的 URL
3. **菜单匹配**：将系统菜单的 URL 解析为通配符模式，与请求 URL 进行匹配
4. **权限判断**：匹配到菜单后，检查当前用户是否拥有该菜单的权限（`menuIdList.contains(menu.getId())`）

**URL 通配符转换规则**：

| 原始 URL | 转换后的通配符 | 说明 |
|----------|--------------|------|
| `/module/Project_list.action` | `/module/Project_*.action` | 下划线方法名通配 |
| `/module/Project!list.action` | `/module/Project!*.action` | 感叹号方法名通配 |
| `/module/ProjectManage.action` | `/module/Project*.action` | 去除 Manage/Action 后缀 |
| `/module/Project.action` | `/module/Project_*.action` | 添加方法名通配 |

---

## 5. 密码安全

### 5.1 密码加密方式

PMS 系统支持两种密码加密方式：

#### 方式一：MD5 加密（旧版）

**工具类**：`com.dp.plat.util.Md5Util`

```java
public static String getMD5(byte[] source) {
    java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
    md.update(source);
    byte tmp[] = md.digest();
    // 转换为 32 位十六进制字符串
    char str[] = new char[16 * 2];
    char hexDigits[] = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
    // ... 转换逻辑
    return s;
}
```

- 使用 `java.security.MessageDigest` 实现 MD5 摘要
- 输出 32 位小写十六进制字符串
- 无盐值，单次哈希

#### 方式二：SHA1 + MD5 加盐迭代加密（新版）

**工具类**：`com.dp.plat.util.PasswordUtil`

```java
public static String encryptPassword(String saltSource, String credentials) {
    return encryptMD5Password(encryptSHA1Password(credentials, saltSource, 1), saltSource, 1024);
}
```

加密流程：

```
原始密码 (credentials)
    │
    ▼ SHA1(密码, 盐值=用户名, 迭代1次)
SHA1 哈希值
    │
    ▼ MD5(SHA1哈希值, 盐值=用户名, 迭代1024次)
最终加密密码
```

| 步骤 | 算法 | 盐值 | 迭代次数 | 说明 |
|------|------|------|---------|------|
| 1 | SHA1 | 用户名 | 1 | 先对密码做一次 SHA1 加盐哈希 |
| 2 | MD5 | 用户名 | 1024 | 再对 SHA1 结果做 1024 次 MD5 加盐迭代哈希 |

### 5.2 密码过期机制

PMS 系统通过 `User` 实体的 `pwdoverdue` 字段实现密码过期检查：

```java
User user = userContext.getUser();
Date currentDate = new Date();
Date pwdoverdue = user.getPwdoverdue();
pwdoverdue = pwdoverdue != null ? pwdoverdue : currentDate;
needChangePwd = !currentDate.before(pwdoverdue);
```

**检查逻辑**：

| 条件 | 结果 |
|------|------|
| `pwdoverdue` 为 `null` | 视为已过期，需要修改密码 |
| 当前日期 > `pwdoverdue` | 密码已过期，需要修改密码 |
| 当前日期 ≤ `pwdoverdue` | 密码未过期，正常使用 |
| CAS 模式（`sys.cas=1`） | 跳过密码过期检查 |

**密码过期处理流程**：

1. `UserCheckFilter.getChangePasswordRedirect()` 检查密码是否过期
2. 若过期且非 CAS 模式，查询 `sys.change.password.redirect` 获取重定向地址
3. 排除 `sys.change.password.redirect.excludeUrls` 中的 URL
4. 设置 `session.needChangePwd = true`，重定向到密码修改页面
5. `PasswordInterceptor` 在 Struts2 拦截器层面进行同样的检查

> ⚠️ **事实性警告：PasswordInterceptor 未在拦截器栈中注册**
>
> `PasswordInterceptor`（`com.dp.plat.interceptor.PasswordInterceptor`）作为 Java 类存在于代码库中，但**未在 `struts.xml` 的 `baseStack` 拦截器栈中注册**。`baseStack` 中仅包含 `XssStrutsInterceptor`、`MyInterceptor`、`fileUpload`、`defaultStack`、`paramsPrepareParamsStack`，不包含 `PasswordInterceptor`。
>
> **影响**：密码过期检查**只有 `UserCheckFilter` 一层保障**，不存在文档之前声称的"拦截器层面双重保障"。如果 `UserCheckFilter` 的检查逻辑被绕过（例如直接访问非 `*.action` 的资源），密码过期检查将完全失效。
>
> **建议**：如需实现双重保障，应在 `struts.xml` 的 `baseStack` 中添加 `PasswordInterceptor` 的声明和引用。

---

## 6. CSRF 防护

> ⚠️ **严重事实性警告：CSRF 防护组件在当前源码中不存在**
>
> 本节描述的 `CSRFTokenManager`（`com.dp.plat.security.csrf.CSRFTokenManager`）和 `CsrfFilter`（`com.dp.plat.security.csrf.CsrfFilter`）及其所在包 `com.dp.plat.security` **在当前代码库中完全不存在**。虽然 `applicationContext.xml` 中有 `CSRFTokenManager` 的注释配置、dev 环境 `web.xml` 中有 `CsrfFilter` 的配置，但由于类文件缺失，这些配置**均无法生效**。
>
> **影响**：
> - 第 6.1 节描述的 `CSRFTokenManager` 仅为设计意图，Bean 配置已被注释且类不存在
> - 第 9.2 节描述的 dev 环境 `CsrfFilter` 配置仅为设计意图，类不存在导致过滤器无法加载
> - 当前系统**没有任何 CSRF 防护功能在运行**（所有环境均无）
>
> **建议**：如需启用 CSRF 防护，需先实现 `com.dp.plat.security.csrf.CSRFTokenManager` 和 `com.dp.plat.security.csrf.CsrfFilter` 类，或使用其他 CSRF 防护方案。

### 6.1 CSRFTokenManager（已禁用）

PMS 系统曾实现 CSRF（跨站请求伪造）防护，但当前处于**禁用状态**。

**配置**（`applicationContext.xml`，已注释）：

```xml
<!-- <bean id="CSRFTokenManager" class="com.dp.plat.security.csrf.CSRFTokenManager" >
    <constructor-arg name="csrfTokenName" value="csrfToken"/>
</bean> -->
```

### 6.2 设计意图

`CSRFTokenManager` 的设计思路：

| 配置项 | 值 | 说明 |
|--------|-----|------|
| Bean ID | `CSRFTokenManager` | CSRF 令牌管理器 |
| 类名 | `com.dp.plat.security.csrf.CSRFTokenManager` | 自定义 CSRF 防护实现 |
| 令牌参数名 | `csrfToken` | 表单中 CSRF 令牌的参数名 |

### 6.3 安全建议

当前 CSRF 防护已禁用，建议在以下场景中重新启用：

- 所有状态修改操作（INSERT/UPDATE/DELETE）的表单提交
- 关键业务操作（审批、权限变更等）
- 管理员操作接口

---

## 7. 权限控制模型

### 7.1 四级权限体系

PMS 系统采用 **User → RoleIds → Role → Menu → Power** 四级权限控制模型：

```
┌─────────────────────────────────────────────────────────────┐
│                     User（用户）                              │
│  - id: 用户ID                                                │
│  - username: 用户名                                          │
│  - roleIds: 角色ID列表（逗号分隔）                             │
│  - areapower: 区域权限                                       │
│  - dpNo: 部门编号                                            │
│  - pwdoverdue: 密码过期时间                                   │
└──────────────────────────┬──────────────────────────────────┘
                           │ 1:N
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                     Role（角色）                              │
│  - id: 角色ID                                                │
│  - 角色名称、描述                                             │
│  - 关联菜单列表                                               │
└──────────────────────────┬──────────────────────────────────┘
                           │ 1:N
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                     Menu（菜单）                              │
│  - id: 菜单ID                                                │
│  - path: 菜单URL路径                                         │
│  - name: 菜单名称                                            │
│  - 子菜单列表（UserMenu.userMenuList）                        │
└──────────────────────────┬──────────────────────────────────┘
                           │ 1:N
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                     Power（权限）                             │
│  - 菜单级别的操作权限                                         │
│  - 控制按钮级别的可见性                                       │
│  - 数据范围权限（区域权限 areapower）                          │
└─────────────────────────────────────────────────────────────┘
```

### 7.2 权限数据流转

```
┌──────────┐    查询用户角色    ┌──────────┐    查询角色菜单    ┌──────────┐
│   User   │────────────────▶│   Role   │────────────────▶│   Menu   │
│ (用户表)  │  queryUserMenu   │ (角色表)  │  queryAllMenu   │ (菜单表)  │
│          │  Map(userId)     │          │  List()         │          │
└──────────┘                  └──────────┘                  └──────────┘
      │                                                        │
      │ 登录时加载权限数据                                       │ URL 通配符匹配
      ▼                                                        ▼
┌──────────────────────────────────────────────────────────────┐
│                     UserContext（用户上下文）                   │
│  - login: 是否已登录                                          │
│  - user: 当前用户对象                                         │
│  - permissionMap: 权限映射 (menuId → permission)              │
│  - roleMenuPowerMap: 角色菜单权限映射                          │
│  - extData: 扩展数据（含 cachedUserHandlerMapping）            │
└──────────────────────────────────────────────────────────────┘
```

### 7.3 权限验证层次

PMS 系统的权限验证在多个层次进行：

| 层次 | 组件 | 验证方式 | 说明 |
|------|------|---------|------|
| 第 1 层 | ~~Spring Security~~ **（未激活）** | URL Pattern + Role | ⚠️ 配置存在但未生效，见第 2 节警告。实际不提供任何安全拦截 |
| 第 2 层 | UserCheckFilter | URL + 菜单权限 | **实际的第一层防线**，细粒度，基于菜单 URL 通配符匹配 |
| 第 3 层 | Action 代码 | 业务逻辑权限 | 特定业务操作权限检查（如 `getUserPower()`） |
| 第 4 层 | JSP 标签 | 按钮级别控制 | 页面按钮的显示/隐藏控制 |

### 7.4 登录认证流程

```
┌──────────────────────────────────────────────────────────────┐
│                       登录认证流程                             │
└──────────────────────────┬───────────────────────────────────┘
                           │
            ┌──────────────┴──────────────┐
            │                             │
     sys.cas = "0"                  sys.cas = "1"
     本地表单登录                    CAS SSO 登录
            │                             │
            ▼                             ▼
┌────────────────────┐     ┌──────────────────────────┐
│ LoginAction.start  │     │ CAS Filter 自动认证       │
│ 展示 login.jsp     │     │ 重定向到 CAS 登录页       │
└────────┬───────────┘     └────────────┬─────────────┘
         │                              │
         ▼                              ▼
┌────────────────────┐     ┌──────────────────────────┐
│ LoginAction.login  │     │ CAS Ticket 校验          │
│ 提交用户名/密码     │     │ 获取 CAS 认证用户名       │
└────────┬───────────┘     └────────────┬─────────────┘
         │                              │
         ▼                              ▼
┌────────────────────┐     ┌──────────────────────────┐
│ LoginService.login │     │ 根据 CAS 用户名           │
│ MD5 密码验证       │     │ 查询本地用户库            │
│ 验证码校验(生产环境) │     │ 加载权限数据              │
└────────┬───────────┘     └────────────┬─────────────┘
         │                              │
         └──────────────┬───────────────┘
                        │
                        ▼
         ┌──────────────────────────┐
         │ 加载用户权限数据           │
         │ - queryUserMenuMap()     │
         │ - 处理区域权限 areapower  │
         │ - 设置 UserContext       │
         └────────────┬─────────────┘
                      │
                      ▼
         ┌──────────────────────────┐
         │ 密码过期检查              │
         │ pwdoverdue 字段判断       │
         │ CAS 模式跳过此检查        │
         └────────────┬─────────────┘
                      │
                      ▼
              登录成功，进入系统
```

### 7.5 关键角色定义

| 角色标识 | 角色名称 | 说明 |
|---------|---------|------|
| `ROLE_ADMIN` | 系统管理员 | Spring Security 内置角色，拥有所有权限 |
| `ROLE_SERVICE` | 服务角色 | 可访问错误页面等基础功能 |
| 服务经理 (sm) | 项目服务经理 | 项目服务管理相关权限 |
| 项目经理 (prosm) | 项目经理 | 项目管理相关权限 |

### 7.6 Session 管理

| 配置项 | 值 | 说明 |
|--------|-----|------|
| Session 超时时间 | 120 分钟 | `web.xml` 中 `<session-timeout>120</session-timeout>` |
| 编码过滤器 | UTF-8 | `CharacterEncodingFilter` 强制 UTF-8 编码 |
| 单点登出 | 启用 | `SingleSignOutHttpSessionListener` + `SingleSignOutFilter` |

---

## 8. 安全架构总览

```
┌─────────────────────────────────────────────────────────────────────┐
│                          浏览器 (Client)                             │
└───────────────────────────────┬─────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        Web 容器 (Tomcat)                             │
│                                                                     │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │                    Filter Chain                                │  │
│  │                                                               │  │
│  │  1. CharacterEncodingFilter (UTF-8 编码)                      │  │
│  │  2. SingleSignOutFilter (CAS 单点登出)                         │  │
│  │  3. CASFilter (CAS 认证重定向)                                 │  │
│  │  4. CAS Validation Filter (CAS Ticket 校验)                   │  │
│  │  5. CAS HttpServletRequest Wrapper Filter                      │  │
│  │  6. CAS Assertion Thread Local Filter                          │  │
│  │  7. UserCheckFilter (登录状态 + 权限检查)                      │  │
│  │  8. StrutsPrepareFilter → SiteMesh → StrutsExecuteFilter      │  │
│  │  9. StrutsPrepareAndExecuteFilter (合并式，与8并存)             │  │
│  │ 10. ResponseOverrideFilter (DisplayTag 响应覆盖)               │  │
│  │     └─ XssStrutsInterceptor (XSS 防护) ← ⚠️ 类不存在，未生效    │  │
│  │     └─ MyInterceptor (自定义拦截器)                             │  │
│  │     └─ PasswordInterceptor (密码过期检查) ← ⚠️ 未注册到栈中    │  │
│  └───────────────────────────────────────────────────────────────┘  │
│                                                                     │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │              Spring Security ⚠️ 未激活                         │  │
│  │  - applicationContext-security.xml 未被 Spring 容器加载        │  │
│  │  - web.xml 未配置 springSecurityFilterChain                   │  │
│  │  - 以下配置均未生效：                                          │  │
│  │    · 静态资源放行                                              │  │
│  │    · ROLE_ADMIN / ROLE_SERVICE 角色控制                        │  │
│  │    · 内置 admin 用户                                           │  │
│  └───────────────────────────────────────────────────────────────┘  │
│                                                                     │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │                    业务安全层                                   │  │
│  │  - User → Role → Menu → Power 四级权限模型                     │  │
│  │  - URL 通配符菜单权限匹配                                      │  │
│  │  - 密码 MD5/SHA1+MD5 加密                                      │  │
│  │  - 密码过期机制 (pwdoverdue) ← 仅 UserCheckFilter 一层保障     │  │
│  │  - CSRFTokenManager (已禁用) ← ⚠️ 类不存在                    │  │
│  └───────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────┘
```

> ⚠️ **过滤器链补充说明**
>
> 1. **ResponseOverrideFilter 遗漏**：上图已补充 `ResponseOverrideFilter`（`org.displaytag.filter.ResponseOverrideFilter`），该过滤器在 `web.xml` 中配置了 `*.action` 和 `*.jsp` 两个 URL Pattern 映射，用于 DisplayTag 分页导出功能。它位于 Struts2 过滤器之后。
>
> 2. **Struts2 过滤器重复配置问题**：`web.xml` 中同时配置了两套 Struts2 过滤器：
>    - **三段式**：`StrutsPrepareFilter` → `SiteMesh(PageFilter)` → `StrutsExecuteFilter`（序号 8）
>    - **合并式**：`StrutsPrepareAndExecuteFilter`（序号 9）
>
>    这两种方式本应只选其一。同时配置可能导致请求被 Struts2 处理两次，引发潜在问题。三段式配置的目的是在 Struts2 处理前后插入 SiteMesh 装饰器，而合并式过滤器已包含 Prepare 和 Execute 两阶段，会绕过 SiteMesh。
>
> 3. **环境差异**：CAS 过滤器（序号 2-6）仅在非 dev 环境（yfpms/test/release）的 `web.xml` 中配置，dev 环境使用 `CsrfFilter` 替代 CAS 过滤器链。详见下方环境差异说明。

---

## 9. 多环境配置差异

> ⚠️ **重要说明：安全配置存在环境差异**
>
> PMS 项目通过 Maven Profile 机制管理多环境配置，不同环境的 `web.xml` 存在显著差异，直接影响安全架构的行为。

### 9.1 环境配置文件位置

| 环境 | web.xml 路径 | 说明 |
|------|-------------|------|
| dev | `config/profiles/dev/web.xml` | 开发环境 |
| yfpms | `config/profiles/yfpms/web.xml` | 预生产环境 |
| test | `config/profiles/test/web.xml` | 测试环境 |
| release | `config/profiles/release/web.xml` | 生产环境 |

### 9.2 关键差异：CAS vs CsrfFilter

| 配置项 | dev 环境 | 非 dev 环境（yfpms/test/release） |
|--------|---------|----------------------------------|
| CAS 过滤器链 | ❌ 未配置 | ✅ 配置完整（6 个 CAS Filter/Listener） |
| CsrfFilter | ✅ 配置（`com.dp.plat.security.csrf.CsrfFilter`，`/*`）⚠️ 类不存在 | ❌ 未配置 |
| SingleSignOutHttpSessionListener | ❌ 未配置 | ✅ 配置 |
| 其他过滤器 | 相同 | 相同 |

**dev 环境过滤器链顺序**：
1. `CharacterEncodingFilter`（UTF-8 编码）
2. `CsrfFilter`（CSRF 防护，替代 CAS 过滤器链）⚠️ 类不存在，实际未生效
3. `UserCheckFilter`（登录状态 + 权限检查）
4. `StrutsPrepareFilter` → `SiteMesh` → `StrutsExecuteFilter`（三段式）
5. `StrutsPrepareAndExecuteFilter`（合并式）
6. `ResponseOverrideFilter`（DisplayTag）

**非 dev 环境过滤器链顺序**：
1. `CharacterEncodingFilter`（UTF-8 编码）
2. `SingleSignOutFilter`（CAS 单点登出）
3. `CASFilter`（CAS 认证重定向）
4. `CAS Validation Filter`（CAS Ticket 校验）
5. `CAS HttpServletRequest Wrapper Filter`
6. `CAS Assertion Thread Local Filter`
7. `UserCheckFilter`（登录状态 + 权限检查）
8. `StrutsPrepareFilter` → `SiteMesh` → `StrutsExecuteFilter`（三段式）
9. `StrutsPrepareAndExecuteFilter`（合并式）
10. `ResponseOverrideFilter`（DisplayTag）

### 9.3 影响分析

- **dev 环境无 CAS 保护**：开发环境下所有请求不经过 CAS 认证，依赖 `UserCheckFilter` 和本地表单登录进行认证。
- **dev 环境"有" CsrfFilter**：开发环境 `web.xml` 中配置了 `CsrfFilter`，但 ⚠️ 该类在源码中不存在，实际未生效；而生产环境没有此过滤器配置，CSRF 防护依赖已注释的 `CSRFTokenManager`（类同样不存在）。**结论：所有环境均无 CSRF 防护**。
- **环境切换风险**：从 dev 切换到生产环境时，安全模型发生根本变化（CsrfFilter → CAS），需确保 `sys.cas` 参数与环境匹配。

---

## 10. 配置文件索引

| 文件路径 | 用途 |
|---------|------|
| `WebContent/WEB-INF/web.xml` | **默认/生产环境** web.xml（含完整 CAS 过滤器链） |
| `config/profiles/dev/web.xml` | **开发环境** web.xml（无 CAS，配置了 CsrfFilter ⚠️ 但类不存在） |
| `config/profiles/yfpms/web.xml` | 预生产环境 web.xml |
| `config/profiles/test/web.xml` | 测试环境 web.xml |
| `config/profiles/release/web.xml` | 生产环境 web.xml |
| `config-spring/applicationContext-security.xml` | Spring Security 配置（⚠️ 未被加载，见第 2 节警告） |
| `config-spring/applicationContext.xml` | 主 Spring 配置文件（⚠️ 未 import applicationContext-security.xml） |
| `config/struts.xml` | XssStrutsInterceptor、拦截器栈配置（⚠️ PasswordInterceptor 未注册到 baseStack） |
| `src/com/dp/plat/util/UserCheckFilter.java` | 登录状态检查、菜单权限验证 |
| `src/com/dp/plat/util/Md5Util.java` | MD5 加密工具类 |
| `src/com/dp/plat/util/PasswordUtil.java` | SHA1+MD5 加盐迭代加密工具类 |
| `src/com/dp/plat/interceptor/PasswordInterceptor.java` | 密码过期拦截器（⚠️ 未在 struts.xml 中注册使用） |
| `src/com/dp/plat/context/UserContext.java` | 用户上下文，权限数据持有 |
| `src/com/dp/plat/service/LoginServiceImpl.java` | 登录认证逻辑 |
