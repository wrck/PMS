package com.dp.plat.pms.springmvc.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.util.FileUtil;
import com.dp.plat.core.util.UploadUtils;
import com.dp.plat.core.vo.PermissionResult;
import com.dp.plat.dao.ProjectDao;
import com.dp.plat.pms.springmvc.constant.RoleConstant;
import com.dp.plat.pms.springmvc.dao.ProjectTaskMapper;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;
import com.dp.plat.pms.springmvc.entity.ProjectTask;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.service.IProjectTaskService;
import com.dp.plat.pms.springmvc.util.PermissionUtils;
import com.dp.plat.pms.springmvc.vo.ProjectDeliver;
import com.dp.plat.pms.springmvc.vo.TaskVO;

/**
 *
 * Created by CodeGenerator
 */
@Service("projectTaskService")
public class ProjectTaskService extends AbstractBaseService<ProjectTaskMapper, ProjectTask> implements IProjectTaskService {

    @Autowired
    protected ProjectDao projectDao;
    
    @Autowired
    @Lazy
    protected IProjectHeaderService projectHeaderService;

    @Override
    public void uploadFile(ProjectDeliver deliver, MultipartFile multipartFile) {
        // 文件名
        String originalFilename = multipartFile.getOriginalFilename();
        // 检查上传文件类型
        if (!FileUtil.checkFileExt(originalFilename, SystemConfig.systemVariables.get("pm.upload.file.type.whitelist"))) {
        	return;
        }
        // 文件重命名
        String fileName = FileUtil.getFileNameByMD5(multipartFile);
        String webDir = UploadUtils.getWebDir(HttpContext.getCurrentRequest());
        // 获取文件保存目录
        String saveDir = UploadUtils.getSaveDir(SystemConfig.systemVariables.getOrDefault("pm.project.deliver.dir", "/upload/delivery/"));
        // 构造完整的文件保存路径
        String fullPath = webDir + saveDir + fileName;
        // 处理操作系统的差异
        fullPath = fullPath.replaceAll("//", File.separator);
        String shortPath = saveDir + fileName;
        // 处理操作系统的差异
        shortPath = shortPath.replaceAll("//", File.separator);
        // 判断上传目录是否存在
        UploadUtils.mkdir(webDir, saveDir.replaceAll("//", File.separator));
        File file = new File(fullPath);
        if (file.isFile() && file.exists()) {
        //
        // 重命名的情况下，文件已经存在的不用再重复上传
        } else {
            try {
                BufferedInputStream inputStream = new BufferedInputStream(multipartFile.getInputStream());
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fullPath));
                Streams.copy(inputStream, outputStream, true);
                inputStream.close();
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ProjectDeliver pdeliver = new ProjectDeliver();
        pdeliver.setProjectId(deliver.getProjectId());
        pdeliver.setContractNo(deliver.getContractNo());
        pdeliver.setDeliverId(deliver.getDeliverId());
        pdeliver.setDeliverableType(deliver.getDeliverableType());
        pdeliver.setDeliverableName(originalFilename);
        pdeliver.setDeliverablePath(shortPath);
        List<ProjectDeliver> pdlist = new ArrayList<ProjectDeliver>(1);
        pdlist.add(pdeliver);
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (pdlist != null && pdlist.size() > 0) {
            paramMap.put("uploadUser", UserContext.getCurrentUser().getUserName());
            paramMap.put("list", pdlist);
            if (deliver != null) {
                paramMap.put("taskId", deliver.getTaskId());
                paramMap.put("projectType", deliver.getProjectType());
            }
            projectDao.batchInsertDeliverFiles(paramMap);
        }
    }
    
    @Override
    @Transactional
    public boolean updateEventActualFinishDateByTask(ProjectDeliver pd) {
		// 非直签 、督导项目 获取的even节点任务column010为null
		if (StringUtils.isEmpty(pd.getColumn010())) {
			pd.setColumn010(null);
		}
		Integer count = projectDao.queryDeliverDetailCountByProjectDeliver(pd);
		ProjectTask pt = new ProjectTask(pd.getProjectId(), pd.getProjectType());
		if (pd.getTaskId() != null) {
			pt.setTaskId(pd.getTaskId());
		} else {
			pt.setTaskTypeCode(pd.getDataTypeCode());
			pt.setTaskTypeId(pd.getBasicDataId());
		}
		if (count == 0) {// 如果当前节点下必上传交付件完整，则置当前时间为完成时间
			pt.setEventActualFinishDate(new Date());
		} else {
			pt.setEventActualFinishDate(null);
		}
		dao.updateEventActualFinishDateByTask(pt);
		
		// 安服项目的验收节点
		String inspectEventKey = SystemConfig.systemVariables.getOrDefault("pm.project." + pd.getProjectType() + ".inspect.eventkey", "");
		if (inspectEventKey.equals(pd.getEventKey())) {
			ProjectHeader project = projectHeaderService.selectByPrimaryKey(pd.getProjectId());
			String inspectState = SystemConfig.systemVariables.getOrDefault("pm.project." + pd.getProjectType() + ".inspect.state", "50");
			String projectState = StringUtils.trimToEmpty(project.getProjectState());
			if (project != null && inspectState.compareTo(projectState) > 0) {
				ProjectHeader temp = new ProjectHeader();
				temp.setProjectId(project.getProjectId());
				temp.setProjectState(inspectState);
				projectHeaderService.updateByPrimaryKeySelective(temp);
			}
		}
		return count == 0;
	}
    

    @Override
    @Transactional
	public void deleteDeliverFile(ProjectDeliver projectDeliver) {
    	String[] deliverIds = StringUtils.trimToEmpty(projectDeliver.getDeliverId()).split(",");
    	for (String deleteId : deliverIds) {
    		Integer deliverId = Integer.valueOf(deleteId);
    		projectDao.deleteDeliverById(deliverId);
    		com.dp.plat.data.bean.ProjectDeliver pd = projectDao.queryProjectDeliverById(deliverId);
    		ProjectDeliver deliver = new ProjectDeliver();
    		BeanUtils.copyProperties(projectDeliver, deliver);
    		BeanUtils.copyProperties(pd, deliver);
    		this.updateEventActualFinishDateByTask(deliver);
		}
	}

	@Override
    public List<ProjectDeliver> selectProjectDeliverBySelective(ProjectDeliver projectDeliver) {
        return dao.selectProjectDeliverBySelective(projectDeliver);
    }
    
    @Override
	public Map<String, Object> checkPermissionMap(TaskVO task, String... permissions) {
		Map<String, Object> permissionMap;
		if (permissions != null) {
			Set<String> permissTypes = new HashSet<String>(permissions.length);
			for (String permission : permissions) {
				if (StringUtils.isNotBlank(permission)) {
					String type = permission.split(":")[0];
					permissTypes.add(type);
				}
			}
			permissionMap = dao.checkPermission(task, StringUtils.join(permissTypes, ":|") + ":", UserContext.getCurrentPrincipal());
		} else {
			permissionMap = dao.checkPermission(task, UserContext.getCurrentPrincipal());
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
	public PermissionResult checkPermission(TaskVO task, String... permissions) {
		if (!UserContext.checkPermission(permissions)) {
			return new PermissionResult(Boolean.FALSE, "没有权限进行该操作！");
		}
		Boolean isPermit = false;
		String permissionType = "";
		Collection<String> permissionSet = null;
		PermissionResult result = null;
		if (!UserContext.checkPermission("project:*") && task != null) {
			// 允许访问的项目类型
			if (!UserContext.hasAnyRoles(RoleConstant.ROLE_PM_ADMIN, RoleConstant.ROLE_ADMIN)) {
				Principal user = UserContext.getCurrentPrincipal();
				String projectTypes = StringUtils.defaultString(user.getUserInfo().getCustom4(), "-1");
				task.setProjectTypes(projectTypes);

				// 非子项目管理员，添加允许访问的办事处权限
				String officeCodes = StringUtils.defaultString(user.getUserInfo().getCustom5(), "-1");
				if (!UserContext.hasRole(RoleConstant.ROLE_PM_SUB_ADMIN)) {
					task.setOfficeCodes(officeCodes);
					
				}
				// 添加指派的项目成员
				task.setMemberCode(user.getUserName());
			}
						
//			Map<String, Object> permission = this.checkPermissionMap(task, permissions);
//			result = new PermissionUtils(new String[] { RoleConstant.ROLE_PM_ADMIN, RoleConstant.ROLE_ADMIN, RoleConstant.ROLE_PM_SUB_ADMIN,
//							RoleConstant.ROLE_PM_AREA_MANAGER }).checkPermit(permission, permissions);
			Map<String, Object> permission = this.checkPermissionMap(task, permissions);
			Collection<String> roles = (Collection<String>) permission.get("roles");
			String[] allPermitRoles = PermissionUtils.getRetainAllRoles(new String[] { RoleConstant.ROLE_PM_ADMIN, RoleConstant.ROLE_ADMIN, RoleConstant.ROLE_PM_SUB_ADMIN,
							RoleConstant.ROLE_PM_AREA_MANAGER }, roles);
			result = new PermissionUtils(allPermitRoles).checkPermit(permission, permissions);
		} else {
			isPermit = true;
			permissionType= "all";
		}
		return result != null ? result : new PermissionResult(isPermit, permissionType, permissionSet);
	}
}
