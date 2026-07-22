# pms-notification 模块知识库

> 源码路径：`/workspace/network-equipment-pms/pms-notification`
> 基础包名：`com.dp.plat.notification`
> 父项目：`com.dp.plat:network-equipment-pms:1.0.0-SNAPSHOT`

---

## 模块概述

`pms-notification` 是网络设备 PMS 平台的**通知中心领域模块**，为全平台提供统一的消息投递能力，覆盖站内信落库、WebSocket 实时推送、邮件、OA 待办等多通道，并基于 Freemarker 提供可配置的模板化通知。

- **Maven 坐标**：`com.dp.plat:pms-notification:1.0.0-SNAPSHOT`，父工程为 `com.dp.plat:network-equipment-pms`。
- **artifactId / name**：`pms-notification`，description 为 `Notification center: in-app messages, multi-channel delivery, template engine`。
- **打包类型**：默认 `jar`（pom.xml 未显式声明 packaging，Maven 默认 `jar`）。
- **核心职责**：
  1. **站内信管理**：以 `pms_notification` 表为中心，记录每条通知的接收人、标题、正文、业务分类、业务类型、业务 id、已读状态与投递通道；
  2. **多通道并发投递**：`multiChannelSend` 支持 `IN_APP` / `WS` / `EMAIL` / `OA` 四种通道，任一通道失败仅记录日志、不阻塞其他通道；
  3. **模板化发送**：基于 Freemarker 渲染 `pms_notification_template` 表中的模板，调用方仅传 `templateCode + variables` 即可生成通知；
  4. **WebSocket 实时推送**：通过 Redis Pub/Sub 跨实例广播 + STOMP 广播频道 `/topic/notification/{userId}`，将通知实时推送给在线用户；
  5. **JWT 握手鉴权**：WebSocket 握手阶段从 `Authorization: Bearer` 头或 `?token=` 查询参数解析 JWT，提取 `userId` 后存入会话属性，无 token 或解析失败拒绝握手；
  6. **被多个业务模块依赖**：`pms-project`（里程碑逾期、Punch List 到期）、`pms-implementation`（实施域通知委托）、`pms-asset`（RMA 状态变更、质保到期预警）、`pms-lowcode`（评论 @提及通知）、`pms-admin`（聚合）等。

---

## 包结构

`com.dp.plat.notification` 下的子包组织如下：

| 子包 | 主要内容 |
|------|----------|
| `controller` | REST 控制器：`NotificationController`（站内信查询、已读管理、手动发送）、`NotificationTemplateController`（模板 CRUD 与按编码查询） |
| `entity` | 领域实体：`Notification`（表 `pms_notification`）、`NotificationTemplate`（表 `pms_notification_template`） |
| `mapper` | MyBatis-Plus Mapper：`NotificationMapper`、`NotificationTemplateMapper`（均继承 `BaseMapper`，无自定义 SQL） |
| `service` | 服务接口：`INotificationService`、`INotificationTemplateService` |
| `service.impl` | 服务实现：`NotificationServiceImpl`、`NotificationTemplateServiceImpl` |
| `template` | 模板引擎：`NotificationTemplateEngine`（基于 Freemarker，含内部 record `RenderedTemplate`） |
| `ws` | WebSocket 实时推送：`WebSocketConfig`（STOMP 端点 + JWT 握手拦截器）、`NotificationPublisher`（Redis Pub/Sub 发布）、`NotificationSubscriber`（Redis 订阅 + STOMP 推送） |

**实体继承说明**：两个实体均**不继承** `com.dp.plat.common.entity.BaseEntity`：
- `Notification` 使用独立的 `createdAt` / `createdBy` 审计字段，且不参与逻辑删除（通知一经产生即留存审计）；
- `NotificationTemplate` 使用独立的 `createdAt` / `updatedAt` 时间戳。

---

## 核心实体模型

### Notification — 站内通知（`pms_notification`）

建表脚本：`pms-admin/src/main/resources/db/migration/V20__init_notification_tables.sql`，索引补充脚本：`V23__add_core_indexes.sql`。

| 字段 | Java 类型 | 数据库列 | DB 类型 | 说明 |
|------|-----------|----------|---------|------|
| `id` | Long | `id` | BIGINT, AUTO_INCREMENT, PK | 主键（`@TableId(type = IdType.AUTO)`） |
| `userId` | Long | `user_id` | BIGINT, NOT NULL | 接收人用户 id |
| `title` | String | `title` | VARCHAR(200) | 通知标题 |
| `content` | String | `content` | TEXT | 通知正文 |
| `category` | String | `category` | VARCHAR(50) | 业务分类：`MILESTONE` / `TASK` / `APPROVAL` / `PUNCH_LIST` / `WARRANTY` / `RMA` / `SETTLEMENT` 等 |
| `bizType` | String | `biz_type` | VARCHAR(50) | 业务类型（如 `TASK_ASSIGNED`、`WARRANTY_EXPIRE_30`），与模板编码对应 |
| `bizId` | Long | `biz_id` | BIGINT | 关联业务记录 id |
| `readStatus` | String | `read_status` | VARCHAR(20), DEFAULT 'UNREAD' | 已读状态：`UNREAD` / `READ` |
| `channel` | String | `channel` | VARCHAR(20), DEFAULT 'IN_APP' | 投递通道：`IN_APP` / `WS` / `EMAIL` / `OA` |
| `createdAt` | LocalDateTime | `created_at` | DATETIME | 创建时间 |
| `createdBy` | Long | `created_by` | BIGINT | 创建人用户 id |

**索引**（V20 + V23）：
- `PRIMARY KEY (id)`
- `idx_user_read (user_id, read_status)` — V20 建表时创建，加速未读数查询
- `idx_user_created (user_id, created_at)` — V20 建表时创建，加速分页列表查询
- `idx_pms_notification_user_read_created (user_id, read_status, created_at)` — V23 补充，覆盖式复合索引
- `idx_pms_notification_biz (biz_type, biz_id)` — V23 补充，加速业务反查

**关系**：`Notification` 通过 `(biz_type, biz_id)` 与各业务模块（如 `pms_milestone`、`pms_punch_list`、`pms_rma`、`pms_warranty` 等）弱关联，无物理外键约束。

### NotificationTemplate — 通知模板（`pms_notification_template`）

建表脚本：`pms-admin/src/main/resources/db/migration/V20__init_notification_tables.sql`。

| 字段 | Java 类型 | 数据库列 | DB 类型 | 说明 |
|------|-----------|----------|---------|------|
| `id` | Long | `id` | BIGINT, AUTO_INCREMENT, PK | 主键（`@TableId(type = IdType.AUTO)`） |
| `templateCode` | String | `template_code` | VARCHAR(100), NOT NULL, UNIQUE | 模板编码（唯一），如 `TASK_ASSIGNED`、`WARRANTY_EXPIRE_30` |
| `subject` | String | `subject` | VARCHAR(500) | 通知标题模板，含 `${var}` Freemarker 占位符 |
| `body` | String | `body` | TEXT | 通知正文模板，含 `${var}` 占位符 |
| `variables` | String | `variables` | TEXT | 变量定义（JSON 数组），描述模板可用变量名与说明 |
| `description` | String | `description` | VARCHAR(500) | 模板描述 |
| `createdAt` | LocalDateTime | `created_at` | DATETIME | 创建时间 |
| `updatedAt` | LocalDateTime | `updated_at` | DATETIME | 更新时间 |

**索引**：
- `PRIMARY KEY (id)`
- `UNIQUE KEY uk_template_code (template_code)` — 模板编码唯一约束

**预置模板**：V20 脚本预置了 12 个标准通知模板，覆盖核心业务场景：

| templateCode | 描述 | 变量 |
|--------------|------|------|
| `MILESTONE_OVERDUE` | 里程碑逾期提醒 | projectName / milestoneName / planDate |
| `TASK_ASSIGNED` | 任务分派通知 | taskName / projectName / planEndDate |
| `TASK_DELEGATED` | 任务转派通知 | fromUser / taskName / projectName / planEndDate |
| `APPROVAL_TODO` | 审批待办通知 | approvalTitle / submitter |
| `PUNCH_LIST_DEADLINE` | 尾项清单到期提醒 | punchItemName / deadline / status |
| `WARRANTY_EXPIRE_90` | 质保到期 90 天预警 | assetName / assetCode / warrantyEndDate |
| `WARRANTY_EXPIRE_60` | 质保到期 60 天预警 | assetName / assetCode / warrantyEndDate |
| `WARRANTY_EXPIRE_30` | 质保到期 30 天预警 | assetName / assetCode / warrantyEndDate |
| `RMA_STATUS_CHANGE` | RMA 状态变更通知 | rmaNo / status |
| `SETTLEMENT_APPROVED` | 结算审批通过通知 | settlementNo / amount |
| `CHANGE_REQUEST_CCB` | 变更请求 CCB 评审通知 | crNo / title |
| `RISK_ESCALATED` | 风险升级提醒 | riskName / level |

---

## 通知渠道

`INotificationService.multiChannelSend` 支持四种通道，在 `NotificationServiceImpl` 中以常量定义并通过 `switch` 分发：

| 通道常量 | 通道值 | 投递方式 | 实现要点 |
|----------|--------|----------|----------|
| `CHANNEL_IN_APP` | `IN_APP` | 同步落库 | 调用 `notificationMapper.insert(notification)` 持久化到 `pms_notification` 表，状态默认 `UNREAD` |
| `CHANNEL_WS` | `WS` | 异步 + Redis Pub/Sub + STOMP 广播 | 调用 `NotificationPublisher.publish(userId, notificationId)`，发布到 Redis 频道 `pms:notification:broadcast`；各实例的 `NotificationSubscriber` 收到后通过 `SimpMessagingTemplate` 推送到 `/topic/notification/{userId}` |
| `CHANNEL_EMAIL` | `EMAIL` | 异步（占位实现） | 当前仅打印日志，生产环境可接入 `EmailService` |
| `CHANNEL_OA` | `OA` | 异步（占位实现） | 当前仅打印日志，生产环境可接入 `OaIntegrationService` 创建 OA 待办 |

**通道持久化策略**：当 channels 包含 `IN_APP` 或 `WS` 时，会先调用 `notificationMapper.insert(notification)` 落库以生成 `id`，供 WS 通道引用；如 `notification.id` 已存在则跳过重复落库。`EMAIL` / `OA` 通道不依赖 `id`，可独立投递。

**前端订阅约定**：前端使用原生 WebSocket API（不启用 SockJS）连接 STOMP 端点 `/ws`，订阅 `/topic/notification/{当前登录用户id}` 即可收到推送。该方案不依赖 STOMP CONNECT 帧的 Principal，更适合原生 WebSocket 客户端。

---

## 通知模板机制

### NotificationTemplateEngine

`com.dp.plat.notification.template.NotificationTemplateEngine` 是基于 Freemarker 的模板引擎，核心方法：

```java
public RenderedTemplate render(String templateCode, Map<String, Object> variables)
```

**渲染流程**：
1. 通过 `NotificationTemplateMapper.selectOne` 按 `template_code` 查询模板实体；
2. 模板不存在时抛出 `BusinessException("通知模板不存在: " + templateCode)`；
3. 对 `subject` 与 `body` 分别调用 `renderString(name, templateText, variables)`：
   - 每次渲染新建 `Configuration`（`VERSION_2_3_32`）+ `StringTemplateLoader`，避免多线程共享可变模板加载器的线程安全问题；
   - 模板文本为 `null` 或空串时直接返回 `""`；
   - 渲染过程抛出任何异常均被捕获并包装为 `BusinessException("通知模板渲染失败: " + e.getMessage())`；
4. 返回 `RenderedTemplate(subject, body)` record。

### RenderedTemplate

`NotificationTemplateEngine.RenderedTemplate` 是 Java 14+ record，作为渲染结果载体：

```java
public record RenderedTemplate(String subject, String body) {}
```

### 模板化发送流程

`INotificationService.sendByTemplate(templateCode, variables, userId, channels)`：

1. 调用 `templateEngine.render(templateCode, variables)` 得到 `RenderedTemplate`；
2. 通过 `Notification.builder()` 构造通知：`title = rendered.subject()`、`content = rendered.body()`、`bizType = templateCode`、`readStatus = "UNREAD"`、`createdAt = LocalDateTime.now()`；
3. 委托 `multiChannelSend(notification, channels)` 完成多通道投递。

模板不存在时 `sendByTemplate` 直接抛出 `BusinessException`，不会进入 `multiChannelSend`、不会落库、不会推送 WS。

---

## 异步发送机制

### 多通道并发投递

`NotificationServiceImpl.multiChannelSend` 的并发模型：

1. **前置校验**：`channels` 为 `null` 或空集合直接返回，不触发任何操作；
2. **同步落库**：当 channels 包含 `IN_APP` 或 `WS` 且 `notification.id == null` 时，先调用 `notificationMapper.insert(notification)` 生成 id（同时填充默认 `readStatus=UNREAD` 与 `createdAt=now()`）；
3. **并发分发**：对每个 channel 创建 `CompletableFuture.runAsync(...)` 任务加入 `futures` 列表：
   - `IN_APP`：无操作（已落库），仅记录 `log.debug`；
   - `WS`：调用 `notificationPublisher.publish(userId, id)`，异常被 `try-catch` 吞掉并记录 `log.error`；
   - `EMAIL` / `OA`：占位实现，仅打印 `log.info`，异常被吞掉；
   - 未知通道：记录 `log.warn`，不创建 future；
4. **等待完成**：`CompletableFuture.allOf(futures.toArray()).join()` 等待所有通道完成；因每个任务内部已吞掉异常，`join` 不会抛出 `CompletionException`。

### Redis Pub/Sub 跨实例广播

WS 通道的实时推送采用 **Redis Pub/Sub 广播 + STOMP 广播频道** 方案，解决多实例部署下的推送一致性问题：

**NotificationPublisher**（发布端）：
- 通道常量：`BROADCAST_CHANNEL = "pms:notification:broadcast"`；
- 通过 `StringRedisTemplate.convertAndSend(BROADCAST_CHANNEL, payload)` 发布 JSON 字符串 `{"userId":<uid>,"notificationId":<nid>}`；
- 在 `NotificationServiceImpl.multiChannelSend` 的 WS 分支中异步调用。

**NotificationSubscriber**（订阅端）：
- 实现 `org.springframework.data.redis.connection.MessageListener`；
- `onMessage` 解析 JSON 取得 `userId` 与 `notificationId`，调用 `notificationMapper.selectById(notificationId)` 加载完整通知；
- 通过 `SimpMessagingTemplate.convertAndSend("/topic/notification/" + userId, notification)` 推送到对应用户的 STOMP 广播频道；
- 通知不存在时记录 `log.warn` 并返回，处理异常被 `try-catch` 吞掉。

> Redis 频道订阅本身由 `pms-system` 的 `RedisConfig` 配置（将 `NotificationSubscriber` Bean 绑定到 `BROADCAST_CHANNEL`），本模块的 `NotificationSubscriber` 只负责消息处理逻辑。

### WebSocket 配置

`com.dp.plat.notification.ws.WebSocketConfig` 实现 `WebSocketMessageBrokerConfigurer`，开启 `@EnableWebSocketMessageBroker`：

- **STOMP 端点**：`/ws`，`setAllowedOriginPatterns("*")`，**不启用 SockJS**（前端使用原生 WebSocket API）；
- **消息代理**：`/topic`、`/queue` 为 broker 广播目的地前缀，`/app` 为应用目的地前缀（路由到 `@MessageMapping` 方法）；
- **心跳**：SimpleBroker 入站/出站心跳均为 10s（`HEARTBEAT_MS = 10_000L`），使用独立 `ThreadPoolTaskScheduler`（poolSize=1，线程名前缀 `ws-heartbeat-`）避免与 WebSocket 配置形成循环依赖；
- **消息大小上限**：入站 64KB（`MESSAGE_SIZE_LIMIT = 64 * 1024`）；
- **JWT 握手鉴权**：内部类 `JwtHandshakeInterceptor` 实现 `HandshakeInterceptor`，在 `beforeHandshake` 阶段：
  1. 优先从 `Authorization: Bearer xxx` 请求头提取 token；
  2. 兜底从 `?token=xxx` 查询参数提取（原生 WebSocket 客户端无法自定义请求头）；
  3. 使用 jjwt `Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token)` 解析，从 `claims.getSubject()` 取得 `userId`；
  4. 解析成功后将 `userId` 存入 `attributes`（即 STOMP 会话属性 `sessionAttributes["userId"]`），返回 `true`；
  5. 缺少 token 或解析失败返回 `false`，拒绝握手；
- **JWT 密钥**：从配置项 `${jwt.secret}` 读取，Base64 解码后通过 `Keys.hmacShaKeyFor` 构造 `SecretKey`，缓存到 `signingKey` 字段。

---

## Service 层与 API 端点

### INotificationService / NotificationServiceImpl

| 方法签名 | 行为 | 落库 / 推送动作 |
|----------|------|------------------|
| `Notification create(Notification)` | 创建一条站内信，`readStatus` 默认 `UNREAD`、`channel` 默认 `IN_APP`、`createdAt` 默认 `now()` | `notificationMapper.insert` |
| `boolean markAsRead(Long id)` | 标记单条通知为已读，仅更新 `id` 与 `readStatus=READ` | `notificationMapper.updateById` |
| `boolean markAllRead(Long userId)` | 批量标记指定用户的全部未读通知为已读 | `notificationMapper.update(entity, wrapper)`，wrapper 条件 `userId + readStatus=UNREAD` |
| `int unreadCount(Long userId)` | 统计未读条数，mapper 返回 null 时归零 | `notificationMapper.selectCount` |
| `IPage<Notification> list(int page, int size, Notification filter)` | 分页查询，按 `createdAt` 倒序，filter 中 `userId` / `category` / `readStatus` / `bizType` / `bizId` 作为可选过滤条件 | `notificationMapper.selectPage` |
| `void multiChannelSend(Notification, Set<String> channels)` | 多通道并发投递，详见上文「异步发送机制」 | IN_APP: `insert`；WS: `publisher.publish`；EMAIL/OA: 占位 |
| `void sendByTemplate(String templateCode, Map variables, Long userId, Set<String> channels)` | 模板渲染 + 多通道投递，详见上文「通知模板机制」 | `templateEngine.render` → 构造 `Notification` → `multiChannelSend` |

### INotificationTemplateService / NotificationTemplateServiceImpl

继承 `IService<NotificationTemplate>`，自定义方法：

| 方法签名 | 行为 |
|----------|------|
| `NotificationTemplate getByCode(String templateCode)` | 按 `template_code` 查询单条模板，不存在返回 `null` |
| `IPage<NotificationTemplate> list(int page, int size)` | 分页查询模板，按 `createdAt` 倒序 |
| `boolean save(NotificationTemplate entity)`（重写） | 新建模板，`createdAt` 为 null 时填充 `now()`，`updatedAt` 一律填充 `now()` |
| `boolean updateById(NotificationTemplate entity)`（重写） | 更新模板，填充 `updatedAt=now()`，保留原 `createdAt` |

继承自 `ServiceImpl` 的方法：`getById`、`removeById`、`list()` 等。

### Controller 与 API 端点

#### NotificationController — 通知中心（`/api/notification`）

| HTTP 方法 | 路径 | 方法 | 鉴权 | OperLog | 说明 |
|-----------|------|------|------|---------|------|
| GET | `/api/notification/page` | `page` | — | — | 分页查询当前用户通知（`SecurityUtils.getCurrentUserId()`），支持 `category` / `readStatus` 过滤，默认 page=1、size=10 |
| GET | `/api/notification/unread/count` | `unreadCount` | — | — | 当前用户未读通知数，userId 为 null 时返回 0 |
| PUT | `/api/notification/{id}/read` | `markAsRead` | — | `title="通知中心", businessType=2` | 标记单条通知为已读 |
| PUT | `/api/notification/read/all` | `markAllRead` | — | `title="通知中心", businessType=2` | 当前用户通知全部已读 |
| POST | `/api/notification/send` | `send` | `@PreAuthorize("hasAuthority('notification:notification:send')")` | `title="通知中心", businessType=1` | 管理员手动发送通知（调试用），`channels` 参数可选，默认 `Set.of("IN_APP")` |

#### NotificationTemplateController — 通知模板（`/api/notification/template`）

| HTTP 方法 | 路径 | 方法 | 鉴权 | OperLog | 说明 |
|-----------|------|------|------|---------|------|
| GET | `/api/notification/template/page` | `page` | — | — | 分页查询模板，默认 page=1、size=10 |
| GET | `/api/notification/template/{id}` | `get` | — | — | 按 id 查询模板 |
| GET | `/api/notification/template/code/{code}` | `getByCode` | — | — | 按编码查询模板 |
| POST | `/api/notification/template` | `create` | `@PreAuthorize("hasAuthority('notification:template:add')")` | `title="通知模板", businessType=1` | 新增模板 |
| PUT | `/api/notification/template/{id}` | `update` | `@PreAuthorize("hasAuthority('notification:template:edit')")` | `title="通知模板", businessType=2` | 修改模板 |
| DELETE | `/api/notification/template/{id}` | `delete` | `@PreAuthorize("hasAuthority('notification:template:remove')")` | `title="通知模板", businessType=3` | 删除模板 |

**权限编码**：`notification:notification:send`、`notification:template:add`、`notification:template:edit`、`notification:template:remove`。

**Swagger Tag**：通知中心 `@Tag(name = "通知中心", description = "站内通知查询、已读管理与多通道发送")`，通知模板 `@Tag(name = "通知模板", description = "通知模板 CRUD 与按编码查询")`。

---

## 模块依赖关系

### 本模块依赖

| 依赖 | 用途 |
|------|------|
| `com.dp.plat:pms-common` | 公共基础：`Result`、`BusinessException`、`SecurityUtils`、`OperLog` 注解等 |
| `org.springframework.boot:spring-boot-starter-web` | REST 控制器 |
| `com.baomidou:mybatis-plus-spring-boot3-starter` | MyBatis-Plus ORM |
| `org.springframework.boot:spring-boot-starter-websocket` | STOMP / WebSocket 实时推送 |
| `org.springframework.boot:spring-boot-starter-data-redis` | Redis Pub/Sub 跨实例广播 |
| `io.jsonwebtoken:jjwt-api` / `jjwt-impl` / `jjwt-jackson` | WebSocket 握手时解析 JWT 提取 userId |
| `org.freemarker:freemarker` | 模板渲染（版本由 spring-boot 父 pom 管理） |
| `org.springframework.boot:spring-boot-starter-test` | 单元测试（test 作用域） |

### 被其他模块依赖

`pms-notification` 是 PMS 平台的核心横切模块，被多个业务模块依赖以发送通知：

| 依赖方 | 主要使用方式 | 文件示例 |
|--------|--------------|----------|
| **pms-project** | 里程碑逾期扫描、Punch List 整改到期提醒，category=`MILESTONE`/`PUNCH_LIST`，channels=`{IN_APP, WS}` | `schedule/MilestoneOverdueScheduler.java`、`punchlist/service/impl/PunchListServiceImpl.java` |
| **pms-implementation** | 实施域通知服务委托，将简单 `notifyUser` 调用转换为多通道投递，category=`TASK`，channels=`{IN_APP, WS}` | `service/impl/NotificationServiceImpl.java`（`@Service("implementationNotificationServiceImpl")`） |
| **pms-asset** | RMA 状态变更通知、质保到期预警（90/60/30 天），category=`RMA`/`WARRANTY`，channels=`{IN_APP, WS}` | `rma/service/impl/RmaServiceImpl.java`、`warranty/schedule/WarrantyExpiryScheduler.java` |
| **pms-lowcode** | 低代码平台评论 @提及通知，category=`LOWCODE_MENTION`，channels=`{IN_APP, WS}` | `service/impl/LowCodeCommentServiceImpl.java#sendMentionNotifications` |
| **pms-admin** | 聚合模块统一部署，依赖全部业务模块（含 pms-notification） | `pom.xml` |
| **pms-frontend** | 前端类型与 Schema 镜像，`validators/notification.ts` 与后端 `Notification.java` 严格对齐 | `src/validators/notification.ts` |

**调用方典型模式**（统一约定）：
1. 调用方在业务事件触发时（如状态变更、定时扫描）构造 `Notification` builder，填充 `userId` / `title` / `content` / `category` / `bizType` / `bizId`；
2. 调用 `notificationService.multiChannelSend(notification, Set.of("IN_APP", "WS"))`；
3. 通知失败被调用方 `try-catch` 吞掉（best-effort），仅记录 `log.error`，不影响主业务事务；
4. `pms-implementation` 的 `NotificationServiceImpl` 进一步将简单的 `(userId, title, content)` 调用包装为多通道投递，category 固定为 `TASK`。

### 依赖关系图

```
pms-common ────────────────────────┐
                                   ▼
                            pms-notification
                                   ▲
       ┌───────┬───────┬───────────┼───────────┐
       │       │       │           │           │
pms-project pms-impl pms-asset  pms-lowcode  pms-admin
                                   (聚合)
```

---

## 关键技术点

1. **实体不继承 BaseEntity**：`Notification` 与 `NotificationTemplate` 均不继承 `com.dp.plat.common.entity.BaseEntity`，使用独立的审计字段（`createdAt` / `createdBy` 或 `createdAt` / `updatedAt`），且不参与逻辑删除——通知一经产生即留存审计。前端 `validators/notification.ts` 中的 Schema 注释明确指出此点。

2. **多通道并发 + 异常隔离**：`multiChannelSend` 用 `CompletableFuture.runAsync` 并发分发各通道任务，每个任务内部 `try-catch` 吞掉异常，最后 `CompletableFuture.allOf(...).join()` 等待全部完成。任何单通道失败（如 Redis 宕机、邮件服务异常）都不会传播到调用方，保证主业务流程稳定。

3. **Redis Pub/Sub 跨实例广播**：WS 通道不直接调用 `SimpMessagingTemplate`，而是先发布到 Redis 频道 `pms:notification:broadcast`，由各实例的 `NotificationSubscriber` 决定是否推送给本地连接的用户。这解决了多实例部署下「用户连接在 A 实例、通知触发在 B 实例」的推送一致性问题。

4. **按 userId 划分的 STOMP 广播频道**：放弃 `convertAndSendToUser`，改用 `/topic/notification/{userId}` 广播频道。原因：前端为原生 WebSocket 客户端，STOMP CONNECT 帧不携带 Principal，使用按 userId 划分的广播频道更简单可靠，无需自定义 `UserDestinationResolver`。

5. **JWT 握手鉴权双通道**：WebSocket 握手阶段同时支持 `Authorization: Bearer` 请求头与 `?token=` 查询参数。查询参数兜底是为了适配原生 WebSocket 客户端无法自定义请求头的限制。解析失败的握手直接 `return false` 拒绝。

6. **Freemarker 模板渲染的线程安全**：`NotificationTemplateEngine.renderString` 每次渲染新建 `Configuration` 与 `StringTemplateLoader`，避免多线程共享可变模板加载器的线程安全问题。虽然带来轻微的对象创建开销，但保证了高并发下的渲染安全。

7. **持久化优先级**：当 channels 同时包含 `IN_APP` 与 `WS` 时，落库只发生一次（在分发前），WS 通道直接复用已生成的 `notificationId`。若 `notification.id` 已存在则跳过落库，支持调用方预先持久化的场景。

8. **best-effort 通知模式**：所有调用方（`MilestoneOverdueScheduler`、`PunchListServiceImpl`、`RmaServiceImpl`、`WarrantyExpiryScheduler`、`LowCodeCommentServiceImpl` 等）都遵循「通知失败被吞掉」的约定，在 `try-catch` 中调用 `multiChannelSend`，仅记录 `log.error`，绝不影响主业务事务（如里程碑状态更新、RMA 状态变更、质保扫描）。

9. **预置 12 个标准模板**：V20 迁移脚本预置覆盖里程碑逾期、任务分派/转派、审批待办、Punch List 到期、质保 90/60/30 天预警、RMA 状态变更、结算审批、变更请求 CCB、风险升级等 12 个场景的标准模板，调用方通过 `templateCode` 直接复用。

10. **复合索引优化高频查询**：`pms_notification` 表有 `idx_user_read (user_id, read_status)` 与 `idx_pms_notification_user_read_created (user_id, read_status, created_at)` 两个索引，分别加速「未读数统计」与「按已读状态 + 时间倒序分页」两类高频查询；`idx_pms_notification_biz (biz_type, biz_id)` 支持按业务反查通知。

11. **测试覆盖**：`src/test/java/.../service/impl/` 下有 `NotificationServiceImplTest`（21+ 用例）与 `NotificationTemplateServiceImplTest`（13+ 用例），使用 Mockito `@ExtendWith(MockitoExtension.class)` + `ReflectionTestUtils`（注入 `ServiceImpl.baseMapper`）覆盖创建、已读管理、未读统计、分页、多通道并发、模板化发送、模板 CRUD 等核心逻辑。`src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker` 启用 mockito-inline 支持 final 类与静态方法 mock。
