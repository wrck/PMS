# PMS 技术术语解释文档

本文档对 PMS 项目管理系统中的系统专用术语、技术缩写、业务概念和技术概念进行详细解释。

---

## 一、系统专用术语

### PMS（Project Management System，项目管理系统）

PMS 是本系统的核心名称，用于管理公司售后服务项目的全生命周期，包括项目创建、计划制定、实施跟踪、交付验收、闭环回访等环节。系统覆盖直销项目、代理商项目、售前测试项目等多种项目类型。

### SPMS（Spare Parts Management System，备件管理系统）

SPMS 是备件管理系统，用于管理RMA逆向维修/退回、备品/备件管理、备件转移等过程是售后服务体系管理的重要组成部分。（信息待完善）

### Presales（售前测试）

售前测试项目是指在产品正式销售前，为潜在客户提供的设备测试和验证服务。售前项目有独立的状态流转（待启动→进行中→已完成），由售前专员（`ROLE_PRESALES_STAFF`）负责管理。售前项目类型编码为 `20`，与售后项目（编码 `10`）区分。

### Prob（Problem Ticket，技术公告）

技术公告模块用于发布产品软件版本的安全漏洞、功能缺陷及修复建议。每条技术公告关联受影响的软件版本范围，通过 `SoftVersionStrategy` 解析版本号进行自动匹配，判断项目设备是否受影响。技术公告涉及的角色包括技术公告管理员（`ROLE_PROB_ADMIN`）、技术支持人员（`ROLE_PROB_SUPPORTER`）和研发人员（`ROLE_PROB_RD`）。

### Subcontract（项目转包）

项目转包是指将项目的部分实施工作委托给第三方服务商完成。转包流程包括转包申请、审批、交付件上传、验收和付款等环节，通过 Activiti 工作流驱动审批流程。转包状态包括草稿、审批中、已审批、已完成等，由 `subcontractState` 基础数据配置。

### ClosedLoop（闭环）

闭环是项目生命周期的最终阶段，表示项目所有交付工作已完成、验收通过、回访确认。闭环流程包括：闭环申请 → 服务经理审批 → 回访 → 工程人员审核 → 项目闭环。闭环流程状态由 `closeProcessState` 字段管理，对应 `pm_project_state` 表。

### Callback（回访）

回访是项目闭环流程中的关键环节，由回访人员（`ROLE_CALLBACKPER`）对已完成实施的项目进行客户满意度调查。回访流程通过 Activiti 工作流引擎驱动，业务键格式为 `CallBack.callBackId.projectId`。回访流程启动后，项目闭环流程状态更新为 `30`（回访中）。

---

## 二、技术缩写

### ERP（Enterprise Resource Planning，企业资源计划）

企业资源计划系统，用于管理企业的核心业务流程。在 PMS 中，ERP 是订单数据的主要来源，通过定时任务（`GainOrderByERP`）每日同步订单和订单行数据。

### SAP（Systems Applications and Products）

SAP 是公司此前使用的 ERP 系统，PMS 通过 `sqlMapClientTemplateSAP` 数据源连接 SAP 数据库，查询销售订单（`DP_V_SO_ORDER_4_PMS`）和退货订单（`DP_V_RMA_ORDER_4_PMS`）。SAP 数据源已逐步迁移至 D365。

### D365（Dynamics 365）

微软 Dynamics 365 是公司当前使用的 ERP 系统，替代了原有的 SAP。PMS 通过 `sqlMapClientTemplateERP`/`sqlMapClientTemplateD365` 数据源连接 D365 数据库，同步订单、合同等业务数据。

### CRM（Customer Relationship Management，客户关系管理）

客户关系管理系统，PMS 通过 `sqlMapClientTemplateCRM` 数据源与 CRM 系统交互，同步客户和商机信息。CRM 数据同步由 `sql-map-refresh-data-crm-config.xml` 配置。

### SMS（Sales Management System，销售管理系统）

销售管理系统是公司内部的销售项目管理平台，PMS 通过定时任务（`TaskBySMS`）每日 23:30 同步 SMS 中的项目属性数据。SMS 同步的项目信息包括项目编码、实施方式（`column012`）等，其中实施方式标记为只读（`column012Readonly ≠ -1`）。

### OA（Office Automation，办公自动化）

办公自动化系统，PMS 早期通过 `GainPersonByOA` 任务同步 OA 中的人员信息。目前已切换至 EHR 系统同步。

### EHR（Electronic Human Resources，电子人力资源）

EHR 是公司的人力资源管理系统，PMS 通过定时任务（`GainPersonByEHR`）每日 22:30 同步 EHR 中的人员组织架构信息，包括员工姓名、部门、联系方式等。

### SSE（Self-Service Expense System，自助费用报销系统）

SSE 是公司内部的自助费用报销系统，PMS 通过 `sqlMapClientTemplateSSE` 数据源连接 SSE 数据库，主要用于项目转包付款信息的同步（`SubcontractPaymentAutoComplete` 任务）。

### ITR（IT Service Request，IT 服务请求）

ITR 是公司的工单管理系统，PMS 通过 `itr.problemTicket.base.url` 系统参数配置 ITR 基础 URL，在项目详情页面提供工单查询的跳转链接。

### RMA（Return Merchandise Authorization，退货授权）

RMA 是退货授权流程的缩写，在 PMS 中退货订单的 `orderType=1`。RMA 订单数据从 SAP/D365 同步，在设备清单页面与正常订单合并展示。RMA 核销是指将退货设备与原销售订单进行对冲处理。

---

## 三、业务概念

### 直销项目

由公司销售团队直接与客户签约的项目，销售类型（`salesType`）为 `01`。直销项目由公司服务团队负责实施交付。

### 代理商项目

通过代理商渠道签约的项目，实施方式（`column012`）为代理商自服（`3`）或原厂督导（`1`）。代理商项目需维护代理商渠道信息（`agentChannel`）。

### 售前借货

在产品正式销售前，将设备借给潜在客户进行测试验证的业务模式。售前借货项目类型为 `20`（售前测试项目），有独立的状态流转和自动超时关闭机制（`AutoStartPresalesProjectJob`）。

### 借转销

售前借货项目转化为正式销售项目的业务流程，销售类型（`salesType`）为 `02`。借转销项目需将借货设备转为正式销售设备。

### RMA 核销

退货授权核销，将退货订单的设备与原销售订单进行对冲。在 PMS 设备清单页面，RMA 订单数据与正常订单合并展示，通过 `queryRmaOrderDataByContractNo` 方法查询退货数据。

### 总代借货

总代理借货项目，销售类型（`salesType`）为 `14`。总代借货项目的特殊性在于：同一合同号下可能存在多个利润中心（`column001`），需要按利润中心拆分订单信息。系统通过 `callSplitSoleAgentLendOrderInfo` 存储过程自动拆分。

### 项目闭环

项目完成所有交付工作后的收尾流程。闭环前需满足以下条件：
1. 必传交付件已全部上传
2. 最终客户和服务提供商信息已维护
3. 安装数量与发货数量一致
4. 无正在进行的回访流程

### 维保回访

项目闭环后对客户进行的售后服务质量回访，由维保回访人员（`ROLE_WARRANTY_CALLBACKER`）负责。维保回访与普通项目回访（`ROLE_CALLBACKPER`）使用不同的角色，确保职责分离。

### 项目督查

项目监督和检查机制，通过 `ProjectSupervision` 模块实现，对项目实施过程进行质量管控和进度监督。

### 项目转包

将项目的部分实施工作外包给第三方服务商。转包流程包括：
1. 转包申请（填写转包信息、上传附件）
2. 审批流程（Activiti 工作流驱动）
3. 交付件上传与验收
4. 付款管理（与 SSE 系统同步付款信息）

### 交付物

项目实施过程中需要提交的各类文档和成果物，存储在 `pm_project_deliver` 表中。交付物按事件节点（`eventKey`）分类，部分交付物为必传项（影响闭环申请）。

### 验收

项目交付后的确认流程，包括初验（`PROJECT_PLAN_STATE_45`）和终验（`PROJECT_PLAN_STATE_46`）两个阶段。验收状态由工程计划状态字段管理。

---

## 四、技术概念

### 泛化字段（column001~014）

PMS 项目表（`pm_project_header`）采用泛化字段设计，使用 `column001` 到 `column014` 共 14 个通用字段存储不同业务含义的数据。每个泛化字段通过 `pm_column_of_relationship` 配置表映射到具体的业务含义。

| 字段 | 业务含义 | 说明 |
|---|---|---|
| `column001` | 办事处编码/利润中心 | 项目所属办事处，也是总代借货的利润中心 |
| `column002` | 行业 | 项目所属行业 |
| `column003` | 客户名称 | 项目客户 |
| `column004` | 市场部 | 所属市场部门（如"运营商市场部"） |
| `column005` | 系统名称 | 项目涉及的系统 |
| `column006` | 扩展属性1 | 预留扩展字段 |
| `column007` | 扩展属性2 | 预留扩展字段 |
| `column008` | 不予跟踪理由 | 项目不予跟踪的原因 |
| `column009` | 订单创建时间 | 关联订单的创建日期 |
| `column010` | 项目类别 | 10=普通类，20=工程类 |
| `column011` | 签约类型 | 10=直签类，20=非直签类 |
| `column012` | 实施方式 | 0=原厂直服，1=原厂督导，3=代理商自服，4=原厂集成 |
| `column013` | 最终客户 | 最终客户名称 |
| `column014` | 回退原因 | 项目回退说明 |

### 字段映射（pm_column_of_relationship）

泛化字段与业务含义的映射关系配置表。通过 `dataTypeCode` 和 `columnId` 确定每个泛化字段在不同业务场景下的实际含义、显示名称和描述信息。Bean 类中对应的 `columnXXXName` 和 `columnXXXDesc` 属性即来源于此映射。

### customInfo（JSON 扩展字段）

`pm_project_header.customInfo` 字段使用 JSON 格式存储项目的扩展信息，避免频繁修改数据库表结构。在 `Project` Bean 中通过 `JsonCustomInfo` 类进行封装，支持通过 `getCustomInfoByKey(key)` 和 `setCustomInfoByKey(key, value)` 方法读写扩展字段。

```java
// customInfo 的典型使用场景
project.setCustomInfoByKey("serviceManagerCode", serviceManagerCode);
project.setCustomInfoByKey("programManagerCode", programManagerCode);
project.setCustomInfoByKey("smsProjectAmount", smsProjectAmount);

// 读取时优先从显式字段获取，若为空则从 customInfo 中获取
public String getServiceManagerCode() {
    return StringUtils.defaultIfBlank(serviceManagerCode,
        (String) getCustomInfoByKey("serviceManagerCode"));
}
```

### effectiveFrom / effectiveTo（软删除时间窗口）

项目成员等记录采用时间窗口方式实现软删除，而非物理删除或标志位删除。`effectiveFrom` 表示记录生效时间，`effectiveTo` 表示记录失效时间。查询有效成员时，条件为 `effectiveTo IS NULL`。

```sql
-- 查询当前有效的项目成员
SELECT * FROM pm_project_member
WHERE projectId = #projectId#
  AND effectiveTo IS NULL
```

当需要"删除"一个成员时，设置其 `effectiveTo` 为当前时间，而非物理删除记录。

### ServiceAgent（事务代理 Bean）

PMS 采用 `*Service` + `*ServiceAgent` 双 Bean 模式实现声明式事务管理。`*Service` 是实际的业务实现类，`*ServiceAgent` 是通过 `TransactionProxyFactoryBean` 创建的事务代理，为匹配 `insert*`/`update*`/`delete*` 等方法名的方法自动添加事务。

- **Action 层**注入 `*ServiceAgent`（事务代理），确保写操作在事务中执行
- **Service 层互调**注入 `*Service`（原始 Bean），避免事务嵌套问题

```xml
<!-- 业务 Bean -->
<bean id="projectService" class="com.dp.plat.service.ProjectServiceImpl" parent="baseServce">
    <property name="projectDao" ref="projectDao"/>
</bean>
<!-- 事务代理 Bean -->
<bean id="projectServiceAgent" parent="transactionBaseService">
    <property name="target" ref="projectService"/>
</bean>
```

### SqlMapClient（iBatis 客户端）

iBatis（现已更名为 MyBatis）的 SQL 映射客户端，PMS 使用 iBatis 2.x 版本。`SqlMapClientTemplate` 是 Spring 对 iBatis 的封装，提供类型安全的数据库操作方法。

```java
// DAO 层通过 SqlMapClientTemplate 调用 SQL 映射
List<Project> list = getSqlMapClientTemplate().queryForList("query_project_list", project);
Integer count = (Integer) getSqlMapClientTemplate().queryForObject("query_project_count", project);
getSqlMapClientTemplate().insert("insert_project", project);
getSqlMapClientTemplate().update("update_project", project);
getSqlMapClientTemplate().delete("delete_project", projectId);
```

PMS 配置了多个 `SqlMapClientTemplate` 对应不同数据源：
- `sqlMapClientTemplate`：主数据源（PMS 数据库）
- `sqlMapClientTemplateSAP`：SAP 数据源
- `sqlMapClientTemplateERP`/`sqlMapClientTemplateD365`：D365 数据源
- `sqlMapClientTemplateSSE`：SSE 数据源

### ProcessInstance（流程实例）

Activiti 工作流引擎中的流程实例对象。在 PMS 中，每个业务流程（回访、闭环、转包审批等）启动时创建一个 `ProcessInstance`，通过 `businessKey` 关联业务数据。

```java
// 启动流程实例
ProcessInstance pi = runtimeService.startProcessInstanceByKey(
    processDefinitionKey,  // 流程定义键（如 "CallBack"）
    businessKey,           // 业务键（格式：类名.业务ID.项目ID）
    vars                   // 流程变量
);
```

businessKey 的格式约定为 `{SimpleClassName}.{businessId}.{projectId}`，例如 `CallBack.123.456` 表示回访 ID 为 123、项目 ID 为 456 的流程实例。

### TaskHandler（任务处理器）

Activiti 流程中的任务处理器，用于在任务创建、分配、完成时执行自定义逻辑。PMS 通过 `UnifyTaskBPMNParserHandler` 统一注册任务处理器，支持将任务信息同步到统一任务平台。

```xml
<!-- activiti-context.xml 中的任务处理器配置 -->
<property name="customDefaultBpmnParseHandlers">
    <list>
        <bean class="com.dp.plat.activiti.unifytask.handler.UnifyTaskBPMNParserHandler">
            <property name="parsers">
                <list>
                    <bean class="com.dp.plat.activiti.unifytask.handler.UnifyTaskBPMNParser"/>
                </list>
            </property>
        </bean>
    </list>
</property>
```

### UserContext（用户上下文）

当前登录用户的上下文信息，以 Session 作用域的 Spring Bean 形式存在。通过 `UserContext.getUserContext()` 获取，提供当前用户信息、角色判断、权限校验等功能。

```java
// Spring 配置：Session 作用域 Bean
<bean id="userContext" class="com.dp.plat.context.UserContext" scope="session">
    <aop:scoped-proxy />
</bean>

// 代码中使用
UserContext ctx = UserContext.getUserContext();
User user = ctx.getUser();                    // 获取当前用户
boolean isAdmin = ctx.isHasRole(ROLE_ADMIN);  // 角色判断
boolean hasPerm = ctx.isHasPermission(group, function);  // 权限校验
```

### DisplayParam（分页参数）

分页查询参数封装类，包含当前页码、每页条数、排序字段映射等。前端通过 URL 参数传递，后端在 `getParam()` 方法中进行 URL 解码处理。

### BaseDao（DAO 基类）

所有 DAO 实现类的基类，提供多数据源 `SqlMapClientTemplate` 注入和操作日志记录能力。DAO 层采用 `scope="prototype"` 配置，每次请求创建新实例。

### BaseServiceImpl（Service 基类）

所有 Service 实现类的基类，提供用户上下文注入、错误/警告消息收集、操作日志记录等通用能力。通过 `addErrmsg()` 和 `addWarnmsg()` 方法收集业务异常信息，由 Action 层的 `setErrmsg(BaseService)` 方法统一提取到页面显示。

### CopyLRU（带深拷贝的 LRU 缓存）

PMS 系统中 iBatis 自定义的缓存类型，别名 `CopyLRU`，实现类为 `com.dp.plat.ibatis.cache.LRUCacheController`。在标准 LRU（最近最少使用）缓存策略基础上增加了深拷贝能力，确保缓存返回的对象与缓存中存储的对象互不影响，避免因外部修改导致缓存数据污染。PMS 中所有 iBatis 缓存均使用 `type="CopyLRU"`，默认刷新间隔为 1 小时。

```xml
<!-- sql-map-config.xml 中的类型别名注册 -->
<typeAlias type="com.dp.plat.ibatis.cache.LRUCacheController" alias="CopyLRU" />
```

### PreformanceThresholdInterceptor（性能阈值拦截器）

Spring AOP 方法拦截器，实现 `MethodInterceptor` 接口，类路径 `com.dp.plat.interceptor.PreformanceThresholdInterceptor`。该拦截器在 Service 方法执行后，检查当前用户上下文（`UserContext`）中是否存在待记录的操作日志标记（`option` 非空），若有则调用 `OpLogService.insertLog()` 将操作日志写入 `fnd_operate_log` 表。注意：类名中 "Preformance" 为 "Performance" 的拼写错误，与源码保持一致。

### BaseAction（Action 基类）

所有 Action 类的基类，类路径 `com.dp.plat.action.BaseAction`，继承 Struts2 的 `ActionSupport`，实现 `ServletContextAware`、`ServletRequestAware`、`ServletResponseAware` 接口。提供以下通用能力：

- `start()` 方法：页面初始化入口，默认返回 `INPUT`
- `setErrmsg(BaseService)` 方法：从 Service 层收集错误/警告信息到 Action
- `getServletRequest()` / `getServletResponse()`：获取 Servlet API 对象
- `addFieldError()`：添加字段级错误信息

Action 在 Spring 容器中必须配置为 `scope="prototype"`（非单例），确保每个请求独立的 Action 实例。

### SiteMesh（页面装饰器框架）

OpenSymphony 的页面装饰器框架，PMS 使用 `struts2-sitemesh-plugin` 集成。SiteMesh 通过 Servlet Filter（`PageFilter`）拦截响应，根据 JSP 页面中 `<meta>` 标签声明的装饰器信息（menu、module、group、function），自动应用对应的页面装饰模板，实现页面布局（顶部导航栏、左侧菜单、底部版权）的统一管理和复用。PMS 配置了主装饰器、弹窗装饰器和 JSON 装饰器三种类型。

### DisplayTag（列表展示标签库）

开源 JSP 标签库，用于列表数据的分页展示和导出。PMS 通过 `displaytag` 和 `displaytag-export-poi` 依赖引入，使用 `<display:table>` 标签渲染数据列表，支持分页（`pagesize`）、排序（`sortable`）、Excel 导出（`export="true"`）等功能。通过 `ResponseOverrideFilter` 处理导出响应。标签中 `media="html"` 控制仅页面显示，`media="excel"` 控制仅导出时包含。

### Quartz（定时任务调度器）

PMS 使用 Quartz 调度框架（`org.quartz-scheduler:quartz`）管理定时任务，包括数据同步任务（如 `GainOrderByERP`、`GainPersonByEHR`、`TaskBySMS`）和业务定时任务（如 `AutoStartPresalesProjectJob`）。Quartz 与 Spring 集成，通过 `applicationContext.xml` 中的 `SchedulerFactoryBean` 配置调度器和触发器，支持 cron 表达式和简单间隔两种触发方式。

### XssStrutsInterceptor（XSS 防护拦截器）

Struts2 自定义拦截器，类路径 `com.dp.plat.security.xss.struts.XssStrutsInterceptor`，用于防止跨站脚本攻击（XSS）。采用三级 URL 策略处理请求数据：

1. **排除策略**（`excludeUrls`）：如 `/base/executeSql.*`，不处理直接放行
2. **清理策略**（`cleanUrls`）：如 `/module/prob_*`，移除危险 HTML 标签但保留安全内容（适用于富文本模块）
3. **编码策略**（`encodeUrls`）：如 `/*`，对所有特殊字符进行 HTML 实体编码

优先级：`excludeUrls` > `cleanUrls` > `encodeUrls`。该拦截器在 `struts.xml` 中注册，加入默认拦截器栈。

### UserCheckFilter（用户认证过滤器）

Servlet 过滤器，类路径 `com.dp.plat.util.UserCheckFilter`，实现 `Filter` 接口。负责以下安全检查：

1. **登录状态检查**：未登录用户重定向到登录页面
2. **CAS 模式判断**：根据 `sys.cas` 系统参数决定是否启用 CAS 单点登录
3. **强制修改密码**：检测到需要修改密码时重定向到修改密码页面
4. **URL 权限缓存**：使用 `ConcurrentHashMap` 缓存 URL 权限判断结果，避免每次请求都查询数据库
5. **会话管理**：处理 CAS 单点登出（`SingleSignOutHttpSessionListener`）

该过滤器在 `web.xml` 中配置，拦截所有请求（`/*`），是 PMS 安全防护的第一道屏障。

### CustomRuntimeException（自定义业务异常）

PMS 系统自定义的运行时异常类，继承 `RuntimeException`，用于在业务逻辑中抛出可预期的业务异常（如权限不足、数据校验失败等）。与系统异常（如 `NullPointerException`）不同，`CustomRuntimeException` 携带明确的业务错误信息，由 Action 层捕获后通过 `setErrmsg()` 方法展示给用户。

### FastjsonTypeHandler（Fastjson 类型处理器）

iBatis 自定义 TypeHandler，类路径 `com.dp.plat.ibatis.handler.FastjsonTypeHandler`，别名 `JsonTypeHandler`。负责在 Java 对象与数据库 JSON 字段之间进行类型转换：写入时将 `JsonCustomInfo` 对象序列化为 JSON 字符串，读取时将 JSON 字符串反序列化为 `JsonCustomInfo` 对象。在 `sql-map-config.xml` 中注册，同时处理 `jdbcType="JSON"` 和 `jdbcType="OTHER"` 两种情况。

### DateTimeTypeHandler（日期时间类型处理器）

iBatis 自定义 TypeHandler，类路径 `com.dp.plat.util.DateTimeTypeHandler`，别名 `DateTimeHandler`。负责在 PMS 自定义的 `com.dp.plat.type.DateTime` 类型与数据库 `int` 类型之间进行转换，用于处理以整数形式存储的日期时间字段。

### BaseCustomInfoBean（自定义信息基类）

PMS 系统中 JSON 扩展字段的基类，`JsonCustomInfo` 继承自此类。提供 JSON 扩展字段的通用读写能力，通过 `getCustomInfoByKey(key)` 和 `setCustomInfoByKey(key, value)` 方法实现动态属性的存取。

### JsonCustomInfo（JSON 自定义信息）

`pm_project_header.customInfo` 字段的 Java 映射类，继承 `BaseCustomInfoBean`，用于封装项目的 JSON 扩展信息。在 `Project` Bean 中作为 `customInfo` 属性的类型，支持通过键值对方式读写扩展字段（如 `serviceManagerCode`、`programManagerCode`、`smsProjectAmount` 等）。通过 `FastjsonTypeHandler` 实现与数据库 JSON 字段的自动转换。

### Md5Util（MD5 加密工具类）

密码加密工具类，类路径 `com.dp.plat.util.Md5Util`，提供旧版密码加密功能。使用 `java.security.MessageDigest` 实现 MD5 摘要，输出 32 位小写十六进制字符串，无盐值、单次哈希。该加密方式安全性较低，已被 `PasswordUtil` 替代，但为兼容旧版用户密码仍保留。

### PasswordUtil（密码加密工具类）

密码加密工具类，类路径 `com.dp.plat.util.PasswordUtil`，提供新版密码加密功能。采用 SHA1 + MD5 加盐迭代加密：先对密码做一次 SHA1 加盐哈希（盐值为用户名，迭代 1 次），再对 SHA1 结果做 1024 次 MD5 加盐迭代哈希（盐值为用户名）。相比 `Md5Util` 的单次 MD5 无盐加密，安全性显著提升。
