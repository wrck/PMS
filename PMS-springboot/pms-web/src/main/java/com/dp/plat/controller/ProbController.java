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

    // ===== 审核 =====

    @PostMapping("/{id}/audit")
    public R<Void> audit(@PathVariable Long id, @RequestParam String status) {
        probService.audit(id, status);
        return R.ok();
    }

    // ===== 恢复任务管理(高级) =====

    /**
     * 发布恢复任务
     * 迁移自: ProbManageAction.releaseTask()
     */
    @PostMapping("/release-task")
    public R<Void> releaseTask(@RequestBody Map<String, Object> body) {
        // TODO: 从body解析restore和taskList
        // probService.releaseTask(restore, taskList);
        return R.ok();
    }

    /**
     * 查询个人恢复任务
     * 迁移自: ProbManageAction.managePrivateTask()
     */
    @GetMapping("/private-tasks")
    public R<List<Map<String, Object>>> privateTasks(
            @RequestParam String username,
            @RequestParam(required = false) Long probId) {
        return R.ok(probService.queryPrivateTaskList(username, probId));
    }

    /**
     * 更新个人恢复任务状态
     * 迁移自: ProbManageAction.updatePrivateTask()
     */
    @PutMapping("/private-tasks")
    public R<Void> updatePrivateTask(
            @RequestParam String restoreIds,
            @RequestBody Map<String, Object> restore) {
        probService.updateRestoreTask(restoreIds, 0, null);
        return R.ok();
    }

    /**
     * 管理员查询全部恢复任务
     * 迁移自: ProbManageAction.manageAllTask()
     */
    @GetMapping("/all-tasks")
    public R<List<Map<String, Object>>> allTasks(
            @RequestParam(required = false) Long probId,
            @RequestParam(defaultValue = "30") int restoreStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Map<String, Object> params = new HashMap<>();
        params.put("probId", probId);
        params.put("restoreStatus", restoreStatus);
        params.put("page", page);
        params.put("size", size);
        return R.ok(probService.queryAllRestoreTaskList(params));
    }

    /**
     * 管理员更新恢复任务
     * 迁移自: ProbManageAction.updateRestoreTask()
     */
    @PutMapping("/all-tasks")
    public R<Void> updateRestoreTask(
            @RequestParam String restoreIds,
            @RequestBody Map<String, Object> restore) {
        probService.updateRestoreTask(restoreIds, 2, null);
        return R.ok();
    }

    // ===== 导入导出 =====

    /**
     * 导出技术公告
     * 迁移自: ProbManageAction.export()
     */
    @GetMapping("/export")
    public R<byte[]> export(@RequestParam(required = false) Map<String, Object> params) {
        return R.ok(probService.exportProbList(params));
    }

    /**
     * 批量导入软件版本
     * 迁移自: ProbManageAction.importSoftVersion()
     */
    @PostMapping("/import-soft-version")
    public R<Void> importSoftVersion(@RequestBody List<Map<String, Object>> dataList) {
        probService.batchImportSoftVersion(dataList);
        return R.ok();
    }

    /**
     * 查询/检查软件版本
     * 迁移自: ProbManageAction.toCheckSoftVersion()
     */
    @GetMapping("/check-soft-version")
    public R<List<Map<String, Object>>> checkSoftVersion(@RequestParam(required = false) Map<String, Object> params) {
        return R.ok(probService.checkSoftVersionList(params));
    }

    // ===== 统计分析 =====

    /**
     * 技术公告统计(多维度)
     * 迁移自: ProbManageAction.statistics()
     */
    @GetMapping("/statistics")
    public R<Map<String, Object>> statistics(@RequestParam(required = false) Map<String, Object> params) {
        return R.ok(probService.queryStatistics(params));
    }

    /**
     * 受影响项目软件版本
     * 迁移自: ProbManageAction.affectedProjectSoftVersion()
     */
    @GetMapping("/affected-project-soft-version")
    public R<List<Map<String, Object>>> affectedProjectSoftVersion(@RequestParam(required = false) Map<String, Object> params) {
        return R.ok(probService.queryAffectedProjectSoftVersion(params));
    }

    // ===== 产品物料管理 =====

    /**
     * 查询产品物料列表
     * 迁移自: ProbManageAction.listProductItem()
     */
    @GetMapping("/product-items")
    public R<List<Map<String, Object>>> productItems(@RequestParam(required = false) Map<String, Object> params) {
        return R.ok(probService.queryProductItemList(params));
    }

    /**
     * 查询公告产品列表(分页)
     * 迁移自: ProbManageAction.listProbProduct()
     */
    @GetMapping("/prob-products")
    public R<List<Map<String, Object>>> probProducts(@RequestParam(required = false) Map<String, Object> params) {
        return R.ok(probService.queryProbProductList(params));
    }

    /**
     * 保存公告产品
     * 迁移自: ProbManageAction.saveProbProduct()
     */
    @PostMapping("/prob-product")
    public R<Void> saveProbProduct(@RequestBody Map<String, Object> product) {
        probService.saveProbProduct(product);
        return R.ok();
    }

    /**
     * 批量导入公告产品
     * 迁移自: ProbManageAction.importProbProduct()
     */
    @PostMapping("/import-prob-product")
    public R<Void> importProbProduct(@RequestBody List<Map<String, Object>> dataList) {
        probService.batchImportProbProduct(dataList);
        return R.ok();
    }

    /**
     * 查询产品组件列表
     * 迁移自: ProbManageAction.listComponent()
     */
    @GetMapping("/components")
    public R<List<Map<String, Object>>> components(@RequestParam(required = false) Map<String, Object> params) {
        return R.ok(probService.queryComponentList(params));
    }

    /**
     * 保存产品组件
     * 迁移自: ProbManageAction.saveComponent()
     */
    @PostMapping("/component")
    public R<Void> saveComponent(@RequestBody Map<String, Object> component) {
        probService.saveComponent(component);
        return R.ok();
    }

    /**
     * 批量导入产品组件
     * 迁移自: ProbManageAction.importComponent()
     */
    @PostMapping("/import-component")
    public R<Void> importComponent(@RequestBody List<Map<String, Object>> dataList) {
        probService.batchImportComponent(dataList);
        return R.ok();
    }
}
