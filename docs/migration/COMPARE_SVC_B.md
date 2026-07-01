# Service层迁移比对报告（第二批次）

> 比对时间：2026-07-01  
> 比对专员：PMS迁移比对专员  
> 源目录：`PMS/PMS/PMS-struts/src/com/dp/plat/service/`  
> 目标目录：`PMS/PMS-springboot/pms-service/src/main/java/com/dp/plat/service/impl/`

---

## 目录

1. [ProjectServiceImpl.java → PmsProjectServiceImpl.java](#1-projectserviceimpl)
2. [ProjectPlanServiceImpl.java → (无对应)](#2-projectplanserviceimpl)
3. [PresalesServiceImpl.java → PmsPresalesServiceImpl.java](#3-presalesserviceimpl)
4. [ReportServiceImpl.java → ReportServiceImpl.java](#4-reportserviceimpl)
5. [汇总统计](#5-汇总统计)

---

## 1. ProjectServiceImpl.java → PmsProjectServiceImpl.java

**源文件**：3907行 | **目标文件**：~600行  
**整体评估**：⚠️ **部分迁移** — 目标仅实现约15%的源方法，大量核心业务逻辑缺失

### 1.1 项目创建与基本信息

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 1 | `queryProjectList(Project, DisplayParam)` | `queryProjectPage(...)` | ⚠️部分迁移 | 目标改用MyBatis-Plus分页，缺少DisplayParam排序逻辑、`log()`审计 |
| 2 | `insertProject(Project)` | `addProject(ProjectDTO)` | ⚠️部分迁移 | **缺失**：①`queryProjectByContractNo`查已有项目 ②`putProperties`属性合并 ③SMS项目编码处理 ④项目组编码自动生成逻辑 ⑤`insertProjectGroup/Contract/GroupRelationship`三表写入 ⑥`queryProjectShipmentState` ⑦`insertOrUpdateProjectState` ⑧销售成员从OA获取邮箱 ⑨项目经理B保存 ⑩SAP订单产品线同步 ⑪不予跟踪邮件通知+闭环时间更新 ⑫`updateChannel`渠道信息更新 |
| 3 | `insertBatchProject(Project, int)` | `importProject(List, int)` | ⚠️部分迁移 | **缺失**：①batchFunc三种模式的完整处理 ②成员角色区分(服务经理/项目经理) ③SAP产品线同步 ④项目组/合同/关系三表写入 |
| 4 | `queryOrderLineFromSapByContractNo(Project)` | — | ❌未迁移 | SAP订单数据查询 |
| 5 | `insertProjectMember(Project)` [protected] | `addInitialMembers(...)` | ⚠️部分迁移 | **缺失**：①OA人员信息查询补充邮箱 ②`BeanUtils.copyProperties`完整字段映射 ③销售人员成员未添加(源码添加了销售10+服务经理20+项目经理30) |
| 6 | `putProperties(Project, Project)` [protected] | — | ❌未迁移 | 合同号属性合并逻辑 |
| 7 | `updateProjectByProjectId(Project)` | `updateProject(ProjectDTO)` | ⚠️部分迁移 | **缺失**：①成员更新`updateProjectMember`完整逻辑 ②通知发送 ③闭环审批任务assignee更新 ④`updateProjectStateByProjectId` ⑤项目经理变更终止审批流程 ⑥`updateChannel` ⑦`updateProjectImplByProjectId` |
| 8 | `updateProjectByProjectIdSelective(Project)` | — | ❌未迁移 | 反射设置updateBy/updateTime的选择性更新 |
| 9 | `terminateProgramManagerActivities(Project)` | — | ❌未迁移 | 项目经理变更时终止闭环/回访流程 |
| 10 | `updateProjectMember(Project, String, String)` | — | ❌未迁移 | 成员变更核心逻辑(查count→更新旧→插入新) |
| 11 | `queryProjectByContractNo(String)` | — | ❌未迁移 | 按合同号查询项目 |
| 12 | `queryProjectByContractNoAndType(String, String)` | — | ❌未迁移 | 按合同号+类型查询 |
| 13 | `queryProjectById(int)` | `getProjectDetail(Long)` | ⚠️部分迁移 | 签名不同(int→Long)，缺少成员查询 |
| 14 | `queryProjectByPowerId(Project)` | — | ❌未迁移 | 权限查询项目 |
| 15 | `queryProjectSimplifyByProjectId(Integer)` | — | ❌未迁移 | 简化查询 |

### 1.2 项目成员管理

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 16 | `queryProjectMembers(int)` | `getProjectMembers(Long)` | ⚠️部分迁移 | 类型不同(int→Long)，缺少`dataState`有效状态过滤 |
| 17 | `insertProjectMember(ProjectMember)` | `addProjectMember(ProjectMemberDTO)` | ⚠️部分迁移 | 缺少`fromFlag`标记、`log()`审计 |
| 18 | `updateProjectMember(ProjectMember)` | `updateProjectMember(Long, LocalDateTime)` | ⚠️部分迁移 | 签名完全不同，源直接更新成员对象，目标只更新effectiveTo |
| 19 | `updateProjectProgramManagerByProjectId(Project)` | — | ❌未迁移 | 指定项目经理(含通知、状态更新、渠道更新) |
| 20 | `updateProjectProgramManagerByProjectId(Project, String)` | — | ❌未迁移 | 指定项目经理(含邮件模板、终止流程) |
| 21 | `queryValidMemberByProjectId(int)` | — | ❌未迁移 | 查询有效成员 |
| 22 | `queryValidMemberEmailByProjectIdAndRoles(int, String)` | — | ❌未迁移 | 按角色查询成员邮箱 |
| 23 | `batchChangeMember(BatchChangeMemberDTO)` | — | ⚠️有方法 | 目标有方法但实现简化，缺少闭环审批任务变更 |

### 1.3 项目状态管理

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 24 | `updateProjectStateByProjectId(Project, String)` | — | ❌未迁移 | 更新项目回退标识 |
| 25 | `updateProjectStatus(int, String)` | — | ❌未迁移 | 更新projectState |
| 26 | `queryProjectStateByProjectId(Project)` | — | ❌未迁移 | 查询项目状态 |
| 27 | `insertOrUpdateProjectState(Project)` | — | ❌未迁移 | 插入或更新pm_project_state表 |
| 28 | `queryProjectPlanState(int)` | — | ❌未迁移 | 查询计划状态 |
| 29 | `queryProjectCurrentPlan(int)` | — | ❌未迁移 | 查询当前计划阶段 |
| 30 | `updateProjectExecutionState(Project, String)` | `updateProjectExecutionState(Long, String)` | ⚠️部分迁移 | **缺失**：①直签项目终验报告判断 ②非直签项目canCloseLoop判断 ③闭环状态比较逻辑 ④`prevExecutionState`自定义信息 |
| 31 | `updateProjectCloseProcessState(int, String)` | — | ❌未迁移 | 闭环流程状态更新 |
| 32 | `canCloseLoop(Project)` | — | ❌未迁移 | 闭环条件判断(安装数=发货数、回访完成、渠道非空、交付件完整) |

### 1.4 回退与不予跟踪

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 33 | `backToLastStep(int, String, String, Map)` | `backToLastStep(ProjectBackDTO)` | ⚠️部分迁移 | **缺失**：①5种isback状态的完整处理(40/42/30/50/30) ②不同模板的邮件通知 ③终止活动`termainteActivities` ④闭环时间更新/清除 ⑤通知信息发送 |
| 34 | `updateProjectIsbackByProjectId(int, String, String, String, int)` | `updateProjectIsback(...)` | ⚠️部分迁移 | **缺失**：①sendto 1-4的分支处理 ②无效化项目`invalidProject` ③通知发送 |
| 35 | `invalidProject(int)` | — | ❌未迁移 | 失效项目(主表+通知表+关系表) |
| 36 | `termainteActivities(Project)` | — | ❌未迁移 | 终止进行中的审批流程 |

### 1.5 项目计划与工程

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 37 | `editProjectPlan(ProjectTask)` | `editProjectPlan(Long, String, String)` | ⚠️部分迁移 | **缺失**：①eventKey循环解析 ②合同号拆分处理 ③visibleFlag可见性控制 ④计划日期解析 ⑤批量插入`insertProjectPlan` |
| 38 | `queryProjectTaskByProjectId(int)` | — | ❌未迁移 | 查询项目任务 |
| 39 | `queryProjectPlanEventByProject(Project)` | — | ❌未迁移 | 查询计划事件(含IMPL_WAY判断) |
| 40 | `queryNeededUndelivedCount(Project)` | — | ❌未迁移 | 必传未交付件数量 |
| 41 | `queryNeededUndelivedProjectDeliverList(Project)` | — | ❌未迁移 | 必传未交付件列表 |

### 1.6 交付件管理

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 42 | `uploadFile(ProjectDeliver, String, ProjectDeliver)` | `uploadDeliverableFile(Long, String, String)` | ⚠️部分迁移 | **缺失**：①文件类型白名单校验 ②文件重命名 ③物理文件复制 ④交付件记录批量插入 ⑤eventActualFinishDate更新 ⑥计划状态联动 ⑦运营商直签自动发起回访 ⑧验收报告邮件通知 |
| 43 | `uploadFile(ProjectDeliver, String, File[], String)` | — | ❌未迁移 | File数组上传版本 |
| 44 | `deleteDeliverById(int)` | `deleteDeliverById(Long)` | ⚠️部分迁移 | **缺失**：①`updateEventActualFinishDateByTask` ②交付件类型查询 ③系统通知 |
| 45 | `insertProjectDeliverFiles(ProjectDeliver, List, String)` | — | ❌未迁移 | 批量插入交付件+更新完成时间 |
| 46 | `queryProjectDeliverList(ProjectDeliver)` | — | ❌未迁移 | 查询交付件列表(含非直签逻辑) |
| 47 | `queryDeliverDetailByProjectId(int)` | — | ❌未迁移 | 查询交付件详情 |
| 48 | `queryDeliverDetailByProjectIdAndProjectType(int, String)` | — | ❌未迁移 | 按项目类型查询 |
| 49 | `updateEventActualFinishDateByTask(ProjectDeliver)` [private] | — | ❌未迁移 | 更新事件实际完成时间+计划状态+闭环状态+自动回访 |

### 1.7 发货与设备管理

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 50 | `queryShipmentInfoByContractNo(String, int)` | — | ❌未迁移 | 按合同号查询发货信息 |
| 51 | `queryShipmentInfoByContractNo(String, int, String)` | — | ❌未迁移 | 含利润中心 |
| 52 | `queryShipmentInfoSizeByContractNo(String)` | — | ❌未迁移 | 发货数量查询 |
| 53 | `queryTransferShipmentInfoByContractNo(...)` | — | ❌未迁移 | 转移发货查询 |
| 54 | `deleteShipmentInstallInfoByProjectId(int)` | `deleteShipmentInfo(Long)` | ✅已迁移 | 逻辑一致 |
| 55 | `insertInstallAddress(String, int, String, String)` | `saveInstallAddress(Long, String[], String)` | ⚠️部分迁移 | **缺失**：①合同号拆分处理 ②`queryProjectShipment`查已有记录 ③批量更新/插入发货信息 |
| 56 | `insertInstallAddress(String, int, String, String, String)` | — | ❌未迁移 | 含利润中心版本 |
| 57 | `insertTransferShipment(String, Project, Project)` | `transferShipment(List, Long, Long)` | ⚠️部分迁移 | **缺失**：①串货合同号插入 ②转移标识区分(转出1/转入0) ③条码级转移处理 ④系统通知 |
| 58 | `queryProjectShipment(int)` | — | ❌未迁移 | 发货数量查询 |
| 59 | `queryHistoryProjectShipmentSize(int)` | — | ❌未迁移 | 历史发货数量 |

### 1.8 周报管理

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 60 | `insertPorjectWeekly(ProjectWeekly, List×6)` | — | ❌未迁移 | 周报创建(含6类内容批量插入) |
| 61 | `updatePorjectWeekly(ProjectWeekly, List×6)` | — | ❌未迁移 | 周报更新(先删后插) |
| 62 | `queryProjectWeeklyList(int, int)` | — | ❌未迁移 | 周报列表查询 |
| 63 | `queryPorjectWeekly(int)` | — | ❌未迁移 | 单个周报查询 |
| 64 | `queryWeeklyContentList(int, int)` | — | ❌未迁移 | 周报内容查询 |
| 65 | `insertWeeklyFiles(List, int)` | — | ❌未迁移 | 周报附件上传 |
| 66 | `insertWeeklyFeedback(Map)` | — | ❌未迁移 | 周报批注 |
| 67 | `queryFeedbackList(int)` | — | ❌未迁移 | 批注列表 |
| 68 | `createProjectWeeklyExecl(...)` | — | ❌未迁移 | 周报Excel生成(模板填充) |
| 69 | `deleteFileById(int)` | — | ❌未迁移 | 删除文件 |

### 1.9 软件版本管理

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 70 | `querySoftversionList(String, int)` | `checkSoftVersion(Long, String, Map)` | ⚠️部分迁移 | 签名不同，缺少利润中心和DisplayParam版本 |
| 71 | `updateSoftversion(List, SoftChangeLog)` | `updateSoftVersion(List, SoftChangeLogVO)` | ⚠️部分迁移 | **缺失**：①SoftVersionParser解析 ②版本号自动生成V+n ③旧版本失效 ④批量插入版本列表 |
| 72 | `queryHistSoftChangeLog(int)` | `checkhistsoftversion(Long)` | ⚠️部分迁移 | 签名不同(int→Long) |
| 73 | `queryHistSoftVersionList(SoftChangeLog)` | `queryHistSoftVersionList(Long)` | ⚠️部分迁移 | **缺失**：出厂版本特殊处理(-1判断) |
| 74 | `queryOneSoftChangeLog(int)` | `queryOneSoftChangeLog(Long)` | ⚠️部分迁移 | 类型不同 |
| 75 | `selectProjectSoftVersionList(...)` | — | ❌未迁移 | 分页查询软件版本 |

### 1.10 合同管理

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 76 | `queryContractList(Map)` | `queryContractList(String)` | ⚠️部分迁移 | 签名不同 |
| 77 | `insertMergeContract(String, int)` | `mergeContract(String[], Long)` | ⚠️部分迁移 | **缺失**：①`insertMergeProduct`产品复制 ②`insertMergeTask`计划复制 |
| 78 | `insertNewProject(int, String, List, String)` | `branchContract(Long, String, String)` | ⚠️部分迁移 | **缺失**：①产品数量扣减`updateProjectProduct` ②批量插入新产品`batchInsertProduct` |
| 79 | `queryProjectContractCountByContractNo(String)` | — | ❌未迁移 | 合同数量查询 |

### 1.11 批示管理

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 80 | `queryInstructionList(int)` | — | ❌未迁移 | 批示列表(含反馈嵌套) |
| 81 | `insertInstruction(Instruction)` | — | ❌未迁移 | 插入批示 |
| 82 | `saveInstruction(Object...)` | `saveInstruction(Long, String, Long)` | ⚠️部分迁移 | **缺失**：①邮件通知(服务经理+项目经理+项目经理B) ②通知模板 |

### 1.12 维保管理

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 83 | `selectProjectMaintenanceById(Integer)` | — | ❌未迁移 | 维保详情查询 |
| 84 | `selectProjectMaintenanceList(...)` | — | ❌未迁移 | 维保列表 |
| 85 | `insertOrUpdateProjectMaintenance(...)` | `createProjectMaintenance(PmsMaintenance)` | ⚠️部分迁移 | 签名不同，缺少update逻辑 |
| 86 | `uploadMaintenanceFile(...)` | — | ❌未迁移 | 维保文件上传(含服务交付记录、周期计算、邮件通知) |
| 87 | `selectProjectMaintenanceServiceDeliveryList(...)` | — | ❌未迁移 | 服务交付列表 |
| 88 | `queryProjectWarrantyState(Integer)` | — | ❌未迁移 | 质保状态查询 |

### 1.13 督导管理

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 89 | `selectProjectSupervisionById(Integer)` | — | ❌未迁移 | 督导详情查询 |
| 90 | `selectProjectSupervisionList(...)` | — | ❌未迁移 | 督导列表 |
| 91 | `insertOrUpdateProjectSupervision(...)` | — | ❌未迁移 | 督导插入/更新 |

### 1.14 导出功能

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 92 | `exportSpotCheckList(Project)` | `exportSpotCheck(Long)` | ⚠️部分迁移 | **缺失**：①模板文件读取 ②合同号拆分 ③条码级明细 ④Doc备选导出 ⑤Word模板生成 |
| 93 | `exportOverWarrantyRemindList(Project)` | `exportOverWarrantyRemind(Long)` | ⚠️部分迁移 | **缺失**：①OA服务经理信息查询 ②保修期计算 ③Word模板生成 ④数据Map填充 |
| 94 | `importSpotCheckIgnoreItem(List)` | `importSpotCheckIgnoreItem(List<?>)` | ⚠️部分迁移 | **缺失**：`truncateSpotCheckIgnoreItem`先清空后批量插入 |

### 1.15 总代借货项目

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 95 | `updateSoleAgentLendProject()` | — | ❌未迁移 | 总代借货项目合同合并(含临时表创建/销毁、新旧合同处理) |
| 96 | `mergeOldSoleAgentLendProjectContract(...)` | — | ❌未迁移 | 旧合同合并 |
| 97 | `mergeNewSoleAgentLendProjectContract(...)` | — | ❌未迁移 | 新合同合并(含失效逻辑) |

### 1.16 通知与邮件

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 98 | `addFixedNotification(String, int)` | — | ❌未迁移 | 固定通知 |
| 99 | `addDynamicNotification(String, int, String)` | — | ❌未迁移 | 动态通知 |
| 100 | `addDynamicNotification(String, int, HashMap)` | — | ❌未迁移 | 动态通知(Map参数) |
| 101 | `keepMailInfo(Project, NotificationTemplate, String)` | — | ❌未迁移 | 邮件信息保存 |
| 102 | `getMails(String)` | — | ❌未迁移 | 按用户名获取邮箱 |
| 103 | `getMails(int)` | — | ❌未迁移 | 按角色ID获取邮箱 |
| 104 | `getUsernames(int)` | — | ❌未迁移 | 按角色获取用户名列表 |
| 105 | `queryMailByUserNameFromOA(String)` | — | ❌未迁移 | OA系统邮箱查询 |

### 1.17 其他查询

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 106 | `queryProjectListByPower(Project, DisplayParam)` | — | ❌未迁移 | 权限项目列表 |
| 107 | `findProjectList(Object...)` | — | ❌未迁移 | 复杂项目查询(含角色权限、区域过滤) |
| 108 | `queryPersonList()` | `queryPersonList()` | ⚠️部分迁移 | 源返回`List<Person>`，目标返回`List<Map>` |
| 109 | `queryAllUser()` | `queryAllUser()` | ✅已迁移 | 逻辑基本一致 |
| 110 | `queryUserWithRoleId(Long)` | `queryUserWithRoleId(Long)` | ✅已迁移 | 逻辑基本一致 |
| 111 | `queryDpNoRoleUser(Long, String)` | `queryDpNoRoleUser(Long, String)` | ✅已迁移 | 逻辑基本一致 |
| 112 | `queryOrderDataListByProjectId(int)` | `checkOrderData(Long, String)` | ⚠️部分迁移 | 签名不同 |
| 113 | `queryOrderDataDetailListByProjectId(int)` | `checkRealOrderData(Long)` | ⚠️部分迁移 | 签名不同 |
| 114 | `queryTransferProjectList(Project)` | `queryTransferProjectList(String)` | ⚠️部分迁移 | 签名不同 |
| 115 | `queryMarketRelations()` | — | ❌未迁移 | 市场关系查询 |
| 116 | `selectContractAcceptanceDeliveryInfo(Map)` | — | ❌未迁移 | 合同验收交付信息 |
| 117 | `selectProblemTicket(Map)` | — | ❌未迁移 | 问题工单查询 |
| 118 | `selectLicenseInfo(Map)` | — | ❌未迁移 | License信息查询 |
| 119 | `selectProblemTicketByProject(Project)` | — | ❌未迁移 | 按项目查工单(含总代借货利润中心) |
| 120 | `queryProjectLeaseLineByProjectCode(String)` | `projectLeaseLine(String)` | ⚠️部分迁移 | 签名不同 |
| 121 | `queryProjectProductConfigLevelInfoByProjectCode(String)` | `projectProductConfigLevelInfo(String)` | ⚠️部分迁移 | 签名不同 |
| 122 | `queryNotifyList(int)` | — | ❌未迁移 | 通知列表 |
| 123 | `queryCallBackList(int)` | — | ❌未迁移 | 回访列表 |
| 124 | `queryCallBackingSize(int)` | — | ❌未迁移 | 进行中回访数量 |
| 125 | `batchDeleteProject(List)` | — | ❌未迁移 | 批量删除(含终止活动) |
| 126 | `batchInvalidProject(List)` | — | ❌未迁移 | 批量失效(含终止活动+invalidProject) |
| 127 | `queryRmaOrderDataByContractNo(String)` | — | ❌未迁移 | RMA订单查询 |
| 128 | `queryRealOrderDataListByProjectId(int)` | — | ❌未迁移 | 实际订单查询 |
| 129 | `clearProject(List, boolean)` | — | ⚠️有方法 | 目标有实现但逻辑简化 |

### 1.18 ProjectServiceImpl 统计

| 状态 | 数量 | 占比 |
|------|------|------|
| ✅完全迁移 | 5 | 3.9% |
| ⚠️部分迁移 | 35 | 27.1% |
| ❌未迁移 | 89 | 68.9% |
| **合计** | **129** | **100%** |

---

## 2. ProjectPlanServiceImpl.java

**源文件**：24行 | **目标文件**：无对应文件  
**整体评估**：❌ **完全未迁移**

### 2.1 方法清单

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 1 | `queryProjectPlanListByContractNo(String)` | — | ❌未迁移 | 按合同号查询项目计划列表，目标目录中无任何plan相关Service实现 |

> **注意**：`PmsProjectServiceImpl`中注入了`PmsProjectPlanMapper`但未使用。项目计划查询逻辑散落在其他Service中，建议创建独立的`ProjectPlanServiceImpl`。

---

## 3. PresalesServiceImpl.java → PmsPresalesServiceImpl.java

**源文件**：851行 | **目标文件**：~350行  
**整体评估**：⚠️ **部分迁移** — 目标实现了基础CRUD和简化审批，但缺失大量Activiti工作流集成和复杂业务逻辑

### 3.1 查询方法

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 1 | `queryPresalesById(int)` | `getPresalesDetail(Long)` | ❌未迁移 | **缺失**：①角色权限校验(6种角色+区域权限+申请人/PM/SM匹配) ②SMS/OA附件解析(fileName匹配、OA Token获取) ③新交付件查询`queryDeliverDetailByProjectIdAndProjectType` ④历史交付件查询`queryFileList` ⑤FileParam组装 |
| 2 | `queryPresalesProductByPresalesId(int)` | `queryProducts(Long)` | ⚠️部分迁移 | 签名不同(int→Long) |
| 3 | `queryPresalesList(Presales, DisplayParam)` | `queryPresalesPage(...)` | ⚠️部分迁移 | **缺失**：DisplayParam分页排序、`throws UnsupportedEncodingException` |
| 4 | `queryPresalesCommentList(int)` | `queryComments(Long)` | ⚠️部分迁移 | 签名不同 |
| 5 | `queryPresalesTaskList(int, int)` | `queryTasks(Long)` | ❌未迁移 | **缺失**：①新交付件按eventKey分组`queryDeliverDetailByProjectIdAndProjectType` ②历史交付件`queryFileMap` ③FileParam/fileMap组装 |
| 6 | `queryPresalesQuesnaireId(Presales)` | — | ❌未迁移 | 问卷ID查询 |
| 7 | `queryPresaleShipmentInfo(String)` | `queryShipmentInfo(String, boolean)` | ⚠️部分迁移 | 签名不同 |
| 8 | `queryPresaleShipmentInfo(String, boolean)` | `queryShipmentInfo(String, boolean)` | ⚠️部分迁移 | 返回类型不同 |
| 9 | `queryPresaleLend2SaleInfo(String)` | `queryLend2SaleInfo(String)` | ✅已迁移 | 逻辑一致 |
| 10 | `queryPresaleLend2RmaInfo(String)` | `queryLend2RmaInfo(String)` | ✅已迁移 | 逻辑一致 |
| 11 | `selectPresalesTempAuthInfo(Map)` | `queryTempAuthInfo(Long)` | ⚠️部分迁移 | 签名不同，目标直接从presales查lendInfoId |
| 12 | `queryPresalesExportData(Presales)` | `exportPresales(PmsPresales)` | ⚠️部分迁移 | 目标简化查询条件 |

### 3.2 流程启动

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 13 | `startPresalesFlow(Presales, PresalesComment)` | `startFlow(Long)` | ❌未迁移 | **缺失全部核心逻辑**：①pmTaskNextRole系统参数 ②售前项目分类保存 ③项目编码更新`updatePresalesProjectCode` ④产品明细表presalesId回写 ⑤成员添加(addProjectMemeber SM+PM) ⑥状态更新(31/32) ⑦工程计划判断+插入 ⑧Activiti流程启动(startProcess) ⑨businessKey拼接 ⑩流程实例ID回写 ⑪任务办理(doSelfTask) ⑫审批意见(addSelfActComment) ⑬邮件通知 ⑭阶段耗时更新 |

### 3.3 审批方法

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 14 | `submitSmAduit(Presales, PresalesComment)` | `smAudit(Long, String, boolean)` | ❌未迁移 | **缺失全部核心逻辑**：①成员更新 ②工程计划判断+插入 ③taskDefKey分支(usertask2/serviceApprove) ④任务转办(assigneeTask) ⑤状态更新(32/30) ⑥Activiti流程变量+任务办理 ⑦审批意见 ⑧邮件通知(项目经理/工程管理部) ⑨阶段耗时 |
| 15 | `submitpmAduit(Presales, PresalesComment)` | `pmAudit(Long, String, boolean)` | ❌未迁移 | **缺失**：①confirmFileIds更新 ②pmTaskNextRole流程变量 ③nextIsSmRole判断 ④状态(33/34/31) ⑤Activiti流程办理 ⑥审批意见 ⑦邮件(工程管理部/服务经理) ⑧阶段耗时 |
| 16 | `submitEmAduit(Presales, PresalesComment)` | `emAudit(Long, String, boolean)` | ❌未迁移 | **缺失**：①项目分类保存 ②状态更新(32) ③Activiti流程办理(emRole) ④审批意见 ⑤邮件 ⑥阶段耗时 |
| 17 | `submitReApply(Presales, PresalesComment)` | `reApply(Long, PmsPresales)` | ❌未迁移 | **缺失**：①驳回时设置closeRemark ②成员添加(SM+PM) ③状态更新(31/32) ④Activiti流程变量(sm/pm/emRole) ⑤任务办理 ⑥审批意见 ⑦邮件 ⑧阶段耗时 |
| 18 | `approve(Long, String, boolean)` | `approve(Long, String, boolean)` | ⚠️部分迁移 | 目标有通用审批，但缺少Activiti集成 |

> **关键差异**：源码的审批方法深度集成Activiti工作流引擎（启动流程、获取任务、办理任务、添加审批意见），目标完全移除了Activiti依赖，改为简单的状态机模式。这是**架构级差异**。

### 3.4 问卷管理

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 19 | `insertPresalesQuesnaire(Presales, Header, List)` | — | ❌未迁移 | 问卷保存(头+行+关联关系，含版本号管理) |

### 3.5 项目结束

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 20 | `updateEndingPresalesProject(int)` | — | ❌未迁移 | 闭环结束(applyState=FLOW_PASS, projectState=CLOSEDLOOP) |
| 21 | `updateEnding20PresalesProject(int)` | — | ❌未迁移 | 驳回结束(applyState=FLOW_PASS, projectState=DENY) |

### 3.6 任务管理

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 22 | `updatePresalesTask(Date, int)` | `updateTask(PmsPresalesTask)` | ⚠️部分迁移 | 签名完全不同 |
| 23 | `updatePresalesTask(Date, String, int)` | — | ❌未迁移 | 含remark版本 |
| 24 | `updatePresalesTaskDeliverFiles(int, String)` | — | ❌未迁移 | 更新任务交付件文件ID |
| 25 | `updatePresalesConfirmFileIds(int, String)` | `updateConfirmFiles(Long, String)` | ⚠️部分迁移 | 签名不同 |
| 26 | `updatePrealesFileIds(int, int, int)` | — | ❌未迁移 | 更新文件ID |

### 3.7 文件上传

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 27 | `uploadFile(ProjectDeliver, String, File[], String)` | `uploadDeliver(Long, Long, String)` | ❌未迁移 | **缺失**：①文件类型白名单校验 ②文件重命名 ③物理文件复制 ④`insertProjectDeliverFiles` ⑤`updateEventActualFinishDateByTask` |
| 28 | `deleteDeliverById(int)` | `deleteDeliver(Long)` | ⚠️部分迁移 | **缺失**：`updateEventActualFinishDateByTask`自动更新完成时间 |
| 29 | `updateProjectDeliverById(ProjectDeliver)` | `updateDeliverById(Map)` | ⚠️部分迁移 | 签名完全不同 |

### 3.8 批量操作

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 30 | `terminate2Close(String, String)` | `terminate2Close(Long, String)` | ⚠️部分迁移 | **缺失**：①批量处理(逗号分隔多个ID) ②Activiti流程终止(deleteProcessInstance) ③审批意见 ④updateEnding20PresalesProject ⑤阶段耗时更新 |

### 3.9 私有/辅助方法

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 30 | `updatePresalesProjectCode(Presales)` | — | ❌未迁移 | 项目编码更新 |
| 31 | `addProjectMemeber(int, String, String)` | — | ❌未迁移 | 添加项目成员(失效旧+查询用户+插入新) |
| 32 | `insertProjectDeliverFiles(...)` | — | ❌未迁移 | 批量插入交付件 |
| 33 | `updateEventActualFinishDateByTask(ProjectDeliver)` | — | ❌未迁移 | 事件完成时间更新 |
| 34 | `updatePresalesDuration(int)` | — | ❌未迁移 | 异步更新阶段耗时(新线程) |

### 3.10 PresalesServiceImpl 统计

| 状态 | 数量 | 占比 |
|------|------|------|
| ✅完全迁移 | 2 | 5.9% |
| ⚠️部分迁移 | 13 | 38.2% |
| ❌未迁移 | 19 | 55.9% |
| **合计** | **34** | **100%** |

---

## 4. ReportServiceImpl.java → ReportServiceImpl.java

**源文件**：462行 | **目标文件**：~350行  
**整体评估**：⚠️ **部分迁移** — 目标实现了基础报表查询，但缺失质量管理临时表机制、报表数据持久化、自动统计等核心功能

### 4.1 指派率/跟踪率

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 1 | `queryAssignedRate(ReportQueryParam)` | `queryAssignedRate(Map)` | ⚠️部分迁移 | **缺失**：①按办事处分组计算(源遍历allMap) ②isALl=0/1两次查询 ③DecimalFormat格式化 ④total汇总 |
| 2 | `queryTraceRate(ReportQueryParam)` | `queryTraceRate(Map)` | ⚠️部分迁移 | 同上，**缺失**按办事处分组逻辑 |

### 4.2 质量管理

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 3 | `queryQualityList(ReportQueryParam)` | `queryQuality(Map)` | ❌未迁移 | **缺失全部核心逻辑**：①`createQualityTmpTable`临时表创建 ②`queryOfficeQuality`办事处闭环平均分 ③`queryOtherOfficeQuality`无闭环办事处 ④`queryTotalQuality`全国数据 ⑤`deleteQualityTmpTable`清理 |
| 4 | `queryTotalAndRemainderList(ReportQueryParam)` | — | ❌未迁移 | 全量+去除督导/代理商的质量数据(含两轮查询) |
| 5 | `queryTotalQuality(ReportQueryParam)` | — | ❌未迁移 | 全国质量管理数据(含临时表) |

### 4.3 闭环率

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 6 | `queryCloseRate(ReportQueryParam)` | `queryCloseRate(Map)` | ⚠️部分迁移 | **缺失**：①`queryCloseMap`+`queryNewMap`两次查询 ②按办事处分组 ③遍历closeMap/newMap较大者 |

### 4.4 实施方式

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 7 | `queryImplWayMap(ReportQueryParam, int)` | `queryImplRate(Map)` | ⚠️部分迁移 | **缺失**：①按实施方式分组查询 ②异常catch中的临时表备选方案 |

### 4.5 趋势图数据

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 8 | `queryLineData(String, String)` | `loadLineData(Map)` | ⚠️部分迁移 | **缺失**：源直接查询ReportLineData表，目标改为内存分组统计 |
| 9 | `queryLineQualityData(String, String)` | `loadQualityLineData(Map)` | ⚠️部分迁移 | 同上 |
| 10 | `queryReportLineAssignedData(ReportQueryParam)` | — | ❌未迁移 | 指派率趋势数据查询 |
| 11 | `queryReportLineTraceData(ReportQueryParam)` | — | ❌未迁移 | 跟踪率趋势数据查询 |
| 12 | `queryReportLineClosedData(ReportQueryParam)` | — | ❌未迁移 | 闭环率趋势数据查询 |
| 13 | `queryReportLineQualityData(ReportQueryParam)` | — | ❌未迁移 | 质量趋势数据(含临时表) |
| 14 | `queryReportLineRemainderQualityDataAndTotalsize(ReportQueryParam)` | — | ❌未迁移 | 去除督导/代理商的质量趋势+总量 |
| 15 | `queryReportLineImplData(ReportQueryParam)` | `loadImplLineData(Map)` | ⚠️部分迁移 | **缺失**：源查询ReportLineData表，目标改为内存统计 |

### 4.6 报表数据持久化

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 16 | `insertReportLineDataByList(List, String)` | — | ❌未迁移 | 批量插入报表数据行 |
| 17 | `statisticsTotalData(List)` | — | ❌未迁移 | 统计全国汇总(conditionValue/totalValue/specificValue) |
| 18 | `keepReportLineData()` | — | ❌未迁移 | **核心方法**：自动统计并持久化5类报表(指派率、跟踪率、闭环率、实施方式、质量)，源码每月自动执行 |
| 19 | `updateQualityData(List, Map)` [private] | — | ❌未迁移 | 更新各办事处闭环总数 |

### 4.7 报表查询

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 20 | `queryReportLineImplWayData(String, String)` | — | ❌未迁移 | 实施方式详情查询 |
| 21 | `queryReportSettingTimes(String, String)` | — | ❌未迁移 | 报表设置时间查询 |
| 22 | `queryReportTableAssignedData(ReportQueryParam)` | — | ❌未迁移 | 指派率表格数据(含全国汇总) |
| 23 | `queryReportTableTraceData(ReportQueryParam)` | — | ❌未迁移 | 跟踪率表格数据 |
| 24 | `queryReportTableQualityData(ReportQueryParam)` | — | ❌未迁移 | 质量表格数据 |
| 25 | `queryReportTableClosedData(ReportQueryParam)` | — | ❌未迁移 | 闭环率表格数据 |

### 4.8 统计概览

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 26 | `queryStatisticsSummarize()` | `getReportOverview()` | ⚠️部分迁移 | **缺失**：源查询5个独立指标(totalNum/engineeringTypeNum/commonTypeNum/assignedNum/traceNum)，目标简化为3个(total/active/closed) |

### 4.9 项目汇总

| # | 源方法 | 目标方法 | 状态 | 说明 |
|---|--------|----------|------|------|
| 27 | `queryProjectSummaryStatus(Map)` | `queryProjectSummaryStatus(Map)` | ⚠️部分迁移 | **缺失**：源直接SQL查询，目标改为内存分组+部门名称关联 |

### 4.10 ReportServiceImpl 统计

| 状态 | 数量 | 占比 |
|------|------|------|
| ✅完全迁移 | 0 | 0% |
| ⚠️部分迁移 | 12 | 44.4% |
| ❌未迁移 | 15 | 55.6% |
| **合计** | **27** | **100%** |

---

## 5. 汇总统计

### 5.1 各文件迁移状态

| 源文件 | 行数 | 目标文件 | ✅完全 | ⚠️部分 | ❌未迁移 | 总方法 | 迁移率 |
|--------|------|----------|--------|--------|----------|--------|--------|
| ProjectServiceImpl.java | 3907 | PmsProjectServiceImpl.java | 5 | 35 | 89 | 129 | 31.0% |
| ProjectPlanServiceImpl.java | 24 | (无对应) | 0 | 0 | 1 | 1 | 0% |
| PresalesServiceImpl.java | 851 | PmsPresalesServiceImpl.java | 2 | 13 | 19 | 34 | 50.0% |
| ReportServiceImpl.java | 462 | ReportServiceImpl.java | 0 | 12 | 15 | 27 | 44.4% |
| **合计** | **5244** | — | **7** | **60** | **124** | **191** | **35.1%** |

### 5.2 关键缺失模块

| 优先级 | 缺失模块 | 影响范围 | 建议 |
|--------|----------|----------|------|
| **P0** | Activiti工作流集成 | Presales全部审批流程 | 需决策是否保留Activiti或改用其他方案 |
| **P0** | 项目创建完整逻辑(insertProject) | 项目管理核心 | 需补充项目组/合同/关系三表写入、SAP产品同步 |
| **P0** | 闭环条件判断(canCloseLoop) | 项目闭环流程 | 需补充安装数=发货数、回访完成、交付件完整等判断 |
| **P0** | 通知邮件系统 | 全部模块 | 需集成邮件发送服务 |
| **P1** | 周报管理(9个方法) | 项目经理日常工作 | 完整功能模块未迁移 |
| **P1** | 质量管理临时表机制 | ReportService | createQualityTmpTable/queryOfficeQuality等 |
| **P1** | 报表数据持久化(keepReportLineData) | ReportService | 月度自动统计核心功能 |
| **P1** | 维保管理 | ProjectService | 服务交付、周期计算、邮件通知 |
| **P2** | 总代借货项目合并 | ProjectService | 临时表+新旧合同处理 |
| **P2** | 周报Excel/Word导出 | ProjectService | 模板填充生成 |
| **P2** | 督导管理 | ProjectService | 完整功能模块未迁移 |

### 5.3 架构差异说明

1. **工作流引擎**：源码深度集成Activiti 5.x，目标完全移除。Presales的审批流程（启动→SM审批→PM审批→EM审批→闭环）在目标中简化为状态机模式。
2. **ORM框架**：源码使用iBatis/MyBatis XML映射，目标使用MyBatis-Plus LambdaQueryWrapper。
3. **分页查询**：源码使用自定义DisplayParam，目标使用MyBatis-Plus IPage。
4. **文件上传**：源码使用ServletActionContext获取物理路径，目标未实现物理文件操作。
5. **邮件通知**：源码使用NotificationTemplateUtil模板引擎，目标注释掉了所有通知代码。
6. **项目状态**：源码使用pm_project_state独立表管理(planState/executionState/closeProcessState)，目标简化为project单字段。

---

> **结论**：本批次4个Service文件的迁移完成度约为35%，大量核心业务逻辑（尤其是Activiti工作流集成、项目创建完整流程、报表数据持久化）尚未迁移。建议按P0→P1→P2优先级逐步补齐。
