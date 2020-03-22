/**
 * 
 */
package com.dp.plat.support.mail.vo;

import java.util.Arrays;
import java.util.List;

import com.dp.plat.core.vo.DataTableColumn;

/**
 * @author w02611
 * @param <T>
 *
 */
public class PageParam<T> {

	private int draw = 0;
	private int start = 0;
	private long pageSize = 10L;
	private long total;
	private long filtered = -1;
	private T model;
	private List<DataTableColumn> columns;
	private String rowId;
	/**
	 * 是否初始化时懒加载
	 */
	private boolean lazyLoad = false;
	/**
	 * 是否为模糊查询，即多字段模糊查询
	 */
	private boolean fuzzySearch;
	/**
	 * 模糊查询内容
	 */
	private String fuzzy = "";
	/**
	 * 拼接好的 column1 asc,column2 desc,... order by 排序条件
	 */
	private String orderBy;
	
	/**
	 * 拼接好的 column1 asc,column2 desc,... order by 排序条件
	 */
	private String groupBy;
	
	/**
	 * 多重模糊查询内容
	 */
	private String[] multipleFuzzy;
	
	/**
	 * 自定义字段
	 */
	private String customField;

	public boolean isLazyLoad() {
		return lazyLoad;
	}

	public void setLazyLoad(boolean lazyLoad) {
		this.lazyLoad = lazyLoad;
	}

	/**
	 * @return the draw
	 */
	public int getDraw() {
		return draw;
	}

	/**
	 * @param draw
	 *            the draw to set
	 */
	public void setDraw(int draw) {
		this.draw = draw;
	}

	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * @return the pageSize
	 */
	public long getPageSize() {
		return pageSize;
	}

	/**
	 * @param pageSize
	 *            the pageSize to set
	 */
	public void setPageSize(long pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @return the total
	 */
	public long getTotal() {
		return total;
	}

	/**
	 * 设置记录总数，filtered默认与记录总数相同
	 * @param total
	 *            the total to set
	 */
	public void setTotal(long total) {
		if (this.filtered == -1) {
			this.filtered = total;
		}
		this.total = total;
	}

	/**
	 * @return the filtered
	 */
	public long getFiltered() {
		return filtered;
	}

	/**
	 * @param filtered
	 *            the filtered to set
	 */
	public void setFiltered(long filtered) {
		this.filtered = filtered;
	}

	/**
	 * @return the model
	 */
	public T getModel() {
		return model;
	}

	/**
	 * @param model
	 *            the model to set
	 */
	public void setModel(T model) {
		this.model = model;
	}

	/**
	 * @return the fuzzySearch
	 */
	public boolean isFuzzySearch() {
		return fuzzySearch;
	}

	/**
	 * @param fuzzySearch
	 *            the fuzzySearch to set
	 */
	public void setFuzzySearch(boolean fuzzySearch) {
		this.fuzzySearch = fuzzySearch;
	}

	/**
	 * @return the fuzzy
	 */
	public String getFuzzy() {
		return fuzzy;
	}

	/**
	 * @param fuzzy
	 *            the fuzzy to set
	 */
	public void setFuzzy(String fuzzy) {
		this.fuzzy = fuzzy.trim();
		this.multipleFuzzy = fuzzy.split(" ");
	}

	/**
	 * @return the orderBy
	 */
	public String getOrderBy() {
		return orderBy;
	}

	/**
	 * @param orderBy
	 *            the orderBy to set
	 */
	public void setOrderBy(String orderBy) {
		orderBy = orderBy.replaceAll("( \\s|\\S)*((%27)|(')|(%3D)|(=)|(/)|(%2F)|(\")|((%22)|(-|%2D){2})|(%23)|(%3B)|(;))+(\\s|\\S)*", "");
		this.orderBy = orderBy;
	}

	public String getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(String groupBy) {
		groupBy = groupBy.replaceAll("( \\s|\\S)*((%27)|(')|(%3D)|(=)|(/)|(%2F)|(\")|((%22)|(-|%2D){2})|(%23)|(%3B)|(;))+(\\s|\\S)*", "");
		this.groupBy = groupBy;
	}

	public List<DataTableColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<DataTableColumn> columns) {
		this.columns = columns;
	}

	public String getRowId() {
		return rowId;
	}

	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	public String[] getMultipleFuzzy() {
		return multipleFuzzy;
	}

	public void setMultipleFuzzy(String[] multipleFuzzy) {
		this.multipleFuzzy = multipleFuzzy;
	}

	public String getCustomField() {
		return customField;
	}

	public void setCustomField(String customField) {
		this.customField = customField;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PageParam [draw=").append(draw).append(", start=").append(start).append(", pageSize=")
				.append(pageSize).append(", total=").append(total).append(", filtered=").append(filtered).append(", ");
		if (model != null)
			builder.append("model=").append(model).append(", ");
		if (columns != null)
			builder.append("columns=").append(columns).append(", ");
		if (rowId != null)
			builder.append("rowId=").append(rowId).append(", ");
		builder.append("fuzzySearch=").append(fuzzySearch).append(", ");
		if (fuzzy != null)
			builder.append("fuzzy=").append(fuzzy).append(", ");
		if (orderBy != null)
			builder.append("orderBy=").append(orderBy).append(", ");
		if (multipleFuzzy != null)
			builder.append("multipleFuzzy=").append(Arrays.toString(multipleFuzzy));
		builder.append("]");
		return builder.toString();
	}
	
	public void clone(PageParam<?> pageParam) {
		this.draw = pageParam.getDraw();
		this.start = pageParam.getStart();
		this.pageSize = pageParam.getPageSize();
		this.total = pageParam.getTotal();
		this.filtered = pageParam.getFiltered();
		this.columns = pageParam.getColumns();
		this.rowId = pageParam.getRowId();
		this.fuzzy = pageParam.getFuzzy();
		this.fuzzySearch = pageParam.isFuzzySearch();
		this.orderBy = pageParam.getOrderBy();
		this.multipleFuzzy = pageParam.getMultipleFuzzy();
		this.lazyLoad = pageParam.isLazyLoad();
	}

}
