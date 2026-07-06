# Checklist

## Phase 1: 安全加固

### Task 1: 业务 Controller 权限/审计/校验全量补齐
- [x] 34 个 Controller 写操作标注 `@PreAuthorize` + `@OperLog` + `@Valid`
- [x] Flyway V24 权限初始化 SQL 执行成功
- [x] 单元测试：未授权返回 403 + 审计日志

### Task 2: SysUser 密码字段保护
- [x] `SysUser.password` 标注 `@JsonProperty(access = WRITE_ONLY)`
- [x] 密码修改使用 BCryptPasswordEncoder
- [x] `LoginResponse` 不含 password 字段
- [x] 单元测试：响应不含 password

### Task 3: 实体 Bean Validation 注解补齐
- [x] 30+ 实体字段标注 JSR-380 注解
- [x] DTO 类标注校验注解
- [x] `GlobalExceptionHandler` 处理 `MethodArgumentNotValidException` 返回 400
- [x] 单元测试：非法入参返回 400

### Task 4: Rate Limiting 限流（Bucket4j + Redis）
- [x] `@RateLimit` 注解定义完成
- [x] `RateLimitAspect` 使用 Bucket4j + Redis + SpEL
- [x] `RateLimitFilter` 敏感端点 IP 限流（10/分钟）
- [x] 关键写接口标注 `@RateLimit`
- [x] 单元测试：429 + Retry-After

### Task 5: XSS 过滤器与安全 Headers
- [x] `XssFilter` 基于 Jsoup 清洗
- [x] `SecurityHeadersFilter` 注入 7 个安全头
- [x] `SecurityConfig` 注册两个 Filter
- [x] 单元测试：XSS 清洗 + 响应头

### Task 6: 字段级加密（AES-256-GCM）
- [x] `@FieldEncrypt` 注解定义
- [x] `AesGcmEncryptor` 实现（密钥 + IV + Base64）
- [x] `EncryptTypeHandler` MyBatis TypeHandler
- [x] `FieldEncryptAspect` 兜底加解密
- [x] `SysUser.phone`/`email` 标注 `@FieldEncrypt` + Mapper XML typeHandler
- [x] `application.yml` 配置 `app.security.encrypt-key`（32 字节）
- [x] 单元测试：加密存储 + 解密读取

## Phase 2: 可观测性栈

### Task 7: Actuator + Micrometer + Prometheus
- [x] 引入 actuator + micrometer-registry-prometheus
- [x] `application.yml` 配置端点暴露 + 指标直方图
- [x] 自定义业务指标注册
- [x] `SecurityConfig` 放行 actuator 端点
- [x] 验证：`curl /actuator/prometheus` 返回指标

### Task 8: Grafana Dashboard 仪表盘
- [x] `api-overview.json` 创建（QPS/P50/P95/P99/错误率）
- [x] `jvm.json` 创建（堆内存/GC/线程）
- [x] `business-metrics.json` 创建（项目/资产/结算/里程碑/RMA）
- [x] `integration-health.json` 创建（D365/FP/OA）
- [x] `schedule-tasks.json` 创建
- [x] provisioning 配置（dashboards.yaml + datasources.yaml）
- [x] alerting/rules.yaml 告警规则
- [ ] 验证：Grafana 5 个仪表盘自动加载

### Task 9: 结构化日志 + 分布式追踪
- [x] logstash-logback-encoder:7.4 引入
- [x] `logback-spring.xml` 配置 JSON 日志 + MDC
- [x] `TraceIdFilter` 实现（X-Trace-Id + MDC）
- [x] `UserContextFilter` 实现（userId + MDC）
- [x] opentelemetry-spring-boot-starter:2.6.0 引入
- [x] `application.yml` 配置 OTLP + service.name + 采样率
- [x] docker-compose 增加 jaeger 服务
- [ ] 验证：日志 JSON 格式 + traceId + Jaeger 调用链

### Task 10: 慢 SQL 监控与告警
- [x] `SlowSqlInterceptor` 实现（>1s WARN，>5s ERROR + 指标）
- [x] `MyBatisPlusConfig` 注册 SlowSqlInterceptor
- [x] Grafana 增加慢 SQL 面板
- [x] Alertmanager 增加 SlowSqlAlert 规则
- [ ] 验证：慢 SQL 日志 + Grafana + 告警

## Phase 3: 测试基础设施

### Task 11: 5 个核心模块单元测试
- [ ] pms-project Service 覆盖率 ≥70%
- [ ] pms-asset Service 覆盖率 ≥70%
- [ ] pms-implementation Service 覆盖率 ≥70%
- [ ] pms-governance Service 覆盖率 ≥70%
- [ ] pms-notification + pms-file Service 覆盖率 ≥70%
- [ ] 验证：`mvn test` 通过 + 覆盖率报告 ≥70%

### Task 12: Testcontainers 集成测试
- [ ] testcontainers 1.19.7 引入
- [ ] `ProjectControllerIntegrationTest` 修复（移除 @Disabled）
- [ ] `AssetControllerIntegrationTest` 新增
- [ ] `SettlementControllerIntegrationTest` 新增
- [ ] `WorkflowControllerIntegrationTest` 新增
- [ ] 验证：`mvn verify` 集成测试通过

### Task 13: 集成测试 + 前端测试补齐
- [ ] WebSocket 集成测试
- [ ] 文件上传/下载/EXIF 集成测试
- [ ] Excel 导入导出集成测试
- [ ] 前端 Vitest 组件 + store + 工具
- [ ] 前端 Playwright E2E
- [ ] 验证：`npm run test` + `npm run e2e` 通过

## Phase 4: 数据完整性

### Task 14: 外键约束与乐观锁
- [x] Flyway V25 外键约束补齐
- [x] 历史脏数据清洗脚本
- [x] 5 个核心实体 `@Version` + Flyway V26 version 字段
- [x] `GlobalExceptionHandler` 处理乐观锁返回 409
- [x] 单元测试：并发编辑返回 409

### Task 15: 幂等性设计
- [x] `@Idempotent` 注解定义
- [x] `IdempotentAspect` Redis SETNX 实现
- [x] 关键写操作标注 `@Idempotent`
- [x] 前端生成 `X-Idempotent-Key` UUID
- [x] 集成测试：重复请求返回首次结果

### Task 16: Saga 补偿事务
- [x] `SettlementSaga` 协调器实现
- [x] `SagaCoordinator` 通用框架
- [x] `SettlementServiceImpl.submit` 改造
- [x] Saga 补偿：FP 失败回滚 + 告警 + IntegrationLog
- [x] 单元测试 + 集成测试验证补偿

## Phase 5: 集成层可靠性

### Task 17: Resilience4j 熔断/隔离/限流
- [x] resilience4j-spring-boot3:2.2.0 引入
- [x] D365/FP/OA 配置 CircuitBreaker/Bulkhead/RateLimiter/Retry
- [x] 三个 IntegrationServiceImpl 标注注解
- [x] `IntegrationConfig` 事件监听器（指标 + 告警）
- [x] 集成测试：熔断器开启/半开/关闭

### Task 18: 分布式 OAuth2 Token 缓存
- [x] `OAuthTokenCache` 接口定义
- [x] `RedisOAuthTokenCache` 实现（Hash + TTL + 续期）
- [x] `TokenRefreshLock` 实现（SETNX 互斥锁）
- [x] 三个 IntegrationServiceImpl 改造
- [x] Token 失效降级（3 次失败告警）
- [x] 集成测试：单飞防击穿

### Task 19: OA 集成接入 RetryService + 统一异常处理
- [x] `IntegrationException` 定义
- [x] `GlobalExceptionHandler` 处理 IntegrationException
- [x] `OaIntegrationServiceImpl` 接入 RetryService + @Retry + IntegrationLog
- [x] `OaTaskListener` 异常独立于主流程（REQUIRES_NEW）
- [x] 集成测试：重试 + 主流程不受影响

## Phase 6: 部署链路生产化

### Task 20: Docker Compose 拆分与凭证安全
- [x] `docker-compose.infra.yml` 创建
- [x] `docker-compose.app.yml` 创建
- [x] `docker-compose.observe.yml` 创建
- [x] `.env.example` 创建
- [x] `.gitignore` 增加 `.env`
- [x] `application.yml` 敏感配置改为 `${ENV_VAR}`
- [x] 验证：infra 启动 + healthcheck 通过

### Task 21: Dockerfile 安全加固
- [x] `Dockerfile.backend` 多阶段构建
- [x] 非 root 用户 pms（UID 1000）
- [x] HEALTHCHECK
- [x] `Dockerfile.frontend` 多阶段构建
- [x] Mock 服务 Dockerfile 加固
- [x] 验证：build + run 非 root + healthcheck

### Task 22: 备份脚本与 DR 演练
- [x] `scripts/backup.sh` 创建
- [x] `scripts/restore.sh` 创建
- [x] `scripts/dr-drill.sh` 创建
- [x] `scripts/backup-cleanup.sh` 创建
- [x] crontab 配置示例
- [x] 验证：backup + restore 流程成功

### Task 23: 自动化部署脚本
- [x] `scripts/deploy.sh` 创建（蓝绿部署）
- [x] `scripts/rollback.sh` 创建
- [x] `scripts/health-check.sh` 创建
- [x] `.github/workflows/deploy.yml` 完善
- [ ] 验证：deploy 成功 + 健康检查 + 可回滚

## Phase 7: 低代码模块实现

### Task 24: pms-lowcode 模块骨架与配置存储
- [x] `pms-lowcode/pom.xml` 创建
- [x] 根 pom.xml 增加 module + pms-admin 依赖
- [x] Flyway V27 创建 4 张配置表
- [x] 4 个实体 + Mapper + Service + Controller
- [x] 验证：`mvn compile -pl pms-lowcode` 成功

### Task 25: 表单设计器与渲染引擎
- [x] `FormConfig` JSON Schema 规范
- [x] `FormController` CRUD + 导入导出
- [x] 前端设计器（组件库 + 画布 + 属性面板）
- [x] 前端渲染器（动态渲染 + 校验 + 双向绑定）
- [x] 预置 3 个标准模板
- [x] 验证：拖拽 → 配置 → 保存 → 渲染 → 提交

### Task 26: 列表设计器与渲染引擎
- [x] `ListConfig` JSON Schema
- [x] `ListController` CRUD + 导入导出
- [x] 前端设计器
- [x] 前端渲染器（el-table + 分页 + 筛选 + 操作 + 字典）
- [x] 预置 3 个标准模板
- [x] 验证：配置 → 保存 → 渲染 → 操作生效

### Task 27: 标签页与关联页设计器
- [x] `TabConfig` + `RelatedPageConfig` JSON Schema
- [x] `TabController` + `RelatedPageController` CRUD
- [x] 前端设计器 + 渲染器
- [x] 预置 4 个标准模板
- [x] 验证：配置 → 保存 → 渲染正确

### Task 28: 低代码页面路由与权限集成
- [ ] 前端 `/lowcode/:pageCode` 动态路由
- [ ] 前端通用渲染入口
- [ ] 后端权限校验接口
- [ ] 菜单管理增加"低代码页面"类型
- [ ] `lowcode_*_config` 增加 `permission` 字段
- [ ] 集成测试验证权限

## Phase 8: 前端类型安全与 UI 完善

### Task 29: TypeScript 类型安全修复
- [x] `tsconfig.app.json` 启用 strict + noImplicitAny + strictNullChecks
- [x] 消除 `any` 类型
- [x] `types/api.d.ts` 定义所有响应类型
- [x] `api/*.ts` 返回类型改为 `Promise<Result<具体类型>>`
- [x] ESLint 增加 `@typescript-eslint/no-explicit-any` 规则
- [x] 验证：`npm run lint` + `npm run build` 无错误

### Task 30: 设计系统统一与响应式增强
- [x] `styles/design-tokens.scss` 创建
- [x] `styles/elements-overrides.scss` 创建
- [x] `styles/responsive.scss` 断点 mixin
- [x] 业务页面响应式栅格
- [x] 表单移动端单列 + 桌面双列；列表移动端卡片 + 桌面表格
- [x] 验证：三档断点布局正确

### Task 31: Dashboard 真实数据接入
- [x] 后端 `dashboardStats()` 返回 4 项核心数据
- [x] 后端 `projectTrend()` 返回 6 月分布
- [x] 后端 `todoList()` 返回 Top 5 待办
- [x] 后端 `recentActivities()` 返回最近 10 条日志
- [x] 前端 Dashboard 改造（真实接口 + ECharts + 待办 + 动态）
- [x] 验证：Dashboard 显示真实数据

## Phase 9: 文档/用户引导/技术支持

### Task 32: 部署文档与运维手册
- [ ] `docs/deployment.md` 创建
- [ ] `docs/operations.md` 创建
- [ ] `docs/troubleshooting.md` 创建
- [ ] `docs/api-spec.md` 创建
- [ ] `docs/lowcode-guide.md` 创建
- [ ] 验证：按文档可独立部署/运维/排查

### Task 33: 用户引导系统
- [ ] 前端 `UserGuide` 首次登录引导（driver.js 5 步）
- [ ] `useFirstLogin.ts` 检测首次登录
- [ ] 前端 `HelpBubble` 功能气泡
- [ ] 前端 `views/help/index.vue` 帮助中心
- [ ] 后端 `/api/system/help-content` 接口
- [ ] 验证：首次登录触发引导 + 完成后不再触发

### Task 34: 技术支持与反馈机制
- [ ] 前端 `FeedbackButton` 浮动按钮
- [ ] 后端 `FeedbackController` + `Feedback` 实体 + Mapper/Service
- [ ] Flyway V28 创建 `sys_feedback` 表
- [ ] 前端 `views/system-status/index.vue` 系统状态页
- [ ] 前端 `views/changelog/index.vue` 版本日志
- [ ] `CHANGELOG.md` 创建
- [ ] 验证：提交反馈 + 管理员查看 + 状态页正确
