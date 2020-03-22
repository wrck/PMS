package com.dp.plat.data.bean;

import com.dp.plat.type.DateTime;

public class OperateLog {

	private String name;
	private String ip;
	private int time;
	private String info;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public DateTime getTime() {
		return new DateTime(time);
	}
	public void setTime(int time) {
		this.time = time;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
}
