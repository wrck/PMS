package com.dp.plat.core.service.impl;

import org.springframework.stereotype.Service;

import com.dp.plat.core.dao.NotifyTemplateMapper;
import com.dp.plat.core.pojo.NotifyTemplate;
import com.dp.plat.core.service.INotifyTemplateService;

@Service("notifyTemplateService")
public class NotifyTemplateService extends AbstractBaseService<NotifyTemplateMapper, NotifyTemplate>
		implements INotifyTemplateService {

	@Override
	public NotifyTemplate selectByTemplateCode(String templateCode) {
		return dao.selectByTemplateCode(templateCode);
	}

	@Override
	public void deleteByTemplateCode(String templateCode) {
		dao.deleteByTemplateCode(templateCode);
	}

}
