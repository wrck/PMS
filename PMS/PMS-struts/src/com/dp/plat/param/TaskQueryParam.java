package com.dp.plat.param;
/**
 * 代办理任务筛选条件
 * @author admin
 *
 */
public class TaskQueryParam {
	private String projectName;
	private String officeCode;
	private String programManager;
	private String serviceManager;
	private String projectCustomer;
	
	
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public String getOfficeCode() {
		return officeCode;
	}
	public void setOfficeCode(String officeCode) {
		this.officeCode = officeCode;
	}
	public String getProgramManager() {
		return programManager;
	}
	public void setProgramManager(String programManager) {
		this.programManager = programManager;
	}
	public String getServiceManager() {
		return serviceManager;
	}
	public void setServiceManager(String serviceManager) {
		this.serviceManager = serviceManager;
	}
    public String getProjectCustomer() {
        return projectCustomer;
    }
    public void setProjectCustomer(String projectCustomer) {
        this.projectCustomer = projectCustomer;
    }
}
