# 低代码平台功能完善规划方案

> **文档状态**：评审通过 ✓
> **版本**：v1.1（含评审决策）
> **日期**：2026-07-07
> **作者**：平台架构组
> **适用项目**：network-equipment-pms / pms-lowcode 模块
>
> **评审决策记录（2026-07-07）**：
> 1. 范围确认：阶段一-四（25 项功能点）覆盖核心需求，阶段五（AI）延后 ✓
> 2. 数据建模深度：F1.1 实体设计器需支持复杂关联（多对多/自关联/级联删除）✓
> 3. 微流 vs 规则引擎：F2.1 微流（图灵完备）与 F2.2 规则引擎（决策表）都需要 ✓
> 4. 流程编排复用：F2.4 流程设计器复用现有 Flowable + 新增前端设计器 ✓
> 5. 协作模式：F4.1 悲观锁优先，CRDT（F4.3）延后 ✓
> 6. 部署灰度：F4.8 灰度发布延后到平台成熟期 ✓
> 7. 技术选型：ER 图编辑器 X6（开源），微流编辑器 X6，Vue 节点适配器 + 交互插件（拖拽、撤销重做）✓
> 8. 数据库范围：DDL 生成仅支持 MySQL 8.0，DDL 生成器抽象为接口预留 PostgreSQL 扩展点 ✓

---

## 一、主流低代码平台调研分析

### 1.1 调研对象与维度

对业界 4 大主流企业级低代码平台（Mendix、OutSystems、Microsoft Power Apps、Appian）从以下维度进行调研：

- 核心功能模块（数据建模、UI 设计、业务逻辑、流程编排、集成）
- 用户交互模式（设计器形态、协作模式、AI 辅助）
- 技术实现方案（架构、扩展机制、部署、版本控制）
- 差异化能力（AI、Data Fabric、Process Mining）

### 1.2 四大平台核心能力对比

| 能力维度 | Mendix（西门子） | OutSystems | Microsoft Power Apps | Appian |
|---------|------------------|------------|---------------------|--------|
| **建模范式** | 模型驱动（MDD），5 大 DSL | 全栈可视化 + OML 蓝图 | Canvas（自由）+ Model-Driven（数据驱动） | BPMN 流程驱动 + Data Fabric |
| **数据建模** | UML 可视化领域模型 | 可视化数据库管理 | Dataverse 统一数据存储 + 900+ 连接器 | Data Fabric 虚拟化（SQL/SAP/Salesforce/Oracle） |
| **UI 设计** | WYSIWYG 页面编辑器，响应式 | 拖拽式 UI + 模板 | Canvas 自由布局 + Model-Driven 自动生成 | SAIL 自组装界面层 |
| **业务逻辑** | Microflow（服务端）+ Nanoflow（客户端） | 可视化逻辑 + 预构建组件 | Excel 风格公式 + Power Fx | Smart Services + 业务规则 |
| **流程编排** | 集成式 Workflow 语言 | 工作流引擎 | Power Automate（BPM） | BPMN Process Modeler + RPA |
| **AI 能力** | Maia（Logic/BestPractice/Workflow Recommender）+ ML Kit | AI 生成完整应用 + Agent 编排 | Copilot 自然语言生成应用 + 生成式页面 + Agent | Agent Studio + AI Document Center + Smart Search |
| **协作** | Portfolio Mgmt + App Insights + Kanban/Scrum + Git | 协作开发 + 反馈管理 | Teams 集成 + 解决方案层 | Process HQ + 协作开发 |
| **版本控制** | Git-based 版本控制 | 内置版本 + 环境晋升 | 解决方案版本 + 环境晋升 | 应用版本 + 包管理 |
| **扩展性** | Model SDK + Platform APIs + Marketplace | 自定义代码扩展 + 连接器 SDK | Custom Connectors + PCF 组件框架 | SDK + 自定义 Smart Service |
| **部署** | 一键部署，云原生无状态容器 | 一键部署 + 自动扩缩容 | 环境晋升 + ALM 加速器 | 包部署 + Autoscale |
| **DevSecOps** | 自动化测试 + 调试 + 质量检查 | 内置 DevSecOps（依赖分析 + 代码审查 + 监控） | ALM + Power Platform CoE | Process Mining + 性能追踪 |
| **多端** | Web + Mobile + 离线 | Web + Mobile + PWA | Web + Mobile + 离线 + Tablet | Web + Mobile + 离线 |
| **差异化** | 模型即代码，白盒效应 | AI-readable OML 项目蓝图 | 生成式页面（Agent 直接写 React 代码） | Data Fabric 无需数据迁移 + Process Mining |

### 1.3 共性核心能力提炼（业内主流平台必备）

通过横向对比，提炼出低代码平台必备的 **12 项核心能力**：

1. **可视化数据建模** — 实体/属性/关联/索引的可视化定义，自动生成 DDL
2. **拖拽式 UI 设计器** — 组件库 + 画布 + 属性面板三栏布局，WYSIWYG
3. **可视化业务逻辑** — 微流/纳流/规则引擎，服务端 + 客户端逻辑
4. **流程编排（BPM）** — BPMN 流程建模 + 审批 + 自动化 + 人工任务
5. **数据连接器** — 预构建连接器 + 自定义连接器 + 数据虚拟化
6. **AI 辅助开发** — 自然语言生成 + 逻辑推荐 + 最佳实践推荐
7. **版本控制** — 配置版本树 + 分支/合并 + 环境晋升 + 回滚
8. **团队协作** — 协作编辑 + 锁机制 + 评论 + 敏捷项目管理
9. **DevSecOps** — 一键部署 + 自动化测试 + 监控 + 安全扫描
10. **扩展性** — 组件 SDK + 沙箱 + 自定义代码 + 插件市场
11. **多端渲染** — Web + Mobile + 离线 + 响应式
12. **模板复用** — 预置模板 + 可复用组件 + 应用市场

### 1.4 技术实现趋势

- **模型驱动（MDD）** 成为共识：Mendix 5 大 DSL、OutSystems OML、Power Apps Dataverse、Appian Data Fabric 均采用"模型即代码"
- **AI 原生** 成为新标准：4 大平台 2025 年均推出 AI Agent / Copilot 能力，从"辅助"走向"生成"
- **Data Fabric / 数据虚拟化** 解决集成痛点：Appian 无需数据迁移即可连接异构数据源
- **环境晋升 + 包管理** 替代传统 CI/CD：通过配置包在 dev/test/prod 间晋升
- **开放扩展** 平衡标准与灵活：SDK + 自定义代码 + 组件市场，避免厂商锁定

---

## 二、现状评估（pms-lowcode 已有能力 vs 业内标准）

### 2.1 能力成熟度矩阵

| # | 核心能力 | 业内标准 | pms-lowcode 现状 | 成熟度 | 差距 |
|---|---------|---------|-----------------|--------|------|
| 1 | 可视化数据建模 | 实体/属性/关联/索引可视化 + DDL 生成 | ❌ 无，仅 JSON 配置存储 | 0% | 全量缺失 |
| 2 | 拖拽式 UI 设计器 | 组件库 + 画布 + 属性面板 + WYSIWYG | ✅ 4 类设计器已实现 | 70% | 缺实时预览独立模式、多设备模拟 |
| 3 | 可视化业务逻辑 | 微流/纳流/规则引擎 | ❌ 无，仅 events 回调名约定 | 0% | 全量缺失 |
| 4 | 流程编排（BPM） | BPMN 流程 + 审批 + 自动化 | ⚠️ 依赖 pms-workflow 模块（Flowable），未与低代码集成 | 30% | 需低代码流程设计器 |
| 5 | 数据连接器 | 预构建 + 自定义连接器 | ❌ 无，searchApi 仅支持内部 REST | 10% | 需连接器框架 |
| 6 | AI 辅助开发 | 自然语言生成 + 推荐 | ❌ 无 | 0% | 全量缺失（可后续阶段） |
| 7 | 版本控制 | 配置版本树 + 分支/合并 + 环境晋升 | ⚠️ 仅 @Version 乐观锁 + DRAFT/PUBLISHED/ARCHIVED 状态机 | 20% | 缺版本历史、对比、回滚、环境晋升 |
| 8 | 团队协作 | 协作编辑 + 锁 + 评论 | ❌ 无 | 0% | 全量缺失 |
| 9 | DevSecOps | 一键部署 + 测试 + 监控 | ⚠️ 有导入导出 + 权限，缺一键发布流水线 | 25% | 需发布流水线 |
| 10 | 扩展性 | 组件 SDK + 沙箱 + 市场 | ⚠️ type=custom 占位，无 SDK | 10% | 需组件 SDK + 注册机制 |
| 11 | 多端渲染 | Web + Mobile + 离线 | ⚠️ 有响应式（responsive.scss），无独立移动端/离线 | 30% | 需设备模拟 + 移动端优化 |
| 12 | 模板复用 | 预置模板 + 可复用组件 + 市场 | ✅ 10 个预置模板 + 导入导出 | 60% | 缺组件市场 + 模板版本管理 |

### 2.2 综合评估

- **整体成熟度**：约 **25%**（12 项能力中 2 项达 60%+，4 项部分实现，6 项缺失）
- **核心优势**：UI 设计器（4 类页面）+ 配置存储 + 权限菜单已具备基础
- **关键差距**：数据建模、业务逻辑、流程编排、版本控制、团队协作、扩展性 6 大能力缺失
- **结论**：当前 pms-lowcode 属于"表单/列表配置工具"层级，距离"生产级低代码平台"仍有显著差距，需系统性补齐

---

## 三、功能规划方案

### 3.1 设计原则

1. **模型驱动（MDD）** — 所有能力以可视化模型为核心，模型即配置、配置即代码
2. **渐进增强** — 在现有 4 类设计器基础上扩展，不推翻重写
3. **复用现有基础设施** — 流程引擎复用 Flowable、规则引擎复用 pms-rules（Aviator/LiteFlow/Groovy）、文件复用 pms-file、通知复用 pms-notification
4. **企业级优先** — 版本控制、协作、权限、审计等企业级能力优先于 AI 等锦上添花能力
5. **开放扩展** — 通过 SDK + 沙箱 + 注册机制支持自定义扩展，避免厂商锁定

### 3.2 功能清单与优先级

按 **MoSCoW 优先级**（Must/Should/Could/Won't）+ **实施阶段**划分，共 **8 大能力域、38 项功能点**。

#### 阶段一（P0 — Must Have）：核心建模与版本控制

| ID | 能力域 | 功能点 | 优先级 | 技术实现路径 | 复杂度 |
|----|--------|--------|--------|-------------|--------|
| F1.1 | 数据建模 | 可视化实体设计器（实体/属性/类型/主键/索引/关联） | P0 | 前端：新增 EntityDesigner.vue（ER 图 + 属性表）；后端：LowCodeEntity/Field 实体 + Mapper + DDL 生成器（JCodeModel） | 高 |
| F1.2 | 数据建模 | DDL 自动生成与执行（CREATE TABLE/ALTER TABLE/INDEX） | P0 | 后端：DDLGenerator 基于 JSqlParser 生成 SQL；Flyway 动态迁移 V29+；审计日志 | 高 |
| F1.3 | 数据建模 | 实体数据 CRUD API 自动生成（RESTful） | P0 | 后端：DynamicEntityController 运行时反射 + MyBatis-Plus 动态表名；权限校验 | 高 |
| F1.4 | 版本控制 | 配置版本快照（每次发布生成不可变快照） | P0 | 后端：LowCodeConfigVersion 表（configId/version/snapshot/status）；发布时快照 | 中 |
| F1.5 | 版本控制 | 版本历史列表 + Diff 对比（字段级） | P0 | 前端：VersionHistory.vue + jsondiffpatch 库；后端：版本查询 + Diff 计算 | 中 |
| F1.6 | 版本控制 | 版本回滚（恢复到任意历史版本） | P0 | 后端：回滚生成新版本（不删除历史）+ 审计日志 | 中 |
| F1.7 | 版本控制 | 环境晋升（dev → test → prod 配置包迁移） | P0 | 后端：ConfigPackage 导出/导入 + 环境标记 + 依赖校验 | 高 |

#### 阶段二（P1 — Should Have）：业务逻辑与流程编排

| ID | 能力域 | 功能点 | 优先级 | 技术实现路径 | 复杂度 |
|----|--------|--------|--------|-------------|--------|
| F2.1 | 业务逻辑 | 可视化微流设计器（服务端逻辑流，节点：开始/结束/赋值/条件/循环/调用/异常） | P1 | 前端：MicroflowDesigner.vue（基于 LogicFlow 的 DAG 编辑器）；后端：Microflow 引擎解释执行（Groovy 脚本生成） | 极高 |
| F2.2 | 业务逻辑 | 规则引擎集成（决策表 + 表达式规则） | P1 | 后端：复用 pms-rules（Aviator 表达式 + LiteFlow DSL）；LowCodeRule 实体 + Mapper | 高 |
| F2.3 | 业务逻辑 | 表单事件绑定（onLoad/onChange/onSubmit 调用微流/规则） | P1 | 前端：FormRenderer 增强 eventHandlers 注册；后端：事件触发微流执行 | 中 |
| F2.4 | 流程编排 | 低代码流程设计器（BPMN 可视化，复用 Flowable） | P1 | 前端：BpmnDesigner.vue（基于 bpmn-js）；后端：LowCodeProcess + 部署到 Flowable | 极高 |
| F2.5 | 流程编排 | 表单 × 流程绑定（流程节点绑定低代码表单） | P1 | 后端：ProcessFormBinding；前端：流程设计器节点属性配置表单 | 中 |
| F2.6 | 流程编排 | 流程触发器（数据 CRUD/定时/事件 触发流程） | P1 | 后端：ProcessTrigger（实体监听 + Quartz 定时 + 事件总线） | 高 |

#### 阶段三（P1 — Should Have）：扩展性与连接器

| ID | 能力域 | 功能点 | 优先级 | 技术实现路径 | 复杂度 |
|----|--------|--------|--------|-------------|--------|
| F3.1 | 扩展性 | 自定义组件 SDK（Vue 组件注册 + 属性面板定义） | P1 | 前端：LowCodeComponentRegistry + 组件清单规范；构建工具：vite-plugin-lowcode 打包 | 高 |
| F3.2 | 扩展性 | 组件沙箱（iframe 隔离 + postMessage 通信） | P1 | 前端：ComponentSandbox.vue（iframe + postMessage 协议）；安全：CSP + 白名单 | 高 |
| F3.3 | 扩展性 | 组件市场（上传/下载/版本管理/审核） | P2 | 后端：ComponentMarket 表 + 审核 workflow；前端：组件市场页面 | 中 |
| F3.4 | 连接器 | REST 连接器配置（URL/Method/Header/Body/认证/分页/重试） | P1 | 后端：LowCodeConnector + ConnectorExecutor（RestTemplate + Resilience4j）；前端：连接器配置 UI | 高 |
| F3.5 | 连接器 | 数据库连接器（JDBC 直连外部数据源查询） | P1 | 后端：DataSourceConfig（RoutingDataSource 扩展）+ SQL 模板 + 参数化 | 中 |
| F3.6 | 连接器 | 连接器市场（预置 D365/FP/OA/MES 等连接器） | P2 | 后端：预置连接器模板；前端：市场页面 | 中 |

#### 阶段四（P2 — Could Have）：协作、预览、部署

| ID | 能力域 | 功能点 | 优先级 | 技术实现路径 | 复杂度 |
|----|--------|--------|--------|-------------|--------|
| F4.1 | 团队协作 | 配置编辑锁（悲观锁，编辑时锁定，超时自动释放） | P2 | 后端：ConfigLock（Redis SETNX + TTL 30min + 心跳续期） | 中 |
| F4.2 | 团队协作 | 配置评论 + @提及通知 | P2 | 后端：ConfigComment 表 + 通知接入 pms-notification；前端：评论侧边栏 | 中 |
| F4.3 | 团队协作 | 协作编辑（基于 OT/CRDT 实时协同） | P3 | 前端：Yjs + y-websocket；后端：WebSocket 协作网关 | 极高 |
| F4.4 | 实时预览 | 独立预览模式（脱离设计器全屏预览） | P2 | 前端：PreviewMode.vue（iframe 渲染 + 配置实时同步） | 低 |
| F4.5 | 实时预览 | 多设备模拟（PC / Tablet / Mobile 尺寸切换） | P2 | 前端：DeviceSimulator（预设尺寸 + 自定义 + 横竖屏） | 低 |
| F4.6 | 实时预览 | 编辑-预览实时同步（配置变更即刷新） | P2 | 前端：响应式 config watch + iframe postMessage | 低 |
| F4.7 | 部署 | 一键发布流水线（校验 → 审批 → 发布 → 通知） | P2 | 后端：PublishPipeline（校验 + 审批 workflow + 发布 + 通知）；前端：发布向导 | 高 |
| F4.8 | 部署 | 灰度发布（按用户/角色/比例放量） | P3 | 后端：GrayRelease（用户白名单 + 角色过滤 + 比例计算）；前端：灰度配置 — **评审决策：延后到平台成熟期** | 高 |
| F4.9 | 部署 | 回滚（一键回滚到上一版本） | P2 | 后端：复用 F1.6 版本回滚 + 发布记录 | 低 |

#### 阶段五（P3 — Won't Have in this plan）：AI 辅助（后续规划）

| ID | 能力域 | 功能点 | 优先级 | 技术实现路径 | 复杂度 |
|----|--------|--------|--------|-------------|--------|
| F5.1 | AI | 自然语言生成表单/列表配置（LLM + Schema 约束） | P3 | 后端：LLM 集成（OpenAI/通义千问）+ Prompt 工程 + Schema 校验 | 极高 |
| F5.2 | AI | 逻辑推荐（基于上下文推荐下一步操作） | P3 | 后端：规则挖掘 + 推荐引擎 | 极高 |
| F5.3 | AI | 配置质量评分（最佳实践检查 + 修复建议） | P3 | 后端：质量规则引擎 + 评分模型 | 高 |

### 3.3 数据模型扩展

新增 **8 张表**（Flyway V29-V36）：

| 版本 | 表名 | 用途 |
|------|------|------|
| V29 | `pms_lowcode_entity` | 低代码实体定义（name/tableName/bizType） |
| V30 | `pms_lowcode_field` | 实体字段定义（entityId/name/type/length/nullable/primaryKey/indexed） |
| V31 | `pms_lowcode_relation` | 实体关联关系（fromEntityId/toEntityId/type/reverseName） |
| V32 | `pms_lowcode_config_version` | 配置版本快照（configType/configId/version/snapshot/status） |
| V33 | `pms_lowcode_microflow` | 微流定义（code/name/triggerType/definition JSON） |
| V34 | `pms_lowcode_rule` | 规则定义（code/name/type/expression/script） |
| V35 | `pms_lowcode_connector` | 连接器定义（code/name/type/config authType） |
| V36 | `pms_lowcode_config_lock` | 配置编辑锁（configType/configId/userId/expireAt） |

### 3.4 模块结构调整

```
pms-lowcode/
├── controller/
│   ├── LowCodeEntityController.java          # 新增：实体 CRUD + DDL 生成
│   ├── LowCodeConfigVersionController.java   # 新增：版本管理
│   ├── LowCodeMicroflowController.java       # 新增：微流 CRUD + 执行
│   ├── LowCodeRuleController.java            # 新增：规则 CRUD + 执行
│   ├── LowCodeConnectorController.java       # 新增：连接器 CRUD + 测试
│   └── LowCodePublishController.java         # 新增：发布流水线
├── entity/  (新增 8 个实体)
├── mapper/  (新增 8 个 Mapper)
├── service/ (新增 6 个 Service)
├── engine/  # 新增子包
│   ├── DdlGenerator.java                     # DDL 生成器
│   ├── DynamicEntityService.java             # 动态实体 CRUD
│   ├── MicroflowEngine.java                  # 微流执行引擎
│   └── ConnectorExecutor.java                # 连接器执行器
└── version/ # 新增子包
    ├── ConfigVersionService.java             # 版本快照
    ├── VersionDiffCalculator.java            # Diff 计算
    └── EnvironmentPromotionService.java      # 环境晋升
```

前端新增页面：

```
src/views/lowcode/
├── entity-designer/        # 新增：实体设计器（ER 图）
├── microflow-designer/     # 新增：微流设计器（DAG）
├── rule-designer/          # 新增：规则设计器（决策表 + 表达式）
├── connector-designer/     # 新增：连接器配置
├── version-history/        # 新增：版本历史 + Diff
├── publish/                # 新增：发布流水线向导
└── component-market/       # 新增：组件市场
```

---

## 四、实施路线图

### 4.1 五阶段路线图

```
阶段一（P0 核心）          阶段二（P1 逻辑/流程）      阶段三（P1 扩展/连接器）
├─ 数据建模               ├─ 微流设计器              ├─ 自定义组件 SDK
├─ DDL 生成执行           ├─ 规则引擎集成            ├─ 组件沙箱
├─ 动态实体 CRUD          ├─ 表单事件绑定            ├─ REST 连接器
├─ 配置版本快照           ├─ BPMN 流程设计器         ├─ 数据库连接器
├─ 版本 Diff/回滚         ├─ 表单×流程绑定           └─ 连接器市场
└─ 环境晋升               └─ 流程触发器

阶段四（P2 协作/预览/部署）   阶段五（P3 AI — 后续规划）
├─ 配置编辑锁              ├─ 自然语言生成配置
├─ 配置评论                ├─ 逻辑推荐
├─ 独立预览模式            └─ 配置质量评分
├─ 多设备模拟
├─ 一键发布流水线
└─ 回滚
```

### 4.2 阶段里程碑

| 阶段 | 交付物 | 功能点数 | 关键里程碑 |
|------|--------|---------|-----------|
| 阶段一（P0） | 数据建模 + 版本控制 | 7 | 实体设计器上线 + 环境晋升打通 |
| 阶段二（P1） | 业务逻辑 + 流程编排 | 6 | 微流可执行 + BPMN 可部署 |
| 阶段三（P1） | 扩展性 + 连接器 | 6 | 自定义组件可注册 + 外部数据源可查 |
| 阶段四（P2） | 协作 + 预览 + 部署 | 9 | 一键发布流水线上线 |
| 阶段五（P3） | AI 辅助（后续规划） | 3 | — |

### 4.3 优先级排序依据

1. **阶段一优先**：数据建模是低代码平台的基础（无数据模型则业务逻辑/流程/连接器无处挂载）；版本控制是企业级使用的硬性要求（无版本=无法回滚=生产事故）
2. **阶段二、三并行**：业务逻辑与扩展性可并行开发，互不阻塞
3. **阶段四延后**：协作/预览/部署在核心建模能力就绪后才有价值
4. **阶段五不规划**：AI 辅助依赖 LLM 集成与大量训练数据，当前阶段投入产出比低，建议平台成熟后再规划

### 4.4 依赖关系

```
F1.1 实体设计器 → F1.2 DDL 生成 → F1.3 动态实体 CRUD
                                   ↓
F2.1 微流设计器 → F2.3 表单事件绑定（依赖 F1.3 动态实体）
F2.4 流程设计器 → F2.5 表单×流程绑定 → F2.6 流程触发器（依赖 F1.3）

F1.4 版本快照 → F1.5 Diff → F1.6 回滚 → F4.9 回滚（复用）
F1.4 版本快照 → F1.7 环境晋升 → F4.7 发布流水线

F3.1 组件 SDK → F3.2 沙箱 → F3.3 组件市场
F3.4 REST 连接器 → F3.6 连接器市场
```

---

## 五、技术实现路径详述（阶段一 P0）

### 5.1 数据建模（F1.1-F1.3）

**F1.1 可视化实体设计器**

- 前端：基于 [AntV X6](https://x6.antv.antgroup.com/)（开源，**评审决策选型**）实现 ER 图编辑器
  - 实体以矩形节点表示，字段以列表项展示，关联以连线表示
  - X6 Vue 节点适配器 + 交互插件（拖拽、撤销重做、缩放、框选）
  - 属性面板：实体名/表名/业务类型 + 字段名/类型/长度/主键/索引/可空/默认值
  - 操作：新增实体/删除实体/新增字段/编辑字段/拖拽建立关联
  - **复杂关联支持（评审决策）**：多对多（自动生成中间表）、自关联（实体自引用外键）、级联删除（CASCADE/SET NULL/RESTRICT 三种策略可选）
- 后端：`LowCodeEntity` + `LowCodeField` + `LowCodeRelation` 三实体
  - 实体定义存储为配置，不立即生成 DDL
  - 校验：表名唯一、字段名合法、主键必填、关联双向校验

**F1.2 DDL 自动生成与执行**

- 后端：`DdlGenerator` 接口（抽象，预留 PostgreSQL 扩展点）+ `MySQLDdlGenerator` 实现（**评审决策：本期仅支持 MySQL 8.0**）
  - 支持 MySQL 8.0（项目主库 dppms_d365）
  - 生成 `CREATE TABLE` / `ALTER TABLE ADD COLUMN` / `ALTER TABLE DROP COLUMN` / `CREATE INDEX`
  - 支持复杂关联：多对多（自动生成中间表 DDL）、自关联（self-reference 外键）、级联删除（ON DELETE CASCADE / SET NULL / RESTRICT）
  - **安全策略**：DROP COLUMN 需二次确认 + 审批；DROP TABLE 禁止（仅归档）
- 执行：通过 Flyway 动态迁移（版本号 V29+ 自动递增），保留迁移历史
- 审计：每次 DDL 变更记录到 `sys_oper_log` + `pms_lowcode_config_version`

**F1.3 动态实体 CRUD API**

- 后端：`DynamicEntityController` 运行时反射
  - 路由：`/api/lowcode/data/{entityCode}`（GET 列表 / GET {id} 详情 / POST 新增 / PUT 更新 / DELETE 删除）
  - 数据访问：MyBatis-Plus 动态表名（`@TableName` 运行时解析 + `DynamicTableNameParser`）
  - 权限：`@PreAuthorize("hasAuthority('lowcode:data:{entityCode}:list')")` 动态权限
  - 校验：基于 `LowCodeField` 定义自动生成 Bean Validation（必填/长度/类型）

### 5.2 版本控制（F1.4-F1.7）

**F1.4 配置版本快照**

- 后端：`LowCodeConfigVersion` 表
  - 字段：`id` / `config_type`（FORM/LIST/TAB/RELATED_PAGE/ENTITY/MICROFLOW/...） / `config_id` / `version` / `snapshot`（JSON 全量快照）/ `status`（ACTIVE/ARCHIVED）/ `created_by` / `created_at` / `change_log`（变更说明）
  - 触发：每次 `publish` 操作生成新版本快照（不可变）
  - 存储：JSON 全量快照（非增量，简化实现 + 快速回滚）

**F1.5 版本 Diff 对比**

- 前端：`VersionHistory.vue`
  - 列表：版本号 / 变更说明 / 操作人 / 时间 / 操作（查看/Diff/回滚）
  - Diff：选择两个版本，左侧树形展示 JSON 结构差异（新增/删除/修改高亮）
  - 库：[jsondiffpatch](https://github.com/benjamine/jsondiffpatch) 计算 + 渲染 Diff
- 后端：`VersionDiffCalculator` 返回结构化 Diff（field path / old value / new value / change type）

**F1.6 版本回滚**

- 后端：回滚 = 用历史版本快照覆盖当前配置 + 生成新版本（**不删除历史**）
  - 审计：记录回滚操作 + 源版本号 + 目标版本号
  - 校验：回滚后配置需通过 Schema 校验

**F1.7 环境晋升**

- 后端：`EnvironmentPromotionService`
  - 导出：将 dev 环境的配置（含版本快照）打包为 `ConfigPackage`（JSON zip）
  - 导入：在 test/prod 环境导入，校验依赖（实体/连接器/微流是否存在）+ 覆盖确认
  - 环境标记：`pms_lowcode_config_version.environment`（DEV/TEST/PROD）
  - 审计：晋升记录到 `sys_oper_log` + 通知相关人员

---

## 六、风险与对策

| 风险 | 影响 | 对策 |
|------|------|------|
| DDL 自动生成导致生产数据丢失 | 极高 | DROP 操作需二次确认 + 审批；禁止 DROP TABLE；ALTER 前自动备份 |
| 动态实体 API 权限漏洞 | 高 | 动态权限校验（基于实体 code 生成权限标识）+ 默认拒绝 + 白名单 |
| 微流执行引擎性能问题 | 中 | 微流编译为 Groovy 脚本缓存 + 超时熔断 + 资源限制 |
| 版本快照存储膨胀 | 中 | JSON 压缩存储 + 90 天自动归档 + 仅保留最近 50 版本 |
| 环境晋升依赖冲突 | 中 | 晋升前依赖完整性校验 + 依赖清单可视化 |
| 协作编辑冲突 | 中 | 悲观锁（F4.1）优先，CRDT（F4.3）延后 |

---

## 七、评审要点

请评审人员重点关注以下决策点：

1. **范围确认**：阶段一-四（25 项功能点）是否覆盖核心需求？阶段五（AI）是否同意延后？
2. **数据建模深度**：F1.1 实体设计器是否需要支持复杂关联（多对多/自关联/级联删除）？还是仅支持简单外键？
3. **微流 vs 规则引擎**：F2.1 微流（图灵完备）与 F2.2 规则引擎（决策表）是否都需要？还是只做规则引擎？
4. **流程编排复用**：F2.4 流程设计器是复用现有 Flowable + 新增前端设计器，还是独立实现简化版？
5. **协作模式**：F4.1 悲观锁 vs F4.3 CRDT 实时协同，是否同意先做悲观锁、CRDT 延后？
6. **部署灰度**：F4.8 灰度发布是否本期需要？还是延后到平台成熟期？
7. **技术选型**：ER 图编辑器 GoJS（商业授权）vs X6（开源），微流编辑器 LogicFlow vs X6，请确认选型倾向
8. **数据库范围**：DDL 生成是否需要同时支持 MySQL + PostgreSQL？还是仅 MySQL？

---

## 八、参考来源

- [Mendix 模型驱动开发](https://www.mendix.com/de/visuelle-Modellierung/)
- [Mendix Platform](https://www.mendix.com/de/platform)
- [Mendix Release 10.24 LTS](https://www.mendix.com/blog/mendix-release-10-24-studio-pro-a-stable-ide-for-modern-enterprise-development/)
- [OutSystems AI-powered low-code platform](https://www.outsystems.com/low-code-platform/)
- [OutSystems vs Power Apps 对比](https://blog.csdn.net/2503_92849275/article/details/149858254)
- [Microsoft Power Apps 2025 Release Wave 1](https://learn.microsoft.com/en-us/power-platform/release-plan/2025wave1/power-apps/planned-features)
- [Introducing the new Power Apps: Generative power](https://www.microsoft.com/en-us/power-platform/blog/power-apps/introducing-the-new-power-apps-generative-power-meets-enterprise-grade-trust/)
- [Appian Platform Overview](https://appian.com/products/platform/overview.html)
- [Appian BPM](https://appian.com/products/platform/process-automation/business-process-management-bpm.html)
- [Appian Agentic AI 2025](https://appian.com/about/explore/press-releases/2025/appian-embeds-agentic-ai-into-business-processes-to-deliver-scal)
- [Appian Alternatives（SAIL/Data Fabric 详解）](https://manchtech.com/en/Comparison-Blogs-Section/appian-alternatives/)
