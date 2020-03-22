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
import com.dp.plat.core.pojo.SyncLog;
import com.dp.plat.core.service.ISyncLogService;
import com.dp.plat.core.vo.PageParam;

/**
 * 同步日志管理Controller
 * 
 * @author w02611
 *
 */

@Controller()
@RequestMapping(Consts.URLPath.SYSTEM_MANAGER + "synclog")
public class SyncLogController {
	@Resource
	private ISyncLogService syncLogService;

	@RequestMapping
	public void listView() {
	}

	@RequestMapping("/list")
	public String findAll(PageParam<Object> pageParam, SyncLog data, Model model) {
		pageParam.setModel(data);
		pageParam.setTotal(syncLogService.countBySelectivePageable(null));
		pageParam.setFiltered(syncLogService.countBySelectivePageable(pageParam));
		List<Object> dataList = new ArrayList<>();
		if (pageParam.getPageSize() == -1L) {
			pageParam.setPageSize(pageParam.getTotal());
		}
		dataList = syncLogService.selectBySelectivePageable(pageParam);
		model.addAttribute("data", dataList);
		return Consts.URLPath.SYSTEM_MANAGER + "synclog";
	}

	@RequestMapping("{id}")
	public String getOne(@PathVariable("id") Integer id, Model model) {
		model.addAttribute("id", id);
		return Consts.URLPath.SYSTEM_MANAGER + "synclog_detail";
	}
	
	@RequestMapping(value = "{id}", method = RequestMethod.POST)
	public String findOne(@PathVariable("id") Integer id, Model model) {
		SyncLog sysLog = syncLogService.selectByPrimaryKey(id);
		model.addAttribute("syncLog", sysLog);
		return Consts.URLPath.SYSTEM_MANAGER + "synclog_detail";
	}
}
