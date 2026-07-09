# 003-project-delivery 域规格草稿(Spec Reverse-Draft)

> 来源:逆向反推自 PMS-struts project 代码,日期 2026-07-09
> 域职责:项目全生命周期——创建/成员/合同/发货/周报/通知/验货/版本
> 证据基准:struts-sys.xml 第 318-440 行(ProjectAction)、1051-1145 行(AJAX)、1226-1337 行(子域 Action);sql-map-project-config.xml、sql-map-project-config2.xml、sql-map-project-common-config.xml;ProjectAction.java、ProjectContractAction.java、ProjectMemberAction.java、ProjectFileAction.java、ProjectWeeklyAction.java、ProjectNotificationAction.java;ProjectDao.java、ProjectUtils.java、ProjectInspectionMailer.java;Project.java、ProjectMember.java、ProjectWeekly.java、ProjectDeliver.java

---

## 第1章 用户故事

> 端点证据:struts-sys.xml:318-440(ProjectAction)、1051-1145(AJAX/项目)、1226-1337(projectMember/projectContract/projectFile/projectWeekly/projectNotification 包)

### 1.1 项目基本信息

- **US-PROJ-01 项目列表** — 作为工程管理部/管理员/财务/项目管理员,我希望按状态、办事处、人员、时间筛选项目列表;作为服务经理/项目经理/普通用户/项目查阅,我希望按权限查看已创建项目。证据:`ProjectManage.action` → `ProjectAction.execute()`(ProjectAction.java:347);SQL:`query_project_list`(sql-map-project-config.xml)。
- **US-PROJ-02 创建项目** — 作为工程管理部/管理员,我希望基于合同号创建项目,系统校验合同唯一性、生成项目编码、写入主表/合同/分组/成员,并发送立项通知邮件。证据:`ProjectCreate.action` → `ProjectAction.insertProject()`(ProjectAction.java:373);SQL:`insert-project`(sql-map-project-config.xml:1075)。
- **US-PROJ-03 创建串货项目** — 作为工程管理人员,我希望手动创建串货(CH)项目,流程与普通创建一致但跳过合同唯一性来源校验。证据:`createCHProject.action` → `ProjectAction.createCHProject()`(ProjectAction.java:434)。
- **US-PROJ-04 修改项目** — 作为有权限的项目相关方(工程管理部/服务经理/项目经理),我希望根据当前项目状态与角色,分权更新项目信息(工程管理部全权、服务经理指定项目经理、项目经理更新渠道与实施方式)。证据:`ProjectModify.action` → `ProjectAction.updateProject()`(ProjectAction.java:639)。
- **US-PROJ-05 编辑工程计划** — 作为项目相关方,我希望编辑项目的工程计划事件节点(初验、终验等到货/验收时间)。证据:`ProjectPlanEdit.action` → `ProjectAction.editProjectPlan()`(ProjectAction.java:1376);SQL:`insert-projectplan`/`update-projectplan-byprojectid`(sql-map-project-config.xml:2781/2776)。
- **US-PROJ-06 回退上一步** — 作为有权限用户,我希望将项目状态回退到上一步并记录回退说明。证据:`backToLastStep.action` → `ProjectAction.backToLastStep()`(ProjectAction.java:1463);SQL:`backToLastStep`/`update-projectisback-byprojectid`(sql-map-project-config.xml:3204)。
- **US-PROJ-07 项目清理/批量删除** — 作为管理员/工程管理部,我希望上传 Excel 批量失效或物理删除项目。证据:`clearProject.action` → `ProjectAction.clearProject()`(ProjectAction.java:2995)。
- **US-PROJ-08 批量导入/批量操作** — 作为系统管理员,我希望通过 Excel 批量创建项目、直接闭环、指定服务经理/项目经理。证据:`importProject.action` → `ProjectAction.importProject()`(ProjectAction.java:2967)。
- **US-PROJ-09 项目转包/转移设备** — 作为项目相关方,我希望将一个项目的设备序列号转移到另一个项目,并维护转移合同关系。证据:`transferShipment.action` → `ProjectAction.transferShipment()`(ProjectAction.java:470);SQL:`insert_project_transfer_shipment`/`insert_transfer_contract`(sql-map-project-config.xml:2996/3015)。
- **US-PROJ-10 查询上期周报继承** — 作为周报创建人,我希望新建周报时自动继承上期周报的任务偏差、备注与各项工作内容。证据:`CreateWeekly.action` → `ProjectWeeklyAction.createWeekly()`(ProjectWeeklyAction.java:49);SQL:`query_project_weekly_one`/`query_weekly_contents`(sql-map-project-config.xml:1545/1556)。

### 1.2 项目成员

- **US-MEMBER-01 添加成员** — 作为项目相关方,我希望为项目添加成员(角色、姓名、电话、邮箱、生效时间),并触发动态通知。证据:`createMember.action` → `ProjectMemberAction.createMember()`(ProjectMemberAction.java:43);SQL:`insert_project_member`(sql-map-project-config.xml:2757)。
- **US-MEMBER-02 更新成员(失效)** — 作为项目相关方,我希望结束某个成员的任期(设置 effectiveTo),并触发固定通知。证据:`updateMember.action` → `ProjectMemberAction.updateMember()`(ProjectMemberAction.java:74);SQL:`update_project_member`(sql-map-project-config.xml:2767)。
- **US-MEMBER-03 批量变更成员** — 作为工程管理部/管理员,我希望按部门批量变更项目服务经理/项目经理,并联动终止项目经理手中的闭环与回访流程。证据:`BatchChangeProjectMember.action` → `ProjectAction.batchChangeMember()`(ProjectAction.java:2195);工具:`ProjectUtils.updateServiceAndProgramMember()`(ProjectUtils.java:26)/`terminateProgramManagerActivities()`(ProjectUtils.java:126)。
- **US-MEMBER-04 保存安装地址** — 作为项目相关方,我希望为选中的设备序列号批量保存安装地址,并触发固定通知。证据:`SaveInstallAdress.action` → `ProjectMemberAction.saveInstallAdress()`(ProjectMemberAction.java:96);SQL:`insert_project_shipment`/`update_project_shipment`(sql-map-project-config.xml:2820/2815)。
- **US-MEMBER-05 更新项目实施状态** — 作为项目相关方,我希望更新项目实施状态(工程实施状态)。证据:`updateProjectExecutionState.action` → `ProjectMemberAction.updateProjectExecutionState()`(ProjectMemberAction.java:118)。

### 1.3 合同管理

- **US-CONTRACT-01 合同合并预检** — 作为项目相关方,我希望输入合并合同号后查询可合并的合同信息(若项目组只有1条记录则提示404)。证据:`checkMergeContract.action` → `ProjectContractAction.checkMergeContract()`(ProjectContractAction.java:51);SQL:`query-projectcontractcount-bycontractno`/`query-contract-list`(sql-map-project-config.xml:3522)。
- **US-CONTRACT-02 合同合并** — 作为项目相关方,我希望把选中的合同合并到当前项目,系统插入合同关系、产品清单、项目计划。证据:`MergeContract.action` → `ProjectContractAction.mergeContract()`(ProjectContractAction.java:68);SQL:`insert_merge_contract`/`insert_merge_product`/`insert_merge_task`(sql-map-project-config.xml:3360/3366/3490)。
- **US-CONTRACT-03 项目拆分** — 作为项目相关方,我希望基于当前项目拆分出新项目,复制项目主表/成员/产品清单/分组关系。证据:`BranchContract.action` → `ProjectContractAction.branchContract()`(ProjectContractAction.java:82);SQL:`insert_branch_project_info`/`insert_branch_project_member`/`batch_insert_product`/`insert_project_group_relationship`(sql-map-project-config.xml:3435/3448/3460/3430)。
- **US-CONTRACT-04 查询订单数据** — 作为项目相关方,我希望查询项目关联合同的订单数据汇总/明细(含 RMA 退货)。证据:`checkOrderData.action` → `ProjectContractAction.checkOrderData()`(ProjectContractAction.java:92);SQL:`query-orderdatalist-byprojectid`/`queryOrderDataDetailListByProjectId`(sql-map-project-config.xml:2128/2116)。
- **US-CONTRACT-05 查询实际发货清单** — 作为项目相关方,我希望查询项目的实际发货清单(序列号维度)。证据:`checkRealOrderData.action` → `ProjectContractAction.checkRealOrderData()`(ProjectContractAction.java:101);SQL:`queryRealOrderDataListByProjectId`(sql-map-project-config.xml)。
- **US-CONTRACT-06 查询租赁配置/配置关系** — 作为项目相关方,我希望按项目编码查询租赁配置清单与产品配置关系。证据:`projectLeaseLine`/`projectProductConfigLevelInfo`(ProjectAction.java:899/914);SQL:`queryProjectLeaseLineByProjectCode`(sql-map-project-config.xml:4158)。

### 1.4 发货与设备

- **US-SHIP-01 查询发货序列号** — 作为项目相关方,我希望按合同号查询发货设备序列号清单(总代借货按 profitCenter 过滤,排除已转出设备)。证据:`checkShipmentInfo.action` → `ProjectAction.checkShipmentInfo()`(ProjectAction.java:928);SQL:`query_shipment_info_by_contractno`(sql-map-project-config.xml:2194)。
- **US-SHIP-02 删除发货安装信息** — 作为项目相关方,我希望按序列号列表删除项目的发货安装信息。证据:`deleteShipmentInfo.action` → `ProjectAction.deleteShipmentInfo()`(ProjectAction.java:951);SQL:`deleteShipmentInstallInfoByList`(sql-map-project-config.xml:2833)。
- **US-SHIP-03 设备序列号转移** — 作为项目相关方,我希望把设备从一个项目转移到另一个项目,并按 salesType=14 处理总代借货特殊逻辑。证据:`transferShipment.action` → `ProjectAction.transferShipment()`(ProjectAction.java:470);SQL:`insert_project_transfer_shipment`/`update_project_transfer_shipment`(sql-map-project-config.xml:2996/2986)。

### 1.5 项目周报

- **US-WEEKLY-01 创建周报** — 作为周报创建人,我希望创建周报时按周计算起止时间,并继承上期内容。证据:`CreateWeekly.action` → `ProjectWeeklyAction.createWeekly()`(ProjectWeeklyAction.java:49);时间算法:`getWeeklyDateTime()`(ProjectWeeklyAction.java:80)。
- **US-WEEKLY-02 保存草稿** — 作为周报创建人,我希望将周报保存为草稿(weeklyState=0),不发送邮件。证据:`SaveWeekly.action` → `ProjectWeeklyAction.saveWeekly()`(ProjectWeeklyAction.java:102);SQL:`insert-projectweekly`/`update_project_weekly`(sql-map-project-config.xml:1503/1562)。
- **US-WEEKLY-03 提交周报** — 作为周报创建人,我希望提交周报(weeklyState=1),系统生成 Excel 附件并向项目团队成员、抄送人、服务经理发送邮件,并写入固定通知。证据:`SubmitWeekly.action` → `ProjectWeeklyAction.submitWeekly()`(ProjectWeeklyAction.java:125)。
- **US-WEEKLY-04 编辑周报** — 作为周报创建人,我希望查看周报并按 optionType 分组查询各项工作/风险/帮助/进展/计划/附件/抄送内容。证据:`EditWeekly.action` → `ProjectWeeklyAction.updateWeekly()`(ProjectWeeklyAction.java:192);SQL:`query_weekly_contents`(sql-map-project-config.xml:1556)。
- **US-WEEKLY-05 周报回复** — 作为周报接收人,我希望对已提交周报进行回复反馈。证据:`Feedback.action` → `ProjectWeeklyAction.feedback()`(ProjectWeeklyAction.java:213);SQL:`insert_weekly_feedback`(sql-map-project-config.xml:1580)。

### 1.6 通知批示

- **US-NOTIFY-01 查询项目通知** — 作为项目相关方,我希望查询项目的系统通知列表。证据:`queryProjectNotification.action` → `ProjectNotificationAction.queryProjectNotification()`(ProjectNotificationAction.java:43);SQL:`query-notify-list`(sql-map-project-config.xml)。
- **US-NOTIFY-02 保存批示** — 作为有权限用户,我希望保存项目批示信息(可针对已有批示反馈)。证据:`instruction.action` → `ProjectNotificationAction.instruction()`(ProjectNotificationAction.java:121);SQL:`insert-instruction`(sql-map-project-config.xml:1248)。
- **US-NOTIFY-03 查询批示** — 作为项目相关方,我希望查询项目批示列表并拼接为文本。证据:`ProjectNotificationAction.getInstructionsInfo()`(ProjectNotificationAction.java:135);SQL:`query-instruction-list`(sql-map-project-config.xml:1238)。
- **US-NOTIFY-04 问题工单查询** — 作为项目相关方,我希望查询项目关联的 ITR 工单(先按项目编号,再按合同号回退查询)。证据:`problemTicket.action` → `ProjectNotificationAction.problemTicket()`(ProjectNotificationAction.java:58);SQL:`selectProblemTicket`(sql-map-project-config.xml:6381)。
- **US-NOTIFY-05 License 授权查询** — 作为项目相关方,我希望按合同号/项目编号查询项目 License 授权信息。证据:`licenseInfo.action` → `ProjectNotificationAction.licenseInfo()`(ProjectNotificationAction.java:91);SQL:`selectLicenseInfo`(sql-map-project-config.xml:6405)。

### 1.7 现场验货

- **US-INSPECT-01 现场验货单导出** — 作为项目相关方,我希望按项目导出现场验货单(Word/Excel),系统聚合发货设备并忽略指定 item。证据:`exportSpotCheck.action` → `ProjectAction.exportSpotCheck()`(ProjectAction.java:532);SQL:`querySpotCheckList`(sql-map-project-config.xml:4474)。
- **US-INSPECT-02 过保提醒函导出** — 作为项目相关方,我希望按项目导出设备过保提醒函。证据:`exportOverWarrantyRemind.action` → `ProjectAction.exportOverWarrantyRemind()`(ProjectAction.java:549);SQL:`queryOverWarrantyRemindList`(sql-map-project-config.xml:4541)。
- **US-INSPECT-03 验货忽略项导入** — 作为管理员/工程管理部,我希望导入现场验货单不需要序列号明细的 item 列表(覆盖式)。证据:`importSpotCheckIgnoreItem`(ProjectAction.java:567);SQL:`truncateSpotCheckIgnoreItem`/`batchInsertSpotCheckIgnoreItem`(sql-map-project-config.xml:4529/4532)。
- **US-INSPECT-04 验货状态定时提醒** — 作为系统,我希望定时(每日/每周)统计各办事处项目验货节点(到货验收、初验、终验)的超期情况,并向办事处领导、服务经理、项目经理、销售、验收小组发送汇总邮件。证据:`ProjectInspectionMailer.execute()`(ProjectInspectionMailer.java:67);SQL:`queryProjectInspection`/`queryProjectInspectionCounts`/`queryProjectInspectionOfficeCode`(sql-map-project-config.xml:4683/4929/4616)。

### 1.8 软件版本更新

- **US-SOFT-01 查询设备软件版本** — 作为项目相关方,我希望按合同号查询发货设备的软件版本(conp/cpld/boot/pcb)及受影响的技术公告。证据:`checkSoftVersion.action` → `ProjectAction.checkSoftVersion()`(ProjectAction.java:974);SQL:`query_softversion_list`(sql-map-project-config.xml)与 `sqlSubSelectProjectSoftVersionAffectedProbs`(sql-map-project-common-config.xml:161)。
- **US-SOFT-02 更新软件版本** — 作为项目相关方,我希望批量更新设备的软件版本,系统失效旧版本、插入新版本,并写入版本变更日志。证据:`updateSoftVersion.action` → `ProjectAction.updateSoftVersion()`(ProjectAction.java:1011);SQL:`update_invalid_softversion`/`insert_soft_version_list`/`insert_soft_version_log`(sql-map-project-config.xml:2410/2415/2459)。
- **US-SOFT-03 查询版本变更历史** — 作为项目相关方,我希望查看项目的软件版本变更历史与出厂版本(V0)。证据:`checkhistsoftversion.action` → `ProjectAction.checkhistsoftversion()`(ProjectAction.java:1035);SQL:`query_hist_soft_version_change`/`query_hist_soft_version_list`(sql-map-project-config.xml:2473/2610)。

### 1.9 文件管理

- **US-FILE-01 文件上传** — 作为项目相关方,我希望上传项目文件(周报附件、交付件),按白名单校验扩展名,自动重命名并落盘。证据:`UploadFile.action` → `ProjectFileAction.UploadFile()`(ProjectFileAction.java:105);`UploadDeliverableFile.action` → `ProjectFileAction.uploadDeliverableFile()`(ProjectFileAction.java:249)。
- **US-FILE-02 文件下载** — 作为项目相关方,我希望按路径下载项目附件,支持 ISO8859-1 与 UTF-8 编码回退。证据:`DownloadFile.action` → `ProjectFileAction.downloadFile()`(ProjectFileAction.java:153);`getFileStream()`(ProjectFileAction.java:186)。
- **US-FILE-03 文件删除** — 作为项目相关方,我希望按 id 删除周报附件。证据:`DeleteFile.action` → `ProjectFileAction.deleteFile()`(ProjectFileAction.java:219);SQL:`deleteFileById`(sql-map-project-config.xml)。
- **US-FILE-04 交付件删除** — 作为项目相关方,我希望按 id 软删除(失效)交付件。证据:`deleteDeliverById.action` → `ProjectFileAction.deleteDeliverById()`(ProjectFileAction.java:234);SQL:`delete-deliver-by-id`(sql-map-project-config.xml:3192)。

---

## 第2章 功能需求

> 子域组织:2.1 项目基本信息 / 2.2 项目成员 / 2.3 合同管理 / 2.4 发货与设备 / 2.5 项目周报 / 2.6 通知批示 / 2.7 现场验货 / 2.8 软件版本更新

### 2.1 项目基本信息

#### FR-PROJ-01 项目列表查询
- **触发条件**:用户进入项目管理列表页(ProjectManage.action)。证据:ProjectAction.java:347。
- **输入**:项目过滤条件(状态、办事处 column001、服务/项目经理、时间类型、起止时间、项目编号/名称/合同号、序列号);分页/排序参数。
- **处理规则**:
  1. 按角色分流:工程管理部/管理员/财务/项目管理员查询全部;服务经理/项目经理/普通用户/项目查阅按权限查询(queryProjectListByPower)。证据:ProjectAction.java:350-357。
  2. 当状态为 30(已创建)时,自动扩展为 30,31,32 三种状态查询。证据:ProjectAction.java:333-335。
  3. 财务角色且状态为空时,默认查询已闭环项目。证据:ProjectAction.java:329-331。
  4. 加载办事处、公司、项目类型、发货状态、工程计划/实施/闭环状态、项目时间点、实施方式等基础数据集合。证据:ProjectAction.java:272-306。
- **输出**:项目列表 + 市场关系映射 JSON。
- **异常**:列表查询失败时打印堆栈,返回空列表。

#### FR-PROJ-02 项目创建
- **触发条件**:工程管理部/管理员点击创建项目(ProjectCreate.action)。证据:ProjectAction.java:373。
- **输入**:合同号、项目名称、项目分类(column010)、项目类别(column011)、实施方式(column012)、最终客户(column013)、公司 compId、销售类型 salesType、客户项目名、重大项目级别、自定义信息 customInfo;服务经理、项目经理(可选)。
- **处理规则**:
  1. 若参数为空,按合同号查询 SAP 同步订单数据,生成项目编码,返回创建页。证据:ProjectAction.java:376-406。
  2. 若参数非空,先校验合同号是否已创建项目(count != 0 则报错"该合同号已创建项目")。证据:ProjectAction.java:410-414。
  3. 调用 insertProject 保存:写入 pm_project_header 主表、pm_project_contract 合同、pm_project_group_relationship 分组关系、pm_project_member 成员(服务经理/项目经理/销售)。证据:sql-map-project-config.xml:1075/1261/1266/1271。
  4. 设置重大项目级别到项目类别的映射(系统参数 `pm.project.majorProjectLevel2projectCategory`)。证据:ProjectAction.java:388-397。
  5. 发送立项通知邮件(普通类/工程类模板)。证据:ProjectAction.java:592-610。
- **输出**:项目主键 projectId(paramId 为 Base64 编码);成功/失败代码。
- **异常**:合同号已存在返回 ERROR;保存异常返回 INPUT 并设置错误代码。

#### FR-PROJ-03 串货项目创建
- **触发条件**:工程管理人员手动创建串货项目(createCHProject.action)。证据:ProjectAction.java:434。
- **输入**:同 FR-PROJ-02,但合同号来源不依赖 SAP 同步。
- **处理规则**:与 FR-PROJ-02 一致(查询合同唯一性、生成项目编码、insertProject、发邮件)。证据:ProjectAction.java:444-461。
- **输出**:同 FR-PROJ-02。
- **异常**:合同号已存在返回 ERROR。

#### FR-PROJ-04 项目修改(分权)
- **触发条件**:用户在项目维护页提交修改(ProjectModify.action)。证据:ProjectAction.java:639。
- **输入**:项目主键 paramId、各字段;当前用户角色与用户名。
- **处理规则**:
  1. paramId 解码为 projectId。证据:ProjectAction.java:646-648。
  2. 非管理员/工程管理部等角色时,Session 缓存权限判断结果(单项目 Session 期内只判一次)。证据:ProjectAction.java:649-685。
  3. 参数为空时进入查看模式:查询项目、状态、周报列表、财务验收计划、产品列表/实际清单/租赁/配置、工程计划、事件节点、交付件、批示、成员、回访流程,并计算闭环条件(必传交付件、最终客户/渠道、安装数量=发货数量、无回访流程)。证据:ProjectAction.java:687-783。
  4. 参数非空时按角色分权:
     - 工程管理部且未指定服务经理 → 失效项目(invalidProject)。证据:ProjectAction.java:791-792。
     - 工程管理部 → updateProjectByProjectId(全字段)。证据:ProjectAction.java:794。
     - 已闭环或不予跟踪项目且当前用户为服务/项目经理 → updateChannel + updateProjectImplByProjectId(渠道与实施方式)。证据:ProjectAction.java:798-806。
     - 服务经理且状态为 30/32/34 → updateProjectProgramManagerByProjectId(指定项目经理)。证据:ProjectAction.java:808-810。
     - 项目经理且状态为 30/32/34 → updateChannel + updateProjectImplByProjectId。证据:ProjectAction.java:837-842。
  5. 闭环条件:必传交付件数为 0、最终客户与渠道齐全、安装数量=发货数量、无正在回访流程 → isToCloseProject=1。证据:ProjectAction.java:732-779。
- **输出**:重定向回 ProjectModify(带 paramId);错误时设置错误代码。
- **异常**:无权限返回 ERROR("没有权限访问!");状态不满足时返回 message 提示。

#### FR-PROJ-05 工程计划编辑
- **触发条件**:用户编辑工程计划事件节点(ProjectPlanEdit.action)。证据:ProjectAction.java:1376。
- **输入**:projectId、事件节点列表(eventKeyStr/eventValueStr/eventDoingStr)、计划时间、实际完成时间。
- **处理规则**:失效旧任务记录(effectiveTo=NOW),插入新记录到 pm_project_task。证据:sql-map-project-config.xml:2776/2781。
- **输出**:重定向回项目维护页。
- **异常**:异常返回 INPUT。

#### FR-PROJ-06 项目状态回退
- **触发条件**:用户回退项目状态(backToLastStep.action)。证据:ProjectAction.java:1463。
- **输入**:projectId、目标状态、回退说明(isback/backCause)。
- **处理规则**:更新 pm_project_header 的 isback 字段为状态、column014 为回退说明。证据:sql-map-project-config.xml:3204-3210。
- **输出**:JSON 结果。
- **异常**:异常返回错误。

#### FR-PROJ-07 项目清理(批量失效/删除)
- **触发条件**:管理员/工程管理部上传 Excel(clearProject.action)。证据:ProjectAction.java:2995。
- **输入**:Excel 文件、modifyflag(1=物理删除,0=失效)。
- **处理规则**:
  1. 角色校验(管理员/工程管理部),否则返回 authError。证据:ProjectAction.java:2997-3000。
  2. 解析 Excel 为项目列表。
  3. modifyflag=1 → batchDeleteProject(物理删除);否则 batchInvalidProject(失效)。证据:ProjectAction.java:3004-3008。
- **输出**:影响行数。
- **异常**:异常返回 ERROR 并提示。

#### FR-PROJ-08 项目批量导入
- **触发条件**:管理员上传 Excel(importProject.action)。证据:ProjectAction.java:2967。
- **输入**:Excel(data.xlsx)、batchFunc(1=直接闭环,2=指定服务经理,3=指定项目经理+服务经理)。
- **处理规则**:
  1. 非管理员返回 result=2。证据:ProjectAction.java:2969-2972。
  2. 按批次函数解析对应 Sheet(闭环清单/进行中清单-指定服务经理/进行中清单-指定项目经理)。证据:ProjectAction.java:3027-3033。
  3. 跳过已存在的合同号,调用 insertBatchProject 保存。证据:ProjectAction.java:2977-2983。
- **输出**:result(1=成功,2=失败)。
- **异常**:异常返回 result=2。

#### FR-PROJ-09 设备转移
- **触发条件**:用户发起设备转移(transferShipment.action)。证据:ProjectAction.java:470。
- **输入**:当前项目、目标项目(transferProject)、selected(序列号列表)、contractNo、transferType、transferFlag(0=转销,1=退货)。
- **处理规则**:
  1. result=0:按 projectCode 查询可转移项目列表。证据:ProjectAction.java:472-479。
  2. result=1:按合同号查询可转移的设备序列号(排除已转出),salesType=14 时按 profitCenter 过滤。证据:ProjectAction.java:491-510。
  3. result=2:执行转移,salesType=14 时按借货特殊逻辑处理合同号(加 -C 后缀)。证据:ProjectAction.java:480-490;SQL:`insert_project_transfer_shipment`(sql-map-project-config.xml:2996)。
  4. 转移后插入转移合同关系到 pm_project_contract。证据:sql-map-project-config.xml:3015。
  5. 更新两个项目的最后刷新时间。证据:ProjectAction.java:487-488。
- **输出**:转移结果。
- **异常**:异常返回错误。

### 2.2 项目成员

#### FR-MEMBER-01 添加项目成员
- **触发条件**:用户在成员管理页添加成员(createMember.action)。证据:ProjectMemberAction.java:43。
- **输入**:projectId、memberRole、memberCode、memberName、phoneNum、email、memberEffectiveFrom(默认当前时间)。
- **处理规则**:
  1. 构造成员对象,projectType 默认为售后项目(PROJECT_TYPE_AFTERSALES)。证据:ProjectMemberAction.java:46-47。
  2. phoneNum 去除空白字符。证据:ProjectMemberAction.java:51。
  3. createBy 取当前用户名,createTime 取当前时间。证据:ProjectMemberAction.java:53-54。
  4. 插入 pm_project_member,返回 memberId。证据:sql-map-project-config.xml:2757。
  5. 更新项目最后刷新时间。证据:ProjectMemberAction.java:60。
  6. 触发动态通知(NOTIFICATION_CODE_113,带 memberRoleName)。证据:ProjectMemberAction.java:61。
- **输出**:memberId(JSON result)。
- **异常**:异常 result=0。

#### FR-MEMBER-02 更新成员(失效)
- **触发条件**:用户结束成员任期(updateMember.action)。证据:ProjectMemberAction.java:74。
- **输入**:memberId、memberEffectiveTo。
- **处理规则**:
  1. 更新 pm_project_member 的 effectiveTo。证据:sql-map-project-config.xml:2767。
  2. 更新项目最后刷新时间。证据:ProjectMemberAction.java:82。
  3. 触发固定通知(NOTIFICATION_CODE_116)。证据:ProjectMemberAction.java:83。
- **输出**:memberId(JSON result)。
- **异常**:异常 result=0。

#### FR-MEMBER-03 批量变更服务/项目经理
- **触发条件**:工程管理部/管理员批量变更(BatchChangeProjectMember.action)。证据:ProjectAction.java:2195。
- **输入**:部门 dpNo、oldMemberCode、newMemberName(code-name 格式)、changeType(service/program/both)。
- **处理规则**:
  1. 查询该部门下状态为 30/31/32 且指定旧成员的项目列表。证据:ProjectUtils.java:47-58。
  2. 变更服务经理:逐项目更新服务经理,更新项目状态,记录闭环流程 taskId;批量更新闭环评估头表的 nextAcceptPerson,并重指派工作流任务。证据:ProjectUtils.java:56-94。
  3. 变更项目经理:逐项目更新项目经理,终止该项目经理手中的闭环申请与回访流程(更新回访状态为驳回)。证据:ProjectUtils.java:99-118/126-155。
- **输出**:serviceCount:programCount。
- **异常**:异常返回错误。

#### FR-MEMBER-04 保存安装地址
- **触发条件**:用户为设备保存安装地址(SaveInstallAdress.action)。证据:ProjectMemberAction.java:96。
- **输入**:projectId、selected(序列号列表)、installAddress。
- **处理规则**:
  1. 查询项目获取合同号与 salesType。证据:ProjectMemberAction.java:98-99。
  2. salesType=14(总代借货)时传入 column001(profitCenter)过滤。证据:ProjectMemberAction.java:100-104。
  3. 调用 insertInstallAddress:从发货视图查询设备,批量插入 pm_project_shipment。证据:sql-map-project-config.xml:2820。
  4. 更新项目最后刷新时间,触发固定通知(NOTIFICATION_CODE_114)。证据:ProjectMemberAction.java:105-106。
- **输出**:result=303。
- **异常**:异常打印堆栈。

#### FR-MEMBER-05 更新项目实施状态
- **触发条件**:用户更新实施状态(updateProjectExecutionState.action)。证据:ProjectMemberAction.java:118。
- **输入**:projectId、executionState。
- **处理规则**:projectId 与 executionState 非空时调用 updateProjectExecutionState,更新 pm_project_state.executionState。证据:ProjectMemberAction.java:120-124;sql-map-project-config.xml:3861。
- **输出**:result=313。
- **异常**:异常 result=0。

### 2.3 合同管理

#### FR-CONTRACT-01 合同合并预检
- **触发条件**:用户输入合并合同号(checkMergeContract.action)。证据:ProjectContractAction.java:51。
- **输入**:mergeContractNo。
- **处理规则**:
  1. 按合同号查询 pm_project_contract 数量。证据:ProjectContractAction.java:55。
  2. 数量为 1 时返回 result=404(无需合并)。证据:ProjectContractAction.java:56-57。
  3. 否则查询合同列表(contractList)。证据:ProjectContractAction.java:59。
- **输出**:JSON(result 或 contractList)。
- **异常**:无。

#### FR-CONTRACT-02 合同合并
- **触发条件**:用户选择合同执行合并(MergeContract.action)。证据:ProjectContractAction.java:68。
- **输入**:selected(合同号列表)、projectId。
- **处理规则**:
  1. selected 为空时报错"请至少选择一条合同数据"。证据:ProjectContractAction.java:69-72。
  2. 调用 insertMergeContract:对每个选中合同插入 pm_project_contract 关联、插入 pm_project_product_line 产品清单(从 pm_order_line_from_sap 关联查询)、合并项目计划(pm_project_task)。证据:sql-map-project-config.xml:3360/3366/3490。
  3. 重定向到项目维护页,result=302。证据:ProjectContractAction.java:74。
- **输出**:重定向。
- **异常**:selected 为空返回 ERROR。

#### FR-CONTRACT-03 项目拆分
- **触发条件**:用户执行项目拆分(BranchContract.action)。证据:ProjectContractAction.java:82。
- **输入**:projectId、newProjectCode、productList(含拆分数量 branchQuantity)、mergeBranchMark。
- **处理规则**:
  1. 调用 insertNewProject:复制 pm_project_header 主表(新 projectCode)、复制 pm_project_member 成员、批量插入 pm_project_product_line(按 branchQuantity)、复制 pm_project_group_relationship。证据:sql-map-project-config.xml:3435/3448/3460/3430。
  2. 重定向到项目维护页,result=202。证据:ProjectContractAction.java:84。
- **输出**:重定向(含新项目 paramId)。
- **异常**:无。

#### FR-CONTRACT-04 查询订单数据
- **触发条件**:用户查看订单数据(checkOrderData.action)。证据:ProjectContractAction.java:92。
- **输入**:projectId。
- **处理规则**:查询 pm_project_product_line 汇总 + RMA 退货数据 + 明细。证据:ProjectAction.java:861-873。
- **输出**:orderDataList + 明细列表。
- **异常**:异常返回 ERROR。

#### FR-CONTRACT-05 查询实际发货清单
- **触发条件**:用户查看实际发货(checkRealOrderData.action)。证据:ProjectContractAction.java:101。
- **输入**:projectId。
- **处理规则**:调用 queryRealOrderDataListByProjectId。证据:ProjectContractAction.java:102。
- **输出**:realOrderDataList + size。
- **异常**:异常返回 ERROR。

### 2.4 发货与设备

#### FR-SHIP-01 查询发货序列号
- **触发条件**:用户查看发货序列号(checkShipmentInfo.action)。证据:ProjectAction.java:928。
- **输入**:projectId、contractNo。
- **处理规则**:
  1. 查询历史发货数量。证据:ProjectAction.java:931。
  2. 查询项目简化信息(含 salesType、column001)。证据:ProjectAction.java:932。
  3. salesType=14 时按 column001(profitCenter)过滤查询。证据:ProjectAction.java:934-935。
  4. 否则按合同号查询,排除已转出设备(transferFlag != 1)。证据:ProjectAction.java:937;sql-map-project-config.xml:2194。
- **输出**:shipmentInfoList。
- **异常**:异常返回 ERROR。

#### FR-SHIP-02 删除发货安装信息
- **触发条件**:用户删除发货安装信息(deleteShipmentInfo.action)。证据:ProjectAction.java:951。
- **输入**:序列号列表。
- **处理规则**:按 contractNo + barCode 关联删除 pm_project_shipment。证据:sql-map-project-config.xml:2833。
- **输出**:result=303。
- **异常**:异常返回 ERROR。

#### FR-SHIP-03 设备转移(见 FR-PROJ-09)
- 详细规则参见 FR-PROJ-09。

### 2.5 项目周报

#### FR-WEEKLY-01 周报起止时间计算
- **触发条件**:创建周报时。证据:ProjectWeeklyAction.java:80。
- **输入**:任意日期。
- **处理规则**:
  1. 取当前日期的 DAY_OF_WEEK,向前回退到周一(2 - day)作为开始(0:00:00)。证据:ProjectWeeklyAction.java:84-89。
  2. 加 6 天作为结束(23:59:59)。证据:ProjectWeeklyAction.java:90-94。
- **输出**:开始时间、结束时间。
- **异常**:无。

#### FR-WEEKLY-02 创建周报(继承上期)
- **触发条件**:用户创建周报(CreateWeekly.action)。证据:ProjectWeeklyAction.java:49。
- **输入**:projectId。
- **处理规则**:
  1. 设置周报起止时间(本周)。证据:ProjectWeeklyAction.java:54-55。
  2. 查询项目计划任务列表。证据:ProjectWeeklyAction.java:56。
  3. 查询上期周报 ID(queryLastWeeklyId),若存在则继承 taskDeviation、remark,并按 optionType 查询上期各项工作/风险/计划/帮助/进展/抄送内容。证据:ProjectWeeklyAction.java:57-68。
- **输出**:周报初始化数据。
- **异常**:异常打印堆栈。

#### FR-WEEKLY-03 保存周报(草稿/提交)
- **触发条件**:用户保存或提交周报(SaveWeekly/SubmitWeekly.action)。证据:ProjectWeeklyAction.java:102/125。
- **输入**:projectWeekly(weeklyId、currentTask、taskStartTime/EndTime、taskDeviation、remark)、workcontentList/riskcontentList/helpcontentList/progresscontentList/plancontentList/mailcontentList。
- **处理规则**:
  1. weeklyId=0 时插入,否则更新。证据:ProjectWeeklyAction.java:104-112/127-135。
  2. 保存草稿:weeklyState=0;提交:weeklyState=1。证据:ProjectWeeklyAction.java:105/128。
  3. 提交时:生成周报 Excel 附件;收集抄送邮箱(mailcontentList 的 optionDesc002 + 当前用户邮箱 + 系统参数 weekly.css.address + 项目成员邮箱);按模板 NOTIFICATION_CODE_WEEKLY_SUBMIT 发送邮件;触发固定通知(NOTIFICATION_CODE_118)。证据:ProjectWeeklyAction.java:138-161。
  4. 更新项目最后刷新时间。证据:ProjectWeeklyAction.java:113/162。
- **输出**:weeklyId(JSON result);失败 result=0。
- **异常**:异常 result=0。

#### FR-WEEKLY-04 编辑周报
- **触发条件**:用户编辑周报(EditWeekly.action)。证据:ProjectWeeklyAction.java:192。
- **输入**:weeklyId。
- **处理规则**:
  1. 查询周报主信息。证据:ProjectWeeklyAction.java:193。
  2. 按 optionType 查询各项工作/风险/帮助/进展/计划/附件/抄送内容。证据:ProjectWeeklyAction.java:195-201。
  3. 已提交周报(weeklyState=1)时查询回复列表。证据:ProjectWeeklyAction.java:202-204。
  4. 更新项目最后刷新时间。证据:ProjectWeeklyAction.java:205。
- **输出**:周报与内容列表。
- **异常**:无。

#### FR-WEEKLY-05 周报回复
- **触发条件**:用户回复周报(Feedback.action)。证据:ProjectWeeklyAction.java:213。
- **输入**:weeklyId、feedback、projectId。
- **处理规则**:插入 pm_project_weekly_feedback(weeklyId、feedback、当前用户、当前时间)。证据:sql-map-project-config.xml:1580。
- **输出**:result=302。
- **异常**:异常打印堆栈。

### 2.6 通知批示

#### FR-NOTIFY-01 查询项目通知
- **触发条件**:用户查询通知(queryProjectNotification.action)。证据:ProjectNotificationAction.java:43。
- **输入**:projectId。
- **处理规则**:查询 pm_project_notification 关联 pm_project_notification_state 的通知列表。证据:ProjectNotificationAction.java:45。
- **输出**:notificationList。
- **异常**:异常返回 ERROR。

#### FR-NOTIFY-02 保存批示
- **触发条件**:用户保存批示(instruction.action)。证据:ProjectNotificationAction.java:121。
- **输入**:projectId、instructionsInfo、instructionId(>0 表示对已有批示的反馈)。
- **处理规则**:插入 pm_project_instruction(projectId、instructionsInfo、instructionsTime=now、instructionsUser=当前用户、dataType=0/1、instructionsId)。证据:sql-map-project-config.xml:1248。
- **输出**:result=301。
- **异常**:异常打印堆栈。

#### FR-NOTIFY-03 查询批示
- **触发条件**:用户查询批示(getInstructionsInfo)。证据:ProjectNotificationAction.java:135。
- **输入**:projectId。
- **处理规则**:查询批示列表(dataType=0),按换行拼接 instructionsInfo。证据:sql-map-project-config.xml:1238。
- **输出**:拼接后的批示文本。
- **异常**:异常打印堆栈。

#### FR-NOTIFY-04 问题工单查询
- **触发条件**:用户查询工单(problemTicket.action)。证据:ProjectNotificationAction.java:58。
- **输入**:projectId。
- **处理规则**:
  1. 查询项目,取 projectCode(去 -后缀)。证据:ProjectNotificationAction.java:66-67。
  2. 按 projectCode 查询 pm_project_incident_table_from_itr。证据:ProjectNotificationAction.java:69。
  3. 查询不到时按 contractNoList 回退查询。证据:ProjectNotificationAction.java:71-75。
  4. 加载 ITR 基础 URL(系统参数 itr.problemTicket.base.url)。证据:ProjectNotificationAction.java:60。
- **输出**:commonList + itrBaseUrl。
- **异常**:异常返回 ERROR。

#### FR-NOTIFY-05 License 授权查询
- **触发条件**:用户查询 License(licenseInfo.action)。证据:ProjectNotificationAction.java:91。
- **输入**:projectId。
- **处理规则**:查询项目,构造 contractNoList(合同号 + projectCode),查询 pm_project_license_info_from_license。证据:ProjectNotificationAction.java:93-105。
- **输出**:commonList。
- **异常**:异常返回 ERROR。

### 2.7 现场验货

#### FR-INSPECT-01 现场验货单导出
- **触发条件**:用户导出现场验货单(exportSpotCheck.action)。证据:ProjectAction.java:532。
- **输入**:projectId。
- **处理规则**:
  1. 查询项目获取合同号。证据:ProjectAction.java:534。
  2. 调用 exportSpotCheckList:从 view_shipment_info_4_pm 聚合设备(按 contractNo + itemCode 分组,排除已转出设备 transferFlag != 1,LEFT JOIN pm_project_spot_check_ignore_item 忽略指定 item)。证据:sql-map-project-config.xml:4474。
  3. 基于模板(spotCheck.xlsx/spotCheckDoc.ftl)生成文件。证据:模板文件位于 template 目录。
- **输出**:文件路径 + 文件名(流式下载)。
- **异常**:异常返回 ERROR。

#### FR-INSPECT-02 过保提醒函导出
- **触发条件**:用户导出过保提醒函(exportOverWarrantyRemind.action)。证据:ProjectAction.java:549。
- **输入**:projectId。
- **处理规则**:类似 FR-INSPECT-01,查询 queryOverWarrantyRemindList,基于模板(《设备过保提醒函》.ftl)生成。证据:sql-map-project-config.xml:4541。
- **输出**:文件路径 + 文件名。
- **异常**:异常返回 ERROR。

#### FR-INSPECT-03 验货忽略项导入
- **触发条件**:管理员/工程管理部导入忽略项(importSpotCheckIgnoreItem)。证据:ProjectAction.java:567。
- **输入**:Excel 文件(itemCode/itemModel/itemName)。
- **处理规则**:
  1. 角色校验(管理员/工程管理部),否则返回 authError。证据:ProjectAction.java:569-572。
  2. 解析 Excel。
  3. truncate pm_project_spot_check_ignore_item 后批量插入。证据:sql-map-project-config.xml:4529/4532。
- **输出**:导入成功条数。
- **异常**:异常返回 ERROR 并提示 exception。

#### FR-INSPECT-04 验货状态定时提醒
- **触发条件**:定时任务(每日/每周)触发。证据:ProjectInspectionMailer.java:67。
- **输入**:无(全量扫描)。
- **处理规则**:
  1. 查询所有需验货项目(queryProjectInspection),含到货验收(taskTypeId=30)、初验(60)、终验(61)的计划与实际时间。证据:sql-map-project-config.xml:4683。
  2. 按办事处分组,计算各节点超期情况(到货验收超期、初验/终验超期)。证据:ProjectInspectionMailer.java:101-138。
  3. 节点超期阈值:43_after(到货验收后)=2 月,44=5 月,45=9 月,46(终验)=-1(按初验后超期计算)。证据:ProjectInspectionMailer.java:60-65/568-617。
  4. 按办事处发送汇总邮件(主送服务经理+项目经理,抄送销售+办事处领导+验收小组);北京办和运营商额外抄送特定人员。证据:ProjectInspectionMailer.java:143-218。
  5. 全国汇总邮件(主送验收小组+用服领导群组,延迟 30 分钟发送)。证据:ProjectInspectionMailer.java:229-453。
  6. 到货验收超期项目单独发送邮件(主送商务,抄送验收小组)。证据:ProjectInspectionMailer.java:458-503。
- **输出**:多封邮件(写入待发送队列)。
- **异常**:异常记录日志。

### 2.8 软件版本更新

#### FR-SOFT-01 查询设备软件版本
- **触发条件**:用户查看软件版本(checkSoftVersion.action)。证据:ProjectAction.java:974。
- **输入**:projectId、contractNo、cbForm(filterItem、queryAffectedProbs)。
- **处理规则**:
  1. 查询项目简化信息,salesType=14 时按 column001(profitCenter)过滤。证据:ProjectAction.java:976-985。
  2. 按合同号查询设备软件版本(conp/cpld/boot/pcb 及备份/变更标记)。证据:ProjectAction.java:997。
  3. queryAffectedProbs=true 时,关联查询受影响的技术公告(JSON_ARRAYAGG)。证据:sql-map-project-common-config.xml:161-176。
- **输出**:softversionList。
- **异常**:异常返回 ERROR。

#### FR-SOFT-02 更新软件版本
- **触发条件**:用户提交软件版本更新(updateSoftVersion.action)。证据:ProjectAction.java:1011。
- **输入**:softVersionJson(含 softversionList 与 softChangeLog)。
- **处理规则**:
  1. 解析 JSON 为对象列表。证据:ProjectAction.java:1014-1022。
  2. 调用 updateSoftversion:
     - 失效旧版本:UPDATE pm_project_soft_version SET datastate=0 WHERE projectId。证据:sql-map-project-config.xml:2410。
     - 插入新版本:批量 INSERT pm_project_soft_version(id、projectId、logId、barcode、conp/conpBak/conpChange、cpld、boot、pcb、executeTime、datastate、contractNo、itemCode、conpType/Series/Mark、customInfo)。证据:sql-map-project-config.xml:2415。
     - 失效旧日志:UPDATE pm_project_soft_change_logs SET latest=0 WHERE projectId。证据:sql-map-project-config.xml:2455。
     - 插入新日志:INSERT pm_project_soft_change_logs(projectId、changeVersion、changeRemark、latest=1、createBy、createTime)。证据:sql-map-project-config.xml:2461。
- **输出**:result=310;softChangeLog.* 序列化。
- **异常**:异常打印堆栈。

#### FR-SOFT-03 查询版本变更历史
- **触发条件**:用户查看历史(checkhistsoftversion.action)。证据:ProjectAction.java:1035。
- **输入**:softChangeLog.projectId、softChangeLog.id。
- **处理规则**:
  1. 查询变更日志列表(按时间倒序),追加 V0(出厂版本)。证据:ProjectAction.java:1036-1037。
  2. id != 0 时查询具体变更的版本列表;id != -1 时查询日志详情。证据:ProjectAction.java:1039-1043。
- **输出**:changeLogList + softversionList + softChangeLog。
- **异常**:无。

### 2.9 文件管理

#### FR-FILE-01 文件上传(周报附件/交付件)
- **触发条件**:用户上传文件(UploadFile/UploadDeliverableFile.action)。证据:ProjectFileAction.java:105/249。
- **输入**:upload(文件数组)、uploadFileName(逗号分隔)、projectWeekly.weeklyId 或 projectDeliver(projectId、eventKey)。
- **处理规则**:
  1. 创建上传目录(/UPLOAD_PATH/weekly|deliver/时间戳)。证据:ProjectFileAction.java:111/253。
  2. 加载扩展名白名单(系统参数 sys.upload.ext.whitelist),逐文件校验。证据:ProjectFileAction.java:117/259。
  3. 调用 getUploadFileRename 重命名文件。证据:ProjectFileAction.java:127/269。
  4. 复制文件到目标目录。证据:ProjectFileAction.java:132-136/274。
  5. 周报附件:构造 WeeklyContent(optionDesc001=原名、optionDesc002=路径),调用 insertWeeklyFiles。证据:ProjectFileAction.java:137-142。
  6. 交付件:构造 ProjectDeliver(deliverableName、deliverablePath),调用 insertProjectDeliverFiles。证据:ProjectFileAction.java:276-282。
- **输出**:重定向(周报)或 result(交付件)。
- **异常**:扩展名不合法返回 ERROR;目录创建失败提示 sys.adderror。

#### FR-FILE-02 文件下载
- **触发条件**:用户下载文件(DownloadFile.action)。证据:ProjectFileAction.java:153。
- **输入**:downpath、downname、result。
- **处理规则**:
  1. 设置响应 charset=ISO8859-1。证据:ProjectFileAction.java:163。
  2. result=0 时直接转码 downname;否则 URLEncoder 编码。证据:ProjectFileAction.java:166-171。
  3. getFileStream:先按 upload 前缀补全路径查找,失败时按 ISO8859-1→UTF-8 解码路径重试。证据:ProjectFileAction.java:186-213。
- **输出**:文件流(stream)。
- **异常**:流为空时打印警告。

#### FR-FILE-03 文件删除
- **触发条件**:用户删除文件(DeleteFile.action)。证据:ProjectFileAction.java:219。
- **输入**:downFlileId。
- **处理规则**:调用 deleteFileById 删除周报附件。证据:ProjectFileAction.java:221。
- **输出**:result=0(成功)/1(失败)。
- **异常**:异常 result=1。

#### FR-FILE-04 交付件删除
- **触发条件**:用户删除交付件(deleteDeliverById.action)。证据:ProjectFileAction.java:234。
- **输入**:deliverid。
- **处理规则**:软删除(update pm_basic_deliver_detail set effectiveTo=NOW where id)。证据:sql-map-project-config.xml:3192。
- **输出**:result=0(成功)/1(失败)。
- **异常**:异常 result=1。

---

## 第3章 数据契约【最关键】

> 分级说明:C=契约字段(对外/跨域稳定接口);I=内部字段(实现细节);D=废弃/历史字段。
> 表结构来自 sql-map-project-config.xml、sql-map-project-config2.xml 的 INSERT/UPDATE/SELECT 语句反推。

### 3.1 pm_project_header(项目主表)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| projectId | int | 否 | 项目主键,自增 | 全局唯一 | C |
| projectCode | varchar | 是 | 项目编码(可含 -后缀,后缀用于区分) | 项目组内可重复,全串唯一 | C |
| smsProjectCode | varchar | 是 | SMS 系统项目编码 | 来自 SMS 同步 | C |
| projectName | varchar | 是 | 项目名称 | - | C |
| projectState | varchar | 是 | 项目状态码(30=已创建,31=指定服务经理,32=指定项目经理,34=填写渠道,20=已闭环,100=不予跟踪,21=创建中) | 状态机驱动 | C |
| isback | varchar | 是 | 回退状态码(同 projectState 语义,用于回退场景) | 与 projectState 互补 | I |
| column001 | varchar | 是 | 办事处编码(officeCode) | 关联 fnd_department | C |
| column002 | varchar | 是 | 客户编码(customerCode) | - | C |
| column003 | varchar | 是 | 客户名称(customerName) | - | C |
| column004 | varchar | 是 | 市场人员名称(marketName) | - | C |
| column005 | varchar | 是 | 系统部名称(systemName) | - | C |
| column006 | varchar | 是 | 拓展人员名称(expendName) | - | C |
| column007 | varchar | 是 | 行业名称(industryName) | - | C |
| column008 | varchar | 是 | 保留字段(服务经理可改) | - | I |
| column009 | date | 是 | 订单创建时间(orderCreateTime) | 来自 SAP | C |
| column010 | varchar | 是 | 项目类别(10=普通类,20=工程类) | 关联基础数据 | C |
| column011 | varchar | 是 | 项目分类(10=直签类,20=非直签类) | 关联基础数据 | C |
| column012 | varchar | 是 | 实施方式(0=原厂直服,1=代理商自服,3=代理商集成,4=原厂集成) | column012Readonly=-1 时可改 | C |
| columno12_readonly | int | 是 | 实施方式只读标记(-1=可改,其他=不可改,从 SMS 刷新) | - | I |
| column013 | varchar | 是 | 最终客户名称 | - | C |
| column014 | varchar | 是 | 不予跟踪/回退说明(backCause) | - | I |
| salesType | varchar | 是 | 销售类型(01=正常,02=借转销,14=销售类借货/总代借货) | 影响 profitCenter 过滤 | C |
| customerProjectName | varchar | 是 | 客户项目名 | - | C |
| majorProjectLevel | varchar | 是 | 重大项目级别 | 映射到 column010 | C |
| compId | varchar | 是 | 公司主表ID | 关联 fnd_company | C |
| customInfo | json | 是 | 自定义信息(JSON:serviceManagerCode、programManagerCode、programManagerCodeB、salesManCode 等) | JSON_MERGE_PATCH 更新 | C |
| createTime | datetime | 否 | 创建时间 | 默认 NOW() | C |
| createBy | varchar | 否 | 创建人(用户名) | - | C |
| updateTime | datetime | 是 | 修改时间 | - | C |
| updateBy | varchar | 是 | 修改人 | - | C |
| effectiveFrom | datetime | 否 | 生效时间_起 | 默认 NOW() | C |
| effectiveTo | datetime | 是 | 生效时间_止(失效时设置) | 失效项目时设为 NOW() | C |

> 证据:sql-map-project-config.xml:1075-1086(INSERT)、1088-1102(UPDATE)、3437-3442(拆分复制);Project.java:21-150。

### 3.2 pm_project_member(项目成员)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键,自增 | 全局唯一 | C |
| projectId | int | 否 | 项目ID | 关联 pm_project_header | C |
| projectType | varchar | 是 | 项目类型(默认售后) | - | I |
| memberRole | varchar | 否 | 成员角色(10=项目经理,20=服务经理,30=销售,40/70/71=团队成员等) | 关联 fnd_basic_data(dataTypeCode=03) | C |
| memberCode | varchar | 否 | 成员用户名 | 关联 fnd_user_info | C |
| memberName | varchar | 是 | 成员姓名 | - | C |
| phoneNum | varchar | 是 | 电话(去除空白) | - | C |
| email | varchar | 是 | 邮箱 | - | C |
| fromFlag | varchar | 是 | 来源标记(FLAG_FROM_PROJECT/FLAG_FROM_MEMBER) | - | I |
| createTime | datetime | 否 | 创建时间 | 默认 NOW() | C |
| createBy | varchar | 否 | 创建人 | - | C |
| effectiveFrom | datetime | 否 | 生效时间_起 | 默认 NOW() | C |
| effectiveTo | datetime | 是 | 生效时间_止 | 失效时设置 | C |

> 证据:sql-map-project-config.xml:2757-2766(INSERT)、2767-2772(UPDATE)、3450-3453(拆分复制);ProjectMember.java:10-27。

### 3.3 pm_project_contract(项目合同)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| contractNo | varchar | 否 | 合同号 | 关联 SAP 订单 | C |
| projectGroupCode | varchar | 否 | 项目组编码 | 关联 pm_project_group | C |
| createTime | datetime | 否 | 创建时间 | 默认 NOW() | C |
| createBy | varchar | 否 | 创建人 | - | C |

> 证据:sql-map-project-config.xml:1261-1264(INSERT)、3360-3363(合并插入)、3017-3026(转移插入)。
> [待澄清] 表是否包含主键 id、projectId 字段?SELECT 语句未明确显示,但语义上应存在。

### 3.4 pm_project_group(项目组)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键,自增 | 全局唯一 | C |
| projectGroupCode | varchar | 否 | 项目组编码 | 全局唯一 | C |
| projectGroupName | varchar | 是 | 项目组名称 | - | C |
| projectType | varchar | 是 | 项目类型 | - | I |
| createTime | datetime | 否 | 创建时间 | - | C |
| createBy | varchar | 否 | 创建人 | - | C |

> 证据:sql-map-project-config.xml:1256-1259(INSERT)、1276-1278(query-maxproject-groupcode)。

### 3.5 pm_project_group_relationship(项目分组关系)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| projectGroupCode | varchar | 否 | 项目组编码 | 关联 pm_project_group | C |
| projectCode | varchar | 否 | 项目编码 | 关联 pm_project_header | C |
| smsProjectCode | varchar | 是 | SMS 项目编码 | - | C |
| mergeBranchMark | varchar | 是 | 合并/拆分标记 | - | I |
| createTime | datetime | 否 | 创建时间 | 默认 NOW() | C |
| createBy | varchar | 否 | 创建人 | - | C |

> 证据:sql-map-project-config.xml:1266-1269(INSERT)、3430-3433(拆分复制)。

### 3.6 pm_project_related_party(项目相关方/渠道)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| projectId | int | 否 | 项目ID | 关联 pm_project_header | C |
| partyRole | varchar | 否 | 角色类型(deliverChannel/serviceChannel/agentChannel/partnerChannel) | - | C |
| partyCode | varchar | 是 | 渠道编码 | - | C |
| partyName | varchar | 是 | 渠道名称 | - | C |
| createTime | datetime | 否 | 创建时间 | 默认 NOW() | C |
| createBy | varchar | 否 | 创建人 | - | C |
| updateTime | datetime | 是 | 修改时间 | - | C |
| updateBy | varchar | 是 | 修改人 | - | C |
| effectiveFrom | datetime | 否 | 生效时间_起 | 默认 NOW() | C |
| effectiveTo | datetime | 是 | 生效时间_止 | 失效时设置 | C |

> 证据:sql-map-project-config.xml:2009-2012(INSERT)、2014-2020(UPDATE)。

### 3.7 pm_project_state(项目状态)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| projectId | int | 否 | 项目ID | 关联 pm_project_header | C |
| projectPlanState | varchar | 是 | 工程计划状态(43=到货验收,44=安装,45=初验,46=终验,48=项目闭环) | 关联 fnd_basic_data(dataTypeCode=22) | C |
| projectplanTime | datetime | 是 | 计划状态时间 | 默认 NOW() | I |
| shipmentState | int | 是 | 发货状态(-1=全到货,1=未发货,2=部分发货) | 由 pm_project_product_line 计算 | C |
| shipmentTime | datetime | 是 | 发货状态时间 | - | I |
| executionState | varchar | 是 | 工程实施状态 | 关联基础数据 | C |
| executionStateTime | datetime | 是 | 实施状态时间 | - | I |
| closeProcessState | varchar | 是 | 闭环流程状态(15=闭环申请) | 关联基础数据 | C |
| closeProcessStateTime | datetime | 是 | 闭环流程状态时间 | - | I |

> 证据:sql-map-project-config.xml:3846-3860(INSERT)、3861-3868(UPDATE)、3870-3881(发货状态计算)。

### 3.8 pm_project_task(项目任务/工程计划)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| taskId | int | 否 | 主键,自增 | 全局唯一 | C |
| projectId | int | 否 | 项目ID | 关联 pm_project_header | C |
| projectType | varchar | 是 | 项目类型 | - | I |
| contractNo | varchar | 是 | 合同号 | - | C |
| taskTypeCode | varchar | 否 | 任务类型编码(关联 fnd_basic_data.dataTypeCode) | - | C |
| taskTypeId | varchar | 否 | 任务类型ID(30=到货验收,60=初验,61=终验) | 关联 fnd_basic_data | C |
| eventPlanHappenDate | date | 是 | 计划发生日期 | - | C |
| eventPlanHappenDateENG | date | 是 | 计划发生日期(ENG) | - | I |
| eventActualFinishDate | date | 是 | 实际完成日期 | - | C |
| visibleFlag | int | 是 | 可见标记 | - | I |
| createTime | datetime | 否 | 创建时间 | 默认 NOW() | C |
| createBy | varchar | 否 | 创建人 | - | C |
| updateTime | datetime | 是 | 修改时间 | - | C |
| updateBy | varchar | 是 | 修改人 | - | C |
| effectiveFrom | datetime | 否 | 生效时间_起 | 默认 NOW() | C |
| effectiveTo | datetime | 是 | 生效时间_止 | 失效时设置 | C |

> 证据:sql-map-project-config.xml:2781-2785(INSERT)、2776-2779(UPDATE 失效)、3490-3499(合并插入)。

### 3.9 pm_project_product_line(项目产品线)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键,自增 | 全局唯一 | C |
| projectId | int | 否 | 项目ID | 关联 pm_project_header | C |
| contractNo | varchar | 是 | 合同号 | - | C |
| itemCode | varchar | 是 | 物料编码 | 关联 fb_items | C |
| itemName | varchar | 是 | 物料名称/描述 | - | C |
| projectQuantity | int | 是 | 项目数量(拆分时设置,否则=orderQuantity) | - | C |
| orderQuantity | int | 是 | 订单数量 | 来自 SAP | C |
| deliverQuantity | int | 是 | 已发货数量 | = orderQuantity - openQuantity | C |
| openQuantity | int | 是 | 未发货数量 | 来自 SAP | C |
| orderNumber | varchar | 是 | 订单号 | 关联 pm_order_data_from_sap | C |
| lineNum | varchar | 是 | 行号 | 关联 pm_order_line_from_sap | C |

> 证据:sql-map-project-config.xml:2098-2101(INSERT)、3460-3471(批量插入)、3366-3385(合并插入)。

### 3.10 pm_project_shipment(项目发货安装)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键,自增 | 全局唯一 | C |
| projectId | int | 否 | 项目ID | 关联 pm_project_header | C |
| barcode | varchar | 否 | 设备序列号 | 项目内唯一 | C |
| itemCode | varchar | 是 | 物料编码 | - | C |
| itemName | varchar | 是 | 物料名称 | - | C |
| receiveName | varchar | 是 | 收货人 | 来自 EMS 视图 | C |
| emsNum | varchar | 是 | EMS 单号 | - | C |
| emsCompany | varchar | 是 | EMS 公司 | - | C |
| packdate | date | 是 | 发货日期 | - | C |
| contractNo | varchar | 是 | 合同号 | - | C |
| installAddress | varchar | 是 | 安装地址 | - | C |
| chProjectId | int | 是 | 串货项目ID(转移用) | - | I |
| chContractNo | varchar | 是 | 串货合同号(转移用,可能含 -C 后缀) | - | I |
| transferProjectId | int | 是 | 转入项目ID | - | I |
| transferContractNo | varchar | 是 | 转入合同号 | - | I |
| transferFlag | int | 是 | 转移标记(0=转销,1=退货,!=1 排除) | - | C |
| createTime | datetime | 否 | 创建时间 | 默认 NOW() | C |
| createBy | varchar | 否 | 创建人 | - | C |

> 证据:sql-map-project-config.xml:2820-2829(INSERT)、2815-2819(UPDATE installAddress)、2996-3013(转移 INSERT)、2830-2848(DELETE)。

### 3.11 pm_project_weekly(项目周报)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| weeklyId | int | 否 | 主键,自增 | 全局唯一 | C |
| projectId | int | 否 | 项目ID | 关联 pm_project_header | C |
| currentTask | varchar | 是 | 当前任务 | - | C |
| taskStartTime | date | 是 | 任务开始时间 | - | C |
| taskEndTime | date | 是 | 任务结束时间 | - | C |
| taskDeviation | varchar | 是 | 任务偏差 | 继承上期 | C |
| remark | varchar | 是 | 备注 | 继承上期 | C |
| weeklyStartTime | datetime | 是 | 周报开始时间(周一 0:00:00) | 由 getWeeklyDateTime 计算 | C |
| weeklyEndTime | datetime | 是 | 周报结束时间(周日 23:59:59) | 由 getWeeklyDateTime 计算 | C |
| weeklyState | int | 否 | 周报状态(0=草稿,1=已提交) | - | C |
| createTime | datetime | 否 | 创建时间 | - | C |
| createBy | varchar | 否 | 创建人 | - | C |
| updateTime | datetime | 是 | 修改时间 | - | C |
| updateBy | varchar | 是 | 修改人 | - | C |

> 证据:sql-map-project-config.xml:1503-1509(INSERT)、1562-1579(UPDATE)、1535-1549(查询)。

### 3.12 pm_project_weekly_content(周报内容)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键,自增 | 全局唯一 | C |
| weeklyId | int | 否 | 周报ID | 关联 pm_project_weekly | C |
| optionDesc001 | varchar | 是 | 选项描述1(文件名/内容) | - | C |
| optionDesc002 | varchar | 是 | 选项描述2(文件路径/邮箱) | - | C |
| optionType | varchar | 否 | 选项类型(WORK=工作,RISK=风险,HELP=帮助,PROGRESS=进展,PLAN=计划,FILE=附件,MAIL=抄送) | - | C |
| createTime | datetime | 否 | 创建时间 | 默认 NOW() | C |
| createBy | varchar | 否 | 创建人 | - | C |
| effectiveFrom | datetime | 否 | 生效时间_起 | 默认 NOW() | C |
| effectiveTo | datetime | 是 | 生效时间_止 | - | I |

> 证据:sql-map-project-config.xml:1510-1521(INSERT)、1556-1561(查询)。

### 3.13 pm_project_weekly_feedback(周报回复)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键,自增 | 全局唯一 | C |
| weeklyId | int | 否 | 周报ID | 关联 pm_project_weekly | C |
| feedback | varchar | 是 | 回复内容 | - | C |
| feedbacker | varchar | 是 | 回复人(用户名) | 关联 fnd_user_info | C |
| feedbackTime | datetime | 否 | 回复时间 | 默认 NOW() | C |

> 证据:sql-map-project-config.xml:1580-1585(INSERT)、1593-1598(查询)。

### 3.14 pm_project_instruction(项目批示)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| projectId | int | 否 | 项目ID | 关联 pm_project_header | C |
| instructionsInfo | varchar | 是 | 批示内容 | - | C |
| instructionsTime | datetime | 是 | 批示时间 | - | C |
| instructionsUser | varchar | 是 | 批示人(用户名) | 关联 fnd_user_info | C |
| dataType | int | 是 | 数据类型(0=批示,1=对批示的反馈) | - | C |
| instructionsId | int | 是 | 关联批示ID(反馈时指向原批示) | - | I |
| createTime | datetime | 否 | 创建时间 | - | C |
| createBy | varchar | 否 | 创建人 | - | C |

> 证据:sql-map-project-config.xml:1248-1253(INSERT)、1238-1244(查询 dataType=0)、1243-1244(查询 dataType=1)。
> [待澄清] 表是否含主键 id?INSERT 语句未显式包含,但反馈语义需要。

### 3.15 pm_project_notification(项目通知)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| notifyId | int | 否 | 主键,自增 | 全局唯一 | C |
| notifySubject | varchar | 是 | 通知主题 | - | C |
| notifyContent | varchar | 是 | 通知内容 | - | C |
| projectId | int | 否 | 项目ID | 关联 pm_project_header | C |
| createBy | varchar | 否 | 创建人 | - | C |
| createTime | datetime | 否 | 创建时间 | 默认 NOW() | C |

> 证据:sql-map-project-config.xml:3299-3305(INSERT)。

### 3.16 pm_project_notification_state(通知状态/对象)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| notifyId | int | 否 | 通知ID | 关联 pm_project_notification | C |
| notifyObject | varchar | 否 | 通知对象(用户名) | 关联 fnd_user_info | C |
| notifyState | int | 否 | 通知状态(0=未读,1=已读) | 默认 0 | C |
| createTime | datetime | 否 | 创建时间 | 默认 NOW() | C |
| createBy | varchar | 否 | 创建人 | - | C |

> 证据:sql-map-project-config.xml:3307-3317(INSERT)、3632-3637(UPDATE notifyState=1)。

### 3.17 pm_project_soft_version(项目软件版本)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | - | C |
| projectId | int | 否 | 项目ID | 关联 pm_project_header | C |
| logId | int | 否 | 变更日志ID | 关联 pm_project_soft_change_logs | C |
| barcode | varchar | 否 | 设备序列号 | - | C |
| conp | varchar | 是 | CONP 版本 | - | C |
| conpBak | varchar | 是 | CONP 备份版本 | - | C |
| conpChange | int | 是 | CONP 变更标记(0=未变,1=已变) | 默认 0 | C |
| cpld | varchar | 是 | CPLD 版本 | - | C |
| cpldBak | varchar | 是 | CPLD 备份版本 | - | C |
| cpldChange | int | 是 | CPLD 变更标记 | - | C |
| boot | varchar | 是 | BOOT 版本 | - | C |
| bootBak | varchar | 是 | BOOT 备份版本 | - | C |
| bootChange | int | 是 | BOOT 变更标记 | - | C |
| pcb | varchar | 是 | PCB 版本 | - | C |
| pcbBak | varchar | 是 | PCB 备份版本 | - | C |
| pcbChange | int | 是 | PCB 变更标记 | - | C |
| executeTime | datetime | 是 | 执行时间 | 默认 NOW() | C |
| datastate | int | 是 | 数据状态(0=失效,1=有效) | 默认 1 | C |
| contractNo | varchar | 是 | 合同号 | - | C |
| itemCode | varchar | 是 | 物料编码 | - | C |
| conpType | varchar | 是 | CONP 类型 | 关联 prob_softwares.entryType | C |
| conpSeries | varchar | 是 | CONP 系列 | 关联 prob_softwares.entrySeries | C |
| conpMark | varchar | 是 | CONP 标记(BETWEEN markStart AND markEnd) | 关联 prob_softwares | C |
| customInfo | json | 是 | 自定义信息 | - | I |
| createTime | datetime | 否 | 创建时间 | 默认 NOW() | C |
| createBy | varchar | 否 | 创建人 | - | C |
| updateTime | datetime | 是 | 修改时间 | - | C |
| updateBy | varchar | 是 | 修改人 | - | C |

> 证据:sql-map-project-config.xml:2415-2447(INSERT)、2410-2413(UPDATE 失效)、2610(查询)。

### 3.18 pm_project_soft_change_logs(软件变更日志)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键,自增 | 全局唯一 | C |
| projectId | int | 否 | 项目ID | 关联 pm_project_header | C |
| changeVersion | varchar | 是 | 变更版本号 | - | C |
| changeRemark | varchar | 是 | 变更备注 | - | C |
| latest | int | 否 | 是否最新(0=否,1=是) | 项目内仅一条 latest=1 | C |
| createBy | varchar | 否 | 创建人 | - | C |
| createTime | datetime | 否 | 创建时间 | 默认 NOW() | C |
| updateTime | datetime | 是 | 修改时间 | - | C |
| updateBy | varchar | 是 | 修改人 | - | C |

> 证据:sql-map-project-config.xml:2459-2467(INSERT)、2455-2458(UPDATE latest=0)、2475-2477(查询历史)。

### 3.19 pm_project_spot_check_ignore_item(现场验货忽略项)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| itemCode | varchar | 否 | 物料编码 | - | C |
| itemModel | varchar | 是 | 物料型号 | - | C |
| itemName | varchar | 是 | 物料名称 | - | C |

> 证据:sql-map-project-config.xml:4529-4538(truncate + 批量 INSERT)、4522-4524(LEFT JOIN)。
> [待澄清] 表无主键,truncate 覆盖式导入,语义为全局配置表。

### 3.20 pm_basic_prj_deliver(交付件模板)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键,自增 | 全局唯一 | C |
| column010 | varchar | 是 | 项目类别(10/20) | - | C |
| column011 | varchar | 是 | 项目分类(10/20) | - | C |
| dataTypeCode | varchar | 否 | 事件类型编码 | 关联 fnd_basic_data | C |
| basicDataId | varchar | 否 | 事件基础数据ID | 关联 fnd_basic_data | C |
| dataTypeCodeSon | varchar | 否 | 交付件类型编码 | 关联 fnd_basic_data | C |
| basicDataIdSon | varchar | 否 | 交付件基础数据ID | 关联 fnd_basic_data | C |
| isNeed | int | 是 | 是否必传(0=否,1=是) | - | C |
| effectiveFrom | datetime | 否 | 生效时间_起 | - | C |
| effectiveTo | datetime | 是 | 生效时间_止 | 失效时设置 | C |

> 证据:sql-map-project-config.xml:3040-3047(查询)、3253-3262(必传校验)、3289-3295(按 id 查询)。

### 3.21 pm_basic_deliver_detail(交付件明细/实际交付件)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键,自增 | 全局唯一 | C |
| projectId | int | 否 | 项目ID | 关联 pm_project_header | C |
| projectType | varchar | 是 | 项目类型 | - | I |
| taskId | int | 是 | 任务ID | - | I |
| contractNo | varchar | 是 | 合同号 | - | C |
| deliverId | int | 否 | 交付件模板ID | 关联 pm_basic_prj_deliver | C |
| deliverableName | varchar | 是 | 交付件文件名 | - | C |
| deliverablePath | varchar | 是 | 交付件文件路径 | - | C |
| deliverableType | varchar | 是 | 交付件类型 | - | I |
| uploadUser | varchar | 否 | 上传人(用户名) | 关联 fnd_user_info | C |
| uploadTime | datetime | 否 | 上传时间 | 默认 NOW() | C |
| effectiveFrom | datetime | 否 | 生效时间_起 | 默认 NOW() | C |
| effectiveTo | datetime | 是 | 生效时间_止 | 软删除时设置 | C |

> 证据:sql-map-project-config.xml:3108-3120(INSERT)、3192-3194(软删除)、3135-3141(查询)、6359-6378(合同验收交付查询)。

### 3.22 pm_project_incident_table_from_itr(工单记录-来自 ITR)

> 外部同步表,字段未在源码中显式定义,SELECT * 查询。证据:sql-map-project-config.xml:6382-6390。
> 关键字段(反推):projectCode、contractNo、barCode 等。
> [待澄清] 表完整字段列表需查询 ITR 同步任务定义。

### 3.23 pm_project_license_info_from_license(License 授权-来自 License)

> 外部同步表,字段未在源码中显式定义,SELECT * 查询。证据:sql-map-project-config.xml:6405。
> [待澄清] 表完整字段列表需查询 License 同步任务定义。

### 3.24 关联外部表(引用,非本域所有)

| 表名 | 用途 | 关联键 | 分级 |
|---|---|---|---|
| pm_order_data_from_sap | 订单数据(SAP 同步) | contractNo、orderNumber、compCode | C(外部契约) |
| pm_order_line_from_sap | 订单行(SAP 同步) | orderNumber、lineNum、lineType、compCode | C(外部契约) |
| pm_project_property_from_sms | 项目属性(SMS 同步) | systemName | C(外部契约) |
| pm_project_product_lease_line_from_crm | 租赁配置(CRM 同步) | projectCode | C(外部契约) |
| pm_project_product_config_level_info_from_crm | 配置关系(CRM 同步) | smsProjectCode | C(外部契约) |
| pm_person_from_oa | 人员(OA 同步) | username | C(外部契约) |
| fnd_user_info | 用户信息 | username | C(基础数据) |
| fnd_basic_data | 基础数据 | dataTypeCode、basicDataId | C(基础数据) |
| fnd_department | 部门 | departmentNum | C(基础数据) |
| fnd_company | 公司 | code、compId | C(基础数据) |
| fb_items | 物料 | item、itemname、describe_ | C(基础数据) |
| view_shipment_info_4_pm | 发货信息视图 | contract_code、barcode、packId | C(视图契约) |
| view_shipment_ems_4_pm | 发货 EMS 视图 | contract_code、packId | C(视图契约) |
| pm_notification_template | 通知模板 | templateCode | C(基础数据) |
| pm_cl_evaluation_header | 闭环评估头(引用) | projectId、projectCode | C(跨域引用) |
| pm_cl_callback | 闭环回访(引用) | projectId | C(跨域引用) |

---

## 第4章 非功能需求

### 4.1 权限与安全
- **NFR-SEC-01 角色分权**:项目列表与修改按角色分流(工程管理部/管理员/财务/项目管理员 → 全量;服务经理/项目经理/普通用户/项目查阅 → 按权限)。证据:ProjectAction.java:350-357/649-685。
- **NFR-SEC-02 项目权限缓存**:非管理员角色在单项目 Session 期内只判断一次权限,结果缓存到 Session(cacheProjectPowerMap)。证据:ProjectAction.java:654-684。
- **NFR-SEC-03 安全标识**:项目操作需校验 validateFlag(MD5("success"))。证据:Project.java:149-150/498-503。
- **NFR-SEC-04 批量操作角色限制**:项目清理、批量导入、验货忽略项导入仅管理员/工程管理部可执行。证据:ProjectAction.java:2969-2972/2997-3000/569-572。
- **NFR-SEC-05 上传扩展名白名单**:文件上传按系统参数 `sys.upload.ext.whitelist` 校验。证据:ProjectFileAction.java:117/259。

### 4.2 数据一致性
- **NFR-CON-01 项目状态机**:项目状态(30→31→32→34→20/100)由角色与操作驱动,回退通过 isback 字段记录。证据:ProjectAction.java:789-842。
- **NFR-CON-02 软删除模式**:交付件、项目主表、成员、合同等均采用 effectiveTo 失效模式,保留历史。证据:sql-map-project-config.xml:3192/728-742。
- **NFR-CON-03 闭环条件校验**:项目可发起闭环申请的前提:必传交付件齐全 + 最终客户/渠道维护 + 安装数量=发货数量 + 无正在回访流程。证据:ProjectAction.java:732-779。
- **NFR-CON-04 合同唯一性**:创建项目时校验合同号未已创建。证据:ProjectAction.java:410-414/446-448。
- **NFR-CON-05 软件版本最新标记**:pm_project_soft_change_logs 项目内仅一条 latest=1,更新前先失效旧记录。证据:sql-map-project-config.xml:2455-2467。
- **NFR-CON-06 customInfo JSON 合并**:项目自定义信息通过 JSON_MERGE_PATCH 增量更新,避免覆盖。证据:sql-map-project-config.xml:1099。

### 4.3 通知与集成
- **NFR-NOTIFY-01 邮件通知**:立项、周报提交、批量变更成员、验货超期等场景发送邮件,模板来自 pm_notification_template。证据:ProjectAction.java:592-610;ProjectWeeklyAction.java:153-159;ProjectInspectionMailer.java:143-218。
- **NFR-NOTIFY-02 系统通知**:成员变更、安装地址保存、周报提交等触发动态/固定通知(写入 pm_project_notification + pm_project_notification_state)。证据:ProjectMemberAction.java:61/83/106;ProjectWeeklyAction.java:161。
- **NFR-INT-01 外部数据同步**:订单(SAP)、项目属性(SMS)、人员(OA)、租赁/配置(CRM)、工单(ITR)、License 等通过定时任务同步。证据:ProjectDao.java:983-1097(queryRmaOrderDataByContractNo/queryProjectPreAndFinalInspection 等)。
- **NFR-INT-02 工作流集成**:批量变更项目经理时联动终止 Activiti 工作流任务(setAssignee/terminate)。证据:ProjectUtils.java:84-86/154。

### 4.4 性能与定时任务
- **NFR-PERF-01 验货提醒定时**:每日/每周定时扫描项目验货节点,按办事处分组发送邮件。证据:ProjectInspectionMailer.java:67。
- **NFR-PERF-02 全国汇总邮件延迟发送**:全国汇总邮件延迟 30 分钟发送(mailExpectSendTime)。证据:ProjectInspectionMailer.java:450-452。
- **NFR-PERF-03 项目列表临时表**:复杂查询使用临时表(tmp_tb_project、tmp_tb_shipstate、temp_tb_projectId_filter_itemModel)优化。证据:sql-map-project-config2.xml:44-610。

### 4.5 可观测性
- **NFR-OBS-01 项目最后刷新时间**:成员变更、安装地址保存、周报保存/提交等操作均更新 pm_project_header.projectRefreshTime。证据:ProjectMemberAction.java:60/82/105;ProjectWeeklyAction.java:113/162。
- **NFR-OBS-02 操作日志**:项目操作记录到 pm_project_handle_log(insertProjecthandleLog)。证据:ProjectDao.java:437。

### 4.6 数据保留
- **NFR-RET-01 历史设备保留**:查询发货设备时默认排除已转出设备(transferFlag != 1),但 queryHistoryProjectShipmentSize 包含转销/退货设备。证据:ProjectAction.java:931;ProjectDao.java:758。

---

## 附录:关键歧义点与待澄清事项

1. **[待澄清] pm_project 与 pm_project_header 关系**:部分 UPDATE 语句使用 pm_project(如 `UPDATE pm_project SET effectiveTo = NOW()` sql-map-project-config.xml:3626/3996/4001/4007),与主表 pm_project_header 是否为同义词/视图?需确认。
2. **[待澄清] pm_project_contract 主键与 projectId**:SELECT/INSERT 语句仅显示 contractNo + projectGroupCode,但语义上应存在主键与 projectId 关联,需确认表结构。
3. **[待澄清] pm_project_instruction 主键**:INSERT 语句未包含主键 id,但反馈语义(dataType=1, instructionsId)需要关联原批示,需确认是否含自增主键。
4. **[待澄清] pm_project_spot_check_ignore_item 无主键**:truncate + 全量插入模式,语义为全局配置,但缺少主键与生效时间字段,需确认是否需要审计。
5. **[待澄清] 工单/License 同步表字段**:pm_project_incident_table_from_itr、pm_project_license_info_from_license 使用 SELECT * 查询,字段定义需查询对应同步任务(GainDataFromITR/GainDataFromLicense)。
