package com.dp.plat.activiti.enums;

public enum VacationType {
	PAID("带薪假", 0), SICK("病假", 1), MATTER("事假", 2);
	
	// 成员变量
	private String name;
	private int index;

	// 构造方法
	private VacationType(String name, int index) {
		this.name = name;
		this.index = index;
	}

	// 普通方法
	public static String getName(int index) {
		for (VacationType c : VacationType.values()) {
			if (c.getIndex() == index) {
				return c.name;
			}
		}
		return null;
	}

	// get set 方法
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
