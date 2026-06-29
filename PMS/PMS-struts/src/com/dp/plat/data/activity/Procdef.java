package com.dp.plat.data.activity;
/**
 * 对应activity的表： act_re_procdef
 * @author admin
 *
 */
public class Procdef {
	private String id;
	private int rev;
	private String category;
	private String name;
	private String key;
	private int version;
	private String deploymentId;
	private String resourceName;
	private String dgrmResouceName;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getRev() {
		return rev;
	}
	public void setRev(int rev) {
		this.rev = rev;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getDeploymentId() {
		return deploymentId;
	}
	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public String getDgrmResouceName() {
		return dgrmResouceName;
	}
	public void setDgrmResouceName(String dgrmResouceName) {
		this.dgrmResouceName = dgrmResouceName;
	}
	 
}
