# 基础数据表结构（fnd_*）

> 数据库：dppms_d365 (MySQL)
> 模块：系统基础管理
> 命名前缀：fnd_
> 数据来源：[complete-data-dictionary.md](./complete-data-dictionary.md)（实际数据库导出）

> ⚠️ **修订说明**：本文档已基于实际数据库字段定义（complete-data-dictionary.md）和SQL映射文件代码进行全面校对。此前版本存在类型精度错误（如 fnd_user_info.password 标注为 VARCHAR(200) 实为 VARCHAR(32)）、虚构字段（如 fnd_department.createBy）、遗漏字段（如 fnd_user_info.isemail/defaultPage/pwdoverdue/customInfo）等问题。所有修正均以实际数据库结构为准，并添加了代码比对注释。

---

## 1. fnd_user_info（用户信息表）

系统用户基本信息表，存储登录凭证和个人信息。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(8) | PK, AUTO_INCREMENT | - | 用户ID | User.id；【疑问】int(8)而非int(11)，与其他表int(11)不一致 |
| username | varchar(128) | NOT NULL, UNIQUE | - | 登录账号 | User.username；resultMap: username←username |
| password | varchar(32) | NOT NULL | 5416d7cd... | 密码（MD5加密） | User.password；resultMap: password←password；默认值为MD5('初始密码') |
| email | varchar(128) | NOT NULL | - | 邮箱 | User.email；resultMap: email←email；【疑问】NOT NULL但部分用户邮箱为空 |
| dpNo | varchar(25) | - | NULL | 工号 | User.dpNo；resultMap: dpNo←dpNo |
| realName | varchar(128) | NOT NULL | - | 真实姓名 | User.realName；resultMap: realName←realName；此前文档错误标注为VARCHAR(50) |
| roleIds | varchar(64) | - | NULL | 角色ID集合（分号分隔，如`;12;`） | User.roleids；resultMap: roleids←roleIds（注意大小写映射） |
| isemail | int(11) | - | NULL | 邮件发送标志 | User.isemail；resultMap: isemail←isemail(nullValue=1)；此前文档遗漏 |
| status | int(1) | - | NULL | 状态（0=正常, 1=禁用） | User.status；【疑问】此前文档错误标注为CHAR(1) NOT NULL DEFAULT '1'，实际为int(1)可空无默认值 |
| defaultPage | varchar(255) | - | NULL | 用户登录默认首页 | User.defaultPage；resultMap: defaultPage←defaultPage；此前文档遗漏 |
| pwdoverdue | datetime | - | NULL | 密码过期时间 | User.pwdoverdue；resultMap: pwdoverdue←pwdoverdue；此前文档遗漏 |
| customInfo | json | - | NULL | 自定义扩展信息（JSON格式） | 继承CustomInfoEntity.customInfo；此前文档遗漏 |
| createBy | varchar(25) | - | NULL | 创建人 | BaseBean.createBy；此前文档错误标注为VARCHAR(100) |
| createTime | datetime | - | NULL | 创建时间 | BaseBean.createTime |
| updateBy | varchar(25) | - | NULL | 更新人 | BaseBean.updateBy；此前文档错误标注为VARCHAR(100) |
| updateTime | datetime | - | NULL | 更新时间 | BaseBean.updateTime |
| effectiveFrom | datetime | - | NULL | 生效开始时间 | BaseBean.effectiveFrom |
| effectiveTo | datetime | - | NULL | 生效结束时间 | BaseBean.effectiveTo |

> **此前文档错误**：
> - `password` 类型错误：VARCHAR(200)→varchar(32)
> - `realName` 长度错误：VARCHAR(50)→varchar(128)
> - `status` 类型完全错误：CHAR(1) NOT NULL DEFAULT '1'→int(1) 可空无默认值
> - `email` 约束错误：标注为可空，实际为 NOT NULL
> - `createBy`/`updateBy` 长度错误：VARCHAR(100)→varchar(25)
> - 虚构字段 `dpName`：实际数据库中不含此字段
> - 虚构字段 `roleName`：实际数据库中不含此字段（由代码通过roleIds关联查询）
> - 虚构字段 `jobDesc`：实际数据库中不含此字段
> - 遗漏字段 `isemail`、`defaultPage`、`pwdoverdue`、`customInfo`

**索引列表**：

| 索引名 | 列 | 唯一性 | 类型 | 用途 |
|--------|-----|--------|------|------|
| PRIMARY | id | 否 | BTREE | 主键 |
| username | username | 是 | BTREE | 用户名唯一约束 |

**SQL映射**：`sql-map-admin-config.xml`
- 主要SQL ID：`query-user-by-name`（登录验证）、`insert-user-object`、`update-user-object`、`update-user-chageloginpass`
- resultMap：`user_All`（完整映射含defaultPage/areapower）、`LoginInfo`（登录映射含areapower）、`user`（基础映射）

---

## 2. fnd_roles（角色表）

系统角色定义表。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(6) | PK, AUTO_INCREMENT | - | 角色ID | Role.id；【疑问】int(6)而非int(11) |
| roleName | varchar(64) | NOT NULL, UNIQUE | - | 角色名称 | Role.roleName；resultMap: roleName←roleName |
| defaultPage | varchar(255) | - | NULL | 角色默认首页 | Role.defaultPage；resultMap: defaultPage←defaultPage |
| status | int(1) | NOT NULL | - | 状态 | Role.status |
| roleRemark | varchar(200) | - | NULL | 角色备注 | Role.roleRemark；resultMap: roleRemark←roleRemark |
| createBy | varchar(25) | - | NULL | 创建人 | BaseBean.createBy；此前文档错误标注为VARCHAR(100) |
| createTime | datetime | - | NULL | 创建时间 | BaseBean.createTime |
| updateBy | varchar(25) | - | NULL | 更新人 | BaseBean.updateBy；此前文档错误标注为VARCHAR(100) |
| updateTime | datetime | - | NULL | 更新时间 | BaseBean.updateTime |
| effectiveFrom | datetime | - | NULL | 生效开始时间 | BaseBean.effectiveFrom |
| effectiveTo | datetime | - | NULL | 生效结束时间 | BaseBean.effectiveTo |

> **此前文档错误**：
> - `createBy`/`updateBy` 长度错误：VARCHAR(100)→varchar(25)

**索引列表**：

| 索引名 | 列 | 唯一性 | 类型 | 用途 |
|--------|-----|--------|------|------|
| PRIMARY | id | 否 | BTREE | 主键 |
| roleName | roleName | 否 | BTREE | 角色名查询（非唯一） |

**SQL映射**：`sql-map-admin-config.xml`
- `query_sys_roles`、`query-rolelist`、`insert-roleObject`、`update-roleObject`

---

## 3. fnd_department（部门表）

组织架构部门信息表。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 部门ID | Department.id |
| departmentNum | varchar(20) | NOT NULL, UNIQUE | - | 部门编号（办事处编码） | Department.departmentNum；resultMap: departmentNum←departmentNum |
| departmentName | varchar(20) | NOT NULL | - | 部门名称 | Department.departmentName；resultMap: departmentName←departmentName |
| isparam | int(11) | - | 0 | 是否参数化部门（0=否, 1=是） | Department.isparam |
| status | int(11) | NOT NULL | 1 | 状态（1=有效） | Department.status |
| createTime | datetime | - | NULL | 创建时间 | BaseBean.createTime |
| updateTime | datetime | - | NULL | 更新时间 | BaseBean.updateTime |
| effectiveFrom | datetime | - | NULL | 生效开始时间 | BaseBean.effectiveFrom |
| effectiveTo | datetime | - | NULL | 生效结束时间 | BaseBean.effectiveTo |

> **此前文档错误**：
> - 虚构字段 `createBy`：实际数据库中不含此字段
> - 虚构字段 `updateBy`：实际数据库中不含此字段

**索引列表**：

| 索引名 | 列 | 唯一性 | 类型 | 用途 |
|--------|-----|--------|------|------|
| PRIMARY | id | 否 | BTREE | 主键 |
| deparmentNum | departmentNum | 否 | BTREE | 部门编号查询（注意索引名拼写缺少t） |

**SQL映射**：`sql-map-admin-config.xml`
- `query-departmentlist`、`query_all_department`、`query_department_map`、`insert-departmentObject`、`truncate_department`、`query-sap-departmentList`

---

## 4. fnd_menus（菜单表）

系统菜单定义表，树形结构。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 菜单ID | UserMenu.id |
| menuCode | varchar(50) | - | NULL | 菜单编码 | UserMenu.menuCode；resultMap: menuCode←menuCode |
| menuName | varchar(25) | - | NULL | 菜单名称 | UserMenu.menuName；resultMap: menuName←menuName；此前文档错误标注为VARCHAR(50) |
| menuLevel | int(1) | - | NULL | 菜单级别 | UserMenu.menuLevel；resultMap: menuLevel←menuLevel |
| superId | int(11) | - | NULL | 父菜单ID | UserMenu.superId；resultMap: superId←superId |
| path | varchar(200) | - | NULL | 访问路径 | UserMenu.path；resultMap: path←path |
| effectiveFrom | datetime | - | NULL | 生效开始时间 | 此前文档遗漏 |
| effectiveTo | datetime | - | NULL | 生效结束时间 | 此前文档遗漏 |
| createBy | varchar(25) | - | NULL | 创建人 | 此前文档遗漏 |
| createTime | datetime | - | NULL | 创建时间 | 此前文档遗漏 |
| updateBy | varchar(25) | - | NULL | 更新人 | 此前文档遗漏 |
| updateTime | datetime | - | NULL | 更新时间 | 此前文档遗漏 |

> **此前文档错误**：
> - `menuName` 长度错误：VARCHAR(50)→varchar(25)
> - 遗漏字段 `effectiveFrom`/`effectiveTo`/`createBy`/`createTime`/`updateBy`/`updateTime`

**SQL映射**：`sql-map-admin-config.xml`
- `query_menu_modules`、`query-menu-byId`

---

## 5. fnd_role_menus（角色菜单权限表）

角色与菜单的权限关联表。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 主键ID | RoleMenuPower.id |
| roleId | int(11) | NOT NULL | - | 角色ID | RoleMenuPower.roleId；resultMap: roleId←roleId |
| menuId | int(11) | NOT NULL | - | 菜单ID | RoleMenuPower.menuId；resultMap: menuId←menuId |
| menuPower | varchar(20) | NOT NULL | - | 菜单权限（8:增加 1:删除 4:查找 2:更新，组合如"8412"） | RoleMenuPower.menuPower；resultMap: menuPower←menuPower |
| createTime | datetime | - | NULL | 创建时间 | BaseBean.createTime |
| updateTime | datetime | - | NULL | 更新时间 | BaseBean.updateTime |
| effectiveFrom | datetime | - | NULL | 生效开始时间 | BaseBean.effectiveFrom |
| effectiveTo | datetime | - | NULL | 生效结束时间 | BaseBean.effectiveTo |

> **此前文档错误**：
> - 虚构字段 `createBy`：实际数据库中不含此字段
> - 虚构字段 `updateBy`：实际数据库中不含此字段

**SQL映射**：`sql-map-admin-config.xml`
- `query-roleMenu-list`、`delete-roleMenuPower-byRoleId`、`insert-roleMenuPower-object`

---

## 6. fnd_user_menus（用户菜单权限表）

用户与菜单的个性化权限表。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 主键ID | MenuForUser.id |
| fnd_user_id | int(11) | - | NULL | 用户ID | MenuForUser.sys_user_id；【疑问】DB字段名为fnd_user_id，Java属性名为sys_user_id，命名不一致；此前文档错误标注为sys_user_id |
| username | varchar(128) | - | NULL | 用户名 | MenuForUser.username |
| menuCode | varchar(50) | - | NULL | 菜单编码 | MenuForUser.menuCode；resultMap: menuCode←menuCode |
| menuValue | int(1) | - | NULL | 菜单权限值 | MenuForUser.menuValue；【疑问】此前文档错误标注为VARCHAR类型，实际为int(1)；resultMap: menuValue←menuValue(Integer) |
| effectiveFrom | datetime | - | NULL | 生效开始时间 | 此前文档遗漏 |
| effectiveTo | datetime | - | NULL | 生效结束时间 | 此前文档遗漏 |
| createdBy | varchar(25) | - | NULL | 创建人 | 注意字段名为createdBy而非createBy |
| createTime | datetime | - | NULL | 创建时间 | 此前文档遗漏 |
| updateBy | varchar(25) | - | NULL | 更新人 | 此前文档遗漏 |
| updateTime | datetime | - | NULL | 更新时间 | 此前文档遗漏 |

> **此前文档错误**：
> - 字段名错误：`sys_user_id`→实际DB字段名为 `fnd_user_id`
> - `menuValue` 类型错误：VARCHAR→int(1)
> - 遗漏字段 `effectiveFrom`/`effectiveTo`/`createdBy`/`createTime`/`updateBy`/`updateTime`

**SQL映射**：`sql-map-admin-config.xml`
- `query_permissions_by_name`、`insert-menuForUser-object`、`delete-menuForUser-byUserId`

---

## 7. fnd_user_power（用户区域权限表）

用户区域/办事处权限表，控制用户可见的办事处范围。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 主键ID | - |
| fndUserId | int(11) | - | NULL | 用户ID | User.areapower通过LEFT JOIN关联 |
| username | varchar(25) | - | NULL | 用户名 | - |
| areapower | varchar(4096) | - | NULL | 区域权限（逗号分隔的办事处编码） | User.areapower；【疑问】此前文档错误标注为VARCHAR(2000)，实际为varchar(4096) |
| createTime | datetime | - | NULL | 创建时间 | BaseBean.createTime |
| createBy | varchar(25) | - | NULL | 创建人 | BaseBean.createBy |
| updateTime | datetime | - | NULL | 更新时间 | BaseBean.updateTime；此前文档遗漏 |
| updateBy | varchar(25) | - | NULL | 更新人 | BaseBean.updateBy；此前文档遗漏 |
| effectiveFrom | datetime | - | NULL | 生效开始时间 | BaseBean.effectiveFrom |
| effectiveTo | datetime | - | NULL | 生效结束时间 | BaseBean.effectiveTo |

> **此前文档错误**：
> - `areapower` 长度错误：VARCHAR(2000)→varchar(4096)
> - 遗漏字段 `updateTime`/`updateBy`

**SQL映射**：`sql-map-admin-config.xml`
- `update_user_power`、`insert_user_power`
- 注意：查询通过 fnd_user_info 的 LEFT JOIN 实现，无独立查询SQL

---

## 8. fnd_basic_data（基础数据表）

系统枚举值/字典数据表，存储各类下拉选项。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 主键ID | BasicDataBean.id |
| dataTypeCode | varchar(45) | - | NULL | 数据类型编码 | BasicDataBean.basicDataTypeCode；resultMap: basicDataTypeCode←dataTypeCode |
| basicDataId | varchar(255) | - | NULL | 基础数据ID | BasicDataBean.basicDataId；resultMap: basicDataId←basicDataId |
| basicDataName | varchar(255) | - | NULL | 基础数据名称 | BasicDataBean.basicDataName；resultMap: basicDataName←basicDataName |
| basicDataAttri1 | varchar(255) | - | NULL | 属性1（扩展属性） | BasicDataBean.basicDataAttri1；resultMap: basicDataAttri1←basicDataAttri1 |
| sortId | int(11) | - | NULL | 排序序号（数值越大越靠前） | BasicDataBean.sortId |
| createTime | datetime | - | NULL | 创建时间 | BaseBean.createTime |
| createBy | varchar(45) | - | NULL | 创建人 | BaseBean.createBy；此前文档错误标注为VARCHAR(100) |
| updateTime | datetime | - | NULL | 更新时间 | BaseBean.updateTime |
| updateBy | varchar(45) | - | NULL | 更新人 | BaseBean.updateBy；此前文档错误标注为VARCHAR(100) |
| effectiveFrom | datetime | - | NULL | 生效开始时间 | BaseBean.effectiveFrom |
| effectiveTo | datetime | - | NULL | 生效结束时间 | BaseBean.effectiveTo |

> **此前文档错误**：
> - `createBy`/`updateBy` 长度错误：VARCHAR(100)→varchar(45)

**索引列表**：

| 索引名 | 列 | 唯一性 | 类型 | 用途 |
|--------|-----|--------|------|------|
| PRIMARY | id | 否 | BTREE | 主键 |
| basicDataId | basicDataId | 是 | BTREE | 数据ID唯一约束 |
| basicDataId_dataTypeCode | dataTypeCode, basicDataId | 是 | BTREE | 类型+数据ID唯一约束 |

**常用 dataTypeCode 列表**：

| dataTypeCode | 含义 | 使用场景 |
|-------------|------|---------|
| 02 | 项目状态 | pm_project_header.projectState |
| 05 | 项目类型/等级 | pm_project_header.column010 |
| 15 | 项目实施方式 | pm_project_header.column012 |
| 22 | 工程计划状态 | pm_project_state.projectPlanState |
| majorProjectLevel | 重大项目级别 | pm_project_header.majorProjectLevel |
| projectExecutionState | 项目实施状态 | pm_project_state.executionState |
| projectCloseProcessState | 闭环流程状态 | pm_project_state.closeProcessState |

**SQL映射**：`sql-map-admin-config.xml`
- `query_basic_data`、`query_basic_data_all`、`insert_basic_data`、`update_basic_data`

---

## 9. fnd_basic_data_type（基础数据类型表）

基础数据分类定义表。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 主键ID | BasicDataBean.id（共用实体） |
| dataTypeCode | varchar(45) | - | NULL | 数据类型编码 | BasicDataBean.basicDataTypeCode；resultMap: basicDataTypeCode←dataTypeCode |
| dataTypeName | varchar(45) | - | NULL | 数据类型名称 | BasicDataBean.basicDataTypeName；resultMap: basicDataTypeName←dataTypeName |
| status | int(11) | - | NULL | 是否需要放在前台管理 | 此前文档遗漏 |
| createTime | datetime | - | NULL | 创建时间 | BaseBean.createTime |
| createBy | varchar(45) | - | NULL | 创建人 | BaseBean.createBy；此前文档遗漏 |
| updateTime | datetime | - | NULL | 更新时间 | BaseBean.updateTime；此前文档遗漏 |
| updateBy | varchar(45) | - | NULL | 更新人 | BaseBean.updateBy；此前文档遗漏 |
| effectiveFrom | datetime | - | NULL | 生效开始时间 | BaseBean.effectiveFrom |
| effectiveTo | datetime | - | NULL | 生效结束时间 | BaseBean.effectiveTo |

> **此前文档错误**：
> - 遗漏字段 `status`、`createBy`、`updateTime`、`updateBy`

**SQL映射**：`sql-map-admin-config.xml`
- `query_basic_data_type`

---

## 10. fnd_basic_prjstate（基础项目状态表）

基础数据与项目状态映射表，定义不同项目类型/分类下的状态配置。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 主键ID | 此前文档遗漏此表 |
| dataTypeCode | varchar(45) | - | NULL | 数据类型编码，对应fnd_basic_data | - |
| basicDataId | varchar(11) | - | NULL | 基础数据ID，对应fnd_basic_data | - |
| column010 | varchar(10) | - | NULL | 项目类型，对应pm_project_header.column010 | - |
| column011 | varchar(10) | - | NULL | 项目类别，对应pm_project_header.column011 | - |
| createTime | datetime | - | NULL | 创建时间 | BaseBean.createTime |
| createBy | varchar(45) | - | NULL | 创建人 | BaseBean.createBy |
| updateTime | datetime | - | NULL | 更新时间 | BaseBean.updateTime |
| updateBy | varchar(45) | - | NULL | 更新人 | BaseBean.updateBy |
| effectiveFrom | datetime | - | NULL | 生效开始时间 | BaseBean.effectiveFrom |
| effectiveTo | datetime | - | NULL | 生效结束时间 | BaseBean.effectiveTo |

**索引列表**：

| 索引名 | 列 | 唯一性 | 类型 | 用途 |
|--------|-----|--------|------|------|
| PRIMARY | id | 否 | BTREE | 主键 |
| dataTypeCode | dataTypeCode, basicDataId | 是 | BTREE | 类型+数据ID唯一约束 |

---

## 11. fnd_company（公司信息表）

组织机构/公司信息表。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 公司ID | Company.id |
| pid | int(11) | NOT NULL | - | 父组织机构ID | Company.pid |
| name | varchar(128) | NOT NULL | - | 组织机构全名 | Company.name |
| abbr | varchar(64) | NOT NULL | - | 组织机构简写 | Company.abbr |
| website | varchar(128) | - | NULL | 组织机构网址 | Company.website |
| code | varchar(25) | - | 0 | 组织机构代码 | Company.code；【疑问】默认值为'0'而非NULL |
| account | varchar(25) | - | NULL | 组织机构账套 | Company.account |
| status | smallint(1) | NOT NULL | 1 | 有效性（1=有效, 0=失效） | Company.status；【疑问】类型为smallint(1)而非INT |
| createTime | datetime | - | NULL | 创建时间 | BaseBean.createTime |
| createBy | varchar(32) | - | NULL | 创建人 | BaseBean.createBy；此前文档错误标注为VARCHAR(100) |
| updateTime | datetime | - | NULL | 更新时间 | BaseBean.updateTime |
| updateBy | varchar(32) | - | NULL | 更新人 | BaseBean.updateBy；此前文档错误标注为VARCHAR(100) |

> **此前文档错误**：
> - `status` 类型错误：INT→smallint(1)
> - `createBy`/`updateBy` 长度错误：VARCHAR(100)→varchar(32)
> - 遗漏字段 `pid`（父组织机构ID）

**索引列表**：

| 索引名 | 列 | 唯一性 | 类型 | 用途 |
|--------|-----|--------|------|------|
| PRIMARY | id | 否 | BTREE | 主键 |
| code | code | 是 | BTREE | 组织机构代码唯一约束 |
| pid | pid | 是 | BTREE | 父组织机构查询 |

**SQL映射**：`sql-map-admin-config.xml`
- `queryCompanyList`、`queryCompanyOne`

---

## 12. fnd_files（文件信息表）

系统上传文件信息表。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 文件ID | FileParam.id |
| fileName | varchar(255) | - | NULL | 文件名称 | FileParam.fileName；resultMap: fileName←fileName |
| filePath | varchar(255) | - | NULL | 文件路径 | FileParam.filePath；resultMap: filePath←filePath |
| fileType | varchar(255) | - | NULL | 文件分类 | FileParam.fileType |
| uploadBy | varchar(25) | - | NULL | 上传用户 | FileParam.uploadBy；resultMap: uploadBy←uploader |
| uploadTime | datetime | - | NULL | 上传时间 | FileParam.uploadTime；resultMap: uploadTime←uploadTime |

**SQL映射**：`sql-map-admin-config.xml`
- `insert_file_info`、`query_flie_info`、`query_file_map`、`query_file_list`、`delete_file`

---

## 13. fnd_mails（邮件表）

系统邮件队列表，存储待发送和已发送邮件。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 邮件ID | MailSenderInfo.id |
| mailSubject | varchar(255) | NOT NULL | - | 邮件主题 | MailSenderInfo.subject；resultMap: subject←mailSubject |
| mailContent | longtext | NOT NULL | - | 邮件正文 | MailSenderInfo.content；resultMap: content←mailContent；【疑问】此前文档错误标注为TEXT，实际为longtext |
| mailTos | text | - | NULL | 邮件主送（分号分隔） | MailSenderInfo.tos；resultMap: tos←mailTos |
| mailCcs | text | - | NULL | 邮件抄送 | MailSenderInfo.ccs；resultMap: ccs←mailCcs |
| mailBcc | text | - | NULL | 邮件密送 | MailSenderInfo.bcc；resultMap: bcc←mailBcc |
| mailAttachFiles | text | - | NULL | 邮件附件（特殊符号间隔） | MailSenderInfo.attachFileNames；resultMap: attachFileNames←mailAttachFiles |
| mailSendTime | datetime | - | NULL | 邮件实际发送时间 | MailSenderInfo.mailSendTime |
| mailExpectSendTime | datetime | - | NULL | 邮件期望发送时间 | MailSenderInfo.mailExpectSendTime；此前文档遗漏 |
| mailServerPort | varchar(25) | - | NULL | 邮件服务器端口 | MailSenderInfo.mailServerPort；resultMap: mailServerPort←mailserverPort |
| mailServerHost | varchar(25) | - | NULL | 邮件服务器地址 | MailSenderInfo.mailServerHost |
| mailUsername | varchar(25) | - | NULL | 登录用户名 | MailSenderInfo.userName；resultMap: userName←mailUsername |
| mailPassword | varchar(25) | - | NULL | 登录密码 | MailSenderInfo.password；resultMap: password←mailPassword |
| mailFromaddress | varchar(25) | - | NULL | 发送者地址 | MailSenderInfo.fromAddress；resultMap: fromAddress←mailFromaddress |
| sendFlag | int(11) | - | 0 | 发送状态（0=未发送, 1=已发送） | MailSenderInfo.sendFlag；此前文档遗漏 |
| createBy | varchar(25) | - | NULL | 创建人 | BaseBean.createBy |
| createTime | datetime | - | NULL | 创建时间 | BaseBean.createTime |
| updateBy | varchar(25) | - | NULL | 更新人 | BaseBean.updateBy |
| updatteTime | datetime | - | NULL | 更新时间 | BaseBean.updateTime；【疑问】DB字段名为updatteTime（多了一个t），疑似建表时拼写错误 |
| effectiveFrom | datetime | - | NULL | 生效开始时间 | BaseBean.effectiveFrom |
| effectiveTo | datetime | - | NULL | 生效结束时间 | BaseBean.effectiveTo |

> **此前文档错误**：
> - `mailContent` 类型错误：TEXT→longtext
> - 遗漏字段 `sendFlag`、`mailExpectSendTime`、`updateBy`、`effectiveFrom`/`effectiveTo`
> - `updateTime` 字段名错误：实际DB字段名为 `updatteTime`（拼写错误）

**SQL映射**：`sql-map-admin-config.xml`
- `insert_into_sys_mails`、`query_sys_mails`、`update_sys_mails_state`

---

## 14. fnd_sys_arg（系统参数表）

系统参数配置表。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 主键ID | Arg.id |
| code | varchar(25) | - | NULL | 参数编码 | Arg.code；`query_sys_arg`返回String |
| var | text | - | NULL | 参数值 | Arg.var；`refreshCacheData`更新此字段为当前时间 |
| mark | varchar(255) | - | NULL | 参数说明 | 此前文档遗漏 |
| effectiveFrom | datetime | - | NULL | 生效开始时间 | BaseBean.effectiveFrom；此前文档遗漏 |
| effectiveTo | datetime | - | NULL | 生效结束时间 | BaseBean.effectiveTo；此前文档遗漏 |

> **此前文档错误**：
> - 遗漏字段 `mark`、`effectiveFrom`、`effectiveTo`

**SQL映射**：`sql-map-admin-config.xml`
- `query_sys_arg`、`querySysArgList`、`refreshCacheData`

---

## 15. fnd_spms_arg（SPMS系统参数表）

SPMS备件系统参数配置表。结构与 fnd_sys_arg 相同。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 主键ID | - |
| code | varchar(25) | - | NULL | 参数编码 | - |
| var | text | - | NULL | 参数值 | - |
| mark | varchar(255) | - | NULL | 参数说明 | - |
| effectiveFrom | datetime | - | NULL | 生效开始时间 | - |
| effectiveTo | datetime | - | NULL | 生效结束时间 | - |

> **注意**：此表在项目代码中无SQL映射定义和DAO访问层，仅存在于数据库中。

---

## 16. fnd_data_refresh_log（数据刷新日志表）

数据同步任务执行日志表。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 主键ID | 此前文档遗漏此表 |
| refreshTaskName | varchar(100) | - | NULL | 刷新任务名称 | - |
| handleUser | varchar(15) | - | NULL | 处理人 | - |
| dataFrom | varchar(25) | - | NULL | 数据来源 | - |
| dataTo | varchar(25) | - | NULL | 数据目标 | - |
| refreshFrom | datetime | - | NULL | 刷新开始时间 | - |
| refreshTo | datetime | - | NULL | 刷新结束时间 | - |
| refreshState | int(11) | - | 0 | 刷新状态（0=失败, 1=成功） | - |
| refreshException | mediumtext | - | NULL | 异常信息 | - |

**SQL映射**：`sql-map-refresh-data-common-config.xml`

---

## 17. fnd_act_hi_comment（流程审批意见表）

Activiti流程审批意见记录表（fnd_前缀的辅助视图/表）。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | varchar(64) | PK | - | 审批意见ID |
| userId | varchar(255) | - | NULL | 审批人 |
| time | datetime | - | NULL | 审批时间 |
| taskId | varchar(64) | - | NULL | 任务ID |
| message | text | - | NULL | 审批意见内容 |
| type | varchar(255) | - | NULL | 意见类型 |

> **注意**：此表实际为Activiti的 `act_hi_comment` 表的视图或别名，用于在业务查询中关联审批意见。

---

## 表间关系概览

```
fnd_user_info (1) ──→ (N) fnd_user_menus       通过 id→fnd_user_id
fnd_user_info (1) ──→ (N) fnd_user_power        通过 id→fndUserId
fnd_user_info (1) ──→ (N) fnd_role_menus        通过 roleIds→roleId（间接关联）

fnd_roles (1) ──→ (N) fnd_role_menus            通过 id→roleId
fnd_menus (1) ──→ (N) fnd_role_menus            通过 id→menuId

fnd_basic_data_type (1) ──→ (N) fnd_basic_data  通过 dataTypeCode
fnd_basic_data (1) ──→ (N) fnd_basic_prjstate   通过 dataTypeCode+basicDataId

fnd_company ──→ 自关联                           通过 id→pid（树形结构）
```

---

## 修订记录

| 日期 | 修订内容 | 修正问题数 |
|------|---------|-----------|
| 2026-05-20 | 基于complete-data-dictionary.md全面校对字段定义 | 25+ |
| - | 修正类型精度错误（password VARCHAR(200)→varchar(32), status CHAR(1)→int(1), menuValue VARCHAR→int(1), mailContent TEXT→longtext等） | 6 |
| - | 修正字段长度错误（realName VARCHAR(50)→varchar(128), areapower VARCHAR(2000)→varchar(4096), createBy/updateBy VARCHAR(100)→varchar(25/45)等） | 8 |
| - | 删除虚构字段（fnd_department.createBy/updateBy, fnd_role_menus.createBy/updateBy, fnd_user_info.dpName/roleName/jobDesc） | 7 |
| - | 补充遗漏字段（fnd_user_info.isemail/defaultPage/pwdoverdue/customInfo, fnd_menus审计字段, fnd_user_menus全部遗漏字段, fnd_basic_data_type.status/createBy/updateBy/updateTime, fnd_sys_arg.mark/effectiveFrom/effectiveTo） | 15+ |
| - | 修正字段名（fnd_user_menus.sys_user_id→fnd_user_id, fnd_mails.updateTime→updatteTime） | 2 |
| - | 新增遗漏表（fnd_basic_prjstate, fnd_data_refresh_log, fnd_act_hi_comment） | 3 |
| - | 添加代码比对注释和【疑问】标记 | 全部表 |
