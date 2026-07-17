package com.dp.plat.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.workflow.entity.ApprovalRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 统一审批记录 Mapper。
 */
@Mapper
public interface ApprovalRecordMapper extends BaseMapper<ApprovalRecord> {
}
