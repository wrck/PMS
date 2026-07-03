# PMS-security 数据流图

## 1. 安全过滤器链数据流

### 1.1 请求处理流程

```mermaid
sequenceDiagram
    participant C as 浏览器
    participant F1 as CsrfFilter
    participant F2 as XssFilter
    participant S as ShiroFilter
    participant A as Action/Controller
    participant DB as Database

    C->>F1: HTTP 请求
    F1->>F1: 检查 CSRF Token
    alt Token 无效
        F1-->>C: forward /404.jsp
    else Token 有效
        F1->>F2: 继续过滤链
    end
    F2->>F2: XSS 清理参数
    Note over F2,S: SQL 注入防护由 SQLParser 表名白名单<br/>+ MyBatis #{} 参数化实现，无独立 Filter
    F2->>S: Shiro 认证授权
    S->>A: 转发到业务处理
    A->>DB: 数据库操作
    DB-->>A: 返回结果
    A-->>C: HTTP 响应
```

### 1.2 CSRF 验证数据流

```mermaid
graph LR
    subgraph 首次请求
        A1[浏览器] -->|GET 请求| B1[CsrfFilter]
        B1 -->|生成 Token| C1[CSRFTokenManager]
        C1 -->|存入 Session| D1[HttpSession]
        C1 -->|写入 Cookie| A1
    end

    subgraph 后续请求
        A2[浏览器] -->|POST + Token| B2[CsrfFilter]
        B2 -->|提取 Token| C2[请求参数/Header/Cookie]
        B2 -->|获取 Session Token| D2[HttpSession]
        B2 -->|比较 Token| E2{Token 匹配?}
        E2 -->|是| F2[继续过滤链]
        E2 -->|否| G2[forward /404.jsp]
    end
```

### 1.3 XSS 清理数据流

```mermaid
graph TB
    A[HTTP 请求参数] --> B[XssFilter.doFilter]
    B --> C[包装 HttpServletRequest]
    C --> D[XssRequestBodyHttpServletRequestWrapper]
    D --> E{获取参数}
    E -->|getParameter| F[JsoupUtil.clean]
    E -->|getParameterValues| F
    E -->|getHeader| F
    F --> G[Jsoup Safelist.relaxed]
    G --> H[清理后的参数]
    H --> I[传递给下游]
```

### 1.4 密码加密数据流

```mermaid
sequenceDiagram
    participant U as 用户
    participant A as Action
    participant PI as PasswordInterceptor
    participant ASE as ASEUtil
    participant DB as Database

    U->>A: 提交密码（明文）
    A->>PI: Spring MVC HandlerInterceptor
    PI->>PI: 检测 password 字段
    alt 是 password 字段
        PI->>ASE: ASEUtil.encrypt(content, password)
        ASE-->>PI: 返回密文
        PI->>DB: INSERT/UPDATE 密文
    else 非 password 字段
        PI->>DB: 正常执行
    end
    DB-->>A: 操作结果
```

## 2. 过滤器配置

### 2.1 web.xml 配置（PMS-struts Profile）

```xml
<!-- CSRF 过滤器 -->
<filter>
    <filter-name>CsrfFilter</filter-name>
    <filter-class>com.dp.plat.security.csrf.CsrfFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>CsrfFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>

<!-- XSS 过滤器（部分 Profile 注释启用） -->
<filter>
    <filter-name>XssFilter</filter-name>
    <filter-class>com.dp.plat.security.xss.XssFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>XssFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

### 2.2 Shiro 过滤器链

```mermaid
graph LR
    A[Shiro Filter Chain] --> B[anon: 登录页/CSS/JS]
    A --> C[authc: 需认证页面]
    A --> D[cas: CAS 单点登录]
    A --> E[perms: 权限检查]
    A --> F[roles: 角色检查]
```

## 3. 安全组件与数据库交互

> ⚠️ **重要说明**：PMS-security 是纯工具库（jar），**无任何数据库表、Mapper、DAO、Service**（见 [no-database.md](../03-database/no-database.md)）。以下 §3.1/§3.2 描述的是 **core 模块**（Shiro 集成）的认证/权限流程，PMS-security 仅提供 `ASEUtil`、`CSRFTokenManager` 等工具类供 core 模块调用。`t_user`、`t_role`、`UserService`、`Realm` 均在 core 模块。

### 3.1 用户认证数据流

```mermaid
sequenceDiagram
    participant U as 用户
    participant S as Shiro
    participant R as Realm
    participant US as UserService
    participant DB as t_user

    U->>S: 提交凭证
    S->>R: doGetAuthenticationInfo
    R->>US: 查询用户
    US->>DB: SELECT * FROM t_user WHERE username=?
    DB-->>US: 用户记录
    US-->>R: UserInfo
    R->>R: 密码比对（ASEUtil.decrypt）
    R-->>S: AuthenticationInfo
    S-->>U: 认证结果
```

### 3.2 权限验证数据流

```mermaid
graph TB
    A[用户请求] --> B[Shiro 权限检查]
    B --> C{是否已认证?}
    C -->|否| D[重定向登录页]
    C -->|是| E{是否有权限?}
    E -->|否| F[403 禁止访问]
    E -->|是| G[访问业务逻辑]

    subgraph 权限查询
        E --> H[t_role]
        E --> I[t_permission]
        E --> J[t_role_permission]
        E --> K[t_user_role]
    end
```

## 4. 外部系统交互

PMS-security 不与外部系统直接交互，所有安全验证在本地完成。
