package com.dp.plat.governance.issue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.governance.issue.entity.Issue;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for {@link Issue}.
 */
@Mapper
public interface IssueMapper extends BaseMapper<Issue> {
}
