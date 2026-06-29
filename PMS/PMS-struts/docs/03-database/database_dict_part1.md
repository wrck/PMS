# DPPMS D365 全量数据字典

> 数据库: dppms_d365 | 生成时间: 2026-06-12 | 数据基准: 生产环境 information_schema 实时查询
> 业务含义来源: Java Bean 注释 + iBatis SQL映射 + 字段命名推断

---

## 目录

- [项目管理域 (pm_project*)](#一项目管理域-pm_project)
- [回访管理域 (pm_cl*)](#二回访管理域-pm_cl)
- [售前管理域 (pm_presales*)](#三售前管理域-pm_presales)
- [转包管理域 (pm_subcontract*)](#四转包管理域-pm_subcontract)
- [问题管理域 (prob*)](#五问题管理域-prob)
- [基础平台域 (fnd*)](#六基础平台域-fnd)

---

## 一、项目管理域 (pm_project)

### 1.1 pm_project -- 项目主表

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目主表，存储项目核心信息，是整个PMS系统的核心实体 |
| 数据量 | ~70,370 行 |
| 数据大小 | 45.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectId | int(11) | NO | - | PRI, auto_increment | 项目头信息主键,跟项目其他具体信息关联 | 项目唯一标识，关联项目其他子表 |
| projectType | varchar(45) | NO | 10 | MUL | 项目类型，用服售后:10，安服售后:afss，安服先行:afxx | 用服售后:10，安服售后:afss，安服先行:afxx |
| projectCode | varchar(45) | NO | - | MUL | 项目名称 | 项目唯一业务编码，由系统生成 |
| projectName | varchar(200) | YES | - |  | 项目名称 | 项目的业务名称 |
| projectState | varchar(11) | YES | - |  | 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态 | 项目阶段状态，1=初始创建，0=不予跟踪，对应fnd_basic_data(dataTypeCode=02) |
| isback | varchar(11) | YES | 30 |  | 30表示创建项目，32表示指定项目经理，34表示填写渠道信息 ,40表示工程管理部不予跟踪处理 ，42 表示项目经理选择不予跟踪 | 30=创建项目，32=指定项目经理，34=填写渠道信息，40=工程管理部不予跟踪，42=项目经理选择不予跟踪 |
| column001 | varchar(255) | YES | - | MUL | 办事处编码 | 逻辑外键 -> fnd_department.departmentNum |
| column002 | varchar(255) | YES | - |  | 客户编码--ERP | ERP系统中的客户编码 |
| column003 | varchar(255) | YES | - |  | 客户名称--ERP | ERP系统中的客户名称 |
| column004 | varchar(255) | YES | - |  | 市场部编码 | 市场部组织编码 |
| column005 | varchar(255) | YES | - |  | 系统部ID | 系统部组织ID |
| column006 | varchar(255) | YES | - |  | 拓展部ID | 拓展部组织ID |
| column007 | varchar(255) | YES | - |  | 子行业ID | 子行业分类ID |
| column008 | varchar(255) | YES | - |  | 不予跟踪原因 notGrantTailCause | notGrantTailCause，项目不予跟踪的原因说明 |
| column009 | datetime | YES | - |  | 订单创建时间 | 来自SMS系统的订单创建时间 |
| column010 | varchar(10) | YES | - |  | 项目类型 | 逻辑外键 -> fnd_basic_data(dataTypeCode=05)，项目等级分类 |
| column011 | varchar(10) | YES | - |  | 项目分类 | 项目业务分类 |
| column012 | varchar(2) | YES | - |  | 项目实施方式 | 实施方式编码，0/1/2/3/4对应不同实施模式 |
| columno12_readonly | int(2) | YES | -1 |  | 项目实施方式是否可以修改 -1表示可以改 其他值表示readonly | -1=可修改，其他值=只读（来自SMS的不可修改） |
| column013 | varchar(255) | YES | - |  | 最终客户名称 | 最终客户单位名称 |
| column014 | text | YES | - |  | 回退说明 | 项目回退时的说明文字 |
| customerProjectName | varchar(255) | YES | - |  | 客户项目名称 | 客户侧的项目名称 |
| salesType | varchar(25) | YES | 01 |  | 销售类型 | 01=正常，02=借转销，14=销售类借货 |
| majorProjectLevel | varchar(255) | YES | - |  | 重大项目级别 | 重大项目等级标识 |
| compId | int(2) | YES | 0 |  | 公司ID | 逻辑外键 -> fnd_company.id |
| createTime | datetime | YES | - |  | 记录数据创建时间 | 记录创建时间（指定服务经理时间） |
| createBy | varchar(45) | YES | - |  | 记录数据创建用户 | 记录创建用户 |
| updateTime | datetime | YES | - |  | 记录数据最新更新时间 | 记录最新更新时间 |
| updateBy | varchar(45) | YES | - |  | 记录数据最新更新用户 | 记录最新更新用户 |
| effectiveFrom | datetime | YES | - |  | 数据有效性开始时间 | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  | 数据有效性结束时间 | 数据有效性结束时间，NULL=当前有效 |
| disabled | bit(1) | YES | b'0' |  | 数据是否失效 | 0=有效，1=失效 |
| projectStartTime | datetime | YES | - |  | 项目开始实施时间 | 指定项目经理的时间 |
| projectRefreshTime | datetime | YES | - |  | 项目相关数据最后编辑时间 | 项目相关数据最后编辑时间 |
| projectCloseTime | datetime | YES | - |  | 项目闭环时间点 | 项目闭环的时间点 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段，存储serviceManagerCode/programManagerCode/programManagerCodeB等动态属性 |
| customConfig | json | YES | - |  | 自定义配置 | JSON配置字段 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| department | BTREE | NON-UNIQUE | column001 |
| PRIMARY | BTREE | UNIQUE | projectId |
| projectCode_index | BTREE | NON-UNIQUE | projectCode, projectType |
| projectType_projectId_IDX | BTREE | NON-UNIQUE | projectType, projectId |

---

### 1.2 pm_project_contract -- 项目对应的合同信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目对应的合同信息，一个项目组可关联多个合同 |
| 数据量 | ~79,021 行 |
| 数据大小 | 5.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 合同记录唯一标识 |
| contractNo | varchar(45) | NO | - | MUL | 合同号 | 合同编号，逻辑外键 -> pm_project_product_line.contractNo |
| projectGroupCode | varchar(45) | NO | - | MUL | 项目组编码 | 逻辑外键 -> pm_project_group.projectGroupCode |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(45) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(45) | YES | - |  |  | 记录最新更新用户编码 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contract_projectGroupCode_IDX | BTREE | NON-UNIQUE | contractNo, projectGroupCode |
| PRIMARY | BTREE | UNIQUE | id |
| projectGroupCode_contract_IDX | BTREE | NON-UNIQUE | projectGroupCode, contractNo |

---

### 1.3 pm_project_group -- 项目组信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目组信息，多个项目编码可归入同一项目组 |
| 数据量 | ~77,958 行 |
| 数据大小 | 4.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，项目组唯一标识 |
| projectGroupCode | varchar(45) | NO | - | UNI | 项目组组编码 | 项目组唯一编码 |
| projectGroupName | varchar(45) | YES | - |  | 项目组名称 | 项目组的业务名称 |
| projectType | varchar(25) | YES | 10 |  | 项目类型  默认10 为工程管理售后项目 | 默认10=工程管理售后项目 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(15) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(15) | YES | - |  |  | 记录最新更新用户编码 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectGroupCode_UNIQUE | BTREE | UNIQUE | projectGroupCode |

---

### 1.4 pm_project_group_relationship -- 项目编码与项目组的关联关系

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目编码与项目组的关联关系，支持项目拆分合并 |
| 数据量 | ~77,456 行 |
| 数据大小 | 6.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，关系记录唯一标识 |
| projectGroupCode | varchar(45) | NO | - | MUL | 项目组编码 | 逻辑外键 -> pm_project_group.projectGroupCode |
| projectCode | varchar(45) | YES | - | MUL | 项目编码 | 逻辑外键 -> pm_project.projectCode |
| mergeBranchMark | varchar(45) | YES | - |  | 项目拆分合并 | 标识项目拆分/合并的业务标记 |
| smsProjectCode | varchar(45) | YES | - | MUL | 原SMS项目编码 | 从SMS系统迁移过来的原始项目编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(45) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(45) | YES | - |  |  | 记录最新更新用户编码 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectCode | BTREE | NON-UNIQUE | projectCode |
| projectGroupCode | BTREE | NON-UNIQUE | projectGroupCode |
| smsProjectCode | BTREE | NON-UNIQUE | smsProjectCode |

---

### 1.5 pm_project_member -- 项目相关人员信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目相关人员信息，通过memberRole区分角色(10=销售,20=服务经理,30=项目经理) |
| 数据量 | ~302,428 行 |
| 数据大小 | 32.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，成员记录唯一标识 |
| projectId | int(11) | YES | - | MUL |  | 逻辑外键 -> pm_project.projectId 或 pm_presales_project_header.presalesId |
| projectType | varchar(25) | YES | 10 |  | 项目类型 售后10 或售前 20 详见fnd_basic_data | 售后10/售前20，详见fnd_basic_data |
| memberRole | varchar(45) | YES | - |  | 人员在项目中所处的角色 | 10=销售人员,20=服务经理,30=项目经理 |
| memberCode | varchar(45) | YES | - | MUL | 人员编码,外部人员为空 | 逻辑外键 -> fnd_user_info.username，外部人员为空 |
| memberName | varchar(45) | YES | - |  | 人员名称 | 项目成员的真实姓名 |
| phoneNum | varchar(20) | YES | - |  | 电话 | 项目成员联系电话 |
| email | varchar(45) | YES | - |  | 邮箱 | 项目成员邮箱地址 |
| fromFlag | varchar(2) | YES | 0 |  | 信息来源，1表示来源于项目信息，2表示来源于成员信息 | 1=来源于项目信息，2=来源于成员信息 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(45) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(15) | YES | - |  |  | 记录最新更新用户编码 |
| effectiveTo | datetime | YES | - |  | 有效结束时间 | NULL=当前有效，非NULL=已失效 |
| effectiveFrom | datetime | YES | - |  | 有效开始时间 | 数据有效性开始时间（软删除模式） |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| memberCode_IDX | BTREE | NON-UNIQUE | memberCode, projectId, projectType |
| PRIMARY | BTREE | UNIQUE | id |
| projectId_role | BTREE | NON-UNIQUE | projectId, memberRole |
| projectId_type | BTREE | NON-UNIQUE | projectId, projectType |

---

### 1.6 pm_project_state -- 项目各维度状态信息（工程计划/发货/实施/闭环）

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目各维度状态信息（工程计划/发货/实施/闭环），以projectId为主键 |
| 数据量 | ~45,915 行 |
| 数据大小 | 2.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectId | int(11) | NO | - | PRI |  | 逻辑外键 -> pm_project.projectId |
| projectPlanState | varchar(10) | YES | - | MUL | 工程计划状态 | 逻辑外键 -> fnd_basic_data |
| projectplanTime | datetime | YES | - |  | 工程计划状态更新时间 | 工程计划状态最后变更时间 |
| shipmentState | varchar(11) | YES | - | MUL | 项目发货状态 -1 已发货 1 未发货 2部分发货 | -1=已发货，1=未发货，2=部分发货 |
| shipmentTime | datetime | YES | - |  | 发货状态更新时间戳 | 发货状态最后变更时间 |
| executionState | varchar(45) | YES | 5 |  | 实施状态 | 项目实施阶段状态 |
| executionStateTime | datetime | YES | - |  | 实施状态更新时间 | 实施状态最后变更时间 |
| closeProcessState | varchar(45) | YES | 10 |  | 闭环流程状态 | 项目闭环流程阶段 |
| closeProcessStateTime | datetime | YES | - |  | 闭环流程状态更新时间 | 闭环流程状态最后变更时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| index_projectId | BTREE | UNIQUE | projectId |
| projectPlanState | BTREE | NON-UNIQUE | projectPlanState |
| shipmentState | BTREE | NON-UNIQUE | shipmentState |

---

### 1.7 pm_project_task -- 项目具体任务

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目具体任务，支持树形结构(parentId)，关联Activiti工作流 |
| 数据量 | ~59,042 行 |
| 数据大小 | 8.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| taskId | int(11) | NO | - | PRI, auto_increment | 任务ID | 任务自增主键，任务唯一标识 |
| projectId | int(11) | YES | - | MUL |  | 逻辑外键 -> pm_project.projectId |
| projectType | varchar(25) | YES | 10 | MUL | 项目类型 默认售后项目10 售前测试20 详见fnd_basic_data | 默认10=售后，20=售前测试 |
| contractNo | varchar(45) | YES | - |  | 合同号 | 关联合同编号 |
| taskTypeCode | varchar(45) | YES | - | MUL | 任务类型code，关联基础数据表 | 逻辑外键 -> fnd_basic_data |
| taskTypeId | varchar(25) | YES | - |  | 任务类型id，关联基础数据表 | 如completeTest=完成测试 |
| taskName | varchar(255) | YES | - |  | 任务名 | 任务业务名称 |
| eventPlanHappenDate | datetime | YES | - |  | 款项计划发生日期 | 款项计划发生日期 |
| eventPlanHappenDateENG | datetime | YES | - |  | 工程计划发生日期 | 工程计划发生日期 |
| planStartTime | datetime | YES | - |  | 计划开始日期 | 任务计划开始时间 |
| planEndTime | datetime | YES | - |  | 计划结束日期 | 任务计划结束时间 |
| actualStartTime | datetime | YES | - |  | 实际开始日期 | 任务实际开始时间 |
| eventActualFinishDate | datetime | YES | - |  | 实际完成日期 | 任务实际完成日期 |
| priority | varchar(25) | YES | - |  | 优先级 | 任务优先级 |
| progress | int(3) | YES | 0 |  | 进度百分比 | 0-100 |
| progressDesc | varchar(255) | YES | - |  | 进度描述 | 任务进度文字描述 |
| status | varchar(25) | YES | 0 |  | 状态 | 任务状态，0=未开始 |
| parentId | int(11) | YES | - |  | 父级任务 | 支持树形任务结构 |
| remark | text | YES | - |  | 备注 | 备注说明 |
| createTime | datetime | YES | - |  | 记录数据创建时间 | 记录创建时间 |
| createBy | varchar(45) | YES | - |  | 记录数据创建用户 | 记录创建用户编码 |
| updateTime | datetime | YES | - |  | 记录数据最新更新时间 | 记录最新更新时间 |
| updateBy | varchar(45) | YES | - |  | 记录数据最新更新用户 | 记录最新更新用户编码 |
| effectiveFrom | datetime | YES | - |  | 数据有效性开始时间 | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  | 数据有效性结束时间 | 数据有效性结束时间，NULL=当前有效 |
| visibleFlag | varchar(2) | YES | 1 |  | 是否可见，1表示可见，2表示不可见 | 1=可见，2=不可见 |
| deliverFileIds | varchar(255) | YES | - |  | 上传的交付件 | 逻辑外键 -> fnd_files.id |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | taskId |
| projectId | BTREE | NON-UNIQUE | projectId, projectType |
| projectType | BTREE | NON-UNIQUE | projectType, projectId |
| taskTypeCode_Id | BTREE | NON-UNIQUE | taskTypeCode, taskTypeId |

---

### 1.8 pm_project_related_party -- 项目相关的团体信息（渠道商、代理商、服务商等）

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目相关的团体信息（渠道商、代理商、服务商等） |
| 数据量 | ~126,864 行 |
| 数据大小 | 12.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，相关方记录唯一标识 |
| projectId | int(11) | YES | - | MUL |  | 逻辑外键 -> pm_project.projectId |
| partyRole | varchar(45) | YES | - | MUL |  | 0=服务商渠道，1=代理商渠道 |
| partyCode | varchar(45) | YES | - |  |  | 相关方（渠道商/代理商）编码 |
| partyName | varchar(45) | YES | - |  |  | 相关方（渠道商/代理商）名称 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(45) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  | 更新时间 | 记录最新更新时间 |
| updateBy | varchar(45) | YES | - |  | 更新人 | 记录最新更新用户编码 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| partyRole_parojectId | BTREE | NON-UNIQUE | partyRole, projectId |
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 1.9 pm_project_product_line -- 订单产品信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 订单产品信息，记录项目下的产品明细 |
| 数据量 | ~185,819 行 |
| 数据大小 | 25.1 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | MUL, auto_increment |  | 自增主键，产品线记录唯一标识 |
| projectId | int(11) | YES | - | MUL | 关联主表 | 逻辑外键 -> pm_project.projectId |
| contractNo | varchar(45) | YES | - | MUL | 合同号 | 逻辑外键 -> pm_project_contract.contractNo |
| itemCode | varchar(15) | YES | - | MUL | 产品编码 | ERP系统产品编码 |
| itemName | varchar(255) | YES | - |  | 产品名称 | 产品名称 |
| projectQuantity | int(11) | YES | - |  | 项目产品数量 | 项目产品总数量 |
| orderQuantity | int(11) | YES | - |  | 产品订单数量 | 已下单产品数量 |
| deliverQuantity | int(11) | YES | - |  | 已发货数量 | 已发货产品数量 |
| openQuantity | int(11) | YES | - |  | 未发货数量 | 未发货产品数量 |
| orderNumber | varchar(25) | YES | - |  |  | ERP订单号 |
| lineNum | varchar(25) | YES | - |  |  | 订单行号 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | contractNo |
| id | BTREE | NON-UNIQUE | id |
| itemCode | BTREE | NON-UNIQUE | itemCode |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 1.10 pm_project_shipment -- 项目发货记录

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目发货记录，支持串货转移 |
| 数据量 | ~460,132 行 |
| 数据大小 | 117.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，发货记录唯一标识 |
| projectId | int(11) | YES | - | MUL |  | 逻辑外键 -> pm_project.projectId |
| barcode | varchar(25) | YES | - | MUL |  | 设备序列号/条码 |
| itemCode | varchar(25) | YES | - |  |  | 发货产品编码 |
| itemModel | varchar(255) | YES | - |  |  | 产品型号 |
| itemName | varchar(255) | YES | - |  |  | 产品名称 |
| receiveName | varchar(255) | YES | - |  |  | 收货人姓名 |
| emsNum | varchar(255) | YES | - |  |  | 快递/物流单号 |
| emsCompany | varchar(15) | YES | - |  |  | 快递/物流公司名称 |
| packdate | datetime | YES | - |  |  | 设备打包日期 |
| contractNo | varchar(50) | YES | - | MUL |  | 关联合同编号 |
| installAddress | text | YES | - |  |  | 设备安装地址 |
| chProjectId | int(11) | YES | - |  | 串货转移之前的projectId | 串货转移前所属项目ID |
| chContractNo | varchar(50) | YES | - |  | 串货转移之前的contractNo | 串货转移前合同编号 |
| transferProjectId | int(11) | YES | - |  | 串货转移之后的projectId | 串货转移后目标项目ID |
| transferContractNo | varchar(50) | YES | - |  | 串货转移之后的projectId | 串货转移后合同编号 |
| transferFlag | varchar(2) | YES | -1 |  | 转移标识，默认:-1,转出:1，转入:0 | -1=默认，1=转出，0=转入 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barcode | BTREE | NON-UNIQUE | barcode |
| contractNo | BTREE | NON-UNIQUE | contractNo, barcode |
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 1.11 pm_project_maintenance -- 项目维护/巡检记录

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目维护/巡检记录，记录售后服务的详细过程 |
| 数据量 | ~184,753 行 |
| 数据大小 | 110.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，维护记录唯一标识 |
| projectId | int(11) | NO | - | MUL | 项目头信息主键 | 逻辑外键 -> pm_project.projectId |
| projectCode | varchar(45) | NO |  | MUL | 项目名称 | 项目编码，冗余存储 |
| projectName | varchar(200) | YES |  |  | 项目名称 | 项目名称，冗余存储 |
| projectType | int(11) | NO | 10 | MUL | 项目类型，售前:20/售后:10 | 售前:20/售后:10 |
| projectExecutionState | varchar(45) | YES |  |  | 项目实施状态 | 项目当前实施状态 |
| contractNo | varchar(255) | YES |  |  | 合同号 | 关联合同编号 |
| officeCode | varchar(25) | YES | - | MUL | 办事处编码 | 逻辑外键 -> fnd_department.departmentNum |
| compId | int(2) | YES | 1 |  | 所属公司 | 逻辑外键 -> fnd_company.id |
| type | varchar(45) | YES | - | MUL | 任务性质 | 服务任务性质编码 |
| category | varchar(45) | YES | - | MUL | 任务分类 | 服务任务分类编码 |
| subCategory | varchar(45) | YES | - | MUL | 任务小类 | 服务任务小类编码 |
| processTime | datetime | YES | - | MUL | 处理时间 | 服务处理时间 |
| processDesc | varchar(1024) | YES | - |  | 事项描述 | 服务事项描述 |
| processStep | varchar(1024) | YES | - |  | 解决进展 | 问题解决进展 |
| remainProblem | varchar(1024) | YES | - |  | 遗留问题 | 遗留问题描述 |
| transitHour | float | YES | 0 |  | 在途耗时(h) | 在途耗时（小时） |
| processHour | float | YES | 0 |  | 处理耗时(h) | 处理耗时（小时） |
| itemModel | varchar(255) | YES | - |  | 产品型号 | 服务产品型号 |
| softVersion | varchar(255) | YES | - |  | 在网版本 | 设备在网软件版本 |
| enabledFeatures | varchar(255) | YES | - |  | 启用功能 | 设备启用功能列表 |
| customTos | varchar(512) | YES | - |  | 自定义主送 | 自定义邮件主送人 |
| customCcs | varchar(512) | YES | - |  | 自定义抄送 | 自定义邮件抄送人 |
| hasReport | bit(1) | NO | b'0' |  | 是否有巡检报告 | 0=无巡检报告，1=有巡检报告 |
| quesnaireId | int(11) | YES | - |  | 问卷ID | 逻辑外键 -> pm_cl_quesnaire_result_header.id |
| deliverFileIds | varchar(255) | YES |  |  | 交付件，fnd_files id | 逻辑外键 -> fnd_files.id |
| warrantyStatus | varchar(25) | YES | - |  | 维保状态 | 项目维保状态 |
| industryName | varchar(25) | YES | - |  | 行业 | 客户所属行业 |
| userOffice | varchar(25) | YES | - |  | 用户办事处 | 用户所属办事处 |
| year | int(4) | YES | - |  | 所属年度 | 服务记录所属年度 |
| quarter | int(1) | YES | - |  | 所属季度 | 服务记录所属季度(1-4) |
| month | int(2) | YES | - |  | 所属月份 | 服务记录所属月份(1-12) |
| wsCount | int(2) | YES | - |  | 当前维保服务次数 | 当前维保服务次数 |
| wafCount | int(2) | YES | - |  | 当前其他服务次数 | 当前其他服务次数 |
| wsYearCount | int(2) | YES | - |  | 维保服务年次数 | 年度维保服务累计次数 |
| wafYearCount | int(2) | YES | - |  | 其他服务年次数 | 年度其他服务累计次数 |
| warrantyInfo | varchar(4096) | YES | - |  | 维保信息 | 维保服务详细信息 |
| serviceInfo | varchar(2048) | YES | - |  | 其他服务信息 | 其他服务详细信息 |
| remark | varchar(2048) | YES | - |  | 备注 | 备注说明 |
| createTime | datetime | YES | - | MUL | 创建时间 | 记录创建时间 |
| createBy | varchar(45) | YES | - | MUL | 创建用户 | 记录创建用户编码 |
| updateTime | datetime | YES | - |  | 最新更新时间 | 记录最新更新时间 |
| updateBy | varchar(45) | YES | - |  | 最新更新用户 | 记录最新更新用户编码 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| category | BTREE | NON-UNIQUE | category, subCategory |
| createBy | BTREE | NON-UNIQUE | createBy |
| createTime | BTREE | NON-UNIQUE | createTime |
| officeCode | BTREE | NON-UNIQUE | officeCode |
| PRIMARY | BTREE | UNIQUE | id |
| processTime_IDX | BTREE | NON-UNIQUE | processTime |
| projectCode | BTREE | NON-UNIQUE | projectCode |
| projectId | BTREE | NON-UNIQUE | projectId |
| projectType | BTREE | NON-UNIQUE | projectType |
| subCategory | BTREE | NON-UNIQUE | subCategory |
| type | BTREE | NON-UNIQUE | type |

---

### 1.12 pm_project_log -- 项目主要操作跟踪日志

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目主要操作跟踪日志 |
| 数据量 | ~6,411 行 |
| 数据大小 | 496.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，日志记录唯一标识 |
| projectId | int(11) | YES | - | MUL |  | 逻辑外键 -> pm_project.projectId |
| handleName | varchar(255) | YES | - |  | 操作名称 | 操作名称（如：指定项目经理） |
| handleDesc | varchar(255) | YES | - |  | 操作描述或原因 | 操作描述或原因说明 |
| handleUser | varchar(45) | YES | - |  | 操作用户 | 执行操作的用户编码 |
| taskStartTime | datetime | YES | - |  | 操作开始时间 | 操作开始时间 |
| handleEndTime | datetime | YES | - |  | 操作结束时间 | 操作结束时间 |
| handleState | int(11) | YES | - |  | 有无通知用户 0 无 1 有 | 0=无通知，1=已通知 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 1.13 pm_project_instruction -- 总部或领导对项目的批示及反馈

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 总部或领导对项目的批示及反馈 |
| 数据量 | ~127 行 |
| 数据大小 | 48.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，批示记录唯一标识 |
| projectId | int(11) | YES | - | MUL | 项目头关联主键 | 逻辑外键 -> pm_project.projectId |
| instructionsInfo | text | YES | - |  | 批示内容或反馈内容 | 批示/反馈的具体内容 |
| instructionsTime | datetime | YES | - |  | 批示时间或反馈时间 | 批示/反馈的时间 |
| instructionsUser | varchar(45) | YES | - |  | 批示用户或反馈用户 | 批示/反馈的用户编码 |
| dataType | int(11) | YES | 0 |  | 数据类型  0 批示信息 1 批示反馈 | 0=批示信息，1=批示反馈 |
| instructionsId | int(11) | YES | - |  | 批示ID 针对批示反馈的信息 | 反馈对应的原始批示记录ID |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 1.14 pm_project_supervision -- 项目督查头信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目督查头信息，记录督查任务 |
| 数据量 | ~818 行 |
| 数据大小 | 192.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，督查记录唯一标识 |
| projectId | int(11) | NO | - |  | 项目头信息主键 | 逻辑外键 -> pm_project.projectId |
| projectCode | varchar(45) | NO | - | MUL | 项目名称 | 项目编码 |
| projectName | varchar(200) | YES | - |  | 项目名称 | 项目名称 |
| channel | varchar(64) | YES | - |  | 代理商/服务商 | 代理商/服务商名称 |
| officeCode | varchar(25) | YES | - | MUL | 办事处编码 | 办事处编码 |
| type | varchar(25) | YES | - |  | 任务性质 | 督查任务性质 |
| processTime | datetime | YES | - |  | 处理时间 | 督查处理时间 |
| state | bit(1) | NO | b'0' |  | 是否完成 | 0=未完成，1=已完成 |
| isDelete | bit(1) | NO | b'0' |  | 是否删除 | 0=未删除，1=已删除 |
| quesnaireId | int(11) | YES | - |  | 问卷ID | 逻辑外键 -> pm_cl_quesnaire_result_header.id |
| deliverFileIds | varchar(255) | YES |  |  | 交付件，fnd_files id | 逻辑外键 -> fnd_files.id |
| remark | text | YES | - |  | 备注 | 备注说明 |
| createTime | datetime | YES | - |  | 创建时间 | 记录创建时间 |
| createBy | varchar(45) | YES | - |  | 创建用户 | 记录创建用户编码 |
| updateTime | datetime | YES | - |  | 最新更新时间 | 记录最新更新时间 |
| updateBy | varchar(45) | YES | - |  | 最新更新用户 | 记录最新更新用户编码 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| department | BTREE | NON-UNIQUE | officeCode |
| PRIMARY | BTREE | UNIQUE | id |
| projectCode_index | BTREE | NON-UNIQUE | projectCode |

---

### 1.15 pm_project_warranty_callback -- 项目维保回访问卷表

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目维保回访问卷表，记录维保回访详情 |
| 数据量 | ~5,588 行 |
| 数据大小 | 2.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 项目维保回访问卷表 | 自增主键，维保回访记录唯一标识 |
| projectId | int(11) | YES | - | MUL | 项目ID | 逻辑外键 -> pm_project.projectId |
| projectCode | varchar(45) | YES | - | MUL | 项目编码 | 项目编码 |
| officeCode | varchar(25) | YES | - |  | 办事处 | 办事处编码 |
| contractNos | varchar(255) | YES | - |  | 合同号 | 关联合同编号（多个逗号分隔） |
| projectIds | varchar(255) | YES | - |  | 关联的项目 | 关联项目ID（多个逗号分隔） |
| projectName | varchar(255) | YES | - |  | 项目名称 | 项目名称 |
| serviceImpl | varchar(25) | YES | - |  | 实施方式 | 项目实施方式编码 |
| industryName | varchar(25) | YES | - |  | 行业 | 客户所属行业 |
| agentChannel | varchar(255) | YES | - |  | 下单代理商 | 下单代理商名称 |
| finalCustomerName | varchar(255) | YES | - |  | 最终客户单位 | 最终客户单位名称 |
| customer1 | tinytext | YES | - |  | 客户联系人1 | 客户联系人1姓名 |
| customerContact1 | tinytext | YES | - |  | 客户联系方式1 | 客户联系人1联系方式 |
| customer2 | tinytext | YES | - |  | 客户联系人2 | 客户联系人2姓名 |
| customerContact2 | tinytext | YES | - |  | 客户联系方式2 | 客户联系人2联系方式 |
| warrantyStartTime | date | YES | - |  | 维保开始日期 | 维保合同开始日期 |
| warrantyEndTime | date | YES | - |  | 维保结束日期 | 维保合同结束日期 |
| renewalIntention | int(1) | YES | - |  | 续保意向,0:无,1:有,2:待定 | 0=无，1=有，2=待定 |
| callbackTime | datetime | YES | - |  | 回访时间 | 回访时间 |
| nextCallbackTime | datetime | YES | - |  | 下次回访时间 | 下次回访时间 |
| taskId | varchar(25) | YES | - |  | 任务ID | Activiti任务ID |
| quesnaireId | int(11) | YES | - |  | 问卷ID | 逻辑外键 -> pm_cl_quesnaire_result_header.id |
| quesnaireVersion | int(11) | YES | - |  | 问卷版本 | 问卷模板版本号 |
| quesnaireState | int(11) | YES | - |  | 状态 -1 草稿 1已提交 | -1=草稿，1=已提交 |
| isDelete | bit(1) | YES | b'0' |  | 删除标记 | 0=未删除，1=已删除 |
| remark | varchar(255) | YES | - |  | 备注 | 备注说明 |
| compId | int(2) | YES | 0 |  | 所属公司 | 所属公司ID |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| customInfo | json | YES | - |  |  | JSON扩展字段 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectCode | BTREE | NON-UNIQUE | projectCode |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 1.16 pm_project_weekly -- 项目周报主表

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目周报主表 |
| 数据量 | ~932 行 |
| 数据大小 | 208.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| weeklyId | int(11) | NO | - | PRI, auto_increment |  | 周报自增主键 |
| projectId | int(11) | YES | - | MUL | 项目信息头ID | 逻辑外键 -> pm_project.projectId |
| currentTask | varchar(100) | YES | - |  | 当前工程阶段 | 当前工程阶段名称 |
| taskStartTime | datetime | YES | - |  | 阶段开始时间 | 当前阶段开始时间 |
| taskEndTime | datetime | YES | - |  | 阶段结束时间 | 当前阶段结束时间 |
| taskDeviation | text | YES | - |  | 偏差 | 进度偏差说明 |
| remark | text | YES | - |  | 备注 | 备注说明 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| weeklyStartTime | datetime | YES | - |  | 报告开始时间 | 周报统计周期开始时间 |
| weeklyEndTime | datetime | YES | - |  | 报告结束时间 | 周报统计周期结束时间 |
| weeklyState | int(11) | YES | 0 |  | 周报状态 0 草稿 1提交 | 0=草稿，1=已提交 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | weeklyId |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 1.17 pm_project_weekly_content -- 项目周报详细内容

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目周报详细内容 |
| 数据量 | ~12,979 行 |
| 数据大小 | 1.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，内容记录唯一标识 |
| weeklyId | int(11) | YES | - | MUL |  | 逻辑外键 -> pm_project_weekly.weeklyId |
| optionDesc001 | text | YES | - |  |  | 周报选项描述1（工作内容） |
| optionDesc002 | text | YES | - |  |  | 周报选项描述2（下周计划） |
| optionType | int(11) | YES | - |  | option对应周报的部分 | 选项类型，对应周报不同部分 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(15) | YES | - |  |  | 记录创建用户编码 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| weeklyId | BTREE | NON-UNIQUE | weeklyId |

---

### 1.18 pm_project_soft_version -- 项目设备软件版本信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目设备软件版本信息，记录conp/cpld/boot/pcb等版本 |
| 数据量 | ~532,125 行 |
| 数据大小 | 327.8 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 项目软件版本表 | 自增主键，版本记录唯一标识 |
| projectId | int(11) | YES | - | MUL | 项目ID | 逻辑外键 -> pm_project.projectId |
| logId | int(11) | YES | - |  | 软件版本变更记录 | 逻辑外键 -> pm_project_soft_change_logs.id |
| contractNo | varchar(100) | YES | - |  | 合同号 | 关联合同编号 |
| itemCode | varchar(25) | YES | - |  | 产品编码 | 产品编码 |
| barCode | varchar(25) | YES | - | MUL | 设备序列号 | 设备序列号/条码 |
| conp | varchar(100) | YES | - | MUL |  | CONP软件版本号 |
| conpType | varchar(100) | YES | - |  | 版本类型 | CONP版本类型 |
| conpSeries | varchar(100) | YES | - |  | 版本系列 | CONP版本系列 |
| conpMark | varchar(255) | YES | - |  | 软件版本掩码 | 软件版本掩码，用于版本范围匹配 |
| conpBak | varchar(255) | YES | - |  | 备份变更之前的版本 | CONP变更前备份版本号 |
| conpChange | int(11) | YES | - |  | 0无更新 1有更新 | 0=CONP无更新，1=CONP有更新 |
| cpld | varchar(100) | YES | - |  |  | CPLD版本号 |
| cpldBak | varchar(255) | YES | - |  |  | CPLD变更前备份版本号 |
| cpldChange | int(11) | YES | - |  |  | 0=CPLD无更新，1=CPLD有更新 |
| boot | varchar(100) | YES | - |  |  | Boot版本号 |
| bootBak | varchar(255) | YES | - |  |  | Boot变更前备份版本号 |
| bootChange | int(11) | YES | - |  |  | 0=Boot无更新，1=Boot有更新 |
| pcb | varchar(100) | YES | - |  |  | PCB版本号 |
| pcbBak | varchar(255) | YES | - |  |  | PCB变更前备份版本号 |
| pcbChange | int(11) | YES | - |  |  | 0=PCB无更新，1=PCB有更新 |
| executeTime | date | YES | - |  | 若有更新的情况下为执行更新时间，否则没有实际意义 | 版本更新执行日期 |
| datastate | int(11) | YES | - | MUL | 数据状态 0 失效 1 有效 | 0=失效，1=有效 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barcode | BTREE | NON-UNIQUE | barCode |
| idx_conp_item_query | BTREE | NON-UNIQUE | datastate, conpType, conpSeries, conpMark, itemCode, projectId |
| pm_project_soft_version_conp_IDX | BTREE | NON-UNIQUE | conp |
| PRIMARY | BTREE | UNIQUE | id |
| projectBarcodeValid | BTREE | NON-UNIQUE | projectId, barCode, datastate |

---

### 1.19 pm_project_notification -- 项目通知信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目通知信息 |
| 数据量 | ~152,161 行 |
| 数据大小 | 13.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，通知记录唯一标识 |
| notifySubject | varchar(255) | YES | - |  | 通知标题 | 通知标题 |
| notifyContent | text | YES | - |  | 通知内容 | 通知正文内容 |
| projectId | int(11) | YES | - | MUL | 相关项目ID | 逻辑外键 -> pm_project.projectId |
| createTime | datetime | YES | - |  | 创建时间 | 记录创建时间 |
| createBy | varchar(25) | YES | - |  | 创建用户 | 记录创建用户编码 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 1.20 pm_project_notification_state -- 通知的阅读状态记录

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 通知的阅读状态记录 |
| 数据量 | ~9,905 行 |
| 数据大小 | 1.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，状态记录唯一标识 |
| notifyId | int(11) | YES | - | MUL |  | 逻辑外键 -> pm_project_notification.id |
| notifyObject | varchar(25) | YES | - |  | 通知主题，系统用户 | 通知接收用户编码 |
| notifyState | int(11) | YES | - |  | 通知状态，有无通知 0 无 1 有 | 0=未读，1=已读 |
| checkTime | datetime | YES | - |  | 用户查看通知时间 | 用户查看通知的时间 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| notifyId | BTREE | NON-UNIQUE | notifyId |
| PRIMARY | BTREE | UNIQUE | id |

---

### 1.21 pm_project_header_view_cache -- pm_project_header视图的物化缓存表

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | pm_project_header视图的物化缓存表，加速项目列表查询 |
| 数据量 | ~71,993 行 |
| 数据大小 | 31.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| projectCode | varchar(45) | YES | - | MUL | 原SMS项目编码 | 项目编码 |
| subProjectCode | varchar(45) | NO | - |  | 项目名称 | 子项目/合同级别编码 |
| projectName | varchar(200) | YES | - |  | 项目名称 | 项目名称 |
| contractNo | varchar(45) | YES | - | MUL | 合同号 | 合同编号 |
| majorProjectLevel | varchar(255) | YES | - |  | 重大项目级别 | 重大项目级别 |
| officeName | varchar(20) | YES | - |  |  | 办事处名称（冗余） |
| customerName | varchar(255) | YES | - |  | 客户名称--ERP | 客户名称（冗余） |
| marketName | varchar(255) | YES | - |  | 市场部编码 | 市场部名称（冗余） |
| systemName | varchar(255) | YES | - |  | 系统部ID | 系统部名称（冗余） |
| expendName | varchar(255) | YES | - |  | 拓展部ID | 拓展部名称（冗余） |
| industryName | varchar(255) | YES | - |  | 子行业ID | 行业名称（冗余） |
| salesManCode | varchar(45) | YES | - |  |  | 销售人员编码 |
| salesManName | varchar(45) | YES | - |  |  | 销售人员姓名 |
| salesManTel | varchar(45) | YES | - |  |  | 销售人员电话 |
| salesManMail | varchar(100) | YES | - |  |  | 销售人员邮箱 |
| smCode | varchar(45) | YES | - |  | 人员编码,外部人员为空 | 服务经理编码 |
| smName | varchar(45) | YES | - |  | 人员名称 | 服务经理姓名 |
| pmCode1 | varchar(45) | YES | - |  | 人员编码,外部人员为空 | 项目经理1编码 |
| pmName1 | varchar(45) | YES | - |  | 人员名称 | 项目经理1姓名 |
| pmCode2 | varchar(45) | YES | - |  | 人员编码,外部人员为空 | 项目经理2编码 |
| pmName2 | varchar(45) | YES | - |  | 人员名称 | 项目经理2姓名 |
| compId | int(2) | YES | - |  | 公司ID | 公司ID |
| compName | varchar(128) | YES | - |  | 组织机构全名 | 公司名称（冗余） |
| ssfsName | varchar(255) | YES | - |  |  | 实施方式名称 |
| partnerChannel | varchar(45) | YES | - |  |  | 合作伙伴渠道名称 |
| projectType | varchar(4) | NO |  | MUL |  | 项目类型编码 |
| finalCustomerName | varchar(255) | YES | - |  | 最终客户名称 | 最终客户名称 |
| customerProjectName | varchar(255) | YES | - |  | 客户项目名称 | 客户项目名称 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contractNo | BTREE | NON-UNIQUE | contractNo |
| projectCode | BTREE | NON-UNIQUE | projectCode |
| projectType | BTREE | NON-UNIQUE | projectType |

---

## 二、回访管理域 (pm_cl)

### 2.1 pm_cl_callback -- 运营商直签项目回访申请主表

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 运营商直签项目回访申请主表，关联Activiti工作流 |
| 数据量 | ~2,729 行 |
| 数据大小 | 384.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 运营商直签项目回访申请主表 | 回访申请ID |
| projectId | int(11) | YES | - | MUL | 项目ID | 逻辑外键 -> pm_project.projectId |
| instId | varchar(25) | YES | - | MUL | 流程ID | Activiti流程实例ID |
| remark | text | YES | - |  | 回访申请备注 | 回访申请备注 |
| applyState | int(11) | YES | - |  | -1草稿 1 审批中 2审批通过 | -1=草稿，1=审批中，2=审批通过 |
| applyBy | varchar(25) | YES | - |  | 申请人 | 回访申请人编码 |
| applyTime | datetime | YES | - |  | 申请时间 | 回访申请时间 |
| createTime | timestamp | YES | - |  |  | 记录创建时间 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| instId | BTREE | NON-UNIQUE | instId |
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 2.2 pm_cl_callback_quesnaire -- 回访与问卷的关联表

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 回访与问卷的关联表，一次回访可关联多个问卷版本 |
| 数据量 | ~2,855 行 |
| 数据大小 | 224.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，关联记录唯一标识 |
| callBackId | int(11) | YES | - | MUL | 回访主键主表 | 逻辑外键 -> pm_cl_callback.id |
| taskId | varchar(25) | YES | - |  | 对应任务ID | Activiti任务ID |
| quesnaireId | int(11) | YES | - | MUL | 对应pm_cl_quesnaire_result_header主键 | 逻辑外键 -> pm_cl_quesnaire_result_header.id |
| quesnaireVersion | int(11) | YES | - |  | 版本号 | 问卷模板版本号 |
| quesnaireState | int(11) | YES | - |  | -1 草稿 1已提交 | 0=未填写，1=已填写 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| callBackId | BTREE | NON-UNIQUE | callBackId |
| PRIMARY | BTREE | UNIQUE | id |
| quesnaireId | BTREE | NON-UNIQUE | quesnaireId |

---

### 2.3 pm_cl_evaluation_header -- 客户评价表头

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 客户评价表头，记录评价基本信息 |
| 数据量 | ~25,911 行 |
| 数据大小 | 5.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) unsigned | NO | - | PRI, auto_increment |  | 自增主键，评价记录唯一标识 |
| projectCode | varchar(45) | NO | - | MUL | 评测项目编码 | 评测项目编码 |
| projectId | int(11) | NO | 0 | MUL | 项目ID | 支持多项目逗号分隔 |
| projectName | varchar(120) | YES | - |  | 项目名称 | 项目名称 |
| evaluationTime | datetime | YES | 0000-00-00 00:00:00 |  | 审核时间 | 审核时间 |
| evaluationPeopleName | varchar(45) | YES | - |  | 审核人员姓名 | 审核人员姓名 |
| evaluationScore | double | NO | 0 |  | 评测总分数 | 评测总分数 |
| evaluationResult | int(11) | NO | 0 |  | 评测结果（通过/未通过） | 评测结果（通过/未通过） |
| evaluationComment | text | YES | - |  | 项目评价（驳回时为驳回原因） | 项目评价（驳回时为驳回原因） |
| evaluationType | int(11) | NO | 0 |  | 400回访/项目组总分评定 | 400回访/项目组总分评定 |
| status | int(11) | NO | 0 |  |  | 回访申请状态，0=待审核，1=已审核 |
| createdTime | datetime | YES | 0000-00-00 00:00:00 |  |  | 记录创建时间 |
| createdPerson | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updatedTime | datetime | YES | 0000-00-00 00:00:00 |  |  | 记录最新更新时间 |
| updatedPerson | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| nextAcceptPerson | varchar(25) | YES | - |  | 下一个接收申请的人员 | 下一处理人编码 |
| evaluationPeopleId | varchar(25) | YES | - |  | 审核人员用户名 | 审核人员用户名 |
| nextAcceptPersonName | varchar(25) | YES | - |  |  | 下一处理人姓名 |
| applyHeaderId | int(11) | NO | 0 |  | 申请表Id | 申请表Id |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| projectCode_index | BTREE | NON-UNIQUE | projectCode |
| projectId | BTREE | NON-UNIQUE | projectId |

---

### 2.4 pm_cl_quesnaire_result_header -- 问卷结果头表

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 问卷结果头表，记录一次问卷填写的结果 |
| 数据量 | ~103,363 行 |
| 数据大小 | 6.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) unsigned | NO | - | PRI, auto_increment |  | 自增主键，问卷结果唯一标识 |
| evaluationHeaderId | int(11) | NO | - | MUL | 测评记录Id | 逻辑外键 -> pm_cl_evaluation_header.id |
| quesnaireTemplateHeaderId | int(11) | YES | - |  | 问卷模板Id | 逻辑外键 -> pm_cl_quesnaire_template_header.id |
| quesMarkScore | double | YES | 0 |  | 问卷得分 | 问卷得分 |
| createdTime | datetime | YES | - |  |  | 记录创建时间 |
| createdPerson | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updatedTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updatedPerson | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| quesMarkResult | int(11) | YES | - |  | 评分结果 | 评分结果 |
| status | int(11) | NO | 0 |  |  | 问卷结果状态，0=未完成，1=已完成 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| index_evaluationHeaderId | BTREE | NON-UNIQUE | evaluationHeaderId |
| PRIMARY | BTREE | UNIQUE | id |

---

### 2.5 pm_cl_quesnaire_result_line -- 问卷结果行表

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 问卷结果行表，记录每个问题的回答 |
| 数据量 | ~454,563 行 |
| 数据大小 | 28.6 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) unsigned | NO | - | PRI, auto_increment |  | 自增主键，结果行唯一标识 |
| quesnaireTemplateHeaderId | int(11) | NO | - | MUL | 回访问卷Id | 回访问卷Id |
| quesnaireTemplateLineId | int(11) | NO | - | MUL | 问卷中问题的Id | 逻辑外键 -> pm_cl_quesnaire_template_line.id |
| questionTemplateOptId | int(11) | YES | - |  | 选中的选项id | 选中的选项id |
| questionAnswer | text | YES | - |  |  | 题目答案/正确选项文本 |
| questionScore | double | NO | 0 |  | 问题得分 | 问题得分 |
| quesnaireResultHeaderId | int(11) | NO | - | MUL | 回访结果头信息Id | 逻辑外键 -> pm_cl_quesnaire_result_header.id |
| createdTime | datetime | YES | - |  |  | 记录创建时间 |
| createdPerson | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updatedTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updatedPerson | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| quesTypeForCB | varchar(10) | YES | - |  | 问题回访类型 | 问题回访类型 |
| quesEvaResult | int(11) | YES | - |  | 选项是否为不同选项 | 选项是否为不同选项 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| quesnaireResultHeaderId | BTREE | NON-UNIQUE | quesnaireResultHeaderId, quesTypeForCB |
| quesnaireTemplateHeaderId | BTREE | NON-UNIQUE | quesnaireTemplateHeaderId, quesnaireTemplateLineId |
| quesnaireTemplateLineId | BTREE | NON-UNIQUE | quesnaireTemplateLineId, questionTemplateOptId |

---

### 2.6 pm_cl_quesnaire_template_header -- 问卷模板定义头表

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 问卷模板定义头表 |
| 数据量 | ~13 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(10) unsigned | NO | - | PRI, auto_increment |  | 自增主键，模板唯一标识 |
| questionnaireTemplateNum | varchar(45) | NO | - |  | 问卷模板编号 | 问卷模板编号 |
| questionnaireTemplateName | varchar(200) | NO | - |  | 问卷模板名称 | 问卷模板名称 |
| questionnaireScore | double | NO | 0 |  | 问卷总分数 | 问卷满分 |
| questionnairePassScore | double | NO | 0 |  | 问卷达标分数 | 问卷及格分 |
| questionnaireStatus | int(11) | NO | 0 |  | 问卷状态 | 模板状态，0=禁用，1=启用 |
| effectiveStartTime | datetime | YES | - |  |  | 模板生效开始时间 |
| effectiveEndTime | datetime | YES | - |  |  | 模板生效结束时间 |
| createdTime | datetime | YES | - |  |  | 模板创建时间 |
| updatedTime | datetime | YES | - |  |  | 模板最后修改时间 |
| createdPerson | varchar(25) | YES | - |  |  | 模板创建人 |
| updatedPerson | varchar(25) | YES | - |  |  | 模板最后修改人 |
| quesType | varchar(25) | YES | - | MUL |  | 问卷业务类型 |
| markIndexs | varchar(45) | YES | - |  | 问卷计分规则的index | 标记索引，用于模板配置 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| quesType | BTREE | NON-UNIQUE | quesType |

---

### 2.7 pm_cl_quesnaire_template_line -- 问卷模板题目定义

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 问卷模板题目定义 |
| 数据量 | ~80 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) unsigned | NO | - | PRI, auto_increment |  | 自增主键，题目唯一标识 |
| questionContent | varchar(200) | NO | - |  | 题目内容 | 题目内容 |
| questionType | int(11) | NO | - |  | 题目类型,如:多选\单选 | 题目类型（单选/多选/文本） |
| questionScore | double | NO | 0 |  | 题目分数 | 题目分值 |
| questionRemark | varchar(200) | YES | - |  | 题目备注 | 题目备注 |
| questionNum | int(11) | NO | 0 |  | 问题编号,表示了问卷中问题的顺序 | 问题编号,表示了问卷中问题的顺序 |
| quesnaireTemplateHeaderId | int(11) | NO | 0 | MUL | 问卷模板Id | 逻辑外键 -> pm_cl_quesnaire_template_header.id |
| questionStatus | int(11) | YES | 0 |  |  | 题目状态，0=禁用，1=启用 |
| effectiveStartTime | datetime | YES | - |  |  | 生效开始时间 |
| effectiveEndTime | datetime | YES | - |  |  | 生效结束时间 |
| createdTime | datetime | YES | - |  |  | 记录创建时间 |
| updatedTime | datetime | YES | - |  |  | 记录最新更新时间 |
| createdPerson | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updatedPerson | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| questionTypeForCB | varchar(10) | YES | - |  | 回访问题类型 | 回访问题类型 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| id_UNIQUE | BTREE | UNIQUE | id |
| PRIMARY | BTREE | UNIQUE | id |
| quesnaireTemplateHeaderId | BTREE | NON-UNIQUE | quesnaireTemplateHeaderId |

---

### 2.8 pm_cl_quesnaire_template_options -- 问卷模板题目选项

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 问卷模板题目选项 |
| 数据量 | ~231 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) unsigned | NO | - | PRI, auto_increment |  | 自增主键，选项唯一标识 |
| questionId | int(11) | NO | 0 |  | 题目Id | 题目Id |
| questionOptionNum | int(11) | NO | 0 |  | 选项编号 | 选项编号 |
| questionOptionsContent | varchar(200) | NO | - |  | 选项内容 | 选项内容 |
| questionOptionScore | double | YES | 0 |  | 选项分数 | 选项分数 |
| quesnaireTemplateHeaderId | int(11) | NO | 0 |  | 问卷模板Id | 问卷模板Id |
| effectiveStartTime | datetime | YES | - |  |  | 生效开始时间 |
| effectiveEndTime | datetime | YES | - |  |  | 生效结束时间 |
| createdTime | datetime | YES | - |  |  | 记录创建时间 |
| updatedTime | datetime | YES | - |  |  | 记录最新更新时间 |
| createdPerson | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updatedPerson | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| quesLineType | varchar(10) | YES | - |  | 问题类型 | 问题类型 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| id_UNIQUE | BTREE | UNIQUE | id |
| PRIMARY | BTREE | UNIQUE | id |

---

## 三、售前管理域 (pm_presales)

### 3.1 pm_presales_project_header -- 售前测试项目主表

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 售前测试项目主表，关联Activiti工作流 |
| 数据量 | ~16,660 行 |
| 数据大小 | 6.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| presalesId | int(11) | NO | - | PRI, auto_increment | 售前项目主表 | 售前项目自增主键 |
| instId | varchar(64) | YES | - | MUL | activity工作流流程ID | Activiti流程实例ID |
| applyState | int(11) | YES | - |  | -1草稿 1 审批中 2结束 | -1=草稿，1=审批中，2=审批通过 |
| applyBy | varchar(25) | YES | - |  | 申请人 | 售前申请人编码 |
| applyTime | datetime | YES | - |  | 申请时间 | 售前申请时间 |
| endTime | datetime | YES | - |  | 申请结束时间 | 项目结束时间 |
| projectState | varchar(25) | YES | 10 |  | 项目状态 ，同售后项目状态  10 未创建 20 直接闭环 30 已创建 31待指派项目经理 32 项目经理跟踪 33工程管理部回访 100闭环 | 项目状态编码 |
| presalesCode | varchar(64) | YES | - |  | 售前项目编码 | 售前项目唯一编码 |
| projectCode | varchar(64) | YES | - | MUL | 项目编码 | 关联的原售后项目编码 |
| projectName | varchar(255) | YES | - |  | 项目名称 | 项目名称 |
| projectType | varchar(25) | YES | - |  |  | 逻辑外键 -> fnd_basic_data(dataTypeCode=presalesType) |
| marketName | varchar(25) | YES | - |  | 市场部名称 | 市场部名称 |
| systemName | varchar(25) | YES | - |  | 系统部名称 | 系统部名称 |
| expendName | varchar(25) | YES | - |  | 拓展部名称 | 拓展部名称 |
| industryName | varchar(25) | YES | - |  | 子行业名称 | 行业名称 |
| officeCode | varchar(25) | YES | - |  | 办事处编码 | 逻辑外键 -> fnd_department.departmentNum |
| salesman | varchar(25) | YES | - |  | 销售人员 | 销售人员姓名 |
| productManager | varchar(25) | YES | - |  | 产品经理 | 产品经理姓名 |
| salesmanLink | varchar(125) | YES | - |  | 销售人员联系方式 | 销售人员联系方式 |
| lendInfoId | varchar(64) | YES | - | MUL | SMS系统测试类借货申请主键，标识存在则不再刷新过来 | 逻辑外键 -> pm_presales_lend_info_from_oa |
| lendfiles | varchar(2048) | YES | - |  | 借货交付件 从SMS中同步过来 | 借货交付件信息 |
| confirmFileIds | varchar(2048) | YES | - |  | 现场测试服务确认单 | 逻辑外键 -> fnd_files.id |
| hasRma | int(1) | YES | 0 |  | 是否有未核销数据 | 是否存在未核销RMA数据 |
| hasTransfer | int(1) | YES | 0 |  | 是否发生借转销 | 是否存在借转销数据 |
| closeRemark | varchar(512) | YES | - |  | 闭环备注 | 项目关闭备注 |
| createBy | varchar(25) | YES | - |  | 数据创建人 | 记录创建用户编码 |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  | 数据创建时间 | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  | 数据更新人 | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  | 数据更新时间 | 记录最新更新时间 |
| effectiveFrom | datetime | YES | - |  | 数据有效开始时间 | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  | 数据有效结束时间 | 数据有效性结束时间，NULL=当前有效 |
| source | varchar(25) | NO | SMS |  | 数据来源 | 数据来源标识 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| instId | BTREE | NON-UNIQUE | instId |
| lendInfoId | BTREE | NON-UNIQUE | lendInfoId |
| PRIMARY | BTREE | UNIQUE | presalesId |
| projectCode | BTREE | NON-UNIQUE | projectCode |

---

### 3.2 pm_presales_project_product_line -- 售前项目产品明细（数据量最大的表）

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 售前项目产品明细（数据量最大的表） |
| 数据量 | ~4,358,185 行 |
| 数据大小 | 879.0 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| productLineId | int(11) | NO | - | PRI, auto_increment |  | 产品线自增主键 |
| presalesId | int(11) | YES | - | MUL | 售前项目ID | 逻辑外键 -> pm_presales_project_header.presalesId |
| lendInfoId | varchar(64) | YES | - | MUL | 借货主表主键 | 借货信息ID |
| productFirstName | varchar(255) | YES | - |  | 产品一级 | 产品一级分类名称 |
| productTypeName | varchar(255) | YES | - |  | 产品类别 | 产品类型名称 |
| itemCode | varchar(255) | YES | - |  | item编码 | 产品编码 |
| itemModel | varchar(255) | YES | - |  | item型号 | 产品型号 |
| itemDesc | text | YES | - |  | item描述 | 产品描述 |
| price | double | YES | - |  | 目录价 | 目录价 |
| productNum | int(11) | NO | 0 |  | 产品数量 | 借货产品数量 |
| orderNum | int(11) | NO | 0 |  | 下单数量 | 下单数量 |
| deliverNum | int(11) | NO | 0 |  | 发货数量 | 发货数量 |
| hexiaoNum | int(11) | NO | 0 |  | 核销数量 | 核销数量 |
| transferNum | int(11) | NO | 0 |  | 转销数量 | 借转销数量 |
| remark | text | YES | - |  | 备注 | 备注 |
| effectiveFrom | datetime | YES | - |  | 数据有效开始时间 | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  | 数据有效结束时间 | 数据有效性结束时间，NULL=当前有效 |
| source | varchar(25) | YES | SMS |  |  | 数据来源标识，默认SMS |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| lendInfoId | BTREE | NON-UNIQUE | lendInfoId |
| presalesId | BTREE | NON-UNIQUE | presalesId |
| PRIMARY | BTREE | UNIQUE | productLineId |

---

### 3.3 pm_presales_project_callback -- 售前项目回访记录

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 售前项目回访记录 |
| 数据量 | ~1,865 行 |
| 数据大小 | 160.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 售前回访问卷表 | 自增主键，回访记录唯一标识 |
| presalesId | int(11) | YES | - | MUL | 售前项目ID | 逻辑外键 -> pm_presales_project_header.presalesId |
| taskId | varchar(25) | YES | - | MUL | 任务ID | Activiti任务ID |
| quesnaireId | int(11) | YES | - | MUL | 问卷ID | 逻辑外键 -> pm_cl_quesnaire_result_header.id |
| quesnaireVersion | int(11) | YES | - |  | 问卷版本 | 问卷版本 |
| quesnaireState | int(11) | YES | - |  | 状态 -1 草稿 1已提交 | 0=未填写，1=已填写 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| presalesId | BTREE | NON-UNIQUE | presalesId |
| PRIMARY | BTREE | UNIQUE | id |
| quesnaireId | BTREE | NON-UNIQUE | quesnaireId |
| taskId | BTREE | NON-UNIQUE | taskId |

---

### 3.4 pm_presales_project_duration -- 售前项目各阶段耗时统计

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 售前项目各阶段耗时统计 |
| 数据量 | ~4,320 行 |
| 数据大小 | 416.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| presalesId | int(11) | NO | - | PRI |  | 逻辑外键 -> pm_presales_project_header.presalesId |
| instId | int(11) | YES | - |  | 流程实例ID | 流程实例ID |
| totalDuration | varchar(20) | YES | - |  | 开始时间 | 项目开始到结束的时间间隔 |
| serviceDuration | varchar(20) | YES | - |  | 指派服务经理时间 | 服务经理指派耗时 |
| programDuration | varchar(20) | YES | - |  | 指派项目经理时间 | 项目经理指派耗时 |
| testDuration | varchar(20) | YES | - |  | 测试开始时间 | 测试跟踪耗时 |
| callbackDuration | varchar(20) | YES | - |  | 回访开始时间 | 回访耗时 |
| serviceApproveDuration | varchar(100) | YES | - |  | 服务经理审批时间 | 服务经理审批耗时 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | presalesId |

---

### 3.5 pm_presales_project_rma_info -- 售前项目RMA（退货授权）信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 售前项目RMA（退货授权）信息 |
| 数据量 | ~62,906 行 |
| 数据大小 | 14.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| orderNumber | varchar(11) | YES | - |  |  | SMS订单号 |
| ppliCode | varchar(25) | YES | - |  |  | 产品线编码 |
| orderType | varchar(10) | YES | - |  |  | 订单类型 |
| contract | varchar(25) | YES | - | MUL |  | 合同编号 |
| itemcode | varchar(10) | YES | - | MUL |  | 产品编码 |
| itemModel | varchar(255) | YES | - |  |  | 产品型号 |
| description | varchar(255) | YES | - |  |  | 产品描述 |
| productfirstName | varchar(255) | YES | - |  |  | 产品一级分类名称 |
| productName | varchar(255) | YES | - |  |  | 产品名称 |
| orderQty | decimal(32,0) | YES | - |  |  | 订单数量 |
| dlvQty | decimal(32,0) | YES | - |  |  | 已发货数量 |
| rmaQty | decimal(32,0) | YES | - |  |  | RMA退货数量 |
| createDate | date | YES | - |  |  | 订单创建日期 |
| canceled | char(1) | YES | - |  |  | 是否取消，Y=已取消，N=未取消 |
| deliveryDate | date | YES | - |  |  | 发货日期 |
| rmaDate | date | YES | - |  |  | RMA退货日期 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| contract | BTREE | NON-UNIQUE | contract |
| itemcode | BTREE | NON-UNIQUE | itemcode |

---

## 四、转包管理域 (pm_subcontract)

### 4.1 pm_subcontract_project_header -- 转包项目主表

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 转包项目主表，记录转包项目基本信息 |
| 数据量 | ~3,220 行 |
| 数据大小 | 1.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，转包项目唯一标识 |
| subcontractName | varchar(512) | YES |  |  | 转包名称 | 转包项目名称 |
| subcontractNo | varchar(64) | YES |  | MUL | 转包合同号 | 转包合同编号 |
| contractNos | varchar(2048) | YES |  |  | 项目合同号 | 关联合同编号（多个逗号分隔） |
| projectIds | varchar(1024) | YES |  |  | 转包的项目ID | 逻辑外键 -> pm_project.projectId |
| type | int(11) | YES | - |  | 转包类型 | 转包类型编码 |
| state | int(11) | NO | 0 |  | 转包状态 | 转包项目状态 |
| callbackState | int(11) | YES | - |  | 回访状态 | 回访状态 |
| facilitatorId | int(11) | YES | - | MUL | 服务商表ID | 逻辑外键 -> pm_subcontract_facilitator.id |
| facilitatorName | varchar(64) | YES |  |  | 服务商名 | 服务商名称 |
| bankInfo | varchar(255) | YES |  |  | 服务商开户地址 | 服务商开户行信息 |
| bankAccount | varchar(64) | YES |  |  | 服务商收款账户 | 服务商收款银行账号 |
| officeCode | varchar(25) | YES |  | MUL | 办事处部门 | 逻辑外键 -> fnd_department.departmentNum |
| profitDepCode | varchar(25) | YES |  | MUL | 收益部门 | 收益部门编码 |
| isAccrued | bit(1) | YES | - |  | 是否计提 | 0=未计提，1=已计提 |
| isInvoiced | bit(1) | YES | - |  | 是否提供发票 | 0=未提供发票，1=已提供发票 |
| subcontractAmount | varchar(25) | YES |  |  | 转包价 | 转包合同金额 |
| reason | varchar(512) | YES |  |  | 转包原因 | 转包原因说明 |
| remark | varchar(512) | YES |  |  | 备注 | 备注 |
| effectiveFrom | datetime | YES | - |  | 有效开始时间 | 数据有效性开始时间 |
| effectiveTo | datetime | YES | - |  | 有效结束时间 | 数据有效性结束时间 |
| zrApproveTime | datetime | YES | - |  | 最新主任审批通过时间 | 最新主任审批时间 |
| createBy | varchar(25) | YES |  |  |  | 记录创建用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES |  |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| orgId | int(2) | YES | 1 |  | 所属公司 | 组织ID |
| customInfo | json | YES | - |  | 自定义信息 | 使用JSON_MERGE_PATCH增量更新 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| facilitatorId | BTREE | NON-UNIQUE | facilitatorId |
| officeCode | BTREE | NON-UNIQUE | officeCode |
| PRIMARY | BTREE | UNIQUE | id |
| profitDepCode | BTREE | NON-UNIQUE | profitDepCode |
| subcontractNo | BTREE | NON-UNIQUE | subcontractNo |

---

### 4.2 pm_subcontract_project_line -- 转包项目明细行

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 转包项目明细行 |
| 数据量 | ~51,088 行 |
| 数据大小 | 8.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，转包明细唯一标识 |
| subcontractId | int(11) | NO | - | MUL | 转包项目Id | 逻辑外键 -> pm_subcontract_project_header.id |
| projectId | int(11) | YES | - | MUL | 原项目Id | 原项目Id |
| barcode | varchar(25) | YES | - | MUL | 设备序列号 | 设备序列号 |
| itemCode | varchar(25) | YES | - | MUL | 设备编码 | 产品编码 |
| itemModel | varchar(255) | YES | - |  | 设备型号 | 产品型号 |
| itemName | varchar(255) | YES | - |  | 设备名称 | 产品名称 |
| contractNo | varchar(50) | YES | - | MUL | 合同号 | 合同号 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| barcode | BTREE | NON-UNIQUE | barcode |
| contractNo | BTREE | NON-UNIQUE | contractNo |
| itemCode | BTREE | NON-UNIQUE | itemCode |
| PRIMARY | BTREE | UNIQUE | id |
| projectId | BTREE | NON-UNIQUE | projectId |
| unique_index | BTREE | UNIQUE | subcontractId, barcode |

---

### 4.3 pm_subcontract_facilitator -- 转包服务商信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 转包服务商信息 |
| 数据量 | ~174 行 |
| 数据大小 | 80.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，服务商唯一标识 |
| name | varchar(64) | YES | - |  | 服务商名 | 服务商名 |
| code | varchar(64) | YES | - |  | 服务商编号 | 服务商编号 |
| account | varchar(64) | YES | - |  | 服务商账户 | 服务商账户 |
| bankInfo | varchar(255) | YES | - |  | 开户行信息 | 开户行信息 |
| bankAccount | varchar(64) | YES | - |  | 收款账户 | 银行账号 |
| receiver | varchar(64) | YES | - |  | 邮箱收件人 | 邮箱收件人 |
| cnapsCode | varchar(64) | YES | - |  | 联行号 | 联行号 |
| contacts | varchar(64) | YES | - |  | 联系人 | 联系人 |
| tel | varchar(64) | YES | - |  | 联系电话 | 联系电话 |
| email | varchar(64) | YES | - |  | 邮箱账号 | 邮箱账号 |
| state | bit(1) | YES | b'1' |  | 状态 | 状态 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |
| createBy | varchar(45) | YES | - |  |  | 记录创建用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(45) | YES | - |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| relateType | varchar(45) | YES | - |  | 关联类型 | 关联类型 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段，存储动态属性 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 4.4 pm_subcontract_project_payment -- 转包项目付款记录

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 转包项目付款记录 |
| 数据量 | ~3,351 行 |
| 数据大小 | 432.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，付款记录唯一标识 |
| subcontractId | int(11) | NO | - | MUL | 转包项目Id | 逻辑外键 -> pm_subcontract_project_header.id |
| ratio | varchar(10) | YES | - |  | 比例 | 比例 |
| amount | varchar(25) | YES | - |  | 付款金额 | 付款金额 |
| confirmTime | datetime | YES | - |  | 提交时间 | 提交时间 |
| paymentTime | datetime | YES | - |  | 付款时间 | 付款时间 |
| remark | varchar(512) | YES | - |  | 备注 | 备注 |
| sseId | bigint(20) | YES | -1 |  | sse报销单审批行ID,0：会进行匹配跟新 | sse报销单审批行ID,0：会进行匹配跟新 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段，存储动态属性 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| subcontractId | BTREE | NON-UNIQUE | subcontractId |

---

### 4.5 pm_subcontract_deliver_files -- 转包项目交付件文件

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 转包项目交付件文件 |
| 数据量 | ~3,823 行 |
| 数据大小 | 1.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，交付件唯一标识 |
| subcontractId | int(11) | YES | - | MUL | 转包项目ID | 逻辑外键 -> pm_subcontract_project_header.id |
| paymentId | int(11) | YES | - |  | 转包付款ID | 逻辑外键 -> pm_subcontract_project_payment.id |
| fileName | varchar(255) | YES | - |  | 交付件名称 | 交付件文件名 |
| filePath | varchar(255) | YES | - |  | 交付件路径 | 交付件文件存储路径 |
| type | varchar(45) | YES | - |  | 交付件类型,0:用服交付合同，1：用服服务单，2：工程合同 | 0=用服交付合同，1=用服服务单，2=工程合同 |
| uploadBy | varchar(45) | YES | - |  | 上传者 | 文件上传人编码 |
| uploadTime | datetime | YES | - |  | 上传时间 | 文件上传时间 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段，存储动态属性 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| subcontractId | BTREE | NON-UNIQUE | subcontractId |

---

## 五、问题管理域 (prob)

### 5.1 prob_main -- 技术公告/问题主表

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 技术公告/问题主表，使用bitMark位运算进行多值筛选 |
| 数据量 | ~1,080 行 |
| 数据大小 | 3.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | Java Bean中映射为probId |
| probNum | varchar(25) | YES | - | MUL | 编码 | 技术公告编号 |
| watch | varchar(10) | YES | - |  | 跟踪 | 逻辑外键 -> fnd_basic_data(dataTypeCode=30) |
| theme | varchar(255) | YES | - |  | 主题 | 技术公告主题 |
| desc | text | YES | - |  | 问题描述 | 问题描述详细内容 |
| solution | text | YES | - |  | 解决方案 | 问题解决方案 |
| status | varchar(10) | YES | - |  | 状态 | 逻辑外键 -> fnd_basic_data(dataTypeCode=31) |
| startdate | date | YES | - |  | 开始日期 | 问题发现/开始日期 |
| duedate | date | YES | - |  | 计划完成日期 | 问题计划完成日期 |
| attachments | varchar(255) | YES | - |  | 文件 | 附件路径 |
| priority | varchar(10) | YES | - |  | 严重级别 | 逻辑外键 -> fnd_basic_data(dataTypeCode=32) |
| productType | text | YES | - |  | 产品类型 | 产品类型 |
| trackingUser | varchar(10) | YES | - |  | 跟踪用户 | 逻辑外键 -> fnd_user_info.username |
| visibleRange | int(1) | NO | 0 |  | 可见范围，0:All, 1:对内 | 可见范围设置 |
| createBy | varchar(15) | YES | - |  |  | 记录创建用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |
| remark | text | YES | - |  | 审批意见 | 备注 |
| customInfo | json | YES | - |  | 自定义信息 | 存储relatedSceneTypes等动态属性 |
| probTicketNo | varchar(255) | YES | - |  | 网上问题单号 | 关联工单编号 |
| relatedSceneTypes | varchar(255) | YES | - |  | relatedSceneTypes | 逗号分隔的多值 |
| relatedSceneTypesMark | bigint(20) | YES | - |  | relatedSceneTypes的bitmark | 位运算标记，用于高效筛选 |
| mitigationActionTypes | varchar(255) | YES | - |  | mitigationActionTypes | 规避方案操作类型（逗号分隔） |
| mitigationActionTypesMark | bigint(20) | YES | - |  | mitigationActionTypes的bitmark | 规避方案操作类型位运算标记 |
| solutionActionTypes | varchar(255) | YES | - |  | solutionActionTypes | 解决方案操作类型（逗号分隔） |
| solutionActionTypesMark | bigint(20) | YES | - |  | solutionActionTypes的bitmark | 解决方案操作类型位运算标记 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| probNum_IDX | BTREE | NON-UNIQUE | probNum, id |

---

### 5.2 prob_product -- 技术公告关联的产品信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 技术公告关联的产品信息 |
| 数据量 | ~31,823 行 |
| 数据大小 | 4.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，产品关联唯一标识 |
| probId | int(11) | YES | 0 | MUL | ProbId | 逻辑外键 -> prob_main.id |
| productCode | varchar(255) | YES |  |  | 产品大类 | 产品大类 |
| productSubCode | varchar(255) | YES |  |  | 产品小类 | 产品小类 |
| itemCode | varchar(255) | NO |  |  | item编码 | item编码 |
| itemModel | varchar(255) | YES | - |  | item类型 | item类型 |
| itemDesc | varchar(255) | YES | - |  | item描述 | item描述 |
| status | int(11) | YES | 1 |  | 0 失效 1 有效 | 0 失效 1 有效 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| createTime | datetime | YES | CURRENT_TIMESTAMP |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - | on update CURRENT_TIMESTAMP |  | 记录最新更新时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| probId_Status_IDX | BTREE | NON-UNIQUE | probId, status |
| probId_status_item_IDX | BTREE | NON-UNIQUE | probId, status, itemCode |

---

### 5.3 prob_softwares -- 技术公告关联的软件版本信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 技术公告关联的软件版本信息，用于版本范围匹配 |
| 数据量 | ~11,456 行 |
| 数据大小 | 11.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 已知问题影响的软件版本表 | 自增主键，软件版本唯一标识 |
| probId | int(11) | YES | 0 | MUL | 问题ID | 逻辑外键 -> prob_main.id |
| conp | varchar(100) | YES | - | MUL |  | 受影响主控版本号 |
| cpld | varchar(100) | YES | - | MUL |  | 受影响CPLD版本号 |
| boot | varchar(100) | YES | - | MUL |  | 受影响Boot版本号 |
| pcb | varchar(100) | YES | - | MUL |  | 受影响PCB版本号 |
| manualEntry | varchar(2048) | YES | - |  | 手工录入 | 手动录入的版本号 |
| manualEntrySub | varchar(2048) | YES | - |  | 手工录入拆解 | 手工录入拆解 |
| entryType | varchar(100) | YES | - |  | 版本类型 | 版本类型 |
| entrySeries | varchar(100) | YES | - |  | 版本系列 | 版本系列 |
| entryStart | varchar(255) | YES | - |  | 版本范围开始 | 版本范围开始 |
| entryEnd | varchar(255) | YES | - |  | 版本范围结束 | 版本范围结束 |
| markStart | varchar(255) | YES | - |  | 缺省补充版本范围开始 | 版本范围起始标记 |
| markEnd | varchar(255) | YES | - |  | 缺省补充版本范围结束 | 版本范围结束标记 |
| affectedType | int(11) | YES | 0 | MUL | 影响类型，0：所有系列，1：盒式系列，2：框式系列 | 1=盒式，2=框式 |
| groupId | bigint(11) | YES | 0 |  | 分组ID | 分组ID |
| splited | int(11) | YES | 0 |  | 是否拆解 | 是否拆解 |
| datastate | int(11) | YES | 1 | MUL | 0 失效 1 有效 | 0=失效，1=有效 |
| createBy | varchar(10) | YES | - |  |  | 记录创建用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(10) | YES | - |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段，存储动态属性 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| affectedType | BTREE | NON-UNIQUE | affectedType |
| boot | BTREE | NON-UNIQUE | boot |
| conp | BTREE | NON-UNIQUE | conp |
| cpld | BTREE | NON-UNIQUE | cpld |
| datastate_entry_probId_IDX | BTREE | NON-UNIQUE | datastate, entryType, entrySeries, probId |
| pcb | BTREE | NON-UNIQUE | pcb |
| PRIMARY | BTREE | UNIQUE | id |
| probId_datastate_IDX | BTREE | NON-UNIQUE | probId, datastate |

---

### 5.4 prob_restore -- 技术公告恢复/修复方案记录

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 技术公告恢复/修复方案记录 |
| 数据量 | ~1,269 行 |
| 数据大小 | 288.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 问题修复数据对象 | 自增主键，恢复记录唯一标识 |
| probId | int(11) | YES | 0 | MUL | 涉及到的问题ID | 逻辑外键 -> prob_main.id |
| serialNum | varchar(50) | YES | - | MUL | 序列号 | 序列号 |
| itemModel | varchar(50) | YES | - | MUL | 设备类型 | 设备类型 |
| processId | int(11) | YES | 0 | MUL | 记录任务流程过程中的相关信息 | 记录任务流程过程中的相关信息 |
| officeCode | varchar(25) | YES | - |  | 办事处编码 | 办事处编码 |
| conp | varchar(255) | YES | - |  | 任务发布时的软件版本 | 任务发布时的软件版本 |
| boot | varchar(100) | YES | - |  |  | BOOT引导程序版本号 |
| cpld | varchar(100) | YES | - |  |  | CPLD固件版本号 |
| pcb | varchar(100) | YES | - |  |  | PCB电路板版本号 |
| projectId | int(11) | YES | 0 | MUL | 涉及到的项目ID | 涉及到的项目ID |
| projectName | varchar(255) | YES | - |  | 项目名称 | 项目名称 |
| contractNo | varchar(255) | YES | - |  | 合同号 | 合同号 |
| assignee | varchar(25) | YES | - |  | 办理用户 | 办理用户 |
| assigneeRole | int(11) | YES | 0 |  | 办理角色 | 办理角色 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| itemModel | BTREE | NON-UNIQUE | itemModel |
| PRIMARY | BTREE | UNIQUE | id |
| probId_serialNum_IDX | BTREE | NON-UNIQUE | probId, serialNum |
| processId | BTREE | NON-UNIQUE | processId |
| projectId | BTREE | NON-UNIQUE | projectId |
| serialNum | BTREE | NON-UNIQUE | serialNum |

---

### 5.5 prob_read_log -- 技术公告阅读记录

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 技术公告阅读记录 |
| 数据量 | ~43,284 行 |
| 数据大小 | 2.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，阅读记录唯一标识 |
| probId | int(11) | NO | - |  |  | 逻辑外键 -> prob_main.id |
| reader | varchar(25) | NO |  |  | 查阅人 | 阅读用户编码 |
| readTime | datetime | NO | - |  | 查阅时间 | 阅读时间 |
| status | int(1) | NO | 0 |  | 是否已经确认查阅 | 是否已经确认查阅 |
| firstTime | datetime | YES | - |  | 第一次查阅时间 | 第一次查阅时间 |
| commitTime | datetime | YES | - |  | 确认时间 | 确认时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

## 六、基础平台域 (fnd)

### 6.1 fnd_act_hi_comment -- Activiti工作流审批意见记录

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | Activiti工作流审批意见记录，被各业务模块共用 |
| 数据量 | ~36,824 行 |
| 数据大小 | 4.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | activity 审批意见表 | 自增主键，审批意见唯一标识 |
| objId | int(11) | YES | - | MUL | 业务ID | 关联各业务表的主键 |
| procdefKey | varchar(50) | YES | - |  | 流程类型 | 如CallBack、Presales等 |
| taskKey | varchar(50) | YES | - |  | 任务Key | Activiti任务定义Key |
| taskId | varchar(25) | YES | - | MUL | activity任务ID | Activiti任务ID |
| instId | varchar(25) | YES | - | MUL | 流程ID | Activiti流程实例ID |
| assignee | varchar(25) | YES | - | MUL | 办理人 | 逻辑外键 -> fnd_user_info.username |
| assigneeTime | datetime | YES | - |  | 办理时间 | 任务办理时间 |
| nextAssignee | varchar(25) | YES | - |  | 下一步办理人 | 下一步办理人 |
| nextAssigneeName | varchar(64) | YES | - |  | 下一步办理人姓名 | 下一步办理人姓名 |
| result | int(11) | YES | - |  | 审批结果 | 逻辑外键 -> fnd_basic_data(dataTypeCode=26) |
| message | text | YES | - |  | 审批意见 | 审批意见内容 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| assignee | BTREE | NON-UNIQUE | assignee, procdefKey |
| instId | BTREE | NON-UNIQUE | instId |
| objId | BTREE | NON-UNIQUE | objId, procdefKey |
| PRIMARY | BTREE | UNIQUE | id |
| taskId | BTREE | NON-UNIQUE | taskId |

---

### 6.2 fnd_basic_data -- 系统基础数据字典

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 系统基础数据字典，通过dataTypeCode区分不同数据类型 |
| 数据量 | ~480 行 |
| 数据大小 | 64.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，基础数据唯一标识 |
| dataTypeCode | varchar(45) | YES | - | MUL |  | 逻辑外键 -> fnd_basic_data_type.dataTypeCode |
| basicDataId | varchar(255) | YES | - | MUL |  | 数据项编码 |
| basicDataName | varchar(255) | YES | - |  |  | 数据项名称 |
| basicDataAttri1 | varchar(255) | YES | - |  | 字段属性1 | 字段属性1 |
| sortId | int(11) | YES | - |  | 查询排序字段数值越大越在前 | 查询排序字段数值越大越在前 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(45) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(45) | YES | - |  |  | 记录最新更新用户编码 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| basicDataId | BTREE | NON-UNIQUE | basicDataId |
| basicDataId_dataTypeCode | BTREE | NON-UNIQUE | dataTypeCode, basicDataId |
| PRIMARY | BTREE | UNIQUE | id |

---

### 6.3 fnd_basic_data_type -- 基础数据类型定义

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 基础数据类型定义 |
| 数据量 | ~30 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，数据类型唯一标识 |
| dataTypeCode | varchar(45) | YES | - |  |  | 数据类型唯一编码 |
| dataTypeName | varchar(45) | YES | - |  |  | 数据类型名称 |
| status | int(11) | YES | - |  | 是否需要放在前台管理 | 是否需要放在前台管理 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(45) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(45) | YES | - |  |  | 记录最新更新用户编码 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 6.4 fnd_company -- 公司/组织机构信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 公司/组织机构信息 |
| 数据量 | ~3 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，公司唯一标识 |
| pid | int(11) | NO | - | MUL | 父组织机构ID | 父组织机构ID |
| name | varchar(128) | NO | - |  | 组织机构全名 | 组织机构全名 |
| abbr | varchar(64) | NO | - |  | 组织机构简写 | 组织机构简写 |
| website | varchar(128) | YES | - |  | 组织机构网址 | 组织机构网址 |
| code | varchar(25) | YES | 0 | MUL | 组织机构代码 | 组织机构代码 |
| account | varchar(25) | YES |  |  | 组织机构账套 | 组织机构账套 |
| status | smallint(1) | NO | 1 |  | 有效性（1-有效，0-失效），默认有效 | 有效性（1-有效，0-失效），默认有效 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(32) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(32) | YES | - |  |  | 记录最新更新用户编码 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| code | BTREE | NON-UNIQUE | code |
| pid | BTREE | NON-UNIQUE | pid |
| PRIMARY | BTREE | UNIQUE | id |

---

### 6.5 fnd_department -- 部门/办事处信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 部门/办事处信息 |
| 数据量 | ~137 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，部门唯一标识 |
| departmentNum | varchar(20) | NO | - | UNI |  | 部门编码，全局唯一 |
| departmentName | varchar(20) | NO | - |  |  | 部门名称 |
| isparam | int(11) | YES | 0 |  |  | 是否为参数部门，1=参数部门（如办事处/市场部），0=非参数部门 |
| status | int(11) | NO | 1 |  |  | 部门状态，1=启用，0=禁用 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| deparmentNum | BTREE | UNIQUE | departmentNum |
| PRIMARY | BTREE | UNIQUE | id |

---

### 6.6 fnd_user_info -- 系统用户信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 系统用户信息 |
| 数据量 | ~459 行 |
| 数据大小 | 112.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(8) | NO | - | PRI, auto_increment |  | 自增主键，用户唯一标识 |
| username | varchar(128) | NO | - | MUL |  | 用户登录名，全局唯一 |
| password | varchar(32) | NO | 5416d7cd6ef195a0f7622a9c56b55e84 |  |  | 加密后的用户密码 |
| email | varchar(128) | NO | - |  |  | 用户邮箱 |
| dpNo | varchar(25) | YES | - |  |  | 用户所属部门编码，逻辑外键 -> fnd_department.departmentNum |
| realName | varchar(128) | NO | - |  |  | 用户真实姓名 |
| roleIds | varchar(64) | YES | - |  | 用户角色，支持多角色 | 用户角色，支持多角色 |
| isemail | int(11) | YES | - |  |  | 是否接收邮件通知，1=接收，0=不接收 |
| status | int(1) | YES | - |  |  | 用户状态，1=启用，0=禁用 |
| defaultPage | varchar(255) | YES | - |  | 该用户登录首页 | 该用户登录首页 |
| pwdoverdue | datetime | YES | - |  |  | 密码过期时间，超过此时间需强制修改密码 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |
| customInfo | json | YES | - |  | 自定义信息 | JSON扩展字段，存储动态属性 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| username | BTREE | NON-UNIQUE | username |

---

### 6.7 fnd_files -- 文件上传记录

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 文件上传记录 |
| 数据量 | ~9,096 行 |
| 数据大小 | 2.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 系统上传文件信息 | 自增主键，文件唯一标识 |
| fileName | varchar(255) | YES | - |  | 文件名称 | 上传文件原始名 |
| filePath | varchar(255) | YES | - |  | 文件路径 | 文件服务器存储路径 |
| fileType | varchar(255) | YES | - |  | 文件分类 | 文件MIME类型 |
| uploadBy | varchar(25) | YES | - |  | 上传用户 | 上传用户编码 |
| uploadTime | datetime | YES | - |  | 上传时间 | 文件上传时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 6.8 fnd_roles -- 系统角色定义

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 系统角色定义 |
| 数据量 | ~16 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(6) | NO | - | PRI, auto_increment |  | 自增主键，角色唯一标识 |
| roleName | varchar(64) | NO | - | UNI |  | 角色名称 |
| defaultPage | varchar(255) | YES | - |  | 该角色登录的默认首页 | 该角色登录的默认首页 |
| status | int(1) | NO | - |  |  | 角色状态，1=启用，0=禁用 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| roleRemark | varchar(200) | YES | - |  |  | 角色备注说明 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |
| roleName | BTREE | UNIQUE | roleName |

---

### 6.9 fnd_user_power -- 用户权限配置

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 用户权限配置 |
| 数据量 | ~442 行 |
| 数据大小 | 64.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，权限记录唯一标识 |
| fndUserId | int(11) | YES | - |  |  | 关联用户ID，逻辑外键 -> fnd_user_info.id |
| username | varchar(25) | YES | - |  |  | 逻辑外键 -> fnd_user_info.username |
| areapower | varchar(4096) | YES | - |  |  | 用户数据权限区域编码，逗号分隔的部门编码列表，-1=全部权限 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 6.10 fnd_mails -- 系统发送的邮件记录

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 系统发送的邮件记录 |
| 数据量 | ~146,157 行 |
| 数据大小 | 440.8 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，邮件记录唯一标识 |
| mailSubject | varchar(255) | NO | - |  | 邮件主题 | 邮件主题 |
| mailContent | longtext | NO | - |  | 邮件正文 | 邮件正文HTML内容 |
| mailTos | text | YES | - |  | 邮件主送 | 邮件主送 |
| mailCcs | text | YES | - |  | 邮件抄送 | 邮件抄送 |
| mailBcc | text | YES | - |  | 邮件密送 | 邮件密送 |
| mailAttachFiles | text | YES | - |  | 邮件附件 以特殊符号间隔多个文件 | 邮件附件 以特殊符号间隔多个文件 |
| mailSendTime | datetime | YES | - |  | 邮件实际发送时间 | 邮件实际发送时间 |
| mailExpectSendTime | datetime | YES | - |  | 邮件期望发送时间 | 邮件期望发送时间 |
| mailServerPort | varchar(25) | YES | - |  |  | 邮件服务器端口 |
| mailServerHost | varchar(25) | YES | - |  |  | 邮件服务器地址 |
| mailUsername | varchar(25) | YES | - |  |  | 邮件服务器登录用户名 |
| mailPassword | varchar(25) | YES | - |  |  | 邮件服务器登录密码 |
| mailFromaddress | varchar(25) | YES | - |  |  | 邮件发件人地址 |
| sendFlag | int(11) | YES | 0 |  | 邮件是否发送 1 为已发送 | 邮件是否发送 1 为已发送 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| updatteTime | datetime | YES | - |  |  | 记录更新时间（字段名拼写错误，应为updateTime） |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 6.11 fnd_data_refresh_log -- 外部系统数据同步刷新日志

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 外部系统数据同步刷新日志 |
| 数据量 | ~16,540 行 |
| 数据大小 | 3.5 MB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，刷新日志唯一标识 |
| refreshTaskName | varchar(100) | YES | - |  |  | 刷新任务名称（Java类全限定名） |
| handleUser | varchar(15) | YES | - |  |  | 执行刷新操作的用户，通常为system |
| dataFrom | varchar(25) | YES | - |  |  | 数据来源系统标识（如SMS/CRM/Local） |
| dataTo | varchar(25) | YES | - |  |  | 数据目标系统标识 |
| refreshFrom | datetime | YES | - |  | 刷新开始时间 | 刷新开始时间 |
| refreshTo | datetime | YES | - |  | 结束时间 | 结束时间 |
| refreshState | int(11) | YES | 0 |  | 刷新成功或失败 0失败 1 成功 | 0=失败，1=成功 |
| refreshException | mediumtext | YES | - |  |  | 刷新异常堆栈信息，失败时记录完整异常 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 6.12 fnd_sys_arg -- 系统参数配置

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 系统参数配置 |
| 数据量 | ~45 行 |
| 数据大小 | 80.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 系统变量 | 自增主键，参数唯一标识 |
| code | varchar(64) | YES | - |  |  | 系统参数编码（唯一标识，如sys.envirment.argu） |
| var | text | YES | - |  |  | 系统参数值（支持字符串/JSON/数字等多种格式） |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 6.13 fnd_menus -- 系统菜单定义

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 系统菜单定义 |
| 数据量 | ~22 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，菜单唯一标识 |
| menuCode | varchar(50) | YES | - |  | 菜单编码 | 菜单编码 |
| menuName | varchar(25) | YES | - |  | 菜单名称 | 菜单名称 |
| menuLevel | int(1) | YES | - |  | 菜单级别 | 菜单级别 |
| superId | int(11) | YES | - |  | 父菜单ID | 父菜单ID |
| path | varchar(200) | YES | - |  | 访问路径 | 访问路径 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |
| createBy | varchar(25) | YES | - |  |  | 记录创建用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 6.14 fnd_role_menus -- 角色与菜单的关联关系

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 角色与菜单的关联关系 |
| 数据量 | ~58 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，角色菜单关联唯一标识 |
| roleId | int(11) | NO | - |  |  | 关联角色ID，逻辑外键 -> fnd_roles.id |
| menuId | int(11) | NO | - |  |  | 关联菜单ID |
| menuPower | varchar(20) | NO | - |  | 各菜单增删改权限 | 各菜单增删改权限 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 6.15 fnd_user_menus -- 用户与菜单的关联关系

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 用户与菜单的关联关系 |
| 数据量 | ~3,044 行 |
| 数据大小 | 272.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，用户菜单关联唯一标识 |
| fnd_user_id | int(11) | YES | - |  |  | 关联用户ID，逻辑外键 -> fnd_user_info.id |
| username | varchar(128) | YES | - |  |  | 逻辑外键 -> fnd_user_info.username |
| menuCode | varchar(50) | YES | - |  |  | 逻辑外键 -> fnd_menus.menuCode |
| menuValue | int(1) | YES | - |  |  | 菜单权限值，1=有权限 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |
| createdBy | varchar(25) | YES | - |  |  | 记录创建人用户编码 |
| createTime | datetime | YES | - |  |  | 记录创建时间 |
| updateBy | varchar(25) | YES | - |  |  | 记录最新更新用户编码 |
| updateTime | datetime | YES | - |  |  | 记录最新更新时间 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

### 6.16 fnd_basic_prjstate -- 项目状态基础配置

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目状态基础配置 |
| 数据量 | ~40 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment |  | 自增主键，项目状态唯一标识 |
| dataTypeCode | varchar(45) | YES | - | MUL | 数据类型编码，对应fnd_basic_data | 数据类型编码，对应fnd_basic_data |
| basicDataId | varchar(11) | YES | - |  | 基础数据ID，对应fnd_basic_data | 基础数据ID，对应fnd_basic_data |
| column010 | varchar(10) | YES | - |  | 项目类型，对应pm_project_header | 项目类型，对应pm_project_header |
| column011 | varchar(10) | YES | - |  | 项目类别，对应pm_project_header | 项目类别，对应pm_project_header |
| createTime | datetime | YES | - |  | 记录数据创建时间 | 记录创建时间 |
| createBy | varchar(45) | YES | - |  | 记录数据创建用户 | 记录创建用户编码 |
| updateTime | datetime | YES | - |  | 记录数据最新更新时间 | 记录最新更新时间 |
| updateBy | varchar(45) | YES | - |  | 记录数据最新更新用户 | 记录最新更新用户编码 |
| effectiveFrom | datetime | YES | - |  | 数据有效性开始时间 | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  | 数据有效性结束时间 | 数据有效性结束时间，NULL=当前有效 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| dataTypeCode | BTREE | NON-UNIQUE | dataTypeCode, basicDataId |
| PRIMARY | BTREE | UNIQUE | id |

---

### 6.17 fnd_spms_arg -- SPMS系统参数配置

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | SPMS系统参数配置 |
| 数据量 | ~5 行 |
| 数据大小 | 16.0 KB |

**字段列表**

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |
|--------|----------|------|--------|------|----------|----------|
| id | int(11) | NO | - | PRI, auto_increment | 备件系统一些特殊参数控制 如邮件等 | 自增主键，SPMS参数唯一标识 |
| code | varchar(25) | YES | - |  |  | SPMS系统参数编码（唯一标识） |
| var | text | YES | - |  |  | SPMS系统参数值（支持字符串/JSON等格式） |
| mark | varchar(255) | YES | - |  |  | SPMS系统参数说明/备注 |
| effectiveFrom | datetime | YES | - |  |  | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | YES | - |  |  | 数据有效性结束时间，NULL=当前有效 |

**索引列表**

| 索引名 | 索引类型 | 唯一性 | 索引字段 |
|--------|----------|--------|----------|
| PRIMARY | BTREE | UNIQUE | id |

---

## 附录A: 数据量TOP 10表

| 排名 | 表名 | 行数 | 数据大小 |
|------|------|------|----------|
| 1 | pm_presales_project_product_line | 4,358,185 | 879.0 MB |
| 2 | pm_project_soft_version | 532,125 | 327.8 MB |
| 3 | pm_project_shipment | 460,132 | 117.6 MB |
| 4 | pm_cl_quesnaire_result_line | 454,563 | 28.6 MB |
| 5 | pm_project_member | 302,428 | 32.6 MB |
| 6 | pm_project_product_line | 185,819 | 25.1 MB |
| 7 | pm_project_maintenance | 184,753 | 110.6 MB |
| 8 | pm_project_notification | 152,161 | 13.5 MB |
| 9 | fnd_mails | 146,157 | 440.8 MB |
| 10 | pm_project_related_party | 126,864 | 12.5 MB |

---

## 附录B: customInfo JSON字段常用Key

以下Key存储在各表的customInfo JSON字段中：

| 表名 | Key | 含义 |
|------|-----|------|
| pm_project | serviceManagerCode | 服务经理编码 |
| pm_project | programManagerCode | 项目经理编码 |
| pm_project | programManagerCodeB | 第二项目经理编码 |
| pm_project | smsProjectAmount | SMS项目金额 |
| pm_project | salesManCode | 销售人员编码 |
| pm_subcontract_project_header | parentOfficeCode | 上级办事处编码 |
| prob_main | relatedSceneTypes | 关联场景类型列表 |
| prob_main | mitigationActionTypes | 规避方案操作类型列表 |
| prob_main | solutionActionTypes | 解决方案操作类型列表 |

---

## 附录C: bitMark位运算模式说明

prob_main表使用bitMark位运算实现多值筛选，原理如下：

- `relatedSceneTypes`: 存储逗号分隔的场景类型值（如"1,2,4"）
- `relatedSceneTypesMark`: 将场景类型值进行位或运算得到的长整型（如 1|2|4 = 7）
- 查询时使用位与运算：`relatedSceneTypesMark & #{mark} > 0` 判断是否包含指定场景

此模式同样适用于 `mitigationActionTypesMark` 和 `solutionActionTypesMark`。

---

## 附录D: customInfo更新策略差异

| 表名 | 更新策略 | SQL模式 |
|------|----------|---------|
| pm_subcontract_project_header | 增量合并 | `JSON_MERGE_PATCH(IFNULL(customInfo, "{}"), #{customInfo:JSON})` |
| pm_project | 直接赋值 | `customInfo = #{customInfo:JSON}` |
| pm_project_maintenance | 直接赋值 | `customInfo = #{customInfo:JSON}` |

注意：增量合并策略只更新传入的Key，不影响其他Key；直接赋值策略会整体替换customInfo内容。

---

---

## 补录：缺失表定义

> 以下13张表在初版文档中遗漏，现根据生产数据库实际结构补录。按业务域归类，编号衔接各域原有编号。

---

### 1.22 pm_project_soft_version_history -- 项目设备软件版本历史记录

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目设备软件版本的历史变更记录表，结构与pm_project_soft_version相同，用于保存版本变更的历史快照，支撑版本回溯和审计追踪 |
| 数据量 | ~1,055,447 行 |
| 数据大小 | 317.8 MB |

**字段列表**

| 字段名 | 数据类型 | 非空 | 默认值 | 字段注释 | 业务含义 |
|--------|----------|------|--------|----------|----------|
| id | int(11) | YES | auto_increment | 主键自增ID | 历史记录唯一标识 |
| projectId | int(11) | NO | - | 项目ID | 逻辑外键 -> pm_project.projectId，关联项目 |
| logId | int(11) | NO | - | 日志ID | 逻辑外键 -> pm_project_log.id，关联操作日志 |
| contractNo | varchar(100) | NO | - | 合同编号 | 关联合同编号 |
| itemCode | varchar(25) | NO | - | 物料编码 | 设备物料编码 |
| barCode | varchar(25) | NO | - | 条码 | 设备唯一条码标识 |
| conp | varchar(100) | NO | - | 主控版本 | 主控（CONP）软件版本号 |
| conpType | varchar(100) | NO | - | 主控类型 | 主控组件类型 |
| conpSeries | varchar(100) | NO | - | 主控系列 | 主控组件系列 |
| conpMark | varchar(255) | NO | - | 主控标记 | 主控版本标记/备注 |
| conpBak | varchar(255) | NO | - | 主控备份版本 | 变更前的主控版本（用于对比） |
| conpChange | int(11) | NO | - | 主控变更标记 | 主控是否变更，0=未变更，1=已变更 |
| cpld | varchar(100) | NO | - | CPLD版本 | CPLD（复杂可编程逻辑器件）版本号 |
| cpldBak | varchar(255) | NO | - | CPLD备份版本 | 变更前的CPLD版本 |
| cpldChange | int(11) | NO | - | CPLD变更标记 | CPLD是否变更 |
| boot | varchar(100) | NO | - | Boot版本 | 引导程序版本号 |
| bootBak | varchar(255) | NO | - | Boot备份版本 | 变更前的Boot版本 |
| bootChange | int(11) | NO | - | Boot变更标记 | Boot是否变更 |
| pcb | varchar(100) | NO | - | PCB版本 | 印刷电路板版本号 |
| pcbBak | varchar(255) | NO | - | PCB备份版本 | 变更前的PCB版本 |
| pcbChange | int(11) | NO | - | PCB变更标记 | PCB是否变更 |
| executeTime | date | NO | - | 执行时间 | 版本变更执行日期 |
| datastate | int(11) | NO | - | 数据状态 | 数据有效性状态，用于逻辑删除和状态标记 |
| createTime | datetime | NO | - | 创建时间 | 记录创建时间 |
| createBy | varchar(25) | NO | - | 创建人 | 记录创建用户 |
| updateTime | datetime | NO | - | 更新时间 | 记录最后更新时间 |
| updateBy | varchar(25) | NO | - | 更新人 | 记录最后更新用户 |
| customInfo | json | NO | - | 自定义信息 | JSON扩展字段 |

**约束信息**

| 约束类型 | 约束名称 | 字段 | 关联表 | 关联字段 | 说明 |
|----------|----------|------|--------|----------|------|
| PRIMARY KEY | PRIMARY | id | - | - | 主键自增 |

**索引列表**

| 索引名 | 列 | 唯一性 | 索引类型 | 用途说明 |
|--------|-----|--------|----------|----------|
| PRIMARY | id | UNIQUE | BTREE | 主键索引 |
| barcode | barCode | NON-UNIQUE | BTREE | 按条码查询历史版本记录 |
| pm_project_soft_version_conp_IDX | conp | NON-UNIQUE | BTREE | 按主控版本号查询 |
| idx_conp_item_query | datastate, conpType, conpSeries, conpMark, itemCode, projectId | NON-UNIQUE | BTREE | 复合索引，支撑按主控类型/系列/标记+物料编码+项目的组合查询 |
| projectBarcodeValid | projectId, barCode, datastate | NON-UNIQUE | BTREE | 复合索引，支撑按项目+条码+有效状态查询 |

---

### 1.23 pm_project_product_line_real -- 项目实际产品线明细

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 项目实际产品线明细表，记录项目下真实的产品明细信息，与pm_project_product_line（订单产品）互补，用于区分订单产品与实际交付产品的差异 |
| 数据量 | ~3,812 行 |
| 数据大小 | 1.5 MB |

**字段列表**

| 字段名 | 数据类型 | 非空 | 默认值 | 字段注释 | 业务含义 |
|--------|----------|------|--------|----------|----------|
| id | int(11) | YES | auto_increment | 主键自增ID | 记录唯一标识 |
| projectId | int(11) | NO | - | 项目ID | 逻辑外键 -> pm_project.projectId |
| contractNo | varchar(25) | NO | '' | 合同编号 | 关联合同编号 |
| projectCode | varchar(255) | NO | '' | 项目编码 | 项目业务编码 |
| orderExecNumber | varchar(255) | NO | '' | 订单执行编号 | ERP订单执行行号 |
| productFirstName | varchar(255) | NO | '' | 产品大类名称 | 产品一级分类名称 |
| productName | varchar(128) | NO | '' | 产品名称 | 产品名称 |
| productSubCode | varchar(255) | NO | '' | 产品子类编码 | 产品子类编码 |
| productSubModel | varchar(255) | NO | '' | 产品子类型号 | 产品子类型号 |
| productSubName | varchar(255) | NO | '' | 产品子类名称 | 产品子类名称 |
| num | int(11) | NO | 0 | 数量 | 产品数量 |
| memo | mediumtext | NO | - | 备注 | 备注信息 |

**约束信息**

| 约束类型 | 约束名称 | 字段 | 关联表 | 关联字段 | 说明 |
|----------|----------|------|--------|----------|------|
| PRIMARY KEY | PRIMARY | id | - | - | 主键自增 |

**索引列表**

| 索引名 | 列 | 唯一性 | 索引类型 | 用途说明 |
|--------|-----|--------|----------|----------|
| PRIMARY | id | UNIQUE | BTREE | 主键索引 |
| projectId | projectId | NON-UNIQUE | BTREE | 按项目ID查询产品明细 |

---

### 1.24 pm_project_maintenance_service_delivery -- 维保服务交付记录

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 维保服务交付记录表，记录每次维保巡检服务的交付情况，包括交付时间、周期、数量等，用于跟踪维保服务的实际交付进度和完成情况 |
| 数据量 | ~66 行 |
| 数据大小 | 16 KB |

**字段列表**

| 字段名 | 数据类型 | 非空 | 默认值 | 字段注释 | 业务含义 |
|--------|----------|------|--------|----------|----------|
| id | int(11) | YES | auto_increment | 主键自增ID | 记录唯一标识 |
| maintenanceId | int(11) | NO | - | 维保ID | 逻辑外键 -> pm_project_maintenance.id，关联维保主记录 |
| projectId | int(11) | NO | - | 项目ID | 逻辑外键 -> pm_project.projectId |
| projectType | varchar(25) | NO | '10' | 项目类型 | 10=用服售后，afss=安服售后，afxx=安服先行 |
| serviceType | varchar(25) | NO | - | 服务类型 | 维保服务类型编码 |
| processTime | datetime | NO | - | 处理时间 | 服务处理时间 |
| year | int(4) | NO | - | 年份 | 交付所属年份 |
| quarter | int(2) | NO | - | 季度 | 交付所属季度(1-4) |
| month | int(2) | NO | - | 月份 | 交付所属月份(1-12) |
| delivered | int(1) | NO | 0 | 是否已交付 | 0=未交付，1=已交付 |
| startDate | date | NO | - | 开始日期 | 服务周期开始日期 |
| endDate | date | NO | - | 结束日期 | 服务周期结束日期 |
| count | int(2) | NO | 0 | 交付次数 | 本周期内交付次数 |
| yearCount | int(2) | NO | 0 | 年度交付次数 | 本年度累计交付次数 |
| remark | varchar(2048) | NO | - | 备注 | 备注说明 |
| createBy | varchar(25) | NO | - | 创建人 | 记录创建用户 |
| createTime | datetime | NO | - | 创建时间 | 记录创建时间 |
| updateBy | varchar(25) | NO | - | 更新人 | 记录最后更新用户 |
| updateTime | datetime | NO | - | 更新时间 | 记录最后更新时间 |

**约束信息**

| 约束类型 | 约束名称 | 字段 | 关联表 | 关联字段 | 说明 |
|----------|----------|------|--------|----------|------|
| PRIMARY KEY | PRIMARY | id | - | - | 主键自增 |

**索引列表**

| 索引名 | 列 | 唯一性 | 索引类型 | 用途说明 |
|--------|-----|--------|----------|----------|
| PRIMARY | id | UNIQUE | BTREE | 主键索引 |
| maintenanceId | maintenanceId | NON-UNIQUE | BTREE | 按维保ID查询交付记录 |
| projectId | projectId, projectType | NON-UNIQUE | BTREE | 复合索引，按项目+项目类型查询 |
| serviceType | serviceType | NON-UNIQUE | BTREE | 按服务类型查询 |

---

### 1.25 pm_project_maintenance_view -- 维保视图缓存表

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 维保视图缓存表，是pm_project_maintenance的宽表物化缓存，冗余了项目、组织、人员、分类等多个维度的名称字段，用于加速维保列表查询，避免多表JOIN。包含中文列名为问卷评分维度字段 |
| 数据量 | ~183,607 行 |
| 数据大小 | 122.7 MB |

**字段列表**

| 字段名 | 数据类型 | 非空 | 默认值 | 字段注释 | 业务含义 |
|--------|----------|------|--------|----------|----------|
| id | int(11) | YES | 0 | 主键ID | 维保记录ID，对应pm_project_maintenance.id |
| projectId | int(11) | NO | - | 项目ID | 逻辑外键 -> pm_project.projectId |
| projectCode | varchar(45) | NO | '' | 项目编码 | 项目业务编码 |
| projectName | varchar(200) | NO | '' | 项目名称 | 项目业务名称 |
| projectType | int(11) | NO | 10 | 项目类型 | 10=用服售后，其他值对应不同类型 |
| projectExecutionState | varchar(45) | NO | '' | 项目执行状态 | 项目当前执行状态编码 |
| contractNo | varchar(255) | NO | '' | 合同编号 | 关联合同编号 |
| officeCode | varchar(25) | NO | - | 办事处编码 | 逻辑外键 -> fnd_department.departmentNum |
| compId | int(2) | NO | 1 | 公司ID | 逻辑外键 -> fnd_company.id |
| type | varchar(45) | NO | - | 维保类型 | 维保服务类型编码 |
| category | varchar(45) | NO | - | 维保分类 | 维保服务分类编码 |
| subCategory | varchar(45) | NO | - | 维保子分类 | 维保服务子分类编码 |
| processTime | datetime | NO | - | 处理时间 | 维保处理时间 |
| processDesc | varchar(1024) | NO | - | 处理描述 | 维保处理过程描述 |
| processStep | varchar(1024) | NO | - | 处理步骤 | 维保处理步骤说明 |
| remainProblem | varchar(1024) | NO | - | 遗留问题 | 维保后仍遗留的问题 |
| transitHour | float | NO | 0 | 在途时长 | 工程师在途耗时（小时） |
| processHour | float | NO | 0 | 处理时长 | 工程师现场处理耗时（小时） |
| itemModel | varchar(255) | NO | - | 设备型号 | 维保设备型号 |
| softVersion | varchar(255) | NO | - | 软件版本 | 维保设备软件版本 |
| enabledFeatures | varchar(255) | NO | - | 已启用特性 | 设备已启用的功能特性 |
| customTos | varchar(512) | NO | - | 客户TOS | 客户技术联系人 |
| customCcs | varchar(512) | NO | - | 客户CCS | 客户抄送联系人 |
| hasReport | bit(1) | YES | b'0' | 是否有报告 | 是否上传了维保报告，0=无，1=有 |
| quesnaireId | int(11) | NO | - | 问卷ID | 关联回访问卷ID |
| deliverFileIds | varchar(255) | NO | '' | 交付文件ID列表 | 交付件文件ID，逗号分隔 |
| warrantyStatus | varchar(25) | NO | - | 维保状态 | 维保状态编码 |
| industryName | varchar(25) | NO | - | 行业名称 | 客户所属行业名称（冗余） |
| userOffice | varchar(25) | NO | - | 用户办事处 | 维保人员所属办事处编码 |
| year | int(4) | NO | - | 年份 | 维保所属年份 |
| quarter | int(1) | NO | - | 季度 | 维保所属季度 |
| month | int(2) | NO | - | 月份 | 维保所属月份 |
| wsCount | int(2) | NO | - | WS次数 | 本周期维保服务次数 |
| wafCount | int(2) | NO | - | WAF次数 | 本周期WAF（保修期外收费）次数 |
| wsYearCount | int(2) | NO | - | WS年度次数 | 年度累计维保服务次数 |
| wafYearCount | int(2) | NO | - | WAF年度次数 | 年度累计WAF次数 |
| warrantyInfo | varchar(4096) | NO | - | 维保信息 | 维保详细信息 |
| serviceInfo | varchar(2048) | NO | - | 服务信息 | 服务详细信息 |
| remark | varchar(2048) | NO | - | 备注 | 备注说明 |
| createTime | datetime | NO | - | 创建时间 | 记录创建时间 |
| createBy | varchar(45) | NO | - | 创建人 | 记录创建用户 |
| updateTime | datetime | NO | - | 更新时间 | 记录最后更新时间 |
| updateBy | varchar(45) | NO | - | 更新人 | 记录最后更新用户 |
| customInfo | json | NO | - | 自定义信息 | JSON扩展字段 |
| officeName | varchar(20) | NO | - | 办事处名称 | 办事处名称（冗余自fnd_department） |
| userOfficeName | varchar(20) | NO | - | 用户办事处名称 | 维保人员所属办事处名称（冗余） |
| serviceManager | varchar(45) | NO | - | 服务经理 | 服务经理姓名（冗余自pm_project） |
| programManagerA | varchar(45) | NO | - | 项目经理A | 第一项目经理姓名（冗余） |
| programManagerB | varchar(45) | NO | - | 项目经理B | 第二项目经理姓名（冗余） |
| createUser | varchar(174) | NO | - | 创建用户名 | 创建人姓名（冗余，含工号+姓名拼接） |
| typeName | varchar(255) | NO | - | 类型名称 | 维保类型中文名称（冗余自fnd_basic_data） |
| projectExecutionStateName | varchar(255) | NO | - | 项目执行状态名称 | 项目执行状态中文名称（冗余） |
| categoryName | varchar(258) | NO | - | 分类名称 | 维保分类中文名称（冗余） |
| subCategoryName | varchar(255) | NO | - | 子分类名称 | 维保子分类中文名称（冗余） |
| marketName | varchar(255) | NO | - | 市场部名称 | 市场部名称（冗余） |
| systemName | varchar(255) | NO | - | 系统部名称 | 系统部名称（冗余） |
| expendName | varchar(255) | NO | - | 拓展部名称 | 拓展部名称（冗余） |
| industryNameN | varchar(255) | NO | - | 行业名称N | 子行业名称（冗余，与industryName区分） |
| finalCustomerName | varchar(255) | NO | - | 最终客户名称 | 最终客户名称（冗余） |
| salerName | varchar(91) | NO | - | 销售人员名称 | 销售人员姓名（冗余） |
| quesnaireResultHeaderId | int(11) | NO | - | 问卷结果头ID | 逻辑外键 -> pm_cl_quesnaire_result_header.id |
| 工程师技术能力 | longtext | NO | - | 工程师技术能力评分 | 问卷评分维度：工程师技术能力（中文列名） |
| 服务及时性 | longtext | NO | - | 服务及时性评分 | 问卷评分维度：服务及时性（中文列名） |
| 服务水平及规范性 | longtext | NO | - | 服务水平及规范性评分 | 问卷评分维度：服务水平及规范性（中文列名） |
| warrantyStatusName | varchar(4) | NO | - | 维保状态名称 | 维保状态中文名称（冗余） |
| syncTime | datetime | YES | - | 同步时间 | 缓存数据最后同步刷新时间 |

**约束信息**

| 约束类型 | 约束名称 | 字段 | 关联表 | 关联字段 | 说明 |
|----------|----------|------|--------|----------|------|
| - | - | - | - | - | 无显式约束（缓存表，无主键/外键） |

**索引列表**

| 索引名 | 列 | 唯一性 | 索引类型 | 用途说明 |
|--------|-----|--------|----------|----------|
| - | - | - | - | 无索引（缓存宽表，通过全表扫描或应用层控制查询） |

---

### 1.26 pm_project_maintenance_sectary_from_sse -- SSE维保秘书配置表

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 从SSE系统同步的维保秘书配置表，记录各部门对应的维保秘书信息（秘书工号、姓名、邮箱、电话），用于维保流程中的通知和分配。数据从SSE系统定期同步 |
| 数据量 | ~160 行 |
| 数据大小 | 16 KB |

**字段列表**

| 字段名 | 数据类型 | 非空 | 默认值 | 字段注释 | 业务含义 |
|--------|----------|------|--------|----------|----------|
| id | int(11) | YES | auto_increment | 主键自增ID | 记录唯一标识 |
| depNum | varchar(10) | NO | - | 部门编号 | 部门业务编号，唯一标识，逻辑外键 -> fnd_department.departmentNum |
| depName | varchar(20) | NO | - | 部门名称 | 部门名称 |
| pDepNum | varchar(10) | NO | - | 上级部门编号 | 上级部门编号 |
| pDepName | varchar(20) | NO | - | 上级部门名称 | 上级部门名称 |
| sectary | varchar(10) | NO | - | 秘书工号 | 维保秘书工号 |
| sectaryName | varchar(255) | NO | - | 秘书姓名 | 维保秘书姓名 |
| sectaryEmail | varchar(255) | NO | - | 秘书邮箱 | 维保秘书邮箱地址 |
| sectaryPhone | varchar(255) | NO | - | 秘书电话 | 维保秘书联系电话 |
| status | int(4) | NO | 1 | 状态 | 记录状态，1=有效 |
| updateTime | datetime | NO | - | 更新时间 | 数据最后更新时间（同步时间） |

**约束信息**

| 约束类型 | 约束名称 | 字段 | 关联表 | 关联字段 | 说明 |
|----------|----------|------|--------|----------|------|
| PRIMARY KEY | PRIMARY | id | - | - | 主键自增 |
| UNIQUE | depNum | depNum | - | - | 部门编号唯一约束 |

**索引列表**

| 索引名 | 列 | 唯一性 | 索引类型 | 用途说明 |
|--------|-----|--------|----------|----------|
| PRIMARY | id | UNIQUE | BTREE | 主键索引 |
| depNum | depNum | UNIQUE | BTREE | 部门编号唯一索引，确保每个部门只有一条秘书配置 |
| sectary | sectary | NON-UNIQUE | BTREE | 按秘书工号查询 |

---

### 1.27 pm_project_spot_check_ignore_item -- 抽检忽略物料配置

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 抽检忽略物料配置表，配置在项目抽检中需要忽略的物料清单，被忽略的物料不参与抽检评分。当前无数据，为预留功能 |
| 数据量 | ~0 行 |
| 数据大小 | 16 KB |

**字段列表**

| 字段名 | 数据类型 | 非空 | 默认值 | 字段注释 | 业务含义 |
|--------|----------|------|--------|----------|----------|
| itemCode | varchar(25) | NO | - | 物料编码 | 忽略的物料编码 |
| itemModel | varchar(64) | NO | - | 物料型号 | 忽略的物料型号 |
| itemName | varchar(255) | NO | - | 物料名称 | 忽略的物料名称 |

**约束信息**

| 约束类型 | 约束名称 | 字段 | 关联表 | 关联字段 | 说明 |
|----------|----------|------|--------|----------|------|
| - | - | - | - | - | 无主键约束（配置表，无自增ID） |

**索引列表**

| 索引名 | 列 | 唯一性 | 索引类型 | 用途说明 |
|--------|-----|--------|----------|----------|
| itemCode | itemCode | NON-UNIQUE | BTREE | 按物料编码查询忽略配置 |

---

### 4.6 pm_subcontract_project_payment_sse -- SSE转包付款同步表

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 从SSE系统同步的转包项目付款信息，记录转包项目的付款申请、审批和支付状态。数据从SSE系统定期同步，用于PMS中查看转包付款进度 |
| 数据量 | ~3,608 行 |
| 数据大小 | 1.5 MB |

**字段列表**

| 字段名 | 数据类型 | 非空 | 默认值 | 字段注释 | 业务含义 |
|--------|----------|------|--------|----------|----------|
| id | int(11) unsigned | NO | 0 | 记录ID | 付款记录ID（非自增，来自SSE系统） |
| workNo | varchar(10) | NO | - | 工号 | 付款申请人/服务商工号 |
| name | varchar(10) | NO | - | 姓名 | 付款申请人/服务商姓名 |
| offerNum | varchar(20) | NO | - | 报价编号 | SSE系统中的报价编号 |
| applyAmount | decimal(16,2) | NO | - | 申请金额 | 付款申请金额 |
| receiver | varchar(255) | NO | - | 收款人 | 收款人名称 |
| bank | varchar(80) | NO | - | 开户银行 | 收款银行名称 |
| bankAccount | varchar(255) | NO | - | 银行账号 | 收款银行账号 |
| useage | varchar(512) | NO | - | 用途 | 付款用途说明 |
| paystate | varchar(25) | NO | - | 付款状态 | 付款状态编码 |
| confirmTime | datetime | NO | - | 确认时间 | 付款确认时间 |
| paymentTime | datetime | NO | - | 支付时间 | 实际支付时间 |
| approveState | varchar(25) | YES | '' | 审批状态 | 审批状态编码 |
| type | varchar(255) | NO | - | 类型 | 付款类型 |
| approveAmount | decimal(16,2) | NO | - | 审批金额 | 审批通过金额 |
| remark | text | NO | - | 备注 | 备注说明 |
| subcontractNo | varchar(255) | NO | - | 转包编号 | 逻辑外键 -> pm_subcontract_project_header.subcontractNo，关联转包项目 |

**约束信息**

| 约束类型 | 约束名称 | 字段 | 关联表 | 关联字段 | 说明 |
|----------|----------|------|--------|----------|------|
| - | - | - | - | - | 无显式主键约束（同步表，id非自增） |

**索引列表**

| 索引名 | 列 | 唯一性 | 索引类型 | 用途说明 |
|--------|-----|--------|----------|----------|
| id | id | NON-UNIQUE | BTREE | 按记录ID查询 |
| workNo | workNo | NON-UNIQUE | BTREE | 按工号查询付款记录 |
| subcontractNo | subcontractNo | NON-UNIQUE | BTREE | 按转包编号查询付款记录 |

---

### 4.7 pm_subcontract_project_price -- 转包项目价格信息

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 转包项目价格信息表，记录转包项目各订单行的工程费用和价格信息，用于转包费用核算和成本管理 |
| 数据量 | ~5,176 行 |
| 数据大小 | 1.5 MB |

**字段列表**

| 字段名 | 数据类型 | 非空 | 默认值 | 字段注释 | 业务含义 |
|--------|----------|------|--------|----------|----------|
| id | int(11) | YES | auto_increment | 主键自增ID | 记录唯一标识 |
| subcontractId | int(11) | NO | - | 转包ID | 逻辑外键 -> pm_subcontract_project_header.id，关联转包项目 |
| contractNo | varchar(50) | NO | - | 合同编号 | 关联合同编号 |
| orderExecNumber | varchar(25) | NO | - | 订单执行编号 | ERP订单执行行号 |
| projectCode | varchar(25) | NO | - | 项目编码 | 项目业务编码 |
| engineeFee | varchar(25) | NO | - | 工程费用 | 工程费用金额 |
| objId | varchar(64) | NO | - | 对象ID | 关联对象标识 |
| procType | varchar(25) | NO | - | 处理类型 | 费用处理类型编码 |
| price | varchar(25) | NO | - | 价格 | 单价 |
| createTime | datetime | NO | - | 创建时间 | 记录创建时间 |
| createBy | varchar(25) | NO | - | 创建人 | 记录创建用户 |
| updateTime | datetime | NO | - | 更新时间 | 记录最后更新时间 |
| updateBy | varchar(25) | NO | - | 更新人 | 记录最后更新用户 |

**约束信息**

| 约束类型 | 约束名称 | 字段 | 关联表 | 关联字段 | 说明 |
|----------|----------|------|--------|----------|------|
| PRIMARY KEY | PRIMARY | id | - | - | 主键自增 |

**索引列表**

| 索引名 | 列 | 唯一性 | 索引类型 | 用途说明 |
|--------|-----|--------|----------|----------|
| PRIMARY | id | UNIQUE | BTREE | 主键索引 |

---

### 4.8 pm_subcontract_project_callback -- 转包项目回访记录

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 转包项目回访记录表，记录转包项目的回访流程信息，关联工作流任务和问卷，用于跟踪转包项目的服务质量回访 |
| 数据量 | ~416 行 |
| 数据大小 | 64 KB |

**字段列表**

| 字段名 | 数据类型 | 非空 | 默认值 | 字段注释 | 业务含义 |
|--------|----------|------|--------|----------|----------|
| id | int(11) | YES | auto_increment | 主键自增ID | 记录唯一标识 |
| subcontractId | int(11) | NO | - | 转包ID | 逻辑外键 -> pm_subcontract_project_header.id，关联转包项目 |
| taskKey | varchar(25) | NO | - | 任务Key | Activiti工作流任务定义Key |
| taskId | varchar(25) | NO | - | 任务ID | Activiti工作流任务实例ID |
| quesnaireId | int(11) | NO | - | 问卷ID | 关联回访问卷模板ID |
| quesnaireVersion | int(11) | NO | - | 问卷版本 | 问卷模板版本号 |
| quesnaireState | int(11) | NO | - | 问卷状态 | 问卷填写状态 |
| createBy | varchar(25) | NO | - | 创建人 | 记录创建用户 |
| createTime | datetime | NO | - | 创建时间 | 记录创建时间 |
| updateBy | varchar(25) | NO | - | 更新人 | 记录最后更新用户 |
| updateTime | datetime | NO | - | 更新时间 | 记录最后更新时间 |
| effectiveFrom | datetime | NO | - | 有效开始时间 | 数据有效性开始时间（软删除模式） |
| effectiveTo | datetime | NO | - | 有效结束时间 | 数据有效性结束时间，NULL=当前有效 |
| customInfo | json | NO | - | 自定义信息 | JSON扩展字段 |

**约束信息**

| 约束类型 | 约束名称 | 字段 | 关联表 | 关联字段 | 说明 |
|----------|----------|------|--------|----------|------|
| PRIMARY KEY | PRIMARY | id | - | - | 主键自增 |

**索引列表**

| 索引名 | 列 | 唯一性 | 索引类型 | 用途说明 |
|--------|-----|--------|----------|----------|
| PRIMARY | id | UNIQUE | BTREE | 主键索引 |

---

### 5.6 prob_product_component -- 问题关联产品组件

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 技术公告/问题关联的产品组件表，定义产品组件的树形结构（通过parentId），用于技术公告关联到具体的产品组件而非仅到产品级别，实现更精细的组件级问题定位 |
| 数据量 | ~66 行 |
| 数据大小 | 16 KB |

**字段列表**

| 字段名 | 数据类型 | 非空 | 默认值 | 字段注释 | 业务含义 |
|--------|----------|------|--------|----------|----------|
| id | int(11) | YES | auto_increment | 主键自增ID | 组件唯一标识 |
| type | varchar(100) | NO | - | 组件类型 | 产品组件类型分类 |
| name | varchar(100) | NO | - | 组件名称 | 产品组件名称 |
| version | varchar(100) | NO | - | 组件版本 | 产品组件版本号 |
| parentId | int(11) | NO | - | 父组件ID | 逻辑外键 -> prob_product_component.id，构建组件树形结构 |
| state | bit(1) | NO | b'1' | 状态 | 组件状态，1=有效，0=失效 |
| customInfo | json | NO | - | 自定义信息 | JSON扩展字段 |
| createBy | varchar(25) | NO | - | 创建人 | 记录创建用户 |
| createTime | datetime | NO | CURRENT_TIMESTAMP | 创建时间 | 记录创建时间，默认当前时间 |
| updateBy | varchar(25) | NO | - | 更新人 | 记录最后更新用户 |
| updateTime | datetime | NO | - | 更新时间 | 记录最后更新时间，自动更新（on update CURRENT_TIMESTAMP） |

**约束信息**

| 约束类型 | 约束名称 | 字段 | 关联表 | 关联字段 | 说明 |
|----------|----------|------|--------|----------|------|
| PRIMARY KEY | PRIMARY | id | - | - | 主键自增 |

**索引列表**

| 索引名 | 列 | 唯一性 | 索引类型 | 用途说明 |
|--------|-----|--------|----------|----------|
| PRIMARY | id | UNIQUE | BTREE | 主键索引 |

---

### 5.7 prob_restore_process -- 问题恢复流程记录

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 技术公告/问题的恢复流程记录表，记录问题恢复的流程状态和备注，与prob_restore（恢复方案）互补，侧重于恢复过程的流程跟踪而非具体方案内容 |
| 数据量 | ~9 行 |
| 数据大小 | 16 KB |

**字段列表**

| 字段名 | 数据类型 | 非空 | 默认值 | 字段注释 | 业务含义 |
|--------|----------|------|--------|----------|----------|
| id | int(11) | YES | auto_increment | 主键自增ID | 记录唯一标识 |
| probId | int(11) | NO | - | 问题ID | 逻辑外键 -> prob_main.id，关联技术公告 |
| restoreStatus | int(11) | NO | - | 恢复状态 | 恢复流程状态编码 |
| restoreRemark | text | NO | - | 恢复备注 | 恢复流程备注说明 |
| createBy | varchar(25) | NO | - | 创建人 | 记录创建用户 |
| createTime | datetime | NO | - | 创建时间 | 记录创建时间 |
| updateBy | varchar(25) | NO | - | 更新人 | 记录最后更新用户 |
| updateTime | datetime | NO | - | 更新时间 | 记录最后更新时间 |

**约束信息**

| 约束类型 | 约束名称 | 字段 | 关联表 | 关联字段 | 说明 |
|----------|----------|------|--------|----------|------|
| PRIMARY KEY | PRIMARY | id | - | - | 主键自增 |

**索引列表**

| 索引名 | 列 | 唯一性 | 索引类型 | 用途说明 |
|--------|-----|--------|----------|----------|
| PRIMARY | id | UNIQUE | BTREE | 主键索引 |
| probId | probId, restoreStatus | NON-UNIQUE | BTREE | 复合索引，按问题ID+恢复状态查询 |

---

### 5.8 prob_restore_weekly -- 问题恢复周报

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 技术公告/问题的恢复周报附件表，关联问题与周报文件。当前无数据，为预留功能 |
| 数据量 | ~0 行 |
| 数据大小 | 16 KB |

**字段列表**

| 字段名 | 数据类型 | 非空 | 默认值 | 字段注释 | 业务含义 |
|--------|----------|------|--------|----------|----------|
| id | int(11) | YES | auto_increment | 主键自增ID | 记录唯一标识 |
| probId | int(11) | NO | - | 问题ID | 逻辑外键 -> prob_main.id，关联技术公告 |
| fileId | int(11) | NO | - | 文件ID | 逻辑外键 -> fnd_files.id，关联周报文件 |
| createBy | varchar(25) | NO | - | 创建人 | 记录创建用户 |
| createTime | datetime | NO | - | 创建时间 | 记录创建时间 |
| updateBy | varchar(25) | NO | - | 更新人 | 记录最后更新用户 |
| updateTime | datetime | NO | - | 更新时间 | 记录最后更新时间 |

**约束信息**

| 约束类型 | 约束名称 | 字段 | 关联表 | 关联字段 | 说明 |
|----------|----------|------|--------|----------|------|
| PRIMARY KEY | PRIMARY | id | - | - | 主键自增 |

**索引列表**

| 索引名 | 列 | 唯一性 | 索引类型 | 用途说明 |
|--------|-----|--------|----------|----------|
| PRIMARY | id | UNIQUE | BTREE | 主键索引 |
| probId | probId | NON-UNIQUE | BTREE | 按问题ID查询周报 |

---

### 5.9 prob_soft_version -- 问题关联软件版本

| 属性 | 值 |
|------|-----|
| 对象类型 | BASE TABLE |
| 业务含义 | 技术公告/问题关联的软件版本组合表，定义CONP/CPLD/Boot/PCB四元组版本号，通过唯一约束确保版本组合不重复。当前无数据，为预留功能 |
| 数据量 | ~0 行 |
| 数据大小 | 16 KB |

**字段列表**

| 字段名 | 数据类型 | 非空 | 默认值 | 字段注释 | 业务含义 |
|--------|----------|------|--------|----------|----------|
| id | int(11) | YES | auto_increment | 主键自增ID | 记录唯一标识 |
| conp | varchar(100) | NO | - | 主控版本 | CONP主控软件版本号 |
| cpld | varchar(100) | NO | - | CPLD版本 | CPLD复杂可编程逻辑器件版本号 |
| boot | varchar(100) | NO | - | Boot版本 | 引导程序版本号 |
| pcb | varchar(100) | NO | - | PCB版本 | 印刷电路板版本号 |
| createdBy | varchar(25) | NO | - | 创建人 | 记录创建用户 |
| createdTime | datetime | NO | - | 创建时间 | 记录创建时间 |

**约束信息**

| 约束类型 | 约束名称 | 字段 | 关联表 | 关联字段 | 说明 |
|----------|----------|------|--------|----------|------|
| PRIMARY KEY | PRIMARY | id | - | - | 主键自增 |
| UNIQUE | conp | conp, cpld, boot, pcb | - | - | 四字段联合唯一约束，确保版本组合不重复 |

**索引列表**

| 索引名 | 列 | 唯一性 | 索引类型 | 用途说明 |
|--------|-----|--------|----------|----------|
| PRIMARY | id | UNIQUE | BTREE | 主键索引 |
| conp | conp, cpld, boot, pcb | UNIQUE | BTREE | 四字段联合唯一索引，确保CONP+CPLD+Boot+PCB版本组合唯一 |

---

*文档结束 - 数据基准时间: 2026-06-12 | 数据库: dppms_d365*