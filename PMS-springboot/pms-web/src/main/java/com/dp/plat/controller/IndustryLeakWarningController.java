package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.IndustryLeakWarning;
import com.dp.plat.service.IndustryLeakWarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 行业泄漏预警 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/industry/leak-warning")
public class IndustryLeakWarningController {

    @Autowired
    private IndustryLeakWarningService industryLeakWarningService;

    @GetMapping("/list")
    public R<IPage<IndustryLeakWarning>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(industryLeakWarningService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<IndustryLeakWarning> detail(@PathVariable Long id) {
        return R.ok(industryLeakWarningService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody IndustryLeakWarning entity) {
        industryLeakWarningService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody IndustryLeakWarning entity) {
        industryLeakWarningService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        industryLeakWarningService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<IndustryLeakWarning>> listAll() {
        return R.ok(industryLeakWarningService.listAll());
    }
}
