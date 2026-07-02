package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.Purchase;
import com.dp.plat.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 采购 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/d365/purchase")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping("/list")
    public R<IPage<Purchase>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(purchaseService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<Purchase> detail(@PathVariable Long id) {
        return R.ok(purchaseService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody Purchase entity) {
        purchaseService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody Purchase entity) {
        purchaseService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        purchaseService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<Purchase>> listAll() {
        return R.ok(purchaseService.listAll());
    }
}
