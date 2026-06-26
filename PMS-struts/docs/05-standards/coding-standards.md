# 编码规范文档

本文档基于 PMS 项目源码提炼，涵盖 Action、Service、DAO、iBatis SQL 映射、Spring Bean 装配、JSP 页面、自定义标签和 Entity 命名等各层编码规范，并补充代码复用策略、缓存机制、并发控制和数据加密方案。

---

## 1. Action 层规范

### 1.1 继承 BaseAction

所有 Action 类必须继承 `com.dp.plat.action.BaseAction`，该基类提供以下能力：

- 实现 `ServletContextAware`、`ServletRequestAware`、`ServletResponseAware`，自动注入 Servlet API 对象
- 提供 `start()` 默认方法，返回 `INPUT`，用于页面初始化加载
- 提供 `setErrmsg(BaseService)` 方法，统一收集 Service 层的错误和警告信息
- 提供 `getServletRequest()` / `getServletResponse()` 获取请求/响应对象

```java
public class ProjectAction extends BaseAction {
    private ProjectService projectService;

    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public String start() {
        return INPUT;
    }

    public String list() {
        return SUCCESS;
    }
}
```

### 1.2 scope=prototype

Action 在 Spring 容器中必须配置为 `scope="prototype"`（非单例），因为 Struts2 的 Action 是有状态的，每个请求需要独立的 Action 实例。

```xml
<bean id="projectAction" class="com.dp.plat.action.ProjectAction"
    scope="prototype">
    <property name="projectService" ref="projectServiceAgent" />
</bean>
```

> **注意**：如果忘记设置 `scope="prototype"`，多个请求将共享同一个 Action 实例，导致数据串扰。

### 1.3 execute() 方法约定

- `start()` 方法：页面初始化入口，默认返回 `INPUT`，跳过 Struts2 的 workflow 和 validation 拦截器
- 业务方法命名应语义清晰，如 `list()`、`save()`、`delete()`、`submit()`
- 方法返回值使用 Struts2 常量：`SUCCESS`、`INPUT`、`ERROR`、`NONE`、`LOGIN`

### 1.4 Action 注解与结果映射

Action 配置在 `struts-sys.xml` 等模块配置文件中，使用 XML 方式而非注解方式：

```xml
<package name="main" extends="basepackage" namespace="/sys">
    <action name="Project_*" class="projectAction" method="{1}">
        <result name="input">/sys/module/project_{1}.jsp</result>
        <result name="success">/sys/module/project_{1}.jsp</result>
    </action>
</package>
```

**命名空间约定**：

| 命名空间 | 用途 | 继承包 |
|----------|------|--------|
| `/sys` | 主业务页面 | basepackage + json-default |
| `/module` | 模块操作 | basepackage |
| `/module/sub` | 弹窗子页面 | basepackage |
| `/work` | 工作流操作 | basepackage |
| `/ajax` | AJAX JSON 请求 | defaultJson |
| `/base` | 基础功能 | basepackage |

### 1.5 全局结果映射

`basepackage` 中定义了全局结果映射，所有继承该包的 Action 共享：

```xml
<global-results>
    <result name="redirect1">/redirect.jsp</result>
    <result name="globalLogin">/index.jsp</result>
    <result name="globalAdminLogin">/error403.jsp</result>
    <result name="errorRole">/error403.jsp</result>
    <result name="error">/error.jsp</result>
</global-results>
```

---

## 2. Service 层规范

### 2.1 接口 + 实现分离

Service 层采用接口与实现分离的模式：

- 接口定义业务方法签名：`ProjectService`
- 实现类提供具体逻辑：`ProjectServiceImpl extends BaseServiceImpl implements ProjectService`

```java
public interface ProjectService extends BaseService {
    List<Project> findProjectList(ProjectQuery query);
    void insertProject(Project project);
    void updateProject(Project project);
}

public class ProjectServiceImpl extends BaseServiceImpl implements ProjectService {
    private ProjectDao projectDao;

    public void setProjectDao(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    @Override
    public List<Project> findProjectList(ProjectQuery query) {
        return projectDao.queryForList("findProjectList", query);
    }
}
```

### 2.2 *Service + *ServiceAgent 事务代理命名

每个 Service 必须配置两个 Bean：

1. **真实 Service Bean**：`{业务模块}Service`，继承 `baseServce`，注入 DAO 和其他依赖
2. **事务代理 Bean**：`{业务模块}ServiceAgent`，继承 `transactionBaseService`，包装真实 Service

```xml
<bean id="projectServiceAgent" parent="transactionBaseService">
    <property name="target" ref="projectService" />
</bean>
<bean id="projectService" class="com.dp.plat.service.ProjectServiceImpl"
    lazy-init="false" parent="baseServce">
    <property name="projectDao" ref="projectDao" />
</bean>
```

> **关键规则**：Action 层必须引用 `*ServiceAgent`，而非直接引用 `*Service`，否则事务不生效。

### 2.3 事务方法前缀规则

`transactionBaseService` 通过方法名前缀匹配决定是否开启事务，传播行为均为 `PROPAGATION_REQUIRED`：

| 方法名前缀 | 语义 | 示例方法 |
|-----------|------|---------|
| `insert*` | 插入操作 | `insertUser()`, `insertProject()` |
| `update*` | 更新操作 | `updatePassword()`, `updateStatus()` |
| `delete*` | 删除操作 | `deleteRole()`, `deleteMenu()` |
| `parse*` | 解析操作 | `parseExcel()`, `parseXML()` |
| `add*` | 添加操作 | `addMember()`, `addRole()` |
| `save*` | 保存操作 | `saveProject()`, `saveConfig()` |
| `do*` | 执行操作 | `doApprove()`, `doCallback()` |
| `keep*` | 维持操作 | `keepAlive()`, `keepSession()` |
| `start*` | 启动操作 | `startProcess()`, `startWorkflow()` |
| `submit*` | 提交操作 | `submitApproval()`, `submitForm()` |

**不匹配以上前缀的方法（如 `find*`、`get*`、`query*`、`search*`）不开启事务**，以只读方式执行。

```xml
<bean id="transactionBaseService" abstract="true"
    class="org.springframework.transaction.interceptor.TransactionProxyFactoryBean">
    <property name="transactionManager" ref="transactionManager" />
    <property name="transactionAttributes">
        <props>
            <prop key="insert*">PROPAGATION_REQUIRED</prop>
            <prop key="update*">PROPAGATION_REQUIRED</prop>
            <prop key="delete*">PROPAGATION_REQUIRED</prop>
            <prop key="parse*">PROPAGATION_REQUIRED</prop>
            <prop key="add*">PROPAGATION_REQUIRED</prop>
            <prop key="save*">PROPAGATION_REQUIRED</prop>
            <prop key="do*">PROPAGATION_REQUIRED</prop>
            <prop key="keep*">PROPAGATION_REQUIRED</prop>
            <prop key="start*">PROPAGATION_REQUIRED</prop>
            <prop key="submit*">PROPAGATION_REQUIRED</prop>
        </props>
    </property>
</bean>
```

### 2.4 BaseServiceImpl 基类

`BaseServiceImpl` 提供以下公共能力：

- `addErrmsg(String)` / `getErrmsg()`：错误信息收集，Action 层通过 `setErrmsg(service)` 获取
- `addWarnmsg(String)` / `getWarnmsg()`：警告信息收集
- `isError()` / `isWarn()`：错误/警告状态判断
- `clearErrMsg()`：清除所有错误和警告信息
- `getUserContext()`：获取当前登录用户上下文
- `log(String action)`：记录操作日志
- `getLoginName()` / `getRealname()`：获取当前用户信息

---

## 3. DAO 层规范

### 3.1 继承 BaseDao

所有 DAO 类必须继承 `com.dp.plat.dao.BaseDao`，该基类提供：

- `sqlMapClientTemplate`：主库 SQL 操作模板
- `sqlMapClientTemplateSAP`：SAP 数据源模板
- `sqlMapClientTemplateERP`：ERP/D365 数据源模板
- `sqlMapClientTemplateSSE`：SSE 数据源模板
- `opLoggerDao`：操作日志记录 DAO
- `getCurrUsername()`：获取当前用户名

```java
public class ProjectDaoImpl extends BaseDao {
    public List<Project> findProjectList(ProjectQuery query) {
        return getSqlMapClientTemplate().queryForList("findProjectList", query);
    }

    public Object insertProject(Project project) {
        return getSqlMapClientTemplate().insert("insertProject", project);
    }
}
```

### 3.2 iBatis SqlMapClientTemplate 使用

**查询操作**：

```java
List list = getSqlMapClientTemplate().queryForList("sqlId", paramObj);
Object obj = getSqlMapClientTemplate().queryForObject("sqlId", paramObj);
Map map = getSqlMapClientTemplate().queryForMap("sqlId", paramObj, "keyProperty");
```

**写入操作**：

```java
Object newId = getSqlMapClientTemplate().insert("sqlId", paramObj);
int rows = getSqlMapClientTemplate().update("sqlId", paramObj);
int rows = getSqlMapClientTemplate().delete("sqlId", paramObj);
```

**外部数据源查询**：

```java
List sapList = getSqlMapClientTemplateSAP().queryForList("findSAPData", param);
List erpList = getSqlMapClientTemplateERP().queryForList("findD365Data", param);
```

> **注意**：外部数据源（SAP/ERP/SSE）的操作不在 Spring 事务管理范围内，跨数据源操作不会回滚。

### 3.3 查询方法命名规范

| 方法前缀 | 语义 | 返回类型 | 示例 |
|---------|------|---------|------|
| `find*` | 条件查询 | `List` | `findProjectList()`, `findUserByRole()` |
| `get*` | 单条查询 | `Object` | `getProjectById()`, `getUserByName()` |
| `query*` | 复杂查询 | `List`/`Map` | `queryStatistics()`, `queryReport()` |
| `search*` | 搜索查询 | `List` | `searchProjects()`, `searchLogs()` |

### 3.4 DAO Bean 配置

DAO Bean 必须配置 `scope="prototype"` 和 `parent` 属性：

```xml
<bean id="projectDao" class="com.dp.plat.dao.ProjectDaoImpl"
    scope="prototype" parent="baseDao" />
```

需要操作日志的 DAO 继承 `baseContextLoggerDao`：

```xml
<bean id="loginDao" class="com.dp.plat.dao.LoginDaoImpl"
    scope="prototype" parent="baseContextLoggerDao" />
```

---

## 4. iBatis SQL 映射规范

### 4.1 namespace 约定

主库 SQL 映射文件 `useStatementNamespaces="false"`，namespace 主要用于逻辑分组：

```xml
<sqlMap namespace="business">
    <select id="findProjectList" ...>
</sqlMap>
```

数据刷新配置 `useStatementNamespaces="true"`，SQL ID 需要带 namespace 前缀调用。

### 4.2 resultMap / resultClass 使用

**resultClass**：简单查询，直接映射到 Java 类或 Map：

```xml
<select id="findProjectList" parameterClass="com.dp.plat.data.bean.ProjectQuery"
    resultClass="com.dp.plat.data.bean.Project">
    SELECT projectId, projectCode, projectName
    FROM pm_project_header
    WHERE effectiveTo IS NULL
</select>
```

**resultMap**：复杂映射，需要字段别名或嵌套查询：

```xml
<resultMap class="java.util.HashMap" id="user_permissions">
    <result property="menuId" column="menu_id" />
    <result property="menuName" column="menu_name" />
    <result property="powerId" column="power_id" />
</resultMap>

<select id="queryUserPermissions" parameterClass="int"
    resultMap="user_permissions">
    SELECT m.menu_id, m.menu_name, p.power_id
    FROM fnd_menus m
    LEFT JOIN fnd_powers p ON m.menu_id = p.menu_id
    WHERE m.effectiveTo IS NULL
</select>
```

### 4.3 # 与 $ 区别

| 符号 | 行为 | 安全性 | 使用场景 |
|------|------|--------|---------|
| `#property#` | 参数化绑定（PreparedStatement 占位符 `?`） | **安全**，防止 SQL 注入 | 所有用户输入的参数值 |
| `$property$` | 字符串直接拼接（Text Substitution） | **不安全**，存在 SQL 注入风险 | 表名、列名、ORDER BY 等结构性元素 |

**安全用法示例**：

```xml
<select id="findProjectList" parameterClass="map"
    resultClass="com.dp.plat.data.bean.Project">
    SELECT * FROM pm_project_header
    WHERE projectCode LIKE concat('%', #projectCode#, '%')
    AND officeCode = #officeCode#
</select>
```

**$ 的必要使用场景**：

```xml
<select id="findDataByTable" parameterClass="map" resultClass="hashmap">
    SELECT * FROM $tableName$ WHERE id = #id#
</select>

<isNotEmpty prepend="ORDER BY" property="sortField">
    $sortField$ $sortOrder$
</isNotEmpty>
```

> **安全警告**：使用 `$` 时必须确保参数来源可信（如系统内部枚举值），绝不能直接拼接用户输入。

### 4.4 动态 SQL

**isNotEmpty**：判断属性非空：

```xml
<dynamic prepend="WHERE">
    <isNotEmpty prepend="AND" property="projectCode">
        projectCode LIKE concat('%', #projectCode#, '%')
    </isNotEmpty>
    <isNotEmpty prepend="AND" property="officeCode">
        officeCode = #officeCode#
    </isNotEmpty>
    <isNotEmpty prepend="AND" property="effectiveFrom">
        effectiveFrom >= #effectiveFrom#
    </isNotEmpty>
</dynamic>
```

**iterate**：遍历集合：

```xml
<isNotEmpty property="roleIds">
    <iterate property="roleIds" open="AND roleId IN (" close=")" conjunction=",">
        #roleIds[]#
    </iterate>
</isNotEmpty>
```

**isEqual / isNotEqual**：等值判断：

```xml
<isEqual property="projectType" compareValue="10">
    AND s.memberRole = 10
</isEqual>
```

**isEmpty**：判断属性为空：

```xml
<isEmpty property="userPower">
    <isNotEmpty prepend="AND" property="areaPower">
        FIND_IN_SET(wcs.officeCode, #areaPower#)
    </isNotEmpty>
</isEmpty>
```

---

## 5. Spring Bean 装配规范

### 5.1 abstract Bean 继承

系统使用 abstract Bean 定义公共属性，子 Bean 通过 `parent` 继承：

**DAO 层继承体系**：

```
baseDao (abstract)
├── sqlMapClientTemplate → sqlMapClientTemplate
│
├── baseContextLoggerDao (abstract, parent=baseDao)
│   ├── opLoggerDao → opLoggerDao
│   │
│   └── loginDao (parent=baseContextLoggerDao)
│
├── projectDao (parent=baseDao)
├── userManageDao (parent=baseDao)
└── ...
```

**Service 层继承体系**：

```
baseServce (abstract)
├── userContext → userContext
│
├── loginService (parent=baseServce)
├── projectService (parent=baseServce)
└── ...

transactionBaseService (abstract)
├── transactionManager → transactionManager
├── transactionAttributes → (事务方法前缀规则)
│
├── loginServiceAgent (parent=transactionBaseService)
├── projectServiceAgent (parent=transactionBaseService)
└── ...
```

### 5.2 parent 属性

子 Bean 通过 `parent` 属性继承父 Bean 的配置，避免重复声明：

```xml
<bean id="baseDao" abstract="true">
    <property name="sqlMapClientTemplate" ref="sqlMapClientTemplate" />
</bean>

<bean id="projectDao" class="com.dp.plat.dao.ProjectDaoImpl"
    scope="prototype" parent="baseDao" />
```

`projectDao` 自动继承 `baseDao` 的 `sqlMapClientTemplate` 属性注入。

### 5.3 lazy-init

- Service Bean 设置 `lazy-init="false"`，确保应用启动时即初始化，便于及早发现配置错误
- DAO Bean 默认跟随容器配置，通常也为非懒加载

```xml
<bean id="projectService" class="com.dp.plat.service.ProjectServiceImpl"
    lazy-init="false" parent="baseServce">
    <property name="projectDao" ref="projectDao" />
</bean>
```

---

## 6. JSP 页面组织规范

### 6.1 /sys/ 下的子目录结构

JSP 页面统一放置在 `WebContent/sys/` 目录下，按业务模块划分子目录：

```
WebContent/sys/
├── module/                    # 项目管理模块
│   ├── projectcreate.jsp      # 项目创建
│   ├── projectmodify.jsp      # 项目修改
│   └── project_list.jsp       # 项目列表
├── prob/                      # 技术公告模块
│   ├── prob_list.jsp
│   ├── prob_edit.jsp
│   └── prob_project_statistics.jsp
├── subcontract/               # 项目转包模块
│   ├── subcontract_list.jsp
│   └── subcontract_facilitatorEdit.jsp
├── callback/                  # 回访管理模块
├── presales/                  # 售前项目模块
├── maintenance/               # 维保管理模块
└── report/                    # 报表统计模块
```

### 6.2 SiteMesh 装饰器

JSP 页面通过 `<meta>` 标签声明装饰器信息，SiteMesh 根据这些元数据应用不同的页面装饰：

```jsp
<dp:base />
<meta name="menu" content="SysLeftMenu">
<meta name="module" content="<s:text name='module.plat' />">
<meta name="group" content="<s:text name='sys.leftmenu.powermanage' />">
<meta name="function" content="<s:text name='sys.project.management' />">
```

- `menu`：左侧菜单类型
- `module`：所属模块名称
- `group`：功能分组
- `function`：具体功能名称

### 6.3 DisplayTag 使用

DisplayTag 用于列表数据展示和导出：

```jsp
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<display:table name="projectList" id="project" requestURI="Project_list.action"
    pagesize="15" export="true" class="listView">
    <display:column property="projectCode" titleKey="pm.project.code"
        sortable="true" />
    <display:column property="projectName" titleKey="pm.project.name" />
    <display:column property="projectStateName" titleKey="pm.project.state" />

    <display:column title="操作" media="html">
        <a href="Project_edit.action?project.projectId=${project.projectId}">
            编辑
        </a>
    </display:column>
    <display:column title="数据" media="excel">
        ${project.projectCode}：${project.projectName}
    </display:column>
    <display:setProperty name="export.excel.filename"
        value="project-list.xls" />
</display:table>
```

**关键属性**：

| 属性 | 说明 |
|------|------|
| `name` | ValueStack 中的集合属性名 |
| `id` | 行对象变量名 |
| `requestURI` | 分页请求 URL |
| `pagesize` | 每页记录数 |
| `export` | 是否启用导出 |
| `media="html"` | 仅 HTML 页面显示 |
| `media="excel"` | 仅 Excel 导出时包含 |

---

## 7. 自定义标签使用规范

### 7.1 dp 标签库声明

```jsp
<%@ taglib prefix="dp" uri="/dp" %>
```

### 7.2 标签清单

| 标签名 | 类 | 用途 | 必要属性 |
|--------|-----|------|---------|
| `<dp:base />` | `PageBaseTag` | 输出页面基础路径（`<base href="...">`） | 无 |
| `<dp:fielderror>` | `FieldErrorTag` | 显示字段错误信息 | `accesskey` |
| `<dp:errormsg>` | `ErrorMsgTag` | 显示错误消息 | `accesskey` |
| `<dp:permission>` | `PermissionTag` | 页面级权限控制 | `permissionId` |
| `<dp:leftmenu>` | `LeftMenuTag` | 渲染左侧菜单 | `menu`, `module`, `group`, `function` |
| `<dp:barpercent>` | `BarPercentTag` | 显示百分比进度条 | `value` |
| `<dp:pagesize>` | `PagesizeTag` | 分页大小选择器 | `displayParam`, `formid` |
| `<dp:timesheet>` | `TimeSheetTag` | 显示时间策略表 | `name`, `value` |
| `<dp:refresh>` | `RefreshTag` | 页面自动刷新 | `interval` |
| `<dp:queryParams>` | `QueryParamsTag` | 生成查询参数字符串 | `param` |
| `<dp:script>` | `ScriptTag` | 输出 `<script>` 标签（支持 SRI/nonce） | `src` |
| `<dp:link>` | `LinkTag` | 输出 `<link>` 标签（支持 SRI/nonce） | `href` |
| `<dp:style>` | `StyleTag` | 输出 `<style>` 标签（支持 nonce） | 无 |

### 7.3 PermissionTag 页面级权限

控制页面片段的显示/隐藏：

```jsp
<dp:permission permissionId="1">
    <button type="submit">提交审批</button>
</dp:permission>
```

当用户无指定权限时，标签体内容不会被渲染（`SKIP_BODY`）。

### 7.4 LeftMenuTag 左侧菜单

根据用户权限自动渲染左侧菜单，并校验当前页面的访问权限：

```jsp
<dp:leftmenu menu="SysLeftMenu" module="plat"
    group="sys.leftmenu.powermanage" function="sys.project.management" />
```

### 7.5 BarPercentTag 百分比进度条

显示百分比数值的图形化进度条：

```jsp
<dp:barpercent value="completionRate" />
```

进度条根据数值自动变色：正常范围显示绿色，超过阈值显示红色。

---

## 8. Bean / Entity 命名规范

### 8.1 字段命名

- 数据库字段使用 camelCase（与 Java 属性名一致），如 `projectCode`、`officeCode`
- 主键字段统一命名为 `id` 或 `{表名}Id`，如 `projectId`
- 外键字段命名为关联表的主键名，如 `roleId`、`menuId`
- 布尔类型字段使用 `is` 前缀或语义化名称，如 `isHasRole()`、`wafService`

### 8.2 customInfo JSON 扩展字段

系统通过 `CustomInfoEntity` 基类提供 JSON 扩展字段能力，允许在不修改表结构的情况下动态添加属性：

```java
public class CustomInfoEntity implements Serializable {
    private JsonCustomInfo<String, Object> customInfo;
    private JsonCustomInfo<String, String> customStrInfo;

    public Object getCustomInfoByKey(String key) { ... }
    public void setCustomInfoByKey(String key, Object value) { ... }
    public Object removeCustomInfoByKey(String key) { ... }
}
```

> **注意**：系统中存在两个 `JsonCustomInfo` 类型：
> - `com.dp.plat.data.bean.JsonCustomInfo`：主业务模块使用的 JSON 扩展字段类型
> - `com.dp.plat.subcontract.entity.JsonCustomInfo`：转包模块使用的 JSON 扩展字段类型
>
> 两者功能相同但包路径不同，iBatis 映射时需注意 `javaType` 指定正确的全限定类名。

**数据库存储**：`customInfo` 字段以 JSON 格式存储在数据库中：

```sql
customInfo JSON COMMENT '自定义扩展信息'
```

**iBatis 映射**：使用 `FastjsonTypeHandler` 自动完成 JSON 序列化/反序列化：

```xml
<typeAlias type="com.dp.plat.ibatis.handler.FastjsonTypeHandler" alias="JsonTypeHandler" />
<typeHandler jdbcType="JSON" javaType="com.dp.plat.data.bean.JsonCustomInfo"
    callback="JsonTypeHandler" />
```

**SQL 查询 customInfo 中的属性**：

```xml
<iterate property="customInfo.iterator">
    <isNotEmpty prepend="AND" property="customInfo.iterator[].value">
        #customInfo.iterator[].value# = spc.customInfo ->> '$$.$customInfo.iterator[].key$'
    </isNotEmpty>
</iterate>
```

> **注意**：`customInfo` 字段仅用于存储和返回，避免在 WHERE 条件中对 JSON 字段进行过滤，性能较差。

### 8.3 effectiveFrom / effectiveTo 软删除模式

系统采用 `effectiveFrom` / `effectiveTo` 字段实现软删除和历史版本管理，而非物理删除：

**字段语义**：

| 字段 | 含义 | 有效记录条件 |
|------|------|------------|
| `effectiveFrom` | 生效开始时间 | `effectiveFrom <= NOW()` |
| `effectiveTo` | 生效结束时间 | `effectiveTo IS NULL` 或 `effectiveTo > NOW()` |

**查询有效记录**：

```sql
SELECT * FROM pm_project_header
WHERE effectiveTo IS NULL
```

或：

```sql
SELECT * FROM fnd_basic_data
WHERE effectiveFrom <= NOW()
AND (effectiveTo > NOW() OR effectiveTo IS NULL)
```

**软删除操作**（设置 effectiveTo 为当前时间）：

```sql
UPDATE pm_project_member
SET effectiveTo = NOW()
WHERE projectId = #projectId# AND memberRole = #memberRole#
AND effectiveTo IS NULL
```

**索引建议**：所有含 `effectiveTo` 的表建议建立索引，查询条件统一使用 `effectiveTo IS NULL` 而非 `effectiveTo > NOW()`，前者索引效率更高。

### 8.4 公共审计字段

所有业务实体继承 `BaseCustomInfoBean`，包含以下公共字段：

| 字段 | 类型 | 说明 |
|------|------|------|
| `createBy` | String | 创建人（默认取当前用户名） |
| `createTime` | Date | 创建时间 |
| `updateBy` | String | 更新人（默认取当前用户名） |
| `updateTime` | Date | 更新时间 |
| `effectiveFrom` | Date | 生效开始时间 |
| `effectiveTo` | Date | 生效结束时间（NULL 表示当前有效） |

---

## 9. 代码复用策略

### 9.1 BaseAction 模板方法复用

`BaseAction` 提供通用的请求处理模板，所有 Action 继承后自动获得以下能力：

| 复用能力 | 方法 | 说明 |
|----------|------|------|
| 错误信息收集 | `setErrmsg(BaseService)` | 从Service层收集错误/警告信息到Action |
| 页面初始化 | `start()` | 默认返回INPUT，子类可覆写 |
| Servlet API访问 | `getServletRequest()` / `getServletResponse()` | 自动注入，无需手动获取 |
| 文本资源访问 | `getText(String key)` | Struts2国际化文本 |

**使用模式**：

```java
public String save() {
    projectService.insertProject(project);
    setErrmsg(projectService);  // 复用：统一收集Service错误信息
    if (projectService.isError()) {
        return ERROR;
    }
    return SUCCESS;
}
```

### 9.2 BaseService/BaseServiceImpl 模板方法复用

`BaseServiceImpl` 提供业务层通用模板方法：

| 复用能力 | 方法 | 说明 |
|----------|------|------|
| 错误信息传递 | `addErrmsg()` / `getErrmsg()` | Service→Action错误信息通道 |
| 警告信息传递 | `addWarnmsg()` / `getWarnmsg()` | Service→Action警告信息通道 |
| 操作日志记录 | `log(String action)` | AOP拦截器自动写入fnd_operate_log |
| 用户上下文 | `getUserContext()` / `getLoginName()` | 获取当前登录用户信息 |
| 错误状态判断 | `isError()` / `isWarn()` | 判断是否有错误/警告 |

**操作日志复用模式**：

```java
// Service方法中调用log()，AOP拦截器自动记录
public void insertProject(Project project) {
    // 业务逻辑...
    this.log("创建项目：" + project.getProjectCode());  // 复用：自动记录操作日志
}
```

### 9.3 BaseDao 模板方法复用

`BaseDao` 提供数据访问层通用能力：

| 复用能力 | 方法/属性 | 说明 |
|----------|-----------|------|
| 主库操作 | `getSqlMapClientTemplate()` | 默认数据源操作模板 |
| SAP数据源 | `getSqlMapClientTemplateSAP()` | SAP RFC数据源 |
| ERP数据源 | `getSqlMapClientTemplateERP()` | D365数据源 |
| SSE数据源 | `getSqlMapClientTemplateSSE()` | SSE数据源 |
| 当前用户 | `getCurrUsername()` | 获取当前操作用户名 |

### 9.4 自定义标签复用

| 标签 | 复用场景 | 复用频率 | 说明 |
|------|----------|----------|------|
| `<dp:base />` | 所有JSP页面 | 每页必用 | 输出`<base>`标签，统一资源路径 |
| `<dp:leftmenu>` | 所有业务页面 | 每页必用 | 自动渲染左侧菜单+权限校验 |
| `<dp:permission>` | 需要权限控制的按钮/区域 | 高频 | 页面级权限控制，避免JSP中写Java代码 |
| `<dp:errormsg>` | 表单提交页面 | 高频 | 统一错误信息展示样式 |
| `<dp:barpercent>` | 项目进度/完成率 | 中频 | 百分比进度条，避免重复CSS/JS |
| `<dp:pagesize>` | DisplayTag列表页面 | 中频 | 分页大小选择器 |
| `<dp:script>` | 所有引入JS的页面 | 高频 | 支持SRI完整性校验和CSP nonce |
| `<dp:link>` | 所有引入CSS的页面 | 高频 | 支持SRI完整性校验和CSP nonce |

### 9.5 SiteMesh Decorator 复用

SiteMesh 装饰器模式实现页面布局复用：

| 装饰器 | 适用页面 | 复用内容 |
|--------|----------|----------|
| 主装饰器 | 所有业务页面 | 顶部导航栏、左侧菜单、底部版权、CSS/JS引入 |
| 弹窗装饰器 | 弹窗子页面 | 精简布局，无左侧菜单 |
| JSON装饰器 | AJAX请求 | 无装饰，直接输出JSON |

---

## 10. 缓存机制

### 10.1 iBatis CopyLRU 缓存配置

iBatis 支持在 SqlMap 文件中配置查询结果缓存，PMS 系统使用 CopyLRU（带深拷贝的最近最少使用）策略：

```xml
<sqlMap namespace="business">
    <!-- 开启CopyLRU缓存，缓存128个查询结果引用，1小时刷新 -->
    <cacheModel id="basicDataCache" type="CopyLRU" readOnly="true">
        <flushInterval hours="1" />
        <property name="size" value="128" />
        <flushOnExecute statement="insertBasicData" />
        <flushOnExecute statement="updateBasicData" />
        <flushOnExecute statement="deleteBasicData" />
    </cacheModel>

    <select id="findBasicDataList" parameterClass="map"
        resultClass="hashmap" cacheModel="basicDataCache">
        SELECT * FROM fnd_basic_data WHERE effectiveTo IS NULL
    </select>
</sqlMap>
```

**缓存配置参数说明**：

| 参数 | 说明 | 推荐值 |
|------|------|--------|
| `type` | 缓存算法：LRU/FIFO/OSCACHE/MEMORY | CopyLRU |
| `readOnly` | 只读缓存（true性能更好，false保证数据安全） | true（数据字典类） |
| `serialize` | 是否深拷贝（true安全但慢） | false |
| `flushInterval` | 定时刷新间隔 | hours="1" |
| `size` | 缓存引用数量 | 128 |
| `flushOnExecute` | 指定SQL执行时刷新缓存 | 写操作SQL ID |

### 10.2 查询结果缓存策略

| 数据类型 | 缓存策略 | 缓存时长 | 刷新触发 | 说明 |
|----------|----------|----------|----------|------|
| 基础数据(fnd_basic_data) | CopyLRU缓存 | 1小时 | 增删改操作时刷新 | 数据变更频率极低，适合缓存 |
| 菜单数据(fnd_menus) | CopyLRU缓存 | 1小时 | 菜单配置变更时刷新 | 系统预置数据，几乎不变 |
| 部门数据(fnd_department) | CopyLRU缓存 | 1小时 | 部门变更时刷新 | 组织架构偶尔调整 |
| 系统参数(fnd_sys_arg) | CopyLRU缓存 | 1小时 | 参数变更时刷新 | 系统配置，极少变更 |
| 项目列表(pm_project_header) | **不缓存** | - | - | 数据变更频繁，实时性要求高 |
| 用户信息(fnd_user_info) | Session缓存 | 会话期间 | 登录时加载 | UserContext存储在Session中 |
| 权限数据(fnd_user_power) | Session缓存 | 会话期间 | 登录时加载 | UserContext.menuList/powerList |
| URL权限判断 | ConcurrentHashMap | 应用期间 | 无 | UserCheckFilter缓存URL权限判断结果 |

### 10.3 缓存失效规则

| 失效方式 | 触发条件 | 影响范围 | 配置方式 |
|----------|----------|----------|----------|
| 定时失效 | `flushInterval` 到期 | 该cacheModel下所有查询 | `<flushInterval hours="1" />` |
| 写操作失效 | INSERT/UPDATE/DELETE 执行 | 指定的cacheModel | `<flushOnExecute statement="insertXxx" />` |
| 会话失效 | 用户登出/Session超时 | Session级缓存(UserContext) | `session.invalidate()` |
| 应用重启 | 服务器重启 | 所有内存缓存 | 重启Tomcat |
| 手动清空 | 管理员操作 | 指定缓存 | 无现成界面，需开发 |

### 10.4 缓存使用注意事项

1. **不要缓存频繁变更的数据**：项目列表、项目状态等实时性要求高的数据不应使用iBatis缓存
2. **readOnly选择**：数据字典等只读数据设为`readOnly="true"`提升性能；需要深拷贝保证线程安全时设为`false`
3. **flushOnExecute必须完整**：所有可能修改缓存数据的SQL都必须配置为flushOnExecute，否则会出现脏数据
4. **集群环境注意**：iBatis缓存是JVM本地缓存，集群环境下各节点缓存独立，可能出现短暂不一致。对于强一致性要求的数据，不应使用iBatis缓存

---

## 11. 并发控制

### 11.1 synchronized 使用规范

PMS 系统中部分操作使用 `synchronized` 关键字保证线程安全：

**当前使用场景**：

```java
// ProjectServiceImpl.java - 项目组编码生成
public synchronized String queryProjectGroupCode() {
    String maxCode = projectDao.queryMaxProjectGroupCode();
    // 生成新编码...
    return newCode;
}
```

**synchronized 使用规则**：

| 规则 | 说明 | 示例 |
|------|------|------|
| 锁对象明确 | 优先锁定具体对象，而非整个方法 | `synchronized(this)` → `synchronized(codeLock)` |
| 锁粒度最小化 | 仅锁定必要的临界区代码 | 只锁编码生成，不锁整个insertProject方法 |
| 避免嵌套锁 | 嵌套synchronized容易死锁 | 不要在持锁时调用其他synchronized方法 |
| 超时保护 | 长时间持锁操作需设置超时 | 数据库查询不应在锁内执行 |

**⚠️ 集群环境注意事项**：

> `synchronized` 仅在单个JVM内有效。PMS 如果部署为集群（多台Tomcat），`synchronized` 无法保证跨JVM的互斥。

**集群环境解决方案**：

| 方案 | 适用场景 | 实现方式 |
|------|----------|----------|
| 数据库唯一索引 | 编码唯一性 | `ALTER TABLE pm_project_group ADD UNIQUE INDEX uk_projectGroupCode (projectGroupCode)` |
| 数据库行锁 | 编码生成 | `SELECT ... FOR UPDATE` |
| 分布式锁(Redis) | 通用互斥 | `SET key value NX EX timeout` |
| 编码预留 | 高并发编码生成 | 一次预留一段编码，内存中分配 |

### 11.2 乐观锁策略

PMS 系统当前未实现标准的乐观锁机制（无version字段），但部分场景通过业务逻辑实现类似效果：

| 场景 | 实现方式 | 代码位置 |
|------|----------|----------|
| 项目状态更新 | 先查询当前状态，更新时WHERE条件包含状态值 | `UPDATE pm_project_state SET state=#newState# WHERE projectId=#projectId# AND state=#oldState#` |
| 成员变更 | 查询当前有效成员，变更时WHERE条件包含effectiveTo IS NULL | `UPDATE pm_project_member SET effectiveTo=NOW() WHERE ... AND effectiveTo IS NULL` |
| 闭环流程 | 检查processStatus当前值，更新时WHERE包含当前状态 | `UPDATE pm_cl_evaluation_header SET processStatus=#newStatus# WHERE id=#id# AND processStatus=#oldStatus#` |

**乐观锁推荐实现**（新增功能参考）：

```sql
-- 添加version字段
ALTER TABLE pm_project_header ADD COLUMN version INT DEFAULT 0;

-- 更新时检查版本
UPDATE pm_project_header
SET projectName = #projectName#, version = version + 1
WHERE projectId = #projectId# AND version = #version#
```

```java
// Service层检查更新结果
int rows = projectDao.updateProject(project);
if (rows == 0) {
    addErrmsg("数据已被其他用户修改，请刷新后重试");
}
```

### 11.3 悲观锁策略

PMS 系统在以下场景使用数据库行锁（SELECT ... FOR UPDATE）：

| 场景 | SQL | 说明 |
|------|-----|------|
| 项目编码生成 | `SELECT MAX(projectGroupCode) FROM pm_project_group FOR UPDATE` | 防止并发生成重复编码 |
| 工作流任务认领 | Activiti内部使用行锁 | 防止同一任务被多人同时认领 |

**使用规则**：

1. FOR UPDATE 必须在事务中执行（方法名匹配事务前缀规则）
2. 锁定时间尽量短，避免在持锁期间执行耗时操作
3. 查询条件必须走索引，否则会锁表而非锁行
4. 避免在FOR UPDATE结果集上做循环操作

### 11.4 数据库行锁使用

**InnoDB 行锁特性**：

| 特性 | 说明 | PMS系统注意事项 |
|------|------|-----------------|
| 行锁条件 | 仅在WHERE条件使用索引时生效 | 确保查询条件字段有索引 |
| 间隙锁 | 范围查询会锁定间隙 | 避免范围查询FOR UPDATE |
| 死锁检测 | InnoDB自动检测死锁并回滚 | 日志中关注Deadlock错误 |
| 锁等待超时 | 默认50秒，`innodb_lock_wait_timeout` | 长事务需关注超时设置 |

**死锁预防**：

1. 固定顺序访问表和行（如按projectId升序操作）
2. 保持事务简短，减少持锁时间
3. 避免在事务中执行用户交互等待
4. 合理使用索引，避免锁升级为表锁

---

## 12. 数据加密方案

### 12.1 密码加密

PMS 系统采用 **SHA1 + MD5 加盐迭代** 的双重加密方案：

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
| 1 | SHA1 | 用户名 | 1 | 先对密码做一次SHA1加盐哈希 |
| 2 | MD5 | 用户名 | 1024 | 再对SHA1结果做1024次MD5加盐迭代哈希 |

**安全特性**：

- 以用户名作为盐值，不同用户即使密码相同，加密结果也不同
- 1024次MD5迭代增加暴力破解成本
- 自动生成8位随机密码（含大小写字母、数字和特殊字符~!@#$%^&*_-#.）

**旧版MD5加密**（`com.dp.plat.util.Md5Util`）：

- 无盐值，单次MD5哈希
- 安全性较低，仅用于兼容旧数据
- 新功能必须使用 `PasswordUtil`

### 12.2 敏感字段加密

| 字段类型 | 加密方式 | 存储方式 | 解密场景 |
|----------|----------|----------|----------|
| 用户密码 | SHA1+MD5加盐迭代 | 32位十六进制字符串 | 不可逆，仅比对 |
| 邮件内容 | 无加密 | 明文存储 | 邮件展示 |
| 手机号码 | 无加密 | 明文存储 | 成员信息展示 |
| customInfo JSON | 无加密 | JSON明文存储 | 业务查询展示 |

> **安全建议**：手机号、身份证号等PII数据应考虑加密存储或脱敏展示。

### 12.3 传输加密

#### 12.3.1 HTTPS

| 配置项 | 说明 | 当前状态 |
|--------|------|----------|
| HTTPS | SSL/TLS加密传输 | 生产环境必须启用 |
| 证书类型 | 域名证书 | 由运维配置 |
| 强制HTTPS | HTTP自动跳转HTTPS | 建议启用 |
| HSTS | 严格传输安全头 | 建议启用 |

#### 12.3.2 CAS 单点登录

PMS 系统支持 CAS（Central Authentication Service）单点登录：

**配置**（`applicationContext.xml`）：

```xml
<!-- CAS配置 -->
<bean id="casFilter" class="org.jasig.cas.client.authentication.AuthenticationFilter">
    <property name="casServerLoginUrl" value="https://cas.example.com/login" />
    <property name="serverName" value="https://pms.example.com" />
</bean>
```

**CAS模式判断**：

```java
// sys.cas=1 时启用CAS模式
String casMode = sysArgService.getSysArg("sys.cas");
if ("1".equals(casMode)) {
    // CAS模式：跳过密码验证，使用CAS票据
    // 跳过密码过期检查
    // 跳过验证码校验
}
```

| 模式 | 认证方式 | 密码检查 | 验证码 | 适用场景 |
|------|----------|----------|--------|----------|
| 本地模式 | PMS本地用户表 | SHA1+MD5加盐 | 生产环境启用 | 内网独立部署 |
| CAS模式 | CAS统一认证 | 跳过 | 跳过 | 企业统一认证 |

#### 12.3.3 XSS防护传输安全

`XssStrutsInterceptor` 三级URL策略确保输入数据安全：

| 策略 | URL Pattern | 处理方式 | 适用场景 |
|------|-------------|----------|----------|
| 排除策略 | `/base/executeSql.*` | 不处理，直接放行 | SQL执行功能 |
| 清理策略 | `/module/prob_*`, `/probAudit.*` | HTML清理（移除危险标签） | 富文本模块 |
| 编码策略 | `/*` | HTML实体编码（全量转义） | 所有其他请求 |

**优先级**：`excludeUrls` > `cleanUrls` > `encodeUrls`

### 12.4 加密方案选择指南

| 场景 | 推荐方案 | 不推荐方案 | 原因 |
|------|----------|------------|------|
| 密码存储 | SHA1+MD5加盐迭代 | 纯MD5/纯SHA1 | 无盐值易被彩虹表破解 |
| 数据签名 | HMAC-SHA256 | MD5签名 | MD5已被证明不安全 |
| 敏感数据传输 | HTTPS+CAS | HTTP明文 | 中间人攻击风险 |
| 数据库敏感字段 | AES-256加密 | 明文存储 | 数据泄露风险 |
| 前端输入 | XSS过滤+参数化查询 | 直接拼接SQL | SQL注入/XSS攻击风险 |
