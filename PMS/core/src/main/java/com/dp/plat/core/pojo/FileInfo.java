package com.dp.plat.core.pojo;

import com.dp.plat.core.entity.BaseEntity;

/**
 * 	描述系统上传附件信息
 * @author j01441
 *
 */
public class FileInfo extends BaseEntity {
	
    private static final long serialVersionUID = -131302632858664475L;

    // 对应file_type表的主键
	private Integer typeId;
	
    // 文件名称
	private String name;
	
    // 文件存储路径
	private String path;
	
    // 文件名后缀
	private String ext;
	
	private Long size;
	
    private String downloadKey;
    
    // 关联数据类型
    private String dataType;

    // 关联数据ID
    private Integer dataId;
    
    /**
     * 获取对应file_type表的主键
     *
     * @return typeId - 对应file_type表的主键
     */
	public Integer getTypeId() {
		return typeId;
	}

    /**
     * 设置对应file_type表的主键
     *
     * @param typeId 对应file_type表的主键
     */
	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

    /**
     * 获取文件名称
     *
     * @return name - 文件名称
     */
	public String getName() {
		return name;
	}

    /**
     * 设置文件名称
     *
     * @param name 文件名称
     */
	public void setName(String name) {
		this.name = name;
	}

    /**
     * 获取文件存储路径
     *
     * @return path - 文件存储路径
     */
	public String getPath() {
		return path;
	}

    /**
     * 设置文件存储路径
     *
     * @param path 文件存储路径
     */
	public void setPath(String path) {
		this.path = path;
	}

    /**
     * 获取文件名后缀
     *
     * @return ext - 文件名后缀
     */
	public String getExt() {
		return ext;
	}

    /**
     * 设置文件名后缀
     *
     * @param ext 文件名后缀
     */
	public void setExt(String ext) {
		this.ext = ext;
	}

    /**
     * 获取文件大小
     *
     * @return size - 文件大小
     */
    public Long getSize() {
		return size;
	}

    /**
     * 设置文件大小
     *
     * @param size 文件大小
     */
	public void setSize(Long size) {
		this.size = size;
	}
	
    /**
     * @return downloadKey
     */
    public String getDownloadKey() {
        return downloadKey;
    }

    /**
     * @param downloadKey
     */
    public void setDownloadKey(String downloadKey) {
        this.downloadKey = downloadKey;
    }
    
    /**
     * 获取关联数据类型
     *
     * @return dataType - 关联数据类型
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * 设置关联数据类型
     *
     * @param dataType 关联数据类型
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * 获取关联数据ID
     *
     * @return dataId - 关联数据ID
     */
    public Integer getDataId() {
        return dataId;
    }

    /**
     * 设置关联数据ID
     *
     * @param dataId 关联数据ID
     */
    public void setDataId(Integer dataId) {
        this.dataId = dataId;
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
