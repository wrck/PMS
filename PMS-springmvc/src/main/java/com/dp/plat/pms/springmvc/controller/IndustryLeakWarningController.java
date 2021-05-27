package com.dp.plat.pms.springmvc.controller;

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

import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.constant.RoleConstant;
import com.dp.plat.pms.springmvc.entity.IndustryLeakWarning;
import com.dp.plat.pms.springmvc.service.IIndustryAssetService;
import com.dp.plat.pms.springmvc.service.IIndustryLeakService;
import com.dp.plat.pms.springmvc.service.IIndustryLeakWarningService;
import com.dp.plat.pms.springmvc.vo.IndustryLeakVO;
import com.dp.plat.pms.springmvc.vo.LeakWarningVO;

@Controller
@RequestMapping(ProjectConstant.URLPath.AF_MANAGER + "/industry/warning")
public class IndustryLeakWarningController extends AbstractController<IIndustryLeakWarningService, IndustryLeakWarning, LeakWarningVO> {

	@Autowired
	private IIndustryLeakService industryLeakService;
	
	@Autowired
	private IIndustryAssetService industryAssetService;
	
	@PostConstruct
	public void init() {
		this.setUrlNameSpace(ProjectConstant.URLPath.AF_MANAGER);
		this.setViewModel("industryWarning");
		this.setUseTemplate(true);
		this.setViewNameSpace("industry/warning");
	}
	

	@Override
	public String home(Model model) {
		String view = super.home(model);
		return view;
	}

	@Override
	@RequestMapping("/list")
	public String list(PageParam<Object> pageParam, LeakWarningVO v, Model model) {
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
			LeakWarningVO temp = new LeakWarningVO();
			// temp.setCompID(user.getCompId());
			temp.setDisabled(false);
			tempParam.setModel(temp);
			pageParam.setModel(v);
			
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


	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id, Model model) {
		Boolean status = true;
		String message = null;
		try {
			IndustryLeakWarning vo = new IndustryLeakWarning();
			vo.setId(id);
			vo.setDisabled(true);
			service.updateByPrimaryKeySelective(vo);
		} catch (Exception e) {
			status = false;
			Integer errorId = ExceptionHandler.insertException(e);
			model.addAttribute("errorId", errorId);
			message = e.getMessage();
		}
		model.addAttribute("status", status);
		model.addAttribute("message", message);
	}

	@RequestMapping(value = {"/asset", "/asset/list"})
	public String warningAsset(PageParam<Object> pageParam, LeakWarningVO v, Model model) {
		if (HttpContext.isJSON()) {
			List<Object> list = Collections.emptyList();
			try {
				// Principal user = UserContext.getCurrentPrincipal();
				// v.setCompId(user.getCompId());
				PageParam<Object> tempParam = new PageParam<>();
				LeakWarningVO temp = new LeakWarningVO();
				// temp.setCompID(user.getCompId());
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
				
				pageParam.setTotal(service.countWarningAssetBySelectivePageable(tempParam));
				pageParam.setFiltered(service.countWarningAssetBySelectivePageable(pageParam));
				list = service.selectWarningAssetBySelectivePageable(pageParam);
				
				if (pageParam.getPageSize() == -1L) {
					pageParam.setPageSize(pageParam.getTotal());
				}
			} catch (Exception e) {
				ExceptionHandler.insertException(e);
			}
			model.addAttribute("data", list);
			List<DataTableColumn> columns = this.findColumnList("industryWarningAssetList");
			pageParam.setColumns(columns);
			pageParam.setRowId("assetId");
		} else {
			model.addAttribute("urlNamespace", URL_NAMESPACE);
			model.addAttribute("model", "industryWarningAsset");
			model.addAttribute("keyword", getKeyword());
		}
		return getRealViewNameSpace() + "list";
	}
	
}