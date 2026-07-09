# PMS 业务域切分图(Domain Map)

> **来源**:基于 PMS-struts `struts-sys.xml` Action 映射 + `com.dp.plat.*` 包结构 + BPMN 流程文件 + iBatis SQL-map 配置反推。
> **日期**:2026-07-09
> **域数**:9(目标区间 5-10)

---

## 域间依赖关系(无环校验)

```
001-user-auth ─────────────────────────────────┐ (基础设施,被所有域依赖)
                                               │
004-workflow-workspace ──► 001                 │ (工作流引擎依赖用户)
                                               │
008-external-integration ─► 001                │ (外部集成依赖用户)
                                               │
009-system-base ─► 001                         │ (系统基础依赖用户)
                                               │
002-presales-product ─► 001, 004               │ (售前/产品依赖用户、工作流)
                                               │
003-project-delivery ─► 001, 002, 004, 008     │ (项目交付依赖售前、工作流、外部集成)
                                               │
005-warranty-callback ─► 001, 003, 004         │ (质保回访依赖项目、工作流)
                                               │
006-maintenance-supervision ─► 001, 003        │ (维保监管依赖项目)
                                               │
007-subcontract ─► 001, 003, 004               │ (分包依赖项目、工作流)
```

**拓扑序**:001 → 004/008/009 → 002 → 003 → 005/006/007

依赖方向严格向下流动,无环。

---

## 域清单

### 001-user-auth(用户与权限)

- **职责**:用户认证、授权、组织架构(角色/部门/菜单/权限)、登录会话、密码管理、CAS 单点登录。
- **核心聚合根**:User、Role、Department、Menu、Permission
- **证据**:
  - Action:`Login`、`UserManageAction`、`RoleManageAction`、`DepartmentManageAction`(struts-sys.xml:10-212)
  - 新架构:`core/controller/admin/UserController.java`、`RoleController.java`、`MenuController.java`
  - DAO:`LoginDao`、`PasswordDao`(PMS-struts/src/com/dp/plat/dao)、`UserMapper`、`RoleMapper`、`MenuMapper`、`DepartmentMapper`(core)
  - 上下文:`UserContext`、`HttpContext`、`SpringContext`
  - CAS:`core/cas/CasLogoutFilter.java`、`SingleSignOutHandler.java`
- **涉及表(待 Task 6 细化)**:user、role、department、menu、resource、permission、user_role、role_menu、role_permission、user_info、user_login_record、company
- **上下游**:
  - 上游:无(基础域)
  - 下游:所有其他域
- **分支**:`001-user-auth`

---

### 002-presales-product(售前与产品)

- **职责**:售前测试项目管理、技术公告(Prob)发布与修复跟踪、产品与产品组件维护、软件/设备版本解析与匹配。
- **核心聚合根**:Presales、Prob、ProbProduct、ProductComponent、SoftVersion
- **证据**:
  - Action:`PresalesAction`(presales_*)、`ProbManageAction`(prob_*、probProduct_*、component_*)(struts-sys.xml:217-278, 285-296)
  - 包:`com.dp.plat.prob.{action,service,dao,bean,vo,param,version,util,exception}`
  - BPMN:`PMS-struts/bpmn/Presales.bpmn`
  - 版本策略:`prob/version/SoftVersionParserFactory.java`、`VersionParserFactory.java`、`SoftVersionStrategy.java`
  - 实体:`Prob.java`、`ProbProduct.java`、`ProductComponent.java`、`SoftVersion.java`、`DeviceVersionInfo.java`、`ProbRestore.java`、`ProbFile.java`、`ProbReadLog.java`
- **涉及表(待 Task 6 细化)**:presales、prob、prob_product、product_component、soft_version、device_version、prob_restore、prob_restore_weekly、prob_file、prob_read_log、prob_statistic
- **上下游**:
  - 上游:001-user-auth、004-workflow-workspace
  - 下游:003-project-delivery
- **分支**:`002-presales-product`

---

### 003-project-delivery(项目交付)

- **职责**:交付项目全生命周期管理——项目创建/修改/清理、项目成员、合同合并/拆分、发货清单与设备清单、项目周报、项目通知批示、现场验货、软件版本更新、串货项目。
- **核心聚合根**:Project、ProjectMember、ProjectContract、ProjectWeekly、ProjectNotification
- **证据**:
  - Action:`ProjectAction`(ProjectManage/Create/Modify/PlanEdit/BatchChangeProjectMember/clearProject/DownloadFile/exportSpotCheck/exportOverWarrantyRemind/createCHProject)(struts-sys.xml:318-440)
  - 子域 Action:`ProjectMemberAction`、`ProjectContractAction`、`ProjectFileAction`、`ProjectWeeklyAction`、`ProjectNotificationAction`(struts-sys.xml:1226-1337)
  - AJAX:`projectAjax_*`、`checkMergeContract`、`importProject`、`updateSoftVersion`(struts-sys.xml:1051-1145)
  - DAO:`ProjectDao`(PMS-struts/src/com/dp/plat/dao)
  - 工具:`ProjectUtils.java`、`ProjectInspectionMailer.java`
- **涉及表(待 Task 6 细化)**:project、project_member、project_contract、project_file、project_weekly、project_weekly_feedback、project_notification、shipment、order_data、real_order_data、install_address、spot_check、soft_change_log
- **上下游**:
  - 上游:001-user-auth、002-presales-product、004-workflow-workspace、008-external-integration
  - 下游:005-warranty-callback、006-maintenance-supervision、007-subcontract
- **分支**:`003-project-delivery`

---

### 004-workflow-workspace(工作流与工作空间)

- **职责**:BPMN 流程引擎(Activiti)的流程部署/查看/任务管理/委派/历史,以及用户工作空间(待办事项)。
- **核心聚合根**:ProcessDefinition、Deployment、Task、Delegate、WorkSpace
- **证据**:
  - Action:`WorkFlowAction`(WorkFlowAction/NewDeploy/DelDeployment/ViewImage/SelfTaskManager/ViewTaskForm/HisTaskForm/SubmitTask/TaskManager/ProcDefDelegate*)(struts-sys.xml:830-903)
  - Action:`WorkSpaceAction`(Workspace、updateNotifyState)(struts-sys.xml:404-411)
  - DAO:`WorkflowDao`、`WorkSpaceDao`(PMS-struts/src/com/dp/plat/dao)
  - 工具:`WorkflowUtil.java`
  - 引擎:`PMS-activiti/` 模块、`config-spring/activiti-context.xml`
  - 流程文件:`PMS-struts/bpmn/*.bpmn`(本域管理部署,流程定义归属各业务域)
- **涉及表(待 Task 6 细化)**:act_re_deployment、act_re_procdef、act_ru_task、act_ru_execution、act_hi_taskinst、act_hi_procinst、act_ge_bytearray、act_id_*、workspace、delegate、proc_def_delegate
- **上下游**:
  - 上游:001-user-auth
  - 下游:002-presales-product、003-project-delivery、005-warranty-callback、007-subcontract
- **分支**:`004-workflow-workspace`

---

### 005-warranty-callback(质保回访与 PM 闭环)

- **职责**:项目质保回访申请与执行、维保回访、PM 闭环(项目交付后闭环评估)、闭环问卷管理、回访流程驱动。
- **核心聚合根**:WarrantyCallback、ProjectWarrantyCallback、CallBack、PmClosedLoop、PmClosedLoopQuesnaire
- **证据**:
  - Action:`WarrantyCallbackAction`(warrantyCallback_*)(struts-sys.xml:502-505, 815-821)
  - Action:`CallBackAction`(callback_*)(struts-sys.xml:651-654)
  - Action:`PmClosedLoopAction`(PmClosedLoop_*、PmClosedLoopSub_*、pmCLoopAjax_*)(struts-sys.xml:298-316, 632-643, 1076-1084)
  - Action:`PmClosedLoopQuesnaireAction`(PmClosedLoopQuesnaire、PmClQues_*、AddPmClosedLoopQuesnaire、SubmitPmClLQues、SeePmClosedLoopQuesnaire、EditPmClosedLoopQuesnaire)(struts-sys.xml:155-210)
  - 包:`com.dp.plat.warrantyCallback.{action,service,dao,entity,vo,decorators}`
  - 实体:`ProjectWarrantyCallback.java`、`ProjectMaintenance.java`(维保关联)
  - 工具:`PmClosedLoopUtil.java`、`PmClosedLoopMark*.java`、`PmClosedLoopConstant.java`、`QuestionnarieUtil.java`
  - BPMN:`PMS-struts/bpmn/CallBack.bpmn`、`PMS-struts/bpmn/PmClosedLoop.bpmn`
- **涉及表(待 Task 6 细化)**:warranty_callback、project_warranty_callback、callback、pm_closed_loop、pm_closed_loop_quesnaire、pm_closed_loop_result、renewal_intention
- **上下游**:
  - 上游:001-user-auth、003-project-delivery、004-workflow-workspace
  - 下游:无
- **分支**:`005-warranty-callback`

---

### 006-maintenance-supervision(维保与监管)

- **职责**:项目维保计划与执行、维保日报/季报、项目督查(监管)、借用维保额度管理。
- **核心聚合根**:ProjectMaintenance、ProjectSupervision、LendMaintenance
- **证据**:
  - Action:`MaintenanceAction`(maintenance_*、maintenanceAjax_*)(struts-sys.xml:490-493, 801-805, 1204-1208)
  - Action:`SupervisionAction`(supervision_*、supervisionAjax_*)(struts-sys.xml:496-499, 808-812, 1211-1215)
  - Action:`LendMaintenanceAction`(LendMaintenance、AddLendMaintenance、SeeLendMaintenance、UpdateLendMaintenance、DeleteLendMaintenance、IsLendQuotaRepeat)(struts-sys.xml:928-962)
  - 包:`com.dp.plat.maintenance.{action,aop,entity,quartz,vo,decorators}`
  - 包:`com.dp.plat.supervision.{entity,vo}`
  - 实体:`ProjectMaintenance.java`、`ProjectSupervision.java`
  - 定时:`maintenance/quartz/MaintenanceDailyReportMailer.java`、`MaintenanceDepartmentSectaryJob.java`、`MaintenanceServiceQuarterMailer.java`
  - AOP:`maintenance/aop/ProjectStateUpdateAspect.java`
- **涉及表(待 Task 6 细化)**:project_maintenance、project_supervision、lend_maintenance、lend_quota、maintenance_daily_report
- **上下游**:
  - 上游:001-user-auth、003-project-delivery
  - 下游:无
- **分支**:`006-maintenance-supervision`

---

### 007-subcontract(分包)

- **职责**:项目转包(分包)全流程——转包创建/审核/执行/查询,转包设备序列号选择、转包工程费、转包付款、转包附件、转包回访、转包评论。
- **核心聚合根**:Subcontract
- **证据**:
  - Action:`SubcontractAction`(subcontract_*、chooseShipmentInfo、querySubcontractLine、chooseSubcontractProject、queryContractNoEngineeFee、querySubcontractPayment、querySubcontractDeliver、querySubcontractCallback、querySubcontractComment、checkSubcontractName、subcontractAjax_*)(struts-sys.xml:442-473, 701-778, 1184-1194)
  - BPMN:`PMS-struts/bpmn/Subcontract.bpmn`、`Subcontract2.bpmn`、`SubcontractCallBack.bpmn`、`SubcontractCallBack2.bpmn`
- **涉及表(待 Task 6 细化)**:subcontract、subcontract_line、subcontract_payment、subcontract_deliver、subcontract_callback、subcontract_comment、subcontract_project
- **上下游**:
  - 上游:001-user-auth、003-project-delivery、004-workflow-workspace
  - 下游:无
- **分支**:`007-subcontract`

---

### 008-external-integration(外部集成)

- **职责**:与外部业务系统(SAP/ERP、D365、CRM、OA、EHR、ITR、SMS、SSE、License)的数据同步——订单、人员、售前信息、项目属性、市场关系、合同验收交付推送。
- **核心聚合根**:SyncTask(各 `Gain*By*` 同步任务的抽象)
- **证据**:
  - 定时任务:`com.dp.plat.job.GainOrderBySAP`、`GainOrderByERP`、`GainPersonByOA`、`GainPersonByEHR`、`GainDataFromITR`、`GainDataFromLicense`、`GainPresalesInfoBySMS`、`GainPresalesInfoFromOA`、`GainPrjPropertyBySMS`、`GainPrjRealProjectLineBySMS`、`GainMarketRelationsBySMS`、`PlanGetBySMS`、`PushContractAcceptanceDeliveryJob`(PMS-struts/src/com/dp/plat/job)
  - 抽象基类:`AbstractSynchronizeTask.java`
  - SQL-map:`config-ibaits/sqlMapConfigSAP.xml`、`sqlMapConfigD365.xml`、`sqlMapConfigCRM.xml`、`sqlMapConfigOA.xml`、`sqlMapConfigEHR.xml`、`sqlMapConfigITR.xml`、`sqlMapConfigSMS.xml`、`sqlMapConfigSSE.xml`、`sqlMapConfigLicense.xml`
  - 独立模块:`PMS-ext-d365/`、`pms-ext-fp/`
  - 同步日志:`core/dao/SyncLogMapper.java`、`core/service/impl/SyncLogService.java`
  - 数据源:`core/config/RoutingDataSource.java`、`core/annotation/DataSource.java`、`core/aop/DataSourceAspect.java`
- **涉及表(待 Task 6 细化)**:sync_log、各外部系统只读视图/镜像表(SAP/D365/CRM/OA/EHR/ITR/SMS/SSE/License)
- **上下游**:
  - 上游:001-user-auth
  - 下游:003-project-delivery(提供订单/人员/售前数据)
- **分支**:`008-external-integration`

---

### 009-system-base(系统基础与报表)

- **职责**:基础数据(字典)维护、操作日志、合格证管理、报表统计与数据分析、文件上传下载、邮件通知与通知模板、系统变量、集群管理、数据导出。
- **核心聚合根**:BasicData、OpLog、Certificate、Report、Dictionary、MailInfo、NotifyTemplate、SystemVariable
- **证据**:
  - Action:`BasicDataManageAction`(BasicdataManage/Update/Insert、findBasicDataId、executeSql)(struts-sys.xml:134-152, 1106-1125)
  - Action:`OperateLogAction`(LogManage、ExportLogAll、syncTask)(struts-sys.xml:923-971, 974-979)
  - Action:`Certificate`(certificate、uploadSealInfo、queryCertificate)(struts-sys.xml:476-487, 795-798)
  - Action:`ReportAction`(report_*、assignedRate、loadLineData、loadLine_qualityData、loadLine_implData、traceRate、closeRate、implRate、quality)(struts-sys.xml:280-283, 824-827, 1007-1047)
  - Action:`DataAnalysisAction`(DataAnalysis)(struts-sys.xml:409-411)
  - Action:`UploadAction`(upload、download、uploadImage、deleteFile、queryFile)(struts-sys.xml:422-429, 511-515, 992-996, 1342-1353)
  - 包:`com.dp.plat.plus.certificate.{action,service,dao}`
  - 新架构:`core/controller/admin/DictionaryController.java`、`SystemVariableController.java`、`NotifyTemplateController.java`、`SyncLogController.java`、`SysLogController.java`、`SubModalController.java`
  - 新架构:`core/support/mail/`、`core/controller/DataExportController.java`、`core/controller/cluster/ClusterController.java`
  - DAO:`BasicDataDao`、`OpLogDao`、`SendMailDao`、`ReportDao`(PMS-struts/src/com/dp/plat/dao)
- **涉及表(待 Task 6 细化)**:basic_data、op_log、certificate、certificate_seal_info、report_*、dictionary、mail_info、notify_template、system_variable、sync_log、sys_log、file_info、data_operation、data_export、sub_modal
- **上下游**:
  - 上游:001-user-auth
  - 下游:无(被各域引用的基础设施)
- **分支**:`009-system-base`

---

## 域统计

| 编号 | 域名(中) | 域名(英) | 分支 | 聚合根数 | 上游依赖 |
|---|---|---|---|---|---|
| 001 | 用户与权限 | user-auth | 001-user-auth | 5 | 无 |
| 002 | 售前与产品 | presales-product | 002-presales-product | 5 | 001, 004 |
| 003 | 项目交付 | project-delivery | 003-project-delivery | 5 | 001, 002, 004, 008 |
| 004 | 工作流与工作空间 | workflow-workspace | 004-workflow-workspace | 5 | 001 |
| 005 | 质保回访与PM闭环 | warranty-callback | 005-warranty-callback | 5 | 001, 003, 004 |
| 006 | 维保与监管 | maintenance-supervision | 006-maintenance-supervision | 3 | 001, 003 |
| 007 | 分包 | subcontract | 007-subcontract | 1 | 001, 003, 004 |
| 008 | 外部集成 | external-integration | 008-external-integration | 1 | 001 |
| 009 | 系统基础与报表 | system-base | 009-system-base | 8 | 001 |

**总计**:9 域,38 聚合根,依赖无环。

---

## 备注

- 本域切分基于代码结构反推,表清单为初步推断,精确字段与分级在 Task 6(各域 spec 反推)中细化。
- BPMN 流程定义文件物理位于 `PMS-struts/bpmn/`,但其业务归属在使用它的域中描述(本域 004 仅管理引擎部署)。
- 003-project-delivery 域较大(含项目/成员/合同/文件/周报/通知子域),若 Task 6 发现过大,可在该域内按子域分节但不拆分为独立 spec。
