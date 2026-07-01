package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.model.entity.*;

import java.util.List;
import java.util.Map;

public interface CallBackService {

    // ===== 已有方法 =====

    IPage<PmsCallBack> queryCallBackPage(Integer pageNum, Integer pageSize, Long projectId, Integer applyState);
    PmsCallBack getCallBackDetail(Long id);
    void createCallBack(PmsCallBack callBack);
    void startFlow(Long id);
    void approve(Long id, String comment, boolean approved);
    void resubmit(Long id, PmsCallBack callBack);
    Map<String, Object> queryQuestionnaire(Long callBackId);

    // ===== 迁移自 CallBackAction - 发起回访申请 =====

    /**
     * 发起回访申请表单数据（项目信息 + 项目成员）
     * 迁移自: CallBackAction.input()
     */
    Map<String, Object> getApplyFormData(Long projectId);

    /**
     * 发起回访审批流程（含工作流引擎交互）
     * 迁移自: CallBackAction.apply() -> callBackService.startCallBackFlow()
     */
    void startCallBackFlow(PmsCallBack callBack);

    // ===== 迁移自 CallBackAction - 查看回访详情 =====

    /**
     * 查询回访详情（含项目信息、成员、审批意见）
     * 迁移自: CallBackAction.read()
     */
    Map<String, Object> getCallBackReadData(Long callBackId, String taskId);

    // ===== 迁移自 CallBackAction - 驳回后重新提交 =====

    /**
     * 驳回后重新提交表单数据
     * 迁移自: CallBackAction.resubmit() 路径B
     */
    Map<String, Object> getResubmitFormData(Long callBackId, String taskId);

    /**
     * 驳回后重新提交审批流程
     * 迁移自: CallBackAction.resubmit() 路径A -> callBackService.reSubmitCallBackFlow()
     */
    void reSubmitCallBackFlow(PmsCallBack callBack, String comment);

    // ===== 迁移自 CallBackAction - 审批/问卷 =====

    /**
     * 查询审批表单数据（项目信息、成员、问卷分类、问卷内容、审批意见）
     * 迁移自: CallBackAction.aduit() 路径C
     */
    Map<String, Object> getAuditFormData(Long callBackId, String taskId, Long quesnaireId);

    /**
     * 保存/提交问卷（含分数计算）
     * 迁移自: CallBackAction.aduit() 路径A
     */
    void saveQuestionnaire(PmsCallBack callBack, PmClQuesnaireResultHeader resultHeader,
                           List<PmClQuesnaireResultLine> resultLines);

    /**
     * 提交审批（含工作流引擎交互）
     * 迁移自: CallBackAction.aduit() 路径B -> callBackService.submitCallBackFlow()
     */
    void submitCallBackFlow(PmsCallBack callBack, String comment);

    // ===== 迁移自 CallBackAction - 问卷相关 =====

    /**
     * 查询回访问卷详情（含模板、结果、评分）
     * 迁移自: CallBackAction.seeQuesnaire() -> queryQuesnaire()
     */
    Map<String, Object> queryQuesnaireDetail(int quesnaireId);

    /**
     * 插入问卷结果（保存草稿或提交问卷）
     * 迁移自: CallBackAction.aduit() -> callBackService.insertCallBackQuesnaire()
     */
    void insertCallBackQuesnaire(PmsCallBack callBack, PmClQuesnaireResultHeader resultHeader,
                                  List<PmClQuesnaireResultLine> resultLines);

    // ===== 迁移自 CallBackAction - 审批意见 =====

    /**
     * 查询回访审批意见列表
     * 迁移自: CallBackAction.read() -> callBackService.queryCallBackComment()
     */
    List<DpComment> queryCallBackComment(Long callBackId);

    // ===== 迁移自 CallBackAction - 问卷辅助 =====

    /**
     * 查询问卷模板ID（通过问卷结果ID反查）
     * 迁移自: CallBackAction -> callBackService.queryQuesnaireTemplateId()
     */
    Long queryQuesnaireTemplateId(Long quesnaireId);

    /**
     * 查询已填写的问卷信息
     * 迁移自: CallBackAction -> callBackService.queryCbQuesnaire()
     */
    Map<String, Object> queryCbQuesnaire(Long quesnaireId);

    /**
     * 查询生效的问卷模板列表
     * 迁移自: CallBackAction.findPmClosedLoopQuesnaireList()
     */
    List<PmClosedLoopQuesnaire> findActiveQuesnaireList();
}
