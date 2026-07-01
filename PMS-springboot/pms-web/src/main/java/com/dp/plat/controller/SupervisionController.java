package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.PmsSupervision;
import com.dp.plat.service.SupervisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/supervision")
public class SupervisionController {
    @Autowired
    private SupervisionService service;

    @GetMapping("/list")
    public R<IPage<PmsSupervision>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                          @RequestParam(required = false) Long projectId,
                                          @RequestParam(required = false) String officeCode) {
        return R.ok(service.queryPage(pageNum, pageSize, projectId, officeCode));
    }

    @GetMapping("/{id}")
    public R<PmsSupervision> detail(@PathVariable Long id) {
        return R.ok(service.getDetail(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody PmsSupervision s) {
        service.create(s);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody PmsSupervision s) {
        service.update(s);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return R.ok();
    }

    /**
     * 查询权限用户
     * 迁移自: SupervisionAction.queryPowerUser()
     */
    @GetMapping("/power-users")
    public R<List<Map<String, Object>>> queryPowerUsers() {
        return R.ok(service.queryPowerUsers());
    }
}
