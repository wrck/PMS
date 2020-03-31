package com.dp.plat.pms.springmvc.vo;

import java.util.HashMap;
import java.util.Map;

import com.dp.plat.pms.springmvc.entity.ProjectHeader;

public class ProjectVO extends ProjectHeader {
	
	public String getContractNo() {
		return (String) getCustomInfoByKey("contractNo");
	}

	public void setContractNo(String contractNo) {
		super.setContractNo(contractNo);
		setCustomInfoByKey("contractNo", contractNo);
	}

	public Object getCustomInfoByKey(String key) {
		Map<?, ?> customInfo = getCustomInfo();
		if (customInfo != null && !customInfo.isEmpty()) {
			return customInfo.get(key);
		}
		return null;
	}

	public void setCustomInfoByKey(String key, Object value) {
		Map<String, Object> customInfo = (Map<String, Object>) getCustomInfo();
		if (customInfo == null) {
			customInfo = new HashMap<>();
			this.setCustomInfo(customInfo);
		}
		customInfo.put(key, value);
	}
}
