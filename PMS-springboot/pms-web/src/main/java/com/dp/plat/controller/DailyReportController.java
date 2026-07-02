package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.DailyReport;
import com.dp.plat.service.DailyReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 日报 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/project/daily-report")
public class DailyReportController {

    @Autowired
    private DailyReportService dailyReportService;

    @GetMapping("/list")
    public R<IPage<DailyReport>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(dailyReportService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<DailyReport> detail(@PathVariable Long id) {
        return R.ok(dailyReportService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody DailyReport entity) {
        dailyReportService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody DailyReport entity) {
        dailyReportService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        dailyReportService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<DailyReport>> listAll() {
        return R.ok(dailyReportService.listAll());
    }
}
