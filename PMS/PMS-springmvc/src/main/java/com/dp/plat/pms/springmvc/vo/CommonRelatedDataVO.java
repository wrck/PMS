package com.dp.plat.pms.springmvc.vo;

import java.util.HashMap;
import java.util.Map;

import com.dp.plat.pms.springmvc.entity.CommonRelatedData;

public class CommonRelatedDataVO extends CommonRelatedData {

	public CommonRelatedDataVO() {
		super();
	}

	public CommonRelatedDataVO(String objType, Integer objId, String type) {
		super(objType, objId, type);
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
			customInfo =  (Map<String, Object>) this.getCustomInfo();
		}
		customInfo.put(key, value);
	}

	@Override
	public void setCustomInfo(Map customInfo) {
		Map info = this.getCustomInfo();
		if (info != null && customInfo != null) {
			info.putAll(customInfo);
		} else if (customInfo != null) {
			super.setCustomInfo(customInfo);
		}
	}
}
