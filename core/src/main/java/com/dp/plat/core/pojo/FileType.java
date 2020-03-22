package com.dp.plat.core.pojo;

public class FileType extends BasePojo{
	
	private String name;
	
	private Integer limitSize;
	
	private String allowType;
	
	private boolean rename;//是否重命名
	
	private boolean cut;//是否压缩
	
	private boolean thumbnail;//是否生成缩略图
	
	private String dir;//保存的相对路径
	
	private String uploadUrl;//
	
	private String code;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getLimitSize() {
		return limitSize;
	}

	public void setLimitSize(Integer limitSize) {
		this.limitSize = limitSize;
	}

	public String getAllowType() {
		return allowType;
	}

	public void setAllowType(String allowType) {
		this.allowType = allowType;
	}

	public boolean isRename() {
		return rename;
	}

	public void setRename(boolean rename) {
		this.rename = rename;
	}

	public boolean isCut() {
		return cut;
	}

	public void setCut(boolean cut) {
		this.cut = cut;
	}

	public boolean isThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(boolean thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
