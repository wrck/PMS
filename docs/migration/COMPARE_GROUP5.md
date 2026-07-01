# 第五组迁移比对报告：PresalesAction / ReportAction / SubcontractAction

> 比对时间: 2026-07-01  
> 比对规则: 逐一提取源文件(struts)每个业务方法的核心逻辑，与目标文件(springboot)对应方法比对

---

## 一、PresalesAction → PmsPresalesController + PmsPresalesServiceImpl

### 方法: list()
- **源逻辑摘要**: 获取当前用户；根据角色(工程管理经理/售前人员)设置默认项目状态筛选；支持导出模式（`displayParam.getExport()`）时查询导出数据并设为全量；非导出时分页查询售前列表
- **目标实现**: `GET /list` 使用 MyBatis-Plus IPage 分页，支持 presalesCode/projectName/applyState/officeCode 参数
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①基于角色(ROLE_ENGINEEMANAGER/ROLE_PRESALES_STAFF)的默认状态筛选逻辑；②导出模式（export参数）的特殊处理；③导出明细(exportDetail)参数支持

### 方法: input()
- **源逻辑摘要**: 角色权限判断——工程管理经理/售前人员进入编辑页(input)，项目查看者重定向到只读页(read)，其他返回ERROR；查询项目详情+产品列表+评论列表+项目类型基础数据
- **目标实现**: `GET /{id}` 仅查询单条记录详情
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①角色权限路由逻辑（不同角色跳不同页面）；②关联查询（产品列表、评论列表、项目类型基础数据）未在Controller层一次性返回

### 方法: apply()
- **源逻辑摘要**: 判断是否新申请(taskId为空)→调用startPresalesFlow；否则→调用submitReApply；设置重定向到列表页
- **目标实现**: `POST /{id}/start-flow` 和 `POST /{id}/re-apply` 两个独立接口
- **状态**: ✅ 完全迁移
- **差异说明**: 拆分为两个接口，逻辑等价

### 方法: read()
- **源逻辑摘要**: 查询项目详情+产品列表+评论列表+任务列表（类型为TYPE_OF_PRESALES）
- **目标实现**: `GET /{id}` 仅返回项目详情；产品/评论/任务通过独立接口获取
- **状态**: ⚠️ 部分迁移
- **差异说明**: 产品(`/products`)、评论(`/comments`)、任务(`/tasks`)均有独立接口，但需要前端多次调用；原系统一次请求返回所有数据

### 方法: aduit()
- **源逻辑摘要**: 根据taskDefKey(usertask2/serviceApprove/usertask3/usertask4/usertask1)决定审批页面重定向URL
- **目标实现**: `POST /{id}/approve` 统一审批接口
- **状态**: ⚠️ 部分迁移
- **差异说明**: 原系统根据流程节点key路由到不同审批页面（smaduit/pmaduit/emaduit/input），新系统简化为统一approve接口，丢失了流程节点路由逻辑

### 方法: smaduit()
- **源逻辑摘要**: param为空时→加载项目详情/产品/评论/任务/User返回审批页面；param.instId不为空时→调用submitSmAduit进行审批
- **目标实现**: `POST /{id}/sm-audit` 接收comment和approved参数
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①审批页面数据预加载（查询产品/评论/任务/User）；②原系统通过instId判断是否进入审批，新系统简化为直接审批

### 方法: pmaduit()
- **源逻辑摘要**: param为空时→解析urlParams获取taskId，查询产品/评论/项目详情/任务列表，获取交付件列表(projectDeliverList)用于变更交付件类型，设置taskFinshedTime；param.instId不为空时→调用submitpmAduit审批
- **目标实现**: `POST /{id}/pm-audit` 接收comment和approved参数
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①urlParams解析逻辑；②交付件列表(projectDeliverList)查询；③taskFinshedTime初始化；④审批页面数据预加载

### 方法: updateTask()
- **源逻辑摘要**: 校验taskFinshedTime不能晚于当前时间（若晚于则设为当前时间）；调用updatePresalesTask更新；设置成功/失败消息
- **目标实现**: `PUT /task` 接收PmsPresalesTask对象
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①taskFinshedTime时间校验逻辑（不能晚于当前时间）；②成功/失败消息设置

### 方法: emaduit()
- **源逻辑摘要**: param为空时→加载项目详情/产品/评论/任务/项目类型/交付件列表；若问卷状态为1则加载问卷表单(getCbForm)；param.instId不为空→调用submitEmAduit审批
- **目标实现**: `POST /{id}/em-audit` 接收comment和approved参数
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①审批页面数据预加载；②交付件列表查询；③问卷表单加载逻辑(getCbForm)

### 方法: callback()
- **源逻辑摘要**: 问卷提交时→若status==1则计算分数(queryQuesnaireScore)，保存问卷(insertPresalesQuesnaire)；非提交时→查询项目详情，获取生效问卷列表(findPmClosedLoopQuesnaireList)，获取问卷模板/已填写内容(getCbForm)
- **目标实现**: 无直接对应接口
- **状态**: ❌ 未迁移
- **差异说明**: 回访问卷功能完全未迁移，包括问卷提交、草稿保存、分数计算、问卷模板加载等全部逻辑

### 方法: shipmentInfo()
- **源逻辑摘要**: 根据presalesCode和containRma查询发货信息
- **目标实现**: `GET /shipment-info` 接收presalesCode和containRma参数
- **状态**: ✅ 完全迁移

### 方法: lend2SaleInfo()
- **源逻辑摘要**: 根据presalesCode查询借转销信息
- **目标实现**: `GET /lend2sale-info` 接收presalesCode参数
- **状态**: ✅ 完全迁移

### 方法: lend2RmaInfo()
- **源逻辑摘要**: 根据presalesCode查询核销信息
- **目标实现**: `GET /lend2rma-info` 接收presalesCode参数
- **状态**: ✅ 完全迁移

### 方法: tempAuthInfo()
- **源逻辑摘要**: 根据presalesId查询项目，获取lendInfoId，查询临时授权信息
- **目标实现**: `GET /temp-auth-info` 接收presalesId参数，ServiceImpl中先查询项目获取lendInfoId
- **状态**: ✅ 完全迁移

### 方法: terminate2Close()
- **源逻辑摘要**: 调用presalesService.terminate2Close(presalesIds, message)批量终止；presalesIds支持多个ID
- **目标实现**: `POST /{id}/terminate` 单个ID终止
- **状态**: ⚠️ 部分迁移
- **差异说明**: 原系统支持批量终止(presalesIds逗号分隔)，新系统仅支持单个ID

### 方法: syncOaData()
- **源逻辑摘要**: 调用GainPresalesInfoFromOA.execute(null)从OA系统同步售前数据
- **目标实现**: `POST /sync-oa` 接口已定义，ServiceImpl中标记为待实现
- **状态**: ⚠️ 部分迁移
- **差异说明**: 接口骨架已迁移，但实际同步逻辑（GainPresalesInfoFromOA Job）未实现

### 方法: upload()
- **源逻辑摘要**: 两种模式——①有projectDeliverList时遍历上传交付件文件（解析deliverableType、调用uploadFile）；②仅有eventKey时查询交付件列表返回
- **目标实现**: `POST /upload-delivers` 接收presalesId和deliverList
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①eventKey解析逻辑（ek.split("-")获取dataTypeCode和basicDataId）；②文件二进制上传（原系统处理uploaddelivery字节数组）；③deliverableType解析逻辑

### 方法: deleteDeliverById()
- **源逻辑摘要**: 调用presalesService.deleteDeliverById(fileId)删除交付件，异常时fileId设为0
- **目标实现**: `DELETE /deliver/{fileId}` 接口+ServiceImpl骨架
- **状态**: ⚠️ 部分迁移
- **差异说明**: ServiceImpl中删除逻辑标记为待实现（"实际删除逻辑需要根据具体的文件关联表实现"）

### 方法: updateDeliverById()
- **源逻辑摘要**: 调用presalesService.updateProjectDeliverById(projectDeliver)更新交付件
- **目标实现**: `PUT /deliver` 接口+ServiceImpl骨架
- **状态**: ⚠️ 部分迁移
- **差异说明**: ServiceImpl简化为更新PmsPresalesTask的deliverFileIds，原系统更新的是ProjectDeliver实体

### 方法: updateConfirmFiles()
- **源逻辑摘要**: fileId不为0时调用updatePrealesFileIds(presalesId, presalesTaskId, fileId)删除确认文件
- **目标实现**: `PUT /{id}/confirm-files` 接收fileIds
- **状态**: ⚠️ 部分迁移
- **差异说明**: 原系统是删除指定fileId的文件，新系统是直接设置fileIds字符串，逻辑不同

### 方法: exportPresales()
- **源逻辑摘要**: 与list()类似的角色判断+默认状态设置；导出参数(6578706f7274)为"1"时查询导出数据
- **目标实现**: `GET /export` 接口
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①角色权限判断；②默认状态设置逻辑；③导出参数特殊编码(6578706f7274)处理

### 私有方法: queryQuesnaireScore() / quesMark() / queryQuesnaireOpt() / queryPmClosedLoopQuesnaire()
- **源逻辑摘要**: 问卷评分核心算法——获取选项映射、计算总分、拼接答案字符串、应用评分规则(PmClosedLoopMarkFactory)、判断通过/驳回
- **目标实现**: 无对应实现
- **状态**: ❌ 未迁移
- **差异说明**: 问卷评分算法完全未迁移

### 私有方法: getCbForm() / findPmClosedLoopQuesnaireList() / getQuesTypeScore()
- **源逻辑摘要**: 获取问卷模板信息、获取生效问卷列表、计算各类型得分
- **目标实现**: 无对应实现
- **状态**: ❌ 未迁移
- **差异说明**: 问卷表单加载和分数计算逻辑完全未迁移

---

## 二、ReportAction → ReportController + ReportServiceImpl

### 方法: show()
- **源逻辑摘要**: 加载选项卡(navTabList)；查询全国办事处列表(officeList)；查询全国综述(summarize)；查询项目经理指派率表格(assignedTableHtml)；查询跟踪率表格(traceTableHtml)；查询闭环新增比表格(closeTableHtml)
- **目标实现**: `GET /overview` 返回Map，包含totalProjects/activeProjects/closedProjects
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①选项卡(navTabList)加载；②办事处列表加载；③指派率/跟踪率/闭环新增比的HTML表格渲染（原系统通过EchartsUtil.packagingTableHtml生成）；④综述数据字段不完整（缺少工程类型数量等）

### 方法: loadLineData()
- **源逻辑摘要**: 获取办事处名称映射和目标数据类型映射；调用queryLineData获取按月统计数据；冒泡排序按时间排序；构建ECharts折线图JSON（含Y轴min/max/scale设置）
- **目标实现**: `GET /line-data` 返回按月统计的Map列表
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①办事处名称映射（officeMap）；②目标数据类型映射（targetMap）；③ECharts图表JSON构建（原系统生成完整ECharts配置）；④Y轴min/max/scale动态设置；⑤按时间排序逻辑

### 方法: loadLine_qualityData()
- **源逻辑摘要**: 查询闭环项目数量按月趋势；排序；构建ECharts折线图
- **目标实现**: `GET /quality-line` 返回按月闭环项目数
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①办事处维度筛选；②ECharts图表JSON构建；③排序逻辑

### 方法: loadLine_implData()
- **源逻辑摘要**: 查询实施占比趋势（原厂直服/原厂督导/代理商自服三种方式）；按settingTimes排序；构建多系列ECharts折线图
- **目标实现**: `GET /impl-line` 返回按月实施项目数
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①三种实施方式(impl0/impl1/impl3)的分别统计；②settingTimes排序；③多系列ECharts图表构建；④办事处维度筛选

### 方法: assignedRate()
- **源逻辑摘要**: 查询各办事处指派率数据；统计全国汇总(totalData)；获取基础数据名称；构建ECharts柱状图（含全国汇总放在首位）
- **目标实现**: `GET /assigned-rate` 返回单个办事处的total/assigned/rate
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①按办事处分组统计所有办事处的指派率；②全国汇总数据；③ECharts柱状图JSON构建；④基础数据名称映射

### 方法: traceRate()
- **源逻辑摘要**: 查询各办事处跟踪率数据；统计全国汇总；构建ECharts柱状图
- **目标实现**: `GET /trace-rate` 返回单个办事处的active/traced/rate
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①按办事处分组统计；②全国汇总；③ECharts柱状图构建；④跟踪率计算逻辑差异（原系统基于实际跟踪记录，新系统默认已进入实施即有跟踪）

### 方法: closeRate()
- **源逻辑摘要**: 查询闭环新增比数据（按办事处）；冒泡排序；构建ECharts柱状图（全国放在首位，rate=10000.0占位后替换）
- **目标实现**: `GET /close-rate` 返回单个办事处的total/closed/rate
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①按办事处分组统计所有办事处的闭环新增比；②排序逻辑；③ECharts柱状图构建；④全国汇总特殊处理

### 方法: implRate()
- **源逻辑摘要**: 分别查询ALL/原厂直服/原厂督导/代理商自服四种实施方式数据；按办事处计算各方式占比百分比；全国数据累加后重新计算百分比；构建多系列ECharts柱状图
- **目标实现**: `GET /impl-rate` 返回active/implementing/rate/byServiceType
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①三种实施方式的分别占比计算（原厂直服/原厂督导/代理商自服）；②按办事处分组统计；③全国汇总的百分比重新计算；④ECharts多系列柱状图构建

### 方法: quality()
- **源逻辑摘要**: 查询闭环项目质量数据（按办事处）；查询全部闭环数和非直签督导闭环数；计算平均分和项目数量；构建两个ECharts柱状图（平均分+项目数量）+质量表格HTML（含全部闭环数和非直签督导闭环数）
- **目标实现**: `GET /quality` 返回byState/byOffice/byType/total
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①闭环平均得分(avgCloseScore)计算；②全部闭环数 vs 非直签督导闭环数的区分；③ECharts图表构建；④质量表格HTML渲染；⑤Y轴min=69/max=100的设置

### 方法: projectSummaryStatus()
- **源逻辑摘要**: 极其复杂的多维度统计——①权限判断（管理员/工程管理等看全国，其他人看权限范围内）；②查询项目汇总状态数据；③通过summaryStr配置动态构建多维度统计（实施状态/流程状态/可闭环项目数等）；④支持正则匹配条件判断；⑤生成带链接的HTML表格（可点击查看详情）；⑥支持导出模式
- **目标实现**: `GET /summary-status` 返回按办事处+状态分组的简单列表
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①权限范围过滤（areapower）；②多维度统计配置（summaryStr动态配置）；③正则匹配条件判断逻辑；④实施状态关系映射(executionStateRelation)；⑤可闭环项目数统计；⑥HTML表格渲染（含可点击链接）；⑦导出模式支持；⑧选项卡(navTabList)过滤

### 方法: input() [已废弃]
- **源逻辑摘要**: @Deprecated；加载办事处列表和选项卡
- **目标实现**: `GET /custom` 聚合调用其他报表方法
- **状态**: ✅ 完全迁移
- **差异说明**: 原方法已标记@Deprecated，新实现为聚合查询，合理迁移

### 私有方法: getOfficeMap() / converter2Map() / fillExpressionParams() / summaryDimensionStatus() / arraySort() / sortSettingTime()
- **源逻辑摘要**: 辅助方法——办事处映射、基础数据转Map、表达式参数填充、多维度统计状态计算、数组排序
- **目标实现**: 无对应实现
- **状态**: ❌ 未迁移
- **差异说明**: 所有辅助方法均未迁移，特别是summaryDimensionStatus()是projectSummaryStatus的核心统计逻辑

---

## 三、SubcontractAction → SubcontractController + SubcontractServiceImpl

### 方法: view()
- **源逻辑摘要**: 调用list()，若结果仅1条则直接进入input()详情页，否则返回列表
- **目标实现**: 无直接对应接口
- **状态**: ❌ 未迁移
- **差异说明**: 快速查看功能未迁移

### 方法: list()
- **源逻辑摘要**: 角色权限检查（7种角色）；非管理员/工程管理等设置areapower过滤；加载状态/回访状态/类型基础数据；加载市场部+用服部门列表（processDepartment添加"市场-"前缀）；加载利润部门列表；加载公司集合；查询付款审批状态；支持导出模式；分页查询
- **目标实现**: `GET /list` 简单分页查询，支持subcontractName/officeCode/state
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①角色权限检查（7种角色）；②areapower区域过滤；③基础数据加载（状态/回访状态/类型/部门/公司）；④部门名称前缀处理(processDepartment)；⑤利润部门列表；⑥付款审批状态查询；⑦导出模式；⑧needPayment等扩展参数

### 方法: input()
- **源逻辑摘要**: 角色权限检查；新建时初始化空对象；编辑时查询详情+项目列表+权限校验（areapower+角色组合判断）；根据角色设置默认tabIndex；加载类型/部门/利润部门基础数据；加载autoCheckProjects配置
- **目标实现**: `GET /{id}` 仅查询详情
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①角色权限检查和areapower校验；②项目列表加载；③tabIndex角色路由逻辑；④基础数据加载（类型/部门/利润部门）；⑤autoCheckProjects配置加载

### 方法: create()
- **源逻辑摘要**: 新建(id==null)→createSubcontractProject；更新→updateSubcontractProject；保存交付件文件(saveDeliverFiles)；若callbackFlag为true则发起回访流程(startCallBackFlow)
- **目标实现**: `POST` (add) 和 `PUT` (update) 分开
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①交付件文件保存(saveDeliverFiles)；②自动触发回访流程逻辑(callbackFlag判断)

### 方法: apply()
- **源逻辑摘要**: 先调用create()保存内容；再调用startSubcontractFlow发起流程
- **目标实现**: `POST /{id}/start-flow` 仅更新状态为审批中
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①先保存再发起的两步逻辑；②实际工作流引擎集成（原系统调用startSubcontractFlow）

### 方法: audit()
- **源逻辑摘要**: 根据workflowCommonParam.outcome路由——TASK_KEY_APPROVE→auditNormalApproveSubcontractFlow；TASK_KEY_ZR_APPROVE→approveSubcontractFlow
- **目标实现**: `POST /{id}/approve` 统一审批
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①根据outcome路由到不同审批逻辑（普通审批 vs 总监审批）；②subcontractPriceList转包价录入

### 方法: close()
- **源逻辑摘要**: 调用closeSubcontractFlow(workflowCommonParam, null, subcontract)闭环
- **目标实现**: `POST /{id}/close` 更新状态为已关闭
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少实际工作流闭环逻辑（closeSubcontractFlow）

### 方法: startCallBackFlow()
- **源逻辑摘要**: 调用subcontractService.startCallBackFlow(subcontractId, subcontractComment)发起回访；返回JSON结果（success/message/data）
- **目标实现**: `POST /{id}/callback` 仅更新回调状态
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①subcontractComment参数传递；②回访工作流实际启动逻辑；③返回回访对象(SubcontractCallback)数据

### 方法: querySubcontractCallback()
- **源逻辑摘要**: 复杂方法——①审批(workflowCommonParam.instId不为空→submitCallBackFlow2)；②问卷提交(status!=0→计算分数+保存问卷)；③查询当前工作流参数；④查询最新回访记录；⑤获取生效问卷列表；⑥获取问卷模板/已填写内容
- **目标实现**: 无直接对应接口
- **状态**: ❌ 未迁移
- **差异说明**: 回访问卷查询+审批+问卷提交功能完全未迁移

### 方法: chooseSubcontractProject()
- **源逻辑摘要**: 根据contractNos查询关联项目列表
- **目标实现**: `GET /projects?contractNos=xxx`
- **状态**: ⚠️ 部分迁移
- **差异说明**: 原系统通过project.setContractNo(contractNos)查询，新系统通过officeCode查询，查询条件可能不一致

### 方法: refreshSubcontractProject()
- **源逻辑摘要**: 调用input()验证权限；检查原项目列表是否为空（空则允许刷新，管理员/工程管理等可强制刷新）；查询合同对应的新项目；合并新旧项目ID（去重）；更新数据库
- **目标实现**: 无直接对应接口
- **状态**: ❌ 未迁移
- **差异说明**: 刷新项目列表功能完全未迁移

### 方法: chooseShipmentInfo()
- **源逻辑摘要**: 根据contractNos和projectIds查询发货序列号；解析selected参数中的contractProfitCenter（JSON数组）
- **目标实现**: `GET /shipment-info` 按contractNos查询
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①projectIds筛选；②contractProfitCenter利润中心筛选（selected参数解析）

### 方法: querySubcontractLine()
- **源逻辑摘要**: 根据subcontract.id查询设备行列表
- **目标实现**: `GET /{id}/lines`
- **状态**: ✅ 完全迁移

### 方法: querySubcontractDeliver()
- **源逻辑摘要**: 根据subcontract.id查询附件列表，设置effectiveTo为当前时间
- **目标实现**: `GET /{id}/delivers`
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少effectiveTo有效期过滤（原系统只返回有效期内的附件）

### 方法: deleteSubcontractDeliver()
- **源逻辑摘要**: 根据subcontractDeliverVO.getIds()批量删除附件
- **目标实现**: `DELETE /deliver/{id}` 单个删除
- **状态**: ⚠️ 部分迁移
- **差异说明**: 原系统支持批量删除(IDs)，新系统仅支持单个删除

### 方法: checkSubcontractName()
- **源逻辑摘要**: 检查转包名是否存在，返回"0"或"1"
- **目标实现**: 无直接对应接口
- **状态**: ❌ 未迁移
- **差异说明**: 转包名校验功能未迁移

### 方法: queryContractNoEngineeFee()
- **源逻辑摘要**: 查询合同工程服务费+转包价格；获取当前工作流参数（APPROVE+ZR_APPROVE）
- **目标实现**: 无直接对应接口
- **状态**: ❌ 未迁移
- **差异说明**: 合同工程服务费查询功能未迁移

### 方法: querySubcontractPayment()
- **源逻辑摘要**: 极其复杂——①加载税率基础数据；②查询转包详情+项目列表；③查询/设置多维度信息(multiDimInfo)；④查询付款列表；⑤并行流处理每个付款→查询关联交付件→过滤发票附件→发票识别状态检查→去重→计算发票总金额；⑥统计申请比例/已付比例/发票总额；⑦查询多个工作流节点参数(GENERATE_CONTRACT→APPLY_PAYMENT→APPROVE_PAYMENT→ACCEPTANCE_TASK)；⑧维护类项目检查服务单上传；⑨查询当前公司信息；⑩加载公司集合
- **目标实现**: `GET /{id}/payments` 简单查询付款列表
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①税率基础数据加载；②多维度信息查询/设置；③发票附件识别和金额计算；④申请比例/已付比例/发票总额统计；⑤多个工作流节点参数查询；⑥维护类服务单检查；⑦公司信息查询；⑧并行流处理逻辑

### 方法: savePayment()
- **源逻辑摘要**: 极其复杂——①工程管理角色→先调用create()保存；②根据workflowCommonParam.outcome路由到不同处理：GENERATE_CONTRACT→generateContractFlow、APPROVE_PAYMENT→approvePaymentFlow、PROFIT_SERVICE_APPROVE→profitSerivceManagerFlow、NORMAL_APPROVE_TASK→normalApproveSubcontractFlow、ACCEPTANCE_TASK→submitAcceptanceFlow、其他→applyPaymentFlow；③APPLY_PAYMENT时先保存付款信息+交付件
- **目标实现**: `POST /payment` 简单保存单条付款记录
- **状态**: ⚠️ 部分迁移
- **差异说明**: 缺少①根据outcome路由到6种不同的工作流处理逻辑；②付款信息批量保存+交付件保存；③工程管理角色的前置保存逻辑

### 方法: querySubcontractPaymentPrint()
- **源逻辑摘要**: 打印付款申请——查询转包详情+项目列表+付款列表；并行流处理付款→查询交付件→过滤发票附件；按selected过滤排除的付款；统计申请比例；查询公司信息
- **目标实现**: 无直接对应接口
- **状态**: ❌ 未迁移
- **差异说明**: 打印付款申请功能未迁移

### 方法: verifyPaymentDeliver()
- **源逻辑摘要**: 验证付款交付件发票识别——遍历selected付款ID；调用verifySubcontractPaymentDeliver进行发票识别；更新发票编号；返回识别结果
- **目标实现**: 无直接对应接口
- **状态**: ❌ 未迁移
- **差异说明**: 发票识别验证功能未迁移

### 方法: terminateWorkFlow()
- **源逻辑摘要**: 终止工作流——调用subcontractService.terminateWorkFlow(subcontractId, workflowCommonParam)
- **目标实现**: 无直接对应接口
- **状态**: ❌ 未迁移
- **差异说明**: 工作流终止功能未迁移

### 方法: querySubcontractComment()
- **源逻辑摘要**: 查询审批记录+当前项目详情；根据用户角色和项目状态获取不同的工作流参数（受益部门服务经理/一级部门服务经理/通用审批）
- **目标实现**: 无直接对应接口
- **状态**: ❌ 未迁移
- **差异说明**: 审批记录查询+角色路由逻辑未迁移

### 方法: queryFacilitator()
- **源逻辑摘要**: 查询所有启用状态的服务商，返回JSON
- **目标实现**: `GET /facilitators`
- **状态**: ✅ 完全迁移

### 方法: querySubcontractInfoForProject()
- **源逻辑摘要**: 根据projectId查询关联的转包项目列表
- **目标实现**: 无直接对应接口
- **状态**: ❌ 未迁移
- **差异说明**: 按项目查询转包记录功能未迁移

### 方法: facilitatorList()
- **源逻辑摘要**: 查询服务商列表
- **目标实现**: `GET /facilitators`
- **状态**: ✅ 完全迁移

### 方法: facilitatorEdit()
- **源逻辑摘要**: GET→查询单个服务商详情；POST→更新或新增服务商
- **目标实现**: `GET /facilitator/{id}` + `POST /facilitator`
- **状态**: ✅ 完全迁移

### 方法: downloadFile()
- **源逻辑摘要**: Base64解码redirect获取deliverId；查询交付件详情用于下载
- **目标实现**: 无直接对应接口
- **状态**: ❌ 未迁移
- **差异说明**: 附件下载功能未迁移

### 私有方法: queryQuesnaireScore() / quesMark() / queryQuesnaireOpt() / findPmClosedLoopQuesnaireList() / getCbForm() / getQuesTypeScore() / processDepartment()
- **源逻辑摘要**: 问卷评分算法（与PresalesAction相同）；部门名称前缀处理
- **目标实现**: 无对应实现
- **状态**: ❌ 未迁移
- **差异说明**: 问卷评分算法和部门处理辅助方法均未迁移

---

## 四、汇总表

### PresalesAction (18个业务方法)

| # | 方法 | 状态 | 关键差异 |
|---|------|------|----------|
| 1 | list() | ⚠️ | 缺角色默认状态筛选、导出模式 |
| 2 | input() | ⚠️ | 缺角色路由、关联查询 |
| 3 | apply() | ✅ | 拆分为两个等价接口 |
| 4 | read() | ⚠️ | 需前端多次调用 |
| 5 | aduit() | ⚠️ | 缺流程节点路由逻辑 |
| 6 | smaduit() | ⚠️ | 缺审批页面预加载 |
| 7 | pmaduit() | ⚠️ | 缺urlParams解析、交付件列表 |
| 8 | updateTask() | ⚠️ | 缺时间校验逻辑 |
| 9 | emaduit() | ⚠️ | 缺问卷表单加载 |
| 10 | callback() | ❌ | 回访问卷完全未迁移 |
| 11 | shipmentInfo() | ✅ | — |
| 12 | lend2SaleInfo() | ✅ | — |
| 13 | lend2RmaInfo() | ✅ | — |
| 14 | tempAuthInfo() | ✅ | — |
| 15 | terminate2Close() | ⚠️ | 缺批量终止支持 |
| 16 | syncOaData() | ⚠️ | 同步逻辑未实现 |
| 17 | upload() | ⚠️ | 缺eventKey解析、文件上传 |
| 18 | deleteDeliverById() | ⚠️ | 删除逻辑待实现 |
| 19 | updateDeliverById() | ⚠️ | 更新对象不同 |
| 20 | updateConfirmFiles() | ⚠️ | 逻辑差异 |
| 21 | exportPresales() | ⚠️ | 缺角色判断、默认状态 |
| — | 问卷评分私有方法(5个) | ❌ | 完全未迁移 |

**小计**: ✅ 5个 | ⚠️ 13个 | ❌ 3个（含5个私有方法）

### ReportAction (9个业务方法)

| # | 方法 | 状态 | 关键差异 |
|---|------|------|----------|
| 1 | show() | ⚠️ | 缺选项卡、ECharts HTML渲染 |
| 2 | loadLineData() | ⚠️ | 缺ECharts图表构建、排序 |
| 3 | loadLine_qualityData() | ⚠️ | 缺办事处维度、ECharts |
| 4 | loadLine_implData() | ⚠️ | 缺三种实施方式分组 |
| 5 | assignedRate() | ⚠️ | 缺按办事处分组、ECharts |
| 6 | traceRate() | ⚠️ | 缺按办事处分组、ECharts |
| 7 | closeRate() | ⚠️ | 缺按办事处分组、排序 |
| 8 | implRate() | ⚠️ | 缺三种实施方式占比计算 |
| 9 | quality() | ⚠️ | 缺闭环平均分、全部/非直签区分 |
| 10 | projectSummaryStatus() | ⚠️ | 缺多维度统计、权限过滤、HTML渲染 |
| 11 | input() @Deprecated | ✅ | 合理迁移 |
| — | 辅助方法(6个) | ❌ | 完全未迁移 |

**小计**: ✅ 1个 | ⚠️ 10个 | ❌ 7个（含6个辅助方法）

### SubcontractAction (26个业务方法)

| # | 方法 | 状态 | 关键差异 |
|---|------|------|----------|
| 1 | view() | ❌ | 快速查看未迁移 |
| 2 | list() | ⚠️ | 缺角色权限、areapower、基础数据 |
| 3 | input() | ⚠️ | 缺权限校验、tabIndex路由 |
| 4 | create() | ⚠️ | 缺交付件保存、自动回访 |
| 5 | apply() | ⚠️ | 缺先保存再发起逻辑 |
| 6 | audit() | ⚠️ | 缺outcome路由、转包价录入 |
| 7 | close() | ⚠️ | 缺工作流闭环逻辑 |
| 8 | startCallBackFlow() | ⚠️ | 缺工作流启动逻辑 |
| 9 | querySubcontractCallback() | ❌ | 回访问卷完全未迁移 |
| 10 | chooseSubcontractProject() | ⚠️ | 查询条件可能不一致 |
| 11 | refreshSubcontractProject() | ❌ | 完全未迁移 |
| 12 | chooseShipmentInfo() | ⚠️ | 缺projectIds/profitCenter筛选 |
| 13 | querySubcontractLine() | ✅ | — |
| 14 | querySubcontractDeliver() | ⚠️ | 缺effectiveTo过滤 |
| 15 | deleteSubcontractDeliver() | ⚠️ | 仅支持单个删除 |
| 16 | checkSubcontractName() | ❌ | 完全未迁移 |
| 17 | queryContractNoEngineeFee() | ❌ | 完全未迁移 |
| 18 | querySubcontractPayment() | ⚠️ | 缺发票识别、金额统计、多工作流 |
| 19 | savePayment() | ⚠️ | 缺6种outcome路由逻辑 |
| 20 | querySubcontractPaymentPrint() | ❌ | 完全未迁移 |
| 21 | verifyPaymentDeliver() | ❌ | 完全未迁移 |
| 22 | terminateWorkFlow() | ❌ | 完全未迁移 |
| 23 | querySubcontractComment() | ❌ | 完全未迁移 |
| 24 | queryFacilitator() | ✅ | — |
| 25 | querySubcontractInfoForProject() | ❌ | 完全未迁移 |
| 26 | facilitatorList() | ✅ | — |
| 27 | facilitatorEdit() | ✅ | — |
| 28 | downloadFile() | ❌ | 完全未迁移 |
| — | 问卷/部门私有方法(7个) | ❌ | 完全未迁移 |

**小计**: ✅ 4个 | ⚠️ 13个 | ❌ 12个（含7个私有方法）

---

## 五、总体统计

| 文件组 | 总方法数 | ✅完全迁移 | ⚠️部分迁移 | ❌未迁移 |
|--------|---------|-----------|-----------|---------|
| PresalesAction | 21+5私有 | 5 (24%) | 13 (62%) | 3+5私有 (31%) |
| ReportAction | 11+6私有 | 1 (9%) | 10 (91%) | 0+6私有 (35%) |
| SubcontractAction | 28+7私有 | 4 (14%) | 13 (46%) | 12+7私有 (54%) |
| **合计** | **78** | **10 (13%)** | **36 (46%)** | **32+18私有 (51%)** |

### 主要迁移风险

1. **问卷评分算法** (PresalesAction + SubcontractAction): queryQuesnaireScore/quesMark/getCbForm等核心算法完全未迁移，影响回访问卷功能
2. **复杂工作流路由** (SubcontractAction): savePayment的6种outcome路由、audit的审批路由等核心业务流程逻辑丢失
3. **ECharts图表渲染** (ReportAction): 所有报表的ECharts图表JSON构建逻辑未迁移，新系统仅返回原始数据
4. **权限控制** (全部): 原系统基于角色+areapower的细粒度权限控制在新系统中大幅简化
5. **多维度统计** (ReportAction.projectSummaryStatus): 极其复杂的动态多维度统计逻辑简化为简单分组
6. **发票识别** (SubcontractAction): 付款发票识别、去重、金额计算等复杂逻辑未迁移
