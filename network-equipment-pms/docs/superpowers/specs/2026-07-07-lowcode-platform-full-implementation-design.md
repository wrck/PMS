# 低代码平台全量实施设计（阶段一补全 + 二/三/四 + 组件库）

> **状态**：待评审 v1.0
> **范围**：阶段一 P0 缺口补全 + 阶段二（业务逻辑/流程编排）+ 阶段三（扩展性/连接器）+ 阶段四（协作/预览/部署）+ 基础组件库
> **分支**：`lowcode`
> **日期**：2026-07-07

---

## 1. 背景与目标

### 1.1 现状

低代码平台阶段一 P0（数据建模 + 版本控制）已完成约 80%，存在 3 个关键缺口：
- **F1.2 DDL 执行引擎缺失**：仅生成 DDL，未执行（`DdlExecutionService.java` 未创建，ALTER/DROP/审批/备份未实现）
- **F1.1 ER 图连线建关联缺失**：前端仅渲染单实体节点，未实现多实体画布 + 拖拽连线建关联
- **F1.5 树形 Diff 缺失**：前端用 `el-table` 平铺展示，未用 `jsondiffpatch` 树形可视化
- **F1.7 环境晋升增强缺失**：导出为 JSON 字符串而非 zip 包，无依赖完整性校验，无覆盖确认

阶段二/三/四完全未启动。组件库仅有 4 类内置渲染器 + FormRenderer 局部 `componentRegistry` props（占位机制）。

### 1.2 目标

在本轮交付中完成：
1. **补全阶段一 P0 缺口**（DDL 执行 + ER 图连线 + 树形 Diff + 环境晋升增强）
2. **阶段二**：微流引擎（Groovy + X6）+ 规则引擎集成 + 表单事件绑定 + 流程设计器（Flowable + bpmn-js）+ 流程触发器
3. **阶段三**：基础组件库（全局注册中心 + 10-15 个预置业务组件）+ REST 连接器 + 数据库连接器
4. **阶段四**：配置编辑锁（悲观锁）+ 评论 + 预览模式 + 多设备模拟 + 一键发布流水线 + 回滚

不在本轮范围：F3.3 组件市场、F4.3 CRDT 协作、F4.8 灰度发布、阶段五 AI（评审决策延后）。

### 1.3 评审决策（继承自前序评审）

| 决策项 | 选择 |
|--------|------|
| 微流 vs 规则引擎 | 两者都要（微流图灵完备 + 规则引擎决策表） |
| 流程编排 | 复用 Flowable + 新增 bpmn-js 前端设计器 |
| 协作模式 | 悲观锁优先，CRDT 延后 |
| 灰度发布 | 延后到平台成熟期 |
| ER 图/微流编辑器 | AntV X6（开源） |
| 数据库范围 | MySQL-only DDL（预留 PostgreSQL 扩展点） |

### 1.4 本轮新增决策

| 决策项 | 选择 |
|--------|------|
| 微流后端执行 | Groovy 脚本引擎（`GroovyShell`）+ 节点执行器模式 |
| 流程引擎 | Flowable（复用 pms-workflow）+ bpmn-js 前端设计器 |
| 连接器范围 | REST + DB（预置 D365/FP/OA 模板） |
| 组件库深度 | 全局注册中心 + 10-15 个预置业务组件（不做 SDK/沙箱，属阶段三 F3.1-F3.3 后续） |
| 测试策略 | 单元测试（Mockito）+ 关键集成测试（Testcontainers） |

---

## 2. 架构总览

```
┌─────────────────────────────────────────────────────────────────────┐
│                         前端（Vue 3 + Element Plus）                 │
├─────────────────────────────────────────────────────────────────────┤
│  设计器层    │ ER 图设计器(X6) │ 微流设计器(X6) │ 流程设计器(bpmn-js)│
│  组件库      │ 全局注册中心 + 15 个预置业务组件 + 属性面板抽象         │
│  渲染引擎    │ FormRenderer │ ListRenderer │ TabRenderer │ 关联页     │
│  协作层      │ 编辑锁(悲观) │ 评论 │ 预览模式 │ 多设备模拟            │
└─────────────────────────────────────────────────────────────────────┘
                                     │ REST API
┌─────────────────────────────────────────────────────────────────────┐
│                    后端（Spring Boot 3.2.5 + MyBatis-Plus）          │
├─────────────────────────────────────────────────────────────────────┤
│  引擎层      │ DdlExecutionService │ MicroflowEngine(Groovy)          │
│              │ RuleEngineService(Aviator+LiteFlow) │ FlowableEngine   │
│  连接器层    │ RestConnectorExecutor │ DbConnectorExecutor            │
│  协作层      │ EditLockService(Redis) │ CommentService │ PublishService│
│  数据层      │ DynamicEntityDataService │ ConfigVersionService        │
│  触发器层    │ CrudTrigger │ QuartzTrigger │ EventBusTrigger          │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.1 模块归属

所有新增代码归入 `pms-lowcode` 模块，包名 `com.dp.plat.lowcode.*`。新增子包：

| 子包 | 职责 | 阶段 |
|------|------|------|
| `engine.ddl` | DDL 执行引擎（补全 F1.2） | 一补 |
| `engine.microflow` | 微流引擎（节点执行器 + Groovy） | 二 |
| `engine.rule` | 规则引擎集成（Aviator + LiteFlow 适配） | 二 |
| `engine.flowable` | Flowable 流程集成 | 二 |
| `connector` | REST + DB 连接器 | 三 |
| `component` | 组件注册中心后端元数据 | 三 |
| `collaboration` | 编辑锁 + 评论 | 四 |
| `preview` | 预览模式 + 多设备 | 四 |
| `publish` | 发布流水线 | 四 |
| `trigger` | 流程触发器 | 二 |

---

## 3. 阶段一补全设计

### 3.1 F1.2 DDL 执行引擎（补全）

**目标**：实体发布时不仅生成 DDL，还执行到数据库，支持 ALTER/DROP 安全策略。

**组件**：
- `DdlExecutionService`（接口）+ `DdlExecutionServiceImpl`
  - `executeCreate(entityId)`：执行 CREATE TABLE + 中间表
  - `executeAlter(entityId, fromVersion, toVersion)`：对比字段差异，生成 ALTER TABLE ADD/DROP COLUMN + CREATE/DROP INDEX
  - `executeWithBackup(entityId, ddlSql)`：ALTER 前自动备份表结构到 `pms_lc_ddl_backup` 表
  - `validateBeforeExecution(ddlSql)`：SQL 白名单校验（禁止 DROP TABLE / TRUNCATE / DROP DATABASE）
- `LowCodeEntityService.publish()` 改造：调用 `DdlExecutionService.executeWithApproval()`
- 新增审批流程：DROP COLUMN 需二次确认（前端弹窗 + 后端 `confirmDrop` 参数）
- 新增表 `pms_lc_ddl_backup`（记录 DDL 执行历史 + 备份 SQL）

**安全策略**：
- DROP TABLE / TRUNCATE / DROP DATABASE：禁止，抛 `DdlSecurityException`
- DROP COLUMN：需 `confirmDrop=true`，执行前备份列数据到 `pms_lc_ddl_backup` 表（JSON 格式存储）
- ALTER TABLE：执行前备份表结构（`SHOW CREATE TABLE` 结果）
- 所有 DDL 执行记录入 `pms_lc_ddl_execution_log`（含 SQL + 执行人 + 时间 + 结果）

### 3.2 F1.1 ER 图连线建关联（补全）

**目标**：前端 ER 图支持多实体画布 + 拖拽连线建立关联。

**组件**：
- `EntityDesigner/index.vue` 改造：
  - 画布从单节点改为多节点（左侧实体列表拖入画布）
  - X6 `edge` 连线：从实体字段端口拖出到另一实体，弹出关联配置弹窗
  - 关联配置弹窗：关联类型（一对多/多对一/多对多/自关联）+ 级联策略（CASCADE/SET_NULL/RESTRICT）+ 外键字段
- `EntityNode.vue` 改造：增加字段端口（port），支持连线起点/终点
- 新增 `RelationConfigDialog.vue`：关联配置弹窗
- 后端 `LowCodeEntityController` 新增 `POST /{entityId}/relations` 保存关联

### 3.3 F1.5 树形 Diff 可视化（补全）

**目标**：前端用 `jsondiffpatch` 树形展示 JSON 配置差异。

**组件**：
- `version-history/index.vue` 改造：
  - 引入 `jsondiffpatch`（已在 package.json）
  - Diff 展示从 `el-table` 改为左右双栏树形视图（左侧 old + 右侧 new + 增删改高亮）
  - 保留表格视图作为"扁平模式"切换选项
- 新增 `JsonTreeDiff.vue` 组件：封装 jsondiffpatch 渲染

### 3.4 F1.7 环境晋升增强（补全）

**目标**：导出 zip 包 + 依赖完整性校验 + 覆盖确认。

**组件**：
- `EnvironmentPromotionService.exportPackage()` 改造：返回 `byte[]` zip 包（含 `config.json` + `metadata.json`）
- 新增 `validatePackage(ConfigPackageDTO)`：校验实体/连接器/微流依赖是否存在
- 新增 `importPackageWithConfirm(ConfigPackageDTO, overwrite)`：覆盖确认参数
- 前端 `version-history/index.vue` 新增导出按钮（下载 zip）+ 导入弹窗（上传 zip + 覆盖确认）

---

## 4. 阶段二设计：业务逻辑与流程编排

### 4.1 F2.1 微流引擎（Groovy + X6）

**目标**：可视化微流设计器 + 后端 Groovy 解释执行。

**后端**：
- 实体 `LowCodeMicroflow`（id, code, name, definition JSON, status, version）
- 表 `pms_lc_microflow` + `pms_lc_microflow_version`
- 节点类型枚举 `MicroflowNodeType`：START, END, ASSIGN, CONDITION, LOOP, CALL_SERVICE, CALL_MICROFLOW, CALL_RULE, CALL_CONNECTOR, THROW_EXCEPTION, RETURN
- 节点执行器接口 `MicroflowNodeExecutor` + 各节点类型实现
- `MicroflowEngine.execute(code, context)`：遍历 DAG 节点，按类型调用执行器
- Groovy 集成：`AssignExecutor` 和 `ConditionExecutor` 用 `GroovyShell` 执行表达式
- 上下文 `MicroflowContext`：变量作用域 + 输入参数 + 输出结果

**前端**：
- `views/lowcode/microflow-designer/index.vue`：X6 画布 + 节点面板 + 属性面板
- `components/MicroflowDesigner/`：自定义节点 + 节点配置弹窗
- 节点面板：拖拽节点到画布，连线建立执行顺序
- 属性面板：按节点类型显示不同配置（赋值表达式/条件表达式/调用目标等）

### 4.2 F2.2 规则引擎集成

**目标**：复用 pms-rules（Aviator + LiteFlow），提供决策表 + 表达式规则配置。

**后端**：
- 实体 `LowCodeRule`（id, code, name, type DECISION_TABLE/EXPRESSION, definition JSON, status）
- 表 `pms_lc_rule`
- `RuleEngineService` 接口 + 实现：
  - `executeDecisionTable(code, facts)`：解析决策表 JSON，逐行匹配条件，返回命中行动作
  - `executeExpression(code, context)`：用 Aviator 执行表达式
  - `executeLiteFlow(code, context)`：委托 pms-rules 的 LiteFlow 引擎
- 决策表 JSON 结构：`{conditions: [{field, operator, value}], actions: [{field, value}]}`

**前端**：
- `views/lowcode/rule-designer/index.vue`：决策表编辑器（表格 UI）+ 表达式编辑器（代码编辑器）

### 4.3 F2.3 表单事件绑定

**目标**：表单 onLoad/onChange/onSubmit 调用微流/规则。

**后端**：
- `LowCodeForm` 实体增加 `events` JSON 字段：`{onLoad: {type: MICROFLOW/RULE, code: 'xxx'}, onChange: {...}, onSubmit: {...}}`
- `LowCodeFormController` 新增 `POST /{formId}/event/{eventType}`：前端事件触发时调用，后端执行微流/规则
- 前端 `LowCodeFormRenderer` 生命周期钩子调用后端事件 API

**前端**：
- `LowCodeFormRenderer/index.vue` 改造：onMounted/onUpdated/onSubmit 时调用事件 API
- 表单设计器增加"事件绑定"面板

### 4.4 F2.4 流程设计器（Flowable + bpmn-js）

**目标**：复用 Flowable 引擎，新增 bpmn-js 前端设计器，低代码表单绑定到流程节点。

**后端**：
- `LowCodeProcessBinding` 实体（processDefinitionKey, nodeFormBindings JSON）
- 表 `pms_lc_process_binding`
- 复用 pms-workflow 的 Flowable 引擎部署 BPMN XML
- `LowCodeProcessController`：CRUD + 部署 + 启动 + 查询任务

**前端**：
- `views/lowcode/process-designer/index.vue`：bpmn-js 画布 + 属性面板
- 节点属性面板：formKey 绑定低代码表单 + 审批人/候选组 + 微流绑定（节点事件）

### 4.5 F2.5 表单 × 流程绑定

**目标**：流程节点绑定低代码表单，任务打开时渲染对应表单。

**后端**：
- `LowCodeProcessController.getTaskForm(taskId)`：返回任务节点绑定的表单配置 + 业务数据
- 提交表单时调用 `completeTask(taskId, formData)`：保存业务数据 + 完成任务

**前端**：
- `views/lowcode/task-center/index.vue`：待办列表 + 表单渲染 + 提交

### 4.6 F2.6 流程触发器

**目标**：数据 CRUD / Quartz 定时 / 事件总线触发流程。

**后端**：
- `LowCodeTrigger` 实体（type CRUD/QUARTZ/EVENT, config JSON, targetMicroflow/processCode）
- 表 `pms_lc_trigger`
- `CrudTriggerExecutor`：监听动态实体 CRUD（通过 `DynamicEntityDataService` 钩子）
- `QuartzTriggerExecutor`：注册 Quartz Job
- `EventBusTriggerExecutor`：监听 Spring `ApplicationEvent`

---

## 5. 阶段三设计：扩展性与连接器

### 5.1 基础组件库（全局注册中心 + 预置组件）

**目标**：全局组件注册中心 + 10-15 个预置业务组件 + 统一属性面板抽象。

**前端**：
- `components/LowCodeComponentRegistry/index.ts`：全局注册中心单例
  - `register(name, component, metaSchema)`：注册组件
  - `get(name)`：获取组件
  - `list()`：列出所有组件元数据
  - `getPropertyPanel(name)`：获取属性面板配置
- 预置组件（15 个）：
  1. `UserSelector` 用户选择器（对接系统用户 API）
  2. `DeptSelector` 部门选择器
  3. `DictSelect` 数据字典下拉
  4. `FileUploader` 文件上传（对接 pms-file）
  5. `RichTextEditor` 富文本编辑器
  6. `CodeEditor` 代码编辑器
  7. `ColorPicker` 颜色选择器
  8. `TreeSelect` 树形选择
  9. `DateRangePicker` 日期范围
  10. `NumberRangeInput` 数字范围
  11. `AddressPicker` 地址选择（省市区联动）
  12. `BarcodeInput` 条码扫描输入
  13. `SignaturePad` 电子签名
  14. `ChartPreview` 图表预览（echarts）
  15. `QrcodeDisplay` 二维码展示
- `components/LowCodePropertyPanel/index.vue`：统一属性面板，根据 `metaSchema` 动态渲染配置项
- `LowCodeFormRenderer` 改造：从全局注册中心解析 `type='custom'` 组件

**后端**：
- 实体 `LowCodeComponentMeta`（name, displayName, category, icon, propsSchema JSON）
- 表 `pms_lc_component_meta`：存储组件元数据（供设计器属性面板渲染）

### 5.2 F3.4 REST 连接器

**目标**：可视化配置 REST API 调用，支持认证/重试/分页。

**后端**：
- 实体 `LowCodeConnector`（type REST/DB, code, name, config JSON, status）
- 表 `pms_lc_connector`
- `RestConnectorExecutor`：
  - 配置：URL/Method/Header/Body/Auth(NONE/BASIC/BEARER/API_KEY)/Retry/Timeout/Pagination
  - 执行：RestTemplate 调用 + Resilience4j 重试 + 分页自动聚合
  - 返回：`ConnectorResult`（status/data/headers）
- `LowCodeConnectorController`：CRUD + `POST /{code}/test` 测试连接

**前端**：
- `views/lowcode/connector-designer/index.vue`：REST 连接器配置表单 + 测试按钮

### 5.3 F3.5 数据库连接器

**目标**：JDBC 直连外部数据源，扩展 RoutingDataSource。

**后端**：
- `DbConnectorExecutor`：
  - 配置：url/username/password/driverClassName/maxPoolSize
  - 执行：`JdbcTemplate` 查询/更新
  - 安全：SQL 白名单（禁止 DDL）+ 参数化查询防注入
- `DynamicDataSourceManager`：动态注册/注销数据源到 RoutingDataSource
- 复用 `LowCodeConnector` 实体（type=DB）

**前端**：
- `connector-designer/index.vue` 增加 DB 类型配置表单

### 5.4 预置连接器模板

**目标**：预置 D365/FP/OA 连接器配置模板。

- `LowCodeTemplateInitializer` 增加 3 个连接器模板：
  - `d365_connector`：D365 REST API（Bearer 认证）
  - `fp_connector`：FP REST API（Basic 认证）
  - `oa_connector`：OA REST API（API Key 认证）
- 模板仅预置配置，凭据由用户填写

---

## 6. 阶段四设计：协作、预览、部署

### 6.1 F4.1 配置编辑锁（悲观锁）

**目标**：防止多人同时编辑同一配置。

**后端**：
- `EditLockService`：
  - `acquire(configType, configId, userId)`：Redis SETNX + TTL 30min
  - `renew(configType, configId, userId)`：心跳续期
  - `release(configType, configId, userId)`：释放锁
  - `getLock(configType, configId)`：查询当前持锁人
- 拦截器 `EditLockInterceptor`：写操作前检查锁
- 表 `pms_lc_edit_lock`（持久化记录，Redis 为缓存）

**前端**：
- 编辑器进入时 `acquire` 锁，失败提示"XXX 正在编辑"
- 每 5 分钟 `renew` 心跳
- 离开页面 `release` 锁
- 顶部显示"编辑中"状态徽标

### 6.2 F4.2 配置评论 + @提及通知

**目标**：配置项评论 + @提及触发通知。

**后端**：
- 实体 `LowCodeComment`（configType, configId, userId, content, mentions JSON, createdAt）
- 表 `pms_lc_comment`
- `CommentService`：CRUD + 解析 @提及 + 调用 pms-notification 发送通知
- `LowCodeCommentController`：CRUD API

**前端**：
- `components/CommentPanel/index.vue`：评论列表 + 输入框 + @提及选择器

### 6.3 F4.4 + F4.5 预览模式 + 多设备模拟

**目标**：独立预览模式 + 多设备尺寸模拟。

**前端**：
- `views/lowcode/preview/index.vue`：iframe 全屏预览
- 设备切换：PC（1920×1080）/ Tablet（768×1024）/ Mobile（375×812）+ 横竖屏
- 预览数据：从后端拉取测试数据填充表单/列表

### 6.4 F4.6 编辑-预览实时同步

**目标**：编辑器改动实时同步到预览。

**前端**：
- 编辑器 `watch` 配置变化，通过 `postMessage` 发送到预览 iframe
- 预览 iframe 接收消息后重新渲染

### 6.5 F4.7 一键发布流水线

**目标**：校验 → 审批 → 发布 → 通知。

**后端**：
- `PublishService`：
  - `submitForPublish(configType, configId)`：提交发布申请
  - `validate(configType, configId)`：校验配置完整性（Schema + 依赖）
  - `approve(publishId)`：审批通过，执行发布
  - `reject(publishId, reason)`：审批拒绝
- 实体 `LowCodePublishRecord`（configType, configId, version, status, applicant, approver, createdAt）
- 表 `pms_lc_publish_record`
- 状态机：DRAFT → SUBMITTED → APPROVED/REJECTED → PUBLISHED

**前端**：
- `views/lowcode/publish-center/index.vue`：发布申请列表 + 审批操作

### 6.6 F4.9 回滚

**目标**：复用 F1.6 版本回滚 + 发布记录。

- `PublishService.rollback(publishId)`：回滚到指定发布版本（调用 `LowCodeConfigVersionService.rollback`）

---

## 7. 数据模型变更汇总

新增 12 张表（11 个 Flyway 迁移文件 V32-V42，其中 V34 含 2 张表）：

| 迁移 | 表名 | 阶段 |
|------|------|------|
| V32 | `pms_lc_ddl_backup` | 一补 |
| V33 | `pms_lc_ddl_execution_log` | 一补 |
| V34 | `pms_lc_microflow` + `pms_lc_microflow_version` | 二 |
| V35 | `pms_lc_rule` | 二 |
| V36 | `pms_lc_process_binding` | 二 |
| V37 | `pms_lc_trigger` | 二 |
| V38 | `pms_lc_connector` | 三 |
| V39 | `pms_lc_component_meta` | 三 |
| V40 | `pms_lc_edit_lock` | 四 |
| V41 | `pms_lc_comment` | 四 |
| V42 | `pms_lc_publish_record` | 四 |

已有表扩展（增量迁移）：
- `pms_lc_form` 增加 `events` JSON 字段（V34 附带）

---

## 8. 测试策略

### 8.1 单元测试（Mockito）

每个核心引擎/执行器/服务配套单元测试：
- `DdlExecutionServiceImplTest`：CREATE/ALTER/DROP + 安全策略 + 备份
- `MicroflowEngineTest`：各节点类型执行 + Groovy 表达式 + 上下文作用域
- `RuleEngineServiceTest`：决策表匹配 + Aviator 表达式 + LiteFlow 委托
- `RestConnectorExecutorTest`：认证/重试/分页/超时
- `DbConnectorExecutorTest`：查询/更新/SQL 白名单
- `EditLockServiceTest`：获取/续期/释放/竞争
- `PublishServiceTest`：状态机流转 + 校验 + 回滚

### 8.2 集成测试（Testcontainers）

复用现有 `AbstractIntegrationTest` 基类（MySQL + Redis 容器）：
- `MicroflowEngineIntegrationTest`：端到端微流执行
- `FlowableIntegrationTest`：流程部署 + 启动 + 任务完成
- `ConnectorIntegrationTest`：REST 连接器调用 MockServer + DB 连接器查询
- `EditLockIntegrationTest`：Redis 锁竞争
- `PublishFlowIntegrationTest`：发布流水线端到端

### 8.3 测试不覆盖

- 前端 E2E（Playwright）：本轮不新增，已有 12 个 E2E 用例足够
- 性能测试：延后
- 安全渗透：延后

---

## 9. 风险与缓解

| 风险 | 缓解 |
|------|------|
| 工作量巨大（11 表 + 30+ 类 + 15 组件） | 分阶段交付，每阶段独立可验证，优先级：一补 > 二 > 三 > 四 |
| Groovy 脚本安全（注入攻击） | Groovy `SecureASTCustomizer` + 白名单 import + 执行超时 |
| Flowable 版本兼容（pms-workflow 现有版本） | 复用 pms-workflow 已有依赖，不引入新版本 |
| DDL 执行破坏生产数据 | 备份机制 + 禁止 DROP TABLE + DROP COLUMN 二次确认 + 执行日志 |
| 连接器凭据泄露 | 凭据加密存储（复用 APP_ENCRYPT_KEY）+ 接口返回脱敏 |
| 编辑锁 Redis 宕机 | 锁信息持久化到 `pms_lc_edit_lock` 表，Redis 恢复后重建 |

---

## 10. 交付里程碑

| 里程碑 | 内容 | 可验证标准 |
|--------|------|-----------|
| M1 | 阶段一补全 | DDL 执行落库 + ER 图连线建关联 + 树形 Diff + zip 包导出 |
| M2 | 阶段二 | 微流可执行 + 规则引擎可调用 + 流程可部署启动 + 表单事件触发 |
| M3 | 阶段三 | 15 个组件可用 + REST/DB 连接器可测试 + D365/FP/OA 模板预置 |
| M4 | 阶段四 | 编辑锁生效 + 评论通知 + 预览多设备 + 发布流水线 + 回滚 |

---

## 11. 不在本轮范围

- F3.1 自定义组件 SDK（vite-plugin-lowcode 打包）
- F3.2 组件沙箱（iframe 隔离 + postMessage + CSP）
- F3.3 组件市场（上传/下载/审核）
- F3.6 连接器市场
- F4.3 CRDT 实时协同编辑
- F4.8 灰度发布
- 阶段五 AI 能力

这些将在平台成熟后后续规划。

---

## 12. 后续步骤

1. 用户审阅本设计文档
2. 审阅通过后调用 writing-plans 技能创建详细实施计划
3. 实施计划按里程碑 M1→M2→M3→M4 分批，每批次独立提交验证
