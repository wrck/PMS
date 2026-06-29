package com.dp.plat.core.dao;

import com.dp.plat.core.pojo.NotifyTemplate;

public interface NotifyTemplateMapper extends AbstractBaseMapper<NotifyTemplate> {

	/**
	 * 根据模板编码查询模板
	 * 
	 * @param templateCode
	 * @return
	 */
	NotifyTemplate selectByTemplateCode(String templateCode);

	/**
	 * 根据模板编码删除模板
	 * 
	 * @param templateCode
	 */
	void deleteByTemplateCode(String templateCode);

}