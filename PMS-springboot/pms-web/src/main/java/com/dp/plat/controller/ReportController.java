package com.dp.plat.controller;

import com.dp.plat.common.result.R;
import com.dp.plat.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 报表统计控制器 - 迁移自老系统 ReportAction
 *
 * 源码: 1073行, 11个方法
 * 迁移映射:
 *   show()              -> GET /overview
 *   loadLineData()      -> GET /line-data
 *   loadLine_qualityData() -> GET /quality-line
 *   loadLine_implData() -> GET /impl-line
 *   assignedRate()      -> GET /assigned-rate
 *   traceRate()         -> GET /trace-rate
 *   closeRate()         -> GET /close-rate
 *   implRate()          -> GET /impl-rate
 *   quality()           -> GET /quality
 *   projectSummaryStatus() -> GET /summary-status
 *   input()             -> GET /custom
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /** 报表首页概览 */
    @GetMapping("/overview")
    public R<Map<String, Object>> overview() {
        return R.ok(reportService.getReportOverview());
    }

    /** 项目数量趋势折线图 */
    @GetMapping("/line-data")
    public R<List<Map<String, Object>>> lineData(@RequestParam(required = false) Map<String, Object> params) {
        return R.ok(reportService.loadLineData(params));
    }

    /** 质量趋势折线图 */
    @GetMapping("/quality-line")
    public R<List<Map<String, Object>>> qualityLine(@RequestParam(required = false) Map<String, Object> params) {
        return R.ok(reportService.loadQualityLineData(params));
    }

    /** 实施趋势折线图 */
    @GetMapping("/impl-line")
    public R<List<Map<String, Object>>> implLine(@RequestParam(required = false) Map<String, Object> params) {
        return R.ok(reportService.loadImplLineData(params));
    }

    /** 指派率统计 */
    @GetMapping("/assigned-rate")
    public R<Map<String, Object>> assignedRate(@RequestParam(required = false) Map<String, Object> params) {
        return R.ok(reportService.queryAssignedRate(params));
    }

    /** 跟踪率统计 */
    @GetMapping("/trace-rate")
    public R<Map<String, Object>> traceRate(@RequestParam(required = false) Map<String, Object> params) {
        return R.ok(reportService.queryTraceRate(params));
    }

    /** 闭环率统计 */
    @GetMapping("/close-rate")
    public R<Map<String, Object>> closeRate(@RequestParam(required = false) Map<String, Object> params) {
        return R.ok(reportService.queryCloseRate(params));
    }

    /** 实施率统计 */
    @GetMapping("/impl-rate")
    public R<Map<String, Object>> implRate(@RequestParam(required = false) Map<String, Object> params) {
        return R.ok(reportService.queryImplRate(params));
    }

    /** 质量统计 */
    @GetMapping("/quality")
    public R<Map<String, Object>> quality(@RequestParam(required = false) Map<String, Object> params) {
        return R.ok(reportService.queryQuality(params));
    }

    /** 项目汇总状态 */
    @GetMapping("/summary-status")
    public R<List<Map<String, Object>>> summaryStatus(@RequestParam(required = false) Map<String, Object> params) {
        return R.ok(reportService.queryProjectSummaryStatus(params));
    }

    /** 自定义报表查询 */
    @GetMapping("/custom")
    public R<Map<String, Object>> custom(@RequestParam(required = false) Map<String, Object> params) {
        return R.ok(reportService.queryCustomReport(params));
    }
}
