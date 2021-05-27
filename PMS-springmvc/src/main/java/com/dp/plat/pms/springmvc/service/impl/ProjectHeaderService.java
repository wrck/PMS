package com.dp.plat.pms.springmvc.service.impl;

import static com.dp.plat.core.param.RoleConstant.ROLE_ADMIN;
import static com.dp.plat.pms.springmvc.constant.RoleConstant.ROLE_PM_ADMIN;
import static com.dp.plat.pms.springmvc.constant.RoleConstant.ROLE_PM_AREA_MANAGER;
import static com.dp.plat.pms.springmvc.constant.RoleConstant.ROLE_PM_SUB_ADMIN;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.TaskService;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.core.service.IUserInfoService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.PermissionResult;
import com.dp.plat.core.vo.Result;
import com.dp.plat.dao.ProjectDao;
import com.dp.plat.data.bean.OrderDataFromSap;
import com.dp.plat.data.bean.Project;
import com.dp.plat.ehr.service.IEmployeeService;
import com.dp.plat.ehr.vo.EmployeeVO;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.ProcessType;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.ProcessType.TaskType;
import com.dp.plat.pms.springmvc.constant.RoleConstant;
import com.dp.plat.pms.springmvc.dao.PmWorkBenchMapper;
import com.dp.plat.pms.springmvc.dao.ProjectHeaderMapper;
import com.dp.plat.pms.springmvc.entity.PmWorkFlow;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.service.IProjectManageUserService;
import com.dp.plat.pms.springmvc.util.PermissionUtils;
import com.dp.plat.pms.springmvc.vo.PmWorkFlowVO;
import com.dp.plat.pms.springmvc.vo.ProjectProduct;
import com.dp.plat.pms.springmvc.vo.ProjectVO;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.CallBackService;
import com.dp.plat.service.PmClosedLoopService;
import com.dp.plat.service.ProjectServiceImpl;
import com.dp.plat.service.SendMailService;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.Util;

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
    private IProjectManageUserService projectManageUserService;
    
    @Autowired 
    private IUserInfoService userInfoService;
    
    @Autowired 
    private IEmployeeService employeeService;
    
    @Autowired
    private PmWorkBenchMapper pmWorkBenchDao;

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
    public ProjectVO selectVOByProjectId(Object projectId) {
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
	public List<ProjectProduct> queryProductInfoFromSmsByProjectCode(ProjectProduct product) {
		return dao.queryProductInfoFromSmsByProjectCode(product);
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
            String projectState = MessageUtil.PROJECT_STATE_CREATING;
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
                String pre = MessageUtil.PROJECT_GROUPCODE_PRE + "_" + projectType;
                // 如果查询的组编码为空，则置为prj_gp1，否则置为最大值+1
                projectGroupCode = ((projectGroupCode == null) ? (pre + "1") : pre + (Integer.valueOf(projectGroupCode.replace(pre, "")) + 1));
                project.setProjectGroupCode(projectGroupCode);
            }
            if (StringUtils.isBlank(project.getSmsProjectCode())) {
                String projectCode = project.getProjectCode();
                int indexOf = projectCode.indexOf("-");
                if (indexOf != -1) {
                	project.setSmsProjectCode(projectCode.substring(0, indexOf));
                } else {
                	project.setSmsProjectCode(projectCode);
                }
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
//                // 03-20
//                project.setMemberRole(MessageUtil.DATATYPE_CODE03_20);
//                project.setMemberCode(project.getServiceManagerCode());
//                project.setMemberName(project.getServiceManagerCodeforjson());
//                project.setFromFlag(MessageUtil.FLAG_FROM_PROJECT);
//                // 查询邮件
//                project.setEmail(this.getMails(project.getServiceManagerCode()));
//                // 插入到表pm_project_member - 项目成员表
//                this.insertProjectMember(project);
            	this.updateProjectMember(project, MessageUtil.DATATYPE_CODE03_20, project.getServiceManagerCode(), project.getServiceManagerCodeforjson(), MessageUtil.FLAG_FROM_PROJECT);
                projectState = MessageUtil.PROJECT_STATE_31;
            }
            // 保存销售代表
            if (StringUtils.isNotBlank(project.getSalesManCode())) {
//            	// 03-10
//	            project.setMemberRole(MessageUtil.DATATYPE_CODE03_10);
//	            project.setMemberCode(project.getSalesManCode());
//	            try {
//	            	project.setMemberName(project.getSalesManCode() + "-" + project.getSalesManName());
//	                project.setEmail(this.queryMailByUserNameFromOA(project.getSalesManCode()));
//	            } catch (Exception e) {
//	                project.setEmail(null);
//	                e.printStackTrace();
//	            }
//	            // 插入到表pm_project_member - 项目成员表
//	            this.insertProjectMember(project);
            	String salesManName = StringUtils.stripToEmpty(project.getSalesManName());
            	if (!salesManName.contains(project.getSalesManCode())) {
            		salesManName = project.getSalesManCode() + "-" + project.getSalesManName();
            	}
            	this.updateProjectMember(project, MessageUtil.DATATYPE_CODE03_10, project.getSalesManCode(), salesManName, MessageUtil.FLAG_FROM_PROJECT);
            }
            // 保存项目经理信息
            if (StringUtils.isNotBlank(project.getProgramManagerCode())) {
//                // 03-30
//                project.setMemberRole(MessageUtil.DATATYPE_CODE03_30);
//                project.setMemberCode(project.getProgramManagerCode());
//                project.setMemberName(project.getProgramManagerCodeforjson());
//                project.setFromFlag(MessageUtil.FLAG_FROM_PROJECT);
//                // 查询邮件
//                project.setEmail(this.getMails(project.getProgramManagerCode()));
//                // 插入到表pm_project_member - 项目成员表
//                this.insertProjectMember(project);
            	this.updateProjectMember(project, MessageUtil.DATATYPE_CODE03_30, project.getProgramManagerCode(), project.getProgramManagerCodeforjson(), MessageUtil.FLAG_FROM_PROJECT);
                projectState = MessageUtil.PROJECT_STATE_32;
            }
            if (StringUtils.isNotBlank(project.getProgramManagerCodeB())) {
//                project.setMemberCode(project.getProgramManagerCodeB());
//                project.setMemberName(project.getProgramManagerCodeforjsonB());
//                // 项目经理B归为从成员信息添加，便于区分
//                project.setFromFlag(MessageUtil.FLAG_FROM_MEMBER);
//                // 查询邮件
//                project.setEmail(this.getMails(project.getProgramManagerCodeB()));
//                // 插入到表pm_project_member - 项目成员表
//                this.insertProjectMember(project);
            	this.updateProjectMember(project, MessageUtil.DATATYPE_CODE03_30, project.getProgramManagerCodeB(), project.getProgramManagerCodeforjsonB(), MessageUtil.FLAG_FROM_MEMBER);
                projectState = MessageUtil.PROJECT_STATE_32;
            }
            
            // 更新项目状态
            ProjectHeader temp = new ProjectHeader();
            temp.setProjectId(pid);
            temp.setProjectState(projectState);
            this.updateByPrimaryKeySelective(temp);
            
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
	        String newProjectState = MessageUtil.PROJECT_STATE_32.compareTo(oldProjectState) < 0 || MessageUtil.PROJECT_STATE_CLOSEDLOOP.equals(oldProjectState) ? oldProjectState : MessageUtil.PROJECT_STATE_32;
	        
	        // 修改区域负责人
			boolean b = this.updateProjectMember(project, project.getServiceManagerCode(),
					project.getServiceManagerCodeforjson());// 更新项目成员表
			
			// 修改项目经理
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
	                this.updateProjectStateByProjectId(project, newProjectState);
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
	                this.updateProjectStateByProjectId(project, newProjectState);
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
    
    /**
	 * 返回true 做更新操作
	 * 
	 * @param project
	 * @param memberCode
	 * @param memberName
	 * @return
	 */
	@Override
	public boolean updateProjectMember(Project project, String memberCode, String memberName) {
		if (StringUtils.isBlank(memberCode)) {
			return false;
		}
		// 查询当前生效记录
		project.setMemberRole(project.getDataTypeCode());
		project.setMemberCode(memberCode);
		project.setMemberName(memberName);
		Integer count = projectDao.queryProjectMemberCountByProject(project);
//		if (!memberCode.equals(project.getOldMemberCode())) {
//			// 这个才是正真的插入
//			projectDao.updateProjectMember(project);// 更新生效的记录即可
//		}
		// 如果能查到，说明未更改人员，不做操作，否则插入member表
		if (count == 0) {
//			UserInfoVO user = userInfoService.selectOneByUserNameAndCompId(memberCode);
			EmployeeVO user = employeeService.selectByWorkNo(StringUtils.substring(memberCode, -5));
			if (user != null) {
				project.setPhoneNum(user.getMobile());
				project.setEmail(user.getEmail());
			}
			project.setCreateBy(UserContext.getUsername());
			project.setUpdateBy(UserContext.getUsername());
			// 这个才是正真的插入
			projectDao.updateProjectMember(project);// 更新生效的记录即可

			this.insertProjectMember(project);
		}
		return count == 0;
	}
	
	/**
	 * 返回true 做更新操作
	 * 
	 * @param project
	 * @param membercode
	 * @param memberName
	 * @return
	 */
	public boolean updateProjectMember(Project project, String memberRole, String membercode, String memberName, String fromFlag) {
//		Project project = new Project(projectId);
		// 查询当前生效记录
		project.setDataTypeCode(memberRole);
//		project.setMemberRole(memberRole);
//		project.setMemberCode(membercode);
//		project.setMemberName(memberName);
		project.setFromFlag(fromFlag);
		
		return this.updateProjectMember(project, membercode, memberName);
	}
    
    @Override
	public ProjectVO queryProjectStateByProjectId(Object projectId) {
		return dao.queryProjectStateByProjectId(projectId);
	}
    
    @Override
    @Transactional
	public Result insertMergeContract(ProjectVO project, int projectId) {
		String[] contractNos = StringUtils.split(project.getContractNos(), ",");
		String[] projectTypes = StringUtils.split(project.getProjectTypes(), ",");
		Set<String> newProjectCodes = new LinkedHashSet<String>();
		Set<String> newOrderExecNumbers = new LinkedHashSet<String>();
		for (int i = 0; i < contractNos.length; i++) {
			String contractNo = contractNos[i];
			String projectType = projectTypes[i];
			// 如果当前合同号已经创建项目，则直接返回不再创建
			Integer count = this.queryProjectContractCountByContractNoAndType(
					Util.appendChar((String) project.getContractNo(), "'"), project.getProjectType());
			if (count != null && count != 0) {
				return new Result(Boolean.FALSE, String.format("合同号为【%s】的项目已存在", contractNo));
			} else {
				ProjectVO temp = (ProjectVO) this.queryProjectByContractNoAndType(contractNo, projectType);
				if (!checkProjectTypeAndAreaPower(temp)) {
					return new Result(Boolean.FALSE, String.format("没有权限访问合同号为【%s】的项目", contractNo));
				}
				newProjectCodes.add(temp.getSmsProjectCode());
				newOrderExecNumbers.add(temp.getSmsOrderExecNumber());
			}
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("createBy", UserContext.getUsername());
		paramMap.put("projectId", projectId);
		int taskSize = projectDao.queryProjectTaskSize(projectId);
		for (int i = 0; i < contractNos.length; i++) {
			String contractNo = contractNos[i];
			String projectType = projectTypes[i];
			paramMap.put("contractNo", StringUtils.trim(contractNo));
			paramMap.put("projectType", StringUtils.trim(projectType));
			dao.insertMergeContract(paramMap);
			projectDao.insertMergeProduct(paramMap);
			
			if (taskSize > 0) {// 已创建工程计划,做复制计划
				projectDao.insertMergeTask(paramMap);
			}
		}
		project.setSmsProjectCode(StringUtils.join(newProjectCodes, ","));
		project.setSmsOrderExecNumber(StringUtils.join(newOrderExecNumbers, ","));
		
		ProjectVO vo = this.selectVOByProjectId(projectId);
		List<String> contractNo = Arrays.asList(StringUtils.split(vo.getContractNo(), ","));
		List<String> projectCodes = Arrays.asList(StringUtils.split(StringUtils.trimToEmpty(vo.getSmsProjectCode()), ","));
		List<String> orderExecNumbers = Arrays.asList(StringUtils.split(StringUtils.trimToEmpty(vo.getSmsOrderExecNumber()), ","));
		
		List<String> newContractNos = Arrays.asList(contractNos);
		
		Set<String> contractNoSet = new LinkedHashSet<String>();
		contractNoSet.addAll(contractNo);
		contractNoSet.addAll(newContractNos);
		
		Set<String> projectCodeSet = new LinkedHashSet<String>();
		projectCodeSet.addAll(projectCodes);
		projectCodeSet.addAll(newProjectCodes);
		
		Set<String> orderExecNumberSet = new LinkedHashSet<String>();
		orderExecNumberSet.addAll(orderExecNumbers);
		orderExecNumberSet.addAll(newOrderExecNumbers);
		
		ProjectVO temp = new ProjectVO(projectId);
		temp.setContractNo(StringUtils.join(contractNoSet, ","));
		temp.setSmsProjectCode(StringUtils.join(projectCodeSet, ","));
		temp.setSmsOrderExecNumber(StringUtils.join(orderExecNumberSet, ","));
		
		// 保存每次合并的合同号，项目编码，执行单号
		List<Map<?, ?>> mergeLog = (List<Map<?, ?>>) vo.getCustomInfoByKey("mergeLog");
		if (mergeLog == null) {
			mergeLog = new ArrayList<Map<?, ?>>();
		}
		mergeLog.add(MapUtils.putAll(new HashMap<Object, Object>(), new Object[] { 
				"contractNo", newContractNos,
				"projectType", projectTypes,
				"smsProjectCode", newProjectCodes, 
				"smsOrderExecNumber", newOrderExecNumbers }));
		temp.setCustomInfoByKey("mergeLog", mergeLog);
		this.updateByPrimaryKeySelective(temp);
		return new Result(Boolean.TRUE);
	}
    
    @Override
	public void insertMergeContract(String selected, int projectId) {
		String[] contracts = selected.split(",");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("createBy", UserContext.getUsername());
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
    public Map<String, Object> checkPermission(ProjectVO project) {
        return this.checkPermissionMap(project);
    }

    @Override
    public Map<String, Object> checkPermissionMap(ProjectVO project, String... permissions) {
    	Map<String, Object> permissionMap;
        if (permissions != null) {
            Set<String> permissTypes = new HashSet<String>(permissions.length);
            for (String permission : permissions) {
                if (StringUtils.isNotBlank(permission)) {
                    String type = permission.split(":")[0];
                    permissTypes.add(type);
                }
            }
            permissionMap = dao.checkPermission(project, StringUtils.join(permissTypes, ":|") + ":", UserContext.getCurrentPrincipal());
        } else {
        	permissionMap = dao.checkPermission(project, UserContext.getCurrentPrincipal());
        }
        // 已闭环的项目，不允许修改，只有项目管理员、区域负责人才可以重新打开
        String closedState = SystemConfig.systemVariables.getOrDefault("pm.project.closed.state", "100");
        if (closedState.equals(permissionMap.get("maxState"))) {
        	permissionMap.put("all", Boolean.FALSE);
        	permissionMap.put("edit", Boolean.FALSE);
        }
		return permissionMap;
    }

    @Override
    public PermissionResult checkPermission(ProjectVO project, String... permissions) {
        if (!UserContext.checkPermission(permissions)) {
            return new PermissionResult(Boolean.FALSE, "没有权限进行该操作！");
        }
        Boolean isPermit = false;
        String permissionType = "";
        Collection<String> permissionSet = null;
        Collection<String> roleSet = null;
        String permissionProjectTypes = null;
        Principal user = UserContext.getCurrentPrincipal();
        Map<String, Object> permissionMap = null;
        String[] allPermitRoles = null;
        if (!UserContext.checkPermission("project:*") && project != null) {
        	String projectType = StringUtils.trimToEmpty(project.getProjectType());
        	String officeCode = StringUtils.trimToEmpty(project.getColumn001());
        	boolean hasPtAndOfficePower = true;
        	// 允许访问的项目类型
			if (!UserContext.hasAnyRoles(ROLE_PM_ADMIN, ROLE_ADMIN)) {
				String projectTypes = StringUtils.defaultString(user.getUserInfo().getCustom4(), "-1");
				project.setProjectTypes(projectTypes);

				if (!projectTypes.contains(projectType)) {
					hasPtAndOfficePower = false;
				}

				// 非子项目管理员，添加允许访问的办事处权限
				String officeCodes = StringUtils.defaultString(user.getUserInfo().getCustom5(), "-1");
				if (!UserContext.hasRole(ROLE_PM_SUB_ADMIN)) {
					project.setOfficeCodes(officeCodes);
					
					if (!officeCodes.contains(officeCode)) {
						hasPtAndOfficePower = false;
					}
				}
				// 添加指派的项目成员
				project.setMemberCode(user.getUserName());
			}
        				
            Map<String, Object> permission = this.checkPermissionMap(project, permissions);
//            List<String> allPermitRoleList = new ArrayList<String>(Arrays.asList(new String[] { ROLE_PM_ADMIN, ROLE_ADMIN, 
//					hasPtAndOfficePower ? ROLE_PM_SUB_ADMIN : null,
//					hasPtAndOfficePower ? ROLE_PM_AREA_MANAGER : null }));
//            allPermitRoleList.retainAll((Collection<?>) permission.get("roles"));
            String[] allPermitRoleArr = PermissionUtils.getRetainAllRoles(new String[] { ROLE_PM_ADMIN, ROLE_ADMIN, ROLE_PM_SUB_ADMIN, ROLE_PM_AREA_MANAGER }, (Collection<String>) permission.get("roles"));
			PermissionResult checkPermit = new PermissionUtils("project:", allPermitRoleArr)
					.checkPermit(permission, permissions);
			permissionProjectTypes = (String) permission.get("projectTypes");
			isPermit = checkPermit.isPermit();
			permissionType = checkPermit.getPermissionType();
			permissionSet = checkPermit.getPermissions();
			roleSet = checkPermit.getRoles();
			permissionMap = checkPermit.getPermissionMap();
			allPermitRoles = checkPermit.getAllPermitRoles();
			
//			// 子项目管理员，区域负责人，作为项目成员时需要额外判断项目类型和区域
//			if (!"all".equalsIgnoreCase(permissionType) && !UserContext.hasAnyRoles(ROLE_PM_ADMIN, ROLE_ADMIN)) {
//				if (UserContext.hasRole(ROLE_PM_SUB_ADMIN) && hasPtAndOfficePower && isPermit) {
//					permissionType = "all";
//				} else if (UserContext.hasRole(ROLE_PM_AREA_MANAGER) && hasPtAndOfficePower && isPermit) {
//					permissionType = "all";
//				}
//			}
        } else {
            isPermit = true;
            permissionType = "all";
        }
        
        // 安服、研发质量管理员角色判断任务
        if (project != null && project.getProjectId() != null && !isPermit && UserContext.hasAnyRoles(RoleConstant.ROLE_PM_AFQC, RoleConstant.ROLE_PM_YFQC)) {
    		List<String> processKeyList = Arrays.asList(ProcessType.QUALITY_APPROVE_TRACK);
    		List<String> taskKeyList = Arrays.asList(TaskType.AF_APPROVE_TASK, TaskType.YF_APPROVE_TASK, TaskType.TRACK_TASK);
    		
    		PmWorkFlowVO workFlow = new PmWorkFlowVO();
    		workFlow.setTaskKey(StringUtils.join(taskKeyList, ","));
    		workFlow.setProcessKey(StringUtils.join(processKeyList, ","));
    		workFlow.setAssignee(String.valueOf(user.getUserCustom4()));
    		workFlow.setBeginTime(new Date());
    		String areaPower = StringUtils.trimToEmpty(user.getUserInfo().getCustom5());
    		List<String> areaList = new ArrayList<String>(Arrays.asList(StringUtils.split(areaPower, ",")));
    		areaList.add("all");
    		workFlow.setAreaPower(StringUtils.join(areaList, ","));
        	PageParam<PmWorkFlow> pageParam = new PageParam<PmWorkFlow>();
        	pageParam.setModel(workFlow);
        	workFlow.setObjType("project");
        	workFlow.setObjId(project.getProjectId());
			long runCount = pmWorkBenchDao.countRunTasksByAssigneeAndProcessKeyAndTaskKey(pageParam);
			isPermit = runCount > 0;
			if (!isPermit) {
				long finishCount = pmWorkBenchDao.countFinishedTasksByAssignee(pageParam);
				isPermit = finishCount > 0;
			}
			if (isPermit) {
				permissionType = "view";
			}
        }
        
        PermissionResult result = new PermissionResult(isPermit, permissionType, permissionSet, roleSet, permissionMap, allPermitRoles);
        result.setData(permissionProjectTypes);
        return result;
    }

    @Override
    protected Project putProperties(Project project, Project p) {
        super.putProperties(project, p);
        try {
            if (project instanceof ProjectVO && p != null) {
                ((ProjectVO) project).setCustomInfo(((ProjectVO) p).getCustomInfo());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return project;
    }
    
    public boolean checkProjectTypeAndAreaPower(Project project) {
		if (project == null) {
			return true;
		}
		String projectType = StringUtils.trimToEmpty(project.getProjectType());
		String officeCode = StringUtils.trimToEmpty(project.getColumn001());
		Principal user = UserContext.getCurrentPrincipal();
		if (!UserContext.hasAnyRoles(RoleConstant.ROLE_PM_ADMIN, RoleConstant.ROLE_ADMIN)) {
			// 校验允许访问的项目类型
			String projectTypes = StringUtils.defaultString(user.getUserInfo().getCustom4(), "-1");
			if (!projectTypes.contains(projectType)) {
				return false;
			}

			// 非子项目管理员，添加允许访问的办事处权限
			String officeCodes = StringUtils.defaultString(user.getUserInfo().getCustom5(), "-1");
			if (!UserContext.hasRole(RoleConstant.ROLE_PM_SUB_ADMIN) && !officeCodes.contains(officeCode)) {
				return false;
			}
		}
		return true;
	}
}
