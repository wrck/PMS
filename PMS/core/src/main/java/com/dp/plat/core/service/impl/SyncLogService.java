package com.dp.plat.core.service.impl;

import org.springframework.stereotype.Service;

import com.dp.plat.core.dao.SyncLogMapper;
import com.dp.plat.core.pojo.SyncLog;
import com.dp.plat.core.service.ISyncLogService;

@Service("syncLogService")
public class SyncLogService extends AbstractBaseService<SyncLogMapper, SyncLog> implements ISyncLogService {

}
