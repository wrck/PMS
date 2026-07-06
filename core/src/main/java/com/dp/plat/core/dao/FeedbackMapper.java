package com.dp.plat.core.dao;

import java.util.List;

import com.dp.plat.core.pojo.Feedback;

/**
 * 用户反馈 Mapper
 *
 * @author trae
 *
 */
public interface FeedbackMapper {

    /**
     * 新增反馈
     *
     * @param record
     * @return
     */
    int insert(Feedback record);

    /**
     * 按主键选择性更新
     *
     * @param record
     * @return
     */
    int update(Feedback record);

    /**
     * 根据主键查询反馈
     *
     * @param id
     * @return
     */
    Feedback findById(Long id);

    /**
     * 按条件查询反馈列表
     *
     * @param query
     * @return
     */
    List<Feedback> findList(Feedback query);

    /**
     * 根据主键列表批量删除
     *
     * @param ids
     * @return
     */
    int deleteByIds(List<Long> ids);
}
