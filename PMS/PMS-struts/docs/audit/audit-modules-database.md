# PMS文档事实性审查报告

> 审查时间：2026-05-19
> 审查范围：10个模块文档、11个数据库文档、2个关联矩阵文档
> 审查方法：与struts.xml配置、Action源码、Service源码、iBatis映射文件交叉验证

---

## 审查报告

### 模块文档错误

| 序号 | 文档 | 错误类型 | 文档描述 | 实际情况 | 修正方案 |
|------|------|----------|----------|----------|----------|
| 1 | presales.md | Action类名错误 | `PresaleAction`（包路径`com.dp.plat.action`） | 实际Action类名为`PresalesAction`（带s），源码文件为`PresalesAction.java` | 将`PresaleAction`改为`PresalesAction` |
| 2 | presales.md | Service类名错误 | `PresaleServiceImpl`（事务代理Bean `presaleServiceAgent`） | 实际Service类名为`PresalesServiceImpl`，代理Bean名为`presalesServiceAgent`（带s） | 将`PresaleServiceImpl`改为`PresalesServiceImpl`，代理Bean改为`presalesServiceAgent` |
| 3 | presales.md | 数据库表名错误 | `tb_presale_info`、`tb_presale_product`、`tb_presale_member` | 实际数据库表名为`pm_presales_project_header`、`pm_presales_project_product_line`，不存在`tb_presale_*`系列表。售前项目无独立成员表，成员信息存储在`pm_project_member`中（projectType=20） | 将表名改为`pm_presales_project_header`、`pm_presales_project_product_line`，移除`tb_presale_member`，补充`pm_presales_project_callback`、`pm_presales_project_duration`等实际表 |
| 4 | presales.md | 状态机描述不准确 | 状态为英文枚举：DRAFT→PENDING→APPROVED→EXECUTING→CLOSED | 实际状态为数字编码：applyState=-1(草稿)/1(审批中)/2(审批通过)，projectState=30/31/32/33等数字编码 | 将英文枚举改为数字编码，与数据库字段一致 |
| 5 | presales.md | 接口URL前缀错误 | 文档中接口URL格式为`/module/Presale!xxx.action` | struts.xml中配置为`presales_*`通配符格式，即`/module/presales_list.action`等，非`Presale!xxx`格式 | 将URL改为通配符格式`/module/presales_{method}.action` |
| 6 | callback.md | Action类名错误 | `CallbackAction`（包路径`com.dp.plat.action`） | 实际Action类名为`CallBackAction`（B大写），源码文件为`CallBackAction.java` | 将`CallbackAction`改为`CallBackAction` |
| 7 | callback.md | Service类名错误 | `CallbackServiceImpl`（事务代理Bean `callbackServiceAgent`） | 实际Service类名为`CallBackServiceImpl`（B大写），代理Bean名为`callBackServiceAgent` | 将`CallbackServiceImpl`改为`CallBackServiceImpl`，代理Bean改为`callBackServiceAgent` |
| 8 | callback.md | 数据库表名虚构 | `tb_callback_plan`、`tb_callback_record`、`tb_callback_questionnaire`、`tb_pm_callback` | 实际数据库中不存在这些表。回访相关表为`pm_cl_callback`、`pm_cl_callback_quesnaire`，闭环回访由`pm_cl_evaluation_header`管理 | 将表名改为`pm_cl_callback`、`pm_cl_callback_quesnaire`、`pm_cl_evaluation_header`、`pm_cl_quesnaire_result_header`、`pm_cl_quesnaire_result_line` |
| 9 | callback.md | 状态机描述虚构 | 英文枚举PLANNED→ASSIGNED→EXECUTING→COMPLETED | 实际回访状态为applyState数字编码：-1(草稿)/1(审批中)/2(审批通过)，无PLANNED/ASSIGNED等英文状态 | 改为数字编码状态描述 |
| 10 | callback.md | 接口URL格式错误 | `/module/Callback.action`等 | struts.xml中配置为`callback_*`通配符格式（在/module/sub命名空间下），即`/module/sub/callback_input.action`等 | 将URL改为`/module/sub/callback_{method}.action` |
| 11 | workflow.md | Action类名错误 | `WorkflowAction`（包路径`com.dp.plat.action`） | 实际Action类名为`WorkFlowAction`（F大写），源码文件为`WorkFlowAction.java` | 将`WorkflowAction`改为`WorkFlowAction` |
| 12 | workflow.md | Service类名不准确 | `WorkflowServiceImpl`（事务代理Bean `workflowServiceAgent`） | 实际Service类名为`WorkFlowServiceImpl`（F大写），代理Bean名为`workFlowServiceAgent` | 将`WorkflowServiceImpl`改为`WorkFlowServiceImpl`，代理Bean改为`workFlowServiceAgent` |
| 13 | workflow.md | 接口URL映射错误 | `/module/WorkSpace.action`、`/module/WorkSpace!task.action`等 | struts.xml中工作台配置为`/module/Workspace`（W大写但space小写），工作流在`/work`命名空间下，如`/work/WorkFlowAction.action` | 将工作台URL改为`/module/Workspace.action`，工作流URL改为`/work/WorkFlowAction.action`等 |
| 14 | workflow.md | 虚构Service方法 | `UnifyTaskServiceImpl`及其方法`addTask()`、`completeTask()`、`syncOATask()` | 源码中不存在`UnifyTaskServiceImpl`类，统一待办任务由`WorkSpaceServiceImpl`管理，任务表为`dp_act_unify_task` | 移除`UnifyTaskServiceImpl`，将相关功能归入`WorkSpaceServiceImpl`，表名改为`dp_act_unify_task` |
| 15 | workflow.md | 虚构OA待办同步流程 | 描述了OA待办同步流程：`UnifyTaskServiceImpl.syncOATask()`→`addOATask()` | 源码中OA同步由定时任务`OperateLogAction.syncTask()`触发，通过`WorkSpaceService`处理，非`UnifyTaskService` | 修正OA同步流程描述，改为`OperateLogAction.syncTask()`→`WorkSpaceService` |
| 16 | maintenance.md | 数据库表名虚构 | `tb_maintenance_info`、`tb_maintenance_daily`、`tb_maintenance_member`、`tb_maintenance_renewal` | 实际数据库中维护相关表为`pm_project_maintenance`、`pm_project_maintenance_service_delivery`，不存在`tb_maintenance_*`系列表。维护模块无独立的日报表、成员表、续签表 | 将表名改为`pm_project_maintenance`、`pm_project_maintenance_service_delivery`，移除不存在的表 |
| 17 | maintenance.md | 状态机描述虚构 | 英文枚举ACTIVE→TERMINATED/RENEWED | 实际维护记录无独立状态机，维护任务通过`pm_project_maintenance`表的字段管理，无ACTIVE/TERMINATED等英文状态 | 移除虚构的状态机描述，改为实际字段描述 |
| 18 | maintenance.md | 虚构Service方法 | `MaintenanceServiceImpl.createByProject()`、`renewal()`、`terminate()`、`addDailyReport()`、`sendQuarterlyReminder()` | 实际`MaintenanceAction`中维护记录通过`projectService.insertOrUpdateProjectMaintenance()`管理，无独立的`MaintenanceServiceImpl`中的这些方法 | 修正Service方法描述，改为实际调用的`projectService`方法 |
| 19 | maintenance.md | 接口URL格式错误 | `/module/Maintenance.action`、`/module/Maintenance!view.action`等 | struts.xml中配置为`maintenance_*`通配符格式，即`/module/maintenance_list.action`等 | 将URL改为通配符格式`/module/maintenance_{method}.action` |
| 20 | auxiliary-modules.md | 合格证接口URL错误 | `/certificate!execute.action`、`/certificate!upload.action`等 | struts.xml中合格证配置为独立action：`/module/certificate`（class=Certificate, method=certificate），上传为`/module/uploadSealInfo`，查询为`/module/sub/queryCertificate` | 修正URL映射为实际struts配置 |
| 21 | auxiliary-modules.md | 督查接口URL格式错误 | `/supervision!execute.action`等 | struts.xml中配置为`supervision_*`通配符格式，即`/module/supervision_list.action`等 | 将URL改为通配符格式 |
| 22 | auxiliary-modules.md | 数据库表名虚构 | `tb_certificate_info`、`tb_supervision`、`tb_supervision_questionnaire` | 实际数据库中合格证表为`mes_oqc_info`、`mes_seal_info`（MES系统表），督查表为`pm_project_supervision`，无`tb_certificate_info`和`tb_supervision_questionnaire` | 将表名改为`mes_oqc_info`、`mes_seal_info`、`pm_project_supervision` |
| 23 | auxiliary-modules.md | 虚构Service类 | `CertificateServiceImpl`、`SupervisionServiceImpl` | 实际合格证由`CertificateAction`直接调用`basicDataService`，督查由`SupervisionAction`调用相关Service，无独立的`CertificateServiceImpl`和`SupervisionServiceImpl` | 移除虚构的Service类，改为实际调用的Service |
| 24 | auxiliary-modules.md | 文件上传接口URL错误 | `/module/Upload!upload.action`、`/module/Upload!downloadFile.action`等 | struts.xml中文件上传在`/module/sub`命名空间下：`/module/sub/upload`，下载为`/module/download`，Ajax上传为`/ajax/upload` | 修正URL为实际struts配置路径 |
| 25 | auxiliary-modules.md | 工作台接口URL大小写错误 | `/module/WorkSpace.action`、`/module/WorkSpace!task.action`等 | struts.xml中配置为`Workspace`（小写s），即`/module/Workspace.action` | 将`WorkSpace`改为`Workspace` |
| 26 | project-management.md | 项目状态描述不完整 | 文档描述项目状态为简单的数字编码 | 实际项目状态包含多种复合状态：30(创建项目)、31(指定服务经理)、32(指定项目经理)、34(填写渠道)、36(回退申请)、38(回退申请)、40/42(其他中间状态)、100(闭环)、20(不予跟踪) | 补充完整的项目状态列表和转换规则 |
| 27 | prob.md | 技术公告状态描述不完整 | 状态描述缺少部分状态 | 实际prob_main.status包含：0(草稿)、1(新建)、4(审批通过)、5(处理中)、6(驳回)、8(待确认)、10(闭环)，文档中缺少0和5 | 补充缺失的状态码0(草稿)和5(处理中) |
| 28 | report-analysis.md | 报表接口URL格式错误 | 文档中描述的URL格式与实际不一致 | struts.xml中报表配置为`report_*`通配符格式，在`/module`和`/module/sub`两个命名空间下都有配置 | 修正URL为实际struts配置路径 |
| 29 | system-management.md | PasswordGetinfo类名描述 | 文档中标记为"已废弃" | 实际struts.xml中仍配置了`Password`和`PasswordEditLogin`两个action（class=PasswordGetinfo），仍在使用中 | 移除"已废弃"标记，或说明仍在使用但功能被LoginAction覆盖 |
| 30 | system-management.md | ClusterAction未在struts.xml配置 | 文档中列出`ClusterAction` | struts-sys.xml中未找到ClusterAction的配置，该Action可能通过其他方式注册或已移除 | 确认ClusterAction是否仍在使用，如未使用则从文档中移除 |

### 数据库文档错误

| 序号 | 文档 | 错误类型 | 文档描述 | 实际情况 | 修正方案 |
|------|------|----------|----------|----------|----------|
| 1 | fnd-tables.md | fnd_user_info字段描述不准确 | `areapower`字段描述为"区域权限（部门编号列表），存储于fnd_user_power" | `areapower`实际存储在`fnd_user_info`表本身（冗余字段），同时也存储在`fnd_user_power`表中，两处可能不一致 | 修正描述，说明areapower在两处存储，可能存在不一致 |
| 2 | fnd-tables.md | fnd_user_info字段缺失 | 未列出`customInfo`字段 | 实际数据库中`fnd_user_info`表有`customInfo` JSON字段（在complete-data-dictionary.md中有记录） | 补充`customInfo`字段 |
| 3 | project-tables.md | pm_project_header字段名错误 | `columno12_readonly`字段 | 实际数据库字段名为`columno12_readonly`（无下划线分隔），但文档中写为`columno12_readonly`，与实际一致但命名风格与其他column字段不一致 | 确认实际字段名，保持与数据库一致 |
| 4 | project-tables.md | pm_project_state主键描述 | 描述`projectId`为PK, FK | 实际`pm_project_state`表的`projectId`是主键也是外键，但该表可能不存在独立主键ID字段，与pm_project_header是1:1关系 | 确认实际表结构，如无独立id字段则描述正确 |
| 5 | project-tables.md | 缺少pm_project_deliver表 | 未在project-tables.md中详细描述 | 实际数据库中存在`pm_project_deliver`表（项目交付件表），在crud-matrix.md中有引用 | 补充pm_project_deliver表的详细字段说明 |
| 6 | project-tables.md | 缺少pm_project_notification相关表 | 未描述项目通知相关表 | 实际存在`pm_project_notification`和`pm_project_notification_state`表 | 补充通知相关表的字段说明 |
| 7 | presales-tables.md | 售前项目状态描述不准确 | 状态流转：30→31→32→33，特殊状态：闭环、拒绝 | 实际售前项目状态更为复杂，applyState和projectState是两个独立维度，且存在多种中间状态 | 修正状态描述，区分applyState和projectState两个维度 |
| 8 | presales-tables.md | 缺少pm_presales_project_duration表 | 未描述售前项目耗时表 | 实际数据库中存在`pm_presales_project_duration`表，在crud-matrix.md中有引用 | 补充pm_presales_project_duration表 |
| 9 | presales-tables.md | 缺少pm_presales_project_rma_info表 | 未描述售前RMA信息表 | 实际数据库中存在`pm_presales_project_rma_info`表，在crud-matrix.md中有引用 | 补充pm_presales_project_rma_info表 |
| 10 | callback-tables.md | pm_cl_evaluation_header字段processStatus缺失 | 未描述processStatus字段 | 实际`pm_cl_evaluation_header`表有`processStatus`字段（闭环流程状态：10/15/20/25/30/50），这是闭环流程的核心状态字段 | 补充processStatus字段及其枚举值说明 |
| 11 | callback-tables.md | 缺少pm_cl_quesnaire_template相关表 | 未描述问卷模板表 | 实际存在`pm_cl_quesnaire_template_header`、`pm_cl_quesnaire_template_line`、`pm_cl_quesnaire_template_options`三张模板表 | 补充问卷模板相关表 |
| 12 | subcontract-tables.md | 缺少pm_subcontract_project_payment_sse表 | 未描述转包付款SSE关联表 | 实际数据库中存在`pm_subcontract_project_payment_sse`表（在complete-data-dictionary.md中有记录） | 补充pm_subcontract_project_payment_sse表 |
| 13 | subcontract-tables.md | 缺少pm_subcontract_deliver_files表 | 未描述转包交付文件表 | 实际数据库中存在`pm_subcontract_deliver_files`表，在crud-matrix.md中有引用 | 补充pm_subcontract_deliver_files表 |
| 14 | subcontract-tables.md | 缺少pm_facilitator表 | 未描述服务商表 | 实际数据库中存在`pm_facilitator`表，在crud-matrix.md中有引用 | 补充pm_facilitator表 |
| 15 | prob-tables.md | 缺少prob_softwares表 | 未描述影响软件版本表 | 实际数据库中存在`prob_softwares`表，在crud-matrix.md中有引用 | 补充prob_softwares表 |
| 16 | prob-tables.md | 缺少prob_restore_weekly表 | 未描述修复周报表 | 实际数据库中存在`prob_restore_weekly`表，在crud-matrix.md中有引用 | 补充prob_restore_weekly表 |
| 17 | prob-tables.md | 缺少prob_read_log表 | 未描述阅读日志表 | 实际数据库中存在`prob_read_log`表，在crud-matrix.md中有引用 | 补充prob_read_log表 |
| 18 | other-tables.md | 缺少pm_project_warranty_callback表 | 未描述维保回访表 | 实际数据库中存在`pm_project_warranty_callback`表，在crud-matrix.md中有引用 | 补充pm_project_warranty_callback表 |
| 19 | other-tables.md | 缺少pm_project_instruction表 | 未描述项目批示表 | ER关系图中引用了`pm_project_instruction`，但other-tables.md中未描述 | 补充pm_project_instruction表 |
| 20 | er-diagram.md | pm_project_weekly字段名错误 | 字段列表中包含`weeklyName`和`weeklyDate` | 实际`pm_project_weekly`表中无`weeklyName`和`weeklyDate`字段，实际字段为`weeklyId`、`projectId`、`weeklyStartTime`、`weeklyEndTime`、`weeklyState`等 | 修正pm_project_weekly的字段列表 |
| 21 | index-analysis.md | fnd_user_info索引描述不准确 | 列出`idx_roleIds`前缀索引 | 实际数据库中`roleIds`字段为VARCHAR(500)，存储逗号分隔的角色ID，前缀索引效果有限，需确认实际是否存在该索引 | 确认实际索引定义，如不存在则移除 |
| 22 | complete-data-dictionary.md | 业务表总数可能不准确 | 记录业务表总数为215 | 实际表数量需通过`SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA='dppms_d365'`确认，排除act_*等前缀后可能不同 | 通过实际数据库查询确认表总数 |
| 23 | sync-tables.md | 缺少pm_presales_lend_header_from_sms和pm_presales_lend_line_from_sms表 | 未描述售前借货数据同步表 | 实际数据库中存在这两张表，在data-flow.md中有引用 | 补充售前借货数据同步表 |

### 关联矩阵文档错误

| 序号 | 文档 | 错误类型 | 文档描述 | 实际情况 | 修正方案 |
|------|------|----------|----------|----------|----------|
| 1 | crud-matrix.md | 售前模块表名不一致 | 售前模块列出`pm_presales_project_header`等正确表名 | 与presales.md模块文档中的`tb_presale_info`等虚构表名不一致，crud-matrix.md使用的是正确表名 | 以crud-matrix.md为准，修正presales.md中的表名 |
| 2 | crud-matrix.md | 回访模块表名不一致 | 回访模块列出`pm_cl_callback`等正确表名 | 与callback.md模块文档中的`tb_callback_plan`等虚构表名不一致，crud-matrix.md使用的是正确表名 | 以crud-matrix.md为准，修正callback.md中的表名 |
| 3 | crud-matrix.md | 维护模块表名不一致 | 维护模块列出`pm_project_maintenance`正确表名 | 与maintenance.md模块文档中的`tb_maintenance_info`等虚构表名不一致 | 以crud-matrix.md为准，修正maintenance.md中的表名 |
| 4 | crud-matrix.md | 项目成员角色编码描述错误 | 数据转换规则中描述"memberRole=10(SM)、memberRole=20(PM)" | 实际编码为：10(销售)、20(服务经理SM)、30(项目经理PM)、40(团队成员)，与fnd_basic_data(dataTypeCode=03)一致 | 将memberRole编码修正为10=销售、20=服务经理、30=项目经理 |
| 5 | crud-matrix.md | 项目状态转换描述不完整 | "STATE_30→STATE_31(指派SM)→STATE_32(指派PM)→STATE_100(闭环)" | 实际状态转换更复杂：还包括STATE_34(填写渠道)、STATE_36/38(回退)、STATE_40/42(其他中间状态)、STATE_20(不予跟踪)，且任意状态可回退到STATE_20 | 补充完整的状态转换路径 |
| 6 | crud-matrix.md | 闭环流程状态映射描述不准确 | "processStatus×10=closeProcessState值" | 实际processStatus和closeProcessState的映射关系并非简单乘10，processStatus=10对应closeProcessState=10，processStatus=20对应closeProcessState=20等，但processStatus=15(驳回)对应closeProcessState=15 | 修正映射关系描述，列出完整的processStatus到closeProcessState的映射表 |
| 7 | data-flow.md | 售前流程状态描述不准确 | "status=10"→"status=20"→"status递增" | 实际售前项目状态为projectState=30/31/32/33，applyState=-1/1/2，非简单的10/20递增 | 修正售前流程中的状态编码 |
| 8 | data-flow.md | 缺少项目回退流程 | 未描述项目回退数据流 | 实际项目回退是重要的数据流：服务经理/项目经理申请回退→工程管理部审批→状态回退，涉及邮件通知和状态变更 | 补充项目回退数据流 |
| 9 | data-flow.md | 缺少技术公告数据流 | 未描述技术公告的数据流 | 实际技术公告有完整的数据流：创建→审批→发布→跟踪任务分配→修复→闭环 | 补充技术公告数据流 |
| 10 | data-flow.md | 缺少转包数据流 | 未描述转包项目的数据流 | 实际转包有完整的数据流：创建→审批→D365采购订单→收货确认→付款→回访→闭环 | 补充转包数据流 |

---

## 审查总结

### 严重问题（需立即修正）

1. **模块文档中大量虚构的数据库表名**：presales.md、callback.md、maintenance.md、auxiliary-modules.md中使用了`tb_presale_*`、`tb_callback_*`、`tb_maintenance_*`、`tb_certificate_info`、`tb_supervision*`等虚构表名，实际数据库中不存在这些表。正确表名以`pm_`、`fnd_`、`mes_`为前缀。

2. **Action/Service类名拼写错误**：多个文档中Action和Service类名与源码不一致，如`PresaleAction`应为`PresalesAction`、`CallbackAction`应为`CallBackAction`、`WorkflowAction`应为`WorkFlowAction`等。

3. **接口URL映射与struts.xml配置不一致**：多个文档使用了`ActionName!method.action`的动态方法调用格式，但实际struts.xml配置使用的是通配符格式`actionName_*`。

4. **虚构的业务流程和状态机**：callback.md中PLANNED→ASSIGNED→EXECUTING→COMPLETED的状态机、maintenance.md中ACTIVE→TERMINATED/RENEWED的状态机均为虚构，实际代码中不存在这些英文枚举状态。

5. **虚构的Service类和方法**：workflow.md中的`UnifyTaskServiceImpl`、auxiliary-modules.md中的`CertificateServiceImpl`和`SupervisionServiceImpl`在源码中不存在。

### 中等问题（建议修正）

1. **数据库文档表缺失**：多个数据库文档缺少实际存在的表描述，如prob-tables.md缺少prob_softwares、prob_read_log等表。

2. **ER关系图字段名错误**：er-diagram.md中pm_project_weekly的字段列表与实际不符。

3. **关联矩阵中角色编码错误**：crud-matrix.md中memberRole编码描述与实际不一致。

4. **数据流文档不完整**：data-flow.md缺少项目回退、技术公告、转包等重要数据流。

### 轻微问题（可选修正）

1. **字段描述不够精确**：部分字段的业务含义描述可以更准确。
2. **索引分析需实际验证**：index-analysis.md中的部分索引需通过实际数据库查询确认。
3. **complete-data-dictionary.md表总数需确认**：需通过实际查询确认。
