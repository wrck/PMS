package com.dp.plat.data.bean;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.dp.plat.param.ProjectQueryParam;
import com.dp.plat.util.Base64Util;
import com.dp.plat.util.Md5Util;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.Util;
/**
 * 项目主信息，对应 pm_project_header
 * @author admin
 *
 */

public class Project extends ProjectQueryParam{
	private Integer projectId;
	private String paramId;
	private String projectCode;
	private String smsProjectCode;
	private String projectName;
	private String projectGroupCode;
	private String projectGroupName;
	private String projectType;
	private String isback;//30表示创建项目，32表示指定项目经理，34表示填写渠道信息
	private String projectState;//项目状态

	private int projectStatus;
	private String projectStateName;
	private String contractNo;
	private String orderNumber;
	private String column001;
	private String column001Name;//字段对应的编码实际意义
	private String column001Desc;//字段的实际描述
	private String column002;
	private String column002Name;//字段对应的编码实际意义
	private String column002Desc;//字段的实际描述
	private String column003;
	private String column003Name;//字段对应的编码实际意义
	private String column003Desc;//字段的实际描述
	private String column004;
	private String column004Name;//字段对应的编码实际意义
	private String column004Desc;//字段的实际描述
	private String column005;
	private String column005Name;//字段对应的编码实际意义
	private String column005Desc;//字段的实际描述
	private String column006;
	private String column006Name;//字段对应的编码实际意义
	private String column006Desc;//字段的实际描述
	private String column007;
	private String column007Name;//字段对应的编码实际意义
	private String column007Desc;//字段的实际描述
	private String column008;
	private String column008Name;//字段对应的编码实际意义
	private String column008Desc;//字段的实际描述
	private Date column009;
	private String column009Name;//字段对应的编码实际意义
	private String column009Desc;//字段的实际描述
	private String column010;
	private String column010Name;//字段对应的编码实际意义
	private String column010Desc;//字段的实际描述
	private String column011;
	private String column011Name;//字段对应的编码实际意义
	private String column011Desc;//字段的实际描述
	private String column012;
	private String column012Name;//字段对应的编码实际意义
	private String column012Desc;//字段的实际描述
	private int column012Readonly;//控制实施方式是否可以修改   从SMS系统刷过来的不可修改 表现为-1表示可以修改 其他值不可以修改
	
	private String column013;
	private String column013Name;//字段对应的编码实际意义
	private String column013Desc;//字段的实际描述
	private String column014;
	private String column014Name;//字段对应的编码实际意义
	private String column014Desc;//字段的实际描述
	
	private String salesType; //项目订单类型，01：正常，02：借转销，14：销售类借货
	
	// 公司所属
	private String compCode; // 公司编码
	private String compId;// 公司主表Id
	private String compAbbr;// 公司简称
	private String compName;// 公司名称
	
	private Date projectStartTime;//项目开始时间，指定项目经理时间
	private Date projectCreateTime;//项目创建时间，指定服务经理时间
	private Date projectRefreshTime;//项目相关数据最后编辑时间
	private Date projectCloseTime;//项目闭环时间
	private String projectTimeType;//项目时间类型
	
	private String shipmentStateName;//订单出货状态
	/**
	 * 销售人员 pm_project_member
	 * @return
	 */
	private String marketName;
	private String systemName;
	private String expendName;
	private String industryName;
	private String officeName;
	private String salesManCode;
	private String salesManName;
	private String serviceManagerCode;
	private String oldServiceManagerCode;
	private String serviceManagerCodeforjson;
	private String programManagerCode;
	private String oldProgramManagerCode;
	private String programManagerCodeforjson;
	private String programManagerCodeB;
	private String oldProgramManagerCodeB;
	private String programManagerCodeforjsonB;
	private String memberRole;
	private String memberCode;
	private String oldMemberCode;
	private String memberName;
	private String phoneNum;
	private String email;
	private String fromFlag;
	private String partyRole;
	private String partyCode;
	private String partyName;
	private String deliverChannel;
	private String serviceChannel;
	private String agentChannel;
	private String partnerChannel; // 合作伙伴渠道，代理商/服务商
	private String customerProjectName;
	private String majorProjectLevel;// 重大项目级别
	private String orderCreateTime;	//订单创建时间-字符串
	private Date createTime;		//创建时间
	private String createBy;		//创建人
	private Date updateTime;		//修改时间
	private String updateBy;		//修改人
	private Date effectiveFrom;		//生效时间_起
	private Date effectiveTo;		//生效时间_止
	/* 业务属性 */
	
	/* 记录属性 */
	private int errCode;			//错误编码（统一为1表示不报错，2表示报错）
	private String errMess;			//错误信息
	private String dataTypeCode;	//基础数据类型
	/* 记录属性 */
	
	/* 安全属性*/
	private String validateFlag;	//安全标识（值为md5("success")是操作项目的必要条件）
	/* 安全属性*/
	
	//工作流字段
	private String taskId;

	/**
	 * 项目查询权限
	 * @return
	 */
	private String officeCodes;
	private String username;
	private String currentTask;
	private int shipmentState;
	private String tos;
	private String cos;
	/**
	 * 根据序列号查询项目
	 */
	private String barCode; 
	
	/**
	 * 维保状态
	 */
	private String warrantyStatus;
	private String warrantyStatusName;
	/**
	 * 维保级别
	 */
	private String warrantyGrade;
	private String warrantyGradeName;
	/**
	 * waf服务
	 */
	private String wafService;
	private String wafServiceName;
	
	/**
	 * 项目状态表字段
	 * 
	 * projectPlanState 工程计划状态
	 * shipmentState 发货状态
	 * projectExecutionState 项目实施状态
	 * closeProcessState 闭环流程状态
	 */
	private String projectPlanState; 
	private String projectPlanStateName;
	
	private String executionState; 
    private String executionStateName;
    
    private String closeProcessState; 
    private String closeProcessStateName;
    
	/**
	 * 从SMS过来的辅助字段
	 */
	private int serviceType;
	private String channelName;
	
	/**
	 * 项目维护用来判断是否是项目的团队成员，进行权限控制
	 */
	private String teamMemberCodes;
	
	public Project() {
	    super();
    }

    public Project(Integer projectId) {
        this.projectId = projectId;
    }

    public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public Integer getProjectId() {
		return projectId == null ? 0 : projectId;
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
	public String getProjectState() {
		return projectState;
	}
	public void setProjectState(String projectState) {
		this.projectState = projectState;
	}
	public String getProjectStateName() {
		return projectStateName;
	}
	public void setProjectStateName(String projectStateName) {
		this.projectStateName = projectStateName;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getColumn001() {
		return column001;
	}
	public void setColumn001(String column001) {
		this.column001 = column001;
	}
	public String getColumn001Name() {
		return column001Name;
	}
	public void setColumn001Name(String column001Name) {
		this.column001Name = column001Name;
	}
	public String getColumn001Desc() {
		return column001Desc;
	}
	public void setColumn001Desc(String column001Desc) {
		this.column001Desc = column001Desc;
	}
	public String getColumn002() {
		return column002;
	}
	public void setColumn002(String column002) {
		this.column002 = column002;
	}
	public String getColumn002Name() {
		return column002Name;
	}
	public void setColumn002Name(String column002Name) {
		this.column002Name = column002Name;
	}
	public String getColumn002Desc() {
		return column002Desc;
	}
	public void setColumn002Desc(String column002Desc) {
		this.column002Desc = column002Desc;
	}
	public String getColumn003() {
		return column003;
	}
	public void setColumn003(String column003) {
		this.column003 = column003;
	}
	public String getColumn003Name() {
		return column003Name;
	}
	public void setColumn003Name(String column003Name) {
		this.column003Name = column003Name;
	}
	public String getColumn003Desc() {
		return column003Desc;
	}
	public void setColumn003Desc(String column003Desc) {
		this.column003Desc = column003Desc;
	}
	public String getColumn004() {
		return column004;
	}
	public void setColumn004(String column004) {
		this.column004 = column004;
	}
	public String getColumn004Name() {
		return column004Name;
	}
	public void setColumn004Name(String column004Name) {
		this.column004Name = column004Name;
	}
	public String getColumn004Desc() {
		return column004Desc;
	}
	public void setColumn004Desc(String column004Desc) {
		this.column004Desc = column004Desc;
	}
	public String getColumn005() {
		return column005;
	}
	public void setColumn005(String column005) {
		this.column005 = column005;
	}
	public String getColumn005Name() {
		return column005Name;
	}
	public void setColumn005Name(String column005Name) {
		this.column005Name = column005Name;
	}
	public String getColumn005Desc() {
		return column005Desc;
	}
	public void setColumn005Desc(String column005Desc) {
		this.column005Desc = column005Desc;
	}
	public String getColumn006() {
		return column006;
	}
	public void setColumn006(String column006) {
		this.column006 = column006;
	}
	public String getColumn006Name() {
		return column006Name;
	}
	public void setColumn006Name(String column006Name) {
		this.column006Name = column006Name;
	}
	public String getColumn006Desc() {
		return column006Desc;
	}
	public void setColumn006Desc(String column006Desc) {
		this.column006Desc = column006Desc;
	}
	public String getColumn007() {
		return column007;
	}
	public void setColumn007(String column007) {
		this.column007 = column007;
	}
	public String getColumn007Name() {
		return column007Name;
	}
	public void setColumn007Name(String column007Name) {
		this.column007Name = column007Name;
	}
	public String getColumn007Desc() {
		return column007Desc;
	}
	public void setColumn007Desc(String column007Desc) {
		this.column007Desc = column007Desc;
	}
	public String getColumn008() {
		return column008;
	}
	public void setColumn008(String column008) {
		this.column008 = column008;
	}
	public String getColumn008Name() {
		return column008Name;
	}
	public void setColumn008Name(String column008Name) {
		this.column008Name = column008Name;
	}
	public String getColumn008Desc() {
		return column008Desc;
	}
	public void setColumn008Desc(String column008Desc) {
		this.column008Desc = column008Desc;
	}
	public Date getColumn009() {
		return column009;
	}
	public void setColumn009(Date column009) {
		this.column009 = column009;
	}
	public String getColumn009Name() {
		return column009Name;
	}
	public void setColumn009Name(String column009Name) {
		this.column009Name = column009Name;
	}
	public String getColumn009Desc() {
		return column009Desc;
	}
	public void setColumn009Desc(String column009Desc) {
		this.column009Desc = column009Desc;
	}
	public String getSalesManCode() {
		return salesManCode;
	}
	public void setSalesManCode(String salesManCode) {
		this.salesManCode = salesManCode;
	}
	public String getSalesManName() {
		return salesManName;
	}
	public void setSalesManName(String salesManName) {
		this.salesManName = salesManName;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	public Date getEffectiveFrom() {
		return effectiveFrom;
	}
	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}
	public Date getEffectiveTo() {
		return effectiveTo;
	}
	public void setEffectiveTo(Date effectiveTo) {
		this.effectiveTo = effectiveTo;
	}
	public int getErrCode() {
		return errCode;
	}
	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}
	public String getErrMess() {
		return errMess;
	}
	public void setErrMess(String errMess) {
		this.errMess = errMess;
	}
	public String getValidateFlag() {
		if(MessageUtil.SAVE_SUCCESS.equals(validateFlag)){
			validateFlag = Md5Util.getMD5(MessageUtil.SAVE_SUCCESS.getBytes());
		}
		return validateFlag;
	}
	public void setValidateFlag(String validateFlag) {
		this.validateFlag = validateFlag;
	}
	public String getProjectGroupCode() {
		return projectGroupCode;
	}
	public void setProjectGroupCode(String projectGroupCode) {
		this.projectGroupCode = projectGroupCode;
	}
	public String getProjectGroupName() {
		return projectGroupName;
	}
	public void setProjectGroupName(String projectGroupName) {
		this.projectGroupName = projectGroupName;
	}
	public String getProjectType() {
		return projectType;
	}
	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}
	public String getOrderCreateTime() {
		if(column009 != null){
			orderCreateTime = Util.dateFormat(column009);
		}
		return orderCreateTime;
	}
	public void setOrderCreateTime(String orderCreateTime) {
		this.orderCreateTime = orderCreateTime;
	}
	public String getServiceManagerCode() {
		return serviceManagerCode;
	}
	public void setServiceManagerCode(String serviceManagerCode) {
		this.serviceManagerCode = serviceManagerCode;
	}
	/**
	 * @return the oldServiceManagerCode
	 */
	public String getOldServiceManagerCode() {
		return oldServiceManagerCode;
	}

	/**
	 * @param oldServiceManagerCode the oldServiceManagerCode to set
	 */
	public void setOldServiceManagerCode(String oldServiceManagerCode) {
		this.oldServiceManagerCode = oldServiceManagerCode;
	}

	public String getServiceManagerCodeforjson() {
		return serviceManagerCodeforjson;
	}
	public void setServiceManagerCodeforjson(String serviceManagerCodeforjson) {
		this.serviceManagerCodeforjson = serviceManagerCodeforjson;
	}
	public String getMemberRole() {
		return memberRole;
	}
	public void setMemberRole(String memberRole) {
		this.memberRole = memberRole;
	}
	public String getColumn010() {
		return column010;
	}
	public void setColumn010(String column010) {
		this.column010 = column010;
	}
	public String getColumn010Name() {
		return column010Name;
	}
	public void setColumn010Name(String column010Name) {
		this.column010Name = column010Name;
	}
	public String getColumn010Desc() {
		return column010Desc;
	}
	public void setColumn010Desc(String column010Desc) {
		this.column010Desc = column010Desc;
	}
	public String getColumn011() {
		return column011;
	}
	public void setColumn011(String column011) {
		this.column011 = column011;
	}
	public String getColumn011Name() {
		return column011Name;
	}
	public void setColumn011Name(String column011Name) {
		this.column011Name = column011Name;
	}
	public String getColumn011Desc() {
		return column011Desc;
	}
	public void setColumn011Desc(String column011Desc) {
		this.column011Desc = column011Desc;
	}
	public String getProgramManagerCode() {
		return programManagerCode;
	}
	public void setProgramManagerCode(String programManagerCode) {
		this.programManagerCode = programManagerCode;
	}
	/**
	 * @return the oldProgramManagerCode
	 */
	public String getOldProgramManagerCode() {
		return oldProgramManagerCode;
	}

	/**
	 * @param oldProgramManagerCode the oldProgramManagerCode to set
	 */
	public void setOldProgramManagerCode(String oldProgramManagerCode) {
		this.oldProgramManagerCode = oldProgramManagerCode;
	}

	public String getProgramManagerCodeforjson() {
		return programManagerCodeforjson;
	}
	public void setProgramManagerCodeforjson(String programManagerCodeforjson) {
		this.programManagerCodeforjson = programManagerCodeforjson;
	}
	/**
	 * @return the programManagerCodeB
	 */
	public String getProgramManagerCodeB() {
		return programManagerCodeB;
	}

	/**
	 * @param programManagerCodeB the programManagerCodeB to set
	 */
	public void setProgramManagerCodeB(String programManagerCodeB) {
		this.programManagerCodeB = programManagerCodeB;
	}

	/**
	 * @return the oldProgramManagerCodeB
	 */
	public String getOldProgramManagerCodeB() {
		return oldProgramManagerCodeB;
	}

	/**
	 * @param oldProgramManagerCodeB the oldProgramManagerCodeB to set
	 */
	public void setOldProgramManagerCodeB(String oldProgramManagerCodeB) {
		this.oldProgramManagerCodeB = oldProgramManagerCodeB;
	}

	/**
	 * @return the programManagerCodeforjsonB
	 */
	public String getProgramManagerCodeforjsonB() {
		return programManagerCodeforjsonB;
	}

	/**
	 * @param programManagerCodeforjsonB the programManagerCodeforjsonB to set
	 */
	public void setProgramManagerCodeforjsonB(String programManagerCodeforjsonB) {
		this.programManagerCodeforjsonB = programManagerCodeforjsonB;
	}

	public String getMemberCode() {
		return memberCode;
	}
	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}
	/**
	 * @return the oldMemberCode
	 */
	public String getOldMemberCode() {
		return oldMemberCode;
	}

	/**
	 * @param oldMemberCode the oldMemberCode to set
	 */
	public void setOldMemberCode(String oldMemberCode) {
		this.oldMemberCode = oldMemberCode;
	}

	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	public String getOfficeCodes() {
		return officeCodes;
	}
	public void setOfficeCodes(String officeCodes) {
		this.officeCodes = officeCodes;
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
	public String getOfficeName() {
		return officeName;
	}
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	public String getDataTypeCode() {
		return dataTypeCode;
	}
	public void setDataTypeCode(String dataTypeCode) {
		this.dataTypeCode = dataTypeCode;
	}
	public String getPartyRole() {
		return partyRole;
	}
	public void setPartyRole(String partyRole) {
		this.partyRole = partyRole;
	}
	public String getDeliverChannel() {
		return deliverChannel;
	}
	public void setDeliverChannel(String deliverChannel) {
		this.deliverChannel = deliverChannel;
	}
	public String getServiceChannel() {
		return serviceChannel;
	}
	public void setServiceChannel(String serviceChannel) {
		this.serviceChannel = serviceChannel;
	}
	public String getPartyName() {
		return partyName;
	}
	public void setPartyName(String partyName) {
		this.partyName = partyName;
	}
	public String getPartyCode() {
		return partyCode;
	}
	public void setPartyCode(String partyCode) {
		this.partyCode = partyCode;
	}
	public String getIsback() {
		return isback;
	}
	public void setIsback(String isback) {
		this.isback = isback;
	}
	public String getColumn012() {
		return column012;
	}
	public void setColumn012(String column012) {
		this.column012 = column012;
	}
	public String getColumn012Name() {
		return column012Name;
	}
	public void setColumn012Name(String column012Name) {
		this.column012Name = column012Name;
	}
	public String getColumn012Desc() {
		return column012Desc;
	}
	public void setColumn012Desc(String column012Desc) {
		this.column012Desc = column012Desc;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFromFlag() {
		return fromFlag;
	}
	public void setFromFlag(String fromFlag) {
		this.fromFlag = fromFlag;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getColumn013() {
		return column013;
	}
	public void setColumn013(String column013) {
		this.column013 = column013;
	}
	public String getColumn013Name() {
		return column013Name;
	}
	public void setColumn013Name(String column013Name) {
		this.column013Name = column013Name;
	}
	public String getColumn013Desc() {
		return column013Desc;
	}
	public void setColumn013Desc(String column013Desc) {
		this.column013Desc = column013Desc;
	}
	public String getCurrentTask() {
		return currentTask;
	}
	public void setCurrentTask(String currentTask) {
		this.currentTask = currentTask;
	}

	public String getSmsProjectCode() {
		return smsProjectCode;
	}

	public void setSmsProjectCode(String smsProjectCode) {
		this.smsProjectCode = smsProjectCode;
	}

	public String getAgentChannel() {
		return agentChannel;
	}

	public void setAgentChannel(String agentChannel) {
		this.agentChannel = agentChannel;
	}

	public String getPartnerChannel() {
        return partnerChannel;
    }

    public void setPartnerChannel(String partnerChannel) {
        this.partnerChannel = partnerChannel;
    }

    public String getTos() {
		return tos;
	}

	public void setTos(String tos) {
		this.tos = tos;
	}

	public String getCos() {
		return cos;
	}

	public void setCos(String cos) {
		this.cos = cos;
	}

	public String getColumn014() {
		return column014;
	}

	public void setColumn014(String column014) {
		this.column014 = column014;
	}

	public String getColumn014Name() {
		return column014Name;
	}

	public void setColumn014Name(String column014Name) {
		this.column014Name = column014Name;
	}

	public String getColumn014Desc() {
		return column014Desc;
	}

	public void setColumn014Desc(String column014Desc) {
		this.column014Desc = column014Desc;
	}

	public String getSalesType() {
        return salesType;
    }

    public void setSalesType(String salesType) {
        this.salesType = salesType;
    }

    public String getCompCode() {
        return compCode;
    }

    public void setCompCode(String compCode) {
        this.compCode = compCode;
    }

    public String getCompId() {
        return compId;
    }

    public void setCompId(String compId) {
        this.compId = compId;
    }
    
    public String getCompAbbr() {
		return compAbbr;
	}

	public void setCompAbbr(String compAbbr) {
		this.compAbbr = compAbbr;
	}

	public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public int getProjectStatus() {
		return projectStatus;
	}

	public void setProjectStatus(int projectStatus) {
		this.projectStatus = projectStatus;
	}

	public Date getProjectStartTime() {
		return projectStartTime;
	}

	public void setProjectStartTime(Date projectStartTime) {
		this.projectStartTime = projectStartTime;
	}

	public int getServiceType() {
		return serviceType;
	}

	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public int getColumn012Readonly() {
		return column012Readonly;
	}

	public void setColumn012Readonly(int column012Readonly) {
		this.column012Readonly = column012Readonly;
	}

	public String getParamId() {
		if(projectId != null && projectId != 0){
			return Base64Util.EncodeBase64(projectId);
		}
		return paramId;
	}

	public void setParamId(String paramId) {
		this.paramId = paramId;
	}

	public String getShipmentStateName() {
		return shipmentStateName;
	}

	public void setShipmentStateName(String shipmentStateName) {
		this.shipmentStateName = shipmentStateName;
	}

	public int getShipmentState() {
		return shipmentState;
	}

	public void setShipmentState(int shipmentState) {
		this.shipmentState = shipmentState;
	}

	public String getProjectPlanState() {
		return projectPlanState;
	}

	public void setProjectPlanState(String projectPlanState) {
		this.projectPlanState = projectPlanState;
	}

	public String getProjectPlanStateName() {
		return projectPlanStateName;
	}

	public void setProjectPlanStateName(String projectPlanStateName) {
		this.projectPlanStateName = projectPlanStateName;
	}
	
	public String getExecutionState() {
        return executionState;
    }

    public void setExecutionState(String executionState) {
        this.executionState = executionState;
    }

    public String getExecutionStateName() {
        return executionStateName;
    }

    public void setExecutionStateName(String executionStateName) {
        this.executionStateName = executionStateName;
    }

    public String getCloseProcessState() {
        return closeProcessState;
    }

    public void setCloseProcessState(String closeProcessState) {
        this.closeProcessState = closeProcessState;
    }

    public String getCloseProcessStateName() {
        return closeProcessStateName;
    }

    public void setCloseProcessStateName(String closeProcessStateName) {
        this.closeProcessStateName = closeProcessStateName;
    }

    public Date getProjectCreateTime() {
		return projectCreateTime;
	}

	public void setProjectCreateTime(Date projectCreateTime) {
		this.projectCreateTime = projectCreateTime;
	}

	public Date getProjectRefreshTime() {
		return projectRefreshTime;
	}

	public void setProjectRefreshTime(Date projectRefreshTime) {
		this.projectRefreshTime = projectRefreshTime;
	}

	public Date getProjectCloseTime() {
		return projectCloseTime;
	}

	public void setProjectCloseTime(Date projectCloseTime) {
		this.projectCloseTime = projectCloseTime;
	}

	public String getProjectTimeType() {
		return projectTimeType;
	}

	public void setProjectTimeType(String projectTimeType) {
		this.projectTimeType = projectTimeType;
	}

    public String getCustomerProjectName() {
        return customerProjectName;
    }

    public void setCustomerProjectName(String customerProjectName) {
        this.customerProjectName = customerProjectName;
    }

    public String getMajorProjectLevel() {
		return majorProjectLevel;
	}

	public void setMajorProjectLevel(String majorProjectLevel) {
		this.majorProjectLevel = majorProjectLevel;
	}

	public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getTeamMemberCodes() {
        return teamMemberCodes;
    }

    public void setTeamMemberCodes(String teamMemberCodes) {
        this.teamMemberCodes = teamMemberCodes;
    }

	public String getWarrantyStatus() {
		return warrantyStatus;
	}

	public void setWarrantyStatus(String warrantyStatus) {
		this.warrantyStatus = warrantyStatus;
	}

	public String getWarrantyStatusName() {
		return warrantyStatusName;
	}

	public void setWarrantyStatusName(String warrantyStatusName) {
		this.warrantyStatusName = warrantyStatusName;
	}

	public String getWarrantyGrade() {
		return warrantyGrade;
	}

	public void setWarrantyGrade(String warrantyGrade) {
		this.warrantyGrade = warrantyGrade;
	}

	public String getWarrantyGradeName() {
		return warrantyGradeName;
	}

	public void setWarrantyGradeName(String warrantyGradeName) {
		this.warrantyGradeName = warrantyGradeName;
	}

	public String getWafService() {
		return wafService;
	}

	public void setWafService(String wafService) {
		this.wafService = wafService;
	}

	public String getWafServiceName() {
		return wafServiceName;
	}

	public void setWafServiceName(String wafServiceName) {
		this.wafServiceName = wafServiceName;
	}

	public boolean getCheckWarranty() {
		return StringUtils.isNotBlank(this.warrantyStatus) || StringUtils.isNotBlank(this.warrantyGrade) || StringUtils.isNotBlank(this.wafService);
	}
}
