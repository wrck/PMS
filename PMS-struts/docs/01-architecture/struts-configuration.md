# Struts2 配置文档

> 本文档基于 `config/struts.xml` 和 `config/struts-sys.xml` 配置文件，详细描述 PMS 系统的 Struts2 框架配置，包括包结构、拦截器栈、URL 映射、常量配置及全局结果映射。

---

## 1. 包结构（Package Structure）

PMS 系统的 Struts2 配置采用分层包结构，通过 `namespace` 实现模块化的 URL 路由。

### 1.1 包结构总览

| 包名 | 命名空间 | 继承 | 类型 | 功能说明 |
|:---|:---|:---|:---|:---|
| basepackage | — | struts-default | abstract | 基础抽象包，定义公共拦截器栈和全局结果 |
| defaultJson | — | json-default | abstract | JSON 抽象包，定义 JSON 请求的拦截器栈 |
| login | `/` | struts-default | concrete | 用户登录/登出 |
| base | `/base` | basepackage | concrete | 基础信息维护（用户、角色、部门、基础数据） |
| module | `/module` | basepackage | concrete | 核心功能模块（项目、技术公告、售前、转包等） |
| popwin | `/module/sub` | basepackage | concrete | 弹出层/子页面（上传、查询、回访等） |
| flow | `/work` | basepackage | concrete | 工作流管理 |
| flow_sub | `/work/sub` | basepackage | concrete | 工作流子页面 |
| main | `/sys` | basepackage, json-default | concrete | 系统管理（密码、日志、借出维护） |
| ajax | — | defaultJson | concrete | AJAX JSON 请求（无命名空间） |
| ajaxJSON | `/ajax` | defaultJson | concrete | AJAX JSON 请求（/ajax 命名空间） |

### 1.2 包继承关系图

```
struts-default
    │
    ├── basepackage (abstract)
    │       │
    │       ├── base (/base)
    │       ├── module (/module)
    │       ├── popwin (/module/sub)
    │       ├── flow (/work)
    │       ├── flow_sub (/work/sub)
    │       └── main (/sys) ──── 同时继承 json-default
    │
    └── login (/)

json-default
    │
    ├── defaultJson (abstract)
    │       │
    │       ├── ajax
    │       └── ajaxJSON (/ajax)
    │
    └── main (/sys) ──── 同时继承 basepackage
```

---

## 2. 拦截器栈（Interceptor Stack）

### 2.1 basepackage 拦截器栈（baseStack）

`baseStack` 是 PMS 系统的核心拦截器栈，用于处理所有常规 Action 请求。

**执行顺序**：

```
XssStrutsInterceptor → MyInterceptor → fileUpload → defaultStack → paramsPrepareParamsStack
```

**配置示例**：

```xml
<interceptors>
    <interceptor name="MyInterceptor" class="com.dp.plat.interceptor.MyInterceptor"></interceptor>
    <interceptor name="XssStrutsInterceptor" class="com.dp.plat.security.xss.struts.XssStrutsInterceptor">
        <param name="enable">true</param>
        <param name="excludeUrls">/base/executeSql.*</param>
        <param name="cleanUrls">/module/prob_*,/probAudit.*,/probAjax_*.*</param>
        <param name="encodeUrls">/*</param>
    </interceptor>
    <interceptor-stack name="baseStack">
        <interceptor-ref name="XssStrutsInterceptor"></interceptor-ref>
        <interceptor-ref name="MyInterceptor"></interceptor-ref>
        <interceptor-ref name="fileUpload">
            <param name="maximumSize">209715200</param>
            <param name="allowedTypes"></param>
        </interceptor-ref>
        <interceptor-ref name="defaultStack">
            <param name="workflow.excludeMethods">start</param>
            <param name="validation.excludeMethods">start</param>
        </interceptor-ref>
        <interceptor-ref name="paramsPrepareParamsStack"></interceptor-ref>
    </interceptor-stack>
</interceptors>
<default-interceptor-ref name="baseStack"></default-interceptor-ref>
```

### 2.2 defaultJson 拦截器栈（baseStack）

JSON 请求使用简化的拦截器栈，仅包含 XSS 防护和默认栈。

**执行顺序**：

```
XssStrutsInterceptor → defaultStack
```

**配置示例**：

```xml
<interceptor-stack name="baseStack">
    <interceptor-ref name="XssStrutsInterceptor"></interceptor-ref>
    <interceptor-ref name="defaultStack"></interceptor-ref>
</interceptor-stack>
```

### 2.3 各拦截器说明

| 拦截器 | 类 | 说明 |
|:---|:---|:---|
| XssStrutsInterceptor | `com.dp.plat.security.xss.struts.XssStrutsInterceptor` | XSS 攻击防护拦截器 |
| MyInterceptor | `com.dp.plat.interceptor.MyInterceptor` | 自定义业务拦截器（权限校验等） |
| fileUpload | Struts2 内置 | 文件上传拦截器，限制最大 200MB |
| defaultStack | Struts2 内置 | 默认拦截器栈（参数注入、类型转换、验证等） |
| paramsPrepareParamsStack | Struts2 内置 | 参数-准备-参数拦截器栈（ModelDriven 支持） |

---

## 3. XSS 拦截器详细配置

XSS 拦截器（`XssStrutsInterceptor`）是 PMS 系统安全防护的核心组件，提供三种 URL 处理策略：

### 3.1 配置参数

| 参数名 | 值 | 说明 |
|:---|:---|:---|
| enable | `true` | 启用 XSS 拦截 |
| excludeUrls | `/base/executeSql.*` | 排除的 URL（不进行任何 XSS 处理） |
| cleanUrls | `/module/prob_*,/probAudit.*,/probAjax_*.*` | 清理型 URL（移除危险标签，保留安全内容） |
| encodeUrls | `/*` | 编码型 URL（对特殊字符进行 HTML 实体编码） |

### 3.2 三种处理策略

#### 3.2.1 excludeUrls（排除策略）

- **匹配的 URL**：`/base/executeSql.*`
- **处理方式**：完全跳过 XSS 处理，请求参数原样传递
- **适用场景**：SQL 执行功能，参数中可能包含合法的 SQL 特殊字符

#### 3.2.2 cleanUrls（清理策略）

- **匹配的 URL**：
  - `/module/prob_*` — 技术公告相关页面
  - `/probAudit.*` — 技术公告审批
  - `/probAjax_*.*` — 技术公告 AJAX 请求
- **处理方式**：使用 HTML 清理器（如 OWASP Java HTML Sanitizer）移除危险标签（如 `<script>`），保留安全的 HTML 内容
- **适用场景**：需要支持富文本编辑的页面，允许用户输入格式化 HTML 但过滤恶意脚本

#### 3.2.3 encodeUrls（编码策略）

- **匹配的 URL**：`/*`（所有未匹配 excludeUrls 和 cleanUrls 的请求）
- **处理方式**：对请求参数中的特殊字符进行 HTML 实体编码（如 `<` → `&lt;`）
- **适用场景**：普通表单提交，不需要保留 HTML 格式

### 3.3 处理优先级

```
请求进入
    │
    ▼
是否匹配 excludeUrls？ ──是──→ 跳过 XSS 处理
    │否
    ▼
是否匹配 cleanUrls？ ──是──→ 清理模式（移除危险标签）
    │否
    ▼
是否匹配 encodeUrls？ ──是──→ 编码模式（HTML 实体编码）
    │否
    ▼
默认：编码模式
```

---

## 4. 关键 URL 映射表

### 4.1 登录模块（login，命名空间：`/`）

| URL | Action 类 | 方法 | 功能 |
|:---|:---|:---|:---|
| `/Login.action` | Login | execute | 用户登录 |
| `/Logout.action` | Login | logout | 用户登出 |
| `/404.action` | Login | error404 | 404 错误页面 |

### 4.2 用户管理模块（base，命名空间：`/base`）

| URL | Action 类 | 方法 | 功能 |
|:---|:---|:---|:---|
| `/base/UserManage.action` | UserManageAction | — | 用户列表 |
| `/base/UserEdit.action` | UserManageAction | edit | 用户编辑 |
| `/base/UserAdd.action` | UserManageAction | add | 用户新增 |
| `/base/RoleManage.action` | RoleManageAction | — | 角色列表 |
| `/base/RoleEdit.action` | RoleManageAction | edit | 角色编辑 |
| `/base/RoleAdd.action` | RoleManageAction | add | 角色新增 |
| `/base/RoleAddSubmit.action` | RoleManageAction | addSubmit | 角色新增提交 |
| `/base/RoleEditSubmit.action` | RoleManageAction | editSubmit | 角色编辑提交 |
| `/base/DepartmentManage.action` | DepartmentManageAction | — | 部门列表 |
| `/base/DepartmentRefresh.action` | DepartmentManageAction | refresh | 部门刷新 |
| `/base/DepartmentEdit.action` | DepartmentManageAction | edit | 部门编辑 |
| `/base/DepartmentAdd.action` | DepartmentManageAction | add | 部门新增 |
| `/base/DepartmentAddSubmit.action` | DepartmentManageAction | addSubmit | 部门新增提交 |
| `/base/BasicdataManage.action` | BasicDataManageAction | — | 基础数据管理 |
| `/base/BasicdataUpdate.action` | BasicDataManageAction | basicdataUpdate | 基础数据更新 |
| `/base/BasicdataInsert.action` | BasicDataManageAction | basicdataInsert | 基础数据新增 |
| `/base/PmClosedLoopQuesnaire.action` | PmClosedLoopQuesnaireAction | — | 测评试卷维护 |
| `/base/PmClQues_*.action` | PmClosedLoopQuesnaireAction | {1} | 问卷通配操作 |
| `/base/AddPmClosedLoopQuesnaire.action` | PmClosedLoopQuesnaireAction | addPCLQuesnaire | 问卷添加 |
| `/base/SubmitPmClLQues.action` | PmClosedLoopQuesnaireAction | submitQues | 问卷提交 |
| `/base/SeePmClosedLoopQuesnaire.action` | PmClosedLoopQuesnaireAction | pmCLQuesSee | 问卷查看 |
| `/base/EditPmClosedLoopQuesnaire.action` | PmClosedLoopQuesnaireAction | pmCLQuesEdit | 问卷编辑 |

### 4.3 项目管理模块（module，命名空间：`/module`）

| URL | Action 类 | 方法 | 功能 |
|:---|:---|:---|:---|
| `/module/ProjectManage.action` | ProjectAction | — | 项目列表 |
| `/module/ProjectCreate.action` | ProjectAction | insertProject | 项目创建 |
| `/module/ProjectModify.action` | ProjectAction | updateProject | 项目修改 |
| `/module/ProjectPlanEdit.action` | ProjectAction | editProjectPlan | 项目计划编辑 |
| `/module/BatchChangeProjectMember.action` | ProjectAction | batchChangeMember | 批量变更项目成员 |
| `/module/clearProject.action` | ProjectAction | clearProject | 清理项目 |
| `/module/DownloadFile.action` | ProjectAction | downloadFile | 文件下载 |
| `/module/exportSpotCheck.action` | ProjectAction | exportSpotCheck | 导出抽查记录 |
| `/module/exportOverWarrantyRemind.action` | ProjectAction | exportOverWarrantyRemind | 导出过保提醒 |
| `/module/Workspace.action` | WorkSpaceAction | — | 待办事项/工作台 |
| `/module/DataAnalysis.action` | DataAnalysisAction | — | 数据统计 |
| `/module/MergeContract.action` | ProjectAction | mergeContract | 合同合并 |
| `/module/BranchContract.action` | ProjectAction | branchContract | 项目拆分 |
| `/module/download.action` | UploadAction | downloadFile | 公共文件下载 |
| `/module/createCHProject.action` | ProjectAction | createCHProject | 创建串货项目 |
| `/module/PmClosedLoop.action` | PmClosedLoopAction | — | 项目闭环 |
| `/module/PmClosedLoop_*.action` | PmClosedLoopAction | {1} | 闭环通配操作 |
| `/module/maintenance_*.action` | MaintenanceAction | {1} | 项目维护 |
| `/module/supervision_*.action` | SupervisionAction | {1} | 项目督查 |
| `/module/warrantyCallback_*.action` | WarrantyCallbackAction | {1} | 项目维保回访 |
| `/module/uploadSealInfo.action` | Certificate | uploadSealInfo | 合格证印章上传 |
| `/module/certificate.action` | Certificate | certificate | 合格证查询 |

### 4.4 售前测试模块（module，命名空间：`/module`）

| URL | Action 类 | 方法 | 功能 |
|:---|:---|:---|:---|
| `/module/presales_*.action` | PresalesAction | {1} | 售前测试通配操作 |

### 4.5 技术公告模块（module，命名空间：`/module`）

| URL | Action 类 | 方法 | 功能 |
|:---|:---|:---|:---|
| `/module/prob_*.action` | ProbManageAction | {1} | 技术公告通配操作 |
| `/module/probProduct_*.action` | ProbManageAction | {1}ProbProduct | 产品公告通配操作 |
| `/module/component_*.action` | ProbManageAction | {1}Component | 产品组件通配操作 |
| `/module/report_*.action` | ReportAction | {1} | 报表统计 |

### 4.6 项目转包模块（module，命名空间：`/module`）

| URL | Action 类 | 方法 | 功能 |
|:---|:---|:---|:---|
| `/module/subcontract_*.action` | SubcontractAction | {1} | 项目转包通配操作 |

### 4.7 弹出层/子页面模块（popwin，命名空间：`/module/sub`）

> **说明**：popwin 包实际包含 40+ 个 action 定义，下表仅列出部分代表性 action，完整列表请参考 `config/struts-sys.xml` 源码中 `name="popwin"` 的 package 配置。

#### 4.7.1 回访与上传

| URL | Action 类 | 方法 | 功能 |
|:---|:---|:---|:---|
| `/module/sub/callback_*.action` | CallBackAction | {1} | 回访申请通配操作 |
| `/module/sub/upload.action` | UploadAction | upload | 文件上传 |

#### 4.7.2 技术公告子任务

| URL | Action 类 | 方法 | 功能 |
|:---|:---|:---|:---|
| `/module/sub/toCheckSoftVersion.action` | ProbManageAction | toCheckSoftVersion | 检查软件版本 |
| `/module/sub/weeklyUpload.action` | ProbManageAction | weeklyUpload | 上传任务进展周报 |
| `/module/sub/checkProject.action` | ProbManageAction | checkProject | 检索受技术公告影响的项目 |
| `/module/sub/checkSubProject.action` | ProbManageAction | checkSubProject | 检索受技术公告影响的子项目 |
| `/module/sub/releaseTask.action` | ProbManageAction | releaseTask | 发布修复技术公告任务 |
| `/module/sub/managePrivateTask.action` | ProbManageAction | managePrivateTask | 管理个人任务 |
| `/module/sub/manageAllTask.action` | ProbManageAction | manageAllTask | 管理所有任务 |
| `/module/sub/updatePrivateTask.action` | ProbManageAction | updatePrivateTask | 更新个人任务 |
| `/module/sub/updateRestoreTask.action` | ProbManageAction | updateRestoreTask | 管理员更新任务 |
| `/module/sub/prob_*.action` | ProbManageAction | {1} | 技术公告子页面通配操作 |

#### 4.7.3 项目周报

| URL | Action 类 | 方法 | 功能 |
|:---|:---|:---|:---|
| `/module/sub/CreateWeekly.action` | ProjectAction | createWeekly | 创建项目周报 |
| `/module/sub/EditWeekly.action` | ProjectAction | updateWeekly | 编辑项目周报 |
| `/module/sub/ToUploadFile.action` | ProjectAction | toUploadFile | 跳转上传文件 |
| `/module/sub/UploadFile.action` | ProjectAction | UploadFile | 上传文件 |
| `/module/sub/ToUploadDeliverableFile.action` | ProjectAction | toUploadDeliverableFile | 跳转上传交付物文件 |
| `/module/sub/UploadDeliverableFile.action` | ProjectAction | uploadDeliverableFile | 上传交付物文件 |

#### 4.7.4 项目闭环子页面

| URL | Action 类 | 方法 | 功能 |
|:---|:---|:---|:---|
| `/module/sub/PmClosedLoopSub_*.action` | PmClosedLoopAction | {1} | 项目闭环子页面通配操作 |
| `/module/sub/PmClQuesSub_*.action` | PmClosedLoopQuesnaireAction | {1} | 闭环问卷子页面通配操作 |

#### 4.7.5 项目信息查询子页面

| URL | Action 类 | 方法 | 功能 |
|:---|:---|:---|:---|
| `/module/sub/checkhistsoftversion.action` | ProjectAction | checkhistsoftversion | 检索项目软件历史版本 |
| `/module/sub/checkShipmentInfo.action` | ProjectAction | checkShipmentInfo | 查询项目发货清单 |
| `/module/sub/checkOrderData.action` | ProjectAction | checkOrderData | 获取设备清单 |
| `/module/sub/checkRealOrderData.action` | ProjectAction | checkRealOrderData | 获取实际发货设备清单 |
| `/module/sub/checkSoftVersion.action` | ProjectAction | checkSoftVersion | 获取局点信息、软件版本清单 |
| `/module/sub/queryProjectNotification.action` | ProjectAction | queryProjectNotification | 获取项目系统通知 |
| `/module/sub/projectSub_*.action` | ProjectAction | {1} | 项目子页面通配操作 |
| `/module/sub/transferShipment.action` | ProjectAction | transferShipment | 转移设备查询页面 |
| `/module/sub/importSpotCheckIgnoreItem.action` | ProjectAction | importSpotCheckIgnoreItem | 导入现场验货单 |
| `/module/sub/MergeOrBranchContract_*.action` | ProjectAction | {1} | 合同合并/拆分子页面 |
| `/module/sub/ProjectModify.action` | ProjectAction | updateProject | 项目修改（子页面） |

#### 4.7.6 转包查询子页面

| URL | Action 类 | 方法 | 功能 |
|:---|:---|:---|:---|
| `/module/sub/chooseShipmentInfo.action` | SubcontractAction | chooseShipmentInfo | 转包查询项目序列号清单 |
| `/module/sub/querySubcontractLine.action` | SubcontractAction | querySubcontractLine | 转包查询序列号信息 |
| `/module/sub/chooseSubcontractProject.action` | SubcontractAction | chooseSubcontractProject | 转包查询转包项目清单 |
| `/module/sub/queryContractNoEngineeFee.action` | SubcontractAction | queryContractNoEngineeFee | 转包查询合同无工程费 |
| `/module/sub/querySubcontractPayment.action` | SubcontractAction | querySubcontractPayment | 转包查询付款信息 |
| `/module/sub/querySubcontractDeliver.action` | SubcontractAction | querySubcontractDeliver | 转包查询附件列表 |
| `/module/sub/querySubcontractCallback.action` | SubcontractAction | querySubcontractCallback | 转包查询回访信息 |
| `/module/sub/querySubcontractComment.action` | SubcontractAction | querySubcontractComment | 转包查询评论信息 |
| `/module/sub/querySubcontract*.action` | SubcontractAction | querySubcontract{1} | 转包查询通配操作 |
| `/module/sub/subcontract_*.action` | SubcontractAction | {1} | 项目转包子页面通配操作 |

#### 4.7.7 合格证查询

| URL | Action 类 | 方法 | 功能 |
|:---|:---|:---|:---|
| `/module/sub/queryCertificate.action` | Certificate | queryCertificate | 合格证查询/显示 |

#### 4.7.8 售前回访问卷

| URL | Action 类 | 方法 | 功能 |
|:---|:---|:---|:---|
| `/module/sub/presales_*.action` | PresalesAction | {1} | 售前回访问卷通配操作 |

#### 4.7.9 项目维护/督查/维保子页面

| URL | Action 类 | 方法 | 功能 |
|:---|:---|:---|:---|
| `/module/sub/maintenance_*.action` | MaintenanceAction | {1} | 项目维护子页面通配操作 |
| `/module/sub/supervision_*.action` | SupervisionAction | {1} | 项目督查子页面通配操作 |
| `/module/sub/warrantyCallback_*.action` | WarrantyCallbackAction | {1} | 项目维保子页面通配操作 |

#### 4.7.10 报表子页面

| URL | Action 类 | 方法 | 功能 |
|:---|:---|:---|:---|
| `/module/sub/report_*.action` | ReportAction | {1} | 报表统计子页面通配操作 |

#### 4.7.11 popwin 包与 module 包同名 action 设计模式

popwin 包（命名空间 `/module/sub`）与 module 包（命名空间 `/module`）存在多个同名 action，这是 PMS 系统的一种设计模式——**子页面镜像模式**：

| 同名 action | module 包（主页面） | popwin 包（子页面） | 设计意图 |
|:---|:---|:---|:---|
| `prob_*` | 技术公告主操作，结果渲染 `/sys/prob/prob_{1}.jsp` | 技术公告子页面，结果渲染 `/sys/prob/sub/prob_{1}.jsp` | 主页面与弹出层使用不同的 JSP 模板 |
| `presales_*` | 售前主操作，结果渲染 `/sys/presales/presales_{1}.jsp` | 售前回访问卷，结果渲染 `/sys/presales/presales_{1}.jsp` | 同一 Action 类在不同命名空间复用 |
| `subcontract_*` | 转包主操作，重定向到 `/module` 命名空间 | 转包子页面，重定向到 `/module/sub` 命名空间 | 重定向目标命名空间不同 |
| `maintenance_*` | 维护主页面，结果渲染 `/sys/maintenance/maintenance_{1}.jsp` | 维护子页面，结果渲染 `/sys/maintenance/sub/{1}.jsp` | 主页面与子页面使用不同目录的 JSP |
| `supervision_*` | 督查主页面，结果渲染 `/sys/supervision/supervision_{1}.jsp` | 督查子页面，结果渲染 `/sys/supervision/sub/{1}.jsp` | 主页面与子页面使用不同目录的 JSP |
| `warrantyCallback_*` | 维保主页面，结果渲染 `/sys/warrantyCallback/warrantyCallback_{1}.jsp` | 维保子页面，结果渲染 `/sys/warrantyCallback/sub/{1}.jsp` | 主页面与子页面使用不同目录的 JSP |
| `report_*` | 报表主页面，结果渲染 `/sys/report/report_{1}.jsp` | 报表子页面，结果渲染 `/sys/report/sub/report_{1}.jsp` | 主页面与子页面使用不同目录的 JSP |
| `ProjectModify` | 项目修改，重定向到 `/module` 命名空间 | 项目修改子页面，重定向到 `/module/sub` 命名空间 | 重定向目标命名空间不同 |

**设计要点**：
- 同名 action 通过不同的命名空间（`/module` vs `/module/sub`）实现 URL 隔离，不会产生冲突
- 子页面 action 通常渲染 `sub/` 子目录下的 JSP，提供简化版页面布局（适用于弹出层/iframe 嵌入场景）
- 重定向类型的 action 在两个命名空间中指向不同的目标命名空间，确保流程闭环

### 4.8 工作流模块（flow，命名空间：`/work`）

| URL | Action 类 | 方法 | 功能 |
|:---|:---|:---|:---|
| `/work/WorkFlowAction.action` | WorkFlowAction | — | 流程部署管理 |
| `/work/WorkFlowNewDeploy.action` | WorkFlowAction | newdeploy | 新建部署 |
| `/work/WorkFlowDelDeployment.action` | WorkFlowAction | deldeployment | 删除部署 |
| `/work/WorkFlowViewImage.action` | WorkFlowAction | viewimage | 查看流程图 |
| `/work/WorkFlowSelfTaskManager.action` | WorkFlowAction | selftask | 个人任务管理 |
| `/work/WorkFlowViewTaskForm.action` | WorkFlowAction | viewTaskForm | 查看任务表单 |
| `/work/WorkFlowHisTaskForm.action` | WorkFlowAction | hisTaskForm | 查看历史任务表单 |
| `/work/WorkFlowSubmitTask.action` | WorkFlowAction | submitTask | 提交任务 |
| `/work/WorkFlowTaskManager.action` | WorkFlowAction | taskmanager | 任务管理 |
| `/work/ProcDefDelegateList.action` | WorkFlowAction | delegatelist | 委托列表 |
| `/work/AddProcDefDelegate.action` | WorkFlowAction | delegateadd | 新增委托 |
| `/work/EditProcDefDelegate.action` | WorkFlowAction | delegateedit | 编辑委托 |
| `/work/UpdateProcDefDelegate.action` | WorkFlowAction | delegateupdate | 更新委托 |

### 4.9 工作流子页面（flow_sub，命名空间：`/work/sub`）

| URL | Action 类 | 方法 | 功能 |
|:---|:---|:---|:---|
| `/work/sub/WorkFlowViewCurrentImage.action` | WorkFlowAction | viewCurrentImage | 查看当前流程图 |
| `/work/sub/WorkFlowViewImage.action` | WorkFlowAction | viewimage | 查看流程图 |

### 4.10 系统管理模块（main，命名空间：`/sys`）

| URL | Action 类 | 方法 | 功能 |
|:---|:---|:---|:---|
| `/sys/Password.action` | PasswordGetinfo | executepwd | 修改密码 |
| `/sys/PasswordEditLogin.action` | PasswordGetinfo | editlogin | 编辑登录密码 |
| `/sys/LogManage.action` | OperateLogAction | — | 日志管理 |
| `/sys/ExportLogAll.action` | OperateLogAction | exportlog | 导出日志 |
| `/sys/LendMaintenance.action` | LendMaintenanceAction | — | 借出维护 |
| `/sys/AddLendMaintenance.action` | LendMaintenanceAction | addQuota | 新增借出配额 |
| `/sys/IsLendQuotaRepeat.action` | LendMaintenanceAction | isQuotaRepeat | 检查配额重复（JSON） |
| `/sys/SeeLendMaintenance.action` | LendMaintenanceAction | seeQuota | 查看借出配额 |
| `/sys/UpdateLendMaintenance.action` | LendMaintenanceAction | updateQuota | 更新借出配额 |
| `/sys/DeleteLendMaintenance.action` | LendMaintenanceAction | deleteQuota | 删除借出配额（JSON） |

> **拦截器栈冲突说明**：`main` 包同时继承 `basepackage` 和 `json-default`（`extends="basepackage,json-default"`），而这两个父包各自定义了名为 `baseStack` 的拦截器栈，配置内容不同：
>
> | 父包 | baseStack 组成 |
> |:---|:---|
> | basepackage | XssStrutsInterceptor → MyInterceptor → fileUpload → defaultStack → paramsPrepareParamsStack |
> | json-default (defaultJson) | XssStrutsInterceptor → defaultStack |
>
> Struts2 多继承时，按声明顺序解析，先声明的父包配置优先。因此 `main` 包实际使用 `basepackage` 的完整 `baseStack`（包含 MyInterceptor、fileUpload、paramsPrepareParamsStack），而非 `defaultJson` 的简化栈。这使得 `main` 包中的 Action 既具备完整的业务拦截能力（权限校验、文件上传、ModelDriven 支持），又可通过 `type="json"` 结果类型返回 JSON 响应（如 `IsLendQuotaRepeat`、`DeleteLendMaintenance`）。此设计在功能上是正确的，但存在以下隐患：
> - `baseStack` 命名冲突可能导致维护者误解实际生效的拦截器栈
> - 若未来需要 `main` 包使用简化栈，需显式在包内重新定义 `default-interceptor-ref`

### 4.11 AJAX 模块（ajax，无命名空间）

| URL | Action 类 | 方法 | 功能 |
|:---|:---|:---|:---|
| `/syncTask.action` | OperateLogAction | syncTask | 同步任务 |
| `/updatePresalesTask.action` | PresalesAction | updateTask | 更新售前任务 |
| `/terminate2Close.action` | PresalesAction | terminate2Close | 终止转关闭 |
| `/deleteFile.action` | UploadAction | deleteFile | 删除文件 |
| `/updateConfirmFiles.action` | PresalesAction | updateConfirmFiles | 更新确认文件 |
| `/viewDeployment.action` | WorkFlowAction | viewDeployment | 查看流程部署 |
| `/assignedRate.action` | ReportAction | assignedRate | 指派率统计 |
| `/loadLineData.action` | ReportAction | loadLineData | 折线图数据 |
| `/loadLine_qualityData.action` | ReportAction | loadLine_qualityData | 质量折线图数据 |
| `/loadLine_implData.action` | ReportAction | loadLine_implData | 实施折线图数据 |
| `/traceRate.action` | ReportAction | traceRate | 追踪率统计 |
| `/closeRate.action` | ReportAction | closeRate | 关闭率统计 |
| `/implRate.action` | ReportAction | implRate | 实施率统计 |
| `/quality.action` | ReportAction | quality | 质量统计 |
| `/closeOrderState.action` | OrderManageAction | closeOrderStateAjax | 关闭工单状态 |
| `/queryalluser.action` | ProjectAction | queryalluser | 查询所有用户 |
| `/queryperson.action` | ProjectAction | queryperson | 查询人员 |
| `/queryDpNoRoleUser.action` | ProjectAction | queryDpNoRoleUser | 查询无角色用户 |
| `/instruction.action` | ProjectAction | instruction | 指令操作 |
| `/SaveWeekly.action` | ProjectAction | saveWeekly | 保存周报 |
| `/SubmitWeekly.action` | ProjectAction | submitWeekly | 提交周报 |
| `/DeleteFile.action` | ProjectAction | deleteFile | 删除文件 |
| `/backToLastStep.action` | ProjectAction | backToLastStep | 回退到上一步 |
| `/pmCLoopAjax_execute.action` | PmClosedLoopAction | — | 闭环 AJAX 执行 |
| `/pmCLoopAjax_addPmCLApply.action` | PmClosedLoopAction | addPmCLApply | 添加项目闭环申请 |
| `/pmCLoopAjax_addSmCLApply.action` | PmClosedLoopAction | addSmCLApply | 添加售前闭环申请 |
| `/createMember.action` | ProjectAction | createMember | 创建成员 |
| `/updateMember.action` | ProjectAction | updateMember | 更新成员 |
| `/SaveInstallAdress.action` | ProjectAction | saveInstallAdress | 保存安装地址 |
| `/Feedback.action` | ProjectAction | feedback | 周报回复 |
| `/deleteDeliverById.action` | ProjectAction | deleteDeliverById | 删除交付物 |
| `/updateprojectisback.action` | ProjectAction | updateprojectisback | 更新项目回退状态 |
| `/findbasicdataid.action` | BasicDataManageAction | findBasicDataId | 查找基础数据ID |
| `/updateNotifyState.action` | WorkSpaceAction | updateNotifyState | 更新通知状态 |
| `/checkMergeContract.action` | ProjectAction | checkMergeContract | 检查合并合同 |
| `/checkUsername.action` | UserManageAction | checkUsername | 检查用户名 |
| `/executeSql.action` | BasicDataManageAction | executeSql | 执行 SQL |
| `/importProject.action` | ProjectAction | importProject | 导入项目 |
| `/checkMemberCode.action` | UserManageAction | findUser | 检查成员编码 |
| `/resetPassword.action` | UserManageAction | pwdreset | 重置密码 |
| `/updateSoftVersion.action` | ProjectAction | updateSoftVersion | 更新软件版本 |
| `/submitSoftVersion.action` | ProbManageAction | submitSoftVersion | 确认软件版本 |
| `/uploadImage.action` | UploadAction | uploadImage | 上传图片 |
| `/bacthDeleteProbRestores.action` | ProbManageAction | bacthDeleteProbRestores | 批量删除子任务 |
| `/probAudit.action` | ProbManageAction | audit | 审批技术公告 |
| `/probAjax_*.action` | ProbManageAction | {1} | 技术公告 AJAX 通配 |
| `/projectAjax_*.action` | ProjectAction | {1} | 项目管理 AJAX 通配 |
| `/checkSubcontractName.action` | SubcontractAction | checkSubcontractName | 检查转包名称 |
| `/subcontractAjax_*.action` | SubcontractAction | {1} | 项目转包 AJAX 通配 |
| `/presalesAjax_*.action` | PresalesAction | {1} | 售前 AJAX 通配 |
| `/maintenanceAjax_*.action` | MaintenanceAction | {1} | 项目维护 AJAX 通配 |
| `/supervisionAjax_*.action` | SupervisionAction | {1} | 项目督查 AJAX 通配 |
| `/warrantyCallbackAjax_*.action` | WarrantyCallbackAction | {1} | 维保回访 AJAX 通配 |

### 4.12 AJAX JSON 模块（ajaxJSON，命名空间：`/ajax`）

| URL | Action 类 | 方法 | 功能 |
|:---|:---|:---|:---|
| `/ajax/upload.action` | UploadAction | upload | AJAX 文件上传 |
| `/ajax/queryFile.action` | UploadAction | queryFile | AJAX 查询文件 |

---

## 5. 常量配置（Constants）

### 5.1 常量配置表

| 常量名 | 值 | 说明 |
|:---|:---|:---|
| `struts.custom.i18n.resources` | `com.dp.plat.properties.res` | 国际化资源文件路径 |
| `struts.enable.DynamicMethodInvocation` | `true` | 启用动态方法调用（支持 `action!method` 语法） |
| `struts.ui.theme` | `simple` | Struts2 标签主题风格（简单模式，不自动生成额外 HTML） |
| `struts.objectFactory` | `spring` | 对象工厂由 Spring 管理（Struts2-Spring 集成） |
| `struts.multipart.maxSize` | `209715200` | 文件上传最大尺寸（200MB） |
| `struts.i18n.encoding` | `UTF-8` | 编码格式 |

**配置示例**：

```xml
<constant name="struts.custom.i18n.resources" value="com.dp.plat.properties.res"></constant>
<constant name="struts.enable.DynamicMethodInvocation" value="true" />
<constant name="struts.ui.theme" value="simple"></constant>
<constant name="struts.objectFactory" value="spring"></constant>
<constant name="struts.multipart.maxSize" value="209715200" />
<constant name="struts.i18n.encoding" value="UTF-8"/>
```

### 5.2 关键常量说明

#### struts.objectFactory = spring

将 Struts2 的对象创建交由 Spring 容器管理，实现 Struts2 与 Spring 的深度集成。Action 实例由 Spring 创建，支持依赖注入。

#### struts.enable.DynamicMethodInvocation = true

启用动态方法调用，允许通过 `actionName!methodName.action` 的方式调用 Action 中的指定方法。此配置在 Struts2 2.5 版本后默认关闭（安全考虑），PMS 系统显式开启。

#### struts.ui.theme = simple

使用简单主题，Struts2 标签（如 `<s:textfield>`）不会自动生成表格布局 HTML，便于开发者完全控制页面布局。

#### struts.multipart.maxSize = 209715200

文件上传总大小限制为 200MB（209715200 字节）。此限制作用于整个请求（包含所有文件和表单字段），单个文件大小由 `fileUpload` 拦截器的 `maximumSize` 参数控制。

---

## 6. 全局结果映射（Global Results）

### 6.1 basepackage 全局结果

`basepackage` 抽象包定义了所有业务包共享的全局结果映射：

| 结果名 | 跳转页面 | 说明 |
|:---|:---|:---|
| redirect1 | `/redirect.jsp` | 重定向中间页 |
| globalLogin | `/index.jsp` | 全局登录页（未登录时跳转） |
| globalAdminLogin | `/error403.jsp` | 管理员登录错误页（权限不足） |
| errorRole | `/error403.jsp` | 角色权限错误页 |
| error | `/error.jsp` | 通用错误页 |

**配置示例**：

```xml
<global-results>
    <result name="redirect1">/redirect.jsp</result>
    <result name="globalLogin">/index.jsp</result>
    <result name="globalAdminLogin">/error403.jsp</result>
    <result name="errorRole">/error403.jsp</result>
    <result name="error">/error.jsp</result>
</global-results>
```

### 6.2 全局异常映射

| 异常类型 | 结果名 | 跳转页面 |
|:---|:---|:---|
| `java.lang.Exception` | error | `/error.jsp` |

**配置示例**：

```xml
<global-exception-mappings>
    <exception-mapping result="error" exception="java.lang.Exception">/error.jsp</exception-mapping>
</global-exception-mappings>
```

### 6.3 defaultJson 全局异常映射

JSON 抽象包同样配置了全局异常映射：

```xml
<global-exception-mappings>
    <exception-mapping result="error" exception="java.lang.Exception">/error.jsp</exception-mapping>
</global-exception-mappings>
```

### 6.4 login 包专用结果

`login` 包直接继承 `struts-default`，未使用 `basepackage` 的全局结果，定义了自己的结果映射：

| Action | 结果名 | 跳转页面 |
|:---|:---|:---|
| Login | input | `/login.jsp` |
| Login | error | `/error.jsp` |
| Login | success | `/redirect.jsp` |
| Login | errorCas | `/errorCas.jsp` |
| Logout | success | `/redirect.jsp` |

---

## 7. 模块配置文件引用

PMS 系统采用模块化配置，通过 `<include>` 引入各子模块配置文件：

```xml
<include file="struts-sys.xml" />
```

当前仅引入了 `struts-sys.xml`，包含所有业务模块的 Action 配置。如需新增模块，可创建独立的配置文件并通过 `<include>` 引入。

---

## 8. 通配符方法映射说明

PMS 系统大量使用了 Struts2 的通配符方法映射（Wildcard Method Selection），通过 `name="prefix_*"` 的方式将多个方法映射到同一个 Action 类。

### 8.1 常见通配符模式

| 通配符模式 | 示例 | 方法调用 | 说明 |
|:---|:---|:---|:---|
| `prefix_*` | `prob_list` → `list()` | `{1}` | 单通配符，方法名直接匹配 |
| `prefix_*` + 结果 `{1}` | `prob_list` → `prob_list.jsp` | `{1}` | 方法名同时用于结果页面名 |
| `prefix_*` + 双通配符 | `presales_*` → `{1}` 和 `{2}` | `{1}` | 支持多结果映射 |

### 8.2 通配符配置示例

```xml
<action name="prob_*" class="ProbManageAction" method="{1}">
    <result name="{1}">/sys/prob/prob_{1}.jsp</result>
    <result name="success" type="redirect">
        <param name="location">prob_list.action</param>
        <param name="namespace">/module</param>
    </result>
    <result name="error">/sys/error.jsp</result>
</action>
```

**说明**：访问 `/module/prob_list.action` 时，将调用 `ProbManageAction.list()` 方法，并返回 `prob_list` 结果，渲染 `/sys/prob/prob_list.jsp` 页面。

---

## 9. 配置注意事项

1. **动态方法调用安全风险**：`struts.enable.DynamicMethodInvocation=true` 存在安全风险，Struts2 2.5+ 默认关闭。建议配合 `global-allowed-methods` 使用正则白名单控制可调用的方法
2. **global-allowed-methods 处于注释状态**：源码 `struts-sys.xml` 中 login 包已预留 `global-allowed-methods` 配置但处于注释状态（`<!-- <global-allowed-methods>regex:.*</global-allowed-methods> -->`）。当前系统使用 Struts2 2.0 DTD，不要求配置此项，通配符方法映射可正常工作。但升级到 Struts2 2.5+ 时必须取消注释或在各包中补充此配置，否则所有通配符方法调用（如 `prob_*`、`maintenance_*` 等）将被框架拒绝
3. **XSS 拦截器双重配置**：`XssStrutsInterceptor` 在 `basepackage` 和 `defaultJson` 两个抽象包中分别定义，参数配置一致，修改时需同步更新
4. **文件上传限制**：`struts.multipart.maxSize` 和 `fileUpload` 拦截器的 `maximumSize` 均设置为 200MB，需保持一致
5. **login 包独立性**：`login` 包直接继承 `struts-default` 而非 `basepackage`，因此不经过自定义拦截器栈（XSS、权限校验等），这是合理的——登录页面不需要权限校验
6. **Struts2 版本兼容**：当前 DTD 使用 Struts 2.0 版本，过滤器使用 `ng` 包名。升级到 Struts2 2.5.x 时需注意：
   - DTD 版本升级
   - 过滤器包名去掉 `.ng`
   - 动态方法调用需配置 `global-allowed-methods`
7. **defaultStack 排除方法**：`workflow.excludeMethods=start` 和 `validation.excludeMethods=start` 表示 `start` 方法跳过工作流和验证拦截器，通常用于页面初始化加载
