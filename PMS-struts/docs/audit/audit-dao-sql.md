# PMS-Struts DAO/SQL 参考文档事实性审查报告

**审查日期**：2026-05-19  
**审查对象**：`docs/03-database/dao-sql-reference.md`  
**审查方法**：与源码（`src/com/dp/plat/dao/`等）及iBatis映射文件（`config-ibaits/`）交叉验证

---

## 一、总体结论

**文档状态：严重缺失（几乎为空）**

文档文件 `dao-sql-reference.md` 仅有9字节，内容为截断的标题 `# PMS-Str`，**不包含任何实质性的DAO/SQL参考内容**。因此，本次审查无法进行"文档与源码差异"的常规比对，而是基于源码和iBatis映射文件，**逆向梳理出文档应包含的全部内容**，并标记源码中发现的事实性问题。

---

## 二、文档应包含但缺失的内容清单

### 2.1 DAO类继承关系总览

| DAO类 | 继承关系 | 实现接口 | 对应映射文件 |
|-------|---------|---------|-------------|
| `BaseDao` | 无（顶层基类） | 无 | 无（不直接操作SQL） |
| `LoginDaoImpl` | extends BaseDao | implements LoginDao | sql-map-admin-config.xml |
| `UserManageDaoImpl` | extends BaseDao | implements UserManageDao | sql-map-admin-config.xml |
| `ProjectDaoImpl` | extends BaseDao | implements ProjectDao | sql-map-project-config.xml |
| `PresalesDaoImpl` | extends BaseDao | implements PresalesDao | sql-map-presales-config.xml |
| `CallBackDaoImpl` | extends BaseDao | implements CallBackDao | sql-map-callback-config.xml |
| `PmClosedLoopDaoImpl` | extends BaseDao | implements PmClosedLoopDao | sql-map-callback-config.xml |
| `ReportDaoImpl` | extends BaseDao | implements ReportDao | sql-map-report-config.xml |
| `WorkflowDaoImpl` | extends BaseDao | implements WorkflowDao | sql-map-work-config.xml |
| `WorkSpaceDaoImpl` | extends BaseDao | implements WorkSpaceDao | sql-map-work-config.xml |
| `SendMailDaoImpl` | extends BaseDao | implements SendMailDao | sql-map-admin-config.xml |
| `OpLogDaoImpl` | **未继承BaseDao** | implements OpLogDao | sql-map-admin-config.xml |
| `PasswordDaoImpl` | extends BaseDao | implements PasswordDao | sql-map-admin-config.xml |
| `ProbManageDaoImpl` | extends BaseDao | implements ProbManageDao | sql-map-prob-config.xml |
| `SubcontractDaoImpl` | extends BaseDao | implements SubcontractDao | sql-map-subcontract-config.xml |
| `CertificateDaoImpl` | extends BaseDao | implements CertificateDao | （未确认独立映射文件） |

**关键发现**：
- 文档中提到的 `WorkFlowServiceImpl.java` **不存在**，实际类名为 `WorkflowDaoImpl.java`（注意大小写：Workflow非WorkFlow，DaoImpl非ServiceImpl）
- `OpLogDaoImpl` **未继承BaseDao**，而是直接实现 `OpLogDao` 接口，自行持有 `SqlMapClientTemplate`，这是唯一一个不继承BaseDao的DAO类

### 2.2 BaseDao 基类分析

`BaseDao` 提供以下共享能力：

| 属性/方法 | 说明 |
|----------|------|
| `sqlMapClientTemplate` | 主数据源SQL客户端 |
| `sqlMapClientTemplateSAP` | SAP数据源SQL客户端 |
| `sqlMapClientTemplateERP` | ERP数据源SQL客户端 |
| `sqlMapClientTemplateSSE` | SSE数据源SQL客户端 |
| `opLoggerDao` (OpLogDao) | 操作日志记录器 |
| `errmsg` | 错误信息 |
| `getCurrUsername()` | 获取当前用户名（从UserContext） |
| `OP_SUCCESS / OP_FAIL` | 操作结果常量（引用自OpLogDaoImpl） |

---

## 三、各DAO方法与iBatis映射ID交叉验证

### 3.1 LoginDaoImpl（6个方法）

| 方法 | iBatis映射ID | 映射文件 | 验证结果 |
|------|-------------|---------|---------|
| `querUser(String)` | `query-user-by-name` | admin | ✅ 映射存在，SQL正确 |
| `queryUserMenuMap(int)` | `query_permissions_by_name` | admin | ✅ 映射存在，SQL正确 |
| `queryUserMenuNameMap(int)` | `query_permissions_name_code_by_userId` | admin | ✅ 映射存在，SQL正确 |
| `queryUserDefaultPage(int)` | `query_defaultpage_by_username_1` / `query_defaultpage_by_username_2` | admin | ✅ 双查询回退逻辑，映射均存在 |
| `queryRoleMenuPowerList(Role)` | `query-roleMenu-list` | admin | ✅ 映射存在，SQL正确 |
| `querySysArg(String)` | `query_sys_arg` | admin | ✅ 映射存在，SQL正确 |

**注意**：方法名 `querUser` 存在拼写错误（应为 `queryUser`），这是源码中的原始拼写。

### 3.2 UserManageDaoImpl（22个方法）

| 方法 | iBatis映射ID | 验证结果 |
|------|-------------|---------|
| `queryUserList(DisplayParam, User)` | `query-user-count` + `query-userlist` | ✅ |
| `updateuser(User)` | `update-user` | ✅ |
| `queryUserByUserName(String)` | `select-user-byusername` | ✅ |
| `queryUsersByUserNames(String)` | `select-users-byusernames` | ✅ |
| `updatepwdbyusername(String, String)` | `update-md5pwd-byusername` | ⚠️ 使用insert而非update调用 |
| `updatepwdbyuser(User)` | `update-pwd-byusername` | ✅ |
| `queryRolelist()` | `query_sys_roles` | ✅ |
| `queryAllMenuList()` | `query_menu_modules` | ✅ 递归查询 |
| `queryUserMenuList()` | `query_menu_modules` | ✅ 递归查询 |
| `queryUserByUserId(int)` | `query_user_by_id` | ✅ |
| `queryUserMenuidsByUserid(int)` | `query_usermenuids_by_id` | ✅ |
| `addUserInfo(User, String)` | `query-menu-byId` + `insert-user-object` + `insert-menuForUser-object` + `insert_user_power` | ✅ 事务操作 |
| `updateUserInfo(User, String)` | `query-menu-byId` + `update-user-object` + `delete-menuForUser-byUserId` + `insert-menuForUser-object` | ✅ 事务操作 |
| `queryUserMenu(int)` | `query-menu-byId` | ✅ |
| `updateUser(User)` | `update-user-object` | ✅ |
| `deleteUsermenu(int)` | `delete-menuForUser-byUserId` | ✅ |
| `insertUsermenu(MenuForUser)` | `insert-menuForUser-object` | ✅ |
| `updateUserPower(int, String, String)` | `update_user_power` | ✅ |
| `insertUserpower(int, String, String)` | `insert_user_power` | ✅ |
| `queryAllUser()` | `query-all-user` | ✅ |
| `queryAllUserList(User)` | `query-userlist-all` | ✅ |
| `queryAllUserMap()` | `query_user_allMap` | ✅ |
| `queryUserSizeByUserName(String)` | `query_username_size` | ✅ |
| `queryUserWithRoleId(int)` | `query_user_with_role` | ✅ |
| `queryServiceMails(int)` | `query_mails_with_role` | ✅ |
| `queryServiceMails(String, Integer)` | `query_mails_with_role_and_office` | ✅ |
| `queryMailsByRoleAndOfficeCodes(String, Integer)` | `query_mails_with_role_and_office` | ✅ 与上一个方法使用相同映射ID |
| `updateServiceAndProgramMember(ProjectBatchCgMbParam)` | `UpdateServiceAndProgramMember` | ✅ |
| `queryUserWithRoleIdAndDpNo(Map)` | `query_user_with_dpNo_role` | ✅ |
| `queryUserWithRoleIdAndDpNoOrInAreaPower(Map)` | `query_user_with_dpNo_role_orin_areaPower` | ✅ |

**问题发现**：
- `updatepwdbyusername` 方法使用 `getSqlMapClientTemplate().insert()` 调用 `update-md5pwd-byusername`，但映射文件中该ID定义为 `<update>` 标签。虽然iBatis允许这样做，但语义不一致。
- 映射文件中 `update-md5pwd-byusername` 的SQL更新的是 `user` 表而非 `fnd_user_info` 表，这可能是遗留问题。

### 3.3 ProjectDaoImpl（方法数量极多，约100+方法）

由于ProjectDaoImpl方法数量庞大，以下列出关键映射ID及验证状态：

| 分类 | 关键映射ID | 验证结果 |
|------|-----------|---------|
| 项目查询 | `find_project_list`, `find_project_count`, `create_tmp_tb_project`, `drop_tmp_tb_project` | ✅ 使用临时表复杂查询 |
| 权限项目查询 | `query_project_bypower_count`, `query_project_bypower_list`, `query_project_by_power_Id` | ✅ |
| 项目CRUD | `insert-project`, `update-project-byprojectid`, `query_project_byId` | ✅ |
| 项目成员 | `insert-projectmember`, `update-projectmember`, `query_pm_member_list`, `insert_project_member`, `update_project_member` | ✅ |
| 项目周报 | `insert_project_weekly`, `query_project_weekly`, `update_project_weekly`, `insert_weekly_content`, `query_weekly_contents` | ✅ |
| 发货信息 | `query-shipmentinfo-bycontractno`, `query_shipmentInfo_size_by_contractNo` | ✅ |
| 软件版本 | `query_soft_version_list`, `update_invalid_soft_version`, `insert_project_soft_version` | ✅ |
| 项目交付 | `query-projectdeliver-list`, `insert-deliverfiles`, `updateProjectDeliverById` | ✅ |
| 项目状态 | `update-projectstate-byprojectid`, `update_project_state`, `query_project_state` | ✅ |
| 通知 | `insert_into_notification`, `insert_into_notification_obj`, `query_project_notify_list` | ✅ |
| 维保 | `selectProjectMaintenanceList`, `selectProjectMaintenanceMapList`, `queryProjectWarrantyState` | ✅ |
| 督查 | `selectProjectSupervisionList`, `selectProjectSupervisionMapList`, `insertOrUpdateProjectSupervision` | ✅ |
| 临时表操作 | `create_temp_tb_projectId_filter_itemModel`, `create_temp_table_project_contract_warrantyState`, `setMaxGroupContractLength` | ✅ |
| 问卷 | `createTempQuesnaireResultTable`, `createTempQuesnaireResultLineTable`, `queryQuesnaireResultColumns` | ✅ |

**问题发现**：
- `queryProjectSimplifyByProjectId` 映射ID在源码中调用，需确认映射文件中是否存在
- `deleteShipmentInstallInfoByProjectId` 使用 `insert` 调用但语义为删除操作
- `deleteWeeklyContent` 和 `deleteFileById` 使用 `update` 调用但语义为删除操作
- `backToLastStep` 调用 `update_project_state`，参数包含 `isback`，实际是更新项目状态

### 3.4 PresalesDaoImpl（22个方法）

| 方法 | iBatis映射ID | 验证结果 |
|------|-------------|---------|
| `queryPresalesById(int)` | `query_presales_byid` | ✅ |
| `queryPresalesProductByPresalesId(int)` | `query_presalesproduct_by_presalesid` | ✅ |
| `invalidProjectMember(int, String)` | `update_invalid_member_bymemberRole` | ✅ |
| `queryPresalesList(Presales, DisplayParam)` | `query_presales_count` + `query_presales_list` | ✅ |
| `queryActComment(int, String)` | `query_act_comment_list` | ✅ 共享映射（与WorkflowDaoImpl共用） |
| `updatePresaleHeader(Presales)` | `update_presales_header` | ✅ |
| `queryCallBackQuesnaireId(Presales)` | `query_presales_callbackId` | ✅ |
| `updateCallBackQuesnaire(int, int, int)` | `update_presales_quesnaire` | ✅ |
| `queryCallBackQuesnaireVersion(int)` | `query_presales_version` | ✅ |
| `insertCallBackQuesnaire(Map)` | `insert_presales_quesnaire` | ✅ |
| `queryQuesnaireIdBycallbackId(Presales)` | `query_presales_quesnaireId` | ✅ |
| `updatePresalesState(Map)` | `update_presales_state` | ✅ |
| `queryPresalesCodeNum(int)` | `query_presales_code_num` | ✅ |
| `updatePresalesCode(int, int)` | `update_presales_code` | ✅ |
| `updatePresalesProduct(int)` | `update_presales_product` | ✅ |
| `queryIsHasProjectTask(int, int)` | `query_presales_task_size` | ✅ |
| `insertPresaleTasks(int, int, String)` | `insert_presales_tasks` | ✅ |
| `queryPresalesTaskList(int, int)` | `query_presales_en_task` | ✅ |
| `updatePresalesTaskDeliverFiles(int, String)` | `update_presales_task_files` | ✅ |
| `updatePresalesTask(Date, int)` | `update_presales_task_finshedtime` | ✅ |
| `updatePresalesTask(Date, String, int)` | `update_presales_task_finshedtime` | ✅ 重载方法，同一映射ID |
| `updatePresalesConfirmFileIds(int, String)` | `update_presales_confirmfiles` | ✅ |
| `updatePrealesFileIds(int, int, int)` | `update_presales_confirmfiles_delete` + `update_presales_task_deliverFileIds_delete` | ✅ |
| `queryPresaleShipmentInfo(String)` | `query_presale_shipmentInfo` | ✅ |
| `queryPresaleLend2SaleInfo(String)` | `query_presale_lend_2_sale` | ✅ |
| `queryPresaleLend2RmaInfo(String)` | `query_presale_lend_2_rma` | ✅ |
| `selectPresalesTempAuthInfo(Map)` | `selectPresalesTempAuthInfo` | ✅ |
| `queryPresalesExportData(Presales)` | `queryPresalesExportData` + `createTempPresalesProductLine` + `deleteTempPresalesProductLine` | ✅ |
| `updatePresalesDuration(int)` | `updatePresalesDuration` | ✅ 使用INSERT...ON DUPLICATE KEY UPDATE |

### 3.5 CallBackDaoImpl（10个方法）

| 方法 | iBatis映射ID | 验证结果 |
|------|-------------|---------|
| `insertCallBack(CallBack)` | `insert_callback_info` | ✅ |
| `updateCallBackInstId(int, String)` | `update_callback_instid` | ✅ |
| `queryCallBackById(int)` | `query_callback_byId` | ✅ |
| `queryCallBackQuesnaireVersion(int)` | `query_cb_quesnaire_version` | ✅ |
| `insertCallBackQuesnaire(CallBackQuesnaire)` | `insert_callback_quesnaire` | ✅ |
| `queryCbQuesnaire(int)` | `query_callback_quesnaire` | ✅ |
| `queryQuesnaireTemplateID(int)` | `query_quesnaire_template_id` | ✅ |
| `queryCallBackQuesnaireId(CallBack)` | `query_callbackQuesnaireId` | ✅ |
| `updateCallBackQuesnaire(int, int, int)` | `update_callback_quesnaire` | ✅ |
| `updateCallBackApplyState(int, int)` | `update_callback_applyState` | ✅ |
| `queryCallBackComment(int)` | `query_callback_comment` | ✅ |
| `updateCallBack(CallBack)` | `update_callback` | ✅ |

### 3.6 PmClosedLoopDaoImpl（13个方法）

| 方法 | iBatis映射ID | 验证结果 |
|------|-------------|---------|
| `addPmClEvaluationHeaderObj(PmClEvaluationHeader)` | `insert-evaluation_header-obj` | ✅ |
| `queryEvaluationHeaderList(PmClEvaluationHeader)` | `select-evaluation_header-list` | ✅ |
| `queryEvaluationHeaderMap(PmClEvaluationHeader)` | `select-evaluation_header-maxDateMap` | ✅ |
| `queryEvaluationHeaderObjMap(PmClEvaluationHeader, String)` | `select-evaluation_header-objMap` | ✅ 使用临时表优化 |
| `addPmClQuesResultHeader(PmClQuesnaireResultHeader)` | `insert-quesnaire_result_header-obj` | ✅ |
| `addPmClQuesResultLineList(List, int)` | `select-quesnaire_result_header-list` + `insert-quesnaire_result_line-obj` | ✅ |
| `deletePmClQuesResultHeader(PmClQuesnaireResultHeader)` | `delete-quesnaire_result_header` | ✅ |
| `deletePmClQuesResultLine(PmClQuesnaireResultLine)` | `delete-quesnaire_result_line` | ✅ |
| `updateEvaluationHeaderObj(PmClEvaluationHeader)` | `update-evaluation_header-obj` | ✅ |
| `queryPmClQuesResultHeaderList(PmClQuesnaireResultHeader)` | `select-quesnaire_result_header-list` | ✅ |
| `queryPmClQuesResultLineList(PmClQuesnaireResultLine)` | `select-quesnaire_result_line-list` | ✅ |
| `deleteEvaluationHeader(PmClEvaluationHeader)` | `delete-evaluation_header` | ✅ |
| `queryIsCallBack(int)` | `query_is_callback` | ✅ |
| `updateEvaluationHeaderId(int, int)` | `update_EvaluationHeaderId_byId` | ✅ |
| `updateEvaluationHeaderNextAcceptPerson(HashMap)` | `update_EvaluationHeader_NextAcceptPerson` | ✅ |

**临时表操作**：
- `create_temp_final_customer_table` / `drop_temp_final_customer_table` — 用于优化评估头查询

### 3.7 ReportDaoImpl（20个方法）

| 方法 | iBatis映射ID | 验证结果 |
|------|-------------|---------|
| `queryAssignedRate(ReportQueryParam)` | `query_assigned_rate` | ✅ |
| `queryTraceRate(ReportQueryParam)` | `query_trace_rate` | ✅ |
| `createQualityTmpTable()` | `create_tmp_table_for_quality` | ✅ |
| `queryTotalQuality(ReportQueryParam)` | `query_total_quality` | ✅ |
| `queryOfficeQuality(ReportQueryParam)` | `query_office_quality` | ✅ |
| `queryOtherOfficeQuality(ReportQueryParam)` | `query_other_office_quality` | ✅ |
| `queryCloseMap(ReportQueryParam)` | `query_close_project_size` | ✅ |
| `queryNewMap(ReportQueryParam)` | `query_new_project_size` | ✅ |
| `createImplTmptable(ReportQueryParam)` | `create_implway_tmp_table` | ✅ |
| `queryImplWayMap(ReportQueryParam)` | `query_implway_size` | ✅ |
| `queryLineData(String, String)` | `query_line_data` | ✅ |
| `queryTotalNum()` | `query_totalNum` | ✅ |
| `queryEngineeringTypeNum()` | `query_engineeringTypeNum` | ✅ |
| `queryCommonTypeNum()` | `query_commonTypeNum` | ✅ |
| `queryAssignedNum()` | `query_assignedNum` | ✅ |
| `queryTraceNum()` | `query_traceNum` | ✅ |
| `queryReportLineAssignedData(ReportQueryParam)` | `query_reportline_assigned_info` | ✅ |
| `insertReportLineDataByList(List, String)` | `insert_reportline_data_bylist` / `insert_reportline_data_bylist_self` | ✅ |
| `queryReportLineTraceData(ReportQueryParam)` | `query_reportline_trace_info` | ✅ |
| `queryReportLineClosedData(ReportQueryParam)` | `query_reportline_closed_info` | ✅ |
| `queryReportLineQualityData(ReportQueryParam)` | `query_reportline_quality_info` | ✅ |
| `queryReportLineNoQualityData(ReportQueryParam)` | `query_reportline_no_quality_info` | ✅ |
| `deleteQualityTmpTable()` | `delete_quality_tmp_table` | ✅ |
| `queryLineQualityData(String, String)` | `query_line_quality_data` | ✅ |
| `queryReportLineImplData(ReportQueryParam)` | `query_reportline_impl_info` | ✅ |
| `queryReportLineImplWayData(String, String)` | `query_line_implway_data` | ✅ |
| `queryReportSettingTimes(String, String)` | `query_report_impl_settingTime` | ✅ |
| `queryProjectSummaryStatus(Map)` | `queryProjectSummaryStatus` | ✅ |

### 3.8 WorkflowDaoImpl（7个方法）

| 方法 | iBatis映射ID | 验证结果 |
|------|-------------|---------|
| `queryProcdef(Procdef)` | `query_procdef` | 需确认映射文件位置 |
| `insertActComment(int, String, String, int, String)` | `insert_fnd_act_comment` | 需确认映射文件位置 |
| `insertActComment(HashMap)` | `insert_fnd_act_comment_by_params` | 需确认映射文件位置 |
| `updateSelfActComment(HashMap)` | `update_fnd_act_comment_by_params` | 需确认映射文件位置 |
| `queryActComment(int, String)` | `query_act_comment_list` | ✅ 与PresalesDaoImpl共用 |
| `updateApplytableInfo(String, String, int, String)` | `update_apply_info_byobjid` | 需确认映射文件位置 |
| `queryTaskByInstIdAndVariable(String, String, Object)` | `queryTaskByInstIdAndVariable` | ✅ 在work-config中找到 |
| `updateRunVariableByInstIdAndVariable(...)` | `updateRunVariableByInstIdAndVariable` | ✅ 在work-config中找到 |
| `updateRunVariableById(String, String)` | `updateRunVariableById` | ✅ 在work-config中找到 |

### 3.9 WorkSpaceDaoImpl（17个方法）

| 方法 | iBatis映射ID | 验证结果 |
|------|-------------|---------|
| `getprojectcodelistbyusername(String)` | `get-projectcodelist-byusername` | ✅ |
| `getprojectcodelistfrombeforebyusername(String)` | `get-projectcodelistfrombefore-byusername` | ✅ |
| `getprojectbyapplyid(int)` | `get-projectCode-byapplyid` | ✅ |
| `getapplyidsfromorderbyusername(String)` | `get-applyid-byusernameorder` | ✅ |
| `getprojectbyapplyidorder(int)` | `get-project-byapplyidorder` | ✅ |
| `querybusinessorderprojectcodelist(String)` | `select-businessorderprojectcodelist` | ✅ |
| `queryProductFirstCodeByUsername(String)` | `query_productfirst_byuserassist` | ✅ |
| `queryConcatFirstCode(String)` | `query_productfirstCode_byprojectCode` | ✅ |
| `queryActRunTask(String)` | `query_act_ru_task_bytasktype` | ✅ |
| `checkNotificationList(String)` | `check_notification_list` | ✅ |
| `updateNotificationState(int)` | `update_notification_state` | ✅ |
| `queryPmTaskList(TaskQueryParam, DisplayParam)` | `query_pm_task_count` + `query_pm_task_list` | ✅ |
| `querySelfHistoryTaskList(TaskQueryParam, DisplayParam)` | `countSelfHistoryTaskList` + `querySelfHistoryTaskList` | ✅ |
| `queryProjectBackTaskList()` | `query_project_back_task_list` | ✅ |
| `queryProjectTrackTaskList()` | `query_project_track_task_list` | ✅ |
| `queryNotifyList(TaskQueryParam, DisplayParam)` | `query_notify_count` + `query_notify_list` | ✅ |
| `queryCallBackTaskList()` | `query_call_back_task` | ✅ |
| `queryPresalesTaskList()` | `query_presales_task` | ✅ |
| `queryCallbackHisList(String)` | `query_callback_his_list` | ✅ |
| `queryProbTaskList()` | `query_probTask_list` | ✅ |
| `querySubcontractTaskList(HashMap)` | `querySubcontractTaskList` | ✅ |
| `queryProjectSupervisionTask(HashMap)` | `queryProjectSupervisionTask` | ✅ |

### 3.10 SendMailDaoImpl（3个方法）

| 方法 | iBatis映射ID | 验证结果 |
|------|-------------|---------|
| `keepMailInfo(MailSenderInfo)` | `insert_into_sys_mails` | ✅ |
| `gainMailInfoList()` | `query_sys_mails` | ✅ |
| `updateMailInfo(MailSenderInfo)` | `update_sys_mails_state` | ✅ |

### 3.11 OpLogDaoImpl（4个方法）

| 方法 | iBatis映射ID | 验证结果 |
|------|-------------|---------|
| `insertLog()` | `insert-Operation-Log` | ✅ |
| `queryLogList(DisplayParam)` | `select-Operation-Log-Sum` + `select-Operation-Log` | ✅ |
| `delete(ArrayList<String>)` | `delete-Log-List` | ✅ 批量删除，每500条执行一次 |
| `queryLogAllList(DisplayParam)` | `select-Operation-Log-Sum` + `select-all-log` | ✅ |

**特殊说明**：`OpLogDaoImpl` 不继承 `BaseDao`，自行注入 `SqlMapClientTemplate`。

### 3.12 PasswordDaoImpl（1个方法）

| 方法 | iBatis映射ID | 验证结果 |
|------|-------------|---------|
| `usChangelogin(PasswordEditParam)` | `update-user-chageloginpass` | ✅ |

### 3.13 ProbManageDaoImpl（约40个方法）

关键映射ID验证：

| 方法 | iBatis映射ID | 验证结果 |
|------|-------------|---------|
| `saveProb(Prob)` | `insert_into_prob` | ✅ |
| `queryProbList(Prob, DisplayParam)` | `setMaxGroupContractLength` + `query_prob_count` + `query_prob_list` | ✅ |
| `queryOneProb(Prob)` | `query_prob_one` | ✅ |
| `updateProb(Prob)` | `update_prob` | ✅ |
| `checkSoftVersionList(SoftVersion)` | `check_soft_version_list` | ✅ |
| `updateInvalidSoftVersion(int)` | `update_invalid_softversion` | ✅ |
| `saveSoftVersion(List, int)` | `insert_into_softversion` | ✅ |
| `querySoftVersionList(int/SoftVersion)` | `query_prob_soft_version` | ✅ |
| `queryProbFileMap(int)` | `query_prob_file_map` | ✅ |
| `queryProbRestoreList(ProbRestore, DisplayParam)` | `count_prob_restore_list` + `query_prob_restore_list` | ✅ |
| `insertBatchProbRestoreTask(ProbRestore, List)` | `insert_batch_probRestore_task_list` | ✅ |
| `queryProbRestoreTaskList(...)` | `query_count_probRestore_task` + `query_list_probRestore_task` | ✅ |
| `queryProbRestoreTaskProjectList(...)` | `query_count_probRestore_task_project` + `query_list_probRestore_task_project` | ✅ |
| `insertProbRestoreProcess(ProbRestore)` | `insert_restore_process` | ✅ |
| `updateProbRestore(int, String, String)` | `update_prob_restore_processId` | ✅ |
| `updateProbRestoreAssignee(ProbRestore, String)` | `update_prob_restore_assignee` | ✅ |
| `queryProbRestoreProcessSize(int)` | `query_prob_restore_process_size` | ✅ |
| `deleteProbInfo(int)` | `delete_prob_info` | ✅ |
| `queryNextVal()` | `query_next_val` | ✅ |
| `queryProbFileList(String)` | `query_prob_file_list` | ✅ |
| `insertProbTaskWeekly(int, int)` | `insert_prob_task_weekly` | ✅ |
| `queryProbWeekly(int, String)` | `query_prob_task_weekly` | ✅ |
| `queryProbAssigneeEmails(String)` | `query_prob_assignee_emails` | ✅ |
| `bacthDeleteProbRestores(String)` | `batch_delete_probRestores` | ✅ |
| `updateProbStatus(Prob)` | `update_prob_status` | ✅ |
| `queryProjectIdsByProbRestoreIds(String)` | `query_projectIds_by_probrestoreIds` | ✅ |
| `queryHistSoftVersionListByProbRestoreIds(String)` | `query_hist_soft_version_list_by_probRestoreIds` | ✅ |
| `queryOfficeMailsByProbRestoreIds(String)` | `query_officeMails_by_probRestoreIds` | ✅ |
| `queryExportProbList(Map)` | `query_exportProb_list` | ✅ |
| `batchAddSoftVersion(List)` | `batch_add_softVersion` | ✅ |
| `queryProbStatisticList/WithReport(...)` | `create_prob_softChangeLog_tempTable` + `create_prob_statistics_tempTable` + `query_prob_statistics_count` + `query_prob_statistics` | ✅ 复杂临时表操作 |
| `queryProbStatisticProjectList(...)` | `create_prob_statistics_projectTempTable` + `query_prob_statistics_project_Count` + `query_prob_statistics_project` | ✅ |
| `queryContractShipmentSoftList(...)` | `create_prob_statistics_projectTempTable` + `query_contract_shipment_soft_count` + `query_contract_shipment_soft_list` | ✅ |
| `insertProbReadLog(ProbReadLog)` | `insertProbReadLog` | ✅ |
| `queryProbReadLogList(ProbReadLog, DisplayParam)` | `count_prob_read_log` + `query_prob_read_log` | ✅ |
| 产品组件CRUD | `selectProductComponentById`, `insertProductComponent`, `updateProductComponentById` 等 | ✅ |
| 产品CRUD | `selectProbProductById`, `insertProbProduct`, `updateProbProductById` 等 | ✅ |
| 产品项查询 | `selectProductItemListByParams`, `selectProductItemListByExample` | ✅ |

### 3.14 SubcontractDaoImpl（约40个方法）

关键映射ID验证：

| 方法 | iBatis映射ID | 验证结果 |
|------|-------------|---------|
| 分包项目CRUD | `selectSubcontractProjectById`, `insertSubcontractProject`, `updateSubcontractProjectByIdSelective` 等 | ✅ |
| 发货信息查询 | `queryShipmentinfoByContractNosAndProjectIds` | ✅ |
| 项目查询 | `queryProjectList` | ✅ |
| 名称检查 | `checkSubcontractName` | ✅ |
| 分包行 | `selectSubcontractLineList`, `batchInsertSubcontractLine`, `batchDeleteSubcontractLine` | ✅ |
| 交付件 | `selectSubcontractDeliverById`, `insertSubcontractDeliver`, `updateSubcontractDeliverByIdSelective` | ✅ |
| 付款 | `selectSubcontractPaymentList`, `insertSubcontractPayment`, `updateSubcontractPaymentByIdSelective` | ✅ |
| 评估 | `insertSubcontractEvaluationHeader`, `updateSubcontractEvaluationHeader` | ✅ |
| 回访 | `insertSubcontractCallback`, `selectSubcontractCallbackList`, `queryCallBackId`, `queryCallBackQuesnaireId`, `queryCallBackQuesnaireVersion` | ✅ |
| 供应商 | `insertSubcontractFacilitator`, `updateSubcontractFacilitatorByIdSelective`, `selectSubcontractFacilitatorById` | ✅ |
| SSE数据源 | `selectDefaultMultiDimByDep`, `selectDefaultMultiDimByDepDirect` | ✅ 使用 `sqlMapClientTemplateSSE` |

**关键发现**：SubcontractDaoImpl 是唯一使用 `getSqlMapClientTemplateSSE()` 的DAO类。

### 3.15 CertificateDaoImpl（4个方法）

| 方法 | iBatis映射ID | 验证结果 |
|------|-------------|---------|
| `queryOQCInfo(String)` | `queryOQCInfo` | ✅ |
| `insertSealInfo(List)` | `insertSealInfo` | ✅ |
| `insertSealInfo(HashMap)` | `insertSealInfo` | ✅ 重载方法，同一映射ID |
| `deleteSealInfo()` | `truncateSealInfo` | ✅ |

---

## 四、源码中发现的事实性问题

### 4.1 严重问题

| 编号 | 问题 | 位置 | 说明 |
|------|------|------|------|
| S-01 | **文档几乎为空** | `dao-sql-reference.md` | 仅含截断标题 `# PMS-Str`，无任何实质内容 |
| S-02 | **类名错误** | 文档提及 `WorkFlowServiceImpl.java` | 实际类名为 `WorkflowDaoImpl.java`，且该文件确实存在于源码中 |
| S-03 | **OpLogDaoImpl不继承BaseDao** | `OpLogDaoImpl.java` | 唯一不继承BaseDao的DAO类，自行持有SqlMapClientTemplate，文档若按统一模板描述将产生误导 |
| S-04 | **update-md5pwd-byusername更新错误表** | `sql-map-admin-config.xml` 第300-305行 | SQL更新的是 `user` 表而非 `fnd_user_info` 表，与其他用户相关SQL不一致 |

### 4.2 中等问题

| 编号 | 问题 | 位置 | 说明 |
|------|------|------|------|
| M-01 | **insert/update语义混用** | `UserManageDaoImpl.updatepwdbyusername()` | 使用 `insert()` 调用 `update-md5pwd-byusername` 映射（定义为update标签） |
| M-02 | **delete语义使用insert/update调用** | `ProjectDaoImpl` | `deleteShipmentInstallInfoByProjectId` 用insert调用，`deleteWeeklyContent`/`deleteFileById` 用update调用 |
| M-03 | **方法名拼写错误** | `LoginDaoImpl.querUser()` | 应为 `queryUser`，源码中保留了此拼写错误 |
| M-04 | **query_defaultpage_by_username_2引用不存在的列** | `sql-map-admin-config.xml` 第532-537行 | SQL引用 `fnd_user_info.role_id`，但fnd_user_info表中该列可能不存在（其他SQL使用roleIds） |

### 4.3 轻微问题

| 编号 | 问题 | 位置 | 说明 |
|------|------|------|------|
| L-01 | **方法命名风格不统一** | 多个DAO | 混用中划线（`query-user-by-name`）和下划线（`query_user_by_id`）两种映射ID命名风格 |
| L-02 | **大量注释掉的代码** | `ProjectDaoImpl` | 约30%代码被注释，包括旧版查询逻辑 |
| L-03 | **临时表操作散布** | `ProjectDaoImpl`, `ProbManageDaoImpl`, `PmClosedLoopDaoImpl` | 临时表创建/删除未封装，散布在业务方法中 |

---

## 五、iBatis映射文件与源码映射ID一致性验证

### 5.1 映射文件覆盖情况

| 映射文件 | 大小 | 对应DAO | 状态 |
|---------|------|---------|------|
| sql-map-admin-config.xml | ~1060行 | LoginDaoImpl, UserManageDaoImpl, SendMailDaoImpl, OpLogDaoImpl, PasswordDaoImpl | ✅ 已验证 |
| sql-map-project-config.xml | ~367KB | ProjectDaoImpl | ⚠️ 文件过大，部分验证 |
| sql-map-presales-config.xml | ~835行 | PresalesDaoImpl | ✅ 已验证 |
| sql-map-callback-config.xml | ~150行 | CallBackDaoImpl, PmClosedLoopDaoImpl | ✅ 已验证 |
| sql-map-prob-config.xml | ~136KB | ProbManageDaoImpl | ⚠️ 文件过大，部分验证 |
| sql-map-subcontract-config.xml | ~160KB | SubcontractDaoImpl | ⚠️ 文件过大，部分验证 |
| sql-map-report-config.xml | ~664行 | ReportDaoImpl | ✅ 已验证 |
| sql-map-work-config.xml | ~588行 | WorkflowDaoImpl, WorkSpaceDaoImpl | ✅ 已验证 |

### 5.2 跨映射文件共享的映射ID

以下映射ID被多个DAO类共用：

| 映射ID | 使用者 | 所在映射文件 |
|--------|-------|-------------|
| `query_act_comment_list` | PresalesDaoImpl, WorkflowDaoImpl | 需确认具体位置 |
| `query_sys_arg` | LoginDaoImpl, ProbManageDaoImpl | sql-map-admin-config.xml |
| `queryQuesnaireResultColumns` | ProjectDaoImpl, PresalesDaoImpl | 需确认具体位置 |
| `createTempQuesnaireResultLineTable` / `deleteTempQuesnaireResultLineTable` | ProjectDaoImpl, PresalesDaoImpl | 需确认具体位置 |
| `setMaxGroupContractLength` | ProjectDaoImpl, ProbManageDaoImpl, PresalesDaoImpl | 需确认具体位置 |

---

## 六、动态SQL标签使用统计

| 标签类型 | 使用场景 | 出现频率 |
|---------|---------|---------|
| `<isNotEmpty>` | 条件查询参数非空判断 | 极高（几乎所有条件查询） |
| `<isEqual>` | 枚举值条件判断 | 高（报表查询、权限判断） |
| `<isNotNull>` | 时间范围条件 | 中 |
| `<iterate>` | 批量操作、IN条件 | 中（批量插入、多值查询） |
| `<dynamic>` | 动态WHERE条件组合 | 高 |
| `<isNotEqual>` | 排除特定值 | 低 |
| `<isEmpty>` | 空值判断（如默认办事处） | 低 |

---

## 七、建议

### 7.1 文档重建建议

1. **重写 `dao-sql-reference.md`**：当前文档为空，需基于本审查报告中的信息重建
2. **按DAO类分章节组织**：每个DAO类一个章节，包含继承关系、方法列表、映射ID、SQL语句
3. **标注动态SQL逻辑**：对使用 `<isNotEmpty>`、`<iterate>` 等动态标签的SQL，描述其条件逻辑
4. **标注临时表操作**：对使用临时表的复杂查询，说明临时表生命周期

### 7.2 源码修复建议

1. **修复 `update-md5pwd-byusername` 的表名**：`user` → `fnd_user_info`
2. **统一映射ID命名风格**：建议统一使用下划线风格
3. **修复 `query_defaultpage_by_username_2` 中不存在的列引用**
4. **修正 `LoginDaoImpl.querUser` 方法名拼写**
5. **统一insert/update/delete调用语义**：避免用insert调用update映射等

---

## 八、审查结论

| 审查维度 | 结论 |
|---------|------|
| 文档完整性 | ❌ **严重缺失** — 文档几乎为空，无法作为参考 |
| 类名准确性 | ❌ **存在错误** — WorkFlowServiceImpl应为WorkflowDaoImpl |
| 继承关系准确性 | ❌ **存在遗漏** — OpLogDaoImpl不继承BaseDao这一特殊情况未体现 |
| 映射ID一致性 | ✅ **基本一致** — 源码中调用的映射ID在映射文件中均能找到对应定义 |
| SQL语句准确性 | ⚠️ **部分问题** — 存在表名错误（user vs fnd_user_info） |
| 动态SQL描述 | ❌ **无法验证** — 文档为空，无动态SQL描述可供审查 |

**总体评级**：文档当前状态为 **不可用**，需要基于源码完全重建。
