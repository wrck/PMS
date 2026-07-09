# 002-presales-product 域规格草稿(Spec Reverse-Draft)

> 来源:逆向反推自 PMS-struts prob 包与 presales 配置,日期 2026-07-09
> 域职责:售前测试项目、技术公告(Prob)、产品组件、软件/设备版本匹配
> 证据基准:struts-sys.xml 第 217-296 行、sql-map-prob-config.xml、sql-map-presales-config.xml、prob 包源码、Presales.bpmn

---

## 第1章 用户故事

> 端点证据:struts-sys.xml:217-296(PresalesAction/ProbManageAction 路由配置)

### 1.1 售前测试项目

- **US-PRESALES-01 列表查询** — 作为工程管理部/服务经理/项目经理,我希望按状态、办事处、人员、时间筛选售前测试项目列表,以便跟踪项目进展。证据:`presales_list.action` → `PresalesAction.list()`(PresalesAction.java:160);SQL:`query_presales_list`(sql-map-presales-config.xml:208)。
- **US-PRESALES-02 申请/重申请** — 作为工程管理人员,我希望发起或重新发起售前测试流程,以便启动审批。证据:`presales_apply.action` → `PresalesAction.apply()`(PresalesAction.java:240)。
- **US-PRESALES-03 查看详情** — 作为项目相关方,我希望查看售前项目主信息、产品线、评论、计划任务。证据:`presales_read.action` → `PresalesAction.read()`(PresalesAction.java:262)。
- **US-PRESALES-04 审批路由** — 作为系统,我希望按当前流程节点跳转到对应审批页(服务经理/项目经理/工程管理部回访)。证据:`presales_aduit.action` → `PresalesAction.aduit()`(PresalesAction.java:281);节点判定依据 `taskDefKey`(usertask1/2/3/4、serviceApprove)。
- **US-PRESALES-05 服务经理审批** — 作为服务经理,我希望办理"指定项目经理"任务并提交审批结果。证据:`presales_smaduit.action` → `PresalesAction.smaduit()`(PresalesAction.java:302)。
- **US-PRESALES-06 项目经理审批** — 作为项目经理,我希望办理"跟踪项目"任务、上传交付件、更新计划完成时间。证据:`presales_pmaduit.action` → `PresalesAction.pmaduit()`(PresalesAction.java:330);`presales_updateTask.action` → `updateTask()`(PresalesAction.java:388)。
- **US-PRESALES-07 工程管理部回访** — 作为工程管理部回访人员,我希望办理回访任务并填写回访问卷。证据:`presales_emaduit.action` → `PresalesAction.emaduit()`(PresalesAction.java:406);`presales_callback.action` → `callback()`(PresalesAction.java:444)。
- **US-PRESALES-08 发货/借转销/RMA 查询** — 作为项目相关方,我希望按项目编码查询发货明细、借转销、核销信息。证据:`presales_shipmentInfo/lend2SaleInfo/lend2RmaInfo/tempAuthInfo`(PresalesAction.java:470/480/490/500)。
- **US-PRESALES-09 交付件管理** — 作为项目相关方,我希望上传、删除、更新交付件。证据:`presales_upload/deleteDeliverById/updateDeliverById/updateConfirmFiles`(PresalesAction.java:577/612/621/832)。
- **US-PRESALES-10 终止闭环** — 作为管理员,我希望批量终止流程并闭环。证据:`presales_terminate2Close.action` → `terminate2Close()`(PresalesAction.java:510)。
- **US-PRESALES-11 导出** — 作为项目相关方,我希望按项目/设备/回访维度导出售前项目数据。证据:`presales_exportPresales.action` → `exportPresales()`(PresalesAction.java:845);SQL:`queryPresalesExportData`(sql-map-presales-config.xml:559)。

### 1.2 技术公告(Prob)

- **US-PROB-01 列表查询** — 作为技术公告相关方,我希望按编号、主题、状态、跟踪人、影响版本、关联场景等筛选公告列表。证据:`prob_list.action` → `ProbManageAction.list()`(ProbManageAction.java:205);SQL:`query_prob_list`(sql-map-prob-config.xml:171)。
- **US-PROB-02 创建/编辑** — 作为技术公告员,我希望创建、编辑公告主题、描述、解决方案、影响版本、产品型号。证据:`prob_input.action` → `input()`(ProbManageAction.java:237);`prob_edit.action` → `edit()`(ProbManageAction.java:310)。
- **US-PROB-03 保存/更新** — 作为技术公告员,我希望保存公告(含附件、软件版本、产品型号)。证据:`prob_save.action` → `save()`(ProbManageAction.java:638);`prob_update.action` → `update()`(ProbManageAction.java:676)。
- **US-PROB-04 审批** — 作为技术公告技术支持人员,我希望审批公告并更新软件版本。证据:`prob_audit.action` → `audit()`(ProbManageAction.java:708)。
- **US-PROB-05 删除** — 作为技术公告员,我希望删除(失效)公告。证据:`prob_delete.action` → `delete()`(ProbManageAction.java:286);SQL:`delete_prob_info` 软删除(effectiveTo=NOW(),sql-map-prob-config.xml:1031)。
- **US-PROB-06 导出** — 作为技术公告相关方,我希望导出公告列表为 Excel。证据:`prob_export.action` → `export()`(ProbManageAction.java:725)。
- **US-PROB-07 阅读确认/记录** — 作为公告阅读人,我希望确认已读并查看阅读记录。证据:`prob_readSure.action` → `readSure()`(ProbManageAction.java:1078);`prob_readLog.action` → `readLog()`(ProbManageAction.java:1096)。
- **US-PROB-08 检索受影响项目** — 作为技术公告相关方,我希望检索受公告影响的设备/项目集合,以便生成修复任务。证据:`prob_checkProject.action` → `checkProject()`(ProbManageAction.java:408);SQL:`query_prob_restore_list`(sql-map-prob-config.xml:729)。
- **US-PROB-09 发布修复任务** — 作为技术公告员,我希望对受影响设备批量发布修复任务并指派办理人。证据:`prob_releaseTask.action` → `releaseTask()`(ProbManageAction.java:478);SQL:`insert_batch_probRestore_task_list`(sql-map-prob-config.xml:736)。
- **US-PROB-10 个人任务管理** — 作为办理人,我希望管理指派给我的修复任务并更新状态。证据:`prob_managePrivateTask/updatePrivateTask.action`(ProbManageAction.java:502/541)。
- **US-PROB-11 管理员任务管理** — 作为管理员/技术支持,我希望管理全部修复任务(待闭环/已闭环/办事处返回)。证据:`prob_manageAllTask/updateRestoreTask.action`(ProbManageAction.java:584/620)。
- **US-PROB-12 周报上传** — 作为办理人,我希望上传修复进展周报附件。证据:`prob_weeklyUpload.action` → `weeklyUpload()`(ProbManageAction.java:559)。
- **US-PROB-13 批量删除子任务** — 作为管理员,我希望批量删除修复子任务。证据:`prob_bacthDeleteProbRestores.action` → `bacthDeleteProbRestores()`(ProbManageAction.java:296)。
- **US-PROB-14 统计** — 作为技术公告相关方,我希望按时间、办事处、项目统计公告影响及修复情况。证据:`prob_statistics.action` → `statistics()`(ProbManageAction.java:978)。
- **US-PROB-15 受影响项目软件版本** — 作为技术公告相关方,我希望查看受影响项目的软件版本明细。证据:`prob_affectedProjectSoftVersion.action` → `affectedProjectSoftVersion()`(ProbManageAction.java:1036)。

### 1.3 软件版本解析与匹配

- **US-VER-01 软件版本检索** — 作为技术公告员,我希望按 conp/cpld/boot/pcb(精确/范围/正则)检索已存在的软件版本。证据:`prob_toCheckSoftVersion.action` → `toCheckSoftVersion()`(ProbManageAction.java:813);SQL:`check_soft_version_list`(sql-map-prob-config.xml:389)。
- **US-VER-02 解析手工录入版本范围** — 作为技术公告员,我希望将手工录入的版本描述(如"神州五号 C012D005P09PATCH07-C012D006P01PATCH09 之前版本")解析为结构化版本范围(markStart/markEnd)。证据:`prob_parserSoftVersion.action` → `parserSoftVersion()`(ProbManageAction.java:874);规则定义:SoftVersionStrategy.java:18(版本正则)、SoftVersionStrategy.java:26(范围正则)。
- **US-VER-03 批量重解析历史版本** — 作为技术公告技术支持,我希望对历史手工录入版本重新解析并分组保存。证据:`prob_parserOldSoftVersion.action` → `parserOldSoftVersion()`(ProbManageAction.java:900)。
- **US-VER-04 批量导入软件版本** — 作为技术公告技术支持,我希望通过 Excel 批量导入软件版本字典。证据:`prob_importSoftVersion.action` → `importSoftVersion()`(ProbManageAction.java:787)。

### 1.4 产品与产品组件

- **US-PROD-01 产品组件列表/新建/保存/导入** — 作为产品组件管理员,我希望维护产品组件树(分组/名称/版本/父节点)并支持 Excel 导入。证据:`component_list/input/save/import.action`(ProbManageAction.java:1268/1300/1314/1333)。
- **US-PROBPROD-01 技术公告产品列表/新建/保存/导入** — 作为技术公告员,我希望维护技术公告关联的产品型号(itemCode/itemModel)并支持 Excel 导入。证据:`probProduct_list/input/save/import.action`(ProbManageAction.java:1182/1214/1228/1243)。
- **US-PRODITEM-01 产品组件项检索** — 作为技术公告员,我希望按 itemCode/itemModel/描述检索产品项。证据:`component_listProductItem.action` → `listProductItem()`(ProbManageAction.java:1119)。

---

## 第2章 功能需求

### 2.1 售前测试项目管理

#### FR-PRESALES-01 售前项目列表查询
- **触发条件**:用户进入售前项目列表页。
- **输入**:项目编码(presalesCode,匹配 presalesCode 或 projectCode)、项目名称(模糊)、项目类型、办事处(单选/多选 officeCodeList)、项目状态(单值 projectState 或多值 projectStates,逗号分隔)、服务经理、项目经理、是否借转销(hasTransfer)、是否未核销(hasRma)、时间类型(applyTime申请时间/endTime结束时间/testCompleteTime测试完成时间/callbackQuesnarieTime回访问卷时间/callbackCompleteTime回访完成时间)+ 起止时间、分页参数。
- **处理规则**:
  1. 工程管理人员/售前专员默认只看待创建状态;其他角色默认看 `30,31,32,33` 状态(PresalesAction.java:170-175)。
  2. 项目编码同时匹配 `presalesCode` 与 `projectCode`(sql-map-presales-config.xml:241-243)。
  3. 服务经理条件匹配 `memberRole='20'` 的成员或 `memberRole='30'` 的项目经理(sql-map-presales-config.xml:235-239)。
  4. 多状态匹配使用 `FIND_IN_SET`(sql-map-presales-config.xml:257-259)。
  5. 当时间类型为 testCompleteTime/callbackQuesnarieTime/callbackCompleteTime 时,需关联活动历史任务表并 `GROUP BY presalesId` 去重(sql-map-presales-config.xml:276-290)。
  6. 默认按 `applyTime DESC` 排序。
- **输出**:项目列表(含项目编码、名称、状态、类型、办事处、服务经理、项目经理、当前任务办理人等)。
- **异常**:权限不足返回错误页;查询异常返回错误页。

#### FR-PRESALES-02 售前流程申请/重申请
- **触发条件**:工程管理人员提交申请。
- **输入**:项目主信息(presalesCode/projectName/projectType/officeCode/salesman/productManager 等)、产品线列表、评论参数。
- **处理规则**:
  1. 无 taskId 时启动新流程;有 taskId 时提交重新申请(PresalesAction.java:242-246)。
  2. 启动流程后写入 `pm_presales_project_header`,并根据 `fnd_basic_data` 初始化计划任务(`insert_presales_tasks`,taskTypeCode 来自基础数据,sql-map-presales-config.xml:381-388)。
- **输出**:跳转至列表页。
- **异常**:启动失败返回错误页并提示。

#### FR-PRESALES-03 售前审批流转(基于 BPMN 流程)
- **触发条件**:流程到达各审批节点。
- **处理规则**(流程定义见 Presales.bpmn):
  1. **usertask1 工程管理部指派服务经理**:办理人 `${applyBy}`;分支 result==1 → usertask2;result==-1 → 直接闭环(endevent1);result==2 → usertask3(同时指定服务和项目经理)(Presales.bpmn:5-9,48-56)。
  2. **usertask2 服务经理指定项目经理**:办理人 `${sm}`;result==1 → usertask3;result==-1 → 返回 usertask1(Presales.bpmn:9-22)。
  3. **usertask3 项目经理跟踪项目**:办理人 `${pm}`;result==1 → usertask4;result==-1 → 返回 usertask2(Presales.bpmn:15-36)。
  4. **usertask4 工程管理部回访销售**:办理人 `${em}`,候选组 `${emRole}`;result==1 → 闭环;result==-1 → 驳回 usertask3(Presales.bpmn:25-33)。
  5. 代码中存在 `serviceApprove` 节点键(用于服务经理审批时长统计,sql-map-presales-config.xml:806),与 usertask2 关联 [待澄清]。
- **输出**:流程实例推进,任务办理人变更。
- **异常**:驳回后回退到上一节点;终止流程批量闭环(terminate2Close)。

#### FR-PRESALES-04 项目计划任务管理
- **触发条件**:项目经理更新任务完成时间或交付件。
- **输入**:presalesTaskId、taskFinshedTime(完成时间,不得晚于当前时间)、remark、fileIds。
- **处理规则**:
  1. 完成时间若晚于当前时间,则修正为当前时间(PresalesAction.java:391-393)。
  2. 交付件 ID 追加到 `deliverFileIds`(逗号拼接,sql-map-presales-config.xml:412-416)。
  3. 删除交付件时按逗号边界精确移除 fileId(sql-map-presales-config.xml:432-456)。
  4. 现场测试确认单 `confirmFileIds` 同样支持追加/删除(sql-map-presales-config.xml:425-477)。
- **输出**:任务/主表附件字段更新。

#### FR-PRESALES-05 回访问卷
- **触发条件**:回访节点办理问卷。
- **输入**:问卷头信息(状态 1=已提交/-1=草稿)、问卷结果行列表。
- **处理规则**:
  1. 状态=1 时计算分数:逐题按选项得分累加,拼接答案串(格式 `题类:序号-行号|选项,`)(PresalesAction.java:646-684)。
  2. 按问卷的 `markIndexs` 规则计算评价结果(通过/驳回)(PresalesAction.java:691-723)。
  3. 每次保存/提交都生成一份新的问卷结果数据(PresalesAction.java:451)。
  4. 问卷结果写入 `pm_presales_project_callback`(sql-map-presales-config.xml:331-339)。
- **输出**:问卷结果头/行更新;流程推进。

#### FR-PRESALES-06 项目时长统计
- **触发条件**:需要展示/导出项目各阶段耗时。
- **处理规则**:按流程节点(usertask1/usertask2/usertask3/usertask4/serviceApprove)的 `DURATION_` 求和,换算为"天时分秒"字符串,空段去除前导"0天0时0分"(sql-map-presales-config.xml:764-821)。写入 `pm_presales_project_duration`(ON DUPLICATE KEY UPDATE)。
- **输出**:各阶段时长字段(applyDuration/totalDuration/serviceDuration/programDuration/testDuration/callbackDuration/serviceApproveDuration)。

#### FR-PRESALES-07 发货/借转销/RMA 查询
- **触发条件**:按 projectCode 查询发货、借转销、核销。
- **处理规则**:
  1. 发货信息关联合同/发货/条码/物料,默认排除退货(rma_no 为空);containRma=true 时 UNION 退货记录(sql-map-presales-config.xml:479-523)。
  2. 借转销查 `pm_presales_lend_2_sale_from_sms`(sql-map-presales-config.xml:525-528)。
  3. 核销查 `pm_presales_lend_2_rma_from_sms`,UNION contract 与 ppliCode 两个匹配条件(sql-map-presales-config.xml:530-549)。
- **输出**:发货/借转销/核销明细列表。

### 2.2 技术公告管理

#### FR-PROB-01 技术公告列表查询
- **触发条件**:用户进入公告列表页。
- **输入**:probNum(模糊)、watch(跟踪)、theme(模糊)、status(单值或 statusList 多值)、productType(模糊)、desc(模糊)、affectedType、affectedVersion、visibleRange、trackingUser(含 customInfo.trackingUserSearch)、relatedSceneTypesMark(位运算)、mitigationActionTypesMark(位运算)、solutionActionTypesMark(位运算)、probTicketNo(模糊)、checkSoft(是否关联软件版本)、分页/排序。
- **处理规则**:
  1. 软删除过滤 `effectiveTo IS NULL`(sql-map-prob-config.xml:206)。
  2. 当 checkSoft=true 时,左联 `prob_softwares` 聚合受影响版本串(`GROUP_CONCAT` 格式 `conp:x cpld:y boot:z pcb:w manual:m`)(sql-map-prob-config.xml:185-205)。
  3. 位掩码匹配用 `& #mark# > 0`(包含任意一个,OR 语义)(sql-map-prob-config.xml:152,155,156)。
  4. 可见范围:visibleRange=-1 时不过滤;否则 `visibleRange = #visibleRange# OR trackingUser = #trackingUser#`(sql-map-prob-config.xml:141-146)。
  5. 默认按 `createTime DESC` 排序(sql-map-prob-config.xml:256-258)。
- **输出**:公告列表(含 probId/probNum/theme/watch/status/priority/trackingUser/影响版本串/各类 mark/customInfo)。

#### FR-PROB-02 技术公告创建/编辑
- **触发条件**:用户新建或编辑公告。
- **输入**:主题、描述、解决方案、状态(默认"1")、跟踪、优先级、产品类型、起止日期、可见范围、关联场景类型、规避/解决方案操作类型、工单号、附件、受影响软件版本列表、关联产品型号列表。
- **处理规则**:
  1. 新建时自动生成 probNum,格式 `SP.yyyyMMddHHmm`(ProbManageAction.java:254-255)。
  2. 关联场景/操作类型同时存"逗号分隔串"与"位掩码 mark"(Prob.java:261-299)。设置 mark 时按 64 位遍历反推成员(Prob.java:288-298)。
  3. 描述/解决方案模板可从配置取(`prob.info.desc.template`/`prob.info.solution.template`)(ProbManageAction.java:248-249)。
  4. 编辑时回显附件名、受影响版本(序列化为 affectedVersion JSON)、关联产品型号列表(写入 customInfo.probProductList)(ProbManageAction.java:261-275)。
  5. customInfo 字段使用 `JSON_MERGE_PATCH(IFNULL(customInfo,"{}"), #customInfo:JSON#)` 合并(sql-map-prob-config.xml:339-342)。
- **输出**:公告主表与子表数据。

#### FR-PROB-03 技术公告保存/更新
- **触发条件**:用户提交保存。
- **输入**:Prob 主对象、softVersionList、附件上传文件。
- **处理规则**:
  1. 上传附件后调用文件服务写入 `fnd_files`,返回 fileIds(ProbManageAction.java:642-646)。
  2. 状态为"0"时保持,否则强制为"1"(ProbManageAction.java:648)。
  3. 保存时同步软件版本与产品型号;继续编辑(isContinue=1)返回编辑页(ProbManageAction.java:665-668)。
  4. 更新时 attachments 采用追加(`concat(attachments,',',#attachments#)`)(sql-map-prob-config.xml:335)。
- **输出**:公告 ID;失败返回错误页。

#### FR-PROB-04 技术公告审批
- **触发条件**:技术支持人员审批。
- **处理规则**:先更新软件版本(`updateProbSoftVersion`),再更新公告状态(`updateProbStatus`)(ProbManageAction.java:710-711)。
- **输出**:成功/异常消息。

#### FR-PROB-05 技术公告删除
- **处理规则**:软删除,设置 `effectiveTo=NOW()`(sql-map-prob-config.xml:1031-1035)。
- **输出**:跳转列表页。

#### FR-PROB-06 受影响项目检索与修复任务发布
- **触发条件**:技术公告员检索受影响设备并发布修复任务。
- **输入**:probId、过滤条件(serialNum/itemModel/projectName/contractNo/officeCode/marketCode/systemCode/expendCode/industryCode/areapower)。
- **处理规则**:
  1. 受影响设备匹配规则:技术公告受影响版本(`prob_softwares`)通过 `entryType/entrySeries` 与项目设备版本(`pm_project_soft_version`)关联,且 `conpMark BETWEEN sw.markStart AND sw.markEnd`(sql-map-prob-config.xml:659-682)。
  2. 同时要求 `prob_product.itemCode = sv.itemCode AND prob_product.status = 1`(sql-map-prob-config.xml:667)。
  3. 项目必须有效(`effectiveTo IS NULL` 且非 `%his` 历史项目)(sql-map-prob-config.xml:675,681)。
  4. 排除已生成修复任务的设备(`NOT EXISTS prob_restore`)(sql-map-prob-config.xml:686)。
  5. 发布任务:批量插入 `prob_restore`,受 `ischecked=1` 标记控制(sql-map-prob-config.xml:736-750)。
  6. 若指派人为空,则 assigneeRole=服务经理角色;否则 assigneeRole=0(ProbManageAction.java:480-484)。
  7. restoreStatus=0 时设为 10(开始流程)(ProbManageAction.java:485-486)。
- **输出**:修复任务记录;异常返回错误页。

#### FR-PROB-07 修复任务管理(个人/管理员)
- **处理规则**:
  1. 非管理员只能看自己/办事处权限内任务(areapower 注入)(ProbManageAction.java:355-371)。
  2. 个人任务:restoreStatus=10(已发布接受)起查(ProbManageAction.java:524)。
  3. 管理员任务:按 restoreStatus 分流(31=闭环走分页查询;20=办事处返回;30=待闭环)(ProbManageAction.java:596-606)。
  4. 任务列表会通过 JSON 提取受影响公告信息(`JSON_EXTRACT` 子查询)(sql-map-prob-config.xml:874-877)。
  5. 更新任务时,创建流转过程记录(`prob_restore_process`)并回写 processId 到 `prob_restore`(sql-map-prob-config.xml:1000-1019)。
- **输出**:任务列表;状态变更结果。

#### FR-PROB-08 阅读确认与阅读记录
- **处理规则**:
  1. 进入编辑页自动记录阅读(status=0,未确认)(ProbManageAction.java:390)。
  2. 阅读确认写入 status=1;同一读者同公告取 `GREATEST(IFNULL(MAX(status),#status#),#status#)` 保证状态单调递增(sql-map-prob-config.xml:1940-1943)。
  3. 首次阅读时间取 `MIN(readTime)`,确认时间取 `MAX(commitTime)`(sql-map-prob-config.xml:1944-1945)。
- **输出**:阅读记录列表(含读者姓名、首次时间、确认时间)。

#### FR-PROB-09 技术公告统计
- **触发条件**:进入统计页(tabIndex 0-4)。
- **处理规则**:
  1. tabIndex<2:按季度(默认当季度,可 autoAdjust 调整)统计软件版本变更明细。先建临时表 `softChangeLog_tempTable`(聚合设备版本变更历史),再建 `statistic_tempTable`(关联项目/成员/办事处)(sql-map-prob-config.xml:1450-1588)。
  2. tabIndex==2:统计受影响项目列表(临时表 `projectTempTable`)(sql-map-prob-config.xml:1721-1776)。
  3. tabIndex==3:统计合同发货软件版本明细(关联 `view_shipment_info_4_pm`)(sql-map-prob-config.xml:1835-1938)。
  4. tabIndex==4:受影响设备明细(复用 `query_prob_restore_list`)。
  5. 报表按办事处分组统计受影响/总数占比(sql-map-prob-config.xml:1708-1719)。
  6. 临时表使用后显式 DROP(sql-map-prob-config.xml:1700-1706,1778-1780)。
- **输出**:统计明细列表 + 图表 HTML。

### 2.3 软件版本解析与匹配

#### FR-VER-01 软件版本检索
- **输入**:conp/cpld/boot/pcb 各自的值与条件(between/regexp/like 缺省)。
- **处理规则**:从 `fb_soft_version`、`pm_project_soft_version`、`prob_soft_version` 三表 UNION 去重查询(sql-map-prob-config.xml:416-420)。between 时按 `~` 拆分为起止值(SoftVersion.java:166-176)。
- **输出**:版本组合列表。

#### FR-VER-02 手工录入版本范围解析
- **输入**:manualEntry(手工录入文本)、platformType、softVersionTypes(releaseType/architectureType/branchType 组合)、entryStart/entryEnd。
- **处理规则**(版本解析规则,证据 SoftVersionStrategy.java:18-116):
  1. 版本由 10 个可选部分顺序组成,正则按命名捕获组解析:
     - **Hvvv**:产品类型(如 LSW3000、FW1000)
     - **Evvv**:产品版本(E=S/B/A 发布类型 + 3 位 VRB 号)
     - **Fxxx**:平台分支(C=Conplat/S=Smartplat + 3 位编号)
     - **Dxxx**:官网版本阶段(D + 3 位流水)
     - **Pxx**:阶段版本(P + 2-3 位流水)
     - **PATCHxx**:补丁(PATCH + 2-3 位流水)
     - **Txx**:内部测试版本(T + 2-3 位流水)
     - **Lxx**:定制版本(L + 2-3 位流水)
     - **LATCHxx**:定制补丁(PATCH + 2-3 位流水)
     - **MATCHxx**:多版本补丁匹配
  2. 范围表达式由"版本 + 分隔符(~ 及之前/及之后/前后) + 版本"构成,支持中文括号"包含/不含"修饰(SoftVersionStrategy.java:21-28)。
  3. 范围继承:范围两端若缺省 Hvvv/Evvv/Fxxx,则从前一端继承(SoftVersionStrategy.java:39, rangeInheritParts)。
  4. 缺省值:起始用 `indexMarkMapStart`(如 Evvv 起始"0000",Dxxx 起始"D000"),结束用 `indexMarkMapEnd`(如 Evvv 结束"Z999",Dxxx 结束"D999")(SoftVersionStrategy.java:86-112)。
  5. 数字部分补零至 3 位以保障字典序比较(SoftVersionStrategy.java:164-180)。
  6. platformType="other" 时直接以 entryStart/entryEnd 作为起止,不走标准解析(ProbManageAction.java:881-887)。
- **输出**:结构化解析结果 Map<manualEntry, Map<manualEntrySub, [startParser, endParser]>>,每个 parser 含 type/series/version/mark。
- **异常**:无匹配版本格式时抛 `NoMatchedSoftVersionStrategyExecption`(SoftVersionParserFactory.java:21)。

#### FR-VER-03 批量重解析历史版本
- **处理规则**(ProbManageAction.java:900-976):
  1. 查询所有版本(含 splited=0 的未拆解项)。
  2. 对有 manualEntry 的项重新解析,按 manualEntry 子项分组,每组生成唯一 groupId(基于时间戳,递增防重)。
  3. 解析失败(子项为空)时仍写入空记录(manualEntrySub/entryType 等为空)。
  4. 无 manualEntry 的项 groupId=0,原样保留。
  5. 按 probId 分组批量更新(`updateProbSoftVersion`)。

### 2.4 产品与产品组件管理

#### FR-COMP-01 产品组件 CRUD
- **权限**:input/save/import 需 ROLE_ADMIN 或 ROLE_COMPONENT_ADMIN(ProbManageAction.java:1301,1315,1336)。
- **处理规则**:
  1. 新建:id 为空走 insert;有 id 走 updateByIdSelective(ProbManageAction.java:1319-1323)。
  2. customInfo 合并:`JSON_MERGE_PATCH(IFNULL(customInfo,"{}"), #customInfo:JSON#)`(sql-map-prob-config.xml:2168,2182-2185)。
  3. 查询支持模糊(type/name 模糊,version/parentId/state 精确)与 fuzzySearch(三字段 OR 模糊)(sql-map-prob-config.xml:2208-2246)。
  4. 导入:Excel 解析后逐条 upsert(`insertOrUpdateProductComponentSelective` ON DUPLICATE KEY UPDATE)(sql-map-prob-config.xml:2128-2173)。
- **输出**:列表/分页/操作结果。

#### FR-PROBPROD-01 技术公告产品 CRUD
- **权限**:input 需 ROLE_ADMIN/ROLE_PROB_ADMIN/ROLE_PROB_RD;save/import 需 ROLE_ADMIN/ROLE_COMPONENT_ADMIN(ProbManageAction.java:1215,1229,1246)。
- **处理规则**:与产品组件类似,支持模糊查询、fuzzySearch、分页、Excel 导入(前缀 `probProduct.info.`)(sql-map-prob-config.xml:2290-2539)。
- **输出**:列表/分页/操作结果。

#### FR-PRODITEM-01 产品项检索
- **处理规则**:从 `fb_items` 查询 item/itemName/describe_,支持 itemCode/itemModel 前缀匹配与 itemGroups/itemFilters OR 组合过滤(sql-map-prob-config.xml:2541-2577)。result="json" 时直接返回 JSON(ProbManageAction.java:1168-1171)。

---

## 第3章 数据契约【最关键】

> 字段分级说明:C=契约字段(对外稳定);I=内部字段(实现细节);D=废弃字段。
> 表名按实际物理表名;别名/视图单独标注。

### 表 pm_presales_project_header(售前项目主表)

> 证据:sql-map-presales-config.xml:9-55(resultMap)、296-308(update)、341-355(update_state)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| presalesId | int | 否 | 业务主键 | 自增 | C |
| instId | varchar | 是 | 流程实例 ID | 启动流程后非空 | C |
| applyState | int | 是 | 申请状态:-1 草稿、1 审批中、2 审批通过 | nullValue=-1 | C |
| applyBy | varchar | 是 | 申请人 | 创建时写入 | C |
| applyTime | datetime | 是 | 申请时间 | 创建时写入 | C |
| endTime | datetime | 是 | 结束时间 | 闭环时写入 | C |
| presalesCode | varchar | 是 | 项目编码(正式) | 闭环时 CONCAT(projectCode,'-',num) | C |
| projectCode | varchar | 是 | 原项目编码(借货/SMS) | 来源 SMS | C |
| projectName | varchar | 是 | 项目名称 | - | C |
| projectState | varchar | 是 | 项目状态(基础数据 dataTypeCode=27) | 10=待开始等 | C |
| projectType | varchar | 是 | 项目类型(基础数据 dataTypeCode='presalesType') | - | C |
| marketName | varchar | 是 | 市场部名称 | - | C |
| systemName | varchar | 是 | 系统部名称 | - | C |
| expendName | varchar | 是 | 拓展部名称 | - | C |
| industryName | varchar | 是 | 行业名称 | - | C |
| officeCode | varchar | 是 | 办事处编码(关联 fnd_department.departmentNum) | - | C |
| salesman | varchar | 是 | 销售人员 | - | C |
| salesmanLink | varchar | 是 | 联系方式 | - | C |
| productManager | varchar | 是 | 产品经理 | - | C |
| confirmFileIds | varchar | 是 | 现场测试确认单文件 ID 串(逗号分隔) | 追加/删除式维护 | C |
| closeRemark | varchar | 是 | 关闭备注 | 闭环时写入 | C |
| customInfo | json | 是 | 自定义信息(JSON) | JSON_MERGE_PATCH 合并 | C |
| source | varchar | 是 | 数据来源 | - | C |
| hasTransfer | varchar | 是 | 是否存在借转销数据 | 0/1 | C |
| hasRma | varchar | 是 | 是否存在未核销数据 | 0/1 | C |
| createBy | varchar | 是 | 创建人 | 审计字段 | I |
| createTime | datetime | 是 | 创建时间 | 审计字段 | I |
| updateBy | varchar | 是 | 更新人 | 审计字段 | I |
| updateTime | datetime | 是 | 更新时间 | IFNULL(updateTime,now()) | I |
| effectiveFrom | datetime | 是 | 生效起始 | 默认 now() | I |
| effectiveTo | datetime | 是 | 失效时间 | 软删除标记 | I |

> 注:projectState 取值见基础数据 27;常见值 10(待开始)、20、30、31、32、33、100 等 [待澄清]。

### 表 pm_presales_project_product_line(售前项目产品线)

> 证据:sql-map-presales-config.xml:77-96

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| productLineId | int | 否 | 主键 | 自增 | C |
| presalesId | int | 是 | 关联售前项目 | nullValue=0 | C |
| lendInfoId | varchar | 是 | SMS 借货主键 ID | 与 presalesId 二选一关联 | C |
| productFirstName | varchar | 是 | 产品大类名称 | - | C |
| productTypeName | varchar | 是 | 产品类型名称 | - | C |
| itemCode | varchar | 是 | 物料编码 | 关联 fb_items.item | C |
| itemModel | varchar | 是 | 物料型号 | - | C |
| itemDesc | varchar | 是 | 物料描述 | - | C |
| productNum | int | 是 | 产品数量 | - | C |
| transferNum | int | 是 | 借转销数量 | - | C |
| hexiaoNum | int | 是 | 核销数量 | - | C |
| remark | varchar | 是 | 备注 | - | C |
| effectiveFrom | datetime | 是 | 生效起始 | - | I |
| effectiveTo | datetime | 是 | 失效时间 | 查询过滤 IS NULL | I |

> 业务不变量:产品线通过 presalesId 或 lendInfoId 关联;update_presales_product 会按 lendInfoId 回填 presalesId(sql-map-presales-config.xml:370-375)。

### 表 pm_presales_project_callback(售前回访问卷)

> 证据:sql-map-presales-config.xml:310-339

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| presalesId | int | 否 | 关联售前项目 | - | C |
| taskId | varchar | 否 | 关联流程任务 ID | 联合唯一(presalesId+taskId) | C |
| quesnaireId | int | 是 | 问卷结果头 ID | - | C |
| quesnaireVersion | int | 是 | 问卷版本 | - | C |
| quesnaireState | int | 是 | 问卷状态:1 已提交、-1 草稿 | - | C |
| createBy | varchar | 是 | 创建人 | - | I |
| createTime | datetime | 是 | 创建时间 | now() | I |
| updateBy | varchar | 是 | 更新人 | - | I |
| updateTime | datetime | 是 | 更新时间 | - | I |
| effectiveFrom | datetime | 是 | 生效起始 | now() | I |

### 表 pm_presales_project_duration(售前项目时长)

> 证据:sql-map-presales-config.xml:764-821

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| presalesId | int | 否 | 关联售前项目(唯一) | ON DUPLICATE KEY UPDATE | C |
| instId | varchar | 是 | 流程实例 ID | - | C |
| totalDuration | varchar | 是 | 总耗时(天时分秒串) | TIMESTAMPDIFF*1000 换算 | C |
| serviceDuration | varchar | 是 | 服务经理指派耗时(usertask1 DURATION 求和) | - | C |
| programDuration | varchar | 是 | 项目经理指派耗时(usertask2) | - | C |
| testDuration | varchar | 是 | 测试跟踪耗时(usertask3) | - | C |
| callbackDuration | varchar | 是 | 回访耗时(usertask4) | - | C |
| serviceApproveDuration | varchar | 是 | 服务经理审批耗时(serviceApprove) | - | C |
| applyDuration | varchar | 是 | 项目同步到开始的耗时 | 视图 view_presales_project_duration | C |

### 表 prob_main(技术公告主表)

> 证据:sql-map-prob-config.xml:14-30(insert)、159-294(resultMap)、322-380(update)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键(列名为 id,映射 probId) | 自增,last_insert_id | C |
| probNum | varchar | 否 | 公告编号 | 格式 SP.yyyyMMddHHmm | C |
| probTicketNo | varchar | 是 | 工单号 | - | C |
| watch | varchar | 是 | 跟踪(基础数据 dataTypeCode='30') | - | C |
| theme | varchar | 是 | 主题 | - | C |
| desc | text | 是 | 公告描述(富文本) | 列名 desc 为保留字,需反引号 | C |
| solution | text | 是 | 解决方案(富文本) | - | C |
| status | varchar | 是 | 状态(基础数据 dataTypeCode='31') | "0"/"1" 等 | C |
| startdate | datetime | 是 | 开始日期 | - | C |
| duedate | datetime | 是 | 计划完成日期 | - | C |
| attachments | varchar | 是 | 附件文件 ID 串(逗号分隔) | 追加式 concat | C |
| priority | varchar | 是 | 严重级别(基础数据 dataTypeCode='32') | - | C |
| productType | varchar | 是 | 产品类型(模糊匹配) | - | C |
| trackingUser | varchar | 是 | 跟踪用户(username) | - | C |
| visibleRange | int | 是 | 可见范围 | -1=全部,否则受限 | C |
| relatedSceneTypes | varchar | 是 | 关联场景类型(逗号分隔) | 与 mark 双向同步 | C |
| relatedSceneTypesMark | bigint | 是 | 关联场景类型位掩码 | 按 64 位遍历反推 | C |
| mitigationActionTypes | varchar | 是 | 规避方案操作类型(逗号分隔) | 与 mark 双向同步 | C |
| mitigationActionTypesMark | bigint | 是 | 规避方案操作类型位掩码 | - | C |
| solutionActionTypes | varchar | 是 | 解决方案操作类型(逗号分隔) | 与 mark 双向同步 | C |
| solutionActionTypesMark | bigint | 是 | 解决方案操作类型位掩码 | - | C |
| customInfo | json | 是 | 自定义信息(JSON) | JSON_MERGE_PATCH 合并 | C |
| remark | varchar | 是 | 备注 | - | C |
| createBy | varchar | 是 | 创建人 | 审计字段 | I |
| createTime | datetime | 是 | 创建时间 | now() | I |
| updateBy | varchar | 是 | 更新人 | 审计字段 | I |
| updateTime | datetime | 是 | 更新时间 | now() | I |
| effectiveFrom | datetime | 是 | 生效起始 | now() | I |
| effectiveTo | datetime | 是 | 失效时间 | 软删除标记,IS NULL 过滤 | I |

### 表 prob_softwares(技术公告受影响软件版本)

> 证据:sql-map-prob-config.xml:486-516(insert)、517-531(query)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| probId | int | 否 | 关联技术公告 | - | C |
| conp | varchar | 是 | 主程序版本 | 与 entryType 等组合匹配 | C |
| cpld | varchar | 是 | CPLD 版本 | - | C |
| boot | varchar | 是 | Boot 版本 | - | C |
| pcb | varchar | 是 | PCB 版本 | - | C |
| manualEntry | varchar | 是 | 手工录入文本 | 与 conp/cpld/boot/pcb 互斥使用 | C |
| manualEntrySub | varchar | 是 | 手工录入子项(解析后) | - | C |
| entryType | varchar | 是 | 版本类型(解析后,对应 pm_project_soft_version.conpType) | - | C |
| entrySeries | varchar | 是 | 版本系列(解析后,对应 conpSeries) | - | C |
| entryStart | varchar | 是 | 版本范围起始(原始) | - | C |
| entryEnd | varchar | 是 | 版本范围结束(原始) | - | C |
| markStart | varchar | 是 | 缺省补充后的起始 mark | 用于 BETWEEN 匹配 | C |
| markEnd | varchar | 是 | 缺省补充后的结束 mark | 用于 BETWEEN 匹配 | C |
| affectedType | int | 是 | 影响版本类型:0 所有、1 盒式、2 框式(基础数据 'sofeVersionAffectedType') | - | C |
| groupId | bigint | 是 | 分组 ID(同 manualEntry 子项共享) | 时间戳递增 | C |
| splited | int | 是 | 是否已拆解:0 否、1 是 | - | C |
| datastate | int | 是 | 数据状态:1 有效、0 失效 | 失效时 datastate=0 | C |
| customInfo | json | 是 | 自定义信息(JSON) | - | C |
| createBy | varchar | 是 | 创建人 | - | I |
| createTime | datetime | 是 | 创建时间 | now() | I |
| updateBy | varchar | 是 | 更新人 | - | I |
| updateTime | datetime | 是 | 更新时间 | now() | I |

### 表 prob_soft_version(技术公告软件版本字典)

> 证据:sql-map-prob-config.xml:1212-1219(batch_add)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| conp | varchar | 是 | 主程序版本 | INSERT IGNORE 去重 | C |
| cpld | varchar | 是 | CPLD 版本 | - | C |
| boot | varchar | 是 | Boot 版本 | - | C |
| pcb | varchar | 是 | PCB 版本 | - | C |
| createdBy | varchar | 是 | 创建人 | - | I |

### 表 prob_restore(技术公告修复任务)

> 证据:sql-map-prob-config.xml:736-750(insert)、821-860(resultMap)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| probId | int | 否 | 关联技术公告 | - | C |
| serialNum | varchar | 否 | 设备序列号 | 关联 pm_project_soft_version.barcode | C |
| itemModel | varchar | 是 | 设备型号 | - | C |
| processId | int | 是 | 关联流转过程 ID | 关联 prob_restore_process.id | C |
| officeCode | varchar | 是 | 办事处编码 | - | C |
| conp | varchar | 是 | 主程序版本(发布时) | - | C |
| boot | varchar | 是 | Boot 版本(发布时) | - | C |
| cpld | varchar | 是 | CPLD 版本(发布时) | - | C |
| pcb | varchar | 是 | PCB 版本(发布时) | - | C |
| projectId | int | 是 | 关联项目 | - | C |
| projectName | varchar | 是 | 项目名称 | - | C |
| contractNo | varchar | 是 | 合同号 | - | C |
| assignee | varchar | 是 | 办理人(username) | - | C |
| assigneeRole | int | 是 | 办理角色(0=指定人,服务经理角色值见 MessageUtil) | - | C |
| createBy | varchar | 是 | 创建人 | - | I |
| createTime | datetime | 是 | 创建时间 | now() | I |
| updateBy | varchar | 是 | 更新人 | - | I |
| updateTime | datetime | 是 | 更新时间 | now() | I |

> 注:restoreStatus/restoreRemark 不在此表,而在 `prob_restore_process`(通过 processId 关联)(sql-map-prob-config.xml:836-838)。

### 表 prob_restore_process(技术公告修复流转过程)

> 证据:sql-map-prob-config.xml:1000-1008(insert)、1026-1030(count)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增,last_insert_id | C |
| probId | int | 否 | 关联技术公告 | - | C |
| restoreStatus | int | 是 | 修复状态(基础数据 dataTypeCode='33') | 10=开始流程、20=办事处返回、30=待闭环、31=闭环 | C |
| restoreRemark | varchar | 是 | 流转备注说明 | - | C |
| createBy | varchar | 是 | 创建人 | - | I |
| createTime | datetime | 是 | 创建时间 | now() | I |

### 表 prob_restore_weekly(技术公告修复进展周报)

> 证据:sql-map-prob-config.xml:1045-1050(insert)、1051-1067(query)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| probId | int | 否 | 关联技术公告 | - | C |
| fileId | int | 否 | 关联附件文件(fnd_files.id) | - | C |
| createBy | varchar | 是 | 上传人 | - | I |
| createTime | datetime | 是 | 上传时间 | now() | I |

> 查询时联表 fnd_files 取 fileName/uploadTime,联表 fnd_user_info 取 uploadUser(格式 username-realName)(sql-map-prob-config.xml:1057-1067)。

### 表 prob_read_log(技术公告阅读记录)

> 证据:sql-map-prob-config.xml:1940-1953(insert)、1954-1979(query)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| probId | int | 否 | 关联技术公告 | - | C |
| reader | varchar | 否 | 读者(username) | 联合(probId+reader) | C |
| readTime | datetime | 是 | 阅读时间 | NOW() | C |
| status | int | 是 | 状态:0 未确认、1 已确认 | GREATEST 单调递增 | C |
| firstTime | datetime | 是 | 首次阅读时间 | MIN(readTime) | C |
| commitTime | datetime | 是 | 确认时间 | status=1 时 NOW() | C |

> insert 语义:对已存在(probId+reader)记录则更新,`status=GREATEST(IFNULL(MAX(status),#status#),#status#)`、`firstTime=IFNULL(MIN(readTime),now())`、`commitTime=IFNULL(MAX(commitTime), IF(#status#=1,now(),null))`(sql-map-prob-config.xml:1940-1953)。

### 表 prob_product(技术公告关联产品)

> 证据:sql-map-prob-config.xml:2290-2308(resultMap)、2319-2333(insert)、2368-2405(update)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| probId | int | 是 | 关联技术公告 | - | C |
| productCode | varchar | 是 | 产品大类 | - | C |
| productSubCode | varchar | 是 | 产品小类 | - | C |
| itemCode | varchar | 是 | 物料编码 | 关联 fb_items.item,受影响设备匹配键 | C |
| itemModel | varchar | 是 | 物料型号 | - | C |
| itemDesc | varchar | 是 | 物料描述 | - | C |
| status | int | 是 | 状态:0 失效、1 有效 | 受影响匹配要求 status=1 | C |
| customInfo | json | 是 | 自定义信息(JSON) | JSON_MERGE_PATCH 合并 | C |
| createBy | varchar | 是 | 创建人 | - | I |
| createTime | datetime | 是 | 创建时间 | - | I |
| updateBy | varchar | 是 | 更新人 | - | I |
| updateTime | datetime | 是 | 更新时间 | - | I |

### 表 prob_product_component(产品组件)

> 证据:sql-map-prob-config.xml:2052-2064(resultMap)、2085-2127(insert)、2174-2207(update)

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | 自增 | C |
| type | varchar | 是 | 分组 | 模糊查询 | C |
| name | varchar | 是 | 名称 | 模糊查询 | C |
| version | varchar | 是 | 版本 | 精确查询 | C |
| parentId | int | 是 | 父节点 ID(树结构) | - | C |
| state | bit | 是 | 状态(布尔) | - | C |
| customInfo | json | 是 | 自定义信息(JSON) | JSON_MERGE_PATCH 合并 | C |
| createBy | varchar | 是 | 创建人 | - | I |
| createTime | datetime | 是 | 创建时间 | - | I |
| updateBy | varchar | 是 | 更新人 | - | I |
| updateTime | datetime | 是 | 更新时间 | - | I |

### 表 pm_project_soft_version(项目设备软件版本)— 跨域引用

> 证据:sql-map-prob-config.xml:630-658(sqlColumn)、1082-1100(resultMap)
> 说明:此表属于项目域,但本域通过受影响匹配与统计大量引用,故列为引用契约。

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| id | int | 否 | 主键 | - | C(引用) |
| projectId | int | 是 | 关联项目 | - | C(引用) |
| logId | int | 是 | 关联变更日志 | - | C(引用) |
| contractNo | varchar | 是 | 合同号 | - | C(引用) |
| itemCode | varchar | 是 | 物料编码 | - | C(引用) |
| datastate | int | 是 | 数据状态:1 有效 | 匹配时要求 =1 | C(引用) |
| barCode | varchar | 是 | 设备序列号(barcode) | 关联 fb_soft_version.serial_number | C(引用) |
| conp | varchar | 是 | 当前主程序版本 | - | C(引用) |
| conpType | varchar | 是 | 主程序版本类型 | 与 prob_softwares.entryType 匹配 | C(引用) |
| conpSeries | varchar | 是 | 主程序版本系列 | 与 entrySeries 匹配 | C(引用) |
| conpMark | varchar | 是 | 主程序版本 mark | BETWEEN markStart AND markEnd | C(引用) |
| conpBak | varchar | 是 | 变更前主程序版本 | - | C(引用) |
| conpChange | int | 是 | 是否变更:0/1 | nullValue=0 | C(引用) |
| cpld | varchar | 是 | 当前 CPLD 版本 | - | C(引用) |
| cpldBak | varchar | 是 | 变更前 CPLD | - | C(引用) |
| cpldChange | int | 是 | 是否变更 | - | C(引用) |
| boot | varchar | 是 | 当前 Boot 版本 | - | C(引用) |
| bootBak | varchar | 是 | 变更前 Boot | - | C(引用) |
| bootChange | int | 是 | 是否变更 | - | C(引用) |
| pcb | varchar | 是 | 当前 PCB 版本 | - | C(引用) |
| pcbBak | varchar | 是 | 变更前 PCB | - | C(引用) |
| pcbChange | int | 是 | 是否变更 | - | C(引用) |
| executeTime | datetime | 是 | 执行更新时间 | 统计时间过滤键 | C(引用) |
| customInfo | json | 是 | 自定义信息 | - | C(引用) |
| createBy/createTime/updateBy/updateTime | - | 是 | 审计字段 | - | I(引用) |

### 表 fb_soft_version(出厂软件版本)— 跨域引用

> 证据:sql-map-prob-config.xml:416、579
> 说明:出厂版本字典,本域检索与受影响匹配时引用。

| 字段名 | 类型 | 可空 | 语义说明 | 业务不变量 | 分级 |
|---|---|---|---|---|---|
| serial_number | varchar | 否 | 设备序列号(主键) | - | C(引用) |
| conp | varchar | 是 | 出厂主程序版本 | - | C(引用) |
| cpld | varchar | 是 | 出厂 CPLD | - | C(引用) |
| boot | varchar | 是 | 出厂 Boot | - | C(引用) |
| pcb | varchar | 是 | 出厂 PCB | - | C(引用) |

### 视图/临时表(非持久化契约)

| 名称 | 类型 | 语义说明 | 证据 |
|---|---|---|---|
| view_presales_project_duration | 视图 | 售前项目时长(含 applyDuration) | sql-map-presales-config.xml:73 |
| view_presales_project_duration_temp | 视图 | 售前项目时长(导出用) | sql-map-presales-config.xml:638 |
| view_shipment_info_4_pm | 视图 | 发货信息(供 PMS 用) | sql-map-prob-config.xml:1856 |
| view_shipment_ems_4_pm | 视图 | 发货 EMS 信息 | sql-map-prob-config.xml:1857 |
| temp_presales_product_line | 临时表 | 售前产品线(导出用) | sql-map-presales-config.xml:739-762 |
| statistic_tempTable | 临时表 | 技术公告统计中间表 | sql-map-prob-config.xml:1547 |
| softChangeLog_tempTable | 临时表 | 软件变更日志中间表 | sql-map-prob-config.xml:1450 |
| projectTempTable | 临时表 | 受影响项目中间表 | sql-map-prob-config.xml:1721 |
| presales_tempQuesnaireResultTable | 临时表 | 问卷结果(导出用) | sql-map-presales-config.xml:669 |

### 关于"prob_file"与"prob_statistic"

- **prob_file**:代码中存在 ProbFile Bean(ProbFile.java),但实际附件存储在通用 `fnd_files` 表,`prob_main.attachments` 仅存文件 ID 串。无独立 `prob_file` 物理表。证据:`query_prob_file_map` 查 fnd_files(sql-map-prob-config.xml:314-320)、`query_prob_file_list` 查 fnd_files(sql-map-prob-config.xml:1040-1044)。`fnd_files` 字段:id/fileName/filePath/uploadBy/uploadTime。
- **prob_statistic**:ProbStatistic Bean 为查询结果对象(ProbStatistic.java),无独立持久化表。统计使用临时表 statistic_tempTable,查询后显式 DROP。

---

## 第4章 非功能需求

### 4.1 性能

- **NFR-PERF-01 列表分页**:售前列表与技术公告列表均强制分页(`limit #offset#, #pagesize#`),避免全量返回。证据:sql-map-presales-config.xml:292-294、sql-map-prob-config.xml:259-261。
- **NFR-PERF-02 受影响设备检索 GROUP BY 去重**:testCompleteTime/callbackQuesnarieTime/callbackCompleteTime 时间类型查询时,因关联活动历史任务表会产生重复,需 `GROUP BY presalesId` 去重(sql-map-presales-config.xml:276-290)。
- **NFR-PERF-03 统计临时表**:技术公告统计涉及大表 JOIN,采用"先建临时表再分页查询"模式,避免重复扫描;统计后必须显式 `DROP TEMPORARY TABLE`(sql-map-prob-config.xml:1700-1706)。统计前 `SET group_concat_max_len` 以避免 GROUP_CONCAT 截断(sql-map-prob-config.xml:1447-1449)。
- **NFR-PERF-04 软件版本检索 UNION 去重**:三表(fb_soft_version/pm_project_soft_version/prob_soft_version)UNION 去重查询(sql-map-prob-config.xml:416-420),数据量大时需评估性能 [待澄清]。
- **NFR-PERF-05 首次进入不查询**:`checkProject` 首次打开页面不查询避免数据量过大(firstCheck 标志,ProbManageAction.java:413-414)。

### 4.2 一致性

- **NFR-CONS-01 软删除统一**:prob_main 与 pm_presales_project_header 均采用 `effectiveTo` 软删除;查询统一过滤 `effectiveTo IS NULL`。证据:sql-map-prob-config.xml:206、sql-map-presales-config.xml(隐式)。
- **NFR-CONS-02 customInfo JSON 合并语义**:prob_main/prob_product/prob_product_component/pm_presales_project_header 的 customInfo 字段统一使用 `JSON_MERGE_PATCH(IFNULL(customInfo,"{}"), #customInfo:JSON#)` 合并更新,避免覆盖已有键。证据:sql-map-prob-config.xml:341、2168、2380;sql-map-presales-config.xml:306。
- **NFR-CONS-03 位掩码双向同步**:relatedSceneTypes/mitigationActionTypes/solutionActionTypes 的逗号串与 mark 位掩码在 Bean setter 中双向同步,保证两者一致。证据:Prob.java:261-299、309-346、356-393。
- **NFR-CONS-04 阅读状态单调递增**:prob_read_log 的 status 取 `GREATEST(旧值, 新值)`,防止已确认被回退为未确认。证据:sql-map-prob-config.xml:1943。
- **NFR-CONS-05 修复任务与流转过程一致性**:发布/更新修复任务时,先创建 `prob_restore_process` 取得 processId,再回写 `prob_restore.processId`(sql-map-prob-config.xml:1000-1019)。批量操作使用 `FIND_IN_SET(id, #restoreIds#)`。
- **NFR-CONS-06 附件 ID 串维护原子性**:confirmFileIds/deliverFileIds 的追加与删除按逗号边界精确处理(前/中/后三种位置),避免误删。证据:sql-map-presales-config.xml:432-477。
- **NFR-CONS-07 时长计算幂等**:pm_presales_project_duration 使用 `ON DUPLICATE KEY UPDATE`,重复计算不会产生重复行。证据:sql-map-presales-config.xml:813-820。

### 4.3 可用性

- **NFR-AVAIL-01 权限分级**:
  - 技术公告:ROLE_PROB_ADMIN(技术公告员)、ROLE_PROB_SUPPORTER(技术支持)、ROLE_PROB_RD(研发)、ROLE_COMPONENT_ADMIN(组件管理员)、ROLE_ADMIN。导入软件版本需 SUPPORTER;产品组件维护需 COMPONENT_ADMIN;技术公告产品 input 需 PROB_ADMIN/PROB_RD。证据:ProbManageAction.java:790、1215、1229、1246、1301、1315、1336。
  - 售前:ROLE_ENGINEEMANAGER(工程管理部)、ROLE_PRESALES_STAFF(售前专员)、ROLE_PROJECT_VIEWER(项目查看者)、ROLE_SERVICEMANAGER(服务经理)。证据:PresalesAction.java:170-175、210-233。
- **NFR-AVAIL-02 数据权限(办事处)**:非管理员查询修复任务时按 `areapower`(用户所属办事处权限)注入过滤(`officeCode in ($areapower$)`)(sql-map-prob-config.xml:699、770、892)。管理员/技术支持可见全部(ProbManageAction.java:1024-1026、1063-1065)。
- **NFR-AVAIL-03 可见范围**:技术公告支持 visibleRange 字段控制可见性;-1 全部可见,否则仅 trackingUser 本人可见(sql-map-prob-config.xml:141-146)。
- **NFR-AVAIL-04 流程终止容错**:terminate2Close 失败时返回异常消息但不中断,保证批量操作部分成功(PresalesAction.java:510-521)。

### 4.4 版本解析规则(非功能约束)

- **NFR-VER-01 版本格式正则约束**:软件版本必须符合 10 段顺序结构正则(SoftVersionStrategy.java:18),不匹配时按 NewSoftVersionStrategy 兜底(SoftVersionParserFactory.java:10-13)。
- **NFR-VER-02 范围匹配字典序**:版本范围匹配使用字符串 `BETWEEN`,依赖数字补零至 3 位保证字典序与数值序一致(SoftVersionStrategy.java:164-180)。
- **NFR-VER-03 范围继承固定优先**:当 softVersionTypes 固定前缀与范围两端缺省同时存在时,固定前缀优先继承(SoftVersionStrategy.java:225-296 注释逻辑)。
- **NFR-VER-04 设备日志解析多策略**:设备日志版本解析支持 Legacy 与 New 两种策略(VersionParserFactory.java:8-11),按输入匹配选择 [待澄清]。

### 4.5 数据集成

- **NFR-INT-01 外部数据源同步**:售前项目数据来源于 SMS(借货)、OA(借货明细)、SAP(发货核销),通过定时任务同步(GainPresalesInfoFromOA,PresalesAction.java:523-532)。同步表:pm_presales_lend_2_sale_from_sms、pm_presales_lend_2_rma_from_sms、pm_presales_lend_detail_from_oa、pm_presales_lend_2_delivery_off_from_sap。
- **NFR-INT-02 基础数据依赖**:状态/类型/办事处等依赖 fnd_basic_data( dataTypeCode: 27=售前项目状态、'presalesType'=售前类型、30=跟踪、31=状态、32=优先级、33=修复状态、34=选项卡、'relatedSceneType'、'mitigationActionType'、'solutionActionType'、'sofeVersionAffectedType'、29=交付件类型)与 fnd_department、fnd_user_info。
- **NFR-INT-03 流程引擎集成**:售前流程依赖 Activiti(act_ru_task 运行时任务、act_hi_taskinst 历史任务),通过 instId 关联(sql-map-presales-config.xml:69、106)。时长统计依赖 act_hi_taskinst.DURATION_。

---

## 附录:关键歧义点

1. **[待澄清] projectState 取值字典**:代码出现 10/20/30/31/32/33/100 等值,完整状态机与基础数据 27 的映射需核对 fnd_basic_data 实际数据。
2. **[待澄清] serviceApprove 节点**:代码中存在 `serviceApprove` taskDefKey(用于时长统计),但 Presales.bpmn 流程定义中未见该节点,疑为动态加入或历史遗留。
3. **[待澄清] 软件版本检索性能**:三表 UNION 查询数据量未知,是否需要索引优化或缓存待评估。
4. **[待澄清] 设备日志解析策略**:LegacyVersionParserStrategy 与 NewVersionParserStrategy 的匹配规则与适用场景未完全展开,需结合 DeviceLogParserFacade 进一步分析。
5. **[待澄清] assigneeRole 角色值**:prob_restore.assigneeRole 取 0 或 MessageUtil.ROLE_SERVICEMANAGER 值,具体数值常量需核对 MessageUtil。
