# 技术公告管理功能说明文档

## 1. 模块概述

技术公告管理模块（Prob）负责技术公告的发布、审批、跟踪和修复任务管理。支持软件版本影响范围匹配、受影响项目检索、修复任务发布与跟踪、进展周报管理、阅读确认、统计报表等功能，是产品技术问题跟踪与修复管控的核心模块。

### 涉及的Action类列表

| Action类 | 包路径 | 职责 |
|----------|--------|------|
| `ProbManageAction` | `com.dp.plat.prob.action` | 技术公告全生命周期管理（创建/编辑/审批/修复任务/统计/阅读确认/产品组件/产品型号） |

### 涉及的Service类列表

| Service类 | 包路径 | 依赖DAO |
|-----------|---------|---------|
| `ProbManageServiceImpl` | `com.dp.plat.prob.service` | `ProbManageDao` |

### 涉及的DAO类列表

| DAO类 | 包路径 |
|-------|--------|
| `ProbManageDaoImpl` | `com.dp.plat.prob.dao` |

### 涉及的数据库表列表

| 表名 | 说明 |
|------|------|
| `prob_main` | 技术公告信息主表 |
| `prob_restore` | 技术公告修复任务（子任务）表 |
| `prob_restore_process` | 修复任务流程过程记录表 |
| `prob_restore_weekly` | 修复任务进展周报表 |
| `prob_soft_version` | 技术公告软件版本表 |
| `prob_softwares` | 软件版本信息表 |
| `prob_read_log` | 技术公告阅读记录表 |

### 依赖的其他模块

- 项目管理模块（项目信息关联、发货清单、软件版本查询）
- 系统管理模块（用户信息、基础数据、部门信息）
- 邮件服务模块（技术公告通知邮件）
- 报表统计模块（Echarts图表）

## 2. 业务流程

### 2.1 技术公告管理流程

```
[创建技术公告] ──> [保存/提交] ──> [管理员审批] ──> [发布修复任务] ──> [修复跟踪] ──> [闭环]
      |                |               |                |               |            |
 ProbManageAction  ProbManageAction ProbManageAction ProbManageAction ProbManageAction ProbManageAction
 .input()         .save()         .audit()         .releaseTask()  .manageAllTask() .updateRestoreTask()
```

### 2.2 技术公告状态机转换图

```
┌───────────┐
│     0     │
│  (草稿)    │
└─────┬─────┘
      │ save() (status非0)
      v
┌───────────┐
│     1     │
│ (待确认)   │
└─────┬─────┘
      │ audit() 审批通过
      v
┌───────────┐
│     4     │
│ (已确认)   │
└─────┬─────┘
      │ releaseTask() 发布修复任务
      v
┌───────────┐
│     5     │
│ (解决中)   │
└─────┬─────┘
      │ audit() 关闭
      v
┌───────────┐
│    10     │
│ (已关闭)   │
└───────────┘

特殊状态:
┌───────────┐
│     6     │  ← audit() 驳回（从状态1/4/5）
│ (已拒绝)   │     → update() 修改后重新提交回到1
└───────────┘

┌───────────┐
│     8     │  ← update() 非管理员更新时自动设为待确认
│ (待确认)   │
└───────────┘
```

### 2.3 修复任务状态流转

```
┌───────────┐
│    10     │
│(发布接受)  │  ← releaseTask() 发布任务
└─────┬─────┘
      │ managePrivateTask() / updatePrivateTask()
      v
┌───────────┐
│    20     │
│(办事处返回)│  ← 无需跟踪的子任务返回
└─────┬─────┘
      │ updatePrivateTask()
      v
┌───────────┐
│    30     │
│(闭环申请)  │  ← 办事处已处理，申请闭环
└─────┬─────┘
      │ updateRestoreTask() 管理员审批
      v
┌───────────┐
│    31     │
│  (闭环)   │  ← 管理员审批通过闭环 / 发布时直接闭环
└───────────┘
```

## 3. 接口文档

### 3.1 技术公告列表查询

| 项目 | 说明 |
|------|------|
| URL | /module/prob_list.action |
| HTTP方法 | GET |
| 功能描述 | 分页查询技术公告列表，支持多条件筛选 |
| 权限要求 | 已登录用户（按角色和区域权限过滤） |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| displayParam | DisplayParam | 否 | - | 默认分页 | 分页参数 |
| prob.watch | String | 否 | - | 无 | 跟踪类型过滤（基础数据编码"30"） |
| prob.status | String | 否 | - | 无 | 状态过滤（基础数据编码"31"） |
| prob.priority | String | 否 | - | 无 | 严重级别过滤（基础数据编码"32"） |
| prob.affectedVersion | String | 否 | - | 无 | 影响版本模糊搜索 |
| prob.productType | String | 否 | - | 无 | 产品类型过滤 |

**返回结果**：

| result名 | 类型 | 跳转页面 | 说明 |
|----------|------|----------|------|
| list | String | /sys/prob/prob_list.jsp | 查询成功 |
| ERROR | String | /sys/error.jsp | 查询失败 |

### 3.2 进入创建页面

| 项目 | 说明 |
|------|------|
| URL | /module/prob_input.action |
| HTTP方法 | GET |
| 功能描述 | 进入技术公告创建页面 |
| 权限要求 | 已登录用户 |

**返回结果**：INPUT → /sys/prob/prob_input.jsp

### 3.3 保存技术公告

| 项目 | 说明 |
|------|------|
| URL | /module/prob_save.action |
| HTTP方法 | POST |
| 功能描述 | 保存技术公告信息（新建） |
| 权限要求 | 已登录用户 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| prob.theme | String | 是 | 非空 | 无 | 技术公告主题 |
| prob.desc | String | 是 | 非空 | 无 | 技术公告描述 |
| prob.solution | String | 否 | - | 无 | 解决方案 |
| prob.watch | String | 是 | 非空 | 无 | 跟踪类型 |
| prob.priority | String | 是 | 非空 | 无 | 严重级别 |
| prob.status | String | 否 | - | "1" | 状态（"0"=草稿，"1"=待确认） |
| softVersionList | List\<SoftVersion\> | 否 | - | 无 | 受影响软件版本列表 |
| upload | File[] | 否 | - | 无 | 上传附件 |
| isContinue | int | 否 | - | 0 | 是否继续编辑（0=否，1=是） |

**返回结果**：

| result名 | 类型 | 跳转页面 | 说明 |
|----------|------|----------|------|
| SUCCESS | String | 重定向到prob_list | 保存成功 |
| continue | String | 重定向到prob_edit | 继续编辑 |
| ERROR | String | /sys/error.jsp | 保存失败 |

**处理逻辑**：
1. 上传附件 → `basicDataService.insertFileInfo()`
2. 保存技术公告 → `probManageService.saveProb()`
3. 保存软件版本信息 → `probManageService.updateProbSoftVersion()`
4. 保存产品型号信息 → `probManageService.updateProbProduct()`
5. 非草稿状态发送邮件通知技术公告员审批

### 3.4 更新技术公告

| 项目 | 说明 |
|------|------|
| URL | /module/prob_update.action |
| HTTP方法 | POST |
| 功能描述 | 更新技术公告信息 |
| 权限要求 | 技术公告创建人/管理员 |

**输入参数**：同3.3，额外需要 `prob.probId`

**返回结果**：SUCCESS → 重定向到prob_list

**处理逻辑**：
1. 非管理员更新时状态自动改为"8"（待确认）
2. 管理员审批通过（状态"4"）时通知tsc/sp/pdt_ld/xteam群组
3. 已拒绝状态（"6"）时只通知任务创建者
4. 发送邮件通知相关人员

### 3.5 查看技术公告详情（编辑页面）

| 项目 | 说明 |
|------|------|
| URL | /module/prob_edit.action |
| HTTP方法 | GET |
| 功能描述 | 查看技术公告详细信息及修复任务 |
| 权限要求 | 已登录用户（按角色权限过滤子任务） |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| prob.probId | int | 是 | 非空 | 无 | 技术公告ID |

**返回结果**：edit → /sys/prob/prob_edit.jsp

### 3.6 删除技术公告

| 项目 | 说明 |
|------|------|
| URL | /module/prob_delete.action |
| HTTP方法 | POST |
| 功能描述 | 删除技术公告 |
| 权限要求 | 管理员 |

**输入参数**：`prob.probId`（必填）

**返回结果**：SUCCESS → 重定向到prob_list

### 3.7 审批技术公告

| 项目 | 说明 |
|------|------|
| URL | /ajax/probAudit.action |
| HTTP方法 | POST |
| 功能描述 | 管理员审批技术公告（通过/驳回） |
| 权限要求 | 技术公告管理员（ROLE_PROB_ADMIN） |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| prob.probId | int | 是 | 非空 | 无 | 技术公告ID |
| prob.status | String | 是 | 非空 | 无 | 审批后状态（"4"=通过，"6"=驳回） |
| prob.remark | String | 否 | - | 无 | 审批意见 |
| softVersionList | List\<SoftVersion\> | 否 | - | 无 | 更新的软件版本列表 |

**返回结果**：SUCCESS → JSON `{result: "success"/错误信息}`

### 3.8 检索受影响项目

| 项目 | 说明 |
|------|------|
| URL | /module/sub/checkProject.action |
| HTTP方法 | GET |
| 功能描述 | 检索受技术公告影响的项目集合 |
| 权限要求 | 已登录用户 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| probRestore.probId | int | 是 | 非空 | 无 | 技术公告ID |
| restoreDisplayParam | DisplayParam | 否 | - | 默认分页 | 分页参数 |
| firstCheck | boolean | 否 | - | false | 是否首次打开（首次不查询） |

**返回结果**：SUCCESS → /sys/prob/check_project.jsp

### 3.9 发布修复任务

| 项目 | 说明 |
|------|------|
| URL | /module/sub/releaseTask.action |
| HTTP方法 | POST |
| 功能描述 | 发布技术公告修复任务 |
| 权限要求 | 技术公告管理员/技术支持人员 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| probRestore.probId | int | 是 | 非空 | 无 | 技术公告ID |
| probRestore.assignee | String | 否 | - | 无 | 指定办理人 |
| probRestore.restoreStatus | int | 否 | - | 0 | 修复状态（0=开始流程，10=发布接受，31=直接闭环） |
| probRestoreTaskList | List\<ProbRestore\> | 是 | 非空 | 无 | 子任务列表 |

**返回结果**：SUCCESS → 重定向到checkProject

**处理逻辑**：
1. 技术公告状态改为"5"（解决中）
2. 插入流程过程记录
3. 批量插入子任务
4. 发送邮件通知相关人员

### 3.10 管理个人任务

| 项目 | 说明 |
|------|------|
| URL | /module/sub/managePrivateTask.action |
| HTTP方法 | GET |
| 功能描述 | 管理当前用户的修复任务 |
| 权限要求 | 已登录用户 |

**返回结果**：SUCCESS → /sys/prob/manage_private_task.jsp

### 3.11 更新个人任务状态

| 项目 | 说明 |
|------|------|
| URL | /module/sub/updatePrivateTask.action |
| HTTP方法 | POST |
| 功能描述 | 更新个人修复任务状态 |
| 权限要求 | 任务办理人 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| probRestore.probId | int | 是 | 非空 | 无 | 技术公告ID |
| probRestore.restoreStatus | int | 是 | 非空 | 无 | 更新后状态 |
| restoreIds | String | 是 | 非空 | 无 | 要操作的子任务ID串 |

**返回结果**：SUCCESS → 重定向到managePrivateTask

### 3.12 管理员管理所有任务

| 项目 | 说明 |
|------|------|
| URL | /module/sub/manageAllTask.action |
| HTTP方法 | GET |
| 功能描述 | 管理员查看和管理所有修复任务 |
| 权限要求 | 技术公告管理员/技术支持人员 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| probRestore.probId | int | 是 | 非空 | 无 | 技术公告ID |
| probRestore.restoreStatus | int | 否 | - | 无 | 任务状态（10/20/30/31） |

**返回结果**：SUCCESS → /sys/prob/manage_all_task.jsp

### 3.13 管理员更新任务

| 项目 | 说明 |
|------|------|
| URL | /module/sub/updateRestoreTask.action |
| HTTP方法 | POST |
| 功能描述 | 管理员更新修复任务 |
| 权限要求 | 技术公告管理员/技术支持人员 |

**输入参数**：同3.11

**返回结果**：SUCCESS → 重定向到manageAllTask

### 3.14 上传进展周报

| 项目 | 说明 |
|------|------|
| URL | /module/sub/weeklyUpload.action |
| HTTP方法 | POST |
| 功能描述 | 上传修复任务进展周报 |
| 权限要求 | 已登录用户 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| probRestore.probId | int | 是 | 非空 | 无 | 技术公告ID |
| upload | File[] | 是 | 非空 | 无 | 周报附件 |

**返回结果**：SUCCESS → 重定向到managePrivateTask

### 3.15 导出技术公告

| 项目 | 说明 |
|------|------|
| URL | /module/prob_export.action |
| HTTP方法 | GET |
| 功能描述 | 导出技术公告列表为Excel |
| 权限要求 | 已登录用户 |

**返回结果**：直接输出Excel文件流

### 3.16 导入软件版本

| 项目 | 说明 |
|------|------|
| URL | /module/prob_importSoftVersion.action |
| HTTP方法 | POST |
| 功能描述 | 上传Excel批量导入软件版本 |
| 权限要求 | 技术公告技术支持人员（ROLE_PROB_SUPPORTER） |

**返回结果**：importSoftVersion → /sys/prob/prob_importSoftVersion.jsp

### 3.17 查询软件版本

| 项目 | 说明 |
|------|------|
| URL | /module/sub/toCheckSoftVersion.action |
| HTTP方法 | GET |
| 功能描述 | 进入查询软件版本页面 |
| 权限要求 | 已登录用户 |

**返回结果**：INPUT → /sys/prob/check_soft_version.jsp

### 3.18 确认选择软件版本

| 项目 | 说明 |
|------|------|
| URL | /ajax/submitSoftVersion.action |
| HTTP方法 | POST |
| 功能描述 | 确认选择的软件版本 |
| 权限要求 | 已登录用户 |

**输入参数**：`softVersionCodes`（软件版本编码串）

**返回结果**：SUCCESS → JSON `{result: 软件版本列表JSON}`

### 3.19 解析软件版本范围

| 项目 | 说明 |
|------|------|
| URL | /ajax/probAjax_parserSoftVersion.action |
| HTTP方法 | POST |
| 功能描述 | 根据手工录入信息解析软件版本范围 |
| 权限要求 | 已登录用户 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| softVersion.manualEntry | String | 是 | 非空 | 无 | 手工录入的版本范围描述 |
| softVersion.platformType | String | 否 | - | 无 | 平台类型 |
| softVersion.softVersionTypes | String | 否 | - | 无 | 固定版本类型 |

**返回结果**：SUCCESS → JSON `{result: 解析结果}`

### 3.20 技术公告统计

| 项目 | 说明 |
|------|------|
| URL | /module/prob_statistics.action |
| HTTP方法 | GET |
| 功能描述 | 技术公告统计报表 |
| 权限要求 | 已登录用户 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| probStatistic.startTime | String | 否 | - | 当前季度第一天 | 统计开始时间 |
| probStatistic.endTime | String | 否 | - | 当前季度最后一天 | 统计结束时间 |
| probStatistic.tabIndex | int | 否 | - | 0 | 选项卡索引（0=统计表，2=项目列表，3=合同发货软件版本，4+=受影响项目） |

**返回结果**：statistics → /sys/prob/prob_project_statistics.jsp

### 3.21 阅读确认

| 项目 | 说明 |
|------|------|
| URL | /ajax/probAjax_readSure.action |
| HTTP方法 | POST |
| 功能描述 | 确认已阅读技术公告 |
| 权限要求 | 已登录用户 |

**输入参数**：`probReadLog.probId`（必填）

**返回结果**：SUCCESS → JSON `{result: "success"/"error"}`

### 3.22 阅读记录

| 项目 | 说明 |
|------|------|
| URL | /module/sub/prob_readLog.action |
| HTTP方法 | GET |
| 功能描述 | 查询技术公告阅读记录 |
| 权限要求 | 已登录用户（管理员可查看全部，其他用户只能查看自己） |

**输入参数**：`probReadLog.probId`（必填）

**返回结果**：SUCCESS → /sys/prob/sub/prob_readLog.jsp

### 3.23 产品组件管理

| 项目 | 说明 |
|------|------|
| URL | /module/component_list.action |
| HTTP方法 | GET |
| 功能描述 | 查询产品组件列表 |
| 权限要求 | 已登录用户 |

**返回结果**：list → /sys/component/component_list.jsp

### 3.24 产品型号管理

| 项目 | 说明 |
|------|------|
| URL | /module/probProduct_list.action |
| HTTP方法 | GET |
| 功能描述 | 查询产品型号列表 |
| 权限要求 | 已登录用户 |

**返回结果**：list → /sys/prob/product/list.jsp

### 3.25 批量删除子任务

| 项目 | 说明 |
|------|------|
| URL | /ajax/bacthDeleteProbRestores.action |
| HTTP方法 | POST |
| 功能描述 | 批量删除修复子任务 |
| 权限要求 | 已登录用户 |

**输入参数**：`probRestoreIds`（子任务ID串）

**返回结果**：SUCCESS → JSON `{result: "200"/错误信息}`

## 4. Service层详解

### 4.1 ProbManageServiceImpl.saveProb(Prob, List\<SoftVersion\>, String)

- **功能描述**：保存技术公告（新建）
- **事务类型**：@Transactional
- **核心算法**：
  1. 保存产品型号信息，拼接产品类型名称
  2. 保存技术公告主表 → `probManageDao.saveProb()`
  3. 保存软件版本 → `updateProbSoftVersion()`
  4. 保存产品型号 → `updateProbProduct()`
  5. 草稿状态（"0"）直接返回
  6. 非草稿发送邮件通知技术公告员审批
- **调用的DAO方法**：`probManageDao.saveProb()`, `probManageDao.queryOneProb()`, `probManageDao.updateInvalidSoftVersion()`, `probManageDao.saveSoftVersion()`, `probManageDao.insertProbProductSelective()`

### 4.2 ProbManageServiceImpl.updateProb(Prob, List\<SoftVersion\>)

- **功能描述**：更新技术公告
- **事务类型**：@Transactional
- **核心算法**：
  1. 草稿状态不修改状态
  2. 非管理员更新时状态改为"8"（待确认）
  3. 更新产品型号信息
  4. 更新主表 → `probManageDao.updateProb()`
  5. 更新软件版本和产品型号
  6. 根据角色和状态发送不同邮件通知
- **调用的DAO方法**：`probManageDao.updateProb()`, `probManageDao.queryOneProb()`

### 4.3 ProbManageServiceImpl.updateProbStatus(Prob)

- **功能描述**：更新技术公告状态（审批）
- **核心算法**：
  1. 更新状态 → `probManageDao.updateProbStatus()`
  2. 根据状态发送邮件通知（已拒绝通知创建者，审批通过通知相关群组）
- **调用的DAO方法**：`probManageDao.updateProbStatus()`, `probManageDao.queryOneProb()`

### 4.4 ProbManageServiceImpl.insertBatchProbRestoreTask(ProbRestore, List\<ProbRestore\>, String)

- **功能描述**：发布修复任务
- **核心算法**：
  1. 技术公告状态改为"5"（解决中）
  2. 插入流程过程记录 → `probManageDao.insertProbRestoreProcess()`
  3. 批量插入子任务 → `probManageDao.insertBatchProbRestoreTask()`
  4. 发送邮件通知
- **调用的DAO方法**：`probManageDao.updateProb()`, `probManageDao.insertProbRestoreProcess()`, `probManageDao.insertBatchProbRestoreTask()`

### 4.5 ProbManageServiceImpl.updateProbRestoreTask(ProbRestore, String, int)

- **功能描述**：更新修复任务
- **核心算法**：
  1. 插入流程过程记录
  2. 更新办理人
  3. 根据操作者角色更新子任务状态
  4. 发送邮件通知相关人员
- **调用的DAO方法**：`probManageDao.insertProbRestoreProcess()`, `probManageDao.updateProbRestoreAssignee()`, `probManageDao.updateProbRestore()`

### 4.6 ProbManageServiceImpl.updateProbSoftVersion(List\<SoftVersion\>, int)

- **功能描述**：更新软件版本信息
- **核心算法**：
  1. 失效原有版本 → `probManageDao.updateInvalidSoftVersion()`
  2. 新增版本 → `probManageDao.saveSoftVersion()`

### 4.7 ProbManageServiceImpl.readLog(int, int)

- **功能描述**：记录阅读日志
- **核心算法**：异步线程插入阅读记录
- **调用的DAO方法**：`probManageDao.insertProbReadLog()`

### 4.8 ProbManageServiceImpl.queryProbStatisticListWithReport(ProbStatistic, DisplayParam, List\<ReportLineData\>)

- **功能描述**：查询技术公告统计报表
- **事务类型**：无事务（方法名 `query*` 前缀不匹配事务代理规则）
- **核心算法**：
  1. 创建临时表
  2. 分页查询统计数据
  3. 生成Echarts图表数据
  4. 删除临时表
- **风险提示**：
  - 使用"创建临时表 → 查询 → 删除临时表"模式，如果中间步骤异常，临时表可能未被清理
  - `statistic_tempTable` 和 `projectTempTable` 为运行时临时表，异常时可能残留

## 5. 数据操作

### 5.1 本模块涉及的数据库表及CRUD操作

| 表名 | CREATE | READ | UPDATE | DELETE |
|------|--------|------|--------|--------|
| prob_main | ✓ saveProb | ✓ queryProbList/queryOneProb | ✓ updateProb/updateProbStatus | ✓ deleteProbInfo |
| prob_restore | ✓ insertBatchProbRestoreTask | ✓ queryProbRestoreList/queryProbRestoreTaskList | ✓ updateProbRestore/updateProbRestoreAssignee | ✓ bacthDeleteProbRestores |
| prob_restore_process | ✓ insertProbRestoreProcess | ✓ queryProbRestoreProcessSize | ✓ updateProbRestore | - |
| prob_restore_weekly | ✓ insertProbTaskWeekly | ✓ queryProbWeekly | - | - |
| prob_soft_version | ✓ saveSoftVersion/batchAddSoftVersion | ✓ querySoftVersionList/checkSoftVersionList | ✓ updateInvalidSoftVersion | - |
| prob_softwares | ✓ (通过saveSoftVersion) | ✓ (通过querySoftVersionList) | - | - |
| prob_read_log | ✓ insertProbReadLog | ✓ queryProbReadLogList | - | - |

### 5.2 数据转换规则

| 转换场景 | 源格式 | 目标格式 | 说明 |
|----------|--------|----------|------|
| 技术公告编号 | 自动生成 | SP.yyyyMMddHHmm | 如SP.202605191430 |
| 技术公告状态 | 数字编码 | 中文 | 0→草稿, 1→待确认, 4→已确认, 5→解决中, 6→已拒绝, 8→待确认, 10→已关闭 |
| 严重级别 | 基础数据编码"32" | 中文 | 通过basicDataService查询 |
| 跟踪类型 | 基础数据编码"30" | 中文 | 通过basicDataService查询 |
| 修复任务状态 | 数字编码 | 中文 | 10→发布接受, 20→办事处返回, 30→闭环申请, 31→闭环 |
| 关联场景类型 | 数字编码 | 中文 | 通过basicDataService查询"relatedSceneType" |
| 规避方案操作类型 | 数字编码 | 中文 | 通过basicDataService查询"mitigationActionType" |
| 解决方案操作类型 | 数字编码 | 中文 | 通过basicDataService查询"solutionActionType" |

## 6. 业务规则

| 规则编号 | 规则描述 | 触发条件 | 执行逻辑 |
|----------|----------|----------|----------|
| PB-001 | 技术公告编号自动生成 | 创建技术公告时 | SP.yyyyMMddHHmm |
| PB-002 | 非管理员更新状态控制 | 非管理员更新技术公告时 | 状态自动改为"8"（待确认） |
| PB-003 | 管理员审批通知 | 管理员审批通过/驳回时 | 通过通知tsc/sp/pdt_ld/xteam群组，驳回只通知创建者 |
| PB-004 | 发布修复任务状态变更 | 发布修复任务时 | 技术公告状态改为"5"（解决中） |
| PB-005 | 修复任务直接闭环 | 发布时restoreStatus=31时 | 直接闭环，不经过流程 |
| PB-006 | 区域权限数据过滤 | 查询修复任务列表时 | 非管理员只能查看权限范围内的数据 |
| PB-007 | 阅读确认 | 查看技术公告详情时 | 自动记录阅读日志（status=0） |
| PB-008 | 邮件通知 | 保存/更新/审批/发布任务时 | 根据角色和操作类型发送不同邮件 |
| PB-009 | 软件版本解析 | 手工录入版本范围时 | 使用SoftVersionStrategy解析版本号范围 |
| PB-010 | 仅搜索公告不发送sp群组 | visibleRange=1时 | 不发送全体用服群组邮件 |

## 7. 配置项

| 配置项 | 配置Key | 默认值 | 说明 |
|--------|---------|--------|------|
| 邮件通知模板 | NOTIFICATION_CODE_PROB | - | 技术公告邮件通知模板编码 |
| 总部二线邮箱 | prob.execute.mail | - | tsc群组邮箱 |
| 全体用服邮箱 | prob.release.mail | - | sp群组邮箱 |
| 维护经理邮箱 | prob.xteam.mail | - | xteam群组邮箱 |
| 产品经理邮箱 | prob.pdt_ld.mail | - | pdt_ld群组邮箱 |
| 普通用户可查看状态 | prob.common.user.status.query | 4,5,6,10 | 普通用户可查看的公告状态列表 |
| 自动检查项目 | subcontract.autocheck.projects | - | 自动检查项目配置 |
| 产品组件过滤 | prob.product.item.filters | - | 产品组件查询过滤配置 |
