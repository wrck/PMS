# PMS 关键业务流程数据流向图

> 本文档绘制 PMS 与 SPMS 系统 7 个关键业务流程的数据流向图，使用 Mermaid flowchart 表示。
> 每个流程图标注：数据节点（表名）、数据转换规则、校验机制、数据流向箭头。
> 数据库：dppms_d365 / dppms_d365 (MySQL 8.0.16) / activiti (独立 MySQL) / 外部 SQL Server (D365/SAP/MES)

---

## 1. 售前项目流程

> 业务说明：售前测试项目从创建到闭环的完整数据流，涵盖项目创建、产品线维护、任务分配、回访闭环。
> 涉及模块：PMS-struts（售前测试子模块）、PMS-activiti（Presales 流程）
> BPMN 流程：`Presales.bpmn`（流程 ID：`Presales`）

### 1.1 数据流向图

```mermaid
flowchart TD
    A([售前项目流程开始]) --> B[创建售前项目<br/>pm_presales_project_header<br/>C 操作]
    B --> C[维护产品线<br/>pm_presales_project_product_line<br/>C 操作]
    C --> D[记录项目耗时<br/>pm_presales_project_duration<br/>C 操作]
    D --> E[关联 RMA 信息<br/>pm_presales_project_rma_info<br/>C 操作]
    E --> F[启动 Presales 工作流<br/>ACT_RE_PROCDEF / ACT_RU_EXECUTION<br/>C 操作]

    F --> G[工程管理部指派服务经理<br/>usertask1 assignee=applyBy<br/>ACT_RU_TASK R 操作]
    G --> H{审批结果 result}
    H -->|result=1 通过| I[指定项目经理与服务经理<br/>pm_presales_project_header U 操作]
    H -->|result=-1 返回/驳回| G
    H -->|result=2 同时指定| I

    I --> J[服务经理执行<br/>usertask2 assignee=sm<br/>ACT_RU_TASK R 操作]
    J --> K[项目经理执行<br/>usertask3 assignee=pm<br/>ACT_RU_TASK R 操作]
    K --> L[工程管理部回访<br/>usertask4 candidateGroups=emRole<br/>ACT_RU_TASK R 操作]

    L --> M{回访结果 result}
    M -->|result=1 通过| N[回访记录<br/>pm_presales_project_callback<br/>C 操作]
    M -->|result=-1 驳回| J

    N --> O[流程结束<br/>ACT_HI_PROCINST / ACT_HI_TASKINST<br/>C 操作]
    O --> P([售前项目流程结束])

    %% 外部数据同步
    Q[SMS 系统<br/>售前借出信息] -->|同步| R[pm_presales_lend_info_from_sms<br/>R 操作]
    Q -->|同步| S[pm_presales_lend_order_from_sms<br/>R 操作]
    Q -->|同步| T[pm_presales_lend_product_from_sms<br/>R 操作]
    Q -->|同步| U[pm_presales_lend_2_rma_from_sms<br/>R 操作]
    Q -->|同步| V[pm_presales_lend_2_sale_from_sms<br/>R 操作]
    Q -->|同步| W[pm_presales_lend_2_delivery_off_from_sap<br/>R 操作]
```

### 1.2 数据节点说明

| 数据节点 | 表名 | 操作 | 说明 |
|---------|------|------|------|
| 售前项目主表 | pm_presales_project_header | C/R/U/D | 售前项目创建、状态流转 |
| 售前项目产品线 | pm_presales_project_product_line | C/R/U/D | 产品线维护 |
| 售前项目耗时 | pm_presales_project_duration | C/R/U | 项目耗时记录 |
| 售前项目RMA信息 | pm_presales_project_rma_info | C/R/U | RMA 关联信息 |
| 售前项目回访 | pm_presales_project_callback | C/R/U | 回访记录 |
| 流程定义 | ACT_RE_PROCDEF | R | Presales 流程定义 |
| 流程实例 | ACT_RU_EXECUTION / ACT_HI_PROCINST | C/R | 流程实例运行/历史 |
| 任务实例 | ACT_RU_TASK / ACT_HI_TASKINST | C/R/U | 任务待办/已办 |
| SMS 同步表 | pm_presales_lend_*_from_sms | R | SMS 系统同步数据（只读） |

### 1.3 数据转换规则

| 转换环节 | 转换规则 | 校验机制 |
|---------|---------|---------|
| SMS → 本地表 | 售前借出信息定时同步至 pm_presales_lend_* 表 | 数据格式校验、唯一性校验（sheetID） |
| 项目状态流转 | 待指派 → 已指派 → 执行中 → 回访中 → 已闭环 | 状态机校验（result 字段：1=通过，-1=驳回） |
| 工作流变量绑定 | applyBy/sm/pm/emRole → ACT_RU_TASK.assignee | 用户存在性校验（user_info） |
| 回访结果回写 | result=1 → pm_presales_project_callback 插入回访记录 | 回访人员权限校验（emRole 候选组） |

### 1.4 校验机制

- **唯一性校验**：售前项目编号唯一（pm_presales_project_header.project_no）
- **引用完整性**：产品线关联项目主表（product_line.project_id → header.id）
- **业务规则**：工作流审批顺序校验（必须先指派服务经理，再执行项目）
- **权限校验**：回访人员必须属于 emRole 候选组

---

## 2. 项目管理流程

> 业务说明：项目从立项到闭环的完整数据流，涵盖项目创建、成员管理、里程碑/任务、交付物、闭环审批。
> 涉及模块：PMS-struts（项目管理子模块）、PMS-springmvc（项目管理 Controller）、PMS-activiti（PmClosedLoop 流程）
> BPMN 流程：`PmClosedLoop.bpmn`（流程 ID：`PmClosedLoop`）

### 2.1 数据流向图

```mermaid
flowchart TD
    A([项目管理流程开始]) --> B[项目立项<br/>pm_project_header<br/>C 操作]
    B --> C[维护项目合同<br/>pm_project_contract<br/>C 操作]
    C --> D[分配项目成员<br/>pm_project_member<br/>C 操作]
    D --> E[维护产品线<br/>pm_project_product_line<br/>C 操作]
    E --> F[创建项目任务<br/>pm_project_task<br/>C 操作]
    F --> G[记录项目状态<br/>pm_project_state<br/>C 操作]
    G --> H[项目周报<br/>pm_project_weekly + pm_project_weekly_content<br/>C 操作]
    H --> I[项目通知<br/>pm_project_notification + pm_project_notification_state<br/>C 操作]

    I --> J[交付物管理<br/>pm_project_deliver<br/>C/R/U/D 操作]
    J --> K[维护记录<br/>pm_project_maintenance<br/>C/R/U/D 操作]
    K --> L[发货关联<br/>pm_project_shipment<br/>C/R/U 操作]

    L --> M[启动闭环工作流<br/>PmClosedLoop<br/>ACT_RU_EXECUTION C 操作]
    M --> N[项目经理发起闭环<br/>usertask1 assignee=projectManager<br/>ACT_RU_TASK R 操作]

    N --> O{evaluationResult 分流}
    O -->|result=1| P[服务经理审核<br/>usertask3 assignee=serviceManager]
    O -->|result=2 服务经理与项目经理一致| Q[回访人员回访<br/>usertask5 candidateUsers=callBackPerson]
    O -->|result=3 一致且通过回访| R[工程管理人员评分<br/>usertask4 candidateUsers=projectManageEmp]
    O -->|result=-2 驳回| S([异常结束 endevent2])

    P --> T{服务经理审核}
    T -->|result=1 通过| Q
    T -->|result=-1 不通过| S
    T -->|result=2 已通过回访| R

    Q --> U{回访结果}
    U -->|result=1 达标| R
    U -->|result=-1 不达标| S
    U -->|result=-3 无法回访| P

    R --> V{工程人员审核}
    V -->|result=1 通过| W[闭环完成<br/>ProjectCloseTaskHandler<br/>pm_project_header U 操作]
    V -->|result=-1 不通过| S

    W --> X[维保回访记录<br/>pm_project_warranty_callback<br/>C 操作]
    X --> Y([项目管理流程结束])
```

### 2.2 数据节点说明

| 数据节点 | 表名 | 操作 | 说明 |
|---------|------|------|------|
| 项目主表 | pm_project_header | C/R/U/D | 项目立项、状态更新 |
| 项目合同 | pm_project_contract | C/R | 项目合同关联 |
| 项目成员 | pm_project_member | C/R/U/D | 成员分配、失效变更 |
| 项目产品线 | pm_project_product_line | C/R/U/D | 产品线维护 |
| 项目任务 | pm_project_task | C/R/U | 里程碑/任务管理 |
| 项目状态 | pm_project_state | C/R/U | 状态变更记录 |
| 项目周报 | pm_project_weekly / pm_project_weekly_content | C/R/U | 周报主表与内容 |
| 项目通知 | pm_project_notification / pm_project_notification_state | C/R/U | 通知与状态 |
| 项目交付物 | pm_project_deliver | C/R/U/D | 交付件管理 |
| 维护记录 | pm_project_maintenance | C/R/U/D | 维护记录 |
| 项目发货 | pm_project_shipment | C/R/U | 发货关联 |
| 维保回访 | pm_project_warranty_callback | C/R/U/D | 维保回访记录 |
| 工作流实例 | ACT_RU_EXECUTION / ACT_HI_PROCINST | C/R | 闭环流程实例 |
| 任务实例 | ACT_RU_TASK / ACT_HI_TASKINST | C/R/U | 审批任务 |

### 2.3 数据转换规则

| 转换环节 | 转换规则 | 校验机制 |
|---------|---------|---------|
| 项目状态流转 | 立项 → 执行中 → 待闭环 → 已闭环 | 状态机校验（pm_project_state 记录变更） |
| 闭环审批分流 | evaluationResult 决定审批路径（1/2/3/-2） | 分流网关校验（exclusivegateway6） |
| 服务经理审核 | evaluationResult（1=通过/-1=不通过/2=已通过回访） | 审核结果校验（exclusivegateway4） |
| 回访结果 | evaluationResult（1=达标/-1=不达标/-3=无法回访） | 回访结果校验（exclusivegateway2） |
| 工程人员评分 | evaluationResult（1=通过/-1=不通过） | 评分结果校验（exclusivegateway3） |
| 闭环回调 | ProjectCloseTaskHandler 更新 pm_project_header 状态 | 流程结束监听器校验 |

### 2.4 校验机制

- **唯一性校验**：项目编号唯一（pm_project_header.project_no）
- **引用完整性**：成员关联项目（member.project_id → header.id）、合同关联项目
- **业务规则**：闭环前必须完成交付物、维护记录
- **权限校验**：项目经理（projectManager）、服务经理（serviceManager）、回访人员（callBackPerson）、工程管理人员（projectManageEmp）

---

## 3. 转包管理流程

> 业务说明：项目转包从创建到付款的完整数据流，涵盖转包创建、服务商管理、交付验收、付款、D365 同步。
> 涉及模块：PMS-struts（转包子模块）、PMS-activiti（Subcontract 流程）、PMS-ext-d365（D365 集成）
> BPMN 流程：`Subcontract.bpmn` / `Subcontract2.bpmn`（流程 ID：`Subcontract`）

### 3.1 数据流向图

```mermaid
flowchart TD
    A([转包管理流程开始]) --> B[创建转包项目<br/>pm_subcontract_project_header<br/>C 操作]
    B --> C[维护转包明细<br/>pm_subcontract_project_line<br/>C 操作]
    C --> D[配置服务商<br/>pm_subcontract_facilitator<br/>C 操作]
    D --> E[维护价格<br/>pm_subcontract_project_price<br/>C 操作]

    E --> F[启动转包审批工作流<br/>Subcontract<br/>ACT_RU_EXECUTION C 操作]
    F --> G[转包审批<br/>ACT_RU_TASK R 操作]
    G --> H{审批结果}
    H -->|通过| I[转包执行]
    H -->|驳回| B

    I --> J[交付文件管理<br/>pm_subcontract_deliver_files<br/>C 操作]
    J --> K[转包验收<br/>SubcontractInspectionListener<br/>触发 D365 推送]

    K --> L[推送采购订单至 D365<br/>D365Api.pushPurchaseOrder]
    L --> M[D365 返回 purchId/inventTransId]
    M --> N[持久化采购订单<br/>dp_erp_purchase_order_header<br/>dp_erp_purchase_order_line<br/>C 操作]

    N --> O[交付验收回访<br/>pm_subcontract_project_callback<br/>C 操作]

    O --> P[付款管理<br/>pm_subcontract_project_payment<br/>C 操作]
    P --> Q[付款 SSE 同步<br/>pm_subcontract_project_payment_sse<br/>R 操作]

    Q --> R[推送采购收货至 D365<br/>D365Api.pushPurchaseReceipt]
    R --> S[D365 返回 packingSlipId]
    S --> T[持久化采购收货<br/>dp_erp_purchase_receipt_header<br/>dp_erp_purchase_receipt_line<br/>C 操作]

    T --> U[填充采购基准单位<br/>D365Api.fillPurchaseUnitBase]
    U --> V([转包管理流程结束])

    %% 定时任务
    W[Quartz 定时任务<br/>PushContractAcceptanceDeliveryJob] -->|定时推送| X[推送合同验收交付<br/>D365Api.pushContractAcceptanceDeliveryInfo]
    X --> Y[D365 返回结果]
```

### 3.2 数据节点说明

| 数据节点 | 表名 | 操作 | 说明 |
|---------|------|------|------|
| 转包项目主表 | pm_subcontract_project_header | C/R/U/D | 转包项目创建 |
| 转包项目明细 | pm_subcontract_project_line | C/R/U/D | 转包明细 |
| 服务商 | pm_subcontract_facilitator | C/R/U | 服务商配置 |
| 转包价格 | pm_subcontract_project_price | C/R/U | 价格维护 |
| 交付文件 | pm_subcontract_deliver_files | C/R/U/D | 交付文件管理 |
| 转包回访 | pm_subcontract_project_callback | C/R/U | 验收回访 |
| 付款记录 | pm_subcontract_project_payment | C/R/U | 付款管理 |
| 付款SSE同步 | pm_subcontract_project_payment_sse | R | SSE 系统同步（只读） |
| 采购订单头 | dp_erp_purchase_order_header | C/R/U | D365 采购订单持久化 |
| 采购订单行 | dp_erp_purchase_order_line | C/R/U | D365 采购订单行持久化 |
| 采购收货头 | dp_erp_purchase_receipt_header | C/R/U | D365 采购收货持久化 |
| 采购收货行 | dp_erp_purchase_receipt_line | C/R/U | D365 采购收货行持久化 |
| 工作流实例 | ACT_RU_EXECUTION / ACT_HI_PROCINST | C/R | 转包审批流程 |

### 3.3 数据转换规则

| 转换环节 | 转换规则 | 校验机制 |
|---------|---------|---------|
| 转包项目 → D365 采购订单 | SubcontractProject → PurchaseHeader + PurchaseLine | 字段映射校验、dataAreaId 校验 |
| D365 返回 → 本地持久化 | purchId/inventTransId 回填至 customInfo | response.code==200 校验 |
| 转包验收 → D365 采购收货 | 验收信息 → PurchaseReceiptHeader + PurchaseReceiptLine | 收货数量校验 |
| D365 返回 → 本地持久化 | packingSlipId 回填至 customInfo | response.code==200 校验 |
| 采购基准单位填充 | qtyScale/priceScale 精度配置 → purchUnitBase/purchPriceBase/purchQtyBase | 精度配置校验 |
| SSE 付款同步 | SSE 系统付款数据 → pm_subcontract_project_payment_sse | 数据格式校验 |
| 合同验收交付推送 | Quartz 定时任务 → D365 pushContractAcceptanceDeliveryInfo | 定时触发校验 |

### 3.4 校验机制

- **OAuth2 认证**：D365 API 调用前获取 Azure AD Token（client_credentials 模式），Token 缓存与过期刷新
- **响应校验**：D365 返回 response.code==200 表示成功，否则抛出 CustomRuntimeException
- **字段映射校验**：lineNum 匹配采购订单行（purchLines.lineNum ↔ response.lineNum）
- **唯一性校验**：转包项目编号唯一、D365 采购订单号（purchId）唯一
- **引用完整性**：转包明细关联主表、采购订单行关联订单头（headerId）

---

## 4. 问题管理流程

> 业务说明：技术公告/问题从创建到修复的完整数据流，涵盖问题创建、处理方案、修复进度、软件版本管理。
> 涉及模块：PMS-struts（技术公告/问题管理子模块）

### 4.1 数据流向图

```mermaid
flowchart TD
    A([问题管理流程开始]) --> B[创建技术公告<br/>prob_main<br/>C 操作]
    B --> C[关联项目<br/>pm_project_header<br/>R 操作]
    C --> D[创建修复方案<br/>prob_restore<br/>C 操作]
    D --> E[维护修复进度<br/>prob_restore_process<br/>C 操作]
    E --> F[修复周报<br/>prob_restore_weekly<br/>C 操作]

    F --> G[关联软件信息<br/>prob_softwares<br/>C 操作]
    G --> H[维护软件版本<br/>prob_soft_version<br/>C 操作]

    H --> I{问题状态}
    I -->|待处理| J[继续修复<br/>prob_restore_process U 操作]
    I -->|已解决| K[更新问题状态<br/>prob_main U 操作]
    I -->|已关闭| L[归档]

    J --> F
    K --> M[阅读日志记录<br/>prob_read_log<br/>C 操作]
    L --> M

    M --> N([问题管理流程结束])

    %% 项目软件版本关联
    O[项目软件版本<br/>pm_project_soft_version] -->|关联| H
    O -->|变更日志| P[pm_project_soft_change_logs<br/>C 操作]
```

### 4.2 数据节点说明

| 数据节点 | 表名 | 操作 | 说明 |
|---------|------|------|------|
| 技术公告主表 | prob_main | C/R/U/D | 问题创建、状态更新 |
| 修复方案表 | prob_restore | C/R/U/D | 修复方案管理 |
| 修复进度表 | prob_restore_process | C/R/U | 修复进度记录（probId 关联 prob_main） |
| 修复周报表 | prob_restore_weekly | C/R/U | 修复周报（fileId 关联附件） |
| 软件信息表 | prob_softwares | C/R/U/D | 软件信息管理 |
| 软件版本表 | prob_soft_version | C/R/U/D | 软件版本维护 |
| 阅读日志 | prob_read_log | C/R | 问题阅读记录 |
| 项目软件版本 | pm_project_soft_version | C/R/U/D | 项目软件版本关联 |
| 软件变更日志 | pm_project_soft_change_logs | C/R | 软件版本变更记录 |

### 4.3 数据转换规则

| 转换环节 | 转换规则 | 校验机制 |
|---------|---------|---------|
| 问题状态流转 | 待处理 → 处理中 → 已解决 → 已关闭 | 状态机校验（prob_main.state） |
| 修复方案关联 | prob_restore.probId → prob_main.id | 引用完整性校验 |
| 修复进度关联 | prob_restore_process.probId → prob_main.id | 引用完整性校验 |
| 软件版本关联 | prob_soft_version 关联 prob_softwares | 软件存在性校验 |
| 项目软件版本同步 | pm_project_soft_version 变更 → pm_project_soft_change_logs 记录 | 变更日志校验 |

### 4.4 校验机制

- **唯一性校验**：技术公告编号唯一（prob_main.id）
- **引用完整性**：修复方案/进度通过 probId 关联问题主表
- **业务规则**：问题关闭前必须有修复方案
- **阅读记录校验**：prob_read_log 记录用户阅读历史，避免重复提醒

---

## 5. 工作流审批流程

> 业务说明：Activiti 工作流从启动到回调的完整数据流，涵盖流程启动、任务分配、审批操作、回调处理。
> 涉及模块：PMS-activiti（工作流引擎）、PMS-springmvc（统一任务推送）
> 数据库：独立 `activiti` 数据库（ACT_* 表），与业务库 `dppms_d365` 分离

### 5.1 数据流向图

```mermaid
flowchart TD
    A([工作流审批流程开始]) --> B[部署流程定义<br/>ACT_RE_DEPLOYMENT / ACT_RE_PROCDEF<br/>C 操作]
    B --> C[启动流程实例<br/>ACT_RU_EXECUTION / ACT_HI_PROCINST<br/>C 操作]
    C --> D[设置流程变量<br/>ACT_RU_VARIABLE / ACT_HI_VARINST<br/>C 操作]

    D --> E[创建用户任务<br/>ACT_RU_TASK<br/>C 操作]
    E --> F[动态任务分配<br/>dp_act_unify_task<br/>R 操作]
    F --> G[UserTaskListener 监听<br/>动态设置 assignee/candidateUsers]

    G --> H[任务待办<br/>ACT_RU_TASK R 操作]
    H --> I{审批操作}
    I -->|签收| J[签收任务<br/>ACT_RU_IDENTITYLINK C 操作<br/>ACT_RU_TASK U 操作]
    I -->|委派| K[委派任务<br/>ACT_RU_TASK U 操作]
    I -->|转办| L[转办任务<br/>ACT_RU_TASK U 操作]
    I -->|完成| M[完成任务<br/>ACT_RU_TASK D 操作<br/>ACT_HI_TASKINST C 操作]

    J --> H
    K --> H
    L --> H

    M --> N{审批结果}
    N -->|通过| O[下一节点任务<br/>ACT_RU_TASK C 操作]
    N -->|驳回| P[撤销/撤回<br/>RevokeTaskCmd / WithdrawTaskCmd]
    N -->|跳转| Q[任务跳转<br/>JumpTaskCmdService]

    P --> E
    Q --> E
    O --> R{是否最后节点?}
    R -->|否| H
    R -->|是| S[流程结束<br/>ACT_HI_PROCINST U 操作<br/>ACT_RU_EXECUTION D 操作]

    S --> T[流程结束监听器<br/>CallBackTaskHandler / ProjectCloseTaskHandler]
    T --> U[回调业务表<br/>pm_project_header / pm_presales_project_header U 操作]

    U --> V[统一任务推送<br/>UnifyTaskPushListener]
    V --> W[推送至致远 OA<br/>UnifyTask2SeeyonSender]
    W --> X([工作流审批流程结束])

    %% 流程图生成
    Y[流程图生成<br/>CustomProcessDiagramGenerator] -->|查询| Z[ACT_GE_BYTEARRAY<br/>流程图 PNG/SVG]
```

### 5.2 数据节点说明

| 数据节点 | 表名 | 操作 | 说明 |
|---------|------|------|------|
| 流程部署 | ACT_RE_DEPLOYMENT / ACT_RE_PROCDEF | C/R/D | 流程定义部署 |
| 流程实例 | ACT_RU_EXECUTION / ACT_HI_PROCINST | C/R/U/D | 运行中/历史流程实例 |
| 任务实例 | ACT_RU_TASK / ACT_HI_TASKINST | C/R/U/D | 运行中/历史任务 |
| 流程变量 | ACT_RU_VARIABLE / ACT_HI_VARINST | C/R/U/D | 运行中/历史变量 |
| 身份链接 | ACT_RU_IDENTITYLINK / ACT_HI_IDENTITYLINK | C/R | 任务参与者（签收/委派/转办） |
| 流程图资源 | ACT_GE_BYTEARRAY | R | 流程图 PNG/SVG 二进制资源 |
| 动态任务配置 | dp_act_unify_task | C/R/U/D | 动态审批人/候选人/候选组配置 |
| 业务回调表 | pm_project_header / pm_presales_project_header | U | 流程结束后更新业务状态 |
| 统一任务推送 | activiti-api-unifytask（外部 jar） | C | 任务推送至致远 OA |

### 5.3 数据转换规则

| 转换环节 | 转换规则 | 校验机制 |
|---------|---------|---------|
| 流程定义部署 | BPMN 文件 → ACT_RE_PROCDEF | BPMN 格式校验、流程 ID 唯一性 |
| 流程变量绑定 | applyBy/sm/pm → ACT_RU_TASK.assignee | 用户存在性校验 |
| 动态任务分配 | dp_act_unify_task 配置 → UserTaskListener 设置 assignee | 配置存在性校验 |
| 任务完成 → 历史 | ACT_RU_TASK → ACT_HI_TASKINST | 任务状态校验（已完成才能归档） |
| 流程结束 → 业务回调 | endevent 监听器 → 业务表状态更新 | 监听器配置校验 |
| 统一任务推送 | ACT_RU_TASK → SeeyonTask → 致远 OA | 推送格式校验 |

### 5.4 校验机制

- **流程定义校验**：BPMN 文件格式校验、流程 ID 唯一性
- **任务分配校验**：UserTaskListener 根据 dp_act_unify_task 配置动态分配
- **撤销校验**：RevokeTaskCmd 返回 0=成功 / 1=流程已结束 / 2=下一节点已通过不可撤销
- **撤回校验**：WithdrawTaskCmd 支持多实例节点撤回、单节点↔多实例节点互转
- **跳转校验**：JumpTaskCmdService 删除当前 execution 下所有任务后跳转
- **流程图校验**：CustomProcessDiagramGenerator 解决流程线条不显示文字问题

---

## 6. RMA 备件申请流程

> 业务说明：SPMS 系统 RMA 备件申请从申请到销账的完整数据流，涵盖申请、审核、出库、质检。
> 涉及模块：SPMS 备件申请模块（RmaApplicantAction）
> 数据库：dppms_d365 (MySQL)
> 审核机制：RMA 角色（RMA_ROLE=6）先审核 → QA 角色（QA_ROLE=7）复审

### 6.1 数据流向图

```mermaid
flowchart TD
    A([RMA 备件申请流程开始]) --> B[创建 RMA 申请<br/>rma_applicant<br/>C 操作]
    B --> C[添加备件条码<br/>rma_bar<br/>C 操作]
    C --> D[维护备件信息<br/>rma_spare_info<br/>C 操作]
    D --> E[上传 EMS 运单<br/>app_accessory_info<br/>C 操作]
    E --> F[上传 RMA 文件<br/>app_accessory_info<br/>C 操作]
    F --> G[添加备件明细<br/>app_spare_part<br/>C 操作]
    G --> H[提交申请<br/>rma_applicant U 操作]

    H --> I[RMA 角色审核<br/>rmaRoleAudit RMA_ROLE=6<br/>app_comment C 操作]
    I --> J{审核结果}
    J -->|通过| K[QA 角色审核<br/>qaRoleAudit QA_ROLE=7<br/>app_comment C 操作]
    J -->|驳回| L[重新申请<br/>afreshApply<br/>rma_applicant U 操作]
    L --> B

    K --> M{QA 审核结果}
    M -->|通过| N[发送至 RMA<br/>sendSure2Rma<br/>rma_applicant U 操作]
    M -->|驳回| L

    N --> O[OA 工作流集成<br/>rma_oa<br/>C 操作]
    O --> P[推送 MES 系统<br/>toMES<br/>rma_info2mes_result C 操作]

    P --> Q[备件出库<br/>warehouse_info U 操作<br/>warehouse_info_detail U 操作]
    Q --> R[物流运输]

    R --> S[收货确认<br/>receiveSure / ReceiveBatchSure<br/>rma_applicant U 操作]
    S --> T[质检]

    T --> U{质检结果}
    U -->|合格| V[销账批准<br/>backApproved<br/>rma_applicant U 操作]
    U -->|不合格| W[维修报告<br/>rma_repair_report_from_mes C 操作]

    W --> R
    V --> X[流程结束<br/>rma_applicant 状态更新]
    X --> Y([RMA 备件申请流程结束])

    %% 库存校验
    Z[库存校验<br/>warehouse_info_detail R 操作] -->|校验库存| Q
    AA[转移校验<br/>takePlace=2 判断] -->|过滤已转移| S
```

### 6.2 数据节点说明

| 数据节点 | 表名 | 操作 | 说明 |
|---------|------|------|------|
| RMA 申请主表 | rma_applicant | C/R/U/D | 申请创建、状态流转、销账 |
| RMA 条码表 | rma_bar | C/R/U/D | 备件条码维护 |
| RMA 备件信息 | rma_spare_info | C/R/U | 备件信息维护 |
| 申请备件明细 | app_spare_part | C/R/U/D | 备件明细管理 |
| 审批意见表 | app_comment | C/R/U | 审核意见记录 |
| 申请附件表 | app_accessory_info | C/R/U/D | EMS 运单、RMA 文件上传 |
| OA 工作流表 | rma_oa | C | OA 工作流集成 |
| MES 推送结果 | rma_info2mes_result | C/R | MES 推送记录 |
| MES 维修报告 | rma_repair_report_from_mes | C/R | MES 维修报告回传 |
| 库存汇总表 | warehouse_info | R/U | 出库时数量调整 |
| 库存明细表 | warehouse_info_detail | R/U | 出库时状态更新 |

### 6.3 数据转换规则

| 转换环节 | 转换规则 | 校验机制 |
|---------|---------|---------|
| 申请状态流转 | 待审批 → RMA审核 → QA审核 → 已发送 → 已收货 → 已销账 | 状态机校验（is_pass 字段） |
| RMA 角色审核 | is_pass=1（通过）/ is_pass=2（驳回） | 角色权限校验（RMA_ROLE=6） |
| QA 角色审核 | is_pass=1（通过）/ is_pass=2（驳回） | 角色权限校验（QA_ROLE=7） |
| OA 工作流集成 | insert-rma_oa 写入 OA 表 | OA 表结构一致性校验 |
| MES 推送 | toMES 推送 RMA 信息至 MES | MES 系统可用性校验 |
| 出库库存调整 | warehouse_info 数量减一、warehouse_info_detail 状态更新 | 库存数量校验（数量>0） |
| 收货确认 | isReceive=1 | 收货状态校验 |
| 销账 | backApproved 更新销账状态 | 流程状态校验（必须已收货） |

### 6.4 校验机制

- **双角色审核**：RMA 角色（6）先审核 → QA 角色（7）复审，顺序不可颠倒
- **库存校验**：出库前校验 warehouse_info_detail 库存数量
- **转移校验**：takePlace=2 时通过 queryHasHistory/queryHasNewStory 校验是否已转移
- **条码唯一性**：rma_bar 条码唯一
- **附件校验**：EMS 运单、RMA 文件上传路径校验
- **MES 推送容错**：推送失败不影响主流程，记录 rma_info2mes_result

---

## 7. D365 集成数据流

> 业务说明：PMS 系统与 Microsoft Dynamics 365 ERP 系统的集成数据流，涵盖 OAuth2 认证、采购订单/收货推送、结果回填持久化。
> 涉及模块：PMS-ext-d365（D365 集成扩展层）
> 外部系统：Azure AD（OAuth2 认证）、D365 ERP（REST API）
> 本地存储：dp_erp_purchase_* 表（MySQL dppms_d365）

### 7.1 数据流向图

```mermaid
flowchart TD
    A([D365 集成数据流开始]) --> B{业务触发场景}

    B -->|转包审批| C[SubcontractInspectionListener<br/>转包验收触发]
    B -->|派工立项| D[DispatchProjectService<br/>派工立项触发]
    B -->|派工结算| E[DispatchSettlementService<br/>派工结算触发]
    B -->|定时任务| F[PushContractAcceptanceDeliveryJob<br/>Quartz 定时触发]

    C --> G[D365Api.pushPurchaseOrder]
    D --> G
    E --> H[D365Api.pushPurchaseReceipt]
    F --> I[D365Api.pushContractAcceptanceDeliveryInfo]

    G --> J[初始化 D365 配置<br/>sys.d365.api.config]
    H --> J
    I --> J

    J --> K{Token 缓存检查}
    K -->|Token 有效| M[使用缓存 Token]
    K -->|Token 过期/不存在| L[请求 Azure AD<br/>OAuth2 client_credentials]
    L --> N[Azure AD 返回 Token<br/>access_token + expires_in]
    N --> O[缓存 Token + timestamp]
    O --> M

    M --> P{推送类型}
    P -->|采购订单| Q[构建 PurchaseRequestBody<br/>dataAreaId + purchTable + purchLine]
    P -->|采购收货| R[构建 PurchaseReceiptHeader<br/>dataAreaId + receipt + receiptLines]
    P -->|合同验收| S[构建合同验收节点信息<br/>contractNo + lines]

    Q --> T[POST createPOUrl<br/>Bearer Token + JSON Body]
    R --> U[POST receiptPOUrl<br/>Bearer Token + JSON Body]
    S --> V[POST paymentSchedUrl<br/>Bearer Token + JSON Body]

    T --> W{D365 响应校验<br/>response.code == 200?}
    U --> W
    V --> W

    W -->|否| X[抛出 CustomRuntimeException]
    W -->|是| Y[解析响应 data]

    Y --> Z{回填持久化}
    Z -->|采购订单| AA[回填 purchId/inventTransId<br/>dp_erp_purchase_order_header C 操作<br/>dp_erp_purchase_order_line C 操作]
    Z -->|采购收货| BB[回填 packingSlipId<br/>dp_erp_purchase_receipt_header C 操作<br/>dp_erp_purchase_receipt_line C 操作]
    Z -->|合同验收| CC[记录推送结果]

    AA --> DD[填充采购基准单位<br/>D365Api.fillPurchaseUnitBase<br/>qtyScale/priceScale 精度配置]
    BB --> DD
    DD --> EE[返回 customInfo<br/>purchId/purchIds/inventTransId/inventTransIds]
    CC --> EE

    EE --> FF([D365 集成数据流结束])
    X --> FF

    %% JSON 序列化
    GG[fastjson 序列化<br/>禁用 SortField/MapSortField] -->|保持字段顺序| T
    GG -->|保持字段顺序| U
    GG -->|保持字段顺序| V
```

### 7.2 数据节点说明

| 数据节点 | 表名/系统 | 操作 | 说明 |
|---------|----------|------|------|
| 系统参数配置 | sys.d365.api.config（fnd_sys_arg） | R | D365 API 配置（JSON 字符串） |
| 采购订单头 | dp_erp_purchase_order_header | C/R/U | D365 采购订单持久化 |
| 采购订单行 | dp_erp_purchase_order_line | C/R/U | D365 采购订单行持久化 |
| 采购收货头 | dp_erp_purchase_receipt_header | C/R/U | D365 采购收货持久化 |
| 采购收货行 | dp_erp_purchase_receipt_line | C/R/U | D365 采购收货行持久化 |
| Azure AD | 外部系统 | R | OAuth2 Token 获取 |
| D365 ERP | 外部系统 | C | REST API 调用（创建采购订单/收货/合同验收） |
| 业务对象 | SubcontractProject / DispatchProject | R/U | 转包项目/派工项目（回填 customInfo） |

### 7.3 数据转换规则

| 转换环节 | 转换规则 | 校验机制 |
|---------|---------|---------|
| 业务对象 → D365 请求 | SubcontractProject → PurchaseHeader + PurchaseLine | 字段映射、dataAreaId 校验 |
| OAuth2 认证 | client_credentials 模式 → access_token | Token 过期校验（expiresIn + timestamp） |
| Token 缓存 | cachedToken + timestamp → 过期判断 | volatile 关键字保证可见性 |
| D365 响应解析 | response.data → List<PurchaseRequestBody> | response.code==200 校验 |
| purchId 回填 | response.purchId → customInfo.purchId | lineNum 匹配校验 |
| inventTransId 回填 | response.inventTransId → customInfo.inventTransId | lineNum 匹配校验 |
| 采购基准单位填充 | qtyScale/priceScale → purchUnitBase/purchPriceBase/purchQtyBase | 精度配置校验（默认2位小数） |
| JSON 序列化 | fastjson 禁用 SortField/MapSortField | 字段顺序保持校验 |

### 7.4 校验机制

- **OAuth2 认证校验**：Token 缓存机制，过期自动刷新（volatile cachedToken + timestamp）
- **响应状态码校验**：response.code==200 表示成功，否则抛出 CustomRuntimeException
- **字段映射校验**：lineNum 匹配采购订单行（purchLines.lineNum ↔ response.lineNum）
- **配置校验**：enablePushPurchaseOrder / enablePushContractAcceptanceDelivery 开关控制
- **JSON 序列化校验**：禁用 fastjson SortField/MapSortField，保持字段声明顺序
- **精度校验**：qtyScale（数量小数位，默认2）、priceScale（价格小数位，默认2）
- **HTTP 通信校验**：Hutool-http 封装 POST 请求，支持自定义 headers、form/JSON body

---

## 8. 跨流程数据关联分析

### 8.1 流程间数据依赖关系

```mermaid
flowchart LR
    P1[售前项目流程] -->|项目转正式| P2[项目管理流程]
    P2 -->|项目转包| P3[转包管理流程]
    P2 -->|技术问题| P4[问题管理流程]
    P2 -->|闭环审批| P5[工作流审批流程]
    P3 -->|转包审批| P5
    P3 -->|采购推送| P7[D365 集成数据流]
    P1 -->|RMA 关联| P6[RMA 备件申请流程]
    P5 -->|流程回调| P2
    P5 -->|任务推送| OA[致远 OA 系统]
```

### 8.2 关键共享表跨流程分析

| 共享表 | 涉及流程 | 数据流向 | 冲突风险 |
|--------|---------|---------|---------|
| pm_project_header | 售前项目、项目管理、问题管理、工作流审批 | 售前转正式创建项目 → 项目管理维护 → 工作流回调更新状态 | 低（各流程操作不同字段） |
| pm_project_soft_version | 项目管理、问题管理 | 项目管理维护版本 → 问题管理关联软件版本 | 中（需事务保证一致性） |
| ACT_RU_TASK | 工作流审批、项目管理、转包管理 | 各流程启动工作流 → 创建任务 → 审批完成 | 低（流程实例隔离） |
| dp_erp_purchase_order_* | 转包管理、D365 集成 | 转包触发推送 → D365 返回回填 → 本地持久化 | 低（单流程独占） |
| rma_applicant | RMA 备件申请、售前项目 | 售前项目关联 RMA → RMA 独立流程 | 低（售前只读关联） |
| warehouse_info_detail | RMA 备件申请、备件转移 | RMA 出库更新库存 → 转移调整库存 | 中（需事务保证数量一致） |

### 8.3 数据一致性保障策略

| 一致性场景 | 保障策略 | 说明 |
|-----------|---------|------|
| 工作流与业务表一致性 | 流程结束监听器回调更新业务表 | CallBackTaskHandler / ProjectCloseTaskHandler |
| D365 推送与本地持久化一致性 | 同步推送 + 响应回填持久化 | pushPurchaseOrder 内部完成推送与持久化 |
| 库存数量一致性 | 事务保证 + 数量校验 | 出库/转移操作在事务内完成 |
| 跨数据源一致性 | 续保写回发货系统需异常处理 | insertServiceToshipment 跨数据源操作 |
| Token 缓存一致性 | volatile + timestamp 过期判断 | 多线程环境 Token 刷新安全 |

---

## 9. 变更记录

| 版本 | 日期 | 变更内容 | 变更人 |
|------|------|---------|--------|
| 1.0 | 2026-06-24 | 初始版本，覆盖 7 个关键业务流程的数据流向图 | 知识库构建 |
