package com.dp.plat.project.deliverable.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.project.deliverable.entity.DeliverableChecklist;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for {@link DeliverableChecklist}.
 *
 * @deprecated 终验校验已改为直接查 pms_deliverable 表，本 Mapper 保留用于历史兼容，将在下版本删除。
 */
@Deprecated
@Mapper
public interface DeliverableChecklistMapper extends BaseMapper<DeliverableChecklist> {
}
