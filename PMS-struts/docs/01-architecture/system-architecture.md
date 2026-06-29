# PMS 系统架构总览

## 1. 系统概述

PMS（Project Management System）是迪普科技（DPtech）内部的**项目管理系统**，核心围绕项目全生命周期管理，覆盖从售前立项、项目执行、交付验收、维保回访到项目闭环的完整业务流程。

系统主要功能模块包括：

| 模块 | 说明 |
|------|------|
| 售前项目管理 | 售前流程审批、售前转立项、售前跟踪 |
| 项目管理 | 项目创建/修改/删除、项目成员管理、项目计划 |
| 项目闭环 | 闭环流程审批、测评问卷管理、闭环评价 |
| 回访管理 | 客户回访流程、回访问卷 |
| 项目转包 | 转包流程审批、转包付款管理、转包验收 |
| 项目维保 | 维保管理、维保日报/季报 |
| 项目督查 | 项目执行督查 |
| 维保回访 | 过保提醒、维保回访流程 |
| 技术公告 | 产品问题管理、软件版本管理 |
| 合格证管理 | 产品合格证生成与查询 |
| 数据分析 | 项目数据统计分析、报表展示 |
| 工作流管理 | Activiti流程部署、任务管理、流程委托 |
| 系统管理 | 用户/角色/部门/基础数据管理 |

系统通过多数据源集成 SAP、D365、CRM、OA、EHR、SMS、SSE、ITR 等外部系统，实现企业级数据互通与业务协同。

---

## 2. 技术栈详解

### 2.1 表现层

| 技术 | 版本 | 用途 | 选型理由 |
|------|------|------|----------|
| **Struts2** | 2.3.35 | MVC框架，请求分发与Action映射 | 企业级Java Web标准框架，与Spring集成成熟 |
| **JSP** | - | 页面模板渲染 | 与Struts2天然集成，支持JSTL标签库 |
| **SiteMesh** | 2.x | 页面装饰器，统一布局 | 通过过滤器实现页面布局统一，主页面（main.jsp）和子页面（sub.jsp）装饰 |
| **Velocity** | - | 模板引擎，邮件模板等 | 轻量级模板语言，适合邮件/文档模板生成 |
| **JFreeChart** | 1.5.0 | 图表生成 | Java原生图表库，用于报表数据可视化 |
| **DisplayTag** | 1.2 | 表格分页与导出 | 支持分页、排序、Excel/PDF导出 |
| **jQuery** | 2.1.4 | 前端JS库 | DOM操作与AJAX交互 |
| **Bootstrap** | 3.3.4/3.3.7 | UI框架 | 响应式布局 |
| **ECharts** | - | 数据可视化图表 | 报表页面交互式图表展示 |
| **Select2** | - | 下拉选择增强 | 支持搜索、多选的下拉组件 |
| **Summernote** | - | 富文本编辑器 | 文本内容编辑 |

### 2.2 控制层

| 技术 | 版本 | 用途 | 选型理由 |
|------|------|------|----------|
| **Struts2 Action** | 2.3.35 | 请求处理控制器 | 通过`struts2-spring-plugin`将Action交由Spring容器管理 |
| **Spring IoC** | 5.3.19 | Action Bean托管与依赖注入 | 统一管理Action生命周期，实现控制反转 |

Struts2对象工厂配置为Spring（`struts.objectFactory=spring`），所有Action通过Spring XML配置实例化，支持prototype作用域。

### 2.3 业务层

| 技术 | 版本 | 用途 | 选型理由 |
|------|------|------|----------|
| **Spring IoC** | 5.3.19 | Bean容器、依赖注入 | 统一管理Service/Dao/Action组件 |
| **Spring AOP** | 5.3.19 | 切面编程 | 性能阈值拦截、日志记录、事务管理 |
| **AspectJ** | - | AOP增强 | `@AspectJ`注解驱动的切面 |
| **声明式事务** | - | 事务管理 | 通过`TransactionProxyFactoryBean`代理模式实现声明式事务 |

### 2.4 持久层

| 技术 | 版本 | 用途 | 选型理由 |
|------|------|------|----------|
| **iBatis 2.x** | (ibatis-sqlmap) | 核心ORM框架 | SQL映射灵活，适合复杂查询场景 |
| **MyBatis** | 3.x | 扩展模块ORM | 新增模块采用MyBatis，通过`mybatis-2-spring`桥接 |
| **Spring ORM** | 3.x | ORM集成 | `SqlMapClientTemplate`/`SqlSessionFactoryBean`封装 |
| **DBCP** | - | 数据库连接池 | 主库使用DBCP连接池管理 |
| **P6Spy** | 3.9.1 | SQL日志追踪 | 开发环境SQL监控 |

### 2.5 工作流

| 技术 | 版本 | 用途 | 选型理由 |
|------|------|------|----------|
| **Activiti** | 5.23.0 | BPMN流程引擎 | 轻量级工作流引擎，与Spring无缝集成 |
| **Activiti Spring** | 5.23.0 | Spring集成模块 | 流程引擎由Spring管理生命周期与事务 |

### 2.6 定时任务

| 技术 | 版本 | 用途 | 选型理由 |
|------|------|------|----------|
| **Quartz** | - | 定时任务调度 | 通过`SchedulerFactoryBean`与Spring集成，Cron表达式配置 |

### 2.7 安全

| 技术 | 版本 | 用途 | 选型理由 |
|------|------|------|----------|
| **CAS SSO** | - | 单点登录认证 | 企业统一认证中心（cas2.dptech.com:8443） |
| **Spring Security** | 3.1.x | 访问控制 | URL级别的权限拦截与角色控制 |
| **XSS拦截器** | 自研 | XSS防护 | Struts2拦截器实现请求参数过滤 |

### 2.8 数据库

| 数据库 | 用途 | 连接方式 |
|--------|------|----------|
| **MySQL** | PMS主库，存储核心业务数据 | DBCP连接池（主数据源） |
| **SQL Server** | SAP/D365/CRM/OA/EHR/SMS/SSE 外部系统数据源 | DriverManagerDataSource（独立SqlMapClient） |
| **PostgreSQL** | ITR系统数据源 | 独立SqlMapClient配置 |

### 2.9 其他组件

| 技术 | 版本 | 用途 |
|------|------|------|
| **FastJSON** | - | JSON序列化/反序列化 |
| **Jackson** | - | JSON处理（多版本并存） |
| **POI** | - | Excel导入导出 |
| **JavaMail** | - | 邮件发送 |
| **Log4j/Log4j2** | - | 日志框架 |
| **EhCache** | - | 数据缓存 |
| **Jsoup** | - | HTML解析（XSS过滤） |
| **FreeMarker** | - | 文档模板（合格证/过保提醒函） |

---

## 3. 分层架构图

```
┌─────────────────────────────────────────────────────────────────────┐
│                          客户端（Browser）                           │
│                    HTML / CSS / JS / jQuery / Bootstrap              │
└──────────────────────────────┬──────────────────────────────────────┘
                               │ HTTP Request
                               ▼
┌─────────────────────────────────────────────────────────────────────┐
│                          Web 容器（Tomcat）                          │
│                                                                     │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │                      过滤器链（Filter Chain）                   │  │
│  │                                                               │  │
│  │  ┌─────────┐ ┌─────────┐ ┌──────────┐ ┌───────────────────┐  │  │
│  │  │Encoding │ │  CAS    │ │UserCheck │ │ Struts2 Prepare   │  │  │
│  │  │ Filter  │ │ SSO     │ │ Filter   │ │ Filter            │  │  │
│  │  └─────────┘ └─────────┘ └──────────┘ └─────────┬─────────┘  │  │
│  │                                                  │            │  │
│  │                                    ┌─────────────▼──────────┐ │  │
│  │                                    │   SiteMesh Filter      │ │  │
│  │                                    │  (页面装饰/布局统一)    │ │  │
│  │                                    └─────────────┬──────────┘ │  │
│  │                                                  │            │  │
│  │                                    ┌─────────────▼──────────┐ │  │
│  │                                    │  Struts2 Execute       │ │  │
│  │                                    │  Filter                │ │  │
│  │                                    └─────────────┬──────────┘ │  │
│  └─────────────────────────────────────────────────┼────────────┘  │
│                                                    │               │
│  ┌─────────────────────────────────────────────────▼────────────┐  │
│  │                    表现层（Presentation Layer）                │  │
│  │                                                              │  │
│  │   ┌──────────┐  ┌──────────┐  ┌───────────┐  ┌───────────┐  │  │
│  │   │   JSP    │  │ Velocity │  │JFreeChart │  │DisplayTag │  │  │
│  │   │  Pages   │  │ Template │  │  Charts   │  │  Tables   │  │  │
│  │   └────┬─────┘  └──────────┘  └───────────┘  └───────────┘  │  │
│  └────────┼─────────────────────────────────────────────────────┘  │
│           │                                                        │
│  ┌────────▼─────────────────────────────────────────────────────┐  │
│  │                    控制层（Controller Layer）                  │  │
│  │                                                              │  │
│  │   ┌──────────────────────────────────────────────────────┐   │  │
│  │   │              BaseAction (Struts2 ActionSupport)       │   │  │
│  │   │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────┐  │   │  │
│  │   │  │Project   │ │Presales  │ │CallBack  │ │Subcon- │  │   │  │
│  │   │  │Action    │ │Action    │ │Action    │ │tract   │  │   │  │
│  │   │  └──────────┘ └──────────┘ └──────────┘ └────────┘  │   │  │
│  │   │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────┐  │   │  │
│  │   │  │PmClosed  │ │Prob      │ │Mainte-   │ │WorkFlow│  │   │  │
│  │   │  │LoopAction│ │Action    │ │nanceAct. │ │Action  │  │   │  │
│  │   │  └──────────┘ └──────────┘ └──────────┘ └────────┘  │   │  │
│  │   └──────────────────────────────────────────────────────┘   │  │
│  │              ▲ Spring IoC 托管 (prototype scope)              │  │
│  └──────────────┼───────────────────────────────────────────────┘  │
│                 │                                                  │
│  ┌──────────────▼───────────────────────────────────────────────┐  │
│  │                    业务层（Service Layer）                     │  │
│  │                                                              │  │
│  │   ┌──────────────────────────────────────────────────────┐   │  │
│  │   │  BaseService / BaseServiceImpl (错误/警告消息机制)     │   │  │
│  │   │                                                      │   │  │
│  │   │  ┌────────────┐  ┌────────────┐  ┌──────────────┐   │   │  │
│  │   │  │xxxService  │  │xxxService  │  │xxxService    │   │   │  │
│  │   │  │(业务实现)   │  │(业务实现)   │  │(业务实现)     │   │   │  │
│  │   │  └─────┬──────┘  └─────┬──────┘  └──────┬───────┘   │   │  │
│  │   └────────┼───────────────┼─────────────────┼───────────┘   │  │
│  │            │               │                 │               │  │
│  │   ┌────────▼───────────────▼─────────────────▼───────────┐   │  │
│  │   │  TransactionProxyFactoryBean (声明式事务代理)          │   │  │
│  │   │  xxxServiceAgent → 代理 xxxService 事务边界           │   │  │
│  │   └──────────────────────────────────────────────────────┘   │  │
│  │                                                              │  │
│  │   ┌──────────────────────────────────────────────────────┐   │  │
│  │   │  AOP 拦截器 (PreformanceThresholdInterceptor)         │   │  │
│  │   │  BeanNameAutoProxyCreator → 拦截所有 *Service Bean    │   │  │
│  │   └──────────────────────────────────────────────────────┘   │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                 │                                                  │
│  ┌──────────────▼───────────────────────────────────────────────┐  │
│  │                    持久层（DAO Layer）                         │  │
│  │                                                              │  │
│  │   ┌──────────────────────────────────────────────────────┐   │  │
│  │   │  BaseDao (SqlMapClientTemplate / 操作日志)             │   │  │
│  │   │                                                      │   │  │
│  │   │  ┌────────────┐  ┌────────────┐  ┌──────────────┐   │   │  │
│  │   │  │xxxDao      │  │xxxDao      │  │xxxDao        │   │   │  │
│  │   │  │(iBatis实现) │  │(iBatis实现) │  │(MyBatis实现)  │   │   │  │
│  │   │  └────────────┘  └────────────┘  └──────────────┘   │   │  │
│  │   └──────────────────────────────────────────────────────┘   │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                 │                                                  │
│  ┌──────────────▼───────────────────────────────────────────────┐  │
│  │                    数据源层（DataSource Layer）                │  │
│  │                                                              │  │
│  │   ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────┐   │  │
│  │   │  MySQL   │ │SQL Server│ │SQL Server│ │  PostgreSQL  │   │  │
│  │   │  (主库)   │ │  (SAP)   │ │(D365/CRM │ │    (ITR)     │   │  │
│  │   │          │ │          │ │/OA/EHR/  │ │              │   │  │
│  │   │          │ │          │ │ SMS/SSE) │ │              │   │  │
│  │   └──────────┘ └──────────┘ └──────────┘ └──────────────┘   │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                    │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                    横切关注点（Cross-cutting Concerns）        │  │
│  │                                                              │  │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────────────┐  │  │
│  │  │Activiti  │ │ Quartz   │ │  CAS SSO │ │ Spring Security│  │  │
│  │  │工作流引擎 │ │定时任务   │ │ 单点登录  │ │   访问控制     │  │  │
│  │  └──────────┘ └──────────┘ └──────────┘ └────────────────┘  │  │
│  └──────────────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────────┘
```

---

## 4. 核心设计模式

### 4.1 模板方法模式（Template Method）

项目在 Action / Service / Dao 三层均采用了模板方法模式，通过基类定义公共行为骨架，子类实现具体业务逻辑。

**BaseAction** — 所有Action的基类

```
BaseAction extends ActionSupport
  ├── 实现 ServletRequestAware / ServletResponseAware / ServletContextAware
  ├── 统一错误消息处理（errmsg / warnmsg）
  ├── 默认入口方法 start() → 返回 INPUT
  └── 子类：ProjectAction, PresalesAction, CallBackAction, ...
```

**BaseService / BaseServiceImpl** — 所有Service的基类与接口

```
BaseService (接口)
  ├── getErrmsg() / addErrmsg()     — 错误消息收集
  ├── getWarnmsg() / addWarnmsg()   — 警告消息收集
  ├── isError() / isWarn()          — 状态判断
  ├── clearErrMsg()                 — 消息清理
  ├── setUserContext() / getUserContext() — 用户上下文
  └── getLoginName() / getRealname() — 用户信息

BaseServiceImpl (实现)
  └── 子类：ProjectServiceImpl, PresalesServiceImpl, CallBackServiceImpl, ...
```

**BaseDao** — 所有Dao的基类

```
BaseDao
  ├── sqlMapClientTemplate         — 主库SQL操作模板
  ├── sqlMapClientTemplateSAP      — SAP数据源模板
  ├── sqlMapClientTemplateERP      — ERP/D365数据源模板
  ├── sqlMapClientTemplateSSE      — SSE数据源模板
  ├── opLoggerDao                  — 操作日志记录
  ├── getCurrUsername()            — 获取当前用户
  └── 子类：ProjectDaoImpl, PresalesDaoImpl, CallBackDaoImpl, ...
```

### 4.2 代理模式（Proxy Pattern）

系统通过 **TransactionProxyFactoryBean** 实现Service层的事务代理，每个Service Bean均配置了对应的Agent代理Bean：

```
┌──────────────────┐       ┌──────────────────┐       ┌──────────────────┐
│   Action 层       │       │   代理层          │       │   Service 层      │
│                  │       │                  │       │                  │
│ projectService   │──────▶│ projectService   │──────▶│ projectService   │
│     (引用)       │       │    Agent         │       │    (真实实现)     │
│                  │       │ (事务代理)        │       │                  │
└──────────────────┘       └──────────────────┘       └──────────────────┘
                                │
                                ▼
                       ┌──────────────────┐
                       │ transactionBase  │
                       │ Service (抽象)    │
                       │ PROPAGATION_     │
                       │ REQUIRED         │
                       │ insert*/update*/ │
                       │ delete*/save*/   │
                       │ add*/do*/        │
                       │ parse*/keep*/    │
                       │ start*/submit*   │
                       └──────────────────┘
```

事务传播规则定义在 `transactionBaseService` 抽象Bean中，匹配以下方法名前缀自动应用事务：

| 方法前缀 | 事务传播行为 |
|----------|-------------|
| `insert*` | PROPAGATION_REQUIRED |
| `update*` | PROPAGATION_REQUIRED |
| `delete*` | PROPAGATION_REQUIRED |
| `save*` | PROPAGATION_REQUIRED |
| `add*` | PROPAGATION_REQUIRED |
| `do*` | PROPAGATION_REQUIRED |
| `parse*` | PROPAGATION_REQUIRED |
| `keep*` | PROPAGATION_REQUIRED |
| `start*` | PROPAGATION_REQUIRED |
| `submit*` | PROPAGATION_REQUIRED |

此外，`BeanNameAutoProxyCreator` 对所有 `*Service` Bean 应用 `PreformanceThresholdInterceptor` 性能阈值拦截器，实现方法级性能监控。

### 4.3 观察者模式（Observer Pattern）

Activiti工作流引擎通过**事件监听器**实现观察者模式，在流程节点事件触发时执行业务逻辑：

```
┌─────────────────────────────────────────────────────────────┐
│              Activiti 流程引擎事件监听机制                     │
│                                                             │
│  UnifyTaskBPMNParserHandler (全局BPMN解析处理器)              │
│  ├── UnifyTaskBPMNParser → UnifyTaskListener                │
│  │   ├── create 事件      → 创建统一待办                     │
│  │   ├── assignment 事件   → 任务分配通知                     │
│  │   ├── complete 事件     → 完成待办更新                     │
│  │   ├── delete 事件       → 删除待办                        │
│  │   ├── ENTITY_ACTIVATED  → 流程激活                        │
│  │   └── ENTITY_SUSPENDED  → 流程挂起                        │
│  │                                                          │
│  └── UnifyTaskBPMNParser → SubcontractInspectionListener     │
│      ├── create 事件      → 转包验收创建                     │
│      ├── assignment 事件   → 转包验收分配                     │
│      ├── complete 事件     → 转包验收完成                     │
│      └── delete 事件       → 转包验收删除                     │
│                                                             │
│  待办推送发送器（UnifyTaskSender）:                            │
│  └── UnifyTask2SeeyonSender → 推送到致远OA统一待办             │
└─────────────────────────────────────────────────────────────┘
```

### 4.4 工厂模式（Factory Pattern）

系统大量使用Spring的**Bean工厂模式**，通过XML配置或注解扫描管理所有组件的创建与依赖注入：

| 工厂类型 | 配置方式 | 说明 |
|----------|---------|------|
| Spring XML Bean Factory | `applicationContext-*.xml` | Action/Service/Dao 均通过XML声明式配置 |
| Component Scan | `<context:component-scan>` | `com.dp.plat` 包下注解自动扫描 |
| Activiti Service Factory | `ProcessEngineFactoryBean` | 通过工厂方法获取RepositoryService/RuntimeService等 |
| MyBatis Mapper Scanner | `MapperScannerConfigurer` | 自动扫描 `com.dp.plat.pms.**.dao` 下的Mapper接口 |

Activiti核心服务通过工厂方法从ProcessEngine获取：

```
ProcessEngineFactoryBean
  ├── getRepositoryService()  → 流程定义管理
  ├── getRuntimeService()     → 流程实例管理
  ├── getTaskService()        → 任务管理
  ├── getHistoryService()     → 历史记录查询
  ├── getFormService()        → 表单服务
  └── getIdentityService()    → 身份管理
```

### 4.5 DAO模式（Data Access Object）

系统严格遵循DAO模式，每个业务模块均有独立的Dao接口与实现：

```
┌────────────────────────────────────────────────────────────┐
│                    DAO 分层结构                              │
│                                                            │
│  BaseDao (抽象基类)                                         │
│  ├── sqlMapClientTemplate      — MySQL主库                  │
│  ├── sqlMapClientTemplateSAP   — SAP数据源                  │
│  ├── sqlMapClientTemplateERP   — D365数据源                 │
│  ├── sqlMapClientTemplateSSE   — SSE数据源                  │
│  └── opLoggerDao               — 操作日志                   │
│                                                            │
│  具体Dao实现（均直接继承 BaseDao）:                          │
│  ├── LoginDaoImpl          (extends BaseDao)                │
│  ├── ProjectDaoImpl        (extends BaseDao)                │
│  ├── PresalesDaoImpl       (extends BaseDao)                │
│  ├── CallBackDaoImpl       (extends BaseDao)                │
│  ├── SubcontractDaoImpl    (extends BaseDao)                │
│  ├── ProbManageDaoImpl     (extends BaseDao)                │
│  └── ...                                                   │
│                                                            │
│  ⚠️ 修正：早期文档虚构了 baseContextLoggerDao 抽象中间层     │
│      （声称 LoginDaoImpl 继承该层），源码中该类不存在，       │
│      所有 DAO 实现均直接继承 BaseDao                        │
│                                                            │
│  MyBatis扩展模块:                                           │
│  ├── AbstractBaseMapper     — MyBatis Mapper基类             │
│  └── MapperScannerConfigurer — 自动扫描Mapper接口             │
└────────────────────────────────────────────────────────────┘
```

---

## 5. 系统部署架构

### 5.1 部署拓扑图

```
┌─────────────────────────────────────────────────────────────────────┐
│                         用户浏览器                                    │
└──────────────────────────────┬──────────────────────────────────────┘
                               │ HTTPS
                               ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    CAS 认证服务器                                      │
│                  cas2.dptech.com:8443                                 │
│                  (单点登录认证中心)                                     │
└──────────────────────────────┬──────────────────────────────────────┘
                               │ SSO Ticket 校验
                               ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    Web 应用服务器                                      │
│                      Tomcat                                           │
│                  (PMS WAR 部署)                                       │
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │  PMS Application (pms-struts.war)                            │   │
│  │                                                              │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌───────────────┐  │   │
│  │  │ Struts2  │ │ Spring   │ │ Activiti │ │   Quartz      │  │   │
│  │  │ Actions  │ │ Services │ │ Engine   │ │  Scheduler    │  │   │
│  │  └──────────┘ └──────────┘ └──────────┘ └───────────────┘  │   │
│  └──────────────────────────────────────────────────────────────┘   │
└────────┬──────────────┬──────────────┬──────────────┬───────────────┘
         │              │              │              │
         ▼              ▼              ▼              ▼
   ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────┐
   │  MySQL   │  │SQL Server│  │SQL Server│  │ PostgreSQL   │
   │  (主库)   │  │  (SAP)   │  │(D365/CRM │  │   (ITR)      │
   │          │  │          │  │ /OA/EHR/ │  │              │
   │ PMS业务  │  │ 合同订单  │  │ SMS/SSE) │  │ IT运维数据   │
   │ 数据     │  │ 数据      │  │ 外部数据  │  │              │
   └──────────┘  └──────────┘  └──────────┘  └──────────────┘
```

### 5.2 外部系统集成点

| 外部系统 | 数据库 | 集成方式 | 数据内容 | 定时任务 |
|----------|--------|---------|---------|---------|
| **SAP** | SQL Server | iBatis独立SqlMapClient | 合同订单数据 | `GainOrderBySAP` (每日23:50) |
| **D365** | SQL Server | iBatis独立SqlMapClient + pms-ext-d365模块 | D365业务数据 | `SubcontractPaymentAutoComplete` |
| **CRM** | SQL Server | iBatis独立SqlMapClient + crm-util模块 | 客户关系数据 | `PullJobFromCRM` / `PushJobToCRM` |
| **OA (致远)** | SQL Server | iBatis独立SqlMapClient + 统一待办推送 | 人员信息/统一待办 | `GainPersonByOA` (每日22:30) |
| **EHR** | SQL Server | iBatis独立SqlMapClient | 人员信息 | `GainPersonByEHR` |
| **SMS** | SQL Server | iBatis独立SqlMapClient | 市场关系/售前信息/项目属性 | `TaskBySMS` (每日23:30) |
| **SSE** | SQL Server | iBatis独立SqlMapClient | 付款信息 | `SubcontractPaymentAutoComplete` |
| **ITR** | PostgreSQL | iBatis独立SqlMapClient | IT运维数据 | `GainDataFromITR` |
| **FP (发票)** | - | erms-plugin模块 | 电子档案/发票 | `SubcontractInvoiceToFP` |
| **邮件系统** | - | JavaMail (mail.dptech.com:25) | 业务通知邮件 | 多个Mailer定时任务 |

### 5.3 Maven 多环境构建

系统通过Maven Profile支持多环境构建：

| Profile | 环境 | 说明 |
|---------|------|------|
| `dev` | 开发环境 | 默认激活，含P6Spy SQL日志 |
| `test` | 测试环境 | 独立JDBC配置 |
| `release` | 生产环境 | 独立JDBC与Web配置 |
| `pms` | PMS标准版 | 默认激活 |
| `yfpms` | YFPMS版 | 独立配置与Web上下文 |
| `pms2` / `pms3` | 多实例部署 | 不同上下文路径 |

---

## 6. 项目目录结构说明

```
PMS-struts/
│
├── WebContent/                          # Web应用根目录
│   ├── META-INF/                        # 元数据配置
│   ├── WEB-INF/
│   │   ├── lib/                         # 第三方JAR包 (Utils-v0.1.jar等)
│   │   ├── web.xml                      # Web应用部署描述符 (开发环境)
│   │   ├── web-s.xml                    # Web应用部署描述符 (生产环境)
│   │   ├── web-f.xml                    # Web应用部署描述符 (备用)
│   │   ├── decorators.xml               # SiteMesh装饰器配置
│   │   ├── velocity.properties          # Velocity模板引擎配置
│   │   ├── dp.tld                       # 自定义标签库描述符
│   │   └── struts-tags.tld              # Struts2标签库描述符
│   ├── css/                             # 样式表
│   │   ├── common.css                   # 全局公共样式
│   │   ├── style.css                    # 主样式
│   │   └── prob/                        # 技术公告模块样式
│   ├── images/                          # 图片资源
│   ├── js/                              # JavaScript脚本
│   │   ├── common.js                    # 全局公共JS
│   │   ├── jquery-2.1.4.min.js          # jQuery库
│   │   ├── echarts-all.js               # ECharts图表库
│   │   ├── bootstrap-3.3.7.min.js       # Bootstrap框架
│   │   └── plugins/                     # 插件 (Select2等)
│   ├── plat/                            # SiteMesh装饰页面
│   │   └── common/
│   │       ├── header.jsp               # 页面头部装饰器
│   │       ├── footer.jsp               # 页面底部装饰器
│   │       ├── main.jsp                 # 主布局装饰器
│   │       └── sub.jsp                  # 子页面装饰器
│   ├── sys/                             # 业务JSP页面
│   │   ├── base/                        # 系统管理页面 (用户/角色/部门)
│   │   ├── callback/                    # 回访管理页面
│   │   ├── component/                   # 组件管理页面
│   │   ├── module/                      # 项目管理页面
│   │   ├── presales/                    # 售前管理页面
│   │   ├── prob/                        # 技术公告页面
│   │   ├── report/                      # 报表统计页面
│   │   └── sub/                         # 公共子页面
│   ├── work/                            # 工作流管理页面
│   ├── certificate/                     # 合格证管理页面
│   ├── template/                        # 模板文件 (周报模板等)
│   ├── swfupload/                       # 文件上传组件
│   ├── multiselect/                     # 多选下拉组件
│   └── statics/                         # 静态资源 (Layui等)
│
├── config/                              # 通用配置文件
│   ├── config.properties                # 系统配置参数
│   ├── system.properties                # 系统级属性
│   ├── jdbc_dev.properties              # 开发环境JDBC配置
│   ├── jdbc_release.properties          # 生产环境JDBC配置
│   ├── log4j.properties                 # Log4j日志配置
│   ├── log4j2.xml                       # Log4j2日志配置
│   ├── ehcache.xml                      # EhCache缓存配置
│   ├── mailConfig.properties            # 邮件服务配置
│   ├── struts.xml                       # Struts2核心配置
│   ├── struts-sys.xml                   # Struts2模块配置
│   ├── beans-quartz.xml                 # Quartz定时任务配置
│   ├── displaytag_*.properties          # DisplayTag国际化
│   ├── xwork-conversion.properties      # Struts2类型转换器配置
│   └── profiles/                        # 多环境配置
│       ├── dev/                         # 开发环境 (jdbc.properties, web.xml等)
│       ├── test/                        # 测试环境
│       ├── release/                     # 生产环境
│       ├── pms/                         # PMS标准版配置
│       └── yfpms/                       # YFPMS版配置
│
├── config-spring/                       # Spring配置文件
│   ├── applicationContext.xml           # Spring主配置 (数据源/事务/导入)
│   ├── applicationContext-common.xml    # 公共配置 (左菜单/布局)
│   ├── applicationContext-context.xml   # 上下文配置 (UserContext/SystemContext)
│   ├── applicationContext-dao.xml       # DAO层Bean配置
│   ├── applicationContext-service.xml   # Service层Bean配置 (含事务代理)
│   ├── applicationContext-action.xml    # Action层Bean配置
│   ├── applicationContext-security.xml  # Spring Security安全配置
│   ├── activiti-context.xml             # Activiti工作流配置
│   └── spring-extend-mybatis.xml        # MyBatis扩展模块配置
│
├── config-ibaits/                       # iBatis SQL映射配置
│   ├── sql-map-config.xml               # 主库SQL映射总配置
│   ├── sql-map-admin-config.xml         # 系统管理SQL映射
│   ├── sql-map-project-config.xml       # 项目管理SQL映射
│   ├── sql-map-project-common-config.xml # 项目公共SQL映射
│   ├── sql-map-presales-config.xml      # 售前管理SQL映射
│   ├── sql-map-callback-config.xml      # 回访管理SQL映射
│   ├── sql-map-prob-config.xml          # 技术公告SQL映射
│   ├── sql-map-subcontract-config.xml   # 项目转包SQL映射
│   ├── sql-map-work-config.xml          # 工作流SQL映射
│   ├── sql-map-activity-config.xml      # 活动流程SQL映射
│   ├── sql-map-report-config.xml        # 报表统计SQL映射
│   ├── sql-map-certificate-config.xml   # 合格证SQL映射
│   ├── sql-map-maintenance-config.xml   # 维保管理SQL映射
│   ├── sql-map-warrantyCallback-config.xml # 维保回访SQL映射
│   ├── sqlMapConfigSAP.xml              # SAP数据源SQL映射
│   ├── sqlMapConfigD365.xml             # D365数据源SQL映射
│   ├── sqlMapConfigCRM.xml              # CRM数据源SQL映射
│   ├── sqlMapConfigOA.xml               # OA数据源SQL映射
│   ├── sqlMapConfigEHR.xml              # EHR数据源SQL映射
│   ├── sqlMapConfigSMS.xml              # SMS数据源SQL映射
│   ├── sqlMapConfigSSE.xml              # SSE数据源SQL映射
│   ├── sqlMapConfigITR.xml              # ITR数据源SQL映射
│   └── sql-map-refresh-data-*-config.xml # 各外部系统数据刷新SQL映射
│
├── bpmn/                                # BPMN流程定义文件
│   ├── Presales.bpmn                    # 售前流程定义
│   ├── CallBack.bpmn                    # 回访流程定义
│   ├── PmClosedLoop.bpmn               # 项目闭环流程定义
│   ├── Subcontract.bpmn                # 项目转包流程定义
│   └── SubcontractCallBack.bpmn        # 转包回访流程定义
│
├── src/com/dp/plat/                     # Java源代码
│   ├── action/                          # Struts2 Action控制器
│   │   ├── BaseAction.java              # Action基类
│   │   ├── LoginAction.java             # 登录控制器
│   │   ├── ProjectAction.java           # 项目管理控制器
│   │   ├── PresalesAction.java          # 售前管理控制器
│   │   ├── CallBackAction.java          # 回访管理控制器
│   │   ├── PmClosedLoopAction.java      # 项目闭环控制器
│   │   ├── WorkFlowAction.java          # 工作流管理控制器
│   │   └── ...                          # 其他Action
│   │
│   ├── service/                         # 业务逻辑层
│   │   ├── BaseService.java             # Service接口
│   │   ├── BaseServiceImpl.java         # Service基类实现
│   │   ├── ProjectService/Impl.java     # 项目管理服务
│   │   ├── PresalesService/Impl.java    # 售前管理服务
│   │   ├── WorkFlowService/Impl.java    # 工作流服务
│   │   └── ...                          # 其他Service
│   │
│   ├── dao/                             # 数据访问层
│   │   ├── BaseDao.java                 # DAO基类
│   │   ├── ProjectDao/Impl.java         # 项目管理DAO
│   │   ├── PresalesDao/Impl.java        # 售前管理DAO
│   │   ├── WorkflowDao/Impl.java        # 工作流DAO
│   │   └── ...                          # 其他DAO
│   │
│   ├── data/                            # 数据模型
│   │   ├── bean/                        # 业务实体Bean
│   │   │   ├── BaseBean.java            # 实体基类
│   │   │   ├── Project.java             # 项目实体
│   │   │   ├── Presales.java            # 售前实体
│   │   │   ├── User.java                # 用户实体
│   │   │   └── ...                      # 其他实体
│   │   ├── activity/                    # 工作流相关实体
│   │   ├── report/                      # 报表相关 (EchartsFactory等)
│   │   └── vo/                          # 视图对象 (VO)
│   │
│   ├── context/                         # 上下文管理
│   │   ├── UserContext.java             # 用户会话上下文 (session scope)
│   │   ├── SpringContext.java           # Spring应用上下文
│   │   ├── SystemContext.java           # 系统级上下文
│   │   └── HttpContext.java             # HTTP请求上下文
│   │
│   ├── param/                           # 请求参数对象
│   │   ├── ActionParam.java             # Action参数
│   │   ├── ProjectParam.java            # 项目参数
│   │   ├── DisplayParam.java            # 分页显示参数
│   │   └── ...                          # 其他参数
│   │
│   ├── interceptor/                     # Struts2拦截器
│   │   ├── MyInterceptor.java           # 自定义拦截器
│   │   ├── PasswordInterceptor.java     # 密码拦截器
│   │   └── PreformanceThresholdInterceptor.java  # 性能阈值拦截器
│   │
│   ├── aop/                             # AOP切面
│   │   ├── XMLAdvice.java               # XML配置式切面
│   │   └── annotation/                  # 注解式切面
│   │
│   ├── job/                             # Quartz定时任务
│   │   ├── AbstractSynchronizeTask.java  # 同步任务基类
│   │   ├── GainOrderBySAP.java          # SAP订单同步
│   │   ├── GainPersonByEHR.java         # EHR人员同步
│   │   ├── Mailer.java                  # 邮件发送任务
│   │   └── ...                          # 其他定时任务
│   │
│   ├── taskHandler/                     # Activiti任务处理器
│   │   ├── AutoTaskHandler.java         # 自动任务处理
│   │   ├── CallBackTaskHandler.java     # 回访任务处理
│   │   ├── PresalesClosedTaskHandler.java  # 售前关闭任务处理
│   │   └── ProjectCloseTaskHandler.java    # 项目关闭任务处理
│   │
│   ├── decorators/                      # DisplayTag装饰器
│   │   ├── PresalesDecorator.java       # 售前数据装饰
│   │   ├── AmountThousandth.java        # 金额千分位格式化
│   │   └── ...                          # 其他装饰器
│   │
│   ├── tags/                            # 自定义JSP标签
│   │   ├── LeftMenuTag.java             # 左菜单标签
│   │   ├── PermissionTag.java           # 权限标签
│   │   ├── BarPercentTag.java           # 进度条标签
│   │   └── ...                          # 其他标签
│   │
│   ├── support/                         # 辅助支撑类
│   │   ├── LeftMenu.java                # 左菜单配置
│   │   ├── LeftMenuGroup.java           # 菜单分组
│   │   └── LeftMenuLi.java              # 菜单项
│   │
│   ├── util/                            # 工具类
│   │   ├── MailUtil.java                # 邮件工具
│   │   ├── DateUtil.java                # 日期工具
│   │   ├── Md5Util.java                 # MD5加密工具
│   │   ├── Constant.java                # 常量定义
│   │   └── ...                          # 其他工具
│   │
│   ├── type/                            # 自定义类型
│   │   ├── DateTime.java                # 日期时间类型
│   │   ├── ChartUtil.java               # 图表工具
│   │   └── Validation.java              # 校验工具
│   │
│   ├── converter/                       # Struts2类型转换器
│   │   ├── BooleanConverter.java        # 布尔转换器
│   │   └── StandParamConverter.java     # 标准参数转换器
│   │
│   ├── ibatis/                          # iBatis扩展
│   │   ├── cache/                       # 缓存控制器 (LRUCacheController)
│   │   └── handler/                     # 类型处理器 (FastjsonTypeHandler等)
│   │
│   ├── extend/                          # 扩展模块
│   │   ├── crm/                         # CRM集成 (同步任务/推送/拉取)
│   │   ├── erms/                        # 电子档案集成 (发票推送)
│   │   └── mybatis/                     # MyBatis扩展
│   │       ├── dao/AbstractBaseMapper.java   # Mapper基类
│   │       └── service/                      # Service基类
│   │
│   ├── prob/                            # 技术公告模块
│   │   ├── action/                      # 控制器
│   │   ├── bean/                        # 实体
│   │   ├── dao/                         # DAO
│   │   ├── service/                     # Service
│   │   ├── version/                     # 版本解析策略 (策略模式)
│   │   └── util/                        # 工具类
│   │
│   ├── subcontract/                     # 项目转包模块
│   │   ├── action/                      # 控制器
│   │   ├── dao/                         # DAO
│   │   ├── service/                     # Service
│   │   ├── entity/                      # 实体
│   │   ├── listener/                    # Activiti事件监听器
│   │   ├── quartz/                      # 定时任务
│   │   └── decorators/                  # 装饰器
│   │
│   ├── maintenance/                     # 项目维保模块
│   │   ├── action/                      # 控制器
│   │   ├── aop/                         # AOP切面 (ProjectStateUpdateAspect)
│   │   ├── entity/                      # 实体
│   │   ├── quartz/                      # 定时任务
│   │   └── decorators/                  # 装饰器
│   │
│   ├── supervision/                     # 项目督查模块
│   │   ├── action/                      # 控制器
│   │   ├── entity/                      # 实体
│   │   └── decorators/                  # 装饰器
│   │
│   ├── plus/                            # 增强功能模块
│   │   ├── certificate/                 # 合格证管理
│   │   │   ├── action/                  # 控制器
│   │   │   ├── dao/                     # DAO
│   │   │   └── service/                 # Service
│   │   └── unifytask/                   # 统一待办推送
│   │       ├── listener/                # 事件监听器
│   │       ├── sender/                  # 待办发送器
│   │       └── vo/                      # 值对象
│   │
│   ├── displaytag/                      # DisplayTag扩展
│   │   ├── AbstractExcelView.java       # Excel导出抽象视图
│   │   ├── ExcelHssfView.java           # XLS格式导出
│   │   └── ExcelXssfView.java           # XLSX格式导出
│   │
│   ├── exception/                       # 自定义异常
│   │   ├── CustomRuntimeException.java  # 通用运行时异常
│   │   └── UploadException.java         # 上传异常
│   │
│   ├── init/                            # 系统初始化
│   │   ├── InitLicenser.java            # 许可证初始化
│   │   └── SpringInit.java              # Spring上下文初始化
│   │
│   ├── listener/                        # Web监听器
│   │   └── JdbcDriverCleanupListener.java  # JDBC驱动清理
│   │
│   ├── template/                        # 文档模板
│   │   ├── spotCheckDoc.ftl/xml         # 抽检文档模板
│   │   └── 《设备过保提醒函》.ftl/xml     # 过保提醒函模板
│   │
│   └── quartz/                          # Quartz任务
│       ├── ReportTask.java              # 报表定时任务
│       └── TaskBySMS.java              # SMS数据同步任务
│
├── pom.xml                              # Maven项目配置
└── .settings/                           # Eclipse项目配置
```

---

## 附录：Spring配置文件加载顺序

```
web.xml
  └── contextConfigLocation
      ├── /WEB-INF/classes/applicationContext.xml    ← Spring主入口
      │   ├── applicationContext-service.xml          ← Service层 (含事务代理)
      │   │   └── applicationContext-dao.xml          ← DAO层
      │   ├── applicationContext-common.xml           ← 公共配置 (菜单)
      │   ├── applicationContext-context.xml          ← 上下文 (UserContext)
      │   ├── applicationContext-action.xml           ← Action层
      │   │   ├── applicationContext-service.xml      ← (循环引用Service)
      │   │   └── applicationContext-context.xml      ← (循环引用Context)
      │   ├── activiti-context.xml                    ← Activiti工作流
      │   └── spring-extend-mybatis.xml               ← MyBatis扩展
      │
      └── /WEB-INF/classes/beans-quartz.xml           ← Quartz定时任务
```
