package com.dp.plat.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.system.entity.HelpContent;

import java.util.List;

/**
 * Service for {@link HelpContent}.
 */
public interface IHelpContentService extends IService<HelpContent> {

    /**
     * List enabled help contents by category, ordered by sortOrder asc.
     *
     * @param category 帮助内容分类；为 {@code null} 时返回所有启用的内容
     * @return 帮助内容列表
     */
    List<HelpContent> listByCategory(String category);

    /**
     * Increment the view count of the given help content by 1.
     *
     * @param id 帮助内容 id
     * @return 是否更新成功
     */
    boolean incrementViewCount(Long id);
}
