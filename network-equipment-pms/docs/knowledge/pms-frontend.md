# pms-frontend 模块知识库

> 源码根目录：`network-equipment-pms/pms-frontend/`
> 入口文件：`src/main.ts`、`src/App.vue`
> 构建产物：主应用 `dist/`、低代码组件 SDK `dist-sdk/`

## 模块概述

`pms-frontend` 是网络设备工程项目管理系统（PMS）的 Vue 3 单页前端应用，承担所有面向最终用户的浏览器交互：项目管理、资产管理、实施交付、计划基线、工作流审批、交付件全生命周期、风险/问题/变更治理、报表统计、低代码设计平台以及系统管理等。

该模块同时输出一份独立的「低代码组件 SDK」（`src/sdk/`，构建配置 `vite.config.lib.ts`），借鉴 Power Apps PCF / ToolJet Component SDK 模式，将低代码渲染器（表单、列表、Tab、关联页、Widget 组件等）打包为 `pms-lowcode-sdk.es.js` / `pms-lowcode-sdk.umd.js`，可在主应用之外被宿主引用。

整体定位：
- **业务前台**：项目全生命周期（立项 → 阶段 → 任务 → 交付件 → 终验 → 关闭）的统一交互入口，以「项目工作区 /workspace/:id」为枢纽，整合 8 个 Tab。
- **低代码平台前台**：实体 / 表单 / 列表 / Tab / 关联页 / 微流 / 规则 / 流程 / 触发器 / 连接器 / 发布中心 / 审批链 / 模板市场 / APM 看板 / 应用源码导出 全套设计器 UI。
- **系统管理控制台**：用户 / 角色 / 菜单 / 字典 / 缓存 / 定时任务 / 审计日志 / 系统状态 / 集成健康。

### 技术栈

| 维度 | 选型 |
|------|------|
| 框架 | Vue 3.5（`<script setup>` + Composition API） |
| 语言 | TypeScript 6（`strict: true`、`noImplicitAny`、`strictNullChecks`） |
| 构建 | Vite 8（`@vitejs/plugin-vue` 6） |
| 路由 | Vue Router 4.6（`createWebHistory` + 路由守卫 + 懒加载） |
| 状态管理 | Pinia 3 |
| UI 组件库 | Element Plus 2.14 + `@element-plus/icons-vue` |
| HTTP 客户端 | Axios 1.18（统一信封 + 拦截器） |
| 图编辑 / 流程图 | AntV X6 3（微流 / 实体设计器）、AntV G6 5（任务依赖 DAG）、bpmn-js 18 + bpmn-js-properties-panel 5 + camunda-bpmn-moddle 7 + diagram-js 15（流程设计器） |
| 代码编辑器 | Monaco Editor 0.55（`@guolao/vue-monaco-editor`） |
| 图表 | ECharts 6 |
| Excel | SheetJS（`xlsx`） |
| 富文本/安全 | DOMPurify 3 |
| 二维码/条码 | jsqr 1.4 |
| 差异对比 | jsondiffpatch 0.7 |
| 测试 | Vitest 4（单元）+ `@vue/test-utils` + jsdom + Playwright 1.49（E2E） |
| Lint | ESLint 9 + `eslint-plugin-vue` + `typescript-eslint` |
| 样式 | Sass（设计令牌 + 响应式 + Element Plus 覆盖） |

## 目录结构

```
pms-frontend/
├── public/                 # 静态资源（favicon、PWA manifest、Service Worker）
├── e2e/                    # Playwright E2E 用例（dashboard / login / lowcode / project）
├── src/
│   ├── api/                # API 封装层（按业务模块拆分，约 55 个文件）
│   ├── components/         # 公共组件（含低代码设计器 / Widget / 业务组件）
│   ├── composables/        # Vue Composition API 复用逻辑（useUndoRedo / useEditLock 等）
│   ├── config/             # 静态菜单配置（menu.ts）
│   ├── directives/         # 自定义指令（v-debounce / v-permission / 路由 loading）
│   ├── layouts/            # 布局壳子（DefaultLayout.vue）
│   ├── router/             # Vue Router 配置（index.ts）
│   ├── sdk/                # 低代码组件 SDK 公共入口（独立打包）
│   ├── stores/             # Pinia 状态管理（app / user / tags / websocket）
│   ├── styles/             # 全局样式与设计令牌（SCSS）
│   ├── types/              # TypeScript 全局类型声明（api / bpmn / declarations）
│   ├── utils/              # 工具类（request.ts：Axios 封装 + 数据校验集成）
│   ├── validators/         # 数据集成校验对象（前端镜像后端 @Valid）
│   ├── views/              # 页面视图（按业务模块分目录）
│   ├── App.vue             # 根组件（路由切换动画 + PWA 更新提示）
│   ├── main.ts             # 应用入口（注册 Pinia / Router / ElementPlus / 指令 / 图标）
│   ├── style.css           # 全局基础样式
│   └── vite-env.d.ts       # Vite 类型声明
├── index.html
├── package.json
├── vite.config.ts          # 主应用构建配置
├── vite.config.lib.ts      # 低代码组件 SDK 库模式构建配置
├── tsconfig.json / tsconfig.app.json / tsconfig.node.json
├── vitest.config.ts        # Vitest 配置
├── playwright.config.ts    # Playwright 配置（baseURL :3000）
└── eslint.config.js
```

## 技术栈与依赖

### 运行时依赖（dependencies）

| 依赖 | 版本 | 用途 |
|------|------|------|
| `vue` | ^3.5.39 | 核心框架，`<script setup>` SFC |
| `vue-router` | ^4.6.4 | SPA 路由 + 守卫 + 懒加载 |
| `pinia` | ^3.0.4 | 状态管理（user / app / tags / websocket） |
| `element-plus` | ^2.14.2 | UI 组件库 |
| `@element-plus/icons-vue` | ^2.3.2 | 图标库（在 `main.ts` 全局注册） |
| `axios` | ^1.18.1 | HTTP 客户端，统一封装在 `utils/request.ts` |
| `@antv/x6` | ^3.1.7 | 微流设计器 / 实体关系图编辑画布 |
| `@antv/x6-vue-shape` | ^3.0.2 | X6 Vue 节点适配（MicroflowNode / EntityNode） |
| `@antv/g6` | ^5.1.1 | 任务依赖关系 DAG 渲染（DependencyGraph） |
| `bpmn-js` | ^18.21.0 | BPMN 流程设计器内核 |
| `bpmn-js-properties-panel` | ^5.60.0 | BPMN 节点属性面板 |
| `camunda-bpmn-moddle` | ^7.0.1 | Camunda BPMN 扩展命名空间 |
| `diagram-js` | ^15.22.0 | bpmn-js 底层图编辑框架 |
| `monaco-editor` | ^0.55.1 | 代码编辑器（CodeEditor Widget） |
| `@guolao/vue-monaco-editor` | ^1.6.0 | Monaco 的 Vue 包装 |
| `echarts` | ^6.1.0 | 图表预览 / 报表 / Dashboard |
| `xlsx` | ^0.18.5 | Excel 导入导出（ExcelImportExport 组件） |
| `dompurify` | ^3.4.11 | 富文本 XSS 净化 |
| `jsqr` | ^1.4.0 | 二维码扫描（BarcodeInput Widget） |
| `jsondiffpatch` | ^0.7.6 | JSON Tree Diff（JsonTreeDiff 组件 / 版本对比） |
| `tslib` | ^2.8.1 | TS 运行时辅助 |

### 开发时依赖（devDependencies，节选）

| 依赖 | 版本 | 用途 |
|------|------|------|
| `vite` | ^8.1.1 | 构建器 |
| `@vitejs/plugin-vue` | ^6.0.7 | Vue SFC 编译 |
| `typescript` | ~6.0.2 | TypeScript 编译器 |
| `vue-tsc` | ^3.3.5 | Vue + TS 类型检查（`typecheck` 与 `build`） |
| `@vue/tsconfig` | ^0.9.1 | Vue 项目 TS 配置基线 |
| `vitest` | ^4.1.9 | 单元测试框架 |
| `@vue/test-utils` | ^2.4.11 | Vue 组件测试工具 |
| `jsdom` | ^29.1.1 | DOM 模拟环境 |
| `@playwright/test` | ^1.49.0 | E2E 测试 |
| `eslint` | ^9.14.0 | Lint 主程序 |
| `eslint-plugin-vue` | ^9.30.0 | Vue 模板 Lint |
| `typescript-eslint` | ^8.13.0 | TS Lint 规则 |
| `sass` | ^1.101.0 | SCSS 编译 |
| `@types/node` | ^24.13.2 | Node 类型 |

## 页面模块划分

路由配置位于 `src/router/index.ts`，按顶级路径分组。下表列出 `src/views/` 下的核心页面目录及其职责。

| 页面目录（`src/views/`） | 路由前缀 | 功能描述 | 对应后端模块 |
|--------------------------|----------|----------|--------------|
| `dashboard/` | `/dashboard` | 首页 / 工作台，统计看板 | `pms-dashboard` |
| `login/` | `/login` | 登录页（含 `__tests__/index.test.ts`） | `pms-auth` |
| `help/` | `/help` | 帮助中心（无需登录） | `pms-help` |
| `project/list/` | `/project/list` | 项目列表，进入项目工作区入口 | `pms-project` |
| `project/workspace/` | `/project/workspace/:id` | **项目工作区枢纽页**：8 Tab（概览 / 阶段 / 任务 / 交付件 / 基线 / 审批 / 成员 / 配置） | `pms-project` + 多模块聚合 |
| `project/detail/` | `/project/detail/:id` | 兼容旧路径，内部 `router.replace` 到工作区 | `pms-project` |
| `project/overview/` | — | 项目概览子组件（被工作区 Tab 引用） | `pms-project` |
| `project/gantt/` | `/project/:id/gantt` | 项目甘特图（ProjectGantt 组件） | `pms-implementation` |
| `project/kanban/` | `/project/kanban` | 交付看板（按状态分组） | `pms-project` |
| `project/tree/` | `/project/tree` | 主子项目树（SubProjectTree） | `pms-project` |
| `project/todo/` | `/project/:id/todo` | 项目待办 | `pms-workflow` |
| `project/template/` | `/project/template` | 项目模板列表 / 编辑表单 / 版本管理 | `pms-project-template` |
| `project-config/` | `/project/config/:id` | 项目配置（工作区 Tab 引用） | `pms-project-config` |
| `project-member/` | — | 项目成员（工作区 Tab 引用） | `pms-project-member` |
| `phase/` | `/project/phase/:projectId` | 阶段管理 + 阶段退出闸门（PhaseExitGateEditor） | `pms-project-phase` |
| `task/list/` | `/implementation/task/list` | 任务树列表（TaskTree 递归渲染） | `pms-implementation` |
| `task/detail/` | `/implementation/task/detail/:id` | 任务详情 | `pms-implementation` |
| `task/dependency/` | `/implementation/task/dependency/:projectId` | 任务依赖关系图（AntV G6 DAG） | `pms-task-dependency` |
| `implementation/task/` | `/implementation/task` | 实施任务 | `pms-implementation` |
| `implementation/agent/` | `/implementation/agent` | 服务商管理 | `pms-implementation-agent` |
| `implementation/settlement/` | `/implementation/settlement` | 结算管理 | `pms-implementation-settlement` |
| `asset/category/` | `/asset/category` | 设备分类树 | `pms-asset-category` |
| `asset/model/` | `/asset/model` | 设备型号 | `pms-asset-model` |
| `asset/list/` | `/asset/list` | 资产清单 | `pms-asset` |
| `baseline/` | `/baseline/list` | 计划基线管理 | `pms-baseline` |
| `baseline/diff.vue` | `/baseline/diff/:baselineId` | 基线偏差分析（BaselineDiffTable） | `pms-baseline` |
| `workflow/todo/` | `/workflow/todo` | 待办中心 | `pms-workflow` |
| `workflow/approval-center/` | `/workflow/approval-center` | 统一审批中心 | `pms-approval-center` |
| `workflow/approval-detail/` | `/workflow/approval-detail/:id` | 审批详情（ApprovalTimeline + SensitiveFieldDisplay） | `pms-approval-center` |
| `workflow/approval-history/` | `/workflow/approval-history/:recordId` | 审批历史（多轮次） | `pms-approval-center` |
| `workflow/field-perm/` | `/workflow/field-perm` | 字段权限配置 | `pms-approval-field-perm` |
| `deliverable/` | `/deliverable` | 终验交付物 | `pms-deliverable` |
| `deliverable/lifecycle.vue` | `/deliverable/lifecycle` | 交付件全生命周期（7 态状态机） | `pms-deliverable` |
| `deliverable/detail/` | `/deliverable/detail/:id` | 交付件详情（DeliverableStatusFlow + DeliverableVersionList） | `pms-deliverable` |
| `punch-list/` | `/punch-list` | Punch List（交付遗留问题） | `pms-punch-list` |
| `rma/` | `/rma` | RMA 返修工单 | `pms-rma` |
| `warranty/` | `/warranty` | 质保期管理 | `pms-warranty` |
| `risk/` | `/risk` | 风险登记册 | `pms-governance-risk` |
| `change-request/` | `/change-request` | 变更管理 | `pms-change-request` |
| `issue/` | `/issue` | 问题日志 | `pms-governance-issue` |
| `report/` | `/report` | 报表统计（ECharts） | `pms-report` |
| `notification/` | `/notification` | 消息中心 | `pms-notification` |
| `integration-health/` | `/integration-health` | 集成健康检查（D365/FP/OA） | `pms-integration-health` |
| `system/user/` `/role/` `/menu/` `/dict/` `/cache/` `/schedule/` `/audit/` | `/system/*` | 系统管理：用户 / 角色 / 菜单 / 字典 / 缓存 / 定时任务 / 审计日志 | `pms-system-*` |
| `system-status/` | `/system-status` | 系统状态 | `pms-system-status` |
| `changelog/` | `/changelog` | 版本日志 | — |
| `lowcode/entity-designer/` | `/lowcode/entity-designer` | 实体设计器（X6 + ER 关系） | `pms-lowcode-entity` |
| `lowcode/form-list/` `form-designer/` | `/lowcode/form-list` `/lowcode/form-designer` | 表单配置 / 表单设计器 | `pms-lowcode-form` |
| `lowcode/list-list/` `list-designer/` | `/lowcode/list-list` `/lowcode/list-designer` | 列表配置 / 列表设计器 | `pms-lowcode-list` |
| `lowcode/tab-list/` `tab-designer/` | `/lowcode/tab-list` `/lowcode/tab-designer` | 标签页配置 / 设计器 | `pms-lowcode-tab` |
| `lowcode/related-page-list/` `related-page-designer/` | `/lowcode/related-page-list` `/lowcode/related-page-designer` | 关联页配置 / 设计器 | `pms-lowcode-related-page` |
| `lowcode/microflow-designer/` | `/lowcode/microflow-designer` | 微流设计器（X6 + 节点执行高亮） | `pms-lowcode-microflow` |
| `lowcode/rule-designer/` | `/lowcode/rule-designer` | 规则设计器（决策表 / 表达式） | `pms-lowcode-rule` |
| `lowcode/process-designer/` | `/lowcode/process-designer` | BPMN 流程设计器（bpmn-js） | `pms-lowcode-process` |
| `lowcode/trigger-list/` | `/lowcode/trigger-list` | 触发器（CRUD / Quartz / Event） | `pms-lowcode-trigger` |
| `lowcode/connector-designer/` | `/lowcode/connector-designer` | 连接器配置（向导式 + OpenAPI 导入） | `pms-lowcode-connector` |
| `lowcode/publish-center/` | `/lowcode/publish-center` | 发布中心（含 GrayReleaseManager 灰度） | `pms-lowcode-publish` |
| `lowcode/approval-chain/` | `/lowcode/approval-chain` | 审批链配置 | `pms-lowcode-approval-chain` |
| `lowcode/version-history/` | `/lowcode/version-history` | 版本历史（PromotionPipeline / RollbackPreview / ImportConflict） | `pms-lowcode-version` |
| `lowcode/template-market/` | `/lowcode/template-market` | 模板市场（DetailDialog） | `pms-lowcode-template` |
| `lowcode/apm-dashboard/` | `/lowcode/apm-dashboard` | APM 看板（微流 / 规则运行监控） | `pms-lowcode-apm` |
| `lowcode/app-source-export/` | `/lowcode/app-source-export` | 应用源码导出 | `pms-lowcode-app-source` |
| `lowcode/preview/` `/render/` | `/lowcode/preview` `/lowcode/:pageType/:pageCode` | 低代码页面预览 / 运行时渲染 | `pms-lowcode-render` |

> 备注：路由表底部 `/:pathMatch(.*)*` 兜底重定向到 `/dashboard`。

## API 封装层

所有 API 文件位于 `src/api/`，统一通过 `src/utils/request.ts` 暴露的 `get / post / put / del` 四个泛型 helper 调用。响应拦截器已剥离外层 `{ code, message, data }` 信封，所以 helper 返回的 `Promise<T>` 即对应后端 `Result.data`。

### 请求基础设施（`src/utils/request.ts`）

- `service`：Axios 实例，`baseURL = ''`（依赖 Vite dev-server 代理 `/api → http://localhost:8080`），`timeout = 30000`。
- **JWT 注入**：从 `localStorage.getItem('pms_token')` 读取，写入 `Authorization: Bearer <token>`。
- **幂等键**：写操作（POST/PUT/DELETE/PATCH）自动注入 `X-Idempotent-Key`（UUID v4，优先 `crypto.randomUUID()`，降级到手动 RFC 4122 v4），与后端 `IdempotentAspect` 配合。
- **数据集成校验**：写操作请求体先经 `validators/registry` 查找 `requestValidator`，校验失败 `ElMessage.error` + reject；通过后用 normalize 后的数据替换请求体。响应可选 `responseValidator` 做白名单过滤。
- **统一错误处理**：`code === 401` 清 token 跳登录；其它业务错误 `ElMessage.error`；`silent: true` 配置项可静默错误。
- **`skipValidate: true`**：单次请求跳过校验（如 `multipart/form-data` 上传）。
- **导出**：`get<T>` / `post<T>` / `put<T>` / `del<T>` / 默认 `service` / `TOKEN_KEY` / `IDEMPOTENT_KEY_HEADER` / `VALIDATOR_ENABLED` / `ApiResult`。

### API 文件清单（55 个，按业务分组）

| 分组 | 文件 | 主要端点 / 职责 |
|------|------|-----------------|
| 认证 | `auth.ts` | `POST /api/auth/login`、`POST /api/auth/logout`、`GET /api/auth/info`；导出 `LoginParams / UserInfo / LoginResult` |
| 项目核心 | `project.ts` | 项目 CRUD / 列表 / 看板 / 关闭 / 取消 / 进度，字段名与后端 `Project.java` 严格对齐（projectCode / projectName / projectType 等） |
| 项目阶段 | `project-phase.ts` | `GET /api/project/phase/project/{id}` 等，阶段推进 |
| 项目模板 | `project-template.ts` | 模板 CRUD / 版本发布 |
| 项目配置 | `project-config.ts` | 项目级配置 |
| 项目成员 | `project-member.ts` | 项目成员管理 |
| 实施 | `implementation.ts` | ImplTask CRUD / 任务树（taskPath/depth） / OEM 分配 / 优先级；`TaskStatus` 8 态 |
| 任务关联 | `task-activity.ts` `task-checklist.ts` `task-comment.ts` `task-dependency.ts` | 任务活动 / 清单 / 评论 / 依赖 |
| 资产 | `asset.ts` | Category 树 / Model 分页 / Asset 清单 |
| 基线 | `baseline.ts` | `BaselineStatus` 三态、`TaskPlanSnapshot`、偏差分析 |
| 交付件 | `deliverable.ts` | 7 态状态机 + 版本 + 签名 + 引用 + 阶段退出校验 + 字典兜底（详见「工具类与字典机制」） |
| 工作流 | `workflow.ts` | BPMN 部署 / 启动 / 任务 / 历史 |
| 审批中心 | `approval-center.ts` | 统一审批（10 种 ApprovalType / 5 种 ApprovalStatus） |
| 字段权限 | `approval-field-perm.ts` | 字段脱敏 / 隐藏规则 |
| 治理 | `risk.ts` `issue.ts` `change-request.ts` `punch-list.ts` | 风险 / 问题 / 变更 / Punch List |
| 售后 | `rma.ts` `warranty.ts` | RMA 工单 / 质保期 / SLA |
| 报表 | `report.ts` | 报表数据 |
| 通知 | `notification.ts` | 站内通知 |
| 集成 | `integration-health.ts` | D365/FP/OA 健康状态 + 推送日志 |
| 系统 | `system.ts` | 用户 / 角色 / 菜单 / 字典 CRUD + `getDictItems(dictType)` + `searchUsers`（@提及补全） |
| 系统监控 | `system-audit.ts` `system-cache.ts` `system-schedule.ts` | 审计日志 / 缓存 / 定时任务 |
| Excel | `excel.ts` | `triggerBlobDownload` 通用下载工具 |
| 附件 | `attachment.ts` | 文件上传 / 下载 |
| 反馈 | `feedback.ts` | 用户反馈 |
| 帮助 | `help.ts` | 帮助中心内容 |
| 低代码（19 个） | `lowcode.ts` `lowcode-entity.ts` `lowcode-microflow.ts` `lowcode-rule.ts` `lowcode-process.ts` `lowcode-trigger.ts` `lowcode-connector.ts` `lowcode-publish.ts` `lowcode-approval-chain.ts` `lowcode-version.ts` `lowcode-template.ts` `lowcode-edit-lock.ts` `lowcode-collaboration.ts` `lowcode-comment.ts` `lowcode-gray-release.ts` `lowcode-apm.ts` `lowcode-app-source.ts` `lowcode-component-meta.ts` | 表单 / 列表 / Tab / 关联页配置 / 实体 / 微流 / 规则 / 流程 / 触发器 / 连接器 / 发布 / 灰度 / 审批链 / 版本 / 模板 / 编辑锁 / 协作 / 评论 / APM / 应用源 / 组件元数据 |

### API 风格约定

- **字段名严格对齐后端实体**：例如 `Project` 接口字段使用 `projectCode / projectName / projectType / projectManagerName`（长前缀），而非短名。`@/validators/project.ts` 中的 `projectFieldMapping` 已置为空对象，不再做字段名映射。
- **分页响应统一**：`PageResult<T> = { records, total, page, size }` 或 MyBatis Plus 风格 `IPage<T> = { records, total, current, size, pages }`（低代码模块用 `current/size`）。
- **枚举常量集中导出**：如 `DELIVERABLE_STATUS_LABELS`、`DELIVERABLE_STATUS_ORDER`、`DELIVERABLE_TYPE_LABELS`、`DELIVERABLE_REF_ENTITY_TYPE_LABELS`。
- **历史接口标记 `@deprecated`**：如 `listDeliverables` / `initChecklist` / `markUploaded` 等保留用于历史兼容，新代码改用 `listFullDeliverables` 与状态机流转。

## 路由配置

路由定义在 `src/router/index.ts`，使用 `createWebHistory()` + `scrollBehavior: () => ({ left: 0, top: 0 })`。

### 路由组织

- **顶层路由**：`/login`、`/help`（无需登录）；其余业务路由均以 `Layout = DefaultLayout.vue` 为父组件，`requiresAuth: true`。
- **嵌套分组**：`/project`、`/asset`、`/implementation`、`/baseline`、`/workflow`、`/lowcode`、`/system` 各自独立分组；其他业务（punch-list / rma / warranty / deliverable / risk / change-request / issue / report / notification / integration-health）和 system-status / changelog 保留平铺。
- **懒加载**：所有页面组件均 `() => import('@/views/...')`，按需打包。
- **`meta` 字段**：`title`（页面标题，用于浏览器 tab）、`icon`（菜单图标名）、`hidden`（不在菜单显示）、`requiresAuth`、`perms`（单权限码或数组，任一满足即可）、`transitionName`（路由切换动画，默认 `fade-slide-up`）。

### 路由守卫（`router.beforeEach`）

1. `startRouteLoading()`：触发顶部进度条（200ms 延迟显示，避免闪烁）。
2. 设置 `document.title`：`${title} - 网络设备工程项目管理系统`。
3. `requiresAuth === false` 直接放行；若已登录访问 `/login` 则跳 `/dashboard`。
4. 无 token 跳 `/login?redirect=...`。
5. **token 恢复用户信息**：`localStorage` 仅持久化 token，刷新页面后若 `userInfo` 为空，调用 `userStore.fetchUserInfo()` 拉取用户与权限码。401/403 重置跳登录；其它错误放行（避免临时故障丢权限）。
6. **低代码权限校验**：`requiredLowCodePermissions(path)` 优先匹配运行时低代码页面 `/lowcode/(form|list|tab|related-page)/:code` → `lowcode:page:{type}:{code}`，否则查 `LOWCODE_ROUTE_PERMISSIONS` 静态表。
7. **`meta.perms` 校验**：与菜单 `canAccessMenu` 逻辑对齐，单字符串或数组。
8. `router.afterEach` → `stopRouteLoading()`。

### 低代码路由权限映射（`LOWCODE_ROUTE_PERMISSIONS`）

| 路径前缀 | 所需权限（任一） |
|----------|------------------|
| `/lowcode/entity-designer` | `lowcode:entity:list` |
| `/lowcode/form-designer` | `lowcode:form:edit`、`lowcode:form:add` |
| `/lowcode/form-list` | `lowcode:form:list` |
| `/lowcode/list-designer` | `lowcode:list:edit`、`lowcode:list:add` |
| `/lowcode/list-list` | `lowcode:list:list` |
| `/lowcode/tab-designer`、`/lowcode/tab-list` | `lowcode:tab:edit`、`lowcode:tab:add` |
| `/lowcode/related-page-designer`、`/lowcode/related-page-list` | `lowcode:relatedPage:edit`、`lowcode:relatedPage:add` |
| `/lowcode/microflow-designer` | `lowcode:microflow:list` |
| `/lowcode/rule-designer` | `lowcode:rule:list` |
| `/lowcode/process-designer` | `lowcode:process:list` |
| `/lowcode/trigger-list` | `lowcode:trigger:list` |
| `/lowcode/connector-designer` | `lowcode:connector:list` |
| `/lowcode/publish-center` | `lowcode:publish:list` |
| `/lowcode/approval-chain` | `lowcode:approval-chain:list` |
| `/lowcode/version-history` | `lowcode:version:list` |
| `/lowcode/template-market` | `lowcode:template:list` |
| `/lowcode/apm-dashboard` | `lowcode:microflow:list`、`lowcode:rule:list` |
| `/lowcode/app-source-export` | `lowcode:app-source:export` |

## 状态管理（Pinia stores）

所有 store 位于 `src/stores/`，使用 Composition API 风格（`defineStore('name', () => { ... })`）。

### `user.ts`（`useUserStore`）

- **state**：`token`（启动时从 `localStorage.pms_token` 恢复）、`userInfo`（`UserInfo | null`）、`permissions: string[]`。
- **getters / actions**：
  - `hasPermission(code)`：超级管理员（`permissions` 含 `*`）或精确匹配。
  - `hasAnyPermission(codes)`：任一满足。
  - `login(params)`：调 `loginApi`，写 token + userInfo + permissions。
  - `fetchUserInfo()`：刷新页面后恢复用户信息与权限码。
  - `logout()`：调 `logoutApi`（错误忽略）→ `reset()` → `router.push('/login')`。
  - `reset()`：清空所有 state + 移除 `localStorage.pms_token`。

### `app.ts`（`useAppStore`）

- `sidebarCollapsed`：从 `localStorage.pms_sidebar_collapsed` 恢复。
- `toggleSidebar()`：切换并持久化。

### `tags.ts`（`useTagsStore`）

- `visitedViews: View[]`：已访问路由标签，从 `localStorage.pms_tags_view` 恢复；首页 `/dashboard` 始终固定（`affix: true`）。
- **actions**：`addView` / `delView`（固定标签不可删）/ `delOthers` / `delLeft` / `delRight` / `delAll` / `moveView`（拖拽排序，固定标签位置不变）。
- `View` 接口：`path / title / name / fullPath / affix`。
- 配套组件：`src/components/TagsView/index.vue`。

### `websocket.ts`（`useWebSocketStore`）

- **state**：`connected`、`ws: WebSocket | null`、`reconnectTimer`、`unreadCount`。
- `connect()`：拼接 `ws(s)://{host}:8080/ws?token=...`，`onmessage` 解析通知 JSON → `ElNotification` + `unreadCount++`，`onClick` 跳 `/notification`。
- `scheduleReconnect()`：5 秒重连。
- `disconnect()`：清 timer + 置空 `onclose` + close。
- `resetUnread()`：清零未读数。
- **不依赖** sockjs/stompjs，纯原生 WebSocket。

## 公共组件

组件目录位于 `src/components/`，下表按子目录归类。

### 业务关键组件（顶层）

| 组件 | 文件 | 用途 |
|------|------|------|
| `DeliverableRefEntitySelector` | `DeliverableRefEntitySelector.vue` | 交付件引用实体选择器（ENTITY_REF 类型），双 `el-select`（实体类型 + 实体），双向绑定 `refEntityType / refEntityId`，支持「新建实体」跳转到对应模块（TASK→/implementation/task、ASSET→/asset 等），加载 `loadDeliverableRefEntityTypes` 字典 + `listReferencedEntities` 实体列表 |
| `DeliverableStatusFlow` | `DeliverableStatusFlow.vue` | 7 态交付件状态流可视化（DRAFT→SUBMITTED→REVIEWED→SIGNED→PUBLISHED→REFERENCED→ARCHIVED），横向节点 + 箭头，当前态高亮、已通过 ✓、未到达灰色序号；支持 `compact` 与 `showLabels` |
| `DeliverableVersionList` | `DeliverableVersionList.vue` | 交付件版本历史列表（不可变记录），按版本号倒序，状态标签（DRAFT=info / SUBMITTED=warning / REVIEWED=primary / SIGNED/PUBLISHED/REFERENCED=success / ARCHIVED=danger），支持下载与变更说明，emit `loaded / download` |
| `ApprovalTimeline` | `ApprovalTimeline.vue` | 审批时间轴（Story 6），按 `round` 分组渲染多轮次审批历史 |
| `BaselineDiffTable` | `BaselineDiffTable.vue` | 基线偏差对比表，逐任务展示基线 vs 当前计划，延迟红色 / 提前绿色高亮 |
| `DependencyGraph` | `DependencyGraph.vue` | 任务依赖关系图（AntV G6 v5 DAG，dagre LR 布局），支持循环依赖高亮 |
| `PhaseExitGateEditor` | `PhaseExitGateEditor.vue` | 阶段退出闸门编辑器（与 `validateMandatoryDeliverables` 联动） |
| `ProjectTemplateSelector` | `ProjectTemplateSelector.vue` | 项目模板选择器（从模板创建子项目） |
| `SensitiveFieldDisplay` | `SensitiveFieldDisplay.vue` | 敏感字段脱敏展示（Story 6），MASKED 字段后显示 ⓘ 提示脱敏规则 |
| `SubProjectTree` | `SubProjectTree.vue` | 主子项目树递归渲染，emit `node-click` |
| `TaskChecklist` | `TaskChecklist.vue` | 任务清单 |
| `TaskTree` | `TaskTree.vue` | 任务树递归渲染（基于 `ImplTaskNode`，含 `taskPath/depth`） |

### `common/` 通用展示组件

| 组件 | 用途 |
|------|------|
| `PageHeader.vue` | 页面统一头部：breadcrumb + title + description + actions 插槽，下边框分隔 |
| `SkeletonCard.vue` | 骨架卡片（基于 `el-skeleton`），`loading / rows / padding` props |
| `EmptyState.vue` | 空状态：大图标 + 标题 + 描述 + action 插槽 |
| `DeliverableStatusBadge.vue` | 交付件状态徽章 |
| `PhaseStatusTag.vue` | 阶段状态标签 |
| `ProjectStatusTag.vue` | 项目状态标签 |
| `TaskPriorityTag.vue` | 任务优先级标签 |
| `UserSelect.vue` | 用户选择器（对接 `searchUsers`） |

### `layout/`

| 组件 | 用途 |
|------|------|
| `SidebarMenu.vue` | 递归侧栏菜单组件，消费 `config/menu.ts` 输出的可见菜单 |

### `project/`

| 组件 | 用途 |
|------|------|
| `ProjectGantt.vue` | 项目甘特图 |
| `ProjectTreeSidebar.vue` | 项目树侧栏（已弃用，导航改为列表入口） |

### `workflow/`

| 组件 | 用途 |
|------|------|
| `GlobalTodoCenter.vue` | 全局待办中心浮层 |

### 低代码设计器组件

| 组件目录 | 用途 |
|----------|------|
| `EntityDesigner/` | 实体设计器：`EntityNode`（X6 Vue 节点，渲染实体名 + 表名 + 字段列表，PK 标红）、`FieldPanel`、`IndexPanel`、`RelationConfigDialog` |
| `ConnectorDesigner/` | 连接器向导：`StepBasicInfo / StepAuth / StepOperations / StepPagination / StepResponseMapping / StepRetry / TestConsole / OpenApiImporter / JsonTree` |
| `MicroflowDesigner/` | 微流设计器：`MicroflowNode`（X6 节点 + 执行状态高亮）、`NodePalette`、`NodeParamPanel`、`MicroflowMetaPanel`、`ExecutionLogPanel`、`VariablePanel` |
| `ProcessDesigner/` | BPMN 流程设计器：`BpmnCanvas`（封装 bpmn-js Modeler，expose `newDiagram/importXml/exportXml/zoomToFit/getModeler`）、`BpmnPalette`、`LowCodeBpmnProperties`、`NodeFormBindingPanel`、`ProcessPreview`、`bpmn-helper.ts` |
| `RuleDesigner/` | 规则设计器：`DecisionTableEditor`、`ExpressionRuleEditor`、`RuleTestPanel` |
| `TriggerDesigner/` | 触发器配置：`CrudTriggerConfig`、`EventTriggerConfig`、`QuartzTriggerConfig` |
| `LowCodeFormRenderer/` | 表单运行时渲染器 |
| `LowCodeListRenderer/` | 列表运行时渲染器 |
| `LowCodeTabRenderer/` | Tab 页运行时渲染器 |
| `LowCodeRelatedPageRenderer/` | 关联页运行时渲染器 |
| `LowCodeMultiDeviceRenderer/` | 多设备渲染（含 `MobileCardList.vue`） |
| `LowCodePropertyPanel/` | 属性面板（`PropField` + `index.vue`） |
| `LowCodeComponentRegistry/` | 组件注册中心（`register/get/list/has` + `initBuiltinComponents` + 内置 15 个 Widget 元数据兜底） |

### `LowCodeWidgets/` 内置 Widget（15 个）

| Widget | 用途 |
|--------|------|
| `AddressPicker.vue` | 地址选择（省市区） |
| `BarcodeInput.vue` | 条码扫描（jsqr） |
| `ChartPreview.vue` | 图表预览（ECharts） |
| `CodeEditor.vue` | 代码编辑器（Monaco） |
| `ColorPicker.vue` | 颜色选择器 |
| `DateRangePicker.vue` | 日期范围 |
| `DeptSelector.vue` | 部门选择器 |
| `DictSelect.vue` | 数据字典下拉（直接调用 `getDictItems(dictCode)`） |
| `FileUploader.vue` | 文件上传 |
| `NumberRangeInput.vue` | 数字范围 |
| `QrcodeDisplay.vue` | 二维码展示 |
| `RichTextEditor.vue` | 富文本编辑器（DOMPurify 净化） |
| `SignaturePad.vue` | 电子签名 |
| `TreeSelect.vue` | 树形选择 |
| `UserSelector.vue` | 用户选择器 |

### 其他公共组件

| 组件 | 用途 |
|------|------|
| `CommentPanel/` | 评论面板（对接 `lowcode-comment` API） |
| `ComponentSandbox/` | 组件沙箱（`guest-runtime.ts` + `protocol.ts`，隔离加载第三方组件） |
| `CronEditor/` | Cron 表达式编辑器 |
| `ExcelImportExport/` | Excel 导入导出（xlsx） |
| `ExpressionEditor/` | 表达式编辑器（`bindings.ts` 提供上下文绑定） |
| `FeedbackButton/` | 反馈按钮（对接 `feedback` API） |
| `FileUploader/` | 通用文件上传 |
| `HelpBubble/` | 帮助气泡 |
| `JsonTreeDiff/` | JSON 树形差异对比（jsondiffpatch） |
| `MobileComponents/` | 移动端组件：`BottomSheet`、`MobileDrawer`、`MobileScanner`、`SwipeActions` |
| `MobileListCard/` | 移动端列表卡片 |
| `NotificationBell/` | 通知铃铛（顶部，对接 `websocket` store） |
| `OnlineUsersIndicator/` | 在线用户指示器 |
| `TagsView/` | 标签视图（多标签页，对接 `tags` store） |
| `UserGuide/` | 用户引导 |

## 工具类与字典机制

### 请求工具（`src/utils/request.ts`）

详见「API 封装层 → 请求基础设施」。核心导出：
- `service`（默认 axios 实例）
- `get<T> / post<T> / put<T> / del<T>`（剥离信封后的泛型 helper）
- `TOKEN_KEY = 'pms_token'`
- `IDEMPOTENT_KEY_HEADER = 'X-Idempotent-Key'`
- `VALIDATOR_ENABLED`（默认 true）
- `ApiResult<T>` 接口

### 数据集成校验对象（`src/validators/`）

设计目标：作为前端 ↔ 后端数据交互的统一守门员，作为后端 Jakarta Validation 注解（`@NotBlank/@Size/@Min/@Max` 等）的前端镜像。

- **`registry.ts`**：Validator 注册中心，维护 `URL 模式 → validator` 映射。支持精确 URL 与正则 URL 匹配；同一 URL 可分别注册 `requestValidator` 和 `responseValidator`；`findValidators` 从后向前匹配（后注册优先）。
- **`index.ts`**：核心类型与链式 API：`FieldSpec`（type/required/maxLen/min/max/enum/pattern/schema/items）、`Schema`、`ValidationResult<T>`、`ValidationError`、`defineSchema`、`validate`、`required / string / number / boolean / optional / maxLen / range / enumOf` 等规则构造函数、`formatErrors`。
- **业务 validator（30+ 文件）**：`project / impl-task / milestone / punch-list / deliverable / change-request / risk / issue / asset / asset-model / asset-category / asset-transfer / warranty / rma / impl-progress / task-comment / task-checklist / task-dependency / settlement / agent / agent-score / workflow / approval-field-perm / sys-user / sys-role / sys-menu / sys-dept / sys-dict / sys-dict-item / sys-config / feedback / help-content / notification`。
- **集成方式**：`request.ts` 顶部 `import '@/validators/xxx'` 触发各模块自注册；`VALIDATOR_ENABLED = true` 时写操作请求体先校验，失败 `ElMessage.error` + reject；通过则用 normalize 数据替换请求体。响应校验失败仅 `console.warn`，不阻断流程。

### 字典驱动的前端机制（核心：交付件类型兜底）

`src/api/deliverable.ts` 实现了「字典驱动 + 兜底常量」的标准模式，是全前端字典机制的范例：

```ts
export const DELIVERABLE_TYPE_DICT = 'pms_deliverable_type'
export const DELIVERABLE_REF_ENTITY_TYPE_DICT = 'pms_deliverable_ref_entity_type'

// 兜底常量（字典未就绪时使用）
export const DELIVERABLE_TYPE_LABELS: Record<string, string> = {
  DOCUMENT: '文档', CODE: '代码', ENTITY_REF: '实体引用',
  MODEL: '模型', CONFIG: '配置', DATA: '数据', OTHER: '其他'
}
export const DELIVERABLE_REF_ENTITY_TYPE_LABELS: Record<string, string> = {
  TASK: '任务', ASSET: '资产', PHASE: '阶段',
  PROJECT: '项目', DELIVERABLE: '交付件', REPORT: '报告'
}

let deliverableTypeItems: SysDictItem[] | null = null

export async function loadDeliverableTypes(): Promise<SysDictItem[]> {
  if (deliverableTypeItems) return deliverableTypeItems            // 模块级缓存
  try {
    const items = await getDictItems(DELIVERABLE_TYPE_DICT)        // GET /api/system/dict/items/pms_deliverable_type
    deliverableTypeItems = items.length > 0 ? items : toFallbackItems(DELIVERABLE_TYPE_LABELS)
  } catch {
    deliverableTypeItems = toFallbackItems(DELIVERABLE_TYPE_LABELS) // 异常降级到兜底常量
  }
  return deliverableTypeItems
}

export function translateDeliverableType(value?: string): string {
  if (!value) return '-'
  if (deliverableTypeItems) {
    const item = deliverableTypeItems.find((it) => it.itemValue === value)
    if (item) return item.itemText
  }
  return DELIVERABLE_TYPE_LABELS[value] ?? value                  // 最终兜底
}
```

**机制要点**：
1. **字典编码常量**：`DELIVERABLE_TYPE_DICT` / `DELIVERABLE_REF_ENTITY_TYPE_DICT` 集中定义。
2. **模块级缓存**：`deliverableTypeItems` / `deliverableRefEntityTypeItems` 首次加载后缓存，避免重复请求。
3. **兜底常量**：`DELIVERABLE_TYPE_LABELS` 等保留作为字典未就绪时的 fallback，通过 `toFallbackItems` 转成 `SysDictItem[]` 统一结构。
4. **翻译函数**：`translateDeliverableType(value)` 优先查字典缓存，找不到再查兜底常量，最后返回原值。
5. **降级策略**：字典接口异常时 `catch` 兜底，不阻断 UI。
6. **同样的模式**：`loadDeliverableRefEntityTypes` / `translateRefEntityType` / `DictSelect` Widget（直接调 `getDictItems(dictCode)`）。

### 其他工具

| 工具 | 文件 | 用途 |
|------|------|------|
| 自定义指令 | `directives/index.ts` | `v-debounce`（防抖点击，WeakMap 缓存 listener）、`v-permission`（无权限移除 DOM）；`registerDirectives(app)` 在 `main.ts` 注册 |
| 路由 loading | `directives/loading.ts` | `routeLoading` ref + `startRouteLoading`（200ms 延迟）/ `stopRouteLoading`，由路由守卫触发，DefaultLayout 顶部进度条消费 |
| 设计令牌 | `styles/design-tokens.scss` | 颜色（品牌 / 状态 / 中性）、字体、字号、字重、阴影、圆角等 SCSS 变量 + `:root` 输出 `--pms-*` CSS 自定义属性 |
| Element Plus 覆盖 | `styles/elements-overrides.scss` | Element Plus 样式定制 |
| 响应式 | `styles/responsive.scss` `styles/breakpoints.ts` | 移动端断点（768px） |
| 项目上下文 | `composables/useProjectContext.ts` | `provideProjectContext()` 在 DefaultLayout 提供，`useProjectContext()` 注入；持有 `currentProject / currentPhase / projectList` |
| 撤销重做 | `composables/useUndoRedo.ts` | 三段式历史（past/present/future），maxHistory 默认 50（借鉴 Power Apps Studio），深拷贝保护，可选键盘快捷键 |
| 编辑锁 | `composables/useEditLock.ts` | 调 `lowcode-edit-lock` API，acquire/renew（5 分钟心跳）/release |
| 防抖 | `composables/useDebounce.ts` | 通用防抖 |
| 协作 | `composables/useCollaboration.ts` | 多人协作（对接 `lowcode-collaboration`） |
| 键盘快捷键 | `composables/useKeyboardShortcut.ts` | 全局快捷键注册 |
| 首次登录 | `composables/useFirstLogin.ts` | 首次登录引导 |
| PWA | `composables/usePWA.ts` | Service Worker 注册 + 离线监听 + 更新检测（App.vue 消费） |

### 菜单配置（`src/config/menu.ts`）

- **数据结构**：`MenuLeaf`（title/path/icon/permissions/group）、`MenuGroup`（含 children）、`MenuItem` 联合类型。
- **顶级分组**（`group` 字段，对应顶栏一级 Tab）：`home / project / asset / implementation / governance / lowcode / system / report`。
- **`menuGroups`**：静态菜单数据源，覆盖项目管理、资产管理、实施管理、计划基线、工作流、交付治理、项目治理、系统监控、系统管理、报表统计、低代码、演示中心。
- **`lowCodeMenuPermissions`**：低代码静态菜单路径 → 权限码映射。
- **`canAccessMenu(item)`**：权限解析顺序 = 显式 `permissions` → 运行时低代码页面派生 `lowcode:page:{type}:{code}` → `lowCodeMenuPermissions` → 默认放行。
- **`useVisibleMenuGroups()`**：ComputedRef，按权限过滤后的可见菜单。
- **`useTopTabs()`**：顶栏一级 Tab 列表（去重 group + 显示名 + 图标 + defaultPath）。
- **`useMenuByGroup(getter)`**：按当前 Tab group 过滤侧栏二级菜单。
- **`inferActiveTabGroup(path)`**：根据路由推断当前 Tab group。

## 构建配置

### `vite.config.ts`（主应用）

```ts
export default defineConfig({
  plugins: [vue()],
  resolve: { alias: { '@': path.resolve(__dirname, 'src') } },
  optimizeDeps: { include: ['monaco-editor/esm/vs/editor/editor.worker'] },  // monaco worker 预打包
  server: {
    port: 3000,
    proxy: { '/api': { target: 'http://localhost:8080', changeOrigin: true } }
  }
})
```

要点：
- `@` 别名指向 `src`，全项目通过 `@/...` 引用。
- dev server 监听 3000 端口，`/api` 反向代理到后端 8080（与 `websocket.ts` 中硬编码的 8080 端口一致）。
- Monaco Editor worker 通过 `optimizeDeps.include` 预打包，避免 dev 模式 worker 加载报错。

### `vite.config.lib.ts`（低代码组件 SDK 库模式）

- 入口：`src/sdk/index.ts`。
- 产物：`dist-sdk/pms-lowcode-sdk.es.js`（ES Module）、`dist-sdk/pms-lowcode-sdk.umd.js`（UMD，全局变量 `PmsLowCodeSDK`）、`dist-sdk/pms-lowcode-sdk.css`。
- 外部化 peer dependencies：`vue / element-plus / @element-plus/icons-vue / monaco-editor`（不打包进 SDK，由宿主提供）。
- `cssCodeSplit: false`（CSS 内联）、`sourcemap: true`、`minify: false`（发布时手动开启）、`target: 'es2018'`。
- 构建命令：`npm run build:sdk`（`vite build --config vite.config.lib.ts`）。

### TypeScript 配置

- `tsconfig.json`：项目引用（references）聚合，无 `files`。
- `tsconfig.app.json`：继承 `@vue/tsconfig/tsconfig.dom.json`，`strict: true`、`noImplicitAny`、`strictNullChecks`、`noImplicitThis`、`alwaysStrict`、`noImplicitReturns`、`erasableSyntaxOnly`、`noFallthroughCasesInSwitch`；`noUnusedLocals / noUnusedParameters` 故意 false（交由 ESLint warn 级别覆盖）；`paths: { "@/*": ["./src/*"] }`；`types: ["vite/client"]`。
- `tsconfig.node.json`：Node 端配置（vite.config 等）。

### 测试配置

- **`vitest.config.ts`**：单元测试（Vitest 4 + jsdom + `@vue/test-utils`）。各模块 `__tests__/` 目录下有用例，如 `api/__tests__/project.test.ts`、`stores/__tests__/user.test.ts`、`components/__tests__/FeedbackButton.test.ts`、`composables/__tests__/useUndoRedo.test.ts`、`validators/__tests__/index.test.ts`。
- **`playwright.config.ts`**：E2E 测试（Playwright 1.49）。`baseURL: http://localhost:3000`，`testDir: ./e2e`，`testMatch: '**/*.spec.ts'`（与 vitest 的 `*.test.ts` 隔离），`webServer` 自动启动 `npm run dev`。用例：`dashboard.spec.ts / login.spec.ts / lowcode.spec.ts / project-list.spec.ts / project-lifecycle.test.ts`（后者实为 vitest 用例）。

### NPM Scripts

```json
{
  "dev": "vite",
  "build": "vue-tsc -b && vite build",
  "preview": "vite preview",
  "test": "vitest run",
  "test:watch": "vitest",
  "lint": "eslint . --max-warnings 0",
  "lint:fix": "eslint . --fix",
  "typecheck": "vue-tsc --noEmit",
  "build:sdk": "vite build --config vite.config.lib.ts"
}
```

## 关键技术点

### 1. 项目工作区枢纽页（`/project/workspace/:id`）

8 Tab 整合：概览 / 阶段 / 任务 / 交付件 / 基线 / 审批 / 成员 / 配置。通过组件内部动态组件加载（不占用独立路由）。进入时 `setProject` 同步 `useProjectContext`，离开时 `clearProject` 清理。顶部 `PageHeader` 展示项目名 + `ProjectStatusTag` + 关键操作（关闭 / 取消 / 从模板创建子项目）。旧路径 `/project/detail/:id` 兼容性跳转。

### 2. 交付件 7 态状态机

定义在 `api/deliverable.ts`：`DRAFT → SUBMITTED → REVIEWED → SIGNED → PUBLISHED → REFERENCED → ARCHIVED`。配套 API：`submitDeliverable / reviewDeliverable(passed) / signDeliverable / publishDeliverable / archiveDeliverable`。版本管理：`reviseDeliverable`（新建版本不覆盖旧版本，不可变历史）。阶段退出校验：`validateMandatoryDeliverables(phaseId)`。UI 侧：`DeliverableStatusFlow`（横向流可视化）+ `DeliverableStatusBadge` + `DeliverableVersionList`。

### 3. 字典驱动 + 兜底常量模式

`loadDeliverableTypes / translateDeliverableType` 等函数演示了前端字典消费的标准模式：模块级缓存 + 异常降级 + 兜底常量。`DictSelect` Widget 直接消费 `getDictItems(dictCode)`。后端字典端点：`GET /api/system/dict/items/{dictType}`。

### 4. 数据集成校验对象（前端镜像后端 @Valid）

`validators/registry.ts` + 30+ 业务 validator 文件，在 `request.ts` 拦截器中：
- 写操作请求体先校验，失败 reject，避免触发后端 400。
- 通过后用 normalize 数据替换请求体（字段白名单 + 旧字段名映射）。
- 响应可选校验，仅 warn 不阻断。
- 各业务模块在文件末尾 `registerValidator(...)` 自注册，`request.ts` 顶部 `import '@/validators/xxx'` 触发。

### 5. 幂等键机制

写操作自动注入 `X-Idempotent-Key`（UUID v4），与后端 `IdempotentAspect` 配合，防止用户重复点击导致重复提交。`generateIdempotentKey` 优先 `crypto.randomUUID()`，降级到手动 RFC 4122 v4，再降级到 `Date.now() + Math.random`。

### 6. 低代码组件 SDK 独立打包

`src/sdk/` 提供面向第三方组件开发者的公共入口（`defineLowCodeComponent / register / ComponentMeta / LowCodeProps / LowCodeContext` 等），借鉴 Power Apps PCF 与 ToolJet Component SDK。通过 `vite.config.lib.ts` 库模式打包，外部化 vue/element-plus/monaco-editor，输出 ES + UMD 双格式。`LowCodeComponentRegistry` 维护注册中心，`initBuiltinComponents` 异步批量注册 15 个内置 Widget 元数据（含本地 .vue 与后端 `/api/lowcode/component-meta` 合并）。

### 7. 三套图编辑技术栈

- **AntV X6**：微流设计器（`MicroflowNode` 通过 `@antv/x6-vue-shape` 注册 Vue 节点，支持执行状态高亮）、实体设计器（`EntityNode` 渲染实体 + 字段列表 + ER 关系）。
- **AntV G6 v5**：任务依赖关系 DAG（`DependencyGraph`，dagre LR 布局，支持循环依赖高亮）。
- **bpmn-js**：BPMN 流程设计器（`BpmnCanvas` 封装 BpmnModeler，expose `newDiagram/importXml/exportXml/zoomToFit/getModeler`）。

### 8. 路由权限三段式校验

路由守卫按顺序校验：① token 存在性 ② 低代码运行时派生权限 `/lowcode/(form|list|tab|related-page)/:code` → `lowcode:page:{type}:{code}` 或 `LOWCODE_ROUTE_PERMISSIONS` 静态表 ③ `meta.perms` 单权限码或数组。与菜单 `canAccessMenu` 逻辑对齐。超级管理员通过 `permissions` 含 `*` 全量放行。

### 9. PWA + 离线 + WebSocket

- `composables/usePWA.ts`：Service Worker 注册 + 离线监听 + 更新检测，`App.vue` 监听 `needRefresh` 弹 `ElMessageBox` 提示刷新。
- `stores/websocket.ts`：原生 WebSocket（不依赖 sockjs/stompjs），自动 5 秒重连，收到通知弹 `ElNotification` + `unreadCount++`，点击跳 `/notification`。
- `public/sw.js`、`public/manifest.webmanifest`、`public/offline.html`：PWA 资源。

### 10. 钉飞风格路由切换动画

`App.vue` 内联全局过渡：`fade-slide-up`（默认，垂直淡入，飞书风格）、`slide-fade-x`（水平滑动，钉钉风格，Tab 切换）、`fade-scale`（缩放淡入，弹窗）。统一缓动 `cubic-bezier(0.4, 0, 0.2, 1)`，时长 200~250ms。

### 11. 多标签页 + 拖拽排序

`stores/tags.ts` 维护 `visitedViews`，持久化到 `localStorage.pms_tags_view`，首页固定（`affix: true`）。`TagsView/index.vue` 组件消费，支持关闭其他 / 关闭左侧 / 关闭右侧 / 关闭全部 / 拖拽排序（固定标签位置不变）。

### 12. 顶栏一级 Tab + 侧栏二级菜单联动

`DefaultLayout.vue` 消费 `config/menu.ts` 的 `useVisibleMenuGroups / useTopTabs / useMenuByGroup / inferActiveTabGroup`，实现钉钉/飞书风格的「顶栏一级 Tab + 侧栏二级菜单」分级渲染。路由变化时 `activeTabGroup` 自动同步推断。移动端 768px 断点切换为抽屉式侧栏。
