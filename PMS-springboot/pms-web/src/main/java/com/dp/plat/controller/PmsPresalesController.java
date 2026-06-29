package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.PmsPresales;
import com.dp.plat.service.PmsPresalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/presales")
public class PmsPresalesController {

    @Autowired
    private PmsPresalesService presalesService;

    @GetMapping("/list")
    public R<IPage<PmsPresales>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestParam(required = false) String presalesCode,
                                       @RequestParam(required = false) String projectName,
                                       @RequestParam(required = false) Integer applyState,
                                       @RequestParam(required = false) String officeCode) {
        return R.ok(presalesService.queryPresalesPage(pageNum, pageSize, presalesCode, projectName, applyState, officeCode));
    }

    @GetMapping("/{id}")
    public R<PmsPresales> detail(@PathVariable Long id) {
        return R.ok(presalesService.getPresalesDetail(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody PmsPresales presales) {
        presalesService.createPresales(presales);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody PmsPresales presales) {
        presalesService.updatePresales(presales);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        presalesService.deletePresales(id);
        return R.ok();
    }

    @PostMapping("/{id}/start-flow")
    public R<Void> startFlow(@PathVariable Long id) {
        presalesService.startFlow(id);
        return R.ok();
    }

    @PostMapping("/{id}/approve")
    public R<Void> approve(@PathVariable Long id,
                            @RequestParam String comment,
                            @RequestParam boolean approved) {
        presalesService.approve(id, comment, approved);
        return R.ok();
    }
}
