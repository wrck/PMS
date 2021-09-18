package com.dp.plat.core.pojo;

import java.util.ArrayList;

/**
 * @author w02611 头像
 */
public class AvatarResult {
	/**
	 * 表示图片是否已上传成功。
	 */
	private Boolean success;
	/**
	 * 自定义的附加消息。
	 */
	private String msg;
	/**
	 * 表示原始图片的保存地址。
	 */
	private String sourceUrl;
	/**
	 * 表示所有头像图片的保存地址，该变量为一个数组。
	 */
	private ArrayList<String> avatarUrls;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public ArrayList<String> getAvatarUrls() {
		return avatarUrls;
	}

	public void setAvatarUrls(ArrayList<String> avatarUrls) {
		this.avatarUrls = avatarUrls;
	}

}
