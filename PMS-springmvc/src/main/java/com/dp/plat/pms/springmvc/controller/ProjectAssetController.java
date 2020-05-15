package com.dp.plat.pms.springmvc.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.PermissionResult;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.entity.IndustryAsset;
import com.dp.plat.pms.springmvc.entity.IndustryAssetProjectRelation;
import com.dp.plat.pms.springmvc.service.IIndustryAssetProjectRelationService;
import com.dp.plat.pms.springmvc.service.IIndustryAssetService;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.vo.IndustryAssetVO;
import com.dp.plat.pms.springmvc.vo.IndustryLeakVO;
import com.dp.plat.pms.springmvc.vo.ProjectAssetVO;
import com.dp.plat.pms.springmvc.vo.ProjectVO;

@Controller
@RequestMapping(ProjectConstant.URLPath.PROJECT_MANAGER + "/project/asset")
public class ProjectAssetController extends AbstractController<IIndustryAssetService, IndustryAsset, ProjectAssetVO> {

	@Autowired
	private IProjectHeaderService projectHeaderService;
	
	@Autowired
	private IIndustryAssetProjectRelationService industryAssetProjectRelationService;
	
	@PostConstruct
	public void init() {
		this.setUrlNameSpace(ProjectConstant.URLPath.PROJECT_MANAGER);
		this.setViewModel("projectAsset");
		this.setUseTemplate(true);
	}
	
	@Override
	public String home(Model model) {
		return super.home(model);
	}

	@Override
	@GetMapping("/list")
	public String list(PageParam<Object> pageParam, ProjectAssetVO v, Model model) {
		if (!checkPermission(v, model, "project:detail", "projectAsset:list")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return Consts.VIEW_UNAUTHORIZED;
		}
		v.setDisabled(false);
		v.setEffective(new Date());
		pageParam.setModel(v);

		PageParam<Object> tempParam = new PageParam<>();
		ProjectAssetVO temp = new ProjectAssetVO(v.getProjectId());
		temp.setDisabled(false);
		temp.setEffective(new Date());
		
		
		pageParam.setTotal(industryAssetProjectRelationService.countProjectAssetBySelectivePageable(tempParam));
		List<Object> projectAssetList = industryAssetProjectRelationService.selectProjectAssetBySelectivePageable(pageParam);
		if (pageParam.getPageSize() == -1L) {
			pageParam.setPageSize(pageParam.getTotal());
		}
		model.addAttribute("columns", this.findColumnList(getDataNameTable()));
		model.addAttribute("data", projectAssetList);
		return getRealViewNameSpace() + "list";
	}



	@Override
	@GetMapping(value = {"{id}", "/modals/{id}"})
	public String findOne(@PathVariable("id") Integer id, Model model) {
		if (!checkPermission(null, model, "projectAsset:detail")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return Consts.VIEW_UNAUTHORIZED;
		}
		if (HttpContext.isJSON()) {
			IndustryAssetProjectRelation projectRelation = industryAssetProjectRelationService.selectByPrimaryKey(id);
			if (projectRelation != null) {
				PermissionResult checkPermission = projectHeaderService.checkPermission(new ProjectVO(projectRelation.getProjectId()), "project:detail", "projectAsset:detail");
				if (!checkPermission.isPermit()) {
					return "redirect:" + Consts.VIEW_UNAUTHORIZED;
				}
				IndustryAsset asset = service.selectByPrimaryKey(projectRelation.getAssetId());
				ProjectAssetVO v = new ProjectAssetVO();
				BeanUtils.copyProperties(asset, v);
				v.setId(projectRelation.getId());
				v.setAssetId(asset.getId());
				v.setProjectId(projectRelation.getProjectId());
				model.addAttribute("targetValue", v);

				List<Object> fieldList = this.findFieldList(getDataNameForm(), DATATYPE_FORM);
				model.addAttribute("fieldList", fieldList);

				List<?> navTavList = this.findNavTabList(getDataNameNavTab(), model);
				model.addAttribute("tabList", navTavList);
				
				model.addAllAttributes(checkPermission.getMap());
			}
		} else {
			model.addAttribute("urlNamespace", URL_NAMESPACE);
			model.addAttribute("model", getViewModel());
			model.addAttribute("keyword", getKeyword());

			String servletPath = HttpContext.getCurrentRequest().getServletPath();
			model.addAttribute("isModals", servletPath.contains("/modals/"));
		}
		return getRealViewNameSpace() + "detail";
	}



	@Override
	@GetMapping(value = {"detail", "modals/detail"})
	public String detail(ProjectAssetVO v, Model model) {
		if (!checkPermission(v, model, "projectAsset:add")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return Consts.VIEW_UNAUTHORIZED;
		}
		if (HttpContext.isJSON()) {
			model.addAttribute("targetValue", v);

			List<Object> fieldList = this.findFieldList(getDataNameForm(), DATATYPE_FORM);
			model.addAttribute("fieldList", fieldList);
		} else {
//			model.addAttribute("urlNamespace", "/pm/");
//			model.addAttribute("model", "projectAsset");
//			model.addAttribute("keyword", "id");
			model.addAttribute("urlNamespace", URL_NAMESPACE);
			model.addAttribute("model", getViewModel());
			model.addAttribute("keyword", getKeyword());

			String servletPath = HttpContext.getCurrentRequest().getServletPath();
			model.addAttribute("isModals", servletPath.contains("/modals/"));
		}
		return getRealViewNameSpace() + "detail";
	}



	@Override
	@PostMapping("detail")
	public String create(ProjectAssetVO v, Model model) {
		if (!checkPermission(v, model, "projectAsset:add")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return Consts.VIEW_UNAUTHORIZED;
		}
		Boolean status = true;
		String message = null;
		try {
			industryAssetProjectRelationService.insertProjectAssetSelective(v);
			model.addAttribute("targetName", this.getTargetName(v.getClass()));
		} catch (Exception e) {
			status = false;
			Integer errorId = ExceptionHandler.insertException(e);
			model.addAttribute("errorId", errorId);
			message = e.getMessage();
		}
		model.addAttribute("status", status);
		model.addAttribute("message", message);
		return getRealViewNameSpace() + "detail";
	}



	@Override
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public String update(@PathVariable("id") Integer id, ProjectAssetVO v, Model model) {
		ProjectAssetVO asset = new ProjectAssetVO();
		BeanUtils.copyProperties(v, asset);
		asset.setId(v.getAssetId());
		return super.update(v.getAssetId(), asset, model);
//		if (!checkPermission(v, model, getDataName() + ":update")) {
//			model.addAttribute("status", false);
//			model.addAttribute("message", "没有权限进行该操作！");
//			return Consts.VIEW_UNAUTHORIZED;
//		}
//		Boolean status = true;
//		String message = null;
//		try {
//			service.updateByPrimaryKeySelective(v);
//			model.addAttribute("targetName", this.getTargetName(v.getClass()));
//		} catch (Exception e) {
//			status = false;
//			Integer errorId = ExceptionHandler.insertException(e);
//			model.addAttribute("errorId", errorId);
//			message = e.getMessage();
//		}
//		model.addAttribute("status", status);
//		model.addAttribute("message", message);
//		return getRealViewNameSpace() + "detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id, Model model) {
		Boolean status = true;
		String message = null;
		try {
			String cascade = HttpContext.getCurrentRequest().getParameter("cascade");
			if (Boolean.TRUE.equals(cascade)) {
				IndustryAssetProjectRelation relation = industryAssetProjectRelationService.selectByPrimaryKey(id);
				
				IndustryAssetVO asset = new IndustryAssetVO();
				asset.setId(relation.getAssetId());
				asset.setDisabled(true);
				service.updateByPrimaryKeySelective(asset);
			}
			IndustryAssetProjectRelation t = new IndustryAssetProjectRelation();
			t.setId(id);
			t.setDisabled(true);
			t.setEffectiveTo(new Date());
			industryAssetProjectRelationService.updateByPrimaryKeySelective(t);
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
	public boolean checkPermission(ProjectAssetVO v, Model model, String... permissions) {
		if (!super.checkPermission(v, model, permissions)) {
			return false;
		}
		boolean isPermit = false;
		String permissionType = "";
		if (!UserContext.checkPermission("project:*") && v != null && v.getProjectId() != null) {
			ProjectVO project = new ProjectVO();
			project.setProjectId(v.getProjectId());
			Map<String, Object> permission = projectHeaderService.checkPermissionMap(project, permissions);
			Boolean allPerm = (Boolean) permission.get("all");
			if (Boolean.TRUE.equals(allPerm)) {
				isPermit = true;
				permissionType = "all";
			} else {
				String perms = StringUtils.join(permissions, ",");
				if (Boolean.TRUE.equals(permission.get("edit")) && perms.matches(".*projectAsset:(add|edit|delete|import|list|detail)\\b,?.*")) {
					isPermit = true;
					permissionType = "edit";
				} else if ((Boolean.TRUE.equals(permission.get("edit")) || Boolean.TRUE.equals(permission.get("view"))) && perms.matches(".*projectAsset:(list|detail)\\b,?.*")) {
					isPermit = true;
					permissionType = Boolean.TRUE.equals(permission.get("edit")) ? "edit" : "view";
				}
			}
			model.addAttribute("permissions", permission.getOrDefault("permissions", model.getAttribute("permissions")));
		} else {
			isPermit = true;
			permissionType = "all";
		}
		model.addAttribute("permissionType", permissionType);
		return isPermit;
	}

}