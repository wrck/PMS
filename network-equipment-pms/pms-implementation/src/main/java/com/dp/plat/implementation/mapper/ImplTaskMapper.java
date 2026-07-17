package com.dp.plat.implementation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.implementation.entity.ImplTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Mapper for {@link ImplTask}.
 */
@Mapper
public interface ImplTaskMapper extends BaseMapper<ImplTask> {

    /**
     * 按任务ID查询任务名称（跨模块调用：pms-baseline 循环依赖检测拼装闭环路径用）。
     *
     * @param taskId 任务ID
     * @return 任务名称（不存在或已删除返回 null）
     */
    @Select("SELECT task_name FROM pms_impl_task WHERE id = #{taskId} AND deleted = 0")
    String selectTaskNameById(@Param("taskId") Long taskId);
}
