package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.PmsPresalesComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PmsPresalesCommentMapper extends BaseMapper<PmsPresalesComment> {

    @Select("SELECT * FROM pm_presales_comment WHERE presales_id = #{presalesId} ORDER BY comment_time ASC")
    List<PmsPresalesComment> selectByPresalesId(@Param("presalesId") Long presalesId);
}
