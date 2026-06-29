package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.SysFileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysFileInfoMapper extends BaseMapper<SysFileInfo> {

    /** 根据文件路径查询文件 */
    @Select("SELECT * FROM fnd_file_info WHERE file_path = #{filePath} LIMIT 1")
    SysFileInfo selectByFilePath(@Param("filePath") String filePath);
}
