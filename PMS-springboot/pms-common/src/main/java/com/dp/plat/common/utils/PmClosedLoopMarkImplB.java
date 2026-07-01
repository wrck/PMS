package com.dp.plat.common.utils;

import com.dp.plat.common.constants.MessageUtil;
import com.dp.plat.model.entity.PmClQuesnaireResultHeader;

/**
 * 评分规则B：工程师满意度任意单选题出现选项C则驳回
 * 迁移自老系统 PmClosedLoopMarkImplB
 */
public class PmClosedLoopMarkImplB implements PmClosedLoopMark {

    private static final String MARK_EXPLAIN = "问卷中，如果工程师满意度任意单选题出现选项C，则驳回";

    @Override
    public String quesMark(PmClQuesnaireResultHeader resultHeader) {
        String quesAnw = resultHeader.getQuesAnw();
        if (quesAnw == null || quesAnw.isEmpty()) {
            return "-2";
        }
        String[] anwArr = quesAnw.split(";");
        StringBuilder resultBuilder = new StringBuilder();
        for (String anwArrEle : anwArr) {
            if (anwArrEle.contains(MessageUtil.CL_QUESNAIRE_LINE_TYPE3 + ":")) {
                String cleaned = anwArrEle.replace(MessageUtil.CL_QUESNAIRE_LINE_TYPE3 + ":", "");
                for (String optEle : cleaned.split(",")) {
                    if (optEle.contains("|C")) {
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
