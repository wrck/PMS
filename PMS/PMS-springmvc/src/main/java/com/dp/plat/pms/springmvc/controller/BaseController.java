package com.dp.plat.pms.springmvc.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.util.MessageUtils;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.entity.DataFieldRelation;
import com.dp.plat.pms.springmvc.service.IDataFieldRelationService;
import com.dp.plat.pms.springmvc.vo.DataFieldRelationVO;
import com.dp.plat.util.MessageUtil;

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
		boolean isExcel = HttpContext.isExcel();
		List<Object> fieldList = this.findFieldList(dataName, DATATYPE_TABLE, withSuper);
		List<DataTableColumn> columns = new ArrayList<>();
		for (Iterator<Object> iterator = fieldList.iterator(); iterator.hasNext();) {
			DataFieldRelation dataFieldRelation = (DataFieldRelation) iterator.next();
			String media = dataFieldRelation.getMedia();
			DataTableColumn dataTableColumn = dataFieldRelation;
			String alias = dataFieldRelation.getAlias();
			dataTableColumn.setTitle(dataFieldRelation.getTitle());
//			MessageUtils.getLocaleMessage(dataFieldRelation.getTitleKey(), dataFieldRelation.getTitle());
			dataTableColumn.setName(alias);
			if (media == null || (isExcel && "excel".equalsIgnoreCase(media))) {
				columns.add((DataFieldRelation) dataTableColumn);
			} else if (!isExcel && !"excel".equalsIgnoreCase(media)) {
				columns.add((DataFieldRelation) dataTableColumn);
			}
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
		return this.findNavTabList(dataName, true, null);
	}
	
	/**
	 * 查询Tab标签页，带父级页
	 * 
	 * @param dataName
	 * @param model
	 * @return
	 */
	protected List<?> findNavTabList(String dataName, Model model) {
		return this.findNavTabList(dataName, true, model);
	}

	/**
	 * 查询Tab标签页
	 * 
	 * @param dataName
	 * @param withSuper
	 * @param model
	 * @return
	 */
	protected List<?> findNavTabList(String dataName, Boolean withSuper, Model model) {
		List<Object> tabList = this.findFieldList(dataName, DATATYPE_NAVTAB, withSuper);
		if (model != null) {
			Collection<String> permissions = (Collection<String>) model.getAttribute("permissions");
			if (permissions != null) {
				for (Iterator<Object> iterator = tabList.iterator(); iterator.hasNext();) {
					DataFieldRelation dataFieldRelation = (DataFieldRelation) iterator.next();
					String type = dataFieldRelation.getField();
					String alias = dataFieldRelation.getAlias();
					Set<String> perms = new HashSet<String>(2);
					String permission = null;
					if (StringUtils.isNotBlank(type)) {
						permission = type + ":list";
					} 
					if (UserContext.checkPermission(permission)) {
						perms.add(permission);
					} else if (StringUtils.isNotBlank(alias)) {
						String tPerm = alias + ":list";
						if (UserContext.checkPermission(tPerm)) {
							perms.add(permission);
							perms.add(tPerm);
						}
					}
					permissions.addAll(perms);
				}
			}
		}
		return tabList;
	}
}
