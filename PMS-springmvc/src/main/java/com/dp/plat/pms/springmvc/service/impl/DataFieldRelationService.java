package com.dp.plat.pms.springmvc.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.pms.springmvc.dao.DataFieldRelationMapper;
import com.dp.plat.pms.springmvc.entity.DataFieldRelation;
import com.dp.plat.pms.springmvc.service.IDataFieldRelationService;

/**
 *
 * Created by CodeGenerator
 */
@Service("dataFieldRelationService")
public class DataFieldRelationService extends AbstractBaseService<DataFieldRelationMapper, DataFieldRelation>
		implements IDataFieldRelationService {

	/** 配置状态：草稿 */
	private static final String CONFIG_STATUS_DRAFT = "draft";
	/** 配置状态：已发布 */
	private static final String CONFIG_STATUS_PUBLISHED = "published";
	/** 配置状态：已禁用 */
	private static final String CONFIG_STATUS_DISABLED = "disabled";

	/**
	 * 旧版 status 字段：published 对应 1（findFieldList 运行时渲染按 status=1 过滤），其它为 0
	 */
	private static final Integer LEGACY_STATUS_PUBLISHED = 1;
	private static final Integer LEGACY_STATUS_INACTIVE = 0;

	@Override
	public List<DataFieldRelation> findPublishedConfig(String dataName, String dataType) {
		return dao.selectByDataNameAndConfigStatus(dataName, dataType, CONFIG_STATUS_PUBLISHED);
	}

	@Override
	public List<DataFieldRelation> findDraft(String dataName, String dataType) {
		return dao.selectByDataNameAndConfigStatus(dataName, dataType, CONFIG_STATUS_DRAFT);
	}

	@Override
	@Transactional
	public void saveDraft(List<DataFieldRelation> fields, String dataName, String dataType) {
		// 删除该 dataName + dataType 现有的草稿
		dao.deleteByDataNameAndConfigStatus(dataName, dataType, CONFIG_STATUS_DRAFT);
		if (fields == null) {
			return;
		}
		for (DataFieldRelation field : fields) {
			field.setDataName(dataName);
			field.setDataType(dataType);
			field.setConfigStatus(CONFIG_STATUS_DRAFT);
			// 草稿不参与运行时渲染（findFieldList 过滤 status=1）
			field.setStatus(LEGACY_STATUS_INACTIVE);
			this.insertSelective(field);
		}
	}

	@Override
	@Transactional
	public void publish(String dataName, String dataType) {
		// 计算新版本号 = 当前最大版本 + 1
		Integer maxVersion = dao.selectMaxVersion(dataName, dataType);
		int newVersion = (maxVersion == null ? 0 : maxVersion.intValue()) + 1;

		// 1. 将此前已发布的记录置为已禁用
		dao.updateStateByDataName(dataName, dataType, CONFIG_STATUS_PUBLISHED, CONFIG_STATUS_DISABLED,
				LEGACY_STATUS_INACTIVE);
		// 2. 将当前草稿置为已发布
		dao.updateStateByDataName(dataName, dataType, CONFIG_STATUS_DRAFT, CONFIG_STATUS_PUBLISHED,
				LEGACY_STATUS_PUBLISHED);
		// 3. 递增已发布记录的版本号
		dao.updateVersionByDataName(dataName, dataType, CONFIG_STATUS_PUBLISHED, Integer.valueOf(newVersion));
	}

	@Override
	@Transactional
	public void disable(String dataName, String dataType) {
		dao.updateStateByDataName(dataName, dataType, CONFIG_STATUS_PUBLISHED, CONFIG_STATUS_DISABLED,
				LEGACY_STATUS_INACTIVE);
	}
}
