package com.dp.plat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.system.entity.SysConfig;
import com.dp.plat.system.mapper.SysConfigMapper;
import com.dp.plat.system.service.ISysConfigService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link ISysConfigService}.
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements ISysConfigService {

    @Override
    @Cacheable(value = "sysConfig", key = "#configKey")
    public SysConfig getByConfigKey(String configKey) {
        return this.getOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, configKey));
    }

    @Override
    @CacheEvict(value = "sysConfig", allEntries = true)
    public boolean create(SysConfig config) {
        return this.save(config);
    }

    @Override
    @CacheEvict(value = "sysConfig", allEntries = true)
    public boolean update(SysConfig config) {
        return this.updateById(config);
    }

    @Override
    @CacheEvict(value = "sysConfig", allEntries = true)
    public boolean deleteById(Long id) {
        return this.removeById(id);
    }

    @Override
    public Page<SysConfig> selectPage(Integer pageNum, Integer pageSize, String configName) {
        int num = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null || pageSize < 1 ? 10 : pageSize;
        return this.page(new Page<>(num, size),
                new LambdaQueryWrapper<SysConfig>()
                        .like(configName != null && !configName.isBlank(), SysConfig::getConfigName, configName)
                        .orderByDesc(SysConfig::getCreateTime));
    }
}
