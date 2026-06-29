# 术语表（Glossary）

> 本术语表收录 PMS / SPMS 项目相关的架构、技术栈、数据库、业务及开发术语，供知识库编写与团队沟通时统一用语。如发现术语缺失或描述有误，请补充修订。

---

## 一、架构术语

| 术语名 | 英文全称 | 中文含义 | 详细说明 |
|--------|----------|----------|----------|
| PMS | Project Management System（项目管理系统） | 项目管理系统 | 本仓库主项目，Maven 多模块（8 个子模块）Java EE 企业应用，JDK 1.8，使用 Git 版本控制。 |
| SPMS | Spare Parts Management System（备件管理系统） | 备件管理系统 | 传统 Eclipse 项目（非 Maven），JavaSE-1.7，使用 SVN，与 PMS 共用 MySQL 数据库 `dppms_d365`。 |
| core | Core Module | 核心模块 | PMS 的基础核心模块，被 PMS-activiti、PMS-struts 等模块依赖，提供公共基础能力。 |
| PMS-struts | PMS Struts2 Module | Struts2 Web 模块 | PMS 的 Web 表现层模块之一，使用 Struts2 2.3.35。源码目录非标准（`src/`），配置在 `config/`，打包为 jar 供 PMS-springmvc 依赖。 |
| PMS-springmvc | PMS Spring MVC Module | Spring MVC Web 模块 | PMS 的 Web 表现层模块，使用 Struts2 2.5.30 + Spring MVC 5.3.19，是最终 WAR 部署模块。 |
| PMS-activiti | PMS Activiti Workflow Module | 工作流模块 | 基于 Activiti 5.23.0 的工作流模块，依赖 core，提供流程定义与审批流转能力。 |
| PMS-ext-d365 | PMS D365 Extension Module | D365 集成扩展模块 | 负责与 D365（Microsoft Dynamics 365）系统集成的扩展模块，依赖 PMS-struts 与 PMS-springmvc。 |
| PMS-security | PMS Security Module | 安全模块 | 基于 Shiro 1.8.0 + CAS 3.2.2 的安全模块，提供认证鉴权能力，依赖 PMS-struts。 |
| pms-rules | PMS Rules Engine Module | 规则引擎模块 | 业务规则引擎模块，依赖 PMS-struts 与 pms-ext-fp，用于动态业务规则计算。 |
| pms-ext-fp | PMS FP Extension Module | FP 扩展模块 | 规则引擎所依赖的功能扩展模块，被 pms-rules 依赖。 |

## 二、技术栈术语

| 术语名 | 英文全称 | 中文含义 | 详细说明 |
|--------|----------|----------|----------|
| Struts2 | Apache Struts 2 | Struts2 Web 框架 | 基于 MVC 的 Java Web 框架。PMS-struts 使用 2.3.35，PMS-springmvc 使用 2.5.30，SPMS 使用 2.3.35，版本不一需注意兼容性。 |
| Spring MVC | Spring Model-View-Controller | Spring MVC 框架 | Spring 提供的 Web MVC 框架，PMS-springmvc 使用 5.3.19 版本。 |
| MyBatis | MyBatis | MyBatis ORM 框架 | 半自动 ORM 框架，PMS 使用 3.5.9。XML 映射文件与 Java 文件同目录（`com/dp/plat/**/mapping/*.xml`）。 |
| iBATIS | iBATIS | iBATIS ORM 框架 | MyBatis 前身。SPMS 使用 iBATIS 2.3.0.677，XML 映射格式为 `<sqlMap>` 而非 `<mapper>`；PMS 中亦有遗留 iBATIS 使用。 |
| Shiro | Apache Shiro | Shiro 安全框架 | Java 安全框架，PMS 使用 1.8.0，提供认证、授权、加密、会话管理。 |
| CAS | Central Authentication Service | 中央认证服务 | 单点登录（SSO）协议与实现，PMS 使用 CAS 3.2.2 与 Shiro 集成实现统一登录。 |
| Activiti | Activiti Workflow Engine | Activiti 工作流引擎 | 轻量级 BPMN 2.0 工作流引擎，PMS 使用 5.23.0，负责审批、流转等业务流程。 |
| Quartz | Quartz Scheduler | Quartz 任务调度框架 | 定时任务调度框架，SPMS 使用 quartz-all-1.8.3，用于定时刷新数据、报表生成等。 |
| Druid | Druid Database Connection Pool | Druid 数据库连接池 | 阿里数据库连接池，PMS 使用 1.2.8，提供监控与 SQL 解析能力。 |
| Aviator | Aviator Expression Engine | Aviator 表达式引擎 | 高性能表达式求值引擎，常用于规则引擎中的动态表达式计算。 |
| LiteFlow | LiteFlow Rule Engine | LiteFlow 规则引擎 | 轻量级规则编排引擎，用于复杂业务规则的流程化编排。 |
| Groovy | Apache Groovy | Groovy 脚本语言 | 运行于 JVM 的动态脚本语言，常用于动态规则脚本与 DSL 编写。 |

## 三、数据库术语

| 术语名 | 英文全称 | 中文含义 | 详细说明 |
|--------|----------|----------|----------|
| dppms_d365 | D365 PMS Database | D365 集成数据库 | PMS 与 D365 系统集成相关的数据库/数据源命名。 |
| dppms_d365 | DP Spare Parts Management Database | 备件管理主数据库 | SPMS 与 PMS 共用的 MySQL 主数据库，存储备件、RMA、出入库等核心业务数据。 |
| RoutingDataSource | Routing Data Source | 动态路由数据源 | Spring 提供的抽象类 `AbstractRoutingDataSource` 的实现，用于在运行时根据上下文动态切换数据源。 |
| DataSourceHolder | Data Source Holder | 数据源上下文持有者 | 配合 RoutingDataSource 使用的工具类，基于 ThreadLocal 保存当前线程的数据源标识，实现多数据源切换。 |
| mysql | MySQL DataSource | MySQL 数据源 | SPMS 主数据源，连接 MySQL `dppms_d365`。 |
| mes | MES DataSource | MES 系统数据源 | SPMS 集成 MES 系统的数据源，SQL Server `R2EMES5SQL`。 |
| sap | SAP DataSource | SAP 系统数据源 | SPMS 集成 SAP 系统的数据源，SQL Server `DIPULive`。 |
| d365 | D365 DataSource | D365 系统数据源 | SPMS 集成 D365 系统的数据源，SQL Server `AXDB`。 |
| sms | SMS DataSource | SMS 系统数据源 | SPMS 集成 SMS 系统的数据源，MySQL `dpsms`。 |

## 四、业务术语

| 术语名 | 英文全称 | 中文含义 | 详细说明 |
|--------|----------|----------|----------|
| RMA | Return Merchandise Authorization | 退货授权 | 客户退货授权流程，涉及退货申请、审批、备件更换、质保等环节，是 SPMS/PMS 核心业务之一。 |
| 备件管理 | Spare Parts Management | 备件管理 | 对备件的申请、出入库、更换、核销、库存盘点等全生命周期管理，SPMS 的核心业务域。 |
| 工作流 | Workflow | 工作流 | 基于 Activiti 的业务流程编排，涵盖审批、流转、节点回退等，用于 RMA、备件申请等审批场景。 |
| 售前 | Pre-sales | 售前业务 | 销售达成前的业务环节，可能涉及备件预申请、方案支持等。 |
| 回访 | Customer Visit / Callback | 客户回访 | 售后对客户进行的服务回访，记录反馈与问题，可能触发问题管理流程。 |
| 转包 | Subcontracting / Package Transfer | 转包业务 | 将业务/项目转包给第三方处理的业务模式，涉及权限与数据隔离。 |
| 问题管理 | Problem Management | 问题管理 | 对售后/回访中发现的问题进行记录、跟踪、分析与解决的闭环管理流程。 |

## 五、开发术语

| 术语名 | 英文全称 | 中文含义 | 详细说明 |
|--------|----------|----------|----------|
| Maven Profile | Maven Profile | Maven 构建配置 | Maven 针对不同环境（dev/test/release）的构建配置。PMS-struts 含 dev/test/release/pms/yfpms/pms2/pms3 等 profile；PMS-springmvc 含 dev/test/release/pms2/pms3。 |
| war+jar | WAR + JAR Packaging | WAR 与 JAR 打包模式 | PMS 多模块中，PMS-struts 等以 jar 形式被 PMS-springmvc（war）依赖，最终聚合为 WAR 部署。 |
| classifier | Maven Classifier | Maven 分类器 | Maven 坐标中用于区分同一构件不同变体的标识（如 sources、javadoc，或不同环境产物），配合 profile 生成多版本构件。 |
| system 作用域依赖 | System Scope Dependency | system 作用域依赖 | Maven 依赖作用域之一，需通过 `systemPath` 显式指定本地 jar 路径。PMS-struts 的 `echarts-utils` 即为 system 作用域，位于 `WebContent/WEB-INF/lib/Utils-v0.1.jar`，不在 Maven 仓库中。 |

---

## 维护说明

- 新增术语时请归入对应分类，保持表格列结构一致。
- 术语命名以项目中实际使用为准，英文全称不确定时标注「（推测）」。
- 业务术语随业务演进需同步更新。

| 版本 | 日期 | 修改人 | 修改内容 |
|------|------|--------|----------|
| v1.0 | 2026-06-24 | 知识库初始化 | 创建术语表初稿，覆盖架构/技术栈/数据库/业务/开发五类术语 |
