# PMS 数据流向文档

> 本文档描述PMS系统的完整数据流向，包括项目全生命周期数据流、数据转换规则、校验机制、异常处理、数据生命周期管理和数据同步失败处理机制。

---

## 1. 项目全生命周期数据流向图

### 1.1 主流程数据流向（ASCII图）

```
┌─────────────────────────────────────────────────────────────────────────────────────────────────┐
│                              PMS 项目全生命周期数据流向                                          │
└─────────────────────────────────────────────────────────────────────────────────────────────────┘

[外部系统]                    [PMS系统]                                                       [外部系统]
                                                                                                
 SMS ──同步──▶ pm_project_property_from_sms ──转换──▶ pm_project_header                        │
 SAP ──同步──▶ pm_order_data_from_erp_sap  ─┐                                                    │
 D365──同步──▶ pm_order_data_from_erp_d365 ─┤▶ 合并 ▶ pm_order_data_from_erp_source            │
 OA  ──同步──▶ pm_person_from_oa            │                                        │          │
                                             │                                        │          │
                                             ▼                                        ▼          │
                                    ┌─────────────────┐                      ┌───────────┐    │
                                    │  定时同步任务     │                      │ 项目创建   │    │
                                    │  (Quartz)        │                      │ Service   │    │
                                    └────────┬────────┘                      └─────┬─────┘    │
                                             │                                     │          │
                                             ▼                                     ▼          │
┌──────────────────────────────────────────────────────────────────────────────────────────────┐  │
│ 阶段1: 项目创建 (STATE_10→STATE_30)                                                          │  │
│                                                                                              │  │
│  pm_order_data_from_erp_source ──读取──▶ projectService.insertProject()                     │  │
│       │                                │  校验: contractNo唯一性                              │  │
│       │                                │  转换: contractNo→pm_project_contract              │  │
│       │                                │  转换: itemCode→pm_project_product_line            │  │
│       │                                │  生成: projectCode自动编码                          │  │
│       │                                │  创建: pm_project_state(state=30)                  │  │
│       │                                │  创建: pm_project_group + relationship             │  │
│       │                                │  异常: contractNo重复→ERROR                         │  │
│       ▼                                ▼                                                     │
│  [pm_project_header] [pm_project_contract] [pm_project_product_line]                        │  │
│  [pm_project_state]  [pm_project_group]     [pm_project_group_relationship]                 │  │
└──────────────────────────────────────────────────────────────────────────────────────────────┘  │
                                             │                                                 │
                                             ▼                                                 │
┌──────────────────────────────────────────────────────────────────────────────────────────────┐  │
│ 阶段2: 指派服务经理SM (STATE_30→STATE_31)                                                    │  │
│                                                                                              │  │
│  projectService.updateProjectByProjectId()                                                  │  │
│       │  校验: memberCode在fnd_user_info存在                                                │  │
│       │  校验: memberRole=20(SM)合法性                                                      │  │
│       │  转换: memberCode→pm_project_member(memberRole=20)                                  │  │
│       │  转换: 成员变更→状态机计算→STATE_31                                                  │  │
│       │  更新: pm_project_state(state=31)                                                   │  │
│       │  异常: 用户不存在→ERROR                                                              │  │
│       ▼                                                                                      │
│  [pm_project_member] [pm_project_state]                                                     │  │
└──────────────────────────────────────────────────────────────────────────────────────────────┘  │
                                             │                                                 │
                                             ▼                                                 │
┌──────────────────────────────────────────────────────────────────────────────────────────────┐  │
│ 阶段3: 指派项目经理PM (STATE_31→STATE_32)                                                    │  │
│                                                                                              │  │
│  projectService.updateProjectByProjectId()                                                  │  │
│       │  校验: memberCode在fnd_user_info存在                                                │  │
│       │  校验: memberRole=20(PM)合法性                                                      │  │
│       │  转换: memberCode→pm_project_member(memberRole=20)                                  │  │
│       │  转换: 成员变更→状态机计算→STATE_32                                                  │  │
│       │  更新: pm_project_state(state=32)                                                   │  │
│       │  异常: 用户不存在→ERROR                                                              │  │
│       ▼                                                                                      │
│  [pm_project_member] [pm_project_state]                                                     │  │
└──────────────────────────────────────────────────────────────────────────────────────────────┘  │
                                             │                                                 │
                                             ▼                                                 │
┌──────────────────────────────────────────────────────────────────────────────────────────────┐  │
│ 阶段4: 项目执行 (STATE_32)                                                                   │  │
│                                                                                              │  │
│  ┌─ 周报管理 ──────────────────────────────────────────────────────────┐                     │  │
│  │  projectWeeklyService.insertWeekly()                                │                     │  │
│  │       校验: projectId存在                                           │                     │  │
│  │       创建: pm_project_weekly + pm_project_weekly_content           │                     │  │
│  │       异常: 项目不存在→ERROR                                        │                     │  │
│  └────────────────────────────────────────────────────────────────────┘                     │  │
│                                                                                              │  │
│  ┌─ 任务管理 ──────────────────────────────────────────────────────────┐                     │  │
│  │  projectTaskService.insertTask()                                    │                     │  │
│  │       校验: projectId存在                                           │                     │  │
│  │       创建: pm_project_task                                         │                     │  │
│  │       异常: 项目不存在→ERROR                                        │                     │  │
│  └────────────────────────────────────────────────────────────────────┘                     │  │
│                                                                                              │  │
│  ┌─ 成员变更 ──────────────────────────────────────────────────────────┐                     │  │
│  │  projectService.insertProjectMember()                               │                     │  │
│  │       校验: memberCode存在、memberRole合法                          │                     │  │
│  │       转换: 旧成员effectiveTo=当前日期(失效)                        │                     │  │
│  │       创建: 新pm_project_member记录                                 │                     │  │
│  │       触发: 状态机重新计算(可能改变项目状态)                        │                     │  │
│  │       异常: 成员已存在→ERROR                                        │                     │  │
│  └────────────────────────────────────────────────────────────────────┘                     │  │
│                                                                                              │  │
│  ┌─ 通知管理 ──────────────────────────────────────────────────────────┐                     │  │
│  │  projectNotificationService.sendNotification()                      │                     │  │
│  │       创建: pm_project_notification                                 │                     │  │
│  │       创建: pm_project_notification_state(每人一条)                 │                     │  │
│  │       触发: 邮件发送→fnd_mails                                      │                     │  │
│  └────────────────────────────────────────────────────────────────────┘                     │  │
└──────────────────────────────────────────────────────────────────────────────────────────────┘  │
                                             │                                                 │
                                             ▼                                                 │
┌──────────────────────────────────────────────────────────────────────────────────────────────┐  │
│ 阶段5: 闭环流程 (STATE_32→STATE_100)                                                         │
│                                                                                              │  │
│  ┌─ PM发起闭环申请 ────────────────────────────────────────────────────┐                     │  │
│  │  pmClosedLoopService.addPmCLApply()                                 │                     │  │
│  │       校验: nextAcceptPerson(服务经理)有效                          │                     │  │
│  │       校验: 项目当前状态=STATE_32                                   │                     │  │
│  │       创建: pm_cl_evaluation_header(processStatus=10)               │                     │  │
│  │       转换: processStatus×10=closeProcessState→pm_project_state     │                     │  │
│  │       更新: pm_project_state.closeProcessState=10                   │                     │  │
│  │       异常: 服务经理无效→RuntimeException                           │                     │  │
│  └────────────────────────────────────────────────────────────────────┘                     │  │
│                         │                                                                    │  │
│                         ▼                                                                    │  │
│  ┌─ SM审核 ────────────────────────────────────────────────────────────┐                     │  │
│  │  pmClosedLoopService.smAudit()                                      │                     │  │
│  │       校验: 当前用户=SM                                             │                     │  │
│  │       更新: pm_cl_evaluation_header(processStatus=20)               │                     │  │
│  │       更新: pm_project_state.closeProcessState=20                   │                     │  │
│  │       [驳回] processStatus=15, closeProcessState=15                 │                     │  │
│  │       异常: 非SM操作→ERROR                                          │                     │  │
│  └────────────────────────────────────────────────────────────────────┘                     │  │
│                         │                                                                    │  │
│                         ▼                                                                    │  │
│  ┌─ CB回访 ────────────────────────────────────────────────────────────┐                     │  │
│  │  pmClosedLoopService.cbCallback()                                   │                     │  │
│  │       校验: 问卷已填写                                              │                     │  │
│  │       创建: pm_cl_callback + pm_cl_callback_quesnaire               │                     │  │
│  │       更新: pm_cl_evaluation_header(processStatus=30)               │                     │  │
│  │       更新: pm_project_state.closeProcessState=30                   │                     │  │
│  │       [驳回] processStatus=25, closeProcessState=25                 │                     │  │
│  │       异常: 问卷未填写→ERROR                                        │                     │  │
│  └────────────────────────────────────────────────────────────────────┘                     │  │
│                         │                                                                    │  │
│                         ▼                                                                    │  │
│  ┌─ CL闭环确认 ────────────────────────────────────────────────────────┐                     │  │
│  │  pmClosedLoopService.clConfirm()                                    │                     │  │
│  │       校验: 所有回访已完成                                          │                     │  │
│  │       更新: pm_cl_evaluation_header(processStatus=50)               │                     │  │
│  │       更新: pm_project_state(state=100, closeProcessState=50)       │                     │  │
│  │       触发: 项目状态→已闭环                                         │                     │  │
│  │       异常: 回访未完成→ERROR                                        │                     │  │
│  └────────────────────────────────────────────────────────────────────┘                     │  │
└──────────────────────────────────────────────────────────────────────────────────────────────┘  │
                                                                                                  │
                              ◀──────────────────────── D365回调(收货确认) ──────────────────────┘
                              ◀──────────────────────── D365回调(采购订单状态) ──────────────────┘
```

### 1.2 售前测试流程数据流向

```
SMS ──同步──▶ pm_presales_lend_info_from_sms ──转换──▶ pm_presales_project_header
SMS ──同步──▶ pm_presales_lend_order_from_sms  ──转换──▶ pm_presales_project_product_line
                                                          │
                                                          ▼
┌──────────────────────────────────────────────────────────────────────────┐
│ 售前测试流程                                                              │
│                                                                          │
│  presalesService.startPresalesFlow()                                     │
│       校验: projectCode非空                                              │
│       生成: presalesCode自动编码                                         │
│       创建: pm_presales_project_header(status=10)                        │
│       创建: pm_presales_project_product_line                             │
│       异常: projectCode重复→ERROR                                        │
│                          │                                               │
│                          ▼                                               │
│  presalesService.assignSM()                                              │
│       更新: pm_presales_project_header(smCode, status=20)                │
│       创建: Activiti流程实例                                             │
│                          │                                               │
│                          ▼                                               │
│  presalesService.completeTask() → Activiti流程驱动                       │
│       更新: pm_presales_project_header(status递增)                       │
│       创建: dp_act_unify_task(统一任务记录)                              │
│       创建: fnd_act_hi_comment(审批意见)                                 │
│                          │                                               │
│                          ▼                                               │
│  presalesService.presalesCallback()                                      │
│       创建: pm_presales_project_callback                                 │
│       创建: pm_presales_project_duration                                 │
│                          │                                               │
│                          ▼                                               │
│  presalesService.presalesClose()                                         │
│       更新: pm_presales_project_header(status=100)                       │
│       触发: 可转为正式项目→projectService.insertProject()                │
└──────────────────────────────────────────────────────────────────────────┘
```

### 1.3 转包流程数据流向

```
┌──────────────────────────────────────────────────────────────────────────┐
│ 转包流程                                                                  │
│                                                                          │
│  subcontractService.insertSubcontract()                                  │
│       校验: subcontractName唯一                                          │
│       创建: pm_subcontract_project_header(status=10)                     │
│       创建: pm_subcontract_project_line(设备清单)                        │
│       创建: Activiti流程实例                                             │
│                          │                                               │
│                          ▼                                               │
│  Activiti流程驱动 → GENERATE_CON节点                                     │
│       转换: amount→PurchAmount, facilitatorId→VendAccount                │
│       调用: D365 API生成采购订单                                         │
│       异常: D365调用失败→流程挂起，重试机制                              │
│                          │                                               │
│                          ▼                                               │
│  D365回调: 收货确认                                                      │
│       转换: packingSlipId/purchIds→customInfo JSON                       │
│       更新: pm_subcontract_project_payment.customInfo                    │
│                          │                                               │
│                          ▼                                               │
│  subcontractService.addPayment()                                         │
│       校验: amount>0, facilitatorId非空                                  │
│       创建: pm_subcontract_project_payment                               │
│                          │                                               │
│                          ▼                                               │
│  subcontractService.subcontractCallback()                                │
│       创建: pm_subcontract_project_callback                              │
│       更新: pm_subcontract_project_header(status=100)                    │
└──────────────────────────────────────────────────────────────────────────┘
```

### 1.4 数据同步流向

```
┌──────────────────────────────────────────────────────────────────────────────────────┐
│ 外部系统数据同步流向 (Quartz定时任务)                                                  │
│                                                                                      │
│  ┌─ SAP同步 ────────────────────────────────────────────────────────────────────┐    │
│  │  SapDataSyncJob.execute()                                                   │    │
│  │       读取: SAP RFC接口                                                      │    │
│  │       转换: SAP字段→pm_order_data_from_erp_sap                              │    │
│  │       校验: contractNo非空、profitCenter非空                                 │    │
│  │       写入: INSERT ON DUPLICATE KEY UPDATE                                  │    │
│  │       日志: fnd_data_refresh_log(syncType='SAP')                            │    │
│  │       异常: RFC连接失败→记录日志，下次重试                                   │    │
│  └─────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                      │
│  ┌─ D365同步 ───────────────────────────────────────────────────────────────────┐    │
│  │  D365DataSyncJob.execute()                                                  │    │
│  │       读取: D365 OData API                                                  │    │
│  │       转换: D365字段→pm_order_data_from_erp_d365                            │    │
│  │       校验: contractNo非空                                                   │    │
│  │       写入: INSERT ON DUPLICATE KEY UPDATE                                  │    │
│  │       日志: fnd_data_refresh_log(syncType='D365')                           │    │
│  │       异常: API超时→记录日志，下次重试                                       │    │
│  └─────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                      │
│  ┌─ SMS同步 ────────────────────────────────────────────────────────────────────┐    │
│  │  SmsDataSyncJob.execute()                                                   │    │
│  │       读取: SMS数据库视图                                                    │    │
│  │       转换: SMS字段→pm_project_property_from_sms                            │    │
│  │       校验: projectCode非空、officeCode存在性                               │    │
│  │       写入: 清空后重写(DELETE + INSERT)                                     │    │
│  │       日志: fnd_data_refresh_log(syncType='SMS')                            │    │
│  │       异常: 数据库连接失败→记录日志，下次重试                               │    │
│  └─────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                      │
│  ┌─ OA同步 ─────────────────────────────────────────────────────────────────────┐    │
│  │  OaDataSyncJob.execute()                                                    │    │
│  │       读取: OA数据库视图                                                     │    │
│  │       转换: OA字段→pm_person_from_oa                                        │    │
│  │       校验: salesmanCode非空                                                 │    │
│  │       写入: INSERT ON DUPLICATE KEY UPDATE                                  │    │
│  │       日志: fnd_data_refresh_log(syncType='OA')                             │    │
│  │       异常: 数据库连接失败→记录日志，下次重试                               │    │
│  └─────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                      │
│  ┌─ EHR同步 ────────────────────────────────────────────────────────────────────┐    │
│  │  EhrDataSyncJob.execute()                                                   │    │
│  │       读取: EHR API                                                         │    │
│  │       转换: EHR字段→fnd_user_info(人员信息更新)                             │    │
│  │       校验: username非空、email格式                                          │    │
│  │       写入: UPDATE fnd_user_info                                            │    │
│  │       日志: fnd_data_refresh_log(syncType='EHR')                            │    │
│  └─────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                      │
│  ┌─ 订单合并 ───────────────────────────────────────────────────────────────────┐    │
│  │  OrderDataMergeJob.execute()                                                │    │
│  │       读取: pm_order_data_from_erp_sap + pm_order_data_from_erp_d365        │    │
│  │       转换: 按contractNo去重合并→pm_order_data_from_erp_source              │    │
│  │       校验: contractNo非空、profitCenter非空                                │    │
│  │       写入: 清空后重写(DELETE + INSERT)                                     │    │
│  │       日志: fnd_data_refresh_log(syncType='MERGE')                          │    │
│  └─────────────────────────────────────────────────────────────────────────────┘    │
└──────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. 数据生命周期管理策略

### 2.1 核心业务数据生命周期

| 数据类型 | 创建时机 | 修改规则 | 归档策略 | 删除策略 |
|----------|----------|----------|----------|----------|
| 项目主数据 (pm_project_header) | ERP订单创建项目时(projectService.insertProject) | 仅SM/PM/管理员可修改；状态≥STATE_32后部分字段只读；闭环后仅管理员可修改 | 闭环3年后归档到历史表(pm_project_header_archive) | 物理删除仅管理员操作(clearProject)，需二次确认 |
| 项目状态 (pm_project_state) | 项目创建时初始化；每次状态变更新增记录 | 仅追加，不修改历史记录；更新当前状态字段 | 随项目主数据归档 | 随项目主数据删除 |
| 项目成员 (pm_project_member) | 指派SM/PM时创建 | 变更时旧记录设置effectiveTo(失效)，新增记录；不物理修改历史成员 | 随项目主数据归档 | 仅删除effectiveTo为null的当前成员 |
| 项目合同 (pm_project_contract) | 项目创建时从ERP订单映射 | 合同金额等关键字段不可修改 | 随项目主数据归档 | 随项目主数据删除 |
| 项目产品线 (pm_project_product_line) | 项目创建时从ERP订单映射 | 可增删产品线，不可修改合同关联的产品线 | 随项目主数据归档 | 支持物理删除(delProductLine) |
| 项目周报 (pm_project_weekly + content) | SM/PM每周创建 | 创建后当周可修改，下周锁定 | 闭环1年后归档 | 不支持删除，仅可清空内容 |
| 项目任务 (pm_project_task) | SM/PM创建任务时 | 创建者可修改状态和内容 | 闭环1年后归档 | 不支持删除 |
| 项目通知 (pm_project_notification + state) | 系统自动触发或手动发送 | 通知内容不可修改；状态可更新(已读/未读) | 6个月后归档 | 不支持删除 |
| 项目交付件 (pm_project_deliver) | PM上传交付件时 | 可修改文件和描述 | 随项目主数据归档 | 支持物理删除(delProjectDeliver) |
| 项目日志 (pm_project_log) | 系统自动记录 | 不可修改 | 闭环1年后归档 | 不支持删除 |

### 2.2 售前测试数据生命周期

| 数据类型 | 创建时机 | 修改规则 | 归档策略 | 删除策略 |
|----------|----------|----------|----------|----------|
| 售前项目 (pm_presales_project_header) | SMS同步或手动创建 | 流程中可修改；完成后只读 | 完成2年后归档 | 管理员可物理删除 |
| 售前产品线 (pm_presales_project_product_line) | 售前项目创建时 | 可增删 | 随售前项目归档 | 支持物理删除 |
| 售前回访 (pm_presales_project_callback) | 回访时创建 | 创建后可修改 | 随售前项目归档 | 不支持删除 |
| 售前耗时 (pm_presales_project_duration) | 流程节点完成时自动记录 | 不可修改 | 随售前项目归档 | 不支持删除 |

### 2.3 闭环与回访数据生命周期

| 数据类型 | 创建时机 | 修改规则 | 归档策略 | 删除策略 |
|----------|----------|----------|----------|----------|
| 闭环评价 (pm_cl_evaluation_header) | PM发起闭环申请时 | 流程中按状态流转修改；闭环后只读 | 项目闭环3年后归档 | 不支持删除 |
| 回访申请 (pm_cl_callback) | CB回访时创建 | 创建后可修改 | 随闭环评价归档 | 管理员可删除 |
| 问卷模板 (pm_cl_quesnaire_template_header/line/options) | 管理员创建模板时 | 生效后不可修改，需新建版本 | 2年未使用归档 | 仅未生效模板可删除 |
| 问卷结果 (pm_cl_quesnaire_result_header/line) | 填写问卷时创建 | 提交后不可修改 | 随闭环评价归档 | 不支持删除 |

### 2.4 转包数据生命周期

| 数据类型 | 创建时机 | 修改规则 | 归档策略 | 删除策略 |
|----------|----------|----------|----------|----------|
| 转包项目 (pm_subcontract_project_header) | 创建转包申请时 | 流程中可修改；完成后只读 | 完成2年后归档 | 管理员可物理删除 |
| 转包设备清单 (pm_subcontract_project_line) | 转包项目创建时 | 可增删 | 随转包项目归档 | 支持物理删除 |
| 转包付款 (pm_subcontract_project_payment) | 付款节点触发时 | customInfo可更新(收货信息) | 随转包项目归档 | 不支持删除 |
| 转包价格 (pm_subcontract_project_price) | 转包项目创建时 | 不可修改 | 随转包项目归档 | 不支持删除 |

### 2.5 系统管理数据生命周期

| 数据类型 | 创建时机 | 修改规则 | 归档策略 | 删除策略 |
|----------|----------|----------|----------|----------|
| 用户 (fnd_user_info) | 管理员创建或EHR同步 | 管理员可修改；用户可修改密码/邮箱/默认页面 | 离职后标记inactive，不物理删除 | 不支持物理删除，仅逻辑删除 |
| 角色 (fnd_roles) | 管理员创建 | 管理员可修改 | 不归档 | 有用户关联时不可删除 |
| 菜单 (fnd_menus) | 系统预置 | 仅开发人员通过SQL修改 | 不归档 | 不支持删除 |
| 基础数据 (fnd_basic_data) | 管理员创建 | 管理员可修改 | 不归档 | 有引用时不可删除 |
| 操作日志 (tb_sys_log / fnd_operate_log) | 系统自动记录 | 不可修改 | 6个月后归档到日志表 | 定期清理(保留6个月) |
| 邮件记录 (fnd_mails) | 系统发送邮件时 | 不可修改 | 3个月后归档 | 定期清理(保留3个月) |
| 数据同步日志 (fnd_data_refresh_log) | 同步任务执行时 | 不可修改 | 6个月后归档 | 定期清理(保留6个月) |

### 2.6 技术公告数据生命周期

| 数据类型 | 创建时机 | 修改规则 | 归档策略 | 删除策略 |
|----------|----------|----------|----------|----------|
| 公告主表 (prob_main) | 创建公告时 | 审核前可修改；发布后仅管理员可修改 | 3年后归档 | 仅创建者/管理员可删除 |
| 修复方案 (prob_restore) | 创建修复方案时 | 审核前可修改 | 随公告归档 | 仅创建者/管理员可删除 |
| 修复过程 (prob_restore_process) | 状态变更时记录 | 可追加更新 | 随公告归档 | 不支持删除 |
| 修复周报 (prob_restore_weekly) | 上传周报附件时 | 不可修改 | 随公告归档 | 不支持删除 |
| 软件版本参考 (prob_soft_version) | 版本号首次出现时 | 不可修改(INSERT IGNORE) | 不归档 | 不支持删除 |
| 影响版本明细 (prob_softwares) | 创建/更新公告时 | 先失效旧记录(datastate=0)再插入新记录 | 随公告归档 | 逻辑删除(datastate=0) |
| 阅读日志 (prob_read_log) | 用户阅读时自动记录 | status可从0更新为1 | 6个月后归档 | 定期清理 |

---

## 3. 数据同步失败处理机制

### 3.1 同步任务失败处理流程

```
同步任务执行
    │
    ▼
┌─ 开始同步 ─┐
│             │
│  1.记录开始时间(startTime)              │
│  2.读取外部数据源                       │
│             │
▼             │
外部数据源可用？── 否 ──▶ 记录连接失败日志
│                            │
│ 是                         ▼
│                    fnd_data_refresh_log:
▼                    status=FAIL
数据校验通过？              errorMessage=连接超时/拒绝
│                           recordCount=0
│ 否                        │
▼                           ▼
记录校验失败日志        ──▶ 等待下次定时任务触发
fnd_data_refresh_log:        (不自动重试，依赖Quartz调度)
status=FAIL
errorMessage=校验失败详情
recordCount=0
│
│ 是
▼
写入目标表
│
▼
写入成功？── 否 ──▶ 记录写入失败日志
│                      fnd_data_refresh_log:
│ 是                   status=FAIL
│                      errorMessage=SQL异常详情
▼                      recordCount=已写入数
记录成功日志
fnd_data_refresh_log:
status=SUCCESS
recordCount=实际写入数
endTime=当前时间
│
▼
同步完成
```

### 3.2 各同步源失败处理策略

| 同步源 | 失败场景 | 检测方式 | 处理策略 | 数据恢复 | 通知机制 |
|--------|----------|----------|----------|----------|----------|
| SAP RFC | 网络超时/连接拒绝 | RFC调用异常捕获 | 记录FAIL日志，等待下次Quartz调度 | 下次同步时全量覆盖 | 无自动通知，需查看日志 |
| SAP RFC | 数据格式异常 | 字段解析异常捕获 | 跳过异常记录，继续处理其他记录 | 异常记录下次重试 | 日志记录异常详情 |
| D365 OData | API超时(>30s) | HTTP连接超时异常 | 记录FAIL日志，等待下次调度 | 下次同步时全量覆盖 | 无自动通知 |
| D365 OData | 认证失败(401) | HTTP状态码检查 | 记录FAIL日志，需人工检查token | 修复token后下次同步正常 | 需人工检查日志 |
| SMS数据库 | 数据库连接失败 | JDBC连接异常捕获 | 记录FAIL日志，等待下次调度 | 下次同步时清空后重写 | 无自动通知 |
| SMS数据库 | officeCode不存在 | 业务校验失败 | 跳过该记录，记录WARN日志 | 新增officeCode后下次同步正常 | 日志记录 |
| OA数据库 | 数据库连接失败 | JDBC连接异常捕获 | 记录FAIL日志，等待下次调度 | 下次同步时UPSERT | 无自动通知 |
| EHR API | 接口不可用 | HTTP连接异常 | 记录FAIL日志，等待下次调度 | 下次同步时UPSERT | 无自动通知 |
| 订单合并 | SAP/D365源数据为空 | 源表记录数=0 | 跳过合并，记录WARN日志 | 源数据同步成功后下次合并正常 | 日志记录 |

### 3.3 数据一致性保障机制

| 机制 | 说明 | 适用场景 |
|------|------|----------|
| INSERT ON DUPLICATE KEY UPDATE | 基于唯一键的UPSERT操作，存在则更新，不存在则插入 | SAP/D365/OA数据同步 |
| 清空后重写(DELETE + INSERT) | 先清空目标表，再全量写入 | SMS数据同步、订单合并 |
| 事务控制 | 同步操作在单个数据库事务中执行，失败则回滚 | 所有同步任务 |
| 同步日志记录 | fnd_data_refresh_log记录每次同步的状态、记录数、耗时 | 所有同步任务 |
| 定时调度重试 | Quartz定时任务按固定间隔重新执行，实现自动重试 | 所有同步任务 |
| 手动触发同步 | 管理员可通过界面手动触发同步任务 | 紧急数据修复场景 |

### 3.4 数据对账机制

| 对账项 | 对账方式 | 对账频率 | 异常处理 |
|--------|----------|----------|----------|
| SAP订单数量 | 比较pm_order_data_from_erp_sap记录数与SAP源系统 | 每日 | 记录差异日志，人工核查 |
| D365订单数量 | 比较pm_order_data_from_erp_d365记录数与D365源系统 | 每日 | 记录差异日志，人工核查 |
| SMS项目属性 | 比较pm_project_property_from_sms与SMS源系统项目数 | 每周 | 差异>5%时告警 |
| OA人员信息 | 比较pm_person_from_oa与OA系统活跃人员数 | 每周 | 差异>10%时告警 |
| 合同号关联 | 检查pm_order_data_from_erp_source中未创建项目的合同号 | 每日 | 列出待创建项目的合同号 |
| 同步日志异常 | 检查fnd_data_refresh_log中status=FAIL的记录 | 每日 | 连续3次失败需人工介入 |

---

## 4. 数据流转步骤与Service方法映射

### 4.1 项目创建流程

| 步骤 | Service方法 | 操作表 | 数据转换 | 校验机制 | 异常处理 |
|------|-------------|--------|----------|----------|----------|
| 1.查询合同号 | projectService.queryProjectByContractNo() | pm_order_data_from_erp_source→R | contractNo映射 | contractNo非空 | 返回空列表 |
| 2.生成项目编码 | projectService.queryProjectCode() | pm_project_header→R | 自动编码规则 | 编码唯一性 | 编码冲突时重新生成 |
| 3.创建项目主表 | projectService.insertProject() | pm_project_header→C | ERP字段→泛化字段(column001-020) | projectCode唯一 | 抛出CustomRuntimeException |
| 4.创建合同关联 | projectService.insertProject() | pm_project_contract→C | contractNo直接映射 | contractNo唯一 | 抛出异常 |
| 5.创建产品线 | projectService.insertProject() | pm_project_product_line→C | itemCode/itemName直接映射 | itemCode非空 | 抛出异常 |
| 6.初始化状态 | projectService.insertProject() | pm_project_state→C | 初始state=30 | 状态值合法性 | 抛出异常 |
| 7.创建项目组 | projectService.insertProject() | pm_project_group→C + pm_project_group_relationship→C | projectGroupCode自动生成 | ⚠️ 编码并发安全 | FIXME已标注 |

### 4.2 项目成员变更流程

| 步骤 | Service方法 | 操作表 | 数据转换 | 校验机制 | 异常处理 |
|------|-------------|--------|----------|----------|----------|
| 1.查询用户 | userManageService.queryUserInfo() | fnd_user_info→R | username→memberCode | 用户存在性 | 返回ERROR |
| 2.失效旧成员 | projectService.updateProjectMember() | pm_project_member→U | effectiveTo=当前日期 | 旧成员存在性 | 无旧成员则跳过 |
| 3.创建新成员 | projectService.insertProjectMember() | pm_project_member→C | memberCode/memberRole映射 | 角色编码合法性(10/20/30/40/60) | 抛出异常 |
| 4.状态机计算 | projectService.updateProjectByProjectId() | pm_project_state→U | 成员角色→状态值 | 状态转换合法性 | 不合法时不更新 |
| 5.发送通知 | projectNotificationService.sendNotification() | pm_project_notification→C | 通知内容模板填充 | 通知接收人非空 | 跳过空接收人 |

### 4.3 闭环流程

| 步骤 | Service方法 | 操作表 | 数据转换 | 校验机制 | 异常处理 |
|------|-------------|--------|----------|----------|----------|
| 1.PM发起闭环 | pmClosedLoopService.addPmCLApply() | pm_cl_evaluation_header→C | processStatus=10 | nextAcceptPerson有效性 | RuntimeException |
| 2.更新闭环状态 | pmClosedLoopService.addPmCLApply() | pm_project_state→U | processStatus×10=closeProcessState | closeProcessState值范围 | 抛出异常 |
| 3.SM审核通过 | pmClosedLoopService.smAudit() | pm_cl_evaluation_header→U | processStatus=20 | 当前用户=SM | 返回ERROR |
| 4.SM审核驳回 | pmClosedLoopService.smAudit() | pm_cl_evaluation_header→U | processStatus=15 | 驳回原因非空 | 返回ERROR |
| 5.CB回访 | pmClosedLoopService.cbCallback() | pm_cl_callback→C | 回访信息映射 | 问卷已填写 | 返回ERROR |
| 6.CL闭环确认 | pmClosedLoopService.clConfirm() | pm_cl_evaluation_header→U + pm_project_state→U | processStatus=50, state=100 | 所有回访完成 | 返回ERROR |

---

## 5. 异常处理汇总

### 5.1 数据流转异常分类

| 异常类型 | 典型场景 | 处理方式 | 用户提示 | 日志记录 |
|----------|----------|----------|----------|----------|
| 数据校验异常 | 必填字段为空、格式错误 | 阻止操作，返回ERROR | "请填写必填项" | WARN级别 |
| 唯一性冲突 | 重复创建(合同号/用户名) | 阻止操作，返回ERROR | "记录已存在" | WARN级别 |
| 引用完整性异常 | 外键引用不存在 | 阻止操作，返回ERROR | "关联数据不存在" | WARN级别 |
| 业务规则异常 | 状态转换不合法 | 阻止操作，返回ERROR | "操作不允许" | WARN级别 |
| 数据库异常 | SQL执行失败、死锁 | 事务回滚，返回ERROR | "操作失败，请重试" | ERROR级别 |
| 外部系统异常 | SAP/D365/OA连接失败 | 记录日志，等待重试 | 无直接提示(后台任务) | ERROR级别 |
| 并发冲突 | 乐观锁版本不匹配 | 提示用户刷新重试 | "数据已被修改，请刷新" | WARN级别 |
| 文件操作异常 | 上传文件失败、路径不存在 | 返回ERROR | "文件操作失败" | ERROR级别 |
| 权限异常 | 无操作权限 | 返回404或ERROR | "无权限" | INFO级别 |

### 5.2 异常处理代码模式

```java
// Service层异常处理模式
public void someBusinessOperation() {
    try {
        // 1. 数据校验
        validateInput(params);
        
        // 2. 业务逻辑
        doBusinessLogic(params);
        
        // 3. 记录操作日志
        this.log("操作描述");
        
    } catch (CustomRuntimeException e) {
        // 业务异常：已知错误，直接抛出
        throw e;
    } catch (Exception e) {
        // 系统异常：未知错误，包装后抛出
        logger.error("业务操作异常", e);
        throw new CustomRuntimeException("操作失败，请重试");
    }
}
```
