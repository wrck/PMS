# PMS 迁移计划：PMS-struts → PMS-springboot

> 生成日期：2026-06-29
> 基于对两个代码库的完整静态分析

---

## 一、架构对比

| 维度 | PMS-struts (旧) | PMS-springboot (新) |
|------|-----------------|-------------------|
| 框架 | Struts2 + Spring + iBATIS | Spring Boot 3.2.5 + MyBatis-Plus 3.5.6 |
| Java | Java 8 | Java 17 |
| 数据库 | MySQL (dppms_d365) | MySQL (同库) |
| 认证 | CAS SSO + Session | JWT + Shiro |
| ORM | iBATIS XML (846条SQL) | MyBatis-Plus 注解 + 少量XML |
| 前端 | JSP (179个) + Decorator | REST API (前后端分离) |
| 工作流 | Activiti 5 | **未迁移** |
| 定时任务 | Quartz (42个Job) | **未迁移** |
| 邮件 | 自研 SendMailService | **未迁移** |
| 数据同步 | 多数据源(SAP/CRM/OA/EHR/ITR/License/SSE) | **未迁移** |

---

## 二、模块总览与迁移状态

| # | 模块 | Struts Action | SQL数 | Spring Boot Controller | 迁移状态 |
|---|------|--------------|-------|----------------------|---------|
| 1 | 认证登录 | LoginAction | - | AuthController | ✅ 基本完成 |
| 2 | 系统管理-用户 | UserManageAction | 93 | SysUserController | ✅ 基本完成 |
| 3 | 系统管理-角色 | RoleManageAction | (含上) | SysRoleController | ✅ 基本完成 |
| 4 | 系统管理-部门 | DepartmentManageAction | (含上) | SysDeptController | ✅ 基本完成 |
| 5 | 系统管理-基础数据 | BasicDataManageAction | (含上) | BasicDataController | ✅ 基本完成 |
| 6 | 操作日志 | OperateLogAction | (含上) | OperateLogController | ⚠️ 仅列表 |
| 7 | 项目管理 | ProjectAction | 465 | PmsProjectController | ⚠️ 部分完成 |
| 8 | 项目周报 | (ProjectAction内) | (含上) | WeeklyController | ✅ 基本完成 |
| 9 | 项目任务 | (ProjectAction内) | (含上) | ProjectTaskController | ✅ 基本完成 |
| 10 | 项目交付 | (ProjectAction内) | (含上) | ProjectDeliverController | ✅ 基本完成 |
| 11 | 技术公告(Prob) | ProbManageAction | 83 | ProbController | ⚠️ 仅CRUD骨架 |
| 12 | 售前项目 | PresalesAction | 32 | PmsPresalesController | ⚠️ 仅CRUD骨架 |
| 13 | 分包管理 | SubcontractAction | 90 | SubcontractController | ⚠️ 仅CRUD骨架 |
| 14 | 闭环管理 | PmClosedLoopAction | 15 | PmClosedLoopController | ⚠️ 仅CRUD骨架 |
| 15 | 回访问卷 | PmClosedLoopQuesnaireAction | (含上) | **无** | ❌ 未迁移 |
| 16 | 回访管理 | CallBackAction | 15 | CallBackController | ⚠️ 仅CRUD骨架 |
| 17 | 运维管理 | MaintenanceAction | 37 | MaintenanceController | ⚠️ 仅CRUD骨架 |
| 18 | 监理管理 | SupervisionAction | - | SupervisionController | ⚠️ 仅CRUD骨架 |
| 19 | 证书管理 | CertificateAction | 3 | CertificateController | ⚠️ 仅CRUD骨架 |
| 20 | 质保回调 | WarrantyCallbackAction | 19 | **无** | ❌ 未迁移 |
| 21 | 文件管理 | UploadAction | - | FileController | ✅ 基本完成 |
| 22 | 通知管理 | - | - | NotificationController | ✅ 基本完成 |
| 23 | 工作台 | WorkSpaceAction | 18 | WorkSpaceController | ⚠️ 仅骨架 |
| 24 | 报表统计 | ReportAction | 32 | **无** | ❌ 未迁移 |
| 25 | 工作流 | WorkFlowAction | 6 | **无** | ❌ 未迁移(依赖Activiti) |
| 26 | 数据分析 | DataAnalysisAction | - | **无** | ❌ 未迁移 |
| 27 | 缓存管理 | ClusterAction | - | **无** | ❌ 未迁移 |
| 28 | 定时任务 | 42个Job类 | - | **无** | ❌ 未迁移 |
| 29 | 数据同步 | 20+个同步Job | 107+ | **无** | ❌ 未迁移 |
| 30 | 密码管理 | PasswordGetinfo | - | (AuthController内) | ⚠️ 部分 |

---

## 三、各模块详细功能清单

### 模块1：项目管理 (ProjectAction → PmsProjectController)

> 源码：3371行，Struts最核心模块

| # | 方法 | 功能 | Spring Boot 状态 | 备注 |
|---|------|------|-----------------|------|
| 1.1 | execute | 项目列表(权限过滤) | ✅ `GET /list` | 已迁移 |
| 1.2 | insertProject | 创建项目 | ✅ `POST /` | 已迁移 |
| 1.3 | updateProject | 更新项目(含状态流转) | ✅ `PUT /` | 已迁移，部分业务逻辑待补 |
| 1.4 | createCHProject | 创建串货项目 | ✅ `POST /ch` | 已迁移 |
| 1.5 | transferShipment | 转移设备到其他项目 | ✅ `POST /transfer/shipment` | 已迁移 |
| 1.6 | transferProject | 查询可转移目标项目 | ✅ `GET /transfer/list` | 已迁移 |
| 1.7 | exportSpotCheck | 现场验货单下载 | ⚠️ `GET /{id}/export-spot-check` | 接口已建，逻辑TODO |
| 1.8 | exportOverWarrantyRemind | 超期保修提醒导出 | ⚠️ `GET /{id}/export-over-warranty-remind` | 接口已建，逻辑TODO |
| 1.9 | importSpotCheckIgnoreItem | 导入现场验货单 | ⚠️ `POST /import-spot-check-ignore-item` | 接口已建，逻辑TODO |
| 1.10 | checkOrderData | 查询设备清单 | ✅ `GET /{id}/order-data` | 已迁移 |
| 1.11 | checkRealOrderData | 查询实施发货设备清单 | ✅ `GET /{id}/real-order-data` | 已迁移 |
| 1.12 | projectLeaseLine | 查询租赁配置清单 | ⚠️ `GET /{id}/lease-line` | 接口已建，逻辑TODO |
| 1.13 | projectProductConfigLevelInfo | 查询配置关系清单 | ⚠️ `GET /{id}/config-level-info` | 接口已建，逻辑TODO |
| 1.14 | checkShipmentInfo | 查询发货序列号 | ✅ `GET /{id}/shipment-info` | 已迁移 |
| 1.15 | deleteShipmentInfo | 删除发货安装信息 | ✅ `DELETE /{id}/shipment-info` | 已迁移 |
| 1.16 | checkSoftVersion | 查询设备软件版本 | ✅ `GET /{id}/soft-version` | 已迁移 |
| 1.17 | updateSoftVersion | AJAX更新设备软件版本 | ✅ `PUT /soft-version` | 已迁移 |
| 1.18 | checkhistsoftversion | 获取软件版本历史数据 | ✅ `GET /soft-version/history` | 已迁移 |
| 1.19 | projectMaintenance | 获取项目维护记录 | ✅ `GET /{id}/maintenance` | 已迁移 |
| 1.20 | createProjectMaintenance | 创建/编辑维护记录 | ✅ `POST /maintenance` | 已迁移 |
| 1.21 | editProjectPlan | 制定/修改工程计划 | ⚠️ `POST /{id}/plan` | 接口已建，逻辑TODO |
| 1.22 | uploadDeliverableFile | 上传工程交付件 | ⚠️ | 未迁移 |
| 1.23 | deleteDeliverById | 删除工程交付件 | ⚠️ | 未迁移 |
| 1.24 | backToLastStep | 项目回退到上一步 | ✅ `POST /back-to-last-step` | 已迁移(简化版) |
| 1.25 | createWeekly | 创建周报 | ✅ WeeklyController | 已迁移 |
| 1.26 | saveWeekly | 保存周报草稿 | ✅ WeeklyController | 已迁移 |
| 1.27 | submitWeekly | 提交周报 | ✅ WeeklyController | 已迁移 |
| 1.28 | updateWeekly | 更新周报 | ✅ WeeklyController | 已迁移 |
| 1.29 | toUploadFile | 进入文件上传页面 | ⚠️ | JSP页面，API化后不需要 |
| 1.30 | toUploadDeliverableFile | 进入交付件上传页面 | ⚠️ | JSP页面，API化后不需要 |
| 1.31 | UploadFile | 周报附件上传 | ✅ FileController | 已迁移 |
| 1.32 | downloadFile | 下载文件 | ✅ FileController | 已迁移 |
| 1.33 | deleteFile | 删除文件 | ✅ FileController | 已迁移 |
| 1.34 | feedback | 周报回复 | ✅ WeeklyController | 已迁移 |
| 1.35 | instruction | 项目批示 | ⚠️ | 未迁移 |
| 1.36 | queryalluser | 根据角色查询用户 | ✅ `GET /users` | 已迁移 |
| 1.37 | queryperson | 查询项目干系人 | ✅ `GET /persons` | 已迁移 |
| 1.38 | updateprojectisback | 项目回退流程(含邮件) | ✅ `POST /update-isback` | 已迁移(无邮件) |
| 1.39 | createMember | 创建项目成员 | ✅ `POST /member` | 已迁移 |
| 1.40 | updateMember | 更新项目成员 | ⚠️ | 未迁移 |
| 1.41 | saveInstallAdress | 保存安装地址 | ⚠️ | 未迁移 |
| 1.42 | updateProjectExecutionState | 更新项目实施状态 | ⚠️ | 未迁移 |
| 1.43 | toMergeOrBranch | 合同拆分合并页面 | ⚠️ | 未迁移 |
| 1.44 | checkMergeContract | 查询要合并的合同 | ⚠️ | 未迁移 |
| 1.45 | mergeContract | 合并操作 | ⚠️ | 未迁移 |
| 1.46 | branchContract | 项目拆分 | ⚠️ | 未迁移 |
| 1.47 | queryDpNoRoleUser | 查询部门无角色用户 | ✅ `GET /users/no-role` | 已迁移 |
| 1.48 | batchChangeMember | 批量变更项目成员 | ✅ `POST /batch-change-member` | 已迁移 |
| 1.49 | importProject | 批量创建项目(Excel) | ✅ `POST /import` | 已迁移 |
| 1.50 | clearProject | 批量删除/无效化项目 | ✅ `POST /clear` | 已迁移 |
| 1.51 | queryProjectNotification | 获取项目系统通知 | ⚠️ | 未迁移 |
| 1.52 | problemTicket | 问题工单 | ⚠️ | 未迁移 |
| 1.53 | licenseInfo | License信息 | ⚠️ | 未迁移 |

### 模块2：技术公告 (ProbManageAction → ProbController)

> 源码：1726行，业务复杂度高

| # | 方法 | 功能 | Spring Boot 状态 | 备注 |
|---|------|------|-----------------|------|
| 2.1 | list | 技术公告列表 | ⚠️ `GET /list` | 仅基础分页 |
| 2.2 | input | 新建/编辑页面 | ⚠️ `GET /{id}` | 仅基础查询 |
| 2.3 | delete | 删除技术公告 | ⚠️ `DELETE /{id}` | 仅基础删除 |
| 2.4 | bacthDeleteProbRestores | 批量删除恢复任务 | ❌ | 未迁移 |
| 2.5 | edit | 编辑技术公告 | ⚠️ `PUT /` | 仅基础更新 |
| 2.6 | checkProject | 检查关联项目 | ❌ | 未迁移 |
| 2.7 | checkSubProject | 检查子项目 | ❌ | 未迁移 |
| 2.8 | releaseTask | 发布任务 | ❌ | 未迁移 |
| 2.9 | managePrivateTask | 管理私有任务 | ❌ | 未迁移 |
| 2.10 | updatePrivateTask | 更新私有任务 | ❌ | 未迁移 |
| 2.11 | weeklyUpload | 周报上传 | ❌ | 未迁移 |
| 2.12 | manageAllTask | 管理全部任务 | ❌ | 未迁移 |
| 2.13 | updateRestoreTask | 更新恢复任务 | ❌ | 未迁移 |
| 2.14 | save | 保存技术公告 | ⚠️ `POST /` | 仅基础保存 |
| 2.15 | update | 更新技术公告 | ⚠️ `PUT /` | 仅基础更新 |
| 2.16 | audit | 审核技术公告 | ❌ | 未迁移 |
| 2.17 | export | 导出技术公告 | ❌ | 未迁移 |
| 2.18 | importSoftVersion | 导入软件版本 | ❌ | 未迁移 |
| 2.19 | toCheckSoftVersion | 检查软件版本页面 | ❌ | 未迁移 |
| 2.20 | submitSoftVersion | 提交软件版本 | ❌ | 未迁移 |
| 2.21 | parserSoftVersion | 解析软件版本 | ❌ | 未迁移 |
| 2.22 | parserOldSoftVersion | 解析旧软件版本 | ❌ | 未迁移 |
| 2.23 | statistics | 统计分析 | ❌ | 未迁移 |
| 2.24 | affectedProjectSoftVersion | 受影响项目软件版本 | ❌ | 未迁移 |
| 2.25 | readSure | 确认阅读 | ❌ | 未迁移 |
| 2.26 | readLog | 阅读日志 | ❌ | 未迁移 |
| 2.27 | listProductItem | 产品物料列表 | ❌ | 未迁移 |
| 2.28 | listProbProduct | 公告产品列表 | ❌ | 未迁移 |
| 2.29 | inputProbProduct | 编辑公告产品 | ❌ | 未迁移 |
| 2.30 | saveProbProduct | 保存公告产品 | ❌ | 未迁移 |
| 2.31 | importProbProduct | 导入公告产品 | ❌ | 未迁移 |
| 2.32 | listComponent | 组件列表 | ❌ | 未迁移 |
| 2.33 | inputComponent | 编辑组件 | ❌ | 未迁移 |
| 2.34 | saveComponent | 保存组件 | ❌ | 未迁移 |
| 2.35 | importComponent | 导入组件 | ❌ | 未迁移 |

### 模块3：售前项目 (PresalesAction → PmsPresalesController)

> 源码：1195行

| # | 方法 | 功能 | Spring Boot 状态 | 备注 |
|---|------|------|-----------------|------|
| 3.1 | list | 售前项目列表 | ⚠️ `GET /list` | 仅基础分页 |
| 3.2 | input | 新建/编辑页面 | ⚠️ `GET /{id}` | 仅基础查询 |
| 3.3 | apply | 发起申请 | ⚠️ `POST /{id}/start-flow` | 骨架 |
| 3.4 | read | 查看详情 | ⚠️ `GET /{id}` | 仅基础查询 |
| 3.5 | aduit | 审核 | ⚠️ `POST /{id}/approve` | 骨架 |
| 3.6 | smaduit | 服务经理审核 | ❌ | 未迁移 |
| 3.7 | pmaduit | 项目经理审核 | ❌ | 未迁移 |
| 3.8 | updateTask | 更新任务 | ❌ | 未迁移 |
| 3.9 | emaduit | 工程部审核 | ❌ | 未迁移 |
| 3.10 | callback | 回调 | ❌ | 未迁移 |
| 3.11 | shipmentInfo | 发货信息 | ❌ | 未迁移 |
| 3.12 | lend2SaleInfo | 借转销信息 | ❌ | 未迁移 |
| 3.13 | lend2RmaInfo | 借转退信息 | ❌ | 未迁移 |
| 3.14 | tempAuthInfo | 临时授权信息 | ❌ | 未迁移 |
| 3.15 | terminate2Close | 终止关闭 | ❌ | 未迁移 |
| 3.16 | syncOaData | 同步OA数据 | ❌ | 未迁移 |
| 3.17 | upload | 上传附件 | ❌ | 未迁移 |
| 3.18 | deleteDeliverById | 删除交付件 | ❌ | 未迁移 |
| 3.19 | updateDeliverById | 更新交付件 | ❌ | 未迁移 |
| 3.20 | updateConfirmFiles | 更新确认文件 | ❌ | 未迁移 |
| 3.21 | exportPresales | 导出售前项目 | ❌ | 未迁移 |

### 模块4：分包管理 (SubcontractAction → SubcontractController)

> 源码：1994行，含子实体15个

| # | 方法 | 功能 | Spring Boot 状态 | 备注 |
|---|------|------|-----------------|------|
| 4.1 | view | 分包详情页 | ⚠️ `GET /{id}` | 仅基础查询 |
| 4.2 | list | 分包列表 | ⚠️ `GET /list` | 仅基础分页 |
| 4.3 | input | 新建/编辑页面 | ⚠️ | 骨架 |
| 4.4 | create | 创建分包 | ⚠️ `POST /` | 仅基础保存 |
| 4.5 | apply | 发起申请 | ❌ | 未迁移 |
| 4.6 | audit | 审核 | ❌ | 未迁移 |
| 4.7 | close | 关闭 | ❌ | 未迁移 |
| 4.8 | startCallBackFlow | 发起回调流程 | ❌ | 未迁移 |
| 4.9 | querySubcontractCallback | 查询分包回调 | ❌ | 未迁移 |
| 4.10 | chooseSubcontractProject | 选择分包项目 | ❌ | 未迁移 |
| 4.11 | refreshSubcontractProject | 刷新分包项目 | ❌ | 未迁移 |
| 4.12 | chooseShipmentInfo | 选择发货信息 | ❌ | 未迁移 |
| 4.13 | querySubcontractLine | 查询分包行 | ❌ | 未迁移 |
| 4.14 | querySubcontractDeliver | 查询分包交付 | ❌ | 未迁移 |
| 4.15 | deleteSubcontractDeliver | 删除分包交付 | ❌ | 未迁移 |
| 4.16 | checkSubcontractName | 检查分包名称 | ❌ | 未迁移 |
| 4.17 | queryContractNoEngineeFee | 查询工程费 | ❌ | 未迁移 |
| 4.18 | querySubcontractPayment | 查询分包付款 | ❌ | 未迁移 |
| 4.19 | savePayment | 保存付款 | ❌ | 未迁移 |
| 4.20 | querySubcontractPaymentPrint | 付款打印 | ❌ | 未迁移 |
| 4.21 | verifyPaymentDeliver | 验证付款交付 | ❌ | 未迁移 |
| 4.22 | terminateWorkFlow | 终止工作流 | ❌ | 未迁移 |
| 4.23 | querySubcontractComment | 查询分包评论 | ❌ | 未迁移 |
| 4.24 | queryFacilitator | 查询服务商 | ❌ | 未迁移 |
| 4.25 | querySubcontractInfoForProject | 查询项目分包信息 | ❌ | 未迁移 |
| 4.26 | facilitatorList | 服务商列表 | ❌ | 未迁移 |
| 4.27 | facilitatorEdit | 编辑服务商 | ❌ | 未迁移 |
| 4.28 | downloadFile | 下载文件 | ❌ | 未迁移 |

### 模块5：闭环管理 (PmClosedLoopAction → PmClosedLoopController)

> 源码：1170行，含工作流审批

| # | 方法 | 功能 | Spring Boot 状态 | 备注 |
|---|------|------|-----------------|------|
| 5.1 | execute | 闭环列表 | ⚠️ `GET /list` | 仅基础分页 |
| 5.2 | addPmCLApply | PM发起闭环申请 | ❌ | 未迁移 |
| 5.3 | addSmCLApply | SM发起闭环申请 | ❌ | 未迁移 |
| 5.4 | addCbCLApply | CB发起闭环申请 | ❌ | 未迁移 |
| 5.5 | cantCB | 无法闭环 | ❌ | 未迁移 |
| 5.6 | addClCLApply | CL发起闭环申请 | ❌ | 未迁移 |
| 5.7 | pmSeeCbCl | PM查看闭环 | ⚠️ `GET /{id}` | 仅基础查询 |

### 模块6：回访问卷 (PmClosedLoopQuesnaireAction)

> 源码：491行，Spring Boot **完全未迁移**

| # | 方法 | 功能 | Spring Boot 状态 |
|---|------|------|-----------------|
| 6.1 | execute | 问卷列表 | ❌ |
| 6.2 | addPCLQuesnaire | 新建问卷 | ❌ |
| 6.3 | pmCLQuesEdit | 编辑问卷 | ❌ |
| 6.4 | submitQues | 提交问卷 | ❌ |
| 6.5 | addLine | 添加问卷行 | ❌ |
| 6.6 | submitLine | 提交问卷行 | ❌ |
| 6.7 | updateQues | 更新问卷 | ❌ |
| 6.8 | deleteHeader | 删除问卷头 | ❌ |
| 6.9 | startEffective | 生效问卷 | ❌ |
| 6.10 | pmCLQuesSee | 查看问卷 | ❌ |
| 6.11 | deleteLine | 删除问卷行 | ❌ |
| 6.12 | editLine | 编辑问卷行 | ❌ |
| 6.13 | endEffective | 失效问卷 | ❌ |

### 模块7：回访管理 (CallBackAction → CallBackController)

> 源码：581行

| # | 方法 | 功能 | Spring Boot 状态 | 备注 |
|---|------|------|-----------------|------|
| 7.1 | input | 回访列表 | ⚠️ `GET /list` | 仅基础分页 |
| 7.2 | apply | 发起回访申请 | ⚠️ `POST /` | 骨架 |
| 7.3 | read | 查看回访 | ⚠️ `GET /{id}` | 仅基础查询 |
| 7.4 | seeQuesnaire | 查看回访问卷 | ❌ | 未迁移 |
| 7.5 | resubmit | 重新提交 | ❌ | 未迁移 |
| 7.6 | aduit | 审核 | ⚠️ `POST /{id}/approve` | 骨架 |

### 模块8：运维管理 (MaintenanceAction → MaintenanceController)

> 源码：765行

| # | 方法 | 功能 | Spring Boot 状态 | 备注 |
|---|------|------|-----------------|------|
| 8.1 | execute | 运维列表 | ⚠️ `GET /list` | 仅基础分页 |
| 8.2 | projectMaintenance | 项目维护记录 | ⚠️ `GET /{id}` | 仅基础查询 |
| 8.3 | createProjectMaintenance | 创建维护记录 | ⚠️ `POST /` | 仅基础保存 |
| 8.4 | serviceDelivery | 服务交付 | ❌ | 未迁移 |
| 8.5 | toUploadFile | 上传文件页面 | ❌ | 未迁移 |
| 8.6 | uploadFileList | 上传文件列表 | ❌ | 未迁移 |

### 模块9：监理管理 (SupervisionAction → SupervisionController)

> 源码：468行

| # | 方法 | 功能 | Spring Boot 状态 | 备注 |
|---|------|------|-----------------|------|
| 9.1 | execute | 监理列表 | ⚠️ `GET /list` | 仅基础分页 |
| 9.2 | projectSupervision | 项目监理记录 | ⚠️ `GET /{id}` | 仅基础查询 |
| 9.3 | createProjectSupervision | 创建监理记录 | ⚠️ `POST /` | 仅基础保存 |
| 9.4 | deleteProjectSupervision | 删除监理记录 | ⚠️ `DELETE /{id}` | 仅基础删除 |
| 9.5 | queryPowerUser | 查询权限用户 | ❌ | 未迁移 |

### 模块10：证书管理 (CertificateAction → CertificateController)

> 源码：143行

| # | 方法 | 功能 | Spring Boot 状态 | 备注 |
|---|------|------|-----------------|------|
| 10.1 | certificate | 证书列表 | ⚠️ `GET /list` | 仅基础分页 |
| 10.2 | queryCertificate | 查询证书 | ⚠️ `GET /barcode/{barcode}` | 已迁移 |
| 10.3 | uploadSealInfo | 上传印章信息 | ❌ | 未迁移 |

### 模块11：质保回调 (WarrantyCallbackAction)

> 源码：639行，Spring Boot **完全未迁移**

| # | 方法 | 功能 | Spring Boot 状态 |
|---|------|------|-----------------|
| 11.1 | execute | 质保回调列表 | ❌ |
| 11.2 | projectWarrantyCallback | 项目质保回调 | ❌ |
| 11.3 | createProjectWarrantyCallback | 创建质保回调 | ❌ |
| 11.4 | deleteProjectWarrantyCallback | 删除质保回调 | ❌ |
| 11.5 | projectWarranty | 项目质保 | ❌ |
| 11.6 | customerProject | 客户项目 | ❌ |
| 11.7 | queryPowerUser | 查询权限用户 | ❌ |

### 模块12：工作台 (WorkSpaceAction → WorkSpaceController)

> 源码：540行

| # | 方法 | 功能 | Spring Boot 状态 | 备注 |
|---|------|------|-----------------|------|
| 12.1 | execute | 工作台首页 | ⚠️ `GET /dashboard` | 仅骨架 |
| 12.2 | notice | 通知列表 | ⚠️ `GET /notifications` | 仅骨架 |
| 12.3 | task | 待办任务 | ⚠️ `GET /pending-tasks` | 仅骨架 |
| 12.4 | dailyTask | 日常任务 | ❌ | 未迁移 |
| 12.5 | hisselftask | 历史任务 | ❌ | 未迁移 |
| 12.6 | probTask | 技术公告任务 | ❌ | 未迁移 |
| 12.7 | subcontractTask | 分包任务 | ❌ | 未迁移 |
| 12.8 | updateNotifyState | 更新通知状态 | ❌ | 未迁移 |

### 模块13：报表统计 (ReportAction)

> 源码：1073行，Spring Boot **完全未迁移**

| # | 方法 | 功能 | Spring Boot 状态 |
|---|------|------|-----------------|
| 13.1 | show | 报表首页 | ❌ |
| 13.2 | loadLineData | 加载折线数据 | ❌ |
| 13.3 | loadLine_qualityData | 质量折线数据 | ❌ |
| 13.4 | loadLine_implData | 实施折线数据 | ❌ |
| 13.5 | assignedRate | 指派率统计 | ❌ |
| 13.6 | traceRate | 跟踪率统计 | ❌ |
| 13.7 | closeRate | 闭环率统计 | ❌ |
| 13.8 | implRate | 实施率统计 | ❌ |
| 13.9 | quality | 质量统计 | ❌ |
| 13.10 | projectSummaryStatus | 项目汇总状态 | ❌ |
| 13.11 | input | 报表输入 | ❌ |

### 模块14：工作流 (WorkFlowAction)

> 源码：464行，依赖Activiti引擎，Spring Boot **完全未迁移**

| # | 方法 | 功能 | Spring Boot 状态 |
|---|------|------|-----------------|
| 14.1 | execute | 流程列表 | ❌ |
| 14.2 | newdeploy | 新部署 | ❌ |
| 14.3 | deldeployment | 删除部署 | ❌ |
| 14.4 | viewDeployment | 查看部署 | ❌ |
| 14.5 | viewimage | 查看流程图 | ❌ |
| 14.6 | selftask | 我的任务 | ❌ |
| 14.7 | viewTaskForm | 查看任务表单 | ❌ |
| 14.8 | submitTask | 提交任务 | ❌ |
| 14.9 | viewCurrentImage | 查看当前流程图 | ❌ |
| 14.10 | taskmanager | 任务管理 | ❌ |
| 14.11 | hisTaskForm | 历史任务表单 | ❌ |
| 14.12 | delegatelist | 委托列表 | ❌ |
| 14.13 | delegateadd | 添加委托 | ❌ |
| 14.14 | delegateedit | 编辑委托 | ❌ |
| 14.15 | delegateupdate | 更新委托 | ❌ |

### 模块15：系统管理

| # | 子模块 | 方法 | 功能 | Spring Boot 状态 |
|---|--------|------|------|-----------------|
| 15.1 | 用户管理 | execute | 用户列表 | ✅ `GET /list` |
| 15.2 | 用户管理 | add | 新增用户 | ✅ `POST /` |
| 15.3 | 用户管理 | checkUsername | 检查用户名 | ⚠️ 可合并到add |
| 15.4 | 用户管理 | edit | 编辑用户 | ✅ `PUT /` |
| 15.5 | 用户管理 | submit | 提交用户 | ✅ 合并到add/edit |
| 15.6 | 用户管理 | pwdreset | 密码重置 | ✅ `POST /reset-password` |
| 15.7 | 用户管理 | findUser | 查找用户 | ⚠️ 可合并到list |
| 15.8 | 角色管理 | execute | 角色列表 | ✅ `GET /list` |
| 15.9 | 角色管理 | add | 新增角色 | ✅ `POST /` |
| 15.10 | 角色管理 | addSubmit | 提交角色 | ✅ 合并到add |
| 15.11 | 角色管理 | edit | 编辑角色 | ✅ `PUT /` |
| 15.12 | 角色管理 | editSubmit | 提交编辑 | ✅ 合并到edit |
| 15.13 | 部门管理 | execute | 部门列表 | ✅ `GET /list` |
| 15.14 | 部门管理 | refresh | 刷新部门 | ⚠️ 未迁移 |
| 15.15 | 部门管理 | add | 新增部门 | ✅ `POST /` |
| 15.16 | 部门管理 | addSubmit | 提交部门 | ✅ 合并到add |
| 15.17 | 部门管理 | edit | 编辑部门 | ✅ `PUT /` |
| 15.18 | 基础数据 | execute | 数据列表 | ✅ `GET /list` |
| 15.19 | 基础数据 | basicdataUpdate | 更新数据 | ✅ `PUT /` |
| 15.20 | 基础数据 | basicdataInsert | 插入数据 | ✅ `POST /` |
| 15.21 | 基础数据 | findBasicDataId | 查找数据ID | ⚠️ 未迁移 |
| 15.22 | 基础数据 | executeSql | 执行SQL | ❌ 安全风险，不迁移 |
| 15.23 | 操作日志 | execute | 日志列表 | ⚠️ `GET /list` | 仅基础分页 |
| 15.24 | 操作日志 | exportlog | 导出日志 | ❌ 未迁移 |
| 15.25 | 密码管理 | executepwd | 修改密码 | ⚠️ AuthController内 |
| 15.26 | 密码管理 | editlogin | 编辑登录 | ⚠️ AuthController内 |

### 模块16：数据同步 (Job类)

> 20+个同步Job，Spring Boot **完全未迁移**

| # | Job类 | 功能 | 数据源 |
|---|-------|------|--------|
| 16.1 | GainOrderBySAP | 从SAP获取订单 | SAP |
| 16.2 | GainOrderByERP | 从ERP获取订单 | ERP |
| 16.3 | GainPersonByOA | 从OA获取人员 | OA |
| 16.4 | GainPersonByEHR | 从EHR获取人员 | EHR |
| 16.5 | GainMarketRelationsBySMS | 从SMS获取市场关系 | SMS |
| 16.6 | GainPrjPropertyBySMS | 从SMS获取项目属性 | SMS |
| 16.7 | GainPrjRealProjectLineBySMS | 从SMS获取项目产品线 | SMS |
| 16.8 | GainPresalesInfoBySMS | 从SMS获取售前信息 | SMS |
| 16.9 | GainPresalesInfoFromOA | 从OA获取售前信息 | OA |
| 16.10 | GainDataFromITR | 从ITR获取数据 | ITR |
| 16.11 | GainDataFromLicense | 从License获取数据 | License |
| 16.12 | PlanGetBySMS | 从SMS获取计划 | SMS |
| 16.13 | PlanGetFromCRM | 从CRM获取计划 | CRM |
| 16.14 | GainDataFromCRM | 从CRM获取数据 | CRM |
| 16.15 | PullJobFromCRM | 从CRM拉取任务 | CRM |
| 16.16 | PushJobToCRM | 向CRM推送任务 | CRM |
| 16.17 | CrmSyncTask | CRM同步任务 | CRM |
| 16.18 | SubcontractInvoiceToFP | 分包发票到FP | ERMS |
| 16.19 | UpdateShipmentState | 更新发货状态 | 本地 |
| 16.20 | ProjectSoftVersionInitJob | 项目软件版本初始化 | 本地 |
| 16.21 | CloseNotTrackProject | 关闭不跟踪项目 | 本地 |
| 16.22 | AutoStartPresalesProjectJob | 自动启动售前项目 | 本地 |
| 16.23 | ReportDataTask | 报表数据任务 | 本地 |
| 16.24 | PushContractAcceptanceDeliveryJob | 推送合同验收交付 | 本地 |

### 模块17：定时邮件 (Job类)

| # | Job类 | 功能 |
|---|-------|------|
| 17.1 | Mailer | 通用邮件发送 |
| 17.2 | PrjArrivalReceiptMailer | 项目到货回执邮件 |
| 17.3 | PrjPreAndFinalInspectionMailer | 项目初终验邮件 |
| 17.4 | ProjectArrivalDelayMailer | 项目到货延迟邮件 |
| 17.5 | ProjectInspectionMailer | 项目验收邮件 |
| 17.6 | MaintenanceDailyReportMailer | 运维日报邮件 |
| 17.7 | MaintenanceServiceQuarterMailer | 运维季度服务邮件 |
| 17.8 | MaintenanceDepartmentSectaryJob | 运维部门秘书任务 |
| 17.9 | SubcontractNextPaymentMailer | 分包下期付款邮件 |
| 17.10 | SubcontractPaymentAutoComplete | 分包付款自动完成 |
| 17.11 | SubcontractPaymentAutoUpdate | 分包付款自动更新 |

---

## 四、缺失的Entity/Bean对比

### Struts有但Spring Boot缺失的Entity

| Struts Bean | 字段数 | 对应业务 | Spring Boot 状态 |
|------------|--------|---------|-----------------|
| Project | 136 | 项目主表(含大量column字段) | ⚠️ PmsProject仅32字段 |
| Presales | 57 | 售前项目 | ⚠️ PmsPresales仅32字段 |
| Prob | 43 | 技术公告 | ⚠️ PmsProb仅13字段 |
| ProbRestore | 41 | 恢复任务 | ❌ 缺失 |
| ProbStatistic | 36 | 统计分析 | ❌ 缺失 |
| SoftVersion | 43 | 软件版本 | ❌ 缺失 |
| ProjectMaintenance | 44 | 运维记录 | ⚠️ PmsMaintenance仅10字段 |
| ShipmentInfo | 42 | 发货信息 | ⚠️ PmsShipmentInfo仅40字段 |
| ProjectSoftVersionEntity | 47 | 项目软件版本 | ⚠️ PmsProjectSoftVersion仅22字段 |
| SubcontractProject | 26 | 分包项目 | ⚠️ PmsSubcontract仅16字段 |
| SubcontractLine | 12 | 分包行 | ❌ 缺失 |
| SubcontractDeliver | 11 | 分包交付 | ❌ 缺失 |
| SubcontractPayment | 13 | 分包付款 | ❌ 缺失 |
| SubcontractPrice | 13 | 分包价格 | ❌ 缺失 |
| SubcontractFacilitator | 15 | 服务商 | ❌ 缺失 |
| SubcontractCallback | 14 | 分包回调 | ❌ 缺失 |
| ProjectWarrantyCallback | 32 | 质保回调 | ❌ 缺失 |
| PmClEvaluationHeader | 28 | 闭环评估头 | ❌ 缺失 |
| PmClQuesnaireResultHeader | 17 | 问卷结果头 | ❌ 缺失 |
| PmClQuesnaireResultLine | 14 | 问卷结果行 | ❌ 缺失 |
| PmClosedLoopQuesnaire | 18 | 问卷模板头 | ❌ 缺失 |
| PmClosedLoopQuesnaireLine | 15 | 问卷模板行 | ❌ 缺失 |
| PmClosedLoopQuesnaireOpt | 12 | 问卷选项 | ❌ 缺失 |
| PresalesProduct | 12 | 售前产品 | ❌ 缺失 |
| PresalesTask | 14 | 售前任务 | ❌ 缺失 |
| PresalesComment | 1 | 售前评论 | ❌ 缺失 |
| DpActProcDesc | 28 | 工作流流程描述 | ❌ 缺失 |
| Notification | 14 | 通知 | ⚠️ SysNotification仅10字段 |
| User | 23 | 用户 | ⚠️ SysUser仅21字段 |
| Role | 9 | 角色 | ⚠️ SysRole仅8字段 |
| Department | 9 | 部门 | ⚠️ SysDepartment仅8字段 |
| Instruction | 10 | 项目批示 | ❌ 缺失 |
| ProjectLog | 8 | 项目日志 | ❌ 缺失 |
| ProjectDeliver | 24 | 项目交付 | ⚠️ PmsProjectDeliver仅9字段 |
| ProjectTask | 24 | 项目任务 | ⚠️ PmsProjectTask仅13字段 |
| OrderMainBean | 33 | 订单主信息 | ❌ 缺失 |
| OrderChangeState | 9 | 订单状态变更 | ❌ 缺失 |
| Product | 10 | 产品 | ❌ 缺失 |
| ProductType | 7 | 产品类型 | ❌ 缺失 |
| Company | 12 | 公司 | ❌ 缺失 |
| Contract | 7 | 合同 | ❌ 缺失 |
| MailSenderInfo | 21 | 邮件发送信息 | ❌ 缺失 |
| MailContent | 5 | 邮件内容 | ❌ 缺失 |
| NotificationTemplate | 3 | 通知模板 | ❌ 缺失 |
| ProbProduct | 13 | 公告产品 | ❌ 缺失 |
| ProductComponent | 11 | 产品组件 | ❌ 缺失 |
| DeviceVersionInfo | 10 | 设备版本信息 | ❌ 缺失 |
| ProbReadLog | 8 | 公告阅读日志 | ❌ 缺失 |
| ProbRestoreWeekly | 6 | 恢复任务周报 | ❌ 缺失 |

---

## 五、迁移优先级与计划

### P0 - 核心业务（必须迁移）

**第一批：补全项目管理缺失功能**
- [ ] 1.42 updateProjectExecutionState - 更新项目实施状态
- [ ] 1.43~1.46 合同拆分合并 (toMergeOrBranch, checkMergeContract, mergeContract, branchContract)
- [ ] 1.40 updateMember - 更新项目成员
- [ ] 1.41 saveInstallAdress - 保存安装地址
- [ ] 1.35 instruction - 项目批示
- [ ] 1.22 uploadDeliverableFile - 上传工程交付件
- [ ] 1.23 deleteDeliverById - 删除工程交付件
- [ ] 1.51~1.53 通知/工单/License查询
- [ ] 补全PmsProject缺失字段(column001~014, salesType等)

**第二批：技术公告模块完善**
- [ ] 2.5~2.16 审核/发布/任务管理
- [ ] 2.17~2.22 导入导出/软件版本管理
- [ ] 2.23~2.26 统计/阅读确认
- [ ] 2.27~2.35 产品/组件管理
- [ ] 补全PmsProb缺失字段, 创建ProbRestore/SoftVersion等Entity

**第三批：售前项目模块完善**
- [ ] 3.6~3.10 多级审核(SM/PM/EM)
- [ ] 3.11~3.16 发货/借转销/临时授权
- [ ] 3.17~3.21 附件/导出
- [ ] 补全PmsPresales缺失字段, 创建PresalesProduct/PresalesTask等Entity

### P1 - 重要业务

**第四批：分包管理模块完善**
- [ ] 4.5~4.8 申请/审核/关闭/回调流程
- [ ] 4.9~4.17 分包项目/发货/行/交付管理
- [ ] 4.18~4.28 付款/服务商/文件管理
- [ ] 创建SubcontractLine/Deliver/Payment/Price/Facilitator等Entity

**第五批：闭环管理模块完善**
- [ ] 5.2~5.7 PM/SM/CB/CL多角色闭环申请
- [ ] 创建PmClEvaluationHeader/问卷相关Entity
- [ ] 回访问卷模块(6.1~6.13)

**第六批：运维/监理/证书/质保回调完善**
- [ ] 8.4~8.6 运维服务交付/文件管理
- [ ] 9.5 监理权限用户查询
- [ ] 10.3 证书印章上传
- [ ] 11.1~11.7 质保回调全部功能

### P2 - 报表与工作流

**第七批：报表统计模块**
- [ ] 13.1~13.11 全部报表功能
- [ ] 需要确认报表数据源和展示方式

**第八批：工作台完善**
- [ ] 12.4~12.8 各类任务列表
- [ ] 12.8 通知状态更新

### P3 - 基础设施

**第九批：工作流引擎**
- [ ] 决定是否迁移Activiti或替换为其他方案
- [ ] 如迁移，需引入activiti-spring-boot-starter

**第十批：定时任务**
- [ ] 数据同步Job(16.1~16.24)
- [ ] 邮件Job(17.1~17.11)
- [ ] 引入Spring Scheduler或Quartz

**第十一批：其他**
- [ ] 操作日志导出(15.24)
- [ ] 缓存管理(15.22)
- [ ] 数据分析(模块16)

---

## 六、技术要点

### 6.1 字段映射问题
老系统Project bean有136个字段，大量使用column001~column014泛化字段。Spring Boot版PmsProject仅32字段，需补全或确认哪些column字段在新系统中仍需使用。

### 6.2 工作流依赖
闭环(5)、回访(7)、分包(4)、售前(3)、项目回退(1.24)均依赖Activiti工作流。Spring Boot版目前没有引入Activiti依赖。需决定：
- 方案A：引入activiti-spring-boot-starter，保持原有工作流
- 方案B：用状态机替代简单流程，复杂流程用Flowable
- 方案C：暂不迁移工作流相关功能

### 6.3 数据同步
老系统有20+个数据同步Job，连接SAP/CRM/OA/EHR/ITR/License/SSE等多个外部系统。Spring Boot版需配置多数据源或通过API调用。

### 6.4 邮件服务
老系统有自研SendMailService和11个邮件Job。Spring Boot版需引入spring-boot-starter-mail。

### 6.5 iBATIS → MyBatis-Plus转换
老系统有846条iBATIS SQL，其中项目管理模块465条。复杂SQL需转为MyBatis-Plus的LambdaQueryWrapper或自定义XML Mapper。

---

## 七、统计摘要

| 指标 | 数量 |
|------|------|
| Struts Action 总数 | 25个 |
| Struts Action 方法总数 | ~280个 |
| Struts Service 总数 | 22个 |
| Struts Service 方法总数 | ~700个 |
| Struts iBATIS SQL总数 | 846条 |
| Struts Bean/Entity总数 | 95个 |
| Struts 定时Job总数 | 42个 |
| Spring Boot Controller总数 | 21个 |
| Spring Boot Controller端点总数 | ~90个 |
| Spring Boot Entity总数 | 28个 |
| **已迁移功能(基本可用)** | **~45个方法** |
| **已建接口但逻辑TODO** | **~25个方法** |
| **完全未迁移** | **~210个方法** |
| **迁移完成度** | **约25%** |
