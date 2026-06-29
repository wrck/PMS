package com.dp.plat.core.util.poi.param;

public class ExcelImportParam {
	/**
	 * excel解密密码
	 */
	private String password;
	/**
	 * 数据所在业务Id
	 */
	private Integer entityId;
	/**
	 * 开始列
	 */
	private Integer startCell;
	/**
	 * 结束列
	 */
	private Integer endCell;
	/**
	 * 是否忽略错误
	 */
	private Boolean ignoreError;
	/**
	 * 需要忽略的列标题
	 */
	private String ignoreTitle;

	public ExcelImportParam(Integer entityId, Integer startCell, Integer endCell, Boolean ignoreError,
			String ignoreTitle) {
		super();
		this.entityId = entityId;
		this.startCell = startCell;
		this.endCell = endCell;
		this.ignoreError = ignoreError;
		this.ignoreTitle = ignoreTitle;
	}
	
	public ExcelImportParam(String password, Integer entityId, Integer startCell, Integer endCell, Boolean ignoreError) {
		super();
		this.password = password;
		this.entityId = entityId;
		this.startCell = startCell;
		this.endCell = endCell;
		this.ignoreError = ignoreError;
	}
	
	public ExcelImportParam(Integer entityId, Integer startCell, Integer endCell, Boolean ignoreError,
			String ignoreTitle, String password) {
		super();
		this.password = password;
		this.entityId = entityId;
		this.startCell = startCell;
		this.endCell = endCell;
		this.ignoreError = ignoreError;
		this.ignoreTitle = ignoreTitle;
	}



	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	public Integer getStartCell() {
		return startCell;
	}

	public void setStartCell(Integer startCell) {
		this.startCell = startCell;
	}

	public Integer getEndCell() {
		return endCell;
	}

	public void setEndCell(Integer endCell) {
		this.endCell = endCell;
	}

	public Boolean getIgnoreError() {
		return ignoreError;
	}

	public void setIgnoreError(Boolean ignoreError) {
		this.ignoreError = ignoreError;
	}

	public String getIgnoreTitle() {
		return ignoreTitle;
	}

	public void setIgnoreTitle(String ignoreTitle) {
		this.ignoreTitle = ignoreTitle;
	}

}
