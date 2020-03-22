package com.dp.plat.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.dp.plat.data.bean.BasicDataBean;
import com.dp.plat.data.bean.PmClosedLoopQuesnaire;
import com.dp.plat.data.bean.PmClosedLoopQuesnaireLine;
import com.dp.plat.data.bean.PmClosedLoopQuesnaireOpt;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.PmClosedLoopQuesnaireService;
import com.dp.plat.util.PmClosedLoopConstant;
import com.dp.plat.util.PmClosedLoopMarkFactory;

public class PmClosedLoopQuesnaireAction extends BaseAction{
	private static final long serialVersionUID = 1L;
	private PmClosedLoopQuesnaireService pmClosedLoopQuesnaireService;
	private DisplayParam displayParam;
	private PmClosedLoopQuesnaire pmClosedLoopQuesnaire;
	private List<PmClosedLoopQuesnaire>pmClosedLoopQuesnaireList=new ArrayList<PmClosedLoopQuesnaire>();
	private List<PmClosedLoopQuesnaireLine>pmClosedLoopQuesnaireLineList=new ArrayList<PmClosedLoopQuesnaireLine>();
	private List<PmClosedLoopQuesnaireOpt>pmClosedLoopQuesnaireOptList=new ArrayList<PmClosedLoopQuesnaireOpt>();
	private PmClosedLoopQuesnaireLine pmClosedLoopQuesnaireLine;
	private int pmClosedLoopQuesnaireId;
	private List<BasicDataBean>quesTypeList=new ArrayList<BasicDataBean>();
	private List<BasicDataBean>quesLineTypeList=new ArrayList<BasicDataBean>();
	private BasicDataService basicDataService;
	private List<String>markList;	//评分规则
	private String redirect;
	private int doType;
	private double returnTotalLineScore=0;
	@Override
	public String execute() throws Exception {
		pmClosedLoopQuesnaire=(pmClosedLoopQuesnaire==null)?new PmClosedLoopQuesnaire():pmClosedLoopQuesnaire;
		displayParam.getParam();
		pmClosedLoopQuesnaireList=pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(pmClosedLoopQuesnaire, displayParam);
		displayParam.setTotalcount(pmClosedLoopQuesnaireList.size());
		displayParam.setPagesize(50);
		return INPUT;
	}

	public String addPCLQuesnaire() throws Exception{
		quesTypeList = basicDataService.queryBasicDataBeanAll(PmClosedLoopConstant.CL_QUESNAIRE_HEADERID);
		PmClosedLoopMarkFactory pmClosedLoopMarkFactory=new PmClosedLoopMarkFactory();
		markList=pmClosedLoopMarkFactory.getAllMarkExplain();
		return INPUT;
	}
	
	public String pmCLQuesEdit(){
		try {
			if(pmClosedLoopQuesnaire==null||pmClosedLoopQuesnaire.getId()==0){
				return ERROR;
			}
			PmClosedLoopMarkFactory pmClosedLoopMarkFactory=new PmClosedLoopMarkFactory();
			markList=pmClosedLoopMarkFactory.getAllMarkExplain();
			quesTypeList = basicDataService.queryBasicDataBeanAll(PmClosedLoopConstant.CL_QUESNAIRE_HEADERID);
			quesLineTypeList = basicDataService.queryBasicDataBeanAll(PmClosedLoopConstant.CL_QUESNAIRE_LINEID);
			pmClosedLoopQuesnaire=pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(pmClosedLoopQuesnaire, displayParam).get(0);
			
			pmClosedLoopQuesnaireLine=new PmClosedLoopQuesnaireLine();
			pmClosedLoopQuesnaireLine.setQuesnaireTemplateHeaderId(pmClosedLoopQuesnaire.getId());
			pmClosedLoopQuesnaireLineList=pmClosedLoopQuesnaireService.queryPmClQuesnaireLineList(pmClosedLoopQuesnaireLine, "asc");
			
			PmClosedLoopQuesnaireOpt pmClosedLoopQuesnaireOpt=new PmClosedLoopQuesnaireOpt();
			pmClosedLoopQuesnaireOpt.setQuesnaireTemplateHeaderId(pmClosedLoopQuesnaire.getId());
			pmClosedLoopQuesnaireOptList=pmClosedLoopQuesnaireService.queryPmClosedLoopQuesnaireOptList(pmClosedLoopQuesnaireOpt, "asc");

			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return INPUT;
	}
	
	public String submitQues()throws Exception{
		if(pmClosedLoopQuesnaire==null||
				pmClosedLoopQuesnaire.getQuestionnaireTemplateName()==null||pmClosedLoopQuesnaire.getQuestionnaireTemplateName().equals("")||
				/*pmClosedLoopQuesnaire.getQuestionnaireScore()==0||*/
				/*pmClosedLoopQuesnaire.getQuestionnairePassScore()==0||*/
				pmClosedLoopQuesnaire.getQuesType().equals("")/*||
				pmClosedLoopQuesnaire.getQuestionnaireScore()<=pmClosedLoopQuesnaire.getQuestionnairePassScore()*/){
				return ERROR;
		}
		pmClosedLoopQuesnaire.setQuestionnaireStatus(PmClosedLoopConstant.CL_STATUS_DRAFT);
		int returnId=pmClosedLoopQuesnaireService.insertQuesnaireHeader(pmClosedLoopQuesnaire);
		if(returnId<=0){
			setErrmsg("插入失败");
			return ERROR;
		}
		pmClosedLoopQuesnaire.setId(returnId);
		return "addQues";
	}
	
	public String addLine()throws Exception{
		if(doType==2){ //编辑
			editLine();
		}else{	//新增
			if(pmClosedLoopQuesnaire==null||pmClosedLoopQuesnaire.getId()==0){
				return ERROR;
			}
			quesLineTypeList = basicDataService.queryBasicDataBeanAll(PmClosedLoopConstant.CL_QUESNAIRE_LINEID);
			pmClosedLoopQuesnaireList=pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(pmClosedLoopQuesnaire, displayParam);
			if(pmClosedLoopQuesnaireList==null||pmClosedLoopQuesnaireList.size()<=0){
				return ERROR;
			}
			pmClosedLoopQuesnaire=pmClosedLoopQuesnaireList.get(0);
			
			//获取该问卷下已保存的问题列表
			pmClosedLoopQuesnaireLineList.clear();
			pmClosedLoopQuesnaireLine=new PmClosedLoopQuesnaireLine();
			pmClosedLoopQuesnaireLine.setQuesnaireTemplateHeaderId(pmClosedLoopQuesnaire.getId());
			pmClosedLoopQuesnaireLineList=pmClosedLoopQuesnaireService.queryPmClQuesnaireLineList(pmClosedLoopQuesnaireLine, null);
			
			if(checkScore(pmClosedLoopQuesnaire, pmClosedLoopQuesnaireLineList)){
				setErrmsg("问卷已添加问题分数不能大于问卷总分");
				return ERROR;
			}
			if (pmClosedLoopQuesnaireLineList!=null&&pmClosedLoopQuesnaireLineList.size()>0) {
				if(pmClosedLoopQuesnaireLineList.get(0).getQuestionNum()==0){
					return ERROR;
				}else{
					pmClosedLoopQuesnaireLine.setQuestionNum(pmClosedLoopQuesnaireLineList.get(0).getQuestionNum()+1);
				}
			}else{
				pmClosedLoopQuesnaireLine.setQuestionNum(1);
			}
		}
	
		return INPUT;
	}
	
	public String submitLine() throws Exception {
		try {
			if(pmClosedLoopQuesnaireLine==null||pmClosedLoopQuesnaireLine.getQuesnaireTemplateHeaderId()==0||pmClosedLoopQuesnaireLine.getQuestionNum()==0||
					pmClosedLoopQuesnaireLine.getQuestionContent()==null||pmClosedLoopQuesnaireLine.getQuestionContent().equals("")){
				return ERROR;
			}
			if(pmClosedLoopQuesnaireLine.getQuestionType()!=PmClosedLoopConstant.CL_QUESNAIRE_LINE_TYPE_AQ){
				if(pmClosedLoopQuesnaireOptList==null||pmClosedLoopQuesnaireOptList.size()<=0){
					return ERROR;
				}
				for (PmClosedLoopQuesnaireOpt optObj : pmClosedLoopQuesnaireOptList) {
					if(optObj==null||optObj.getQuestionOptionsContent()==null||optObj.getQuestionOptionsContent().equals("")||
							optObj.getQuestionOptionScore()>pmClosedLoopQuesnaireLine.getQuestionScore()){
						return ERROR;
					}
				}
			}
			
			if(checkScore(pmClosedLoopQuesnaireLine.getQuesnaireTemplateHeaderId(),pmClosedLoopQuesnaireLine.getQuestionScore(),pmClosedLoopQuesnaireLine.getId())){
				setErrmsg("问卷已添加问题分数不能大于问卷总分");
				return ERROR;
			}
			
			
			if(doType==2){	//编辑
				if(pmClosedLoopQuesnaireLine.getId()==0){
					setErrmsg("获取题目ID失败");	
					return ERROR;
				}
				
				pmClosedLoopQuesnaireService.updateQuesLineOpt(pmClosedLoopQuesnaireLine, pmClosedLoopQuesnaireOptList);
			}else{	//新增
				pmClosedLoopQuesnaireService.insertQuesnaireLineOptList(pmClosedLoopQuesnaireLine, pmClosedLoopQuesnaireOptList);
		
			}
		} catch (Exception e) {
			e.printStackTrace();
			//setErrmsg("题目更新错误");
			setErrmsg("题目更新错误\r\n" + ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		
		redirect="base/EditPmClosedLoopQuesnaire.action?pmClosedLoopQuesnaire.id="+pmClosedLoopQuesnaireLine.getQuesnaireTemplateHeaderId();
		return SUCCESS;
		
	}

	public String updateQues()throws Exception{
		if(pmClosedLoopQuesnaire==null||pmClosedLoopQuesnaire.getId()==0||
				pmClosedLoopQuesnaire.getQuestionnaireTemplateName()==null||pmClosedLoopQuesnaire.getQuestionnaireTemplateName().equals("")||
				pmClosedLoopQuesnaire.getQuestionnaireScore()==0||
				/*pmClosedLoopQuesnaire.getQuestionnairePassScore()==0||*/
				pmClosedLoopQuesnaire.getQuesType().equals("")||
				pmClosedLoopQuesnaire.getQuestionnaireScore()<pmClosedLoopQuesnaire.getQuestionnairePassScore()){
			setErrmsg("问卷信息有误");	
			return ERROR;
		}
		if(pmClosedLoopQuesnaire.getMarkIndexs()!=null&&(pmClosedLoopQuesnaire.getMarkIndexs().equals(""))){
			pmClosedLoopQuesnaire.getMarkIndexs().trim();
		}
		pmClosedLoopQuesnaire.setQuestionnaireStatus(PmClosedLoopConstant.CL_STATUS_DRAFT);
		
		pmClosedLoopQuesnaireService.updateQuesHeader(pmClosedLoopQuesnaire);
		
		return INPUT;
	}
	
	public String deleteHeader(){
		if(pmClosedLoopQuesnaire==null||pmClosedLoopQuesnaire.getId()==0){
			return ERROR;
		}
		pmClosedLoopQuesnaireService.deleteQuesHeader(pmClosedLoopQuesnaire);
		return SUCCESS;
	}
	public String startEffective()throws Exception{
		if(pmClosedLoopQuesnaire==null||pmClosedLoopQuesnaire.getId()==0||
				pmClosedLoopQuesnaire.getQuesType()==null||pmClosedLoopQuesnaire.getQuesType().equals("")){
			return ERROR;
		}
		pmClosedLoopQuesnaire.setQuestionnaireStatus(PmClosedLoopConstant.CL_STATUS_SUBMIT);
		pmClosedLoopQuesnaire.setEffectiveStartTime(new Date());
		if(pmClosedLoopQuesnaireService.updateEffecticeStart(pmClosedLoopQuesnaire)==-1){
			setErrmsg("问卷生效错误");
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	public String pmCLQuesSee(){
		try {
			if(pmClosedLoopQuesnaire==null||pmClosedLoopQuesnaire.getId()==0){
				return ERROR;
			}
			pmClosedLoopQuesnaire=pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(pmClosedLoopQuesnaire, displayParam).get(0);
			
			PmClosedLoopMarkFactory pmClosedLoopMarkFactory=new PmClosedLoopMarkFactory();
			markList=pmClosedLoopMarkFactory.getMarksExplain(pmClosedLoopQuesnaire.getMarkIndexs());
			
			quesLineTypeList = basicDataService.queryBasicDataBeanAll(PmClosedLoopConstant.CL_QUESNAIRE_LINEID);
			
			pmClosedLoopQuesnaireLine=new PmClosedLoopQuesnaireLine();
			pmClosedLoopQuesnaireLine.setQuesnaireTemplateHeaderId(pmClosedLoopQuesnaire.getId());
			pmClosedLoopQuesnaireLineList=pmClosedLoopQuesnaireService.queryPmClQuesnaireLineList(pmClosedLoopQuesnaireLine, "asc");
			
			PmClosedLoopQuesnaireOpt pmClosedLoopQuesnaireOpt=new PmClosedLoopQuesnaireOpt();
			pmClosedLoopQuesnaireOpt.setQuesnaireTemplateHeaderId(pmClosedLoopQuesnaire.getId());
			pmClosedLoopQuesnaireOptList=pmClosedLoopQuesnaireService.queryPmClosedLoopQuesnaireOptList(pmClosedLoopQuesnaireOpt, "asc");

			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return INPUT;
	}
	
	public String deleteLine(){
		try {
			if(pmClosedLoopQuesnaireLine==null||pmClosedLoopQuesnaireLine.getId()==0){
				return ERROR;
			}
			pmClosedLoopQuesnaireLine=pmClosedLoopQuesnaireService.queryPmClQuesnaireLineList(pmClosedLoopQuesnaireLine, "").get(0);
			if(pmClosedLoopQuesnaireLine.getQuesnaireTemplateHeaderId()<=0||pmClosedLoopQuesnaireLine.getQuestionNum()<=0){
				return ERROR;
			}
			int result=pmClosedLoopQuesnaireService.deleteQuesLine(pmClosedLoopQuesnaireLine);
			if(result<=0){
				setErrmsg("删除失败");
				return ERROR;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		pmClosedLoopQuesnaire=new PmClosedLoopQuesnaire();
		pmClosedLoopQuesnaire.setId(pmClosedLoopQuesnaireLine.getQuesnaireTemplateHeaderId());
		return INPUT;
	}
	
	public String editLine()throws Exception{
		if(pmClosedLoopQuesnaireLine==null||pmClosedLoopQuesnaireLine.getId()==0){
			return ERROR;
		}
		pmClosedLoopQuesnaireLine=pmClosedLoopQuesnaireService.queryPmClQuesnaireLineList(pmClosedLoopQuesnaireLine, "").get(0);
		if(pmClosedLoopQuesnaireLine.getQuesnaireTemplateHeaderId()<=0||pmClosedLoopQuesnaireLine.getQuestionNum()<=0){
			return ERROR;
		}
		
		quesLineTypeList = basicDataService.queryBasicDataBeanAll(PmClosedLoopConstant.CL_QUESNAIRE_LINEID);
		
		pmClosedLoopQuesnaire=new PmClosedLoopQuesnaire();
		pmClosedLoopQuesnaire.setId(pmClosedLoopQuesnaireLine.getQuesnaireTemplateHeaderId());
		pmClosedLoopQuesnaire=pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(pmClosedLoopQuesnaire, displayParam).get(0);
		

		if(checkScore(pmClosedLoopQuesnaire)){
			setErrmsg("问卷已添加问题分数不能大于问卷总分");
			return ERROR;
		}
		
		if(pmClosedLoopQuesnaireLine.getQuestionType()!=PmClosedLoopConstant.CL_QUESNAIRE_LINE_TYPE_AQ){
			PmClosedLoopQuesnaireOpt optObj = new PmClosedLoopQuesnaireOpt();
			optObj.setQuestionId(pmClosedLoopQuesnaireLine.getId());
			pmClosedLoopQuesnaireOptList=pmClosedLoopQuesnaireService.queryPmClosedLoopQuesnaireOptList(optObj, "asc");
		}
		return INPUT;
	}
	
	public String endEffective() throws Exception{
		if(pmClosedLoopQuesnaire==null||pmClosedLoopQuesnaire.getId()==0){
			return ERROR;
		}
		pmClosedLoopQuesnaire.setQuestionnaireStatus(PmClosedLoopConstant.CL_STATUS_ENDEFFEC);
		pmClosedLoopQuesnaire.setEffectiveEndTime(new Date());
		if(pmClosedLoopQuesnaireService.updateQuesStatus(pmClosedLoopQuesnaire)==-1){
			setErrmsg("问卷失效错误");
			return ERROR;	
		}
		return SUCCESS;
	}

	private boolean checkScore(int quesId,double newScore,int oldLineId){
		PmClosedLoopQuesnaire headerObj=new PmClosedLoopQuesnaire();
		headerObj.setId(quesId);
		headerObj=pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(headerObj, displayParam).get(0);
	
		PmClosedLoopQuesnaireLine lineObj=new PmClosedLoopQuesnaireLine();
		lineObj.setQuesnaireTemplateHeaderId(headerObj.getId());
		List<PmClosedLoopQuesnaireLine>lineList=pmClosedLoopQuesnaireService.queryPmClQuesnaireLineList(lineObj, null);
		
		double totalLineScore=newScore;
		for (PmClosedLoopQuesnaireLine lineObj2 : lineList) {
			if(lineObj2.getId()!=oldLineId){
				totalLineScore+=lineObj2.getQuestionScore();
			}
		}
		returnTotalLineScore=totalLineScore;
		return totalLineScore>headerObj.getQuestionnaireScore();			
		
	}
	
	private boolean checkScore(PmClosedLoopQuesnaire headerObj){
		PmClosedLoopQuesnaireLine lineObj=new PmClosedLoopQuesnaireLine();
		lineObj.setQuesnaireTemplateHeaderId(headerObj.getId());
		List<PmClosedLoopQuesnaireLine>lineList=pmClosedLoopQuesnaireService.queryPmClQuesnaireLineList(lineObj, null);
		
		double totalLineScore=0;
		for (PmClosedLoopQuesnaireLine lineObj2 : lineList) {
			totalLineScore+=lineObj2.getQuestionScore();
		}
		returnTotalLineScore=totalLineScore;
		return totalLineScore>headerObj.getQuestionnaireScore();			
		
	}
	
	
	private boolean checkScore(PmClosedLoopQuesnaire headerObj,List<PmClosedLoopQuesnaireLine>lineList){
		double totalLineScore=0;
		for (PmClosedLoopQuesnaireLine lineObj2 : lineList) {
			totalLineScore+=lineObj2.getQuestionScore();
		}
		returnTotalLineScore=totalLineScore;
		return totalLineScore>headerObj.getQuestionnaireScore();			
		
	}
	public PmClosedLoopQuesnaireService getPmClosedLoopQuesnaireService() {
		return pmClosedLoopQuesnaireService;
	}

	public void setPmClosedLoopQuesnaireService(
			PmClosedLoopQuesnaireService pmClosedLoopQuesnaireService) {
		this.pmClosedLoopQuesnaireService = pmClosedLoopQuesnaireService;
	}

	public DisplayParam getDisplayParam() {
		return displayParam;
	}

	public void setDisplayParam(DisplayParam displayParam) {
		this.displayParam = displayParam;
	}

	public PmClosedLoopQuesnaire getPmClosedLoopQuesnaire() {
		return pmClosedLoopQuesnaire;
	}

	public void setPmClosedLoopQuesnaire(PmClosedLoopQuesnaire pmClosedLoopQuesnaire) {
		this.pmClosedLoopQuesnaire = pmClosedLoopQuesnaire;
	}

	public List<PmClosedLoopQuesnaireLine> getPmClosedLoopQuesnaireLineList() {
		return pmClosedLoopQuesnaireLineList;
	}

	public void setPmClosedLoopQuesnaireLineList(
			List<PmClosedLoopQuesnaireLine> pmClosedLoopQuesnaireLineList) {
		this.pmClosedLoopQuesnaireLineList = pmClosedLoopQuesnaireLineList;
	}

	public List<PmClosedLoopQuesnaireOpt> getPmClosedLoopQuesnaireOptList() {
		return pmClosedLoopQuesnaireOptList;
	}

	public void setPmClosedLoopQuesnaireOptList(
			List<PmClosedLoopQuesnaireOpt> pmClosedLoopQuesnaireOptList) {
		this.pmClosedLoopQuesnaireOptList = pmClosedLoopQuesnaireOptList;
	}

	public List<PmClosedLoopQuesnaire> getPmClosedLoopQuesnaireList() {
		return pmClosedLoopQuesnaireList;
	}

	public void setPmClosedLoopQuesnaireList(
			List<PmClosedLoopQuesnaire> pmClosedLoopQuesnaireList) {
		this.pmClosedLoopQuesnaireList = pmClosedLoopQuesnaireList;
	}

	public PmClosedLoopQuesnaireLine getPmClosedLoopQuesnaireLine() {
		return pmClosedLoopQuesnaireLine;
	}

	public void setPmClosedLoopQuesnaireLine(
			PmClosedLoopQuesnaireLine pmClosedLoopQuesnaireLine) {
		this.pmClosedLoopQuesnaireLine = pmClosedLoopQuesnaireLine;
	}

	public int getPmClosedLoopQuesnaireId() {
		return pmClosedLoopQuesnaireId;
	}

	public void setPmClosedLoopQuesnaireId(int pmClosedLoopQuesnaireId) {
		this.pmClosedLoopQuesnaireId = pmClosedLoopQuesnaireId;
	}

	public List<BasicDataBean> getQuesTypeList() {
		return quesTypeList;
	}

	public int getDoType() {
		return doType;
	}

	public void setDoType(int doType) {
		this.doType = doType;
	}

	public void setQuesTypeList(List<BasicDataBean> quesTypeList) {
		this.quesTypeList = quesTypeList;
	}

	public BasicDataService getBasicDataService() {
		return basicDataService;
	}

	public void setBasicDataService(BasicDataService basicDataService) {
		this.basicDataService = basicDataService;
	}

	public List<String> getMarkList() {
		return markList;
	}

	public void setMarkList(List<String> markList) {
		this.markList = markList;
	}

	public String getRedirect() {
		return redirect;
	}

	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}

	public List<BasicDataBean> getQuesLineTypeList() {
		return quesLineTypeList;
	}

	public void setQuesLineTypeList(List<BasicDataBean> quesLineTypeList) {
		this.quesLineTypeList = quesLineTypeList;
	}

	public double getReturnTotalLineScore() {
		return returnTotalLineScore;
	}

	public void setReturnTotalLineScore(double returnTotalLineScore) {
		this.returnTotalLineScore = returnTotalLineScore;
	}

	
	
}
