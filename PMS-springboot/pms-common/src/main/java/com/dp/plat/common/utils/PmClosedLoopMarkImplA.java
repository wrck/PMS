package com.dp.plat.common.utils;

import com.dp.plat.model.entity.PmClQuesnaireResultHeader;

/**
 * 评分规则A：低于达标分则驳回
 * 迁移自老系统 PmClosedLoopMarkImplA
 */
public class PmClosedLoopMarkImplA implements PmClosedLoopMark {

    private static final String MARK_EXPLAIN = "低于问卷达标分数则驳回";

    @Override
    public String quesMark(PmClQuesnaireResultHeader resultHeader) {
        if (resultHeader.getQuesMarkScore() == null || resultHeader.getQuesPassScore() == null) {
            return "-2";
        }
        return resultHeader.getQuesMarkScore() < resultHeader.getQuesPassScore() ? "-1" : "pass";
    }

    @Override
    public String getMarkExplain() {
        return MARK_EXPLAIN;
    }

    @Override
    public String toString() {
        return MARK_EXPLAIN;
    }
}
