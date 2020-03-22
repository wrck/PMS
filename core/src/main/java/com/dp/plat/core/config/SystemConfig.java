/**
 * 
 */
package com.dp.plat.core.config;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.dp.plat.core.service.ISystemVariableService;

/**
 * @author w02611
 *
 */
@Order(1)
@Configuration
public class SystemConfig {
	public static HashMap<String, String> systemVariables;
	
	@Autowired
	private ISystemVariableService systemVariableService;
	
	@Bean(name = "systemVariables")
	public HashMap<String, String> getSystemVariables(){
		systemVariables = systemVariableService.querySystemVariables();
		return systemVariables;
	}
}
