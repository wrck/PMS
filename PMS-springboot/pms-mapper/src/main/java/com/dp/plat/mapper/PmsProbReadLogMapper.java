package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsProbReadLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PmsProbReadLogMapper extends BaseMapper<PmsProbReadLog> {

    @Select("SELECT * FROM pm_prob_read_log WHERE prob_id = #{probId} ORDER BY read_time DESC")
    List<PmsProbReadLog> selectByProbId(@Param("probId") Long probId);
}
