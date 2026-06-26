# PMS-springmvc DAO-SQL 映射参考文档

> 本文档基于 MyBatis Mapper 接口与 XML 映射文件，梳理每个 DAO 方法对应的 SQL ID、SQL 类型及操作表。
> PMS-springmvc 使用 MyBatis 3.5.9，Mapper 接口继承 `AbstractBaseMapper<T>` 通用基类。

---

## 一、Mapper 映射文件与 DAO 对应关系总览

| SQL 映射文件 | 业务模块 | 对应 DAO 接口 | 操作表 |
|---|---|---|---|
| ProjectMapper.xml | 项目管理 | ProjectMapper | pm_project |
| ProjectHeaderMapper.xml | 项目头信息 | ProjectHeaderMapper | pm_project |
| ProjectMemberMapper.xml | 项目成员 | ProjectMemberMapper | pm_project_member |
| ProjectTaskMapper.xml | 项目任务 | ProjectTaskMapper | pm_project_task |
| DailyReportMapper.xml | 日报管理 | DailyReportMapper | pm_daily_report |
| DispatchProjectMapper.xml | 转包项目 | DispatchProjectMapper | pm_dispatch_project_header |
| DispatchSettlementMapper.xml | 转包结算 | DispatchSettlementMapper | pm_dispatch_project_settlement |
| FacilitatorMapper.xml | 服务商管理 | FacilitatorMapper | pm_facilitator |
| IndustryAssetMapper.xml | 行业资产 | IndustryAssetMapper | af_industry_asset |
| IndustryLeakMapper.xml | 行业漏洞 | IndustryLeakMapper | af_industry_leak |
| IndustryLeakWarningMapper.xml | 漏洞预警 | IndustryLeakWarningMapper | af_industry_leak_warning |
| IndustryAssetProjectRelationMapper.xml | 资产-项目关联 | IndustryAssetProjectRelationMapper | af_industry_asset_project_relation |
| IndustryAssetLeakRelationMapper.xml | 资产-漏洞关联 | IndustryAssetLeakRelationMapper | af_industry_asset_leak_relation |
| PmWorkFlowMapper.xml | 工作流 | PmWorkFlowMapper | pm_workflow |
| PmWorkBenchMapper.xml | 工作台（待办） | PmWorkBenchMapper | ACT_RU_TASK 等 Activiti 表 |
| CommonRelatedDataMapper.xml | 通用关联数据 | CommonRelatedDataMapper | pm_common_related_data |
| DataFieldRelationMapper.xml | 动态字段配置 | DataFieldRelationMapper | data_field_relation |
| ProjectManageUserMapper.xml | 项目管理用户 | ProjectManageUserMapper | t_user, t_user_info |
| PmSynchronizeMapper.xml | 数据同步 | PmSynchronizeMapper | 多表（同步暂存表） |
| ExcelAnalysisMapper.xml | Excel 导入 | ExcelAnalysisMapper | 临时表 + 业务表 |
| EhrCompanyMapper.xml | EHR 公司 | EhrCompanyMapper | ehr_company |
| EhrDepartmentMapper.xml | EHR 部门 | EhrDepartmentMapper | ehr_department |
| EmployeeMapper.xml | EHR 员工 | EmployeeMapper | ehr_employee |
| JobMapper.xml | EHR 职位 | JobMapper | ehr_job |
| HolidayMapper.xml | EHR 假期 | HolidayMapper | ehr_holiday |
| EHRLoginAccountMapper.xml | EHR 登录账号 | EHRLoginAccountMapper | ehr_login_account |
| EhrEmpPowerMapper.xml | EHR 员工权限 | EhrEmpPowerMapper | ehr_emp_power |
| EhrSynchronizeMapper.xml | EHR 数据同步 | EhrSynchronizeMapper | 多表（EHR 暂存表） |

---

## 二、AbstractBaseMapper 通用方法

所有继承 `AbstractBaseMapper<T>` 的 Mapper 接口均自动拥有以下 10 个通用方法：

| DAO 方法 | SQL ID | SQL 类型 | 说明 |
|---|---|---|---|
| selectByPrimaryKey | selectByPrimaryKey | SELECT | 按主键查询 |
| deleteByPrimaryKey | deleteByPrimaryKey | DELETE | 按主键删除 |
| insert | insert | INSERT | 全字段插入 |
| insertSelective | insertSelective | INSERT | 选择性插入（null 字段不插入） |
| updateByPrimaryKeySelective | updateByPrimaryKeySelective | UPDATE | 选择性更新（null 字段不更新） |
| updateByPrimaryKey | updateByPrimaryKey | UPDATE | 全字段更新 |
| selectBySelective | selectBySelective | SELECT | 按实体非空字段条件查询 |
| countBySelective | countBySelective | SELECT COUNT | 按实体非空字段条件计数 |
| selectBySelectivePageable | selectBySelectivePageable | SELECT | 分页查询（支持排序、关联查询） |
| countBySelectivePageable | countBySelectivePageable | SELECT COUNT | 分页查询总数 |

> **说明**：`selectBySelective` 和 `selectBySelectivePageable` 的区别在于后者支持 `PageParam` 参数（包含分页信息、排序信息、权限控制等），且通常包含 JOIN 关联查询。

---

## 三、ProjectMapper（项目管理）

**接口路径**：`com.dp.plat.pms.springmvc.dao.ProjectMapper`
**SQL 映射文件**：`ProjectMapper.xml`
**操作表**：`pm_project`

### 3.1 通用方法

继承 `AbstractBaseMapper<Project>` 的 10 个通用方法，操作 `pm_project` 表。

| DAO 方法 | SQL ID | SQL 类型 | 操作表 | 说明 |
|---|---|---|---|---|
| selectByPrimaryKey | selectByPrimaryKey | SELECT | pm_project | WHERE id = ? |
| deleteByPrimaryKey | deleteByPrimaryKey | DELETE | pm_project | WHERE id = ? |
| insert | insert | INSERT | pm_project | 全字段插入，SELECT LAST_INSERT_ID() |
| insertSelective | insertSelective | INSERT | pm_project | 选择性插入 |
| updateByPrimaryKeySelective | updateByPrimaryKeySelective | UPDATE | pm_project | 选择性更新 WHERE id = ? |
| updateByPrimaryKey | updateByPrimaryKey | UPDATE | pm_project | 全字段更新 WHERE id = ? |
| selectBySelective | selectBySelective | SELECT | pm_project | 按实体非空字段查询 |
| countBySelective | countBySelective | SELECT COUNT | pm_project | 按实体非空字段计数 |
| selectBySelectivePageable | selectBySelectivePageable | SELECT | pm_project | 分页查询 |
| countBySelectivePageable | countBySelectivePageable | SELECT COUNT | pm_project | 分页计数 |

---

## 四、ProjectHeaderMapper（项目头信息）

**接口路径**：`com.dp.plat.pms.springmvc.dao.ProjectHeaderMapper`
**SQL 映射文件**：`ProjectHeaderMapper.xml`
**操作表**：`pm_project`（使用 `projectId` 作为主键名）

### 4.1 通用方法

继承 `AbstractBaseMapper<ProjectHeader>` 的 10 个通用方法，主键字段为 `projectId`。

### 4.2 扩展方法

| DAO 方法 | SQL ID | SQL 类型 | 操作表 | 说明 |
|---|---|---|---|---|
| countUncreateProjectList | countUncreateProjectList | SELECT COUNT | pm_project + 关联表 | 查询未创建项目数量（来自 SMS 同步数据） |
| selectUncreateProjectList | selectUncreateProjectList | SELECT | pm_project + 关联表 | 分页查询未创建项目列表 |
| queryProjectByContractNoAndType | queryProjectByContractNoAndType | SELECT | pm_project | 按合同号和项目类型查询项目 |
| checkPermission | checkPermission | SELECT | pm_project + t_user_info | 检查用户对项目的权限 |
| selectVOByProjectId | selectVOByProjectId | SELECT | pm_project | 按项目ID查询 ProjectVO（含扩展信息） |
| queryProjectStateByProjectId | queryProjectStateByProjectId | SELECT | pm_project | 查询项目状态 |
| queryProductInfoFromSmsByProjectCode | queryProductInfoFromSmsByProjectCode | SELECT | pm_project_product_af_from_sms | 按 SMS 同步数据查询产品信息 |
| insertMergeContract | insertMergeContract | INSERT | pm_project | 合同合并插入 |
| selectAllProjectRelateInfos | selectAllProjectRelateInfos | SELECT | information_schema | 查询所有项目相关表的结构信息 |
| executeSql | executeSql | DDL/DML | 动态 | 执行动态 SQL（项目数据迁移） |

---

## 五、ProjectMemberMapper（项目成员）

**接口路径**：`com.dp.plat.pms.springmvc.dao.ProjectMemberMapper`
**SQL 映射文件**：`ProjectMemberMapper.xml`
**操作表**：`pm_project_member`

### 5.1 通用方法

继承 `AbstractBaseMapper<ProjectMember>` 的 10 个通用方法，操作 `pm_project_member` 表。

---

## 六、ProjectTaskMapper（项目任务）

**接口路径**：`com.dp.plat.pms.springmvc.dao.ProjectTaskMapper`
**SQL 映射文件**：`ProjectTaskMapper.xml`
**操作表**：`pm_project_task`

### 6.1 通用方法

继承 `AbstractBaseMapper<ProjectTask>` 的 10 个通用方法，主键字段为 `taskId`。

### 6.2 扩展方法

| DAO 方法 | SQL ID | SQL 类型 | 操作表 | 说明 |
|---|---|---|---|---|
| updateByPrimaryKeyWithBLOBs | updateByPrimaryKeyWithBLOBs | UPDATE | pm_project_task | 含 BLOB 字段（remark）更新 |
| selectProjectDeliverBySelective | selectProjectDeliverBySelective | SELECT | pm_project_task | 查询项目交付件列表 |
| checkPermission | checkPermission | SELECT | pm_project_task + t_user_info | 检查用户对任务的权限 |
| updateEventActualFinishDateByTask | updateEventActualFinishDateByTask | UPDATE | pm_project_task | 更新任务实际完成时间 |

---

## 七、DailyReportMapper（日报管理）

**接口路径**：`com.dp.plat.pms.springmvc.dao.DailyReportMapper`
**SQL 映射文件**：`DailyReportMapper.xml`
**操作表**：`pm_daily_report`

### 7.1 通用方法

继承 `AbstractBaseMapper<DailyReport>` 的 10 个通用方法。

### 7.2 扩展方法

| DAO 方法 | SQL ID | SQL 类型 | 操作表 | 说明 |
|---|---|---|---|---|
| updateByPrimaryKeyWithBLOBs | updateByPrimaryKeyWithBLOBs | UPDATE | pm_daily_report | 含 BLOB 字段更新 |
| checkPermission | checkPermission | SELECT | pm_daily_report + pm_project + t_user_info | 检查用户对日报的权限（含项目权限继承） |

**checkPermission 查询逻辑**：
```sql
-- 检查日报所属项目的 disabled 状态
SELECT IFNULL(ph.disabled, 
    (SELECT IF(disabled, 1, 0) FROM pm_project WHERE projectId = #{model.projectId} LIMIT 1)
) AS disabled
FROM pm_daily_report dr
LEFT JOIN pm_project ph ON dr.projectId = ph.projectId
WHERE dr.id = #{model.id}
```

---

## 八、DispatchProjectMapper（转包项目）

**接口路径**：`com.dp.plat.pms.springmvc.dao.DispatchProjectMapper`
**SQL 映射文件**：`DispatchProjectMapper.xml`
**操作表**：`pm_dispatch_project_header`

### 8.1 通用方法

继承 `AbstractBaseMapper<DispatchProject>` 的 10 个通用方法。

### 8.2 扩展方法

| DAO 方法 | SQL ID | SQL 类型 | 操作表 | 说明 |
|---|---|---|---|---|
| insertOrUpdateSelective | insertOrUpdateSelective | INSERT/UPDATE | pm_dispatch_project_header | 存在则更新，不存在则插入 |
| selectDispatchProjectVOList | selectDispatchProjectVOList | SELECT | pm_dispatch_project_header + 关联表 | 查询转包项目 VO 列表（含结算信息） |
| countDispatchProjectVOList | countDispatchProjectVOList | SELECT COUNT | pm_dispatch_project_header | 转包项目 VO 列表计数 |
| selectDispatchVOWithAmountById | selectDispatchVOWithAmountById | SELECT | pm_dispatch_project_header + pm_dispatch_project_settlement | 查询转包项目详情（含结算金额汇总） |
| selectDispatchVOWithAmountBySelective | selectDispatchVOWithAmountBySelective | SELECT | pm_dispatch_project_header + pm_dispatch_project_settlement | 按条件查询转包项目（含金额） |
| selectDispatchVOWithAmountBySelectivePageable | selectDispatchVOWithAmountBySelectivePageable | SELECT | pm_dispatch_project_header + pm_dispatch_project_settlement | 分页查询转包项目（含金额） |

**selectBySelectivePageable 关联查询**：
```sql
SELECT h.*, d.departmentName AS officeName, d1.departmentName AS profitDepName,
    bd.basicDataName AS stateName, bd1.basicDataName AS callbackStateName,
    bd2.basicDataName AS typeName, CONCAT(h.createBy, '-', ui.realName) AS createName
FROM pm_dispatch_project_header h
LEFT JOIN fnd_department d ON h.officeCode = d.departmentNum
LEFT JOIN fnd_department d1 ON h.profitDepCode = d1.departmentNum
LEFT JOIN fnd_basic_data bd ON h.state = bd.basicDataId AND bd.dataTypeCode = 'dispatchState'
LEFT JOIN fnd_basic_data bd1 ON h.callbackState = bd1.basicDataId AND bd1.dataTypeCode = 'dispatchCbState'
LEFT JOIN fnd_basic_data bd2 ON h.type = bd2.basicDataId AND bd2.dataTypeCode = 'dispatchType'
LEFT JOIN t_user u ON u.user_name = h.createBy
LEFT JOIN t_user_info ui ON ui.user_id = u.user_id
```

---

## 九、DispatchSettlementMapper（转包结算）

**接口路径**：`com.dp.plat.pms.springmvc.dao.DispatchSettlementMapper`
**SQL 映射文件**：`DispatchSettlementMapper.xml`
**操作表**：`pm_dispatch_project_settlement`

### 9.1 通用方法

继承 `AbstractBaseMapper<DispatchSettlement>` 的 10 个通用方法。

### 9.2 扩展方法

| DAO 方法 | SQL ID | SQL 类型 | 操作表 | 说明 |
|---|---|---|---|---|
| countSettlementWidthDispatchPageable | countSettlementWidthDispatchPageable | SELECT COUNT | pm_dispatch_project_settlement + pm_dispatch_project_header | 分页查询结算列表计数（含转包项目信息） |
| selectSettlementWidthDispatchPageable | selectSettlementWidthDispatchPageable | SELECT | pm_dispatch_project_settlement + pm_dispatch_project_header | 分页查询结算列表（含转包项目信息） |
| querySSEDispatchSettlementPaymentList | querySSEDispatchSettlementPaymentList | SELECT | pm_dispatch_project_settlement | 查询需要同步到 SEE 系统的结算付款列表 |

---

## 十、FacilitatorMapper（服务商管理）

**接口路径**：`com.dp.plat.pms.springmvc.dao.FacilitatorMapper`
**SQL 映射文件**：`FacilitatorMapper.xml`
**操作表**：`pm_facilitator`

### 10.1 通用方法

继承 `AbstractBaseMapper<Facilitator>` 的 10 个通用方法。

---

## 十一、IndustryAssetMapper（行业资产）

**接口路径**：`com.dp.plat.pms.springmvc.dao.IndustryAssetMapper`
**SQL 映射文件**：`IndustryAssetMapper.xml`
**操作表**：`af_industry_asset`

### 11.1 通用方法

继承 `AbstractBaseMapper<IndustryAsset>` 的 10 个通用方法。

### 11.2 扩展方法

| DAO 方法 | SQL ID | SQL 类型 | 操作表 | 说明 |
|---|---|---|---|---|
| selectProjectAssetBySelectivePageable | selectProjectAssetBySelectivePageable | SELECT | af_industry_asset + af_industry_asset_project_relation | 分页查询项目关联的资产 |
| countProjectAssetBySelectivePageable | countProjectAssetBySelectivePageable | SELECT COUNT | af_industry_asset + af_industry_asset_project_relation | 项目关联资产计数 |

---

## 十二、IndustryLeakMapper（行业漏洞）

**接口路径**：`com.dp.plat.pms.springmvc.dao.IndustryLeakMapper`
**SQL 映射文件**：`IndustryLeakMapper.xml`
**操作表**：`af_industry_leak`

### 12.1 通用方法

继承 `AbstractBaseMapper<IndustryLeak>` 的 10 个通用方法。

### 12.2 扩展方法

| DAO 方法 | SQL ID | SQL 类型 | 操作表 | 说明 |
|---|---|---|---|---|
| updateByPrimaryKeyWithBLOBs | updateByPrimaryKeyWithBLOBs | UPDATE | af_industry_leak | 含 BLOB 字段更新 |
| createTempTable | createTempTable | DDL | 临时表 | 创建 Excel 导入临时表 |
| insertTempData | insertTempData | INSERT | 临时表 | 批量插入临时表数据 |
| selectTempData | selectTempData | SELECT | 临时表 | 分页查询临时表数据 |
| countTempData | countTempData | SELECT COUNT | 临时表 | 临时表数据计数 |
| dropTempTable | dropTempTable | DDL | 临时表 | 删除临时表 |

---

## 十三、IndustryLeakWarningMapper（漏洞预警）

**接口路径**：`com.dp.plat.pms.springmvc.dao.IndustryLeakWarningMapper`
**SQL 映射文件**：`IndustryLeakWarningMapper.xml`
**操作表**：`af_industry_leak_warning`

### 13.1 通用方法

继承 `AbstractBaseMapper<IndustryLeakWarning>` 的 10 个通用方法。

### 13.2 扩展方法

| DAO 方法 | SQL ID | SQL 类型 | 操作表 | 说明 |
|---|---|---|---|---|
| selectWarningAssetBySelectivePageable | selectWarningAssetBySelectivePageable | SELECT | af_industry_leak_warning + af_industry_asset | 分页查询预警资产列表 |
| countWarningAssetBySelectivePageable | countWarningAssetBySelectivePageable | SELECT COUNT | af_industry_leak_warning + af_industry_asset | 预警资产计数 |

---

## 十四、IndustryAssetProjectRelationMapper（资产-项目关联）

**接口路径**：`com.dp.plat.pms.springmvc.dao.IndustryAssetProjectRelationMapper`
**SQL 映射文件**：`IndustryAssetProjectRelationMapper.xml`
**操作表**：`af_industry_asset_project_relation`

### 14.1 通用方法

继承 `AbstractBaseMapper<IndustryAssetProjectRelation>` 的 10 个通用方法。

### 14.2 扩展方法

| DAO 方法 | SQL ID | SQL 类型 | 操作表 | 说明 |
|---|---|---|---|---|
| invalidAssetProjectRelation | invalidAssetProjectRelation | UPDATE | af_industry_asset_project_relation | 失效资产-项目关联（设置 effectiveTo） |
| selectProjectAssetBySelectivePageable | selectProjectAssetBySelectivePageable | SELECT | af_industry_asset_project_relation + af_industry_asset | 分页查询项目关联资产 |
| countProjectAssetBySelectivePageable | countProjectAssetBySelectivePageable | SELECT COUNT | af_industry_asset_project_relation + af_industry_asset | 项目关联资产计数 |

---

## 十五、IndustryAssetLeakRelationMapper（资产-漏洞关联）

**接口路径**：`com.dp.plat.pms.springmvc.dao.IndustryAssetLeakRelationMapper`
**SQL 映射文件**：`IndustryAssetLeakRelationMapper.xml`
**操作表**：`af_industry_asset_leak_relation`

### 15.1 通用方法

继承 `AbstractBaseMapper<IndustryAssetLeakRelation>` 的 10 个通用方法。

### 15.2 扩展方法

| DAO 方法 | SQL ID | SQL 类型 | 操作表 | 说明 |
|---|---|---|---|---|
| countProjectAssetLeakBySelectivePageable | countProjectAssetLeakBySelectivePageable | SELECT COUNT | af_industry_asset_leak_relation + 多表 | 分页查询项目资产漏洞计数 |
| selectProjectAssetLeakBySelectivePageable | selectProjectAssetLeakBySelectivePageable | SELECT | af_industry_asset_leak_relation + 多表 | 分页查询项目资产漏洞列表 |
| invalidProjectAssetLeakRelation | invalidProjectAssetLeakRelation | UPDATE | af_industry_asset_leak_relation | 失效资产-漏洞-项目关联 |

---

## 十六、PmWorkFlowMapper（工作流）

**接口路径**：`com.dp.plat.pms.springmvc.dao.PmWorkFlowMapper`
**SQL 映射文件**：`PmWorkFlowMapper.xml`
**操作表**：`pm_workflow`

### 16.1 通用方法

继承 `AbstractBaseMapper<PmWorkFlow>` 的 10 个通用方法。

### 16.2 扩展方法

| DAO 方法 | SQL ID | SQL 类型 | 操作表 | 说明 |
|---|---|---|---|---|
| selectProcInstIdsBySelective | selectProcInstIdsBySelective | SELECT | pm_workflow | 按条件查询流程实例ID列表 |
| deleteByProcInstId | deleteByProcInstId | DELETE | pm_workflow | 按流程实例ID删除 |
| deleteByProcInstIds | deleteByProcInstIds | DELETE | pm_workflow | 批量按流程实例ID删除（JOIN 字符串） |
| selectActivitiUserMails | selectActivitiUserMails | SELECT | ACT_RU_IDENTITYLINK + act_id_user + act_id_group + act_id_membership | 查询 Activiti 用户的邮箱（用于邮件通知） |

**selectActivitiUserMails 查询逻辑**：
```sql
-- 根据 userIds/groupIds/areaPower/projectTypes 查询相关用户邮箱
SELECT DISTINCT u.EMAIL_ 
FROM act_id_user u
LEFT JOIN ACT_RU_IDENTITYLINK i ON u.ID_ = i.USER_ID_
LEFT JOIN act_id_group g ON i.GROUP_ID_ = g.ID_
LEFT JOIN act_id_membership m ON u.ID_ = m.USER_ID_
WHERE ...
```

---

## 十七、PmWorkBenchMapper（工作台/待办）

**接口路径**：`com.dp.plat.pms.springmvc.dao.PmWorkBenchMapper`
**SQL 映射文件**：`PmWorkBenchMapper.xml`
**操作表**：`ACT_RU_TASK` + `ACT_RU_IDENTITYLINK` + `ACT_RE_PROCDEF` + `pm_workflow` 等

> **注意**：此 Mapper 不继承 `AbstractBaseMapper`，直接查询 Activiti 引擎表。

| DAO 方法 | SQL ID | SQL 类型 | 操作表 | 说明 |
|---|---|---|---|---|
| countRunTasksByAssigneeAndProcessKeyAndTaskKey | countRunTasksByAssigneeAndProcessKeyAndTaskKey | SELECT COUNT | ACT_RU_TASK + ACT_RU_IDENTITYLINK + ACT_RE_PROCDEF + pm_workflow | 统计运行中待办任务数 |
| selectRunTasksByAssigneeAndProcessKeyAndTaskKey | selectRunTasksByAssigneeAndProcessKeyAndTaskKey | SELECT | ACT_RU_TASK + ACT_RU_IDENTITYLINK + ACT_RE_PROCDEF + pm_workflow | 分页查询运行中待办任务 |
| countFinishedTasksByAssignee | countFinishedTasksByAssignee | SELECT COUNT | ACT_HI_TASKINST + pm_workflow | 统计已完成任务数 |
| selectFinishedTasksByAssignee | selectFinishedTasksByAssignee | SELECT | ACT_HI_TASKINST + pm_workflow | 分页查询已完成任务 |

**待办查询核心 SQL**：
```sql
SELECT count(DISTINCT RES.ID_)
FROM ACT_RU_TASK RES
LEFT JOIN ACT_RU_IDENTITYLINK I ON I.TASK_ID_ = RES.ID_
LEFT JOIN act_id_group g ON g.ID_ = i.GROUP_ID_ AND i.TYPE_ = 'candidate'
LEFT JOIN act_id_membership im ON im.GROUP_ID_ = g.ID_
INNER JOIN ACT_RE_PROCDEF D ON RES.PROC_DEF_ID_ = D.ID_
INNER JOIN pm_workflow pw ON pw.procInstId = RES.PROC_INST_ID_
LEFT JOIN act_id_user u ON u.ID_ = RES.ASSIGNEE_
WHERE FIND_IN_SET(D.KEY_, #{model.processKey})
  AND (RES.ASSIGNEE_ = #{model.userId} OR im.USER_ID_ = #{model.userId})
  AND RES.SUSPENSION_STATE_ = 1
```

---

## 十八、CommonRelatedDataMapper（通用关联数据）

**接口路径**：`com.dp.plat.pms.springmvc.dao.CommonRelatedDataMapper`
**SQL 映射文件**：`CommonRelatedDataMapper.xml`
**操作表**：`pm_common_related_data`

### 18.1 通用方法

继承 `AbstractBaseMapper<CommonRelatedData>` 的 10 个通用方法。

---

## 十九、DataFieldRelationMapper（动态字段配置）

**接口路径**：`com.dp.plat.pms.springmvc.dao.DataFieldRelationMapper`
**SQL 映射文件**：`DataFieldRelationMapper.xml`
**操作表**：`data_field_relation`

### 19.1 通用方法

继承 `AbstractBaseMapper<DataFieldRelation>` 的 10 个通用方法。

---

## 二十、ProjectManageUserMapper（项目管理用户）

**接口路径**：`com.dp.plat.pms.springmvc.dao.ProjectManageUserMapper`
**SQL 映射文件**：`ProjectManageUserMapper.xml`
**操作表**：`t_user` + `t_user_info`

> **注意**：此 Mapper 不继承 `AbstractBaseMapper`，直接查询用户表。

| DAO 方法 | SQL ID | SQL 类型 | 操作表 | 说明 |
|---|---|---|---|---|
| countBySelectivePageable | countBySelectivePageable | SELECT COUNT | t_user + t_user_info | 分页查询用户计数 |
| countBySelective | countBySelective | SELECT COUNT | t_user + t_user_info | 按条件查询用户计数 |
| selectBySelectivePageable | selectBySelectivePageable | SELECT | t_user + t_user_info | 分页查询用户列表 |
| selectBySelective | selectBySelective | SELECT | t_user + t_user_info | 按条件查询用户列表 |

---

## 二十一、PmSynchronizeMapper（数据同步）

**接口路径**：`com.dp.plat.pms.springmvc.dao.PmSynchronizeMapper`
**SQL 映射文件**：`PmSynchronizeMapper.xml`
**操作表**：多表（同步暂存表 + 业务表）

> **注意**：此 Mapper 不继承 `AbstractBaseMapper`，用于外部系统数据同步。

### 21.1 SMS 数据同步

| DAO 方法 | SQL ID | SQL 类型 | 操作表 | 说明 |
|---|---|---|---|---|
| selectAllAfPrjProperty | selectAllAfPrjProperty | SELECT | DPtech_v_lend_info_afxx_4_pms | 查询 SMS 安服项目属性（视图） |
| clearAllAfPrjProperty | clearAllAfPrjProperty | TRUNCATE | pm_project_property_af_from_sms | 清空安服项目属性暂存表 |
| insertAfPrjProperty | insertAfPrjProperty | INSERT | pm_project_property_af_from_sms | 批量插入安服项目属性 |
| selectAllProjectProduct | selectAllProjectProduct | SELECT | DPtech_v_order_ack_line_4_pms | 查询 SMS 项目产品（视图） |
| clearAllProjectProduct | clearAllProjectProduct | TRUNCATE | pm_project_product_af_from_sms | 清空项目产品暂存表 |
| insertProjectProduct | insertProjectProduct | INSERT | pm_project_product_af_from_sms | 批量插入项目产品 |
| splitAfProjectByProductCode | splitAfProjectByProductCode | UPDATE | pm_project | 按产品编码拆分安服项目 |

### 21.2 D365 数据同步

| DAO 方法 | SQL ID | SQL 类型 | 操作表 | 说明 |
|---|---|---|---|---|
| selectAllFacilitator | selectAllFacilitator | SELECT | DPtech_V_Vend + IWS_VendInfoUpdate | 查询 D365 供应商数据（SQL Server 视图） |
| clearAllFacilitator | clearAllFacilitator | TRUNCATE | pm_facilitator_form_d365 | 清空服务商暂存表 |
| insertFacilitator | insertFacilitator | INSERT | pm_facilitator_form_d365 | 批量插入服务商数据 |
| insertOrUpdateFacilitatorFromD365 | insertOrUpdateFacilitatorFromD365 | INSERT/UPDATE | pm_facilitator | 从 D365 暂存表更新到服务商主表 |
| selectAllPurchaseReceiptSettlement | selectAllPurchaseReceiptSettlement | SELECT | D365 采购收货结算视图 | 查询 D365 采购收货结算数据 |
| clearAllPurchaseReceiptSettlement | clearAllPurchaseReceiptSettlement | TRUNCATE | pm_purchase_receipt_settlement_d365 | 清空采购收货结算暂存表 |
| insertPurchaseReceiptSettlement | insertPurchaseReceiptSettlement | INSERT | pm_purchase_receipt_settlement_d365 | 批量插入采购收货结算 |
| updateDispatchAndSubcontractPaymentFromD365 | updateDispatchAndSubcontractPaymentFromD365 | UPDATE | pm_dispatch_project_settlement | 从 D365 数据更新转包结算付款信息 |

### 21.3 SAP 数据同步

| DAO 方法 | SQL ID | SQL 类型 | 操作表 | 说明 |
|---|---|---|---|---|
| selectAllOfstContractHeadSAP | selectAllOfstContractHeadSAP | SELECT | SAP 合同头视图 | 查询 SAP 合同头数据 |
| clearAllOfstContractHeadSAP | clearAllOfstContractHeadSAP | TRUNCATE | pm_ofst_contract_head_sap | 清空 SAP 合同暂存表 |
| insertOfstContractHeadSAP | insertOfstContractHeadSAP | INSERT | pm_ofst_contract_head_sap | 批量插入 SAP 合同数据 |

---

## 二十二、ExcelAnalysisMapper（Excel 导入）

**接口路径**：`com.dp.plat.pms.springmvc.dao.ExcelAnalysisMapper`
**SQL 映射文件**：`ExcelAnalysisMapper.xml`
**操作表**：临时表 + 业务表

> **注意**：此 Mapper 不继承 `AbstractBaseMapper`，用于 Excel 数据导入的临时表操作。

| DAO 方法 | SQL ID | SQL 类型 | 操作表 | 说明 |
|---|---|---|---|---|
| createTempTable | createTempTable | DDL | 临时表 | 创建导入临时表（基于源表结构） |
| insertTempData | insertTempData | INSERT | 临时表 | 批量插入 Excel 数据到临时表 |
| selectTempData | selectTempData | SELECT | 临时表 | 分页查询临时表数据（预览） |
| countTempData | countTempData | SELECT COUNT | 临时表 | 临时表数据计数 |
| dropTempTable | dropTempTable | DDL | 临时表 | 删除临时表 |
| doImportData | doImportData | INSERT | 业务表 | 直接导入数据到业务表 |
| submitImportData | submitImportData | INSERT | 业务表 | 从临时表提交数据到业务表 |
| doImportData2 | doImportData2 | INSERT | ar_report_data 等 | 收入确认报表数据导入 |

---

## 二十三、EHR Mapper 接口

### 23.1 EhrCompanyMapper（EHR 公司）

**接口路径**：`com.dp.plat.ehr.dao.EhrCompanyMapper`
**操作表**：`ehr_company`

继承 `AbstractBaseMapper<Company>` 的 10 个通用方法，主键字段为 `compID`。

### 23.2 EhrDepartmentMapper（EHR 部门）

**接口路径**：`com.dp.plat.ehr.dao.EhrDepartmentMapper`
**操作表**：`ehr_department`

#### 通用方法

继承 `AbstractBaseMapper<Department>` 的 10 个通用方法，主键字段为 `depID`。

#### 扩展方法

| DAO 方法 | SQL ID | SQL 类型 | 操作表 | 说明 |
|---|---|---|---|---|
| selectDepartmentWithChildren | selectDepartmentWithChildren | SELECT | ehr_department | 查询部门及其子部门 |
| selectDepartmentWithChildrenTreeNode | selectDepartmentWithChildrenTreeNode | SELECT | ehr_department | 查询部门树形结构 |
| selectVOBySelective | selectVOBySelective | SELECT | ehr_department + ehr_company | 按条件查询部门 VO（含公司信息） |
| countVOBySelective | countVOBySelective | SELECT COUNT | ehr_department | 部门 VO 计数 |

### 23.3 EmployeeMapper（EHR 员工）

**接口路径**：`com.dp.plat.ehr.dao.EmployeeMapper`
**操作表**：`ehr_employee`

#### 通用方法

继承 `AbstractBaseMapper<Employee>` 的 10 个通用方法，主键字段为 `empID`。

#### 扩展方法

| DAO 方法 | SQL ID | SQL 类型 | 操作表 | 说明 |
|---|---|---|---|---|
| selectEmployeeVOByIds | selectEmployeeVOByIds | SELECT | ehr_employee + ehr_department + ehr_company | 按 ID 列表查询员工 VO |
| selectEmployeeSelect2Data | selectEmployeeSelect2Data | SELECT | ehr_employee | 查询 Select2 下拉数据 |
| countBySelectivePageableVO | countBySelectivePageableVO | SELECT COUNT | ehr_employee + 关联表 | 员工 VO 分页计数 |
| selectBySelectivePageableVO | selectBySelectivePageableVO | SELECT | ehr_employee + 关联表 | 员工 VO 分页查询 |
| selectByWorkNo | selectByWorkNo | SELECT | ehr_employee | 按工号查询员工 |
| selectVOByPrimaryKey | selectVOByPrimaryKey | SELECT | ehr_employee + 关联表 | 按主键查询员工 VO |
| selectEmployeeAppraiserBySelectivePageableVO | selectEmployeeAppraiserBySelectivePageableVO | SELECT | ehr_employee + 关联表 | 员工考评人分页查询 |
| selectEmployeeWithAccount | selectEmployeeWithAccount | SELECT | ehr_employee + ehr_login_account | 查询员工（含登录账号） |

### 23.4 JobMapper（EHR 职位）

**接口路径**：`com.dp.plat.ehr.dao.JobMapper`
**操作表**：`ehr_job`

继承 `AbstractBaseMapper<Job>` 的 10 个通用方法。

### 23.5 HolidayMapper（EHR 假期）

**接口路径**：`com.dp.plat.ehr.dao.HolidayMapper`
**操作表**：`ehr_holiday`

继承 `AbstractBaseMapper<Holiday>` 的 10 个通用方法。

### 23.6 EHRLoginAccountMapper（EHR 登录账号）

**接口路径**：`com.dp.plat.ehr.dao.EHRLoginAccountMapper`
**操作表**：`ehr_login_account`

继承 `AbstractBaseMapper<EHRLoginAccount>` 的 10 个通用方法。

### 23.7 EhrEmpPowerMapper（EHR 员工权限）

**接口路径**：`com.dp.plat.ehr.dao.EhrEmpPowerMapper`
**操作表**：`ehr_emp_power`

#### 通用方法

继承 `AbstractBaseMapper<EhrEmpPower>` 的 10 个通用方法。

#### 扩展方法

| DAO 方法 | SQL ID | SQL 类型 | 操作表 | 说明 |
|---|---|---|---|---|
| selectByEmpID | selectByEmpID | SELECT | ehr_emp_power | 按员工ID查询权限 |
| insertEhrDepPower | insertEhrDepPower | INSERT | ehr_emp_power | 批量插入部门权限 |
| insertEhrEmpPower | insertEhrEmpPower | INSERT | ehr_emp_power | 批量插入员工权限 |
| clearEhrDepPower | clearEhrDepPower | DELETE | ehr_emp_power | 清除部门权限 |
| clearEhrEmpPower | clearEhrEmpPower | DELETE | ehr_emp_power | 清除员工权限 |
| setGroupConcatMaxLen | setGroupConcatMaxLen | SET | - | 设置 GROUP_CONCAT 最大长度 |

### 23.8 EhrSynchronizeMapper（EHR 数据同步）

**接口路径**：`com.dp.plat.ehr.dao.EhrSynchronizeMapper`
**操作表**：多表（EHR 暂存表）

> **注意**：此 Mapper 不继承 `AbstractBaseMapper`，用于 EHR 系统数据同步。

| DAO 方法 | SQL ID | SQL 类型 | 操作表 | 说明 |
|---|---|---|---|---|
| selectAllCompany | selectAllCompany | SELECT | EHR 源公司表 | 查询所有公司 |
| selectAllDepartment | selectAllDepartment | SELECT | EHR 源部门表 | 查询所有部门 |
| selectAllEmployee | selectAllEmployee | SELECT | EHR 源员工表 | 查询所有员工 |
| selectAllEHRLoginAccount | selectAllEHRLoginAccount | SELECT | EHR 源登录账号表 | 查询所有登录账号 |
| selectAllHoliday | selectAllHoliday | SELECT | EHR 源假期表 | 查询所有假期 |
| selectAllJob | selectAllJob | SELECT | EHR 源职位表 | 查询所有职位 |
| clearAllHoliday | clearAllHoliday | TRUNCATE | ehr_holiday | 清空假期表 |
| insertCompany | insertCompany | INSERT | ehr_company | 批量插入公司 |
| insertDepartment | insertDepartment | INSERT | ehr_department | 批量插入部门 |
| insertEmployee | insertEmployee | INSERT | ehr_employee | 批量插入员工 |
| insertEHRLoginAccount | insertEHRLoginAccount | INSERT | ehr_login_account | 批量插入登录账号 |
| insertHoliday | insertHoliday | INSERT | ehr_holiday | 批量插入假期 |
| insertJob | insertJob | INSERT | ehr_job | 批量插入职位 |

---

## 二十四、SQL 编写规范

### 24.1 命名规范

| 类型 | 命名规范 | 示例 |
|------|---------|------|
| Mapper 接口 | `表名Mapper` | `ProjectMapper` |
| XML 映射文件 | `表名Mapper.xml` | `ProjectMapper.xml` |
| namespace | `完整包名.表名Mapper` | `com.dp.plat.pms.springmvc.dao.ProjectMapper` |
| SQL ID | 方法名与 SQL ID 一致 | `selectByPrimaryKey` |
| resultMap | `BaseResultMap` | 通用 resultMap |
| Column List | `Base_Column_List` | 通用字段列表 |

### 24.2 查询规范

1. **主键查询**：使用 `selectByPrimaryKey`，WHERE 条件为主键。
2. **条件查询**：使用 `selectBySelective`，按实体非空字段动态拼接 WHERE。
3. **分页查询**：使用 `selectBySelectivePageable`，参数为 `PageParam` 对象。
4. **逻辑删除**：查询条件需包含 `disabled = 0`（在 `sql_where_selective` 中内置）。
5. **JSON 字段**：使用 `jdbcType="JSON"` 类型，通过 `FastjsonTypeHandler` 处理。

### 24.3 动态 SQL 片段

PMS-springmvc 的 Mapper XML 中常用的动态 SQL 片段：

| 片段 ID | 说明 | 使用位置 |
|---------|------|---------|
| `Base_Column_List` | 基础字段列表 | 所有 SELECT 语句 |
| `sql_where_selective` | 实体非空字段 WHERE 条件 | selectBySelective / countBySelective |
| `sql_model_where_selective` | PageParam.model 的 WHERE 条件 | selectBySelectivePageable / countBySelectivePageable |
| `sql_fuzzy_search` | 模糊搜索条件 | 列表查询 |
| `sql_pageable_limit` | 分页 LIMIT 语句 | 分页查询 |
| `pageWhereSql` | 转包项目专用 WHERE 条件 | DispatchProjectMapper |
| `pageLimitSql` | 转包项目专用 LIMIT | DispatchProjectMapper |

### 24.4 关联查询规范

- 列表查询（`selectBySelectivePageable`）通常使用 LEFT JOIN 关联基础数据表（`fnd_basic_data`、`fnd_department`、`t_user`、`t_user_info`）获取名称信息。
- 关联字段建议建立索引（详见 [index-analysis.md](./index-analysis.md)）。
- 避免在列表查询中使用子查询，优先使用 JOIN。
