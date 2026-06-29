# 标准化接口文档模板

> 本模板用于PMS系统所有Action接口的标准化文档编写。每个接口对应一个文档实例，按照以下结构填写。

---

## 接口基本信息

| 项目 | 内容 |
|------|------|
| 接口名称 | [接口中文名称，如：创建项目] |
| Action类 | [如：ProjectAction] |
| 方法名 | [如：insertProject] |
| URL | [如：/module/Project_insertProject.action] |
| HTTP方法 | [GET / POST] |
| 功能描述 | [接口功能的简要描述] |
| 权限要求 | [需要的角色/权限，如：SM/PM/管理员] |
| 所属模块 | [如：项目管理] |
| 命名空间 | [如：/module] |
| 继承包 | [如：basepackage] |

---

## 请求参数

### 表单参数（POST）

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| project.projectCode | String | 否 | 自动生成，无需传入 | 自动生成 | 项目编码 |
| project.projectName | String | 是 | 非空，最大200字符 | - | 项目名称 |
| project.column001 | String | 是 | 非空，必须为有效办事处编码 | - | 办事处编码 |
| project.contractNo | String | 是 | 非空，唯一 | - | 合同号 |
| project.column002 | String | 否 | 最大200字符 | - | 市场名称 |
| project.column003 | String | 否 | 最大200字符 | - | 系统名称 |
| project.column005 | String | 否 | 最大200字符 | - | 行业名称 |
| project.column010 | String | 否 | 基础数据编码校验 | - | 重大级别 |
| project.column012 | String | 否 | 基础数据编码校验 | - | 服务类型 |
| project.compId | String | 否 | 最大50字符 | - | 公司编码 |

### URL参数（GET）

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| paramId | String | 是 | Base64编码的项目ID | - | 加密项目ID |

### Session/上下文参数

| 参数名 | 来源 | 说明 |
|--------|------|------|
| UserContext | Session | 当前登录用户上下文，包含用户名、角色、权限、区域权限 |
| userContext.loginName | Session | 当前用户名，用于createBy/updateBy |
| userContext.areapower | Session | 区域权限，逗号分隔的officeCode列表 |

---

## 响应结果

### 成功响应

| result名 | 跳转页面/JSON结构 | 说明 |
|----------|-------------------|------|
| SUCCESS | /sys/module/project_insertProject.jsp | 创建成功，跳转到项目详情页 |
| INPUT | /sys/module/project_insertProject.jsp | 页面初始化/表单验证失败，返回输入页 |

### 错误响应

| result名 | 跳转页面/JSON结构 | 说明 |
|----------|-------------------|------|
| ERROR | /sys/module/project_insertProject.jsp | 业务逻辑错误，页面显示错误信息 |
| globalLogin | /index.jsp | 未登录，重定向到登录页 |
| errorRole | /error403.jsp | 无权限，重定向到403页面 |
| error | /error.jsp | 系统异常，重定向到错误页 |

### 响应数据模型

| 属性名 | 类型 | 说明 |
|--------|------|------|
| project | Project | 项目对象（含所有字段） |
| errmsg | String | 错误信息（通过`<dp:errormsg>`标签显示） |
| warnmsg | String | 警告信息 |

---

## 处理逻辑

### 步骤化描述

```
步骤1: 页面初始化（start方法）
  ├── 调用: projectService.queryProjectByContractNo(contractNo)
  ├── 逻辑: 根据合同号查询ERP订单数据
  ├── 输出: 设置projectList到ValueStack
  └── 返回: INPUT

步骤2: 提交创建（insertProject方法）
  ├── 2.1 参数校验
  │     ├── 校验: contractNo非空
  │     ├── 校验: contractNo唯一性（未创建过项目）
  │     └── 校验: column001(办事处)非空
  │
  ├── 2.2 生成项目编码
  │     ├── 调用: projectService.queryProjectCode()
  │     ├── 逻辑: 自动生成唯一projectCode
  │     └── 异常: 编码冲突→重新生成
  │
  ├── 2.3 创建项目主记录
  │     ├── 调用: projectService.insertProject(project)
  │     ├── 操作: INSERT pm_project_header
  │     ├── 操作: INSERT pm_project_contract
  │     ├── 操作: INSERT pm_project_product_line
  │     ├── 操作: INSERT pm_project_state (state=30)
  │     ├── 操作: INSERT pm_project_group + pm_project_group_relationship
  │     └── 异常: contractNo重复→CustomRuntimeException
  │
  ├── 2.4 收集错误信息
  │     ├── 调用: setErrmsg(projectService)
  │     └── 逻辑: 从Service层收集错误/警告信息
  │
  └── 2.5 返回结果
        ├── isError()=true → ERROR
        └── isError()=false → SUCCESS
```

### 调用的Service方法

| Service方法 | 所在类 | 事务 | 说明 |
|-------------|--------|------|------|
| queryProjectByContractNo() | ProjectServiceImpl | 否(查询) | 根据合同号查询ERP订单 |
| queryProjectCode() | ProjectServiceImpl | 否(查询) | 生成唯一项目编码 |
| insertProject() | ProjectServiceImpl | 是(insert*) | 创建项目及关联数据 |

---

## 异常处理

| 异常场景 | 异常类型 | 处理方式 | 错误码 | 用户提示 |
|----------|----------|----------|--------|----------|
| 合同号已创建项目 | CustomRuntimeException | addErrmsg() | - | "合同号已创建项目" |
| 项目编码冲突 | RuntimeException | 重新生成编码 | - | 无感知（自动重试） |
| 办事处编码无效 | CustomRuntimeException | addErrmsg() | - | "办事处编码无效" |
| 用户未登录 | - | 重定向 | - | 跳转登录页 |
| 无操作权限 | CustomRuntimeException | 重定向403 | - | "无权限" |
| 数据库异常 | SQLException | 事务回滚 | - | "操作失败，请重试" |
| 项目组编码并发冲突 | - | synchronized保护 | - | ⚠️ 集群环境有隐患 |

---

## 业务规则

| 规则编号 | 触发条件 | 执行逻辑 | 备注 |
|----------|----------|----------|------|
| BR-001 | 项目创建 | 自动生成projectCode，格式：PG+年月+序号 | 编码规则 |
| BR-002 | 项目创建 | 初始状态设为STATE_30（待指派SM） | 状态机初始态 |
| BR-003 | 项目创建 | 自动创建项目组，projectGroupCode自动生成 | ⚠️ 并发安全 |
| BR-004 | 合同号重复 | 阻止创建，返回错误信息 | 唯一性校验 |
| BR-005 | 项目创建 | 从ERP订单数据映射合同和产品线信息 | 数据转换 |
| BR-006 | 项目创建 | 设置createBy为当前用户名 | 审计字段 |

---

## 数据变更

### 涉及的表及CRUD操作

| 表名 | 操作类型 | 操作说明 | 关键字段 |
|------|----------|----------|----------|
| pm_project_header | C | 创建项目主记录 | projectCode, projectName, column001-020, compId |
| pm_project_contract | C | 创建合同关联 | contractNo, projectGroupCode |
| pm_project_product_line | C | 创建产品线关联 | itemCode, itemName, orderQuantity |
| pm_project_state | C | 初始化项目状态 | state=30, closeProcessState=0 |
| pm_project_group | C | 创建项目组 | projectGroupCode |
| pm_project_group_relationship | C | 创建项目组关联 | projectGroupId, projectId |
| pm_order_data_from_erp_source | R | 查询ERP订单数据 | contractNo |
| fnd_operate_log | C | 记录操作日志 | username, option, createTime |

### 数据转换规则

| 源字段 | 目标表.字段 | 转换逻辑 |
|--------|-------------|----------|
| contractNo | pm_project_contract.contractNo | 直接映射 |
| itemCode | pm_project_product_line.itemCode | 直接映射 |
| officeCode | pm_project_header.column001 | 直接映射（泛化字段） |
| marketName | pm_project_header.column002 | 直接映射（泛化字段） |
| systemName | pm_project_header.column003 | 直接映射（泛化字段） |
| corpCode | pm_project_header.compId | 直接映射 |

---

## 附录：模板使用说明

### 填写规范

1. **接口基本信息**：必须完整填写所有字段，URL需包含命名空间
2. **请求参数**：区分POST表单参数和GET URL参数，标注必填和校验规则
3. **响应结果**：列出所有可能的result映射和对应的跳转页面
4. **处理逻辑**：步骤化描述，标注调用的Service方法名
5. **异常处理**：覆盖所有可能的异常场景
6. **业务规则**：编号管理，便于需求追溯
7. **数据变更**：明确标注涉及的表和CRUD操作类型

### 命名规范

- 接口文档文件名：`{模块名}-{Action名}-{方法名}.md`
- 例如：`project-ProjectAction-insertProject.md`

### 示例接口列表

以下接口应按照此模板编写文档：

| 模块 | Action | 方法 | 优先级 |
|------|--------|------|--------|
| 项目管理 | ProjectAction | insertProject | 高 |
| 项目管理 | ProjectAction | updateProject | 高 |
| 项目管理 | ProjectAction | updateProjectByProjectId | 高 |
| 项目管理 | ProjectAction | backToLastStep | 高 |
| 系统管理 | UserManageAction | add | 高 |
| 系统管理 | UserManageAction | edit | 高 |
| 系统管理 | LoginAction | login | 高 |
| 售前测试 | PresalesAction | startPresalesFlow | 中 |
| 闭环回访 | PmClosedLoopAction | addPmCLApply | 中 |
| 闭环回访 | PmClosedLoopAction | smAudit | 中 |
| 闭环回访 | PmClosedLoopAction | cbCallback | 中 |
| 闭环回访 | PmClosedLoopAction | clConfirm | 中 |
| 转包 | SubcontractAction | insertSubcontract | 中 |
| 技术公告 | ProbAction | insertProb | 低 |
| 问卷 | PmClosedLoopQuesnaireAction | insertQuesnaireHeader | 低 |
