package com.dp.plat.core.vo;

import java.util.Arrays;
import java.util.List;

public class TreeNode {

	/**
	 * 节点节点上的文本（必选）
	 */
	private String text;

	/**
	 * 节点上的图标
	 */
	private String icon;

	/**
	 * 节点被选择后显示的图标
	 */
	private String selectedIcon;

	/**
	 * 结合全局enableLinks选项为列表树节点指定URL
	 */
	private String href;

	/**
	 * 指定列表树的节点是否可选择，默认true。设置为false将使节点展开，并且不能被选择
	 */
	private Boolean selectable = true;

	/**
	 * 节点的前景色，覆盖全局的前景色选项。
	 */
	private String color;

	/**
	 * 节点的背景色，覆盖全局的背景色选项
	 */
	private String backColor;

	/**
	 * 通过结合全局showTags选项来在列表树节点的右边添加额外的信息。
	 */
	private String[] tags;

	private TreeNodeState state;
	/**
	 * 子节点，列表
	 */
	private List<TreeNode> nodes;

	/**
	 * 节点ID，自定义参数
	 */
	private Integer id;
	/**
	 * 父节点ID，自定义参数
	 */
	private String parentId;
	/**
	 * 排序值，自定义参数
	 */
	private Integer sort;
	/**
	 * 节点状态值（有效、失效）编辑时使用，自定义参数
	 */
	private Boolean status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public List<TreeNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<TreeNode> nodes) {
		this.nodes = nodes;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getSelectedIcon() {
		return selectedIcon;
	}

	public void setSelectedIcon(String selectedIcon) {
		this.selectedIcon = selectedIcon;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public Boolean getSelectable() {
		return selectable;
	}

	public void setSelectable(Boolean selectable) {
		this.selectable = selectable;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getBackColor() {
		return backColor;
	}

	public void setBackColor(String backColor) {
		this.backColor = backColor;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public TreeNodeState getState() {
		return state;
	}

	public void setState(TreeNodeState state) {
		this.state = state;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "TreeNode [" + (text != null ? "text=" + text + ", " : "") + (icon != null ? "icon=" + icon + ", " : "")
				+ (selectedIcon != null ? "selectedIcon=" + selectedIcon + ", " : "")
				+ (href != null ? "href=" + href + ", " : "")
				+ (selectable != null ? "selectable=" + selectable + ", " : "")
				+ (color != null ? "color=" + color + ", " : "")
				+ (backColor != null ? "backColor=" + backColor + ", " : "")
				+ (tags != null ? "tags=" + Arrays.toString(tags) + ", " : "")
				+ (state != null ? "state=" + state + ", " : "") + (nodes != null ? "nodes=" + nodes + ", " : "")
				+ (id != null ? "id=" + id + ", " : "") + (parentId != null ? "parentId=" + parentId + ", " : "")
				+ (sort != null ? "sort=" + sort : "") + "]";
	}

}
