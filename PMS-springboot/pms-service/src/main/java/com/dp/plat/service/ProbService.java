package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.model.entity.*;

import java.util.List;
import java.util.Map;

public interface ProbService {

    // ===== 基础CRUD =====
    IPage<PmsProb> queryPage(Integer pageNum, Integer pageSize, String probTitle, Integer probState, Integer probType);

    PmsProb getDetail(Long id);

    void create(PmsProb prob);

    void update(PmsProb prob);

    void delete(Long id);

    // ===== 软件版本管理 =====

    /** 查询公告受影响的软件版本 */
    List<PmsProbSoftVersion> querySoftVersionList(Long probId);

    /** 保存软件版本(替换) */
    void saveSoftVersions(Long probId, List<PmsProbSoftVersion> versions);

    /** 更新软件版本 */
    void updateProbSoftVersion(List<PmsProbSoftVersion> versions, Long probId);

    // ===== 恢复任务管理 =====

    /** 查询恢复任务列表 */
    IPage<PmsProbRestore> queryRestorePage(Integer pageNum, Integer pageSize, Long probId, String assignee);

    /** 保存恢复任务 */
    void saveRestore(PmsProbRestore restore);

    /** 更新恢复任务 */
    void updateRestore(PmsProbRestore restore);

    /** 批量删除恢复任务 */
    void batchDeleteRestores(String restoreIds);

    /** 查询未完成恢复任务数 */
    int countUnfinishedRestores(Long probId);

    // ===== 产品管理 =====

    /** 查询公告产品列表 */
    List<PmsProbProduct> queryProducts(Long probId);

    /** 保存公告产品 */
    void saveProduct(PmsProbProduct product);

    // ===== 阅读日志 =====

    /** 记录阅读 */
    void recordRead(Long probId, String reader);

    /** 查询阅读日志 */
    List<PmsProbReadLog> queryReadLogs(Long probId);

    // ===== 审核 =====

    /** 审核(发布/驳回/关闭) */
    void audit(Long probId, String status);

    // ===== 统计 =====

    /** 技术公告统计 */
    Map<String, Object> statistics(Map<String, Object> params);
}
