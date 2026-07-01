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

    /** 审核(发布/驳回/关闭) - 迁移自 ProbManageAction.audit() */
    void audit(Long probId, String status);

    // ===== 恢复任务管理(高级) =====

    /**
     * 发布恢复任务
     * 迁移自: ProbManageAction.releaseTask()
     *
     * 业务逻辑:
     * 1. 如果未指定assignee,默认分配给服务经理(assigneeRole=20)
     * 2. 如果不是直接闭环的子任务,设置restoreStatus=10(开始流程)
     * 3. 批量插入恢复任务
     */
    void releaseTask(PmsProbRestore restore, List<PmsProbRestore> taskList);

    /**
     * 查询个人恢复任务
     * 迁移自: ProbManageAction.managePrivateTask()
     *
     * 业务逻辑:
     * 1. 按当前用户过滤(assignee)
     * 2. 按权限区域过滤(areapower)
     * 3. 只查restoreStatus=10(发布接受状态)的任务
     */
    List<Map<String, Object>> queryPrivateTaskList(String username, Long probId);

    /**
     * 更新个人恢复任务状态
     * 迁移自: ProbManageAction.updatePrivateTask()
     *
     * @param restoreIds 逗号分隔的恢复任务ID
     * @param isProbAdmin 是否管理员(0=个人, 2=管理员)
     */
    void updateRestoreTask(String restoreIds, int isProbAdmin, PmsProbRestore restore);

    /**
     * 查询管理员全部恢复任务
     * 迁移自: ProbManageAction.manageAllTask()
     *
     * 业务逻辑:
     * 1. 按restoreStatus分tab查询:
     *    - 31: 闭环任务
     *    - 20: 办事处返回的任务
     *    - 其他: 待闭环任务(30)
     * 2. 支持分页
     */
    List<Map<String, Object>> queryAllRestoreTaskList(Map<String, Object> params);

    /**
     * 上传恢复任务周报
     * 迁移自: ProbManageAction.weeklyUpload()
     */
    void uploadRestoreWeekly(Long fileId, Long probId, String filePath);

    // ===== 导入导出 =====

    /**
     * 导出技术公告列表
     * 迁移自: ProbManageAction.export()
     *
     * @return Excel工作簿数据
     */
    byte[] exportProbList(Map<String, Object> params);

    /**
     * 批量导入软件版本
     * 迁移自: ProbManageAction.importSoftVersion()
     *
     * @param dataList 从Excel解析的软件版本数据
     */
    void batchImportSoftVersion(List<Map<String, Object>> dataList);

    // ===== 软件版本解析 =====

    /**
     * 查询/检查软件版本列表
     * 迁移自: ProbManageAction.toCheckSoftVersion()
     */
    List<Map<String, Object>> checkSoftVersionList(Map<String, Object> params);

    // ===== 统计分析 =====

    /**
     * 技术公告统计(多维度)
     * 迁移自: ProbManageAction.statistics()
     *
     * 支持4个tab:
     * tab 0: 按时间统计折线图
     * tab 1: 按维度统计
     * tab 2: 受影响项目列表
     * tab 3: 合同发货软件版本
     */
    Map<String, Object> queryStatistics(Map<String, Object> params);

    /**
     * 受影响项目软件版本
     * 迁移自: ProbManageAction.affectedProjectSoftVersion()
     */
    List<Map<String, Object>> queryAffectedProjectSoftVersion(Map<String, Object> params);

    // ===== 产品物料管理 =====

    /**
     * 查询产品物料列表
     * 迁移自: ProbManageAction.listProductItem()
     */
    List<Map<String, Object>> queryProductItemList(Map<String, Object> params);

    /**
     * 查询公告产品列表(分页)
     * 迁移自: ProbManageAction.listProbProduct()
     */
    List<Map<String, Object>> queryProbProductList(Map<String, Object> params);

    /**
     * 保存公告产品
     * 迁移自: ProbManageAction.saveProbProduct()
     */
    void saveProbProduct(Map<String, Object> product);

    /**
     * 批量导入公告产品
     * 迁移自: ProbManageAction.importProbProduct()
     */
    void batchImportProbProduct(List<Map<String, Object>> dataList);

    /**
     * 查询产品组件列表(分页)
     * 迁移自: ProbManageAction.listComponent()
     */
    List<Map<String, Object>> queryComponentList(Map<String, Object> params);

    /**
     * 保存产品组件
     * 迁移自: ProbManageAction.saveComponent()
     */
    void saveComponent(Map<String, Object> component);

    /**
     * 批量导入产品组件
     * 迁移自: ProbManageAction.importComponent()
     */
    void batchImportComponent(List<Map<String, Object>> dataList);
}
