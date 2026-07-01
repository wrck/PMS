package com.dp.plat.common.utils;

import com.dp.plat.model.entity.PmClQuesnaireResultHeader;

/**
 * 问卷评分接口 - 迁移自老系统 PmClosedLoopMark
 * 不同评分规则需创建一个类实现该接口
 */
public interface PmClosedLoopMark {

    /**
     * 评分方法
     * @param resultHeader 问卷结果头对象（包含quesMarkScore, quesPassScore, quesAnw等）
     * @return 评分结果: "pass"=通过, "-1"=驳回, "-2"=数据异常, "索引,索引"=指定题目驳回
     */
    String quesMark(PmClQuesnaireResultHeader resultHeader);

    /**
     * 评分规则说明
     */
    String getMarkExplain();
}
