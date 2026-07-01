package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmClosedLoopQuesnaireLine;
import org.apache.ibatis.annotations.Mapper;

/**
 * 问卷模板行Mapper - 对应老系统 PmClosedLoopQuesnaireService
 */
@Mapper
public interface PmClosedLoopQuesnaireLineMapper extends BaseMapper<PmClosedLoopQuesnaireLine> {
}
