/**
 * 
 */
package com.dp.plat.core.vo;

/**
 * @author w02611
 *
 */
public class DataTableColumn {
	private String title;
	private String data;
	private String name;
	private Boolean orderable = true;
	private Boolean searchable = true;
	private Boolean visible = true;
	private String className;
	private String type;
	private String render;
	

	public DataTableColumn() {
    }

    /**
	 * @param title
	 * @param data
	 */
	public DataTableColumn(String title, String data) {
		this.title = title;
		this.data = data;
	}
	
	/**
	 * @param title
	 * @param data
	 * @param render
	 */
	public DataTableColumn(String title, String data, String render) {
		this.title = title;
		this.data = data;
		this.render = render;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOrderable(Boolean orderable) {
		this.orderable = orderable;
	}

	public Boolean getOrderable() {
		return orderable;
	}

	public void setSearchable(Boolean searchable) {
		this.searchable = searchable;
	}

	public Boolean getSearchable() {
		return searchable;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}
	
	public Boolean getVisible() {
		return visible;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRender() {
		return render;
	}

	public void setRender(String render) {
		this.render = render;
	}

}
