# PMS-springmvc 术语表

> 本文档汇总 PMS-springmvc 模块中使用的业务术语和技术术语，按拼音排序。

---

## A

### Activiti
开源工作流引擎，PMS-springmvc 使用 Activiti 5.23.0 实现审批流程管理。核心表包括 `ACT_RU_TASK`（运行时任务）、`ACT_RE_PROCDEF`（流程定义）等。

### AbstractController
PMS-springmvc 的通用 CRUD 控制器基类，提供 13 个通用方法（home、list、findOne、detail、create、update、delete 等），所有业务 Controller 继承此类。

### AbstractBaseMapper
MyBatis Mapper 接口通用基类，定义了 10 个通用方法（selectByPrimaryKey、insert、updateByPrimaryKeySelective 等），所有业务 Mapper 继承此接口。

### AF（安服）
安服（安全服务）的简称，PMS-springmvc 中以 `af_` 为前缀的表（如 `af_industry_asset`）属于安服业务域。

### afss（安服售后）
安服订单项目类型编码，对应 `ProjectConstant.ProjectType.AF_SALES_PROJECT`。

### afxx（安服先行）
安服先行项目类型编码，对应 `ProjectConstant.ProjectType.AF_XX_PROJECT`。

---

## B

### BaseEntity
PMS-springmvc 实体类的基类，提供 `customInfo`（JSON 扩展字段）、`disabled`（逻辑删除）、`effectiveFrom`/`effectiveTo`（有效期）等通用字段。

### BaseController
PMS-springmvc 的基础控制器基类，提供用户上下文获取、分页参数处理等能力。`AbstractController` 继承自 `BaseController`。

---

## C

### CAS（Central Authentication Service）
中央认证服务，PMS-springmvc 使用 CAS 3.2.2 实现单点登录（SSO）。

### Column001-014（泛化字段）
`pm_project` 表中的泛化字段，用于存储不同项目类型的扩展属性。如 `column001` 存储办事处编码，`column005` 存储系统部ID。

### CommonRelatedData（通用关联数据）
`pm_common_related_data` 表，通过 `objType` + `objId` 多态关联到任意业务对象，存储扩展关联信息。

### ContentNegotiatingViewResolver
内容协商视图解析器，PMS-springmvc 使用此解析器根据请求头自动返回 JSON 或 JSP 视图。

### CRUD（Create, Read, Update, Delete）
增删改查操作，PMS-springmvc 的 `AbstractController` 提供标准 CRUD 方法。

### customInfo（自定义扩展信息）
JSON 类型字段，大部分业务表包含此字段，用于存储动态扩展属性，避免频繁加列。通过 `FastjsonTypeHandler` 与 Java `Map<String, Object>` 互转。

---

## D

### D365
Microsoft Dynamics 365 ERP 系统，PMS-springmvc 通过 `D365DataJob` 定时从 D365 同步供应商和付款数据。

### DataType（数据类型）
工作流多态关联的数据类型标识，如 `INDUSTRY_ASSET`（行业资产）、`INDUSTRY_LEAK`（行业漏洞）等，配合 `dataId` 关联到具体业务数据。

### DispatchProject（转包项目）
`pm_dispatch_project_header` 表，存储外派/转包项目信息，通过 `projectIds`（逗号分隔字符串）关联到多个项目。

### DispatchSettlement（转包结算）
`pm_dispatch_project_settlement` 表，存储转包项目的结算信息，通过 `dispatchId` 外键关联到转包项目。

### Druid
阿里巴巴开源的数据库连接池，PMS-springmvc 使用 Druid 1.2.8，提供连接池管理、SQL 监控、SQL 防火墙等功能。

### disabled（逻辑删除标志）
BIT 类型字段，`0` 表示有效记录，`1` 表示已删除记录。所有业务表均采用逻辑删除策略。

---

## E

### EHR（Electronic Human Resource）
电子人力资源系统，PMS-springmvc 通过 `EhrDataJob` 定时从 EHR 同步公司、部门、员工等数据。

### effectiveFrom / effectiveTo（有效期）
时间字段对，`effectiveFrom` 表示记录生效开始时间，`effectiveTo` 表示失效时间（NULL 表示当前有效）。

### ExcludeAdminControllerTypeFilter
Spring 类型过滤器（非 Servlet Filter），用于在组件扫描时排除不需要的 Controller 类型。

---

## F

### Facilitator（服务商）
`pm_facilitator` 表，存储外派服务商信息，由 D365 系统同步。

### FastjsonTypeHandler
MyBatis 类型处理器，用于处理 JSON 字段与 Java `Map<String, Object>` 的相互转换，基于阿里巴巴 Fastjson 库。

### FIND_IN_SET
MySQL 函数，在逗号分隔字符串中查找值。PMS-springmvc 中常用于权限过滤，但无法使用索引，性能较差。

### FreeMarker
模板引擎，PMS-springmvc 使用 FreeMarker 渲染邮件模板和 Word 文档。

---

## I

### iBATIS
遗留 ORM 框架，PMS-struts 使用 iBATIS 2.x，PMS-springmvc 使用 MyBatis 3.5.9，两套 ORM 共享同一数据库。详见 [mybatis-ibatis-coexistence.md](../01-architecture/mybatis-ibatis-coexistence.md)。

### IndustryAsset（行业资产）
`af_industry_asset` 表，存储行业客户的安全资产信息（主机、应用系统、数据库等）。

### IndustryLeak（行业漏洞）
`af_industry_leak` 表，存储行业安全漏洞信息，通过 `assetIds` 关联到受影响的资产。

---

## J

### JF（用服）
用服（用户服务）的简称，项目类型 `10`（用服售后）和 `20`（用服售前测试）属于用服业务域。

---

## M

### Maven Profile
Maven 构建配置文件，PMS-springmvc 使用二维 Profile 矩阵：环境维度（dev/test/release）× 版本维度（pms2/pms3）。详见 [profile-mechanism.md](../01-architecture/profile-mechanism.md)。

### MemberRole（成员角色）
项目成员的角色编码，如 `30`（项目经理）、`20`（服务经理）、`80`（质量监督员）等。

### MyBatis
ORM 框架，PMS-springmvc 使用 MyBatis 3.5.9，通过 XML 映射文件定义 SQL。

---

## P

### PageParam
分页参数对象，包含 `model`（查询条件）、`pageNo`（页码）、`pageSize`（每页条数）、`columns`（列配置）等信息。

### PmWorkFlow（工作流业务表）
`pm_workflow` 表，存储业务层工作流信息，通过 `procInstId` 关联到 Activiti 引擎，通过 `dataType` + `dataId` 多态关联到业务数据。

### Principal
Shiro 认证主体，包含用户信息、权限列表等，通过 `UserContext.getCurrentPrincipal()` 获取。

### ProcessKey（流程定义 Key）
Activiti 流程定义的唯一标识，如 `QualityApproveTrack`（质量审批跟踪流程）。

### Profile（环境配置）
Maven Profile 机制，用于管理不同环境（dev/test/release）和不同版本（pms2/pms3）的配置差异。

---

## Q

### QualityApproveTrack（质量审批跟踪）
Activiti 流程定义 Key，用于行业资产、行业漏洞等业务数据的审批流程。

### Quartz
定时任务调度框架，PMS-springmvc 使用 Quartz 调度 `D365DataJob`、`SMSDataJob`、`EhrDataJob` 等定时任务。

---

## R

### RoutingDataSource
Spring 动态数据源路由，基于 `AbstractRoutingDataSource` 实现，PMS-springmvc 通过此机制在 6 个数据源（Local/PMS/SMS/EHR/D365/CRM）之间动态切换。

---

## S

### SEE（结算付款系统）
外部结算付款系统，PMS-springmvc 通过 `DispatchSettlementSEEPaymentJob` 定时同步结算付款数据到 SEE 系统。

### Shiro
Apache Shiro 安全框架，PMS-springmvc 使用 Shiro 1.8.0 实现认证、授权、会话管理。

### SMS（Service Management System）
服务管理系统，PMS-springmvc 通过 `SMSDataJob` 定时从 SMS 同步安服项目属性和产品数据。

### SynchronizeJob
定时同步任务基类，提供同步日志记录、异常处理等通用能力。`D365DataJob`、`SMSDataJob`、`EhrDataJob` 均继承此类。

### SyncLog（同步日志）
`sync_log` 表，记录数据同步任务的执行情况（成功/失败、异常信息等）。

---

## T

### TaskKey（任务节点 Key）
Activiti 流程中任务节点的唯一标识，如 `afApproveTask`（安服质量审核任务）、`trackTask`（任务跟踪任务）。

### TreeNode
树形节点对象，EHR 模块使用 `TreeNodeUtils.constructTreeNodeData()` 构建公司-部门-员工树形结构。

---

## U

### UserContext
用户上下文工具类，提供 `getCurrentPrincipal()`（获取当前用户）、`hasRole()`（角色检查）、`checkPermission()`（权限检查）等方法。

---

## V

### VO（View Object）
视图对象，用于 Controller 层与前端的数据交互，如 `ProjectVO`、`DailyReportVO`、`DispatchVO` 等。VO 通常继承对应的 Entity 类并添加展示用字段。

---

## W

### War Overlay
Maven WAR 插件的覆盖机制，PMS-springmvc 的 `web.xml` 等配置文件继承自父模块 `pms-mvc-core`，通过 overlay 机制合并。

### WorkBench（工作台）
工作台模块，提供待办任务、已办任务、统计信息等功能，`PmWorkBenchMapper` 直接查询 Activiti 引擎表。

---

## 其他

### 逗号分隔字符串
PMS-springmvc 中部分字段（如 `pm_dispatch_project_header.projectIds`、`af_industry_leak.assetIds`）使用逗号分隔的字符串存储多值，无法使用索引，关联查询需在应用层解析。

### 多态外键
PMS-springmvc 普遍使用多态外键关联，如 `pm_workflow.dataType + dataId`、`pm_common_related_data.objType + objId`、`data_field_relation.dataName + dataId`，通过类型字段区分关联的业务表。

### 逻辑删除
PMS-springmvc 所有业务表采用 `disabled` 字段实现逻辑删除，查询时需显式添加 `WHERE disabled = 0` 条件。

### 父子容器
Spring MVC 的父子容器机制，`ContextLoaderListener` 创建父容器（管理 Service、DAO），`DispatcherServlet` 创建子容器（管理 Controller），子容器可以访问父容器的 Bean。
