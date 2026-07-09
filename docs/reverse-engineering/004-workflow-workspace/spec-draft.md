# 004-workflow-workspace 域规格草稿(Spec Reverse-Draft)

> 来源:逆向反推自 PMS-struts + PMS-activiti 代码,日期 2026-07-09
> 域职责:BPMN 流程引擎管理、任务管理、委派、工作空间

---

## 第1章 用户故事

> 端点路径取自 `PMS-struts/config/struts-sys.xml` 第 830-903 行(/work 命名空间)、第 904-909 行(/work/sub 命名空间)及第 404-411、1109 行(/sys 命名空间)。

### 1.1 流程部署管理

- **US-DEPLOY-01** 作为流程管理员,我希望查看已部署的流程包与流程定义列表,以便掌握当前系统中可用的流程。
  证据:`WorkFlowAction.action` → `execute()`(`struts-sys.xml:854-856`;`WorkFlowAction.java:64-71`)。
- **US-DEPLOY-02** 作为流程管理员,我希望上传 BPMN 压缩包(zip)发布新流程,以便业务方使用最新流程定义。
  证据:`WorkFlowNewDeploy.action` → `newdeploy()`(`struts-sys.xml:858-864`;`WorkFlowAction.java:77-86`)。
- **US-DEPLOY-03** 作为流程管理员,我希望删除某个部署(含其流程定义),以便清理废弃流程。
  证据:`WorkFlowDelDeployment.action` → `deldeployment()`(`struts-sys.xml:866-871`;`WorkFlowAction.java:91-96`)。
- **US-DEPLOY-04** 作为流程管理员/办理人,我希望查看流程图图片与当前节点坐标,以便了解流程走向与当前位置。
  证据:`WorkFlowViewImage.action` → `viewimage()`(`struts-sys.xml:873-875`、`908-909`;`WorkFlowAction.java:116-131`);
  `WorkFlowViewCurrentImage.action` → `viewCurrentImage()`(`struts-sys.xml:905-907`;`WorkFlowAction.java:176-182`)。

### 1.2 任务办理

- **US-TASK-01** 作为办理人,我希望查看分配给我的待办任务列表,以便逐项处理。
  证据:`WorkFlowSelfTaskManager.action` → `selftask()`(`struts-sys.xml:877-879`;`WorkFlowAction.java:137-146`)。
- **US-TASK-02** 作为办理人,我希望打开某条任务的业务表单,以便填写审批意见。
  证据:`WorkFlowViewTaskForm.action` → `viewTaskForm()`(`struts-sys.xml:881-883`;`WorkFlowAction.java:152-160`)。
- **US-TASK-03** 作为办理人,我希望提交任务(含审批结果与意见),使流程流转到下一节点。
  证据:`WorkFlowSubmitTask.action` → `submitTask()`(`struts-sys.xml:889-896`;`WorkFlowAction.java:166-172`)。
- **US-TASK-04** 作为办理人,我希望查看已办理任务的历史表单,以便回溯审批记录。
  证据:`WorkFlowHisTaskForm.action` → `hisTaskForm()`(`struts-sys.xml:885-887`;`WorkFlowAction.java:197-205`)。
- **US-TASK-05** 作为系统管理员,我希望查看所有在执行阶段的任务。
  证据:`WorkFlowTaskManager.action` → `taskmanager()`(`struts-sys.xml:898-901`;`WorkFlowAction.java:189-192`)。
  注:该方法当前为空实现(仅返回成功),`[待澄清]` 是否依赖前端 JSP 直接查询。

### 1.3 委派规则管理

- **US-DELEGATE-01** 作为办理人/管理员,我希望查询流程定义委派规则列表。
  证据:`ProcDefDelegateList.action` → `delegatelist()`(`struts-sys.xml:850-852`;`WorkFlowAction.java:214-218`)。
- **US-DELEGATE-02/03/04** 添加/编辑/更新委派规则。
  证据:`AddProcDefDelegate.action`/`EditProcDefDelegate.action`/`UpdateProcDefDelegate.action`(`struts-sys.xml:842-848`、`838-840`、`831-836`;`WorkFlowAction.java:227-276`)。
  **重大发现**:委派功能在服务层与动作层核心逻辑均被注释,实际为废弃功能(详见第3章与第4章歧义点)。

### 1.4 工作空间(待办聚合)

- **US-WS-01** 作为登录用户,我希望进入工作台后默认看到日常项目跟踪待办,以便聚焦当前需要处理的项目。
  证据:`Workspace.action` → `execute()`(`struts-sys.xml:404-406`;`WorkSpaceAction.java:167-178`)。
- **US-WS-02** 作为办理人,我希望在工作台切换到"业务流程办理"标签,查看各类业务审批待办(闭环流程、回访、项目回退、不予跟踪、项目督查、售前)。
  证据:`WorkSpaceAction.task()`(`WorkSpaceAction.java:216-270`)。
- **US-WS-03** 作为办理人,我希望查看"已办理"历史任务。
  证据:`WorkSpaceAction.hisselftask()`(`WorkSpaceAction.java:290-298`)。
- **US-WS-04** 作为技术支持人员,我希望查看技术公告待办。
  证据:`WorkSpaceAction.probTask()`(`WorkSpaceAction.java:306-311`)。
- **US-WS-05** 作为区域负责人/财务人员,我希望查看项目转包待办。
  证据:`WorkSpaceAction.subcontractTask()`(`WorkSpaceAction.java:319-331`)。
- **US-WS-06** 作为用户,我希望查看系统通知消息并标记已读。
  证据:`WorkSpaceAction.notice()`(`WorkSpaceAction.java:194-200`);
  `updateNotifyState.action`(`struts-sys.xml:1109`;`WorkSpaceAction.java:338-342`,代码中标注为已废弃)。

---

## 第2章 功能需求

### FR-DEPLOY-01 查看部署列表与流程定义列表
- **触发**:`WorkFlowAction.action` 入口。
- **输入**:无。
- **处理规则**:并行查询所有部署记录与所有流程定义记录(均按引擎默认排序)。
- **输出**:部署列表(部署ID、名称、部署时间)、流程定义列表(定义ID、Key、名称、版本、部署ID、资源名、流程图资源名)。
- **异常**:无显式异常处理。
- 证据:`WorkFlowAction.java:64-71`;`WorkFlowServiceImpl.java:117-124`。

### FR-DEPLOY-02 发布流程
- **触发**:`WorkFlowNewDeploy.action`。
- **输入**:流程部署文件(File,zip 压缩包)、文件名。
- **处理规则**:将 zip 包作为部署资源,以文件名作为部署显示名,执行部署。
- **输出**:成功后重定向回部署管理页;失败返回错误页并展示堆栈。
- **异常**:文件未找到/格式非法时打印堆栈并设置错误信息,返回错误页。
- 证据:`WorkFlowAction.java:77-86`;`WorkFlowServiceImpl.java:102-114`。

### FR-DEPLOY-03 删除部署
- **触发**:`WorkFlowDelDeployment.action`。
- **输入**:部署ID(`param.deploymentId`)。
- **处理规则**:当部署ID非空时,删除该部署。删除采用非级联模式(不删除相关运行时实例)。
- **输出**:成功后重定向回部署管理页。
- **异常**:无显式异常处理。
- 证据:`WorkFlowAction.java:91-96`;`WorkFlowServiceImpl.java:127-129`。
- `[待澄清]` 非级联删除会导致运行中的流程实例成为孤儿,是否有补偿机制。

### FR-DEPLOY-04 查看流程图
- **触发**:`WorkFlowViewImage.action`(静态图)/`WorkFlowViewCurrentImage.action`(当前节点高亮)。
- **输入**:部署ID、图片资源名 / 任务ID。
- **处理规则**:静态图——按部署ID+资源名读取字节流并写入响应输出。当前图——按任务ID查流程定义,再查当前活动节点坐标(x、y、width、height)。
- **输出**:PNG 图片字节流 / 流程定义对象 + 坐标 Map。
- 证据:`WorkFlowAction.java:116-131`、`176-182`;`WorkFlowServiceImpl.java:132-134`。

### FR-TASK-01 查询个人待办任务
- **触发**:`WorkFlowSelfTaskManager.action`。
- **输入**:当前登录用户名、分页参数。
- **处理规则**:按当前用户名作为办理人查询运行中任务,扩展展示任务描述信息(项目编码、项目名、流程类型等)。
- **输出**:任务描述列表(DpActProcDesc)。
- 证据:`WorkFlowAction.java:137-146`。

### FR-TASK-02 打开任务表单
- **触发**:`WorkFlowViewTaskForm.action`。
- **输入**:任务ID、查看标记(`canSee`)、流程类型(`procType`)。
- **处理规则**:按任务ID取表单元数据(formKey),按任务ID查流程实例业务键并拆分出业务对象ID;拼装表单 URL = `formKey?param.objId={objId}&param.taskId={taskId}&param.canSee={canSee}&dpActProcDesc.procType={procType}`。
- **输出**:跳转 URL,前端重定向。
- 证据:`WorkFlowAction.java:152-160`;`WorkFlowServiceImpl.java:163-178`。
- **业务键约定**:业务键格式为 `{classType}.{objId}`,按 "." 拆分(证据:`WorkFlowServiceImpl.java:197-200`)。

### FR-TASK-03 提交任务
- **触发**:`WorkFlowSubmitTask.action`。
- **输入**:任务ID、审批结果(outcome)、意见(comment)、是否同客户、是否需领导审批等流程变量。
- **处理规则**:设置当前认证用户;按任务ID查流程实例,从业务键解析 `classType` 与 `objId`;根据 `outcome` 与 `classType` 执行业务侧状态更新(驳回场景,各分支当前多为注释代码);完成任务并写入流程变量 `outcome`、`issamecustomer`、`needleader`。
- **输出**:成功后重定向到 `WorkSpaceBusinessOrder.action`(module 命名空间, livalue=3)。
- **异常**:跳转 redirect 视图。
- 证据:`WorkFlowAction.java:166-172`;`WorkFlowServiceImpl.java:181-260+`。
- `[待澄清]` 驳回业务状态更新分支被大量注释,实际驳回是否生效。

### FR-TASK-04 查看历史任务表单
- **触发**:`WorkFlowHisTaskForm.action`。
- **输入**:流程实例ID(`instId`)。
- **处理规则**:按实例ID查历史业务对象ID;按实例ID取首条任务的 formKey;拼装 URL = `formKey?param.objId={objId}&param.instId={instId}&param.flag=1&param.showflag=1`。
- **输出**:跳转 URL。
- 证据:`WorkFlowAction.java:197-205`。

### FR-TASK-05 管理员任务管理
- **触发**:`WorkFlowTaskManager.action`。
- **处理规则**:当前为空实现,仅返回成功视图。
- 证据:`WorkFlowAction.java:189-192`。
- `[待澄清]` 实际任务管理能力是否完全由前端或其他动作承担。

### FR-DELEGATE-01~04 委派规则管理(列表/新增/编辑/更新)
- **触发**:`ProcDefDelegateList`/`AddProcDefDelegate`/`EditProcDefDelegate`/`UpdateProcDefDelegate`。
- **处理规则**:
  - 列表:按 `handleUsername`(设置人)过滤查询委派规则,关联用户表展示真实姓名。
  - 新增:动作层核心逻辑被注释(仅返回成功)。
  - 编辑:动作层核心逻辑被注释(仅返回成功)。
  - 更新:调用服务层 `updateProcdefDelegate`,但服务层方法体被注释(空实现)。
- **结论**:委派功能在应用层不可用,数据表与 SQL 仍保留但实际为废弃功能。
- 证据:`WorkFlowAction.java:214-276`(动作层);`WorkFlowServiceImpl.java:660-681`(服务层全注释)。

### FR-WS-01 工作台默认视图(日常项目跟踪)
- **触发**:`Workspace.action`(登录后默认)。
- **输入**:当前用户、筛选条件(项目名、办事处、项目经理、服务经理)、分页。
- **处理规则**:根据 `tabIndex` 派发;默认查询项目经理待办任务(项目状态在 30/31/32、有效、计划偏离 7 天内、项目经理非空)。`prepare()` 中根据角色裁剪可见标签:回访角色才显示"已办"标签;技术公告角色只显示技术公告标签;区域/财务角色显示转包标签。
- **输出**:日常项目跟踪任务列表。
- 证据:`WorkSpaceAction.java:62-178`;`sql-map-work-config.xml:68-95`。

### FR-WS-02 业务流程办理标签
- **触发**:`WorkSpaceAction.task()`。
- **输入**:流程Key过滤(`procKey`,空则查全部)。
- **处理规则**:按 `procKey` 聚合多类待办——闭环流程(`CL_PROCESS_KEY`)、回访(`CallBack`)、项目回退(`ProjectBack`)、不予跟踪(`ProjectTrack`)、项目督查(`ProjectSupervision`,仅工程经理角色)、售前(`Presales`)。
- **输出**:聚合后的 DpActProcDesc 列表。
- 证据:`WorkSpaceAction.java:216-270`;`sql-map-work-config.xml:166-316`、`484-500`。

### FR-WS-03 已办理历史标签
- **触发**:`WorkSpaceAction.hisselftask()`。
- **处理规则**:查询当前用户已办理任务(项目回访评价 + 回访流程已办),按项目名/办事处/客户筛选分页。
- **输出**:历史任务列表。
- 证据:`WorkSpaceAction.java:290-298`;`sql-map-work-config.xml:528-587`。

### FR-WS-04 技术公告标签
- **触发**:`WorkSpaceAction.probTask()`。
- **处理规则**:按角色(管理员/支持/研发)查询技术公告待办,过滤已关闭/已解决状态。
- **输出**:技术公告列表(ProbParam)。
- 证据:`WorkSpaceAction.java:306-311`;`sql-map-work-config.xml:403-482`。

### FR-WS-05 项目转包标签
- **触发**:`WorkSpaceAction.subcontractTask()`。
- **处理规则**:查询项目转包任务列表,同时加载公司列表(status=1)。
- **输出**:转包任务列表 + 公司列表。
- 证据:`WorkSpaceAction.java:319-331`。

### FR-WS-06 系统通知与已读
- **触发**:`WorkSpaceAction.notice()` / `updateNotifyState.action`。
- **处理规则**:查询当前用户通知(未读按创建时间倒序 + 已读按查看时间倒序限 100 条);更新通知状态为已读(checkTime=now)。
- **输出**:通知列表。
- 证据:`WorkSpaceAction.java:194-200`、`338-342`;`sql-map-work-config.xml:6-57`。
- 注:`updateNotifyState` 在代码中标注为已废弃。

### FR-COMMENT-01 自定义审批意见
- 本域提供自定义审批意见写入与查询(独立于引擎内置批注),记录办理人、办理时间、结果、意见、下一办理人。
- 证据:`sql-map-activity-config.xml:22-68`;`WorkflowDao.java:24-48`。

### FR-PROC-01 终止流程
- 提供按任务ID终止流程的能力:动态修改流程定义流向,将当前节点连接到结束节点,完成任务后还原流向;支持多实例节点完成条件表达式处理。
- 证据:`WorkflowUtil.java:39-132`。

---

## 第3章 数据契约【最关键】

> 分级说明:**C**=契约字段(业务必须依赖);**I**=内部字段(框架实现细节,不应直接依赖);**D**=废弃字段/表(代码保留但功能不可用)。

### 表 dp_act_procdef_delegate(流程定义委派规则)
> 证据:`sql-map-admin-config.xml:626-670`;Bean `ProcdefDelegate.java`。
> **状态说明**:表结构与 SQL 完整存在,但应用层 CRUD 方法(`insertProcdefDelegate`/`findProcdefDelegateList`/`findProcdefDelegateById`/`updateProcdefDelegate`)在 `WorkFlowServiceImpl.java:660-681` 全部被注释,动作层 `delegateadd`/`delegateedit` 核心逻辑也被注释。功能实际不可用,标记为废弃(D)。

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键,自增 | 唯一标识 | D |
| owner | varchar | 否 | 任务原所属人用户名 | 关联用户表 | D |
| assignee | varchar | 否 | 任务接收人用户名 | 关联用户表;查询有效规则时需 status=1 且当前时间在 startTime/endTime 区间内 | D |
| handleUsername | varchar | 否 | 委派设置人用户名 | 列表与更新按此字段过滤 | D |
| handleTime | datetime | 是 | 设置时间;新增/更新时置为 now() | — | D |
| startTime | datetime | 是 | 委派生效开始时间 | 可空,空表示无下界 | D |
| endTime | datetime | 是 | 委派生效结束时间 | 可空,空表示无上界 | D |
| procdefId | varchar | 是 | 流程定义Key(支持模糊匹配,concat '%'+key+'%') | — | D |
| status | int | 否 | 状态(1=有效) | 查询有效规则要求 status=1 | D |
| cause | varchar | 是 | 委派原因 | — | D |

### 表 dp_act_proc_type(流程类型字典)
> 证据:`sql-map-admin-config.xml:672-681`;Bean `DpActProcType.java`。
> 用于将流程定义Key映射到业务流程类型,常量定义在 `DpActProcType.java:13-21`。

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 唯一;业务侧常量:1=商务提前报备、2=项目总结、3=订单申报、4=重大项目、5=借货、7=借转退、8=借货转项目、9=丢单总结、10=项目失效 | C |
| desc | varchar | 否 | 流程类型描述 | — | C |
| procDefKey | varchar | 否 | 流程定义Key | 关联引擎流程定义 | C |

### 表 fnd_act_hi_comment(自定义审批意见)
> 证据:`sql-map-activity-config.xml:22-68`;`WorkflowDao.java:24-48`。
> 本域管理的自定义意见表,独立于引擎内置批注表 act_hi_comment。

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键,自增(insert 后回填 last_insert_id) | 唯一 | C |
| objId | int | 否 | 业务对象ID(来自业务键拆分) | 关联各业务表主键 | C |
| procdefKey | varchar | 否 | 流程定义Key | 关联 dp_act_proc_type.procDefKey / act_re_procdef.KEY_ | C |
| taskKey | varchar | 是 | 任务节点定义Key(扩展字段,部分重载方法写入) | — | C |
| taskId | varchar | 是 | 引擎任务ID | 关联 act_ru_task.ID_ / act_hi_taskinst.ID_;可先插入后更新 | C |
| instId | varchar | 是 | 流程实例ID | 关联 act_ru_execution.PROC_INST_ID_ | C |
| assignee | varchar | 否 | 办理人用户名 | 关联用户表;查询历史按此字段过滤当前用户 | C |
| assigneeTime | datetime | 否 | 办理时间 | 查询按此字段升序 | C |
| result | int | 是 | 审批结果(关联基础数据 dataTypeCode=26) | — | C |
| message | varchar | 是 | 审批意见文本 | — | C |
| nextAssignee | varchar | 是 | 下一办理人用户名(扩展) | — | C |
| nextAssigneeName | varchar | 是 | 下一办理人姓名(扩展) | — | C |

### 表 pm_project_notification(项目通知)
> 证据:`sql-map-work-config.xml:6-52`、`143-164`。工作空间"通知"标签读取。

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 被状态表引用为 notifyId | C |
| notifySubject | varchar | 是 | 通知主题 | — | C |
| notifyContent | varchar | 是 | 通知内容 | — | C |
| projectId | int | 是 | 关联项目ID | 关联 pm_project_header.projectId | C |
| notifyObject | varchar | 否 | 通知接收人用户名 | 未读查询按此字段过滤 | C |
| createTime | datetime | 是 | 创建时间 | 未读按此字段倒序 | C |
| createBy | varchar | 是 | 创建人用户名 | 展示时拼接 username-realName | C |

### 表 pm_project_notification_state(项目通知状态)
> 证据:`sql-map-work-config.xml:6-57`。

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 作为 notifyStateId 返回 | C |
| notifyId | int | 否 | 关联 pm_project_notification.id | — | C |
| notifyObject | varchar | 否 | 接收人用户名 | — | C |
| notifyState | int | 否 | 状态(0=未读,1=已读) | 已读按 checkTime 倒序,限 100 条 | C |
| checkTime | datetime | 是 | 查看时间;标记已读时置为 now() | — | C |

### 表 pm_project_supervision(项目督查)
> 证据:`sql-map-work-config.xml:484-500`。工作空间"项目督查"待办读取。

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| projectId | int | 否 | 关联项目ID | — | C |
| projectCode | varchar | 是 | 项目编码 | — | C |
| projectName | varchar | 是 | 项目名称 | — | C |
| officeCode | varchar | 是 | 办事处编码 | 关联 fnd_department.departmentNum | C |
| createTime | datetime | 是 | 创建时间 | — | C |
| isdelete | bool | 否 | 软删除标记 | 查询过滤 isdelete=FALSE | C |
| state | bool | 否 | 督查状态(未完成=FALSE) | 查询过滤 state=FALSE | C |
| (其他字段) | — | — | 表中其他字段未在本域查询中使用 | — | I |

### Activiti 引擎表(act_*)(框架内部表,仅记录存在,字段为内部实现)
> 以下表由 BPMN 流程引擎(代码中实现为 Activiti)内部管理,本域通过引擎 API 间接读写,不应直接依赖其字段结构。`databaseSchemaUpdate=true` 表示引擎启动时自动建表/升级(证据:`activiti-context.xml:38`)。

- **act_re_deployment**(部署记录):部署ID、名称、部署时间。由 `repositoryService.createDeployment/deleteDeployment` 管理。
- **act_re_procdef**(流程定义):定义ID、Key、名称、版本、部署ID、资源名、流程图资源名。`Procdef.java` 映射其字段。
- **act_ge_bytearray**(二进制资源):存储 BPMN XML 与流程图 PNG。
- **act_ru_task**(运行时任务):任务ID、名称、办理人、流程定义ID、流程实例ID、创建时间。本域待办查询核心来源。
- **act_ru_execution**(运行时执行实例):含流程实例与业务键。
- **act_ru_variable**(运行时变量):本域直接读写其 ID_/PROC_INST_ID_/NAME_/TYPE_/TEXT_ 字段以更新流程变量(证据:`sql-map-work-config.xml:502-526`)。
- **act_ru_identitylink**(运行时身份链接):候选用户/组。
- **act_hi_taskinst**(历史任务):任务ID、办理人、起止时间。
- **act_hi_procinst**(历史流程实例):实例ID、业务键、起止时间。
- **act_hi_comment**(引擎内置批注):本域另设 fnd_act_hi_comment 替代。
- **act_hi_attachment**(历史附件)。
- **act_id_**(身份表族:user/group/membership):引擎内置用户身份,本域实际使用业务侧用户表(fnd_user_info/user)。

### 表 dp_act_proc_desc(流程描述)—— `[待澄清]` 是否存在
> Bean `DpActProcDesc.java` 注释声称"对应 dp_act_proc_desc 表的 ID",但全库 SQL 中无任何对该表的 from/insert/update 引用。**结论**:该表实际不存在,DpActProcDesc 为纯传输对象(DTO),用于聚合多源待办任务展示。标注为 **D**(规划未落地)。

### 表 workspace —— 不存在
> 工作空间(WORKSPACE)并非独立表,而是基于 act_ru_task 关联各业务表(pm_cl_callback、pm_presales_project_header、pm_project_header、pm_project_supervision 等)的聚合视图。

---

## 第4章 非功能需求

### NFR-PERF-01 任务列表分页
- 日常项目跟踪、通知、已办历史等列表均支持分页(`displayParam.offset`/`pagesize`),通过 SQL `limit #offset#, #pagesize#` 实现。
- 证据:`sql-map-work-config.xml:92-94`、`161-163`、`576-578`、`584-586`。

### NFR-PERF-02 待办查询性能
- 待办查询大量使用多表 LEFT JOIN(项目头+状态+基础数据+成员+部门),且未读通知使用 UNION ALL。`[待澄清]` 数据量增长后的性能保障(是否有索引)。
- 证据:`sql-map-work-config.xml:68-95`、`15-52`。

### NFR-CONSISTENCY-01 引擎与业务统一事务
- 流程引擎与业务数据共用同一数据源与事务管理器,保证流程操作与业务状态更新在同一事务内。
- 证据:`activiti-context.xml:34-36`(dataSource 与 transactionManager 注入引擎配置)。

### NFR-CONSISTENCY-02 引擎自动建表
- 引擎启动时 `databaseSchemaUpdate=true`,自动创建/升级 act_* 表结构。生产环境存在结构漂移风险。
- 证据:`activiti-context.xml:38`。

### NFR-CONSISTENCY-03 业务键解析约定
- 流程实例业务键采用 `{classType}.{objId}` 格式,提交任务时按 "." 拆分定位业务对象。该约定为跨域隐式契约,破坏将导致任务提交失败。
- 证据:`WorkFlowServiceImpl.java:197-200`、`168-177`。

### NFR-AVAIL-01 邮件服务器配置
- 引擎配置邮件服务器(mail.dptech.com:25),用于流程邮件通知。
- 证据:`activiti-context.xml:41-42`。

### NFR-EXT-01 统一任务监听器
- 通过全局 BPMN 解析器注入统一任务监听器,在任务 create/assignment/complete/delete/ENTITY_ACTIVATED/ENTITY_SUSPENDED 事件触发自定义逻辑(如同步到第三方系统)。
- 证据:`activiti-context.xml:44-96`;监听器实现 `com.dp.plat.plus.unifytask.listener.UnifyTaskListener`。
- 项目验收监听器(`SubcontractInspectionListener`)同样挂载于所有用户任务。
- 证据:`activiti-context.xml:29-30`、`73-91`。

### NFR-SEC-01 认证用户上下文
- 启动流程与提交任务前,从用户上下文取用户名设置引擎认证身份,完成后清除。失败时静默吞异常(不阻断流程)。
- 证据:`WorkFlowServiceImpl.java:139-147`、`187-188`。
- `[待澄清]` 静默吞异常是否会导致审计缺失。

### NFR-SEC-02 工作台标签可见性按角色控制
- 工作台标签按角色裁剪:回访角色(ROLE_CALLBACKPER)才显示"已办";技术公告角色(ROLE_PROB_ADMIN/SUPPORTER/RD 且角色ID长度=4)仅显示技术公告;区域负责人(ROLE_AREA_LEADER)/财务(ROLE_FINANCIAL_STAFF)才显示转包。
- 证据:`WorkSpaceAction.java:62-152`。

---

## 附:关键歧义点(最多5条)

1. **委派功能废弃但表与SQL保留**:`dp_act_procdef_delegate` 表与完整 SQL 存在,但 `WorkFlowServiceImpl` 中四个委派方法(660-681行)全部注释,`WorkFlowAction.delegateadd/delegateedit` 核心逻辑也注释。需确认是历史遗留还是计划恢复。`[待澄清]`

2. **dp_act_proc_desc 表疑似未落地**:Bean 注释声称对应此表,但全库无任何 SQL 引用,实为纯 DTO。需确认是否曾规划独立任务描述表。`[待澄清]`

3. **taskmanager() 空实现**:`WorkFlowTaskManager.action` 的动作方法体为空(仅返回成功),管理员任务管理能力是否由前端 JSP 或其他动作承担不明。`[待澄清]`

4. **删除部署非级联**:`delDeployment` 使用非级联删除(`deleteDeployment(id, false)`),运行中流程实例可能成为孤儿,是否有补偿或限制策略不明。`[待澄清]`

5. **PMS-activiti 模块定位**:该模块是独立的 Activiti Explorer/Modeler(VAADIN + Angular 前端,含独立 spring-activiti.xml、webapp、editor-app),与 PMS-struts 共用 act_* 表但为独立应用。其与主系统的工作流管理边界、是否在生产部署未明。`[待澄清]`
