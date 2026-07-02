package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.PurchaseReceipt;
import com.dp.plat.service.PurchaseReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 采购收货 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/d365/purchase-receipt")
public class PurchaseReceiptController {

    @Autowired
    private PurchaseReceiptService purchaseReceiptService;

    @GetMapping("/list")
    public R<IPage<PurchaseReceipt>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(purchaseReceiptService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<PurchaseReceipt> detail(@PathVariable Long id) {
        return R.ok(purchaseReceiptService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody PurchaseReceipt entity) {
        purchaseReceiptService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody PurchaseReceipt entity) {
        purchaseReceiptService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        purchaseReceiptService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<PurchaseReceipt>> listAll() {
        return R.ok(purchaseReceiptService.listAll());
    }
}
