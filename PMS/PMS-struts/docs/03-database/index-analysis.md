# 索引设计与性能分析

> 数据库：dppms_d365 (MySQL)
> 基于iBatis SQL映射文件中的查询模式分析

> ⚠️ **准确性警告**：本文档中的索引信息是基于 iBatis SQL 映射文件中的查询模式**推测**编写的，并非从实际数据库导出。经与 [complete-data-dictionary.md](./complete-data-dictionary.md)（从实际数据库导出）对比，大量索引与实际数据库配置不符。下方已对已确认不存在的索引添加 ~~删除线~~ 标注，并补充了实际索引信息。**建议通过 `SHOW INDEX FROM table_name` 命令获取实际索引信息。**

---

## 一、各表索引汇总

### 1. 基础数据表索引

#### fnd_user_info

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| username | 唯一索引 | username | 登录名唯一（实际索引名） |
| ~~idx_dpNo~~ | ~~普通索引~~ | ~~dpNo~~ | ~~按部门查询用户~~ ⚠️ 实际不存在此索引 |
| ~~idx_roleIds~~ | ~~普通索引~~ | ~~roleIds(前缀)~~ | ~~按角色模糊查询~~ ⚠️ 实际不存在此索引 |

> ⚠️ 实际索引仅有：PRIMARY(id), username(username, 唯一)

**查询模式分析：**
- `query-user-by-name`：WHERE username = ?，命中唯一索引
- `query-userlist`：WHERE (username LIKE ? OR realName LIKE ?) AND roleIds LIKE ? AND dpNo = ?，需组合索引优化
- `query_permissions_by_name`：WHERE fnd_user_id = ? AND effectiveFrom < NOW()，需关联fnd_user_menus

#### fnd_user_menus

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| ~~idx_fnd_user_id~~ | ~~普通索引~~ | ~~fnd_user_id~~ | ~~按用户查询菜单权限~~ ⚠️ 实际不存在此索引 |
| ~~idx_menuCode~~ | ~~普通索引~~ | ~~menuCode~~ | ~~按菜单编码查询~~ ⚠️ 实际不存在此索引 |

> ⚠️ 实际索引仅有：PRIMARY(id)

#### fnd_user_power

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| ~~idx_fndUserId~~ | ~~普通索引~~ | ~~fndUserId~~ | ~~按用户查询区域权限~~ ⚠️ 实际不存在此索引 |

> ⚠️ 实际索引仅有：PRIMARY(id)

#### fnd_basic_data

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| basicDataId | 唯一索引 | basicDataId | 数据项ID唯一（实际索引名） |
| basicDataId_dataTypeCode | 唯一索引 | dataTypeCode, basicDataId | 类型+数据项唯一（实际索引名） |
| ~~idx_dataTypeCode~~ | ~~普通索引~~ | ~~dataTypeCode~~ | ~~按类型查询数据字典~~ ⚠️ 实际不存在此独立索引 |
| ~~uk_type_id~~ | ~~唯一索引~~ | ~~dataTypeCode, basicDataId~~ | ~~类型+数据项唯一~~ ⚠️ 实际索引名为 basicDataId_dataTypeCode |

> ⚠️ 实际索引：PRIMARY(id), basicDataId(basicDataId, 唯一), basicDataId_dataTypeCode(dataTypeCode+basicDataId, 唯一)

#### fnd_department

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| deparmentNum | 普通索引 | departmentNum | 部门编号索引（实际索引名，注意拼写） ⚠️ 实际为非唯一索引 |
| ~~uk_departmentNum~~ | ~~唯一索引~~ | ~~departmentNum~~ | ~~部门编号唯一~~ ⚠️ 实际索引名为 deparmentNum，且为非唯一索引 |

> ⚠️ 实际索引：PRIMARY(id), deparmentNum(departmentNum, 非唯一)

#### fnd_menus

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| ~~uk_menuCode~~ | ~~唯一索引~~ | ~~menuCode~~ | ~~菜单编码唯一~~ ⚠️ 实际不存在此索引 |
| ~~idx_superId~~ | ~~普通索引~~ | ~~superId~~ | ~~按父菜单查询子菜单~~ ⚠️ 实际不存在此索引 |

> ⚠️ 实际索引仅有：PRIMARY(id)

#### fnd_roles

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| roleName | 普通索引 | roleName | 角色名索引（实际索引名，非唯一） |

> ⚠️ 实际索引：PRIMARY(id), roleName(roleName, 非唯一)

#### fnd_company

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| code | 唯一索引 | code | 公司编码唯一（实际索引名） |
| pid | 唯一索引 | pid | 父公司索引（实际索引名） |
| ~~uk_code~~ | ~~唯一索引~~ | ~~code~~ | ~~公司编码唯一~~ ⚠️ 实际索引名为 code |

> ⚠️ 实际索引：PRIMARY(id), code(code, 唯一), pid(pid, 唯一)

#### fnd_sys_arg

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| ~~uk_code~~ | ~~唯一索引~~ | ~~code~~ | ~~参数编码唯一~~ ⚠️ 实际不存在此索引 |

> ⚠️ 实际索引仅有：PRIMARY(id)

---

### 2. 项目核心表索引

#### pm_project_header（视图） / pm_project（实际表）

> ⚠️ `pm_project_header` 是基于 `pm_project` 表的 VIEW，非独立表。实际索引定义在 `pm_project` 表上。`pm_project` 表约有 70,370 行数据。

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | projectId | 自增主键（实际索引） |
| department | 普通索引 | column001 | 按办事处筛选（实际索引名，非唯一） |
| projectCode_index | 普通索引 | projectCode, projectType | 项目编码+类型组合查询（实际索引名，非唯一） |
| projectType_projectId_IDX | 普通索引 | projectType, projectId | 项目类型+ID组合查询（实际索引名，非唯一） |
| ~~uk_projectCode~~ | ~~唯一索引~~ | ~~projectCode~~ | ~~项目编码唯一~~ ⚠️ 实际为组合索引 projectCode_index(projectCode, projectType)，非唯一 |
| ~~idx_projectState~~ | ~~普通索引~~ | ~~projectState~~ | ~~按状态筛选~~ ⚠️ 实际不存在此索引 |
| ~~idx_column001~~ | ~~普通索引~~ | ~~column001~~ | ~~按办事处筛选~~ ⚠️ 实际索引名为 department，且为非唯一索引 |
| ~~idx_compId~~ | ~~普通索引~~ | ~~compId~~ | ~~按公司筛选~~ ⚠️ 实际不存在此索引 |
| ~~idx_effectiveTo~~ | ~~普通索引~~ | ~~effectiveTo~~ | ~~有效数据过滤~~ ⚠️ 实际不存在此索引 |
| ~~idx_createTime~~ | ~~普通索引~~ | ~~createTime~~ | ~~按创建时间排序~~ ⚠️ 实际不存在此索引 |

> ⚠️ 实际索引（pm_project表）：PRIMARY(projectId, 唯一), department(column001, 非唯一), projectCode_index(projectCode+projectType, 非唯一), projectType_projectId_IDX(projectType+projectId, 非唯一)

**查询模式分析：**
- 项目列表查询是最复杂的查询，涉及多表LEFT JOIN（member、state、department、basic_data、related_party等6-10张表）
- WHERE条件通常包含：effectiveTo IS NULL、projectState、column001(officeCode)、memberCode
- GROUP BY用于合同号聚合
- 建议增加组合索引：(effectiveTo, projectState, column001)

#### pm_project_member

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| memberCode_IDX | 唯一索引 | memberCode, projectId, projectType | 人员编码+项目+类型（实际索引名） |
| projectId_role | 唯一索引 | projectId, memberRole | 项目+角色（实际索引名） |
| projectId_type | 唯一索引 | projectId, projectType | 项目+类型（实际索引名） |
| ~~idx_projectId~~ | ~~普通索引~~ | ~~projectId~~ | ~~按项目查询成员~~ ⚠️ 实际不存在此独立索引，有组合索引 |
| ~~idx_memberCode~~ | ~~普通索引~~ | ~~memberCode~~ | ~~按人员编码查询~~ ⚠️ 实际为组合索引 memberCode_IDX |
| ~~idx_projectId_role~~ | ~~组合索引~~ | ~~projectId, memberRole, effectiveFrom, effectiveTo~~ | ~~项目+角色+有效期查询~~ ⚠️ 实际索引名为 projectId_role，仅含 projectId+memberRole |

> ⚠️ 实际索引：PRIMARY(id), memberCode_IDX(memberCode+projectId+projectType, 唯一), projectId_role(projectId+memberRole, 唯一), projectId_type(projectId+projectType, 唯一)

**查询模式分析：**
- 项目列表查询中需要多次LEFT JOIN此表（pm/pm2/pm3/gm），分别按memberRole=10/20/30/40查询
- 每次JOIN条件：projectId = ? AND memberRole = ? AND effectiveFrom < NOW() AND (effectiveTo > NOW() OR effectiveTo IS NULL)
- **关键索引**：组合索引(projectId, memberRole, effectiveFrom, effectiveTo)可大幅提升JOIN性能

#### pm_project_state

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | projectId | 主键 |
| index_projectId | 普通索引 | projectId | 项目ID索引（实际索引名） |
| projectPlanState | 唯一索引 | projectPlanState | 工程计划状态（实际索引名） |
| shipmentState | 唯一索引 | shipmentState | 发货状态（实际索引名） |
| ~~idx_shipmentState~~ | ~~普通索引~~ | ~~shipmentState~~ | ~~按发货状态查询~~ ⚠️ 实际索引名为 shipmentState，且为唯一索引 |
| ~~idx_executionState~~ | ~~普通索引~~ | ~~executionState~~ | ~~按实施状态查询~~ ⚠️ 实际不存在此索引 |

> ⚠️ 实际索引：PRIMARY(projectId), index_projectId(projectId, 非唯一), projectPlanState(projectPlanState, 唯一), shipmentState(shipmentState, 唯一)

#### pm_project_contract

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| id | 普通索引 | id | 非唯一索引（⚠️ 该表无PRIMARY KEY，id仅为KEY） |
| contract_projectGroupCode_IDX | 普通索引 | contractNo, projectGroupCode | 合同号+项目组编码（实际索引名，非唯一） |
| projectGroupCode_contract_IDX | 普通索引 | projectGroupCode, contractNo | 项目组编码+合同号（实际索引名，非唯一） |
| ~~idx_projectGroupCode~~ | ~~普通索引~~ | ~~projectGroupCode~~ | ~~按项目组查询合同~~ ⚠️ 实际不存在此独立索引 |
| ~~idx_contractNo~~ | ~~普通索引~~ | ~~contractNo~~ | ~~按合同号查询~~ ⚠️ 实际不存在此独立索引 |

> ⚠️ 实际索引：id(id, 非唯一), contract_projectGroupCode_IDX(contractNo+projectGroupCode, 非唯一), projectGroupCode_contract_IDX(projectGroupCode+contractNo, 非唯一)。⚠️ 该表无PRIMARY KEY

#### pm_project_group_relationship

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| projectCode | 唯一索引 | projectCode | 项目编码（实际索引名） |
| projectGroupCode | 唯一索引 | projectGroupCode | 项目组编码（实际索引名） |
| smsProjectCode | 唯一索引 | smsProjectCode | SMS项目编码（实际索引名） |
| ~~idx_projectCode~~ | ~~普通索引~~ | ~~projectCode~~ | ~~按项目编码查询~~ ⚠️ 实际为唯一索引 |
| ~~idx_projectGroupCode~~ | ~~普通索引~~ | ~~projectGroupCode~~ | ~~按项目组编码查询~~ ⚠️ 实际为唯一索引 |

> ⚠️ 实际索引：PRIMARY(id), projectCode(projectCode, 唯一), projectGroupCode(projectGroupCode, 唯一), smsProjectCode(smsProjectCode, 唯一)

#### pm_project_soft_version

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| barcode | 唯一索引 | barCode | 设备序列号（实际索引名） |
| projectBarcodeValid | 唯一索引 | projectId, barCode, datastate | 项目+序列号+数据状态（实际索引名） |
| pm_project_soft_version_conp_IDX | 唯一索引 | conp | 软件版本（实际索引名） |
| idx_conp_item_query | 唯一索引 | datastate, conpType, conpSeries, conpMark, itemCode, projectId | 版本查询组合索引（实际索引名） |
| ~~idx_projectId~~ | ~~普通索引~~ | ~~projectId~~ | ~~按项目查询版本~~ ⚠️ 实际不存在此独立索引 |
| ~~idx_barCode~~ | ~~普通索引~~ | ~~barCode~~ | ~~按序列号查询~~ ⚠️ 实际索引名为 barcode，且为唯一索引 |
| ~~idx_contractNo~~ | ~~普通索引~~ | ~~contractNo~~ | ~~按合同号查询~~ ⚠️ 实际不存在此索引 |

> ⚠️ 实际索引：PRIMARY(id), barcode(barCode, 唯一), projectBarcodeValid(projectId+barCode+datastate, 唯一), pm_project_soft_version_conp_IDX(conp, 唯一), idx_conp_item_query(datastate+conpType+conpSeries+conpMark+itemCode+projectId, 唯一)

---

### 3. 售前项目表索引

#### pm_presales_project_header

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | presalesId | 自增主键 |
| instId | 唯一索引 | instId | 工作流流程ID（实际索引名） |
| lendInfoId | 唯一索引 | lendInfoId | 借货申请ID（实际索引名） |
| projectCode | 唯一索引 | projectCode | 项目编码（实际索引名） |
| ~~uk_presalesCode~~ | ~~唯一索引~~ | ~~presalesCode~~ | ~~售前编码唯一~~ ⚠️ 实际不存在此索引 |
| ~~idx_projectState~~ | ~~普通索引~~ | ~~projectState~~ | ~~按状态筛选~~ ⚠️ 实际不存在此索引 |
| ~~idx_officeCode~~ | ~~普通索引~~ | ~~officeCode~~ | ~~按办事处筛选~~ ⚠️ 实际不存在此索引 |
| ~~idx_applyState~~ | ~~普通索引~~ | ~~applyState~~ | ~~按申请状态筛选~~ ⚠️ 实际不存在此索引 |

> ⚠️ 实际索引：PRIMARY(presalesId), instId(instId, 唯一), lendInfoId(lendInfoId, 唯一), projectCode(projectCode, 唯一)

#### pm_presales_project_product_line

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | productLineId | 自增主键 |
| presalesId | 唯一索引 | presalesId | 售前项目ID（实际索引名） |
| lendInfoId | 唯一索引 | lendInfoId | 借货主表主键（实际索引名） |
| ~~idx_presalesId~~ | ~~普通索引~~ | ~~presalesId~~ | ~~按售前项目查询~~ ⚠️ 实际为唯一索引 |

> ⚠️ 实际索引：PRIMARY(productLineId), presalesId(presalesId, 唯一), lendInfoId(lendInfoId, 唯一)

---

### 4. 回访管理表索引

#### pm_cl_evaluation_header

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| projectCode_index | 唯一索引 | projectCode | 项目编码（实际索引名） |
| projectId | 唯一索引 | projectId | 项目ID（实际索引名） |
| ~~idx_projectCode~~ | ~~普通索引~~ | ~~projectCode~~ | ~~按项目编码查询~~ ⚠️ 实际索引名为 projectCode_index，且为唯一索引 |
| ~~idx_projectId~~ | ~~普通索引~~ | ~~projectId~~ | ~~按项目ID查询~~ ⚠️ 实际为唯一索引 |
| ~~idx_evaluationType~~ | ~~普通索引~~ | ~~evaluationType~~ | ~~按评价类型查询~~ ⚠️ 实际不存在此索引 |

> ⚠️ 实际索引：PRIMARY(id), projectCode_index(projectCode, 唯一), projectId(projectId, 唯一)

#### pm_cl_quesnaire_template_header

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| quesType | 唯一索引 | quesType | 问卷类型（实际索引名） |
| ~~uk_templateNum~~ | ~~唯一索引~~ | ~~questionnaireTemplateNum~~ | ~~模板编号唯一~~ ⚠️ 实际不存在此索引 |
| ~~idx_status~~ | ~~普通索引~~ | ~~questionnaireStatus~~ | ~~按状态查询~~ ⚠️ 实际不存在此索引 |

> ⚠️ 实际索引：PRIMARY(id), quesType(quesType, 唯一)

---

### 5. 转包项目表索引

#### pm_subcontract_project_header

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| subcontractNo | 唯一索引 | subcontractNo | 转包合同号（实际索引名） |
| facilitatorId | 唯一索引 | facilitatorId | 服务商ID（实际索引名） |
| officeCode | 唯一索引 | officeCode | 办事处编码（实际索引名） |
| profitDepCode | 唯一索引 | profitDepCode | 收益部门（实际索引名） |
| ~~idx_subcontractNo~~ | ~~普通索引~~ | ~~subcontractNo~~ | ~~按转包合同号查询~~ ⚠️ 实际为唯一索引 |
| ~~idx_state~~ | ~~普通索引~~ | ~~state~~ | ~~按状态筛选~~ ⚠️ 实际不存在此索引 |
| ~~idx_facilitatorId~~ | ~~普通索引~~ | ~~facilitatorId~~ | ~~按服务商查询~~ ⚠️ 实际为唯一索引 |
| ~~idx_officeCode~~ | ~~普通索引~~ | ~~officeCode~~ | ~~按办事处筛选~~ ⚠️ 实际为唯一索引 |

> ⚠️ 实际索引：PRIMARY(id), subcontractNo(subcontractNo, 唯一), facilitatorId(facilitatorId, 唯一), officeCode(officeCode, 唯一), profitDepCode(profitDepCode, 唯一)

#### pm_subcontract_project_line

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| barcode | 普通索引 | barcode | 设备序列号（实际索引名，非唯一） |
| contractNo | 普通索引 | contractNo | 合同号（实际索引名，非唯一） |
| itemCode | 普通索引 | itemCode | 设备编码（实际索引名，非唯一） |
| projectId | 普通索引 | projectId | 原项目ID（实际索引名，非唯一） |
| unique_index | 唯一索引 | subcontractId, barcode | 转包项目+序列号（实际索引名，唯一） |
| ~~idx_subcontractId~~ | ~~普通索引~~ | ~~subcontractId~~ | ~~按转包项目查询~~ ⚠️ 实际不存在此独立索引，有组合索引 unique_index |

> ⚠️ 实际索引：PRIMARY(id), barcode(barcode, 非唯一), contractNo(contractNo, 非唯一), itemCode(itemCode, 非唯一), projectId(projectId, 非唯一), unique_index(subcontractId+barcode, 唯一)

#### pm_subcontract_project_payment

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| subcontractId | 普通索引 | subcontractId | 转包项目ID（实际索引名，非唯一） |

> ⚠️ 实际索引：PRIMARY(id), subcontractId(subcontractId, 非唯一)

#### pm_facilitator

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| code | 唯一索引 | code | 服务商编号（实际索引名，唯一） |
| account | 唯一索引 | account, state | 服务商账号+状态（实际索引名，唯一） |
| ~~idx_code~~ | ~~普通索引~~ | ~~code~~ | ~~按编号查询~~ ⚠️ 实际索引名为 code，且为唯一索引 |
| ~~idx_state~~ | ~~普通索引~~ | ~~state~~ | ~~按状态筛选~~ ⚠️ 实际不存在此独立索引，state 在 account 组合索引中 |

> ⚠️ 实际索引：PRIMARY(id), code(code, 唯一), account(account+state, 唯一)

---

### 6. 技术公告表索引

#### prob_main

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| probNum_IDX | 唯一索引 | probNum, id | 公告编号+ID（实际索引名） |
| ~~uk_probNum~~ | ~~唯一索引~~ | ~~probNum~~ | ~~公告编号唯一~~ ⚠️ 实际索引名为 probNum_IDX，且包含 id 列 |
| ~~idx_status~~ | ~~普通索引~~ | ~~status~~ | ~~按状态筛选~~ ⚠️ 实际不存在此索引 |
| ~~idx_priority~~ | ~~普通索引~~ | ~~priority~~ | ~~按优先级筛选~~ ⚠️ 实际不存在此索引 |
| ~~idx_trackingUser~~ | ~~普通索引~~ | ~~trackingUser~~ | ~~按跟踪人查询~~ ⚠️ 实际不存在此索引 |
| ~~idx_productType~~ | ~~普通索引~~ | ~~productType(前缀)~~ | ~~按产品类型查询~~ ⚠️ 实际不存在此索引 |

> ⚠️ 实际索引：PRIMARY(id), probNum_IDX(probNum+id, 唯一)

#### prob_restore

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| probId_serialNum_IDX | 普通索引 | probId, serialNum | 公告ID+序列号（实际索引名，非唯一） |
| itemModel | 普通索引 | itemModel | 设备类型（实际索引名，非唯一） |
| processId | 普通索引 | processId | 流程ID（实际索引名，非唯一） |
| projectId | 普通索引 | projectId | 项目ID（实际索引名，非唯一） |
| serialNum | 普通索引 | serialNum | 序列号（实际索引名，非唯一） |
| ~~idx_probId~~ | ~~普通索引~~ | ~~probId~~ | ~~按公告查询跟踪任务~~ ⚠️ 实际为组合索引 probId_serialNum_IDX |
| ~~idx_assignee~~ | ~~普通索引~~ | ~~assignee~~ | ~~按指派人查询~~ ⚠️ 实际不存在此索引 |
| ~~idx_restoreStatus~~ | ~~普通索引~~ | ~~restoreStatus~~ | ~~按恢复状态查询~~ ⚠️ 实际不存在此索引 |

> ⚠️ 实际索引：PRIMARY(id), probId_serialNum_IDX(probId+serialNum, 非唯一), itemModel(itemModel, 非唯一), processId(processId, 非唯一), projectId(projectId, 非唯一), serialNum(serialNum, 非唯一)

---

### 7. 数据同步中间表索引

#### pm_order_data_from_erp_source / _sap / _d365

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| orderNumber | 唯一索引 | orderNumber | 订单号唯一（实际索引名） |
| contractNo | 唯一索引 | contractNo | 合同号唯一（实际索引名） |
| orderExecNumber | 唯一索引 | orderExecNumber | 执行号唯一（实际索引名） |
| orderType | 唯一索引 | orderType, salesType | 类型+销售类型组合（仅source表） |
| ~~idx_syncTime~~ | ~~普通索引~~ | ~~syncTime~~ | ~~按同步时间查询~~ ⚠️ 实际不存在此索引 |

> ⚠️ 实际索引（以 pm_order_data_from_erp_source 为例）：PRIMARY(id), orderNumber(orderNumber, 唯一), contractNo(contractNo, 唯一), orderExecNumber(orderExecNumber, 唯一), orderType(orderType+salesType, 唯一)。SAP/D365子表索引类似但不含orderType组合索引。

#### pm_person_from_oa

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| salesmanCode1 | 唯一索引 | salesmanCode | 人员编码（实际索引名） |
| ~~uk_salesmanCode~~ | ~~唯一索引~~ | ~~salesmanCode~~ | ~~人员编码唯一~~ ⚠️ 实际索引名为 salesmanCode1 |

> ⚠️ 实际索引：PRIMARY(id), salesmanCode1(salesmanCode, 唯一)

#### pm_project_property_from_sms

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| projectCode | 唯一索引 | projectCode | 项目编码（实际索引名） |
| orderExecNum | 唯一索引 | orderExecNumber | 订单执行号（实际索引名） |
| ~~idx_projectCode~~ | ~~普通索引~~ | ~~projectCode~~ | ~~按项目编码查询~~ ⚠️ 实际为唯一索引 |
| ~~idx_orderExecNumber~~ | ~~普通索引~~ | ~~orderExecNumber~~ | ~~按订单执行号查询~~ ⚠️ 实际索引名为 orderExecNum，且为唯一索引 |

> ⚠️ 实际索引：PRIMARY(id), projectCode(projectCode, 唯一), orderExecNum(orderExecNumber, 唯一)

---

### 8. 其他表索引

#### pm_project_maintenance

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| projectId | 唯一索引 | projectId | 项目ID（实际索引名） |
| projectCode | 唯一索引 | projectCode | 项目编码（实际索引名） |
| officeCode | 唯一索引 | officeCode | 办事处编码（实际索引名） |
| type | 唯一索引 | type | 任务性质（实际索引名） |
| category | 唯一索引 | category, subCategory | 任务分类+小类（实际索引名） |
| subCategory | 唯一索引 | subCategory | 任务小类（实际索引名） |
| projectType | 唯一索引 | projectType | 项目类型（实际索引名） |
| createBy | 唯一索引 | createBy | 创建用户（实际索引名） |
| createTime | 唯一索引 | createTime | 创建时间（实际索引名） |
| processTime_IDX | 唯一索引 | processTime | 处理时间（实际索引名） |
| ~~idx_projectId~~ | ~~普通索引~~ | ~~projectId~~ | ~~按项目查询~~ ⚠️ 实际为唯一索引 |
| ~~idx_officeCode~~ | ~~普通索引~~ | ~~officeCode~~ | ~~按办事处筛选~~ ⚠️ 实际为唯一索引 |
| ~~idx_type~~ | ~~普通索引~~ | ~~type~~ | ~~按任务性质筛选~~ ⚠️ 实际为唯一索引 |

> ⚠️ 实际索引：PRIMARY(id), projectId(projectId, 唯一), projectCode(projectCode, 唯一), officeCode(officeCode, 唯一), type(type, 唯一), category(category+subCategory, 唯一), subCategory(subCategory, 唯一), projectType(projectType, 唯一), createBy(createBy, 唯一), createTime(createTime, 唯一), processTime_IDX(processTime, 唯一)

#### dp_act_unify_task

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| taskId | 唯一索引 | taskId | 统一待办任务ID（实际索引名） |
| originTaskId | 唯一索引 | originTaskId | Activiti源TaskId（实际索引名） |
| procInstId | 唯一索引 | procInstId | 流程实例ID（实际索引名） |
| ~~idx_taskId~~ | ~~普通索引~~ | ~~taskId~~ | ~~按任务ID查询~~ ⚠️ 实际为唯一索引 |
| ~~idx_assignee~~ | ~~普通索引~~ | ~~assignee~~ | ~~按办理人查询~~ ⚠️ 实际不存在此索引 |
| ~~idx_processKey~~ | ~~普通索引~~ | ~~processKey~~ | ~~按流程Key查询~~ ⚠️ 实际不存在此索引 |
| ~~idx_procInstId~~ | ~~普通索引~~ | ~~procInstId~~ | ~~按流程实例查询~~ ⚠️ 实际为唯一索引 |
| ~~idx_state~~ | ~~普通索引~~ | ~~state~~ | ~~按状态筛选~~ ⚠️ 实际不存在此索引 |
| ~~idx_latest~~ | ~~普通索引~~ | ~~latest~~ | ~~查询最新记录~~ ⚠️ 实际不存在此索引 |

> ⚠️ 实际索引：PRIMARY(id), taskId(taskId, 唯一), originTaskId(originTaskId, 唯一), procInstId(procInstId, 唯一)

---

## 二、高频查询索引覆盖分析

### 1. 项目列表查询（最高频）

**查询特征：**
```sql
SELECT ph.*, pm.*, state.*, d.*
FROM pm_project_header ph
LEFT JOIN pm_project_group_relationship pr ON ph.projectCode = pr.projectCode
LEFT JOIN pm_project_contract pc ON pr.projectGroupCode = pc.projectGroupCode
LEFT JOIN pm_project_member pm ON pm.memberRole = '10' AND ph.projectId = pm.projectId
    AND pm.effectiveFrom < NOW() AND (pm.effectiveTo > NOW() OR pm.effectiveTo IS NULL)
LEFT JOIN pm_project_member pm2 ON pm2.memberRole = '20' AND ph.projectId = pm2.projectId ...
LEFT JOIN pm_project_member pm3 ON pm3.memberRole = '30' AND ph.projectId = pm3.projectId ...
LEFT JOIN pm_project_state state ON state.projectId = ph.projectId
LEFT JOIN fnd_department d ON d.departmentNum = ph.column001
WHERE ph.effectiveTo IS NULL
    AND ph.projectState = ?
    AND ph.column001 IN (...)
    AND pm.memberCode = ?
```

**索引覆盖分析：**

| 表 | 实际索引 | 是否覆盖 | 优化建议 |
|----|---------|---------|---------|
| pm_project（pm_project_header视图的底层表） | PRIMARY(projectId), department(column001), projectCode_index(projectCode+projectType), projectType_projectId_IDX(projectType+projectId) | 部分 | department覆盖column001查询，但缺少effectiveTo/projectState索引；建议增加组合索引(effectiveTo, projectState, column001) |
| pm_project_member | memberCode_IDX, projectId_role, projectId_type | 部分 | projectId_role(projectId+memberRole)覆盖JOIN条件，但缺少effectiveFrom/effectiveTo列 |
| pm_project_state | PK(projectId), index_projectId | 是 | 主键即最优 |
| pm_project_contract | id(非唯一), contract_projectGroupCode_IDX(非唯一), projectGroupCode_contract_IDX(非唯一) | 是 | 覆盖良好（⚠️ 该表无PRIMARY KEY） |
| pm_project_group_relationship | projectCode(唯一), projectGroupCode(唯一) | 是 | 覆盖良好 |
| fnd_department | deparmentNum(非唯一) | 是 | 索引覆盖查询 |

### 2. 用户登录/权限查询（高频）

**查询特征：**
```sql
SELECT u.*, p.areapower FROM fnd_user_info u
LEFT JOIN fnd_user_power p ON u.id = p.fndUserId
WHERE u.username = ? AND u.status = 1

SELECT menuCode, menuValue FROM fnd_user_menus
WHERE fnd_user_id = ? AND effectiveFrom < NOW() AND (effectiveTo > NOW() OR effectiveTo IS NULL)
```

**索引覆盖分析：**

| 表 | 实际索引 | 是否覆盖 | 优化建议 |
|----|---------|---------|---------|
| fnd_user_info | username(唯一) | 是 | 唯一索引精确匹配 |
| fnd_user_power | PRIMARY(id) | 部分 | 无fndUserId索引，LEFT JOIN走全表扫描；建议增加(fndUserId)索引 |
| fnd_user_menus | PRIMARY(id) | 否 | 无fnd_user_id索引，权限查询走全表扫描；建议增加(fnd_user_id, effectiveFrom, effectiveTo)索引 |

### 3. 技术公告查询（中频）

**查询特征：**
```sql
SELECT * FROM prob_main WHERE status = ? AND priority = ? AND trackingUser = ?
SELECT * FROM prob_restore WHERE probId = ? AND restoreStatus = ?
```

**索引覆盖分析：**

| 表 | 实际索引 | 是否覆盖 | 优化建议 |
|----|---------|---------|---------|
| prob_main | PRIMARY(id), probNum_IDX(probNum+id) | 部分 | 无status/priority索引，列表查询走全表扫描 |
| prob_restore | probId_serialNum_IDX(probId+serialNum) | 是 | 组合索引覆盖良好 |

---

## 三、性能优化建议

### 1. 高优先级优化项

| 优化项 | 影响范围 | 预期收益 | 实施建议 |
|--------|---------|---------|---------|
| pm_project_member增加组合索引(projectId, memberRole, effectiveFrom, effectiveTo) | 项目列表查询 | **极高** - 项目列表查询中4次LEFT JOIN此表 | 立即实施 |
| pm_project增加组合索引(effectiveTo, projectState, column001) | 项目列表查询 | **高** - WHERE条件核心过滤 | 立即实施 |
| fnd_user_menus增加组合索引(fnd_user_id, effectiveFrom, effectiveTo) | 权限查询 | 中 - 每次请求均查询 | 建议实施 |

### 2. 中优先级优化项

| 优化项 | 影响范围 | 预期收益 | 实施建议 |
|--------|---------|---------|---------|
| pm_project的column001~column014考虑提取为扩展属性表 | 项目表结构 | 中 - 减少主表列数，提高缓存效率 | 长期规划 |
| fnd_user_info的roleIds字段考虑拆分为关联表 | 用户角色查询 | 中 - 避免LIKE模糊查询 | 长期规划 |
| fnd_user_power的areapower字段考虑拆分为关联表 | 区域权限查询 | 中 - 避免FIND_IN_SET函数 | 长期规划 |

### 3. 查询优化建议

| 优化项 | 说明 |
|--------|------|
| 项目列表查询优化 | 考虑使用临时表分步查询，避免单次10+表JOIN |
| effectiveTo过滤优化 | 所有含effectiveTo的表建议建立索引，查询条件统一为 `effectiveTo IS NULL` 而非 `effectiveTo > NOW()` |
| customInfo JSON查询 | 避免在WHERE条件中对JSON字段进行过滤，JSON字段仅用于存储和返回 |
| 分页查询优化 | 使用覆盖索引+延迟关联方式优化深分页查询 |
| 缓存策略 | fnd_user_info使用iBatis的CopyLRU缓存模型（1小时过期），fnd_basic_data建议增加缓存 |

### 4. 数据量增长预估与索引策略

| 表 | 预估年增长 | 索引策略 |
|----|-----------|---------|
| pm_project | ~5000条/年 | projectCode_index为组合非唯一索引，关注projectCode查询效率 |
| pm_project_member | ~20000条/年 | 组合索引必须，effectiveTo过滤减少扫描行数 |
| pm_project_soft_version | ~50000条/年 | barCode索引关键，考虑分区 |
| pm_order_data_from_erp_* | ~100000条/年 | syncTime索引用于定期清理，考虑按时间分区 |
| prob_main | ~1000条/年 | 当前索引足够 |
| dp_act_unify_task | ~50000条/年 | latest索引减少扫描，考虑按state分区归档 |

### 5. 慢查询风险点

| 风险点 | SQL模式 | 原因 | 建议 |
|--------|---------|------|------|
| 项目列表多表JOIN | 10+表LEFT JOIN | pm_project_member被JOIN 4次 | 增加组合索引，考虑拆分查询 |
| FIND_IN_SET权限查询 | WHERE FIND_IN_SET(?, areapower) | 无法使用索引 | 拆分为关联表 |
| roleIds LIKE查询 | WHERE roleIds LIKE '%?%' | 前缀通配符无法使用索引 | 拆分为关联表 |
| customInfo JSON查询 | WHERE customInfo->'$.key' = ? | JSON函数查询无法使用索引 | 提取高频查询字段为独立列 |
| 临时表创建 | CREATE TEMPORARY TABLE | 频繁创建销毁 | 考虑使用CTE或预计算 |
