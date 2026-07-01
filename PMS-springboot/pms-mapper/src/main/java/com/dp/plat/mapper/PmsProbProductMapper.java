package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsProbProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface PmsProbProductMapper extends BaseMapper<PmsProbProduct> {

    @Select("SELECT * FROM pm_prob_product WHERE prob_id = #{probId} AND status = 1")
    List<PmsProbProduct> selectByProbId(@Param("probId") Long probId);

    /** 按参数查询公告产品列表(分页) */
    List<Map<String, Object>> selectProbProductListByParams(@Param("params") Map<String, Object> params);
}
