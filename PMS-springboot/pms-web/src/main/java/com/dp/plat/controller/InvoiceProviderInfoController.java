package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.InvoiceProviderInfo;
import com.dp.plat.service.InvoiceProviderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 发票供应商 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/invoice/provider")
public class InvoiceProviderInfoController {

    @Autowired
    private InvoiceProviderInfoService invoiceProviderInfoService;

    @GetMapping("/list")
    public R<IPage<InvoiceProviderInfo>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(invoiceProviderInfoService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<InvoiceProviderInfo> detail(@PathVariable Long id) {
        return R.ok(invoiceProviderInfoService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody InvoiceProviderInfo entity) {
        invoiceProviderInfoService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody InvoiceProviderInfo entity) {
        invoiceProviderInfoService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        invoiceProviderInfoService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<InvoiceProviderInfo>> listAll() {
        return R.ok(invoiceProviderInfoService.listAll());
    }
}
