# PMS-springmvc 数据流向图

> 本文档描述 PMS-springmvc 模块核心业务场景的数据流向，包括数据创建、同步、审批等流程。
> 使用 Mermaid 流程图和时序图绘制。

---

## 一、数据流概览

PMS-springmvc 模块的数据流按业务场景分为以下 7 类：

| 数据流类型 | 触发方式 | 涉及表 | 说明 |
|-----------|---------|--------|------|
| 项目创建 | 用户操作 | pm_project, pm_project_member, pm_project_task, data_field_relation | 项目主数据创建 |
| 转包结算 | 用户操作 + 定时任务 | pm_dispatch_project_header, pm_dispatch_project_settlement, pm_facilitator | 转包项目与结算管理 |
| D365 数据同步 | 定时任务（D365DataJob） | D365 视图, pm_facilitator, pm_dispatch_project_settlement | 供应商和付款信息同步 |
| SMS 数据同步 | 定时任务（SMSDataJob） | SMS 视图, pm_project_property_af_from_sms, pm_project | 安服项目属性同步 |
| EHR 数据同步 | 定时任务（EhrDataJob） | EHR 源表, ehr_company, ehr_department, ehr_employee | 人力资源数据同步 |
| 工作流审批 | 用户操作 + Activiti 引擎 | pm_workflow, ACT_RU_TASK, ACT_RU_IDENTITYLINK | 审批流程处理 |
| 日报管理 | 用户操作 | pm_daily_report, pm_project | 日报创建与邮件通知 |

---

## 二、项目创建数据流

```mermaid
flowchart TD
    U([用户]) -->|填写项目信息| C[ProjectController.create]
    C -->|调用| S[ProjectHeaderService.insertSelective]
    S -->|MyBatis| M1[ProjectHeaderMapper.insertSelective]
    M1 -->|INSERT| DB1[(pm_project)]

    C -->|保存项目成员| S2[ProjectMemberService.insertSelective]
    S2 -->|MyBatis| M2[ProjectMemberMapper.insertSelective]
    M2 -->|INSERT| DB2[(pm_project_member)]

    C -->|保存项目任务| S3[ProjectTaskService.insertSelective]
    S3 -->|MyBatis| M3[ProjectTaskMapper.insertSelective]
    M3 -->|INSERT| DB3[(pm_project_task)]

    C -->|加载字段配置| S4[AbstractController.findFieldList]
    S4 -->|MyBatis| M4[DataFieldRelationMapper.selectBySelective]
    M4 -->|SELECT| DB4[(data_field_relation)]

    C -->|保存关联数据| S5[CommonRelatedDataService.insertSelective]
    S5 -->|MyBatis| M5[CommonRelatedDataMapper.insertSelective]
    M5 -->|INSERT| DB5[(pm_common_related_data)]

    style DB1 fill:#e1f5fe
    style DB2 fill:#e8f5e9
    style DB3 fill:#e8f5e9
    style DB4 fill:#fff3e0
    style DB5 fill:#f3e5f5
```

**数据流说明：**

1. **项目主表**：`ProjectController.create` 调用 `ProjectHeaderService.insertSelective` 向 `pm_project` 表插入项目主记录，`customInfo` JSON 字段存储扩展属性。
2. **项目成员**：项目创建时同步保存项目成员（项目经理、技术负责人等），写入 `pm_project_member` 表。
3. **项目任务**：项目创建时根据模板生成初始任务节点，写入 `pm_project_task` 表。
4. **字段配置**：`AbstractController.findFieldList` 从 `data_field_relation` 表加载动态表单字段配置，用于前端渲染。
5. **关联数据**：项目的扩展关联信息写入 `pm_common_related_data` 表（`objType='project'`）。

---

## 三、转包结算数据流

```mermaid
flowchart TD
    subgraph 用户操作
        U1([用户]) -->|创建转包项目| C1[DispatchProjectController.create]
        U2([用户]) -->|创建结算单| C2[DispatchSettlementController.create]
        U3([用户]) -->|提交结算| C3[DispatchSettlementController.settlementSubmit]
    end

    subgraph 转包项目创建
        C1 --> S1[DispatchProjectService.insertSelective]
        S1 --> M1[DispatchProjectMapper.insertSelective]
        M1 --> DB1[(pm_dispatch_project_header)]
    end

    subgraph 结算单创建
        C2 -->|查询转包项目| S2[DispatchProjectService.selectByPrimaryKey]
        S2 --> M2[DispatchProjectMapper.selectByPrimaryKey]
        M2 -->|SELECT| DB1

        C2 --> S3[DispatchSettlementService.insertSelective]
        S3 --> M3[DispatchSettlementMapper.insertSelective]
        M3 -->|INSERT| DB2[(pm_dispatch_project_settlement)]
    end

    subgraph 结算提交
        C3 -->|更新状态| S4[DispatchSettlementService.updateByPrimaryKeySelective]
        S4 --> M4[DispatchSettlementMapper.updateByPrimaryKeySelective]
        M4 -->|UPDATE state=1| DB2

        C3 -->|触发工作流| S5[PmWorkFlowService.startProcess]
        S5 -->|INSERT| DB3[(pm_workflow)]
        S5 -->|启动流程| ACT[Activiti 引擎]
    end

    subgraph AOP切面
        AOP[DispatchSettlementUpdateAspect] -.->|监听结算更新| DB2
        AOP -.->|同步更新转包项目| DB1
    end

    style DB1 fill:#e1f5fe
    style DB2 fill:#e8f5e9
    style DB3 fill:#fff3e0
```

**数据流说明：**

1. **转包项目创建**：`DispatchProjectController.create` 向 `pm_dispatch_project_header` 插入转包项目记录，`projectIds` 字段存储关联的项目ID列表（逗号分隔）。
2. **结算单创建**：结算单通过 `dispatchId` 外键关联到转包项目，写入 `pm_dispatch_project_settlement` 表。
3. **结算提交**：提交结算时更新结算状态（`state=1`），并启动 Activiti 工作流审批流程，在 `pm_workflow` 表创建审批记录。
4. **AOP 切面**：`DispatchSettlementUpdateAspect` 监听结算单变更，自动更新转包项目的 `settled` 状态。

---

## 四、D365 数据同步数据流

```mermaid
flowchart TD
    subgraph 定时触发
        T[Quartz 调度器] -->|Cron 触发| J[D365DataJob.execute]
    end

    subgraph D365数据源 SQL Server
        D365_1[(DPtech_V_Vend<br/>供应商视图)]
        D365_2[(IWS_VendInfoUpdate<br/>供应商扩展信息)]
        D365_3[(D365 采购收货结算视图)]
    end

    subgraph PMS数据源 MySQL
        T1[(pm_facilitator_form_d365<br/>服务商暂存表)]
        T2[(pm_purchase_receipt_settlement_d365<br/>结算暂存表)]
        M1[(pm_facilitator<br/>服务商主表)]
        M2[(pm_dispatch_project_settlement<br/>转包结算表)]
        LOG[(sync_log<br/>同步日志)]
    end

    J -->|1. 查询供应商| Q1[PmSynchronizeMapper.selectAllFacilitator]
    Q1 -->|SELECT| D365_1
    Q1 -->|LEFT JOIN| D365_2

    J -->|2. 清空暂存表| Q2[PmSynchronizeMapper.clearAllFacilitator]
    Q2 -->|TRUNCATE| T1

    J -->|3. 写入暂存表| Q3[PmSynchronizeMapper.insertFacilitator]
    Q3 -->|INSERT| T1

    J -->|4. 更新主表| Q4[PmSynchronizeMapper.insertOrUpdateFacilitatorFromD365]
    Q4 -->|INSERT/UPDATE| M1

    J -->|5. 查询结算数据| Q5[PmSynchronizeMapper.selectAllPurchaseReceiptSettlement]
    Q5 -->|SELECT| D365_3

    J -->|6. 更新结算付款| Q6[PmSynchronizeMapper.updateDispatchAndSubcontractPaymentFromD365]
    Q6 -->|UPDATE paymentTime| M2

    J -->|7. 记录日志| Q7[SynchronizeService.insertSyncLog]
    Q7 -->|INSERT| LOG

    style D365_1 fill:#fce4ec
    style D365_2 fill:#fce4ec
    style D365_3 fill:#fce4ec
    style T1 fill:#e1f5fe
    style T2 fill:#e1f5fe
    style M1 fill:#e8f5e9
    style M2 fill:#e8f5e9
    style LOG fill:#fff3e0
```

**数据流说明：**

1. **定时触发**：`D365DataJob` 由 Quartz 调度器按 Cron 表达式定时触发（生产环境每日执行）。
2. **供应商同步**：
   - 从 D365 数据源（SQL Server）的 `DPtech_V_Vend` 视图查询供应商数据。
   - 清空 MySQL 中的 `pm_facilitator_form_d365` 暂存表。
   - 将 D365 数据写入暂存表。
   - 调用 `insertOrUpdateFacilitatorFromD365()` 将暂存数据同步到 `pm_facilitator` 主表（存在则更新，不存在则插入）。
3. **结算付款同步**：
   - 从 D365 查询采购收货结算数据。
   - 调用 `updateDispatchAndSubcontractPaymentFromD365()` 更新 `pm_dispatch_project_settlement` 表的付款信息（`paymentTime` 等字段）。
4. **日志记录**：同步完成后向 `sync_log` 表插入同步日志，记录成功/失败状态和异常信息。

---

## 五、SMS 数据同步数据流

```mermaid
flowchart TD
    subgraph 定时触发
        T[Quartz 调度器] -->|Cron 触发| J[SMSDataJob.execute]
    end

    subgraph SMS数据源
        SMS1[(DPtech_v_lend_info_afxx_4_pms<br/>安服项目属性视图)]
        SMS2[(DPtech_v_order_ack_line_4_pms<br/>订单确认行视图)]
    end

    subgraph PMS数据源
        T1[(pm_project_property_af_from_sms<br/>项目属性暂存表)]
        T2[(pm_project_product_af_from_sms<br/>项目产品暂存表)]
        M1[(pm_project<br/>项目主表)]
        LOG[(sync_log<br/>同步日志)]
    end

    J -->|1. 查询项目属性| Q1[PmSynchronizeMapper.selectAllAfPrjProperty]
    Q1 -->|SELECT| SMS1

    J -->|2. 清空暂存表| Q2[PmSynchronizeMapper.clearAllAfPrjProperty]
    Q2 -->|TRUNCATE| T1

    J -->|3. 写入暂存表| Q3[PmSynchronizeMapper.insertAfPrjProperty]
    Q3 -->|INSERT| T1

    J -->|4. 查询项目产品| Q4[PmSynchronizeMapper.selectAllProjectProduct]
    Q4 -->|SELECT| SMS2

    J -->|5. 清空暂存表| Q5[PmSynchronizeMapper.clearAllProjectProduct]
    Q5 -->|TRUNCATE| T2

    J -->|6. 写入暂存表| Q6[PmSynchronizeMapper.insertProjectProduct]
    Q6 -->|INSERT| T2

    J -->|7. 拆分安服项目| Q7[PmSynchronizeMapper.splitAfProjectByProductCode]
    Q7 -->|UPDATE| M1

    J -->|8. 记录日志| Q8[SynchronizeService.insertSyncLog]
    Q8 -->|INSERT| LOG

    style SMS1 fill:#fce4ec
    style SMS2 fill:#fce4ec
    style T1 fill:#e1f5fe
    style T2 fill:#e1f5fe
    style M1 fill:#e8f5e9
    style LOG fill:#fff3e0
```

**数据流说明：**

1. **项目属性同步**：从 SMS 数据源查询安服先行项目属性，写入 `pm_project_property_af_from_sms` 暂存表。
2. **项目产品同步**：从 SMS 查询订单确认行数据，写入 `pm_project_product_af_from_sms` 暂存表。
3. **项目拆分**：调用 `splitAfProjectByProductCode()` 按产品编码拆分安服项目，更新 `pm_project` 表。
4. **日志记录**：同步完成后记录同步日志。

---

## 六、EHR 数据同步数据流

```mermaid
flowchart TD
    subgraph 定时触发
        T[Quartz 调度器] -->|Cron 触发| J[EhrDataJob.execute]
    end

    subgraph EHR数据源
        E1[(EHR 公司表)]
        E2[(EHR 部门表)]
        E3[(EHR 员工表)]
        E4[(EHR 职位表)]
        E5[(EHR 登录账号表)]
        E6[(EHR 假期表)]
    end

    subgraph PMS数据源
        M1[(ehr_company<br/>公司表)]
        M2[(ehr_department<br/>部门表)]
        M3[(ehr_employee<br/>员工表)]
        M4[(ehr_job<br/>职位表)]
        M5[(ehr_login_account<br/>登录账号表)]
        M6[(ehr_holiday<br/>假期表)]
        M7[(ehr_emp_power<br/>员工权限表)]
        ACT[Activiti 引擎<br/>act_id_user/group]
        LOG[(sync_log<br/>同步日志)]
    end

    J -->|1. 查询EHR数据| Q1[EhrSynchronizeMapper.selectAll*]
    Q1 -->|SELECT| E1
    Q1 -->|SELECT| E2
    Q1 -->|SELECT| E3
    Q1 -->|SELECT| E4
    Q1 -->|SELECT| E5
    Q1 -->|SELECT| E6

    J -->|2. 清空目标表| Q2[EhrSynchronizeMapper.clearAll*]
    Q2 -->|TRUNCATE| M1
    Q2 -->|TRUNCATE| M2
    Q2 -->|TRUNCATE| M3
    Q2 -->|TRUNCATE| M6

    J -->|3. 批量写入| Q3[EhrSynchronizeMapper.insert*]
    Q3 -->|INSERT| M1
    Q3 -->|INSERT| M2
    Q3 -->|INSERT| M3
    Q3 -->|INSERT| M4
    Q3 -->|INSERT| M5
    Q3 -->|INSERT| M6

    J -->|4. 更新权限| Q4[EhrEmpPowerMapper.insertEhrDepPower]
    Q4 -->|INSERT| M7

    J -->|5. 同步Activiti| Q5[IdentityService.saveUser/Group]
    Q5 -->|INSERT/UPDATE| ACT

    J -->|6. 记录日志| Q6[SynchronizeService.insertSyncLog]
    Q6 -->|INSERT| LOG

    style E1 fill:#fce4ec
    style E2 fill:#fce4ec
    style E3 fill:#fce4ec
    style M1 fill:#e8f5e9
    style M2 fill:#e8f5e9
    style M3 fill:#e8f5e9
    style ACT fill:#e1f5fe
    style LOG fill:#fff3e0
```

**数据流说明：**

1. **全量同步**：`EhrDataJob` 采用全量同步策略，先清空目标表再批量写入。
2. **数据范围**：同步公司、部门、员工、职位、登录账号、假期 6 类数据。
3. **权限同步**：同步完成后调用 `EhrEmpPowerMapper.insertEhrDepPower()` 和 `insertEhrEmpPower()` 重建员工权限数据。
4. **Activiti 同步**：通过 `IdentityService` 将用户和组信息同步到 Activiti 引擎的 `act_id_user`、`act_id_group`、`act_id_membership` 表，用于工作流候选人查询。
5. **系统参数控制**：通过 `SystemConfig.systemVariables.get("ehr.sync.user")` 参数控制是否同步用户数据。

---

## 七、工作流审批数据流

```mermaid
sequenceDiagram
    participant U as 用户
    participant C as Controller
    participant S as PmWorkFlowService
    participant DB as pm_workflow表
    participant ACT as Activiti引擎
    participant L as QualityApproveTrackListener

    U->>C: 提交审批（如资产更新）
    C->>S: startProcess(workFlowVO)
    S->>DB: INSERT pm_workflow (status=PENDING)
    S->>ACT: runtimeService.startProcessInstanceByKey()
    ACT-->>S: 返回 processInstanceId
    S->>DB: UPDATE pm_workflow SET procInstId=?

    Note over ACT: 流程流转到审批节点

    ACT->>L: 触发 TaskListener
    L->>DB: 查询 pm_workflow (dataType+dataId)
    L->>L: 设置流程变量（审批人等）

    U->>C: 审批通过
    C->>S: completeTask(taskId, variables)
    S->>ACT: taskService.complete()
    ACT->>L: 触发 TaskListener
    L->>DB: UPDATE pm_workflow SET status=COMPLETED

    alt 审批驳回
        U->>C: 审批驳回
        C->>S: terminateProcess(workFlowVO)
        S->>ACT: runtimeService.deleteProcessInstance()
        S->>DB: UPDATE pm_workflow SET status=TERMINATED
    end
```

**数据流说明：**

1. **流程启动**：用户提交审批时，`PmWorkFlowService.startProcess()` 在 `pm_workflow` 表创建审批记录（`status=PENDING`），并调用 Activiti 引擎启动流程实例。
2. **流程实例关联**：获取 Activiti 返回的 `processInstanceId` 后，更新 `pm_workflow` 表的 `procInstId` 字段，建立业务表与引擎表的关联。
3. **任务监听**：`QualityApproveTrackListener` 监听 Activiti 任务节点事件，查询 `pm_workflow` 表获取业务上下文，设置流程变量（如审批人、区域权限等）。
4. **审批完成**：用户审批通过后，`PmWorkFlowService.completeTask()` 调用 Activiti 完成任务，监听器更新 `pm_workflow` 状态为 `COMPLETED`。
5. **流程终止**：审批驳回或业务数据变更时，调用 `terminateProcess()` 删除 Activiti 流程实例，更新 `pm_workflow` 状态为 `TERMINATED`。

---

## 八、日报管理与邮件通知数据流

```mermaid
flowchart TD
    subgraph 日报创建
        U([用户]) -->|填写日报| C[DailyReportController.create]
        C -->|查询项目信息| S1[ProjectHeaderService.selectByPrimaryKey]
        S1 -->|SELECT| DB1[(pm_project)]
        C -->|保存日报| S2[DailyReportService.insertSelective]
        S2 -->|INSERT| DB2[(pm_daily_report)]
    end

    subgraph 权限检查
        C -->|检查权限| S3[DailyReportMapper.checkPermission]
        S3 -->|SELECT| DB2
        S3 -->|LEFT JOIN| DB1
        S3 -->|LEFT JOIN| DB3[(t_user_info)]
    end

    subgraph 邮件通知
        C -->|渲染模板| FT[FreeMarker Template]
        C -->|发送邮件| MU[MailUtil.sendMail]
        MU -->|SMTP| MAIL[邮件服务器]
    end

    subgraph 日报列表查询
        U2([用户]) -->|查询日报列表| C2[DailyReportController.list]
        C2 -->|权限过滤| S4[DailyReportService.selectBySelectivePageable]
        S4 -->|SELECT| DB2
        S4 -->|LEFT JOIN| DB4[(pm_project_member)]
        S4 -->|LEFT JOIN| DB1
        S4 -->|LEFT JOIN| DB5[(fnd_basic_data)]
    end

    style DB1 fill:#e1f5fe
    style DB2 fill:#e8f5e9
    style DB3 fill:#fff3e0
    style DB4 fill:#e8f5e9
    style DB5 fill:#f3e5f5
```

**数据流说明：**

1. **日报创建**：
   - 用户填写日报时，`DailyReportController.create` 先查询 `pm_project` 表获取项目信息（项目名称、办事处、合同号等）。
   - 将项目信息冗余到日报记录中，插入 `pm_daily_report` 表。
   - `customInfo` JSON 字段存储项目详情、创建人姓名等扩展信息。

2. **权限检查**：
   - `DailyReportMapper.checkPermission` 通过日报ID查询所属项目，再关联 `t_user_info` 表检查用户权限。
   - 权限检查逻辑：用户是否为项目管理员、是否为项目成员、是否有所属办事处权限。

3. **邮件通知**：
   - 日报创建后，使用 FreeMarker 模板引擎渲染邮件内容。
   - 通过 `MailUtil.sendMail()` 发送邮件通知相关人员。

4. **列表查询**：
   - 日报列表查询涉及多表 JOIN：`pm_daily_report` + `pm_project` + `pm_project_member` + `fnd_basic_data`。
   - 权限过滤条件包括：项目类型（`FIND_IN_SET`）、办事处（`FIND_IN_SET`）、项目成员（`memberCode`）、创建人（`createBy`）。

---

## 九、跨模块数据流总览

```mermaid
graph TB
    subgraph 外部系统
        D365[D365 系统<br/>SQL Server]
        SMS[SMS 系统<br/>SQL Server]
        EHR[EHR 系统<br/>MySQL]
        MAIL[邮件服务器<br/>SMTP]
    end

    subgraph 定时任务
        J1[D365DataJob]
        J2[SMSDataJob]
        J3[EhrDataJob]
        J4[MailerJob]
        J5[DispatchSettlementSEEPaymentJob]
        J6[DispatchSettlementInvoiceToFPJob]
    end

    subgraph PMS-springmvc 业务表
        P[(pm_project<br/>项目)]
        DP[(pm_dispatch_project_header<br/>转包项目)]
        DS[(pm_dispatch_project_settlement<br/>转包结算)]
        F[(pm_facilitator<br/>服务商)]
        DR[(pm_daily_report<br/>日报)]
        WF[(pm_workflow<br/>工作流)]
        IA[(af_industry_asset<br/>行业资产)]
        IL[(af_industry_leak<br/>行业漏洞)]
    end

    subgraph 同步暂存表
        T1[(pm_facilitator_form_d365)]
        T2[(pm_project_property_af_from_sms)]
        T3[(pm_project_product_af_from_sms)]
    end

    subgraph Activiti引擎
        ACT[(ACT_RU_TASK<br/>待办任务)]
    end

    D365 -->|供应商数据| J1
    J1 --> T1
    T1 -->|同步| F
    D365 -->|付款数据| J1
    J1 -->|更新付款| DS

    SMS -->|项目属性| J2
    J2 --> T2
    SMS -->|项目产品| J2
    J2 --> T3
    J2 -->|拆分项目| P

    EHR -->|人力资源| J3
    J3 -->|同步用户| ACT

    J4 -->|发送邮件| MAIL
    DR -->|触发| J4

    J5 -->|同步付款| DS
    DS -->|触发| J5

    J6 -->|同步发票| DS

    P -->|关联| DP
    DP -->|1:N| DS
    F -->|关联| DP

    P -->|关联| DR
    P -->|关联| IA
    IA -->|关联| IL

    WF -->|procInstId| ACT
    IA -->|dataType+dataId| WF
    IL -->|dataType+dataId| WF
    DR -->|dataType+dataId| WF

    style D365 fill:#fce4ec
    style SMS fill:#fce4ec
    style EHR fill:#fce4ec
    style MAIL fill:#fce4ec
    style P fill:#e1f5fe
    style DP fill:#e1f5fe
    style DS fill:#e1f5fe
    style F fill:#e8f5e9
    style DR fill:#e8f5e9
    style WF fill:#fff3e0
    style ACT fill:#e3f2fd
```

**图例说明：**
- 粉色节点：外部系统数据源
- 蓝色节点：项目相关业务表
- 绿色节点：服务商、日报等业务表
- 橙色节点：工作流相关表
- 浅蓝色节点：Activiti 引擎表

**关键数据流：**
1. **D365 → 服务商**：D365 供应商数据通过暂存表同步到服务商主表。
2. **D365 → 转包结算**：D365 付款数据直接更新转包结算表的付款信息。
3. **SMS → 项目**：SMS 项目属性和产品数据同步后，触发安服项目拆分。
4. **EHR → Activiti**：EHR 用户数据同步到 Activiti 引擎，用于工作流候选人查询。
5. **日报 → 邮件**：日报创建后触发邮件通知。
6. **业务表 → 工作流**：行业资产、行业漏洞、日报等业务数据通过 `dataType+dataId` 多态关联到工作流审批。
