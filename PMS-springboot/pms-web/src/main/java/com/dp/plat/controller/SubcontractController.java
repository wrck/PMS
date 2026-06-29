package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.*;
import com.dp.plat.service.SubcontractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subcontract")
public class SubcontractController {

    @Autowired
    private SubcontractService subcontractService;

    // ===== 基础CRUD =====

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

    // ===== 设备行 =====

    @GetMapping("/{id}/lines")
    public R<List<PmsSubcontractLine>> lines(@PathVariable Long id) {
        return R.ok(subcontractService.queryLines(id));
    }

    @PostMapping("/line")
    public R<Void> saveLine(@RequestBody PmsSubcontractLine line) {
        subcontractService.saveLine(line);
        return R.ok();
    }

    @DeleteMapping("/line/{id}")
    public R<Void> deleteLine(@PathVariable Long id) {
        subcontractService.deleteLine(id);
        return R.ok();
    }

    // ===== 交付件 =====

    @GetMapping("/{id}/delivers")
    public R<List<PmsSubcontractDeliver>> delivers(@PathVariable Long id) {
        return R.ok(subcontractService.queryDelivers(id));
    }

    @PostMapping("/deliver")
    public R<Void> saveDeliver(@RequestBody PmsSubcontractDeliver deliver) {
        subcontractService.saveDeliver(deliver);
        return R.ok();
    }

    @DeleteMapping("/deliver/{id}")
    public R<Void> deleteDeliver(@PathVariable Long id) {
        subcontractService.deleteDeliver(id);
        return R.ok();
    }

    // ===== 付款 =====

    @GetMapping("/{id}/payments")
    public R<List<PmsSubcontractPayment>> payments(@PathVariable Long id) {
        return R.ok(subcontractService.queryPayments(id));
    }

    @PostMapping("/payment")
    public R<Void> savePayment(@RequestBody PmsSubcontractPayment payment) {
        subcontractService.savePayment(payment);
        return R.ok();
    }

    @DeleteMapping("/payment/{id}")
    public R<Void> deletePayment(@PathVariable Long id) {
        subcontractService.deletePayment(id);
        return R.ok();
    }

    // ===== 服务商 =====

    @GetMapping("/facilitators")
    public R<List<PmsSubcontractFacilitator>> facilitators() {
        return R.ok(subcontractService.queryFacilitators());
    }

    @GetMapping("/facilitator/{id}")
    public R<PmsSubcontractFacilitator> facilitator(@PathVariable Long id) {
        return R.ok(subcontractService.getFacilitator(id));
    }

    @PostMapping("/facilitator")
    public R<Void> saveFacilitator(@RequestBody PmsSubcontractFacilitator facilitator) {
        subcontractService.saveFacilitator(facilitator);
        return R.ok();
    }

    // ===== 流程 =====

    @PostMapping("/{id}/start-flow")
    public R<Void> startFlow(@PathVariable Long id) {
        subcontractService.startFlow(id);
        return R.ok();
    }

    @PostMapping("/{id}/approve")
    public R<Void> approve(@PathVariable Long id,
                            @RequestParam String comment,
                            @RequestParam boolean approved) {
        subcontractService.approve(id, comment, approved);
        return R.ok();
    }

    @PostMapping("/{id}/close")
    public R<Void> close(@PathVariable Long id, @RequestParam String comment) {
        subcontractService.close(id, comment);
        return R.ok();
    }

    @PostMapping("/{id}/callback")
    public R<Void> startCallBackFlow(@PathVariable Long id) {
        subcontractService.startCallBackFlow(id);
        return R.ok();
    }

    // ===== 辅助查询 =====

    @GetMapping("/projects")
    public R<List<Map<String, Object>>> queryProjects(@RequestParam String contractNos) {
        return R.ok(subcontractService.queryProjectList(contractNos));
    }

    @GetMapping("/shipment-info")
    public R<List<Map<String, Object>>> queryShipmentInfo(@RequestParam String contractNos,
                                                           @RequestParam(required = false) String projectIds) {
        return R.ok(subcontractService.queryShipmentInfo(contractNos, projectIds));
    }
}
