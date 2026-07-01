package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.ApprovalComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审批意见Mapper - 对应老系统 WorkflowDao / DpCommentMapper
 */
@Mapper
public interface ApprovalCommentMapper extends BaseMapper<ApprovalComment> {
}
