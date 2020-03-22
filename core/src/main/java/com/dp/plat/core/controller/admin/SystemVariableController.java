/**
 * 
 */
package com.dp.plat.core.controller.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.core.param.Consts;
import com.dp.plat.core.pojo.SystemVariable;
import com.dp.plat.core.service.ISystemVariableService;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;

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
		systemVariableService.updateByPrimaryKeySelective(variable);
		return Consts.URLPath.SYSTEM_MANAGER + "sysVariable_detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id) {
		systemVariableService.deleteById(id);
	}

}
