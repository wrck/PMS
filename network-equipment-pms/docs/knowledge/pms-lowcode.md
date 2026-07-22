# pms-lowcode 模块知识库

> 本文基于 `network-equipment-pms/pms-lowcode` 模块源码（`com.dp.plat.lowcode`）整理，记录低代码平台的实体建模、表单/列表/标签页/关联页可视化配置、微流编排、规则引擎、连接器集成、触发器调度、流程绑定、版本管理与发布流水线等核心机制。

## 模块概述

`pms-lowcode` 是网络设备 PMS 平台的**低代码配置与运行时模块**，定位为平台"配置即应用"的中枢。它将表单、列表、标签页、关联页、实体、微流、规则、连接器、触发器、流程绑定等十类配置以 JSON Schema 形式存储于数据库，通过运行时引擎动态解释执行，无需重新部署即可生成新业务页面与流程。模块借鉴 Mendix / OutSystems / Appsmith / Joget / 网易轻舟 / 华为 AppCube 等业内低代码产品的能力模型，覆盖以下五大职责：

1. **可视化配置存储** — 表单/列表/标签页/关联页/组件元数据/模板市场的 JSON Schema 配置存储与状态流转（DRAFT → PUBLISHED → ARCHIVED）。
2. **实体建模与 DDL 自动化** — 实体设计器产出实体 + 字段 + 关联关系，DDL 生成器按方言（MySQL/PostgreSQL/SQLServer）生成 CREATE/ALTER/DROP 语句并安全执行（带备份与回滚）。
3. **运行时引擎集群** — 微流引擎（DAG 节点编排 + Groovy 沙箱）、规则引擎（决策表 / Aviator 沙箱 / LiteFlow）、连接器（REST/DB/MQ/FILE + Resilience4j 熔断限流重试 + OAuth2 + JsonPath 映射）、触发器（CRUD/QUARTZ/EVENT）、流程任务回调。
4. **配置生命周期管理** — 版本快照（不可变）、版本树分支（借鉴 git parent commit 模型）、Diff 对比、回滚预览、环境晋升（DEV→TEST→PROD 配置包 zip 导入导出 + 门禁预检 + 冲突检测与解决方案）、多级审批链、灰度发布、协同编辑、编辑锁、配置审计、模板市场、应用源码导出。
5. **动态数据访问** — `DynamicEntityDataService` 基于 JdbcTemplate 操作动态生成的物理表，CRUD 前后接入触发器机制；高级查询支持 LIKE/IN/BETWEEN/OR/排序；Excel 异步导入导出。

- **Maven 坐标**：`com.dp.plat:pms-lowcode:1.0.0-SNAPSHOT`，父工程为 `com.dp.plat:network-equipment-pms`。
- **artifactId / name**：`pms-lowcode`，`<description>` 为 `低代码配置模块 - 表单/列表/标签页/关联页可视化配置`。
- **基础包名**：`com.dp.plat.lowcode`。
- **Java 版本**：JDK 17（Spring Boot 3.2.5）。
- **核心定位**：平台"配置即应用"中枢 —— 上层为前端设计器与运行时渲染器提供 API，下层封装 Groovy/Aviator/LiteFlow/Quartz/Flowable/Resilience4j/Hikari/RabbitMQ/Kafka/SFTP 等技术细节；通过 APM（Micrometer）实现全链路指标采集，通过 AOP（ConfigAuditAspect）实现配置写操作审计。

## 包结构

```
com.dp.plat.lowcode
├── config/                       # 异步任务配置
│   └── LowCodeAsyncConfig.java
├── controller/                   # 30 个 REST 控制器
│   ├── DynamicEntityController.java
│   ├── LowCodeApprovalChainController.java
│   ├── LowCodeAppSourceExportController.java
│   ├── LowCodeCollaborationController.java
│   ├── LowCodeCommentController.java
│   ├── LowCodeComponentMetaController.java
│   ├── LowCodeConfigAuditLogController.java
│   ├── LowCodeConfigTemplateController.java
│   ├── LowCodeConfigVersionController.java
│   ├── LowCodeConnectorController.java
│   ├── LowCodeDataImportExportController.java
│   ├── LowCodeDataSourceController.java
│   ├── LowCodeEditLockController.java
│   ├── LowCodeEntityController.java
│   ├── LowCodeFormController.java
│   ├── LowCodeFormEventController.java
│   ├── LowCodeGrayReleaseController.java
│   ├── LowCodeListController.java
│   ├── LowCodeMicroflowController.java
│   ├── LowCodeMicroflowExecutionLogController.java
│   ├── LowCodePermissionController.java
│   ├── LowCodePreviewController.java
│   ├── LowCodeProcessController.java
│   ├── LowCodePublishController.java
│   ├── LowCodeRelatedPageController.java
│   ├── LowCodeRuleController.java
│   ├── LowCodeRuleSetController.java
│   ├── LowCodeRuleTestCaseController.java
│   ├── LowCodeTabController.java
│   └── LowCodeTriggerController.java
├── dto/                          # 18 个数据传输对象
│   ├── AppSourceManifest.java            # 应用源码导出清单
│   ├── ApprovalLevel.java                # 审批级别
│   ├── CollaborationChange.java          # 协同变更事件
│   ├── CommentTreeNode.java              # 线程化评论树节点
│   ├── ConfigPackageDTO.java             # 环境晋升配置包
│   ├── CreateLowCodeMenuRequest.java     # 创建菜单请求
│   ├── DdlResultDTO.java                 # DDL 生成结果
│   ├── DependencyValidationResult.java   # 依赖校验结果
│   ├── DeployBpmnRequest.java            # BPMN 部署请求
│   ├── DynamicQueryRequest.java          # 高级动态查询
│   ├── EntityDesignDTO.java              # 实体设计（实体+字段+关联）
│   ├── ImportConflictDTO.java            # 导入冲突明细
│   ├── LowCodeConfigQuery.java           # 配置分页查询条件
│   ├── LowCodePageVO.java                # 低代码页面 VO
│   ├── OnlineUser.java                   # 协同在线用户
│   ├── OpenApiOperation.java             # OpenAPI 操作
│   ├── PromotionPipelineDTO.java         # 晋升管道状态
│   ├── PublishImpactDTO.java             # 发布影响范围
│   ├── VersionDiffDTO.java               # 版本差异
│   └── VersionTreeNode.java              # 版本树节点
├── engine/                       # 运行时引擎集群
│   ├── apm/                              # APM 指标
│   │   └── LowCodeApmService.java
│   ├── audit/                            # 审计切面
│   │   └── ConfigAuditAspect.java
│   ├── connector/                        # 连接器执行器
│   │   ├── ConnectorCredentialEncryptor.java
│   │   ├── ConnectorResult.java
│   │   ├── DbConnectorExecutor.java
│   │   ├── DynamicDataSourceManager.java
│   │   ├── FileConnectorExecutor.java
│   │   ├── MqConnectorExecutor.java
│   │   ├── OpenApiImporter.java
│   │   └── RestConnectorExecutor.java
│   ├── dataio/                           # 数据异步导入
│   │   └── LowCodeImportAsyncProcessor.java
│   ├── ddl/                              # DDL 安全执行
│   │   ├── DdlBackup.java
│   │   ├── DdlBackupMapper.java
│   │   ├── DdlExecutionLog.java
│   │   ├── DdlExecutionLogMapper.java
│   │   ├── DdlExecutionService.java
│   │   ├── DdlExecutionServiceImpl.java
│   │   └── DdlSecurityException.java
│   ├── editlock/                         # 编辑锁
│   │   ├── EditLockInfo.java
│   │   └── EditLockService.java
│   ├── microflow/                        # 微流引擎（11 种节点执行器）
│   │   ├── AssignExecutor.java
│   │   ├── CallConnectorExecutor.java
│   │   ├── CallMicroflowExecutor.java
│   │   ├── CallRuleExecutor.java
│   │   ├── CallServiceExecutor.java
│   │   ├── ConditionExecutor.java
│   │   ├── GroovySandboxExecutor.java
│   │   ├── LoopExecutor.java
│   │   ├── MicroflowContext.java
│   │   ├── MicroflowDebugger.java
│   │   ├── MicroflowDiagramService.java
│   │   ├── MicroflowEngine.java
│   │   ├── MicroflowExecutionException.java
│   │   ├── MicroflowNodeExecutor.java
│   │   ├── MicroflowNodeType.java
│   │   ├── ReturnExecutor.java
│   │   ├── StartEndExecutor.java
│   │   └── ThrowExceptionExecutor.java
│   ├── process/                          # 流程任务回调
│   │   └── ProcessTaskCallbackListener.java
│   ├── publish/                          # 发布流水线
│   │   └── PublishService.java
│   ├── rule/                             # 规则引擎
│   │   ├── AviatorSandboxExecutor.java
│   │   ├── LiteFlowExecutor.java
│   │   ├── RuleEngineService.java
│   │   ├── RuleSetDefinition.java
│   │   └── RuleSetOrchestrator.java
│   ├── trigger/                          # 触发器执行器
│   │   ├── CrudTriggerExecutor.java
│   │   ├── EventBusTriggerExecutor.java
│   │   ├── LowCodeQuartzJob.java
│   │   ├── LowCodeTrigger.java           # 触发器实体（位于 engine.trigger 包）
│   │   ├── QuartzTriggerExecutor.java
│   │   └── TriggerExecutor.java
│   ├── DdlGenerator.java                 # DDL 生成器接口
│   ├── DdlGeneratorFactory.java
│   ├── DynamicEntityDataService.java     # 动态实体数据 CRUD
│   ├── MySQLDdlGenerator.java
│   ├── PostgreSQLDdlGenerator.java
│   └── SqlServerDdlGenerator.java
├── entity/                       # 27 个实体（@TableName 持久化）
│   ├── LowCodeApprovalChain.java
│   ├── LowCodeBackupRecord.java
│   ├── LowCodeCollaborationSession.java
│   ├── LowCodeComment.java
│   ├── LowCodeComponentMeta.java
│   ├── LowCodeConfigAuditLog.java
│   ├── LowCodeConfigTemplate.java
│   ├── LowCodeConfigVersion.java
│   ├── LowCodeConnector.java
│   ├── LowCodeDataSource.java
│   ├── LowCodeEditLock.java
│   ├── LowCodeEntity.java
│   ├── LowCodeField.java
│   ├── LowCodeForm.java
│   ├── LowCodeGrayRelease.java
│   ├── LowCodeImportTask.java
│   ├── LowCodeList.java
│   ├── LowCodeMicroflow.java
│   ├── LowCodeMicroflowExecutionLog.java
│   ├── LowCodeProcessBinding.java
│   ├── LowCodeProcessSlaRecord.java
│   ├── LowCodePublishRecord.java
│   ├── LowCodeRelatedPage.java
│   ├── LowCodeRelation.java
│   ├── LowCodeRule.java
│   ├── LowCodeRuleTestCase.java
│   └── LowCodeTab.java
├── init/                         # 应用启动初始化器
│   ├── LowCodeConnectorTemplateInitializer.java
│   └── LowCodeTemplateInitializer.java
├── mapper/                       # 27 个 MyBatis-Plus Mapper
│   └── ...（与实体一一对应，LowCodeTriggerMapper 位于 trigger 子包外）
├── schema/                       # JSON Schema 规范文档
│   ├── FormConfigSchema.java             # 表单配置规范
│   ├── ListConfigSchema.java             # 列表配置规范
│   ├── RelatedPageConfigSchema.java      # 关联页配置规范
│   └── TabConfigSchema.java              # 标签页配置规范
├── service/                      # 25 个 Service 接口与实现
│   ├── CollaborationService.java
│   ├── ConfigTemplateService.java
│   ├── GrayReleaseService.java
│   ├── LowCodeAppSourceExportService.java
│   ├── LowCodeApprovalChainService.java
│   ├── LowCodeCommentService.java
│   ├── LowCodeConfigAuditLogService.java
│   ├── LowCodeConfigVersionService.java
│   ├── LowCodeConnectorService.java
│   ├── LowCodeDataImportExportService.java
│   ├── LowCodeDataSourceService.java
│   ├── LowCodeEntityService.java
│   ├── LowCodeFormEventService.java
│   ├── LowCodeFormService.java
│   ├── LowCodeListService.java
│   ├── LowCodeMicroflowService.java
│   ├── LowCodeProcessBindingService.java
│   ├── LowCodeRelatedPageService.java
│   ├── LowCodeRuleService.java
│   ├── LowCodeRuleTestCaseService.java
│   ├── LowCodeTabService.java
│   ├── LowCodeTriggerExecutionLogService.java
│   ├── LowCodeTriggerService.java
│   ├── ProcessSlaService.java
│   └── impl/  # 24 个实现类
├── util/                         # 工具类
│   ├── CredentialEncryptor.java          # 凭据加密
│   └── SpringApplicationContextHolder.java
└── version/                      # 版本与环境晋升
    ├── EnvironmentPromotionService.java
    ├── PromotionGateService.java
    ├── PublishImpactService.java
    └── VersionDiffCalculator.java
```

各包职责说明：

| 包 | 主要类型 | 职责 |
|----|----------|------|
| `config` | `LowCodeAsyncConfig` | 启用 Spring `@Async` 支持，供 `LowCodeImportAsyncProcessor` 异步导入 |
| `controller` | 30 个 `@RestController` | 暴露 30 组 REST API，覆盖 30 个 `/api/lowcode/*` 路径前缀 |
| `dto` | 18 个 DTO | 实体设计、查询请求、版本/晋升/影响/冲突等数据传输对象 |
| `engine.apm` | `LowCodeApmService` | Micrometer 全链路指标采集（Counter + Timer），best-effort |
| `engine.audit` | `ConfigAuditAspect` | AOP 拦截 8 大 ConfigService 写方法，自动写审计日志 |
| `engine.connector` | 8 个连接器组件 | REST/DB/MQ/FILE 连接器执行器、动态数据源管理、OpenAPI 导入、凭据加密 |
| `engine.dataio` | `LowCodeImportAsyncProcessor` | 异步 Excel 导入处理（独立 `@Component` 承载 `@Async`） |
| `engine.ddl` | `DdlExecutionService` 等 7 个类 | DDL 安全校验、表结构备份、执行、回滚、日志记录 |
| `engine.editlock` | `EditLockService` | 编辑锁（Redis SETNX + DB 持久化） |
| `engine.microflow` | `MicroflowEngine` + 11 节点执行器 + 调试器 + 图渲染 | 微流 DAG 编排、Groovy 沙箱、断点调试、SVG/PNG 图导出 |
| `engine.process` | `ProcessTaskCallbackListener` | Flowable 任务事件回调微流（Bean 名 `processTaskCallbackListener`） |
| `engine.publish` | `PublishService` | 发布流水线：提交→审批→发布→回滚 |
| `engine.rule` | `RuleEngineService` + `RuleSetOrchestrator` + 2 沙箱 | 决策表/Aviator/LiteFlow 三类规则执行 + THEN/WHEN/IF/SWITCH 编排 |
| `engine.trigger` | `TriggerExecutor` + 3 执行器 + Quartz Job | CRUD/QUARTZ/EVENT 触发器执行 |
| `engine` | `DdlGenerator` 工厂 + `DynamicEntityDataService` | 方言 DDL 生成 + 动态实体 CRUD（JdbcTemplate） |
| `entity` | 27 个 `@TableName` 实体 | 持久化模型，部分含 `@Version` 乐观锁、`@TableLogic` 逻辑删除 |
| `init` | 2 个 `ApplicationRunner` | 启动时加载预置模板（form/list/tab/related-page）与连接器模板 |
| `mapper` | 27 个 `BaseMapper` 子接口 | MyBatis-Plus 标准 CRUD，无自定义 SQL |
| `schema` | 4 个文档类 | 表单/列表/标签页/关联页配置 JSON Schema 规范（含字段类型常量与示例） |
| `service` / `service.impl` | 25 接口 + 24 实现 | 业务逻辑层（CRUD + 引擎调度 + 版本管理） |
| `util` | 2 个工具类 | 凭据 AES 加密、Spring 上下文持有者（Quartz Job 取 Bean 用） |
| `version` | 4 个服务类 | 环境晋升、门禁预检、发布影响分析、版本 Diff 计算 |

## 核心实体模型

模块共定义 27 张持久化表，全部以 `pms_lowcode_` 前缀命名。除 `LowCodeComponentMeta`、`LowCodeApprovalChain`、`LowCodeCollaborationSession`、`LowCodeComment`、`LowCodeEditLock`、`LowCodeGrayRelease`、`LowCodePublishRecord`、`LowCodeConfigTemplate` 外，其余均继承 `com.dp.plat.common.entity.BaseEntity`（含 id、createTime、updateTime、createBy、updateBy、deleted 等通用字段）。

### 配置类实体（核心配置存储）

| 实体类 | 表名 | 核心字段 | 说明 |
|--------|------|----------|------|
| `LowCodeEntity` | `pms_lowcode_entity` | `code`(唯一,正则`^[a-zA-Z][a-zA-Z0-9_]*$`)、`name`、`tableName`(正则`^pms_lc_[a-z][a-z0-9_]*$`)、`description`、`bizType`、`status`(DRAFT/PUBLISHED/ARCHIVED)、`version`(@Version 乐观锁) | 实体元数据，存储可视化实体设计器产出 |
| `LowCodeField` | `pms_lowcode_field` | `entityId`、`name`(小写正则)、`label`、`fieldType`(STRING/INTEGER/DECIMAL/BOOLEAN/DATE/DATETIME/TEXT/LONG)、`length`、`scale`、`nullable`、`primaryKey`、`indexed`、`uniqueFlag`、`defaultValue`、`sortOrder` | 实体字段定义，DDL 生成依据 |
| `LowCodeRelation` | `pms_lowcode_relation` | `fromEntityId`、`toEntityId`、`relationType`(ONE_TO_ONE/ONE_TO_MANY/MANY_TO_ONE/MANY_TO_MANY)、`fromFieldName`、`toFieldName`、`reverseName`、`junctionTable`(M2M 中间表)、`onDelete`(CASCADE/SET_NULL/RESTRICT)、`onUpdate`(CASCADE/RESTRICT) | 实体关联关系，支持自关联 |
| `LowCodeForm` | `pms_lowcode_form` | `code`(唯一)、`name`、`description`、`formConfig`(JSON: fields+layout)、`events`(JSON: onLoad/onChange/onSubmit)、`version`、`status`、`bizType` | 表单设计器产出 |
| `LowCodeList` | `pms_lowcode_list` | `code`、`name`、`description`、`listConfig`(JSON: columns/filters/operations/pagination/searchApi)、`version`、`status`、`bizType` | 列表设计器产出 |
| `LowCodeTab` | `pms_lowcode_tab` | `code`、`name`、`description`、`tabConfig`(JSON: tabs 数组,每项含 title/pageCode/type)、`version`、`status`、`bizType` | 标签页设计器产出 |
| `LowCodeRelatedPage` | `pms_lowcode_related_page` | `code`、`name`、`description`、`relatedConfig`(JSON: 关联关系+关联页面引用)、`version`、`status`、`bizType` | 关联页设计器产出 |
| `LowCodeComponentMeta` | `pms_lowcode_component_meta` | `id`、`name`(注册 key)、`displayName`、`category`(SELECTOR/INPUT/DISPLAY)、`icon`、`propsSchema`(JSON)、`description`、`version`、`author`、`status`、`tags`、`downloadCount`、`sourceType`(BUILTIN/CUSTOM/MARKETPLACE)、`entryUrl`、`builtin` | 组件元数据，支持远程组件市场 |
| `LowCodeConfigTemplate` | `pms_lowcode_config_template` | `id`、`code`(唯一)、`name`、`configType`、`category`、`configJson`(完整快照)、`thumbnail`、`description`、`author`、`tags`、`status`、`downloadCount`、`rating`、`ratingCount`、`version`、`parameters`(参数化定义 JSON) | 模板市场实体 |

### 引擎类实体（运行时配置）

| 实体类 | 表名 | 核心字段 | 说明 |
|--------|------|----------|------|
| `LowCodeMicroflow` | `pms_lowcode_microflow` | `code`、`name`、`description`、`definition`(JSON: nodes+edges)、`status`、`version`、`bizType` | 微流定义（位于 `entity` 包） |
| `LowCodeRule` | `pms_lowcode_rule` | `code`、`name`、`description`、`type`(DECISION_TABLE/EXPRESSION/LITEFLOW)、`definition`、`status`、`version`、`bizType`、`ext`(JSON 扩展,如 inputsSchema) | 规则定义 |
| `LowCodeRuleTestCase` | `pms_lowcode_rule_test_case` | `ruleId`、`ruleCode`(冗余)、`name`、`description`、`inputJson`、`expectedOutputJson`、`assertionMode`(EQUALS/CONTAINS/NOT_NULL)、`enabled` | 规则测试用例 |
| `LowCodeConnector` | `pms_lowcode_connector` | `code`、`name`、`description`、`type`(REST/DB)、`config`(JSON)、`status`(ACTIVE/INACTIVE)、`version`、`bizType` | 连接器配置 |
| `LowCodeTrigger` | `pms_lowcode_trigger` | `code`、`name`、`type`(CRUD/QUARTZ/EVENT)、`config`(JSON:按类型不同结构)、`targetType`(MICROFLOW/PROCESS)、`targetCode`、`status` | 触发器（位于 `engine.trigger` 包） |
| `LowCodeProcessBinding` | `pms_lowcode_process_binding` | `processDefinitionKey`、`processDefinitionName`、`nodeFormBindings`(JSON: [{nodeId,formCode,microflowCode}])、`taskCallbacks`(JSON: {nodeId:{onCreate,onAssign,onComplete}})、`status` | Flowable 流程绑定 |
| `LowCodeDataSource` | `pms_lowcode_datasource` | `code`、`name`、`dbType`(mysql/postgresql/sqlserver/oracle)、`integrationMode`(DIRECT/REPLICA/FEDERATED)、`url`、`username`、`password`(加密)、`driverClassName`、`poolSize`、`status`、`linkedEntityCode`、`syncConfig`(JSON) | 多数据源配置 |

### 版本与发布类实体

| 实体类 | 表名 | 核心字段 | 说明 |
|--------|------|----------|------|
| `LowCodeConfigVersion` | `pms_lowcode_config_version` | `configType`(FORM/LIST/TAB/RELATED_PAGE/ENTITY/MICROFLOW/RULE/CONNECTOR)、`configId`、`configCode`、`version`、`snapshot`(JSON 全量)、`changeLog`、`status`(ACTIVE/ARCHIVED)、`environment`(DEV/TEST/PROD)、`parentVersionId`(版本树分支)、`branch`(默认 main)、`tags`(逗号分隔) | 不可变版本快照，支持分支与标签 |
| `LowCodePublishRecord` | `pms_lowcode_publish_record` | `id`、`configType`、`configId`、`configCode`、`version`、`status`(DRAFT/SUBMITTED/APPROVING/APPROVED/REJECTED/PUBLISHED)、`currentLevel`、`approvalChainId`、`applicantId`、`applicant`、`approverId`、`approver`、`changeLog`、`rejectReason`、`submittedAt`、`approvedAt`、`publishedAt` | 发布记录 |
| `LowCodeApprovalChain` | `pms_lowcode_approval_chain` | `id`、`configType`、`name`、`levels`(JSON: [{level,approverRole,name}])、`enabled` | 多级审批链（借鉴 OutSystems LifeTime） |
| `LowCodeGrayRelease` | `pms_lowcode_gray_release` | `id`、`configType`、`configId`、`configCode`、`version`、`publishRecordId`、`grayPercentage`(0-100)、`tenantWhitelist`(JSON)、`status`(GRAYING/FULL/ROLLED_BACK)、`grayStartedAt`、`fullReleasedAt`、`rolledBackAt`、`createBy` | 灰度发布策略（借鉴华为 AppCube） |

### 协同与日志类实体

| 实体类 | 表名 | 核心字段 | 说明 |
|--------|------|----------|------|
| `LowCodeCollaborationSession` | `pms_lowcode_collaboration_session` | `id`、`configType`、`configId`、`onlineUsers`(JSON: [{userId,userName,avatar,joinedAt,lastHeartbeat}])、`changeSeq`、`createdAt` | 协同编辑会话（HTTP 轮询,预留 Yjs 升级） |
| `LowCodeComment` | `pms_lowcode_comment` | `id`、`configType`、`configId`、`userId`、`userName`、`content`、`mentions`(逗号分隔)、`parentId`、`@TableLogic deleted` | 线程化评论（支持 @提及） |
| `LowCodeEditLock` | `pms_lowcode_edit_lock` | `id`、`configType`、`configId`、`userId`、`userName`、`acquiredAt`、`expireAt`、`renewCount` | 编辑锁（DB 持久化,Redis 同步） |
| `LowCodeConfigAuditLog` | `pms_lowcode_config_audit_log` | `actor`、`configType`、`configId`、`configCode`、`action`(CREATE/UPDATE/DELETE/PUBLISH/ROLLBACK/PROMOTE)、`beforeSnapshot`、`afterSnapshot`、`diffSummary`、`ip`、`userAgent`、`tenantId`、`operateTime` | 配置审计日志（AOP 自动写入） |
| `LowCodeMicroflowExecutionLog` | `pms_lowcode_microflow_execution_log` | `microflowId`、`microflowCode`、`executionId`(UUID)、`nodeId`、`nodeType`、`startTime`、`endTime`、`durationMs`、`inputs`、`outputs`、`variablesSnapshot`、`status`(RUNNING/SUCCESS/FAILED)、`errorMessage`、`operator` | 微流执行轨迹（借鉴 Joget APM） |
| `LowCodeTriggerExecutionLog` | `pms_lowcode_trigger_execution_log` | `triggerId`、`triggerCode`、`triggerType`、`targetType`、`targetCode`、`executionId`、`inputs`、`outputs`、`status`(SUCCESS/FAILED)、`errorMessage`、`durationMs`、`operator` | 触发器执行日志 |
| `LowCodeProcessSlaRecord` | `pms_lowcode_process_sla_record` | `processInstanceId`、`taskId`、`slaConfigJson`(slaDuration/slaUnit/slaEscalationMicroflow)、`deadline`、`warningSent`、`escalateSent`、`status`(ACTIVE/WARNING/ESCALATED/COMPLETED) | 流程 SLA 记录 |

### 数据导入与备份类实体

| 实体类 | 表名 | 核心字段 | 说明 |
|--------|------|----------|------|
| `LowCodeImportTask` | `pms_lowcode_import_task` | `entityCode`、`fileName`、`status`(PENDING/RUNNING/SUCCESS/FAILED)、`totalRows`、`successRows`、`failedRows`、`failedDetail`(JSON: [{row,field,error}])、`errorMessage`、`operator`、`startTime`、`endTime` | 异步 Excel 导入任务 |
| `LowCodeBackupRecord` | `pms_lowcode_backup_record` | `type`(FULL/INCREMENTAL)、`scope`、`filePath`、`fileSize`、`status`、`operator`、`backupTime`、`expireAt` | 数据备份记录（仅建表,功能待批次6实现） |
| `DdlBackup`（engine.ddl 包） | `pms_lowcode_ddl_backup` | `entityId`、`tableName`、`backupType`(CREATE/ALTER/DROP_COLUMN)、`backupSql`(SHOW CREATE TABLE 结果)、`operator`、`backupTime` | DDL 表结构备份（用于回滚） |
| `DdlExecutionLog`（engine.ddl 包） | `pms_lowcode_ddl_execution_log` | `entityId`、`sql`、`status`、`errorMessage`、`operator`、`executeTime` | DDL 执行日志 |

### 实体关系

```
LowCodeEntity (1) ──── (N) LowCodeField
LowCodeEntity (1) ──── (N) LowCodeRelation (fromEntityId / toEntityId)
LowCodeRule   (1) ──── (N) LowCodeRuleTestCase
LowCodePublishRecord (1) ── (0..1) LowCodeGrayRelease
LowCodeApprovalChain (1) ── (N) LowCodePublishRecord
LowCodeConfigVersion (parentVersionId 自关联,构建版本树)
LowCodeProcessBinding ──(JSON 引用)──> LowCodeForm (formCode) / LowCodeMicroflow (microflowCode)
LowCodeTrigger ──(targetCode 引用)──> LowCodeMicroflow 或 Flowable Process
LowCodeMicroflow.definition ──(JSON 引用)──> LowCodeConnector / LowCodeRule / LowCodeMicroflow / Spring Bean
LowCodeForm.formConfig ──(JSON 引用)──> LowCodeEntity (entityCode)
```

## 低代码核心能力

### 1. 可视化配置存储与状态流转

四类核心配置（表单/列表/标签页/关联页）均采用相同的元数据模型：`code`（唯一编码）、`name`、`description`、`xxxConfig`（JSON Schema）、`version`（乐观锁）、`status`（DRAFT/PUBLISHED/ARCHIVED）、`bizType`（业务类型，作为应用分组键）。每类配置在 `schema` 包中有对应的 `*ConfigSchema` 文档类，作为前端设计器与渲染器约定的 JSON 结构规范。

**表单配置规范**（`FormConfigSchema`）：顶层含 `title`、`description`、`labelWidth`、`labelPosition`、`size`、`fields[]`、`layout`；字段类型支持 17 种常量：`TYPE_INPUT` / `TYPE_TEXTAREA` / `TYPE_NUMBER` / `TYPE_PASSWORD` / `TYPE_SELECT` / `TYPE_RADIO` / `TYPE_CHECKBOX` / `TYPE_DATE` / `TYPE_DATETIME` / `TYPE_DATERANGE` / `TYPE_SWITCH` / `TYPE_RATE` / `TYPE_SLIDER` / `TYPE_CASCADER` / `TYPE_UPLOAD` / `TYPE_DIVIDER` / `TYPE_TITLE` / `TYPE_CUSTOM`；布局支持 `grid` / `tabs` / `collapse` 三种类型。

**列表配置规范**（`ListConfigSchema`）：含 `columns[]`、`filters[]`、`operations[]`、`pagination`、`searchApi`。

状态流转通过 Service 的 `publish()` / `archive()` 方法触发，发布时生成不可变版本快照。

### 2. 实体建模与 DDL 自动化

**实体设计器**：`LowCodeEntityService.saveDesign(EntityDesignDTO)` 接收实体 + 字段 + 关联的完整设计，原子保存。`publish(Long entityId, String changeLog)` 将实体从 DRAFT 流转到 PUBLISHED 并生成版本快照。

**DDL 生成**：`DdlGenerator` 接口抽象方言差异，工厂 `DdlGeneratorFactory` 按 `LowCodeDataSource.dbType` 选择 `MySQLDdlGenerator`（默认）/`PostgreSQLDdlGenerator`/`SqlServerDdlGenerator`。接口方法包括 `generateCreateTable`、`generateAddColumn`、`generateDropColumn`、`generateCreateIndex`、`generateJunctionTable`（M2M 中间表）、`generateDropIndex`、`generateAlterColumn`。CREATE TABLE 支持 `entityIdToTableName` 映射正确推导外键目标表名。

**DDL 安全执行**（`DdlExecutionService`）：所有 DDL 在执行前由 `validateBeforeExecution` 拦截危险语句（如 `DROP DATABASE`），执行前先 `backupTableStructure`（SHOW CREATE TABLE 备份到 `pms_lowcode_ddl_backup`），执行后写 `DdlExecutionLog`。支持 `executeCreate` / `executeAlter`（增量 diff）/ `rollbackLastDdl` / `rollbackByBackupId`（按备份回滚）。

**动态实体数据访问**（`DynamicEntityDataService`）：基于 `JdbcTemplate` 直接操作 `pms_lc_*` 物理表。提供 `list`（分页 + 字段白名单防注入）、`queryAdvanced`（LIKE/IN/BETWEEN/OR/排序，JOIN 类型白名单 INNER/LEFT/RIGHT）、`getById`、`create`、`update`、`delete`。CRUD 前后自动触发 `CrudTriggerExecutor`（BEFORE 异常阻断主操作，AFTER 异常仅记日志）。

### 3. 微流引擎（DAG 编排）

**核心引擎**（`MicroflowEngine.execute`）：解析 `LowCodeMicroflow.definition` JSON（含 `nodes[]` 与 `edges[]`），构建节点查找表与边查找表（source→target），从 START 节点开始按 DAG 顺序遍历执行。安全计数器上限 1000 防死循环。每个节点执行前后通过 `executeNodeWithTrace` 记录执行轨迹（best-effort）到 `pms_lowcode_microflow_execution_log`，通过 `executionId`（UUID）串联同一次执行的所有节点轨迹。

**11 种节点类型**（`MicroflowNodeType` 枚举）：
- `START` / `END` — 开始/结束节点（`StartEndExecutor`）
- `ASSIGN` — Groovy 表达式赋值（`AssignExecutor`）
- `CONDITION` — Groovy 布尔表达式条件分支（`ConditionExecutor`）
- `LOOP` — Groovy 布尔表达式循环（`LoopExecutor`）
- `CALL_SERVICE` — 调用 Spring Bean 方法（`CallServiceExecutor`）
- `CALL_MICROFLOW` — 调用另一微流（`CallMicroflowExecutor`）
- `CALL_RULE` — 调用规则（`CallRuleExecutor`）
- `CALL_CONNECTOR` — 调用连接器（`CallConnectorExecutor`）
- `THROW_EXCEPTION` — 抛出业务异常（`ThrowExceptionExecutor`）
- `RETURN` — 返回结果终止执行（`ReturnExecutor`）

每个执行器实现 `MicroflowNodeExecutor` 接口（`getNodeType()` + `execute(nodeDef, context)`），返回下一节点 ID（null 表示按默认边或终止）。

**Groovy 沙箱**（`GroovySandboxExecutor`）：使用 `SecureASTCustomizer` 配置白名单与黑名单 —— 禁用 receivers（System/Runtime/ProcessBuilder/Thread/ClassLoader/File/GroovyShell 等）、imports 白名单仅 java.lang/java.util/java.math/groovy.lang、禁止显式 import 与静态 import、AST 表达式检查拦截 ConstructorCallExpression/ClassExpression/StaticMethodCallExpression。借鉴 Mendix/OutSystems 沙箱机制。

**断点调试器**（`MicroflowDebugger`）：支持断点设置（`addBreakpoint`/`removeBreakpoint`）、单步执行（`stepOver`）、继续执行到下一断点（`continueExecution`，单次最大 1000 步防死循环）、变量监视（`getVariables`）。会话存储 `ConcurrentHashMap`，30 分钟无操作超时由 `@Scheduled` 定时清理（`evictExpired`）。

**流程图渲染**（`MicroflowDiagramService`）：将微流定义渲染为 SVG/PNG 图像。节点形状按类型区分：START/END→圆角矩形（绿/红）、CONDITION/LOOP→菱形（黄/紫）、ASSIGN/RETURN/THROW_EXCEPTION→矩形（蓝/灰/橙）、CALL_*→矩形（青）。

### 4. 规则引擎

**三种规则类型**（`LowCodeRule.type`）：
- `DECISION_TABLE` — 决策表 JSON 定义，`executeDecisionTable` 返回命中的行动作列表
- `EXPRESSION` — Aviator 表达式，`executeExpression` 返回求值结果
- `LITEFLOW` — LiteFlow EL 表达式，`executeLiteFlow` 委托 `LiteFlowExecutor`

**Aviator 沙箱**（`AviatorSandboxExecutor`）：使用独立 `AviatorEvaluatorInstance`（非全局单例，避免污染其他模块）。禁用 `Feature.NewInstance`（new 关键字 + 类加载）、`Feature.Module`（Java 9+ 模块加载）、`Feature.InternalVars`（`__env__` 内部变量）。移除系统函数 `sysdate`/`now`/`rand`/`rand_long`/`date_to_string`/`get_sys_prop`/`get_sys_env`/`load`/`require`。编译前用正则 `FORBIDDEN_REFERENCE` 阻断 java/javax/jdk/sun/runtime/system/class/classloader 等引用。

**规则集编排**（`RuleSetOrchestrator`）：将多个规则节点按语义组合执行（借鉴 pms-rules 的 LiteFlow 能力但自实现编排语义）：
- `THEN` — 顺序执行，前一节点结果作为后一节点输入（key="input"）
- `WHEN` — 并行执行（`CompletableFuture`），结果聚合为 Map
- `IF` — 执行 conditionNode，true 走 thenNode 否则 elseNode
- `SWITCH` — 执行 switchNode，结果值匹配 caseMapping 决定执行哪个节点

节点类型支持 `decision_table` / `expression` / `liteflow` / `microflow`，委托对应执行器。

**规则测试**（`LowCodeRuleTestCase` + `LowCodeRuleTestCaseService`）：为规则定义可重复执行的测试用例，含 `inputJson`（决策表 facts / 表达式 context）与 `expectedOutputJson`，支持三种断言模式 `EQUALS`（完全相等）/ `CONTAINS`（实际包含期望）/ `NOT_NULL`（非空即可）。

### 5. 连接器集成

**4 种连接器执行器**：

| 执行器 | 类型 | 核心能力 |
|--------|------|----------|
| `RestConnectorExecutor` | REST | HTTP 调用，支持 NONE/BASIC/BEARER/API_KEY/OAUTH2 五种鉴权、Resilience4j 重试/熔断/限流、OFFSET/PAGE/NEXT_LINK 三种分页、JsonPath responseMapping 字段重命名 |
| `DbConnectorExecutor` | DB | JDBC 查询/更新，通过 `DynamicDataSourceManager`（HikariDataSource 缓存）获取 JdbcTemplate；DDL_PATTERN 正则拦截 CREATE/ALTER/DROP/TRUNCATE 等危险语句 |
| `MqConnectorExecutor` | MQ | 支持 RabbitMQ 与 Kafka，PRODUCE/CONSUME 两种操作 |
| `FileConnectorExecutor` | FILE | SFTP 协议，UPLOAD/DOWNLOAD/LIST/DELETE 四种操作（基于 JSch） |

**辅助组件**：
- `DynamicDataSourceManager` — 按 code 缓存 HikariDataSource + JdbcTemplate，支持 register/get/getDataSource/unregister
- `ConnectorCredentialEncryptor` + `CredentialEncryptor` — 凭据 AES 加密存储
- `OpenApiImporter` — 从 OpenAPI/Swagger 文档导入连接器配置

### 6. 触发器调度

**3 种触发类型**（`LowCodeTrigger.type`）：
- `CRUD` — 由 `DynamicEntityDataService` 在 create/update/delete 前后调用 `CrudTriggerExecutor`，config 含 `{entityCode, operations:[CREATE/UPDATE/DELETE], timing:[BEFORE/AFTER]}`
- `QUARTZ` — 由 `QuartzTriggerExecutor` 注册 Quartz Job（`LowCodeQuartzJob`），按 cron 表达式触发，config 含 `{cronExpression, cron}`（cron 为兼容别名）
- `EVENT` — 由 `EventBusTriggerExecutor` 发布 Spring `ApplicationEvent`（`LowCodeTriggerEvent`），同时直接执行目标微流

**目标类型**（`targetType`）：`MICROFLOW`（调用微流）/`PROCESS`（启动 Flowable 流程）。每个执行器实现 `TriggerExecutor` 接口（`supportedType()` + `execute(trigger, data)`）。

**Quartz Job**（`LowCodeQuartzJob`）：实现 `org.quartz.Job`，从 JobDataMap 取 `triggerCode`，通过 `SpringApplicationContextHolder.getBean(LowCodeTriggerService.class)` 获取 Service 执行触发器。异常捕获后仅记日志不重抛，避免 Quartz 反复触发失败任务。

### 7. 流程集成

**流程绑定**（`LowCodeProcessBinding`）：将 Flowable `processDefinitionKey` 绑定到节点-表单映射 JSON（`nodeFormBindings`：`[{nodeId, formCode, microflowCode}]`）与任务回调 JSON（`taskCallbacks`：`{nodeId:{onCreate,onAssign,onComplete}}`）。

**任务回调监听器**（`ProcessTaskCallbackListener`）：实现 Flowable `TaskListener`，Bean 名固定为 `processTaskCallbackListener`，在 BPMN UserTask 上配置 `<flowable:taskListener event="complete" delegateExpression="${processTaskCallbackListener}"/>` 接入。Flowable 事件名到回调键映射：create→onCreate、assignment→onAssign、complete→onComplete。回调微流失败仅记 ERROR 日志，不阻断流程主事务。

**流程 SLA**（`ProcessSlaService` + `LowCodeProcessSlaRecord`）：双阶段 SLA 触发 —— 任务截止前 80% 时间点触发预警微流（置 WARNING），截止时间到达触发升级微流（置 ESCALATED），任务完成置 COMPLETED。由 `@Scheduled` 每小时执行 `checkSlaStatus`。SLA 配置来自 BPMN 用户任务的 `lowcode:config` 扩展元素（`slaDuration` / `slaUnit` / `slaEscalationMicroflow`）。

**流程实例管理**（`LowCodeProcessController`）：复用 `pms-workflow` 的 `WorkflowService` + Flowable 原生 `RuntimeService`/`TaskService`/`HistoryService`，支持流程定义查询、BPMN XML 部署（`InMemoryMultipartFile` 包装字符串）、流程实例启动/挂起/激活/终止、活动节点 ID 查询、运行中/已完成实例列表（默认上限 200）。

### 8. 版本管理与环境晋升

**版本快照**（`LowCodeConfigVersionService`）：每次发布生成不可变全量 JSON 快照，按 `environment`（DEV/TEST/PROD）区分环境。`createSnapshot(SnapshotContext)` 创建快照，`getVersionHistory` 查询历史，`diff` 对比两版本差异（`VersionDiffCalculator`），`rollback` 用历史快照覆盖当前配置并生成新版本，`rollbackPreview` 仅对比不实际回滚（借鉴 OutSystems LifeTime）。

**版本树分支**（批次5-T1，借鉴 git parent commit 模型）：`parentVersionId` 指向父版本构建真正的分支树（非线性链），`branch` 默认 "main"，`tags` 支持里程碑标记。`createBranch` 从指定版本创建新分支，`addTag` 追加标签，`getVersionTree` 递归构建多分支树（旧数据无 parentVersionId 时降级为线性）。

**环境晋升**（`EnvironmentPromotionService`）：将 DEV 配置包晋升到 TEST/PROD。`exportPackageJson` 导出 JSON，`exportPackageZip` 导出 zip（含 config.json + metadata.json），`importPackageWithConfirm` 导入（带覆盖确认），`importPackageWithResolution` 按冲突解决方案导入（KEEP_SOURCE/KEEP_TARGET/SKIP）。

**依赖完整性校验**（`validatePackageDependencies`）：递归遍历快照 JSON，按字段名→依赖类型映射（entityCode→ENTITY、formCode→FORM、listCode→LIST、connectorCode→CONNECTOR、ruleCode→RULE、microflowCode→MICROFLOW）收集引用。TRIGGER 特化：targetCode 依赖类型由 targetType 决定。引用必须包内存在或目标环境已存在，否则记入缺失清单。JSON 解析失败回退正则提取。

**晋升门禁**（`PromotionGateService`，借鉴 OutSystems LifeTime）：DEV→TEST→PROD 晋升前执行门禁规则校验 —— 依赖完整性、版本递增（目标不能高于源）、状态校验（源最新必须 ACTIVE）、环境顺序（不能跨级）。返回 `GateResult`（passed + failures 列表）。与 `EnvironmentPromotionService` 互引，对后者用 `@Lazy` 注入打破循环依赖。

**晋升管道状态**（`getPipelineStatus`）：对每个 configCode 返回 DEV/TEST/PROD 三环境最新版本 + DEV→TEST/TEST→PROD 门禁状态，借鉴 OutSystems LifeTime 管道图视图。

**发布影响分析**（`PublishImpactService`，借鉴 OutSystems LifeTime）：反向查询给定源配置的所有下游引用。严重度分级：HIGH（ENTITY 变更影响 FORM/LIST）、MEDIUM（CONNECTOR/RULE 变更影响 MICROFLOW）、LOW（MICROFLOW 变更影响 TRIGGER）。

### 9. 发布流水线

**`PublishService`** 提供完整发布流水线：
- `submitForPublish` — 提交发布申请（status → SUBMITTED），若有审批链则置 APPROVING + currentLevel=1
- `validate` — 校验配置完整性
- `approve` — 审批通过（按审批链逐级推进，全部通过则 PUBLISHED + 生成版本快照）
- `reject` — 审批拒绝（status → REJECTED）
- `rollback` — 回滚到指定发布版本
- `listByConfig` / `listPending` — 查询发布记录

**多级审批链**（`LowCodeApprovalChain`，借鉴 OutSystems LifeTime）：每个 configType 可配置一条启用审批链，`levels` JSON 数组 `[{level, approverRole, name}]`，按 level 顺序逐级推进，当前用户需具备对应角色方可通过当前级别。

**灰度发布**（`GrayReleaseService` + `LowCodeGrayRelease`，借鉴华为 AppCube）：发布审批通过后可创建灰度策略，按 `grayPercentage`（0-100）或 `tenantWhitelist` 渐进生效。`isInGray(configType, configId, userId, tenantId)` 按 userId hash 取模判断比例命中。状态流转 GRAYING → FULL（全量）或 ROLLED_BACK（回滚）。

### 10. 协同编辑与编辑锁

**协同编辑**（`CollaborationService` + `LowCodeCollaborationSession`，批次5-T6，借鉴 Mendix）：当前实现基于 HTTP 轮询（需求文档要求 Yjs + y-websocket，但环境无网络无法安装 npm 包），接口预留 WebSocket 升级点。提供 `join`/`leave`/`heartbeat`（保活，超时自动离线）/`getOnlineUsers`/`broadcastChange`/`getChanges`（增量拉取 seq > sinceSeq）。

**编辑锁**（`EditLockService` + `LowCodeEditLock`）：Redis SETNX + DB 持久化双重保障。提供 `acquire`/`renew`（心跳续期）/`release`/`getLock`。

**线程化评论**（`LowCodeCommentService` + `LowCodeComment`）：支持 `parentId` 树形结构，`listThreaded` 在内存一次性构建树（先查全部再按 parentId 分组递归，避免 N+1）。支持 `mentions`（逗号分隔用户 ID），集成 `pms-notification` 发送 @提及通知。

### 11. 配置审计

**AOP 切面**（`ConfigAuditAspect`，缺口2）：拦截 8 大核心 ConfigService 的写方法（save/saveOrUpdate/update/updateById/removeById/removeByIds/delete/create），自动写审计日志到 `pms_lowcode_config_audit_log`。Service 类简单名→configType 映射：LowCodeEntityServiceImpl→ENTITY、LowCodeFormServiceImpl→FORM、LowCodeListServiceImpl→LIST、LowCodeTabServiceImpl→TAB、LowCodeRelatedPageServiceImpl→RELATED_PAGE、LowCodeMicroflowServiceImpl→MICROFLOW、LowCodeRuleServiceImpl→RULE、LowCodeConnectorServiceImpl→CONNECTOR。审计写入 best-effort，异常被吞掉不阻断主业务。

### 12. 模板市场

**`ConfigTemplateService` + `LowCodeConfigTemplate`**（批次5-T8，借鉴 Zoho 模板市场 / Appsmith 模板 / Mendix App Store）：提供模板上架（publish→PUBLISHED）/下架（unpublish→DRAFT）/归档（archive→ARCHIVED）、市场浏览（marketplace，仅 PUBLISHED，支持关键词搜索 name/tags/description + configType/category 过滤，按 downloadCount desc 排序）、下载（download，downloadCount++，应用参数化替换占位符）、评分（rate，更新 rating 平均值与 ratingCount）、版本查询（listVersions，按 version desc）。

### 13. 应用源码导出

**`LowCodeAppSourceExportService` + `AppSourceManifest`**（批次5-T10，借鉴网易轻舟源码导出）：将低代码应用配置打包为可独立部署的源码 ZIP（JSON + DDL + POM + README），无黑盒引擎，所有配置均为可读 JSON + 标准 SQL DDL，运行时依赖 `pms-lowcode` 开源 Maven 模块。`previewManifest` 预览导出清单，`exportAsZip` 生成 ZIP。

### 14. 数据导入导出

**`LowCodeDataImportExportService` + `LowCodeImportAsyncProcessor`**（缺口3）：Excel 模板下载（列头为字段 label）、异步导入（`@Async` 独立线程池，通过 `LowCodeImportAsyncProcessor` 独立 `@Component` 承载避免自调用代理失效）、同步导出、导入任务历史查询。导入任务记录到 `pms_lowcode_import_task`，含 totalRows/successRows/failedRows/failedDetail JSON。

### 15. APM 全链路指标

**`LowCodeApmService`**（批次5-T9，借鉴 Joget APM）：为四大执行引擎提供统一 Micrometer 指标入口。指标命名（Prometheus 抓取格式 `lowcode_*`）：
- `lowcode_microflow_execution_total{microflow_code, status}` Counter
- `lowcode_microflow_duration_seconds{microflow_code}` Timer
- `lowcode_microflow_node_duration_seconds{node_type, status}` Timer
- `lowcode_rule_execution_total{rule_type, status}` Counter
- `lowcode_rule_duration_seconds{rule_type}` Timer
- `lowcode_connector_call_total{connector_type, connector_code, status}` Counter
- `lowcode_connector_duration_seconds{connector_type, connector_code}` Timer
- `lowcode_trigger_execution_total{trigger_type, trigger_code, status}` Counter
- `lowcode_trigger_duration_seconds{trigger_type, trigger_code}` Timer
- `lowcode_flowable_callback_total{process_key, event, status}` Counter

`MeterRegistry` 未注入时全部 no-op，业务引擎通过 `@Autowired(required=false)` 引用，null-skip。

### 16. 预置模板初始化

**`LowCodeTemplateInitializer`**（`ApplicationRunner`）：应用启动时检查 `pms_lowcode_form`/`pms_lowcode_list`/`pms_lowcode_tab`/`pms_lowcode_related_page` 是否为空，为空则加载 `classpath:lowcode-templates/{form,list,tab,related-page}/` 下的预置模板 JSON，通过 `importConfig` 导入（code 冲突自动追加数字后缀，重复启动不抛错）。

预置模板：
- 表单：`project-create`（项目创建）、`asset-inbound`（资产入库）、`settlement-create`（结算创建）
- 列表：`project-list`、`asset-list`、`settlement-list`
- 标签页：`project-detail-tabs`（8 个 Tab）、`asset-detail-tabs`（4 个 Tab）
- 关联页：`project-overview`（4 个区块）、`asset-overview`（4 个区块）

**`LowCodeConnectorTemplateInitializer`**：预置连接器模板。

## Service 层与 API 端点

### Service 接口清单

模块在 `service` 包下定义 25 个 Service 接口，`service.impl` 包下 24 个实现类。核心 Service 接口及其关键方法：

| Service 接口 | 关键方法 | 说明 |
|--------------|----------|------|
| `LowCodeEntityService` | `saveDesign(EntityDesignDTO)`、`getDesign(Long)`、`generateDdl(Long)`、`publish(Long, String)`、`isTableNameExists(String, Long)`、`saveRelations(Long, List<LowCodeRelation>)` | 实体设计管理 |
| `LowCodeFormService` | `getByCode(String)`、`page(IPage, LowCodeConfigQuery)`、`create`/`update`/`delete`、`publish`/`archive`、`exportConfig(String)`、`importConfig(String)` | 表单配置管理 |
| `LowCodeListService` | 同 Form 模式 | 列表配置管理 |
| `LowCodeTabService` | 同 Form 模式 | 标签页配置管理 |
| `LowCodeRelatedPageService` | 同 Form 模式 | 关联页配置管理 |
| `LowCodeMicroflowService` | `execute(String code, Map<String,Object> inputs)` | 微流执行 |
| `LowCodeRuleService` | `execute(String, Map)`、`publishWithVersion(Long)`、`listRuleVersions(Long)`、`rollbackRule(Long, Integer)` | 规则执行 + 版本管理 |
| `LowCodeConnectorService` | `execute(String, Map)`、`test(String)`、`testOperation(String, String, Map)` | 连接器执行 + 测试 |
| `LowCodeTriggerService` | `executeTrigger(String, Map)` | 触发器执行 |
| `LowCodeProcessBindingService` | `findByProcessKey(String)`、`getFormCodeForNode(String, String)` | 流程绑定查询 |
| `LowCodeConfigVersionService` | `createSnapshot(SnapshotContext)`、`getVersionHistory`、`getVersionTree`、`diff`、`rollback`、`rollbackPreview`、`exportPackage`/`importPackage`、`createBranch`、`addTag` | 版本管理 |
| `LowCodeDataSourceService` | `testConnection(LowCodeDataSource)`、`activate(String)`、`deactivate(String)`、`getConnection(String)` | 多数据源管理 |
| `LowCodeCommentService` | `listByConfig`、`addComment`、`listThreaded` | 线程化评论 |
| `CollaborationService` | `join`/`leave`/`heartbeat`/`getOnlineUsers`/`broadcastChange`/`getChanges` | 协同编辑 |
| `ConfigTemplateService` | `save`/`publish`/`unpublish`/`archive`/`getByCode`/`listAll`/`marketplace`/`download`/`rate`/`listVersions` | 模板市场 |
| `GrayReleaseService` | `createGrayRelease`/`updatePercentage`/`releaseFull`/`rollbackGray`/`getActiveGrayRelease`/`listByConfig`/`isInGray` | 灰度发布 |
| `LowCodeApprovalChainService` | 多级审批链 CRUD | 审批链配置 |
| `LowCodeConfigAuditLogService` | 审计日志查询 | 配置审计 |
| `LowCodeRuleTestCaseService` | 规则测试用例 CRUD + 执行 | 规则测试 |
| `LowCodeTriggerExecutionLogService` | 触发器执行日志查询 | 执行日志 |
| `ProcessSlaService` | `recordSlaForTask`/`checkSlaStatus`/`completeSla` | 流程 SLA |
| `LowCodeDataImportExportService` | `downloadImportTemplate`/`importExcel`/`exportExcel`/`listImportTasks`/`getImportTask` | 数据导入导出 |
| `LowCodeAppSourceExportService` | `previewManifest`/`exportAsZip` | 应用源码导出 |
| `LowCodeFormEventService` | 表单事件绑定管理 | 表单事件 |

### Controller 与 API 端点

模块共 30 个 `@RestController`，全部以 `/api/lowcode/*` 为路径前缀。所有写操作均通过 `@PreAuthorize` 校验权限（权限串格式 `lowcode:{resource}:{action}`），并通过 `@OperLog` 记录操作日志。动态实体数据 Controller 通过 SpEL 拼接权限串 `lowcode:data:{entityCode}:{action}` 实现按实体粒度授权。

| Controller | 路径前缀 | 主要端点 | 权限前缀 |
|------------|----------|----------|----------|
| `LowCodeEntityController` | `/api/lowcode/entity` | GET `/list`、GET `/{id}`、POST（saveDesign）、GET `/{id}/ddl`、POST `/{id}/publish`、DELETE `/{id}`、POST `/{entityId}/relations`、GET `/check-table-name`、GET `/{entityId}/ddl-backups`、POST `/{entityId}/rollback-ddl`、POST `/ddl/rollback/{backupId}` | `lowcode:entity:*` |
| `DynamicEntityController` | `/api/lowcode/data/{entityCode}` | GET（分页）、POST `/query`（高级查询）、GET `/{id}`、POST（create）、PUT `/{id}`、DELETE `/{id}` | `lowcode:data:{entityCode}:*` |
| `LowCodeFormController` | `/api/lowcode/form` | GET（分页）、GET `/{id}`、GET `/code/{code}`、POST、PUT `/{id}`、DELETE `/{id}`、POST `/{id}/publish`、POST `/{id}/archive`、GET `/{code}/export`、POST `/import` | `lowcode:form:*` |
| `LowCodeFormEventController` | `/api/lowcode/form` | 表单事件绑定 CRUD | `lowcode:form:*` |
| `LowCodeListController` | `/api/lowcode/list` | 同 Form 模式 | `lowcode:list:*` |
| `LowCodeTabController` | `/api/lowcode/tab` | 同 Form 模式 | `lowcode:tab:*` |
| `LowCodeRelatedPageController` | `/api/lowcode/related-page` | 同 Form 模式 | `lowcode:related-page:*` |
| `LowCodeComponentMetaController` | `/api/lowcode/component-meta` | 组件元数据 CRUD | `lowcode:component:*` |
| `LowCodeMicroflowController` | `/api/lowcode/microflow` | GET（list）、GET `/{id}`、POST（save）、DELETE `/{id}`、POST `/{code}/execute`、GET `/{id}/diagram.svg`、GET `/{id}/diagram.png`、POST `/{code}/debug/start`、POST `/debug/{sessionId}/step`、POST `/debug/{sessionId}/continue`、GET `/debug/{sessionId}/variables`、DELETE `/debug/{sessionId}`、POST/DELETE `/debug/{sessionId}/breakpoints/{nodeId}` | `lowcode:microflow:*` |
| `LowCodeMicroflowExecutionLogController` | `/api/lowcode/microflow-execution-log` | 微流执行轨迹查询 | `lowcode:microflow:*` |
| `LowCodeRuleController` | `/api/lowcode/rule` | GET、GET `/{id}`、POST、DELETE `/{id}`、POST `/{code}/execute`、POST `/{id}/publish`、GET `/{id}/versions`、POST `/{id}/rollback/{targetVersion}` | `lowcode:rule:*` |
| `LowCodeRuleSetController` | `/api/lowcode/rule-set` | 规则集编排执行 | `lowcode:rule:*` |
| `LowCodeRuleTestCaseController` | `/api/lowcode/rule-test-case` | 规则测试用例 CRUD + 执行 | `lowcode:rule:*` |
| `LowCodeConnectorController` | `/api/lowcode/connector` | GET、GET `/{id}`、POST、DELETE `/{id}`、POST `/{code}/test`、POST `/{code}/execute`、POST `/{code}/test-operation`、OpenAPI 导入 | `lowcode:connector:*` |
| `LowCodeTriggerController` | `/api/lowcode/trigger` | 触发器 CRUD + 执行 | `lowcode:trigger:*` |
| `LowCodeProcessController` | `/api/lowcode/process` | GET `/bindings`、POST `/bindings`、GET `/definitions`、GET `/task-form`、POST `/deploy`、GET `/bpmn-xml`、GET `/instance/activity-ids`、GET `/instances`、DELETE `/instances/{id}`、POST `/instances`、POST `/instances/{id}/suspend`、POST `/instances/{id}/activate` | `lowcode:process:*` |
| `LowCodeDataSourceController` | `/api/lowcode/datasource` | 多数据源 CRUD + 测试 + 激活/停用 | `lowcode:datasource:*` |
| `LowCodeConfigVersionController` | `/api/lowcode/version` | GET `/history`、GET `/tree`、GET `/diff`、POST `/rollback`、GET `/rollback-preview`、GET `/publish-impact`、POST `/promote`、GET `/pipeline`、POST `/gate-check`、GET/POST `/export-package`、POST `/import-package`、POST `/import-conflicts`、POST `/import-resolve`、POST `/branch`、POST `/tag` | `lowcode:version:*` |
| `LowCodePublishController` | `/api/lowcode/publish` | POST `/submit`、POST `/{id}/approve`、POST `/{id}/reject`、POST `/{id}/rollback`、GET、GET `/pending` | `lowcode:publish:*` |
| `LowCodeApprovalChainController` | `/api/lowcode/approval-chain` | 审批链 CRUD | `lowcode:approval:*` |
| `LowCodeGrayReleaseController` | `/api/lowcode/gray-release` | 灰度策略 CRUD + 调整比例 + 全量 + 回滚 + 命中判断 | `lowcode:gray:*` |
| `LowCodeCollaborationController` | `/api/lowcode/collaboration` | join/leave/heartbeat/onlineUsers/broadcast/changes | `lowcode:collaboration:*` |
| `LowCodeCommentController` | `/api/lowcode/comment` | 评论 CRUD + 线程化查询 | `lowcode:comment:*` |
| `LowCodeEditLockController` | `/api/lowcode/edit-lock` | acquire/renew/release/getLock | `lowcode:edit-lock:*` |
| `LowCodeConfigAuditLogController` | `/api/lowcode/config-audit` | 审计日志查询 | `lowcode:audit:*` |
| `LowCodeConfigTemplateController` | `/api/lowcode/config-template` | 模板 CRUD + 上架/下架/归档 + 市场浏览 + 下载 + 评分 + 版本查询 | `lowcode:template:*` |
| `LowCodeDataImportExportController` | `/api/lowcode/data-import-export` | GET `/template`、POST `/import`、GET `/export`、GET `/tasks`、GET `/tasks/{id}` | `lowcode:data:import`/`lowcode:data:export` |
| `LowCodeAppSourceExportController` | `/api/lowcode/app-source` | GET `/manifest`、GET `/export` | `lowcode:app-source:export` |
| `LowCodePreviewController` | `/api/lowcode/preview` | GET `/form/{formId}`、GET `/list/{listId}` | 无（公开预览） |
| `LowCodePermissionController` | `/api/lowcode/permission` | 低代码权限管理 | `lowcode:permission:*` |

## 模块依赖关系

### Maven 依赖

`pms-lowcode` 的 `pom.xml` 声明以下依赖：

**内部模块依赖**：
- `com.dp.plat:pms-common` — 通用实体（`BaseEntity`）、`Result`、`OperLog` 注解、`SecurityUtils`
- `com.dp.plat:pms-system` — 系统模块（用户、角色、权限）
- `com.dp.plat:pms-workflow` — Flowable 工作流引擎（`WorkflowService`、`ProcessInstanceDTO`）
- `com.dp.plat:pms-notification` — 评论 @提及通知
- `com.dp.plat:pms-file` — 文件上传组件对接

**Spring Boot Starters**：
- `spring-boot-starter-web` — REST API
- `spring-boot-starter-validation` — Bean Validation
- `spring-boot-starter-quartz` — 触发器调度
- `spring-boot-starter-data-redis` — 编辑锁 + Quartz 集群
- `spring-boot-starter-test`（test 作用域）

**MyBatis-Plus**：
- `com.baomidou:mybatis-plus-spring-boot3-starter` — ORM

**低代码引擎依赖**：
- `org.apache.groovy:groovy` + `groovy-json` — 微流 Groovy 沙箱
- `com.googlecode.aviator:aviator:5.4.3` — Aviator 规则表达式沙箱
- `com.yomahub:liteflow-spring-boot-starter:2.15.0` — LiteFlow 规则引擎

**连接器依赖**：
- `io.github.resilience4j:resilience4j-retry:2.2.0` / `resilience4j-circuitbreaker:2.2.0` / `resilience4j-ratelimiter:2.2.0` — REST 连接器重试/熔断/限流
- `com.jayway.jsonpath:json-path:2.9.0` — REST 连接器 Request/Response 映射
- `com.rabbitmq:amqp-client:5.20.0` — RabbitMQ 连接器
- `org.apache.kafka:kafka-clients:3.6.1` — Kafka 连接器
- `com.jcraft:jsch:0.1.55` — SFTP 文件连接器

### 模块间关系

```
pms-common ──> pms-lowcode
pms-system ──> pms-lowcode
pms-workflow ──> pms-lowcode
pms-notification ──> pms-lowcode
pms-file ──> pms-lowcode

pms-lowcode ──> pms-admin（pms-admin 依赖 pms-lowcode）
```

`pms-lowcode` 在 `network-equipment-pms` 父工程的 14 个模块中处于中下层位置：依赖 5 个底层模块（common/system/workflow/notification/file），被聚合模块 `pms-admin` 依赖（`pms-admin/pom.xml` 第 60 行引入）。`pms-lowcode` 自身不依赖 `pms-project`/`pms-asset`/`pms-implementation`/`pms-governance`/`pms-baseline`/`pms-deliverable` 等业务模块，保持低代码平台的通用性。

## 关键技术点

### 1. JSON Schema 驱动的配置存储

四类核心配置（表单/列表/标签页/关联页）+ 微流定义 + 规则定义 + 连接器配置 + 触发器配置 + 流程绑定，全部以 JSON 字符串存储于数据库字段。这种设计带来三个优势：
- **灵活性** — 配置结构可随设计器演进，无需 DDL 变更
- **可序列化** — 配置可整体导出/导入，支持环境晋升与应用源码导出
- **版本快照** — 全量 JSON 快照实现不可变版本管理与 Diff 对比

代价是 JSON 字段无法在 DB 层索引，需通过 `code` 等冗余字段建立唯一索引。`schema` 包下的 4 个文档类作为前后端约定的 JSON 结构规范，含字段类型常量与示例。

### 2. Groovy 与 Aviator 双沙箱

低代码平台允许用户编写表达式（微流 ASSIGN/CONDITION/LOOP 节点、规则 EXPRESSION 类型），必须防止命令注入与反射逃逸。

**Groovy 沙箱**（`GroovySandboxExecutor`）用于微流节点，使用 `SecureASTCustomizer` 在 AST 层拦截：
- receiversBlackList 拦截 System/Runtime/ProcessBuilder/Thread/ClassLoader/File 等危险类的方法调用
- importsWhitelist 为空 + starImportsWhitelist 仅 java.lang/java.util/java.math/groovy.lang
- AST 表达式检查器拦截 ConstructorCallExpression（new File(...)）、ClassExpression（全限定类名）、StaticMethodCallExpression（静态方法调用）

**Aviator 沙箱**（`AviatorSandboxExecutor`）用于规则表达式，使用独立 `AviatorEvaluatorInstance`：
- 禁用 `Feature.NewInstance` / `Feature.Module` / `Feature.InternalVars`
- 移除 sysdate/now/rand/get_sys_prop/get_sys_env/load/require 等系统函数
- 编译前正则阻断 java/javax/jdk/sun/runtime/system/class/classloader 等引用

两者均为 best-effort 安全，沙箱并非绝对安全，但能拦截绝大多数常见攻击向量。表达式来源应在调用方做权限校验。

### 3. DDL 安全执行与回滚

DDL 操作具有破坏性（DROP COLUMN 不可逆），模块通过三层防护：

1. **安全校验**（`DdlExecutionService.validateBeforeExecution`）— 正则拦截 DROP DATABASE 等危险语句，抛 `DdlSecurityException`
2. **执行前备份**（`backupTableStructure`）— `SHOW CREATE TABLE` 备份到 `pms_lowcode_ddl_backup`，记录 `backupType`（CREATE/ALTER/DROP_COLUMN）
3. **执行日志**（`DdlExecutionLog`）— 每条 DDL 执行后写日志，含 status/errorMessage

回滚支持两种粒度：`rollbackLastDdl(entityId)` 回滚最近一次（按 id 倒序取最新备份），`rollbackByBackupId(backupId)` 按指定备份回滚。

### 4. 动态实体数据访问的 SQL 注入防护

`DynamicEntityDataService` 基于 JdbcTemplate 拼接 SQL，通过多重白名单防护：

- **字段名白名单** — 过滤条件字段名必须命中实体定义的字段集合 + `id`，否则忽略（防 `UNION SELECT` 注入）
- **JOIN 类型白名单** — `VALID_JOIN_TYPES = Set.of("INNER", "LEFT", "RIGHT")`
- **标识符正则白名单** — `IDENTIFIER_PATTERN = ^[A-Za-z_][A-Za-z0-9_]{0,63}$`，表名/别名/字段名必须匹配
- **参数化查询** — 所有值通过 `?` 占位符传入，不拼接字面量

高级查询（`queryAdvanced`）支持 LIKE/IN/BETWEEN/OR/排序，同样遵循白名单原则。

### 5. 微流 DAG 执行与轨迹记录

`MicroflowEngine.execute` 的核心循环：
1. 解析 definition JSON 构建 `nodeMap`（id→node）与 `edgeMap`（source→target）
2. 找 START 节点作为起始 currentNodeId
3. 循环执行：找执行器 → `executeNodeWithTrace` → 执行器返回 nextNodeId（null 时按默认边走）→ 更新 currentNodeId
4. 终止条件：currentNodeId 为 null / `context.isTerminated()` / safetyCounter 超 1000

每个节点执行前后通过 `executeNodeWithTrace` 记录轨迹（best-effort）：
- 执行前 insert 一条 RUNNING 记录（含 inputs/variablesSnapshot）
- 执行后 update 为 SUCCESS/FAILED（含 outputs/durationMs/errorMessage）
- 轨迹记录异常仅 WARN 日志，不影响主流程

通过 `executionId`（UUID）串联同一次执行的所有节点轨迹，便于前端按执行 ID 查询完整链路。

### 6. 触发器机制与 CRUD 集成

CRUD 触发器由 `DynamicEntityDataService` 在 create/update/delete 前后主动调用 `CrudTriggerExecutor.execute`：

- **BEFORE 触发器** — 异常向上抛出，阻断主操作（如校验失败）
- **AFTER 触发器** — 异常仅记日志，不阻断（如发通知失败）

`CrudTriggerExecutor.isMatching` 判断触发器是否匹配指定 entityCode/operation/timing，config 缺省时按宽松匹配（不限制），保证向后兼容旧 config 结构。

QUARTZ 触发器通过 `LowCodeQuartzJob`（Quartz Job 实现）按 cron 触发，从 JobDataMap 取 `triggerCode`，通过 `SpringApplicationContextHolder.getBean` 获取 Service（因 Quartz Job 不受 Spring 容器管理，无法直接注入）。异常不重抛，避免 Quartz 反复触发失败任务。

EVENT 触发器通过 `EventBusTriggerExecutor` 发布 Spring `ApplicationEvent`（`LowCodeTriggerEvent`），同时直接执行目标微流。

### 7. 版本树分支模型（借鉴 git）

`LowCodeConfigVersion` 通过 `parentVersionId` 自关联构建真正的分支树：
- 根版本 `parentVersionId` 为 null
- 从 v2 分支出 v3a 与 v3b 时，两者 parentId 都指向 v2
- `branch` 字段标识所属分支（默认 "main"），同一 configType+configId 下可有多个分支独立演进
- `tags` 字段标记里程碑（如 "v1.0-release"、"审核通过"），逗号分隔多标签

`getVersionTree` 递归构建多分支树，旧数据（所有 parentVersionId 为 null）降级为按版本号升序线性构建。借鉴 git parent commit 模型，但简化为扁平分支（不支持 merge）。

### 8. 环境晋升的依赖完整性校验

`EnvironmentPromotionService.validatePackageDependencies` 实现包内自洽 + 目标环境存在性的双层校验：

1. **包内自洽** — 汇总包内所有 item 的 configCode（按类型分组），引用的 code 若在包内存在则满足
2. **目标环境存在性** — 包内不存在时查目标环境（`dependencyExists` 按 depType 路由到对应 Service.count）
3. **TRIGGER 特化** — targetCode 的依赖类型由 targetType（MICROFLOW/PROCESS）决定，PROCESS 类型由 Flowable 独立部署管理，归入 default 假定存在避免误报
4. **JSON 解析回退** — 解析失败时回退正则提取（`regexExtractReferences`），best-effort 不阻断整体校验

引用字段→依赖类型映射：`entityCode`→ENTITY、`formCode`→FORM、`listCode`→LIST、`connectorCode`→CONNECTOR、`ruleCode`→RULE、`microflowCode`→MICROFLOW。

### 9. 异步处理的代理失效规避

`LowCodeImportAsyncProcessor` 独立为 `@Component` 承载 `@Async` 方法，避免同类自调用导致 Spring AOP 代理失效（`this.processImportAsync(...)` 不走代理，`@Async` 不生效）。由 `LowCodeDataImportExportServiceImpl.importExcel` 委托调用，在独立线程中完成 Excel 解析、按行 create、失败明细记录与任务状态更新。

`LowCodeAsyncConfig` 通过 `@EnableAsync` 启用 Spring 异步支持，使用默认 `SimpleAsyncTaskExecutor`（每次调用新建线程），适用于低代码数据导入这种低频操作。

### 10. AOP 配置审计的 best-effort 策略

`ConfigAuditAspect` 拦截 8 大 ConfigService 的写方法，通过 `@Around` 在方法执行前后查询 before/after 快照，写入 `pms_lowcode_config_audit_log`。关键设计：

- **best-effort** — 所有异常被吞掉（catch + WARN 日志），不阻断主业务
- **Service 类简单名映射** — 通过 `SERVICE_TO_CONFIG_TYPE` 静态 Map 将 ServiceImpl 类名映射到 configType
- **方法名识别动作** — save/saveOrUpdate/create→CREATE、update/updateById→UPDATE、removeById/removeByIds/delete→DELETE
- **性能权衡** — 写操作本身已含数据库事务，多查一次 before 快照（按 ID 查询）的代价可接受

### 11. Flowable 任务回调的 Bean 命名约定

`ProcessTaskCallbackListener` 的 Bean 名固定为 `processTaskCallbackListener`（通过 `@Component("processTaskCallbackListener")` 显式指定），与 `pms-workflow` 的 `oaTaskListener` 同机制。在 BPMN UserTask 上配置 `<flowable:taskListener event="complete" delegateExpression="${processTaskCallbackListener}"/>` 接入。这种约定使得流程设计器无需知道 Bean 全限定名，仅需引用固定 ID。

回调微流失败仅记 ERROR 日志，不向 Flowable 引擎上抛，不阻断流程主事务（与 OaTaskListener 的 best-effort 策略一致）。

### 12. 循环依赖的 @Lazy 打破

`PromotionGateService` 与 `EnvironmentPromotionService` 存在相互引用：
- `PromotionGateService` 调用 `EnvironmentPromotionService.validatePackageDependencies` 校验依赖完整性
- `EnvironmentPromotionService` 调用 `PromotionGateService.check` 构建管道状态

通过在 `PromotionGateService` 构造器对 `EnvironmentPromotionService` 使用 `@Lazy` 注入打破循环依赖，避免 Spring Boot 默认禁用循环引用时启动失败。

### 13. 预置模板的幂等初始化

`LowCodeTemplateInitializer` 实现 `ApplicationRunner`，应用启动时检查四张配置表是否为空，为空才加载预置模板。导入采用 `importConfig`：若 code 冲突会自动追加数字后缀，因此重复启动不会抛错也不会污染已有配置。这种设计保证了：
- 首次启动自动注入业务模板（项目创建表单、资产入库表单等）
- 重启不会重复导入
- 用户修改模板后不会被覆盖

### 14. 多数据库方言的 DDL 生成

`DdlGenerator` 接口抽象方言差异，`DdlGeneratorFactory` 按 `LowCodeDataSource.dbType` 选择实现：
- `MySQLDdlGenerator`（默认）— MySQL 方言
- `PostgreSQLDdlGenerator` — PostgreSQL 方言
- `SqlServerDdlGenerator` — SQL Server 方言

接口方法覆盖完整 DDL 场景：`generateCreateTable`（含主键/索引/外键约束）、`generateAddColumn`、`generateDropColumn`、`generateCreateIndex`、`generateJunctionTable`（M2M 中间表）、`generateDropIndex`、`generateAlterColumn`。CREATE TABLE 支持 `entityIdToTableName` 映射正确推导外键目标表名（避免依赖字段名约定猜测目标表）。
