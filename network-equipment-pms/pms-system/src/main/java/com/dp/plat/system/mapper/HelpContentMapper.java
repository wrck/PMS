package com.dp.plat.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.system.entity.HelpContent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * Mapper for {@link HelpContent}.
 */
@Mapper
public interface HelpContentMapper extends BaseMapper<HelpContent> {

    /**
     * Increment the view count of the given help content by 1.
     *
     * <p>使用乐观更新：直接 {@code view_count = view_count + 1}，避免并发覆盖。</p>
     *
     * @param id 帮助内容 id
     * @return 影响行数
     */
    @Update("UPDATE sys_help_content SET view_count = view_count + 1 WHERE id = #{id} AND deleted = 0")
    int incrementViewCount(@Param("id") Long id);
}
