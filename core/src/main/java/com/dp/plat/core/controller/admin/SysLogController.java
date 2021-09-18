package com.dp.plat.core.controller.admin;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.core.param.Consts;
import com.dp.plat.core.pojo.SysLog;
import com.dp.plat.core.service.ISysLogService;
import com.dp.plat.core.vo.PageParam;

/**
 * 日志管理Controller
 * 
 * @author sunmengyuan
 *
 */

@Controller()
@RequestMapping(Consts.URLPath.SYSTEM_MANAGER + "syslog")
public class SysLogController {
	@Resource
	private ISysLogService sysLogService;

	@RequestMapping
	public void listView() {
	}

	@RequestMapping("/list")
	public String getContractData(PageParam<SysLog> pageParam, SysLog data, Model model) {
		pageParam.setModel(data);
		pageParam.setTotal(sysLogService.countBySelective(null));
		pageParam.setFiltered(sysLogService.countBySelective(pageParam));
		List<SysLog> dataList = new ArrayList<>();
		if (pageParam.getPageSize() == -1L) {
			pageParam.setPageSize(pageParam.getTotal());
		}
		dataList = sysLogService.selectBySelective(pageParam);
		model.addAttribute("data", dataList);
		return Consts.URLPath.SYSTEM_MANAGER + "syslog";
	}

	@RequestMapping("{id}")
	public String getOne(@PathVariable("id") Integer id, Model model) {
		model.addAttribute("id", id);
		return Consts.URLPath.SYSTEM_MANAGER + "syslog_detail";
	}
	
	@RequestMapping(value = "{id}", method = RequestMethod.POST)
	public String findOne(@PathVariable("id") Integer id, Model model) {
		SysLog sysLog = sysLogService.selectByPrimaryKey(id);
		model.addAttribute("sysLog", sysLog);
		return Consts.URLPath.SYSTEM_MANAGER + "syslog_detail";
	}
}
