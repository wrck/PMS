package com.dp.plat.core.controller.admin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.core.annotation.SystemControllerLog;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.pojo.Dictionary;
import com.dp.plat.core.service.IDictionaryService;
import com.dp.plat.core.vo.PageParam;

/**
 * 数据字典管理
 * @author sunmengyuan
 *
 */

@Controller
@RequestMapping(Consts.URLPath.SYSTEM_MANAGER + "dictionary")
public class DictionaryController {
	@Resource
	private IDictionaryService dictionaryService;
	
	@RequestMapping
	public void listView(Model model) {
		Map<Integer,String> dicMap = new LinkedHashMap<Integer,String>();
		List<Dictionary> dataList = dictionaryService.selectBySelective(null);
		for (Dictionary dic : dataList) {
			dicMap.put(dic.getDicTypeId(), dic.getDicTypeName());
		}
		model.addAttribute("dicMap",dicMap);
	}
	
	@RequestMapping("/list")
	@SystemControllerLog(description = "查看数据字典") 
	public String getContractData(PageParam<Dictionary> pageParam, Dictionary data,Model model) {
		pageParam.setModel(data);
		pageParam.setTotal(dictionaryService.countBySelective(null));
		pageParam.setFiltered(dictionaryService.countBySelective(pageParam));
		List<Dictionary> dataList = new ArrayList<>();
		if (pageParam.getPageSize() == -1L) {
			pageParam.setPageSize(pageParam.getTotal());
		}
		dataList = dictionaryService.selectBySelective(pageParam);
		model.addAttribute("data", dataList);
		return Consts.URLPath.SYSTEM_MANAGER + "dictionary";
	}
	
	@RequestMapping("add")
	public String add(){
		return Consts.URLPath.SYSTEM_MANAGER + "dic_detail";
	}
	
	@RequestMapping("{id}")
	public String findOne(@PathVariable("id") Integer id, Model model) {
		Dictionary dic = dictionaryService.selectByPrimaryKey(id);
		model.addAttribute("dic", dic);
		return Consts.URLPath.SYSTEM_MANAGER + "dic_detail";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	@SystemControllerLog(description = "新增数据字典")
	public String create(Dictionary dic, Model model) {
		dictionaryService.insertSelective(dic);
		return Consts.URLPath.SYSTEM_MANAGER + "dictionary";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	@SystemControllerLog(description = "修改数据字典")
	public String update(@PathVariable("id") Integer id, Dictionary dic) {
		dictionaryService.updateByPrimaryKeySelective(dic);
		return Consts.URLPath.SYSTEM_MANAGER + "dictionary";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	@SystemControllerLog(description = "删除数据字典")
	public void delete(@PathVariable("id") Integer id) {
		dictionaryService.deleteByPrimaryKey(id);
	}
}
