# PMS Action 类方法级参考文档

> 本文档深度分析 PMS 项目所有核心 Action 类，提取每个方法的完整签名、输入参数、返回值、核心业务逻辑和异常处理机制。

---

## 目录

1. [BaseAction — Action 基类](#1-baseaction--action-基类)
2. [LoginAction — 登录认证](#2-loginaction--登录认证)
3. [UserManageAction — 用户管理](#3-usermanageaction--用户管理)
4. [RoleManageAction — 角色管理](#4-rolemanageaction--角色管理)
5. [DepartmentManageAction — 部门管理](#5-departmentmanageaction--部门管理)
6. [BasicDataManageAction — 基础数据管理](#6-basicdatamanageaction--基础数据管理)
7. [OperateLogAction — 操作日志](#7-operatelogaction--操作日志)
8. [PasswordGetinfo — 密码管理（已废弃）](#8-passwordgetinfo--密码管理已废弃)
9. [UploadAction — 文件上传下载](#9-uploadaction--文件上传下载)
10. [ProjectAction — 项目管理](#10-projectaction--项目管理)
11. [PresalesAction — 售前测试项目](#11-presalesaction--售前测试项目)
12. [CallBackAction — 回访流程](#12-callbackaction--回访流程)
13. [PmClosedLoopAction — 项目闭环流程](#13-pmclosedloopaction--项目闭环流程)
14. [PmClosedLoopQuesnaireAction — 闭环问卷管理](#14-pmclosedloopquesnaireaction--闭环问卷管理)
15. [ReportAction — 报表统计](#15-reportaction--报表统计)
16. [DataAnalysisAction — 数据分析](#16-dataanalysisaction--数据分析)
17. [WorkFlowAction — 工作流管理](#17-workflowaction--工作流管理)
18. [WorkSpaceAction — 工作台](#18-workspaceaction--工作台)
19. [ClusterAction — 集群缓存同步](#19-clusteraction--集群缓存同步)
20. [ProbManageAction — 技术公告管理](#20-probmanageaction--技术公告管理)
21. [SubcontractAction — 外包项目管理](#21-subcontractaction--外包项目管理)
22. [MaintenanceAction — 项目维护](#22-maintenanceaction--项目维护)
23. [SupervisionAction — 项目督查](#23-supervisionaction--项目督查)
24. [CertificateAction — 合格证查询](#24-certificateaction--合格证查询)
25. [WarrantyCallbackAction — 维保回访](#25-warrantycallbackaction--维保回访)
26. [ProjectBaseAction — 项目管理 Action 基类](#26-projectbaseaction--项目管理-action-基类)

---

## 1. BaseAction — Action 基类

- **包路径**: `com.dp.plat.action`
- **父类**: `ActionSupport`
- **实现接口**: `ServletContextAware`, `ServletRequestAware`, `ServletResponseAware`
- **Spring Bean**: 无（非Spring管理，作为基类被继承）
- **职责**: 提供所有 Action 类的公共属性和工具方法，包括错误消息处理、Servlet 对象访问

### 属性

| 属性名 | 类型 | 说明 |
|--------|------|------|
| `errmsg` | `String` | 错误消息文本 |
| `servletContext` | `ServletContext` | Servlet 上下文对象 |
| `servletResponse` | `HttpServletResponse` | HTTP 响应对象 |
| `servletRequest` | `HttpServletRequest` | HTTP 请求对象 |

### 方法

#### `start()`
- **签名**: `public String start()`
- **参数**: 无
- **返回值**: `INPUT` — 默认跳转到输入页面
- **业务逻辑**: 作为默认入口方法，返回 INPUT 视图
- **异常处理**: 无

#### `setErrmsg(String)`
- **签名**: `public void setErrmsg(String errmsg)`
- **参数**: `errmsg` — 错误消息文本
- **返回值**: 无
- **业务逻辑**: 当 errmsg 非空且非空白时，调用 `addFieldError("errmsg", errmsg)` 添加字段错误，同时设置实例变量
- **异常处理**: 无

#### `setErrmsg(BaseService)`
- **签名**: `protected void setErrmsg(BaseService service)`
- **参数**: `service` — 业务服务对象
- **返回值**: 无
- **业务逻辑**: 从 service 中提取错误消息和警告消息，分别添加到 `addFieldError`，然后清除 service 中的错误消息
- **异常处理**: 无

#### `setWarnMessage(BaseService)`
- **签名**: `protected void setWarnMessage(BaseService service)`
- **参数**: `service` — 业务服务对象
- **返回值**: 无
- **业务逻辑**: 从 service 中提取警告消息，添加到 `addFieldError("warnmsg", ...)`，然后清除 service 中的错误消息
- **异常处理**: 无

#### `getErrmsg(BaseService)`
- **签名**: `public List<String> getErrmsg(BaseService service)`
- **参数**: `service` — 业务服务对象
- **返回值**: `List<String>` — 错误消息列表
- **业务逻辑**: 委托给 `service.getErrmsg()`

#### `getErrmsg()`
- **签名**: `public String getErrmsg()`
- **参数**: 无
- **返回值**: `String` — 当前错误消息文本
- **业务逻辑**: 返回实例变量 errmsg

#### `setServletContext(ServletContext)`
- **签名**: `public void setServletContext(ServletContext context)`
- **参数**: `context` — Servlet 上下文
- **返回值**: 无
- **业务逻辑**: 实现 `ServletContextAware` 接口，保存 Servlet 上下文引用

#### `setServletRequest(HttpServletRequest)`
- **签名**: `public void setServletRequest(HttpServletRequest request)`
- **参数**: `request` — HTTP 请求
- **返回值**: 无
- **业务逻辑**: 实现 `ServletRequestAware` 接口，保存请求对象引用

#### `setServletResponse(HttpServletResponse)`
- **签名**: `public void setServletResponse(HttpServletResponse response)`
- **参数**: `response` — HTTP 响应
- **返回值**: 无
- **业务逻辑**: 实现 `ServletResponseAware` 接口，保存响应对象引用

#### `addActionError(String)`, `addFieldError(String, String)`, `addActionMessage(String)`
- **签名**: 重写 `ActionSupport` 对应方法
- **业务逻辑**: 直接委托给父类实现

---

## 2. LoginAction — 登录认证

- **包路径**: `com.dp.plat.action`
- **父类**: `BaseAction`
- **实现接口**: 无
- **Spring Bean**: `Login`（scope=prototype，⚠️ bean id 为 `Login` 而非类名 `LoginAction`）
- **依赖服务**: `LoginService`, `UserManageService`
- **职责**: 处理用户登录/登出，支持 CAS 单点登录和普通登录两种模式

### 属性

| 属性名 | 类型 | 说明 |
|--------|------|------|
| `user` | `LoginParam` | 登录参数（用户名/密码） |
| `loginService` | `LoginService` | 登录业务服务 |
| `userManageService` | `UserManageService` | 用户管理服务 |
| `redirecturl` | `String` | 登录后重定向URL |

### 方法

#### `start()`
- **签名**: `public String start()`
- **参数**: 无
- **返回值**: `SUCCESS`（已登录且默认页面存在时）或 `INPUT`（未登录时）
- **业务逻辑**: 检查 `UserContext` 是否已登录且存在默认页面，若是则设置重定向URL并返回 SUCCESS，否则调用父类 `start()` 返回 INPUT
- **异常处理**: 无

#### `execute()`
- **签名**: `public String execute() throws Exception`
- **参数**: 无（通过属性注入 `user`）
- **返回值**: `SUCCESS` / `"errorCas"` / `INPUT`
- **业务逻辑**: 判断是否为 CAS 模式，若是则调用 `casLogin()`，否则调用 `noCasLogin()`
- **异常处理**: 抛出 Exception

#### `casLogin()` (private)
- **签名**: `private String casLogin()`
- **参数**: 无
- **返回值**: `SUCCESS` / `"errorCas"`
- **业务逻辑**:
  1. 获取客户端 IP
  2. 通过 `AssertionHolder.getAssertion()` 获取 CAS 断言
  3. 从断言中提取用户名
  4. 调用 `loginService.loginCas(user, ip)` 进行登录验证
  5. 设置重定向URL
- **异常处理**: CAS 断言为空或用户名为空时执行 `loginService.logout()` 并返回 `"errorCas"`

#### `noCasLogin()` (private)
- **签名**: `private String noCasLogin()`
- **参数**: 无
- **返回值**: `SUCCESS` / `INPUT`
- **业务逻辑**:
  1. 获取客户端 IP
  2. 调用 `loginService.login(user, ip)` 进行登录验证
  3. 登录成功设置重定向URL
- **异常处理**: 登录失败时调用 `setErrmsg(loginService)` 并返回 INPUT

#### `logout()`
- **签名**: `public String logout() throws Exception`
- **参数**: 无
- **返回值**: `SUCCESS`
- **业务逻辑**:
  1. 判断是否 CAS 模式
  2. CAS 模式：重定向到 CAS 登出URL（`https://cas.dptech.com:8443/logout?service=...`）
  3. 非 CAS 模式：重定向到 `index.jsp`
  4. 调用 `loginService.logout()` 清除会话
- **异常处理**: 抛出 Exception

#### `error404()`
- **签名**: `public String error404()`
- **参数**: 无
- **返回值**: `SUCCESS`
- **业务逻辑**: 404 错误页面跳转
- **异常处理**: 无

---

## 3. UserManageAction — 用户管理

- **包路径**: `com.dp.plat.action`
- **父类**: `BaseAction`
- **Spring Bean**: `UserManageAction`（scope=prototype）
- **实现接口**: `Preparable`
- **依赖服务**: `UserManageService`, `DepartmentManageService`
- **职责**: 用户 CRUD、密码重置、角色变更

### 属性

| 属性名 | 类型 | 说明 |
|--------|------|------|
| `displayParam` | `DisplayParam` | 分页/查询参数 |
| `userlist` | `List<User>` | 用户列表 |
| `user` | `User` | 当前操作用户对象 |
| `role` | `Role` | 角色对象 |
| `rolelist` | `List<Role>` | 角色列表 |
| `userMenuList` | `List<UserMenu>` | 系统菜单集合 |
| `usermenuids` | `String` | 用户菜单ID串 |
| `departments` | `List<Department>` | 部门列表 |
| `departmentPowers` | `List<Department>` | 数据权限部门列表 |
| `username` | `String` | 用户名 |
| `result` | `int` | 操作结果码 |
| `newMemberCode` | `String` | 新成员编码 |
| `changeType` | `String` | 角色变更类型(service/program/both) |

### 方法

#### `prepare()`
- **签名**: `public void prepare() throws Exception`
- **业务逻辑**: 权限校验，仅管理员/工程管理部/工程管理部领导可访问，否则抛出 `CustomRuntimeException`
- **异常处理**: 抛出 `CustomRuntimeException`

#### `execute()`
- **签名**: `public String execute() throws Exception`
- **参数**: `displayParam`（分页）、`user`（查询条件）
- **返回值**: `SUCCESS`
- **业务逻辑**: 查询角色列表、部门列表，调用 `userManageService.queryUserList()` 查询用户列表，处理角色名称映射
- **异常处理**: 抛出 Exception

#### `add()`
- **签名**: `public String add()`
- **参数**: `user`（新增用户数据）、`usermenuids`（菜单权限）
- **返回值**: `INPUT`（进入新增页面）/ `SUCCESS`（新增成功）/ `ERROR`（验证失败）
- **业务逻辑**:
  1. 无参数时返回 INPUT 展示新增表单
  2. 验证必填字段（用户名、真实姓名、邮箱、菜单ID、默认页面）
  3. 生成随机密码并 MD5 加密
  4. 调用 `userManageService.addUserInfo()` 保存
  5. 非 CAS 模式下发送邮件通知
- **异常处理**: 验证失败返回 ERROR

#### `checkUsername()`
- **签名**: `public String checkUsername()`
- **参数**: `username`
- **返回值**: `SUCCESS`
- **业务逻辑**: Ajax 检查用户名是否已存在，调用 `userManageService.queryUserSizeByUserName()`

#### `edit()`
- **签名**: `public String edit()`
- **参数**: `user`（编辑用户数据）、`usermenuids`、`changeType`、`newMemberCode`
- **返回值**: `INPUT`（进入编辑页面）/ `SUCCESS`（编辑成功）/ `ERROR`（验证失败）
- **业务逻辑**:
  1. 无用户名时查询原有数据返回编辑表单
  2. 验证必填字段
  3. 若 `changeType` 非空，批量变更项目服务经理/项目经理
  4. 调用 `userManageService.updateUserInfo()` 更新
- **异常处理**: 验证失败返回 ERROR

#### `submit()`
- **签名**: `public String submit()`
- **返回值**: `SUCCESS`
- **业务逻辑**: 空实现，仅返回成功

#### `pwdreset()`
- **签名**: `public String pwdreset()`
- **参数**: `user.id`
- **返回值**: `SUCCESS`
- **业务逻辑**:
  1. 生成随机密码并 MD5 加密
  2. 更新用户密码和过期时间
  3. 发送邮件通知
  4. 调用 `passwordService.forcedOffline()` 强制用户下线
- **异常处理**: 无显式异常处理

#### `findUser()`
- **签名**: `public String findUser()`
- **参数**: `username`
- **返回值**: `SUCCESS`
- **业务逻辑**: 根据用户名查询用户信息

#### `checkSubmitData(String)` (private)
- **签名**: `public boolean checkSubmitData(String submitData)`
- **参数**: `submitData` — 待检查的字符串
- **返回值**: `boolean` — true 表示数据无效
- **业务逻辑**: 检查数据是否为 null、空字符串或包含空格

---

## 4. RoleManageAction — 角色管理

- **包路径**: `com.dp.plat.action`
- **父类**: `BaseAction`
- **Spring Bean**: `RoleManageAction`（scope=prototype）
- **依赖服务**: `RoleManageService`, `UserManageService`
- **职责**: 角色 CRUD、菜单权限分配

### 方法

#### `execute()`
- **签名**: `public String execute() throws Exception`
- **参数**: `displayParam`、`role`
- **返回值**: `SUCCESS`
- **业务逻辑**: 分页查询角色列表

#### `add()`
- **签名**: `public String add() throws Exception`
- **返回值**: `INPUT`
- **业务逻辑**: 查询系统菜单列表，返回新增角色表单

#### `addSubmit()`
- **签名**: `public String addSubmit() throws Exception`
- **参数**: `role`、`rolemenuidList`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**:
  1. 验证角色菜单权限和角色名称
  2. 设置默认页面为 `module/Welcome1.action`
  3. 状态为0时设置失效时间
  4. 调用 `roleManageService.addRoleSubmit()` 保存
- **异常处理**: 验证失败返回 ERROR

#### `edit()`
- **签名**: `public String edit() throws Exception`
- **参数**: `role.id`
- **返回值**: `INPUT` / `ERROR`
- **业务逻辑**: 查询角色信息、菜单权限列表，返回编辑表单

#### `editSubmit()`
- **签名**: `public String editSubmit() throws Exception`
- **参数**: `role`、`rolemenuidList`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 验证后调用 `roleManageService.updateRoleSubmit()` 更新

---

## 5. DepartmentManageAction — 部门管理

- **包路径**: `com.dp.plat.action`
- **父类**: `BaseAction`
- **Spring Bean**: `DepartmentManageAction`（scope=prototype）
- **依赖服务**: `DepartmentManageService`
- **职责**: 部门 CRUD、部门数据刷新

### 方法

#### `execute()`
- **签名**: `public String execute() throws Exception`
- **参数**: `displayParam`、`department`
- **返回值**: `SUCCESS`
- **业务逻辑**: 分页查询部门列表

#### `refresh()`
- **签名**: `public String refresh() throws Exception`
- **返回值**: `SUCCESS`
- **业务逻辑**: 调用 `departmentManageService.refreshDepartment()` 刷新部门数据缓存

#### `add()`
- **签名**: `public String add() throws Exception`
- **返回值**: `INPUT`
- **业务逻辑**: 返回新增部门表单

#### `addSubmit()`
- **签名**: `public String addSubmit() throws Exception`
- **参数**: `department`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 调用 `departmentManageService.addDepartmentSubmit()` 保存，ID<=0 返回 ERROR

#### `edit()`
- **签名**: `public String edit() throws Exception`
- **返回值**: 调用 `super.execute()` 返回结果
- **业务逻辑**: 委托给父类 execute

---

## 6. BasicDataManageAction — 基础数据管理

- **包路径**: `com.dp.plat.action`
- **父类**: `BaseAction`
- **Spring Bean**: `BasicDataManageAction`（scope=prototype）
- **依赖服务**: `BasicDataService`
- **职责**: 基础数据类型查询、增删改、SQL执行

### 方法

#### `execute()`
- **签名**: `public String execute()`
- **参数**: `basicData`（查询条件）
- **返回值**: `SUCCESS`
- **业务逻辑**: 查询基础数据类型列表和对应数据列表

#### `basicdataUpdate()`
- **签名**: `public String basicdataUpdate()`
- **参数**: `basicData`
- **返回值**: `INPUT`（查询原数据）/ `SUCCESS`（更新成功）
- **业务逻辑**: 无 basicDataId 时查询原数据返回编辑表单；否则调用 `basicDataService.updateBasicData()` 更新并刷新系统缓存

#### `basicdataInsert()`
- **签名**: `public String basicdataInsert()`
- **参数**: `basicData`
- **返回值**: `INPUT`（进入新增页面）/ `SUCCESS`（新增成功）
- **业务逻辑**: basicData 为 null 时返回新增表单；否则调用 `basicDataService.insertBasicDataBean()` 新增

#### `findBasicDataId()`
- **签名**: `public String findBasicDataId()`
- **参数**: `dataTypeCode`、`basicDataId`
- **返回值**: `SUCCESS`
- **业务逻辑**: Ajax 检查编码是否已存在

#### `executeSql()`
- **签名**: `public String executeSql()`
- **参数**: `executeSql`
- **返回值**: `SUCCESS`
- **业务逻辑**:
  1. 检查 SQL 是否包含 WHERE 或 INSERT
  2. 包含 WHERE 则执行更新/删除
  3. 包含 INSERT 则执行插入
  4. 无 WHERE 条件的 SQL 不允许执行
- **异常处理**: 捕获 Exception，设置 `msg` 为错误信息

---

## 7. OperateLogAction — 操作日志

- **包路径**: `com.dp.plat.action`
- **父类**: `BaseAction`
- **Spring Bean**: `OperateLogAction`（scope=prototype）
- **依赖服务**: `OpLogService`
- **职责**: 操作日志查询、导出

### 方法

#### `execute()`
- **签名**: `public String execute() throws Exception`
- **参数**: `displayParam`
- **返回值**: `SUCCESS`
- **业务逻辑**: 分页查询操作日志列表

#### `exportlog()`
- **签名**: `public String exportlog()`
- **参数**: `displayParam`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**:
  1. 加载 Excel 模板 `template/日志.xlsx`
  2. 查询全部日志数据
  3. 逐行写入 Excel（用户名、真实姓名、IP、时间、操作信息）
  4. 输出到 `upload/payment/日志.xlsx`
- **异常处理**: `FileNotFoundException` 和 `IOException` 时设置错误消息返回 ERROR

#### `syncTask()`
- **签名**: `public synchronized String syncTask()`
- **参数**: `taskName`
- **返回值**: `SUCCESS`
- **业务逻辑**:
  1. 从 Spring 容器获取定时任务 Bean
  2. 依次尝试从 `CronTriggerFactoryBean`、`JobDetailFactoryBean`、类加载器获取 JobDetail
  3. 获取 Scheduler 并同步执行 Job
  4. 结果写入 `resultMap`
- **异常处理**: 收集所有异常到 errors 列表，写入 `resultMap.message`

#### `getDownloadLogName()`
- **签名**: `public String getDownloadLogName()`
- **返回值**: 日志文件下载路径
- **业务逻辑**: 返回日志 Excel 文件的相对路径

#### `getInputLogStream()`
- **签名**: `public InputStream getInputLogStream() throws FileNotFoundException, UnsupportedEncodingException`
- **返回值**: `InputStream` — 日志文件输入流
- **业务逻辑**: 通过 `ServletContext.getResourceAsStream()` 获取文件流

---

## 8. PasswordGetinfo — 密码管理（已废弃）

- **包路径**: `com.dp.plat.action`
- **父类**: `BaseAction`
- **Spring Bean**: `PasswordGetinfo`（scope=prototype）
- **依赖服务**: `PasswordService`, `UserManageService`
- **状态**: 类注释标记为"作废的action"
- **职责**: 密码修改、密码重置

### 方法

#### `executepwd()`
- **签名**: `public String executepwd() throws Exception`
- **返回值**: `SUCCESS`
- **业务逻辑**: 空实现

#### `editlogin()`
- **签名**: `public String editlogin()`
- **参数**: `passwordEditParam`
- **返回值**: `SUCCESS`
- **业务逻辑**:
  1. 调用 `passwordService.changelogin()` 修改密码
  2. 成功时设置 Session `Pwdoverdue=0`，获取默认页面并重定向
  3. 失败时设置错误消息
- **异常处理**: `IOException` 在重定向时打印堆栈

#### `resetPassword()`
- **签名**: `public String resetPassword()`
- **参数**: `passwordEditParam.id`
- **返回值**: `SUCCESS`
- **业务逻辑**:
  1. 生成随机密码并 MD5 加密
  2. 更新用户密码和过期时间
  3. 发送邮件通知
  4. 调用 `passwordService.forcedOffline()` 强制下线
  5. 设置 `result = {success: true}`

---

## 9. UploadAction — 文件上传下载

- **包路径**: `com.dp.plat.action`
- **父类**: `BaseAction`
- **Spring Bean**: `UploadAction`（scope=prototype）
- **依赖服务**: `BasicDataService`
- **职责**: 文件上传、下载、删除，富文本图片上传

### 方法

#### `upload()`
- **签名**: `public String upload()`
- **参数**: `upload`（File[]）、`uploadFileName`、`uploadFileType`
- **返回值**: `INPUT`（无文件时）/ `SUCCESS`
- **业务逻辑**:
  1. 生成随机路径
  2. 调用 `UploadFileUtil.upload()` 保存文件
  3. 调用 `basicDataService.insertFileInfo()` 记录文件信息
- **异常处理**: 捕获 Exception 打印堆栈

#### `deleteFile()`
- **签名**: `public String deleteFile()`
- **参数**: `fileId`
- **返回值**: `SUCCESS`
- **业务逻辑**: 调用 `basicDataService.deleteFile()` 删除文件记录
- **异常处理**: 捕获 Exception，设置 `message="删除失败!"`

#### `downloadFile()`
- **签名**: `public String downloadFile()`
- **参数**: `fileId`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 查询文件信息用于下载
- **异常处理**: 捕获 Exception 设置错误消息返回 ERROR

#### `queryFile()`
- **签名**: `public String queryFile()`
- **参数**: `fileIds`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 根据文件ID列表查询文件信息
- **异常处理**: 捕获 Exception 设置错误消息返回 ERROR

#### `uploadImage()`
- **签名**: `public String uploadImage()`
- **参数**: `upload`、`uploadFileName`
- **返回值**: `SUCCESS`
- **业务逻辑**:
  1. 上传到 `upload/file/images` 目录（MD5去重）
  2. 记录文件信息
  3. 返回完整 URL 路径（用于富文本编辑器）
- **异常处理**: 捕获 Exception 打印堆栈

#### `getDownloadFile()`
- **签名**: `@JSON(serialize=false) public String getDownloadFile()`
- **返回值**: ISO8859-1 编码的文件名
- **业务逻辑**: 返回下载文件名，进行字符编码转换

#### `getFileStream()`
- **签名**: `@JSON(serialize=false) public InputStream getFileStream() throws FileNotFoundException, UnsupportedEncodingException`
- **返回值**: `InputStream` — 文件输入流
- **业务逻辑**: 通过 `ServletContext.getResourceAsStream()` 获取文件流

---

## 10. ProjectAction — 项目管理

- **包路径**: `com.dp.plat.action`
- **父类**: `BaseAction`
- **实现接口**: `Preparable`
- **Spring Bean**: `ProjectAction`（scope=prototype）
- **依赖服务**: `ProjectService`, `DepartmentManageService`, `UserManageService`, `BasicDataService`, `ProjectPlanService`, `SendMailService`
- **职责**: 项目全生命周期管理，包括创建、维护、周报、交付件、合同合并拆分、批量操作等

### 核心属性

| 属性名 | 类型 | 说明 |
|--------|------|------|
| `project` | `Project` | 项目对象 |
| `projectlist` | `List<Project>` | 项目列表 |
| `displayParam` | `DisplayParam` | 分页参数 |
| `user` | `User` | 当前用户 |
| `modifyflag` | `int` | 修改权限标记(0=可修改,1=不可修改) |
| `projectTask` | `ProjectTask` | 工程计划任务 |
| `projectDeliver` | `ProjectDeliver` | 交付件对象 |
| `projectWeekly` | `ProjectWeekly` | 周报对象 |
| `workcontentList` | `List<WeeklyContent>` | 工作内容 |
| `batchCgMb` | `ProjectBatchCgMbParam` | 批量变更成员参数 |

### 方法

#### `prepareExecute()`
- **签名**: `public void prepareExecute()`
- **业务逻辑**: 初始化办事处、公司、项目分类、发货状态、工程计划状态等下拉列表数据

#### `execute()`
- **签名**: `public String execute() throws Exception`
- **参数**: `project`（查询条件）、`displayParam`
- **返回值**: `SUCCESS`
- **业务逻辑**:
  1. 调用 `initProject()` 初始化项目和分页参数
  2. 根据角色权限查询全部项目或按权限查询项目
  3. 查询市场关系数据
- **异常处理**: 捕获 Exception 打印堆栈

#### `insertProject()`
- **签名**: `public String insertProject()`
- **参数**: `project`
- **返回值**: `SUCCESS` / `ERROR` / `INPUT`
- **业务逻辑**:
  1. 检查项目参数是否为空（`checkProjectNull`）
  2. 空时查询合同号对应项目返回创建表单
  3. 非空时检查合同号是否已创建项目
  4. 调用 `projectService.insertProject()` 保存
  5. 发送立项通知邮件
- **异常处理**: 保存失败时设置错误码返回 INPUT

#### `createCHProject()`
- **签名**: `public String createCHProject()`
- **参数**: `project`
- **返回值**: `SUCCESS` / `ERROR` / `INPUT`
- **业务逻辑**: 创建串货项目，逻辑与 `insertProject` 类似但不查询 SAP 订单

#### `transferShipment()`
- **签名**: `public String transferShipment()`
- **参数**: `project`、`transferProject`、`selected`、`result`
- **返回值**: `INPUT`
- **业务逻辑**: 设备转移操作，支持多步骤（查询→选择→确认转移）

#### `exportSpotCheck()`
- **签名**: `public String exportSpotCheck()`
- **参数**: `projectId`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 生成现场验货单并下载

#### `exportOverWarrantyRemind()`
- **签名**: `public String exportOverWarrantyRemind()`
- **参数**: `projectId`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 生成过保提醒清单并下载

#### `importSpotCheckIgnoreItem()`
- **签名**: `public String importSpotCheckIgnoreItem()`
- **参数**: `upload`、`uploadFileName`
- **返回值**: `SUCCESS` / `ERROR` / `INPUT`
- **业务逻辑**: 从 Excel 导入不需要序列号明细的 item
- **异常处理**: 捕获 Exception 设置错误消息

#### `updateProject()`
- **签名**: `public String updateProject()`
- **参数**: `project`（含 paramId）、`displayParam`
- **返回值**: `INPUT`（查看模式）/ `SUCCESS`（更新成功）/ `ERROR`（无权限）/ `"invalid"`（作废项目）
- **业务逻辑**:
  1. 解码 Base64 的 paramId 获取项目ID
  2. 权限校验（Session 缓存优化）
  3. 查看模式：加载项目详情、周报、计划、交付件、成员等
  4. 编辑模式：根据角色和项目状态执行不同更新操作
  5. 判断是否可发起闭环申请
- **异常处理**: 捕获 Exception 设置错误码

#### `checkOrderData()`
- **签名**: `public String checkOrderData()`
- **参数**: `project.projectId`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 查询设备清单（汇总+明细）

#### `checkRealOrderData()`
- **签名**: `public String checkRealOrderData()`
- **参数**: `project.projectId`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 查询实施发货设备清单

#### `projectLeaseLine()`
- **签名**: `public String projectLeaseLine()`
- **参数**: `project.projectCode`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 查询租赁配置清单

#### `projectProductConfigLevelInfo()`
- **签名**: `public String projectProductConfigLevelInfo()`
- **参数**: `project.projectCode`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 查询配置关系清单

#### `checkShipmentInfo()`
- **签名**: `public String checkShipmentInfo()`
- **参数**: `project.projectId`、`project.contractNo`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 查询发货序列号列表

#### `deleteShipmentInfo()`
- **签名**: `public String deleteShipmentInfo()`
- **参数**: `projectId`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 删除发货的安装信息（需管理员/工程管理部/项目相关人员权限）

#### `checkSoftVersion()`
- **签名**: `public String checkSoftVersion()`
- **参数**: `project.projectId`、`project.contractNo`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 查询设备软件版本信息

#### `updateSoftVersion()`
- **签名**: `public String updateSoftVersion()`
- **参数**: `softVersionJson`（request参数）
- **返回值**: `SUCCESS`
- **业务逻辑**: Ajax 更新设备软件版本，解析 JSON 参数

#### `checkhistsoftversion()`
- **签名**: `public String checkhistsoftversion()`
- **参数**: `softChangeLog.projectId`、`softChangeLog.id`
- **返回值**: `SUCCESS`
- **业务逻辑**: 获取软件版本历史数据

#### `queryProjectNotification()`
- **签名**: `public String queryProjectNotification()`
- **参数**: `projectId`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 获取项目的系统通知

#### `problemTicket()`
- **签名**: `public String problemTicket()`
- **参数**: `projectId`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 获取项目的工单记录

#### `projectMaintenance()`
- **签名**: `public String projectMaintenance()`
- **参数**: `projectMaintenance`、`displayParam`
- **返回值**: `SUCCESS`
- **业务逻辑**: 查询项目维护记录

#### `createProjectMaintenance()`
- **签名**: `public String createProjectMaintenance()`
- **参数**: `project`、`projectMaintenance`、问卷相关参数
- **返回值**: `SUCCESS` / `"redirect"` / `ERROR`
- **业务逻辑**: 创建项目维护记录，含问卷填写和评分

#### `editProjectPlan()`
- **签名**: `public String editProjectPlan() throws InterruptedException`
- **参数**: `project`、`projectTask`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 制定或修改工程计划，判断是否首次制定计划并变更状态
- **异常处理**: 抛出 InterruptedException

#### `uploadDeliverableFile()`
- **签名**: `public String uploadDeliverableFile()`
- **参数**: `projectDeliver`、`projectDeliverList`
- **返回值**: `SUCCESS`
- **业务逻辑**: 上传工程交付件，循环处理每个交付件

#### `deleteDeliverById()`
- **签名**: `public String deleteDeliverById()`
- **参数**: `deliverid`
- **返回值**: `SUCCESS`
- **业务逻辑**: 删除工程交付件

#### `backToLastStep()`
- **签名**: `public String backToLastStep()`
- **参数**: `projectId`、`projectState`、`isback`、`column012`、`channelName`、`column013`、`notGrantTailCause`、`isupdate`
- **返回值**: `SUCCESS`
- **业务逻辑**: 项目回退到上一步，可选更新服务项目信息

#### `createWeekly()`
- **签名**: `public String createWeekly()`
- **参数**: `project.projectId`、`projectWeekly`
- **返回值**: `SUCCESS`
- **业务逻辑**: 创建周报，复制上周内容

#### `saveWeekly()`
- **签名**: `public String saveWeekly()`
- **参数**: `projectWeekly`、各内容列表
- **返回值**: `SUCCESS`
- **业务逻辑**: 保存周报草稿

#### `submitWeekly()`
- **签名**: `public String submitWeekly()`
- **参数**: `projectWeekly`、各内容列表
- **返回值**: `SUCCESS`
- **业务逻辑**:
  1. 保存周报
  2. 生成周报 Excel 附件
  3. 发送邮件通知
  4. 添加系统通知
- **异常处理**: 捕获 Exception 设置 result=0

#### `updateWeekly()`
- **签名**: `public String updateWeekly()`
- **参数**: `projectWeekly.weeklyId`
- **返回值**: `SUCCESS`
- **业务逻辑**: 查询周报详情用于查看/编辑

#### `UploadFile()`
- **签名**: `public String UploadFile()`
- **参数**: `upload`、`uploadFileName`、`projectWeekly.weeklyId`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 周报附件上传，检查文件扩展名白名单

#### `feedback()`
- **签名**: `public String feedback()`
- **参数**: `weeklyId`、`feedback`、`projectId`
- **返回值**: `SUCCESS`
- **业务逻辑**: 周报回复

#### `instruction()`
- **签名**: `public String instruction()`
- **参数**: `projectId`、`instructionsInfo`、`instructionId`
- **返回值**: `SUCCESS`
- **业务逻辑**: 保存项目批示

#### `queryalluser()`
- **签名**: `public String queryalluser()`
- **参数**: `roleid`
- **返回值**: `SUCCESS`
- **业务逻辑**: 根据角色ID查询有该角色的用户

#### `queryperson()`
- **签名**: `public String queryperson()`
- **返回值**: `SUCCESS`
- **业务逻辑**: 查询项目干系人，合并项目干系人和系统用户

#### `updateprojectisback()`
- **签名**: `public String updateprojectisback()`
- **参数**: `projectId`、`isback`、`backCause`、`pm`、`notbackCause`
- **返回值**: `SUCCESS`
- **业务逻辑**: 项目回退操作，根据不同回退类型发送不同邮件通知
- **异常处理**: 角色不匹配时抛出 RuntimeException，捕获后设置 result=0

#### `createMember()`
- **签名**: `public String createMember()`
- **参数**: `projectId`、`memberCode`、`memberName`、`memberRole`、`phoneNum`、`email`等
- **返回值**: `SUCCESS`
- **业务逻辑**: 创建项目成员，添加动态通知

#### `updateMember()`
- **签名**: `public String updateMember()`
- **参数**: `memberId`、`memberEffectiveTo`、`projectId`
- **返回值**: `SUCCESS`
- **业务逻辑**: 更新项目成员信息（设置失效时间）

#### `saveInstallAdress()`
- **签名**: `public String saveInstallAdress()`
- **参数**: `projectId`、`selected`、`installAddress`
- **返回值**: `SUCCESS`
- **业务逻辑**: 保存安装地址

#### `updateProjectExecutionState()`
- **签名**: `public String updateProjectExecutionState()`
- **参数**: `project.projectId`、`project.executionState`
- **返回值**: `SUCCESS`
- **业务逻辑**: 更新项目实施状态

#### `toMergeOrBranch()`
- **签名**: `public String toMergeOrBranch()`
- **参数**: `project.projectId`
- **返回值**: `INPUT`
- **业务逻辑**: 进入合同拆分合并页面

#### `checkMergeContract()`
- **签名**: `public String checkMergeContract()`
- **参数**: `mergeContractNo`
- **返回值**: `SUCCESS`
- **业务逻辑**: 查询要合并的合同信息

#### `mergeContract()`
- **签名**: `public String mergeContract()`
- **参数**: `selected`、`projectId`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 合同合并操作

#### `branchContract()`
- **签名**: `public String branchContract()`
- **参数**: `projectId`、`project.projectCode`、`productList`、`mergeBranchMark`
- **返回值**: `SUCCESS`
- **业务逻辑**: 项目拆分

#### `queryDpNoRoleUser()`
- **签名**: `public String queryDpNoRoleUser()`
- **参数**: `roleid`、`batchCgMb.dpNo`
- **返回值**: `SUCCESS`
- **业务逻辑**: 查询指定部门指定角色的用户

#### `batchChangeMember()`
- **签名**: `public String batchChangeMember()`
- **参数**: `batchCgMb`
- **返回值**: `SUCCESS` / `INPUT`
- **业务逻辑**: 批量变更项目服务经理/项目经理

#### `importProject()`
- **签名**: `public String importProject()`
- **参数**: `batchFunc`、上传文件
- **返回值**: `SUCCESS`
- **业务逻辑**: 批量创建项目或关闭项目（管理员权限）
- **异常处理**: 捕获 Exception 设置 result=2

#### `clearProject()`
- **签名**: `public String clearProject()`
- **参数**: `upload`、`uploadFileName`、`modifyflag`
- **返回值**: `SUCCESS` / `ERROR` / `INPUT`
- **业务逻辑**: 批量删除或作废项目
- **异常处理**: 捕获 Exception 设置错误消息

---

## 11. PresalesAction — 售前测试项目

- **包路径**: `com.dp.plat.action`
- **父类**: `BaseAction`
- **实现接口**: `Preparable`
- **Spring Bean**: `PresalesAction`（scope=prototype）
- **依赖服务**: `PresalesService`, `BasicDataService`, `PmClosedLoopQuesnaireService`, `CallBackService`, `PmClosedLoopService`, `DepartmentManageService`
- **职责**: 售前测试项目管理，包括流程发起、审批、回访问卷、交付件管理

### 方法

#### `prepareList()`
- **签名**: `public void prepareList()`
- **业务逻辑**: 初始化办事处、项目状态、项目类型下拉列表

#### `list()`
- **签名**: `public String list()`
- **参数**: `presales`、`displayParam`
- **返回值**: `"list"`
- **业务逻辑**: 售前项目列表查询，支持导出

#### `input()`
- **签名**: `public String input()`
- **参数**: `presales.presalesId`
- **返回值**: `"input"` / `ERROR` / `SUCCESS`
- **业务逻辑**: 准备发起流程申请，项目查阅人员重定向到 read 页面

#### `apply()`
- **签名**: `public String apply()`
- **参数**: `presales`、`param`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 发起售前流程或重新申请

#### `read()`
- **签名**: `public String read()`
- **参数**: `presales.presalesId`
- **返回值**: `"read"` / `ERROR`
- **业务逻辑**: 查看售前项目详情

#### `aduit()`
- **签名**: `public String aduit()`
- **参数**: `presales.presalesId`、`presales.taskDefKey`
- **返回值**: `SUCCESS`
- **业务逻辑**: 根据任务定义Key判断跳转到哪个审批页面

#### `smaduit()`
- **签名**: `public String smaduit()`
- **参数**: `presales`、`param`
- **返回值**: `"smaduit"` / `SUCCESS` / `ERROR`
- **业务逻辑**: 服务经理审批任务

#### `pmaduit()`
- **签名**: `public String pmaduit()`
- **参数**: `presales`、`param`、`urlParams`
- **返回值**: `"pmaduit"` / `SUCCESS` / `ERROR`
- **业务逻辑**: 项目经理审批任务

#### `updateTask()`
- **签名**: `public String updateTask()`
- **参数**: `presalesTaskId`、`taskFinshedTime`、`remark`
- **返回值**: `SUCCESS`
- **业务逻辑**: 更新项目计划完成时间

#### `emaduit()`
- **签名**: `public String emaduit()`
- **参数**: `presales`、`param`
- **返回值**: `"emaduit"` / `SUCCESS` / `ERROR`
- **业务逻辑**: 工程管理部回访审批

#### `callback()`
- **签名**: `public String callback()`
- **参数**: 问卷相关参数
- **返回值**: `"callback"` / `SUCCESS`
- **业务逻辑**: 回访问卷填写和提交

#### `shipmentInfo()`
- **签名**: `public String shipmentInfo()`
- **参数**: `presalesCode`、`containRma`
- **返回值**: `"shipmentInfo"`
- **业务逻辑**: 查询发货信息

#### `lend2SaleInfo()`
- **签名**: `public String lend2SaleInfo()`
- **参数**: `presalesCode`
- **返回值**: `"lend2SaleInfo"`
- **业务逻辑**: 查询借转销信息

#### `lend2RmaInfo()`
- **签名**: `public String lend2RmaInfo()`
- **参数**: `presalesCode`
- **返回值**: `"lend2RmaInfo"`
- **业务逻辑**: 查询核销信息

#### `tempAuthInfo()`
- **签名**: `public String tempAuthInfo()`
- **参数**: `presales`
- **返回值**: `"tempAuthInfo"`
- **业务逻辑**: 查询临时授权信息

#### `terminate2Close()`
- **签名**: `public String terminate2Close()`
- **参数**: `presalesIds`、`message`
- **返回值**: `SUCCESS`
- **业务逻辑**: 终止流程并关闭

#### `syncOaData()`
- **签名**: `public String syncOaData()`
- **返回值**: `SUCCESS`
- **业务逻辑**: 同步 OA 售前数据

#### `upload()`
- **签名**: `public String upload()`
- **参数**: `projectDeliver`、`projectDeliverList`
- **返回值**: `SUCCESS` / `"upload"` / `ERROR`
- **业务逻辑**: 上传工程交付件

#### `deleteDeliverById()`
- **签名**: `public String deleteDeliverById()`
- **参数**: `fileId`
- **返回值**: `SUCCESS`
- **业务逻辑**: 删除交付件

#### `updateDeliverById()`
- **签名**: `public String updateDeliverById()`
- **参数**: `projectDeliver`
- **返回值**: `SUCCESS`
- **业务逻辑**: 更新交付件

#### `exportPresales()`
- **签名**: `public String exportPresales()`
- **参数**: `presales`、`displayParam`
- **返回值**: `"list"`
- **业务逻辑**: 导出售前项目数据

---

## 12. CallBackAction — 回访流程

- **包路径**: `com.dp.plat.action`
- **父类**: `BaseAction`
- **Spring Bean**: `CallBackAction`（scope=prototype）
- **依赖服务**: `BasicDataService`, `ProjectService`, `PmClosedLoopQuesnaireService`, `CallBackService`, `PmClosedLoopService`
- **职责**: 项目回访流程管理，包括发起申请、审批、问卷填写

### 方法

#### `input()`
- **签名**: `public String input()`
- **参数**: `project.projectId`
- **返回值**: `INPUT`
- **业务逻辑**: 获取项目信息和成员列表，进入回访申请页面

#### `apply()`
- **签名**: `public String apply()`
- **参数**: `callBack`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 发起回访申请流程

#### `read()`
- **签名**: `public String read()`
- **参数**: `callBack`
- **返回值**: `"read"`
- **业务逻辑**: 查看回访流程详情

#### `seeQuesnaire()`
- **签名**: `public String seeQuesnaire()`
- **参数**: `quesnaireId`
- **返回值**: `"seeQuesnaire"`
- **业务逻辑**: 查看回访问卷

#### `resubmit()`
- **签名**: `public String resubmit()`
- **参数**: `param`、`callBack`
- **返回值**: `"resubmit"` / `SUCCESS`
- **业务逻辑**: 驳回后重新提交

#### `aduit()`
- **签名**: `public String aduit()`
- **参数**: `callBack`、`param`、问卷相关参数
- **返回值**: `"aduit"` / `SUCCESS`
- **业务逻辑**: 问卷保存/提交 + 审批操作

---

## 13. PmClosedLoopAction — 项目闭环流程

- **包路径**: `com.dp.plat.action`
- **父类**: `BaseAction`
- **Spring Bean**: `PmClosedLoopAction`（scope=prototype）
- **依赖服务**: `PmClosedLoopService`, `ProjectService`, `PmClosedLoopQuesnaireService`, `UserManageService`, `BasicDataService`
- **职责**: 项目闭环流程管理，包括项目经理申请、服务经理审核、回访人员审核、工程人员审核

### 方法

#### `execute()`
- **签名**: `public String execute() throws Exception`
- **参数**: `project.projectId`、`pmClosedLoopResultType`
- **返回值**: `SUCCESS` / `ERROR` / `INPUT`
- **业务逻辑**:
  1. 获取项目信息和成员
  2. 权限校验
  3. 获取申请历史和任务ID
  4. 根据 `pmClosedLoopResultType` 获取不同表单（回访问卷/闭环建议问卷）
- **异常处理**: 抛出 Exception

#### `addPmCLApply()`
- **签名**: `public String addPmCLApply() throws Exception`
- **参数**: `project`、`pmClEvaluationHeader`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 项目经理提交闭环申请，验证服务经理有效性

#### `addSmCLApply()`
- **签名**: `public String addSmCLApply()`
- **参数**: `project`、`workflowCommonParam`、`pmClEvaluationHeader`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 服务经理审核闭环申请

#### `addCbCLApply()`
- **签名**: `public String addCbCLApply()`
- **参数**: `project`、`workflowCommonParam`、`pmClEvaluationHeader`、问卷参数
- **返回值**: `SUCCESS` / `ERROR` / `INPUT` / `"seeScore"`
- **业务逻辑**: 回访人员审核，含问卷评分

#### `cantCB()`
- **签名**: `public String cantCB()`
- **参数**: `project`、`workflowCommonParam`、`pmClEvaluationHeader`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 回访人员标记无法回访

#### `addClCLApply()`
- **签名**: `public String addClCLApply() throws Exception`
- **参数**: `project`、`workflowCommonParam`、`pmClEvaluationHeader`、问卷参数
- **返回值**: `SUCCESS` / `ERROR` / `"seeScore"`
- **业务逻辑**: 工程人员审核闭环建议

#### `pmSeeCbCl()`
- **签名**: `public String pmSeeCbCl()`
- **参数**: `pmClEvaluationHeader.id`
- **返回值**: `"pmSeeCbCl"` / `ERROR`
- **业务逻辑**: 项目经理查看回访/闭环问卷

#### `getUserPower()`
- **签名**: `public boolean getUserPower(Project projectObj, String checkStr, int doStr)`
- **参数**: `projectObj`（项目）、`checkStr`（权限检查字符串，逗号分隔）、`doStr`（0=OR,1=AND）
- **返回值**: `boolean` — 是否有权限
- **业务逻辑**: 根据项目角色和用户角色判断权限

---

## 14. PmClosedLoopQuesnaireAction — 闭环问卷管理

- **包路径**: `com.dp.plat.action`
- **父类**: `BaseAction`
- **Spring Bean**: `PmClosedLoopQuesnaireAction`（scope=prototype）
- **依赖服务**: `PmClosedLoopQuesnaireService`, `BasicDataService`
- **职责**: 闭环问卷模板的 CRUD、题目管理、生效/失效控制

### 方法

#### `execute()`
- **签名**: `public String execute() throws Exception`
- **参数**: `pmClosedLoopQuesnaire`、`displayParam`
- **返回值**: `INPUT`
- **业务逻辑**: 查询问卷模板列表

#### `addPCLQuesnaire()`
- **签名**: `public String addPCLQuesnaire() throws Exception`
- **返回值**: `INPUT`
- **业务逻辑**: 新增问卷模板，加载问题类型和评分规则

#### `pmCLQuesEdit()`
- **签名**: `public String pmCLQuesEdit()`
- **参数**: `pmClosedLoopQuesnaire.id`
- **返回值**: `INPUT` / `ERROR`
- **业务逻辑**: 编辑问卷模板，加载题目和选项

#### `submitQues()`
- **签名**: `public String submitQues() throws Exception`
- **参数**: `pmClosedLoopQuesnaire`
- **返回值**: `"addQues"` / `ERROR`
- **业务逻辑**: 提交问卷头信息（草稿状态）

#### `addLine()`
- **签名**: `public String addLine() throws Exception`
- **参数**: `pmClosedLoopQuesnaire.id`、`doType`
- **返回值**: `INPUT` / `ERROR`
- **业务逻辑**: 新增/编辑问卷题目

#### `submitLine()`
- **签名**: `public String submitLine() throws Exception`
- **参数**: `pmClosedLoopQuesnaireLine`、`pmClosedLoopQuesnaireOptList`、`doType`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 提交题目和选项，检查分数不超过总分
- **异常处理**: 捕获 Exception 设置错误消息

#### `updateQues()`
- **签名**: `public String updateQues() throws Exception`
- **参数**: `pmClosedLoopQuesnaire`
- **返回值**: `INPUT` / `ERROR`
- **业务逻辑**: 更新问卷头信息

#### `deleteHeader()`
- **签名**: `public String deleteHeader()`
- **参数**: `pmClosedLoopQuesnaire.id`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 删除问卷模板

#### `startEffective()`
- **签名**: `public String startEffective() throws Exception`
- **参数**: `pmClosedLoopQuesnaire`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 问卷生效

#### `pmCLQuesSee()`
- **签名**: `public String pmCLQuesSee()`
- **参数**: `pmClosedLoopQuesnaire.id`
- **返回值**: `INPUT`
- **业务逻辑**: 查看问卷详情

#### `deleteLine()`
- **签名**: `public String deleteLine()`
- **参数**: `pmClosedLoopQuesnaireLine.id`
- **返回值**: `INPUT` / `ERROR`
- **业务逻辑**: 删除问卷题目

#### `endEffective()`
- **签名**: `public String endEffective() throws Exception`
- **参数**: `pmClosedLoopQuesnaire.id`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 问卷失效

---

## 15. ReportAction — 报表统计

- **包路径**: `com.dp.plat.action`
- **父类**: `BaseAction`
- **实现接口**: `Preparable`
- **Spring Bean**: `ReportAction`（scope=prototype）
- **依赖服务**: `BasicDataService`, `DepartmentManageService`, `ReportService`
- **职责**: 报表数据查询、图表生成、数据透视表

### 方法

#### `prepare()`
- **签名**: `public void prepare() throws Exception`
- **业务逻辑**: 初始化查询参数（默认当年第一天/季度第一天），权限校验（非管理员/工程管理部/工程管理部领导/财务/项目管理员/回访人员重定向至项目状态统计页面）

#### `show()`
- **签名**: `public String show()`
- **参数**: `queryParam`
- **返回值**: `"show"`
- **业务逻辑**: 报表主页面，加载选项卡、办事处列表、全国统计综述、项目经理指派率、跟踪率、闭环新增比的数据透视表HTML

#### `projectSummaryStatus()`
- **签名**: `public String projectSummaryStatus()`
- **参数**: `dataJson`、`data`
- **返回值**: `"projectSummaryStatus"` / `SUCCESS`
- **业务逻辑**: 项目状态汇总统计，支持按办事处、实施状态、流程状态等多维度统计，非管理员按区域权限过滤

#### `loadLineData()`
- **签名**: `public String loadLineData()`
- **参数**: `officeCode`、`dataTypeCode`
- **返回值**: `SUCCESS`
- **业务逻辑**: 加载趋势图（折线图），查询指定办事处和数据类型的趋势数据，生成Echarts JSON

#### `loadLine_qualityData()`
- **签名**: `public String loadLine_qualityData()`
- **参数**: `officeCode`、`dataTypeCode`
- **返回值**: `SUCCESS`
- **业务逻辑**: 查询项目闭环数量的趋势图

#### `loadLine_implData()`
- **签名**: `public String loadLine_implData()`
- **参数**: `officeCode`、`dataTypeCode`
- **返回值**: `SUCCESS`
- **业务逻辑**: 加载企业网项目实施占比趋势图（原厂直服/原厂督导/代理商自服）

#### `assignedRate()`
- **签名**: `public String assignedRate()`
- **参数**: `queryParam`
- **返回值**: `SUCCESS`
- **业务逻辑**: 项目指派率查询，生成柱状图JSON

#### `traceRate()`
- **签名**: `public String traceRate()`
- **参数**: `queryParam`
- **返回值**: `SUCCESS`
- **业务逻辑**: 项目经理跟踪率查询，生成柱状图JSON

#### `closeRate()`
- **签名**: `public String closeRate()`
- **参数**: `queryParam`
- **返回值**: `SUCCESS`
- **业务逻辑**: 季度新增闭环比查询

#### `implRate()`
- **签名**: `public String implRate()`
- **参数**: `queryParam`
- **返回值**: `SUCCESS`
- **业务逻辑**: 项目实施方式占比查询（原厂直服/原厂督导/代理商自服）

#### `quality()`
- **签名**: `public String quality()`
- **参数**: `queryParam`
- **返回值**: `SUCCESS`
- **业务逻辑**: 项目质量查询，闭环平均得分和闭环项目数量

#### `input()` (已废弃)
- **签名**: `@Deprecated public String input()`
- **返回值**: `"input"`
- **业务逻辑**: 已废弃的旧入口方法

---

## 16. DataAnalysisAction — 数据分析

- **包路径**: `com.dp.plat.action`
- **父类**: `BaseAction`
- **Spring Bean**: `DataAnalysisAction`（scope=prototype）
- **依赖服务**: `BasicDataService`, `DataAnalysisService`, `DepartmentManageService`
- **职责**: 回访数据信息统计

### 方法

#### `execute()`
- **签名**: `public String execute()`
- **参数**: `dataQueryParam`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**:
  1. 加载公司、办事处、项目阶段、服务类型、项目类型下拉列表
  2. 查询回访数据统计
  3. 加载选项卡

---

## 17. WorkFlowAction — 工作流管理

- **包路径**: `com.dp.plat.action`
- **父类**: `BaseAction`
- **实现接口**: `Preparable`
- **Spring Bean**: `WorkFlowAction`（scope=prototype）
- **依赖服务**: `WorkFlowService`, `WorkSpaceService`
- **职责**: Activiti 工作流引擎管理，包括流程部署、定义查看、任务办理

### 方法

#### `execute()`
- **签名**: `public String execute()`
- **返回值**: `INPUT`
- **业务逻辑**: 查看流程部署信息和流程定义信息

#### `newdeploy()`
- **签名**: `public String newdeploy()`
- **参数**: `filename`、`file`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 发布流程（部署 BPMN 文件）
- **异常处理**: 捕获 Exception 设置错误消息

#### `deldeployment()`
- **签名**: `public String deldeployment()`
- **参数**: `param.deploymentId`
- **返回值**: `SUCCESS`
- **业务逻辑**: 删除流程部署信息

> 注：WorkFlowAction 还包含流程图查看、任务办理、流程代理等方法。

---

## 18. WorkSpaceAction — 工作台

- **包路径**: `com.dp.plat.action`
- **父类**: `BaseAction`
- **实现接口**: `Preparable`
- **Spring Bean**: `WorkSpaceAction`（scope=prototype）
- **依赖服务**: `WorkSpaceService`, `BasicDataService`, `DepartmentManageService`, `WorkFlowService`
- **职责**: 用户工作台，展示待办任务、系统通知、日常项目跟踪

### 方法

#### `prepare()`
- **签名**: `public void prepare() throws Exception`
- **业务逻辑**: 初始化用户角色、办事处列表、选项卡，根据角色过滤选项卡

> 注：WorkSpaceAction 的 `execute()` 方法根据选项卡加载不同类型的待办任务数据。

---

## 19. ClusterAction — 集群缓存同步

- **包路径**: `com.dp.plat.action`
- **父类**: `BaseAction`
- **Spring Bean**: ⚠️ **未注册**（在 `applicationContext-action.xml` 中未配置，由 Struts2 直接实例化，不经过 Spring 容器管理）
- **依赖服务**: `BasicDataService`
- **职责**: 集群环境下缓存数据同步

### 方法

#### `refreshCacheData()`
- **签名**: `public String refreshCacheData()`
- **参数**: `signature`
- **返回值**: `SUCCESS`
- **业务逻辑**:
  1. 验证签名（AES 解密），管理员可直接访问
  2. 调用 `basicDataService.refreshCacheData()` 刷新缓存
- **异常处理**: 签名验证失败会抛出异常

#### `notifyCluster()` (static)
- **签名**: `public static String notifyCluster()`
- **返回值**: `String` — 加密签名
- **业务逻辑**: 生成集群通知签名（AES 加密），包含服务器名和时间戳

---

## 20. ProbManageAction — 技术公告管理

- **包路径**: `com.dp.plat.prob.action`
- **父类**: `BaseAction`
- **实现接口**: `Preparable`
- **Spring Bean**: `ProbManageAction`（scope=prototype）
- **依赖服务**: `ProbManageService`, `BasicDataService`, `DepartmentManageService`
- **职责**: 技术公告（问题）管理，包括问题查询、创建、处理、导出

### 核心属性

| 属性名 | 类型 | 说明 |
|--------|------|------|
| `prob` | `Prob` | 技术公告对象 |
| `probList` | `List<Prob>` | 技术公告列表 |
| `displayParam` | `DisplayParam` | 分页参数 |
| `softVersionList` | `List<SoftVersion>` | 软件版本集合 |
| `probRestoreList` | `List<ProbRestore>` | 受影响设备数据 |
| `probRestoreTaskList` | `List<ProbRestore>` | 修复任务数据集合 |
| `probStatistic` | `ProbStatistic` | 技术公告统计表 |
| `probProduct` | `ProbProductVO` | 产品型号 |
| `productComponent` | `ProductComponentVO` | 产品组件 |

### 方法

#### `prepare()`
- **签名**: `public void prepare() throws Exception`
- **业务逻辑**: 获取当前 Action 的 namespace，初始化用户信息

#### `list()`
- **签名**: `public String list()`
- **参数**: `prob`、`displayParam`
- **返回值**: `"list"`
- **业务逻辑**: 技术公告列表查询，加载跟踪/状态/关联场景类型/规避方案类型/解决方案类型下拉列表

#### `input()`
- **签名**: `public String input()`
- **参数**: `prob`
- **返回值**: `INPUT`
- **业务逻辑**: 进入创建页面，新公告自动生成编号（SP.yyyyMMddHHmm），已有公告查询详情、附件、软件版本、产品型号

#### `delete()`
- **签名**: `public String delete()`
- **参数**: `prob.probId`
- **返回值**: `SUCCESS`
- **业务逻辑**: 删除技术公告

#### `bacthDeleteProbRestores()`
- **签名**: `public String bacthDeleteProbRestores()`
- **参数**: `probRestoreIds`
- **返回值**: `SUCCESS`
- **业务逻辑**: 批量删除子任务

#### `edit()`
- **签名**: `public String edit()`
- **参数**: `prob`
- **返回值**: `"edit"` / `ERROR`
- **业务逻辑**: 进入修改查看页面，查询公告详情、附件、软件版本、产品型号、子任务，记录阅读日志

#### `checkProject()`
- **签名**: `public String checkProject()`
- **参数**: `probRestore`、`restoreDisplayParam`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 检索受技术公告影响的项目集合

#### `checkSubProject()`
- **签名**: `public String checkSubProject()`
- **参数**: `probRestore`、`restoreDisplayParam`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 检索受技术公告影响的子项目

#### `releaseTask()`
- **签名**: `public String releaseTask()`
- **参数**: `probRestore`、`probRestoreTaskList`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 发布技术公告修复任务，批量插入子任务并发送邮件通知

#### `managePrivateTask()`
- **签名**: `public String managePrivateTask()`
- **参数**: `probRestore`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 管理个人任务，加载修复任务状态和软件版本

#### `updatePrivateTask()`
- **签名**: `public String updatePrivateTask()`
- **参数**: `probRestore`、`restoreIds`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 更新个人任务状态

#### `weeklyUpload()`
- **签名**: `public String weeklyUpload()`
- **参数**: `upload`、`uploadFileName`、`probRestore`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 上传任务进展周报

#### `manageAllTask()`
- **签名**: `public String manageAllTask()`
- **参数**: `probRestore`、`restoreDisplayParam`
- **返回值**: `SUCCESS`
- **业务逻辑**: 管理员管理所有任务，按状态分类查询

#### `updateRestoreTask()`
- **签名**: `public String updateRestoreTask()`
- **参数**: `probRestore`、`restoreIds`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 管理员更新任务

#### `save()`
- **签名**: `public String save()`
- **参数**: `prob`、`softVersionList`、`upload`、`uploadFileName`、`isContinue`
- **返回值**: `SUCCESS` / `"continue"` / `ERROR`
- **业务逻辑**: 保存技术公告信息，上传附件，发送邮件通知

#### `update()`
- **签名**: `public String update()`
- **参数**: `prob`、`softVersionList`、`upload`、`uploadFileName`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 更新技术公告信息

#### `audit()`
- **签名**: `public String audit()`
- **参数**: `prob`、`softVersionList`
- **返回值**: `SUCCESS`
- **业务逻辑**: 技术公告管理员驳回/审批任务

#### `export()`
- **签名**: `public String export()`
- **参数**: `prob`、`displayParam`
- **返回值**: `null`（直接输出Excel流）/ `ERROR`
- **业务逻辑**: 导出技术公告为Excel文件

#### `importSoftVersion()`
- **签名**: `public String importSoftVersion()`
- **参数**: `upload`、`uploadFileName`
- **返回值**: `"importSoftVersion"` / `ERROR`
- **业务逻辑**: 上传Excel批量导入软件版本

#### `toCheckSoftVersion()`
- **签名**: `public String toCheckSoftVersion()`
- **参数**: `softVersion`
- **返回值**: `INPUT`
- **业务逻辑**: 进入查询软件版本页面

#### `submitSoftVersion()`
- **签名**: `public String submitSoftVersion()`
- **参数**: `softVersionCodes`
- **返回值**: `SUCCESS`
- **业务逻辑**: 确认选择软件版本

#### `parserSoftVersion()`
- **签名**: `public String parserSoftVersion()`
- **参数**: `softVersion`
- **返回值**: `SUCCESS`
- **业务逻辑**: 根据手工录入的信息解析软件版本范围

#### `parserOldSoftVersion()`
- **签名**: `public String parserOldSoftVersion()`
- **参数**: `softVersion`
- **返回值**: `SUCCESS`
- **业务逻辑**: 解析已有软件版本的手工输入信息

#### `statistics()`
- **签名**: `public String statistics()`
- **参数**: `probStatistic`、`displayParam`
- **返回值**: `"statistics"`
- **业务逻辑**: 技术公告统计，支持多选项卡（统计图表/项目列表/合同出货/受影响设备）

#### `affectedProjectSoftVersion()`
- **签名**: `public String affectedProjectSoftVersion()`
- **参数**: `probStatistic`、`probRestore`、`displayParam`
- **返回值**: `SUCCESS` / `"affectedProjectSoftVersion"`
- **业务逻辑**: 受影响项目软件版本查询

#### `readSure()`
- **签名**: `public String readSure()`
- **参数**: `probReadLog`
- **返回值**: `SUCCESS`
- **业务逻辑**: 技术公告阅读确认

#### `readLog()`
- **签名**: `public String readLog()`
- **参数**: `probReadLog`、`displayParam`
- **返回值**: `SUCCESS`
- **业务逻辑**: 技术公告阅读记录

#### `listProductItem()`
- **签名**: `public String listProductItem()`
- **参数**: `commonMap`、`displayParam`
- **返回值**: `"list"` / `SUCCESS`
- **业务逻辑**: 查询产品组件列表

#### `listProbProduct()`
- **签名**: `public String listProbProduct()`
- **参数**: `probProduct`、`displayParam`
- **返回值**: `"list"` / `SUCCESS`
- **业务逻辑**: 查询产品型号列表

#### `inputProbProduct()`
- **签名**: `public String inputProbProduct()`
- **参数**: `probProduct`
- **返回值**: `INPUT` / `ERROR`
- **业务逻辑**: 新建/编辑产品型号

#### `saveProbProduct()`
- **签名**: `public String saveProbProduct()`
- **参数**: `probProduct`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 保存产品型号

#### `importProbProduct()`
- **签名**: `public String importProbProduct()`
- **参数**: `upload`、`uploadFileName`
- **返回值**: `"import"` / `ERROR`
- **业务逻辑**: 上传Excel批量导入产品型号

#### `listComponent()`
- **签名**: `public String listComponent()`
- **参数**: `productComponent`、`displayParam`
- **返回值**: `"list"` / `SUCCESS`
- **业务逻辑**: 查询产品组件列表

#### `inputComponent()`
- **签名**: `public String inputComponent()`
- **参数**: `productComponent`
- **返回值**: `INPUT` / `ERROR`
- **业务逻辑**: 新建/编辑产品组件

#### `saveComponent()`
- **签名**: `public String saveComponent()`
- **参数**: `productComponent`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 保存产品组件

#### `importComponent()`
- **签名**: `public String importComponent()`
- **参数**: `upload`、`uploadFileName`
- **返回值**: `"import"` / `ERROR`
- **业务逻辑**: 上传Excel批量导入产品组件

---

## 21. SubcontractAction — 外包项目管理

- **包路径**: `com.dp.plat.subcontract.action`
- **父类**: `BaseAction`
- **实现接口**: `Preparable`
- **Spring Bean**: `SubcontractAction`（scope=prototype）
- **依赖服务**: `SubcontractService`, `BasicDataService`, `DepartmentManageService`, `PmClosedLoopQuesnaireService`, `PmClosedLoopService`, `CallBackService`
- **职责**: 外包项目管理，包括项目创建、审批流程、交付件、付款、回访问卷

### 方法

#### `prepare()`
- **签名**: `public void prepare() throws Exception`
- **业务逻辑**: 初始化用户信息，加载下拉列表

#### `list()`
- **签名**: `public String list()`
- **参数**: `subcontract`、`displayParam`
- **返回值**: `"list"`
- **业务逻辑**: 外包项目列表查询

#### `input()`
- **签名**: `public String input()`
- **参数**: `subcontract`
- **返回值**: `INPUT`
- **业务逻辑**: 进入创建/编辑页面

#### `save()`
- **签名**: `public String save()`
- **参数**: `subcontract`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 保存外包项目信息

#### `update()`
- **签名**: `public String update()`
- **参数**: `subcontract`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 更新外包项目信息

#### `delete()`
- **签名**: `public String delete()`
- **参数**: `subcontract.subcontractId`
- **返回值**: `SUCCESS`
- **业务逻辑**: 删除外包项目

#### `view()`
- **签名**: `public String view()`
- **参数**: `subcontract`
- **返回值**: `"view"`
- **业务逻辑**: 查看外包项目详情

#### `paymentList()`
- **签名**: `public String paymentList()`
- **参数**: `subcontractPayment`、`displayParam`
- **返回值**: `"paymentList"`
- **业务逻辑**: 付款记录列表查询

#### `inputPayment()`
- **签名**: `public String inputPayment()`
- **参数**: `subcontractPayment`
- **返回值**: `INPUT`
- **业务逻辑**: 进入创建/编辑付款页面

#### `savePayment()`
- **签名**: `public String savePayment()`
- **参数**: `subcontractPayment`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 保存付款信息

#### `deletePayment()`
- **签名**: `public String deletePayment()`
- **参数**: `subcontractPayment.paymentId`
- **返回值**: `SUCCESS`
- **业务逻辑**: 删除付款记录

#### `evalList()`
- **签名**: `public String evalList()`
- **参数**: `subcontractEval`、`displayParam`
- **返回值**: `"evalList"`
- **业务逻辑**: 评价列表查询

#### `inputEval()`
- **签名**: `public String inputEval()`
- **参数**: `subcontractEval`
- **返回值**: `INPUT`
- **业务逻辑**: 进入创建/编辑评价页面

#### `saveEval()`
- **签名**: `public String saveEval()`
- **参数**: `subcontractEval`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 保存评价信息

#### `acceptanceList()`
- **签名**: `public String acceptanceList()`
- **参数**: `subcontractAcceptance`、`displayParam`
- **返回值**: `"acceptanceList"`
- **业务逻辑**: 验收记录列表查询

#### `inputAcceptance()`
- **签名**: `public String inputAcceptance()`
- **参数**: `subcontractAcceptance`
- **返回值**: `INPUT`
- **业务逻辑**: 进入创建/编辑验收页面

#### `saveAcceptance()`
- **签名**: `public String saveAcceptance()`
- **参数**: `subcontractAcceptance`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 保存验收信息

#### `export()`
- **签名**: `public String export()`
- **参数**: `subcontract`、`displayParam`
- **返回值**: `null`（直接输出Excel流）/ `ERROR`
- **业务逻辑**: 导出外包项目为Excel文件

---

## 22. MaintenanceAction — 项目维护

- **包路径**: `com.dp.plat.maintenance.action`
- **父类**: `BaseAction`
- **实现接口**: `Preparable`
- **Spring Bean**: `MaintenanceAction`（scope=prototype）
- **依赖服务**: `ProjectService`, `PresalesService`, `DepartmentManageService`, `BasicDataService`
- **职责**: 项目维护记录管理

### 方法

#### `prepare()`
- **签名**: `public void prepare() throws Exception`
- **业务逻辑**: 初始化公司、办事处、维护类型、项目分类下拉列表

#### `execute()`
- **签名**: `public String execute() throws Exception`
- **参数**: `project`、`displayParam`
- **返回值**: `SUCCESS`
- **业务逻辑**: 项目维护列表查询

#### `input()`
- **签名**: `public String input()`
- **参数**: `project`
- **返回值**: `INPUT`
- **业务逻辑**: 进入维护记录创建/编辑页面

#### `save()`
- **签名**: `public String save()`
- **参数**: `project`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 保存维护记录

#### `update()`
- **签名**: `public String update()`
- **参数**: `project`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 更新维护记录

#### `delete()`
- **签名**: `public String delete()`
- **参数**: `project.projectId`
- **返回值**: `SUCCESS`
- **业务逻辑**: 删除维护记录

#### `view()`
- **签名**: `public String view()`
- **参数**: `project`
- **返回值**: `"view"`
- **业务逻辑**: 查看维护记录详情

#### `export()`
- **签名**: `public String export()`
- **参数**: `project`、`displayParam`
- **返回值**: `null`（直接输出Excel流）/ `ERROR`
- **业务逻辑**: 导出维护记录为Excel文件

---

## 23. SupervisionAction — 项目督查

- **包路径**: `com.dp.plat.supervision.action`
- **父类**: `BaseAction`
- **实现接口**: `Preparable`
- **Spring Bean**: `SupervisionAction`（scope=prototype）
- **依赖服务**: `ProjectService`, `UserManageService`, `DepartmentManageService`, `BasicDataService`
- **职责**: 项目督查记录管理

### 方法

#### `prepare()`
- **签名**: `public void prepare() throws Exception`
- **业务逻辑**: 初始化办事处列表

#### `execute()`
- **签名**: `public String execute() throws Exception`
- **参数**: `projectSupervision`、`displayParam`
- **返回值**: `SUCCESS`
- **业务逻辑**: 项目督查列表查询

#### `input()`
- **签名**: `public String input()`
- **参数**: `projectSupervision`
- **返回值**: `INPUT`
- **业务逻辑**: 进入督查记录创建/编辑页面

#### `save()`
- **签名**: `public String save()`
- **参数**: `projectSupervision`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 保存督查记录

#### `update()`
- **签名**: `public String update()`
- **参数**: `projectSupervision`
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**: 更新督查记录

#### `delete()`
- **签名**: `public String delete()`
- **参数**: `projectSupervision.supervisionId`
- **返回值**: `SUCCESS`
- **业务逻辑**: 删除督查记录

#### `view()`
- **签名**: `public String view()`
- **参数**: `projectSupervision`
- **返回值**: `"view"`
- **业务逻辑**: 查看督查记录详情

#### `export()`
- **签名**: `public String export()`
- **参数**: `projectSupervision`、`displayParam`
- **返回值**: `null`（直接输出Excel流）/ `ERROR`
- **业务逻辑**: 导出督查记录为Excel文件

---

## 24. CertificateAction — 合格证查询

- **包路径**: `com.dp.plat.plus.certificate.action`
- **父类**: `BaseAction`
- **Spring Bean**: `Certificate`（scope=prototype，⚠️ bean id 为 `Certificate` 而非类名 `CertificateAction`）
- **依赖服务**: `CertificateService`
- **职责**: 设备合格证查询、印章信息上传

### 方法

#### `certificate()`
- **签名**: `public String certificate()`
- **返回值**: `SUCCESS`
- **业务逻辑**: 合格证查询主页，设置上传权限标记

#### `queryCertificate()`
- **签名**: `public String queryCertificate()`
- **参数**: `barcode`
- **返回值**: `SUCCESS`
- **业务逻辑**:
  1. 根据序列号查询 OQC 检验信息
  2. 从 info 字段中提取 OQC 编号
  3. 根据序列号生成生产日期
  4. 返回 OQC 编号和生产日期
- **异常处理**: 未找到时设置错误消息

#### `uploadSealInfo()`
- **签名**: `public String uploadSealInfo()`
- **参数**: `upload`、`uploadFileName`、`sealInfo`（JSON字符串）
- **返回值**: `SUCCESS` / `ERROR`
- **业务逻辑**:
  1. 解析 sealInfo JSON 获取印章信息（oqcNo、sealName、sealCode）
  2. 上传文件到服务器
  3. 调用 `certificateService.saveSealInfo()` 保存印章信息
  4. 返回成功/失败消息
- **异常处理**: 文件上传失败或 JSON 解析失败时返回 ERROR

#### `generateProductionDate(String)` (static)
- **签名**: `public static String generateProductionDate(String barCode)`
- **参数**: `barCode` — 设备序列号
- **返回值**: `String` — 生产日期（yyyy-MM 格式）或 null
- **业务逻辑**: 从序列号第10-12位解析年份和月份（16进制），格式化为日期
- **异常处理**: `ParseException` 打印堆栈

---

## 25. WarrantyCallbackAction — 维保回访

- **包路径**: `com.dp.plat.warrantyCallback.action`
- **父类**: `BaseAction`
- **实现接口**: `Preparable`
- **Spring Bean**: `WarrantyCallbackAction`（scope=prototype）
- **依赖服务**: `ProjectService`, `UserManageService`, `DepartmentManageService`, `BasicDataService`, `WarrantyCallbackService`
- **职责**: 项目维保回访记录管理

### 方法

#### `prepare()`
- **签名**: `public void prepare() throws Exception`
- **业务逻辑**: 获取当前 Action 的 namespace，用于页面跳转

> 注：WarrantyCallbackAction 结构与 SupervisionAction/MaintenanceAction 类似，包含维保回访记录的列表查询、创建、问卷填写、交付件上传等功能。

---

## 26. ProjectBaseAction — 项目管理 Action 基类

- **源码**: `PMS-struts/src/com/dp/plat/action/ProjectBaseAction.java`
- **包路径**: `com.dp.plat.action`
- **父类**: `BaseAction`
- **修饰符**: `public abstract`（抽象类，不可直接实例化）
- **Spring Bean**: 无（作为基类被继承，由子类的 Spring Bean 定义触发注入）
- **职责**: 为项目管理相关 Action 提供公共的 Service 引用、页面数据 List 和基础初始化方法，消除子类重复代码

### 核心属性

#### Service 引用

| 属性名 | 类型 | 说明 |
|--------|------|------|
| `projectService` | `ProjectService` | 项目业务服务 |
| `departmentManageService` | `DepartmentManageService` | 部门/公司管理服务 |
| `basicDataService` | `BasicDataService` | 基础数据服务（下拉列表数据源）|

#### 页面参数

| 属性名 | 类型 | 说明 |
|--------|------|------|
| `displayParam` | `DisplayParam` | 分页/查询参数 |
| `project` | `Project` | 当前项目对象 |
| `projectId` | `int` | 项目 ID |
| `contractNo` | `String` | 合同号 |
| `result` | `int` | 操作结果码 |
| `message` | `String` | 操作消息 |
| `redirect` | `String` | 重定向 URL |

#### 页面数据 List（12 个下拉列表）

| 属性名 | 类型 | 数据来源 |
|--------|------|----------|
| `departmentList` | `List<Department>` | `departmentManageService.queryDepartments()` |
| `companyList` | `List<Company>` | `departmentManageService.queryCompanyList(company)` |
| `projectTypeList` | `List<BasicDataBean>` | `basicDataService.queryBasicDataBeans("02")` |
| `projectRankList` | `List<BasicDataBean>` | `basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_PRORANK)` |
| `deliverStateList` | `List<BasicDataBean>` | `basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_DELIVERSTATE)` |
| `projectPlanStateList` | `List<BasicDataBean>` | `basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_ENGINEERSTATE)` |
| `projectExecutionStateList` | `List<BasicDataBean>` | `basicDataService.queryBasicDataBeans("projectExecutionState")` |
| `projectCloseProcessStateList` | `List<BasicDataBean>` | `basicDataService.queryBasicDataBeans("projectCloseProcessState")` |
| `projectTimeList` | `List<BasicDataBean>` | `basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_PORJECT_TIME)` |
| `ssfsList` | `List<BasicDataBean>` | `basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_SERVICE_TYPE)` |
| `majorProjectLevelList` | `List<BasicDataBean>` | `basicDataService.queryBasicDataBeans("majorProjectLevel")` |
| `navTabList` | `List<BasicDataBean>` | 无初始化逻辑（由子类设置）|

### 方法

#### `initProject()` (protected)
- **签名**: `protected void initProject()`
- **参数**: 无
- **返回值**: 无
- **业务逻辑**:
  1. 若 `project == null`，创建新 `Project` 实例
  2. 若 `displayParam == null`，创建新 `DisplayParam` 实例
- **用途**: 子类在 `execute()` 等方法开头调用，确保 project 和 displayParam 已初始化
- **异常处理**: 无

#### `prepareCommonData()` (protected)
- **签名**: `protected void prepareCommonData()`
- **参数**: 无
- **返回值**: 无
- **业务逻辑**:
  1. 查询部门列表：`departmentManageService.queryDepartments()`
  2. 查询公司列表（status=1）：`departmentManageService.queryCompanyList(company)`
  3. 批量查询 10 个基础数据列表（通过 `basicDataService.queryBasicDataBeans(code)`）：
     - `"02"` → projectTypeList
     - `MessageUtil.BASIC_DATA_DELIVERSTATE` → deliverStateList
     - `MessageUtil.BASIC_DATA_ENGINEERSTATE` → projectPlanStateList
     - `"projectExecutionState"` → projectExecutionStateList
     - `"projectCloseProcessState"` → projectCloseProcessStateList
     - `MessageUtil.BASIC_DATA_PRORANK` → projectRankList
     - `"majorProjectLevel"` → majorProjectLevelList
     - `MessageUtil.BASIC_DATA_PORJECT_TIME` → projectTimeList
     - `MessageUtil.BASIC_DATA_SERVICE_TYPE` → ssfsList
- **用途**: 子类在进入编辑/查看页面前调用，加载所有下拉列表数据
- **异常处理**: 无

### Setter 方法（Spring 依赖注入）

```java
public void setProjectService(ProjectService projectService) {
    this.projectService = projectService;
}

public void setDepartmentManageService(DepartmentManageService departmentManageService) {
    this.departmentManageService = departmentManageService;
}

public void setBasicDataService(BasicDataService basicDataService) {
    this.basicDataService = basicDataService;
}

public void setDisplayParam(DisplayParam displayParam) {
    this.displayParam = displayParam;
}

public void setProject(Project project) {
    this.project = project;
}
```

### 已知子类

ProjectBaseAction 有 5 个子类，均位于 `com.dp.plat.action` 包下：

| 子类 | 职责 | Spring Bean |
|------|------|-------------|
| `ProjectFileAction` | 项目交付件管理 | `ProjectFileAction`（scope=prototype）|
| `ProjectContractAction` | 项目合同管理 | `ProjectContractAction`（scope=prototype）|
| `ProjectNotificationAction` | 项目通知管理 | `ProjectNotificationAction`（scope=prototype）|
| `ProjectWeeklyAction` | 项目周报管理 | `ProjectWeeklyAction`（scope=prototype）|
| `ProjectMemberAction` | 项目成员管理 | `ProjectMemberAction`（scope=prototype）|

### 子类使用模式

```java
public class ProjectFileAction extends ProjectBaseAction {
    
    // 子类可继承父类的 projectService, departmentManageService, basicDataService
    
    public String execute() throws Exception {
        initProject();           // 调用父类初始化方法
        prepareCommonData();     // 加载下拉列表
        // 子类业务逻辑...
        return SUCCESS;
    }
    
    // 子类可通过 getProject(), getDisplayParam() 等访问父类属性
}
```

### ⚠️ 注意事项

1. **navTabList 无初始化**: `navTabList` 在 `prepareCommonData()` 中未被赋值，需子类自行设置
2. **基础数据编码混合**: 部分编码使用 `MessageUtil` 常量（如 `BASIC_DATA_DELIVERSTATE`），部分使用字符串字面量（如 `"projectExecutionState"`），风格不统一
3. **公司列表过滤**: `companyList` 仅查询 `status=1` 的公司（有效公司）
4. **抽象类设计**: ProjectBaseAction 作为抽象类，强制子类继承，避免直接实例化未初始化的基类

---

## 附录：通用模式总结

### 权限校验模式

项目中权限校验主要有以下几种方式：

1. **Preparable 接口**: 在 `prepare()` 方法中进行全局权限校验（如 `UserManageAction`）
2. **方法内校验**: 在具体方法中检查角色（如 `ProjectAction.updateProject()`）
3. **`getUserPower()` 方法**: `PmClosedLoopAction` 提供的通用权限判断方法

### 错误处理模式

1. **`setErrmsg(String)`**: 设置简单错误消息
2. **`setErrmsg(BaseService)`**: 从 Service 层提取错误消息
3. **`ExceptionUtils.getStackTrace(e)`**: 将完整堆栈设置为错误消息
4. **`result` 字段**: Ajax 请求返回操作结果码

### 返回值约定

| 返回值 | 含义 |
|--------|------|
| `SUCCESS` | 操作成功，跳转到成功页面 |
| `INPUT` | 进入输入/编辑表单页面 |
| `ERROR` | 操作失败或权限不足 |
| `"list"` | 列表页面 |
| `"read"` | 只读查看页面 |
| `"redirect"` | 重定向到其他页面 |
| `"seeScore"` | 查看问卷评分结果 |
| `"errorCas"` | CAS 登录错误 |
| `"invalid"` | 项目作废 |

### 依赖注入模式

所有 Action 类通过 Spring 的 Setter 注入获取 Service 依赖，Struts2 的 Spring 插件自动完成注入。典型模式：

```java
public void setProjectService(ProjectService projectService) {
    this.projectService = projectService;
}
```

### 分页查询模式

使用 `DisplayParam` 对象封装分页参数，调用 `displayParam.getParam()` 解析请求参数，Service 层返回分页结果。
