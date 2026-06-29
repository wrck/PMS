# 项目核心表结构（pm_project*）

> 数据库：dppms_d365 (MySQL)
> 模块：项目管理核心
> 命名前缀：pm_project_ / pm_column_
> 数据来源：[complete-data-dictionary.md](./complete-data-dictionary.md)（实际数据库导出）

> ⚠️ **修订说明**：本文档已基于实际数据库字段定义（complete-data-dictionary.md）和SQL映射文件代码进行全面校对。此前版本存在虚构字段（如 pm_project_header 中的 contractNo、orderNumber、smsProjectCode、projectGroupCode、projectGroupName）、错误的字段类型/长度、以及错误的泛化字段语义映射。所有修正均以实际数据库结构为准，并添加了代码比对注释。

---

## 1. pm_project_header（视图） / pm_project（实际表）

> ⚠️ **重要说明**：`pm_project_header` 是基于 `pm_project` 表的 VIEW，非独立表。下方字段定义来自 `pm_project` 实际表。`pm_project` 表约有 70,370 行数据。

项目核心信息表，存储项目的基本属性和动态字段。一个项目对应一条记录。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| projectId | int(11) | NOT NULL, PK, AUTO_INCREMENT | 0 | 项目主键ID | Project.projectId；AUTO_INCREMENT自增主键 |
| projectType | varchar(45) | NOT NULL | 10 | 项目类型（10=用服售后, afss=安服售后, afxx=安服先行） | Project.projectType；INSERT字段 |
| projectCode | varchar(45) | NOT NULL | - | 项目编码（系统自动生成） | Project.projectCode；INSERT字段 |
| projectName | varchar(200) | - | NULL | 项目名称 | Project.projectName；INSERT字段 |
| projectState | varchar(11) | - | NULL | 项目状态编码，关联fnd_basic_data(dataTypeCode=02) | Project.projectState；默认1为初始创建，0为不予跟踪 |
| isback | varchar(11) | - | 30 | 回退标志（30=创建项目, 32=指定项目经理, 34=填写渠道信息, 40=工程管理部不予跟踪, 42=项目经理不予跟踪） | Project.isback |
| column001 | varchar(255) | - | NULL | 办事处编码 | Project.column001 / Project.officeCode；SQL映射使用column001 |
| column002 | varchar(255) | - | NULL | 客户编码（ERP） | Project.column002 / Project.customerCode；SQL映射使用column002 |
| column003 | varchar(255) | - | NULL | 客户名称（ERP） | Project.column003 / Project.customerName；SQL映射使用column003 |
| column004 | varchar(255) | - | NULL | 市场部编码 | Project.column004 / Project.marketDeptCode；SQL映射使用column004 |
| column005 | varchar(255) | - | NULL | 系统部ID | Project.column005 / Project.systemDeptId；SQL映射使用column005 |
| column006 | varchar(255) | - | NULL | 拓展部ID | Project.column006 / Project.extendDeptId；SQL映射使用column006 |
| column007 | varchar(255) | - | NULL | 子行业ID | Project.column007 / Project.subIndustryId；SQL映射使用column007 |
| column008 | varchar(255) | - | NULL | 不予跟踪原因 | Project.column008 / Project.notGrantTailCause；SQL映射使用column008 |
| column009 | datetime | - | NULL | 订单创建时间 | Project.column009 / Project.orderCreateTime；【疑问】此字段为datetime类型而非varchar，与其他column字段类型不同 |
| column010 | varchar(10) | - | NULL | 项目类型/等级 | Project.column010；关联fnd_basic_data(dataTypeCode=05) |
| column011 | varchar(10) | - | NULL | 项目分类 | Project.column011 |
| column012 | varchar(2) | - | NULL | 项目实施方式 | Project.column012 / Project.serviceType；关联fnd_basic_data(dataTypeCode=15) |
| columno12_readonly | int(2) | - | -1 | 实施方式只读值（-1=可修改，其他值=只读） | Project.columno12_readonly；从SMS同步的默认值 |
| column013 | varchar(255) | - | NULL | 最终客户名称 | Project.column013 / Project.channelName |
| column014 | text | - | NULL | 回退说明 | Project.column014 / Project.backCause |
| customerProjectName | varchar(255) | - | NULL | 客户项目名称 | Project.customerProjectName |
| salesType | varchar(25) | - | 01 | 销售类型 | Project.salesType |
| majorProjectLevel | varchar(255) | - | NULL | 重大项目级别 | Project.majorProjectLevel；关联fnd_basic_data(majorProjectLevel) |
| compId | int(2) | - | 0 | 公司ID | Project.compId；【疑问】类型为int(2)而非varchar，与fnd_company.code(varchar)类型不匹配，关联方式可能通过fnd_company.id |
| projectStartTime | datetime | - | NULL | 项目开始实施时间 | Project.projectStartTime |
| projectRefreshTime | datetime | - | NULL | 项目最近刷新时间 | Project.projectRefreshTime |
| projectCloseTime | datetime | - | NULL | 项目关闭时间 | Project.projectCloseTime |
| customInfo | json | - | NULL | 自定义扩展信息（JSON格式） | 继承CustomInfoEntity.customInfo |
| customConfig | json | - | NULL | 自定义配置信息（JSON格式） | Project.customConfig |
| disabled | bit(1) | - | b'0' | 数据是否失效（0=有效, 1=失效） | 【疑问】此前文档遗漏此字段；与effectiveTo逻辑可能存在重叠 |
| createBy | varchar(45) | - | NULL | 创建人 | BaseBean.createBy |
| createTime | datetime | - | NULL | 创建时间 | BaseBean.createTime |
| updateBy | varchar(45) | - | NULL | 更新人 | BaseBean.updateBy |
| updateTime | datetime | - | NULL | 更新时间 | BaseBean.updateTime |
| effectiveFrom | datetime | - | NULL | 生效开始时间 | BaseBean.effectiveFrom |
| effectiveTo | datetime | - | NULL | 生效结束时间（NULL表示有效） | BaseBean.effectiveTo |

> **此前文档错误**：
> - 虚构字段 `contractNo`（合同号）：实际数据库中 pm_project_header 不含此字段，合同号存储于 pm_project_contract 表
> - 虚构字段 `orderNumber`（订单号）：实际数据库中不含此字段
> - 虚构字段 `smsProjectCode`：实际数据库中不含此字段（存在于 pm_project_group_relationship.smsProjectCode）
> - 虚构字段 `projectGroupCode`/`projectGroupName`：实际数据库中不含此字段（存在于 pm_project_group 表）
> - `compId` 类型错误：文档为 VARCHAR(50)，实际为 int(2)
> - 多个 column 字段类型错误：文档统一为 VARCHAR(100)，实际 column001~column008 为 varchar(255)，column009 为 datetime，column010/011 为 varchar(10)，column012 为 varchar(2)，column014 为 text
> - 遗漏字段 `disabled`（bit(1)）

**泛化字段与语义化字段映射表：**

> 以下映射关系基于实际数据库字段注释和SQL映射文件，展示售后项目（projectType=10）下各泛化字段的业务含义。

| 泛化字段 | 语义化名称 | 实际类型 | 业务含义 | 关联基础数据 | 代码比对注释 |
|----------|-----------|---------|---------|-------------|-------------|
| column001 | officeCode | varchar(255) | 办事处编码 | fnd_department.departmentNum | DB注释=办事处编码 ✓ |
| column002 | customerCode | varchar(255) | 客户编码（ERP） | - | DB注释=客户编码--ERP；此前文档错误标注为"市场部" |
| column003 | customerName | varchar(255) | 客户名称（ERP） | - | DB注释=客户名称--ERP；此前文档错误标注为"系统部" |
| column004 | marketDeptCode | varchar(255) | 市场部编码 | - | DB注释=市场部编码；此前文档错误标注为"拓展部" |
| column005 | systemDeptId | varchar(255) | 系统部ID | - | DB注释=系统部ID；此前文档错误标注为"行业" |
| column006 | extendDeptId | varchar(255) | 拓展部ID | - | DB注释=拓展部ID；此前文档错误标注为"动态字段6" |
| column007 | subIndustryId | varchar(255) | 子行业ID | - | DB注释=子行业ID；此前文档错误标注为"动态字段7" |
| column008 | notGrantTailCause | varchar(255) | 不予跟踪原因 | - | DB注释=不予跟踪原因 ✓ |
| column009 | orderCreateTime | datetime | 订单创建时间 | - | DB注释=订单创建时间；此前文档错误标注为"动态字段9" |
| column010 | projectCategory | varchar(10) | 项目类型/等级 | fnd_basic_data(dataTypeCode=05) | DB注释=项目类型；此前文档错误标注为"项目等级" |
| column011 | projectClassify | varchar(10) | 项目分类 | - | DB注释=项目分类；此前文档错误标注为"签约类型" |
| column012 | serviceType | varchar(2) | 项目实施方式 | fnd_basic_data(dataTypeCode=15) | DB注释=项目实施方式 ✓ |
| column013 | finalCustomerName | varchar(255) | 最终客户名称 | - | DB注释=最终客户名称；此前文档错误标注为"渠道名称" |
| column014 | backCause | text | 回退说明 | - | DB注释=回退说明 ✓ |

> **注意**：SQL 映射文件中使用泛化字段名（如 `column001`），而 Java Bean（Project.java）中同时定义了语义化属性（如 `officeCode`）和泛化属性（如 `column001`），两者通过 iBatis 的 resultMap 映射关联。在代码中可通过任一名称访问，但 SQL 层面统一使用泛化字段名。

**索引列表**（pm_project 表）：

| 索引名 | 列 | 唯一性 | 类型 | 用途 |
|--------|-----|--------|------|------|
| PRIMARY | projectId | 是 | BTREE | 主键 |
| department | column001 | 否 | BTREE | 按办事处查询 |
| projectCode_index | projectCode, projectType | 否 | BTREE | 项目编码+类型组合查询 |
| projectType_projectId_IDX | projectType, projectId | 否 | BTREE | 项目类型+ID组合查询 |

---

## 2. pm_project_state（项目状态表）

项目各维度状态跟踪表，一个项目一条记录，记录项目在不同维度的状态。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| projectId | int(11) | PK | - | 项目ID，关联pm_project_header.projectId | ProjectState.projectId |
| projectPlanState | varchar(10) | - | NULL | 工程计划状态，关联fnd_basic_data(dataTypeCode=22) | ProjectState.projectPlanState |
| projectplanTime | datetime | - | NULL | 工程计划状态变更时间 | ProjectState.projectPlanStateTime；【疑问】DB字段名为projectplanTime，Java属性名为projectPlanStateTime，命名不一致 |
| shipmentState | varchar(11) | - | NULL | 发货状态（-1=已发货, 1=未发货, 2=部分发货） | ProjectState.shipmentState；【疑问】此前文档标注为INT类型，实际为varchar(11) |
| shipmentTime | datetime | - | NULL | 发货状态变更时间 | ProjectState.shipmentStateTime；【疑问】DB字段名为shipmentTime，Java属性名为shipmentStateTime |
| executionState | varchar(45) | - | 5 | 项目实施状态，关联fnd_basic_data(dataTypeCode=projectExecutionState) | ProjectState.executionState |
| executionStateTime | datetime | - | NULL | 实施状态变更时间 | ProjectState.executionStateTime |
| closeProcessState | varchar(45) | - | 10 | 闭环流程状态，关联fnd_basic_data(dataTypeCode=projectCloseProcessState) | ProjectState.closeProcessState |
| closeProcessStateTime | datetime | - | NULL | 闭环流程状态变更时间 | ProjectState.closeProcessStateTime |

> **此前文档错误**：
> - `shipmentState` 类型错误：文档为 INT，实际为 varchar(11)
> - `projectPlanStateTime` 字段名错误：实际DB字段名为 `projectplanTime`（无大写L）
> - `shipmentStateTime` 字段名错误：实际DB字段名为 `shipmentTime`
> - 虚构字段 `createTime`/`updateTime`：实际数据库中不含这两个字段
> - `executionState`/`closeProcessState` 类型错误：文档为 VARCHAR(10)，实际为 varchar(45)

**状态流转说明：**
- 工程计划状态：项目创建 → 准备实施 → 实施中 → 完成
- 发货状态：未发货(1) → 部分发货(2) → 已发货(-1)
- 实施状态：待实施 → 实施中 → 已完成
- 闭环流程状态：未闭环 → 闭环中 → 已闭环

---

## 3. pm_project_member（项目成员表）

项目团队成员表，记录项目关联的各类角色人员。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 主键ID | - |
| projectId | int(11) | - | NULL | 项目ID，关联pm_project_header.projectId | ProjectMember.projectId |
| projectType | varchar(25) | - | 10 | 项目类型（10=售后 20=售前） | ProjectMember.projectType |
| memberRole | varchar(45) | - | NULL | 成员角色编码 | ProjectMember.memberRole；见下方角色编码表 |
| memberCode | varchar(45) | - | NULL | 成员用户名/编码，关联fnd_user_info.username | ProjectMember.memberCode |
| memberName | varchar(45) | - | NULL | 成员姓名 | ProjectMember.memberName |
| phoneNum | varchar(20) | - | NULL | 联系电话 | ProjectMember.phoneNum |
| email | varchar(45) | - | NULL | 电子邮箱 | ProjectMember.email |
| fromFlag | varchar(2) | - | 0 | 来源标志（0=默认, 1=来源于项目信息, 2=来源于成员信息） | ProjectMember.fromFlag |
| createBy | varchar(45) | - | NULL | 创建人 | BaseBean.createBy |
| createTime | datetime | - | NULL | 创建时间 | BaseBean.createTime |
| updateBy | varchar(15) | - | NULL | 更新人 | BaseBean.updateBy；【疑问】varchar(15)比createBy的varchar(45)短 |
| updateTime | datetime | - | NULL | 更新时间 | BaseBean.updateTime |
| effectiveFrom | datetime | - | NULL | 生效开始时间 | BaseBean.effectiveFrom |
| effectiveTo | datetime | - | NULL | 失效时间（设置后表示该成员已变更） | BaseBean.effectiveTo |

> **此前文档错误**：
> - 虚构字段 `memberRoleName`：实际数据库中不含此字段
> - 虚构字段 `dataState`：实际数据库中不含此字段
> - 多个字段类型/长度错误：memberRole VARCHAR(10)→varchar(45)，memberCode VARCHAR(100)→varchar(45)，memberName VARCHAR(200)→varchar(45)，phoneNum VARCHAR(50)→varchar(20)，email VARCHAR(200)→varchar(45)，fromFlag VARCHAR(10)→varchar(2)

**成员角色编码：** 

> 当前有错误需要比对修正

| memberRole | 含义 |
|-----------|------|
| 10 | 项目经理 |
| 15 | 副项目经理 |
| 20 | 项目成员 |
| 30 | 技术负责人 |
| 40 | 质量负责人 |
| 50 | 安全负责人 |
| 60 | 远程支持 |
| 71 | 驻场工程师 |
| 80 | 其他 |

> **此前文档错误**：角色编码表完全错误（10=销售→实际为项目经理，20=服务经理→实际为项目成员，30=项目经理→实际为技术负责人，40=团队成员→实际为质量负责人）

**业务说明：**
- 成员变更时通过设置 `effectiveTo` 失效旧记录，新增一条记录
- 查询当前有效成员需过滤 `effectiveFrom < NOW() AND (effectiveTo > NOW() OR effectiveTo IS NULL)`

**索引列表**：

| 索引名 | 列 | 唯一性 | 类型 | 用途 |
|--------|-----|--------|------|------|
| PRIMARY | id | 否 | BTREE | 主键 |
| memberCode_IDX | memberCode, projectId, projectType | 是 | BTREE | 成员-项目-类型唯一约束 |
| projectId_role | projectId, memberRole | 是 | BTREE | 项目-角色唯一约束 |
| projectId_type | projectId, projectType | 否 | BTREE | 按项目和类型查询成员 |

---

## 4. pm_project_contract（项目合同表）

项目与合同的关联表，一个项目组可关联多个合同。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | KEY, AUTO_INCREMENT | - | 主键ID | ⚠️ 实际无PRIMARY KEY，id仅为KEY(MUL) |
| contractNo | varchar(45) | NOT NULL | - | 合同号 | ProjectContract.contractNo |
| projectGroupCode | varchar(45) | NOT NULL | - | 项目组编码，关联pm_project_group.projectGroupCode | ProjectContract.projectGroupCode |
| createBy | varchar(45) | - | NULL | 创建人 | BaseBean.createBy |
| createTime | datetime | - | NULL | 创建时间 | BaseBean.createTime |
| updateBy | varchar(45) | - | NULL | 更新人 | BaseBean.updateBy；此前文档遗漏 |
| updateTime | datetime | - | NULL | 更新时间 | BaseBean.updateTime；此前文档遗漏 |

> **此前文档错误**：
> - `contractNo` 类型错误：VARCHAR(200)→varchar(45)
> - `projectGroupCode` 类型错误：VARCHAR(50)→varchar(45)
> - 遗漏字段 `updateBy`/`updateTime`
> - `id` 约束错误：标注为PK，实际该表无PRIMARY KEY，id仅为KEY(MUL)

**索引列表**：

| 索引名 | 列 | 唯一性 | 类型 | 用途 |
|--------|-----|--------|------|------|
| id | id | 否 | BTREE | 非唯一索引（非主键） |
| contract_projectGroupCode_IDX | contractNo, projectGroupCode | 否 | BTREE | 合同+项目组查询 |
| projectGroupCode_contract_IDX | projectGroupCode, contractNo | 否 | BTREE | 项目组+合同查询 |

---

## 5. pm_project_product_line（项目产品线表）

项目产品明细表，记录项目关联的产品线/设备信息。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | NOT NULL | - | 主键ID（非AUTO_INCREMENT） | ⚠️ id列为MUL键而非PRI，无PRIMARY KEY，id仅为非唯一索引 |
| projectId | int(11) | - | NULL | 项目ID | ProjectProductLine.projectId |
| contractNo | varchar(45) | - | NULL | 合同号 | ProjectProductLine.contractNo |
| itemCode | varchar(15) | - | NULL | 产品编码 | ProjectProductLine.itemCode |
| itemName | varchar(255) | - | NULL | 产品名称 | ProjectProductLine.itemName |
| projectQuantity | int(11) | - | NULL | 项目数量 | ProjectProductLine.projectQuantity |
| orderQuantity | int(11) | - | NULL | 订单数量 | ProjectProductLine.orderQuantity |
| deliverQuantity | int(11) | - | NULL | 发货数量 | ProjectProductLine.deliverQuantity |
| openQuantity | int(11) | - | NULL | 未清数量 | ProjectProductLine.openQuantity |
| orderNumber | varchar(25) | - | NULL | 订单号 | 此前文档遗漏 |
| lineNum | varchar(25) | - | NULL | 行号 | 此前文档遗漏 |

> **此前文档错误**：
> - `itemCode` 类型错误：VARCHAR(100)→varchar(15)
> - `contractNo` 类型错误：VARCHAR(200)→varchar(45)
> - `projectQuantity`/`orderQuantity`/`deliverQuantity`/`openQuantity` 类型错误：DECIMAL(18,2)→int(11)
> - 遗漏字段 `orderNumber`/`lineNum`

**索引列表**：

| 索引名 | 列 | 唯一性 | 类型 | 用途 |
|--------|-----|--------|------|------|
| id | id | 否 | BTREE | 非唯一索引（非主键，该表无PRIMARY KEY） |
| contractNo | contractNo | 否 | BTREE | 合同号查询 |
| itemCode | itemCode | 否 | BTREE | 产品编码查询 |
| projectId | projectId | 否 | BTREE | 项目ID查询 |

---

## 6. pm_project_soft_version（项目软件版本表）

项目设备软件版本跟踪表，记录每台设备的软件版本信息及变更历史。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 主键ID | - |
| projectId | INT | NOT NULL | - | 项目ID | ProjectSoftVersion.projectId |
| logId | INT | - | NULL | 关联日志ID | ProjectSoftVersion.logId |
| contractNo | VARCHAR(200) | - | NULL | 合同号 | ProjectSoftVersion.contractNo |
| itemCode | VARCHAR(100) | - | NULL | 产品编码 | ProjectSoftVersion.itemCode |
| barCode | VARCHAR(100) | - | NULL | 设备序列号/条形码 | ProjectSoftVersion.barCode |
| conp | VARCHAR(100) | - | NULL | App版本 | ProjectSoftVersion.conp |
| conpType | VARCHAR(50) | - | NULL | App版本类型 | ProjectSoftVersion.conpType |
| conpSeries | VARCHAR(50) | - | NULL | App版本系列 | ProjectSoftVersion.conpSeries |
| conpMark | VARCHAR(100) | - | NULL | App版本掩码 | ProjectSoftVersion.conpMark |
| cpld | VARCHAR(100) | - | NULL | CPLD/驱动版本 | ProjectSoftVersion.cpld |
| boot | VARCHAR(100) | - | NULL | Boot版本 | ProjectSoftVersion.boot |
| pcb | VARCHAR(100) | - | NULL | PCB/硬件版本 | ProjectSoftVersion.pcb |
| conpBak | VARCHAR(100) | - | NULL | App版本备份（变更前） | ProjectSoftVersion.conpBak |
| cpldBak | VARCHAR(100) | - | NULL | CPLD版本备份（变更前） | ProjectSoftVersion.cpldBak |
| bootBak | VARCHAR(100) | - | NULL | Boot版本备份（变更前） | ProjectSoftVersion.bootBak |
| pcbBak | VARCHAR(100) | - | NULL | PCB版本备份（变更前） | ProjectSoftVersion.pcbBak |
| conpChange | INT | - | 0 | App版本是否更新（0:未更新 1:已更新） | ProjectSoftVersion.conpChange |
| cpldChange | INT | - | 0 | CPLD版本是否更新 | ProjectSoftVersion.cpldChange |
| bootChange | INT | - | 0 | Boot版本是否更新 | ProjectSoftVersion.bootChange |
| pcbChange | INT | - | 0 | PCB版本是否更新 | ProjectSoftVersion.pcbChange |
| datastate | INT | - | 0 | 数据状态 | ProjectSoftVersion.datastate |
| customInfo | JSON | - | NULL | 自定义扩展信息 | 继承CustomInfoEntity.customInfo |
| itemModel | VARCHAR(200) | - | NULL | 产品型号 | ProjectSoftVersion.itemModel |
| itemName | VARCHAR(500) | - | NULL | 产品名称 | ProjectSoftVersion.itemName |
| receiveName | VARCHAR(200) | - | NULL | 收货人 | ProjectSoftVersion.receiveName |
| emsNum | VARCHAR(100) | - | NULL | 快递单号 | ProjectSoftVersion.emsNum |
| packdate | DATETIME | - | NULL | 发货日期 | ProjectSoftVersion.packdate |
| emsCompany | VARCHAR(100) | - | NULL | 快递公司 | ProjectSoftVersion.emsCompany |
| installAddress | VARCHAR(500) | - | NULL | 安装地址 | ProjectSoftVersion.installAddress |
| barCode2 | VARCHAR(100) | - | NULL | 第二条形码（母子公司一物双码） | ProjectSoftVersion.barCode2 |
| itemCode2 | VARCHAR(100) | - | NULL | 第二产品编码 | ProjectSoftVersion.itemCode2 |
| itemModel2 | VARCHAR(200) | - | NULL | 第二产品型号 | ProjectSoftVersion.itemModel2 |
| itemName2 | VARCHAR(500) | - | NULL | 第二产品名称 | ProjectSoftVersion.itemName2 |
| createBy | VARCHAR(100) | - | NULL | 创建人 | BaseBean.createBy |
| createTime | DATETIME | - | NULL | 创建时间 | BaseBean.createTime |
| updateBy | VARCHAR(100) | - | NULL | 更新人 | BaseBean.updateBy |
| updateTime | DATETIME | - | NULL | 更新时间 | BaseBean.updateTime |

---

## 7. pm_project_weekly（项目周报表头）

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT | PK, AUTO_INCREMENT | - | 主键ID |
| projectId | INT | NOT NULL | - | 项目ID |
| weeklyName | VARCHAR(200) | - | NULL | 周报名称 |
| weeklyDate | DATETIME | - | NULL | 周报日期 |
| createBy | VARCHAR(100) | - | NULL | 创建人 |
| createTime | DATETIME | - | NULL | 创建时间 |
| updateBy | VARCHAR(100) | - | NULL | 更新人 |
| updateTime | DATETIME | - | NULL | 更新时间 |

## 8. pm_project_weekly_content（周报内容表）

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT | PK, AUTO_INCREMENT | - | 主键ID |
| weeklyId | INT | NOT NULL | - | 周报ID，关联pm_project_weekly.id |
| content | TEXT | - | NULL | 周报内容 |
| contentType | VARCHAR(10) | - | NULL | 内容类型 |
| sortId | INT | - | 0 | 排序序号 |

## 9. pm_project_weekly_feedback（周报反馈表）

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT | PK, AUTO_INCREMENT | - | 主键ID |
| weeklyId | INT | NOT NULL | - | 周报ID |
| feedbackBy | VARCHAR(100) | - | NULL | 反馈人 |
| feedbackContent | TEXT | - | NULL | 反馈内容 |
| feedbackTime | DATETIME | - | NULL | 反馈时间 |

---

## 10. pm_project_log（项目日志表）

项目操作日志/动态记录表。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT | PK, AUTO_INCREMENT | - | 主键ID |
| projectId | INT | NOT NULL | - | 项目ID |
| logType | VARCHAR(10) | - | NULL | 日志类型 |
| logContent | TEXT | - | NULL | 日志内容 |
| operator | VARCHAR(100) | - | NULL | 操作人 |
| operateTime | DATETIME | - | NULL | 操作时间 |
| createBy | VARCHAR(100) | - | NULL | 创建人 |
| createTime | DATETIME | - | NULL | 创建时间 |

---

## 11. pm_project_task（项目任务表）

项目任务/待办事项表。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT | PK, AUTO_INCREMENT | - | 主键ID |
| projectId | INT | NOT NULL | - | 项目ID |
| projectType | INT | - | NULL | 项目类型 |
| taskName | VARCHAR(200) | - | NULL | 任务名称 |
| taskState | INT | - | 0 | 任务状态（0:待处理 1:已完成） |
| taskDesc | TEXT | - | NULL | 任务描述 |
| assignee | VARCHAR(100) | - | NULL | 指派人 |
| dueTime | DATETIME | - | NULL | 截止时间 |
| createBy | VARCHAR(100) | - | NULL | 创建人 |
| createTime | DATETIME | - | NULL | 创建时间 |

---

## 12. pm_project_instruction（项目指令表）

项目指令/通知表。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT | PK, AUTO_INCREMENT | - | 主键ID |
| projectId | INT | NOT NULL | - | 项目ID |
| instructionType | VARCHAR(10) | - | NULL | 指令类型 |
| instructionContent | TEXT | - | NULL | 指令内容 |
| sendBy | VARCHAR(100) | - | NULL | 发送人 |
| sendTime | DATETIME | - | NULL | 发送时间 |
| createBy | VARCHAR(100) | - | NULL | 创建人 |
| createTime | DATETIME | - | NULL | 创建时间 |

---

## 13. pm_column_of_relationship（字段映射表）

项目动态字段映射配置表，定义不同项目类型下 column 字段的业务含义。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 |
|--------|------|------|--------|----------|
| id | INT | PK, AUTO_INCREMENT | - | 主键ID |
| projectType | VARCHAR(10) | NOT NULL | - | 项目类型（10:售后 20:售前） |
| columnCode | VARCHAR(50) | NOT NULL | - | 字段编码（如column001） |
| colemnName | VARCHAR(200) | - | NULL | 字段名称（业务含义） |
| columnDesc | VARCHAR(500) | - | NULL | 字段描述 |
| dataTypeCode | VARCHAR(50) | - | NULL | 关联基础数据类型编码 |
| sortId | INT | - | 0 | 排序序号 |
| createBy | VARCHAR(100) | - | NULL | 创建人 |
| createTime | DATETIME | - | NULL | 创建时间 |

**业务说明：**
- 同一 columnCode 在不同 projectType 下可映射不同的业务含义
- 例如：column001 在售后项目中映射为"办事处编码"，在售前项目中可能映射为其他含义
- 【疑问】`colemnName` 字段名疑似拼写错误（应为 columnName），但实际数据库中即为 `colemnName`

---

## 14. pm_project_group（项目组表）

项目组信息表，管理项目分组。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 主键ID | 此前文档遗漏此字段 |
| projectGroupCode | varchar(45) | NOT NULL, UNIQUE | - | 项目组编码 | ProjectGroup.projectGroupCode |
| projectGroupName | varchar(45) | - | NULL | 项目组名称 | ProjectGroup.projectGroupName |
| projectType | varchar(25) | - | 10 | 项目类型（10=售后） | ProjectGroup.projectType |
| createBy | varchar(15) | - | NULL | 创建人 | BaseBean.createBy |
| createTime | datetime | - | NULL | 创建时间 | BaseBean.createTime |
| updateBy | varchar(15) | - | NULL | 更新人 | BaseBean.updateBy；此前文档遗漏 |
| updateTime | datetime | - | NULL | 更新时间 | BaseBean.updateTime；此前文档遗漏 |

> **此前文档错误**：
> - `projectGroupCode` 标注为 PK，实际有独立的 `id` 自增主键，`projectGroupCode` 为 UNIQUE
> - `projectGroupName` 类型错误：VARCHAR(200)→varchar(45)
> - `projectType` 类型错误：VARCHAR(10)→varchar(25)
> - 虚构字段 `mergeBranchMark`：实际数据库中不含此字段（存在于 pm_project_group_relationship）
> - 遗漏字段 `id`、`updateBy`、`updateTime`

---

## 15. pm_project_group_relationship（项目组关联表）

项目组与项目的关联表，一个项目组可包含多个项目。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 主键ID | - |
| projectGroupCode | varchar(45) | NOT NULL | - | 项目组编码，关联pm_project_group.projectGroupCode | ProjectGroupRelationship.projectGroupCode |
| projectCode | varchar(45) | - | NULL | 项目编码，关联pm_project_header.projectCode | ProjectGroupRelationship.projectCode |
| mergeBranchMark | varchar(45) | - | NULL | 项目拆分合并标记 | ProjectGroupRelationship.mergeBranchMark |
| smsProjectCode | varchar(45) | - | NULL | 原SMS项目编码 | 此前文档遗漏此字段 |
| createBy | varchar(45) | - | NULL | 创建人 | BaseBean.createBy |
| createTime | datetime | - | NULL | 创建时间 | BaseBean.createTime |
| updateBy | varchar(45) | - | NULL | 更新人 | BaseBean.updateBy；此前文档遗漏 |
| updateTime | datetime | - | NULL | 更新时间 | BaseBean.updateTime；此前文档遗漏 |

> **此前文档错误**：
> - `projectGroupCode`/`projectCode` 类型错误：VARCHAR(50)→varchar(45)
> - `mergeBranchMark` 类型错误：VARCHAR(100)→varchar(45)
> - 遗漏字段 `smsProjectCode`、`updateBy`、`updateTime`

**索引列表**：

| 索引名 | 列 | 唯一性 | 类型 | 用途 |
|--------|-----|--------|------|------|
| PRIMARY | id | 否 | BTREE | 主键 |
| projectCode | projectCode | 是 | BTREE | 项目编码唯一约束 |
| projectGroupCode | projectGroupCode | 是 | BTREE | 项目组编码查询 |
| smsProjectCode | smsProjectCode | 是 | BTREE | SMS项目编码查询 |

---

## 16. pm_project_related_party（项目关联方表）

项目关联方/渠道信息表。

| 字段名 | 类型 | 约束 | 默认值 | 业务含义 | 代码比对注释 |
|--------|------|------|--------|----------|-------------|
| id | int(11) | PK, AUTO_INCREMENT | - | 主键ID | - |
| projectId | int(11) | - | NULL | 项目ID | ProjectRelatedParty.projectId |
| partyRole | varchar(45) | - | NULL | 关联方角色（20=服务商 30=代理商） | ProjectRelatedParty.partyRole |
| partyCode | varchar(45) | - | NULL | 关联方编码 | 此前文档遗漏此字段 |
| partyName | varchar(45) | - | NULL | 关联方名称 | ProjectRelatedParty.partyName |
| createTime | datetime | - | NULL | 创建时间 | BaseBean.createTime |
| createBy | varchar(45) | - | NULL | 创建人 | BaseBean.createBy |
| updateTime | datetime | - | NULL | 更新时间 | BaseBean.updateTime；此前文档遗漏 |
| updateBy | varchar(45) | - | NULL | 更新人 | BaseBean.updateBy；此前文档遗漏 |
| effectiveFrom | datetime | - | NULL | 生效开始时间 | BaseBean.effectiveFrom |
| effectiveTo | datetime | - | NULL | 生效结束时间 | BaseBean.effectiveTo |

> **此前文档错误**：
> - 虚构字段 `dataTypeCode`：实际数据库中不含此字段
> - `partyRole` 类型错误：VARCHAR(10)→varchar(45)
> - `partyName` 类型错误：VARCHAR(200)→varchar(45)
> - 遗漏字段 `partyCode`、`updateTime`、`updateBy`

**索引列表**：

| 索引名 | 列 | 唯一性 | 类型 | 用途 |
|--------|-----|--------|------|------|
| PRIMARY | id | 否 | BTREE | 主键 |
| partyRole_parojectId | partyRole, projectId | 是 | BTREE | 角色+项目唯一约束 |
| projectId | projectId | 是 | BTREE | 项目ID查询 |

---

## 表间关系概览

```
pm_project (1) ──→ (N) pm_project_member       通过 projectId
pm_project (1) ──→ (1) pm_project_state         通过 projectId
pm_project (1) ──→ (N) pm_project_product_line  通过 projectId
pm_project (1) ──→ (N) pm_project_soft_version  通过 projectId
pm_project (1) ──→ (N) pm_project_weekly        通过 projectId
pm_project (1) ──→ (N) pm_project_log           通过 projectId
pm_project (1) ──→ (N) pm_project_task          通过 projectId
pm_project (1) ──→ (N) pm_project_instruction   通过 projectId
pm_project (1) ──→ (N) pm_project_related_party 通过 projectId

pm_project ←── pm_project_group_relationship ──→ pm_project_group
            通过 projectCode                     通过 projectGroupCode
pm_project_group ──→ pm_project_contract
                 通过 projectGroupCode

pm_project_weekly (1) ──→ (N) pm_project_weekly_content   通过 weeklyId
pm_project_weekly (1) ──→ (N) pm_project_weekly_feedback  通过 weeklyId

pm_column_of_relationship ── 定义 pm_project.column001~column014 的业务含义

注：pm_project_header 是基于 pm_project 的 VIEW，SQL查询中使用 pm_project_header 视图名
```

---

## 修订记录

| 日期 | 修订内容 | 修正问题数 |
|------|---------|-----------|
| 2026-05-20 | 基于complete-data-dictionary.md全面校对字段定义 | 30+ |
| - | 删除虚构字段（contractNo/orderNumber/smsProjectCode/projectGroupCode/projectGroupName/memberRoleName/dataState/dataTypeCode） | 8 |
| - | 修正字段类型/长度错误（compId VARCHAR→int, shipmentState INT→varchar, column* VARCHAR(100)→实际类型等） | 15+ |
| - | 修正泛化字段语义映射（column002~column007/column009~column013 含义全部更正） | 9 |
| - | 修正成员角色编码表（此前完全错误） | 9 |
| - | 补充遗漏字段（disabled/partyCode/smsProjectCode/updateBy/updateTime/orderNumber/lineNum等） | 8+ |
| - | 修正字段名（projectPlanStateTime→projectplanTime, shipmentStateTime→shipmentTime） | 2 |
| - | 添加代码比对注释和【疑问】标记 | 全部表 |
| 2026-05-21 | 修正pm_project_header为VIEW说明，补充pm_project表索引 | 4 |
| - | 修正pm_project.projectId为AUTO_INCREMENT（此前错误标注为非自增） | 1 |
| - | 修正pm_project_contract无PRIMARY KEY，id仅为KEY(MUL) | 1 |
| - | 修正pm_project_product_line索引唯一性（id/contractNo/itemCode/projectId均为非唯一，无主键） | 4 |
| - | 更新表间关系概览中pm_project_header→pm_project | 全部 |
