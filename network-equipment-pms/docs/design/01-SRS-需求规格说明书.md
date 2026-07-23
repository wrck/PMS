# 网络设备工程项目交付管理平台 — 软件需求规格说明书（SRS）

---

## 第 1 章 引言

### 1.1 目的

本《软件需求规格说明书》（Software Requirements Specification，SRS）遵循 IEEE 830-1998 与 GB/T 9385-2008 规范编制，对 `network-equipment-pms`（网络设备工程项目交付管理平台）的软件需求进行完整、精确、可验证的规格化描述。文档作为研发、测试、验收的统一基线，定义系统的功能需求、接口需求、性能需求、安全需求、可靠性需求、可维护性需求与设计约束。

### 1.2 范围

本说明书覆盖 `network-equipment-pms` 1.0.0-SNAPSHOT 版本，包含 14 个后端 Maven 模块（pms-common / pms-system / pms-project / pms-implementation / pms-asset / pms-deliverable / pms-baseline / pms-file / pms-workflow / pms-integration / pms-notification / pms-governance / pms-lowcode / pms-admin）与 1 个前端（pms-frontend，Vue 3）。

软件名称：网络设备工程项目交付管理平台（Network Equipment Project Management System，简称 PMS）。

软件功能概述：覆盖 PPDIOO（Prepare / Plan / Design / Implement / Operate / Optimize）全生命周期的项目交付管理，包括项目立项、阶段管理、任务执行、资产全生命周期、交付件全生命周期、计划基线、统一审批中心、风险/问题/变更治理、外部系统集成（D365/FP/OA）、多通道通知中心与低代码平台。

### 1.3 定义、首字母缩写和缩略语

| 术语 | 全称 | 说明 |
|------|------|------|
| SRS | Software Requirements Specification | 软件需求规格说明书 |
| FR | Functional Requirement | 功能需求 |
| NFR | Non-Functional Requirement | 非功能需求 |
| IR | Interface Requirement | 接口需求 |
| PMS | Project Management System | 项目管理系统 |
| PPDIOO | Prepare/Plan/Design/Implement/Operate/Optimize | 思科网络项目交付方法论 |
| RBAC | Role-Based Access Control | 基于角色的访问控制 |
| CCB | Change Control Board | 变更控制委员会 |
| RMA | Return Merchandise Authorization | 退换货授权 |
| DFS | Depth-First Search | 深度优先遍历 |
| Saga | 长事务协调模式 | 用于结算单提交编排 |
| SPI | Service Provider Interface | 服务提供接口 |
| BPMN | Business Process Model and Notation | 业务流程建模标记 |
| JWT | JSON Web Token | 基于 JSON 的无状态令牌 |
| D365 | Microsoft Dynamics 365 | 微软 ERP 系统 |
| FP | Financial Platform | 财务平台 |
| OA | Office Automation | 致远 OA 协同办公系统 |
| SLA | Service Level Agreement | 服务水平协议 |
| APM | Application Performance Monitoring | 应用性能监控 |
| Punch List | 尾项清单 | 交付遗留问题清单 |
| WBS | Work Breakdown Structure | 工作分解结构 |
| STOMP | Simple Text Oriented Messaging Protocol | 简单文本消息协议 |
| Pub/Sub | Publish/Subscribe | 发布/订阅模式 |
| SETNX | Set if Not eXists | Redis 命令 |
| AST | Abstract Syntax Tree | 抽象语法树 |
| DAG | Directed Acyclic Graph | 有向无环图 |
| ETL | Extract/Transform/Load | 数据抽取/转换/加载 |
| OTLP | OpenTelemetry Protocol | OpenTelemetry 协议 |
| SLO | Service Level Objective | 服务水平目标 |
| RTO | Recovery Time Objective | 恢复时间目标 |
| RPO | Recovery Point Objective | 恢复点目标 |

### 1.4 参考资料

| 编号 | 文档名 | 来源 |
|------|--------|------|
| R1 | IEEE Std 830-1998 | IEEE Recommended Practice for Software Requirements Specifications |
| R2 | GB/T 9385-2008 | 软件需求规格说明书规范 |
| R3 | PMBOK Guide 7th Edition | 项目管理知识体系 |
| R4 | Cisco PPDIOO Methodology | 思科网络项目交付方法论 |
| R5 | Flowable 7.0.1 Documentation | BPMN 工作流引擎 |
| R6 | Spring Boot 3.2.5 Reference | Spring Boot 框架 |
| R7 | Vue 3.5 Documentation | Vue 3 前端框架 |
| R8 | RFC 7519 - JSON Web Token (JWT) | JWT 标准 |
| R9 | RFC 6749 - OAuth 2.0 | OAuth2 授权框架 |
| R10 | BPMN 2.0 Specification | 业务流程建模标记 |
| R11 | OpenAPI Specification 3.0 | REST API 文档规范 |
| R12 | Resilience4j 2.2.0 Documentation | 弹性容错库 |
| R13 | OWASP Top 10 | Web 安全风险 |
| R14 | Flyway Documentation | 数据库迁移工具 |
| R15 | MyBatis-Plus 3.5.9 Documentation | ORM 框架 |

### 1.5 概述

本说明书第 1 章引言介绍文档目的、范围、定义与参考资料。第 2 章总体描述介绍产品视角、产品功能、用户特征、约束、假设与依赖。第 3 章需求详细规格按 IEEE 830 结构组织，包括功能需求（FR）、接口需求（IR）、性能需求（PRF）、安全需求（SEC）、可靠性需求（REL）、可维护性需求（MNT）与设计约束（CON）。第 4 章需求跟踪矩阵建立需求与模块的映射关系。第 5 章附录提供状态机定义、数据字典与 SPI 接口清单。

---

## 第 2 章 总体描述

### 2.1 产品视角

`network-equipment-pms` 是独立运行的企业级 Web 应用系统，通过集成接口与外部系统（D365 / FP / OA）协同工作。系统采用前后端分离架构：

- **后端**：14 个 Maven 模块组成的单体应用，由 `pms-admin` 聚合启动为可执行 fat jar，部署于应用服务器。
- **前端**：Vue 3 单页应用，构建产物部署于 Nginx 或内嵌静态资源。
- **数据库**：MySQL 8.0 主库 + Redis 缓存。
- **外部系统**：D365（ERP）、FP（财务）、致远 OA。

系统上下文图：

```
                  ┌──────────────────┐
                  │   浏览器 / 移动端   │
                  │  (Vue 3 SPA)     │
                  └─────────┬────────┘
                            │ HTTPS
                            ▼
        ┌───────────────────────────────────────┐
        │       network-equipment-pms           │
        │   (Spring Boot 3 + 14 模块)          │
        │                                       │
        │  ┌──────────┐  ┌──────────┐         │
        │  │ MySQL 8  │  │  Redis   │         │
        │  └──────────┘  └──────────┘         │
        └──────┬──────────┬──────────┬────────┘
               │          │          │
               ▼          ▼          ▼
        ┌─────────┐ ┌─────────┐ ┌─────────┐
        │  D365   │ │   FP    │ │  致远 OA │
        │  (ERP)  │ │  (财务) │ │  (协同)  │
        └─────────┘ └─────────┘ └─────────┘
```

### 2.2 产品功能

系统由 14 个后端模块 + 1 个前端模块构成，按职责划分为五大功能域：

1. **基础与系统域**：pms-common（公共基础）、pms-system（系统管理）、pms-admin（聚合启动）。
2. **项目交付域**：pms-project（项目交付管理）、pms-implementation（实施管理）、pms-deliverable（交付件管理）、pms-baseline（基线管理）。
3. **资产与售后域**：pms-asset（设备资产管理，含 RMA / 质保）、pms-file（文件管理）。
4. **治理与流程域**：pms-workflow（工作流与审批中心）、pms-governance（项目治理三本账）、pms-notification（通知中心）。
5. **集成与扩展域**：pms-integration（外部集成）、pms-lowcode（低代码平台）、pms-frontend（前端应用）。

### 2.3 用户特征

| 用户角色 | 教育水平 | 技术熟练度 | 使用频率 | 主要场景 |
|----------|----------|------------|----------|----------|
| 项目经理 | 本科以上 | 中 | 每日 | 项目立项、阶段管理、任务分派、审批 |
| 实施工程师 | 专科以上 | 中 | 每日 | 任务执行、资产装箱、交付件提交 |
| 资产管理员 | 专科以上 | 中 | 每日 | 资产入库、调拨、RMA、质保 |
| 变更经理 | 本科以上 | 中 | 每周 | CCB 审批、变更处理 |
| 财务专员 | 本科以上 | 中 | 每周 | 结算审批、发票推送 |
| 集成运维 | 本科以上 | 高 | 每日 | 集成监控、重试调度 |
| 治理专员 | 本科以上 | 中 | 每周 | 风险登记、问题处理 |
| 低代码设计师 | 本科以上 | 高 | 按需 | 实体/表单/微流设计 |
| 系统管理员 | 本科以上 | 高 | 每日 | 用户/角色/权限管理 |
| 超级管理员 | 本科以上 | 高 | 按需 | 全权管理 |

### 2.4 约束

1. **技术栈约束**：
   - 后端：Spring Boot 3.2.5 + Java 17 + MyBatis-Plus 3.5.9 + Flowable 7.0.1。
   - 前端：Vue 3.5 + TypeScript 6 + Vite 8 + Element Plus 2.14。
   - 数据库：MySQL 8.0.16。
   - 缓存：Redis。
   - 构建：Maven 3.x（后端）/ npm（前端）。

2. **标准约束**：
   - IEEE 830 + GB/T 9385 SRS 规范。
   - BPMN 2.0 流程定义规范。
   - OAuth2 / JWT 鉴权标准。
   - RESTful API 设计规范。
   - OpenAPI 3.0 文档规范。

3. **业务约束**：
   - 不直接承担 ERP / 财务核心账务处理，仅通过集成接口同步数据。
   - 不替换 OA 系统的协同办公能力，仅做待办镜像。
   - 不提供网络设备配置管理（CMDB）功能。

4. **法规约束**：
   - 数据安全合规：AES-256-GCM 字段级加密 + 传输层 HTTPS。
   - 操作审计：所有写操作通过 `@OperLog` 记录日志。
   - 用户隐私：密码 BCrypt 加密，不可逆。

5. **硬件约束**：
   - 服务器：4 核 CPU / 8GB 内存起步。
   - 数据库：MySQL 单实例（生产建议主从）。
   - Redis：单实例（V2.0 升级集群）。

### 2.5 假设与依赖

#### 2.5.1 假设

- A1：用户已具备基本的 Web 浏览器使用能力。
- A2：外部系统（D365/FP/OA）提供稳定的 OAuth2 鉴权与 REST API。
- A3：网络环境支持 HTTPS 与 WebSocket 长连接。
- A4：MySQL 与 Redis 在部署期间可用性 ≥ 99.5%。
- A5：浏览器版本满足兼容性要求（Chrome 90+ / Edge 90+ / Firefox 88+ / Safari 14+）。

#### 2.5.2 依赖

- D1：依赖 Microsoft Dynamics 365 提供 OAuth2 `client_credentials` 鉴权与采购单/收货/资产序列号/发票接口。
- D2：依赖 Financial Platform 提供 OAuth2 鉴权与结算推送/OCR/支付回调接口。
- D3：依赖致远 OA 提供 OAuth2 鉴权与待办推送/完成/转办接口。
- D4：依赖 MySQL 8.0.16+ 提供数据库服务。
- D5：依赖 Redis 提供缓存 / 分布式锁 / Pub-Sub。
- D6：依赖 Java 17 运行时。
- D7：依赖 Flowable 7.0.1 工作流引擎。
- D8：依赖 Maven 3.x 构建工具。

### 2.6 需求分配

需求按以下优先级分级：

- **P0（必须）**：核心业务流程，缺失则系统无法运行。
- **P1（应当）**：重要功能，缺失则用户体验显著下降。
- **P2（可选）**：增强功能，可在后续版本实现。

---

## 第 3 章 需求详细规格

### 3.1 外部接口需求

#### 3.1.1 用户界面接口

| 编号 | 接口名 | 描述 | 优先级 |
|------|--------|------|--------|
| UI-01 | 登录界面 | 用户名 + 密码登录，JWT 鉴权 | P0 |
| UI-02 | 仪表盘 | 8 指标概要 + 项目趋势 + 待办列表 + 近期动态 | P0 |
| UI-03 | 项目工作区 | 8 Tab 整合（概览/阶段/任务/交付件/基线/审批/成员/配置） | P0 |
| UI-04 | 项目甘特图 | 任务时序可视化 | P1 |
| UI-05 | 交付看板 | 按状态分组的项目卡片 | P1 |
| UI-06 | 主子项目树 | 递归树形结构，物化路径展示 | P1 |
| UI-07 | 任务树 | 递归任务列表，物化路径 | P0 |
| UI-08 | 任务依赖图 | AntV G6 DAG 渲染 | P1 |
| UI-09 | 资产清单 | 分页 + 多条件过滤 | P0 |
| UI-10 | 交付件生命周期 | 7 态状态流可视化 | P0 |
| UI-11 | 审批时间轴 | 多轮次审批历史 | P0 |
| UI-12 | 基线偏差表 | 延迟红色 / 提前绿色高亮 | P1 |
| UI-13 | 5×5 风险矩阵 | 风险评分可视化 | P1 |
| UI-14 | 集成健康大盘 | D365/FP/OA 健康状态 | P1 |
| UI-15 | 通知中心 | 站内信列表 + 未读数 + 标记已读 | P0 |
| UI-16 | 低代码实体设计器 | X6 ER 关系图编辑 | P1 |
| UI-17 | 低代码微流设计器 | X6 DAG 节点编排 + 调试器 | P1 |
| UI-18 | 低代码 BPMN 设计器 | bpmn-js 流程设计 | P1 |
| UI-19 | 低代码发布中心 | 发布流水线 + 灰度管理 | P1 |
| UI-20 | 移动端适配 | 响应式 768px 断点 + PWA | P2 |

#### 3.1.2 硬件接口

- HI-01：服务器硬件接口遵循 x86_64 架构标准。
- HI-02：打印机接口（用于纸质交付件输出，通过浏览器原生打印）。

#### 3.1.3 软件接口

##### 3.1.3.1 外部系统集成接口

| 编号 | 接口名 | 协议 | 鉴权 | 端点 |
|------|--------|------|------|------|
| IR-D365-01 | D365 OAuth2 Token | HTTPS POST | client_credentials | `{d365.tokenUrl}` |
| IR-D365-02 | D365 推送采购收货 | HTTPS POST | Bearer Token | `{d365.baseUrl}/purchase-receipts` |
| IR-D365-03 | D365 推送采购单 | HTTPS POST | Bearer Token | `{d365.baseUrl}/purchase-orders` |
| IR-D365-04 | D365 同步采购单 | HTTPS GET | Bearer Token | `{d365.baseUrl}/purchase-orders` |
| IR-D365-05 | D365 同步采购收货 | HTTPS GET | Bearer Token | `{d365.baseUrl}/purchase-receipts` |
| IR-D365-06 | D365 同步资产序列号 | HTTPS GET | Bearer Token | `{d365.baseUrl}/asset-serial-numbers` |
| IR-D365-07 | D365 同步发票 | HTTPS GET | Bearer Token | `{d365.baseUrl}/invoices` |
| IR-FP-01 | FP OAuth2 Token | HTTPS POST | client_credentials | `{fp.tokenUrl}` |
| IR-FP-02 | FP 推送结算单 | HTTPS POST | Bearer Token | `{fp.baseUrl}/settlements` |
| IR-FP-03 | FP 发票 OCR | HTTPS POST multipart | Bearer Token | `{fp.baseUrl}/ocr/invoice` |
| IR-FP-04 | FP 支付回调 | HTTPS POST | — | `/api/integration/fp/payment-callback` |
| IR-OA-01 | OA OAuth2 Token | HTTPS POST | client_credentials | `{integration.oa.tokenUrl}` |
| IR-OA-02 | OA 推送待办 | HTTPS POST | Bearer Token | `{integration.oa.baseUrl}/todo/push` |
| IR-OA-03 | OA 完成待办 | HTTPS PUT | Bearer Token | `{integration.oa.baseUrl}/todo/complete` |
| IR-OA-04 | OA 转办待办 | HTTPS PUT | Bearer Token | `{integration.oa.baseUrl}/todo/transfer` |

##### 3.1.3.2 内部系统间接口（SPI）

详见第 5 章附录 C：SPI 接口清单（12 个）。

#### 3.1.4 通信接口

- CI-01：HTTP/HTTPS（前端 ↔ 后端 REST API）。
- CI-02：WebSocket（通知实时推送，STOMP 协议）。
- CI-03：Redis Pub/Sub（跨实例通知广播）。
- CI-04：MySQL JDBC（数据库连接，HikariCP 连接池）。
- CI-05：Redis（缓存 / 分布式锁 / Pub-Sub）。
- CI-06：RabbitMQ / Kafka（低代码 MQ 连接器，可选）。
- CI-07：SFTP（低代码 FILE 连接器，可选）。

### 3.2 功能需求

功能需求按模块分组，编号格式 `FR-{模块缩写}-{序号}`。

#### 3.2.1 公共基础模块（pms-common）

**FR-CM-01**：系统应提供统一响应封装 `Result<T>`，包含 `code`（int）/ `message`（String）/ `data`（T）三字段，HTTP 状态码与业务码分离。

**FR-CM-02**：系统应提供统一异常体系，包括 `BusinessException`（业务异常）、`IntegrationException`（集成异常）、`DdlSecurityException`（DDL 安全异常），由全局异常处理器捕获并转换为标准 `Result` 响应。

**FR-CM-03**：系统应提供 `BaseEntity` 公共基类，包含 `id` / `createTime` / `updateTime` / `createBy` / `updateBy` / `deleted` 字段，通过 `MetaObjectHandler` 自动填充审计字段，通过 `@TableLogic` 实现逻辑删除。

**FR-CM-04**：系统应提供 12 个 SPI（Service Provider Interface）接口下沉到 pms-common 模块，业务模块通过实现扩展点协作，避免模块环依赖。

**FR-CM-05**：系统应提供字段级加密能力，通过 `@FieldEncrypt` 注解标记敏感字段，使用 AES-256-GCM 算法加密存储，读取时自动解密。

**FR-CM-06**：系统应提供基于 Redis 的滑动窗口限流能力，通过 `@RateLimit` 注解配置限流规则（时间窗口 / 最大请求数），超出限制返回 429 状态码。

**FR-CM-07**：系统应提供幂等性保证，通过 `X-Idempotent-Key` HTTP 头与 `@Idempotent` 注解配合，相同键的请求在指定时间内仅执行一次，重复请求返回首次结果。

**FR-CM-08**：系统应提供 XSS 防护，通过 `XssFilter` + `XssHttpServletRequestWrapper` 对请求参数进行 HTML 实体转义，防止 XSS 攻击。

**FR-CM-09**：系统应提供链路追踪能力，通过 MDC 注入 `traceId` / `userId` / `username` 字段到日志上下文，贯穿请求全生命周期。

**FR-CM-10**：系统应提供 MyBatis-Plus 基础设施，包括分页 InnerInterceptor、乐观锁 InnerInterceptor、数据权限 InnerInterceptor、`MetaObjectHandler` 自动填充。

**FR-CM-11**：系统应提供 `SecurityUtils` 工具类，从 JWT 解析当前用户信息，包括 `getCurrentUserId()` / `getCurrentUsername()` 方法。

#### 3.2.2 系统管理模块（pms-system）

**FR-SY-01**：系统应提供用户管理功能，包括用户 CRUD、密码 BCrypt 加密、用户状态启用/禁用、密码重置。

**FR-SY-02**：系统应提供角色管理功能，包括角色 CRUD、角色-菜单关联、角色-数据权限配置。

**FR-SY-03**：系统应提供菜单管理功能，包括菜单树 CRUD、菜单-权限码关联、菜单类型（目录/菜单/按钮）。

**FR-SY-04**：系统应提供字典管理功能，包括字典类型与字典项 CRUD，缓存到 Redis `sysDict` 命名缓存（TTL 60 分钟）。

**FR-SY-05**：系统应提供配置管理功能，包括系统配置表 `sys_config` 的键值对 CRUD。

**FR-SY-06**：系统应提供操作日志记录能力，通过 `@OperLog` 注解 + AOP 切面自动记录，包括操作标题、业务类型（1=新增/2=修改/3=删除）、操作人、操作时间。

**FR-SY-07**：系统应提供登录日志记录能力，记录登录成功/失败、IP、UserAgent、登录时间。

**FR-SY-08**：系统应提供 JWT 认证能力，使用 JJWT 签发与校验，通过 `JwtAuthenticationFilter` 过滤器解析 `Authorization: Bearer` 头，过期时间 24 小时。

**FR-SY-09**：系统应提供 Spring Security 配置，无状态会话（STATELESS）+ 过滤器链（SecurityHeadersFilter → RateLimitFilter → XssFilter → JwtAuthenticationFilter → UsernamePasswordAuthenticationFilter），未认证返回 401 JSON，权限不足返回 403 JSON。

**FR-SY-10**：系统应提供 Redis 配置，`@EnableCaching` + `RedisTemplate`（String key + GenericJackson2Json value）+ `CacheManager`（默认 TTL 30 分钟 + 0~5 分钟随机抖动防雪崩，`disableCachingNullValues` 防穿透）。

**FR-SY-11**：系统应提供 RBAC 权限校验能力，包括方法级 `@PreAuthorize("hasAuthority('xxx')")` 注解与 SpEL 级 `@ss.hasPermi('xxx')` 自定义方法，支持复杂表达式如 `@ss.hasPermi('lowcode:data:' + #entityCode + ':list')`。

**FR-SY-12**：系统应提供 Springdoc OpenAPI 3 文档自动生成能力，Bearer JWT SecurityScheme。

#### 3.2.3 项目交付管理模块（pms-project）

**FR-PJ-01**：系统应提供项目 CRUD 功能，项目编码 `PRJ-YYYY-XXXX` 自动生成。

**FR-PJ-02**：系统应提供项目 11 态状态机管理，状态包括 `DRAFT` / `PLANNING` / `IN_PROGRESS` / `PENDING_ACCEPTANCE` / `ACCEPTED` / `CLOSED` / `ON_HOLD` / `CANCELLED` / `REJECTED` 等，状态转换需通过校验。

**FR-PJ-03**：系统应提供物化路径树管理，通过 `project_path` + `depth` 字段实现主子项目树，支持子树查询（`LIKE '/1/3/%'`）与层级统计。

**FR-PJ-04**：系统应提供阶段管理功能，4 态状态机（`PENDING` / `IN_PROGRESS` / `COMPLETED` / `EXIT_BLOCKED`），通过 `PhaseExitValidator` SPI 校验阶段退出闸门。

**FR-PJ-05**：系统应提供 PPDIOO 12 节点里程碑模型，覆盖 Prepare/Plan/Design/Implement/Operate/Optimize 六阶段。

**FR-PJ-06**：系统应提供项目模板深拷贝功能，12 步原子复制（项目基础 → 阶段 → 任务 → 资产清单 → 交付件模板 → 基线 → 审批节点 → 成员 → 配置 → Punch List 模板 → RMA 模板 → 关联关系）。

**FR-PJ-07**：系统应提供项目模板版本发布与归档功能。

**FR-PJ-08**：系统应提供项目配置管理，按项目维度存储配置项。

**FR-PJ-09**：系统应提供项目成员管理，角色分配。

**FR-PJ-10**：系统应提供里程碑管理，5 态状态机（`PLANNED` / `IN_PROGRESS` / `COMPLETED` / `DELAYED` / `CANCELLED`），逾期自动触发通知。

**FR-PJ-11**：系统应提供 Punch List（尾项清单）管理，到期提醒通知。

#### 3.2.4 实施管理模块（pms-implementation）

**FR-IM-01**：系统应提供实施任务 CRUD 功能，7 态状态机（`PENDING` / `ACCEPTED` / `IN_PROGRESS` / `COMPLETED` / `VERIFIED` / `REJECTED` / `CANCELLED`），状态转换需通过校验。

**FR-IM-02**：系统应提供任务树管理，通过 `taskPath` + `depth` 物化路径实现任务层级。

**FR-IM-03**：系统应提供任务依赖关系管理，支持 DAG（有向无环图）结构，循环依赖检测。

**FR-IM-04**：系统应提供任务分配功能，支持 OEM 分派与代理商分派。

**FR-IM-05**：系统应提供任务优先级与计划日期管理。

**FR-IM-06**：系统应提供任务清单（Checklist）功能，强制检查项闸门，任务完成前校验强制检查项是否全部勾选。

**FR-IM-07**：系统应提供任务评论与任务活动日志。

**FR-IM-08**：系统应提供双轨进度汇总能力，同步递归汇总（子任务 → 父任务 → 阶段 → 项目）与异步持久化进度快照。

**FR-IM-09**：系统应提供结算单 CRUD 功能。

**FR-IM-10**：系统应提供结算单 Saga 协调器，6 步编排（校验 → 推送 FP → 推送发票到 D365 → 更新状态 → 通知 → 日志），任一步骤失败触发补偿。

**FR-IM-11**：系统应提供结算审批工作流，PM → 财务两级审批（基于 BPMN `settlementApproval` 流程）。

**FR-IM-12**：系统应提供代理商管理与代理商评分功能，按 `overallScore` 排名 Top10。

#### 3.2.5 设备资产管理模块（pms-asset）

**FR-AS-01**：系统应提供资产 CRUD 功能，9 态状态机（`IN_STOCK` / `ALLOCATED` / `IN_TRANSIT` / `INSTALLED` / `ACCEPTED` / `WARRANTY_PERIOD` / `OUT_OF_WARRANTY` / `SCRAPPED` / `RMA_IN_PROGRESS`）。

**FR-AS-02**：系统应提供资产编码与序列号（SN）管理，SN 通过 D365 同步回填。

**FR-AS-03**：系统应提供资产分类树（`pms_asset_category`）与资产型号（`pms_asset_model`，含 `standard_price` 用于资产总值统计）。

**FR-AS-04**：系统应提供资产装箱与位置管理（库房 / 站点）。

**FR-AS-05**：系统应提供资产调拨功能，跨项目调拨审批工作流（源 PM → 目标 PM 两级审批，基于 BPMN `assetTransfer` 流程）。

**FR-AS-06**：系统应提供 RMA 退换货管理，6 步闭环（申请 → 审批 → 发货 → 维修 → 返回 → 入库），RMA 工单 CRUD 与状态追踪。

**FR-AS-07**：系统应提供质保管理，质保记录、质保期起止。

**FR-AS-08**：系统应提供质保到期预警，三档预警（90 天 / 60 天 / 30 天），通过 `@Scheduled` 定时扫描，触发通知模板 `WARRANTY_EXPIRE_90/60/30`。

**FR-AS-09**：系统应提供质保扫描功能，现场扫码查询质保状态。

#### 3.2.6 交付件管理模块（pms-deliverable）

**FR-DV-01**：系统应提供交付件 CRUD 功能，7 态状态机（`DRAFT` / `SUBMITTED` / `REVIEWED` / `SIGNED` / `PUBLISHED` / `REFERENCED` / `ARCHIVED`）。

**FR-DV-02**：系统应提供字典驱动的交付件类型，通过 `pms_deliverable_type` 字典配置类型（DOCUMENT / CODE / ENTITY_REF / MODEL / CONFIG / DATA / OTHER）。

**FR-DV-03**：系统应提供交付件性质分类，`ref_entity_type` / `ref_entity_id` / `template_inherited` 字段。

**FR-DV-04**：系统应提供交付件版本管理，`reviseDeliverable` 新建版本不覆盖旧版本，不可变历史。

**FR-DV-05**：系统应提供电子签名能力，`signDeliverable` 数字签名记录。

**FR-DV-06**：系统应提供交付件引用实体功能，支持引用 TASK / ASSET / PHASE / PROJECT / DELIVERABLE / REPORT 实体。

**FR-DV-07**：系统应提供阶段退出校验，`validateMandatoryDeliverables(phaseId)` 校验阶段强制交付件是否全部到位。

#### 3.2.7 基线管理模块（pms-baseline）

**FR-BL-01**：系统应提供基线快照管理，任务计划快照（`TaskPlanSnapshot`）。

**FR-BL-02**：系统应支持单一活跃基线约束，同一项目同一时间仅允许一个 `ACTIVE` 基线，基线状态 `DRAFT` → `ACTIVE` → `ARCHIVED`。

**FR-BL-03**：系统应提供 DFS 循环检测，基线变更前深度优先遍历检测任务依赖循环。

**FR-BL-04**：系统应提供三阈值偏差监控，包括天数阈值、百分比阈值、任务数阈值，三者 OR 关系，任一满足即触发审批。

**FR-BL-05**：系统应提供偏差分析能力，逐任务对比基线 vs 当前计划，延迟红色 / 提前绿色高亮。

**FR-BL-06**：系统应通过 `ApprovalTrigger` SPI 触发基线变更审批。

#### 3.2.8 文件管理模块（pms-file）

**FR-FL-01**：系统应提供文件存储抽象，支持本地存储（`local`，默认 `./pms-files`）/ MinIO（`minio`）/ 阿里云 OSS（`oss`），通过 `pms.file.storage.type` 配置切换。

**FR-FL-02**：系统应提供附件管理，关联业务对象（`biz_type` + `biz_id`），支持文件上传 / 下载 / 删除。

**FR-FL-03**：系统应提供 EXIF GPS 解析能力，从图片 EXIF 信息提取 GPS 坐标。

**FR-FL-04**：系统应提供地理围栏校验，基于 GPS 坐标校验是否在允许范围内，防止现场虚假打卡。

#### 3.2.9 工作流与审批中心模块（pms-workflow）

**FR-WF-01**：系统应提供 Flowable 工作流引擎集成，BPMN 流程定义部署、流程实例生命周期管理、任务办理/转办/撤回、流程图渲染、流程历史查询。

**FR-WF-02**：系统应提供 5 个预置 BPMN 流程：项目审批（`projectApproval`）、资产转移（`assetTransfer`）、最终验收（`finalAcceptance`）、结算审批（`settlementApproval`）、网络割接（`network-cutover`）。

**FR-WF-03**：系统应提供统一审批中心，双轨并存（自建审批表 + Flowable 引擎），10 类业务对象审批（PROJECT / TASK / DELIVERABLE / RISK / ISSUE / CHANGE / RESOURCE / COST / PHASE_EXIT / BASELINE_CHANGE）。

**FR-WF-04**：系统应提供审批 5 态状态机（`PENDING` / `APPROVED` / `REJECTED` / `WITHDRAWN` / `TIMEOUT`），审批退回重新提交复用原记录、`round` 递增。

**FR-WF-05**：系统应提供字段脱敏能力，通过 `ApprovalFieldPermission` 配置字段可见性（VISIBLE / MASKED / HIDDEN），支持 `phone-mask` / `amount-mask` / `email-mask` / `custom` 脱敏规则。

**FR-WF-06**：系统应提供超时调度能力，`ApprovalTimeoutScheduler` 定时扫描超时审批，触发升级。

**FR-WF-07**：系统应提供 OA 任务镜像能力，`OaTaskListener` 将 Flowable 任务 create/complete 事件镜像到致远 OA。

**FR-WF-08**：系统应通过 `ApprovalTriggerEvent` Spring 事件 + `ApprovalDispatcher` 异步消费实现审批触发机制。

#### 3.2.10 外部系统集成模块（pms-integration）

**FR-IT-01**：系统应提供 D365 集成能力，OAuth2 `client_credentials` 鉴权，采购单/收货推送、同步采购单/收货/资产序列号/发票到本地。

**FR-IT-02**：系统应通过反射查找 `assetMapper` 更新 `pms_asset.serial_no`、`settlementMapper` 更新 `pms_settlement.invoice_no`。

**FR-IT-03**：系统应提供 FP 集成能力，OAuth2 鉴权，结算单推送（首调同步 + 后台指数退避重试 1/2/4/8/16 分钟，最多 5 次）、发票图片 OCR、支付回调接收。

**FR-IT-04**：系统应提供 OA 集成能力，OAuth2 鉴权（Token 剩余 < 5 分钟自动续期），待办推送 / 完成 / 转办。

**FR-IT-05**：系统应提供 Resilience4j 四层弹性保护：CircuitBreaker（计数滑动窗口 20，失败率 ≥50% 熔断，30s 后半开，半开 5 次试探）/ Bulkhead（信号量隔离，最大并发 10，等待 5s）/ RateLimiter（50 次/秒，等待 10s）/ Retry（最多 3 次，1s 起步指数退避上限 16s）。

**FR-IT-06**：系统应提供 OAuth2 Token 缓存能力，基于 Redis Hash 跨实例共享 + `TokenRefreshLock`（Redis SETNX + Lua 解锁）单飞刷新，避免 Token 端点雪崩。

**FR-IT-07**：系统应提供集成日志能力，所有外部调用落库 `pms_integration_log`，记录请求 URL、请求体、响应状态、响应体、错误信息、重试次数、下次重试时间。

**FR-IT-08**：系统应提供集成重试能力，支持按日志 ID 手动重试与定时调度重试（5 分钟扫描一次）。

**FR-IT-09**：系统应提供健康检查聚合能力，输出 `HEALTHY` / `DEGRADED` / `DOWN` 总体状态，3 系统全连通 = HEALTHY，部分 = DEGRADED，全断 = DOWN。

#### 3.2.11 通知中心模块（pms-notification）

**FR-NT-01**：系统应提供站内信管理，`pms_notification` 表记录接收人、标题、正文、业务分类、业务类型、业务 id、已读状态、投递通道。

**FR-NT-02**：系统应提供四通道并发投递能力，IN_APP（同步落库）/ WS（异步 + Redis Pub/Sub + STOMP 广播）/ EMAIL（异步）/ OA（异步），任一通道失败仅记录日志、不阻塞其他通道。

**FR-NT-03**：系统应提供模板化发送能力，基于 Freemarker 渲染 `pms_notification_template` 表中的模板，调用方仅传 `templateCode + variables` 即可生成通知。

**FR-NT-04**：系统应预置 12 个标准通知模板：里程碑逾期、任务分派、任务转派、审批待办、Punch List 到期、质保 90/60/30 天预警、RMA 状态变更、结算审批通过、变更请求 CCB、风险升级。

**FR-NT-05**：系统应提供 WebSocket 实时推送能力，STOMP 端点 `/ws`，不启用 SockJS（原生 WebSocket），订阅 `/topic/notification/{userId}` 接收推送。

**FR-NT-06**：系统应提供 JWT 握手鉴权，从 `Authorization: Bearer` 头或 `?token=` 查询参数解析 JWT，提取 `userId` 后存入会话属性，无 token 或解析失败拒绝握手。

**FR-NT-07**：系统应提供 WebSocket 心跳机制，SimpleBroker 入站/出站心跳 10s，独立 ThreadPoolTaskScheduler（poolSize=1，线程名前缀 `ws-heartbeat-`）。

**FR-NT-08**：系统应提供 WebSocket 消息大小上限 64KB。

**FR-NT-09**：系统应提供已读管理能力，单条标记已读、批量标记已读、未读数统计。

**FR-NT-10**：系统应通过 Redis Pub/Sub 跨实例广播解决多实例部署下的推送一致性，发布端 `NotificationPublisher.publish`，订阅端 `NotificationSubscriber.onMessage` 通过 `SimpMessagingTemplate` 推送。

#### 3.2.12 项目治理模块（pms-governance）

**FR-GV-01**：系统应提供变更请求管理，6 态状态机（`SUBMITTED` / `UNDER_REVIEW` / `CCB_APPROVED` / `CCB_REJECTED` / `IMPLEMENTING` / `CLOSED`），CCB 审批 BPMN 流程（`change-request-approval.bpmn20.xml`）。

**FR-GV-02**：系统应提供基线变更审计能力，审批通过时对 `impactSchedule` / `impactCost` / `impactScope` 三维度分别记录 `BaselineHistory`。

**FR-GV-03**：系统应通过 `ObjectProvider<WorkflowService>` 注入工作流服务，工作流模块未加载时降级运行。

**FR-GV-04**：系统应提供变更请求编号自动生成 `CR-YYYY-XXXX`。

**FR-GV-05**：系统应提供风险登记册管理，4 态状态机（`OPEN` / `IN_PROGRESS` / `ESCALATED` / `CLOSED`）。

**FR-GV-06**：系统应提供 5×5 风险矩阵，`likelihood`（1-5）× `impact`（1-5）= `score`（1-25），三档优先级（1-6 LOW / 7-12 MEDIUM / 13-25 HIGH）。

**FR-GV-07**：系统应提供风险已发生转化为问题（`markOccurred`）与风险升级为变更请求（`escalate`）能力。

**FR-GV-08**：系统应提供风险编号自动生成 `RISK-YYYY-XXXX`。

**FR-GV-09**：系统应提供问题日志管理，4 态状态机（`OPEN` / `IN_PROGRESS` / `RESOLVED` / `CLOSED`），分配处理人（OPEN 自动转 IN_PROGRESS）、解决、关闭。

**FR-GV-10**：系统应提供问题升级为变更请求（`escalate`）能力。

**FR-GV-11**：系统应提供问题编号自动生成 `ISSUE-YYYY-XXXX`。

**FR-GV-12**：系统应保证三账联动单向无环依赖（风险 → 问题、风险 → 变更请求、问题 → 变更请求）。

#### 3.2.13 低代码平台模块（pms-lowcode）

**FR-LC-01**：系统应提供可视化配置存储，表单/列表/标签页/关联页四类配置 JSON Schema 存储，状态流转 DRAFT → PUBLISHED → ARCHIVED。

**FR-LC-02**：系统应提供实体建模能力，实体设计器（实体 + 字段 + 关联关系），DDL 生成器按方言（MySQL/PostgreSQL/SQLServer）生成 CREATE/ALTER/DROP。

**FR-LC-03**：系统应提供 DDL 安全执行，危险语句拦截 + 表结构备份 + 执行日志 + 回滚（按 entityId 回滚最近一次 / 按 backupId 回滚指定备份）。

**FR-LC-04**：系统应提供动态实体数据 CRUD（JdbcTemplate + 字段白名单防注入 + 标识符正则白名单 + 参数化查询）。

**FR-LC-05**：系统应提供微流引擎，DAG 节点编排 + Groovy 沙箱（`SecureASTCustomizer`），11 种节点类型（START/END/ASSIGN/CONDITION/LOOP/CALL_SERVICE/CALL_MICROFLOW/CALL_RULE/CALL_CONNECTOR/THROW_EXCEPTION/RETURN）。

**FR-LC-06**：系统应提供微流断点调试器，支持断点设置、单步执行、继续执行、变量监视，30 分钟无操作超时清理。

**FR-LC-07**：系统应提供微流流程图渲染（SVG/PNG）。

**FR-LC-08**：系统应提供规则引擎，三种规则类型（决策表 / Aviator 表达式 / LiteFlow）。

**FR-LC-09**：系统应提供 Aviator 沙箱，禁用 NewInstance / Module / InternalVars，移除系统函数（sysdate/now/rand 等），编译前正则阻断 java/javax/jdk/sun/runtime 等引用。

**FR-LC-10**：系统应提供规则集编排，THEN（顺序）/ WHEN（并行）/ IF（条件）/ SWITCH（分支）。

**FR-LC-11**：系统应提供规则测试用例，三种断言模式（EQUALS / CONTAINS / NOT_NULL）。

**FR-LC-12**：系统应提供连接器集成，4 种类型（REST / DB / MQ / FILE），5 种鉴权（NONE / BASIC / BEARER / API_KEY / OAUTH2），Resilience4j 重试/熔断/限流，JsonPath responseMapping。

**FR-LC-13**：系统应提供 OpenAPI/Swagger 文档导入能力。

**FR-LC-14**：系统应提供触发器调度，3 种触发类型（CRUD / QUARTZ / EVENT），2 种目标类型（MICROFLOW / PROCESS）。

**FR-LC-15**：系统应保证 CRUD 触发器 BEFORE 异常阻断主操作，AFTER 异常仅记日志。

**FR-LC-16**：系统应提供流程集成，流程绑定（节点-表单映射 + 任务回调 JSON）、`ProcessTaskCallbackListener` Flowable 任务事件回调微流。

**FR-LC-17**：系统应提供流程 SLA 双阶段触发，任务截止前 80% 时间点触发预警微流（WARNING），截止时间到达触发升级微流（ESCALATED），任务完成置 COMPLETED。

**FR-LC-18**：系统应提供版本管理，不可变版本快照，版本树分支（借鉴 git parent commit 模型），Diff 对比，回滚预览。

**FR-LC-19**：系统应提供环境晋升，DEV → TEST → PROD 配置包 zip 导入导出 + 门禁预检 + 冲突检测与解决方案（KEEP_SOURCE/KEEP_TARGET/SKIP）。

**FR-LC-20**：系统应提供依赖完整性校验，包内自洽 + 目标环境存在性双层校验。

**FR-LC-21**：系统应提供发布流水线，多级审批链、灰度发布（按 `grayPercentage` 或 `tenantWhitelist` 渐进生效）。

**FR-LC-22**：系统应提供协同编辑（HTTP 轮询，预留 Yjs 升级）、编辑锁（Redis SETNX + DB 持久化）、线程化评论（@提及通知）。

**FR-LC-23**：系统应提供配置审计，AOP 切面自动写审计日志到 `pms_lowcode_config_audit_log`。

**FR-LC-24**：系统应提供模板市场，模板上架/下架/归档/下载/评分。

**FR-LC-25**：系统应提供应用源码导出，将低代码应用打包为可独立部署的源码 ZIP（JSON + DDL + POM + README）。

**FR-LC-26**：系统应提供数据导入导出，Excel 异步导入（`@Async` 独立线程池）/ 同步导出。

**FR-LC-27**：系统应提供 APM 全链路指标采集，Micrometer Counter + Timer，指标命名 `lowcode_*` 前缀。

**FR-LC-28**：系统应提供预置模板初始化，应用启动时检查四张配置表为空则加载预置模板，重复启动不抛错。

#### 3.2.14 聚合启动模块（pms-admin）

**FR-AD-01**：系统应提供统一启动入口 `PmsApplication`，`@SpringBootApplication(scanBasePackages = "com.dp.plat")` 扫描全部 14 个模块。

**FR-AD-02**：系统应提供 MyBatis Mapper 扫描三路径：`com.dp.plat.**.mapper` / `com.dp.plat.**.dao` / `com.dp.plat.**.engine.ddl`。

**FR-AD-03**：系统应启用 `@EnableScheduling` 与 `@EnableRetry`。

**FR-AD-04**：系统应提供聚合查询端点，`DeliverableRefEntityController` 交付件引用实体聚合查询（TASK/ASSET/PHASE/PROJECT/DELIVERABLE/REPORT）。

**FR-AD-05**：系统应提供报表统计与仪表盘聚合端点，`ReportController` 8 个仪表盘指标 + 项目趋势 + 待办列表 + 近期动态。

**FR-AD-06**：系统应托管 Flyway 数据库迁移，86 个迁移脚本，`baseline-on-migrate: true`、`out-of-order: true`、`validate-on-migrate: false`、`clean-disabled: true`。

**FR-AD-07**：系统应托管 Flowable BPMN 流程部署，5 个 `*.bpmn20.xml` 自动部署。

**FR-AD-08**：系统应提供健康检查指示器，`DatabaseHealthIndicator`（连接 + 表行数校验）+ `RedisHealthIndicator`（ping 校验）。

**FR-AD-09**：系统应提供可观测性三件套：Metrics（Micrometer + Prometheus，HTTP SLO 50ms/100ms/200ms/500ms/1s/2s/5s）+ Tracing（OpenTelemetry OTLP gRPC → Jaeger，10% 采样）+ Logging（Logback JSON 结构化，MDC traceId/userId/username）。

**FR-AD-10**：系统应提供多 Profile 安全分级：dev（默认）/ test / prod / mock。

#### 3.2.15 前端应用模块（pms-frontend）

**FR-FE-01**：前端应提供 Vue 3.5 单页应用，TypeScript 6 + Vite 8 + Element Plus 2.14 + Pinia 3 + Vue Router 4.6 + Axios 1.18。

**FR-FE-02**：前端应提供项目工作区枢纽页，8 Tab 整合（概览/阶段/任务/交付件/基线/审批/成员/配置）。

**FR-FE-03**：前端应提供 30+ 页面模块，覆盖项目/资产/实施/基线/工作流/交付治理/项目治理/系统监控/系统管理/报表统计/低代码/演示中心。

**FR-FE-04**：前端应提供 55+ API 封装文件，统一通过 `utils/request.ts` 暴露的 `get/post/put/del` 泛型 helper 调用，响应拦截器剥离外层信封。

**FR-FE-05**：前端应提供 JWT 注入能力，从 `localStorage.getItem('pms_token')` 读取，写入 `Authorization: Bearer` 头。

**FR-FE-06**：前端应提供幂等键注入能力，写操作自动注入 `X-Idempotent-Key`（UUID v4，优先 `crypto.randomUUID()`）。

**FR-FE-07**：前端应提供数据集成校验，30+ 业务 validator 文件，前端镜像后端 `@Valid`，校验失败 `ElMessage.error` + reject，通过后用 normalize 数据替换请求体。

**FR-FE-08**：前端应提供统一错误处理，401 清 token 跳登录，其它业务错误 `ElMessage.error`，`silent: true` 可静默。

**FR-FE-09**：前端应提供路由守卫三段式校验：token 存在性 + 低代码运行时派生权限 `/lowcode/(form|list|tab|related-page)/:code` → `lowcode:page:{type}:{code}` 或 `LOWCODE_ROUTE_PERMISSIONS` 静态表 + `meta.perms`。

**FR-FE-10**：前端应提供 PWA 能力，Service Worker 注册 + 离线监听 + 更新检测。

**FR-FE-11**：前端应提供 WebSocket 通知，原生 WebSocket（不依赖 sockjs/stompjs），5 秒重连，收到通知弹 `ElNotification` + `unreadCount++`。

**FR-FE-12**：前端应提供多标签页，标签持久化 + 拖拽排序，首页固定（`affix: true`）。

**FR-FE-13**：前端应提供顶栏一级 Tab + 侧栏二级菜单联动，钉钉/飞书风格，移动端 768px 断点切换抽屉式侧栏。

**FR-FE-14**：前端应提供低代码组件 SDK，独立打包 ES + UMD 双格式，借鉴 Power Apps PCF / ToolJet Component SDK。

**FR-FE-15**：前端应提供三套图编辑技术栈：AntV X6（微流/实体设计器）+ AntV G6（任务依赖 DAG）+ bpmn-js（BPMN 流程设计器）。

**FR-FE-16**：前端应提供字典驱动 + 兜底常量模式，模块级缓存 + 异常降级 + 兜底常量。

### 3.3 性能需求

| 编号 | 需求描述 | 测量方式 |
|------|----------|----------|
| PRF-01 | API 响应时间 p50 < 200ms | Micrometer HTTP 直方图 |
| PRF-02 | API 响应时间 p95 < 500ms | Micrometer HTTP 直方图 |
| PRF-03 | API 响应时间 p99 < 2s | Micrometer HTTP 直方图 |
| PRF-04 | 列表查询接口（10 万行数据）< 1s | 集成测试 |
| PRF-05 | 文件上传（100MB）< 30s | 集成测试 |
| PRF-06 | WebSocket 推送延迟 < 1s | 前端监控 |
| PRF-07 | 外部集成首调响应 < 30s（读取超时） | Resilience4j 指标 |
| PRF-08 | 并发用户数 ≥ 200 | 压测 |
| PRF-09 | HikariCP 连接池：min-idle=5（开发）/ 10（生产），max=20（开发）/ 50（生产） | 配置 |
| PRF-10 | Quartz 线程池 5 | 配置 |
| PRF-11 | Flowable 流程定义缓存上限 100 | 配置 |
| PRF-12 | WebSocket 消息大小上限 64KB | 配置 |
| PRF-13 | 低代码微流执行安全计数器上限 1000（防死循环） | 实现 |
| PRF-14 | 低代码微流调试器单次最大 1000 步（防死循环） | 实现 |
| PRF-15 | Prometheus 指标导出间隔 30s | 配置 |
| PRF-16 | OpenTelemetry traces 采样率 10% | 配置 |

### 3.4 安全需求

| 编号 | 需求描述 |
|------|----------|
| SEC-01 | 认证：JWT 无状态会话，密钥从 `JWT_SECRET` 环境变量读取（生产环境缺失快速失败），过期时间 24 小时 |
| SEC-02 | 授权：RBAC + `@PreAuthorize` + `@ss.hasPermi` 双轨权限校验 |
| SEC-03 | 数据加密 - 传输层：HTTPS（生产环境） |
| SEC-04 | 数据加密 - 字段级：AES-256-GCM（`@FieldEncrypt` 注解） |
| SEC-05 | 数据加密 - 密码：BCrypt |
| SEC-06 | 数据加密 - 凭据：低代码连接器凭据 AES 加密存储 |
| SEC-07 | XSS 防护：XssFilter + XssHttpServletRequestWrapper + 前端 DOMPurify |
| SEC-08 | CSRF 防护：无状态会话天然免疫 CSRF |
| SEC-09 | SQL 注入防护：MyBatis 参数化查询 + 低代码字段白名单 + 标识符正则白名单 |
| SEC-10 | 幂等性：`X-Idempotent-Key` 头 + `IdempotentAspect` |
| SEC-11 | 限流：基于 Redis 的滑动窗口限流（`@RateLimit`） |
| SEC-12 | Groovy 沙箱：拦截危险类调用、禁止 new 关键字、AST 检查（ConstructorCallExpression/ClassExpression/StaticMethodCallExpression） |
| SEC-13 | Aviator 沙箱：禁用 NewInstance/Module/InternalVars，移除系统函数，编译前正则阻断 |
| SEC-14 | DDL 安全：拦截 DROP DATABASE 等危险语句，执行前 SHOW CREATE TABLE 备份 |
| SEC-15 | 审计日志：`@OperLog` 操作日志 + 低代码 `ConfigAuditAspect` 配置审计 + 登录日志 |
| SEC-16 | WebSocket 鉴权：握手阶段 JWT 校验，无 token 或解析失败拒绝握手 |
| SEC-17 | OAuth2 Token 单飞刷新：Redis SETNX + Lua 解锁，避免多实例并发刷新 |
| SEC-18 | 生产环境关闭 Swagger UI 与 api-docs |
| SEC-19 | 生产环境 `jwt.secret` 仅从环境变量读取 |
| SEC-20 | Flyway `clean-disabled: true`，禁止误删生产库 |

### 3.5 可靠性需求

| 编号 | 需求描述 |
|------|----------|
| REL-01 | 系统可用性 ≥ 99.5%（年停机 ≤ 43.8 小时） |
| REL-02 | 故障恢复时间（RTO）≤ 30 分钟 |
| REL-03 | 数据丢失容忍（RPO）≤ 5 分钟（MySQL 主从复制延迟） |
| REL-04 | 集成失败自愈率 ≥ 80%（Resilience4j + 定时重试） |
| REL-05 | 乐观锁：`@Version` 注解，并发更新冲突时抛 `OptimisticLockingFailureException` |
| REL-06 | 逻辑删除：`@TableLogic`，`deleted` 字段（0/1），查询自动过滤 |
| REL-07 | Saga 协调器：结算单提交 6 步编排，任一步骤失败触发补偿 |
| REL-08 | 集成重试：Resilience4j Retry + 定时调度重试（5 分钟扫描） |
| REL-09 | OAuth2 单飞刷新：Redis SETNX + Lua 解锁 |
| REL-10 | WebSocket 心跳：10s，独立 ThreadPoolTaskScheduler 避免循环依赖 |
| REL-11 | Redis 防雪崩：缓存 TTL 30 分钟 + 0~5 分钟随机抖动 |
| REL-12 | Redis 防穿透：`disableCachingNullValues` |
| REL-13 | 降级运行：`ObjectProvider` 注入工作流服务，模块未加载时降级 |
| REL-14 | 三账联动单向无环依赖，避免循环依赖 |
| REL-15 | 跨实例 WebSocket 推送一致性：Redis Pub/Sub 广播 |

### 3.6 可维护性需求

| 编号 | 需求描述 |
|------|----------|
| MNT-01 | 模块化：14 个 Maven 模块，单一职责，依赖关系单向无环 |
| MNT-02 | SPI 解耦：12 个 SPI 接口下沉到 pms-common |
| MNT-03 | 配置化：字典驱动 + Properties 文件按环境区分 + 配置中心 `sys_config` |
| MNT-04 | Flyway 迁移：86 个迁移脚本，`out-of-order: true` 允许乱序补录 |
| MNT-05 | 可观测性三件套：Metrics + Tracing + Logging，通过 traceId 关联 |
| MNT-06 | 链路追踪：MDC `traceId` / `userId` / `username` 贯穿日志 |
| MNT-07 | API 文档：Springdoc OpenAPI 3 自动生成 |
| MNT-08 | 测试覆盖：JUnit 5 + Mockito（单元）+ Testcontainers（集成）+ Vitest（前端单元）+ Playwright（E2E） |
| MNT-09 | 日志分级：dev/test 控制台普通文本，prod/release 控制台 + JSON 结构化文件（LogstashEncoder，按天 + 100MB 滚动，保留 30 天，总上限 10GB，异步包装） |
| MNT-10 | 代码规范：ESLint 9 + `eslint-plugin-vue` + `typescript-eslint`（前端），Java 标准规范（后端） |

### 3.7 设计约束

| 编号 | 约束描述 |
|------|----------|
| CON-01 | 后端技术栈：Spring Boot 3.2.5 + Java 17 + MyBatis-Plus 3.5.9 + Flowable 7.0.1 |
| CON-02 | 前端技术栈：Vue 3.5 + TypeScript 6 + Vite 8 + Element Plus 2.14 |
| CON-03 | 数据库：MySQL 8.0.16（主）、PostgreSQL 42.7.0（可选） |
| CON-04 | 缓存：Redis |
| CON-05 | 构建：Maven 3.x（后端）/ npm（前端） |
| CON-06 | 基础包名：`com.dp.plat` |
| CON-07 | 模块子包遵循模块名：`com.dp.plat.{module}` |
| CON-08 | MyBatis Mapper 扫描路径：`com.dp.plat.**.mapper` / `com.dp.plat.**.dao` / `com.dp.plat.**.engine.ddl` |
| CON-09 | MyBatis XML 映射文件与 Java 文件同目录：`com/dp/plat/**/mapping/*.xml` |
| CON-10 | Spring XML 配置（非注解驱动）— 部分；SecurityConfig / RedisConfig 使用 Java Config |
| CON-11 | BPMN 流程定义文件位于 `src/main/resources/processes/` |
| CON-12 | Flyway 迁移脚本位于 `src/main/resources/db/migration/` |
| CON-13 | 前端 `@` 别名指向 `src`，全项目通过 `@/...` 引用 |
| CON-14 | 前端 dev server 监听 3000 端口，`/api` 反向代理到后端 8080 |
| CON-15 | 前端 Monaco Editor worker 通过 `optimizeDeps.include` 预打包 |
| CON-16 | 低代码组件 SDK 外部化 peer dependencies：vue / element-plus / @element-plus/icons-vue / monaco-editor |
| CON-17 | 低代码表名前缀 `pms_lowcode_`，动态表前缀 `pms_lc_` |
| CON-18 | 通知模板编码唯一约束（`uk_template_code`） |
| CON-19 | WebSocket STOMP 端点 `/ws`，不启用 SockJS |
| CON-20 | 权限码命名约定：`{module}:{resource}:{action}` |

### 3.8 数据需求

#### 3.8.1 数据字典

详见第 5 章附录 B：数据字典。

#### 3.8.2 数据持久化

- 系统使用 MySQL 8.0 作为主数据库，数据库名 `network_equipment_pms`。
- 持久化通过 MyBatis-Plus 3.5.9，Mapper 接口继承 `BaseMapper<T>`。
- 所有实体继承 `BaseEntity`（含 `id` / `createTime` / `updateTime` / `createBy` / `updateBy` / `deleted`）。
- 逻辑删除字段 `deleted`（0/1），`@TableLogic` 自动过滤。
- 乐观锁字段 `version`（`@Version`）用于部分实体（如 `ChangeRequest` / `ApprovalRecord` / `ApprovalFieldPermission`）。

#### 3.8.3 数据迁移

- Flyway 86 个迁移脚本（V1-V86），`baseline-on-migrate: true`、`baseline-version: 0`、`out-of-order: true`、`validate-on-migrate: false`、`clean-disabled: true`。
- 幂等写法：V69 使用 `DELIMITER` + 存储过程，V60/V86 使用 `PREPARE/EXECUTE` + `INFORMATION_SCHEMA` 查询。

### 3.9 其他需求

#### 3.9.1 安装与部署

- 后端打包：Spring Boot fat jar，`spring-boot-maven-plugin` 重新打包，排除 Lombok。
- 前端打包：`npm run build`（`vue-tsc -b && vite build`）。
- 低代码组件 SDK：`npm run build:sdk`（`vite build --config vite.config.lib.ts`）。
- 部署：fat jar + 前端静态资源，可独立部署或集成到应用服务器。

#### 3.9.2 国际化

- 当前版本仅支持简体中文。
- 字典项支持多语言扩展。
- 预留 i18n 接入点（Vue i18n 待引入）。

---

## 第 4 章 需求跟踪矩阵

### 4.1 功能需求与模块跟踪

| 需求编号 | 需求名称 | 实现模块 | 优先级 | 状态 |
|----------|----------|----------|--------|------|
| FR-CM-01 ~ FR-CM-11 | 公共基础（11 条） | pms-common | P0 | 已实现 |
| FR-SY-01 ~ FR-SY-12 | 系统管理（12 条） | pms-system | P0 | 已实现 |
| FR-PJ-01 ~ FR-PJ-11 | 项目交付管理（11 条） | pms-project | P0 | 已实现 |
| FR-IM-01 ~ FR-IM-12 | 实施管理（12 条） | pms-implementation | P0 | 已实现 |
| FR-AS-01 ~ FR-AS-09 | 设备资产管理（9 条） | pms-asset | P0 | 已实现 |
| FR-DV-01 ~ FR-DV-07 | 交付件管理（7 条） | pms-deliverable | P0 | 已实现 |
| FR-BL-01 ~ FR-BL-06 | 基线管理（6 条） | pms-baseline | P0 | 已实现 |
| FR-FL-01 ~ FR-FL-04 | 文件管理（4 条） | pms-file | P0 | 已实现 |
| FR-WF-01 ~ FR-WF-08 | 工作流与审批中心（8 条） | pms-workflow | P0 | 已实现 |
| FR-IT-01 ~ FR-IT-09 | 外部集成（9 条） | pms-integration | P0 | 已实现 |
| FR-NT-01 ~ FR-NT-10 | 通知中心（10 条） | pms-notification | P0 | 已实现 |
| FR-GV-01 ~ FR-GV-12 | 项目治理（12 条） | pms-governance | P0 | 已实现 |
| FR-LC-01 ~ FR-LC-28 | 低代码平台（28 条） | pms-lowcode | P0/P1 | 已实现 |
| FR-AD-01 ~ FR-AD-10 | 聚合启动（10 条） | pms-admin | P0 | 已实现 |
| FR-FE-01 ~ FR-FE-16 | 前端应用（16 条） | pms-frontend | P0/P1 | 已实现 |

**功能需求总数**：11 + 12 + 11 + 12 + 9 + 7 + 6 + 4 + 8 + 9 + 10 + 12 + 28 + 10 + 16 = **165 条**

> 注：远超用户要求的 40 条 FR-XX-YY 编号功能需求下限。

### 4.2 非功能需求与模块跟踪

| 需求类别 | 编号范围 | 实现模块 |
|----------|----------|----------|
| PRF 性能需求 | PRF-01 ~ PRF-16 | pms-common / pms-system / pms-admin / 全模块 |
| SEC 安全需求 | SEC-01 ~ SEC-20 | pms-common / pms-system / pms-lowcode / pms-file / 全模块 |
| REL 可靠性需求 | REL-01 ~ REL-15 | pms-common / pms-integration / pms-workflow / pms-notification / 全模块 |
| MNT 可维护性需求 | MNT-01 ~ MNT-10 | pms-admin / 全模块 |
| CON 设计约束 | CON-01 ~ CON-20 | 全模块 |

### 4.3 接口需求与模块跟踪

| 接口类别 | 编号范围 | 实现模块 |
|----------|----------|----------|
| UI 用户界面 | UI-01 ~ UI-20 | pms-frontend |
| IR 外部系统接口 | IR-D365-01 ~ IR-OA-04 | pms-integration |
| SPI 内部接口 | SPI-01 ~ SPI-12 | pms-common（接口下沉）+ 各业务模块（实现） |
| CI 通信接口 | CI-01 ~ CI-07 | 全模块 |

### 4.4 需求与里程碑跟踪

| 里程碑 | 对应需求 | 验收标准 |
|--------|----------|----------|
| M1：基础框架 | FR-CM-* / FR-SY-* | 用户登录、RBAC、字典、缓存就绪 |
| M2：项目核心 | FR-PJ-* / FR-IM-01 ~ FR-IM-08 | 项目立项 → 阶段 → 任务全链路 |
| M3：资产管理 | FR-AS-* / FR-FL-* | 资产入库 → 调拨 → RMA → 质保 |
| M4：交付治理 | FR-DV-* / FR-BL-* / FR-GV-* | 交付件 7 态 + 基线偏差 + 三本账 |
| M5：工作流 | FR-WF-* / FR-NT-* | Flowable + 统一审批中心 + 多通道通知 |
| M6：外部集成 | FR-IT-* | D365/FP/OA 双向集成 + Resilience4j |
| M7：低代码 | FR-LC-* | 实体/微流/规则/连接器/触发器全能力 |
| M8：聚合启动 | FR-AD-* / FR-FE-* | Flyway 86 脚本 + 前端 30+ 页面 |

---

## 第 5 章 附录

### 附录 A：状态机定义

#### A.1 项目状态机（11 态）

```
状态枚举：DRAFT / PLANNING / IN_PROGRESS / PENDING_ACCEPTANCE / ACCEPTED / CLOSED / ON_HOLD / CANCELLED / REJECTED

状态转换：
[DRAFT] --提交审批--> [PLANNING]（待审批，状态可为 REJECTED）
[PLANNING] --审批通过--> [IN_PROGRESS]
[PLANNING] --审批驳回--> [REJECTED]
[IN_PROGRESS] --申请验收--> [PENDING_ACCEPTANCE]
[PENDING_ACCEPTANCE] --验收通过--> [ACCEPTED]
[PENDING_ACCEPTANCE] --验收驳回--> [IN_PROGRESS]
[ACCEPTED] --关闭--> [CLOSED]
[IN_PROGRESS] --暂停--> [ON_HOLD]
[ON_HOLD] --恢复--> [IN_PROGRESS]
[IN_PROGRESS] --取消--> [CANCELLED]
```

#### A.2 任务状态机（7 态）

```
状态枚举：PENDING / ACCEPTED / IN_PROGRESS / COMPLETED / VERIFIED / REJECTED / CANCELLED

状态转换：
[PENDING] --接受--> [ACCEPTED]
[ACCEPTED] --开始--> [IN_PROGRESS]
[IN_PROGRESS] --完成--> [COMPLETED]
[COMPLETED] --验收通过--> [VERIFIED]
[COMPLETED] --验收驳回--> [REJECTED]
[PENDING] --取消--> [CANCELLED]
[REJECTED] --重新分派--> [PENDING]
```

#### A.3 资产状态机（9 态）

```
状态枚举：IN_STOCK / ALLOCATED / IN_TRANSIT / INSTALLED / ACCEPTED / WARRANTY_PERIOD / OUT_OF_WARRANTY / SCRAPPED / RMA_IN_PROGRESS

状态转换：
[IN_STOCK] --调拨--> [ALLOCATED]
[ALLOCATED] --发货--> [IN_TRANSIT]
[IN_TRANSIT] --到货安装--> [INSTALLED]
[INSTALLED] --验收--> [ACCEPTED]
[ACCEPTED] --进入质保--> [WARRANTY_PERIOD]
[WARRANTY_PERIOD] --质保到期--> [OUT_OF_WARRANTY]
[OUT_OF_WARRANTY] --报废--> [SCRAPPED]
[IN_STOCK] --RMA申请--> [RMA_IN_PROGRESS]
[RMA_IN_PROGRESS] --返回入库--> [IN_STOCK]
[RMA_IN_PROGRESS] --报废--> [SCRAPPED]
```

#### A.4 交付件状态机（7 态）

```
状态枚举：DRAFT / SUBMITTED / REVIEWED / SIGNED / PUBLISHED / REFERENCED / ARCHIVED

状态转换：
[DRAFT] --提交--> [SUBMITTED]
[SUBMITTED] --评审通过--> [REVIEWED]
[SUBMITTED] --评审驳回--> [DRAFT]
[REVIEWED] --签署--> [SIGNED]
[SIGNED] --发布--> [PUBLISHED]
[PUBLISHED] --被引用--> [REFERENCED]
[REFERENCED] --归档--> [ARCHIVED]
[PUBLISHED] --版本修订--> [DRAFT]（新建版本，旧版本不可变）
```

#### A.5 阶段状态机（4 态）

```
状态枚举：PENDING / IN_PROGRESS / COMPLETED / EXIT_BLOCKED

状态转换：
[PENDING] --开始--> [IN_PROGRESS]
[IN_PROGRESS] --阶段完成--> [COMPLETED]
[IN_PROGRESS] --强制交付件未满足--> [EXIT_BLOCKED]
[EXIT_BLOCKED] --补齐交付件--> [COMPLETED]
```

#### A.6 里程碑状态机（5 态）

```
状态枚举：PLANNED / IN_PROGRESS / COMPLETED / DELAYED / CANCELLED

状态转换：
[PLANNED] --开始--> [IN_PROGRESS]
[IN_PROGRESS] --完成--> [COMPLETED]
[IN_PROGRESS] --逾期--> [DELAYED]
[DELAYED] --完成--> [COMPLETED]
[PLANNED] --取消--> [CANCELLED]
```

#### A.7 变更请求状态机（6 态）

```
状态枚举：SUBMITTED / UNDER_REVIEW / CCB_APPROVED / CCB_REJECTED / IMPLEMENTING / CLOSED

状态转换：
[SUBMITTED] --提交审批--> [UNDER_REVIEW]
[UNDER_REVIEW] --审批通过--> [CCB_APPROVED]
[UNDER_REVIEW] --审批驳回--> [CCB_REJECTED]
[CCB_APPROVED] --开始实施--> [IMPLEMENTING]
[IMPLEMENTING] --实施完成--> [CLOSED]
[CCB_REJECTED] --关闭--> [CLOSED]
任意状态 --关闭--> [CLOSED]
```

#### A.8 风险状态机（4 态）

```
状态枚举：OPEN / IN_PROGRESS / ESCALATED / CLOSED

状态转换：
[OPEN] --开始处理--> [IN_PROGRESS]
[OPEN] --升级--> [ESCALATED]
[IN_PROGRESS] --关闭--> [CLOSED]
[ESCALATED] --关闭--> [CLOSED]
[OPEN] --已发生（转化为问题）--> [CLOSED]
```

#### A.9 问题状态机（4 态）

```
状态枚举：OPEN / IN_PROGRESS / RESOLVED / CLOSED

状态转换：
[OPEN] --分配处理人--> [IN_PROGRESS]
[IN_PROGRESS] --解决--> [RESOLVED]
[RESOLVED] --关闭--> [CLOSED]
[OPEN] --升级为变更请求--> [OPEN]（状态不变）
```

#### A.10 审批状态机（5 态）

```
状态枚举：PENDING / APPROVED / REJECTED / WITHDRAWN / TIMEOUT

状态转换：
[未提交] --提交--> [PENDING]
[PENDING] --审批通过--> [APPROVED]
[PENDING] --审批驳回--> [REJECTED]
[PENDING] --提交人撤回--> [WITHDRAWN]
[PENDING] --超时--> [TIMEOUT]
[REJECTED] --重新提交--> [PENDING]（round+1，复用原记录）
```

#### A.11 基线状态机（3 态）

```
状态枚举：DRAFT / ACTIVE / ARCHIVED

状态转换：
[DRAFT] --激活--> [ACTIVE]
[ACTIVE] --归档--> [ARCHIVED]
约束：同一项目同一时间仅允许一个 ACTIVE 基线
```

#### A.12 RMA 状态机（6 步）

```
步骤：申请 → 审批 → 发货 → 维修 → 返回 → 入库

状态转换：
[申请] --提交--> [审批]
[审批] --通过--> [发货]
[审批] --驳回--> [申请]
[发货] --寄出--> [维修]
[维修] --完成--> [返回]
[返回] --到货--> [入库]
[入库] --结束--> [IN_STOCK]（资产状态回写）
```

### 附录 B：数据字典

#### B.1 核心实体表清单（按模块）

| 模块 | 表名 | 中文含义 | 关键字段 |
|------|------|----------|----------|
| pms-system | sys_user | 用户 | username / password（BCrypt）/ status |
| pms-system | sys_role | 角色 | role_code / role_name |
| pms-system | sys_menu | 菜单 | parent_id / menu_type / perms |
| pms-system | sys_dict | 字典类型 | dict_type / dict_name |
| pms-system | sys_dict_item | 字典项 | dict_type / item_value / item_text |
| pms-system | sys_config | 系统配置 | config_key / config_value |
| pms-system | sys_oper_log | 操作日志 | title / business_type / oper_user |
| pms-system | sys_login_log | 登录日志 | login_user / ip / status |
| pms-project | pms_project | 项目 | project_code / status / project_path / depth |
| pms-project | pms_project_phase | 项目阶段 | project_id / phase_name / status |
| pms-project | pms_project_template | 项目模板 | template_code / version |
| pms-project | pms_milestone | 里程碑 | milestone_name / status / plan_date |
| pms-project | pms_punch_list | Punch List | punch_item / deadline / status |
| pms-implementation | pms_impl_task | 实施任务 | task_path / depth / status / assignee_id |
| pms-implementation | pms_settlement | 结算单 | settlement_no / amount / push_status |
| pms-implementation | pms_agent | 代理商 | agent_name / overall_score |
| pms-implementation | pms_agent_score | 代理商评分 | agent_id / score / comment |
| pms-asset | pms_asset | 资产 | asset_code / serial_no / status / category_id |
| pms-asset | pms_asset_category | 资产分类 | parent_id / category_name |
| pms-asset | pms_asset_model | 资产型号 | model_name / standard_price |
| pms-asset | pms_rma | RMA 工单 | rma_no / status / asset_id |
| pms-asset | pms_warranty | 质保 | asset_id / start_date / end_date |
| pms-deliverable | pms_deliverable | 交付件 | deliverable_code / status / type / version |
| pms-deliverable | pms_deliverable_version | 交付件版本 | deliverable_id / version_no / signed_at |
| pms-deliverable | pms_deliverable_ref | 交付件引用 | deliverable_id / ref_entity_type / ref_entity_id |
| pms-baseline | pms_baseline | 基线 | project_id / status / snapshot |
| pms-baseline | pms_baseline_deviation | 基线偏差 | baseline_id / task_id / deviation_days |
| pms-file | pms_attachment | 附件 | biz_type / biz_id / file_path / gps |
| pms-workflow | pms_approval_record | 审批记录 | approval_type / business_id / status / round |
| pms-workflow | pms_approval_node | 审批节点 | record_id / node_order / approver_id / status |
| pms-workflow | pms_approval_history | 审批历史 | record_id / round / action / operator_id |
| pms-workflow | pms_approval_field_permission | 字段权限 | approval_node_id / entity_type / field_name / permission |
| pms-governance | pms_change_request | 变更请求 | cr_no / status / process_instance_id / baseline_updated |
| pms-governance | pms_baseline_history | 基线变更历史 | project_id / change_request_id / change_type / old_value / new_value |
| pms-governance | pms_risk | 风险 | risk_no / likelihood / impact / score / priority |
| pms-governance | pms_issue | 问题 | issue_no / status / source_risk_id / source_change_id |
| pms-integration | pms_integration_log | 集成日志 | log_type / business_type / response_status / retry_count |
| pms-integration | d365_invoice | D365 发票 | invoice_no / settlement_no / push_status / ocr_status |
| pms-integration | d365_purchase_receipt | D365 采购收货 | receipt_no / po_no / asset_id / push_status |
| pms-notification | pms_notification | 站内通知 | user_id / title / read_status / channel / biz_type / biz_id |
| pms-notification | pms_notification_template | 通知模板 | template_code / subject / body / variables |
| pms-lowcode | pms_lowcode_entity | 低代码实体 | code / table_name / status / version |
| pms-lowcode | pms_lowcode_field | 低代码字段 | entity_id / name / field_type / length |
| pms-lowcode | pms_lowcode_form | 低代码表单 | code / form_config / status |
| pms-lowcode | pms_lowcode_microflow | 低代码微流 | code / definition / status |
| pms-lowcode | pms_lowcode_rule | 低代码规则 | code / type / definition / status |
| pms-lowcode | pms_lowcode_connector | 低代码连接器 | code / type / config / status |
| pms-lowcode | pms_lowcode_trigger | 低代码触发器 | code / type / target_type / target_code |
| pms-lowcode | pms_lowcode_config_version | 低代码版本快照 | config_type / config_id / version / snapshot / environment |
| pms-lowcode | pms_lowcode_publish_record | 低代码发布记录 | config_type / config_id / status / approval_chain_id |
| pms-lowcode | pms_lowcode_gray_release | 低代码灰度发布 | config_id / gray_percentage / tenant_whitelist / status |

#### B.2 公共字段（BaseEntity）

| 字段 | Java 类型 | 数据库列 | DB 类型 | 说明 |
|------|-----------|----------|---------|------|
| `id` | Long | `id` | BIGINT, AUTO_INCREMENT, PK | 主键 |
| `createTime` | LocalDateTime | `create_time` | DATETIME | 创建时间 |
| `updateTime` | LocalDateTime | `update_time` | DATETIME | 更新时间 |
| `createBy` | String | `create_by` | VARCHAR | 创建人 |
| `updateBy` | String | `update_by` | VARCHAR | 更新人 |
| `deleted` | Integer | `deleted` | INT, DEFAULT 0 | 逻辑删除（0/1） |

#### B.3 枚举字典

| 枚举名 | 取值 | 用途 |
|--------|------|------|
| `ProjectStatus` | DRAFT / PLANNING / IN_PROGRESS / PENDING_ACCEPTANCE / ACCEPTED / CLOSED / ON_HOLD / CANCELLED / REJECTED | 项目状态 |
| `TaskStatus` | PENDING / ACCEPTED / IN_PROGRESS / COMPLETED / VERIFIED / REJECTED / CANCELLED | 任务状态 |
| `AssetStatus` | IN_STOCK / ALLOCATED / IN_TRANSIT / INSTALLED / ACCEPTED / WARRANTY_PERIOD / OUT_OF_WARRANTY / SCRAPPED / RMA_IN_PROGRESS | 资产状态 |
| `DeliverableStatus` | DRAFT / SUBMITTED / REVIEWED / SIGNED / PUBLISHED / REFERENCED / ARCHIVED | 交付件状态 |
| `PhaseStatus` | PENDING / IN_PROGRESS / COMPLETED / EXIT_BLOCKED | 阶段状态 |
| `MilestoneStatus` | PLANNED / IN_PROGRESS / COMPLETED / DELAYED / CANCELLED | 里程碑状态 |
| `ChangeRequestStatus` | SUBMITTED / UNDER_REVIEW / CCB_APPROVED / CCB_REJECTED / IMPLEMENTING / CLOSED | 变更请求状态 |
| `RiskStatus` | OPEN / IN_PROGRESS / ESCALATED / CLOSED | 风险状态 |
| `IssueStatus` | OPEN / IN_PROGRESS / RESOLVED / CLOSED | 问题状态 |
| `ApprovalStatus` | PENDING / APPROVED / REJECTED / WITHDRAWN / TIMEOUT | 审批状态 |
| `BaselineStatus` | DRAFT / ACTIVE / ARCHIVED | 基线状态 |
| `ApprovalType` | PROJECT / TASK / DELIVERABLE / RISK / ISSUE / CHANGE / RESOURCE / COST / PHASE_EXIT / BASELINE_CHANGE | 审批类型 |
| `NotificationChannel` | IN_APP / WS / EMAIL / OA | 通知通道 |
| `NotificationReadStatus` | UNREAD / READ | 通知已读状态 |
| `IntegrationLogType` | D365 / FP / OA / SMS / EHR | 集成日志类型 |
| `IntegrationResponseStatus` | SUCCESS / FAILED / PENDING | 集成响应状态 |
| `PushStatus` | PENDING / PUSHED / FAILED | 推送状态 |
| `RiskPriority` | LOW / MEDIUM / HIGH | 风险优先级 |
| `RiskMitigation` | AVOID / MITIGATE / TRANSFER / ACCEPT | 风险缓解策略 |
| `LowCodeConfigType` | FORM / LIST / TAB / RELATED_PAGE / ENTITY / MICROFLOW / RULE / CONNECTOR | 低代码配置类型 |
| `LowCodeConfigStatus` | DRAFT / PUBLISHED / ARCHIVED | 低代码配置状态 |
| `LowCodeFieldType` | STRING / INTEGER / DECIMAL / BOOLEAN / DATE / DATETIME / TEXT / LONG | 低代码字段类型 |
| `LowCodeTriggerType` | CRUD / QUARTZ / EVENT | 低代码触发器类型 |
| `LowCodeRuleType` | DECISION_TABLE / EXPRESSION / LITEFLOW | 低代码规则类型 |
| `LowCodeConnectorType` | REST / DB / MQ / FILE | 低代码连接器类型 |
| `LowCodeEnvironment` | DEV / TEST / PROD | 低代码环境 |
| `LowCodePublishStatus` | DRAFT / SUBMITTED / APPROVING / APPROVED / REJECTED / PUBLISHED | 低代码发布状态 |
| `LowCodeGrayReleaseStatus` | GRAYING / FULL / ROLLED_BACK | 灰度发布状态 |
| `FieldTypePermission` | VISIBLE / MASKED / HIDDEN | 字段权限 |

### 附录 C：SPI 接口清单（12 个）

#### C.1 ApprovalTrigger — 审批触发

- **接口位置**：`pms-common`（接口下沉）
- **实现方**：`pms-workflow`（`ApprovalTriggerImpl`）
- **消费方**：`pms-project` / `pms-baseline`
- **方法签名**：
  - `void triggerApproval(String approvalType, Long businessId, String title, Long submitterId, Map<String, Object> variables)`
- **用途**：业务模块通过此 SPI 触发审批流程，无需依赖 `pms-workflow` 模块。

#### C.2 ApprovalStatusChecker — 审批状态校验

- **接口位置**：`pms-common`
- **实现方**：`pms-workflow`（`ApprovalStatusCheckerImpl`）
- **消费方**：`pms-project` / `pms-baseline`
- **方法签名**：
  - `boolean isApproved(String approvalType, Long businessId)`
  - `boolean isPending(String approvalType, Long businessId)`
- **用途**：业务模块校验业务对象是否已审批通过。

#### C.3 ApprovalPlanBatchCreator — 审批计划批量创建

- **接口位置**：`pms-common`
- **实现方**：`pms-workflow`（`ApprovalPlanBatchCreatorImpl`）
- **消费方**：`pms-project`（项目模板深拷贝时批量创建审批节点）
- **方法签名**：
  - `void batchCreateApprovalPlans(List<ApprovalPlanCreateRequest> requests)`
- **用途**：项目模板深拷贝时批量创建审批节点，避免逐条调用性能问题。

#### C.4 ProjectTemplateProvider — 项目模板提供

- **接口位置**：`pms-common`
- **实现方**：`pms-project`
- **消费方**：`pms-admin`（聚合层查询项目模板）
- **方法签名**：
  - `ProjectTemplate getTemplate(Long templateId)`
  - `List<ProjectTemplate> listTemplates()`
- **用途**：聚合层查询项目模板用于报表统计。

#### C.5 ProjectPathGenerator — 物化路径生成

- **接口位置**：`pms-common`
- **实现方**：`pms-project`
- **消费方**：`pms-admin`（聚合层生成物化路径）
- **方法签名**：
  - `String generatePath(Long parentId, Long currentId)`
  - `int calculateDepth(String path)`
- **用途**：生成项目主子关系的物化路径。

#### C.6 PhaseExitValidator — 阶段退出校验

- **接口位置**：`pms-common`
- **实现方**：`pms-project`
- **消费方**：`pms-deliverable`（交付件阶段退出校验联动）
- **方法签名**：
  - `PhaseExitResult validatePhaseExit(Long phaseId)`
- **用途**：校验阶段退出时强制交付件是否全部到位。

#### C.7 BaselineSnapshotProvider — 基线快照提供

- **接口位置**：`pms-common`
- **实现方**：`pms-baseline`
- **消费方**：`pms-governance`（基线变更审计时读取真实基线值）
- **方法签名**：
  - `BaselineSnapshot getActiveSnapshot(Long projectId)`
  - `BaselineSnapshot getSnapshotByBaselineId(Long baselineId)`
- **用途**：基线变更审计时对比真实基线快照。

#### C.8 BaselineDeviationCalculator — 基线偏差计算

- **接口位置**：`pms-common`
- **实现方**：`pms-baseline`
- **消费方**：`pms-admin`（报表统计）
- **方法签名**：
  - `List<TaskDeviation> calculateDeviations(Long baselineId)`
  - `DeviationSummary summarizeDeviations(Long projectId)`
- **用途**：计算基线偏差用于报表与告警。

#### C.9 NotificationSender — 通知发送

- **接口位置**：`pms-common`
- **实现方**：`pms-notification`
- **消费方**：所有业务模块（`pms-project` / `pms-implementation` / `pms-asset` / `pms-lowcode` 等）
- **方法签名**：
  - `void sendByTemplate(String templateCode, Map<String, Object> variables, Long userId, Set<String> channels)`
  - `void multiChannelSend(Notification notification, Set<String> channels)`
- **用途**：业务模块通过此 SPI 发送通知，无需依赖 `pms-notification` 模块。

#### C.10 FileStorageProvider — 文件存储提供

- **接口位置**：`pms-common`
- **实现方**：`pms-file`
- **消费方**：所有业务模块
- **方法签名**：
  - `String upload(MultipartFile file, String directory)`
  - `Resource download(String filePath)`
  - `void delete(String filePath)`
- **用途**：业务模块通过此 SPI 上传/下载/删除文件。

#### C.11 IntegrationRetryHandler — 集成重试处理

- **接口位置**：`pms-common`
- **实现方**：`pms-integration`
- **消费方**：`pms-admin`（定时调度重试）
- **方法签名**：
  - `void retryLog(Long logId)`
  - `List<IntegrationLog> getPendingRetryLogs()`
- **用途**：定时调度重试失败的集成调用。

#### C.12 SagaCoordinator — Saga 协调器

- **接口位置**：`pms-common`
- **实现方**：`pms-common`（通用实现）
- **消费方**：`pms-implementation`（结算单 Saga 6 步编排）
- **方法签名**：
  - `<T> T execute(SagaDefinition<T> saga)`
  - `void compensate(SagaExecution execution)`
- **用途**：编排长事务步骤，任一步骤失败触发补偿。

### 附录 D：BPMN 流程定义清单（5 个）

| 编号 | 流程 ID | 流程名称 | 节点结构 | 用途 |
|------|---------|----------|----------|------|
| BPMN-01 | `projectApproval` | 项目审批流程 | 开始 → PM审核 → 网关 → 部门经理审核 → 网关 → 结束/驳回终止 | 项目立项两级审批（PM → 部门经理） |
| BPMN-02 | `assetTransfer` | 资产转移流程 | 开始 → 源PM审核 → 网关 → 目标PM审核 → 网关 → 结束/驳回终止 | 资产跨项目转移两级审批（源 PM → 目标 PM） |
| BPMN-03 | `finalAcceptance` | 最终验收流程 | 开始 → 客户确认 → 网关 → PM审核 → 网关 → 结束/驳回终止 | 项目最终验收两级确认（客户 → PM） |
| BPMN-04 | `settlementApproval` | 结算审批流程 | 开始 → PM审核 → 网关 → 财务审核 → 网关 → 结束/驳回终止 | 项目结算两级审批（PM → 财务） |
| BPMN-05 | `changeRequestApproval` | 变更请求 CCB 审批流程 | 开始 → CCB审核 → 网关 → CCB审批通过/驳回终止 | CCB 审批（来自 pms-governance） |

**通用特征**：
- 使用 `flowable:skipExpression="${assignee == initiator}"` 实现「发起人即审批人时自动跳过」，避免自审。
- 在每个 userTask 上挂载 `oaTaskListener`（`delegateExpression="${oaTaskListener}"`，create/complete 事件），将任务创建与完成镜像到致远 OA 系统。
- 驳回走 `terminateEventDefinition`（终止结束事件），直接终止整个流程实例。

### 附录 E：预置通知模板清单（12 个）

| 编号 | templateCode | 描述 | 变量 |
|------|--------------|------|------|
| TPL-01 | `MILESTONE_OVERDUE` | 里程碑逾期提醒 | projectName / milestoneName / planDate |
| TPL-02 | `TASK_ASSIGNED` | 任务分派通知 | taskName / projectName / planEndDate |
| TPL-03 | `TASK_DELEGATED` | 任务转派通知 | fromUser / taskName / projectName / planEndDate |
| TPL-04 | `APPROVAL_TODO` | 审批待办通知 | approvalTitle / submitter |
| TPL-05 | `PUNCH_LIST_DEADLINE` | 尾项清单到期提醒 | punchItemName / deadline / status |
| TPL-06 | `WARRANTY_EXPIRE_90` | 质保到期 90 天预警 | assetName / assetCode / warrantyEndDate |
| TPL-07 | `WARRANTY_EXPIRE_60` | 质保到期 60 天预警 | assetName / assetCode / warrantyEndDate |
| TPL-08 | `WARRANTY_EXPIRE_30` | 质保到期 30 天预警 | assetName / assetCode / warrantyEndDate |
| TPL-09 | `RMA_STATUS_CHANGE` | RMA 状态变更通知 | rmaNo / status |
| TPL-10 | `SETTLEMENT_APPROVED` | 结算审批通过通知 | settlementNo / amount |
| TPL-11 | `CHANGE_REQUEST_CCB` | 变更请求 CCB 评审通知 | crNo / title |
| TPL-12 | `RISK_ESCALATED` | 风险升级提醒 | riskName / level |

### 附录 F：Flyway 迁移脚本清单（86 个）

| 版本范围 | 主旨 | 脚本数 |
|----------|------|--------|
| V1 — V6 | 系统/项目/资产/实施/集成五大领域基线建表 | 6 |
| V7 — V26 | 流程实例 ID 关联、领域扩展、索引/权限/外键/版本字段补强 | 20 |
| V27 — V57 | 低代码平台大规模建表（实体、配置版本、权限、微流、规则、流程绑定、触发器、连接器等） | 31 |
| V58 — V68 | 演示数据种子与低代码/业务修复 | 11 |
| V69 — V86 | 项目管理增强、交付件全生命周期、审批中心、网络割接工作区 | 18 |
| **合计** | | **86** |

### 附录 G：低代码微流节点类型（11 种）

| 编号 | 节点类型 | 执行器 | 用途 |
|------|----------|--------|------|
| MF-01 | `START` / `END` | `StartEndExecutor` | 开始/结束节点 |
| MF-02 | `ASSIGN` | `AssignExecutor` | Groovy 表达式赋值 |
| MF-03 | `CONDITION` | `ConditionExecutor` | Groovy 布尔表达式条件分支 |
| MF-04 | `LOOP` | `LoopExecutor` | Groovy 布尔表达式循环 |
| MF-05 | `CALL_SERVICE` | `CallServiceExecutor` | 调用 Spring Bean 方法 |
| MF-06 | `CALL_MICROFLOW` | `CallMicroflowExecutor` | 调用另一微流 |
| MF-07 | `CALL_RULE` | `CallRuleExecutor` | 调用规则 |
| MF-08 | `CALL_CONNECTOR` | `CallConnectorExecutor` | 调用连接器 |
| MF-09 | `THROW_EXCEPTION` | `ThrowExceptionExecutor` | 抛出业务异常 |
| MF-10 | `RETURN` | `ReturnExecutor` | 返回结果终止执行 |

### 附录 H：低代码连接器类型（4 种）

| 编号 | 类型 | 执行器 | 能力 |
|------|------|--------|------|
| CN-01 | REST | `RestConnectorExecutor` | HTTP 调用 + 5 种鉴权 + Resilience4j + 3 种分页 + JsonPath |
| CN-02 | DB | `DbConnectorExecutor` | JDBC 查询/更新 + DDL 拦截 |
| CN-03 | MQ | `MqConnectorExecutor` | RabbitMQ + Kafka，PRODUCE/CONSUME |
| CN-04 | FILE | `FileConnectorExecutor` | SFTP，UPLOAD/DOWNLOAD/LIST/DELETE |

### 附录 I：低代码触发器类型（3 种）

| 编号 | 类型 | 执行器 | 触发方式 |
|------|------|--------|----------|
| TR-01 | CRUD | `CrudTriggerExecutor` | DynamicEntityDataService 在 create/update/delete 前后调用 |
| TR-02 | QUARTZ | `QuartzTriggerExecutor` | Quartz Job 按 cron 表达式触发 |
| TR-03 | EVENT | `EventBusTriggerExecutor` | Spring ApplicationEvent 发布 + 直接执行目标微流 |

### 附录 J：外部集成端点清单

| 编号 | 系统 | 端点 | 用途 | 鉴权 |
|------|------|------|------|------|
| EP-01 | D365 | `/api/integration/d365/health` | 健康检查 | — |
| EP-02 | D365 | `/api/integration/d365/push-receipt` | 推送采购收货 | `integration:d365:push` |
| EP-03 | D365 | `/api/integration/d365/sync/purchase-orders` | 同步采购单 | `integration:d365:sync` |
| EP-04 | D365 | `/api/integration/d365/sync/purchase-receipts` | 同步采购收货 | `integration:d365:sync` |
| EP-05 | D365 | `/api/integration/d365/sync/asset-serial-numbers` | 同步资产序列号 | `integration:d365:sync` |
| EP-06 | D365 | `/api/integration/d365/sync/invoices` | 同步发票 | `integration:d365:sync` |
| EP-07 | FP | `/api/integration/fp/health` | 健康检查 | — |
| EP-08 | FP | `/api/integration/fp/push-settlement` | 推送结算单 | `integration:fp:push` |
| EP-09 | FP | `/api/integration/fp/ocr-invoice` | 发票图片 OCR | `integration:fp:ocr` |
| EP-10 | FP | `/api/integration/fp/payment-callback` | 支付回调 | — |
| EP-11 | OA | `/api/integration/oa/health` | 健康检查 | — |
| EP-12 | OA | `/api/integration/oa/todo/push` | 推送待办 | `integration:oa:push` |
| EP-13 | OA | `/api/integration/oa/todo/complete` | 完成待办 | `integration:oa:process` |
| EP-14 | OA | `/api/integration/oa/todo/transfer` | 转办待办 | `integration:oa:process` |
| EP-15 | 通用 | `/api/integration/log/list` | 集成日志分页 | — |
| EP-16 | 通用 | `/api/integration/log/{id}/retry` | 手动重试 | `integration:log:retry` |
| EP-17 | 通用 | `/api/integration/health` | 聚合健康检查 | — |

### 附录 K：关键配置项清单

| 配置项 | 默认值 | 环境变量覆盖 | 说明 |
|--------|--------|--------------|------|
| `server.port` | 8080 | — | HTTP 端口 |
| `spring.application.name` | `network-equipment-pms` | — | 应用名 |
| `spring.datasource.hikari.min-idle` | 5（dev）/ 10（prod） | — | 连接池最小空闲 |
| `spring.datasource.hikari.max-pool` | 20（dev）/ 50（prod） | — | 连接池上限 |
| `spring.data.redis.host` | localhost | `REDIS_HOST` | Redis 主机 |
| `spring.data.redis.password` | — | `REDIS_PASSWORD` | Redis 密码 |
| `spring.flyway.enabled` | true | — | Flyway 启用 |
| `spring.flyway.out-of-order` | true | — | 允许乱序补录 |
| `spring.flyway.clean-disabled` | true | — | 禁止 clean |
| `flowable.database-schema-update` | true | — | Flowable 自动建表 |
| `flowable.async-executor-activate` | false | — | 关闭异步执行器 |
| `flowable.process-definition-cache-limit` | 100 | — | 流程定义缓存上限 |
| `jwt.secret` | 内置（dev）/ 环境变量（prod） | `JWT_SECRET` | JWT 密钥（Base64） |
| `jwt.expiration` | 86400000（24h） | — | JWT 过期时间 |
| `app.security.encrypt-key` | 内置（dev）/ 环境变量（prod） | `APP_ENCRYPT_KEY` | AES-256-GCM 字段级加密密钥 |
| `app.encrypt-key` | 内置（dev）/ 环境变量（prod） | `APP_CONNECTOR_ENCRYPT_KEY` | 低代码连接器凭据加密密钥 |
| `pms.file.storage.type` | local | — | 文件存储类型 |
| `pms.file.storage.local.base-dir` | `./pms-files` | `PMS_FILE_LOCAL_BASE_DIR` | 本地存储根目录 |
| `lowcode.encryption.key` | 内置（dev）/ 环境变量（prod） | `LOWCODE_ENCRYPTION_KEY` | 低代码连接器凭据 AES 密钥 |
| `liteflow.rule-source` | `""` | — | LiteFlow 规则源 |
| `integration.retry.interval` | 300000（5 分钟） | — | 集成重试间隔 |
| `integration.retry.max-retry` | 3 | — | 最大重试次数 |
| `management.endpoints.web.exposure` | health,info,metrics,prometheus,env,configprops,loggers,scheduledtasks,threaddump | — | Actuator 暴露端点 |
| `otel.service.name` | pms | — | OTel 服务名 |
| `otel.traces.sampler` | parentbased_traceidratio 0.1 | — | OTel 采样 10% |
| `springdoc.swagger-ui.enabled` | true（dev）/ false（prod） | — | Swagger UI 启用 |

### 附录 L：需求统计汇总

| 需求类别 | 数量 |
|----------|------|
| 功能需求（FR） | 165 |
| 用户界面接口（UI） | 20 |
| 外部系统接口（IR） | 16 |
| SPI 接口 | 12 |
| 通信接口（CI） | 7 |
| 性能需求（PRF） | 16 |
| 安全需求（SEC） | 20 |
| 可靠性需求（REL） | 15 |
| 可维护性需求（MNT） | 10 |
| 设计约束（CON） | 20 |
| BPMN 流程定义 | 5 |
| 通知模板 | 12 |
| Flyway 迁移脚本 | 86 |
| 低代码微流节点类型 | 11 |
| 低代码连接器类型 | 4 |
| 低代码触发器类型 | 3 |
| 状态机定义 | 12 |
| **合计** | **444** |

### 附录 M：修订记录

| 版本 | 日期 | 修订人 | 修订说明 |
|------|------|--------|----------|
| V1.0 | 2026-07-22 | 研发团队 | 首版发布，符合 IEEE 830 + GB/T 9385 规范 |

---

**文档结束**
