package com.dp.plat.pms.springmvc.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.entity.DataFieldRelation;
import com.dp.plat.pms.springmvc.service.IDataFieldRelationService;
import com.dp.plat.pms.springmvc.vo.DataFieldRelationVO;

public class BaseController {
	public final static String DATATYPE_FORM = "form";
	public final static String DATATYPE_TABLE = "table";
	public final static String DATATYPE_NAVTAB = "tab";

	protected IDataFieldRelationService dataFieldRelationService;

	@Autowired
	public void setDataFieldRelationService(IDataFieldRelationService dataFieldRelationService) {
		this.dataFieldRelationService = dataFieldRelationService;
	}

	/**
	 * 查询dataName, dataType，默认withSuper：true 带父级数据
	 * 
	 * @param dataName
	 * @param dataType
	 * @withSuper true
	 * @return
	 */
	protected List<Object> findFieldList(String dataName, String dataType) {
		return this.findFieldList(dataName, dataType, true);
	}

	/**
	 * 查询dataName, dataType，是否带父类
	 * 
	 * @param dataName
	 * @param dataType
	 * @param withSuper
	 * @return
	 */
	protected List<Object> findFieldList(String dataName, String dataType, Boolean withSuper) {
		PageParam<Object> tPage = new PageParam<>();
		DataFieldRelationVO dataFieldRelation = new DataFieldRelationVO(dataName, dataType, 1, withSuper);
		tPage.setPageSize(-1);
		tPage.setOrderBy("sort");
		tPage.setModel(dataFieldRelation);
		List<Object> fieldList = dataFieldRelationService.selectBySelectivePageable(tPage);
		return fieldList;
	}

	/**
	 * 查询表格的列，带父级表
	 * 
	 * @param dataName
	 * @return
	 */
	protected List<DataTableColumn> findColumnList(String dataName) {
		return this.findColumnList(dataName, true);
	}

	/**
	 * 查询表格的列
	 * 
	 * @param dataName
	 * @param withSuper
	 * @return
	 */
	protected List<DataTableColumn> findColumnList(String dataName, Boolean withSuper) {
		List<Object> fieldList = this.findFieldList(dataName, DATATYPE_TABLE, withSuper);
		List<DataTableColumn> columns = new ArrayList<>();
		for (Iterator<Object> iterator = fieldList.iterator(); iterator.hasNext();) {
			DataFieldRelation dataFieldRelation = (DataFieldRelation) iterator.next();
			DataTableColumn dataTableColumn = dataFieldRelation;
			String alias = dataFieldRelation.getAlias();
			dataTableColumn.setTitle(dataFieldRelation.getTitle());
			dataTableColumn.setName(alias);
			columns.add((DataFieldRelation) dataTableColumn);
		}
		return columns;
	}
	
	/**
	 * 查询Tab标签页，带父级页
	 * 
	 * @param dataName
	 * @return
	 */
	protected List<?> findNavTabList(String dataName) {
		return this.findNavTabList(dataName, true);
	}

	/**
	 * 查询Tab标签页
	 * 
	 * @param dataName
	 * @param withSuper
	 * @return
	 */
	protected List<?> findNavTabList(String dataName, Boolean withSuper) {
		List<Object> tabList = this.findFieldList(dataName, DATATYPE_NAVTAB, withSuper);
		return tabList;
	}
}
