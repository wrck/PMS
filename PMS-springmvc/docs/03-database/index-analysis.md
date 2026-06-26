# PMS-springmvc 索引设计与性能分析

> 数据库：dppms_d365 (MySQL 8.0.16)
> 本文档基于 PMS-springmvc 模块 MyBatis Mapper XML 映射文件中的查询模式分析索引需求。

> ⚠️ **准确性说明**：本文档中的索引信息分为两部分：
> 1. **主键索引**：基于 Mapper XML 中的 `<id>` 标签确定，准确性高。
> 2. **推荐索引**：基于 SQL 查询模式分析得出的优化建议，**并非实际数据库索引**。
> 获取实际索引信息请执行：`SHOW INDEX FROM table_name;`

---

## 一、索引设计原则

PMS-springmvc 模块的索引设计遵循以下原则：

1. **主键索引**：所有表均使用自增 INT 主键，命名通常为 `id` 或 `表名+Id`（如 `taskId`、`projectId`、`empID`）。
2. **唯一索引**：业务编码字段（如 `projectCode`、`compCode`、`depCode`）建议建立唯一索引。
3. **外键索引**：虽无数据库外键约束，但关联字段（如 `projectId`、`dispatchId`、`assetId`）建议建立普通索引。
4. **逻辑删除索引**：`disabled` 字段频繁出现在 WHERE 条件中，建议纳入组合索引。
5. **时间范围索引**：`effectiveFrom`、`effectiveTo` 字段用于有效期判断，建议纳入组合索引。

---

## 二、项目管理表索引

### 2.1 pm_project（项目主表）

**Mapper**：`ProjectMapper.xml`、`ProjectHeaderMapper.xml`

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键（ProjectMapper 使用） |
| uk_projectCode | 唯一索引 | projectCode | 项目编码唯一（推荐） |

**查询模式分析：**

| 查询方法 | WHERE 条件 | 推荐索引 |
|---------|-----------|---------|
| selectByPrimaryKey | `id = ?` | PRIMARY |
| selectBySelective | `projectCode = ?` | uk_projectCode |
| selectBySelective | `projectState = ?` | idx_projectState |
| selectBySelective | `officeCode = ?` | idx_officeCode |
| selectBySelective | `projectType = ?` | idx_projectType |
| selectBySelective | `customerCode = ?` | idx_customerCode |
| selectBySelective | `compId = ?` | idx_compId |
| selectBySelectivePageable | `disabled = ? AND projectType IN (?) AND officeCode IN (?)` | idx_disabled_type_office (disabled, projectType, officeCode) |

**性能建议：**
- `projectCode` 是项目唯一标识，必须建立唯一索引。
- 列表查询常按 `projectType` + `officeCode` + `disabled` 过滤，建议建立组合索引。
- `customInfo` JSON 字段无法建索引，JSON 内部查询需全表扫描，建议将高频查询字段冗余到独立列。

---

### 2.2 pm_project_member（项目成员表）

**Mapper**：`ProjectMemberMapper.xml`

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| idx_projectId | 普通索引（推荐） | projectId | 按项目查询成员 |
| idx_memberCode | 普通索引（推荐） | memberCode | 按成员账号查询参与项目 |

**查询模式分析：**

| 查询方法 | WHERE 条件 | 推荐索引 |
|---------|-----------|---------|
| selectByPrimaryKey | `id = ?` | PRIMARY |
| selectBySelective | `projectId = ?` | idx_projectId |
| selectBySelective | `memberCode = ?` | idx_memberCode |
| selectBySelective | `memberRole = ?` | idx_member_role (memberCode, memberRole) |
| 日报权限查询 | `projectId = ? AND memberCode = ? AND effectiveFrom <= NOW() AND (effectiveTo > NOW() OR effectiveTo IS NULL)` | idx_project_member_effective (projectId, memberCode, effectiveFrom, effectiveTo) |

**性能建议：**
- 日报查询中频繁使用 `projectId` + `memberCode` + 有效期条件，建议建立组合索引。
- `memberRole` 常用于筛选特定角色成员（如项目经理），可纳入组合索引。

---

### 2.3 pm_project_task（项目任务表）

**Mapper**：`ProjectTaskMapper.xml`

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | taskId | 自增主键 |
| idx_projectId | 普通索引（推荐） | projectId | 按项目查询任务 |
| idx_parentId | 普通索引（推荐） | parentId | 按父任务查询子任务 |

**查询模式分析：**

| 查询方法 | WHERE 条件 | 推荐索引 |
|---------|-----------|---------|
| selectByPrimaryKey | `taskId = ?` | PRIMARY |
| selectBySelective | `projectId = ?` | idx_projectId |
| selectBySelective | `status = ?` | idx_status |
| selectBySelective | `parentId = ?` | idx_parentId |

**性能建议：**
- 任务列表查询常按 `projectId` + `status` 过滤，建议建立组合索引 `idx_project_status (projectId, status)`。
- `parentId` 用于构建任务树，建议建立索引。

---

### 2.4 pm_daily_report（日报表）

**Mapper**：`DailyReportMapper.xml`

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| idx_projectId | 普通索引（推荐） | projectId | 按项目查询日报 |
| idx_createBy | 普通索引（推荐） | createBy | 按创建人查询日报 |

**查询模式分析：**

| 查询方法 | WHERE 条件 | 推荐索引 |
|---------|-----------|---------|
| selectByPrimaryKey | `id = ?` | PRIMARY |
| selectBySelectivePageable | `disabled = ? AND projectType IN (?) AND officeCode IN (?)` | idx_disabled_type_office (disabled, projectType, officeCode) |
| selectBySelectivePageable | `projectId = ?` | idx_projectId |
| selectBySelectivePageable | `FIND_IN_SET(projectType, ?)` | 无法使用普通索引 |
| selectBySelectivePageable | `FIND_IN_SET(officeCode, ?)` | 无法使用普通索引 |
| selectBySelectivePageable | `customInfo->"$.serviceManagerCode" = ?` | 无法使用普通索引（JSON 查询） |
| checkPermission | `projectId = ?` | idx_projectId |

**性能建议：**
- 日报列表查询涉及复杂的权限控制（`FIND_IN_SET`、JSON 查询），这些查询无法使用普通索引，建议：
  1. 将 `FIND_IN_SET` 改为 `IN` 查询，应用层解析逗号分隔字符串。
  2. 将 `customInfo` 中的 `serviceManagerCode`、`programManagerCode` 冗余为独立列并建索引。
- `projectId` 是最常用的过滤字段，必须建立索引。
- `createBy` 用于按创建人查询日报，建议建立索引。

---

## 三、转包管理表索引

### 3.1 pm_dispatch_project_header（转包项目表）

**Mapper**：`DispatchProjectMapper.xml`

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| idx_dispatchSeq | 普通索引（推荐） | dispatchSeq | 按外派编号查询 |
| idx_facilitatorId | 普通索引（推荐） | facilitatorId | 按服务商查询 |
| idx_officeCode | 普通索引（推荐） | officeCode | 按办事处查询 |

**查询模式分析：**

| 查询方法 | WHERE 条件 | 推荐索引 |
|---------|-----------|---------|
| selectByPrimaryKey | `id = ?` | PRIMARY |
| selectBySelective | `dispatchSeq = ?` | idx_dispatchSeq |
| selectBySelective | `facilitatorId = ?` | idx_facilitatorId |
| selectBySelective | `officeCode = ?` | idx_officeCode |
| selectBySelective | `state = ?` | idx_state |
| selectBySelective | `disabled = ?` | idx_disabled |
| selectBySelectivePageable | `disabled = ? AND state = ? AND officeCode = ?` | idx_disabled_state_office (disabled, state, officeCode) |
| selectDispatchProjectVOList | `disabled = false` + 多条件 | idx_disabled |

**性能建议：**
- 列表查询常按 `disabled` + `state` + `officeCode` 过滤，建议建立组合索引。
- `dispatchSeq` 是业务编号，常用于查询，建议建立索引。
- `projectIds` 和 `contractNos` 是逗号分隔字符串，无法使用索引，关联查询需全表扫描。

---

### 3.2 pm_dispatch_project_settlement（转包结算表）

**Mapper**：`DispatchSettlementMapper.xml`

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| idx_dispatchId | 普通索引（推荐） | dispatchId | 按转包项目查询结算 |
| idx_dispatchSeq | 普通索引（推荐） | dispatchSeq | 按外派编号查询 |

**查询模式分析：**

| 查询方法 | WHERE 条件 | 推荐索引 |
|---------|-----------|---------|
| selectByPrimaryKey | `id = ?` | PRIMARY |
| selectBySelective | `dispatchId = ?` | idx_dispatchId |
| selectBySelective | `dispatchSeq = ?` | idx_dispatchSeq |
| selectBySelective | `state = ?` | idx_state |
| selectBySelective | `disabled = ?` | idx_disabled |
| selectBySelectivePageable | `disabled = ? AND dispatchId = ?` | idx_disabled_dispatch (disabled, dispatchId) |

**性能建议：**
- `dispatchId` 是最常用的关联字段，必须建立索引。
- 结算列表查询常按 `dispatchId` + `disabled` 过滤，建议建立组合索引。

---

### 3.3 pm_facilitator（服务商表）

**Mapper**：`FacilitatorMapper.xml`

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| uk_code | 唯一索引（推荐） | code | 服务商编码唯一 |
| idx_account | 普通索引（推荐） | account | 按账号查询 |

**查询模式分析：**

| 查询方法 | WHERE 条件 | 推荐索引 |
|---------|-----------|---------|
| selectByPrimaryKey | `id = ?` | PRIMARY |
| selectBySelective | `code = ?` | uk_code |
| selectBySelective | `account = ?` | idx_account |
| selectBySelective | `state = ?` | idx_state |
| selectBySelective | `disabled = ?` | idx_disabled |

**性能建议：**
- `code` 是服务商唯一编码，建议建立唯一索引。
- D365 同步时通过 `account` 匹配更新，建议建立索引。

---

## 四、行业资产表索引

### 4.1 af_industry_asset（行业资产表）

**Mapper**：`IndustryAssetMapper.xml`

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| idx_assetNum | 普通索引（推荐） | assetNum | 按资产编号查询 |
| idx_industryCode | 普通索引（推荐） | industryCode | 按行业查询 |

**查询模式分析：**

| 查询方法 | WHERE 条件 | 推荐索引 |
|---------|-----------|---------|
| selectByPrimaryKey | `id = ?` | PRIMARY |
| selectBySelective | `assetNum = ?` | idx_assetNum |
| selectBySelective | `industryCode = ?` | idx_industryCode |
| selectBySelective | `status = ?` | idx_status |
| selectBySelective | `trackStatus = ?` | idx_trackStatus |
| selectBySelective | `disabled = ?` | idx_disabled |
| selectBySelectivePageable | `disabled = ? AND projectTypes IN (?) AND officeCodes IN (?)` | idx_disabled (disabled) |

**性能建议：**
- `assetNum` 是资产唯一编号，建议建立索引。
- 列表查询常按 `disabled` + `industryCode` 过滤，建议建立组合索引。

---

### 4.2 af_industry_leak（行业漏洞表）

**Mapper**：`IndustryLeakMapper.xml`

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| idx_leakCode | 普通索引（推荐） | leakCode | 按漏洞编码查询 |
| idx_industryCode | 普通索引（推荐） | industryCode | 按行业查询 |

**查询模式分析：**

| 查询方法 | WHERE 条件 | 推荐索引 |
|---------|-----------|---------|
| selectByPrimaryKey | `id = ?` | PRIMARY |
| selectBySelective | `leakCode = ?` | idx_leakCode |
| selectBySelective | `industryCode = ?` | idx_industryCode |
| selectBySelective | `status = ?` | idx_status |
| selectBySelective | `disabled = ?` | idx_disabled |

---

### 4.3 af_industry_asset_project_relation（资产-项目关联表）

**Mapper**：`IndustryAssetProjectRelationMapper.xml`

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| idx_assetId | 普通索引（推荐） | assetId | 按资产查询关联项目 |
| idx_projectId | 普通索引（推荐） | projectId | 按项目查询关联资产 |

**查询模式分析：**

| 查询方法 | WHERE 条件 | 推荐索引 |
|---------|-----------|---------|
| selectByPrimaryKey | `id = ?` | PRIMARY |
| selectBySelective | `assetId = ?` | idx_assetId |
| selectBySelective | `projectId = ?` | idx_projectId |
| selectBySelective | `disabled = ?` | idx_disabled |

**性能建议：**
- `assetId` 和 `projectId` 是双向查询的常用字段，建议都建立索引。
- 可考虑建立唯一组合索引 `uk_asset_project (assetId, projectId, disabled)` 防止重复关联。

---

### 4.4 af_industry_asset_leak_relation（资产-漏洞-项目关联表）

**Mapper**：`IndustryAssetLeakRelationMapper.xml`

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| idx_assetId | 普通索引（推荐） | assetId | 按资产查询 |
| idx_leakId | 普通索引（推荐） | leakId | 按漏洞查询 |
| idx_projectId | 普通索引（推荐） | projectId | 按项目查询 |

**查询模式分析：**

| 查询方法 | WHERE 条件 | 推荐索引 |
|---------|-----------|---------|
| selectByPrimaryKey | `id = ?` | PRIMARY |
| selectBySelective | `assetId = ?` | idx_assetId |
| selectBySelective | `leakId = ?` | idx_leakId |
| selectBySelective | `projectId = ?` | idx_projectId |

---

## 五、工作流表索引

### 5.1 pm_workflow（工作流业务表）

**Mapper**：`PmWorkFlowMapper.xml`

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| idx_procInstId | 普通索引（推荐） | procInstId | 按流程实例ID查询 |
| idx_dataType_dataId | 普通索引（推荐） | dataType, dataId | 按业务数据查询审批流程 |
| idx_status | 普通索引（推荐） | status | 按状态查询 |

**查询模式分析：**

| 查询方法 | WHERE 条件 | 推荐索引 |
|---------|-----------|---------|
| selectByPrimaryKey | `id = ?` | PRIMARY |
| selectBySelective | `procInstId = ?` | idx_procInstId |
| selectBySelective | `dataType = ? AND dataId = ?` | idx_dataType_dataId |
| selectBySelective | `status = ?` | idx_status |
| selectBySelective | `userId = ? AND status = ?` | idx_user_status (userId, status) |
| terminateProcess | `dataId = ? AND dataType = ? AND status = ?` | idx_dataType_dataId_status (dataType, dataId, status) |

**性能建议：**
- `dataType` + `dataId` 是多态关联的常用查询条件，必须建立组合索引。
- `procInstId` 关联到 Activiti 引擎，常用于流程回调时查询业务数据，建议建立索引。
- `status = 'PENDING'` 是高频查询条件（查询待办），建议纳入组合索引。

---

### 5.2 Activiti 引擎表（ACT_*）

**说明**：Activiti 引擎表由引擎自动管理索引，无需手动创建。以下是关键表的已有索引（由 Activiti DDL 创建）：

| 表名 | 索引字段 | 说明 |
|------|---------|------|
| ACT_RU_TASK | EXECUTION_ID_ | 执行实例ID |
| ACT_RU_TASK | PROC_DEF_ID_ | 流程定义ID |
| ACT_RU_TASK | ASSIGNEE_ | 办理人 |
| ACT_RU_TASK | TASK_DEF_KEY_ | 任务定义Key |
| ACT_RU_IDENTITYLINK | TASK_ID_ | 任务ID |
| ACT_RU_IDENTITYLINK | USER_ID_ | 用户ID |
| ACT_RU_IDENTITYLINK | GROUP_ID_ | 组ID |
| ACT_RU_VARIABLE | TASK_ID_ | 任务ID |
| ACT_RU_VARIABLE | EXECUTION_ID_ | 执行ID |
| ACT_RU_VARIABLE | NAME_ | 变量名 |

**性能建议：**
- `PmWorkBenchMapper` 中的待办查询涉及多表 JOIN（`ACT_RU_TASK` + `ACT_RU_IDENTITYLINK` + `ACT_RE_PROCDEF` + `pm_workflow`），查询复杂度高。
- `FIND_IN_SET(D.KEY_, ?)` 条件无法使用索引，建议改为 `IN` 查询。
- 待办查询结果缓存到 Redis 可大幅降低数据库压力。

---

## 六、EHR 表索引

### 6.1 ehr_company（公司表）

**Mapper**：`EhrCompanyMapper.xml`

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | compID | 自增主键 |
| uk_compCode | 唯一索引（推荐） | compCode | 公司编码唯一 |

### 6.2 ehr_department（部门表）

**Mapper**：`EhrDepartmentMapper.xml`

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | depID | 自增主键 |
| uk_depCode | 唯一索引（推荐） | depCode | 部门编码唯一 |
| idx_compID | 普通索引（推荐） | compID | 按公司查询部门 |

### 6.3 ehr_employee（员工表）

**Mapper**：`EmployeeMapper.xml`

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | empID | 自增主键 |
| uk_workNo | 唯一索引（推荐） | workNo | 工号唯一 |
| idx_compID | 普通索引（推荐） | compID | 按公司查询员工 |
| idx_depID | 普通索引（推荐） | depID | 按部门查询员工 |

**查询模式分析：**

| 查询方法 | WHERE 条件 | 推荐索引 |
|---------|-----------|---------|
| selectByPrimaryKey | `empID = ?` | PRIMARY |
| selectBySelective | `workNo = ?` | uk_workNo |
| selectBySelective | `compID = ?` | idx_compID |
| selectBySelective | `depID = ?` | idx_depID |
| selectBySelective | `empStatus = ?` | idx_empStatus |
| 树形查询 | `depID = ? AND empStatus = 1` | idx_dep_status (depID, empStatus) |

---

## 七、通用表索引

### 7.1 pm_common_related_data（通用关联数据表）

**Mapper**：`CommonRelatedDataMapper.xml`

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| idx_objType_objId | 普通索引（推荐） | objType, objId | 按对象查询关联数据 |
| idx_type | 普通索引（推荐） | type | 按关联类型查询 |

**性能建议：**
- `objType` + `objId` 是多态关联的常用查询条件，必须建立组合索引。

---

### 7.2 data_field_relation（动态字段表）

**Mapper**：`DataFieldRelationMapper.xml`

| 索引名称 | 索引类型 | 索引字段 | 说明 |
|---------|---------|---------|------|
| PRIMARY | 主键索引 | id | 自增主键 |
| idx_dataName_dataType | 普通索引（推荐） | dataName, dataType | 按数据名和类型查询字段配置 |
| idx_dataId | 普通索引（推荐） | dataId | 按数据ID查询 |

**查询模式分析：**

| 查询方法 | WHERE 条件 | 推荐索引 |
|---------|-----------|---------|
| selectByPrimaryKey | `id = ?` | PRIMARY |
| selectBySelective | `dataName = ? AND dataType = ?` | idx_dataName_dataType |
| selectBySelective | `dataId = ?` | idx_dataId |
| findColumnList | `dataName = ? AND dataType = 'table' AND disabled = 0` | idx_dataName_type_disabled (dataName, dataType, disabled) |
| findFieldList | `dataName = ? AND dataType = 'form' AND disabled = 0` | idx_dataName_type_disabled |

**性能建议：**
- `dataName` + `dataType` + `disabled` 是列表/表单字段配置的高频查询条件，建议建立组合索引。
- 此表数据量通常不大（每个业务对象几十到几百条字段配置），索引优化收益有限。

---

## 八、索引优化建议汇总

### 8.1 高优先级索引（必须建立）

| 表 | 索引字段 | 索引类型 | 原因 |
|----|---------|---------|------|
| pm_project | projectCode | 唯一索引 | 业务唯一标识，频繁查询 |
| pm_project_member | projectId | 普通索引 | 按项目查询成员 |
| pm_project_task | projectId | 普通索引 | 按项目查询任务 |
| pm_daily_report | projectId | 普通索引 | 按项目查询日报 |
| pm_dispatch_project_header | dispatchSeq | 普通索引 | 业务编号查询 |
| pm_dispatch_project_settlement | dispatchId | 普通索引 | 按转包项目查询结算 |
| pm_workflow | dataType, dataId | 组合索引 | 多态关联查询 |
| pm_workflow | procInstId | 普通索引 | 流程回调查询 |
| af_industry_asset_project_relation | assetId, projectId | 普通索引 | 双向关联查询 |

### 8.2 中优先级索引（建议建立）

| 表 | 索引字段 | 索引类型 | 原因 |
|----|---------|---------|------|
| pm_project | projectType, officeCode, disabled | 组合索引 | 列表查询优化 |
| pm_project_member | memberCode | 普通索引 | 按成员查询参与项目 |
| pm_daily_report | createBy | 普通索引 | 按创建人查询 |
| pm_dispatch_project_header | facilitatorId | 普通索引 | 按服务商查询 |
| pm_facilitator | code | 唯一索引 | 业务唯一标识 |
| pm_workflow | status | 普通索引 | 待办查询优化 |

### 8.3 查询优化建议

#### 8.3.1 避免 FIND_IN_SET 查询

**问题**：`FIND_IN_SET(field, 'a,b,c')` 无法使用索引，导致全表扫描。

**示例**（DailyReportMapper.xml）：
```sql
FIND_IN_SET(dr.`projectType`, CONCAT(#{model.projectTypes}, ",30,40"))
FIND_IN_SET(dr.`officeCode`, #{model.officeCodes})
```

**建议**：应用层解析逗号分隔字符串，改为 `IN` 查询：
```sql
dr.`projectType` IN ('a', 'b', 'c', '30', '40')
dr.`officeCode` IN ('a', 'b', 'c')
```

#### 8.3.2 JSON 字段查询优化

**问题**：`customInfo->"$.serviceManagerCode" = ?` 无法使用普通索引。

**建议**：
1. 将高频查询的 JSON 字段冗余为独立列并建索引。
2. MySQL 8.0+ 支持生成列（Generated Column）+ 索引：
   ```sql
   ALTER TABLE pm_daily_report 
   ADD COLUMN serviceManagerCode VARCHAR(50) 
   GENERATED ALWAYS AS (JSON_UNQUOTE(JSON_EXTRACT(customInfo, '$.serviceManagerCode'))) STORED,
   ADD INDEX idx_serviceManagerCode (serviceManagerCode);
   ```

#### 8.3.3 逗号分隔字符串字段优化

**问题**：`pm_dispatch_project_header.projectIds` 存储逗号分隔的项目ID，无法使用索引关联查询。

**建议**：建立中间关联表 `pm_dispatch_project_relation`：
```sql
CREATE TABLE pm_dispatch_project_relation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dispatchId INT NOT NULL COMMENT '转包项目ID',
    projectId INT NOT NULL COMMENT '关联项目ID',
    disabled BIT(1) DEFAULT b'0',
    INDEX idx_dispatchId (dispatchId),
    INDEX idx_projectId (projectId)
);
```

#### 8.3.4 分页查询优化

**问题**：`LIMIT offset, pageSize` 在 offset 较大时性能差。

**建议**：
1. 使用游标分页（基于主键 `WHERE id > last_id LIMIT pageSize`）。
2. 避免深分页（限制最大页数）。
3. 列表查询避免 `SELECT *`，只查询必要字段。

---

## 九、索引维护建议

### 9.1 索引数量控制

- 单表索引数量建议不超过 5 个。
- 组合索引字段数建议不超过 4 个。
- 避免在低基数列（如 `disabled` 只有 0/1）上单独建索引，应纳入组合索引。

### 9.2 索引监控

定期执行以下 SQL 监控索引使用情况：

```sql
-- 查看未使用的索引
SELECT * FROM sys.schema_unused_indexes 
WHERE object_schema = 'dppms_d365';

-- 查看索引大小
SELECT 
    table_name,
    index_name,
    ROUND(stat_value * @@innodb_page_size / 1024 / 1024, 2) AS size_mb
FROM mysql.innodb_index_stats
WHERE database_name = 'dppms_d365'
AND stat_name = 'size'
ORDER BY size_mb DESC;

-- 查看慢查询
SELECT * FROM mysql.slow_log 
WHERE db = 'dppms_d365' 
ORDER BY start_time DESC 
LIMIT 100;
```

### 9.3 索引更新策略

- **批量导入**：大量数据导入前先删除索引，导入后重建。
- **定时任务**：`D365DataJob`、`SMSDataJob` 同步数据时，避免在同步过程中产生大量索引更新，可考虑临时禁用非关键索引。
- `pm_facilitator_form_d365` 等同步暂存表使用 `TRUNCATE` 清空后重写，无需维护索引。
