package com.dp.plat.subcontract.service.impl;

import static com.dp.plat.subcontract.constant.SubcontractConstant.CommentStatus.AGREE;
import static com.dp.plat.subcontract.constant.SubcontractConstant.CommentStatus.APPLY;
import static com.dp.plat.subcontract.constant.SubcontractConstant.CommentStatus.CALLBACK_DISABLE;
import static com.dp.plat.subcontract.constant.SubcontractConstant.CommentStatus.CALLBACK_PASS;
import static com.dp.plat.subcontract.constant.SubcontractConstant.CommentStatus.CALLBACK_REJECT;
import static com.dp.plat.subcontract.constant.SubcontractConstant.CommentStatus.CLOSE_ABLE;
import static com.dp.plat.subcontract.constant.SubcontractConstant.CommentStatus.CLOSE_DISABLE;
import static com.dp.plat.subcontract.constant.SubcontractConstant.CommentStatus.PAYMENT;
import static com.dp.plat.subcontract.constant.SubcontractConstant.CommentStatus.REJECT;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.dp.plat.context.SpringContext;
import com.dp.plat.context.UserContext;
import com.dp.plat.dao.PmClosedLoopDao;
import com.dp.plat.data.bean.Department;
import com.dp.plat.data.bean.PmClQuesnaireResultHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultLine;
import com.dp.plat.data.bean.Project;
import com.dp.plat.data.bean.ShipmentInfo;
import com.dp.plat.data.bean.User;
import com.dp.plat.data.bean.WorkflowCommonParam;
import com.dp.plat.exception.UploadException;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.service.BaseServiceImpl;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.CallBackService;
import com.dp.plat.service.DepartmentManageService;
import com.dp.plat.service.SendMailService;
import com.dp.plat.service.UserManageService;
import com.dp.plat.service.WorkFlowService;
import com.dp.plat.subcontract.constant.SubcontractConstant;
import com.dp.plat.subcontract.constant.SubcontractConstant.CommentStatus;
import com.dp.plat.subcontract.constant.SubcontractConstant.SubcontractStatus;
import com.dp.plat.subcontract.constant.SubcontractConstant.SubcontractTemplate;
import com.dp.plat.subcontract.constant.SubcontractConstant.SubcontractType;
import com.dp.plat.subcontract.constant.SubcontractConstant.TaskKey;
import com.dp.plat.subcontract.dao.SubcontractDao;
import com.dp.plat.subcontract.entity.SubcontractCallback;
import com.dp.plat.subcontract.entity.SubcontractDeliver;
import com.dp.plat.subcontract.entity.SubcontractFacilitator;
import com.dp.plat.subcontract.entity.SubcontractLine;
import com.dp.plat.subcontract.entity.SubcontractPayment;
import com.dp.plat.subcontract.entity.SubcontractPrice;
import com.dp.plat.subcontract.entity.SubcontractProject;
import com.dp.plat.subcontract.exception.SubcontractException;
import com.dp.plat.subcontract.service.SubcontractService;
import com.dp.plat.subcontract.vo.SubcontractComment;
import com.dp.plat.subcontract.vo.SubcontractDeliverVO;
import com.dp.plat.subcontract.vo.SubcontractEvaluationHeader;
import com.dp.plat.subcontract.vo.SubcontractPageParam;
import com.dp.plat.subcontract.vo.SubcontractProjectVO;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.NotificationTemplateUtil;
import com.dp.plat.util.UploadFileUtil;
import com.dp.plat.util.UserUtil;
import com.dp.plat.util.Util;

public class SubcontractServiceImpl extends BaseServiceImpl implements SubcontractService {

	/**
	 * 上传路径 /upload/subcontract/
	 */
//	private static final String uploadDir = File.separator + "upload" + File.separator + "subcontract" + File.separator;
	private static final String uploadDir = File.separator + UploadFileUtil.UPLOAD_PATH + File.separator + "subcontract" + File.separator;

	private SubcontractDao dao;
	private BasicDataService basicDataService;
	private CallBackService callBackService;
	private TaskService taskService;
	private SendMailService sendMailService;
	private UserManageService userManageService;
	private PmClosedLoopDao pmClosedLoopDao;
	private WorkFlowService workFlowService;
	private DepartmentManageService departmentManageService;

	public void setSubcontractDao(SubcontractDao subcontractDao) {
		this.dao = subcontractDao;
	}

	public void setBasicDataService(BasicDataService basicDataService) {
		this.basicDataService = basicDataService;
	}

	public void setCallBackService(CallBackService callBackService) {
		this.callBackService = callBackService;
	}

	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	public void setSendMailService(SendMailService sendMailService) {
		this.sendMailService = sendMailService;
	}

	public SubcontractDao getSubcontractDao() {
		return dao;
	}

	public BasicDataService getBasicDataService() {
		return basicDataService;
	}

	public CallBackService getCallBackService() {
		return callBackService;
	}

	public TaskService getTaskService() {
		return taskService;
	}

	public SendMailService getSendMailService() {
		return sendMailService;
	}

	public UserManageService getUserManageService() {
		return userManageService;
	}

	public void setUserManageService(UserManageService userManageService) {
		this.userManageService = userManageService;
	}

	public PmClosedLoopDao getPmClosedLoopDao() {
		return pmClosedLoopDao;
	}

	public void setPmClosedLoopDao(PmClosedLoopDao pmClosedLoopDao) {
		this.pmClosedLoopDao = pmClosedLoopDao;
	}

	public WorkFlowService getWorkFlowService() {
		return workFlowService;
	}

	public void setWorkFlowService(WorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	public DepartmentManageService getDepartmentManageService() {
		return departmentManageService;
	}

	public void setDepartmentManageService(DepartmentManageService departmentManageService) {
		this.departmentManageService = departmentManageService;
	}

	@Override
	public SubcontractProject selectSubcontractProjectById(Integer subcontractId) {
		return dao.selectSubcontractProjectById(subcontractId);
	}

	@Override
	public SubcontractProjectVO selectSubcontractProjectVOById(Integer subcontractId) {
		return dao.selectSubcontractProjectVOById(subcontractId);
	}

	@Override
	public List<SubcontractProject> selectSubcontractProjectList(SubcontractProject subcontractProject) {
		return dao.selectSubcontractProjectList(subcontractProject);
	}

	@Override
	public List<SubcontractProjectVO> selectSubcontractProjectVOList(SubcontractProject subcontractProject) {
		return dao.selectSubcontractProjectVOList(subcontractProject);
	}

	@Override
	public List<SubcontractProjectVO> selectSubcontractProjectVOListPageable(SubcontractPageParam pageParam) {
		List<SubcontractProjectVO> list = new ArrayList<>();
		try {
			DisplayParam displayParam = pageParam.getDisplayParam();
			if (displayParam == null) {
				displayParam = new DisplayParam();
			}
			displayParam.getParam();

			Integer totalcount = this.countSubcontractProjectVOListPageable(pageParam);
			if (!displayParam.getExport()) {
				displayParam.setPagesize(50);
				displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
			} else {
				displayParam.setPagesize(totalcount);
				displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
			}
			displayParam.setTotalcount(totalcount);
			list = dao.selectSubcontractProjectVOListPageable(pageParam);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * @param pageParam
	 */
	private Integer countSubcontractProjectVOListPageable(SubcontractPageParam pageParam) {
		return dao.countSubcontractProjectVOListPageable(pageParam);
	}

	@Override
	public List<SubcontractFacilitator> selectSubcontractFacilitatorList(
			SubcontractFacilitator subcontractFacilitator) {
		return dao.selectSubcontractFacilitatorList(subcontractFacilitator);
	}

	@Override
	public List<SubcontractPayment> selectSubcontractPaymentList(SubcontractPayment payment) {
		return dao.selectSubcontractPaymentList(payment);
	}

	@Override
	public String querySubcontractPaiedAmount(Integer subcontractId) {
		return dao.querySubcontractPaiedAmmount(subcontractId);
	}

	@Override
	public List<ShipmentInfo> queryShipmentinfoByContractNosAndProjectIds(String contractNos, String projectIds) {
		return queryShipmentinfoByContractNosAndProjectIds(contractNos, projectIds, false);
	}

	@Override
	public List<ShipmentInfo> queryShipmentinfoByContractNosAndProjectIds(String contractNos, String projectIds,
			boolean excludeTransferOut) {
		return dao.queryShipmentinfoByContractNosAndProjectIds(contractNos, projectIds, excludeTransferOut);
	}
	
	@Override
	public List<ShipmentInfo> queryShipmentinfoByContractNosAndProjectIds(Map<String, Object> params) {
		return dao.queryShipmentinfoByContractNosAndProjectIds(params);
	}

	@Override
	public List<Project> queryProjectList(Project project) {
		return dao.queryProjectList(project);
	}

	@Override
	public List<Project> queryProjectList(SubcontractProject subcontract) {
		return dao.queryProjectList(subcontract);
	}

	@Override
	public String checkSubcontractName(String subcontractName) {
		return dao.checkSubcontractName(subcontractName);
	}

	/**
	 * @param subcontract
	 * @return
	 */
	@Override
	public String checkSubcontractName(SubcontractProject subcontract) {
		return dao.checkSubcontractName(subcontract);
	}

	@Override
	@Transactional
	public void insertSubcontractProject(SubcontractProject subcontract) {
		subcontract.setCreateBy(getLoginName());
		subcontract.setCreateTime(new Date());
		dao.insertSubcontractProject(subcontract);
	}

	@Override
	@Transactional
	public void insertSubcontractProjectSelective(SubcontractProject subcontract) {
		subcontract.setCreateBy(getLoginName());
		subcontract.setCreateTime(new Date());
		dao.insertSubcontractProjectSelective(subcontract);
	}

	@Override
	public void insertSubcontractDeliver(SubcontractDeliver deliver) {
		deliver.setUploadBy(getLoginName());
		deliver.setUploadTime(new Date());
		deliver.setEffectiveFrom(deliver.getUploadTime());
		dao.insertSubcontractDeliver(deliver);
	}

	@Override
	public void insertSubcontractPayment(SubcontractPayment subcontractPayment) {
		subcontractPayment.setCreateBy(getLoginName());
		subcontractPayment.setCreateTime(new Date());
		dao.insertSubcontractPayment(subcontractPayment);
	}

	/**
	 * @param subcontractPayment
	 */
	private void updateSubcontractPaymentByIdSelective(SubcontractPayment subcontractPayment) {
		subcontractPayment.setUpdateBy(getLoginName());
		subcontractPayment.setUpdateTime(new Date());
		dao.updateSubcontractPaymentByIdSelective(subcontractPayment);
	}

	@Override
	public void saveSubcontractPayment(List<SubcontractPayment> subcontractPaymentList) {
		saveSubcontractPayment(subcontractPaymentList, null);
	}

	@Override
	@Transactional
	public void saveSubcontractPayment(List<SubcontractPayment> subcontractPaymentList, Integer[] delIds) {
		// 删除原来的付款信息
		if (delIds != null && delIds.length > 0) {
			for (Integer id : delIds) {
				this.deleteSubcontractPaymentById(id);
			}
		}
		if (subcontractPaymentList != null && !subcontractPaymentList.isEmpty()) {
			for (SubcontractPayment subcontractPayment : subcontractPaymentList) {
				if (subcontractPayment.getId() != null) {
					this.updateSubcontractPaymentByIdSelective(subcontractPayment);
				} else {
					this.insertSubcontractPayment(subcontractPayment);
				}
			}
		}
	}

	/**
	 * @param id
	 */
	private void deleteSubcontractPaymentById(Integer id) {
		dao.deleteSubcontractPaymentById(id);
	}

	@Override
	@Transactional
	public void createSubcontractProject(SubcontractProject subcontract, List<SubcontractLine> subcontractLineList,
			File[] uploadFiles, String[] deliverNames, String[] deliverTypes) {
		this.insertSubcontractProject(subcontract);
		Integer subcontractId = subcontract.getId();
		int length = Math.min(uploadFiles.length, deliverTypes.length);
		for (int i = 0; i < length; i++) {
			File file = uploadFiles[i];
			String type = deliverTypes[i];
			String fileName = file.getName();

			SubcontractDeliver deliver = new SubcontractDeliver(subcontractId);
			deliver.setFileName(fileName);
			deliver.setType(type);
			this.insertSubcontractDeliver(deliver);
		}
	}

	@Override
	@Transactional
	public void createSubcontractProject(SubcontractProject subcontract, List<SubcontractLine> subcontractLineList,
			List<SubcontractDeliver> subcontractDeliverList, File[] uploadFiles) {

	}

	@Override
	// @Transactional
	public void createSubcontractProject(SubcontractProject subcontract, List<SubcontractLine> subcontractLineList,
			List<SubcontractDeliverVO> uploadDeliverList) {
		// this.insertSubcontractProject(subcontract);
		this.insertSubcontractProjectSelective(subcontract);
		// saveDeliverFiles(subcontract.getId(), uploadDeliverList);
	}

	@Override
	public void createSubcontractProject(SubcontractProject subcontract, List<SubcontractDeliverVO> uploadDeliverList) {
		createSubcontractProject(subcontract, null, uploadDeliverList);
	}

	@Override
	@Transactional
	public void createSubcontractProject(SubcontractProject subcontract, List<SubcontractDeliverVO> uploadDeliverList,
			String[] selected) {
		createSubcontractProject(subcontract, uploadDeliverList);
		this.batchInsertSubcontractLine(subcontract, selected);
	}

	@Override
	@Transactional
	public void updateSubcontractProject(SubcontractProject subcontract, List<SubcontractDeliverVO> uploadDeliverList) {
		this.updateSubcontractProjectByIdSelective(subcontract);
		// saveDeliverFiles(subcontract.getId(), uploadDeliverList);
	}

	@Override
	@Transactional
	public void updateSubcontractProject(SubcontractProject subcontract, List<SubcontractDeliverVO> uploadDeliverList,
			String[] selected) {
		updateSubcontractProject(subcontract, uploadDeliverList);
		batchInsertSubcontractLine(subcontract, selected);
	}

	@Override
	public void updateSubcontractProjectByIdSelective(SubcontractProject subcontract) {
		subcontract.setUpdateBy(getLoginName());
		subcontract.setUpdateTime(new Date());
		dao.updateSubcontractProjectByIdSelective(subcontract);
	}

	@Override
	public List<SubcontractLine> selectSubcontractLineList(SubcontractLine subcontractLine) {
		return dao.selectSubcontractLineList(subcontractLine);
	}

	@Override
	public SubcontractDeliver selectSubcontractDeliverById(Integer deliverId) {
		return dao.selectSubcontractDeliverById(deliverId);
	}

	@Override
	public List<SubcontractDeliver> selectSubcontractDeliverList(SubcontractDeliver deliver) {
		return dao.selectSubcontractDeliverList(deliver);
	}

	@Override
	public List<SubcontractDeliverVO> selectSubcontractDeliverVOList(SubcontractDeliver deliver) {
		return dao.selectSubcontractDeliverVOList(deliver);
	}

	/**
	 * 保存上传的交付件文件
	 * 
	 * @param subcontractId
	 * @param uploadDeliverList
	 * @return callBackFlag 是否需要发起回访流程
	 */
	@Override
	public boolean saveDeliverFiles(Integer subcontractId, List<SubcontractDeliverVO> uploadDeliverList) {
		boolean callBackFlag = false;
		if (uploadDeliverList != null) {
			String uploadExtWhiteList = basicDataService.querySysArg("sys.upload.ext.whitelist");
			for (SubcontractDeliverVO deliverVO : uploadDeliverList) {
				try {
					if (deliverVO == null) {
						continue;
					}
					String type = deliverVO.getType();
					File[] uploadFiles = deliverVO.getUploads();
					String[] fileNames = deliverVO.getUploadsFileName();
					if (uploadFiles == null) {
						continue;
					}
					for (int i = 0; i < uploadFiles.length; i++) {
						File file = uploadFiles[i];
						String fileName = fileNames[i];
						// 检查文件上传类型
						if (!UploadFileUtil.checkFileExt(fileName, uploadExtWhiteList)) {
							return false;
						}
						SubcontractDeliver deliver = new SubcontractDeliver(subcontractId);
						deliver.setFileName(fileName);
						fileName = UploadFileUtil.uploadNoRepeat(file, uploadDir, fileName);
						deliver.setFilePath(uploadDir + fileName);
						deliver.setType(type);
						this.insertSubcontractDeliver(deliver);
						// // 上传服务单，触发回访流程
						// if ("1".equals(type)) {
						// callBackFlag = true;
						// }
					}
				} catch (UploadException e) {
					throw e;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return callBackFlag;
	}
	
	@Override
	public void deleteSubcontractDeliver(SubcontractDeliverVO subcontractDeliverVO) {
		dao.deleteSubcontractDeliver(subcontractDeliverVO);
	}

	/**
	 * 批量插入或更新转包序列号清单
	 * 
	 * @param subcontractProject
	 * @param barCodes
	 */
	private void batchInsertSubcontractLine(SubcontractProject subcontractProject, String[] barCodes) {
		if (barCodes == null || barCodes.length == 0) {
			return;
		}
		HashMap<String, String> params = new HashMap<>();
		params.put("subcontractId", String.valueOf(subcontractProject.getId()));
		params.put("barCodes", StringUtils.join(barCodes, ","));
		dao.batchDeleteSubcontractLine(params);

		params.put("projectIds", subcontractProject.getProjectIds());
		params.put("contractNos", Util.appendChar(subcontractProject.getContractNos(), "'"));
		// params.put("barCodes", "'" + StringUtils.join(barCodes, "','") +
		// "'");
		params.put("createBy", getLoginName());
		params.put("updateBy", getLoginName());
		dao.batchInsertSubcontractLine(params);
	}

	@Override
	public List<Map<String, Object>> queryContractNoEngineeFee(String contractNos) {
		return dao.queryContractNoEngineeFee(contractNos);
	}

	@Override
	public List<SubcontractPrice> queryContractNoEngineeFeeWithSubPrice(String contractNos, Integer subcontractId) {
		return dao.queryContractNoEngineeFeeWithSubPrice(contractNos, subcontractId);
	}

	@Override
	@Transactional
	public String startSubcontractFlow(WorkflowCommonParam workflowCommonParam,
			SubcontractEvaluationHeader pmClEvaluationHeader, SubcontractProject subcontract) {
		log("服务经理发起项目转包申请");
		String processKey = SubcontractConstant.PROCESS_SUBCONTRACT_KEY;

		// 判断是否已经有正在进行的转包申请流程
		List<Task> subcontractTaskList = taskService.createTaskQuery().processDefinitionKey(processKey)
				.processVariableValueEquals("subcontractId", subcontract.getId()).active().list();

		// 转包申请流程正在进行，本次不再发起！
		if (!subcontractTaskList.isEmpty()) {
			throw new SubcontractException("项目转包申请流程正在进行，本次不再重复发起！");
		}

		// 将项目转包状态更改为“待审批”
		SubcontractProject temp = new SubcontractProject();
		temp.setId(subcontract.getId());
		temp.setState(SubcontractStatus.APPLY);
		this.updateSubcontractProjectByIdSelective(temp);

		String nowUser = UserContext.getUserContext().getUser().getUsername();
		String[] nextAssign = getNextAssignPer(MessageUtil.ROLE_ENGINEEMANAGER_LEADER);

		// 添加审批流程记录
		Integer returnId = workFlowService.addSelfActComment(subcontract.getId(), processKey, TaskKey.START_SUBCONTRACT, null, null, APPLY,
				subcontract.getReason(), nextAssign[0], nextAssign[1]);

		// 增加流程变量
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put(SubcontractConstant.TASK_USER_SERVICE, nowUser);
		vars.put("subcontractId", subcontract.getId());

		vars.put("objId", returnId);

		// 启动流程
		String businessKey = processKey + "." + subcontract.getId() + "." + returnId;
		ProcessInstance processInstance = workFlowService.startProcess(processKey, businessKey, vars);
		String procInstId = processInstance.getId();
		// 办理任务
		Task task = workFlowService.getTaskIdByProcessInstanceId(procInstId, nowUser);
		if (task == null) {
			throw new SubcontractException("发起项目转包申请失败！");
		}

		vars.clear();
		vars.put(SubcontractConstant.TASK_USER_ENGINEEMANAGER_LEADER, nextAssign[0]);
		workFlowService.doSelfTask(task, procInstId, "发起项目转包申请", vars);

		// 6.增加自定义的审批意见
		// workFlowService.addSelfActComment(subcontract.getId(), processKey,
		// task.getId(), procInstId, APPLY,
		// subcontract.getReason(), nextAssign[0], nextAssign[1]);
		workFlowService.updateSelfActComment(returnId, task.getId(), procInstId);

		// mailPerson(subcontract, processStatus, pmClEvaluationHeader,
		// PmClosedLoopConstant.CL_EVALU_TYPE_PM, nowUser);

		return workflowCommonParam.getTaskId();
	}

	@Override
	@Transactional
	public String startSubcontractFlow(SubcontractProject subcontract) {
		log("服务经理发起项目转包申请");
		String processKey = SubcontractConstant.PROCESS_SUBCONTRACT_KEY;

		// 判断是否已经有正在进行的转包申请流程
		List<Task> subcontractTaskList = taskService.createTaskQuery().processDefinitionKey(processKey)
				.processVariableValueEquals("subcontractId", subcontract.getId()).active().list();

		// 转包申请流程正在进行，本次不再发起！
		if (!subcontractTaskList.isEmpty()) {
			throw new SubcontractException("项目转包申请流程正在进行，本次不再重复发起！");
		}

		// 将项目转包状态更改为“待审批”
		SubcontractProjectVO tempSubcontract = new SubcontractProjectVO();
		tempSubcontract.setId(subcontract.getId());
		tempSubcontract.setState(SubcontractStatus.APPLY);
		this.updateSubcontractProjectByIdSelective(tempSubcontract);

		tempSubcontract = dao.selectSubcontractProjectVOById(subcontract.getId());

		String nowUser = UserContext.getUserContext().getUser().getUsername();
		String nextAssignName = tempSubcontract.getProfitDepName() + "服务经理";
		String[] nextAssign = new String[] { nextAssignName, nextAssignName };

		// 添加审批流程记录
		Integer commentId = workFlowService.addSelfActComment(subcontract.getId(), processKey, TaskKey.START_SUBCONTRACT, null, null, APPLY,
				subcontract.getReason(), nextAssign[0], nextAssign[1]);

		// 增加流程变量
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put(SubcontractConstant.TASK_USER_SERVICE, nowUser);
		vars.put("subcontractId", subcontract.getId());
		vars.put("objId", commentId);

		// 启动流程
		String businessKey = processKey + "." + subcontract.getId() + "." + commentId;
		ProcessInstance processInstance = workFlowService.startProcess(processKey, businessKey, vars);
		String procInstId = processInstance.getId();

		// 办理任务
		Task task = workFlowService.getTaskIdByProcessInstanceId(procInstId, nowUser);
		if (task == null) {
			throw new SubcontractException("发起项目转包申请失败！");
		}

		vars.clear();
		vars.put(SubcontractConstant.TASK_PROFIT_SERVICEMANAGER, "profitSmRole");
		vars.put("profitSmRole", tempSubcontract.getProfitDepCode2Office() + "smRole");
		vars.put("dpNo", tempSubcontract.getProfitDepCode2Office());
		workFlowService.doSelfTask(task, procInstId, "发起项目转包申请", vars);

		// 6.增加自定义的审批意见
		// workFlowService.addSelfActComment(subcontract.getId(), processKey,
		// task.getId(), procInstId, APPLY,
		// subcontract.getReason(), nextAssign[0], nextAssign[1]);
		workFlowService.updateSelfActComment(commentId, task.getId(), procInstId);

		// 如果受益部门相同，则自动办理任务
		if (tempSubcontract.getOfficeCode().equals(tempSubcontract.getProfitDepCode2Office())) {
			HashMap<String, Object> params = new HashMap<>();
			params.put("checkProfitDep", "true");
			List<Task> taskList = querySubcontractTaskList(TaskKey.PROFIT_SERVICE_APPROVE, "profitSmRole",
					subcontract.getId(), params);
			Task nextTask = null;
			if (taskList != null && !taskList.isEmpty()) {
				nextTask = taskList.get(0);
				WorkflowCommonParam taskParam = new WorkflowCommonParam();
				taskParam.setTaskId(nextTask.getId());
				taskParam.setApproveStatus(AGREE);
				taskParam.setComment("受益部门相同，自动审批通过");
				this.profitSerivceManagerFlow(taskParam, tempSubcontract);
			}
		} else {

			// 7.增加通知下一步审批人邮件
//			String tos = userManageService.queryMailsByRoleAndOfficeCodes(tempSubcontract.getProfitDepCode(),
//					MessageUtil.ROLE_SERVICEMANAGER);
			String[] nextAssignPer = getNextAssignPer(MessageUtil.ROLE_SERVICEMANAGER, tempSubcontract.getProfitDepCode2Office());
			String tos = nextAssignPer[2];
			if (StringUtils.isNotBlank(tos)) {
				Map<String, Object> mailMap = new HashMap<String, Object>();
				mailMap.put("tos", tos);
				mailMap.put("templateCode", SubcontractTemplate.APPROVE_NOTIFY_CODE);
				mailMap.put("username", nextAssignPer[1]);
				mailMap.put("subcontractName", subcontract.getSubcontractName());
				mailMap.put("taskName", "审批");
				NotificationTemplateUtil.keepMail(mailMap);
			}
		}
		return procInstId;
	}

	@Override
	@Transactional
	public String profitSerivceManagerFlow(WorkflowCommonParam taskParam, SubcontractProject subcontract) {
		log("收益部门服务经理审批项目转包申请");
		// 更改项目转包状态
		SubcontractProject tempSubcontract = new SubcontractProject();
		tempSubcontract.setId(subcontract.getId());
		SubcontractProjectVO subcontractVO = dao.selectSubcontractProjectVOById(subcontract.getId());

		String candidateRole = "";
		String[] nextAssign = new String[] { "", "", "" };
		// 流程变量
		Map<String, Object> vars = new HashMap<String, Object>();
		if (taskParam.getApproveStatus() == AGREE) {
			tempSubcontract.setState(SubcontractStatus.PROFIT_SM_AGREE);// 收益部门服务经理审批通过
			
			// 特殊部门由办事处主任审批
			String officeCodes = StringUtils.trimToEmpty(basicDataService.querySysArg(SubcontractTemplate.AREA_LEADER_AUDIT_ENGINEE_FEE_OFFICES));
			if (officeCodes.contains(subcontractVO.getProfitDepCode2Office())) {
//				Map<String, String> params = new HashMap<>();
//				params.put("roleid", MessageUtil.ROLE_AREA_LEADER + "");
//				params.put("areaPower", subcontract.getProfitDepCode());
//				List<User> userList = userManageService.queryUserWithRoleIdAndDpNoOrInAreaPower(params);
//				if (userList.isEmpty()) {
//					params.put("areaPower", subcontract.getProfitDepCode2Office());
//					List<User> tempList = userManageService.queryUserWithRoleIdAndDpNoOrInAreaPower(params);
//					userList.addAll(tempList);
//				}
//				if (userList.isEmpty()) {
//					throw new SubcontractException("没有找到办事处主任！");
//				}
//				User nextPerson = userList.get(0);
//				nextAssign[0] = nextPerson.getUsername();
//				nextAssign[1] = nextAssign[0] + "-" + nextPerson.getRealName();
//				nextAssign[2] = nextPerson.getEmail();
				try {
					nextAssign = getNextAssignPer(MessageUtil.ROLE_AREA_LEADER, subcontractVO.getProfitDepCode());
					if ("1".compareTo(nextAssign[3]) < 0) {
						nextAssign[0] = subcontractVO.getProfitDepName() + "主任";
						nextAssign[1] = subcontractVO.getProfitDepName() + "主任";
						candidateRole = String.valueOf(MessageUtil.ROLE_AREA_LEADER);
					}
					vars.put("isAreaLeaderAudit", true);
				} catch(SubcontractException e) {
					throw new SubcontractException("没有找到办事处主任！");
				}
			} else {
				nextAssign = getNextAssignPer(MessageUtil.ROLE_ENGINEEMANAGER_LEADER);
				if ("1".compareTo(nextAssign[3]) < 0) {
					nextAssign[0] = "工程管理部主管";
					nextAssign[1] = "工程管理部主管";
					candidateRole = String.valueOf(MessageUtil.ROLE_ENGINEEMANAGER_LEADER);
				}
			}
		} else if (taskParam.getApproveStatus() == REJECT) {
			tempSubcontract.setState(SubcontractStatus.PROFIT_SM_REJECT);// 收益部门服务经理审批驳回

			nextAssign[0] = "审批驳回";
			nextAssign[1] = "审批驳回";
		} else {
			throw new SubcontractException("请选择审批意见！");
		}

		dao.updateSubcontractProjectByIdSelective(tempSubcontract);

		String nowUser = UserContext.getUserContext().getUser().getUsername();
		String processKey = SubcontractConstant.PROCESS_SUBCONTRACT_KEY;

		// 办理任务
		HashMap<String, Object> params = new HashMap<>();
		params.put("checkProfitDep", "true");
		List<Task> taskList = querySubcontractTaskList(TaskKey.PROFIT_SERVICE_APPROVE, "profitSmRole",
				subcontractVO.getId(), params);
		Task task = null;
		if (taskList != null && !taskList.isEmpty()) {
			task = taskList.get(0);
			taskParam.setTaskId(task.getId());
		} else {
			throw new SubcontractException("没有【" + nowUser + "】的待办任务！任务办理失败！");
		}

		// 增加流程变量
//		Map<String, Object> vars = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(candidateRole)) {
			vars.put(SubcontractConstant.TASK_USER_ENGINEEMANAGER_LEADER, "role_" + candidateRole);
		} else {
			vars.put(SubcontractConstant.TASK_USER_ENGINEEMANAGER_LEADER, nextAssign[0]);
		}
		vars.put("emlRole", candidateRole);
		vars.put("dpNo", subcontractVO.getProfitDepCode2Office());
		vars.put("result", taskParam.getApproveStatus());
		this.submitSelfTask(taskParam, vars);

		// 3.增加自定义的审批意见
		workFlowService.addSelfActComment(subcontractVO.getId(), processKey, task.getTaskDefinitionKey(), task.getId(),
				task.getProcessInstanceId(), taskParam.getApproveStatus(), taskParam.getComment(), nextAssign[0],
				nextAssign[1]);

		// 增加通知下一步审批人邮件
		Map<String, Object> mailMap = new HashMap<String, Object>();
		if (taskParam.getApproveStatus() == AGREE) {
//			String tos = userManageService.queryMailsByRoleAndOfficeCodes("", MessageUtil.ROLE_ENGINEEMANAGER_LEADER);
			String tos = nextAssign[2];
			if (StringUtils.isNotBlank(tos)) {
				mailMap.put("tos", tos);
				mailMap.put("templateCode", SubcontractTemplate.APPROVE_NOTIFY_CODE);
				mailMap.put("username", nextAssign[1]);
				mailMap.put("subcontractName", subcontractVO.getSubcontractName());
				mailMap.put("taskName", "审批");
			}
		} else {
			// String tos =
			// userManageService.queryMailsByRoleAndOfficeCodes(subcontract.getOfficeCode(),
			// MessageUtil.ROLE_SERVICEMANAGER);
			String[] nextAssignPer = getNextAssignPer(MessageUtil.ROLE_SERVICEMANAGER, subcontractVO.getOfficeCode());
			String tos = nextAssignPer[2];
			if (StringUtils.isNotBlank(tos)) {
				mailMap.put("tos", tos);
				mailMap.put("ccs", basicDataService.querySysArg(SubcontractTemplate.SUBCONTRACT_CCS_MAIL));
				mailMap.put("templateCode", SubcontractTemplate.APPROVE_REJECT_NOTIFY_CODE);
				// mailMap.put("username", ((SubcontractProjectVO)
				// subcontract).getOfficeName() + "服务经理");
				mailMap.put("username", nextAssignPer[1]);
				mailMap.put("subcontractName", subcontractVO.getSubcontractName());
				mailMap.put("taskAssignee", nowUser + "-" + UserContext.getUserContext().getUser().getRealName());
				mailMap.put("comment", taskParam.getComment());
			}
		}
		NotificationTemplateUtil.keepMail(mailMap);
		return taskParam.getTaskId();
	}

	/**
	 * {@link Deprecated} SubcontractEvaluationHeader 无用，弃用该方法
	 */
	@Override
	@Transactional
	@Deprecated
	public String auditSubcontractFlow(WorkflowCommonParam taskParam, SubcontractEvaluationHeader pmClEvaluationHeader,
			SubcontractProject subcontract) {
		log("工程管理部主管审批项目转包申请");
		// 更改项目转包状态
		SubcontractProject tempSubcontract = new SubcontractProject();
		tempSubcontract.setId(subcontract.getId());

		String subcontractAmount = subcontract.getSubcontractAmount();
		subcontract = dao.selectSubcontractProjectById(subcontract.getId());

		String[] nextAssign = new String[] { "", "" };
		if (taskParam.getApproveStatus() == AGREE) {
			tempSubcontract.setSubcontractAmount(subcontractAmount);
			tempSubcontract.setState(SubcontractStatus.ENG_AGREE);// 工程管理部审批通过
			Map<String, String> params = new HashMap<>();
			params.put("roleid", MessageUtil.ROLE_AREA_LEADER + "");
			params.put("areaPower", subcontract.getProfitDepCode());
			List<User> userList = userManageService.queryUserWithRoleIdAndDpNoOrInAreaPower(params);
			if (userList.isEmpty()) {
				params.put("areaPower", subcontract.getProfitDepCode2Office());
				List<User> tempList = userManageService.queryUserWithRoleIdAndDpNoOrInAreaPower(params);
				userList.addAll(tempList);
			}
//			params.put("areaPower", subcontract.getProfitDepCode2Office());
//			List<User> userList = userManageService.queryUserWithRoleIdAndDpNoOrInAreaPower(params);
			if (userList.isEmpty()) {
				throw new SubcontractException("没有找到办事处主任！");
			}
			User nextPerson = userList.get(0);
			nextAssign[0] = nextPerson.getUsername();
			nextAssign[1] = nextAssign[0] + "-" + nextPerson.getRealName();
		} else if (taskParam.getApproveStatus() == REJECT) {
			tempSubcontract.setState(SubcontractStatus.ENG_REJECT);// 工程管理部审批驳回

			nextAssign[0] = "审批驳回";
			nextAssign[1] = "审批驳回";
		} else {
			throw new SubcontractException("请选择审批意见！");
		}

		dao.updateSubcontractProjectByIdSelective(tempSubcontract);

		String nowUser = UserContext.getUserContext().getUser().getUsername();
		String processKey = SubcontractConstant.PROCESS_SUBCONTRACT_KEY;

		// 办理任务
		// Task task =
		// taskService.createTaskQuery().taskId(taskParam.getTaskId()).processInstanceId(taskParam.getInstId()).taskAssignee(nowUser).active().singleResult();
		List<Task> taskList = querySubcontractTaskList(SubcontractConstant.TASK_KEY_APPROVE, taskParam.getTaskId());
		Task task = null;
		if (taskList != null && !taskList.isEmpty()) {
			task = taskList.get(0);
			taskParam.setTaskId(task.getId());
		} else {
			throw new SubcontractException("没有【" + nowUser + "】的待办任务！任务办理失败！");
		}

		// 增加流程变量
		Map<String, Object> vars = new HashMap<String, Object>();
		// vars.put(SubcontractConstant.TASK_USER_ENGINEEMANAGER_EMP, "emRole");
		// vars.put("emRole", MessageUtil.ROLE_ENGINEEMANAGER + "");

		vars.put(SubcontractConstant.TASK_USER_AREA_LEADER, nextAssign[0]);
		vars.put("result", taskParam.getApproveStatus());
		this.submitSelfTask(taskParam, vars);

		// 3.增加自定义的审批意见
		workFlowService.addSelfActComment(subcontract.getId(), processKey, task.getTaskDefinitionKey(), task.getId(),
				task.getProcessInstanceId(), taskParam.getApproveStatus(), taskParam.getComment(), nextAssign[0],
				nextAssign[1]);
		return "";
	}

	@Override
	@Transactional
	public String auditSubcontractFlow(WorkflowCommonParam taskParam, SubcontractProject subcontract,
			List<SubcontractPrice> subcontractPriceList) {
		log("工程管理部主管审批项目转包申请");
		// 更改项目转包状态
		SubcontractProject tempSubcontract = new SubcontractProject();
		tempSubcontract.setId(subcontract.getId());

		String subcontractAmount = subcontract.getSubcontractAmount();
		SubcontractProjectVO subcontractVO = dao.selectSubcontractProjectVOById(subcontract.getId());

		String[] nextAssign = new String[] { "", "", "" };
		String candidateRole = "";
		if (taskParam.getApproveStatus() == AGREE) {
			if (subcontractPriceList != null) {
				for (SubcontractPrice price : subcontractPriceList) {
					price.setSubcontractId(subcontractVO.getId());
					if (price.getId() == null) {
						this.insertSubcontractPrice(price);
					} else {
						this.updateSubcontractPriceByIdSelective(price);
					}
				}
			}
			tempSubcontract.setSubcontractAmount(subcontractAmount);
			tempSubcontract.setState(SubcontractStatus.ENG_AGREE);// 工程管理部审批通过

//			Map<String, String> params = new HashMap<>();
//			params.put("roleid", MessageUtil.ROLE_AREA_LEADER + "");
//			params.put("areaPower", subcontract.getProfitDepCode());
//			List<User> userList = userManageService.queryUserWithRoleIdAndDpNoOrInAreaPower(params);
//			if (userList.isEmpty()) {
//				params.put("areaPower", subcontract.getProfitDepCode2Office());
//				List<User> tempList = userManageService.queryUserWithRoleIdAndDpNoOrInAreaPower(params);
//				userList.addAll(tempList);
//			}
////			params.put("areaPower", subcontract.getProfitDepCode2Office());
////			List<User> userList = userManageService.queryUserWithRoleIdAndDpNoOrInAreaPower(params);
//			if (userList.isEmpty()) {
//				throw new SubcontractException("没有找到办事处主任！");
//			}
//			User nextPerson = userList.get(0);
//			nextAssign[0] = nextPerson.getUsername();
//			nextAssign[1] = nextAssign[0] + "-" + nextPerson.getRealName();
//			nextAssign[2] = nextPerson.getEmail();
			try {
				nextAssign = getNextAssignPer(MessageUtil.ROLE_AREA_LEADER, subcontractVO.getProfitDepCode());
				if ("1".compareTo(nextAssign[3]) < 0) {
					nextAssign[0] = subcontractVO.getProfitDepName() + "主任";
					nextAssign[1] = subcontractVO.getProfitDepName() + "主任";
					candidateRole = String.valueOf(MessageUtil.ROLE_AREA_LEADER);
				}
			} catch(SubcontractException e) {
				throw new SubcontractException("没有找到办事处主任！");
			}
		} else if (taskParam.getApproveStatus() == REJECT) {
			tempSubcontract.setState(SubcontractStatus.ENG_REJECT);// 工程管理部审批驳回

			nextAssign[0] = "审批驳回";
			nextAssign[1] = "审批驳回";
		} else {
			throw new SubcontractException("请选择审批意见！");
		}

		dao.updateSubcontractProjectByIdSelective(tempSubcontract);

		String nowUser = UserContext.getUserContext().getUser().getUsername();
		String processKey = SubcontractConstant.PROCESS_SUBCONTRACT_KEY;

		// 办理任务
		List<Task> taskList = querySubcontractTaskList(SubcontractConstant.TASK_KEY_APPROVE, taskParam.getTaskId());
		Task task = null;
		if (taskList != null && !taskList.isEmpty()) {
			task = taskList.get(0);
			taskParam.setTaskId(task.getId());
		} else {
			throw new SubcontractException("没有【" + nowUser + "】的待办任务！任务办理失败！");
		}

		Map<String, Object> taskVariables = taskService.getVariables(task.getId());
		// 增加流程变量
		Map<String, Object> vars = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(candidateRole)) {
			vars.put(SubcontractConstant.TASK_USER_AREA_LEADER, "zrRole");
			vars.put("zrRole", MessageUtil.ROLE_AREA_LEADER + "");
		} else {
			vars.put(SubcontractConstant.TASK_USER_AREA_LEADER, nextAssign[0]);
			vars.put("zrRole", "");
		}
		vars.put("dpNo", subcontractVO.getProfitDepCode2Office());
		vars.put("result", taskParam.getApproveStatus());
		this.submitSelfTask(taskParam, vars);

		// 3.增加自定义的审批意见
		workFlowService.addSelfActComment(subcontractVO.getId(), processKey, task.getTaskDefinitionKey(), task.getId(),
				task.getProcessInstanceId(), taskParam.getApproveStatus(), taskParam.getComment(), nextAssign[0],
				nextAssign[1]);

		// 增加通知下一步审批人邮件
		Map<String, Object> mailMap = new HashMap<String, Object>();
		if (taskParam.getApproveStatus() == AGREE) {
			// 如果下级审批人与当前审批人一致
			if (Boolean.TRUE.equals(taskVariables.get("isAreaLeaderAudit"))) {
				HashMap<String, Object> params = new HashMap<>();
				params.put("checkProfitDep", "true");
				List<Task> nextTaskList = querySubcontractTaskList(TaskKey.ZR_APPROVE, "zrRole", subcontractVO.getId(), params);
//				List<Task> nextTaskList = querySubcontractTaskList(TaskKey.ZR_APPROVE, subcontract.getId());
				Task nextTask = null;
				if (nextTaskList != null && !nextTaskList.isEmpty()) {
					nextTask = nextTaskList.get(0);
					WorkflowCommonParam nextTaskParam = new WorkflowCommonParam();
					nextTaskParam.setTaskId(nextTask.getId());
					nextTaskParam.setApproveStatus(AGREE);
					nextTaskParam.setComment("办理人相同，自动审批通过");
					this.approveSubcontractFlow(nextTaskParam, tempSubcontract);
				}
			} else {
				String tos = nextAssign[2];
				if (StringUtils.isNotBlank(tos)) {
					mailMap.put("tos", tos);
					mailMap.put("templateCode", SubcontractTemplate.APPROVE_NOTIFY_CODE);
					mailMap.put("username", nextAssign[1]);
					mailMap.put("subcontractName", subcontractVO.getSubcontractName());
					mailMap.put("taskName", "审批");
				}
			}
		} else {
			// String tos =
			// userManageService.queryMailsByRoleAndOfficeCodes(subcontract.getOfficeCode(),
			// MessageUtil.ROLE_SERVICEMANAGER);
			String[] nextAssignPer = getNextAssignPer(MessageUtil.ROLE_SERVICEMANAGER, subcontractVO.getOfficeCode());
			String tos = nextAssignPer[2];
			if (StringUtils.isNotBlank(tos)) {
				mailMap.put("tos", tos);
				mailMap.put("ccs", basicDataService.querySysArg(SubcontractTemplate.SUBCONTRACT_CCS_MAIL));
				mailMap.put("templateCode", SubcontractTemplate.APPROVE_REJECT_NOTIFY_CODE);
				// mailMap.put("username", ((SubcontractProjectVO)
				// subcontract).getOfficeName() + "服务经理");
				mailMap.put("username", nextAssignPer[1]);
				mailMap.put("subcontractName", subcontractVO.getSubcontractName());
				mailMap.put("taskAssignee", nowUser + "-" + UserContext.getUserContext().getUser().getRealName());
				mailMap.put("comment", taskParam.getComment());
			}
		}
		NotificationTemplateUtil.keepMail(mailMap);
		return "";
	}

	@Override
	@Transactional
	@Deprecated
	public String auditSubcontractFlow(SubcontractComment taskParam, SubcontractProject subcontract) {
		log("工程管理部主管审批项目转包申请");
		// 更改项目转包状态
		SubcontractProject tempSubcontract = new SubcontractProject();
		tempSubcontract.setId(subcontract.getId());

		String subcontractAmount = subcontract.getSubcontractAmount();
		subcontract = dao.selectSubcontractProjectById(subcontract.getId());

		String[] nextAssign = new String[] { "", "" };
		if (taskParam.getResult() == AGREE) {
			tempSubcontract.setSubcontractAmount(subcontractAmount);
			tempSubcontract.setState(SubcontractStatus.ENG_AGREE);// 工程管理部审批通过

			Map<String, String> params = new HashMap<>();
			params.put("roleid", MessageUtil.ROLE_AREA_LEADER + "");
			params.put("areaPower", subcontract.getProfitDepCode());
			List<User> userList = userManageService.queryUserWithRoleIdAndDpNoOrInAreaPower(params);
			if (userList.isEmpty()) {
				params.put("areaPower", subcontract.getProfitDepCode2Office());
				List<User> tempList = userManageService.queryUserWithRoleIdAndDpNoOrInAreaPower(params);
				userList.addAll(tempList);
			}
			if (userList.isEmpty()) {
				throw new SubcontractException("没有找到办事处主任！");
			}
			User nextPerson = userList.get(0);
			nextAssign[0] = nextPerson.getUsername();
			nextAssign[1] = nextAssign[0] + "-" + nextPerson.getRealName();
		} else if (taskParam.getResult() == REJECT) {
			tempSubcontract.setState(SubcontractStatus.ENG_REJECT);// 工程管理部审批驳回

			nextAssign[0] = "审批驳回";
			nextAssign[1] = "审批驳回";
		} else {
			throw new SubcontractException("请选择审批意见！");
		}

		dao.updateSubcontractProjectByIdSelective(tempSubcontract);

		String nowUser = UserContext.getUserContext().getUser().getUsername();
		String processKey = SubcontractConstant.PROCESS_SUBCONTRACT_KEY;

		List<Task> taskList = querySubcontractTaskList(SubcontractConstant.TASK_KEY_APPROVE, taskParam.getTaskId());
		Task task = null;
		if (taskList != null && !taskList.isEmpty()) {
			task = taskList.get(0);
			taskParam.setTaskId(task.getId());
		} else {
			throw new SubcontractException("没有【" + nowUser + "】的待办任务！任务办理失败！");
		}

		// 1.获取流程变量
		Map<String, Object> vars = new HashMap<String, Object>();
		// vars.put(SubcontractConstant.TASK_USER_ENGINEEMANAGER_EMP, "emRole");
		// vars.put("emRole", MessageUtil.ROLE_ENGINEEMANAGER + "");

		vars.put(SubcontractConstant.TASK_USER_AREA_LEADER, nextAssign[0]);
		vars.put("result", taskParam.getApproveStatus());
		this.submitSelfTask(taskParam, vars);

		// 3.增加自定义的审批意见
		workFlowService.addSelfActComment(subcontract.getId(), processKey, task.getTaskDefinitionKey(), task.getId(),
				task.getProcessInstanceId(), taskParam.getApproveStatus(), taskParam.getComment(), nextAssign[0],
				nextAssign[1]);

		// 增加通知下一步审批人邮件
		Map<String, Object> mailMap = new HashMap<String, Object>();
		if (taskParam.getApproveStatus() == AGREE) {
			String tos = nextAssign[2];
			if (StringUtils.isNotBlank(tos)) {
				mailMap.put("tos", tos);
				mailMap.put("templateCode", SubcontractTemplate.APPROVE_NOTIFY_CODE);
				mailMap.put("username", nextAssign[1]);
				mailMap.put("subcontractName", subcontract.getSubcontractName());
				mailMap.put("taskName", "审批");
			}
		} else {
			// String tos =
			// userManageService.queryMailsByRoleAndOfficeCodes(subcontract.getOfficeCode(),
			// MessageUtil.ROLE_SERVICEMANAGER);
			String[] nextAssignPer = getNextAssignPer(MessageUtil.ROLE_SERVICEMANAGER, subcontract.getOfficeCode());
			String tos = nextAssignPer[2];
			if (StringUtils.isNotBlank(tos)) {
				mailMap.put("tos", tos);
				mailMap.put("ccs", basicDataService.querySysArg(SubcontractTemplate.SUBCONTRACT_CCS_MAIL));
				mailMap.put("templateCode", SubcontractTemplate.APPROVE_REJECT_NOTIFY_CODE);
				// mailMap.put("username", ((SubcontractProjectVO)
				// subcontract).getOfficeName() + "服务经理");
				mailMap.put("username", nextAssignPer[1]);
				mailMap.put("subcontractName", subcontract.getSubcontractName());
				mailMap.put("taskAssignee", nowUser + "-" + UserContext.getUserContext().getUser().getRealName());
				mailMap.put("comment", taskParam.getComment());
			}
		}
		NotificationTemplateUtil.keepMail(mailMap);
		return null;
	}

	@Override
	@Transactional
	public String approveSubcontractFlow(WorkflowCommonParam taskParam, SubcontractProject subcontract) {
		log("办事处主任审批项目转包申请");
		// 更改项目转包状态
		SubcontractProject tempSubcontract = new SubcontractProject();
		tempSubcontract.setId(subcontract.getId());
		String[] nextAssign = new String[] { "", "" };
		if (taskParam.getApproveStatus() == AGREE) {
			// 更新主任审批时间
			tempSubcontract.setZrApproveTime(new Date());
			tempSubcontract.setState(SubcontractStatus.AREA_AGREE);// 主任审批通过
			nextAssign[0] = "工程人员";
			nextAssign[1] = "工程人员";
		} else if (taskParam.getApproveStatus() == REJECT) {
			tempSubcontract.setState(SubcontractStatus.AREA_REJECT);// 审批驳回
			nextAssign[0] = "审批驳回";
			nextAssign[1] = "审批驳回";
		} else {
			throw new SubcontractException("请选择审批意见！");
		}

		dao.updateSubcontractProjectByIdSelective(tempSubcontract);

		String nowUser = UserContext.getUserContext().getUser().getUsername();
		String processKey = SubcontractConstant.PROCESS_SUBCONTRACT_KEY;
		// subcontract = dao.selectSubcontractProjectById(subcontract.getId());
		subcontract = dao.selectSubcontractProjectVOById(subcontract.getId());

		// 办理任务
		// Task task =
		// taskService.createTaskQuery().taskId(taskParam.getTaskId()).processInstanceId(taskParam.getInstId()).taskAssignee(nowUser).active().singleResult();
		List<Task> taskList = querySubcontractTaskList(SubcontractConstant.TASK_KEY_ZR_APPROVE, taskParam.getTaskId());
		if (null == taskList || taskList.isEmpty()) {
			HashMap<String, Object> params = new HashMap<>();
			params.put("checkProfitDep", "true");
			taskList = querySubcontractTaskList(TaskKey.ZR_APPROVE, "zrRole", subcontract.getId(), params);
		}
		Task task = null;
		if (taskList != null && !taskList.isEmpty()) {
			task = taskList.get(0);
			taskParam.setTaskId(task.getId());
		} else {
			throw new SubcontractException("没有【" + nowUser + "】的待办任务！任务办理失败！");
		}

		// 增加流程变量
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put(SubcontractConstant.TASK_USER_ENGINEEMANAGER_EMP, "emRole");
		vars.put("emRole", MessageUtil.ROLE_ENGINEEMANAGER + "");
		vars.put("dpNo", subcontract.getProfitDepCode2Office());
		vars.put("result", taskParam.getApproveStatus());
		this.submitSelfTask(taskParam, vars);

		// 3.增加自定义的审批意见
		// workFlowService.addSelfActComment(subcontract.getId(), processKey,
		// task.getId(), task.getProcessInstanceId(),
		// taskParam.getApproveStatus(), taskParam.getComment());
		workFlowService.addSelfActComment(subcontract.getId(), processKey, task.getTaskDefinitionKey(), task.getId(),
				task.getProcessInstanceId(), taskParam.getApproveStatus(), taskParam.getComment(), nextAssign[0],
				nextAssign[1]);

		// 审批通过通知服务商
		if (taskParam.getApproveStatus() == AGREE) {
			notifyFacilitator(subcontract);
		}
		Map<String, Object> mailMap = new HashMap<String, Object>();
		// String tos =
		// userManageService.queryMailsByRoleAndOfficeCodes(subcontract.getOfficeCode(),
		// MessageUtil.ROLE_SERVICEMANAGER);
		String[] nextAssignPer = getNextAssignPer(MessageUtil.ROLE_SERVICEMANAGER, subcontract.getOfficeCode());
		String tos = nextAssignPer[2];
		if (StringUtils.isNotBlank(tos)) {
			mailMap.put("tos", tos);
			mailMap.put("ccs", basicDataService.querySysArg(SubcontractTemplate.SUBCONTRACT_CCS_MAIL));
			if (taskParam.getApproveStatus() == AGREE) {
				mailMap.put("templateCode", SubcontractTemplate.APPROVE_PASS_NOTIFY_CODE);
			} else {
				mailMap.put("templateCode", SubcontractTemplate.APPROVE_REJECT_NOTIFY_CODE);
			}
			// mailMap.put("username", ((SubcontractProjectVO)
			// subcontract).getOfficeName() + "服务经理");
			mailMap.put("username", nextAssignPer[1]);
			mailMap.put("subcontractName", subcontract.getSubcontractName());
			mailMap.put("taskAssignee", nowUser + "-" + UserContext.getUserContext().getUser().getRealName());
			mailMap.put("comment", taskParam.getComment());
		}
		NotificationTemplateUtil.keepMail(mailMap);
		return "";
	}

	@Override
	@Transactional
	public String closeSubcontractFlow(WorkflowCommonParam taskParam, SubcontractEvaluationHeader pmClEvaluationHeader,
			SubcontractProject subcontract) {
		log("项目转包闭环");
		// 更改项目转包状态
		SubcontractProject tempSubcontract = new SubcontractProject();
		tempSubcontract.setId(subcontract.getId());
		String[] nextAssignee = new String[2];
		if (taskParam.getApproveStatus() == CLOSE_ABLE) {
			tempSubcontract.setState(SubcontractStatus.CLOSED);// 已闭环
			nextAssignee[0] = "已闭环";
			nextAssignee[1] = "已闭环";
		} else if (taskParam.getApproveStatus() == CLOSE_DISABLE) {
			tempSubcontract.setState(SubcontractStatus.CLOSE_REJECT);// 无法闭环
			nextAssignee[0] = "无法闭环";
			nextAssignee[1] = "无法闭环";
		} else {
			throw new SubcontractException("请选择闭环意见！");
		}
		dao.updateSubcontractProjectByIdSelective(tempSubcontract);

		String nowUser = UserContext.getUserContext().getUser().getUsername();
		String processKey = SubcontractConstant.PROCESS_SUBCONTRACT_KEY;
		subcontract = dao.selectSubcontractProjectById(subcontract.getId());

		// 办理任务
		// Task task =
		// taskService.createTaskQuery().taskId(taskParam.getTaskId()).processInstanceId(taskParam.getInstId()).taskAssignee(nowUser).active().singleResult();
		List<Task> taskList = querySubcontractTaskList(SubcontractConstant.TASK_KEY_CLOSE, taskParam.getTaskId());
		Task task = null;
		if (taskList != null && !taskList.isEmpty()) {
			task = taskList.get(0);
			taskParam.setTaskId(task.getId());
		} else {
			throw new SubcontractException("没有【" + nowUser + "】的待办任务！任务办理失败！");
		}

		// 增加流程变量
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("result", taskParam.getApproveStatus());

		this.submitSelfTask(taskParam, vars);

		// 3.增加自定义的审批意见
		// workFlowService.addSelfActComment(subcontract.getId(), processKey,
		// task.getId(), task.getProcessInstanceId(),
		// taskParam.getApproveStatus(), taskParam.getComment());
		workFlowService.addSelfActComment(subcontract.getId(), processKey, task.getTaskDefinitionKey(), task.getId(),
				task.getProcessInstanceId(), taskParam.getApproveStatus(), taskParam.getComment(), nextAssignee[0],
				nextAssignee[1]);

		// 邮件
		Map<String, Object> mailMap = new HashMap<String, Object>();
		// String tos =
		// userManageService.queryMailsByRoleAndOfficeCodes(subcontract.getOfficeCode(),
		// MessageUtil.ROLE_SERVICEMANAGER);
		String[] nextAssignPer = getNextAssignPer(MessageUtil.ROLE_SERVICEMANAGER, subcontract.getOfficeCode());
		String tos = nextAssignPer[2];
		if (StringUtils.isNotBlank(tos)) {
			mailMap.put("tos", tos);
			mailMap.put("ccs", basicDataService.querySysArg(SubcontractTemplate.SUBCONTRACT_CCS_MAIL));
			if (taskParam.getApproveStatus() == CLOSE_ABLE) {
				mailMap.put("templateCode", SubcontractTemplate.CLOSE_NOTIFY_CODE);
			} else {
				mailMap.put("templateCode", SubcontractTemplate.APPROVE_REJECT_NOTIFY_CODE);
			}
			// mailMap.put("username", ((SubcontractProjectVO)
			// subcontract).getOfficeName() + "服务经理");
			mailMap.put("username", nextAssignPer[1]);
			mailMap.put("subcontractName", subcontract.getSubcontractName());
			mailMap.put("taskAssignee", nowUser + "-" + UserContext.getUserContext().getUser().getRealName());
			mailMap.put("comment", taskParam.getComment());
		}
		NotificationTemplateUtil.keepMail(mailMap);
		return "";
	}

	@Override
	@Transactional
	public String generateContractFlow(WorkflowCommonParam taskParam, SubcontractProject subcontract) {
		log("项目转包生成合同号");
		// 更改项目转包状态
		SubcontractProject tempSubcontract = new SubcontractProject();
		tempSubcontract.setId(subcontract.getId());
		tempSubcontract.setState(SubcontractStatus.EXECUTING);// 合同已生成.执行中
		dao.updateSubcontractProjectByIdSelective(tempSubcontract);

		String nowUser = UserContext.getUserContext().getUser().getUsername();
		String processKey = SubcontractConstant.PROCESS_SUBCONTRACT_KEY;
		subcontract = dao.selectSubcontractProjectVOById(subcontract.getId());

		List<Task> taskList = querySubcontractTaskList(TaskKey.GENERATE_CONTRACT, taskParam.getTaskId());
		Task task = null;
		if (taskList != null && !taskList.isEmpty()) {
			task = taskList.get(0);
			taskParam.setTaskId(task.getId());
		} else {
			throw new SubcontractException("没有【" + nowUser + "】的待办任务！任务办理失败！");
		}

		// 增加流程变量
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("sm", "smRole");
		vars.put("smRole", MessageUtil.ROLE_SERVICEMANAGER + "");
		vars.put("dpNo", subcontract.getOfficeCode());
		taskParam.setComment("生成转包合同号");
		this.submitSelfTask(taskParam, vars);

		// 3.增加自定义的审批意见
		// workFlowService.addSelfActComment(subcontract.getId(), processKey,
		// task.getId(), task.getProcessInstanceId(),
		// taskParam.getApproveStatus(), taskParam.getComment());
		String officeCode = subcontract.getOfficeCode();
		Department department = departmentManageService.queryDepartmentByDepartmentNum(officeCode);
		String[] nextAssignee = new String[] { "", "" };
		if (department != null) {
			nextAssignee[0] += department.getDepartmentName();
			nextAssignee[1] += department.getDepartmentName();
		}
		nextAssignee[0] += "服务经理";
		nextAssignee[1] += "服务经理";
		workFlowService.addSelfActComment(subcontract.getId(), processKey, task.getTaskDefinitionKey(), task.getId(),
				task.getProcessInstanceId(), AGREE, taskParam.getComment(), nextAssignee[0], nextAssignee[1]);

		// 邮件
		Map<String, Object> mailMap = new HashMap<String, Object>();
		// String tos =
		// userManageService.queryMailsByRoleAndOfficeCodes(subcontract.getOfficeCode(),
		// MessageUtil.ROLE_SERVICEMANAGER);
		String[] nextAssignPer = getNextAssignPer(MessageUtil.ROLE_SERVICEMANAGER, subcontract.getOfficeCode());
		String tos = nextAssignPer[2];
		if (StringUtils.isNotBlank(tos)) {
			mailMap.put("tos", tos);
			mailMap.put("templateCode", SubcontractTemplate.GEN_CONTRACT_NOTIFY_CODE);
			// mailMap.put("username", ((SubcontractProjectVO)
			// subcontract).getOfficeName() + "服务经理");
			mailMap.put("username", nextAssignPer[1]);
			mailMap.put("subcontractName", subcontract.getSubcontractName());
		}
		NotificationTemplateUtil.keepMail(mailMap);
		return "";
	}

	@Override
	@Transactional
	public String applyPaymentFlow(WorkflowCommonParam taskParam, SubcontractProject subcontract) {
	    return this.applyPaymentFlow(taskParam, subcontract, null);
//		log("项目转包服务经理提交付款信息");
//		// 更改项目转包状态
//		SubcontractProject tempSubcontract = new SubcontractProject();
//		tempSubcontract.setId(subcontract.getId());
//		tempSubcontract.setState(SubcontractStatus.EXECUTING);// 合同已生成.执行中
//		dao.updateSubcontractProjectByIdSelective(tempSubcontract);
//
//		String nowUser = UserContext.getUserContext().getUser().getUsername();
//		String processKey = SubcontractConstant.PROCESS_SUBCONTRACT_KEY;
//		subcontract = dao.selectSubcontractProjectById(subcontract.getId());
//
//		HashMap<String, Object> params = new HashMap<>();
//		params.put("checkOffice", "true");
//		List<Task> taskList = querySubcontractTaskList(TaskKey.APPLY_PAYMENT, "smRole", subcontract.getId(),
//				taskParam.getTaskId(), params);
//		Task task = null;
//		if (taskList != null && !taskList.isEmpty()) {
//			task = taskList.get(0);
//			taskParam.setTaskId(task.getId());
//		} else {
//			throw new SubcontractException("没有【" + nowUser + "】的待办任务！任务办理失败！");
//		}
//
//		// 增加流程变量
//		String[] nextAssignee = new String[2];
//		Map<String, Object> vars = new HashMap<String, Object>();
//		if (subcontract.getType() != null && subcontract.getType().equals(SubcontractType.MAINTENANCE)) {
//			vars.put("result", 1);
//			vars.put("cb", "cbRole");
//			vars.put("cbRole", MessageUtil.ROLE_CALLBACKPER + "");
//			nextAssignee[0] = "回访人员";
//			nextAssignee[1] = "回访人员";
//		} else {
//			vars.put("result", -1);
//			vars.put(SubcontractConstant.TASK_USER_ENGINEEMANAGER_EMP, "emRole");
//			vars.put("emRole", MessageUtil.ROLE_ENGINEEMANAGER + "");
//			nextAssignee[0] = "工程人员";
//			nextAssignee[1] = "工程人员";
//		}
//		taskParam.setComment("服务经理申请付款");
//		vars.put("paymentApplicant", nowUser + "-" + UserContext.getUserContext().getUser().getRealName());
//		this.submitSelfTask(taskParam, vars);
//
//		if (subcontract.getType() != null && subcontract.getType().equals(SubcontractType.MAINTENANCE)) {
//			SubcontractCallback callback = new SubcontractCallback();
//			callback.setSubcontractId(subcontract.getId());
//			int version = dao.queryCallBackQuesnaireVersion(subcontract.getId());
//			callback.setQuesnaireVersion(version);
//			this.insertSubcontractCallback(callback);
//
//			List<Task> callBackList = querySubcontractTaskList(TaskKey.CALLBACK, "cbRole", subcontract.getId());
//			if (!callBackList.isEmpty()) {
//				Task callBackTask = callBackList.get(0);
//				callback.setTaskId(callBackTask.getId());
//				this.updateSubcontractCallbackByIdSelective(callback);
//			}
//
//			subcontract.setCallbackState(20);
//			this.updateSubcontractProjectByIdSelective(subcontract);
//		}
//
//		// 3.增加自定义的审批意见
//		workFlowService.addSelfActComment(subcontract.getId(), processKey, task.getTaskDefinitionKey(), task.getId(),
//				task.getProcessInstanceId(), taskParam.getApproveStatus(), taskParam.getComment(), nextAssignee[0],
//				nextAssignee[1]);
//
//		// 邮件
//		Map<String, Object> mailMap = new HashMap<String, Object>();
//		if (subcontract.getType() != null && subcontract.getType().equals(SubcontractType.MAINTENANCE)) {
////			String tos = userManageService.queryMailsByRoleAndOfficeCodes("", MessageUtil.ROLE_CALLBACKPER);
//			String[] nextAssignPer = getNextAssignPer(MessageUtil.ROLE_CALLBACKPER, null);
//			String tos = nextAssignPer[2];
//			if (StringUtils.isNotBlank(tos)) {
//				mailMap.put("tos", tos);
//				mailMap.put("templateCode", SubcontractTemplate.APPROVE_NOTIFY_CODE);
//				mailMap.put("username", nextAssignPer[1]);
//				mailMap.put("subcontractName", subcontract.getSubcontractName());
//				mailMap.put("taskName", "回访");
//			}
//		} else {
//			String paymentUsers = basicDataService.querySysArg(SubcontractTemplate.PAYMENT_USER);
//			if (StringUtils.isNotBlank(paymentUsers)) {
////				String[] paymentUserArr = StringUtils.split(paymentUsers, ";");
////				List<String> usernameList = new ArrayList<>(paymentUserArr.length);
////				List<String> toList = new ArrayList<>(paymentUserArr.length);
////				for (String paymentUser : paymentUserArr) {
////					String[] userInfo = StringUtils.split(paymentUser, "/");
////					usernameList.add(userInfo[0]);
////					toList.add(userInfo[1]);
////				}
//				List<User> userList = userManageService.queryUsersByUserNames(paymentUsers);
//				List<String> usernameList = new ArrayList<>(userList.size());
//				List<String> toList = new ArrayList<>(userList.size());
//				if (!userList.isEmpty()) {
//					for (User user : userList) {
//						if (user.getStatus() == 0 ) {
//							continue;
//						}
//						usernameList.add(user.getUsername() + "-" + user.getRealName());
//						toList.add(user.getEmail());
//					}
//				}
//				if (!toList.isEmpty()) {
//					mailMap.put("tos", StringUtils.join(toList, ";"));
//					mailMap.put("templateCode", SubcontractTemplate.PAYMENT_APPLY_NOTIFY_CODE);
//					mailMap.put("username", StringUtils.join(usernameList, ";"));
//					mailMap.put("subcontractName", subcontract.getSubcontractName());
//					mailMap.put("subcontractNo", subcontract.getSubcontractNo());
//					mailMap.put("taskAssignee", nowUser + "-" + UserContext.getUserContext().getUser().getRealName());
//				}
//			}
//		}
//		NotificationTemplateUtil.keepMail(mailMap);
//		return "";
	}
	
    @Transactional
    public String applyPaymentFlow(WorkflowCommonParam taskParam, SubcontractProject subcontract, String nowUser) {
        log("项目转包服务经理提交付款信息");
        // 更改项目转包状态
        SubcontractProject tempSubcontract = new SubcontractProject();
        tempSubcontract.setId(subcontract.getId());
        tempSubcontract.setState(SubcontractStatus.EXECUTING);// 合同已生成.执行中
        dao.updateSubcontractProjectByIdSelective(tempSubcontract);

        if (StringUtils.isBlank(nowUser)) {
            nowUser = UserContext.getUserContext().getUser().getUsername();
        }
        String processKey = SubcontractConstant.PROCESS_SUBCONTRACT_KEY;
        subcontract = dao.selectSubcontractProjectById(subcontract.getId());

        HashMap<String, Object> params = new HashMap<>();
        params.put("checkOffice", "true");
        List<Task> taskList = querySubcontractTaskList(TaskKey.APPLY_PAYMENT, "smRole", subcontract.getId(),
                taskParam.getTaskId(), params);
        Task task = null;
        if (taskList != null && !taskList.isEmpty()) {
            task = taskList.get(0);
            taskParam.setTaskId(task.getId());
        } else {
            throw new SubcontractException("没有【" + nowUser + "】的待办任务！任务办理失败！");
        }

        // 增加流程变量
        String[] nextAssignee = new String[2];
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("approveStatus", 1); // 统一待办任务推OA状态
        if (subcontract.getType() != null && subcontract.getType().equals(SubcontractType.MAINTENANCE)) {
            vars.put("result", 1);
            vars.put("cb", "cbRole");
            vars.put("cbRole", MessageUtil.ROLE_CALLBACKPER + "");
            nextAssignee[0] = "回访人员";
            nextAssignee[1] = "回访人员";
        } else {
            vars.put("result", -1);
            vars.put(SubcontractConstant.TASK_USER_ENGINEEMANAGER_EMP, "emRole");
            vars.put("emRole", MessageUtil.ROLE_ENGINEEMANAGER + "");
            nextAssignee[0] = "工程人员";
            nextAssignee[1] = "工程人员";
        }
        taskParam.setComment("服务经理申请付款");
        vars.put("dpNo", subcontract.getOfficeCode());
        vars.put("paymentApplicant", nowUser + "-" + UserContext.getUserContext().getUser().getRealName());
        this.submitSelfTask(taskParam, vars);

        if (subcontract.getType() != null && subcontract.getType().equals(SubcontractType.MAINTENANCE)) {
            SubcontractCallback callback = new SubcontractCallback();
            callback.setSubcontractId(subcontract.getId());
            int version = dao.queryCallBackQuesnaireVersion(subcontract.getId());
            callback.setQuesnaireVersion(version);
            this.insertSubcontractCallback(callback);

            List<Task> callBackList = querySubcontractTaskList(TaskKey.CALLBACK, "cbRole", subcontract.getId());
            if (!callBackList.isEmpty()) {
                Task callBackTask = callBackList.get(0);
                callback.setTaskId(callBackTask.getId());
                this.updateSubcontractCallbackByIdSelective(callback);
            }

            subcontract.setCallbackState(20);
            this.updateSubcontractProjectByIdSelective(subcontract);
        }

        // 3.增加自定义的审批意见
        workFlowService.addSelfActComment(subcontract.getId(), processKey, task.getTaskDefinitionKey(), task.getId(),
                task.getProcessInstanceId(), taskParam.getApproveStatus(), taskParam.getComment(), nextAssignee[0],
                nextAssignee[1]);

        // 邮件
        Map<String, Object> mailMap = new HashMap<String, Object>();
        if (subcontract.getType() != null && subcontract.getType().equals(SubcontractType.MAINTENANCE)) {
//          String tos = userManageService.queryMailsByRoleAndOfficeCodes("", MessageUtil.ROLE_CALLBACKPER);
            String[] nextAssignPer = getNextAssignPer(MessageUtil.ROLE_CALLBACKPER, null);
            String tos = nextAssignPer[2];
            if (StringUtils.isNotBlank(tos)) {
                mailMap.put("tos", tos);
                mailMap.put("templateCode", SubcontractTemplate.APPROVE_NOTIFY_CODE);
                mailMap.put("username", nextAssignPer[1]);
                mailMap.put("subcontractName", subcontract.getSubcontractName());
                mailMap.put("taskName", "回访");
            }
        } else {
            String paymentUsers = basicDataService.querySysArg(SubcontractTemplate.PAYMENT_USER);
            if (StringUtils.isNotBlank(paymentUsers)) {
//              String[] paymentUserArr = StringUtils.split(paymentUsers, ";");
//              List<String> usernameList = new ArrayList<>(paymentUserArr.length);
//              List<String> toList = new ArrayList<>(paymentUserArr.length);
//              for (String paymentUser : paymentUserArr) {
//                  String[] userInfo = StringUtils.split(paymentUser, "/");
//                  usernameList.add(userInfo[0]);
//                  toList.add(userInfo[1]);
//              }
                List<User> userList = userManageService.queryUsersByUserNames(paymentUsers);
                List<String> usernameList = new ArrayList<>(userList.size());
                List<String> toList = new ArrayList<>(userList.size());
                if (!userList.isEmpty()) {
                    for (User user : userList) {
                        if (user.getStatus() == 0 ) {
                            continue;
                        }
                        usernameList.add(user.getUsername() + "-" + user.getRealName());
                        toList.add(user.getEmail());
                    }
                }
                if (!toList.isEmpty()) {
                    mailMap.put("tos", StringUtils.join(toList, ";"));
                    mailMap.put("templateCode", SubcontractTemplate.PAYMENT_APPLY_NOTIFY_CODE);
                    mailMap.put("username", StringUtils.join(usernameList, ";"));
                    mailMap.put("subcontractName", subcontract.getSubcontractName());
                    mailMap.put("subcontractNo", subcontract.getSubcontractNo());
                    mailMap.put("taskAssignee", nowUser + "-" + UserContext.getUserContext().getUser().getRealName());
                }
            }
        }
        NotificationTemplateUtil.keepMail(mailMap);
        return "";
    }

	@Override
	@Transactional
	public String submitCallBackFlow2(WorkflowCommonParam taskParam, SubcontractCallback subcontractCallback) {
		String processKey = SubcontractConstant.PROCESS_CALLBACK_KEY;
		// 更改项目回访状态
		// SubcontractProject tempSubcontract = new SubcontractProject();
		SubcontractProjectVO tempSubcontract = this
				.selectSubcontractProjectVOById(subcontractCallback.getSubcontractId());
		if (tempSubcontract == null) {
			tempSubcontract = new SubcontractProjectVO();
		}
		tempSubcontract.setId(subcontractCallback.getSubcontractId());
		String[] nextAssignee = new String[] { "", "" };
		Map<String, Object> vars = new HashMap<String, Object>();
		if (taskParam.getApproveStatus() == CALLBACK_PASS) {
			tempSubcontract.setCallbackState(60);
			vars.put(SubcontractConstant.TASK_USER_ENGINEEMANAGER_EMP, "emRole");
			vars.put("emRole", MessageUtil.ROLE_ENGINEEMANAGER + "");
			nextAssignee[0] = "工程人员";
			nextAssignee[1] = "工程人员";
		} else if (taskParam.getApproveStatus() == CALLBACK_DISABLE) {
			tempSubcontract.setCallbackState(50);
			vars.put(SubcontractConstant.TASK_USER_ENGINEEMANAGER_EMP, "emRole");
			vars.put("emRole", MessageUtil.ROLE_ENGINEEMANAGER + "");
			nextAssignee[0] = "工程人员";
			nextAssignee[1] = "工程人员";
		} else if (taskParam.getApproveStatus() == CALLBACK_REJECT) {
			tempSubcontract.setCallbackState(-20);
			String officeCode = tempSubcontract.getOfficeCode();
			Department department = departmentManageService.queryDepartmentByDepartmentNum(officeCode);
			if (department != null) {
				nextAssignee[0] += department.getDepartmentName();
				nextAssignee[1] += department.getDepartmentName();
			}
			nextAssignee[0] += "服务经理";
			nextAssignee[1] += "服务经理";
		} else {
			throw new SubcontractException("请选择回访意见！");
		}
		dao.updateSubcontractProjectByIdSelective(tempSubcontract);

		String nowUser = UserContext.getUserContext().getUser().getUsername();

		// 办理任务
		List<Task> taskList = querySubcontractTaskList(TaskKey.CALLBACK, taskParam.getTaskId());
		Task task = null;
		if (taskList != null && !taskList.isEmpty()) {
			task = taskList.get(0);
			taskParam.setTaskId(task.getId());
		} else {
			throw new SubcontractException("没有【" + nowUser + "】的待办任务！任务办理失败！");
		}

		// 增加流程变量
		vars.put("result", taskParam.getApproveStatus());

		// 获取原来的流程变量，用于取付款申请发起人
		Map<String, Object> oldVars = workFlowService.queryProcessVarMap(task.getId());

		this.submitSelfTask(taskParam, vars);

		// 3.增加自定义的审批意见
		workFlowService.addSelfActComment(subcontractCallback.getId(), processKey, task.getTaskDefinitionKey(),
				task.getId(), task.getProcessInstanceId(), taskParam.getApproveStatus(), taskParam.getComment(),
				nextAssignee[0], nextAssignee[1]);

		// 邮件
		Map<String, Object> mailMap = new HashMap<String, Object>();
		if (taskParam.getApproveStatus() == CALLBACK_REJECT) {
			// String tos =
			// userManageService.queryMailsByRoleAndOfficeCodes(tempSubcontract.getOfficeCode(),
			// MessageUtil.ROLE_SERVICEMANAGER);
			String[] nextAssignPer = getNextAssignPer(MessageUtil.ROLE_SERVICEMANAGER, tempSubcontract.getOfficeCode());
			String tos = nextAssignPer[2];
			if (StringUtils.isNotBlank(tos)) {
				mailMap.put("tos", tos);
				mailMap.put("ccs", basicDataService.querySysArg(SubcontractTemplate.SUBCONTRACT_CCS_MAIL));
				mailMap.put("templateCode", SubcontractTemplate.CALLBACK_REJECT_NOTIFY_CODE);
				// mailMap.put("username", tempSubcontract.getOfficeName() +
				// "服务经理");
				mailMap.put("username", nextAssignPer[1]);
				mailMap.put("subcontractName", tempSubcontract.getSubcontractName());
				mailMap.put("comment", taskParam.getComment());
			}
		} else {
			String paymentUsers = basicDataService.querySysArg(SubcontractTemplate.PAYMENT_USER);
			String taskAssignee = (String) oldVars.get("paymentApplicant");
			if (StringUtils.isNotBlank(paymentUsers)) {
//				String[] paymentUserArr = StringUtils.split(paymentUsers, ";");
//				List<String> usernameList = new ArrayList<>(paymentUserArr.length);
//				List<String> toList = new ArrayList<>(paymentUserArr.length);
//				for (String paymentUser : paymentUserArr) {
//					String[] userInfo = StringUtils.split(paymentUser, "/");
//					usernameList.add(userInfo[0]);
//					toList.add(userInfo[1]);
//				}
				List<User> userList = userManageService.queryUsersByUserNames(paymentUsers);
				List<String> usernameList = new ArrayList<>(userList.size());
				List<String> toList = new ArrayList<>(userList.size());
				if (!userList.isEmpty()) {
					for (User user : userList) {
						if (user.getStatus() == 0 ) {
							continue;
						}
						usernameList.add(user.getUsername() + "-" + user.getRealName());
						toList.add(user.getEmail());
					}
				}
				if (!toList.isEmpty()) {
					mailMap.put("tos", StringUtils.join(toList, ";"));
					mailMap.put("templateCode", SubcontractTemplate.PAYMENT_APPLY_NOTIFY_CODE);
					mailMap.put("username", StringUtils.join(usernameList, ";"));
					mailMap.put("subcontractNo", tempSubcontract.getSubcontractNo());
					mailMap.put("subcontractName", tempSubcontract.getSubcontractName());
					mailMap.put("taskAssignee", taskAssignee);
					if (taskParam.getApproveStatus() == CALLBACK_PASS) {
						mailMap.put("content", "并已经回访通过，");
					} else {
						mailMap.put("content", "但无法回访，无法回访原因：" + taskParam.getComment() + "，");
					}
				}
			}
		}
		NotificationTemplateUtil.keepMail(mailMap);
		return "";
	}

	@Override
	@Transactional
	public String approvePaymentFlow(WorkflowCommonParam taskParam, SubcontractProject subcontract) {
		log("项目转包付款");
		// 更改项目转包状态
		// SubcontractProject tempSubcontract = new SubcontractProject();
		SubcontractProject tempSubcontract = this.selectSubcontractProjectById(subcontract.getId());
		if (tempSubcontract == null) {
			tempSubcontract = new SubcontractProject();
		}

		tempSubcontract.setId(subcontract.getId());
		String[] nextAssignee = new String[] { "", "" };
		if (taskParam.getApproveStatus() == CLOSE_ABLE) {
			tempSubcontract.setState(SubcontractStatus.CLOSED);// 已闭环
			nextAssignee[0] = "已闭环";
			nextAssignee[1] = "已闭环";
		} else if (taskParam.getApproveStatus() == REJECT) {
			tempSubcontract.setState(SubcontractStatus.CLOSE_REJECT);// 闭环驳回
			nextAssignee[0] = "闭环驳回";
			nextAssignee[1] = "闭环驳回";
		} else if (taskParam.getApproveStatus() == PAYMENT) {
			String officeCode = tempSubcontract.getOfficeCode();
			Department department = departmentManageService.queryDepartmentByDepartmentNum(officeCode);
			if (department != null) {
				nextAssignee[0] += department.getDepartmentName();
				nextAssignee[1] += department.getDepartmentName();
			}
			nextAssignee[0] += "服务经理";
			nextAssignee[1] += "服务经理";
		} else {
			throw new SubcontractException("请选择付款意见！");
		}
		dao.updateSubcontractProjectByIdSelective(tempSubcontract);

		String nowUser = UserContext.getUserContext().getUser().getUsername();
		String processKey = SubcontractConstant.PROCESS_SUBCONTRACT_KEY;
		subcontract = dao.selectSubcontractProjectVOById(subcontract.getId());

		// 办理任务
		List<Task> taskList = querySubcontractTaskList(TaskKey.APPROVE_PAYMENT, taskParam.getTaskId());
		Task task = null;
		if (taskList != null && !taskList.isEmpty()) {
			task = taskList.get(0);
			taskParam.setTaskId(task.getId());
		} else {
			throw new SubcontractException("没有【" + nowUser + "】的待办任务！任务办理失败！");
		}
		task.getTaskDefinitionKey();
		// 增加流程变量
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("result", taskParam.getApproveStatus());
		vars.put("dpNo", tempSubcontract.getOfficeCode());
		this.submitSelfTask(taskParam, vars);

		// 3.增加自定义的审批意见
		workFlowService.addSelfActComment(subcontract.getId(), processKey, task.getTaskDefinitionKey(), task.getId(),
				task.getProcessInstanceId(), taskParam.getApproveStatus(), taskParam.getComment(), nextAssignee[0],
				nextAssignee[1]);
		// 邮件
		// String tos =
		// userManageService.queryMailsByRoleAndOfficeCodes(tempSubcontract.getOfficeCode(),
		// MessageUtil.ROLE_SERVICEMANAGER);
		String[] nextAssignPer = getNextAssignPer(MessageUtil.ROLE_SERVICEMANAGER, tempSubcontract.getOfficeCode());
		String tos = nextAssignPer[2];
		HashMap<String, Object> mailMap = new HashMap<>();
		if (StringUtils.isNotBlank(tos)) {
			mailMap.put("tos", tos);
			if (taskParam.getApproveStatus() == CLOSE_ABLE) {
				mailMap.put("templateCode", SubcontractTemplate.CLOSE_NOTIFY_CODE);
			} else if (taskParam.getApproveStatus() == PAYMENT) {
				String paiedAmount = this.querySubcontractPaiedAmount(subcontract.getId());
				if (StringUtils.isBlank(paiedAmount)) {
					throw new SubcontractException("付款金额为空！");
				}
				DecimalFormat decimalFormat = new DecimalFormat("#,##0.##");
				BigDecimal b = new BigDecimal(paiedAmount);
				paiedAmount = decimalFormat.format(b);
				mailMap.put("paiedAmount", paiedAmount);
				mailMap.put("templateCode", SubcontractTemplate.PAYMENT_FINISH_NOTIFY_CODE);
			} else if (taskParam.getApproveStatus() == REJECT) {
				mailMap.put("ccs", basicDataService.querySysArg(SubcontractTemplate.SUBCONTRACT_CCS_MAIL));
				mailMap.put("templateCode", SubcontractTemplate.APPROVE_REJECT_NOTIFY_CODE);
			}
			// mailMap.put("username", ((SubcontractProjectVO)
			// subcontract).getOfficeName() + "服务经理");
			mailMap.put("username", nextAssignPer[1]);
			mailMap.put("subcontractName", subcontract.getSubcontractName());
			mailMap.put("taskAssignee", nowUser + "-" + UserContext.getUserContext().getUser().getRealName());
			mailMap.put("comment", taskParam.getComment());
		}
		NotificationTemplateUtil.keepMail(mailMap);
		return "";
	}

	@Override
	@Transactional
	public String startCallBackFlow(Integer subcontractId) {
		log("触发项目转包回访流程");
		// 更改项目转包状态
		SubcontractProject tempSubcontract = new SubcontractProject();
		tempSubcontract.setId(subcontractId);
		tempSubcontract.setCallbackState(20);// 回访中
		dao.updateSubcontractProjectByIdSelective(tempSubcontract);

		String nowUser = UserContext.getUserContext().getUser().getUsername();
		String processKey = SubcontractConstant.PROCESS_CALLBACK_KEY;

		SubcontractCallback callback = new SubcontractCallback();
		callback.setSubcontractId(subcontractId);
		int version = dao.queryCallBackQuesnaireVersion(subcontractId);
		callback.setQuesnaireVersion(version);
		this.insertSubcontractCallback(callback);

		// 判断是否已经有正在进行的回访流程
		Task callBackTask = taskService.createTaskQuery().processDefinitionKey(processKey)
				.processVariableValueEquals("subcontractId", subcontractId).active().singleResult();

		// 回访流程正在进行，本次不再发起回访！
		if (callBackTask != null) {
			throw new SubcontractException("回访流程正在进行，本次不再发起回访！");
		}

		// 1.获取流程变量
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("applyBy", getLoginName());
		vars.put("subcontractId", subcontractId);
		vars.put(SubcontractConstant.TASK_USER_SERVICE, nowUser);

		// 2.拼接businessKey
		String businessKey = processKey + "." + subcontractId + "." + callback.getId();

		// 3.启动流程
		ProcessInstance process = workFlowService.startProcess(processKey, businessKey, vars);
		String instId = process.getId();

		// 将回访任务taskId更新至回访问卷表中
		Task task = taskService.createTaskQuery().processInstanceId(instId).singleResult();
		callback.setTaskId(task.getId());
		this.updateSubcontractCallbackByIdSelective(callback);

		vars.clear();
		vars.put("cb", "cbRole");
		vars.put("cbRole", MessageUtil.ROLE_CALLBACKPER + "");
		workFlowService.doSelfTask(task, instId, "上传服务单触发回访流程", vars);

		String[] nextAssign = new String[2];
		nextAssign[0] = "回访人员";
		nextAssign[1] = "回访人员";
		// 6.增加自定义的审批意见
		// workFlowService.addSelfActComment(callback.getId(), processKey,
		// task.getId(), instId, APPLY, "上传服务单触发回访流程");
		workFlowService.addSelfActComment(callback.getId(), processKey, task.getTaskDefinitionKey(), task.getId(),
				instId, APPLY, "上传服务单触发回访流程", nextAssign[0], nextAssign[1]);

		// 7.增加通知下一步审批人邮件
		return "";
	}

	@Override
	@Transactional
	public String submitCallBackFlow(WorkflowCommonParam taskParam, SubcontractCallback subcontractCallback) {
		String processKey = SubcontractConstant.PROCESS_CALLBACK_KEY;
		// 更改项目回访状态
		SubcontractProject tempSubcontract = new SubcontractProject();
		tempSubcontract.setId(subcontractCallback.getSubcontractId());
		String[] nextAssignee = new String[2];
		if (taskParam.getApproveStatus() == CALLBACK_PASS) {
			// tempSubcontract.setState(60);// 回访通过
			tempSubcontract.setCallbackState(60);
			nextAssignee[0] = "回访通过";
			nextAssignee[1] = "回访通过";
		} else if (taskParam.getApproveStatus() == CALLBACK_DISABLE) {
			// tempSubcontract.setState(50);// 无法回访
			tempSubcontract.setCallbackState(50);
			nextAssignee[0] = "无法回访";
			nextAssignee[1] = "无法回访";
		} else if (taskParam.getApproveStatus() == CALLBACK_REJECT) {
			// tempSubcontract.setState(-20);// 回访不通过
			tempSubcontract.setCallbackState(-20);
			nextAssignee[0] = "回访不通过";
			nextAssignee[1] = "回访不通过";
		} else {
			throw new SubcontractException("请选择回访意见！");
		}
		dao.updateSubcontractProjectByIdSelective(tempSubcontract);

		String nowUser = UserContext.getUserContext().getUser().getUsername();

		// 办理任务
		// Task task =
		// taskService.createTaskQuery().taskId(taskParam.getTaskId()).processInstanceId(taskParam.getInstId()).taskAssignee(nowUser).active().singleResult();
		List<Task> taskList = querySubcontractTaskList(SubcontractConstant.TASK_KEY_CALLBACK, taskParam.getTaskId());
		Task task = null;
		if (taskList != null && !taskList.isEmpty()) {
			task = taskList.get(0);
			taskParam.setTaskId(task.getId());
		} else {
			throw new SubcontractException("没有【" + nowUser + "】的待办任务！任务办理失败！");
		}

		// 增加流程变量
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("result", taskParam.getApproveStatus());

		this.submitSelfTask(taskParam, vars);

		// 3.增加自定义的审批意见
		// workFlowService.addSelfActComment(subcontractCallback.getId(),
		// processKey, task.getId(),
		// task.getProcessInstanceId(), taskParam.getApproveStatus(),
		// taskParam.getComment());
		workFlowService.addSelfActComment(subcontractCallback.getId(), processKey, task.getTaskDefinitionKey(),
				task.getId(), task.getProcessInstanceId(), taskParam.getApproveStatus(), taskParam.getComment(),
				nextAssignee[0], nextAssignee[1]);

		return "";
	}

	@Override
	@Transactional
	public void terminateWorkFlow(Integer subcontractId) {
		this.terminateWorkFlow(subcontractId, null);
	}

	@Override
	@Transactional
	public void terminateWorkFlow(Integer subcontractId, String comment) {
		log("项目转包闭环");
		// 更改项目转包状态
		SubcontractProject tempSubcontract = new SubcontractProject();
		tempSubcontract.setId(subcontractId);
		String[] nextAssignee = new String[2];
		tempSubcontract.setState(SubcontractStatus.CLOSE_REJECT);// 无法闭环
		nextAssignee[0] = "无法闭环";
		nextAssignee[1] = "无法闭环";
		dao.updateSubcontractProjectByIdSelective(tempSubcontract);

		String nowUser = UserContext.getUserContext().getUser().getUsername();
		String processKey = SubcontractConstant.PROCESS_SUBCONTRACT_KEY;
		SubcontractProjectVO subcontract = dao.selectSubcontractProjectVOById(subcontractId);

		// 办理任务
		HashMap<String, Object> params = new HashMap<>();
		params.put("subcontractId", subcontractId);
		List<Task> taskList = dao.querySubcontractTaskList(params);
		// List<Task> taskList = querySubcontractTaskList("", subcontractId);
		if (taskList.isEmpty()) {
			throw new SubcontractException("该转包申请没有进行中的流程，终止流程失败！");
		}

		// 3.增加自定义的审批意见
		RuntimeService runtimeService = SpringContext.getApplicationContext().getBean("runtimeService",
				RuntimeService.class);
		// List<String> instIds = new ArrayList<>(taskList.size());
		if (StringUtils.isBlank(comment)) {
			comment = "终止流程";
		}
		for (Task task : taskList) {
			// instIds.add(task.getProcessInstanceId());
			runtimeService.deleteProcessInstance(task.getProcessInstanceId(), comment);
			workFlowService.addSelfActComment(subcontract.getId(), processKey, task.getTaskDefinitionKey(),
					task.getId(), task.getProcessInstanceId(), CommentStatus.CLOSE_DISABLE, comment, nextAssignee[0],
					nextAssignee[1]);
		}

		Map<String, Object> mailMap = new HashMap<String, Object>();
		// String tos =
		// userManageService.queryMailsByRoleAndOfficeCodes(subcontract.getOfficeCode(),
		// MessageUtil.ROLE_SERVICEMANAGER);
		String[] nextAssignPer = getNextAssignPer(MessageUtil.ROLE_SERVICEMANAGER, subcontract.getOfficeCode());
		String tos = nextAssignPer[2];
		if (StringUtils.isNotBlank(tos)) {
			mailMap.put("tos", tos);
			mailMap.put("ccs", basicDataService.querySysArg(SubcontractTemplate.SUBCONTRACT_CCS_MAIL));
			mailMap.put("templateCode", SubcontractTemplate.APPROVE_REJECT_NOTIFY_CODE);
			// mailMap.put("username", subcontract.getOfficeName() + "服务经理");
			mailMap.put("username", nextAssignPer[1]);
			mailMap.put("subcontractName", subcontract.getSubcontractName());
			mailMap.put("taskAssignee", nowUser + "-" + UserContext.getUserContext().getUser().getRealName());
			mailMap.put("comment", comment);
		}
		NotificationTemplateUtil.keepMail(mailMap);
	}

	@Override
	@Deprecated
	public Task queryCurrentTask(Integer subcontractId) {
		String processKey = SubcontractConstant.PROCESS_SUBCONTRACT_KEY;
		// String bussinessKey = SubcontractConstant.PROCESS_SUBCONTRACT_KEY +
		// subcontractId;
		List<Task> taskList = taskService.createTaskQuery().processDefinitionKey(processKey)
				.taskAssignee(getLoginName()).processVariableValueEquals("subcontractId", subcontractId).active()
				.orderByTaskCreateTime().desc().list();
		if (!taskList.isEmpty()) {
			return taskList.get(0);
		}
		return null;
	}

	@Override
	@Deprecated
	public SubcontractComment queryCurrentSubcontractCommon(Integer subcontractId) {
		String processKey = SubcontractConstant.PROCESS_SUBCONTRACT_KEY;
		String bussinessKey = SubcontractConstant.PROCESS_SUBCONTRACT_KEY + "." + subcontractId;
		List<Task> taskList = taskService.createTaskQuery().processInstanceBusinessKey(bussinessKey)
				.processDefinitionKey(processKey).taskAssignee(getLoginName())
				.processVariableValueEquals("subcontractId", subcontractId).active().orderByTaskCreateTime().desc()
				.list();
		if (!taskList.isEmpty()) {
			Task task = taskList.get(0);
			SubcontractComment taskParam = new SubcontractComment();
			taskParam.setInstId(task.getProcessInstanceId());
			taskParam.setSubcontractId(subcontractId);
			taskParam.setTaskId(task.getId());
			return taskParam;
		}
		return null;
	}

	@Override
	public WorkflowCommonParam queryCurrentWorkFlowCommonParam(Integer subcontractId) {
		return queryCurrentWorkFlowCommonParam(subcontractId, null);
	}

	@Override
	public WorkflowCommonParam queryCurrentWorkFlowCommonParam(Integer subcontractId, String taskKey) {
		return queryCurrentWorkFlowCommonParam(subcontractId, taskKey, null);
	}

	@Override
	public WorkflowCommonParam queryCurrentWorkFlowCommonParam(Integer subcontractId, String taskKey,
			String roleGroup) {
		return queryCurrentWorkFlowCommonParam(subcontractId, taskKey, null, null);
	}

	@Override
	public WorkflowCommonParam queryCurrentWorkFlowCommonParam(Integer subcontractId, String taskKey, String roleGroup,
			HashMap<String, Object> params) {
		// List<Task> taskList =
		// taskService.createTaskQuery().taskDefinitionKey("approveTask").processDefinitionKey(processKey).taskAssignee(getLoginName()).processVariableValueEquals("subcontractId",
		// subcontractId).active().orderByTaskCreateTime().desc().list();
		List<Task> taskList = this.querySubcontractTaskList(taskKey, roleGroup, subcontractId, params);
		if (!taskList.isEmpty()) {
			Task task = taskList.get(0);
			Map<String, Object> taskLocalVariables = taskService.getVariables(task.getId());
			// Map<String, Object> taskLocalVariables =
			// task.getProcessVariables();
			Integer evHeaderId = (Integer) taskLocalVariables.get("objId");
			WorkflowCommonParam taskParam = new WorkflowCommonParam();
			taskParam.setTaskId(task.getId());
			taskParam.setInstId(task.getProcessInstanceId());
			taskParam.setOutcome(task.getTaskDefinitionKey());
			if (evHeaderId != null && evHeaderId != 0) {
				taskParam.setObjId(evHeaderId);
			}
			return taskParam;
		}
		return null;
	}

	/**
	 * 
	 * @param taskKey
	 * @return
	 */
	public List<Task> querySubcontractTaskList(String taskKey) {
		return querySubcontractTaskList(taskKey, null, null, null, null);
	}

	/**
	 * 
	 * @param taskKey
	 * @param taskId
	 * @return
	 */
	public List<Task> querySubcontractTaskList(String taskKey, String taskId) {
		return querySubcontractTaskList(taskKey, null, taskId);
	}

	/**
	 * 
	 * @param taskKey
	 * @param taskId
	 * @return
	 */
	public List<Task> querySubcontractTaskList(String taskKey, Integer subcontractId) {
		return querySubcontractTaskList(taskKey, subcontractId, null);
	}

	/**
	 * 
	 * @param taskKey
	 * @param taskId
	 * @return
	 */
	public List<Task> querySubcontractTaskList(String taskKey, Integer subcontractId, String taskId) {
		return querySubcontractTaskList(taskKey, null, subcontractId, taskId);
	}

	public List<Task> querySubcontractTaskList(String taskKey, String roleGroup, Integer subcontractId) {
		return querySubcontractTaskList(taskKey, roleGroup, subcontractId, null, null);
	}

	/**
	 * 
	 * @param taskKey
	 * @param roleGroup
	 * @param subcontractId
	 * @param taskId
	 * @return
	 */
	public List<Task> querySubcontractTaskList(String taskKey, String roleGroup, Integer subcontractId, String taskId) {
		return querySubcontractTaskList(taskKey, roleGroup, subcontractId, taskId, new HashMap<String, Object>());
	}

	/**
	 * 
	 * @param taskKey
	 * @param roleGroup
	 * @param subcontractId
	 * @param taskId
	 * @return
	 */
	public List<Task> querySubcontractTaskList(String taskKey, String roleGroup, Integer subcontractId,
			HashMap<String, Object> params) {
		return querySubcontractTaskList(taskKey, roleGroup, subcontractId, null, params);
	}

	/**
	 * 
	 * @param taskKey
	 * @param roleGroup
	 * @param subcontractId
	 * @param taskId
	 * @return
	 */
	public List<Task> querySubcontractTaskList(String taskKey, String roleGroup, Integer subcontractId, String taskId,
			HashMap<String, Object> params) {
		// HashMap<String, Object> params = new HashMap<>();
		if (params == null) {
			params = new HashMap<>();
		}
		UserContext context = UserContext.getUserContext();
		params.put("assignee", getLoginName());
		params.put("areaPower", context.getUser().getAreapower());
		if (StringUtils.isNotBlank(taskKey)) {
			String[] taskKeys = taskKey.split(";");
			params.put("taskKey", taskKeys);
		}
		params.put("taskId", taskId);
		params.put("subcontractId", subcontractId);
		if (StringUtils.isBlank(roleGroup)) {
			if (context.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER)
					|| context.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER)) {
				roleGroup = "emRole";
				if (context.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER)) {
					roleGroup += ",role_" + MessageUtil.ROLE_ENGINEEMANAGER_LEADER;
				}
				// params.put("roleGroup", "emRole");
			} else if (context.isHasRole(MessageUtil.ROLE_CALLBACKPER)) {
				// params.put("roleGroup", "cbRole");
				roleGroup = "cbRole";
			} else if (context.isHasRole(MessageUtil.ROLE_AREA_LEADER)) {
				roleGroup = "zrRole,role_" + MessageUtil.ROLE_AREA_LEADER;
				params.put("checkProfitDep", "true");
			}
		}
		params.put("roleGroup", roleGroup);
		return dao.querySubcontractTaskList(params);
	}

	public void submitSelfTask(WorkflowCommonParam param, Map<String, Object> vars) {
		String taskId = param.getTaskId();
		String username = UserContext.getUserContext().getUser().getUsername();
		Authentication.setAuthenticatedUserId(username);

		taskService.addComment(taskId, null, param.getComment());
		taskService.setVariablesLocal(taskId, vars);
		taskService.complete(taskId, vars);
	}

	public void submitSelfTask(SubcontractComment param, Map<String, Object> vars) {
		String taskId = param.getTaskId();
		String username = UserContext.getUserContext().getUser().getUsername();
		Authentication.setAuthenticatedUserId(username);

		taskService.addComment(taskId, null, param.getComment());
		taskService.setVariablesLocal(taskId, vars);
		taskService.complete(taskId, vars);
	}

	@Override
	@Transactional
	public void insertSubcontractQuesnaire(SubcontractCallback callback,
			PmClQuesnaireResultHeader pmClQuesnaireResultHeader,
			List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList) {
		// 1.插入问卷头
		pmClQuesnaireResultHeader.setEvaluationHeaderId(0);
		int pmClQuesnaireResultHeaderId = pmClosedLoopDao.addPmClQuesResultHeader(pmClQuesnaireResultHeader);
		// 2.插入问卷结果行信息
		pmClosedLoopDao.addPmClQuesResultLineList(pmClQuesnaireResultLineList, pmClQuesnaireResultHeader);

		// 3.将问卷信息保存进回访流程中
		// 3.0查询本次审批的问卷是否已经保存过
		int callbackId = dao.queryCallBackId(callback);

		if (callbackId != 0) {
			// 3.1将新的问卷ID更新到回访问卷表中
			SubcontractCallback temp = new SubcontractCallback();
			temp.setId(callbackId);
			temp.setQuesnaireId(pmClQuesnaireResultHeaderId);
			temp.setQuesnaireState(pmClQuesnaireResultHeader.getStatus());
			this.updateSubcontractCallbackByIdSelective(temp);
		} else {
			// 3.1查询问卷版本号
			int version = dao.queryCallBackQuesnaireVersion(callback.getSubcontractId());
			// 3.2保存问卷与回访关联关系表
			callback.setQuesnaireId(pmClQuesnaireResultHeaderId);
			callback.setQuesnaireVersion(version);
			callback.setQuesnaireState(pmClQuesnaireResultHeader.getStatus());
			this.insertSubcontractCallback(callback);
		}
	}

	/**
	 * @param subcontractCallback
	 */
	public void updateSubcontractCallbackByIdSelective(SubcontractCallback subcontractCallback) {
		subcontractCallback.setUpdateBy(getLoginName());
		subcontractCallback.setUpdateTime(new Date());
		dao.updateSubcontractCallbackByIdSelective(subcontractCallback);
	}

	/**
	 * @param callback
	 */
	@Override
	public void insertSubcontractCallback(SubcontractCallback callback) {
		callback.setCreateBy(getLoginName());
		callback.setCreateTime(new Date());
		dao.insertSubcontractCallback(callback);
	}

	/**
	 * @param callback
	 */
	public void insertSubcontractCallbackSelective(SubcontractCallback callback) {
		callback.setCreateBy(getLoginName());
		callback.setCreateTime(new Date());
		dao.insertSubcontractCallbackSelective(callback);
	}

	@Override
	public List<SubcontractCallback> selectSubcontractCallbackList(SubcontractCallback subcontractCallback) {
		return dao.selectSubcontractCallbackList(subcontractCallback);
	}

	@Override
	public SubcontractCallback selectMaxSubcontractCallback(SubcontractCallback subcontractCallback) {
		SubcontractCallback maxSubcontractCallback = dao.selectMaxSubcontractCallback(subcontractCallback);
		if (maxSubcontractCallback == null) {
			maxSubcontractCallback = subcontractCallback;
		}
		return maxSubcontractCallback;
	}

	@Override
	public List<Map<String, Object>> querySubcontractCommentList(Integer subcontractId) {
		return dao.querySubcontractCommentList(subcontractId);
	}

	@Override
	public void insertSubcontractFacilitator(SubcontractFacilitator subcontractFacilitator) {
		subcontractFacilitator.setState(true);
		subcontractFacilitator.setCreateTime(new Date());
		subcontractFacilitator.setCreateBy(getLoginName());
		subcontractFacilitator.setEffectiveFrom(subcontractFacilitator.getEffectiveFrom() == null
				? subcontractFacilitator.getCreateTime() : subcontractFacilitator.getEffectiveFrom());
		dao.insertSubcontractFacilitator(subcontractFacilitator);
	}

	@Override
	public void updateSubcontractFacilitatorByIdSelective(SubcontractFacilitator subcontractFacilitator) {
		subcontractFacilitator.setUpdateTime(new Date());
		subcontractFacilitator.setUpdateBy(getLoginName());
		dao.updateSubcontractFacilitatorByIdSelective(subcontractFacilitator);
	}

	@Override
	public SubcontractFacilitator selectSubcontractFacilitatorById(Integer id) {
		return dao.selectSubcontractFacilitatorById(id);
	}

	@Override
	public List<SubcontractProjectVO> querySubcontractInfoForProject(String projectIds) {
		return dao.querySubcontractInfoForProject(projectIds);
	}

	@Override
	public void insertSubcontractPrice(SubcontractPrice price) {
		price.setCreateTime(new Date());
		price.setCreateBy(getLoginName());
		dao.insertSubcontractPrice(price);
	}

	@Override
	public void updateSubcontractPriceByIdSelective(SubcontractPrice price) {
		price.setUpdateTime(new Date());
		price.setUpdateBy(getLoginName());
		dao.updateSubcontractPriceByIdSelective(price);
	}

	@Override
	public List<Map<String, Object>> selectRejectedSubcontractProjectList(HashMap<String, Object> params) {
		return dao.selectRejectedSubcontractProjectList(params);
	}

	@Override
	public List<SubcontractProjectVO> querySubcontractExportData(SubcontractProjectVO subcontractVO) {
		return dao.querySubcontractExportData(subcontractVO);
	}

	
	@Override
	public List<SubcontractProjectVO> queryNextPaymentTask() {
		return dao.queryNextPaymentTask();
	}
	
	@Override
	public List<SubcontractPayment> querySSESubcontractPaymentList() {
	    return dao.querySSESubcontractPaymentList();
	}

	/**
	 * 查询某角色的用户名字符串
	 * 
	 * @param roleStr
	 * @return
	 */
	@SuppressWarnings("unused")
	private StringBuilder getNextAssignPer(String roleStr) {
		User user = new User();
		user.setStatus(1); // 获取有效的回访人员或工程人员
		List<User> userList = userManageService.queryAllUserList(user);
		StringBuilder nextAssignPer = new StringBuilder();

		for (User userObj : userList) {
			if (userObj.getRoleids().contains(roleStr)) {
				nextAssignPer.append(userObj.getUsername() + ",");
			}
		}

		if (nextAssignPer.length() <= 0) {
			throw new SubcontractException("获取下一级审核人员出错");
		}

		return nextAssignPer;
	}

	/**
	 * 查询某角色的用户名字符串,以及姓名
	 * 
	 * @param roleId
	 * @return
	 */
	private String[] getNextAssignPer(int roleId) {
//		// 获取有效的回访人员或工程人员
//		List<User> userList = userManageService.queryUserWithRoleId(roleId);
//		List<String> nextAssignPer = new ArrayList<>(userList.size());
//		List<String> nextAssignName = new ArrayList<>(userList.size());
//		for (User userObj : userList) {
//			nextAssignPer.add(userObj.getUsername());
//			nextAssignName.add(userObj.getUsername() + "-" + userObj.getRealName());
//		}
//
//		if (nextAssignPer.size() <= 0) {
//			throw new SubcontractException("获取下一级审核人员出错");
//		}
//		String[] nextAssigen = new String[] { StringUtils.join(nextAssignPer, ","),
//				StringUtils.join(nextAssignName, ",") };
//		return nextAssigen;
		return getNextAssignPer(roleId, null);
	}

	/**
	 * 查询某角色的用户名字符串,以及姓名
	 * 
	 * @param roleId
	 * @return
	 */
	private String[] getNextAssignPer(int roleId, String dpNo) {
		// 获取有效的回访人员或工程人员
		Map<String, String> params = new HashMap<>();
		String newdpNo = null;
		if (StringUtils.isNotBlank(dpNo)) {
//			String tempDpNo = dpNo;
//			if (tempDpNo.length() > 6) {
//				tempDpNo = tempDpNo.substring(0, 6);
//				newdpNo = tempDpNo;
//			}
//			if (tempDpNo.startsWith("16")) {
//				newdpNo = tempDpNo.replaceFirst("16", "31");
//			} else if (tempDpNo.startsWith("31")) {
//				newdpNo = tempDpNo.replaceFirst("31", "16");
//			}
			newdpNo = UserUtil.transferDepNo(dpNo);
		}
		params.put("roleid", String.valueOf(roleId));
		params.put("dpNo", dpNo);
		List<User> userList = userManageService.queryUserWithRoleIdAndDpNo(params);
		
		if (userList.isEmpty()) {
			params.remove("dpNo");
			params.put("areaPower", dpNo);
			userList = userManageService.queryUserWithRoleIdAndDpNoOrInAreaPower(params);
		}
		// 如果没找到，则查找转换后的部门对应觉得人员
		if (userList.isEmpty() && StringUtils.isNotBlank(newdpNo) && !newdpNo.equals(dpNo)) {
			params.clear();
			params.put("roleid", String.valueOf(roleId));
			params.put("dpNo", newdpNo);
			userList = userManageService.queryUserWithRoleIdAndDpNo(params);
			
			if (userList.isEmpty()) {
				params.remove("dpNo");
				params.put("areaPower", newdpNo);
				userList = userManageService.queryUserWithRoleIdAndDpNoOrInAreaPower(params);
			}
		}
		List<String> nextAssignPer = new ArrayList<>(userList.size());
		List<String> nextAssignName = new ArrayList<>(userList.size());
		List<String> nextAssignEmail = new ArrayList<>(userList.size());
		for (User userObj : userList) {
			nextAssignPer.add(userObj.getUsername());
			nextAssignName.add(userObj.getUsername() + "-" + userObj.getRealName());
			nextAssignEmail.add(userObj.getEmail());
		}
		
		if (nextAssignPer.size() <= 0) {
			throw new SubcontractException("获取下一级审核人员出错");
		}
		String[] nextAssigen = new String[] { 
			StringUtils.join(nextAssignPer, ","),
			StringUtils.join(nextAssignName, ","), 
			StringUtils.join(nextAssignEmail, ";"),
			String.valueOf(nextAssignPer.size())
		};
		return nextAssigen;
	}

	/**
	 * 服务商转包下单通知
	 * 
	 * @param subcontractProject
	 */
	private void notifyFacilitator(SubcontractProject subcontractProject) {
		Integer facilitatorId = subcontractProject.getFacilitatorId();
		if (facilitatorId == null || facilitatorId == 0) {
			return;
		}
		SubcontractFacilitator facilitator = this.selectSubcontractFacilitatorById(facilitatorId);
		if (facilitator == null || StringUtils.isBlank(facilitator.getEmail())
				|| StringUtils.isBlank(facilitator.getReceiver())) {
			return;
		}

		SubcontractProjectVO subcontractVO;
		if (subcontractProject instanceof SubcontractProjectVO) {
			subcontractVO = (SubcontractProjectVO) subcontractProject;
		} else {
			subcontractVO = this.selectSubcontractProjectVOById(subcontractProject.getId());
		}

		HashMap<String, Object> context = new HashMap<>();
		context.put("templateCode", SubcontractConstant.SubcontractTemplate.FACILITATOR_NOTIFY_CODE);
		context.put("tos", facilitator.getEmail());
		String serviceEmails = userManageService.queryMailsByRoleAndOfficeCodes(subcontractVO.getOfficeCode(),
				MessageUtil.ROLE_SERVICEMANAGER);
		String emEmails = userManageService.queryMailsByRoleAndOfficeCodes(null, MessageUtil.ROLE_ENGINEEMANAGER);
		context.put("ccs", serviceEmails + ";" + emEmails);
		context.put("facilitatorReceiver", facilitator.getReceiver());
		context.put("officeName", subcontractVO.getOfficeName());
		context.put("subcontractName", subcontractVO.getSubcontractName());
		context.put("typeName", subcontractVO.getTypeName());
		context.put("subcontractAmount", subcontractVO.getSubcontractAmount());
		List<Project> projectList = this.queryProjectList(subcontractVO);
		context.put("projectName", initProjectDetailTable(projectList));
		NotificationTemplateUtil.keepMail(context);
	}

	/**
	 * 将项目转包的所包含的项目转变成一个Table
	 * 
	 * @param projectList
	 * @return
	 */
	private String initProjectDetailTable(List<Project> projectList) {
		StringBuilder projectDetailHtml = new StringBuilder("");
		if (!projectList.isEmpty()) {
			/*
			 * projectDetailHtml.append(
			 * "<table id='projectdisplaytable' style='text-align: left;margin-bottom:0;width:100%;font-size:11pt;' class='displaytable table table-condensed table-hover table-striped'><thead><tr><th>项目名称</th><th>合同号</th></tr></thead><tbody>"
			 * ); for (Project project : projectList) {
			 * projectDetailHtml.append(
			 * "<tr class='odd'><td class='transferProjectName'><span>"
			 * ).append(project.getProjectCode()) .append(
			 * "</span><br><span style='color:blue;'>"
			 * ).append(project.getProjectName()).append("</span></td>").append(
			 * "<td class='transferContractNo'>"
			 * ).append(project.getContractNo()).append("</td></tr>"); }
			 * projectDetailHtml.append("</tbody></table>");
			 */
			for (Project project : projectList) {
				projectDetailHtml.append("<p style='font-size: 11pt; font-family: 宋体;'>")
						.append(project.getProjectName()).append("</p>");
			}
		}
		return projectDetailHtml.toString();
	}
}
