package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.mapper.*;
import com.dp.plat.model.entity.*;
import com.dp.plat.service.CallBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

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
        callBack.setApplyState(-1);
        callBack.setCreateTime(LocalDateTime.now());
        callBackMapper.insert(callBack);
    }

    @Override
    @Transactional
    public void startFlow(Long id) {
        PmsCallBack cb = callBackMapper.selectById(id);
        if (cb == null) throw new BusinessException("回访记录不存在");
        cb.setApplyState(0);
        cb.setApplyTime(LocalDateTime.now());
        callBackMapper.updateById(cb);
    }

    @Override
    @Transactional
    public void approve(Long id, String comment, boolean approved) {
        PmsCallBack cb = callBackMapper.selectById(id);
        if (cb == null) throw new BusinessException("回访记录不存在");
        cb.setApplyState(approved ? 1 : 2);
        if (!approved) cb.setEndTime(LocalDateTime.now());
        callBackMapper.updateById(cb);
    }

    @Override
    @Transactional
    public void resubmit(Long id, PmsCallBack callBack) {
        PmsCallBack existing = callBackMapper.selectById(id);
        if (existing == null) throw new BusinessException("回访记录不存在");
        existing.setApplyState(0);
        existing.setApplyTime(LocalDateTime.now());
        callBackMapper.updateById(existing);
    }

    @Override
    public Map<String, Object> queryQuestionnaire(Long callBackId) {
        // 迁移自: CallBackAction.seeQuesnaire() -> CallBackServiceImpl.getCbForm()
        PmsCallBack cb = callBackMapper.selectById(callBackId);
        if (cb == null) throw new BusinessException("回访记录不存在");
        Map<String, Object> result = new HashMap<>();
        result.put("callBack", cb);

        // 查询关联的问卷模板和已填写的问卷结果
        // 迁移自: CallBackServiceImpl.getCbForm() -> queryQuesnaireTemplateId()
        // 1. 查询生效的问卷模板
        List<PmClosedLoopQuesnaire> questionnaires = quesnaireMapper.selectList(
            new LambdaQueryWrapper<PmClosedLoopQuesnaire>()
                .eq(PmClosedLoopQuesnaire::getStatus, 1)
                .orderByDesc(PmClosedLoopQuesnaire::getCreateTime));
        result.put("questionnaire", questionnaires);

        // 2. 如果有问卷模板，查询模板行和选项
        if (questionnaires != null && !questionnaires.isEmpty()) {
            Long headerId = questionnaires.get(0).getId();
            List<PmClosedLoopQuesnaireLine> lines = quesnaireLineMapper.selectList(
                new LambdaQueryWrapper<PmClosedLoopQuesnaireLine>()
                    .eq(PmClosedLoopQuesnaireLine::getHeaderId, headerId)
                    .orderByAsc(PmClosedLoopQuesnaireLine::getSortOrder));
            result.put("questionnaireLines", lines);

            // 3. 查询每个行的选项
            List<PmClosedLoopQuesnaireOpt> allOptions = new ArrayList<>();
            for (PmClosedLoopQuesnaireLine line : lines) {
                List<PmClosedLoopQuesnaireOpt> options = quesnaireOptMapper.selectList(
                    new LambdaQueryWrapper<PmClosedLoopQuesnaireOpt>()
                        .eq(PmClosedLoopQuesnaireOpt::getLineId, line.getId())
                        .orderByAsc(PmClosedLoopQuesnaireOpt::getSortOrder));
                allOptions.addAll(options);
            }
            result.put("questionnaireOptions", allOptions);
        } else {
            result.put("questionnaireLines", Collections.emptyList());
            result.put("questionnaireOptions", Collections.emptyList());
        }

        // 4. 查询已填写的问卷结果
        PmClQuesnaireResultHeader resultHeader = resultHeaderMapper.selectOne(
            new LambdaQueryWrapper<PmClQuesnaireResultHeader>()
                .eq(PmClQuesnaireResultHeader::getStatus, 1)
                .last("LIMIT 1"));
        if (resultHeader != null) {
            List<PmClQuesnaireResultLine> resultLines = resultLineMapper.selectList(
                new LambdaQueryWrapper<PmClQuesnaireResultLine>()
                    .eq(PmClQuesnaireResultLine::getResultHeaderId, resultHeader.getId()));
            result.put("resultHeader", resultHeader);
            result.put("resultLines", resultLines);
        }

        result.put("commentList", Collections.emptyList());
        return result;
    }
}
