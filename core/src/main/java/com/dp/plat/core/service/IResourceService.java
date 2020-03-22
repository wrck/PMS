/**
 * 
 */
package com.dp.plat.core.service;

import java.util.List;

import com.dp.plat.core.pojo.Resource;

/**
 * @author w02611
 *
 */
public interface IResourceService extends IAbstractBaseService<Resource> {

	/**
	 * 更新资源的优先级排序
	 * @param resources
	 */
	void updatePriorities(List<Resource> resources);

}
