package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.model.entity.*;

import java.util.List;

public interface PmsPresalesService {

    // ===== 基础CRUD =====
    IPage<PmsPresales> queryPresalesPage(Integer pageNum, Integer pageSize,
                                          String presalesCode, String projectName,
                                          Integer applyState, String officeCode);

    PmsPresales getPresalesDetail(Long id);

    void createPresales(PmsPresales presales);

    void updatePresales(PmsPresales presales);

    void deletePresales(Long id);

    // ===== 流程 =====

    /** 发起流程 */
    void startFlow(Long id);

    /** 重新申请 */
    void reApply(Long id, PmsPresales presales);

    /** 服务经理审批 */
    void smAudit(Long id, String comment, boolean approved);

    /** 项目经理审批 */
    void pmAudit(Long id, String comment, boolean approved);

    /** 工程管理部审批 */
    void emAudit(Long id, String comment, boolean approved);

    /** 通用审批(兼容旧接口) */
    void approve(Long id, String comment, boolean approved);

    /** 终止并关闭 */
    void terminate2Close(Long id, String closeRemark);

    // ===== 产品 =====

    /** 查询售前产品 */
    List<PmsPresalesProduct> queryProducts(Long presalesId);

    /** 保存售前产品 */
    void saveProduct(PmsPresalesProduct product);

    // ===== 任务 =====

    /** 查询售前任务 */
    List<PmsPresalesTask> queryTasks(Long presalesId);

    /** 更新任务 */
    void updateTask(PmsPresalesTask task);

    // ===== 审批意见 =====

    /** 查询审批意见 */
    List<PmsPresalesComment> queryComments(Long presalesId);

    /** 添加审批意见 */
    void addComment(PmsPresalesComment comment);

    // ===== 交付件 =====

    /** 上传交付件 */
    void uploadDeliver(Long presalesId, Long taskId, String fileIds);

    /** 删除交付件 */
    void deleteDeliver(Long deliverId);

    /** 更新确认文件 */
    void updateConfirmFiles(Long presalesId, String fileIds);

    // ===== 导出 =====

    /** 导出售前项目 */
    List<PmsPresales> exportPresales(PmsPresales query);

    // ===== 发货/借转销/授权信息 =====

    /**
     * 查询售前发货信息
     * 迁移自: PresalesAction.shipmentInfo()
     */
    List<Map<String, Object>> queryShipmentInfo(String presalesCode, boolean containRma);

    /**
     * 查询借转销信息
     * 迁移自: PresalesAction.lend2SaleInfo()
     */
    List<Map<String, Object>> queryLend2SaleInfo(String presalesCode);

    /**
     * 查询核销信息
     * 迁移自: PresalesAction.lend2RmaInfo()
     */
    List<Map<String, Object>> queryLend2RmaInfo(String presalesCode);

    /**
     * 查询临时授权信息
     * 迁移自: PresalesAction.tempAuthInfo()
     */
    List<Map<String, Object>> queryTempAuthInfo(Long presalesId);

    // ===== 交付件管理(扩展) =====

    /**
     * 上传多个交付件
     * 迁移自: PresalesAction.upload()
     */
    void uploadDeliverFiles(Long presalesId, List<Map<String, Object>> deliverList);

    /**
     * 删除交付件(按文件ID)
     * 迁移自: PresalesAction.deleteDeliverById()
     */
    void deleteDeliverById(Long fileId);

    /**
     * 更新交付件
     * 迁移自: PresalesAction.updateDeliverById()
     */
    void updateDeliverById(Map<String, Object> deliver);

    // ===== 同步 =====

    /**
     * 同步OA售前数据
     * 迁移自: PresalesAction.syncOaData()
     */
    void syncOaData();
}
