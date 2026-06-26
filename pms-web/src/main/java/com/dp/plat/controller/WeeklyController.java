package com.dp.plat.controller;

import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.PmsProjectWeekly;
import com.dp.plat.model.entity.WeeklyContent;
import com.dp.plat.model.entity.WeeklyFeedback;
import com.dp.plat.service.WeeklyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/project/weekly")
public class WeeklyController {
    @Autowired
    private WeeklyService weeklyService;

    @GetMapping("/list")
    public R<List<PmsProjectWeekly>> list(@RequestParam Long projectId,
                                           @RequestParam(defaultValue = "-1") Integer weeklyState) {
        return R.ok(weeklyService.queryWeeklyList(projectId, weeklyState));
    }

    @GetMapping("/{id}")
    public R<PmsProjectWeekly> detail(@PathVariable Long id) {
        return R.ok(weeklyService.getWeeklyDetail(id));
    }

    @PostMapping
    public R<Long> add(@RequestBody PmsProjectWeekly weekly, HttpServletRequest request) {
        weekly.setCreateBy((String) request.getAttribute("currentUsername"));
        return R.ok(weeklyService.createWeekly(weekly));
    }

    @PutMapping
    public R<Void> update(@RequestBody PmsProjectWeekly weekly) {
        weeklyService.updateWeekly(weekly);
        return R.ok();
    }

    @PostMapping("/{id}/submit")
    public R<Void> submit(@PathVariable Long id) {
        weeklyService.submitWeekly(id);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        weeklyService.deleteWeekly(id);
        return R.ok();
    }

    @GetMapping("/{id}/contents")
    public R<List<WeeklyContent>> contents(@PathVariable Long id,
                                            @RequestParam(required = false) Integer optionType) {
        return R.ok(weeklyService.queryWeeklyContents(id, optionType));
    }

    @PostMapping("/{id}/contents")
    public R<Void> saveContents(@PathVariable Long id, @RequestBody List<WeeklyContent> contents) {
        weeklyService.saveWeeklyContents(id, contents);
        return R.ok();
    }

    @GetMapping("/{id}/feedbacks")
    public R<List<WeeklyFeedback>> feedbacks(@PathVariable Long id) {
        return R.ok(weeklyService.queryFeedbacks(id));
    }

    @PostMapping("/feedback")
    public R<Void> addFeedback(@RequestBody WeeklyFeedback feedback, HttpServletRequest request) {
        feedback.setFeedbacker((String) request.getAttribute("currentUsername"));
        weeklyService.addFeedback(feedback);
        return R.ok();
    }
}
