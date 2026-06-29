package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsProbRestore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PmsProbRestoreMapper extends BaseMapper<PmsProbRestore> {

    @Select("SELECT * FROM pm_prob_restore WHERE prob_id = #{probId}")
    List<PmsProbRestore> selectByProbId(@Param("probId") Long probId);

    @Select("SELECT COUNT(*) FROM pm_prob_restore WHERE prob_id = #{probId} AND restore_status != 3")
    int countUnfinishedByProbId(@Param("probId") Long probId);
}
