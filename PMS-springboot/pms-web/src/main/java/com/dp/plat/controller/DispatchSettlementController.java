package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.DispatchSettlement;
import com.dp.plat.service.DispatchSettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 调度结算 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/dispatch/settlement")
public class DispatchSettlementController {

    @Autowired
    private DispatchSettlementService dispatchSettlementService;

    @GetMapping("/list")
    public R<IPage<DispatchSettlement>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(dispatchSettlementService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<DispatchSettlement> detail(@PathVariable Long id) {
        return R.ok(dispatchSettlementService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody DispatchSettlement entity) {
        dispatchSettlementService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody DispatchSettlement entity) {
        dispatchSettlementService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        dispatchSettlementService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<DispatchSettlement>> listAll() {
        return R.ok(dispatchSettlementService.listAll());
    }
}
