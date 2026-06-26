## 审查报告：Action方法参考文档

> 审查日期：2026-05-19
> 审查对象：`docs/02-modules/action-methods-reference.md`
> 审查方法：逐一读取源码与文档进行交叉验证，同时对照 `applicationContext-action.xml` 和 `struts-sys.xml` 配置

---

### 发现的错误

| 序号 | Action类 | 错误类型 | 文档描述 | 实际情况 | 修正方案 |
|------|----------|----------|----------|----------|----------|
| 1 | ReportAction | 方法名错误 | 文档第15节列出核心入口方法为 `execute()`，返回值 `SUCCESS` | 源码中 ReportAction 没有 `execute()` 方法，核心入口方法为 `show()`（返回 `"show"`）和 `projectSummaryStatus()`（返回 `"projectSummaryStatus"` 或 `SUCCESS`） | 将 `execute()` 改为 `show()` 和 `projectSummaryStatus()` |
| 2 | ReportAction | 方法遗漏严重 | 文档称"仅列出核心入口方法"，暗示只有 `execute()` 和 `prepare()` | 源码包含大量公开方法：`show()`, `loadLineData()`, `loadLine_qualityData()`, `loadLine_implData()`, `assignedRate()`, `traceRate()`, `closeRate()`, `implRate()`, `quality()`, `projectSummaryStatus()`, `input()`（已废弃） | 补充所有公开方法的文档 |
| 3 | WorkFlowAction | 方法遗漏严重 | 文档称"还包含流程图查看、任务办理、流程代理等方法"但未列出 | 源码包含：`viewDeployment()`, `viewimage()`, `selftask()`, `viewTaskForm()`, `submitTask()`, `viewCurrentImage()`, `taskmanager()`, `hisTaskForm()`, `delegatelist()`, `delegateadd()`, `delegateedit()`, `delegateupdate()` | 补充所有公开方法的文档 |
| 4 | WorkSpaceAction | 方法遗漏严重 | 文档仅描述 `prepare()` 和 `execute()` | 源码包含：`prepareExecute()`, `execute()`, `prepareNotice()`, `notice()`, `prepareTask()`, `task()`, `dailyTask()`, `hisselftask()`, `probTask()`, `subcontractTask()`, `updateNotifyState()`（已废弃） | 补充所有公开方法的文档 |
| 5 | ProbManageAction | 方法遗漏严重 | 文档仅概括性描述"包含技术公告的完整CRUD、设备关联、软件版本管理、导出等功能" | 源码包含大量公开方法：`list()`, `input()`, `delete()`, `bacthDeleteProbRestores()`, `edit()`, `checkProject()`, `checkSubProject()`, `releaseTask()`, `managePrivateTask()`, `updatePrivateTask()`, `weeklyUpload()`, `manageAllTask()`, `updateRestoreTask()`, `save()`, `update()`, `audit()`, `export()`, `importSoftVersion()`, `toCheckSoftVersion()`, `submitSoftVersion()`, `parserSoftVersion()`, `parserOldSoftVersion()`, `statistics()`, `affectedProjectSoftVersion()`, `readSure()`, `readLog()`, `listProductItem()`, `listProbProduct()`, `inputProbProduct()`, `saveProbProduct()`, `importProbProduct()`, `listComponent()`, `inputComponent()`, `saveComponent()`, `importComponent()` | 补充所有公开方法的文档 |
| 6 | SubcontractAction | 方法遗漏严重 | 文档仅概括性描述"包含外包项目全生命周期管理" | 源码包含大量公开方法：`view()`, `list()`, `input()`, `create()`, `apply()`, `audit()`, `close()`, `startCallBackFlow()`, `querySubcontractCallback()`, `chooseSubcontractProject()`, `refreshSubcontractProject()`, `chooseShipmentInfo()`, `querySubcontractLine()`, `querySubcontractDeliver()`, `deleteSubcontractDeliver()`, `checkSubcontractName()`, `queryContractNoEngineeFee()`, `querySubcontractPayment()`, `savePayment()`, `querySubcontractPaymentPrint()`, `verifyPaymentDeliver()`, `terminateWorkFlow()`, `querySubcontractComment()`, `queryFacilitator()`, `querySubcontractInfoForProject()`, `facilitatorList()`, `facilitatorEdit()`, `downloadFile()` | 补充所有公开方法的文档 |
| 7 | MaintenanceAction | 方法遗漏 | 文档仅列出 `prepareExecute()` 和 `execute()`，称"还包含维护记录创建、问卷填写、交付件上传等方法" | 源码包含：`execute()`, `projectMaintenance()`, `createProjectMaintenance()`, `serviceDelivery()`, `toUploadFile()`, `uploadFileList()` | 补充所有公开方法的文档 |
| 8 | SupervisionAction | 方法遗漏 | 文档仅列出 `prepareExecute()` 和 `execute()`，称"结构与 MaintenanceAction 类似" | 源码包含：`execute()`, `projectSupervision()`, `createProjectSupervision()`, `deleteProjectSupervision()`, `queryPowerUser()` | 补充所有公开方法的文档 |
| 9 | WarrantyCallbackAction | 方法遗漏 | 文档仅列出 `prepare()`，称"结构与 SupervisionAction/MaintenanceAction 类似" | 源码包含：`prepare()`, `prepareExecute()`, `execute()`, `projectWarrantyCallback()`, `createProjectWarrantyCallback()`, `deleteProjectWarrantyCallback()`, `projectWarranty()`, `customerProject()`, `queryPowerUser()` | 补充所有公开方法的文档 |
| 10 | ProjectAction | 方法遗漏 | 文档列出了大量方法但仍有遗漏 | 源码中缺少以下方法：`transferProject()`, `toUploadFile()`, `toUploadDeliverableFile()`, `downloadFile()`, `deleteFile()`, `getDownloadFile()` | 补充遗漏方法的文档 |
| 11 | CertificateAction | 方法遗漏 | 文档列出 `certificate()`, `queryCertificate()`, `generateProductionDate()` | 源码还包含 `uploadSealInfo()` 方法（Struts配置中有 `uploadSealInfo` action映射） | 补充 `uploadSealInfo()` 方法文档 |
| 12 | PresalesAction | 方法遗漏 | 文档列出了大部分方法 | 源码还包含 `updateConfirmFiles()` 方法（Struts配置中有 `updateConfirmFiles` action映射） | 补充 `updateConfirmFiles()` 方法文档 |
| 13 | PmClosedLoopQuesnaireAction | 方法遗漏 | 文档列出了大部分方法 | 源码还包含 `editLine()` 方法（第278行） | 补充 `editLine()` 方法文档 |
| 14 | ClusterAction | Spring Bean名缺失 | 文档未提及Spring Bean名 | ClusterAction未在 `applicationContext-action.xml` 中注册，也未在 `struts-sys.xml` 中配置URL映射 | 文档应注明该类未在Spring/Struts配置中注册，可能为未启用功能 |
| 15 | LoginAction | Spring Bean名描述不完整 | 文档未明确提及Spring Bean名 | Spring配置中Bean id为 `Login`（非 `LoginAction`） | 补充说明Spring Bean名为 `Login` |
| 16 | PasswordGetinfo | Spring Bean名描述不完整 | 文档未明确提及Spring Bean名 | Spring配置中Bean id为 `PasswordGetinfo`（与类名相同） | 补充说明Spring Bean名 |
| 17 | CertificateAction | Spring Bean名描述不完整 | 文档未明确提及Spring Bean名 | Spring配置中Bean id为 `Certificate`（非 `CertificateAction`） | 补充说明Spring Bean名为 `Certificate` |
| 18 | UserManageAction | 依赖服务描述不完整 | 文档列出依赖服务为 `UserManageService`, `DepartmentManageService` | `pwdreset()` 方法通过 `SpringContext.getBean()` 动态获取 `PasswordService`，虽然不是注入的，但功能上依赖该服务 | 在依赖服务说明中补充备注 |
| 19 | BaseAction | 方法遗漏 | 文档列出了主要方法 | 源码还包含 `getServletContext()` 和 `setServletContext()`（非接口方法，返回 `ServletContext`），`getServletResponse()`, `getServletRequest()` 等getter方法 | 补充getter方法文档（可选，低优先级） |
| 20 | UserManageAction | 方法遗漏 | 文档列出了主要方法 | 源码还包含 `getStatusList()` 方法（返回 `Map<String, String>`，获取用户状态列表） | 补充 `getStatusList()` 方法文档 |
| 21 | RoleManageAction | 属性遗漏 | 文档未列出属性 | 源码包含 `errorMessage` 属性（String类型） | 补充属性说明 |
| 22 | DataAnalysisAction | 依赖服务遗漏 | 文档列出依赖服务为 `BasicDataService`, `DataAnalysisService`, `DepartmentManageService` | Spring配置中注入了这三个服务 ✓，验证通过 | 无需修改 |
| 23 | ReportAction | 实现接口描述错误 | 文档说"实现接口: Preparable" | 源码确实实现了 `Preparable` ✓，但 `prepare()` 方法中权限校验逻辑描述不准确——文档说"权限校验"，实际是"非管理员/工程管理部/财务/项目admin/回访人员角色时重定向到 projectSummaryStatus 页面" | 修正权限校验逻辑描述 |

---

### 遗漏的内容

| 序号 | Action类 | 遗漏内容 | 源码位置 | 补充方案 |
|------|----------|----------|----------|----------|
| 1 | ProjectAction | 缺少 `transferProject()` 方法 | 第519行 | 添加该方法文档，转移项目操作 |
| 2 | ProjectAction | 缺少 `toUploadFile()` 方法 | 第1654行 | 添加该方法文档，跳转到周报附件上传页面 |
| 3 | ProjectAction | 缺少 `toUploadDeliverableFile()` 方法 | 第1658行 | 添加该方法文档，跳转到交付件上传页面 |
| 4 | ProjectAction | 缺少 `downloadFile()` 方法 | 第1730行 | 添加该方法文档，项目文件下载（Struts配置中有映射） |
| 5 | ProjectAction | 缺少 `deleteFile()` 方法 | 第1795行 | 添加该方法文档，删除项目附件文件 |
| 6 | CertificateAction | 缺少 `uploadSealInfo()` 方法 | 第101行 | 添加该方法文档，上传合格证印章登记表（Struts配置中有映射） |
| 7 | PresalesAction | 缺少 `updateConfirmFiles()` 方法 | 第832行 | 添加该方法文档，更新确认文件（Struts配置中有映射） |
| 8 | PmClosedLoopQuesnaireAction | 缺少 `editLine()` 方法 | 第278行 | 添加该方法文档，编辑问卷题目 |
| 9 | ReportAction | 缺少 `show()` 方法 | 第113行 | 添加该方法文档，报表展示主页面（返回 `"show"`） |
| 10 | ReportAction | 缺少 `loadLineData()` 方法 | 第160行 | 添加该方法文档，加载趋势图（折线图） |
| 11 | ReportAction | 缺少 `loadLine_qualityData()` 方法 | 第202行 | 添加该方法文档，查询项目闭环数量趋势图 |
| 12 | ReportAction | 缺少 `loadLine_implData()` 方法 | 第235行 | 添加该方法文档，加载企业网项目实施占比趋势图 |
| 13 | ReportAction | 缺少 `assignedRate()` 方法 | 第291行 | 添加该方法文档，项目指派率查询 |
| 14 | ReportAction | 缺少 `traceRate()` 方法 | 第322行 | 添加该方法文档，项目经理跟踪率 |
| 15 | ReportAction | 缺少 `closeRate()` 方法 | 第349行 | 添加该方法文档，季度新增闭环比 |
| 16 | ReportAction | 缺少 `implRate()` 方法 | 第392行 | 添加该方法文档，项目实施方式占比 |
| 17 | ReportAction | 缺少 `quality()` 方法 | 第464行 | 添加该方法文档，项目质量统计 |
| 18 | ReportAction | 缺少 `projectSummaryStatus()` 方法 | 第537行 | 添加该方法文档，项目汇总状态统计 |
| 19 | ReportAction | 缺少 `input()` 方法（已废弃） | 第1064行 | 添加该方法文档，标注 @Deprecated |
| 20 | WorkFlowAction | 缺少 `viewDeployment()` 方法 | 第101行 | 添加该方法文档，根据流程KEY查询流程部署信息 |
| 21 | WorkFlowAction | 缺少 `viewimage()` 方法 | 第116行 | 添加该方法文档，查看流程图 |
| 22 | WorkFlowAction | 缺少 `selftask()` 方法 | 第137行 | 添加该方法文档，私有任务查询 |
| 23 | WorkFlowAction | 缺少 `viewTaskForm()` 方法 | 第152行 | 添加该方法文档，打开任务表单 |
| 24 | WorkFlowAction | 缺少 `submitTask()` 方法 | 第166行 | 添加该方法文档，提交任务 |
| 25 | WorkFlowAction | 缺少 `viewCurrentImage()` 方法 | 第176行 | 添加该方法文档，查看当前流程图 |
| 26 | WorkFlowAction | 缺少 `taskmanager()` 方法 | 第189行 | 添加该方法文档，管理员查看所有任务 |
| 27 | WorkFlowAction | 缺少 `hisTaskForm()` 方法 | 第197行 | 添加该方法文档，查看已办理任务表单 |
| 28 | WorkFlowAction | 缺少 `delegatelist()` 方法 | 第214行 | 添加该方法文档，获取委派任务列表 |
| 29 | WorkFlowAction | 缺少 `delegateadd()` 方法 | 第227行 | 添加该方法文档，添加委派任务规则 |
| 30 | WorkFlowAction | 缺少 `delegateedit()` 方法 | 第243行 | 添加该方法文档，编辑委派任务规则 |
| 31 | WorkFlowAction | 缺少 `delegateupdate()` 方法 | 第273行 | 添加该方法文档，修改委派任务规则 |
| 32 | WorkSpaceAction | 缺少 `prepareExecute()` 方法 | 第154行 | 添加该方法文档 |
| 33 | WorkSpaceAction | 缺少 `notice()` 方法 | 第194行 | 添加该方法文档，系统通知查询 |
| 34 | WorkSpaceAction | 缺少 `task()` 方法 | 第216行 | 添加该方法文档，业务流程办理 |
| 35 | WorkSpaceAction | 缺少 `dailyTask()` 方法 | 第278行 | 添加该方法文档，日常项目跟踪 |
| 36 | WorkSpaceAction | 缺少 `hisselftask()` 方法 | 第290行 | 添加该方法文档，查看自己办理过的任务 |
| 37 | WorkSpaceAction | 缺少 `probTask()` 方法 | 第306行 | 添加该方法文档，查看技术公告任务 |
| 38 | WorkSpaceAction | 缺少 `subcontractTask()` 方法 | 第319行 | 添加该方法文档，查看项目转包任务 |
| 39 | WorkSpaceAction | 缺少 `updateNotifyState()` 方法（已废弃） | 第339行 | 添加该方法文档，标注 @Deprecated |
| 40 | MaintenanceAction | 缺少 `projectMaintenance()` 方法 | 第142行 | 添加该方法文档 |
| 41 | MaintenanceAction | 缺少 `createProjectMaintenance()` 方法 | 第218行 | 添加该方法文档 |
| 42 | MaintenanceAction | 缺少 `serviceDelivery()` 方法 | 第432行 | 添加该方法文档 |
| 43 | MaintenanceAction | 缺少 `toUploadFile()` 方法 | 第479行 | 添加该方法文档 |
| 44 | MaintenanceAction | 缺少 `uploadFileList()` 方法 | 第529行 | 添加该方法文档 |
| 45 | SupervisionAction | 缺少 `projectSupervision()` 方法 | 第108行 | 添加该方法文档 |
| 46 | SupervisionAction | 缺少 `createProjectSupervision()` 方法 | 第157行 | 添加该方法文档 |
| 47 | SupervisionAction | 缺少 `deleteProjectSupervision()` 方法 | 第236行 | 添加该方法文档 |
| 48 | SupervisionAction | 缺少 `queryPowerUser()` 方法 | 第253行 | 添加该方法文档 |
| 49 | WarrantyCallbackAction | 缺少 `prepareExecute()` 方法 | 第112行 | 添加该方法文档 |
| 50 | WarrantyCallbackAction | 缺少 `execute()` 方法 | 第126行 | 添加该方法文档 |
| 51 | WarrantyCallbackAction | 缺少 `projectWarrantyCallback()` 方法 | 第143行 | 添加该方法文档 |
| 52 | WarrantyCallbackAction | 缺少 `createProjectWarrantyCallback()` 方法 | 第220行 | 添加该方法文档 |
| 53 | WarrantyCallbackAction | 缺少 `deleteProjectWarrantyCallback()` 方法 | 第305行 | 添加该方法文档 |
| 54 | WarrantyCallbackAction | 缺少 `projectWarranty()` 方法 | 第322行 | 添加该方法文档 |
| 55 | WarrantyCallbackAction | 缺少 `customerProject()` 方法 | 第374行 | 添加该方法文档 |
| 56 | WarrantyCallbackAction | 缺少 `queryPowerUser()` 方法 | 第411行 | 添加该方法文档 |
| 57 | ProbManageAction | 缺少全部公开方法文档 | 多处 | 需补充约30+个公开方法的详细文档 |
| 58 | SubcontractAction | 缺少全部公开方法文档 | 多处 | 需补充约28个公开方法的详细文档 |
| 59 | UserManageAction | 缺少 `getStatusList()` 方法 | 第231行 | 添加该方法文档 |

---

### 验证通过的内容

- **BaseAction**: 继承关系、实现接口、核心方法（start, setErrmsg, setWarnMessage, getErrmsg, setServletContext/Request/Response）全部验证通过
- **LoginAction**: 继承关系、依赖服务、所有方法（start, execute, casLogin, noCasLogin, logout, error404）验证通过
- **UserManageAction**: 继承关系、Preparable接口、依赖服务、主要方法（prepare, execute, add, checkUsername, edit, submit, pwdreset, findUser, checkSubmitData）验证通过
- **RoleManageAction**: 继承关系、依赖服务、所有方法（execute, add, addSubmit, edit, editSubmit）验证通过
- **DepartmentManageAction**: 继承关系、依赖服务、所有方法（execute, refresh, add, addSubmit, edit）验证通过
- **BasicDataManageAction**: 继承关系、依赖服务、所有方法（execute, basicdataUpdate, basicdataInsert, findBasicDataId, executeSql）验证通过
- **OperateLogAction**: 继承关系、依赖服务（OpLogService）、所有方法（execute, exportlog, syncTask, getDownloadLogName, getInputLogStream）验证通过
- **PasswordGetinfo**: 继承关系、依赖服务、所有方法（executepwd, editlogin, resetPassword）验证通过
- **UploadAction**: 继承关系、依赖服务、所有方法（upload, deleteFile, downloadFile, queryFile, uploadImage, getDownloadFile, getFileStream）验证通过
- **ProjectAction**: 继承关系、Preparable接口、依赖服务、大部分方法验证通过（execute, insertProject, createCHProject, transferShipment, exportSpotCheck, exportOverWarrantyRemind, importSpotCheckIgnoreItem, updateProject, checkOrderData, checkRealOrderData, projectLeaseLine, projectProductConfigLevelInfo, checkShipmentInfo, deleteShipmentInfo, checkSoftVersion, updateSoftVersion, checkhistsoftversion, queryProjectNotification, problemTicket, projectMaintenance, createProjectMaintenance, editProjectPlan, uploadDeliverableFile, deleteDeliverById, backToLastStep, createWeekly, saveWeekly, submitWeekly, updateWeekly, UploadFile, feedback, instruction, queryalluser, queryperson, updateprojectisback, createMember, updateMember, saveInstallAdress, updateProjectExecutionState, toMergeOrBranch, checkMergeContract, mergeContract, branchContract, queryDpNoRoleUser, batchChangeMember, importProject, clearProject）
- **PresalesAction**: 继承关系、Preparable接口、依赖服务、大部分方法验证通过（prepareList, list, input, apply, read, aduit, smaduit, pmaduit, updateTask, emaduit, callback, shipmentInfo, lend2SaleInfo, lend2RmaInfo, tempAuthInfo, terminate2Close, syncOaData, upload, deleteDeliverById, updateDeliverById, exportPresales）
- **CallBackAction**: 继承关系、依赖服务、所有方法（input, apply, read, seeQuesnaire, resubmit, aduit）验证通过
- **PmClosedLoopAction**: 继承关系、依赖服务、所有方法（execute, addPmCLApply, addSmCLApply, addCbCLApply, cantCB, addClCLApply, pmSeeCbCl, getUserPower）验证通过
- **PmClosedLoopQuesnaireAction**: 继承关系、依赖服务、大部分方法验证通过（execute, addPCLQuesnaire, pmCLQuesEdit, submitQues, addLine, submitLine, updateQues, deleteHeader, startEffective, pmCLQuesSee, deleteLine, endEffective）
- **DataAnalysisAction**: 继承关系、依赖服务、方法（execute）验证通过
- **ClusterAction**: 继承关系、依赖服务、方法（refreshCacheData, notifyCluster）验证通过

---

### Spring Bean名与源码对照表

| Action类 | Spring Bean id | 文档是否提及 | 备注 |
|----------|---------------|-------------|------|
| LoginAction | `Login` | 未提及 | Bean名与类名不同 |
| UserManageAction | `UserManageAction` | 未提及 | |
| RoleManageAction | `RoleManageAction` | 未提及 | |
| DepartmentManageAction | `DepartmentManageAction` | 未提及 | |
| PasswordGetinfo | `PasswordGetinfo` | 未提及 | |
| OperateLogAction | `OperateLogAction` | 未提及 | |
| WorkSpaceAction | `WorkSpaceAction` | 未提及 | |
| PmClosedLoopAction | `PmClosedLoopAction` | 未提及 | |
| PmClosedLoopQuesnaireAction | `PmClosedLoopQuesnaireAction` | 未提及 | |
| ProjectAction | `ProjectAction` | 未提及 | |
| BasicDataManageAction | `BasicDataManageAction` | 未提及 | |
| DataAnalysisAction | `DataAnalysisAction` | 未提及 | |
| ReportAction | `ReportAction` | 未提及 | |
| CallBackAction | `CallBackAction` | 未提及 | |
| PresalesAction | `PresalesAction` | 未提及 | |
| ProbManageAction | `ProbManageAction` | 未提及 | |
| UploadAction | `UploadAction` | 未提及 | |
| SubcontractAction | `SubcontractAction` | 未提及 | |
| CertificateAction | `Certificate` | 未提及 | Bean名与类名不同 |
| MaintenanceAction | `MaintenanceAction` | 未提及 | |
| SupervisionAction | `SupervisionAction` | 未提及 | |
| WarrantyCallbackAction | `WarrantyCallbackAction` | 未提及 | |
| ClusterAction | **未注册** | 未提及 | 未在Spring/Struts配置中注册 |
| WorkFlowAction | `WorkFlowAction` | 未提及 | |

---

### 总结

1. **最严重问题**：ReportAction 的核心入口方法名错误（文档写 `execute()`，实际为 `show()` 和 `projectSummaryStatus()`），这会导致使用者无法正确调用。

2. **最普遍问题**：多个大型 Action 类（ReportAction、WorkFlowAction、WorkSpaceAction、ProbManageAction、SubcontractAction、MaintenanceAction、SupervisionAction、WarrantyCallbackAction）的方法文档严重不完整，仅以概括性描述代替具体方法列表，不符合"方法级参考文档"的定位。

3. **Spring Bean名缺失**：文档未为任何 Action 类标注 Spring Bean 名，其中 `Login`（LoginAction）、`Certificate`（CertificateAction）的 Bean 名与类名不同，容易造成混淆。

4. **ClusterAction 未注册**：ClusterAction 存在源码但未在 Spring 和 Struts 配置中注册，文档应明确标注其状态。

5. **个别方法遗漏**：ProjectAction 遗漏了 `transferProject()`、`toUploadFile()`、`toUploadDeliverableFile()`、`downloadFile()`、`deleteFile()` 等方法；CertificateAction 遗漏了 `uploadSealInfo()` 方法；PresalesAction 遗漏了 `updateConfirmFiles()` 方法；PmClosedLoopQuesnaireAction 遗漏了 `editLine()` 方法。
