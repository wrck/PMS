package com.dp.plat.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.workflow.entity.ApprovalHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审批历史 Mapper。
 */
@Mapper
public interface ApprovalHistoryMapper extends BaseMapper<ApprovalHistory> {
}
