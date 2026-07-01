package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.*;
import com.dp.plat.service.PmsPresalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/presales")
public class PmsPresalesController {

    @Autowired
    private PmsPresalesService presalesService;

    // ===== 基础CRUD =====

    @GetMapping("/list")
    public R<IPage<PmsPresales>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestParam(required = false) String presalesCode,
                                       @RequestParam(required = false) String projectName,
                                       @RequestParam(required = false) Integer applyState,
                                       @RequestParam(required = false) String officeCode) {
        return R.ok(presalesService.queryPresalesPage(pageNum, pageSize, presalesCode, projectName, applyState, officeCode));
    }

    @GetMapping("/{id}")
    public R<PmsPresales> detail(@PathVariable Long id) {
        return R.ok(presalesService.getPresalesDetail(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody PmsPresales presales) {
        presalesService.createPresales(presales);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody PmsPresales presales) {
        presalesService.updatePresales(presales);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        presalesService.deletePresales(id);
        return R.ok();
    }

    // ===== 流程 =====

    @PostMapping("/{id}/start-flow")
    public R<Void> startFlow(@PathVariable Long id) {
        presalesService.startFlow(id);
        return R.ok();
    }

    @PostMapping("/{id}/re-apply")
    public R<Void> reApply(@PathVariable Long id, @RequestBody PmsPresales presales) {
        presalesService.reApply(id, presales);
        return R.ok();
    }

    @PostMapping("/{id}/sm-audit")
    public R<Void> smAudit(@PathVariable Long id,
                            @RequestParam String comment,
                            @RequestParam boolean approved) {
        presalesService.smAudit(id, comment, approved);
        return R.ok();
    }

    @PostMapping("/{id}/pm-audit")
    public R<Void> pmAudit(@PathVariable Long id,
                            @RequestParam String comment,
                            @RequestParam boolean approved) {
        presalesService.pmAudit(id, comment, approved);
        return R.ok();
    }

    @PostMapping("/{id}/em-audit")
    public R<Void> emAudit(@PathVariable Long id,
                            @RequestParam String comment,
                            @RequestParam boolean approved) {
        presalesService.emAudit(id, comment, approved);
        return R.ok();
    }

    @PostMapping("/{id}/approve")
    public R<Void> approve(@PathVariable Long id,
                            @RequestParam String comment,
                            @RequestParam boolean approved) {
        presalesService.approve(id, comment, approved);
        return R.ok();
    }

    @PostMapping("/{id}/terminate")
    public R<Void> terminate2Close(@PathVariable Long id, @RequestParam String closeRemark) {
        presalesService.terminate2Close(id, closeRemark);
        return R.ok();
    }

    // ===== 产品 =====

    @GetMapping("/{id}/products")
    public R<List<PmsPresalesProduct>> products(@PathVariable Long id) {
        return R.ok(presalesService.queryProducts(id));
    }

    @PostMapping("/product")
    public R<Void> saveProduct(@RequestBody PmsPresalesProduct product) {
        presalesService.saveProduct(product);
        return R.ok();
    }

    // ===== 任务 =====

    @GetMapping("/{id}/tasks")
    public R<List<PmsPresalesTask>> tasks(@PathVariable Long id) {
        return R.ok(presalesService.queryTasks(id));
    }

    @PutMapping("/task")
    public R<Void> updateTask(@RequestBody PmsPresalesTask task) {
        presalesService.updateTask(task);
        return R.ok();
    }

    // ===== 审批意见 =====

    @GetMapping("/{id}/comments")
    public R<List<PmsPresalesComment>> comments(@PathVariable Long id) {
        return R.ok(presalesService.queryComments(id));
    }

    // ===== 交付件 =====

    @PostMapping("/{id}/deliver")
    public R<Void> uploadDeliver(@PathVariable Long id,
                                  @RequestParam Long taskId,
                                  @RequestParam String fileIds) {
        presalesService.uploadDeliver(id, taskId, fileIds);
        return R.ok();
    }

    @PutMapping("/{id}/confirm-files")
    public R<Void> updateConfirmFiles(@PathVariable Long id, @RequestParam String fileIds) {
        presalesService.updateConfirmFiles(id, fileIds);
        return R.ok();
    }

    // ===== 导出 =====

    @GetMapping("/export")
    public R<List<PmsPresales>> export(@RequestParam(required = false) String officeCode,
                                        @RequestParam(required = false) Integer applyState) {
        PmsPresales query = new PmsPresales();
        query.setOfficeCode(officeCode);
        query.setApplyState(applyState);
        return R.ok(presalesService.exportPresales(query));
    }

    // ===== 发货/借转销/授权信息 =====

    /**
     * 查询发货信息
     * 迁移自: PresalesAction.shipmentInfo()
     */
    @GetMapping("/shipment-info")
    public R<List<Map<String, Object>>> shipmentInfo(
            @RequestParam String presalesCode,
            @RequestParam(defaultValue = "false") boolean containRma) {
        return R.ok(presalesService.queryShipmentInfo(presalesCode, containRma));
    }

    /**
     * 查询借转销信息
     * 迁移自: PresalesAction.lend2SaleInfo()
     */
    @GetMapping("/lend2sale-info")
    public R<List<Map<String, Object>>> lend2SaleInfo(@RequestParam String presalesCode) {
        return R.ok(presalesService.queryLend2SaleInfo(presalesCode));
    }

    /**
     * 查询核销信息
     * 迁移自: PresalesAction.lend2RmaInfo()
     */
    @GetMapping("/lend2rma-info")
    public R<List<Map<String, Object>>> lend2RmaInfo(@RequestParam String presalesCode) {
        return R.ok(presalesService.queryLend2RmaInfo(presalesCode));
    }

    /**
     * 查询临时授权信息
     * 迁移自: PresalesAction.tempAuthInfo()
     */
    @GetMapping("/temp-auth-info")
    public R<List<Map<String, Object>>> tempAuthInfo(@RequestParam Long presalesId) {
        return R.ok(presalesService.queryTempAuthInfo(presalesId));
    }

    // ===== 交付件管理(扩展) =====

    /**
     * 上传多个交付件
     * 迁移自: PresalesAction.upload()
     */
    @PostMapping("/upload-delivers")
    public R<Void> uploadDelivers(@RequestBody Map<String, Object> body) {
        Long presalesId = Long.parseLong(body.get("presalesId").toString());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> deliverList = (List<Map<String, Object>>) body.get("deliverList");
        presalesService.uploadDeliverFiles(presalesId, deliverList);
        return R.ok();
    }

    /**
     * 删除交付件
     * 迁移自: PresalesAction.deleteDeliverById()
     */
    @DeleteMapping("/deliver/{fileId}")
    public R<Void> deleteDeliverById(@PathVariable Long fileId) {
        presalesService.deleteDeliverById(fileId);
        return R.ok();
    }

    /**
     * 更新交付件
     * 迁移自: PresalesAction.updateDeliverById()
     */
    @PutMapping("/deliver")
    public R<Void> updateDeliverById(@RequestBody Map<String, Object> deliver) {
        presalesService.updateDeliverById(deliver);
        return R.ok();
    }

    // ===== 同步 =====

    /**
     * 同步OA售前数据
     * 迁移自: PresalesAction.syncOaData()
     */
    @PostMapping("/sync-oa")
    public R<Void> syncOaData() {
        presalesService.syncOaData();
        return R.ok();
    }
}
