# Checklist

## Phase 1: 业务深度对齐业内规范 — 领域模型与数据库

### Task 1: 12 节点里程碑体系
- [x] MilestoneType 枚举扩展为 12 节点，含 PPDIOO 阶段归属字段
- [x] PRINCE2 阶段门控制生效（跳过前置里程碑时拒绝并提示）
- [x] Flyway V10 迁移成功，历史里程碑数据正确映射（DEBUG→TESTING+COMMISSIONING，INITIAL_ACCEPTANCE→SAT）
- [x] 里程碑仪表盘按 PPDIOO 阶段分组展示接口返回正确分组数据

### Task 2: 9 状态设备资产状态机
- [x] AssetStatus 枚举扩展为 9 态
- [x] AssetStateTransitionValidator 校验非法迁移并提示合法路径
- [x] Flyway V11 迁移成功，历史资产状态正确映射（IN_STOCK→RECEIVED 等）
- [x] AssetServiceImpl 各方法适配新状态机（inbound→RECEIVED，allocate→INSTALLED 等）

### Task 3: Punch List 缺陷闭环
- [x] PunchList 实体/Mapper/Service/Controller 完整 CRUD
- [x] Safety 级缺陷创建时关联里程碑置 BLOCKED + 紧急通知
- [x] 终验审批前 Punch List 清零校验生效
- [x] Flyway V12 pms_punch_list 表创建成功

### Task 4: RMA 返修流程
- [x] Rma 实体/Mapper/Service/Controller 完整 CRUD
- [x] 6 步闭环流程可流转（登记→保修校验→签发→返回→检验→CMDB 更新）
- [x] MTTR 与一次通过率 KPI 统计接口返回正确数据
- [x] Flyway V13 pms_rma 表创建成功

### Task 5: 终验交付物强制校验
- [x] DeliverableChecklist 实体/Mapper/Service 完整
- [x] 终验申请前 8 项交付物强制校验生效，缺项拒绝并列出清单
- [x] 终验申请前 Punch List 全部 VERIFIED 校验生效
- [x] Flyway V14 pms_deliverable_checklist 表创建成功

### Task 6: 质保期管理
- [x] Warranty 实体/Mapper/Service/Controller 完整
- [x] isInWarranty(assetId, date) 保内/保外判定正确
- [x] WarrantyExpiryScheduler 90/60/30 天分级预警并推送通知
- [x] 终验通过后自动初始化质保期（终验日+1，默认 12 月）
- [x] Flyway V15 pms_warranty 表 + pms_asset.warranty_id 字段创建成功

### Task 7: 资产序列化 + 派工单字段 + 代理商认证
- [x] Asset 增加 macAddress/managementIp/hostname/dataCenter/rack/startU/endU/imei/poNo/invoiceNo/warrantyContractNo 字段
- [x] ImplTask 扩展 10 类派工单标准字段
- [x] Agent 增加 certLevel/ccieCount/specializations/certExpiryDate 认证档案字段
- [x] Flyway V16 三个表 ALTER 迁移成功

## Phase 2: 项目治理三本账

### Task 8: 变更管理（CR）
- [x] pms-governance 模块创建并依赖 pms-common/pms-workflow
- [x] ChangeRequest 实体/Mapper/Service/Controller 完整 CRUD
- [x] 变更审批工作流 changeRequestApproval.bpmn 部署并可流转
- [x] CCB 批准后基线更新（进度/成本/范围变更历史记录）
- [x] Flyway V17 pms_change_request + pms_baseline_history 表创建成功

### Task 9: 风险登记册 + 问题日志 + 三本账联动
- [x] Risk 实体 12 字段完整，Mapper/Service/Controller CRUD
- [x] Issue 实体完整，Mapper/Service/Controller CRUD
- [x] 风险标记"已发生"自动创建 Issue 关联源风险
- [x] Issue 升级触发 ChangeRequest 转化
- [x] 概率影响矩阵 5x5 视图接口返回热力图数据
- [x] Flyway V18 pms_risk + pms_issue 表创建成功

## Phase 3: D365/FP/OA 全量真实对接

### Task 10: D365 真实对接
- [x] D365IntegrationServiceImpl OAuth2 真实实现，Token 失败 3 次告警
- [x] PO 同步、采购收货业务表回填、资产 SN 同步、发票同步 4 个能力实现
- [x] D365 健康检查接口返回连通性/Token 有效性/最近推送状态
- [x] Flyway V19 d365_purchase_receipt + d365_invoice 同步表创建成功
- [x] D365 Mock 服务可启动并响应 OAuth2 + PO/receipt/invoice CRUD

### Task 11: FP 真实对接
- [x] FpIntegrationServiceImpl OAuth2 真实实现，指数退避重试 1/2/4/8/16min
- [x] 发票 OCR 识别接口调用 FP API 并回填发票号/金额/税额
- [x] 付款状态回调接口接收 FP 回调并更新结算单 payment_status
- [x] FP 健康检查接口 + 推送状态全量记录（IntegrationLog 完整 request/response）
- [x] FP Mock 服务可启动并响应 OAuth2 + 结算推送 + 发票识别 + 付款回调

### Task 12: 致远 OA 真实对接
- [x] OaIntegrationServiceImpl OAuth2 真实实现 + 票据缓存与自动续期
- [x] pushTodo/completeTodo/transferTask 三个能力真实对接致远 OA API
- [x] OaTaskListener 异常重试独立于主流程事务，失败不影响工作流
- [x] OA 健康检查接口 + OA Mock 服务可启动

### Task 13: 集成健康检查面板 + Mock 编排
- [x] IntegrationHealthController 聚合三系统健康状态 + Token 有效性 + 推送统计
- [x] 前端集成健康检查面板页面（卡片式 + 历史记录 + 手动重试）
- [x] docker-compose.yml 增加 mock-d365/mock-fp/mock-oa 三个服务
- [x] application-mock.yml 配置 profile=mock 启用 Mock 模式

## Phase 4: 实时通知与消息中心

### Task 14: WebSocket 基础设施
- [x] NotificationPublisher 通过 Redis Pub/Sub 发布广播消息
- [x] NotificationSubscriber 订阅 Redis 频道并推送 WebSocket 消息
- [ ] WebSocketConfig 配置 STOMP + SockJS 端点 /ws + 心跳（待补全 @EnableWebSocketMessageBroker 配置）
- [ ] WebSocket 鉴权拦截器校验 JWT Token 与订阅权限
- [x] 前端 WebSocket 客户端自动重连 + 心跳 + 订阅通知（原生 WebSocket API + Pinia store）

### Task 15: 站内信通知中心 + 模板引擎
- [x] pms-notification 模块创建，Notification 实体/Mapper/Service/Controller 完整
- [x] multiChannelSend 并发发送四通道（站内信/WebSocket/邮件/OA），任一失败不阻塞
- [x] NotificationTemplate 模板渲染引擎（Freemarker 变量注入）生效
- [x] 前端消息中心页面（列表 + 已读/未读 + 分类 + 批量已读 + 跳转详情）
- [x] Flyway V20 pms_notification + pms_notification_template 表 + 12 个标准模板预置

### Task 16: 多通道通知触发点接入
- [x] 任务派工/委派通知接入（pms-implementation NotificationServiceImpl 已委托至通知中心）
- [ ] 里程碑延期预警接入真实通知（替代日志）
- [ ] 审批待办通知接入（双通道：OA + 站内信）
- [ ] Punch List 整改到期通知（@Scheduled 扫描 deadline 临近项）
- [ ] 质保期到期/RMA 状态变更/结算审批通知接入

## Phase 5: 文件与附件管理

### Task 17: 统一附件存储 + 元数据
- [x] pms-file 模块创建，依赖 pms-common + OSS/MinIO SDK
- [x] StorageService 接口 + Local/Oss/Minio 三个实现，@ConditionalOnProperty 切换
- [x] Attachment 实体/Mapper/Service/Controller 完整
- [x] FileController 上传/下载/删除接口（multipart + 流式下载）
- [x] Flyway V21 pms_attachment 表创建成功

### Task 18: 业务附件能力
- [x] 照片 GPS EXIF 解析 + 围栏 500m 比对 + 异常标记
- [x] 文件预览：图片缩略图生成（Thumbnailator）
- [x] Punch List 缺陷照片附件（前端 FileUploader 组件 + bizType=PUNCH_LIST）
- [x] 终验交付物附件关联（前端 FileUploader 组件 + bizType=DELIVERABLE）
- [ ] RMA 故障件照片附件关联（后端实体字段预留，前端待接入）
- [ ] 实施照片附件（ImplProgress 关联多图）
- [ ] PDF 在线预览 + Office 预览（可选）

### Task 19: Excel 批量导入导出
- [ ] ExcelUtils 工具类（模板下载/导入/导出/校验/错误报告）
- [ ] 资产批量导入（模板下载 + 上传解析 + 校验 + 入库 + 错误报告）
- [ ] 里程碑批量导入
- [ ] 结算明细导出 + 报表导出（delivery/asset/implementation）
- [ ] 前端导入导出组件封装（上传 + 模板下载 + 错误报告下载 + 导出按钮）

## Phase 6: 前端 UX 补全

### Task 20: TagsView + 移动端 + 加载态
- [x] TagsView 多标签页（标签栏 + 右键菜单 + localStorage 持久化 + 路由联动）
- [x] DefaultLayout 集成 TagsView
- [ ] 移动端响应式适配（栅格 + 抽屉侧边栏 + 触摸友好）
- [ ] 全局 loading 指令 + 表单异步校验 + 防抖搜索

### Task 21: 文件上传 + 通知下拉 + 实时 Toast
- [x] FileUploader.vue 拖拽上传 + 进度条 + 缩略图
- [x] NotificationBell.vue 未读数 badge + 下拉列表 + 跳转消息中心 + 标记已读
- [x] 实时消息 Toast（WebSocket 推送 → ElNotification + 点击跳转）
- [x] 全局 WebSocket 连接管理（Pinia store + 自动重连 + 在线状态）

### Task 22: 新业务页面
- [x] Punch List 管理页面（缺陷列表 + 三级筛选 + 流程 + 照片）
- [x] RMA 返修管理页面（列表 + 6 步进度条 + 照片 + KPI 卡片）
- [x] 风险登记册页面（5x5 矩阵热力图 + 列表 + 复审 + 转问题）
- [x] 变更管理页面（CR 列表 + 影响评估 + CCB 审批 + 基线时间线）
- [x] 问题日志页面（列表 + 分派 + 状态流转 + 源关联）
- [x] 质保期管理页面（列表 + 预警日历 + 续保/退网 + SLA）
- [x] 终验交付物清单页面（8 项 checklist + 上传 + 校验 + 下载）
- [x] 消息中心页面（列表 + 已读/未读 + 分类 + 批量已读 + 跳转）
- [x] 集成健康检查面板页面（卡片 + 历史记录 + 手动重试）
- [x] 路由配置 + 菜单配置更新（新增业务菜单分组）

## Phase 7: 运维与可观测性

### Task 23: SpringDoc + Druid + 定时任务监控
- [x] SpringDoc OpenAPI 配置完成，/swagger-ui.html 可访问
- [x] API 分组生效（system/project/asset/implementation/workflow/integration/file/notification/governance）
- [x] 全局 JWT 鉴权配置（SecurityScheme bearerAuth）
- [ ] Druid SQL 监控面板可访问，慢 SQL 告警生效，SQL 防火墙启用
- [x] ScheduleLog 实体 + Mapper + Service 创建完成
- [ ] 定时任务监控：失败告警 + 手动触发接口

### Task 24: Redis 缓存 + 性能优化 + 审计增强
- [x] RedisConfig 配置 RedisTemplate + CacheManager（TTL 30min + 随机抖动防雪崩）
- [x] 命名缓存 sysDict/sysMenu/sysConfig/sysRole（TTL 60min）
- [ ] SysDict/SysMenu/SysConfig/SysRole @Cacheable 注解接入
- [ ] 缓存管理面板（手动清除）
- [ ] 核心表索引优化（复合索引）+ 慢查询日志分析
- [x] LoginLog + ExceptionLog + ScheduleLog 实体 + 统一审计面板 Controller
- [x] Flyway V22 sys_login_log + sys_exception_log + sys_schedule_log 表创建成功

## Phase 8: 全面质量保障

### Task 25: 单元测试 + Testcontainers 集成测试
- [ ] pms-project Service 测试覆盖率 ≥70%（12 节点 + Punch List + 终验交付物）— 用户指示跳过
- [ ] pms-asset Service 测试覆盖率 ≥70%（9 状态机 + RMA + 质保 + 序列化）— 用户指示跳过
- [ ] pms-implementation Service 测试覆盖率 ≥70%（派工单 + 代理商认证 + 结算 FP）— 用户指示跳过
- [ ] pms-governance Service 测试覆盖率 ≥70%（三本账联动）— 用户指示跳过
- [ ] pms-notification/pms-file Service 测试覆盖率 ≥70% — 用户指示跳过
- [ ] @Disabled 集成测试修复：Testcontainers（MySQL/Redis/Flowable）真实联调通过 — 用户指示跳过
- [ ] WebSocket/文件上传/Excel 导入导出集成测试通过 — 用户指示跳过
- [ ] 前端 Vitest 测试补全 — 用户指示跳过

### Task 26: CI + SonarQube
- [x] .github/workflows/ci.yml PR 触发流水线（编译 + 测试 + 前端构建 + SonarQube + 镜像构建）
- [x] sonar-project.properties 配置 + 质量门禁（覆盖率 ≥70% + 重复率 ≤3% + 严重漏洞 = 0 + 圈复杂度 ≤15）
- [x] docker-compose.yml 增加 sonarqube 服务
- [x] .github/workflows/deploy.yml main 分支部署流水线（占位）
- [x] README.md 补充 CI/CD 流程说明 + 质量门禁规则 + 本地开发指南
