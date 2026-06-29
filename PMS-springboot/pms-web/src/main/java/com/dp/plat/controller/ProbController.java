package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.common.utils.SecurityUtil;
import com.dp.plat.model.entity.*;
import com.dp.plat.service.ProbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prob")
public class ProbController {

    @Autowired
    private ProbService probService;

    // ===== 基础CRUD =====

    @GetMapping("/list")
    public R<IPage<PmsProb>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(required = false) String probTitle,
                                   @RequestParam(required = false) Integer probState,
                                   @RequestParam(required = false) Integer probType) {
        return R.ok(probService.queryPage(pageNum, pageSize, probTitle, probState, probType));
    }

    @GetMapping("/{id}")
    public R<PmsProb> detail(@PathVariable Long id) {
        return R.ok(probService.getDetail(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody PmsProb prob) {
        probService.create(prob);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody PmsProb prob) {
        probService.update(prob);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        probService.delete(id);
        return R.ok();
    }

    // ===== 软件版本 =====

    @GetMapping("/{id}/soft-versions")
    public R<List<PmsProbSoftVersion>> softVersions(@PathVariable Long id) {
        return R.ok(probService.querySoftVersionList(id));
    }

    @PostMapping("/{id}/soft-versions")
    public R<Void> saveSoftVersions(@PathVariable Long id, @RequestBody List<PmsProbSoftVersion> versions) {
        probService.saveSoftVersions(id, versions);
        return R.ok();
    }

    // ===== 恢复任务 =====

    @GetMapping("/{id}/restores")
    public R<IPage<PmsProbRestore>> restores(@PathVariable Long id,
                                              @RequestParam(defaultValue = "1") Integer pageNum,
                                              @RequestParam(defaultValue = "10") Integer pageSize,
                                              @RequestParam(required = false) String assignee) {
        return R.ok(probService.queryRestorePage(pageNum, pageSize, id, assignee));
    }

    @PostMapping("/restore")
    public R<Void> saveRestore(@RequestBody PmsProbRestore restore) {
        probService.saveRestore(restore);
        return R.ok();
    }

    @PutMapping("/restore")
    public R<Void> updateRestore(@RequestBody PmsProbRestore restore) {
        probService.updateRestore(restore);
        return R.ok();
    }

    @DeleteMapping("/restore/batch")
    public R<Void> batchDeleteRestores(@RequestParam String restoreIds) {
        probService.batchDeleteRestores(restoreIds);
        return R.ok();
    }

    // ===== 产品 =====

    @GetMapping("/{id}/products")
    public R<List<PmsProbProduct>> products(@PathVariable Long id) {
        return R.ok(probService.queryProducts(id));
    }

    @PostMapping("/product")
    public R<Void> saveProduct(@RequestBody PmsProbProduct product) {
        probService.saveProduct(product);
        return R.ok();
    }

    // ===== 阅读日志 =====

    @PostMapping("/{id}/read")
    public R<Void> recordRead(@PathVariable Long id) {
        probService.recordRead(id, SecurityUtil.getCurrentUsername());
        return R.ok();
    }

    @GetMapping("/{id}/read-logs")
    public R<List<PmsProbReadLog>> readLogs(@PathVariable Long id) {
        return R.ok(probService.queryReadLogs(id));
    }

    // ===== 审核 =====

    @PostMapping("/{id}/audit")
    public R<Void> audit(@PathVariable Long id, @RequestParam String status) {
        probService.audit(id, status);
        return R.ok();
    }

    // ===== 统计 =====

    @GetMapping("/statistics")
    public R<Map<String, Object>> statistics(@RequestParam(required = false) Map<String, Object> params) {
        return R.ok(probService.statistics(params));
    }
}
