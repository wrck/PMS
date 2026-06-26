# PMS-springmvc 项目管理模块详细文档

> 本文档深度分析 PMS-springmvc 项目管理模块的所有 Controller、Service、Entity 类，
> 包含完整的类结构、方法签名、业务逻辑、状态码定义和异常处理机制。

---

## 1. 模块概述

项目管理模块是 PMS-springmvc 系统的核心业务模块，负责项目全生命周期管理，包括项目创建、项目查询、项目成员管理、项目任务管理、项目资产管理等。该模块基于 Spring MVC 框架，通过 @Autowired 注入 Service 层实现业务逻辑。

### 涉及的 Controller 类列表

| Controller 类 | 包路径 | URL 命名空间 | 职责 |
|---------------|--------|-------------|------|
| `ProjectController` | `com.dp.plat.pms.springmvc.controller` | `/pm/project` | 项目全生命周期管理 |
| `ProjectMemberController` | `com.dp.plat.pms.springmvc.controller` | `/pm/projectMember` | 项目成员管理 |
| `ProjectTaskController` | `com.dp.plat.pms.springmvc.controller` | `/pm/projectTask` | 项目任务管理 |
| `ProjectManageUserController` | `com.dp.plat.pms.springmvc.controller` | `/pm/projectManageUser` | 项目管理用户 |
| `ProjectAssetController` | `com.dp.plat.pms.springmvc.controller` | `/pm/projectAsset` | 项目资产管理 |
| `ProjectAssetLeakController` | `com.dp.plat.pms.springmvc.controller` | `/pm/projectAssetLeak` | 项目资产泄露管理 |

### 涉及的 Service 类列表

| Service 接口 | 依赖的 DAO | 职责 |
|-------------|-----------|------|
| `IProjectService` | `ProjectMapper` | 项目基础服务 |
| `IProjectHeaderService` | `ProjectHeaderMapper` | 项目头信息服务 |
| `IProjectMemberService` | `ProjectMemberMapper` | 项目成员服务 |
| `IProjectTaskService` | `ProjectTaskMapper` | 项目任务服务 |
| `IProjectManageUserService` | `ProjectManageUserMapper` | 项目管理用户服务 |
| `IIndustryAssetService` | `IndustryAssetMapper` | 行业资产服务 |
| `IIndustryAssetProjectRelationService` | `IndustryAssetProjectRelationMapper` | 资产项目关联服务 |
| `IIndustryLeakService` | `IndustryLeakMapper` | 行业泄露服务 |

### 涉及的数据库表列表

| 表名 | 说明 |
|------|------|
| `pm_project` | 项目主表 |
| `pm_project_header` | 项目头信息（视图） |
| `pm_project_member` | 项目成员表 |
| `pm_project_task` | 项目任务表 |
| `pm_project_manage_user` | 项目管理用户表 |
| `af_industry_asset` | 行业资产表 |
| `af_industry_asset_project_relation` | 资产项目关联表 |
| `af_industry_leak` | 行业泄露表 |
| `af_industry_leak_warning` | 行业泄露预警表 |
| `af_industry_asset_leak_relation` | 资产泄露关联表 |
| `pm_workflow` | 工作流数据表 |

### 依赖的其他模块

- 工作流模块（`IPmWorkFlowService`，流程启动/任务完成）
- 发运管理模块（`IDispatchProjectService`，发运项目关联）
- 发运结算模块（`IDispatchSettlementService`，结算关联）
- PMS-struts 旧模块（`ProjectService`，兼容旧逻辑）

---

## 2. 业务流程

### 2.1 项目查询流程

```
[用户访问项目列表] ──> ProjectController.home()
      |
[加载列表数据] ──> ProjectController.list()
      |
      ├── 权限检查：checkPermission("project:list")
      ├── 设置过滤条件：disabled=false
      ├── 角色判断：特定角色增加回款信息
      ├── 分页查询：projectService.selectBySelectivePageable()
      └── 返回视图：project/list
```

### 2.2 项目详情查询流程

```
[用户查看项目详情] ──> ProjectController.findOne()
      |
      ├── 权限检查：checkPermission("project:detail")
      ├── JSON 请求处理
      │   ├── 查询项目：projectService.selectByPrimaryKey()
      │   ├── 查询项目头信息
      │   ├── 查询项目成员
      │   └── 查询项目任务
      └── 返回视图：project/detail
```

### 2.3 项目任务审批流程

```
[用户发起审批] ──> WorkFlowController.startProcess()
      |
      ├── 权限检查
      ├── 流程类型判断
      │   ├── QualityApproveTrack：质量审批跟踪流程
      │   └── SubcontractInspection：转包检验流程
      ├── 查询关联实体
      └── 启动流程实例
```

---

## 3. Controller 方法详细说明

### 3.1 ProjectController

#### `home(Model model)`
- **URL**: `/pm/project/`
- **HTTP 方法**: GET
- **功能**: 项目管理首页
- **权限**: `project:list`
- **返回值**: 视图名称

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

## 4. Service 方法详细说明

### 4.1 IProjectService

| 方法签名 | 功能 | 事务类型 |
|----------|------|----------|
| `ProjectVO queryProjectByContractNoAndType(String contractNo, String projectType)` | 根据合同号和类型查询项目 | 无事务 |

### 4.2 IProjectHeaderService

| 方法签名 | 功能 | 事务类型 |
|----------|------|----------|
| `int deleteByPrimaryKey(Object pk)` | 删除项目头信息 | 事务 |
| `int insert(ProjectHeader t)` | 插入项目头信息 | 事务 |
| `int insertSelective(ProjectHeader t)` | 选择性插入 | 事务 |
| `ProjectHeader selectByPrimaryKey(Object pk)` | 根据ID查询 | 无事务 |
| `int updateByPrimaryKeySelective(ProjectHeader t)` | 选择性更新 | 事务 |
| `int updateByPrimaryKey(ProjectHeader t)` | 更新项目头信息 | 事务 |
| `long countBySelectivePageable(PageParam<?> pageParam)` | 分页计数 | 无事务 |
| `long countBySelective(ProjectHeader t)` | 条件计数 | 无事务 |
| `<T> List<T> selectBySelectivePageable(PageParam<?> pageParam)` | 分页查询 | 无事务 |
| `List<ProjectHeader> selectBySelective(ProjectHeader t)` | 条件查询 | 无事务 |
| `long countUncreateProjectList(PageParam<Object> tempParam)` | 未创建项目数 | 无事务 |
| `List<Object> selectUncreateProjectList(PageParam<Object> pageParam)` | 未创建项目列表 | 无事务 |
| `Map<String, Object> checkPermission(ProjectVO project)` | 检查权限 | 无事务 |
| `PermissionResult checkPermission(ProjectVO project, String... permissions)` | 检查权限（返回结果） | 无事务 |
| `ProjectVO selectVOByProjectId(Object projectId)` | 查询项目VO | 无事务 |
| `ProjectVO queryProjectStateByProjectId(Object projecrId)` | 查询项目状态 | 无事务 |
| `List<ProjectProduct> queryProductInfoFromSmsByProjectCode(ProjectProduct project)` | 查询SMS产品信息 | 无事务 |
| `Result insertMergeContract(ProjectVO project, Integer projectId)` | 插入合并合同 | 事务 |
| `Result transferProject(ProjectVO project, Integer projectId, String projectType)` | 转移项目 | 事务 |

### 4.3 IProjectMemberService

| 方法签名 | 功能 | 事务类型 |
|----------|------|----------|
| 继承 IAbstractBaseService<ProjectMember> | 基础 CRUD | 按方法前缀 |

### 4.4 IProjectTaskService

| 方法签名 | 功能 | 事务类型 |
|----------|------|----------|
| 继承 IAbstractBaseService<ProjectTask> | 基础 CRUD | 按方法前缀 |

---

## 5. 数据模型

### 5.1 Project 实体

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `id` | Integer | 主键ID |
| `projectCode` | String | 项目编码 |
| `projectName` | String | 项目名称 |
| `projectState` | String | 项目状态 |
| `projectType` | String | 项目类型 |
| `customerName` | String | 客户名称 |
| `createTime` | Date | 创建时间 |
| `updateTime` | Date | 更新时间 |
| `customInfo` | JSONObject | 自定义扩展信息 |

### 5.2 ProjectHeader 实体

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `projectId` | Integer | 项目ID |
| `projectType` | String | 项目类型 |
| `projectCode` | String | 项目编码 |
| `projectName` | String | 项目名称 |
| `projectState` | String | 项目状态 |
| `column001` | String | 办事处编码 |
| `column002` | String | 客户编码 |
| `column003` | String | 客户名称 |
| `column012` | String | 实施方式 |
| `customInfo` | JSONObject | 自定义扩展信息 |

### 5.3 ProjectTask 实体

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `taskId` | Integer | 任务ID |
| `projectId` | Integer | 项目ID |
| `taskName` | String | 任务名称 |
| `taskTypeCode` | String | 任务类型编码 |
| `status` | String | 任务状态 |
| `planStartTime` | Date | 计划开始时间 |
| `planEndTime` | Date | 计划结束时间 |
| `actualStartTime` | Date | 实际开始时间 |
| `progress` | Integer | 进度百分比 |

---

## 6. 常量定义

### 6.1 项目类型（ProjectType）

| 常量名 | 值 | 说明 |
|--------|-----|------|
| `AF_SALES_PROJECT` | `afss` | 安服订单项目 |
| `AF_XX_PROJECT` | `afxx` | 安服先行项目 |
| `JF_SALES_PROJECT` | `10` | 用服售后项目 |
| `JF_TEST_PROJECT` | `20` | 用服售前测试 |

### 6.2 项目成员角色（MemberRole）

| 常量名 | 值 | 说明 |
|--------|-----|------|
| `MEMBER_SALESMAN` | `10` | 销售人员 |
| `MEMBER_SM` | `20` | 服务经理 |
| `MEMBER_PM` | `30` | 项目经理 |
| `MEMBER_PARTY` | `40` | 团队成员 |
| `MEMBER_SERVICE_CHANNEL` | `50` | 服务渠道工程师 |
| `MEMBER_CUSTOMER` | `60` | 最终客户 |
| `MEMBER_TECH_MANMER` | `70` | 技术经理 |
| `MEMBER_QC` | `80` | 质量监督员 |

---

## 7. 异常处理

### 7.1 异常类型

| 异常类 | 触发条件 | 处理方式 |
|--------|----------|----------|
| `ActivitiObjectNotFoundException` | 任务不存在 | 返回错误信息 |
| `ActivitiException` | 流程异常 | 提取错误信息 |
| `BusinessException` | 业务规则违反 | 返回业务错误 |
| `Exception` | 系统内部错误 | 记录日志并返回 |

### 7.2 异常处理示例

```java
try {
    // 业务逻辑
} catch (ActivitiObjectNotFoundException e) {
    model.addAttribute("status", Boolean.FALSE);
    model.addAttribute("message", "此任务不存在，请联系管理员！");
    ExceptionHandler.insertException(e);
} catch (ActivitiException e) {
    String errorMsg = extractErrorMessage(e);
    model.addAttribute("status", Boolean.FALSE);
    model.addAttribute("message", errorMsg);
    ExceptionHandler.insertException(e);
} catch (Exception e) {
    model.addAttribute("status", Boolean.FALSE);
    model.addAttribute("message", "操作失败，请联系管理员！");
    ExceptionHandler.insertException(e);
}
```
