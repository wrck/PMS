package com.dp.plat.decorators;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.displaytag.decorator.TableDecorator;
import org.displaytag.properties.MediaTypeEnum;

import com.dp.plat.context.UserContext;
import com.dp.plat.data.bean.BasicDataBean;
import com.dp.plat.data.bean.CallBackComment;
import com.dp.plat.data.bean.Department;
import com.dp.plat.data.bean.DpActProcDesc;
import com.dp.plat.data.bean.DpComment;
import com.dp.plat.data.bean.PmClCBData;
import com.dp.plat.data.bean.PmClEvaluationHeader;
import com.dp.plat.data.bean.PmClosedLoopQuesnaire;
import com.dp.plat.data.bean.Presales;
import com.dp.plat.data.bean.ProcdefDelegate;
import com.dp.plat.data.bean.Project;
import com.dp.plat.data.bean.ProjectDeliver;
import com.dp.plat.data.bean.ProjectWeekly;
import com.dp.plat.data.bean.Role;
import com.dp.plat.data.bean.ShipmentInfo;
import com.dp.plat.data.bean.User;
import com.dp.plat.init.SpringInit;
import com.dp.plat.prob.bean.Prob;
import com.dp.plat.prob.bean.ProbRestore;
import com.dp.plat.prob.bean.ProbRestoreWeekly;
import com.dp.plat.prob.bean.SoftVersion;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.subcontract.entity.SubcontractDeliver;
import com.dp.plat.subcontract.entity.SubcontractPrice;
import com.dp.plat.subcontract.vo.SubcontractProjectVO;
import com.dp.plat.supervision.entity.ProjectSupervision;
import com.dp.plat.util.ActivityMessage;
import com.dp.plat.util.Base64Util;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.PmClosedLoopConstant;
import com.dp.plat.util.StringEscUtil;

import cn.hutool.core.convert.impl.URLConverter;
/**
 * 作为displayTag的装饰器，在JSP页面起作用
 * 通过getPageContext()能获取JSP页面的上下文，进而获取项目绝对路径等信息
 * eg:
 * 	   getPageContext().getRequest().getServletContext().getContextPath()
 * @author j01441
 *
 */
public class Wrapper extends TableDecorator {
	private static HashMap<String, String> serviceTypeMap = new HashMap<String, String>();
	static {
		BasicDataService basicDataService = SpringInit.getApplicationContext().getBean("basicDataService", BasicDataService.class);
		List<BasicDataBean> serviceTypeList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_SERVICE_TYPE);
		serviceTypeMap.clear();
		for (BasicDataBean basicDataBean : serviceTypeList) {
			serviceTypeMap.put(basicDataBean.getBasicDataId(), basicDataBean.getBasicDataName());
		}
	}
	
	public String getWeeklyer(){
		ProbRestoreWeekly weekly = (ProbRestoreWeekly) getCurrentRowObject();
		return "<a href='module/download.action?fileId="+weekly.getFileId()+"'>"+weekly.getFileName()+"</a>";
	}
	
	public String getProjectNamea(){
		ProbRestore probRestore = (ProbRestore) getCurrentRowObject();
		int projectId = probRestore.getProjectId();
		String projectName = probRestore.getProjectName();
		if(projectId  == 0){//没有项目
			return projectName;
		}else{
			String paramId = Base64Util.EncodeBase64(projectId);
			return "<a target='_blank' href='"+getPageContext().getRequest().getServletContext().getContextPath()+"/module/ProjectModify.action?project.paramId="+paramId+"&result=310' >"+projectName+"</a>";
		}
	}

	public String getSoftCheckBox(){
		SoftVersion softVersion = (SoftVersion) getCurrentRowObject();
		StringBuilder ver = new StringBuilder();
		ver.append("conp-");
		if(softVersion.getConp() != null){
			ver.append(softVersion.getConp());
		}
		ver.append(",");
		ver.append("boot-");
		if(softVersion.getBoot() != null){
			ver.append(softVersion.getBoot());
		}
		ver.append(",");
		ver.append("cpld-");
		if(softVersion.getCpld() != null){
			ver.append(softVersion.getCpld());
		}
		ver.append(",");
		ver.append("pcb-");
		if(softVersion.getPcb() != null){
			ver.append(softVersion.getPcb());
		}
		if(ver.length() == 22){
			return null;
		}
		return "<input type='checkbox' name='softVersionCodes' value='"+ver.toString()+"'/>";
	}
	
	public String getProjectCheckBox() {
		ProbRestore probRestore = (ProbRestore) getCurrentRowObject();
		int index = getListIndex();
		String html = "<input type='checkbox' name='probRestoreTaskList[" + index + "].ischecked' value='0'>"
				+ "<input type='hidden' name='probRestoreTaskList[" + index + "].serialNum' value='" + probRestore.getSerialNum() + "'>"
				+ "<input type='hidden' name='probRestoreTaskList[" + index + "].itemModel' value='" + probRestore.getItemModel() + "'>"
				+ "<input type='hidden' name='probRestoreTaskList[" + index + "].conp' value='" + probRestore.getConp() + "'>"
				+ "<input type='hidden' name='probRestoreTaskList[" + index + "].boot' value='" + probRestore.getBoot() + "'>"
				+ "<input type='hidden' name='probRestoreTaskList[" + index + "].cpld' value='" + probRestore.getCpld() + "'>"
				+ "<input type='hidden' name='probRestoreTaskList[" + index + "].pcb' value='" + probRestore.getPcb() + "'>"
				+ "<input type='hidden' name='probRestoreTaskList[" + index + "].projectName' value='" + probRestore.getProjectName() + "'>"
				+ "<input type='hidden' name='probRestoreTaskList[" + index + "].contractNo' value='" + probRestore.getContractNo() + "'>"
				+ "<input type='hidden' name='probRestoreTaskList[" + index + "].officeCode' value='" + probRestore.getOfficeCode() + "'>"
				+ "<input type='hidden' name='probRestoreTaskList[" + index + "].projectId' value='" + probRestore.getProjectId() + "'>";		
		return html;
	}
	public String getProbNumCheck(){
		Prob prob = (Prob) getCurrentRowObject();
		User currectUser = UserContext.getUserContext().getUser();
		String html = "";
		String status = prob.getStatus();
		if (currectUser.isHasRole(MessageUtil.ROLE_PROB_ADMIN)) {
			if ("1".equals(status) || "8".equals(status)) {
				html = "<a href='module/prob_input.action?prob.probId=" + prob.getProbId() + "'>审批</a>";
			} else if ("14".equals(prob.getWatch())){ // 只有跟踪公告才需要发布任务
				html = "<a href='module/prob_edit.action?prob.probId=" + prob.getProbId() + "'>发布任务</a>";
			}
		} else if (currectUser.isHasRole(MessageUtil.ROLE_PROB_RD) && prob.getTrackingUser().equals(currectUser.getUsername()) && "6".equals(status)) {
			html = "<a href='module/prob_input.action?prob.probId=" + prob.getProbId() + "'>编辑</a>";
		} else {
			html = "<a href='module/prob_edit.action?prob.probId=" + prob.getProbId() + "'>查看</a>";
		}
		return html;
	}
	
	public String getProbEdit(){
		Prob prob = (Prob) getCurrentRowObject();
		return "<a href='module/prob_edit.action?prob.probId="+prob.getProbId()+"'>"+prob.getProbNum()+"</a>";
	}
	public String getProbOperate(){
		Prob prob = (Prob) getCurrentRowObject();
		User currectUser = UserContext.getUserContext().getUser();
		String html = "";
		if(currectUser.isHasRole(MessageUtil.ROLE_PROB_SUPPORTER)){
			html += "<a href='module/prob_edit.action?prob.probId="+prob.getProbId()+"' class='btn-link'>查看</a>";
		}
		if(currectUser.isHasRole(MessageUtil.ROLE_PROB_RD) && currectUser.getUsername().equals(prob.getTrackingUser())){
			html += "<a href='module/prob_input.action?prob.probId="+prob.getProbId()+"' class='btn-link'>编辑</a>";
		}
		String status = prob.getStatus();
		if(currectUser.isHasRole(MessageUtil.ROLE_PROB_ADMIN)){
			if("8".equals(status) || "1".equals(status)){
				html += "<a href='module/prob_input.action?prob.probId="+prob.getProbId()+"' class='btn-link'>审批</a>";
			} else if(("4".equals(status) || "5".equals(status)) && "14".equals(prob.getWatch())){
				html += "<a href='module/prob_edit.action?prob.probId="+prob.getProbId()+"' class='btn-link'>发布任务</a>";
			}
			html += "<a href='javascript:void(0)' class='btn-link' onclick='deleteProb(" + prob.getProbId() + ")'>删除</a>";
		}
		if(html.isEmpty()){
			if(currectUser.isHasRole(MessageUtil.ROLE_PROB_RD)){
				html += "<a href='module/prob_input.action?prob.probId="+prob.getProbId()+"' class='btn-link'>查看</a>";
			} else {
				html += "<a href='module/prob_edit.action?prob.probId="+prob.getProbId()+"' class='btn-link'>查看</a>";
			}
		}
		return html;
	}
	
	public String getProbRestoreBox(){
		ProbRestore probRestore = (ProbRestore) getCurrentRowObject();
		return "<input type='checkbox' name='id' value='"+probRestore.getId()+"'/>";
	}
	
	public String getProbRestoreDelete(){
		ProbRestore probRestore = (ProbRestore) getCurrentRowObject();
		return "<a href='JavaScript:void(0)' onclick='deleteSingle(" + probRestore.getId() + ")'>删除</a>";
	}
	
	public String getPresalesWrapper(){
		Presales presales = (Presales) getCurrentRowObject();
		UserContext context = UserContext.getUserContext();
		String username = UserContext.getUserContext().getUsername();
		String taskDefKey = presales.getTaskDefKey();
		if( presales.getApplyState() == ActivityMessage.FLOW_RUNING || presales.getApplyState() == ActivityMessage.FLOW_UNSTART
				){//任务ID不为空，且为当前办理人
			if("usertask2".equals(taskDefKey) && username.equals(presales.getTaskAssignee())){
				return "<a href='module/presales_smaduit.action?presales.presalesId="+presales.getPresalesId()+"'>办理</a>";
			} else if("usertask3".equals(taskDefKey) && username.equals(presales.getTaskAssignee())){
				return "<a href='module/presales_pmaduit.action?presales.presalesId="+presales.getPresalesId()+"'>办理</a>";
			} else if("usertask4".equals(taskDefKey) && "emRole".equals(presales.getTaskAssignee()) && (context.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || context.isHasRole(MessageUtil.ROLE_PRESALES_STAFF))){
				return "<a href='module/presales_emaduit.action?presales.presalesId="+presales.getPresalesId()+"'>回访</a>";
			} else if("usertask1".equals(taskDefKey) && "emRole".equals(presales.getTaskAssignee()) && (context.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || context.isHasRole(MessageUtil.ROLE_PRESALES_STAFF))){
				return "<a href='module/presales_input.action?presales.presalesId="+presales.getPresalesId()+"' >办理</a>";
			} else if(presales.getApplyState() == ActivityMessage.FLOW_UNSTART){
				return "<a href='module/presales_input.action?presales.presalesId="+presales.getPresalesId()+"' >创建</a>";
			}
		}
		if (context.isHasRole(MessageUtil.ROLE_SERVICEMANAGER) && username.equals(presales.getServiceManager())) {
			return "<a href='module/presales_smaduit.action?presales.presalesId="+presales.getPresalesId()+"' >变更</a>";
		}
		return "<a href='module/presales_read.action?presales.presalesId="+presales.getPresalesId()+"' >查看</a>";
	}
	
	
	public String getBasicDataWrapper(){
		BasicDataBean basicDataBean = (BasicDataBean) getCurrentRowObject();
		return "<a href=\"javascript:void(0)\" onclick='updateBasicData("+basicDataBean.getId()+")' class=\"btn btn-default updateMark\"><span class=\"glyphicon glyphicon-edit\"></span>编辑</a>";
	}
	
	public String getSeeQuesnaire(){
		CallBackComment comment = (CallBackComment) getCurrentRowObject();
		int quesnaireId = comment.getQuesnaireId();
		if(quesnaireId != 0){
			return "<a href=\"javascript:void(0)\" onclick=\"javascript:popWindow('module/sub/callback_seeQuesnaire.action?quesnaireId="+quesnaireId+"',880, 600,'查看测评问卷', 'BudgetUpload', true);\">点击查看</a>";
		}
		return "";
	}
	
	public String getProjectNameEr(){
		DpActProcDesc dap = (DpActProcDesc) getCurrentRowObject();
		String projectname = dap.getProjectName();
		if(projectname!= null){
			if(projectname.startsWith("<")){
				projectname = projectname.substring(1);
			}
			if(projectname.endsWith(">")){
				projectname = projectname.substring(0, projectname.length()-1);
			}
			int circle = 0;
			StringBuffer sb = new StringBuffer(projectname);
			if(projectname.length()%30 != 0){
				circle = projectname.length()/30+1;
			}else{
				circle = projectname.length()/30;
			}
			for (int i = 1; i < circle; i++) {
				sb = sb.insert(i*30+(i-1)*5, "<br/>");
			}
			return sb.toString();
		}
		return projectname;
	}
	
	public String getProjectNameWarrper(){
		Project project = (Project) getCurrentRowObject();
		String state = project.getProjectState();
		int projectId = project.getProjectId();
		String projectname = project.getProjectName();
		if(projectname!= null){
			if(projectname.startsWith("<")){
				projectname = projectname.substring(1);
			}
			if(projectname.endsWith(">")){
				projectname = projectname.substring(0, projectname.length()-1);
			}
		}
		
		if(state.equals("10") && projectId == -1){//待创建项目
			return "<a href='javascript:void(0)' class='createMark' onclick='createProject( \""+project.getContractNo()+"\")'>"+projectname+"</a>";
		}else{
			return "<a href='javascript:void(0)' class='updateMark' onclick='updateProject( \""+ Base64Util.EncodeBase64(project.getProjectId())+"\")'>"+projectname+"</a>";
		}
	}
	
	public String getProjectNameWithCodeWarrper(){
		Project project = (Project) getCurrentRowObject();
		int projectId = project.getProjectId();
		String projectname = project.getProjectName();
		if(projectname!= null){
			if(projectname.startsWith("<")){
				projectname = projectname.substring(1);
			}
			if(projectname.endsWith(">")){
				projectname = projectname.substring(0, projectname.length()-1);
			}
		}
		return "<span>" + project.getProjectCode() + "</span><br><a href='javascript:void(0)' class='updateMark' onclick='updateProject( \""+ Base64Util.EncodeBase64(projectId)+"\")'>"+projectname+"</a>";
	}
	
	public String getHandleWarrper(){
		Project project = (Project) getCurrentRowObject();
		String state = project.getProjectState();
		int projectId = project.getProjectId();
		if(state.equals(MessageUtil.PROJECT_STATE_CREATING)&& projectId == -1){//待创建项目
			return "<a href=\"javascript:void(0)\" onclick='createProject( \""+project.getContractNo()+"\")' class=\"btn btn-default createMark\"><span class=\"glyphicon glyphicon-edit\"></span>创建</a>";
		}else if(state.equals(MessageUtil.PROJECT_STATE_CLOSEDLOOP)){
			return "<a href=\"javascript:void(0)\" onclick='updateProject(\""+Base64Util.EncodeBase64(project.getProjectId())+"\" )' class=\"btn btn-default updateMark\"><span class=\"glyphicon glyphicon-edit\"></span>查看</a>";
		}else{
			return "<a href=\"javascript:void(0)\" onclick='updateProject( \""+Base64Util.EncodeBase64(project.getProjectId())+"\" )' class=\"btn btn-default updateMark\"><span class=\"glyphicon glyphicon-edit\"></span>编辑</a>";
		}
	}
	
	public String getCheckboxWrapper(){
		ShipmentInfo obj = (ShipmentInfo) getCurrentRowObject();
		String code = obj.getBarCode();
		// 转移标识，默认:-1,转出:1，转入:0
		String transferFlag = obj.getTransferFlag();
		if ("1".equals(transferFlag)) {
			return "";
		}
		return "<input name=\"selected\" type=\"checkbox\" value=\""+code+"\">";
	}
	
	public String getProjectCheckWrapper(){
		Project obj = (Project) getCurrentRowObject();
		Integer code = obj.getProjectId();
		return "<input name=\"selected\" type=\"checkbox\" value=\""+code+"\">";
	}
	
	public String getProjectRadioWrapper(){
		Project obj = (Project) getCurrentRowObject();
		Integer code = obj.getProjectId();
		return "<input name=\"selected\" type=\"radio\" value=\""+code+"\">";
	}
	
	public String getTransferFlagWrapper(){
		ShipmentInfo shipmentInfo = (ShipmentInfo) getCurrentRowObject();
		String transferFlag = shipmentInfo.getTransferFlag();
		Integer projectId = 0;
		String flagText = null;
		String CSSClass = "";
		String iconClass = "glyphicon-share-alt";
		// 转移标识，默认:-1,转出:1，转入:0
		if ("1".equals(transferFlag)) {
			projectId = shipmentInfo.getTransferProjectId();
			flagText = " 已转出";
			CSSClass = "text-danger";
//			iconClass = "glyphicon-log-out";
		} else if ("0".equals(transferFlag)){
			projectId = shipmentInfo.getChProjectId();
			flagText = " 已转入";
			iconClass += " rotate180";
//			iconClass = "glyphicon-log-in";
		} else {
			return "";
		}
		return "<a href='javascript:void(0)' class='transferMark " + CSSClass + "' onclick='transferProject(\""+ Base64Util.EncodeBase64(projectId)+"\")'><i class='glyphicon " + iconClass + "'></i>" + flagText +"</a>";
	}
	
	public String getSmsTargetUrl() {
		Object object = getCurrentRowObject();
		Integer objId = null;
		String procType = null;
		if (object instanceof Map) {
			objId = (Integer) ((Map) object).get("objId");
			procType = (String) ((Map) object).get("procType");
		} else if (object instanceof SubcontractPrice){
			objId = ((SubcontractPrice) object).getObjId();
			procType = ((SubcontractPrice) object).getProcType();
		}
		
		if (objId == null || procType == null) {
			return "";
		}
		return "<a href=\"javascript:void(0)\" onclick=\"javascript:window.open('"
				+ StringEscUtil.getText("pm.subcontract.sms.url") + "module/BusinessView43.action?param.objId=" + objId
				+ "&dpActProcDesc.procType="+ procType +"');\"><span class=\"glyphicon glyphicon-link\"></span>外链</a>";
	}
	
	public String getSubcontractWsOperator() {
		Map<String, Object> object = (Map<String, Object>) getCurrentRowObject();
		if (object.get("taskId") != null) {
			String html = "<a href='module/subcontract_input.action?subcontract.id=" + object.get("subcontractId") + "'>办理</a>";
			if (!"-1".equals(object.get("taskId").toString())) {
				html += "&nbsp;&nbsp;&nbsp;"
						+ "<a target='_blank' href='work/sub/WorkFlowViewCurrentImage.action?param.taskId="
						+ object.get("taskId") + "'>查看当前流程</a>";
			}
			return html;
//			return "<a href='module/subcontract_input.action?subcontract.id=" + object.get("subcontractId") + "'>办理</a>"
//					+ "&nbsp;&nbsp;&nbsp;"
//					+ "<a target='_blank' href='work/sub/WorkFlowViewCurrentImage.action?param.taskId="
//					+ object.get("taskId") + "'>查看当前流程</a>";
		}
		return "";
	}
	
	public String getSubcontractFileName() {
		SubcontractDeliver object = (SubcontractDeliver) getCurrentRowObject();
		return "<a href='module/subcontract_downloadFile.action?redirect=" + Base64Util.EncodeBase64(object.getId())
				+ "'>" + object.getFileName() + "</a>";
	}
	
	public String getSubcontractSeeQuesnaireLink() {
		Map<String, Object> object = (Map<String, Object>) getCurrentRowObject();
		if (object.get("quesnaireId") != null) {
			return "<a href=\"javascript:popWindow('module/sub/callback_seeQuesnaire.action?quesnaireId=" + object.get("quesnaireId") + "',880, 600, '查看测评问卷', 'BudgetUpload', true)\">查看问卷</a>";
		}
		return "";
	}
	
	public String getPaidAmountWrapper() {
		SubcontractProjectVO object = (SubcontractProjectVO) getCurrentRowObject();
		DecimalFormat decimalFormat = new DecimalFormat("#,##0.##");
		if (StringUtils.isNotBlank(object.getPaidAmount())) {
			BigDecimal b = new BigDecimal(object.getPaidAmount());
			return decimalFormat.format(b);
		}
		return "";
	}
	
	public String getStateWrapper() {
		Object obj = getCurrentRowObject();
		try {
			Method method = obj.getClass().getDeclaredMethod("getState");
			Object state = method.invoke(obj);
			if (state == null) {
				return "";
			}
			if (state instanceof Boolean) {
				if (((Boolean) state).booleanValue()) {
					return "有效";
				} else {
					return "失效";
				}
			} else if (state instanceof Integer) {
				if (state.equals(1)) {
					return "有效";
				} else {
					return "失效";
				}
			} else if (state instanceof String) {
				if (state.equals("1")) {
					return "有效";
				} else {
					return "失效";
				}
			}
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public String getWeeklyHandleWrapper(){
		ProjectWeekly weekly = (ProjectWeekly) getCurrentRowObject();
		
		return "<a href=\"javascript:void(0)\" onclick=\"javascript:popWindow('module/sub/EditWeekly.action?projectWeekly.weeklyId="+weekly.getWeeklyId()+"', 1100, 700,'查看周报', 'BudgetUpload', true);\" class=\"btn btn-default btn-block\"  ><span class=\"glyphicon glyphicon-edit\"></span> 编辑</a>";
	}
	
  	public String getStatusWrapper(){
  		User user = (User) getCurrentRowObject();
  		int status = user.getStatus();
  		if(status == 0){
  			return "失效";
  		}else{
  			return "生效";
  		}
  	} 
	
  	public String getUserWriteWrapper(){
  		User user = (User) getCurrentRowObject();
  		int id = user.getId();
  		return "<a href='base/UserEdit.action?user.id="+id+"'>编辑</a>";
  	}
	
	public String getDelegateAssignee(){
		DpActProcDesc param = (DpActProcDesc)getCurrentRowObject();
		String assignee = param.getAssignee();
		if(param.getCause() != null && !param.getCause().equals("")){
			assignee = assignee + "[委派原因："+param.getCause()+"]";
		}
		return assignee;
	}
	
	
	public String getProcDefDelegateStatus(){
		ProcdefDelegate dp = (ProcdefDelegate)getCurrentRowObject();
		if(dp.getStatus() == 1){
			return "有效";
		}else {
			return "无效";
		}
	}
	
	public String getProcDefDelegateOperator(){
		ProcdefDelegate dp = (ProcdefDelegate)getCurrentRowObject();
		return "<a href=\"work/EditProcDefDelegate.action?procdefDelegate.id="
		+ dp.getId() + "\">查看</a>";
	}
	
	public String getUserId(){
		DpComment dc = (DpComment)getCurrentRowObject();
		String userId = dc.getUserId();
		if(dc.getOwner() != null && !dc.getOwner().equals("")){
			userId = userId + "[原办理人："+dc.getOwner()+",委派原因："+dc.getCause()+"]";
		}
		return userId;
	}
	public String getSeeSelfHidOperteTask(){
		DpActProcDesc ht = (DpActProcDesc)getCurrentRowObject();
		
		String str =  "<a target='_blank' href=\"work/WorkFlowHisTaskForm.action?param.instId="
			       +ht.getProcInstId()+"\">查看详情</a>";
		return str;
	}
	public String getSelfTaskOperator(){
		DpActProcDesc task = (DpActProcDesc)getCurrentRowObject();
		String desc = "办理";
		
		String str1 =  "<a href=\"work/WorkFlowViewTaskForm.action?param.taskId="
				       +task.getTaskId()+"&param.canSee="+task.getIsCandidateUser()+"&dpActProcDesc.procType="+task.getProcType()+"\">"+desc+"</a>";
		String str2 =  "&nbsp;&nbsp;&nbsp;<a target=\"_blank\" href=\"work/sub/WorkFlowViewCurrentImage.action?param.taskId="
			       +task.getTaskId()+"\">查看当前流程</a>";
		if(task.getIsCandidateUser() == 1||task.getCansee()==0){
			desc = "查看";
			str2 = "";
		}
		return str1 + str2;
	}
	
	public String getTaskOperator(){
		HistoricProcessInstance task = (HistoricProcessInstance)getCurrentRowObject();
		String str2 =  "&nbsp;<a target=\"_blank\" href=\"work/sub/WorkFlowViewCurrentImage.action?param.taskId="
			       +task.getId()+"\">查看当前流程</a>";
		return str2;
	}
	
	
	public String getWorkFlowDelDeployment(){
		Deployment d = (Deployment)getCurrentRowObject();
		
		return "<a style=\"cursor:pointer;\" onclick=\"delDeployment(" 
			+d.getId()+")\">删除</a>";
	}
	public String getWorkFlowViewImage(){
		ProcessDefinition pd = (ProcessDefinition)getCurrentRowObject();
		
		return "<a target=\"_blank\" href=\"work/WorkFlowViewImage.action?param.deploymentId="
		+pd.getDeploymentId() + "&param.imageName=" + pd.getDiagramResourceName() +"\">查看流程图</a>";
	}
	
	
	

	public String getRealNameWr() {
		User user = (User) getCurrentRowObject();
		int id = user.getId();

		return "<a href=\"sys/ModifyUserInfo.action?user.id=" //$NON-NLS-1$
				+ id + "\">" + user.getRealName() + "</a>";

	}

	

	public String getUserOperator() {
		User user = (User) getCurrentRowObject();
		int id = user.getId();

		return "<a href=\"sys/ModifyUserInfo.action?user.id=" //$NON-NLS-1$
				+ id + "\">编辑</a>";

	}

	public String getEditRole() {
		Role role = (Role) getCurrentRowObject();
		int id = role.getId();
		return "<a href=\"base/RoleEdit.action?role.id=" + id + "\">"
				+ "编辑" + "</a>";
	}

	
	public String getRoleStatus() {
		Role role = (Role) getCurrentRowObject();
		if(role.getStatus() == 1){
			return "有效";
		}else {
			return "失效";
		}
	}
	
	public String getEditDepartment() {
		Department department = (Department) getCurrentRowObject();
		int id = department.getId();
		return "<a href=\"base/DepartmentEdit.action?department.id=" + id + "\">"
				+ "编辑" + "</a>";
	}

	
	public String getDepartmentStatus() {
		Department department = (Department) getCurrentRowObject();
		if(department.getStatus() == 1){
			return "有效";
		}else {
			return "无效";
		}
	}
	
	public String getPmCLDoSomthing() {
		Project project=(Project)getCurrentRowObject();
		return "<a href='module/PmClosedLoop_addPmCLApply.action?pmClosedLoopResultType=1&project.contractNo="+project.getContractNo()+"'>操作</a>";
	}
	
	public String getPmCLViewCurrentProcess() {
		Project project=(Project)getCurrentRowObject();
		String returnAction="addPmCLApply";	
		int pmClosedLoopResultType=1;
		if (project.getTaskId()!=null&&!(project.getTaskId().equals(""))) {
			return "<a href='module/PmClosedLoop_"+returnAction+".action?pmClosedLoopResultType="+pmClosedLoopResultType+"&project.contractNo="+project.getContractNo()+"&project.taskId="+project.getTaskId()+"'>操作</a>"+
					"&nbsp;&nbsp;&nbsp;"+
					"<a target='_blank' href='work/sub/WorkFlowViewCurrentImage.action?param.taskId="+project.getTaskId()+"'>查看当前流程</a>";
		}
		return "";
	}
	
	public String getSmCLViewCurrentProcess() {
		Project project=(Project)getCurrentRowObject();
		String returnAction="addSmCLApply";	
		int pmClosedLoopResultType=1;
		if (project.getTaskId()!=null&&!(project.getTaskId().equals(""))) {
			return "<a href='module/PmClosedLoop_"+returnAction+".action?pmClosedLoopResultType="+pmClosedLoopResultType+"&project.contractNo="+project.getContractNo()+"&project.taskId="+project.getTaskId()+"'>操作</a>"+
					"&nbsp;&nbsp;&nbsp;"+
					"<a target='_blank' href='work/sub/WorkFlowViewCurrentImage.action?param.taskId="+project.getTaskId()+"'>查看当前流程</a>";
		}
		return "";
	}
	
	public String getCbCLViewCurrentProcess() {
		Project project=(Project)getCurrentRowObject();
		//待调整，根据项目的状态跳转
		String returnAction="addCbCLApply";
		int pmClosedLoopResultType=1;
		if (project.getTaskId()!=null&&!(project.getTaskId().equals(""))) {
			return "<a href='module/PmClosedLoop_"+returnAction+".action?pmClosedLoopResultType="+pmClosedLoopResultType+"&project.contractNo="+project.getContractNo()+"&project.taskId="+project.getTaskId()+"'>操作</a>"+
					"&nbsp;&nbsp;&nbsp;"+
					"<a target='_blank' href='work/sub/WorkFlowViewCurrentImage.action?param.taskId="+project.getTaskId()+"'>查看当前流程</a>";
		}
		return "";
	}
	
	public String getClCLViewCurrentProcess() {
		Project project=(Project)getCurrentRowObject();
		//待调整，根据项目的状态跳转
		String returnAction="addClCLApply";
		int pmClosedLoopResultType=1;
		if (project.getTaskId()!=null&&!(project.getTaskId().equals(""))) {
			return "<a href='module/PmClosedLoop_"+returnAction+".action?pmClosedLoopResultType="+pmClosedLoopResultType+"&project.contractNo="+project.getContractNo()+"&project.taskId="+project.getTaskId()+"'>操作</a>"+
					"&nbsp;&nbsp;&nbsp;"+
					"<a target='_blank' href='work/sub/WorkFlowViewCurrentImage.action?param.taskId="+project.getTaskId()+"'>查看当前流程</a>";
		}
		return "";
	}
	
	
	public String getClQuestionnaireTemplateDo() {
		PmClosedLoopQuesnaire pmClosedLoopQuesnaire=(PmClosedLoopQuesnaire)getCurrentRowObject();
		if(pmClosedLoopQuesnaire.getQuestionnaireStatus()==PmClosedLoopConstant.CL_STATUS_DRAFT){
			return "<a href='base/EditPmClosedLoopQuesnaire.action?pmClosedLoopQuesnaire.id="+pmClosedLoopQuesnaire.getId()+"'>编辑</a>"+
					"&nbsp;&nbsp;&nbsp;"+
					"<a class='deleteQues' href='base/PmClQues_deleteHeader.action?pmClosedLoopQuesnaire.id="+pmClosedLoopQuesnaire.getId()+"'>删除</a>";
		}else if(pmClosedLoopQuesnaire.getQuestionnaireStatus()==PmClosedLoopConstant.CL_STATUS_SUBMIT){
			return "<a class='endEffective' href='base/PmClQues_endEffective.action?pmClosedLoopQuesnaire.id="+pmClosedLoopQuesnaire.getId()+"'>失效</a>";
		}
		return "";
	}
	
	public String getBarcode(){
		return "<a href='javascript:void(0)' onclick='showBarcode(\"序列号明细\")'>查看详情</a>";
	}
	
	/**
	 * 母子公司发货条码对应关系
	 * @return
	 */
	public String getBarCodeRelation() {
	    return wrapperValueRelation("barCode", "barCode2");
	}
	
	/**
     * 母子公司发货产品编码对应关系
     * @return
     */
    public String getItemCodeRelation() {
        return wrapperValueRelation("itemCode", "itemCode2");
    }
    
    /**
     * 母子公司发货产品名称对应关系
     * @return
     */
    public String getItemNameRelation() {
        return wrapperValueRelation("itemName", "itemName2");
    }
    
    private String wrapperValueRelation(String key1, String key2) {
        Object obj = getCurrentRowObject();
        String value = "";
        String value2 = "";
        if (obj instanceof Map) {
            Map info = (Map) getCurrentRowObject();
            value = (String) info.get(key1);
            value2 = (String) info.get(key2);
        } else {
            Class<? extends Object> clazz = obj.getClass();
            // 子类无法直接获取父类的属性，所以需要循环遍历所有父类
            value = (String) reflectObjectValue(obj, key1, clazz);
            value2 = (String) reflectObjectValue(obj, key2, clazz);
        }
        value = StringUtils.trimToEmpty(value);
        StringBuilder str = new StringBuilder(value);
        if (StringUtils.isNotBlank(value2) && !value2.equals(value)) {
            Object mediaType = getPageContext().getAttribute("mediaType");
            if (MediaTypeEnum.HTML.equals(mediaType)) {
                str.append("<br><span class='text-danger'>(").append(value2).append(")</span>");
            } else {
                str.append("\r\n(").append(value2).append(")");
            }
        }
        return str.toString();
    }

    /**
     * 反射获取对象的值
     * @param obj
     * @param fieldName
     * @param clazz
     * @return
     */
    private Object reflectObjectValue(Object obj, String fieldName, Class<?> clazz) {
        Object value = null;
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            if (field != null) {
                value = field.get(obj);
            }
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            // 子类无法直接获取父类的属性，所以需要递归遍历所有父类
            if (clazz.getGenericSuperclass() != null) {
                value = reflectObjectValue(obj, fieldName, (Class<?>) clazz.getGenericSuperclass());
            }
        }
        return value;
    }
        
	public String getDeliverableName(){
		ProjectDeliver pd = (ProjectDeliver)getCurrentRowObject();
		String downloadTag = "<a href='module/DownloadFile.action?downname=%s&downpath=%s'>下载</a>";
        String viewTag = "<a href='%s%s' target='_blank'>预览</a>";
        Pattern pattern = Pattern.compile(".*\\.(png|jpg|jpeg|gif)$", Pattern.CASE_INSENSITIVE);
        String contextPath = getPageContext().getRequest().getServletContext().getContextPath();
        String fileName = pd.getDeliverableName();
        String filePath = pd.getDeliverablePath();
        String encodeFileName = fileName;
        String encodeFilePath = filePath;
		try {
			encodeFilePath = URLEncoder.encode(filePath, "UTF-8");
			encodeFileName = URLEncoder.encode(fileName, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String atag2 = "<a href='module/DownloadFile.action?downname=%s&downpath=%s'>%s</a>";
		String alink = String.format(atag2 , encodeFileName, encodeFilePath, fileName);
		StringBuilder str = new StringBuilder();
		if (pattern.matcher(fileName).matches()) {
			str .append("<span class='hover-wrapper'>")
				.append(alink)
				.append("<label class='hover-label'>")
				.append(String.format(viewTag, contextPath, filePath))
				.append(String.format(downloadTag, encodeFileName, encodeFilePath))
				.append("</label>")
				.append("</span>");
		} else {
			str.append(alink);
		}
		return str.toString();
//		return "<a href='module/DownloadFile.action?downname=" + pd.getDeliverableName() + "&downpath=" + pd.getDeliverablePath() + "'>"
//					+ pd.getDeliverableName()
//				+ "</a>";
	}
	

	public String getPmCLApproveResult(){
		PmClEvaluationHeader pmClEvaluationHeader=(PmClEvaluationHeader)getCurrentRowObject();
		if(pmClEvaluationHeader.getEvaluationType()==PmClosedLoopConstant.CL_EVALU_TYPE_PM){
			return "发起回访申请";
		}else{
			if(pmClEvaluationHeader.getEvaluationResult()==PmClosedLoopConstant.CL_EVALU_RESULT_AGREE){
				if(pmClEvaluationHeader.getEvaluationType()==PmClosedLoopConstant.CL_EVALU_TYPE_CL){
					return "项目已闭环";
				}
				return "申请通过";
			}else if(pmClEvaluationHeader.getEvaluationResult()==PmClosedLoopConstant.CL_EVALU_RESULT_CANTCB){
				return "无法回访";
			}else{
				return "驳回整改";
			}
		}
	}

	public String getOperate(){
		ProjectDeliver pd = (ProjectDeliver)getCurrentRowObject();
		Integer id = pd.getId();
		return "<a href='javascript:void(0)' onclick='deleteDeliverById(" + id + ", this)'>删除</a>";
	}
	
	public String getPmCLWorkSpaceOpe(){
		DpActProcDesc dpActProcDesc=(DpActProcDesc)getCurrentRowObject();
		if((Presales.class.getSimpleName().equals(dpActProcDesc.getProcTypeName()))){
			return "<a href='module/presales_aduit.action?presales.presalesId="+dpActProcDesc.getApplyNum()+"'>办理</a>"+
					"&nbsp;&nbsp;&nbsp;"+
					"<a target='_blank' href='work/sub/WorkFlowViewCurrentImage.action?param.taskId="+dpActProcDesc.getTaskId()+"'>查看当前流程</a>";
		}
		if (ProjectSupervision.class.getSimpleName().equals(dpActProcDesc.getProcTypeName())) {
		    return "<a href='module/ProjectModify.action?project.paramId="+Base64Util.EncodeBase64(dpActProcDesc.getApplyNum())+"&result=314'>办理</a>";
		}
		
		if(dpActProcDesc.getTaskId()!=null&&!(dpActProcDesc.getTaskId().equals(""))){
			int type = 100;
			if(ActivityMessage.CALLBACK_KEY.equals(dpActProcDesc.getProcTypeName())){
				type = 101;
			}
			return "<a href='module/ProjectModify.action?workSpaceReturnType="+type+"&project.paramId="+Base64Util.EncodeBase64(dpActProcDesc.getApplyNum())+"'>办理</a>"+
					"&nbsp;&nbsp;&nbsp;"+
					"<a target='_blank' href='work/sub/WorkFlowViewCurrentImage.action?param.taskId="+dpActProcDesc.getTaskId()+"'>查看当前流程</a>";
		}else{
			return "<a href='module/ProjectModify.action?project.paramId="+Base64Util.EncodeBase64(dpActProcDesc.getApplyNum())+"'>查看/办理</a>";
		}
		
	}
	public String getProcTypeNameWrapper(){
		DpActProcDesc dpActProcDesc=(DpActProcDesc)getCurrentRowObject();
		return dpActProcDesc.getProcTypeName() == null ? "自定义流程" : dpActProcDesc.getProcTypeName();
	}
	
	public String getWorkspEvaluaResult(){
		DpActProcDesc dpObj=(DpActProcDesc)getCurrentRowObject();
		if(dpObj.getEvaluaResult()==PmClosedLoopConstant.CL_EVALU_RESULT_AGREE){
			return "审核通过";
		}else if(dpObj.getEvaluaResult()==PmClosedLoopConstant.CL_EVALU_RESULT_REJECT){
			return "驳回整改";
		}else if(dpObj.getEvaluaResult()==PmClosedLoopConstant.CL_EVALU_RESULT_CANTCB){
			return "无法回访";
		}else{
			return "";
		}
	}
	
	public String getProjectImpl() {
		DpActProcDesc dpObj = (DpActProcDesc) getCurrentRowObject();
		String ServiceType = dpObj.getProjectImpl();
		return serviceTypeMap.get(ServiceType);
	}
	
	public String getPmSeeCbCl(){
		PmClEvaluationHeader pmObj=(PmClEvaluationHeader)getCurrentRowObject();
		
		if(pmObj.getEvaluationType()==PmClosedLoopConstant.CL_EVALU_TYPE_CB&&pmObj.getStatus()==PmClosedLoopConstant.CL_STATUS_SUBMIT&&pmObj.getEvaluationResult()!=PmClosedLoopConstant.CL_EVALU_RESULT_CANTCB){
			return "<a href='javascript:void(0)'"
					+"onclick=\"javascript:popWindow('module/sub/PmClosedLoopSub_pmSeeCbCl.action?pmClEvaluationHeader.id="+pmObj.getId()+"',880, 600,'查看测评问卷', 'BudgetUpload', true);\""
					+ " >查看回访问卷</a>";
		}else if(pmObj.getEvaluationType()==PmClosedLoopConstant.CL_EVALU_TYPE_CL&&pmObj.getStatus()==PmClosedLoopConstant.CL_STATUS_SUBMIT){
			return "<a href='javascript:void(0)'"
					+"onclick=\"javascript:popWindow('module/sub/PmClosedLoopSub_pmSeeCbCl.action?pmClEvaluationHeader.id="+pmObj.getId()+"',880, 600,'查看测评问卷', 'BudgetUpload', true);\""
					+ " >查看闭环建议</a>";
		}
		
		return "";
	}
	
	public String getClQuestionnaireStatus(){
		PmClosedLoopQuesnaire pmClosedLoopQuesnaire=(PmClosedLoopQuesnaire)getCurrentRowObject();
		if(pmClosedLoopQuesnaire.getQuestionnaireStatus()==PmClosedLoopConstant.CL_STATUS_SUBMIT){
			return "已生效";
		}else if(pmClosedLoopQuesnaire.getQuestionnaireStatus()==PmClosedLoopConstant.CL_STATUS_DRAFT){
			return "草稿";
		}else if(pmClosedLoopQuesnaire.getQuestionnaireStatus()==PmClosedLoopConstant.CL_STATUS_ENDEFFEC){
			return "已失效";
		}
		return "";
	}
	
	public String getClQuestionnaireSee(){
		PmClosedLoopQuesnaire pmClosedLoopQuesnaire=(PmClosedLoopQuesnaire)getCurrentRowObject();
		return "<a href='base/SeePmClosedLoopQuesnaire.action?pmClosedLoopQuesnaire.id="+pmClosedLoopQuesnaire.getId()+"'>查看</a>";					
	}
	
	public String getPmclEvaluationComment(){
		PmClEvaluationHeader pmObj=(PmClEvaluationHeader)getCurrentRowObject();
		
		String commentStr=pmObj.getEvaluationComment();
		StringBuilder commentBuilder=new StringBuilder();
		if(commentStr!=null&&commentStr.length()>0){
			for (int i=0;i<commentStr.length();i++) {
				commentBuilder.append(commentStr.charAt(i));
				if(i>0&&i%18==0){
					commentBuilder.append("<br/>");
				}
			}
			return commentBuilder.toString();
		}
		
		return "";
	}
	
	
	public String getDataCbProName(){
		PmClCBData datacb=(PmClCBData)getCurrentRowObject();
		String str=datacb.getProjectName();
		StringBuilder builder=new StringBuilder();
		if(str!=null&&str.length()>0){	
			if(str!=null&&str.length()>0){
				for (int i=0;i<str.length();i++) {
					builder.append(str.charAt(i));
					if(i>0&&i%10==0){
						builder.append("<br/>");
					}
				}
				return builder.toString();
			}
		}
		return "";
	}
	
	public String getDataCbOpion(){
		PmClCBData datacb=(PmClCBData)getCurrentRowObject();
		String str=datacb.getOpinion();
		StringBuilder builder=new StringBuilder();
		if(str!=null&&str.length()>0){	
			if(str!=null&&str.length()>0){
				for (int i=0;i<str.length();i++) {
					builder.append(str.charAt(i));
					if(i>0&&i%14==0){
						builder.append("<br/>");
					}
				}
				return builder.toString();
			}
		}
		return "";
	}
	
	public String getDataCbEquain(){
		PmClCBData datacb=(PmClCBData)getCurrentRowObject();
		String str=datacb.getEquExplain();
		StringBuilder builder=new StringBuilder();
		if(str!=null&&str.length()>0){	
			if(str!=null&&str.length()>0){
				for (int i=0;i<str.length();i++) {
					builder.append(str.charAt(i));
					if(i>0&&i%10==0){
						builder.append("<br/>");
					}
				}
				return builder.toString();
			}
		}
		return "";
	}
	
	public String getDataCbResult(){
		PmClCBData datacb=(PmClCBData)getCurrentRowObject();
		if(datacb.getEvaResult()==PmClosedLoopConstant.CL_EVALU_RESULT_CANTCB)
			return "无法回访";
		if(datacb.getEvaResult()==PmClosedLoopConstant.CL_EVALU_RESULT_AGREE)
			return "通过";
		if(datacb.getEvaResult()==PmClosedLoopConstant.CL_EVALU_RESULT_REJECT)
			return "不通过";
		return "";
	}
	
	public String getServiceTypeWrapper(){
		PmClCBData datacb=(PmClCBData)getCurrentRowObject();
		String ServiceType = datacb.getServiceType();
		return serviceTypeMap.get(ServiceType);
	}
}
