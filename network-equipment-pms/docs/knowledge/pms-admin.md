# pms-admin 模块知识库

> 源码路径：`/workspace/network-equipment-pms/pms-admin`
> 基础包名：`com.dp.plat.admin`
> 父项目：`com.dp.plat:network-equipment-pms:1.0.0-SNAPSHOT`
> 启动模块（Spring Boot Application entry point）

---

## 模块概述

**定位**：`pms-admin` 是 `network-equipment-pms`（网络设备项目管理系统）多模块工程的**聚合启动模块**（Aggregator / Bootstrap Module）。它本身不承载独立业务领域逻辑，而是承担以下三类职责：

1. **统一启动入口**：提供 `PmsApplication` 主类，通过 `@SpringBootApplication(scanBasePackages = "com.dp.plat")` 扫描全部 14 个内部模块（含自身），将分散的业务模块装配为一个可独立运行的 Spring Boot 应用。
2. **聚合编排**：依赖全部 13 个业务模块（`pms-common` / `pms-system` / `pms-project` / `pms-asset` / `pms-implementation` / `pms-workflow` / `pms-integration` / `pms-governance` / `pms-notification` / `pms-file` / `pms-lowcode` / `pms-baseline` / `pms-deliverable`），在此层提供**跨模块聚合查询端点**（如交付件引用实体查询、报表统计、仪表盘），避免下层业务模块之间产生相互依赖。
3. **运行时基础设施托管**：托管 Flyway 数据库迁移脚本（`db/migration`）、Flowable BPMN 流程定义（`processes`）、Actuator 健康指示器、结构化日志配置、全局 `application*.yml` 环境配置，作为整个系统对外部署的唯一可执行产物（fat jar / war）。

**打包类型**：默认 `jar`（pom.xml 未显式声明 packaging），通过 `spring-boot-maven-plugin` 打包为可执行 fat jar。构建时排除 Lombok（`<excludes>` 配置），避免运行期对 Lombok 的依赖。

**artifactId / name**：`pms-admin` / `pms-admin`，`<description>` 为 `Spring Boot Application startup module`。

**在依赖图中的位置**：位于依赖图最顶层，是唯一被 `spring-boot-maven-plugin` 重新打包的模块；它向下聚合所有业务模块，自身不被任何内部模块依赖。

---

## 包结构

```
com.dp.plat.admin
├── PmsApplication.java            # Spring Boot 启动类（@SpringBootApplication + @MapperScan + @EnableScheduling + @EnableRetry）
├── controller                     # 聚合控制器（跨模块查询端点）
│   ├── DeliverableRefEntityController.java   # 交付件引用实体聚合查询
│   └── ReportController.java                 # 报表统计 + 仪表盘聚合
├── dto                            # 聚合层 DTO（仪表盘专用）
│   ├── ActivityItem.java          # 近期动态项（操作日志 + 登录日志）
│   ├── DashboardStats.java        # 仪表盘概要统计（8 个指标）
│   ├── ProjectTrendItem.java      # 项目趋势数据点（月 + 状态 + 数量）
│   └── TodoItem.java              # 待办事项项（任务 + 优先级 + 截止日）
├── health                         # Actuator 自定义健康指示器
│   ├── DatabaseHealthIndicator.java   # 主数据库健康检查（pmsDatabase）
│   └── RedisHealthIndicator.java      # Redis 健康检查（pmsRedis）
└── service                        # 聚合服务
    ├── ReportService.java         # 报表/仪表盘聚合服务接口
    └── impl
        └── ReportServiceImpl.java # 跨 project/asset/implementation/system 聚合实现
```

测试侧（`src/test/java/com/dp/plat/admin/`）提供了对聚合层的集成测试覆盖：`AssetControllerIntegrationTest` / `ProjectControllerIntegrationTest` / `SettlementControllerIntegrationTest` / `WorkflowControllerIntegrationTest` / `ExcelIntegrationTest` / `FileControllerIntegrationTest` / `BusinessMetricsTest` / `ReportServiceTest` / `WebSocketIntegrationTest` / `IdempotentIntegrationTest`，基类为 `testconfig/AbstractIntegrationTest`（基于 Testcontainers 启动真实 MySQL 8 + Redis 容器）。

---

## 启动与配置

### 启动类

`com.dp.plat.admin.PmsApplication`：

```java
@SpringBootApplication(scanBasePackages = "com.dp.plat")
@MapperScan({"com.dp.plat.**.mapper", "com.dp.plat.**.dao", "com.dp.plat.**.engine.ddl"})
@EnableScheduling
@EnableRetry
public class PmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(PmsApplication.class, args);
    }
}
```

关键注解读解：

| 注解 | 作用 |
|------|------|
| `@SpringBootApplication(scanBasePackages = "com.dp.plat")` | 组件扫描覆盖所有 14 个内部模块（基础包 `com.dp.plat`），自动装配各模块的 `@Configuration` / `@Service` / `@RestController` / `@Component` |
| `@MapperScan({"com.dp.plat.**.mapper", "com.dp.plat.**.dao", "com.dp.plat.**.engine.ddl"})` | MyBatis-Plus Mapper 扫描三条路径：常规 `mapper` 包、遗留 `dao` 包、低代码 DDL 引擎 `engine.ddl` 包 |
| `@EnableScheduling` | 启用 Quartz / `@Scheduled` 定时任务（如集成重试、质保到期扫描） |
| `@EnableRetry` | 启用 Spring Retry，配合 `@Retryable` 实现外部集成（D365/FP/OA）失败重试 |

### application.yml 关键配置

`src/main/resources/application.yml` 是开发环境默认配置（无 profile 激活时生效），涵盖以下域：

| 配置域 | 关键项 | 说明 |
|--------|--------|------|
| `server` | `port: 8080`，`context-path: /` | HTTP 服务端口与上下文路径 |
| `spring.application.name` | `network-equipment-pms` | 应用名（用于日志 / Actuator / OTel service.name 回退） |
| `spring.datasource` | HikariCP 连接池（min-idle=5，max-pool=20），MySQL 8 驱动 `com.mysql.cj.jdbc.Driver`，数据库名 `network_equipment_pms`，全部通过 `${SPRING_DATASOURCE_URL}` / `${MYSQL_USER}` / `${MYSQL_PASSWORD}` 环境变量覆盖 |
| `spring.data.redis` | host/port/database/password/timeout，`REDIS_PASSWORD` 环境变量覆盖 |
| `spring.flyway` | `enabled: true`，`locations: classpath:db/migration`，`baseline-on-migrate: true`，`baseline-version: 0`，`out-of-order: true`，`validate-on-migrate: false`（开发期跳过 checksum 校验，允许修改已应用脚本），`clean-disabled: true`（禁止 `flyway clean` 防误删） |
| `spring.quartz` | `job-store-type: memory`（内存存储，简化部署；如需集群持久化可改 jdbc），线程池 `threadCount: 5` |
| `flowable` | `database-schema-update: true`（启动时自动建/改 `act_*` 表），`async-executor-activate: false`（关闭异步执行器），`process-definition-cache-limit: 100` |
| `mybatis-plus` | `mapper-locations: classpath*:com/dp/plat/**/mapper/**/*.xml`，`map-underscore-to-camel-case: true`，`log-impl: StdOutImpl`（开发期打印 SQL），逻辑删除字段 `deleted`（0/1），主键 `id-type: auto` |
| `jwt` | `secret`（Base64 编码，`JWT_SECRET` 环境变量覆盖，生产必须覆盖），`expiration: 86400000`（24h） |
| `app.security.encrypt-key` | AES-256-GCM 字段级加密密钥（`APP_ENCRYPT_KEY` 覆盖） |
| `app.encrypt-key` | 低代码连接器凭据加密密钥（`APP_CONNECTOR_ENCRYPT_KEY` 覆盖） |
| `pms.file.storage` | `type: local`（本地磁盘，可选 minio/oss），`local.base-dir: ./pms-files`（`PMS_FILE_LOCAL_BASE_DIR` 覆盖） |
| `lowcode.encryption.key` | 低代码连接器凭据 AES 密钥（`LOWCODE_ENCRYPTION_KEY` 覆盖） |
| `liteflow` | `rule-source: ""`（低代码规则设计器动态传入 EL 表达式，无默认规则），`enable-log: false`，`thread-executor-number: 2` |
| `management` (Actuator) | 暴露 `health,info,metrics,prometheus,env,configprops,loggers,scheduledtasks,threaddump`，`/actuator` 基路径；Prometheus 导出 `step: 30s`；HTTP 请求直方图 + p50/p95/p99 分位 + SLO（50ms/100ms/200ms/500ms/1s/2s/5s）；全局标签 `application=pms`、`env=${spring.profiles.active:dev}` |
| `springdoc` | `swagger-ui.path: /swagger-ui.html`，`api-docs.path: /v3/api-docs` |
| `d365` / `fp` / `integration.oa` | 三个外部集成的 base-url / token-url / client-id / client-secret / scope / grant-type，全部支持环境变量覆盖 |
| `integration.retry` | `interval: 300000`（5 分钟），`max-retry: 3`，`backoff-multiplier: 2` |
| `resilience4j` | 三套实例（d365/fp/oa）的 CircuitBreaker / Bulkhead / RateLimiter / Retry 配置（详见下文「关键技术点」） |
| `otel` (OpenTelemetry) | `service.name: pms`，OTLP gRPC 上报至 `http://localhost:4317`，traces 采样率 `parentbased_traceidratio 0.1`（10%），metrics/logs exporter 关闭（避免与 Micrometer/Prometheus 重复） |

### 环境 Profile

| Profile 文件 | 激活方式 | 用途与差异 |
|--------------|----------|-----------|
| `application.yml` | 默认（无 profile） | 开发环境基线，HikariCP min-idle=5/max=20，Flyway 跳过 checksum 校验，SQL 打印到 stdout |
| `application-prod.yml` | `SPRING_PROFILES_ACTIVE=prod` | 生产环境：HikariCP min-idle=10/max=50；`jwt.secret` 仅从 `JWT_SECRET` 环境变量读取（缺失即快速失败，不提供内置密钥）；**关闭 Swagger UI 与 api-docs**（`springdoc.swagger-ui.enabled: false`）；日志级别 root=INFO、`com.dp.plat`=INFO |
| `application-mock.yml` | `SPRING_PROFILES_ACTIVE=mock` | 对接本地 mock 服务（`mock-d365:8091` / `mock-fp:8092` / `mock-oa:8093`），用于集成测试与本地联调；`com.dp.plat.integration` 日志级别降为 DEBUG |
| `application-test.yml`（test/resources） | `@ActiveProfiles("test")` | 集成测试专用：端口 0（随机端口）；数据源/Redis 由 `AbstractIntegrationTest` 的 `@DynamicPropertySource` 注入 Testcontainers 动态地址；Flyway `clean-disabled: false`（测试可重置）；Quartz `auto-startup: false`；JWT 专用测试密钥 + 1h 过期；外部集成全部指向 `http://localhost:0/dummy`；Resilience4j 关闭健康指标注册；Actuator 仅暴露 health；OTel/Prometheus/Swagger 全部关闭 |

---

## 聚合控制器

pms-admin 通过两个聚合控制器提供跨模块查询端点。这些端点之所以放在 admin 层而非各业务模块，是为了**避免业务模块间的横向依赖**（例如 `pms-deliverable` 不应直接依赖 `pms-implementation` / `pms-asset`）。

| 控制器 | 路径前缀 | 用途 | 跨模块聚合的模块 / Mapper |
|--------|----------|------|---------------------------|
| `DeliverableRefEntityController` | `/api/deliverable/ref-entity` | 交付件「实体引用」类型的统一查询：根据 `refEntityType`（TASK/ASSET/PHASE/PROJECT/DELIVERABLE/REPORT）路由到对应模块的 Service 查询实体概要与可选列表，供交付件引用选择器下拉使用 | `pms-implementation`（`IImplTaskService`）、`pms-asset`（`IAssetService`）、`pms-project`（`IProjectPhaseService` / `IProjectService`）、`pms-deliverable`（`DeliverableService`） |
| `ReportController` | `/api/report` | 报表统计与仪表盘聚合：项目交付统计、设备资产统计、实施效能统计、仪表盘概要、项目趋势、待办列表、近期动态 | `pms-project`（`ProjectMapper`）、`pms-asset`（`AssetMapper` / `AssetCategoryMapper` / `AssetModelMapper`）、`pms-implementation`（`ImplTaskMapper` / `AgentMapper` / `AgentScoreMapper`）、`pms-system`（`SysOperLogMapper` / `LoginLogMapper`）、`pms-asset.warranty`（`WarrantyMapper`）；报表聚合逻辑委托 `ReportService` |

### DeliverableRefEntityController 端点

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/{refEntityType}/{refEntityId}` | 查询引用实体概要信息，返回 `Map`（含 name / projectId / detailUrl 等），实体不存在抛 `BusinessException` |
| GET | `/list?refEntityType=&projectId=` | 查询可选引用实体列表（用于选择器下拉），每项含 id 与 name；`projectId` 可空（空时查全部） |

支持的 `refEntityType`：`TASK`（实施任务）、`ASSET`（资产）、`PHASE`（项目阶段）、`PROJECT`（项目）、`DELIVERABLE`（交付件）、`REPORT`（统计报告，无单实体，返回固定信息）。

### ReportController 端点

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/delivery?startDate=&endDate=` | 项目交付统计：按月分组发起数与完成数、进行中/已完成总数、平均交付周期（天）、延期率 |
| GET | `/asset` | 设备资产统计：按状态分组（IN_STOCK/ALLOCATED/IN_TRANSIT/SCRAPPED）、按分类分组、资产总值（关联 `pms_asset_model.standard_price`） |
| GET | `/implementation` | 实施效能统计：按月完成数与平均时长、按任务类型（OEM/AGENT）完成率与平均时长、代理商 Top10 排名（按 overallScore 降序） |
| GET | `/dashboard/stats` | 仪表盘概要（8 指标）：项目总数、进行中项目、在库设备、待办数、本月交付、本月新增项目、本月新增资产、告警数（逾期任务 + 30 天内到期质保） |
| GET | `/project/trend` | 项目趋势：最近 6 个月按月 + 状态分组（预填 6 个月保证 x 轴完整） |
| GET | `/todo/list?limit=` | 待办列表（Top N）：当前用户的开放态任务（PENDING/ACCEPTED/IN_PROGRESS），按截止日升序，批量加载项目避免 N+1 |
| GET | `/recent-activities?limit=` | 近期动态：合并操作日志与登录日志，按时间倒序取 Top N |

`ReportServiceImpl` 中聚合查询通过 MyBatis-Plus `selectCount` / `LambdaQueryWrapper` 实现，待办列表会按当前登录用户（`SecurityUtils.getCurrentUserId()`）过滤；趋势图在 Java 内存中分组以保持 MySQL / PostgreSQL 可移植性（不依赖 `DATE_FORMAT`）。

---

## 数据库迁移

pms-admin 托管整个系统的 Flyway 数据库迁移脚本，位于 `src/main/resources/db/migration/`，共 **86 个** `V*.sql` 文件（V1 — V86）。Flyway 配置：`baseline-version: 0`、`baseline-on-migrate: true`、`out-of-order: true`（允许乱序补录）、`validate-on-migrate: false`（开发期容忍 checksum 变更）、`placeholder-replacement: false`、`clean-disabled: true`。

### 迁移脚本清单与演进历程

| 版本 | 脚本 | 主旨 |
|------|------|------|
| V1 | `V1__init_system_tables.sql` | 系统管理基线：`sys_dept` / `sys_user` / `sys_role` / `sys_menu` / `sys_dict` 等核心表 |
| V2 | `V2__init_project_tables.sql` | 项目交付管理：`pms_project`（含 project_code / project_type / status 8 态）等 |
| V3 | `V3__init_asset_tables.sql` | 设备资产管理基线 |
| V4 | `V4__init_implementation_tables.sql` | 实施管理基线（ImplTask / Agent / AgentScore） |
| V5 | `V5__init_integration_tables.sql` | 外部集成基线 |
| V6 | `V6__init_sys_config.sql` | 系统配置表 `sys_config` |
| V7 | `V7__add_project_process_instance_id.sql` | 项目流程实例 ID 字段 |
| V8 | `V8__add_transfer_process_instance_id.sql` | 转移流程实例 ID 字段 |
| V9 | `V9__add_settlement_process_instance_id.sql` | 结算流程实例 ID 字段 |
| V10 | `V10__expand_milestone_type.sql` | 里程碑类型扩展 |
| V11 | `V11__expand_asset_status.sql` | 资产状态扩展 |
| V12 | `V12__init_punch_list.sql` | Punch List（问题清单）建表 |
| V13 | `V13__init_rma.sql` | RMA（退换货）建表 |
| V14 | `V14__init_deliverable_checklist.sql` | 交付件检查清单建表 |
| V15 | `V15__init_warranty.sql` | 质保管理建表 |
| V16 | `V16__expand_asset_task_agent_fields.sql` | 资产/任务/代理商字段扩展 |
| V17 | `V17__init_change_request.sql` | 变更申请建表 |
| V18 | `V18__init_risk_issue.sql` | 风险问题建表 |
| V19 | `V19__init_d365_sync_tables.sql` | D365 同步表建表 |
| V20 | `V20__init_notification_tables.sql` | 通知表建表 |
| V21 | `V21__init_attachment_tables.sql` | 附件表建表 |
| V22 | `V22__init_audit_log_tables.sql` | 审计日志表建表（操作日志 / 登录日志） |
| V23 | `V23__add_core_indexes.sql` | 核心索引补充 |
| V24 | `V24__init_permissions.sql` | 权限初始化 |
| V25 | `V25__add_foreign_keys.sql` | 外键约束补充 |
| V26 | `V26__add_version_fields.sql` | 乐观锁版本字段补充 |
| V27 — V57 | `V27__init_lowcode_tables.sql` — `V57__init_lowcode_process_sla.sql` | 低代码平台建表与扩展（实体表、配置版本、权限、DDL 备份日志、微流、规则、流程绑定、触发器、连接器、组件元数据、编辑锁、评论、发布记录、微流执行日志、规则扩展、触发器执行日志、发布审批链、流程绑定任务回调、规则测试用例、数据源、组件市场字段、配置版本分支字段、灰度发布、协作会话、配置模板、应用源导出、配置审计日志、数据导入导出、流程 SLA） |
| V58 | `V58__demo_employee_onboarding.sql` | 员工入职演示数据 |
| V59 | `V59__lowcode_demo_data_seed.sql` | 低代码演示数据种子 |
| V60 | `V60__fix_lowcode_permissions_and_columns.sql` | 低代码权限与列修复（PREPARE/EXECUTE 幂等写法） |
| V61 | `V61__seed_business_demo_data.sql` | 业务演示数据种子 |
| V62 | `V62__fix_lowcode_menus.sql` | 低代码菜单修复 |
| V63 | `V63__seed_leave_request_demo.sql` | 请假申请演示数据 |
| V64 | `V64__fix_demo_employee_permissions.sql` | 员工演示权限修复 |
| V65 | `V65__fix_demo_list_crud_operations.sql` | 演示列表 CRUD 修复 |
| V66 | `V66__fix_url_html_escape.sql` | URL HTML 转义修复 |
| V67 | `V67__fix_onboarding_task_emp_id_nullable.sql` | 入职任务 emp_id 可空修复 |
| V68 | `V68__seed_network_cutover_demo.sql` | 网络割接演示数据 |
| V69 | `V69__create_project_template_tables.sql` | 项目模板表建表 |
| V70 | `V70__alter_project_for_subproject.sql` | 项目子项目字段扩展 |
| V71 | `V71__create_project_phase_member_config.sql` | 项目阶段成员配置建表 |
| V72 | `V72__alter_impltask_and_create_task_tables.sql` | ImplTask 扩展 + 任务表建表 |
| V73 | `V73__create_task_dependency.sql` | 任务依赖表建表 |
| V74 | `V74__create_baseline_snapshot.sql` | 基线快照表建表 |
| V75 | `V75__deliverable_full_lifecycle.sql` | 交付件全生命周期：7 态状态机（DRAFT/SUBMITTED/REVIEWED/SIGNED/PUBLISHED/REFERENCED/ARCHIVED）+ 版本/签名/引用表 |
| V76 | `V76__create_approval_center.sql` | 审批中心建表 |
| V77 | `V77__seed_demo_data_and_permissions.sql` | 演示数据与权限种子 |
| V78 | `V78__complete_lowcode_permissions.sql` | 低代码权限补全 |
| V79 | `V79__complete_network_cutover_workspace.sql` | 网络割接工作区补全 |
| V80 | `V80__polish_network_cutover_runtime.sql` | 网络割接运行时打磨 |
| V81 | `V81__fix_missing_sys_menu_perms.sql` | 缺失菜单权限修复 |
| V82 | `V82__expand_demo_data.sql` | 演示数据扩展 |
| V83 | `V83__align_createby_updateby_to_varchar.sql` | createBy/updateBy 统一为 VARCHAR |
| V84 | `V84__align_deliverable_permissions.sql` | 交付件权限对齐 |
| V85 | `V85__unify_deliverable_type.sql` | 交付件类型统一 |
| V86 | `V86__deliverable_nature_type_and_ref.sql` | 交付件类型重构为「性质分类」（数据字典驱动）+ 引用实体字段（ref_entity_type / ref_entity_id / template_inherited） |

**演进主线**：
1. **V1 — V6**：系统/项目/资产/实施/集成五大领域基线建表。
2. **V7 — V26**：流程实例 ID 关联、领域扩展（里程碑/资产状态/Punch List/RMA/交付件检查清单/质保/变更/风险/D365 同步/通知/附件/审计日志）、索引/权限/外键/版本字段补强。
3. **V27 — V57**：低代码平台大规模建表（31 个脚本），覆盖实体、配置版本、权限、微流、规则、流程绑定、触发器、连接器、组件、灰度发布、协作会话等完整低代码能力。
4. **V58 — V68**：演示数据种子与低代码/业务修复。
5. **V69 — V86**：项目管理增强（模板/子项目/阶段成员/任务依赖/基线快照）、交付件全生命周期 7 态状态机、审批中心、网络割接工作区、交付件性质分类重构。

幂等写法演进：早期（V69）使用 `DELIMITER` + 存储过程实现幂等 ALTER；后期（V60、V86）改用 `PREPARE/EXECUTE` + `INFORMATION_SCHEMA` 查询，确保 Flyway 完全兼容。

---

## BPMN 流程定义

Flowable 工作流引擎的流程定义文件位于 `src/main/resources/processes/`，启动时由 Flowable 自动部署（`database-schema-update: true` 自动维护 `act_*` 表）。共 **5 个** `*.bpmn20.xml` 文件：

| 文件 | process id | 流程名 | 节点结构 | 用途 |
|------|-----------|--------|----------|------|
| `project-approval.bpmn20.xml` | `projectApproval` | 项目审批流程 | 开始 → PM审核 → 网关 → 部门经理审核 → 网关 → 结束/驳回终止 | 项目立项两级审批（PM → 部门经理） |
| `asset-transfer.bpmn20.xml` | `assetTransfer` | 资产转移流程 | 开始 → 源PM审核 → 网关 → 目标PM审核 → 网关 → 结束/驳回终止 | 资产跨项目转移两级审批（源 PM → 目标 PM） |
| `final-acceptance.bpmn20.xml` | `finalAcceptance` | 最终验收流程 | 开始 → 客户确认 → 网关 → PM审核 → 网关 → 结束/驳回终止 | 项目最终验收两级确认（客户 → PM） |
| `settlement-approval.bpmn20.xml` | `settlementApproval` | 结算审批流程 | 开始 → PM审核 → 网关 → 财务审核 → 网关 → 结束/驳回终止 | 项目结算两级审批（PM → 财务） |
| `network-cutover.bpmn20.xml` | `demo_network_cutover` | 网络割接流程 | 开始 → 风险与方案审核 → 网关 → 割接窗口与指挥确认 → 实施割接 → 业务验证 → 网关 → 复盘与归档/执行回退 → 结束 | 网络割接全流程（含回退分支），使用 candidateGroups（network_manager / change_manager / network_engineer / business_owner） |

**通用特征**：
- 前 4 个审批流程均使用 `flowable:skipExpression="${assignee == initiator}"` 实现「发起人即审批人时自动跳过」，避免自审。
- 前 4 个流程在每个 userTask 上挂载 `oaTaskListener`（`delegateExpression="${oaTaskListener}"`，create/complete 事件），将任务创建与完成镜像到致远 OA 系统。
- 前 4 个流程驳回走 `terminateEventDefinition`（终止结束事件），直接终止整个流程实例。
- `network-cutover` 是低代码演示流程，命名空间为 `http://dp.plat/pms/lowcode`，含完整 BPMNDI 图形坐标，使用候选组而非指派人，且包含回退（rollback）分支。

---

## 全局配置

pms-admin 本身只托管 `PmsApplication` 与少量聚合组件；全局基础设施配置分散在各业务模块，通过 `@SpringBootApplication(scanBasePackages = "com.dp.plat")` 统一装配：

| 配置类 | 所在模块 / 包 | 职责 |
|--------|--------------|------|
| `SecurityConfig` | `pms-system` / `com.dp.plat.system.config` | Spring Security 6 配置：无状态会话（STATELESS）、JWT 鉴权过滤器链（SecurityHeadersFilter → RateLimitFilter → XssFilter → JwtAuthenticationFilter → UsernamePasswordAuthenticationFilter）；放行 `/api/auth/**`、帮助中心公开端点、`/actuator/**`、Swagger；未认证返回 401 JSON、权限不足返回 403 JSON；`BCryptPasswordEncoder`；`@EnableMethodSecurity` 启用 `@PreAuthorize` |
| `RedisConfig` | `pms-system` / `com.dp.plat.system.config` | `@EnableCaching`；`RedisTemplate`（String key + GenericJackson2Json value）；`CacheManager` 默认 TTL 30 分钟 + 0~5 分钟随机抖动防雪崩，命名缓存 `sysDict` / `sysMenu` / `sysConfig` / `sysRole` 各 60 分钟；`disableCachingNullValues` 防穿透 |
| `OpenApiConfig` | `pms-system` / `com.dp.plat.system.config` | Springdoc OpenAPI 3 配置：Bearer JWT SecurityScheme |
| `MyBatisPlusConfig` | `pms-common` / `com.dp.plat.common.config` | `MybatisPlusInterceptor`（分页 + 乐观锁 + 自定义 InnerInterceptor 如数据权限）；`MetaObjectHandler` 自动填充 createTime/updateTime/createBy/updateBy/deleted |
| `WebMvcConfig` | `pms-common` / `com.dp.plat.common.config` | 注册 `IdempotentKeyInterceptor`（从 `X-IdempotentKey` 头读取幂等键写入 request attribute） |
| `AspectConfig` | `pms-common` / `com.dp.plat.common.config` | `@EnableAspectJAutoProxy`，启用限流/幂等/字段加密等切面 |

**pms-admin 自身的可观测性配置**：
- `DatabaseHealthIndicator`（Bean 名 `pmsDatabaseHealthIndicator`，健康检查名 `pmsDatabase`）：通过 `Connection.isValid(5)` 验证连接 + 查询 `pms_project` / `sys_user` 行数确认表结构可访问；与 Spring Boot 默认 `db` 指标并存互补。
- `RedisHealthIndicator`（Bean 名 `pmsRedisHealthIndicator`，健康检查名 `pmsRedis`）：通过 `RedisConnection.ping()` 验证 Redis 可用性；与默认 `redis` 指标并存互补。
- Actuator 暴露 `health,info,metrics,prometheus,env,configprops,loggers,scheduledtasks,threaddump`；`health.show-details: when_authorized`、`show-components: always`。
- Micrometer + Prometheus：HTTP 请求直方图 + p50/p95/p99 + SLO；全局标签 `application=pms` / `env`；Prometheus 导出 `step: 30s`。
- OpenTelemetry：OTLP gRPC 上报 Jaeger，traces 采样 10%，metrics/logs 关闭（避免与 Prometheus 重复）。
- Logback（`logback-spring.xml`）：dev/test 控制台普通文本（含 MDC traceId/userId/username）；prod/release 控制台 + JSON 结构化文件（`LogstashEncoder`，按天 + 100MB 滚动，保留 30 天，总上限 10GB，异步包装 `AsyncAppender` 队列 1024 / `neverBlock=true` 避免阻塞业务线程）。

---

## 模块依赖关系

pms-admin 在 pom.xml 中显式依赖全部 13 个业务模块，构成完整的可部署单元：

| 依赖模块 | artifactId | 在聚合层的主要用途 |
|----------|-----------|-------------------|
| `pms-common` | `pms-common` | 横切关注点（Result/异常/加密/限流/幂等/XSS/链路追踪/MyBatis 基础设施/SPI） |
| `pms-system` | `pms-system` | 用户/角色/菜单/字典/配置/权限/安全（SecurityConfig/RedisConfig/OpenApiConfig）/操作日志/登录日志 |
| `pms-project` | `pms-project` | 项目/项目阶段/项目模板/项目配置 |
| `pms-asset` | `pms-asset` | 资产/资产分类/资产型号/质保 |
| `pms-implementation` | `pms-implementation` | 实施任务/代理商/代理商评分 |
| `pms-workflow` | `pms-workflow` | Flowable 工作流（审批/转移/验收/结算）+ OA 任务监听器 |
| `pms-integration` | `pms-integration` | D365/FP/OA 外部集成 + Resilience4j 熔断/限流/重试 |
| `pms-governance` | `pms-governance` | 治理（Punch List/变更/风险） |
| `pms-notification` | `pms-notification` | 通知 |
| `pms-file` | `pms-file` | 文件存储（local/minio/oss） |
| `pms-lowcode` | `pms-lowcode`（显式 `${project.version}`） | 低代码平台（实体/微流/规则/连接器/组件/灰度发布） |
| `pms-baseline` | `pms-baseline` | 基线快照 |
| `pms-deliverable` | `pms-deliverable` | 交付件全生命周期（7 态状态机） |

**外部依赖**（pom.xml 显式声明）：
- `com.mysql:mysql-connector-j`（runtime）— MySQL 驱动
- `spring-boot-starter-data-redis` — Redis 客户端
- `spring-boot-starter-actuator` — 可观测性端点
- `io.micrometer:micrometer-registry-prometheus` — Prometheus 指标注册表
- `org.flywaydb:flyway-core` + `flyway-mysql` — 数据库迁移
- `net.logstash.logback:logstash-logback-encoder:7.4` — JSON 结构化日志
- `io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter:2.6.0` — OTel 自动埋点
- 测试：`spring-boot-starter-test` / `spring-security-test`（`@WithMockUser`）/ Testcontainers 1.19.7（testcontainers / mysql / junit-jupiter）/ `com.redis:testcontainers-redis:2.2.2`

**构建插件**：`spring-boot-maven-plugin`（重新打包为可执行 fat jar，排除 Lombok）。

---

## 关键技术点

1. **聚合层避免模块环依赖**：交付件引用实体查询（`DeliverableRefEntityController`）需要同时访问实施任务、资产、项目阶段、项目、交付件五个领域。若放在 `pms-deliverable` 内，则该模块需反向依赖 `pms-implementation` / `pms-asset` / `pms-project`，破坏分层。pms-admin 作为顶层聚合点，合法地持有所有模块的 Service 引用，是此类跨模块查询的唯一合法宿主。报表统计（`ReportController` / `ReportServiceImpl`）同理。

2. **仪表盘聚合的 N+1 规避**：`ReportServiceImpl.getTodoList` 先批量加载任务，再一次性 `selectList(in projectIds)` 加载关联项目构造 `Map<Long, Project>`，避免逐任务查询项目的 N+1 问题。趋势图在 Java 内存分组（预填 6 个月空桶）以保持 MySQL/PostgreSQL 可移植性。

3. **Flyway 乱序与开发期容忍**：`out-of-order: true` 允许补录历史版本号脚本（如 V60 修复早期问题）；`validate-on-migrate: false` 让开发期修改已应用脚本不会导致启动失败；`clean-disabled: true` 杜绝误删生产库。幂等写法从 V69 的 `DELIMITER`/存储过程演进到 V60/V86 的 `PREPARE/EXECUTE`，后者对 Flyway 兼容性更好。

4. **Flowable 自动建表与流程部署**：`flowable.database-schema-update: true` 启动时自动创建/更新 `act_*` 表；`processes/*.bpmn20.xml` 由 Flowable 自动部署；`async-executor-activate: false` 关闭异步执行器（任务监听器同步执行）；`process-definition-cache-limit: 100` 限制流程定义缓存。审批流程统一使用 `skipExpression` 跳过自审 + `oaTaskListener` 镜像致远 OA + `terminateEventDefinition` 终止驳回。

5. **多 Profile 安全分级**：开发环境（`application.yml`）提供内置 JWT 密钥与加密密钥便于本地启动；生产环境（`application-prod.yml`）`jwt.secret` 仅从 `JWT_SECRET` 读取（缺失快速失败）、关闭 Swagger、收紧日志级别；mock 环境对接本地 mock 服务；test 环境关闭一切外部调用与可观测性上报，由 Testcontainers 提供真实 MySQL 8 + Redis。

6. **Resilience4j 三实例统一治理**：D365/FP/OA 三个外部集成各自配置 CircuitBreaker（计数滑动窗口 20，失败率 ≥50% 熔断，30s 后半开，半开 5 次试探）、Bulkhead（信号量隔离，最大并发 10，等待 5s）、RateLimiter（50 次/秒，等待 10s）、Retry（最多 3 次，1s 起步指数退避上限 16s，仅对 IOException/TimeoutException 重试）。健康检查方法不加注解，避免熔断器 OPEN 时健康端点无法恢复。

7. **可观测性三件套**：Metrics（Micrometer + Prometheus，`/actuator/prometheus`，HTTP SLO + 业务指标）、Tracing（OpenTelemetry OTLP gRPC → Jaeger，10% 采样）、Logging（Logback JSON 结构化，MDC traceId/userId/username/requestUri/method，ELK/Loki 采集）。三者通过 traceId 关联。

8. **Mapper 扫描三路径**：`@MapperScan({"com.dp.plat.**.mapper", "com.dp.plat.**.dao", "com.dp.plat.**.engine.ddl"})` 同时覆盖常规 mapper 包、遗留 dao 包（兼容旧模块）、低代码 DDL 引擎包（`engine.ddl`，低代码动态建表/改表的 Mapper）。

9. **健康检查双轨制**：`DatabaseHealthIndicator` / `RedisHealthIndicator` 使用自定义 Bean 名（`pmsDatabase` / `pmsRedis`），与 Spring Boot 默认的 `db` / `redis` 指标并存互补——默认指标验证连接，自定义指标额外验证关键表行数与 ping，提供更细粒度的健康视图。
