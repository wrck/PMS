package com.dp.plat.prob.bean;

import org.apache.commons.lang.StringUtils;

/**
 * 软件版本属性
 * 
 * @author j01441
 *
 */
public class SoftVersion {
	private int id;
	private int probId;
	private String conp;
	private Object conp1;
	private Object conp2;
	private String conpCondition;// 查询条件
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

	private String manualEntry;
	
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

	public int getProbId() {
		return probId;
	}

	public void setProbId(int probId) {
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

}
