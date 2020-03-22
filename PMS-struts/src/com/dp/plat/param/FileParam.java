package com.dp.plat.param;

import java.util.Date;

public class FileParam {
	private int id;
	private String fileName;
	private String filePath;
	private String fileType;
	private String uploadBy;
	private Date uploadTime;
	private int path;
	private String flag;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileType() {
        return fileType;
    }
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    public String getUploadBy() {
		return uploadBy;
	}
	public void setUploadBy(String uploadBy) {
		this.uploadBy = uploadBy;
	}
	public Date getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPath() {
		return path;
	}
	public void setPath(int path) {
		this.path = path;
	}
	public String getFlag() {
        return flag;
    }
    public void setFlag(String flag) {
        this.flag = flag;
    }
    public FileParam() {
		// TODO Auto-generated constructor stub
	}
	public FileParam(String fileName, String filePath, String uploadBy, int path) {
		super();
		this.fileName = fileName;
		this.filePath = filePath;
		this.uploadBy = uploadBy;
		this.path = path;
	}

	
}
