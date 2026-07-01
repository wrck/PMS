package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.model.entity.PmClosedLoop;

import java.util.List;

public interface PmClosedLoopService {

    /** 分页查询闭环记录 */
    IPage<PmClosedLoop> queryClosedLoopPage(Integer pageNum, Integer pageSize, Long projectId, Integer applyState);

    /** 获取闭环详情 */
    PmClosedLoop getDetail(Long id);

    /** 发起闭环申请(通用) */
    void apply(PmClosedLoop closedLoop);

    /** PM发起闭环申请 - 迁移自 PmClosedLoopAction.addPmCLApply() */
    void pmApply(PmClosedLoop closedLoop);

    /** SM发起闭环申请 - 迁移自 PmClosedLoopAction.addSmCLApply() */
    void smApply(PmClosedLoop closedLoop);

    /** CB发起闭环申请 - 迁移自 PmClosedLoopAction.addCbCLApply() */
    void cbApply(PmClosedLoop closedLoop);

    /** 无法闭环 - 迁移自 PmClosedLoopAction.cantCB() */
    void cantClose(Long id, String reason);

    /** CL发起闭环申请 - 迁移自 PmClosedLoopAction.addClCLApply() */
    void clApply(PmClosedLoop closedLoop);

    /** 审批闭环 */
    void approve(Long id, String comment, boolean approved, String role);

    /** 查询项目的闭环历史 */
    List<PmClosedLoop> queryByProject(Long projectId);

    /** 查询进行中的闭环申请 */
    PmClosedLoop queryRunningByProject(Long projectId);
}
