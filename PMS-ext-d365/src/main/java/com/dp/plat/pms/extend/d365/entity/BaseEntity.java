package com.dp.plat.pms.extend.d365.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author w02611
 *
 */
public class BaseEntity implements Serializable{

	private static final long serialVersionUID = 4016882606830988730L;

	private Integer id;

	private String createBy;

	private Date createTime;

	private String updateBy;

	private Date updateTime;
	
	// 自定义信息
    private Map<String, Object> customInfo;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

    /**
     * 获取自定义信息
     *
     * @return customInfo - 自定义信息
     */
    public Map<String, Object> getCustomInfo() {
        return customInfo;
    }

    /**
     * 设置自定义信息
     *
     * @param customInfo 自定义信息
     */
    public void setCustomInfo(Map<String, Object> customInfo) {
        this.customInfo = customInfo;
    }

    /**
     * 获取自定义信息，指定key
     *
     * @return customInfo.get(Key) - 自定义信息
     */
	public Object getCustomInfoByKey(String key) {
		Map<String, Object> customInfo = getCustomInfo();
		if (customInfo != null && !customInfo.isEmpty()) {
			return customInfo.get(key);
		}
		return null;
	}
	
	/**
     * 获取自定义信息，指定key，带默认值
     *
	 * @param key 
	 * @param defaultValue
	 * @return customInfo.getOrDefault(Key, defaultValue) - 自定义信息
	 */
	public Object getCustomInfoByKey(String key, Object defaultValue) {
		Map<String, Object> customInfo = (Map<String, Object>) getCustomInfo();
		if (customInfo != null && !customInfo.isEmpty()) {
			return customInfo.getOrDefault(key, defaultValue);
		}
		return defaultValue;
	}

	/**
     * 设置自定义信息, key, value
     *
     * @param customInfo.put(key, value) 自定义信息
     */
	public void setCustomInfoByKey(String key, Object value) {
		Map<String, Object> customInfo = (Map<String, Object>) getCustomInfo();
		if (customInfo == null) {
			customInfo = new HashMap<>();
			this.setCustomInfo(customInfo);
		}
		customInfo.put(key, value);
	}
	
	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	protected String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}
