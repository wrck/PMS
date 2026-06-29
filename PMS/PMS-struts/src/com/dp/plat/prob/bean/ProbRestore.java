package com.dp.plat.prob.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;

/**
 * 技术公告修复任务对象
 * @author j01441
 *
 */
public class ProbRestore {
	private int id;
	private int probId;//技术公告ID
	private int processId;//流转过程ID
	private String serialNum;//设备序列号
	private String itemModel;//设备型号
	private String officeCode;//办事处编码
	private String areapower;//权限区域
	private String officeName;//办事处名称
	private String projectName;//项目名称
	private int projectId;//项目ID
	private String contractNo;//合同号
	private int restoreStatus;//修复状态
	private String restoreStatusName;//修复状态名称
	private String restoreRemark;//流转备注说明
	//软件版本  设置为技术公告发布时的软件版本
	private String conp;
	private String cpld;
	private String boot;
	private String pcb;
	//软件版本 为最新版本数据
	private String latestConp;
	private String latestCpld;
	private String latestBoot;
	private String latestPcb;
	private Date executeTime;//执行更新时间
	//办理人 or 办理角色
	private String assignee;
	private Integer assigneeRole;
	//ischecked  用于前台判断，没有业务意义
	private int ischecked;
	//记录产生、更新时间
	private String createBy;
	private Date createTime;
	private String updateBy;
	private Date updateTime;
	
	// 辅助字段
	private Prob prob;
	private SoftVersion version;
	private String projectCode;
	private String marketCode;
    private String systemCode;
    private String expendCode;
    private String industryCode;
    private String marketName;
    private String systemName;
    private String expendName;
    private String industryName;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSerialNum() {
		return serialNum;
	}
	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}
	public String getItemModel() {
		return itemModel;
	}
	public void setItemModel(String itemModel) {
		this.itemModel = itemModel;
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
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public int getRestoreStatus() {
		return restoreStatus;
	}
	public void setRestoreStatus(int restoreStatus) {
		this.restoreStatus = restoreStatus;
	}
	public int getProbId() {
		return probId;
	}
	public void setProbId(int probId) {
		this.probId = probId;
	}
	public String getConp() {
		return conp;
	}
	public void setConp(String conp) {
		this.conp = conp;
	}
	public String getCpld() {
		return cpld;
	}
	public void setCpld(String cpld) {
		this.cpld = cpld;
	}
	public String getBoot() {
		return boot;
	}
	public void setBoot(String boot) {
		this.boot = boot;
	}
	public String getPcb() {
		return pcb;
	}
	public void setPcb(String pcb) {
		this.pcb = pcb;
	}
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public Integer getAssigneeRole() {
		return assigneeRole;
	}
	public void setAssigneeRole(Integer assigneeRole) {
		this.assigneeRole = assigneeRole;
	}
	public int getIschecked() {
		return ischecked;
	}
	public void setIschecked(int ischecked) {
		this.ischecked = ischecked;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getRestoreStatusName() {
		return restoreStatusName;
	}
	public void setRestoreStatusName(String restoreStatusName) {
		this.restoreStatusName = restoreStatusName;
	}
	public int getProcessId() {
		return processId;
	}
	public void setProcessId(int processId) {
		this.processId = processId;
	}
	public String getRestoreRemark() {
		return restoreRemark;
	}
	public void setRestoreRemark(String restoreRemark) {
		this.restoreRemark = restoreRemark;
	}
	public String getLatestConp() {
		return latestConp;
	}
	public void setLatestConp(String latestConp) {
		this.latestConp = latestConp;
	}
	public String getLatestCpld() {
		return latestCpld;
	}
	public void setLatestCpld(String latestCpld) {
		this.latestCpld = latestCpld;
	}
	public String getLatestBoot() {
		return latestBoot;
	}
	public void setLatestBoot(String latestBoot) {
		this.latestBoot = latestBoot;
	}
	public String getLatestPcb() {
		return latestPcb;
	}
	public void setLatestPcb(String latestPcb) {
		this.latestPcb = latestPcb;
	}
	public Date getExecuteTime() {
		return executeTime;
	}
	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}
	public String getAreapower() {
		return areapower;
	}
	public void setAreapower(String areapower) {
		this.areapower = areapower;
	}
	public List<String> getAreapowerList() {
	    return areapower != null ? Arrays.asList(areapower.replaceAll("'\"", "").split(",")) : new ArrayList<String>(0);
    }
	
    public Prob getProb() {
        return prob;
    }
    public void setProb(Prob prob) {
        this.prob = prob;
    }
    public void setProbJson(String prob) {
        this.prob = JSON.parseObject(prob, Prob.class);
    }
    public SoftVersion getVersion() {
        return version;
    }
    public void setVersion(SoftVersion version) {
        this.version = version;
    }
    public String getProjectCode() {
        return projectCode;
    }
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }
    public String getMarketCode() {
        return marketCode;
    }
    public void setMarketCode(String marketCode) {
        this.marketCode = marketCode;
    }
    public String getSystemCode() {
        return systemCode;
    }
    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }
    public String getExpendCode() {
        return expendCode;
    }
    public void setExpendCode(String expendCode) {
        this.expendCode = expendCode;
    }
    public String getIndustryCode() {
        return industryCode;
    }
    public void setIndustryCode(String industryCode) {
        this.industryCode = industryCode;
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
}
