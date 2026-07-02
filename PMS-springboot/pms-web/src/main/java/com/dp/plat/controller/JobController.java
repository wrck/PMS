package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.Job;
import com.dp.plat.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/ehr/job")
public class JobController {

    @Autowired
    private JobService jobService;

    @GetMapping("/list")
    public R<IPage<Job>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(jobService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<Job> detail(@PathVariable Long id) {
        return R.ok(jobService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody Job entity) {
        jobService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody Job entity) {
        jobService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        jobService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<Job>> listAll() {
        return R.ok(jobService.listAll());
    }
}
