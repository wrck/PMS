/**
 * 
 */
package com.dp.plat.core.service;

import com.dp.plat.core.pojo.NotifyTemplate;

/**
 * @author w02611
 *
 */
public interface INotifyTemplateService extends IAbstractBaseService<NotifyTemplate>{

	/**
	 * 根据模板编码查询模板
	 * @param templateCode
	 * @return
	 */
	NotifyTemplate selectByTemplateCode(String templateCode);

	/**
	 * 根据模板编码删除模板
	 * @param templateCode
	 */
	void deleteByTemplateCode(String templateCode);

}
