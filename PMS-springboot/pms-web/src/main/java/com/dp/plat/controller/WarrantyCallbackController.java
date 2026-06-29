package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.PmsWarrantyCallback;
import com.dp.plat.service.WarrantyCallbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warranty-callback")
public class WarrantyCallbackController {

    @Autowired
    private WarrantyCallbackService warrantyCallbackService;

    @GetMapping("/list")
    public R<IPage<PmsWarrantyCallback>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                               @RequestParam(defaultValue = "10") Integer pageSize,
                                               @RequestParam(required = false) Long projectId,
                                               @RequestParam(required = false) String officeCode) {
        return R.ok(warrantyCallbackService.queryPage(pageNum, pageSize, projectId, officeCode));
    }

    @GetMapping("/{id}")
    public R<PmsWarrantyCallback> detail(@PathVariable Long id) {
        return R.ok(warrantyCallbackService.getDetail(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody PmsWarrantyCallback callback) {
        warrantyCallbackService.create(callback);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody PmsWarrantyCallback callback) {
        warrantyCallbackService.update(callback);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        warrantyCallbackService.delete(id);
        return R.ok();
    }

    @GetMapping("/project/{projectId}")
    public R<List<PmsWarrantyCallback>> byProject(@PathVariable Long projectId) {
        return R.ok(warrantyCallbackService.queryByProject(projectId));
    }

    @GetMapping("/customer")
    public R<List<PmsWarrantyCallback>> byCustomer(@RequestParam String customerName) {
        return R.ok(warrantyCallbackService.queryCustomerProject(customerName));
    }
}
