# pms-system 模块知识库

> 本文基于 `network-equipment-pms/pms-system` 模块源码（`com.dp.plat.system`）整理，记录系统管理中台的用户、角色、菜单、字典、配置、部门、反馈、帮助中心、日志、缓存与 JWT 认证授权等核心机制。

> 源码路径：`/workspace/network-equipment-pms/pms-system`
> 基础包名：`com.dp.plat.system`
> 父项目：`com.dp.plat:network-equipment-pms:1.0.0-SNAPSHOT`

---

## 模块概述

**定位**：`pms-system` 是 `network-equipment-pms`（网络设备工程项目管理系统）多模块工程的**系统管理中台模块**，承担"身份认证 + 权限模型 + 主数据维护 + 审计日志 + 缓存治理 + 用户支持"等基础设施职责，是其他业务模块（项目、资产、实施、工作流、低代码等）共同依赖的中枢。

**Maven 坐标**：`com.dp.plat:pms-system:1.0.0-SNAPSHOT`，父工程为 `com.dp.plat:network-equipment-pms`。
**artifactId / name**：`pms-system`，`description` 为 `System management: user, role, permission, menu, dict, log`。
**JDK**：跟随父工程（Java 17+，使用 `jakarta.*` 命名空间）。

**核心职责**：

1. **认证（Authentication）**：基于 JWT 的无状态登录/登出，密码 BCrypt 哈希，敏感字段（email/phone）AES-256-GCM 字段级加密；
2. **授权（Authorization）**：RBAC 权限模型（用户-角色-菜单），`@PreAuthorize` 注解 + `@ss.hasPermi` SpEL Bean 双轨权限校验，超级管理员直通；
3. **数据权限**：MyBatis-Plus `InnerInterceptor` + `@DataScope` 注解，按 `create_by` 字段对查询结果做行级过滤（管理员放行）；
4. **主数据**：用户（`sys_user`）、角色（`sys_role`）、菜单（`sys_menu`）、部门（`sys_dept`）、字典（`sys_dict`/`sys_dict_item`）、参数配置（`sys_config`）的 CRUD；
5. **审计日志**：操作日志（`@OperLog` AOP 切面）、登录日志、异常日志、定时任务日志四类；
6. **缓存治理**：基于 Spring Cache + Redis 的命名缓存（`sysDict`/`sysMenu`/`sysConfig`/`sysRole`），提供缓存清空 API；
7. **用户支持**：技术支持反馈工单（`sys_feedback`，4 态状态机）+ 帮助中心（`sys_help_content`，Markdown 文档 + 浏览计数）；
8. **任务监控**：定时任务最近执行记录、24h 失败列表、按任务名分组统计、手动重试占位接口；
9. **API 文档**：SpringDoc OpenAPI 3 + Swagger UI，Bearer JWT 鉴权方案，按业务域分组。

**技术栈**：Spring Boot 3.x + Spring Security 6 + MyBatis-Plus + JJWT 0.12.x + Spring Data Redis + Spring Cache + AOP + springdoc-openapi + Jakarta Validation + Lombok。

---

## 包结构

`com.dp.plat.system` 下的子包组织如下：

| 子包 | 主要内容 |
|------|----------|
| `entity` | 领域实体 15 个：`SysUser`、`SysRole`、`SysMenu`、`SysDept`、`SysDict`、`SysDictItem`、`SysConfig`、`SysUserRole`、`SysRoleMenu`、`SysOperLog`、`LoginLog`、`ExceptionLog`、`ScheduleLog`、`Feedback`、`HelpContent` |
| `dto` | 登录请求/响应：`LoginRequest`、`LoginResponse` |
| `mapper` | MyBatis-Plus Mapper 15 个：均继承 `BaseMapper`，`SysUserMapper` 配套 XML，`SysMenuMapper`/`HelpContentMapper` 含注解 SQL |
| `service` | 服务接口 13 个：`ISysUserService`、`ISysRoleService`、`ISysMenuService`、`ISysDeptService`、`ISysDictService`、`ISysDictItemService`、`ISysConfigService`、`ISysOperLogService`、`ILoginLogService`、`IExceptionLogService`、`IScheduleLogService`、`IFeedbackService`、`IHelpContentService` |
| `service.impl` | 上述接口的实现类，多继承 `ServiceImpl<Mapper, Entity>` |
| `controller` | REST 控制器 13 个：`AuthController`、`SysUserController`、`SysRoleController`、`SysMenuController`、`SysDeptController`、`SysDictController`、`SysDictItemController`、`SysConfigController`、`AuditLogController`、`ScheduleMonitorController`、`CacheManagementController`、`FeedbackController`、`HelpContentController` |
| `security` | 安全组件：`JwtTokenProvider`、`JwtAuthenticationFilter`、`UserAuthorityService`、`TokenBlacklistService`、`PermissionService`、`DataPermissionInterceptor` |
| `aop` | `OperLogAspect`（操作日志切面） |
| `config` | `SecurityConfig`、`RedisConfig`、`OpenApiConfig` |

业务实体（除日志类）均继承 `com.dp.plat.common.entity.BaseEntity`，公共字段为：`id`（`IdType.AUTO`）、`createTime`、`updateTime`、`createBy`、`updateBy`、`deleted`（`@TableLogic` 逻辑删除，0=未删 1=已删）。

日志类实体（`LoginLog`、`ExceptionLog`、`ScheduleLog`）不继承 `BaseEntity`，直接 `implements Serializable`，使用 `@TableId(type = IdType.AUTO)` 主键，无逻辑删除字段。

---

## 核心实体模型

| 实体 | 表名 | 职责 | 关键字段 | 关系 |
|------|------|------|----------|------|
| `SysUser` | `sys_user` | 系统用户 | `username`(3-50,字母数字下划线)、`password`(BCrypt, `@JsonProperty(WRITE_ONLY)`)、`realName`、`email`(`@FieldEncrypt`+`EncryptTypeHandler`)、`phone`(同 email)、`status`(0正常/1禁用)、`deptId`、`companyId` | → `SysDept`(多对一)、→ `SysRole`(多对多，经 `sys_user_role`) |
| `SysRole` | `sys_role` | 系统角色 | `roleName`、`roleCode`、`description`、`status`(0/1) | → `SysMenu`(多对多，经 `sys_role_menu`) |
| `SysMenu` | `sys_menu` | 菜单/权限资源 | `parentId`、`menuName`、`menuType`(M=目录/C=菜单/F=按钮/L=低代码页面)、`path`、`component`、`perms`(权限标识,如 `system:user:list`)、`icon`、`orderNum`、`visible`(0可见/1隐藏)、`children`(`@TableField(exist=false)` 非持久化) | 自关联 `parentId` 构成树 |
| `SysDept` | `sys_dept` | 部门/公司树 | `parentId`、`deptName`、`orderNum`、`status` | 自关联 `parentId` 构成树 |
| `SysDict` | `sys_dict` | 字典主表 | `dictName`、`dictType`(唯一标识,如 `asset_status`)、`status`(0/1) | → `SysDictItem`(一对多) |
| `SysDictItem` | `sys_dict_item` | 字典项 | `dictId`、`itemText`、`itemValue`、`sortOrder` | → `SysDict`(多对一) |
| `SysConfig` | `sys_config` | 参数配置 | `configName`、`configKey`、`configValue`、`configType`(0系统内置/1用户定义)、`remark` | 独立 |
| `SysUserRole` | `sys_user_role` | 用户-角色映射 | `userId`、`roleId` | 关联 `sys_user` ↔ `sys_role` |
| `SysRoleMenu` | `sys_role_menu` | 角色-菜单映射 | `roleId`、`menuId` | 关联 `sys_role` ↔ `sys_menu` |
| `SysOperLog` | `sys_oper_log` | 操作日志 | `title`、`businessType`(1新增/2修改/3删除/4导出/5导入/其他查询)、`method`、`requestMethod`、`operName`、`operUrl`、`operParam`、`jsonResult`、`status`(0成功/1失败)、`errorMsg`、`operTime` | 继承 `BaseEntity` |
| `LoginLog` | `sys_login_log` | 登录日志 | `username`、`loginTime`、`loginIp`、`loginLocation`、`browser`、`os`、`status`(SUCCESS/FAIL)、`message`、`userId` | 独立（非 BaseEntity） |
| `ExceptionLog` | `sys_exception_log` | 异常日志 | `userId`、`username`、`requestUri`、`requestMethod`、`requestParams`(TEXT)、`exceptionType`、`exceptionMessage`(TEXT)、`stackTrace`(LONGTEXT)、`requestIp`、`occurTime` | 独立（非 BaseEntity） |
| `ScheduleLog` | `sys_schedule_log` | 定时任务日志 | `taskName`、`taskGroup`、`cronExpression`、`startTime`、`endTime`、`costMs`、`status`(SUCCESS/FAIL)、`errorMessage`(TEXT)、`triggerType`(AUTO/MANUAL) | 独立（非 BaseEntity） |
| `Feedback` | `sys_feedback` | 用户反馈工单 | `userId`、`username`(冗余)、`category`(BUG/SUGGESTION/QUESTION/OTHER)、`title`、`content`(≤4000)、`contact`、`status`(PENDING/PROCESSING/RESOLVED/CLOSED)、`reply`、`replyBy`、`replyAt` | → `SysUser`(冗余 `userId`/`username`) |
| `HelpContent` | `sys_help_content` | 帮助中心文档 | `category`(QUICK_START/FAQ/VIDEO/ADVANCED)、`title`、`content`(Markdown)、`sortOrder`、`status`(0启用/1禁用)、`viewCount` | 独立 |

**字段级加密**：`SysUser` 的 `email` 和 `phone` 通过 `com.dp.plat.common.crypto.EncryptTypeHandler` 在数据库读写时自动 AES-256-GCM 加解密；实体启用 `@TableName(autoResultMap = true)` 让 MyBatis-Plus BaseMapper 自动应用 typeHandler。`SysUserMapper.xml` 中自定义查询/写入显式声明 `typeHandler`，作为标准用法参考与兜底。

**密码保护**：`SysUser.password` 标注 `@JsonProperty(access = WRITE_ONLY)`，序列化（响应）时不输出密码哈希，仅反序列化（创建/更新请求）时接收。`SysUserServiceImpl.save/updateById` 自动检测 BCrypt 前缀（`$2`），未加密的明文密码才会被 `PasswordEncoder` 加密，避免重复哈希。

---

## Service 层

| 接口 | 实现类 | 关键方法 | 用途 |
|------|--------|----------|------|
| `ISysUserService` | `SysUserServiceImpl` | `getByUsername(username)`；继承 `IService<SysUser>`：`save`(自动加密)、`updateById`(自动加密)、`changePassword(userId, newPassword)` | 用户管理；密码 BCrypt 加密 |
| `ISysRoleService` | `SysRoleServiceImpl` | `getByRoleCode(roleCode)`(`@Cacheable(sysRole,#roleCode)`)、`assignMenus(roleId, menuIds)`(`@Transactional`+`@CacheEvict(sysRole,allEntries)`,先删 `sys_role_menu` 再批量插入) | 角色管理 + 角色菜单授权 + 权限缓存失效 |
| `ISysMenuService` | `SysMenuServiceImpl` | `listMenusByUserId(userId)`(`@Cacheable(sysMenu,'byUser:'+userId)`)、`listChildren(parentId)`(`@Cacheable(sysMenu,'children:'+parentId)`)、`buildTree(menus)`(邻接表→树) | 菜单查询 + 树构建 + 用户菜单缓存 |
| `ISysDeptService` | `SysDeptServiceImpl` | `listChildren(parentId)` | 部门子节点查询 |
| `ISysDictService` | `SysDictServiceImpl` | `getByDictType(dictType)`(`@Cacheable(sysDict,#dictType)`)、`listItemsByDictType(dictType)`(`@Cacheable(sysDict,'items:'+dictType)`) | 字典查询 + 字典项缓存 |
| `ISysDictItemService` | `SysDictItemServiceImpl` | `listByDictId(dictId)`、`create/update/deleteById` | 字典项 CRUD（无缓存注解，字典级失效） |
| `ISysConfigService` | `SysConfigServiceImpl` | `getByConfigKey(configKey)`(`@Cacheable(sysConfig,#configKey)`)、`create/update/deleteById`(均 `@CacheEvict(sysConfig,allEntries)`)、`selectPage(pageNum,pageSize,configName)` | 参数配置 CRUD + 缓存失效 |
| `ISysOperLogService` | `SysOperLogServiceImpl` | 继承 `IService<SysOperLog>`，无自定义方法 | 操作日志（由 `OperLogAspect` 写入） |
| `ILoginLogService` | `LoginLogServiceImpl` | `record(loginLog)`、`page(page,size,filter)`(支持 username/status/userId 过滤,按 loginTime 倒序) | 登录日志写入 + 分页查询 |
| `IExceptionLogService` | `ExceptionLogServiceImpl` | `record(exceptionLog)`、`page(page,size,filter)`(支持 username/requestUri/userId 过滤,按 occurTime 倒序) | 异常日志写入 + 分页查询 |
| `IScheduleLogService` | `ScheduleLogServiceImpl` | `record(scheduleLog)`、`page(page,size,filter)`(支持 taskName/status/taskGroup 过滤)、`listFailed(page,size)`(只查 status=FAIL) | 定时任务日志写入 + 分页查询 + 失败列表 |
| `IFeedbackService` | `FeedbackServiceImpl` | `save(feedback)`(自动填充 userId/username/PENDING)、`listByUser(username)`、`listAll(status,category)`、`reply(id,reply,replyBy)`(PENDING→PROCESSING)、`close(id)`(→CLOSED)、`countByStatus()`(按状态计数,初始化 4 态为 0) | 反馈工单全生命周期 |
| `IHelpContentService` | `HelpContentServiceImpl` | `listByCategory(category)`(只查 status=0,按 sortOrder/id 升序)、`incrementViewCount(id)`(调用 `HelpContentMapper.incrementViewCount`,乐观更新 `view_count=view_count+1`) | 帮助内容列表 + 浏览计数 |

> 服务实现中 `SysUserServiceImpl`、`SysRoleServiceImpl`、`SysMenuServiceImpl`、`SysDictServiceImpl`、`SysConfigServiceImpl`、`SysOperLogServiceImpl`、`SysDeptServiceImpl`、`SysDictItemServiceImpl`、`FeedbackServiceImpl`、`HelpContentServiceImpl` 均继承 MyBatis-Plus 的 `ServiceImpl<Mapper, Entity>`，可直接使用 `IService` 提供的 `page/list/save/updateById/removeById/getById` 等通用方法。`LoginLogServiceImpl`、`ExceptionLogServiceImpl`、`ScheduleLogServiceImpl` 因为实体不继承 `BaseEntity`，直接持有 Mapper 实现接口方法。

---

## API 端点

所有 API 统一返回 `com.dp.plat.common.result.Result<T>`，路径前缀分两组：`/api/auth/**`（认证，公开）与 `/api/system/**`（业务，需登录）。OpenAPI 分组由 `OpenApiConfig.systemGroup` 提供，匹配 `/api/system/**`。

### 认证 `/api/auth` — `AuthController`（公开访问）

| 路径 | 方法 | 说明 | 权限 |
|------|------|------|------|
| `/api/auth/login` | POST | 用户名密码登录，返回 JWT token + userInfo(含 roles/permissions) | 公开 |
| `/api/auth/logout` | POST | 登出，将当前 JWT 的 jti 加入 Redis 黑名单（TTL=剩余有效期） | 公开（但需携带 token） |
| `/api/auth/info` | GET | 获取当前登录用户信息（含 roles/permissions，超管加载全部权限） | 需登录 |

### 用户管理 `/api/system/user` — `SysUserController`

| 路径 | 方法 | 说明 | 权限 |
|------|------|------|------|
| `/api/system/user/page` | GET | 分页查询用户（支持 username 模糊匹配） | `system:user:list` |
| `/api/system/user/search` | GET | 用户搜索（@提及自动补全，最多 20 条，仅返回 id/username/realName） | 仅需登录 |
| `/api/system/user/{id}` | GET | 按 id 查询用户 | `system:user:list` |
| `/api/system/user` | POST | 新增用户（`@OperLog` businessType=1） | `system:user:add` |
| `/api/system/user` | PUT | 更新用户（`@OperLog` businessType=2） | `system:user:edit` |
| `/api/system/user/{id}` | DELETE | 删除用户（`@OperLog` businessType=3） | `system:user:remove` |

### 角色管理 `/api/system/role` — `SysRoleController`

| 路径 | 方法 | 说明 | 权限 |
|------|------|------|------|
| `/api/system/role/page` | GET | 分页查询角色 | `system:role:list` |
| `/api/system/role/all` | GET | 全部启用角色列表（下拉用，仅 id/roleName/roleCode） | 仅需登录 |
| `/api/system/role/{id}` | GET | 按 id 查询角色 | `system:role:list` |
| `/api/system/role` | POST | 新增角色（`@OperLog`） | `system:role:add` |
| `/api/system/role` | PUT | 更新角色（`@OperLog`） | `system:role:edit` |
| `/api/system/role/{id}` | DELETE | 删除角色（`@OperLog`） | `system:role:remove` |
| `/api/system/role/{id}/menus` | POST | 给角色分配菜单（替换式，`@OperLog`） | `system:role:edit` |

### 菜单管理 `/api/system/menu` — `SysMenuController`

| 路径 | 方法 | 说明 | 权限 |
|------|------|------|------|
| `/api/system/menu/list` | GET | 全部菜单列表（按 orderNum 升序） | `system:menu:list` |
| `/api/system/menu/tree` | GET | 完整菜单树 | `system:menu:list` |
| `/api/system/menu/routers` | GET | 当前用户菜单树（前端路由用） | 仅需登录 |
| `/api/system/menu/{id}` | GET | 按 id 查询菜单 | `system:menu:list` |
| `/api/system/menu` | POST | 新增菜单（`@OperLog`） | `system:menu:add` |
| `/api/system/menu` | PUT | 更新菜单（`@OperLog`） | `system:menu:edit` |
| `/api/system/menu/{id}` | DELETE | 删除菜单（`@OperLog`） | `system:menu:remove` |

### 部门管理 `/api/system/dept` — `SysDeptController`

| 路径 | 方法 | 说明 | 权限 |
|------|------|------|------|
| `/api/system/dept/list` | GET | 全部部门列表（按 orderNum 升序） | 仅需登录 |
| `/api/system/dept/{id}` | GET | 按 id 查询部门 | 仅需登录 |
| `/api/system/dept` | POST | 新增部门（`@OperLog`） | `system:dept:add` |
| `/api/system/dept` | PUT | 更新部门（`@OperLog`） | `system:dept:edit` |
| `/api/system/dept/{id}` | DELETE | 删除部门（`@OperLog`） | `system:dept:remove` |

### 字典管理 `/api/system/dict` — `SysDictController`

| 路径 | 方法 | 说明 | 权限 |
|------|------|------|------|
| `/api/system/dict/page` | GET | 分页查询字典（支持 dictName 模糊匹配） | `system:dict:list` |
| `/api/system/dict/{id}` | GET | 按 id 查询字典 | `system:dict:list` |
| `/api/system/dict` | POST | 新增字典（`@OperLog`） | `system:dict:add` |
| `/api/system/dict` | PUT | 更新字典（`@OperLog`） | `system:dict:edit` |
| `/api/system/dict/{id}` | DELETE | 删除字典（`@OperLog`） | `system:dict:remove` |
| `/api/system/dict/items/{dictType}` | GET | **按 dictType 列出字典项（公开端点，仅需登录，无权限注解）** | 仅需登录 |

### 字典项管理 `/api/system/dict/item` — `SysDictItemController`

| 路径 | 方法 | 说明 | 权限 |
|------|------|------|------|
| `/api/system/dict/item/list?dictId=` | GET | 按 dictId 列出字典项（按 sortOrder 升序） | `system:dict:list` |
| `/api/system/dict/item` | POST | 新增字典项（`@OperLog`） | `system:dict:add` |
| `/api/system/dict/item` | PUT | 更新字典项（`@OperLog`） | `system:dict:edit` |
| `/api/system/dict/item/{id}` | DELETE | 删除字典项（`@OperLog`） | `system:dict:remove` |

### 参数配置 `/api/system/config` — `SysConfigController`

| 路径 | 方法 | 说明 | 权限 |
|------|------|------|------|
| `/api/system/config/page` | GET | 分页查询配置（支持 configName 模糊匹配） | `system:config:list` |
| `/api/system/config/{id}` | GET | 按 id 查询配置 | `system:config:list` |
| `/api/system/config/key/{configKey}` | GET | 按 configKey 查询配置（公开端点，仅需登录） | 仅需登录 |
| `/api/system/config` | POST | 新增配置（`@OperLog`） | `system:config:add` |
| `/api/system/config` | PUT | 更新配置（`@OperLog`） | `system:config:edit` |
| `/api/system/config/{id}` | DELETE | 删除配置（`@OperLog`） | `system:config:remove` |

### 审计日志 `/api/system/audit` — `AuditLogController`

| 路径 | 方法 | 说明 | 权限 |
|------|------|------|------|
| `/api/system/audit/login/page` | GET | 分页查询登录日志（支持 username/status 过滤） | `system:audit:list` |
| `/api/system/audit/exception/page` | GET | 分页查询异常日志（支持 username/requestUri 过滤） | `system:audit:list` |
| `/api/system/audit/schedule/page` | GET | 分页查询定时任务日志（支持 taskName/status 过滤） | `system:audit:list` |
| `/api/system/audit/schedule/failed` | GET | 分页查询失败的定时任务 | `system:audit:list` |

### 定时任务监控 `/api/system/schedule` — `ScheduleMonitorController`

| 路径 | 方法 | 说明 | 权限 |
|------|------|------|------|
| `/api/system/schedule/recent` | GET | 最近 100 条定时任务日志 | 仅需登录 |
| `/api/system/schedule/failed` | GET | 最近 24h 失败的定时任务 | 仅需登录 |
| `/api/system/schedule/statistic` | GET | 按任务名分组的成功/失败次数统计（取最近 1000 条聚合） | 仅需登录 |
| `/api/system/schedule/retry/{id}` | POST | 手动重试占位接口（仅记录 MANUAL_TRIGGER 日志，不真正触发任务，`@OperLog`） | `system:schedule:retry` |

### 缓存管理 `/api/system/cache` — `CacheManagementController`

| 路径 | 方法 | 说明 | 权限 |
|------|------|------|------|
| `/api/system/cache/names` | GET | 获取所有缓存名称（sysDict/sysMenu/sysConfig/sysRole） | 仅需登录 |
| `/api/system/cache/clearAll` | POST | 清空全部缓存（`@OperLog`） | `system:cache:clear` |
| `/api/system/cache/clear/{cacheName}` | POST | 按名称清空指定缓存（`@OperLog`） | `system:cache:clear` |

### 技术支持反馈 `/api/system/feedback` — `FeedbackController`

| 路径 | 方法 | 说明 | 权限 | 限流 |
|------|------|------|------|------|
| `/api/system/feedback` | POST | 提交反馈工单（防伪造提交人，强制覆盖 userId/username，状态默认 PENDING，`@OperLog`） | `isAuthenticated()` | 5/60s |
| `/api/system/feedback/{id}` | GET | 查询反馈详情（提交人或管理员可访问） | `isAuthenticated()` | - |
| `/api/system/feedback/my` | GET | 当前用户提交的反馈列表 | `isAuthenticated()` | - |
| `/api/system/feedback/list` | GET | 全部反馈列表（管理员，支持 status/category 过滤） | `system:feedback:list` | - |
| `/api/system/feedback/{id}/reply` | PUT | 回复反馈（管理员，PENDING→PROCESSING，`@OperLog`） | `system:feedback:reply` | 30/60s |
| `/api/system/feedback/{id}/close` | PUT | 关闭反馈（管理员，→CLOSED，`@OperLog`） | `system:feedback:reply` | 30/60s |
| `/api/system/feedback/stats` | GET | 按状态分组的反馈计数（当前用户） | `isAuthenticated()` | - |

请求体 `FeedbackReplyRequest` 嵌套在 Controller 中，含 `reply`（`@NotBlank`，≤4000 字符）字段。

### 帮助中心 `/api/system/help-content` — `HelpContentController`

| 路径 | 方法 | 说明 | 权限 | 限流 |
|------|------|------|------|------|
| `/api/system/help-content/list` | GET | 列出启用的帮助内容（可按 category 过滤） | **公开（无需登录）** | - |
| `/api/system/help-content/{id}` | GET | 按 id 查询帮助内容并自动累加浏览次数 | **公开** | - |
| `/api/system/help-content/categories` | GET | 列出全部帮助内容分类（QUICK_START/FAQ/VIDEO/ADVANCED） | **公开** | - |
| `/api/system/help-content` | POST | 新增帮助内容（自动填充 sortOrder=0/status=0/viewCount=0，`@OperLog`） | `system:help:create` | 20/60s |
| `/api/system/help-content` | PUT | 更新帮助内容（`@OperLog`） | `system:help:edit` | 30/60s |
| `/api/system/help-content/{id}` | DELETE | 删除帮助内容（`@OperLog`） | `system:help:remove` | 20/60s |

> 公开端点（帮助中心 list/{id}/categories、`/api/auth/**`、Actuator、Swagger 资源）在 `SecurityConfig` 中通过 `permitAll()` 放行；其余请求均要求 `authenticated()`。

---

## 认证与授权

### JWT 认证流程

**登录流程**（`AuthController.login`）：

1. 接收 `LoginRequest`（`username`/`password`，均 `@NotBlank`）。
2. 通过 `sysUserService.getByUsername(username)` 查询用户；用户不存在抛 `BusinessException(USERNAME_OR_PASSWORD_ERROR)`。
3. 使用 `PasswordEncoder`（`BCryptPasswordEncoder`）比对密码：`passwordEncoder.matches(rawPassword, user.getPassword())`，不匹配抛同一异常（避免泄露用户是否存在）。
4. 调用 `JwtTokenProvider.generateToken(userId, username)` 生成 JWT，返回 `LoginResponse{token, userInfo}`。
5. `userInfo` 包含 `id/username/nickname(=realName)/email/phone/deptId/roles(roleCodes)/permissions`；超管（roles 含 `admin`）加载 `sys_menu` 全部权限，普通用户通过 `sysMenuMapper.listPermsByUserId(userId)` 加载具体权限。

**JWT 结构**（`JwtTokenProvider`，基于 jjwt 0.12.x）：

- 签名算法：HMAC-SHA，密钥来自 `jwt.secret`（Base64 解码后构造 `SecretKey`），生产环境必须通过 `JWT_SECRET` 环境变量覆盖默认开发密钥。
- Claims：`sub`=userId、`username`=username（自定义 claim）、`iat`=签发时间、`exp`=过期时间（`jwt.expiration` 毫秒，默认 86400000 即 24h）、`jti`=UUID（用于黑名单）。
- `validateToken(token)` 通过 `Jwts.parser().verifyWith(key).build().parseSignedClaims(token)` 校验签名与过期。
- 配置项（`pms-admin/src/main/resources/application.yml`）：`jwt.secret`、`jwt.expiration`。

**请求鉴权流程**（`JwtAuthenticationFilter`，`OncePerRequestFilter`）：

1. 从 `Authorization` 头提取 `Bearer ` 前缀后的 token。
2. `jwtTokenProvider.validateToken(token)` 校验签名+过期；不通过则放行（匿名访问）。
3. 提取 `jti`，调用 `TokenBlacklistService.isBlacklisted(jti)`，若在 Redis 黑名单中则拒绝（写日志后放行）。
4. 提取 `userId`/`username`，调用 `UserAuthorityService.loadAuthorities(username)` 加载权限（带 5 分钟 TTL 内存缓存）。
5. 构造 `UsernamePasswordAuthenticationToken`（principal=`User(username=userId, password="", authorities)`，details 含 `webDetails` 与 `username`），写入 `SecurityContextHolder`。

**登出流程**（`AuthController.logout`）：

1. 从 `Authorization` 头提取 token。
2. `validateToken` 校验通过后提取 `jti` 与 `exp`。
3. 计算剩余有效期 `expAt - now`，调用 `TokenBlacklistService.blacklist(jti, remaining)`。
4. `TokenBlacklistService` 在 Redis 写入 `token:blacklist:{jti}` = "1"，TTL = 剩余毫秒数；过期后自动清理。

### RBAC 权限模型

**模型**：用户（`sys_user`）— 用户角色映射（`sys_user_role`）— 角色（`sys_role`）— 角色菜单映射（`sys_role_menu`）— 菜单/权限（`sys_menu`）。

**权限标识**：`sys_menu.perms` 字段存储权限字符串，约定格式为 `模块:资源:操作`，如 `system:user:list`、`system:role:add`、`system:cache:clear`、`lowcode:data:*:list`。

**权限校验双轨制**：

1. **Spring Security 原生**：Controller 方法标注 `@PreAuthorize("hasAuthority('system:user:list')")`，由 `JwtAuthenticationFilter` 注入的 `GrantedAuthority` 集合判定。
2. **SpEL Bean**：`PermissionService`（Bean 名 `ss`），在 SpEL 中以 `@ss.hasPermi('xxx')` 引用，支持：
   - 超级管理员（角色 code = `admin`，常量 `CommonConstants.SUPER_ADMIN_ROLE`）直通；
   - 精确匹配；
   - 通配符匹配：用户权限含 `*` 时，使用 `AntPathMatcher`（分隔符 `:`）匹配，例如 `lowcode:data:*:list` 可访问 `lowcode:data:customer:list`。

**超级管理员加载策略**（`UserAuthorityService.doLoad`）：检测到用户绑定 `admin` 角色时，除添加 `admin` 角色标识外，还通过 `sysMenuMapper.listAllPerms()` 加载 `sys_menu` 中所有已注册的权限标识，使 `@PreAuthorize("hasAuthority('xxx')")` 注解对超管也生效。

**权限缓存**（`UserAuthorityService`）：

- 使用 `ConcurrentHashMap<String, CachedAuthorities>` 内存缓存（非 Redis），TTL = 5 分钟（`CACHE_TTL_MS = 5 * 60 * 1000L`）。
- `evict(username)` 失效单个用户缓存；`evictAll()` 失效全部缓存。
- 角色菜单授权（`SysRoleServiceImpl.assignMenus`）后调用 `userAuthorityService.evictAll()` 强制刷新权限缓存。

### 数据权限拦截器

**实现**：`DataPermissionInterceptor implements InnerInterceptor`（MyBatis-Plus 内部拦截器），在 SQL 执行前对查询语句追加 WHERE 条件。

**触发条件**：Mapper 方法标注 `@DataScope` 注解（`com.dp.plat.common.annotation.DataScope`，含 `deptAlias`/`userAlias` 预留属性）。

**过滤逻辑**：

1. 解析 `MappedStatement.getId()`（如 `com.dp.plat.xxx.mapper.XxxMapper.selectList`）反射获取 Mapper 方法上的 `@DataScope` 注解，无注解则放行。
2. 获取当前登录用户名；若为空、为 `admin`、为 `system`、或当前用户为管理员（持有 `admin` authority），则放行。
3. 使用 JSqlParser 解析 SQL，仅处理 `SELECT` 语句的 `PlainSelect`，在 WHERE 子句追加 `create_by = 'username'`（`EqualsTo(new Column("create_by"), new StringValue(username))`），原 WHERE 用 `AndExpression` 包裹。
4. 解析异常时仅打 warn 日志，不阻断查询。

> 该拦截器简化实现：当前仅按 `create_by` 字段过滤，未来可扩展按部门树过滤。需在 `MyBatisPlusConfig` 中注册为 InnerInterceptor 才生效。

### Spring Security 配置

**配置类**：`SecurityConfig`（`@EnableWebSecurity` + `@EnableMethodSecurity(prePostEnabled=true)`）。

**过滤器链顺序**（从先到后）：`SecurityHeadersFilter` → `RateLimitFilter` → `XssFilter` → `JwtAuthenticationFilter` → `UsernamePasswordAuthenticationFilter`。

通过 `addFilterBefore` 逆序注册：
- `addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)`
- `addFilterBefore(xssFilter, JwtAuthenticationFilter.class)`
- `addFilterBefore(rateLimitFilter, XssFilter.class)`
- `addFilterBefore(securityHeadersFilter, RateLimitFilter.class)`

**关键设置**：

- CSRF 禁用（无状态 REST API）。
- Session 创建策略：`STATELESS`（不创建 HttpSession，纯 JWT）。
- 公开路径：`/api/auth/**`、`GET /api/system/help-content/list|categories|*`、`/actuator/**`、`/doc.html`、`/swagger-ui/**`、`/swagger-ui.html`、`/v3/api-docs/**`、`/webjars/**`、`/favicon.ico`、所有 `OPTIONS` 请求。
- 其余请求 `anyRequest().authenticated()`。
- 未认证（401）与权限不足（403）返回 JSON：`{"code":401/403,"message":"未登录或登录已过期，请重新登录"/"权限不足，无法访问该资源"}`。
- `PasswordEncoder` Bean：`BCryptPasswordEncoder`。
- `AuthenticationManager` Bean：从 `AuthenticationConfiguration` 获取。

---

## 字典机制

### 双表结构

字典采用**主表 + 项表**两表结构：

- `sys_dict`（字典主表）：`id`、`dictName`（字典名称，如"资产状态"）、`dictType`（字典类型标识，如 `asset_status`，前端按此取项）、`status`（0正常/1禁用）。
- `sys_dict_item`（字典项表）：`id`、`dictId`（外键关联 `sys_dict.id`）、`itemText`（展示文本，如"在库"）、`itemValue`（实际值，如 `IN_STOCK`）、`sortOrder`（排序序号，升序）。

### 缓存策略

`SysDictServiceImpl` 中两个查询方法均使用 Spring Cache 注解，缓存命名空间 `sysDict`：

- `getByDictType(dictType)` → `@Cacheable(value="sysDict", key="#dictType")`：按 dictType 缓存字典主表记录。
- `listItemsByDictType(dictType)` → `@Cacheable(value="sysDict", key="'items:' + #dictType")`：按 dictType 缓存字典项列表（内部先 `getByDictType` 拿到 dictId，再按 dictId 查询并按 sortOrder 升序）。

字典项的 CRUD（`SysDictItemServiceImpl`）**未标注缓存注解**，因此字典项变更不会自动失效 `sysDict` 缓存。如需立即生效，需通过 `CacheManagementController` 手动清空 `sysDict` 缓存，或通过删除/更新字典主表（目前 `SysDictController` 的增删改也未加 `@CacheEvict`）。

### 公开端点

`GET /api/system/dict/items/{dictType}` 是**字典项的公开查询端点**：

- 路径变量 `dictType` 为字典类型标识。
- 无 `@PreAuthorize` 注解，仅需登录（`anyRequest().authenticated()`）即可访问。
- 前端用于下拉框、单选框、状态标签等场景的字典数据加载，避免每次访问管理后台字典维护页面。
- 返回 `Result<List<SysDictItem>>`。

### @Cacheable 工作机制

Spring Cache 通过 `RedisConfig.cacheManager` 配置的 `RedisCacheManager` 实现，命名缓存 `sysDict` 的 TTL 为 60 分钟（`NAMED_CACHE_TTL = Duration.ofMinutes(60)`）。

- 第一次调用 `listItemsByDictType("asset_status")` 时，方法体执行并查询数据库，结果序列化为 JSON 写入 Redis（key 形如 `sysDict::items:asset_status`）。
- 后续相同 `dictType` 的调用直接从 Redis 读取，跳过数据库查询。
- Redis 序列化使用 `GenericJackson2JsonRedisSerializer` + 自定义 `ObjectMapper`（注册 `JavaTimeModule` 支持 `LocalDateTime`，激活默认类型信息保留多态能力）。

---

## Redis 缓存策略

### 缓存键与命名空间

`RedisConfig.cacheManager` 预置 4 个命名缓存，TTL 均为 60 分钟：

| 缓存名 | TTL | 用途 | 写入点 | 失效点 |
|--------|-----|------|--------|--------|
| `sysDict` | 60 min | 字典主表与字典项 | `SysDictServiceImpl.getByDictType`/`listItemsByDictType`（`@Cacheable`） | 无自动失效（需手动清空） |
| `sysMenu` | 60 min | 用户菜单与子菜单 | `SysMenuServiceImpl.listMenusByUserId`/`listChildren`（`@Cacheable`） | 无自动失效（菜单变更需手动清空） |
| `sysConfig` | 60 min | 参数配置 | `SysConfigServiceImpl.getByConfigKey`（`@Cacheable`） | `create`/`update`/`deleteById` 均 `@CacheEvict(allEntries=true)` |
| `sysRole` | 60 min | 角色 | `SysRoleServiceImpl.getByRoleCode`（`@Cacheable`） | `assignMenus`（`@CacheEvict(allEntries=true)`） |

默认缓存（未指定名称的 `@Cacheable`）TTL = 30 分钟 + 0~5 分钟随机抖动（防雪崩，`DEFAULT_TTL = Duration.ofMinutes(30)`，`JITTER_MAX = Duration.ofMinutes(5)`）。

**缓存键规则**：

- `sysDict::{dictType}` 与 `sysDict::items:{dictType}`
- `sysMenu::byUser:{userId}` 与 `sysMenu::children:{parentId}`
- `sysConfig::{configKey}`
- `sysRole::{roleCode}`

### 失效策略

- **显式失效**：通过 `@CacheEvict(allEntries=true)` 在写操作时清空整个命名空间。`SysConfigServiceImpl` 的 `create/update/deleteById` 与 `SysRoleServiceImpl.assignMenus` 采用此策略。
- **手动失效**：通过 `CacheManagementController` 提供 API：
  - `POST /api/system/cache/clearAll` 清空所有缓存（需 `system:cache:clear` 权限）。
  - `POST /api/system/cache/clear/{cacheName}` 清空指定命名空间缓存（需 `system:cache:clear` 权限）。
  - 两者均标注 `@OperLog`，记录操作日志。
- **未自动失效的隐患**：`SysDictController`/`SysDictItemController`/`SysMenuController` 的增删改未加 `@CacheEvict`，因此字典/菜单变更后需手动清空对应缓存或等待 TTL 过期。`UserAuthorityService` 的权限缓存（内存）在 `assignMenus` 时通过 `evictAll()` 主动失效。

### 权限缓存（独立机制）

`UserAuthorityService` 使用 `ConcurrentHashMap` 内存缓存（**非 Redis**），TTL = 5 分钟，与上述 Redis 命名缓存独立。键为 `username`，值为 `CachedAuthorities(authorities, expiresAt)` record。提供 `evict(username)` 与 `evictAll()` 两个失效方法，供角色权限变更时调用。

### RedisTemplate 配置

`RedisConfig.redisTemplate` Bean：

- Key 序列化：`StringRedisSerializer.UTF_8`。
- Value 序列化：`GenericJackson2JsonRedisSerializer`（使用自定义 `ObjectMapper`，注册 `JavaTimeModule`，激活默认类型信息）。
- HashKey 序列化：`StringRedisSerializer`。
- HashValue 序列化：`GenericJackson2JsonRedisSerializer`。

`TokenBlacklistService` 直接使用 `StringRedisTemplate`（Spring Boot 自动配置），存取 `token:blacklist:{jti}` 字符串键。

### 缓存预热

模块未提供显式缓存预热逻辑。首次访问字典/菜单/配置/角色查询时触发缓存写入。如需预热，可在应用启动后通过 `ApplicationRunner` 调用各 Service 的查询方法，或通过 `CacheManagementController` 清空后由首次请求触发重建。

---

## 日志体系

### 操作日志（`sys_oper_log`）

**机制**：基于 AOP 切面 `OperLogAspect`（`@Aspect` + `@Component`），拦截标注 `@OperLog` 注解的 Controller 方法。

**`@OperLog` 注解**（位于 `pms-common`，便于所有业务模块共享，避免业务模块反向依赖 `pms-system`）：

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `title` | `""` | 操作模块标题（如"用户管理"） |
| `businessType` | `0` | 业务类型：1=新增, 2=修改, 3=删除, 4=导出, 5=导入, 其他=查询 |
| `isSaveRequestData` | `true` | 是否保存请求参数（序列化为 JSON） |
| `isSaveResponseData` | `true` | 是否保存响应结果（序列化为 JSON） |

**记录流程**（`OperLogAspect.around`，`@Around("@annotation(operLog)")`）：

1. 构造 `SysOperLog`，填充 `title`、`businessType`、`method`（`类全名.方法名`）、`operName`（`SecurityUtils.getCurrentUsername()`）、`operTime`（`LocalDateTime.now()`）。
2. 从 `RequestContextHolder` 获取 `HttpServletRequest`，填充 `operUrl`、`requestMethod`。
3. 若 `isSaveRequestData=true`，将 `joinPoint.getArgs()` 序列化为 JSON 存入 `operParam`。
4. 执行原方法 `joinPoint.proceed()`：
   - 成功：`status=0`，若 `isSaveResponseData=true` 则将结果序列化为 JSON 存入 `jsonResult`。
   - 抛异常：`status=1`，`errorMsg=异常消息`，**重新抛出原异常**（不吞异常）。
5. `finally` 块中调用 `sysOperLogService.save(logEntity)` 持久化日志；持久化失败仅打 error 日志，不影响业务。
6. 返回原方法结果。

**单元测试**：`ControllerPermissionTest` 验证成功（status=0）与 `AccessDeniedException`（status=1 + errorMsg）两种场景下的日志记录。

### 登录日志（`sys_login_log`）

**机制**：`ILoginLogService.record(loginLog)` 由其他模块（如 `pms-admin` 的登录处理器）在登录成功/失败时调用。

**字段**：`username`、`loginTime`、`loginIp`、`loginLocation`、`browser`、`os`、`status`（SUCCESS/FAIL）、`message`、`userId`。

**查询**：`AuditLogController` 的 `GET /api/system/audit/login/page`（需 `system:audit:list` 权限），支持 `username`/`status` 过滤，按 `loginTime` 倒序分页。

### 异常日志（`sys_exception_log`）

**机制**：`IExceptionLogService.record(exceptionLog)` 由全局异常处理器或其他模块在捕获未处理异常时调用。

**字段**：`userId`、`username`、`requestUri`、`requestMethod`、`requestParams`（TEXT）、`exceptionType`、`exceptionMessage`（TEXT）、`stackTrace`（LONGTEXT，完整堆栈）、`requestIp`、`occurTime`。

**查询**：`AuditLogController` 的 `GET /api/system/audit/exception/page`（需 `system:audit:list` 权限），支持 `username`/`requestUri` 过滤，按 `occurTime` 倒序分页。

### 调度日志（`sys_schedule_log`）

**机制**：`IScheduleLogService.record(scheduleLog)` 由定时任务执行器（如 `pms-admin` 的 Quartz Job 或 `pms-schedule` 模块）在任务执行前后调用。

**字段**：`taskName`、`taskGroup`、`cronExpression`、`startTime`、`endTime`、`costMs`、`status`（SUCCESS/FAIL）、`errorMessage`（TEXT）、`triggerType`（AUTO/MANUAL）。

**查询与监控**：

- `AuditLogController` 的 `GET /api/system/audit/schedule/page`（需 `system:audit:list` 权限），支持 `taskName`/`status` 过滤。
- `AuditLogController` 的 `GET /api/system/audit/schedule/failed`，分页查询失败任务。
- `ScheduleMonitorController` 的 `GET /api/system/schedule/recent`（最近 100 条）、`GET /api/system/schedule/failed`（最近 24h 失败）、`GET /api/system/schedule/statistic`（按任务名分组成功/失败统计，取最近 1000 条聚合）、`POST /api/system/schedule/retry/{id}`（手动重试占位，仅记录 `MANUAL_TRIGGER` 日志，需 `system:schedule:retry` 权限）。

---

## 配置项

### JWT 配置（`pms-admin/src/main/resources/application.yml`）

```yaml
jwt:
  # Base64 编码的开发默认密钥；生产环境必须通过 JWT_SECRET 覆盖
  secret: ${JWT_SECRET:ZGV2ZWxvcG1lbnQtand0LXNlY3JldC1rZXktY2hhbmdlLW1lLTMydC1ieXRlcw==}
  expiration: 86400000  # 24 小时（毫秒）
```

### Redis 配置

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
      password: ${REDIS_PASSWORD:}
      timeout: 3000
```

### 字段级加密密钥

```yaml
app:
  security:
    # AES-256-GCM 字段级加密密钥（Base64 编码的 32 字节）
    encrypt-key: ${APP_ENCRYPT_KEY:default-encrypt-key}
```

由 `com.dp.plat.common.crypto.AesGcmEncryptor` 与 `EncryptTypeHandler` 使用，加密 `SysUser.email`/`phone` 字段。生产环境必须通过 `APP_ENCRYPT_KEY` 环境变量覆盖默认密钥。

### MyBatis-Plus 配置

```yaml
mybatis-plus:
  mapper-locations: classpath*:com/dp/plat/**/mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```

`SysUserMapper.xml` 位于 `classpath:com/dp/plat/system/mapper/SysUserMapper.xml`，会被上述 `mapper-locations` 扫描加载。

### Springdoc OpenAPI 分组

`OpenApiConfig` 定义 9 个分组，其中 `systemGroup` 匹配 `/api/system/**`：

```java
GroupedOpenApi.builder().group("system").pathsToMatch("/api/system/**").build()
```

其他分组：`project`/`asset`/`implementation`/`workflow`/`integration`/`file`/`notification`/`governance`，覆盖整个 PMS 平台的 API 文档分组。

### 缓存 TTL 常量（`RedisConfig`）

- `DEFAULT_TTL = Duration.ofMinutes(30)`：默认缓存 TTL。
- `JITTER_MAX = Duration.ofMinutes(5)`：默认缓存的随机抖动上限，防雪崩。
- `NAMED_CACHE_TTL = Duration.ofMinutes(60)`：命名缓存（sysDict/sysMenu/sysConfig/sysRole）TTL。

---

## 模块依赖关系

### Maven 依赖

`pms-system/pom.xml` 声明的依赖：

| 依赖 | 用途 |
|------|------|
| `com.dp.plat:pms-common` | 公共基础：`BaseEntity`、`Result`/`ResultCode`/`BusinessException`、`SecurityUtils`、`@OperLog`/`@DataScope`/`@FieldEncrypt`/`@RateLimit` 注解、`EncryptTypeHandler`、`XssFilter`/`RateLimitFilter`/`SecurityHeadersFilter`、`GlobalExceptionHandler` 等 |
| `spring-boot-starter-aop` | AOP 支持（`OperLogAspect`） |
| `spring-boot-starter-data-redis` | Redis 操作（`TokenBlacklistService`、`RedisTemplate`、`CacheManager`） |
| `spring-boot-starter-cache` | Spring Cache 抽象（`@Cacheable`/`@CacheEvict`） |
| `springdoc-openapi-starter-webmvc-ui` | OpenAPI 3 文档 + Swagger UI |
| `io.jsonwebtoken:jjwt-api`/`jjwt-impl`(runtime)/`jjwt-jackson`(runtime) | JWT 生成与校验 |
| `spring-boot-starter-test`(test) | 单元测试 |

间接依赖（经 `pms-common` 传递）：Spring Boot Web、Spring Security、MyBatis-Plus、MySQL 驱动、Lombok、Jackson 等。

### 被依赖关系

`pms-system` 作为系统管理中台，被以下模块依赖（基于 `network-equipment-pms` 工程的实际依赖图）：

- `pms-admin`：主启动模块，聚合 `pms-system` 与所有业务模块，提供 `application.yml` 与启动入口。
- 业务模块（项目、资产、实施、工作流、低代码等）通过 `pms-common` 共享 `@OperLog`/`@DataScope` 注解，**不直接依赖** `pms-system`，避免反向依赖。`OperLogAspect` 通过 Spring `@Component` 自动注册，对所有模块的 `@OperLog` 注解生效。

### 与其他模块的协作

- **`pms-common`**：提供 `BaseEntity`、`Result`/`ResultCode`/`BusinessException`、`SecurityUtils`、注解（`@OperLog`/`@DataScope`/`@FieldEncrypt`/`@RateLimit`）、过滤器（`XssFilter`/`RateLimitFilter`/`SecurityHeadersFilter`）、`EncryptTypeHandler`、`GlobalExceptionHandler`、MyBatis-Plus 配置等基础设施。
- **`pms-admin`**：提供 `jwt.secret`/`jwt.expiration`/Redis/数据库等运行时配置，是 `pms-system` 的实际部署宿主。
- **业务模块**：通过 `SecurityUtils.getCurrentUserId()`/`getCurrentUsername()` 获取当前登录用户，通过 `@OperLog` 记录操作日志（由 `pms-system` 的 `OperLogAspect` 切面统一处理），通过 `@DataScope` 标注 Mapper 方法触发数据权限过滤。
- **前端**：通过 `/api/auth/login` 获取 JWT，后续请求携带 `Authorization: Bearer {token}`；通过 `/api/auth/info` 获取用户信息与权限列表，用于前端路由守卫与按钮权限控制；通过 `/api/system/menu/routers` 获取动态路由；通过 `/api/system/dict/items/{dictType}` 加载字典数据。

---

## 关键技术点

1. **JWT 无状态认证 + Redis 黑名单**：JWT 自身无状态，但通过 Redis 黑名单（`token:blacklist:{jti}`，TTL = 剩余有效期）实现登出主动失效，兼顾无状态与可撤销。

2. **权限缓存分层**：
   - 第一层：`UserAuthorityService` 内存缓存（5 分钟 TTL），避免每次请求都查 DB。
   - 第二层：Spring Cache + Redis 命名缓存（60 分钟 TTL），缓存字典/菜单/配置/角色。
   - 失效联动：`assignMenus` 后调用 `userAuthorityService.evictAll()` 失效权限缓存，并 `@CacheEvict(sysRole, allEntries=true)` 失效角色缓存。

3. **字段级加密**：`SysUser.email`/`phone` 通过 `@FieldEncrypt` + `@TableField(typeHandler=EncryptTypeHandler.class)` + `@TableName(autoResultMap=true)` 三件套实现 AES-256-GCM 透明加解密，BaseMapper 自动应用，自定义 XML 显式声明 typeHandler 兜底。

4. **密码保护**：`@JsonProperty(WRITE_ONLY)` 防止密码哈希泄露到响应；`SysUserServiceImpl` 通过 BCrypt 前缀（`$2`）判断是否已加密，避免重复哈希；登录时使用 `passwordEncoder.matches` 比对，不存储明文。

5. **超级管理员加载策略**：`UserAuthorityService.doLoad` 检测到 `admin` 角色时，加载 `sys_menu` 全部权限标识，使 `@PreAuthorize("hasAuthority('xxx')")` 注解对超管也生效，避免超管被权限注解拦截。

6. **`@OperLog` AOP 切面**：`@Around("@annotation(operLog)")` 拦截标注注解的方法，成功记录 `status=0`，异常记录 `status=1 + errorMsg` 后重新抛出，确保审计日志不丢且不影响业务异常传播。注解放置于 `pms-common`，业务模块无需反向依赖 `pms-system`。

7. **数据权限拦截器**：MyBatis-Plus `InnerInterceptor` + JSqlParser，按 `@DataScope` 注解触发，对 SELECT 语句追加 `create_by = 'username'` WHERE 条件，管理员放行。简化实现，预留 `deptAlias`/`userAlias` 扩展点。

8. **缓存雪崩防护**：默认缓存 TTL = 30 分钟 + 0~5 分钟随机抖动（`ThreadLocalRandom`），避免大量缓存同时过期导致数据库压力骤增。

9. **防伪造反馈提交人**：`FeedbackController.create` 强制清空请求体中的 `userId`/`username`/`reply`/`replyBy`/`replyAt` 字段，由 `FeedbackServiceImpl.save` 通过 `SecurityUtils.getCurrentUsername()` 重新填充，防止用户伪造提交人。

10. **帮助中心公开访问**：`/api/system/help-content/list|categories|*` 在 `SecurityConfig` 中通过 `permitAll()` 放行，无需登录即可浏览帮助文档，便于未登录用户查看使用指南；写入操作仍需对应权限。

11. **限流保护**：反馈提交（5/60s）、反馈回复/关闭（30/60s）、帮助内容 CRUD（20~30/60s）通过 `@RateLimit` 注解（位于 `pms-common`）进行接口级限流，防止滥用。

12. **手动重试占位**：`ScheduleMonitorController.retry` 不真正触发任务执行，仅记录一条 `MANUAL_TRIGGER` 状态的日志用于审计，实际重试需任务自身支持（避免越权触发与重复执行）。

13. **OpenAPI 分组**：`OpenApiConfig` 按 API 路径前缀将 9 个业务域分组（system/project/asset/implementation/workflow/integration/file/notification/governance），Swagger UI 可按分组浏览，避免单页文档过长。

14. **测试覆盖**：
    - `SysUserSerializationTest`：验证 `password` 字段序列化时不输出，反序列化时可接收。
    - `ControllerPermissionTest`：验证 `@PreAuthorize` 拒绝时抛 `AccessDeniedException`（由 `GlobalExceptionHandler` 转 403），以及 `OperLogAspect` 在成功/失败两种场景下均记录审计日志。
