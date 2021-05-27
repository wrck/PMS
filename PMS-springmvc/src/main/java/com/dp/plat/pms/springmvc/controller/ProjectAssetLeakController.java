package com.dp.plat.pms.springmvc.controller;

import static com.dp.plat.core.param.RoleConstant.ROLE_ADMIN;
import static com.dp.plat.pms.springmvc.constant.RoleConstant.ROLE_PM_ADMIN;
import static com.dp.plat.pms.springmvc.constant.RoleConstant.ROLE_PM_SUB_ADMIN;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
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
import com.dp.plat.pms.springmvc.constant.ProjectConstant.ProcessType.DataType;
import com.dp.plat.pms.springmvc.constant.RoleConstant;
import com.dp.plat.pms.springmvc.entity.IndustryAsset;
import com.dp.plat.pms.springmvc.entity.IndustryAssetLeakRelation;
import com.dp.plat.pms.springmvc.entity.IndustryLeak;
import com.dp.plat.pms.springmvc.entity.ProjectTask;
import com.dp.plat.pms.springmvc.service.IIndustryAssetLeakRelationService;
import com.dp.plat.pms.springmvc.service.IIndustryAssetService;
import com.dp.plat.pms.springmvc.service.IIndustryLeakService;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.util.PermissionUtils;
import com.dp.plat.pms.springmvc.vo.IndustryLeakVO;
import com.dp.plat.pms.springmvc.vo.PmWorkFlowVO;
import com.dp.plat.pms.springmvc.vo.ProjectAssetLeakVO;
import com.dp.plat.pms.springmvc.vo.ProjectVO;

@Controller
@RequestMapping(ProjectConstant.URLPath.PROJECT_MANAGER + "/asset/leak")
public class ProjectAssetLeakController extends AbstractController<IIndustryLeakService, IndustryLeak, ProjectAssetLeakVO> {

	@Autowired
	private IProjectHeaderService projectHeaderService;
	
	@Autowired
	private IIndustryAssetLeakRelationService industryAssetLeakRelationService;
	
	@Autowired
	private IIndustryAssetService industryAssetService;
	
	@PostConstruct
	public void init() {
		this.setUrlNameSpace(ProjectConstant.URLPath.PROJECT_MANAGER);
		this.setViewModel("assetLeak");
		this.setUseTemplate(true);
	}
	
	@Override
	public String home(Model model) {
		return super.home(model);
	}

	@Override
	@GetMapping("/list")
	public String list(PageParam<Object> pageParam, ProjectAssetLeakVO v, Model model) {
		if (!checkPermission(v, model, "projectAsset:detail", "assetLeak:list")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return Consts.VIEW_UNAUTHORIZED;
		}
		v.setDisabled(false);
		v.setEffective(new Date());
		pageParam.setModel(v);

		PageParam<Object> tempParam = new PageParam<>();
		ProjectAssetLeakVO temp = new ProjectAssetLeakVO(v.getProjectId());
		temp.setDisabled(false);
		temp.setEffective(new Date());
		pageParam.setOrderBy("apr.projectId, ia.id desc, l.id desc");
		
		pageParam.setTotal(industryAssetLeakRelationService.countProjectAssetLeakBySelectivePageable(tempParam));
		List<Object> projectAssetList = industryAssetLeakRelationService.selectProjectAssetLeakBySelectivePageable(pageParam);
		if (pageParam.getPageSize() == -1L) {
			pageParam.setPageSize(pageParam.getTotal());
		}
		pageParam.setColumns(this.findColumnList(getDataNameTable()));
//		model.addAttribute("columns", this.findColumnList(getDataNameTable()));
		model.addAttribute("data", projectAssetList);
		return getRealViewNameSpace() + "list";
	}



	@Override
	@GetMapping(value = {"{id}", "/modals/{id}"})
	public String findOne(@PathVariable("id") Integer id, Model model) {
		if (!checkPermission(null, model, "assetLeak:detail")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return Consts.VIEW_UNAUTHORIZED;
		}
		if (HttpContext.isJSON()) {
			IndustryAssetLeakRelation projectRelation = industryAssetLeakRelationService.selectByPrimaryKey(id);
			if (projectRelation != null) {
				PermissionResult checkPermission = projectHeaderService.checkPermission(new ProjectVO(projectRelation.getProjectId()), "project:detail", "assetLeak:detail");
				if (!checkPermission.isPermit()) {
					return "redirect:" + Consts.VIEW_UNAUTHORIZED;
				}
				model.addAllAttributes(checkPermission.getMap());
				
				IndustryLeak leak = service.selectByPrimaryKey(projectRelation.getLeakId());
				IndustryAsset asset = industryAssetService.selectByPrimaryKey(projectRelation.getAssetId());
				ProjectAssetLeakVO v = new ProjectAssetLeakVO();
				BeanUtils.copyProperties(leak, v);
				v.setId(projectRelation.getId());
				v.setLeakId(leak.getId());
				v.setAssetId(asset.getId());
				v.setAssetName(asset.getAssetName());
				v.setProjectId(projectRelation.getProjectId());
				model.addAttribute("targetValue", v);

				List<Object> fieldList = this.findFieldList(getDataNameForm(), DATATYPE_FORM);
				model.addAttribute("fieldList", fieldList);

				List<?> navTavList = this.findNavTabList(getDataNameNavTab(), model);
				model.addAttribute("tabList", navTavList);
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
	public String detail(ProjectAssetLeakVO v, Model model) {
		if (!checkPermission(v, model, "assetLeak:add")) {
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
//			model.addAttribute("model", "assetLeak");
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
	public String create(ProjectAssetLeakVO v, Model model) {
		if (!checkPermission(v, model, "assetLeak:add")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return Consts.VIEW_UNAUTHORIZED;
		}
		Boolean status = true;
		String message = null;
		try {
			String assetIds = StringUtils.trimToEmpty(v.getAssetIds());
			HashSet<String> set = new HashSet<String>(Arrays.asList(assetIds.split(",")));
			if (v.getAssetId() != null) {
				set.add(v.getAssetId().toString());
			}
			ProjectAssetLeakVO assetLeak = new ProjectAssetLeakVO();
			BeanUtils.copyProperties(v, assetLeak);
			for (String assetId : set) {
				if (StringUtils.isNotBlank(assetId)) {
					assetLeak.setAssetId(Integer.valueOf(assetId));
					industryAssetLeakRelationService.insertProjectAssetLeakSelective(assetLeak);
				}
			}
//			for (String assetId : set) {
//				if (StringUtils.isNotBlank(assetId)) {
//					ProjectAssetLeakVO assetLeak = new ProjectAssetLeakVO();
//					BeanUtils.copyProperties(v, assetLeak);
//					assetLeak.setAssetId(Integer.valueOf(assetId));
//					industryAssetLeakRelationService.insertProjectAssetLeakSelective(assetLeak);
//				}
//			}
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
	public String update(@PathVariable("id") Integer id, ProjectAssetLeakVO v, Model model) {
		ProjectAssetLeakVO leak = new ProjectAssetLeakVO();
		BeanUtils.copyProperties(v, leak);
		leak.setId(v.getLeakId());
		
		// 终止正在进行中的任务
		PmWorkFlowVO workflow = new PmWorkFlowVO();
		workflow.setDataId(v.getLeakId());
		workflow.setDataType(DataType.INDUSTRY_LEAK);
//		workflow.setObjId(v.getProjectId());
//		workflow.setObjType(DataType.PROJECT);
		workflow.setStatus(PmWorkFlowVO.PENDING);
		pmWorkFlowService.terminateProcess(workflow, "审批内容发生变更！");
		
		leak.setStatus("0");
		leak.setTrackStatus(0);
		return super.update(v.getLeakId(), leak, model);
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
				IndustryAssetLeakRelation relation = industryAssetLeakRelationService.selectByPrimaryKey(id);
				
				// 终止正在进行中的任务
				PmWorkFlowVO workflow = new PmWorkFlowVO();
				workflow.setDataId(relation.getLeakId());
				workflow.setDataType(DataType.INDUSTRY_LEAK);
//				workflow.setObjId(relation.getProjectId());
//				workflow.setObjType(DataType.PROJECT);
				workflow.setStatus(PmWorkFlowVO.PENDING);
				pmWorkFlowService.terminateProcess(workflow, "审批内容发生变更！");
				
				IndustryLeakVO leak = new IndustryLeakVO();
				leak.setId(relation.getLeakId());
				leak.setDisabled(true);
				service.updateByPrimaryKeySelective(leak);
			}
			
			
			IndustryAssetLeakRelation t = new IndustryAssetLeakRelation();
			t.setId(id);
			t.setDisabled(true);
			t.setEffectiveTo(new Date());
			industryAssetLeakRelationService.updateByPrimaryKeySelective(t);
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
	public boolean checkPermission(ProjectAssetLeakVO v, Model model, String... permissions) {
		if (!super.checkPermission(v, model, permissions)) {
			return false;
		}
		boolean isPermit = false;
		String permissionType = "";
		if (!UserContext.checkPermission("industryLeak:*") && v != null && v.getProjectId() != null) {
			ProjectVO project = new ProjectVO();
			project.setProjectId(v.getProjectId());
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