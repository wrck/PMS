package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.SysBasicData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysBasicDataMapper extends BaseMapper<SysBasicData> {

    /** 根据数据类型查询基础数据 */
    @Select("SELECT * FROM fnd_basic_data WHERE data_type_code = #{dataTypeCode} ORDER BY sort")
    List<SysBasicData> selectByDataTypeCode(@Param("dataTypeCode") String dataTypeCode);

    /** 查询系统参数 */
    @Select("SELECT arg_value FROM fnd_sys_arg WHERE arg_code = #{code} LIMIT 1")
    String selectSysArg(@Param("code") String code);
}
