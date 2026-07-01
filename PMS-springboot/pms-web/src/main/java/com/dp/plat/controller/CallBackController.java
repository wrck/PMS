package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.*;
import com.dp.plat.service.CallBackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/callback")
public class CallBackController {

    @Autowired
    private CallBackService callBackService;

    // ==================== 已有端点 ====================

    @GetMapping("/list")
    public R<IPage<PmsCallBack>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestParam(required = false) Long projectId,
                                       @RequestParam(required = false) Integer applyState) {
        return R.ok(callBackService.queryCallBackPage(pageNum, pageSize, projectId, applyState));
    }

    @GetMapping("/{id}")
    public R<PmsCallBack> detail(@PathVariable Long id) {
        return R.ok(callBackService.getCallBackDetail(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody PmsCallBack callBack) {
        callBackService.createCallBack(callBack);
        return R.ok();
    }

    @PostMapping("/{id}/start-flow")
    public R<Void> startFlow(@PathVariable Long id) {
        callBackService.startFlow(id);
        return R.ok();
    }

    @PostMapping("/{id}/approve")
    public R<Void> approve(@PathVariable Long id, @RequestParam String comment, @RequestParam boolean approved) {
        callBackService.approve(id, comment, approved);
        return R.ok();
    }

    @PostMapping("/{id}/resubmit")
    public R<Void> resubmit(@PathVariable Long id, @RequestBody PmsCallBack callBack) {
        callBackService.resubmit(id, callBack);
        return R.ok();
    }

    @GetMapping("/{id}/questionnaire")
    public R<Map<String, Object>> seeQuestionnaire(@PathVariable Long id) {
        return R.ok(callBackService.queryQuestionnaire(id));
    }

    // ==================== 迁移自 CallBackAction - 发起回访申请 ====================

    /**
     * 发起回访申请表单数据（项目信息 + 项目成员）
     * 迁移自: CallBackAction.input()
     */
    @GetMapping("/apply/form")
    public R<Map<String, Object>> getApplyFormData(@RequestParam Long projectId) {
        return R.ok(callBackService.getApplyFormData(projectId));
    }

    /**
     * 发起回访审批流程
     * 迁移自: CallBackAction.apply() -> callBackService.startCallBackFlow()
     */
    @PostMapping("/apply")
    public R<Void> apply(@RequestBody PmsCallBack callBack) {
        callBackService.startCallBackFlow(callBack);
        return R.ok();
    }

    // ==================== 迁移自 CallBackAction - 查看回访详情 ====================

    /**
     * 查询回访详情（含项目信息、成员、审批意见）
     * 迁移自: CallBackAction.read()
     */
    @GetMapping("/{id}/read")
    public R<Map<String, Object>> read(@PathVariable Long id,
                                        @RequestParam(required = false) String taskId) {
        return R.ok(callBackService.getCallBackReadData(id, taskId));
    }

    // ==================== 迁移自 CallBackAction - 驳回后重新提交 ====================

    /**
     * 驳回后重新提交表单数据
     * 迁移自: CallBackAction.resubmit() 路径B
     */
    @GetMapping("/{id}/resubmit/form")
    public R<Map<String, Object>> getResubmitFormData(@PathVariable Long id,
                                                       @RequestParam(required = false) String taskId) {
        return R.ok(callBackService.getResubmitFormData(id, taskId));
    }

    /**
     * 驳回后重新提交审批流程
     * 迁移自: CallBackAction.resubmit() 路径A
     */
    @PostMapping("/{id}/resubmit/flow")
    public R<Void> resubmitFlow(@PathVariable Long id,
                                 @RequestBody PmsCallBack callBack,
                                 @RequestParam(required = false) String comment) {
        callBackService.reSubmitCallBackFlow(callBack, comment);
        return R.ok();
    }

    // ==================== 迁移自 CallBackAction - 审批/问卷 ====================

    /**
     * 查询审批表单数据（项目信息、成员、问卷分类、问卷内容、审批意见）
     * 迁移自: CallBackAction.aduit() 路径C
     */
    @GetMapping("/{id}/audit/form")
    public R<Map<String, Object>> getAuditFormData(@PathVariable Long id,
                                                    @RequestParam(required = false) String taskId,
                                                    @RequestParam(required = false) Long quesnaireId) {
        return R.ok(callBackService.getAuditFormData(id, taskId, quesnaireId));
    }

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 保存/提交问卷（含分数计算）
     * 迁移自: CallBackAction.aduit() 路径A
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/{id}/questionnaire/save")
    public R<Void> saveQuestionnaire(@PathVariable Long id,
                                      @RequestBody Map<String, Object> requestBody) {
        PmsCallBack callBack = callBackService.getCallBackDetail(id);

        // 使用ObjectMapper进行类型安全的反序列化
        PmClQuesnaireResultHeader resultHeader = objectMapper.convertValue(
            requestBody.get("resultHeader"), PmClQuesnaireResultHeader.class);

        List<PmClQuesnaireResultLine> resultLines = null;
        if (requestBody.get("resultLines") != null) {
            resultLines = objectMapper.convertValue(
                requestBody.get("resultLines"),
                objectMapper.getTypeFactory().constructCollectionType(List.class, PmClQuesnaireResultLine.class));
        }

        callBackService.saveQuestionnaire(callBack, resultHeader, resultLines);
        return R.ok();
    }

    /**
     * 提交审批（含工作流引擎交互）
     * 迁移自: CallBackAction.aduit() 路径B
     */
    @PostMapping("/{id}/audit/submit")
    public R<Void> submitAudit(@PathVariable Long id,
                                @RequestParam(required = false) String comment) {
        PmsCallBack callBack = callBackService.getCallBackDetail(id);
        callBackService.submitCallBackFlow(callBack, comment);
        return R.ok();
    }

    // ==================== 迁移自 CallBackAction - 问卷详情 ====================

    /**
     * 查询回访问卷详情（含模板、结果、评分）
     * 迁移自: CallBackAction.seeQuesnaire() -> queryQuesnaire()
     */
    @GetMapping("/questionnaire/{quesnaireId}")
    public R<Map<String, Object>> queryQuesnaireDetail(@PathVariable int quesnaireId) {
        return R.ok(callBackService.queryQuesnaireDetail(quesnaireId));
    }

    /**
     * 查询生效的问卷模板列表
     * 迁移自: CallBackAction.findPmClosedLoopQuesnaireList()
     */
    @GetMapping("/questionnaire/templates")
    public R<List<PmClosedLoopQuesnaire>> getActiveQuesnaireList() {
        return R.ok(callBackService.findActiveQuesnaireList());
    }


