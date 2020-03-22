package com.dp.plat.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.json.annotations.JSON;

import com.dp.plat.context.UserContext;
import com.dp.plat.data.bean.BasicDataBean;
import com.dp.plat.data.bean.PmClEvaluationHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultLine;
import com.dp.plat.data.bean.PmClosedLoopQuesnaire;
import com.dp.plat.data.bean.PmClosedLoopQuesnaireLine;
import com.dp.plat.data.bean.PmClosedLoopQuesnaireOpt;
import com.dp.plat.data.bean.Project;
import com.dp.plat.data.bean.ProjectMember;
import com.dp.plat.data.bean.User;
import com.dp.plat.data.bean.WorkflowCommonParam;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.PmClosedLoopQuesnaireService;
import com.dp.plat.service.PmClosedLoopService;
import com.dp.plat.service.ProjectService;
import com.dp.plat.service.UserManageService;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.PmClosedLoopConstant;
import com.dp.plat.util.PmClosedLoopMark;
import com.dp.plat.util.PmClosedLoopMarkFactory;

public class PmClosedLoopAction extends BaseAction{
	
	private static final long serialVersionUID = 1L;
	private PmClosedLoopService pmClosedLoopService;
	private DisplayParam displayParam;
	private List<Project> projectlist=new ArrayList<Project>();
	private ProjectService projectService;
	private Project project;
	private int pmClosedLoopResultType;	//30:回访人员选择问卷，40：回访人员提交选择问卷结果，41：回访人员保存问卷草稿，42：回访人员提交问卷，50：工程人员填写问题，51：工程人员提交问题
	private WorkflowCommonParam workflowCommonParam;
	private PmClEvaluationHeader pmClEvaluationHeader;
	private PmClQuesnaireResultHeader pmClQuesnaireResultHeader;
	private PmClQuesnaireResultLine pmClQuesnaireResultLine;
	private PmClosedLoopQuesnaireService pmClosedLoopQuesnaireService;
	private PmClosedLoopQuesnaire pmClosedLoopQuesnaire;
	private List<PmClosedLoopQuesnaire> pmClosedLoopQuesnaireList;
	private List<PmClosedLoopQuesnaireLine>pmClosedLoopQuesnaireLineList;
	private List<PmClosedLoopQuesnaireOpt>pmClosedLoopQuesnaireOptList;
	private int projectProcessStatu;
	private List<PmClEvaluationHeader> pmClEvaluationHeaderList=new ArrayList<PmClEvaluationHeader>();
	private List<PmClQuesnaireResultLine>pmClQuesnaireResultLineList;
	private String redirect;
	private String viewCurrTaskId;
	private UserManageService userManageService;
	private BasicDataService basicDataService;
	private String projectTypeName;
	private List<ProjectMember> projectMemberList;
	private List<BasicDataBean>quesTypeList;
	private PmClEvaluationHeader pmClApplyHeader;
	private List<PmClEvaluationHeader>pmClEvaResultList;
	private List<PmClEvaluationHeader> pmClEvaluationHeaderListHis=new ArrayList<PmClEvaluationHeader>();
	private String closeApplyUser;
	@Override
	public String execute() throws Exception {
		if(project==null||project.getProjectId()==0){
			return ERROR;
		}
		
		//获取项目信息
		project=projectService.queryProjectById(project.getProjectId());
		if(project==null){
			return ERROR;
		}
		
		if(!getUserPower(project, "propm,prosm,cl,cb,manager",0)){
			setErrmsg("您没有访问权限");
			return ERROR;
		}
		
		//获取最终客户
		projectMemberList = projectService.queryProjectMembers(project.getProjectId());
		
		//获取申请历史集合
		pmClEvaluationHeader=new PmClEvaluationHeader();
		pmClEvaluationHeader.setEvaluationType(0);
		pmClEvaluationHeader.setProjectCode(project.getProjectCode());
		pmClEvaluationHeaderList=pmClosedLoopService.queryPmEvaluationHeaderList(pmClEvaluationHeader);
		
		//获取任务Id
		getTaskId();
		
		//获取流程状态与流程图信息
		if(pmClosedLoopResultType==0){
			getProcessStatus();
		}
		
		if(pmClosedLoopResultType!=0){
			//获取历史流程信息			
			quesTypeList = basicDataService.queryBasicDataBeanAll(PmClosedLoopConstant.CL_QUESNAIRE_LINEID); //获取问题类型
			getHisProcess();
		}
		
		//根据不同状态获取当前表单
		if(pmClosedLoopResultType==30){
			if(getCbForm()==-1){
				setErrmsg("获取回访问卷出错");
				return ERROR;
			}
		}
		if(pmClosedLoopResultType==40){
			if(getClForm()==-1){
				setErrmsg("获取闭环建议问卷出错");
				return ERROR;
			}
		}
		closeApplyUser = UserContext.getUserContext().getUser().getUsername() +"-" +UserContext.getUserContext().getUser().getRealName();
		if(pmClosedLoopResultType!=0){
			return INPUT;
		}
		
		return SUCCESS;
	}
	
	private void getTaskId(){
		//获取  当前用户的私有任务Id
		projectlist.clear();
		projectlist.add(project);
		pmClosedLoopService.getProjectSefTaskId(projectlist);
		
		//获取  当前用户的公有任务Id
		if(project.getTaskId()==null){
			pmClosedLoopService.getProjectPubTaskId(projectlist);
		}
	}
	
	private void getProcessStatus(){
		//获取流程任务Id,查看当前流程图
		viewCurrTaskId=pmClosedLoopService.queryTaskByBussinessKey(project);
		
		//获取项目闭环状态
		projectProcessStatu=-1;
		Map<String, Object>varMap=pmClosedLoopService.queryProcessVarMap(project);
		if(varMap!=null&&varMap.get(PmClosedLoopConstant.CL_PROJECT_PROCESS_STATUS)!=null){
			projectProcessStatu=(Integer)varMap.get(PmClosedLoopConstant.CL_PROJECT_PROCESS_STATUS);
		}	
		
		if(projectProcessStatu<2){
			//获得流程图部署ID，可查看流程图最新版本
			workflowCommonParam=new WorkflowCommonParam();
			pmClosedLoopService.querymaxDefinitionObjByKey(PmClosedLoopConstant.CL_PROCESS_KEY, workflowCommonParam);
		}
		
	}
	
	public void getHisProcess() {	
		
		pmClEvaResultList=new ArrayList<PmClEvaluationHeader>();
		
		pmClQuesnaireResultHeader=null;
		boolean flag0=true;
		boolean flag1=true;
		boolean flag2=true;
		for (PmClEvaluationHeader pmObj : pmClEvaluationHeaderList) {
			if(flag0&&pmObj.getEvaluationType()==PmClosedLoopConstant.CL_EVALU_TYPE_CL&&pmObj.getStatus()==PmClosedLoopConstant.CL_STATUS_SUBMIT){	//存在闭环建议
				pmClEvaResultList.add(pmObj);
				flag0=false;				
			}
			if(flag1&&pmObj.getEvaluationType()==PmClosedLoopConstant.CL_EVALU_TYPE_CB&&pmObj.getStatus()==PmClosedLoopConstant.CL_STATUS_SUBMIT){	//存在回访
				pmClEvaResultList.add(pmObj);
				flag1=false;
			}
			
			if(flag2&&pmObj.getEvaluationType()==PmClosedLoopConstant.CL_EVALU_TYPE_PM&&pmObj.getStatus()==PmClosedLoopConstant.CL_STATUS_SUBMIT){//申请头信息
				pmClApplyHeader=pmObj;
				flag2=false;
			}
			if(pmObj.getStatus()==PmClosedLoopConstant.CL_STATUS_SUBMIT){
				pmClEvaluationHeaderListHis.add(pmObj);
			}
		}
		if(pmClEvaluationHeaderListHis.size()>0){
			Collections.reverse(pmClEvaluationHeaderListHis);
		}
		
		//获取问卷结果信息
		pmClQuesnaireResultHeader=new PmClQuesnaireResultHeader();
		for (PmClEvaluationHeader pmResultObj : pmClEvaResultList) {
			pmClQuesnaireResultHeader.setEvaluationHeaderId(pmResultObj.getId());
			
			//获取问卷结果头信息
			List<PmClQuesnaireResultHeader>resultListObj=pmClosedLoopService.queryPmClQuesResultHeaderList(pmClQuesnaireResultHeader);
			pmResultObj.setResultHeaderList(resultListObj);
			if(resultListObj!=null){
				for (PmClQuesnaireResultHeader pmHeaderObj : resultListObj) {
					pmClQuesnaireResultLine=new PmClQuesnaireResultLine();
					pmClQuesnaireResultLine.setQuesnaireResultHeaderId(pmHeaderObj.getId());
					//获取问卷结果行信息
					List<PmClQuesnaireResultLine>resultLineListObj=pmClosedLoopService.queryPmClQuesResultLineList(pmClQuesnaireResultLine);
					pmHeaderObj.setResultLineList(resultLineListObj);
					
					//获取问题类型的得分
					pmHeaderObj.setQuesResultMarkList(getQuesTypeScore(resultLineListObj));
					
					//获取问卷模板信息
					PmClosedLoopQuesnaire quesnaireObj=new PmClosedLoopQuesnaire();
					quesnaireObj.setId(pmHeaderObj.getQuesnaireTemplateHeaderId());
					quesnaireObj=pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(quesnaireObj, displayParam).get(0);
					
					PmClosedLoopQuesnaireLine quesLineObj=new PmClosedLoopQuesnaireLine();
					quesLineObj.setQuesnaireTemplateHeaderId(pmHeaderObj.getQuesnaireTemplateHeaderId());
					quesnaireObj.setPmCLQuesLineList(pmClosedLoopQuesnaireService.queryPmClQuesnaireLineList(quesLineObj, "asc"));
					
					PmClosedLoopQuesnaireOpt quesnaireOptObj=new PmClosedLoopQuesnaireOpt();
					quesnaireOptObj.setQuesnaireTemplateHeaderId(pmHeaderObj.getQuesnaireTemplateHeaderId());
					quesnaireObj.setPmCLQuesOptList(pmClosedLoopQuesnaireService.queryPmClosedLoopQuesnaireOptList(quesnaireOptObj, "asc"));
					
					//获取评分规则说明
					PmClosedLoopMarkFactory factory=new PmClosedLoopMarkFactory();
					quesnaireObj.setMarkList(factory.getMarks(quesnaireObj.getMarkIndexs()));

					pmHeaderObj.setQuesnaireTemp(quesnaireObj);
					
					
					
				}
			}
			
			
		}
		
		if(pmClEvaluationHeaderList!=null&&pmClEvaluationHeaderList.size()>0){
			Collections.reverse(pmClEvaluationHeaderList);
		}else{
			pmClEvaluationHeaderList=new ArrayList<PmClEvaluationHeader>();
		}
		
	}
	
	private int getCbForm(){	
		pmClEvaluationHeader=new PmClEvaluationHeader();
		if(pmClEvaluationHeaderList!=null&&pmClEvaluationHeaderList.size()>0){	
			pmClQuesnaireResultHeader=new PmClQuesnaireResultHeader();
			if(pmClEvaluationHeaderList.get(pmClEvaluationHeaderList.size()-1).getEvaluationType()==PmClosedLoopConstant.CL_EVALU_TYPE_CB){
				pmClEvaluationHeader=pmClEvaluationHeaderList.get(pmClEvaluationHeaderList.size()-1);
				pmClQuesnaireResultHeader.setEvaluationHeaderId(pmClEvaluationHeader.getId());
				
				//1.获取问卷结果头信息
				pmClQuesnaireResultHeader=pmClosedLoopService.queryPmClQuesResultHeaderList(pmClQuesnaireResultHeader).get(0);
				
				if(pmClQuesnaireResultHeader==null||pmClQuesnaireResultHeader.getId()==0){
					return -1;
				}
				
				if(pmClQuesnaireResultHeader.getStatus()!=PmClosedLoopConstant.CL_STATUS_SUBMIT){
					PmClosedLoopQuesnaire quesObj=new PmClosedLoopQuesnaire();
					quesObj.setQuestionnaireStatus(PmClosedLoopConstant.CL_STATUS_SUBMIT);  //只获取生效的问卷
					pmClosedLoopQuesnaireList=pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(quesObj, displayParam);
				}
				if(!(pmClosedLoopQuesnaire!=null&&pmClosedLoopQuesnaire.getId()!=pmClQuesnaireResultHeader.getQuesnaireTemplateHeaderId())){
					pmClosedLoopQuesnaire=new PmClosedLoopQuesnaire();
					pmClosedLoopQuesnaire.setId(pmClQuesnaireResultHeader.getQuesnaireTemplateHeaderId());
					//2.获取问卷结果行信息
					pmClQuesnaireResultLine=new PmClQuesnaireResultLine();
					pmClQuesnaireResultLine.setQuesnaireResultHeaderId(pmClQuesnaireResultHeader.getId());
					pmClQuesnaireResultLineList=pmClosedLoopService.queryPmClQuesResultLineList(pmClQuesnaireResultLine);
					
					if(pmClQuesnaireResultHeader.getStatus()==PmClosedLoopConstant.CL_STATUS_SUBMIT){
						//获取各类型评分结果
						pmClQuesnaireResultHeader.setQuesResultMarkList(getQuesTypeScore(pmClQuesnaireResultLineList));
					}
				}
				
			}else{
				PmClosedLoopQuesnaire quesObj=new PmClosedLoopQuesnaire();
				quesObj.setQuestionnaireStatus(PmClosedLoopConstant.CL_STATUS_SUBMIT);  //只获取生效的问卷
				pmClosedLoopQuesnaireList=pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(quesObj, displayParam);
			}
		}
		
		
		if(pmClosedLoopQuesnaire==null){
			return 1;
		}else{
			if(pmClosedLoopQuesnaire.getId()==0){
				return -1;
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
		
		return 1;
	}
	
	private int getClForm(){		
		if(pmClEvaluationHeaderList!=null&&pmClEvaluationHeaderList.size()>0){
			pmClEvaluationHeader=new PmClEvaluationHeader();
			pmClQuesnaireResultHeader=new PmClQuesnaireResultHeader();
			if(pmClEvaluationHeaderList.get(pmClEvaluationHeaderList.size()-1).getEvaluationType()==PmClosedLoopConstant.CL_EVALU_TYPE_CL){
				pmClEvaluationHeader=pmClEvaluationHeaderList.get(pmClEvaluationHeaderList.size()-1);	
				pmClQuesnaireResultHeader.setEvaluationHeaderId(pmClEvaluationHeader.getId());
				
				//1.获取问卷结果头信息
				pmClQuesnaireResultHeader=pmClosedLoopService.queryPmClQuesResultHeaderList(pmClQuesnaireResultHeader).get(0);
				
				if(pmClQuesnaireResultHeader==null||pmClQuesnaireResultHeader.getId()==0){
					return -1;
				}
				
				//2.获取问卷结果行信息
				pmClQuesnaireResultLine=new PmClQuesnaireResultLine();
				pmClQuesnaireResultLine.setQuesnaireResultHeaderId(pmClQuesnaireResultHeader.getId());
				pmClQuesnaireResultLineList=pmClosedLoopService.queryPmClQuesResultLineList(pmClQuesnaireResultLine);
				
				if(pmClQuesnaireResultHeader.getStatus()==PmClosedLoopConstant.CL_STATUS_SUBMIT){
					//获取各类型评分结果
					pmClQuesnaireResultHeader.setQuesResultMarkList(getQuesTypeScore(pmClQuesnaireResultLineList));
				}
				
				pmClosedLoopQuesnaire=new PmClosedLoopQuesnaire();
				pmClosedLoopQuesnaire.setId(pmClQuesnaireResultHeader.getQuesnaireTemplateHeaderId());

				if(pmClEvaluationHeader.getStatus()!=PmClosedLoopConstant.CL_STATUS_SUBMIT){
					pmClosedLoopQuesnaireList=pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(new PmClosedLoopQuesnaire(), displayParam);
				}
				
			}else{
				pmClosedLoopQuesnaireList=pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(new PmClosedLoopQuesnaire(), displayParam);
			}
		}
		
		
		if(pmClosedLoopQuesnaire==null){
			pmClosedLoopQuesnaire=new PmClosedLoopQuesnaire();
			pmClosedLoopQuesnaire.setQuestionnaireStatus(PmClosedLoopConstant.CL_STATUS_SUBMIT);
			pmClosedLoopQuesnaire.setQuesType(PmClosedLoopConstant.CL_QUESNAIRE_HEADER_TYPE); //闭环建议类型
		}
		
		//1.获取问卷模板头信息
		List<PmClosedLoopQuesnaire>quesList=pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(pmClosedLoopQuesnaire, displayParam);
		if(quesList.size()>1){
			return -1;
		}
		pmClosedLoopQuesnaire=quesList.get(0);
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
		
		return 1;
	}
	
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
			//	System.out.println(pmClQuesnaireResultLineObj.getQuestionScore());
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
	
	public boolean getUserPower(Project projectObj,String checkStr,int doStr){
		String nowUserObj=UserContext.getUserContext().getUser().getUsername();
		Map<String, Boolean>powerMap=new HashMap<String, Boolean>();
		powerMap.put("pm", UserContext.getUserContext().isHasRole(MessageUtil.ROLE_PROGRAMMANAGER));
		powerMap.put("propm",nowUserObj.equals(projectObj.getProgramManagerCode()) || nowUserObj.equals(projectObj.getProgramManagerCodeB()));
		powerMap.put("sm", UserContext.getUserContext().isHasRole(MessageUtil.ROLE_SERVICEMANAGER));
		powerMap.put("prosm",nowUserObj.equals(projectObj.getServiceManagerCode()));
		powerMap.put("cb", UserContext.getUserContext().isHasRole(MessageUtil.ROLE_CALLBACKPER));
		powerMap.put("cl", UserContext.getUserContext().isHasRole(MessageUtil.ROLE_ENGINEEMANAGER));
		powerMap.put("manager", UserContext.getUserContext().isHasRole(MessageUtil.ROLE_ADMIN));
		if(checkStr.contains(",")){
			String strArr[]=checkStr.split(",");
			boolean resultBoolean=powerMap.get(strArr[0]);
			for (int i = 1; i < strArr.length; i++) {
				if(doStr==0){
					resultBoolean=resultBoolean||powerMap.get(strArr[i]);
				}else if(doStr==1){
					resultBoolean=resultBoolean&&powerMap.get(strArr[i]);
				}
			}
			return resultBoolean;
		}else{
			return powerMap.get(checkStr);
		}
	}
	/**
	 * 项目经理提交闭环申请
	 * @return
	 * @throws Exception
	 */
	public String addPmCLApply()throws Exception {		
			if(project==null||project.getProjectId()==0){
				return ERROR;
			}
			
			project=projectService.queryProjectById(project.getProjectId());
			if(project==null){
				return ERROR;
			}
			
			if(!getUserPower(project, "pm,propm",1)){
				setErrmsg("您没有访问权限");
				return ERROR;
			}
			
			//确认服务经理人员是否有效
			User userObj=userManageService.queryUserByUserName(project.getServiceManagerCode());
			if(userObj==null||userObj.getId()==0){
				setErrmsg("项目服务经理已失效");
				return ERROR;
			}

			pmClEvaluationHeader.setProjectCode(project.getProjectCode());
			pmClEvaluationHeader.setProjectId(project.getProjectId());
			pmClEvaluationHeader.setProjectName(project.getProjectName());
			
//			pmClEvaluationHeaderList=pmClosedLoopService.queryPmEvaluationHeaderList(pmClEvaluationHeader);
//			if(pmClEvaluationHeaderList!=null&&pmClEvaluationHeaderList.size()>0&&pmClEvaluationHeaderList.get(0).getEvaluationResult()==PmClosedLoopConstant.CL_EVALU_RESULT_REJECT){
//				getTaskId();
//				if(project.getTaskId()==null||project.getTaskId().equals("")){
//					return ERROR;
//				}
//			}
			
			
			workflowCommonParam=new WorkflowCommonParam();
			String resultObj=this.pmClosedLoopService.addPmCLApply(workflowCommonParam,pmClEvaluationHeader, project);
			
			if(resultObj==null||resultObj.equals("")){
				return ERROR;
			}
			projectService.updateProjectLastRefreshTime(project.getProjectId());
			redirect="module/ProjectModify.action?project.projectId="+project.getProjectId();
			return SUCCESS;
	}
	/**
	 * 服务经理审核
	 * @return
	 */
	public String addSmCLApply(){
		if(project==null||project.getProjectId()==0||
				workflowCommonParam==null||
				workflowCommonParam.getTaskId()==null||workflowCommonParam.getTaskId().equals("")){
			return ERROR;
		}
		//待开发。验证提交人权限与任务Id
		project=projectService.queryProjectById(project.getProjectId());
		if(project==null){
			return ERROR;
		}
		if(!getUserPower(project, "sm,prosm",1)){
			setErrmsg("您没有访问权限");
			return ERROR;
		}

		if(pmClEvaluationHeader.getEvaluationResult()==PmClosedLoopConstant.CL_EVALU_RESULT_REJECT){
			//确认项目经理人员是否有效
			User userObj=userManageService.queryUserByUserName(project.getProgramManagerCode());
			if(StringUtils.isNotBlank(project.getProgramManagerCodeB())){
				User userObjB=userManageService.queryUserByUserName(project.getProgramManagerCodeB());
				if((userObj==null||userObj.getId()==0)&&(userObjB==null||userObjB.getId()==0)){
					setErrmsg("项目经理已失效");
					return ERROR;
				}
			}else{
				if(userObj==null||userObj.getId()==0){
					setErrmsg("项目经理已失效");
					return ERROR;
				}
			}
		}
		
		pmClEvaluationHeader.setProjectCode(project.getProjectCode());
		pmClEvaluationHeader.setProjectId(project.getProjectId());
		pmClEvaluationHeader.setProjectName(project.getProjectName());
		pmClEvaluationHeader.setApplyHeaderId(getApplyHeaderId(project));
		String result=this.pmClosedLoopService.addSmCLApply(workflowCommonParam, pmClEvaluationHeader, project);
		if(result==null||result.equals("")){
			return ERROR;
		}
		redirect="module/ProjectModify.action?project.projectId="+project.getProjectId();
		return SUCCESS;
	}
	
	public String addCbCLApply(){
		if(project==null||project.getProjectId()==0){
			return ERROR;
		}
		project=projectService.queryProjectById(project.getProjectId());
		if(project==null){
			return ERROR;
		}	
		if(!getUserPower(project, "cb",1)){
			setErrmsg("您没有访问权限");
			return ERROR;
		}
		if(pmClosedLoopResultType==1){	//提交回访问卷返回回访分数界面
			if(workflowCommonParam==null||workflowCommonParam.getTaskId()==null||workflowCommonParam.getTaskId().equals("")||				
				pmClQuesnaireResultHeader==null||pmClQuesnaireResultHeader.getQuesnaireTemplateHeaderId()==0||
				pmClQuesnaireResultLineList==null||pmClQuesnaireResultLineList.size()<=0||
				(pmClQuesnaireResultHeader.getStatus()==PmClosedLoopConstant.CL_STATUS_DRAFT&&(pmClEvaluationHeader==null||pmClEvaluationHeader.getId()==0))){
				return ERROR;
			}
		
			if(pmClQuesnaireResultHeader.getStatus()==0){
				pmClQuesnaireResultHeader.setStatus(PmClosedLoopConstant.CL_STATUS_DRAFT);
			}
			if(pmClEvaluationHeader==null||pmClEvaluationHeader.getId()==0){	//第一次插入头信息
				pmClEvaluationHeader=new PmClEvaluationHeader();
			}

			pmClEvaluationHeader.setProjectCode(project.getProjectCode());
			pmClEvaluationHeader.setProjectId(project.getProjectId());
			pmClEvaluationHeader.setProjectName(project.getProjectName());
			
			//问卷结果行信息验证与补充
			PmClosedLoopQuesnaireOpt pmClosedLoopQuesnaireOpt=new PmClosedLoopQuesnaireOpt();
			pmClosedLoopQuesnaireOpt.setQuesnaireTemplateHeaderId(pmClQuesnaireResultHeader.getQuesnaireTemplateHeaderId());
			pmClosedLoopQuesnaireOpt.setQuestionId(0); 
			Map<Integer, PmClosedLoopQuesnaireOpt>optMap=pmClosedLoopQuesnaireService.queryPmClosedLoopQuesnaireOptMap(pmClosedLoopQuesnaireOpt);
			
			pmClosedLoopQuesnaire=new PmClosedLoopQuesnaire();
			pmClosedLoopQuesnaire.setId(pmClQuesnaireResultHeader.getQuesnaireTemplateHeaderId());
			pmClosedLoopQuesnaire=pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(pmClosedLoopQuesnaire, displayParam).get(0);
			
			pmClQuesnaireResultHeader.setQuesTotalScore(pmClosedLoopQuesnaire.getQuestionnaireScore());
			pmClQuesnaireResultHeader.setQuesPassScore(pmClosedLoopQuesnaire.getQuestionnairePassScore());
		
			int result=1;
			if(pmClQuesnaireResultHeader.getStatus()==PmClosedLoopConstant.CL_STATUS_SUBMIT){
				result=quesMark(pmClosedLoopQuesnaire,optMap,pmClQuesnaireResultLineList,pmClEvaluationHeader,pmClQuesnaireResultHeader);
			}
			if(result<=-1){ //插入错误
				return ERROR;
			}

			pmClEvaluationHeader.setEvaluationType(PmClosedLoopConstant.CL_EVALU_TYPE_CB);
			pmClEvaluationHeader.setNextAcceptPersonName("回访人员");
			pmClEvaluationHeader.setProjectCode(project.getProjectCode());
			pmClEvaluationHeader.setApplyHeaderId(getApplyHeaderId(project));
			
			
			result=this.pmClosedLoopService.addCbCLApplyQues(workflowCommonParam, pmClEvaluationHeader, project, pmClQuesnaireResultHeader, pmClQuesnaireResultLineList);
			if(result<=-1){ //插入错误
				return ERROR;
			}
			
			if(pmClQuesnaireResultHeader.getStatus()==PmClosedLoopConstant.CL_STATUS_DRAFT){
				redirect="module/ProjectModify.action?project.projectId="+project.getProjectId();
				pmClosedLoopResultType=0;
				return SUCCESS;
			}else{
				pmClosedLoopResultType=30;
				return "seeScore";
			}
		}else if(pmClosedLoopResultType==2){
			if(pmClEvaluationHeader==null||
					workflowCommonParam==null||
					workflowCommonParam.getTaskId()==null||workflowCommonParam.getTaskId().equals("")||
					pmClQuesnaireResultHeader==null||pmClQuesnaireResultHeader.getId()==0){
				return ERROR;
			}
			//设置问卷头结果
			pmClQuesnaireResultHeader=pmClosedLoopService.queryPmClQuesResultHeaderList(pmClQuesnaireResultHeader).get(0);
			if(pmClQuesnaireResultHeader==null){
				return ERROR;
			}
			if(pmClQuesnaireResultHeader.getQuesMarkResult()==-1){
				pmClEvaluationHeader.setEvaluationResult(-1);
			}
			if(pmClEvaluationHeader.getEvaluationResult()==PmClosedLoopConstant.CL_EVALU_RESULT_REJECT){
				//确认项目经理人员是否有效
				User userObj=userManageService.queryUserByUserName(project.getProgramManagerCode());
				if(StringUtils.isNotBlank(project.getProgramManagerCodeB())){
					User userObjB=userManageService.queryUserByUserName(project.getProgramManagerCodeB());
					if((userObj==null||userObj.getId()==0)&&(userObjB==null||userObjB.getId()==0)){
						setErrmsg("项目经理已失效");
						return ERROR;
					}
				}else{
					if(userObj==null||userObj.getId()==0){
						setErrmsg("项目经理已失效");
						return ERROR;
					}
				}
			}
			pmClEvaluationHeader.setProjectCode(project.getProjectCode());
			pmClEvaluationHeader.setId(pmClQuesnaireResultHeader.getEvaluationHeaderId());
			
			this.pmClosedLoopService.addCbCLApply(workflowCommonParam, pmClEvaluationHeader, project);			
			redirect="module/ProjectModify.action?project.projectId="+project.getProjectId();
			return SUCCESS;
		}
		return INPUT;
	}

	public String cantCB(){
		if(project==null||project.getProjectId()==0||
				workflowCommonParam==null||
				workflowCommonParam.getTaskId()==null||workflowCommonParam.getTaskId().equals("")||
				pmClEvaluationHeader==null||
				pmClEvaluationHeader.getEvaluationComment()==null||pmClEvaluationHeader.getEvaluationComment().equals("")){
			return ERROR;
		}
		project=projectService.queryProjectById(project.getProjectId());
		if(project==null){
			return ERROR;
		}	
		if(!getUserPower(project, "cb",1)){
			setErrmsg("您没有访问权限");
			return ERROR;
		}
		//确认服务经理人员是否有效
		User userObj=userManageService.queryUserByUserName(project.getServiceManagerCode());
		if(userObj==null||userObj.getId()==0){
			setErrmsg("服务经理已失效");
			return ERROR;
		}
		pmClEvaluationHeader.setProjectCode(project.getProjectCode());
		
		List<PmClEvaluationHeader>pmEvaList=pmClosedLoopService.queryPmEvaluationHeaderList(pmClEvaluationHeader);
		if(pmEvaList!=null&&pmEvaList.get(0).getEvaluationType()==PmClosedLoopConstant.CL_EVALU_TYPE_CB&&pmEvaList.get(0).getStatus()==PmClosedLoopConstant.CL_STATUS_DRAFT)
			pmClosedLoopService.deletePmClEvaRecur(pmEvaList.get(0));
		
		pmClEvaluationHeader.setProjectId(project.getProjectId());
		pmClEvaluationHeader.setProjectName(project.getProjectName());
		pmClEvaluationHeader.setApplyHeaderId(getApplyHeaderId(project));
		pmClEvaluationHeader.setEvaluationResult(PmClosedLoopConstant.CL_EVALU_RESULT_CANTCB);
		
		
		
		
		pmClosedLoopService.addCbCLApply(workflowCommonParam, pmClEvaluationHeader, project);
		redirect="module/ProjectModify.action?project.projectId="+project.getProjectId();
		return SUCCESS;
	}
	
	public String addClCLApply() throws Exception{
		if(project==null||project.getProjectId()==0){
			return ERROR;
		}
		project=projectService.queryProjectById(project.getProjectId());
		if(project==null){
			return ERROR;
		}
		if(!getUserPower(project, "cl",1)){
			setErrmsg("您没有访问权限");
			return ERROR;
		}
		if(pmClosedLoopResultType==1){
			if(workflowCommonParam==null||workflowCommonParam.getTaskId()==null||workflowCommonParam.getTaskId().equals("")||
					pmClQuesnaireResultLineList==null||pmClQuesnaireResultLineList.size()<=0||
					pmClQuesnaireResultHeader==null||pmClQuesnaireResultHeader.getQuesnaireTemplateHeaderId()==0)
			{
				return ERROR;
			}
			
			pmClEvaluationHeader=new PmClEvaluationHeader();
			pmClEvaluationHeader.setProjectCode(project.getProjectCode());
			pmClEvaluationHeader.setProjectId(project.getProjectId());
			pmClEvaluationHeader.setProjectName(project.getProjectName());
			
			//问卷结果行信息验证与补充
			PmClosedLoopQuesnaireOpt pmClosedLoopQuesnaireOpt=new PmClosedLoopQuesnaireOpt();
			pmClosedLoopQuesnaireOpt.setQuesnaireTemplateHeaderId(pmClQuesnaireResultHeader.getQuesnaireTemplateHeaderId());
			pmClosedLoopQuesnaireOpt.setQuestionId(0); 
			Map<Integer, PmClosedLoopQuesnaireOpt>optMap=pmClosedLoopQuesnaireService.queryPmClosedLoopQuesnaireOptMap(pmClosedLoopQuesnaireOpt);
			
			pmClosedLoopQuesnaire=new PmClosedLoopQuesnaire();
			pmClosedLoopQuesnaire.setId(pmClQuesnaireResultHeader.getQuesnaireTemplateHeaderId());
			pmClosedLoopQuesnaire=pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(pmClosedLoopQuesnaire, displayParam).get(0);
			
			pmClQuesnaireResultHeader.setQuesTotalScore(pmClosedLoopQuesnaire.getQuestionnaireScore());
			pmClQuesnaireResultHeader.setQuesPassScore(pmClosedLoopQuesnaire.getQuestionnairePassScore());			
		
						
			pmClEvaluationHeader.setEvaluationType(PmClosedLoopConstant.CL_EVALU_TYPE_CL);
			pmClEvaluationHeader.setNextAcceptPersonName("工程人员");
			pmClEvaluationHeader.setApplyHeaderId(getApplyHeaderId(project));
			
			pmClQuesnaireResultHeader.setStatus(PmClosedLoopConstant.CL_STATUS_SUBMIT);
			
			
			int result=quesMark(pmClosedLoopQuesnaire,optMap,pmClQuesnaireResultLineList,pmClEvaluationHeader,pmClQuesnaireResultHeader);	//设置问卷总分以及问卷评分结果
			if(result<=-1){ //插入错误
				return ERROR;
			}
			
			result=this.pmClosedLoopService.addCbCLApplyQues(workflowCommonParam, pmClEvaluationHeader, project, pmClQuesnaireResultHeader, pmClQuesnaireResultLineList);
			if(result<=-1){ //插入错误
				return ERROR;
			}
			
			pmClosedLoopResultType=40;
			return "seeScore";
		}else{
			if(pmClEvaluationHeader==null||
					workflowCommonParam==null||
					workflowCommonParam.getTaskId()==null||workflowCommonParam.getTaskId().equals("")||
					pmClQuesnaireResultHeader==null||pmClQuesnaireResultHeader.getId()==0){
				return ERROR;
			}
			//设置问卷头结果
			pmClQuesnaireResultHeader=pmClosedLoopService.queryPmClQuesResultHeaderList(pmClQuesnaireResultHeader).get(0);
			if(pmClQuesnaireResultHeader==null){
				return ERROR;
			}
			if(pmClQuesnaireResultHeader.getQuesMarkResult()==-1){
				pmClEvaluationHeader.setEvaluationResult(-1);
			}
			
			if(pmClEvaluationHeader.getEvaluationResult()==PmClosedLoopConstant.CL_EVALU_RESULT_REJECT){
				//确认项目经理人员是否有效
				User userObj=userManageService.queryUserByUserName(project.getProgramManagerCode());
				if(StringUtils.isNotBlank(project.getProgramManagerCodeB())){
					User userObjB=userManageService.queryUserByUserName(project.getProgramManagerCodeB());
					if((userObj==null||userObj.getId()==0)&&(userObjB==null||userObjB.getId()==0)){
						setErrmsg("项目经理已失效");
						return ERROR;
					}
				}else{
					if(userObj==null||userObj.getId()==0){
						setErrmsg("项目经理已失效");
						return ERROR;
					}
				}
			}
			pmClEvaluationHeader.setProjectCode(project.getProjectCode());
			pmClEvaluationHeader.setId(pmClQuesnaireResultHeader.getEvaluationHeaderId());
			
			this.pmClosedLoopService.addClCLApply(workflowCommonParam, pmClEvaluationHeader, project);
			redirect="module/ProjectModify.action?project.projectId="+project.getProjectId();
			return SUCCESS;
		}
	}
	
	public String pmSeeCbCl(){
		if(pmClEvaluationHeader==null||pmClEvaluationHeader.getId()==0){
			return ERROR;
		}
		quesTypeList = basicDataService.queryBasicDataBeanAll(PmClosedLoopConstant.CL_QUESNAIRE_LINEID); //获取问题类型
		
		pmClEvaluationHeader=pmClosedLoopService.queryPmEvaluationHeaderList(pmClEvaluationHeader).get(0);	
		
		
		//1.获取问卷结果头信息
		pmClQuesnaireResultHeader=new PmClQuesnaireResultHeader();
		pmClQuesnaireResultHeader.setEvaluationHeaderId(pmClEvaluationHeader.getId());
		pmClQuesnaireResultHeader=pmClosedLoopService.queryPmClQuesResultHeaderList(pmClQuesnaireResultHeader).get(0);
		
		if(pmClQuesnaireResultHeader==null||pmClQuesnaireResultHeader.getId()==0){
			return ERROR;
		}
		
		//2.获取问卷结果行信息
		pmClQuesnaireResultLine=new PmClQuesnaireResultLine();
		pmClQuesnaireResultLine.setQuesnaireResultHeaderId(pmClQuesnaireResultHeader.getId());
		pmClQuesnaireResultLineList=pmClosedLoopService.queryPmClQuesResultLineList(pmClQuesnaireResultLine);
		
		if(pmClQuesnaireResultHeader.getStatus()==PmClosedLoopConstant.CL_STATUS_SUBMIT){
			//获取各类型评分结果
			pmClQuesnaireResultHeader.setQuesResultMarkList(getQuesTypeScore(pmClQuesnaireResultLineList));
		}
		
	
		
		//1.获取问卷模板头信息
		pmClosedLoopQuesnaire=new PmClosedLoopQuesnaire();
		pmClosedLoopQuesnaire.setId(pmClQuesnaireResultHeader.getQuesnaireTemplateHeaderId());
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
		
		return "pmSeeCbCl";
	}
	
	private int getApplyHeaderId(Project project){
		PmClEvaluationHeader pmClEvaluationHeaderObj=new PmClEvaluationHeader();
		pmClEvaluationHeaderObj.setProjectCode(project.getProjectCode());
		pmClEvaluationHeaderObj.setEvaluationType(PmClosedLoopConstant.CL_EVALU_TYPE_PM);
		List<PmClEvaluationHeader>listObj=pmClosedLoopService.queryPmEvaluationHeaderList(pmClEvaluationHeaderObj);
		return listObj.get(0).getId();
	}
	
	
	private int quesMark(PmClosedLoopQuesnaire quesObj,Map<Integer, PmClosedLoopQuesnaireOpt>optMap,List<PmClQuesnaireResultLine>resultLineListObj,
			PmClEvaluationHeader evaHeaderObj,PmClQuesnaireResultHeader resultHeaderObj
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
		evaHeaderObj.setEvaluationScore(totalScore);
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
			evaHeaderObj.setEvaluationResult(PmClosedLoopConstant.CL_EVALU_RESULT_REJECT);
		}else{
			resultHeaderObj.setQuesMarkResult(PmClosedLoopConstant.CL_EVALU_RESULT_AGREE);
			evaHeaderObj.setEvaluationResult(PmClosedLoopConstant.CL_EVALU_RESULT_AGREE);
		}
		
		return 1;
	}
	
	@JSON(serialize=false)
	public PmClosedLoopService getPmClosedLoopService() {
		return pmClosedLoopService;
	}
	public void setPmClosedLoopService(
			PmClosedLoopService pmClosedLoopService) {
		this.pmClosedLoopService = pmClosedLoopService;
	}
	@JSON(serialize=false)
	public DisplayParam getDisplayParam() {
		return displayParam;
	}
	public void setDisplayParam(DisplayParam displayParam) {
		this.displayParam = displayParam;
	}
	@JSON(serialize=false)
	public PmClEvaluationHeader getPmClEvaluationHeader() {
		return pmClEvaluationHeader;
	}

	public void setPmClEvaluationHeader(PmClEvaluationHeader pmClEvaluationHeader) {
		this.pmClEvaluationHeader = pmClEvaluationHeader;
	}
	
	public WorkflowCommonParam getWorkflowCommonParam() {
		return workflowCommonParam;
	}

	public void setWorkflowCommonParam(WorkflowCommonParam workflowCommonParam) {
		this.workflowCommonParam = workflowCommonParam;
	}

	public int getPmClosedLoopResultType() {
		return pmClosedLoopResultType;
	}

	public void setPmClosedLoopResultType(int pmClosedLoopResultType) {
		this.pmClosedLoopResultType = pmClosedLoopResultType;
	}
	@JSON(serialize=false)
	public ProjectService getProjectService() {
		return projectService;
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
	@JSON(serialize=false)
	public List<Project> getProjectlist() {
		return projectlist;
	}

	public void setProjectlist(List<Project> projectlist) {
		this.projectlist = projectlist;
	}
	@JSON(serialize=false)
	public PmClQuesnaireResultHeader getPmClQuesnaireResultHeader() {
		return pmClQuesnaireResultHeader;
	}

	public void setPmClQuesnaireResultHeader(
			PmClQuesnaireResultHeader pmClQuesnaireResultHeader) {
		this.pmClQuesnaireResultHeader = pmClQuesnaireResultHeader;
	}
	@JSON(serialize=false)
	public PmClQuesnaireResultLine getPmClQuesnaireResultLine() {
		return pmClQuesnaireResultLine;
	}

	public void setPmClQuesnaireResultLine(
			PmClQuesnaireResultLine pmClQuesnaireResultLine) {
		this.pmClQuesnaireResultLine = pmClQuesnaireResultLine;
	}
	@JSON(serialize=false)
	public PmClosedLoopQuesnaireService getPmClosedLoopQuesnaireService() {
		return pmClosedLoopQuesnaireService;
	}

	public void setPmClosedLoopQuesnaireService(
			PmClosedLoopQuesnaireService pmClosedLoopQuesnaireService) {
		this.pmClosedLoopQuesnaireService = pmClosedLoopQuesnaireService;
	}
	
	public PmClosedLoopQuesnaire getPmClosedLoopQuesnaire() {
		return pmClosedLoopQuesnaire;
	}

	public void setPmClosedLoopQuesnaire(PmClosedLoopQuesnaire pmClosedLoopQuesnaire) {
		this.pmClosedLoopQuesnaire = pmClosedLoopQuesnaire;
	}

	public int getProjectProcessStatu() {
		return projectProcessStatu;
	}

	public void setProjectProcessStatu(int projectProcessStatu) {
		this.projectProcessStatu = projectProcessStatu;
	}

	public List<PmClEvaluationHeader> getPmClEvaluationHeaderList() {
		return pmClEvaluationHeaderList;
	}

	public void setPmClEvaluationHeaderList(
			List<PmClEvaluationHeader> pmClEvaluationHeaderList) {
		this.pmClEvaluationHeaderList = pmClEvaluationHeaderList;
	}
	

	public String getRedirect() {
		return redirect;
	}

	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}

	public String getViewCurrTaskId() {
		return viewCurrTaskId;
	}

	public void setViewCurrTaskId(String viewCurrTaskId) {
		this.viewCurrTaskId = viewCurrTaskId;
	}
	@JSON(serialize=false)
	public UserManageService getUserManageService() {
		return userManageService;
	}

	public void setUserManageService(UserManageService userManageService) {
		this.userManageService = userManageService;
	}
	@JSON(serialize=false)
	public BasicDataService getBasicDataService() {
		return basicDataService;
	}

	public void setBasicDataService(BasicDataService basicDataService) {
		this.basicDataService = basicDataService;
	}

	public String getProjectTypeName() {
		return projectTypeName;
	}

	public void setProjectTypeName(String projectTypeName) {
		this.projectTypeName = projectTypeName;
	}
	
	@JSON(serialize=false)
	public List<PmClosedLoopQuesnaire> getPmClosedLoopQuesnaireList() {
		return pmClosedLoopQuesnaireList;
	}

	public void setPmClosedLoopQuesnaireList(
			List<PmClosedLoopQuesnaire> pmClosedLoopQuesnaireList) {
		this.pmClosedLoopQuesnaireList = pmClosedLoopQuesnaireList;
	}

	public List<PmClQuesnaireResultLine> getPmClQuesnaireResultLineList() {
		return pmClQuesnaireResultLineList;
	}

	public void setPmClQuesnaireResultLineList(
			List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList) {
		this.pmClQuesnaireResultLineList = pmClQuesnaireResultLineList;
	}

	public List<ProjectMember> getProjectMemberList() {
		return projectMemberList;
	}

	public void setProjectMemberList(List<ProjectMember> projectMemberList) {
		this.projectMemberList = projectMemberList;
	}

	public List<BasicDataBean> getQuesTypeList() {
		return quesTypeList;
	}

	public void setQuesTypeList(List<BasicDataBean> quesTypeList) {
		this.quesTypeList = quesTypeList;
	}


	public PmClEvaluationHeader getPmClApplyHeader() {
		return pmClApplyHeader;
	}

	public void setPmClApplyHeader(PmClEvaluationHeader pmClApplyHeader) {
		this.pmClApplyHeader = pmClApplyHeader;
	}

	public List<PmClEvaluationHeader> getPmClEvaResultList() {
		return pmClEvaResultList;
	}

	public void setPmClEvaResultList(List<PmClEvaluationHeader> pmClEvaResultList) {
		this.pmClEvaResultList = pmClEvaResultList;
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

	public List<PmClEvaluationHeader> getPmClEvaluationHeaderListHis() {
		return pmClEvaluationHeaderListHis;
	}

	public void setPmClEvaluationHeaderListHis(
			List<PmClEvaluationHeader> pmClEvaluationHeaderListHis) {
		this.pmClEvaluationHeaderListHis = pmClEvaluationHeaderListHis;
	}

	/**
	 * @return the closeApplyUser
	 */
	public String getCloseApplyUser() {
		return closeApplyUser;
	}

	/**
	 * @param closeApplyUser the closeApplyUser to set
	 */
	public void setCloseApplyUser(String closeApplyUser) {
		this.closeApplyUser = closeApplyUser;
	}
	
}
