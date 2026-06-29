package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsProjectPlanEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 工程计划事件节点Mapper
 */
@Mapper
public interface PmsProjectPlanEventMapper extends BaseMapper<PmsProjectPlanEvent> {

    /**
     * 根据项目ID查询计划事件
     */
    @Select("SELECT * FROM pm_project_plan_event WHERE project_id = #{projectId}")
    List<PmsProjectPlanEvent> selectByProjectId(@Param("projectId") Long projectId);
}
