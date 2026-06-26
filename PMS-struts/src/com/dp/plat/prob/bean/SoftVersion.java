package com.dp.plat.prob.bean;

import org.apache.commons.lang.StringUtils;

import com.dp.plat.data.bean.CustomInfoEntity;

/**
 * 软件版本属性
 * 
 * @author j01441
 *
 */
public class SoftVersion extends CustomInfoEntity {
	private static final long serialVersionUID = 8150879090779424000L;
    private int id;
	private Integer probId = 0;
	private String conp;
	private Object conp1;
	private Object conp2;
	private String conpCondition;// 查询条件
    private String conpMark;
	private String cpld;
	private Object cpld1;
	private Object cpld2;
	private String cpldCondition;
	private String boot;
	private Object boot1;
	private Object boot2;
	private String bootCondition;
	private String pcb;
	private Object pcb1;
	private Object pcb2;
	private String pcbCondition;

	/**
	 * 手工录入
	 */
	private String manualEntry;
	/**
	 * 手工录入拆解
	 */
	private String manualEntrySub;
	/**
     * 版本类型
     */
    private String entryType;
	/**
	 * 版本范围系列
	 */
	private String entrySeries;
	/**
	 * 版本范围开始
	 */
	private String entryStart;
	/**
	 * 版本范围结束
	 */
	private String entryEnd;
	/**
	 * 缺省补充版本范围开始
	 */
	private String markStart;
	/**
	 * 缺省补充版本范围结束
	 */
	private String markEnd;
	/**
	 * 影响版本类型，0：所有系列，1：盒式系列，2：框式系列
	 */
	private Integer affectedType;
	private String affectedTypeName;
	private String platformType;
    private String platformTypeName;
    private String releaseType;
    private String releaseTypeName;
    private String architectureType;
    private String architectureTypeName;
    private String branchType;
    private String branchTypeName;
    /**
     * releaseType、architectureType、branchType的组合
     */
    private String softVersionTypes;
	/**
	 * 分组ID
	 */
	private Long groupId = 0l;
	/**
	 * 是否拆解
	 */
	private Integer splited = 1;
	
	private String createBy;
	private String updateBy;

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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getProbId() {
		return probId;
	}

	public void setProbId(Integer probId) {
		this.probId = probId;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public String getConpCondition() {
		return conpCondition;
	}

	public void setConpCondition(String conpCondition) {
		this.conpCondition = conpCondition;
		if (StringUtils.isNotBlank(this.conp) && "between".equalsIgnoreCase(conpCondition)) {
			String[] conps = this.conp.split("~");
			try {
				this.conp1 = Double.parseDouble(conps[0]);
				this.conp2 = Double.parseDouble(conps[1]);
			} catch (NumberFormatException e) {
				this.conp1 = conps[0];
				this.conp2 = conps[1];
			}
		}
	}
	
	public String getConpMark() {
        return conpMark;
    }

    public void setConpMark(String conpMark) {
        this.conpMark = conpMark;
    }

    public String getCpldCondition() {
		return cpldCondition;
	}

	public void setCpldCondition(String cpldCondition) {
		this.cpldCondition = cpldCondition;
		if (StringUtils.isNotBlank(this.cpld) && "between".equalsIgnoreCase(cpldCondition)) {
			String[] cplds = this.cpld.split("~");
			try {
				this.cpld1 = Double.parseDouble(cplds[0]);
				this.cpld2 = Double.parseDouble(cplds[1]);
			} catch (NumberFormatException e) {
				this.cpld1 = cplds[0];
				this.cpld2 = cplds[1];
			}
		}
	}

	public String getBootCondition() {
		return bootCondition;
	}

	public void setBootCondition(String bootCondition) {
		this.bootCondition = bootCondition;
		if (StringUtils.isNotBlank(this.boot) && "between".equalsIgnoreCase(bootCondition)) {
			String[] boots = this.boot.split("~");
			try {
				this.boot1 = Double.parseDouble(boots[0]);
				this.boot2 = Double.parseDouble(boots[1]);
			} catch (NumberFormatException e) {
				this.boot1 = boots[0];
				this.boot2 = boots[1];
			}
		}
	}

	public String getPcbCondition() {
		return pcbCondition;
	}

	public void setPcbCondition(String pcbCondition) {
		this.pcbCondition = pcbCondition;
		if (StringUtils.isNotBlank(this.pcb) && "between".equalsIgnoreCase(pcbCondition)) {
			String[] pcbs = this.pcb.split("~");
			try {
				this.pcb1 = Double.parseDouble(pcbs[0]);
				this.pcb2 = Double.parseDouble(pcbs[1]);
			} catch (NumberFormatException e) {
				this.pcb1 = pcbs[0];
				this.pcb2 = pcbs[1];
			}
		}
	}

	public SoftVersion(String conp, String boot, String cpld, String pcb) {
		super();
		this.conp = conp;
		this.cpld = cpld;
		this.boot = boot;
		this.pcb = pcb;
	}

	public SoftVersion() {

	}

	public Object getConp1() {
		return conp1;
	}

	public void setConp1(Object conp1) {
		this.conp1 = conp1;
	}

	public Object getConp2() {
		return conp2;
	}

	public void setConp2(Object conp2) {
		this.conp2 = conp2;
	}

	public Object getCpld1() {
		return cpld1;
	}

	public void setCpld1(Object cpld1) {
		this.cpld1 = cpld1;
	}

	public Object getCpld2() {
		return cpld2;
	}

	public void setCpld2(Object cpld2) {
		this.cpld2 = cpld2;
	}

	public Object getBoot1() {
		return boot1;
	}

	public void setBoot1(Object boot1) {
		this.boot1 = boot1;
	}

	public Object getBoot2() {
		return boot2;
	}

	public void setBoot2(Object boot2) {
		this.boot2 = boot2;
	}

	public Object getPcb1() {
		return pcb1;
	}

	public void setPcb1(Object pcb1) {
		this.pcb1 = pcb1;
	}

	public Object getPcb2() {
		return pcb2;
	}

	public void setPcb2(Object pcb2) {
		this.pcb2 = pcb2;
	}

	public String getManualEntry() {
		return manualEntry;
	}

	public void setManualEntry(String manualEntry) {
		this.manualEntry = manualEntry;
	}

	public String getManualEntrySub() {
		return manualEntrySub;
	}

	public void setManualEntrySub(String manualEntrySub) {
		this.manualEntrySub = manualEntrySub;
	}

	public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }

    public String getEntrySeries() {
        return entrySeries;
    }

    public void setEntrySeries(String entrySeries) {
        this.entrySeries = entrySeries;
    }

    public String getEntryStart() {
		return entryStart;
	}

	public void setEntryStart(String entryStart) {
		this.entryStart = entryStart;
	}

	public String getEntryEnd() {
		return entryEnd;
	}

	public void setEntryEnd(String entryEnd) {
		this.entryEnd = entryEnd;
	}

	public String getMarkStart() {
		return markStart;
	}

	public void setMarkStart(String markStart) {
		this.markStart = markStart;
	}

	public String getMarkEnd() {
		return markEnd;
	}

	public void setMarkEnd(String markEnd) {
		this.markEnd = markEnd;
	}
	
	public Integer getAffectedType() {
		return affectedType;
	}

	public void setAffectedType(Integer affectedType) {
		this.affectedType = affectedType;
	}
	
	public String getAffectedTypeName() {
		return affectedTypeName;
	}

	public void setAffectedTypeName(String affectedTypeName) {
		this.affectedTypeName = affectedTypeName;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Integer getSplited() {
		return splited;
	}

	public void setSplited(Integer splited) {
		this.splited = splited;
	}

    public String getPlatformType() {
        return (String) this.getCustomInfoByKey("platformType", this.platformType);
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
        this.setCustomInfoByKey("platformType", platformType);
    }

    public String getPlatformTypeName() {
        return (String) this.getCustomInfoByKey("platformTypeName", this.platformTypeName);
    }

    public void setPlatformTypeName(String platformTypeName) {
        this.platformTypeName = platformTypeName;
        this.setCustomInfoByKey("platformType", platformType);
    }

    public String getReleaseType() {
        return (String) this.getCustomInfoByKey("releaseType", this.releaseType);
    }

    public void setReleaseType(String releaseType) {
        this.releaseType = releaseType;
        this.setCustomInfoByKey("releaseType", releaseType);
    }

    public String getReleaseTypeName() {
        return (String) this.getCustomInfoByKey("releaseTypeName", this.releaseTypeName);
    }

    public void setReleaseTypeName(String releaseTypeName) {
        this.releaseTypeName = releaseTypeName;
        this.setCustomInfoByKey("releaseTypeName", releaseTypeName);
    }

    public String getArchitectureType() {
        return (String) this.getCustomInfoByKey("architectureType", this.architectureType);
    }

    public void setArchitectureType(String architectureType) {
        this.architectureType = architectureType;
        this.setCustomInfoByKey("architectureType", architectureType);
    }

    public String getArchitectureTypeName() {
        return (String) this.getCustomInfoByKey("architectureTypeName", this.architectureTypeName);
    }

    public void setArchitectureTypeName(String architectureTypeName) {
        this.architectureTypeName = architectureTypeName;
        this.setCustomInfoByKey("architectureTypeName", architectureTypeName);
    }

    public String getBranchType() {
        return (String) this.getCustomInfoByKey("branchType", this.branchType);
    }

    public void setBranchType(String branchType) {
        this.branchType = branchType;
        this.setCustomInfoByKey("branchType", branchType);
    }

    public String getBranchTypeName() {
        return (String) this.getCustomInfoByKey("branchTypeName", this.branchTypeName);
    }

    public void setBranchTypeName(String branchTypeName) {
        this.branchTypeName = branchTypeName;
        this.setCustomInfoByKey("branchTypeName", branchTypeName);
    }

    public String getSoftVersionTypes() {
        return (String) this.getCustomInfoByKey("softVersionTypes", this.softVersionTypes);
    }

    public void setSoftVersionTypes(String softVersionTypes) {
        this.softVersionTypes = softVersionTypes;
        this.setCustomInfoByKey("softVersionTypes", softVersionTypes);
    }

}
