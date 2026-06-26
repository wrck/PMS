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
| `ProjectAssetLeakController` | `/pm/asset/leak` | `AbstractController` | 项目资产泄露管理 |
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
| `EHRDataController` | `/ehr/data` | - (无继承) | [EHR 集成](ehr-integration.md) |

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
| `findOne` | `/pm/project/{id}`、`/pm/project/modals/{id}` | GET | 项目详情查询 | `project:detail` |
| `detail` | `/pm/project/detail`、`/pm/project/modals/detail` | GET | 项目详情页面 | `project:detail` |
| `create` | `/pm/project/detail` | POST | 新增项目 | `project:add` |
| `update` | `/pm/project/{id}` | PUT | 更新项目 | `project:edit` |
| `delete` | `/pm/project/{id}` | DELETE | 删除项目 | `project:delete` |
| `toMerge` | `/pm/project/modals/merge` | GET | 合同合并弹窗 | `project:edit` |
| `merge` | `/pm/project/merge` | POST | 合同合并提交 | `project:edit` |
| `orderDetailByProjectId` | `/pm/project/{id}/orderDetailByProjectId` | GET | 按项目ID查询订单明细 | `project:detail` |
| `orderDetailByContractNo` | `/pm/project/orderDetailByContractNo` | GET | 按合同号查询订单明细 | `project:detail` |
| `productInfoByProjectCode` | `/pm/project/productInfoByProjectCode` | GET | 按项目编码查询产品信息 | `project:detail` |
| `projectTask` | `/pm/project/{id}/projectTask` | GET | 项目任务列表 | `project:detail` |
| `projectState` | `/pm/project/{id}/projectState` | GET | 项目状态查询 | `project:detail` |
| `syncSMSData` | `/pm/project/syncSMSData` | GET/POST | 同步 SMS 数据 | `project:edit` |
| `checkPermission` | - | - | 权限检查 | - |

> 已删除虚构方法：`save`、`delete/{id}`、`transferProject`、`mergeContract`、`queryProjectState`、`queryProductInfo`、`exportProject`、`importProject`、`queryUncreateProjectList`。

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
| `findOneByTaskId` | `/workflow/findByTaskId/{taskId}` | GET | 按任务ID查询工作流 | `workflow:detail` |
| `checkTask` | `/workflow/checkTask/{taskId}` | GET | 检查任务权限 | `workflow:detail` |
| `complete` | `/workflow/complete/{taskId}` | POST | 完成任务 | `workflow:edit` |
| `batchComplete` | `/workflow/batchComplete` | POST | 批量完成任务（重载1：基于 instanceIds/userId） | `workflow:edit` |
| `batchComplete` | `/workflow/batchComplete2` | POST | 批量完成任务（重载2：基于 taskIds） | `workflow:edit` |
| `batchEvaluate` | `/workflow/batchEvaluate` | POST | 批量评价 | `workflow:edit` |
| `closeProcess` | `/workflow/closeProcess` | POST | 关闭流程 | `workflow:edit` |
| `withdrawTask` | `/workflow/withdraw/{instanceId}/{userId}` | POST | 撤回任务 | `workflow:edit` |
| `startProcess` | `/workflow/startProcess` | POST | 启动流程实例 | `workflow:edit` |
| `complete` (deprecated) | `/workflow/completeProcess/{taskId}` | POST | 完成流程（已废弃） | `workflow:edit` |
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
| `listView` | `/ehr/data` | GET | EHR 数据首页 |
| `findCompanies` | `/ehr/data/company/list` | GET | 公司列表 |
| `findCompany` | `/ehr/data/company/{id}` | GET | 公司详情 |
| `findCompaniesTree` | `/ehr/data/company/tree` | GET | 公司树形数据 |
| `findDepartments` | `/ehr/data/department/list` | GET | 部门列表 |
| `findDepartment` | `/ehr/data/department/{id}` | GET | 部门详情 |
| `findDepartmentTree` | `/ehr/data/department/tree` | GET | 部门树形数据 |
| `findJobs` | `/ehr/data/job/list` | GET | 岗位列表 |
| `findJob` | `/ehr/data/job/{id}` | GET | 岗位详情 |
| `findEmployees` | `/ehr/data/employee/list` | GET | 员工列表 |
| `findEmployee` | `/ehr/data/employee/{id}` | GET | 员工详情 |
| `listEmployeeSelect2Data` | `/ehr/data/employeeDataList` | GET | 员工 Select2 数据 |
| `initUser` | `/ehr/data/initUser` | GET | 初始化用户 |
| `syncData` | `/ehr/data/syncData` | GET | 手动同步 |

> 完整方法说明详见 [ehr-integration.md](ehr-integration.md)

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
| `home` | `/pm/asset/leak/` | GET | 项目资产泄露首页 | `projectAssetLeak:list` |
| `list` | `/pm/asset/leak/list` | GET | 项目资产泄露列表 | `projectAssetLeak:list` |
| `findOne` | `/pm/asset/leak/{id}` | GET | 项目资产泄露详情 | `projectAssetLeak:detail` |
| `detail` | `/pm/asset/leak/detail` | GET | 项目资产泄露详情页面 | `projectAssetLeak:detail` |
| `create` | `/pm/asset/leak/detail` | POST | 新增项目资产泄露 | `projectAssetLeak:add` |
| `update` | `/pm/asset/leak/{id}` | PUT | 更新项目资产泄露 | `projectAssetLeak:edit` |
| `delete` | `/pm/asset/leak/{id}` | DELETE | 删除项目资产泄露 | `projectAssetLeak:delete` |

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
