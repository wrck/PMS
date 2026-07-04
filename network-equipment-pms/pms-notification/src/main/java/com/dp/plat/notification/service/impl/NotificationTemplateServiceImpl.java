package com.dp.plat.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.notification.entity.NotificationTemplate;
import com.dp.plat.notification.mapper.NotificationTemplateMapper;
import com.dp.plat.notification.service.INotificationTemplateService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 通知模板服务实现。
 */
@Service
public class NotificationTemplateServiceImpl
        extends ServiceImpl<NotificationTemplateMapper, NotificationTemplate>
        implements INotificationTemplateService {

    @Override
    public NotificationTemplate getByCode(String templateCode) {
        return baseMapper.selectOne(new LambdaQueryWrapper<NotificationTemplate>()
                .eq(NotificationTemplate::getTemplateCode, templateCode));
    }

    @Override
    public IPage<NotificationTemplate> list(int page, int size) {
        return baseMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<NotificationTemplate>().orderByDesc(NotificationTemplate::getCreatedAt));
    }

    @Override
    public boolean save(NotificationTemplate entity) {
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        return super.save(entity);
    }

    @Override
    public boolean updateById(NotificationTemplate entity) {
        entity.setUpdatedAt(LocalDateTime.now());
        return super.updateById(entity);
    }
}
