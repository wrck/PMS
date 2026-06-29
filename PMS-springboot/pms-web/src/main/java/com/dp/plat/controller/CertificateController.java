package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.PmsCertificate;
import com.dp.plat.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/certificate")
public class CertificateController {
    @Autowired
    private CertificateService service;

    @GetMapping("/list")
    public R<IPage<PmsCertificate>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                          @RequestParam(required = false) String barcode) {
        return R.ok(service.queryPage(pageNum, pageSize, barcode));
    }

    @GetMapping("/barcode/{barcode}")
    public R<PmsCertificate> getByBarcode(@PathVariable String barcode) {
        return R.ok(service.getByBarcode(barcode));
    }

    @PostMapping
    public R<Void> add(@RequestBody PmsCertificate c) {
        service.create(c);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return R.ok();
    }
}
