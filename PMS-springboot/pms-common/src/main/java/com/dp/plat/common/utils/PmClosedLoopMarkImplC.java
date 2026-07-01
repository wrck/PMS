package com.dp.plat.common.utils;

import com.dp.plat.common.constants.MessageUtil;
import com.dp.plat.model.entity.PmClQuesnaireResultHeader;

/**
 * 评分规则C：设备满意度任意单选题出现选项C或D则驳回
 * 迁移自老系统 PmClosedLoopMarkImplC
 */
public class PmClosedLoopMarkImplC implements PmClosedLoopMark {

    private static final String MARK_EXPLAIN = "问卷中，如果设备满意度任意单选题出现选项C或者D，则驳回";

    @Override
    public String quesMark(PmClQuesnaireResultHeader resultHeader) {
        String quesAnw = resultHeader.getQuesAnw();
        if (quesAnw == null || quesAnw.isEmpty()) {
            return "-2";
        }
        String[] anwArr = quesAnw.split(";");
        StringBuilder resultBuilder = new StringBuilder();
        for (String anwArrEle : anwArr) {
            if (anwArrEle.contains(MessageUtil.CL_QUESNAIRE_LINE_TYPE2 + ":")) {
                String cleaned = anwArrEle.replace(MessageUtil.CL_QUESNAIRE_LINE_TYPE2 + ":", "");
                for (String optEle : cleaned.split(",")) {
                    if (optEle.contains("|C") || optEle.contains("|D")) {
                        resultBuilder.append(optEle.split("-")[0]).append(",");
                    }
                }
            }
        }
        if (resultBuilder.length() <= 0) {
            resultBuilder.append("pass");
        }
        return resultBuilder.toString();
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
