package com.dp.plat.pms.springmvc.controller;

import static com.dp.plat.core.param.RoleConstant.ROLE_ADMIN;
import static com.dp.plat.pms.springmvc.constant.ProjectConstant.Common.PROJECT_DISPATCHED_KEY;
import static com.dp.plat.pms.springmvc.constant.RoleConstant.ROLE_FINANCIAL_AP;
import static com.dp.plat.pms.springmvc.constant.RoleConstant.ROLE_PM_ADMIN;
import static com.dp.plat.pms.springmvc.constant.RoleConstant.ROLE_PM_DISPATCH_SETTLE_STAFF;
import static com.dp.plat.pms.springmvc.constant.RoleConstant.ROLE_PM_SUB_ADMIN;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.vo.PermissionResult;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.constant.RoleConstant;
import com.dp.plat.pms.springmvc.entity.ProjectMember;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.service.IProjectMemberService;
import com.dp.plat.pms.springmvc.util.PermissionUtils;
import com.dp.plat.pms.springmvc.vo.MemberVO;
import com.dp.plat.pms.springmvc.vo.ProjectVO;
import com.dp.plat.util.MessageUtil;

@Controller
@RequestMapping(ProjectConstant.URLPath.PROJECT_MANAGER + "member")
public class ProjectMemberController extends AbstractController<IProjectMemberService, ProjectMember, MemberVO> {

	@Autowired
	private IProjectHeaderService projectHeaderService;
	
	@PostConstruct
	public void init() {
		this.setViewModel("projectMember");
		this.setUseTemplate(true);
	}
	
	@Override
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public String update(@PathVariable("id") Integer id, MemberVO v, Model model) {
		String view = super.update(id, v, model);
		
		if (MessageUtil.FLAG_FROM_PROJECT.equals(v.getFromFlag()) && (v.getEffectiveTo() == null || v.getEffectiveTo().after(new Date()))) {
			ProjectVO project = new ProjectVO();
			project.setProjectId(v.getProjectId());
			if (MessageUtil.MEMBER_SM.equals(v.getMemberRole())) {
				project.setCustomInfoByKey("serviceManagerCode", v.getMemberCode());
				project.setCustomInfoByKey("serviceManagerCodeforjson", v.getMemberName());
				projectHeaderService.updateByPrimaryKeySelective(project);
			} else if (MessageUtil.MEMBER_PM.equals(v.getMemberRole())) {
				project.setCustomInfoByKey("programManagerCode", v.getMemberCode());
				project.setCustomInfoByKey("programManagerCodeforjson", v.getMemberName());
				projectHeaderService.updateByPrimaryKeySelective(project);
			} else if (MessageUtil.MEMBER_SALESMAN.equals(v.getMemberRole())) {
				project.setCustomInfoByKey("salesManCode", v.getMemberCode());
				project.setCustomInfoByKey("salesManName", v.getMemberName());
				projectHeaderService.updateByPrimaryKeySelective(project);
			}
		}
		return view;
	}



	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id, Model model) {
		Boolean status = true;
		String message = null;
		try {
			ProjectMember member = new ProjectMember();
			member.setId(id);
			member.setEffectiveTo(new Date());
			service.updateByPrimaryKeySelective(member);
		} catch (Exception e) {
			status = false;
			Integer errorId = ExceptionHandler.insertException(e);
			model.addAttribute("errorId", errorId);
			message = e.getMessage();
		}
		model.addAttribute("status", status);
		model.addAttribute("message", message);
	}

	@Override
	public boolean checkPermission(MemberVO v, Model model, String... permissions) {
		if (!super.checkPermission(v, model, permissions)) {
			return false;
		}
		boolean isPermit = false;
		String permissionType = "";
		if (!UserContext.checkPermission("project:*") && v != null) {
			ProjectVO project = new ProjectVO();
			project.setProjectId(v.getProjectId());
			// 允许访问的项目类型
			if (!UserContext.hasAnyRoles(ROLE_PM_ADMIN, ROLE_ADMIN)) {
				Principal user = UserContext.getCurrentPrincipal();
				String projectTypes = StringUtils.defaultString(user.getUserInfo().getCustom4(), "-1");
				project.setProjectTypes(projectTypes);

				// 非子项目管理员，添加允许访问的办事处权限
				String officeCodes = StringUtils.defaultString(user.getUserInfo().getCustom5(), "-1");
                if (!UserContext.hasAnyRoles(ROLE_PM_SUB_ADMIN,
                        ROLE_FINANCIAL_AP/* , ROLE_PM_DISPATCH_SETTLE_STAFF */)) {
					project.setOfficeCodes(officeCodes);
				}
                
                // 项目外派结算人员，只能查看已外派的项目
                if (UserContext.hasAnyRoles(ROLE_PM_DISPATCH_SETTLE_STAFF)) {
                    project.setCustomInfoByKey(PROJECT_DISPATCHED_KEY, Boolean.TRUE.toString());
                }
				// 添加指派的项目成员
				project.setMemberCode(user.getUserName());
			}
						
//			Map<String, Object> permission = projectHeaderService.checkPermissionMap(project, permissions);
//			PermissionResult checkPermit = new PermissionUtils(getDataName() + ":",
//					new String[] { RoleConstant.ROLE_PM_ADMIN, RoleConstant.ROLE_ADMIN, RoleConstant.ROLE_PM_SUB_ADMIN,
//							RoleConstant.ROLE_PM_AREA_MANAGER }).checkPermit(permission, permissions);
			PermissionResult projectPermit = projectHeaderService.checkPermission(project, permissions);
			String[] allPermitRoles = PermissionUtils.getRetainAllRoles(new String[] { RoleConstant.ROLE_PM_ADMIN, RoleConstant.ROLE_ADMIN, RoleConstant.ROLE_PM_SUB_ADMIN,
					RoleConstant.ROLE_PM_AREA_MANAGER }, projectPermit.getRoles());
			PermissionResult checkPermit = new PermissionUtils(getDataName() + ":", allPermitRoles)
					.checkPermit(projectPermit.getPermissionMap(), permissions);
			isPermit = checkPermit.isPermit();
			permissionType = checkPermit.getPermissionType();
			if (!UserContext.hasAnyRoles(RoleConstant.ROLE_PM_AREA_MANAGER, RoleConstant.ROLE_PM_PROGRAM) && "edit".equals(permissionType)) {
				permissionType = "view";
			}
//			model.addAttribute("permissions", checkPermit.getMap().getOrDefault("permissions", model.getAttribute("permissions")));
			model.addAllAttributes(checkPermit.getMap());
		} else {
			isPermit = true;
			permissionType = "all";
		}
		model.addAttribute("permissionType", permissionType);
		return isPermit;
	}

}