# Tasks

## Phase 1: 业务深度对齐业内规范 — 领域模型与数据库

- [ ] Task 1: 12 节点里程碑体系扩展
  - [ ] SubTask 1.1: 扩展 MilestoneType 枚举为 12 节点（SITE_SURVEY/NETWORK_DESIGN/PROCUREMENT/STAGING/FAT/ARRIVAL/INSTALLATION/TESTING/COMMISSIONING/SAT/UAT/FINAL_ACCEPTANCE），增加 PPDIOO 阶段归属字段（PREPARE/PLAN/DESIGN/IMPLEMENT/OPERATE）
  - [ ] SubTask 1.2: 实现 PRINCE2 阶段门控制（前置里程碑未完成则拒绝跳过，FINA_ACCEPTANCE 前置需 SAT/UAT 完成）
  - [ ] SubTask 1.3: Flyway V10 迁移：扩展 milestone_type 枚举 + 历史数据迁移（DEBUG→TESTING+COMMISSIONING，INITIAL_ACCEPTANCE→SAT）+ 增加 ppdioo_phase 字段
  - [ ] SubTask 1.4: 里程碑仪表盘按 PPDIOO 阶段分组展示接口

- [ ] Task 2: 9 状态设备资产状态机升级
  - [ ] SubTask 2.1: 扩展 AssetStatus 枚举为 9 态（ORDERED/IN_TRANSIT/RECEIVED/STAGED/INSTALLED/COMMISSIONED/IN_PRODUCTION/RMA/DECOMMISSIONED）
  - [ ] SubTask 2.2: 实现状态迁移规则校验器 AssetStateTransitionValidator（含合法迁移矩阵 + 非法迁移拒绝 + 错误提示合法路径）
  - [ ] SubTask 2.3: Flyway V11 迁移：扩展 asset_status 枚举 + 历史数据迁移（IN_STOCK→RECEIVED，ALLOCATED→INSTALLED，RETURNED→RECEIVED，SCRAP→DECOMMISSIONED）
  - [ ] SubTask 2.4: 升级 AssetServiceImpl 各状态变更方法适配新状态机（inbound→RECEIVED，allocate→INSTALLED，transfer→IN_TRANSIT 暂存→归还 RECEIVED，returnAsset→RECEIVED）

- [ ] Task 3: Punch List 缺陷闭环模块（pms-project 子包）
  - [ ] SubTask 3.1: 新增 PunchList 实体（projectId/milestoneId/severity[SAFETY/FUNCTIONAL/COSMETIC]/title/description/walkdownStage[PRE_PUNCH/FORMAL]/assigneeId/deadline/status[OPEN/RESOLVED/VERIFIED]/resolvedAt/verifiedAt）+ Mapper/Service/Controller
  - [ ] SubTask 3.2: 实现 Safety 级缺陷立即停工逻辑（创建 SAFETY 缺陷时置关联里程碑 BLOCKED + 推送紧急通知）
  - [ ] SubTask 3.3: 实现 Punch List 清零校验（终验审批前校验所有 OPEN/非 VERIFIED 项清零）
  - [ ] SubTask 3.4: Flyway V12 创建 pms_punch_list 表
  - [ ] SubTask 3.5: Punch List 照片附件关联（依赖 Task 17 文件附件模块，先预留 attachmentId 字段）

- [ ] Task 4: RMA 返修流程模块（pms-asset 子包）
  - [ ] SubTask 4.1: 新增 Rma 实体（rmaNo/assetId/sn/faultDescription/faultPhotos/ticketStatus[REGISTERED/WARRANTY_CHECKED/RMA_ISSUED/RETURNING/INSPECTED/CLOSED]/warrantyStatus[IN_WARRANTY/OUT_OF_WARRANTY]/createdAt/...）+ Mapper/Service/Controller
  - [ ] SubTask 4.2: 实现 6 步闭环：登记工单 → 校验保修（联动 Task 6 质保期） → 签发 RMA → 故障件返回 → 到货检验 → CMDB 更新（资产状态 RMA→IN_PRODUCTION/DECOMMISSIONED）
  - [ ] SubTask 4.3: 实现 MTTR 与一次通过率 KPI 统计接口
  - [ ] SubTask 4.4: Flyway V13 创建 pms_rma 表

- [ ] Task 5: 终验交付物强制校验 + 保留金台账
  - [ ] SubTask 5.1: 新增 DeliverableChecklist 实体（projectId/deliverableType[AS_BUILT/TEST_REPORT/ACCEPTANCE_CERT/TRAINING_RECORD/OPERATION_MANUAL/ASSET_REGISTER/WARRANTY_CERT/SPARE_PARTS_LIST]/required/uploaded/attachmentId/checkedAt）+ Mapper/Service
  - [ ] SubTask 5.2: 修改 FinalAcceptanceServiceImpl.apply：终验申请前校验 8 项交付物全部 uploaded=true，缺项拒绝并列出清单
  - [ ] SubTask 5.3: 新增 Retainage 实体（projectId/contractId/milestoneId/withheldAmount/releasableAmount/releasedAmount/status[WITHHELD/RELEASABLE/RELEASED]/...）+ Mapper/Service/Controller
  - [ ] SubTask 5.4: 实现里程碑结算自动扣留保留金（按合同比例 5%-10%）+ Punch List 清零触发释放流程
  - [ ] SubTask 5.5: Flyway V14 创建 pms_deliverable_checklist 与 pms_retainage 表

- [ ] Task 6: 质保期管理 + 保内/保外判定
  - [ ] SubTask 6.1: 新增 Warranty 实体（assetId/startDate/endDate/durationMonths/slaLevel[BASIC/PREMIUM/PLATINUM]/contractNo/...）+ Mapper/Service/Controller
  - [ ] SubTask 6.2: 实现 WarrantyService.isInWarranty(assetId, date)：基于保修起止 + 终验日期判定保内/保外
  - [ ] SubTask 6.3: 实现 WarrantyExpiryScheduler（@Scheduled 每日 03:00）：扫描到期前 90/60/30 天分级预警，推送通知（依赖 Task 14/15）
  - [ ] SubTask 6.4: 终验通过后自动初始化质保期（终验日 +1 天为 startDate，durationMonths 默认 12，可配置）
  - [ ] SubTask 6.5: Flyway V15 创建 pms_warranty 表 + pms_asset 增加 warranty_id 字段

- [ ] Task 7: 资产序列化扩展 + 派工单标准字段 + 代理商认证档案
  - [ ] SubTask 7.1: Asset 实体增加字段：macAddress/managementIp/hostname/dataCenter/rack/startU/endU/imei/poNo/invoiceNo/warrantyContractNo
  - [ ] SubTask 7.2: ImplTask 实体扩展 10 类派工单标准字段：customerContact/serviceAddress/serviceType[SITE_SURVEY/INSTALL/DEBUG/MAINTENANCE]/sopSteps/materialList/plannedHours/skillLevel/safetyPpe[PPE/LOTO/PERMIT]/evidenceCheckpoints/signOffRequired
  - [ ] SubTask 7.3: Agent 实体增加认证档案字段：certLevel[SELECT/PREMIER/SILVER/GOLD]/ccieCount/specializations[JSON]/certExpiryDate
  - [ ] SubTask 7.4: Flyway V16 迁移：ALTER 三个表增加字段

## Phase 2: 项目治理三本账（变更/风险/问题）

- [ ] Task 8: 变更管理（CR）模块（新增 pms-governance 模块）
  - [ ] SubTask 8.1: 创建 pms-governance 模块骨架（pom.xml 依赖 pms-common/pms-workflow）
  - [ ] SubTask 8.2: 新增 ChangeRequest 实体（crNo/projectId/title/description/requesterId/requestDate/impactScope/impactSchedule/impactCost/impactQuality/priority[LOW/MEDIUM/HIGH/CRITICAL]/status[SUBMITTED/UNDER_REVIEW/CCB_APPROVED/CCB_REJECTED/IMPLEMENTING/CLOSED]/approverId/baselineUpdated）+ Mapper/Service/Controller
  - [ ] SubTask 8.3: 实现变更审批工作流（changeRequestApproval.bpmn + CCB 多角色审批）+ 基线更新（记录进度/成本/范围变更历史）
  - [ ] SubTask 8.4: Flyway V17 创建 pms_change_request + pms_baseline_history 表

- [ ] Task 9: 风险登记册 + 问题日志 + 三本账联动
  - [ ] SubTask 9.1: 新增 Risk 实体（riskNo/projectId/description/category[TECHNICAL/EXTERNAL/ORGANIZATIONAL/PM]/likelihood[1-5]/impact[1-5]/score/priority[LOW/MEDIUM/HIGH]/mitigation[AVOID/MITIGATE/TRANSFER/ACCEPT]/contingencyPlan/ownerId/status[OPEN/IN_PROGRESS/CLOSED/ESCALATED]/reviewDate）+ Mapper/Service/Controller
  - [ ] SubTask 9.2: 新增 Issue 实体（issueNo/projectId/description/raisedBy/assigneeId/priority/targetResolveDate/status[OPEN/IN_PROGRESS/RESOLVED/CLOSED]/sourceRiskId/sourceChangeId）+ Mapper/Service/Controller
  - [ ] SubTask 9.3: 实现三本账联动：风险标记"已发生"自动创建 Issue 关联源风险；Issue 升级触发 ChangeRequest
  - [ ] SubTask 9.4: 实现概率影响矩阵视图接口（5x5 矩阵 + 风险分布热力图数据）
  - [ ] SubTask 9.5: Flyway V18 创建 pms_risk + pms_issue 表

## Phase 3: D365/FP/OA 全量真实对接

- [ ] Task 10: D365 真实对接
  - [ ] SubTask 10.1: 完善 D365IntegrationServiceImpl：真实 OAuth2 client_credentials（移除桩），增加 Token 失败计数与告警（连续 3 次失败推送告警 + 健康面板标红）
  - [ ] SubTask 10.2: 新增 D365 同步能力：PO 同步（拉取 D365 PO → 本地采购单表）、采购收货业务表回填（receipt 表 + push_status 字段）、资产 SN 同步（D365 SN → 本地 Asset.sn）、发票同步（D365 Invoice → 本地结算单 invoice_no 回填）
  - [ ] SubTask 10.3: 实现 D365 健康检查接口（GET /api/integration/d365/health：连通性 + Token 有效性 + 最近 10 笔推送状态）
  - [ ] SubTask 10.4: 新增 d365_purchase_receipt 与 d365_invoice 同步业务表（V19 迁移）
  - [ ] SubTask 10.5: 新增 D365 Mock 服务（Spring Boot 小应用，docker-compose 内）：模拟 OAuth2 token 端点 + PO/receipt/invoice CRUD 端点

- [ ] Task 11: FP 财务平台真实对接
  - [ ] SubTask 11.1: 完善 FpIntegrationServiceImpl：真实 OAuth2 + 结算推送（组装符合 FP 规范的 JSON payload）+ 推送失败指数退避重试（1/2/4/8/16min 上限 5 次）
  - [ ] SubTask 11.2: 新增发票 OCR 识别接口（调用 FP 发票识别 API，回填发票号/金额/税额到结算单）
  - [ ] SubTask 11.3: 新增付款状态回调接口（POST /api/integration/fp/payment-callback：FP 主动回调付款状态，更新结算单 payment_status）
  - [ ] SubTask 11.4: FP 健康检查接口 + 推送状态全量记录（IntegrationLog 含完整 request/response）
  - [ ] SubTask 11.5: FP Mock 服务（docker-compose 内）：模拟 OAuth2 + 结算推送接收 + 发票识别 + 付款回调

- [ ] Task 12: 致远 OA 真实对接
  - [ ] SubTask 12.1: 完善 OaIntegrationServiceImpl：真实 OAuth2 + 认证票据管理（票据缓存 + 自动续期）
  - [ ] SubTask 12.2: 实现待办推送（pushTodo：组装符合致远 OA 待办 API 的 JSON，含 title/content/handler/processUrl/参数）+ 待办完成（completeTodo：标记致远 OA 待办为已办）+ 任务转办同步（transferTask 时同步致远 OA 待办处理人变更）
  - [ ] SubTask 12.3: OaTaskListener 增强：异常重试（独立于主流程事务，失败不影响工作流）+ 推送状态记录（IntegrationLog logType=OA）
  - [ ] SubTask 12.4: OA 健康检查接口 + OA Mock 服务（docker-compose 内）

- [ ] Task 13: 集成健康检查面板 + Mock 服务编排
  - [ ] SubTask 13.1: 新增 IntegrationHealthController（GET /api/integration/health：聚合 D365/FP/OA 三个子系统健康状态 + Token 有效性 + 最近推送统计）
  - [ ] SubTask 13.2: 前端集成健康检查面板页面（卡片式展示三个系统状态 + 历史推送记录列表 + 手动重试按钮）
  - [ ] SubTask 13.3: docker-compose.yml 增加 mock-d365/mock-fp/mock-oa 三个服务，应用配置 profile=mock 启用 Mock 模式
  - [ ] SubTask 13.4: application-mock.yml 配置：所有外部系统 baseUrl 指向 Mock 服务

## Phase 4: 实时通知与消息中心

- [ ] Task 14: WebSocket 基础设施
  - [ ] SubTask 14.1: 引入 spring-boot-starter-websocket + spring-messaging，配置 WebSocketConfig（STOMP + SockJS 端点 /ws + 心跳 + 鉴权拦截器）
  - [ ] SubTask 14.2: 实现 Redis Pub/Sub 多实例广播（NotificationPublisher 发布 + NotificationSubscriber 订阅 + 用户连接路由）
  - [ ] SubTask 14.3: 实现 WebSocket 鉴权拦截器（HandshakeInterceptor 校验 JWT Token + ChannelInterceptor 校验订阅权限）
  - [ ] SubTask 14.4: 前端 WebSocket 客户端封装（sockjs-client + stompjs + 自动重连 + 心跳 + 订阅 /user/queue/notifications）

- [ ] Task 15: 站内信通知中心 + 消息模板引擎
  - [ ] SubTask 15.1: 新增 pms-notification 模块骨架 + Notification 实体（userId/title/content/category[MILESTONE/TASK/APPROVAL/PUNCH_LIST/WARRANTY/RMA/SETTLEMENT/RETAINAGE]/bizType/bizId/readStatus[UNREAD/READ]/channel[IN_APP/WS/EMAIL/OA]/createdAt）+ Mapper/Service/Controller
  - [ ] SubTask 15.2: 实现 NotificationService.multiChannelSend（并发发送：站内信落库 + WebSocket 推送 + 邮件 + OA 待办，任一通道失败不阻塞其他）
  - [ ] SubTask 15.3: 新增 NotificationTemplate 实体（templateCode/subject/body/variables[JSON]）+ 模板渲染引擎（Freemarker，支持变量注入 ${user} ${projectName} 等）
  - [ ] SubTask 15.4: 前端消息中心页面（消息列表 + 已读/未读筛选 + 分类筛选 + 批量已读 + 跳转业务详情）
  - [ ] SubTask 15.5: Flyway V20 创建 pms_notification + pms_notification_template 表 + 预置 12 个标准模板

- [ ] Task 16: 多通道通知触发点接入
  - [ ] SubTask 16.1: 里程碑延期预警接入真实通知（MilestoneOverdueScheduler 调用 NotificationService 替代日志，推送项目经理 + 部门经理）
  - [ ] SubTask 16.2: 任务派工/委派通知接入（ImplTaskServiceImpl.assignOemTask/assignAgentTask 调用 NotificationService 替代桩）
  - [ ] SubTask 16.3: 审批待办通知接入（WorkflowServiceImpl.startProcess/completeTask 通过 OaTaskListener + NotificationService 双通道推送）
  - [ ] SubTask 16.4: Punch List 整改到期通知（@Scheduled 扫描 deadline 临近的 OPEN 项推送责任人）
  - [ ] SubTask 16.5: 质保期到期/RMA 状态变更/结算审批/保留金释放通知接入对应业务 Service

## Phase 5: 文件与附件管理

- [ ] Task 17: 统一附件存储抽象层 + 元数据管理
  - [ ] SubTask 17.1: 创建 pms-file 模块骨架（pom.xml 依赖 pms-common + 阿里云 OSS SDK + MinIO SDK 可选）
  - [ ] SubTask 17.2: 定义 StorageService 接口（upload/download/delete/generatePresignedUrl）+ LocalStorageServiceImpl（本地文件系统）+ OssStorageServiceImpl（阿里云 OSS）+ MinioStorageServiceImpl（MinIO），通过 @ConditionalOnProperty 切换
  - [ ] SubTask 17.3: 新增 Attachment 实体（bizType/bizId/fileName/fileSize/mimeType/uploadUserId/uploadTime/md5/storagePath/storageType[LOCAL/OSS/MINIO]）+ Mapper/Service/Controller
  - [ ] SubTask 17.4: 实现 FileController 上传/下载/删除接口（POST /api/file/upload multipart + GET /api/file/{id}/download + DELETE /api/file/{id}）
  - [ ] SubTask 17.5: Flyway V21 创建 pms_attachment 表

- [ ] Task 18: 业务附件能力（GPS EXIF + 终验交付物 + Punch List/RMA 照片）
  - [ ] SubTask 18.1: 实现照片 GPS EXIF 解析（metadata-extractor 库），上传时提取 GPS 坐标 + 拍摄时间，与项目站点坐标比对（围栏 500m），异常标记
  - [ ] SubTask 18.2: 终验交付物附件：DeliverableChecklist.uploaded=true 时关联 attachmentId（Task 5 联动）
  - [ ] SubTask 18.3: Punch List 缺陷照片附件 + RMA 故障件照片附件（attachmentId 关联）
  - [ ] SubTask 18.4: 实施照片附件（ImplProgress 上报时关联 attachmentIds，支持多图）
  - [ ] SubTask 18.5: 文件预览：图片缩略图生成（Thumbnailator）+ PDF 在线预览（PDF.js 前端）+ Office 文档预览（可选 LibreOffice headless 转换）

- [ ] Task 19: Excel 批量导入导出
  - [ ] SubTask 19.1: 引入 EasyExcel 依赖，定义通用导入导出工具类 ExcelUtils（支持模板下载/导入/导出/校验/错误报告）
  - [ ] SubTask 19.2: 资产批量导入（POST /api/asset/import：Excel 模板下载 + 上传解析 + 校验 + 批量入库 + 错误报告下载）
  - [ ] SubTask 19.3: 里程碑批量导入（POST /api/project/{id}/milestones/import）
  - [ ] SubTask 19.4: 结算明细导出（GET /api/settlement/{id}/export）+ 报表导出（GET /api/report/export?type=delivery/asset/implementation）
  - [ ] SubTask 19.5: 前端导入导出组件封装（上传 Excel + 下载模板 + 下载错误报告 + 导出按钮）

## Phase 6: 前端 UX 补全

- [ ] Task 20: TagsView + 移动端响应式 + 加载态优化
  - [ ] SubTask 20.1: 实现 TagsView 多标签页组件（标签栏 + 右键菜单关闭其他/关闭所有/刷新当前 + localStorage 持久化 + 路由联动）
  - [ ] SubTask 20.2: DefaultLayout 集成 TagsView（顶部导航下方位标签栏）
  - [ ] SubTask 20.3: 移动端响应式适配（Element Plus el-row/el-col 响应式栅格 + 侧边栏抽屉模式 + Header 简化 + 触摸友好交互）
  - [ ] SubTask 20.4: 全局 loading 指令 v-loading + 表单异步校验（用户名/项目编号唯一性等）+ 防抖搜索（el-input debounce）

- [ ] Task 21: 文件上传组件 + 通知下拉 + 实时消息 Toast
  - [ ] SubTask 21.1: 实现文件上传组件 FileUploader.vue（拖拽上传 + 进度条 + 分片上传大文件 + 断点续传 + 预览缩略图 + 多文件管理）
  - [ ] SubTask 21.2: Header 通知铃铛 NotificationBell.vue（未读数 badge + 下拉未读列表 Top 5 + 跳转消息中心 + 标记已读）
  - [ ] SubTask 21.3: 实时消息 Toast（WebSocket 收到通知 → ElNotification 右上角 Toast + 点击跳转业务详情 + 5 秒自动消失）
  - [ ] SubTask 21.4: 全局 WebSocket 连接管理（Pinia store + 自动重连 + 在线状态）

- [ ] Task 22: 新业务页面
  - [ ] SubTask 22.1: Punch List 管理页面（项目内嵌 Tab：缺陷列表 + 三级严重度筛选 + 创建/整改/验证流程 + 照片附件展示）
  - [ ] SubTask 22.2: RMA 返修管理页面（RMA 列表 + 6 步流程进度条 + 故障件照片 + 保修判定展示 + MTTR/一次通过率 KPI 卡片）
  - [ ] SubTask 22.3: 风险登记册页面（5x5 概率影响矩阵热力图 + 风险列表 + 复审提醒 + 风险转问题操作）
  - [ ] SubTask 22.4: 变更管理页面（CR 列表 + 影响评估表单 + CCB 审批 + 基线变更历史时间线）
  - [ ] SubTask 22.5: 问题日志页面（问题列表 + 责任人分派 + 状态流转 + 源风险/源变更关联展示）
  - [ ] SubTask 22.6: 质保期管理页面（资产质保期列表 + 到期预警日历 + 续保/退网决策操作 + SLA 等级展示）
  - [ ] SubTask 22.7: 保留金台账页面（按项目/合同分组 + 扣留/应释放/已释放金额 + 释放审批流程）
  - [ ] SubTask 22.8: 终验交付物清单页面（8 项交付物 checklist + 上传附件 + 校验状态 + 一键下载全部）
  - [ ] SubTask 22.9: 消息中心页面（Task 15.4 实现）
  - [ ] SubTask 22.10: 集成健康检查面板页面（Task 13.2 实现）
  - [ ] SubTask 22.11: 路由配置 + 菜单配置更新（新增业务菜单分组）

## Phase 7: 运维与可观测性

- [ ] Task 23: SpringDoc OpenAPI + Druid 监控 + 定时任务监控
  - [ ] SubTask 23.1: 引入 springdoc-openapi-starter-webmvc-ui 依赖，配置 OpenApiConfig（API 分组：system/project/asset/implementation/workflow/integration/file/notification/governance + 全局 JWT 鉴权 + 示例参数）
  - [ ] SubTask 23.2: 各 Controller 补充 @Tag/@Operation/@Parameter 注解，关键 DTO 补充 @Schema 注解
  - [ ] SubTask 23.3: Druid SQL 监控配置（替换 HikariCP 为 Druid + StatViewServlet 监控面板 + 慢 SQL 阈值告警 + SQL 防火墙 WallFilter）
  - [ ] SubTask 23.4: 定时任务监控：ScheduleLog 实体（任务名/执行时间/耗时/状态/异常）+ ScheduleMonitorController 查询接口 + 失败告警 + 手动触发接口

- [ ] Task 24: Redis 缓存优化 + 性能优化 + 操作审计增强
  - [ ] SubTask 24.1: Redis 缓存：SysDict/SysMenu/SysConfig/SysRole 权限缓存 + @Cacheable 注解 + 缓存穿透防护（空值缓存）+ 击穿防护（互斥锁）+ 雪崩防护（随机 TTL）+ 缓存管理面板（手动清除）
  - [ ] SubTask 24.2: 性能优化：核心表索引优化（pms_project.status/pm_user_role.user_id 等复合索引）+ 慢查询日志分析 + 分页查询 count 优化
  - [ ] SubTask 24.3: 操作审计增强：LoginLog 实体（登录日志）+ ExceptionLog 实体（异常日志）+ 统一审计面板（登录/操作/异常三 Tab + 时间/用户/IP 筛选）
  - [ ] SubTask 24.4: Flyway V22 创建 sys_login_log + sys_exception_log + sys_schedule_log 表

## Phase 8: 全面质量保障

- [ ] Task 25: 单元测试覆盖率提升 + Testcontainers 集成测试修复
  - [ ] SubTask 25.1: 补全 pms-project Service 层测试（12 节点里程碑 + Punch List + 终验交付物校验 + 保留金），覆盖率 70%+
  - [ ] SubTask 25.2: 补全 pms-asset Service 层测试（9 状态机迁移校验 + RMA 6 步 + 质保判定 + 序列化字段），覆盖率 70%+
  - [ ] SubTask 25.3: 补全 pms-implementation Service 层测试（派工单字段 + 代理商认证 + 结算推送 FP），覆盖率 70%+
  - [ ] SubTask 25.4: 补全 pms-governance Service 层测试（变更/风险/问题三本账联动），覆盖率 70%+
  - [ ] SubTask 25.5: 补全 pms-notification/pms-file Service 层测试，覆盖率 70%+
  - [ ] SubTask 25.6: 修复 @Disabled 集成测试：引入 Testcontainers（MySQL/Redis/Flowable），ProjectControllerIntegrationTest 改为 Testcontainers 真实联调
  - [ ] SubTask 25.7: 新增 WebSocket/文件上传/Excel 导入导出集成测试
  - [ ] SubTask 25.8: 前端测试补全：新增 Punch List/RMA/风险/变更/消息中心组件 Vitest 测试 + WebSocket 客户端测试

- [ ] Task 26: GitHub Actions CI + SonarQube 静态扫描
  - [ ] SubTask 26.1: 创建 .github/workflows/ci.yml：PR 触发 → JDK 17 setup → Maven 编译 → 单元测试 → 前端构建 → 测试 → SonarQube 扫描 → Docker 镜像构建（条件：main 分支）
  - [ ] SubSub Task 26.2: 创建 sonar-project.properties + SonarQube 质量门禁配置（覆盖率 ≥70% + 重复率 ≤3% + 严重漏洞 = 0 + 圈复杂度 ≤15）
  - [ ] SubTask 26.3: docker-compose.yml 增加 sonarqube 服务（开发环境）+ CI 中使用 SonarQube 官方 Action
  - [ ] SubTask 26.4: 创建 .github/workflows/deploy.yml：main 分支推送触发 → 构建镜像 → 推送镜像仓库 → 触发部署（占位，按实际环境补充）
  - [ ] SubTask 26.5: README.md 补充 CI/CD 流程说明 + 质量门禁规则 + 本地开发指南

# Task Dependencies

- Task 2 depends on Task 1
- Task 3 depends on Task 1
- Task 4 depends on Task 2, Task 6
- Task 5 depends on Task 1, Task 3, Task 17
- Task 6 depends on Task 2
- Task 7 depends on Task 2
- Task 8 depends on Task 1 (pms-governance 依赖 pms-workflow 已有)
- Task 9 depends on Task 8
- Task 10 depends on Task 7
- Task 11 depends on Task 7
- Task 12 depends on Task 7
- Task 13 depends on Task 10, Task 11, Task 12
- Task 14 depends on Task 1 (基础设施，无强依赖)
- Task 15 depends on Task 14
- Task 16 depends on Task 15, Task 3, Task 4, Task 5, Task 6, Task 8, Task 9
- Task 17 depends on Task 1 (基础设施，无强依赖)
- Task 18 depends on Task 17, Task 3, Task 4, Task 5
- Task 19 depends on Task 17
- Task 20 depends on Task 1
- Task 21 depends on Task 14, Task 15, Task 17
- Task 22 depends on Task 3, Task 4, Task 5, Task 6, Task 8, Task 9, Task 13, Task 15, Task 17
- Task 23 depends on Task 1
- Task 24 depends on Task 23
- Task 25 depends on Task 1, Task 2, Task 3, Task 4, Task 5, Task 6, Task 7, Task 8, Task 9, Task 14, Task 15, Task 17
- Task 26 depends on Task 25
