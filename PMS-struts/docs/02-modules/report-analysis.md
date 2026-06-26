# 报表分析功能说明文档

## 1. 模块概述

报表分析模块为PMS系统提供数据统计和报表展示能力，涵盖项目统计综述、项目经理指派率、跟踪率、闭环新增比、实施方式占比、质量管理、项目汇总状态等多维度数据分析，以及回访数据信息统计。支持按办事处、时间等条件进行数据聚合，使用ECharts图表展示，并通过定时任务保存趋势数据，为管理层提供决策支持。

### 涉及的Action类列表

| Action类 | 包路径 | 职责 |
|----------|--------|------|
| `ReportAction` | `com.dp.plat.action` | 报表统计展示（指派率/跟踪率/闭环比/实施方式/质量管理/项目汇总状态） |
| `DataAnalysisAction` | `com.dp.plat.action` | 回访数据信息统计 |

### 涉及的Service类列表

| Service类 | 事务代理Bean | 依赖DAO |
|-----------|-------------|---------|
| `ReportServiceImpl` | `reportServiceAgent` | `ReportDao` |
| `DataAnalysisServiceImpl` | `dataAnalysisServiceAgent` | `DataAnalysisDao` |

### 涉及的数据库表列表

| 表名 | 说明 |
|------|------|
| `pm_project_header` | 项目信息主表（只读查询） |
| `pm_project_member` | 项目成员表（只读查询） |
| `pm_project_state` | 项目状态表（只读查询） |
| `fnd_department` | 部门表（只读查询） |
| `pm_cl_evaluation_header` | 闭环评估头表（只读查询） |
| `pm_cl_quesnaire_result_header` | 问卷结果头表（只读查询） |
| `pm_cl_quesnaire_result_line` | 问卷结果行表（只读查询） |
| `pm_cl_quesnaire_template_line` | 问卷模板行表（只读查询） |
| `pm_cl_quesnaire_template_options` | 问卷模板选项表（只读查询） |
| `pm_report_line_data` | 报表趋势数据表（读写） |
| `pm_project_group_relationship` | 项目组关系表（只读查询） |
| `pm_project_contract` | 项目合同表（只读查询） |
| `fnd_company` | 公司表（只读查询） |
| `pm_cl_callback` | 回访申请表（只读查询） |
| `pm_cl_callback_quesnaire` | 回访问卷表（只读查询） |
| `fnd_act_hi_comment` | 流程审批评论表（只读查询） |
| `act_hi_taskinst` | Activiti历史任务表（只读查询） |

### 依赖的其他模块

- 项目管理模块（项目数据源）
- 闭环管理模块（评估/问卷数据源）
- 回访管理模块（回访数据源）
- 系统管理模块（用户信息、部门信息、基础数据）

## 2. 业务流程

### 2.1 报表展示流程

```
[报表展示页面] ──> [加载综述数据] ──> [加载各维度数据] ──> [ECharts图表渲染]
      |                  |                  |                    |
 ReportAction       ReportAction       ReportAction         前端ECharts
 .show()            .queryStatistics   .queryReportTable    .渲染柱状图/折线图
                    Summarize()        AssignedData()
                                       .queryReportTable
                                       TraceData()
                                       .queryReportTable
                                       ClosedData()
```

### 2.2 趋势图加载流程

```
[选择办事处/数据类型] ──> [请求趋势数据] ──> [返回JSON] ──> [ECharts渲染]
        |                      |                |              |
    前端交互              ReportAction       ReportAction    前端ECharts
                    .loadLineData()    返回data字段
                    .loadLine_qualityData()
                    .loadLine_implData()
```

### 2.3 定时趋势数据保存流程

```
[定时任务触发] ──> [统计各维度数据] ──> [批量写入趋势表]
      |                  |                    |
  Spring定时器     ReportServiceImpl     ReportDao
                  .keepReportLineData() .insertReportLineDataByList()
```

### 2.4 回访数据统计流程

```
[设置查询条件] ──> [执行查询] ──> [展示结果]
      |                |              |
 DataAnalysisAction  DataAnalysis    页面展示
 .execute()         Service
                    .quesyCbDataList()
```

## 3. 接口文档

### 3.1 报表展示页面

| 项目 | 说明 |
|------|------|
| URL | /module/report_show.action |
| HTTP方法 | GET |
| 功能描述 | 报表展示页面，展示全国项目统计综述、项目经理指派率、跟踪率、闭环新增比 |
| 权限要求 | 管理员/工程管理部/工程管理部领导/财务人员/项目管理员/回访人员 |

**处理逻辑**：
1. 查询选项卡数据 → `basicDataService.queryBasicDataBeans()`
2. 查询办事处列表 → `departmentManageService.queryDepartments()`
3. 查询全国项目统计综述 → `reportService.queryStatisticsSummarize()`
4. 查询指派率表格数据 → `reportService.queryReportTableAssignedData()`
5. 查询跟踪率表格数据 → `reportService.queryReportTableTraceData()`
6. 查询闭环比表格数据 → `reportService.queryReportTableClosedData()`

### 3.2 加载趋势图

| 项目 | 说明 |
|------|------|
| URL | /ajax/loadLineData.action |
| HTTP方法 | GET |
| 功能描述 | 加载指定办事处和数据类型的趋势图（折线图） |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| officeCode | String | 否 | - | total | 办事处编码 |
| dataTypeCode | String | 否 | - | 无 | 数据类型编码 |

**返回结果**：

| result名 | 类型 | 跳转页面/JSON结构 | 说明 |
|----------|------|-------------------|------|
| SUCCESS | String | ECharts JSON数据 | 返回data字段包含ECharts配置JSON |

### 3.3 项目指派率查询

| 项目 | 说明 |
|------|------|
| URL | /report_assignedRate.action |
| HTTP方法 | GET |
| 功能描述 | 查询各办事处项目经理指派率，返回柱状图数据 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| queryParam.startTime | Date | 否 | - | 本年1月1日 | 统计开始日期 |
| queryParam.quarterStartTime | Date | 否 | - | 季度首日 | 季度开始日期 |

**返回结果**：SUCCESS → 返回ECharts柱状图JSON数据

**处理逻辑**：
1. 查询指派率报表数据 → `reportService.queryReportLineAssignedData()`
2. 统计全国汇总 → `reportService.statisticsTotalData()`
3. 封装ECharts柱状图 → `EchartsUtil.packagingOneBarEcharts()`

### 3.4 项目经理跟踪率

| 项目 | 说明 |
|------|------|
| URL | /report_traceRate.action |
| HTTP方法 | GET |
| 功能描述 | 查询各办事处项目经理跟踪率，返回柱状图数据 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| queryParam.startTime | Date | 否 | - | 本年1月1日 | 统计开始日期 |
| queryParam.quarterStartTime | Date | 否 | - | 季度首日 | 季度开始日期 |

**返回结果**：SUCCESS → 返回ECharts柱状图JSON数据

### 3.5 季度新增闭环比

| 项目 | 说明 |
|------|------|
| URL | /report_closeRate.action |
| HTTP方法 | GET |
| 功能描述 | 查询各办事处季度新增闭环比，返回柱状图数据 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| queryParam.startTime | Date | 否 | - | 本年1月1日 | 统计开始日期 |
| queryParam.quarterStartTime | Date | 否 | - | 季度首日 | 季度开始日期 |

**返回结果**：SUCCESS → 返回ECharts柱状图JSON数据

### 3.6 项目实施方式占比

| 项目 | 说明 |
|------|------|
| URL | /report_implRate.action |
| HTTP方法 | GET |
| 功能描述 | 查询企业网项目各类实施方式占比（原厂直服/原厂督导/代理商自服），返回柱状图数据 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| queryParam.startTime | Date | 否 | - | 本年1月1日 | 统计开始日期 |
| queryParam.quarterStartTime | Date | 否 | - | 季度首日 | 季度开始日期 |

**返回结果**：SUCCESS → 返回ECharts分组柱状图JSON数据

**处理逻辑**：
1. 查询全部项目数量 → `reportService.queryImplWayMap(queryParam, IMPL_WAY_ALL)`
2. 查询原厂直服数量 → `reportService.queryImplWayMap(queryParam, IMPL_WAY_0)`
3. 查询原厂督导数量 → `reportService.queryImplWayMap(queryParam, IMPL_WAY_1)`
4. 查询代理商自服数量 → `reportService.queryImplWayMap(queryParam, IMPL_WAY_3)`
5. 计算各办事处占比并封装ECharts数据

### 3.7 项目质量

| 项目 | 说明 |
|------|------|
| URL | /report_quality.action |
| HTTP方法 | GET |
| 功能描述 | 查询各办事处闭环平均得分和闭环项目数量，返回柱状图数据 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| queryParam.startTime | Date | 否 | - | 季度首日 | 统计开始日期 |
| queryParam.quarterStartTime | Date | 否 | - | 季度首日 | 季度开始日期 |

**返回结果**：SUCCESS → 返回data字段（闭环平均得分）和dataJson字段（闭环项目数量）

**处理逻辑**：
1. 查询全部和剩余质量数据 → `reportService.queryTotalAndRemainderList()`
2. 封装闭环平均得分柱状图 → `EchartsUtil.packagingBarEcharts()`
3. 封装闭环项目数量柱状图 → `EchartsUtil.packagingBarEcharts()`

### 3.8 项目汇总状态

| 项目 | 说明 |
|------|------|
| URL | /module/report_projectSummaryStatus.action |
| HTTP方法 | GET |
| 功能描述 | 项目汇总状态统计页面，按实施状态、流程状态等维度汇总项目数量 |
| 权限要求 | 所有角色可访问（非管理员仅查看有权限的区域） |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| dataJson | String | 否 | JSON格式 | 无 | 查询条件JSON |
| data | String | 否 | - | 无 | 值为"info"时返回明细列表 |

**返回结果**：

| result名 | 类型 | 跳转页面/JSON结构 | 说明 |
|----------|------|-------------------|------|
| SUCCESS | String | JSON数据 | 子页面请求时返回JSON |
| projectSummaryStatus | String | 项目汇总状态页面 | 主页面请求时返回页面 |

**处理逻辑**：
1. 读取汇总配置 → `basicDataService.querySysArg("pm.report.project.summary.status")`
2. 查询项目汇总数据 → `reportService.queryProjectSummaryStatus()`
3. 按配置维度统计项目数量（实施状态/流程状态等）
4. 生成HTML表格数据

### 3.9 回访数据信息统计

| 项目 | 说明 |
|------|------|
| URL | /module/DataAnalysis.action |
| HTTP方法 | GET |
| 功能描述 | 回访数据信息统计页面，展示回访项目评分、问卷结果等 |

**输入参数**：

| 参数名 | 类型 | 必填 | 校验规则 | 默认值 | 业务含义 |
|--------|------|------|----------|--------|----------|
| dataQueryParam.cbStartTime | Date | 否 | - | 无 | 回访开始日期 |
| dataQueryParam.cbEndTime | Date | 否 | - | 无 | 回访结束日期 |
| dataQueryParam.serviceType | String | 否 | - | 无 | 服务类型 |
| dataQueryParam.officeCode | String | 否 | - | 无 | 办事处编码 |
| dataQueryParam.compId | String | 否 | - | 无 | 公司ID |

**返回结果**：

| result名 | 类型 | 跳转页面/JSON结构 | 说明 |
|----------|------|-------------------|------|
| SUCCESS | String | 回访数据统计页面 | 查询成功 |
| ERROR | String | 错误页 | 查询失败 |

**处理逻辑**：
1. 加载基础数据（公司/办事处/项目阶段/服务类型/项目类型）
2. 查询回访数据列表 → `dataAnalysisService.quesyCbDataList()`

## 4. Service层详解

### 4.1 ReportServiceImpl.queryStatisticsSummarize()

- **功能描述**：查询全国项目统计综述
- **事务类型**：无事务（query*前缀）
- **核心算法**：
  1. 查询项目总数 → `reportDao.queryTotalNum()`
  2. 查询工程类项目数 → `reportDao.queryEngineeringTypeNum()`
  3. 查询常规类项目数 → `reportDao.queryCommonTypeNum()`
  4. 查询已指派项目数 → `reportDao.queryAssignedNum()`
  5. 查询已跟踪项目数 → `reportDao.queryTraceNum()`
  6. 构造StatisticsSummarize对象返回
- **调用的DAO方法**：`reportDao.queryTotalNum()`, `reportDao.queryEngineeringTypeNum()`, `reportDao.queryCommonTypeNum()`, `reportDao.queryAssignedNum()`, `reportDao.queryTraceNum()`

### 4.2 ReportServiceImpl.queryAssignedRate(ReportQueryParam)

- **功能描述**：查询项目指派率
- **事务类型**：无事务
- **核心算法**：
  1. 查询所有项目数量（按办事处分组） → `reportDao.queryAssignedRate(isALl=0)`
  2. 查询已指派项目经理的项目数量 → `reportDao.queryAssignedRate(isALl=1)`
  3. 计算指派率 = 已指派数 / 总数 * 100%
  4. 汇总全国指派率
- **调用的DAO方法**：`reportDao.queryAssignedRate()`

### 4.3 ReportServiceImpl.queryTraceRate(ReportQueryParam)

- **功能描述**：查询项目跟踪率
- **事务类型**：无事务
- **核心算法**：
  1. 查询已指派项目经理的项目数量 → `reportDao.queryTraceRate(isALl=0)`
  2. 查询已跟踪的项目数量（projectPlanState != 40） → `reportDao.queryTraceRate(isALl=1)`
  3. 计算跟踪率 = 已跟踪数 / 已指派数 * 100%
  4. 汇总全国跟踪率
- **调用的DAO方法**：`reportDao.queryTraceRate()`

### 4.4 ReportServiceImpl.queryCloseRate(ReportQueryParam)

- **功能描述**：查询闭环新增比
- **事务类型**：无事务
- **核心算法**：
  1. 查询闭环项目数量 → `reportDao.queryCloseMap()`
  2. 查询新增项目数量 → `reportDao.queryNewMap()`
  3. 计算闭环比 = 闭环数 / 新增数 * 100%
  4. 汇总全国闭环比
- **调用的DAO方法**：`reportDao.queryCloseMap()`, `reportDao.queryNewMap()`

### 4.5 ReportServiceImpl.queryImplWayMap(ReportQueryParam, int)

- **功能描述**：查询实施方式项目数量
- **事务类型**：无事务
- **核心算法**：
  1. 根据implWay参数查询对应实施方式的项目数量（按办事处分组）
- **调用的DAO方法**：`reportDao.queryImplWayMap()`

### 4.6 ReportServiceImpl.queryTotalAndRemainderList(ReportQueryParam)

- **功能描述**：查询全部项目和剩余（除去非直签督导/代理商自服）项目的质量数据
- **事务类型**：无事务
- **核心算法**：
  1. 创建临时表quality → `reportDao.createQualityTmpTable()`
  2. 查询全部项目各办事处闭环平均分和数量 → `reportDao.queryOfficeQuality()`
  3. 查询没有闭环项目的办事处 → `reportDao.queryOtherOfficeQuality()`
  4. 查询全国质量数据 → `reportDao.queryTotalQuality()`
  5. 设置过滤条件（原厂直服/非直签/代理商自服），再次查询剩余项目数据
  6. 删除临时表 → `reportDao.deleteQualityTmpTable()`
- **调用的DAO方法**：`reportDao.createQualityTmpTable()`, `reportDao.queryOfficeQuality()`, `reportDao.queryOtherOfficeQuality()`, `reportDao.queryTotalQuality()`, `reportDao.deleteQualityTmpTable()`

### 4.7 ReportServiceImpl.keepReportLineData()

- **功能描述**：定时保存报表趋势数据（由定时任务调用）
- **事务类型**：无事务
- **核心算法**：
  1. 统计项目经理指派率 → `queryReportLineAssignedData()` + `statisticsTotalData()`
  2. 统计项目经理跟踪率 → `queryReportLineTraceData()` + `statisticsTotalData()`
  3. 统计闭环新增比 → `queryReportLineClosedData()` + `statisticsTotalData()`
  4. 统计企业网项目实施方式占比 → `queryReportLineImplData()`
  5. 统计质量管理数据 → `queryReportLineRemainderQualityDataAndTotalsize()`
  6. 批量写入pm_report_line_data表 → `insertReportLineDataByList()`
- **调用的DAO方法**：通过内部Service方法调用各query和insert方法

### 4.8 ReportServiceImpl.queryProjectSummaryStatus(Map)

- **功能描述**：查询项目汇总状态
- **事务类型**：无事务
- **核心算法**：
  1. 查询项目汇总数据（关联项目组关系、合同、状态、部门表）
- **调用的DAO方法**：`reportDao.queryProjectSummaryStatus()`

### 4.9 DataAnalysisServiceImpl.quesyCbDataList(DataQueryParam)

- **功能描述**：查询回访数据列表
- **事务类型**：无事务
- **核心算法**：
  1. 查询闭环评估回访数据和回访问卷数据（UNION合并）
- **调用的DAO方法**：`dataAnalysisDao.quesyCbDataList()`

## 5. 数据操作

### 5.1 本模块涉及的数据库表及CRUD操作

| 表名 | CREATE | READ | UPDATE | DELETE |
|------|--------|------|--------|--------|
| pm_project_header | - | ✓ 聚合查询 | - | - |
| pm_project_member | - | ✓ 关联查询 | - | - |
| pm_project_state | - | ✓ 关联查询 | - | - |
| fnd_department | - | ✓ 关联查询 | - | - |
| pm_cl_evaluation_header | - | ✓ 聚合查询 | - | - |
| pm_cl_quesnaire_result_header | - | ✓ 关联查询 | - | - |
| pm_cl_quesnaire_result_line | - | ✓ 聚合查询 | - | - |
| pm_cl_quesnaire_template_line | - | ✓ 关联查询 | - | - |
| pm_cl_quesnaire_template_options | - | ✓ 关联查询 | - | - |
| pm_report_line_data | ✓ 批量插入 | ✓ 趋势查询 | - | - |
| pm_project_group_relationship | - | ✓ 关联查询 | - | - |
| pm_project_contract | - | ✓ 关联查询 | - | - |
| fnd_company | - | ✓ 关联查询 | - | - |
| pm_cl_callback | - | ✓ 关联查询 | - | - |
| pm_cl_callback_quesnaire | - | ✓ 关联查询 | - | - |
| fnd_act_hi_comment | - | ✓ 关联查询 | - | - |
| act_hi_taskinst | - | ✓ 关联查询 | - | - |

> 注：报表模块仅对pm_report_line_data表进行写入（定时保存趋势数据），其余表均为只读聚合查询。质量管理查询使用临时表quality（CREATE TEMPORARY TABLE / DROP TEMPORARY TABLE）。

### 5.2 数据校验规则

| 数据对象 | 校验字段 | 校验规则 | 错误提示 |
|----------|----------|----------|----------|
| ReportQueryParam | startTime | 早于quarterStartTime | 开始日期不能晚于季度开始日期 |

### 5.3 数据生命周期

报表趋势数据（pm_report_line_data）由定时任务定期写入，按月保存各维度统计结果。其余数据为实时聚合查询结果，不持久化存储。质量管理查询使用临时表quality，查询完成后自动删除。

### 5.4 数据转换规则

| 转换场景 | 源格式 | 目标格式 | 说明 |
|----------|--------|----------|------|
| 百分比计算 | 整数/整数 | 百分比字符串 | 保留2位小数（DecimalFormat("###.00")） |
| 日期格式化 | Date | yyyy-MM | 趋势图X轴标签 |
| 日期范围 | Date区间 | SQL BETWEEN条件 | 含边界 |

## 6. 业务规则

| 规则编号 | 规则描述 | 触发条件 | 执行逻辑 |
|----------|----------|----------|----------|
| RP-001 | 区域权限数据过滤 | 查询报表时 | 非管理员重定向至项目汇总状态页面 |
| RP-002 | 默认日期范围 | 未指定日期时 | 默认本年1月1日（startTime）、季度首日（quarterStartTime） |
| RP-003 | 项目指派率计算 | 指派率统计时 | 已指派项目经理的项目数 / 有效项目总数 * 100% |
| RP-004 | 项目跟踪率计算 | 跟踪率统计时 | 已跟踪项目数（projectPlanState != 40）/ 已指派项目数 * 100% |
| RP-005 | 闭环新增比计算 | 闭环比统计时 | 闭环项目数 / 新增项目数 * 100% |
| RP-006 | 实施方式分类 | 实施方式统计时 | 原厂直服(IMPL_WAY_0)/原厂督导(IMPL_WAY_1)/代理商自服(IMPL_WAY_3) |
| RP-007 | 质量管理过滤 | 质量统计时 | 除去非直签督导和代理商/用户自服项目 |
| RP-008 | 项目汇总状态配置 | 项目汇总统计时 | 通过系统参数pm.report.project.summary.status配置统计维度 |
| RP-009 | 有效项目条件 | 统计查询时 | projectState IN (30,31,32) 且 effectiveTo IS NULL |
| RP-010 | 闭环项目条件 | 质量统计时 | projectState = 100 |

## 7. 配置项

| 配置项 | 配置Key | 默认值 | 说明 |
|--------|---------|--------|------|
| 项目汇总状态配置 | pm.report.project.summary.status | JSON配置字符串 | 定义汇总统计维度和条件 |
| 实施状态映射关系 | pm.report.project.summary.executionState.relation | JSON配置字符串 | 定义实施状态值的分组映射 |
| 数据类型编码 | 硬编码（ReportDataTypeParam） | - | REPORT_ASSIGNED_RATE/REPORT_TRACE_RATE/REPORT_CLOSE_RATE/REPORT_QUALITY_RATE |
| 质量图Y轴范围 | 硬编码 | min:69, max:100 | 闭环平均得分Y轴范围 |
