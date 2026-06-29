package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsProjectTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;

/**
 * 项目任务Mapper (扩展)
 */
@Mapper
public interface PmsProjectTaskExtMapper extends BaseMapper<PmsProjectTask> {

    /**
     * 查询项目是否有任务
     */
    @Select("SELECT COUNT(*) FROM pm_project_task WHERE project_id = #{projectId} AND effective_from <= NOW() AND (effective_to IS NULL OR effective_to > NOW())")
    int countByProjectId(@Param("projectId") Long projectId);

    /**
     * 复制任务计划到新合同
     */
    @Insert("INSERT INTO pm_project_task (project_id, contract_no, task_type_code, task_type_id, event_plan_happen_date, event_plan_happen_date_eng, " +
            "event_actual_finish_date, create_time, create_by, effective_from) " +
            "SELECT #{newProjectId}, #{contractNo}, task_type_code, task_type_id, NULL, event_plan_happen_date_eng, " +
            "event_actual_finish_date, NOW(), #{createBy}, NOW() FROM pm_project_task " +
            "WHERE project_id = #{sourceProjectId} AND effective_from <= NOW() AND (effective_to IS NULL OR effective_to > NOW()) LIMIT 1")
    int insertMergeTask(@Param("newProjectId") Long newProjectId, @Param("contractNo") String contractNo,
                        @Param("sourceProjectId") Long sourceProjectId, @Param("createBy") String createBy);
}
