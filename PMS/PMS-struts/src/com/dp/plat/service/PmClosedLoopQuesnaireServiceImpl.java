package com.dp.plat.service;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.dp.plat.context.UserContext;
import com.dp.plat.dao.PmClosedLoopQuesnaireDao;
import com.dp.plat.data.bean.PmClQuesnaireResultHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultLine;
import com.dp.plat.data.bean.PmClosedLoopQuesnaire;
import com.dp.plat.data.bean.PmClosedLoopQuesnaireLine;
import com.dp.plat.data.bean.PmClosedLoopQuesnaireOpt;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.util.PmClosedLoopConstant;
import com.dp.plat.util.PmClosedLoopUtil;

public class PmClosedLoopQuesnaireServiceImpl extends BaseServiceImpl implements PmClosedLoopQuesnaireService{
	private PmClosedLoopQuesnaireDao pmClosedLoopQuesnaireDao;

	public PmClosedLoopQuesnaireDao getPmClosedLoopQuesnaireDao() {
		return pmClosedLoopQuesnaireDao;
	}

	public void setPmClosedLoopQuesnaireDao(
			PmClosedLoopQuesnaireDao pmClosedLoopQuesnaireDao) {
		this.pmClosedLoopQuesnaireDao = pmClosedLoopQuesnaireDao;
	}

	@Override
	public int insertQuesnaireHeader(PmClosedLoopQuesnaire pmClosedLoopQuesnaire) {
		//获得最大编号
		String maxQuesnaireNum="";
		List<PmClosedLoopQuesnaire>pmList=selectQuesnaireHeaderList(new PmClosedLoopQuesnaire(), new DisplayParam());
		if(pmList!=null&&pmList.size()>0){
			maxQuesnaireNum=pmList.get(0).getQuestionnaireTemplateNum()!=null?pmList.get(0).getQuestionnaireTemplateNum():"";
		}else{
			maxQuesnaireNum="";
		}
		//产生问卷编号
		pmClosedLoopQuesnaire.setQuestionnaireTemplateNum(PmClosedLoopUtil.geneticSerialNumber(PmClosedLoopConstant.QUESNAIRE_TEMPLATE_NUM_HEADER, maxQuesnaireNum));
		pmClosedLoopQuesnaire.setCreatedPerson(UserContext.getUserContext().getUser().getRealName());
		pmClosedLoopQuesnaire.setCreatedTime(new Date());
		return pmClosedLoopQuesnaireDao.insertQuesnaireHeader(pmClosedLoopQuesnaire);
	}

	@Override
	public List<PmClosedLoopQuesnaire> selectQuesnaireHeaderList(
			PmClosedLoopQuesnaire pmClosedLoopQuesnaire,
			DisplayParam displayParam) {
		return pmClosedLoopQuesnaireDao.selectQuesnaireHeaderList(pmClosedLoopQuesnaire, displayParam);
	}

	@Override
	public void insertQuesnaireLineOptList(
			PmClosedLoopQuesnaireLine pmClosedLoopQuesnaireLine,
			List<PmClosedLoopQuesnaireOpt> pmClosedLoopQuesnaireOptList) {
		pmClosedLoopQuesnaireLine.setQuestionStatus(1);
		int questionId=pmClosedLoopQuesnaireDao.insertQuesnaireLineList(pmClosedLoopQuesnaireLine);
		int i=0;
		for (PmClosedLoopQuesnaireOpt pmClosedLoopQuesnaireOpt : pmClosedLoopQuesnaireOptList) {
			pmClosedLoopQuesnaireOpt.setQuesnaireTemplateHeaderId(pmClosedLoopQuesnaireLine.getQuesnaireTemplateHeaderId());
			pmClosedLoopQuesnaireOpt.setQuestionOptionNum(++i);
		}
		//问答题不需要选项
		if(pmClosedLoopQuesnaireLine.getQuestionType()!=PmClosedLoopConstant.CL_QUESNAIRE_LINE_TYPE_AQ){
			pmClosedLoopQuesnaireDao.insertQuesnaireOptList(pmClosedLoopQuesnaireOptList,questionId);
		}
	}
	
	@Override
	public void updateQuesLineOpt(PmClosedLoopQuesnaireLine pmClosedLoopQuesnaireLine,List<PmClosedLoopQuesnaireOpt> pmClosedLoopQuesnaireOptList){
		int returnResult=pmClosedLoopQuesnaireDao.deleteQuesLine(pmClosedLoopQuesnaireLine);
		
		PmClosedLoopQuesnaireOpt optObj=new PmClosedLoopQuesnaireOpt();
		optObj.setQuestionId(pmClosedLoopQuesnaireLine.getId());
		deleteQuesOpt(optObj);
		
		if(returnResult<=0){
			throw new RuntimeException("题目更新错误！");
		}
		
		insertQuesnaireLineOptList(pmClosedLoopQuesnaireLine, pmClosedLoopQuesnaireOptList);
	}
	
	@Override
	public int deleteQuesLine(PmClosedLoopQuesnaireLine pmClosedLoopQuesnaireLine) {
		int returnResult=pmClosedLoopQuesnaireDao.deleteQuesLine(pmClosedLoopQuesnaireLine);
		
		PmClosedLoopQuesnaireOpt optObj=new PmClosedLoopQuesnaireOpt();
		optObj.setQuestionId(pmClosedLoopQuesnaireLine.getId());
		deleteQuesOpt(optObj);

		updateLineQuesnum(pmClosedLoopQuesnaireLine);
		
		return returnResult;
	}
	
	@Override
	public int deleteQuesHeader(PmClosedLoopQuesnaire pmClosedLoopQuesnaire){
		int returnResult=pmClosedLoopQuesnaireDao.deleteQuesHeader(pmClosedLoopQuesnaire);
		if(returnResult<=0){
			throw new RuntimeException("删除问卷出错！");
		}
		pmClosedLoopQuesnaireDao.deleteLineAll(pmClosedLoopQuesnaire.getId());		
		pmClosedLoopQuesnaireDao.deleteOptAll(pmClosedLoopQuesnaire.getId());
		return returnResult;
	}
	@Override
	public int updateEffecticeStart(PmClosedLoopQuesnaire pmClosedLoopQuesnaire){
		if(pmClosedLoopQuesnaire.getQuesType().equals(PmClosedLoopConstant.CL_QUESNAIRE_HEADER_TYPE)){
			//失效其他闭环建议
			PmClosedLoopQuesnaire quesObj=new PmClosedLoopQuesnaire();
			quesObj.setQuesType(pmClosedLoopQuesnaire.getQuesType());
			quesObj.setQuestionnaireStatus(PmClosedLoopConstant.CL_STATUS_ENDEFFEC);
			if(updateQuesStatus(quesObj)==-1){
				return -1;
			}
		}
		
		pmClosedLoopQuesnaire.setQuesType("");
		if(updateQuesStatus(pmClosedLoopQuesnaire)==-1){
			return -1;
		}
		
		return 1;
		
	}
	
	@Override
	public List<PmClosedLoopQuesnaireLine> queryPmClQuesnaireLineList(
			PmClosedLoopQuesnaireLine pmClosedLoopQuesnaireLine, String sqlType) {
		return pmClosedLoopQuesnaireDao.queryPmClQuesnaireLineList(pmClosedLoopQuesnaireLine, sqlType);
	}

	@Override
	public List<PmClosedLoopQuesnaireOpt> queryPmClosedLoopQuesnaireOptList(
			PmClosedLoopQuesnaireOpt pmClosedLoopQuesnaireOpt, String sqlType) {
		return pmClosedLoopQuesnaireDao.queryPmClosedLoopQuesnaireOptList(pmClosedLoopQuesnaireOpt, sqlType);
	}

	@Override
	public Map<Integer, PmClosedLoopQuesnaireOpt> queryPmClosedLoopQuesnaireOptMap(
			PmClosedLoopQuesnaireOpt pmClosedLoopQuesnaireOpt) {
		return pmClosedLoopQuesnaireDao.queryPmClosedLoopQuesnaireOptMap(pmClosedLoopQuesnaireOpt);
	}

	@Override
	public void updateQuesHeader(PmClosedLoopQuesnaire pmClosedLoopQuesnaire) {
		pmClosedLoopQuesnaireDao.updateQuesHeader(pmClosedLoopQuesnaire);		
	}

	@Override
	public int updateQuesStatus(PmClosedLoopQuesnaire pmClosedLoopQuesnaire) {
		return pmClosedLoopQuesnaireDao.updateQuesStatus(pmClosedLoopQuesnaire);
	}

	@Override
	public void deleteQuesOpt(PmClosedLoopQuesnaireOpt pmClosedLoopQuesnaireOpt) {
		pmClosedLoopQuesnaireDao.deleteQuesOpt(pmClosedLoopQuesnaireOpt);
	}

	@Override
	public void updateLineQuesnum(PmClosedLoopQuesnaireLine pmClosedLoopQuesnaireLine) {
		pmClosedLoopQuesnaireDao.updateLineQuesnum(pmClosedLoopQuesnaireLine);
	}

	@Override
    @Transactional
    public int addQuestionnaireResult(PmClQuesnaireResultHeader pmClQuesnaireResultHeader, List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList) {
        // 1.插入问卷头
        pmClQuesnaireResultHeader.setEvaluationHeaderId(0);
        int pmClQuesnaireResultHeaderId = pmClosedLoopQuesnaireDao.addPmClQuesResultHeader(pmClQuesnaireResultHeader);
        // 2.插入问卷结果行信息
        pmClosedLoopQuesnaireDao.addPmClQuesResultLineList(pmClQuesnaireResultLineList, pmClQuesnaireResultHeader);
        
        return pmClQuesnaireResultHeaderId;
    }
}
