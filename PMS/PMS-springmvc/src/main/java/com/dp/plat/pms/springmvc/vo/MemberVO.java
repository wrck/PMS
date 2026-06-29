package com.dp.plat.pms.springmvc.vo;

import java.util.Date;

import com.dp.plat.pms.springmvc.entity.ProjectMember;

public class MemberVO extends ProjectMember {
	
	private Integer orgId;
	
	private String createName;

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public void setEffective(Date date) {
		this.setEffectiveFrom(date);
		this.setEffectiveTo(date);
	}
	
}
