# 安全防护措施文档

本文档详细描述 PMS 系统的安全防护体系，包括 XSS 防护、CSRF 防护、SQL 注入防护、密码加密、权限控制和安全审计。

---

## 1. XSS 防护实现

### 1.1 XssStrutsInterceptor 三级 URL 策略

PMS 系统通过 `XssStrutsInterceptor`（`com.dp.plat.security.xss.struts.XssStrutsInterceptor`）实现 XSS 防护，该拦截器作为 `baseStack` 的第一个拦截器执行，对所有请求参数进行过滤。

**配置**：

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
|------|------|-------------|---------|---------|
| 排除策略 | `excludeUrls` | `/base/executeSql.*` | 不处理，直接放行 | SQL 执行功能，需要保留原始输入 |
| 清理策略 | `cleanUrls` | `/module/prob_*`, `/probAudit.*`, `/probAjax_*.*` | HTML 清理（移除危险标签，保留安全 HTML） | 技术公告等富文本模块 |
| 编码策略 | `encodeUrls` | `/*` | HTML 实体编码（全量转义） | 所有其他请求 |

**处理优先级**：`excludeUrls` > `cleanUrls` > `encodeUrls`

```
请求进入 XssStrutsInterceptor
        │
        ▼
  URL 匹配 excludeUrls？ ── 是 ──▶ 不处理，直接放行
        │ 否
        ▼
  URL 匹配 cleanUrls？ ── 是 ──▶ HTML 清理模式
        │ 否                     （移除危险标签，
        ▼                        保留安全 HTML）
  URL 匹配 encodeUrls？ ── 是 ──▶ HTML 编码模式
                                 （全量转义特殊字符）
```

### 1.2 HTML Clean 配置

**cleanUrls** 模式使用 HTML Cleaner 对请求参数进行清理：

- 移除危险标签：`<script>`、`<iframe>`、`<object>`、`<embed>`、`<applet>` 等
- 移除危险属性：`onerror`、`onclick`、`onload`、`onmouseover` 等事件处理器
- 移除 `javascript:` 协议链接
- 保留安全的 HTML 格式标签：`<p>`、`<b>`、`<i>`、`<table>`、`<img>` 等

**适用模块**：

- 技术公告（`/module/prob_*`）：需要保留 HTML 格式展示
- 技术公告审核（`/probAudit.*`）：审核内容需要富文本
- 技术公告 AJAX（`/probAjax_*.*`）：异步提交的富文本内容

### 1.3 URL 编码策略

**encodeUrls** 模式对所有请求参数值进行 HTML 实体编码：

| 原始字符 | 编码后 | 说明 |
|---------|--------|------|
| `<` | `&lt;` | 小于号 |
| `>` | `&gt;` | 大于号 |
| `"` | `&quot;` | 双引号 |
| `'` | `&#39;` | 单引号 |
| `&` | `&amp;` | & 符号 |

**配置注意事项**：

1. `XssStrutsInterceptor` 在 `basepackage` 和 `defaultJson` 两个抽象包中分别定义，参数配置一致，修改时需同步更新
2. 新增富文本模块时，需将对应 URL 添加到 `cleanUrls`，否则 HTML 标签会被编码转义
3. 排除 URL 仅用于特殊功能（如 SQL 执行），不应随意添加

---

## 2. CSRF 防护

### 2.1 CSRFTokenManager 实现（已禁用）

PMS 系统曾实现 CSRF（跨站请求伪造）防护，但当前处于**禁用状态**。

**原始配置**（`applicationContext.xml`，已注释）：

```xml
<!-- <bean id="CSRFTokenManager" class="com.dp.plat.security.csrf.CSRFTokenManager" >
    <constructor-arg name="csrfTokenName" value="csrfToken"/>
</bean> -->
```

**设计意图**：

| 配置项 | 值 | 说明 |
|--------|-----|------|
| Bean ID | `CSRFTokenManager` | CSRF 令牌管理器 |
| 类名 | `com.dp.plat.security.csrf.CSRFTokenManager` | 自定义 CSRF 防护实现 |
| 令牌参数名 | `csrfToken` | 表单中 CSRF 令牌的参数名 |

### 2.2 启用建议

当前 CSRF 防护已禁用，建议在以下场景中重新启用：

**高优先级场景**：

1. 所有状态修改操作（INSERT/UPDATE/DELETE）的表单提交
2. 关键业务操作（审批、权限变更、密码修改等）
3. 管理员操作接口

**启用步骤**：

1. 取消 `applicationContext.xml` 中 `CSRFTokenManager` 的注释
2. 在所有表单中添加 CSRF 令牌隐藏字段：

```jsp
<form action="xxx.action" method="post">
    <input type="hidden" name="csrfToken" value="${csrfToken}" />
    ...
</form>
```

3. 在 Struts2 拦截器栈中添加 CSRF 校验拦截器
4. AJAX 请求在 Header 中携带 CSRF 令牌：

```javascript
$.ajaxSetup({
    headers: { "X-CSRF-Token": csrfToken }
});
```

---

## 3. SQL 注入防护

### 3.1 iBatis 参数化查询（#）vs 字符串拼接（$）

iBatis 提供两种参数引用方式，安全性差异巨大：

| 符号 | 行为 | 安全性 | 生成的 SQL |
|------|------|--------|-----------|
| `#property#` | 参数化绑定（PreparedStatement `?`） | **安全** | `WHERE name = ?` |
| `$property$` | 字符串直接拼接（Text Substitution） | **不安全** | `WHERE name = 'value'` |

**安全用法（#）**：

```xml
<select id="findProjectList" parameterClass="map" resultClass="hashmap">
    SELECT * FROM pm_project_header
    WHERE projectCode LIKE concat('%', #projectCode#, '%')
    AND officeCode = #officeCode#
    AND effectiveTo IS NULL
</select>
```

生成的 SQL：

```sql
SELECT * FROM pm_project_header
WHERE projectCode LIKE concat('%', ?, '%')
AND officeCode = ?
AND effectiveTo IS NULL
```

**不安全用法（$）**：

```xml
<select id="findProjectList" parameterClass="map" resultClass="hashmap">
    SELECT * FROM pm_project_header
    WHERE projectCode LIKE '%$projectCode$%'
    AND officeCode = '$officeCode$'
</select>
```

如果 `officeCode` 传入 `' OR '1'='1`，生成的 SQL：

```sql
SELECT * FROM pm_project_header
WHERE projectCode LIKE '%%'
AND officeCode = '' OR '1'='1'
```

### 3.2 安全使用 $ 的场景

`$` 在以下场景中不可避免，但必须确保参数来源可信：

**场景一：动态表名/列名**

```xml
<select id="findDataByTable" parameterClass="map" resultClass="hashmap">
    SELECT * FROM $tableName$ WHERE id = #id#
</select>
```

**安全措施**：在 Java 代码中对 `tableName` 进行白名单校验：

```java
private static final Set<String> ALLOWED_TABLES = Set.of(
    "pm_project_header", "pm_project_member", "fnd_user_info"
);

public List findByTable(String tableName, int id) {
    if (!ALLOWED_TABLES.contains(tableName)) {
        throw new IllegalArgumentException("Invalid table name: " + tableName);
    }
    Map<String, Object> param = new HashMap<>();
    param.put("tableName", tableName);
    param.put("id", id);
    return getSqlMapClientTemplate().queryForList("findDataByTable", param);
}
```

**场景二：动态排序**

```xml
<isNotEmpty prepend="ORDER BY" property="sortField">
    $sortField$ $sortOrder$
</isNotEmpty>
```

**安全措施**：对 `sortField` 和 `sortOrder` 进行枚举校验：

```java
private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
    "projectCode", "projectName", "createTime", "updateTime"
);
private static final Set<String> ALLOWED_SORT_ORDERS = Set.of("ASC", "DESC");

public void validateSort(String sortField, String sortOrder) {
    if (!ALLOWED_SORT_FIELDS.contains(sortField)) {
        throw new IllegalArgumentException("Invalid sort field: " + sortField);
    }
    if (!ALLOWED_SORT_ORDERS.contains(sortOrder.toUpperCase())) {
        throw new IllegalArgumentException("Invalid sort order: " + sortOrder);
    }
}
```

**场景三：customInfo JSON 属性查询**

```xml
<iterate property="customInfo.iterator">
    <isNotEmpty prepend="AND" property="customInfo.iterator[].value">
        #customInfo.iterator[].value# = spc.customInfo ->> '$$.$customInfo.iterator[].key$'
    </isNotEmpty>
</iterate>
```

此处 `$customInfo.iterator[].key$` 用于 JSON 路径表达式，key 值应为系统内部定义的属性名，不接受用户直接输入。

---

## 4. 密码加密策略

### 4.1 MD5 加密（旧版）

**工具类**：`com.dp.plat.util.Md5Util`

```java
public static String getMD5(byte[] source) {
    java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
    md.update(source);
    byte tmp[] = md.digest();
    // 转换为 32 位十六进制字符串
    ...
    return s;
}
```

**特点**：

- 使用 `java.security.MessageDigest` 实现 MD5 摘要
- 输出 32 位小写十六进制字符串
- **无盐值**，单次哈希
- 安全性较低，容易被彩虹表破解

### 4.2 SHA1 + MD5 加盐迭代加密（新版）

**工具类**：`com.dp.plat.util.PasswordUtil`

```java
public static String encryptPassword(String saltSource, String credentials) {
    return encryptMD5Password(
        encryptSHA1Password(credentials, saltSource, 1),
        saltSource,
        1024
    );
}
```

**加密流程**：

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

**密码生成**：

```java
public static String generatePass() {
    return createRandomPassword(8);
}
```

- 自动生成 8 位随机密码，含大小写字母、数字和特殊字符~!@#$%^&*_-#.
- 以用户名作为盐值，不同用户即使密码相同，加密结果也不同

**验证密码**：

```java
String encrypted = PasswordUtil.encryptPassword(username, inputPassword);
if (encrypted.equals(user.getPassword())) {
    // 密码正确
}
```

### 4.3 密码过期机制

PMS 系统通过 `User` 实体的 `pwdoverdue` 字段实现密码过期检查：

```java
User user = userContext.getUser();
Date currentDate = new Date();
Date pwdoverdue = user.getPwdoverdue();
pwdoverdue = pwdoverdue != null ? pwdoverdue : currentDate;
needChangePwd = !currentDate.before(pwdoverdue);
```

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

---

## 5. 权限控制机制

### 5.1 User → RoleIds → Role → Menu → Power 四级体系

PMS 系统采用四级权限控制模型：

```
用户 (fnd_user_info)
  │ roleIds 字段（;分隔的角色ID列表）
  │
  ▼
角色 (fnd_role)
  │ role_id
  │
  ▼
菜单 (fnd_menus)
  │ menu_id, menu_url, parent_id
  │
  ▼
操作权限 (fnd_user_power)
  user_id, power_id
```

**权限数据流**：

1. 用户登录后，根据 `roleIds` 加载角色列表
2. 根据角色加载可访问的菜单列表（`UserContext.menuList`）
3. 根据菜单加载操作权限列表（`UserContext.powerList`）
4. 区域权限通过 `areapower` 字段控制数据范围

### 5.2 UserCheckFilter 登录检查

`UserCheckFilter`（`com.dp.plat.util.UserCheckFilter`）对所有 `*.action` 请求进行登录状态检查和权限验证。

**配置**：

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

**过滤器执行流程**：

```
请求进入 UserCheckFilter
        │
        ▼
  检查密码过期 ── 过期 ──▶ 重定向到密码修改页面
        │ 未过期
        ▼
  检查登录状态 ── 未登录 ──▶ 重定向到登录页面
        │ 已登录
        ▼
  CAS 模式检查 ── CAS模式访问登录页 ──▶ 重定向到 CAS 登录
        │
        ▼
  权限验证 (checkHandlerMapping)
        │
        ├── URL 匹配排除规则 ──▶ 放行
        ├── URL 匹配用户菜单 ──▶ 有权限 ──▶ 放行
        └── URL 不在用户菜单中 ──▶ 重定向 404.action
```

**权限验证机制**：

1. **缓存机制**：使用 `ConcurrentHashMap` 缓存用户的 URL 权限判断结果
2. **排除规则**：通过 `sys.handler.mapping.check.exclude.mapping` 系统参数配置不需要权限校验的 URL
3. **菜单匹配**：将系统菜单的 URL 解析为通配符模式，与请求 URL 进行匹配
4. **权限判断**：匹配到菜单后，检查当前用户是否拥有该菜单的权限

**URL 通配符转换规则**：

| 原始 URL | 转换后的通配符 | 说明 |
|----------|--------------|------|
| `/module/Project_list.action` | `/module/Project_*.action` | 下划线方法名通配 |
| `/module/Project!list.action` | `/module/Project!*.action` | 感叹号方法名通配 |
| `/module/ProjectManage.action` | `/module/Project*.action` | 去除 Manage/Action 后缀 |
| `/module/Project.action` | `/module/Project_*.action` | 添加方法名通配 |

### 5.3 PermissionTag 页面级权限

`PermissionTag`（`com.dp.plat.tags.PermissionTag`）提供页面级别的权限控制：

```jsp
<dp:permission permissionId="1">
    <button type="submit">提交审批</button>
</dp:permission>
```

**工作原理**：

1. 从 `UserContext` 获取当前用户
2. 检查用户是否拥有指定的 `permissionId`
3. 有权限：渲染标签体内容（`EVAL_BODY_INCLUDE`）
4. 无权限：跳过标签体（`SKIP_BODY`）

**当前状态**：PermissionTag 的权限检查逻辑已注释，当前总是返回 `EVAL_BODY_INCLUDE`，即所有内容均显示。建议恢复权限检查逻辑。

### 5.4 LeftMenuTag 菜单级权限

`LeftMenuTag`（`com.dp.plat.tags.LeftMenuTag`）在渲染左侧菜单时进行权限校验：

1. 从 SiteMesh 装饰器获取当前页面的 `meta.menu`、`meta.module`、`meta.group`、`meta.function`
2. 调用 `UserContext.isHasPermission()` 检查用户是否有权限访问当前页面
3. 无权限时重定向到 `404.action`

```java
if (!userContext.isHasPermission(group, function)
    && !userContext.isHasPermission(group, supfunction)
    && !userContext.isHasPermission(netmgroup, function)
    && !userContext.isHasPermission(netmgroup, supfunction)) {
    resp.sendRedirect(req.getContextPath() + "/404.action");
}
```

---

## 6. 安全审计

### 6.1 操作日志记录

PMS 系统通过 `PreformanceThresholdInterceptor`（AOP 拦截器）和 `OpLogService` 记录操作日志。

**拦截器配置**：

```xml
<bean id="preformanceThresholdInterceptor"
    class="com.dp.plat.interceptor.PreformanceThresholdInterceptor">
    <property name="opLogService" ref="opLogServiceAgent" />
</bean>

<bean id="performanceThresholdProxyCreator"
    class="org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator">
    <property name="beanNames">
        <list>
            <value>*Service</value>
        </list>
    </property>
    <property name="interceptorNames">
        <value>preformanceThresholdInterceptor</value>
    </property>
</bean>
```

**记录机制**：

1. Service 方法执行前，通过 `BaseServiceImpl.log(action)` 记录操作描述
2. Service 方法执行后，`PreformanceThresholdInterceptor.invoke()` 检查 `UserContext.option` 是否非空
3. 非空则调用 `opLogService.insertLog()` 插入操作日志
4. 清空 `UserContext.option`

```java
public Object invoke(MethodInvocation mi) throws Throwable {
    Object o = mi.proceed();
    try {
        UserContext userContext = (UserContext) SpringContext.getBean("userContext");
        if (!userContext.getOption().equals("")) {
            opLogService.insertLog();
            userContext.setOption("");
        }
    } catch (Exception e) {
    }
    return o;
}
```

**操作日志表**：`fnd_operate_log`

| 字段 | 说明 |
|------|------|
| `id` | 主键 |
| `username` | 操作用户 |
| `option` | 操作描述 |
| `createTime` | 操作时间 |
| `url` | 请求 URL |

### 6.2 fnd_data_refresh_log 数据同步日志

数据同步任务通过 `fnd_data_refresh_log` 表记录同步状态，用于故障排查和数据对账：

```sql
SELECT * FROM fnd_data_refresh_log
WHERE syncType = 'SAP'
ORDER BY createTime DESC
LIMIT 10;
```

**日志字段**：

| 字段 | 说明 |
|------|------|
| `id` | 主键 |
| `syncType` | 同步类型（SAP/D365/CRM/SMS/OA/EHR/SSE/ITR） |
| `status` | 同步状态（SUCCESS/FAIL） |
| `recordCount` | 同步记录数 |
| `errorMessage` | 错误信息 |
| `createTime` | 创建时间 |
| `startTime` | 同步开始时间 |
| `endTime` | 同步结束时间 |

**安全审计用途**：

1. 追踪数据变更来源（哪个外部系统、何时同步）
2. 检测异常同步（大量失败、数据量异常波动）
3. 数据对账（同步记录数与源系统记录数对比）
4. 合规审计（数据变更的时间线和责任人）
