package com.dp.plat.dao;

import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.PmClQuesnaireResultHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultLine;
import com.dp.plat.data.bean.PmClosedLoopQuesnaire;
import com.dp.plat.data.bean.PmClosedLoopQuesnaireLine;
import com.dp.plat.data.bean.PmClosedLoopQuesnaireOpt;
import com.dp.plat.param.DisplayParam;

public interface PmClosedLoopQuesnaireDao {
	/**
	 * 插入问卷模板头信息
	 * @param pmClosedLoopQuesnaire
	 * @return TODO
	 */
	int insertQuesnaireHeader(PmClosedLoopQuesnaire pmClosedLoopQuesnaire);
	
	/**
	 * 获得问卷模板头信息集合
	 * @param pmClosedLoopQuesnaire
	 * @param displayParam
	 * @return 
	 */
	List<PmClosedLoopQuesnaire> selectQuesnaireHeaderList(PmClosedLoopQuesnaire pmClosedLoopQuesnaire,DisplayParam displayParam);

	/**
	 * 插入问卷行信息（问卷问题）集合
	 * @param pmClosedLoopQuesnaireLine
	 * @return
	 */
	int insertQuesnaireLineList(PmClosedLoopQuesnaireLine pmClosedLoopQuesnaireLine);
	
	/**
	 *插入问卷选项信息集合
	 * @param pmClosedLoopQuesnaireOptList
	 * @return
	 */
	int insertQuesnaireOptList(List<PmClosedLoopQuesnaireOpt>pmClosedLoopQuesnaireOptList,int questionId);
	
	/**
	 * 获取问卷行信息集合
	 * @param pmClosedLoopQuesnaireLine
	 * @param sqlType 
	 * @return
	 */
	List<PmClosedLoopQuesnaireLine>queryPmClQuesnaireLineList(PmClosedLoopQuesnaireLine pmClosedLoopQuesnaireLine, String sqlType);
	
	/**
	 * 获取问卷选项信息集合
	 * @param pmClosedLoopQuesnaireOpt
	 * @param sqlType 
	 * @return
	 */
	List<PmClosedLoopQuesnaireOpt>queryPmClosedLoopQuesnaireOptList(PmClosedLoopQuesnaireOpt pmClosedLoopQuesnaireOpt, String sqlType);
	
	/**
	 * 获取问卷选项map
	 * @param pmClosedLoopQuesnaireOpt
	 * @return
	 */
	Map<Integer,PmClosedLoopQuesnaireOpt> queryPmClosedLoopQuesnaireOptMap(PmClosedLoopQuesnaireOpt pmClosedLoopQuesnaireOpt);
	
	/**
	 * 修改问卷头信息
	 * @param pmClosedLoopQuesnaire
	 */
	void updateQuesHeader(PmClosedLoopQuesnaire pmClosedLoopQuesnaire);
	
	/**
	 *问卷生效
	 * @param pmClosedLoopQuesnaire 
	 * @return TODO
	 */
	int updateQuesStatus(PmClosedLoopQuesnaire pmClosedLoopQuesnaire);
	
	/**
	 * 删除问卷行信息
	 * @param pmClosedLoopQuesnaireLine
	 * @return
	 */
	int deleteQuesLine(PmClosedLoopQuesnaireLine pmClosedLoopQuesnaireLine);
	
	/**
	 *删除问卷选项信息
	 * @param pmClosedLoopQuesnaireOpt
	 */
	void deleteQuesOpt(PmClosedLoopQuesnaireOpt pmClosedLoopQuesnaireOpt);
	
	/**
	 * 删除问卷行信息后，更新行信息的题号
	 * @param pmClosedLoopQuesnaireLine
	 */
	void updateLineQuesnum(PmClosedLoopQuesnaireLine pmClosedLoopQuesnaireLine);
	
	/**
	 * 删除问卷头信
	 * @param pmClosedLoopQuesnaire
	 * @return
	 */
	int deleteQuesHeader(PmClosedLoopQuesnaire pmClosedLoopQuesnaire);
	
	/**
	 * 删除问卷下的全部行信息
	 * @param quesnaireTemplateHeaderId
	 */
	void deleteLineAll(int quesnaireTemplateHeaderId);
	
	/**
	 *删除问卷下的全部选项信息
	 * @param quesnaireTemplateHeaderId
	 */
	void deleteOptAll(int quesnaireTemplateHeaderId);

    /**
     * @param pmClQuesnaireResultHeader
     * @return
     */
    int addPmClQuesResultHeader(PmClQuesnaireResultHeader pmClQuesnaireResultHeader);

    /**
     * @param pmClQuesnaireResultLineList
     * @param pmClQuesnaireResultHeaderId
     */
    void addPmClQuesResultLineList(List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList, int pmClQuesnaireResultHeaderId);

    /**
     * @param pmClQuesnaireResultLineList
     * @param pmClQuesnaireResultHeader
     */
	void addPmClQuesResultLineList(List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList, PmClQuesnaireResultHeader pmClQuesnaireResultHeader);
}
