# 安全防护文档

> 本文档基于 PMS 与 SPMS 项目实际源码，详细描述认证授权、Web 安全防护、数据加密及安全注意事项，所有内容均来自真实实现，并标注避坑指南与最佳实践。

---

## 目录

1. [认证与授权](#1-认证与授权)
2. [Web 安全防护](#2-web-安全防护)
3. [数据加密](#3-数据加密)
4. [安全注意事项](#4-安全注意事项)
5. [避坑指南与最佳实践](#5-避坑指南与最佳实践)

---

## 1. 认证与授权

### 1.1 Shiro 认证流程（PMS-springmvc）

PMS-springmvc 使用 Apache Shiro 1.8.0 实现认证与授权，配置于 `spring-shiro-cas.xml`：

**认证流程**：

```
用户登录请求（用户名 + 密码 + 验证码）
    │
    ▼
ShiroFilter 拦截
    │
    ▼
ShiroRealm.doGetAuthenticationInfo()
    │
    ├── 验证码校验（sys.login.check.captcha=1 时）
    │   └── 失败 → CaptchaException
    │
    ├── 查询用户（queryUserByName）
    │   └── 不存在 → UnknownAccountException
    │
    ├── 用户状态检查
    │   ├── status=2（锁定）→ DisabledAccountException
    │   └── status=0（禁用）→ DisabledAccountException
    │
    ├── 密码比对（MD5 + 盐值 + 1024 次迭代）
    │   └── 不匹配 → IncorrectCredentialsException
    │
    └── 返回 SimpleAuthenticationInfo
```

**Shiro 关键配置**：

| 配置项 | 值 | 说明 |
|--------|-----|------|
| SecurityManager | `DefaultWebSecurityManager` | 默认 Web 安全管理器 |
| Session Cookie | `dp.session.id` | 自定义会话 Cookie 名 |
| Session 超时 | 7200000ms（2 小时） | `globalSessionTimeout` |
| SessionDAO | `MemorySessionDAO` | 内存会话存储 |
| 密码算法 | MD5 | 哈希算法 |
| 迭代次数 | 1024 | 密码哈希迭代次数 |
| 认证策略 | `AtLeastOneSuccessfulStrategy` | 至少一个 Realm 成功 |
| 缓存管理器 | EhCacheManager | 权限缓存 |

**Realm 配置**：

```xml
<!-- 本地认证 Realm -->
<bean id="jdbcRealm" class="com.dp.plat.core.realms.ShiroRealm">
    <property name="credentialsMatcher">
        <bean class="org.apache.shiro.authc.credential.HashedCredentialsMatcher">
            <property name="hashAlgorithmName" value="MD5"></property>
            <property name="hashIterations" value="1024"></property>
        </bean>
    </property>
</bean>

<!-- CAS 单点登录 Realm -->
<bean id="casRealm" class="com.dp.plat.core.realms.CasRealm">
    <property name="casServerUrlPrefix" value="${shiro.cas.serverUrlPrefix}" />
    <property name="casService" value="${shiro.cas.service}" />
</bean>
```

### 1.2 CAS 单点登录

PMS-springmvc 支持 CAS（Central Authentication Service）单点登录，通过 `CasRealm` 和 `CasFilter` 实现：

**CAS 模式判断**：

```java
String casMode = SystemConfig.systemVariables.get("sys.cas");
if ("1".equals(casMode)) {
    // CAS 模式：跳过密码验证，使用 CAS 票据
    // 跳过密码过期检查
    // 跳过验证码校验
}
```

| 模式 | 认证方式 | 密码检查 | 验证码 | 适用场景 |
|------|----------|----------|--------|----------|
| 本地模式（sys.cas=0） | PMS 本地用户表 | MD5 + 盐值 + 1024 迭代 | 生产环境启用 | 内网独立部署 |
| CAS 模式（sys.cas=1） | CAS 统一认证 | 跳过 | 跳过 | 企业统一认证 |

**CAS 过滤器配置**：

```xml
<bean id="casFilter" class="com.dp.plat.core.filter.CasFilter">
    <property name="failureUrl" value="${shiro.failureUrl}" />
    <property name="successUrl" value="${shiro.successUrl}" />
</bean>

<bean id="casLogoutFilter" class="com.dp.plat.core.cas.CasLogoutFilter">
    <property name="sessionManager" ref="sessionManager"/>
</bean>
```

### 1.3 权限模型（RBAC）

PMS-springmvc 采用 RBAC（基于角色的访问控制）权限模型：

```
用户 (t_user)
  │ user_id
  ▼
用户-角色关联 (t_user_role)
  │ role_id, comp_id
  ▼
角色 (t_role)
  │ role_id
  ├── 角色-权限关联 (t_role_permission) → 权限 (t_permission)
  └── 角色-菜单关联 (t_role_menu) → 菜单 (t_menu)
```

**权限数据流**：

1. 用户登录后，ShiroRealm 查询用户角色（`queryUserRoleByNameAndCompId`）
2. 根据角色查询权限（`queryPermissionByUsernameAndCompId`）
3. 权限信息缓存于 EhCache（10 分钟过期）
4. 通过 `@RequiresPermissions`、`@RequiresRoles` 注解或代码检查控制访问

**权限表结构**：

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `t_user` | 用户表 | user_id, user_name, password, status, needChangePwd |
| `t_role` | 角色表 | role_id, role_name, home_page, priority |
| `t_permission` | 权限表 | permission_id, permission_name |
| `t_user_role` | 用户-角色关联 | user_id, role_id, comp_id |
| `t_role_permission` | 角色-权限关联 | role_id, permission_id |
| `t_role_menu` | 角色-菜单关联 | role_id, menu_id |
| `t_menu` | 菜单表 | id, pid, name, url, sort, status |

**用户状态**：

| status 值 | 含义 | 登录行为 |
|-----------|------|----------|
| 0 | 禁用 | DisabledAccountException |
| 1 | 有效 | 正常登录 |
| 2 | 锁定 | DisabledAccountException |

### 1.4 PMS-struts 权限模型

PMS-struts 采用不同的权限模型（`fnd_` 前缀表）：

```
用户 (fnd_user_info)
  │ roleIds 字段（; 分隔的角色 ID 列表）
  ▼
角色 (fnd_role)
  │
  ▼
菜单 (fnd_menus)
  │
  ▼
操作权限 (fnd_user_power)
```

**UserCheckFilter 登录检查**：

PMS-struts 通过 `UserCheckFilter` 拦截 `*.action` 请求，进行登录状态和权限验证：

1. 检查密码过期 → 重定向到密码修改页面
2. 检查登录状态 → 未登录重定向到登录页面
3. CAS 模式检查 → CAS 模式访问登录页重定向到 CAS
4. 权限验证（`checkHandlerMapping`）→ URL 匹配用户菜单

**URL 通配符转换规则**：

| 原始 URL | 转换后的通配符 | 说明 |
|----------|--------------|------|
| `/module/Project_list.action` | `/module/Project_*.action` | 下划线方法名通配 |
| `/module/Project!list.action` | `/module/Project!*.action` | 感叹号方法名通配 |
| `/module/ProjectManage.action` | `/module/Project*.action` | 去除 Manage/Action 后缀 |

### 1.5 密码加密策略

PMS-springmvc 使用 **MD5 + 盐值 + 1024 次迭代** 的密码加密方案：

**Shiro 配置**：

```xml
<bean class="org.apache.shiro.authc.credential.HashedCredentialsMatcher">
    <property name="hashAlgorithmName" value="MD5"></property>
    <property name="hashIterations" value="1024"></property>
</bean>
```

**PMS-struts 密码加密**（`PasswordUtil`）：

PMS-struts 采用 **SHA1 + MD5 加盐迭代** 的双重加密方案：

```java
public static String encryptPassword(String saltSource, String credentials) {
    return encryptMD5Password(
        encryptSHA1Password(credentials, saltSource, 1),  // SHA1 加盐 1 次
        saltSource,
        1024                                              // MD5 加盐 1024 次
    );
}
```

| 步骤 | 算法 | 盐值 | 迭代次数 | 说明 |
|------|------|------|---------|------|
| 1 | SHA1 | 用户名 | 1 | 先对密码做一次 SHA1 加盐哈希 |
| 2 | MD5 | 用户名 | 1024 | 再对 SHA1 结果做 1024 次 MD5 加盐迭代 |

**安全特性**：
- 以用户名作为盐值，不同用户即使密码相同，加密结果也不同
- 1024 次 MD5 迭代增加暴力破解成本
- 自动生成 8 位随机密码（含大小写字母、数字和特殊字符 `~!@#$%^&*_-#.`）

> **避坑指南**：PMS-springmvc 和 PMS-struts 使用不同的密码加密方案。PMS-springmvc 用 MD5+1024 迭代，PMS-struts 用 SHA1+MD5+1024 迭代。两套用户体系不互通。

---

## 2. Web 安全防护

### 2.1 CSRF 防护

#### 2.1.1 PMS-security 的 CSRF 防护（PMS-springmvc）

PMS-security 模块提供两种 CSRF 防护接入方式：

| 组件 | 类型 | 说明 |
|------|------|------|
| `CsrfFilter` | Servlet Filter | 校验失败转发至 `/404.jsp`，支持 `excludePattern` 豁免 |
| `CsrfInterceptor` | Spring MVC Interceptor | 仅对 POST/PUT/DELETE 校验，失败抛 `CsrfValidateFailedException` |

**CSRFTokenManager**：

```java
// Token 生成（UUID）
String token = UUID.randomUUID().toString();

// Session 存储
session.setAttribute("csrfToken", token);

// 从 Parameter/Header/Cookie 提取
String token = request.getParameter("csrfToken");
```

**使用方式**：

```jsp
<form action="xxx.action" method="post">
    <input type="hidden" name="csrfToken" value="${csrfToken}" />
    ...
</form>
```

```javascript
// AJAX 请求在 Header 中携带
$.ajaxSetup({
    headers: { "X-CSRF-Token": csrfToken }
});
```

#### 2.1.2 PMS-struts 的 CSRF 防护（已禁用）

PMS-struts 曾实现 CSRF 防护，但当前处于**禁用状态**：

```xml
<!-- applicationContext.xml 中已注释 -->
<!-- <bean id="CSRFTokenManager" class="com.dp.plat.security.csrf.CSRFTokenManager">
    <constructor-arg name="csrfTokenName" value="csrfToken"/>
</bean> -->
```

> **安全风险**：PMS-struts 的 CSRF 防护已禁用，建议在状态修改操作（INSERT/UPDATE/DELETE）的表单提交中重新启用。

### 2.2 XSS 防护

#### 2.2.1 XssStrutsInterceptor 三级 URL 策略（PMS-struts）

PMS-struts 通过 `XssStrutsInterceptor`（`com.dp.plat.security.xss.struts.XssStrutsInterceptor`）实现 XSS 防护：

```xml
<interceptor name="XssStrutsInterceptor"
    class="com.dp.plat.security.xss.struts.XssStrutsInterceptor">
    <param name="enable">true</param>
    <param name="excludeUrls">/base/executeSql.*</param>
    <param name="cleanUrls">/module/prob_*,/probAudit.*,/probAjax_*.*</param>
    <param name="encodeUrls">/*</param>
</interceptor>
```

**三级 URL 匹配策略**：

| 策略 | 参数 | URL Pattern | 处理方式 | 适用场景 |
|------|------|-------------|----------|----------|
| 排除 | `excludeUrls` | `/base/executeSql.*` | 不处理，直接放行 | SQL 执行功能 |
| 清理 | `cleanUrls` | `/module/prob_*`, `/probAudit.*` | HTML 清理（移除危险标签） | 富文本模块 |
| 编码 | `encodeUrls` | `/*` | HTML 实体编码（全量转义） | 所有其他请求 |

**处理优先级**：`excludeUrls` > `cleanUrls` > `encodeUrls`

**HTML 编码映射**：

| 原始字符 | 编码后 |
|---------|--------|
| `<` | `&lt;` |
| `>` | `&gt;` |
| `"` | `&quot;` |
| `'` | `&#39;` |
| `&` | `&amp;` |

> **避坑指南**：`XssStrutsInterceptor` 在 `basepackage` 和 `defaultJson` 两个抽象包中分别定义，修改时需同步更新。新增富文本模块时，必须将对应 URL 添加到 `cleanUrls`，否则 HTML 标签会被编码转义。

#### 2.2.2 XssFilter（PMS-security）

PMS-security 提供 Servlet Filter 级别的 XSS 防护，支持多种请求形态：

| 包装器类 | 说明 |
|----------|------|
| `XssHttpServletRequestWrapper` | 基础包装器，对 getHeader/getParameter 调用 `JsoupUtil.clean` |
| `XssRequestBodyHttpServletRequestWrapper` | POST Body 缓存 + `escapeHtml` 过滤，password 字段豁免 |
| `XssRequestBodyHttpServletRequestWrapper2` | 使用 `JSON.parseObject` 解析 Body |
| `XssRequestBodyHttpServletRequestWrapper3` | JSON Body 解析 + 过滤 |

**JsoupUtil 工具类**：

- HTML 清理（jsoup Safelist）：移除危险标签和事件属性
- HTML 转义/反转义
- XSS 编码（处理 `%3c`/`%3e` URL 编码）

### 2.3 SQL 注入防护

#### 2.3.1 参数化查询（主要防护手段）

PMS 系统的 SQL 注入防护主要依赖 iBATIS/MyBatis 的参数化查询：

| 符号 | iBATIS | MyBatis | 安全性 | 生成 SQL |
|------|--------|---------|--------|----------|
| 参数化 | `#var#` | `#{var}` | **安全** | `WHERE name = ?` |
| 拼接 | `$var$` | `${var}` | **不安全** | `WHERE name = 'value'` |

**安全用法**：

```xml
<!-- iBATIS：参数化绑定 -->
<select id="findProjectList" parameterClass="map" resultClass="hashmap">
    SELECT * FROM pm_project_header
    WHERE projectCode LIKE concat('%', #projectCode#, '%')
    AND effectiveTo IS NULL
</select>
```

#### 2.3.2 SQL 注入关键字过滤

PMS-springmvc 配置了 SQL 注入关键字过滤（`sys.sql.inject.filter`），但当前在 `DataOperationController` 中被注释：

```properties
# config.properties
sys.sql.inject.filter=;|--|#|\/\*|0x|@@?|@@version|...|(create|drop|show|delete|update|insert|alter|truncate)\W+...
```

> **注意**：该过滤去除了 `SET` 关键字，因为 `FIND_IN_SET` 会被误识别。当前 `DataOperationController` 中的过滤逻辑已注释，SQL 执行功能依赖参数化查询和权限控制。

#### 2.3.3 Druid SQL 解析器

PMS-security 模块提供 `SQLParser` 工具类（基于 Druid SQL 解析器），用于：
- 表名提取
- 正则匹配校验
- SQL 变量解析与填充

> **说明**：项目中未使用 Druid 的 `WallFilter`（SQL 防火墙），SQL 注入防护主要依赖参数化查询。如需增强防护，可考虑引入 Druid `WallFilter`。

#### 2.3.4 `$` 拼接的安全使用场景

`$`（字符串拼接）在以下场景不可避免，但必须确保参数来源可信：

**场景一：动态表名/列名**（需白名单校验）

```java
private static final Set<String> ALLOWED_TABLES = new HashSet<>(Arrays.asList(
    "pm_project_header", "pm_project_member", "fnd_user_info"
));

public List findByTable(String tableName, int id) {
    if (!ALLOWED_TABLES.contains(tableName)) {
        throw new IllegalArgumentException("Invalid table name: " + tableName);
    }
    // ...
}
```

**场景二：动态排序**（需枚举校验）

```java
private static final Set<String> ALLOWED_SORT_FIELDS = new HashSet<>(Arrays.asList(
    "projectCode", "projectName", "createTime"
));
private static final Set<String> ALLOWED_SORT_ORDERS = new HashSet<>(Arrays.asList("ASC", "DESC"));
```

### 2.4 Shiro 过滤器链

PMS-springmvc 通过 `FilterChainDefinitionMapBuilder` 动态构建 Shiro 过滤器链：

```xml
<bean id="shiroFilter" class="org.apache.shiro.spring.web.ShiroFilterFactoryBean">
    <property name="securityManager" ref="securityManager"/>
    <property name="loginUrl" value="${shiro.loginUrl}"/>
    <property name="unauthorizedUrl" value="/unauthorized.html"/>
    <property name="filterChainDefinitionMap" ref="filterChainDefinitionMap"/>
    <property name="filters">
        <map>
            <entry key="logout" value-ref="logoutFilter"/>
            <entry key="casFilter" value-ref="casFilter"/>
            <entry key="casLogoutFilter" value-ref="casLogoutFilter"/>
            <entry key="anyRoles" value-ref="anyRoles"/>
            <entry key="hostFilter" value-ref="hostFilter"/>
        </map>
    </property>
</bean>
```

**过滤器说明**：

| 过滤器 | 类 | 说明 |
|--------|----|------|
| `anon` | Shiro 内置 | 匿名访问 |
| `authc` | Shiro 内置 | 需认证 |
| `logout` | `LogoutFilter` | 登出 |
| `casFilter` | `com.dp.plat.core.filter.CasFilter` | CAS 票据校验 |
| `casLogoutFilter` | `com.dp.plat.core.cas.CasLogoutFilter` | CAS 单点登出 |
| `anyRoles` | `com.dp.plat.core.filter.AnyRolesAuthorizationFilter` | 任一角色 |
| `hostFilter` | `com.dp.plat.core.filter.HostFilter` | IP 地址过滤 |

---

## 3. 数据加密

### 3.1 ASEUtil（AES 加密）

PMS-security 模块提供 `ASEUtil`（`com.dp.plat.security.util.ASEUtil`）对称加密工具：

| 配置项 | 值 |
|--------|-----|
| 算法 | AES |
| 模式 | ECB |
| 填充 | PKCS5Padding |
| 密钥长度 | 128 位 |
| 密钥生成 | `KeyGenerator` + `SHA1PRNG` 随机数 |
| 默认密码 | `DP_SECRET` |
| 输出编码 | Base64 |

**核心方法**：

```java
// 加密（返回 Base64 字符串）
public static String encrypt(String content, String password)

// 解密（输入 Base64 字符串）
public static String decrypt(String content, String password)
```

> **安全提示**：ECB 模式不安全，相同明文产生相同密文。`SHA1PRNG` 在不同 JDK 实现间可能不兼容。默认密码 `DP_SECRET` 硬编码在源码中，生产环境应使用自定义密码。

### 3.2 密码加密对比

| 项目 | 工具类 | 算法 | 盐值 | 迭代次数 | 安全性 |
|------|--------|------|------|---------|--------|
| PMS-springmvc | Shiro HashedCredentialsMatcher | MD5 | - | 1024 | 中 |
| PMS-struts | `PasswordUtil` | SHA1 + MD5 | 用户名 | 1 + 1024 | 中高 |
| PMS-struts（旧版） | `Md5Util` | MD5 | 无 | 1 | 低 |
| SPMS | `MD5Util` | MD5 | 无 | 1 | 低 |

> **避坑指南**：SPMS 使用无盐值单次 MD5，安全性最低。PMS-struts 存在新旧两种加密方式，旧版 `Md5Util` 仅用于兼容旧数据，新功能必须使用 `PasswordUtil`。

### 3.3 敏感字段加密现状

| 字段类型 | 加密方式 | 存储方式 | 安全建议 |
|----------|----------|----------|----------|
| 用户密码 | MD5/SHA1+MD5 加盐迭代 | 32 位十六进制 | 不可逆，仅比对 |
| 邮件内容 | 无加密 | 明文存储 | - |
| 手机号码 | 无加密 | 明文存储 | 建议脱敏展示 |
| customInfo JSON | 无加密 | JSON 明文 | - |
| 数据库密码 | 无加密 | `jdbc.properties` 明文 | 应使用加密配置 |

### 3.4 传输加密

| 配置项 | 说明 | 当前状态 |
|--------|------|----------|
| HTTPS | SSL/TLS 加密传输 | 生产环境必须启用 |
| HSTS | 严格传输安全头 | 建议启用 |
| Cookie 安全 | `HttpOnly` + `Secure` | 建议启用 |

---

## 4. 安全注意事项

### 4.1 SPMS AuthCheckInterceptor 当前已注释（安全风险）

SPMS 的 `AuthCheckInterceptor`（`com.dp.plat.interceptor.AuthCheckInterceptor`）权限校验逻辑**已被注释，直接放行所有请求**：

**当前状态**：

```java
// AuthCheckInterceptor - 权限校验逻辑已注释，直接放行
public String intercept(ActionInvocation invocation) throws Exception {
    // 权限校验逻辑已被注释
    return invocation.invoke();  // 直接放行
}
```

**拦截器栈配置**：

```
baseStack = fileUpload → defaultStack → authCheck → pwdCheck
```

| 拦截器 | 作用 | 当前状态 |
|--------|------|----------|
| `authCheck` | 权限校验 | ⚠️ **逻辑已注释，直接放行** |
| `pwdCheck` | 密码过期校验 | 正常（排除 `execute1`、`editlogin` 方法） |

**安全影响**：
- SPMS 的 Struts2 Action 层无权限拦截
- 仅靠 `UserCheckFilter`（Servlet Filter）进行登录状态检查
- 管理员页面（UserManage/Serve/Tain/Back）仅通过 `role==4` 判断

> **安全风险（高）**：`AuthCheckInterceptor` 失效意味着 SPMS 的细粒度权限控制缺失。建议恢复或重新实现权限校验逻辑。

### 4.2 Struts2 DMI 动态方法调用风险

SPMS 启用了 Struts2 动态方法调用（DMI）：

```xml
<constant name="struts.enable.DynamicMethodInvocation" value="true" />
```

**DMI 风险**：
- URL 支持 `actionName!methodName.action` 形式
- 可调用 Action 的任意 public 方法
- Struts2 2.3.35 存在已知漏洞（如 S2-057）

**示例**：

```
/sys/RMA!toAudit.action?id=123        → RmaApplicantAction.toAudit()
/sys/hexiao!hexiao.action             → SparePartsChangeAction.hexiao()
```

> **安全风险（高）**：DMI 存在安全风险，可调用任意 public 方法。建议限制可调用方法（`global-allowed-methods` 白名单）或关闭 DMI。

**PMS-struts 的 DMI 配置**：

PMS-struts 使用通配符映射替代 DMI：

```xml
<action name="Project_*" class="projectAction" method="{1}">
    <result name="input">/sys/module/project_{1}.jsp</result>
</action>
```

> **避坑指南**：PMS-struts 使用 2.3.35，PMS-springmvc 使用 2.5.30，版本不同。Struts2 2.3.35 存在已知安全漏洞，建议升级至 2.5.x 最新版（需评估兼容性）。

### 4.3 敏感信息配置

#### 4.3.1 jdbc.properties 不应提交真实凭据

> **安全红线**：配置文件通过 Maven Profile 资源过滤管理，**不应提交真实凭据**。

**PMS 配置文件**：

| 文件 | 位置 | 说明 |
|------|------|------|
| `jdbc.properties` | `core/src/main/resources/` | 主数据源配置 |
| `jdbc_dev.properties` | `PMS-struts/config/` | 开发环境 |
| `jdbc_release.properties` | `PMS-struts/config/` | 生产环境 |
| `profiles/<env>/config.properties` | `PMS-springmvc/src/main/resources/` | 多环境配置 |

**SPMS 配置文件**：

| 文件 | 位置 | 说明 |
|------|------|------|
| `jdbc.properties` | `config/` | 含 5 个数据源连接信息（明文密码） |

> **安全风险**：SPMS 的 `jdbc.properties` 含多个数据源的明文密码，SVN 提交时需注意脱敏。建议使用加密配置或环境变量注入。

#### 4.3.2 FP API 配置

pms-ext-fp 的 API 配置存储于系统变量 `sys.fp.api.config`（JSON 字符串），包含 FP 平台的认证信息：

```json
{
  "serviceUrl": "http://fp.dptech.com",
  "tokenUrl": "/m/oauth.json",
  "openId": "yfPurchase",
  "authType": "header",
  "authKey": "__RequestVerificationToken"
}
```

> **安全建议**：FP API 配置含认证信息，应通过管理后台维护，不应硬编码在代码中。

### 4.4 SPMS 安全组件现状

| 组件 | 类型 | 作用 | 当前状态 |
|------|------|------|----------|
| `UserCheckFilter` | Servlet Filter | 拦截 `*.action`，校验登录状态 | ✅ 正常 |
| `AuthCheckInterceptor` | Struts2 Interceptor | 权限校验 | ⚠️ **逻辑已注释** |
| `PwdInterceptor` | Struts2 Interceptor | 密码过期校验 | ✅ 正常 |
| `InitLicenser` | ServletContextListener | License 初始化校验 | ✅ 正常 |

**SPMS 角色体系**：

| Role 值 | 角色 | 权限 |
|---------|------|------|
| 4 | 管理员 | 可访问 UserManage/Serve/Tain/Back 等管理页面 |
| 其他 | 普通用户 | 仅可访问业务页面 |

> **注意**：SPMS 未使用 Shiro/CAS 等安全框架（与 PMS 不同），认证逻辑完全自实现，密码使用无盐值 MD5。

### 4.5 Shiro invalidRequestFilter 配置

PMS-springmvc 配置了 Shiro 1.6+ 的 `invalidRequestFilter`，但已禁用：

```xml
<bean id="invalidRequest" class="org.apache.shiro.web.filter.InvalidRequestFilter">
    <property name="enabled" value="false" />
    <property name="blockBackslash" value="false" />
    <property name="blockSemicolon" value="false" />
    <property name="blockNonAscii" value="false" />
</bean>
```

> **安全提示**：`invalidRequestFilter` 禁用后，URL 中的 `\`、`;`、非 ASCII 字符不会被拦截。如果 URL rewriting 不需要 jsessionid，建议启用 `blockSemicolon=true`。

---

## 5. 避坑指南与最佳实践

### 5.1 避坑指南

| 安全陷阱 | 风险等级 | 影响 | 解决方案 |
|----------|----------|------|----------|
| SPMS AuthCheckInterceptor 已注释 | 高 | 权限校验缺失 | 恢复或重新实现权限校验 |
| SPMS DMI 动态方法调用 | 高 | 可调用任意 public 方法 | 限制 `global-allowed-methods` 或关闭 DMI |
| SPMS 无盐值 MD5 密码 | 高 | 彩虹表破解风险 | 迁移至加盐迭代加密 |
| PMS-struts CSRF 防护已禁用 | 中 | CSRF 攻击风险 | 重新启用 CSRF Token 机制 |
| jdbc.properties 明文密码 | 中 | 凭据泄露 | 使用加密配置或环境变量 |
| Struts2 2.3.35 已知漏洞 | 中 | S2-057 等漏洞 | 升级至 2.5.x |
| ASEUtil ECB 模式 | 中 | 相同明文相同密文 | 使用 CBC/GCM 模式 |
| ASEUtil 默认密码硬编码 | 中 | 密钥泄露 | 使用自定义密码 |
| PermissionTag 权限检查已注释 | 中 | 页面级权限失效 | 恢复权限检查逻辑 |
| SQL 注入过滤已注释 | 低 | SQL 执行功能风险 | 依赖参数化查询 + 权限控制 |

### 5.2 最佳实践

1. **认证授权**：生产环境启用 CAS 单点登录；密码使用加盐迭代加密；权限修改后清除 Shiro 缓存
2. **Web 安全**：所有状态修改操作启用 CSRF Token；富文本模块配置 `cleanUrls`；SQL 使用参数化查询（`#`）
3. **数据加密**：敏感字段（手机号、身份证）加密存储或脱敏展示；数据库密码使用加密配置；生产环境启用 HTTPS
4. **SPMS 安全加固**：恢复 `AuthCheckInterceptor` 权限校验；关闭 DMI 或限制方法白名单；密码加密升级为加盐迭代
5. **依赖安全**：Struts2 升级至 2.5.x 最新版；Fastjson 使用安全版本（1.2.83）；定期扫描依赖漏洞
6. **配置安全**：不提交真实凭据；SPMS 的 `jdbc.properties` SVN 提交时脱敏；FP API 配置通过管理后台维护
7. **日志安全**：操作日志记录关键操作；不记录敏感信息（密码、Token）；定期审计 `fnd_operate_log`
