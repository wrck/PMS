/**
 * 
 */
package com.dp.plat.core.controller.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.core.param.Consts;
import com.dp.plat.core.pojo.Resource;
import com.dp.plat.core.service.IResourceService;
import com.dp.plat.core.vo.PageParam;

/**
 * @author w02611
 *
 */
@RequestMapping(Consts.URLPath.SYSTEM_MANAGER + "resource")
@Controller
public class ResourceController {
	@Autowired
	private IResourceService resourceService;

	@RequestMapping
	public String listView() {
		return Consts.URLPath.SYSTEM_MANAGER + "resource";
	}

	@RequestMapping("/list")
	public String list(PageParam<Object> pageParam, Model model) {
		pageParam.setTotal(resourceService.countBySelective(null));
		pageParam.setFiltered(resourceService.countBySelectivePageable(pageParam));
		List<Object> resourceList = new ArrayList<>();
		if (pageParam.getPageSize() == -1L) {
			pageParam.setPageSize(pageParam.getTotal());
		}
		resourceList = resourceService.selectBySelectivePageable(pageParam);
		model.addAttribute("data", resourceList);
		model.addAttribute("pageParam", pageParam);
		return Consts.URLPath.SYSTEM_MANAGER + "resource";
	}

	@RequestMapping("{id}")
	public String findOne(@PathVariable("id") Integer id, Model model) {
		Resource resource = resourceService.selectByPrimaryKey(id);
		model.addAttribute("resource", resource);
		return Consts.URLPath.SYSTEM_MANAGER + "modals/resource_detail";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	public String create(Resource resource) {
		resource.setCreateTime(new Date());
		resourceService.insertSelective(resource);
		return Consts.URLPath.SYSTEM_MANAGER + "modals/resource_detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public String update(@PathVariable("id") Integer id, Resource resource) {
		resource.setUpdateTime(new Date());
		resourceService.updateByPrimaryKeySelective(resource);
		return Consts.URLPath.SYSTEM_MANAGER + "modals/resource_detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id) {
		resourceService.deleteByPrimaryKey(id);
	}
	
	@RequestMapping(value = "/reorder", method = RequestMethod.POST) 
	public void reorder(@RequestBody List<Resource> list){
		resourceService.updatePriorities(list);
	}
}
