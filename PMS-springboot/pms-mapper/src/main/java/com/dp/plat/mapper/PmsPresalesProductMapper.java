package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsPresalesProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PmsPresalesProductMapper extends BaseMapper<PmsPresalesProduct> {

    @Select("SELECT * FROM pm_presales_product WHERE presales_id = #{presalesId}")
    List<PmsPresalesProduct> selectByPresalesId(@Param("presalesId") Long presalesId);
}
