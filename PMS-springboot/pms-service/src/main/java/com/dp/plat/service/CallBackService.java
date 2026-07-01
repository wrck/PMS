package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.model.entity.PmsCallBack;

import java.util.Map;

public interface CallBackService {
    IPage<PmsCallBack> queryCallBackPage(Integer pageNum, Integer pageSize, Long projectId, Integer applyState);
    PmsCallBack getCallBackDetail(Long id);
    void createCallBack(PmsCallBack callBack);
    void startFlow(Long id);
    void approve(Long id, String comment, boolean approved);
    void resubmit(Long id, PmsCallBack callBack);

    /** 查看回访问卷 - 迁移自 CallBackAction.seeQuesnaire() */
    Map<String, Object> queryQuestionnaire(Long callBackId);
}
