# Service层迁移比对报告（第三批）

> 比对时间: 2026-07-01
> 比对范围: PmClosedLoopServiceImpl, PmClosedLoopQuesnaireServiceImpl, WorkFlowServiceImpl, WorkSpaceServiceImpl, RoleManageServiceImpl, UserManageServiceImpl
> 源目录: PMS-struts/src/com/dp/plat/service/
> 目标目录: PMS-springboot/pms-service/src/main/java/com/dp/plat/service/impl/

---

## 总览

| # | 源文件 | 目标文件 | 迁移状态 | 方法覆盖率 |
|---|--------|----------|----------|-----------|
| 1 | PmClosedLoopServiceImpl (879行) | PmClosedLoopServiceImpl | ⚠️ 部分迁移 | 4/25 (16%) |
| 2 | PmClosedLoopQuesnaireServiceImpl (180行) | **无对应文件** | ❌ 未迁移 | 0/13 (0%) |
| 3 | WorkFlowServiceImpl (891行) | **无对应文件** | ❌ 未迁移 | 0/35+ (0%) |
| 4 | WorkSpaceServiceImpl (518行) | WorkSpaceServiceImpl | ⚠️ 部分迁移 | 3/18 (17%) |
| 5 | RoleManageServiceImpl (40行) | SysRoleServiceImpl | ⚠️ 部分迁移 | 1/5 (20%) |
| 6 | UserManageServiceImpl (188行) | SysUserServiceImpl | ⚠️ 部分迁移 | 3/19 (16%) |

**整批评定: ⚠️ 大量逻辑未迁移 — 主要受Activiti工作流引擎移除影响，核心闭环审批流程全部缺失**

---

## 1. PmClosedLoopServiceImpl

**源文件**: `service/PmClosedLoopServiceImpl.java` (879行)
**目标文件**: `service/impl/PmClosedLoopServiceImpl.java` (167行)

### 核心差异说明

源系统基于 **Activiti工作流引擎** 实现闭环审批全流程，包含PM发起→SM审核→CB回访→CL闭环的多级审批链，每级都涉及流程变量设置、任务认领/办理、邮件通知。目标系统**完全移除了Activiti依赖**，改为简单的状态字段管理，审批流程被大幅简化。

### 方法比对明细

| # | 源方法 | 目标方法 | 状态 | 备注 |
|---|--------|----------|------|------|
| 1 | `addPmCLApply(WorkflowCommonParam, PmClEvaluationHeader, Project)` | `pmApply(PmClosedLoop)` | ⚠️ 部分迁移 | 缺：回访问卷自动关联(cbQuesnaire)、根据回访状态分流(CB/CL)、Activiti流程启动、任务办理、邮件通知、闭环流程状态更新 |
| 2 | `addSmCLApply(WorkflowCommonParam, PmClEvaluationHeader, Project)` | `smApply(PmClosedLoop)` | ⚠️ 部分迁移 | 缺：回访状态判断分流、驳回时退回PM(B码处理)、回访问卷关联插入、Activiti任务办理、邮件发送、闭环流程状态更新 |
| 3 | `addCbCLApplyQues(WorkflowCommonParam, PmClEvaluationHeader, Project, PmClQuesnaireResultHeader, List<PmClQuesnaireResultLine>)` | — | ❌ 未迁移 | 提交测评问卷信息（新建/更新问卷结果），包含头信息+行信息的完整CRUD |
| 4 | `addCbCLApply(WorkflowCommonParam, PmClEvaluationHeader, Project)` | `cbApply(PmClosedLoop)` | ⚠️ 部分迁移 | 缺：驳回→PM、无法回访→SM的分流逻辑、评估头更新vs新增判断、Activiti任务认领/办理、邮件通知 |
| 5 | `addClCLApply(WorkflowCommonParam, PmClEvaluationHeader, Project)` | `clApply(PmClosedLoop)` | ⚠️ 部分迁移 | 缺：驳回→PM分流、项目状态更新为已闭环(updateProjectStatus)、Activiti任务办理、邮件通知 |
| 6 | `getProjectSefTaskId(List<Project>)` | — | ❌ 未迁移 | 获取私有任务Id，依赖Activiti |
| 7 | `getProjectPubTaskId(List<Project>)` | — | ❌ 未迁移 | 获取公有任务Id，依赖Activiti |
| 8 | `querymaxDefinitionObjByKey(String, WorkflowCommonParam)` | — | ❌ 未迁移 | 查询最新流程定义 |
| 9 | `queryProcessVarMap(Project)` | — | ❌ 未迁移 | 获取流程状态变量 |
| 10 | `queryTaskByBussinessKey(Project)` | — | ❌ 未迁移 | 按业务key查询任务 |
| 11 | `queryTaskByBussinessKeyAndUser(Project, String)` | — | ❌ 未迁移 | 按业务key+用户查询任务 |
| 12 | `getNextAssignPer(String roleStr)` | — | ❌ 未迁移 | 获取下一级审核人员（按角色筛选有效用户） |
| 13 | `mailPerson(Project, int, PmClEvaluationHeader, int, String)` | — | ❌ 未迁移 | 复杂邮件通知逻辑，按状态组合选择模板、收集收件人 |
| 14 | `getMail(String, Map, String, String)` | — | ❌ 未迁移 | 邮件模板替换+发送 |
| 15 | `defaultParaMap(Project, PmClEvaluationHeader)` | — | ❌ 未迁移 | 邮件模板默认参数构建 |
| 16 | `isPassCb(Project)` | — | ❌ 未迁移 | 判断项目是否已通过回访 |
| 17 | `isCallBack(int projectId)` | — | ❌ 未迁移 | 查询是否有回访问卷 |
| 18 | `deletePmClEvaRecur(PmClEvaluationHeader)` | — | ❌ 未迁移 | 递删评估头+问卷结果头+问卷结果行 |
| 19 | `queryPmEvaluationHeaderList(PmClEvaluationHeader)` | — | ❌ 未迁移 | 查询评估头列表 |
| 20 | `queryEvaluationHeaderMap(PmClEvaluationHeader)` | — | ❌ 未迁移 | 查询评估头Map |
| 21 | `queryPmClQuesResultHeaderList(PmClQuesnaireResultHeader)` | — | ❌ 未迁移 | 查询问卷结果头 |
| 22 | `queryPmClQuesResultLineList(PmClQuesnaireResultLine)` | — | ❌ 未迁移 | 查询问卷结果行 |
| 23 | `queryEvaluationHeaderObjMap(PmClEvaluationHeader, String)` | — | ❌ 未迁移 | 查询评估头对象Map（含排序） |
| 24 | `updateEvaluationHeaderNextAcceptPerson(HashMap)` | — | ❌ 未迁移 | 更新下一审批人 |
| 25 | `updateProjectCloseProcessState(Project, int)` | — | ❌ 未迁移 | 更新项目闭环流程状态（含canCloseLoop判断、回访流程检查） |

**目标新增方法（源中无对应）**:
- `queryClosedLoopPage()` — 分页查询（新设计）
- `getDetail(Long id)` — 获取详情（新设计）
- `approve(Long id, String comment, boolean approved, String role)` — 通用审批（简化版，替代多级审批）
- `queryByProject(Long projectId)` — 按项目查询历史
- `queryRunningByProject(Long projectId)` — 查询进行中的申请
- `cantClose(Long id, String reason)` — 无法闭环处理

---

## 2. PmClosedLoopQuesnaireServiceImpl

**源文件**: `service/PmClosedLoopQuesnaireServiceImpl.java` (180行)
**目标文件**: **无对应文件** ❌

> 目标系统存在Mapper层文件（PmClosedLoopQuesnaireMapper, PmClosedLoopQuesnaireLineMapper, PmClosedLoopQuesnaireOptMapper, PmClQuesnaireResultHeaderMapper, PmClQuesnaireResultLineMapper），但**无Service层实现**。问卷相关逻辑可能通过Controller直接调用Mapper，或在CallBackServiceImpl中部分处理。

### 方法比对明细

| # | 源方法 | 目标方法 | 状态 | 备注 |
|---|--------|----------|------|------|
| 1 | `insertQuesnaireHeader(PmClosedLoopQuesnaire)` | — | ❌ 未迁移 | 插入问卷模板头（含自动生成编号） |
| 2 | `selectQuesnaireHeaderList(PmClosedLoopQuesnaire, DisplayParam)` | — | ❌ 未迁移 | 查询问卷模板头列表 |
| 3 | `insertQuesnaireLineOptList(PmClosedLoopQuesnaireLine, List<PmClosedLoopQuesnaireOpt>)` | — | ❌ 未迁移 | 批量插入问题行+选项（问答题不插入选项） |
| 4 | `updateQuesLineOpt(PmClosedLoopQuesnaireLine, List<PmClosedLoopQuesnaireOpt>) | — | ❌ 未迁移 | 更新问题行+选项（删旧插新） |
| 5 | `deleteQuesLine(PmClosedLoopQuesnaireLine)` | — | ❌ 未迁移 | 删除问题行+选项+重排序 |
| 6 | `deleteQuesHeader(PmClosedLoopQuesnaire)` | — | ❌ 未迁移 | 删除问卷头+所有行+所有选项 |
| 7 | `updateEffecticeStart(PmClosedLoopQuesnaire)` | — | ❌ 未迁移 | 生效问卷（失效同类旧问卷） |
| 8 | `queryPmClQuesnaireLineList(PmClosedLoopQuesnaireLine, String)` | — | ❌ 未迁移 | 查询问题行列表 |
| 9 | `queryPmClosedLoopQuesnaireOptList(PmClosedLoopQuesnaireOpt, String)` | — | ❌ 未迁移 | 查询选项列表 |
| 10 | `queryPmClosedLoopQuesnaireOptMap(PmClosedLoopQuesnaireOpt)` | — | ❌ 未迁移 | 查询选项Map |
| 11 | `updateQuesHeader(PmClosedLoopQuesnaire)` | — | ❌ 未迁移 | 更新问卷头 |
| 12 | `updateQuesStatus(PmClosedLoopQuesnaire)` | — | ❌ 未迁移 | 更新问卷状态 |
| 13 | `addQuestionnaireResult(PmClQuesnaireResultHeader, List<PmClQuesnaireResultLine>)` | — | ❌ 未迁移 | 插入问卷结果（头+行） |

**评定: ❌ 完全未迁移 — 13个方法全部缺失，问卷模板管理和问卷结果处理逻辑未迁移**

---

## 3. WorkFlowServiceImpl

**源文件**: `service/WorkFlowServiceImpl.java` (891行)
**目标文件**: **无对应文件** ❌

> 目标系统**完全移除了Activiti工作流引擎依赖**，无任何WorkFlowService实现。这是影响面最大的缺失，导致所有审批流程（闭环、售前、分包、回退等）均无流程引擎支撑。

### 方法比对明细

| # | 源方法 | 状态 | 备注 |
|---|--------|------|------|
| **流程部署** | | | |
| 1 | `deployFlow(String, File)` | ❌ 未迁移 | 部署流程定义（ZIP） |
| 2 | `listDeployments()` | ❌ 未迁移 | 列出所有部署 |
| 3 | `listProcessDefinition()` | ❌ 未迁移 | 列出所有流程定义 |
| 4 | `delDeployment(String)` | ❌ 未迁移 | 删除部署 |
| 5 | `getInputStream(String, String)` | ❌ 未迁移 | 获取流程图资源 |
| 6 | `getProcessDefinitionByClassType(String)` | ❌ 未迁移 | 按类型获取流程定义 |
| 7 | `querymaxdeploymentidByBean(String)` | ❌ 未迁移 | 获取最新部署ID |
| 8 | `querymaxDefinitionObjByKey(String, WorkflowCommonParam)` | ❌ 未迁移 | 查询最新流程定义 |
| 9 | `getProcessDefinitionByTaskId(String)` | ❌ 未迁移 | 按任务获取流程定义 |
| **流程实例** | | | |
| 10 | `startProcess(String, String, Map)` | ❌ 未迁移 | 启动流程实例 |
| 11 | `deleteProcessInstance(String, String)` | ❌ 未迁移 | 删除流程实例 |
| 12 | `getBusinessObjId(String)` | ❌ 未迁移 | 获取业务对象ID |
| 13 | `getHistBusinessObjId(String)` | ❌ 未迁移 | 获取历史业务对象ID |
| 14 | `getFormKey(String)` | ❌ 未迁移 | 获取表单Key |
| **任务操作** | | | |
| 15 | `findPersonalTask(String)` | ❌ 未迁移 | 查询个人待办 |
| 16 | `findPersonalTask(String, String)` | ❌ 未迁移 | 按流程实例查询个人待办 |
| 17 | `findAllRunTask()` | ❌ 未迁移 | 查询所有运行中任务 |
| 18 | `getTaskByInstId(String)` | ❌ 未迁移 | 按流程实例查询任务 |
| 19 | `getTaskIdByProcessInstanceId(String, String)` | ❌ 未迁移 | 按流程实例+办理人查询任务 |
| 20 | `queryCurrentApprover(String)` | ❌ 未迁移 | 查询当前审批人 |
| 21 | `queryTaskByBussinessKey(String)` | ❌ 未迁移 | 按业务key查询任务 |
| 22 | `queryTaskByBussinessKeyUser(String, String)` | ❌ 未迁移 | 按业务key+用户查询任务 |
| 23 | `queryPubTaskByBussinessKeyUser(String, String)` | ❌ 未迁移 | 按业务key查询候选任务 |
| 24 | `queryAllSelfTaskList(String)` | ❌ 未迁移 | 查询用户所有待办 |
| 25 | `queryAllPubTaskList(String)` | ❌ 未迁移 | 查询用户所有候选任务 |
| 26 | `claimTask(String, String)` | ❌ 未迁移 | 认领任务 |
| 27 | `assigneeTask(String, String, String)` | ❌ 未迁移 | 指派任务 |
| 28 | `submitTask(WorkflowCommonParam)` | ❌ 未迁移 | 提交任务（含复杂业务类型分支） |
| 29 | `submitTaskNoComment(WorkflowCommonParam, Map)` | ❌ 未迁移 | 提交任务（无批注） |
| 30 | `submitSelfTask(WorkflowCommonParam, Map)` | ❌ 未迁移 | 自提交任务（含批注） |
| 31 | `submitTaskSystemAuto(Task)` | ❌ 未迁移 | 系统自动提交 |
| 32 | `doSelfTask(Task, String, String, Map)` | ❌ 未迁移 | 办理自身任务 |
| 33 | `getProcessComments(String, String)` | ❌ 未迁移 | 获取流程批注 |
| 34 | `getCurrentActivityCoordinates(String)` | ❌ 未迁移 | 获取当前活动坐标 |
| 35 | `isExistNextNode(String, String)` | ❌ 未迁移 | 判断下一节点 |
| **历史查询** | | | |
| 36 | `findHisProcess()` | ❌ 未迁移 | 查询历史流程 |
| 37 | `findHistoricPersonalTask(String)` | ❌ 未迁移 | 查询个人已办 |
| 38 | `queryHisProcessInstanceByIds(Set)` | ❌ 未迁移 | 按ID集合查询历史流程 |
| **审批意见** | | | |
| 39 | `addSelfActComment(...)` (多个重载) | ❌ 未迁移 | 插入自定义审批意见 |
| 40 | `updateSelfActComment(...)` | ❌ 未迁移 | 更新审批意见 |
| 41 | `queryActComment(...)` | ❌ 未迁移 | 查询审批意见 |
| 42 | `updateApplytableInfo(...)` | ❌ 未迁移 | 更新申请表信息 |
| **流程变量** | | | |
| 43 | `queryProcessVarMap(String)` | ❌ 未迁移 | 查询流程变量 |
| 44 | `setVariable(...)` | ❌ 未迁移 | 设置流程变量 |
| **工作台** | | | |
| 45 | `findRunSelfTaskList(...)` | ❌ 未迁移 | 查询运行中任务列表 |
| 46 | `findHisSelfTaskList(...)` | ❌ 未迁移 | 查询历史任务列表 |
| 47 | `findDpActProcTypeList()` | ❌ 未迁移 | 查询流程类型 |
| 48 | `getWorkFlowCountMap(...)` | ❌ 未迁移 | 获取工作流统计 |
| 49 | `getRunTask(...)` | ❌ 未迁移 | 获取运行中任务 |
| 50 | `getRunVariable(...)` | ❌ 未迁移 | 获取运行中变量 |
| **委派** | | | |
| 51 | `insertProcdefDelegate(...)` | ❌ 未迁移 | 插入委派记录 |
| 52 | `findProcdefDelegateList(...)` | ❌ 未迁移 | 查询委派列表 |
| 53 | `findProcdefDelegateById(int)` | ❌ 未迁移 | 按ID查询委派 |
| 54 | `updateProcdefDelegate(...)` | ❌ 未迁移 | 更新委派 |
| **流程定义** | | | |
| 55 | `getProcdef(Procdef)` | ❌ 未迁移 | 查询流程定义 |

**评定: ❌ 完全未迁移 — 目标系统未引入任何工作流引擎，55+个方法全部缺失。这是整个迁移中影响面最大的缺失项。**

---

## 4. WorkSpaceServiceImpl

**源文件**: `service/WorkSpaceServiceImpl.java` (518行)
**目标文件**: `service/impl/WorkSpaceServiceImpl.java` (336行)

### 核心差异说明

源系统通过Activiti API获取待办任务，结合闭环评估头信息组装工作台视图。目标系统改为直接查询数据库（Mapper层），移除了Activiti依赖。部分方法被合并或重新设计。

### 方法比对明细

| # | 源方法 | 目标方法 | 状态 | 备注 |
|---|--------|----------|------|------|
| 1 | `queryPmCLTaskList()` | `queryBusinessTaskList(procKey, username)` | ⚠️ 部分迁移 | 原逻辑：合并Activiti私有+公有任务→评估头映射→组装DpActProcDesc。目标：按procKey参数分发查询，闭环部分简化为Mapper查询，丢失了评估头映射和过程类型判断逻辑 |
| 2 | `queryPmCLHisTaskList()` | `querySelfHistoryTaskList(params)` | ⚠️ 部分迁移 | 原逻辑：按evaluationPeopleId+status查评估头→组装历史列表。目标：简化为Mapper查询 |
| 3 | `getprojectcodelistbyusername(String)` | — | ❌ 未迁移 | 查询用户项目代码列表 |
| 4 | `getprojectcodelistfrombeforebyusername(String)` | — | ❌ 未迁移 | 查询用户前置项目代码列表 |
| 5 | `getprojectbyapplyid(int)` | — | ❌ 未迁移 | 按申请ID查项目代码 |
| 6 | `getapplyidsfromorderbyusername(String)` | — | ❌ 未迁移 | 查询用户订单申请ID列表 |
| 7 | `getprojectbyapplyidorder(int)` | — | ❌ 未迁移 | 按订单申请ID查项目代码 |
| 8 | `querybusinessorderprojectcodelist(String)` | — | ❌ 未迁移 | 查询商务订单项目代码列表 |
| 9 | `queryProductFirstCodeByUsername(String)` | — | ❌ 未迁移 | 查询用户首个产品代码 |
| 10 | `queryConcatFirstCode(String)` | — | ❌ 未迁移 | 查询合同首个代码 |
| 11 | `queryActRunTask(String)` | — | ❌ 未迁移 | 查询Activiti运行中任务 |
| 12 | `checkNotificationList(String)` | `querySystemNotifications(username)` | ⚠️ 部分迁移 | 原返回`List<Notification>`，目标返回`List<Map>`，实现依赖Mapper |
| 13 | `updateNotificationState(int)` | `updateNotificationState(int)` | ✅ 完全迁移 | — |
| 14 | `queryPmTaskList(TaskQueryParam, DisplayParam)` | `queryDailyTaskList(params)` | ⚠️ 部分迁移 | 原逻辑含角色过滤（工程管理部/服务经理/项目经理），目标将过滤逻辑移至Controller层 |
| 15 | `querySelfHistoryTaskList(TaskQueryParam, DisplayParam)` | `querySelfHistoryTaskList(params)` | ⚠️ 部分迁移 | 签名变更，原含DisplayParam分页 |
| 16 | `queryProjectBackTaskList()` | `queryBusinessTaskList("ProjectBack", username)` | ⚠️ 部分迁移 | 合并到统一方法 |
| 17 | `queryProjectTrackTaskList()` | `queryBusinessTaskList("ProjectTrack", username)` | ⚠️ 部分迁移 | 合并到统一方法 |
| 18 | `queryNotifyList(TaskQueryParam, DisplayParam)` | `queryNotifyList(params)` | ⚠️ 部分迁移 | 原含角色过滤逻辑 |
| 19 | `queryCallBackTaskList()` | `queryBusinessTaskList("CLProcess", username)` | ⚠️ 部分迁移 | 合并到统一方法 |
| 20 | `queryPresalesTaskList()` | `queryBusinessTaskList("Presales", username)` | ⚠️ 部分迁移 | 合并到统一方法 |
| 21 | `queryCallbackHisList()` | — | ❌ 未迁移 | 查询回访历史 |
| 22 | `queryProbTaskList()` | `queryProbTaskList(username)` | ⚠️ 部分迁移 | 原含角色判断（管理员/技术支持/研发），目标简化 |
| 23 | `querySubcontractTaskList()` / `querySubcontractTaskList(Map)` | `querySubcontractTaskList(params)` | ⚠️ 部分迁移 | 原含5种角色分组查询（工程管理部/回访/区域主管/服务经理/财务），目标大幅简化 |
| 24 | `queryProjectSupervisionTask(HashMap)` | `queryBusinessTaskList("ProjectSupervision", username)` | ⚠️ 部分迁移 | 合并到统一方法 |

**目标新增方法（源中无对应）**:
- `getDashboardData(username)` — 仪表盘数据聚合
- `getPendingTasks(username)` — 简化版待办任务
- `getRecentNotifications(username)` — 最近通知
- `queryPresalesTasks(username)` — 售前任务
- `querySubcontractTasks(username)` — 分包任务
- `countDailyTaskList(params)` / `countNotifyList(params)` / `countSelfHistoryTaskList(params)` — 计数方法

---

## 5. RoleManageServiceImpl → SysRoleServiceImpl

**源文件**: `service/RoleManageServiceImpl.java` (40行)
**目标文件**: `service/impl/SysRoleServiceImpl.java` (85行)

### 方法比对明细

| # | 源方法 | 目标方法 | 状态 | 备注 |
|---|--------|----------|------|------|
| 1 | `queryRoleList(DisplayParam, Role)` | `queryRolePage(pageNum, pageSize, roleName)` | ⚠️ 部分迁移 | 改为MyBatis-Plus分页查询，丢失DisplayParam通用分页参数 |
| 2 | `addRoleSubmit(Role, List<RoleMenuPower>)` | `addRole(RoleDTO)` | ⚠️ 部分迁移 | 缺：角色菜单权限(RoleMenuPower)关联插入 |
| 3 | `updateRoleSubmit(Role, List<RoleMenuPower>)` | `updateRole(RoleDTO)` | ⚠️ 部分迁移 | 缺：角色菜单权限(RoleMenuPower)关联更新 |
| 4 | `queryRoleMenuPowerList(Role)` | — | ❌ 未迁移 | 查询角色菜单权限列表 |
| — | — | `deleteRole(Long)` | ✅ 目标新增 | 源中无此方法 |
| — | — | `listAllRoles()` | ✅ 目标新增 | 源中无此方法 |

---

## 6. UserManageServiceImpl → SysUserServiceImpl

**源文件**: `service/UserManageServiceImpl.java` (188行)
**目标文件**: `service/impl/SysUserServiceImpl.java` (160行)

### 方法比对明细

| # | 源方法 | 目标方法 | 状态 | 备注 |
|---|--------|----------|------|------|
| 1 | `queryUserList(DisplayParam, User)` | `queryUserPage(pageNum, pageSize, username, realname, deptId)` | ⚠️ 部分迁移 | 改为分页查询，丢失User对象条件过滤 |
| 2 | `queryUserByUserName(String)` | — | ❌ 未迁移 | 按用户名查询用户（基础方法） |
| 3 | `queryUsersByUserNames(String)` | — | ❌ 未迁移 | 按用户名集合批量查询 |
| 4 | `updatepwdbyusername(String, String)` | — | ❌ 未迁移 | 按用户名更新密码 |
| 5 | `updatepwdbyuser(User)` | — | ❌ 未迁移 | 按用户对象更新密码 |
| 6 | `queryRolelist()` | — | ❌ 未迁移 | 查询角色列表 |
| 7 | `queryAllMenuList()` | — | ❌ 未迁移 | 查询所有菜单 |
| 8 | `queryUserMenuList()` | — | ❌ 未迁移 | 查询用户菜单 |
| 9 | `queryUserByUserId(int)` | `getUserById(Long)` | ⚠️ 部分迁移 | 返回类型不同（User vs UserVO） |
| 10 | `queryUserMenuidsByUserid(int)` | — | ❌ 未迁移 | 查询用户菜单ID字符串 |
| 11 | `addUserInfo(User, String)` | `addUser(UserDTO)` | ⚠️ 部分迁移 | 缺：角色ID格式化(dealWith)、用户菜单关联 |
| 12 | `updateUserInfo(User, String)` | `updateUser(UserDTO)` | ⚠️ 部分迁移 | 缺：默认页面路径验证、用户菜单删除重插、区域权限(areapower)处理 |
| 13 | `queryAllUser()` | — | ❌ 未迁移 | 查询所有用户 |
| 14 | `queryAllUserList(User)` | — | ❌ 未迁移 | 按条件查询所有用户（含状态过滤） |
| 15 | `queryAllUserMap()` | — | ❌ 未迁移 | 查询所有用户Map |
| 16 | `queryUserSizeByUserName(String)` | — | ❌ 未迁移 | 按用户名统计数量 |
| 17 | `queryUserWithRoleId(int)` | — | ❌ 未迁移 | 按角色ID查询用户 |
| 18 | `updateServiceAndProgramMember(ProjectBatchCgMbParam)` | — | ❌ 未迁移 | 批量更新服务经理/项目经理 |
| 19 | `queryUserWithRoleIdAndDpNo(Map)` | — | ❌ 未迁移 | 按角色+部门查询用户 |
| 20 | `queryUserWithRoleIdAndDpNoOrInAreaPower(Map)` | — | ❌ 未迁移 | 按角色+部门或区域权限查询用户 |
| 21 | `queryMailsByRoleAndOfficeCodes(String, Integer)` | — | ❌ 未迁移 | 按角色+办事处查邮件 |
| — | — | `deleteUser(Long)` | ✅ 目标新增 | 源中无此方法 |
| — | — | `resetPassword(Long)` | ✅ 目标新增 | 源中无此方法 |
| — | — | `changePassword(Long, String, String)` | ✅ 目标新增 | 源中无此方法 |

---

## 关键风险总结

### 🔴 高风险 — 核心业务逻辑缺失

1. **Activiti工作流引擎完全移除**: WorkFlowServiceImpl 55+个方法全部未迁移。源系统所有审批流程（闭环、售前、分包、回退等）均依赖Activiti，目标系统仅用简单状态字段替代，**丢失了流程引擎的全部能力**（多级审批、任务认领、流程变量、历史追踪、流程图）。

2. **闭环审批流程严重简化**: 源系统的PM→SM→CB→CL四级审批链（含回访自动跳转、驳回退回、邮件通知）在目标系统中被简化为单一的`approve()`方法，**丢失了全部审批路由逻辑**。

3. **问卷管理完全缺失**: PmClosedLoopQuesnaireServiceImpl 13个方法未迁移，问卷模板CRUD和问卷结果处理逻辑完全丢失。

### 🟡 中风险 — 业务功能不完整

4. **用户查询方法大量缺失**: `queryUserByUserName`、`queryAllUserList`、`queryUsersByUserNames` 等被其他Service广泛调用的基础方法未迁移，会导致上层调用方编译失败。

5. **角色菜单权限未迁移**: `queryRoleMenuPowerList` 及角色-菜单关联CRUD未迁移，权限管理功能不完整。

6. **工作台角色过滤逻辑简化**: 源系统基于UserContext的角色判断（工程管理部/服务经理/项目经理/回访人员/区域主管等）在目标系统中被大幅简化或移至Controller层。

### 🟢 低风险 — 可接受的变更

7. **分页查询统一改为MyBatis-Plus**: DisplayParam → IPage，属于框架升级的正常变更。
8. **getter/setter移除**: Spring Boot使用@Autowired注入，无需手动getter/setter。
9. **目标新增方法**: `getDashboardData`、`getPendingTasks`等为新设计的功能增强。

### 建议优先处理

| 优先级 | 事项 | 影响范围 |
|--------|------|----------|
| P0 | 决定是否引入Flowable/Activiti替代方案 | 全部审批流程 |
| P0 | 迁移`queryUserByUserName`等基础查询方法 | 被多个Service调用 |
| P1 | 补充PmClosedLoopQuesnaireService实现 | 问卷管理功能 |
| P1 | 补充角色菜单权限(RoleMenuPower)CRUD | 权限管理 |
| P2 | 补充工作台角色过滤逻辑 | 工作台体验 |
| P2 | 补充邮件通知Service | 审批通知 |
