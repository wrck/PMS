package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsPresalesTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PmsPresalesTaskMapper extends BaseMapper<PmsPresalesTask> {

    @Select("SELECT * FROM pm_presales_task WHERE presales_id = #{presalesId}")
    List<PmsPresalesTask> selectByPresalesId(@Param("presalesId") Long presalesId);
}
