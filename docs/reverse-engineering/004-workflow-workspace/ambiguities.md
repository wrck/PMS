# 004-workflow-workspace 域歧义清单(Ambiguities)
> 日期: 2026-07-09
> 歧义总数: 15
> 比对范围: spec.md(004-workflow-workspace 分支) vs spec-draft.md vs 代码(PMS-struts/PMS-activiti)
> 比对类型: 代码 vs 文档 / 代码 vs 代码 / spec 内部 [待澄清] 展开

## AMB-004-01: findRunSelfTaskList 返回 null,FR-TASK-01 实际不可用
- **位置**: FR-TASK-01 查询个人待办任务;`WorkFlowServiceImpl.java:638-645`;`WorkFlowAction.java:137-146`(selftask)
- **现象**: spec 声称 "MUST 按当前登录用户名作为办理人查询运行中任务,扩展展示任务描述信息,返回任务描述列表,支持分页",但服务实现 `findRunSelfTaskList` 方法体 `projectService.findRuTaskProcDescList(...)` 调用被注释,直接 `return null`。连带 `getWorkFlowCountMap`(line 693-694)对 null 调用 `.iterator()` 必然 NPE。
- **候选解释**:
  - (a) 该通用个人待办查询已废弃,实际待办由工作空间标签(WorkSpaceAction.task / FR-WS-02)聚合承担;
  - (b) 代码缺陷,projectService 依赖被移除后未修复。
- **影响面**: FR-TASK-01、SC-001(分页)、selftask() 动作、getWorkFlowCountMap(运行期 NPE)、Acceptance Scenarios。
- **建议决策**: 将 FR-TASK-01 标注为废弃(D),明确"通用个人待办查询不可用,实际待办由 FR-WS-02 业务流程办理标签聚合承担";SC-001 范围相应收敛。

## AMB-004-02: findProcdefDelegateList 返回 null,FR-DELEGATE-01 列表查询亦不可用
- **位置**: FR-DELEGATE-01 查询委派规则列表;`WorkFlowServiceImpl.java:666-670`;`WorkFlowAction.java:214-218`(delegatelist)
- **现象**: spec 仅将 FR-DELEGATE-02/03/04 标注为"核心逻辑被注释/空实现",FR-DELEGATE-01 描述为可用且 Acceptance Scenario 1 称"仅能验证列表查询返回"。但服务层 `findProcdefDelegateList` 同样 `return null`(调用被注释),动作层随后 `displayParam.setTotalcount(pdlist.size())` 必然 NPE。
- **候选解释**:
  - (a) 列表查询也已废弃,spec 漏标;
  - (b) 列表查询走另一入口未发现。
- **影响面**: FR-DELEGATE-01、Acceptance Scenario 1、delegatelist() 运行期 NPE。
- **建议决策**: 将 FR-DELEGATE-01 一并标记为废弃(D),与 02/03/04 一致;修正 Acceptance Scenario 1"仅能验证列表查询"的表述。

## AMB-004-03: submitTask 重定向目标与 spec 描述不符
- **位置**: FR-TASK-03 提交任务输出;`WorkFlowAction.java:166-172`;`struts-sys.xml:889-896`;`/work/redirect.jsp`
- **现象**: spec 称"成功后重定向到工作台业务办理页(WorkSpaceBusinessOrder.action,module 命名空间,livalue=3)"。struts.xml 中 `WorkFlowSubmitTask` 的 `success` 结果确实指向 `WorkSpaceBusinessOrder.action`,但动作代码 `return "redirect"`(非 "success"),映射到 `/work/redirect.jsp`;且动作中 `param.setFormUrl("")` 置空,redirect.jsp 通过 `window.location.href = home + url` 实际跳转到应用根路径,而非工作台业务办理页。
- **候选解释**:
  - (a) 代码 bug,应返回 "success";
  - (b) 设计意图由前端 redirect.jsp 接管,formUrl 应由他处填充;
  - (c) spec 描述为设计意图,代码未实现。
- **影响面**: FR-TASK-03 输出、提交任务后用户落地页。
- **建议决策**: spec 注明实际行为——动作返回 "redirect",经 redirect.jsp 跳转,formUrl 为空时落地应用根;`success→WorkSpaceBusinessOrder.action` 配置存在但未被触发,标注为代码缺陷或废弃配置。

## AMB-004-04: submitTask 自动办理同办理人下一节点行为 spec 未体现
- **位置**: FR-TASK-03 提交任务;`WorkFlowServiceImpl.java:357-382`
- **现象**: 代码含 do-while 循环,完成任务后查询同实例下一任务,若下一任务办理人与当前用户相同,则以"与上环节办理人相同,系统默认办理"为意见、outcome=1 自动完成下一任务,循环直至办理人不同。spec FR-TASK-03 完全未描述此自动连办行为。
- **候选解释**:
  - (a) 隐式业务规则,spec 遗漏;
  - (b) 历史遗留逻辑,实际不应触发。
- **影响面**: FR-TASK-03 流程语义、审批意见记录(自动写入默认意见)、流程跳转正确性。
- **建议决策**: spec 在 FR-TASK-03 补充扩展规则:"当下一任务办理人与当前办理人相同时,系统自动以默认意见完成下一任务,outcome=1"。

## AMB-004-05: submitTask 写入引擎批注,与 FR-COMMENT-01 自定义意见表双路径冲突
- **位置**: FR-COMMENT-01 自定义审批意见;`WorkFlowServiceImpl.java:360`(taskService.addComment)vs `:856-864`(addSelfActComment→workflowDao.insertActComment→fnd_act_hi_comment)
- **现象**: spec FR-COMMENT-01 笼统称 "MUST 提供独立于 BPMN 流程引擎内置批注的自定义审批意见写入与查询"。但实际存在双路径:通用 `submitTask` 调用 `taskService.addComment`(写入引擎 `act_hi_comment`),而 callback/presales/subcontract 等业务流程调用 `addSelfActComment`(写入自定义 `fnd_act_hi_comment`)。`getProcessComments` 读取的也是引擎批注。spec 未区分两条路径,导致"自定义意见独立存储"的表述与 submitTask 实际行为矛盾。
- **候选解释**:
  - (a) submitTask 是通用兜底路径,使用引擎批注;业务专用流程使用自定义意见表;
  - (b) submitTask 应改用自定义表但未改造。
- **影响面**: FR-COMMENT-01、审批意见查询一致性、fnd_act_hi_comment 数据完整性。
- **建议决策**: spec 明确双路径——"通用 submitTask 走引擎 act_hi_comment;callback/presales/subcontract 等业务流程走 fnd_act_hi_comment";FR-COMMENT-01 限定为"业务流程专用自定义意见"。

## AMB-004-06: 流程邮件通知代码被注释,SC-006 与实现不符
- **位置**: SC-006 邮件通知可达;`WorkFlowServiceImpl.java:424-425`;`activiti-context.xml:40-42`
- **现象**: SC-006 称"流程邮件通知通过配置的邮件服务器发送,正常网络条件下送达率 100%"。邮件服务器配置存在(mail.dptech.com:25),但 submitTask 中 `MailUtil.sendmail(...)` 调用被注释。MailUtil 在其他模块(UserManageAction、SubcontractInspectionListener:467)仍被调用。
- **候选解释**:
  - (a) 工作流提交任务的邮件提醒已废弃,改由监听器或其他机制承担;
  - (b) 临时注释未恢复。
- **影响面**: SC-006、NFR-AVAIL-01、流程办理人通知可达性。
- **建议决策**: SC-006 标注"工作流 submitTask 的邮件提醒已注释;邮件服务器配置存在且被 SubcontractInspectionListener 等监听器使用";送达率 100% 的表述降级或限定到监听器路径。

## AMB-004-07: 统一任务监听器 sender 列表被注释,SC-007 第三方同步不生效
- **位置**: SC-007 统一任务监听器;`activiti-context.xml:14-26`
- **现象**: SC-007 称"100% 触发自定义监听逻辑(如同步到第三方系统)"。`unifyTaskListener` bean 存在且注册了 create/assignment/complete/delete/ENTITY_ACTIVATED/ENTITY_SUSPENDED 事件,但 `unifyTaskSenders` 列表(`UnifyTask2SeeyonSender`)被整体注释,监听器无实际 sender,第三方同步(致远 OA)不生效。
- **候选解释**:
  - (a) 第三方同步已下线,仅保留监听器框架;
  - (b) 配置待恢复。
- **影响面**: SC-007、第三方系统集成、任务事件同步。
- **建议决策**: SC-007 标注"监听器框架存在且事件注册完整,但 sender 列表已注释,第三方同步实际不生效";将"100% 触发自定义监听逻辑"限定为"事件捕获 100%,外部同步 0%"。

## AMB-004-08: submitTask 认证身份未清除且未 try-catch,SC-008 描述不完整
- **位置**: SC-008 认证身份记录;`WorkFlowServiceImpl.java:187-188`(submitTask)vs `:139-147`(startProcess)
- **现象**: SC-008 称"启动流程与提交任务前 100% 设置当前认证用户身份,完成后清除",并注明"认证身份设置失败被静默处理"。但 `submitTask` 中 `Authentication.setAuthenticatedUserId(username)` 既未包裹 try-catch(失败将抛异常阻断流程,与"静默处理"矛盾),也未在完成后 `setAuthenticatedUserId(null)` 清除(仅 startProcess 有清除)。
- **候选解释**:
  - (a) 代码缺陷,遗漏清除与异常处理;
  - (b) submitTask 后线程结束自动清理,无需显式清除。
- **影响面**: SC-008、认证上下文泄漏风险、submitTask 异常阻断。
- **建议决策**: SC-008 区分两路径——"startProcess:try-catch 静默 + 完成清除;submitTask:无 try-catch、无清除",并标注 submitTask 路径为代码缺陷。

## AMB-004-09: 非级联删除部署的孤儿实例补偿机制未明(spec [待澄清] 展开)
- **位置**: FR-DEPLOY-03 删除部署;`WorkFlowServiceImpl.java:128`(`deleteDeployment(deploymentId, false)`)
- **现象**: spec 已标 [待澄清]。代码确认使用非级联删除(false),运行中流程实例(act_ru_task/act_ru_execution)将保留但定义已删,成为孤儿。全代码库未发现针对孤儿实例的补偿/清理任务。
- **候选解释**:
  - (a) 无补偿,依赖人工处理或运维脚本;
  - (b) 由 PMS-activiti 独立应用或 DBA 脚本清理。
- **影响面**: FR-DEPLOY-03、数据一致性、生产运维。
- **建议决策**: 维持 [待澄清],建议补充约束:"生产环境删除部署前须先终止运行中实例;非级联删除产生孤儿实例无自动补偿"。

## AMB-004-10: dp_act_proc_desc 表不存在(spec [待澄清] 展开)
- **位置**: 数据契约 3.8 dp_act_proc_desc;`DpActProcDesc.java`
- **现象**: spec 已标 [待澄清]。全库 SQL 无任何对该表的 from/insert/update 引用,Bean 注释声称"对应 dp_act_proc_desc 表的 ID"不成立,实为纯传输对象(DTO),用于聚合多源待办。
- **候选解释**: 规划未落地,DTO 保留聚合用途。
- **影响面**: 数据契约 3.8、DpActProcDesc 语义。
- **建议决策**: 维持结论"表不存在,纯 DTO",标注 D(规划未落地);spec 已正确,移除 [待澄清] 改为确定结论。

## AMB-004-11: 管理员任务管理空实现(spec [待澄清] 展开)
- **位置**: FR-TASK-05 管理员任务管理;`WorkFlowAction.java:189-192`;`struts-sys.xml:898-901`
- **现象**: spec 已标 [待澄清]。`taskmanager()` 方法体为空,仅 `return SUCCESS`,映射到 `/work/taskmanager.jsp`。后端无任何任务查询逻辑。
- **候选解释**:
  - (a) 由前端 taskmanager.jsp 自渲染(可能内嵌 AJAX 调用其他 action);
  - (b) 功能未实现。
- **影响面**: FR-TASK-05。
- **建议决策**: 标注为"后端空实现,若有管理能力由前端 taskmanager.jsp 或其他 action 承担";建议核查 taskmanager.jsp 内容确认。

## AMB-004-12: PMS-activiti 独立应用边界不明(spec [待澄清] 展开)
- **位置**: Edge Cases 独立流程建模/浏览应用;`PMS-activiti/` 模块
- **现象**: spec 已标 [待澄清]。PMS-activiti 模块确实存在,含 VAADIN 主题、Activiti Explorer(task_list/running_process/model_list 等 JSP)、vacation 示例、独立 web.xml 与 spring-activiti.xml,与 PMS-struts 共用 act_* 表但为独立 webapp。
- **候选解释**:
  - (a) 仅用于建模/浏览,生产未部署;
  - (b) 独立部署供流程管理员使用。
- **影响面**: 工作流管理边界、部署架构。
- **建议决策**: 维持 [待澄清],建议确认生产部署清单与该模块的运行状态。

## AMB-004-13: 工作台转包标签角色判定含 roleids.length 双分支,spec 未体现
- **位置**: FR-WS-05 项目转包标签 / SC-009 工作台标签角色控制;`WorkSpaceAction.java:119-148`(prepare)
- **现象**: spec 笼统称"区域/财务角色显示转包"。代码实际逻辑:当用户不具有 ENGINEEMANAGER/LEADER/CALLBACKPER/SERVICEMANAGER 时进入转包判定,内含两分支——`isHasAnyRole(AREA_LEADER, FINANCIAL_STAFF) && roleids.length()==3` 时清空 navTabMap 仅留转包并设 tabIndex=5;`isHasAnyRole(AREA_LEADER, FINANCIAL_STAFF)`(length≠3) 时仅设 tabIndex=5 不清空;否则移除转包标签。roleids.length 条件语义 spec 未体现。
- **候选解释**:
  - (a) roleids.length 区分单角色(length=3)与多角色用户,单角色时强制只显示转包;
  - (b) 历史遗留魔法值。
- **影响面**: FR-WS-05、SC-009 角色裁剪正确性、多角色用户体验。
- **建议决策**: spec 补充 roleids.length==3 条件语义(单角色用户强制转包标签),或标注为实现细节(I)不入契约。

## AMB-004-14: 终止流程仅处理最后一个结束节点,多结束节点场景未覆盖
- **位置**: FR-PROC-01 终止流程;`WorkflowUtil.java:86-129`
- **现象**: spec 称"动态修改流程定义流向,将当前节点连接到结束节点,完成任务后还原流向"。代码遍历 `endActivityImpls`,仅在 `!iterator.hasNext()`(最后一个结束节点)时执行转向与还原,若流程含多个结束节点,只使用最后一个。
- **候选解释**:
  - (a) 默认流程仅单结束节点,设计成立;
  - (b) 多结束节点场景未考虑,存在隐患。
- **影响面**: FR-PROC-01、多结束节点流程终止正确性。
- **建议决策**: spec 补充约束:"假定流程单结束节点;多结束节点取最后一个作为终止目标"。

## AMB-004-15: 通知已读查询含次要排序字段,spec 未体现
- **位置**: FR-WS-06 系统通知与已读;`sql-map-work-config.xml:46-50`
- **现象**: spec 称"已读按查看时间倒序限 100 条"。SQL 实际为 `ORDER BY a.checkTime DESC, b.createTime DESC LIMIT 100`,含 `b.createTime DESC` 次要排序字段,同 checkTime 下按通知创建时间倒序。
- **候选解释**:
  - (a) 实现细节,spec 简化;
  - (b) 次要排序影响确定性,应入契约。
- **影响面**: FR-WS-06 排序确定性、已读列表展示顺序。
- **建议决策**: spec 补充次要排序字段"checkTime DESC, createTime DESC",或标注为实现细节(I)不入契约。

---

## 汇总

| 类别 | 数量 | 编号 |
|---|---|---|
| 代码 vs 文档(功能不可用/不符) | 8 | AMB-004-01,02,03,04,05,06,07,08 |
| 代码 vs 代码(行为/分支未覆盖) | 4 | AMB-004-04,13,14,15 |
| spec 内部 [待澄清] 展开 | 4 | AMB-004-09,10,11,12 |
| 关键歧义(影响功能可用性) | 3 | AMB-004-01,02,03 |

> 注:部分歧义同时归属多类别。关键歧义指直接影响功能可用性或 spec 真实性的条目。
