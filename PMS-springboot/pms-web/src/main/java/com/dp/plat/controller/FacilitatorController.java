package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.Facilitator;
import com.dp.plat.service.FacilitatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 服务商 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/facilitator")
public class FacilitatorController {

    @Autowired
    private FacilitatorService facilitatorService;

    @GetMapping("/list")
    public R<IPage<Facilitator>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(facilitatorService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<Facilitator> detail(@PathVariable Long id) {
        return R.ok(facilitatorService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody Facilitator entity) {
        facilitatorService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody Facilitator entity) {
        facilitatorService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        facilitatorService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<Facilitator>> listAll() {
        return R.ok(facilitatorService.listAll());
    }
}
