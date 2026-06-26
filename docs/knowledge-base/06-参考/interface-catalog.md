# 接口总目录

> 本文档汇总 PMS 与 SPMS 两个系统的全部接口清单，按模块和命名空间分组，并提供接口统计与外部系统集成接口说明。详细接口字段定义请参考 [interface-template.md](interface-template.md) 模板及各模块文档。

---

## 1. 接口分类总览

PMS 与 SPMS 系统采用双 Web 框架并存架构，接口按调用入口分为以下几类：

| 分类 | 框架 | URL 规则 | 接口数量（约） | 文档位置 |
|------|------|----------|---------------|----------|
| PMS-struts Action | Struts2 2.5.30 | `*.action` | 200+ | [PMS-struts Action 方法参考](../../PMS-struts/docs/02-modules/action-methods-reference.md) |
| PMS-springmvc Controller | Spring MVC 5.3.19 | `*.html/*.json/*.xlsx/*.xls/modals/*` | 150+ | [pms-springmvc.md](../02-模块/pms-springmvc.md) |
| PMS-activiti 工作流 API | Activiti 5.23.0 | `/activiti/*` | 30+ | [pms-activiti.md](../02-模块/pms-activiti.md) |
| PMS-ext-d365 D365 集成 | 静态工具类 | 内部调用 | 4 | [pms-ext-d365.md](../02-模块/pms-ext-d365.md) |
| PMS-ext-fp FP 集成 | 静态工具类 | 内部调用 | 5+ | [pms-ext-fp.md](../02-模块/pms-ext-fp.md) |
| SPMS Action | Struts2 2.0 | `*.action` | 150+ | [SPMS 模块文档](../../SPMS/docs/02-模块/) |
| 外部系统集成接口 | HTTP/REST | 跨系统调用 | 15+ | 本文第 8 节 |

---

## 2. PMS-struts Action 接口清单

PMS-struts 通过 `struts-sys.xml` 配置 Action 映射，所有 URL 以 `.action` 结尾。命名空间分组如下：

### 2.1 命名空间 `/`（login 包）— 登录注销

| Action 名称 | 类 | 方法 | URL | 说明 |
|------------|-----|------|-----|------|
| Login | LoginAction | execute | `/Login.action` | 登录页面 |
| Login | LoginAction | login | `/Login_login.action` | 提交登录 |
| Logout | LoginAction | logout | `/Logout.action` | 注销 |
| PasswordGetinfo | PasswordGetinfo | execute | `/PasswordGetinfo.action` | 密码找回 |

### 2.2 命名空间 `/base`（base 包）— 基础数据维护

| Action 名称 | 类 | 方法 | URL | 说明 |
|------------|-----|------|-----|------|
| UserManage | UserManageAction | execute | `/base/UserManage.action` | 用户列表 |
| UserEdit | UserManageAction | edit | `/base/UserEdit_edit.action` | 编辑用户 |
| UserAdd | UserManageAction | add | `/base/UserAdd_add.action` | 新增用户 |
| RoleManage | RoleManageAction | execute | `/base/RoleManage.action` | 角色管理 |
| DepartmentManage | DepartmentManageAction | execute | `/base/DepartmentManage.action` | 部门管理 |
| BasicdataManage | BasicDataManageAction | execute | `/base/BasicdataManage.action` | 基础数据管理 |

### 2.3 命名空间 `/module`（module 包）— 项目主业务

| Action 名称 | 类 | 方法 | URL | 说明 |
|------------|-----|------|-----|------|
| ProjectManage | ProjectAction | execute | `/module/ProjectManage.action` | 项目列表 |
| ProjectCreate | ProjectAction | insertProject | `/module/ProjectCreate_insertProject.action` | 创建项目 |
| ProjectModify | ProjectAction | modifyProject | `/module/ProjectModify_modifyProject.action` | 修改项目 |
| Presales | PresalesAction | execute | `/module/Presales.action` | 售前项目 |
| CallBack | CallBackAction | execute | `/module/CallBack.action` | 回访管理 |
| PmClosedLoop | PmClosedLoopAction | execute | `/module/PmClosedLoop.action` | 闭环管理 |
| PmClosedLoopQuesnaire | PmClosedLoopQuesnaireAction | execute | `/module/PmClosedLoopQuesnaire.action` | 闭环问卷 |
| Report | ReportAction | execute | `/module/Report.action` | 报表 |
| DataAnalysis | DataAnalysisAction | execute | `/module/DataAnalysis.action` | 数据分析 |
| WorkSpace | WorkSpaceAction | execute | `/module/WorkSpace.action` | 工作台 |
| Cluster | ClusterAction | execute | `/module/Cluster.action` | 集群管理 |
| ProbManage | ProbManageAction | execute | `/module/ProbManage.action` | 技术公告 |
| Subcontract | SubcontractAction | execute | `/module/Subcontract.action` | 转包管理 |
| Maintenance | MaintenanceAction | execute | `/module/Maintenance.action` | 维保管理 |
| Supervision | SupervisionAction | execute | `/module/Supervision.action` | 督导管理 |
| Certificate | CertificateAction | execute | `/module/Certificate.action` | 证书管理 |
| WarrantyCallback | WarrantyCallbackAction | execute | `/module/WarrantyCallback.action` | 维保回访 |

### 2.4 命名空间 `/module/sub`（popwin 包）— 弹窗子页面

| Action 名称 | 类 | 方法 | URL | 说明 |
|------------|-----|------|-----|------|
| projectMember | ProjectAction | * | `/module/sub/projectMember_*.action` | 项目成员弹窗 |
| projectContract | ProjectAction | * | `/module/sub/projectContract_*.action` | 项目合同弹窗 |
| projectFile | ProjectAction | * | `/module/sub/projectFile_*.action` | 项目文件弹窗 |
| projectWeekly | ProjectAction | * | `/module/sub/projectWeekly_*.action` | 项目周报弹窗 |
| projectNotification | ProjectAction | * | `/module/sub/projectNotification_*.action` | 项目通知弹窗 |

### 2.5 命名空间 `/work`（flow 包）— 工作流

| Action 名称 | 类 | 方法 | URL | 说明 |
|------------|-----|------|-----|------|
| WorkFlow | WorkFlowAction | execute | `/work/WorkFlow.action` | 工作流入口 |
| WorkFlow | WorkFlowAction | startProcess | `/work/WorkFlow_startProcess.action` | 启动流程 |
| WorkFlow | WorkFlowAction | complete | `/work/WorkFlow_complete.action` | 完成任务 |

### 2.6 命名空间 `/sys`（main 包）— 系统主页面

| Action 名称 | 类 | 方法 | URL | 说明 |
|------------|-----|------|-----|------|
| main | BaseAction | execute | `/sys/main.action` | 系统首页 |
| Upload | UploadAction | execute | `/sys/Upload.action` | 文件上传 |
| OperateLog | OperateLogAction | execute | `/sys/OperateLog.action` | 操作日志 |

### 2.7 命名空间 `/ajax`（ajax/ajaxJSON 包）— AJAX 接口

| Action 名称 | 类 | 方法 | URL | 说明 |
|------------|-----|------|-----|------|
| syncTask | BaseAction | syncTask | `/ajax/syncTask.action` | 同步任务 |
| updatePresalesTask | PresalesAction | updatePresalesTask | `/ajax/updatePresalesTask.action` | 更新售前任务 |
| terminate2Close | PmClosedLoopAction | terminate2Close | `/ajax/terminate2Close.action` | 终止闭环 |
| deleteFile | UploadAction | deleteFile | `/ajax/deleteFile.action` | 删除文件 |
| assignedRate | ReportAction | assignedRate | `/ajax/assignedRate.action` | 指派率统计 |
| loadLineData | ReportAction | loadLineData | `/ajax/loadLineData.action` | 折线图数据 |
| traceRate | ReportAction | traceRate | `/ajax/traceRate.action` | 跟踪率统计 |
| closeRate | ReportAction | closeRate | `/ajax/closeRate.action` | 闭环率统计 |
| implRate | ReportAction | implRate | `/ajax/implRate.action` | 实施率统计 |
| quality | ReportAction | quality | `/ajax/quality.action` | 质量统计 |
| queryalluser | UserManageAction | queryalluser | `/ajax/queryalluser.action` | 查询所有用户 |
| queryperson | UserManageAction | queryperson | `/ajax/queryperson.action` | 查询人员 |
| checkMergeContract | ProjectAction | checkMergeContract | `/ajax/checkMergeContract.action` | 校验合并合同 |
| checkUsername | UserManageAction | checkUsername | `/ajax/checkUsername.action` | 校验用户名 |
| executeSql | BaseAction | executeSql | `/ajax/executeSql.action` | 执行 SQL |
| importProject | ProjectAction | importProject | `/ajax/importProject.action` | 导入项目 |
| resetPassword | UserManageAction | resetPassword | `/ajax/resetPassword.action` | 重置密码 |
| updateSoftVersion | BaseAction | updateSoftVersion | `/ajax/updateSoftVersion.action` | 更新软件版本 |
| probAudit | ProbManageAction | probAudit | `/ajax/probAudit.action` | 公告审核 |
| probAjax_* | ProbManageAction | * | `/ajax/probAjax_*.action` | 技术公告 AJAX |
| projectAjax_* | ProjectAction | * | `/ajax/projectAjax_*.action` | 项目 AJAX |
| subcontractAjax_* | SubcontractAction | * | `/ajax/subcontractAjax_*.action` | 转包 AJAX |
| presalesAjax_* | PresalesAction | * | `/ajax/presalesAjax_*.action` | 售前 AJAX |
| maintenanceAjax_* | MaintenanceAction | * | `/ajax/maintenanceAjax_*.action` | 维保 AJAX |
| supervisionAjax_* | SupervisionAction | * | `/ajax/supervisionAjax_*.action` | 督导 AJAX |
| warrantyCallbackAjax_* | WarrantyCallbackAction | * | `/ajax/warrantyCallbackAjax_*.action` | 维保回访 AJAX |

> 完整方法级参考见 [PMS-struts Action 方法参考](../../PMS-struts/docs/02-modules/action-methods-reference.md)（25 个 Action 类）。

---

## 3. PMS-springmvc Controller 接口清单

PMS-springmvc 基于 `@Controller` 注解，URL 后缀区分返回类型：`.html`（页面）、`.json`（JSON）、`.xlsx/.xls`（Excel）、`modals/*`（弹窗）。共 19 个 Controller。

### 3.1 项目管理 `/pm/project`

| URL | 方法 | 说明 |
|-----|------|------|
| `/pm/project/home.html` | home | 项目首页 |
| `/pm/project/list.json` | list | 项目列表 |
| `/pm/project/findOne.json` | findOne | 查询单个 |
| `/pm/project/detail.html` | detail | 项目详情 |
| `/pm/project/create.json` | create | 创建项目 |
| `/pm/project/update.json` | update | 更新项目 |
| `/pm/project/delete.json` | delete | 删除项目 |
| `/pm/project/transform.json` | transform | 项目转化 |
| `/pm/project/orderDetail.json` | orderDetail | 订单详情 |
| `/pm/project/syncSMSData.json` | syncSMSData | 同步 SMS 数据 |

### 3.2 项目任务 `/pm/project/task`

继承 AbstractController 通用接口：home/list/findOne/detail/create/update/delete/toImport/importPreview/previewTempTable/dropTempTable/importSubmit/submitTempTable。

### 3.3 项目成员 `/pm/member`

继承 AbstractController 通用接口。

### 3.4 项目管理用户 `/pm/user`

继承 AbstractController 通用接口。

### 3.5 项目资产 `/pm/project/asset`

继承 AbstractController 通用接口。

### 3.6 项目资产泄漏 `/pm/asset/leak`

继承 AbstractController 通用接口。

### 3.7 外派项目 `/pm/dispatch`

| URL | 方法 | 说明 |
|-----|------|------|
| `/pm/dispatch/list.json` | list | 外派列表 |
| `/pm/dispatch/findOne.json` | findOne | 查询外派 |
| `/pm/dispatch/detail.html` | detail | 外派详情 |
| `/pm/dispatch/create.json` | create | 创建外派 |
| `/pm/dispatch/update.json` | update | 更新外派 |
| `/pm/dispatch/delete.json` | delete | 删除外派 |
| `/pm/dispatch/submit.json` | submit | 提交外派 |
| `/pm/dispatch/payment.json` | payment | 付款信息 |
| `/pm/dispatch/generateDispatchSeq.json` | generateDispatchSeq | 生成外派编号 |
| `/pm/dispatch/multiDimInfos.json` | multiDimInfos | 多维信息 |
| `/pm/dispatch/listWithSettleInfo.json` | listWithSettleInfo | 含结算信息列表 |

### 3.8 外派结算 `/pm/settlement`

| URL | 方法 | 说明 |
|-----|------|------|
| `/pm/settlement/list.json` | list | 结算列表 |
| `/pm/settlement/findOne.json` | findOne | 查询结算 |
| `/pm/settlement/detail.html` | detail | 结算详情 |
| `/pm/settlement/create.json` | create | 创建结算 |
| `/pm/settlement/update.json` | update | 更新结算 |
| `/pm/settlement/submit.json` | submit | 提交结算 |
| `/pm/settlement/delete.json` | delete | 删除结算 |
| `/pm/settlement/invoice.json` | invoice | 发票信息 |
| `/pm/settlement/verifyInvoice.json` | verifyInvoice | 发票校验 |
| `/pm/settlement/syncPayment.json` | syncPayment | 同步付款 |

### 3.9 日报 `/pm/daily/report`

继承 AbstractController 通用接口。

### 3.10 服务商 `/pm/facilitator`

继承 AbstractController 通用接口。

### 3.11 公共关联数据 `/pm/common/related`

继承 AbstractController 通用接口。

### 3.12 行业资产 `/af/industry/asset`

继承 AbstractController 通用接口。

### 3.13 行业泄漏 `/af/industry/leak`

继承 AbstractController 通用接口。

### 3.14 行业泄漏预警 `/af/industry/warning`

继承 AbstractController 通用接口。

### 3.15 工作流 `/workflow`

| URL | 方法 | 说明 |
|-----|------|------|
| `/workflow/home.html` | home | 工作流首页 |
| `/workflow/list.json` | list | 流程列表 |
| `/workflow/infoList.json` | infoList | 信息列表 |
| `/workflow/findOne.json` | findOne | 查询流程 |
| `/workflow/findTask.json` | findTask | 查询任务 |
| `/workflow/checkTask.json` | checkTask | 校验任务 |
| `/workflow/complete.json` | complete | 完成任务 |
| `/workflow/completeBatch.json` | completeBatch | 批量完成 |
| `/workflow/evaluateBatch.json` | evaluateBatch | 批量评价 |
| `/workflow/closeProcess.json` | closeProcess | 关闭流程 |
| `/workflow/withdraw.json` | withdraw | 撤回 |
| `/workflow/startProcess.json` | startProcess | 启动流程 |
| `/workflow/completeByKey.json` | completeByKey | 按 key 完成 |
| `/workflow/revokeProcess.json` | revokeProcess | 撤销流程 |

### 3.16 工作台 `/workflow/workbench`

| URL | 方法 | 说明 |
|-----|------|------|
| `/workflow/workbench/listView.html` | listView | 工作台视图 |
| `/workflow/workbench/listToDoTask.json` | listToDoTask | 待办任务 |
| `/workflow/workbench/listOthersTask.json` | listOthersTask | 他人任务 |
| `/workflow/workbench/finishedTaskList.json` | finishedTaskList | 已办任务 |

### 3.17 Struts API 桥接 `/api`

| URL | 方法 | 说明 |
|-----|------|------|
| `/api/departmentList.json` | departmentList | 部门列表 |
| `/api/companyList.json` | companyList | 公司列表 |
| `/api/basicDataByType.json` | basicDataByType | 按类型查基础数据 |

### 3.18 AbstractController 通用接口

所有继承 AbstractController 的 Controller 均提供以下通用接口：

| URL 后缀 | 方法 | 说明 |
|---------|------|------|
| `/home.html` | home | 模块首页 |
| `/list.json` | list | 列表查询 |
| `/findOne.json` | findOne | 单条查询 |
| `/detail.html` | detail | 详情页 |
| `/create.json` | create | 创建 |
| `/update.json` | update | 更新 |
| `/delete.json` | delete | 删除 |
| `/toImport.html` | toImport | 导入页 |
| `/importPreview.json` | importPreview | 导入预览 |
| `/previewTempTable.json` | previewTempTable | 预览临时表 |
| `/dropTempTable.json` | dropTempTable | 删除临时表 |
| `/importSubmit.json` | importSubmit | 提交导入 |
| `/submitTempTable.json` | submitTempTable | 提交临时表 |

> 完整 Controller 清单见 [pms-springmvc.md](../02-模块/pms-springmvc.md)。

---

## 4. PMS-activiti 工作流 API 清单

PMS-activiti 模块基于 Activiti 5.23.0，提供 5 个 Controller 和 7 大 Service。

### 4.1 Controller 接口

| Controller | URL 前缀 | 主要方法 | 说明 |
|-----------|---------|---------|------|
| ModelController | `/activiti/model` | list/create/update/delete/deploy | 流程模型管理 |
| ProcessDefinitionController | `/activiti/procdef` | list/deploy | 流程定义管理 |
| ProcessInstanceController | `/activiti/procinst` | list/start/delete/suspend | 流程实例管理 |
| TaskController | `/activiti/task` | todoTask/endTask/claim/unclaim/delegateTask/transferTask/revoke/withdrawTask/jumpTargetTask | 任务管理 |
| WorkFlowSubModalController | `/activiti/submodal` | * | 流程子窗口 |

### 4.2 Activiti Service API

| Service | 主要方法 | 说明 |
|---------|---------|------|
| RepositoryService | createDeployment/deleteDeployment/createProcessDefinitionQuery | 流程定义部署与查询 |
| RuntimeService | startProcessInstanceById/startProcessInstanceByKey/signal/deleteProcessInstance | 流程实例运行时操作 |
| TaskService | complete/claim/unclaim/delegate/resolve/setVariable | 任务操作 |
| HistoryService | createHistoricProcessInstanceQuery/createHistoricTaskInstanceQuery | 历史查询 |
| ManagementService | executeCommand/executeJob | 命令执行与作业管理 |
| IdentityService | createUser/createGroup/membership | 用户身份管理 |
| FormService | getStartFormData/getTaskFormData/submitStartFormData/submitTaskFormData | 表单服务 |

### 4.3 自定义 Command

| Command | 说明 |
|---------|------|
| RevokeTaskCmd | 撤销任务 |
| WithdrawTaskCmd | 撤回任务 |
| JumpTaskCmdService | 跳转任务 |
| ProcessService | 流程服务（转办、任务查询） |

### 4.4 BPMN 流程定义

| 流程 Key | 说明 |
|---------|------|
| CallBack | 回访流程 |
| PmClosedLoop | 闭环流程 |
| Presales | 售前流程 |
| Subcontract | 转包流程 |
| Subcontract2 | 转包流程2 |
| SubcontractCallBack | 转包回访流程 |
| testprocess | 测试流程（activitiReview） |

> 完整文档见 [pms-activiti.md](../02-模块/pms-activiti.md)。

---

## 5. PMS-ext-d365 D365 集成接口清单

PMS-ext-d365 模块通过 `D365Api` 静态工具类集成 D365 ERP，采用 OAuth2 client_credentials 认证。

| API 方法 | D365 端点 | 说明 |
|---------|----------|------|
| D365Api.getToken | tokenUrl（含 appId 占位符） | 获取 OAuth2 Token（带缓存） |
| D365Api.pushPurchaseOrder | `/api/services/IWS_InterfaceInboundServiceGroup/CreatePurchTable/create` | 推送采购订单 |
| D365Api.pushPurchaseReceipt | `/api/services/IWS_InterfaceInboundServiceGroup/CreatePurchPackingSlip/create` | 推送采购收货 |
| D365Api.pushContractAcceptanceDeliveryInfo | paymentSchedUrl | 推送合同验收交付信息 |
| D365Api.fillPurchaseUnitBase | - | 填充采购单位基础信息 |

**认证方式**：OAuth2 client_credentials，Token 缓存于 `cachedToken`，过期自动刷新。

**数据表**：`dp_erp_purchase_order_header`、`dp_erp_purchase_order_line`、`dp_erp_purchase_receipt_header`、`dp_erp_purchase_receipt_line`。

> 完整文档见 [pms-ext-d365.md](../02-模块/pms-ext-d365.md)。

---

## 6. PMS-ext-fp FP 集成接口清单

PMS-ext-fp 模块通过 `FPApi` 静态工具类集成 FP 财务平台，支持多种认证方式（bearer/header/query/cookie）。

| API 方法 | 说明 |
|---------|------|
| FPApi.getToken | 获取 Token（带缓存，支持多种认证类型） |
| FPApi.postElectronicInvoice | 批量发票查验（支持单条/批量/文件上传） |
| FPApi.pushListData | 推送列表数据（按速率限制拆分） |
| FPApi.pushSingleData | 推送单条数据 |
| FPApi.postForm | 以表单形式 POST 请求 |
| FPApi.postBody | 以 JSON Body 形式 POST 请求 |
| FPApi.get | 发送 GET 请求 |
| FPApi.request | 通用请求方法（支持 OkHttp/HttpClient/Hutool 三种实现） |

**配置项**：`sys.fp.api` 配置 key，支持 `serviceUrl`、`tokenUrl`、`archiveUrl`、`ssoUrl`、`authType`、`authKey`、`appId`、`clientSecret`、`clientId`、`resource`、`grantType` 等。

**发票推送 Job**：`DispatchSettlementInvoiceToFPJob` 定时将外派结算发票推送至 FP 邮箱（通过邮件模板 `pm.dispatch.settlement.invoice.to.fp.mail`）。

> 完整文档见 [pms-ext-fp.md](../02-模块/pms-ext-fp.md)。

---

## 7. SPMS Action 接口清单

SPMS 通过 `struts-sys.xml` 配置 Action 映射，URL 以 `.action` 结尾。命名空间分组如下：

### 7.1 命名空间 `/`（login 包）— 登录注销

| Action 名称 | 类 | 方法 | URL | 说明 |
|------------|-----|------|-----|------|
| Login | LoginAction | execute | `/Login.action` | 登录 |
| Logout | LoginAction | logout | `/Logout.action` | 注销 |
| reset | LoginAction | reset | `/reset.action` | 重置 |

### 7.2 命名空间 `/sys`（main 包）— 主系统业务

| Action 名称 | 类 | 方法 | URL | 说明 |
|------------|-----|------|-----|------|
| DepartmentManager | DepartmentManagerAction | execute | `/sys/DepartmentManager.action` | 部门管理 |
| DepartmentRefresh | DepartmentManagerAction | refresh | `/sys/DepartmentRefresh.action` | 部门刷新 |
| UserManage | UserManageAction | execute | `/sys/UserManage.action` | 用户管理 |
| UserManageX | UserManageAction | * | `/sys/UserManageX_*.action` | 用户管理扩展 |
| List/ToAdd/Add/ToUpdate/Update | AddresseeAction | * | `/sys/*.action` | 收件人管理 |
| warehouseList | WarehouseAction | execute | `/sys/warehouseList.action` | 库房列表 |
| toAddWarehouse/addWarehouse | WarehouseAction | * | `/sys/*Warehouse.action` | 新增库房 |
| toUpdateWarehouse/updateWarehouse/deleteWarehouse | WarehouseAction | * | `/sys/*Warehouse.action` | 修改/删除库房 |
| Tain/TainX | TainAction | * | `/sys/Tain*.action` | 维保类型 |
| Serve/ServeX | ServeAction | * | `/sys/Serve*.action` | 服务类型 |
| Back/BackX | BackAction | * | `/sys/Back*.action` | 返回类型 |
| Spare/SpareX | SpareAction | * | `/sys/Spare*.action` | 备件报表 |
| Change/afreshChange/approve/receive | SparePartsChangeAction | * | `/sys/Change*.action` | 备件转移 |
| ToHeXiao/ToSureHeXiao/ChangeX | SparePartsChangeAction | * | `/sys/*.action` | 核销管理 |
| RMA/RmaRoleAudit/QaRoleAudit | RmaApplicantAction | * | `/sys/RMA*.action` | RMA 申请 |
| SubmitBack/afreshApply/sendSure2Rma/batchRMA | RmaApplicantAction | * | `/sys/*.action` | RMA 流程 |
| uploadRMAInformation/RMAX/DelRsi/toEditRsi/batchApply/hexiao | RmaApplicantAction | * | `/sys/*.action` | RMA 操作 |
| ProductTemplateDownload | RmaApplicantAction | download | `/sys/ProductTemplateDownload.action` | 产品模板下载 |
| SpareParts/BrwTemplateDownload/afreshSubmit/sendSure | SparePartsApplicantAction | * | `/sys/Spare*.action` | 备件申请 |
| AccessoryDownLoad | SparePartsApplicantAction | download | `/sys/AccessoryDownLoad.action` | 附件下载 |
| WarrantyQuery/QuerySubmit/RefreshWarranty | WarrantyAction | * | `/sys/Warranty*.action` | 质保查询 |
| queryWarranty/updateWarranty/toUpdateWarranty | WarrantyAction | * | `/sys/*Warranty.action` | 质保操作 |
| InsteadSure | SparePartsApplicantAction | insteadSure | `/sys/InsteadSure.action` | 代填确认 |
| toSparePartAttriList/toEditSparePartAttri/editSparePartAttri/checkHistory | SparePartsApplicantAction | * | `/sys/Spare*.action` | 备件属性 |
| SparePartsX/BrwAudit | SparePartsApplicantAction | * | `/sys/*.action` | 备件审核 |
| applicant/OS/CIAO | OsApplicantAction | * | `/sys/*.action` | OS 申请 |
| toInventoryQuery/ChangeInventory/inventoryQuery | WarehouseAction | * | `/sys/*Inventory*.action` | 库存查询 |
| toUpdateDetailInfo/updateDetailInfo/checkInventory | WarehouseAction | * | `/sys/*.action` | 库存明细 |
| storageLocationTransfer/toTransfer | WarehouseAction | * | `/sys/*Transfer.action` | 库位转移 |
| Password/PasswordEditLogin | PasswordGetinfo | * | `/sys/Password*.action` | 密码管理 |
| toDataImport/dataImport | WarehouseAction | * | `/sys/*DataImport.action` | 数据导入 |
| uploadSealInfo/certificate | CertificateAction | * | `/sys/*.action` | 证书/印章 |
| shipment_* | ShipmentAction | * | `/sys/shipment_*.action` | 发货管理 |
| fillDutyPerson/fillBarCode/fillKuCun | WarehouseAction | * | `/sys/fill*.action` | 填充数据 |
| batchAddChangePart | SparePartsChangeAction | batchAdd | `/sys/batchAddChangePart.action` | 批量新增转移备件 |

### 7.3 命名空间 `/s`（json 包）— AJAX JSON 接口

| Action 名称 | 类 | 方法 | URL | 说明 |
|------------|-----|------|-----|------|
| syncTask | BaseAction | syncTask | `/s/syncTask.action` | 同步任务 |
| deleteReapplicant | RmaApplicantAction | deleteReapplicant | `/s/deleteReapplicant.action` | 删除申请人 |
| getAddreList/getAddre | AddresseeAction | * | `/s/getAddre*.action` | 收件人查询 |
| douser/dopassword/dorma | UserManageAction | * | `/s/do*.action` | 用户操作 |
| checkIncident/docode/getcode | LoginAction | * | `/s/*.action` | 校验/验证码 |
| doos/doserve/dotain/doback | WarrantyAction | * | `/s/do*.action` | 类型校验 |
| checkContract | ShipmentAction | checkContract | `/s/checkContract.action` | 合同校验 |
| changeRmaBackSpare | RmaApplicantAction | changeRmaBackSpare | `/s/changeRmaBackSpare.action` | 更换 RMA 退回备件 |
| toMES | RmaApplicantAction | toMES | `/s/toMES.action` | 推送 MES |
| backApproved | RmaApplicantAction | backApproved | `/s/backApproved.action` | 退回已审批 |
| *Json | 各 Action | * | `/s/*Json.action` | JSON 通用接口 |
| sparePartsUpload | SparePartsApplicantAction | upload | `/s/sparePartsUpload.action` | 备件上传 |

### 7.4 命名空间 `/sub`（sub 包）— 子页面

| Action 名称 | 类 | 方法 | URL | 说明 |
|------------|-----|------|-----|------|
| DeleteUploadFile/DeleteAccessory/checkAccessory/deleteAction | 各 Action | * | `/sub/*.action` | 文件/附件操作 |

### 7.5 命名空间 `/mark`（mark 包）— 马甲系统

| Action 名称 | 类 | 方法 | URL | 说明 |
|------------|-----|------|-----|------|
| RMA | RmaApplicantAction | execute | `/mark/RMA.action` | RMA 列表（马甲版） |
| SpareParts | SparePartsApplicantAction | execute | `/mark/SpareParts.action` | 备件申请列表（马甲版） |
| Change | SparePartsChangeAction | execute | `/mark/Change.action` | 备件转移列表（马甲版） |
| qualityIn | WarehouseAction | inventoryQuery | `/mark/qualityIn.action` | 质量入库 |
| qualityOut | WarehouseAction | inventoryQuery | `/mark/qualityOut.action` | 质量出库 |
| qualityRule | WarehouseAction | inventoryQuery | `/mark/qualityRule.action` | 质量规则 |
| shipment_* | ShipmentAction | * | `/mark/shipment_*.action` | 发货查询（马甲版） |

### 7.6 命名空间 `/download`（download 包）— 文件下载

| Action 名称 | 类 | 方法 | URL | 说明 |
|------------|-----|------|-----|------|
| SwfUploadFile | UploadAction | swfUpload | `/download/SwfUploadFile.action` | SWF 上传 |
| UploadRmaFile | RmaApplicantAction | uploadRma | `/download/UploadRmaFile.action` | RMA 文件上传 |
| uploadems | WarrantyAction | uploadEms | `/download/uploadems.action` | EMS 上传 |
| uploadrmaems | RmaApplicantAction | uploadRmaEms | `/download/uploadrmaems.action` | RMA EMS 上传 |
| toAddSparePart/addSparePart | SparePartsApplicantAction | * | `/download/*SparePart.action` | 备件操作 |
| toCheckSpare/checkSpare | SparePartsApplicantAction | * | `/download/*Spare.action` | 备件校验 |
| toModifySpare/modifySpare/toModifySparePart/modifySparePart | SparePartsApplicantAction | * | `/download/*.action` | 备件修改 |
| queryCertificate | CertificateAction | query | `/download/queryCertificate.action` | 证书查询 |
| RepairReportDownload | SpareAction | download | `/download/RepairReportDownload.action` | 修复报告下载 |
| resetSocket | BaseAction | resetSocket | `/download/resetSocket.action` | 重置 Socket |

> 完整模块文档见 [SPMS 模块文档目录](../../SPMS/docs/02-模块/)（备件申请、库房、发运、用户、质保、报表、马甲 7 个模块）。

---

## 8. 外部系统集成接口清单

PMS 与 SPMS 通过多数据源（RoutingDataSource）和 HTTP API 与外部系统集成。

### 8.1 D365 ERP 集成接口

| 接口 | 调用方 | 协议 | 认证 | 说明 |
|------|--------|------|------|------|
| 获取 Token | PMS-ext-d365 | HTTPS POST | OAuth2 client_credentials | Token 缓存，过期自动刷新 |
| 推送采购订单 | PMS-ext-d365 | HTTPS POST | Bearer Token | `CreatePurchTable/create` |
| 推送采购收货 | PMS-ext-d365 | HTTPS POST | Bearer Token | `CreatePurchPackingSlip/create` |
| 推送合同验收交付信息 | PMS-ext-d365 | HTTPS POST | Bearer Token | paymentSchedUrl |
| 填充采购单位基础信息 | PMS-ext-d365 | HTTPS POST | Bearer Token | 内部调用 |
| D365 全量同步 Job | PMS-springmvc | 内部 | - | `D365DataJob` 同步供应商、付款信息 |

**数据源**：PMS 通过 `dataSourceD365`（SQL Server AXDB）访问 D365 数据库。

### 8.2 MES 系统集成接口

| 接口 | 调用方 | 协议 | 说明 |
|------|--------|------|------|
| RMA 信息推送 MES | SPMS | SOAP/WebService | `SPMS2MESService.RMAInfoSPMS2MES(sheetID)` 推送 RMA 申请至 MES |
| MES 发货信息同步 | SPMS | 定时任务 | `SyncMesShipmentInfoJob` 增量同步 ShipmentBarcode |
| 每日维保信息刷新 | SPMS | 定时任务 | `DailyWarrantyInfoTask` 刷新 warranty_info |

**数据源**：SPMS 通过 `firebird/mes`（SQL Server R2EMES5SQL）访问 MES 数据库。

**WebService 端点**：`SAP2MESServices`（`BasicHttpBinding_ITAP2MESServicesStub`），通过 `org.tempuri` 包生成的 Stub 调用。

### 8.3 SAP 系统集成接口

| 接口 | 调用方 | 协议 | 说明 |
|------|--------|------|------|
| SAP 合同头同步 | PMS-springmvc | 数据库直连 | `SMSDataJob` 同步 `OfstContractHeadSAP` |
| SAP 数据查询 | SPMS | 数据库直连 | 通过 `dataSourceSAP`（SQL Server DIPULive） |

**数据源**：SPMS 通过 `sap`（SQL Server DIPULive）访问 SAP 数据库；PMS 通过 `dataSourceSAP` 访问。

### 8.4 FP 财务平台集成接口

| 接口 | 调用方 | 协议 | 认证 | 说明 |
|------|--------|------|------|------|
| 获取 Token | PMS-ext-fp | HTTPS GET | 多种（bearer/header/query/cookie） | Token 缓存 |
| 批量发票查验 | PMS-ext-fp | HTTPS POST | Bearer Token | `postElectronicInvoice` 支持文件上传 |
| 推送列表数据 | PMS-ext-fp | HTTPS POST | Bearer Token | `pushListData` 按速率限制拆分 |
| 推送单条数据 | PMS-ext-fp | HTTPS POST | Bearer Token | `pushSingleData` |
| 外派结算发票推送 FP 邮箱 | PMS-springmvc | SMTP 邮件 | - | `DispatchSettlementInvoiceToFPJob` 定时推送 |
| 转包发票推送 FP | PMS-struts | SMTP 邮件 | - | `SubcontractInvoiceToFP` |

**配置项**：`sys.fp.api` 配置 key，`sys.erms.api.config` 控制 Job 启用。

### 8.5 CRM 系统集成接口

| 接口 | 调用方 | 协议 | 说明 |
|------|--------|------|------|
| CRM 数据同步 | PMS-struts | 定时任务 | `CrmSyncTask` 接口（含 syncDataBefore/Success/Fail 钩子） |
| 从 CRM 拉取任务 | PMS-struts | 定时任务 | `DefaultPullTaskFormCRM` |
| 推送任务至 CRM | PMS-struts | 定时任务 | `DefaultPushTaskFormCRM` |
| 默认同步任务 | PMS-struts | 定时任务 | `DefaultSyncTaskFormCRM` |
| 从 CRM 获取计划 | PMS-struts | 定时任务 | `PlanGetFromCRM` |
| 从 CRM 获取数据 | PMS-struts | 定时任务 | `GainDataFromCRM` |
| CRM 全量同步 | PMS-springmvc | 定时任务 | `SMSDataJob` 同步 `ProjectProduct`、`AfPrjProperty` |

**数据源**：PMS 通过 `dataSourceCRM` 访问 CRM 数据库。

**同步日志**：通过 `insert_fnd_data_refresh_log` 记录同步日志，含 `refreshTaskName`、`dataFrom`、`dataTo`、`refreshFrom`、`refreshTo`、`refreshState`、`refreshException` 字段。

### 8.6 SMS 系统集成接口

| 接口 | 调用方 | 协议 | 说明 |
|------|--------|------|------|
| SMS 数据同步 | PMS-springmvc | 定时任务 | `SMSDataJob` 全量同步 |
| 项目同步 SMS 数据 | PMS-springmvc | 内部调用 | `ProjectController.syncSMSData` |

**数据源**：PMS 通过 `dataSourceSMS`（MySQL dpsms）访问 SMS 数据库；SPMS 通过 `sms`（MySQL dpsms）访问。

### 8.7 EHR 系统集成接口

| 接口 | 调用方 | 协议 | 说明 |
|------|--------|------|------|
| EHR 数据同步 | PMS | 数据库直连 | 通过 `dataSourceEHR` 访问 EHR 数据库 |

### 8.8 致远 OA 集成接口

| 接口 | 调用方 | 协议 | 说明 |
|------|--------|------|------|
| 统一任务推送致远 OA | PMS-activiti | 内部调用 | `UnifyTaskPushListener` 监听 `dp_act_unify_task` 表推送任务 |

---

## 9. 接口统计

### 9.1 按系统统计

| 系统 | 接口分类 | 接口数量（约） |
|------|---------|---------------|
| PMS | struts Action | 200+ |
| PMS | springmvc Controller | 150+ |
| PMS | activiti 工作流 API | 30+ |
| PMS | ext-d365 集成 | 4 |
| PMS | ext-fp 集成 | 5+ |
| SPMS | struts Action | 150+ |
| 外部系统 | 集成接口 | 15+ |
| **合计** | - | **550+** |

### 9.2 按命名空间统计（PMS-struts）

| 命名空间 | 说明 | Action 数量（约） |
|---------|------|------------------|
| `/` | 登录注销 | 4 |
| `/base` | 基础数据维护 | 6 |
| `/module` | 项目主业务 | 17 |
| `/module/sub` | 弹窗子页面 | 5 |
| `/work` | 工作流 | 1 |
| `/sys` | 系统主页面 | 3 |
| `/ajax` | AJAX 接口 | 25+ |

### 9.3 按命名空间统计（SPMS）

| 命名空间 | 说明 | Action 数量（约） |
|---------|------|------------------|
| `/` | 登录注销 | 3 |
| `/sys` | 主系统业务 | 80+ |
| `/s` | AJAX JSON 接口 | 20+ |
| `/sub` | 子页面 | 4 |
| `/mark` | 马甲系统 | 7 |
| `/download` | 文件下载 | 12 |

### 9.4 按外部系统统计

| 外部系统 | 集成方式 | 接口数量 |
|---------|---------|---------|
| D365 ERP | HTTPS API + 数据库直连 | 5+ |
| MES | SOAP WebService + 数据库直连 | 3 |
| SAP | 数据库直连 | 2 |
| FP 财务平台 | HTTPS API + SMTP 邮件 | 5+ |
| CRM | 数据库直连 + 定时任务 | 6 |
| SMS | 数据库直连 + 定时任务 | 2 |
| EHR | 数据库直连 | 1 |
| 致远 OA | 内部表监听 | 1 |

---

## 10. 相关文档索引

### 10.1 PMS 模块文档

| 文档 | 路径 |
|------|------|
| PMS-struts Action 方法参考 | [../../PMS-struts/docs/02-modules/action-methods-reference.md](../../PMS-struts/docs/02-modules/action-methods-reference.md) |
| PMS-springmvc 模块 | [../02-模块/pms-springmvc.md](../02-模块/pms-springmvc.md) |
| PMS-activiti 模块 | [../02-模块/pms-activiti.md](../02-模块/pms-activiti.md) |
| PMS-ext-d365 模块 | [../02-模块/pms-ext-d365.md](../02-模块/pms-ext-d365.md) |
| PMS-ext-fp 模块 | [../02-模块/pms-ext-fp.md](../02-模块/pms-ext-fp.md) |
| PMS-core 模块 | [../02-模块/core.md](../02-模块/core.md) |
| PMS-security 模块 | [../02-模块/pms-security.md](../02-模块/pms-security.md) |
| PMS-rules 模块 | [../02-模块/pms-rules.md](../02-模块/pms-rules.md) |

### 10.2 SPMS 模块文档

| 文档 | 路径 |
|------|------|
| 备件申请模块 | [../../SPMS/docs/02-模块/spare-parts-application.md](../../SPMS/docs/02-模块/spare-parts-application.md) |
| 库房管理模块 | [../../SPMS/docs/02-模块/warehouse-management.md](../../SPMS/docs/02-模块/warehouse-management.md) |
| 发运管理模块 | [../../SPMS/docs/02-模块/shipment-management.md](../../SPMS/docs/02-模块/shipment-management.md) |
| 用户管理模块 | [../../SPMS/docs/02-模块/user-management.md](../../SPMS/docs/02-模块/user-management.md) |
| 质保管理模块 | [../../SPMS/docs/02-模块/warranty-management.md](../../SPMS/docs/02-模块/warranty-management.md) |
| 报表分析模块 | [../../SPMS/docs/02-模块/report-analysis.md](../../SPMS/docs/02-模块/report-analysis.md) |
| 马甲系统模块 | [../../SPMS/docs/02-模块/mark-system.md](../../SPMS/docs/02-模块/mark-system.md) |

### 10.3 参考文档

| 文档 | 路径 |
|------|------|
| 接口文档模板 | [interface-template.md](interface-template.md) |
| 错误码说明 | [error-codes.md](error-codes.md) |
| 术语表 | [glossary.md](glossary.md) |
| 模块文档模板 | [module-template.md](module-template.md) |
| 数据字典模板 | [data-dictionary-template.md](data-dictionary-template.md) |

---

## 11. 接口调用约定

### 11.1 PMS 双框架路由规则

| URL 模式 | 框架 | 处理器 |
|---------|------|--------|
| `*.action` | Struts2 2.5.30 | StrutsPrepareAndExecuteFilter |
| `*.html/*.json/*.xlsx/*.xls` | Spring MVC 5.3.19 | DispatcherServlet |
| `modals/*` | Spring MVC 5.3.19 | DispatcherServlet |
| `/activiti/*` | Activiti MVC | ActivitiDispatcherServlet |

### 11.2 认证机制

| 系统 | 认证方式 | 说明 |
|------|---------|------|
| PMS | Shiro 1.8.0 + CAS 3.2.2 | 支持 CAS 单点登录，Session 维持 |
| SPMS | 自定义 AuthCheckInterceptor | Session 维持，PwdInterceptor 密码校验 |
| D365 | OAuth2 client_credentials | Token 缓存，过期自动刷新 |
| FP | 多种（bearer/header/query/cookie） | Token 缓存，支持 Cookie |

### 11.3 通用响应格式

**PMS-springmvc 响应格式**（`com.dp.plat.core.vo.Result`）：

```json
{
  "success": true,
  "status": true,
  "data": {},
  "message": "操作成功",
  "code": "200"
}
```

**PMS-ext-d365 响应格式**（`com.dp.plat.pms.extend.d365.model.Response`）：

```json
{
  "code": 200,
  "message": "success",
  "data": [{}]
}
```

**PMS-ext-fp 响应格式**（`com.dp.plat.pms.extend.fp.model.Response`）：

```json
{
  "code": 0,
  "msg": "success",
  "data": [{}],
  "extend": {},
  "status": true
}
```

> 成功码：D365 为 `200`，FP 为 `0` 或 `200`。
