package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsProjectDeliver;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PmsProjectDeliverMapper extends BaseMapper<PmsProjectDeliver> {

    /** 查询项目的交付件列表 */
    @Select("SELECT * FROM pm_project_deliver WHERE project_id = #{projectId} ORDER BY create_time DESC")
    List<PmsProjectDeliver> selectByProjectId(@Param("projectId") Long projectId);

    /** 查询项目未上传的必传交付件数量 */
    @Select("SELECT COUNT(*) FROM pm_basic_prj_deliver d " +
            "WHERE d.project_type = #{projectType} AND d.required = 1 " +
            "AND NOT EXISTS (SELECT 1 FROM pm_project_deliver pd WHERE pd.project_id = #{projectId} AND pd.deliver_type = d.deliver_key)")
    int countRequiredUndelivered(@Param("projectId") Long projectId, @Param("projectType") String projectType);
}
