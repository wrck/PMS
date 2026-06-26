# ER关系图

> 数据库：dppms_d365 (MySQL)  
> 使用 Mermaid 语法绘制核心ER关系图

---

## 1. 项目核心关系网

```mermaid
erDiagram
    pm_project_header ||--o{ pm_project_member : "1:N projectId"
    pm_project_header ||--|| pm_project_state : "1:1 projectId"
    pm_project_header ||--o{ pm_project_product_line : "1:N projectId"
    pm_project_header ||--o{ pm_project_soft_version : "1:N projectId"
    pm_project_header ||--o{ pm_project_weekly : "1:N projectId"
    pm_project_header ||--o{ pm_project_log : "1:N projectId"
    pm_project_header ||--o{ pm_project_task : "1:N projectId"
    pm_project_header ||--o{ pm_project_instruction : "1:N projectId"
    pm_project_header ||--o{ pm_project_related_party : "1:N projectId"
    pm_project_header ||--o{ pm_project_maintenance : "1:N projectId"
    pm_project_header ||--o{ pm_project_supervision : "1:N projectId"
    pm_project_header ||--o{ pm_project_warranty_callback : "1:N projectId"

    pm_project_header {
        INT projectId PK
        VARCHAR projectType
        VARCHAR projectCode MUL
        VARCHAR projectName
        VARCHAR projectState
        VARCHAR column001 "办事处编码"
        VARCHAR column005 "系统部"
        VARCHAR column010 "项目等级"
        VARCHAR column012 "服务方式"
        VARCHAR compId "公司编码"
        JSON customInfo
    }

    pm_project_member {
        INT id PK
        INT projectId FK
        VARCHAR memberRole "10=项目经理/15=副项目经理/20=项目成员/30=技术负责人/40=质量负责人/50=安全负责人/60=远程支持/71=驻场工程师/80=其他"
        VARCHAR memberCode
        VARCHAR memberName
        DATETIME effectiveFrom
        DATETIME effectiveTo
    }

    pm_project_state {
        INT projectId PK_FK
        VARCHAR projectPlanState
        VARCHAR shipmentState
        VARCHAR executionState
        VARCHAR closeProcessState
    }

    pm_project_soft_version {
        INT id PK
        INT projectId FK
        VARCHAR contractNo
        VARCHAR barCode
        VARCHAR conp "App版本"
        VARCHAR cpld "驱动版本"
        VARCHAR boot "Boot版本"
        VARCHAR pcb "硬件版本"
    }

    pm_project_product_line {
        INT id PK
        INT projectId FK
        VARCHAR contractNo
        VARCHAR itemCode
        VARCHAR itemName
    }

    pm_project_weekly {
        INT id PK
        INT projectId FK
        VARCHAR weeklyName
        DATETIME weeklyDate
    }

    pm_project_maintenance {
        INT id PK
        INT projectId FK
        VARCHAR type "任务性质"
        VARCHAR processDesc "事项描述"
        VARCHAR itemModel "产品型号"
    }
```

---

## 2. 项目组-合同关系

```mermaid
erDiagram
    pm_project_header }o--|| pm_project_group_relationship : "projectCode"
    pm_project_group_relationship }o--|| pm_project_group : "projectGroupCode"
    pm_project_contract }o--|| pm_project_group : "projectGroupCode"

    pm_project_header {
        INT projectId PK
        VARCHAR projectCode MUL
        VARCHAR projectName
    }

    pm_project_group_relationship {
        INT id PK
        VARCHAR projectGroupCode FK
        VARCHAR projectCode FK
        VARCHAR mergeBranchMark
    }

    pm_project_group {
        INT id PK
        VARCHAR projectGroupCode UK
        VARCHAR projectGroupName
        VARCHAR projectType
    }

    pm_project_contract {
        INT id PK
        VARCHAR contractNo
        VARCHAR projectGroupCode FK
    }
```

---

## 3. 售前项目关联关系

```mermaid
erDiagram
    pm_presales_project_header ||--o{ pm_presales_project_product_line : "1:N presalesId"
    pm_presales_project_header ||--o{ pm_presales_project_callback : "1:N presalesId"
    pm_presales_project_header ||--o{ pm_presales_project_duration : "1:N presalesId"
    pm_presales_project_header ||--o{ pm_presales_project_rma_info : "1:N presalesId"
    pm_presales_project_header }o--o| pm_project_header : "projectCode"

    pm_presales_project_header {
        INT presalesId PK
        VARCHAR presalesCode UK
        VARCHAR projectCode "关联售后项目"
        VARCHAR projectName
        VARCHAR projectState
        INT applyState
        VARCHAR officeCode
        VARCHAR salesman
        VARCHAR productManager
        VARCHAR serviceManager
        VARCHAR projectManager
        VARCHAR lendInfoId "SMS借货ID"
        VARCHAR hasRma "是否有RMA"
        VARCHAR hasTransfer "是否有借转销"
        VARCHAR source
        JSON customInfo
    }

    pm_presales_project_product_line {
        INT productLineId PK
        INT presalesId FK
        VARCHAR itemCode
        VARCHAR itemModel
        INT productNum
        INT transferNum
        INT hexiaoNum
    }

    pm_presales_project_callback {
        INT id PK
        INT presalesId FK
        INT quesnaireId
        INT callbackState
    }

    pm_presales_project_duration {
        INT id PK
        INT presalesId FK
        VARCHAR stageCode
        VARCHAR duration
    }

    pm_presales_project_rma_info {
        INT id PK
        INT presalesId FK
        VARCHAR rmaNo
        VARCHAR barCode
        VARCHAR itemModel
    }
```

---

## 4. 转包项目关联关系

```mermaid
erDiagram
    pm_subcontract_project_header ||--o{ pm_subcontract_project_line : "1:N subcontractId"
    pm_subcontract_project_header ||--o{ pm_subcontract_project_payment : "1:N subcontractId"
    pm_subcontract_project_header ||--o{ pm_subcontract_project_price : "1:N subcontractId"
    pm_subcontract_project_header ||--o{ pm_subcontract_project_callback : "1:N subcontractId"
    pm_subcontract_project_header ||--o{ pm_subcontract_deliver_files : "1:N subcontractId"
    pm_subcontract_project_header }o--o| pm_facilitator : "facilitatorId"
    pm_subcontract_project_payment_sse }o--o| pm_subcontract_project_header : "subcontractNo"

    pm_subcontract_project_header {
        INT id PK
        VARCHAR subcontractName
        VARCHAR subcontractNo
        VARCHAR contractNos "关联合同号"
        VARCHAR projectIds "关联项目ID"
        INT type
        INT state
        INT callbackState
        INT facilitatorId FK
        VARCHAR facilitatorName
        VARCHAR bankInfo
        VARCHAR bankAccount
        VARCHAR officeCode
        VARCHAR profitDepCode
        BOOLEAN isAccrued
        BOOLEAN isInvoiced
        VARCHAR subcontractAmount
        JSON customInfo
    }

    pm_subcontract_project_line {
        INT id PK
        INT subcontractId FK
        INT projectId
        VARCHAR barCode
        VARCHAR itemCode
        VARCHAR itemModel
        VARCHAR contractNo
    }

    pm_subcontract_project_payment {
        INT id PK
        INT subcontractId FK
        VARCHAR ratio
        VARCHAR amount
        DATETIME confirmTime
        DATETIME paymentTime
        BIGINT sseId
    }

    pm_subcontract_project_payment_sse {
        INT id "SSE报销行ID"
        VARCHAR workNo
        VARCHAR name
        VARCHAR offerNum
        DECIMAL applyAmount
        VARCHAR receiver
        VARCHAR bank
        VARCHAR bankAccount
        VARCHAR useage
        VARCHAR paystate
        VARCHAR approveState
        VARCHAR subcontractNo FK
    }

    pm_subcontract_project_price {
        INT id PK
        INT subcontractId FK
        VARCHAR contractNo
        VARCHAR orderExecNumber
        VARCHAR projectCode
        VARCHAR engineeFee
        VARCHAR objId
        VARCHAR procType
        VARCHAR price
    }

    pm_subcontract_project_callback {
        INT id PK
        INT subcontractId FK
        VARCHAR taskKey
        VARCHAR taskId
        INT quesnaireId
        INT quesnaireVersion
        INT quesnaireState
        JSON customInfo
    }

    pm_facilitator {
        INT id PK
        VARCHAR name
        VARCHAR code UK
        VARCHAR account
        VARCHAR type "合作类型"
        VARCHAR bankInfo
        VARCHAR bankAccount
        VARCHAR cnapsCode "联行号"
        VARCHAR contacts "联系人"
        VARCHAR tel "联系电话"
        VARCHAR email
        BOOLEAN state
        BOOLEAN needApprove "是否评审"
        INT approveStatus "审批结果"
        VARCHAR deliveryIds "附件材料"
        VARCHAR relateType "关联类型"
        JSON customInfo
    }
```

---

## 5. 用户-角色-部门多对多关系

```mermaid
erDiagram
    fnd_user_info }o--o{ fnd_roles : "roleIds逗号分隔"
    fnd_user_info }o--o| fnd_department : "dpNo"
    fnd_user_info ||--o{ fnd_user_menus : "id→fnd_user_id"
    fnd_user_info ||--o{ fnd_user_power : "id→fndUserId"
    fnd_roles ||--o{ fnd_role_menus : "id→roleId"
    fnd_menus ||--o{ fnd_role_menus : "menuCode"
    fnd_menus ||--o{ fnd_user_menus : "menuCode"

    fnd_user_info {
        INT id PK
        VARCHAR username UK
        VARCHAR password
        VARCHAR email
        VARCHAR dpNo FK "部门编号"
        VARCHAR realName
        VARCHAR roleIds "角色ID列表"
        INT status
        VARCHAR defaultPage
        DATETIME pwdoverdue
        JSON customInfo
    }

    fnd_roles {
        INT id PK
        VARCHAR roleName
        VARCHAR defaultPage
        INT status
    }

    fnd_department {
        INT id PK
        VARCHAR departmentNum MUL
        VARCHAR departmentName
        INT status
    }

    fnd_menus {
        INT id PK
        VARCHAR menuCode MUL
        VARCHAR menuName
        INT menuLevel
        INT superId
        VARCHAR path
    }

    fnd_user_menus {
        INT id PK
        INT fnd_user_id FK
        VARCHAR menuCode FK
        INT menuValue
    }

    fnd_role_menus {
        INT id PK
        INT roleId FK
        VARCHAR menuCode FK
        VARCHAR menuPower
    }

    fnd_user_power {
        INT id PK
        INT fndUserId FK
        VARCHAR areapower "区域权限"
    }
```

---

## 6. 数据同步流向

```mermaid
flowchart LR
    subgraph 外部系统
        SAP[SAP ERP]
        D365[D365 ERP]
        OA[OA系统]
        SMS[SMS系统]
    end

    subgraph 中间表
        SAP_ORDER[pm_order_data_from_erp_sap]
        SAP_LINE[pm_order_line_from_erp_sap]
        D365_ORDER[pm_order_data_from_erp_d365]
        D365_LINE[pm_order_line_from_erp_d365]
        SOURCE_ORDER[pm_order_data_from_erp_source]
        SOURCE_LINE[pm_order_line_from_erp_source]
        OA_PERSON[pm_person_from_oa]
        SMS_PROP[pm_project_property_from_sms]
        SMS_LEND[pm_presales_lend_*_from_sms]
        SMS_AGENT[pm_project_soleagent_lend_from_sms]
        SMS_PRODUCT[pm_project_real_product_line_from_sms]
    end

    subgraph 业务表
        PROJECT[pm_project_header]
        CONTRACT[pm_project_contract]
        PRODUCT_LINE[pm_project_product_line]
        MEMBER[pm_project_member]
        PRESALES[pm_presales_project_header]
    end

    SAP --> SAP_ORDER
    SAP --> SAP_LINE
    D365 --> D365_ORDER
    D365 --> D365_LINE
    SAP_ORDER --> SOURCE_ORDER
    D365_ORDER --> SOURCE_ORDER
    SAP_LINE --> SOURCE_LINE
    D365_LINE --> SOURCE_LINE

    SOURCE_ORDER --> PROJECT
    SOURCE_ORDER --> CONTRACT
    SOURCE_LINE --> PRODUCT_LINE

    OA --> OA_PERSON
    OA_PERSON --> MEMBER

    SMS --> SMS_PROP
    SMS --> SMS_LEND
    SMS --> SMS_AGENT
    SMS --> SMS_PRODUCT

    SMS_PROP --> PROJECT
    SMS_LEND --> PRESALES
    SMS_PRODUCT --> PRODUCT_LINE
```

---

## 7. 回访/问卷关系

```mermaid
erDiagram
    pm_cl_callback ||--o{ pm_cl_callback_quesnaire : "1:N callbackId"
    pm_cl_evaluation_header ||--o{ pm_cl_quesnaire_result_header : "1:N evaluationHeaderId"
    pm_cl_quesnaire_result_header ||--o{ pm_cl_quesnaire_result_line : "1:N resultHeaderId"
    pm_cl_quesnaire_template_header ||--o{ pm_cl_quesnaire_template_line : "1:N quesnaireTemplateHeaderId"
    pm_cl_quesnaire_template_line ||--o{ pm_cl_quesnaire_template_options : "1:N questionId"
    pm_cl_quesnaire_result_header }o--|| pm_cl_quesnaire_template_header : "quesnaireId"
    pm_cl_quesnaire_result_line }o--|| pm_cl_quesnaire_template_line : "templateLineId"

    pm_cl_callback {
        INT id PK
        INT projectId FK
        VARCHAR instId
        INT applyState
    }

    pm_cl_evaluation_header {
        INT id PK
        INT projectId
        VARCHAR projectCode
        INT evaluationType
        INT status
        DECIMAL evaluationScore
    }

    pm_cl_quesnaire_template_header {
        INT id PK
        VARCHAR questionnaireTemplateNum
        VARCHAR questionnaireTemplateName
        DOUBLE questionnaireScore
        DOUBLE questionnairePassScore
        INT questionnaireStatus
        VARCHAR quesType
    }

    pm_cl_quesnaire_template_line {
        INT id PK
        INT quesnaireTemplateHeaderId FK
        VARCHAR questionContent
        DOUBLE questionScore
        INT questionType
        INT questionNum
    }
```

---

## 8. 技术公告关系

```mermaid
erDiagram
    prob_main ||--o{ prob_restore : "1:N probId"
    prob_main ||--o{ prob_restore_process : "1:N probId"
    prob_main ||--o{ prob_restore_weekly : "1:N probId"
    prob_main ||--o{ prob_softwares : "1:N probId"
    prob_main ||--o{ prob_read_log : "1:N probId"

    prob_main {
        INT probId PK
        VARCHAR probNum UK
        VARCHAR theme
        TEXT desc
        TEXT solution
        VARCHAR status
        VARCHAR priority
        VARCHAR productType
        VARCHAR trackingUser
        VARCHAR relatedSceneTypes
        VARCHAR mitigationActionTypes
        VARCHAR solutionActionTypes
    }

    prob_restore {
        INT id PK
        INT probId FK
        INT restoreStatus
        VARCHAR assignee
        VARCHAR serialNum
        VARCHAR itemModel
    }

    prob_restore_process {
        INT id PK
        INT probId FK
        INT restoreStatus
        TEXT restoreRemark
        VARCHAR createBy
    }

    prob_softwares {
        INT id PK
        INT probId FK
        VARCHAR conp
        VARCHAR cpld
        VARCHAR boot
        VARCHAR pcb
        INT datastate
        JSON customInfo
    }
```
