# Tasks

## Phase 1: 架构基座搭建

- [x] Task 1: 创建 Spring Boot 多模块项目骨架
  - [x] SubTask 1.1: 创建父 pom.xml，声明 Spring Boot 3.x parent 与公共依赖版本管理
  - [x] SubTask 1.2: 创建 pms-common 模块（统一响应体 Result、全局异常处理、枚举常量、工具类）
  - [x] SubTask 1.3: 创建 pms-system 模块（用户、角色、权限、菜单、字典、日志）
  - [x] SubTask 1.4: 创建 pms-admin 启动模块（Spring Boot Application、配置文件、数据库迁移脚本入口）
  - [x] SubTask 1.5: 配置 MyBatis-Plus、Redis、MySQL 连接池（HikariCP）

- [x] Task 2: 实现用户认证与权限管理（Spring Security 6）
  - [x] SubTask 2.1: 实现用户登录/登出接口，BCrypt 密码加密，JWT Token 签发与校验
  - [x] SubTask 2.2: 实现 RBAC 权限模型（用户→角色→权限），菜单权限 + API 权限注解
  - [x] SubTask 2.3: 实现数据权限拦截器，按公司/部门维度控制数据可见范围
  - [x] SubTask 2.4: 实现基于 Redis 的分布式会话管理

- [x] Task 3: 实现系统基础管理功能
  - [x] SubTask 3.1: 实现菜单管理 CRUD（树形结构、动态路由）
  - [x] SubTask 3.2: 实现角色管理 CRUD（角色-权限分配）
  - [x] SubTask 3.3: 实现字典管理 CRUD（字典类型与字典项）
  - [x] SubTask 3.4: 实现系统参数配置与操作日志 AOP 切面

## Phase 2: 项目交付进度管理（核心域一）

- [x] Task 4: 设计并创建项目域数据库表结构
  - [x] SubTask 4.1: 创建项目主表（pms_project）、项目成员表（pms_project_member）、项目客户表（pms_project_customer）
  - [x] SubTask 4.2: 创建里程碑表（pms_milestone）、交付计划表（pms_delivery_plan）
  - [x] SubTask 4.3: 创建终验记录表（pms_final_acceptance）、交付物表（pms_deliverable）
  - [x] SubTask 4.4: 编写 Flyway 迁移脚本

- [x] Task 5: 实现项目立项管理
  - [x] SubTask 5.1: 实现项目立项 CRUD 接口（创建、查询、详情、编辑、删除）
  - [x] SubTask 5.2: 实现项目编号自动生成规则
  - [x] SubTask 5.3: 实现立项审批工作流集成（调用 Flowable 发起流程）

- [x] Task 6: 实现项目交付进度管理
  - [x] SubTask 6.1: 实现里程碑 CRUD 与计划日期设置
  - [x] SubTask 6.2: 实现里程碑进度更新与自动进度百分比计算
  - [x] SubTask 6.3: 实现延期预警逻辑（对比计划日期与当前日期，触发通知）
  - [x] SubTask 6.4: 实现交付看板接口（按状态分组查询项目列表）

- [x] Task 7: 实现项目终验交付管理
  - [x] SubTask 7.1: 实现终验申请接口（校验所有里程碑完成，创建终验记录）
  - [x] SubTask 7.2: 实现验收报告草稿自动生成
  - [x] SubTask 7.3: 实现终验审批通过后项目关闭逻辑（状态变更、文档归档、设备释放）

## Phase 3: 设备资产管理（核心域二）

- [x] Task 8: 设计并创建设备域数据库表结构
  - [x] SubTask 8.1: 创建设备分类表（pms_asset_category）、设备型号表（pms_asset_model）
  - [x] SubTask 8.2: 创建设备资产表（pms_asset）、设备分配表（pms_asset_allocation）
  - [x] SubTask 8.3: 创建设备调拨表（pms_asset_transfer）、设备生命周期日志表（pms_asset_lifecycle_log）
  - [x] SubTask 8.4: 编写 Flyway 迁移脚本

- [x] Task 9: 实现设备目录管理
  - [x] SubTask 9.1: 实现设备分类树 CRUD（多级分类、排序）
  - [x] SubTask 9.2: 实现设备型号 CRUD（品牌、规格参数、标准单价）

- [x] Task 10: 实现设备资产管理
  - [x] SubTask 10.1: 实现设备入库接口（序列号、型号、数量、库位）
  - [x] SubTask 10.2: 实现设备分配到项目接口（扣减库存、关联项目、状态变更）
  - [x] SubTask 10.3: 实现设备调拨接口（发起调拨申请、调拨审批工作流、审批通过更新归属）
  - [x] SubTask 10.4: 实现设备回收接口（项目终验自动回收、手动回收、状态变更、解除关联）
  - [x] SubTask 10.5: 实现设备生命周期查询接口（按序列号查询完整变更轨迹）

## Phase 4: 实施管理（核心域三）

- [x] Task 11: 设计并创建实施域数据库表结构
  - [x] SubTask 11.1: 创建实施任务表（pms_impl_task）、实施进度表（pms_impl_progress）
  - [x] SubTask 11.2: 创建代理商表（pms_agent）、代理商评分表（pms_agent_score）
  - [x] SubTask 11.3: 创建结算单表（pms_settlement）、结算明细表（pms_settlement_detail）
  - [x] SubTask 11.4: 编写 Flyway 迁移脚本

- [x] Task 12: 实现原厂实施管理
  - [x] SubTask 12.1: 实现原厂实施任务分配接口（关联里程碑、选择工程师、计划工期）
  - [x] SubTask 12.2: 实现实施进度上报接口（完成百分比、工作日志、现场照片上传）
  - [x] SubTask 12.3: 实现实施成果确认接口（项目经理验收、记录验收意见）

- [x] Task 13: 实现代理商实施管理
  - [x] SubTask 13.1: 实现代理商管理 CRUD（代理商基础信息、资质、联系方式）
  - [x] SubTask 13.2: 实现代理商实施任务委派接口（生成委派单、约定工作量与金额）
  - [x] SubTask 13.3: 实现代理商进度上报接口
  - [x] SubTask 13.4: 实现代理商实施结算接口（生成结算单、结算审批工作流、审批通过推送 FP）
  - [x] SubTask 13.5: 实现代理商质量评价接口（多维度评分、综合评分计算）

## Phase 5: 工作流引擎与外部集成

- [x] Task 14: 集成 Flowable 7.x 工作流引擎
  - [x] SubTask 14.1: 引入 Flowable 依赖，配置流程引擎与数据源
  - [x] SubTask 14.2: 实现 BPMN 流程部署与管理接口（部署、查询、删除）
  - [x] SubTask 14.3: 实现流程发起、审批、撤回、转办接口
  - [x] SubTask 14.4: 实现待办/已办任务查询接口
  - [x] SubTask 14.5: 设计并部署核心业务流程定义（立项审批、终验审批、设备调拨审批、结算审批）

- [x] Task 15: 实现外部系统集成适配层
  - [x] SubTask 15.1: 实现 D365 集成适配器（OAuth2 Token 管理、采购收货数据推送、结果回填）
  - [x] SubTask 15.2: 实现 FP 财务平台集成适配器（结算单推送、发票识别、推送状态记录）
  - [x] SubTask 15.3: 实现统一集成日志记录与重试机制

## Phase 6: 前端开发

- [x] Task 16: 搭建 Vue 3 前端项目骨架
  - [x] SubTask 16.1: 初始化 Vite + Vue 3 + TypeScript 项目，配置 Element Plus、Pinia、Vue Router
  - [x] SubTask 16.2: 实现统一请求封装（Axios 拦截器、Token 注入、错误处理）
  - [x] SubTask 16.3: 实现基础布局（侧边栏菜单、顶部导航、面包屑、标签页）

- [x] Task 17: 实现系统管理前端页面
  - [x] SubTask 17.1: 实现登录页与权限路由守卫
  - [x] SubTask 17.2: 实现用户管理、角色管理、菜单管理页面
  - [x] SubTask 17.3: 实现字典管理与系统参数配置页面

- [x] Task 18: 实现项目交付进度管理前端页面
  - [x] SubTask 18.1: 实现项目立项表单与立项列表页
  - [x] SubTask 18.2: 实现项目详情页（里程碑管理、进度跟踪、甘特图）
  - [x] SubTask 18.3: 实现交付看板页（拖拽卡片、状态分组、延期高亮）
  - [x] SubTask 18.4: 实现终验交付页面（终验申请、验收报告、交付物确认）

- [x] Task 19: 实现设备资产管理前端页面
  - [x] SubTask 19.1: 实现设备分类树与设备型号管理页面
  - [x] SubTask 19.2: 实现设备入库与设备列表页面
  - [x] SubTask 19.3: 实现设备分配、调拨、回收页面
  - [x] SubTask 19.4: 实现设备生命周期轨迹详情页

- [x] Task 20: 实现实施管理前端页面
  - [x] SubTask 20.1: 实现原厂实施任务管理页面（任务列表、进度上报、成果确认）
  - [x] SubTask 20.2: 实现代理商管理页面（代理商列表、评分查看）
  - [x] SubTask 20.3: 实现代理商实施任务与结算管理页面
  - [x] SubTask 20.4: 实现工作流待办中心页面（待办列表、审批操作、流程图查看）

- [x] Task 21: 实现数据报表前端页面
  - [x] SubTask 21.1: 实现项目交付统计报表（ECharts 图表、日期筛选、数据导出）
  - [x] SubTask 21.2: 实现设备资产统计报表
  - [x] SubTask 21.3: 实现实施效能分析报表

## Phase 7: 测试与部署

- [x] Task 22: 编写后端单元测试与集成测试
  - [x] SubTask 22.1: 编写三大核心域 Service 层单元测试（JUnit 5 + Mockito）
  - [x] SubTask 22.2: 编写 Controller 层集成测试（Spring Boot Test + MockMvc）
  - [x] SubTask 22.3: 编写工作流流程测试（Flowable ProcessEngineRule）

- [x] Task 23: 编写前端测试与端到端验证
  - [x] SubTask 23.1: 编写核心组件单元测试（Vitest）
  - [x] SubTask 23.2: 编写关键业务流程端到端测试（立项→执行→终验全链路）

- [x] Task 24: 配置部署与 CI/CD
  - [x] SubTask 24.1: 编写 Dockerfile（后端 jar + 前端 nginx 静态资源）
  - [x] SubTask 24.2: 编写 docker-compose.yml（MySQL + Redis + 后端 + 前端）
  - [x] SubTask 24.3: 编写数据库初始化脚本与 Flyway 迁移脚本完整集

## Phase 8: 业务逻辑与系统集成修复（基于 checklist 验证）

> 背景：用户强调"相关业务功能的逻辑要尽可能保持原系统功能"。Checklist 系统验证发现多项关键业务联动断链（工作流未触发、终验未释放资产、结算未推送 FP）、原系统工作流特性缺失（相同处理人跳过、终止结束事件、OA 推送）、权限/系统管理基础能力不完整、报表用 Mock 数据等问题。

- [x] Task 25: 修复认证授权与系统管理基础能力（Phase 1 用户认证 + 系统基础管理）
  - [x] SubTask 25.1: 在 SecurityConfig 加 @EnableMethodSecurity，在 pms-system Controller 关键方法（用户/角色/菜单/字典 CRUD）加 @PreAuthorize；JwtAuthenticationFilter 改为从 DB 加载用户角色/权限（替代硬编码 ROLE_USER）
  - [x] SubTask 25.2: 实现 DataPermissionInterceptor（按 company_id/dept_id 拼接 SQL 条件）并在 MyBatisPlusConfig 中注册
  - [x] SubTask 25.3: 引入 RedisTemplate，AuthController.logout 实现 Token 黑名单（jti + 过期时间），JwtAuthenticationFilter 校验黑名单
  - [x] SubTask 25.4: SysMenuServiceImpl 实现 listMenusByUserId（关联 sys_role_menu/sys_user_role），新增 buildTree 与 /api/system/menu/tree 接口供前端动态路由
  - [x] SubTask 25.5: SysRoleController 新增 POST /api/system/role/{id}/menus 接口保存角色-菜单关联（清空+批量插入 SysRoleMenu）
  - [x] SubTask 25.6: SysDictController 补充 SysDictItem 的 POST/PUT/DELETE 接口（字典项级 CRUD）
  - [x] SubTask 25.7: 新增 SysConfig 实体/Mapper/Service/Controller（按 configKey 动态读取与更新），新增 sys_config 表与 V6 迁移脚本
  - [x] SubTask 25.8: 在 pms-system 关键 Controller 方法（用户/角色/菜单/字典 CRUD）标注 @OperLog 注解

- [x] Task 26: 修复项目域业务联动（Phase 2）
  - [x] SubTask 26.1: pms-project 引入 pms-workflow 依赖；ProjectServiceImpl.createProject 末尾调用 WorkflowService.startProcess("projectApproval", businessKey=projectId, variables={pmUserId, deptManagerUserId})，回写 processInstanceId 到 project 表（需新增字段）
  - [x] SubTask 26.2: 修复 createProject 不应在创建时生成 projectCode（移除第 50 行），仅在 approveProject 中生成
  - [x] SubTask 26.3: 新增 MilestoneOverdueScheduler (@Scheduled 每日 02:00)，扫描 plan_date < CURDATE() AND status NOT IN (COMPLETED, OVERDUE) 的里程碑，置为 OVERDUE，并通过 pms-integration 推送 OA 待办/消息给项目经理
  - [x] SubTask 26.4: 修复 ProjectServiceImpl.dashboard：改为返回 Map<String, List<Project>> 按 status 分组（一次查询后 Java 内分组），与前端 ProjectDashboard 契约对齐
  - [x] SubTask 26.5: pms-project 引入 pms-asset 依赖（或通过 Spring 事件解耦）；FinalAcceptanceServiceImpl.approve 在项目置 COMPLETED 后发布 FinalAcceptanceApprovedEvent（含 projectId），由 pms-asset 中 @EventListener 消费并执行批量回收

- [x] Task 27: 修复设备调拨工作流与终验资产回收（Phase 3）
  - [x] SubTask 27.1: pms-asset 引入 pms-workflow 依赖；AssetTransferServiceImpl.apply 注入 WorkflowService，构造 StartProcessRequest(processDefinitionKey="assetTransfer", businessKey=transferId, variables={fromPmUserId, toPmUserId, assetId}) 调用 startProcess；transfer 表新增 process_instance_id 字段（V8 迁移脚本）
  - [x] SubTask 27.2: AssetTransferServiceImpl.approve/reject 中通过 processInstanceId 查询当前 task 并调用 completeTask（通过/驳回），审批通过后保留原归属更新逻辑
  - [x] SubTask 27.3: 在 IAssetService 新增真正的批量回收方法 recycleByProject(Long projectId)，内部循环调用 returnAsset 并写日志；AssetServiceImpl.returnByProject 改为调用 recycleByProject 或保留为查询方法重命名
  - [x] SubTask 27.4: 创建 FinalAcceptanceApprovedEventListener（@EventListener）在 pms-asset 模块，监听 FinalAcceptanceApprovedEvent 并调用 assetService.recycleByProject

- [x] Task 28: 修复实施管理通知与结算推送链路（Phase 4）
  - [x] SubTask 28.1: 在 pms-implementation 引入 pms-integration 依赖；新增 NotificationService（站内信/消息）桩接口，assignOemTask 与 assignAgentTask 后调用通知服务向对应 userId 发送派工/委派通知
  - [x] SubTask 28.2: 在 ImplTaskServiceImpl.confirmTask/rejectTask 中填充 acceptUserId/acceptUserName（调用 SecurityUtils.getCurrentUserId/getCurrentUsername）
  - [x] SubTask 28.3: pms-implementation 引入 pms-workflow 依赖；SettlementServiceImpl.createSettlement 末尾调用 WorkflowService.startProcess("settlementApproval", businessKey=settlementNo)，回写 processInstanceId
  - [x] SubTask 28.4: pms-implementation 引入 pms-integration 依赖；SettlementServiceImpl.approve 注入 FpIntegrationService，approve 后组装 SettlementPushRequest 调用 pushSettlement，据返回结果回写 settlement 的 pushStatus/pushTime/pushResponse；删除 pushToFp 占位实现

- [x] Task 29: 恢复原系统工作流特性（用户业务约束：相同处理人跳过、终止结束事件、OA 推送）
  - [x] SubTask 29.1: 为 4 个 BPMN 文件（project-approval、final-acceptance、asset-transfer、settlement-approval）的 userTask 添加 skipExpression="${execution.assignee == task.assignee}" 实现"相同处理人自动跳过"（Flowable 7 支持 _ACTIVITI_SKIP_EXPRESSION_ENABLED 变量）
  - [x] SubTask 29.2: 为驳回分支添加 terminateEndEventDefinition（终止结束事件），使驳回时立即结束流程而非继续流转
  - [x] SubTask 29.3: 新增 OaIntegrationService（位于 pms-integration，复用 IntegrationLog 框架，OAuth2 + 推送致远 OA 待办接口）；在 pms-workflow 新增 OaTaskListener（TaskListener create 事件）调用 OaIntegrationService 推送待办；在 4 个 BPMN userTask 上挂 TaskListener

- [x] Task 30: 修复前端报表与端到端测试（Phase 6 + Phase 7）
  - [x] SubTask 30.1: 新增后端报表 API（/api/report/delivery、/api/report/asset、/api/report/implementation）查询真实统计数据；前端 src/views/report/index.vue 改为 onMounted 调用 API 替换 Mock 数据；新增"导出 Excel"按钮（使用 xlsx 库或后端导出接口）
  - [x] SubTask 30.2: e2e/project-lifecycle.test.ts 移除 it.skip，引入 supertest 或 axios 实现真实 8 步全链路测试（登录→创建项目→立项审批→创建里程碑→更新进度→申请终验→审批终验→验证项目状态 COMPLETED）；测试需连接 docker-compose 启动的后端，可保留 skip 标记但需文档说明运行方式
  - [x] SubTask 30.3: 修复 Dockerfile.backend：删除不存在的模块目录 COPY 行（pms-system/pms-project/pms-implementation/pms-workflow/pms-integration），核实后端实际模块结构（仅 pms-common/pms-asset/pms-admin 或补全缺失模块目录）

# Task Dependencies

- Task 2 depends on Task 1
- Task 3 depends on Task 1
- Task 4 depends on Task 1
- Task 5 depends on Task 4, Task 14
- Task 6 depends on Task 4
- Task 7 depends on Task 6, Task 14
- Task 8 depends on Task 1
- Task 9 depends on Task 8
- Task 10 depends on Task 8, Task 14, Task 7
- Task 11 depends on Task 1
- Task 12 depends on Task 11, Task 6
- Task 13 depends on Task 11, Task 14, Task 15
- Task 14 depends on Task 1
- Task 15 depends on Task 1
- Task 16 depends on Task 1
- Task 17 depends on Task 16, Task 2, Task 3
- Task 18 depends on Task 16, Task 5, Task 6, Task 7
- Task 19 depends on Task 16, Task 9, Task 10
- Task 20 depends on Task 16, Task 12, Task 13, Task 14
- Task 21 depends on Task 16, Task 6, Task 10, Task 12, Task 13
- Task 22 depends on Task 5, Task 6, Task 7, Task 9, Task 10, Task 12, Task 13
- Task 23 depends on Task 17, Task 18, Task 19, Task 20
- Task 24 depends on Task 22, Task 23
- Task 25 depends on Task 1, Task 2, Task 3
- Task 26 depends on Task 5, Task 6, Task 7, Task 14
- Task 27 depends on Task 10, Task 14, Task 26
- Task 28 depends on Task 12, Task 13, Task 14, Task 15
- Task 29 depends on Task 14, Task 15
- Task 30 depends on Task 21, Task 23, Task 24
