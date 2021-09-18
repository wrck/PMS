package com.dp.plat.core.factory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.Filter;

import org.apache.shiro.cas.CasFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.filter.AnyRolesAuthorizationFilter;
import com.dp.plat.core.pojo.Resource;
import com.dp.plat.core.service.IResourceService;
import com.dp.plat.core.service.ISystemVariableService;

public class FilterChainDefinitionMapBuilder {
	@Autowired
	private IResourceService resourceService;
	
	@Autowired
	private ISystemVariableService systemVariableService;
	
	@Autowired(required= false)
	private CasFilter casFilter;
	
	@Autowired(required= false)
	private AnyRolesAuthorizationFilter anyRoles;

	public LinkedHashMap<String, String> buildFilterChainDefinitionMap() {
		Resource recod = new Resource();
		recod.setStatus(1);
		List<Resource> resources = resourceService.selectBySelective(recod);
		
		HashMap<String, String> systemVariables = SystemConfig.systemVariables;
		if (systemVariables == null || systemVariables.isEmpty()) {
			systemVariables = systemVariableService.querySystemVariables();
			SystemConfig.systemVariables = systemVariables;
		}
		String isCas = systemVariables.getOrDefault("sys.cas", "0");
		// 如果没有casFilter 则调整为非CAS登录
		if (casFilter == null) {
			isCas = "0";
		}
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		for (Resource resource : resources) {
			String url = resource.getUrl();
			if (StringUtils.isEmpty(url)) {
				continue;
			}
			String authc = StringUtils.isEmpty(resource.getAuthc()) ? "anon" : resource.getAuthc();
			if (authc.contains("authc") && "1".equals(isCas)) {
				authc = "casLogoutFilter," + authc;
			}
			map.put(url, authc);
		}
		
		if ("1".equals(isCas) && casFilter != null) {
			String loginAuth = map.get("/*/login.*");
			map.put("/*/login.*", loginAuth + ",casLogoutFilter,casFilter");
			loginAuth = map.get("/login.*");
			map.put("/login.*", loginAuth + ",casLogoutFilter,casFilter");
		}
		return map;
	}
	
	@Deprecated
	public LinkedHashMap<String, Filter> buildFiltersMap() {
		HashMap<String, String> systemVariables = SystemConfig.systemVariables;
		if (systemVariables == null) {
			systemVariables = systemVariableService.querySystemVariables();
			SystemConfig.systemVariables = systemVariables;
		}
		String isCas = systemVariables.getOrDefault("sys.cas", "0");
		LinkedHashMap<String, Filter> map = new LinkedHashMap<>();
		if ("1".equals(isCas)) {
//			map.put("casFilter", new CasFilter());
			map.put("casFilter", casFilter);
		} 
		map.put("anyRoles", anyRoles);
//		map.put("anyRoles", new AnyRolesAuthorizationFilter());
		return map;
	}
}
