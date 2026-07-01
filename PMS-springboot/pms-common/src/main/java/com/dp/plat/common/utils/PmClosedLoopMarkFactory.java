package com.dp.plat.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 评分规则工厂 - 迁移自老系统 PmClosedLoopMarkFactory
 *
 * 通过markIndex（逗号分隔的索引字符串）获取对应的评分规则实例
 * 索引: 0=A(达标分), 1=B(工程师C驳回), 2=C(设备C/D驳回), 3=D(特定题驳回)
 */
public class PmClosedLoopMarkFactory {

    private final List<PmClosedLoopMark> markList;

    public PmClosedLoopMarkFactory() {
        markList = new ArrayList<>();
        markList.add(new PmClosedLoopMarkImplA()); // 0
        markList.add(new PmClosedLoopMarkImplB()); // 1
        markList.add(new PmClosedLoopMarkImplC()); // 2
        markList.add(new PmClosedLoopMarkImplD()); // 3
    }

    /** 获取所有评分规则 */
    public List<PmClosedLoopMark> getAllMark() {
        return markList;
    }

    /**
     * 根据索引字符串获取对应的评分规则列表
     * @param markIndex 逗号分隔的索引，如 "0,1,2"
     * @return 评分规则列表，无效索引返回null
     */
    public List<PmClosedLoopMark> getMarks(String markIndex) {
        if (markIndex == null || markIndex.isEmpty()) {
            return null;
        }
        List<PmClosedLoopMark> subList = new ArrayList<>();
        for (String indexStr : markIndex.split(",")) {
            int idx = Integer.parseInt(indexStr.trim());
            if (idx >= 0 && idx < markList.size()) {
                subList.add(markList.get(idx));
            } else {
                return null;
            }
        }
        return subList;
    }

    /**
     * 获取评分规则说明列表
     * @param markIndex 逗号分隔的索引
     * @return 规则说明列表
     */
    public List<String> getMarksExplain(String markIndex) {
        if (markIndex == null || markIndex.isEmpty()) {
            return null;
        }
        List<String> explainList = new ArrayList<>();
        for (String indexStr : markIndex.split(",")) {
            int idx = Integer.parseInt(indexStr.trim());
            if (idx >= 0 && idx < markList.size()) {
                explainList.add(markList.get(idx).getMarkExplain());
            } else {
                return null;
            }
        }
        return explainList;
    }

    /** 获取所有评分规则说明 */
    public List<String> getAllMarkExplain() {
        List<String> explainList = new ArrayList<>();
        for (PmClosedLoopMark mark : markList) {
            explainList.add(mark.getMarkExplain());
        }
        return explainList;
    }
}
