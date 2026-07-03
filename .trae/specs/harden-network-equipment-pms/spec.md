# 网络设备工程项目管理系统生产化加固 Spec

> change-id: `harden-network-equipment-pms`
> 前置 spec: `refactor-network-equipment-pms`（已完成，本 spec 在其成果上做生产化加固）

## Why

前一阶段重构交付了"立项→里程碑→终验 + 设备资产 + 原厂/代理商实施"三大核心域基础能力，但存在四类生产化短板：
1. **业务深度不足**：仅 5 类线性里程碑（业内标准 12 节点）、设备状态机仅 5 态（业内 9 态）、无 Punch List 缺陷闭环、无 RMA/质保联动、无变更/风险/问题三本账、无保留金台账，与 Cisco PPDIOO/华为交付流程/HPE Aruba SAC 业内最优规范存在系统性差距。
2. **外部对接为桩**：D365/FP/OA 三大外部系统集成均为占位/桩实现，未完成真实 OAuth2、业务表回填、发票识别、付款回调、致远 OA 真实待办推送。
3. **生产化能力缺失**：无 WebSocket 实时通知、无文件附件管理、无 SpringDoc API 文档、无 Druid 监控、无 CI/CD 流水线、测试覆盖率不足、@Disabled 集成测试未修复。
4. **前端体验不全**：无 TagsView 多标签页、无移动端响应式、无文件上传组件、无通知下拉、无实时消息 Toast。

本 spec 按 Cisco Lifecycle Services（PPDIOO）+ PMBOK 第七版 + ITIL v4 + PRINCE2 阶段门四方法论融合架构，将系统从"里程碑登记簿"升级为"覆盖立项到退网全生命周期的工程交付治理平台"，达到 Cisco Gold Partner / HPE Aruba Readiness Service 同等管理深度。

## What Changes

### A. 业务深度对齐业内规范（P0+P1）

- **MODIFIED** 里程碑体系：由 5 类扩展为 12 节点（Site Survey / Network Design / Procurement / Staging / FAT / Arrival / Installation / Testing / Commissioning / SAT / UAT / Final Acceptance），覆盖 PPDIOO 全周期
- **ADDED** Punch List 缺陷闭环：三级分级（Safety/Functional/Cosmetic）+ 两阶段走场（pre-punch/formal walkdown）+ 照片 GPS 时间戳 + 整改期限 + 保留金释放联动
- **MODIFIED** 设备状态机：由 5 态升级为 9 态（Ordered/In-Transit/Received/Staged/Installed/Commissioned/In-Production/RMA/Decommissioned），含状态迁移规则与权限校验
- **ADDED** RMA 返修流程：6 步闭环（登记工单→校验保修→签发 RMA→故障件返回→到货检验→CMDB 更新）+ SN 主键联动 + MTTR/一次通过率 KPI
- **ADDED** 终验交付物清单：8 项标准交付物（As-Built 竣工图/Test Report/Acceptance Certificate/Training Record/Operation Manual/Asset Register/Warranty Certificate/Spare Parts List）强制校验
- **ADDED** 保留金（Retainage）台账：按合同/项目/里程碑记录扣留/应释放/已释放，与 Punch List 清零联动
- **ADDED** 变更管理（Change Request）：变更日志 + 影响评估 + CCB 审批工作流 + 基线更新
- **ADDED** 风险登记册（Risk Register）：12 字段标准模板 + 概率影响矩阵 + 复审机制
- **ADDED** 问题日志（Issue Log）：与风险/变更联动，支持"风险→问题→变更"转化
- **ADDED** 质保期管理：质保期起止 + 90/60/30 天分级预警 + 保内/保外自动判定 + 续保/退网决策
- **MODIFIED** 资产序列化扩展：增加 MAC/Management IP/Hostname/机房/机架/U 位/PO 号/发票号/保修合同号字段
- **ADDED** 派工单 10 类标准字段：含资产/SOP/物料/工期/技能要求/安全 PPE/证据采集点/签字栏
- **MODIFIED** 代理商认证档案：记录 Gold/Premier/Select 级别 + CCIE 人数 + 专业化认证 + 有效期

### B. D365/FP/OA 全量真实对接

- **MODIFIED** D365 集成：真实 OAuth2 client_credentials + PO 同步 + 采购收货业务表回填 + 资产 SN 同步 + 发票同步 + 健康检查
- **MODIFIED** FP 财务平台集成：真实结算推送 + 发票 OCR 识别 + 付款状态回调 + 推送状态全量记录
- **MODIFIED** 致远 OA 集成：真实 OAuth2 + 待办推送 + 待办完成 + 任务转办同步 + 认证票据管理
- **ADDED** 集成健康检查面板：各外部系统连通性/Token 有效性/最近推送状态可视化
- **ADDED** 集成 Mock 服务：docker-compose 内置 Mock D365/FP/OA 服务，支持联调验证

### C. 实时通知与消息中心

- **ADDED** WebSocket 基础设施：Spring WebSocket + STOMP + Redis Pub/Sub 多实例广播
- **ADDED** 站内信通知中心：消息列表 + 已读/未读 + 分类筛选 + 批量操作
- **ADDED** 消息模板引擎：基于 Velocity/Freemarker 的模板渲染，支持变量注入
- **MODIFIED** 通知触发点：里程碑延期、任务派工、审批待办、Punch List 整改到期、质保期到期、RMA 状态变更、结算审批、保留金释放
- **MODIFIED** 通知通道：站内信 + WebSocket 实时推送 + 邮件 + 致远 OA 待办（多通道并发）

### D. 文件与附件管理

- **ADDED** 统一附件存储抽象层：本地文件系统 + 阿里云 OSS/MinIO 可切换（StorageService 接口 + LocalStorageServiceImpl + OssStorageServiceImpl）
- **ADDED** 附件元数据管理：附件表（业务类型/业务 ID/文件名/大小/MIME/上传人/MD5/存储路径）
- **ADDED** 业务附件能力：实施照片（含 GPS EXIF 解析）、交付物文档、验收报告附件、Punch List 缺陷照片、RMA 故障件照片、终验交付物
- **ADDED** Excel 批量导入导出：基于 EasyExcel，支持资产批量导入、里程碑批量导入、结算明细导出、报表导出
- **ADDED** 文件预览：图片缩略图、PDF 在线预览、Office 文档预览（可选 LibreOffice 转换）

### E. 前端 UX 补全

- **ADDED** TagsView 多标签页：标签栏 + 右键菜单（关闭其他/关闭所有/刷新当前）+ 标签持久化
- **ADDED** 移动端响应式：Element Plus 响应式栅格 + 移动端布局适配 + 触摸友好交互
- **MODIFIED** 加载态与表单校验：全局 loading 指令 + 表单异步校验 + 防抖搜索
- **ADDED** 文件上传组件：拖拽上传 + 进度条 + 分片上传（大文件）+ 断点续传
- **ADDED** 通知下拉：Header 通知铃铛 + 下拉未读列表 + 跳转消息中心
- **ADDED** 实时消息 Toast：WebSocket 推送 + 右上角 Toast 通知 + 点击跳转

### F. 运维与可观测性

- **ADDED** SpringDoc OpenAPI 3：Swagger UI 自动文档 + 接口分组 + 全局鉴权配置 + 示例参数
- **ADDED** Druid SQL 监控：Druid 监控面板 + 慢 SQL 告警 + SQL 防火墙
- **ADDED** 定时任务监控：Quartz/@Scheduled 任务执行历史 + 失败告警 + 手动触发
- **MODIFIED** Redis 缓存：字典/菜单/权限/系统配置缓存 + 缓存穿透/击穿/雪崩防护 + 缓存管理面板
- **ADDED** 性能优化：核心表索引优化 + 慢查询排查 + 分页查询优化
- **ADDED** 操作审计增强：登录日志 + 操作日志 + 异常日志统一审计面板

### G. 全面质量保障

- **MODIFIED** 单元测试覆盖率提升至 70%+：补全 Service/Controller/Mapper 测试
- **MODIFIED** 集成测试修复：使用 Testcontainers 替代 @Disabled，真实 MySQL/Redis/Flowable 联调
- **ADDED** GitHub Actions CI 流水线：PR 触发编译 + 测试 + SonarQube 扫描 + 镜像构建
- **ADDED** SonarQube 静态扫描：代码规范 + 安全漏洞 + 重复代码 + 复杂度 + 覆盖率门禁

## Impact

- **Affected specs**: `refactor-network-equipment-pms`（前置 spec，本 spec 在其代码基础上加固）
- **Affected code**:
  - 后端：所有 8 个模块（pms-common/pms-system/pms-project/pms-asset/pms-implementation/pms-workflow/pms-integration/pms-admin）均有改动
  - 新增模块：`pms-file`（文件附件管理）、`pms-notification`（通知中心）、`pms-governance`（变更/风险/问题三本账）
  - 前端：新增 TagsView、消息中心、文件上传、Punch List、RMA、风险登记册、变更管理等页面
  - 数据库：新增 ~25 张表（punch_list/rma/retention/change_request/risk/issue/attachment/notification/warranty 等）
  - 部署：docker-compose 增加 Mock D365/FP/OA 服务、SonarQube 服务
  - CI：新增 .github/workflows/ci.yml
- **业务影响**：里程碑从 5 类扩展为 12 节点，需 Flyway 数据迁移历史里程碑类型；设备状态机升级需状态迁移脚本；保留金/质保期涉及历史项目数据回填

## ADDED Requirements

### Requirement: 12 节点里程碑体系

The system SHALL provide 12 standard milestone types aligned with Cisco PPDIOO lifecycle: SITE_SURVEY, NETWORK_DESIGN, PROCUREMENT, STAGING, FAT, ARRIVAL, INSTALLATION, TESTING, COMMISSIONING, SAT, UAT, FINAL_ACCEPTANCE.

#### Scenario: 里程碑阶段门控制
- **WHEN** 项目经理尝试跳过 Staging 直接标记 Installation 完成
- **THEN** 系统校验前置 Staging 里程碑状态为 COMPLETED，未完成则拒绝并提示"前置里程碑 Staging 未完成，无法跳过"

#### Scenario: 里程碑阶段归属
- **WHEN** 创建里程碑选择 SITE_SURVEY
- **THEN** 系统自动归属到 PPDIOO 的 Prepare/Plan 阶段，并在仪表盘按阶段分组展示

### Requirement: Punch List 缺陷闭环

The system SHALL provide Punch List management with three-level severity (Safety/Functional/Cosmetic), two-stage walkdown (pre-punch/formal), photo evidence with GPS timestamp, rectification deadline, and retention release linkage.

#### Scenario: Safety 级缺陷立即停工
- **WHEN** 创建 Punch List 项选择 severity=SAFETY
- **THEN** 系统自动置关联里程碑状态为 BLOCKED，并推送紧急通知给项目经理与质量经理

#### Scenario: Punch List 清零触发保留金释放
- **WHEN** 项目所有 Punch List 项状态均为 RESOLVED 且终验通过
- **THEN** 系统自动触发保留金释放流程，生成保留金释放申请单

### Requirement: 9 状态设备资产管理

The system SHALL manage assets with 9-state lifecycle: ORDERED, IN_TRANSIT, RECEIVED, STAGED, INSTALLED, COMMISSIONED, IN_PRODUCTION, RMA, DECOMMISSIONED, with state transition rules and permission validation.

#### Scenario: 非法状态迁移拒绝
- **WHEN** 试图将设备从 RECEIVED 直接迁移到 COMMISSIONED（跳过 STAGED/INSTALLED）
- **THEN** 系统拒绝并提示合法迁移路径：RECEIVED → STAGED → INSTALLED → COMMISSIONED

#### Scenario: RMA 状态联动质保判定
- **WHEN** 创建 RMA 工单关联设备 SN
- **THEN** 系统自动判定设备保内/保外状态（基于保修起止 + 终验日期），保内路由原厂结算，保外路由客户结算

### Requirement: 终验交付物强制校验

The system SHALL enforce 8 standard deliverables before final acceptance approval: As-Built drawing, Test Report, Acceptance Certificate, Training Record, Operation Manual, Asset Register, Warranty Certificate, Spare Parts List.

#### Scenario: 终验审批前交付物校验
- **WHEN** 项目经理提交终验审批
- **THEN** 系统校验 8 项交付物均已上传附件，缺项则拒绝并列出缺失清单

### Requirement: 变更/风险/问题三本账

The system SHALL provide Change Request, Risk Register, and Issue Log with PMBOK standard fields and cross-linkage (risk → issue → change conversion).

#### Scenario: 风险转化为问题
- **WHEN** 用户将 Risk R-001 标记为"已发生"
- **THEN** 系统自动创建 Issue I-xxx 关联 R-001，并保留转化记录

#### Scenario: 变更影响基线
- **WHEN** CCB 批准 Change Request CR-001
- **THEN** 系统更新项目基线（进度/成本/范围），记录基线变更历史，并通知所有项目成员

### Requirement: 保留金台账

The system SHALL maintain Retainage ledger per contract/project/milestone, tracking withheld/releasable/released amounts, linked to Punch List clearance.

#### Scenario: 里程碑结算扣留保留金
- **WHEN** 里程碑结算审批通过
- **THEN** 系统按合同保留金比例（默认 5%-10%）自动扣留，记录到保留金台账

### Requirement: D365/FP/OA 全量真实对接

The system SHALL integrate with D365 (OAuth2 + PO/receipt/asset SN/invoice sync + business table backfill), FP (settlement push + invoice OCR + payment callback), and Seeyon OA (OAuth2 + todo push/complete/transfer + ticket management), with health check dashboard and mock services for integration testing.

#### Scenario: D365 Token 刷新失败告警
- **WHEN** D365 OAuth2 Token 刷新连续失败 3 次
- **THEN** 系统推送告警通知给集成管理员，并在健康检查面板标记 D365 为"异常"

#### Scenario: FP 推送失败指数退避重试
- **WHEN** FP 结算推送失败
- **THEN** 系统按指数退避策略重试（1min/2min/4min/8min/16min），达上限后标记为"推送失败"并通知财务

### Requirement: WebSocket 实时通知

The system SHALL provide real-time notification via WebSocket (STOMP + Redis Pub/Sub), supporting multi-instance broadcast, message templates, and multi-channel delivery (in-app + WebSocket + email + OA todo).

#### Scenario: 多实例广播通知
- **WHEN** 用户 A 在实例 1 提交审批，用户 B（审批人）连接在实例 2
- **THEN** 用户 B 通过 Redis Pub/Sub 收到实时 WebSocket 通知，无需轮询

### Requirement: 文件附件统一管理

The system SHALL provide unified attachment storage abstraction (local + OSS/MinIO switchable), metadata management, business attachment capabilities (implementation photos with GPS EXIF, deliverables, acceptance reports, Punch List photos, RMA photos), and Excel bulk import/export.

#### Scenario: 实施照片 GPS 校验
- **WHEN** 工程师上传实施照片
- **THEN** 系统解析 EXIF GPS 信息，与项目站点坐标比对，超出围栏范围（默认 500m）则标记为"异常位置"并记录

### Requirement: SpringDoc OpenAPI 文档

The system SHALL provide Swagger UI auto-documentation with API grouping, global auth config, and example parameters.

#### Scenario: 接口文档访问
- **WHEN** 开发者访问 /swagger-ui.html
- **THEN** 系统展示分组后的 API 文档（系统/项目/资产/实施/工作流/集成/文件/通知），支持在线调试

### Requirement: 全面质量保障

The system SHALL achieve 70%+ unit test coverage, fix all @Disabled integration tests with Testcontainers, set up GitHub Actions CI pipeline (compile + test + SonarQube + image build), and SonarQube static analysis with quality gates.

#### Scenario: CI 流水线 PR 触发
- **WHEN** 开发者提交 PR
- **THEN** GitHub Actions 自动触发编译 + 单元测试 + SonarQube 扫描，质量门禁通过方可合并

## MODIFIED Requirements

### Requirement: 里程碑体系（原 5 类扩展为 12 节点）

原 5 类：ARRIVAL / INSTALL / DEBUG / INITIAL_ACCEPTANCE / FINAL_ACCEPTANCE
新 12 节点：SITE_SURVEY / NETWORK_DESIGN / PROCUREMENT / STAGING / FAT / ARRIVAL / INSTALLATION / TESTING / COMMISSIONING / SAT / UAT / FINAL_ACCEPTANCE

迁移：历史里程碑 DEBUG 映射为 TESTING+COMMISSIONING，INITIAL_ACCEPTANCE 映射为 SAT，其余保持。

### Requirement: 设备状态机（原 5 态升级为 9 态）

原 5 态：IN_STOCK / ALLOCATED / IN_TRANSIT / RETURNED / SCRAP
新 9 态：ORDERED / IN_TRANSIT / RECEIVED / STAGED / INSTALLED / COMMISSIONED / IN_PRODUCTION / RMA / DECOMMISSIONED

迁移：IN_STOCK 映射为 RECEIVED，ALLOCATED 映射为 INSTALLED，IN_TRANSIT 保持，RETURNED 映射为 RECEIVED，SCRAP 映射为 DECOMMISSIONED。

## REMOVED Requirements

### Requirement: 通知桩实现（NotificationServiceImpl 仅日志）

**Reason**: 替换为真实 WebSocket + 站内信 + OA 多通道通知
**Migration**: NotificationService 接口保持，实现类替换为多通道实现

### Requirement: pushToFp 占位实现

**Reason**: 已在前一阶段删除，本 spec 确认不存在占位
**Migration**: 无（已完成）
