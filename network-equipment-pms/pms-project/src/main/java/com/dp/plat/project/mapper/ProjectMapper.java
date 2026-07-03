package com.dp.plat.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.project.entity.Project;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for {@link Project}.
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
}
