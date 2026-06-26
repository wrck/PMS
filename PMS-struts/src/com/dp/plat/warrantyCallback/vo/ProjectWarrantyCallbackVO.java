/**
 * 
 */
package com.dp.plat.warrantyCallback.vo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dp.plat.param.FileParam;
import com.dp.plat.warrantyCallback.entity.ProjectWarrantyCallback;

/**
 * @author w02611
 */
public class ProjectWarrantyCallbackVO extends ProjectWarrantyCallback {
	private static final long serialVersionUID = 2148976595233026978L;
	
    private String projectCode;
    private boolean hasPower;
    private String typeName;
    private String officeName;
    private String serviceName;
    private String createUser;
    private String areaPower;
    private String userPower;
    
    private String customerNameNotFuzzy;
    
    /**
     * 客户联系人搜索条件
     */
    private String customerSearch;
    /**
     * 客户联系方式搜索条件
     */
    private String customerContactSearch;
    
    /**
     * 续保意向查询参数
     */
    private Integer renewalIntentionInt;
    /**
     * 是否回访
     */
    private Integer hasCallback;
    /**
     * 是否续保
     */
    private Integer hasRenewal;
    /**
     * 是否可续采软件
     */
    private Integer hasLiscense;
    
    private String salerSearch;
    private String salerCode;
    private String salerName;
    private String salerContact;
    private String serviceManager;
    private String programManager;
    private String programManagerA;
    private String programManagerB;

    private Date processStartTime;
    private Date processEndTime;
    
    private Date warrantyEndTimeStart;
    private Date warrantyEndTimeEnd;
    
    private Date callbackTimeStart;
    private Date callbackTimeEnd;
    
    private Date nextCallbackTimeStart;
    private Date nextCallbackTimeEnd;

    private List<FileParam> deliverFileList;
    private List<Map<String, String>> quesnaireResultList;
    private Map<String, Object> questionColumns;
    
    private Map<String, Object> warrantyState;

    public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

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

    public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
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

    public String getCustomerNameNotFuzzy() {
		return customerNameNotFuzzy;
	}

	public void setCustomerNameNotFuzzy(String customerNameNotFuzzy) {
		this.customerNameNotFuzzy = customerNameNotFuzzy;
	}
	
	public String getCustomerSearch() {
		return customerSearch;
	}

	public void setCustomerSearch(String customerSearch) {
		this.customerSearch = customerSearch;
	}

	public String getCustomerContactSearch() {
		return customerContactSearch;
	}

	public void setCustomerContactSearch(String customerContactSearch) {
		this.customerContactSearch = customerContactSearch;
	}
	
	public String getSalerSearch() {
        return salerSearch;
    }

    public void setSalerSearch(String salerSearch) {
        this.salerSearch = salerSearch;
    }

    public String getSalerCode() {
        return salerCode;
    }

    public void setSalerCode(String salerCode) {
        this.salerCode = salerCode;
    }

    public String getSalerName() {
        return salerName;
    }

    public void setSalerName(String salerName) {
        this.salerName = salerName;
    }
    
    public String getSalerContact() {
        return salerContact;
    }

    public void setSalerContact(String salerContact) {
        this.salerContact = salerContact;
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
    
    public Integer getRenewalIntentionInt() {
		return renewalIntentionInt;
	}

	public void setRenewalIntentionInt(Integer renewalIntentionInt) {
		this.renewalIntentionInt = renewalIntentionInt;
	}
	
	public Integer getHasCallback() {
		return hasCallback;
	}

	public void setHasCallback(Integer hasCallback) {
		this.hasCallback = hasCallback;
	}

	public Integer getHasRenewal() {
		return hasRenewal;
	}

	public void setHasRenewal(Integer hasRenewal) {
		this.hasRenewal = hasRenewal;
	}
	
	public Integer getHasLiscense() {
        return hasLiscense;
    }

    public void setHasLiscense(Integer hasLiscense) {
        this.hasLiscense = hasLiscense;
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

	public Date getWarrantyEndTimeStart() {
		return warrantyEndTimeStart;
	}

	public void setWarrantyEndTimeStart(Date warrantyEndTimeStart) {
		this.warrantyEndTimeStart = warrantyEndTimeStart;
	}

	public Date getWarrantyEndTimeEnd() {
		return warrantyEndTimeEnd;
	}

	public void setWarrantyEndTimeEnd(Date warrantyEndTimeEnd) {
		this.warrantyEndTimeEnd = warrantyEndTimeEnd;
	}
	
	public Date getCallbackTimeStart() {
		return callbackTimeStart;
	}

	public void setCallbackTimeStart(Date callbackTimeStart) {
		this.callbackTimeStart = callbackTimeStart;
	}

	public Date getCallbackTimeEnd() {
		return callbackTimeEnd;
	}

	public void setCallbackTimeEnd(Date callbackTimeEnd) {
		this.callbackTimeEnd = callbackTimeEnd;
	}

	public Date getNextCallbackTimeStart() {
		return nextCallbackTimeStart;
	}

	public void setNextCallbackTimeStart(Date nextCallbackTimeStart) {
		this.nextCallbackTimeStart = nextCallbackTimeStart;
	}

	public Date getNextCallbackTimeEnd() {
		return nextCallbackTimeEnd;
	}

	public void setNextCallbackTimeEnd(Date nextCallbackTimeEnd) {
		this.nextCallbackTimeEnd = nextCallbackTimeEnd;
	}

	public void setWarrantyState(Map<String, Object> warrantyState) {
		this.warrantyState = warrantyState;
	}

	public Map<String, Object> getWarrantyState() {
		return warrantyState;
	}

	/**
	 * 是否允许编辑，续保状态为3，或者接听情况以-开头
	 * @return
	 */
	public boolean canEdit() {
	    return canEdit(this.getRenewalIntention(), this.getCustomInfoByKey("phoneAnswerState", "0"));
	}
	
	/**
	 * 是否允许编辑，续保状态为3，或者接听情况以-开头
	 * @param renewalIntention
	 * @param phoneAnswerState
	 * @return
	 */
	public static boolean canEdit(Object renewalIntention, Object phoneAnswerState) {
//        // 是否允许编辑，续保状态为3，或者接听情况以-开头
//        if (Integer.valueOf(3).equals(renewalIntention) || (phoneAnswerState != null && phoneAnswerState.toString().startsWith("-"))) {
//            return true;
//        }
        return false;
    }
}
