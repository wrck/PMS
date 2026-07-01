package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.DpComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审批意见Mapper - 对应老系统 DpComment / ActComment
 */
@Mapper
public interface DpCommentMapper extends BaseMapper<DpComment> {
}
