/**
 * 
 */
package com.dp.plat.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;

import com.dp.plat.context.SpringContext;
import com.dp.plat.data.bean.BasicDataBean;
import com.dp.plat.data.bean.PmClQuesnaireResultHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultLine;
import com.dp.plat.data.bean.PmClosedLoopQuesnaire;
import com.dp.plat.data.bean.PmClosedLoopQuesnaireLine;
import com.dp.plat.data.bean.PmClosedLoopQuesnaireOpt;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.CallBackService;
import com.dp.plat.service.PmClosedLoopQuesnaireService;
import com.dp.plat.service.PmClosedLoopService;

/**
 * @author w02611
 */
public class QuestionnarieUtil {

    /**
     * 检查是否需要计算问卷分数，并进行计算
     * 
     * @param pmClosedLoopQuesnaire
     * @param pmClQuesnaireResultLineList
     * @param pmClQuesnaireResultHeader
     */
    public static void queryQuesnaireScore(PmClosedLoopQuesnaire pmClosedLoopQuesnaire, PmClQuesnaireResultHeader pmClQuesnaireResultHeader,
            List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList) {
        Map<Integer, PmClosedLoopQuesnaireOpt> optMap = queryQuesnaireOpt(pmClQuesnaireResultHeader);
        queryPmClosedLoopQuesnaire(pmClQuesnaireResultHeader);
        quesMark(pmClosedLoopQuesnaire, optMap, pmClQuesnaireResultLineList, pmClQuesnaireResultHeader);
    }

    private static PmClosedLoopQuesnaire queryPmClosedLoopQuesnaire(PmClQuesnaireResultHeader pmClQuesnaireResultHeader) {
        PmClosedLoopQuesnaire pmClosedLoopQuesnaire = new PmClosedLoopQuesnaire();
        pmClosedLoopQuesnaire.setId(pmClQuesnaireResultHeader.getQuesnaireTemplateHeaderId());
        PmClosedLoopQuesnaireService pmClosedLoopQuesnaireService = SpringContext.getApplicationContext().getBean("pmClosedLoopQuesnaireService", PmClosedLoopQuesnaireService.class);
        pmClosedLoopQuesnaire = pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(pmClosedLoopQuesnaire, null).get(0);
        return pmClosedLoopQuesnaire;
    }

    private static int quesMark(PmClosedLoopQuesnaire quesObj, Map<Integer, PmClosedLoopQuesnaireOpt> optMap, List<PmClQuesnaireResultLine> resultLineListObj,
            PmClQuesnaireResultHeader resultHeaderObj) {
        double totalScore = 0;
        StringBuilder quesAnwBuilder = new StringBuilder();
        String quesTypeForCB = resultLineListObj.get(0).getQuesTypeForCB();
        quesAnwBuilder.append(quesTypeForCB + ":");
        StringBuilder evaResultBuilder = new StringBuilder();
        int i = 0;
        for (PmClQuesnaireResultLine pmClQuesnaireResultLineObj : resultLineListObj) {
            if (pmClQuesnaireResultLineObj == null) {
                return -1;
            }
            // 总分计算与答案字符串拼接
            if (pmClQuesnaireResultLineObj.getQuestionTemplateOptId() != 0) {
                if (optMap.get(pmClQuesnaireResultLineObj.getQuestionTemplateOptId()) == null) {
                    return -1;
                }
                if (!(quesTypeForCB.equals(pmClQuesnaireResultLineObj.getQuesTypeForCB()))) {
                    quesAnwBuilder.append(";");
                    quesAnwBuilder.append(pmClQuesnaireResultLineObj.getQuesTypeForCB() + ":");
                }
                quesTypeForCB = pmClQuesnaireResultLineObj.getQuesTypeForCB();

                char opt = (char) ((((int) 'A') - 1) + optMap.get(pmClQuesnaireResultLineObj.getQuestionTemplateOptId()).getQuestionOptionNum());
                quesAnwBuilder.append(i + "-" + pmClQuesnaireResultLineObj.getQuesTemplateLineNum() + "|" + opt + ","); // 10:1-2|C
                                                                                                                        // (10
                                                                                                                        // 题目回访类型，1
                                                                                                                        // 下表，
                                                                                                                        // 2
                                                                                                                        // 题号，
                                                                                                                        // C
                                                                                                                        // 选项)
                pmClQuesnaireResultLineObj.setQuestionScore(optMap.get(pmClQuesnaireResultLineObj.getQuestionTemplateOptId()).getQuestionOptionScore());
                totalScore += pmClQuesnaireResultLineObj.getQuestionScore();
            }
            i++;
        }
        quesAnwBuilder.append(";");

        resultHeaderObj.setQuesMarkScore(totalScore);
        resultHeaderObj.setQuesAnw(quesAnwBuilder.toString());

        // 获取计分规则并计分
        if (quesObj.getMarkIndexs() != null && !(quesObj.getMarkIndexs().equals(""))) {
            PmClosedLoopMarkFactory factory = new PmClosedLoopMarkFactory();
            if (factory.getMarks(quesObj.getMarkIndexs()) != null) {
                for (PmClosedLoopMark pmClosedLoopMarkObj : factory.getMarks(quesObj.getMarkIndexs())) {
                    String evaResultObj = pmClosedLoopMarkObj.quesMark(resultHeaderObj);
                    if (evaResultObj.equals("-2")) {
                        return -1;
                    } else if (evaResultObj.equals("pass")) {
                        evaResultObj = "1";
                    } else if (!evaResultObj.equals("-1")) {
                        if (evaResultObj.contains(",")) {
                            for (String optIndex : evaResultObj.split(",")) {
                                resultLineListObj.get(Integer.parseInt(optIndex)).setQuesEvaResult(-1);
                            }
                        } else {
                            resultLineListObj.get(Integer.parseInt(evaResultObj)).setQuesEvaResult(-1);
                        }
                        evaResultObj = "-1";
                    } else {

                    }
                    evaResultBuilder.append(evaResultObj);
                }
            }
        }
        if (evaResultBuilder.length() > 0 && evaResultBuilder.toString().contains(PmClosedLoopConstant.CL_EVALU_RESULT_REJECT + "")) {
            resultHeaderObj.setQuesMarkResult(PmClosedLoopConstant.CL_EVALU_RESULT_REJECT);

        } else {
            resultHeaderObj.setQuesMarkResult(PmClosedLoopConstant.CL_EVALU_RESULT_AGREE);
        }
        return 1;
    }

    private static Map<Integer, PmClosedLoopQuesnaireOpt> queryQuesnaireOpt(PmClQuesnaireResultHeader pmClQuesnaireResultHeader) {
        PmClosedLoopQuesnaireOpt pmClosedLoopQuesnaireOpt = new PmClosedLoopQuesnaireOpt();
        pmClosedLoopQuesnaireOpt.setQuesnaireTemplateHeaderId(pmClQuesnaireResultHeader.getQuesnaireTemplateHeaderId());
        pmClosedLoopQuesnaireOpt.setQuestionId(0);
        PmClosedLoopQuesnaireService pmClosedLoopQuesnaireService = SpringContext.getApplicationContext().getBean("pmClosedLoopQuesnaireService", PmClosedLoopQuesnaireService.class);
        Map<Integer, PmClosedLoopQuesnaireOpt> optMap = pmClosedLoopQuesnaireService.queryPmClosedLoopQuesnaireOptMap(pmClosedLoopQuesnaireOpt);
        return optMap;
    }

    // 只获取生效的问卷
    public static List<PmClosedLoopQuesnaire> findPmClosedLoopQuesnaireList(PmClosedLoopQuesnaire quesObj) {
        if (quesObj == null) {
            quesObj = new PmClosedLoopQuesnaire();
        }
        quesObj.setQuestionnaireStatus(PmClosedLoopConstant.CL_STATUS_SUBMIT);
        PmClosedLoopQuesnaireService pmClosedLoopQuesnaireService = SpringContext.getApplicationContext().getBean("pmClosedLoopQuesnaireService", PmClosedLoopQuesnaireService.class);
        return pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(quesObj, null);
    }

    public static Map<String, Object> getCbForm(Integer quesnaireId, PmClosedLoopQuesnaire pmClosedLoopQuesnaire, PmClQuesnaireResultHeader pmClQuesnaireResultHeader, int quesnaireState) {
        CallBackService callBackService = SpringContext.getApplicationContext().getBean("callBackService", CallBackService.class);
        PmClosedLoopService pmClosedLoopService = SpringContext.getApplicationContext().getBean("pmClosedLoopService", PmClosedLoopService.class);
        PmClosedLoopQuesnaireService pmClosedLoopQuesnaireService = SpringContext.getApplicationContext().getBean("pmClosedLoopQuesnaireService", PmClosedLoopQuesnaireService.class);

        List<String> quesResultMarkList = null;
        List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList = null;
        List<BasicDataBean> quesTypeList = null;
        if (quesnaireId != null && quesnaireId != 0) {
            // 2.复制给pmClosedLoopQuesnaire传递需要的问卷模板信息
            int templateId = callBackService.queryQuesnaireTemplateId(quesnaireId);

            if (pmClosedLoopQuesnaire == null) {
                pmClosedLoopQuesnaire = new PmClosedLoopQuesnaire();
                pmClosedLoopQuesnaire.setId(templateId);
            }
            // 3.判断选择的问卷模板是否等于已有草稿问卷的模板，等于则获取问卷结果行信息
            if (templateId == pmClosedLoopQuesnaire.getId()) {
                PmClQuesnaireResultLine pmClQuesnaireResultLine = new PmClQuesnaireResultLine();
                pmClQuesnaireResultLine.setQuesnaireResultHeaderId(quesnaireId);
                pmClQuesnaireResultLineList = pmClosedLoopService.queryPmClQuesResultLineList(pmClQuesnaireResultLine);
            }

            // 问卷状态 已提交 1 草稿-1
            if (quesnaireState != -1) {
                BasicDataService basicDataService = SpringContext.getApplicationContext().getBean("basicDataService", BasicDataService.class);

                // 获取问卷结果信息
                quesTypeList = basicDataService.queryBasicDataBeanAll(PmClosedLoopConstant.CL_QUESNAIRE_LINEID); // 获取问题类型
                quesResultMarkList = getQuesTypeScore(pmClQuesnaireResultLineList, quesTypeList);

                // 获取总分以及是否通过
                if (pmClQuesnaireResultHeader == null) {
                    pmClQuesnaireResultHeader = new PmClQuesnaireResultHeader();
                }
                pmClQuesnaireResultHeader.setId(quesnaireId);
                List<PmClQuesnaireResultHeader> resultHeaderList = pmClosedLoopService.queryPmClQuesResultHeaderList(pmClQuesnaireResultHeader);
                if (resultHeaderList != null && !resultHeaderList.isEmpty()) {
                    PmClQuesnaireResultHeader resultHeader = pmClosedLoopService.queryPmClQuesResultHeaderList(pmClQuesnaireResultHeader).get(0);
                    BeanUtils.copyProperties(resultHeader, pmClQuesnaireResultHeader);
                }
            }
        }

        // 1.获取问卷模板头信息
        PmClosedLoopQuesnaire quesnaire = pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(pmClosedLoopQuesnaire, null).get(0);
        BeanUtils.copyProperties(quesnaire, pmClosedLoopQuesnaire);

        // 获取评分规则说明
        PmClosedLoopMarkFactory factory = new PmClosedLoopMarkFactory();
        pmClosedLoopQuesnaire.setMarkList(factory.getMarks(pmClosedLoopQuesnaire.getMarkIndexs()));

        // 2.获取问卷模板行信息
        PmClosedLoopQuesnaireLine pmClosedLoopQuesnaireLine = new PmClosedLoopQuesnaireLine();
        pmClosedLoopQuesnaireLine.setQuesnaireTemplateHeaderId(pmClosedLoopQuesnaire.getId());
        List<PmClosedLoopQuesnaireLine> pmClosedLoopQuesnaireLineList = pmClosedLoopQuesnaireService.queryPmClQuesnaireLineList(pmClosedLoopQuesnaireLine, "asc");

        // 3.获取问卷模板选项信息
        PmClosedLoopQuesnaireOpt pmClosedLoopQuesnaireOpt = new PmClosedLoopQuesnaireOpt();
        pmClosedLoopQuesnaireOpt.setQuesnaireTemplateHeaderId(pmClosedLoopQuesnaire.getId());
        pmClosedLoopQuesnaireOpt.setQuestionId(0);
        List<PmClosedLoopQuesnaireOpt> pmClosedLoopQuesnaireOptList = pmClosedLoopQuesnaireService.queryPmClosedLoopQuesnaireOptList(pmClosedLoopQuesnaireOpt, "asc");

        Map<String, Object> result = new HashMap<>();
        result.put("quesTypeList", quesTypeList);
        result.put("quesResultMarkList", quesResultMarkList);
        result.put("pmClosedLoopQuesnaireLineList", pmClosedLoopQuesnaireLineList);
        result.put("pmClosedLoopQuesnaireOptList", pmClosedLoopQuesnaireOptList);
        result.put("pmClQuesnaireResultLineList", pmClQuesnaireResultLineList);
        return result;
    }

    /**
     * 计算问卷结果
     * 
     * @param quesnaireResultLineListObj
     * @param quesTypeList
     * @return
     */
    private static List<String> getQuesTypeScore(List<PmClQuesnaireResultLine> quesnaireResultLineListObj, List<BasicDataBean> quesTypeList) {
        Map<String, Double> quesTypeMarkMap = new HashMap<String, Double>();
        if (quesTypeList != null && quesnaireResultLineListObj != null) {
            List<String> quesResultMarkList = new ArrayList<String>();
            for (PmClQuesnaireResultLine pmClQuesnaireResultLineObj : quesnaireResultLineListObj) {
                double scoreObj = pmClQuesnaireResultLineObj.getQuestionScore();
                if (quesTypeMarkMap.get(pmClQuesnaireResultLineObj.getQuesTypeForCB()) != null) {
                    scoreObj += quesTypeMarkMap.get(pmClQuesnaireResultLineObj.getQuesTypeForCB());
                }
                quesTypeMarkMap.put(pmClQuesnaireResultLineObj.getQuesTypeForCB(), scoreObj);
            }

            for (BasicDataBean basicDataBeanObj : quesTypeList) {
                if (quesTypeMarkMap.get(basicDataBeanObj.getBasicDataId()) != null) {
                    quesResultMarkList.add(basicDataBeanObj.getBasicDataName() + "|" + basicDataBeanObj.getBasicDataId());
                    quesResultMarkList.add(quesTypeMarkMap.get(basicDataBeanObj.getBasicDataId()) + "");
                }
            }
            return quesResultMarkList;
        }
        return null;
    }

    public static int addQuestionnaireResult(PmClQuesnaireResultHeader pmClQuesnaireResultHeader, List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList) {
        PmClosedLoopQuesnaireService pmClosedLoopQuesnaireService = SpringContext.getApplicationContext().getBean("pmClosedLoopQuesnaireService", PmClosedLoopQuesnaireService.class);
        return pmClosedLoopQuesnaireService.addQuestionnaireResult(pmClQuesnaireResultHeader, pmClQuesnaireResultLineList);
    }
}
