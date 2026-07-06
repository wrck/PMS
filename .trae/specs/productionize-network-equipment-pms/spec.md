# Productionize Network Equipment PMS Spec

## Why

当前 network-equipment-pms 项目（11 个后端模块 + Vue 3 前端 + 23 个 Flyway 迁移）距生产级交付存在 9 类阻断性短板：**安全控制缺失**（无限流/无字段加密/无 XSS 过滤/密码字段未保护/Bean Validation 缺失）、**可观测性栈空白**（无 Actuator/Prometheus/Grafana/分布式追踪/结构化日志）、**数据完整性弱**（无外键约束/无乐观锁/无幂等性/无 Saga 补偿）、**集成层不稳定**（无熔断/无 Token 缓存/无统一异常处理）、**部署链路非生产级**（凭证明文/无备份/无自动化部署）、**低代码能力完全缺失**、**前端类型安全与设计系统不统一**、**测试基础设施不完整**、**用户引导与技术支持体系缺失**。本 spec 系统性补齐上述短板。

## What Changes

### 安全加固
- 新增 `@RateLimit`/`@FieldEncrypt`/`@Idempotent` 注解 + AOP 切面（Bucket4j + Redis + AES-256-GCM）
- 新增 `XssFilter`（Jsoup）+ 安全响应 Headers 过滤器
- 34 个业务 Controller 全量补齐 `@PreAuthorize` + `@OperLog` + `@Valid`
- `SysUser.password` 增加 `@JsonProperty(access = WRITE_ONLY)`，`phone`/`email` 字段级加密
- 30+ 实体字段补齐 Bean Validation

### 可观测性栈
- Spring Boot Actuator + Micrometer + Prometheus，暴露 `/actuator/prometheus`
- `deploy/grafana/` 5 个生产级仪表盘 + 数据源 + 告警规则
- `deploy/prometheus/` + `deploy/alertmanager/` 配置
- logstash-logback-encoder 结构化 JSON 日志 + MDC TraceId
- OpenTelemetry SDK 自动埋点 HTTP/MyBatis/Redis + Jaeger
- MyBatis 慢 SQL 拦截器（>1s WARN，>5s ERROR + 告警）

### 测试基础设施
- 5 个核心模块 Service 层单元测试覆盖率 ≥70%
- Testcontainers 集成测试（MySQL 8.0/Redis 7/Flowable 7）
- WebSocket/文件上传/Excel 集成测试 + 前端 Vitest/Playwright 测试

### 数据完整性
- Flyway V24：核心业务表外键约束 + V25 乐观锁字段
- 5 个核心实体 `@Version` 乐观锁
- `@Idempotent` + Redis SETNX 幂等性（24h TTL）
- Saga 补偿事务（结算推送 FP 失败自动回滚）

### 集成层可靠性
- Resilience4j：熔断器（50% 失败率）+ 隔离仓 + 限流器 + 重试（指数退避）
- 分布式 OAuth2 Token 缓存（Redis Hash + 自动续期 + 单飞防击穿）
- OA 集成接入 RetryService + 统一异常处理 + IntegrationLog 全量记录

### 部署链路生产化
- Docker Compose 拆分（infra/app/observe 三套）+ `.env` 凭证安全
- Dockerfile 多阶段构建 + 非 root 用户 + 健康检查
- 备份脚本（MySQL + binlog + Redis RDB）+ DR 演练脚本
- 蓝绿部署脚本（健康检查 + 自动回滚）

### 低代码模块
- 创建 `pms-lowcode` 模块：表单/列表/标签页/关联页设计器 + 渲染引擎
- 后端：4 个配置实体 + Mapper/Service/Controller + 模板导入导出
- 前端：可视化设计器（拖拽式 + 实时预览）+ 通用渲染器
- 配置 JSON Schema + 导入导出 + 10 个预置模板
- `/lowcode/:pageCode` 动态路由 + 权限集成

### 前端类型安全与 UI 完善
- TypeScript 严格模式 + 消除 `any` + 接口类型补齐
- 设计系统统一（design-tokens.scss）+ 响应式增强（三档断点）
- Dashboard 真实数据接入（项目数/在库设备/待办/本月交付 + 图表 + 待办列表 + 近期动态）

### 文档/用户引导/技术支持
- 部署文档 + 运维手册 + 故障排查 + API 规范 + 低代码指南
- 用户引导系统（首次登录 5 步引导 + 功能气泡 + 帮助中心）
- 技术支持（问题反馈 + 系统状态页 + 版本变更日志）

### **BREAKING**
- `SysUser` 序列化移除 `password` 字段
- Docker Compose 拆分后启动命令变更
- 数据库新增外键约束，历史脏数据可能导致迁移失败

## Impact

- **Affected specs**: harden-network-equipment-pms, refactor-network-equipment-pms
- **Affected code**: pms-common（新增 5 个子包）/ pms-system（SysUser 保护 + 实体校验）/ pms-admin（Controller 权限 + V24-V27 迁移）/ pms-integration（Resilience4j + Token 缓存）/ pms-implementation（Saga + 幂等）/ pms-lowcode（全新模块）/ deploy/（全新目录）/ pms-frontend（TypeScript + 设计系统 + 低代码设计器）/ docs/（全新目录）/ scripts/（3 个脚本）

## ADDED Requirements

### Requirement: 生产级安全控制
系统 SHALL 对所有业务接口实施 RBAC（`@PreAuthorize`）；SHALL 对写操作记录审计日志（`@OperLog`）；SHALL 对入参实施 Bean Validation（`@Valid` + JSR-380）；SHALL 通过 `@RateLimit` + Bucket4j + Redis 实现分布式限流；SHALL 通过 `XssFilter`（Jsoup）清洗富文本；SHALL 通过安全响应 Headers（CSP/HSTS/X-Frame-Options 等）防止 Web 攻击。

#### Scenario: 未授权访问被拒绝
- **WHEN** 用户 A（无 `system:user:create` 权限）调用 `POST /api/system/users`
- **THEN** 系统返回 403 Forbidden，并记录审计日志

#### Scenario: 限流触发
- **WHEN** 单一 IP 在 1 分钟内对 `/api/auth/login` 发起超过 10 次请求
- **THEN** 第 11 次返回 429 + `Retry-After: 60`

#### Scenario: XSS 输入被清洗
- **WHEN** 用户提交 `<script>alert('xss')</script>`
- **THEN** 入库内容被清洗为空或转义，脚本不执行

### Requirement: 敏感数据保护
系统 SHALL 对密码字段实施序列化脱敏（`@JsonProperty(access = WRITE_ONLY)`）；SHALL 对手机号/邮箱等 PII 字段实施 AES-256-GCM 加密存储（`@FieldEncrypt` + MyBatis TypeHandler）。

#### Scenario: 密码字段不返回前端
- **WHEN** 调用 `GET /api/system/users/1`
- **THEN** 响应 JSON 不含 `password` 字段

#### Scenario: PII 字段加密存储
- **WHEN** 创建用户手机号 `13800138000`
- **THEN** 数据库存储为 AES-256-GCM 密文，查询时自动解密返回明文

### Requirement: 全链路可观测性
系统 SHALL 暴露 `/actuator/prometheus`；SHALL 提供 5 个 Grafana 仪表盘（API/JVM/Business/Integration/Schedule）；SHALL 输出结构化 JSON 日志（含 traceId/spanId/userId）；SHALL 通过 OpenTelemetry 自动埋点导出至 Jaeger；SHALL 对慢 SQL（>1s WARN，>5s ERROR）告警。

#### Scenario: 慢 SQL 告警
- **WHEN** SQL 执行耗时 6 秒
- **THEN** 日志输出 ERROR + Alertmanager 触发 `SlowSqlAlert`

#### Scenario: 分布式追踪
- **WHEN** 用户创建项目，触发工作流 + 通知
- **THEN** Jaeger 可见完整调用链（HTTP → Service → MyBatis → Workflow → Notification）

### Requirement: 数据完整性保障
系统 SHALL 在核心业务表间建立外键约束（Flyway V24）；SHALL 对 5 个核心实体实施乐观锁（`@Version`）；SHALL 对关键写操作实施幂等性（`@Idempotent` + Redis SETNX，TTL 24h）；SHALL 对跨服务事务实施 Saga 补偿。

#### Scenario: 乐观锁冲突
- **WHEN** 用户 A 和 B 同时编辑同一项目（version=5）
- **THEN** 后提交者收到 409 + "数据已被其他用户修改，请刷新后重试"

#### Scenario: 幂等性保护
- **WHEN** 同一创建项目请求（IdempotentKey=abc123）被提交两次
- **THEN** 第二次返回首次结果，不产生重复数据

#### Scenario: Saga 补偿
- **WHEN** 结算单推送 FP 连续失败 5 次
- **THEN** Saga 自动回滚本地状态为 `PENDING_PUSH` + 推送告警 + 记录 IntegrationLog

### Requirement: 集成层高可用
系统 SHALL 通过 Resilience4j 配置熔断器（50% 失败率，30s 半开）+ 隔离仓（最大并发 10）+ 限流器（10 QPS）+ 重试（指数退避 1/2/4/8/16min，上限 5 次）；SHALL 对 OAuth2 Token 实施分布式缓存（Redis Hash + 自动续期 + 单飞防击穿）；SHALL 对 OA 集成接入 RetryService + 统一异常处理 + IntegrationLog 全量记录。

#### Scenario: 熔断器触发
- **WHEN** FP 服务连续 5 次失败
- **THEN** 熔断器开启 30s，后续调用快速失败返回 503；30s 后半开探测 1 个请求

#### Scenario: Token 单飞防击穿
- **WHEN** Token 过期瞬间 10 个并发请求刷新
- **THEN** 仅 1 个请求实际调用 Token 端点，其余 9 个等待共享

### Requirement: 低代码可视化配置
系统 SHALL 提供表单/列表/标签页/关联页四类可视化设计器（拖拽 + 实时预览 + Schema 校验）；SHALL 提供通用渲染引擎；SHALL 支持配置导入导出 + 10 个预置模板；SHALL 通过 `/lowcode/:pageCode` 动态路由集成 + 复用权限体系。

#### Scenario: 表单设计器
- **WHEN** 用户拖拽组件配置字段并保存为 `formConfig`
- **THEN** `GET /api/lowcode/form/{code}` 返回配置，前端 `<LowCodeFormRenderer>` 渲染表单

#### Scenario: 模板复用
- **WHEN** 用户导出表单配置 JSON 再导入另一环境
- **THEN** 配置完整复现，导入时校验 Schema，冲突字段提示选择

### Requirement: 测试质量保障
系统 SHALL 对 5 个核心模块 Service 层覆盖率 ≥70%；SHALL 通过 Testcontainers 实施集成测试；SHALL 对 WebSocket/文件上传/Excel 实施集成测试；SHALL 对前端核心组件实施 Vitest；SHALL 通过 GitHub Actions CI 保障质量门禁（覆盖率 ≥70% + 重复率 ≤3% + 严重漏洞 = 0）。

### Requirement: 生产级部署与运维
系统 SHALL 提供 Docker Compose 拆分 + 凭证安全；SHALL 提供多阶段构建 + 非 root + 健康检查的 Dockerfile；SHALL 提供备份脚本 + DR 演练脚本；SHALL 提供蓝绿部署脚本；SHALL 提供完整部署文档 + 运维手册 + 故障排查指南。

### Requirement: 用户引导与技术支持
系统 SHALL 提供首次登录 5 步引导；SHALL 提供功能引导气泡；SHALL 提供帮助中心页面；SHALL 提供问题反馈入口；SHALL 提供系统状态页 + 版本变更日志。

## MODIFIED Requirements

### Requirement: Dashboard 数据接入
原 Dashboard 使用 mock 数据，SHALL 改为真实数据（项目总数/在库设备/待办任务/本月交付）+ 项目进展图表 + 待办列表 + 近期动态。

### Requirement: OAuth2 Token 缓存
原本地内存缓存 SHALL 升级为 Redis Hash 分布式缓存（自动续期 + 单飞防击穿 + 失效降级）。

### Requirement: 集成异常处理
原分散处理 SHALL 统一为 `IntegrationException` + `GlobalExceptionHandler` 标准错误响应 + Resilience4j 熔断 + IntegrationLog 全量记录。

### Requirement: Docker Compose 编排
原单文件凭证明文 SHALL 拆分为 infra/app/observe 三套 + `.env` 注入 + healthcheck + depends_on。

## REMOVED Requirements

### Requirement: Dummy 默认凭证
**Reason**: `application.yml` 含 `admin/123456` 默认凭证，存在安全隐患。
**Migration**: 移除默认凭证，首次启动通过 Flyway 创建随机密码并日志输出一次。
