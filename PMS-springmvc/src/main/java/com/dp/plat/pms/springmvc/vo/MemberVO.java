package com.dp.plat.pms.springmvc.vo;

import java.util.Date;

import com.dp.plat.pms.springmvc.entity.ProjectMember;

public class MemberVO extends ProjectMember {

	public void setEffective(Date date) {
		this.setEffectiveFrom(date);
		this.setEffectiveTo(date);
	}
}
