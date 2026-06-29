package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsProbSoftVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PmsProbSoftVersionMapper extends BaseMapper<PmsProbSoftVersion> {

    @Select("SELECT * FROM pm_prob_soft_version WHERE prob_id = #{probId}")
    List<PmsProbSoftVersion> selectByProbId(@Param("probId") Long probId);
}
