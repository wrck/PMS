package com.dp.plat.pms.springmvc.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.ProcessType.DataType;
import com.dp.plat.pms.springmvc.constant.RoleConstant;
import com.dp.plat.pms.springmvc.entity.IndustryAsset;
import com.dp.plat.pms.springmvc.service.IIndustryAssetProjectRelationService;
import com.dp.plat.pms.springmvc.service.IIndustryAssetService;
import com.dp.plat.pms.springmvc.vo.IndustryAssetVO;
import com.dp.plat.pms.springmvc.vo.PmWorkFlowVO;

@Controller
@RequestMapping(ProjectConstant.URLPath.AF_MANAGER + "/industry/asset")
public class IndustryAssetController extends AbstractController<IIndustryAssetService, IndustryAsset, IndustryAssetVO> {

	@Autowired
	private IIndustryAssetProjectRelationService industryAssetProjectRelationService;
	
	@PostConstruct
	public void init() {
		this.setUrlNameSpace(ProjectConstant.URLPath.AF_MANAGER);
		this.setViewModel("industryAsset");
		this.setUseTemplate(true);
		this.setViewNameSpace("industry/asset/");
	}
	
	@Override
	public String home(Model model) {
		String view = super.home(model);
		return getViewNameSpace() + "list";
	}
	
	@Override
	@RequestMapping("/list")
	public String list(PageParam<Object> pageParam, IndustryAssetVO v, Model model) {
		if (!checkPermission(v, model, getDataName() + ":list")) {
			model.addAttribute("data", Collections.emptyList());
			return Consts.VIEW_UNAUTHORIZED;
		}
		List<Object> list = Collections.emptyList();
		try {
			// Principal user = UserContext.getCurrentPrincipal();
			// v.setCompId(user.getCompId());
			v.setDisabled(false);
			PageParam<Object> tempParam = new PageParam<>();
			IndustryAssetVO temp = new IndustryAssetVO();
			// temp.setCompID(user.getCompId());
			temp.setDisabled(false);
			tempParam.setModel(temp);
			pageParam.setModel(v);
			
			// 允许访问的项目类型
			Principal user = UserContext.getCurrentPrincipal();
			if (!UserContext.hasAnyRoles(RoleConstant.ROLE_PM_ADMIN, RoleConstant.ROLE_ADMIN)) {
				String projectTypes = StringUtils.defaultString(user.getUserInfo().getCustom4(), "-1");
				if (!StringUtils.containsAny(projectTypes, "afss", "afxx") || !UserContext.hasRole(RoleConstant.ROLE_PM_SUB_ADMIN)) {
					temp.setCheckProject(true);
					v.setCheckProject(true);
					
					temp.setProjectTypes(projectTypes);
					v.setProjectTypes(projectTypes);
				} 

				// 非子项目管理员，添加允许访问的办事处权限
				String officeCodes = StringUtils.defaultString(user.getUserInfo().getCustom5(), "-1");
				if (!UserContext.hasRole(RoleConstant.ROLE_PM_SUB_ADMIN)) {
					temp.setOfficeCodes(officeCodes);
					v.setOfficeCodes(officeCodes);
					
				}
				// 添加指派的项目成员
				temp.setMemberCode(user.getUserName());
				v.setMemberCode(user.getUserName());
			}

			pageParam.setTotal(service.countBySelectivePageable(tempParam));
			pageParam.setFiltered(service.countBySelectivePageable(pageParam));
			list = service.selectBySelectivePageable(pageParam);

			if (pageParam.getPageSize() == -1L) {
				pageParam.setPageSize(pageParam.getTotal());
			}
		} catch (Exception e) {
			ExceptionHandler.insertException(e);
		}
		model.addAttribute("data", list);

		List<DataTableColumn> columns = this.findColumnList(getDataNameTable());
		pageParam.setColumns(columns);
		return getRealViewNameSpace() + "list";
	}

	@Override
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public String update(@PathVariable("id") Integer id, IndustryAssetVO v, Model model) {
		if (!checkPermission(v, model, getDataName() + ":edit")) {
			return Consts.VIEW_UNAUTHORIZED;
		}
		
		// 终止正在进行中的任务
		PmWorkFlowVO workflow = new PmWorkFlowVO();
		workflow.setDataId(id);
		workflow.setDataType(DataType.INDUSTRY_ASSET);
		workflow.setStatus(PmWorkFlowVO.PENDING);
		pmWorkFlowService.terminateProcess(workflow, "审批内容发生变更！");
		
		v.setStatus("0");
		v.setTrackStatus(0);
		return super.update(id, v, model);
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id, Model model) {
		if (!checkPermission(null, model, getDataName() + ":delete")) {
			return;
		}
		Boolean status = true;
		String message = null;
		try {
			// 终止正在进行中的任务
			PmWorkFlowVO workflow = new PmWorkFlowVO();
			workflow.setDataId(id);
			workflow.setDataType(DataType.INDUSTRY_ASSET);
			workflow.setStatus(PmWorkFlowVO.PENDING);
			pmWorkFlowService.terminateProcess(workflow, "审批内容发生变更！");
			
			IndustryAssetVO v = new IndustryAssetVO();
			v.setId(id);
			v.setDisabled(true);
			service.updateByPrimaryKeySelective(v);
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
	public boolean checkPermission(IndustryAssetVO v, Model model, String... permissions) {
//		return super.checkPermission(v, model, permissions);
		if (!UserContext.checkPermission(permissions)) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return false;
		}
		boolean isAll = false, isEdit = false, isView = false;
		Collection<String> permissionList = UserContext.getCurrentPrincipal().getPermissions();
		Collection<String> currentPermistions = new ArrayList<String>(permissionList.size());
		for (String requiredPerm : permissions) {
			String type = requiredPerm.split(":")[0] + ":";
			for (String permission : permissionList) {
				if (permission.startsWith(type)) {
					currentPermistions.add(permission);
					if (permission.indexOf(":*") > -1) {
						isAll = true;
					} else if (permission.indexOf(":edit") > -1) {
						isEdit = true;
					} else if (permission.indexOf(":list") > -1 || permission.indexOf(":detail") > -1) {
						isView = true;
					} 
				}
			}
		}
		String permissionType = isAll ? "all" : (isEdit ? "edit" : "view");
		model.addAttribute("permissions", currentPermistions);
		model.addAttribute("permissionType", permissionType);
		return true;
	}

}