package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsSoftChangeLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 软件版本变更记录Mapper
 */
@Mapper
public interface PmsSoftChangeLogMapper extends BaseMapper<PmsSoftChangeLog> {

    /**
     * 根据项目ID查询变更记录
     */
    @Select("SELECT * FROM pm_soft_change_log WHERE project_id = #{projectId} ORDER BY create_time DESC")
    List<PmsSoftChangeLog> selectByProjectId(@Param("projectId") Long projectId);
}
