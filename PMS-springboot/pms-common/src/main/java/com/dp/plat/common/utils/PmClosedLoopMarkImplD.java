package com.dp.plat.common.utils;

import com.dp.plat.model.entity.PmClQuesnaireResultHeader;

/**
 * 评分规则D：第二题选C或第四题选B则驳回
 * 迁移自老系统 PmClosedLoopMarkImplD
 */
public class PmClosedLoopMarkImplD implements PmClosedLoopMark {

    private static final String MARK_EXPLAIN = "问卷中，如果第二题选C或者第四题选择B，则驳回";

    @Override
    public String quesMark(PmClQuesnaireResultHeader resultHeader) {
        String quesAnw = resultHeader.getQuesAnw();
        if (quesAnw == null || quesAnw.isEmpty()) {
            return "-2";
        }
        String[] anwArr = quesAnw.split(";");
        StringBuilder resultBuilder = new StringBuilder();
        for (String anwArrEle : anwArr) {
            int start = anwArrEle.indexOf(":");
            if (start >= 0) {
                anwArrEle = anwArrEle.substring(start + 1);
            }
            for (String optEle : anwArrEle.split(",")) {
                if (optEle.contains("-2|C") || optEle.contains("-4|B")) {
                    resultBuilder.append(optEle.split("-")[0]).append(",");
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
