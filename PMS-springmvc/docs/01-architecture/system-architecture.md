# PMS-springmvc 系统架构总览

## 1. 系统概述

PMS-springmvc 是 PMS 系统中较新的 Spring MVC Web 应用模块，负责项目管理扩展、工作流管理、发运结算、行业资产管理等业务功能。该模块基于 Spring MVC 5.3.19 框架，采用标准 Maven 结构。

### 涉及的 Controller 类列表（20个）

> URL 命名空间以源码 `@RequestMapping` 注解实际值为准（已交叉验证）。

| Controller 类 | URL 命名空间 | 职责 | 方法数 |
|---------------|-------------|------|--------|
| `AbstractController` | - | 控制器基类 | 13 |
| `BaseController` | - | 基础控制器 | 0 |
| `ProjectController` | `/pm/project` | 项目管理 | 16 |
| `ProjectMemberController` | `/pm/member` | 项目成员管理 | 3 |
| `ProjectTaskController` | `/pm/project/task` | 项目任务管理 | 7 |
| `ProjectManageUserController` | `/pm/user` | 项目管理用户 | 12 |
| `ProjectAssetController` | `/pm/project/asset` | 项目资产管理 | 7 |
| `ProjectAssetLeakController` | `/pm/asset/leak` | 项目资产漏洞管理 | 7 |
| `WorkFlowController` | `/workflow` | 工作流管理 | 15 |
| `WorkBenchController` | `/workflow/workbench` | 工作台 | 4 |
| `DailyReportController` | `/pm/daily/report` | 日报管理 | 10 |
| `DispatchProjectController` | `/pm/dispatch` | 发运项目管理 | 13 |
| `DispatchSettlementController` | `/pm/settlement` | 发运结算管理 | 13 |
| `IndustryAssetController` | `/af/industry/asset` | 行业资产管理 | 4 |
| `IndustryLeakController` | `/af/industry/leak` | 行业漏洞管理 | 4 |
| `IndustryLeakWarningController` | `/af/industry/warning` | 行业漏洞预警 | 4 |
| `CommonRelatedDataController` | `/pm/common/related` | 关联数据管理 | 6 |
| `FacilitatorController` | `/pm/facilitator` | 服务商管理 | 2 |
| `StrutsApiController` | `/api` | Struts API 兼容 | 3 |
| `EHRDataController` | `/ehr/` | EHR 数据集成 | 14 |

### 涉及的 Service 类列表（28个）

> 含 springmvc 包 20 个 + ehr 包 8 个。EHR 相关 Service 详见 [EHR 集成模块](../02-modules/ehr-integration.md)。

| Service 接口 | 实现类 | 职责 |
|-------------|--------|------|
| `IProjectService` | `ProjectService` | 项目基础服务 |
| `IProjectHeaderService` | `ProjectHeaderService` | 项目头信息服务 |
| `IProjectMemberService` | `ProjectMemberService` | 项目成员服务 |
| `IProjectTaskService` | `ProjectTaskService` | 项目任务服务 |
| `IProjectManageUserService` | `ProjectManageUserService` | 项目管理用户服务 |
| `IPmWorkFlowService` | `PmWorkFlowService` | 工作流服务 |
| `IPmWorkBenchService` | `PmWorkBenchService` | 工作台服务 |
| `IDailyReportService` | `DailyReportService` | 日报服务 |
| `IDispatchProjectService` | `DispatchProjectService` | 发运项目服务 |
| `IDispatchSettlementService` | `DispatchSettlementService` | 发运结算服务 |
| `IIndustryAssetService` | `IndustryAssetService` | 行业资产服务 |
| `IIndustryLeakService` | `IndustryLeakService` | 行业漏洞服务 |
| `IIndustryLeakWarningService` | `IndustryLeakWarningService` | 行业漏洞预警服务 |
| `IIndustryAssetProjectRelationService` | `IndustryAssetProjectRelationService` | 资产项目关联服务 |
| `IIndustryAssetLeakRelationService` | `IndustryAssetLeakRelationService` | 资产漏洞关联服务 |
| `IExcelAnalysisService` | `ExcelAnalysisService` | Excel分析服务 |
| `ICommonRelatedDataService` | `CommonRelatedDataService` | 关联数据服务 |
| `IFacilitatorService` | `FacilitatorService` | 服务商服务 |
| `IPmSynchronizeService` | `PmSynchronizeService` | 数据同步服务 |
| `IDataFieldRelationService` | `DataFieldRelationService` | 数据字段关联服务 |
| `IEhrCompanyService` | `EhrCompanyService` | EHR 公司服务 |
| `IEhrDepartmentService` | `EhrDepartmentService` | EHR 部门服务 |
| `IEhrEmpPowerService` | `EhrEmpPowerService` | EHR 员工权限服务 |
| `IEHRLoginAccountService` | `EHRLoginAccountService` | EHR 登录账号服务 |
| `IEhrSynchronizeService` | `EhrSynchronizeService` | EHR 数据同步服务 |
| `IEmployeeService` | `EmployeeService` | 员工服务 |
| `IHolidayService` | `HolidayService` | 节假日服务 |
| `IJobService` | `JobService` | 岗位服务 |

### 涉及的数据库表列表（20个）

> 表名以 Mapper XML 实际查询为准（已交叉验证）。`pm_workbench` 表不存在，`PmWorkBenchMapper` 实际查询 `pm_workflow` 表。

| 表名 | 说明 |
|------|------|
| `pm_project` | 项目主表 |
| `pm_project_header` | 项目头信息（视图） |
| `pm_project_member` | 项目成员表 |
| `pm_project_task` | 项目任务表 |
| `pm_project_manage_user` | 项目管理用户表 |
| `pm_workflow` | 工作流数据表 |
| `pm_daily_report` | 日报表 |
| `pm_dispatch_project_header` | 发运项目（派单头表） |
| `pm_dispatch_project_settlement` | 发运结算表 |
| `af_industry_asset` | 行业资产表 |
| `af_industry_leak` | 行业漏洞表 |
| `af_industry_leak_warning` | 行业漏洞预警表 |
| `af_industry_asset_project_relation` | 资产项目关联表 |
| `af_industry_asset_leak_relation` | 资产漏洞关联表 |
| `pm_facilitator` | 服务商表 |
| `pm_common_related_data` | 关联数据表 |
| `pm_project_property_af_from_sms` | SMS 同步暂存表 |
| `ar_report_data_column_mapping` | 报表数据列映射表（ExcelAnalysisMapper） |
| `data_field_relation` | 数据字段关联表 |
| `ehr_login_account` | EHR 登录账号表 |

---

## 2. 技术栈详解

### 2.1 表现层

| 技术 | 版本 | 用途 | 选型理由 |
|------|------|------|----------|
| **Spring MVC** | 5.3.19 | MVC框架 | 注解驱动，RESTful风格 |
| **JSP** | - | 页面模板渲染 | 与Spring MVC集成 |
| **Jackson** | 2.13.1 | JSON序列化 | Spring MVC默认JSON处理 |
| **jQuery** | 2.1.4 | 前端JS库 | DOM操作与AJAX交互 |
| **Bootstrap** | 3.3.4/3.3.7 | UI框架 | 响应式布局 |
| **ECharts** | - | 数据可视化图表 | 报表页面交互式图表展示 |

### 2.2 控制层

| 技术 | 版本 | 用途 | 选型理由 |
|------|------|------|----------|
| **Spring MVC Controller** | 5.3.19 | 请求处理控制器 | @Controller + @RequestMapping |
| **AbstractController** | - | 控制器基类 | 提供通用CRUD操作 |

### 2.3 业务层

| 技术 | 版本 | 用途 | 选型理由 |
|------|------|------|----------|
| **Spring IoC** | 5.3.19 | Bean容器、依赖注入 | 统一管理Service组件 |
| **Spring AOP** | 5.3.19 | 切面编程 | 日志记录、事务管理 |
| **声明式事务** | - | 事务管理 | @Transactional注解 |

### 2.4 持久层

| 技术 | 版本 | 用途 | 选型理由 |
|------|------|------|----------|
| **MyBatis** | 3.5.9 | ORM框架 | SQL映射灵活 |
| **Druid** | 1.2.8 | 数据库连接池 | 监控、性能优化 |
| **动态数据源** | - | 多数据源路由 | RoutingDataSource + @DataSource |

### 2.5 工作流

| 技术 | 版本 | 用途 | 选型理由 |
|------|------|------|----------|
| **Activiti** | 5.23.0 | BPMN流程引擎 | 轻量级工作流引擎 |

### 2.6 安全

| 技术 | 版本 | 用途 | 选型理由 |
|------|------|------|----------|
| **Shiro** | 1.8.0 | 安全框架 | 认证授权 |
| **CAS** | 3.2.2 | 单点登录 | 企业统一认证 |
| **CSRF拦截器** | 自研 | CSRF防护 | Token验证 |
| **密码拦截器** | 自研 | 密码安全 | 定期修改提醒 |

---

## 3. 模块结构

```
PMS-springmvc/
├── src/main/java/com/dp/plat/pms/
│   ├── springmvc/
│   │   ├── constant/          # 常量定义
│   │   ├── controller/        # 控制器（springmvc 包 19 个 + ehr 包 1 个 = 20）
│   │   ├── dao/               # 数据访问层（20个Mapper）
│   │   ├── entity/            # 实体类
│   │   ├── excel/             # Excel处理
│   │   ├── job/               # 定时任务
│   │   ├── listener/          # 事件监听器
│   │   ├── mapping/           # MyBatis XML映射
│   │   ├── service/           # 业务服务接口
│   │   ├── util/              # 工具类
│   │   └── vo/                # 值对象
│   ├── aop/                   # AOP切面
│   └── filter/                # Servlet过滤器
├── src/main/resources/
│   ├── spring.xml             # Spring配置
│   ├── spring-mybatis.xml      # MyBatis配置
│   ├── spring-mvc.xml          # Spring MVC配置
│   ├── spring-shiro-cas.xml    # Shiro+CAS配置
│   ├── spring-pms.xml          # PMS配置
│   ├── spring-activiti.xml     # Activiti配置
│   └── profiles/               # 环境配置
└── pom.xml
```

---

## 4. 多数据源架构

### 4.1 数据源配置

```xml
<!-- 主数据源（Druid连接池） -->
<bean id="dataSourceLocal" class="com.alibaba.druid.pool.DruidDataSource">
    <property name="driverClassName" value="${jdbc.driver}"/>
    <property name="url" value="${jdbc.url}"/>
    <property name="username" value="${jdbc.username}"/>
    <property name="password" value="${jdbc.password}"/>
</bean>

<!-- EHR数据源 -->
<bean id="dataSourceEHR" class="com.alibaba.druid.pool.DruidDataSource">
    <property name="driverClassName" value="${ehr.driver}"/>
    <property name="url" value="${ehr.url}"/>
    <property name="username" value="${ehr.username}"/>
    <property name="password" value="${ehr.password}"/>
</bean>

<!-- 动态数据源路由 -->
<bean id="dataSource" class="com.dp.plat.core.config.RoutingDataSource">
    <property name="targetDataSources">
        <map>
            <entry key="${jdbc.key1}" value-ref="dataSourceLocal"/>
            <entry key="ehr" value-ref="dataSourceEHR"/>
        </map>
    </property>
    <property name="defaultTargetDataSource" ref="dataSourceLocal"/>
</bean>
```

### 4.2 数据源切换机制

```java
// 使用@DataSource注解切换数据源
@DataSource("ehr")
public List<EhrData> queryEhrData() {
    return ehrMapper.selectAll();
}
```

---

## 5. 安全架构

### 5.1 CSRF防护

```xml
<mvc:interceptor>
    <mvc:mapping path="/**"/>
    <bean class="com.dp.plat.security.csrf.CsrfInterceptor"/>
</mvc:interceptor>
```

### 5.2 密码安全

```xml
<mvc:interceptor>
    <mvc:mapping path="/**"/>
    <mvc:exclude-mapping path="/password.*"/>
    <bean class="com.dp.plat.core.interceptor.PasswordInterceptor">
        <property name="redirect" value="/password.html?needChangePwd=true"/>
    </bean>
</mvc:interceptor>
```

### 5.3 XSS防护

```xml
<filter>
    <filter-name>xssFilter</filter-name>
    <filter-class>com.dp.plat.security.xss.XssFilter</filter-class>
</filter>
```

---

## 6. 常量定义

### 6.1 项目类型（ProjectType）

| 常量名 | 值 | 说明 |
|--------|-----|------|
| `AF_SALES_PROJECT` | `afss` | 安服订单项目 |
| `AF_XX_PROJECT` | `afxx` | 安服先行项目 |
| `JF_SALES_PROJECT` | `10` | 用服售后项目 |
| `JF_TEST_PROJECT` | `20` | 用服售前测试 |

### 6.2 项目成员角色（MemberRole）

| 常量名 | 值 | 说明 |
|--------|-----|------|
| `MEMBER_SALESMAN` | `10` | 销售人员 |
| `MEMBER_SM` | `20` | 服务经理 |
| `MEMBER_PM` | `30` | 项目经理 |
| `MEMBER_PARTY` | `40` | 团队成员 |
| `MEMBER_SERVICE_CHANNEL` | `50` | 服务渠道工程师 |
| `MEMBER_CUSTOMER` | `60` | 最终客户 |
| `MEMBER_TECH_MANMER` | `70` | 技术经理 |
| `MEMBER_QC` | `80` | 质量监督员 |

### 6.3 流程类型（ProcessType）

| 常量名 | 值 | 说明 |
|--------|-----|------|
| `QUALITY_APPROVE_TRACK` | `QualityApproveTrack` | 质量审批跟踪流程 |
| `SUBCONTRACT_INSPECTION` | `SubcontractInspection` | 转包检验流程 |

### 6.4 数据类型（DataType）

| 常量名 | 值 | 说明 |
|--------|-----|------|
| `PROJECT` | `project` | 项目 |
| `PROJECT_TASK` | `projectTask` | 项目任务 |
| `PROJECT_OPPORTUNITY` | `projectOpportunity` | 项目机会点 |
| `PROJECT_DISPATCH` | `dispatch` | 项目外派 |
| `DISPATCH_SETTLEMENT` | `settlement` | 项目外派结算 |
| `INDUSTRY_ASSET` | `industryAsset` | 行业资产 |
| `INDUSTRY_LEAK` | `industryLeak` | 行业漏洞 |

### 6.5 角色常量（RoleConstant）

| 常量名 | 值 | 说明 |
|--------|-----|------|
| `ROLE_PM_ADMIN` | `projectAdmin` | 项目管理员 |
| `ROLE_PM_SUB_ADMIN` | `projectSubAdmin` | 子项目管理员 |
| `ROLE_PM_AREA_MANAGER` | `projectAreaManager` | 区域负责人 |
| `ROLE_PM_PROGRAM` | `projectManager` | 项目经理 |
| `ROLE_PM_MEMBER` | `projectMember` | 项目成员 |
| `ROLE_PM_AFQC` | `projectAFQC` | 安服质量监督员 |
| `ROLE_PM_YFQC` | `projectYFQC` | 研发质量监督员 |
| `ROLE_PM_SALES` | `projectSales` | 销售人员 |
| `ROLE_FINANCIAL_AP` | `financialAP` | 财务AP |
| `ROLE_PM_DISPATCH_SETTLE_STAFF` | `dispatchSettleStaff` | 项目外派结算人员 |

---

## 7. 定时任务

| Job 类 | 功能 | 触发方式 |
|--------|------|----------|
| `SMSDataJob` | SMS 数据同步 | Quartz cron |
| `D365DataJob` | D365 数据同步 | Quartz cron |
| `DispatchSettlementInvoiceToFPJob` | 发运结算发票同步 | Quartz cron |
| `DispatchSettlementSEEPaymentJob` | 发运结算付款同步 | Quartz cron |
| `EhrDataJob` | EHR 数据同步 | Quartz cron |

---

## 8. 事件监听器

| Listener 类 | 功能 |
|-------------|------|
| `QualityApproveTrackListener` | 质量审批跟踪监听器 |
| `QualityApproveTrackListener2` | 质量审批跟踪监听器（备选） |
| `SubcontractInspectionListener` | 转包检验监听器 |

---

## 9. AOP 切面

| 切面类 | 功能 |
|--------|------|
| `DispatchSettlementUpdateAspect` | 发运结算更新切面 |
| `ProjectManagementAspect` | 项目管理切面 |

---

## 10. Servlet 过滤器

| 过滤器类 | 功能 |
|----------|------|
| `ExcludeAdminControllerTypeFilter` | 排除管理员类型过滤 |
| `UserCheckFilter` | 用户检查过滤 |

---

## 11. 工具类

| 工具类 | 功能 |
|--------|------|
| `PermissionUtils` | 权限工具 |
| `DocUtil` | 文档工具 |
| `ImageUtil` | 图片工具 |

---

## 12. Maven Profile

| Profile | 用途 |
|---------|------|
| `dev` | 本地开发（默认） |
| `test` | 测试环境 |
| `release` | 生产环境 |
| `pms2` | PMS2 版本（默认构建） |
| `pms3` | PMS3 版本 |
