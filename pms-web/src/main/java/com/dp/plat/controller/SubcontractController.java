package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.PmsSubcontract;
import com.dp.plat.service.SubcontractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subcontract")
public class SubcontractController {
    @Autowired
    private SubcontractService subcontractService;

    @GetMapping("/list")
    public R<IPage<PmsSubcontract>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                          @RequestParam(required = false) String subcontractName,
                                          @RequestParam(required = false) String officeCode,
                                          @RequestParam(required = false) Integer state) {
        return R.ok(subcontractService.queryPage(pageNum, pageSize, subcontractName, officeCode, state));
    }

    @GetMapping("/{id}")
    public R<PmsSubcontract> detail(@PathVariable Long id) {
        return R.ok(subcontractService.getDetail(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody PmsSubcontract subcontract) {
        subcontractService.create(subcontract);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody PmsSubcontract subcontract) {
        subcontractService.update(subcontract);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        subcontractService.delete(id);
        return R.ok();
    }
}
