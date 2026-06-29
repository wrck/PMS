package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsInstruction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 项目批示Mapper
 */
@Mapper
public interface PmsInstructionMapper extends BaseMapper<PmsInstruction> {

    /**
     * 查询项目批示列表
     */
    @Select("SELECT * FROM pm_project_instruction WHERE project_id = #{projectId} AND data_type = 0 ORDER BY instructions_time DESC")
    List<PmsInstruction> selectInstructionsByProjectId(@Param("projectId") Long projectId);

    /**
     * 查询批示的反馈列表
     */
    @Select("SELECT * FROM pm_project_instruction WHERE instructions_id = #{instructionId} AND data_type = 1 ORDER BY instructions_time ASC")
    List<PmsInstruction> selectFeedbacksByInstructionId(@Param("instructionId") Long instructionId);
}
