/**
 * 
 */
package com.dp.plat.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

import com.dp.plat.core.dao.ResourceMapper;
import com.dp.plat.core.pojo.Resource;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.service.IResourceService;

/**
 * @author w02611
 *
 */
@Service("resourceService")
public class ResourceServicee extends AbstractBaseService<ResourceMapper, Resource>  implements IResourceService {

	@Override
	public void updatePriorities(List<Resource> resources) {
		Map<String, Object> params = new HashMap<>();
		params.put("resources", resources);
		String username = ((Principal)SecurityUtils.getSubject().getPrincipal()).getUserName();
		params.put("updateBy", username);
		dao.updatePriorities(params);
	}
	
}

