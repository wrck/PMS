package com.dp.plat.core.pojo;

import java.util.Date;

public class SyncState {
    private Integer id;

    private String tableObject;

    private String lastId;

    private Date lastSyncTime;

    private Integer offset;
    
    /**
	 * @param tableObject
	 * @param lastId
	 * @param offset
	 */
	public SyncState(String tableObject, String lastId, Integer offset) {
		this.tableObject = tableObject;
		this.lastId = lastId;
		this.offset = offset;
		this.lastSyncTime = new Date();
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTableObject() {
        return tableObject;
    }

    public void setTableObject(String tableObject) {
        this.tableObject = tableObject;
    }

    public String getLastId() {
        return lastId;
    }

    public void setLastId(String lastId) {
        this.lastId = lastId;
    }

    public Date getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(Date lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}