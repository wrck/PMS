package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsProjectSoftVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 项目软件版本Mapper
 */
@Mapper
public interface PmsProjectSoftVersionMapper extends BaseMapper<PmsProjectSoftVersion> {

    /**
     * 根据项目ID查询软件版本
     */
    @Select("SELECT * FROM pm_project_soft_version WHERE project_id = #{projectId} AND datastate = 1")
    List<PmsProjectSoftVersion> selectByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据合同号和项目ID查询软件版本
     */
    @Select("SELECT * FROM pm_project_soft_version WHERE contract_no = #{contractNo} AND project_id = #{projectId}")
    List<PmsProjectSoftVersion> selectByContractNoAndProjectId(@Param("contractNo") String contractNo, @Param("projectId") Long projectId);
}
