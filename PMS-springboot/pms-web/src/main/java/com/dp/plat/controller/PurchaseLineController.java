package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.PurchaseLine;
import com.dp.plat.service.PurchaseLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 采购行 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/d365/purchase-line")
public class PurchaseLineController {

    @Autowired
    private PurchaseLineService purchaseLineService;

    @GetMapping("/list")
    public R<IPage<PurchaseLine>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(purchaseLineService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<PurchaseLine> detail(@PathVariable Long id) {
        return R.ok(purchaseLineService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody PurchaseLine entity) {
        purchaseLineService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody PurchaseLine entity) {
        purchaseLineService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        purchaseLineService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<PurchaseLine>> listAll() {
        return R.ok(purchaseLineService.listAll());
    }
}
