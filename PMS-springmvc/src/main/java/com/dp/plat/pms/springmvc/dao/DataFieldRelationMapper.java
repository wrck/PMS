package com.dp.plat.pms.springmvc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dp.plat.core.dao.AbstractBaseMapper;
import com.dp.plat.pms.springmvc.entity.DataFieldRelation;

public interface DataFieldRelationMapper extends AbstractBaseMapper<DataFieldRelation> {

	/**
	 * 按 dataName + dataType + configStatus 查询配置，按 sort 排序
	 */
	List<DataFieldRelation> selectByDataNameAndConfigStatus(@Param("dataName") String dataName,
			@Param("dataType") String dataType, @Param("configStatus") String configStatus);

	/**
	 * 按 dataName + dataType + configStatus 删除配置
	 */
	int deleteByDataNameAndConfigStatus(@Param("dataName") String dataName, @Param("dataType") String dataType,
			@Param("configStatus") String configStatus);

	/**
	 * 状态机迁移：将 dataName + dataType 下指定 configStatus 的记录迁移到新状态，
	 * 同时同步旧版 status 字段（published=1，其它=0）以保持 findFieldList 运行时渲染兼容
	 */
	int updateStateByDataName(@Param("dataName") String dataName, @Param("dataType") String dataType,
			@Param("oldConfigStatus") String oldConfigStatus, @Param("newConfigStatus") String newConfigStatus,
			@Param("status") Integer status);

	/**
	 * 查询 dataName + dataType 下的最大版本号
	 */
	Integer selectMaxVersion(@Param("dataName") String dataName, @Param("dataType") String dataType);

	/**
	 * 设置 dataName + dataType + configStatus 下记录的版本号
	 */
	int updateVersionByDataName(@Param("dataName") String dataName, @Param("dataType") String dataType,
			@Param("configStatus") String configStatus, @Param("version") Integer version);
}
