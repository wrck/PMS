package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmClosedLoopQuesnaire;
import org.apache.ibatis.annotations.Mapper;

/**
 * 问卷模板头Mapper - 对应老系统 PmClosedLoopQuesnaireService.selectQuesnaireHeaderList()
 */
@Mapper
public interface PmClosedLoopQuesnaireMapper extends BaseMapper<PmClosedLoopQuesnaire> {
}
