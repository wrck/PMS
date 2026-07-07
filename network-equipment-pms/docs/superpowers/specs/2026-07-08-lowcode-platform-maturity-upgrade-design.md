# 低代码平台生产级成熟度升级设计

> **Status**: v1.0 — 待用户审查
> **Date**: 2026-07-08
> **Goal**: 将 pms-lowcode 从当前 ~33% 成熟度升级至 80%+ 真正生产可用，对标国内外 20 个主流低代码平台
> **Strategy**: 5 批次渐进推进，每批次独立可交付

---

## 一、背景与对标范围

### 1.1 当前状态

pms-lowcode 已完成 M1-M4 四个里程碑（40+ commit，4 tag），覆盖：
- 数据建模（实体/字段/关联/DDL）、版本控制（快照/Diff/回滚/环境晋升）
- 微流引擎（Groovy）、规则引擎（Aviator）、表单事件、流程绑定、触发器
- REST/DB 连接器、15 个预置 Widget、组件注册中心
- 编辑锁、评论、发布流水线、预览

**但整体成熟度仅 ~33%**，存在大量占位实现、可视化设计器缺失、生产级硬伤、安全风险。

### 1.2 对标平台清单（20 个）

**国外四大（已调研）**：Mendix、OutSystems、Microsoft Power Apps、Appian

**国内十大（本轮调研）**：钉钉宜搭、腾讯微搭、百度爱速搭、明道云、活字格、网易数帆轻舟、华为 AppCube、NocoBase、用友 YonBuilder、金蝶苍穹

**国外开源/商业十大（本轮调研）**：Retool、Appsmith、ToolJet、Budibase、NocoBase、Joget、Creatio、Zoho Creator、Salesforce Lightning、ServiceNow

### 1.3 升级目标

| 维度 | 当前 | 目标 |
|---|---|---|
| 整体成熟度 | ~33% | 80%+ |
| 可视化设计器 | JSON 文本框 | 完整画布 + 拖拽 + 属性面板 |
| 引擎层 | 大量占位 | 全部实现 + 执行日志 + 调试 |
| 安全 | Groovy 无沙箱、凭据明文 | 沙箱 + 加密 + XSS 防护 |
| 治理 | 单步审批 | 多级审批 + 灰度 + 影响分析 |
| 多端 | 仅 Web | PC/移动/响应式 |
| 扩展性 | 仅元数据列表 | SDK + 沙箱 + 市场 |

---

## 二、综合差距矩阵与借鉴来源

### 2.1 十二项核心能力对标

| # | 能力 | 当前 | 目标 | 关键借鉴平台 | 核心补齐项 |
|---|---|---|---|---|---|
| 1 | 数据建模 | 55% | 85% | Mendix Domain Model、NocoBase 模型驱动、活字格多数据源 | 真实 ALTER Diff、外键表名修复、复合索引、主键策略、动态查询增强、多实体画布 |
| 2 | UI 设计器 | 20% | 85% | Power Apps Studio（Undo 50 步）、Budibase Bindings Drawer、NocoBase Block/Action | 撤销重做、响应式断点、组件嵌套、Registry 打通、属性面板 schema 化 |
| 3 | 业务逻辑 | 35% | 85% | Mendix Microflows（30+ 节点）、Zoho Deluge AST、Joget APM | 6 执行器补齐、Groovy 沙箱、执行轨迹、断点调试、规则版本 |
| 4 | 流程编排 | 25% | 80% | Appian Process Modeler、ServiceNow Flow Designer（Data Pill）、钉钉宜搭 SLA | bpmn-js 集成、网关/会签、流程实例管理、任务回调、SLA 超时升级 |
| 5 | 数据连接器 | 40% | 85% | 明道云（节点最细）、金蝶集成云、Retool Query Editor | OAuth2、熔断限流、分页聚合、Request/Response 映射、OpenAPI 导入、凭据加密 |
| 6 | 版本控制 | 55% | 85% | Appsmith Git 双向 Diff、OutSystems LifeTime、百度爱速搭 | 版本树、依赖校验、数组 Diff 修复、导入冲突解决、晋升管道图 |
| 7 | 团队协作 | 45% | 80% | Mendix 协同编辑、Appian 评论锚点、明道云 PAT 治理 | 乐观锁、评论线程、@提及补全、锁转移、协作历史 |
| 8 | DevSecOps | 30% | 80% | OutSystems LifeTime、华为 AppCube 三环境、网易轻舟源码导出 | 发布校验、灰度发布、多级审批、回滚预览、影响分析、APM |
| 9 | 扩展性 | 15% | 80% | NocoBase 微内核插件、ToolJet 三类插件、Power Apps PCF | 组件 SDK、iframe 沙箱、组件市场、propsSchema 校验、插件热插拔 |
| 10 | 多端渲染 | 10% | 75% | 腾讯微搭多端、Zoho 原生+离线、Joget PWA | 响应式断点、设备模拟、PWA、移动端组件、实时同步 |
| 11 | 模板复用 | 40% | 80% | Zoho 250+ 模板、用友资产库、金蝶模型资产化 | 模板版本、模板市场、参数化、多类型模板、升级机制 |
| 12 | 触发器 | 25% | 80% | ServiceNow Flow Designer、Zoho 7 类触发、Budibase Automation | CRUD 钩子、Quartz 真实调度、条件触发、触发器链、执行历史、Cron 编辑器 |

### 2.2 最值得借鉴的 15 个具体功能点

| # | 借鉴功能 | 来源平台 | 落地批次 |
|---|---|---|---|
| 1 | Groovy 沙箱（SecureASTCustomizer 禁 Runtime/System/Process） | Mendix/OutSystems 沙箱 | 批次 1 |
| 2 | 微流执行轨迹表 + 节点级耗时统计 | Joget APM | 批次 1 |
| 3 | 全局 Undo/Redo composable（50 步栈） | Power Apps Studio | 批次 1 |
| 4 | 表达式编辑器（monaco + 字段补全 + 绑定树） | Budibase Bindings Drawer | 批次 1 |
| 5 | 属性面板 schema 驱动（select/array/object/code/expression） | NocoBase JSONSchema | 批次 1 |
| 6 | CRUD 触发器 before/after 钩子 | Zoho/Budibase Automation | 批次 1 |
| 7 | Quartz 真实调度（Scheduler.scheduleJob + CronTrigger） | ServiceNow Flow Designer | 批次 1 |
| 8 | 微流 DAG 画布（X6 + 节点面板 + 参数面板） | Mendix Microflows | 批次 2 |
| 9 | bpmn-js 流程设计器（画布 + 网关 + 泳道 + 属性面板） | Appian/Camunda | 批次 2 |
| 10 | 决策表可视化编辑器（Hit Policy + 条件列/动作列） | Appian Decision Designer | 批次 2 |
| 11 | 连接器分步表单 + OpenAPI 导入 + 测试控制台 | Power Apps Custom Connectors | 批次 2 |
| 12 | OAuth2 认证 + CircuitBreaker + 分页聚合 | Mendix Connectors | 批次 4 |
| 13 | 组件 iframe 沙箱 + 自定义注册 + 市场 | ToolJet 三类插件 / Power Apps PCF | 批次 4 |
| 14 | 版本树 + 环境晋升管道图 + 冲突解决 | Appsmith Git / OutSystems LifeTime | 批次 5 |
| 15 | 多级审批 + 灰度发布 + 回滚预览 + 影响分析 | OutSystems LifeTime / 华为 AppCube | 批次 5 |

---

## 三、五批次详细规划

### 批次 1（P0 基建 + 关键修复）— 14 Task

**目标**：修复生产级硬伤 + 安全风险 + 补齐引擎占位 + 前端基建，为后续可视化设计器打基础。

**后端（8 Task）**：
- T1: Groovy 沙箱（SecureASTCustomizer）+ Aviator 沙箱
- T2: 补齐 6 个微流执行器（LOOP/CALL_MICROFLOW/CALL_RULE/CALL_CONNECTOR/THROW_EXCEPTION）
- T3: 微流执行轨迹表（microflow_execution_log）+ 节点耗时统计
- T4: CRUD 触发器挂钩 DynamicEntityDataService（before/after create/update/delete）
- T5: Quartz 真实调度（Scheduler.scheduleJob + CronTrigger + JobDataMap）
- T6: 发布校验实现（按 configType 分发到 Entity/Form/List 校验）+ approve snapshot null bug 修复
- T7: 依赖校验实现（解析 snapshot 中 entityCode/connectorCode/microflowCode 引用）
- T8: 关键 bug 修复：ALTER 真实字段 Diff + 外键目标表名修复 + 数组 Diff 基于 id 对齐 + 连接器凭据 AES 加密

**前端（6 Task）**：
- T9: useUndoRedo composable（50 步栈 + Ctrl+Z/Y）+ 接入 4 个设计器
- T10: 表达式编辑器组件（monaco-editor + 字段/变量补全 + 绑定树侧栏）
- T11: LowCodePropertyPanel 扩展（select/array/object/color/date/code/expression 类型）
- T12: form-designer 消费 LowCodeComponentRegistry（打通 15 Widget）+ list-designer
- T13: EntityNode 注册到 X6（x6-vue-shape）+ 多实体画布 + 索引/主键策略 UI
- T14: 响应式断点 schema（xs/sm/md/lg/xl）+ JsonTreeDiff XSS 修复（DOMPurify）

**借鉴平台**：Mendix（沙箱/微流）、Joget（APM）、Power Apps（Undo）、Budibase（Bindings）、NocoBase（schema）、Zoho（触发器）、ServiceNow（调度）、Appsmith（Diff）

### 批次 2（P0 可视化设计器）— 12 Task

**目标**：用可视化画布替代 JSON 文本框，让微流/规则/流程/连接器/触发器 5 个设计器达到产品级。

- T1: 微流 DAG 画布（@antv/x6 + 节点面板 + 连线 + 参数面板 + 变量面板）
- T2: 微流执行日志可视化（节点高亮 + 输入输出展开 + 耗时）
- T3: 决策表编辑器（Hit Policy + 条件列/动作列 + 行编辑 + 规则版本）
- T4: 规则表达式编辑器（复用 monaco + 规则函数库）
- T5: bpmn-js 集成（BPMN 画布 + 绘制/连线/网关/泳道）
- T6: bpmn-js 属性面板（节点表单绑定 + 审批人 + 超时 + 回调微流）
- T7: 流程预览模式（只读 BPMN + 当前节点高亮）
- T8: 连接器分步表单（基本信息/认证/操作/映射/分页/重试/测试）
- T9: OpenAPI/Swagger 导入自动生成操作
- T10: 连接器测试控制台（请求预览 + 响应详情 + 耗时 + 历史）
- T11: 触发器构建器（按类型分步：CRUD 实体选择/QUARTZ Cron 编辑器/EVENT 事件选择）
- T12: Cron 可视化编辑器（分/时/日/月/周 + 人类可读描述 + 下次执行预览）

**借鉴平台**：Mendix Microflows、Appian Decision/Process、Camunda bpmn-js、Power Apps Connectors、ServiceNow Flow Designer

### 批次 3（P1 引擎完善）— 10 Task

**目标**：接入 LiteFlow、微流断点调试、规则版本、流程实例管理、多数据源统一建模、动态查询增强。

- T1: LiteFlow 真实接入（消除占位）+ 规则集编排
- T2: 微流断点调试（debug 模式 + step over/continue + 变量监视）
- T3: 规则版本管理（复用 LowCodeConfigVersionService）+ 规则测试用例
- T4: 流程实例管理 API（start/suspend/terminate/listInstances）
- T5: 任务完成回调钩子（完成任务时触发微流）
- T6: 流程图渲染 API（导出 SVG/PNG）
- T7: 多数据源统一建模（借鉴活字格：外联库直连/外联表副本/中间库整合）
- T8: 动态查询增强（LIKE/IN/BETWEEN/排序/OR 条件/关联表 join）
- T9: DDL 回滚机制（RESTORE 接口 + 一键回滚）
- T10: PostgreSQL/SQL Server 方言实现（DdlGenerator 接口扩展）

**借鉴平台**：LiteFlow、Mendix Microflows 调试、Appian 流程、活字格多数据源、NocoBase 多数据库

### 批次 4（P1 集成能力 + 扩展性 + 多端）— 10 Task

**目标**：连接器企业级能力、组件沙箱、多端渲染。

- T1: OAuth2 认证（client_credentials + token 刷新）
- T2: CircuitBreaker + RateLimiter（Resilience4j）
- T3: 分页聚合（OFFSET/PAGE/NEXT_LINK）
- T4: Request/Response 映射（JSONPath + 模板引擎）
- T5: MQ 连接器（Kafka/RabbitMQ）+ File 连接器（SFTP/S3）
- T6: 组件 SDK 规范（Vue3 defineComponent + props 类型 + 打包）
- T7: 组件 iframe 沙箱（postMessage 协议 + CSP 白名单）
- T8: 组件市场（上架/下架/搜索/下载/版本/审核）
- T9: 响应式断点设计器 UI + 多端渲染器
- T10: PWA + 移动端组件（底部弹出/抽屉/手势/扫码）

**借鉴平台**：Mendix Connectors、ToolJet 三类插件、Power Apps PCF、腾讯微搭多端、Zoho 原生+离线、Joget PWA

### 批次 5（P2 治理 + 协作 + 模板）— 10 Task

**目标**：版本治理、发布治理、协作增强、模板生态、APM。

- T1: 版本树可视化（父子关系 + 分支/标签）
- T2: 环境晋升管道图（DEV→TEST→PROD + 门禁审批）
- T3: 导入冲突解决 UI（冲突列表 + 逐项选择保留版本）
- T4: 多级审批链配置 + 灰度发布（比例/租户白名单）
- T5: 回滚预览（复用 JsonTreeDiff）+ 发布影响范围分析
- T6: 协同编辑（Yjs + y-websocket 基础版）+ 在线状态
- T7: @提及自动补全（用户选择器下拉）+ 评论锚点（字段/节点定位）+ 评论线程
- T8: 模板市场（上架/下架/搜索/下载/评分）+ 模板版本 + 参数化
- T9: APM 全链路追踪（Flowable 节点 + Groovy 微流 + Aviator 规则 + MyBatis SQL）
- T10: 应用源码导出 + 独立部署（借鉴网易轻舟，无黑盒引擎）

**借鉴平台**：Appsmith Git、OutSystems LifeTime、Mendix 协同、Appian 评论、Zoho 模板、网易轻舟源码导出、Joget APM

---

## 四、批次 1 详细设计

### 4.1 后端 T1: Groovy 沙箱

**问题**：`new GroovyShell(binding).evaluate(expression)` 无沙箱，可执行 `Runtime.getRuntime().exec("rm -rf /")`。

**方案**：引入 `groovy-sandbox` 或使用 `SecureASTCustomizer` 白名单。

```java
// GroovySandboxExecutor.java
CompilerConfiguration config = new CompilerConfiguration();
SecureASTCustomizer customizer = new SecureASTCustomizer();
customizer.setIndirectImportCheckEnabled(true);
customizer.setImportsWhitelist(Arrays.asList("java.lang", "java.util", "java.math"));
customizer.setStarImportsWhitelist(Arrays.asList("java.util"));
customizer.setStaticImportsWhitelist(Collections.emptyList());
customizer.setReceiversBlackList(Arrays.asList(
    System.class.getName(), Runtime.class.getName(),
    ProcessBuilder.class.getName(), Thread.class.getName(),
    ClassLoader.class.getName(), File.class.getName()
));
customizer.setStatementsBlacklist(Arrays.asList("while", "for")); // 可选
config.addCompilationCustomizers(customizer);
GroovyShell shell = new GroovyShell(binding, config);
```

**测试**：`assertThrows` 执行 `Runtime.getRuntime().exec("ls")` 被拒绝。

### 4.2 后端 T2: 补齐 6 个微流执行器

**节点定义**（已在 MicroflowNodeType 枚举中）：
- LOOP: 循环执行，`loopVar` + `iterable` + `bodyNodeIds`
- CALL_MICROFLOW: 调用子微流，`microflowCode` + `inputs`
- CALL_RULE: 调用规则，`ruleCode` + `inputs`
- CALL_CONNECTOR: 调用连接器，`connectorCode` + `inputs`
- THROW_EXCEPTION: 抛异常，`errorMessage` + `errorCode`

每个执行器实现 `MicroflowNodeExecutor` 接口。

### 4.3 后端 T3: 微流执行轨迹表

**新表**（V42）：
```sql
CREATE TABLE pms_lowcode_microflow_execution_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  microflow_id BIGINT NOT NULL,
  microflow_code VARCHAR(64) NOT NULL,
  execution_id VARCHAR(64) NOT NULL COMMENT '执行唯一ID',
  node_id VARCHAR(64) NOT NULL,
  node_type VARCHAR(32) NOT NULL,
  start_time DATETIME NOT NULL,
  end_time DATETIME NULL,
  duration_ms BIGINT NULL,
  inputs JSON NULL,
  outputs JSON NULL,
  variables_snapshot JSON NULL,
  status VARCHAR(16) NOT NULL COMMENT 'RUNNING/SUCCESS/FAILED',
  error_message TEXT NULL,
  INDEX idx_execution_id (execution_id),
  INDEX idx_microflow_id (microflow_id)
);
```

### 4.4 前端 T9: useUndoRedo composable

```typescript
// src/composables/useUndoRedo.ts
export function useUndoRedo<T>(initial: T, maxHistory = 50) {
  const past = ref<T[]>([])
  const present = ref<T>(initial)
  const future = ref<T[]>([])

  function set(newValue: T) {
    past.value.push(present.value)
    if (past.value.length > maxHistory) past.value.shift()
    present.value = newValue
    future.value = []
  }
  function undo() { /* ... */ }
  function redo() { /* ... */ }
  const canUndo = computed(() => past.value.length > 0)
  const canRedo = computed(() => future.value.length > 0)
  return { present, set, undo, redo, canUndo, canRedo }
}
```

### 4.5 前端 T10: 表达式编辑器

引入 `monaco-editor`，提供：
- 语法高亮（JavaScript/Groovy/Aviator）
- 字段自动补全（从当前表单 schema 加载字段列表）
- 变量自动补全（从微流上下文加载 variables）
- 函数库提示（math/string/date/collection）
- 绑定树侧栏（借鉴 Budibase Bindings Drawer）

---

## 五、风险与对策

| 风险 | 影响 | 对策 |
|---|---|---|
| Groovy 沙箱误杀合法表达式 | 高 | 白名单测试 + 可配置开关 |
| Quartz 调度任务丢失 | 高 | DB 持久化（JobStoreTX）+ 启动恢复 |
| monaco-editor 体积大 | 中 | 按需加载 + Web Worker |
| 批次间依赖导致阻塞 | 中 | 严格按批次顺序，批次内并行 |
| 生产数据迁移风险 | 高 | DDL 变更前自动备份 + 回滚预案 |

---

## 六、验收标准（80% 生产可用）

每个批次交付需满足：
1. **编译通过**：`mvn clean package` + `npx vue-tsc --noEmit` + `npx vite build` 全绿
2. **测试覆盖**：新功能单元测试 + 关键路径集成测试，覆盖率 60%+
3. **无占位实现**：所有 TODO/占位代码消除或明确标注延后理由
4. **安全合规**：无 Groovy 命令注入、无凭据明文、无 XSS
5. **文档同步**：API 文档 + 组件文档更新
6. **集成验证**：端到端场景跑通（建实体→生成 DDL→设计表单→绑定微流→发布）

---

## 七、执行方式

采用 **subagent-driven-development**：
- 每个 Task 派发独立子代理执行
- 两阶段审查（编译验证 + 代码审查）
- 频繁 commit（每 Task 一个 commit）
- 每批次完成打 tag 并推送

