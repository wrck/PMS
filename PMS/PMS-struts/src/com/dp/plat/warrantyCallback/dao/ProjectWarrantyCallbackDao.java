package com.dp.plat.warrantyCallback.dao;

import com.dp.plat.warrantyCallback.entity.ProjectWarrantyCallback;

public interface ProjectWarrantyCallbackDao {

    int deleteProjectWarrantyCallbackById(Integer id);

    Integer insertProjectWarrantyCallback(ProjectWarrantyCallback record);

    Integer insertProjectWarrantyCallbackSelective(ProjectWarrantyCallback record);

    ProjectWarrantyCallback selectProjectWarrantyCallbackById(Integer id);

    int updateProjectWarrantyCallbackByIdSelective(ProjectWarrantyCallback record);

    int updateProjectWarrantyCallbackById(ProjectWarrantyCallback record);
}
