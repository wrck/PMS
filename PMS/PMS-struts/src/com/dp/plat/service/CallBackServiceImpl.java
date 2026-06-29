package com.dp.plat.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;

import com.dp.plat.context.SpringContext;
import com.dp.plat.dao.CallBackDao;
import com.dp.plat.dao.PmClosedLoopDao;
import com.dp.plat.data.bean.CallBack;
import com.dp.plat.data.bean.CallBackComment;
import com.dp.plat.data.bean.CallBackQuesnaire;
import com.dp.plat.data.bean.PmClQuesnaireResultHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultLine;
import com.dp.plat.data.bean.Project;
import com.dp.plat.data.bean.WorkflowCommonParam;
import com.dp.plat.util.ActivityMessage;
import com.dp.plat.util.MessageUtil;

/**
 * 回访流程业务逻辑处理
 * @author admin
 *
 */
public class CallBackServiceImpl extends BaseServiceImpl implements CallBackService{
	/**
	 * 流程管理
	 */
	private WorkFlowService workFlowService;
	/**
	 * 回访管理
	 */
	private CallBackDao callBackDao;
	/**
	 * 闭环
	 */
	private PmClosedLoopDao pmClosedLoopDao;
	
	public void setWorkFlowService(WorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}
	public void setCallBackDao(CallBackDao callBackDao) {
		this.callBackDao = callBackDao;
	}
	public void setPmClosedLoopDao(PmClosedLoopDao pmClosedLoopDao) {
		this.pmClosedLoopDao = pmClosedLoopDao;
	}
    @Override
	public void startCallBackFlow(CallBack callBack) {
		// 保存申请内容
		int callBackId = callBackDao.insertCallBack(callBack);
		//发起activity流程
		
		//1.获取流程变量
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("programManager" , getLoginName() );
		vars.put("callbackManager", "callbackRole");
		vars.put("projectId", callBack.getProjectId());
		//2.拼接businessKey
		String key = callBack.getClass().getSimpleName();
		
		String businessKey=key+"."+callBackId+"."+callBack.getProjectId();
		
		//3.启动流程
		ProcessInstance process = workFlowService.startProcess(key , businessKey, vars);
		
		String instId = process.getId();
		//4.将instId 回写致申请表,并更新申请状态
		callBackDao.updateCallBackInstId(callBackId , instId);
		
		//5.办理流程
		Task task = workFlowService.queryTaskByBussinessKeyUser(businessKey, getLoginName());
		vars.clear();
		workFlowService.doSelfTask(task, instId ,"发起申请", vars);
		//6.增加自定义的审批意见
		workFlowService.addSelfActComment(callBackId , key ,task.getId() ,instId ,ActivityMessage.COMMENT_APPLY , null );
		
		// 更新项目闭环流程状态，开始后变回回访状态
        this.updateProjectCloseProcessState(callBack.getProjectId(), MessageUtil.PROJECT_CLOSE_PROCESS_STATE_30);
	}
	


	@Override
	public CallBack queryCallBackById(int callBackId) {
		return callBackDao.queryCallBackById(callBackId);
	}
	/**
	 * 说明：调用了闭环申请中的保存问卷的方法
	 */
	@Override
	public void insertCallBackQuesnaire(CallBack callBack,
			PmClQuesnaireResultHeader pmClQuesnaireResultHeader,
			List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList) {
		//1.插入问卷头
		pmClQuesnaireResultHeader.setEvaluationHeaderId(0);
		int pmClQuesnaireResultHeaderId=pmClosedLoopDao.addPmClQuesResultHeader(pmClQuesnaireResultHeader);
		//2.插入问卷结果行信息
		pmClosedLoopDao.addPmClQuesResultLineList(pmClQuesnaireResultLineList, pmClQuesnaireResultHeader);
		
		//3.将问卷信息保存进回访流程中
		//3.0查询本次审批的问卷是否已经保存过
		int callbackQuesnaireId = callBackDao.queryCallBackQuesnaireId(callBack);
		if(callbackQuesnaireId != 0){
			//3.1将新的问卷ID更新到回访问卷表中
			callBackDao.updateCallBackQuesnaire(callbackQuesnaireId , pmClQuesnaireResultHeaderId , pmClQuesnaireResultHeader.getStatus());
		}else{
			//3.1查询问卷版本号
			int version = callBackDao.queryCallBackQuesnaireVersion(callBack.getCallBackId());
			//3.2保存问卷与回访关联关系表
			CallBackQuesnaire cbq = new CallBackQuesnaire();
			cbq.setCallBackId(callBack.getCallBackId());
			cbq.setQuesnaireId(pmClQuesnaireResultHeaderId);
			cbq.setQuesnaireState(pmClQuesnaireResultHeader.getStatus());
			cbq.setTaskId(callBack.getTaskId());
			cbq.setQuesnaireVersion(version);
			callBackDao.insertCallBackQuesnaire(cbq);
		}
	}
	@Override
	public CallBackQuesnaire queryCbQuesnaire(int quesnaireId) {
		return callBackDao.queryCbQuesnaire(quesnaireId);
	}
	@Override
	public int queryQuesnaireTemplateId(int quesnaireId) {
		return callBackDao.queryQuesnaireTemplateID(quesnaireId);
	}
	@Override
	public void submitCallBackFlow(WorkflowCommonParam param, CallBack callBack) {
		//1.获取流程变量
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("callbackManager", getLoginName());
		vars.put("result", Integer.parseInt(param.getOutcome()));
		//2.流程走向下一步,因这里涉及到某个角色审批的问题，特殊写好，后续待改进
		Task task = workFlowService.getTaskIdByProcessInstanceId(param.getInstId(), "callbackRole");
		if(task == null){
			task = workFlowService.getTaskIdByProcessInstanceId(param.getInstId(), getLoginName());
		}
		workFlowService.doSelfTask(task, param.getInstId() ,param.getComment(), vars);
		//3.增加自定义的审批意见
		workFlowService.addSelfActComment(param.getObjId(), ActivityMessage.CALLBACK_KEY, task.getId(), param.getInstId(), Integer.parseInt(param.getOutcome()), param.getComment());
		
		// 更新项目闭环流程状态，回访通过或驳回，都回到项目跟踪状态
//		String closeProecssState = String.valueOf(ActivityMessage.COMMENT_REJECT).equals(param.getOutcome()) ? MessageUtil.PROJECT_CLOSE_PROCESS_STATE_15 : MessageUtil.PROJECT_CLOSE_PROCESS_STATE_10;
		this.updateProjectCloseProcessState(callBack.getProjectId(), MessageUtil.PROJECT_CLOSE_PROCESS_STATE_10);
	}
	@Override
	public void updateCallBackApplyState(int callBackId, int applyState) {
		callBackDao.updateCallBackApplyState(callBackId ,applyState);
	}
	@Override
	public List<CallBackComment> queryCallBackComment(int callBackId) {
		return callBackDao.queryCallBackComment(callBackId);
	}
	@Override
	public void reSubmitCallBackFlow(WorkflowCommonParam param,
			CallBack callBack) {
		//1.保存申请表单信息
		callBackDao.updateCallBack(callBack);
		//2.重新提交申请
		//2.1.获取流程变量
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("result", Integer.parseInt(param.getOutcome()));
		//2.2.流程走向下一步
		Task task = workFlowService.getTaskIdByProcessInstanceId(param.getInstId(), getLoginName());
		workFlowService.doSelfTask(task, param.getInstId() ,param.getComment(), vars);
		//2.3.增加自定义的审批意见
		workFlowService.addSelfActComment(param.getObjId(), ActivityMessage.CALLBACK_KEY, task.getId(), param.getInstId(), Integer.parseInt(param.getOutcome()), param.getComment());
		
		// 更新项目闭环流程状态，提交后变回回访状态
		this.updateProjectCloseProcessState(callBack.getProjectId(), MessageUtil.PROJECT_CLOSE_PROCESS_STATE_30);
	}
	
	private void updateProjectCloseProcessState(int projectId, String closeProcessState) {
	    try {
    	    ProjectService projectService = SpringContext.getApplicationContext().getBean("projectService", ProjectService.class);
    	    PmClosedLoopService pmClosedLoopService = SpringContext.getApplicationContext().getBean("pmClosedLoopService", PmClosedLoopService.class);
            
    	    Project project = projectService.queryProjectById(projectId);
    	    String taskId = pmClosedLoopService.queryTaskByBussinessKey(project);
    	    if (StringUtils.isBlank(taskId)) {
        	    Project temp = new Project(projectId);
        	    temp.setCloseProcessState(closeProcessState);
                projectService.insertOrUpdateProjectState(temp);
    	    }
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	}
}
