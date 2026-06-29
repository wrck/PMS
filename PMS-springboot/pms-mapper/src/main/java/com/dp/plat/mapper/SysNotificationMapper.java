package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.SysNotification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SysNotificationMapper extends BaseMapper<SysNotification> {

    /** 查询用户未读通知数 */
    @Select("SELECT COUNT(*) FROM fnd_notification WHERE receiver = #{username} AND read_flag = 0")
    int countUnreadByUsername(@Param("username") String username);

    /** 查询用户通知列表 */
    @Select("SELECT * FROM fnd_notification WHERE receiver = #{username} ORDER BY create_time DESC")
    List<SysNotification> selectByUsername(@Param("username") String username);

    /** 标记通知为已读 */
    @Update("UPDATE fnd_notification SET read_flag = 1, read_time = NOW() WHERE id = #{id}")
    int markAsRead(@Param("id") Long id);
}
