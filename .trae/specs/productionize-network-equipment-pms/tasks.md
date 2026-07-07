# Tasks

## Phase 1: 安全加固

- [x] Task 1: 业务 Controller 权限/审计/校验全量补齐
  - [x] SubTask 1.1: 34 个业务 Controller 写操作补齐 `@PreAuthorize` + `@OperLog` + `@Valid`
  - [x] SubTask 1.2: Flyway V24 权限初始化 SQL（sys_menu + sys_role_menu）
  - [x] SubTask 1.3: Controller 权限单元测试（403 + 审计日志）

- [x] Task 2: SysUser 密码字段保护
  - [x] SubTask 2.1: `SysUser.password` 增加 `@JsonProperty(access = WRITE_ONLY)`
  - [x] SubTask 2.2: `SysUserServiceImpl` 密码 BCrypt 加密
  - [x] SubTask 2.3: `LoginResponse` 移除敏感字段
  - [x] SubTask 2.4: 单元测试验证 password 不返回

- [x] Task 3: 实体 Bean Validation 注解补齐
  - [x] SubTask 3.1: 30+ 实体字段补齐 JSR-380（SysUser/Project/Asset/ImplTask/Settlement/ChangeRequest/Risk/Issue/Rma/Warranty/PunchList/Deliverable）
  - [x] SubTask 3.2: DTO 类补齐校验注解（LoginRequest/SettlementCreateRequest/StartProcessRequest）
  - [x] SubTask 3.3: `GlobalExceptionHandler` 处理 `MethodArgumentNotValidException` 返回 400
  - [x] SubTask 3.4: 单元测试验证非法入参返回 400

- [x] Task 4: Rate Limiting 限流（Bucket4j + Redis）
  - [x] SubTask 4.1: `annotation/RateLimit.java`（key/capacity/refillTokens/refillPeriodSeconds）
  - [x] SubTask 4.2: `aspect/RateLimitAspect.java`（Bucket4j + Redis + SpEL）
  - [x] SubTask 4.3: `filter/RateLimitFilter.java`（敏感端点 IP 限流 10/分钟）
  - [x] SubTask 4.4: 关键写接口标注 `@RateLimit`
  - [x] SubTask 4.5: 单元测试验证 429 + Retry-After

- [x] Task 5: XSS 过滤器与安全 Headers
  - [x] SubTask 5.1: `filter/XssFilter.java`（Jsoup 清洗 + XssHttpServletRequestWrapper）
  - [x] SubTask 5.2: `filter/SecurityHeadersFilter.java`（7 个安全头）
  - [x] SubTask 5.3: `SecurityConfig` 注册两个 Filter
  - [x] SubTask 5.4: 单元测试验证 XSS 清洗 + 响应头

- [x] Task 6: 字段级加密（AES-256-GCM）
  - [x] SubTask 6.1: `annotation/FieldEncrypt.java`
  - [x] SubTask 6.2: `crypto/AesGcmEncryptor.java`（密钥从配置 + IV 随机 + Base64）
  - [x] SubTask 6.3: `crypto/EncryptTypeHandler.java`（MyBatis TypeHandler）
  - [x] SubTask 6.4: `aspect/FieldEncryptAspect.java`（兜底加解密）
  - [x] SubTask 6.5: `SysUser.phone`/`email` 标注 `@FieldEncrypt` + Mapper XML typeHandler
  - [x] SubTask 6.6: `application.yml` 配置 `app.security.encrypt-key`
  - [x] SubTask 6.7: 单元测试验证加密存储 + 解密读取

## Phase 2: 可观测性栈

- [x] Task 7: Actuator + Micrometer + Prometheus
  - [x] SubTask 7.1: 引入 spring-boot-starter-actuator + micrometer-registry-prometheus
  - [x] SubTask 7.2: `application.yml` 配置端点暴露 + 指标直方图
  - [x] SubTask 7.3: 自定义业务指标（pms_project_created_total/pms_asset_status/pms_settlement_amount）
  - [x] SubTask 7.4: `SecurityConfig` 放行 actuator 端点

- [x] Task 8: Grafana Dashboard 仪表盘
  - [x] SubTask 8.1: `deploy/grafana/dashboards/api-overview.json`（QPS/P50/P95/P99/错误率）
  - [x] SubTask 8.2: `deploy/grafana/dashboards/jvm.json`（堆内存/GC/线程）
  - [x] SubTask 8.3: `deploy/grafana/dashboards/business-metrics.json`（项目/资产/结算/里程碑/RMA）
  - [x] SubTask 8.4: `deploy/grafana/dashboards/integration-health.json`（D365/FP/OA）
  - [x] SubTask 8.5: `deploy/grafana/dashboards/schedule-tasks.json`
  - [x] SubTask 8.6: `deploy/grafana/provisioning/` 配置（dashboards.yaml + datasources.yaml）
  - [x] SubTask 8.7: `deploy/grafana/alerting/rules.yaml`（API 错误率 >5%/JVM 堆 >85%/慢 SQL/熔断器）

- [x] Task 9: 结构化日志 + 分布式追踪
  - [x] SubTask 9.1: 引入 logstash-logback-encoder:7.4
  - [x] SubTask 9.2: `logback-spring.xml` 配置 JSON 结构化日志 + MDC
  - [x] SubTask 9.3: `trace/TraceIdFilter.java`（X-Trace-Id 提取/生成 + MDC）
  - [x] SubTask 9.4: `trace/UserContextFilter.java`（userId 注入 MDC）
  - [x] SubTask 9.5: 引入 opentelemetry-spring-boot-starter:2.6.0
  - [x] SubTask 9.6: `application.yml` 配置 OTLP + service.name + 采样率
  - [x] SubTask 9.7: docker-compose 增加 jaeger 服务

- [ ] Task 10: 慢 SQL 监控与告警
  - [x] SubTask 10.1: `mybatis/SlowSqlInterceptor.java`（>1s WARN，>5s ERROR + Micrometer 指标）
  - [x] SubTask 10.2: `MyBatisPlusConfig` 注册 SlowSqlInterceptor
  - [x] SubTask 10.3: Grafana 增加慢 SQL 面板
  - [x] SubTask 10.4: Alertmanager 增加 SlowSqlAlert 规则

## Phase 3: 测试基础设施

- [x] Task 11: 5 个核心模块单元测试
  - [x] SubTask 11.1: pms-project Service 覆盖率 ≥70%
  - [x] SubTask 11.2: pms-asset Service 覆盖率 ≥70%
  - [x] SubTask 11.3: pms-implementation Service 覆盖率 ≥70%
  - [x] SubTask 11.4: pms-governance Service 覆盖率 ≥70%
  - [x] SubTask 11.5: pms-notification + pms-file Service 覆盖率 ≥70%

- [x] Task 12: Testcontainers 集成测试
  - [x] SubTask 12.1: 引入 testcontainers 1.19.7 + mysql/redis/flowable
  - [x] SubTask 12.2: 修复 `ProjectControllerIntegrationTest`（移除 @Disabled）
  - [x] SubTask 12.3: 新增 `AssetControllerIntegrationTest`
  - [x] SubTask 12.4: 新增 `SettlementControllerIntegrationTest`
  - [x] SubTask 12.5: 新增 `WorkflowControllerIntegrationTest`

- [x] Task 13: 集成测试 + 前端测试补齐
  - [x] SubTask 13.1: WebSocket 集成测试
  - [x] SubTask 13.2: 文件上传/下载/EXIF 集成测试
  - [x] SubTask 13.3: Excel 导入导出集成测试
  - [x] SubTask 13.4: 前端 Vitest 组件 + store + 工具测试
  - [x] SubTask 13.5: 前端 Playwright E2E 测试

## Phase 4: 数据完整性

- [x] Task 14: 外键约束与乐观锁
  - [x] SubTask 14.1: Flyway V25 外键约束（pms_asset/pms_impl_task/pms_settlement/pms_milestone/pms_punch_list/pms_rma/pms_warranty/pms_change_request/pms_risk/pms_issue → pms_project）
  - [x] SubTask 14.2: 5 个核心实体 `@Version` + Flyway V26 version 字段
  - [x] SubTask 14.3: `GlobalExceptionHandler` 处理 `OptimisticLockingFailureException` 返回 409
  - [x] SubTask 14.4: 单元测试验证乐观锁冲突

- [x] Task 15: 幂等性设计
  - [x] SubTask 15.1: `annotation/Idempotent.java`（key/ttl/policy）
  - [x] SubTask 15.2: `aspect/IdempotentAspect.java`（Redis SETNX）
  - [x] SubTask 15.3: 关键写操作标注 `@Idempotent`
  - [x] SubTask 15.4: 前端生成 `X-Idempotent-Key` UUID
  - [x] SubTask 15.5: 集成测试验证重复请求返回首次结果

- [x] Task 16: Saga 补偿事务
  - [x] SubTask 16.1: `saga/SettlementSaga.java`（协调器 + 补偿动作）
  - [x] SubTask 16.2: `saga/SagaCoordinator.java`（通用框架）
  - [x] SubTask 16.3: `SettlementServiceImpl.submit` 改造调用 Saga
  - [x] SubTask 16.4: 单元测试 + 集成测试验证补偿

## Phase 5: 集成层可靠性

- [x] Task 17: Resilience4j 熔断/隔离/限流
  - [x] SubTask 17.1: 引入 resilience4j-spring-boot3:2.2.0
  - [x] SubTask 17.2: `application.yml` 配置 D365/FP/OA 的 CircuitBreaker/Bulkhead/RateLimiter/Retry
  - [x] SubTask 17.3: 三个 IntegrationServiceImpl 标注 `@CircuitBreaker` + `@Bulkhead` + `@Retry`
  - [x] SubTask 17.4: `IntegrationConfig` 注册事件监听器（指标 + 告警）
  - [x] SubTask 17.5: 集成测试验证熔断器流程

- [x] Task 18: 分布式 OAuth2 Token 缓存
  - [x] SubTask 18.1: `oauth/OAuthTokenCache.java` 接口
  - [x] SubTask 18.2: `oauth/RedisOAuthTokenCache.java`（Redis Hash + TTL + 自动续期）
  - [x] SubTask 18.3: `oauth/TokenRefreshLock.java`（Redis SETNX 互斥锁）
  - [x] SubTask 18.4: 三个 IntegrationServiceImpl 改造调用 `OAuthTokenCache.getToken()`
  - [x] SubTask 18.5: Token 失效降级（连续 3 次失败告警）
  - [x] SubTask 18.6: 集成测试验证单飞防击穿

- [x] Task 19: OA 集成接入 RetryService + 统一异常处理
  - [x] SubTask 19.1: `exception/IntegrationException.java`
  - [x] SubTask 19.2: `GlobalExceptionHandler` 处理 IntegrationException
  - [x] SubTask 19.3: `OaIntegrationServiceImpl` 接入 RetryService + @Retry + IntegrationLog
  - [x] SubTask 19.4: `OaTaskListener` 异常独立于主流程事务（REQUIRES_NEW）
  - [x] SubTask 19.5: 集成测试验证重试 + 主流程不受影响

## Phase 6: 部署链路生产化

- [x] Task 20: Docker Compose 拆分与凭证安全
  - [x] SubTask 20.1: `docker-compose.infra.yml`（MySQL + Redis + Flowable + volume + healthcheck）
  - [x] SubTask 20.2: `docker-compose.app.yml`（pms-admin + mock 服务 + depends_on）
  - [x] SubTask 20.3: `docker-compose.observe.yml`（Prometheus + Grafana + Alertmanager + Jaeger）
  - [x] SubTask 20.4: `.env.example` 模板
  - [x] SubTask 20.5: `.gitignore` 增加 `.env` + 原 docker-compose.yml 兼容入口
  - [x] SubTask 20.6: `application.yml` 敏感配置改为 `${ENV_VAR}`

- [x] Task 21: Dockerfile 安全加固
  - [x] SubTask 21.1: `Dockerfile.backend` 多阶段构建（maven 编译 + jre 运行）
  - [x] SubTask 21.2: 非 root 用户 pms（UID 1000）
  - [x] SubTask 21.3: HEALTHCHECK（curl /actuator/health）
  - [x] SubTask 21.4: `Dockerfile.frontend` 多阶段构建（node + nginx）
  - [x] SubTask 21.5: 三个 Mock 服务 Dockerfile 加固

- [x] Task 22: 备份脚本与 DR 演练
  - [x] SubTask 22.1: `scripts/backup.sh`（MySQL 全量 + binlog + Redis RDB）
  - [x] SubTask 22.2: `scripts/restore.sh`（恢复 + 校验）
  - [x] SubTask 22.3: `scripts/dr-drill.sh`（DR 演练）
  - [x] SubTask 22.4: `scripts/backup-cleanup.sh`（清理 30 天前备份）
  - [x] SubTask 22.5: crontab 配置示例

- [x] Task 23: 自动化部署脚本
  - [x] SubTask 23.1: `scripts/deploy.sh`（蓝绿部署 + 健康检查 + 回滚）
  - [x] SubTask 23.2: `scripts/rollback.sh`（回滚 + 健康检查）
  - [x] SubTask 23.3: `scripts/health-check.sh`（actuator + 业务接口 + DB + Redis）
  - [x] SubTask 23.4: `.github/workflows/deploy.yml` 完善

## Phase 7: 低代码模块实现

- [x] Task 24: pms-lowcode 模块骨架与配置存储
  - [x] SubTask 24.1: `pms-lowcode/pom.xml`（依赖 pms-common/pms-system/mybatis-plus/web/validation）
  - [x] SubTask 24.2: 根 pom.xml 增加 `<module>pms-lowcode</module>` + pms-admin 依赖
  - [x] SubTask 24.3: Flyway V27 创建 4 张配置表
  - [x] SubTask 24.4: 4 个实体 + Mapper + Service + Controller（CRUD + 按 code 查询 + 导入导出）

- [x] Task 25: 表单设计器与渲染引擎
  - [x] SubTask 25.1: `FormConfig` JSON Schema 规范（fields + layout）
  - [x] SubTask 25.2: `FormController` CRUD + 导入导出
  - [x] SubTask 25.3: 前端 `views/lowcode/form-designer/index.vue`（组件库 + 画布 + 属性面板）
  - [x] SubTask 25.4: 前端 `components/LowCodeFormRenderer/index.vue`（动态渲染 + 校验 + 双向绑定）
  - [x] SubTask 25.5: 预置 3 个标准表单模板

- [x] Task 26: 列表设计器与渲染引擎
  - [x] SubTask 26.1: `ListConfig` JSON Schema（columns + filters + operations + pagination + searchApi）
  - [x] SubTask 26.2: `ListController` CRUD + 导入导出
  - [x] SubTask 26.3: 前端 `views/lowcode/list-designer/index.vue`
  - [x] SubTask 26.4: 前端 `components/LowCodeListRenderer/index.vue`（el-table + 分页 + 筛选 + 操作 + 字典翻译）
  - [x] SubTask 26.5: 预置 3 个标准列表模板

- [x] Task 27: 标签页与关联页设计器
  - [x] SubTask 27.1: `TabConfig` + `RelatedPageConfig` JSON Schema
  - [x] SubTask 27.2: `TabController` + `RelatedPageController` CRUD + 导入导出
  - [x] SubTask 27.3: 前端设计器 + 渲染器组件
  - [x] SubTask 27.4: 预置 4 个标准模板

- [x] Task 28: 低代码页面路由与权限集成
  - [x] SubTask 28.1: 前端 `/lowcode/:pageCode` 动态路由
  - [x] SubTask 28.2: 前端 `views/lowcode/render/index.vue` 通用渲染入口
  - [x] SubTask 28.3: 后端 `LowCodePermissionController` 权限校验接口
  - [x] SubTask 28.4: 菜单管理增加"低代码页面"类型 + `permission` 字段
  - [x] SubTask 28.5: 集成测试验证权限

## Phase 8: 前端类型安全与 UI 完善

- [x] Task 29: TypeScript 类型安全修复
  - [x] SubTask 29.1: `tsconfig.app.json` 启用 strict + noImplicitAny + strictNullChecks
  - [x] SubTask 29.2: 消除 `any` 类型，替换为具体接口
  - [x] SubTask 29.3: `types/api.d.ts` 定义所有 API 响应类型
  - [x] SubTask 29.4: `api/*.ts` 返回类型改为 `Promise<Result<具体类型>>`
  - [x] SubTask 29.5: ESLint 增加 `@typescript-eslint/no-explicit-any` 规则

- [x] Task 30: 设计系统统一与响应式增强
  - [x] SubTask 30.1: `styles/design-tokens.scss`（颜色/间距/字体/阴影/圆角）
  - [x] SubTask 30.2: `styles/elements-overrides.scss`（Element Plus 覆盖）
  - [x] SubTask 30.3: `styles/responsive.scss` 断点 mixin（sm/md/lg/xl）
  - [x] SubTask 30.4: 业务页面响应式栅格
  - [x] SubTask 30.5: 表单移动端单列 + 桌面双列；列表移动端卡片 + 桌面表格

- [x] Task 31: Dashboard 真实数据接入
  - [x] SubTask 31.1: 后端 `ReportController.dashboardStats()`（项目/在库设备/待办/本月交付）
  - [x] SubTask 31.2: 后端 `ReportController.projectTrend()`（6 月状态分布）
  - [x] SubTask 31.3: 后端 `ReportController.todoList()`（Top 5 待办）
  - [x] SubTask 31.4: 后端 `ReportController.recentActivities()`（最近 10 条日志）
  - [x] SubTask 31.5: 前端 Dashboard 改造（真实接口 + ECharts + 待办 + 动态）

## Phase 9: 文档/用户引导/技术支持

- [x] Task 32: 部署文档与运维手册
  - [x] SubTask 32.1: `docs/deployment.md`（Docker Compose + 蓝绿部署 + 回滚 + 配置）
  - [x] SubTask 32.2: `docs/operations.md`（备份恢复 + DR + 日志 + 监控 + 告警）
  - [x] SubTask 32.3: `docs/troubleshooting.md`（FAQ + 错误码 + 故障树）
  - [x] SubTask 32.4: `docs/api-spec.md`（SpringDoc + 鉴权 + 错误规范）
  - [x] SubTask 32.5: `docs/lowcode-guide.md`（设计器 + Schema + 模板）

- [x] Task 33: 用户引导系统
  - [x] SubTask 33.1: 前端 `components/UserGuide/index.vue`（driver.js 5 步引导）
  - [x] SubTask 33.2: `composables/useFirstLogin.ts`（首次登录检测）
  - [x] SubTask 33.3: 前端 `components/HelpBubble/index.vue`（功能气泡）
  - [x] SubTask 33.4: 前端 `views/help/index.vue`（帮助中心）
  - [x] SubTask 33.5: 后端 `/api/system/help-content` 接口

- [x] Task 34: 技术支持与反馈机制
  - [x] SubTask 34.1: 前端 `components/FeedbackButton/index.vue`（反馈表单）
  - [x] SubTask 34.2: 后端 `FeedbackController` + `Feedback` 实体 + Mapper/Service
  - [x] SubTask 34.3: Flyway V28 创建 `sys_feedback` 表
  - [x] SubTask 34.4: 前端 `views/system-status/index.vue`（系统状态页）
  - [x] SubTask 34.5: 前端 `views/changelog/index.vue`（版本日志）
  - [x] SubTask 34.6: `CHANGELOG.md`（Keep a Changelog 格式）

# Task Dependencies

- Phase 1（Task 1-6）为后续阶段基础，优先完成
- Task 7 依赖 Task 1（权限补齐，避免 actuator 未授权）
- Task 8 依赖 Task 7（Prometheus 端点）
- Task 10 依赖 Task 7（Micrometer）
- Task 11-13 依赖 Task 1-6（安全加固完成）
- Task 14（外键约束）需在 Task 11-13 之前
- Task 15 依赖 Task 4（共用 Redis）
- Task 16 依赖 Task 17（Resilience4j 重试）
- Task 17 依赖 Task 7（Actuator 指标）
- Task 18 依赖 Task 4（Redis）
- Task 19 依赖 Task 17 + Task 18
- Task 20 依赖 Task 7-10（可观测性栈）
- Task 21 与 Task 20 并行
- Task 22-23 依赖 Task 20-21
- Task 24-28 独立于其他 Phase，可并行
- Task 29-31 依赖 Task 1-2
- Task 32 依赖所有其他 Task
- Task 33-34 依赖 Task 29-31

# Parallelizable Work

- Phase 1 Task 1-6 内部可并行
- Phase 2 Task 7-10 内部可并行
- Phase 7 Task 24-28 内部高度并行
- Phase 9 Task 32-34 内部可并行
