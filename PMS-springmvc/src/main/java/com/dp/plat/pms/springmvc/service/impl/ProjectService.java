package com.dp.plat.pms.springmvc.service.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.data.bean.OrderDataFromSap;
import com.dp.plat.data.bean.Project;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.dao.ProjectMapper;
import com.dp.plat.pms.springmvc.service.IProjectService;
import com.dp.plat.pms.springmvc.vo.ProjectVO;
import com.dp.plat.service.ProjectServiceImpl;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.NotificationTemplateUtil;

/**
 *
 * Created by CodeGenerator
 */
@Service("pmProjectService")
public class ProjectService extends ProjectServiceImpl /*extends AbstractBaseService<ProjectMapper, Project> */implements IProjectService, IAbstractBaseService<com.dp.plat.pms.springmvc.entity.Project> {

	// 组编码生成锁（按前缀分锁）。注意：仅适用于单节点部署；多节点需改用数据库唯一约束或独立的序列表
	private static final ConcurrentHashMap<String, ReentrantLock> PROJECT_GROUPCODE_LOCKS = new ConcurrentHashMap<>();
	
	@Autowired
	protected ProjectMapper dao;

	@Override
	public int deleteByPrimaryKey(Object pk) {
		return dao.deleteByPrimaryKey(pk);
	}

	@Override
	public int insert(com.dp.plat.pms.springmvc.entity.Project project) {
		try {
			Class<?> objClass = project.getClass();
			Method method = objClass.getMethod("setCreateBy", String.class);
			method.invoke(project, UserContext.getCurrentUser().getUserName());
		} catch (Exception e) {
		}
		return dao.insert(project);
	}

	@Override
	public int insertSelective(com.dp.plat.pms.springmvc.entity.Project project) {
		try {
			Class<?> objClass = project.getClass();
			Method method = objClass.getMethod("setCreateBy", String.class);
			method.invoke(project, UserContext.getCurrentUser().getUserName());
		} catch (Exception e) {
		}
		return dao.insertSelective(project);
	}

	@Override
	public com.dp.plat.pms.springmvc.entity.Project selectByPrimaryKey(Object pk) {
		return dao.selectByPrimaryKey(pk);
	}

	@Override
	public int updateByPrimaryKey(com.dp.plat.pms.springmvc.entity.Project project) {
		try {
			Class<?> objClass = project.getClass();
			Method method = objClass.getMethod("setUpdateBy", String.class);
			method.invoke(project, UserContext.getCurrentUser().getUserName());
		} catch (Exception e) {
		}
		return dao.updateByPrimaryKey(project);
	}

	@Override
	public int updateByPrimaryKeySelective(com.dp.plat.pms.springmvc.entity.Project project) {
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
	public long countBySelectivePageable(PageParam<?> pageParam) {
		return dao.countBySelectivePageable(pageParam);
	}

	public long countBySelective(com.dp.plat.pms.springmvc.entity.Project project) {
		return dao.countBySelective(project);
	}

	/**
	 * 分页查询满足条件的记录
	 * 
	 * @param pageParam
	 * @return
	 */
	public <V> List<V> selectBySelectivePageable(PageParam<?> pageParam) {
		return dao.selectBySelectivePageable(pageParam);
	}

	/**
	 * 查询满足条件的所有记录
	 * 
	 * @param project
	 * @return
	 */
	public List<com.dp.plat.pms.springmvc.entity.Project> selectBySelective(com.dp.plat.pms.springmvc.entity.Project project) {
		return dao.selectBySelective(project);
	}
	
	@Override
	@Transactional
	public int insertProject(Project project) throws Exception {
		log("创建项目");
		String projectType = project.getProjectType();
		if (ProjectConstant.ProjectType.JF_SALES_PROJECT.equals(projectType)) {
			super.insertProject(project);
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
			boolean projectGroupInserted = false;
			if (projectGroupCode == null) {
				// project.setProjectType(0);//页面传递过来，无需在此编写hard code
				String pre = MessageUtil.PROJECT_GROUPCODE_PRE;// 组编码前缀
				// 并发场景下按组编码前缀加锁，避免并发请求获取到相同的组编码；
				// 处于事务中时锁延迟到事务提交/回滚后释放，确保并发事务读取到已提交的最大值
				ReentrantLock groupCodeLock = PROJECT_GROUPCODE_LOCKS.computeIfAbsent(pre, k -> new ReentrantLock());
				groupCodeLock.lock();
				boolean lockReleased = false;
				try {
					// 查询最大的组编码
					projectGroupCode = projectDao.queryMaxProjectGroupCode();
					// 如果查询的组编码为空，则置为prj_gp1，否则置为最大值+1
					projectGroupCode = ((projectGroupCode == null) ? (pre + "1")
							: pre + (Integer.valueOf(projectGroupCode.replace(pre, "")) + 1));
					project.setProjectGroupCode(projectGroupCode);
					// 在锁内完成项目组信息表插入，确保生成的组编码已持久化（或随事务提交后对并发可见）
					projectDao.insertProjectGroup(project);// 插入到表pm_project_group - 项目组信息表
					projectGroupInserted = true;
					if (TransactionSynchronizationManager.isSynchronizationActive()) {
						TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
							@Override
							public void afterCompletion(int status) {
								groupCodeLock.unlock();
							}
						});
						lockReleased = true;
					}
				} finally {
					if (!lockReleased) {
						groupCodeLock.unlock();
					}
				}
			}
			if (StringUtils.isBlank(project.getSmsProjectCode())) {
				String projectCode = project.getProjectCode();
				project.setSmsProjectCode(projectCode.substring(0, projectCode.indexOf("-")));
			}
			if (!projectGroupInserted) {
				projectDao.insertProjectGroup(project);// 插入到表pm_project_group - 项目组信息表
			}
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
				// project.setEmail(this.getMails(project.getSalesManCode()));
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
		}
		Integer pid = null;
		return pid;
	}

	@Override
	public ProjectVO queryProjectByContractNoAndType(String contractNo, String projectType) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
