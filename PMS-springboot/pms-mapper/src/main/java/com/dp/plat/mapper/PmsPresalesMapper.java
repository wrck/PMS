package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsPresales;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PmsPresalesMapper extends BaseMapper<PmsPresales> {

    /** 查询售前发货信息 */
    List<Map<String, Object>> selectShipmentInfoByCode(@Param("presalesCode") String presalesCode, @Param("containRma") boolean containRma);

    /** 查询借转销信息 */
    List<Map<String, Object>> selectLend2SaleInfoByCode(@Param("presalesCode") String presalesCode);

    /** 查询核销信息 */
    List<Map<String, Object>> selectLend2RmaInfoByCode(@Param("presalesCode") String presalesCode);

    /** 查询临时授权信息 */
    List<Map<String, Object>> selectTempAuthInfo(@Param("params") Map<String, Object> params);
}
