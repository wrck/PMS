/**
 * 
 */
package com.dp.plat.data.vo;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.ProjectMaintenance;
import com.dp.plat.param.FileParam;

/**
 * @author w02611
 */
public class ProjectMaintenanceVO extends ProjectMaintenance {

    private boolean hasPower;
    private String typeName;
    private String officeName;
    private String companyName;
    private String companyAbbr;
    private String createUser;
    private String areaPower;
    private String userPower;
    private boolean checkServicePower;
    private String userOfficeName;
    
    private String serviceManager;
    private String programManager;
    private String programManagerA;
    private String programManagerB;

    private Date processStartTime;
    private Date processEndTime;
    private Date createStartTime;
    private Date createEndTime;
    
    private String deliverFiles;
    private List<FileParam> deliverFileList;
    private List<Map<String, String>> quesnaireResultList;
    private Map<String, Object> questionColumns;
    
    private Boolean hideWarranty;
    private Boolean hideQuesnaire;
    private Boolean hideFiles;
    
    private Integer maxId;
    
    private Map<String, Object> warrantyState;
    private String queryWarrantyStatus;
    private String queryWarrantyGrade;
    private String queryWafService;
    
    // 服务交付查询、导出参数
    private Collection<String> serviceTypes;// 服务类型
    private String serviceType;// 服务类型
    private Date serviceDate;// 服务时间
    private Boolean serviceQuarter;// 服务季度
    private Boolean hasQuarterDeliveried; // 服务季度完成交付

    public boolean isHasPower() {
        return hasPower;
    }

    public void setHasPower(boolean hasPower) {
        this.hasPower = hasPower;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyAbbr() {
		return companyAbbr;
	}

	public void setCompanyAbbr(String companyAbbr) {
		this.companyAbbr = companyAbbr;
	}

	public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getAreaPower() {
        return areaPower;
    }

    public void setAreaPower(String areaPower) {
        this.areaPower = areaPower;
    }

    public String getUserPower() {
        return userPower;
    }

    public void setUserPower(String userPower) {
        this.userPower = userPower;
    }

	public boolean isCheckServicePower() {
		return checkServicePower;
	}

	public void setCheckServicePower(boolean checkServicePower) {
		this.checkServicePower = checkServicePower;
	}

	public String getUserOfficeName() {
        return userOfficeName;
    }

    public void setUserOfficeName(String userOfficeName) {
        this.userOfficeName = userOfficeName;
    }

    public String getServiceManager() {
        return serviceManager;
    }

    public void setServiceManager(String serviceManager) {
        this.serviceManager = serviceManager;
    }

    public String getProgramManager() {
        return programManager;
    }

    public void setProgramManager(String programManager) {
        this.programManager = programManager;
    }

    public String getProgramManagerA() {
        return programManagerA;
    }

    public void setProgramManagerA(String programManagerA) {
        this.programManagerA = programManagerA;
    }

    public String getProgramManagerB() {
        return programManagerB;
    }

    public void setProgramManagerB(String programManagerB) {
        this.programManagerB = programManagerB;
    }

    public Date getProcessStartTime() {
        return processStartTime;
    }

    public void setProcessStartTime(Date processStartTime) {
        this.processStartTime = processStartTime;
    }

    public Date getProcessEndTime() {
        return processEndTime;
    }

    public void setProcessEndTime(Date processEndTime) {
        this.processEndTime = processEndTime;
    }

    public Date getCreateStartTime() {
        return createStartTime;
    }

    public void setCreateStartTime(Date createStartTime) {
        this.createStartTime = createStartTime;
    }

    public Date getCreateEndTime() {
        return createEndTime;
    }

    public void setCreateEndTime(Date createEndTime) {
        this.createEndTime = createEndTime;
    }

    public String getDeliverFiles() {
		return deliverFiles;
	}

	public void setDeliverFiles(String deliverFiles) {
		this.deliverFiles = deliverFiles;
	}

	public List<FileParam> getDeliverFileList() {
        return deliverFileList;
    }

    public void setDeliverFileList(List<FileParam> deliverFileList) {
        this.deliverFileList = deliverFileList;
    }

    public List<Map<String, String>> getQuesnaireResultList() {
        return quesnaireResultList;
    }

    public void setQuesnaireResultList(List<Map<String, String>> quesnaireResultList) {
        this.quesnaireResultList = quesnaireResultList;
    }

    public Map<String, Object> getQuestionColumns() {
        return questionColumns;
    }

    public void setQuestionColumns(Map<String, Object> questionColumns) {
        this.questionColumns = questionColumns;
    }
    
    public Boolean getHideWarranty() {
		return hideWarranty;
	}

	public void setHideWarranty(Boolean hideWarranty) {
		this.hideWarranty = hideWarranty;
	}

	public Boolean getHideQuesnaire() {
        return hideQuesnaire;
    }

    public void setHideQuesnaire(Boolean hideQuesnaire) {
        this.hideQuesnaire = hideQuesnaire;
    }
    
    public Boolean getHideFiles() {
		return hideFiles;
	}

	public void setHideFiles(Boolean hideFiles) {
		this.hideFiles = hideFiles;
	}

	public Integer getMaxId() {
        return maxId;
    }

    public void setMaxId(Integer maxId) {
        this.maxId = maxId;
    }

	public Map<String, Object> getWarrantyState() {
		return warrantyState;
	}

	public void setWarrantyState(Map<String, Object> warrantyState) {
		this.warrantyState = warrantyState;

		initWarrantyExtParams(warrantyState);
	}

	public String getQueryWarrantyStatus() {
		return queryWarrantyStatus;
	}

	public void setQueryWarrantyStatus(String queryWarrantyStatus) {
		this.queryWarrantyStatus = queryWarrantyStatus;
	}

	public String getQueryWarrantyGrade() {
		return queryWarrantyGrade;
	}

	public void setQueryWarrantyGrade(String queryWarrantyGrade) {
		this.queryWarrantyGrade = queryWarrantyGrade;
	}

	public String getQueryWafService() {
		return queryWafService;
	}

	public void setQueryWafService(String queryWafService) {
		this.queryWafService = queryWafService;
	}
	
	public Collection<String> getServiceTypes() {
		return serviceTypes;
	}

	public void setServiceTypes(Collection<String> serviceTypes) {
		this.serviceTypes = serviceTypes;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public Date getServiceDate() {
		return serviceDate;
	}

	public void setServiceDate(Date serviceDate) {
		this.serviceDate = serviceDate;
	}
	
	public Boolean getServiceQuarter() {
		return serviceQuarter;
	}

	public void setServiceQuarter(Boolean serviceQuarter) {
		this.serviceQuarter = serviceQuarter;
	}
	
	public Boolean getHasQuarterDeliveried() {
		return hasQuarterDeliveried;
	}

	public void setHasQuarterDeliveried(Boolean hasQuarterDeliveried) {
		this.hasQuarterDeliveried = hasQuarterDeliveried;
	}

	public String getServiceDateQuarter() {
		if (this.serviceDate != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(this.serviceDate);
			int month = calendar.get(Calendar.MONTH) + 1;
			int quarter = (int) Math.ceil(month / 3d);
			return String.format("%dQ%d", calendar.get(Calendar.YEAR), quarter);
		} else {
			return null;
		}
	}

	@Override
	public void setProcessTime(Date processTime) {
		super.setProcessTime(processTime);
		
		if (processTime != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(processTime);
			this.setYear(calendar.get(Calendar.YEAR));
			int month = calendar.get(Calendar.MONTH) + 1;
			int quarter = (int) Math.ceil(month / 3d);
			this.setQuarter(quarter);
			this.setMonth(month);
		}
	}

	public void initWarrantyExtParams(Map<String, Object> warrantyState) {
		if (warrantyState == null) {
			warrantyState = new HashMap<String, Object>();
		}
		Object warrantyGradeServiceEnable = Boolean.parseBoolean(String.valueOf(warrantyState.get("warrantyGradeEnable")));
		Object wafServiceEnable = Boolean.parseBoolean(String.valueOf(warrantyState.get("wafServiceEnable")));
		Integer wsYearCount = (Integer) (Boolean.TRUE.equals(warrantyGradeServiceEnable) ? warrantyState.get("wsYearCount") : 0);
		Integer wafYearCount = (Integer) (Boolean.TRUE.equals(wafServiceEnable) ? warrantyState.get("wafYearCount") : 0);
		Long wsCount = (Long) warrantyState.get("wsCount");
		Long wafCount = (Long) warrantyState.get("wafCount");
		this.setWsYearCount(wsYearCount != null ? Integer.valueOf(wsYearCount.intValue()) : this.getWsYearCount());
		this.setWafYearCount(wafYearCount != null ? Integer.valueOf(wafYearCount.intValue()) : this.getWafYearCount());
		this.setWsCount(wsCount != null ? Integer.valueOf(wsCount.intValue()) : this.getWsCount());
		this.setWafCount(wafCount != null ? Integer.valueOf(wafCount.intValue()) : this.getWafCount());
		
		this.setWarrantyInfo((String) warrantyState.get("warrantyGradeDesc"));
		this.setServiceInfo((String) warrantyState.get("warrantyServiceDesc"));
	}
}
