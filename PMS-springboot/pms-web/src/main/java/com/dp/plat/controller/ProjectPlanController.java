package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.PmsProjectPlan;
import com.dp.plat.service.ProjectPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目计划 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/project/plan")
public class ProjectPlanController {

    @Autowired
    private ProjectPlanService projectPlanService;

    @GetMapping("/list")
    public R<IPage<PmsProjectPlan>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(projectPlanService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<PmsProjectPlan> detail(@PathVariable Long id) {
        return R.ok(projectPlanService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody PmsProjectPlan entity) {
        projectPlanService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody PmsProjectPlan entity) {
        projectPlanService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        projectPlanService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<PmsProjectPlan>> listAll() {
        return R.ok(projectPlanService.listAll());
    }
}
