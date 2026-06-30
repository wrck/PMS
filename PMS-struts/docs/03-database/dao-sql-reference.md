# PMS-Struts DAO-SQL 映射参考文档

> 本文档基于 iBATIS SQL 映射文件与 DAO 接口，梳理每个 DAO 方法对应的 SQL ID、SQL 类型及操作表。

---

## 一、SQL映射文件与DAO对应关系总览

| SQL映射文件 | 模块 | 对应DAO类 |
|---|---|---|
| sql-map-project-config.xml | 项目管理 | ProjectDao |
| sql-map-admin-config.xml | 系统管理 | UserManageDao, RoleManageDao, DepartmentManageDao, BasicDataDao, LoginDao, PasswordDao, SendMailDao |
| sql-map-prob-config.xml | 技术公告 | ProbDao（位于 com.dp.plat.prob.dao） |
| sql-map-subcontract-config.xml | 转包项目 | SubcontractDao（位于 com.dp.plat.subcontract.dao） |
| sql-map-presales-config.xml | 售前项目 | PresalesDao |
| sql-map-callback-config.xml | 回访管理 | CallBackDao |
| sql-map-activity-config.xml | 工作流审批 | WorkflowDao |
| sql-map-report-config.xml | 报表 | ReportDao |
| sql-map-maintenance-config.xml | 维保管理 | ProjectDao（维保相关方法） |
| sql-map-warrantyCallback-config.xml | 质保回访 | WarrantyCallbackDao, ProjectWarrantyCallbackDao（均位于 com.dp.plat.warrantyCallback.dao） |
| sql-map-certificate-config.xml | 合格证/印章 | CertificateDao（位于 com.dp.plat.plus.certificate.dao） |
| sql-map-work-config.xml | 工作流/待办 | WorkflowDao, WorkSpaceDao |
| sql-map-refresh-data-sap-config.xml | SAP数据同步 | 数据同步服务（无直接DAO） |
| sql-map-refresh-data-d365-config.xml | D365数据同步 | 数据同步服务（无直接DAO） |
| sql-map-refresh-data-sms-config.xml | SMS数据同步 | 数据同步服务（无直接DAO） |
| sql-map-refresh-data-crm-config.xml | CRM数据同步 | 数据同步服务（无直接DAO） |
| sql-map-refresh-data-oa-config.xml | OA数据同步 | 数据同步服务（无直接DAO） |
| sql-map-refresh-data-common-config.xml | 通用数据同步 | 数据同步服务（无直接DAO） |

---

## 二、ProjectDao（项目管理）

**接口路径**: `com.dp.plat.dao.ProjectDao`  
**SQL映射文件**: `sql-map-project-config.xml`

| DAO方法 | SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|---|
| queryProjectList | find_project_count / find_project_list | SELECT | pm_project_header（临时表） | 查询项目列表，含临时表创建/删除 |
| queryProjectList | create_tmp_tb_project | UPDATE(DDL) | 临时表 | 创建项目临时表 |
| queryProjectList | drop_tmp_tb_project | UPDATE(DDL) | 临时表 | 删除项目临时表 |
| queryProjectList | create_temp_tb_projectId_filter_itemModel | INSERT(DDL) | 临时表 | 按产品型号过滤 |
| queryProjectList | drop_temp_tb_projectId_filter_itemModel | UPDATE(DDL) | 临时表 | 删除产品型号过滤临时表 |
| queryProjectList | create_temp_table_project_contract_warrantyState | INSERT(DDL) | 临时表 | 按维保状态过滤 |
| queryProjectList | drop_temp_table_project_contract_warrantyState | UPDATE(DDL) | 临时表 | 删除维保状态过滤临时表 |
| insertProject | insert-project | INSERT | pm_project_header | 新增项目 |
| updateProjectByProjectId | update-project-byprojectid | UPDATE | pm_project_header | 更新项目信息 |
| queryPersonList | query_person_list | SELECT | 多表关联 | 查询销售人员信息 |
| queryInstructionList | query_instruction_list | SELECT | pm_project_instruction（文档原写pm_instruction） | 查询总部批示信息 |
| queryFeedbackList | query_feedback_list | SELECT | pm_project_instruction（文档原写pm_instruction） | 查询批示反馈信息 |
| insertProjectGroup | insert-projectgroup | INSERT | pm_project_group | 插入项目组 |
| insertProjectContract | insert-projectcontract | INSERT | pm_project_contract | 插入合同 |
| insertProjectGroupRelationship | insert-projectgrouprelationship | INSERT | pm_project_group_relationship | 插入项目组关系 |
| queryMaxProjectGroupCode | query-max-project-group-id | SELECT | pm_project_group | 查询最大项目组编码 |
| queryProjectByContractNo | query-project-bycontractno | SELECT | pm_project_header | 根据合同号查询项目 |
| queryProjectByContractNoAndType | queryProjectByContractNoAndType | SELECT | pm_project_header | 根据合同号和类型查询项目 |
| insertProjectMember(Project) | insert-projectmember | INSERT | pm_project_member | 插入项目成员 |
| insertInstruction | insert_pm_instruction | INSERT | pm_project_instruction（文档原写pm_instruction） | 插入项目批示 |
| queryProjectById | query_project_byId | SELECT | pm_project_header | 根据ID查询项目 |
| queryProjectSimplifyByProjectId | queryProjectSimplifyByProjectId | SELECT | pm_project_header | 查询项目简化信息 |
| updateProjectMember(Project) | update-projectmember | UPDATE | pm_project_member | 更新项目成员 |
| insertProjectRelatedParty | insert-projectrelatedparty | INSERT | pm_project_relatedparty | 插入渠道信息 |
| updateProjectRelatedParty | update-projectrelatedparty | UPDATE | pm_project_relatedparty | 更新渠道信息 |
| queryProjectListByPower | query_project_bypower_count / query_project_bypower_list | SELECT | pm_project_header | 根据权限查询项目列表 |
| insertProjectWeekly | insert_project_weekly | INSERT | pm_project_weekly | 保存周报主数据 |
| batchInsertWeeklyContent | insert_weekly_content | INSERT | pm_project_weekly_content（文档原写pm_weekly_content） | 保存周报内容 |
| updateProjectStateByProjectId | update-projectstate-byprojectid | UPDATE | pm_project_header | 更新项目状态 |
| queryProjectStateByProjectId | query-projectstate-byprojectid | SELECT | pm_project_header | 查询项目状态 |
| queryOrderLineFromSapByContractNo | query-orderline-fromsap-bycontractno | SELECT | pm_order_data_from_erp_sap（文档原写pm_order_data） | 查询订单产品数据 |
| insertProjectProductLine | insert-projectproductline | INSERT | pm_project_product_line | 插入产品行信息 |
| queryOrderDataListByProjectId | query-orderdatalist-byprojectid | SELECT | pm_order_data_from_erp_sap（文档原写pm_order_data） | 查询产品信息汇总 |
| queryOrderDataDetailListByProjectId | queryOrderDataDetailListByProjectId | SELECT | pm_order_data_from_erp_sap（文档原写pm_order_data） | 查询产品信息明细 |
| queryProjectWeeklyList | query_project_weekly | SELECT | pm_project_weekly | 查询项目周报 |
| queryPorjectWeekly | query_project_weekly_one | SELECT | pm_project_weekly | 查询周报基本信息 |
| queryWeeklyContentList | query_weekly_contents | SELECT | pm_project_weekly_content（文档原写pm_weekly_content） | 查询周报内容 |
| queryShipmentInfoByContractNo | query-shipmentinfo-bycontractno | SELECT | 多表关联 | 查询序列号清单 |
| queryShipmentInfoSizeByContractNo | query_shipmentInfo_size_by_contractNo | SELECT | 多表关联 | 查询序列号数量 |
| updateProjectWeekly | update_project_weekly | UPDATE | pm_project_weekly | 更新项目周报 |
| deleteWeeklyContent | delete_weekly_content | UPDATE | pm_project_weekly_content（文档原写pm_weekly_content） | 删除周报内容（软删除） |
| deleteFileById | delete_upload_file | UPDATE | fnd_files | 删除周报附件 |
| backToLastStep | update_project_state | UPDATE | pm_project_header | 更新项目状态（回退） |
| queryProjectPlanEventByProject | query-projectplanevent-byproject | SELECT | pm_project_task | 查询事件节点列表 |
| queryProjectMembers | query_pm_member_list | SELECT | pm_project_member | 查询项目成员 |
| insertProjectMember(ProjectMember) | insert_project_member | INSERT | pm_project_member | 增加项目成员 |
| updateProjectMember(ProjectMember) | update_project_member | UPDATE | pm_project_member | 更新项目成员 |
| updateProjectPlanByProjectId | update-projectplan-byprojectid | UPDATE | pm_project_task | 更新项目计划 |
| insertProjectPlan | insert-projectplan | INSERT | pm_project_task | 插入项目计划 |
| queryProjectTaskByProjectId | query-projecttask-buprojectid | SELECT | pm_project_task | 查询项目计划列表 |
| updateProjectShipment | update_project_shipment | UPDATE | pm_project_shipment | 更新安装地址 |
| insertProjectShipment | insert_project_shipment | INSERT | pm_project_shipment | 插入安装地址 |
| queryProjectShipment | query_project_shipment | SELECT | pm_project_shipment | 查询安装地址 |
| insertWeeklyFeedback | insert_weekly_feedback | INSERT | pm_project_weekly_feedback（文档原写pm_weekly_feedback） | 插入周报回复 |
| queryWeeklyFeedbackList | query_weekly_feedback | SELECT | pm_project_weekly_feedback（文档原写pm_weekly_feedback） | 查询周报回复列表 |
| queryProjectDeliverList | query-projectdeliver-list | SELECT | pm_basic_prj_deliver（文档原写pm_basic_deliver） | 查询交付件下拉列表 |
| batchInsertDeliverFiles | insert-deliverfiles | INSERT | pm_basic_deliver_detail | 插入交付件 |
| queryDeliverDetailByProjectIdAndProjectType | queryDeliverDetailByProjectIdAndProjectType | SELECT | pm_basic_deliver_detail | 按项目类型查询交付件 |
| queryDeliverDetailByProjectIdAndDeliverType | queryDeliverDetailByProjectIdAndDeliverType | SELECT | pm_basic_deliver_detail | 按数据类型查询交付件 |
| deleteDeliverById | delete-deliver-byid | UPDATE | pm_basic_deliver_detail | 软删除交付件 |
| updateProjectIsbackByProjectId | update-projectisback-byprojectid | UPDATE | pm_project_header | 更新回退状态 |
| insertProjecthandleLog | insert_project_log | INSERT | pm_project_log（文档原写pm_project_handle_log） | 插入项目操作日志 |
| updateProjecthandleLog | update_project_log | UPDATE | pm_project_log（文档原写pm_project_handle_log） | 更新项目日志状态 |
| updateProjectImplByProjectId | update-projectimpl-byprojectid | UPDATE | pm_project_header | 更新项目实施方式 |
| queryPersonFromOaByCode | query-person-fromoa-bycode | SELECT | 多表关联 | 根据code查询Person |
| queryLastWeeklyId | query_last_weeklyid | SELECT | pm_project_weekly | 查询上期周报 |
| queryNotificationTemplate | query_notifation_template | SELECT | pm_notification_template | 查询通知模板 |
| queryDeliverDetailCountByProjectDeliver | queryNeededUndelivedCount | SELECT | pm_basic_deliver_detail | 查询未上传必传交付件数量 |
| queryNeededUndelivedProjectDeliverList | queryNeededUndelivedProjectDeliverList | SELECT | pm_basic_deliver_detail | 查询未上传必传交付件 |
| updateEventActualFinishDateByTask | updateEventActualFinishDateByTask | UPDATE | pm_project_task | 更新完成时间 |
| queryProjectDeliverById | queryProjectDeliverById | SELECT | pm_basic_prj_deliver（文档原写pm_basic_deliver） | 查询交付件模版 |
| insertNotification | insert_notification | INSERT | pm_project_notification（文档原写pm_notification） | 插入项目通知 |
| notificationSetObjectList | insert_notification_object | INSERT | pm_project_notification_state（文档原写pm_notification_object） | 插入通知对象 |
| queryProjectMemberCountByProject | queryProjectMemberCountByProject | SELECT | pm_project_member | 查询生效用户数 |
| queryMailByUsername | query_mail_by_username | SELECT | fnd_user_info | 查询用户邮箱 |
| queryMailByRoleId | query_mails_with_role | SELECT | fnd_user_info | 根据角色查询邮箱 |
| queryProjectNameByProjectId | query-projectname-byprojectid | SELECT | pm_project_header | 查询项目名称 |
| queryContractList | query-contract-list | SELECT | pm_project_contract | 查询合同信息 |
| insertMergeContract | insert-merge-contract | INSERT | pm_project_contract | 插入关联合同 |
| insertMergeProduct | insert-merge-product | INSERT | pm_project_product_line | 插入合并产品 |
| queryProjectGroupSize | query-projectgroup-size | SELECT | pm_project_group | 查询项目组项目数量 |
| insertProjectGroup(Map) | insert-projectgroup-map | INSERT | pm_project_group | 增加项目组信息 |
| insertProjectInfo | insert-project-info | INSERT | pm_project_header | 复制插入项目 |
| insertProjectMember(Map) | insert-projectmember-map | INSERT | pm_project_member | 复制插入项目成员 |
| updateProjectProduct | update-project-product | UPDATE | pm_project_product_line | 更新产品项目数量 |
| batchInsertProduct | batch-insert-product | INSERT | pm_project_product_line | 批量插入产品 |
| queryProjectTaskSize | query-projecttask-size | SELECT | pm_project_task | 查询计划数量 |
| insertMergeTask | insert-merge-task | INSERT | pm_project_task | 合并插入计划 |
| querySystemList | query-system-list | SELECT | fnd_department | 查询系统部集合 |
| queryMemberAddress | query-member-address | SELECT | fnd_user_info | 查询团队成员邮箱 |
| updateServiceProject | update-service-project | UPDATE | pm_project_header | 不予跟踪更新 |
| queryProjectContractCountByContractNo | query-contract-count-bycontractno | SELECT | pm_project_contract | 查询合同是否存在 |
| invalidProjectHeader | invalid-project-header | UPDATE | pm_project_header | 失效项目主表 |
| invalidProjectNotification | invalid-project-notification | UPDATE | pm_project_notification（文档原写pm_notification） | 失效项目通知 |
| invalidProjectGroupRelationship | invalid-project-group-relationship | UPDATE | pm_project_group_relationship | 失效项目关系表 |
| queryProjectShipmentSize | query-project-shipment-size | SELECT | pm_project_shipment | 查询安装地址数量 |
| queryProjectState | query-project-state | SELECT | pm_project_state | 查询项目状态记录 |
| insertProjectState | insert-project-state | INSERT | pm_project_state | 增加项目状态记录 |
| updateProjectState | update-project-state | UPDATE | pm_project_state | 更新项目状态记录 |
| updateProjectCloseTime | update-project-close-time | UPDATE | pm_project_header | 更新项目闭环时间 |
| updateProjectPlanStateToClose | update-project-plan-state-to-close | UPDATE | pm_project_task | 更新计划状态为闭环 |
| queryDeliverName | query-deliver-name | SELECT | pm_basic_prj_deliver（文档原写pm_basic_deliver） | 查询交付件类型名称 |
| queryCallBackList | query-callback-list | SELECT | pm_cl_callback | 查询回访流程任务 |
| queryCallBackingSize | query-callbacking-size | SELECT | pm_cl_callback | 查询审批中回访数量 |
| querySoftversionList | query_soft_version_list | SELECT | 多表关联 | 查询软件版本 |
| updateInvalidSoftversion | update_invalid_soft_version | UPDATE | pm_project_soft_version | 失效软件版本 |
| insertSoftVersionList | insert_project_soft_version | INSERT | pm_project_soft_version | 插入软件版本 |
| querySoftVersionNum | query_hist_soft_version | SELECT | pm_project_soft_version_log | 查询版本数量 |
| updateInvalidSoftVersionLog | update_invalid_soft_log | UPDATE | pm_project_soft_version_log | 失效版本日志 |
| insertSoftVersionLog | insert_soft_version_log | INSERT | pm_project_soft_version_log | 插入版本变更记录 |
| queryHistSoftChangeLog | query_hist_soft_version_change | SELECT | pm_project_soft_version_log | 查询版本变更历史 |
| queryHistSoftVersionList | query_hist_soft_version_list | SELECT | pm_project_soft_version | 查询版本变更详情 |
| queryOneSoftChangeLog | query_one_soft_change_log | SELECT | pm_project_soft_version_log | 查询单个版本日志 |
| queryShipemntSizeByContractNo | query_shipmentInfo_size_by_contractNo | SELECT | 多表关联 | 查询发货数量 |
| queryRmaOrderDataByContractNo | query_rma_order_data | SELECT | pm_order_data_from_erp_sap（文档原写pm_order_data） | 查询退货订单 |
| queryProjectListByOfficeAndMemberCode | find_project_list | SELECT | pm_project_header | 按办事处和成员查询 |
| queryRealOrderDataListByProjectId | query_real_order_data_list | SELECT | pm_project_product_line_real | 查询实际发货清单 |
| queryCallBackRunList | query-callback-run-list | SELECT | pm_cl_callback | 查询审批中回访 |
| queryValidMemberByProjectId | query-valid-member-by-projectid | SELECT | pm_project_member | 查询有效成员 |
| queryProjectPreAndFinalInspection | query-project-pre-final-inspection | SELECT | pm_project_task | 初验终验提醒 |
| batchDeleteProject | batch-delete-project | DELETE | pm_project_header | 批量删除项目 |
| queryExistsProjectByContractNos | query-exists-project-by-contractnos | SELECT | pm_project_header | 查询已创建项目 |
| queryProjectArrivalReceipt | query-project-arrival-receipt | SELECT | pm_project_task | 到货验收提醒 |
| queryTransferProjectList | find_transfer_project_list | SELECT | pm_project_header | 转移设备查询项目 |
| queryTransferShipmentInfoByContractNo | query-transferShipmentInfo-bycontractno | SELECT | 多表关联 | 查询可转移设备 |
| insertProjectTransferShipment | insert_project_transfer_shipment | INSERT | pm_project_shipment | 插入转移设备 |
| updateProjectTransferShipment | update_project_transfer_shipment | UPDATE | pm_project_shipment | 更新转移设备 |
| insertTransferContract | insert_transfer_contract | INSERT | pm_project_contract | 插入转移合同关联 |
| querySpotCheckList | query-spot-check-list | SELECT | 多表关联 | 查询收货确认单 |
| deleteShipmentInstallInfoByProjectId | deleteShipmentInstallInfoByProjectId | DELETE | pm_project_shipment | 删除发货安装信息 |
| selectProjectMaintenanceList | selectProjectMaintenanceList | SELECT | pm_project_maintenance | 查询维保列表 |
| insertOrUpdateProjectMaintenance | insertOrUpdateProjectMaintenance | INSERT | pm_project_maintenance | 插入或更新维保 |
| selectProjectMaintenanceById | selectProjectMaintenanceById | SELECT | pm_project_maintenance | 查询维保详情 |
| updateProjectDeliverById | updateProjectDeliverById | UPDATE | pm_basic_deliver_detail | 更新交付件 |
| queryProjectWarrantyState | queryProjectWarrantyState | SELECT | 多表关联 | 查询维保状态 |
| queryProjectMaintenanceDeliverCount | queryProjectMaintenanceDeliverCount | SELECT | pm_project_service_delivery | 查询维保交付数量 |
| insertProjectServiceDeliveryBySelective | insertProjectServiceDeliveryBySelective | INSERT | pm_project_service_delivery | 增加服务交付 |
| selectProjectSupervisionById | selectProjectSupervisionById | SELECT | pm_project_supervision | 查询督导详情 |
| selectProjectSupervisionList | selectProjectSupervisionList | SELECT | pm_project_supervision | 查询督导列表 |
| insertOrUpdateProjectSupervision | insertOrUpdateProjectSupervision | INSERT | pm_project_supervision | 插入或更新督导 |
| selectDailyMaintenanceUsers | selectDailyMaintenanceUsers | SELECT | pm_project_maintenance | 查询指定日期维护人 |
| selectDailyMaintenanceMapList | selectDailyMaintenanceMapList | SELECT | pm_project_maintenance | 查询指定日期维护记录 |
| querySoleAgentProject | querySoleAgentProject | SELECT | pm_project_header | 查询总代借货项目 |
| queryProjectOldOrderContractInfo | queryProjectOldOrderContractInfo | SELECT | pm_project_contract | 查询历史执行单合同 |
| queryProjectNewOrderContractInfo | queryProjectNewOrderContractInfo | SELECT | pm_project_contract | 查询新增执行单合同 |
| invalidSoleAgentProjectContract | invalidSoleAgentProjectContract | UPDATE | pm_project_contract | 失效旧合同 |
| deleteProjectUnlinkedContractProductLine | deleteProjectUnlinkedContractProductLine | DELETE | pm_project_product_line | 删除失效产品清单 |
| updateProjectSalesType | updateProjectSalesType | UPDATE | pm_project_header | 更新销售类型 |
| queryProjectLeaseLineByProjectCode | queryProjectLeaseLineByProjectCode | SELECT | pm_project_lease_line | 查询租赁配置 |
| queryProjectProductConfigLevelInfoByProjectCode | queryProjectProductConfigLevelInfoByProjectCode | SELECT | pm_project_product_config_level | 查询配置关系 |

---

## 三、UserManageDao（用户管理）

**接口路径**: `com.dp.plat.dao.UserManageDao`  
**SQL映射文件**: `sql-map-admin-config.xml`

| DAO方法 | SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|---|
| queryUserList | query-user-count / query-userlist | SELECT | fnd_user_info | 查询用户列表（分页） |
| queryUserByUserName | query-user-by-name | SELECT | fnd_user_info | 根据用户名查用户 |
| queryUsersByUserNames | select-users-byusernames | SELECT | fnd_user_info | 根据多个用户名查用户 |
| updateuser | update-user | UPDATE | fnd_user_info | 更新用户基本信息 |
| updatepwdbyusername | update-pwd-byusername | UPDATE | fnd_user_info | 更新密码 |
| updatepwdbyuser | update-user-chageloginpass | UPDATE | fnd_user_info | 修改登录密码 |
| queryRolelist | query_sys_roles | SELECT | fnd_roles | 查询角色列表 |
| queryAllMenuList | query_menu_modules | SELECT | fnd_menus | 查询所有菜单 |
| queryUserMenuList | query_menu_modules | SELECT | fnd_menus | 查询用户菜单 |
| queryUserByUserId | query_user_by_id | SELECT | fnd_user_info | 根据ID查询用户 |
| queryUserMenuidsByUserid | query_usermenuids_by_id | SELECT | fnd_user_menus | 查询用户菜单ID |
| addUserInfo | insert-user-object / insert-menuForUser-object | INSERT | fnd_user_info, fnd_user_menus | 新增用户及菜单权限 |
| updateUserInfo | update-user-object / delete-menuForUser-byUserId / insert-menuForUser-object | UPDATE/INSERT/DELETE | fnd_user_info, fnd_user_menus | 更新用户及菜单权限 |
| queryUserMenu | query-menu-byId | SELECT | fnd_menus | 查询菜单信息 |
| updateUser | update-user-object | UPDATE | fnd_user_info | 更新用户对象 |
| deleteUsermenu | delete-menuForUser-byUserId | DELETE | fnd_user_menus | 删除用户菜单 |
| insertUsermenu | insert-menuForUser-object | INSERT | fnd_user_menus | 插入用户菜单 |
| updateUserPower | update_user_power | UPDATE | fnd_user_power | 更新用户区域权限 |
| insertUserpower | insert_user_power | INSERT | fnd_user_power | 插入用户区域权限 |
| queryAllUser | query_user_allMap | SELECT | fnd_user_info | 查询所有系统用户 |
| queryAllUserList | query-userlist-all | SELECT | fnd_user_info | 查询所有用户集合 |
| queryAllUserMap | query_user_allMap | SELECT | fnd_user_info | 查询所有用户Map |
| queryUserSizeByUserName | query_username_size | SELECT | fnd_user_info | 查询用户名是否存在 |
| queryUserWithRoleId | query_user_with_role | SELECT | fnd_user_info | 查询某角色用户 |
| queryServiceMails | query_mails_with_role | SELECT | fnd_user_info | 查询某角色邮件地址 |
| queryServiceMails(officeCodes, roleId) | query_mails_with_role_and_office | SELECT | fnd_user_info | 查询办事处某角色邮件 |
| queryUserWithRoleIdAndDpNo | query_user_with_dpNo_role | SELECT | fnd_user_info | 查询某部门某角色用户 |
| queryUserWithRoleIdAndDpNoOrInAreaPower | query_user_with_dpNo_role_orin_areaPower | SELECT | fnd_user_info | 含区域权限的用户查询 |
| queryMailsByRoleAndOfficeCodes | query_mails_and_office | SELECT | fnd_user_info | 查询办事处角色邮件 |

---

## 四、RoleManageDao（角色管理）

**接口路径**: `com.dp.plat.dao.RoleManageDao`  
**SQL映射文件**: `sql-map-admin-config.xml`

| DAO方法 | SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|---|
| queryRoleList | query-role-count / query-rolelist | SELECT | fnd_roles | 查询角色列表（分页） |
| addRoleSubmit | insert-roleObject / insert-roleMenuPower-object | INSERT | fnd_roles, fnd_role_menus | 新增角色及菜单权限 |
| updateRoleSubmit | update-roleObject / delete-roleMenuPower-byRoleId / insert-roleMenuPower-object | UPDATE/INSERT/DELETE | fnd_roles, fnd_role_menus | 更新角色及菜单权限 |
| queryRoleMenuPowerList | query-roleMenu-list | SELECT | fnd_role_menus | 查询角色菜单权限 |

---

## 五、DepartmentManageDao（部门管理）

**接口路径**: `com.dp.plat.dao.DepartmentManageDao`  
**SQL映射文件**: `sql-map-admin-config.xml`

| DAO方法 | SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|---|
| queryDepartmentList | query-department-count / query-departmentlist | SELECT | fnd_department | 查询部门列表（分页） |
| addDepartmentSubmit | insert-departmentObject | INSERT | fnd_department | 新增部门 |
| refreshDepartment | query-sap-departmentList / truncate_department / 批量插入 | SELECT/DELETE/INSERT | fnd_department | 从SAP刷新部门数据 |
| queryAllDepartments | query_all_department | SELECT | fnd_department | 查询所有部门 |
| queryDepartmentMap | query_department_map | SELECT | fnd_department | 查询部门Map |
| queryDepartmentByDepartmentNum | queryDepartmentByDepartmentNum | SELECT | fnd_department | 根据部门编码查询 |
| queryCompanyList | queryCompanyList | SELECT | fnd_company | 查询公司列表 |
| queryCompanyOne | queryCompanyOne | SELECT | fnd_company | 查询单个公司 |

---

## 六、BasicDataDao（基础数据）

**接口路径**: `com.dp.plat.dao.BasicDataDao`  
**SQL映射文件**: `sql-map-admin-config.xml`

| DAO方法 | SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|---|
| queryBasicDataBeans | query_basic_data | SELECT | fnd_basic_data | 按类型查询基础数据 |
| queryBasicDataType | query_basic_data_type | SELECT | fnd_basic_data_type | 查询基础数据类型 |
| queryBasicDataBean | query_basic_data_one | SELECT | fnd_basic_data | 根据ID查询基础数据 |
| queryBasicDataBeanAll | query_basic_data_all | SELECT | fnd_basic_data | 查询全部基础数据 |
| updateBasicData | update_basic_data | UPDATE | fnd_basic_data | 更新基础数据 |
| insertBasicDataBean | insert_basic_data | INSERT | fnd_basic_data | 插入基础数据 |
| findBasicDataId | find_basic_data_id | SELECT | fnd_basic_data | 查询基础数据ID是否存在 |
| querySysArgList | querySysArgList | SELECT | fnd_sys_arg | 查询系统参数列表 |
| querySysArg | query_sys_arg | SELECT | fnd_sys_arg | 查询系统参数值 |
| executeSql | execute_sql | UPDATE | 动态表 | 执行动态SQL |
| insertFileInfo | insert_file_info | INSERT | fnd_files | 插入文件信息 |
| queryFileInfo | query_flie_info | SELECT | fnd_files | 查询文件信息 |
| queryFileMap | query_file_map | SELECT | fnd_files | 查询文件Map |
| queryFileList | query_file_list | SELECT | fnd_files | 查询文件列表 |
| queryBasicDataBeanMap | query_basic_data_for_map | SELECT | fnd_basic_data | 查询基础数据Map |
| queryBasicDataNameById | query_basicdataname_byId | SELECT | fnd_basic_data | 根据ID查询数据名称 |
| queryBasicDataBeanByDataId | query_basicdata_bydataId | SELECT | fnd_basic_data | 根据dataId查询数据 |
| deleteFile | delete_file | DELETE | fnd_files | 删除文件 |
| queryBasicDataBeanByAttri | query_basicdata_by_attri | SELECT | fnd_basic_data | 按属性查询基础数据 |
| queryBasicDataBeanMapWithSub | query_basic_data_for_map_with_sub | SELECT | fnd_basic_data | 查询含子分类的基础数据 |
| refreshCacheData | refreshCacheData | UPDATE | fnd_sys_arg | 刷新缓存 |

---

## 七、LoginDao（登录认证）

**接口路径**: `com.dp.plat.dao.LoginDao`  
**SQL映射文件**: `sql-map-admin-config.xml`

| DAO方法 | SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|---|
| querUser | query-user-by-name | SELECT | fnd_user_info | 根据用户名查询用户 |
| queryUserMenuMap | query_permissions_by_name | SELECT | fnd_user_menus | 获取用户功能权限 |
| queryUserMenuNameMap | query_permissions_name_code_by_userId | SELECT | fnd_user_menus | 获取权限名称对应关系 |
| queryUserDefaultPage | query_defaultpage_by_username_1 | SELECT | fnd_user_info | 获取用户默认首页 |
| queryRoleMenuPowerList | query-roleMenu-list | SELECT | fnd_role_menus | 获取角色菜单权限 |
| querySysArg | query_sys_arg | SELECT | fnd_sys_arg | 获取系统环境变量 |

---

## 八、PasswordDao（密码管理）

**接口路径**: `com.dp.plat.dao.PasswordDao`  
**SQL映射文件**: `sql-map-admin-config.xml`

| DAO方法 | SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|---|
| usChangelogin | update-user-chageloginpass | UPDATE | fnd_user_info | 修改登录密码 |

---

## 九、SendMailDao（邮件管理）

**接口路径**: `com.dp.plat.dao.SendMailDao`  
**SQL映射文件**: `sql-map-admin-config.xml`

| DAO方法 | SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|---|
| keepMailInfo | insert_into_sys_mails | INSERT | fnd_mails | 保存邮件内容 |
| gainMailInfoList | query_sys_mails | SELECT | fnd_mails | 获取待发送邮件列表 |
| updateMailInfo | update_sys_mails_state | UPDATE | fnd_mails | 更新邮件发送状态 |

---

## 十、ProbDao（技术公告）

**接口路径**: `com.dp.plat.prob.dao.ProbDao`（位于prob子包）  
**SQL映射文件**: `sql-map-prob-config.xml`

| DAO方法 | SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|---|
| queryNextVal | query_next_val | SELECT | 序列 | 查询下一个ID值 |
| insertProb | insert_into_prob | INSERT | prob_main（文档原写pm_prob） | 插入技术公告 |
| queryProbCount | query_prob_count | SELECT | prob_main（文档原写pm_prob） | 查询技术公告数量 |
| queryProbList | query_prob_list | SELECT | prob_main（文档原写pm_prob） | 查询技术公告列表 |
| queryProbOne | query_prob_one | SELECT | prob_main（文档原写pm_prob） | 查询单个技术公告 |
| queryProbFileMap | query_prob_file_map | SELECT | fnd_files | 查询技术公告附件 |
| updateProb | update_prob | UPDATE | prob_main（文档原写pm_prob） | 更新技术公告 |
| updateProbStatus | update_prob_status | UPDATE | prob_main（文档原写pm_prob） | 更新技术公告状态 |
| checkSoftVersionList | check_soft_version_list | SELECT | prob_soft_version（文档原写pm_prob_soft_version） | 检查软件版本列表 |
| updateInvalidSoftversion | update_invalid_softversion | UPDATE | prob_soft_version（文档原写pm_prob_soft_version） | 失效软件版本 |
| insertSoftversion | insert_into_softversion | INSERT | prob_soft_version（文档原写pm_prob_soft_version） | 插入软件版本 |
| queryProbSoftVersion | query_prob_soft_version | SELECT | prob_soft_version（文档原写pm_prob_soft_version） | 查询软件版本 |
| queryProbRestoreList | query_prob_restore_list | SELECT | prob_restore（文档原写pm_prob_restore） | 查询技术公告恢复列表 |
| countProbRestoreList | count_prob_restore_list | SELECT | prob_restore（文档原写pm_prob_restore） | 统计恢复列表数量 |
| insertBatchProbRestoreTaskList | insert_batch_probRestore_task_list | INSERT | pm_prob_restore_task（⚠数据库中不存在此表，可能为prob_restore_process的误写） | 批量插入恢复任务 |
| queryProbRestoreTask | query_list_probRestore_task | SELECT | pm_prob_restore_task（⚠数据库中不存在此表，可能为prob_restore_process的误写） | 查询恢复任务列表 |
| insertRestoreProcess | insert_restore_process | INSERT | prob_restore_process（文档原写pm_prob_restore_process） | 插入恢复流程 |
| updateProbRestoreProcessId | update_prob_restore_processId | UPDATE | prob_restore（文档原写pm_prob_restore） | 更新恢复流程ID |
| updateProbRestoreAssignee | update_prob_restore_assignee | UPDATE | prob_restore（文档原写pm_prob_restore） | 更新恢复处理人 |
| deleteProbInfo | delete_prob_info | UPDATE | prob_main（文档原写pm_prob） | 删除技术公告（软删除） |
| queryProbFileList | query_prob_file_list | SELECT | fnd_files | 查询附件列表 |
| insertProbTaskWeekly | insert_prob_task_weekly | INSERT | prob_restore_weekly（文档原写pm_prob_task_weekly） | 插入技术公告周报 |
| queryProbTaskWeekly | query_prob_task_weekly | SELECT | prob_restore_weekly（文档原写pm_prob_task_weekly） | 查询技术公告周报 |
| batchDeleteProbRestores | batch_delete_probRestores | DELETE | prob_restore（文档原写pm_prob_restore） | 批量删除恢复记录 |
| queryExportProbList | query_exportProb_list | SELECT | prob_main（文档原写pm_prob） | 查询导出数据 |
| queryProbStatistics | query_prob_statistics | SELECT | prob_main（文档原写pm_prob） | 查询技术公告统计 |
| insertProbReadLog | insertProbReadLog | INSERT | pm_prob_read_log | 插入阅读记录 |
| countProbReadLog | count_prob_read_log | SELECT | pm_prob_read_log | 统计阅读记录 |
| queryProbReadLog | query_prob_read_log | SELECT | pm_prob_read_log | 查询阅读记录 |
| selectProductComponentById | selectProductComponentById | SELECT | prob_product_component | 查询产品组件 |
| insertProductComponent | insertProductComponent | INSERT | prob_product_component | 插入产品组件 |
| updateProductComponentById | updateProductComponentById | UPDATE | prob_product_component | 更新产品组件 |
| selectProductComponentList | selectProductComponentList | SELECT | prob_product_component | 查询产品组件列表 |
| selectProbProductById | selectProbProductById | SELECT | prob_product | 查询技术公告产品 |
| insertProbProduct | insertProbProduct | INSERT | prob_product | 插入技术公告产品 |
| updateProbProductById | updateProbProductById | UPDATE | prob_product | 更新技术公告产品 |
| selectProbProductList | selectProbProductList | SELECT | prob_product | 查询技术公告产品列表 |
| deleteProbProductByProbId | deleteProbProductByProbId | DELETE | prob_product | 删除技术公告产品 |
| bastchInsertProbProduct | bastchInsertProbProduct | INSERT | prob_product | 批量插入技术公告产品 |

---

## 十一、SubcontractDao（转包项目）

**接口路径**: `com.dp.plat.subcontract.dao.SubcontractDao`  
**SQL映射文件**: `sql-map-subcontract-config.xml`

| DAO方法 | SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|---|
| selectSubcontractProjectById | selectSubcontractProjectById | SELECT | pm_subcontract_project_header（文档原写pm_sc_project） | 根据ID查询转包项目 |
| selectSubcontractProjectVOById | selectSubcontractProjectVOById | SELECT | pm_subcontract_project_header（文档原写pm_sc_project） | 查询转包项目VO |
| selectSubcontractProjectList | selectSubcontractProjectList | SELECT | pm_subcontract_project_header（文档原写pm_sc_project） | 查询转包项目列表 |
| selectSubcontractProjectVOList | selectSubcontractProjectVOList | SELECT | pm_subcontract_project_header（文档原写pm_sc_project） | 查询转包项目VO列表 |
| selectSubcontractProjectVOListPageable | selectSubcontractProjectVOListPageable | SELECT | pm_subcontract_project_header（文档原写pm_sc_project） | 分页查询转包项目 |
| countSubcontractProjectVOListPageable | countSubcontractProjectVOListPageable | SELECT | pm_subcontract_project_header（文档原写pm_sc_project） | 统计分页转包项目数量 |
| selectSubcontractFacilitatorList | selectSubcontractFacilitatorList | SELECT | pm_facilitator（文档原写pm_sc_facilitator，共用服务商表） | 查询供应商列表 |
| queryShipmentinfoByContractNosAndProjectIds | queryShipmentinfoByContractNosAndProjectIds | SELECT | 多表关联 | 查询转包项目序列号 |
| queryProjectList | queryProjectList | SELECT | pm_project_header | 查询项目列表 |
| checkSubcontractName | checkSubcontractName | SELECT | pm_subcontract_project_header（文档原写pm_sc_project） | 检查转包名称 |
| insertSubcontractProject | insertSubcontractProject | INSERT | pm_subcontract_project_header（文档原写pm_sc_project） | 插入转包项目 |
| insertSubcontractProjectSelective | insertSubcontractProjectSelective | INSERT | pm_subcontract_project_header（文档原写pm_sc_project） | 选择性插入转包项目 |
| selectSubcontractLineList | selectSubcontractLineList | SELECT | pm_subcontract_project_line（文档原写pm_sc_line） | 查询转包行列表 |
| selectSubcontractDeliverById | selectSubcontractDeliverById | SELECT | pm_subcontract_deliver_files（文档原写pm_sc_deliver） | 查询转包交付件 |
| selectSubcontractDeliverList | selectSubcontractDeliverList | SELECT | pm_subcontract_deliver_files（文档原写pm_sc_deliver） | 查询转包交付件列表 |
| selectSubcontractDeliverVOList | selectSubcontractDeliverVOList | SELECT | pm_subcontract_deliver_files（文档原写pm_sc_deliver） | 查询转包交付件VO列表 |
| insertSubcontractDeliver | insertSubcontractDeliver | INSERT | pm_subcontract_deliver_files（文档原写pm_sc_deliver） | 插入转包交付件 |
| deleteSubcontractDeliver | deleteSubcontractDeliverById | DELETE | pm_subcontract_deliver_files（文档原写pm_sc_deliver） | 删除转包交付件 |
| updateSubcontractDeliverByIdSelective | updateSubcontractDeliverByIdSelective | UPDATE | pm_subcontract_deliver_files（文档原写pm_sc_deliver） | 选择性更新交付件 |
| updateSubcontractProjectByIdSelective | updateSubcontractProjectByIdSelective | UPDATE | pm_subcontract_project_header（文档原写pm_sc_project） | 选择性更新转包项目 |
| batchInsertSubcontractLine | batchInsertSubcontractLine | INSERT | pm_subcontract_project_line（文档原写pm_sc_line） | 批量插入转包行 |
| batchDeleteSubcontractLine | batchDeleteSubcontractLine | DELETE | pm_subcontract_project_line（文档原写pm_sc_line） | 批量删除转包行 |
| queryContractNoEngineeFee | queryContractNoEngineeFee | SELECT | 多表关联 | 查询工程服务费 |
| queryContractNoEngineeFeeWithSubPrice | queryContractNoEngineeFeeWithSubPrice | SELECT | 多表关联 | 查询含转包价的工程服务费 |
| insertSubcontractEvaluationHeader | insertSubcontractEvaluationHeader | INSERT | pm_cl_evaluation_header（文档原写pm_sc_evaluation_header，共用评价表） | 插入转包评价 |
| updateSubcontractEvaluationHeader | updateSubcontractEvaluationHeader | UPDATE | pm_cl_evaluation_header（文档原写pm_sc_evaluation_header，共用评价表） | 更新转包评价 |
| querySubcontractTaskList | querySubcontractTaskList | SELECT | act_ru_task | 查询转包任务列表 |
| selectSubcontractPaymentList | selectSubcontractPaymentList | SELECT | pm_subcontract_project_payment（文档原写pm_sc_payment） | 查询转包付款列表 |
| selectSubcontractPaymentById | selectSubcontractPaymentById | SELECT | pm_subcontract_project_payment（文档原写pm_sc_payment） | 查询转包付款详情 |
| insertSubcontractPayment | insertSubcontractPayment | INSERT | pm_subcontract_project_payment（文档原写pm_sc_payment） | 插入转包付款 |
| deleteSubcontractPaymentById | deleteSubcontractPaymentById | DELETE | pm_subcontract_project_payment（文档原写pm_sc_payment） | 删除转包付款 |
| updateSubcontractPaymentByIdSelective | updateSubcontractPaymentByIdSelective | UPDATE | pm_subcontract_project_payment（文档原写pm_sc_payment） | 选择性更新付款 |
| insertSubcontractCallback | insertSubcontractCallback | INSERT | pm_subcontract_project_callback（文档原写pm_sc_callback） | 插入转包回访 |
| insertSubcontractCallbackSelective | insertSubcontractCallbackSelective | INSERT | pm_subcontract_project_callback（文档原写pm_sc_callback） | 选择性插入转包回访 |
| queryCallBackId | queryCallBackId | SELECT | pm_subcontract_project_callback（文档原写pm_sc_callback） | 查询回访ID |
| queryCallBackQuesnaireId | queryCallBackQuesnaireId | SELECT | pm_subcontract_project_callback（文档原写pm_sc_callback） | 查询回访问卷ID |
| queryCallBackQuesnaireVersion | queryCallBackQuesnaireVersion | SELECT | pm_subcontract_project_callback（文档原写pm_sc_callback） | 查询回访问卷版本 |
| selectSubcontractCallbackList | selectSubcontractCallbackList | SELECT | pm_subcontract_project_callback（文档原写pm_sc_callback） | 查询转包回访列表 |
| selectMaxSubcontractCallback | selectMaxSubcontractCallback | SELECT | pm_subcontract_project_callback（文档原写pm_sc_callback） | 查询最大回访记录 |
| updateSubcontractCallbackByIdSelective | updateSubcontractCallbackByIdSelective | UPDATE | pm_subcontract_project_callback（文档原写pm_sc_callback） | 选择性更新回访 |
| querySubcontractCommentList | querySubcontractCommentList | SELECT | fnd_act_hi_comment | 查询转包审批意见 |
| insertSubcontractFacilitator | insertSubcontractFacilitator | INSERT | pm_facilitator（文档原写pm_sc_facilitator，共用服务商表） | 插入供应商 |
| updateSubcontractFacilitatorByIdSelective | updateSubcontractFacilitatorByIdSelective | UPDATE | pm_facilitator（文档原写pm_sc_facilitator，共用服务商表） | 选择性更新供应商 |
| selectSubcontractFacilitatorById | selectSubcontractFacilitatorById | SELECT | pm_facilitator（文档原写pm_sc_facilitator，共用服务商表） | 查询供应商详情 |
| querySubcontractInfoForProject | querySubcontractInfoForProject | SELECT | pm_subcontract_project_header（文档原写pm_sc_project） | 查询项目转包信息 |
| insertSubcontractPrice | insertSubcontractPrice | INSERT | pm_subcontract_project_price（文档原写pm_sc_price） | 插入转包价格 |
| updateSubcontractPriceByIdSelective | updateSubcontractPriceByIdSelective | UPDATE | pm_subcontract_project_price（文档原写pm_sc_price） | 选择性更新转包价格 |
| selectRejectedSubcontractProjectList | selectRejectedSubcontractProjectList | SELECT | pm_subcontract_project_header（文档原写pm_sc_project） | 查询被驳回的转包申请 |
| querySubcontractExportData | querySubcontractExportData | SELECT | pm_subcontract_project_header（文档原写pm_sc_project） | 查询转包导出数据 |
| querySubcontractPaiedAmmount | querySubcontractPaiedAmount | SELECT | pm_subcontract_project_payment（文档原写pm_sc_payment） | 查询已付金额 |
| queryNextPaymentTask | queryNextPaymentTask | SELECT | pm_subcontract_project_header（文档原写pm_sc_project） | 查询下次付款任务 |
| querySSESubcontractPaymentList | querySSESubcontractPaymentList | SELECT | pm_subcontract_project_payment（文档原写pm_sc_payment） | 查询SSE付款信息 |
| deleteEmptySubcontractPayment | deleteEmptySubcontractPayment | DELETE | pm_subcontract_project_payment（文档原写pm_sc_payment） | 删除空付款信息 |
| updateSSESubcontractPaymentTime | updateSSESubcontractPaymentTime | UPDATE | pm_subcontract_project_payment（文档原写pm_sc_payment） | 更新SSE付款时间 |
| selectDefaultMultiDimByDep | selectDefaultMultiDimByDep | SELECT | pm_sc_multi_dim（⚠数据库中不存在此表） | 查询默认多维度信息 |

---

## 十二、PresalesDao（售前项目）

**接口路径**: `com.dp.plat.dao.PresalesDao`  
**SQL映射文件**: `sql-map-presales-config.xml`

| DAO方法 | SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|---|
| queryPresalesById | query_presales_byid | SELECT | pm_presales_project_header | 根据ID查询售前项目 |
| queryPresalesProductByPresalesId | query_presalesproduct_by_presalesid | SELECT | pm_presales_project_product_line | 查询售前产品列表 |
| invalidProjectMember | update_invalid_member_bymemberRole | UPDATE | pm_project_member | 失效项目成员 |
| queryPresalesList | query_presales_count / query_presales_list | SELECT | pm_presales_project_header | 查询售前项目列表 |
| updatePresaleHeader | update_presales_header | UPDATE | pm_presales_project_header | 更新售前项目头 |
| queryCallBackQuesnaireId | query_presales_callbackId | SELECT | pm_presales_project_callback | 查询回访问卷ID |
| queryQuesnaireIdBycallbackId | query_presales_quesnaireId | SELECT | pm_presales_project_callback | 查询问卷ID |
| updateCallBackQuesnaire | update_presales_quesnaire | UPDATE | pm_presales_project_callback | 更新回访问卷 |
| queryCallBackQuesnaireVersion | query_presales_version | SELECT | pm_presales_project_callback | 查询回访问卷版本 |
| insertCallBackQuesnaire | insert_presales_quesnaire | INSERT | pm_presales_project_callback | 插入回访问卷 |
| updatePresalesState | update_presales_state | UPDATE | pm_presales_project_header | 更新售前项目状态 |
| queryPresalesCodeNum | query_presales_code_num | SELECT | pm_presales_project_header | 查询售前编码数量 |
| updatePresalesCode | update_presales_code | UPDATE | pm_presales_project_header | 更新售前编码 |
| updatePresalesProduct | update_presales_product | UPDATE | pm_presales_project_product_line | 更新售前产品 |
| queryIsHasProjectTask | query_presales_task_size | SELECT | pm_project_task | 查询是否有项目任务 |
| insertPresaleTasks | insert_presales_tasks | INSERT | pm_project_task | 插入售前任务 |
| queryPresalesTaskList | query_presales_en_task | SELECT | pm_project_task | 查询售前任务列表 |
| updatePresalesTaskDeliverFiles | update_presales_task_files | UPDATE | pm_project_task | 更新任务交付文件 |
| updatePresalesTask | update_presales_task_finshedtime | UPDATE | pm_project_task | 更新任务完成时间 |
| updatePresalesConfirmFileIds | update_presales_confirmfiles | UPDATE | pm_presales_project_header | 更新确认文件 |
| updatePrealesFileIds | update_presales_task_deliverFileIds_delete | UPDATE | pm_project_task | 删除交付文件ID |
| queryPresaleShipmentInfo | query_presale_shipmentInfo | SELECT | 多表关联 | 查询售前发货信息 |
| queryPresalesExportData | queryPresalesExportData | SELECT | pm_presales_project_header | 查询售前导出数据 |
| updatePresalesDuration | updatePresalesDuration | INSERT | pm_presales_project_duration | 更新售前项目时长 |
| queryPresaleLend2SaleInfo | query_presale_lend_2_sale | SELECT | pm_presales_lend_2_sale_from_sms | 查询借货转销售信息 |
| queryPresaleLend2RmaInfo | query_presale_lend_2_rma | SELECT | pm_presales_lend_2_rma_from_sms | 查询借货退货信息 |
| selectPresalesTempAuthInfo | selectPresalesTempAuthInfo | SELECT | pm_presales_lend_detail_from_oa | 查询临时授权数据 |

---

## 十三、CallBackDao（回访管理）

**接口路径**: `com.dp.plat.dao.CallBackDao`  
**SQL映射文件**: `sql-map-callback-config.xml`

| DAO方法 | SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|---|
| insertCallBack | insert_callback_info | INSERT | pm_cl_callback | 保存回访申请 |
| updateCallBackInstId | update_callback_instid | UPDATE | pm_cl_callback | 更新流程实例ID |
| queryCallBackById | query_callback_byId | SELECT | pm_cl_callback | 根据ID查询回访 |
| queryCallBackQuesnaireVersion | query_cb_quesnaire_version | SELECT | pm_cl_callback_quesnaire | 查询回访问卷版本 |
| insertCallBackQuesnaire | insert_callback_quesnaire | INSERT | pm_cl_callback_quesnaire | 插入回访问卷 |
| queryCbQuesnaire | query_callback_quesnaire | SELECT | pm_cl_callback_quesnaire | 查询回访问卷详情 |
| queryQuesnaireTemplateID | query_quesnaire_template_id | SELECT | pm_cl_quesnaire_result_header | 查询问卷模板ID |
| queryCallBackQuesnaireId | query_callbackQuesnaireId | SELECT | pm_cl_callback_quesnaire | 查询回访问卷ID |
| updateCallBackQuesnaire | update_callback_quesnaire | UPDATE | pm_cl_callback_quesnaire | 更新回访问卷 |
| updateCallBackApplyState | update_callback_applyState | UPDATE | pm_cl_callback | 更新回访申请状态 |
| queryCallBackComment | query_callback_comment | SELECT | fnd_act_hi_comment | 查询回访审批意见 |
| updateCallBack | update_callback | UPDATE | pm_cl_callback | 更新回访信息 |

---

## 十四、WorkflowDao（工作流审批）

**接口路径**: `com.dp.plat.dao.WorkflowDao`  
**SQL映射文件**: `sql-map-activity-config.xml`, `sql-map-work-config.xml`

| DAO方法 | SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|---|
| queryProcdef | query_procdef | SELECT | fnd_act_procdef | 查询流程定义 |
| insertActComment(int, String, String, String, int, String) | insert_fnd_act_comment | INSERT | fnd_act_hi_comment | 插入审批意见 |
| insertActComment(HashMap) | insert_fnd_act_comment_by_params | INSERT | fnd_act_hi_comment | 插入审批意见（参数Map） |
| updateSelfActComment | update_fnd_act_comment_by_params | UPDATE | fnd_act_hi_comment | 更新自身审批意见 |
| queryActComment | query_act_comment_list | SELECT | fnd_act_hi_comment | 查询审批意见列表 |
| updateApplytableInfo | update_apply_info_byobjid | UPDATE | 动态表 | 更新业务表流程数据 |
| queryTaskByInstIdAndVariable | queryTaskByInstIdAndVariable | SELECT | act_ru_variable | 根据流程变量查找任务 |
| updateRunVariableById | updateRunVariableById | UPDATE | act_ru_variable | 更新流程变量值 |
| updateRunVariableByInstIdAndVariable | updateRunVariableByInstIdAndVariable | UPDATE | act_ru_variable | 按实例和变量名更新 |

---

## 十五、ReportDao（报表）

**接口路径**: `com.dp.plat.dao.ReportDao`  
**SQL映射文件**: `sql-map-report-config.xml`

| DAO方法 | SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|---|
| queryAssignedRate | query_assigned_rate | SELECT | pm_project_header | 查询项目指派率 |
| queryTraceRate | query_trace_rate | SELECT | pm_project_header | 查询项目跟踪率 |
| createQualityTmpTable | create_tmp_table_for_quality | UPDATE(DDL) | 临时表 | 创建质量临时表 |
| queryTotalQuality | query_total_quality | SELECT | 临时表 | 查询全国闭环项目质量 |
| queryOfficeQuality | query_office_quality | SELECT | 临时表 | 查询各办事处质量 |
| queryOtherOfficeQuality | query_other_office_quality | SELECT | 临时表 | 查询无闭环项目的办事处 |
| queryCloseMap | query_close_project_size | SELECT | pm_project_header | 查询闭环项目数量 |
| queryNewMap | query_new_project_size | SELECT | pm_project_header | 查询新增项目数量 |
| createImplTmptable | create_implway_tmp_table | UPDATE(DDL) | 临时表 | 创建实施方式临时表 |
| queryImplWayMap | query_implway_size | SELECT | 临时表 | 查询实施方式占比 |
| queryLineData | query_line_data | SELECT | pm_report_line_data | 查询趋势图数据 |
| queryTotalNum | query_totalNum | SELECT | pm_project_header | 查询全国项目数量 |
| queryEngineeringTypeNum | query_engineeringTypeNum | SELECT | pm_project_header | 查询工程类项目数量 |
| queryCommonTypeNum | query_commonTypeNum | SELECT | pm_project_header | 查询普通类项目数量 |
| queryAssignedNum | query_assignedNum | SELECT | pm_project_header | 查询指派项目经理数量 |
| queryTraceNum | query_traceNum | SELECT | pm_project_header | 查询在跟踪项目数量 |
| queryReportLineAssignedData | query_reportline_assigned_info | SELECT | pm_report_line_data | 统计指派率数据 |
| insertReportLineDataByList | insert_reportline_data_bylist | INSERT | pm_report_line_data | 批量插入报表数据 |
| queryReportLineTraceData | query_reportline_trace_info | SELECT | pm_report_line_data | 统计跟踪率数据 |
| queryReportLineClosedData | query_reportline_closed_info | SELECT | pm_report_line_data | 统计闭环新增比数据 |
| queryReportLineQualityData | query_reportline_quality_info | SELECT | pm_report_line_data | 统计闭环质量数据 |
| queryReportLineNoQualityData | query_reportline_no_quality_info | SELECT | pm_report_line_data | 统计无闭环项目办事处 |
| deleteQualityTmpTable | delete_quality_tmp_table | UPDATE(DDL) | 临时表 | 删除质量临时表 |
| queryLineQualityData | query_line_quality_data | SELECT | pm_report_line_data | 查询质量趋势图数据 |
| queryReportLineImplData | query_reportline_impl_info | SELECT | pm_report_line_data | 统计实施方式占比 |
| queryReportLineImplWayData | query_line_implway_data | SELECT | pm_report_line_data | 查询实施方式趋势 |
| queryReportSettingTimes | query_report_impl_settingTime | SELECT | pm_report_line_data | 查询报表设置时间 |
| queryProjectSummaryStatus | queryProjectSummaryStatus | SELECT | pm_project_header | 查询项目各种状态 |

---

## 十六、WarrantyCallbackDao（质保回访）

**接口路径**: `com.dp.plat.warrantyCallback.dao.WarrantyCallbackDao`  
**SQL映射文件**: `sql-map-warrantyCallback-config.xml`

| DAO方法 | SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|---|
| selectProjectWarrantyCallbackById | selectProjectWarrantyCallbackById | SELECT | pm_project_warranty_callback（文档原写pm_wc_callback） | 根据ID查询质保回访 |
| selectProjectWarrantyCallbackVOById | selectProjectWarrantyCallbackVOById | SELECT | pm_project_warranty_callback（文档原写pm_wc_callback） | 查询质保回访VO |
| selectProjectWarrantyCallbackList | selectProjectWarrantyCallbackList | SELECT | pm_project_warranty_callback（文档原写pm_wc_callback） | 查询质保回访列表 |
| selectProjectWarrantyCallbackVOList | selectProjectWarrantyCallbackVOList | SELECT | pm_project_warranty_callback（文档原写pm_wc_callback） | 查询质保回访VO列表 |
| queryProjectList | queryProjectList | SELECT | pm_project_header | 查询项目列表 |
| insertProjectWarrantyCallback | insertProjectWarrantyCallback | INSERT | pm_project_warranty_callback（文档原写pm_wc_callback） | 插入质保回访 |
| insertProjectWarrantyCallbackSelective | insertProjectWarrantyCallbackSelective | INSERT | pm_project_warranty_callback（文档原写pm_wc_callback） | 选择性插入质保回访 |
| updateProjectWarrantyCallbackByIdSelective | updateProjectWarrantyCallbackByIdSelective | UPDATE | pm_project_warranty_callback（文档原写pm_wc_callback） | 选择性更新质保回访 |
| queryCallBackQuesnaireVersion | queryCallBackQuesnaireVersion | SELECT | pm_project_warranty_callback（文档原写pm_wc_callback） | 查询回访问卷版本 |
| queryWarrantyCallbackInfoForProject | queryWarrantyCallbackInfoForProject | SELECT | pm_project_warranty_callback（文档原写pm_wc_callback） | 查询项目质保回访信息 |
| selectRejectedProjectWarrantyCallbackList | selectRejectedProjectWarrantyCallbackList | SELECT | pm_project_warranty_callback（文档原写pm_wc_callback） | 查询被驳回的质保回访 |
| queryWarrantyCallbackExportData | queryWarrantyCallbackExportData | SELECT | pm_project_warranty_callback（文档原写pm_wc_callback） | 查询质保回访导出数据 |
| updateWarrantyCallbackEvaluationHeader | update-evaluation_header-obj | UPDATE | pm_cl_evaluation_header | 更新质保回访评价 |
| insertWarrantyCallbackEvaluationHeader | insert-evaluation_header-obj | INSERT | pm_cl_evaluation_header | 插入质保回访评价 |
| selectProjectWarrantyCallbackVOListPageable | selectProjectWarrantyCallbackVOListPageable | SELECT | pm_project_warranty_callback（文档原写pm_wc_callback） | 分页查询质保回访 |
| countProjectWarrantyCallbackVOListPageable | countProjectWarrantyCallbackVOListPageable | SELECT | pm_project_warranty_callback（文档原写pm_wc_callback） | 统计分页数量 |
| queryWarrantyCallbackTaskList | queryWarrantyCallbackTaskList | SELECT | act_ru_task | 查询质保回访任务 |
| selectProjectWarrantyCallbackMapList | selectProjectWarrantyCallbackMapList | SELECT | pm_project_warranty_callback（文档原写pm_wc_callback） | 查询质保回访Map列表 |
| selectProjectWarranty | selectProjectWarranty | SELECT | pm_project_warranty_callback（文档原写pm_wc_callback） | 查询项目质保 |
| selectCustomerProjectWarrantyCallbackStatistics | selectCustomerProjectWarrantyCallbackStatistics | SELECT | pm_project_warranty_callback（文档原写pm_wc_callback） | 查询客户质保回访统计 |

---

## 十七、WorkSpaceDao（工作台/待办）

**接口路径**: `com.dp.plat.dao.WorkSpaceDao`  
**SQL映射文件**: `sql-map-work-config.xml`

| DAO方法 | SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|---|
| 查询待办任务 | query_pm_task_list | SELECT | 多表关联 | 查询待办任务列表 |
| 查询待办数量 | query_pm_task_count | SELECT | 多表关联 | 查询待办任务数量 |
| 查询通知列表 | query_notify_list | SELECT | pm_project_notification（文档原写pm_notification） | 查询通知列表 |
| 查询通知数量 | query_notify_count | SELECT | pm_project_notification（文档原写pm_notification） | 查询通知数量 |
| 查询回退任务 | query_project_back_task_list | SELECT | 多表关联 | 查询回退任务 |
| 查询跟踪任务 | query_project_track_task_list | SELECT | 多表关联 | 查询跟踪任务 |
| 查询回访任务 | query_call_back_task | SELECT | 多表关联 | 查询回访任务 |
| 查询售前任务 | query_presales_task | SELECT | 多表关联 | 查询售前任务 |
| 查询历史任务 | query_callback_his_list | SELECT | 多表关联 | 查询历史任务 |
| 查询技术公告任务 | query_probTask_list | SELECT | 多表关联 | 查询技术公告任务 |
| 查询督导任务 | queryProjectSupervisionTask | SELECT | 多表关联 | 查询督导任务 |
| 查询自身历史任务 | querySelfHistoryTaskList | SELECT | 多表关联 | 查询自身历史任务 |

---

## 十八、数据同步SQL映射（无直接DAO）

以下SQL映射文件由数据同步服务直接调用，无对应DAO接口：

### 18.1 SAP数据同步（sql-map-refresh-data-sap-config.xml）

| SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|
| query_DP_V_SO_ORDER_4_PMS | SELECT | dp_reports.dp_v_so_order_4_pms | 查询SAP销售订单 |
| query_DP_V_RMA_ORDER_4_PMS | SELECT | dp_reports.dp_v_rma_order_4_pms | 查询SAP退货订单 |
| insert_pm_order_data | INSERT | pm_order_data_from_erp_sap（文档原写pm_order_data） | 插入订单数据 |
| delete_pm_order_data | DELETE | pm_order_data_from_erp_sap（文档原写pm_order_data） | 删除订单数据 |
| query_DP_V_SO_LINE_4_PMS | SELECT | dp_reports.dp_v_so_line_4_pms | 查询SAP订单行 |
| query_DP_V_RMA_LINE_4_PMS | SELECT | dp_reports.dp_v_rma_line_4_pms | 查询SAP退货订单行 |
| delete_pm_order_line | DELETE | pm_order_line_from_erp_sap（文档原写pm_order_line） | 删除订单行 |
| insert_pm_order_line | INSERT | pm_order_line_from_erp_sap（文档原写pm_order_line） | 插入订单行 |
| query_lend_delivery_off_list | SELECT | dp_reports | 查询借货发货清单 |
| insert_pm_presales_lend_2_delivery_off_from_sap | INSERT | pm_presales_lend_2_delivery_off_from_sap | 插入借货发货清单 |
| selectOrderInfoFromSAP | SELECT | dp_reports | 查询SAP订单信息 |
| insertOrderInfoFromSAP | INSERT | pm_order_data_from_erp_sap（文档原写pm_order_data） | 插入SAP订单信息 |
| deleteOrderInfoFromSAP | DELETE | pm_order_data_from_erp_sap（文档原写pm_order_data） | 删除SAP订单信息 |
| selectOrderLineFromSAP | SELECT | dp_reports | 查询SAP订单行信息 |
| insertOrderLineFromSAP | INSERT | pm_order_line_from_erp_sap（文档原写pm_order_line） | 插入SAP订单行 |
| deleteOrderLineFromSAP | DELETE | pm_order_line_from_erp_sap（文档原写pm_order_line） | 删除SAP订单行 |

### 18.2 D365数据同步（sql-map-refresh-data-d365-config.xml）

| SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|
| selectOrderInfoFromD365 | SELECT | dp_reports | 查询D365订单信息 |
| insertOrderInfoFromD365 | INSERT | pm_order_data_from_erp_d365（文档原写pm_order_data） | 插入D365订单信息 |
| deleteOrderInfoFromD365 | DELETE | pm_order_data_from_erp_d365（文档原写pm_order_data） | 删除D365订单信息 |
| selectOrderLineFromD365 | SELECT | dp_reports | 查询D365订单行 |
| insertOrderLineFromD365 | INSERT | pm_order_line_from_erp_d365（文档原写pm_order_line） | 插入D365订单行 |
| deleteOrderLineFromD365 | DELETE | pm_order_line_from_erp_d365（文档原写pm_order_line） | 删除D365订单行 |

### 18.3 SMS数据同步（sql-map-refresh-data-sms-config.xml）

| SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|
| query_v_soleagent_lend_4_pms | SELECT | dp_reports | 查询总代借货信息 |
| delete_pm_project_soleagent_lend_from_sms | DELETE | pm_project_soleagent_lend_from_sms | 删除总代借货 |
| insert_pm_project_soleagent_lend_from_sms | INSERT | pm_project_soleagent_lend_from_sms | 插入总代借货 |
| query_v_prj_property_4_pm | SELECT | dp_reports | 查询项目属性 |
| delete_pm_project_property_from_sms | DELETE | pm_project_property_from_sms | 删除项目属性 |
| insert_pm_project_property_from_sms | INSERT | pm_project_property_from_sms | 插入项目属性 |
| query_lend_info_list | SELECT | dp_reports | 查询借货信息 |
| query_lend_product_list | SELECT | dp_reports | 查询借货产品 |
| query_lend_order_list | SELECT | dp_reports | 查询借货订单 |
| query_lend_2_sale_list | SELECT | dp_reports | 查询借货转销售 |
| query_lend_2_rma_list | SELECT | dp_reports | 查询借货退货 |
| delete_pm_presales_lend_info_from_sms | DELETE | pm_presales_lend_info_from_sms | 删除借货信息 |
| insert_pm_presales_lend_info_from_sms | INSERT | pm_presales_lend_info_from_sms | 插入借货信息 |
| query_view_refer_product | SELECT | dp_reports | 查询实际发货产品 |
| delete_pm_project_real_product_line_from_sms | DELETE | pm_project_product_line_real | 删除实际发货产品 |
| insert_pm_project_real_product_line_from_sms | INSERT | pm_project_product_line_real | 插入实际发货产品 |
| query_view_market_system_expend_industry | SELECT | dp_reports | 查询市场关系 |
| delete_pm_project_market_relations_from_sms | DELETE | pm_project_market_relations | 删除市场关系 |
| insert_pm_project_market_relations_from_sms | INSERT | pm_project_market_relations | 插入市场关系 |

### 18.4 CRM数据同步（sql-map-refresh-data-crm-config.xml）

| SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|
| selectProjectSoleagentLendFormCRM | SELECT | dp_reports | 查询CRM总代借货 |
| deleteProjectSoleagentLendFormCRM | DELETE | pm_project_soleagent_lend_from_sms | 删除CRM总代借货 |
| insertProjectSoleagentLendFormCRM | INSERT | pm_project_soleagent_lend_from_sms | 插入CRM总代借货 |
| selectProjectPropertyFormCRM | SELECT | dp_reports | 查询CRM项目属性 |
| deleteProjectPropertyFormCRM | DELETE | pm_project_property_from_sms | 删除CRM项目属性 |
| insertProjectPropertyFormCRM | INSERT | pm_project_property_from_sms | 插入CRM项目属性 |
| selectContractCollectionPlanFromCRM | SELECT | dp_reports | 查询CRM回款计划 |
| deleteContractCollectionPlanFromCRM | DELETE | pm_contract_collection_plan | 删除CRM回款计划 |
| insertContractCollectionPlanFromCRM | INSERT | pm_contract_collection_plan | 插入CRM回款计划 |
| selectProductInfoFromCRM | SELECT | dp_reports | 查询CRM产品信息 |
| deleteProductInfoFromCRM | DELETE | pm_product_info | 删除CRM产品信息 |
| insertProductInfoFromCRM | INSERT | pm_product_info | 插入CRM产品信息 |
| selectProjectLeaseLineFromCRM | SELECT | dp_reports | 查询CRM租赁配置 |
| deleteProjectLeaseLineFromCRM | DELETE | pm_project_lease_line | 删除CRM租赁配置 |
| insertProjectLeaseLineFromCRM | INSERT | pm_project_lease_line | 插入CRM租赁配置 |
| selectProjectProductConfigLevelInfoFromCRM | SELECT | dp_reports | 查询CRM配置关系 |
| deleteProjectProductConfigLevelInfoFromCRM | DELETE | pm_project_product_config_level | 删除CRM配置关系 |
| insertProjectProductConfigLevelInfoFromCRM | INSERT | pm_project_product_config_level | 插入CRM配置关系 |

### 18.5 OA数据同步（sql-map-refresh-data-oa-config.xml）

| SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|
| selectPresalesInfoFormOA | SELECT | dp_reports | 查询OA售前信息 |
| insertPresalesInfoFormOA | INSERT | pm_presales_lend_detail_from_oa | 插入OA售前信息 |
| deletePresalesInfoFormOA | DELETE | pm_presales_lend_detail_from_oa | 删除OA售前信息 |
| selectPresalesDetailFormOA | SELECT | dp_reports | 查询OA售前明细 |
| insertPresalesDetailFormOA | INSERT | pm_presales_lend_detail_from_oa | 插入OA售前明细 |
| deletePresalesDetailFormOA | DELETE | pm_presales_lend_detail_from_oa | 删除OA售前明细 |
| insertPresalesHeaderFormOA | INSERT | pm_presales_project_header | 插入OA售前项目头 |

### 18.6 通用数据同步（sql-map-refresh-data-common-config.xml）

| SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|
| test | SELECT | 1 | 测试连接 |
| query_pm_project_product_line | SELECT | pm_project_product_line | 查询产品线 |
| delete_2_pm_project_product_line | DELETE | pm_project_product_line | 删除产品线 |
| insert_2_pm_project_product_line | INSERT | pm_project_product_line | 插入产品线 |
| createTempNeedUpdateProject | INSERT(DDL) | 临时表 | 创建需更新项目临时表 |
| deleteOldProductLines | DELETE | pm_project_product_line | 删除旧产品线 |
| updateProductLineId | UPDATE | pm_project_product_line | 更新产品线ID |
| resetProductLineAutoId | INSERT(DDL) | pm_project_product_line | 重置产品线自增ID |
| insertNewProductLines | INSERT | pm_project_product_line | 插入新产品线 |
| dropTempNeedUpdateProject | DELETE(DDL) | 临时表 | 删除临时表 |
| insert_fnd_data_refresh_log | INSERT | fnd_data_refresh_log | 插入数据刷新日志 |
| update_fnd_data_refresh_log_success | UPDATE | fnd_data_refresh_log | 更新刷新日志成功 |
| update_fnd_data_refresh_log_fail | UPDATE | fnd_data_refresh_log | 更新刷新日志失败 |
| update_fnd_data_refresh_log | UPDATE | fnd_data_refresh_log | 更新刷新日志 |
| query_view_person_info_4_pms | SELECT | dp_reports | 查询人员信息 |
| update_project_member | UPDATE | pm_project_member | 更新项目成员 |
| update_pm_salesmember_info | INSERT | pm_project_member | 更新销售成员信息 |
| delete_pm_person_from_oa | DELETE | pm_person_from_oa | 删除OA人员 |
| insert_pm_person_from_oa | INSERT | pm_person_from_oa | 插入OA人员 |
| update_keshanhui_from_oa | UPDATE | pm_project_member | 更新可删回标记 |
| createInvalidPersonsTempTable | INSERT(DDL) | 临时表 | 创建无效人员临时表 |
| dropInvalidPersonsTempTable | DELETE(DDL) | 临时表 | 删除无效人员临时表 |
| invalidQuitProjectQuitSalesMan | UPDATE | pm_project_member | 失效离职人员 |
| query_waiting_mail | SELECT | fnd_mails | 查询待发送邮件 |
| update_waiting_mail | UPDATE | fnd_mails | 更新待发送邮件 |
| describe_table_field | SELECT | INFORMATION_SCHEMA | 检查表字段 |
| add_field | UPDATE(DDL) | 动态表 | 添加字段 |
| resetTableAutoId | INSERT(DDL) | 动态表 | 重置表自增ID |
| create_shipment_state_tmp | UPDATE(DDL) | 临时表 | 创建发货状态临时表 |
| update_shipment_state | UPDATE | pm_project_header | 更新发货状态 |
| create_pm_presales_project_rma_info | INSERT(DDL) | pm_presales_project_rma_info | 创建RMA信息表 |
| drop_pm_presales_project_rma_info | DELETE(DDL) | pm_presales_project_rma_info | 删除RMA信息表 |
| updatePresalesTransferState | UPDATE | pm_presales_project_header | 更新售前转移状态 |
| updatePresalesRMAState | UPDATE | pm_presales_project_header | 更新售前RMA状态 |
| query_lend_info_ids | SELECT | pm_presales_project_header | 查询借货信息ID |
| insert_pm_presales_header | INSERT | pm_presales_project_header | 插入售前项目头 |
| insert_pm_presales_product | INSERT | pm_presales_project_product_line | 插入售前产品 |
| delete_pm_project_product_line_real | DELETE | pm_project_product_line_real | 删除实际产品线 |
| insert_pm_project_product_line_real | INSERT | pm_project_product_line_real | 插入实际产品线 |
| query_pm_self_service_project | SELECT | pm_project_header | 查询自助服务项目 |
| query_project_serviceManager_by_officeCode | SELECT | pm_project_member | 查询办事处服务经理 |
| create_temporary_serviceType_and_channelName_table | INSERT(DDL) | 临时表 | 创建服务类型渠道临时表 |
| update_project_serviceType | UPDATE | pm_project_header | 更新项目服务类型 |
| update_project_channelName | UPDATE | pm_project_header | 更新项目渠道名称 |
| update_project_compId | UPDATE | pm_project_header | 更新项目公司ID |
| update_project_customProjectName | UPDATE | pm_project_header | 更新项目自定义名称 |
| invalid_project_invalid_sales | UPDATE | pm_project_member | 失效无效销售 |
| insert_changed_project_sales | INSERT | pm_project_member | 插入变更的销售 |
| selectOrderInfoFromERP | SELECT | dp_reports | 查询ERP订单信息 |
| insertOrderInfoFromERP | INSERT | pm_order_data_from_erp_sap（文档原写pm_order_data，实际按ERP来源分别为pm_order_data_from_erp_sap或pm_order_data_from_erp_d365） | 插入ERP订单信息 |
| deleteOrderInfoFromERP | DELETE | pm_order_data_from_erp_sap（文档原写pm_order_data，实际按ERP来源分别为pm_order_data_from_erp_sap或pm_order_data_from_erp_d365） | 删除ERP订单信息 |
| selectOrderLineFromERP | SELECT | dp_reports | 查询ERP订单行 |
| insertOrderLineFromERP | INSERT | pm_order_line_from_erp_sap（文档原写pm_order_line，实际按ERP来源分别为pm_order_line_from_erp_sap或pm_order_line_from_erp_d365） | 插入ERP订单行 |
| deleteOrderLineFromERP | DELETE | pm_order_line_from_erp_sap（文档原写pm_order_line，实际按ERP来源分别为pm_order_line_from_erp_sap或pm_order_line_from_erp_d365） | 删除ERP订单行 |
| selectContractShimpentBarcodeToCRM | SELECT | 多表关联 | 查询合同发货条码推送CRM |
| selectContractCollectionPlanToCRM | SELECT | 多表关联 | 查询合同回款计划推送CRM |
| selectSubcontractPaymentToFP | SELECT | 多表关联 | 查询转包付款推送财务 |

---

## 十九、其他DAO

### 19.1 OpLogDao（操作日志）

**接口路径**: `com.dp.plat.dao.OpLogDao`  
**SQL映射文件**: `sql-map-admin-config.xml`

| DAO方法 | SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|---|
| 插入操作日志 | insert-Operation-Log | INSERT | tb_sys_log | 插入操作日志 |
| 查询操作日志 | select-Operation-Log | SELECT | tb_sys_log | 查询操作日志（分页） |
| 查询日志总数 | select-Operation-Log-Sum | SELECT | tb_sys_log | 查询日志总数 |
| 删除日志 | delete-Log-List | DELETE | tb_sys_log | 删除日志 |

### 19.2 PmClosedLoopDao（闭环管理）

**接口路径**: `com.dp.plat.dao.PmClosedLoopDao`
**SQL映射文件**: `sql-map-project-config.xml`

> ⚠️ 文档修订说明（2026-06-30 第六阶段）：原版本错误地将 PmClosedLoopQuesnaireDao 的 6 个问卷模板方法（select-quesnaire_template_header-list / insert_questionnaire_template_header_oneObj / insert-quesnaire_template_line-obj / insert-quesnaire_template_options-list / query-quesnaire_template_line-list / query-quesnaire_template_options-list）归类于本 DAO。经源码核验（PmClosedLoopDaoImpl.java），这些方法实际属于 PmClosedLoopQuesnaireDao（见 19.5 节）。本节现按 PmClosedLoopDao 接口真实方法重写，并补充 6 个原遗漏的方法（queryEvaluationHeaderMap / queryIsCallBack / updateEvaluationHeaderId / updateEvaluationHeaderNextAcceptPerson 及 2 个临时表 DDL）。

| DAO方法 | SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|---|
| addPmClEvaluationHeaderObj | insert-evaluation_header-obj | INSERT | pm_cl_evaluation_header | 插入评价头 |
| queryEvaluationHeaderList | select-evaluation_header-list | SELECT | pm_cl_evaluation_header | 查询评价列表 |
| queryEvaluationHeaderMap | select-evaluation_header-maxDateMap | SELECT | pm_cl_evaluation_header | 查询最新评价 Map（以 projectCode 为 key，id 为 value） |
| queryEvaluationHeaderObjMap | select-evaluation_header-objMap | SELECT | pm_cl_evaluation_header | 查询评价详情 Map（以 projectCode 为 key），实际通过临时表优化查询 |
| queryEvaluationHeaderObjMapUserTempTable | create_temp_final_customer_table + select-evaluation_header-objMap + drop_temp_final_customer_table | DDL+SELECT+DDL | pm_cl_evaluation_header + 临时表 | 临时表优化版（2018-07-12 因 mysql 5.17 以下引擎查询慢而引入），先创建最终客户临时表，再查询，最后删除临时表 |
| addPmClQuesResultHeader | insert-quesnaire_result_header-obj | INSERT | pm_cl_quesnaire_result_header | 插入问卷结果头（自动填充 createdPerson） |
| addPmClQuesResultLineList(int, int) | select-quesnaire_result_header-list + insert-quesnaire_result_line-obj | SELECT+INSERT | pm_cl_quesnaire_result_header + pm_cl_quesnaire_result_line | 插入问卷结果行列表（按 headerId 先查 header，再委托重载方法插入） |
| addPmClQuesResultLineList(List, PmClQuesnaireResultHeader) | insert-quesnaire_result_line-obj | INSERT | pm_cl_quesnaire_result_line | 插入问卷结果行列表（按 header 对象） |
| deleteEvaluationHeader | delete-evaluation_header | DELETE | pm_cl_evaluation_header | 删除评价头 |
| deletePmClQuesResultHeader | delete-quesnaire_result_header | DELETE | pm_cl_quesnaire_result_header | 删除问卷结果头 |
| deletePmClQuesResultLine | delete-quesnaire_result_line | DELETE | pm_cl_quesnaire_result_line | 删除问卷结果行 |
| updateEvaluationHeaderObj | update-evaluation_header-obj | UPDATE | pm_cl_evaluation_header | 更新评价头 |
| queryPmClQuesResultHeaderList | select-quesnaire_result_header-list | SELECT | pm_cl_quesnaire_result_header | 查询问卷结果头列表 |
| queryPmClQuesResultLineList | select-quesnaire_result_line-list | SELECT | pm_cl_quesnaire_result_line | 查询问卷结果行列表 |
| queryIsCallBack | query_is_callback | SELECT | callback_quesnaire（或同名表） | 查询项目是否进行过回访流程 |
| updateEvaluationHeaderId | update_EvaluationHeaderId_byId | UPDATE | pm_cl_quesnaire_result_header | 根据 quesnaireId 更新 evaluationHeaderId |
| updateEvaluationHeaderNextAcceptPerson | update_EvaluationHeader_NextAcceptPerson | UPDATE | pm_cl_evaluation_header | 批量更新下一审批人（按 nextAcceptPerson + projectId 条件） |

> 注：addPmClQuesResultHeader / addPmClQuesResultLineList 方法同时存在于 PmClosedLoopQuesnaireDao（19.5 节），属于跨 DAO 接口重复定义，实际实现逻辑一致（均通过 getCurrUsername() 填充 createdPerson）。

### 19.3 ProjectPlanDao（项目计划）

**接口路径**: `com.dp.plat.dao.ProjectPlanDao`  
**SQL映射文件**: `sql-map-project-config.xml`

| DAO方法 | SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|---|
| 查询项目计划 | query-projectplanlist-bycontractno | SELECT | pm_project_task | 根据合同号查询计划 |

### 19.4 DataAnalysisDao（数据分析）

**接口路径**: `com.dp.plat.dao.DataAnalysisDao`
**SQL映射文件**: `sql-map-project-config.xml`

| DAO方法 | SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|---|
| 查询表结构 | query_tableStructure | SELECT | INFORMATION_SCHEMA | 查询表结构信息 |

### 19.5 PmClosedLoopQuesnaireDao（闭环问卷模板管理）

**接口路径**: `com.dp.plat.dao.PmClosedLoopQuesnaireDao`
**SQL映射文件**: `sql-map-project-config.xml`

> 新增章节（2026-06-30 第六阶段）。原版本错误地将本 DAO 的 6 个问卷模板方法归类于 PmClosedLoopDao（19.2 节）。经源码核验（PmClosedLoopQuesnaireDaoImpl.java），现独立成节。本 DAO 专管闭环问卷**模板**（template）的 CRUD；PmClosedLoopDao 专管闭环**评价/结果**（evaluation/result）的 CRUD。两者通过 `addPmClQuesResultHeader` / `addPmClQuesResultLineList` 存在接口重复定义（实现一致）。

| DAO方法 | SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|---|
| insertQuesnaireHeader | insert_questionnaire_template_header_oneObj | INSERT | pm_cl_quesnaire_template_header | 插入问卷模板头 |
| selectQuesnaireHeaderList | select-quesnaire_template_header-list | SELECT | pm_cl_quesnaire_template_header | 查询问卷模板列表 |
| insertQuesnaireLineList | insert-quesnaire_template_line-obj | INSERT | pm_cl_quesnaire_template_line | 插入问卷模板行（问卷问题） |
| insertQuesnaireOptList | insert-quesnaire_template_options-list | INSERT | pm_cl_quesnaire_template_options | 批量插入问卷选项（参数含 questionId + List） |
| queryPmClQuesnaireLineList | query-quesnaire_template_line-list | SELECT | pm_cl_quesnaire_template_line | 查询问卷模板行（支持 sqlType 排序参数，默认 desc） |
| queryPmClQuesnaireOptList | query-quesnaire_template_options-list | SELECT | pm_cl_quesnaire_template_options | 查询问卷选项（支持 sqlType 排序参数，默认 desc） |
| queryPmClQuesnaireOptMap | query-quesnaire_template_options-list | SELECT | pm_cl_quesnaire_template_options | 查询问卷选项 Map（queryForMap，以 id 为 key，sqlType 固定 "desc"） |
| updateQuesHeader | update-quesnaire_template_header | UPDATE | pm_cl_quesnaire_template_header | 修改问卷模板头信息 |
| updateQuesStatus | updateStatus-quesnaire_template_header | UPDATE | pm_cl_quesnaire_template_header | 问卷生效（校验 quesType 非空且 id > 0，否则返回 -1） |
| deleteQuesLine | delete-quesnaire_template_line | DELETE | pm_cl_quesnaire_template_line | 删除问卷模板行 |
| deleteQuesOpt | delete-quesnaire_template_options | DELETE | pm_cl_quesnaire_template_options | 删除问卷模板选项 |
| updateLineQuesnum | update-quesnaire_template_header-questionNum | UPDATE | pm_cl_quesnaire_template_line | 删除问卷行后更新行信息的题号 |
| deleteQuesHeader | delete-quesnaire_template_header | DELETE | pm_cl_quesnaire_template_header | 删除问卷模板头 |
| deleteLineAll | delete-quesnaire_template_line-all | DELETE | pm_cl_quesnaire_template_line | 删除问卷下的全部行信息（按 quesnaireTemplateHeaderId） |
| deleteOptAll | delete-quesnaire_template_options-all | DELETE | pm_cl_quesnaire_template_options | 删除问卷下的全部选项信息（按 quesnaireTemplateHeaderId） |
| addPmClQuesResultHeader | insert-quesnaire_result_header-obj | INSERT | pm_cl_quesnaire_result_header | 插入问卷结果头（与 PmClosedLoopDao 同名方法重复定义，实现一致） |
| addPmClQuesResultLineList(List, int) | select-quesnaire_result_header-list + insert-quesnaire_result_line-obj | SELECT+INSERT | pm_cl_quesnaire_result_header + pm_cl_quesnaire_result_line | 插入问卷结果行列表（按 headerId） |
| addPmClQuesResultLineList(List, PmClQuesnaireResultHeader) | insert-quesnaire_result_line-obj | INSERT | pm_cl_quesnaire_result_line | 插入问卷结果行列表（按 header 对象） |

### 19.6 ProjectWarrantyCallbackDao（项目质保回访）

**接口路径**: `com.dp.plat.warrantyCallback.dao.ProjectWarrantyCallbackDao`
**SQL映射文件**: `sql-map-warrantyCallback-config.xml`
**namespace**: `pm_project_warranty_callback`
**对应表**: `pm_project_warranty_callback`

> 新增章节（2026-06-30 第六阶段）。原版本仅文档化了同包的 WarrantyCallbackDao（第十六章），遗漏了 ProjectWarrantyCallbackDao。本 DAO 专注于项目维度的质保回访记录管理，提供 6 个标准 CRUD 方法。

| DAO方法 | SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|---|
| deleteProjectWarrantyCallbackById | deleteProjectWarrantyCallbackById | DELETE | pm_project_warranty_callback | 按主键删除项目质保回访记录 |
| insertProjectWarrantyCallback | insertProjectWarrantyCallback | INSERT | pm_project_warranty_callback | 插入项目质保回访记录（返回自增主键） |
| insertProjectWarrantyCallbackSelective | insertProjectWarrantyCallbackSelective | INSERT | pm_project_warranty_callback | 选择性插入（仅非空字段） |
| selectProjectWarrantyCallbackById | ⚠️ 见下方说明 | SELECT | pm_project_warranty_callback | 按主键查询项目质保回访记录 |
| updateProjectWarrantyCallbackByIdSelective | updateProjectWarrantyCallbackByIdSelective | UPDATE | pm_project_warranty_callback | 选择性更新（仅非空字段） |
| updateProjectWarrantyCallbackById | updateProjectWarrantyCallbackById | UPDATE | pm_project_warranty_callback | 全字段更新 |

> ⚠️ **源码 Bug**（`ProjectWarrantyCallbackDaoImpl.java:28`）：`selectProjectWarrantyCallbackById` 方法实现中调用 `getSqlMapClientTemplate().queryForObject("selectById", _key)`，但实际 SQL 映射文件 `sql-map-warrantyCallback-config.xml` 中**不存在** ID 为 `"selectById"` 的 statement，仅有 `"selectProjectWarrantyCallbackById"`。运行时会抛出 iBATIS 异常 `There is no statement named selectById in this SqlMap`。建议源码修正：将 `"selectById"` 改为 `"selectProjectWarrantyCallbackById"`。本次审查仅记录此 Bug，不修改源码。

### 19.7 CertificateDao（合格证/印章管理）

**接口路径**: `com.dp.plat.plus.certificate.dao.CertificateDao`
**SQL映射文件**: `sql-map-certificate-config.xml`
**namespace**: `business`
**对应表**: `mes_oqc_info`（OQC 检验信息）、`mes_seal_info`（印章信息）

> 新增章节（2026-06-30 第六阶段）。原版本遗漏了 plus 包下的 CertificateDao。本 DAO 提供 OQC 检验员信息查询和印章登记管理功能。

| DAO方法 | SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|---|
| queryOQCInfo | queryOQCInfo | SELECT | mes_oqc_info LEFT JOIN mes_seal_info | 根据条码查询 OQC 检验员及检验时间（LEFT JOIN 关联印章信息，条件包含时间范围匹配且 info 以 "QC PASS" 开头） |
| insertSealInfo(List<HashMap>) | insertSealInfo | INSERT | mes_seal_info | 批量插入印章登记记录（@Deprecated，因 list<map> 无法批量插入，已弃用） |
| insertSealInfo(HashMap) | insertSealInfo | INSERT | mes_seal_info | 插入单条印章登记记录（使用 parameterMap sealInfo，含 id/name/info/description/takeTime/backTime/remark/uploadBy 等字段，末尾追加 now()） |
| deleteSealInfo | truncateSealInfo | DELETE | mes_seal_info | 清空印章登记表（实际 SQL 为 `delete from mes_seal_info`，非 TRUNCATE） |

> 注：本 DAO 的 SQL 涉及 MES 数据源（`mes_oqc_info`、`mes_seal_info`），属于跨库查询场景。

---

## 二十、维保管理SQL映射（sql-map-maintenance-config.xml）

维保管理相关SQL由 ProjectDao 中的维保方法调用：

| SQL ID | SQL类型 | 操作表 | 说明 |
|---|---|---|---|
| insertProjectMaintenance | INSERT | pm_project_maintenance | 插入维保记录 |
| insertProjectMaintenanceSelective | INSERT | pm_project_maintenance | 选择性插入维保 |
| selectProjectMaintenanceById | SELECT | pm_project_maintenance | 查询维保详情 |
| selectProjectMaintenanceList | SELECT | pm_project_maintenance | 查询维保列表 |
| countProjectMaintenanceList | SELECT | pm_project_maintenance | 统计维保数量 |
| insertOrUpdateProjectMaintenance | INSERT | pm_project_maintenance | 插入或更新维保 |
| selectSingleProjectMaintenanceMaxId | SELECT | pm_project_maintenance | 查询最大维保ID |
| ProjectMaintenanceQuesnaireResultColumns | SELECT | 动态表 | 查询维保问卷结果列 |
| selectProjectMaintenanceVOList | SELECT | pm_project_maintenance | 查询维保VO列表 |
| selectProjectMaintenanceMapList | SELECT | pm_project_maintenance | 查询维保Map列表 |
| selectDailyMaintenanceMapList | SELECT | pm_project_maintenance | 查询日维护记录 |
| selectDailyMaintenanceUsers | SELECT | pm_project_maintenance | 查询日维护人员 |
| queryProjectWarrantyState | SELECT | 多表关联 | 查询项目维保状态 |
| queryProjectMaintenanceDeliverCount | SELECT | pm_project_service_delivery | 查询维保交付数量 |
| queryProjectMaintenanceDeliverCountByProjectDeliver | SELECT | pm_project_service_delivery | 查询指定类型交付件数量 |
| queryProjectMaintenanceServiceDeliveriedByProjectDeliver | SELECT | pm_project_service_delivery | 查询交付件是否上传完毕 |
| queryProjectMaintenanceServiceDeliveriedByMap | SELECT | pm_project_service_delivery | 按Map查询交付状态 |
| insertProjectServiceDeliveryBySelective | INSERT | pm_project_service_delivery | 插入服务交付记录 |
| selectProjectMaintenanceServiceDeliveryMapList | SELECT | pm_project_service_delivery | 查询服务交付列表 |

---

> **说明**：
> 1. 部分DAO方法在实现中调用多个SQL ID（如分页查询同时调用count和list），已在表格中用 `/` 分隔标注。
> 2. 数据同步相关SQL映射文件无直接DAO接口，由定时任务服务直接调用 `SqlMapClientTemplate`。
> 3. `UPDATE(DDL)` 和 `INSERT(DDL)` 类型表示该SQL实际执行的是DDL操作（创建/删除临时表），在iBATIS中使用update/insert标签。
> 4. ProbDao 位于 `com.dp.plat.prob.dao` 子包而非 `com.dp.plat.dao`。
> 5. SubcontractDao 位于 `com.dp.plat.subcontract.dao` 子包。
> 6. WarrantyCallbackDao 位于 `com.dp.plat.warrantyCallback.dao` 子包。
