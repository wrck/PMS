package com.dp.plat.pms.springmvc.service;

import java.util.List;

import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.pms.springmvc.entity.DataFieldRelation;

/**
 *
 * Created by CodeGenerator
 */
public interface IDataFieldRelationService extends IAbstractBaseService<DataFieldRelation> {

	/**
	 * 查询已发布的配置，按 sort 排序。运行时渲染应使用该方法。
	 *
	 * @param dataName  数据名
	 * @param dataType  数据类型
	 * @return 已发布（published）的配置列表
	 */
	List<DataFieldRelation> findPublishedConfig(String dataName, String dataType);

	/**
	 * 查询草稿配置，按 sort 排序。
	 *
	 * @param dataName  数据名
	 * @param dataType  数据类型
	 * @return 草稿（draft）配置列表
	 */
	List<DataFieldRelation> findDraft(String dataName, String dataType);

	/**
	 * 保存草稿：替换指定 dataName + dataType 的草稿记录
	 * （删除现有草稿，插入新草稿，状态为 draft）。
	 *
	 * @param fields    字段配置列表
	 * @param dataName  数据名
	 * @param dataType  数据类型
	 */
	void saveDraft(List<DataFieldRelation> fields, String dataName, String dataType);

	/**
	 * 发布：将当前草稿置为已发布，将此前已发布的记录置为已禁用，并递增版本号。
	 *
	 * @param dataName  数据名
	 * @param dataType  数据类型
	 */
	void publish(String dataName, String dataType);

	/**
	 * 禁用：将指定 dataName + dataType 的已发布记录置为已禁用。
	 *
	 * @param dataName  数据名
	 * @param dataType  数据类型
	 */
	void disable(String dataName, String dataType);
}
