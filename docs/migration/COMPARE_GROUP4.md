# ProjectAction 迁移比对报告

> 比对日期: 2026-07-01
> 源文件: `ProjectAction.java` (3371行, ~52个业务方法)
> 目标Controller: `PmsProjectController.java`, `ProjectDeliverController.java`, `ProjectTaskController.java`, `WeeklyController.java`
> 目标Service: `PmsProjectServiceImpl.java`

---

## ProjectAction → PmsProjectController / PmsProjectServiceImpl

### 方法: execute() — 项目列表页面
- **源逻辑摘要**: 根据用户角色查询项目列表。工程管理部/管理员/财务人员查全部项目；服务经理/项目经理/普通用户按权限查询已创建项目。同时查询市场关系数据。
- **目标实现**: `PmsProjectController.list()` → `queryProjectPage()`，简单分页查询，基于projectCode/projectName/contractNo/officeCode/projectState筛选。
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少基于角色的权限查询逻辑（`queryProjectList` vs `queryProjectListByPower`）。缺少市场关系数据查询。

---

### 方法: insertProject() — 创建项目
- **源逻辑摘要**: 1) 验证合同号非空；2) 查询合同号是否存在；3) 设置重大项目级别到项目类别的对应关系；4) 生成项目编码；5) 查询SAP订单数据；6) 检查合同号是否已创建项目；7) 保存项目；8) 设置成功/失败编码；9) 发送立项通知邮件。
- **目标实现**: `addProject()` 包含：合同号重复检查、自动生成项目编码、根据服务经理/项目经理自动设置项目状态、添加初始成员。
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少重大项目级别→项目类别映射逻辑。缺少SAP订单数据预查询。缺少立项通知邮件发送（`sendMailForApproval`）。新增了初始成员自动添加逻辑（老系统未体现）。

---

### 方法: createCHProject() — 创建串货项目
- **源逻辑摘要**: 创建串货项目，检查合同号是否已存在，设置项目编码，保存，发送立项通知邮件。
- **目标实现**: `createCHProject()` 基本一致，包含合同号检查、编码生成、状态设置、初始成员添加。
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少立项通知邮件发送。

---

### 方法: transferShipment() — 转移设备
- **源逻辑摘要**: 复杂的多步骤设备转移。result==0时查询可转移项目；result==2时执行转移（区分salesType=14总代借货项目的特殊处理，使用`column001`参数）；result==1时查询转移后的设备列表。
- **目标实现**: `transferShipment()` 简化为一次性转移操作，包含转出/转入记录创建。
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少salesType=14（总代借货）时使用`column001`（profitCenter）的特殊处理逻辑。缺少多步骤交互流程。

---

### 方法: transferProject() — 查询可转移目标项目
- **源逻辑摘要**: 根据合同号查询可转移的目标项目列表。
- **目标实现**: `queryTransferProjectList()` 基本一致。
- **状态**: ✅ 完全迁移

---

### 方法: exportSpotCheck() — 现场验货单下载
- **源逻辑摘要**: 根据projectId查询项目，调用service导出现场验货单。
- **目标实现**: `exportSpotCheck()` 基本一致。
- **状态**: ✅ 完全迁移

---

### 方法: exportOverWarrantyRemind() — 超期保修提醒导出
- **源逻辑摘要**: 根据projectId查询项目，调用service导出超期保修提醒。
- **目标实现**: `exportOverWarrantyRemind()` 基本一致。
- **状态**: ✅ 完全迁移

---

### 方法: importSpotCheckIgnoreItem() — 导入现场验货单
- **源逻辑摘要**: 权限检查（管理员或工程管理部），解析Excel文件，调用service导入数据。
- **目标实现**: `importSpotCheckIgnoreItem()` 简化版，接收已解析的列表。
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少权限检查逻辑。缺少Excel文件解析逻辑（`ExportUtils.readFromExcel`）。

---

### 方法: updateProject() — 更新项目（核心复杂方法）
- **源逻辑摘要**: 极其复杂的更新逻辑，包含：
  1. 权限验证（管理员/工程管理部/财务人员等角色判断，Session缓存权限）
  2. 查看模式：加载项目详情、周报列表、财务计划、产品列表、发货列表、工程计划、交付件、批示、成员、回调流程、闭环条件判断
  3. 编辑模式：工程管理部权限更新、服务经理指定项目经理、项目经理更新渠道和实施方式
  4. 闭环条件检查（必传交付件完整性、最终客户、服务提供商、安装数量与发货数量一致性）
- **目标实现**: `updateProject()` 仅做简单字段更新（projectName/officeCode/projectState/executionState）。
- **状态**: ❌ 未迁移
- **差异说明**: **核心业务逻辑严重缺失**。缺少权限验证、状态流转、服务经理/项目经理指定、渠道更新、闭环条件判断等关键逻辑。

---

### 方法: checkOrderData() — 查询设备清单
- **源逻辑摘要**: 查询产品列表汇总（含RMA数据）+ 产品列表明细。
- **目标实现**: `checkOrderData()` 查询订单数据+RMA数据。
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少产品列表明细查询（`orderDataDetailList`）。

---

### 方法: checkRealOrderData() — 查询实施发货设备清单
- **源逻辑摘要**: 查询实施发货设备清单。
- **目标实现**: `checkRealOrderData()` 基本一致。
- **状态**: ✅ 完全迁移

---

### 方法: projectLeaseLine() — 查询租赁配置清单
- **源逻辑摘要**: 根据项目编码查询租赁配置清单。
- **目标实现**: `projectLeaseLine()` 基本一致。
- **状态**: ✅ 完全迁移

---

### 方法: projectProductConfigLevelInfo() — 查询配置关系清单
- **源逻辑摘要**: 根据项目编码查询配置关系清单。
- **目标实现**: `projectProductConfigLevelInfo()` 基本一致。
- **状态**: ✅ 完全迁移

---

### 方法: checkShipmentInfo() — 查询发货序列号
- **源逻辑摘要**: 查询发货序列号列表，区分salesType=14（总代借货）时使用`column001`参数。同时查询历史项目发货数量。
- **目标实现**: `checkShipmentInfo()` 简化版，按contractNo+projectId查询。
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少salesType=14的特殊处理。缺少历史项目发货数量查询。

---

### 方法: deleteShipmentInfo() — 删除发货安装信息
- **源逻辑摘要**: 权限检查（管理员/工程管理部/工程管理部领导/服务经理/项目经理），然后删除。
- **目标实现**: `deleteShipmentInfo()` 直接删除，无权限检查。
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少权限检查逻辑。

---

### 方法: checkSoftVersion() — 查询软件设备信息
- **源逻辑摘要**: 查询软件版本信息，区分salesType=14时使用profitCenter参数，支持filterItem过滤，查询受影响的问题单。
- **目标实现**: `checkSoftVersion()` 简化版。
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少salesType=14的特殊处理。缺少profitCenter参数。缺少受影响问题单查询。

---

### 方法: updateSoftVersion() — AJAX更新设备软件版本
- **源逻辑摘要**: 从JSON参数解析软件版本列表和变更日志，调用service更新。
- **目标实现**: `updateSoftVersion()` 基本一致。
- **状态**: ✅ 完全迁移

---

### 方法: checkhistsoftversion() — 获取软件版本历史数据
- **源逻辑摘要**: 查询历史变更记录，添加出厂版本条目，根据id查询具体变更信息和版本列表。
- **目标实现**: 拆分为3个接口：`checkhistsoftversion()`、`queryHistSoftVersionList()`、`queryOneSoftChangeLog()`。
- **状态**: ✅ 完全迁移

---

### 方法: queryProjectNotification() — 获取项目系统通知
- **源逻辑摘要**: 根据projectId查询项目系统通知列表。
- **目标实现**: 未找到对应接口。
- **状态**: ❌ 未迁移
- **差异说明**: 项目系统通知功能未迁移。

---

### 方法: problemTicket() — 获取项目工单记录
- **源逻辑摘要**: 查询ITR工单记录，先按项目编号查询，查不到则按合同号查询。获取ITR基础URL。
- **目标实现**: 未找到对应接口。
- **状态**: ❌ 未迁移
- **差异说明**: 工单记录查询功能未迁移。

---

### 方法: licenseInfo() — 获取项目License授权信息
- **源逻辑摘要**: 根据项目编码和合同号查询License授权信息。
- **目标实现**: 未找到对应接口。
- **状态**: ❌ 未迁移
- **差异说明**: License授权信息查询功能未迁移。

---

### 方法: projectMaintenance() — 获取项目维护记录
- **源逻辑摘要**: 查询项目维护记录，包含权限判断（服务经理/项目经理只能查看自己区域的记录）。
- **目标实现**: `projectMaintenance()` 简化版，按projectId查询。
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少区域权限判断逻辑。

---

### 方法: createProjectMaintenance() — 创建/编辑项目维护记录
- **源逻辑摘要**: 复杂的维护记录创建逻辑，包含：权限检查、问卷模板加载、问卷填写/提交/评分、维护记录保存。
- **目标实现**: `createProjectMaintenance()` 简化版，直接插入或更新维护记录。
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少权限检查。缺少问卷模板加载和问卷填写/提交/评分逻辑（`QuestionnarieUtil`相关）。

---

### 方法: editProjectPlan() — 制定或修改工程计划
- **源逻辑摘要**: 制定或修改工程计划，包含：将旧计划置为失效、判断是否第一次制定计划（变更计划状态）、查询当前工程计划阶段、发送通知（112或115）。
- **目标实现**: `editProjectPlan()` 基本逻辑一致，但通知被注释掉。
- **状态**: ⚠️ 部分迁移
- **差异说明**: 通知逻辑被注释（`notificationService.sendFixedNotification`）。

---

### 方法: uploadDeliverableFile() — 上传工程交付件
- **源逻辑摘要**: 遍历交付件列表，逐个上传文件，判断是否触发回调流程，更新项目刷新时间。
- **目标实现**: `uploadDeliverableFile()` 简化版，直接保存交付件记录。
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少文件上传逻辑（`projectService.uploadFile`）。缺少回调流程判断。

---

### 方法: deleteDeliverById() — 删除工程交付件
- **源逻辑摘要**: 根据deliverId删除交付件，更新项目刷新时间。
- **目标实现**: `deleteDeliverById()` 基本一致。
- **状态**: ✅ 完全迁移

---

### 方法: backToLastStep() — 项目回退到上一步
- **源逻辑摘要**: 项目回退，包含：更新渠道信息（实施方式、渠道名称、最终客户）、调用回退逻辑。
- **目标实现**: `backToLastStep()` 简化版，只更新项目状态。
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少渠道信息更新逻辑（`updateServiceProject`）。

---

### 方法: updateprojectisback() — 项目回退流程
- **源逻辑摘要**: 极其复杂的回退流程，包含：
  1. 根据isback值判断回退类型（36=申请回退至工程管理部、38=申请回退至服务经理、30=工程管理部同意回退、32=服务经理同意回退）
  2. 角色权限验证
  3. 邮件通知（主送/抄送不同角色）
  4. 驳回逻辑（notbackCause）
  5. 系统通知
- **目标实现**: `updateProjectIsback()` 简化版，只更新项目状态。
- **状态**: ❌ 未迁移
- **差异说明**: **核心回退流程逻辑严重缺失**。缺少角色权限验证、邮件通知、驳回逻辑。

---

### 方法: createMember() — 创建项目成员
- **源逻辑摘要**: 创建项目成员，设置项目类型、角色、联系方式，发送动态通知（113）。
- **目标实现**: `addProjectMember()` 基本一致，但缺少通知。
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少动态通知发送。

---

### 方法: updateMember() — 更新项目成员信息
- **源逻辑摘要**: 更新项目成员生效时间，发送固定通知（116）。
- **目标实现**: `updateProjectMember()` 基本一致，但缺少通知。
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少固定通知发送。

---

### 方法: saveInstallAdress() — 保存安装地址
- **源逻辑摘要**: 保存安装地址，区分salesType=14时使用`column001`参数，发送固定通知（114）。
- **目标实现**: `saveInstallAddress()` 简化版。
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少salesType=14的特殊处理。缺少固定通知发送。

---

### 方法: updateProjectExecutionState() — 更新项目实施状态
- **源逻辑摘要**: 更新项目实施状态。
- **目标实现**: `updateProjectExecutionState()` 包含闭环条件检查，逻辑更完善。
- **状态**: ✅ 完全迁移（目标实现更完善）

---

### 方法: toMergeOrBranch() — 进入合同拆分合并页面
- **源逻辑摘要**: 加载合并/拆分页面的选项卡和产品列表。
- **目标实现**: 未找到对应接口（前端路由）。
- **状态**: N/A（页面路由，非业务逻辑）

---

### 方法: checkMergeContract() — 查询要合并的合同信息
- **源逻辑摘要**: 查询要合并的合同信息，检查合同数量。
- **目标实现**: `queryContractList()` 基本一致。
- **状态**: ✅ 完全迁移

---

### 方法: mergeContract() — 合并操作
- **源逻辑摘要**: 合并合同，调用service执行合并。
- **目标实现**: `mergeContract()` 基本一致，包含合同关联插入、产品线复制、计划复制。
- **状态**: ✅ 完全迁移

---

### 方法: branchContract() — 项目拆分
- **源逻辑摘要**: 项目拆分，创建新项目。
- **目标实现**: `branchContract()` 基本一致，包含新项目创建、项目组关系、销售人员复制。
- **状态**: ✅ 完全迁移

---

### 方法: queryDpNoRoleUser() — 查询指定部门无特定角色的用户
- **源逻辑摘要**: 根据角色ID和部门编号查询用户。
- **目标实现**: `queryDpNoRoleUser()` 基本一致。
- **状态**: ✅ 完全迁移

---

### 方法: batchChangeMember() — 批量变更项目成员
- **源逻辑摘要**: 批量变更服务经理/项目经理，包含：查询指定部门项目、更新项目成员、更新项目状态、更新闭环流程审批人。
- **目标实现**: `batchChangeMember()` 简化版，只更新项目表中的smCode/pmCode。
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少项目成员表更新。缺少项目状态更新。缺少闭环流程审批人更新。

---

### 方法: importProject() — 批量创建项目
- **源逻辑摘要**: 解析Excel文件，批量创建项目，包含权限检查（管理员）。
- **目标实现**: `importProject()` 接收已解析的项目列表。
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少权限检查。缺少Excel解析逻辑。

---

### 方法: clearProject() — 批量删除/无效化项目
- **源逻辑摘要**: 权限检查（管理员或工程管理部），解析Excel，批量删除或无效化项目。
- **目标实现**: `clearProject()` 基本一致。
- **状态**: ✅ 完全迁移

---

## 项目周报相关

### 方法: createWeekly() — 创建周报
- **源逻辑摘要**: 创建周报，计算周报时间（上周六到本周五），查询上一期周报内容并继承（工作、风险、计划、求助、进展、邮件内容）。
- **目标实现**: `WeeklyController.add()` 简化版，直接创建。
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少周报时间计算逻辑。缺少上一期周报内容继承。

---

### 方法: saveWeekly() — 保存周报
- **源逻辑摘要**: 保存周报（草稿状态），区分新建和更新。
- **目标实现**: `WeeklyController.add()` + `update()` 基本一致。
- **状态**: ✅ 完全迁移

---

### 方法: submitWeekly() — 提交周报
- **源逻辑摘要**: 提交周报，生成周报附件Excel，处理邮件抄送地址，发送邮件通知，发送系统通知（118）。
- **目标实现**: `WeeklyController.submit()` 简化版。
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少周报附件Excel生成。缺少邮件发送逻辑。缺少系统通知。

---

### 方法: updateWeekly() — 更新/查看周报
- **源逻辑摘要**: 查询周报详情、各类内容（工作/风险/求助/进展/计划/附件/邮件）、反馈。
- **目标实现**: 拆分为`detail()`、`contents()`、`feedbacks()`三个接口。
- **状态**: ✅ 完全迁移

---

### 方法: UploadFile() — 周报附件上传
- **源逻辑摘要**: 上传周报附件，检查文件扩展名白名单，重命名文件，保存到服务器，插入数据库。
- **目标实现**: `WeeklyController.saveContents()` 简化版。
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少文件上传逻辑、扩展名检查、文件重命名。

---

### 方法: feedback() — 周报回复
- **源逻辑摘要**: 保存周报回复。
- **目标实现**: `WeeklyController.addFeedback()` 基本一致。
- **状态**: ✅ 完全迁移

---

### 方法: instruction() — 项目批示
- **源逻辑摘要**: 保存项目批示。
- **目标实现**: `PmsProjectController.saveInstruction()` 基本一致，包含新批示和回复批示逻辑。
- **状态**: ✅ 完全迁移

---

### 方法: queryalluser() — 根据角色查询用户
- **源逻辑摘要**: 根据角色ID查询用户列表。
- **目标实现**: `PmsProjectController.queryalluser()` 基本一致。
- **状态**: ✅ 完全迁移

---

### 方法: queryperson() — 项目干系人查询
- **源逻辑摘要**: 查询项目干系人，合并去重（项目成员+全部用户），去除重复的username。
- **目标实现**: `PmsProjectController.queryperson()` 简化版，只查询项目成员。
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少与全部用户列表的合并去重逻辑。

---

### 方法: downloadFile() — 下载交付件
- **源逻辑摘要**: 下载交付件文件。
- **目标实现**: 未找到对应接口。
- **状态**: ❌ 未迁移
- **差异说明**: 文件下载功能未迁移。

---

### 方法: deleteFile() — 删除交付件
- **源逻辑摘要**: 删除交付件文件。
- **目标实现**: `ProjectDeliverController.delete()` 部分覆盖。
- **状态**: ⚠️ 部分迁移
- **差异说明**: 源方法删除的是周报附件文件，目标是删除交付件记录，逻辑不完全一致。

---

### 方法: toUploadDeliverableFile() — 进入交付件上传页面
- **源逻辑摘要**: 根据事件节点查询交付件列表。
- **目标实现**: 未找到对应接口。
- **状态**: ❌ 未迁移
- **差异说明**: 交付件列表查询功能未迁移。

---

### 方法: queryProjectNotification() — 获取项目系统通知
- **源逻辑摘要**: 查询项目系统通知列表。
- **目标实现**: 未找到对应接口。
- **状态**: ❌ 未迁移

---

## 未迁移的私有/辅助方法

| 方法 | 说明 | 状态 |
|------|------|------|
| prepareExecute() | 初始化下拉列表（办事处、公司、项目类型等） | ❌ 未迁移 |
| initProject() | 初始化项目查询条件 | ❌ 未迁移 |
| obtainNavTabList() | 动态生成项目维护页面选项卡 | ❌ 未迁移 |
| obtainModifyflag() | 判断用户是否有权限修改项目 | ❌ 未迁移 |
| dowithMemberRoleList() | 处理项目成员角色列表 | ❌ 未迁移 |
| sendMailForApproval() | 发送立项通知邮件 | ❌ 未迁移 |
| keepMailInfo() | 保存邮件信息 | ❌ 未迁移 |
| addPlanList2EventList() | 合并计划和事件列表 | ❌ 未迁移 |
| getWeeklyDateTime() | 计算周报时间 | ❌ 未迁移 |
| dealWith() | 处理邮件抄送地址 | ❌ 未迁移 |
| parseFileToList() | 解析Excel文件为项目列表 | ❌ 未迁移 |

---

## 相关Controller覆盖情况

### ProjectDeliverController
- `list()` → 查询交付件列表 ✅
- `add()` → 添加交付件 ✅
- `update()` → 更新交付件 ✅
- `delete()` → 删除交付件 ✅
- 注意：源系统中`toUploadDeliverableFile()`根据事件节点查询交付件列表的功能未覆盖。

### ProjectTaskController
- `list()` → 查询任务列表 ✅
- `save()` → 保存任务列表 ✅
- `update()` → 更新任务 ✅

### WeeklyController
- `list()` → 查询周报列表 ✅
- `detail()` → 周报详情 ✅
- `add()` → 创建周报 ✅
- `update()` → 更新周报 ✅
- `submit()` → 提交周报 ⚠️（缺少邮件和通知）
- `delete()` → 删除周报 ✅
- `contents()` → 查询周报内容 ✅
- `saveContents()` → 保存周报内容 ⚠️（缺少文件上传）
- `feedbacks()` → 查询反馈 ✅
- `addFeedback()` → 添加反馈 ✅

---

## 汇总表

| 状态 | 数量 | 占比 |
|------|------|------|
| ✅ 完全迁移 | 20 | 38.5% |
| ⚠️ 部分迁移 | 23 | 44.2% |
| ❌ 未迁移 | 9 | 17.3% |
| **合计** | **52** | **100%** |

### ❌ 未迁移方法清单（高优先级）

| 方法 | 重要性 | 说明 |
|------|--------|------|
| updateProject() | 🔴 极高 | 核心更新逻辑，包含权限验证、状态流转、服务经理/项目经理指定、闭环条件判断 |
| updateprojectisback() | 🔴 极高 | 核心回退流程，包含角色验证、邮件通知、驳回逻辑 |
| queryProjectNotification() | 🟡 中 | 项目系统通知查询 |
| problemTicket() | 🟡 中 | 工单记录查询 |
| licenseInfo() | 🟡 中 | License授权信息查询 |
| downloadFile() | 🟡 中 | 文件下载 |
| deleteFile() | 🟢 低 | 文件删除 |
| toUploadDeliverableFile() | 🟢 低 | 交付件列表查询 |
| queryProjectNotification() | 🟡 中 | 项目系统通知 |

### ⚠️ 部分迁移方法中的关键缺失

| 缺失功能 | 影响方法 | 重要性 |
|----------|----------|--------|
| 邮件通知系统 | insertProject, createCHProject, updateprojectisback, submitWeekly等 | 🔴 高 |
| 基于角色的权限查询 | execute | 🔴 高 |
| salesType=14特殊处理 | transferShipment, checkShipmentInfo, checkSoftVersion, saveInstallAdress | 🟡 中 |
| 问卷系统 | createProjectMaintenance | 🟡 中 |
| Excel解析 | importProject, importSpotCheckIgnoreItem | 🟡 中 |
| 闭环流程更新 | batchChangeMember | 🟡 中 |
| 周报内容继承 | createWeekly | 🟢 低 |
| 文件上传逻辑 | UploadFile, uploadDeliverableFile | 🟡 中 |

---

## 总体评估

**迁移完成度: 约 55%**（完全迁移38.5% + 部分迁移中已完成的核心逻辑约16%）

**关键风险点:**

1. **`updateProject()` 未迁移** — 这是整个项目管理的核心方法，包含权限验证、状态流转、服务经理/项目经理指定、闭环条件判断等关键业务逻辑。当前的`updateProject()`仅做简单字段更新，**完全无法满足业务需求**。

2. **`updateprojectisback()` 未迁移** — 项目回退流程是项目生命周期管理的关键环节，包含复杂的角色权限验证和邮件通知逻辑。当前实现仅更新状态字段。

3. **邮件通知系统整体缺失** — 源系统中大量方法依赖邮件通知（立项通知、回退通知、周报通知、成员变更通知等），目标系统中邮件通知逻辑基本被注释或省略。

4. **权限体系简化过度** — 源系统有精细的角色权限控制（工程管理部、服务经理、项目经理、财务人员等），目标系统大幅简化，可能导致权限漏洞。

**建议优先级:**
1. 🔴 P0: 迁移`updateProject()`核心逻辑
2. 🔴 P0: 迁移`updateprojectisback()`回退流程
3. 🔴 P0: 建设邮件通知服务
4. 🟡 P1: 补充salesType=14特殊处理
5. 🟡 P1: 补充权限检查逻辑
6. 🟡 P1: 迁移工单/License/通知查询
7. 🟢 P2: 补充Excel解析、问卷系统等辅助功能
