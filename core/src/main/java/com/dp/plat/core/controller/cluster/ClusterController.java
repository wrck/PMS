package com.dp.plat.core.controller.cluster;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dp.plat.core.aop.SystemCoreFunctionAspect;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.param.RoleConstant;
import com.dp.plat.core.vo.Result;

@RequestMapping("/cluster")
@Controller
public class ClusterController {
	
	@Autowired
	private SystemCoreFunctionAspect systemCoreFunctionAspect;

	@PostMapping("/refreshCore")
	public void refreshSystemCoreFunction(Model model) {
		if (!UserContext.hasRole(RoleConstant.ROLE_ADMIN)) {
			model.addAllAttributes(new Result(false, "没有权限访问该功能！").getMap());
		} else {
			systemCoreFunctionAspect.updateActiveUserMenu(null);
			systemCoreFunctionAspect.updateFilterChainDefinitionMap();
			systemCoreFunctionAspect.updateSystemVariables(null);
			model.addAllAttributes(new Result(true, "刷新成功！").getMap());
		}
	}
}
