/**
 * 
 */
package com.dp.plat.prob.bean;

import java.util.Date;

/**
 * @author w02611
 *
 */
public class ProbStatistic {

	private Integer projectId;
	private String projectCode;
	private String projectName;
	private String itemName;
	private String softInfo;
	private String serviceManagerCode;
	private String serviceManagerName;
	private String programManagerCode;
	private String programManagerCodeA;
	private String programManagerNameA;
	private String programManagerCodeB;
	private String programManagerNameB;
	private String officeCode;
	private String officeName;
	private String updateCount;
	private Integer probId;
	private String probTheme;
	private Date executeTime;
	
	private SoftVersion version;
	private String contractNo;
	private String itemCode;
	private String barCode;
	private boolean filterItem;
	private String startTime;
	private String endTime;
	private boolean autoAdjust;
	private int tabIndex;
	
	

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getSoftInfo() {
		return softInfo;
	}

	public void setSoftInfo(String softInfo) {
		this.softInfo = softInfo;
	}

	public String getServiceManagerCode() {
		return serviceManagerCode;
	}

	public void setServiceManagerCode(String serviceManagerCode) {
		this.serviceManagerCode = serviceManagerCode;
	}

	public String getServiceManagerName() {
		return serviceManagerName;
	}

	public void setServiceManagerName(String serviceManagerName) {
		this.serviceManagerName = serviceManagerName;
	}

	public String getProgramManagerCode() {
		return programManagerCode;
	}

	public void setProgramManagerCode(String programManagerCode) {
		this.programManagerCode = programManagerCode;
	}

	public String getProgramManagerCodeA() {
		return programManagerCodeA;
	}

	public void setProgramManagerCodeA(String programManagerCodeA) {
		this.programManagerCodeA = programManagerCodeA;
	}

	public String getProgramManagerNameA() {
		return programManagerNameA;
	}

	public void setProgramManagerNameA(String programManagerNameA) {
		this.programManagerNameA = programManagerNameA;
	}

	public String getProgramManagerCodeB() {
		return programManagerCodeB;
	}

	public void setProgramManagerCodeB(String programManagerCodeB) {
		this.programManagerCodeB = programManagerCodeB;
	}

	public String getProgramManagerNameB() {
		return programManagerNameB;
	}

	public void setProgramManagerNameB(String programManagerNameB) {
		this.programManagerNameB = programManagerNameB;
	}

	public String getOfficeCode() {
		return officeCode;
	}

	public void setOfficeCode(String officeCode) {
		this.officeCode = officeCode;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public String getUpdateCount() {
		return updateCount;
	}

	public void setUpdateCount(String updateCount) {
		this.updateCount = updateCount;
	}

	public Integer getProbId() {
		return probId;
	}

	public void setProbId(Integer probId) {
		this.probId = probId;
	}

	public String getProbTheme() {
		return probTheme;
	}

	public void setProbTheme(String probTheme) {
		this.probTheme = probTheme;
	}

	public Date getExecuteTime() {
		return executeTime;
	}

	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}
	
    public SoftVersion getVersion() {
        return version;
    }

    public void setVersion(SoftVersion version) {
        this.version = version;
    }
    
	public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
    
    public boolean getFilterItem() {
        return filterItem;
    }

    public void setFilterItem(boolean filterItem) {
        this.filterItem = filterItem;
    }

    public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	public boolean getAutoAdjust() {
		return autoAdjust;
	}

	public void setAutoAdjust(boolean autoAdjust) {
		this.autoAdjust = autoAdjust;
	}

	public int getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

}
