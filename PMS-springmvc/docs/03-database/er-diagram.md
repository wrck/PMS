# PMS-springmvc ER 关系图

> 数据库：dppms_d365 (MySQL 8.0.16)
> 本文档基于 PMS-springmvc 模块的 MyBatis Mapper XML 映射文件、Entity 实体类与 Service 实现梳理而成。
> 使用 Mermaid `erDiagram` 语法绘制核心实体关系图。

---

## 一、表清单与业务域划分

PMS-springmvc 模块涉及的数据库表按业务域划分为以下 6 组：

| 业务域 | 表前缀 | 表名 | 说明 | Mapper |
|--------|--------|------|------|--------|
| 项目管理 | pm_ | pm_project | 项目主表（含 header 视图字段） | ProjectMapper / ProjectHeaderMapper |
| 项目管理 | pm_ | pm_project_member | 项目成员表 | ProjectMemberMapper |
| 项目管理 | pm_ | pm_project_task | 项目任务表 | ProjectTaskMapper |
| 项目管理 | pm_ | pm_daily_report | 日报表 | DailyReportMapper |
| 项目管理 | pm_ | pm_common_related_data | 通用关联数据表 | CommonRelatedDataMapper |
| 项目管理 | pm_ | data_field_relation | 数据字段关系表（动态表单字段） | DataFieldRelationMapper |
| 转包管理 | pm_ | pm_dispatch_project_header | 转包项目表 | DispatchProjectMapper |
| 转包管理 | pm_ | pm_dispatch_project_settlement | 转包结算表 | DispatchSettlementMapper |
| 转包管理 | pm_ | pm_facilitator | 服务商表 | FacilitatorMapper |
| 行业资产 | af_ | af_industry_asset | 行业资产表 | IndustryAssetMapper |
| 行业资产 | af_ | af_industry_leak | 行业漏洞表 | IndustryLeakMapper |
| 行业资产 | af_ | af_industry_leak_warning | 行业漏洞预警表 | IndustryLeakWarningMapper |
| 行业资产 | af_ | af_industry_asset_project_relation | 资产-项目关联表 | IndustryAssetProjectRelationMapper |
| 行业资产 | af_ | af_industry_asset_leak_relation | 资产-漏洞-项目关联表 | IndustryAssetLeakRelationMapper |
| 工作流 | pm_ | pm_workflow | 工作流业务表 | PmWorkFlowMapper |
| 工作流 | ACT_ | ACT_RU_TASK / ACT_RE_PROCDEF 等 | Activiti 引擎表 | PmWorkBenchMapper（只读） |
| EHR 集成 | ehr_ | ehr_company / ehr_department / ehr_employee / ehr_job 等 | 人力资源数据 | ehr.*Mapper |
| 数据同步 | pm_ | pm_project_property_af_from_sms / pm_facilitator_form_d365 等 | 外部系统同步暂存表 | PmSynchronizeMapper |

---

## 二、项目核心关系网

```mermaid
erDiagram
    pm_project ||--o{ pm_project_member : "1:N projectId"
    pm_project ||--o{ pm_project_task : "1:N projectId"
    pm_project ||--o{ pm_daily_report : "1:N projectId"
    pm_project ||--o{ pm_common_related_data : "1:N objId(当 objType=project)"
    pm_project ||--o{ pm_workflow : "1:N dataId(当 dataType=PROJECT)"

    pm_project {
        INT id PK "项目ID（ProjectMapper 使用）"
        INT projectId PK "项目ID（ProjectHeaderMapper 使用，同 id）"
        VARCHAR projectCode "项目编码（唯一）"
        VARCHAR projectName "项目名称"
        VARCHAR projectState "项目状态"
        VARCHAR projectType "项目类型 10/afss/afxx"
        VARCHAR officeCode "办事处编码"
        VARCHAR customerCode "客户编码"
        VARCHAR customerName "客户名称"
        VARCHAR contractNo "合同号（customInfo）"
        JSON customInfo "扩展信息"
        JSON customConfig "扩展配置"
        BIT disabled "逻辑删除"
    }

    pm_project_member {
        INT id PK
        INT projectId FK "关联 pm_project.id"
        VARCHAR projectType "项目类型"
        VARCHAR memberRole "10=项目经理/15=副项目经理/20=项目成员/30=技术负责人/40=质量负责人/50=安全负责人/60=远程支持/71=驻场工程师/80=其他"
        VARCHAR memberCode "成员账号"
        VARCHAR memberName "成员姓名"
        VARCHAR phoneNum "电话"
        VARCHAR email "邮箱"
        VARCHAR fromFlag "来源标识"
        DATETIME effectiveFrom "生效开始"
        DATETIME effectiveTo "生效结束"
    }

    pm_project_task {
        INT taskId PK
        INT projectId FK "关联 pm_project.id"
        VARCHAR projectType "项目类型"
        VARCHAR contractNo "合同号"
        VARCHAR taskTypeCode "任务类型编码"
        VARCHAR taskName "任务名称"
        DATETIME planStartTime "计划开始"
        DATETIME planEndTime "计划结束"
        DATETIME actualStartTime "实际开始"
        DATETIME eventActualFinishDate "实际完成"
        VARCHAR status "状态"
        INT parentId "父任务ID（自关联）"
        INT progress "进度百分比"
        VARCHAR deliverFileIds "交付件ID列表"
    }

    pm_daily_report {
        INT id PK
        INT projectId FK "关联 pm_project.id"
        VARCHAR projectType "项目类型"
        VARCHAR projectCode "项目编码（冗余）"
        VARCHAR projectName "项目名称（冗余）"
        VARCHAR contractNo "合同号"
        VARCHAR officeCode "办事处编码"
        VARCHAR type "日报类型"
        DATETIME processTime "处理时间"
        VARCHAR processDesc "处理描述"
        VARCHAR processStep "处理步骤"
        VARCHAR remainProblem "遗留问题"
        REAL transitHour "通勤工时"
        REAL processHour "处理工时"
        BIT isReported "是否已上报"
        VARCHAR status "状态"
        BIT disabled "逻辑删除"
        JSON customInfo "扩展信息"
    }

    pm_common_related_data {
        INT id PK
        VARCHAR type "关联类型"
        VARCHAR objType "对象类型（如 project）"
        INT objId FK "对象ID（如 pm_project.id）"
        VARCHAR field1 "扩展字段1"
        VARCHAR field2 "扩展字段2"
        VARCHAR field10 "扩展字段10"
        BIT disabled "逻辑删除"
        JSON customInfo "扩展信息"
    }

    pm_workflow {
        INT id PK
        VARCHAR processKey "流程定义Key"
        VARCHAR taskKey "任务节点Key"
        VARCHAR procInstId "流程实例ID（关联 Activiti）"
        VARCHAR status "状态 PENDING/COMPLETED/TERMINATED"
        INT userId "处理人ID"
        VARCHAR objType "业务对象类型"
        INT objId "业务对象ID"
        VARCHAR dataType "数据类型（如 INDUSTRY_ASSET）"
        INT dataId "数据ID"
        DATETIME applyTime "申请时间"
        DATETIME beginTime "开始时间"
        DATETIME endTime "结束时间"
        JSON customInfo "扩展信息"
    }
```

**关系说明：**
- `pm_project` 是核心实体，`id` 与 `projectId` 指向同一字段（ProjectMapper 使用 `id`，ProjectHeaderMapper 使用 `projectId`）。
- `pm_project_member`、`pm_project_task`、`pm_daily_report` 通过 `projectId` 外键关联到项目。
- `pm_common_related_data` 是通用关联表，通过 `objType` + `objId` 多态关联到任意业务对象。
- `pm_workflow` 通过 `dataType` + `dataId` 多态关联到业务对象（如 INDUSTRY_ASSET、PROJECT_TASK 等）。
- `pm_project_task.parentId` 自关联实现任务层级。

---

## 三、转包项目-结算-服务商关系

```mermaid
erDiagram
    pm_dispatch_project_header ||--o{ pm_dispatch_project_settlement : "1:N dispatchId"
    pm_facilitator ||--o{ pm_dispatch_project_header : "1:N facilitatorId"
    pm_project ||--o{ pm_dispatch_project_header : "N:N projectIds（逗号分隔字符串）"

    pm_dispatch_project_header {
        INT id PK
        VARCHAR dispatchName "外派名称"
        VARCHAR dispatchNo "外派合同号"
        VARCHAR dispatchSeq "外派编号"
        VARCHAR contractNos "项目合同号列表（逗号分隔）"
        VARCHAR projectIds "项目ID列表（逗号分隔）"
        VARCHAR type "外派类型"
        INT state "外派状态"
        INT callbackState "回访状态"
        INT facilitatorId FK "关联 pm_facilitator.id"
        VARCHAR facilitatorCode "服务商编码（冗余）"
        VARCHAR facilitatorName "服务商名称（冗余）"
        VARCHAR bankInfo "开户行"
        VARCHAR bankAccount "收款账户"
        VARCHAR officeCode "办事处编码"
        VARCHAR dispatchAmount "外派价"
        BIT isAccrued "是否计提"
        BIT isInvoiced "是否提供发票"
        BIT dispatched "是否已派单"
        BIT settled "是否已结算"
        BIT disabled "逻辑删除"
        JSON customInfo "扩展信息"
    }

    pm_dispatch_project_settlement {
        INT id PK
        VARCHAR settleSeq "结算编号"
        INT dispatchId FK "关联 pm_dispatch_project_header.id"
        VARCHAR dispatchSeq "外派编号（冗余）"
        VARCHAR progressDesc "进度描述"
        REAL progressRatio "进度比例"
        VARCHAR acceptanceDesc "验收描述"
        VARCHAR acceptanceRatio "验收比例"
        VARCHAR ratio "结算比例"
        VARCHAR amount "结算金额"
        DATETIME confirmTime "确认时间"
        DATETIME paymentTime "付款时间"
        INT state "结算状态"
        INT sseId "SEE 单ID"
        INT year "年度"
        INT quarter "季度"
        INT month "月份"
        BIT settled "是否已结清"
        BIT disabled "逻辑删除"
        JSON customInfo "扩展信息"
    }

    pm_facilitator {
        INT id PK
        VARCHAR code "服务商编码"
        VARCHAR account "账号"
        VARCHAR name "服务商名称"
        VARCHAR type "类型"
        VARCHAR bankInfo "开户行"
        VARCHAR bankAccount "收款账户"
        VARCHAR cnapsCode "CNAPS 编码"
        VARCHAR contacts "联系人"
        VARCHAR tel "电话"
        VARCHAR email "邮箱"
        BIT state "状态（1=启用）"
        BIT needApprove "是否需要审批"
        INT approveStatus "审批状态"
        VARCHAR deliveryIds "交付地ID列表"
        VARCHAR relateType "关联类型"
        JSON customInfo "扩展信息"
    }
```

**关系说明：**
- `pm_dispatch_project_header` 与 `pm_project` 是多对多关系，通过 `projectIds`（逗号分隔的字符串）和 `contractNos` 关联，**非外键约束**，需在应用层解析。
- `pm_dispatch_project_settlement` 通过 `dispatchId` 外键关联到转包项目。
- `pm_facilitator` 与 `pm_dispatch_project_header` 是一对多关系，转包项目冗余存储了 `facilitatorCode` 和 `facilitatorName`。
- `pm_dispatch_project_settlement` 的 `sseId` 关联到 SEE 系统（外部系统）的付款单。

---

## 四、行业资产-漏洞-预警关系

```mermaid
erDiagram
    af_industry_asset ||--o{ af_industry_asset_project_relation : "1:N assetId"
    af_industry_asset ||--o{ af_industry_asset_leak_relation : "1:N assetId"
    af_industry_leak ||--o{ af_industry_asset_leak_relation : "1:N leakId"
    af_industry_leak ||--o{ af_industry_leak_warning : "1:N（通过 leakName+资产属性）"
    pm_project ||--o{ af_industry_asset_project_relation : "1:N projectId"
    pm_project ||--o{ af_industry_asset_leak_relation : "1:N projectId"
    pm_workflow ||--o{ af_industry_asset : "1:N dataId(当 dataType=INDUSTRY_ASSET)"
    pm_workflow ||--o{ af_industry_leak : "1:N dataId(当 dataType=INDUSTRY_LEAK)"

    af_industry_asset {
        INT id PK
        VARCHAR assetNum "资产编号"
        VARCHAR assetName "资产名称"
        VARCHAR assetCategory "资产类别"
        VARCHAR assetType "资产类型"
        VARCHAR assetHost "主机/IP"
        VARCHAR assetOpenPorts "开放端口"
        VARCHAR assetDeployInfo "部署信息"
        VARCHAR assetUsage "用途"
        VARCHAR customerName "客户名称"
        VARCHAR industryCode "行业编码"
        VARCHAR assetAS "应用系统"
        VARCHAR assetASVersion "应用系统版本"
        VARCHAR assetOS "操作系统"
        VARCHAR assetOSVersion "操作系统版本"
        VARCHAR assetDB "数据库"
        VARCHAR assetDBVersion "数据库版本"
        VARCHAR status "审批状态"
        INT trackStatus "跟踪状态"
        DATETIME trackedTime "跟踪时间"
        BIT disabled "逻辑删除"
        JSON customInfo "扩展信息"
    }

    af_industry_leak {
        INT id PK
        VARCHAR leakCode "漏洞编码"
        VARCHAR leakName "漏洞名称"
        VARCHAR leakType "漏洞类型"
        VARCHAR leakLevel "漏洞等级"
        VARCHAR leakDesc "漏洞描述"
        VARCHAR industryCode "行业编码"
        VARCHAR leakSourceInfo "漏洞来源"
        VARCHAR assetIds "受影响资产ID列表（逗号分隔）"
        VARCHAR status "审批状态"
        INT trackStatus "跟踪状态"
        DATETIME trackedTime "跟踪时间"
        BIT disabled "逻辑删除"
        JSON customInfo "扩展信息"
    }

    af_industry_leak_warning {
        INT id PK
        VARCHAR leakName "漏洞名称"
        VARCHAR assetAS "应用系统"
        VARCHAR assetASVersion "应用系统版本"
        VARCHAR assetOS "操作系统"
        VARCHAR assetOSVersion "操作系统版本"
        VARCHAR assetDB "数据库"
        VARCHAR assetDBVersion "数据库版本"
        VARCHAR ports "端口"
        INT status "状态"
        INT trackStatus "跟踪状态"
        DATETIME trackedTime "跟踪时间"
        BIT disabled "逻辑删除"
        JSON customInfo "扩展信息"
    }

    af_industry_asset_project_relation {
        INT id PK
        INT projectId FK "关联 pm_project.id"
        INT assetId FK "关联 af_industry_asset.id"
        DATETIME effectiveFrom "生效开始"
        DATETIME effectiveTo "生效结束"
        BIT disabled "逻辑删除"
    }

    af_industry_asset_leak_relation {
        INT id PK
        INT projectId FK "关联 pm_project.id"
        INT assetId FK "关联 af_industry_asset.id"
        INT leakId FK "关联 af_industry_leak.id"
        DATETIME effectiveFrom "生效开始"
        DATETIME effectiveTo "生效结束"
        BIT disabled "逻辑删除"
    }
```

**关系说明：**
- `af_industry_asset` 与 `pm_project` 通过 `af_industry_asset_project_relation` 关联表实现多对多关系。
- `af_industry_asset` 与 `af_industry_leak` 通过 `af_industry_asset_leak_relation` 关联表实现多对多关系，该表同时包含 `projectId`，表示资产-漏洞-项目的三方关联。
- `af_industry_leak_warning` 是基于资产属性（应用系统、操作系统、数据库等）与漏洞匹配后生成的预警记录，通过 `leakName` 和资产属性字段软关联到 `af_industry_leak` 和 `af_industry_asset`，**非外键约束**。
- `af_industry_leak.assetIds` 是冗余的逗号分隔字符串，存储受影响资产ID列表，用于快速查询，**非外键约束**。
- `pm_workflow` 通过 `dataType`（如 `INDUSTRY_ASSET`、`INDUSTRY_LEAK`）+ `dataId` 多态关联到资产/漏洞的审批流程。

---

## 五、工作流与 Activiti 引擎关系

```mermaid
erDiagram
    pm_workflow ||--|| ACT_RU_EXECUTION : "1:1 procInstId(PROC_INST_ID_)"
    ACT_RU_EXECUTION ||--o{ ACT_RU_TASK : "1:N PROC_INST_ID_"
    ACT_RU_TASK ||--o{ ACT_RU_IDENTITYLINK : "1:N TASK_ID_"
    ACT_RU_TASK ||--o{ ACT_RU_VARIABLE : "1:N TASK_ID_"
    ACT_RE_PROCDEF ||--o{ ACT_RU_EXECUTION : "1:N PROC_DEF_ID_"
    act_id_user ||--o{ ACT_RU_IDENTITYLINK : "1:N USER_ID_(assignee)"
    act_id_group ||--o{ ACT_RU_IDENTITYLINK : "1:N GROUP_ID_(candidate)"
    act_id_group ||--o{ act_id_membership : "1:N GROUP_ID_"
    act_id_user ||--o{ act_id_membership : "1:N USER_ID_"

    pm_workflow {
        INT id PK
        VARCHAR processKey "流程Key（关联 ACT_RE_PROCDEF.KEY_）"
        VARCHAR taskKey "任务节点Key"
        VARCHAR procInstId "流程实例ID（关联 ACT_RU_EXECUTION.PROC_INST_ID_）"
        VARCHAR status "PENDING/COMPLETED/TERMINATED"
        VARCHAR dataType "业务数据类型"
        INT dataId "业务数据ID"
    }

    ACT_RU_EXECUTION {
        VARCHAR ID_ PK "执行实例ID"
        VARCHAR PROC_INST_ID_ "流程实例ID"
        VARCHAR PROC_DEF_ID_ "流程定义ID"
    }

    ACT_RU_TASK {
        VARCHAR ID_ PK "任务ID"
        VARCHAR PROC_INST_ID_ "流程实例ID"
        VARCHAR PROC_DEF_ID_ "流程定义ID"
        VARCHAR TASK_DEF_KEY_ "任务定义Key"
        VARCHAR ASSIGNEE_ "办理人"
        VARCHAR PRIORITY_ "优先级"
    }

    ACT_RU_IDENTITYLINK {
        VARCHAR ID_ PK
        VARCHAR TASK_ID_ "任务ID"
        VARCHAR USER_ID_ "用户ID"
        VARCHAR GROUP_ID_ "组ID"
        VARCHAR TYPE_ "类型（candidate/assignee）"
    }

    ACT_RU_VARIABLE {
        VARCHAR ID_ PK
        VARCHAR TASK_ID_ "任务ID"
        VARCHAR EXECUTION_ID_ "执行ID"
        VARCHAR NAME_ "变量名"
        VARCHAR TYPE_ "变量类型"
    }

    ACT_RE_PROCDEF {
        VARCHAR ID_ PK "流程定义ID"
        VARCHAR KEY_ "流程Key"
        INT VERSION_ "版本号"
        VARCHAR NAME_ "流程名称"
    }

    act_id_user {
        VARCHAR ID_ PK "用户ID"
        VARCHAR FIRST_ "名"
        VARCHAR LAST_ "姓"
    }

    act_id_group {
        VARCHAR ID_ PK "组ID"
        VARCHAR NAME_ "组名称"
        VARCHAR TYPE_ "组类型"
    }

    act_id_membership {
        VARCHAR USER_ID_ PK "用户ID"
        VARCHAR GROUP_ID_ PK "组ID"
    }
```

**关系说明：**
- `pm_workflow` 是业务层工作流表，通过 `procInstId` 关联到 Activiti 引擎的 `ACT_RU_EXECUTION`。
- `PmWorkBenchMapper` 直接查询 Activiti 引擎表（`ACT_RU_TASK`、`ACT_RU_IDENTITYLINK`、`ACT_RE_PROCDEF` 等）获取待办任务。
- `ACT_RU_TASK.ASSIGNEE_` 存储办理人ID，关联 `act_id_user.ID_`。
- `ACT_RU_IDENTITYLINK` 通过 `TYPE_='candidate'` 实现候选组/候选人机制，关联 `act_id_group` 和 `act_id_user`。
- `act_id_membership` 维护用户与组的归属关系。

---

## 六、EHR 人力资源关系

```mermaid
erDiagram
    ehr_company ||--o{ ehr_department : "1:N compID"
    ehr_company ||--o{ ehr_employee : "1:N compID"
    ehr_department ||--o{ ehr_employee : "1:N depID"
    ehr_employee ||--o{ ehr_employee : "N:1 reportTo（自关联）"
    ehr_employee ||--o{ ehr_employee : "N:1 wfreportTo（自关联）"
    ehr_employee ||--|| ehr_job : "N:1 jobID"

    ehr_company {
        INT compID PK "公司ID"
        VARCHAR compCode "公司编码"
        VARCHAR compName "公司名称"
        VARCHAR compAbbr "公司简称"
        INT adminID "管理员ID"
        INT compGrade "公司等级"
        INT compType "公司类型"
        INT compArea "公司区域"
        DATETIME effectDate "生效日期"
        BIT isDisabled "是否禁用"
    }

    ehr_department {
        INT depID PK "部门ID"
        VARCHAR depCode "部门编码"
        VARCHAR depName "部门名称"
        VARCHAR depAbbr "部门简称"
        INT compID FK "关联 ehr_company.compID"
        INT adminID "管理员ID"
        INT depGrade "部门等级"
        INT depType "部门类型"
        INT depProperty "部门属性"
        INT director "负责人ID"
        INT director2 "副负责人ID"
        INT depEmp "部门人数"
        INT depNum "部门序号"
        VARCHAR xOrder "排序"
        BIT isDisabled "是否禁用"
    }

    ehr_employee {
        INT empID PK "员工ID"
        VARCHAR workNo "工号"
        VARCHAR name "姓名"
        VARCHAR eName "英文名"
        INT compID FK "关联 ehr_company.compID"
        INT depID FK "关联 ehr_department.depID"
        INT jobID FK "关联 ehr_job.jobID"
        INT reportTo "直属上级ID（自关联）"
        INT wfreportTo "工作流汇报上级ID（自关联）"
        INT empStatus "员工状态"
        INT jobStatus "工作状态"
        INT empType "员工类型"
        DATETIME joinDate "入职日期"
        DATETIME leaveDate "离职日期"
        INT gender "性别"
        VARCHAR email "邮箱"
        VARCHAR mobile "手机"
        VARCHAR officePhone "办公电话"
        INT disabled "是否禁用"
    }

    ehr_job {
        INT jobID PK "职位ID"
        VARCHAR jobCode "职位编码"
        VARCHAR jobName "职位名称"
        INT jobGrade "职位等级"
        INT jobType "职位类型"
    }
```

**关系说明：**
- `ehr_company`、`ehr_department`、`ehr_employee` 是 EHR 系统的核心三表，通过 `compID`、`depID` 建立层级关系。
- `ehr_employee.reportTo` 和 `wfreportTo` 自关联到同表的 `empID`，分别表示行政汇报线和工作流汇报线。
- EHR 数据通过 `EhrDataJob` 定时从外部 EHR 系统同步，PMS-springmvc 只读使用。
- `EHRDataController` 提供树形查询接口，通过 `TreeNodeUtils.constructTreeNodeData` 构建公司-部门-员工树。

---

## 七、数据同步暂存表关系

```mermaid
erDiagram
    pm_facilitator ||--o{ pm_facilitator_form_d365 : "同步来源（D365）"
    pm_project ||--o{ pm_project_property_af_from_sms : "同步来源（SMS）"
    pm_project ||--o{ pm_project_product_af_from_sms : "同步来源（SMS）"

    pm_facilitator_form_d365 {
        VARCHAR vendCode "供应商编码（D365）"
        VARCHAR vendName "供应商名称"
        VARCHAR bankName "开户行"
        VARCHAR bankNum "银行账号"
        VARCHAR cnaps "CNAPS编码"
        INT status "状态"
        VARCHAR vendSubClasses "供应商子类"
        VARCHAR vendorType "供应商类型"
    }

    pm_project_property_af_from_sms {
        INT projectId "项目ID（SMS）"
        VARCHAR projectCode "项目编码"
        VARCHAR projectName "项目名称"
        VARCHAR contractNo "合同号"
        VARCHAR officeCode "办事处编码"
    }

    pm_project_product_af_from_sms {
        INT projectId "项目ID（SMS）"
        VARCHAR contractNo "合同号"
        VARCHAR itemCode "产品编码"
        VARCHAR itemModel "产品型号"
        VARCHAR itemName "产品名称"
    }
```

**关系说明：**
- `pm_facilitator_form_d365` 是 D365 系统供应商数据的同步暂存表，由 `D365DataJob` 定时全量同步，再由 `pmSynchronizeService.insertOrUpdateFacilitatorFromD365()` 更新到 `pm_facilitator` 主表。
- `pm_project_property_af_from_sms` 和 `pm_project_product_af_from_sms` 是 SMS 系统安服项目数据的同步暂存表，由 `SMSDataJob` 定时同步。
- 同步暂存表通过 `truncate` 清空后重新写入，不维护外键约束。

---

## 八、动态表单字段关系

```mermaid
erDiagram
    data_field_relation }o--|| pm_project : "dataId（当 dataName=project）"
    data_field_relation }o--|| af_industry_asset : "dataId（当 dataName=industryAsset）"
    data_field_relation }o--|| pm_daily_report : "dataId（当 dataName=dailyReport）"

    data_field_relation {
        INT id PK
        VARCHAR dataName "数据名（如 project/industryAsset/dailyReport）"
        VARCHAR dataType "数据类型（table/form）"
        INT dataId "数据ID（关联业务表ID）"
        VARCHAR field "字段名"
        VARCHAR alias "字段别名"
        VARCHAR name "字段中文名"
        VARCHAR title "标题"
        VARCHAR type "字段类型"
        VARCHAR render "渲染方式"
        INT sort "排序"
        BIT orderable "是否可排序"
        BIT searchable "是否可搜索"
        BIT visible "是否可见"
        BIT required "是否必填"
        BIT readonly "是否只读"
        BIT isSystemField "是否系统字段"
        INT status "状态"
    }
```

**关系说明：**
- `data_field_relation` 是动态表单字段配置表，通过 `dataName` + `dataId` 多态关联到业务对象。
- `dataName` 取值包括：`project`（项目）、`industryAsset`（行业资产）、`industryLeak`（行业漏洞）、`dailyReport`（日报）等。
- `dataType` 区分字段用途：`table`（列表列配置）、`form`（表单字段配置）。
- `AbstractController.findColumnList()` 和 `findFieldList()` 方法通过此表实现动态列表列和表单字段的渲染。

---

## 九、跨业务域关系总览

```mermaid
graph TB
    subgraph 项目管理
        P[pm_project<br/>项目主表]
        PM[pm_project_member<br/>项目成员]
        PT[pm_project_task<br/>项目任务]
        DR[pm_daily_report<br/>日报]
        CRD[pm_common_related_data<br/>通用关联]
    end

    subgraph 转包管理
        DP[pm_dispatch_project_header<br/>转包项目]
        DS[pm_dispatch_project_settlement<br/>转包结算]
        F[pm_facilitator<br/>服务商]
    end

    subgraph 行业资产
        IA[af_industry_asset<br/>行业资产]
        IL[af_industry_leak<br/>行业漏洞]
        ILW[af_industry_leak_warning<br/>漏洞预警]
        IAPR[af_industry_asset_project_relation<br/>资产-项目关联]
        IALR[af_industry_asset_leak_relation<br/>资产-漏洞-项目关联]
    end

    subgraph 工作流
        WF[pm_workflow<br/>工作流业务表]
        ACT[Activiti 引擎表<br/>ACT_RU_TASK 等]
    end

    subgraph EHR
        EC[ehr_company<br/>公司]
        ED[ehr_department<br/>部门]
        EE[ehr_employee<br/>员工]
    end

    subgraph 数据同步
        D365[D365 同步暂存]
        SMS[SMS 同步暂存]
        SYNC[pm_synchronize<br/>同步日志]
    end

    subgraph 系统配置
        DFR[data_field_relation<br/>动态字段]
    end

    P --> PM
    P --> PT
    P --> DR
    P --> CRD
    P -.->|"projectIds"| DP
    DP --> DS
    F --> DP

    P --> IAPR
    IA --> IAPR
    IA --> IALR
    IL --> IALR
    P --> IALR
    IL -.->|"属性匹配"| ILW

    WF -.->|"procInstId"| ACT
    WF -.->|"dataType+dataId"| P
    WF -.->|"dataType+dataId"| IA
    WF -.->|"dataType+dataId"| IL

    EC --> ED
    EC --> EE
    ED --> EE

    D365 -.->|"同步"| F
    SMS -.->|"同步"| P

    DFR -.->|"dataName+dataId"| P
    DFR -.->|"dataName+dataId"| IA
    DFR -.->|"dataName+dataId"| DR
```

**图例说明：**
- 实线箭头 `→`：表示外键关联或强关联（有明确字段约束）。
- 虚线箭头 `-.->`：表示弱关联（通过字符串字段、多态字段或应用层逻辑关联，无外键约束）。
- 跨业务域的关联主要通过以下方式实现：
  1. **多态外键**：`pm_workflow.dataType+dataId`、`pm_common_related_data.objType+objId`、`data_field_relation.dataName+dataId`。
  2. **字符串关联**：`pm_dispatch_project_header.projectIds`（逗号分隔）。
  3. **属性匹配**：`af_industry_leak_warning` 通过应用系统、操作系统等属性字段匹配 `af_industry_leak` 和 `af_industry_asset`。
  4. **数据同步**：外部系统数据通过暂存表同步到主表。

---

## 十、关键关系约束说明

### 10.1 外键约束情况

PMS-springmvc 模块的数据库表**普遍没有数据库层的外键约束**，所有关联关系都在应用层维护。主要原因：

1. **多数据源架构**：PMS-springmvc 使用 RoutingDataSource 动态切换 6 个数据源（Local/PMS/SMS/EHR/D365/CRM），跨数据源无法建立外键。
2. **分库分表**：部分表分布在不同的数据库实例上。
3. **历史遗留**：PMS-struts 模块使用 iBATIS，PMS-springmvc 使用 MyBatis，两套 ORM 共享同一数据库，外键约束会增加维护复杂度。

### 10.2 逻辑删除约定

所有业务表均采用 `disabled` 字段（BIT 类型）实现逻辑删除：

| 取值 | 含义 |
|------|------|
| 0 (b'0') | 有效记录 |
| 1 (b'1') | 已删除记录 |

查询时需显式添加 `WHERE disabled = 0` 条件，MyBatis Mapper 的 `selectBySelective` 方法已内置此条件。

### 10.3 时间有效期约定

部分表（如 `pm_project_member`、`af_industry_asset_project_relation`）使用 `effectiveFrom` 和 `effectiveTo` 字段管理记录有效期：

| effectiveTo 取值 | 含义 |
|------------------|------|
| NULL | 记录当前有效 |
| 非 NULL 日期 | 记录已失效，该日期为失效时间 |

### 10.4 customInfo JSON 扩展字段

大部分业务表包含 `customInfo`（JSON 类型）字段，用于存储动态扩展属性，避免频繁加列。常见存储内容：

| 表 | customInfo 存储内容 |
|----|---------------------|
| pm_project | 办事处名称、系统部名称、行业名称等冗余字段 |
| pm_dispatch_project_header | 项目详情、合同详情等 |
| pm_daily_report | 项目信息、创建人姓名等 |
| af_industry_asset | 资产扩展属性 |
| pm_workflow | 审批意见、流程变量等 |

JSON 字段通过 `FastjsonTypeHandler` 处理器与 Java `Map<String, Object>` 互转，详见 [mybatis-ibatis-coexistence.md](../01-architecture/mybatis-ibatis-coexistence.md)。
