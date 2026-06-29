package com.dp.plat.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.dp.plat.core.dao.SystemVariableMapper;
import com.dp.plat.core.pojo.SystemVariable;
import com.dp.plat.core.service.ISystemVariableService;

@Service("systemVariableService")
public class SystemVariableService extends AbstractBaseService<SystemVariableMapper, SystemVariable>
		implements ISystemVariableService {

	@Override
	public HashMap<String, String> querySystemVariables() {
		List<Map<String, String>> mapList = dao.querySystemVariables();
		HashMap<String, String> systemVariables = new HashMap<>();
		for (Map<String, String> map : mapList) {
			String code = map.get("code");
			String var = map.get("var");
			systemVariables.put(code, var);
		}
		return systemVariables;
	}

	@Override
	public SystemVariable selectById(Integer id) {
		return dao.selectById(id);
	}

	@Override
	public void deleteById(Integer id) {
		dao.deleteById(id);
	}

}
