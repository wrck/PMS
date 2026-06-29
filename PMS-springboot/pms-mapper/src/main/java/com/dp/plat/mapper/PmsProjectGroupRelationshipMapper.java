package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsProjectGroupRelationship;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 项目组关系Mapper
 */
@Mapper
public interface PmsProjectGroupRelationshipMapper extends BaseMapper<PmsProjectGroupRelationship> {

    @Select("SELECT project_group_code FROM pm_project_group_relationship WHERE project_code = #{projectCode} LIMIT 1")
    String selectGroupCodeByProjectCode(@Param("projectCode") String projectCode);

    @Select("SELECT COUNT(*) FROM pm_project_group_relationship WHERE project_code LIKE CONCAT(#{projectCodePrefix}, '%')")
    int countByProjectCodePrefix(@Param("projectCodePrefix") String projectCodePrefix);
}
