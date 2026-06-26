# 系统管理功能说明文档

## 1. 模块概述

系统管理模块是PMS系统的基础支撑模块，负责用户身份认证、权限控制、组织架构管理及系统运维支持。该模块采用CAS SSO与非CAS双模式认证架构，实现了Role-Menu-Power三级权限控制模型，为所有业务模块提供统一的用户上下文和访问控制能力。

### 涉及的Action类列表

| Action类 | 包路径 | 职责 |
|----------|--------|------|
| `LoginAction` | `com.dp.plat.action` | 登录/登出，支持CAS与非CAS双模式认证 |
| `UserManageAction` | `com.dp.plat.action` | 用户CRUD、角色分配、密码重置、邮件通知 |
| `RoleManageAction` | `com.dp.plat.action` | 角色CRUD、菜单权限配置 |
| `DepartmentManageAction` | `com.dp.plat.action` | 部门CRUD、部门数据刷新 |
| `BasicDataManageAction` | `com.dp.plat.action` | 基础数据类型及数据项维护、SQL执行 |
| `OperateLogAction` | `com.dp.plat.action` | 操作日志查询、导出、定时任务同步触发 |
| `PasswordGetinfo` | `com.dp.plat.action` | 密码修改/重置（已废弃） |
| `UploadAction` | `com.dp.plat.action` | 文件上传、下载、删除、富文本图片上传 |
| `ClusterAction` | `com.dp.plat.action` | 集群环境下缓存数据同步 |

### 涉及的Service类列表

| Service类 | 事务代理Bean | 依赖DAO |
|-----------|-------------|---------|
| `LoginServiceImpl` | `loginServiceAgent` | `LoginDao` |
| `UserManageServiceImpl` | `userManageServiceAgent` | `UserManageDao` |
| `RoleManageServiceImpl` | `roleManageServiceAgent` | `RoleManageDao` |
| `DepartmentManageServiceImpl` | `departmentManageServiceAgent` | `DepartmentManageDao` |
| `BasicDataServiceImpl` | `basicDataServiceAgent` | `BasicDataDao` |
| `OpLogServiceImpl` | `opLogServiceAgent` | `OpLogDao` |
| `PasswordServiceImpl` | `passwordServiceAgent` | `PasswordDao` |
| `SendMailServiceImpl` | `sendMailServiceAgent` | `SendMailDao` |

### 涉及的数据库表列表

| 表名 | 说明 |
|------|------|
| `fnd_user_info` | 用户信息表 |
| `fnd_roles` | 角色表 |
| `fnd_menus` | 菜单表 |
| `fnd_role_menus` | 角色-菜单关联表 |
| `fnd_user_menus` | 用户-菜单权限关联表 |
| `fnd_user_power` | 用户-区域权限关联表 |
| `fnd_department` | 部门表 |
| `fnd_basic_data_type` | 基础数据类型表 |
| `fnd_basic_data` | 基础数据项表 |
| `fnd_sys_arg` | 系统参数表 |
| `fnd_mails` | 邮件记录表 |
| `tb_sys_log` | 操作日志表 |

### 依赖的其他模块

- 工作流模块（CAS认证集成）
- 邮件服务模块（密码重置邮件通知）
- 集群缓存模块（缓存同步）

## 2. 业务流程

### 2.1 登录认证流程

<<<<<<< HEAD
```mermaid
flowchart TD
    A["用户访问系统"] --> B{是否CAS模式?}
    B -->|是| C["CAS Server重定向"]
    B -->|否| D["本地登录页面"]
    C --> E["CAS Assertion"]
    D --> F["提交用户名/密码<br/>LoginAction.execute()"]
    E --> G["获取Principal"]
    F --> H["MD5加密密码"]
    G --> I["查询用户信息"]
    H --> J["数据库认证校验"]
    I --> K["LoginService.loginCas()"]
    J --> L["LoginService.login()"]
    K --> M{用户有效?}
    L --> M
    M -->|是| N["构建权限映射"]
    M -->|否| O["错误页面/提示"]
    N --> P["区域权限处理"]
    P --> Q["设置UserContext"]
    Q --> R["进入首页"]
=======
```
用户访问系统
      |
 [是否CAS模式?]
 /             \
是               否
|                |
[CAS Server重定向]  [本地登录页面]
|                |
[CAS Assertion]    [提交用户名/密码] ──> LoginAction.execute()
|                |                        |
[获取Principal]   [MD5加密密码]           |
|                |                        |
[查询用户信息]    [数据库认证校验] ──> LoginService.loginCas() / login()
|                |
[用户有效?]
/        \
是         否
|          |
[构建权限映射] [错误页面/提示]
|          |
[区域权限处理] 
|
[设置UserContext]
|
[进入首页]
>>>>>>> cfb09fe3c09bfc11415a492e8001c97b140fddf0
```

### 2.2 登出流程

<<<<<<< HEAD
```mermaid
flowchart TD
    A["用户点击登出"] --> B["LoginAction.logout()"]
    B --> C{是否CAS模式?}
    C -->|是| D["重定向CAS登出URL"]
    C -->|否| E["重定向index.jsp"]
    D --> F["清除UserContext + Session"]
    E --> F
    F --> G["LoginService.logout()"]
=======
```
用户点击登出 ──> LoginAction.logout()
      |
 [是否CAS模式?]
 /             \
是               否
|                |
[重定向CAS登出URL]  [重定向index.jsp]
|                |
[清除UserContext + Session] ──> LoginService.logout()
>>>>>>> cfb09fe3c09bfc11415a492e8001c97b140fddf0
```

### 2.3 用户管理流程

<<<<<<< HEAD
```mermaid
flowchart TD
    A["用户列表"] --> B["UserManageAction.execute()"]
    B --> C["UserManageService.queryUserList()"]
    C --> D["新增用户"]
    C --> E["编辑用户"]
    C --> F["重置密码"]
    D --> G["UserManageAction.add()"]
    G --> H["校验必填字段"]
    H --> I["生成随机密码 + MD5加密"]
    I --> J["保存用户+菜单关联<br/>UserManageService.addUserInfo()"]
    J --> K["发送账号激活邮件"]
    E --> L["UserManageAction.edit()"]
    L --> M["校验必填字段"]
    M --> N["更新用户+菜单+区域权限<br/>UserManageService.updateUserInfo()"]
    F --> O["UserManageAction.pwdreset()"]
    O --> P["生成新密码 + MD5加密"]
    P --> Q["更新密码+强制下线<br/>PasswordService.forcedOffline()"]
    Q --> R["发送密码重置邮件"]
=======
```
[用户列表] ──> UserManageAction.execute() ──> UserManageService.queryUserList()
     |
  [新增用户] ──> UserManageAction.add()
     |              |
     |         [校验必填字段]
     |              |
     |         [生成随机密码 + MD5加密]
     |              |
     |         [保存用户+菜单关联] ──> UserManageService.addUserInfo()
     |              |
     |         [发送账号激活邮件]
     |
  [编辑用户] ──> UserManageAction.edit()
     |              |
     |         [校验必填字段]
     |              |
     |         [更新用户+菜单+区域权限] ──> UserManageService.updateUserInfo()
     |
  [重置密码] ──> UserManageAction.pwdreset()
                    |
               [生成新密码 + MD5加密]
                    |
               [更新密码+强制下线] ──> PasswordService.forcedOffline()
                    |
               [发送密码重置邮件]
>>>>>>> cfb09fe3c09bfc11415a492e8001c97b140fddf0
```

### 2.4 角色权限配置流程

<<<<<<< HEAD
```mermaid
flowchart TD
    A["角色列表"] --> B["RoleManageAction.execute()"]
    B --> C["RoleManageService.queryRoleList()"]
    C --> D["新增角色"]
    C --> E["编辑角色"]
    D --> F["RoleManageAction.addSubmit()"]
    F --> G["校验角色名+菜单权限"]
    G --> H["设置默认页面Welcome1.action"]
    H --> I["保存角色+菜单关联<br/>RoleManageService.addRoleSubmit()"]
    E --> J["RoleManageAction.editSubmit()"]
    J --> K["校验后更新<br/>RoleManageService.updateRoleSubmit()"]
=======
```
[角色列表] ──> RoleManageAction.execute() ──> RoleManageService.queryRoleList()
     |
  [新增角色] ──> RoleManageAction.addSubmit()
     |              |
     |         [校验角色名+菜单权限]
     |              |
     |         [设置默认页面Welcome1.action]
     |              |
     |         [保存角色+菜单关联] ──> RoleManageService.addRoleSubmit()
     |
  [编辑角色] ──> RoleManageAction.editSubmit()
                    |
               [校验后更新] ──> RoleManageService.updateRoleSubmit()
>>>>>>> cfb09fe3c09bfc11415a492e8001c97b140fddf0
```

### 2.5 Role-Menu-Power三级权限模型

<<<<<<< HEAD
```mermaid
erDiagram
    fnd_user_info {
        string username "用户名"
        string realName "真实姓名"
        string password "密码"
        string roleIds "角色ID列表 格式: ;1;,;2;"
        string dpNo "部门编号"
    }
    fnd_roles {
        int id "角色ID"
        string roleName "角色名称"
        string defaultPage "默认页面"
        int status "状态"
    }
    fnd_menus {
        int id "菜单ID"
        string menuCode "菜单编码"
        string menuName "菜单名称"
        int superId "上级菜单ID"
        string path "路径"
    }
    fnd_user_menus {
        int fnd_user_id "用户ID"
        string menuCode "菜单编码"
        string menuValue "菜单权限值"
    }
    fnd_role_menus {
        int roleId "角色ID"
        int menuId "菜单ID"
        int menuPower "菜单权限"
    }
    fnd_user_power {
        int fndUserId "用户ID"
        string areapower "区域权限"
    }

    fnd_user_info ||--o{ fnd_user_menus : "N:M 用户-菜单关联"
    fnd_roles ||--o{ fnd_role_menus : "N:M 角色-菜单关联"
    fnd_role_menus }o--|| fnd_menus : "关联菜单"
    fnd_user_info ||--o| fnd_user_power : "用户-区域权限"
=======
```
┌─────────────────────────────────────────────────┐
│              用户 (fnd_user_info)                │
│  username │ realName │ password │ roleIds │ dpNo│
└──────────────────────┬──────────────────────────┘
                       │ roleIds格式: ;1;,;2;
           ┌───────────┼───────────┐
           │  用户-菜单关联表        │
           │  (fnd_user_menus)     │
           │  fnd_user_id │ menuCode │ menuValue │
           └───────────┬───────────┘
                       │ N:M
┌──────────────────────▼──────────────────────────┐
│              角色 (fnd_roles)                     │
│  id │ roleName │ defaultPage │ status            │
└──────────────────────┬──────────────────────────┘
                       │
           ┌───────────┼───────────┐
           │  角色-菜单关联表        │
           │  (fnd_role_menus)     │
           │  roleId │ menuId │ menuPower │
           └───────────┬───────────┘
                       │ N:M
┌──────────────────────▼──────────────────────────┐
│              菜单 (fnd_menus)                     │
│  id │ menuCode │ menuName │ superId │ path       │
└──────────────────────┬──────────────────────────┘
                       │
           ┌───────────┼───────────┐
           │  用户-区域权限表        │
           │  (fnd_user_power)     │
           │  fndUserId │ areapower │
           └───────────────────────┘
>>>>>>> cfb09fe3c09bfc11415a492e8001c97b140fddf0
```

## 3. 接口文档

### 3.1 登录入口

| 项目 | 说明 |
|------|------|
| URL | /Login.action |
| HTTP方法 | POST |
| 功能描述 | 用户登录认证，自动选择CAS或非CAS模式 |
| 权限要求 | 无（公开接口） |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| user.username | String | 是（非CAS模式） | 非空 | 无 | 用户名 |
| user.password | String | 是（非CAS模式） | 非空 | 无 | 密码 |

**返回结果**：

| result名 | 类型 | 跳转页面/JSON结构 | 说明 |
|----------|------|-------------------|------|
| SUCCESS | String | 首页 | 登录成功，重定向到默认页面 |
| INPUT | String | 登录页 | 未登录或登录失败 |
| errorCas | String | CAS错误页 | CAS认证失败 |

**处理逻辑**：
1. 判断是否CAS模式 → 调用`UserContext.isCas()`
2. CAS模式 → 调用`LoginService.loginCas(user, ip)`
3. 非CAS模式 → 调用`LoginService.login(user, ip)`
4. 登录成功 → 设置重定向URL，返回SUCCESS
5. 登录失败 → 调用`setErrmsg(loginService)`，返回INPUT

**异常处理**：

| 异常场景 | 处理方式 |
|----------|----------|
| CAS断言为空 | 调用`loginService.logout()`，返回errorCas |
| 用户名/密码错误 | 设置错误消息，返回INPUT |
| 用户被禁用 | 设置错误消息，返回INPUT |

### 3.2 登出

| 项目 | 说明 |
|------|------|
| URL | /Logout.action |
| HTTP方法 | GET/POST |
| 功能描述 | 用户登出，清除会话 |
| 权限要求 | 已登录用户 |

**输入参数**：无

**返回结果**：

| result名 | 类型 | 跳转页面/JSON结构 | 说明 |
|----------|------|-------------------|------|
| SUCCESS | String | CAS登出URL或index.jsp | 登出成功 |

**处理逻辑**：
1. 判断CAS模式
2. CAS模式 → 重定向到`https://cas.dptech.com:8443/logout?service=...`
3. 非CAS模式 → 重定向到`index.jsp`
4. 调用`loginService.logout()`清除会话

### 3.3 404错误页面

| 项目 | 说明 |
|------|------|
| URL | /404.action |
| HTTP方法 | GET |
| 功能描述 | 404错误页面跳转 |
| 权限要求 | 无 |

**返回结果**：SUCCESS → 404错误页面

### 3.4 用户列表查询

| 项目 | 说明 |
|------|------|
| URL | /module/UserManage.action |
| HTTP方法 | GET |
| 功能描述 | 分页查询用户列表，含角色名称和部门信息 |
| 权限要求 | 管理员/工程管理部/工程管理部领导 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| displayParam | DisplayParam | 否 | - | 默认分页 | 分页参数 |
| user.username | String | 否 | - | 无 | 用户名/姓名模糊搜索 |
| user.roleids | String | 否 | - | 无 | 角色ID过滤 |
| user.dpNo | String | 否 | - | 无 | 部门编号过滤 |

**返回结果**：

| result名 | 类型 | 跳转页面/JSON结构 | 说明 |
|----------|------|-------------------|------|
| SUCCESS | String | 用户列表页面 | 查询成功 |

**处理逻辑**：
1. `prepare()`权限校验 → 仅管理员/工程管理部/工程管理部领导可访问
2. 查询角色列表 → `userManageService.queryRolelist()`
3. 查询部门列表 → `departmentManageService.queryDepartments()`
4. 查询用户列表 → `userManageService.queryUserList()`
5. 处理角色名称映射

### 3.5 新增用户

| 项目 | 说明 |
|------|------|
| URL | /module/UserManage!add.action |
| HTTP方法 | GET（表单页）/ POST（提交） |
| 功能描述 | 新增用户 |
| 权限要求 | 管理员/工程管理部/工程管理部领导 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| user.username | String | 是 | 非空、不含空格、唯一 | 无 | 用户名 |
| user.realName | String | 是 | 非空、不含空格 | 无 | 真实姓名 |
| user.email | String | 是 | 非空、不含空格 | 无 | 邮箱 |
| usermenuids | String | 是 | 非空 | 无 | 菜单权限ID串 |
| user.defaultPage | String | 是 | 非空 | 无 | 默认登录页面 |
| user.dpNo | String | 否 | - | 无 | 部门编号 |
| user.roleids | String | 否 | - | 无 | 角色ID |

**返回结果**：

| result名 | 类型 | 跳转页面/JSON结构 | 说明 |
|----------|------|-------------------|------|
| INPUT | String | 新增用户表单页 | 进入新增页面 |
| SUCCESS | String | 用户列表页 | 新增成功 |
| ERROR | String | 新增用户表单页 | 验证失败 |

**处理逻辑**：
1. 无参数时返回INPUT展示新增表单
2. 校验必填字段（用户名、真实姓名、邮箱、菜单ID、默认页面）
3. 调用`PasswordUtil.generatePass()`生成8位随机密码
4. 调用`PasswordUtil.encryptMD5Password(password, username)`进行MD5加密
5. 调用`userManageService.addUserInfo()`保存用户和菜单关联
6. 非CAS模式下发送邮件通知

**异常处理**：

| 异常场景 | 处理方式 |
|----------|----------|
| 必填字段为空 | 返回ERROR |
| 用户名含空格 | 返回ERROR |

### 3.6 检查用户名唯一性

| 项目 | 说明 |
|------|------|
| URL | /module/UserManage!checkUsername.action |
| HTTP方法 | POST |
| 功能描述 | Ajax检查用户名是否已存在 |
| 权限要求 | 管理员/工程管理部/工程管理部领导 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| username | String | 是 | 非空 | 无 | 待检查的用户名 |

**返回结果**：

| result名 | 类型 | 跳转页面/JSON结构 | 说明 |
|----------|------|-------------------|------|
| SUCCESS | String | JSON（result字段） | result>0表示已存在 |

### 3.7 编辑用户

| 项目 | 说明 |
|------|------|
| URL | /module/UserManage!edit.action |
| HTTP方法 | GET（表单页）/ POST（提交） |
| 功能描述 | 编辑用户信息，含角色变更和批量变更项目成员 |
| 权限要求 | 管理员/工程管理部/工程管理部领导 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| user | User | 是 | 非空 | 无 | 用户信息 |
| usermenuids | String | 是 | 非空 | 无 | 菜单权限ID串 |
| changeType | String | 否 | - | 无 | 角色变更类型(service/program/both) |
| newMemberCode | String | 否 | - | 无 | 新成员编码 |

**返回结果**：

| result名 | 类型 | 跳转页面/JSON结构 | 说明 |
|----------|------|-------------------|------|
| INPUT | String | 编辑用户表单页 | 进入编辑页面 |
| SUCCESS | String | 用户列表页 | 编辑成功 |
| ERROR | String | 编辑用户表单页 | 验证失败 |

**处理逻辑**：
1. 无用户名时查询原有数据返回编辑表单
2. 校验必填字段
3. 若`changeType`非空，批量变更项目服务经理/项目经理
4. 调用`userManageService.updateUserInfo()`更新

### 3.8 重置密码

| 项目 | 说明 |
|------|------|
| URL | /module/UserManage!pwdreset.action |
| HTTP方法 | POST |
| 功能描述 | 重置用户密码并发送邮件通知 |
| 权限要求 | 管理员/工程管理部/工程管理部领导 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| user.id | int | 是 | 非空 | 无 | 用户ID |

**返回结果**：

| result名 | 类型 | 跳转页面/JSON结构 | 说明 |
|----------|------|-------------------|------|
| SUCCESS | String | JSON（result字段） | 重置成功 |

**处理逻辑**：
1. 生成随机密码并MD5加密
2. 更新用户密码和过期时间
3. 发送邮件通知
4. 调用`passwordService.forcedOffline()`强制用户下线

### 3.9 查询用户信息

| 项目 | 说明 |
|------|------|
| URL | /module/UserManage!findUser.action |
| HTTP方法 | POST |
| 功能描述 | 根据用户名查询用户信息 |
| 权限要求 | 管理员/工程管理部/工程管理部领导 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| username | String | 是 | 非空 | 无 | 用户名 |

**返回结果**：SUCCESS → JSON（用户信息）

### 3.10 角色列表查询

| 项目 | 说明 |
|------|------|
| URL | /module/RoleManage.action |
| HTTP方法 | GET |
| 功能描述 | 分页查询角色列表 |
| 权限要求 | 管理员 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| displayParam | DisplayParam | 否 | - | 默认分页 | 分页参数 |
| role | Role | 否 | - | 无 | 角色查询条件 |

**返回结果**：SUCCESS → 角色列表页面

### 3.11 新增角色表单

| 项目 | 说明 |
|------|------|
| URL | /module/RoleManage!add.action |
| HTTP方法 | GET |
| 功能描述 | 查询系统菜单列表，返回新增角色表单 |
| 权限要求 | 管理员 |

**返回结果**：INPUT → 新增角色表单页

### 3.12 新增角色提交

| 项目 | 说明 |
|------|------|
| URL | /module/RoleManage!addSubmit.action |
| HTTP方法 | POST |
| 功能描述 | 提交新角色及菜单权限 |
| 权限要求 | 管理员 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| role.roleName | String | 是 | 非空 | 无 | 角色名称 |
| rolemenuidList | List | 是 | 非空 | 无 | 角色菜单权限列表 |
| role.status | int | 否 | - | 1 | 状态（0=失效） |
| role.effectiveTo | Date | 否 | 状态为0时必填 | 无 | 失效时间 |

**返回结果**：

| result名 | 类型 | 跳转页面/JSON结构 | 说明 |
|----------|------|-------------------|------|
| SUCCESS | String | 角色列表页 | 新增成功 |
| ERROR | String | 新增角色表单页 | 验证失败 |

**处理逻辑**：
1. 验证角色菜单权限和角色名称
2. 设置默认页面为`module/Welcome1.action`
3. 状态为0时设置失效时间
4. 调用`roleManageService.addRoleSubmit()`保存

### 3.13 编辑角色

| 项目 | 说明 |
|------|------|
| URL | /module/RoleManage!edit.action |
| HTTP方法 | GET |
| 功能描述 | 查询角色信息和菜单权限，返回编辑表单 |
| 权限要求 | 管理员 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| role.id | int | 是 | 非空 | 无 | 角色ID |

**返回结果**：

| result名 | 类型 | 跳转页面/JSON结构 | 说明 |
|----------|------|-------------------|------|
| INPUT | String | 编辑角色表单页 | 查询成功 |
| ERROR | String | 错误页 | 角色不存在 |

### 3.14 编辑角色提交

| 项目 | 说明 |
|------|------|
| URL | /module/RoleManage!editSubmit.action |
| HTTP方法 | POST |
| 功能描述 | 更新角色及菜单权限 |
| 权限要求 | 管理员 |

**输入参数**：同3.12新增角色提交

**返回结果**：SUCCESS → 角色列表页 / ERROR → 编辑角色表单页

### 3.15 部门列表查询

| 项目 | 说明 |
|------|------|
| URL | /module/DepartmentManage.action |
| HTTP方法 | GET |
| 功能描述 | 分页查询部门列表 |
| 权限要求 | 管理员 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| displayParam | DisplayParam | 否 | - | 默认分页 | 分页参数 |
| department | Department | 否 | - | 无 | 部门查询条件 |

**返回结果**：SUCCESS → 部门列表页

### 3.16 刷新部门数据

| 项目 | 说明 |
|------|------|
| URL | /module/DepartmentManage!refresh.action |
| HTTP方法 | POST |
| 功能描述 | 从SAP刷新部门数据缓存 |
| 权限要求 | 管理员 |

**返回结果**：SUCCESS → 部门列表页

### 3.17 新增部门

| 项目 | 说明 |
|------|------|
| URL | /module/DepartmentManage!add.action |
| HTTP方法 | GET（表单页）/ POST（提交） |
| 功能描述 | 新增部门 |
| 权限要求 | 管理员 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| department.departmentNum | String | 是 | 非空 | 无 | 部门编号 |
| department.departmentName | String | 是 | 非空 | 无 | 部门名称 |

**返回结果**：

| result名 | 类型 | 跳转页面/JSON结构 | 说明 |
|----------|------|-------------------|------|
| INPUT | String | 新增部门表单页 | 进入新增页面 |
| SUCCESS | String | 部门列表页 | 新增成功 |
| ERROR | String | 新增部门表单页 | 新增失败（ID<=0） |

### 3.18 基础数据查询

| 项目 | 说明 |
|------|------|
| URL | /module/BasicDataManage.action |
| HTTP方法 | GET |
| 功能描述 | 查询基础数据类型列表和对应数据列表 |
| 权限要求 | 管理员 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| basicData.dataTypeCode | String | 否 | - | 无 | 数据类型编码 |

**返回结果**：SUCCESS → 基础数据管理页面

### 3.19 基础数据更新

| 项目 | 说明 |
|------|------|
| URL | /module/BasicDataManage!basicdataUpdate.action |
| HTTP方法 | POST |
| 功能描述 | 更新基础数据 |
| 权限要求 | 管理员 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| basicData.id | int | 是 | 非空 | 无 | 基础数据ID |
| basicData.basicDataName | String | 否 | - | 无 | 数据名称 |
| basicData.effectiveTo | Date | 否 | - | 无 | 失效时间 |

**返回结果**：

| result名 | 类型 | 跳转页面/JSON结构 | 说明 |
|----------|------|-------------------|------|
| INPUT | String | 编辑页面 | 查询原数据 |
| SUCCESS | String | 基础数据列表页 | 更新成功 |

### 3.20 基础数据新增

| 项目 | 说明 |
|------|------|
| URL | /module/BasicDataManage!basicdataInsert.action |
| HTTP方法 | POST |
| 功能描述 | 新增基础数据 |
| 权限要求 | 管理员 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| basicData.dataTypeCode | String | 是 | 非空 | 无 | 数据类型编码 |
| basicData.basicDataId | String | 是 | 非空、唯一 | 无 | 数据编码 |
| basicData.basicDataName | String | 是 | 非空 | 无 | 数据名称 |

**返回结果**：

| result名 | 类型 | 跳转页面/JSON结构 | 说明 |
|----------|------|-------------------|------|
| INPUT | String | 新增页面 | 进入新增 |
| SUCCESS | String | 基础数据列表页 | 新增成功 |

### 3.21 检查基础数据编码唯一性

| 项目 | 说明 |
|------|------|
| URL | /module/BasicDataManage!findBasicDataId.action |
| HTTP方法 | POST |
| 功能描述 | Ajax检查编码是否已存在 |
| 权限要求 | 管理员 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| dataTypeCode | String | 是 | 非空 | 无 | 数据类型编码 |
| basicDataId | String | 是 | 非空 | 无 | 数据编码 |

**返回结果**：SUCCESS → JSON

### 3.22 执行自定义SQL

| 项目 | 说明 |
|------|------|
| URL | /module/BasicDataManage!executeSql.action |
| HTTP方法 | POST |
| 功能描述 | 执行自定义SQL（仅限含WHERE的UPDATE/DELETE或INSERT） |
| 权限要求 | 管理员 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| executeSql | String | 是 | 必须含WHERE或INSERT | 无 | SQL语句 |

**返回结果**：SUCCESS → JSON（msg字段）

**异常处理**：

| 异常场景 | 处理方式 |
|----------|----------|
| SQL不含WHERE且不含INSERT | 不允许执行 |
| SQL执行异常 | 设置msg为错误信息 |

### 3.23 操作日志查询

| 项目 | 说明 |
|------|------|
| URL | /module/OperateLog.action |
| HTTP方法 | GET |
| 功能描述 | 分页查询操作日志列表 |
| 权限要求 | 管理员 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| displayParam | DisplayParam | 否 | - | 默认分页 | 分页参数 |

**返回结果**：SUCCESS → 操作日志列表页

### 3.24 操作日志导出

| 项目 | 说明 |
|------|------|
| URL | /module/OperateLog!exportlog.action |
| HTTP方法 | GET |
| 功能描述 | 导出操作日志为Excel |
| 权限要求 | 管理员 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| displayParam | DisplayParam | 否 | - | 无 | 查询条件 |

**返回结果**：

| result名 | 类型 | 跳转页面/JSON结构 | 说明 |
|----------|------|-------------------|------|
| SUCCESS | String | Excel文件下载流 | 导出成功 |
| ERROR | String | 错误页 | 导出失败 |

**异常处理**：

| 异常场景 | 处理方式 |
|----------|----------|
| FileNotFoundException | 设置错误消息，返回ERROR |
| IOException | 设置错误消息，返回ERROR |

### 3.25 定时任务同步触发

| 项目 | 说明 |
|------|------|
| URL | /module/OperateLog!syncTask.action |
| HTTP方法 | POST |
| 功能描述 | 手动触发定时任务同步执行 |
| 权限要求 | 管理员 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| taskName | String | 是 | 非空 | 无 | 定时任务Bean名称 |

**返回结果**：SUCCESS → JSON（resultMap）

### 3.26 文件上传

| 项目 | 说明 |
|------|------|
| URL | /module/Upload!upload.action |
| HTTP方法 | POST（multipart/form-data） |
| 功能描述 | 上传文件 |
| 权限要求 | 已登录用户 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| upload | File[] | 是 | 非空 | 无 | 上传文件数组 |
| uploadFileName | String[] | 是 | - | 无 | 文件名数组 |
| uploadFileType | String[] | 否 | - | 无 | 文件类型数组 |

**返回结果**：

| result名 | 类型 | 跳转页面/JSON结构 | 说明 |
|----------|------|-------------------|------|
| INPUT | String | - | 无文件时 |
| SUCCESS | String | JSON（fileIds） | 上传成功 |

### 3.27 文件删除

| 项目 | 说明 |
|------|------|
| URL | /module/Upload!deleteFile.action |
| HTTP方法 | POST |
| 功能描述 | 删除文件记录 |
| 权限要求 | 已登录用户 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| fileId | int | 是 | 非空 | 无 | 文件ID |

**返回结果**：SUCCESS → JSON

### 3.28 文件下载

| 项目 | 说明 |
|------|------|
| URL | /module/Upload!downloadFile.action |
| HTTP方法 | GET |
| 功能描述 | 下载文件 |
| 权限要求 | 已登录用户 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| fileId | int | 是 | 非空 | 无 | 文件ID |

**返回结果**：SUCCESS → 文件流 / ERROR → 错误页

### 3.29 文件查询

| 项目 | 说明 |
|------|------|
| URL | /module/Upload!queryFile.action |
| HTTP方法 | POST |
| 功能描述 | 根据文件ID列表查询文件信息 |
| 权限要求 | 已登录用户 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| fileIds | String | 是 | 非空 | 无 | 文件ID列表（逗号分隔） |

**返回结果**：SUCCESS → JSON（文件信息列表）/ ERROR → 错误页

### 3.30 富文本图片上传

| 项目 | 说明 |
|------|------|
| URL | /module/Upload!uploadImage.action |
| HTTP方法 | POST（multipart/form-data） |
| 功能描述 | 上传富文本编辑器图片（MD5去重） |
| 权限要求 | 已登录用户 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| upload | File[] | 是 | 非空 | 无 | 图片文件 |
| uploadFileName | String[] | 是 | - | 无 | 文件名 |

**返回结果**：SUCCESS → JSON（完整URL路径）

### 3.31 集群缓存刷新

| 项目 | 说明 |
|------|------|
| URL | ⚠️ /module/Cluster!refreshCacheData.action（功能不可用，struts.xml和applicationContext-action.xml中均未配置） |
| HTTP方法 | POST |
| 功能描述 | 集群环境下刷新缓存数据 |
| 权限要求 | 管理员或有效签名 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| signature | String | 否 | AES加密验证 | 无 | 集群通知签名 |

**返回结果**：SUCCESS → JSON

**处理逻辑**：
1. 验证签名（AES解密），管理员可直接访问
2. 调用`basicDataService.refreshCacheData()`刷新缓存

## 4. Service层详解

### 4.1 LoginServiceImpl.login(LoginParam, String)

- **功能描述**：用户登录认证，构建用户上下文和权限映射
- **事务类型**：无事务（query*前缀不匹配事务规则）
- **核心算法**：
  1. 查询系统环境参数`sys.envirment.argu`，判断是否为生产环境（"1"为生产环境需验证码）
  2. 生产环境下验证码校验：从Session获取`rand`属性与用户输入比对
  3. 查询用户信息`loginDao.querUser(username)`
  4. 测试环境（非"1"）忽略密码校验，直接使用数据库密码
  5. 密码比对：`user.getPassword().equalsIgnoreCase(pwd)`（忽略大小写）
  6. **权限映射构建算法**（O(n*m)，n=角色数，m=菜单权限数）：
     - 遍历用户角色ID列表（逗号分隔，格式如`;1;,;2;`）
     - 对每个角色查询`RoleMenuPower`列表
     - 解析菜单权限编码：8=增加, 1=删除, 4=查找, 2=更新
     - 构建`Map<Integer, Map<String, Integer>>`权限映射
  7. **区域权限处理**：调用`processAreaPower()`补充市场和用服相同办事处权限
  8. 查询用户默认页面
  9. 调用`getUserContext().login()`构建用户上下文
  10. 记录登录日志
- **调用的DAO方法**：`loginDao.querySysArg()`, `loginDao.querUser()`, `loginDao.queryUserMenuMap()`, `loginDao.queryRoleMenuPowerList()`, `loginDao.queryUserDefaultPage()`
- **异常处理**：Exception → e.printStackTrace() + addErrmsg("Login Error!") + return false

### 4.2 LoginServiceImpl.loginCas(LoginParam, String)

- **功能描述**：CAS单点登录认证（无需密码校验）
- **事务类型**：无事务
- **核心算法**：与login方法类似，但无验证码校验、无密码校验，用户不存在直接返回false

### 4.3 LoginServiceImpl.logout()

- **功能描述**：用户登出，销毁Session
- **事务类型**：无事务
- **核心算法**：调用`HttpContext.invalidateSession()`销毁当前会话

### 4.4 UserManageServiceImpl.addUserInfo(User, String)

- **功能描述**：新增用户信息
- **事务类型**：REQUIRED（add*前缀）
- **核心算法**：
  1. 记录操作日志
  2. 处理角色ID格式：调用`dealWith()`将`1,2`转为`;1;,;2;`
  3. 调用DAO插入用户信息和菜单关联
- **调用的DAO方法**：`userManageDao.addUserInfo()`

### 4.5 UserManageServiceImpl.updateUserInfo(User, String)

- **功能描述**：更新用户信息（含菜单权限和区域权限更新）
- **事务类型**：REQUIRED（update*前缀）
- **核心算法**：
  1. 校验默认登录页面路径是否有效，无效抛出RuntimeException
  2. 处理角色ID格式
  3. 更新用户基本信息
  4. 删除原有用户菜单关联，重新插入新的菜单关联
  5. 更新区域权限：合并用户区域权限和部门编号，调用`UserUtil.processAreaPower()`处理
  6. 根据是否已有权限记录选择update或insert
- **调用的DAO方法**：`userManageDao.queryUserMenu()`, `userManageDao.updateUser()`, `userManageDao.deleteUsermenu()`, `userManageDao.insertUsermenu()`, `userManageDao.updateUserPower()`, `userManageDao.insertUserpower()`
- **异常处理**：RuntimeException → 默认登录页面路径为空时直接抛出

### 4.6 UserManageServiceImpl.updateServiceAndProgramMember(ProjectBatchCgMbParam)

- **功能描述**：批量更新服务和项目经理成员
- **事务类型**：REQUIRED（update*前缀）

### 4.7 RoleManageServiceImpl.addRoleSubmit(Role, List<RoleMenuPower>)

- **功能描述**：新增角色及其菜单权限
- **事务类型**：REQUIRED（add*前缀）
- **调用的DAO方法**：`roleManageDao.addRoleSubmit()`

### 4.8 RoleManageServiceImpl.updateRoleSubmit(Role, List<RoleMenuPower>)

- **功能描述**：更新角色及其菜单权限
- **事务类型**：REQUIRED（update*前缀）
- **调用的DAO方法**：`roleManageDao.updateRoleSubmit()`

### 4.9 DepartmentManageServiceImpl.addDepartmentSubmit(Department)

- **功能描述**：新增部门
- **事务类型**：REQUIRED（add*前缀）

### 4.10 DepartmentManageServiceImpl.refreshDepartment()

- **功能描述**：刷新部门数据缓存（从SAP同步）
- **事务类型**：无事务（refresh*不匹配事务前缀）
- **核心算法**：从SAP查询部门数据 → 清空部门表(TRUNCATE) → 批量插入新数据

### 4.11 BasicDataServiceImpl.updateBasicData(BasicDataBean)

- **功能描述**：更新基础数据
- **事务类型**：REQUIRED（update*前缀）

### 4.12 BasicDataServiceImpl.insertBasicDataBean(BasicDataBean)

- **功能描述**：新增基础数据
- **事务类型**：REQUIRED（insert*前缀）
- **核心算法**：自动设置创建人和创建时间

### 4.13 BasicDataServiceImpl.insertFileInfo(String, String)

- **功能描述**：插入文件信息，返回文件ID列表
- **事务类型**：REQUIRED（insert*前缀）
- **核心算法**：
  1. 逗号分隔文件名列表
  2. 遍历每个文件名，构建参数Map（fileName, filePath, uploadBy, uploadTime）
  3. 调用DAO插入，收集生成的ID
  4. 拼接ID列表返回（逗号分隔）
- **返回值**：String - 文件ID列表，如"1,2,3"

### 4.14 BasicDataServiceImpl.executeSql(String)

- **功能描述**：执行自定义SQL
- **事务类型**：无事务（execute*不匹配事务前缀）
- **注意事项**：⚠️ 存在SQL注入风险

### 4.15 PasswordServiceImpl.changelogin(PasswordEditParam)

- **功能描述**：修改密码并自动重新登录
- **事务类型**：无事务（change*不匹配事务前缀）
- **核心算法**：
  1. 获取当前用户上下文和用户信息
  2. 校验旧密码
  3. 调用DAO更新密码
  4. 更新UserContext中的密码
  5. **自动重新登录流程**：登出 → 生成验证码 → 使用新密码重新登录
  6. 强制其他会话下线`forcedOffline()`

### 4.16 PasswordServiceImpl.forcedOffline(String)

- **功能描述**：踢指定用户的其他在线会话下线
- **事务类型**：无事务
- **核心算法**：
  1. 获取全局在线用户列表`UserContext.getOnlineList()`
  2. 遍历所有在线会话，匹配指定用户名
  3. 跳过当前Session（保留当前会话）
  4. 对其他匹配会话调用`activeSession.logout()`强制下线
- **性能优化策略**：使用ArrayList副本遍历，避免并发修改异常

### 4.17 OpLogServiceImpl.insertLog()

- **功能描述**：插入操作日志
- **事务类型**：REQUIRED（insert*前缀）

### 4.18 SendMailServiceImpl.keepMailInfo(MailSenderInfo)

- **功能描述**：保存邮件信息到数据库（异步发送）
- **事务类型**：REQUIRED（keep*前缀）
- **核心算法**：若期望发送时间为空，设置为当前时间，调用DAO保存邮件信息
- **注意事项**：邮件实际发送由定时任务异步处理

## 5. 数据操作

### 5.1 本模块涉及的数据库表及CRUD操作

| 表名 | CREATE | READ | UPDATE | DELETE |
|------|--------|------|--------|--------|
| fnd_user_info | ✓ insertUserObject | ✓ queryUserList/queryUserById/queryUserByName | ✓ updateUserObject/updatePwdByUsername | - |
| fnd_roles | ✓ insertRoleObject | ✓ queryRoleList | ✓ updateRoleObject | - |
| fnd_menus | - | ✓ queryMenuModules | - | - |
| fnd_role_menus | ✓ insertRoleMenuPower | ✓ queryRoleMenuList | - | ✓ deleteRoleMenuPower |
| fnd_user_menus | ✓ insertMenuForUser | ✓ queryPermissionsByName | - | ✓ deleteMenuForUser |
| fnd_user_power | ✓ insertUserPower | - | ✓ updateUserPower | - |
| fnd_department | ✓ insertDepartmentObject | ✓ queryDepartmentList/queryDepartmentMap | - | ✓ truncateDepartment |
| fnd_basic_data | ✓ insertBasicData | ✓ queryBasicData/queryBasicDataAll | ✓ updateBasicData | - |
| fnd_basic_data_type | - | ✓ queryBasicDataType | - | - |
| fnd_sys_arg | - | ✓ querySysArg | - | - |
| fnd_mails | ✓ insertIntoSysMails | ✓ querySysMails | ✓ updateSysMailsState | - |
| tb_sys_log | ✓ insertLog | ✓ queryLogList/queryLogAllList | - | ✓ delete |

### 5.2 数据校验规则

| 数据对象 | 校验字段 | 校验规则 | 错误提示 |
|----------|----------|----------|----------|
| User | username | 非空、不含空格、唯一 | 用户名不能为空/已存在 |
| User | realName | 非空、不含空格 | 真实姓名不能为空 |
| User | email | 非空、不含空格 | 邮箱不能为空 |
| User | defaultPage | 非空 | 默认页面不能为空 |
| User | usermenuids | 非空 | 菜单权限不能为空 |
| Role | roleName | 非空 | 角色名称不能为空 |
| Role | rolemenuidList | 非空 | 必须选择菜单权限 |
| Department | departmentNum | 非空 | 部门编号不能为空 |
| Department | departmentName | 非空 | 部门名称不能为空 |
| BasicData | dataTypeCode | 非空 | 数据类型不能为空 |
| BasicData | basicDataId | 非空、唯一 | 数据编码不能为空/已存在 |
| Login | 验证码 | 生产环境下与Session中rand属性匹配 | 验证码错误 |
| Login | password | 与数据库MD5密码匹配（忽略大小写） | 密码错误 |

### 5.3 数据生命周期

| 数据对象 | 创建 | 修改 | 归档 | 删除 |
|----------|------|------|------|------|
| User | 新增用户时创建，status=1 | 编辑用户信息，updateTime更新 | 设置effectiveTo失效时间 | 逻辑删除（设置status=0） |
| Role | 新增角色时创建，status=1 | 编辑角色信息 | 设置effectiveTo失效时间 | 逻辑删除（设置status=0） |
| Department | 新增部门时创建 | - | - | TRUNCATE物理删除（刷新时） |
| BasicData | 新增时创建，effectiveFrom=now | 更新名称/排序 | 设置effectiveTo失效时间 | - |
| UserMenu | 新增用户时创建 | 更新用户时先删后插 | - | 更新用户时先删后插 |
| UserPower | 新增用户时创建 | 更新用户时update或insert | - | - |

### 5.4 数据转换规则

| 转换场景 | 源格式 | 目标格式 | 说明 |
|----------|--------|----------|------|
| 角色ID格式 | `1,2,3` | `;1;,;2;,;3;` | `dealWith()`方法转换，用于LIKE匹配 |
| 密码加密 | 明文 | MD5(username+password) | 32位十六进制摘要 |
| 区域权限 | 逗号分隔办事处编码 | 补充市场和用服相同办事处权限 | `UserUtil.processAreaPower()` |
| customInfo | JSON对象 | JSON_MERGE_PATCH合并 | 更新时合并而非覆盖 |
| 排序字段 | displayParam.sort | SQL ORDER BY子句 | ⚠️ 使用$注入，存在SQL注入风险 |

## 6. 业务规则

| 规则编号 | 规则描述 | 触发条件 | 执行逻辑 |
|----------|----------|----------|----------|
| SYS-001 | 用户名唯一性校验 | 新增用户时 | 调用`queryUserSizeByUserName()`检查，>0则不允许创建 |
| SYS-002 | 密码MD5加密 | 新增用户/重置密码时 | 使用`PasswordUtil.encryptMD5Password(password, username)`，以用户名为盐值 |
| SYS-003 | 密码过期时间 | 修改密码时 | 设置`pwdoverdue = date_add(NOW(), interval 3 MONTH)`，3个月后过期 |
| SYS-004 | 生产环境验证码校验 | 非CAS模式登录时 | 系统参数`sys.envirment.argu`为"1"时需验证码 |
| SYS-005 | 角色默认页面 | 新增角色时 | 默认设置为`module/Welcome1.action` |
| SYS-006 | 角色失效时间 | 角色状态为0时 | 必须设置effectiveTo失效时间 |
| SYS-007 | 密码重置强制下线 | 重置密码时 | 调用`forcedOffline()`踢掉该用户所有其他会话 |
| SYS-008 | 密码修改自动重新登录 | 修改密码时 | 登出当前会话 → 使用新密码重新登录 → 强制其他会话下线 |
| SYS-009 | 部门刷新策略 | 刷新部门数据时 | TRUNCATE清空 → 从SAP查询 → 批量插入 |
| SYS-010 | SQL执行安全限制 | 执行自定义SQL时 | 不允许执行不含WHERE条件的UPDATE/DELETE |
| SYS-011 | 用户权限校验 | UserManageAction访问时 | `prepare()`方法校验，仅管理员/工程管理部/工程管理部领导可访问 |
| SYS-012 | 文件上传MD5去重 | 富文本图片上传时 | 相同MD5的图片不重复存储 |
| SYS-013 | 集群缓存签名验证 | 集群缓存刷新时 | AES加密验证签名，管理员可直接访问 |
| SYS-014 | 用户菜单权限更新策略 | 编辑用户时 | 先删除原有菜单关联，再重新插入 |
| SYS-015 | 区域权限合并策略 | 编辑用户时 | 合并用户区域权限和部门编号 |

## 7. 配置项

| 配置项 | 配置Key | 默认值 | 说明 |
|--------|---------|--------|------|
| 系统环境参数 | `sys.envirment.argu` | - | "1"为生产环境（需验证码），其他为测试环境 |
| CAS模式开关 | `UserContext.isCas()` | - | 是否启用CAS单点登录 |
| CAS登出URL | 硬编码 | `https://cas.dptech.com:8443/logout` | CAS服务登出地址 |
| 邮件发送 | `fnd_mails`表 | - | 异步发送，定时任务扫描sendFlag=0的记录 |
| 文件上传路径 | `sys.upload.path` | - | 文件存储根路径 |
| 富文本图片路径 | 硬编码 | `upload/file/images` | 富文本编辑器图片存储目录 |
| iBatis缓存-用户 | `userCache` | CopyLRU, 1小时 | 用户、角色、菜单、权限相关查询缓存 |
| iBatis缓存-基础数据 | `basicCache` | CopyLRU, 24小时 | 基础数据相关查询缓存 |
| 密码过期周期 | 硬编码 | 3个月 | 修改密码后pwdoverdue设置为3个月后 |
| 随机密码长度 | 硬编码 | 8位 | `PasswordUtil.generatePass()`生成 |
