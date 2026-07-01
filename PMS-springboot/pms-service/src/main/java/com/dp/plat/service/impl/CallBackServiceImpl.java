package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.constants.MessageUtil;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.utils.PmClosedLoopMark;
import com.dp.plat.common.utils.PmClosedLoopMarkFactory;
import com.dp.plat.common.utils.SecurityUtil;
import com.dp.plat.mapper.*;
import com.dp.plat.model.entity.*;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.CallBackService;
import com.dp.plat.service.WorkflowService;
import com.dp.plat.model.vo.WorkflowTaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 回访服务实现 - 迁移自老系统 CallBackAction + CallBackServiceImpl
 *
 * 注意: Activiti工作流引擎集成已移除，改为基于状态机的简化流程管理。
 * 老系统通过 workFlowService 管理流程（startProcess/doSelfTask/addSelfActComment），
 * 新系统通过 applyState 状态字段管理流程状态流转。
 */
@Service
public class CallBackServiceImpl implements CallBackService {

    @Autowired
    private PmsCallBackMapper callBackMapper;
    @Autowired
    private PmClosedLoopQuesnaireMapper quesnaireMapper;
    @Autowired
    private PmClosedLoopQuesnaireLineMapper quesnaireLineMapper;
    @Autowired
    private PmClosedLoopQuesnaireOptMapper quesnaireOptMapper;
    @Autowired
    private PmClQuesnaireResultHeaderMapper resultHeaderMapper;
    @Autowired
    private PmClQuesnaireResultLineMapper resultLineMapper;
    @Autowired
    private DpCommentMapper commentMapper;
    @Autowired
    private PmsProjectMapper projectMapper;
    @Autowired
    private PmsProjectMemberMapper projectMemberMapper;
    @Autowired
    private BasicDataService basicDataService;
    @Autowired
    private WorkflowService workflowService;

    // ===== 状态常量（迁移自老系统 MessageUtil） =====
    private static final int STATE_DRAFT = -1;
    private static final int STATE_PENDING = 0;
    private static final int STATE_APPROVED = 1;
    private static final int STATE_REJECTED = 2;

    // ==================== 已有方法 ====================

    @Override
    public IPage<PmsCallBack> queryCallBackPage(Integer pageNum, Integer pageSize, Long projectId, Integer applyState) {
        Page<PmsCallBack> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsCallBack> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(projectId != null, PmsCallBack::getProjectId, projectId)
               .eq(applyState != null, PmsCallBack::getApplyState, applyState)
               .orderByDesc(PmsCallBack::getCreateTime);
        return callBackMapper.selectPage(page, wrapper);
    }

    @Override
    public PmsCallBack getCallBackDetail(Long id) {
        PmsCallBack cb = callBackMapper.selectById(id);
        if (cb == null) throw new BusinessException("回访记录不存在");
        return cb;
    }

    @Override
    @Transactional
    public void createCallBack(PmsCallBack callBack) {
        callBack.setApplyState(STATE_DRAFT);
        callBack.setCreateTime(LocalDateTime.now());
        callBackMapper.insert(callBack);
    }

    @Override
    @Transactional
    public void startFlow(Long id) {
        PmsCallBack cb = callBackMapper.selectById(id);
        if (cb == null) throw new BusinessException("回访记录不存在");
        cb.setApplyState(STATE_PENDING);
        cb.setApplyTime(LocalDateTime.now());
        cb.setApplyBy(SecurityUtil.getCurrentUsername());
        callBackMapper.updateById(cb);
    }

    @Override
    @Transactional
    public void approve(Long id, String comment, boolean approved) {
        PmsCallBack cb = callBackMapper.selectById(id);
        if (cb == null) throw new BusinessException("回访记录不存在");
        cb.setApplyState(approved ? STATE_APPROVED : STATE_REJECTED);
        if (!approved) cb.setEndTime(LocalDateTime.now());
        callBackMapper.updateById(cb);
        saveComment(cb.getInstId(), comment, approved ? STATE_APPROVED : STATE_REJECTED);
    }

    @Override
    @Transactional
    public void resubmit(Long id, PmsCallBack callBack) {
        PmsCallBack existing = callBackMapper.selectById(id);
        if (existing == null) throw new BusinessException("回访记录不存在");
        existing.setApplyState(STATE_PENDING);
        existing.setApplyTime(LocalDateTime.now());
        existing.setApplyBy(SecurityUtil.getCurrentUsername());
        if (callBack.getProjectId() != null) existing.setProjectId(callBack.getProjectId());
        if (callBack.getProjectCode() != null) existing.setProjectCode(callBack.getProjectCode());
        if (callBack.getProjectName() != null) existing.setProjectName(callBack.getProjectName());
        callBackMapper.updateById(existing);
    }

    @Override
    public Map<String, Object> queryQuestionnaire(Long callBackId) {
        PmsCallBack cb = callBackMapper.selectById(callBackId);
        if (cb == null) throw new BusinessException("回访记录不存在");
        Map<String, Object> result = new HashMap<>();
        result.put("callBack", cb);

        // 1. 查询生效的问卷模板
        List<PmClosedLoopQuesnaire> questionnaires = findActiveQuesnaireList();
        result.put("questionnaire", questionnaires);

        // 2. 批量查询模板行和选项（修复N+1问题）
        if (questionnaires != null && !questionnaires.isEmpty()) {
            Long headerId = questionnaires.get(0).getId();
            List<PmClosedLoopQuesnaireLine> lines = quesnaireLineMapper.selectList(
                new LambdaQueryWrapper<PmClosedLoopQuesnaireLine>()
                    .eq(PmClosedLoopQuesnaireLine::getHeaderId, headerId)
                    .orderByAsc(PmClosedLoopQuesnaireLine::getSortOrder));
            result.put("questionnaireLines", lines);

            // 批量查询所有选项（非逐行查询，修复N+1）
            List<PmClosedLoopQuesnaireOpt> allOptions = quesnaireOptMapper.selectList(
                new LambdaQueryWrapper<PmClosedLoopQuesnaireOpt>()
                    .eq(PmClosedLoopQuesnaireOpt::getLineId, headerId)
                    .orderByAsc(PmClosedLoopQuesnaireOpt::getSortOrder));
            result.put("questionnaireOptions", allOptions);
        } else {
            result.put("questionnaireLines", Collections.emptyList());
            result.put("questionnaireOptions", Collections.emptyList());
        }

        // 3. 查询已填写的问卷结果（通过callBack关联）
        if (cb.getQuesnaireId() != null && cb.getQuesnaireId() > 0) {
            PmClQuesnaireResultHeader resultHeader = resultHeaderMapper.selectById(cb.getQuesnaireId());
            if (resultHeader != null) {
                List<PmClQuesnaireResultLine> resultLines = resultLineMapper.selectList(
                    new LambdaQueryWrapper<PmClQuesnaireResultLine>()
                        .eq(PmClQuesnaireResultLine::getResultHeaderId, resultHeader.getId()));
                result.put("resultHeader", resultHeader);
                result.put("resultLines", resultLines);

                // 计算评分（迁移自: getQuesTypeScore）
                List<SysBasicData> quesTypeList = basicDataService.queryAllByType(MessageUtil.CL_QUESNAIRE_LINEID);
                result.put("quesResultMarkList", getQuesTypeScore(resultLines, quesTypeList));
            }
        }

        // 4. 查询审批意见
        result.put("commentList", queryCallBackComment(callBackId));
        return result;
    }

    // ==================== 迁移自 CallBackAction - 发起回访申请 ====================

    @Override
    public Map<String, Object> getApplyFormData(Long projectId) {
        // 迁移自: CallBackAction.input()
        Map<String, Object> result = new HashMap<>();
        PmsProject project = projectMapper.selectById(projectId);
        result.put("project", project);
        if (project != null) {
            List<PmsProjectMember> members = projectMemberMapper.selectList(
                new LambdaQueryWrapper<PmsProjectMember>()
                    .eq(PmsProjectMember::getProjectId, projectId));
            result.put("projectMemberList", members);
        }
        return result;
    }

    @Override
    @Transactional
    public void startCallBackFlow(PmsCallBack callBack) {
        // 迁移自: CallBackServiceImpl.startCallBackFlow()
        // 老系统逻辑: 保存申请→启动Activiti流程→回写instId→办理任务→增加审批意见

        // 1. 保存回访申请
        callBack.setApplyState(STATE_PENDING);
        callBack.setApplyTime(LocalDateTime.now());
        callBack.setApplyBy(SecurityUtil.getCurrentUsername());
        callBack.setCreateTime(LocalDateTime.now());
        callBackMapper.insert(callBack);

        // 2. 启动Flowable流程
        String businessKey = "PmsCallBack." + callBack.getId() + "." + callBack.getProjectId();
        Map<String, Object> vars = new HashMap<>();
        vars.put("initiator", SecurityUtil.getCurrentUsername());
        vars.put("callbackManager", "callbackRole");
        vars.put("projectId", callBack.getProjectId());

        String instId = workflowService.startProcess("callback", businessKey, vars);

        // 3. 回写流程实例ID
        callBack.setInstId(instId);
        callBackMapper.updateById(callBack);

        // 4. 添加审批意见（发起申请）
        workflowService.addApprovalComment(callBack.getId(), "callback", null, instId,
                STATE_PENDING, "发起回访申请");
        // 注: 流程启动后自动进入callbackApproval节点，等待callbackManager审批
        // 无需手动完成开始节点任务，Flowable会在startEvent后自动流转到第一个userTask
    }

    // ==================== 迁移自 CallBackAction - 查看回访详情 ====================

    @Override
    public Map<String, Object> getCallBackReadData(Long callBackId, String taskId) {
        // 迁移自: CallBackAction.read()
        Map<String, Object> result = new HashMap<>();
        PmsCallBack cb = callBackMapper.selectById(callBackId);
        if (cb == null) throw new BusinessException("回访记录不存在");
        result.put("callBack", cb);

        // 获取项目信息
        if (cb.getProjectId() != null) {
            PmsProject project = projectMapper.selectById(cb.getProjectId());
            result.put("project", project);
            // 获取项目成员
            List<PmsProjectMember> members = projectMemberMapper.selectList(
                new LambdaQueryWrapper<PmsProjectMember>()
                    .eq(PmsProjectMember::getProjectId, cb.getProjectId()));
            result.put("projectMemberList", members);
        }

        // 获取审批意见
        result.put("commentList", queryCallBackComment(callBackId));
        return result;
    }

    // ==================== 迁移自 CallBackAction - 驳回后重新提交 ====================

    @Override
    public Map<String, Object> getResubmitFormData(Long callBackId, String taskId) {
        // 迁移自: CallBackAction.resubmit() 路径B
        return getCallBackReadData(callBackId, taskId);
    }

    @Override
    @Transactional
    public void reSubmitCallBackFlow(PmsCallBack callBack, String comment) {
        // 迁移自: CallBackAction.resubmit() 路径A -> callBackService.reSubmitCallBackFlow()
        PmsCallBack existing = callBackMapper.selectById(callBack.getId());
        if (existing == null) throw new BusinessException("回访记录不存在");

        if (callBack.getProjectId() != null) existing.setProjectId(callBack.getProjectId());
        if (callBack.getProjectCode() != null) existing.setProjectCode(callBack.getProjectCode());
        if (callBack.getProjectName() != null) existing.setProjectName(callBack.getProjectName());
        if (callBack.getOfficeCode() != null) existing.setOfficeCode(callBack.getOfficeCode());

        existing.setApplyState(STATE_PENDING);
        existing.setApplyTime(LocalDateTime.now());
        existing.setApplyBy(SecurityUtil.getCurrentUsername());
        callBackMapper.updateById(existing);

        // 通过Flowable完成重新提交任务
        if (StringUtils.hasText(existing.getInstId())) {
            WorkflowTaskVO task = workflowService.getTaskByProcessInstanceAndAssignee(
                    existing.getInstId(), SecurityUtil.getCurrentUsername());
            if (task != null) {
                Map<String, Object> vars = new HashMap<>();
                vars.put("outcome", 1);
                workflowService.completeTask(task.getTaskId(), existing.getInstId(), comment, vars);
            }
        }

        // 添加审批意见
        workflowService.addApprovalComment(existing.getId(), "callback", null,
                existing.getInstId(), STATE_PENDING, "重新提交: " + comment);
    }

    // ==================== 迁移自 CallBackAction - 审批/问卷 ====================

    @Override
    public Map<String, Object> getAuditFormData(Long callBackId, String taskId, Long quesnaireId) {
        // 迁移自: CallBackAction.aduit() 路径C
        Map<String, Object> result = getCallBackReadData(callBackId, taskId);

        // 获取生效的问卷分类
        result.put("quesnaireList", findActiveQuesnaireList());

        // 获取问卷模板的内容或者已填写的问卷内容
        PmsCallBack cb = (PmsCallBack) result.get("callBack");
        Long effectiveQuesnaireId = quesnaireId;
        if (effectiveQuesnaireId == null || effectiveQuesnaireId == 0) {
            effectiveQuesnaireId = cb != null ? cb.getQuesnaireId() : null;
        }
        if (effectiveQuesnaireId != null && effectiveQuesnaireId > 0) {
            result.putAll(getCbForm(effectiveQuesnaireId));
        }

        return result;
    }

    @Override
    @Transactional
    public void saveQuestionnaire(PmsCallBack callBack, PmClQuesnaireResultHeader resultHeader,
                                  List<PmClQuesnaireResultLine> resultLines) {
        // 迁移自: CallBackAction.aduit() 路径A
        if (resultHeader == null || resultHeader.getStatus() == null || resultHeader.getStatus() == 0) {
            return;
        }

        // 检查是否需要计算问卷分数，并进行计算
        queryQuesnaireScore(resultHeader, resultLines);

        // 每次保存问卷草稿或提交问卷都会重新生成一份数据保存在数据库
        insertCallBackQuesnaire(callBack, resultHeader, resultLines);
    }

    @Override
    @Transactional
    public void submitCallBackFlow(PmsCallBack callBack, String comment) {
        // 迁移自: CallBackServiceImpl.submitCallBackFlow()
        // 老系统逻辑: 获取流程变量→查询任务→办理任务→增加审批意见→更新项目闭环状态
        PmsCallBack cb = callBackMapper.selectById(callBack.getId());
        if (cb == null) throw new BusinessException("回访记录不存在");

        // 通过Flowable完成审批任务
        if (StringUtils.hasText(cb.getInstId())) {
            // BPMN中callbackApproval使用candidateGroups="callback"，任务在候选组中
            // 需要先认领再完成
            WorkflowTaskVO task = workflowService.getTaskByProcessInstanceAndAssignee(
                    cb.getInstId(), SecurityUtil.getCurrentUsername());
            if (task == null) {
                // 任务在候选组中，查询并认领
                List<WorkflowTaskVO> tasks = workflowService.getTasksByProcessInstanceId(cb.getInstId());
                if (tasks != null && !tasks.isEmpty()) {
                    task = tasks.get(0);
                    workflowService.claimTask(task.getTaskId(), SecurityUtil.getCurrentUsername());
                }
            }
            if (task != null) {
                Map<String, Object> vars = new HashMap<>();
                vars.put("outcome", 1);
                vars.put("callbackManager", SecurityUtil.getCurrentUsername());
                workflowService.completeTask(task.getTaskId(), cb.getInstId(), comment, vars);
            }
        }

        // 更新状态
        cb.setApplyState(STATE_APPROVED);
        cb.setEndTime(LocalDateTime.now());
        callBackMapper.updateById(cb);

        // 添加审批意见
        workflowService.addApprovalComment(cb.getId(), "callback", null,
                cb.getInstId(), STATE_APPROVED, comment);
    }

    // ==================== 迁移自 CallBackAction - 问卷相关 ====================

    @Override
    public Map<String, Object> queryQuesnaireDetail(int quesnaireId) {
        // 迁移自: CallBackAction.seeQuesnaire() -> queryQuesnaire()
        Map<String, Object> result = new HashMap<>();

        if (quesnaireId != 0) {
            // 1. 查询问卷模板信息
            Long templateId = queryQuesnaireTemplateId((long) quesnaireId);

            // 2. 获取问卷结果行信息
            List<PmClQuesnaireResultLine> resultLines = resultLineMapper.selectList(
                new LambdaQueryWrapper<PmClQuesnaireResultLine>()
                    .eq(PmClQuesnaireResultLine::getResultHeaderId, quesnaireId));
            result.put("resultLines", resultLines);

            // 3. 获取问题类型基础数据 & 计算各类型分数
            List<SysBasicData> quesTypeList = basicDataService.queryAllByType(MessageUtil.CL_QUESNAIRE_LINEID);
            result.put("quesTypeList", quesTypeList);
            result.put("quesResultMarkList", getQuesTypeScore(resultLines, quesTypeList));

            // 4. 获取总分以及是否通过
            PmClQuesnaireResultHeader resultHeader = resultHeaderMapper.selectById((long) quesnaireId);
            result.put("resultHeader", resultHeader);

            // 5. 获取问卷模板头信息
            if (templateId != null) {
                PmClosedLoopQuesnaire templateHeader = quesnaireMapper.selectById(templateId);
                if (templateHeader != null) {
                    result.put("templateHeader", templateHeader);

                    // 获取评分规则说明
                    String markIndexs = getQuesnaireMarkIndexs(templateId);
                    if (markIndexs != null && !markIndexs.isEmpty()) {
                        PmClosedLoopMarkFactory factory = new PmClosedLoopMarkFactory();
                        result.put("markList", factory.getMarksExplain(markIndexs));
                    }

                    // 获取问卷模板行信息
                    List<PmClosedLoopQuesnaireLine> templateLines = quesnaireLineMapper.selectList(
                        new LambdaQueryWrapper<PmClosedLoopQuesnaireLine>()
                            .eq(PmClosedLoopQuesnaireLine::getHeaderId, templateId)
                            .orderByAsc(PmClosedLoopQuesnaireLine::getSortOrder));
                    result.put("templateLines", templateLines);

                    // 获取问卷模板选项信息（批量查询）
                    List<PmClosedLoopQuesnaireOpt> templateOpts = quesnaireOptMapper.selectList(
                        new LambdaQueryWrapper<PmClosedLoopQuesnaireOpt>()
                            .eq(PmClosedLoopQuesnaireOpt::getLineId, templateId)
                            .orderByAsc(PmClosedLoopQuesnaireOpt::getSortOrder));
                    result.put("templateOptions", templateOpts);
                }
            }
        }

        return result;
    }

    @Override
    @Transactional
    public void insertCallBackQuesnaire(PmsCallBack callBack, PmClQuesnaireResultHeader resultHeader,
                                         List<PmClQuesnaireResultLine> resultLines) {
        // 迁移自: CallBackAction.aduit() -> callBackService.insertCallBackQuesnaire()
        resultHeader.setFillPeopleId(SecurityUtil.getCurrentUsername());
        resultHeader.setFillTime(LocalDateTime.now());
        resultHeader.setProjectId(callBack.getProjectId());
        resultHeader.setProjectCode(callBack.getProjectCode());

        // 计算总分
        int totalScore = 0;
        if (resultLines != null) {
            for (PmClQuesnaireResultLine line : resultLines) {
                if (line.getScore() != null) totalScore += line.getScore();
            }
        }
        resultHeader.setTotalScore(totalScore);

        // 插入或更新问卷头
        if (resultHeader.getId() != null) {
            resultHeaderMapper.updateById(resultHeader);
            // 删除旧行，重新生成
            resultLineMapper.delete(new LambdaQueryWrapper<PmClQuesnaireResultLine>()
                .eq(PmClQuesnaireResultLine::getResultHeaderId, resultHeader.getId()));
        } else {
            resultHeaderMapper.insert(resultHeader);
        }

        // 插入问卷结果行
        if (resultLines != null) {
            for (PmClQuesnaireResultLine line : resultLines) {
                line.setResultHeaderId(resultHeader.getId());
                resultLineMapper.insert(line);
            }
        }

        // 更新回访记录关联的问卷ID
        callBack.setQuesnaireId(resultHeader.getId());
        callBackMapper.updateById(callBack);
    }

    // ==================== 迁移自 CallBackAction - 审批意见 ====================

    @Override
    public List<DpComment> queryCallBackComment(Long callBackId) {
        // 迁移自: CallBackAction.read() -> callBackService.queryCallBackComment()
        PmsCallBack cb = callBackMapper.selectById(callBackId);
        if (cb == null || !StringUtils.hasText(cb.getInstId())) {
            return Collections.emptyList();
        }
        return commentMapper.selectList(
            new LambdaQueryWrapper<DpComment>()
                .eq(DpComment::getInstId, cb.getInstId())
                .orderByAsc(DpComment::getAssigneeTime));
    }

    // ==================== 迁移自 CallBackAction - 问卷辅助 ====================

    @Override
    public Long queryQuesnaireTemplateId(Long quesnaireId) {
        // 迁移自: CallBackAction -> callBackService.queryQuesnaireTemplateId()
        PmClQuesnaireResultHeader header = resultHeaderMapper.selectById(quesnaireId);
        return header != null ? header.getQuesnaireTemplateHeaderId() : null;
    }

    @Override
    public Map<String, Object> queryCbQuesnaire(Long quesnaireId) {
        // 迁移自: CallBackAction -> callBackService.queryCbQuesnaire()
        Map<String, Object> result = new HashMap<>();

        PmClQuesnaireResultHeader header = resultHeaderMapper.selectById(quesnaireId);
        if (header == null) throw new BusinessException("问卷结果不存在");
        result.put("resultHeader", header);

        List<PmClQuesnaireResultLine> lines = resultLineMapper.selectList(
            new LambdaQueryWrapper<PmClQuesnaireResultLine>()
                .eq(PmClQuesnaireResultLine::getResultHeaderId, quesnaireId));
        result.put("resultLines", lines);

        // 查询问题类型 & 计算分数
        List<SysBasicData> quesTypeList = basicDataService.queryAllByType(MessageUtil.CL_QUESNAIRE_LINEID);
        result.put("quesTypeList", quesTypeList);
        result.put("quesResultMarkList", getQuesTypeScore(lines, quesTypeList));

        return result;
    }

    @Override
    public List<PmClosedLoopQuesnaire> findActiveQuesnaireList() {
        // 迁移自: CallBackAction.findPmClosedLoopQuesnaireList()
        return quesnaireMapper.selectList(
            new LambdaQueryWrapper<PmClosedLoopQuesnaire>()
                .eq(PmClosedLoopQuesnaire::getStatus, MessageUtil.CL_STATUS_SUBMIT)
                .orderByDesc(PmClosedLoopQuesnaire::getCreateTime));
    }

    // ==================== 私有方法 - 评分计算 ====================

    /**
     * 按问题类型汇总分数
     * 迁移自: CallBackAction.getQuesTypeScore()
     */
    private List<String> getQuesTypeScore(List<PmClQuesnaireResultLine> resultLines, List<SysBasicData> quesTypeList) {
        if (quesTypeList == null || quesTypeList.isEmpty()) {
            return null;
        }
        Map<String, Double> quesTypeMarkMap = new HashMap<>();
        for (PmClQuesnaireResultLine line : resultLines) {
            String quesType = line.getQuesTypeForCB();
            if (quesType != null) {
                double score = line.getScore() != null ? line.getScore() : 0;
                quesTypeMarkMap.merge(quesType, score, Double::sum);
            }
        }

        List<String> quesResultMarkList = new ArrayList<>();
        for (SysBasicData basicData : quesTypeList) {
            if (quesTypeMarkMap.containsKey(basicData.getDataCode())) {
                quesResultMarkList.add(basicData.getDataName() + "|" + basicData.getDataCode());
                quesResultMarkList.add(String.valueOf(quesTypeMarkMap.get(basicData.getDataCode())));
            }
        }
        return quesResultMarkList;
    }

    /**
     * 检查是否需要计算问卷分数，并进行计算
     * 迁移自: CallBackAction.queryQuesnaireScore()
     */
    private void queryQuesnaireScore(PmClQuesnaireResultHeader resultHeader,
                                      List<PmClQuesnaireResultLine> resultLines) {
        if (resultHeader == null || resultHeader.getQuesnaireTemplateHeaderId() == null) {
            return;
        }

        // 查询选项Map
        Map<Long, PmClosedLoopQuesnaireOpt> optMap = queryQuesnaireOptMap(resultHeader.getQuesnaireTemplateHeaderId());

        // 查询问卷模板
        PmClosedLoopQuesnaire quesnaire = quesnaireMapper.selectById(resultHeader.getQuesnaireTemplateHeaderId());
        if (quesnaire == null) return;

        // 执行计分
        quesMark(quesnaire, optMap, resultLines, resultHeader);
    }

    /**
     * 查询问卷选项Map（optionId -> option）
     * 迁移自: CallBackAction.queryQuesnaireOpt()
     */
    private Map<Long, PmClosedLoopQuesnaireOpt> queryQuesnaireOptMap(Long templateHeaderId) {
        List<PmClosedLoopQuesnaireOpt> opts = quesnaireOptMapper.selectList(
            new LambdaQueryWrapper<PmClosedLoopQuesnaireOpt>()
                .eq(PmClosedLoopQuesnaireOpt::getLineId, templateHeaderId));
        Map<Long, PmClosedLoopQuesnaireOpt> optMap = new HashMap<>();
        for (PmClosedLoopQuesnaireOpt opt : opts) {
            optMap.put(opt.getId(), opt);
        }
        return optMap;
    }

    /**
     * 核心计分逻辑
     * 迁移自: CallBackAction.quesMark()
     *
     * @return 1=成功, -1=失败
     */
    private int quesMark(PmClosedLoopQuesnaire quesnaire, Map<Long, PmClosedLoopQuesnaireOpt> optMap,
                          List<PmClQuesnaireResultLine> resultLines, PmClQuesnaireResultHeader resultHeader) {
        if (resultLines == null || resultLines.isEmpty()) return -1;

        double totalScore = 0;
        StringBuilder quesAnwBuilder = new StringBuilder();
        String quesTypeForCB = resultLines.get(0).getQuesTypeForCB();
        quesAnwBuilder.append(quesTypeForCB).append(":");
        StringBuilder evaResultBuilder = new StringBuilder();
        int i = 0;

        for (PmClQuesnaireResultLine line : resultLines) {
            if (line == null) return -1;

            if (line.getQuestionTemplateOptId() != null && line.getQuestionTemplateOptId() != 0) {
                PmClosedLoopQuesnaireOpt opt = optMap.get(line.getQuestionTemplateOptId());
                if (opt == null) return -1;

                if (!quesTypeForCB.equals(line.getQuesTypeForCB())) {
                    quesAnwBuilder.append(";");
                    quesAnwBuilder.append(line.getQuesTypeForCB()).append(":");
                }
                quesTypeForCB = line.getQuesTypeForCB();

                // 选项字母: A, B, C, D...
                char optChar = (char) (((int) 'A') - 1 + opt.getOptionNo());
                quesAnwBuilder.append(i).append("-").append(line.getQuesTemplateLineNum())
                    .append("|").append(optChar).append(",");

                line.setScore(opt.getScore());
                totalScore += opt.getScore();
            }
            i++;
        }
        quesAnwBuilder.append(";");

        resultHeader.setQuesMarkScore(totalScore);
        resultHeader.setQuesAnw(quesAnwBuilder.toString());

        // 获取计分规则并计分
        String markIndexs = getQuesnaireMarkIndexs(quesnaire.getId());
        if (markIndexs != null && !markIndexs.isEmpty()) {
            PmClosedLoopMarkFactory factory = new PmClosedLoopMarkFactory();
            List<PmClosedLoopMark> marks = factory.getMarks(markIndexs);
            if (marks != null) {
                for (PmClosedLoopMark mark : marks) {
                    String evaResult = mark.quesMark(resultHeader);
                    if ("-2".equals(evaResult)) {
                        return -1;
                    } else if ("pass".equals(evaResult)) {
                        evaResult = "1";
                    } else if (!"-1".equals(evaResult)) {
                        // 指定题目驳回
                        if (evaResult.contains(",")) {
                            for (String optIndex : evaResult.split(",")) {
                                if (!optIndex.isEmpty()) {
                                    int idx = Integer.parseInt(optIndex.trim());
                                    if (idx >= 0 && idx < resultLines.size()) {
                                        resultLines.get(idx).setQuesEvaResult(-1);
                                    }
                                }
                            }
                        } else {
                            int idx = Integer.parseInt(evaResult.trim());
                            if (idx >= 0 && idx < resultLines.size()) {
                                resultLines.get(idx).setQuesEvaResult(-1);
                            }
                        }
                        evaResult = "-1";
                    }
                    evaResultBuilder.append(evaResult);
                }
            }
        }

        // 判断最终结果
        if (evaResultBuilder.length() > 0 &&
            evaResultBuilder.toString().contains(String.valueOf(MessageUtil.CL_EVALU_RESULT_REJECT))) {
            resultHeader.setQuesMarkResult(MessageUtil.CL_EVALU_RESULT_REJECT);
        } else {
            resultHeader.setQuesMarkResult(MessageUtil.CL_EVALU_RESULT_AGREE);
        }
        return 1;
    }

    /**
     * 获取问卷模板的评分规则索引
     */
    private String getQuesnaireMarkIndexs(Long quesnaireId) {
        PmClosedLoopQuesnaire q = quesnaireMapper.selectById(quesnaireId);
        return q != null ? q.getMarkIndexs() : null;
    }

    /**
     * 获取问卷表单数据（已有问卷或新问卷模板）
     * 迁移自: CallBackAction.getCbForm()
     */
    private Map<String, Object> getCbForm(Long quesnaireId) {
        Map<String, Object> result = new HashMap<>();
        if (quesnaireId == null || quesnaireId <= 0) return result;

        // 1. 获取已填写的问卷
        Map<String, Object> cbQuesnaire = queryCbQuesnaire(quesnaireId);
        result.putAll(cbQuesnaire);

        // 2. 获取templateId
        PmClQuesnaireResultHeader resultHeader = (PmClQuesnaireResultHeader) cbQuesnaire.get("resultHeader");
        if (resultHeader != null && resultHeader.getQuesnaireTemplateHeaderId() != null) {
            Long templateId = resultHeader.getQuesnaireTemplateHeaderId();

            // 3. 获取问卷模板头信息
            PmClosedLoopQuesnaire templateHeader = quesnaireMapper.selectById(templateId);
            if (templateHeader != null) {
                result.put("templateHeader", templateHeader);

                // 获取评分规则说明
                String markIndexs = getQuesnaireMarkIndexs(templateId);
                if (markIndexs != null && !markIndexs.isEmpty()) {
                    PmClosedLoopMarkFactory factory = new PmClosedLoopMarkFactory();
                    result.put("markList", factory.getMarksExplain(markIndexs));
                }

                // 4. 获取问卷模板行信息
                List<PmClosedLoopQuesnaireLine> templateLines = quesnaireLineMapper.selectList(
                    new LambdaQueryWrapper<PmClosedLoopQuesnaireLine>()
                        .eq(PmClosedLoopQuesnaireLine::getHeaderId, templateId)
                        .orderByAsc(PmClosedLoopQuesnaireLine::getSortOrder));
                result.put("templateLines", templateLines);

                // 5. 获取问卷模板选项信息（批量查询）
                List<PmClosedLoopQuesnaireOpt> templateOpts = quesnaireOptMapper.selectList(
                    new LambdaQueryWrapper<PmClosedLoopQuesnaireOpt>()
                        .eq(PmClosedLoopQuesnaireOpt::getLineId, templateId)
                        .orderByAsc(PmClosedLoopQuesnaireOpt::getSortOrder));
                result.put("templateOptions", templateOpts);
            }
        }

        return result;
    }

    /**
     * 保存审批意见
     */
    private void saveComment(String instId, String comment, Integer result) {
        if (!StringUtils.hasText(instId) || !StringUtils.hasText(comment)) return;
        DpComment dpComment = new DpComment();
        dpComment.setInstId(instId);
        dpComment.setAssignee(SecurityUtil.getCurrentUsername());
        dpComment.setMessage(comment);
        dpComment.setResult(result);
        dpComment.setAssigneeTime(LocalDateTime.now());
        commentMapper.insert(dpComment);
    }
}
