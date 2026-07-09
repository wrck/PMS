# 001-user-auth 域规格草稿(Spec Reverse-Draft)

> 来源:逆向反推自 PMS-struts + core 代码,日期 2026-07-09
> 域职责:用户认证、授权、组织架构、登录会话、密码管理、CAS 单点登录
> 证据基线:
> - 老架构(Struts):`PMS-struts/config/struts-sys.xml`、`PMS-struts/config-ibaits/sql-map-admin-config.xml`(1060 行)、`PMS-struts/src/com/dp/plat/action/`、`PMS-struts/src/com/dp/plat/service/LoginServiceImpl.java`
> - 新架构(过渡):`core/src/main/java/com/dp/plat/core/controller/admin/`、`core/src/main/java/com/dp/plat/core/cas/`

---

## 第1章 用户故事

> 从控制器/Action 端点反推。每个端点至少 1 个故事。证据标注文件:行号。
> 端点路径分两套:老架构 `.action`(Struts,namespace `/` 与 `/base`)、新架构 REST 路径(过渡,前缀 `/system`)。

### US-001: 账号密码登录
- **故事**: As a 系统用户, I want 通过用户名和密码登录系统, so that 我能访问被授权的功能。
- **证据**: struts-sys.xml:13 (Login.action,execute 方法);LoginAction.java:41-42,91-117;LoginServiceImpl.java:25-116

### US-002: CAS 单点登录
- **故事**: As a 企业用户, I want 通过 CAS 单点登录直接进入系统而无需再次输入密码, so that 我能在多个系统间无缝切换。
- **证据**: struts-sys.xml:13 (Login.action);LoginAction.java:38-39,45-89;SingleSignOutHandler.java:20-145;CasLogoutFilter.java:17-67

### US-003: 登出
- **故事**: As a 已登录用户, I want 登出系统, so that 我的会话被终止、防止他人继续操作。
- **证据**: struts-sys.xml:19 (Logout.action);LoginAction.java:119-135;CasLogoutFilter.java:48-66

### US-004: 用户列表查询
- **故事**: As a 管理员, I want 分页查询用户列表并按用户名/角色/部门筛选, so that 我能了解系统所有用户情况。
- **证据**: struts-sys.xml:30 (UserManage.action);UserManageAction.java:58-79;UserController.java:73-93 (`GET /system/user/list`);sql-map-admin-config.xml:193-221,173-186

### US-005: 新增用户
- **故事**: As a 管理员, I want 创建新用户并分配角色、菜单、部门, so that 新员工能登录系统使用对应功能。
- **证据**: struts-sys.xml:44 (UserAdd.action);UserManageAction.java:108-143;UserController.java:144-192 (`POST /system/user/detail`)

### US-006: 编辑用户
- **故事**: As a 管理员, I want 修改用户基本信息、角色、菜单、部门权限, so that 用户权限变动能及时生效。
- **证据**: struts-sys.xml:35 (UserEdit.action);UserManageAction.java:154-190;UserController.java:194-276 (`PUT /system/user/{userId}`)

### US-007: 删除用户
- **故事**: As a 管理员, I want 删除用户及其角色关联, so that 离职员工账号不再占用系统资源。
- **证据**: UserController.java:278-309 (`DELETE /system/user/{id}`)

### US-008: 用户名唯一性校验
- **故事**: As a 管理员, I want 在创建用户时校验用户名是否已存在, so that 避免账号冲突。
- **证据**: UserManageAction.java:149-152 (checkUsername);UserController.java:311-329 (`POST /system/user/checkUnique`);sql-map-admin-config.xml:877-881

### US-009: 密码重置
- **故事**: As a 管理员, I want 重置用户密码为随机密码并强制下线, so that 用户忘记密码时能重新获得访问。
- **证据**: UserManageAction.java:203-229 (pwdreset)

### US-010: 修改密码
- **故事**: As a 用户, I want 修改自己的登录密码, so that 密码更安全。
- **证据**: sql-map-admin-config.xml:248-253 (update-user-chageloginpass,密码过期时间 +3 个月);UserManageAction.java:203-229 (复用密码处理逻辑)

### US-011: 角色列表查询
- **故事**: As a 管理员, I want 分页查询角色列表, so that 我能管理系统中所有角色。
- **证据**: struts-sys.xml:54 (RoleManage.action);RoleManageAction.java:30-43;RoleController.java:38-50 (`GET /system/role/list`);sql-map-admin-config.xml:366-390

### US-012: 新增角色(含菜单权限)
- **故事**: As a 管理员, I want 创建角色并为其分配菜单及操作权限(增/删/查/改), so that 该角色用户获得对应功能权限。
- **证据**: struts-sys.xml:76 (RoleAddSubmit.action);RoleManageAction.java:50-72;RoleController.java:64-69;sql-map-admin-config.xml:405-419

### US-013: 编辑角色(含菜单权限)
- **故事**: As a 管理员, I want 修改角色信息及其菜单权限, so that 角色权限变更能反映到关联用户。
- **证据**: struts-sys.xml:85 (RoleEditSubmit.action);RoleManageAction.java:99-123;RoleController.java:71-76;sql-map-admin-config.xml:422-428

### US-014: 删除角色
- **故事**: As a 管理员, I want 删除不再使用的角色, so that 角色列表保持精简。
- **证据**: RoleController.java:78-81 (`DELETE /system/role/{id}`)

### US-015: 菜单管理(树)
- **故事**: As a 管理员, I want 维护系统菜单树(含层级关系), so that 功能导航结构清晰可配置。
- **证据**: MenuController.java:36-73;sql-map-admin-config.xml:79-88,70-83

### US-016: 角色菜单授权
- **故事**: As a 管理员, I want 为角色批量勾选菜单, so that 角色用户能访问对应菜单。
- **证据**: RoleMenuController.java:41-61 (`POST /system/rolemenu/updateRoleMenu`);sql-map-admin-config.xml:398-402,414-419

### US-017: 用户角色绑定/解绑
- **故事**: As a 管理员, I want 批量为用户绑定或解绑角色(支持多租户), so that 用户能获得或失去一组角色权限。
- **证据**: UserRoleController.java:77-98 (`POST /system/userrole/bind`,`DELETE /system/userrole/unbind`)

### US-018: 部门列表查询
- **故事**: As a 管理员, I want 分页查询部门列表, so that 我能管理组织架构。
- **证据**: struts-sys.xml:95 (DepartmentManage.action);DepartmentManageAction.java:18-31;sql-map-admin-config.xml:467-491

### US-019: 部门从 SAP 同步刷新
- **故事**: As a 管理员, I want 从 SAP 数据源同步最新部门信息, so that PMS 部门数据与组织主数据一致。
- **证据**: struts-sys.xml:99 (DepartmentRefresh.action);DepartmentManageAction.java:33-36;sql-map-admin-config.xml:442-451 (truncate + 查询 `dp_reports.dp_v_spms_department`)

### US-020: 部门新增
- **故事**: As a 管理员, I want 新增部门, so that 新设组织单元能被纳入系统。
- **证据**: struts-sys.xml:125 (DepartmentAddSubmit.action);DepartmentManageAction.java:43-49;sql-map-admin-config.xml:493-501

### US-021: 基础数据管理
- **故事**: As a 管理员, I want 维护基础数据字典(按类型分类), so that 业务表单的下拉选项可动态配置。
- **证据**: struts-sys.xml:134-152 (BasicdataManage/Update/Insert.action);sql-map-admin-config.xml:785-856

### US-022: 操作日志查询
- **故事**: As a 管理员, I want 分页查询系统操作日志, so that 我能审计用户行为。
- **证据**: sql-map-admin-config.xml:559-597 (select-Operation-Log,select-Operation-Log-Sum)

### US-023: 任务委派规则设置
- **故事**: As a 用户, I want 将我的流程任务委派给他人(按流程定义、按时间段), so that 我外出期间流程不被阻塞。
- **证据**: sql-map-admin-config.xml:626-670 (dp_act_procdef_delegate)

### US-024: 邮件通知(账号开通/密码重置)
- **故事**: As a 系统, I want 在用户新增或密码重置时自动发送通知邮件, so that 用户能获知账号与初始密码。
- **证据**: UserManageAction.java:122-139,211-222 (MailUtil.keepMailWithTemplate);UserController.java:181-190;sql-map-admin-config.xml:685-727

---

## 第2章 功能需求

> 处理规则以行为描述,不出现类名/注解。

### FR-001: 账号密码登录(非 CAS 模式)
- **触发条件**: 用户在登录页提交用户名+密码(+验证码),且系统未启用 CAS。
- **输入**: 用户名(username)、密码(password)、验证码(validation,可选)、客户端 IP。
- **处理规则**:
  1. 读取系统参数 `sys.envirment.argu`:若值为 `1`(生产环境)则校验验证码与当前会话中存储的验证码一致,不一致则失败。
  2. 按用户名查询有效用户(status=1,且在有效期内)。
  3. 若环境参数非 `1`(测试环境),忽略提交密码,直接使用库中密码。
  4. 比对提交密码与库中密码(忽略大小写);一致则继续,否则返回"用户名或密码错误"。
  5. 加载用户菜单权限(menuCode→menuValue)。
  6. 计算区域权限(areapower):若为 `-1` 且 dpNo 非空,用 dpNo 替换;若 areapower 不含 dpNo,则追加 dpNo;再按市场/用服办事处规则补充 [待澄清]。
  7. 按 roleIds(格式 `;id;` 分隔)逐个角色加载角色菜单操作权限,menuPower 编码:`8`=新增,`1`=删除,`4`=查询,`2`=更新,逗号分隔。
  8. 查询用户默认页面(defaultPage)。
  9. 将用户身份、菜单权限、角色菜单操作权限、默认页面写入会话上下文。
  10. 记录登录操作日志。
- **输出**: 登录成功跳转至用户默认页面;失败返回登录页并提示错误。
- **异常**: 验证码错误;用户不存在;密码错误;用户被停用(status≠1);用户不在有效期。
- **证据**: LoginServiceImpl.java:25-116;sql-map-admin-config.xml:166-171,37-43,877-881

### FR-002: CAS 单点登录
- **触发条件**: 系统启用 CAS(`sys.cas=1`),用户被重定向至 CAS 服务器完成认证后回调本系统。
- **输入**: CAS 票据(ticket)、客户端 IP。
- **处理规则**:
  1. 从 CAS 断言中提取用户名(principal.name)。
  2. 用户名为空或用户不存在则登出并返回 CAS 错误页。
  3. 不校验密码,直接执行 FR-001 步骤 5-10 的后续加载流程。
  4. CAS 票据与会话 ID 建立映射并存储,用于后续单点登出。
- **输出**: 登录成功跳转默认页面;失败跳转 CAS 错误页。
- **异常**: CAS 断言缺失;用户不存在于本系统;加载权限异常。
- **证据**: LoginAction.java:45-89;LoginServiceImpl.java:118-196;SingleSignOutHandler.java:99-141

### FR-003: CAS 单点登出
- **触发条件**: CAS 服务器向本系统发送 POST 请求,参数含 `logoutRequest`。
- **处理规则**:
  1. 从 `logoutRequest` XML 中解析 `SessionIndex`(即原 ticket)。
  2. 根据 ticket 查找已记录的会话 ID,获取对应会话。
  3. 在会话上标记 `logoutRequest=true`,使其失效。
  4. 后续请求检测到该标记时执行 subject 登出。
  5. 非登出请求继续过滤链。
- **输出**: 对应用户会话被终止。
- **证据**: CasLogoutFilter.java:40-67;SingleSignOutHandler.java:117-141

### FR-004: 主动登出
- **触发条件**: 用户点击登出。
- **处理规则**:
  1. CAS 模式:重定向至 `https://cas.dptech.com:8443/logout?service={当前URL前缀}/Login.action`。
  2. 非 CAS 模式:重定向至 `index.jsp`。
  3. 销毁当前会话。
- **输出**: 跳转至对应登出/首页地址。
- **证据**: LoginAction.java:119-135

### FR-005: 用户列表分页查询
- **触发条件**: 管理员访问用户管理页(需具备 admin/engineemanager/engineemanager-leader 任一角色)。
- **输入**: 分页参数(offset、pagesize、sort、order)、筛选条件(username/realName 模糊、roleIds 模糊、dpNo)。
- **处理规则**:
  1. 权限校验:无上述角色则抛出"没有访问权限"。
  2. 按 username/realName 模糊、roleIds 模糊、dpNo(等于或 FIND_IN_SET 于 areapower)动态拼装查询。
  3. 支持排序字段与方向,支持 limit 分页。
  4. 查询结果中 roleIds(格式 `;id;`)转换为角色名展示串。
  5. 同时返回角色下拉、部门下拉供筛选。
- **输出**: 用户列表(含角色名)、总数。
- **异常**: 无权限。
- **证据**: UserManageAction.java:50-79;sql-map-admin-config.xml:173-221
- **新架构补充**: 支持按公司(compId)隔离查询,返回 total/filtered 两种计数。证据:UserController.java:73-93

### FR-006: 新增用户
- **触发条件**: 管理员提交新增用户表单。
- **输入**: username、realName、email、roleids、usermenuids、defaultPage、dpNo、customInfo(可选)。
- **处理规则**:
  1. 校验 username/realName/email/usermenuids/defaultPage 均非空且不含空格。
  2. 生成 8 位随机密码,以 MD5(password+username) 方式加密存储。
  3. 默认 status=1、isemail=1,createTime/effectiveFrom=now。
  4. 写入用户主记录,取回自增主键。
  5. 非 CAS 模式下,发送"账号已开通"邮件(含明文随机密码)至用户邮箱。
  6. 老架构:写入用户菜单授权(fnd_user_menus,menuValue=1);新架构:写入用户角色关联(含 compId)。
- **输出**: 新用户 ID;重定向至列表页。
- **异常**: 字段校验失败;用户名重复。
- **证据**: UserManageAction.java:108-143;UserController.java:144-192;sql-map-admin-config.xml:268-294

### FR-007: 编辑用户
- **触发条件**: 管理员提交编辑用户表单。
- **输入**: id、username、email、realName、status、roleids、usermenuids、defaultPage、dpNo、effectiveTo、customInfo、changeType(可选)、newMemberCode(可选)。
- **处理规则**:
  1. 字段校验同 FR-006。
  2. 若 status=0(停用),设置 effectiveTo=now。
  3. customInfo 以 JSON_MERGE_PATCH 与现有值合并(IFNULL 时以 `{}` 为底)。
  4. 若提供 changeType(service/program/both)与 newMemberCode,联动批量更新项目服务经理/项目经理 [待澄清:具体联动规则]。
  5. 老架构:删除原用户菜单授权后重新插入;新架构:计算新旧角色差集,增量增删用户角色关联。
- **输出**: 重定向至列表页。
- **异常**: 字段校验失败;非本人且非 admin 时返回未授权(新架构)。
- **证据**: UserManageAction.java:154-190;UserController.java:194-276;sql-map-admin-config.xml:281-319

### FR-008: 删除用户
- **触发条件**: 管理员请求删除用户。
- **处理规则**:
  1. 删除该用户在当前公司下的用户扩展信息(UserInfo)。
  2. 删除该用户在当前公司下的所有用户角色关联。
  3. 若该用户在所有公司下均无扩展信息残留,则删除用户主记录。
- **输出**: 无返回体。
- **证据**: UserController.java:278-309

### FR-009: 用户名唯一性校验
- **触发条件**: 新增用户前实时校验。
- **输入**: userName。
- **处理规则**:
  1. 老架构:统计 fnd_user_info 中 username 命中且在有效期内记录数,返回计数。
  2. 新架构:按用户名查用户主记录;若用户主记录存在但当前公司下无扩展信息,仍视为可用。
- **输出**: 是否可用(valid=true/false)。
- **证据**: UserManageAction.java:149-152;UserController.java:311-329;sql-map-admin-config.xml:877-881

### FR-010: 密码重置
- **触发条件**: 管理员对某用户执行密码重置。
- **处理规则**:
  1. 查询用户信息。
  2. 生成随机密码并以 MD5(password+username) 加密。
  3. 设置 pwdoverdue=now(立即过期,强制改密)。
  4. 更新用户密码。
  5. 发送"账号密码已重置"邮件(含明文随机密码)。
  6. 调用密码服务将该用户强制下线。
- **输出**: result=1。
- **证据**: UserManageAction.java:203-229

### FR-011: 修改密码
- **触发条件**: 用户提交修改密码。
- **输入**: id、newPassword。
- **处理规则**:
  1. 更新 password=newPassword。
  2. 设置 pwdoverdue=now+3 月(3 个月后过期)。
- **输出**: 更新成功。
- **证据**: sql-map-admin-config.xml:248-253

### FR-012: 角色列表分页查询
- **触发条件**: 管理员访问角色管理。
- **输入**: 分页参数、roleName(模糊)、id(精确非 0)。
- **处理规则**: 按条件动态拼装,支持排序与 limit 分页。
- **输出**: 角色列表(含 roleName/status/defaultPage/roleRemark/effectiveFrom/effectiveTo)。
- **证据**: RoleManageAction.java:30-43;RoleController.java:38-50;sql-map-admin-config.xml:352-390

### FR-013: 新增角色
- **触发条件**: 管理员提交新增角色表单。
- **输入**: roleName、status、roleRemark、effectiveTo、rolemenuidList(菜单权限列表)。
- **处理规则**:
  1. 校验:rolemenuidList 非空;roleName 非空且不含空格。
  2. 默认页面固定写入 `module/Welcome1.action` [待澄清:是否应可配置]。
  3. 若 status=0,effectiveTo=now。
  4. 写入角色主记录,取回自增主键。
  5. 逐条写入角色菜单权限(fnd_role_menus:roleId/menuPower/menuId)。
- **输出**: 新角色 ID。
- **异常**: 菜单权限为空;角色名非法。
- **证据**: RoleManageAction.java:50-72;sql-map-admin-config.xml:405-419

### FR-014: 编辑角色
- **触发条件**: 管理员提交编辑角色表单。
- **处理规则**:
  1. 校验同 FR-013。
  2. 更新角色主记录(含默认页面固定值)。
  3. 删除原角色菜单权限,重新插入新菜单权限。
  4. 角色/菜单操作触发用户缓存刷新。
- **输出**: 更新结果。
- **证据**: RoleManageAction.java:99-123;sql-map-admin-config.xml:398-402,414-428

### FR-015: 菜单管理
- **触发条件**: 管理员访问菜单管理。
- **处理规则**:
  1. 查询菜单树(superId 父子关系,有效期内)。
  2. 详情查询时若 pid 非 0,补全父菜单名称。
  3. 新增/编辑/删除菜单。
  4. 删除菜单时联动删除该菜单在所有角色下的角色菜单关联。
- **输出**: 菜单树/菜单详情。
- **证据**: MenuController.java:36-73;sql-map-admin-config.xml:70-88

### FR-016: 角色菜单授权(批量)
- **触发条件**: 管理员为某角色勾选菜单并提交。
- **输入**: roleId、menuIds(逗号分隔)。
- **处理规则**:
  1. 删除该角色全部角色菜单关联。
  2. 若 menuIds 非空,按逗号拆分批量插入新关联。
- **输出**: 无返回体。
- **证据**: RoleMenuController.java:53-61

### FR-017: 用户角色绑定/解绑
- **触发条件**: 管理员批量绑定或解绑用户角色。
- **输入**: userRoleListStr(JSON 数组,含 userId/roleId) 或 ids(JSON 整数数组)。
- **处理规则**:
  1. 绑定:解析 JSON,为每条记录补 compId(当前组织)后批量插入。
  2. 解绑:按主键批量删除。
- **输出**: 无返回体。
- **证据**: UserRoleController.java:77-98

### FR-018: 部门列表分页查询
- **触发条件**: 管理员访问部门管理。
- **输入**: 分页参数、departmentNum(模糊)、departmentName(模糊)。
- **处理规则**: 动态拼装查询,支持排序与分页。
- **输出**: 部门列表。
- **证据**: DepartmentManageAction.java:18-31;sql-map-admin-config.xml:453-491

### FR-019: 部门从 SAP 同步刷新
- **触发条件**: 管理员触发部门刷新。
- **处理规则**:
  1. TRUNCATE 本地部门表 fnd_department。
  2. 从 SAP 视图 `dp_reports.dp_v_spms_department` 查询部门(OcrCode/OcrName)。
  3. 重新写入本地部门表。
- **输出**: 重定向至部门列表。
- **证据**: DepartmentManageAction.java:33-36;sql-map-admin-config.xml:442-451

### FR-020: 部门新增
- **触发条件**: 管理员提交新增部门。
- **输入**: departmentNum、departmentName。
- **处理规则**: 写入 fnd_department,createTime/effectiveFrom=now。
- **输出**: 新记录 ID。
- **证据**: DepartmentManageAction.java:43-49;sql-map-admin-config.xml:493-501

### FR-021: 基础数据字典维护
- **触发条件**: 管理员维护基础数据。
- **处理规则**:
  1. 按数据类型(dataTypeCode)查询在有效期内的基础数据列表,按 sortId 排序。
  2. 新增:校验 dataTypeCode+basicDataId 不重复。
  3. 更新:可改 basicDataName/sortId/effectiveTo。
  4. 支持父子结构(basicDataAttri1 关联父 basicDataId)。
- **输出**: 基础数据列表/单条。
- **证据**: sql-map-admin-config.xml:785-861

### FR-022: 操作日志记录与查询
- **触发条件**: 登录、关键操作触发日志记录;管理员查询日志。
- **处理规则**:
  1. 记录:写入 tb_sys_log(USER_NAME/IP/INFO/TIME)。
  2. 查询:LEFT JOIN 用户表补充 realName,支持排序与分页,返回总数。
- **输出**: 日志列表。
- **证据**: sql-map-admin-config.xml:541-597

### FR-023: 用户数据权限(区域权限)维护
- **触发条件**: 用户编辑时维护 areapower。
- **处理规则**:
  1. 若用户已有 fnd_user_power 记录,更新 areapower/updateTime/updateBy。
  2. 否则插入新记录(fndUserId/username/areapower/createTime/createBy/effectiveFrom)。
- **输出**: 更新/插入结果。
- **证据**: sql-map-admin-config.xml:750-761;UserManageAction.java(经 service 调用)

### FR-024: 任务委派规则设置
- **触发条件**: 用户设置流程委派。
- **输入**: owner(原负责人)、assignee(被委派人)、startTime、endTime、procdefId(流程定义,模糊)、status、cause。
- **处理规则**:
  1. 新增/更新委派记录(handleTime=now)。
  2. 按 assignee 查询生效中委派:status=1 且时间区间包含 now(或起止均为空)。
  3. 同 owner+assignee+procdefId 模糊命中只取首条。
- **输出**: 委派列表/单条。
- **证据**: sql-map-admin-config.xml:626-670

### FR-025: 缓存刷新
- **触发条件**: 用户/角色/菜单/权限写操作;或手动触发。
- **处理规则**:
  1. 写操作(insert/update/delete)触发 userCache 或 basicCache 失效。
  2. 手动刷新:更新 fnd_sys_arg 中 `sys.cache.latest.refreshTime`=now。
- **证据**: sql-map-admin-config.xml:4-30,763-771

### FR-026: 系统参数查询
- **触发条件**: 业务读取配置项。
- **处理规则**: 按 code 查询 fnd_sys_arg,需在有效期内(effectiveFrom<=now 且 effectiveTo 为空或 >now)。
- **输出**: var 值。
- **证据**: sql-map-admin-config.xml:862-875

---

## 第3章 数据契约【最关键】

> 此章为新系统复用/迁移 DB 的唯一事实来源。
> 分级说明:C=契约字段(业务必需,迁移必须保留);I=内部字段(技术实现,如审计字段);D=废弃字段(代码中已无引用)。
> 字段类型按 SQL-map 中实际使用反推(MySQL 方言)。

### 表 fnd_user_info(用户信息)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 用户主键 | 自增,唯一 | C |
| username | varchar | 否 | 登录名 | 唯一(查重 SQL 反推);不含空格 | C |
| password | varchar | 否 | 登录密码(MD5(password+username)) | 加密存储 | C |
| email | varchar | 是 | 邮箱(用于通知) | - | C |
| realName | varchar | 是 | 真实姓名 | 不含空格 | C |
| status | int | 否 | 状态:1=启用,0=停用 | 值域 {0,1} | C |
| pwdoverdue | datetime | 是 | 密码过期时间 | 改密后=now+3 月;重置后=now | C |
| roleIds | varchar | 是 | 角色ID串,格式 `;id;` 分隔(如 `;1;;2;`) | 多角色;LIKE 模糊匹配 | C |
| isemail | int | 是 | 是否发送邮件(1=是) | 新增/更新置 1 | I |
| defaultPage | varchar | 是 | 默认落地页路径(对应 fnd_menus.path) | - | C |
| dpNo | varchar | 是 | 所属部门编号(对应 fnd_department.departmentNum) | - | C |
| createBy | varchar | 是 | 创建人 | - | I |
| createTime | datetime | 是 | 创建时间 | 新增时=now | I |
| updateBy | varchar | 是 | 更新人 | - | I |
| updateTime | datetime | 是 | 更新时间 | 更新时=now | I |
| effectiveFrom | datetime | 是 | 生效起始 | 新增时=now | C |
| effectiveTo | datetime | 是 | 生效终止 | 停用时=now | C |
| customInfo | text/json | 是 | 扩展信息(JSON) | 更新时 JSON_MERGE_PATCH 合并 | C |

> 证据:sql-map-admin-config.xml:89-102,268-288,313-319;唯一性反推自 :877-881(query_username_size);状态值域反推自 :67,169(status=1)、UserManageAction.java:170(status=0→停用)。

### 表 fnd_roles(角色)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 角色主键 | 自增,唯一 | C |
| roleName | varchar | 否 | 角色名称 | 非空,不含空格 | C |
| defaultPage | varchar | 是 | 默认页面 | 新增/编辑固定 `module/Welcome1.action` | C |
| status | int | 否 | 状态:1=启用,0=停用 | 值域 {0,1} | C |
| roleRemark | varchar | 是 | 角色备注 | - | C |
| createTime | datetime | 是 | 创建时间 | 新增时=now | I |
| effectiveFrom | datetime | 是 | 生效起始 | 新增时=now | C |
| effectiveTo | datetime | 是 | 生效终止 | 停用时=now | C |
| updateTime | datetime | 是 | 更新时间 | 更新时=now | I |

> 证据:sql-map-admin-config.xml:336-344,405-428;RoleManageAction.java:61,113(默认页固定值)。

### 表 fnd_role_menus(角色菜单权限)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| roleId | int | 否 | 角色 ID(外键→fnd_roles.id) | 删除角色时联动清空 | C |
| menuId | int | 否 | 菜单 ID(外键→fnd_menus.id) | 删除菜单时联动清空 | C |
| menuPower | varchar | 否 | 操作权限编码串,逗号分隔 | 值域 `8`(新增)/`1`(删除)/`4`(查询)/`2`(更新) | C |
| createTime | datetime | 是 | 创建时间 | now | I |
| effectiveFrom | datetime | 是 | 生效起始 | now | C |

> 证据:sql-map-admin-config.xml:345-350,392-402,414-419;LoginServiceImpl.java:63-77(权限编码语义)。
> 关键不变量:更新角色权限时,先按 roleId 全删再重插(:398-402,414-419)。

### 表 fnd_menus(系统菜单)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 菜单主键 | 自增,唯一 | C |
| menuCode | varchar | 否 | 菜单编码(业务键,用户菜单关联用) | 唯一 [待澄清:未见显式 unique,但作为关联键] | C |
| menuName | varchar | 是 | 菜单名称 | - | C |
| menuLevel | int | 是 | 菜单层级 | - | C |
| superId | int | 是 | 父菜单 ID(自关联) | 树形结构 | C |
| path | varchar | 是 | 菜单路径(用于 defaultPage 关联) | - | C |
| effectiveFrom | datetime | 是 | 生效起始 | 查询时 < now | C |
| effectiveTo | datetime | 是 | 生效终止 | 查询时为空或 > now | C |

> 证据:sql-map-admin-config.xml:71-88,103-120。

### 表 fnd_user_menus(用户直接菜单授权)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 [推断] | C |
| fnd_user_id | int | 否 | 用户 ID(外键→fnd_user_info.id) | 删除用户菜单时按此清空 | C |
| username | varchar | 是 | 用户名(冗余) | - | I |
| menuCode | varchar | 否 | 菜单编码(外键→fnd_menus.menuCode) | - | C |
| menuValue | int | 否 | 授权值:1=有权限 | 值域 {1} | C |
| createTime | datetime | 是 | 创建时间 | now | I |
| effectiveFrom | datetime | 是 | 生效起始 | now;查询时 < now | C |
| effectiveTo | datetime | 是 | 生效终止 | 查询时为空或 > now | C |

> 证据:sql-map-admin-config.xml:37-59,115-120,289-299。
> 不变量:更新用户菜单时按 fnd_user_id 全删再重插(:295-299,289-294)。

### 表 fnd_user_power(用户数据权限/区域权限)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 [推断] | C |
| fndUserId | int | 否 | 用户 ID(外键→fnd_user_info.id) | 唯一约束 [推断,update 按 fndUserId 定位] | C |
| username | varchar | 是 | 用户名(冗余) | - | I |
| areapower | varchar | 是 | 区域权限串,逗号分隔部门编号 | `-1` 表示无;登录时合并 dpNo | C |
| createTime | datetime | 是 | 创建时间 | now | I |
| createBy | varchar | 是 | 创建人 | - | I |
| effectiveFrom | datetime | 是 | 生效起始 | now;查询时 < now | C |
| effectiveTo | datetime | 是 | 生效终止 | 查询时为空或 > now | C |
| updateTime | datetime | 是 | 更新时间 | now | I |
| updateBy | varchar | 是 | 更新人 | - | I |

> 证据:sql-map-admin-config.xml:750-761,103-113,166-171;LoginServiceImpl.java:47-53(areapower 合并逻辑)。

### 表 fnd_department(部门)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| departmentNum | varchar | 否 | 部门编号(业务键) | SAP 同步来源 OcrCode | C |
| departmentName | varchar | 否 | 部门名称 | SAP 同步来源 OcrName | C |
| createTime | datetime | 是 | 创建时间 | now | I |
| effectiveFrom | datetime | 是 | 生效起始 | now | C |
| status | int | 是 | 状态:1=启用 | 值域 {0,1} | C |
| isparam | int | 是 | 是否参数化部门:1=是 | 用于筛选"办事处" | C |

> 证据:sql-map-admin-config.xml:431-513,728-749。
> 不变量:SAP 刷新时 TRUNCATE 整表后重写(:447-451)。

### 表 fnd_basic_data(基础数据)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| dataTypeCode | varchar | 否 | 数据类型编码(外键→fnd_basic_data_type.dataTypeCode) | - | C |
| basicDataId | varchar | 否 | 基础数据业务 ID | (dataTypeCode, basicDataId) 组合不重复 | C |
| basicDataName | varchar | 是 | 基础数据名称 | - | C |
| basicDataAttri1 | varchar | 是 | 扩展属性1(可作父级 basicDataId 关联) | 父子结构 | C |
| sortId | int | 是 | 排序号 | 按升序 | C |
| createTime | datetime | 是 | 创建时间 | - | I |
| createBy | varchar | 是 | 创建人 | - | I |
| effectiveFrom | datetime | 是 | 生效起始 | - | C |
| effectiveTo | datetime | 是 | 生效终止 | 为空表示永久有效 | C |

> 证据:sql-map-admin-config.xml:774-861,857-861(组合查重)。

### 表 fnd_basic_data_type(基础数据类型)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| dataTypeCode | varchar | 否 | 类型编码(业务键) | 唯一 | C |
| dataTypeName | varchar | 是 | 类型名称 | - | C |
| status | int | 是 | 状态:1=启用 | 值域 {0,1} | C |
| effectiveFrom | datetime | 是 | 生效起始 | - | C |
| effectiveTo | datetime | 是 | 生效终止 | - | C |

> 证据:sql-map-admin-config.xml:821-829。

### 表 fnd_sys_arg(系统参数)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| code | varchar | 否 | 参数编码(业务键) | 唯一 | C |
| var | varchar | 是 | 参数值 | - | C |
| effectiveFrom | datetime | 是 | 生效起始 | <=now | C |
| effectiveTo | datetime | 是 | 生效终止 | 为空或 >now | C |

> 证据:sql-map-admin-config.xml:28-30,862-875。
> 已知 code:`sys.cache.latest.refreshTime`(缓存刷新时间)、`sys.envirment.argu`(环境参数,1=生产)、`sys.cas`(CAS 开关)。

### 表 fnd_files(文件)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| fileName | varchar | 是 | 文件名 | - | C |
| filePath | varchar | 是 | 文件路径 | - | C |
| fileType | varchar | 是 | 文件类型 | - | C |
| uploadBy | varchar | 是 | 上传人(用户名) | 外键→fnd_user_info.username | I |
| uploadTime | datetime | 是 | 上传时间 | - | I |

> 证据:sql-map-admin-config.xml:931-984。

### 表 fnd_mails(邮件队列)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| mailSubject | varchar | 是 | 主题 | - | C |
| mailContent | text | 是 | 正文 | - | C |
| mailTos | varchar | 是 | 收件人(分号分隔) | - | C |
| mailCcs | varchar | 是 | 抄送 | - | C |
| mailBcc | varchar | 是 | 密送 | - | C |
| mailAttachFiles | varchar | 是 | 附件 | - | C |
| mailServerPort | varchar | 是 | 邮件服务器端口 | - | I |
| mailServerHost | varchar | 是 | 邮件服务器主机 | - | I |
| mailUsername | varchar | 是 | 邮件账号 | - | I |
| mailPassword | varchar | 是 | 邮件密码 | - | I |
| mailFromaddress | varchar | 是 | 发件地址 | - | C |
| createBy | varchar | 是 | 创建人 | - | I |
| createTime | datetime | 是 | 创建时间 | - | I |
| effectiveFrom | datetime | 是 | 生效起始 | - | C |
| mailExpectSendTime | datetime | 是 | 期望发送时间 | < now 才被取出 | C |
| mailSendTime | datetime | 是 | 实际发送时间 | 发送时=now | I |
| sendFlag | int | 是 | 发送标志:0=未发,1=已发 | 值域 {0,1} | C |

> 证据:sql-map-admin-config.xml:685-727。

### 表 tb_sys_log(系统操作日志)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| ID | int | 否 | 主键 | 自增 | C |
| USER_NAME | varchar | 是 | 操作用户名 | 外键→user.username(老表名) | C |
| IP | varchar | 是 | 客户端 IP | - | C |
| ACTION | varchar | 是 | 操作动作 | - | C |
| RESULT | varchar | 是 | 操作结果 | - | C |
| INFO | varchar | 是 | 操作详情 | - | C |
| TIME | int | 是 | 操作时间(存为整型时间戳) | - | C |

> 证据:sql-map-admin-config.xml:541-597。
> 注:日志查询 LEFT JOIN `user` 表(老表名,与 fnd_user_info 并存) [待澄清:user 与 fnd_user_info 关系]。

### 表 fnd_company(公司/组织)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| name | varchar | 是 | 公司名称 | - | C |
| code | varchar | 是 | 公司编码 | - | C |
| status | int | 是 | 状态 | - | C |
| pid | int | 是 | 父公司 ID(自关联) | 树形结构 | C |

> 证据:sql-map-admin-config.xml:1007-1032。
> 用途:新架构多租户隔离(compId)。

### 表 dp_act_procdef_delegate(流程任务委派)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| owner | varchar | 否 | 原负责人(用户名) | 外键→user.username | C |
| assignee | varchar | 否 | 被委派人(用户名) | 外键→user.username | C |
| handleUsername | varchar | 是 | 操作人(用户名) | - | C |
| handleTime | datetime | 是 | 操作时间 | 新增/更新时=now | I |
| startTime | datetime | 是 | 委派起始 | 为空表示立即 | C |
| endTime | datetime | 是 | 委派终止 | 为空表示无限期 | C |
| procdefId | varchar | 是 | 流程定义 ID(模糊匹配) | - | C |
| status | int | 是 | 状态:1=生效 | 值域 {0,1} | C |
| cause | varchar | 是 | 委派原因 | - | C |

> 证据:sql-map-admin-config.xml:600-670。
> 不变量:生效中查询条件 = status=1 且 (起止均空 或 起空止非空且 now<=止 或 起止均非空且 起<=now<=止)。

### 表 dp_act_proc_type(流程类型)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| desc | varchar | 是 | 描述 | - | C |
| procDefKey | varchar | 是 | 流程定义键 | - | C |

> 证据:sql-map-admin-config.xml:672-681。

### 表 user(老用户表,与新 fnd_user_info 并存)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| - | - | - | 与 fnd_user_info 字段大体一致 | - | D |

> 证据:sql-map-admin-config.xml:245-247,302-305,563,589,651-660 等多处 LEFT JOIN `user`。
> **重要歧义**:代码中同时存在 `fnd_user_info` 与 `user` 两张用户表,部分 SQL(日志查询、委派查询、MD5 密码更新)仍使用老表名 `user`。[待澄清:是否为同义视图或历史遗留]

### 表 role(老角色表,与新 fnd_roles 并存)
| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | - | D |
| roleName | varchar | 否 | 角色名 | - | D |
| status | int | 是 | 状态:1=启用 | - | D |

> 证据:sql-map-admin-config.xml:243-247,261-266,535。
> **重要歧义**:查询角色名/角色 ID 仍用老表 `role` 而非 `fnd_roles`。[待澄清:两者关系]

---

## 第4章 非功能需求

### NFR-001: 用户/权限数据缓存
- **要求**: 用户与权限相关查询结果需缓存(LRU,容量 50,只读);缓存命中可避免重复查库。以下写操作必须使缓存失效:用户新增/更新、用户改密、用户菜单授权增删、角色菜单授权增删、角色增改、用户区域权限增改、手动刷新缓存。
- **证据**: sql-map-admin-config.xml:4-25(cacheModel userCache,flushInterval 1 小时,flushOnExecute 列表)

### NFR-002: 基础数据缓存
- **要求**: 基础数据查询结果需缓存(LRU,容量 50,只读,24 小时自动刷新);基础数据增改触发失效。
- **证据**: sql-map-admin-config.xml:763-771

### NFR-003: 缓存手动刷新可追溯
- **要求**: 手动触发缓存刷新时,记录最近一次刷新时间至系统参数 `sys.cache.latest.refreshTime`。
- **证据**: sql-map-admin-config.xml:28-30

### NFR-004: 权限校验强制(老架构)
- **要求**: 用户管理相关操作执行前,必须校验当前用户具备 `admin`/`engineemanager`/`engineemanager-leader` 任一角色,否则抛出"没有访问权限"异常。
- **证据**: UserManageAction.java:50-55(prepare 方法)

### NFR-005: 权限校验强制(新架构)
- **要求**: 用户详情/编辑操作:仅本人或 admin 角色可访问,否则重定向至未授权页;删除/角色变更仅 admin 可执行。
- **证据**: UserController.java:97-106,197-205,229

### NFR-006: 密码安全
- **要求**:
  1. 初始/重置密码为 8 位随机串。
  2. 密码以 MD5(password+username) 方式加密存储(老架构)或可配置加密(新架构)。
  3. 改密后 3 个月过期;重置后立即过期,强制改密。
  4. 重置密码后强制用户下线。
- **证据**: UserManageAction.java:122-123,205-209,250;sql-map-admin-config.xml:248-253

### NFR-007: 多租户隔离(新架构)
- **要求**: 用户扩展信息(UserInfo)、用户角色关联(UserRole)均按公司(compId)隔离;查询/增删均带 compId 限定;系统用户(isSysUser≠0)访问自身数据时 compId 置 -1(跨租户)。
- **证据**: UserController.java:76-77,109-116,158-161,211-214,281-290

### NFR-008: 会话单点登出
- **要求**: CAS 模式下,CAS 服务器单点登出请求必须能终止本系统对应会话;通过 ticket→sessionId 映射实现,标记会话失效后由后续请求触发登出。
- **证据**: CasLogoutFilter.java:40-67;SingleSignOutHandler.java:99-141

### NFR-009: 操作日志可审计
- **要求**: 登录及关键操作(标注系统控制器日志注解的端点,如创建/修改/删除用户)必须写入操作日志;日志可按时间排序分页查询,并补全操作人真实姓名。
- **证据**: sql-map-admin-config.xml:541-597;UserController.java:145,195,279(SystemControllerLog 注解,语义为"系统控制器日志")

### NFR-010: CAS 配置可开关
- **要求**: CAS 单点登录通过系统参数 `sys.cas`(0=关,1=开)控制;关闭时回退至账号密码登录。
- **证据**: UserController.java:127-133;LoginAction.java:38-43

### NFR-011: 邮件通知可靠性
- **要求**: 账号开通/密码重置邮件通过邮件队列表(fnd_mails)持久化,按期望发送时间(mailExpectSendTime<now)轮询发送,发送后更新 sendFlag=1 与 mailSendTime。
- **证据**: sql-map-admin-config.xml:685-727;UserManageAction.java:122-139,211-222

### NFR-012: 部门数据与主数据一致
- **要求**: 部门数据支持从 SAP 主数据视图全量同步(TRUNCATE + 重写),保证组织数据一致性。
- **证据**: sql-map-admin-config.xml:442-451

### NFR-013: 时间有效性统一约束
- **要求**: 用户、菜单、用户菜单授权、用户区域权限、角色、基础数据、基础数据类型、系统参数均带 effectiveFrom/effectiveTo 字段;查询时统一过滤 effectiveFrom<now 且(effectiveTo 为空或 >now)。
- **证据**: sql-map-admin-config.xml:40-42,81,86,109,527-528,787-788,827,864-866,879

### NFR-014: 用户名安全校验
- **要求**: 用户名、真实姓名、邮箱、菜单 ID 串、默认页均不得为空或含空格,否则拒绝提交。
- **证据**: UserManageAction.java:118-121,166-169,243-245

### NFR-015: 角色名安全校验
- **要求**: 角色名不得为空或含空格;角色菜单权限列表不得为空。
- **证据**: RoleManageAction.java:52-59,100-111

### NFR-016: 用户角色变更联动(老架构)
- **要求**: 编辑用户时若指定 changeType(service/program/both)与 newMemberCode,需联动批量更新项目的服务经理/项目经理;此联动为外部项目工具调用 [待澄清:具体联动规则与失败处理]。
- **证据**: UserManageAction.java:174-184

### NFR-017: 区域权限合并规则
- **要求**: 登录时区域权限(areapower)需按规则合并:若 areapower 为 `-1` 且 dpNo 非空,以 dpNo 替换;若 areapower 不含 dpNo,追加 dpNo;再按市场/用服办事处映射规则补充(16x↔31x 前缀互换)[待澄清:映射规则当前是否启用,代码中已注释但调用 UserUtil.processAreaPower]。
- **证据**: LoginServiceImpl.java:47-53,225-248

---

## 附录:关键歧义点(待澄清)

1. **`user` 与 `fnd_user_info` 表关系**:代码中日志查询、委派查询、MD5 密码更新仍使用老表名 `user`,而用户管理主流程使用 `fnd_user_info`。两者是同义视图、双写、还是历史遗留待迁移?需确认迁移策略。
2. **`role` 与 `fnd_roles` 表关系**:同上,查询角色名/角色 ID 使用 `role`,角色管理主流程使用 `fnd_roles`。
3. **角色默认页面硬编码 `module/Welcome1.action`**:新增/编辑角色时强制写入此固定值,注释标明"最好不要写死,后期需要调整"。是否应改为可配置?
4. **区域权限市场/用服映射规则**:`LoginServiceImpl.processAreaPower` 中 16x↔31x 前缀互换逻辑已注释,但最终调用 `UserUtil.processAreaPower`,实际规则需查看该工具类确认。
5. **用户角色变更联动**:`changeType`(service/program/both)+`newMemberCode` 触发的项目服务经理/项目经理批量更新,具体业务规则与失败处理需查看 `ProjectUtils.updateServiceAndProgramMember`。
6. **CAS 服务器地址硬编码**:`https://cas.dptech.com:8443/` 硬编码于登出逻辑,是否应改为系统参数?
7. **验证码环境开关**:`sys.envirment.argu=1` 时启用验证码且校验密码;非 1 时跳过验证码且忽略密码(测试环境免密)。生产与测试环境区分逻辑需确认。
