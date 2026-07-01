package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.model.entity.PmsCertificate;

public interface CertificateService {
    IPage<PmsCertificate> queryPage(Integer pageNum, Integer pageSize, String barcode);

    PmsCertificate getByBarcode(String barcode);

    void create(PmsCertificate certificate);

    void update(PmsCertificate certificate);

    void delete(Long id);

    /** 上传印章信息 - 迁移自 CertificateAction.uploadSealInfo() */
    void uploadSealInfo(Long id, String sealInfo);
}
