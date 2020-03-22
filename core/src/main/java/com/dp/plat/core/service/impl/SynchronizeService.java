package com.dp.plat.core.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dp.plat.core.dao.SynchronizeMapper;
import com.dp.plat.core.pojo.SyncLog;
import com.dp.plat.core.pojo.SyncState;
import com.dp.plat.core.service.ISynchronizeService;

@Service("synchronizeService")
public class SynchronizeService implements ISynchronizeService {
	@Resource
	private SynchronizeMapper synchronizeMapper;

	/**
	 * 查询上一次增量同步时的状态值
	 * 
	 * @param tableObject
	 * @return
	 */
	@Override
	public Map<String, Object> selectSyncState(String tableObject) {
		return synchronizeMapper.selectSyncState(tableObject);
	}

	/**
	 * 插入/更新增量同步状态值
	 * 
	 * @param syncState
	 * @return
	 */
	@Override
	public int insertSyncState(SyncState syncState) {
		return synchronizeMapper.insertSyncState(syncState);
	}

	/**
	 * 插入同步日志
	 * 
	 * @param syncLog
	 */
	@Override
	public void insertSyncLog(SyncLog syncLog) {
		synchronizeMapper.insertSyncLog(syncLog);
	}

	/**
	 * 全量更新时清空同步状态表，避免增量同步失败，导致数据丢失
	 */
	@Override
	public void clearSyncState() {
		synchronizeMapper.clearSyncState();
	}

}
