package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.model.entity.PmsWarrantyCallback;

import java.util.List;

public interface WarrantyCallbackService {

    IPage<PmsWarrantyCallback> queryPage(Integer pageNum, Integer pageSize,
                                          Long projectId, String officeCode);

    PmsWarrantyCallback getDetail(Long id);

    void create(PmsWarrantyCallback callback);

    void update(PmsWarrantyCallback callback);

    void delete(Long id);

    /** 查询项目质保信息 */
    List<PmsWarrantyCallback> queryByProject(Long projectId);

    /** 查询客户项目质保统计 */
    List<PmsWarrantyCallback> queryCustomerProject(String customerName);
}
