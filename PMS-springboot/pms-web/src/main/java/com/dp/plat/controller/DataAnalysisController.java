package com.dp.plat.controller;

import com.dp.plat.common.result.R;
import com.dp.plat.model.dto.DataQueryParam;
import com.dp.plat.model.vo.CbDataVO;
import com.dp.plat.service.DataAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据分析控制器 - 迁移自老系统 DataAnalysisAction
 */
@RestController
@RequestMapping("/api/data-analysis")
public class DataAnalysisController {

    @Autowired
    private DataAnalysisService dataAnalysisService;

    /** 查询回访数据列表 */
    @GetMapping("/cb-data")
    public R<List<CbDataVO>> queryCbDataList(DataQueryParam queryParam) {
        return R.ok(dataAnalysisService.queryCbDataList(queryParam));
    }

    /** 分析概览 */
    @GetMapping("/overview")
    public R<Map<String, Object>> overview(DataQueryParam queryParam) {
        return R.ok(dataAnalysisService.overview(queryParam));
    }

    /** 项目状态分布 */
    @GetMapping("/project-status")
    public R<List<Map<String, Object>>> projectStatus(DataQueryParam queryParam) {
        return R.ok(dataAnalysisService.projectStatus(queryParam));
    }

    /** 办事处维度分析 */
    @GetMapping("/by-office")
    public R<List<Map<String, Object>>> byOffice(DataQueryParam queryParam) {
        return R.ok(dataAnalysisService.byOffice(queryParam));
    }

    /** 时间维度分析 */
    @GetMapping("/by-time")
    public R<List<Map<String, Object>>> byTime(DataQueryParam queryParam) {
        return R.ok(dataAnalysisService.byTime(queryParam));
    }

    /** 自定义查询 */
    @PostMapping("/custom-query")
    public R<List<Map<String, Object>>> customQuery(@RequestBody Map<String, Object> queryParams) {
        return R.ok(dataAnalysisService.customQuery(queryParams));
    }
}
