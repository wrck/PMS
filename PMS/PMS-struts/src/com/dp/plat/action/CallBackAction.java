package com.dp.plat.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.dp.plat.data.bean.BasicDataBean;
import com.dp.plat.data.bean.CallBack;
import com.dp.plat.data.bean.CallBackComment;
import com.dp.plat.data.bean.CallBackQuesnaire;
import com.dp.plat.data.bean.PmClQuesnaireResultHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultLine;
import com.dp.plat.data.bean.PmClosedLoopQuesnaire;
import com.dp.plat.data.bean.PmClosedLoopQuesnaireLine;
import com.dp.plat.data.bean.PmClosedLoopQuesnaireOpt;
import com.dp.plat.data.bean.Project;
import com.dp.plat.data.bean.ProjectMember;
import com.dp.plat.data.bean.WorkflowCommonParam;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.CallBackService;
import com.dp.plat.service.PmClosedLoopQuesnaireService;
import com.dp.plat.service.PmClosedLoopService;
import com.dp.plat.service.ProjectService;
import com.dp.plat.util.Base64Util;
import com.dp.plat.util.PmClosedLoopConstant;
import com.dp.plat.util.PmClosedLoopMark;
import com.dp.plat.util.PmClosedLoopMarkFactory;

public class CallBackAction extends BaseAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 基础数据管理
	 */
	private BasicDataService basicDataService;
	/**
	 * 项目业务数据管理
	 */
	private ProjectService projectService;
	/**
	 * 问卷管理
	 */
	private PmClosedLoopQuesnaireService pmClosedLoopQuesnaireService;
	/**
	 * 回访业务逻辑管理
	 */
	private CallBackService callBackService;
	/**
	 * 闭环申请
	 */
	private PmClosedLoopService pmClosedLoopService;
	
	/**
	 * 弹出框跳转URL
	 */
	private DisplayParam displayParam;
	private String redirect;
	private Project project;
	private List<ProjectMember> projectMemberList;
	private PmClQuesnaireResultHeader pmClQuesnaireResultHeader;
	private PmClQuesnaireResultLine pmClQuesnaireResultLine;
	private PmClosedLoopQuesnaire pmClosedLoopQuesnaire;
	private List<PmClosedLoopQuesnaire> pmClosedLoopQuesnaireList;
	private List<PmClosedLoopQuesnaireLine>pmClosedLoopQuesnaireLineList;
	private List<PmClosedLoopQuesnaireOpt>pmClosedLoopQuesnaireOptList;
	private List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList;
	private CallBack callBack;
	private CallBackQuesnaire cbQuesnaire;
	private List<BasicDataBean> quesTypeList;
	private List<CallBackComment> commentList;
	
	private WorkflowCommonParam param;
	private int quesnaireId;//问卷ID
	/**
	 * 发起回访申请
	 */
	public String input(){
		//获取项目信息
		project=projectService.queryProjectById(project.getProjectId());
		
		//获取最终客户
		projectMemberList = projectService.queryProjectMembers(project.getProjectId());
		
		return INPUT;
	}
	/**
	 * 发起申请
	 * @return
	 */
	public String apply(){
		try {
			callBackService.startCallBackFlow(callBack);
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		redirect = "module/ProjectModify.action?project.paramId="+Base64Util.EncodeBase64(callBack.getProjectId());
		return SUCCESS;
	}
	/**
	 * 查询闭环表单
	 */
	public String read(){
		//获取项目信息
		project=projectService.queryProjectById(callBack.getProjectId());
		//获取最终客户
		projectMemberList = projectService.queryProjectMembers(callBack.getProjectId());
		//获取回访流程信息
		String taskId = callBack.getTaskId();
		callBack = callBackService.queryCallBackById(callBack.getCallBackId());
		callBack.setTaskId(taskId);
		
		//获取审批意见
		commentList = callBackService.queryCallBackComment(callBack.getCallBackId());
				
		return "read";
	}		
	/**
	 * 查询回访问卷
	 * @return
	 */
	public String seeQuesnaire(){
		
		queryQuesnaire();
		
		return "seeQuesnaire";
	}
	/**
	 * 查询回访问卷
	 */
	private void queryQuesnaire() {
		if(quesnaireId != 0 ){
			//1.查询问卷模板信息,为下面查询模板做准备
			int templateId = callBackService.queryQuesnaireTemplateId(quesnaireId);
			if(pmClosedLoopQuesnaire == null){
				pmClosedLoopQuesnaire = new PmClosedLoopQuesnaire();
				pmClosedLoopQuesnaire.setId(templateId);;
			}
			//2.获取问卷结果行信息
			
			pmClQuesnaireResultLine=new PmClQuesnaireResultLine();
			pmClQuesnaireResultLine.setQuesnaireResultHeaderId(quesnaireId);
			pmClQuesnaireResultLineList = pmClosedLoopService.queryPmClQuesResultLineList(pmClQuesnaireResultLine);
			
			//3.获取问卷结果信息
			quesTypeList = basicDataService.queryBasicDataBeanAll(PmClosedLoopConstant.CL_QUESNAIRE_LINEID); //获取问题类型
			if(cbQuesnaire == null){
				cbQuesnaire = new CallBackQuesnaire();
			}
			cbQuesnaire.setQuesResultMarkList(getQuesTypeScore(pmClQuesnaireResultLineList));
			
			//4.获取总分以及是否通过
			if(pmClQuesnaireResultHeader == null){
				pmClQuesnaireResultHeader = new PmClQuesnaireResultHeader();
			}
			pmClQuesnaireResultHeader.setId(quesnaireId);
			pmClQuesnaireResultHeader=pmClosedLoopService.queryPmClQuesResultHeaderList(pmClQuesnaireResultHeader).get(0);
		}
		
		//1.获取问卷模板头信息
		pmClosedLoopQuesnaire=pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(pmClosedLoopQuesnaire, displayParam).get(0);
		//获取评分规则说明
		PmClosedLoopMarkFactory factory=new PmClosedLoopMarkFactory();
		pmClosedLoopQuesnaire.setMarkList(factory.getMarks(pmClosedLoopQuesnaire.getMarkIndexs()));
		
		//2.获取问卷模板行信息		
		PmClosedLoopQuesnaireLine pmClosedLoopQuesnaireLine=new PmClosedLoopQuesnaireLine();
		pmClosedLoopQuesnaireLine.setQuesnaireTemplateHeaderId(pmClosedLoopQuesnaire.getId());
		pmClosedLoopQuesnaireLineList=pmClosedLoopQuesnaireService.queryPmClQuesnaireLineList(pmClosedLoopQuesnaireLine, "asc");
		
		
		//3.获取问卷模板选项信息
		PmClosedLoopQuesnaireOpt pmClosedLoopQuesnaireOpt=new PmClosedLoopQuesnaireOpt();
		pmClosedLoopQuesnaireOpt.setQuesnaireTemplateHeaderId(pmClosedLoopQuesnaire.getId());
		pmClosedLoopQuesnaireOpt.setQuestionId(0); 
		pmClosedLoopQuesnaireOptList=pmClosedLoopQuesnaireService.queryPmClosedLoopQuesnaireOptList(pmClosedLoopQuesnaireOpt, "asc");	
	}
	
	/**
	 * 驳回后重新提交
	 * @return
	 */
	public String resubmit(){
		if(param != null){
			//重新提交申请
			callBackService.reSubmitCallBackFlow(param , callBack);
			redirect = "module/ProjectModify.action?project.paramId="+Base64Util.EncodeBase64(callBack.getProjectId());;
			return SUCCESS;
		}
		
		//获取项目信息
		project=projectService.queryProjectById(callBack.getProjectId());
		//获取最终客户
		projectMemberList = projectService.queryProjectMembers(callBack.getProjectId());
		//获取回访流程信息
		String taskId = callBack.getTaskId();
		callBack = callBackService.queryCallBackById(callBack.getCallBackId());
		callBack.setTaskId(taskId);
		
		//获取审批意见
		commentList = callBackService.queryCallBackComment(callBack.getCallBackId());
		return "resubmit";
	}
	/**
	 * 进行问卷保存或提交 /提交审批
	 * @return
	 */
	public String aduit(){
		//问卷提交
		if(pmClQuesnaireResultHeader != null && pmClQuesnaireResultHeader.getStatus() != 0){
			//检查是否需要计算问卷分数，并进行计算
			queryQuesnaireScore();
			//每次保存问卷草稿或提交问卷都会重新生成一份数据保存在数据库，
			callBackService.insertCallBackQuesnaire( callBack , pmClQuesnaireResultHeader, pmClQuesnaireResultLineList);
			
			if(pmClQuesnaireResultHeader.getStatus() != 1){//问卷已提交，则页面保留在原页面，进行下一步审批
				redirect = "module/ProjectModify.action?project.paramId="+Base64Util.EncodeBase64(callBack.getProjectId());;
				return SUCCESS;
			}
		}
		//审批
		if(param != null && param.getInstId() != null){
			callBackService.submitCallBackFlow(param, callBack);
			redirect = "module/ProjectModify.action?project.paramId="+Base64Util.EncodeBase64(callBack.getProjectId());;
			return SUCCESS;
		}
		
		//获取项目信息
		project=projectService.queryProjectById(callBack.getProjectId());
		//获取最终客户
		projectMemberList = projectService.queryProjectMembers(callBack.getProjectId());
		//获取回访流程信息
		String taskId = callBack.getTaskId();
		callBack = callBackService.queryCallBackById(callBack.getCallBackId());
		if(callBack.getTaskId() != null && !callBack.getTaskId().equals(taskId)){//判断查询处理的问卷状态是否是当前任务的问卷状态
			callBack.setQuesnaireId(0);
		}
		
		
		callBack.setTaskId(taskId);
		//获取生效的问卷分类
		findPmClosedLoopQuesnaireList();
		//获取问卷模板的内容或者已填写的问卷内容
		if((pmClosedLoopQuesnaire!= null && pmClosedLoopQuesnaire.getId() != 0) || callBack.getQuesnaireId() != 0 ){
			getCbForm();
		}
		
		//获取审批意见
		commentList = callBackService.queryCallBackComment(callBack.getCallBackId());
		
		return "aduit";
	}
	private void getCbForm(){
		if(callBack.getQuesnaireId() != 0 ){
			//1.获取已经是草稿或提交的问卷
			cbQuesnaire = callBackService.queryCbQuesnaire(callBack.getQuesnaireId());
			//2.复制给pmClosedLoopQuesnaire传递需要的问卷模板信息
			int templateId = callBackService.queryQuesnaireTemplateId(cbQuesnaire.getQuesnaireId());
			if(pmClosedLoopQuesnaire == null){
				pmClosedLoopQuesnaire = new PmClosedLoopQuesnaire();
				pmClosedLoopQuesnaire.setId(templateId);;
			}
			//3.判断选择的问卷模板是否等于已有草稿问卷的模板，等于则获取问卷结果行信息
			if(templateId == pmClosedLoopQuesnaire.getId()){
				pmClQuesnaireResultLine=new PmClQuesnaireResultLine();
				pmClQuesnaireResultLine.setQuesnaireResultHeaderId(cbQuesnaire.getQuesnaireId());
				pmClQuesnaireResultLineList = pmClosedLoopService.queryPmClQuesResultLineList(pmClQuesnaireResultLine);
			}
			
			//问卷状态  已提交 1 草稿0
			if(cbQuesnaire.getQuesnaireState() != -1){
				//获取问卷结果信息
				quesTypeList = basicDataService.queryBasicDataBeanAll(PmClosedLoopConstant.CL_QUESNAIRE_LINEID); //获取问题类型
				cbQuesnaire.setQuesResultMarkList(getQuesTypeScore(pmClQuesnaireResultLineList));
				
				//获取总分以及是否通过
				if(pmClQuesnaireResultHeader == null){
					pmClQuesnaireResultHeader = new PmClQuesnaireResultHeader();
				}
				pmClQuesnaireResultHeader.setId(cbQuesnaire.getQuesnaireId());
				pmClQuesnaireResultHeader=pmClosedLoopService.queryPmClQuesResultHeaderList(pmClQuesnaireResultHeader).get(0);
			}
		}
		
		//1.获取问卷模板头信息
		pmClosedLoopQuesnaire=pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(pmClosedLoopQuesnaire, displayParam).get(0);
		//获取评分规则说明
		PmClosedLoopMarkFactory factory=new PmClosedLoopMarkFactory();
		pmClosedLoopQuesnaire.setMarkList(factory.getMarks(pmClosedLoopQuesnaire.getMarkIndexs()));
		
		//2.获取问卷模板行信息		
		PmClosedLoopQuesnaireLine pmClosedLoopQuesnaireLine=new PmClosedLoopQuesnaireLine();
		pmClosedLoopQuesnaireLine.setQuesnaireTemplateHeaderId(pmClosedLoopQuesnaire.getId());
		pmClosedLoopQuesnaireLineList=pmClosedLoopQuesnaireService.queryPmClQuesnaireLineList(pmClosedLoopQuesnaireLine, "asc");
		
		
		//3.获取问卷模板选项信息
		PmClosedLoopQuesnaireOpt pmClosedLoopQuesnaireOpt=new PmClosedLoopQuesnaireOpt();
		pmClosedLoopQuesnaireOpt.setQuesnaireTemplateHeaderId(pmClosedLoopQuesnaire.getId());
		pmClosedLoopQuesnaireOpt.setQuestionId(0); 
		pmClosedLoopQuesnaireOptList=pmClosedLoopQuesnaireService.queryPmClosedLoopQuesnaireOptList(pmClosedLoopQuesnaireOpt, "asc");	
		
	}
	/**
	 * 计算问卷结果
	 * @param quesnaireResultLineListObj
	 * @return
	 */
	private List<String> getQuesTypeScore(List<PmClQuesnaireResultLine> quesnaireResultLineListObj){
		Map<String, Double>quesTypeMarkMap=new HashMap<String, Double>();
		
		if(quesTypeList!=null){
			List<String>quesResultMarkList=new ArrayList<String>();
			for (PmClQuesnaireResultLine pmClQuesnaireResultLineObj : quesnaireResultLineListObj) {
				double scoreObj=pmClQuesnaireResultLineObj.getQuestionScore();
				if(quesTypeMarkMap.get(pmClQuesnaireResultLineObj.getQuesTypeForCB())!=null){
					scoreObj+=quesTypeMarkMap.get(pmClQuesnaireResultLineObj.getQuesTypeForCB());
				}
				quesTypeMarkMap.put(pmClQuesnaireResultLineObj.getQuesTypeForCB(), scoreObj);
			}
			
			for (BasicDataBean basicDataBeanObj : quesTypeList) {
				if(quesTypeMarkMap.get(basicDataBeanObj.getBasicDataId())!=null){
					quesResultMarkList.add(basicDataBeanObj.getBasicDataName()+"|"+basicDataBeanObj.getBasicDataId());
					quesResultMarkList.add(quesTypeMarkMap.get(basicDataBeanObj.getBasicDataId())+"");
				}
			}
			return quesResultMarkList;
		}
		
		return null;
	}
	/**
	 * 检查是否需要计算问卷分数，并进行计算
	 */
	private void queryQuesnaireScore() {
		Map<Integer, PmClosedLoopQuesnaireOpt> optMap = queryQuesnaireOpt();
		queryPmClosedLoopQuesnaire();
		quesMark(pmClosedLoopQuesnaire,optMap,pmClQuesnaireResultLineList,pmClQuesnaireResultHeader);
	}
	
	private void queryPmClosedLoopQuesnaire() {
		pmClosedLoopQuesnaire=new PmClosedLoopQuesnaire();
		pmClosedLoopQuesnaire.setId(pmClQuesnaireResultHeader.getQuesnaireTemplateHeaderId());
		pmClosedLoopQuesnaire=pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(pmClosedLoopQuesnaire, displayParam).get(0);
	}
	private int quesMark(PmClosedLoopQuesnaire quesObj,Map<Integer, PmClosedLoopQuesnaireOpt>optMap,List<PmClQuesnaireResultLine>resultLineListObj,
			PmClQuesnaireResultHeader resultHeaderObj
			){
		double totalScore=0;
		StringBuilder quesAnwBuilder=new StringBuilder();
		String quesTypeForCB=resultLineListObj.get(0).getQuesTypeForCB();
		quesAnwBuilder.append(quesTypeForCB+":");
		StringBuilder evaResultBuilder=new StringBuilder();
		int i=0;
		for (PmClQuesnaireResultLine pmClQuesnaireResultLineObj : resultLineListObj) {
			if(pmClQuesnaireResultLineObj==null){
				return -1;
			}
			//总分计算与答案字符串拼接
			if(pmClQuesnaireResultLineObj.getQuestionTemplateOptId()!=0){
				if(optMap.get(pmClQuesnaireResultLineObj.getQuestionTemplateOptId())==null){
					return -1;
				}
				if(!(quesTypeForCB.equals(pmClQuesnaireResultLineObj.getQuesTypeForCB()))){
					quesAnwBuilder.append(";");
					quesAnwBuilder.append(pmClQuesnaireResultLineObj.getQuesTypeForCB()+":");
				}
				quesTypeForCB=pmClQuesnaireResultLineObj.getQuesTypeForCB();
				
				char opt=(char)((((int)'A')-1)+optMap.get(pmClQuesnaireResultLineObj.getQuestionTemplateOptId()).getQuestionOptionNum());
				quesAnwBuilder.append(i+"-"+pmClQuesnaireResultLineObj.getQuesTemplateLineNum()+"|"+opt+",");	//10:1-2|C (10 题目回访类型，1 下表，  2 题号，  C 选项)
				pmClQuesnaireResultLineObj.setQuestionScore(optMap.get(pmClQuesnaireResultLineObj.getQuestionTemplateOptId()).getQuestionOptionScore());
				totalScore+=pmClQuesnaireResultLineObj.getQuestionScore();
			}
			i++;
		}
		quesAnwBuilder.append(";");
		
		resultHeaderObj.setQuesMarkScore(totalScore);
		resultHeaderObj.setQuesAnw(quesAnwBuilder.toString());
		
		//获取计分规则并计分
		if(quesObj.getMarkIndexs()!=null&&!(quesObj.getMarkIndexs().equals(""))){
			PmClosedLoopMarkFactory factory=new PmClosedLoopMarkFactory();
			if(factory.getMarks(quesObj.getMarkIndexs())!=null){
				for (PmClosedLoopMark pmClosedLoopMarkObj : factory.getMarks(quesObj.getMarkIndexs())) {
					String evaResultObj=pmClosedLoopMarkObj.quesMark(resultHeaderObj);	
					if(evaResultObj.equals("-2")){
						return -1;
					}else if(evaResultObj.equals("pass")){
						evaResultObj="1";
					}else if(!evaResultObj.equals("-1")){
						if(evaResultObj.contains(",")){
							for (String optIndex : evaResultObj.split(",")) {
								resultLineListObj.get(Integer.parseInt(optIndex)).setQuesEvaResult(-1);
							}
						}else{
							resultLineListObj.get(Integer.parseInt(evaResultObj)).setQuesEvaResult(-1);
						}
						evaResultObj="-1";
					}else{
						
					}
					evaResultBuilder.append(evaResultObj);
				}
			}
		}
		if(evaResultBuilder.length()>0&&evaResultBuilder.toString().contains(PmClosedLoopConstant.CL_EVALU_RESULT_REJECT+"")){
			resultHeaderObj.setQuesMarkResult(PmClosedLoopConstant.CL_EVALU_RESULT_REJECT);
			
		}else{
			resultHeaderObj.setQuesMarkResult(PmClosedLoopConstant.CL_EVALU_RESULT_AGREE);
		}
		return 1;
	}
	
	
	
	private Map<Integer, PmClosedLoopQuesnaireOpt> queryQuesnaireOpt() {
		PmClosedLoopQuesnaireOpt pmClosedLoopQuesnaireOpt=new PmClosedLoopQuesnaireOpt();
		pmClosedLoopQuesnaireOpt.setQuesnaireTemplateHeaderId(pmClQuesnaireResultHeader.getQuesnaireTemplateHeaderId());
		pmClosedLoopQuesnaireOpt.setQuestionId(0); 
		Map<Integer, PmClosedLoopQuesnaireOpt> optMap=pmClosedLoopQuesnaireService.queryPmClosedLoopQuesnaireOptMap(pmClosedLoopQuesnaireOpt);
		return optMap;
	}
	//只获取生效的问卷
	private void findPmClosedLoopQuesnaireList(){
		PmClosedLoopQuesnaire quesObj=new PmClosedLoopQuesnaire();
		quesObj.setQuestionnaireStatus(PmClosedLoopConstant.CL_STATUS_SUBMIT);
		
		pmClosedLoopQuesnaireList = pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(quesObj, displayParam);
	}
	
	public void setBasicDataService(BasicDataService basicDataService) {
		this.basicDataService = basicDataService;
	}
	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public List<ProjectMember> getProjectMemberList() {
		return projectMemberList;
	}

	public void setProjectMemberList(List<ProjectMember> projectMemberList) {
		this.projectMemberList = projectMemberList;
	}

	public List<PmClosedLoopQuesnaire> getPmClosedLoopQuesnaireList() {
		return pmClosedLoopQuesnaireList;
	}

	public void setPmClosedLoopQuesnaireList(
			List<PmClosedLoopQuesnaire> pmClosedLoopQuesnaireList) {
		this.pmClosedLoopQuesnaireList = pmClosedLoopQuesnaireList;
	}

	public DisplayParam getDisplayParam() {
		return displayParam;
	}

	public void setDisplayParam(DisplayParam displayParam) {
		this.displayParam = displayParam;
	}

	public void setPmClosedLoopQuesnaireService(
			PmClosedLoopQuesnaireService pmClosedLoopQuesnaireService) {
		this.pmClosedLoopQuesnaireService = pmClosedLoopQuesnaireService;
	}
	public void setPmClosedLoopService(PmClosedLoopService pmClosedLoopService) {
		this.pmClosedLoopService = pmClosedLoopService;
	}
	public CallBack getCallBack() {
		return callBack;
	}

	public void setCallBack(CallBack callBack) {
		this.callBack = callBack;
	}
	public String getRedirect() {
		return redirect;
	}
	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}
	public void setCallBackService(CallBackService callBackService) {
		this.callBackService = callBackService;
	}
	public PmClQuesnaireResultHeader getPmClQuesnaireResultHeader() {
		return pmClQuesnaireResultHeader;
	}
	public void setPmClQuesnaireResultHeader(
			PmClQuesnaireResultHeader pmClQuesnaireResultHeader) {
		this.pmClQuesnaireResultHeader = pmClQuesnaireResultHeader;
	}
	public PmClQuesnaireResultLine getPmClQuesnaireResultLine() {
		return pmClQuesnaireResultLine;
	}
	public void setPmClQuesnaireResultLine(
			PmClQuesnaireResultLine pmClQuesnaireResultLine) {
		this.pmClQuesnaireResultLine = pmClQuesnaireResultLine;
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
	public PmClosedLoopQuesnaireService getPmClosedLoopQuesnaireService() {
		return pmClosedLoopQuesnaireService;
	}
	public List<PmClQuesnaireResultLine> getPmClQuesnaireResultLineList() {
		return pmClQuesnaireResultLineList;
	}
	public void setPmClQuesnaireResultLineList(
			List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList) {
		this.pmClQuesnaireResultLineList = pmClQuesnaireResultLineList;
	}
	public CallBackQuesnaire getCbQuesnaire() {
		return cbQuesnaire;
	}
	public void setCbQuesnaire(CallBackQuesnaire cbQuesnaire) {
		this.cbQuesnaire = cbQuesnaire;
	}
	public List<BasicDataBean> getQuesTypeList() {
		return quesTypeList;
	}
	public void setQuesTypeList(List<BasicDataBean> quesTypeList) {
		this.quesTypeList = quesTypeList;
	}
	public WorkflowCommonParam getParam() {
		return param;
	}
	public void setParam(WorkflowCommonParam param) {
		this.param = param;
	}
	public List<CallBackComment> getCommentList() {
		return commentList;
	}
	public void setCommentList(List<CallBackComment> commentList) {
		this.commentList = commentList;
	}
	public int getQuesnaireId() {
		return quesnaireId;
	}
	public void setQuesnaireId(int quesnaireId) {
		this.quesnaireId = quesnaireId;
	}
	
}
