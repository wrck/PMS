# Controller 方法参考文档

> 本文档提供 PMS-springmvc 模块所有 Controller 类的方法全量参考，作为现有 [action-methods-reference.md](action-methods-reference.md) 的补充和扩展。
> 源码位置：`com.dp.plat.pms.springmvc.controller`、`com.dp.plat.ehr.controller`

---

## 1. Controller 类总览

> URL 命名空间以源码 `@RequestMapping` 注解实际值为准（已交叉验证）。

| Controller 类 | URL 命名空间 | 继承类 | 文档 |
|---------------|-------------|--------|------|
| `AbstractController` | - | `BaseController` | 通用 CRUD 基类（13 个通用方法） |
| `BaseController` | - | - | 基础控制器（无业务方法） |
| `ProjectController` | `/pm/project` | `AbstractController` | [项目管理](project-management.md) |
| `ProjectMemberController` | `/pm/member` | `AbstractController` | [项目成员](project-member.md) |
| `ProjectTaskController` | `/pm/project/task` | `AbstractController` | [项目任务](project-task.md) |
| `ProjectManageUserController` | `/pm/user` | `AbstractController` | 项目管理用户 |
| `ProjectAssetController` | `/pm/project/asset` | `AbstractController` | 项目资产管理 |
| `ProjectAssetLeakController` | `/pm/asset/leak` | `AbstractController` | 项目资产漏洞管理 |
| `WorkFlowController` | `/workflow` | `AbstractController` | [工作流管理](workflow.md) |
| `WorkBenchController` | `/workflow/workbench` | - (无继承) | [工作台](workbench.md) |
| `DailyReportController` | `/pm/daily/report` | `AbstractController` | [日报管理](daily-report.md) |
| `DispatchProjectController` | `/pm/dispatch` | `AbstractController` | [转包项目](dispatch-project.md) |
| `DispatchSettlementController` | `/pm/settlement` | `AbstractController` | [转包结算](dispatch-settlement.md) |
| `IndustryAssetController` | `/af/industry/asset` | `AbstractController` | [行业资产](industry-asset.md) |
| `IndustryLeakController` | `/af/industry/leak` | `AbstractController` | [行业漏洞](industry-leak.md) |
| `IndustryLeakWarningController` | `/af/industry/warning` | `AbstractController` | [行业漏洞预警](industry-leak.md) |
| `CommonRelatedDataController` | `/pm/common/related` | `AbstractController` | 关联数据管理 |
| `FacilitatorController` | `/pm/facilitator` | `AbstractController` | [服务商管理](facilitator.md) |
| `StrutsApiController` | `/api` | - (无继承) | Struts API 兼容 |
| `EHRDataController` | `/ehr/` | - (无继承) | [EHR 集成](ehr-integration.md) |

> 共 19 个业务 Controller（不含 AbstractController 与 BaseController）+ 1 个 EHR Controller = 20 个 Controller。
> 已修正的 URL 错误：ProjectMember `/pm/member`、ProjectTask `/pm/project/task`、ProjectManageUser `/pm/user`、ProjectAsset `/pm/project/asset`、ProjectAssetLeak `/pm/asset/leak`、WorkBench `/workflow/workbench`、IndustryLeakWarning `/af/industry/warning`、CommonRelatedData `/pm/common/related`、StrutsApi `/api`。

---

## 2. AbstractController 通用方法

`AbstractController<Service, T, V>` 提供通用 CRUD 方法，所有继承的 Controller 自动获得这些方法。

### 2.1 方法列表

| 方法 | URL 模式 | HTTP 方法 | 功能 | 权限 |
|------|---------|----------|------|------|
| `home` | `/` | GET | 模块首页 | `{dataName}:list` |
| `list` | `/list` | GET | 列表查询 | `{dataName}:list` |
| `findOne` | `/{id}`、`/modals/{id}` | GET | 详情查询 | `{dataName}:detail` |
| `detail` | `/detail`、`/modals/detail` | GET | 详情页面 | `{dataName}:detail` |
| `create` | `/detail` | POST | 新增 | `{dataName}:add` |
| `update` | `/{id}` | PUT | 更新 | `{dataName}:edit` |
| `delete` | `/{id}` | DELETE | 删除 | `{dataName}:delete` |
| `toImport` | `/modals/import` | GET | 导入页面 | `{dataName}:import` |
| `importPreview` | `/import/preview` | POST | 导入预览 | `{dataName}:import` |
| `previewTempTable` | `/previewTempTable` | GET | 预览临时表 | `{dataName}:import` |
| `dropTempTable` | `/dropTempTable` | GET | 删除临时表 | - |
| `importSubmit` | `/import/submit` | POST | 导入提交 | `{dataName}:import` |
| `submitTempTable` | `/import/submitTempTable` | POST | 临时表提交 | `{dataName}:import` |

### 2.2 核心方法详解

#### `home(Model model)`

- **功能**：模块首页
- **返回值**：视图名称 `{viewNamespace}list`
- **业务逻辑**：
  1. 权限检查（`{dataName}:list`）
  2. 设置 URL 命名空间、视图模型、关键字
  3. 返回列表视图

#### `list(PageParam<Object> pageParam, V v, Model model)`

- **功能**：分页列表查询
- **参数**：
  - `pageParam`：分页参数
  - `v`：查询条件 VO
  - `model`：模型
- **业务逻辑**：
  1. 权限检查
  2. 构建临时查询参数（计算总数）
  3. 构建过滤查询参数（计算过滤后数量）
  4. 分页查询数据
  5. 查询列定义（`findColumnList`）
  6. 返回列表视图

#### `findOne(@PathVariable("id") Integer id, Model model)`

- **功能**：根据 ID 查询详情
- **业务逻辑**：
  1. 权限检查
  2. JSON 请求：查询实体、表单字段、按钮、导航标签
  3. 非 JSON 请求：设置视图参数
  4. 返回详情视图

#### `create(V v, Model model)`

- **功能**：新增记录
- **业务逻辑**：
  1. 权限检查（`{dataName}:add`）
  2. 调用 `service.insertSelective((T) v)`
  3. 异常处理：记录异常 ID
  4. 返回详情视图

#### `update(@PathVariable("id") Integer id, V v, Model model)`

- **功能**：更新记录
- **业务逻辑**：
  1. 权限检查（`{dataName}:edit`）
  2. 调用 `service.updateByPrimaryKeySelective((T) v)`
  3. 异常处理
  4. 返回详情视图

#### `delete(@PathVariable("id") Integer id, Model model)`

- **功能**：删除记录
- **业务逻辑**：
  1. 权限检查（`{dataName}:delete`）
  2. 调用 `service.deleteByPrimaryKey(id)`
  3. 异常处理

### 2.3 权限检查方法

```java
public boolean checkPermission(V v, Model model, String... permissions) {
    // 1. 检查权限编码
    if (!UserContext.checkPermission(permissions)) {
        model.addAttribute("status", false);
        model.addAttribute("message", "没有权限进行该操作！");
        return false;
    }
    // 2. 收集相关权限
    Collection<String> permissionList = UserContext.getCurrentPrincipal().getPermissions();
    Collection<String> currentPermistions = new ArrayList<>(permissionList.size());
    for (String requiredPerm : permissions) {
        String type = requiredPerm.split(":")[0] + ":";
        for (String permission : permissionList) {
            if (permission.startsWith(type)) {
                currentPermistions.add(permission);
            }
        }
    }
    model.addAttribute("permissions", currentPermistions);
    model.addAttribute("permissionType", "all");
    return true;
}
```

---

## 3. ProjectController 方法

### 3.1 方法列表

| 方法 | URL | HTTP 方法 | 功能 | 权限 |
|------|-----|----------|------|------|
| `home` | `/pm/project/` | GET | 项目管理首页 | `project:list` |
| `list` | `/pm/project/list` | GET | 项目列表查询 | `project:list` |
| `findOne` | `/pm/project/{id}` | GET | 项目详情查询（注：源码无 `/modals/{id}` 变体） | `project:detail` |
| `detail` | `/pm/project/detail` | GET | 项目详情页面（注：源码无 `/modals/detail` 变体） | `project:detail` |
| `create` | `/pm/project/detail` | POST | 新增项目 | `project:add` |
| `update` | `/pm/project/{id}` | PUT | 更新项目 | `project:edit` |
| `delete` | `/pm/project/{id}` | DELETE | 删除项目 | `project:delete` |
| `toMerge` | `/pm/project/{id}/transform/{type}` | GET | 项目类型转换弹窗（`type` 取值：`afToJf`/`jfToAf`/`afToYf`/`jfToYf`，原误标为 `modals/merge`） | `project:edit` |
| `merge` | `/pm/project/{id}/transform/{type}` | POST | 项目类型转换提交（`type` 同上，原误标为 `merge`） | `project:edit` |
| `orderDetailByProjectId` | `/pm/project/{ids}/orderDetail` | GET | 按项目ID查询订单明细（支持多 ID 逗号分隔） | `project:detail` |
| `orderDetailByContractNo` | `/pm/project/orderDetail` | GET | 按合同号查询订单明细 | `project:detail` |
| `productInfoByProjectCode` | `/pm/project/productInfo`、`/pm/project/{id}/productInfo` | GET | 按项目编码/项目ID查询产品信息 | `project:detail` |
| `projectTask` | `/pm/project/{projectId}/task` | GET | 项目任务列表 | `project:detail` |
| `projectState` | `/pm/project/{projectId}/state` | GET | 项目状态查询 | `project:detail` |
| `syncSMSData` | `/pm/project/syncSMSData` | GET/POST | 同步 SMS 数据 | `project:edit` |
| `checkPermission` | - | - | 权限检查（重写父类） | - |
| `checkProjectTypeAndAreaPower` | - | - | 校验项目类型与区域权限（内部调用） | - |

> 已删除虚构方法：`save`、`delete/{id}`、`transferProject`、`mergeContract`、`queryProjectState`、`queryProductInfo`、`exportProject`、`importProject`、`queryUncreateProjectList`。

> 补充说明（2026-06-29）：本次根据源码 `ProjectController.java:914` 补充 `checkProjectTypeAndAreaPower` 方法。该方法为内部权限校验辅助方法，校验项目类型与区域权限，无独立 URL 映射。

> 完整方法说明详见 [project-management.md](project-management.md)

---

## 4. WorkFlowController 方法

### 4.1 方法列表

| 方法 | URL | HTTP 方法 | 功能 | 权限 |
|------|-----|----------|------|------|
| `home` | `/workflow/` | GET | 工作流管理首页 | `workflow:list` |
| `list` | `/workflow/list` | GET | 工作流列表查询 | `workflow:list` |
| `info` | `/workflow/info/list` | GET | 工作流信息列表 | `workflow:list` |
| `findOne` | `/workflow/{id}`、`/workflow/modals/{id}` | GET | 工作流详情查询 | `workflow:detail` |
| `findOneByTaskId` | `/workflow/task/{taskId}`、`/workflow/task/modals/{taskId}` | GET | 按任务ID查询工作流 | `workflow:detail` |
| `checkTask` | `/workflow/task/{taskId}/check` | GET | 检查任务权限 | `workflow:detail` |
| `complete` | `/workflow/complete/{taskId}` | POST | 完成任务 | `workflow:edit` |
| `batchComplete` | `/workflow/complete/batch` | POST | 批量完成任务（重载1：基于 instanceIds/userId） | `workflow:edit` |
| `batchComplete` | `/workflow/{id}/revokeProcess` | POST | 批量完成任务（重载2：实为撤销流程 revokeProcess，原误标为 `batchComplete2`） | `workflow:edit` |
| `batchEvaluate` | `/workflow/evaluate/batch` | POST | 批量评价 | `workflow:edit` |
| `closeProcess` | `/workflow/test/closeProcess` | POST | 关闭流程（注：源码 URL 前缀含 `/test/`，原误标为 `/workflow/closeProcess`） | `workflow:edit` |
| `withdrawTask` | `/workflow/withdraw/{instanceId}/{userId}` | POST | 撤回任务 | `workflow:edit` |
| `startProcess` | `/workflow/startProcess` | POST | 启动流程实例 | `workflow:edit` |
| `complete` (deprecated) | `/workflow/{processKey}/complete/{taskId}` | POST | 完成流程（已废弃，源码用 `processKey` 路径变量） | `workflow:edit` |
| `decoratorEntity` | - | - | 装饰实体对象（内部调用） | - |
| `checkPermission` | - | - | 权限检查 | - |

> 已删除虚构方法：`terminateProcess`、`deleteProcess`、`queryTodoTasks`、`queryFinishedTasks`、`queryProcessActivities`、`queryFormFields`、`exportProcess`。

> 完整方法说明详见 [workflow.md](workflow.md)

---

## 5. DispatchProjectController 方法

### 5.1 方法列表

| 方法 | URL | HTTP 方法 | 功能 | 权限 |
|------|-----|----------|------|------|
| `list` | `/pm/dispatch/list` | GET | 转包记录列表 | `dispatch:list` |
| `findOne` | `/pm/dispatch/{id}` | GET | 转包详情 | `dispatch:detail` |
| `detail` | `/pm/dispatch/detail` | GET | 打开转包详情页面 | `dispatch:detail` |
| `create` | `/pm/dispatch/detail` | POST | 新增转包记录 | `dispatch:add` |
| `update` | `/pm/dispatch/{id}` | PUT | 更新转包记录 | `dispatch:edit` |
| `delete` | `/pm/dispatch/{id}` | DELETE | 删除转包记录 | `dispatch:delete` |
| `dispatchSubmit` | `/pm/dispatch/submit` | POST | 派单提交 | `dispatch:submit` |
| `dispatchPayment` | `/pm/dispatch/modals/payment` | GET | 派单付款弹窗 | - |
| `exportProjectInfoDoc` | `/pm/dispatch/{id}/{exportType}/info` | POST | 导出外派单 | `dispatch:detail` |
| `generateDispatchSeq` | `/pm/dispatch/generateDispatchSeq` | GET | 生成派单编号 | - |
| `multiDimsInfo` | `/pm/dispatch/{id}/multiDimInfos` | GET | 多维度信息 | `dispatch:detail` |
| `listWithSettleInfo` | `/pm/dispatch/listWithSettleInfo` | GET | 带结算信息列表 | - |
| `checkPermission` | - | - | 权限检查 | - |

> 完整方法说明详见 [dispatch-project.md](dispatch-project.md)

---

## 6. DispatchSettlementController 方法

### 6.1 方法列表

| 方法 | URL | HTTP 方法 | 功能 | 权限 |
|------|-----|----------|------|------|
| `list` | `/pm/settlement/list` | GET | 结算列表 | `settlement:list` |
| `findOne` | `/pm/settlement/{id}` | GET | 结算详情 | `settlement:detail` |
| `detail` | `/pm/settlement/detail` | GET | 打开结算页面 | `settlement:detail` |
| `create` | `/pm/settlement/detail` | POST | 新增结算 | `settlement:add` |
| `update` | `/pm/settlement/{id}` | PUT | 更新结算 | `settlement:edit` |
| `delete` | `/pm/settlement/{id}` | DELETE | 删除结算 | `settlement:delete` |
| `settlementSubmit` | `/pm/settlement/submit` | POST | 确认结算 | `settlement:submit` |
| `exportProjectInfoDoc` | `/pm/settlement/{id}/projectInfoDoc` | POST | 生成项目信息单 | `settlement:detail` |
| `settlementInvoiceDetails` | `/pm/settlement/{id}/invoice` | GET | 发票明细 | `settlement:detail` |
| `verifySettlementInvoice` | `/pm/settlement/{id}/invoice/verify` | GET | 发票验证 | `settlement:detail` |
| `syncSettlementPayment` | `/pm/settlement/syncPayment` | GET | 同步付款 | - |
| `checkPermission` | - | - | 权限检查 | - |

> 完整方法说明详见 [dispatch-settlement.md](dispatch-settlement.md)

---

## 7. EHRDataController 方法

### 7.1 方法列表

| 方法 | URL | HTTP 方法 | 功能 |
|------|-----|----------|------|
| `listView` | `/ehr/` | GET | EHR 数据首页 |
| `findCompanies` | `/ehr/company/list` | GET | 公司列表 |
| `findCompany` | `/ehr/company/{id}` | GET | 公司详情 |
| `findCompaniesTree` | `/ehr/company/tree` | GET | 公司树形数据 |
| `findDepartments` | `/ehr/department/list` | GET | 部门列表 |
| `findDepartment` | `/ehr/department/{id}` | GET | 部门详情 |
| `findDepartmentTree` | `/ehr/department/tree` | GET | 部门树形数据 |
| `findJobs` | `/ehr/job/list` | GET | 岗位列表 |
| `findJob` | `/ehr/job/{id}` | GET | 岗位详情 |
| `findEmployees` | `/ehr/employee/list` | GET | 员工列表 |
| `findEmployee` | `/ehr/employee/{id}` | GET | 员工详情 |
| `listEmployeeSelect2Data` | `/ehr/employeeDataList` | GET | 员工 Select2 数据 |
| `initUser` | `/ehr/initUser` | GET | 初始化用户 |
| `syncData` | `/ehr/syncData` | GET | 手动同步 |

> **URL 命名空间校正**：`UrlPrefixConstant.EHR_DATA_URL = "/ehr/"`（非 `/ehr/data`，详见 `com.dp.plat.ehr.constants.UrlPrefixConstant:12`）。方法级 `@RequestMapping` 路径直接拼接到 `/ehr/` 之后。完整方法说明详见 [ehr-integration.md](ehr-integration.md)

---

## 8. ProjectManageUserController 方法

### 8.1 方法列表

> URL 命名空间：`/pm/user`（非 `/pm/projectManageUser`）

| 方法 | URL | HTTP 方法 | 功能 | 权限 |
|------|-----|----------|------|------|
| `home` | `/pm/user/` | GET | 用户管理首页 | `user:list` |
| `list` | `/pm/user/list` | GET | 用户列表 | `user:list` |
| `findOne` | `/pm/user/{id}` | GET | 用户详情 | `user:detail` |
| `detail` | `/pm/user/detail` | GET | 用户详情页面 | `user:detail` |
| `create` | `/pm/user/detail` | POST | 新增用户 | `user:add` |
| `update` | `/pm/user/{userId}` | PUT | 更新用户 | `user:edit` |
| `delete` | `/pm/user/{id}` | DELETE | 删除用户 | `user:delete` |
| `checkUnique` | `/pm/user/checkUnique` | GET | 校验用户名唯一 | - |
| `resetPassword` | `/pm/user/resetPassword` | POST | 重置密码 | `user:edit` |
| `findUserInfoWithParam` | `/pm/user/findUserInfoWithParam` | GET | 按条件查询用户信息 | `user:detail` |
| `initActitityUser` | `/pm/user/initActitityUser` | GET | 初始化 Activiti 用户 | `user:edit` |
| `checkPermission` | - | - | 权限检查 | - |

> 已删除虚构方法：`selectBySelective`、`selectBySelectivePageable`、`countBySelective`、`countBySelectivePageable`（这些是 Service 层方法，非 Controller 方法）。

---

## 9. CommonRelatedDataController 方法

### 9.1 方法列表

> URL 命名空间：`/pm/common/related`（非 `/commonRelatedData`）

| 方法 | URL | HTTP 方法 | 功能 | 权限 |
|------|-----|----------|------|------|
| `list` | `/pm/common/related/list` | GET | 关联数据列表 | `commonRelated:list` |
| `findOne` | `/pm/common/related/{id}`、`/pm/common/related/modals/{id}` | GET | 关联数据详情 | `commonRelated:detail` |
| `detail` | `/pm/common/related/detail`、`/pm/common/related/modals/detail` | GET | 关联数据详情页面 | `commonRelated:detail` |
| `create` | `/pm/common/related/detail` | POST | 新增关联数据 | `commonRelated:add` |
| `update` | `/pm/common/related/{id}` | PUT | 更新关联数据 | `commonRelated:edit` |
| `delete` | `/pm/common/related/{id}` | DELETE | 删除关联数据（逻辑删除：disabled=true） | `commonRelated:delete` |
| `initModelAttr` | - | - | 初始化模型属性（重写） | - |
| `checkPermission` | - | - | 权限检查（重写，含项目/任务级权限） | - |

> 注：通过 `useTemplate=true` 继承自 `AbstractController` 的 `home` 方法也可用。

---

## 10. StrutsApiController 方法

### 10.1 方法列表

> URL 命名空间：`/api`（非 `/strutsApi`）
> 该控制器**不继承** `AbstractController`，无通用 CRUD 方法。

| 方法 | URL | HTTP 方法 | 功能 |
|------|-----|----------|------|
| `queryDepartment` | `/api/departmentList` | GET | 查询办事处信息（带系统参数） |
| `queryCompany` | `/api/companyList` | GET | 查询生效的公司列表 |
| `queryDataBasic` | `/api/basicDataByType` | GET | 按类型查询基础数据（支持 withSub 参数） |

### 10.2 功能说明

`StrutsApiController` 为 PMS-struts 老系统提供数据查询 API 兼容层，依赖 `DepartmentManageService` 和 `BasicDataService`（这些 Service 位于 `com.dp.plat.service` 包，由 PMS-struts 模块提供）。

> 已删除全部虚构方法：`home`、`callStrutsApi`、`queryData`、`syncData`、`getProjectInfo`、`getProjectMember`、`getUserInfo`、`checkPermission`。

---

## 11. ProjectAssetController 方法

### 11.1 方法列表

> URL 命名空间：`/pm/project/asset`（类级 `@RequestMapping(PROJECT_MANAGER + "/project/asset")`）

| 方法 | URL | HTTP 方法 | 功能 | 权限 |
|------|-----|----------|------|------|
| `home` | `/pm/project/asset/` | GET | 项目资产首页 | `projectAsset:list` |
| `list` | `/pm/project/asset/list` | GET | 项目资产列表 | `projectAsset:list` |
| `findOne` | `/pm/project/asset/{id}` | GET | 项目资产详情 | `projectAsset:detail` |
| `detail` | `/pm/project/asset/detail` | GET | 项目资产详情页面 | `projectAsset:detail` |
| `create` | `/pm/project/asset/detail` | POST | 新增项目资产 | `projectAsset:add` |
| `update` | `/pm/project/asset/{id}` | PUT | 更新项目资产 | `projectAsset:edit` |
| `delete` | `/pm/project/asset/{id}` | DELETE | 删除项目资产 | `projectAsset:delete` |

---

## 12. ProjectAssetLeakController 方法

### 12.1 方法列表

> URL 命名空间：`/pm/asset/leak`（类级 `@RequestMapping(PROJECT_MANAGER + "/asset/leak")`）

| 方法 | URL | HTTP 方法 | 功能 | 权限 |
|------|-----|----------|------|------|
| `home` | `/pm/asset/leak/` | GET | 项目资产漏洞首页 | `projectAssetLeak:list` |
| `list` | `/pm/asset/leak/list` | GET | 项目资产漏洞列表 | `projectAssetLeak:list` |
| `findOne` | `/pm/asset/leak/{id}` | GET | 项目资产漏洞详情 | `projectAssetLeak:detail` |
| `detail` | `/pm/asset/leak/detail` | GET | 项目资产漏洞详情页面 | `projectAssetLeak:detail` |
| `create` | `/pm/asset/leak/detail` | POST | 新增项目资产漏洞 | `projectAssetLeak:add` |
| `update` | `/pm/asset/leak/{id}` | PUT | 更新项目资产漏洞 | `projectAssetLeak:edit` |
| `delete` | `/pm/asset/leak/{id}` | DELETE | 删除项目资产漏洞 | `projectAssetLeak:delete` |

---

## 13. ProjectMemberController 方法

### 13.1 方法列表

> URL 命名空间：`/pm/member`（类级 `@RequestMapping(PROJECT_MANAGER + "member")`，注意常量末尾已带斜杠）
> 继承 `AbstractController<ProjectMemberService, ProjectMember, ProjectMemberVO>`，仅重写以下方法。

| 方法 | URL | HTTP 方法 | 功能 | 权限 |
|------|-----|----------|------|------|
| `update` | `/pm/member/{id}` | PUT | 更新项目成员（含权限校验） | `projectMember:edit` |
| `delete` | `/pm/member/{id}` | DELETE | 删除项目成员（软删除，置 `disabled=true`） | `projectMember:delete` |

> 通用方法（`home`/`list`/`findOne`/`detail`/`create`）继承自 `AbstractController`，详见 §2。

---

## 14. ProjectTaskController 方法

### 14.1 方法列表

> URL 命名空间：`/pm/project/task`（类级 `@RequestMapping(PROJECT_MANAGER + "/project/task")`，源码字面拼接产生双斜杠 `/pm//project/task`，Spring 默认归一化）
> 继承 `AbstractController<ProjectTaskService, ProjectTask, TaskVO>`。

| 方法 | URL | HTTP 方法 | 功能 | 权限 | 源码行号 |
|------|-----|----------|------|------|---------|
| `create` | `/pm//project/task/detail` | POST | 新增项目任务（交付件） | `projectTask:add` | 70 |
| `update` | `/pm//project/task/{id}` | PUT | 更新项目任务 | `projectTask:edit` | 95 |
| `delete` | `/pm//project/task/{id}` | DELETE | 删除项目任务 | `projectTask:delete` | 148 |
| `toUpload` | `/pm//project/task/modals/upload` | GET | 上传弹窗页面 | `projectTask:detail` | 181 |
| `uploadDeliverFile`（重载1） | `/pm//project/task/upload` | POST | 上传交付件文件 | `projectTask:edit` | 209 |
| `uploadDeliverFile`（重载2） | `/pm//project/task/upload/{deliverId}` | DELETE | 删除已上传交付件 | `projectTask:delete` | 252 |
| `uploadList` | `/pm//project/task/upload/list` | GET | 已上传交付件列表 | `projectTask:detail` | 264 |
| `download` | `/pm//project/task/download` | GET/POST | 下载交付件 | `projectTask:detail` | 279 |

> 通用方法（`home`/`list`/`findOne`/`detail`）继承自 `AbstractController`。

---

## 15. DailyReportController 方法

### 15.1 方法列表

> URL 命名空间：`/pm/daily/report`（类级 `@RequestMapping(PROJECT_MANAGER + "/daily/report")`，源码字面拼接产生双斜杠）
> 继承 `AbstractController<DailyReportService, DailyReport, DailyReportVO>`。

| 方法 | URL | HTTP 方法 | 功能 | 源码行号 |
|------|-----|----------|------|---------|
| `home` | `/pm//daily/report/` | GET | 日报首页 | - |
| `list` | `/pm//daily/report/list` | GET | 日报列表 | - |
| `findOne` | `/pm//daily/report/{id}` | GET | 日报详情 | - |
| `detail` | `/pm//daily/report/detail` | GET | 日报详情页 | - |
| `create` | `/pm//daily/report/detail` | POST | 新增日报 | - |
| `update` | `/pm//daily/report/{id}` | PUT | 更新日报 | - |
| `delete` | `/pm//daily/report/{id}` | DELETE | 删除日报 | - |
| `exportDailyReportDoc` | `/pm//daily/report/export/{exportType}/report` | POST | 导出日报文档（`exportType`：doc/pdf/html） | 361 |
| `mailSelectList` | `/pm//daily/report/mail/{mailType}/select`、`/modals/mail/{mailType}/select` | GET | 邮件收件人选择列表（`mailType`：to/cc/bcc） | 541 |
| `mailDailyReport` | `/pm//daily/report/mail/{mailType}/report` | POST | 发送日报邮件 | 574 |

---

## 16. IndustryAssetController 方法

### 16.1 方法列表

> URL 命名空间：`/af/industry/asset`（类级 `@RequestMapping(AF_MANAGER + "/industry/asset")`，源码字面拼接产生双斜杠）
> 继承 `AbstractController<IndustryAssetService, IndustryAsset, IndustryAssetVO>`。

| 方法 | URL | HTTP 方法 | 功能 | 权限 |
|------|-----|----------|------|------|
| `home` | `/af//industry/asset/` | GET | 行业资产首页 | `industryAsset:list` |
| `list` | `/af//industry/asset/list` | GET | 行业资产列表 | `industryAsset:list` |
| `update` | `/af//industry/asset/{id}` | PUT | 更新行业资产 | `industryAsset:edit` |
| `delete` | `/af//industry/asset/{id}` | DELETE | 删除行业资产 | `industryAsset:delete` |

> 通用方法（`findOne`/`detail`/`create`）继承自 `AbstractController`。

---

## 17. IndustryLeakController 方法

### 17.1 方法列表

> URL 命名空间：`/af/industry/leak`（类级 `@RequestMapping(AF_MANAGER + "/industry/leak")`，源码字面拼接产生双斜杠）
> 继承 `AbstractController<IndustryLeakService, IndustryLeak, IndustryLeakVO>`。

| 方法 | URL | HTTP 方法 | 功能 | 权限 |
|------|-----|----------|------|------|
| `home` | `/af//industry/leak/` | GET | 行业漏洞首页 | `industryLeak:list` |
| `list` | `/af//industry/leak/list` | GET | 行业漏洞列表 | `industryLeak:list` |
| `update` | `/af//industry/leak/{id}` | PUT | 更新行业漏洞 | `industryLeak:edit` |
| `delete` | `/af//industry/leak/{id}` | DELETE | 删除行业漏洞 | `industryLeak:delete` |

> 通用方法（`findOne`/`detail`/`create`）继承自 `AbstractController`。

---

## 18. IndustryLeakWarningController 方法

### 18.1 方法列表

> URL 命名空间：`/af/industry/warning`（类级 `@RequestMapping(AF_MANAGER + "/industry/warning")`，源码字面拼接产生双斜杠）
> 继承 `AbstractController<IndustryLeakWarningService, IndustryLeakWarning, IndustryLeakWarningVO>`。

| 方法 | URL | HTTP 方法 | 功能 | 源码行号 |
|------|-----|----------|------|---------|
| `home` | `/af//industry/warning/` | GET | 行业漏洞预警首页 | - |
| `list` | `/af//industry/warning/list` | GET | 行业漏洞预警列表 | - |
| `update` | `/af//industry/warning/{id}` | PUT | 更新预警 | - |
| `delete` | `/af//industry/warning/{id}` | DELETE | 删除预警 | - |
| `warningAsset` | `/af//industry/warning/asset`、`/asset/list` | GET | 预警关联资产查询（含资产清单） | 113 |

> 通用方法（`findOne`/`detail`/`create`）继承自 `AbstractController`。`warningAsset` 方法为该 Controller 特有的预警资产查询入口。

---

## 19. FacilitatorController 方法

### 19.1 方法列表

> URL 命名空间：`/pm/facilitator`（类级 `@RequestMapping(PROJECT_MANAGER + "facilitator")`）
> 继承 `AbstractController<FacilitatorService, Facilitator, FacilitatorVO>`。

| 方法 | URL | HTTP 方法 | 功能 | 权限 |
|------|-----|----------|------|------|
| `delete` | `/pm/facilitator/{id}` | DELETE | 删除服务商 | `facilitator:delete` |

> 通用方法（`home`/`list`/`findOne`/`detail`/`create`/`update`）继承自 `AbstractController`，仅重写 `delete` 方法。

---

## 20. WorkBenchController 方法

### 20.1 方法列表

> URL 命名空间：`/workflow/workbench`（类级 `@RequestMapping("/workflow/workbench")`）
> ⚠️ 该控制器**不继承 `AbstractController`**，所有方法均在该类中独立声明。

| 方法 | URL | HTTP 方法 | 功能 | 源码行号 |
|------|-----|----------|------|---------|
| `listView` | `/workflow/workbench` | GET/POST | 工作台首页视图 | 35 |
| `listToDoTask` | `/workflow/workbench/toDoList` | GET/POST | 待办任务列表 | 44 |
| `listOthersTask` | `/workflow/workbench/listOthersTask` | GET/POST | 他人任务列表 | 77 |
| `finishedTask` | `/workflow/workbench/finishedTaskList` | GET/POST | 已办任务列表 | 114 |

> 📝 **历史修订记录**：2026-06-30 修订。新增 8 个 Controller 方法表（§13-§20），填补原版本仅出现在总览表但无方法级表格的缺口。方法 URL 与源码 `@RequestMapping` 实际值严格对齐。

---

## 附录：URL 命名空间常量

```java
public class ProjectConstant {
    public static class URLPath extends Consts.URLPath {
        // 项目管理模块
        public final static String PROJECT_MANAGER = "/pm/";
        // 安服管理
        public final static String AF_MANAGER = "/af/";
    }
}
```

> 注：`/workflow`、`/workbench` 等 URL 前缀在 WorkFlowController、WorkBenchController 中以字面量直接定义于类级 `@RequestMapping`，并非 ProjectConstant 常量。

---

## 相关文档

- [Action 方法参考](action-methods-reference.md)
- [Service 方法参考](service-methods-reference.md)
- [系统架构](../01-architecture/system-architecture.md)
