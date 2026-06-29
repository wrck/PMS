package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsProjectTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PmsProjectTaskMapper extends BaseMapper<PmsProjectTask> {

    /** 查询项目的任务列表 */
    @Select("SELECT * FROM pm_project_task WHERE project_id = #{projectId} " +
            "AND effective_from <= NOW() AND (effective_to IS NULL OR effective_to > NOW()) " +
            "ORDER BY event_plan_happen_date ASC")
    List<PmsProjectTask> selectActiveByProjectId(@Param("projectId") Long projectId);

    /** 将项目的旧计划置为失效 */
    @Update("UPDATE pm_project_task SET effective_to = NOW() WHERE project_id = #{projectId} " +
            "AND effective_from <= NOW() AND (effective_to IS NULL OR effective_to > NOW())")
    int invalidateByProjectId(@Param("projectId") Long projectId);

    /** 查询项目计划状态 */
    @Select("SELECT project_plan_state FROM pm_project_state WHERE project_id = #{projectId} LIMIT 1")
    String selectPlanState(@Param("projectId") Long projectId);

    /** 查询当前工程计划阶段 */
    @Select("SELECT task_type_code FROM pm_project_task WHERE project_id = #{projectId} " +
            "AND effective_from <= NOW() AND (effective_to IS NULL OR effective_to > NOW()) " +
            "ORDER BY event_plan_happen_date ASC LIMIT 1")
    String selectCurrentPlan(@Param("projectId") Long projectId);
}
