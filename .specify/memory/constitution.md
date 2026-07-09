<!--
同步影响报告
- 版本变更:(未初始化模板) → 1.0.0
- 理由:首次正式批准。建立由 PMS 现有代码库逆向推导出的全部架构原则,
  叠加三条强制规范原则(SPEC-TYPE-01、DATA-REUSE-01、AMBIGUITY-01)。
  全新奠基性采纳属于 MAJOR(1.0.0)。
- 新增原则(全部为新增):
  - I. 单向依赖的分层架构
  - II. 按特性纵向切分模块
  - III. Service 层事务边界
  - IV. 外部系统经独立数据源隔离
  - V. BPM 驱动业务流程
  - VI. Spec 技术栈无关(SPEC-TYPE-01)[用户强制]
  - VII. 数据库表结构视为契约(DATA-REUSE-01)[用户强制]
  - VIII. 歧义处理纪律(AMBIGUITY-01)[用户强制]
- 新增章节:
  - "技术约束与横切标准"(第 2 节)
  - "开发流程与质量门禁"(第 3 节)
- 删除章节:无
- 待同步模板:
  - .specify/templates/plan-template.md            ✅ 兼容(Constitution Check 门禁本就通用)
  - .specify/templates/spec-template.md            ✅ 兼容(成功标准本就技术无关)
  - .specify/templates/tasks-template.md           ✅ 兼容(阶段模型未变)
  - .specify/templates/checklist-template.md       ✅ 兼容
- 后续待办:无。所有占位符已填实。
-->

# PMS 项目宪法

## 核心原则

### I. 单向依赖的分层架构

系统 MUST 遵循四层架构:表现层(Action)→ 服务层(Service)→ 数据访问层
(DAO)→ 持久化资源(SQL 映射)。依赖 MUST 严格向下流动;上层 MAY 依赖
下层,下层 MUST NOT 依赖上层。

- 表现层 MUST NOT 包含业务逻辑;仅负责组装参数与转发视图。
- 服务层 MUST NOT 触达 Web 容器(禁止使用 `ServletAction`、
  `ServletContext`、`HttpServletRequest`)。任何此类访问均属分层违规,
  MUST 回退至表现层或通过注入端口处理。
- DAO 层 MUST NOT 编排业务规则;仅暴露数据操作。
- 跨层捷径(如 Service 调用 `ServletActionContext`)在新代码中禁止,
  现有代码中标记为重构目标。

**理由**:逆向分析显示代码中反复出现分层泄漏(Service 调用
`ServletActionContext.getServletContext()`),将业务逻辑耦合到 Web 容器,
阻碍可测试性与复用。

### II. 按特性纵向切分模块

功能 MUST 按 `com.dp.plat.{module}` 下的纵向特性模块组织。每个模块拥有
自身的 `action`、`service`、`dao`、`bean`、`vo`、`param`、`util`、
`exception` 子包。跨模块复用 MUST 经由模块对外发布的 Service 接口,不得
直连其内部 DAO 或 bean。

- 命名后缀强制约定:`*Action`、`*Service` / `*ServiceImpl`、
  `*Dao` / `*DaoImpl`、`*VO`(视图对象)、`*Param`(查询参数)、
  `*Bean` / 实体(持久化)。
- 构建模块(`pms-struts`、`pms-activiti`、`pms-ext-d365`、`pms-security`、
  `pms-rules`、`pms-ext-fp`、`core`)对应部署单元;特性模块对应源码包。
  两个维度均有效,不得混为一谈。
- 特性模块 MUST NOT 直接 import 另一特性模块的 `*DaoImpl` 或
  `*ServiceImpl` 内部实现。

**理由**:现有代码库已一致遵循此约定;将其成文化以保持可导航性并防止
纠缠。

### III. Service 层事务边界

事务 MUST 在 Service 层声明。同一模块同一时刻 MAY 仅启用一种事务策略。
在同一调用路径内混用 `@Transactional`、`TransactionProxyFactoryBean`
前缀匹配与手动 `startTransaction/commit/rollback` 是禁止的。

- 新代码 MUST 在 Service 方法上使用声明式 `@Transactional`。
- DAO 内手动事务控制在新增代码中禁止;现存手动事务 DAO 标记为重构目标。
- 只读操作 SHOULD 声明 `@Transactional(readOnly = true)`。

**理由**:代码库当前并行三套事务机制,边界模糊,存在事务泄漏风险。

### IV. 外部系统经独立数据源隔离

每个外部系统集成(SAP、D365、CRM、OA、EHR、ITR、SMS、SSE、License 等)
MUST 隔离在独立数据源与独立 SQL-map 配置之后。集成 MUST 经由 Service
接口对外暴露,使下游代码无感知源系统。

- 所有外部数据源 MUST 使用池化连接提供者。新增集成禁止使用
  `DriverManagerDataSource`(无池化)。
- 跨系统数据连接 MUST 在 Service 层完成,禁止跨库 SQL。
- 与外部系统的同步 MUST 经由定时任务(`job`)包执行,不得内联在
  请求处理 Action 中。

**理由**:现有外部数据源使用无池化的 `DriverManagerDataSource`,在高负载
下存在性能与资源耗尽风险。

### V. BPM 驱动业务流程

长流程、多步骤、多角色的业务流(售前、回访、分包、PM 闭环等)MUST 以
BPMN 流程经 Activiti 引擎建模与执行,不得以临时状态字段和内联状态机
替代。

- 需要人工审批、指派或异步通知的状态流转 MUST 为 BPMN 任务,不得是
  数字状态字符串。
- 异步工作禁止内联 `new Thread()`;异步工作 MUST 经由引擎、调度器或
  受管执行器处理。

**理由**:代码库将 BPMN 驱动流程与手写状态字符串、裸 `new Thread()`
混用,导致流程行为不一致且不可观测。

### VI. Spec 技术栈无关(SPEC-TYPE-01)

特性规格说明书(spec.md)MUST 技术栈无关。规格 MUST NOT 将需求绑定到具体
框架注解、类名或库标识符(如"使用 `@Transactional`"、"继承
`BaseAction`"、"调用 `SqlMapClientTemplate`")。规格描述系统必须做什么
及其必须具备的质量属性,而非特定框架如何实现。

- 技术选型记录在 plan.md(技术上下文)中,而非 spec.md。
- 验收标准与成功标准 MUST 可度量且框架无关。
- 若规格必须引用既有能力,MUST 按行为命名(如"持久化层"),不得按
  类名命名。

**理由**:现有系统紧耦合于 Struts2 / iBatis / Hibernate。将规格绑定到
这些名称将冻结技术债并阻断任何未来迁移。规格必须保持可移植性。

### VII. 数据库表结构视为契约(DATA-REUSE-01)

现有数据库表结构视为契约。新系统或新特性 MUST 默认复用既有表。任何对表
结构的变更(重命名、删除、类型变更、约束变更)或任何不复用既有表的决策
MUST 作为显式规格条目记录并附理由。

- 新特性 MUST 优先映射既有表,而非新建表。
- 影响表结构的决策 MUST 记录在规格的"关键实体"章节,并在 `/clarify`
  阶段复核。
- 破坏性表结构变更要求在 tasks.md 中单列迁移任务。

**理由**:多个生产集成与报表依赖当前表结构。将其视为契约可防止隐性
破坏并强制进行有意识的迁移规划。

### VIII. 歧义处理纪律(AMBIGUITY-01)

在规格、计划或实现阶段发现的每个歧义 MUST 在 `/clarify` 阶段记录,内容
包含:(a) 问题、(b) 所做决策、(c) 支撑决策的理由/证据。未决歧义 MUST NOT
在代码中被静默处理。

- `/clarify` 之后发现的歧义 MUST 在实现继续前触发补充澄清记录。
- 规格中的"NEEDS CLARIFICATION"标记 MUST 被解决或显式延期,并注明负责人
  与触发时机。
- 禁止臆造特性:需求不明确时询问,不得假设。

**理由**:逆向分析过程中浮现了多处"待澄清"项(日志规范、部署拓扑、分库
分表)。此类缺口若不记录,将导致各模块实现分叉。

## 技术约束与横切标准

### 日志

- 日志门面 MUST 为 SLF4J,实现 MUST 为 Log4j2。业务代码中禁止直接使用
  `System.out`、`e.printStackTrace()` 或具体日志实现。
- Service 与 DAO 层 MUST 在有意义的边界(关键操作的入口/出口、捕获的
  异常)记录日志。当前稀疏日志模式为已知技术债,应予以消除。
- 开发环境的 SQL 日志使用 p6spy;生产环境 MUST NOT 以 INFO 级别输出
  完整 SQL。

### 安全

- 并存的三套安全框架(Spring Security、CAS、Shiro)为遗留。新增认证/
  授权工作 MUST 面向每个模块选定的单一框架,且 MUST NOT 引入硬编码凭据
  (例如 `applicationContext-security.xml` 中的 `admin/admin` 测试账号
  在任何非测试制品中禁止存在)。
- 用户身份 MUST 经由 `UserContext` 抽象访问,不得在 Service/DAO 代码中
  直接读取 HTTP 会话。

### 缓存

- 新增 mapper 模块启用 MyBatis 二级缓存。Hibernate 二级缓存(ehcache)
  仅限于 `pms-activiti` 模块。
- 若重新引入 Redis 缓存,MUST 在 Service 层声明(不得内联于 DAO),并
  记录在 plan.md。

### 异常处理

- 新代码 MUST NOT 抛出裸 `RuntimeException` 或 `Exception`。业务错误
  MUST 使用以单一业务异常基类为根的类型化异常(替代当前临时的
  `CustomRuntimeException` 及拼写错误的模块异常,如
  `NoMatchedSoftVersionStrategyExecption`)。
- 表现层负责将异常翻译为用户消息;Service 层 MUST NOT 自行拼装 HTML
  错误片段。

### 数据访问

- iBatis 2.x(`SqlMapClientTemplate`)为遗留;新特性 MUST 使用 MyBatis
  3.x mapper 接口。既有 iBatis DAO 为重构目标,不得作为可扩展范式。
- 临时表统计模式(每次查询创建/删除临时表)仅当无集合化替代方案时允许
  使用,且 MUST 在 plan.md 中记录。

### 构建与可复现性

- 新增模块禁止依赖 `system` 作用域的本地 JAR(如 `Utils-v0.1.jar`)。
  此类依赖 MUST 发布至仓库或作为正式模块 vendoring。
- Checkstyle 已配置但非阻断(`failOnViolation=false`)。新增模块 MAY
  在既有违规清理后将其提升为阻断。

### 已知技术债(重构清单)

以下为已识别的技术债与显式重构目标;MUST NOT 扩展:

- 双 ORM(iBatis 2.x + MyBatis 3.x)并存。
- 三套事务机制并存。
- 三套安全框架并存且含硬编码凭据。
- Struts2 版本漂移(根 2.5.30 vs 模块 2.3.35)。
- 外部数据源无池化。
- fastjson 1.2.x(autotype RCE 历史);fastjson2 迁移待办。
- Service 层拼装 HTML/SQL 字符串。
- 裸 `new Thread()` 无线程池、无异常兜底。
- 经本地 `system` 作用域 JAR 实现的构建可复现性。
- 累积的 `_bak` / `.bak` / 拼写错误的遗留文件。

## 开发流程与质量门禁

### 规格驱动流程

所有非平凡工作 MUST 遵循 Spec Kit 流程:`/speckit-specify` →
`/speckit-clarify` → `/speckit-plan` → `/speckit-tasks` →
`/speckit-implement`。当存在任何歧义时(见原则 VIII)禁止跳过
`/clarify`。

### 宪法核查门禁

每个 plan.md MUST 包含"Constitution Check"门禁,在 Phase 0 研究前与
Phase 1 设计后各核查一次与各适用原则的对齐情况。违规项要么修正,要么在
计划的"Complexity Tracking"表中给出有理由的登记。

### 分层与边界审查

代码审查 MUST 核查:(a) 单向依赖方向(原则 I)、(b) Service/DAO 不触达
Web 容器、(c) 每模块单一事务策略、(d) 外部数据源池化、(e) 无裸
`new Thread()`。

### 表结构变更审查

任何触及表的任务 MUST 引用授权该表结构变更的规格条目(见原则 VII)。无
引用的表结构变更在审查阶段阻断。

### 技术无关规格审查

规格审查 MUST 拒绝任何将具体框架注解、类名或库作为绑定需求的规格
(见原则 VI)。

## 治理

- 本宪法凌驾于 PMS 项目的所有其他实践之上。当实践与原则冲突时,以原则
  为准。
- 修订需:(a) 书面提案、(b) 记录理由、(c) 批准、(d) 针对已不合规代码的
  迁移计划。
- 版本号遵循语义化版本:MAJOR 用于原则删除或不兼容重定义,MINOR 用于
  新增/扩展原则或章节,PATCH 用于澄清与勘误。
- 所有计划、规格与审查 MUST 核查与本宪法的合规性。超出原则范围的复杂性
  MUST 在计划的"Complexity Tracking"表中给出理由。
- 运行时开发指南位于 `.specify/templates/` 下的 Spec Kit 模板中;本宪法
  为规范性来源。

**版本**: 1.0.0 | **批准日期**: 2026-07-09 | **最后修订**: 2026-07-09
