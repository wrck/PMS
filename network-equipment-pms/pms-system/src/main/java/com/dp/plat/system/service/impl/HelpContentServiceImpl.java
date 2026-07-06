package com.dp.plat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.system.entity.HelpContent;
import com.dp.plat.system.mapper.HelpContentMapper;
import com.dp.plat.system.service.IHelpContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Implementation of {@link IHelpContentService}.
 */
@Slf4j
@Service
public class HelpContentServiceImpl extends ServiceImpl<HelpContentMapper, HelpContent>
        implements IHelpContentService {

    /** 启用状态值 */
    private static final String STATUS_ENABLED = "0";

    @Override
    public List<HelpContent> listByCategory(String category) {
        LambdaQueryWrapper<HelpContent> wrapper = new LambdaQueryWrapper<HelpContent>()
                .eq(HelpContent::getStatus, STATUS_ENABLED)
                .eq(StringUtils.hasText(category), HelpContent::getCategory, category)
                .orderByAsc(HelpContent::getSortOrder)
                .orderByAsc(HelpContent::getId);
        return this.list(wrapper);
    }

    @Override
    public boolean incrementViewCount(Long id) {
        if (id == null) {
            return false;
        }
        return baseMapper.incrementViewCount(id) > 0;
    }
}
