package com.dp.plat.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.workflow.entity.ApprovalNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审批节点 Mapper。
 */
@Mapper
public interface ApprovalNodeMapper extends BaseMapper<ApprovalNode> {
}
