package com.dp.plat.core.vo;

/**
 * TreeNode中使用，表示 一个节点的初始状态
 */
public class TreeNodeState {
	/**
	 * 指示一个节点是否处于checked状态，用一个checkbox图标表示。
	 */
	private Boolean checked = false;

	/**
	 * 指示一个节点是否处于disabled状态。（不是selectable，expandable或checkable）
	 */
	private Boolean disabled = false;
	/**
	 * 指示一个节点是否处于展开状态。
	 */
	private Boolean expanded = false;
	/**
	 * 指示一个节点是否可以被选择。
	 */
	private Boolean selected = false;

	/**
	 * @param checked
	 * @param disabled
	 * @param expanded
	 * @param selected
	 */
	public TreeNodeState(Boolean checked, Boolean disabled, Boolean expanded, Boolean selected) {
		this.checked = checked;
		this.disabled = disabled;
		this.expanded = expanded;
		this.selected = selected;
	}

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public Boolean getExpanded() {
		return expanded;
	}

	public void setExpanded(Boolean expanded) {
		this.expanded = expanded;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

};