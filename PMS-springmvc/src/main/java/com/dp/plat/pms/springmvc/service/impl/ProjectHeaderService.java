package com.dp.plat.pms.springmvc.service.impl;

import java.util.Collection;
import com.dp.plat.pms.springmvc.vo.ProjectVO;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import java.util.Date;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.data.bean.OrderDataFromSap;
import org.springframework.stereotype.Service;
import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.service.SendMailService;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;
import org.springframework.transaction.annotation.Propagation;
import com.dp.plat.data.bean.Project;
import java.lang.reflect.Method;
import org.springframework.beans.factory.annotation.Autowired;
import com.dp.plat.dao.ProjectDao;
import com.dp.plat.service.CallBackService;
import java.util.HashMap;
import java.util.List;
import com.dp.plat.core.service.impl.AbstractBaseService;
import java.util.Map;
import com.dp.plat.service.PmClosedLoopService;
import com.dp.plat.service.ProjectServiceImpl;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.core.context.UserContext;
import java.util.HashSet;
import java.util.Set;
import com.dp.plat.pms.springmvc.dao.ProjectHeaderMapper;
import com.dp.plat.util.NotificationTemplateUtil;
import org.activiti.engine.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.dp.plat.core.vo.PermissionResult;
import com.dp.plat.util.MessageUtil;

/**
 *
 * Created by CodeGenerator
 */
@Service("projectHeaderService")
public class ProjectHeaderService extends ProjectServiceImpl
		/* extends AbstractBaseService<ProjectMapper, Project> */ implements IProjectHeaderService,
		IAbstractBaseService<ProjectHeader> {

    @Autowired
    protected ProjectHeaderMapper dao;

    @Autowired
    public void setProjectDao(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    @Autowired
    public void setBasicDataService(BasicDataService basicDataService) {
        this.basicDataService = basicDataService;
    }

    @Autowired
    public void setCallBackService(CallBackService callBackService) {
        this.callBackService = callBackService;
    }

    @Autowired
    public void setPmClosedLoopService(PmClosedLoopService pmClosedLoopService) {
        this.pmClosedLoopService = pmClosedLoopService;
    }

    @Autowired
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    @Autowired
    public void setSendMailService(SendMailService sendMailService) {
        this.sendMailService = sendMailService;
    }

    @Override
    public int deleteByPrimaryKey(Object pk) {
        return dao.deleteByPrimaryKey(pk);
    }

    @Override
    public int insert(ProjectHeader project) {
        if (project == null) {
            return 0;
        }
        Class<?> objClass = project.getClass();
        try {
            Method method = objClass.getMethod("setCreateBy", String.class);
            method.invoke(project, UserContext.getCurrentUser().getUserName());
        } catch (Exception e) {
        }
        try {
            Method method = objClass.getMethod("setCreateTime", Date.class);
            method.invoke(project, new Date());
        } catch (Exception e) {
        }
        try {
            Method method = objClass.getMethod("getEffectiveFrom");
            Object effectiveFrom = method.invoke(project);
            if (effectiveFrom == null) {
                method = objClass.getMethod("setEffectiveFrom", Date.class);
                method.invoke(project, new Date());
            }
        } catch (Exception e) {
        }
        return dao.insert(project);
    }

    @Override
    public int insertSelective(ProjectHeader project) {
        if (project == null) {
            return 0;
        }
        Class<?> objClass = project.getClass();
        try {
            Method method = objClass.getMethod("setCreateBy", String.class);
            method.invoke(project, UserContext.getCurrentUser().getUserName());
        } catch (Exception e) {
        }
        try {
            Method method = objClass.getMethod("setCreateTime", Date.class);
            method.invoke(project, new Date());
        } catch (Exception e) {
        }
        try {
            Method method = objClass.getMethod("getEffectiveFrom");
            Object effectiveFrom = method.invoke(project);
            if (effectiveFrom == null) {
                method = objClass.getMethod("setEffectiveFrom", Date.class);
                method.invoke(project, new Date());
            }
        } catch (Exception e) {
        }
        return dao.insertSelective(project);
    }

    @Override
    public ProjectHeader selectByPrimaryKey(Object pk) {
        return dao.selectByPrimaryKey(pk);
    }
    
    @Override
    public ProjectVO selectVOByProjectId(Integer projectId) {
        return dao.selectVOByProjectId(projectId);
    }

    @Override
    public int updateByPrimaryKey(ProjectHeader project) {
        if (project == null) {
            return 0;
        }
        Class<?> objClass = project.getClass();
        try {
            Method method = objClass.getMethod("setUpdateBy", String.class);
            method.invoke(project, UserContext.getCurrentUser().getUserName());
        } catch (Exception e) {
        }
        try {
            Method method = objClass.getMethod("setUpdateTime", Date.class);
            method.invoke(project, new Date());
        } catch (Exception e) {
        }
        return dao.updateByPrimaryKey(project);
    }

    @Override
    public int updateByPrimaryKeySelective(ProjectHeader project) {
        if (project == null) {
            return 0;
        }
        Class<?> objClass = project.getClass();
        try {
            Method method = objClass.getMethod("setUpdateBy", String.class);
            method.invoke(project, UserContext.getCurrentUser().getUserName());
        } catch (Exception e) {
        }
        try {
            Method method = objClass.getMethod("setUpdateTime", Date.class);
            method.invoke(project, new Date());
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

    public long countBySelective(ProjectHeader project) {
        return dao.countBySelective(project);
    }

    /**
	 * 分页查询满足条件的记录
	 * 
	 * @param pageParam
	 * @return
	 */
    public List<Object> selectBySelectivePageable(PageParam<?> pageParam) {
        return dao.selectBySelectivePageable(pageParam);
    }

    /**
	 * 查询满足条件的所有记录
	 * 
	 * @param project
	 * @return
	 */
    public List<ProjectHeader> selectBySelective(ProjectHeader project) {
        return dao.selectBySelective(project);
    }

    @Override
    public long countUncreateProjectList(PageParam<Object> pageParam) {
        return dao.countUncreateProjectList(pageParam);
    }

    @Override
    public List<Object> selectUncreateProjectList(PageParam<Object> pageParam) {
        return dao.selectUncreateProjectList(pageParam);
    }

    @Override
    public Project queryProjectByContractNoAndType(String contractNo, String projectType) {
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put("contractNo", contractNo);
        params.put("projectType", projectType);
        return dao.queryProjectByContractNoAndType(params);
    }

    @Override
    public Integer queryProjectContractCountByContractNoAndType(String contractNo, String projectType) {
        return super.queryProjectContractCountByContractNoAndType(contractNo, projectType);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public int insertProject(Project project) throws Exception {
        log("创建项目");
        String projectType = project.getProjectType();
        Integer pid = 0;
        if (ProjectConstant.ProjectType.JF_SALES_PROJECT.equals(projectType)) {
            super.insertProject(project);
        } else if (ProjectConstant.ProjectType.AF_SALES_PROJECT.equals(projectType) || ProjectConstant.ProjectType.AF_XX_PROJECT.equals(projectType)) {
            Project p = this.queryProjectByContractNoAndType(project.getContractNo(), project.getProjectType());
            // p中的部分属性放置到project中
            project = putProperties(project, p);
            // 插入到表pm_project_header
            this.insertSelective((ProjectHeader) project);
            // - 项目表
            pid = project.getProjectId();
            String projectGroupCode = project.getProjectGroupCode();
            if (projectGroupCode == null) {
                // project.setProjectType(0);//页面传递过来，无需在此编写hard code
                // FIXME 并发情况下，会获取到相同的组编码，造成错误
                // 查询最大的组编码
                projectGroupCode = projectDao.queryMaxProjectGroupCode();
                // 组编码前缀
                String pre = MessageUtil.PROJECT_GROUPCODE_PRE;
                // 如果查询的组编码为空，则置为prj_gp1，否则置为最大值+1
                projectGroupCode = ((projectGroupCode == null) ? (pre + "1") : pre + (Integer.valueOf(projectGroupCode.replace(pre, "")) + 1));
                project.setProjectGroupCode(projectGroupCode);
            }
            if (StringUtils.isBlank(project.getSmsProjectCode())) {
                String projectCode = project.getProjectCode();
                project.setSmsProjectCode(projectCode.substring(0, projectCode.indexOf("-")));
            }
            // 插入到表pm_project_group - 项目组信息表
            projectDao.insertProjectGroup(project);
            // 插入到表pm_project_contract -
            projectDao.insertProjectContract(project);
            // 项目合同关联表
            // 插入到表pm_project_group_relationship
            projectDao.insertProjectGroupRelationship(project);
            // - 项目组与项目关联表
            //			int shipmentState = projectDao.queryProjectShipmentState(project.getProjectId());
            //			project.setShipmentState(shipmentState);
            //			project.setProjectPlanState(MessageUtil.PROJECT_PLAN_STATE_40);// 尚未制定计划
            //			this.insertOrUpdateProjectState(project);// 插入或更新项目状态表
            // 保存的项目表id
            project.setProjectId(pid);
            if (StringUtils.isNotBlank(project.getServiceManagerCode())) {
                // 03-20
                project.setMemberRole(MessageUtil.DATATYPE_CODE03_20);
                project.setMemberCode(project.getServiceManagerCode());
                project.setMemberName(project.getServiceManagerCodeforjson());
                project.setFromFlag(MessageUtil.FLAG_FROM_PROJECT);
                // 查询邮件
                project.setEmail(this.getMails(project.getServiceManagerCode()));
                // 插入到表pm_project_member - 项目成员表
                this.insertProjectMember(project);
            }
            // 03-10
            project.setMemberRole(MessageUtil.DATATYPE_CODE03_10);
            project.setMemberCode(project.getSalesManCode());
            project.setMemberName(project.getSalesManName());
            try {
                project.setEmail(this.queryMailByUserNameFromOA(project.getSalesManCode()));
            } catch (Exception e) {
                project.setEmail(null);
                e.printStackTrace();
            }
            // 插入到表pm_project_member - 项目成员表
            this.insertProjectMember(project);
            // 保存项目经理信息
            if (StringUtils.isNotBlank(project.getProgramManagerCode())) {
                // 03-30
                project.setMemberRole(MessageUtil.DATATYPE_CODE03_30);
                project.setMemberCode(project.getProgramManagerCode());
                project.setMemberName(project.getProgramManagerCodeforjson());
                project.setFromFlag(MessageUtil.FLAG_FROM_PROJECT);
                // 查询邮件
                project.setEmail(this.getMails(project.getProgramManagerCode()));
                // 插入到表pm_project_member - 项目成员表
                this.insertProjectMember(project);
            }
            if (StringUtils.isNotBlank(project.getProgramManagerCodeB())) {
                project.setMemberCode(project.getProgramManagerCodeB());
                project.setMemberName(project.getProgramManagerCodeforjsonB());
                // 项目经理B归为从成员信息添加，便于区分
                project.setFromFlag(MessageUtil.FLAG_FROM_MEMBER);
                // 查询邮件
                project.setEmail(this.getMails(project.getProgramManagerCodeB()));
                // 插入到表pm_project_member - 项目成员表
                this.insertProjectMember(project);
            }
            // 保存产品信息
            List<OrderDataFromSap> orderDataList = this.queryOrderLineFromSapByContractNo(project);
            for (OrderDataFromSap od : orderDataList) {
                od.setProjectId(pid);
                projectDao.insertProjectProductLine(od);
            }
        //			// 不予跟踪邮件
        //			if (MessageUtil.PROJECT_STATE_DENY.equals(project.getProjectState())) {
        //				Map<String, Object> context = new HashMap<String, Object>();
        //
        //				context.put("templateCode", MessageUtil.NOTIFICATION_CODE_DENY_PRJ);
        //				context.put("username", getRealname() == null ? "" : getRealname());
        //				context.put("projectName", project.getProjectName());
        //				if (project.getServiceManagerCode() != null) {// 选择不予跟踪，确认有服务经理才发送通知
        //					context.put("tos", basicDataService.querySysArg(MessageUtil.GCGLB)
        //							+ this.getMails(project.getServiceManagerCode()));
        //					NotificationTemplateUtil.keepMail(context);
        //				}
        //
        //				// 增加通知信息
        //				this.addFixedNotification(MessageUtil.NOTIFICATION_CODE_102, pid);
        //
        //				// 更新项目闭环时间
        //				projectDao.updateProjectDirectCloseTime(pid);
        //				this.updateProjectCloseProcessState(pid, MessageUtil.PROJECT_CLOSE_PROCESS_STATE_50);
        //			} else {
        //				// 增加通知信息
        //				this.addFixedNotification(MessageUtil.NOTIFICATION_CODE_101, pid);
        //			}
        //			this.updateChannel(project);// 更新渠道信息
        }
        return pid;
    }

    @Override
    public void updateProjectByProjectId(Project project) {
        // project.setProjectState(MessageUtil.PROJECT_CREATE_STATE30);
    	String projectType = project.getProjectType();
    	Integer pid = 0;
    	if (ProjectConstant.ProjectType.JF_SALES_PROJECT.equals(projectType)) {
    		super.updateProjectByProjectId(project);
    	} else if (ProjectConstant.ProjectType.AF_SALES_PROJECT.equals(projectType) || ProjectConstant.ProjectType.AF_XX_PROJECT.equals(projectType)) {
	        project.setUpdateBy(UserContext.getUsername());
	        // 更新项目表信息
	        projectDao.updateProjectByProjectId(project);
	        project.setDataTypeCode(MessageUtil.DATATYPE_CODE03_20);
	        project.setFromFlag(MessageUtil.FLAG_FROM_PROJECT);
	        String procode = project.getProgramManagerCode();
	        String oldProjectState = StringUtils.trimToEmpty(project.getProjectState());
	        String newProjectState = MessageUtil.PROJECT_STATE_32.compareTo(oldProjectState) < 0 ? oldProjectState : MessageUtil.PROJECT_STATE_32;
	        boolean a = false;
	        if (procode != null && !"".equals(procode)) {
	            project.setDataTypeCode(MessageUtil.DATATYPE_CODE03_30);
	            project.setOldMemberCode(project.getOldProgramManagerCode());
	            // 更新项目成员表
	            a = this.updateProjectMember(project, project.getProgramManagerCode(), project.getProgramManagerCodeforjson());
	            // 服务经理发生变更会更新项目状态，这是需要判断项目经理是否已存在，若存在则更新项目状态；若项目经理发生变更也需要更新项目状态
	            if (a) {
	                // 指定项目经理后，更新指定项目状态，否则项目经理无法操作项目
	                // 更新项目状态
	                this.updateProjectStateByProjectId(project, MessageUtil.PROJECT_CREATE_STATE32);
	                // 更新想目状态（projectState）
	                this.updateProjectStatus(project.getProjectId(), newProjectState);
	            }
	        }
	        String procodeB = project.getProgramManagerCodeB();
	        boolean c = false;
	        if (procodeB != null && !"".equals(procodeB)) {
	            project.setDataTypeCode(MessageUtil.DATATYPE_CODE03_30);
	            project.setOldMemberCode(project.getOldProgramManagerCodeB());
	            project.setFromFlag(MessageUtil.FLAG_FROM_MEMBER);
	            // 更新项目成员表
	            c = this.updateProjectMember(project, project.getProgramManagerCodeB(), project.getProgramManagerCodeforjsonB());
	            // 服务经理发生变更会更新项目状态，这是需要判断项目经理是否已存在，若存在则更新项目状态；若项目经理发生变更也需要更新项目状态
	            if (c) {
	                // 指定项目经理后，更新指定项目状态，否则项目经理无法操作项目
	                // 更新项目状态
	                this.updateProjectStateByProjectId(project, MessageUtil.PROJECT_CREATE_STATE32);
	                // 更新想目状态（projectState）
	                this.updateProjectStatus(project.getProjectId(), newProjectState);
	            }
	        }
	    //		// 成功变更项目经理，终止在项目经理手中的闭环申请,回访申请
	    //		if (a || c) {
	    //			terminateProgramManagerActivities(project);
	    //			// 项目流程状态为“闭环结束”的项目更新项目经理，将流程状态改为“项目跟踪”
	    //			if (MessageUtil.PROJECT_CLOSE_PROCESS_STATE_50.equals(project.getCloseProcessState())) {
	    //			    this.updateProjectCloseProcessState(project.getProjectId(), MessageUtil.PROJECT_CLOSE_PROCESS_STATE_10);
	    //			}
	    //		}
	    //		this.updateChannel(project);// 更新渠道信息
	    //		this.updateProjectImplByProjectId(project);// 更新实施方式
    	}
    }
    
    @Override
	public ProjectVO queryProjectStateByProjectId(Integer projectId) {
		return dao.queryProjectStateByProjectId(projectId);
	}

	@Override
    public Map<String, Object> checkPermission(ProjectVO project) {
        return this.checkPermissionMap(project);
    }

    @Override
    public Map<String, Object> checkPermissionMap(ProjectVO project, String... permissions) {
        if (permissions != null) {
            Set<String> permissTypes = new HashSet<String>(permissions.length);
            for (String permission : permissions) {
                if (StringUtils.isNotBlank(permission)) {
                    String type = permission.split(":")[0];
                    permissTypes.add(type);
                }
            }
            return dao.checkPermission(project, StringUtils.join(permissTypes, ":|") + ":", UserContext.getCurrentPrincipal());
        } else {
            return dao.checkPermission(project, UserContext.getCurrentPrincipal());
        }
    }

    @Override
    public PermissionResult checkPermission(ProjectVO project, String... permissions) {
        if (!UserContext.checkPermission(permissions)) {
            return new PermissionResult(Boolean.FALSE, "没有权限进行该操作！");
        }
        Boolean isPermit = false;
        String permissionType = "";
        Collection<String> permissionSet = null;
        if (!UserContext.checkPermission("project:*") && project != null) {
            Map<String, Object> permission = this.checkPermissionMap(project, permissions);
            permissionSet = (Collection<String>) permission.get("permissions");
            Boolean allPerm = (Boolean) permission.get("all");
            if (Boolean.TRUE.equals(allPerm)) {
                isPermit = true;
                permissionType = "all";
            } else {
                String perms = StringUtils.join(permissions, ",");
                if (Boolean.TRUE.equals(permission.get("edit")) && perms.matches(".*project:(add|edit|delete|upload|import|list|detail)\\b,?.*")) {
                    isPermit = true;
                    permissionType = "edit";
                }
                if (Boolean.TRUE.equals(permission.get("view")) && perms.matches(".*project:(list|detail)\\b,?.*")) {
                    isPermit = true;
                    permissionType = "view";
                }
            }
        } else {
            isPermit = true;
            permissionType = "all";
        }
        return new PermissionResult(isPermit, permissionType, permissionSet);
    }

    @Override
    protected Project putProperties(Project project, Project p) {
        super.putProperties(project, p);
        try {
            if (project instanceof ProjectVO) {
                ((ProjectVO) project).setCustomInfo(((ProjectVO) p).getCustomInfo());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return project;
    }
}
