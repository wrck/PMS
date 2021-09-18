/**
 * 
 */
package com.dp.plat.core.controller.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.pojo.SystemVariable;
import com.dp.plat.core.service.ISystemVariableService;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.security.util.ASEUtil;
import com.dp.plat.support.CaptchaServlet;

/**
 * @author w02611
 *
 */
@Controller
@RequestMapping(Consts.URLPath.SYSTEM_MANAGER + "sysVariable")
public class SystemVariableController {

	@Resource
	private ISystemVariableService systemVariableService;

	@RequestMapping
	public String listView() {
		return Consts.URLPath.SYSTEM_MANAGER + "sysVariable";
	}

	@RequestMapping("/list")
	public String findAll(PageParam<Object> pageParam, SystemVariable data, Model model) {
		pageParam.setModel(data);
		pageParam.setTotal(systemVariableService.countBySelective(null));
		pageParam.setFiltered(systemVariableService.countBySelectivePageable(pageParam));
		List<Object> dataList = new ArrayList<Object>();
		if (pageParam.getPageSize() == -1L) {
			pageParam.setPageSize(pageParam.getTotal());
		}
		dataList = systemVariableService.selectBySelectivePageable(pageParam);

		HttpSession currentSession = HttpContext.getCurrentSession();
		if (!(currentSession != null && Boolean.TRUE.equals(HttpContext.getCurrentSession().getAttribute("isSC")))) {
			for (Iterator<Object> iterator = dataList.iterator(); iterator.hasNext();) {
				SystemVariable variable = (SystemVariable) iterator.next();
				variable.setVar(ASEUtil.encrypt(variable.getVar(), "SystemVariable"));
			}
		}
		
		model.addAttribute("data", dataList);
		List<DataTableColumn> columns = new ArrayList<>();
		columns.add(new DataTableColumn("参数编码", "code"));
		columns.add(new DataTableColumn("参数值", "var"));
		columns.add(new DataTableColumn("备注", "remark"));
		columns.add(new DataTableColumn("开始有效时间", "effectiveFrom"));
		columns.add(new DataTableColumn("开始失效时间", "effectiveTo"));
		pageParam.setColumns(columns);
		pageParam.setRowId("id");
		return Consts.URLPath.SYSTEM_MANAGER + "sysVariable";
	}

	@RequestMapping("{id}")
	public String findOne(@PathVariable("id") Integer id, Model model) {
		SystemVariable variable = null;
		variable = systemVariableService.selectById(id);
		HttpSession currentSession = HttpContext.getCurrentSession();
		if (variable != null && !(currentSession != null && Boolean.TRUE.equals(HttpContext.getCurrentSession().getAttribute("isSC")))) {
			variable.setVar(ASEUtil.encrypt(variable.getVar(), "SystemVariable"));
		}
		model.addAttribute("variable", variable);
		return Consts.URLPath.SYSTEM_MANAGER + "sysVariable_detail";
	}

	@RequestMapping("/detail")
	public String create() {
		return Consts.URLPath.SYSTEM_MANAGER + "sysVariable_detail";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	public String create(SystemVariable variable, Model model) {
		variable.setCreateTime(new Date());
		systemVariableService.insertSelective(variable);
		model.addAttribute("id", variable.getId());
		return Consts.URLPath.SYSTEM_MANAGER + "sysVariable_detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public String update(@PathVariable("id") Integer id, SystemVariable variable) {
		variable.setUpdateTime(new Date());
		HttpSession currentSession = HttpContext.getCurrentSession();
		if (variable != null && !(currentSession != null && Boolean.TRUE.equals(HttpContext.getCurrentSession().getAttribute("isSC")))) {
			String var = variable.getVar();
			String decrypt = ASEUtil.decrypt(variable.getVar(), "SystemVariable");
			variable.setVar(StringUtils.defaultIfBlank(decrypt, var));
		}
		systemVariableService.updateByPrimaryKeySelective(variable);
		return Consts.URLPath.SYSTEM_MANAGER + "sysVariable_detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id) {
		systemVariableService.deleteById(id);
	}

	@PostMapping("secondaryCertification")
	public void secondaryCertification(@RequestParam(name = "cert", required = true) String cert, HttpServletRequest request) {
		HttpSession session = request.getSession();
		Object captcha = session.getAttribute(CaptchaServlet.KEY_CAPTCHA);
		session.removeAttribute(CaptchaServlet.KEY_CAPTCHA);
		if (cert.equalsIgnoreCase((String) captcha)) {
			session.setAttribute("isSC", true);
		} else {
			session.removeAttribute("isSC");
		}
	}
	
}
