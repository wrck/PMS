package com.dp.plat.core.pojo;
/**
 * 	描述系统上传附件信息
 * @author j01441
 *
 */
public class FileInfo extends BasePojo{
	
	private Integer typeId;
	
	private String name;
	
	private String path;
	
	private String ext;
	
	private Long size;

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}

	public FileInfo(Integer typeId, String name, String path, String ext, Long size) {
		super();
		this.typeId = typeId;
		this.name = name;
		this.path = path;
		this.ext = ext;
		this.size = size;
	}
	public FileInfo() {
	}
	
}
