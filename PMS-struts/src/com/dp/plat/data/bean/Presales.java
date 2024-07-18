package com.dp.plat.data.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

import com.dp.plat.data.activity.ActivityBaseBean;
import com.dp.plat.param.FileParam;

public class Presales extends ActivityBaseBean {
	private static final long serialVersionUID = -7959020425102335249L;
    private int presalesId;//业务主键
	private String projectState;//项目状态
	private String projectStates;//项目状态s
	private String projectStateName;//项目状态名称
	
	private String lendInfoId;//SMS借货主键ID
	private String presalesCode;//项目编码
	private String projectCode;//原项目编码
	private String projectName;//项目名称
	private String projectType;// 项目类型
	private String projectTypeName;// 项目类型名称
	
	private String marketName;//市场部
	private String systemName;//系统部
	private String expendName;//拓展部
	private String industryName;//行业
	private String officeCode;//办事处编码
	private String officeName;//办事处名称
	private String officeCodes;//办事处权限
	private String salesman;//销售人员
	private String salesmanLink;//联系方式
	private String productManager;//产品经理
	private String lendfiles;//借货交付件
	private String hasTransfer;// 是否存在借转销数据
	private String hasRma;// 是否存在未核销数据
	private String closeRemark;
	
	private String serviceManager;//服务经理编码
	private String serviceManagerName;//服务经理名称
	private String projectManager;//项目经理编码
	private String projectManagerName;//项目经理名称
	private String oldProjectManager;//原项目经理编码
	private String confirmFileIds;//现场测试确认单
	private List<FileParam> fileParams;//对应confirmFileIds下的文件具体信息
	
	private Date finshedTime;//项目完成测试时间
	
	private int quesnaireId;//回访问卷ID
	private int quesnaireState;//问卷状态
	private List<String> quesResultMarkList;
	
	private String instId;//流程ID
	private int applyState;//申请状态  -1草稿 1 审批中 2审批通过 
	private String applyStateName;
	private String applyBy;
	private String applyByName;
	private Date applyTime;
	private Date endTime;
	
	private String searchTimeType;
	private Date searchStartTime;
	private Date searchEndTime;
	
	/**
	 *  项目同步到项目开始的时间间隔
	 */
	private String applyDuration;
	/**
	 * 项目开始到结束的时间间隔
	 */
	private String totalDuration;
	/**
	 * 指派服务经理的时间耗时
	 */
    private String serviceDuration;
    /**
     * 指派项目经理的时间耗时
     */
    private String programDuration;
    /**
     * 跟踪测试的时间耗时
     */
    private String testDuration;
    /**
     * 回访的时间耗时
     */
    private String callbackDuration;
    /**
     * 服务经理审批的时间耗时
     */
    private String serviceApproveDuration;
	
    /**
     * 导出选项，0-基于项目，1-基于设备，2-基于回访
     */
    private String exportDetail;
    /**
     * 问卷结果行转列
     */
    private Map<String, Object> questionColumns;
    /**
     * 问卷结果拼接
     */
    private String questionResults;
    
    /**
     * 数据来源
     */
    private String source;
    
	public Presales() {
	    super();
    }
	
    public Presales(int presalesId) {
        this.presalesId = presalesId;
    }

    public int getPresalesId() {
		return presalesId;
	}
	public void setPresalesId(int presalesId) {
		this.presalesId = presalesId;
	}
	public String getPresalesCode() {
		return presalesCode;
	}
	public void setPresalesCode(String presalesCode) {
		this.presalesCode = presalesCode;
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
	public String getProjectType() {
		return projectType;
	}
	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}
	public String getProjectTypeName() {
		return projectTypeName;
	}
	public void setProjectTypeName(String projectTypeName) {
		this.projectTypeName = projectTypeName;
	}
	public String getMarketName() {
		return marketName;
	}
	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}
	public String getSystemName() {
		return systemName;
	}
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
	public String getExpendName() {
		return expendName;
	}
	public void setExpendName(String expendName) {
		this.expendName = expendName;
	}
	public String getIndustryName() {
		return industryName;
	}
	public void setIndustryName(String industryName) {
		this.industryName = industryName;
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
	public String getOfficeCodes() {
        return officeCodes;
    }
    public void setOfficeCodes(String officeCodes) {
        this.officeCodes = officeCodes;
    }
    public List<String> getOfficeCodeList() {
        return officeCodes != null ? Arrays.asList(officeCodes.split(",")) : new ArrayList<String>(0);
    }
    
    public String getSalesman() {
		return salesman;
	}
	public void setSalesman(String salesman) {
		this.salesman = salesman;
	}
	public String getSalesmanLink() {
		return salesmanLink;
	}
	public void setSalesmanLink(String salesmanLink) {
		this.salesmanLink = salesmanLink;
	}
	public String getProductManager() {
		return productManager;
	}
	public void setProductManager(String productManager) {
		this.productManager = productManager;
	}
	public String getServiceManager() {
		return serviceManager;
	}
	public void setServiceManager(String serviceManager) {
		this.serviceManager = serviceManager;
	}
	public String getServiceManagerName() {
		return serviceManagerName;
	}
	public void setServiceManagerName(String serviceManagerName) {
		this.serviceManagerName = serviceManagerName;
	}
	public String getProjectManager() {
		return projectManager;
	}
	public void setProjectManager(String projectManager) {
		this.projectManager = projectManager;
	}
	public String getProjectManagerName() {
		return projectManagerName;
	}
	public void setProjectManagerName(String projectManagerName) {
		this.projectManagerName = projectManagerName;
	}
	public String getOldProjectManager() {
		return oldProjectManager;
	}
	public void setOldProjectManager(String oldProjectManager) {
		this.oldProjectManager = oldProjectManager;
	}
	public String getInstId() {
		return instId;
	}
	public void setInstId(String instId) {
		this.instId = instId;
	}
	public int getApplyState() {
		return applyState;
	}
	public void setApplyState(int applyState) {
		this.applyState = applyState;
	}
	public String getApplyStateName() {
		return applyStateName;
	}
	public void setApplyStateName(String applyStateName) {
		this.applyStateName = applyStateName;
	}
	public String getApplyBy() {
		return applyBy;
	}
	public void setApplyBy(String applyBy) {
		this.applyBy = applyBy;
	}
	public String getApplyByName() {
		return applyByName;
	}
	public void setApplyByName(String applyByName) {
		this.applyByName = applyByName;
	}
	public Date getApplyTime() {
		return applyTime;
	}
	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getConfirmFileIds() {
		return confirmFileIds;
	}
	public void setConfirmFileIds(String confirmFileIds) {
		this.confirmFileIds = confirmFileIds;
	}
	public List<FileParam> getFileParams() {
		return fileParams;
	}
	public void setFileParams(List<FileParam> fileParams) {
		this.fileParams = fileParams;
	}
	public int getQuesnaireId() {
		return quesnaireId;
	}
	public void setQuesnaireId(int quesnaireId) {
		this.quesnaireId = quesnaireId;
	}
	public int getQuesnaireState() {
		return quesnaireState;
	}
	public void setQuesnaireState(int quesnaireState) {
		this.quesnaireState = quesnaireState;
	}
	public List<String> getQuesResultMarkList() {
		return quesResultMarkList;
	}
	public void setQuesResultMarkList(List<String> quesResultMarkList) {
		this.quesResultMarkList = quesResultMarkList;
	}
	public String getProjectState() {
		return projectState;
	}
	public void setProjectState(String projectState) {
		this.projectState = projectState;
	}
	public String getProjectStates() {
        return projectStates;
    }
    public void setProjectStates(String projectStates) {
        this.projectStates = projectStates;
    }
	public String getProjectStateName() {
		return projectStateName;
	}
	public void setProjectStateName(String projectStateName) {
		this.projectStateName = projectStateName;
	}
	public String getLendfiles() {
		return lendfiles;
	}
	public void setLendfiles(String lendfiles) {
		this.lendfiles = lendfiles;
	}
	public String getLendInfoId() {
		return lendInfoId;
	}
	public void setLendInfoId(String lendInfoId) {
		this.lendInfoId = lendInfoId;
	}
	public String getHasTransfer() {
        return hasTransfer;
    }
    public void setHasTransfer(String hasTransfer) {
        this.hasTransfer = hasTransfer;
    }
    public String getHasRma() {
        return hasRma;
    }
    public void setHasRma(String hasRma) {
        this.hasRma = hasRma;
    }
    
    public String getCloseRemark() {
        return closeRemark;
    }

    public void setCloseRemark(String closeRemark) {
        this.closeRemark = closeRemark;
    }

    public Date getFinshedTime() {
		return finshedTime;
	}
	public void setFinshedTime(Date finshedTime) {
		this.finshedTime = finshedTime;
	}
    public String getTotalDuration() {
        return totalDuration;
    }
    public String getApplyDuration() {
        return applyDuration;
    }
    public void setApplyDuration(String applyDuration) {
        this.applyDuration = applyDuration;
    }
    public void setTotalDuration(String totalDuration) {
        this.totalDuration = totalDuration;
    }
    public String getServiceDuration() {
        return serviceDuration;
    }
    public void setServiceDuration(String serviceDuration) {
        this.serviceDuration = serviceDuration;
    }
    public String getProgramDuration() {
        return programDuration;
    }
    public void setProgramDuration(String programDuration) {
        this.programDuration = programDuration;
    }
    public String getTestDuration() {
        return testDuration;
    }
    public void setTestDuration(String testDuration) {
        this.testDuration = testDuration;
    }
    public String getCallbackDuration() {
        return callbackDuration;
    }
    public void setCallbackDuration(String callbackDuration) {
        this.callbackDuration = callbackDuration;
    }

    public String getServiceApproveDuration() {
        return serviceApproveDuration;
    }

    public void setServiceApproveDuration(String serviceApproveDuration) {
        this.serviceApproveDuration = serviceApproveDuration;
    }

    public String getSearchTimeType() {
        return searchTimeType;
    }

    public void setSearchTimeType(String searchTimeType) {
        this.searchTimeType = searchTimeType;
    }

    public Date getSearchStartTime() {
        return searchStartTime;
    }

    public void setSearchStartTime(Date searchStartTime) {
        this.searchStartTime = searchStartTime;
    }

    public Date getSearchEndTime() {
        if (this.searchEndTime == null) {
            return null;
        }
        Date searchEndTime = DateUtils.addDays(this.searchEndTime, 1);
        return searchEndTime = DateUtils.addMilliseconds(searchEndTime, -1);
//        return searchEndTime;
    }

    public void setSearchEndTime(Date searchEndTime) {
        this.searchEndTime = searchEndTime;
    }

    public String getExportDetail() {
        return exportDetail;
    }

    public void setExportDetail(String exportDetail) {
        this.exportDetail = exportDetail;
    }

    public Map<String, Object> getQuestionColumns() {
        return questionColumns;
    }

    public void setQuestionColumns(Map<String, Object> questionColumns) {
        this.questionColumns = questionColumns;
    }

    public String getQuestionResults() {
        return questionResults;
    }

    public void setQuestionResults(String questionResults) {
        this.questionResults = questionResults;
    }

    /**
     * 数据来源
     * @return
     */
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
	
}
