# PMS系统开发知识库构建 Spec

## Why
PMS-Struts项目作为迪普科技核心项目管理系统，经过多年迭代已形成复杂的业务逻辑、多数据源集成和大量数据库表结构。当前缺乏系统性的技术文档，新团队成员上手困难，系统维护升级存在知识断层风险。需要构建一套完整的系统开发知识库，为后续维护升级、功能迭代及团队培训提供权威参考。

## What Changes
- 构建系统架构总览文档：涵盖技术栈、分层架构、框架集成、设计模式
- 构建功能模块详解文档：逐模块分析核心功能、业务流程、接口设计、调用关系
- 构建数据库结构分析文档：完整表结构、索引设计、ER关系、数据流转规则
- 构建功能与数据关联矩阵：模块-表CRUD映射、关键业务数据流向图
- 构建开发规范与最佳实践文档：编码规范、常见问题、性能优化、安全防护

## Impact
- Affected specs: 无既有spec受影响（新建知识库）
- Affected code: 不修改任何现有代码，仅产出文档

## ADDED Requirements

### Requirement: 系统架构总览
系统知识库 SHALL 包含完整的系统架构总览章节，涵盖以下内容：

#### Scenario: 架构文档完整性
- **WHEN** 开发人员查阅架构总览
- **THEN** 文档应包含：技术栈说明（Struts2+Spring+iBatis+Activiti+Quartz）、分层架构图（表现层/控制层/业务层/持久层）、Spring配置加载链、多数据源架构说明、安全架构（CAS SSO + Spring Security + XSS防护）、Web过滤器链顺序

### Requirement: 功能模块详解
系统知识库 SHALL 对每个功能模块提供详细分析文档：

#### Scenario: 系统管理模块文档
- **WHEN** 查阅系统管理模块
- **THEN** 文档应包含：登录/登出流程（CAS SSO与非CAS双模式）、用户管理CRUD逻辑、角色权限控制模型（Role-Menu-Power三级）、部门管理、操作日志记录机制、基础数据维护

#### Scenario: 项目管理模块文档
- **WHEN** 查阅项目管理模块
- **THEN** 文档应包含：项目全生命周期管理（创建→计划→发货→实施→闭环）、泛化字段设计（column001~column014 + pm_column_of_relationship映射）、项目成员角色体系（销售/服务经理/项目经理）、项目状态机流转、合同关联与订单同步、项目周报机制

#### Scenario: 售前测试模块文档
- **WHEN** 查阅售前测试模块
- **THEN** 文档应包含：售前测试流程（Activiti驱动：申请→测试→回访→审批）、借货管理（SMS同步）、借转销/RMA核销、自动启动定时任务

#### Scenario: 回访管理模块文档
- **WHEN** 查阅回访管理模块
- **THEN** 文档应包含：回访申请流程、问卷模板体系（header/line/options三级）、评价结果管理

#### Scenario: 技术公告模块文档
- **WHEN** 查阅技术公告模块
- **THEN** 文档应包含：公告发布与审核流程、设备版本解析策略模式（Strategy Pattern）、影响版本匹配算法、修复任务跟踪、阅读日志记录

#### Scenario: 项目转包模块文档
- **WHEN** 查阅项目转包模块
- **THEN** 文档应包含：转包申请审批流程（Activiti驱动）、供应商管理、付款计划与SSE同步、交付物管理

#### Scenario: 报表与数据分析模块文档
- **WHEN** 查阅报表统计模块
- **THEN** 文档应包含：报表生成机制（DisplayTag + Excel导出）、ECharts图表集成、数据统计定时任务

#### Scenario: 工作流模块文档
- **WHEN** 查阅工作流模块
- **THEN** 文档应包含：Activiti 5.x集成配置、流程定义部署、任务分配与委派、统一任务监听器（UnifyTaskListener）、OA待办推送

### Requirement: 数据库结构分析
系统知识库 SHALL 包含完整的数据库结构分析：

#### Scenario: 核心表结构文档
- **WHEN** 查阅数据库表结构
- **THEN** 文档应包含主库dppms_d365所有业务表的字段定义、数据类型、约束条件、默认值及业务含义，按模块分组：基础数据表(fnd_*)、项目表(pm_project*)、售前表(pm_presales*)、回访表(pm_cl_*)、转包表(pm_subcontract_*)、公告表(prob_*)、数据同步中间表(pm_*_from_*)、Activiti表(act_*)

#### Scenario: ER关系文档
- **WHEN** 查阅表间关系
- **THEN** 文档应包含核心实体ER关系图：Project为中心的1:N关系网（成员/合同/产品线/状态/周报/软版本/维护）、Presales关联关系、Subcontract关联关系、User-Role-Department多对多关系

#### Scenario: 索引与性能文档
- **WHEN** 查阅索引设计
- **THEN** 文档应包含各表主键索引、唯一索引、普通索引的设计说明及使用场景分析

#### Scenario: 数据流转文档
- **WHEN** 查阅数据同步机制
- **THEN** 文档应包含8个外部系统的数据同步流程：SAP→PMS、D365→PMS、CRM→PMS、SMS→PMS、OA/EHR→PMS、SSE→PMS、ITR→PMS，含中间表设计、同步频率、数据转换规则

### Requirement: 功能与数据关联矩阵
系统知识库 SHALL 建立功能模块与数据库表的明确对应关系：

#### Scenario: CRUD映射矩阵
- **WHEN** 查阅模块-表关联
- **THEN** 文档应包含每个功能模块对数据库表的CRUD操作标注矩阵

#### Scenario: 关键业务数据流向
- **WHEN** 查阅业务数据流
- **THEN** 文档应包含项目创建→订单同步→发货→实施→闭环的完整数据流向图

### Requirement: 开发规范与最佳实践
系统知识库 SHALL 系统归纳开发规范：

#### Scenario: 编码规范文档
- **WHEN** 查阅开发规范
- **THEN** 文档应包含：Action/Service/DAO三层命名约定、iBatis SQL映射规范、Spring Bean装配规范、事务管理约定、JSP页面组织规范

#### Scenario: 常见问题与解决方案
- **WHEN** 查阅故障排查
- **THEN** 文档应包含：多数据源事务问题、Activiti流程异常处理、数据同步失败排查、性能优化技巧（SQL优化、连接池调优、缓存策略）、安全防护措施（XSS/CSRF/SQL注入防护）

### Requirement: 可视化图表
系统知识库 SHALL 包含必要的可视化图表：

#### Scenario: 架构与流程图
- **WHEN** 查阅可视化内容
- **THEN** 文档应包含：系统分层架构图、Spring配置加载链图、Web过滤器链图、模块调用关系图、核心业务流程图、ER关系图、数据同步流向图、项目状态机图

### Requirement: 代码示例与术语解释
系统知识库 SHALL 包含典型代码示例和术语字典：

#### Scenario: 代码示例
- **WHEN** 查阅代码示例
- **THEN** 文档应包含：BaseAction模板方法使用示例、Service事务代理配置示例、iBatis SQL映射示例、Activiti流程启动示例、定时任务配置示例

#### Scenario: 数据字典与术语
- **WHEN** 查阅术语解释
- **THEN** 文档应包含：项目类型编码(projectType)枚举、项目状态(projectState)枚举、成员角色(memberRole)枚举、订单类型(orderType)枚举、售前状态(applyState/projectState)枚举、转包状态(state)枚举、公告状态(status)枚举
