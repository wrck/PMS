package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.IndustryLeak;
import com.dp.plat.service.IndustryLeakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 行业泄漏 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/industry/leak")
public class IndustryLeakController {

    @Autowired
    private IndustryLeakService industryLeakService;

    @GetMapping("/list")
    public R<IPage<IndustryLeak>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(industryLeakService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<IndustryLeak> detail(@PathVariable Long id) {
        return R.ok(industryLeakService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody IndustryLeak entity) {
        industryLeakService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody IndustryLeak entity) {
        industryLeakService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        industryLeakService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<IndustryLeak>> listAll() {
        return R.ok(industryLeakService.listAll());
    }
}
