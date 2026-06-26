# PMS-springmvc 错误码与状态码参考

> 本文档汇总 PMS-springmvc 模块中使用的各类错误码、状态码和编码常量。

---

## 一、HTTP 状态码

| 状态码 | 含义 | 使用场景 |
|--------|------|---------|
| 200 | 成功 | 正常请求响应 |
| 401 | 未授权 | 未登录或 Session 过期 |
| 403 | 禁止访问 | 权限不足 |
| 404 | 资源不存在 | 请求的 URL 或资源不存在 |
| 429 | 请求过多 | 接口限流 |
| 500 | 服务器内部错误 | 业务异常或系统错误 |

---

## 二、项目相关编码

### 2.1 项目类型（ProjectType）

| 编码 | 常量名 | 含义 |
|------|--------|------|
| `10` | JF_SALES_PROJECT | 用服售后项目 |
| `20` | JF_TEST_PROJECT | 用服售前测试 |
| `afss` | AF_SALES_PROJECT | 安服订单项目（安服售后） |
| `afxx` | AF_XX_PROJECT | 安服先行项目 |

### 2.2 项目状态（ProjectState）

| 编码 | 含义 | 说明 |
|------|------|------|
| `10` | 待创建 | 项目初始状态 |
| `30` | 已创建 | 项目创建完成 |
| `31` | 服务经理已指派 | SM 已分配 |
| `32` | 项目经理已指派 | PM 已分配 |
| `34` | 渠道信息已填写 | 渠道信息完成 |
| `40` | 进行中 | 项目执行中 |
| `42` | 项目经理不予跟踪 | PM 选择不跟踪 |
| `100` | 已关闭 | 项目关闭 |

### 2.3 项目成员角色（MemberRole）

| 编码 | 常量名 | 含义 |
|------|--------|------|
| `10` | MEMBER_SALESMAN | 销售人员 |
| `20` | MEMBER_SM | 服务经理 |
| `30` | MEMBER_PM | 项目经理 |
| `40` | MEMBER_PARTY | 团队成员 |
| `50` | MEMBER_SERVICE_CHANNEL | 出货代理商/服务渠道工程师 |
| `60` | MEMBER_CUSTOMER | 最终客户 |
| `70` | MEMBER_TECH_MANMER | 技术经理 |
| `80` | MEMBER_QC | 质量监督员 |

> **注意**：日报权限查询中使用的成员角色为 `20`（服务经理）、`30`（项目经理）、`80`（质量监督员）。

### 2.4 项目回退状态（isback）

| 编码 | 含义 |
|------|------|
| `30` | 创建项目 |
| `32` | 指定项目经理 |
| `34` | 填写渠道信息 |
| `40` | 工程管理部不予跟踪处理 |
| `42` | 项目经理选择不予跟踪 |

---

## 三、转包管理编码

### 3.1 转包类型（DispatchType）

| 编码值 | 常量名 | 含义 |
|--------|--------|------|
| `frameworkAgreement` | FRAMEWORK_AGREEMENT | 框架协议 |
| `thirdPartyServices` | THIRD_PARTY_SERVICES | 第三方服务 |

### 3.2 转包编号前缀（DispatchNOPrefix）

| 前缀 | 含义 |
|------|------|
| `SS` | 安服项目外派合同 |

### 3.3 转包状态（state）

| 编码 | 含义 | 说明 |
|------|------|------|
| `0` | 草稿 | 新建未提交 |
| `1` | 已提交 | 已提交审批 |
| `2` | 审批中 | 审批流程进行中 |
| `3` | 已审批 | 审批通过 |
| `4` | 已驳回 | 审批驳回 |

### 3.4 转包结算状态（state）

| 编码 | 含义 |
|------|------|
| `0` | 草稿 |
| `1` | 已提交 |
| `2` | 已确认 |
| `3` | 已付款 |

---

## 四、工作流编码

### 4.1 流程定义 Key（ProcessType）

| 流程 Key | 常量名 | 含义 |
|----------|--------|------|
| `QualityApproveTrack` | QUALITY_APPROVE_TRACK | 质量审批跟踪流程 |
| `SubcontractInspection` | SUBCONTRACT_INSPECTION | 转包验收流程 |

### 4.2 任务节点 Key（TaskType）

| 任务 Key | 常量名 | 含义 |
|----------|--------|------|
| `afApproveTask` | AF_APPROVE_TASK | 安服质量审核任务 |
| `yfApproveTask` | YF_APPROVE_TASK | 研发质量审核任务 |
| `trackTask` | TRACK_TASK | 任务跟踪任务 |
| `acceptanceTask` | ACCEPTANCE_TASK | 验收材料审批任务 |
| `end` | END | 流程结束 |
| `reject` | REJECT | 流程驳回 |

### 4.3 工作流状态（pm_workflow.status）

| 状态值 | 含义 | 说明 |
|--------|------|------|
| `PENDING` | 待处理 | 审批流程进行中 |
| `COMPLETED` | 已完成 | 审批流程已完成 |
| `TERMINATED` | 已终止 | 审批流程被终止 |

### 4.4 业务数据类型（ProcessType.DataType）

| 数据类型 | 常量名 | 含义 | 关联表 |
|----------|--------|------|--------|
| `project` | PROJECT | 项目 | pm_project |
| `projectTask` | PROJECT_TASK | 项目任务 | pm_project_task |
| `projectOpportunity` | PROJECT_OPPORTUNITY | 项目机会点 | - |
| `dispatch` | PROJECT_DISPATCH | 项目外派 | pm_dispatch_project_header |
| `settlement` | DISPATCH_SETTLEMENT | 项目外派结算 | pm_dispatch_project_settlement |
| `industryAsset` | INDUSTRY_ASSET | 行业资产 | af_industry_asset |
| `industryLeak` | INDUSTRY_LEAK | 行业漏洞 | af_industry_leak |

---

## 五、角色编码

### 5.1 系统角色

| 角色编码 | 常量名 | 含义 |
|----------|--------|------|
| `admin` | ROLE_ADMIN | 系统管理员 |
| `projectAdmin` | ROLE_PM_ADMIN | 项目管理员 |
| `projectSubAdmin` | ROLE_PM_SUB_ADMIN | 子项目管理员 |
| `projectAreaManager` | ROLE_PM_AREA_MANAGER | 区域负责人 |
| `projectManager` | ROLE_PM_PROGRAM | 项目经理 |
| `projectMember` | ROLE_PM_MEMBER | 项目成员 |
| `projectAFQC` | ROLE_PM_AFQC | 安服质量监督员 |
| `projectYFQC` | ROLE_PM_YFQC | 研发质量监督员 |
| `projectSales` | ROLE_PM_SALES | 销售人员 |
| `financialAP` | ROLE_FINANCIAL_AP | 财务 AP |
| `dispatchSettleStaff` | ROLE_PM_DISPATCH_SETTLE_STAFF | 项目外派结算人员 |

### 5.2 权限类型

| 权限类型 | 编码格式 | 说明 |
|---------|---------|------|
| 全部权限 | `module:*` | 拥有模块所有操作权限 |
| 编辑权限 | `module:edit` | 可编辑数据 |
| 查看权限 | `module:list` / `module:detail` | 仅可查看数据 |
| 新增权限 | `module:add` | 可新增数据 |
| 删除权限 | `module:delete` | 可删除数据 |
| 提交权限 | `module:submit` | 可提交审批 |

---

## 六、行业资产/漏洞编码

### 6.1 资产审批状态（af_industry_asset.status）

| 状态值 | 含义 |
|--------|------|
| `0` | 待审批 / 草稿 |
| `1` | 审批中 |
| `2` | 已审批 |

### 6.2 资产跟踪状态（af_industry_asset.trackStatus）

| 状态值 | 含义 |
|--------|------|
| `0` | 未跟踪 |
| `1` | 已跟踪 |

### 6.3 漏洞等级（af_industry_leak.leakLevel）

| 等级 | 含义 |
|------|------|
| `high` | 高危 |
| `medium` | 中危 |
| `low` | 低危 |

---

## 七、数据同步编码

### 7.1 同步类型（SyncType）

| 类型编码 | 含义 | 说明 |
|---------|------|------|
| `FULL_SYNC` | 全量同步 | 清空目标表后全量写入 |
| `INCREMENTAL_SYNC` | 增量同步 | 仅同步变更数据 |

### 7.2 数据源标识

| 标识 | 含义 | 数据库类型 |
|------|------|-----------|
| `PMS` | PMS 本地数据源 | MySQL |
| `SMS` | SMS 系统 | SQL Server |
| `EHR` | EHR 人力资源系统 | MySQL |
| `D365` | D365 ERP 系统 | SQL Server |
| `CRM` | CRM 客户关系管理 | - |
| `SAP` | SAP ERP 系统 | SQL Server |

### 7.3 同步日志状态（sync_log.isSuccess）

| 状态值 | 含义 |
|--------|------|
| `true` | 同步成功 |
| `false` | 同步失败 |

---

## 八、通用状态码

### 8.1 逻辑删除（disabled）

| 取值 | 含义 |
|------|------|
| `0` (b'0') | 有效记录 |
| `1` (b'1') | 已删除记录 |

### 8.2 有效期状态

| effectiveTo 取值 | 含义 |
|------------------|------|
| `NULL` | 记录当前有效 |
| 非 NULL 日期 | 记录已失效 |

### 8.3 URL 路径前缀

| 前缀 | 常量名 | 含义 |
|------|--------|------|
| `/pm/` | PROJECT_MANAGER | 项目管理模块 |
| `/af/` | AF_MANAGER | 安服管理模块 |

---

## 九、业务异常码

| 异常码 | 含义 | 触发场景 |
|--------|------|---------|
| `400` | 参数错误 | 请求参数校验失败 |
| `401` | 未登录 | Session 过期或未登录 |
| `403` | 权限不足 | `checkPermission` 返回 false |
| `404` | 资源不存在 | 查询不到业务数据 |
| `500` | 系统异常 | 未捕获的异常 |

### 9.1 常见业务异常消息

| 消息 | 触发场景 |
|------|---------|
| `没有权限进行该操作！` | `checkPermission` 失败 |
| `项目编码已存在` | 新增项目时 projectCode 重复 |
| `转包项目已结算，不能删除` | 删除已结算的转包项目 |
| `审批内容发生变更！` | 编辑数据时终止进行中的审批流程 |
| `系统繁忙，请稍后重试` | 接口限流或系统异常 |

---

## 十、EHR 相关编码

### 10.1 员工状态（ehr_employee.empStatus）

| 状态值 | 含义 |
|--------|------|
| `1` | 在职 |
| `2` | 离职 |
| `3` | 试用期 |

### 10.2 员工类型（ehr_employee.empType）

| 类型值 | 含义 |
|--------|------|
| `1` | 正式员工 |
| `2` | 派遣员工 |
| `3` | 实习生 |

### 10.3 公司等级（ehr_company.compGrade）

| 等级 | 含义 |
|------|------|
| `1` | 一级公司（总部） |
| `2` | 二级公司（分公司） |
| `3` | 三级公司（办事处） |
