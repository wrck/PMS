package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.DispatchProject;
import com.dp.plat.service.DispatchProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 调度项目 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/dispatch/project")
public class DispatchProjectController {

    @Autowired
    private DispatchProjectService dispatchProjectService;

    @GetMapping("/list")
    public R<IPage<DispatchProject>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(dispatchProjectService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<DispatchProject> detail(@PathVariable Long id) {
        return R.ok(dispatchProjectService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody DispatchProject entity) {
        dispatchProjectService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody DispatchProject entity) {
        dispatchProjectService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        dispatchProjectService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<DispatchProject>> listAll() {
        return R.ok(dispatchProjectService.listAll());
    }
}
