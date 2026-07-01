package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsProbSoftVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface PmsProbSoftVersionMapper extends BaseMapper<PmsProbSoftVersion> {

    @Select("SELECT * FROM pm_prob_soft_version WHERE prob_id = #{probId}")
    List<PmsProbSoftVersion> selectByProbId(@Param("probId") Long probId);

    /** 按参数查询软件版本列表 */
    List<Map<String, Object>> selectListByParams(@Param("params") Map<String, Object> params);
}
