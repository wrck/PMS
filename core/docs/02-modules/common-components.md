# core 模块 — 公共组件功能说明

> 本文档逐一说明 core 模块各功能组件的职责、核心逻辑、输入输出与异常处理。core 不含业务逻辑，提供基础设施能力，供上层模块复用。

---

## 1. 认证授权组件

### 1.1 ShiroRealm（本地认证授权）

| 项 | 说明 |
|----|------|
| 类 | `com.dp.plat.core.realms.ShiroRealm` |
| 继承 | `org.apache.shiro.realm.AuthorizingRealm` |
| 职责 | 本地账号密码认证 + 角色/权限授权 |

**认证流程 `doGetAuthenticationInfo`**：
1. 将 Token 转为 `UsernamePasswordCaptchaToken`（携带验证码）；
2. 校验验证码：当 `sys.envirment.argu` 为 `1`/`2` 且 `sys.login.check.captcha=1` 时，比对会话中的验证码，失败抛 `CaptchaException`；
3. 调 `IShiroService.queryUserByName` 查用户，不存在抛 `UnknownAccountException`，状态 `2` 抛"已锁定"、状态 `0` 抛"已禁用"（`DisabledAccountException`）；
4. 以**用户名作盐值**，根据 `sys.adAuth`/环境判断是否走 `PasswordUtil.encryptMD5Password(明文,盐,1024次迭代)`（仅第二段 MD5，对应前端已做 SHA1 预处理；密码修改/重置走 `encryptPassword` 两段式 SHA1+MD5）；
5. 返回 `SimpleAuthenticationInfo(principal, credentials, salt, realmName)`。

**授权流程 `doGetAuthorizationInfo`**：
- 按公司隔离：系统用户（`isSysUser!=0`）用 `compId=-1`（全公司），普通用户用自身 `compId`；
- 查角色集合 `queryUserRoleByNameAndCompId` 与权限字符串集合 `queryPermissionByUsernameAndCompId`；
- 写入 `Principal` 并返回 `SimpleAuthorizationInfo`。

**异常处理**：通过抛 Shiro 标准异常，由全局 `ExceptionController` 转为统一错误响应。

### 1.2 CasRealm / CAS 单点登录

| 组件 | 职责 |
|------|------|
| `CasRealm` | 接收 CAS Server 回调的服务票据（service ticket），向 CAS 校验，通过后构建本地 Principal |
| `CasFilter` | 拦截未认证请求，重定向到 CAS Server 登录页 |
| `MySingleSignOutFilter` / `CasLogoutFilter` | CAS 单点登出：接收 CAS 的登出回调，销毁本地会话 |
| `SingleSignOutHandler` | 维护会话映射，处理 CAS 登出通知 |
| `HashMapBackedSessionMappingStorage` | 会话ID ⇄ CAS token 的内存映射存储 |

> **避坑**：CAS 单点登出依赖内存映射表，集群部署时需替换为 Redis 等共享存储，否则登出只能在单节点生效。

### 1.3 验证码与密码

| 组件 | 职责 |
|------|------|
| `UsernamePasswordCaptchaToken` | 扩展 `UsernamePasswordToken`，增加 `captcha` 字段 |
| `PasswordUtil` | 密码加密：SHA1(1 次) + MD5(1024 次) 两段式，用户名作盐（基于 Shiro `SimpleHash`） |
| `PasswordInterceptor` | 密码强度/过期校验拦截器 |
| `PasswordController` | 修改密码、密码重置 |

---

## 2. 多数据源组件

详见 [系统架构 §3 多数据源动态路由](../01-architecture/system-architecture.md#3-多数据源动态路由核心机制)。

| 组件 | 输入 | 输出 | 核心逻辑 |
|------|------|------|----------|
| `@DataSource` 注解 | `value()`=数据源Key | — | 标注在 Service 类/方法 |
| `DataSourceHolder` | 数据源Key | ThreadLocal值 | ThreadLocal 持有/清理 |
| `RoutingDataSource` | — | 目标 DataSource | `determineCurrentLookupKey()` 返回 ThreadLocal |
| `DataSourceAspect` | JoinPoint | — | `@Before` 写入、`@After` 清空 |

**关键约束**：方法注解优先级高于类注解；`@After` 必须清空 ThreadLocal 防串号。

---

## 3. 数据访问基类与通用 Service

### 3.1 AbstractBaseMapper / IAbstractBaseService

core 定义了泛型化的 CRUD 契约，所有实体复用：

```
IAbstractBaseService<T>（接口）
  ├── deleteByPrimaryKey(Object pk)        按主键删除
  ├── insert(T t)                          全字段插入
  ├── insertSelective(T t)                 选择性插入（null字段跳过）
  ├── selectByPrimaryKey(Object pk)        按主键查询
  ├── updateByPrimaryKey(T t)              全字段更新
  ├── updateByPrimaryKeySelective(T t)     选择性更新
  ├── countBySelective(T t)                条件计数
  ├── countBySelectivePageable(PageParam)  分页计数
  ├── selectBySelective(T t)               条件查询
  └── selectBySelectivePageable(PageParam) 分页查询
```

- `AbstractBaseMapper<T>` 为 MyBatis Mapper 基类；
- `AbstractBaseService<T>` 为 Service 基类实现，注入对应 Mapper；
- 上层模块实体只需继承 `BaseEntity` + 实现 Mapper + 继承 `AbstractBaseService`，即获得全套 CRUD，**避免重复样板代码**。

### 3.2 BaseEntity（公共实体基类）

| 字段 | 含义 |
|------|------|
| `id` | 主键 |
| `createBy` / `createTime` | 创建人/时间（审计） |
| `updateBy` / `updateTime` | 更新人/时间（审计） |
| `orgId` | 组织/公司ID，`getOrgId()` 缺省取 `UserContext.getOrgId()`（自动多公司隔离） |
| `customInfo` (Map) | JSON 扩展字段，提供 `getCustomInfoByKey/setCustomInfoByKey` 动态属性访问；`hasTask()` 判断是否含待办任务 |

> `customInfo` Map 模式对应数据库的 `customInfo json` 列，是 PMS 全局扩展字段约定（参见数据字典附录 customInfo）。

### 3.3 core 管理的 23 个领域 Service/Mapper

| 领域 | Service / Mapper | 对应表 |
|------|------------------|--------|
| 用户 | IUserService / UserMapper | `t_user` |
| 用户信息 | IUserInfoService / UserInfoMapper | `t_user_info` |
| 用户角色 | IUserRoleService / UserRoleMapper | `t_user_role` |
| 用户登录记录 | IUserLoginRecordService / UserLoginRecordMapper | `t_user_login_record` |
| 角色 | IRoleService / RoleMapper | `t_role` |
| 角色菜单 | IRoleMenuService / RoleMenuMapper | `t_role_menu` |
| 角色权限 | (RolePermissionMapper) | `t_role_permission` |
| 菜单 | IMenuService / MenuMapper | `t_menu` |
| 权限 | (PermissionMapper) | `t_permission` |
| 资源 | IResourceService / ResourceMapper | `t_resource` |
| 部门 | IDepartmentService / DepartmentMapper | `t_department` |
| 公司 | ICompanyService / CompanyMapper | `t_company` |
| 字典 | IDictionaryService / DictionaryMapper | `t_dictionary` |
| 文件 | IFileInfoService / FileInfoMapper | `t_file` |
| 文件类型 | (FileType) | `t_file_type` |
| 邮件 | IMailInfoService / MailInfoMapper | `t_mails` |
| 通知模板 | INotifyTemplateService / NotifyTemplateMapper | `t_notify_template` |
| 系统日志 | ISysLogService / SysLogMapper | `t_sys_log` |
| 系统变量 | ISystemVariableService / SystemVariableMapper | `t_sys_variable` |
| 同步 | ISynchronizeService / SynchronizeMapper | `t_sync_log` / `t_sync_state` |
| 同步日志 | ISyncLogService / SyncLogMapper | `t_sync_log` |
| Shiro | IShiroService | 聚合查询（用户权限/角色） |
| 数据操作 | IDataOperationService / DataOperationMapper | 通用数据操作 |
| 数据导出 | IDataExportService / DataExportMapper | 通用导出 |

---

## 4. AOP 切面

| 切面 | 注解 | 职责 |
|------|------|------|
| `DataSourceAspect` | `@DataSource` | 多数据源路由（见 §2） |
| `SystemLogAspect` | `@SystemControllerLog` / `@SystemServiceLog` | 自动记录操作日志到 `t_sys_log`（操作人/IP/方法/参数/耗时） |
| `ExceptionAspect` | — | 捕获 Service 层未处理异常，统一转换 |
| `SystemCoreFunctionAspect` | — | 核心功能增强（如权限/数据范围校验） |

`@SystemControllerLog`/`@SystemServiceLog` 通过注解的 `description` 属性描述操作语义，切面反射读取方法签名与参数，落库形成操作审计链。

---

## 5. Controller（通用接口）

| Controller | 路径前缀 | 功能 |
|------------|----------|------|
| `BaseController` | `/base` | 服务器时间、头像、图标选择、角色/资源/系统变量/通知模板详情弹窗、导入进度查询 |
| `LoginController` | `/login` | 登录/登出入口、验证码生成 |
| `UploaderController` | `/upload` | 文件上传（落 `t_file`，返回文件ID） |
| `DataExportController` | `/export` | 通用 Excel 导出 |
| `DataOperationController` | `/dataOp` | 通用数据操作（批量增删改） |
| `PasswordController` | `/password` | 修改密码、密码校验 |
| `ExceptionController` | `/exception` | 统一异常处理，转 `Result` JSON |

**统一返回结构 `Result`**：所有接口返回 `Result{code, message, data}`，`code` 见 `ResultCode`（详见 [06-reference 错误码](../06-reference/error-codes.md)）。

---

## 6. 定时任务（schedule）

| 任务 | 触发 | 职责 |
|------|------|------|
| `SynchronizeJob` | 定时 | 拉取外部系统（EHR/SMS/OA 等）数据同步到本地中间表，按 `SyncType` 枚举区分同步类型 |
| `MailerJob` | 定时 | 扫描 `t_mails` 待发邮件，调用 `support.mail` 发送 |

> 同步细节与中间表流转参见数据字典"数据同步中间表域"。

---

## 7. 视图与导出

| 组件 | 职责 |
|------|------|
| `ExcelView` / `ExcelView4XLSX` | 继承 `AbstractExcelView`，基于 Apache POI 渲染 Excel（.xls/.xlsx） |
| `MyInternalResourceViewResolver` | 自定义视图解析器 |
| `ExportUtils` | 导出数据组装工具 |

---

## 8. 工具类（util，19 个）

| 工具类 | 职责 |
|--------|------|
| `DateUtil` | 日期格式化/计算 |
| `DESSecurityUtils` | DES 对称加解密（敏感数据） |
| `PasswordUtil` | 密码加密（SHA1+MD5 两段式，基于 Shiro `SimpleHash`） |
| `UploadUtils` / `FileUtil` | 文件上传/读写 |
| `DownloadUtils` | 文件下载 |
| `ExportUtils` | Excel 导出组装 |
| `IpUtil` | IP/CIDR 范围计算（不提取客户端 IP，仅做范围匹配；客户端 IP 由 `HttpContext.getCurrentIp` 提取，`HostFilter` 调用 `IpUtil.isInRange/isInMarkRange` 做访问控制） |
| `JsoupUtil` | HTML 清洗（XSS 过滤） |
| `SQLParser` | SQL 解析（表名提取/白名单匹配/变量填充，基于 Druid） |
| `UUIDGenerator` | UUID 生成 |
| `AviatorUtils` | Aviator 表达式引擎封装（动态规则计算，配合 pms-rules） |
| `MessageUtils` | 国际化消息 |
| `PropertyUtil` | 配置文件读取 |
| `SystemLogUtil` | 日志工具 |
| `MenuUtil` | 菜单树构建（配合 `LeftMenuTag`） |
| `LinkedHashMapSort` | Map 排序 |
| `JdbcConnectionUtil` / `JDBCPropertiesUtil` | 原生 JDBC 连接（外部系统直连） |

---

## 9. 自定义标签（tags，11 个）

| 标签 | 职责 |
|------|------|
| `LeftMenuTag` | 渲染左侧菜单树（基于权限过滤） |
| `CsrfTokenScriptTag` | 输出 CSRF Token 脚本 |
| `FileInputTag` | 文件上传控件 |
| `ChangeCompanyTag` | 公司切换下拉 |
| `ShiroPrincipalTag` | 输出当前登录主体信息 |
| `ScriptTag` / `StyleTag` / `LinkTag` | 静态资源引用（带版本号） |
| `JSDebuggerTag` | 前端调试开关 |
| `SiteMeshExtTagRuleBundle` | SiteMesh 装饰器扩展规则 |
| `AbstractHtmlElementBodyTag` | HTML 标签基类 |

---

## 10. 异常体系

```
CustomRuntimeException (RuntimeException)
   ├── CaptchaException          验证码错误
   ├── UploadException           文件上传异常
   ├── ExcelImportException      Excel 导入异常
   └── (实现 CustomExceptionInterface 的其他异常)
```

- `CustomExceptionInterface`：自定义异常统一接口，约定错误码与消息；
- 全局由 `ExceptionController` + `ExceptionAspect` 捕获，转为 `Result(code,message)` 返回前端。

---

## 11. 相关文档

- [01-architecture 系统架构](../01-architecture/system-architecture.md)
- [03-database 数据字典](../03-database/complete-data-dictionary.md)
- [04-mapping 功能-数据映射](../04-mapping/crud-matrix.md)
- 知识共享：[PMS-security 安全组件](../../PMS-security/docs/02-modules/security-components.md)、[pms-rules 规则引擎](../../pms-rules/docs/02-modules/rules-engine.md)
