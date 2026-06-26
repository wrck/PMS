# PMS-springmvc Action 方法级参考文档

> 本文档深度分析 PMS-springmvc 所有 Controller 类的完整方法签名、输入参数、返回值、核心业务逻辑和异常处理机制。

---

## 目录

1. [AbstractController — 控制器基类](#1-abstractcontroller--控制器基类)
2. [ProjectController — 项目管理](#2-projectcontroller--项目管理)
3. [WorkFlowController — 工作流管理](#3-workflowcontroller--工作流管理)
4. [DailyReportController — 日报管理](#4-dailyreportcontroller--日报管理)
5. [DispatchProjectController — 发运项目管理](#5-dispatchprojectcontroller--发运项目管理)
6. [DispatchSettlementController — 发运结算管理](#6-dispatchsettlementcontroller--发运结算管理)
7. [ProjectManageUserController — 项目管理用户](#7-projectmanageusercontroller--项目管理用户)
8. [ProjectMemberController — 项目成员管理](#8-projectmembercontroller--项目成员管理)
9. [ProjectTaskController — 项目任务管理](#9-projecttaskcontroller--项目任务管理)
10. [ProjectAssetController — 项目资产管理](#10-projectassetcontroller--项目资产管理)
11. [ProjectAssetLeakController — 项目资产泄露管理](#11-projectassetleakcontroller--项目资产泄露管理)
12. [IndustryAssetController — 行业资产管理](#12-industryassetcontroller--行业资产管理)
13. [IndustryLeakController — 行业泄露管理](#13-industryleakcontroller--行业泄露管理)
14. [IndustryLeakWarningController — 行业泄露预警](#14-industryleakwarningcontroller--行业泄露预警)
15. [CommonRelatedDataController — 关联数据管理](#15-commonrelateddatacontroller--关联数据管理)
16. [FacilitatorController — 协调员管理](#16-facilitatorcontroller--协调员管理)
17. [WorkBenchController — 工作台](#17-workbenchcontroller--工作台)
18. [StrutsApiController — Struts API 兼容](#18-strutsapicontroller--struts-api-兼容)

---

## 1. AbstractController — 控制器基类

- **包路径**: `com.dp.plat.pms.springmvc.controller`
- **父类**: 无
- **职责**: 提供所有 Controller 类的公共方法，包括 CRUD 操作、权限检查、视图渲染

### 属性

| 属性名 | 类型 | 说明 |
|--------|------|------|
| `urlNameSpace` | String | URL 命名空间 |
| `viewModel` | String | 视图模型名称 |
| `keyword` | String | 关键字字段名 |
| `useTemplate` | Boolean | 是否使用模板 |

### 方法列表

#### `String home(Model model)`
- **URL**: `/`
- **HTTP 方法**: GET
- **功能**: 首页
- **返回值**: String - 视图名称

#### `String list(PageParam pageParam, T v, Model model)`
- **URL**: `/list`
- **HTTP 方法**: GET
- **功能**: 列表查询
- **参数**: 分页参数、查询条件
- **返回值**: String - 视图名称

#### `String findOne(Integer id, Model model)`
- **URL**: `/{id}` 或 `/modals/{id}`
- **HTTP 方法**: GET
- **功能**: 查询详情
- **参数**: `id` - 主键ID
- **返回值**: String - 视图名称

#### `void save(T v, Model model)`
- **URL**: `/save`
- **HTTP 方法**: POST
- **功能**: 保存数据
- **参数**: `v` - 实体对象
- **返回值**: void

#### `void delete(Integer id, Model model)`
- **URL**: `/delete/{id}`
- **HTTP 方法**: POST
- **功能**: 删除数据
- **参数**: `id` - 主键ID
- **返回值**: void

#### `boolean checkPermission(T v, Model model, String... permissions)`
- **功能**: 权限检查
- **参数**: `v` - 实体对象, `permissions` - 权限编码
- **返回值**: boolean - 是否有权限

---

## 2. ProjectController — 项目管理

- **包路径**: `com.dp.plat.pms.springmvc.controller`
- **父类**: `AbstractController<IProjectService, Project, ProjectVO>`
- **URL 命名空间**: `/pm/project`
- **依赖服务**: `IProjectService`, `IProjectHeaderService`, `ProjectService`, `ProjectPlanService`, `IProjectTaskService`, `IIndustryAssetService`, `IIndustryAssetProjectRelationService`, `IIndustryLeakService`, `IProjectManageUserService`

### 方法列表

#### `home(Model model)`
- **URL**: `/pm/project/`
- **HTTP 方法**: GET
- **功能**: 项目管理首页
- **权限**: `project:list`
- **返回值**: String - 视图名称

#### `list(PageParam pageParam, ProjectVO project, Model model)`
- **URL**: `/pm/project/list`
- **HTTP 方法**: GET
- **功能**: 项目列表查询
- **权限**: `project:list`
- **业务逻辑**:
  1. 权限检查
  2. 设置过滤条件：disabled=false
  3. 角色判断：特定角色增加回款信息
  4. 分页查询
  5. 返回列表视图

#### `findOne(Integer id, Model model)`
- **URL**: `/pm/project/{id}` 或 `/pm/project/modals/{id}`
- **HTTP 方法**: GET
- **功能**: 项目详情查询
- **权限**: `project:detail`
- **业务逻辑**:
  1. 权限检查
  2. JSON 请求：查询项目详情、成员、任务
  3. 非 JSON 请求：设置视图参数

#### `save(Project project, Model model)`
- **URL**: `/pm/project/save`
- **HTTP 方法**: POST
- **功能**: 保存项目
- **权限**: `project:edit`
- **业务逻辑**:
  1. 权限检查
  2. 参数校验
  3. 调用 projectService.save()
  4. 返回保存结果

#### `delete(Integer id, Model model)`
- **URL**: `/pm/project/delete/{id}`
- **HTTP 方法**: POST
- **功能**: 删除项目
- **权限**: `project:delete`
- **业务逻辑**:
  1. 权限检查
  2. 调用 projectService.delete()
  3. 返回删除结果

---

## 3. WorkFlowController — 工作流管理

- **包路径**: `com.dp.plat.pms.springmvc.controller`
- **父类**: `AbstractController<IPmWorkFlowService, PmWorkFlow, PmWorkFlowVO>`
- **URL 命名空间**: `/workflow`
- **依赖服务**: `IProcessService`, `TaskService`, `RuntimeService`, `RuntimePageService`, `HistoryService`, `IPmWorkFlowService`, `IProjectHeaderService`, `IProjectTaskService`, `IIndustryAssetService`, `IIndustryLeakService`, `IDispatchProjectService`, `IDispatchSettlementService`

### 方法列表

#### `home(Model model)`
- **URL**: `/workflow/`
- **HTTP 方法**: GET
- **功能**: 工作流管理首页
- **返回值**: String - 视图名称

#### `list(PageParam pageParam, PmWorkFlowVO v, Model model)`
- **URL**: `/workflow/list`
- **HTTP 方法**: GET
- **功能**: 工作流列表查询
- **参数**: 分页参数、查询条件
- **返回值**: String - 视图名称

#### `info(PmWorkFlowVO v, Model model)`
- **URL**: `/workflow/info/list`
- **HTTP 方法**: GET
- **功能**: 工作流信息列表
- **业务逻辑**:
  1. 权限检查
  2. 查询工作流列表
  3. 收集流程实例ID
  4. 查询流程活动列表
- **返回值**: String - 视图名称

#### `findOne(Integer id, Model model)`
- **URL**: `/workflow/{id}` 或 `/workflow/modals/{id}`
- **HTTP 方法**: GET
- **功能**: 查询工作流详情
- **业务逻辑**:
  1. 权限检查
  2. 查询工作流记录
  3. 查询当前任务
  4. 获取流程变量 entity
  5. 装饰实体对象
  6. 查询表单字段列表
- **返回值**: String - 视图名称

#### `complete(Boolean isPass, String content, String data, String taskId, Model model)`
- **URL**: `/workflow/complete/{taskId}`
- **HTTP 方法**: POST
- **功能**: 完成任务
- **参数**:
  - `isPass`: 审批结果（Boolean）
  - `content`: 审批意见（String）
  - `data`: 附加业务数据（String）
  - `taskId`: 任务ID（PathVariable）
- **业务逻辑**:
  1. 查询当前用户
  2. 查询任务并校验权限
  3. 构建流程变量
  4. 调用 processService.complete()
  5. 处理异常
- **返回值**: void（通过 model 设置 status 和 message）

#### `withdrawTask(String instanceId, String userId, Model model)`
- **URL**: `/workflow/withdraw/{instanceId}/{userId}`
- **HTTP 方法**: POST
- **功能**: 撤回任务
- **参数**:
  - `instanceId`: 历史流程节点ID（PathVariable）
  - `userId`: 用户ID（PathVariable）
- **业务逻辑**:
  1. 获取当前用户ID
  2. 调用 processService.withdrawTask()
  3. 返回撤回结果

#### `startProcess(PmWorkFlow pmWorkFlow, Model model)`
- **URL**: `/workflow/startProcess`
- **HTTP 方法**: POST
- **功能**: 启动流程实例
- **参数**: `pmWorkFlow`: 工作流对象
- **业务逻辑**:
  1. 根据 processKey 判断流程类型
  2. 检查操作权限
  3. 查询关联实体
  4. 调用 pmWorkFlowService.startProcess()
  5. 返回 currentTaskId 和 currentProcInstId

---

## 4. DailyReportController — 日报管理

- **包路径**: `com.dp.plat.pms.springmvc.controller`
- **URL 命名空间**: `/dailyReport`
- **依赖服务**: `IDailyReportService`

### 方法列表

#### `home(Model model)`
- **URL**: `/dailyReport/`
- **HTTP 方法**: GET
- **功能**: 日报管理首页
- **返回值**: String - 视图名称

#### `list(PageParam pageParam, DailyReportVO v, Model model)`
- **URL**: `/dailyReport/list`
- **HTTP 方法**: GET
- **功能**: 日报列表查询
- **返回值**: String - 视图名称

#### `findOne(Integer id, Model model)`
- **URL**: `/dailyReport/{id}`
- **HTTP 方法**: GET
- **功能**: 日报详情查询
- **返回值**: String - 视图名称

#### `save(DailyReport dailyReport, Model model)`
- **URL**: `/dailyReport/save`
- **HTTP 方法**: POST
- **功能**: 保存日报
- **返回值**: void

#### `delete(Integer id, Model model)`
- **URL**: `/dailyReport/delete/{id}`
- **HTTP 方法**: POST
- **功能**: 删除日报
- **返回值**: void

---

## 5. DispatchProjectController — 发运项目管理

- **包路径**: `com.dp.plat.pms.springmvc.controller`
- **URL 命名空间**: `/dispatchProject`
- **依赖服务**: `IDispatchProjectService`

### 方法列表

#### `home(Model model)`
- **URL**: `/dispatchProject/`
- **HTTP 方法**: GET
- **功能**: 发运项目管理首页
- **返回值**: String - 视图名称

#### `list(PageParam pageParam, DispatchVO v, Model model)`
- **URL**: `/dispatchProject/list`
- **HTTP 方法**: GET
- **功能**: 发运项目列表查询
- **返回值**: String - 视图名称

#### `findOne(Integer id, Model model)`
- **URL**: `/dispatchProject/{id}`
- **HTTP 方法**: GET
- **功能**: 发运项目详情查询
- **返回值**: String - 视图名称

#### `save(DispatchProject dispatch, Model model)`
- **URL**: `/dispatchProject/save`
- **HTTP 方法**: POST
- **功能**: 保存发运项目
- **返回值**: void

#### `delete(Integer id, Model model)`
- **URL**: `/dispatchProject/delete/{id}`
- **HTTP 方法**: POST
- **功能**: 删除发运项目
- **返回值**: void

#### `submit(Integer id, DispatchVO dispatch, Model model)`
- **URL**: `/dispatchProject/submit`
- **HTTP 方法**: POST
- **功能**: 提交发运项目
- **返回值**: void

#### `generateDispatchSeq(String facilitatorCode, Model model)`
- **URL**: `/dispatchProject/generateDispatchSeq`
- **HTTP 方法**: GET
- **功能**: 生成派单编号
- **参数**: `facilitatorCode` - 服务商编码
- **返回值**: void

---

## 6. DispatchSettlementController — 发运结算管理

- **包路径**: `com.dp.plat.pms.springmvc.controller`
- **URL 命名空间**: `/dispatchSettlement`
- **依赖服务**: `IDispatchSettlementService`

### 方法列表

#### `home(Model model)`
- **URL**: `/dispatchSettlement/`
- **HTTP 方法**: GET
- **功能**: 发运结算管理首页
- **返回值**: String - 视图名称

#### `list(PageParam pageParam, SettlementVO v, Model model)`
- **URL**: `/dispatchSettlement/list`
- **HTTP 方法**: GET
- **功能**: 发运结算列表查询
- **返回值**: String - 视图名称

#### `findOne(Integer id, Model model)`
- **URL**: `/dispatchSettlement/{id}`
- **HTTP 方法**: GET
- **功能**: 发运结算详情查询
- **返回值**: String - 视图名称

#### `save(DispatchSettlement settlement, Model model)`
- **URL**: `/dispatchSettlement/save`
- **HTTP 方法**: POST
- **功能**: 保存发运结算
- **返回值**: void

#### `delete(Integer id, Model model)`
- **URL**: `/dispatchSettlement/delete/{id}`
- **HTTP 方法**: POST
- **功能**: 删除发运结算
- **返回值**: void

#### `confirm(Integer id, Model model)`
- **URL**: `/dispatchSettlement/confirm`
- **HTTP 方法**: POST
- **功能**: 确认发运结算
- **返回值**: void

#### `payment(Integer id, Model model)`
- **URL**: `/dispatchSettlement/payment`
- **HTTP 方法**: POST
- **功能**: 付款确认
- **返回值**: void

---

## 7. ProjectManageUserController — 项目管理用户

- **包路径**: `com.dp.plat.pms.springmvc.controller`
- **URL 命名空间**: `/projectManageUser`
- **依赖服务**: `IProjectManageUserService`

### 方法列表

#### `home(Model model)`
- **URL**: `/projectManageUser/`
- **HTTP 方法**: GET
- **功能**: 项目管理用户首页
- **返回值**: String - 视图名称

#### `list(PageParam pageParam, Object v, Model model)`
- **URL**: `/projectManageUser/list`
- **HTTP 方法**: GET
- **功能**: 项目管理用户列表查询
- **返回值**: String - 视图名称

#### `save(ProjectManageUser user, Model model)`
- **URL**: `/projectManageUser/save`
- **HTTP 方法**: POST
- **功能**: 保存项目管理用户
- **返回值**: void

#### `delete(Integer id, Model model)`
- **URL**: `/projectManageUser/delete/{id}`
- **HTTP 方法**: POST
- **功能**: 删除项目管理用户
- **返回值**: void

---

## 8. ProjectMemberController — 项目成员管理

- **包路径**: `com.dp.plat.pms.springmvc.controller`
- **URL 命名空间**: `/projectMember`
- **依赖服务**: `IProjectMemberService`

### 方法列表

#### `home(Model model)`
- **URL**: `/projectMember/`
- **HTTP 方法**: GET
- **功能**: 项目成员管理首页
- **返回值**: String - 视图名称

#### `list(PageParam pageParam, ProjectMember v, Model model)`
- **URL**: `/projectMember/list`
- **HTTP 方法**: GET
- **功能**: 项目成员列表查询
- **返回值**: String - 视图名称

#### `save(ProjectMember member, Model model)`
- **URL**: `/projectMember/save`
- **HTTP 方法**: POST
- **功能**: 保存项目成员
- **返回值**: void

---

## 9. ProjectTaskController — 项目任务管理

- **包路径**: `com.dp.plat.pms.springmvc.controller`
- **URL 命名空间**: `/projectTask`
- **依赖服务**: `IProjectTaskService`

### 方法列表

#### `home(Model model)`
- **URL**: `/projectTask/`
- **HTTP 方法**: GET
- **功能**: 项目任务管理首页
- **返回值**: String - 视图名称

#### `list(PageParam pageParam, ProjectTask v, Model model)`
- **URL**: `/projectTask/list`
- **HTTP 方法**: GET
- **功能**: 项目任务列表查询
- **返回值**: String - 视图名称

#### `findOne(Integer id, Model model)`
- **URL**: `/projectTask/{id}`
- **HTTP 方法**: GET
- **功能**: 项目任务详情查询
- **返回值**: String - 视图名称

#### `save(ProjectTask task, Model model)`
- **URL**: `/projectTask/save`
- **HTTP 方法**: POST
- **功能**: 保存项目任务
- **返回值**: void

#### `delete(Integer id, Model model)`
- **URL**: `/projectTask/delete/{id}`
- **HTTP 方法**: POST
- **功能**: 删除项目任务
- **返回值**: void

#### `updateProgress(Integer id, Integer progress, Model model)`
- **URL**: `/projectTask/updateProgress`
- **HTTP 方法**: POST
- **功能**: 更新任务进度
- **返回值**: void

#### `updateStatus(Integer id, String status, Model model)`
- **URL**: `/projectTask/updateStatus`
- **HTTP 方法**: POST
- **功能**: 更新任务状态
- **返回值**: void

---

## 10. ProjectAssetController — 项目资产管理

- **包路径**: `com.dp.plat.pms.springmvc.controller`
- **URL 命名空间**: `/projectAsset`
- **依赖服务**: `IIndustryAssetService`, `IIndustryAssetProjectRelationService`

### 方法列表

#### `home(Model model)`
- **URL**: `/projectAsset/`
- **HTTP 方法**: GET
- **功能**: 项目资产管理首页
- **返回值**: String - 视图名称

#### `list(PageParam pageParam, IndustryAssetVO v, Model model)`
- **URL**: `/projectAsset/list`
- **HTTP 方法**: GET
- **功能**: 项目资产列表查询
- **返回值**: String - 视图名称

#### `findOne(Integer id, Model model)`
- **URL**: `/projectAsset/{id}`
- **HTTP 方法**: GET
- **功能**: 项目资产详情查询
- **返回值**: String - 视图名称

#### `save(IndustryAsset asset, Model model)`
- **URL**: `/projectAsset/save`
- **HTTP 方法**: POST
- **功能**: 保存项目资产
- **返回值**: void

#### `delete(Integer id, Model model)`
- **URL**: `/projectAsset/delete/{id}`
- **HTTP 方法**: POST
- **功能**: 删除项目资产
- **返回值**: void

#### `bindProject(Integer assetId, Integer projectId, Model model)`
- **URL**: `/projectAsset/bindProject`
- **HTTP 方法**: POST
- **功能**: 绑定项目
- **返回值**: void

#### `unbindProject(Integer assetId, Integer projectId, Model model)`
- **URL**: `/projectAsset/unbindProject`
- **HTTP 方法**: POST
- **功能**: 解绑项目
- **返回值**: void

---

## 11. ProjectAssetLeakController — 项目资产泄露管理

- **包路径**: `com.dp.plat.pms.springmvc.controller`
- **URL 命名空间**: `/projectAssetLeak`
- **依赖服务**: `IIndustryLeakService`, `IIndustryAssetLeakRelationService`

### 方法列表

#### `home(Model model)`
- **URL**: `/projectAssetLeak/`
- **HTTP 方法**: GET
- **功能**: 项目资产泄露管理首页
- **返回值**: String - 视图名称

#### `list(PageParam pageParam, IndustryLeakVO v, Model model)`
- **URL**: `/projectAssetLeak/list`
- **HTTP 方法**: GET
- **功能**: 项目资产泄露列表查询
- **返回值**: String - 视图名称

#### `findOne(Integer id, Model model)`
- **URL**: `/projectAssetLeak/{id}`
- **HTTP 方法**: GET
- **功能**: 项目资产泄露详情查询
- **返回值**: String - 视图名称

#### `save(IndustryLeak leak, Model model)`
- **URL**: `/projectAssetLeak/save`
- **HTTP 方法**: POST
- **功能**: 保存项目资产泄露
- **返回值**: void

#### `delete(Integer id, Model model)`
- **URL**: `/projectAssetLeak/delete/{id}`
- **HTTP 方法**: POST
- **功能**: 删除项目资产泄露
- **返回值**: void

#### `bindAsset(Integer leakId, Integer assetId, Model model)`
- **URL**: `/projectAssetLeak/bindAsset`
- **HTTP 方法**: POST
- **功能**: 绑定资产
- **返回值**: void

#### `unbindAsset(Integer leakId, Integer assetId, Model model)`
- **URL**: `/projectAssetLeak/unbindAsset`
- **HTTP 方法**: POST
- **功能**: 解绑资产
- **返回值**: void

---

## 12. IndustryAssetController — 行业资产管理

- **包路径**: `com.dp.plat.pms.springmvc.controller`
- **URL 命名空间**: `/industryAsset`
- **依赖服务**: `IIndustryAssetService`

### 方法列表

#### `home(Model model)`
- **URL**: `/industryAsset/`
- **HTTP 方法**: GET
- **功能**: 行业资产管理首页
- **返回值**: String - 视图名称

#### `list(PageParam pageParam, IndustryAsset v, Model model)`
- **URL**: `/industryAsset/list`
- **HTTP 方法**: GET
- **功能**: 行业资产列表查询
- **返回值**: String - 视图名称

#### `save(IndustryAsset asset, Model model)`
- **URL**: `/industryAsset/save`
- **HTTP 方法**: POST
- **功能**: 保存行业资产
- **返回值**: void

#### `delete(Integer id, Model model)`
- **URL**: `/industryAsset/delete/{id}`
- **HTTP 方法**: POST
- **功能**: 删除行业资产
- **返回值**: void

---

## 13. IndustryLeakController — 行业泄露管理

- **包路径**: `com.dp.plat.pms.springmvc.controller`
- **URL 命名空间**: `/industryLeak`
- **依赖服务**: `IIndustryLeakService`

### 方法列表

#### `home(Model model)`
- **URL**: `/industryLeak/`
- **HTTP 方法**: GET
- **功能**: 行业泄露管理首页
- **返回值**: String - 视图名称

#### `list(PageParam pageParam, IndustryLeak v, Model model)`
- **URL**: `/industryLeak/list`
- **HTTP 方法**: GET
- **功能**: 行业泄露列表查询
- **返回值**: String - 视图名称

#### `save(IndustryLeak leak, Model model)`
- **URL**: `/industryLeak/save`
- **HTTP 方法**: POST
- **功能**: 保存行业泄露
- **返回值**: void

#### `delete(Integer id, Model model)`
- **URL**: `/industryLeak/delete/{id}`
- **HTTP 方法**: POST
- **功能**: 删除行业泄露
- **返回值**: void

---

## 14. IndustryLeakWarningController — 行业泄露预警

- **包路径**: `com.dp.plat.pms.springmvc.controller`
- **URL 命名空间**: `/industryLeakWarning`
- **依赖服务**: `IIndustryLeakWarningService`

### 方法列表

#### `home(Model model)`
- **URL**: `/industryLeakWarning/`
- **HTTP 方法**: GET
- **功能**: 行业泄露预警首页
- **返回值**: String - 视图名称

#### `list(PageParam pageParam, IndustryLeakWarning v, Model model)`
- **URL**: `/industryLeakWarning/list`
- **HTTP 方法**: GET
- **功能**: 行业泄露预警列表查询
- **返回值**: String - 视图名称

#### `save(IndustryLeakWarning warning, Model model)`
- **URL**: `/industryLeakWarning/save`
- **HTTP 方法**: POST
- **功能**: 保存行业泄露预警
- **返回值**: void

#### `delete(Integer id, Model model)`
- **URL**: `/industryLeakWarning/delete/{id}`
- **HTTP 方法**: POST
- **功能**: 删除行业泄露预警
- **返回值**: void

---

## 15. CommonRelatedDataController — 关联数据管理

- **包路径**: `com.dp.plat.pms.springmvc.controller`
- **URL 命名空间**: `/commonRelatedData`
- **依赖服务**: `ICommonRelatedDataService`

### 方法列表

#### `home(Model model)`
- **URL**: `/commonRelatedData/`
- **HTTP 方法**: GET
- **功能**: 关联数据管理首页
- **返回值**: String - 视图名称

#### `list(PageParam pageParam, CommonRelatedData v, Model model)`
- **URL**: `/commonRelatedData/list`
- **HTTP 方法**: GET
- **功能**: 关联数据列表查询
- **返回值**: String - 视图名称

#### `save(CommonRelatedData data, Model model)`
- **URL**: `/commonRelatedData/save`
- **HTTP 方法**: POST
- **功能**: 保存关联数据
- **返回值**: void

#### `delete(Integer id, Model model)`
- **URL**: `/commonRelatedData/delete/{id}`
- **HTTP 方法**: POST
- **功能**: 删除关联数据
- **返回值**: void

#### `bind(Integer dataId, String dataType, Integer relatedId, String relatedType, Model model)`
- **URL**: `/commonRelatedData/bind`
- **HTTP 方法**: POST
- **功能**: 绑定关联数据
- **返回值**: void

#### `unbind(Integer id, Model model)`
- **URL**: `/commonRelatedData/unbind/{id}`
- **HTTP 方法**: POST
- **功能**: 解绑关联数据
- **返回值**: void

---

## 16. FacilitatorController — 协调员管理

- **包路径**: `com.dp.plat.pms.springmvc.controller`
- **URL 命名空间**: `/facilitator`
- **依赖服务**: `IFacilitatorService`

### 方法列表

#### `list(Model model)`
- **URL**: `/facilitator/list`
- **HTTP 方法**: GET
- **功能**: 协调员列表查询
- **返回值**: String - 视图名称

#### `save(Facilitator facilitator, Model model)`
- **URL**: `/facilitator/save`
- **HTTP 方法**: POST
- **功能**: 保存协调员
- **返回值**: void

---

## 17. WorkBenchController — 工作台

- **包路径**: `com.dp.plat.pms.springmvc.controller`
- **URL 命名空间**: `/workbench`
- **依赖服务**: `IPmWorkBenchService`

### 方法列表

#### `home(Model model)`
- **URL**: `/workbench/`
- **HTTP 方法**: GET
- **功能**: 工作台首页
- **返回值**: String - 视图名称

#### `todo(Model model)`
- **URL**: `/workbench/todo`
- **HTTP 方法**: GET
- **功能**: 待办任务列表
- **返回值**: String - 视图名称

#### `done(Model model)`
- **URL**: `/workbench/done`
- **HTTP 方法**: GET
- **功能**: 已办任务列表
- **返回值**: String - 视图名称

#### `statistics(Model model)`
- **URL**: `/workbench/statistics`
- **HTTP 方法**: GET
- **功能**: 工作台统计
- **返回值**: String - 视图名称

#### `recent(Model model)`
- **URL**: `/workbench/recent`
- **HTTP 方法**: GET
- **功能**: 最近操作
- **返回值**: String - 视图名称

---

## 18. StrutsApiController — Struts API 兼容

- **包路径**: `com.dp.plat.pms.springmvc.controller`
- **URL 命名空间**: `/strutsApi`
- **职责**: 为旧 Struts2 模块提供 API 兼容

### 方法列表

#### `getProjectInfo(Integer projectId, Model model)`
- **URL**: `/strutsApi/getProjectInfo`
- **HTTP 方法**: GET
- **功能**: 获取项目信息
- **参数**: `projectId` - 项目ID
- **返回值**: void (JSON)

#### `getProjectMember(Integer projectId, Model model)`
- **URL**: `/strutsApi/getProjectMember`
- **HTTP 方法**: GET
- **功能**: 获取项目成员
- **参数**: `projectId` - 项目ID
- **返回值**: void (JSON)

#### `getUserInfo(Model model)`
- **URL**: `/strutsApi/getUserInfo`
- **HTTP 方法**: GET
- **功能**: 获取用户信息
- **返回值**: void (JSON)

#### `checkPermission(String permission, Model model)`
- **URL**: `/strutsApi/checkPermission`
- **HTTP 方法**: GET
- **功能**: 检查权限
- **参数**: `permission` - 权限编码
- **返回值**: void (JSON)
