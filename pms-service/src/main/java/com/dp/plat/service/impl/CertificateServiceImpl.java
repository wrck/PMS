package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.PmsCertificateMapper;
import com.dp.plat.model.entity.PmsCertificate;
import com.dp.plat.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class CertificateServiceImpl implements CertificateService {
    @Autowired
    private PmsCertificateMapper mapper;

    @Override
    public IPage<PmsCertificate> queryPage(Integer pageNum, Integer pageSize, String barcode) {
        Page<PmsCertificate> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsCertificate> w = new LambdaQueryWrapper<>();
        w.like(barcode != null, PmsCertificate::getBarcode, barcode)
         .orderByDesc(PmsCertificate::getCreateTime);
        return mapper.selectPage(page, w);
    }

    @Override
    public PmsCertificate getByBarcode(String barcode) {
        return mapper.selectOne(new LambdaQueryWrapper<PmsCertificate>().eq(PmsCertificate::getBarcode, barcode));
    }

    @Override
    @Transactional
    public void create(PmsCertificate c) {
        c.setCreateTime(LocalDateTime.now());
        mapper.insert(c);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        mapper.deleteById(id);
    }
}
