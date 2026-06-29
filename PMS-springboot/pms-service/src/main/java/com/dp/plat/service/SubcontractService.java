package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.model.entity.*;

import java.util.List;
import java.util.Map;

public interface SubcontractService {

    // ===== 基础CRUD =====
    IPage<PmsSubcontract> queryPage(Integer pageNum, Integer pageSize,
                                     String subcontractName, String officeCode, Integer state);

    PmsSubcontract getDetail(Long id);

    void create(PmsSubcontract subcontract);

    void update(PmsSubcontract subcontract);

    void delete(Long id);

    // ===== 设备行 =====
    List<PmsSubcontractLine> queryLines(Long subcontractId);

    void saveLine(PmsSubcontractLine line);

    void deleteLine(Long id);

    // ===== 交付件 =====
    List<PmsSubcontractDeliver> queryDelivers(Long subcontractId);

    void saveDeliver(PmsSubcontractDeliver deliver);

    void deleteDeliver(Long id);

    // ===== 付款 =====
    List<PmsSubcontractPayment> queryPayments(Long subcontractId);

    void savePayment(PmsSubcontractPayment payment);

    void deletePayment(Long id);

    // ===== 服务商 =====
    List<PmsSubcontractFacilitator> queryFacilitators();

    PmsSubcontractFacilitator getFacilitator(Long id);

    void saveFacilitator(PmsSubcontractFacilitator facilitator);

    // ===== 流程 =====
    void startFlow(Long id);

    void approve(Long id, String comment, boolean approved);

    void close(Long id, String comment);

    // ===== 回访 =====
    void startCallBackFlow(Long id);

    // ===== 辅助查询 =====
    List<Map<String, Object>> queryProjectList(String contractNos);

    List<Map<String, Object>> queryShipmentInfo(String contractNos, String projectIds);
}
