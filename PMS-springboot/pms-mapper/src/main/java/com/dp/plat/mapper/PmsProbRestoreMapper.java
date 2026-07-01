package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsProbRestore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface PmsProbRestoreMapper extends BaseMapper<PmsProbRestore> {

    @Select("SELECT * FROM pm_prob_restore WHERE prob_id = #{probId}")
    List<PmsProbRestore> selectByProbId(@Param("probId") Long probId);

    @Select("SELECT COUNT(*) FROM pm_prob_restore WHERE prob_id = #{probId} AND restore_status != 3")
    int countUnfinishedByProbId(@Param("probId") Long probId);

    /** 按参数查询恢复任务列表(用于工作台/管理员视图) */
    List<Map<String, Object>> selectTaskListByParams(@Param("params") Map<String, Object> params);
}
