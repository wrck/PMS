package com.dp.plat.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.activiti.engine.TaskService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.BeanUtils;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dp.plat.context.SpringContext;
import com.dp.plat.context.UserContext;
import com.dp.plat.dao.ProjectDao;
import com.dp.plat.dao.ProjectDaoImpl;
import com.dp.plat.data.bean.CallBack;
import com.dp.plat.data.bean.Contract;
import com.dp.plat.data.bean.Department;
import com.dp.plat.data.bean.Instruction;
import com.dp.plat.data.bean.Item;
import com.dp.plat.data.bean.MailContent;
import com.dp.plat.data.bean.MailSenderInfo;
import com.dp.plat.data.bean.Notification;
import com.dp.plat.data.bean.NotificationTemplate;
import com.dp.plat.data.bean.OrderDataFromSap;
import com.dp.plat.data.bean.Product;
import com.dp.plat.data.bean.Project;
import com.dp.plat.data.bean.ProjectDeliver;
import com.dp.plat.data.bean.ProjectMember;
import com.dp.plat.data.bean.ProjectPlanEvent;
import com.dp.plat.data.bean.ProjectTask;
import com.dp.plat.data.bean.ProjectWeekly;
import com.dp.plat.data.bean.ShipmentInfo;
import com.dp.plat.data.bean.SoftChangeLog;
import com.dp.plat.data.bean.User;
import com.dp.plat.data.bean.WeeklyContent;
import com.dp.plat.data.bean.WeeklyFeedback;
import com.dp.plat.maintenance.entity.ProjectMaintenance;
import com.dp.plat.maintenance.vo.ProjectMaintenanceVO;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.param.Person;
import com.dp.plat.param.RealProductLineBean;
import com.dp.plat.supervision.entity.ProjectSupervision;
import com.dp.plat.supervision.vo.ProjectSupervisionVO;
import com.dp.plat.util.ActivityMessage;
import com.dp.plat.util.DateUtil;
import com.dp.plat.util.MailHandleUtil;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.NotificationTemplateUtil;
import com.dp.plat.util.ProjectUtils;
import com.dp.plat.util.StringEscUtil;
import com.dp.plat.util.UploadFileUtil;
import com.dp.plat.util.Util;
import com.dp.plat.util.WordUtil;

public class ProjectServiceImpl extends BaseServiceImpl implements ProjectService {
	protected ProjectDao projectDao;
	protected BasicDataService basicDataService;
	protected CallBackService callBackService;
	protected PmClosedLoopService pmClosedLoopService;
	protected TaskService taskService;
	protected SendMailService sendMailService;

	public void setBasicDataService(BasicDataService basicDataService) {
		this.basicDataService = basicDataService;
	}

	public void setProjectDao(ProjectDao projectDao) {
		this.projectDao = projectDao;
	}

	public void setCallBackService(CallBackService callBackService) {
		this.callBackService = callBackService;
	}

	/**
     * @param pmClosedLoopService the pmClosedLoopService to set
	 */
	public void setPmClosedLoopService(PmClosedLoopService pmClosedLoopService) {
		this.pmClosedLoopService = pmClosedLoopService;
	}

	/**
     * @param taskService the taskService to set
	 */
	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	public void setSendMailService(SendMailService sendMailService) {
		this.sendMailService = sendMailService;
	}

	@Override
	public List<Project> queryProjectList(Project project, DisplayParam displayParam) {
		log("查看项目管理");
		return projectDao.queryProjectList(project, displayParam);
	}

	@Override
	public int insertProject(Project project) throws Exception {
		log("创建项目");
		Project p = this.queryProjectByContractNo(project.getContractNo());
		project = putProperties(project, p);// p中的部分属性放置到project中
		if (project.getColumn008() != null && !"".equals(project.getColumn008())) {
			project.setProjectState(MessageUtil.PROJECT_STATE_DENY);
			project.setIsback(MessageUtil.PROJECT_CREATE_STATE40);
		} else {
			// 如服务经理为空，则项目状态变更为“待指定服务经理”
			if ("".equals(project.getServiceManagerCode()) && "".equals(project.getProgramManagerCode())) {
				project.setProjectState(MessageUtil.PROJECT_STATE_30);// 已创建
			}
			// 若项目经理为空，则项目状态变更为 “待指派项目经理”
			if (!"".equals(project.getServiceManagerCode()) && "".equals(project.getProgramManagerCode())) {
				project.setProjectState(MessageUtil.PROJECT_STATE_31);// 已创建
			}
			// 如均不为空，则项目状态变更为“已指派项目经理”
			if (!"".equals(project.getServiceManagerCode()) && !"".equals(project.getProgramManagerCode())) {
				project.setProjectState(MessageUtil.PROJECT_STATE_32);// 已创建
			}
			project.setIsback(MessageUtil.PROJECT_CREATE_STATE30);
		}
		Integer pid = projectDao.insertProject(project);// 插入到表pm_project_header
														// - 项目表
		
		String projectGroupCode = project.getProjectGroupCode();
		if (projectGroupCode == null) {
			// project.setProjectType(0);//页面传递过来，无需在此编写hard code
            // FIXME 并发情况下，会获取到相同的组编码，造成错误
			// 查询最大的组编码
			projectGroupCode = projectDao.queryMaxProjectGroupCode();
			String pre = MessageUtil.PROJECT_GROUPCODE_PRE;// 组编码前缀
			// 如果查询的组编码为空，则置为prj_gp1，否则置为最大值+1
			projectGroupCode = ((projectGroupCode == null) ? (pre + "1")
					: pre + (Integer.valueOf(projectGroupCode.replace(pre, "")) + 1));
			project.setProjectGroupCode(projectGroupCode);
		}
        if (StringUtils.isBlank(project.getSmsProjectCode())) {
            String projectCode = project.getProjectCode();
            project.setSmsProjectCode(projectCode.substring(0, projectCode.indexOf("-")));
        }
		projectDao.insertProjectGroup(project);// 插入到表pm_project_group - 项目组信息表
		projectDao.insertProjectContract(project);// 插入到表pm_project_contract -
													// 项目合同关联表
		projectDao.insertProjectGroupRelationship(project);// 插入到表pm_project_group_relationship
															// - 项目组与项目关联表
		int shipmentState = projectDao.queryProjectShipmentState(project.getProjectId());
		project.setShipmentState(shipmentState);
		project.setProjectPlanState(MessageUtil.PROJECT_PLAN_STATE_40);// 尚未制定计划
		this.insertOrUpdateProjectState(project);// 插入或更新项目状态表

		project.setProjectId(pid);// 保存的项目表id
		project.setMemberRole(MessageUtil.DATATYPE_CODE03_20);// 03-20
		project.setMemberCode(project.getServiceManagerCode());
		project.setMemberName(project.getServiceManagerCodeforjson());
		project.setFromFlag(MessageUtil.FLAG_FROM_PROJECT);
		project.setEmail(this.getMails(project.getServiceManagerCode()));// 查询邮件
		this.insertProjectMember(project);// 插入到表pm_project_member - 项目成员表

		project.setMemberRole(MessageUtil.DATATYPE_CODE03_10);// 03-10
		project.setMemberCode(project.getSalesManCode());
		project.setMemberName(project.getSalesManName());
		try {
			//project.setEmail(this.getMails(project.getSalesManCode()));
			project.setEmail(this.queryMailByUserNameFromOA(project.getSalesManCode()));
		} catch (Exception e) {
		    project.setEmail(null);
		    e.printStackTrace();
		}
		this.insertProjectMember(project);// 插入到表pm_project_member - 项目成员表

		// 保存项目经理信息
		if (StringUtils.isNotBlank(project.getProgramManagerCode())) {
			project.setMemberRole(MessageUtil.DATATYPE_CODE03_30);// 03-30
			project.setMemberCode(project.getProgramManagerCode());
			project.setMemberName(project.getProgramManagerCodeforjson());
			project.setFromFlag(MessageUtil.FLAG_FROM_PROJECT);
			project.setEmail(this.getMails(project.getProgramManagerCode()));// 查询邮件
			this.insertProjectMember(project);// 插入到表pm_project_member - 项目成员表
		}
		if (StringUtils.isNotBlank(project.getProgramManagerCodeB())) {
			project.setMemberCode(project.getProgramManagerCodeB());
			project.setMemberName(project.getProgramManagerCodeforjsonB());
			// 项目经理B归为从成员信息添加，便于区分
			project.setFromFlag(MessageUtil.FLAG_FROM_MEMBER);
			project.setEmail(this.getMails(project.getProgramManagerCodeB()));// 查询邮件
			this.insertProjectMember(project);// 插入到表pm_project_member - 项目成员表
		}

		// 保存产品信息
		List<OrderDataFromSap> orderDataList = this.queryOrderLineFromSapByContractNo(project);
		for (OrderDataFromSap od : orderDataList) {
			od.setProjectId(pid);
			projectDao.insertProjectProductLine(od);
		}

		// 不予跟踪邮件
		if (MessageUtil.PROJECT_STATE_DENY.equals(project.getProjectState())) {
			Map<String, Object> context = new HashMap<String, Object>();

			context.put("templateCode", MessageUtil.NOTIFICATION_CODE_DENY_PRJ);
			context.put("username", getRealname() == null ? "" : getRealname());
			context.put("projectName", project.getProjectName());
			if (project.getServiceManagerCode() != null) {// 选择不予跟踪，确认有服务经理才发送通知
				context.put("tos", basicDataService.querySysArg(MessageUtil.GCGLB)
						+ this.getMails(project.getServiceManagerCode()));
				NotificationTemplateUtil.keepMail(context);
			}

			// 增加通知信息
			/*
			 * List<String> objs = new ArrayList<String>();
			 * objs.add(project.getServiceManagerCode()); Map<String, Object>
			 * params = new HashMap<String, Object>();
			 * params.put("templateCode",
			 * MessageUtil.NOTIFICATION_CODE_102);//templateCode
			 * params.put("objs", objs); params.put("projectId", pid);
			 * params.put("projectName", project.getProjectName());
			 * params.put("username",
			 * UserContext.getUserContext().getUser().getRealName());
			 * NotificationTemplateUtil.KeepNotification(params);
			 */
			this.addFixedNotification(MessageUtil.NOTIFICATION_CODE_102, pid);

			// 更新项目闭环时间
			projectDao.updateProjectDirectCloseTime(pid);
			this.updateProjectCloseProcessState(pid, MessageUtil.PROJECT_CLOSE_PROCESS_STATE_50);
		} else {
			// 增加通知信息
			/*
			 * List<String> objs = new ArrayList<String>();
			 * objs.add(project.getServiceManagerCode()); Map<String, Object>
			 * params = new HashMap<String, Object>();
			 * params.put("templateCode",
			 * MessageUtil.NOTIFICATION_CODE_101);//templateCode
			 * params.put("objs", objs); params.put("projectId", pid);
			 * params.put("projectName", project.getProjectName());
			 * params.put("username",
			 * UserContext.getUserContext().getUser().getRealName());
			 * NotificationTemplateUtil.KeepNotification(params);
			 */

			this.addFixedNotification(MessageUtil.NOTIFICATION_CODE_101, pid);
		}
		this.updateChannel(project);// 更新渠道信息
		return pid;
	}

	@Override
	public void insertBatchProject(Project project, int batchFunc) throws Exception {
		log("创建项目");
		Project p = this.queryProjectByContractNo(project.getContractNo());
		project = putProperties(project, p);// p中的部分属性放置到project中
		if (batchFunc == 1) {// 直接闭环
			project.setProjectState(MessageUtil.PROJECT_STATE_CLOSEDLOOP);
		} else if (batchFunc == 2) {// 进行中的项目--指定服务经理
			project.setProjectState(MessageUtil.PROJECT_STATE_30);
			project.setIsback(MessageUtil.PROJECT_CREATE_STATE30);
		} else if (batchFunc == 3) {// 进行中的项目--指定项目经理+服务经理
			project.setProjectState(MessageUtil.PROJECT_STATE_30);
			project.setIsback(MessageUtil.PROJECT_CREATE_STATE34);
		}
        project.setSmsProjectCode(project.getProjectCode());
		project.setProjectCode(project.getProjectCode() + "-0");// sms项目编码追加编成新的编码方式
		Integer pid = projectDao.insertProject(project);// 插入到表pm_project_header
														// - 项目表

		String projectGroupCode = project.getProjectGroupCode();
		if (projectGroupCode == null) {
			// project.setProjectType(0);//页面传递过来，无需在此编写hard code
			// 查询最大的组编码
			projectGroupCode = projectDao.queryMaxProjectGroupCode();
			String pre = MessageUtil.PROJECT_GROUPCODE_PRE;// 组编码前缀
			// 如果查询的组编码为空，则置为prj_gp1，否则置为最大值+1
			projectGroupCode = ((projectGroupCode == null) ? (pre + "1")
					: pre + (Integer.valueOf(projectGroupCode.replace(pre, "")) + 1));
			project.setProjectGroupCode(projectGroupCode);
		}
		projectDao.insertProjectGroup(project);// 插入到表pm_project_group - 项目组信息表
		projectDao.insertProjectContract(project);// 插入到表pm_project_contract -
													// 项目合同关联表
		projectDao.insertProjectGroupRelationship(project);// 插入到表pm_project_group_relationship
															// - 项目组与项目关联表

		// 保存销售信息
		project.setMemberRole(MessageUtil.DATATYPE_CODE03_10);// 03-10
		project.setMemberCode(project.getSalesManCode());
		project.setMemberName(project.getSalesManName());
		try {
			//project.setEmail(this.getMails(project.getSalesManCode()));
			project.setEmail(this.queryMailByUserNameFromOA(project.getSalesManCode()));
		} catch (Exception e) {
			project.setEmail(null);
		}
		project.setFromFlag(MessageUtil.FLAG_FROM_PROJECT);
		this.insertProjectMember(project);// 插入到表pm_project_member - 项目成员表
		if (batchFunc == 2) {
			// 保存服务经理信息
			project.setMemberRole(MessageUtil.DATATYPE_CODE03_20);// 03-10
			project.setMemberCode(project.getServiceManagerCode());
			project.setMemberName(project.getServiceManagerCodeforjson());
			project.setFromFlag(MessageUtil.FLAG_FROM_PROJECT);
			this.insertProjectMember(project);// 插入到表pm_project_member - 项目成员表
		}
		if (batchFunc == 3) {
			// 保存服务经理信息
			project.setMemberRole(MessageUtil.DATATYPE_CODE03_20);// 03-10
			project.setMemberCode(project.getServiceManagerCode());
			project.setMemberName(project.getServiceManagerCodeforjson());
			project.setFromFlag(MessageUtil.FLAG_FROM_PROJECT);
			this.insertProjectMember(project);// 插入到表pm_project_member - 项目成员表

			// 保存项目经理信息
			project.setMemberRole(MessageUtil.DATATYPE_CODE03_30);// 03-30
			project.setMemberCode(project.getProgramManagerCode());
			project.setMemberName(project.getProgramManagerCodeforjson());
			project.setFromFlag(MessageUtil.FLAG_FROM_PROJECT);
			project.setEmail(this.getMails(project.getProgramManagerCode()));// 查询邮件
			this.insertProjectMember(project);// 插入到表pm_project_member - 项目成员表

			if (StringUtils.isNotBlank(project.getProgramManagerCodeB())) {
				project.setMemberCode(project.getProgramManagerCodeB());
				project.setMemberName(project.getProgramManagerCodeforjsonB());
				// 项目经理B归为从成员信息添加，便于区分
				project.setFromFlag(MessageUtil.FLAG_FROM_MEMBER);
				project.setEmail(this.getMails(project.getProgramManagerCodeB()));// 查询邮件
				this.insertProjectMember(project);// 插入到表pm_project_member -
													// 项目成员表
			}
		}
		// 保存产品信息
		List<OrderDataFromSap> orderDataList = this.queryOrderLineFromSapByContractNo(project);
		for (OrderDataFromSap od : orderDataList) {
			od.setProjectId(pid);
			projectDao.insertProjectProductLine(od);
		}
	}

	@Override
	public List<OrderDataFromSap> queryOrderLineFromSapByContractNo(Project project) {
		return projectDao.queryOrderLineFromSapByContractNo(project);
	}

	protected void insertProjectMember(Project project) {
		String membercode = project.getMemberCode();
		if (project.getEmail() != null && "".equals(project.getEmail())) {// 邮件不为空则，查询OA数据表
			if (membercode != null && !"".equals(membercode)) {// 如果编码不为空，则查找pm_person_from_oa中的信息
				Person p = projectDao.queryPersonFromOaByCode(membercode.substring(1));// 查询pm_person_from_oa的号码和邮箱字段
				if (p != null) {
					if (p.getSalesmanTel() != null) {
						project.setPhoneNum(p.getSalesmanTel().replaceAll("\\s", ""));
					}
					project.setEmail(p.getSalesmanMail());
				}
			}
		}
		ProjectMember member = new ProjectMember();
		BeanUtils.copyProperties(project, member, new String[] {"id", "effectiveFrom", "effectiveTo"});
		member.setEffectiveFrom(new Date());
		String createBy = StringUtils.defaultIfBlank(project.getUpdateBy(), getLoginName());
		member.setCreateBy(createBy);
		member.setCreateTime(new Date());
		projectDao.insertProjectMember(member);
		//projectDao.insertProjectMember(project);
	}

	protected Project putProperties(Project project, Project p) {
		if (p == null) {
			return project;
		}
		project.setProjectName(p.getProjectName());
		if (StringUtils.isEmpty(project.getProjectType())) {
			project.setProjectType(p.getProjectType());
		}
		project.setSalesManCode(p.getSalesManCode());
		project.setSalesManName(p.getSalesManName());
		project.setColumn001(p.getColumn001());
		project.setColumn002(p.getColumn002());
		project.setColumn003(p.getColumn003());
		project.setColumn004(p.getColumn004());
		project.setColumn005(p.getColumn005());
		project.setColumn006(p.getColumn006());
		project.setColumn007(p.getColumn007());
		if (StringUtils.isEmpty(project.getColumn008())) {
			project.setColumn008(p.getColumn008());
		}
		project.setColumn009(p.getColumn009());
        if (StringUtils.isBlank(project.getSalesType())) {
            project.setSalesType(p.getSalesType());
        }
        if (StringUtils.isBlank(project.getAgentChannel())) {
            project.setAgentChannel(p.getAgentChannel());
        }
		return project;
	}

	@Override
	public void updateProjectByProjectId(Project project) {
		log("更新项目基本信息");

		// project.setProjectState(MessageUtil.PROJECT_CREATE_STATE30);
		project.setUpdateBy(UserContext.getUserContext().getUsername());
		projectDao.updateProjectByProjectId(project);// 更新项目表信息
		project.setDataTypeCode(MessageUtil.DATATYPE_CODE03_20);
		project.setFromFlag(MessageUtil.FLAG_FROM_PROJECT);
		boolean b = this.updateProjectMember(project, project.getServiceManagerCode(),
				project.getServiceManagerCodeforjson());// 更新项目成员表
		// TODO 代码可以合并
		// 增加通知信息
		if (b) {
			this.addFixedNotification(MessageUtil.NOTIFICATION_CODE_103, project.getProjectId());

			// 指定服务经理后，更新项目状态，并且更新项目闭环流程审批表
			String assignee = project.getOldServiceManagerCode();
			String taskId = pmClosedLoopService.queryTaskByBussinessKeyAndUser(project, assignee);
			if (taskId != null) {
				// 更新pm_cl_evaluation_header，下一个审批人
				HashMap<String, String> params = new HashMap<>();
				params.put("oldNextAcceptPerson", project.getOldServiceManagerCode());
				params.put("nextAcceptPerson", project.getServiceManagerCode());
				params.put("nextAcceptPersonName", project.getServiceManagerCodeforjson().split("-")[1]);
				params.put("projectIds", "'" + project.getProjectId() + "'");
				pmClosedLoopService.updateEvaluationHeaderNextAcceptPerson(params);
				taskService.setAssignee(taskId, project.getServiceManagerCode());
			}
			this.updateProjectStateByProjectId(project, MessageUtil.PROJECT_CREATE_STATE30);// 更新项目状态
			this.updateProjectStatus(project.getProjectId(), MessageUtil.PROJECT_STATE_31);// 更新项目状态(projectState)
		}
		String procode = project.getProgramManagerCode();
		boolean a = false;
		if (procode != null && !"".equals(procode)) {
			project.setDataTypeCode(MessageUtil.DATATYPE_CODE03_30);
			project.setOldMemberCode(project.getOldProgramManagerCode());
			a = this.updateProjectMember(project, project.getProgramManagerCode(),
					project.getProgramManagerCodeforjson());// 更新项目成员表
			if (a) {
				this.addFixedNotification(MessageUtil.NOTIFICATION_CODE_104, project.getProjectId());
			}
			// 服务经理发生变更会更新项目状态，这是需要判断项目经理是否已存在，若存在则更新项目状态；若项目经理发生变更也需要更新项目状态
			if (a || b) {// 指定项目经理后，更新指定项目状态，否则项目经理无法操作项目
				this.updateProjectStateByProjectId(project, MessageUtil.PROJECT_CREATE_STATE32);// 更新项目状态
				this.updateProjectStatus(project.getProjectId(), MessageUtil.PROJECT_STATE_32);// 更新想目状态（projectState）
			}
		}
		String procodeB = project.getProgramManagerCodeB();
		boolean c = false;
		if (procodeB != null && !"".equals(procodeB)) {
			project.setDataTypeCode(MessageUtil.DATATYPE_CODE03_30);
			project.setOldMemberCode(project.getOldProgramManagerCodeB());
			project.setFromFlag(MessageUtil.FLAG_FROM_MEMBER);
			c = this.updateProjectMember(project, project.getProgramManagerCodeB(),
					project.getProgramManagerCodeforjsonB());// 更新项目成员表
			if (c) {
				this.addFixedNotification(MessageUtil.NOTIFICATION_CODE_104, project.getProjectId());
			}
			// 服务经理发生变更会更新项目状态，这是需要判断项目经理是否已存在，若存在则更新项目状态；若项目经理发生变更也需要更新项目状态
			if (c || b) {// 指定项目经理后，更新指定项目状态，否则项目经理无法操作项目
				this.updateProjectStateByProjectId(project, MessageUtil.PROJECT_CREATE_STATE32);// 更新项目状态
				this.updateProjectStatus(project.getProjectId(), MessageUtil.PROJECT_STATE_32);// 更新想目状态（projectState）
			}
		}
		// 成功变更项目经理，终止在项目经理手中的闭环申请,回访申请
		if (a || c) {
			terminateProgramManagerActivities(project);
			// 项目流程状态为“闭环结束”的项目更新项目经理，将流程状态改为“项目跟踪”
			if (MessageUtil.PROJECT_CLOSE_PROCESS_STATE_50.equals(project.getCloseProcessState())) {
			    this.updateProjectCloseProcessState(project.getProjectId(), MessageUtil.PROJECT_CLOSE_PROCESS_STATE_10);
			}
		}
		this.updateChannel(project);// 更新渠道信息
		this.updateProjectImplByProjectId(project);// 更新实施方式
	}

	/**
	 * 项目经理更新时，终止在项目经理手中的闭环申请,回访申请
	 * 
	 * @param project
	 */
	public synchronized void terminateProgramManagerActivities(Project project) {
		List<String> taskIds = new ArrayList<String>();
		String assigneeA = project.getOldProgramManagerCode();
		String taskId = pmClosedLoopService.queryTaskByBussinessKeyAndUser(project, assigneeA);
		if (taskId != null) {
			taskIds.add(taskId);
		}
		String assigneeB = project.getOldProgramManagerCodeB();
		taskId = pmClosedLoopService.queryTaskByBussinessKeyAndUser(project, assigneeB);
		if (taskId != null) {
			taskIds.add(taskId);
		}
		List<CallBack> callBacks = queryCallBackRunList(project.getProjectId(), ActivityMessage.FLOW_RUNING);
		for (CallBack callBack : callBacks) {
			taskId = callBack.getTaskId();
			// 存在回访流程，并且回访流程在项目经理环节，终止该回访流程
			if (StringUtils.isNotBlank(taskId)) {
				String assignee = callBack.getTaskAssignee();
				// 回访流程在项目经理手中则终止
				if (assignee.equals(assigneeA) || assigneeB.equals(assigneeB)) {
					taskIds.add(taskId);
					// 更新回访审批状态为驳回
					callBackService.updateCallBackApplyState(callBack.getCallBackId(), ActivityMessage.FLOW_REJECT);
				}
			} else {// 不存在回访流程，则将回访审批状态改为驳回
				callBackService.updateCallBackApplyState(callBack.getCallBackId(), ActivityMessage.FLOW_REJECT);
			}
		}
		ProjectUtils.terminateActivities(taskIds, "项目经理更新终止在待办流程");
	}

	/**
	 * 返回true 做更新操作
	 * 
	 * @param project
	 * @param membercode
	 * @param memberName
	 * @return
	 */
	@Override
	public boolean updateProjectMember(Project project, String membercode, String memberName) {
		// 查询当前生效记录
		project.setProjectType(StringUtils.defaultIfBlank(project.getProjectType(), MessageUtil.PROJECT_TYPE_AFTERSALES));
		project.setMemberRole(project.getDataTypeCode());
		project.setMemberCode(membercode);
		project.setMemberName(memberName);
		Integer count = projectDao.queryProjectMemberCountByProject(project);
		// 如果能查到，说明未更改人员，不做操作，否则插入member表
		if (count == 0) {
			// 这个getMails是根据传进来的的用户名查找邮箱，数据中的username和membercode表达的是同一个意思用户名，
			project.setEmail(this.getMails(membercode));
			
			// 这个才是正真的插入
			projectDao.updateProjectMember(project);// 更新生效的记录即可

			this.insertProjectMember(project);

		}
		return count == 0;
	}

	@Override
	public List<Person> queryPersonList() {
		return projectDao.queryPersonList();
	}

	@Override
	public List<Instruction> queryInstructionList(int projectId) {
		List<Instruction> instructions = projectDao.queryInstructionList(projectId);
		if (instructions != null && instructions.size() > 0) {
			for (Instruction in : instructions) {
				List<Instruction> feedbackList = projectDao.queryFeedbackList(in.getId());
				if (feedbackList != null && feedbackList.size() > 0) {
					in.setFeedbackList(feedbackList);
				}
			}
		}
		return instructions;
	}

	@Override
	public Project queryProjectByContractNo(String contractNo) {
		Project project = projectDao.queryProjectByContractNo(contractNo);
		if (project != null) {
			project.setProjectType(StringUtils.defaultIfBlank(project.getProjectType(), MessageUtil.PROJECT_TYPE_AFTERSALES));
		}
		return project;
//		return projectDao.queryProjectByContractNo(contractNo);
//		return this.queryProjectByContractNoAndType(contractNo, MessageUtil.PROJECT_TYPE_AFTERSALES);
	}
	
	@Override
	public Project queryProjectByContractNoAndType(String contractNo, String projectType) {
		return projectDao.queryProjectByContractNoAndType(contractNo, projectType);
	}

	@Override
	public void insertInstruction(Instruction instruction) {
		log("项目留言");
		projectDao.insertInstruction(instruction);
	}

	@Override
	public Project queryProjectById(int projectId) {
		return projectDao.queryProjectById(projectId);
	}
	
    @Override
    public Project queryProjectSimplifyByProjectId(Integer projectId) {
        return projectDao.queryProjectSimplifyByProjectId(projectId);
    }

	@Override
	@Transactional
	public boolean updateProjectProgramManagerByProjectId(Project project) {
		log("指定项目经理");
		project.setFromFlag(MessageUtil.FLAG_FROM_PROJECT);
		boolean b = false;
		if (!"".equals(project.getProgramManagerCode())) {// 项目经理为空，不做更新其他操作
			b = this.updateProjectMember(project, project.getProgramManagerCode(),
					project.getProgramManagerCodeforjson());// 更新项目成员表
			this.updateProjectStateByProjectId(project, MessageUtil.PROJECT_CREATE_STATE32);

			this.updateProjectStatus(project.getProjectId(), MessageUtil.PROJECT_STATE_32);// 更新项目为待制定工程计划状态

			// 立项通知
			/*
			 * project = this.queryProjectById(project.getProjectId());
			 * List<String> objs = new ArrayList<String>();
			 * objs.add(project.getProgramManagerCode()); Map<String, Object>
			 * params = new HashMap<String, Object>();
			 * params.put("templateCode",
			 * MessageUtil.NOTIFICATION_CODE_104);//templateCode
			 * params.put("objs", objs); params.put("projectId",
			 * project.getProjectId()); params.put("projectName",
			 * this.queryProjectNameByProjectId(project.getProjectId()));
			 * params.put("username",
			 * UserContext.getUserContext().getUser().getRealName());
			 * NotificationTemplateUtil.KeepNotification(params);
			 */

			this.addFixedNotification(MessageUtil.NOTIFICATION_CODE_104, project.getProjectId());
		}
		this.updateChannel(project);// 更新渠道信息
		this.updateProjectImplByProjectId(project);// 更新项目实施方式和最终客户名称
		return b;
	}

	@Override
	@Transactional
	public boolean updateProjectProgramManagerByProjectId(Project project, String type) {
		log("指定项目经理");
		try {
			project.setDataTypeCode(MessageUtil.DATATYPE_CODE03_30);
			project.setUpdateBy(UserContext.getUserContext().getUsername());
			boolean a = false;
			if (!"".equals(project.getProgramManagerCode())) {// 项目经理为空，不做更新其他操作
				project.setOldMemberCode(project.getOldProgramManagerCode());
				project.setFromFlag(MessageUtil.FLAG_FROM_PROJECT);
				a = this.updateProjectMember(project, project.getProgramManagerCode(),
						project.getProgramManagerCodeforjson());// 更新项目成员表
			}
			boolean b = false;
			if (!"".equals(project.getProgramManagerCodeB())) {// 项目经理为空，不做更新其他操作
				project.setOldMemberCode(project.getOldProgramManagerCodeB());
				project.setFromFlag(MessageUtil.FLAG_FROM_MEMBER);
				b = this.updateProjectMember(project, project.getProgramManagerCodeB(),
						project.getProgramManagerCodeforjsonB());// 更新项目成员表
			}
			this.updateChannel(project);// 更新渠道信息
			this.updateProjectImplByProjectId(project);// 更新项目实施方式和最终客户名称

			if (a || b) {// 成功指定项目经理
				NotificationTemplate template = null;
				String oldMemberCodeA = project.getOldProgramManagerCode();
				String oldMemberCodeB = project.getOldProgramManagerCodeB();

				project = this.queryProjectById(project.getProjectId());

				if (MessageUtil.PROJECT_TYPE_NORMAL.equals(project.getColumn010())) {// 普通类
					template = this.queryNotificationTemplate(MessageUtil.NOTIFICATION_CODE_PMNOMINATE_NORMAL);
				} else if (MessageUtil.PROJECT_TYPE_ENGINEE.equals(project.getColumn010())) {// 工程类
					template = this.queryNotificationTemplate(MessageUtil.NOTIFICATION_CODE_PMNOMINATE_ENGINEE);
				}
				String serviceUsername = project.getServiceManagerCode();
				project.setCos(this.getMails(serviceUsername));// 抄送服务经理
				if (a) {
					String programUsername = project.getProgramManagerCode();
					project.setTos(this.getMails(programUsername));// 主送项目经理
					this.keepMailInfo(project, template, project.getProgramManagerCodeforjson());

					// 终止原项目经理的审批流程
					project.setOldMemberCode(oldMemberCodeA);
					ProjectUtils.terminateProgramManagerActivities(project);
				}
				if (b) {
					String programUsernameB = project.getProgramManagerCodeB();
					project.setTos(this.getMails(programUsernameB));
					this.keepMailInfo(project, template, project.getProgramManagerCodeforjsonB());

					// 终止原项目经理的审批流程
					project.setOldMemberCode(oldMemberCodeB);
					ProjectUtils.terminateProgramManagerActivities(project);
				}
				this.updateProjectStateByProjectId(project, MessageUtil.PROJECT_CREATE_STATE32);
				this.updateProjectStatus(project.getProjectId(), MessageUtil.PROJECT_STATE_32);// 更新项目为待制定工程计划状态
				this.addFixedNotification(MessageUtil.NOTIFICATION_CODE_104, project.getProjectId());
			}
			return true;
		} catch (Exception e) {
			project.setErrMess(MessageUtil.SAVE_FAILED);
			e.printStackTrace();
		}
		return false;
	}

	private void keepMailInfo(Project p, NotificationTemplate template, String u) throws Exception {
		if (template != null) {// 如果有模板，则后续保存到邮件表中
			MailSenderInfo info = new MailSenderInfo();
			// 创建替换变量对象，将需要替换的变量置入
			MailContent mc = new MailContent();
			mc.setProjectName(p.getProjectName());
			mc.setUsername(u);
			mc.setOfficeName(p.getOfficeName());
			mc.setBackcase(p.getColumn014());
			info.setSubject(MailHandleUtil.dealwithMail(template.getNotificationSubject(), mc));// 邮件主题替换
			info.setContent(MailHandleUtil.dealwithMail(template.getNotificationContent(), mc));// 邮件内容替换

			String arg = basicDataService.querySysArg("sys.envirment.argu");
			if (arg.equals("0")) {// 测试环境
				info.setTos(StringEscUtil.getText("plat.develop.mail.tos"));
			} else {
				info.setTos(p.getTos());
				info.setCcs(p.getCos());
			}
			sendMailService.keepMailInfo(info);
		}
	}

	@Override
	public void updateProjectDetailByProjectId(Project project) {
		this.updateProjectStateByProjectId(project, MessageUtil.PROJECT_CREATE_STATE34);// 更新项目回退标识
	}

	@Override
	public synchronized void updateChannel(Project p) {
		// 更新出货渠道
		p.setDataTypeCode(MessageUtil.DATATYPE_CODE07_10);
		p.setPartyName(p.getDeliverChannel());
		this.updateProjectRelatedParty(p);

		// 更新服务渠道
		p.setDataTypeCode(MessageUtil.DATATYPE_CODE07_20);
		p.setPartyName(p.getServiceChannel());
		this.updateProjectRelatedParty(p);

		// 更新施工渠道
		p.setDataTypeCode(MessageUtil.DATATYPE_CODE07_30);
		p.setPartyName(p.getAgentChannel());
		this.updateProjectRelatedParty(p);
	}

	protected void updateProjectStateByProjectId(Project p, String prjstate) {
		p.setProjectState(prjstate);// 项目状态更改
		projectDao.updateProjectStateByProjectId(p);
	}

	@Override
	public List<Project> queryProjectListByPower(Project project, DisplayParam displayParam) {
		log("查看项目管理");
		return projectDao.queryProjectListByPower(project, displayParam);
	}

	@Override
	public int insertPorjectWeekly(ProjectWeekly projectWeekly, List<WeeklyContent> workcontentList,
			List<WeeklyContent> riskcontentList, List<WeeklyContent> helpcontentList,
			List<WeeklyContent> progresscontentList, List<WeeklyContent> plancontentList,
			List<WeeklyContent> mailcontentList) {
		if (MessageUtil.WEEKLY_STATE_RAFT == projectWeekly.getWeeklyState()) {
			log("保存周报草稿");
		} else {
			log("提交周报");
		}
		int weeklyId = projectDao.insertProjectWeekly(projectWeekly);

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("weeklyId", weeklyId);
		paramMap.put("createBy", UserContext.getUserContext().getUsername());
		if (workcontentList != null && workcontentList.size() > 0) {
			paramMap.put("optionType", MessageUtil.OPTION_TYPE_WORK);
			paramMap.put("list", handleList(workcontentList));
			projectDao.batchInsertWeeklyContent(paramMap);
		}

		if (riskcontentList != null && riskcontentList.size() > 0) {
			paramMap.put("optionType", MessageUtil.OPTION_TYPE_RISK);
			paramMap.put("list", handleList(riskcontentList));
			projectDao.batchInsertWeeklyContent(paramMap);
		}

		if (helpcontentList != null && helpcontentList.size() > 0) {
			paramMap.put("optionType", MessageUtil.OPTION_TYPE_HELP);
			paramMap.put("list", handleList(helpcontentList));
			projectDao.batchInsertWeeklyContent(paramMap);
		}

		if (progresscontentList != null && progresscontentList.size() > 0) {
			paramMap.put("optionType", MessageUtil.OPTION_TYPE_PROPGRESS);
			paramMap.put("list", handleList(progresscontentList));
			projectDao.batchInsertWeeklyContent(paramMap);
		}
		if (plancontentList != null && plancontentList.size() > 0) {
			paramMap.put("optionType", MessageUtil.OPTION_TYPE_PLAN);
			paramMap.put("list", handleList(plancontentList));
			projectDao.batchInsertWeeklyContent(paramMap);
		}
		if (mailcontentList != null && mailcontentList.size() > 0) {
			paramMap.put("optionType", MessageUtil.OPTION_TYPE_MAIL);
			paramMap.put("list", handleList(mailcontentList));
			projectDao.batchInsertWeeklyContent(paramMap);
		}

		return weeklyId;
	}

	private List<WeeklyContent> handleList(List<WeeklyContent> weeklyContents) {
		Iterator<WeeklyContent> it = weeklyContents.iterator();
		while (it.hasNext()) {
			if (it.next() == null) {
				it.remove();
			}
		}
		return weeklyContents;
	}

	private synchronized void updateProjectRelatedParty(Project project) {
		if (StringUtils.isNotBlank(project.getPartyName())) {
			project.setPartyRole(project.getDataTypeCode());
			projectDao.updateProjectRelatedParty(project);// 更新生效的记录即可

			projectDao.insertProjectRelatedParty(project);
		}
	}

	@Override
	public String queryProjectStateByProjectId(Project project) {
		return projectDao.queryProjectStateByProjectId(project);
	}

	@Override
	public List<OrderDataFromSap> queryOrderDataListByProjectId(int projectId) {
		return projectDao.queryOrderDataListByProjectId(projectId);
	}

	@Override
	public List<ProjectWeekly> queryProjectWeeklyList(int projectId, int weeklyState) {
		return projectDao.queryProjectWeeklyList(projectId, weeklyState);
	}

	@Override
	public ProjectWeekly queryPorjectWeekly(int weeklyId) {
		log("查看项目周报");
		return projectDao.queryPorjectWeekly(weeklyId);
	}

	@Override
	public List<WeeklyContent> queryWeeklyContentList(int weeklyId, int optionType) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("weeklyId", weeklyId);
		paramMap.put("optionType", optionType);
		return projectDao.queryWeeklyContentList(paramMap);
	}

	@Override
	public List<ShipmentInfo> queryShipmentInfoByContractNo(String contractNo, int projectId) {
		return projectDao.queryShipmentInfoByContractNo(contractNo, projectId);
	}
	
	@Override
    public List<ShipmentInfo> queryShipmentInfoByContractNo(String contractNo, int projectId, String profitCenter) {
        return projectDao.queryShipmentInfoByContractNo(contractNo, projectId, profitCenter);
    }

    @Override
	public int queryShipmentInfoSizeByContractNo(String contractNos) {
		return projectDao.queryShipmentInfoSizeByContractNo(contractNos);
	}
    
	@Override
    public int queryShipmentInfoSizeByContractNo(String contractNos, String profitCenter) {
        return projectDao.queryShipmentInfoSizeByContractNo(contractNos, profitCenter);
    }

    @Override
	public List<ShipmentInfo> queryTransferShipmentInfoByContractNo(Project project, int transferProjectId) {
		return projectDao.queryTransferShipmentInfoByContractNo(project, transferProjectId);
	}
	
	@Override
    public List<ShipmentInfo> queryTransferShipmentInfoByContractNo(Project project, int transferProjectId, String profitCenter) {
        return projectDao.queryTransferShipmentInfoByContractNo(project, transferProjectId, profitCenter);
    }

    @Override
	public void deleteShipmentInstallInfoByProjectId(int projectId) {
        projectDao.deleteShipmentInstallInfoByProjectId(projectId);
    }

    @Override
	public void updatePorjectWeekly(ProjectWeekly projectWeekly, List<WeeklyContent> workcontentList,
			List<WeeklyContent> riskcontentList, List<WeeklyContent> helpcontentList,
			List<WeeklyContent> progresscontentList, List<WeeklyContent> plancontentList,
			List<WeeklyContent> mailcontentList) {
		if (MessageUtil.WEEKLY_STATE_RAFT == projectWeekly.getWeeklyState()) {
			log("保存周报草稿");
		} else {
			log("提交周报");
		}

		projectWeekly.setUpdateBy(UserContext.getUserContext().getUsername());
		projectDao.updateProjectWeekly(projectWeekly);

		projectDao.deleteWeeklyContent(projectWeekly.getWeeklyId());

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("weeklyId", projectWeekly.getWeeklyId());
		paramMap.put("createBy", UserContext.getUserContext().getUsername());
		if (workcontentList != null && workcontentList.size() > 0) {
			paramMap.put("optionType", MessageUtil.OPTION_TYPE_WORK);
			paramMap.put("list", handleList(workcontentList));
			projectDao.batchInsertWeeklyContent(paramMap);
		}

		if (riskcontentList != null && riskcontentList.size() > 0) {
			paramMap.put("optionType", MessageUtil.OPTION_TYPE_RISK);
			paramMap.put("list", handleList(riskcontentList));
			projectDao.batchInsertWeeklyContent(paramMap);
		}

		if (helpcontentList != null && helpcontentList.size() > 0) {
			paramMap.put("optionType", MessageUtil.OPTION_TYPE_HELP);
			paramMap.put("list", handleList(helpcontentList));
			projectDao.batchInsertWeeklyContent(paramMap);
		}

		if (progresscontentList != null && progresscontentList.size() > 0) {
			paramMap.put("optionType", MessageUtil.OPTION_TYPE_PROPGRESS);
			paramMap.put("list", handleList(progresscontentList));
			projectDao.batchInsertWeeklyContent(paramMap);
		}
		if (plancontentList != null && plancontentList.size() > 0) {
			paramMap.put("optionType", MessageUtil.OPTION_TYPE_PLAN);
			paramMap.put("list", handleList(plancontentList));
			projectDao.batchInsertWeeklyContent(paramMap);
		}
		if (mailcontentList != null && mailcontentList.size() > 0) {
			paramMap.put("optionType", MessageUtil.OPTION_TYPE_MAIL);
			paramMap.put("list", handleList(mailcontentList));
			projectDao.batchInsertWeeklyContent(paramMap);
		}
	}

	@Override
	public void insertWeeklyFiles(List<WeeklyContent> filecontentList, int weeklyId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("weeklyId", weeklyId);
		paramMap.put("createBy", UserContext.getUserContext().getUsername());
		if (filecontentList != null && filecontentList.size() > 0) {
			paramMap.put("optionType", MessageUtil.OPTION_TYPE_FILE);
			paramMap.put("list", handleList(filecontentList));
			projectDao.batchInsertWeeklyContent(paramMap);
		}
	}

	@Override
	public void deleteFileById(int downFlileId) {
		projectDao.deleteFileById(downFlileId);
	}

	@Override
	public void backToLastStep(int projectId, String projectState, String isback, Map<String, Object> paramMap) {
		// projectDao.backToLastStep(projectId, projectState, isback);//
		// 更新项目不予跟踪状态
		projectDao.updateProjectIsbackByProjectId(projectId, isback, null);
		if (paramMap.get("column008") != null) {// 不予跟踪原因为空，做更新操作
			projectDao.updateServiceProject(paramMap);// 更新项目不予跟踪原因
		}
		if (paramMap.get("channelName") != null) {
			Project p = new Project();
			p.setProjectId((Integer) paramMap.get("projectId"));
			p.setDataTypeCode(MessageUtil.DATATYPE_CODE07_30);
			p.setPartyName(paramMap.get("channelName").toString());
			p.setCreateBy(UserContext.getUserContext().getUsername());
			p.setUpdateBy(UserContext.getUserContext().getUsername());
			this.updateProjectRelatedParty(p);// 更新代理商信息
		}

		Project project = this.queryProjectById(projectId);
		// 通知
		/*
		 * List<String> objs = new ArrayList<String>(); Map<String, Object>
		 * params = new HashMap<String, Object>();
		 */
		// 邮件
		Map<String, Object> content = new HashMap<String, Object>();

		content.put("username", UserContext.getUserContext().getUser().getRealName());
		content.put("projectName", project.getProjectName());
		content.put("split", "$");

		String programMangerCodeB = project.getProgramManagerCodeB();
		String mailProgramMangerB = "";
		if (StringUtils.isNotBlank(programMangerCodeB)) {
			mailProgramMangerB = getMails(programMangerCodeB);
		}
		if (MessageUtil.PROJECT_STATE_DENY.equals(projectState)) {// 不予跟踪
			// objs.add(project.getServiceManagerCode());//项目创建后服务经理肯定不为空
			if (MessageUtil.PROJECT_CREATE_STATE42.equals(isback)) {// 项目/服务经理选择不予跟踪
				content.put("templateCode", MessageUtil.NOTIFICATION_CODE_DENY_PRJ_42);
				String ccs = "";
				ccs = basicDataService.querySysArg(MessageUtil.GCGLB);
				content.put("tos", ccs + getMails(project.getServiceManagerCode()) + ";");
				if (project.getProgramManagerCode() != null && !"".equals(project.getProgramManagerCode())) {
					content.put("tos", ccs + getMails(project.getServiceManagerCode()) + ";"
							+ getMails(project.getProgramManagerCode()) + ";" + mailProgramMangerB);
				}
				/*
				 * params.put("templateCode",
				 * MessageUtil.NOTIFICATION_CODE_109);//templateCode
				 * objs.addAll(this.getUsernames(MessageUtil.ROLE_ENGINEEMANAGER
				 * ));
				 */

				this.addFixedNotification(MessageUtil.NOTIFICATION_CODE_109, project.getProjectId());
				// 更新项目闭环时间
				// projectDao.updateProjectDirectCloseTime(projectId);
			} else if (MessageUtil.PROJECT_CREATE_STATE40.equals(isback)) {// 工程管理部项目创建后选择不予跟踪
				content.put("templateCode", MessageUtil.NOTIFICATION_CODE_DENY_PRJ);
				String ccs = "";
				ccs = basicDataService.querySysArg(MessageUtil.GCGLB);
				content.put("tos", ccs + getMails(project.getServiceManagerCode()));// 工程管理部+服务经理
				if (project.getProgramManagerCode() != null && !"".equals(project.getProgramManagerCode())) {
					content.put("tos", ccs + getMails(project.getServiceManagerCode()) + ";"
							+ getMails(project.getProgramManagerCode()) + ";" + mailProgramMangerB);// +项目经理
					// objs.add(project.getProgramManagerCode());
				}
				// params.put("templateCode",
				// MessageUtil.NOTIFICATION_CODE_102);//templateCode

				this.addFixedNotification(MessageUtil.NOTIFICATION_CODE_102, project.getProjectId());
				// 更新项目闭环时间
				projectDao.updateProjectDirectCloseTime(projectId);
				projectDao.backToLastStep(projectId, projectState, isback);// 更新项目不予跟踪状态
			} else if (MessageUtil.PROJECT_CREATE_STATE30.equals(isback)) {// 工程管理部确认项目经理的不予跟踪
				content.put("templateCode", MessageUtil.NOTIFICATION_CODE_DENY_PRJ_SURE);
				String ccs = "";
				ccs = basicDataService.querySysArg(MessageUtil.GCGLB);
				content.put("tos", ccs + getMails(project.getServiceManagerCode()));
				if (project.getProgramManagerCode() != null && !"".equals(project.getProgramManagerCode())) {
					content.put("tos", ccs + getMails(project.getServiceManagerCode()) + ";"
							+ getMails(project.getProgramManagerCode()) + ";" + mailProgramMangerB);
				}
				/*
				 * params.put("templateCode",
				 * MessageUtil.NOTIFICATION_CODE_110);//templateCode
				 * objs.add(project.getProgramManagerCode());
				 */
				this.addFixedNotification(MessageUtil.NOTIFICATION_CODE_110, project.getProjectId());
				// 更新项目闭环时间
				projectDao.updateProjectDirectCloseTime(projectId);
			} else if (MessageUtil.PROJECT_CREATE_STATE50.equals(isback)) {// 服务经理将不与跟踪的项目返回工程管理部,说明需要跟踪

				content.put("templateCode", MessageUtil.NOTIFICATION_CODE_CONTINUE_PRJ);
				String ccs = "";
				ccs = basicDataService.querySysArg(MessageUtil.GCGLB);
				content.put("tos", ccs + this.getMails(project.getServiceManagerCode()));
				if (project.getProgramManagerCode() != null && !"".equals(project.getProgramManagerCode())) {
					content.put("tos", ccs + this.getMails(project.getServiceManagerCode()) + ";"
							+ getMails(project.getProgramManagerCode()) + ";" + mailProgramMangerB);
				}

				/*
				 * params.put("templateCode",
				 * MessageUtil.NOTIFICATION_CODE_105);//templateCode
				 * objs.addAll(this.getUsernames(MessageUtil.ROLE_ENGINEEMANAGER
				 * ));
				 */

				this.addFixedNotification(MessageUtil.NOTIFICATION_CODE_105, project.getProjectId());
				// 需要继续跟踪项目，清空项目闭环时间
				projectDao.clearProjectDirectCloseTime(projectId);
			}

			// 工程管理部确认项目经理的不予跟踪 或 工程管理部项目创建后选择不予跟踪
			if (MessageUtil.PROJECT_CREATE_STATE30.equals(isback)
					|| MessageUtil.PROJECT_CREATE_STATE40.equals(isback)) {
				termainteActivities(project);
				projectDao.backToLastStep(projectId, projectState, isback);// 更新项目不予跟踪状态
				this.updateProjectCloseProcessState(projectId, MessageUtil.PROJECT_CLOSE_PROCESS_STATE_50);
			}
		} else if (MessageUtil.PROJECT_STATE_30.equals(projectState)) {// 确认继续跟踪
			content.put("templateCode", MessageUtil.NOTIFICATION_CODE_SURE_PRJ);
			String ccs = "";
			ccs = basicDataService.querySysArg(MessageUtil.GCGLB);
			content.put("tos", ccs + getMails(project.getServiceManagerCode()));

			if (project.getProgramManagerCode() != null) {
				content.put("tos", ccs + getMails(project.getServiceManagerCode()) + ";"
						+ getMails(project.getProgramManagerCode()) + ";" + mailProgramMangerB);
				// objs.add(project.getProgramManagerCode());
			}
			/*
			 * params.put("templateCode",
			 * MessageUtil.NOTIFICATION_CODE_106);//templateCode
			 * objs.add(project.getServiceManagerCode());
			 */
			this.addFixedNotification(MessageUtil.NOTIFICATION_CODE_106, project.getProjectId());
			projectDao.backToLastStep(projectId, projectState, isback);
			this.updateProjectCloseProcessState(projectId, MessageUtil.PROJECT_CLOSE_PROCESS_STATE_10);
		}
		/*
		 * NotificationTemplateUtil.keepMail(content);
		 * params.put("objs", objs); 
		 * params.put("projectId", project.getProjectId()); 
		 * params.put("projectName", project.getProjectName()); 
		 * params.put("username", UserContext.getUserContext().getUser().getRealName());
		 * NotificationTemplateUtil.KeepNotification(params);
		 */
	}

	@Override
	public List<ProjectPlanEvent> queryProjectPlanEventByProject(Project project) {
		boolean isIMPL_WAY_1 = String.valueOf(MessageUtil.IMPL_WAY_1).equals(project.getColumn012());
		String column010 = project.getColumn010();
		if (isIMPL_WAY_1 && "20".equals(project.getColumn011())) {
			project.setColumn010(null);
		}
		List<ProjectPlanEvent> projectPlanEvents = projectDao.queryProjectPlanEventByProject(project);
		project.setColumn010(column010);
		return projectPlanEvents;
	}

	@Override
    public int queryNeededUndelivedCount(Project project) {
	    boolean isIMPL_WAY_1 = String.valueOf(MessageUtil.IMPL_WAY_1).equals(project.getColumn012());
        ProjectDeliver projectDeliver = new ProjectDeliver();
        projectDeliver.setProjectId(project.getProjectId());
        projectDeliver.setColumn010(project.getColumn010());
        projectDeliver.setColumn011(project.getColumn011());
        projectDeliver.setDataTypeCode(MessageUtil.BASIC_DATA_PRJ_PHASE);
        if (isIMPL_WAY_1 && "20".equals(project.getColumn011())) {
            projectDeliver.setColumn010(null);
        }
        return projectDao.queryNeededUndelivedCount(projectDeliver);
    }
	
	@Override
    public List<ProjectDeliver> queryNeededUndelivedProjectDeliverList(Project project) {
        boolean isIMPL_WAY_1 = String.valueOf(MessageUtil.IMPL_WAY_1).equals(project.getColumn012());
        ProjectDeliver projectDeliver = new ProjectDeliver();
        projectDeliver.setProjectId(project.getProjectId());
        projectDeliver.setColumn010(project.getColumn010());
        projectDeliver.setColumn011(project.getColumn011());
        projectDeliver.setDataTypeCode(MessageUtil.BASIC_DATA_PRJ_PHASE);
        if (isIMPL_WAY_1 && "20".equals(project.getColumn011())) {
            projectDeliver.setColumn010(null);
        }
        return projectDao.queryNeededUndelivedProjectDeliverList(projectDeliver);
    }
	

    @Override
	public List<ProjectMember> queryProjectMembers(int projectId) {
		return projectDao.queryProjectMembers(projectId);
	}

	@Override
	public int insertProjectMember(ProjectMember member) {
		log("创建项目组成员");
		member.setFromFlag(MessageUtil.FLAG_FROM_MEMBER);
		return projectDao.insertProjectMember(member);
	}

	@Override
	public void updateProjectMember(ProjectMember member) {
		projectDao.updateProjectMember(member);
	}

	@Override
	public void editProjectPlan(ProjectTask projectTask) {
		log("更新项目计划");
		// 先将生效记录置为失效，再保存新记录
		projectDao.updateProjectPlanByProjectId(projectTask);
		String[] eventKeyStr = projectTask.getEventKeyStr().split(",");
		String[] contractNos = projectTask.getContractNoStr().split(",");
		String visibleFlagStr = projectTask.getVisibleFlag();
		String[] visibleFlags;
		if (visibleFlagStr != null) {
			visibleFlags = projectTask.getVisibleFlag().split(",");
		} else {
			visibleFlags = new String[0];
		}
		String con = projectTask.getContractNo();
		boolean b = con == null;
		for (int i = 0; i < eventKeyStr.length; i++) {
			String[] es = eventKeyStr[i].split("-");
			projectTask.setTaskTypeCode(es[0].trim());
			projectTask.setTaskTypeId(es[1].trim());
			Date ed = null, dp = null, ep = null;
			try {
				ed = Util.dateParse(projectTask.getEventPlanHappenDateStr().split(",")[i]);
			} catch (Exception e) {
			}
			projectTask.setEventPlanHappenDate(ed);
			try {
				dp = Util.dateParse(projectTask.getEventPlanHappenDateENGStr().split(",")[i]);
			} catch (Exception e) {
			}
			projectTask.setEventPlanHappenDateENG(dp);
			try {
				ep = Util.dateParse(projectTask.getEventActualFinishDateStr().split(",")[i]);
			} catch (Exception e) {
			}
			projectTask.setEventActualFinishDate(ep);
			String[] consplit = null;
			if (!b) {
				consplit = con.split(",");
			}
			if (con != null && consplit != null && i < consplit.length) {
				projectTask.setVisibleFlag(visibleFlags.length != 0 ? visibleFlags[i].trim() : MessageUtil.TASK_SHOW);
				projectTask.setContractNo(con.split(",")[i].trim());
				projectDao.insertProjectPlan(projectTask);
			} else {
				for (int j = 0; j < contractNos.length; j++) {
					if (j == 0 || ed != null) {// 如果是第一个合同号的计划或者财务收款计划不为空，则置为可见
						projectTask.setVisibleFlag(MessageUtil.TASK_SHOW);
					} else {
						projectTask.setVisibleFlag(MessageUtil.TASK_HIDE);
					}
					projectTask.setContractNo(contractNos[j]);
					projectDao.insertProjectPlan(projectTask);
				}
			}
		}
	}

	@Override
	public List<ProjectTask> queryProjectTaskByProjectId(int projectId) {
		return projectDao.queryProjectTaskByProjectId(projectId);
	}

	@Override
	public List<ProjectDeliver> queryProjectDeliverList(ProjectDeliver projectDeliver) {
		// 非直签 、督导项目 获取的even节点任务basicDataId :80,设置column010为null，查询其上传附件列表
		String column010 = projectDeliver.getColumn010();
		if ("80".equals(projectDeliver.getBasicDataId())) {
			projectDeliver.setColumn010(null);
		}
		List<ProjectDeliver> delivers = projectDao.queryProjectDeliverList(projectDeliver);
		projectDeliver.setColumn010(column010);
		return delivers;
	}

	@Override
	public void insertInstallAddress(String selected, int projectId, String installAddress, String contractNo) {
		String[] contractNos = contractNo.split(",");
		StringBuilder contracts = new StringBuilder();
		for (String no : contractNos) {
			contracts.append("'");
			contracts.append(no);
			contracts.append("',");
		}
		int len = contracts.length();
		if (len > 0) {
			contracts.delete(len - 1, len);
		}
		String[] barcodes = selected.split(",");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("projectId", projectId);
		paramMap.put("installAddress", installAddress);
		paramMap.put("contractNo", contracts.toString());
		paramMap.put("createBy", UserContext.getUserContext().getUsername());
		StringBuilder shipmentIds = new StringBuilder();
		StringBuilder codes = new StringBuilder();
		for (String barcode : barcodes) {
			paramMap.put("barcode", barcode.trim());
			int shipmentId = projectDao.queryProjectShipment(paramMap);
			if (shipmentId != 0) {
				shipmentIds.append(shipmentId);
				shipmentIds.append(",");
			} else {
				codes.append("'");
				codes.append(barcode.trim());
				codes.append("',");
			}
		}
		int l = shipmentIds.length();
		if (l > 0) {
			shipmentIds.delete(l - 1, l);
			paramMap.put("shipmentIds", shipmentIds.toString());
			projectDao.updateProjectShipment(paramMap);
		}

		int s = codes.length();
		if (s > 0) {
			codes.delete(s - 1, s);
			paramMap.put("codes", codes.toString());
			projectDao.insertProjectShipment(paramMap);
		}

	}
	
	@Override
    public void insertInstallAddress(String selected, int projectId, String installAddress, String contractNo, String profitCenter) {
        String[] contractNos = contractNo.split(",");
        StringBuilder contracts = new StringBuilder();
        for (String no : contractNos) {
            contracts.append("'");
            contracts.append(no);
            contracts.append("',");
        }
        int len = contracts.length();
        if (len > 0) {
            contracts.delete(len - 1, len);
        }
        if (StringUtils.isNotBlank(profitCenter)) {
            contractNo = contracts.toString().replaceAll("-L", "");
        } else {
            contractNo = contracts.toString();
        }
        
        String[] barcodes = selected.split(",");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("projectId", projectId);
        paramMap.put("installAddress", installAddress);
        paramMap.put("contractNo", contractNo);
        paramMap.put("profitCenter", profitCenter);
        paramMap.put("createBy", UserContext.getUserContext().getUsername());
        StringBuilder shipmentIds = new StringBuilder();
        StringBuilder codes = new StringBuilder();
        for (String barcode : barcodes) {
            paramMap.put("barcode", barcode.trim());
            int shipmentId = projectDao.queryProjectShipment(paramMap);
            if (shipmentId != 0) {
                shipmentIds.append(shipmentId);
                shipmentIds.append(",");
            } else {
                codes.append("'");
                codes.append(barcode.trim());
                codes.append("',");
            }
        }
        int l = shipmentIds.length();
        if (l > 0) {
            shipmentIds.delete(l - 1, l);
            paramMap.put("shipmentIds", shipmentIds.toString());
            projectDao.updateProjectShipment(paramMap);
        }

        int s = codes.length();
        if (s > 0) {
            codes.delete(s - 1, s);
            paramMap.put("codes", codes.toString());
            projectDao.insertProjectShipment(paramMap);
        }
    }

    @Override
	@Transactional
	public void insertTransferShipment(String selected, Project project, Project transferProject) {
		String[] contractNos = project.getContractNo().split(",");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("transferProjectId", transferProject.getProjectId());
		for (int i = 0; i < contractNos.length; i++) {
			contractNos[i] += "-C";
			paramMap.put("contractNo", contractNos[i]);
			// 向接收设备转移的项目添加，串货合同号，插入pm_project_contract
			projectDao.insertTransferContract(paramMap);
		}
		String transferContractNo = StringUtils.join(contractNos, ",");
		
		User user = UserContext.getUserContext().getUser();
		paramMap.clear();
		paramMap.put("chProjectId", project.getProjectId());
		paramMap.put("chContractNo", project.getContractNo());
		paramMap.put("transferProjectId", transferProject.getProjectId());
		paramMap.put("transferContractNo", transferContractNo);
		paramMap.put("createBy", user.getUsername());
		
		Integer[] projectIds = new Integer[] { project.getProjectId(), transferProject.getProjectId() };
		String[] barcodes = selected.split(",");
		for (Integer tempProjectId : projectIds) {
			paramMap.put("projectId", tempProjectId);
			// 转移标识，默认:-1,转出:1，转入:0
			if (tempProjectId.equals(project.getProjectId())) {
				paramMap.put("transferFlag", 1);
			} else {
				paramMap.put("transferFlag", 0);
			}
			StringBuilder shipmentIds = new StringBuilder();
			StringBuilder codes = new StringBuilder();
			for (String barcode : barcodes) {
				paramMap.put("barcode", barcode.trim());
				int shipmentId = projectDao.queryProjectShipment(paramMap);
				if (shipmentId != 0) {
					shipmentIds.append(shipmentId);
					shipmentIds.append(",");
				} else {
					codes.append("'");
					codes.append(barcode.trim());
					codes.append("',");
				}
			}
			int l = shipmentIds.length();
			if (l > 0) {
				shipmentIds.delete(l - 1, l);
				paramMap.put("shipmentIds", shipmentIds.toString());
				projectDao.updateProjectTransferShipment(paramMap);
			}

			int s = codes.length();
			if (s > 0) {
				codes.delete(s - 1, s);
				paramMap.put("codes", codes.toString());
				projectDao.insertProjectTransferShipment(paramMap);
			}
		}
		
		HashMap<String, Object> params = new HashMap<>();
		params.put("username", user.getRealName());
		params.put("projectName", project.getProjectName());
		params.put("backcase", project.getContractNo());
		params.put("content", selected);
		params.put("instruction", transferProject.getProjectName());
		params.put("deliverName", transferContractNo);
		this.addDynamicNotification(MessageUtil.NOTIFICATION_CODE_120, project.getProjectId(), params);
	}
    
    @Override
    @Transactional
    public void insertTransferShipment(String selected, Project project, Project transferProject, String profitCenter) {
        String contractNo = project.getContractNo();
        if (StringUtils.isNotBlank(profitCenter)) {
            contractNo = contractNo.replaceAll("-L", "");
        }
        String[] contractNos = contractNo.split(",");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("transferProjectId", transferProject.getProjectId());
        for (int i = 0; i < contractNos.length; i++) {
            contractNos[i] += "-C";
            paramMap.put("contractNo", contractNos[i]);
            // 向接收设备转移的项目添加，串货合同号，插入pm_project_contract
            projectDao.insertTransferContract(paramMap);
        }
        String transferContractNo = StringUtils.join(contractNos, ",");
        
        User user = UserContext.getUserContext().getUser();
        paramMap.clear();
        paramMap.put("chProjectId", project.getProjectId());
        paramMap.put("chContractNo", contractNo);
        paramMap.put("transferProjectId", transferProject.getProjectId());
        paramMap.put("transferContractNo", transferContractNo);
        paramMap.put("createBy", user.getUsername());
        
        Integer[] projectIds = new Integer[] { project.getProjectId(), transferProject.getProjectId() };
        String[] barcodes = selected.split(",");
        for (Integer tempProjectId : projectIds) {
            paramMap.put("projectId", tempProjectId);
            // 转移标识，默认:-1,转出:1，转入:0
            if (tempProjectId.equals(project.getProjectId())) {
                paramMap.put("transferFlag", 1);
            } else {
                paramMap.put("transferFlag", 0);
            }
            StringBuilder shipmentIds = new StringBuilder();
            StringBuilder codes = new StringBuilder();
            for (String barcode : barcodes) {
                paramMap.put("barcode", barcode.trim());
                int shipmentId = projectDao.queryProjectShipment(paramMap);
                if (shipmentId != 0) {
                    shipmentIds.append(shipmentId);
                    shipmentIds.append(",");
                } else {
                    codes.append("'");
                    codes.append(barcode.trim());
                    codes.append("',");
                }
            }
            int l = shipmentIds.length();
            if (l > 0) {
                shipmentIds.delete(l - 1, l);
                paramMap.put("shipmentIds", shipmentIds.toString());
                projectDao.updateProjectTransferShipment(paramMap);
            }

            int s = codes.length();
            if (s > 0) {
                codes.delete(s - 1, s);
                paramMap.put("codes", codes.toString());
                projectDao.insertProjectTransferShipment(paramMap);
            }
        }
        
        HashMap<String, Object> params = new HashMap<>();
        params.put("username", user.getRealName());
        params.put("projectName", project.getProjectName());
        params.put("backcase", project.getContractNo());
        params.put("content", selected);
        params.put("instruction", transferProject.getProjectName());
        params.put("deliverName", transferContractNo);
        this.addDynamicNotification(MessageUtil.NOTIFICATION_CODE_120, project.getProjectId(), params);
    }

	@Override
	public void insertWeeklyFeedback(Map<String, Object> paramMap) {
		log("回复项目周报");
		projectDao.insertWeeklyFeedback(paramMap);
	}

	@Override
	public List<WeeklyFeedback> queryFeedbackList(int weeklyId) {
		return projectDao.queryWeeklyFeedbackList(weeklyId);
	}

	@Override
	public boolean insertProjectDeliverFiles(ProjectDeliver pd, List<ProjectDeliver> pdlist, String username) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (pdlist != null && pdlist.size() > 0) {
			paramMap.put("uploadUser", username);
			paramMap.put("list", handlePDList(pdlist));
			if (pd != null) {
			    paramMap.put("projectType", pd.getProjectType());
			}
			projectDao.batchInsertDeliverFiles(paramMap);
		}
		return this.updateEventActualFinishDateByTask(pd);
	}

	private Integer queryDeliverDetailCountByProjectDeliver(ProjectDeliver pd) {
		return projectDao.queryDeliverDetailCountByProjectDeliver(pd);
	}
	
	@SuppressWarnings("unused")
    private void updateEventActualFinishDate(ProjectDeliver pd) {
        // 非直签 、督导项目 获取的even节点任务column010为null
        if (StringUtils.isEmpty(pd.getColumn010())) {
            pd.setColumn010(null);
        }
        Integer count = this.queryDeliverDetailCountByProjectDeliver(pd);
        ProjectTask pt = new ProjectTask();
        pt.setProjectId(pd.getProjectId());
        pt.setTaskTypeCode(pd.getDataTypeCode());
        pt.setTaskTypeId(pd.getBasicDataId());
        if (count == 0) {// 如果当前节点下必上传交付件完整，则置当前时间为完成时间
            pt.setEventActualFinishDate(new Date());
        } else {
            pt.setEventActualFinishDate(null);
        }
        projectDao.updateEventActualFinishDateByTask(pt);
	}

	private boolean updateEventActualFinishDateByTask(ProjectDeliver pd) {
		// 判断交付件所属项目类型
		if (pd.getProjectType() != null && !MessageUtil.PROJECT_TYPE_AFTERSALES.equals(pd.getProjectType())) {
			return false;
		}
		// 项目是否存在
		Project project = projectDao.queryProjectById(pd.getProjectId());
		if (project == null || project.getProjectId() == 0) {
			return false;
		}
		// 非直签 、督导项目 获取的even节点任务column010为null
		if (StringUtils.isEmpty(pd.getColumn010())) {
			pd.setColumn010(null);
		}
		Integer count = this.queryDeliverDetailCountByProjectDeliver(pd);
		ProjectTask pt = new ProjectTask();
		pt.setProjectId(pd.getProjectId());
		pt.setTaskTypeCode(pd.getDataTypeCode());
		pt.setTaskTypeId(pd.getBasicDataId());
		if (count == 0) {// 如果当前节点下必上传交付件完整，则置当前时间为完成时间
			pt.setEventActualFinishDate(new Date());
		} else {
			pt.setEventActualFinishDate(null);
		}
		projectDao.updateEventActualFinishDateByTask(pt);
		// 更新项目计划状态
		String currentPlan = this.queryProjectCurrentPlan(pd.getProjectId());
		if (currentPlan == null) {
			currentPlan = MessageUtil.PROJECT_PLAN_STATE_47;
		}
		Project temp = new Project();
		temp.setProjectPlanState(currentPlan);
		temp.setProjectId(pd.getProjectId());
		// 如果当前项目能够修改为闭环申请状态
		String closeProcessState = StringUtils.trimToEmpty(project.getCloseProcessState());
		int canCloseLoop = this.canCloseLoop(project);
        if (canCloseLoop == 0 && closeProcessState.compareTo(MessageUtil.PROJECT_CLOSE_PROCESS_STATE_15) <= 0) {
            temp.setCloseProcessState(MessageUtil.PROJECT_CLOSE_PROCESS_STATE_10);
        }
		this.insertOrUpdateProjectState(temp);
		// 如果是运营商直签项目，发起回访申请
		if (MessageUtil.PROJECT_PLAN_STATE_46.equals(currentPlan)) {
//			project = projectDao.queryProjectById(pd.getProjectId());
			if ("运营商市场部".equals(project.getColumn004()) && "10".equals(project.getColumn011())) {
				// 2016-01-06 增加判断是否已经在
				int callbackingSize = projectDao.queryCallBackingSize(pd.getProjectId());
				if (callbackingSize == 0) {
					CallBack callBack = new CallBack();
					callBack.setProjectId(pd.getProjectId());
					callBack.setRemark("运营商直签项目系统自动发起回访流程");
					callBackService.startCallBackFlow(callBack);
					return true;
				}
			}
		}
		return false;
	}

	private Object handlePDList(List<ProjectDeliver> pdlist) {
		Iterator<ProjectDeliver> it = pdlist.iterator();
		while (it.hasNext()) {
			if (it.next() == null) {
				it.remove();
			}
		}
		return pdlist;
	}

	@Override
	public List<ProjectDeliver> queryDeliverDetailByProjectId(int projectId) {
		return projectDao.queryDeliverDetailByProjectId(projectId);
	}
	
	@Override
    public List<ProjectDeliver> queryDeliverDetailByProjectIdAndProjectType(int projectId, String projectTypes) {
        return projectDao.queryDeliverDetailByProjectIdAndProjectType(projectId, projectTypes);
    }
	
    @Override
    public List<ProjectDeliver> queryDeliverDetailByProjectIdAndDeliverType(int projectId, String dataTypeCode) {
        return projectDao.queryDeliverDetailByProjectIdAndDeliverType(projectId, dataTypeCode);
    }

	@Override
	public int deleteDeliverById(int deliverid) {
		projectDao.deleteDeliverById(deliverid);// 交付件置为失效
		ProjectDeliver pd = projectDao.queryProjectDeliverById(deliverid);
		this.updateEventActualFinishDateByTask(pd);

		// 获取交付件类型：
		int deliverTypeId = projectDao.queryDeliverTypeId(deliverid);
		String deliverName = projectDao.queryDeliverName(deliverTypeId);
		// 增加系统通知
		this.addDynamicNotification(MessageUtil.NOTIFICATION_CODE_117, pd.getProjectId(), deliverName);
		return pd.getProjectId();
	}

	@Override
    public void updateProjectIsbackByProjectId(int projectId, String isback, String backCause, String pm, int sendto, String nobackCause) {
	    if (StringUtils.isBlank(nobackCause)) {
	        this.updateProjectIsbackByProjectId(projectId, isback, backCause, pm, sendto);
	    } else {
	        Project project = projectDao.queryProjectById(projectId);
	        if (StringUtils.isNotBlank(project.getServiceManagerCode()) && (StringUtils.isNotBlank(project.getProgramManagerCode()) || StringUtils.isNotBlank(project.getProgramManagerCodeB()))) {
	            this.updateProjectStateByProjectId(project, MessageUtil.PROJECT_CREATE_STATE32);// 更新项目状态
	            this.updateProjectStatus(projectId, MessageUtil.PROJECT_STATE_32);// 更新想目状态（projectState）
	        } else {
	            this.updateProjectStateByProjectId(project, MessageUtil.PROJECT_CREATE_STATE30);// 更新项目状态
	            this.updateProjectStatus(projectId, MessageUtil.PROJECT_STATE_31);// 更新想目状态（projectState）
	        }
	        this.addDynamicNotification(MessageUtil.NOTIFICATION_CODE_107, projectId, "驳回");
	    }
	}
	
	@Override
	public void updateProjectIsbackByProjectId(int projectId, String isback, String backCause, String pm, int sendto) {
		projectDao.updateProjectIsbackByProjectId(projectId, isback, backCause);

		Project p = projectDao.queryProjectById(projectId);
		String mess = null;
		// 增加通知信息
		List<String> objs = new ArrayList<String>();
		if (sendto == 1) {// 回退至工程管理部
			objs.addAll(this.getUsernames(MessageUtil.ROLE_ENGINEEMANAGER));
			mess = MessageUtil.NOTIFICATION_CODE_107;
		} else if (sendto == 2) {// 回退至服务经理
			objs.add(p.getServiceManagerCode());
			mess = MessageUtil.NOTIFICATION_CODE_107;
		} else if (sendto == 3) {// 工程管理部同意回退
			if (p.getProgramManagerCode() == null) {// 如果项目经理为空，则给服务经理消息提示，否则给项目经理消息提示
				objs.add(p.getServiceManagerCode());
			} else {
				objs.add(p.getProgramManagerCode());
			}
			mess = MessageUtil.NOTIFICATION_CODE_108;
			if (pm == null || "".equals(pm)) {// 服务经理为空则项目回退到未创建状态，可以重新创建或合并到其他项目中
				// 处理已有的项目信息，对其数据进行失效操作
				this.invalidProject(projectId);
			}

		} else if (sendto == 4) {// 服务经理同意回退
			objs.add(p.getProgramManagerCode());
			mess = MessageUtil.NOTIFICATION_CODE_108;
		}
		if (sendto == 3 && (pm == null || "".equals(pm))) {// 项目管理部使项目回退到未创建状态，可以重新创建或合并到其他项目中，不发通知信息

		} else {
			/*
			 * Map<String, Object> params = new HashMap<String, Object>();
			 * params.put("templateCode", mess);//templateCode
			 * params.put("objs", objs); params.put("projectId", projectId);
			 * params.put("projectName", p.getProjectName());
			 * params.put("username",
			 * UserContext.getUserContext().getUser().getRealName());
			 * NotificationTemplateUtil.KeepNotification(params);
			 */

			this.addFixedNotification(mess, projectId);
		}
	}

	public void invalidProject(int projectId) {
		projectDao.invalidProjectHeader(projectId);// 项目主表
		projectDao.invalidProjectNotification(projectId);// 项目通知表
		projectDao.invalidProjectGroupRelationship(projectId);// 失效pm_project_group_relationship
		this.updateProjectCloseProcessState(projectId, MessageUtil.PROJECT_CLOSE_PROCESS_STATE_50);
	}

	@Override
	public int insertLog(String handleName, String handleDesc, Integer projectId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("handleName", handleName);
		paramMap.put("handleDesc", handleDesc);
		paramMap.put("projectId", projectId);
		paramMap.put("handleUser", UserContext.getUserContext().getUsername());
		paramMap.put("handleState", 0);
		return projectDao.insertProjecthandleLog(paramMap);
	}

	@Override
	public void updateLog(int handleId, int handleState) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("handleId", handleId);
		paramMap.put("handleState", handleState);
		projectDao.updateProjecthandleLog(paramMap);
	}

	@Override
	public void updateProjectImplByProjectId(Project project) {
		projectDao.updateProjectImplByProjectId(project);
	}

	@Override
	public int queryLastWeeklyId(int projectId) {
		return projectDao.queryLastWeeklyId(projectId);
	}

	private XSSFWorkbook workbook = null;
	private XSSFSheet worksheet = null;
	private String templatefile;

	@Override
	public String createProjectWeeklyExecl(ProjectWeekly projectWeekly, List<WeeklyContent> workcontentList,
			List<WeeklyContent> riskcontentList, List<WeeklyContent> helpcontentList,
			List<WeeklyContent> progresscontentList, List<WeeklyContent> plancontentList) {
		String path = this.getClass().getClassLoader().getResource("").getPath().replace("%20", " ");
		templatefile = path.replace("WEB-INF/classes/", "template/weekly_template.xlsx");
		try {
			projectWeekly = this.queryPorjectWeekly(projectWeekly.getWeeklyId());
			Project project = this.queryProjectById(projectWeekly.getProjectId());
			List<ProjectMember> members = this.queryProjectMembers(projectWeekly.getProjectId());
			User user = UserContext.getUserContext().getUser();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			workbook = new XSSFWorkbook(new FileInputStream(templatefile));
			worksheet = workbook.getSheet("周报");
			XSSFRow row = worksheet.getRow(0);
			XSSFCell cell = row.getCell(0);// 周报标题
			XSSFCellStyle style = cell.getCellStyle();
			cell.setCellValue("项目周报(" + sdf.format(projectWeekly.getWeeklyStartTime()) + "-----"
					+ sdf.format(projectWeekly.getWeeklyEndTime()) + ")");
			cell.setCellStyle(style);

			row = worksheet.getRow(1);
			cell = row.getCell(0);
			style = cell.getCellStyle();
			cell.setCellValue("项目名称：" + project.getProjectName());
			cell = row.getCell(2);
			cell.setCellValue("合同号:" + project.getContractNo());

			row = worksheet.getRow(2);
			cell = row.getCell(0);
			cell.setCellValue("项目经理:" + project.getProgramManagerCodeforjson());

			for (ProjectMember member : members) {
				if (member.getDataState() == 1 && member.getMemberRole().equals(MessageUtil.MEMBER_TECH_MANMER)) {
					row = worksheet.getRow(2);
					cell = row.getCell(2);
					cell.setCellValue("技术经理:" + member.getMemberName());
				}
				if ("3".equals(project.getColumn012())) {
					row = worksheet.getRow(3);
					cell = row.getCell(2);
					cell.setCellValue("服务渠道工程师:" + member.getMemberName());
				}
			}
			row = worksheet.getRow(3);
			cell = row.getCell(0);
			cell.setCellValue("施工代理商工程师:");

			row = worksheet.getRow(5);
			cell = row.getCell(0);
			cell.setCellValue("项目当前阶段:" + projectWeekly.getCurrentTask());

			row = worksheet.getRow(6);
			cell = row.getCell(0);
			cell.setCellValue("本阶段开始时间:" + fmt.format(projectWeekly.getTaskStartTime()));
			cell = row.getCell(2);
			cell.setCellValue("本阶段预计结束时间:" + fmt.format(projectWeekly.getTaskEndTime()));
			String v = getValue(workcontentList);
			row = worksheet.getRow(8);
			cell = row.getCell(0);
			cell.setCellValue(v);
			cell.setCellStyle(style);
			style = cell.getCellStyle();
			style.setWrapText(true);// 设置换行
			row.setHeightInPoints(getExcelCellAutoHeight(v, 20));

			row = worksheet.getRow(10);
			String taskDeviation = projectWeekly.getTaskDeviation();
			if (taskDeviation != null) {
				cell = row.getCell(0);
				cell.setCellValue(taskDeviation);
				style.setWrapText(true);// 设置换行
				row.setHeightInPoints(getExcelCellAutoHeight(taskDeviation, 20));
				style = cell.getCellStyle();
				cell.setCellStyle(style);
			}
			v = getValue(riskcontentList);
			if (v != null) {
				row = worksheet.getRow(12);
				cell = row.getCell(0);
				cell.setCellValue(v);
				style = cell.getCellStyle();
				style.setWrapText(true);// 设置换行
				row.setHeightInPoints(getExcelCellAutoHeight(v, 20));
				cell.setCellStyle(style);
			}

			v = getValue(helpcontentList);
			if (v != null) {
				row = worksheet.getRow(14);
				cell = row.getCell(0);
				cell.setCellValue(v);
				style = cell.getCellStyle();
				style.setWrapText(true);// 设置换行
				row.setHeightInPoints(getExcelCellAutoHeight(v, 20));
				cell.setCellStyle(style);
			}

			v = getValue(progresscontentList);
			if (v != null) {
				row = worksheet.getRow(16);
				cell = row.getCell(0);
				cell.setCellValue(v);
				style = cell.getCellStyle();
				style.setWrapText(true);// 设置换行
				row.setHeightInPoints(getExcelCellAutoHeight(v, 20));
				cell.setCellStyle(style);
			}

			v = getValue(plancontentList);
			if (v != null) {
				row = worksheet.getRow(18);
				cell = row.getCell(0);
				cell.setCellValue(v);
				style = cell.getCellStyle();
				style.setWrapText(true);// 设置换行
				row.setHeightInPoints(getExcelCellAutoHeight(v, 20));
				cell.setCellStyle(style);
			}

			v = projectWeekly.getRemark();
			if (v != null) {
				row = worksheet.getRow(20);
				cell = row.getCell(0);
				cell.setCellValue(v);
				style = cell.getCellStyle();
				style.setWrapText(true);// 设置换行
				row.setHeightInPoints(getExcelCellAutoHeight(v, 20));
				cell.setCellStyle(style);
			}

			String root = ServletActionContext.getServletContext().getRealPath("/");
//			String pathFile = root + "upload/weekly/" + user.getUsername() + "/" + this.getName() + ".xlsx";
//			File file = new File(root + "upload/weekly/" + user.getUsername());
			String pathFile = root + UploadFileUtil.UPLOAD_PATH + "/weekly/" + user.getUsername() + "/" + this.getName() + ".xlsx";
			File file = new File(root + UploadFileUtil.UPLOAD_PATH + "/weekly/" + user.getUsername());
			if (!file.exists()) {
				file.mkdirs();
			}
			File file1 = new File(pathFile);
			FileOutputStream fos = new FileOutputStream(file1);
			worksheet.setPrintGridlines(true);
			workbook.write(fos);
			return pathFile + ",DPtech 技术支援部  " + user.getUsername() + "-" + user.getRealName() + "项目("
					+ project.getProjectName() + ")周报.xlsx";
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getValue(List<WeeklyContent> list) {
		StringBuilder v = new StringBuilder();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				v.append(i + 1);
				v.append(":");
				v.append(list.get(i).getOptionDesc001());
				v.append(";");
				v.append("\n");
			}
			return v.toString();
		}
		return null;
	}

	public static float getExcelCellAutoHeight(String str, float fontCountInline) {
		float defaultRowHeight = 12.00f;// 每一行的高度指定
		float defaultCount = 0.00f;
		for (int i = 0; i < str.length(); i++) {
			float ff = getregex(str.substring(i, i + 1));
			defaultCount = defaultCount + ff;
		}
		if (((int) (defaultCount / fontCountInline) + 1) == 1) {
			return ((int) (defaultCount / fontCountInline) + 2) * defaultRowHeight;
		} else {
			return ((int) (defaultCount / fontCountInline) + 1) * defaultRowHeight;// 计算
		}
	}

	public static float getregex(String charStr) {

		if (charStr == " ") {
			return 0.5f;
		}
		// 判断是否为字母或字符
		if (Pattern.compile("^[A-Za-z0-9]+$").matcher(charStr).matches()) {
			return 0.5f;
		}
		// 判断是否为全角

		if (Pattern.compile("[u4e00-u9fa5]+$").matcher(charStr).matches()) {
			return 1.00f;
		}
		// 全角符号 及中文
		if (Pattern.compile("[^x00-xff]").matcher(charStr).matches()) {
			return 1.00f;
		}
		return 0.5f;

	}

	@Override
	public NotificationTemplate queryNotificationTemplate(String notificationCodeWeeklySubmit) {
		return projectDao.queryNotificationTemplate(notificationCodeWeeklySubmit);
	}

	@Override
	public void updateProjectStatus(int projectId, String projectState) {
		projectDao.backToLastStep(projectId, projectState, null);
	}

	/**
	 * 根据用户名，查找该用户的邮箱
	 */
	@Override
	public String getMails(String username) {
		if (StringUtils.isBlank(username)) {
			return "";
		}
		String email = projectDao.queryMailByUsername(username);
		if (StringUtils.isBlank(email)) {
			email = projectDao.queryMailByUserNameFromOA(username);
		}
		return StringUtils.trimToEmpty(email);
	}

	@Override
	public String getMails(int roleId) {// 根据角色ID获取用户邮箱

		String mails = projectDao.queryMailByRoleId(roleId);
		StringBuilder sb = new StringBuilder();
		if (mails != null && mails.length() > 0) {
			String[] mailArr = mails.split(",");
			for (String mail : mailArr) {
				sb.append(mail);
				sb.append(";");
			}
		}
		return sb.toString();
	}

	@Override
	public List<String> getUsernames(int roleId) {
		String usernames = projectDao.queryUsernamesByroleId(roleId);
		List<String> list = new ArrayList<String>();
		if (usernames != null && usernames.length() > 0) {
			String[] usernameArr = usernames.split(",");
			for (String username : usernameArr) {
				list.add(username);
			}
		}
		return list;
	}

	@Override
	public List<Contract> queryContractList(Map<String, Object> paramMap) {
		return projectDao.queryContractList(paramMap);
	}

	@Override
	public void insertMergeContract(String selected, int projectId) {
		String[] contracts = selected.split(",");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("createBy", UserContext.getUserContext().getUsername());
		paramMap.put("projectId", projectId);
		int taskSize = projectDao.queryProjectTaskSize(projectId);
		for (String contract : contracts) {
			paramMap.put("contractNo", contract.trim());
			projectDao.insertMergeContract(paramMap);
			projectDao.insertMergeProduct(paramMap);

			if (taskSize > 0) {// 已创建工程计划,做复制计划
				projectDao.insertMergeTask(paramMap);
			}
		}
	}

	@Override
	public int insertNewProject(int projectId, String projectCode, List<Product> productList, String mergeBranchMark) {
		int lastCode = projectDao.queryProjectGroupSize(projectCode);
		String newProjectCode = dealWithNew(lastCode, projectCode);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("newProjectCode", newProjectCode);
		paramMap.put("projectCode", projectCode);
		paramMap.put("createBy", UserContext.getUserContext().getUsername());
		paramMap.put("mergeBranchMark", mergeBranchMark);
		projectDao.insertProjectGroup(paramMap);
		paramMap.put("projectId", projectId);
		paramMap.put("projectState", MessageUtil.PROJECT_STATE_30);
		int newProjectId = projectDao.insertProjectInfo(paramMap);

		paramMap.put("newProjectId", newProjectId);
		paramMap.put("memberRole", MessageUtil.MEMBER_SALESMAN);
		projectDao.insertProjectMember(paramMap);

		// 更新产品
		for (Product p : productList) {
			p.setProjectQuantity(p.getProjectQuantity() - p.getBranchQuantity());
			projectDao.updateProjectProduct(p);
		}
		// 插入新项目产品
		paramMap.put("productList", productList);
		projectDao.batchInsertProduct(paramMap);

		return newProjectId;
	}

	private String dealWithNew(int lastCode, String projectCode) {
		return projectCode.substring(0, projectCode.length() - 2) + "-" + lastCode;
	}

	private String dealWith(int lastCode, String projectCode) {
		return projectCode + "-" + lastCode;
	}

	@Override
	public List<Department> querySystemList() {
		return projectDao.querySystemList();
	}

	@Override
	public String queryMemberAddress(int projectId) {
		return projectDao.queryMemberAddress(projectId);
	}

	@Override
	public void updateServiceProject(Map<String, Object> paramMap) {
		projectDao.updateServiceProject(paramMap);
		Project p = new Project();
		p.setProjectId((Integer) paramMap.get("projectId"));
		p.setDataTypeCode(MessageUtil.DATATYPE_CODE07_30);
		p.setPartyName(paramMap.get("channelName").toString());
		p.setCreateBy(UserContext.getUserContext().getUsername());
		p.setUpdateBy(UserContext.getUserContext().getUsername());
		this.updateProjectRelatedParty(p);
	}

	@Override
	public Integer queryProjectContractCountByContractNo(String contractNo) {
		return this.queryProjectContractCountByContractNoAndType(contractNo, MessageUtil.PROJECT_TYPE_AFTERSALES);
	}
	
	@Override
	public Integer queryProjectContractCountByContractNoAndType(String contractNo, String projectType) {
		return projectDao.queryProjectContractCountByContractNoAndType(contractNo, projectType);
	}

	@Override
	public List<Project> findProjectList(Object... objs) throws UnsupportedEncodingException {
		Project project = (Project) objs[0];// 获取project
		DisplayParam displayParam = (DisplayParam) objs[1];
		Map<String, String> colMap = iniClomnMap();
		User user = UserContext.getUserContext().getUser();
		if (project == null) {
			project = new Project();
			if (project.getProjectState() == null
					&& (user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || user.isHasRole(MessageUtil.ROLE_ADMIN))) {
				project.setProjectState(MessageUtil.PROJECT_STATE_CREATING);
			} else {
				project.setProjectState(MessageUtil.PROJECT_STATE_30);
			}
		} else {
			if (project.getProjectState() == null
					&& (user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || user.isHasRole(MessageUtil.ROLE_ADMIN))) {// 没有查询条件默认显示待创建项目
				project.setProjectState(MessageUtil.PROJECT_STATE_CREATING);
			} else if ("-1".equals(project.getProjectState())) {// 查询全部
				project.setProjectState(null);
			}

		}
		displayParam.setColmap(colMap);
		displayParam.getParam();
		List<Project> projectlist = null;
		if ((user.isHasRole(MessageUtil.ROLE_SERVICEMANAGER) || user.isHasRole(MessageUtil.ROLE_PROGRAMMANAGER)
				|| user.isHasRole(MessageUtil.ROLE_COMMON))// 服务经理 || 项目经理 ||
															// 普通用户
				&& !user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER)) {// 按权限查询已创建项目
			project.setOfficeCodes(Util.appendChar(user.getAreapower(), "'"));
			if (MessageUtil.PROJECT_STATE_30.equals(project.getProjectState())) {
				project.setOfficeCodes("0");
			}
			project.setMemberCode(user.getUsername());
			project.setUsername(user.getUsername());
			projectlist = this.queryProjectListByPower(project, displayParam);

		} else if (user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || user.isHasRole(MessageUtil.ROLE_ADMIN)) {// 查询全部项目(工程管理部或管理员)
			projectlist = this.queryProjectList(project, displayParam);
		}
		return projectlist;
	}

	/**
	 * 页面排序字段初始化
	 */
	private Map<String, String> iniClomnMap() {
		Map<String, String> colMap = new HashMap<String, String>();
		colMap.put("7", "orderCreateTime");
		return colMap;
	}

	@Override
	public void saveInstruction(Object... objs) {
		int projectId = (Integer) objs[0];// 项目ID
		String instructionsInfo = (String) objs[1];// 批示内容
		int instructionId = (Integer) objs[2];// 批示ID

		Instruction instruction = new Instruction();
		instruction.setProjectId(projectId);
		instruction.setInstructionsInfo(instructionsInfo);
		if (instructionId != 0) {// 回复
			instruction.setDataType(MessageUtil.FEEDBACK);
			instruction.setInstructionsId(instructionId);
		} else {
			instruction.setDataType(MessageUtil.INSTRUSTION);
		}
		instruction.setCreateBy(UserContext.getUserContext().getUsername());
		instruction.setInstructionsTime(new Date());
		instruction.setCreateTime(new Date());
		instruction.setInstructionsUser(UserContext.getUserContext().getUsername());
		this.insertInstruction(instruction);

		// 发送邮件
		Project project = this.queryProjectById(projectId);
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("templateCode", MessageUtil.NOTIFICATION_CODE_INSTRUCTION);
		context.put("username", UserContext.getUserContext().getUser().getRealName());
		context.put("projectName", project.getProjectName());
		context.put("instruction", instructionsInfo);
		if (project.getProgramManagerCode() != null && !"".equals(project.getProgramManagerCode())) {
			String programMangerCodeB = project.getProgramManagerCodeB();
			String mailProgramMangerB = "";
			if (StringUtils.isNotBlank(programMangerCodeB)) {
				mailProgramMangerB = getMails(programMangerCodeB);
			}
			context.put("tos", this.getMails(project.getServiceManagerCode()) + ";"
					+ this.getMails(project.getProgramManagerCode()) + ";" + mailProgramMangerB);
		} else {
			context.put("tos", this.getMails(project.getServiceManagerCode()));
		}
		NotificationTemplateUtil.keepMail(context);
	}

	@Override
	public void saveWeeklyFeedback(Object... objs) {
		int weeklyId = (Integer) objs[0];
		String feedback = (String) objs[1];
		int projectId = (Integer) objs[2];

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("weeklyId", weeklyId);
		paramMap.put("feedback", feedback);
		paramMap.put("feedbacker", UserContext.getUserContext().getUsername());
		this.insertWeeklyFeedback(paramMap);
		Project project = this.queryProjectById(projectId);

		// 发送邮件
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("templateCode", MessageUtil.NOTIFICATION_CODE_WEEKLY_PISHI);
		context.put("username", UserContext.getUserContext().getUser().getRealName());
		if (project != null) {
			String programMangerCodeB = project.getProgramManagerCodeB();
			String mailProgramMangerB = "";
			if (StringUtils.isNotBlank(programMangerCodeB))
				mailProgramMangerB = getMails(programMangerCodeB);
			context.put("projectName", project.getProjectName());
			context.put("tos", this.getMails(project.getProgramManagerCode()) + ";" + mailProgramMangerB);
		}
		context.put("instruction", feedback);

		NotificationTemplateUtil.keepMail(context);
	}

	@Override
	public String getUploadFileRename(String targetFileName) {
		return getRename(targetFileName);
	}

	private String getRename(String targetFileName) {
		String[] arr = targetFileName.split("\\.");
		String diff = "";
		String name = getName();
		if (arr.length > 0) {
			diff = arr[arr.length - 1];
		}

		return name + "." + diff;
	}

	private String getName() {
		String[] arr = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
				"q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0" };
		StringBuilder name = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		name.append(sdf.format(new Date()));
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			name.append(arr[random.nextInt(arr.length - 1)]);
		}
		return name.toString();
	}

	@Override
	@Transactional
    public boolean uploadFile(ProjectDeliver pd, String did, ProjectDeliver deliverFile) {
	    boolean flag = false;
	    if (deliverFile == null) {
	        return flag;
	    }
        String username = getLoginName();
        StringBuilder attachFiles = new StringBuilder();
        File[] ul = deliverFile.getUploaddelivery();
        String ufname = deliverFile.getUploaddeliveryFileName();
        if (ul  != null && !ul.equals("")) {
            List<ProjectDeliver> pdlist = new ArrayList<ProjectDeliver>();

            /** 分隔符 **/
            String separator = java.io.File.separator;
//            String path = separator + "upload" + separator + "delivery" + separator + new Date().getTime();
            String path = separator + UploadFileUtil.UPLOAD_PATH + separator + "delivery" + separator + new Date().getTime();
            boolean bool = Util.mkdir(path);
            /*
             * if (!bool) {
             * addActionMessage(HttpContext.getMessage("sys.adderror")); return
             * true; }
             */
            if (bool) {
            	String uploadExtWhiteList = basicDataService.querySysArg("sys.upload.ext.whitelist");
                String targetDirectory = ServletActionContext.getServletContext().getRealPath(path);
                String[] uploaddeliveryFileNames = ufname.split(",");

                for (int i = 0; i < uploaddeliveryFileNames.length; i++) {
                    String ufn = uploaddeliveryFileNames[i];// 附件名称
                    String targetFileName = ufn.trim();
                    // 检查文件上传类型
                    if (!UploadFileUtil.checkFileExt(ufn, uploadExtWhiteList)) {
                    	return false;
                    }
                    String newName = this.getUploadFileRename(targetFileName);// 对上传附件进行重命名
                    if (newName == null) {
                        newName = targetFileName;
                    }
                    File target = new File(targetDirectory, newName);
                    try {
                        FileUtils.copyFile(ul[i], target);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ProjectDeliver pdeliver = new ProjectDeliver();
                    pdeliver.setProjectId(pd.getProjectId());
                    pdeliver.setContractNo(pd.getContractNo());
                    pdeliver.setProjectType(pd.getProjectType());
                    pdeliver.setDeliverId(did);
                    pdeliver.setDeliverableType(pd.getDeliverableType());
                    pdeliver.setDeliverableName(targetFileName);
                    pdeliver.setDeliverablePath(path + separator + newName);
                    pdlist.add(pdeliver);
                    pdeliver = null;
                    attachFiles.append(target.getAbsolutePath()).append(",").append(targetFileName).append("&&");
                }
                flag = this.insertProjectDeliverFiles(pd, pdlist, username);
            }
        }
        return flag;
	}

	@Override
	@Transactional
	public boolean uploadFile(ProjectDeliver pd, String did, File[] ul, String ufname) {
		String username = UserContext.getUserContext().getUsername();
		boolean flag = false;
		StringBuilder attachFiles = new StringBuilder();
		if (ul != null && !ul.equals("")) {
			List<ProjectDeliver> pdlist = new ArrayList<ProjectDeliver>();

			/** 分隔符 **/
			String separator = java.io.File.separator;
//			String path = separator + "upload" + separator + "delivery" + separator + new Date().getTime();
			String path = separator + UploadFileUtil.UPLOAD_PATH + separator + "delivery" + separator + new Date().getTime();
			boolean bool = Util.mkdir(path);
			/*
			 * if (!bool) {
			 * addActionMessage(HttpContext.getMessage("sys.adderror")); return
			 * true; }
			 */
			if (bool) {
            	String uploadExtWhiteList = basicDataService.querySysArg("sys.upload.ext.whitelist");
				String targetDirectory = ServletActionContext.getServletContext().getRealPath(path);
				String[] uploaddeliveryFileNames = ufname.split(",");

				for (int i = 0; i < uploaddeliveryFileNames.length; i++) {
					String ufn = uploaddeliveryFileNames[i];// 附件名称
					String targetFileName = ufn.trim();
					// 检查文件上传类型
					if (!UploadFileUtil.checkFileExt(ufn, uploadExtWhiteList)) {
						return false;
					}
					String newName = this.getUploadFileRename(targetFileName);// 对上传附件进行重命名
					if (newName == null) {
						newName = targetFileName;
					}
					File target = new File(targetDirectory, newName);
					try {
						FileUtils.copyFile(ul[i], target);
					} catch (IOException e) {
						e.printStackTrace();
					}

					ProjectDeliver pdeliver = new ProjectDeliver();
					pdeliver.setProjectId(pd.getProjectId());
					pdeliver.setContractNo(pd.getContractNo());
					pdeliver.setProjectType(pd.getProjectType());
					pdeliver.setDeliverId(did);
					pdeliver.setDeliverableType(pd.getDeliverableType());
					pdeliver.setDeliverableName(targetFileName);
					pdeliver.setDeliverablePath(path + separator + newName);
					pdlist.add(pdeliver);
					pdeliver = null;

					attachFiles.append(target.getAbsolutePath()).append(",").append(targetFileName).append("&&");
				}
				flag = this.insertProjectDeliverFiles(pd, pdlist, username);
			}
		}
		// 查询交付件名称，已记录系统日志
		String deliverName = projectDao.queryDeliverName(Integer.parseInt(did.trim()));
		// 通知
		this.addDynamicNotification(MessageUtil.NOTIFICATION_CODE_111, pd.getProjectId(), deliverName);
		String acceptanceDeliverFile = basicDataService.querySysArg("acceptance.deliverFile");
		if (StringUtils.isBlank(acceptanceDeliverFile)) {
			acceptanceDeliverFile = StringEscUtil.getText("pm.acceptance.deliverFile");
		}
		acceptanceDeliverFile = StringUtils.trimToEmpty(acceptanceDeliverFile);
		//if ("到货签收单".equals(deliverName) || "验收报告-初验".equals(deliverName) || "验收报告-终验".equals(deliverName)) {
		if (acceptanceDeliverFile.matches("(.)*\\b" + deliverName + "\\b(.)*") && attachFiles.length() > 0) {
			Project project = projectDao.queryProjectById(pd.getProjectId());
			if ((project.getColumn011() == null || "10".equals(project.getColumn011()))
					&& "运营商市场部".equals(project.getColumn004())) {
				Map<String, Object> context = new HashMap<>();
				
				// 主送给销售
				List<ProjectMember> projectMembers = this.queryValidMemberEmailByProjectIdAndRoles(pd.getProjectId(), "10,20,30");
				HashSet<String> tos = new HashSet<String>();
				HashSet<String> ccs = new HashSet<String>();
				HashSet<String> salesNames = new HashSet<String>();
				for (ProjectMember member : projectMembers) {
					String email = member.getEmail();
					if (StringUtils.isBlank(email) && StringUtils.isNotBlank(member.getMemberCode())) {
						email = this.queryMailByUserNameFromOA(member.getMemberCode());
					}
					if (StringUtils.isNotBlank(email)) {
						if (MessageUtil.MEMBER_SALESMAN.equals(member.getMemberRole())) {
							//tos.append(email).append(";");
						    tos.add(email);
							if (StringUtils.isNoneBlank(member.getMemberName())) {
								//salesNames.append(member.getMemberName());
							    salesNames.add(member.getMemberName());
							}
						} else if (MessageUtil.MEMBER_PM.equals(member.getMemberRole()) || MessageUtil.MEMBER_SM.equals(member.getMemberRole())) {
							//ccs.append(email).append(";");
						    ccs.add(email);
						}
					}
				}
				
				// 抄送验收小组群组邮箱
				String acceptanceMail = basicDataService.querySysArg("acceptance.mail");
				if (StringUtils.isNotBlank(acceptanceMail)) {
					//ccs.append(acceptanceMail).append(";");
				    ccs.add(acceptanceMail);
				} else {
					String cc = StringEscUtil.getText("project.deliverUpload.mail.person");
					cc = projectDao.queryMailByUsername(cc);
					//ccs.append(cc).append(";");
					ccs.add(cc);
				}
				
				Pattern p = Pattern.compile("(\\b" + deliverName + "\\b\\$)([^$]*)(\\$;?)");
				Matcher m = p.matcher(acceptanceDeliverFile);
				String taskName = "验收";
				if (m.find()) {
					taskName  = m.group(2);
				} else {
					taskName = m.group(2);
				}
				//context.put("username", salesNames.toString());
				context.put("username", StringUtils.join(salesNames, "、"));
				context.put("tos", StringUtils.join(tos, ";"));
				context.put("ccs",StringUtils.join(ccs, ";"));
				context.put("attachFileNames", attachFiles.toString());
				context.put("templateCode", MessageUtil.NOTIFICATION_CODE_PROJECT_UPLOAD_DELIVER);
				context.put("projectName", project.getProjectName());
				context.put("deliverName", deliverName);
				context.put("taskName", taskName);
				context.put("content", pd.getContractNo());
				context.put("officeName", project.getOfficeName());
				NotificationTemplateUtil.keepMail(context);
			}
		}
		return flag;
	}

	@Override
	public int queryProjectShipment(int projectId) {
		return projectDao.queryProjectShipmentSize(projectId);
	}
	
	@Override
	public int queryHistoryProjectShipmentSize(int projectId) {
		return projectDao.queryHistoryProjectShipmentSize(projectId);
	}

	@Override
	public String queryProjectCode(Project project) {
		String projectCode = project.getProjectCode();
        project.setSmsProjectCode(projectCode);
		int lastCode = projectDao.queryProjectGroupSize(projectCode);
		return dealWith(lastCode, projectCode);
	}
	
    @Override
    public void updateProjectExecutionState(int projectId, String executionState) {
        Project project = this.queryProjectById(projectId);
        this.updateProjectExecutionState(project, executionState);
    }

	@Override
    public void updateProjectExecutionState(Project project, String executionState) {
        if (project != null && project.getProjectId() > 0) {
            String oldExecutionState = StringUtils.trimToEmpty(project.getExecutionState());
            // 直签项目已提交终验报告和非直签项目具备闭环条件，则项目始终显示“项目闭环”
            if ("10".equals(project.getColumn011())) {
                List<ProjectDeliver> undelivedProjectDeliverList = this.queryNeededUndelivedProjectDeliverList(project);
                // 直签项目是否已经上传终验报告
                boolean isUploadFinalInspect = true;
                for (ProjectDeliver projectDeliver : undelivedProjectDeliverList) {
                    if ("11-26".equals(projectDeliver.getDeliverKey())) {
                        isUploadFinalInspect = false;
                        break;
                    }
                }
                if (isUploadFinalInspect && executionState.compareTo(MessageUtil.PROJECT_EXECUTION_STATE_80) < 0) {
                    if(oldExecutionState.compareTo(MessageUtil.PROJECT_EXECUTION_STATE_80) <= 0) {
                        executionState = MessageUtil.PROJECT_EXECUTION_STATE_80;
                    } else {
                        executionState = oldExecutionState;
                    }
                }
            } else {
                int canCloseLoop = this.canCloseLoop(project);
                if (canCloseLoop == 1 && executionState.compareTo(MessageUtil.PROJECT_EXECUTION_STATE_80) < 0) {
                    if(oldExecutionState.compareTo(MessageUtil.PROJECT_EXECUTION_STATE_80) <= 0) {
                        executionState = MessageUtil.PROJECT_EXECUTION_STATE_80;
                    } else {
                        executionState = oldExecutionState;
                    }
                }
            }
            if (StringUtils.isNotBlank(executionState)) {
                Project temp = new Project();
                temp.setProjectId(project.getProjectId());
                temp.setExecutionState(executionState);
                this.insertOrUpdateProjectState(temp);
            }
        }
    }
	
	@Override
    public void updateProjectCloseProcessState(int projectId, String closeProcessState) {
	    Project project = null;
	    if (MessageUtil.PROJECT_CLOSE_PROCESS_STATE_50.compareTo(closeProcessState) > 0) {
	        project = this.queryProjectById(projectId);
	    } else {
	        project = new Project(projectId);
	    }
        this.updateProjectCloseProcessState(project, closeProcessState);
    }
	
	@Override
    public void updateProjectCloseProcessState(Project project, String closeProcessState) {
        if (project != null && project.getProjectId() > 0) {
            String projectState = project.getProjectState();
            // String oldCloseProcessState = StringUtils.trimToEmpty(project.getCloseProcessState());
            // 项目不予跟踪或已闭环，则状态改为“闭环结束”
            if (MessageUtil.PROJECT_STATE_DENY.equals(projectState) || MessageUtil.PROJECT_STATE_CLOSEDLOOP.equals(projectState)) {
                closeProcessState = MessageUtil.PROJECT_CLOSE_PROCESS_STATE_50;
            }
            if (StringUtils.isNotBlank(closeProcessState)) {
                Project temp = new Project();
                temp.setProjectId(project.getProjectId());
                temp.setCloseProcessState(closeProcessState);
                this.insertOrUpdateProjectState(temp);
            }
        }
    }

	@Override
	public void insertOrUpdateProjectState(Project project) {
		// 查询有无数据
		int size = projectDao.queryProjectState(project.getProjectId());
		if (size == 0) {
			projectDao.insertProjectState(project);
		} else {
			projectDao.updateProjectState(project);
		}
	}
	
    @Override
	public boolean queryProjectPlanState(int projectId) {
		String planState = projectDao.queryPlanState(projectId);
		if (MessageUtil.PROJECT_PLAN_STATE_40.equals(planState) || planState == null) {// 需要更新，并作更新
			return true;
		}
		return false;
	}

	@Override
	public String queryProjectCurrentPlan(int projectId) {

		return projectDao.queryProjectCurrentPlan(projectId);
	}

	@Override
	public void updateProjectCloseTime(int closeObjId) {
		projectDao.updateProjectCloseTime(closeObjId);
	}

	@Override
	public void updateProjectDirectCloseTime(int projectId) {
		projectDao.updateProjectDirectCloseTime(projectId);
	}

	@Override
	public void updateProjectLastRefreshTime(int projectId) {
		projectDao.updateProjectLastRefreshTime(projectId);
	}

	@Override
	public void updateProjectPlanStateToClose(int closeObjId) {
		projectDao.updateProjectPlanStateToClose(closeObjId);
	}

	@Override
	public void addFixedNotification(String templateCode, int projectId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("templateCode", templateCode);// templateCode
		params.put("projectId", projectId);
		NotificationTemplateUtil.KeepNotification(params);
	}

	@Override
	public void addDynamicNotification(String templateCode, int projectId, String content) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("templateCode", templateCode);// templateCode
		params.put("projectId", projectId);
		params.put("content", content);
		NotificationTemplateUtil.KeepNotification(params);
	}

	@Override
	public void addDynamicNotification(String templateCode, int projectId, HashMap<String, Object> params) {
		params.put("templateCode", templateCode);// templateCode
		params.put("projectId", projectId);
		NotificationTemplateUtil.KeepNotification(params);
	}
	
	@Override
	public int queryProjectIdBycloseId(int closeObjId) {
		return projectDao.queryProjectIdBycloseId(closeObjId);
	}

	@Override
	public List<CallBack> queryCallBackList(int projectId) {
		return projectDao.queryCallBackList(projectId);
	}

	@Override
	public int queryCallBackingSize(int projectId) {
		return projectDao.queryCallBackingSize(projectId);
	}
	
	/**
	 * 判断项目是否可以闭环
	 * 1、安装数=发货数
	 * 2、回访完成（没有进行中的回访）
	 * 3、服务商/代理商、最终客户不为空
	 * 4、必传交付件完整
	 */
	@Override
    public int canCloseLoop(Project project) {
	    int isToCloseProject = 0;
//	    // 1、安装数=发货数
//	    int shipmentInfoSize = this.queryShipmentInfoSizeByContractNo(Util.appendChar(project.getContractNo(), "'"));
//        int anzhuangdizhisize = this.queryProjectShipment(project.getProjectId());
//        
//	    if(anzhuangdizhisize == shipmentInfoSize){
//	          // 2、回访完成（没有进行中的回访）
//            int isCallBacking = this.queryCallBackingSize(project.getProjectId());
//            if (isCallBacking == 0) {
//	              // 3、服务商/代理商、最终客户不为空
//                String serviceType = project.getColumn012();
//                String finalCustomer = project.getColumn013();
//                String channel = "";
//                if (StringUtils.isBlank(serviceType)) {
//                    serviceType = String.valueOf(project.getColumn012Readonly());
//                }
//                if( "0".equals(serviceType) || "4".equals(serviceType)){//原厂直服、原厂集成
//                    channel = project.getServiceChannel();
//                }else if("1".equals(serviceType) || "3".equals(serviceType)){
//                    channel = project.getAgentChannel();
//                }
//                if(StringUtils.isNotBlank(finalCustomer) && StringUtils.isNotBlank(channel)){
//	                // 4、必传交付件完整
                    // 未上传必传交付件数量，必传交付件都上传完毕才可闭环
                    int undelivedCount = this.queryNeededUndelivedCount(project);
                    if (undelivedCount == 0) {
                        isToCloseProject = 1;//可以进行闭环申请
                    }
//                }
//            }
//        }
        return isToCloseProject;
    }

    /**
	 * 查询项目正在审批中的回访流程
	 * 
	 * @param projectId
	 * @param applyState
	 * @return
	 */
	public List<CallBack> queryCallBackRunList(int projectId, int applyState) {
		HashMap<String, Integer> params = new HashMap<>();
		params.put("projectId", projectId);
		params.put("applyState", applyState);
		return projectDao.queryCallBackRunList(params);
	}

	@Override
	public List<OrderDataFromSap> queryRmaOrderDataByContractNo(String contractNo) {
		if (contractNo != null && contractNo.length() > 0) {
			String[] contracts = contractNo.split(",");
			List<OrderDataFromSap> orderdatas = new ArrayList<OrderDataFromSap>();
			for (String contract : contracts) {
				List<OrderDataFromSap> datas = projectDao.queryRmaOrderDataByContractNo(contract.trim());
				orderdatas.addAll(datas);
			}
			return orderdatas;
		}
        return Collections.emptyList();
	}

	@Override
	public List<Project> queryProjectListByOfficeAndMemberCode(Project project) {
		return projectDao.queryProjectListByOfficeAndMemberCode(project);
	}

	@Override
	public List<RealProductLineBean> queryRealOrderDataListByProjectId(int projectId) {
		return projectDao.queryRealOrderDataListByProjectId(projectId);
	}

	@Override
	public int queryRealOrderDataSizeByProjectId(int projectId) {
		return projectDao.queryRealOrderDataSizeByProjectId(projectId);
	}

	/**
	 * 确认不予跟踪时，根据项目project，终止正在进行的审批流程
	 * 
	 * @param project
	 */
	private void termainteActivities(Project project) {
		List<CallBack> callBackList = queryCallBackList(project.getProjectId());
		List<String> taskIds = new ArrayList<String>();
		for (CallBack callBack : callBackList) {
			String taskId = callBack.getTaskId();
			if (StringUtils.isNotBlank(taskId)) {
				taskIds.add(taskId);
				callBackService.updateCallBackApplyState(callBack.getCallBackId(), ActivityMessage.FLOW_REJECT);
			}
		}
		String pmCloseTaskId = pmClosedLoopService.queryTaskByBussinessKey(project);
		if (StringUtils.isNotBlank(pmCloseTaskId)) {
			taskIds.add(pmCloseTaskId);
		}
		ProjectUtils.terminateActivities(taskIds, "不予跟踪后系统自动终止正在进行的任务");
	}

	@Override
	public List<ShipmentInfo> querySoftversionList(String contractNo, int projectId) {
		return projectDao.querySoftversionList(contractNo, projectId);
	}
	
	@Override
    public List<ShipmentInfo> querySoftversionList(String contractNo, int projectId, String profitCenter) {
        return projectDao.querySoftversionList(contractNo, projectId, profitCenter);
    }

    @Override
	public void updateSoftversion(List<ShipmentInfo> softversionList, SoftChangeLog softChangeLog) {
		int sameCount = 0;
		for (Iterator<ShipmentInfo> iterator = softversionList.iterator(); iterator.hasNext();) {
			ShipmentInfo shipmentInfo = (ShipmentInfo) iterator.next();
			if (shipmentInfo.getConpChange() + shipmentInfo.getBootChange()+shipmentInfo.getCpldChange()+shipmentInfo.getPcbChange() == 0) {
				//iterator.remove();
				sameCount++;
			}
		}
		if (softversionList != null && softversionList.size() > 0 && sameCount != softversionList.size()) {
			// 增加版本变更记录
			// 0.1查询现有软件版本
		    System.out.println(softChangeLog == null);
			int version = projectDao.querySoftVersionNum(softChangeLog.getProjectId());
			softChangeLog.setChangeVersion("V" + (version + 1));
			// 0.1失效现在的最新版本
			if (version != 0) {
				projectDao.updateInvalidSoftVersionLog(softChangeLog.getProjectId());
			}
			// 0.2 增加新版本变更记录
			int logId = projectDao.insertSoftVersionLog(softChangeLog);
			// 失效原有版本
			 projectDao.updateInvalidSoftversion(softChangeLog.getProjectId());
			// 新增软件版本
			projectDao.insertSoftVersionList(softversionList, logId);
		}
	}

	@Override
	public List<SoftChangeLog> queryHistSoftChangeLog(int projectId) {
		return projectDao.queryHistSoftChangeLog(projectId);
	}

	@Override
	public List<ShipmentInfo> queryHistSoftVersionList(SoftChangeLog softChangeLog) {
		if (softChangeLog.getId() == -1) {// 查询出厂版本
			Project project = projectDao.queryProjectById(softChangeLog.getProjectId());

			return projectDao.queryHistSoftVersionList(softChangeLog, project.getContractNo());
		} else {
			return projectDao.queryHistSoftVersionList(softChangeLog);// 查询变更版本信息
		}
	}

	@Override
	public SoftChangeLog queryOneSoftChangeLog(int id) {
		return projectDao.queryOneSoftChangeLog(id);
	}

	@Override
	public String queryMailByUserNameFromOA(String userName) {
		return projectDao.queryMailByUserNameFromOA(userName);
	}

	@Override
	public List<ProjectMember> queryValidMemberByProjectId(int projectId) {
		return projectDao.queryValidMemberByProjectId(projectId);
	}

	public List<ProjectMember> queryValidMemberEmailByProjectIdAndRoles(int projectId, String memberRoles) {
		return projectDao.queryValidMemberEmailByProjectIdAndRoles(projectId, memberRoles);
	}
	
	@Override
	public int batchDeleteProject(List<Project> projectList) {
		StringBuilder builder = new StringBuilder();
		for (Project project : projectList) {
			builder.append(project.getContractNo()).append(",");
		}
		String contractNos = Util.appendChar(builder.toString(), "'");
		if (StringUtils.isBlank(contractNos)) {
			return 0;
		}
		List<Project> tempProject = projectDao.queryExistsProjectByContractNos(contractNos);
		for (Project project : tempProject) {
			termainteActivities(project);
		}
		return projectDao.batchDeleteProject(contractNos);
	}

	@Override
	public int batchInvalidProject(List<Project> projectList) {
		StringBuilder builder = new StringBuilder();
		for (Project project : projectList) {
			builder.append(project.getContractNo()).append(",");
		}
		String contractNos = Util.appendChar(builder.toString(), "'");
		if (StringUtils.isBlank(contractNos)) {
			return 0;
		}
		List<Project> tempProject = projectDao.queryExistsProjectByContractNos(contractNos);
		int result = 0;
		for (Project project : tempProject) {
			termainteActivities(project);
			this.invalidProject(project.getProjectId());
			result++;
		}
		return result;
	}

	@Override
	public List<Project> queryTransferProjectList(Project project) {
		return projectDao.queryTransferProjectList(project);
	}

	@Override
	public List<Notification> queryNotifyList(int projectId) {
		return projectDao.queryNotifyList(projectId);
	}

	@Override
	@Transactional
	public void importSpotCheckIgnoreItem(List<Item> itemList) {
		if (itemList == null) {
			return;
		}
		projectDao.truncateSpotCheckIgnoreItem();
		projectDao.batchInsertSpotCheckIgnoreItem(itemList);
	}
	
	@Override
	public Map<String, String> exportSpotCheckList(Project project) {
	    List<Map<String, String>> spotCheckList;
	    if ("14".equals(project.getSalesType())) {
	        spotCheckList = projectDao.querySpotCheckList(Util.appendChar(project.getContractNo(), "'"), project.getProjectId(), project.getColumn001());
	    } else {
	        spotCheckList = projectDao.querySpotCheckList(Util.appendChar(project.getContractNo(), "'"), project.getProjectId());
	    }
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date exportTime = new Date();
		String contractNos = project.getContractNo().replaceAll(",", "、");
		String root = ServletActionContext.getServletContext().getRealPath("/");// 项目跟目录
//			String directory = "upload/spotCheck";
		String directory = UploadFileUtil.UPLOAD_PATH + "/spotCheck";
		String projectName = StringUtils.trimToEmpty(project.getProjectName()).replaceAll("/", "／");
		String exportFileName = projectName + "-现场验货单" + exportTime.getTime();
		try {
		    String realpath = this.getClass().getClassLoader().getResource("").getPath().replaceAll("%20", " ");
			XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(realpath+"com/dp/plat/template/spotCheck.xlsx"));
			XSSFSheet worksheet = workbook.getSheetAt(0);
			XSSFRow row = null;
			XSSFCell cell = null;
			//Header header = worksheet.getHeader();
			//header.setRight(sdf.format(new Date()));
			Footer footer = worksheet.getFooter();
			footer.setLeft(footer.getLeft() + sdf.format(exportTime));
			
			// 填入项目名称
			row = worksheet.getRow(2);
			cell = row.getCell(2);
			cell.setCellValue(project.getProjectName());
			// 填入合同号
			row = worksheet.getRow(3);
			cell = row.getCell(2);
			cell.setCellValue(contractNos);
			
			// 填入明细
			int i = 6;
			XSSFRow defaultRow = null;
			if (spotCheckList.isEmpty()) {
				spotCheckList.add(new HashMap<String, String>());
			}
			for (Map<String, String> bean : spotCheckList) {
				row = createRow(worksheet, i);
				defaultRow = worksheet.getRow(i + 1);
				cell = createCell(row, 0, bean.get("contractNo"), defaultRow);
				cell = createCell(row, 1, bean.get("itemCode"), defaultRow);
				cell = createCell(row, 2, bean.get("itemName"), defaultRow);
				cell = createCell(row, 3, String.valueOf(bean.get("qty")), defaultRow);
				cell = createCell(row, 4, bean.get("barCodes"), defaultRow);
				cell = createCell(row, 5, bean.get("remark"), defaultRow);
				i++;
			}
			worksheet.removeRow(defaultRow);
			worksheet.shiftRows(i + 1, worksheet.getLastRowNum(), -1);
			
			String fileName = exportFileName + ".xlsx";
			String filePath = directory + "/" + fileName;
			File file = new File(root + directory);
			if (!file.exists()) {
				file.mkdirs();
			}
			File file1 = new File(root + filePath);
			FileOutputStream fos = new FileOutputStream(file1);
			//worksheet.setPrintGridlines(true);
			workbook.write(fos);
			fos.close();
			Map<String, String> map = new HashMap<String, String>();
			map.put("filePath", "/" + filePath);
			map.put("fileName", fileName);
			return map;
		} catch (Exception e) {
		    e.printStackTrace();
		    
		    try {
    		    // 如果导出Excel失败，则导出Doc文件
    		    Map<String, Object> dataMap = new HashMap<String, Object>();
    		    dataMap.put("projectName", project.getProjectName());
                dataMap.put("contractNos", contractNos);
                dataMap.put("exportTime", sdf.format(exportTime));
                dataMap.put("spotCheckList", spotCheckList);
    
                String fileName = exportFileName + ".doc";
                String filePath = directory + "/" + fileName;
                boolean success = WordUtil.createWord(dataMap, "spotCheckDoc.ftl", root + "/" + directory, fileName);
                Map<String, String> map = new HashMap<String, String>();
                map.put("filePath", "/" + filePath);
                map.put("fileName", fileName);
                return map;
		    } catch (Exception e2) {
		        e2.printStackTrace();
		    }
		}
		return null;
	}
	
	@Override
    public Map<String, String> exportOverWarrantyRemindList(Project project) {
        try {
            List<Map<String, String>> overWarrantyRemindList;
            if ("14".equals(project.getSalesType())) {
                overWarrantyRemindList = projectDao.queryOverWarrantyRemindList(Util.appendChar(project.getContractNo(), "'"), project.getProjectId(), project.getColumn001());
            } else {
                overWarrantyRemindList = projectDao.queryOverWarrantyRemindList(Util.appendChar(project.getContractNo(), "'"), project.getProjectId());
            }
            List<String> contractNos = Arrays.asList(StringUtils.split(project.getContractNo(), ","));
            Stream<String> stream = contractNos.parallelStream().map(contractNo -> {
                Project temp = this.queryProjectByContractNo(contractNo);
                if (temp != null) {
                    return temp.getOrderCreateTime();
                }
                return null;
            }).filter(time -> {
                return time != null;
            }).sorted();
            Object[] array = stream.toArray();
            Object periodStartDate = null;
            Object periodEndDate = null;
            if (array.length > 0) {
                periodStartDate = array[0];
                periodEndDate = array[array.length - 1];
            }
            
            String customerName = project.getColumn013();
            String warrantyStatus = project.getWarrantyStatusName();
            
            Person serviceManager = projectDao.queryPersonFromOaByCode(StringUtils.rightPad(StringUtils.right(project.getServiceManagerCode(), 5), 5, "0"));
            if (serviceManager == null) {
                serviceManager = new Person();
            }
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("customerName", project.getColumn013());
            dataMap.put("serviceManagerName", serviceManager.getSalesmanName());
            dataMap.put("serviceManagerPhone", serviceManager.getSalesmanTel());
            dataMap.put("serviceManagerEmail", serviceManager.getSalesmanMail());
            dataMap.put("periodStartDate", periodStartDate);
            dataMap.put("periodEndDate", periodEndDate);
            dataMap.put("warrantyStatus", warrantyStatus);
            dataMap.put("overWarrantyRemindList", overWarrantyRemindList);

            String root = ServletActionContext.getServletContext().getRealPath("/");// 项目跟目录
            String directory = UploadFileUtil.UPLOAD_PATH + "/overWarrantyRemind";
            
            String fileName = "《设备过保提醒函》-" + customerName + ".doc";
            String filePath = directory + "/" + fileName;
            boolean success = WordUtil.createWord(dataMap, "《设备过保提醒函》.ftl", root + "/" + directory, fileName);
            Map<String, String> map = new HashMap<String, String>();
            map.put("filePath", "/" + filePath);
            map.put("fileName", fileName);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	private XSSFRow createRow(XSSFSheet sheet, Integer rowIndex) {  
        XSSFRow row = null;  
        if (sheet.getRow(rowIndex) != null) {  
        	int lastRowNo = sheet.getLastRowNum();  
			sheet.shiftRows(rowIndex, lastRowNo, 1); 
        }  
//        CellRangeAddress cellRangeAddress = new CellRangeAddress(rowIndex, rowIndex, 4, 5);
//        sheet.addMergedRegion(cellRangeAddress);
        row = sheet.createRow(rowIndex);  
        return row;
    }
	
	private XSSFCell createCell(XSSFRow row, int cellIndex, String value, XSSFRow defaultRow) {
		XSSFCellStyle cellStyle = defaultRow.getCell(cellIndex).getCellStyle();
		CellType cellType = defaultRow.getCell(cellIndex).getCellType();
		XSSFCell cell = row.createCell(cellIndex);
		cell.setCellStyle(cellStyle);
		cell.setCellType(cellType);
		
		// 合并单元格
//		mergeCell(row, cellIndex, defaultRow);
		
//		// 最大单元格32767问题，只能解决报错，内容会被截断
//		SpreadsheetVersion excel2007 = SpreadsheetVersion.EXCEL2007;
//		if (StringUtils.length(value) > excel2007.getMaxTextLength()) {
//		    if (Integer.MAX_VALUE != excel2007.getMaxTextLength()) {
//		        try {
//		            // SpreadsheetVersion.EXCEL2007的_maxTextLength变量 
//		            Field field = excel2007.getClass().getDeclaredField("_maxTextLength");
//		            // 关闭反射机制的安全检查，可以提高性能
//		            field.setAccessible(true);
//		            // 重新设置这个变量属性值
//		            field.set(excel2007, Integer.MAX_VALUE);
//		        } catch (Exception e) {
//		            e.printStackTrace();
//		        }
//		    }
//		}
		
		if (StringUtils.isNotBlank(value)) {
			cell.setCellValue(value);
		} else {
			cell.setCellValue("");
		}
		return cell;
	}
	
	/**
	 * 合并单元格
	 * @param row
	 * @param cellIndex
	 * @param defaultRow
	 */
	@SuppressWarnings("unused")
    private void mergeCell(XSSFRow row, int cellIndex, XSSFRow defaultRow) {
		try {
			XSSFSheet sheet = row.getSheet();
			int lastMergedRegions = sheet.getNumMergedRegions();
			CellRangeAddress cellRangeAddress = sheet.getMergedRegion(lastMergedRegions - 1);
			int rowIndex = row.getRowNum();
			int firstRowIndex = cellRangeAddress.getFirstRow();
			int lastRowIndex = cellRangeAddress.getLastRow();
			int firstColumnIndex = cellRangeAddress.getFirstColumn();
			int lastColumnIndex = cellRangeAddress.getLastColumn();
			boolean isMerged = false;
			for (int i = lastMergedRegions - 2; i >= 0; i--) {
				if (rowIndex + 1 >= firstRowIndex && rowIndex + 1 <= lastRowIndex) {
					if (cellIndex >= firstColumnIndex && cellIndex <= lastColumnIndex) {
						isMerged = true;
						break;
					}
				} else if (rowIndex + 1 > lastRowIndex) {
					break;
				}
				cellRangeAddress = sheet.getMergedRegion(i);
				firstRowIndex = cellRangeAddress.getFirstRow();
				lastRowIndex = cellRangeAddress.getLastRow();
				firstColumnIndex = cellRangeAddress.getFirstColumn();
				lastColumnIndex = cellRangeAddress.getLastColumn();
			}
			if (isMerged) {
				int step = lastRowIndex - firstRowIndex + 1;
				cellRangeAddress.setFirstRow(firstRowIndex - step);
				cellRangeAddress.setLastRow(lastRowIndex - step);
				sheet.addMergedRegion(cellRangeAddress);
				for (int i= firstColumnIndex + 1; i <= lastColumnIndex; i++) {
					CellStyle tempCellStyle = defaultRow.getCell(i).getCellStyle();
					CellType tempCellType = defaultRow.getCell(cellIndex).getCellType();
					XSSFCell cell2 = row.createCell(i);
					cell2.setCellStyle(tempCellStyle);
					cell2.setCellType(tempCellType);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    @Override
    public ProjectMaintenanceVO selectProjectMaintenanceById(Integer id) {
        return projectDao.selectProjectMaintenanceById(id);
    }

    @Override
    public List<ProjectMaintenanceVO> selectProjectMaintenanceList(ProjectMaintenanceVO projectMaintenance) {
        return projectDao.selectProjectMaintenanceList(projectMaintenance);
    }

    @Override
    public List<ProjectMaintenanceVO> selectProjectMaintenanceVOList(ProjectMaintenanceVO projectMaintenance) {
        return projectDao.selectProjectMaintenanceVOList(projectMaintenance);
    }
    
    @Override
    public List<Map<String, Object>> selectProjectMaintenanceMapList(ProjectMaintenanceVO projectMaintenance) {
        return projectDao.selectProjectMaintenanceMapList(projectMaintenance);
    }

    @Override
    public List<Map<String, Object>> selectProjectMaintenanceMapList(ProjectMaintenanceVO projectMaintenance, DisplayParam displayParam) {
        return projectDao.selectProjectMaintenanceMapList(projectMaintenance, displayParam);
    }

    @Override
    @Transactional
    public Integer insertOrUpdateProjectMaintenance(ProjectMaintenance projectMaintenance) {
    	Integer id = projectMaintenance.getId();
        Integer maintenanceId = projectDao.insertOrUpdateProjectMaintenance(projectMaintenance);
        if (id != null) {
        	projectMaintenance.setId(id);
        }
        return maintenanceId;
    }
    
    @Override
    public Integer selectSingleProjectMaintenanceMaxId(ProjectMaintenance projectMaintenance) {
        return projectDao.selectSingleProjectMaintenanceMaxId(projectMaintenance);
    }

    @Override
	public Map<String, Object> queryProjectWarrantyState(Integer projectId) {
		return projectDao.queryProjectWarrantyState(projectId);
	}
    
	@Override
	@Transactional
	public boolean uploadMaintenanceFile(ProjectMaintenanceVO projectMaintenance,ProjectDeliver projectDeliver, String deliverId, ProjectDeliver deliverFile) {
		if(deliverFile == null || deliverFile.getUploaddelivery() == null || deliverFile.getUploaddelivery().length == 0){
			return false;
		}
		this.uploadFile(projectDeliver, deliverId, deliverFile);
		// 查询交付件名称，已记录系统日志
		String deliverName = projectDao.queryDeliverName(Integer.parseInt(deliverId.trim()));
		// 获取交付件、服务、次数参数
        String mailDeliverFile = basicDataService.querySysArg("pm.project.maintenance.serviceDelivery.deliverFile");
        if (StringUtils.isBlank(mailDeliverFile)) {
        	mailDeliverFile = StringEscUtil.getText("pm.project.maintenance.serviceDelivery.deliverFile");
        }
        mailDeliverFile = StringUtils.trimToEmpty(mailDeliverFile);
        //if ("到货签收单".equals(deliverName) || "验收报告-初验".equals(deliverName) || "验收报告-终验".equals(deliverName)) {
        if (mailDeliverFile.matches("(.)*\\b" + deliverName + "\\b(.)*")) {
        	// 查询服务的每年次数、服务类型
//			Pattern p = Pattern.compile("(\\b" + deliverName + "\\b\\$)([^$]*)\\$([^$]*)\\$([^$]*)\\$([^$]*)(\\$;?)");
//			Matcher m = p.matcher(mailDeliverFile);
//			String serviceName = "";
//			Integer yearCount = 4;
//			String serviceYear = "";
//			String serviceCode = "";
//			if (m.find()) {
//				serviceName  = m.group(2);
//				serviceYear = m.group(3);
//				yearCount  = Integer.parseInt(m.group(4));
//				serviceCode  = m.group(5);
//			} else {
//				serviceName = m.group(2);
//				serviceYear = m.group(3);
//				yearCount  = Integer.parseInt(m.group(4));
//				serviceCode  = m.group(5);
//			}
        	Map<String, Map<String, Object>> configMap = matchServiceDeliveryConfigMap(mailDeliverFile, "deliverName");
        	Map<String, Object> config = configMap.get(deliverName) != null ? configMap.get(deliverName) : new HashMap<String, Object>();
        	String deliverNames = (String) (config.get("deliverNames") != null ? config.get("deliverNames") : "");
        	String serviceName = (String) (config.get("serviceName") != null ? config.get("serviceName") : "");
			Integer yearCount = (Integer) (config.get("yearCount") != null ? config.get("yearCount") : 4);
			String serviceYear = (String) (config.get("serviceYear") != null ? config.get("serviceYear") : "");
			String serviceCode = (String) (config.get("serviceCode") != null ? config.get("serviceCode") : "");
//			Integer deliverId = (Integer) (config.get("deliverId") != null ? config.get("deliverId") : 0);
			
			Map<String, Object> warrantyState = projectMaintenance.getWarrantyState();
			
        	// 计算最近的服务周期
			Date[] nearlyYearDates = DateUtil.getNearlyYearDates(projectMaintenance.getProcessTime(), (Date) warrantyState.get(serviceCode + "StartTime"), (Date) warrantyState.get(serviceCode + "EndTime"));
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("maintenanceId", projectMaintenance.getId());// 上传在同一日报内，去掉可查找同一项目
        	map.put("projectId", projectMaintenance.getProjectId());
        	map.put("deliverId", deliverId);
        	map.put("serviceType", serviceCode);
        	map.put("checkDate", true);
        	map.put("serviceDate", projectMaintenance.getProcessTime());
        	map.put("startDate", nearlyYearDates[0]);
        	map.put("endDate", nearlyYearDates[1]);
        	map.put("mergeQuarterCount", true);
        	
			// 多个交付件同时存在条件判断
			if (deliverNames.contains(",")) {
//        		projectDeliver.setDeliverableType(deliverNames);
//				int deliverTypeCount = projectDao.queryProjectMaintenanceDeliverCountByProjectDeliver(projectDeliver);
//				if (deliverTypeCount > 0) {
//					return false;
//				}
				
//				ProjectDeliver temp = new ProjectDeliver();
//				temp.setProjectId(projectMaintenance.getProjectId());
//				temp.setEventKey(serviceCode);
//				temp.setUploadTime(projectMaintenance.getProcessTime());
//				Boolean hasDeliveried = projectDao.queryProjectMaintenanceServiceDeliveriedByProjectDeliver(temp);
				Boolean hasDeliveried = projectDao.queryProjectMaintenanceServiceDeliveriedByMap(map);
				if (!hasDeliveried) {
					return false;
				}
			}
        	
        	// 查询项目维护对应的项目上传交付件的次数
        	Map<String, Long> counts = queryProjectMaintenanceDeliverCount(map);
        	Long serviceCount = counts.get("count");
//			Long quarterCount = Long.valueOf(String.valueOf(counts.get("quarterCount")));// 本季度上传次数
        	
        	// 添加服务交付记录情况
        	JSONObject serviceDelivery = (JSONObject) JSON.toJSON(projectMaintenance);
        	serviceDelivery.put("maintenanceId", projectMaintenance.getId());
			serviceDelivery.put("deliveried", 1);
			serviceDelivery.put("count", serviceCount);
			serviceDelivery.put("yearCount", yearCount);
			serviceDelivery.putAll(map);
			projectDao.insertProjectServiceDeliveryBySelective(serviceDelivery);
			
			// 回填对应的年服务次数、当前服务次数
			warrantyState.put(serviceYear, yearCount);
			warrantyState.put(serviceYear.replace("Year", ""), serviceCount);
			projectMaintenance.setWarrantyState(warrantyState);
        	
			// 判断是否在服务期限内，服务期外不发送邮件
			if (!Boolean.TRUE.equals(Boolean.parseBoolean(String.valueOf(warrantyState.get(serviceCode + "Enable"))))) {
				return false;
			}
        	// 主送给销售、项目经理、服务经理
			List<ProjectMember> projectMembers = this.queryValidMemberEmailByProjectIdAndRoles(projectMaintenance.getProjectId(), "10,20,30");
			HashSet<String> tos = new HashSet<String>();
			HashSet<String> ccs = new HashSet<String>();
			HashSet<String> salesNames = new HashSet<String>();
			for (ProjectMember member : projectMembers) {
				String email = member.getEmail();
				if (StringUtils.isBlank(email) && StringUtils.isNotBlank(member.getMemberCode())) {
					email = this.queryMailByUserNameFromOA(member.getMemberCode());
				}
				if (StringUtils.isNotBlank(email)) {
					if (MessageUtil.MEMBER_SALESMAN.equals(member.getMemberRole())) {
					    ccs.add(email);
						if (StringUtils.isNoneBlank(member.getMemberName())) {
						    salesNames.add(member.getMemberName());
						}
					} else if (MessageUtil.MEMBER_PM.equals(member.getMemberRole()) || MessageUtil.MEMBER_SM.equals(member.getMemberRole())) {
					    tos.add(email);
					}
				}
			}
			UserManageService userManageService = SpringContext.getApplicationContext().getBean("userManageService", UserManageService.class);
			Map<String, String> params = new HashMap<String, String>();
			params.put("roleid", String.valueOf(MessageUtil.ROLE_AREA_LEADER));
//			params.put("dpNo", projectMaintenance.getOfficeCode());
			params.put("areaPower", projectMaintenance.getOfficeCode());
			// 抄送办事处主任
			List<User> users = userManageService.queryUserWithRoleIdAndDpNoOrInAreaPower(params);
			for (User user : users) {
				ccs.add(user.getEmail());
			}
			// 抄送服务交付验收小组群组邮箱
			String acceptanceMail = basicDataService.querySysArg("pm.project.maintenance.serviceDelivery.mail.user");
			if (StringUtils.isNotBlank(acceptanceMail)) {
			    ccs.add(acceptanceMail);
			} else {
				String cc = StringEscUtil.getText("pm.project.maintenance.serviceDelivery.mail.user");
				cc = projectDao.queryMailByUsername(cc);
				if (StringUtils.isNotBlank(cc)) {
					ccs.add(cc);
				}
			}
			
			Map<String, Object> context = new HashMap<String, Object>();
			//context.put("username", salesNames.toString());
			context.put("username", StringUtils.join(salesNames, "、"));
			context.put("tos", StringUtils.join(tos, ";"));
			context.put("ccs",StringUtils.join(ccs, ";"));
//			context.put("attachFileNames", attachFiles.toString());
			context.put("templateCode", "maintenanceServiceReportInfo");
			context.put("projectName", projectMaintenance.getProjectName());
			context.put("infoType", "通知");
			context.put("deliverName", deliverName);
			context.put("serviceName", serviceName);
			context.put("serviceCount", serviceCount);
			context.put("remainedCount", yearCount - serviceCount);
			context.put("content", yearCount - serviceCount > 0 ? "请持续跟踪" : "服务周期内已全部交付完成");
			context.put("officeName", projectMaintenance.getOfficeName());
			NotificationTemplateUtil.keepMail(context);
        }
		return false;
	}
	
	@Override
	public List<Map<String, Object>> selectProjectMaintenanceServiceDeliveryList(
			ProjectMaintenanceVO projectMaintenance, DisplayParam displayParam) {
		Map<String, Map<String, Object>> configMap = queryServiceDeliveryConfigMap();
		if (configMap == null || configMap.isEmpty()) {
			return Collections.emptyList();
		}
		if (projectMaintenance == null) {
			projectMaintenance = new ProjectMaintenanceVO();
		}
		projectMaintenance.setServiceTypes(configMap.keySet());
		List<Map<String, Object>> list = projectDao.selectProjectMaintenanceServiceDeliveryList(projectMaintenance, displayParam);
		Date serviceDate = projectMaintenance.getServiceDate() != null ? projectMaintenance.getServiceDate() : new Date();
		for (Iterator<Map<String, Object>> iterator = list.iterator(); iterator.hasNext();) {
			Map<String, Object> serviceDelivery = iterator.next();
        	String serviceType = (String) serviceDelivery.get("serviceType");
        	Map<String, Object> config = configMap.get(serviceType);
        	if (config == null || config.isEmpty()) {
        		iterator.remove();
        		continue;
        	}
        	// 获取对应的参数
//        	String deliverName = (String) (config.get("deliverName") != null ? config.get("deliverName") : "");
			String serviceName = (String) (config.get("serviceName") != null ? config.get("serviceName") : "");
			Integer yearCount = (Integer) (config.get("yearCount") != null ? config.get("yearCount") : 4);
//			String serviceYear = (String) (config.get("serviceYear") != null ? config.get("serviceYear") : "");
//			String serviceCode = (String) (config.get("serviceCode") != null ? config.get("serviceCode") : "");
			Integer deliverId = (Integer) (config.get("deliverId") != null ? config.get("deliverId") : 0);
			
//			// 判断是否在服务期限内
//			if (!Boolean.TRUE.equals(
//					Boolean.parseBoolean(String.valueOf(serviceDelivery.get(serviceType  + "Enable"))))) {
//				continue;
//			}

			// 查询项目维护对应的项目上传交付件的次数
			Date[] nearlyYearDates = DateUtil.getNearlyYearDates(serviceDate,
					(Date) serviceDelivery.get(serviceType + "StartTime"),
					(Date) serviceDelivery.get(serviceType + "EndTime"));
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("projectId", serviceDelivery.get("projectId"));
			map.put("deliverId", deliverId);
			map.put("serviceType", serviceType);
			map.put("checkDate", true);
			map.put("serviceDate", serviceDate);
			map.put("startDate", nearlyYearDates[0]);
			map.put("endDate", nearlyYearDates[1]);
			map.put("mergeQuarterCount", true);
			Map<String, Long> counts = this.queryProjectMaintenanceDeliverCount(map);
			Long serviceCount = counts.get("count");
			
			serviceDelivery.put("serviceName", serviceName);
			serviceDelivery.put("yearCount", yearCount);
			serviceDelivery.put("serviceCount", serviceCount);
			serviceDelivery.put("quarterCount", counts.get("quarterCount"));
			serviceDelivery.put("hasQuarterDeliveried", serviceDelivery.get("hasQuarterDeliveried") != null ? serviceDelivery.get("hasQuarterDeliveried") : (Long.valueOf(String.valueOf(counts.get("quarterCount"))) > 0 ? 1 : 0));
			serviceDelivery.put("remainedCount", yearCount - serviceCount);
			serviceDelivery.put("serviceStartDate", nearlyYearDates[0]);
			serviceDelivery.put("serviceEndDate", nearlyYearDates[1]);
		}
		return list;
	}

	private Map<String, Long> queryProjectMaintenanceDeliverCount(Map<String, Object> params) {
		return projectDao.queryProjectMaintenanceDeliverCount(params);
	}
	
	/**
	 * 根据键值对匹配每个服务对应的交付件、服务名、编码、年服务次数等信息
	 * @param config
	 * @return
	 */
	public Map<String, Map<String, Object>> queryServiceDeliveryConfigMap() {
		Map<String, Map<String, Object>> serviceConfigMap = new HashMap<String, Map<String,Object>>();
		try {
			String config = basicDataService.querySysArg("pm.project.maintenance.serviceDelivery.deliverFile");
			serviceConfigMap = matchServiceDeliveryConfigMap(config, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serviceConfigMap;
	}
	
	public Map<String, Map<String, Object>> matchServiceDeliveryConfigMap(String config, String key) {
		Map<String, Map<String, Object>> serviceConfigMap = new HashMap<String, Map<String,Object>>();
		try {
			if (StringUtils.isNotBlank(config)) {
				// 默认按serviceCode分组
				key = StringUtils.defaultIfBlank(key, "serviceCode");
				// 查询服务的每年次数、服务类型
				Pattern p = Pattern.compile("([^$;]*)\\$([^$]*)\\$([^$]*)\\$([^$]*)\\$([^$]*)\\$([^$]*)(\\$;?)");
				Matcher m = p.matcher(config);
				String deliverNames = "";
				String serviceName = "";
				Integer yearCount = 4;
				String serviceYear = "";
				String serviceCode = "";
				String deliverIds = "0";
				while (m.find()) {
					deliverNames = m.group(1);
					serviceName = m.group(2);
					serviceYear = m.group(3);
					yearCount = Integer.parseInt(m.group(4));
					serviceCode = m.group(5);
					deliverIds = m.group(6);
					
					String[] pdName = deliverNames.split(",");
					for (int i = 0; i < pdName.length; i++) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("deliverName", pdName[i]);
						map.put("deliverNames", deliverNames);
						map.put("serviceName", serviceName);
						map.put("serviceYear", serviceYear);
						map.put("yearCount", yearCount);
						map.put("serviceCode", serviceCode);
						map.put("deliverIds", deliverIds);
						serviceConfigMap.put(String.valueOf(map.get(key)), map);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serviceConfigMap;
	}

	@Override
    public ProjectSupervisionVO selectProjectSupervisionById(Integer id) {
        return projectDao.selectProjectSupervisionById(id);
    }

    @Override
    public List<ProjectSupervisionVO> selectProjectSupervisionList(ProjectSupervisionVO projectSupervision) {
        return projectDao.selectProjectSupervisionList(projectSupervision);
    }

    @Override
    public List<ProjectSupervisionVO> selectProjectSupervisionVOList(ProjectSupervisionVO projectSupervision) {
        return projectDao.selectProjectSupervisionVOList(projectSupervision);
    }
    
    @Override
    public List<Map<String, Object>> selectProjectSupervisionMapList(ProjectSupervisionVO projectSupervision) {
        return projectDao.selectProjectSupervisionMapList(projectSupervision);
    }

    @Override
    public List<Map<String, Object>> selectProjectSupervisionMapList(ProjectSupervisionVO projectSupervision, DisplayParam displayParam) {
        return projectDao.selectProjectSupervisionMapList(projectSupervision, displayParam);
    }

    @Override
    @Transactional
    public Integer insertOrUpdateProjectSupervision(ProjectSupervision projectSupervision) {
        Integer supervisionId = projectDao.insertOrUpdateProjectSupervision(projectSupervision);
        return supervisionId;
    }

    @Override
    @Transactional
    public void updateSoleAgentLendProject() {
        String currentUser = null;
        try {
            currentUser = UserContext.getUserContext().getUsername();
        } catch (Exception e) {}
        if (StringUtils.isBlank(currentUser)){
            currentUser = "sys";
        }
        SqlMapClientTemplate sqlMapClientTemplate = ((ProjectDaoImpl) projectDao).getSqlMapClientTemplate();
        try {
            sqlMapClientTemplate.insert("createTempSoleAgentProjectTable");
            sqlMapClientTemplate.insert("createTempNewOrderContractInfoTable");
            sqlMapClientTemplate.insert("createTempOldOrderContractInfoTable");

            List<Project> projects = projectDao.querySoleAgentProject();
            for (Project project : projects) {
                mergeOldSoleAgentLendProjectContract(project, currentUser);

                mergeNewSoleAgentLendProjectContract(project, currentUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlMapClientTemplate.delete("dropTempSoleAgentProjectTable");
            sqlMapClientTemplate.delete("dropTempNewOrderContractInfoTable");
            sqlMapClientTemplate.delete("dropTempOldOrderContractInfoTable");
        }
    }
    
    private void mergeOldSoleAgentLendProjectContract(Project project, String currentUser) {
        if (project == null || project.getProjectId() == 0) {
            return;
        }
        Integer projectId = project.getProjectId();
        List<Map<String, Object>> orderContractInfos = projectDao.queryProjectOldOrderContractInfo(projectId );
        if (orderContractInfos.isEmpty()) {
            return;
        }
        for (Map<String, Object> orderContractInfo : orderContractInfos) {
            String contractNo = StringUtils.trimToEmpty((String) orderContractInfo.get("contractNo"));
            String projectType = StringUtils.trimToEmpty((String) orderContractInfo.get("projectType"));
            // 合同不为空则进行合并
            if (StringUtils.isNotBlank(contractNo)) {
                // 查询最大的组编码
                String projectGroupCode = projectDao.queryMaxProjectGroupCode();
                String pre = MessageUtil.PROJECT_GROUPCODE_PRE;// 组编码前缀
                // 如果查询的组编码为空，则置为prj_gp1，否则置为最大值+1
                projectGroupCode = ((projectGroupCode == null) ? (pre + "1") : pre + (Integer.valueOf(projectGroupCode.replace(pre, "")) + 1));
                project.setProjectGroupCode(projectGroupCode);
                project.setProjectGroupName("总代借货项目合同合并");
                project.setContractNo(contractNo);
                project.setCreateBy(currentUser);
                project.setCreateTime(new Date());
                
                if (StringUtils.isBlank(project.getProjectType())) {
                	project.setProjectType(StringUtils.defaultIfBlank(projectType, MessageUtil.PROJECT_TYPE_AFTERSALES));
                }
                if (StringUtils.isBlank(project.getSmsProjectCode())) {
                    String projectCode = project.getProjectCode();
                    project.setSmsProjectCode(projectCode.substring(0, projectCode.indexOf("-")));
                }
                projectDao.insertProjectGroup(project);// 插入到表pm_project_group-项目组信息表
                projectDao.insertProjectContract(project);// 插入到表pm_project_contract-项目合同关联表
                project.setProjectCode(project.getProjectCode() + "-his");
                projectDao.insertProjectGroupRelationship(project);// 插入到表pm_project_group_relationship
            }
        }
    }

    private void mergeNewSoleAgentLendProjectContract(Project project, String currentUser) {
        if (project == null || project.getProjectId() == 0) {
            return;
        }
        Integer projectId = project.getProjectId();
        List<Map<String, Object>> orderContractInfos = projectDao.queryProjectNewOrderContractInfo(projectId);
        Set<String> orderExecNumbers = new HashSet<>();
        Set<String> contractNos = new HashSet<>();
        if (orderContractInfos.isEmpty()) {
            return;
        }
        for (Map<String, Object> orderContractInfo : orderContractInfos) {
            String orderExecNumber = StringUtils.trimToEmpty((String) orderContractInfo.get("orderExecNumber"));
            String contractNo = StringUtils.trimToEmpty((String) orderContractInfo.get("contractNo"));
            String projectType = StringUtils.trimToEmpty((String) orderContractInfo.get("projectType"));
//            String salesType = StringUtils.trimToEmpty((String) orderContractInfo.get("salesType"));
            // 合同不为空则进行合并
            if (StringUtils.isNotBlank(contractNo)) {
                // 查询最大的组编码
                String projectGroupCode = projectDao.queryMaxProjectGroupCode();
                String pre = MessageUtil.PROJECT_GROUPCODE_PRE;// 组编码前缀
                // 如果查询的组编码为空，则置为prj_gp1，否则置为最大值+1
                projectGroupCode = ((projectGroupCode == null) ? (pre + "1") : pre + (Integer.valueOf(projectGroupCode.replace(pre, "")) + 1));
                project.setProjectGroupCode(projectGroupCode);
                project.setProjectGroupName("总代借货项目合同合并");
                project.setContractNo(contractNo);
                project.setCreateBy(currentUser);
                project.setCreateTime(new Date());
                
                if (StringUtils.isBlank(project.getProjectType())) {
                	project.setProjectType(StringUtils.defaultIfBlank(projectType, MessageUtil.PROJECT_TYPE_AFTERSALES));
                }
                if (StringUtils.isBlank(project.getSmsProjectCode())) {
                    String projectCode = project.getProjectCode();
                    project.setSmsProjectCode(projectCode.substring(0, projectCode.indexOf("-")));
                }
                projectDao.insertProjectGroup(project);// 插入到表pm_project_group-项目组信息表
                projectDao.insertProjectContract(project);// 插入到表pm_project_contract-项目合同关联表
                projectDao.insertProjectGroupRelationship(project);// 插入到表pm_project_group_relationship

                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("createBy", currentUser);
                paramMap.put("projectId", projectId);
                paramMap.put("contractNo", contractNo);
                // projectDao.insertMergeContract(paramMap);
                projectDao.insertMergeProduct(paramMap);

                orderExecNumbers.add(orderExecNumber);
                contractNos.add(contractNo);
                
                // 失效项目中已经不存在的合同以及失效项目中旧的借货合同
                paramMap = new HashMap<String, Object>();
                paramMap.put("projectId", projectId);
//                paramMap.put("orderExecNumbers", new ArrayList<>(orderExecNumbers));
                paramMap.put("contractNos", new ArrayList<>(contractNos));
                paramMap.put("salesTypes", new String[] { "14" });// 14-销售类借货，
                                                                  // 02-借转销，01-正常订单
                paramMap.put("updateBy", currentUser);
                projectDao.invalidSoleAgentProjectContract(paramMap);// 插入到表pm_project_group_relationship

                projectDao.deleteProjectUnlinkedContractProductLine(projectId);
                
                paramMap.put("defaultSalesType", "01");
                projectDao.updateProjectSalesType(paramMap);
                
                orderExecNumbers.clear();
                contractNos.clear();
            }
        }
    }
    
    @SuppressWarnings("unused")
    private void mergeNewSoleAgentLendProjectContractBak(Project project, String currentUser) {
        if (project == null || project.getProjectId() == 0) {
            return;
        }
        Integer projectId = project.getProjectId();
        List<Map<String, Object>> orderContractInfos = projectDao.queryProjectNewOrderContractInfo(projectId);
        Set<String> orderExecNumbers = new HashSet<>();
        Set<String> contractNos = new HashSet<>();
        if (orderContractInfos.isEmpty()) {
            return;
        }
        Map<String, Set<String>> salesTypeOrderExecNumbers = new HashMap<>();
        Map<String, Set<String>> salesTypeContractNos = new HashMap<>();
        for (Map<String, Object> orderContractInfo : orderContractInfos) {
            String orderExecNumber = StringUtils.trimToEmpty((String) orderContractInfo.get("orderExecNumber"));
            String contractNo = StringUtils.trimToEmpty((String) orderContractInfo.get("contractNo"));
            String salesType = StringUtils.trimToEmpty((String) orderContractInfo.get("salesType"));
            String projectType = StringUtils.trimToEmpty((String) orderContractInfo.get("projectType"));
            // 合同不为空则进行合并
            if (StringUtils.isNotBlank(contractNo)) {
                // 查询最大的组编码
                String projectGroupCode = projectDao.queryMaxProjectGroupCode();
                String pre = MessageUtil.PROJECT_GROUPCODE_PRE;// 组编码前缀
                // 如果查询的组编码为空，则置为prj_gp1，否则置为最大值+1
                projectGroupCode = ((projectGroupCode == null) ? (pre + "1") : pre + (Integer.valueOf(projectGroupCode.replace(pre, "")) + 1));
                project.setProjectGroupCode(projectGroupCode);
                project.setProjectGroupName("总代借货项目合同合并");
                project.setContractNo(contractNo);
                project.setCreateBy(currentUser);
                project.setCreateTime(new Date());
                
                if (StringUtils.isBlank(project.getProjectType())) {
                	project.setProjectType(StringUtils.defaultIfBlank(projectType, MessageUtil.PROJECT_TYPE_AFTERSALES));
                }
                if (StringUtils.isBlank(project.getSmsProjectCode())) {
                    String projectCode = project.getProjectCode();
                    project.setSmsProjectCode(projectCode.substring(0, projectCode.indexOf("-")));
                }
                projectDao.insertProjectGroup(project);// 插入到表pm_project_group-项目组信息表
                projectDao.insertProjectContract(project);// 插入到表pm_project_contract-项目合同关联表
                projectDao.insertProjectGroupRelationship(project);// 插入到表pm_project_group_relationship

                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("createBy", currentUser);
                paramMap.put("projectId", projectId);
                paramMap.put("contractNo", contractNo);
                // projectDao.insertMergeContract(paramMap);
                projectDao.insertMergeProduct(paramMap);

                orderExecNumbers.add(orderExecNumber);
                contractNos.add(contractNo);
                
                Set<String> salesTypeOrderExecNumber = salesTypeOrderExecNumbers.get(salesType);
                Set<String> salesTypeContractNo = salesTypeContractNos.get(salesType);
                if (salesTypeOrderExecNumber == null) {
                    salesTypeOrderExecNumber = new HashSet<String>();
                }
                if (salesTypeContractNo == null) {
                    salesTypeContractNo = new HashSet<String>();
                }
                salesTypeOrderExecNumber.add(orderExecNumber);
                salesTypeContractNo.add(contractNo);
                salesTypeOrderExecNumbers.put(salesType, salesTypeOrderExecNumber);
                salesTypeContractNos.put(salesType, salesTypeContractNo);
            }
        }

        if (!contractNos.isEmpty()) {
            if (salesTypeContractNos.containsKey("01") && salesTypeContractNos.size() > 1) {
                for (String salesType : salesTypeContractNos.keySet()) {
                    if (!"01".equals(salesType)) {
//                        orderExecNumbers.removeAll(salesTypeOrderExecNumbers.get(salesType));
                        contractNos.removeAll(salesTypeContractNos.get(salesType));
                    }
                }
            }
            // 失效项目中已经不存在的合同以及失效项目中旧的借货合同
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("projectId", projectId);
//            paramMap.put("orderExecNumbers", new ArrayList<>(orderExecNumbers));
            paramMap.put("contractNos", new ArrayList<>(contractNos));
            paramMap.put("salesTypes", new String[] { "14" });// 14-销售类借货，
                                                              // 02-借转销，01-正常订单
            paramMap.put("updateBy", currentUser);
            projectDao.invalidSoleAgentProjectContract(paramMap);// 插入到表pm_project_group_relationship

            projectDao.deleteProjectUnlinkedContractProductLine(projectId);
            
            paramMap.put("defaultSalesType", "01");
            projectDao.updateProjectSalesType(paramMap);
        }
    }
    
    @Override
    public List<Map<String, Object>> queryMarketRelations() {
		return projectDao.queryMarketRelations();
	}
    
    @Override
    public List<Map<String, Object>> selectContractAcceptanceDeliveryInfo(Map<String, Object> params) {
        return projectDao.selectContractAcceptanceDeliveryInfo(params);
    }
    
}
