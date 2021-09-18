package com.dp.plat.core.service;

import java.util.Map;

import com.dp.plat.core.pojo.SyncLog;
import com.dp.plat.core.pojo.SyncState;

public interface ISynchronizeService {

	/**
	 * 查询上一次增量同步时的状态值
	 * 
	 * @param tableObject
	 * @return
	 */
	Map<String, Object> selectSyncState(String tableObject);

	/**
	 * 插入/更新增量同步状态值
	 * 
	 * @param syncState
	 * @return
	 */
	int insertSyncState(SyncState syncState);

	/**
	 * 插入同步日志
	 * 
	 * @param syncLog
	 */
	void insertSyncLog(SyncLog syncLog);

	/**
	 * 全量更新时清空同步状态表，避免增量同步失败，导致数据丢失
	 */
	void clearSyncState();

}
