package com.dp.plat.service;

import java.util.List;
import java.util.Map;

/**
 * 报表统计服务 - 迁移自老系统 ReportAction
 *
 * 源码: 1073行, 11个方法
 * 功能: 项目统计报表、质量统计、实施率统计等
 */
public interface ReportService {

    /**
     * 报表首页数据
     * 迁移自: ReportAction.show()
     */
    Map<String, Object> getReportOverview();

    /**
     * 加载折线数据(项目数量趋势)
     * 迁移自: ReportAction.loadLineData()
     */
    List<Map<String, Object>> loadLineData(Map<String, Object> params);

    /**
     * 质量折线数据
     * 迁移自: ReportAction.loadLine_qualityData()
     */
    List<Map<String, Object>> loadQualityLineData(Map<String, Object> params);

    /**
     * 实施折线数据
     * 迁移自: ReportAction.loadLine_implData()
     */
    List<Map<String, Object>> loadImplLineData(Map<String, Object> params);

    /**
     * 指派率统计
     * 迁移自: ReportAction.assignedRate()
     */
    Map<String, Object> queryAssignedRate(Map<String, Object> params);

    /**
     * 跟踪率统计
     * 迁移自: ReportAction.traceRate()
     */
    Map<String, Object> queryTraceRate(Map<String, Object> params);

    /**
     * 闭环率统计
     * 迁移自: ReportAction.closeRate()
     */
    Map<String, Object> queryCloseRate(Map<String, Object> params);

    /**
     * 实施率统计
     * 迁移自: ReportAction.implRate()
     */
    Map<String, Object> queryImplRate(Map<String, Object> params);

    /**
     * 质量统计
     * 迁移自: ReportAction.quality()
     */
    Map<String, Object> queryQuality(Map<String, Object> params);

    /**
     * 项目汇总状态
     * 迁移自: ReportAction.projectSummaryStatus()
     */
    List<Map<String, Object>> queryProjectSummaryStatus(Map<String, Object> params);

    /**
     * 报表输入(自定义查询)
     * 迁移自: ReportAction.input()
     */
    Map<String, Object> queryCustomReport(Map<String, Object> params);
}
