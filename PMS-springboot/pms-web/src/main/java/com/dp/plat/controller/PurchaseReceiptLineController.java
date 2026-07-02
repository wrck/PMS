package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.PurchaseReceiptLine;
import com.dp.plat.service.PurchaseReceiptLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 采购收货行 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/d365/purchase-receipt-line")
public class PurchaseReceiptLineController {

    @Autowired
    private PurchaseReceiptLineService purchaseReceiptLineService;

    @GetMapping("/list")
    public R<IPage<PurchaseReceiptLine>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(purchaseReceiptLineService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<PurchaseReceiptLine> detail(@PathVariable Long id) {
        return R.ok(purchaseReceiptLineService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody PurchaseReceiptLine entity) {
        purchaseReceiptLineService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody PurchaseReceiptLine entity) {
        purchaseReceiptLineService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        purchaseReceiptLineService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<PurchaseReceiptLine>> listAll() {
        return R.ok(purchaseReceiptLineService.listAll());
    }
}
