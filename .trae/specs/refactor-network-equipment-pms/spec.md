# 网络设备工程项目管理系统重构 Spec

## Why

现有 PMS 系统基于遗留技术栈（Struts2 2.3.35 + iBATIS 2.x + JDK 1.8），存在双 Web 框架并存、双 ORM 并存、非标准源码目录等技术债务，维护与扩展成本极高。业务上需要聚焦网络设备工程实施场景，围绕"项目交付进度、设备资产、原厂实施与代理商实施"三大核心进行重构，实现从项目立项到终验交付的全生命周期管理，提升交付效率与资产管控能力。

## What Changes

### 技术栈全面升级
- 后端：Spring Boot 3.x + Spring Security 6 + MyBatis-Plus 3.5.x + Flowable 7.x（JDK 17）
- 前端：Vue 3 + Vite + Element Plus + TypeScript + Pinia
- 缓存：Redis 7.x
- 数据库：MySQL 8.x
- 构建：Maven 多模块

### 架构重构
- 废弃双 Web 框架（Struts2 / Spring MVC）并存架构，统一为 Spring Boot 单体应用
- 废弃双 ORM（iBATIS / MyBatis）并存架构，统一为 MyBatis-Plus
- 废弃 war overlay 机制与 classifier 约定，改为标准 Maven 模块依赖
- 废弃 XML 配置驱动的 Spring/Shiro，改为注解 + 自动配置驱动

### 领域重构
- 围绕三大核心域重新组织业务模块：项目交付进度、设备资产、实施管理
- 构建从项目立项→计划→执行→里程碑跟踪→终验交付的完整生命周期流程
- 新增设备资产管理域：设备目录、设备分配与调拨、设备跟踪、设备生命周期
- 重构实施管理域：统一管理原厂实施与代理商实施，含任务分配、进度跟踪、结算、质量控制

### 废弃项
- **BREAKING**：废弃 PMS-struts 模块（Struts2 2.3.35 + iBATIS 全部业务）
- **BREAKING**：废弃 PMS-springmvc 双 Profile（pms2/pms3）机制
- **BREAKING**：废弃 core 模块的 war+jar 双产出模式
- **BREAKING**：废弃 PMS-security 独立 jar 模块（安全能力内置于系统模块）
- **BREAKING**：废弃 pms-rules 独立 jar 模块（规则引擎能力按需内嵌）

## Impact

- **受影响代码**：全部 8 个现有模块（core、PMS-struts、PMS-springmvc、PMS-activiti、PMS-security、PMS-ext-d365、pms-ext-fp、pms-rules）均需重写或废弃
- **受影响数据库**：MySQL `dppms_d365`，需新建重构后的表结构并迁移核心业务数据
- **受影响集成**：D365 ERP、FP 财务平台、致远 OA、SMS、EHR 人事系统
- **受影响用户**：所有 PMS 系统用户（项目管理员、实施工程师、代理商、审批人）

## ADDED Requirements

### Requirement: 现代化架构基座

系统 SHALL 基于 Spring Boot 3.x 构建单体应用，采用 Maven 多模块组织，提供统一的配置管理、安全认证、异常处理、日志记录与 API 规范。

#### Scenario: 应用启动
- **WHEN** 开发者执行 `mvn spring-boot:run` 或部署 jar 包
- **THEN** 应用在 30 秒内启动完成，自动执行数据库迁移脚本，暴露 RESTful API

#### Scenario: 统一 API 响应
- **WHEN** 前端调用任意 API 接口
- **THEN** 返回统一的 JSON 结构 `{code, message, data}`，HTTP 状态码语义正确

#### Scenario: 全局异常处理
- **WHEN** 业务逻辑抛出异常
- **THEN** 全局异常处理器捕获并转换为统一错误响应，记录错误日志，不向前端暴露堆栈信息

### Requirement: 项目立项管理

系统 SHALL 提供项目立项功能，支持创建网络设备工程项目，录入项目基本信息、客户信息、合同信息与设备清单。

#### Scenario: 创建项目立项
- **WHEN** 用户填写项目名称、客户名称、合同编号、项目类型、计划开始/结束日期并提交
- **THEN** 系统创建项目记录，状态为"待审批"，触发立项审批工作流

#### Scenario: 立项审批通过
- **WHEN** 审批人在工作流中批准立项申请
- **THEN** 项目状态变更为"已立项"，自动生成项目编号，创建项目交付计划模板

### Requirement: 项目交付进度管理

系统 SHALL 提供项目交付进度管理功能，支持项目里程碑定义、进度跟踪、延期预警与交付看板。

#### Scenario: 定义项目里程碑
- **WHEN** 项目经理在已立项项目中创建里程碑（如：到货、安装、调试、初验、终验）
- **THEN** 系统生成里程碑记录，关联到项目交付计划，设置计划完成日期

#### Scenario: 更新里程碑进度
- **WHEN** 实施人员更新某里程碑的实际完成日期与完成情况
- **THEN** 系统自动计算进度百分比，更新项目整体进度，若延期则触发预警通知

#### Scenario: 交付看板展示
- **WHEN** 用户查看交付看板
- **THEN** 系统按项目状态（立项/执行中/初验/终验/关闭）分组展示项目卡片，高亮延期项目

### Requirement: 项目终验交付管理

系统 SHALL 提供项目终验交付功能，支持终验申请、验收报告生成、交付物确认与项目关闭。

#### Scenario: 提交终验申请
- **WHEN** 项目经理在所有里程碑完成后提交终验申请
- **THEN** 系统创建终验记录，触发终验审批工作流，生成验收报告草稿

#### Scenario: 终验通过并关闭项目
- **WHEN** 终验审批通过
- **THEN** 项目状态变更为"已终验"，归档项目文档，释放项目关联的设备资产占用

### Requirement: 设备目录管理

系统 SHALL 提供网络设备目录管理功能，支持设备分类、设备型号、设备规格参数的维护。

#### Scenario: 维护设备分类树
- **WHEN** 管理员创建/编辑设备分类（如：交换机、路由器、防火墙、负载均衡）
- **THEN** 系统更新分类树，支持多级分类与排序

#### Scenario: 录入设备型号
- **WHEN** 管理员录入设备型号、品牌、规格参数、标准单价
- **THEN** 系统创建设备型号记录，关联到设备分类，可供项目设备清单引用

### Requirement: 设备资产管理

系统 SHALL 提供设备资产管理功能，支持设备入库、项目分配、调拨、回收与全生命周期跟踪。

#### Scenario: 设备入库
- **WHEN** 仓库管理员录入设备序列号、型号、入库数量
- **THEN** 系统创建设备资产记录，状态为"在库"，记录入库时间与库位

#### Scenario: 设备分配到项目
- **WHEN** 项目经理为项目申请设备，选择设备型号与数量
- **THEN** 系统创建设备分配记录，扣减在库库存，设备状态变更为"已分配"，关联到项目

#### Scenario: 设备调拨
- **WHEN** 用户发起设备调拨申请（从项目 A 调拨到项目 B）
- **THEN** 系统创建调拨记录，触发调拨审批工作流，审批通过后更新设备归属项目

#### Scenario: 设备回收
- **WHEN** 项目终验后系统自动或用户手动回收设备
- **THEN** 设备状态变更为"在库"，解除项目关联，记录回收时间

#### Scenario: 设备生命周期查询
- **WHEN** 用户通过序列号查询设备
- **THEN** 系统展示设备完整生命周期轨迹（入库→分配→调拨→回收），含每次变更的时间与操作人

### Requirement: 原厂实施管理

系统 SHALL 提供原厂（设备厂商）实施管理功能，支持原厂实施任务分配、进度跟踪与成果确认。

#### Scenario: 分配原厂实施任务
- **WHEN** 项目经理为项目某里程碑创建原厂实施任务，选择原厂工程师、计划工期
- **THEN** 系统创建实施任务记录，通知原厂工程师，任务状态为"待接单"

#### Scenario: 原厂工程师上报进度
- **WHEN** 原厂工程师更新实施任务进度（完成百分比、工作日志、现场照片）
- **THEN** 系统记录进度快照，更新任务状态，关联到项目里程碑进度

#### Scenario: 原厂实施成果确认
- **WHEN** 原厂工程师标记任务完成，项目经理确认验收
- **THEN** 任务状态变更为"已完成"，记录验收结果与验收意见

### Requirement: 代理商实施管理

系统 SHALL 提供代理商实施管理功能，支持代理商选择、实施任务委派、进度跟踪、结算与质量评价。

#### Scenario: 委派代理商实施任务
- **WHEN** 项目经理选择代理商并委派实施任务，约定工作量与结算金额
- **THEN** 系统创建代理商实施任务记录，生成委派单，通知代理商

#### Scenario: 代理商上报实施进度
- **WHEN** 代理商通过系统上报实施进度与完工情况
- **THEN** 系统记录进度，更新任务状态，项目经理可查看实时进度

#### Scenario: 代理商实施结算
- **WHEN** 代理商实施任务验收通过后，系统生成结算单
- **THEN** 结算单包含工作量明细、金额、税率，触发结算审批工作流，审批通过后推送至财务平台

#### Scenario: 代理商质量评价
- **WHEN** 项目经理对已完成的代理商实施任务进行质量评分（响应速度、施工质量、文档完备性）
- **THEN** 系统记录评价，更新代理商综合评分，供后续代理商选择参考

### Requirement: 统一工作流引擎

系统 SHALL 基于 Flowable 7.x 提供统一的工作流引擎，支持立项审批、终验审批、设备调拨审批、结算审批等流程的可视化设计与流转。

#### Scenario: 流程设计与部署
- **WHEN** 管理员通过流程设计器设计 BPMN 流程并部署
- **THEN** 系统解析流程定义，存储到流程仓库，供业务流程引用

#### Scenario: 发起与审批流程
- **WHEN** 业务系统调用流程发起 API
- **THEN** 系统创建流程实例，按流程定义自动流转到下一步审批节点，通知待办人

#### Scenario: 待办与已办查询
- **WHEN** 用户查询待办任务
- **THEN** 系统返回当前用户的待办任务列表，支持按流程类型、项目名称筛选

### Requirement: 数据报表与分析

系统 SHALL 提供项目交付、设备资产、实施管理的统计分析报表，支持图表可视化与数据导出。

#### Scenario: 项目交付统计
- **WHEN** 管理员查看项目交付报表
- **THEN** 系统展示按月/季度的项目立项数、完成数、平均交付周期、延期率等指标图表

#### Scenario: 设备资产统计
- **WHEN** 管理员查看设备资产报表
- **THEN** 系统展示设备在库/已分配/总数分布、设备利用率、设备周转率等指标图表

#### Scenario: 实施效能分析
- **WHEN** 管理员查看实施效能报表
- **THEN** 系统展示原厂与代理商的实施任务完成率、平均工期、质量评分对比图表

### Requirement: 外部系统集成

系统 SHALL 保留与 D365 ERP、FP 财务平台的外部集成能力，通过统一的集成适配层实现。

#### Scenario: D365 采购订单同步
- **WHEN** 系统检测到设备入库需要同步至 D365
- **THEN** 集成适配层调用 D365 API 推送采购收货数据，记录同步结果

#### Scenario: FP 结算单推送
- **WHEN** 代理商实施结算审批通过
- **THEN** 集成适配层将结算单推送至 FP 财务平台，记录推送状态与响应

## MODIFIED Requirements

### Requirement: 用户与权限管理

系统 SHALL 基于 Spring Security 6 提供用户认证与 RBAC 授权，支持公司维度与数据权限控制，替代原有 Shiro 实现。

- 认证方式：本地账号密码（BCrypt）+ 可选 CAS 单点登录
- 权限模型：用户→角色→权限（菜单权限 + API 权限 + 数据权限）
- 数据权限：按公司/部门维度控制数据可见范围
- 会话管理：基于 Redis 的分布式会话，替代原 MemorySessionDAO

### Requirement: 系统基础管理

系统 SHALL 提供菜单管理、角色管理、字典管理、系统参数配置、操作日志功能，保留原有核心能力但基于新技术栈重写。

## REMOVED Requirements

### Requirement: Struts2 遗留 Web 应用
**Reason**: Struts2 2.3.35 已停止维护，存在已知安全漏洞，且与 Spring MVC 并存导致架构复杂
**Migration**: 所有 Struts2 Action 的业务逻辑迁移至 Spring Boot RESTful Controller，前端迁移至 Vue 3 SPA

### Requirement: iBATIS 持久层
**Reason**: iBATIS 2.x 已停止维护多年，与现代 MyBatis-Plus 能力差距大
**Migration**: 所有 iBATIS SqlMap XML 映射迁移为 MyBatis-Plus Mapper XML + 实体注解

### Requirement: war overlay 部署机制
**Reason**: war overlay 机制复杂、构建缓慢，不符合现代容器化部署趋势
**Migration**: 改为 Spring Boot 可执行 jar，前端独立构建部署为静态资源或 CDN

### Requirement: PMS-security 独立安全模块
**Reason**: 安全能力（XSS/CSRF/SQL 防护）应内置于框架，无需独立模块
**Migration**: XSS/CSRF 防护迁移至 Spring Security 过滤链与全局拦截器

### Requirement: pms-rules 独立规则引擎模块
**Reason**: Aviator 规则引擎使用场景有限，独立模块增加维护成本
**Migration**: 按需将规则引擎能力内嵌到使用方模块，或引入轻量级表达式求值库
