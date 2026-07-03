package com.dp.plat.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.system.entity.SysConfig;

/**
 * Service for {@link SysConfig}.
 */
public interface ISysConfigService extends IService<SysConfig> {

    /**
     * Get config value by config key, or {@code null} if not found.
     */
    SysConfig getByConfigKey(String configKey);

    /**
     * Create a config entry.
     */
    boolean create(SysConfig config);

    /**
     * Update a config entry.
     */
    boolean update(SysConfig config);

    /**
     * Delete a config entry by id.
     */
    boolean deleteById(Long id);

    /**
     * Paginated config query with optional keyword filter on configName/configKey.
     */
    Page<SysConfig> selectPage(Integer pageNum, Integer pageSize, String configName);
}
