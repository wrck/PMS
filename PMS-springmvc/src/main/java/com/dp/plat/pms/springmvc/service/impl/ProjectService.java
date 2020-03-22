package com.dp.plat.pms.springmvc.service.impl;

import com.dp.plat.pms.springmvc.entity.Project;
import com.dp.plat.pms.springmvc.service.IProjectService;
import com.dp.plat.pms.springmvc.vo.ProjectVO;
import com.dp.plat.service.ProjectServiceImpl;
import com.dp.plat.supervision.entity.ProjectSupervision;
import com.dp.plat.supervision.vo.ProjectSupervisionVO;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.NotificationTemplateUtil;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.dao.AbstractBaseMapper;
import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.data.bean.CallBack;
import com.dp.plat.data.bean.Contract;
import com.dp.plat.data.bean.Department;
import com.dp.plat.data.bean.Instruction;
import com.dp.plat.data.bean.Item;
import com.dp.plat.data.bean.Notification;
import com.dp.plat.data.bean.NotificationTemplate;
import com.dp.plat.data.bean.OrderDataFromSap;
import com.dp.plat.data.bean.Product;
import com.dp.plat.data.bean.ProjectDeliver;
import com.dp.plat.data.bean.ProjectMaintenance;
import com.dp.plat.data.bean.ProjectMember;
import com.dp.plat.data.bean.ProjectPlanEvent;
import com.dp.plat.data.bean.ProjectTask;
import com.dp.plat.data.bean.ProjectWeekly;
import com.dp.plat.data.bean.ShipmentInfo;
import com.dp.plat.data.bean.SoftChangeLog;
import com.dp.plat.data.bean.WeeklyContent;
import com.dp.plat.data.bean.WeeklyFeedback;
import com.dp.plat.data.vo.ProjectMaintenanceVO;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.param.Person;
import com.dp.plat.param.RealProductLineBean;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.dao.ProjectMapper;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * Created by CodeGenerator
 */
@Service("pmProjectService")
public class ProjectService extends ProjectServiceImpl /*extends AbstractBaseService<ProjectMapper, Project> */implements IProjectService, IAbstractBaseService<Project> {
	
	@Autowired
	protected ProjectMapper dao;

	@Override
	public int deleteByPrimaryKey(Object pk) {
		return dao.deleteByPrimaryKey(pk);
	}

	@Override
	public int insert(Project project) {
		try {
			Class<?> objClass = project.getClass();
			Method method = objClass.getMethod("setCreateBy", String.class);
			method.invoke(project, UserContext.getCurrentUser().getUserName());
		} catch (Exception e) {
		}
		return dao.insert(project);
	}

	@Override
	public int insertSelective(Project project) {
		try {
			Class<?> objClass = project.getClass();
			Method method = objClass.getMethod("setCreateBy", String.class);
			method.invoke(project, UserContext.getCurrentUser().getUserName());
		} catch (Exception e) {
		}
		return dao.insertSelective(project);
	}

	@Override
	public Project selectByPrimaryKey(Object pk) {
		return dao.selectByPrimaryKey(pk);
	}

	@Override
	public int updateByPrimaryKey(Project project) {
		try {
			Class<?> objClass = project.getClass();
			Method method = objClass.getMethod("setUpdateBy", String.class);
			method.invoke(project, UserContext.getCurrentUser().getUserName());
		} catch (Exception e) {
		}
		return dao.updateByPrimaryKey(project);
	}

	@Override
	public int updateByPrimaryKeySelective(Project project) {
		try {
			Class<?> objClass = project.getClass();
			Method method = objClass.getMethod("setUpdateBy", String.class);
			method.invoke(project, UserContext.getCurrentUser().getUserName());
		} catch (Exception e) {
		}
		return dao.updateByPrimaryKeySelective(project);
	}

	/**
	 * 查询满足条件的记录条数记录
	 * 
	 * @param pageParam
	 * @return
	 */
	public long countBySelectivePageable(PageParam<Object> pageParam) {
		return dao.countBySelectivePageable(pageParam);
	}

	public long countBySelective(Project project) {
		return dao.countBySelective(project);
	}

	/**
	 * 分页查询满足条件的记录
	 * 
	 * @param pageParam
	 * @return
	 */
	public List<Object> selectBySelectivePageable(PageParam<Object> pageParam) {
		return dao.selectBySelectivePageable(pageParam);
	}

	/**
	 * 查询满足条件的所有记录
	 * 
	 * @param project
	 * @return
	 */
	public List<Project> selectBySelective(Project project) {
		return dao.selectBySelective(project);
	}
	
	@Override
	public Integer insertProject(ProjectVO project) {
		log("创建项目");
		String projectType = project.getProjectType();
		if (ProjectConstant.ProjectType.JF_SALES_PROJECT.equals(projectType)) {
//			super.insertProject(project);
		} else if (ProjectConstant.ProjectType.AF_SALES_PROJECT.equals(projectType)) {
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
			this.addFixedNotification(MessageUtil.NOTIFICATION_CODE_102, pid);
	
			// 更新项目闭环时间
			projectDao.updateProjectDirectCloseTime(pid);
			this.updateProjectCloseProcessState(pid, MessageUtil.PROJECT_CLOSE_PROCESS_STATE_50);
		} else {
			// 增加通知信息
			this.addFixedNotification(MessageUtil.NOTIFICATION_CODE_101, pid);
		}
		this.updateChannel(project);// 更新渠道信息
		return pid;
	}
}
