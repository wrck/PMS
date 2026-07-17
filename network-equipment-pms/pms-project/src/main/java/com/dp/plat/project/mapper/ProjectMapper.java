package com.dp.plat.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.project.entity.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * Mapper for {@link Project}.
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

    /**
     * 递归 CTE 计算主项目下所有子孙项目的加权平均进度。
     *
     * <p>关联设计文档：§2.5 主子项目递归汇总。
     *
     * <p>SQL 说明：
     * <ul>
     *   <li>锚点：根项目本身（id = rootProjectId）</li>
     *   <li>递归：按 parent_project_id 自连接，过滤未删除的子项目（deleted = 0）</li>
     *   <li>加权平均：Σ(progress × weight) / Σ(weight)，根项目（parent_project_id IS NULL）
     *       不计入分子分母，故仅统计子孙项目进度</li>
     *   <li>无子孙时 NULLIF 返回 NULL，由 service 回退到项目自身进度</li>
     * </ul>
     *
     * <p>依赖 MySQL 8.0+ 的 {@code WITH RECURSIVE} 语法（项目基线 MySQL 8.0.16）。
     *
     * @param rootProjectId 根项目 ID
     * @return 加权平均进度（百分比，0-100）；无子孙时为 null
     */
    @Select({
            "WITH RECURSIVE project_tree AS (",
            "  SELECT id, parent_project_id, progress, weight, project_path",
            "  FROM pms_project WHERE id = #{rootProjectId}",
            "  UNION ALL",
            "  SELECT p.id, p.parent_project_id, p.progress, p.weight, p.project_path",
            "  FROM pms_project p",
            "  JOIN project_tree t ON p.parent_project_id = t.id",
            "  WHERE p.deleted = 0",
            ")",
            "SELECT",
            "  SUM(CASE WHEN parent_project_id IS NULL THEN 0 ELSE progress * weight END) /",
            "  NULLIF(SUM(CASE WHEN parent_project_id IS NULL THEN 0 ELSE weight END), 0) AS aggregated_progress",
            "FROM project_tree"
    })
    BigDecimal calculateAggregatedProgress(@Param("rootProjectId") Long rootProjectId);
}
