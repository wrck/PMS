# PMS Service 方法参考手册

> 本文档深度分析PMS项目所有核心Service类的完整签名、核心算法、事务逻辑和异常处理机制。
>
> 事务代理配置来源：`applicationContext.xml` 中的 `transactionBaseService`（`TransactionProxyFactoryBean`）
>
> **事务方法前缀规则**（PROPAGATION_REQUIRED）：
> `insert*` | `update*` | `delete*` | `add*` | `save*` | `do*` | `keep*` | `start*` | `submit*` | `parse*`
>
> **非事务方法**：`query*` | `find*` | `get*` | `check*` | `select*` | `back*` | `edit*` | 其他未匹配前缀

---

## 目录

1. [BaseServiceImpl（基类）](#1-baseserviceimpl基类)
2. [LoginServiceImpl](#2-loginserviceimpl)
3. [UserManageServiceImpl](#3-usermanageserviceimpl)
4. [RoleManageServiceImpl](#4-rolemanageserviceimpl)
5. [DepartmentManageServiceImpl](#5-departmentmanageserviceimpl)
6. [BasicDataServiceImpl](#6-basicdataserviceimpl)
7. [OpLogServiceImpl](#7-oplogserviceimpl)
8. [PasswordServiceImpl](#8-passwordserviceimpl)
9. [ProjectServiceImpl](#9-projectserviceimpl)
10. [ProjectPlanServiceImpl](#10-projectplanserviceimpl)
11. [PresalesServiceImpl](#11-presalesserviceimpl)
12. [CallBackServiceImpl](#12-callbackserviceimpl)
13. [PmClosedLoopServiceImpl](#13-pmclosedloopserviceimpl)
14. [PmClosedLoopQuesnaireServiceImpl](#14-pmclosedloopquesnaireserviceimpl)
15. [ReportServiceImpl](#15-reportserviceimpl)
16. [DataAnalysisServiceImpl](#16-dataanalysisserviceimpl)
17. [WorkFlowServiceImpl](#17-workflowserviceimpl)
18. [WorkSpaceServiceImpl](#18-workspaceserviceimpl)
19. [SendMailServiceImpl](#19-sendmailserviceimpl)
20. [ProbManageServiceImpl](#20-probmanageserviceimpl)
21. [SubcontractServiceImpl](#21-subcontractserviceimpl)
22. [CertificateServiceImpl](#22-certificateserviceimpl)
23. [WarrantyCallbackServiceImpl](#23-warrantycallbackserviceimpl)

---

## 1. BaseServiceImpl（基类）

### 类概述
- 实现接口：`BaseService`
- 事务代理Bean：无（抽象基类，不直接代理）
- 依赖的DAO：无
- 事务方法前缀：不适用

### 方法列表

#### `void log(String action)`
- **功能描述**：记录操作日志，将操作描述设置到UserContext中
- **事务类型**：无事务
- **输入参数**：
  | 参数名 | 类型 | 业务含义 | 校验规则 |
  |--------|------|----------|----------|
  | action | String | 操作描述 | 无 |
- **返回值**：void
- **核心算法/处理逻辑**：
  1. 调用 `userContext.setOption(action)` 设置操作描述
  2. 异常被静默吞掉（catch块为空）
- **调用的DAO方法**：无
- **调用的其他Service方法**：无
- **异常处理**：
  | 异常类型 | 触发条件 | 处理方式 |
  |----------|----------|----------|
  | Exception | userContext为null | 静默吞掉 |
- **注意事项**：日志记录失败不影响业务流程

#### `String getLoginName()`
- **功能描述**：获取当前登录用户名
- **事务类型**：无事务
- **返回值**：String - 当前登录用户名，异常时返回null

#### `String getRealname()`
- **功能描述**：获取当前登录用户真实姓名
- **事务类型**：无事务
- **返回值**：String - 真实姓名，异常时返回null

#### `void addErrmsg(String errmsg)`
- **功能描述**：添加错误消息
- **事务类型**：无事务
- **核心算法/处理逻辑**：设置error标志为true，将错误消息添加到列表

#### `void addWarnmsg(String warnmsg)`
- **功能描述**：添加警告消息
- **事务类型**：无事务
- **核心算法/处理逻辑**：设置warn标志为true，将警告消息添加到列表

#### `boolean isError()` / `boolean isWarn()`
- **功能描述**：检查是否存在错误/警告
- **返回值**：boolean

#### `void clearErrMsg()`
- **功能描述**：清除所有错误和警告消息，重置标志位

#### `boolean checkPass(String checkpass)`
- **功能描述**：密码校验（当前实现直接返回true，未实际校验）
- **注意事项**：遗留代码，未实现真正的校验逻辑

#### `Integer checkJobnum(String jobnum)`
- **功能描述**：工号校验（当前实现直接返回0）
- **注意事项**：遗留代码，未实现

---

## 2. LoginServiceImpl

### 类概述
- 实现接口：`LoginService`
- 事务代理Bean：`loginServiceAgent`
- 依赖的DAO：`LoginDao`
- 事务方法前缀：insert*/update*/delete*/add*/save*/do*/start*/submit*/keep*/parse*

### 方法列表

#### `boolean login(LoginParam loginParam, String ip)`
- **功能描述**：用户登录认证，构建用户上下文和权限映射
- **事务类型**：无事务（query*前缀不匹配事务规则）
- **输入参数**：
  | 参数名 | 类型 | 业务含义 | 校验规则 |
  |--------|------|----------|----------|
  | loginParam | LoginParam | 登录参数（用户名、密码、验证码） | 非空 |
  | ip | String | 客户端IP地址 | 非空 |
- **返回值**：boolean - 登录是否成功
- **核心算法/处理逻辑**：
  1. 查询系统环境参数 `sys.envirment.argu`，判断是否为生产环境（"1"为生产环境需验证码）
  2. 生产环境下验证码校验：从Session获取`rand`属性与用户输入比对
  3. 查询用户信息 `loginDao.querUser(username)`
  4. 测试环境（非"1"）忽略密码校验，直接使用数据库密码
  5. 密码比对：`user.getPassword().equalsIgnoreCase(pwd)`（忽略大小写）
  6. **权限映射构建算法**（O(n*m)，n=角色数，m=菜单权限数）：
     - 遍历用户角色ID列表（逗号分隔，格式如`;1;,;2;`）
     - 对每个角色查询 `RoleMenuPower` 列表
     - 解析菜单权限编码：8=增加, 1=删除, 4=查找, 2=更新
     - 构建 `Map<Integer, Map<String, Integer>>` 权限映射（菜单ID → 操作权限）
     - 缺省权限补0
  7. **区域权限处理**：调用 `processAreaPower()` 补充市场和用服相同办事处权限
  8. 查询用户默认页面
  9. 调用 `getUserContext().login()` 构建用户上下文
  10. 查询功能菜单项名称映射，存入extData
  11. 记录登录日志
- **调用的DAO方法**：`loginDao.querySysArg()`, `loginDao.querUser()`, `loginDao.queryUserMenuMap()`, `loginDao.queryRoleMenuPowerList()`, `loginDao.queryUserDefaultPage()`, `loginDao.queryUserMenuNameMap()`
- **调用的其他Service方法**：`getUserContext().login()`
- **异常处理**：
  | 异常类型 | 触发条件 | 处理方式 |
  |----------|----------|----------|
  | Exception | 任何登录过程异常 | e.printStackTrace() + addErrmsg("Login Error!") + return false |
- **数据校验机制**：验证码校验（生产环境）、密码校验
- **注意事项**：角色ID格式为`;1;`（分号包裹），解析时需substring去掉首尾分号

#### `boolean loginCas(LoginParam loginParam, String ip)`
- **功能描述**：CAS单点登录认证（无需密码校验）
- **事务类型**：无事务
- **核心算法/处理逻辑**：
  1. 查询用户信息，若用户不存在直接返回false
  2. 区域权限处理（同login方法）
  3. 构建权限映射（同login方法）
  4. 构建用户上下文并记录日志
- **与login方法的差异**：无验证码校验、无密码校验、用户不存在直接返回false

#### `void logout()`
- **功能描述**：用户登出，销毁Session
- **事务类型**：无事务
- **核心算法/处理逻辑**：调用 `HttpContext.invalidateSession()` 销毁当前会话
- **异常处理**：Exception → e.printStackTrace()，静默处理

#### `String querySysArg(String code)`
- **功能描述**：查询系统参数
- **事务类型**：无事务
- **返回值**：String - 系统参数值

#### `private String processAreaPower(String areaPower)`
- **功能描述**：处理区域权限，补充市场和用服相同办事处权限
- **核心算法**：委托给 `UserUtil.processAreaPower()` 处理

---

## 3. UserManageServiceImpl

### 类概述
- 实现接口：`UserManageService`
- 事务代理Bean：`userManageServiceAgent`
- 依赖的DAO：`UserManageDao`

### 方法列表

#### `List<User> queryUserList(DisplayParam displayParam, User user)`
- **功能描述**：查询用户列表
- **事务类型**：无事务
- **调用的DAO方法**：`userManageDao.queryUserList()`

#### `User queryUserByUserName(String username)`
- **功能描述**：根据用户名查询用户
- **事务类型**：无事务

#### `List<User> queryUsersByUserNames(String usernames)`
- **功能描述**：根据多个用户名查询用户列表
- **事务类型**：无事务

#### `void updatepwdbyusername(String md5pwd, String username)`
- **功能描述**：根据用户名更新密码
- **事务类型**：REQUIRED（update*前缀）

#### `void updatepwdbyuser(User user)`
- **功能描述**：根据用户对象更新密码
- **事务类型**：REQUIRED（update*前缀）

#### `List<Role> queryRolelist()`
- **功能描述**：查询角色列表
- **事务类型**：无事务

#### `List<UserMenu> queryAllMenuList()` / `List<UserMenu> queryUserMenuList()`
- **功能描述**：查询所有菜单/用户菜单列表
- **事务类型**：无事务

#### `User queryUserByUserId(int id)`
- **功能描述**：根据用户ID查询用户
- **事务类型**：无事务

#### `String queryUserMenuidsByUserid(int id)`
- **功能描述**：查询用户菜单ID列表
- **事务类型**：无事务

#### `void addUserInfo(User user, String usermenuids)`
- **功能描述**：新增用户信息
- **事务类型**：REQUIRED（add*前缀）
- **核心算法/处理逻辑**：
  1. 记录操作日志
  2. 处理角色ID格式：调用 `dealWith()` 将 `1,2` 转为 `;1;,;2;`
  3. 调用DAO插入用户信息和菜单关联
- **调用的DAO方法**：`userManageDao.addUserInfo()`

#### `void updateUserInfo(User user, String usermenuids)`
- **功能描述**：更新用户信息（含菜单权限和区域权限更新）
- **事务类型**：REQUIRED（update*前缀）
- **核心算法/处理逻辑**：
  1. 校验默认登录页面路径是否有效，无效抛出RuntimeException
  2. 处理角色ID格式
  3. 更新用户基本信息
  4. 删除原有用户菜单关联，重新插入新的菜单关联
  5. 更新区域权限：合并用户区域权限和部门编号，调用 `UserUtil.processAreaPower()` 处理
  6. 根据是否已有权限记录选择update或insert
- **调用的DAO方法**：`userManageDao.queryUserMenu()`, `userManageDao.updateUser()`, `userManageDao.deleteUsermenu()`, `userManageDao.insertUsermenu()`, `userManageDao.updateUserPower()`, `userManageDao.insertUserpower()`
- **异常处理**：
  | 异常类型 | 触发条件 | 处理方式 |
  |----------|----------|----------|
  | RuntimeException | 默认登录页面路径为空 | 直接抛出 |

#### `private String dealWith(String roleids)`
- **功能描述**：角色ID格式转换，将 `1,2,3` 转为 `;1;,;2;,;3;`
- **核心算法**：O(n)遍历，逗号分隔后每个角色ID前后加分号

#### `List<User> queryAllUser()` / `List<User> queryAllUserList(User user)` / `Map<String, User> queryAllUserMap()`
- **功能描述**：查询全部用户（列表/带条件列表/Map形式）
- **事务类型**：无事务

#### `int queryUserSizeByUserName(String username)`
- **功能描述**：查询指定用户名的用户数量
- **事务类型**：无事务

#### `List<User> queryUserWithRoleId(int roleid)` / `List<User> queryUserWithRoleIdAndDpNo(Map)` / `List<User> queryUserWithRoleIdAndDpNoOrInAreaPower(Map)`
- **功能描述**：根据角色ID/办事处编号查询用户列表
- **事务类型**：无事务

#### `String updateServiceAndProgramMember(ProjectBatchCgMbParam batchCgMb)`
- **功能描述**：批量更新服务和项目经理成员
- **事务类型**：REQUIRED（update*前缀）

#### `String queryMailsByRoleAndOfficeCodes(String officeCodes, Integer roleId)`
- **功能描述**：根据角色和办事处编码查询邮件地址
- **事务类型**：无事务

---

## 4. RoleManageServiceImpl

### 类概述
- 实现接口：`RoleManageService`
- 事务代理Bean：`roleManageServiceAgent`
- 依赖的DAO：`RoleManageDao`

### 方法列表

#### `List<Role> queryRoleList(DisplayParam displayParam, Role role)`
- **功能描述**：查询角色列表
- **事务类型**：无事务

#### `int addRoleSubmit(Role role, List<RoleMenuPower> rolemenuidList)`
- **功能描述**：新增角色及其菜单权限
- **事务类型**：REQUIRED（add*前缀）
- **调用的DAO方法**：`roleManageDao.addRoleSubmit()`

#### `int updateRoleSubmit(Role role, List<RoleMenuPower> rolemenuidList)`
- **功能描述**：更新角色及其菜单权限
- **事务类型**：REQUIRED（update*前缀）
- **调用的DAO方法**：`roleManageDao.updateRoleSubmit()`

#### `List<RoleMenuPower> queryRoleMenuPowerList(Role role)`
- **功能描述**：查询角色菜单权限列表
- **事务类型**：无事务

---

## 5. DepartmentManageServiceImpl

### 类概述
- 实现接口：`DepartmentManageService`
- 事务代理Bean：`departmentManageServiceAgent`
- 依赖的DAO：`DepartmentManageDao`

### 方法列表

#### `List<Department> queryDepartmentList(DisplayParam displayParam, Department department)`
- **功能描述**：查询部门列表
- **事务类型**：无事务

#### `int addDepartmentSubmit(Department department)`
- **功能描述**：新增部门
- **事务类型**：REQUIRED（add*前缀）

#### `void refreshDepartment()`
- **功能描述**：刷新部门数据缓存
- **事务类型**：无事务（refresh*不匹配事务前缀）

#### `List<Department> queryAllDepartments(Department department)`
- **功能描述**：查询所有部门

#### `List<Department> queryDepartments()`
- **功能描述**：查询参数化部门列表（isparam=1）
- **核心算法**：创建Department对象设置isparam=1，委托给queryAllDepartments

#### `Map<String, String> queryDepartmentMap()`
- **功能描述**：查询部门Map映射

#### `Department queryDepartmentByDepartmentNum(String officeCode)`
- **功能描述**：根据办事处编码查询部门

#### `List<Company> queryCompanyList(Company company)` / `Company queryCompanyOne(Company company)`
- **功能描述**：查询公司列表/单个公司

---

## 6. BasicDataServiceImpl

### 类概述
- 实现接口：`BasicDataService`
- 事务代理Bean：`basicDataServiceAgent`
- 依赖的DAO：`BasicDataDao`

### 方法列表

#### `List<BasicDataBean> queryBasicDataBeans(String basicDataType)`
- **功能描述**：根据类型查询基础数据列表
- **事务类型**：无事务

#### `List<BasicDataBean> queryBasicDataType()`
- **功能描述**：查询基础数据类型列表

#### `BasicDataBean queryBasicDataBean(int id)`
- **功能描述**：根据ID查询基础数据

#### `List<BasicDataBean> queryBasicDataBeanAll(String basicDataType)`
- **功能描述**：查询某类型全部基础数据（含无效数据）

#### `void updateBasicData(BasicDataBean basicData)`
- **功能描述**：更新基础数据
- **事务类型**：REQUIRED（update*前缀）

#### `void insertBasicDataBean(BasicDataBean basicData)`
- **功能描述**：新增基础数据
- **事务类型**：REQUIRED（insert*前缀）
- **核心算法/处理逻辑**：自动设置创建人和创建时间

#### `int findBasicDataId(Map<String, Object> paramMap)`
- **功能描述**：根据条件查找基础数据ID

#### `String querySysArg(String code)`
- **功能描述**：查询系统参数

#### `void executeSql(String executeSql)`
- **功能描述**：执行自定义SQL
- **事务类型**：无事务（execute*不匹配事务前缀）
- **注意事项**：⚠️ 存在SQL注入风险

#### `String insertFileInfo(String path, String uploadFileName)`
- **功能描述**：插入文件信息，返回文件ID列表
- **事务类型**：REQUIRED（insert*前缀）
- **核心算法/处理逻辑**：
  1. 逗号分隔文件名列表
  2. 遍历每个文件名，构建参数Map（fileName, filePath, uploadBy, uploadTime）
  3. 调用DAO插入，收集生成的ID
  4. 拼接ID列表返回（逗号分隔）
- **返回值**：String - 文件ID列表，如 "1,2,3"

#### `String insertFileInfo(String path, String uploadFileName, String uploadFileType)`
- **功能描述**：插入文件信息（含文件类型）
- **事务类型**：REQUIRED（insert*前缀）
- **核心算法/处理逻辑**：与两参数版本类似，额外处理文件类型数组，当文件类型数量与文件名数量一致时设置类型

#### `FileParam queryFileInfo(int fileId)`
- **功能描述**：查询文件信息

#### `Map<Integer, String> queryFileMap(String fileIds)`
- **功能描述**：根据文件ID列表查询文件映射
- **数据校验**：fileIds为null时返回null

#### `List<FileParam> queryFileList(String confirmFileIds)`
- **功能描述**：查询文件列表

#### `Map<String, String> queryBasicDataBeanMap(String dataTypeCode)`
- **功能描述**：查询基础数据Map映射

#### `String queryBasicDataNameById(String basicDataId)` / `BasicDataBean queryBasicDataBeanByDataId(String basicDataId)`
- **功能描述**：根据数据ID查询基础数据名称/对象

#### `void deleteFile(int fileId)`
- **功能描述**：删除文件
- **事务类型**：REQUIRED（delete*前缀）

#### `List<BasicDataBean> queryBasicDataBeanByAttri(String dataType, String attri1)`
- **功能描述**：根据类型和属性1查询基础数据

#### `List<Map<String, Object>> queryBasicDataBeanMapWithSub(String dataTypeCode, String subDataTypeCode, Map extra)`
- **功能描述**：查询基础数据Map（含子类型）

#### `boolean refreshCacheData()`
- **功能描述**：刷新缓存数据
- **事务类型**：无事务

---

## 7. OpLogServiceImpl

### 类概述
- 实现接口：`OpLogService`
- 事务代理Bean：`opLogServiceAgent`
- 依赖的DAO：`OpLogDao`

### 方法列表

#### `void insertLog()`
- **功能描述**：插入操作日志
- **事务类型**：REQUIRED（insert*前缀）

#### `List<OperateLog> queryLogList(DisplayParam displayParam)`
- **功能描述**：查询日志列表

#### `void delete(ArrayList<String> selected)`
- **功能描述**：批量删除日志
- **事务类型**：REQUIRED（delete*前缀）

#### `List<OperateLog> queryLogAllList(DisplayParam displayParam)`
- **功能描述**：查询全部日志列表

---

## 8. PasswordServiceImpl

### 类概述
- 实现接口：`PasswordService`
- 事务代理Bean：`passwordServiceAgent`
- 依赖的DAO：`PasswordDao`
- 跨Service依赖：`LoginService`

### 方法列表

#### `boolean changelogin(PasswordEditParam passwordEditParam)`
- **功能描述**：修改密码并自动重新登录
- **事务类型**：无事务（change*不匹配事务前缀）
- **核心算法/处理逻辑**：
  1. 获取当前用户上下文和用户信息
  2. 校验旧密码：`passwordEditParam.getOldPassword().equals(user.getPassword())`
  3. 调用DAO更新密码
  4. 更新UserContext中的密码
  5. **自动重新登录流程**：
     - 当前会话登出 `userContext.logout()`
     - 调用 `loginService.logout()` 销毁Session
     - 生成验证码并设置到Session
     - 使用新密码调用 `loginService.login()` 重新登录
  6. 强制其他会话下线 `forcedOffline()`
- **调用的DAO方法**：`passwordDao.usChangelogin()`
- **调用的其他Service方法**：`loginService.logout()`, `loginService.login()`
- **异常处理**：无显式异常处理，依赖事务代理
- **注意事项**：密码修改后自动重新登录，确保Session中密码信息同步

#### `void forcedOffline(String username)`
- **功能描述**：踢指定用户的其他在线会话下线
- **事务类型**：无事务
- **核心算法/处理逻辑**：
  1. 获取全局在线用户列表 `UserContext.getOnlineList()`
  2. 遍历所有在线会话，匹配指定用户名
  3. 跳过当前Session（保留当前会话）
  4. 对其他匹配会话调用 `activeSession.logout()` 强制下线
- **性能优化策略**：使用ArrayList副本遍历，避免并发修改异常

---

## 9. ProjectServiceImpl

### 类概述
- 实现接口：`ProjectService`
- 事务代理Bean：`projectServiceAgent`
- 依赖的DAO：`ProjectDao`
- 跨Service依赖：`BasicDataService`, `CallBackService`, `PmClosedLoopService`, `TaskService(Activiti)`, `SendMailService`
- 特殊：自引用 `@Lazy @Autowired ProjectService projectService`（解决事务代理自调用问题）

### 方法列表

#### `List<Project> queryProjectList(Project project, DisplayParam displayParam)`
- **功能描述**：查询项目列表
- **事务类型**：无事务

#### `int insertProject(Project project) throws Exception`
- **功能描述**：创建项目（核心方法，涉及多表插入和状态机）
- **事务类型**：REQUIRED（insert*前缀）
- **核心算法/处理逻辑**：
  1. 根据合同号查询已有项目，合并属性 `putProperties()`
  2. **项目状态机转换**：
     - 不予跟踪（column008非空）→ `PROJECT_STATE_DENY` + `PROJECT_CREATE_STATE40`
     - 服务经理和项目经理均为空 → `PROJECT_STATE_30`（已创建）
     - 有服务经理无项目经理 → `PROJECT_STATE_31`（待指派项目经理）
     - 均不为空 → `PROJECT_STATE_32`（已指派项目经理）
  3. 生成项目编码 `queryProjectCode()`
  4. 插入项目主表 `pm_project_header`
  5. **项目组编码生成**（⚠️并发问题）：
     - 查询最大组编码 `queryMaxProjectGroupCode()`
     - 若为空则从 `prj_gp1` 开始，否则最大值+1
     - FIXME注释标注：并发情况下可能获取相同组编码
  6. 插入项目组表 `pm_project_group`
  7. 插入项目合同关联表 `pm_project_contract`
  8. 插入项目组关联表 `pm_project_group_relationship`
  9. 插入或更新项目状态表
  10. 插入项目成员（服务经理、销售、项目经理、项目经理B）
  11. 保存产品信息（从SAP订单数据）
  12. 发送通知邮件（不予跟踪/正常创建不同模板）
  13. 更新渠道信息
- **调用的DAO方法**：`projectDao.insertProject()`, `projectDao.queryMaxProjectGroupCode()`, `projectDao.insertProjectGroup()`, `projectDao.insertProjectContract()`, `projectDao.insertProjectGroupRelationship()`, `projectDao.queryProjectShipmentState()`, `projectDao.insertProjectProductLine()`, `projectDao.updateProjectDirectCloseTime()`
- **调用的其他Service方法**：`projectService.insertOrUpdateProjectState()`（通过自引用调用确保事务）, `basicDataService.querySysArg()`, `sendMailService`
- **异常处理**：
  | 异常类型 | 触发条件 | 处理方式 |
  |----------|----------|----------|
  | Exception | 查询OA邮件失败 | e.printStackTrace() + email设为null，继续执行 |
- **注意事项**：项目组编码生成存在并发安全隐患

#### `void insertBatchProject(Project project, int batchFunc) throws Exception`
- **功能描述**：批量创建项目
- **事务类型**：REQUIRED（insert*前缀）
- **核心算法/处理逻辑**：
  - batchFunc=1：直接闭环
  - batchFunc=2：指定服务经理
  - batchFunc=3：指定服务经理+项目经理
  - 项目编码追加"-0"后缀
  - 其余流程同insertProject

#### `void updateProjectByProjectId(Project project)`
- **功能描述**：更新项目基本信息（含成员变更和流程处理）
- **事务类型**：REQUIRED（update*前缀）
- **核心算法/处理逻辑**：
  1. 更新项目表信息
  2. 更新服务经理成员 → 若变更则添加通知、更新闭环审批流程中的审批人、更新项目状态
  3. 更新项目经理成员 → 若变更则添加通知、更新项目状态
  4. 更新项目经理B成员
  5. 项目经理变更时终止其手中的闭环/回访申请
  6. 闭环结束状态的项目更新项目经理后改为"项目跟踪"
  7. 更新渠道和实施方式
- **调用的其他Service方法**：`pmClosedLoopService.queryTaskByBussinessKeyAndUser()`, `pmClosedLoopService.updateEvaluationHeaderNextAcceptPerson()`, `taskService.setAssignee()`, `callBackService.updateCallBackApplyState()`

#### `boolean updateProjectProgramManagerByProjectId(Project project)`
- **功能描述**：指定项目经理
- **事务类型**：REQUIRED（@Transactional注解 + update*前缀）
- **核心算法**：更新成员 → 更新项目状态 → 发送通知 → 更新渠道和实施方式

#### `boolean updateProjectProgramManagerByProjectId(Project project, String type)`
- **功能描述**：指定项目经理（增强版，含邮件通知和流程终止）
- **事务类型**：REQUIRED（@Transactional注解 + update*前缀）
- **核心算法/处理逻辑**：
  1. 更新项目经理A和B
  2. 根据项目类型（普通类/工程类）选择不同邮件模板
  3. 发送邮件通知（主送项目经理，抄送服务经理）
  4. 终止原项目经理的审批流程
  5. 更新项目状态
- **异常处理**：
  | 异常类型 | 触发条件 | 处理方式 |
  |----------|----------|----------|
  | Exception | 任何异常 | 设置errMess + e.printStackTrace() + return false |

#### `synchronized void terminateProgramManagerActivities(Project project)`
- **功能描述**：项目经理更新时终止其手中的闭环/回访申请
- **事务类型**：无事务（synchronized修饰）
- **核心算法**：
  1. 查询项目经理A和B在闭环流程中的任务
  2. 查询项目进行中的回访流程
  3. 回访流程在项目经理手中则终止并更新状态为驳回
  4. 调用 `ProjectUtils.terminateActivities()` 统一终止

#### `boolean updateProjectMember(Project project, String membercode, String memberName)`
- **功能描述**：更新项目成员
- **事务类型**：REQUIRED（update*前缀）
- **核心算法**：
  1. 查询当前生效记录数
  2. count=0表示人员变更，需更新：失效原记录 + 插入新记录
  3. count>0表示未变更，不做操作
  4. 返回是否执行了更新操作

#### `void backToLastStep(int projectId, String projectState, String isback, Map paramMap)`
- **功能描述**：项目回退操作（不予跟踪/确认继续跟踪）
- **事务类型**：无事务（back*不匹配事务前缀）
- **核心算法/处理逻辑**：复杂状态机处理：
  - `PROJECT_CREATE_STATE42`：项目/服务经理选择不予跟踪
  - `PROJECT_CREATE_STATE40`：工程管理部创建后不予跟踪
  - `PROJECT_CREATE_STATE30`：工程管理部确认项目经理不予跟踪
  - `PROJECT_CREATE_STATE50`：服务经理将不予跟踪项目返回工程管理部
  - 每种状态对应不同的邮件模板和通知编码
  - 终止相关流程活动
  - 更新项目闭环流程状态

#### `void editProjectPlan(ProjectTask projectTask)`
- **功能描述**：编辑项目计划
- **事务类型**：无事务（edit*不匹配事务前缀）
- **核心算法**：
  1. 失效原有计划记录
  2. 解析事件键、合同号
  3. 遍历事件键，解析日期，按合同号插入新计划记录

#### `int insertPorjectWeekly(ProjectWeekly, List<WeeklyContent>×6)`
- **功能描述**：插入项目周报
- **事务类型**：REQUIRED（insert*前缀）
- **核心算法**：
  1. 插入周报主记录
  2. 按类型（工作/风险/求助/进展/计划/邮件）批量插入周报内容
  3. 过滤null元素

#### `synchronized void updateChannel(Project p)`
- **功能描述**：更新项目渠道信息（出货/服务/施工）
- **事务类型**：REQUIRED（update*前缀）

#### `List<ProjectPlanEvent> queryProjectPlanEventByProject(Project project)`
- **功能描述**：查询项目计划事件
- **核心算法**：实施方式为1且项目类别为20时，临时清除column010查询

#### `List<ProjectMember> queryProjectMembers(int projectId)`
- **功能描述**：查询项目成员列表

#### `int insertProjectMember(ProjectMember member)`
- **功能描述**：创建项目组成员
- **事务类型**：REQUIRED（insert*前缀）

#### `List<ShipmentInfo> queryShipmentInfoByContractNo(String contractNo, int projectId)`
- **功能描述**：查询出货信息

#### `Project queryProjectById(int projectId)` / `Project queryProjectByContractNo(String contractNo)`
- **功能描述**：查询项目信息

#### `List<Instruction> queryInstructionList(int projectId)`
- **功能描述**：查询项目留言列表（含反馈列表）

#### `List<OrderDataFromSap> queryOrderLineFromSapByContractNo(Project project)`
- **功能描述**：根据合同号从SAP查询订单行数据
- **事务类型**：无事务
- **核心算法/处理逻辑**：
  1. 查询SAP订单数据
  2. 按合同号和利润中心匹配项目
  3. 过滤已存在的出货信息
  4. 比对产品型号，标记新增/已有
  5. 返回未匹配的SAP订单行

#### `List<OrderDataFromSap> queryOrderDataListByProjectId(int projectId)` / `queryOrderDataDetailListByProjectId(int projectId)`
- **功能描述**：根据项目ID查询SAP订单数据/明细数据
- **事务类型**：无事务

#### `Project queryProjectByContractNoAndType(String contractNo, String projectType)`
- **功能描述**：根据合同号和项目类型查询项目
- **事务类型**：无事务

#### `Project queryProjectByPowerId(Project project)`
- **功能描述**：根据权限ID查询项目
- **事务类型**：无事务

#### `Project queryProjectSimplifyByProjectId(Integer projectId)`
- **功能描述**：查询项目简化信息（不含关联数据）
- **事务类型**：无事务

#### `int updateProjectByProjectIdSelective(Project project)`
- **功能描述**：选择性更新项目信息（仅更新非null字段）
- **事务类型**：REQUIRED（update*前缀）

#### `void updateProjectDetailByProjectId(Project project)`
- **功能描述**：更新项目详情
- **事务类型**：无事务（updateProjectDetail*不匹配事务前缀，因为代理配置中仅匹配update*前缀）

#### `List<Project> queryProjectListByPower(Project project, DisplayParam displayParam)`
- **功能描述**：根据权限查询项目列表
- **事务类型**：无事务

#### `String queryProjectStateByProjectId(Project project)`
- **功能描述**：查询项目状态
- **事务类型**：无事务

#### `List<ProjectWeekly> queryProjectWeeklyList(int projectId, int weeklyState)`
- **功能描述**：查询项目周报列表
- **事务类型**：无事务

#### `ProjectWeekly queryPorjectWeekly(int weeklyId)`
- **功能描述**：查询项目周报详情
- **事务类型**：无事务

#### `List<WeeklyContent> queryWeeklyContentList(int weeklyId, int optionType)`
- **功能描述**：查询周报内容列表
- **事务类型**：无事务

#### `void updatePorjectWeekly(ProjectWeekly, List<WeeklyContent>×6)`
- **功能描述**：更新项目周报
- **事务类型**：无事务（updatePorject*拼写错误，不匹配update*前缀）
- **注意事项**：⚠️ 方法名拼写错误（Porject而非Project），导致事务前缀不匹配，此写操作无事务保护

#### `void insertWeeklyFiles(List<WeeklyContent> filecontentList, int weeklyId)`
- **功能描述**：插入周报附件
- **事务类型**：REQUIRED（insert*前缀）

#### `void deleteFileById(int downFlileId)`
- **功能描述**：删除文件记录
- **事务类型**：REQUIRED（delete*前缀）

#### `List<ShipmentInfo> queryShipmentInfoByContractNo(String contractNo, int projectId)` / `queryShipmentInfoByContractNo(String, int, String profitCenter)`
- **功能描述**：查询出货信息（含利润中心过滤）
- **事务类型**：无事务

#### `int queryShipmentInfoSizeByContractNo(String contractNos)` / `queryShipmentInfoSizeByContractNo(String, String profitCenter)`
- **功能描述**：查询出货信息数量
- **事务类型**：无事务

#### `List<ShipmentInfo> queryTransferShipmentInfoByContractNo(Project, int)` / `queryTransferShipmentInfoByContractNo(Project, int, String)`
- **功能描述**：查询转移出货信息
- **事务类型**：无事务

#### `void deleteShipmentInstallInfoByProjectId(int projectId)`
- **功能描述**：删除项目安装信息
- **事务类型**：REQUIRED（delete*前缀）

#### `void insertInstallAddress(String selected, int projectId, String installAddress, String contractNo)` / `insertInstallAddress(String, int, String, String, String profitCenter)`
- **功能描述**：插入安装地址信息
- **事务类型**：REQUIRED（insert*前缀）
- **核心算法**：
  1. 解析selected（逗号分隔的出货ID列表）
  2. 遍历每个出货ID，更新安装地址
  3. 查询渠道信息，更新渠道

#### `void insertTransferShipment(String selected, Project project, Project transferProject)` / `insertTransferShipment(String, Project, Project, String profitCenter)`
- **功能描述**：插入转移出货信息（合同拆分/合并）
- **事务类型**：REQUIRED（insert*前缀）
- **核心算法**：
  1. 解析selected出货ID列表
  2. 复制出货信息到目标项目
  3. 更新原项目出货状态
  4. 更新项目渠道信息

#### `void insertWeeklyFeedback(Map<String, Object> paramMap)`
- **功能描述**：插入周报反馈
- **事务类型**：REQUIRED（insert*前缀）

#### `List<WeeklyFeedback> queryFeedbackList(int weeklyId)`
- **功能描述**：查询周报反馈列表
- **事务类型**：无事务

#### `boolean insertProjectDeliverFiles(ProjectDeliver pd, List<ProjectDeliver> pdlist, String username)`
- **功能描述**：插入项目交付件文件
- **事务类型**：无事务（insertProject*不匹配insert*前缀规则，因为代理配置匹配的是方法名前缀）
- **注意事项**：⚠️ 写操作方法无事务保护

#### `boolean uploadFile(ProjectDeliver pd, String did, File[] ul, String ufname)` / `uploadFile(ProjectDeliver, String, ProjectDeliver)`
- **功能描述**：上传交付件文件
- **事务类型**：无事务（upload*不匹配事务前缀）
- **注意事项**：⚠️ 写操作方法无事务保护

#### `List<ProjectDeliver> queryDeliverDetailByProjectId(int projectId)` / `queryDeliverDetailByProjectIdAndProjectType(int, String)` / `queryDeliverDetailByProjectIdAndDeliverType(int, String)`
- **功能描述**：查询交付件详情（按项目/项目类型/交付类型）
- **事务类型**：无事务

#### `int deleteDeliverById(int deliverid)`
- **功能描述**：删除交付件
- **事务类型**：REQUIRED（delete*前缀）

#### `void updateProjectIsbackByProjectId(int projectId, String isback, String backCause, String pm, int sendto, String nobackCause)` / `updateProjectIsbackByProjectId(int, String, String, String, int)`
- **功能描述**：更新项目回退状态
- **事务类型**：无事务（updateProjectIsback*不匹配update*前缀规则）
- **注意事项**：⚠️ 写操作方法无事务保护

#### `void invalidProject(int projectId)`
- **功能描述**：失效项目
- **事务类型**：无事务（invalid*不匹配事务前缀）
- **注意事项**：⚠️ 写操作方法无事务保护

#### `int insertLog(String handleName, String handleDesc, Integer projectId)`
- **功能描述**：插入操作日志
- **事务类型**：REQUIRED（insert*前缀）

#### `void updateLog(int handleId, int handleState)`
- **功能描述**：更新操作日志状态
- **事务类型**：REQUIRED（update*前缀）

#### `void updateProjectImplByProjectId(Project project)`
- **功能描述**：更新项目实施方式
- **事务类型**：无事务（updateProjectImpl*不匹配update*前缀规则）
- **注意事项**：⚠️ 写操作方法无事务保护

#### `int queryLastWeeklyId(int projectId)`
- **功能描述**：查询项目最新周报ID
- **事务类型**：无事务

#### `String createProjectWeeklyExecl(ProjectWeekly projectWeekly, List<WeeklyContent> workcontentList, List<WeeklyContent> riskcontentList, List<WeeklyContent> helpcontentList, List<WeeklyContent> progresscontentList, List<WeeklyContent> plancontentList)`
- **功能描述**：生成项目周报Excel文件
- **事务类型**：无事务
- **源码行号**：`ProjectService.java:403-408`
- **签名修正**：原误标为 `List<WeeklyContent>×6`（暗示 6 个 WeeklyContent 列表参数），实际为 **5 个** `List<WeeklyContent>` 参数（workcontent/riskcontent/helpcontent/progresscontent/plancontent）+ 1 个 `ProjectWeekly` 参数，共 6 个参数但只有 5 个 List 参数

#### `NotificationTemplate queryNotificationTemplate(String notificationCode)`
- **功能描述**：查询通知模板
- **事务类型**：无事务

#### `void updateProjectStatus(int projectId, String projectState)`
- **功能描述**：更新项目状态
- **事务类型**：无事务（updateProjectStatus*不匹配update*前缀规则）
- **注意事项**：⚠️ 写操作方法无事务保护

#### `String getMails(String username)` / `getMails(int roleId)`
- **功能描述**：根据用户名/角色ID获取邮箱地址
- **事务类型**：无事务

#### `List<String> getUsernames(int roleId)`
- **功能描述**：根据角色ID获取用户名列表
- **事务类型**：无事务

#### `List<Contract> queryContractList(Map<String, Object> paramMap)`
- **功能描述**：查询合同列表
- **事务类型**：无事务

#### `void insertMergeContract(String selected, int projectId)`
- **功能描述**：插入合并合同
- **事务类型**：REQUIRED（insert*前缀）
- **核心算法**：解析selected合同ID列表，逐个插入项目合同关联记录

#### `int insertNewProject(int projectId, String projectCode, List<Product> productList, String mergeBranchMark)`
- **功能描述**：插入新项目（合同拆分时创建）
- **事务类型**：REQUIRED（insert*前缀）
- **核心算法**：
  1. 查询原项目信息
  2. 复制项目属性到新项目
  3. 设置新项目编码和合并标记
  4. 插入项目主表、组表、合同关联、状态表
  5. 插入项目成员
  6. 保存产品信息

#### `List<Department> querySystemList()`
- **功能描述**：查询系统列表
- **事务类型**：无事务

#### `String queryMemberAddress(int projectId)`
- **功能描述**：查询项目成员邮箱地址
- **事务类型**：无事务

#### `void updateServiceProject(Map<String, Object> paramMap)`
- **功能描述**：更新服务项目
- **事务类型**：无事务（updateService*不匹配update*前缀规则）
- **注意事项**：⚠️ 写操作方法无事务保护

#### `Integer queryProjectContractCountByContractNo(String contractNo)` / `queryProjectContractCountByContractNoAndType(String, String)`
- **功能描述**：查询合同关联项目数量
- **事务类型**：无事务

#### `List<Project> findProjectList(Object... objs) throws UnsupportedEncodingException`
- **功能描述**：查询项目列表（多条件）
- **事务类型**：无事务
- **核心算法**：根据传入参数动态构建查询条件
- **源码行号**：`ProjectService.java:519`
- **签名修正**：原遗漏 `throws UnsupportedEncodingException` 声明。该方法对参数进行 URL 解码可能抛出 `UnsupportedEncodingException`

#### `void saveInstruction(Object... objs)`
- **功能描述**：保存留言
- **事务类型**：REQUIRED（save*前缀）

#### `void saveWeeklyFeedback(Object... objs)`
- **功能描述**：保存周报反馈
- **事务类型**：REQUIRED（save*前缀）

#### `int queryProjectShipment(int projectId)` / `queryHistoryProjectShipmentSize(int projectId)`
- **功能描述**：查询项目出货数量/历史出货数量
- **事务类型**：无事务

#### `String queryProjectCode(Project project)`
- **功能描述**：生成项目编码
- **事务类型**：无事务
- **核心算法**：查询最大编码 → 递增生成新编码

#### `void updateProjectExecutionState(int projectId, String executionState)` / `updateProjectExecutionState(Project, String)`
- **功能描述**：更新项目实施状态
- **事务类型**：无事务（updateProjectExecution*不匹配update*前缀规则）
- **注意事项**：⚠️ 写操作方法无事务保护

#### `void updateProjectCloseProcessState(int projectId, String closeProcessState)`
- **功能描述**：更新项目闭环流程状态
- **事务类型**：无事务（updateProjectClose*不匹配update*前缀规则）
- **注意事项**：⚠️ 写操作方法无事务保护

#### `List<Person> queryPersonList()`
- **功能描述**：查询人员列表
- **事务类型**：无事务

#### `void insertInstruction(Instruction instruction)`
- **功能描述**：插入项目留言
- **事务类型**：REQUIRED（insert*前缀）

#### `List<ProjectTask> queryProjectTaskByProjectId(int projectId)`
- **功能描述**：查询项目任务列表
- **事务类型**：无事务

#### `List<ProjectDeliver> queryProjectDeliverList(ProjectDeliver projectDeliver)`
- **功能描述**：查询项目交付件列表
- **事务类型**：无事务

#### `void updateProjectMember(ProjectMember member)`
- **功能描述**：更新项目成员
- **事务类型**：REQUIRED（update*前缀）

#### `int queryNeededUndelivedCount(Project project)`
- **功能描述**：查询未交付需求数量
- **事务类型**：无事务

#### `List<ProjectDeliver> queryNeededUndelivedProjectDeliverList(Project project)`
- **功能描述**：查询未交付需求列表
- **事务类型**：无事务

### 无事务写操作风险标注

以下方法执行写操作但**不匹配事务前缀规则**，存在数据一致性风险：

| 方法名 | 操作类型 | 风险说明 |
|--------|----------|----------|
| `updateProjectDetailByProjectId()` | 更新 | updateProjectDetail*不匹配update*前缀 |
| `updatePorjectWeekly()` | 更新 | ⚠️ 拼写错误(Porject)，导致不匹配update*前缀 |
| `updateProjectIsbackByProjectId()` | 更新 | updateProjectIsback*不匹配update*前缀 |
| `updateProjectImplByProjectId()` | 更新 | updateProjectImpl*不匹配update*前缀 |
| `updateProjectStatus()` | 更新 | updateProjectStatus*不匹配update*前缀 |
| `updateProjectExecutionState()` | 更新 | updateProjectExecution*不匹配update*前缀 |
| `updateProjectCloseProcessState()` | 更新 | updateProjectClose*不匹配update*前缀 |
| `updateServiceProject()` | 更新 | updateService*不匹配update*前缀 |
| `invalidProject()` | 失效 | invalid*不匹配任何事务前缀 |
| `uploadFile()` | 上传 | upload*不匹配任何事务前缀 |
| `insertProjectDeliverFiles()` | 插入 | insertProjectDeliver*不匹配insert*前缀 |

### 补充方法文档（遗漏方法补全）

> 以下方法为前期文档遗漏，现按功能类别分组补全。源码行号标注格式：`接口:ProjectService.java:行号` / `实现:ProjectServiceImpl.java:行号`。

**【项目状态/计划相关方法】**

#### `void insertOrUpdateProjectState(Project project)`
- **功能描述**：插入或更新项目状态（按 projectId 查询，存在则更新，不存在则插入）
- **事务类型**：REQUIRED（insert*前缀匹配）
- **核心算法**：
  1. 查询项目状态记录数 `projectDao.queryProjectState(projectId)`
  2. size=0 则插入 `projectDao.insertProjectState()`
  3. size>0 则更新 `projectDao.updateProjectState()`
- **调用的DAO方法**：`projectDao.queryProjectState()`, `projectDao.insertProjectState()`, `projectDao.updateProjectState()`
- **源码行号**：`ProjectService.java:582` / `ProjectServiceImpl.java:2627`

#### `boolean queryProjectPlanState(int projectId)`
- **功能描述**：查询项目是否首次制定计划（返回 true 表示需要更新计划状态）
- **事务类型**：无事务（query*前缀）
- **核心算法**：查询计划状态，若为 `PROJECT_PLAN_STATE_40` 或 null 则返回 true
- **调用的DAO方法**：`projectDao.queryPlanState()`
- **源码行号**：`ProjectService.java:617` / `ProjectServiceImpl.java:2638`

#### `String queryProjectCurrentPlan(int projectId)`
- **功能描述**：查询项目当前工程计划所处的阶段
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`projectDao.queryProjectCurrentPlan()`
- **源码行号**：`ProjectService.java:623` / `ProjectServiceImpl.java:2647`

#### `void updateProjectCloseTime(int closeObjId)`
- **功能描述**：根据项目闭环申请主表ID更新项目闭环时间
- **事务类型**：REQUIRED（update*前缀匹配）
- **调用的DAO方法**：`projectDao.updateProjectCloseTime()`
- **源码行号**：`ProjectService.java:628` / `ProjectServiceImpl.java:2653`

#### `void updateProjectDirectCloseTime(int projectId)`
- **功能描述**：根据项目ID直接更新项目闭环时间
- **事务类型**：REQUIRED（update*前缀匹配）
- **调用的DAO方法**：`projectDao.updateProjectDirectCloseTime()`
- **源码行号**：`ProjectService.java:633` / `ProjectServiceImpl.java:2658`

#### `void updateProjectLastRefreshTime(int projectId)`
- **功能描述**：更新项目数据最新刷新时间
- **事务类型**：REQUIRED（update*前缀匹配）
- **调用的DAO方法**：`projectDao.updateProjectLastRefreshTime()`
- **源码行号**：`ProjectService.java:638` / `ProjectServiceImpl.java:2663`

#### `void updateProjectPlanStateToClose(int closeObjId)`
- **功能描述**：根据闭环申请主表ID更新项目计划状态为"项目闭环"
- **事务类型**：REQUIRED（update*前缀匹配）
- **调用的DAO方法**：`projectDao.updateProjectPlanStateToClose()`
- **源码行号**：`ProjectService.java:643` / `ProjectServiceImpl.java:2668`

#### `void updateProjectCloseProcessState(Project project, String closeProcessState)`
- **功能描述**：更新项目闭环流程状态（重载方法，接收 Project 对象）。当项目状态为"不予跟踪"或"已闭环"时，闭环流程状态强制改为"闭环结束"
- **事务类型**：无事务（updateProjectClose*不匹配update*前缀规则）
- **核心算法**：
  1. 校验 project 和 projectId
  2. 若项目状态为 `PROJECT_STATE_DENY` 或 `PROJECT_STATE_CLOSEDLOOP`，将 closeProcessState 改为 `PROJECT_CLOSE_PROCESS_STATE_50`
  3. 通过自引用 `projectService.insertOrUpdateProjectState()` 更新状态（确保事务）
- **调用的Service方法**：`projectService.insertOrUpdateProjectState()`（通过自引用调用确保事务）
- **源码行号**：`ProjectService.java:610` / `ProjectServiceImpl.java:2609`
- **注意事项**：⚠️ 写操作方法无事务保护，但内部调用 `insertOrUpdateProjectState()` 时该子方法有事务

**【通知相关方法】**

#### `void addFixedNotification(String templateCode, int projectId)`
- **功能描述**：添加固定内容通知（按模板编码生成通知）
- **事务类型**：REQUIRED（add*前缀匹配）
- **核心算法**：构建 params Map（templateCode、projectId），调用 `NotificationTemplateUtil.KeepNotification()` 发送通知
- **调用的工具类**：`NotificationTemplateUtil.KeepNotification()`
- **源码行号**：`ProjectService.java:649` / `ProjectServiceImpl.java:2673`

#### `void addDynamicNotification(String templateCode, int projectId, String content)` / `addDynamicNotification(String templateCode, int projectId, HashMap<String, Object> params)`
- **功能描述**：添加动态内容通知（按模板编码+动态内容生成通知）
- **事务类型**：REQUIRED（add*前缀匹配）
- **参数**：
  - 重载1：`content` 为动态通知文本内容
  - 重载2：`params` 为动态参数 Map（除 templateCode、projectId 外的其他参数）
- **核心算法**：将 templateCode、projectId 写入 params，调用 `NotificationTemplateUtil.KeepNotification()` 发送通知
- **调用的工具类**：`NotificationTemplateUtil.KeepNotification()`
- **源码行号**：`ProjectService.java:656, 663` / `ProjectServiceImpl.java:2681, 2690`

**【回访/闭环相关方法】**

#### `int queryProjectIdBycloseId(int closeObjId)`
- **功能描述**：根据项目闭环申请主表ID获取项目ID
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`projectDao.queryProjectIdBycloseId()`
- **源码行号**：`ProjectService.java:669` / `ProjectServiceImpl.java:2697`

#### `List<CallBack> queryCallBackList(int projectId)`
- **功能描述**：查询项目的回访流程任务列表
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`projectDao.queryCallBackList()`
- **源码行号**：`ProjectService.java:675` / `ProjectServiceImpl.java:2702`

#### `int queryCallBackingSize(int projectId)`
- **功能描述**：查询项目正在审批中的回访申请数量
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`projectDao.queryCallBackingSize()`
- **源码行号**：`ProjectService.java:681` / `ProjectServiceImpl.java:2707`

#### `int canCloseLoop(Project project)`
- **功能描述**：判断项目是否可以闭环
- **事务类型**：无事务（can*不匹配任何事务前缀；查询判断方法）
- **核心算法**：源码中前三个判断条件（安装数=发货数、回访完成、服务商/客户不为空）被注释，仅保留第四个判断：未上传必传交付件数量为 0 时返回 1（可闭环）
- **调用的Service方法**：`this.queryNeededUndelivedCount(project)`
- **源码行号**：`ProjectService.java:688` / `ProjectServiceImpl.java:2719`
- **注意事项**：⚠️ 实际逻辑已大幅简化，原设计四条件仅剩交付件判断

#### `List<CallBack> queryCallBackRunList(int projectId, int applyState)`
- **功能描述**：查询项目正在审批中的回访流程（按审批状态过滤）
- **事务类型**：无事务（query*前缀）
- **参数**：`applyState` 回访申请状态
- **调用的DAO方法**：`projectDao.queryCallBackRunList(params)`
- **源码行号**：`ProjectService.java:733` / `ProjectServiceImpl.java:2761`

**【订单/出货数据相关方法】**

#### `List<OrderDataFromSap> queryRmaOrderDataByContractNo(String contractNo)`
- **功能描述**：查询退货订单（RMA）信息，支持多合同号（逗号分隔）
- **事务类型**：无事务（query*前缀）
- **核心算法**：按逗号拆分合同号，逐个查询 RMA 订单数据并合并返回
- **调用的DAO方法**：`projectDao.queryRmaOrderDataByContractNo(contract)`
- **异常处理**：contractNo 为空时返回 `Collections.emptyList()`
- **源码行号**：`ProjectService.java:695` / `ProjectServiceImpl.java:2769`

#### `List<Project> queryProjectListByOfficeAndMemberCode(Project project)`
- **功能描述**：根据办事处和成员编码查询项目列表
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`projectDao.queryProjectListByOfficeAndMemberCode()`
- **源码行号**：`ProjectService.java:697` / `ProjectServiceImpl.java:2783`

#### `List<RealProductLineBean> queryRealOrderDataListByProjectId(int projectId)` / `int queryRealOrderDataSizeByProjectId(int projectId)`
- **功能描述**：查询项目真实订单数据列表/数量
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`projectDao.queryRealOrderDataListByProjectId()`, `projectDao.queryRealOrderDataSizeByProjectId()`
- **源码行号**：`ProjectService.java:704, 726` / `ProjectServiceImpl.java:2788, 2793`

**【租赁配置相关方法】**

#### `List<?> queryProjectLeaseLineByProjectCode(String smsProjectCode)` / `int queryProjectLeaseLineSizeByProjectCode(String smsProjectCode)`
- **功能描述**：根据 SMS 项目编码查询租赁配置列表/数量
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`projectDao.queryProjectLeaseLineByProjectCode()`, `projectDao.queryProjectLeaseLineSizeByProjectCode()`
- **源码行号**：`ProjectService.java:1082, 1089` / `ProjectServiceImpl.java:2798, 2803`

#### `List<?> queryProjectProductConfigLevelInfoByProjectCode(String smsProjectCode)` / `int queryProjectProductConfigLevelInfoSizeByProjectCode(String smsProjectCode)`
- **功能描述**：根据 SMS 项目编码查询产品配置级别信息列表/数量
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`projectDao.queryProjectProductConfigLevelInfoByProjectCode()`, `projectDao.queryProjectProductConfigLevelInfoSizeByProjectCode()`
- **源码行号**：`ProjectService.java:1091, 1093` / `ProjectServiceImpl.java:2808, 2813`

**【软件版本相关方法】**

#### `List<ProjectSoftVersion> querySoftversionList(String contractNo, int projectId)` / `querySoftversionList(String contractNo, int projectId, String profitCenter)` / `querySoftversionList(String contractNo, int projectId, Map<String, Object> params)`
- **功能描述**：查询项目软件版本列表（三个重载，分别支持基础查询、按利润中心过滤、按参数 Map 过滤）
- **事务类型**：无事务（query*前缀）
- **参数**：
  - 重载1：合同号 + 项目ID
  - 重载2：合同号 + 项目ID + 利润中心
  - 重载3：合同号 + 项目ID + 参数 Map（支持扩展过滤条件）
- **调用的DAO方法**：`projectDao.querySoftversionList()`（三个对应重载）
- **源码行号**：`ProjectService.java:741, 750, 760` / `ProjectServiceImpl.java:2840, 2845, 2850`

#### `List<ProjectSoftVersion> selectProjectSoftVersionList(ProjectSoftVersion projectSoftVersion, DisplayParam displayParam)`
- **功能描述**：查询项目已保存的设备软件版本列表（分页）
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`projectDao.selectProjectSoftVersionList()`
- **源码行号**：`ProjectService.java:769` / `ProjectServiceImpl.java:2855`

#### `void updateSoftversion(List<? extends ShipmentInfo> softversionList, SoftChangeLog softChangeLog)`
- **功能描述**：更新软件版本（含版本变更日志记录、版本失效、新增版本）
- **事务类型**：REQUIRED（update*前缀匹配）
- **核心算法**：
  1. 遍历 softversionList，使用 `SoftVersionUtil.createSoftVersionParser()` 解析 CONP 字段，填充 conpType/conpSeries/conpMark
  2. 统计未变更数量 sameCount（conpChange+bootChange+cpldChange+pcbChange 均为 0）
  3. 若存在变更且非全部未变更：
     - 查询当前版本号 `querySoftVersionNum()`，生成新版本号 V(n+1)
     - 失效现有最新版本记录 `updateInvalidSoftVersionLog()`
     - 插入新的版本变更日志 `insertSoftVersionLog()`
     - 回填 softChangeLog 属性（BeanUtil.copyProperties）
     - 失效现有软件版本 `updateInvalidSoftversion()`
     - 批量插入新软件版本 `insertSoftVersionList()`
- **调用的DAO方法**：`projectDao.querySoftVersionNum()`, `projectDao.updateInvalidSoftVersionLog()`, `projectDao.insertSoftVersionLog()`, `projectDao.queryOneSoftChangeLog()`, `projectDao.updateInvalidSoftversion()`, `projectDao.insertSoftVersionList()`
- **调用的工具类**：`SoftVersionUtil.createSoftVersionParser()`, `BeanUtil.copyProperties()`
- **源码行号**：`ProjectService.java:776` / `ProjectServiceImpl.java:2859`

#### `List<SoftChangeLog> queryHistSoftChangeLog(int projectId)`
- **功能描述**：查询项目软件版本历史变更日志
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`projectDao.queryHistSoftChangeLog()`
- **源码行号**：`ProjectService.java:782` / `ProjectServiceImpl.java:2910`

#### `List<ProjectSoftVersion> queryHistSoftVersionList(SoftChangeLog softChangeLog)`
- **功能描述**：检索具体的版本变更信息（支持查询出厂版本和变更版本）
- **事务类型**：无事务（query*前缀）
- **核心算法**：
  1. 若 `softChangeLog.getId() == -1`，查询出厂版本：先查询项目获取合同号，再调用 `queryHistSoftVersionList(softChangeLog, contractNo)`
  2. 否则查询变更版本：直接调用 `queryHistSoftVersionList(softChangeLog)`
- **调用的DAO方法**：`projectDao.queryProjectById()`, `projectDao.queryHistSoftVersionList()`（两个重载）
- **源码行号**：`ProjectService.java:788` / `ProjectServiceImpl.java:2915`

#### `SoftChangeLog queryOneSoftChangeLog(int id)`
- **功能描述**：查询单个版本更新日志
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`projectDao.queryOneSoftChangeLog()`
- **源码行号**：`ProjectService.java:794` / `ProjectServiceImpl.java:2926`

**【邮件/成员相关方法】**

#### `String queryMailByUserNameFromOA(String userName)`
- **功能描述**：从同步的 OA 数据库表中查询邮件地址
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`projectDao.queryMailByUserNameFromOA()`
- **源码行号**：`ProjectService.java:807` / `ProjectServiceImpl.java:2931`

#### `List<ProjectMember> queryValidMemberByProjectId(int projectId)`
- **功能描述**：查询项目有效的成员，只包括销售、服务经理和项目经理
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`projectDao.queryValidMemberByProjectId()`
- **源码行号**：`ProjectService.java:814` / `ProjectServiceImpl.java:2936`

**【批量操作相关方法】**

#### `int batchDeleteProject(List<Project> projectList)`
- **功能描述**：批量删除项目（主要用于借货项目的实施）
- **事务类型**：无事务（batchDelete*不匹配任何事务前缀规则）
- **核心算法**：
  1. 拼接合同号字符串
  2. 查询存在的项目列表 `queryExistsProjectByContractNos()`
  3. 对每个项目调用 `termainteActivities()` 终止审批流程
  4. 调用 `projectDao.batchDeleteProject()` 批量删除
- **调用的DAO方法**：`projectDao.queryExistsProjectByContractNos()`, `projectDao.batchDeleteProject()`
- **调用的私有方法**：`termainteActivities(project)`
- **源码行号**：`ProjectService.java:820` / `ProjectServiceImpl.java:2945`
- **注意事项**：⚠️ 写操作方法无事务保护

#### `int batchInvalidProject(List<Project> projectList)`
- **功能描述**：批量失效项目（主要用于借货项目的实施）
- **事务类型**：无事务（batchInvalid*不匹配任何事务前缀规则）
- **核心算法**：
  1. 拼接合同号字符串
  2. 查询存在的项目列表
  3. 对每个项目调用 `termainteActivities()` 终止审批流程
  4. 调用 `this.invalidProject()` 失效项目
  5. 返回失效的项目数量
- **调用的DAO方法**：`projectDao.queryExistsProjectByContractNos()`
- **调用的Service方法**：`this.invalidProject(projectId)`
- **源码行号**：`ProjectService.java:827` / `ProjectServiceImpl.java:2962`
- **注意事项**：⚠️ 写操作方法无事务保护

#### `List<Project> queryTransferProjectList(Project project)`
- **功能描述**：转移设备界面查询项目列表
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`projectDao.queryTransferProjectList()`
- **源码行号**：`ProjectService.java:834` / `ProjectServiceImpl.java:2982`

**【通知/导出导入相关方法】**

#### `List<Notification> queryNotifyList(int projectId)`
- **功能描述**：查询项目通知列表
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`projectDao.queryNotifyList()`
- **源码行号**：`ProjectService.java:891` / `ProjectServiceImpl.java:2987`

#### `void importSpotCheckIgnoreItem(List<Item> itemList)`
- **功能描述**：导入现场验货单需要忽略序列号明细的 item
- **事务类型**：REQUIRED（@Transactional注解；import*虽不匹配事务前缀但显式声明事务）
- **核心算法**：
  1. 校验 itemList 非空
  2. 清空忽略列表 `truncateSpotCheckIgnoreItem()`
  3. 批量插入 `batchInsertSpotCheckIgnoreItem()`
- **调用的DAO方法**：`projectDao.truncateSpotCheckIgnoreItem()`, `projectDao.batchInsertSpotCheckIgnoreItem()`
- **源码行号**：`ProjectService.java:885` / `ProjectServiceImpl.java:2993`

#### `Map<String, String> exportSpotCheckList(Project project)`
- **功能描述**：导出现场验货单为 Excel（失败则降级为 Word 文档）
- **事务类型**：无事务（export*不匹配任何事务前缀；只读导出操作）
- **核心算法**：
  1. 根据 salesType 区分查询验货清单（"14"总代借货带 column001，其他不带）
  2. 加载 Excel 模板 `com/dp/plat/template/spotCheck.xlsx`
  3. 填充项目名称、合同号
  4. 遍历明细填充行（合同号、item编码、名称、数量、序列号、备注）
  5. 写入文件，返回 filePath/fileName
- **异常处理**：
  | 异常类型 | 触发条件 | 处理方式 |
  |----------|----------|----------|
  | Exception | Excel 生成失败 | 降级为 Word 文档生成（spotCheckDoc.ftl 模板）；Word 失败则返回 null |
- **调用的DAO方法**：`projectDao.querySpotCheckList()`（两个重载）
- **调用的工具类**：`WordUtil.createWord()`, POI（XSSFWorkbook）
- **源码行号**：`ProjectService.java:873` / `ProjectServiceImpl.java:3002`

#### `Map<String, String> exportOverWarrantyRemindList(Project project)`
- **功能描述**：导出过保提醒函为 Word 文档
- **事务类型**：无事务（export*不匹配任何事务前缀；只读导出操作）
- **核心算法**：
  1. 根据 salesType 区分查询过保提醒清单（"14"总代借货带 column001）
  2. 并行流查询各合同号的订单创建时间，排序得到服务周期起止时间
  3. 查询服务经理人员信息 `queryPersonFromOaByCode()`
  4. 构建数据 Map（客户名称、服务经理、周期起止、维保状态、清单）
  5. 调用 `WordUtil.createWord()` 生成 Word 文档
- **调用的DAO方法**：`projectDao.queryOverWarrantyRemindList()`, `projectDao.queryPersonFromOaByCode()`
- **调用的Service方法**：`this.queryProjectByContractNo()`
- **调用的工具类**：`WordUtil.createWord()`
- **异常处理**：Exception 时 e.printStackTrace() 并返回 null
- **源码行号**：`ProjectService.java:879` / `ProjectServiceImpl.java:3098`

**【维保相关方法】**

#### `ProjectMaintenanceVO selectProjectMaintenanceById(Integer id)`
- **功能描述**：根据 ID 查询项目维保记录
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`projectDao.selectProjectMaintenanceById()`
- **源码行号**：`ProjectService.java:909` / `ProjectServiceImpl.java:3256`

#### `List<ProjectMaintenanceVO> selectProjectMaintenanceList(ProjectMaintenanceVO projectMaintenance)` / `selectProjectMaintenanceVOList(ProjectMaintenanceVO projectMaintenance)`
- **功能描述**：查询项目维保列表（两个重载方法，分别返回不同视图对象）
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`projectDao.selectProjectMaintenanceList()`, `projectDao.selectProjectMaintenanceVOList()`
- **源码行号**：`ProjectService.java:897, 915` / `ProjectServiceImpl.java:3261, 3266`

#### `List<Map<String, Object>> selectProjectMaintenanceMapList(ProjectMaintenanceVO projectMaintenance)` / `selectProjectMaintenanceMapList(ProjectMaintenanceVO projectMaintenance, DisplayParam displayParam)`
- **功能描述**：查询项目维保 Map 列表（重载方法，带 DisplayParam 支持分页）
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`projectDao.selectProjectMaintenanceMapList()`（两个重载）
- **源码行号**：`ProjectService.java:921, 928` / `ProjectServiceImpl.java:3271, 3276`

#### `Integer insertOrUpdateProjectMaintenance(ProjectMaintenance projectMaintenance)`
- **功能描述**：插入或更新项目维保记录
- **事务类型**：REQUIRED（@Transactional注解 + insert*前缀匹配）
- **核心算法**：
  1. 记录原 id（更新场景）
  2. 调用 `projectDao.insertOrUpdateProjectMaintenance()` 执行插入或更新
  3. 若原 id 不为 null，回填到 projectMaintenance 对象
- **调用的DAO方法**：`projectDao.insertOrUpdateProjectMaintenance()`
- **源码行号**：`ProjectService.java:903` / `ProjectServiceImpl.java:3282`

#### `Integer selectSingleProjectMaintenanceMaxId(ProjectMaintenance projectMaintenance)`
- **功能描述**：查询单个项目中最大的维护记录 ID
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`projectDao.selectSingleProjectMaintenanceMaxId()`
- **源码行号**：`ProjectService.java:1003` / `ProjectServiceImpl.java:3292`

#### `Map<String, Object> queryProjectWarrantyState(Integer projectId)`
- **功能描述**：查询项目的维保状态、维保级别、增值服务
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`projectDao.queryProjectWarrantyState()`
- **源码行号**：`ProjectService.java:1020` / `ProjectServiceImpl.java:3297`

#### `boolean uploadMaintenanceFile(ProjectMaintenanceVO projectMaintenance, ProjectDeliver projectDeliver, String deliverId, ProjectDeliver deliverFile)`
- **功能描述**：项目维护上传交付件（含服务交付记录、邮件通知）
- **事务类型**：REQUIRED（@Transactional注解；upload*虽不匹配事务前缀但显式声明事务）
- **参数**：
  - `projectMaintenance`：维保 VO（含 projectId、processTime、warrantyState、officeCode 等）
  - `projectDeliver`：交付件对象
  - `deliverId`：交付件 ID
  - `deliverFile`：上传的交付件文件
- **核心算法**：
  1. 调用 `this.uploadFile()` 上传交付件
  2. 查询交付件名称 `queryDeliverName()`
  3. 读取服务交付配置 `querySysArg("pm.project.maintenance.serviceDelivery.deliverFile")`
  4. 调用 `matchServiceDeliveryConfigMap()` 匹配交付件对应的服务配置（服务名、年服务次数、服务编码等）
  5. 计算最近服务周期 `DateUtil.getNearlyYearDates()`
  6. 多交付件场景校验是否已上传 `queryProjectMaintenanceServiceDeliveriedByMap()`
  7. 查询上传次数 `queryProjectMaintenanceDeliverCount()`
  8. 插入服务交付记录 `insertProjectServiceDeliveryBySelective()`
  9. 回填年服务次数、当前服务次数到 warrantyState
  10. 判断是否在服务期限内，期限内发送邮件：
      - 主送：项目经理、服务经理
      - 抄送：销售、办事处主任、服务交付验收小组群组邮箱
      - 调用 `NotificationTemplateUtil.keepMail()` 发送
- **调用的DAO方法**：`projectDao.queryDeliverName()`, `projectDao.queryProjectMaintenanceServiceDeliveriedByMap()`, `projectDao.insertProjectServiceDeliveryBySelective()`
- **调用的Service方法**：`this.uploadFile()`, `this.queryValidMemberEmailByProjectIdAndRoles()`, `basicDataService.querySysArg()`
- **调用的工具类**：`DateUtil.getNearlyYearDates()`, `NotificationTemplateUtil.keepMail()`, `SpringContext.getApplicationContext().getBean()`
- **异常处理**：服务期限外返回 false（不发送邮件）
- **源码行号**：`ProjectService.java:1029` / `ProjectServiceImpl.java:3303`
- **注意事项**：方法名 upload* 不匹配事务前缀，但通过 @Transactional 显式声明事务

#### `List<Map<String, Object>> selectProjectMaintenanceServiceDeliveryList(ProjectMaintenanceVO projectMaintenance, DisplayParam displayParam)`
- **功能描述**：查询服务交付列表（含服务次数、剩余次数、服务周期等计算字段）
- **事务类型**：无事务（select*前缀）
- **核心算法**：
  1. 查询服务交付配置 Map `queryServiceDeliveryConfigMap()`
  2. 设置 serviceTypes 过滤条件
  3. 调用 `projectDao.selectProjectMaintenanceServiceDeliveryList()` 查询列表
  4. 遍历每条记录：
     - 匹配服务配置（serviceName、yearCount、deliverId）
     - 计算最近服务周期 `DateUtil.getNearlyYearDates()`
     - 查询上传次数 `queryProjectMaintenanceDeliverCount()`
     - 填充 serviceName、yearCount、serviceCount、quarterCount、remainedCount、serviceStartDate、serviceEndDate 等计算字段
  5. 移除未匹配配置的记录
- **调用的DAO方法**：`projectDao.selectProjectMaintenanceServiceDeliveryList()`
- **调用的Service方法**：`this.queryServiceDeliveryConfigMap()`, `this.queryProjectMaintenanceDeliverCount()`
- **调用的工具类**：`DateUtil.getNearlyYearDates()`
- **源码行号**：`ProjectService.java:1037` / `ProjectServiceImpl.java:3466`

**【项目督查相关方法】**

#### `ProjectSupervisionVO selectProjectSupervisionById(Integer id)`
- **功能描述**：根据 ID 查询项目督查记录
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`projectDao.selectProjectSupervisionById()`
- **源码行号**：`ProjectService.java:965` / `ProjectServiceImpl.java:3592`

#### `List<ProjectSupervisionVO> selectProjectSupervisionList(ProjectSupervisionVO projectSupervision)` / `selectProjectSupervisionVOList(ProjectSupervisionVO projectSupervision)`
- **功能描述**：查询项目督查列表（两个重载方法，分别返回不同视图对象）
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`projectDao.selectProjectSupervisionList()`, `projectDao.selectProjectSupervisionVOList()`
- **源码行号**：`ProjectService.java:971, 977` / `ProjectServiceImpl.java:3597, 3602`

#### `List<Map<String, Object>> selectProjectSupervisionMapList(ProjectSupervisionVO projectSupervision)` / `selectProjectSupervisionMapList(ProjectSupervisionVO projectSupervision, DisplayParam displayParam)`
- **功能描述**：查询项目督查 Map 列表（重载方法，带 DisplayParam 支持分页）
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`projectDao.selectProjectSupervisionMapList()`（两个重载）
- **源码行号**：`ProjectService.java:983, 990` / `ProjectServiceImpl.java:3607, 3612`

#### `Integer insertOrUpdateProjectSupervision(ProjectSupervision projectSupervision)`
- **功能描述**：插入或更新项目督查记录
- **事务类型**：REQUIRED（@Transactional注解 + insert*前缀匹配）
- **调用的DAO方法**：`projectDao.insertOrUpdateProjectSupervision()`
- **源码行号**：`ProjectService.java:996` / `ProjectServiceImpl.java:3618`

**【其他方法】**

#### `void updateSoleAgentLendProject()`
- **功能描述**：更新总代借货的项目信息（合同号、项目名称）
- **事务类型**：REQUIRED（@Transactional注解 + update*前缀匹配）
- **核心算法**：
  1. 获取当前用户（失败则默认 "sys"）
  2. 通过 SqlMapClientTemplate 创建临时表（tempSoleAgentProjectTable、tempNewOrderContractInfoTable、tempOldOrderContractInfoTable）
  3. 查询总代借货项目列表 `querySoleAgentProject()`
  4. 对每个项目：
     - `mergeOldSoleAgentLendProjectContract()` 合并历史订单合同
     - `mergeNewSoleAgentLendProjectContract()` 合并新订单合同
  5. finally 块清理临时表
- **调用的DAO方法**：`projectDao.querySoleAgentProject()`，以及 SqlMapClientTemplate 直接调用 createTemp*/dropTemp* 操作
- **调用的私有方法**：`mergeOldSoleAgentLendProjectContract()`, `mergeNewSoleAgentLendProjectContract()`
- **异常处理**：Exception 时 e.printStackTrace()，finally 块始终清理临时表
- **源码行号**：`ProjectService.java:1008` / `ProjectServiceImpl.java:3625`

#### `List<Map<String, Object>> queryMarketRelations()`
- **功能描述**：查询市场关系列表
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`projectDao.queryMarketRelations()`
- **源码行号**：`ProjectService.java:1040` / `ProjectServiceImpl.java:3860`

#### `List<Map<String, Object>> selectContractAcceptanceDeliveryInfo(Map<String, Object> params)`
- **功能描述**：查询合同号的交付件完成情况
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`projectDao.selectContractAcceptanceDeliveryInfo()`
- **源码行号**：`ProjectService.java:1047` / `ProjectServiceImpl.java:3865`

#### `List<Map<String, Object>> selectProblemTicket(Map<String, Object> params)` / `selectLicenseInfo(Map<String, Object> params)`
- **功能描述**：查询项目的工单信息 / License 授权信息
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`projectDao.selectProblemTicket()`, `projectDao.selectLicenseInfo()`
- **源码行号**：`ProjectService.java:1054, 1061` / `ProjectServiceImpl.java:3870, 3875`

#### `List<Map<String, Object>> selectProblemTicketByProject(Project project)`
- **功能描述**：通过项目的发货列表查询项目的工单信息
- **事务类型**：无事务（select*前缀）
- **核心算法**：
  1. 查询项目简化信息 `this.queryProjectSimplifyByProjectId()` 获取合同号
  2. 若为总代借货项目（salesType="14"），取利润中心 column001
  3. 构建 params（contractNo、sourceContractNo 去除 -L/-C 后缀、projectId、profitCenter、excludeTransferOut=true）
  4. 调用 `projectDao.selectProblemTicketByProjectBarcode()` 查询
- **调用的DAO方法**：`projectDao.selectProblemTicketByProjectBarcode()`
- **调用的Service方法**：`this.queryProjectSimplifyByProjectId()`
- **异常处理**：project 为 null 或查询不到项目时返回 `Collections.emptyList()`
- **源码行号**：`ProjectService.java:1068` / `ProjectServiceImpl.java:3880`

#### `String getUploadFileRename(String targetFileName)`
- **功能描述**：将上传的文件重命名（生成唯一文件名）
- **事务类型**：无事务（get*前缀；纯工具方法）
- **核心算法**：
  1. 调用私有方法 `getRename(targetFileName)`
  2. `getRename()` 解析扩展名，调用 `getName()` 生成新文件名
  3. `getName()` 生成规则：yyyyMMddHHmmss 时间戳 + 10 位随机字符（a-z0-9）
- **源码行号**：`ProjectService.java:539` / `ProjectServiceImpl.java:2298`

---

## 10. ProjectPlanServiceImpl

### 类概述
- 实现接口：`ProjectPlanService`
- 事务代理Bean：`projectPlanServiceAgent`
- 依赖的DAO：`ProjectPlanDao`

### 方法列表

#### `List<ProjectPlan> queryProjectPlanListByContractNo(String contractNo)`
- **功能描述**：根据合同号查询项目计划列表
- **事务类型**：无事务

---

## 11. PresalesServiceImpl

### 类概述
- 实现接口：`PresalesService`
- 事务代理Bean：`presalesServiceAgent`
- 依赖的DAO：`PresalesDao`, `PmClosedLoopDao`, `ProjectDao`, `UserManageDao`
- 跨Service依赖：`WorkFlowService`, `BasicDataService`

### 方法列表

#### `Presales queryPresalesById(int presalesId)`
- **功能描述**：查询售前项目详情（含权限校验和附件聚合）
- **事务类型**：无事务
- **核心算法/处理逻辑**：
  1. **权限校验**：检查当前用户是否具有以下角色之一：
     - 工程管理员/管理员/售前人员/财务人员/项目管理员
     - 项目查看者（需区域权限匹配）
     - 申请人/项目经理/服务经理本人
     - 不满足则返回null
  2. **附件聚合**：
     - SMS系统同步的借货交付件（正则解析fileName）
     - OA系统附件（需获取OA Token）
     - 新交付件（ProjectDeliver）
     - 历史交付件（confirmFileIds）
- **调用的DAO方法**：`presalesDao.queryPresalesById()`, `projectDao.queryDeliverDetailByProjectIdAndProjectType()`, `basicDataService.queryFileList()`
- **调用的其他Service方法**：`basicDataService.querySysArg()`

#### `void startPresalesFlow(Presales presales, PresalesComment param)`
- **功能描述**：启动售前项目审批流程
- **事务类型**：REQUIRED（start*前缀）
- **核心算法/处理逻辑**：
  1. 获取项目经理结束后的下级办理人角色配置
  2. 保存售前项目分类和项目编码
  3. 更新产品明细表
  4. 添加项目成员（服务经理、项目经理）
  5. 更新售前项目状态
  6. 工程管理部直接指定项目经理时，增加工程计划
  7. **启动Activiti流程**：
     - 构建流程变量（presalesId, applyBy, pmTaskNextRole）
     - 拼接businessKey：`Presales.presalesId`
     - 启动流程实例
     - 回写instId到申请表
  8. 办理第一个任务
  9. 添加自定义审批意见
  10. 发送邮件通知下一步审批人
  11. 异步更新各阶段耗时
- **调用的其他Service方法**：`workFlowService.startProcess()`, `workFlowService.doSelfTask()`, `workFlowService.addSelfActComment()`

#### `void submitSmAduit(Presales presales, PresalesComment param)`
- **功能描述**：服务经理审核提交
- **事务类型**：REQUIRED（submit*前缀）
- **核心算法**：处理服务经理审核流程，含任务指派、状态更新、流程推进、邮件通知

#### `void submitpmAduit(Presales presales, PresalesComment param)`
- **功能描述**：项目经理审核提交
- **事务类型**：REQUIRED（submit*前缀）
- **核心算法**：根据pmTaskNextRole判断下一步是服务经理还是工程管理部

#### `void submitEmAduit(Presales presales, PresalesComment param)`
- **功能描述**：工程管理部审核提交
- **事务类型**：REQUIRED（submit*前缀）

#### `void submitReApply(Presales presales, PresalesComment param)`
- **功能描述**：重新提交申请
- **事务类型**：REQUIRED（submit*前缀）

#### `void insertPresalesQuesnaire(Presales, PmClQuesnaireResultHeader, List<PmClQuesnaireResultLine>)`
- **功能描述**：插入售前项目问卷
- **事务类型**：REQUIRED（insert*前缀）
- **核心算法**：
  1. 插入问卷头
  2. 插入问卷结果行
  3. 查询是否已保存过问卷 → 更新或新增关联关系

#### `void terminate2Close(String presalesIds, String comment)`
- **功能描述**：终止售前流程并直接关闭
- **事务类型**：REQUIRED（@Transactional注解）
- **核心算法**：
  1. 逗号分隔presalesId列表
  2. 遍历每个售前项目：
     - 查询售前项目信息
     - 删除流程实例
     - 添加审批意见
     - 更新项目状态为不予跟踪
     - 更新售前头信息
     - 异步更新各阶段耗时

#### `boolean uploadFile(ProjectDeliver pd, String did, File[] ul, String ufname)`
- **功能描述**：上传交付件文件
- **事务类型**：无事务
- **核心算法/处理逻辑**：
  1. 文件上传类型白名单校验
  2. 文件重命名
  3. 复制文件到目标目录
  4. 批量插入交付件记录
  5. 更新事件实际完成日期
- **数据校验**：文件扩展名白名单校验

#### `List<PresalesTask> queryPresalesTaskList(int presalesId, int projectType)`
- **功能描述**：查询售前项目任务列表（含交付件）
- **核心算法**：聚合新交付件和历史交付件

#### `private void updatePresalesDuration(int presalesId)`
- **功能描述**：异步更新售前项目各阶段耗时
- **核心算法**：新线程执行 `presalesDao.updatePresalesDuration()`
- **注意事项**：⚠️ 新线程不在事务上下文中，可能导致数据不一致

### 补充方法文档（遗漏方法补全）

> 以下方法为前期文档遗漏，现按功能类别分组补全。源码行号标注格式：`接口:PresalesService.java:行号` / `实现:PresalesServiceImpl.java:行号`。

**【查询类方法】**

#### `List<PresalesProduct> queryPresalesProductByPresalesId(int presalesId)`
- **功能描述**：根据售前项目ID查询售前产品明细列表
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`presalesDao.queryPresalesProductByPresalesId()`
- **源码行号**：`接口:PresalesService.java:37` / `实现:PresalesServiceImpl.java:165`

#### `List<Presales> queryPresalesList(Presales presales, DisplayParam displayParam) throws UnsupportedEncodingException`
- **功能描述**：按条件分页查询售前项目列表
- **事务类型**：无事务（query*前缀）
- **参数**：
  - `presales`：查询条件对象
  - `displayParam`：分页显示参数
- **调用的DAO方法**：`presalesDao.queryPresalesList()`
- **源码行号**：`接口:PresalesService.java:50` / `实现:PresalesServiceImpl.java:294`

#### `List<PresalesComment> queryPresalesCommentList(int presalesId)`
- **功能描述**：查询售前项目流程审批意见列表
- **事务类型**：无事务（query*前缀）
- **核心算法**：以 `Presales` 类名为 procdefKey 查询活动审批意见
- **调用的DAO方法**：`presalesDao.queryActComment(presalesId, "Presales")`
- **源码行号**：`接口:PresalesService.java:57` / `实现:PresalesServiceImpl.java:299`

#### `int queryPresalesQuesnaireId(Presales presales)`
- **功能描述**：查询当前售前任务保存的问卷ID
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`presalesDao.queryQuesnaireIdBycallbackId()`
- **源码行号**：`接口:PresalesService.java:85` / `实现:PresalesServiceImpl.java:484`

#### `List<ShipmentInfo> queryPresaleShipmentInfo(String projectCode)` / `List<ShipmentInfo> queryPresaleShipmentInfo(String projectCode, boolean containRma)`
- **功能描述**：查询售前测试发货信息
- **事务类型**：无事务（query*前缀）
- **参数**：
  - 重载1：默认不包含退货设备
  - 重载2：`containRma` 控制是否包含退货设备
- **调用的DAO方法**：`presalesDao.queryPresaleShipmentInfo()`
- **源码行号**：`接口:PresalesService.java:142,149` / `实现:PresalesServiceImpl.java:674,679`

#### `List<PresalesExportVO> queryPresalesExportData(Presales presales)`
- **功能描述**：查询售前项目导出数据
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`presalesDao.queryPresalesExportData()`
- **源码行号**：`接口:PresalesService.java:168` / `实现:PresalesServiceImpl.java:723`

#### `List<ProjectDeliver> queryProjectDeliverList(ProjectDeliver projectDeliver)`
- **功能描述**：查询项目交付件列表
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`projectDao.queryProjectDeliverList()`
- **源码行号**：`接口:PresalesService.java:181` / `实现:PresalesServiceImpl.java:782`

#### `List<Map<String, Object>> queryPresaleLend2SaleInfo(String presalesCode)`
- **功能描述**：查询售前项目借货转销售信息
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`presalesDao.queryPresaleLend2SaleInfo()`
- **源码行号**：`接口:PresalesService.java:196` / `实现:PresalesServiceImpl.java:684`

#### `List<Map<String, Object>> queryPresaleLend2RmaInfo(String presalesCode)`
- **功能描述**：查询售前项目借货转退货（RMA）信息
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`presalesDao.queryPresaleLend2RmaInfo()`
- **源码行号**：`接口:PresalesService.java:201` / `实现:PresalesServiceImpl.java:689`

#### `List<Map<String, Object>> selectPresalesTempAuthInfo(Map<String, Object> params)`
- **功能描述**：查询售前项目临时授权数据
- **事务类型**：无事务（select*前缀）
- **参数**：`params` 查询参数 Map
- **调用的DAO方法**：`presalesDao.selectPresalesTempAuthInfo()`
- **源码行号**：`接口:PresalesService.java:207` / `实现:PresalesServiceImpl.java:694`

**【状态更新/闭环相关方法】**

#### `void updateEndingPresalesProject(int presalesId)`
- **功能描述**：结束售前项目（正常闭环）
- **事务类型**：REQUIRED（update*前缀匹配）
- **核心算法**：构建 paramMap（applyState=流程通过、projectState=已闭环、endTime=当前时间），更新售前项目状态
- **调用的DAO方法**：`presalesDao.updatePresalesState()`
- **源码行号**：`接口:PresalesService.java:90` / `实现:PresalesServiceImpl.java:489`

#### `void updateEnding20PresalesProject(int presalesId)`
- **功能描述**：直接闭环售前项目（状态置为不予跟踪）
- **事务类型**：REQUIRED（update*前缀匹配）
- **核心算法**：构建 paramMap（applyState=流程通过、projectState=不予跟踪、endTime=当前时间），更新售前项目状态
- **调用的DAO方法**：`presalesDao.updatePresalesState()`
- **源码行号**：`接口:PresalesService.java:95` / `实现:PresalesServiceImpl.java:499`

#### `void updatePresalesTaskDeliverFiles(int taskId, String fileIds)`
- **功能描述**：更新项目计划任务对应的交付件ID
- **事务类型**：REQUIRED（update*前缀匹配）
- **核心算法**：校验 fileIds 和 taskId 非空/非0后委托 DAO 更新
- **调用的DAO方法**：`presalesDao.updatePresalesTaskDeliverFiles()`
- **源码行号**：`接口:PresalesService.java:120` / `实现:PresalesServiceImpl.java:642`

#### `void updatePresalesTask(Date taskFinshedTime, int presalesTaskId)` / `void updatePresalesTask(Date taskFinshedTime, String remark, int presalesTaskId)`
- **功能描述**：更新售前任务计划完成时间（含进展情况）
- **事务类型**：REQUIRED（update*前缀匹配）
- **核心算法**：
  1. 重载1（仅完成时间）：委托给重载2，传 remark=null
  2. 重载2（完成时间+备注）：直接调用 DAO 更新
- **调用的DAO方法**：`presalesDao.updatePresalesTask()`
- **源码行号**：`接口:PresalesService.java:126,163` / `实现:PresalesServiceImpl.java:649,655`

#### `void updatePresalesConfirmFileIds(int presalesId, String fileIds)`
- **功能描述**：更新交付件信息到售前项目主表（confirmFileIds 字段）
- **事务类型**：REQUIRED（update*前缀匹配）
- **核心算法**：校验 fileIds 不为空且 presalesId>0 后委托 DAO 更新
- **调用的DAO方法**：`presalesDao.updatePresalesConfirmFileIds()`
- **源码行号**：`接口:PresalesService.java:132` / `实现:PresalesServiceImpl.java:660`

#### `void updatePrealesFileIds(int presalesId, int taskId, int fileId)`
- **功能描述**：更新售前任务文件ID（注：方法名存在拼写错误 `Preales`）
- **事务类型**：REQUIRED（update*前缀匹配）
- **核心算法**：校验 fileId、presalesId、taskId 均 >0 后委托 DAO 更新
- **调用的DAO方法**：`presalesDao.updatePrealesFileIds()`
- **源码行号**：`接口:PresalesService.java:135` / `实现:PresalesServiceImpl.java:667`
- **注意事项**：⚠️ 方法名 `updatePrealesFileIds` 存在拼写错误（Preales 应为 Presales），但接口与实现保持一致

#### `int deleteDeliverById(int fileId)`
- **功能描述**：根据交付件ID删除交付件（置为失效），返回所属项目ID
- **事务类型**：REQUIRED（delete*前缀匹配）
- **核心算法**：
  1. 调用 DAO 将交付件置为失效
  2. 查询该交付件信息
  3. 更新事件实际完成日期
  4. 返回 projectId
- **调用的DAO方法**：`projectDao.deleteDeliverById()`, `projectDao.queryProjectDeliverById()`
- **调用的Service方法**：`this.updateEventActualFinishDateByTask()`（private）
- **返回值**：交付件所属的 projectId
- **源码行号**：`接口:PresalesService.java:186` / `实现:PresalesServiceImpl.java:787`

#### `void updateProjectDeliverById(ProjectDeliver projectDeliver)`
- **功能描述**：根据ID更新项目交付件信息
- **事务类型**：REQUIRED（update*前缀匹配）
- **调用的DAO方法**：`projectDao.updateProjectDeliverById()`
- **源码行号**：`接口:PresalesService.java:191` / `实现:PresalesServiceImpl.java:795`

---

## 12. CallBackServiceImpl

### 类概述
- 实现接口：`CallBackService`
- 事务代理Bean：`callBackServiceAgent`
- 依赖的DAO：`CallBackDao`, `PmClosedLoopDao`
- 跨Service依赖：`WorkFlowService`, `ProjectService`, `PmClosedLoopService`
- **特殊说明**：CallBackServiceImpl 的 `reSubmitCallBackFlow()` 方法名以 `re` 前缀开头，不匹配事务规则，但内部包含写操作（更新申请表单、重新提交流程、更新闭环流程状态），存在事务风险

### 方法列表

#### `void startCallBackFlow(CallBack callBack)`
- **功能描述**：启动回访流程
- **事务类型**：REQUIRED（start*前缀）
- **核心算法/处理逻辑**：
  1. 保存回访申请
  2. 启动Activiti流程（businessKey格式：`CallBack.callBackId.projectId`）
  3. 回写instId
  4. 办理第一个任务
  5. 添加审批意见
  6. 更新项目闭环流程状态为回访状态

#### `CallBack queryCallBackById(int callBackId)`
- **功能描述**：查询回访详情
- **事务类型**：无事务

#### `void insertCallBackQuesnaire(CallBack, PmClQuesnaireResultHeader, List<PmClQuesnaireResultLine>)`
- **功能描述**：插入回访问卷
- **事务类型**：REQUIRED（insert*前缀）
- **核心算法**：同售前问卷逻辑（插入头/行 → 查询是否已存在 → 更新或新增关联）

#### `CallBackQuesnaire queryCbQuesnaire(int quesnaireId)`
- **功能描述**：查询回访问卷详情
- **事务类型**：无事务

#### `int queryQuesnaireTemplateId(int quesnaireId)`
- **功能描述**：查询问卷模板ID
- **事务类型**：无事务

#### `void submitCallBackFlow(WorkflowCommonParam param, CallBack callBack)`
- **功能描述**：提交回访流程
- **事务类型**：REQUIRED（submit*前缀）
- **核心算法**：
  1. 获取流程变量
  2. 查找任务（先按角色查，再按用户查）
  3. 办理任务
  4. 添加审批意见
  5. 更新项目闭环流程状态

#### `void updateCallBackApplyState(int callBackId, int applyState)`
- **功能描述**：更新回访申请状态
- **事务类型**：REQUIRED（update*前缀）

#### `List<CallBackComment> queryCallBackComment(int callBackId)`
- **功能描述**：查询回访审批意见列表
- **事务类型**：无事务

#### `void reSubmitCallBackFlow(WorkflowCommonParam param, CallBack callBack)`
- **功能描述**：重新提交回访流程
- **事务类型**：无事务（re*不匹配事务前缀）
- **核心算法**：更新申请表单 → 重新提交流程 → 更新闭环流程状态
- **注意事项**：⚠️ 此方法包含多个写操作但无事务保护，若中间步骤失败可能导致数据不一致

#### `private void updateProjectCloseProcessState(int projectId, String closeProcessState)`
- **功能描述**：更新项目闭环流程状态
- **核心算法**：
  1. 通过SpringContext获取projectService和pmClosedLoopService代理Bean
  2. 查询项目信息和闭环任务
  3. 若无进行中的闭环任务，更新项目状态
- **异常处理**：Exception → e.printStackTrace()，静默处理
- **注意事项**：使用SpringContext.getBean()获取代理Bean，确保事务生效

---

## 13. PmClosedLoopServiceImpl

### 类概述
- 实现接口：`PmClosedLoopService`
- 事务代理Bean：`pmClosedLoopServiceAgent`
- 依赖的DAO：`PmClosedLoopDao`
- 跨Service依赖：`WorkFlowService`, `SendMailService`, `UserManageService`, `ProjectService`

### 方法列表

#### `String addPmCLApply(WorkflowCommonParam, PmClEvaluationHeader, Project)`
- **功能描述**：发起闭环申请（项目经理发起）
- **事务类型**：REQUIRED（add*前缀）
- **核心算法/处理逻辑**：
  1. **闭环流程状态机**：
     - 当前用户是服务经理：
       - 已通过回访或已有回访问卷 → 跳过回访，直接到工程人员（taskUserKey=CL_TASK_USER_4, evaResult=3, processStatus=CL_EVALU_TYPE_CL）
       - 未通过回访 → 到回访人员（taskUserKey=CL_TASK_USER_3, evaResult=2, processStatus=CL_EVALU_TYPE_CB）
     - 当前用户不是服务经理 → 到服务经理
  2. 插入回访头信息
  3. 若已有回访问卷，自动创建回访环节的测评记录
  4. 若为驳回任务，先结束驳回任务再启动新流程
  5. 启动Activiti流程
  6. 办理任务
  7. 发送邮件通知
  8. 更新项目闭环流程状态
- **调用的DAO方法**：`pmClosedLoopDao.addPmClEvaluationHeaderObj()`, `pmClosedLoopDao.updateEvaluationHeaderObj()`, `pmClosedLoopDao.updateEvaluationHeaderId()`
- **调用的其他Service方法**：`workFlowService.startProcess()`, `workFlowService.submitSelfTask()`, `workFlowService.findPersonalTask()`, `sendMailService.keepMailInfo()`, `projectService.insertOrUpdateProjectState()`

#### `String addSmCLApply(WorkflowCommonParam, PmClEvaluationHeader, Project)`
- **功能描述**：服务经理审核闭环申请
- **事务类型**：REQUIRED（add*前缀）
- **核心算法**：
  - 审核通过：判断是否已通过回访 → 跳过回访或到回访人员
  - 审核驳回：回到项目经理

#### `int addCbCLApplyQues(WorkflowCommonParam, PmClEvaluationHeader, Project, PmClQuesnaireResultHeader, List<PmClQuesnaireResultLine>)`
- **功能描述**：提交回访测评问卷（草稿保存）
- **事务类型**：REQUIRED（add*前缀）
- **核心算法**：
  - id=0：新增模式 → 插入回访头 → 插入问卷头 → 插入问卷行 → 认领任务
  - id≠0：更新模式 → 删除旧记录 → 重新插入

#### `int addCbCLApply(WorkflowCommonParam, PmClEvaluationHeader, Project)`
- **功能描述**：项目回访审核
- **事务类型**：REQUIRED（add*前缀）
- **核心算法**：
  - 审核通过 → 到工程人员
  - 审核驳回 → 回到项目经理
  - 无法回访 → 到服务经理

#### `int addClCLApply(WorkflowCommonParam, PmClEvaluationHeader, Project)`
- **功能描述**：项目闭环审核
- **事务类型**：REQUIRED（add*前缀）
- **核心算法**：
  - 审核通过 → 更新项目状态为已闭环
  - 审核驳回 → 回到项目经理

#### `void getProjectSefTaskId(List<Project>)` / `void getProjectPubTaskId(List<Project>)`
- **功能描述**：获取项目私有/公有任务ID
- **核心算法**：遍历项目列表，根据businessKey查询任务

#### `Map<String, Object> queryProcessVarMap(Project)` / `String queryTaskByBussinessKey(Project)` / `String queryTaskByBussinessKeyAndUser(Project, String)`
- **功能描述**：查询流程变量/任务

#### `void deletePmClEvaRecur(PmClEvaluationHeader)`
- **功能描述**：递归删除闭环测评记录（含问卷头和行）
- **异常处理**：
  | 异常类型 | 触发条件 | 处理方式 |
  |----------|----------|----------|
  | RuntimeException | 问卷结果头信息不存在 | 直接抛出 |

#### `private StringBuilder getNextAssignPer(String roleStr)`
- **功能描述**：获取下一级审核人员列表
- **核心算法**：遍历所有有效用户，匹配角色ID包含指定角色字符串
- **异常处理**：
  | 异常类型 | 触发条件 | 处理方式 |
  |----------|----------|----------|
  | RuntimeException | 未找到匹配角色的用户 | 直接抛出 |

#### `private void mailPerson(Project, int processStatus, PmClEvaluationHeader, int nowStatus, String nowUser)`
- **功能描述**：闭环流程邮件发送
- **核心算法**：
  1. 根据当前状态和流程状态组合确定邮件模板
  2. 收集收件人（角色匹配 + 项目相关人员）
  3. 闭环状态时额外添加项目组成员和回访人员
  4. 测试环境替换为开发测试邮件地址
- **异常处理**：
  | 异常类型 | 触发条件 | 处理方式 |
  |----------|----------|----------|
  | RuntimeException | 未获取到邮件发送人地址 | 直接抛出 |

#### `private void updateProjectCloseProcessState(Project project, int processStatus)`
- **功能描述**：更新项目闭环流程状态
- **核心算法**：
  1. 计算闭环流程状态值 = |processStatus| × 10
  2. 若状态为"项目跟踪"，检查是否可闭环
  3. 若状态≤"项目跟踪"，检查是否有进行中的回访流程
  4. 调用projectService更新状态

---

## 14. PmClosedLoopQuesnaireServiceImpl

### 类概述
- 实现接口：`PmClosedLoopQuesnaireService`
- 事务代理Bean：`pmClosedLoopQuesnaireServiceAgent`
- 依赖的DAO：`PmClosedLoopQuesnaireDao`

### 方法列表

#### `int insertQuesnaireHeader(PmClosedLoopQuesnaire)`
- **功能描述**：插入问卷模板头
- **事务类型**：REQUIRED（insert*前缀）
- **核心算法**：
  1. 查询当前最大编号
  2. 调用 `PmClosedLoopUtil.geneticSerialNumber()` 生成新编号
  3. 设置创建人和创建时间
  4. 插入记录

#### `void insertQuesnaireLineOptList(PmClosedLoopQuesnaireLine, List<PmClosedLoopQuesnaireOpt>)`
- **功能描述**：插入问卷题目和选项
- **事务类型**：REQUIRED（insert*前缀）
- **核心算法**：
  1. 插入题目行，设置状态为1
  2. 遍历选项列表，设置模板头ID和选项编号
  3. 问答题（CL_QUESNAIRE_LINE_TYPE_AQ）不需要选项，跳过选项插入

#### `void updateQuesLineOpt(PmClosedLoopQuesnaireLine, List<PmClosedLoopQuesnaireOpt>)`
- **功能描述**：更新问卷题目和选项（先删后插）
- **事务类型**：REQUIRED（update*前缀匹配事务规则）
- **核心算法**：删除旧题目 → 删除旧选项 → 插入新题目和选项
- **注意事项**：此方法内部调用 `insertQuesnaireLineOptList()`（同样为REQUIRED事务），先删后插的操作在同一事务中完成，确保数据一致性
- **异常处理**：
  | 异常类型 | 触发条件 | 处理方式 |
  |----------|----------|----------|
  | RuntimeException | 题目更新返回值≤0 | 直接抛出"题目更新错误！" |

#### `int deleteQuesLine(PmClosedLoopQuesnaireLine)`
- **功能描述**：删除问卷题目（含选项和序号更新）

#### `int deleteQuesHeader(PmClosedLoopQuesnaire)`
- **功能描述**：删除问卷模板（含所有题目和选项）
- **异常处理**：
  | 异常类型 | 触发条件 | 处理方式 |
  |----------|----------|----------|
  | RuntimeException | 删除问卷返回值≤0 | 直接抛出"删除问卷出错！" |

#### `int updateEffecticeStart(PmClosedLoopQuesnaire)`
- **功能描述**：生效问卷模板
- **核心算法**：
  1. 若为闭环建议类型，先失效其他同类问卷
  2. 生效当前问卷
  3. 任一步骤返回-1则返回-1

#### `int addQuestionnaireResult(PmClQuesnaireResultHeader, List<PmClQuesnaireResultLine>)`
- **功能描述**：添加问卷结果
- **事务类型**：REQUIRED（@Transactional注解 + add*前缀）
- **核心算法**：插入问卷头 → 插入问卷行

---

## 15. ReportServiceImpl

### 类概述
- 实现接口：`ReportService`
- 事务代理Bean：`reportServiceAgent`
- 依赖的DAO：`ReportDao`

### 方法列表

#### `Map<String, Double> queryAssignedRate(ReportQueryParam)`
- **功能描述**：查询项目经理指派率
- **核心算法**：
  1. 查询全部项目数和已指派项目数（按办事处分组）
  2. 计算各办事处指派率 = 已指派/全部 × 100
  3. 计算全国总指派率
  4. 处理除零和NaN/Infinite情况
  5. 保留两位小数

#### `Map<String, Double> queryTraceRate(ReportQueryParam)`
- **功能描述**：查询项目经理跟踪率
- **核心算法**：同queryAssignedRate

#### `List<QualityParam> queryQualityList(ReportQueryParam)`
- **功能描述**：查询质量管理数据
- **核心算法**：
  1. 创建临时表
  2. 查询各办事处闭环数据
  3. 查询无闭环项目的办事处
  4. 查询全国汇总数据
  5. 合并结果
- **异常处理**：finally块确保删除临时表

#### `Map<String, List<QualityParam>> queryTotalAndRemainderList(ReportQueryParam)`
- **功能描述**：查询全部和去除非直签督导的质量数据
- **核心算法**：两次查询（全部 + 去除非直签），使用临时表，finally确保清理

#### `Map<String, Double> queryCloseRate(ReportQueryParam)`
- **功能描述**：查询闭环率
- **核心算法**：同指派率算法，闭环数/新增数 × 100

#### `StatisticsSummarize queryStatisticsSummarize()`
- **功能描述**：查询统计汇总数据
- **核心算法**：5次DAO查询汇总

#### `ReportLineData statisticsTotalData(List<ReportLineData>)`
- **功能描述**：统计全国汇总数据
- **核心算法**：累加条件值和总值 → 计算百分比 → 去除尾部多余的.0

#### `void keepReportLineData()`
- **功能描述**：保存报表趋势数据（定时任务调用）
- **事务类型**：REQUIRED（keep*前缀）
- **核心算法**：
  1. 统计项目经理指派率 → 保存
  2. 统计项目经理跟踪率 → 保存
  3. 统计闭环新增比 → 保存
  4. 统计企业网项目实施方式占比 → 保存
  5. 统计质量管理数据 → 更新闭环总数 → 保存

#### `QualityParam queryTotalQuality(ReportQueryParam)`
- **功能描述**：查询全国质量汇总数据
- **核心算法**：使用临时表查询全国汇总，finally确保清理
- **事务类型**：无事务

#### `List<ReportLineData> queryReportLineAssignedData(ReportQueryParam)`
- **功能描述**：查询项目经理指派率趋势数据
- **事务类型**：无事务

#### `List<ReportLineData> queryReportLineTraceData(ReportQueryParam)`
- **功能描述**：查询项目经理跟踪率趋势数据
- **事务类型**：无事务

#### `List<ReportLineData> queryReportLineClosedData(ReportQueryParam)`
- **功能描述**：查询闭环率趋势数据
- **事务类型**：无事务

#### `List<ReportLineData> queryReportLineQualityData(ReportQueryParam)`
- **功能描述**：查询质量管理趋势数据
- **事务类型**：无事务

#### `List<Object> queryReportLineRemainderQualityDataAndTotalsize(ReportQueryParam)`
- **功能描述**：查询去除非直签督导的质量趋势数据及总数
- **事务类型**：无事务

#### `List<ReportLineData> queryReportLineImplData(ReportQueryParam)`
- **功能描述**：查询实施方式占比趋势数据
- **事务类型**：无事务

#### `void insertReportLineDataByList(List<ReportLineData>, String)`
- **功能描述**：批量插入报表趋势数据
- **事务类型**：REQUIRED（insert*前缀）

#### `String queryReportSettingTimes(String officeCode, String dataTypeCode)`
- **功能描述**：查询报表设置时间
- **事务类型**：无事务

#### `List<ReportLineData> queryReportTableAssignedData(ReportQueryParam)`
- **功能描述**：查询指派率报表表格数据
- **事务类型**：无事务

#### `List<ReportLineData> queryReportTableTraceData(ReportQueryParam)`
- **功能描述**：查询跟踪率报表表格数据
- **事务类型**：无事务

#### `List<ReportLineData> queryReportTableQualityData(ReportQueryParam)`
- **功能描述**：查询质量管理报表表格数据
- **事务类型**：无事务

#### `List<ReportLineData> queryReportTableClosedData(ReportQueryParam)`
- **功能描述**：查询闭环率报表表格数据
- **事务类型**：无事务

---

## 16. DataAnalysisServiceImpl

### 类概述
- 实现接口：`DataAnalysisService`
- 事务代理Bean：`dataAnalysisServiceAgent`
- 依赖的DAO：`DataAnalysisDao`

### 方法列表

#### `List<PmClCBData> quesyCbDataList(DataQueryParam)`
- **功能描述**：查询回访数据列表
- **事务类型**：无事务

---

## 17. WorkFlowServiceImpl

### 类概述
- 实现接口：`WorkFlowService`
- **Spring Bean**: `workFlowService`（⚠️ **未配置事务代理**，所有方法均无事务管理）
- 事务代理Bean：无（未配置ServiceAgent，不参与事务代理）
- 依赖的DAO：`WorkflowDao`
- 依赖的Activiti引擎：`RepositoryService`, `RuntimeService`, `TaskService`, `FormService`, `HistoryService`
- 跨Service依赖：`UserManageService`
- **特殊说明**：WorkFlowServiceImpl 是唯一未配置事务代理的核心Service，所有写操作（deployFlow、startProcess、submitTask等）均无事务保护。流程引擎操作依赖Activiti自身的事务管理，但DAO写操作（如addSelfActComment）不在事务中

### 方法列表

#### `void deployFlow(String fileName, File file)`
- **功能描述**：部署流程定义
- **核心算法**：读取ZIP文件 → 调用repositoryService部署
- **异常处理**：
  | 异常类型 | 触发条件 | 处理方式 |
  |----------|----------|----------|
  | FileNotFoundException | 文件不存在 | e.printStackTrace() |

#### `ProcessInstance startProcess(String processDefinitionKey, String businessKey, Map<String, Object> vars)`
- **功能描述**：启动流程实例
- **核心算法**：
  1. 设置认证用户ID
  2. 启动流程实例
  3. 清除认证用户ID

#### `void submitTask(WorkflowCommonParam param)`
- **功能描述**：提交任务（通用流程审批）
- **核心算法/处理逻辑**：
  1. 解析businessKey获取classType和objId
  2. 根据outcome判断审批结果，处理不同业务类型的驳回/通过逻辑（大量注释掉的代码）
  3. 构建审批意见DpComment
  4. **自动办理循环**：do-while循环处理当前任务和下一任务为同一办理人的情况
  5. 收集下一步办理人信息
- **注意事项**：大量业务类型处理代码已被注释掉，保留框架

#### `void submitSelfTask(WorkflowCommonParam param, Map<String, Object> vars)`
- **功能描述**：提交自定义任务
- **核心算法**：设置认证用户 → 添加评论 → 设置本地变量 → 完成任务

#### `void doSelfTask(Task task, String instId, String comment, Map<String, Object> vars)`
- **功能描述**：办理自定义任务
- **核心算法**：设置认证用户 → 添加评论 → 设置本地变量 → 完成任务

#### `List<SelfComment> getProcessComments(String taskId, String instId)`
- **功能描述**：获取流程审批意见列表
- **核心算法**：
  1. 查询流程实例的所有用户任务历史活动
  2. 遍历每个历史任务，查询评论
  3. 聚合评论信息（含用户真实姓名）

#### `Task getTaskIdByProcessInstanceId(String piid, String assignee)`
- **功能描述**：根据流程实例ID和办理人查询任务

#### `void assigneeTask(String taskId, String userId, String variableName)`
- **功能描述**：转办任务
- **核心算法**：设置任务持有人和办理人，更新流程变量

#### `Integer addSelfActComment(Integer objId, String procdefKey, String taskKey, String taskId, String instId, int result, String message, String nextAssignee, String nextAssigneeName)`
- **功能描述**：添加自定义审批意见
- **核心算法**：构建参数Map → 调用DAO插入 → 返回生成的ID

#### `void deleteProcessInstance(String proInstId, String comment)`
- **功能描述**：删除流程实例

#### `Map<String, Object> queryProcessVarMap(String taskId)`
- **功能描述**：查询流程变量

#### `boolean isExistNextNode(String taskId, String nodeName)`
- **功能描述**：判断当前节点是否存在指定名称的下一节点
- **核心算法**：获取流程定义 → 查找当前活动 → 遍历出站转换 → 匹配目标节点名称

#### `List<Deployment> listDeployments()`
- **功能描述**：查询所有流程部署信息
- **事务类型**：无事务

#### `List<ProcessDefinition> listProcessDefinition()`
- **功能描述**：查询所有流程定义信息
- **事务类型**：无事务

#### `void delDeployment(String deploymentId)`
- **功能描述**：删除流程部署
- **事务类型**：无事务

#### `InputStream getInputStream(String deploymentId, String imageName)`
- **功能描述**：获取流程图图片输入流
- **事务类型**：无事务

#### `List<Task> findPersonalTask(String userId)` / `findPersonalTask(String userId, String procInstId)`
- **功能描述**：查询个人任务列表（可按流程实例ID过滤）
- **事务类型**：无事务

#### `TaskFormData getTaskFromData(String taskId)`
- **功能描述**：获取任务表单数据
- **事务类型**：无事务

#### `String getBusinessObjId(String taskId)`
- **功能描述**：根据任务ID获取业务对象ID
- **核心算法**：从流程变量中获取classType，拼接businessKey解析objId
- **事务类型**：无事务

#### `void submitTaskNoComment(WorkflowCommonParam, Map<String, Object>)`
- **功能描述**：提交任务（不带审批意见）
- **事务类型**：无事务
- **核心算法**：设置流程变量 → 完成任务

#### `ProcessDefinition getProcessDefinitionByTaskId(String taskId)`
- **功能描述**：根据任务ID获取流程定义
- **事务类型**：无事务

#### `List<Task> findAllRunTask()`
- **功能描述**：查询所有运行中的任务
- **事务类型**：无事务

#### `List<HistoricProcessInstance> findHisProcess()`
- **功能描述**：查询历史流程实例
- **事务类型**：无事务

#### `List<Task> getTaskByInstId(String procInstId)`
- **功能描述**：根据流程实例ID查询任务
- **事务类型**：无事务

#### `void submitTaskSystemAuto(Task task)`
- **功能描述**：系统自动提交任务
- **核心算法**：设置认证用户 → 完成任务
- **事务类型**：无事务

#### `List<HistoricTaskInstance> findHistoricPersonalTask(String userId)`
- **功能描述**：查询用户历史任务
- **事务类型**：无事务

#### `String getHistBusinessObjId(String instId)`
- **功能描述**：根据历史流程实例ID获取业务对象ID
- **核心算法**：从历史流程变量中获取classType和objId
- **事务类型**：无事务

#### `String getFormKey(String instId)`
- **功能描述**：根据流程实例ID获取表单Key
- **事务类型**：无事务

#### `ProcessDefinition getProcessDefinitionByClassType(String simpleName)`
- **功能描述**：根据类名获取流程定义
- **事务类型**：无事务

#### `List<Task> queryCurrentApprover(String instId)`
- **功能描述**：查询当前审批人
- **事务类型**：无事务

#### `List<DpActProcDesc> findRunSelfTaskList(DisplayParam, DpActProcDesc)`
- **功能描述**：查询运行中的个人任务列表（含流程描述）
- **事务类型**：无事务

#### `List<DpActProcType> findDpActProcTypeList()`
- **功能描述**：查询流程类型列表
- **事务类型**：无事务

#### `List<DpActProcDesc> findHisSelfTaskList(DisplayParam, DpActProcDesc)`
- **功能描述**：查询历史个人任务列表
- **事务类型**：无事务

#### `void insertProcdefDelegate(ProcdefDelegate)`
- **功能描述**：插入流程委派规则
- **事务类型**：无事务（insert*前缀匹配事务规则，但Service未配置事务代理）

#### `List<ProcdefDelegate> findProcdefDelegateList(ProcdefDelegate)`
- **功能描述**：查询流程委派规则列表
- **事务类型**：无事务

#### `ProcdefDelegate findProcdefDelegateById(int)`
- **功能描述**：根据ID查询流程委派规则
- **事务类型**：无事务

#### `void updateProcdefDelegate(ProcdefDelegate)`
- **功能描述**：更新流程委派规则
- **事务类型**：无事务（update*前缀匹配事务规则，但Service未配置事务代理）

#### `List<DpActProcDesc> getRunTask(DpActProcDesc dpActProcDesc, DisplayParam displayParam)`
- **功能描述**：获取运行中任务（含流程描述）
- **事务类型**：无事务
- **源码行号**：`WorkFlowService.java:314`
- **签名修正**：原误标为 `getRunTask(DpActProcDesc, String)`，第二参数实际为 `DisplayParam` 类型（流程变量展示参数对象）

#### `List<DpActProcDesc> getRunVariable(DpActProcDesc dpActProcDesc, DisplayParam displayParam)`
- **功能描述**：获取运行中流程变量
- **事务类型**：无事务
- **源码行号**：`WorkFlowService.java:322`
- **签名修正**：原误标为 `getRunVariable(DpActProcDesc, String)`，第二参数实际为 `DisplayParam` 类型

#### `List<DpActProcDesc> findRunVariableList(DisplayParam, DpActProcDesc)`
- **功能描述**：查询运行中流程变量列表
- **事务类型**：无事务

#### `Task queryTaskByBussinessKey(String businessKey)`
- **功能描述**：根据businessKey查询任务
- **事务类型**：无事务

#### `Task queryTaskByBussinessKeyUser(String businessKey, String userId)`
- **功能描述**：根据businessKey和用户查询任务
- **事务类型**：无事务

#### `Task queryPubTaskByBussinessKeyUser(String businessKey, String userId)`
- **功能描述**：根据businessKey和用户查询公有任务
- **事务类型**：无事务

#### `void claimTask(String taskId, String userId)`
- **功能描述**：认领任务
- **事务类型**：无事务

#### `void setVariable(String instId, String variableName, String oldValue, String newValue)`
- **功能描述**：设置流程变量（含旧值比对）
- **事务类型**：无事务

#### `List<Task> queryAllSelfTaskList(String userId)` / `queryAllPubTaskList(String userId)`
- **功能描述**：查询所有私有/公有任务
- **事务类型**：无事务

#### `List<HistoricProcessInstance> queryHisProcessInstanceByIds(Set<String>)`
- **功能描述**：根据ID集合查询历史流程实例
- **事务类型**：无事务

#### `Procdef getProcdef(Procdef)`
- **功能描述**：查询流程定义信息
- **事务类型**：无事务

#### `Integer addSelfActComment(...)` （多个重载版本）
- **功能描述**：添加自定义审批意见（4个重载版本，参数不同）
- **核心算法**：构建参数Map → 调用DAO插入 → 返回生成的ID
- **事务类型**：无事务（add*前缀匹配事务规则，但Service未配置事务代理）

#### `void updateSelfActComment(int commentId, String taskId, String instId)`
- **功能描述**：更新审批意见的taskId和instId
- **事务类型**：无事务

#### `List<ActComment> queryActComment(int objId, String procdefKey)`
- **功能描述**：查询审批意见列表
- **事务类型**：无事务

#### `void updateApplytableInfo(String tableName, String instId, int objId, String objColumn)`
- **功能描述**：更新申请表的流程实例ID
- **事务类型**：无事务（update*前缀匹配事务规则，但Service未配置事务代理）

#### `void querymaxDefinitionObjByKey(String keyString, WorkflowCommonParam)`
- **功能描述**：查询流程定义最大版本信息
- **事务类型**：无事务

---

## 18. WorkSpaceServiceImpl

### 类概述
- 实现接口：`WorkSpaceService`
- 事务代理Bean：`workspaceServiceAgent`
- 依赖的DAO：`WorkSpaceDao`
- 跨Service依赖：`PmClosedLoopService`, `WorkFlowService`, `BasicDataService`

### 方法列表

#### `List<DpActProcDesc> queryPmCLTaskList()`
- **功能描述**：查询项目闭环待办任务列表
- **核心算法/处理逻辑**：
  1. 获取当前用户的所有私有和公有任务
  2. 查询闭环测评头信息Map
  3. 查询闭环流程类型基础数据
  4. 遍历任务集合，匹配闭环流程：
     - 获取流程变量中的projectCode和objId
     - 匹配测评头信息
     - 根据测评类型和状态确定待办类型名称
  5. 构建待办描述列表
- **异常处理**：
  | 异常类型 | 触发条件 | 处理方式 |
  |----------|----------|----------|
  | RuntimeException | 闭环信息基础数据为空 | 直接抛出"获取闭环信息出错" |
  | RuntimeException | 测评类型为0 | 直接抛出"待办事项出错" |

#### `List<DpActProcDesc> queryPmCLHisTaskList()`
- **功能描述**：查询项目闭环历史任务列表

#### `List<DpActProcDesc> queryPmTaskList(TaskQueryParam, DisplayParam)`
- **功能描述**：查询项目管理待办任务列表
- **核心算法**：根据用户角色过滤：
  - 工程管理部/管理员：查看全部
  - 服务经理：只看自己的
  - 项目经理：只看自己的
  - 其他角色：返回空列表

#### `List<Map<String, Object>> querySubcontractTaskList(Map<String, String> queryParams)`
- **功能描述**：查询转包待办任务列表
- **核心算法/处理逻辑**：
  1. 根据用户角色构建不同的查询条件组（roleGroups）
  2. 工程管理员 → emRole + emlRole
  3. 回访人员 → cbRole
  4. 区域负责人 → zrRole
  5. 服务经理 → smRole + profitSmRole + parentSmRole + 被驳回的项目列表
  6. 财务人员 → role_财务
  7. 合并所有查询结果

#### `List<Notification> checkNotificationList(String username)` / `void updateNotificationState(int notifyStateId)`
- **功能描述**：检查/更新通知状态

#### `List<DpActProcDesc> queryCallBackTaskList()`
- **功能描述**：查询回访待办任务列表
- **核心算法**：查询回访流程类型的个人任务，匹配回访申请信息
- **事务类型**：无事务

#### `List<DpActProcDesc> queryPresalesTaskList()`
- **功能描述**：查询售前待办任务列表
- **核心算法**：查询售前流程类型的个人任务
- **事务类型**：无事务

#### `List<DpActProcDesc> queryCallbackHisList()`
- **功能描述**：查询回访历史任务列表
- **事务类型**：无事务

#### `List<ProbParam> queryProbTaskList()`
- **功能描述**：查询技术公告待办任务列表
- **核心算法**：根据用户角色查询不同类型的技术公告任务（管理员/支持人员/研发人员）
- **事务类型**：无事务

#### `List<DpActProcDesc> queryProjectBackTaskList()`
- **功能描述**：查询项目回退确认任务列表
- **事务类型**：无事务

#### `List<DpActProcDesc> queryProjectTrackTaskList()`
- **功能描述**：查询项目不予跟踪确认任务列表
- **事务类型**：无事务

#### `List<Notification> queryNotifyList(TaskQueryParam, DisplayParam)`
- **功能描述**：查询系统通知列表
- **核心算法**：根据用户名查询通知，支持按类型/状态/时间筛选
- **事务类型**：无事务

#### `List<DpActProcDesc> querySelfHistoryTaskList(TaskQueryParam, DisplayParam)`
- **功能描述**：查询个人历史任务列表
- **核心算法**：根据用户角色查询不同类型的历史任务
- **事务类型**：无事务

#### `List<DpActProcDesc> queryProjectSupervisionTask(HashMap)`
- **功能描述**：查询项目督查待办任务列表
- **核心算法**：查询督查流程类型的个人任务
- **事务类型**：无事务

#### `List<DpActProcDesc> queryActRunTask(String taskType)`
- **功能描述**：查询运行中的任务（按任务类型）
- **事务类型**：无事务

#### `List<String> getprojectcodelistbyusername(String usernamenow)` / `List<Integer> getprojectcodelistfrombeforebyusername(String usernamenow)`
- **功能描述**：根据用户名查询项目编码列表
- **事务类型**：无事务
- **源码行号**：`WorkSpaceService.java:15`、`WorkSpaceService.java:18`
- **签名修正**：原误将 `getprojectcodelistfrombeforebyusername` 返回类型标为 `List<String>`，源码实际返回 `List<Integer>`（项目ID列表，非编码字符串列表）。`getprojectcodelistbyusername` 返回 `List<String>` 正确

#### `String getprojectbyapplyid(int)` / `getprojectbyapplyidorder(int)`
- **功能描述**：根据申请ID查询项目编码
- **事务类型**：无事务

#### `List<Integer> getapplyidsfromorderbyusername(String)`
- **功能描述**：根据用户名查询申请ID列表
- **事务类型**：无事务

#### `List<String> querybusinessorderprojectcodelist(String)`
- **功能描述**：查询商务订单项目编码列表
- **事务类型**：无事务

#### `String queryProductFirstCodeByUsername(String)` / `queryConcatFirstCode(String)`
- **功能描述**：查询产品首选编码
- **事务类型**：无事务

---

## 19. SendMailServiceImpl

### 类概述
- 实现接口：`SendMailService`
- 事务代理Bean：`sendMailServiceAgent`
- 依赖的DAO：`SendMailDao`

### 方法列表

#### `void keepMailInfo(MailSenderInfo info)`
- **功能描述**：保存邮件信息到数据库（异步发送）
- **事务类型**：REQUIRED（keep*前缀）
- **核心算法/处理逻辑**：
  1. 若期望发送时间为空，设置为当前时间
  2. 调用DAO保存邮件信息
- **注意事项**：邮件实际发送由定时任务异步处理，此方法仅写入数据表

---

## 20. ProbManageServiceImpl

### 类概述
- 实现接口：`ProbManageService`
- 事务代理Bean：`probManageServiceAgent`
- 依赖的DAO：`ProbManageDao`, `UserManageDao`, `BasicDataDao`

### 方法列表

#### `int saveProb(Prob prob, List<SoftVersion> softVersionList, String root) throws IOException`
- **功能描述**：保存技术公告
- **事务类型**：REQUIRED（@Transactional注解 + save*前缀）
- **核心算法/处理逻辑**：
  1. 解析产品型号临时参数（JSON格式）
  2. 提取产品型号集合，拼接为productType
  3. 保存主表记录
  4. 保存软件版本信息
  5. 保存产品型号信息
  6. 草稿状态（status=0）直接返回
  7. 发送邮件通知技术公告管理员审核

#### `void updateProb(Prob prob, List<SoftVersion> softVersionList)`
- **功能描述**：更新技术公告
- **事务类型**：REQUIRED（@Transactional注解 + update*前缀）
- **核心算法/处理逻辑**：
  1. 非草稿状态下，非管理员将状态改为待确认（8）
  2. 更新主表
  3. 更新软件版本和产品型号
  4. 草稿状态直接返回
  5. **邮件通知逻辑**：
     - 管理员 + 已拒绝 → 通知任务创建者
     - 管理员 + 其他状态 → 通知tsc/sp/pdt_ld/xteam群组
     - 非管理员 → 通知技术公告管理员

#### `void updateProbSoftVersion(List<SoftVersion>, int probId)`
- **功能描述**：更新软件版本信息
- **事务类型**：REQUIRED（@Transactional注解 + update*前缀）
- **核心算法**：失效原有版本 → 新增版本（或仅失效）

#### `void updateProbProduct(Prob, List<? extends ProbProduct>)`
- **功能描述**：更新产品型号信息
- **事务类型**：REQUIRED（@Transactional注解 + update*前缀）
- **核心算法**：失效原有产品型号 → 新增产品型号

#### `void insertBatchProbRestoreTask(ProbRestore, List<ProbRestore>, String root) throws IOException`
- **功能描述**：批量插入技术公告跟踪任务
- **核心算法**：
  1. 更新公告状态为解决中（5）
  2. 插入流程过程数据
  3. 插入子任务
  4. 发送邮件通知（tsc/xteam/pdt_ld + 根据公告类型选择sp或办事处用服）

#### `void updateProbRestoreTask(ProbRestore, String restoreIds, int isProbAdmin) throws IOException`
- **功能描述**：更新技术公告跟踪任务
- **核心算法**：
  1. 插入流程过程数据
  2. 更新办理人
  3. 根据角色类型更新任务状态
  4. 发送邮件通知（根据角色和任务状态选择不同收件人）

#### `String queryNextProbNum()`
- **功能描述**：生成下一个技术公告编号
- **核心算法**：`SP.yyyy` + 4位序号（0001起）

#### `void updateProbStatus(Prob prob)`
- **功能描述**：更新技术公告状态
- **核心算法**：更新状态 → 发送邮件通知

#### `void readLog(int probId, int status)`
- **功能描述**：记录阅读日志
- **核心算法**：新线程异步插入阅读记录
- **注意事项**：⚠️ 新线程不在事务上下文中

#### `List<? extends Object> selectProductItemListByParams(Map<String, Object> commonMap)`
- **功能描述**：根据参数查询产品条目列表
- **核心算法**：使用ProductItemExampleBuilder构建查询条件

#### `List<? extends Object> selectProductItemListFilteredByParams(Map<String, Object> commonMap)`
- **功能描述**：根据搜索条件过滤查询产品条目
- **核心算法**：使用ProductItemExampleBuilder构建过滤条件，从SystemContext获取过滤器配置

#### `List<? extends Object> selectProductItemListByItemSearch(String itemSearch, String itemSearchExclude)`
- **功能描述**：产品条目搜索
- **核心算法**：
  1. 空格分隔搜索词
  2. 每段条件对产品编码/型号/描述进行OR模糊查询
  3. 多个搜索词之间AND连接
  4. 添加排除条件
  5. 构建查询并执行

### 补充方法文档（遗漏方法补全）

> 以下方法为前期文档遗漏，现按功能类别分组补全。源码行号标注格式：`接口:ProbManageService.java:行号` / `实现:ProbManageServiceImpl.java:行号`。

**【技术公告查询类方法】**

#### `List<Prob> queryProbList(Prob prob, DisplayParam displayParam)`
- **功能描述**：查询技术公告列表
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`probManageDao.queryProbList()`
- **源码行号**：`接口:ProbManageService.java:43` / `实现:ProbManageServiceImpl.java:119`

#### `Prob queryOneProb(Prob prob)`
- **功能描述**：查询单个技术公告信息
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`probManageDao.queryOneProb()`
- **源码行号**：`接口:ProbManageService.java:49` / `实现:ProbManageServiceImpl.java:124`

#### `Map<Integer, String> queryProbFileMap(int probId)`
- **功能描述**：查询技术公告的附件 Map（附件ID → 路径）
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`probManageDao.queryProbFileMap()`
- **源码行号**：`接口:ProbManageService.java:85` / `实现:ProbManageServiceImpl.java:272`

#### `List<ProbParam> queryExportProbList(Map<Object, Object> params)`
- **功能描述**：查询导出技术公告数据
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`probManageDao.queryExportProbList()`
- **源码行号**：`接口:ProbManageService.java:175` / `实现:ProbManageServiceImpl.java:902`

#### `void deleteProbInfo(int probId)`
- **功能描述**：删除技术公告
- **事务类型**：REQUIRED（delete*前缀匹配）
- **调用的DAO方法**：`probManageDao.deleteProbInfo()`
- **源码行号**：`接口:ProbManageService.java:121` / `实现:ProbManageServiceImpl.java:465`

**【软件版本相关方法】**

#### `List<SoftVersion> checkSoftVersionList(SoftVersion softVersion)`
- **功能描述**：检索符合条件的软件版本
- **事务类型**：无事务（check*前缀）
- **调用的DAO方法**：`probManageDao.checkSoftVersionList()`
- **源码行号**：`接口:ProbManageService.java:61` / `实现:ProbManageServiceImpl.java:213`

#### `List<SoftVersion> querySoftVersionList(int probId)` / `List<SoftVersion> querySoftVersionList(SoftVersion softVersion)`
- **功能描述**：查询受技术公告影响的软件版本列表
- **事务类型**：无事务（query*前缀）
- **参数**：
  - 重载1：按技术公告ID查询
  - 重载2：按 SoftVersion 条件查询
- **调用的DAO方法**：`probManageDao.querySoftVersionList()`
- **源码行号**：`接口:ProbManageService.java:67,73` / `实现:ProbManageServiceImpl.java:218,223`

#### `void batchAddSoftVersion(List<SoftVersion> softVersions)`
- **功能描述**：批量导入软件版本信息
- **事务类型**：REQUIRED（add*前缀匹配，batchAdd* 含 add 前缀）
- **调用的DAO方法**：`probManageDao.batchAddSoftVersion()`
- **源码行号**：`接口:ProbManageService.java:180` / `实现:ProbManageServiceImpl.java:907`

**【技术公告修复任务相关方法】**

#### `List<ProbRestore> queryProbRestoreList(ProbRestore probRestore, DisplayParam restoreDisplayParam)`
- **功能描述**：查询设备修复情况的数据对象集合
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`probManageDao.queryProbRestoreList()`
- **源码行号**：`接口:ProbManageService.java:92` / `实现:ProbManageServiceImpl.java:277`

#### `List<ProbRestore> queryProbRestoreTaskList(ProbRestore probRestore, DisplayParam restoreDisplayParam) throws UnsupportedEncodingException`
- **功能描述**：查询技术公告修复子任务列表
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`probManageDao.queryProbRestoreTaskList()`
- **源码行号**：`接口:ProbManageService.java:108` / `实现:ProbManageServiceImpl.java:372`

#### `List<ProbRestore> queryProbRestoreTaskProjectList(ProbRestore probRestore, DisplayParam restoreDisplayParam) throws UnsupportedEncodingException`
- **功能描述**：查询子任务涉及的项目列表
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`probManageDao.queryProbRestoreTaskProjectList()`
- **源码行号**：`接口:ProbManageService.java:170` / `实现:ProbManageServiceImpl.java:378`

#### `void bacthDeleteProbRestores(String probRestoreIds)`
- **功能描述**：批量删除子任务（注：方法名存在拼写错误 `bacth`）
- **事务类型**：无事务（bacth*不匹配任何事务前缀；虽然语义为删除，但前缀不匹配）
- **参数**：`probRestoreIds` 逗号分隔的子任务ID
- **调用的DAO方法**：`probManageDao.bacthDeleteProbRestores()`
- **源码行号**：`接口:ProbManageService.java:157` / `实现:ProbManageServiceImpl.java:856`
- **注意事项**：⚠️ 方法名 `bacthDeleteProbRestores` 存在拼写错误（bacth 应为 batch），且前缀不匹配事务规则导致无事务保护

**【邮件通知相关方法】**

#### `void keepRelaseEmail(Prob prob, List<SoftVersion> softVersionList, String remark, String root, String files, String bccs) throws IOException`
- **功能描述**：保存发布邮件（无模板版本，手动拼接邮件内容）
- **事务类型**：REQUIRED（keep*前缀匹配）
- **参数**：
  - `prob`：技术公告主信息（必须要有主键）
  - `softVersionList`：软件版本列表（可为空，为空时查询数据库）
  - `remark`：邮件备注/主题补充
  - `root`：根目录（用于附件路径）
  - `files`：附件路径（为空时查询公告附件）
  - `bccs`：密送邮件地址
- **核心算法**：
  1. 拼接邮件主题：`【工程项目管理系统-技术公告发布】` + 公告编号 + 主题
  2. 构建邮件内容（含跟踪者、状态、严重级别、影响版本、产品类型、描述、解决方案、签名）
  3. 加载 signature.properties 签名配置
  4. 处理附件列表（files 为空时查询公告附件）
  5. 调用 `NotificationTemplateUtil.keepMailNoTemplate()` 发送
- **调用的DAO方法**：`probManageDao.querySoftVersionList()`, `probManageDao.queryProbFileList()`
- **调用的工具类**：`NotificationTemplateUtil.keepMailNoTemplate()`
- **源码行号**：`接口:ProbManageService.java:136` / `实现:ProbManageServiceImpl.java:753`

**【周报相关方法】**

#### `void insertProbTaskWeekly(int fileId, int probId, String root, String weeklyPath) throws IOException`
- **功能描述**：保存技术公告进展周报并发送邮件通知
- **事务类型**：REQUIRED（insert*前缀匹配）
- **参数**：
  - `fileId`：周报文件ID
  - `probId`：技术公告ID
  - `root`：根目录
  - `weeklyPath`：周报文件路径
- **核心算法**：
  1. 保存周报记录到数据库
  2. 查询技术公告信息
  3. 获取 tsc 群组邮件地址（prob.execute.mail）
  4. 调用 `keepRelaseEmail()` 发送邮件（主题为"任务进展周报"）
- **调用的DAO方法**：`probManageDao.insertProbTaskWeekly()`, `probManageDao.queryOneProb()`
- **调用的Service方法**：`this.keepRelaseEmail()`
- **源码行号**：`接口:ProbManageService.java:145` / `实现:ProbManageServiceImpl.java:839`

#### `List<ProbRestoreWeekly> queryProbWeekly(int probId, String username)`
- **功能描述**：查询技术公告进展附件列表
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`probManageDao.queryProbWeekly()`
- **源码行号**：`接口:ProbManageService.java:152` / `实现:ProbManageServiceImpl.java:851`

**【统计报表相关方法】**

#### `List<ProbStatistic> queryProbStatisticList(ProbStatistic probStatistic, DisplayParam displayParam)`
- **功能描述**：查询已维护的项目软件版本统计表
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`probManageDao.queryProbStatisticList()`
- **源码行号**：`接口:ProbManageService.java:188` / `实现:ProbManageServiceImpl.java:912`

#### `List<ProbStatistic> queryProbStatisticListWithReport(ProbStatistic probStatistic, DisplayParam displayParam, List<ReportLineData> reportLineDatas)`
- **功能描述**：查询已维护的项目软件版本统计表（附带报表数据）
- **事务类型**：无事务（query*前缀）
- **参数**：`reportLineDatas` 报表行数据列表
- **调用的DAO方法**：`probManageDao.queryProbStatisticListWithReport()`
- **源码行号**：`接口:ProbManageService.java:196` / `实现:ProbManageServiceImpl.java:917`

#### `List<Project> queryProbStatisticProjectList(ProbStatistic probStatistic, DisplayParam displayParam)`
- **功能描述**：查询原厂直服的项目列表
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`probManageDao.queryProbStatisticProjectList()`
- **源码行号**：`接口:ProbManageService.java:204` / `实现:ProbManageServiceImpl.java:923`

#### `List<?> queryContractShipmentSoftList(ProbStatistic probStatistic, DisplayParam displayParam)`
- **功能描述**：查询合同发货软件版本列表
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`probManageDao.queryContractShipmentSoftList()`
- **源码行号**：`接口:ProbManageService.java:211` / `实现:ProbManageServiceImpl.java:928`

**【阅读日志相关方法】**

#### `void insertProbReadLog(ProbReadLog probReadLog)`
- **功能描述**：插入技术公告阅读日志
- **事务类型**：REQUIRED（insert*前缀匹配）
- **调用的DAO方法**：`probManageDao.insertProbReadLog()`
- **源码行号**：`接口:ProbManageService.java:220` / `实现:ProbManageServiceImpl.java:945`

#### `List<ProbReadLog> queryProbReadLogList(ProbReadLog probReadLog, DisplayParam displayParam)`
- **功能描述**：查询技术公告阅读日志列表（含权限过滤）
- **事务类型**：无事务（query*前缀）
- **核心算法**：非管理员/技术公告管理员/技术支持人员角色时，强制设置 reader 为当前登录用户（仅查询自己的阅读记录）
- **调用的DAO方法**：`probManageDao.queryProbReadLogList()`
- **源码行号**：`接口:ProbManageService.java:227` / `实现:ProbManageServiceImpl.java:950`

**【产品组件 ProductComponent CRUD 方法】**

#### `ProductComponent selectProductComponentById(Integer id)`
- **功能描述**：根据ID查询产品组件
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`probManageDao.selectProductComponentById()`
- **源码行号**：`接口:ProbManageService.java:233` / `实现:ProbManageServiceImpl.java:963`

#### `ProductComponentVO selectProductComponentVOById(Integer id)`
- **功能描述**：根据ID查询产品组件 VO（含关联信息）
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`probManageDao.selectProductComponentVOById()`
- **源码行号**：`接口:ProbManageService.java:239` / `实现:ProbManageServiceImpl.java:968`

#### `List<ProductComponent> selectProductComponentList(ProductComponent component)`
- **功能描述**：查询产品组件列表
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`probManageDao.selectProductComponentList()`
- **源码行号**：`接口:ProbManageService.java:245` / `实现:ProbManageServiceImpl.java:973`

#### `List<ProductComponentVO> selectProductComponentListPageable(ProductComponentPageParam pageParam)`
- **功能描述**：分页查询产品组件列表
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`probManageDao.selectProductComponentListPageable()`
- **源码行号**：`接口:ProbManageService.java:251` / `实现:ProbManageServiceImpl.java:978`

#### `Integer countProductComponentListPageable(ProductComponentPageParam pageParam)`
- **功能描述**：统计分页查询产品组件列表总数
- **事务类型**：无事务（count*为查询统计方法）
- **调用的DAO方法**：`probManageDao.countProductComponentListPageable()`
- **源码行号**：`接口:ProbManageService.java:257` / `实现:ProbManageServiceImpl.java:983`

#### `Integer insertProductComponent(ProductComponent component)`
- **功能描述**：插入产品组件
- **事务类型**：REQUIRED（insert*前缀匹配）
- **调用的DAO方法**：`probManageDao.insertProductComponent()`
- **源码行号**：`接口:ProbManageService.java:262` / `实现:ProbManageServiceImpl.java:988`

#### `Integer insertProductComponentSelective(ProductComponent component)`
- **功能描述**：选择性插入产品组件（仅插入非空字段）
- **事务类型**：REQUIRED（insert*前缀匹配）
- **调用的DAO方法**：`probManageDao.insertProductComponentSelective()`
- **源码行号**：`接口:ProbManageService.java:268` / `实现:ProbManageServiceImpl.java:993`

#### `Integer insertOrUpdateProductComponentSelective(ProductComponent component)`
- **功能描述**：选择性插入或更新产品组件
- **事务类型**：REQUIRED（insert*前缀匹配）
- **调用的DAO方法**：`probManageDao.insertOrUpdateProductComponentSelective()`
- **源码行号**：`接口:ProbManageService.java:274` / `实现:ProbManageServiceImpl.java:998`

#### `void updateProductComponentById(ProductComponent component)`
- **功能描述**：根据ID更新产品组件
- **事务类型**：REQUIRED（update*前缀匹配）
- **调用的DAO方法**：`probManageDao.updateProductComponentById()`
- **源码行号**：`接口:ProbManageService.java:285` / `实现:ProbManageServiceImpl.java:1003`

#### `void updateProductComponentByIdSelective(ProductComponent component)`
- **功能描述**：根据ID选择性更新产品组件（仅更新非空字段）
- **事务类型**：REQUIRED（update*前缀匹配）
- **调用的DAO方法**：`probManageDao.updateProductComponentByIdSelective()`
- **源码行号**：`接口:ProbManageService.java:289` / `实现:ProbManageServiceImpl.java:1008`

#### `void deleteProductComponentById(Integer id)`
- **功能描述**：根据ID删除产品组件
- **事务类型**：REQUIRED（delete*前缀匹配）
- **调用的DAO方法**：`probManageDao.deleteProductComponentById()`
- **源码行号**：`接口:ProbManageService.java:280` / `实现:ProbManageServiceImpl.java:1013`

**【技术公告产品 ProbProduct CRUD 方法】**

#### `ProbProduct selectProbProductById(Integer id)`
- **功能描述**：根据ID查询技术公告产品
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`probManageDao.selectProbProductById()`
- **源码行号**：`接口:ProbManageService.java:295` / `实现:ProbManageServiceImpl.java:1018`

#### `ProbProductVO selectProbProductVOById(Integer id)`
- **功能描述**：根据ID查询技术公告产品 VO（含关联信息）
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`probManageDao.selectProbProductVOById()`
- **源码行号**：`接口:ProbManageService.java:301` / `实现:ProbManageServiceImpl.java:1023`

#### `List<ProbProduct> selectProbProductList(ProbProduct probProduct)`
- **功能描述**：查询技术公告产品列表
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`probManageDao.selectProbProductList()`
- **源码行号**：`接口:ProbManageService.java:307` / `实现:ProbManageServiceImpl.java:1028`

#### `List<ProbProductVO> selectProbProductListPageable(ProbProductPageParam pageParam)`
- **功能描述**：分页查询技术公告产品列表
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`probManageDao.selectProbProductListPageable()`
- **源码行号**：`接口:ProbManageService.java:313` / `实现:ProbManageServiceImpl.java:1033`

#### `Integer countProbProductListPageable(ProbProductPageParam pageParam)`
- **功能描述**：统计分页查询技术公告产品列表总数
- **事务类型**：无事务（count*为查询统计方法）
- **调用的DAO方法**：`probManageDao.countProbProductListPageable()`
- **源码行号**：`接口:ProbManageService.java:319` / `实现:ProbManageServiceImpl.java:1038`

#### `Integer insertProbProduct(ProbProduct probProduct)`
- **功能描述**：插入技术公告产品
- **事务类型**：REQUIRED（insert*前缀匹配）
- **调用的DAO方法**：`probManageDao.insertProbProduct()`
- **源码行号**：`接口:ProbManageService.java:324` / `实现:ProbManageServiceImpl.java:1043`

#### `Integer insertProbProductSelective(ProbProduct probProduct)`
- **功能描述**：选择性插入技术公告产品（仅插入非空字段）
- **事务类型**：REQUIRED（insert*前缀匹配）
- **调用的DAO方法**：`probManageDao.insertProbProductSelective()`
- **源码行号**：`接口:ProbManageService.java:330` / `实现:ProbManageServiceImpl.java:1048`

#### `Integer insertOrUpdateProbProductSelective(ProbProduct probProduct)`
- **功能描述**：选择性插入或更新技术公告产品
- **事务类型**：REQUIRED（insert*前缀匹配）
- **调用的DAO方法**：`probManageDao.insertOrUpdateProbProductSelective()`
- **源码行号**：`接口:ProbManageService.java:349` / `实现:ProbManageServiceImpl.java:1053`

#### `void updateProbProductById(ProbProduct probProduct)`
- **功能描述**：根据ID更新技术公告产品
- **事务类型**：REQUIRED（update*前缀匹配）
- **调用的DAO方法**：`probManageDao.updateProbProductById()`
- **源码行号**：`接口:ProbManageService.java:341` / `实现:ProbManageServiceImpl.java:1058`

#### `void updateProbProductByIdSelective(ProbProduct probProduct)`
- **功能描述**：根据ID选择性更新技术公告产品（仅更新非空字段）
- **事务类型**：REQUIRED（update*前缀匹配）
- **调用的DAO方法**：`probManageDao.updateProbProductByIdSelective()`
- **源码行号**：`接口:ProbManageService.java:345` / `实现:ProbManageServiceImpl.java:1063`

#### `void updateProbProductByProbIdSelective(ProbProduct probProduct)`
- **功能描述**：根据技术公告ID选择性更新技术公告产品（批量更新该公告下的所有产品）
- **事务类型**：REQUIRED（update*前缀匹配）
- **调用的DAO方法**：`probManageDao.updateProbProductByProbIdSelective()`
- **源码行号**：`接口:ProbManageService.java:347` / `实现:ProbManageServiceImpl.java:1068`

#### `void deleteProbProductById(Integer id)`
- **功能描述**：根据ID删除技术公告产品
- **事务类型**：REQUIRED（delete*前缀匹配）
- **调用的DAO方法**：`probManageDao.deleteProbProductById()`
- **源码行号**：`接口:ProbManageService.java:336` / `实现:ProbManageServiceImpl.java:1073`

#### `void deleteProbProductByProbId(Integer probId)`
- **功能描述**：根据技术公告ID删除其下所有产品（批量删除）
- **事务类型**：REQUIRED（delete*前缀匹配）
- **调用的DAO方法**：`probManageDao.deleteProbProductByProbId()`
- **源码行号**：`接口:ProbManageService.java:348` / `实现:ProbManageServiceImpl.java:1078`

**【产品条目查询方法】**

#### `List<? extends Object> selectProductItemListByExample(ProductItemExample example)`
- **功能描述**：根据条件范例查询产品条目列表（规范输入）
- **事务类型**：无事务（select*前缀）
- **参数**：`example` ProductItemExample 查询条件范例对象
- **调用的DAO方法**：`probManageDao.selectProductItemListByExample()`
- **源码行号**：`接口:ProbManageService.java:369` / `实现:ProbManageServiceImpl.java:1135`

---

## 21. SubcontractServiceImpl

### 类概述
- 实现接口：`SubcontractService`
- 事务代理Bean：`subcontractServiceAgent`
- 依赖的DAO：`SubcontractDao`
- 跨Service依赖：`BasicDataService`, `CallBackService`, `TaskService(Activiti)`, `SendMailService`, `UserManageService`, `PmClosedLoopDao`, `WorkFlowService`, `DepartmentManageService`

### 方法列表

#### `SubcontractProject selectSubcontractProjectById(Integer)` / `SubcontractProjectVO selectSubcontractProjectVOById(Integer)`
- **功能描述**：根据ID查询转包项目

#### `List<SubcontractProjectVO> selectSubcontractProjectVOListPageable(SubcontractPageParam)`
- **功能描述**：分页查询转包项目列表
- **核心算法**：
  1. 查询总数
  2. 非导出模式：每页50条
  3. 导出模式：不分页
  4. 设置分页参数后查询

#### `void insertSubcontractProject(SubcontractProject)` / `void insertSubcontractProjectSelective(SubcontractProject)`
- **功能描述**：插入转包项目
- **事务类型**：REQUIRED（@Transactional注解 + insert*前缀）
- **核心算法**：自动设置createBy、createTime、effectiveFrom

#### `void insertSubcontractDeliver(SubcontractDeliver)`
- **功能描述**：插入转包交付件
- **核心算法**：自动设置uploadBy、uploadTime、effectiveFrom

#### `void insertSubcontractPayment(SubcontractPayment)`
- **功能描述**：插入转包付款信息
- **核心算法**：自动设置createBy、createTime

#### `void saveSubcontractPayment(List<SubcontractPayment>, Integer[] delIds)`
- **功能描述**：保存转包付款信息（含删除和新增/更新）
- **事务类型**：REQUIRED（@Transactional注解 + save*前缀）
- **核心算法**：
  1. 删除指定ID的付款信息
  2. 遍历付款列表：有ID则更新，无ID则新增

#### `void createSubcontractProject(SubcontractProject, List<SubcontractLine>, File[], String[], String[])`
- **功能描述**：创建转包项目（含交付件上传）
- **事务类型**：REQUIRED（@Transactional注解 + 间接create*不匹配，但内部调用insert*方法）

#### `String checkSubcontractName(String)` / `String checkSubcontractName(SubcontractProject)`
- **功能描述**：检查转包项目名称是否重复

#### `List<ShipmentInfo> queryShipmentinfoByContractNosAndProjectIds(String, String)` / `queryShipmentinfoByContractNosAndProjectIds(String, String, boolean)` / `queryShipmentinfoByContractNosAndProjectIds(Map)`
- **功能描述**：根据合同号和项目ID查询出货信息

#### `List<Project> queryProjectList(Project)` / `List<Project> queryProjectList(SubcontractProject)`
- **功能描述**：查询项目列表

### 补充方法文档（遗漏方法补全）

> 以下方法为前期文档遗漏，现按功能类别分组补全。源码行号标注格式：`接口:SubcontractService.java:行号` / `实现:SubcontractServiceImpl.java:行号`。

**【列表/查询类方法】**

#### `List<SubcontractProject> selectSubcontractProjectList(SubcontractProject subcontractProject)`
- **功能描述**：查询转包项目列表
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`dao.selectSubcontractProjectList()`
- **源码行号**：`接口:SubcontractService.java:39` / `实现:SubcontractServiceImpl.java:202`

#### `List<SubcontractProjectVO> selectSubcontractProjectVOList(SubcontractProject subcontractProject)`
- **功能描述**：查询转包项目 VO 列表（含关联信息）
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`dao.selectSubcontractProjectVOList()`
- **源码行号**：`接口:SubcontractService.java:45` / `实现:SubcontractServiceImpl.java:207`

#### `List<SubcontractFacilitator> selectSubcontractFacilitatorList(SubcontractFacilitator subcontractFacilitator)`
- **功能描述**：查询服务商列表
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`dao.selectSubcontractFacilitatorList()`
- **源码行号**：`接口:SubcontractService.java:53` / `实现:SubcontractServiceImpl.java:245`

#### `List<SubcontractLine> selectSubcontractLineList(SubcontractLine subcontractLine)`
- **功能描述**：查询转包序列号清单列表
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`dao.selectSubcontractLineList()`
- **源码行号**：`接口:SubcontractService.java:137` / `实现:SubcontractServiceImpl.java:461`

#### `SubcontractDeliver selectSubcontractDeliverById(Integer deliverId)`
- **功能描述**：根据ID查询转包交付件
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`dao.selectSubcontractDeliverById()`
- **源码行号**：`接口:SubcontractService.java:143` / `实现:SubcontractServiceImpl.java:466`

#### `List<SubcontractDeliver> selectSubcontractDeliverList(SubcontractDeliver deliver)`
- **功能描述**：查询转包交付件列表
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`dao.selectSubcontractDeliverList()`
- **源码行号**：`接口:SubcontractService.java:149` / `实现:SubcontractServiceImpl.java:471`

#### `List<SubcontractDeliverVO> selectSubcontractDeliverVOList(SubcontractDeliver deliver)`
- **功能描述**：查询转包交付件 VO 列表（含关联信息）
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`dao.selectSubcontractDeliverVOList()`
- **源码行号**：`接口:SubcontractService.java:155` / `实现:SubcontractServiceImpl.java:476`

#### `List<SubcontractPayment> selectSubcontractPaymentList(SubcontractPayment payment)`
- **功能描述**：查询转包付款信息列表
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`dao.selectSubcontractPaymentList()`
- **源码行号**：`接口:SubcontractService.java:364` / `实现:SubcontractServiceImpl.java:251`

#### `SubcontractPayment selectSubcontractPaymentById(Integer paymentId)`
- **功能描述**：根据ID查询转包付款信息
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`dao.selectSubcontractPaymentById()`
- **源码行号**：`接口:SubcontractService.java:371` / `实现:SubcontractServiceImpl.java:256`

#### `String querySubcontractPaiedAmount(Integer subcontractId)`
- **功能描述**：查询转包项目已付款总额
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`dao.querySubcontractPaiedAmmount()`（注：DAO方法名存在拼写错误 `Ammount`）
- **源码行号**：`接口:SubcontractService.java:399` / `实现:SubcontractServiceImpl.java:261`

#### `List<Map<String, Object>> queryContractNoEngineeFee(String contractNos)`
- **功能描述**：查询合同工程服务费
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`dao.queryContractNoEngineeFee()`
- **源码行号**：`接口:SubcontractService.java:232` / `实现:SubcontractServiceImpl.java:780`

#### `List<SubcontractPrice> queryContractNoEngineeFeeWithSubPrice(String contractNos, Integer subcontractId)`
- **功能描述**：查询工程服务费（附带转包价）
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`dao.queryContractNoEngineeFeeWithSubPrice()`
- **源码行号**：`接口:SubcontractService.java:240` / `实现:SubcontractServiceImpl.java:785`

#### `List<SubcontractCallback> selectSubcontractCallbackList(SubcontractCallback subcontractCallback)`
- **功能描述**：查询转包回访记录列表
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`dao.selectSubcontractCallbackList()`
- **源码行号**：`接口:SubcontractService.java:444` / `实现:SubcontractServiceImpl.java:3213`

#### `SubcontractCallback selectMaxSubcontractCallback(SubcontractCallback subcontractCallback)`
- **功能描述**：查询最新一条转包回访记录
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`dao.selectMaxSubcontractCallback()`
- **源码行号**：`接口:SubcontractService.java:450` / `实现:SubcontractServiceImpl.java:3218`

#### `List<Map<String, Object>> querySubcontractCommentList(Integer subcontractId)`
- **功能描述**：查询转包流程审批意见列表
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`dao.querySubcontractCommentList()`
- **源码行号**：`接口:SubcontractService.java:452` / `实现:SubcontractServiceImpl.java:3227`

#### `SubcontractFacilitator selectSubcontractFacilitatorById(Integer id)`
- **功能描述**：根据ID查询服务商
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`dao.selectSubcontractFacilitatorById()`
- **源码行号**：`接口:SubcontractService.java:468` / `实现:SubcontractServiceImpl.java:3249`

#### `List<SubcontractProjectVO> querySubcontractInfoForProject(String projectIds)`
- **功能描述**：查询项目转包记录（供项目管理中使用）
- **事务类型**：无事务（query*前缀）
- **参数**：`projectIds` 逗号分隔的项目ID
- **调用的DAO方法**：`dao.querySubcontractInfoForProject()`
- **源码行号**：`接口:SubcontractService.java:482` / `实现:SubcontractServiceImpl.java:3254`

#### `List<Map<String, Object>> selectRejectedSubcontractProjectList(HashMap<String, Object> params)`
- **功能描述**：服务经理查询被驳回的转包申请（显示在待办事项中）
- **事务类型**：无事务（select*前缀）
- **调用的DAO方法**：`dao.selectRejectedSubcontractProjectList()`
- **源码行号**：`接口:SubcontractService.java:530` / `实现:SubcontractServiceImpl.java:3273`

#### `List<SubcontractProjectVO> querySubcontractExportData(SubcontractProjectVO subcontractVO)`
- **功能描述**：转包列表数据导出
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`dao.querySubcontractExportData()`
- **源码行号**：`接口:SubcontractService.java:537` / `实现:SubcontractServiceImpl.java:3278`

#### `List<SubcontractProjectVO> queryNextPaymentTask()`
- **功能描述**：查询下一付款任务列表
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`dao.queryNextPaymentTask()`
- **源码行号**：`接口:SubcontractService.java:568` / `实现:SubcontractServiceImpl.java:3284`

#### `List<SubcontractPayment> querySSESubcontractPaymentList()`
- **功能描述**：查询 SSE 新增的转包付款信息
- **事务类型**：无事务（query*前缀）
- **调用的DAO方法**：`dao.querySSESubcontractPaymentList()`
- **源码行号**：`接口:SubcontractService.java:574` / `实现:SubcontractServiceImpl.java:3289`

#### `Map<String, String> selectDefaultMultiDimByDep(String depNum)` / `Map<String, String> selectDefaultMultiDimByDep(String depNum, boolean directWithoutCache)`
- **功能描述**：根据部门查询默认多维度信息
- **事务类型**：无事务（select*前缀）
- **参数**：
  - 重载1：默认查询
  - 重载2：`directWithoutCache` 为 true 时直接查询数据库，不查询缓存
- **调用的DAO方法**：`dao.selectDefaultMultiDimByDep()`
- **源码行号**：`接口:SubcontractService.java:581,589` / `实现:SubcontractServiceImpl.java:3486,3497`

#### `Task queryCurrentTask(Integer subcontractId)`
- **功能描述**：查询转包项目当前流程任务（已弃用）
- **事务类型**：无事务（query*前缀）
- **调用的其他Service方法**：`taskService.createTaskQuery()...`
- **源码行号**：`接口:SubcontractService.java:255` / `实现:SubcontractServiceImpl.java:2864`
- **注意事项**：⚠️ @Deprecated 已弃用

#### `SubcontractComment queryCurrentSubcontractCommon(Integer subcontractId)`
- **功能描述**：查询转包项目当前流程通用参数（已弃用）
- **事务类型**：无事务（query*前缀）
- **调用的其他Service方法**：`workFlowService.queryProcessVarMap()`
- **源码行号**：`接口:SubcontractService.java:314` / `实现:SubcontractServiceImpl.java:2879`
- **注意事项**：⚠️ @Deprecated 已弃用

#### `WorkflowCommonParam queryCurrentWorkFlowCommonParam(Integer subcontractId)` / `queryCurrentWorkFlowCommonParam(Integer, String)` / `queryCurrentWorkFlowCommonParam(Integer, String, String)` / `queryCurrentWorkFlowCommonParam(Integer, String, String, HashMap)`
- **功能描述**：查询转包项目当前流程通用参数（4个重载）
- **事务类型**：无事务（query*前缀）
- **参数**：
  - 重载1：仅 subcontractId
  - 重载2：subcontractId + taskKey
  - 重载3：subcontractId + taskKey + roleGroup
  - 重载4：subcontractId + taskKey + roleGroup + params（额外参数）
- **核心算法**：根据 taskKey 和 roleGroup 过滤查询当前活动任务及其流程变量
- **调用的其他Service方法**：`taskService.createTaskQuery()`, `workFlowService.queryProcessVarMap()`
- **源码行号**：`接口:SubcontractService.java:320,406,415,425` / `实现:SubcontractServiceImpl.java:2898,2903,2908,2914`

**【交付件相关方法】**

#### `boolean saveDeliverFiles(Integer subcontractId, List<SubcontractDeliverVO> uploadDeliverList)`
- **功能描述**：保存上传的交付件文件（含发票识别）
- **事务类型**：REQUIRED（save*前缀匹配；本实现未配置 @Transactional 注解，但按前缀规则判为 REQUIRED）
- **核心算法**：
  1. 文件上传白名单校验
  2. 遍历交付件列表，对每个文件：重命名 → 保存 → 插入交付件记录
  3. 识别发票类型附件，收集发票文件列表
  4. 调用 `verifySubcontractPaymentDeliver()` 进行发票识别
  5. 更新付款申请对应的发票编号
- **返回值**：callBackFlag（是否需要发起回访流程，当前实现固定返回 false）
- **调用的Service方法**：`this.insertSubcontractDeliver()`, `this.verifySubcontractPaymentDeliver()`, `this.updateSubcontractPaymentInvoiceNumber()`
- **调用的工具类**：`UploadFileUtil`, `SubcontractUtil`, `FPApi`（发票识别API）
- **源码行号**：`接口:SubcontractService.java:204` / `实现:SubcontractServiceImpl.java:488`

#### `void deleteSubcontractDeliver(SubcontractDeliverVO subcontractDeliverVO)`
- **功能描述**：删除转包交付件
- **事务类型**：REQUIRED（delete*前缀匹配）
- **核心算法**：
  1. 遍历待删除的交付件ID，查询其关联的 paymentId
  2. 调用 DAO 删除交付件
  3. 更新受影响的付款申请对应的发票编号
- **调用的DAO方法**：`dao.deleteSubcontractDeliver()`
- **调用的Service方法**：`this.selectSubcontractDeliverById()`, `this.updateSubcontractPaymentInvoiceNumber()`
- **源码行号**：`接口:SubcontractService.java:172` / `实现:SubcontractServiceImpl.java:703`

#### `void updateSubcontractDeliverByIdSelective(SubcontractDeliver subcontractDeliver)`
- **功能描述**：根据ID选择性更新转包交付件（仅更新非空字段）
- **事务类型**：REQUIRED（update*前缀匹配）
- **调用的DAO方法**：`dao.updateSubcontractDeliverByIdSelective()`
- **源码行号**：`接口:SubcontractService.java:165` / `实现:SubcontractServiceImpl.java:335`

#### `Result verifySubcontractPaymentDeliver(Integer subcontractId, Integer paymentId)` / `Result verifySubcontractPaymentDeliver(SubcontractPayment payment)`
- **功能描述**：付款附件查验（发票识别与验真）
- **事务类型**：无事务（verify*前缀不匹配事务规则，且未配置 @Transactional 注解）
- **核心算法**：
  1. 重载1：构建 SubcontractPayment 对象委托给重载2
  2. 重载2：查询指定类型的交付件列表 → 按文件路径分组去重 → 委托给内部三参重载
  3. 内部三参重载：调用 `FPApi.postElectronicInvoice()` 发票识别API → 更新交付件的发票信息和类型
- **调用的Service方法**：`this.selectSubcontractDeliverList()`, `this.updateSubcontractDeliverByIdSelective()`
- **调用的工具类**：`FPApi`, `SubcontractUtil`, `InvoiceUtil`
- **源码行号**：`接口:SubcontractService.java:612,619` / `实现:SubcontractServiceImpl.java:569,582`

#### `Result updateSubcontractPaymentInvoiceNumber(Set<Integer> paymentIds)`
- **功能描述**：更新付款申请对应的发票编号（汇总发票号和金额）
- **事务类型**：REQUIRED（update*前缀匹配）
- **核心算法**：
  1. 遍历 paymentIds
  2. 查询每个付款的发票类型交付件列表
  3. 提取去重的发票编号和金额合计
  4. 更新付款记录的发票编号和金额
- **调用的Service方法**：`this.selectSubcontractDeliverList()`, `this.updateSubcontractPaymentByIdSelective()`
- **调用的工具类**：`InvoiceUtil`, `MapUtil`
- **源码行号**：`接口:SubcontractService.java:625` / `实现:SubcontractServiceImpl.java:725`

**【CRUD 方法】**

#### `void createSubcontractProject(SubcontractProject, List<SubcontractLine>, List<SubcontractDeliver>, File[])` / `createSubcontractProject(SubcontractProject, List<SubcontractLine>, List<SubcontractDeliverVO>)` / `createSubcontractProject(SubcontractProject, List<SubcontractDeliverVO>)` / `createSubcontractProject(SubcontractProject, List<SubcontractDeliverVO>, String[])`
- **功能描述**：创建转包项目（4个遗漏重载，含序列号清单批量插入）
- **事务类型**：REQUIRED（@Transactional注解；部分重载注释掉了 @Transactional）
- **核心算法**：
  1. 重载1（含 SubcontractDeliver 列表）：空实现
  2. 重载2（含 SubcontractDeliverVO 列表）：调用 `insertSubcontractProjectSelective()` 插入项目
  3. 重载3（仅 VO 列表）：委托给重载2
  4. 重载4（VO 列表 + selected）：创建项目后调用 `batchInsertSubcontractLine()` 批量插入序列号清单
- **调用的Service方法**：`this.insertSubcontractProjectSelective()`, `this.batchInsertSubcontractLine()`（private）
- **源码行号**：`接口:SubcontractService.java:120,179,197,217` / `实现:SubcontractServiceImpl.java:410,417,425,431`

#### `void updateSubcontractProject(SubcontractProject, List<SubcontractDeliverVO>)` / `void updateSubcontractProject(SubcontractProject, List<SubcontractDeliverVO>, String[])`
- **功能描述**：更新转包项目（2个遗漏重载，含序列号清单批量插入）
- **事务类型**：REQUIRED（@Transactional注解 + update*前缀）
- **核心算法**：
  1. 重载1：调用 `updateSubcontractProjectByIdSelective()` 更新项目
  2. 重载2：更新项目后调用 `batchInsertSubcontractLine()` 批量插入序列号清单
- **调用的Service方法**：`this.updateSubcontractProjectByIdSelective()`, `this.batchInsertSubcontractLine()`（private）
- **源码行号**：`接口:SubcontractService.java:186,225` / `实现:SubcontractServiceImpl.java:439,446`

#### `void updateSubcontractProjectByIdSelective(SubcontractProject subcontract)`
- **功能描述**：根据ID选择性更新转包项目（仅更新非空字段）
- **事务类型**：REQUIRED（update*前缀匹配）
- **核心算法**：自动设置 updateBy、updateTime 后委托 DAO 更新
- **调用的DAO方法**：`dao.updateSubcontractProjectByIdSelective()`
- **源码行号**：`接口:SubcontractService.java:191` / `实现:SubcontractServiceImpl.java:454`

#### `void saveSubcontractPayment(List<SubcontractPayment> subcontractPaymentList)`
- **功能描述**：保存转包付款信息（无删除重载，委托给带 delIds 的重载）
- **事务类型**：REQUIRED（save*前缀匹配）
- **核心算法**：调用 `saveSubcontractPayment(subcontractPaymentList, null)`
- **调用的Service方法**：`this.saveSubcontractPayment(List, Integer[])`
- **源码行号**：`接口:SubcontractService.java:377` / `实现:SubcontractServiceImpl.java:356`

#### `void updateSubcontractPaymentByIdSelective(SubcontractPayment subcontractPayment)`
- **功能描述**：根据ID选择性更新转包付款信息
- **事务类型**：REQUIRED（update*前缀匹配）
- **核心算法**：自动设置 updateBy、updateTime 后委托 DAO 更新
- **调用的DAO方法**：`dao.updateSubcontractPaymentByIdSelective()`
- **源码行号**：`接口:SubcontractService.java:393` / `实现:SubcontractServiceImpl.java:349`

#### `void insertSubcontractCallback(SubcontractCallback callback)`
- **功能描述**：插入转包回访记录
- **事务类型**：REQUIRED（insert*前缀匹配）
- **核心算法**：自动设置 createBy、createTime 后委托 DAO 插入
- **调用的DAO方法**：`dao.insertSubcontractCallback()`
- **源码行号**：`接口:SubcontractService.java:430` / `实现:SubcontractServiceImpl.java:3197`

#### `void insertSubcontractQuesnaire(SubcontractCallback callback, PmClQuesnaireResultHeader pmClQuesnaireResultHeader, List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList)`
- **功能描述**：插入转包回访问卷（问卷头+问卷行+关联关系）
- **事务类型**：REQUIRED（@Transactional注解 + insert*前缀）
- **核心算法**：
  1. 插入问卷头（pmClosedLoopDao）
  2. 插入问卷结果行列表
  3. 查询是否已保存过问卷 → 更新或新增关联关系
- **调用的DAO方法**：`pmClosedLoopDao.addPmClQuesResultHeader()`, `pmClosedLoopDao.addPmClQuesResultLineList()`, `dao.queryCallBackQuesnaireId()`, `dao.updateCallBackQuesnaire()` / `dao.insertCallBackQuesnaire()`
- **源码行号**：`接口:SubcontractService.java:437` / `实现:SubcontractServiceImpl.java:3153`

#### `void insertSubcontractFacilitator(SubcontractFacilitator subcontractFacilitator)`
- **功能描述**：插入服务商
- **事务类型**：REQUIRED（insert*前缀匹配）
- **核心算法**：自动设置 createBy、createTime 后委托 DAO 插入
- **调用的DAO方法**：`dao.insertSubcontractFacilitator()`
- **源码行号**：`接口:SubcontractService.java:457` / `实现:SubcontractServiceImpl.java:3232`

#### `void updateSubcontractFacilitatorByIdSelective(SubcontractFacilitator subcontractFacilitator)`
- **功能描述**：根据ID选择性更新服务商
- **事务类型**：REQUIRED（update*前缀匹配）
- **核心算法**：自动设置 updateBy、updateTime 后委托 DAO 更新
- **调用的DAO方法**：`dao.updateSubcontractFacilitatorByIdSelective()`
- **源码行号**：`接口:SubcontractService.java:462` / `实现:SubcontractServiceImpl.java:3242`

#### `void insertSubcontractPrice(SubcontractPrice price)`
- **功能描述**：插入转包价格
- **事务类型**：REQUIRED（insert*前缀匹配）
- **核心算法**：自动设置 createBy、createTime 后委托 DAO 插入
- **调用的DAO方法**：`dao.insertSubcontractPrice()`
- **源码行号**：`接口:SubcontractService.java:487` / `实现:SubcontractServiceImpl.java:3259`

#### `void updateSubcontractPriceByIdSelective(SubcontractPrice price)`
- **功能描述**：根据ID选择性更新转包价格
- **事务类型**：REQUIRED（update*前缀匹配）
- **核心算法**：自动设置 updateBy、updateTime 后委托 DAO 更新
- **调用的DAO方法**：`dao.updateSubcontractPriceByIdSelective()`
- **源码行号**：`接口:SubcontractService.java:492` / `实现:SubcontractServiceImpl.java:3266`

**【流程发起方法】**

#### `String startSubcontractFlow(WorkflowCommonParam, SubcontractEvaluationHeader, SubcontractProject)` / `String startSubcontractFlow(SubcontractProject)`
- **功能描述**：启动转包申请流程
- **事务类型**：REQUIRED（@Transactional注解 + start*前缀）
- **核心算法**：
  1. 重载1（已弃用）：校验是否有进行中的流程 → 启动 Activiti 流程实例 → 回写 instId → 办理首个任务 → 添加审批意见 → 发送邮件通知
  2. 重载2：构建流程变量 → 启动流程 → 办理任务 → 邮件通知
- **调用的其他Service方法**：`workFlowService.startProcess()`, `workFlowService.doSelfTask()`, `workFlowService.addSelfActComment()`, `taskService.createTaskQuery()`
- **源码行号**：`接口:SubcontractService.java:248,267` / `实现:SubcontractServiceImpl.java:792,854`
- **注意事项**：⚠️ 重载1 标记为 @Deprecated 已弃用

#### `String profitSerivceManagerFlow(WorkflowCommonParam taskParam, SubcontractProject subcontract)`
- **功能描述**：受益部门服务经理审批流程
- **事务类型**：REQUIRED（@Transactional注解）
- **核心算法**：办理当前任务 → 更新流程变量 → 添加审批意见 → 邮件通知下一步审批人
- **调用的其他Service方法**：`workFlowService.doSelfTask()`, `workFlowService.addSelfActComment()`
- **源码行号**：`接口:SubcontractService.java:275` / `实现:SubcontractServiceImpl.java:951`

#### `SubcontractCallback startCallBackFlow(Integer subcontractId)` / `SubcontractCallback startCallBackFlow(Integer subcontractId, ActComment comment)`
- **功能描述**：启动转包回访流程
- **事务类型**：REQUIRED（@Transactional注解 + start*前缀）
- **核心算法**：
  1. 重载1：构建默认 ActComment（消息为"上传服务单触发回访流程"）委托给重载2
  2. 重载2：
     - 校验 subcontractId 有效性
     - 更新转包项目回访状态为"回访中"（20）
     - 插入回访记录（含问卷版本号）
     - 校验是否有进行中的回访流程
     - 启动 Activiti 回访流程实例
     - 更新回访任务的 taskId/taskKey 到回访问卷表
     - 办理首个任务（转给回访人员角色）
     - 添加审批意见
- **调用的DAO方法**：`dao.updateSubcontractProjectByIdSelective()`, `dao.queryCallBackQuesnaireVersion()`
- **调用的Service方法**：`this.insertSubcontractCallback()`, `this.updateSubcontractCallbackByIdSelective()`
- **调用的其他Service方法**：`workFlowService.startProcess()`, `workFlowService.doSelfTask()`, `workFlowService.addSelfActComment()`
- **返回值**：创建的 SubcontractCallback 对象
- **源码行号**：`接口:SubcontractService.java:335,342` / `实现:SubcontractServiceImpl.java:2539,2549`

**【流程审批方法】**

#### `String auditSubcontractFlow(WorkflowCommonParam, SubcontractEvaluationHeader, SubcontractProject)` / `String auditSubcontractFlow(WorkflowCommonParam, SubcontractProject, List<SubcontractPrice>)` / `String auditSubcontractFlow(SubcontractComment, SubcontractProject)`
- **功能描述**：转包流程审批（3个重载）
- **事务类型**：REQUIRED（@Transactional注解）
- **核心算法**：
  1. 重载1（已弃用）：办理任务 → 添加审批意见 → 邮件通知
  2. 重载2：保存转包价格 → 办理任务 → 添加审批意见 → 邮件通知
  3. 重载3（已弃用）：办理任务 → 添加审批意见 → 邮件通知
- **调用的其他Service方法**：`workFlowService.doSelfTask()`, `workFlowService.addSelfActComment()`
- **源码行号**：`接口:SubcontractService.java:291,300,308` / `实现:SubcontractServiceImpl.java:1222,1297,1447`
- **注意事项**：⚠️ 重载1、重载3 标记为 @Deprecated 已弃用

#### `String closeSubcontractFlow(WorkflowCommonParam taskParam, SubcontractEvaluationHeader pmClEvaluationHeader, SubcontractProject subcontract)`
- **功能描述**：关闭转包流程
- **事务类型**：REQUIRED（@Transactional注解）
- **核心算法**：办理任务 → 更新流程变量 → 添加审批意见 → 邮件通知
- **调用的其他Service方法**：`workFlowService.doSelfTask()`, `workFlowService.addSelfActComment()`
- **源码行号**：`接口:SubcontractService.java:328` / `实现:SubcontractServiceImpl.java:1800`

#### `String submitCallBackFlow(WorkflowCommonParam taskParam, SubcontractCallback subcontractCallback)`
- **功能描述**：回访人员回访（单独的回访流程）
- **事务类型**：REQUIRED（@Transactional注解 + submit*前缀）
- **核心算法**：办理回访任务 → 更新流程变量 → 添加审批意见 → 邮件通知
- **调用的其他Service方法**：`workFlowService.doSelfTask()`, `workFlowService.addSelfActComment()`
- **源码行号**：`接口:SubcontractService.java:350` / `实现:SubcontractServiceImpl.java:2632`

#### `String submitCallBackFlow2(WorkflowCommonParam taskParam, SubcontractCallback subcontractCallback)`
- **功能描述**：回访人员回访（在一个流程中，非单独回访流程）
- **事务类型**：REQUIRED（@Transactional注解 + submit*前缀）
- **核心算法**：办理回访任务 → 更新流程变量 → 添加审批意见 → 邮件通知
- **调用的其他Service方法**：`workFlowService.doSelfTask()`, `workFlowService.addSelfActComment()`
- **源码行号**：`接口:SubcontractService.java:358` / `实现:SubcontractServiceImpl.java:2203`

#### `String approveSubcontractFlow(WorkflowCommonParam taskParam, SubcontractProject subcontract)`
- **功能描述**：转包流程审批通过
- **事务类型**：REQUIRED（@Transactional注解）
- **核心算法**：办理任务 → 更新流程变量 → 添加审批意见 → 邮件通知
- **调用的其他Service方法**：`workFlowService.doSelfTask()`, `workFlowService.addSelfActComment()`
- **源码行号**：`接口:SubcontractService.java:475` / `实现:SubcontractServiceImpl.java:1708`

#### `String generateContractFlow(WorkflowCommonParam taskParam, SubcontractProject subcontract)`
- **功能描述**：生成合同号流程
- **事务类型**：REQUIRED（@Transactional注解）
- **核心算法**：办理任务 → 生成合同号 → 更新流程变量 → 添加审批意见 → 邮件通知
- **调用的其他Service方法**：`workFlowService.doSelfTask()`, `workFlowService.addSelfActComment()`
- **源码行号**：`接口:SubcontractService.java:500` / `实现:SubcontractServiceImpl.java:1879`

#### `String applyPaymentFlow(WorkflowCommonParam taskParam, SubcontractProject subcontract)`
- **功能描述**：服务经理提交付款信息
- **事务类型**：REQUIRED（@Transactional注解）
- **核心算法**：办理任务 → 更新流程变量 → 添加审批意见 → 邮件通知
- **调用的其他Service方法**：`workFlowService.doSelfTask()`, `workFlowService.addSelfActComment()`
- **源码行号**：`接口:SubcontractService.java:508` / `实现:SubcontractServiceImpl.java:1951`

#### `String approvePaymentFlow(WorkflowCommonParam taskParam, SubcontractProject subcontract)`
- **功能描述**：工程管理部付款审批
- **事务类型**：REQUIRED（@Transactional注解）
- **核心算法**：办理任务 → 更新流程变量 → 添加审批意见 → 邮件通知
- **调用的其他Service方法**：`workFlowService.doSelfTask()`, `workFlowService.addSelfActComment()`
- **源码行号**：`接口:SubcontractService.java:516` / `实现:SubcontractServiceImpl.java:2345`

#### `String submitAcceptanceFlow(WorkflowCommonParam taskParam, SubcontractProject subcontract)`
- **功能描述**：验收审批流程
- **事务类型**：REQUIRED（submit*前缀匹配；未配置 @Transactional 注解但前缀匹配）
- **核心算法**：办理任务 → 更新流程变量 → 添加审批意见 → 邮件通知
- **调用的其他Service方法**：`workFlowService.doSelfTask()`, `workFlowService.addSelfActComment()`
- **源码行号**：`接口:SubcontractService.java:523` / `实现:SubcontractServiceImpl.java:2439`

#### `String normalApproveSubcontractFlow(WorkflowCommonParam taskParam, SubcontractProject subcontract)`
- **功能描述**：通用审批节点
- **事务类型**：REQUIRED（@Transactional注解）
- **核心算法**：办理任务 → 更新流程变量 → 添加审批意见 → 邮件通知
- **调用的其他Service方法**：`workFlowService.doSelfTask()`, `workFlowService.addSelfActComment()`
- **源码行号**：`接口:SubcontractService.java:597` / `实现:SubcontractServiceImpl.java:1077`

#### `String auditNormalApproveSubcontractFlow(WorkflowCommonParam taskParam, SubcontractProject subcontract, List<SubcontractPrice> subcontractPriceList)`
- **功能描述**：工程管理部主管通用审批节点
- **事务类型**：REQUIRED（@Transactional注解）
- **核心算法**：保存转包价格 → 办理任务 → 更新流程变量 → 添加审批意见 → 邮件通知
- **调用的Service方法**：`this.insertSubcontractPrice()` / `this.updateSubcontractPriceByIdSelective()`
- **调用的其他Service方法**：`workFlowService.doSelfTask()`, `workFlowService.addSelfActComment()`
- **源码行号**：`接口:SubcontractService.java:605` / `实现:SubcontractServiceImpl.java:1550`

**【流程终止方法】**

#### `void terminateWorkFlow(Integer subcontractId)` / `void terminateWorkFlow(Integer subcontractId, String comment)` / `void terminateWorkFlow(Integer subcontractId, WorkflowCommonParam taskParam)`
- **功能描述**：终止转包流程（3个重载）
- **事务类型**：REQUIRED（@Transactional注解）
- **核心算法**：
  1. 重载1：委托给重载2，comment 为空
  2. 重载2：删除流程实例 → 更新项目状态 → 添加审批意见 → 邮件通知
  3. 重载3：基于 WorkflowCommonParam 执行更完整的终止逻辑（含任务办理、状态更新、邮件通知）
- **调用的其他Service方法**：`workFlowService.deleteProcessInstance()`, `workFlowService.addSelfActComment()`, `workFlowService.doSelfTask()`
- **源码行号**：`接口:SubcontractService.java:549,556,563` / `实现:SubcontractServiceImpl.java:2692,2698,2766`

---

## 22. CertificateServiceImpl

### 类概述
- 实现接口：`CertificateService`
- 事务代理Bean：`certificateServiceAgent`
- 依赖的DAO：`CertificateDao`

### 方法列表

#### `List<Map<String, String>> queryOQCInfo(String barcode)`
- **功能描述**：根据条码查询OQC信息
- **事务类型**：无事务

#### `void parseExcelFile(File file)`
- **功能描述**：解析Excel文件（印章登记表）
- **事务类型**：REQUIRED（parse*前缀）
- **核心算法/处理逻辑**：
  1. 文件空检查
  2. 使用ExcelParser解析Excel
  3. 清空原有印章信息 `certificateDao.deleteSealInfo()`
  4. 遍历每个Sheet，从第2行开始读取：
     - 解析7列数据（ID/名称/信息/描述/用户/领取时间/归还时间/备注）
     - 空白单元格继承上一行的值（prevName/prevInfo/prevDesc）
     - 设置上传人
     - 逐行插入数据库
- **异常处理**：
  | 异常类型 | 触发条件 | 处理方式 |
  |----------|----------|----------|
  | InvalidFormatException/IOException | Excel格式错误或读取失败 | e.printStackTrace() + 抛出RuntimeException |

---

## 23. WarrantyCallbackServiceImpl

### 类概述
- 实现接口：`WarrantyCallbackService`
- 事务代理Bean：`warrantyCallbackServiceAgent`
- 依赖的DAO：`WarrantyCallbackDao`
- 跨Service依赖：`BasicDataService`, `CallBackService`, `TaskService(Activiti)`, `SendMailService`, `UserManageService`, `PmClosedLoopDao`, `WorkFlowService`, `DepartmentManageService`

### 方法列表

#### `ProjectWarrantyCallback selectProjectWarrantyCallbackById(Integer)`
- **功能描述**：根据ID查询维保回访记录
- **事务类型**：无事务

#### `ProjectWarrantyCallbackVO selectProjectWarrantyCallbackVOById(Integer)`
- **功能描述**：根据ID查询维保回访记录（含扩展信息）
- **事务类型**：无事务

#### `List<ProjectWarrantyCallback> selectProjectWarrantyCallbackList(ProjectWarrantyCallback)`
- **功能描述**：查询维保回访记录列表
- **事务类型**：无事务

#### `List<ProjectWarrantyCallbackVO> selectProjectWarrantyCallbackVOList(ProjectWarrantyCallback)`
- **功能描述**：查询维保回访记录VO列表
- **事务类型**：无事务

#### `List<ProjectWarrantyCallbackVO> selectProjectWarrantyCallbackVOListPageable(WarrantyCallbackPageParam)`
- **功能描述**：分页查询维保回访记录列表
- **事务类型**：无事务
- **核心算法**：
  1. 查询总数
  2. 非导出模式：每页50条
  3. 导出模式：不分页
  4. 设置分页参数后查询

#### `void insertProjectWarrantyCallback(ProjectWarrantyCallback)`
- **功能描述**：插入维保回访记录
- **事务类型**：REQUIRED（@Transactional注解）
- **核心算法**：自动设置createBy、createTime

#### `void insertProjectWarrantyCallbackSelective(ProjectWarrantyCallback)`
- **功能描述**：选择性插入维保回访记录（仅插入非null字段）
- **事务类型**：REQUIRED（@Transactional注解）
- **核心算法**：自动设置createBy、createTime

#### `void updateProjectWarrantyCallbackByIdSelective(ProjectWarrantyCallback)`
- **功能描述**：选择性更新维保回访记录
- **事务类型**：无事务（update*前缀匹配事务规则，但未使用@Transactional注解）
- **核心算法**：自动设置updateBy、updateTime

#### `void insertOrUpdateProjectWarrantyCallback(ProjectWarrantyCallbackVO)`
- **功能描述**：新增或更新维保回访记录
- **事务类型**：无事务（insertOrUpdate*不匹配事务前缀规则）
- **核心算法**：id为null或0时调用insert，否则调用update
- **注意事项**：⚠️ 写操作方法无事务保护

#### `List<Map<String, Object>> selectProjectWarrantyCallbackMapList(ProjectWarrantyCallbackVO, DisplayParam)`
- **功能描述**：查询维保回访记录Map列表（含分页）
- **事务类型**：无事务

#### `ProjectWarrantyCallbackVO fillProjectWarrantyInfo(ProjectWarrantyCallbackVO)`
- **功能描述**：填充项目维保信息
- **事务类型**：无事务
- **核心算法**：
  1. 根据projectId查询维保信息Map
  2. 设置维保状态和维保级别
  3. 使用BeanUtils.populate填充基础属性
  4. 合并customInfo中的自定义字段

#### `Map<String, Object> selectProjectWarrantyByProjectId(Integer)`
- **功能描述**：根据项目ID查询维保信息
- **事务类型**：无事务

#### `List<Map<String, Object>> selectProjectWarranty(ProjectWarrantyCallbackVO)` / `selectProjectWarranty(ProjectWarrantyCallbackVO, DisplayParam)`
- **功能描述**：查询维保统计数据
- **事务类型**：无事务

#### `List<Map<String, Object>> selectCustomerProjectWarrantyCallbackStatistics(ProjectWarrantyCallbackVO, DisplayParam)`
- **功能描述**：查询客户项目维保回访统计
- **事务类型**：无事务

#### `List<Task> queryWarrantyCallbackTaskList(...)` （8 重载版本，**impl-only**）
- **功能描述**：查询维保回访待办任务列表
- **事务类型**：无事务
- **归属说明**：⚠️ 此 8 个重载方法仅存在于 `WarrantyCallbackServiceImpl` 实现类，**不在 `WarrantyCallbackService` 接口中声明**。底层 5 参版本为核心实现，其余 7 个重载均为委派包装
- **源码行号**：`WarrantyCallbackServiceImpl.java:300-406`

**重载版本清单**（按参数数量递增）：

| # | 签名 | 行号 | 说明 |
|---|------|------|------|
| 1 | `queryWarrantyCallbackTaskList(String taskKey)` | 300 | 最简形式，仅按 taskKey 查询 |
| 2 | `queryWarrantyCallbackTaskList(String taskKey, String taskId)` | 310 | + 指定任务ID |
| 3 | `queryWarrantyCallbackTaskList(String taskKey, Integer projectWarrantyCallbackId)` | 320 | + 指定维保单ID |
| 4 | `queryWarrantyCallbackTaskList(String taskKey, Integer projectWarrantyCallbackId, String taskId)` | 330 | + 维保单ID + 任务ID |
| 5 | `queryWarrantyCallbackTaskList(String taskKey, String roleGroup, Integer projectWarrantyCallbackId)` | 334 | + 显式角色组 |
| 6 | `queryWarrantyCallbackTaskList(String taskKey, String roleGroup, Integer projectWarrantyCallbackId, String taskId)` | 346 | + 任务ID |
| 7 | `queryWarrantyCallbackTaskList(String taskKey, String roleGroup, Integer projectWarrantyCallbackId, HashMap<String, Object> params)` | 358 | + 扩展参数 Map（无 taskId） |
| 8 | `queryWarrantyCallbackTaskList(String taskKey, String roleGroup, Integer projectWarrantyCallbackId, String taskId, HashMap<String, Object> params)` | 371 | **核心实现**，其余 7 个均委派到此 |

- **签名修正**：原文档声称"6 个重载版本"，实际为 **8 个**
- **核心算法**（仅 #8 实现真实逻辑）：
  1. 根据 `UserContext` 获取当前登录用户与区域权限
  2. 设置基础查询参数：`assignee`、`areaPower`、`taskKey`（支持 `;` 分隔多值）、`taskId`、`projectWarrantyCallbackId`
  3. 当 `roleGroup` 为空时按用户角色自动判定（`emRole`/`cbRole`/`role_warranty_callbacker`/`zrRole`）
  4. 区域负责人角色额外设置 `checkProfitDep=true` 用于利润部门过滤
  5. 调用 `dao.queryWarrantyCallbackTaskList(params)` 返回 `List<Task>`

#### `List<Project> queryProjectList(Project)` / `queryProjectList(ProjectWarrantyCallback)`
- **功能描述**：查询项目列表
- **事务类型**：无事务

---

## 附录A：事务方法前缀速查表

| 前缀 | 事务类型 | 说明 |
|------|----------|------|
| `insert*` | PROPAGATION_REQUIRED | 插入操作 |
| `update*` | PROPAGATION_REQUIRED | 更新操作 |
| `delete*` | PROPAGATION_REQUIRED | 删除操作 |
| `add*` | PROPAGATION_REQUIRED | 添加操作 |
| `save*` | PROPAGATION_REQUIRED | 保存操作 |
| `do*` | PROPAGATION_REQUIRED | 执行操作 |
| `keep*` | PROPAGATION_REQUIRED | 保留/保存操作 |
| `start*` | PROPAGATION_REQUIRED | 启动操作 |
| `submit*` | PROPAGATION_REQUIRED | 提交操作 |
| `parse*` | PROPAGATION_REQUIRED | 解析操作 |
| `query*` | 无事务 | 查询操作 |
| `find*` | 无事务 | 查找操作 |
| `get*` | 无事务 | 获取操作 |
| `check*` | 无事务 | 检查操作 |
| `select*` | 无事务 | 选择操作 |
| `back*` | 无事务 | 回退操作 |
| `edit*` | 无事务 | 编辑操作 |
| `change*` | 无事务 | 变更操作 |
| `refresh*` | 无事务 | 刷新操作 |

## 附录B：ServiceAgent代理Bean映射表

| Service Bean | Agent Bean | DAO |
|-------------|-----------|-----|
| loginService | loginServiceAgent | LoginDao |
| userManageService | userManageServiceAgent | UserManageDao |
| roleManageService | roleManageServiceAgent | RoleManageDao |
| departmentManageService | departmentManageServiceAgent | DepartmentManageDao |
| passwordService | passwordServiceAgent | PasswordDao |
| opLogService | opLogServiceAgent | OpLogDao |
| workspaceService | workspaceServiceAgent | WorkSpaceDao |
| sendMailService | sendMailServiceAgent | SendMailDao |
| projectService | projectServiceAgent | ProjectDao |
| basicDataService | basicDataServiceAgent | BasicDataDao |
| projectPlanService | projectPlanServiceAgent | ProjectPlanDao |
| pmClosedLoopService | pmClosedLoopServiceAgent | PmClosedLoopDao |
| callBackService | callBackServiceAgent | CallBackDao |
| presalesService | presalesServiceAgent | PresalesDao |
| pmClosedLoopQuesnaireService | pmClosedLoopQuesnaireServiceAgent | PmClosedLoopQuesnaireDao |
| dataAnalysisService | dataAnalysisServiceAgent | DataAnalysisDao |
| report | reportServiceAgent | ReportDao |
| probManage | probManageServiceAgent | ProbManageDao |
| subcontractService | subcontractServiceAgent | SubcontractDao |
| certificateService | certificateServiceAgent | CertificateDao |
| warrantyCallbackService | warrantyCallbackServiceAgent | WarrantyCallbackDao |
| workFlowService | **无代理** | WorkflowDao |

### ⚠️ 事务风险：跨Service依赖注入使用直接Bean而非Agent

在 `applicationContext-service.xml` 中，`workspaceService` 和 `projectService` 的跨Service依赖注入使用的是**直接Bean**而非**事务代理Bean（Agent）**，存在事务失效风险：

| Service Bean | 注入的跨Service依赖 | 配置中的ref | 应使用的ref | 风险说明 |
|-------------|-------------------|------------|-----------|---------|
| workspaceService | pmClosedLoopService | `pmClosedLoopService` | `pmClosedLoopServiceAgent` | 通过直接Bean调用写操作方法时事务不生效 |
| workspaceService | basicDataService | `basicDataService` | `basicDataServiceAgent` | 同上 |
| projectService | callBackService | `callBackService` | `callBackServiceAgent` | 同上 |
| pmClosedLoopService | projectService | `projectService` | `projectServiceAgent` | 同上 |
| pmClosedLoopService | userManageService | `userManageService` | `userManageServiceAgent` | 同上 |
| pmClosedLoopService | sendMailService | `sendMailService` | `sendMailServiceAgent` | 同上 |
| subcontractService | basicDataService | `basicDataService` | `basicDataServiceAgent` | 同上 |
| subcontractService | callBackService | `callBackService` | `callBackServiceAgent` | 同上 |
| warrantyCallbackService | basicDataService | `basicDataService` | `basicDataServiceAgent` | 同上 |
| warrantyCallbackService | callBackService | `callBackService` | `callBackServiceAgent` | 同上 |

**影响分析**：
- 当 `workspaceService` 调用 `pmClosedLoopService` 的写操作方法（如 `addPmCLApply()`）时，该方法的事务代理不生效，操作不在事务中执行
- 当 `pmClosedLoopService` 调用 `projectService.insertOrUpdateProjectState()` 时，虽然 `ProjectServiceImpl` 内部通过 `@Lazy @Autowired` 自引用解决了自调用事务问题，但外部通过直接Bean调用时事务代理同样不生效
- **注意**：`projectService` 内部的自引用 `@Lazy @Autowired ProjectService projectService` 注入的是直接Bean（`@Qualifier("projectService")`），而非Agent，因此自引用调用的事务也依赖 `@Transactional` 注解而非 `TransactionProxyFactoryBean` 代理

**建议修复**：将上述跨Service依赖的 `ref` 改为对应的 Agent Bean（如 `ref="pmClosedLoopServiceAgent"`），确保事务代理在跨Service调用时生效。

## 附录C：跨Service调用关系图

```
ProjectServiceImpl
  ├──→ BasicDataService (查询系统参数)
  ├──→ CallBackService (回访流程操作)
  ├──→ PmClosedLoopService (闭环流程操作)
  ├──→ TaskService/Activiti (流程任务操作)
  ├──→ SendMailService (邮件发送)
  └──→ ProjectService (自引用，解决事务代理问题)

PmClosedLoopServiceImpl
  ├──→ WorkFlowService (流程引擎操作)
  ├──→ SendMailService (邮件发送)
  ├──→ UserManageService (用户查询)
  └──→ ProjectService (项目状态更新)

PresalesServiceImpl
  ├──→ WorkFlowService (流程引擎操作)
  ├──→ BasicDataService (系统参数/文件查询)
  └──→ UserManageDao (用户查询)

CallBackServiceImpl
  ├──→ WorkFlowService (流程引擎操作)
  ├──→ ProjectService (项目状态更新)
  └──→ PmClosedLoopService (闭环任务查询)

PasswordServiceImpl
  └──→ LoginService (重新登录)

WorkSpaceServiceImpl
  ├──→ PmClosedLoopService (闭环任务查询)
  ├──→ WorkFlowService (流程任务查询)
  ├──→ BasicDataService (基础数据查询)
  └──→ SubcontractService (转包任务查询)

SubcontractServiceImpl
  ├──→ BasicDataService (系统参数)
  ├──→ CallBackService (回访操作)
  ├──→ SendMailService (邮件发送)
  ├──→ UserManageService (用户查询)
  ├──→ WorkFlowService (流程操作)
  └──→ DepartmentManageService (部门查询)

WarrantyCallbackServiceImpl
  ├──→ BasicDataService (系统参数)
  ├──→ CallBackService (回访操作)
  ├──→ TaskService/Activiti (流程任务查询)
  ├──→ SendMailService (邮件发送)
  ├──→ UserManageService (用户查询)
  ├──→ PmClosedLoopDao (闭环数据查询)
  ├──→ WorkFlowService (流程操作)
  └──→ DepartmentManageService (部门查询)
```

## 附录D：核心状态机转换

### 项目状态（projectState）
```
PROJECT_STATE_30 (已创建) ──指定服务经理──→ PROJECT_STATE_31 (待指派项目经理)
PROJECT_STATE_31 ──指定项目经理──→ PROJECT_STATE_32 (已指派项目经理)
PROJECT_STATE_32 ──闭环通过──→ PROJECT_STATE_CLOSEDLOOP (已闭环)
任意状态 ──不予跟踪──→ PROJECT_STATE_DENY (不予跟踪)
```

### 项目闭环流程状态（closeProcessState）
```
10 (项目跟踪) ──可闭环──→ 15 (可闭环)
10/15 ──发起回访──→ 30 (回访中)
30 ──回访完成──→ 10 (项目跟踪)
任意 ──闭环结束──→ 50 (闭环结束)
```

### 售前项目审批流程
```
创建 → 服务经理指定项目经理(usertask2) → 项目经理跟踪(serviceApprove)
  → 工程管理部审核(emRole) → 闭环/回访
```

### 闭环审批流程
```
项目经理申请(PM) → 服务经理审核(SM) → 回访人员回访(CB) → 工程管理部闭环(CL) → 结束(END)
```

---

## 附录E：IAbstractBaseService — MyBatis 风格泛型基类接口

> 📌 **2026-06-30 新增**：本附录补充此前遗漏的 `IAbstractBaseService<T>` 抽象基类接口文档。该接口是 PMS-struts 各业务 Service（如 `WarrantyCallbackService`）的根接口，定义了 8 个通用 CRUD 方法契约。

### 类概述

- **接口全限定名**：`com.dp.plat.extend.mybatis.service.IAbstractBaseService<T>`
- **源码位置**：`PMS/PMS-struts/src/com/dp/plat/extend/mybatis/service/IAbstractBaseService.java`
- **类型**：泛型接口（`<T>` 为实体类型）
- **继承关系**：无父接口
- **直接继承该接口的业务 Service**：`WarrantyCallbackService`（其他 PMS-struts 业务 Service 多继承 `BaseService`，与 core 模块的 `IAbstractBaseService` 是同名但不同包的两个独立接口）

### ⚠️ 与 core 模块同名接口的差异

PMS 项目存在**两个同名但不同包**的 `IAbstractBaseService<T>` 接口：

| 接口 | 包路径 | 方法数 | 差异 |
|------|--------|--------|------|
| **PMS-struts 版本** | `com.dp.plat.extend.mybatis.service` | 8 | 无分页方法 |
| **core 模块版本** | `com.dp.plat.core.service` | 10 | 多出 `countBySelectivePageable` 和 `selectBySelectivePageable` 两个分页方法，供 PMS-springmvc 使用 |

文档引用时必须明确包路径，避免混淆。本附录仅描述 PMS-struts 版本。

### 方法列表（共 8 个）

| # | 方法签名 | 事务前缀匹配 | 功能 |
|---|---------|------------|------|
| 1 | `int deleteByPrimaryKey(Object pk)` | `delete*`（事务） | 按主键删除 |
| 2 | `int insert(T t)` | `insert*`（事务） | 插入（全字段） |
| 3 | `int insertSelective(T t)` | `insert*`（事务） | 选择性插入（仅非空字段） |
| 4 | `T selectByPrimaryKey(Object pk)` | `select*`（非事务） | 按主键查询 |
| 5 | `int updateByPrimaryKeySelective(T t)` | `update*`（事务） | 选择性更新（仅非空字段） |
| 6 | `int updateByPrimaryKey(T t)` | `update*`（事务） | 更新（全字段） |
| 7 | `long countBySelective(T t)` | `count*`（非事务） | 条件计数 |
| 8 | `List<T> selectBySelective(T t)` | `select*`（非事务） | 条件查询所有 |

### 事务行为说明

- 接口本身不声明事务，事务由 `applicationContext-service.xml` 中的 `*ServiceAgent`（`TransactionProxyFactoryBean`）按方法名前缀应用 `PROPAGATION_REQUIRED` 规则
- **事务前缀规则**（与本文件头部声明一致）：`insert*` | `update*` | `delete*` | `add*` | `save*` | `do*` | `keep*` | `start*` | `submit*` | `parse*`
- **非事务前缀**：`query*` | `find*` | `get*` | `check*` | `select*` | `back*` | `edit*` | `count*` 及其他

### 实现示例（参考 `WarrantyCallbackServiceImpl`）

```java
public class WarrantyCallbackServiceImpl extends BaseServiceImpl
        implements WarrantyCallbackService {

    // 显式实现 IAbstractBaseService<ProjectWarrantyCallback> 的 8 个方法
    @Override
    public int deleteByPrimaryKey(Object pk) { ... }

    @Override
    public int insert(ProjectWarrantyCallback t) { ... }

    // ... 其余 6 个方法
}
```

### 历史修订记录

- **2026-06-30 新增**：原 `service-methods-reference.md` 完全未文档化 `IAbstractBaseService` 抽象基类，导致读者无法理解各业务 Service 中"基础 CRUD 方法"的具体契约。本附录补充完整的 8 个方法签名与事务行为说明，并明确标注与 core 模块同名接口的差异。
