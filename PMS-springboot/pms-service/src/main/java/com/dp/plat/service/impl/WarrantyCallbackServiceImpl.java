package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.utils.SecurityUtil;
import com.dp.plat.mapper.PmsWarrantyCallbackMapper;
import com.dp.plat.model.entity.PmsWarrantyCallback;
import com.dp.plat.service.WarrantyCallbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WarrantyCallbackServiceImpl implements WarrantyCallbackService {

    @Autowired
    private PmsWarrantyCallbackMapper callbackMapper;

    @Override
    public IPage<PmsWarrantyCallback> queryPage(Integer pageNum, Integer pageSize,
                                                  Long projectId, String officeCode) {
        Page<PmsWarrantyCallback> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsWarrantyCallback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(projectId != null, PmsWarrantyCallback::getProjectId, projectId)
               .eq(StringUtils.hasText(officeCode), PmsWarrantyCallback::getOfficeCode, officeCode)
               .eq(PmsWarrantyCallback::getIsDelete, 0)
               .orderByDesc(PmsWarrantyCallback::getCreateTime);
        return callbackMapper.selectPage(page, wrapper);
    }

    @Override
    public PmsWarrantyCallback getDetail(Long id) {
        PmsWarrantyCallback cb = callbackMapper.selectById(id);
        if (cb == null) {
            throw new BusinessException("质保回访记录不存在");
        }
        return cb;
    }

    @Override
    @Transactional
    public void create(PmsWarrantyCallback callback) {
        callback.setIsDelete(0);
        callback.setCreateBy(SecurityUtil.getCurrentUsername());
        callback.setCreateTime(LocalDateTime.now());
        callbackMapper.insert(callback);
    }

    @Override
    @Transactional
    public void update(PmsWarrantyCallback callback) {
        callback.setUpdateBy(SecurityUtil.getCurrentUsername());
        callback.setUpdateTime(LocalDateTime.now());
        callbackMapper.updateById(callback);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        PmsWarrantyCallback cb = callbackMapper.selectById(id);
        if (cb == null) {
            throw new BusinessException("质保回访记录不存在");
        }
        cb.setIsDelete(1);
        cb.setUpdateBy(SecurityUtil.getCurrentUsername());
        cb.setUpdateTime(LocalDateTime.now());
        callbackMapper.updateById(cb);
    }

    @Override
    public List<PmsWarrantyCallback> queryByProject(Long projectId) {
        return callbackMapper.selectList(
                new LambdaQueryWrapper<PmsWarrantyCallback>()
                        .eq(PmsWarrantyCallback::getProjectId, projectId)
                        .eq(PmsWarrantyCallback::getIsDelete, 0)
                        .orderByDesc(PmsWarrantyCallback::getCreateTime));
    }

    @Override
    public List<PmsWarrantyCallback> queryCustomerProject(String customerName) {
        return callbackMapper.selectList(
                new LambdaQueryWrapper<PmsWarrantyCallback>()
                        .like(StringUtils.hasText(customerName), PmsWarrantyCallback::getFinalCustomerName, customerName)
                        .eq(PmsWarrantyCallback::getIsDelete, 0)
                        .orderByDesc(PmsWarrantyCallback::getCreateTime));
    }
}
