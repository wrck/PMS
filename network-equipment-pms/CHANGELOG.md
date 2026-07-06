# Changelog

本项目所有重要变更均会记录在本文件中。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/)，
并遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

## [Unreleased]

### Added

- **用户引导系统（Task 33）**：首次登录自动展示 5 步功能引导（欢迎 / 项目管理 / 资产管理 / 任务实施 / 仪表盘），完成后通过 `useFirstLogin` 标记已读，下次登录不再触发。顶部导航栏新增「引导」按钮支持手动重新触发。
- **帮助中心页面**（`/help`，公开访问）：左侧分类菜单（快速开始 / 常见问题 / 视频教程 / 进阶技巧），右侧内容列表 + Markdown 渲染详情，支持标题/内容搜索与浏览次数统计。
- **后端 `sys_help_content` 表与 API**：`/api/system/help-content` 提供 CRUD（管理员）+ 公开浏览接口，分类 `QUICK_START / FAQ / VIDEO / ADVANCED`。
- **技术支持反馈机制（Task 34）**：右下角浮动反馈按钮，支持 BUG / 建议 / 咨询 / 其他 4 类反馈，前端 60 秒冷却 + 后端 `@RateLimit` 双重节流。
- **后端 `sys_feedback` 表与 API**：`/api/system/feedback` 提交（任意已登录用户）/ 查看本人 / 管理员列表 + 回复 + 关闭，状态机 `PENDING → PROCESSING → RESOLVED → CLOSED`。
- **系统状态页**（`/system-status`）：调用 `/actuator/health` 展示后端服务 / 数据库 / Redis / 磁盘使用率，叠加当前用户反馈处理状态统计与最近 5 条系统动态。
- **版本日志页**（`/changelog`）：按版本号分组展示变更记录，支持折叠 / 全部展开，变更类型标签（新增 / 优化 / 修复 / 废弃 / 移除 / 安全）。
- **Flyway V28 迁移**：创建 `sys_help_content` 与 `sys_feedback` 两张表，初始化 5 条帮助内容（快速开始 2 条、FAQ 2 条、进阶 1 条）。
- **功能气泡组件 `HelpBubble`**：在指定元素旁显示帮助提示，点击外部自动关闭，支持 `top / bottom / left / right` 四种位置。

### Security

- 帮助内容写入接口（POST/PUT/DELETE）通过 `@PreAuthorize` 限制为管理员权限（`system:help:create / edit / remove`）。
- 反馈接口通过 `@PreAuthorize("isAuthenticated()")` 限制为已登录用户；管理员操作（list / reply / close）需 `system:feedback:list / reply` 权限。
- 反馈控制器在创建时清空提交人字段，防止用户伪造 `userId / username`；服务端从 `SecurityUtils` 重新填充。

## [v1.1.0] - 2026-06-15

### Added

- **低代码引擎**：表单 / 列表 / 标签页 / 关联页 4 类可视化配置，通过 JSON Schema 生成业务页面，无需编写前端代码。
- **D365 / FP / OA 集成推送日志与重试机制**：所有外部系统集成调用记录入日志表，失败支持手动 / 自动重试。
- **集成健康检查面板**（`/integration-health`）：实时监控 D365 / FP / OA 等外部系统连通性，展示推送成功率与最近失败原因。
- **消息中心与 WebSocket 实时通知**：任务派发、审批节点、质保到期等事件通过 WebSocket 推送实时通知。
- **Punch List 模块**：终验前遗留问题清单管理，支持指派、跟踪、销项。
- **RMA 返修模块**：返修申请、审批、物流跟踪、质保处理全流程。
- **质保期管理模块**：设备质保期跟踪、到期预警、续保管理。
- **风险登记册**：项目风险识别、评估、应对策略跟踪。
- **变更管理**：项目范围 / 进度 / 成本变更申请与审批流程。
- **问题日志**：项目问题记录、指派、解决跟踪。
- **审计日志增强**：操作日志、登录日志、异常日志、调度日志分类存储，支持按用户 / 模块 / 时间检索。
- **定时任务管理界面**：基于 Quartz 的可视化任务管理，支持 Cron 表达式配置、手动执行、暂停 / 恢复。

### Changed

- 升级 MyBatis-Plus 至 3.5.5，启用乐观锁插件，所有实体新增 `version` 字段。
- 数据权限拦截器支持基于角色 + 部门 + 公司的细粒度控制。
- Excel 导入导出统一通过 `ExcelImportExport` 组件，支持大数据量流式处理。
- 前端表单组件统一使用 Element Plus 2.14，新增移动端响应式适配（768px 断点）。

### Fixed

- 修复高并发场景下乐观锁冲突导致的更新失败问题（自动重试 3 次）。
- 修复 Excel 导入大数据量（>10w 行）内存溢出问题，改用流式读取。
- 修复路由切换时 echarts 实例未销毁导致的内存泄漏。
- 修复 JWT Token 黑名单在多实例部署下不同步问题（改用 Redis 存储）。

### Security

- 升级 Spring Boot 至 3.2.5，修复 CVE-2024-22243 等 5 个已知漏洞。
- 升级 Fastjson 至 1.2.83，修复反序列化漏洞。
- 字段级加密扩展至邮箱、手机号、身份证号等敏感字段（AES-256-GCM）。
- 新增 CSRF 过滤器与 XSS 清洗过滤器，覆盖所有 POST/PUT/DELETE 请求。

## [v1.0.0] - 2026-03-01

### Added

- **项目管理模块**：项目列表（分页 / 筛选 / 导出）、项目详情（基本信息 / 里程碑 / 团队 / 文档）、交付看板（Kanban 视图）。
- **资产管理模块**：设备分类树、设备型号管理、资产清单（含状态流转：在库 / 调拨 / 借用 / 报废）、资产与项目关联。
- **实施管理模块**：施工任务派发（OEM / 服务商）、服务商管理与评级、结算管理（按工时 / 按项目）。
- **工作流引擎**：基于 Activiti 5.23 的流程引擎，支持项目立项、变更、结算等多流程审批，待办中心集成。
- **报表统计模块**：项目交付统计（月度趋势）、资产统计（按状态 / 分类）、实施效能统计（服务商排名）。
- **系统管理模块**：用户管理、角色管理、菜单管理、字典管理。
- **安全模块**：JWT 认证、RBAC 权限模型、字段加密、CSRF / XSS 防护、操作日志审计。
- **审计日志模块**：操作日志（增删改）、登录日志（成功 / 失败）。
- **定时任务模块**：基于 Quartz 2.3.2 的任务调度。
- **缓存管理模块**：Redis 缓存查询、清理、预热。
- **基础设施**：Spring Boot 3.2.5 + MyBatis-Plus 3.5.5 + Druid 连接池 + Logback 日志。
- **前端框架**：Vue 3.5 + Element Plus 2.14 + TypeScript + Vite + Pinia。

[Unreleased]: https://example.com/compare/v1.1.0...HEAD
[v1.1.0]: https://example.com/releases/v1.1.0
[v1.0.0]: https://example.com/releases/v1.0.0
