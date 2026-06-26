# Web过滤器链与Servlet配置文档

> 本文档基于 `WebContent/WEB-INF/web.xml` 配置文件，详细描述 PMS 系统的过滤器链、Servlet、Listener 及其他 Web 层配置。

---

## 1. 过滤器链（Filter Chain）

PMS 系统的过滤器按照 `web.xml` 中的声明顺序依次执行，形成完整的请求处理管道。下表按执行顺序列出所有过滤器：

### 1.1 过滤器执行顺序总览

| 序号 | 过滤器名称 | 过滤器类 | URL匹配模式 | 功能说明 |
|:---:|:---|:---|:---|:---|
| 1 | encodingFilter | `org.springframework.web.filter.CharacterEncodingFilter` | `/*` | UTF-8 编码过滤器 |
| 2 | CAS Single Sign Out Filter | `org.jasig.cas.client.session.SingleSignOutFilter` | `/*` | CAS 单点登出过滤器 |
| 3 | CASFilter | `org.jasig.cas.client.authentication.AuthenticationFilter` | `/*` | CAS 认证过滤器 |
| 4 | CAS Validation Filter | `org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter` | `/*` | CAS Ticket 校验过滤器 |
| 5 | CAS HttpServletRequest Wrapper Filter | `org.jasig.cas.client.util.HttpServletRequestWrapperFilter` | `/*` | CAS 请求包装过滤器 |
| 6 | CAS Assertion Thread Local Filter | `org.jasig.cas.client.util.AssertionThreadLocalFilter` | `/*` | CAS 线程本地变量过滤器 |
| 7 | UserCheck | `com.dp.plat.util.UserCheckFilter` | `*.action` | 用户登录检查过滤器 |
| 8 | struts-prepare | `org.apache.struts2.dispatcher.ng.filter.StrutsPrepareFilter` | `/*` | Struts2 准备阶段过滤器 |
| 9 | sitemesh | `com.opensymphony.module.sitemesh.filter.PageFilter` | `/*` | SiteMesh 页面装饰过滤器 |
| 10 | struts-execute | `org.apache.struts2.dispatcher.ng.filter.StrutsExecuteFilter` | `/*` | Struts2 执行阶段过滤器 |
| 11 | struts2 | `org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter` | `/*` | Struts2 主过滤器 |
| 12 | ResponseOverrideFilter | `org.displaytag.filter.ResponseOverrideFilter` | `*.action, *.jsp` | DisplayTag 报表过滤器 |

### 1.2 各过滤器详细说明

#### 1.2.1 CharacterEncodingFilter（编码过滤器）

**作用**：统一设置请求和响应的字符编码为 UTF-8，防止中文乱码问题。

**配置示例**：

```xml
<filter>
    <filter-name>encodingFilter</filter-name>
    <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
    <init-param>
        <param-name>encoding</param-name>
        <param-value>UTF-8</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>encodingFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

**要点**：
- 必须作为第一个过滤器，确保后续所有过滤器处理的请求均已正确编码
- 拦截所有请求（`/*`）

---

#### 1.2.2 CAS Single Sign Out Filter（CAS 单点登出过滤器）

**作用**：实现 CAS 单点登出功能。当用户在 CAS 服务器端执行登出操作时，所有接入 CAS 的应用系统将同步销毁该用户的会话。

**配置示例**：

```xml
<filter>
    <filter-name>CAS Single Sign Out Filter</filter-name>
    <filter-class>org.jasig.cas.client.session.SingleSignOutFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>CAS Single Sign Out Filter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

**要点**：
- 需配合 `SingleSignOutHttpSessionListener` 使用
- 在 CAS 认证过滤器之前配置，以确保登出请求能被正确处理

---

#### 1.2.3 CASFilter（CAS 认证过滤器）

**作用**：负责用户的 CAS 认证工作。当用户未登录时，自动重定向到 CAS 登录页面。

**配置示例**：

```xml
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
```

**参数说明**：

| 参数名 | 值 | 说明 |
|:---|:---|:---|
| casServerLoginUrl | `https://cas2.dptech.com:8443/login` | CAS 服务器登录地址 |
| serverName | `http://10.162.0.141:8083` | 当前应用服务器地址，用于 CAS 回调 |

---

#### 1.2.4 CAS Validation Filter（CAS Ticket 校验过滤器）

**作用**：负责对 CAS 返回的 Ticket 进行校验，使用 CAS 2.0 协议。

**配置示例**：

```xml
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
```

**参数说明**：

| 参数名 | 值 | 说明 |
|:---|:---|:---|
| casServerUrlPrefix | `https://cas2.dptech.com:8443` | CAS 服务器根地址 |
| serverName | `http://10.162.0.141:8083` | 当前应用服务器地址 |

---

#### 1.2.5 CAS HttpServletRequest Wrapper Filter（CAS 请求包装过滤器）

**作用**：包装 `HttpServletRequest`，使开发者可以通过标准的 Servlet API（如 `getRemoteUser()`）获取 CAS 登录用户信息。

**配置示例**：

```xml
<filter>
    <filter-name>CAS HttpServletRequest Wrapper Filter</filter-name>
    <filter-class>org.jasig.cas.client.util.HttpServletRequestWrapperFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>CAS HttpServletRequest Wrapper Filter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

**要点**：
- 可选配置，但推荐启用
- 允许通过 `request.getRemoteUser()` 获取 SSO 登录用户名

---

#### 1.2.6 CAS Assertion Thread Local Filter（CAS 线程本地变量过滤器）

**作用**：将 CAS 认证断言（Assertion）存储到线程本地变量中，使开发者可以通过 `AssertionHolder` 在任意代码位置获取用户登录信息。

**配置示例**：

```xml
<filter>
    <filter-name>CAS Assertion Thread Local Filter</filter-name>
    <filter-class>org.jasig.cas.client.util.AssertionThreadLocalFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>CAS Assertion Thread Local Filter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

**使用方式**：

```java
String username = AssertionHolder.getAssertion().getPrincipal().getName();
```

---

#### 1.2.7 UserCheckFilter（用户登录检查过滤器）

**作用**：检查用户是否已登录，仅对 `.action` 请求生效。这是 PMS 自定义的业务过滤器。

**配置示例**：

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

**要点**：
- 仅拦截 `*.action` 请求，不对静态资源进行登录检查
- 位于 CAS 过滤器之后，确保 CAS 认证已完成

---

#### 1.2.8 StrutsPrepareFilter（Struts2 准备阶段过滤器）

**作用**：Struts2 请求处理的第一阶段，负责创建 `ActionContext`、清理旧上下文等准备工作。

**配置示例**：

```xml
<filter>
    <filter-name>struts-prepare</filter-name>
    <filter-class>org.apache.struts2.dispatcher.ng.filter.StrutsPrepareFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>struts-prepare</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

**要点**：
- 使用 `org.apache.struts2.dispatcher.ng.filter` 包名（旧版 Struts2）
- 新版 Struts2 2.5.x 对应包名为 `org.apache.struts2.dispatcher.filter`（配置中已注释标注）

---

#### 1.2.9 SiteMesh PageFilter（页面装饰过滤器）

**作用**：SiteMesh 页面装饰框架，负责对页面输出进行装饰（如添加统一的页头、页脚、导航栏等布局）。

**配置示例**：

```xml
<filter>
    <filter-name>sitemesh</filter-name>
    <filter-class>com.opensymphony.module.sitemesh.filter.PageFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>sitemesh</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

**要点**：
- 位于 `StrutsPrepareFilter` 和 `StrutsExecuteFilter` 之间
- 这种三段式过滤器配置（Prepare → SiteMesh → Execute）是 Struts2 与 SiteMesh 集成的标准模式
- SiteMesh 在 Struts2 执行前拦截响应，对输出 HTML 进行装饰

---

#### 1.2.10 StrutsExecuteFilter（Struts2 执行阶段过滤器）

**作用**：Struts2 请求处理的第二阶段，负责执行 Action、渲染结果等核心逻辑。

**配置示例**：

```xml
<filter>
    <filter-name>struts-execute</filter-name>
    <filter-class>org.apache.struts2.dispatcher.ng.filter.StrutsExecuteFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>struts-execute</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

---

#### 1.2.11 StrutsPrepareAndExecuteFilter（Struts2 主过滤器）

**作用**：Struts2 的主过滤器，合并了准备和执行两个阶段。当不需要 SiteMesh 装饰时，可单独使用此过滤器。

**配置示例**：

```xml
<filter>
    <filter-name>struts2</filter-name>
    <filter-class>org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>struts2</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

**要点**：
- 当前系统同时配置了三段式过滤器（Prepare + SiteMesh + Execute）和此合并过滤器
- 三段式配置优先处理，此过滤器作为兜底处理未被前面过滤器捕获的请求

---

#### 1.2.12 ResponseOverrideFilter（DisplayTag 报表过滤器）

**作用**：DisplayTag 标签库的响应覆盖过滤器，用于支持 DisplayTag 的报表导出功能（如 Excel、PDF 导出）。

**配置示例**：

```xml
<filter>
    <filter-name>ResponseOverrideFilter</filter-name>
    <filter-class>org.displaytag.filter.ResponseOverrideFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>ResponseOverrideFilter</filter-name>
    <url-pattern>*.action</url-pattern>
</filter-mapping>
<filter-mapping>
    <filter-name>ResponseOverrideFilter</filter-name>
    <url-pattern>*.jsp</url-pattern>
</filter-mapping>
```

**要点**：
- 同时映射 `*.action` 和 `*.jsp` 两种模式
- 必须在 Struts2 过滤器之后配置，以确保 Action 执行完毕后再进行响应覆盖

---

### 1.3 过滤器链执行流程图

> **重要风险提示**：当前系统同时配置了三段式过滤器（StrutsPrepareFilter + SiteMesh + StrutsExecuteFilter）和合并式过滤器（StrutsPrepareAndExecuteFilter）。这种双重配置可能导致请求被重复处理，具体表现为：
> - 三段式过滤器处理请求后，合并式过滤器可能再次处理同一请求
> - `ActionContext` 可能被清理两次，导致空指针异常
> - Struts2 2.5.x 版本中，`StrutsPrepareAndExecuteFilter` 内部已包含 Prepare 和 Execute 逻辑，与三段式配置功能重叠
>
> 建议移除 `struts2`（StrutsPrepareAndExecuteFilter）过滤器，仅保留三段式配置以支持 SiteMesh 集成。

```
HTTP 请求
    │
    ▼
┌──────────────────────────┐
│  1. CharacterEncoding    │  ← 统一 UTF-8 编码
│     Filter               │
└──────────┬───────────────┘
           ▼
┌──────────────────────────┐
│  2. SingleSignOut        │  ← CAS 单点登出
│     Filter               │
└──────────┬───────────────┘
           ▼
┌──────────────────────────┐
│  3. CAS Authentication   │  ← CAS 认证（未登录重定向）
│     Filter               │
└──────────┬───────────────┘
           ▼
┌──────────────────────────┐
│  4. CAS Validation       │  ← CAS Ticket 校验
│     Filter               │
└──────────┬───────────────┘
           ▼
┌──────────────────────────┐
│  5. HttpServletRequest   │  ← 请求包装（getRemoteUser）
│     Wrapper Filter       │
└──────────┬───────────────┘
           ▼
┌──────────────────────────┐
│  6. AssertionThreadLocal │  ← 线程本地变量
│     Filter               │
└──────────┬───────────────┘
           ▼
┌──────────────────────────┐
│  7. UserCheck            │  ← 用户登录检查（*.action）
│     Filter               │
└──────────┬───────────────┘
           ▼
┌──────────────────────────┐
│  8. StrutsPrepare        │  ← Struts2 准备阶段
│     Filter               │
└──────────┬───────────────┘
           ▼
┌──────────────────────────┐
│  9. SiteMesh Page        │  ← 页面装饰
│     Filter               │
└──────────┬───────────────┘
           ▼
┌──────────────────────────┐
│ 10. StrutsExecute        │  ← Struts2 执行阶段
│     Filter               │
└──────────┬───────────────┘
           ▼
┌──────────────────────────┐
│ 11. StrutsPrepareAnd     │  ← Struts2 主过滤器（兜底）
│     ExecuteFilter        │
└──────────┬───────────────┘
           ▼
┌──────────────────────────┐
│ 12. ResponseOverride     │  ← DisplayTag 报表导出
│     Filter               │
└──────────┬───────────────┘
           │
           ▼
      HTTP 响应
```

---

## 2. Servlet 配置

### 2.1 VelocityViewServlet

**作用**：处理 Velocity 模板引擎的 `.vm` 模板文件请求。

| 属性 | 值 |
|:---|:---|
| Servlet 名称 | velocity |
| Servlet 类 | `org.apache.velocity.tools.view.servlet.VelocityViewServlet` |
| URL 匹配模式 | `*.vm` |

**配置示例**：

```xml
<servlet>
    <servlet-name>velocity</servlet-name>
    <servlet-class>org.apache.velocity.tools.view.servlet.VelocityViewServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>velocity</servlet-name>
    <url-pattern>*.vm</url-pattern>
</servlet-mapping>
```

### 2.2 DisplayChart

**作用**：JFreeChart 图表显示 Servlet，用于在页面中动态渲染和展示图表。

| 属性 | 值 |
|:---|:---|
| Servlet 名称 | showChart |
| Servlet 类 | `org.jfree.chart.servlet.DisplayChart` |
| URL 匹配模式 | `/servlet/showChart` |

**配置示例**：

```xml
<servlet>
    <servlet-name>showChart</servlet-name>
    <servlet-class>org.jfree.chart.servlet.DisplayChart</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>showChart</servlet-name>
    <url-pattern>/servlet/showChart</url-pattern>
</servlet-mapping>
```

---

## 3. Listener 配置

### 3.1 Listener 列表

| 序号 | Listener 类 | 功能说明 |
|:---:|:---|:---|
| 1 | `com.dp.plat.init.InitLicenser` | 系统许可证初始化，在应用启动时进行授权校验 |
| 2 | `org.springframework.web.context.request.RequestContextListener` | Spring 请求上下文监听器，将 HTTP 请求绑定到当前线程 |
| 3 | `org.springframework.web.context.ContextLoaderListener` | Spring 上下文加载监听器，启动时加载 Spring 配置文件 |
| 4 | `com.dp.plat.init.SpringInit` | PMS 自定义 Spring 初始化监听器，在 Spring 容器启动后执行自定义初始化逻辑 |
| 5 | `org.jasig.cas.client.session.SingleSignOutHttpSessionListener` | CAS 单点登出会话监听器，配合 SingleSignOutFilter 实现会话销毁同步 |

**配置示例**：

```xml
<listener>
    <listener-class>com.dp.plat.init.InitLicenser</listener-class>
</listener>
<listener>
    <listener-class>org.springframework.web.context.request.RequestContextListener</listener-class>
</listener>
<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
<listener>
    <listener-class>com.dp.plat.init.SpringInit</listener-class>
</listener>
<listener>
    <listener-class>org.jasig.cas.client.session.SingleSignOutHttpSessionListener</listener-class>
</listener>
```

### 3.2 Listener 启动顺序说明

1. **InitLicenser** — 最先执行，验证系统授权，未授权则阻止启动
2. **RequestContextListener** — 建立 Spring 请求上下文支持
3. **ContextLoaderListener** — 加载 Spring 根应用上下文，读取 `contextConfigLocation` 指定的配置文件
4. **SpringInit** — Spring 容器就绪后执行自定义初始化
5. **SingleSignOutHttpSessionListener** — 注册 CAS 单点登出的会话销毁回调

> **注意**：在 `web.xml` 实际配置中，`SingleSignOutHttpSessionListener` 的声明位于 CAS Filter 之前（而非与其他 Listener 在同一个 `<listener>` 块中）。虽然 Servlet 容器会按声明顺序初始化所有 Listener，功能上不受影响，但文档中的顺序列表与实际文件布局不完全对应。

### 3.3 Spring 上下文配置文件

通过 `context-param` 指定 Spring 配置文件路径：

```xml
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>
        /WEB-INF/classes/applicationContext.xml
        ,/WEB-INF/classes/beans-quartz.xml
    </param-value>
</context-param>
```

| 配置文件 | 说明 |
|:---|:---|
| applicationContext.xml | Spring 主配置文件，定义 Bean、数据源、事务等 |
| beans-quartz.xml | Quartz 定时任务配置文件 |

---

## 4. 其他配置

### 4.1 会话超时

```xml
<session-config>
    <session-timeout>120</session-timeout>
</session-config>
```

- 会话超时时间：**120 分钟**（2小时）
- 超时后用户需重新登录

### 4.2 认证方式

```xml
<login-config>
    <auth-method>BASIC</auth-method>
</login-config>
```

- 认证方式：**BASIC**（HTTP 基本认证）
- 注意：实际认证由 CAS 过滤器链处理，此配置主要作为 Servlet 规范的声明

### 4.3 自定义标签库

```xml
<jsp-config>
    <taglib>
        <taglib-uri>/dp</taglib-uri>
        <taglib-location>/WEB-INF/dp.tld</taglib-location>
    </taglib>
</jsp-config>
```

| 属性 | 值 |
|:---|:---|
| 标签库 URI | `/dp` |
| 标签库描述文件 | `/WEB-INF/dp.tld` |

**JSP 页面引用方式**：

```jsp
<%@ taglib uri="/dp" prefix="dp" %>
```

### 4.4 欢迎页

```xml
<welcome-file-list>
    <welcome-file>index.jsp</welcome-file>
</welcome-file-list>
```

### 4.5 上传路径与文件大小限制

文件上传相关配置分布在 Struts2 配置中：

| 配置项 | 值 | 说明 |
|:---|:---|:---|
| struts.multipart.maxSize | 209715200（200MB） | Struts2 文件上传最大尺寸 |
| fileUpload 拦截器 maximumSize | 209715200（200MB） | 单文件上传最大尺寸 |
| 上传路径 | upload/pms | 文件上传存储目录 |

**Struts2 文件上传配置示例**：

```xml
<interceptor-ref name="fileUpload">
    <param name="maximumSize">209715200</param>
    <param name="allowedTypes"></param>
</interceptor-ref>
```

> **注意**：`allowedTypes` 为空表示不限制上传文件类型，实际生产环境建议配置允许的 MIME 类型白名单。

---

## 5. 配置注意事项

1. **过滤器顺序不可随意调整**：CAS 过滤器必须在 Struts2 过滤器之前，编码过滤器必须在最前面
2. **Struts2 双重过滤器配置**：当前同时配置了三段式（Prepare + SiteMesh + Execute）和合并式（PrepareAndExecute）过滤器，需确保不会产生冲突
3. **CAS 服务器地址**：配置中硬编码了 CAS 服务器地址（`cas2.dptech.com:8443`）和应用服务器地址（`10.162.0.141:8083`），部署到不同环境时需修改
4. **SiteMesh 与 Struts2 集成**：三段式过滤器配置是 SiteMesh 与 Struts2 集成的标准模式，不可简化为单个过滤器
5. **包名兼容性**：当前使用 `org.apache.struts2.dispatcher.ng.filter` 包名，升级到 Struts2 2.5.x 后需改为 `org.apache.struts2.dispatcher.filter`
6. **Struts2 DTD 版本**：当前 `struts.xml` 使用 Struts 2.0 DTD（`struts-2.0.dtd`），但实际 Struts2 版本为 2.5.30。2.5 DTD 声明已在配置文件中注释，升级后需启用 2.5 DTD 以支持 `global-allowed-methods` 等新特性
