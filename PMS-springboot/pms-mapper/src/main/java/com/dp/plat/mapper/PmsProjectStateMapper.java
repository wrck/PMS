package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsProjectState;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PmsProjectStateMapper extends BaseMapper<PmsProjectState> {

    @Select("SELECT COUNT(*) FROM pm_project_state WHERE project_id = #{projectId}")
    int countByProjectId(@Param("projectId") Long projectId);

    @Select("SELECT project_plan_state FROM pm_project_state WHERE project_id = #{projectId} LIMIT 1")
    String selectPlanState(@Param("projectId") Long projectId);

    @Update("UPDATE pm_project_state SET execution_state = #{executionState} WHERE project_id = #{projectId}")
    int updateExecutionState(@Param("projectId") Long projectId, @Param("executionState") String executionState);

    @Update("UPDATE pm_project_state SET close_process_state = #{closeProcessState} WHERE project_id = #{projectId}")
    int updateCloseProcessState(@Param("projectId") Long projectId, @Param("closeProcessState") String closeProcessState);
}
