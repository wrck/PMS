package com.dp.plat.baseline.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.baseline.entity.TaskDependency;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务依赖 Mapper。
 */
@Mapper
public interface TaskDependencyMapper extends BaseMapper<TaskDependency> {
}
