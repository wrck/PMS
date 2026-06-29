package com.dp.plat.warrantyCallback.dao;

import com.dp.plat.dao.BaseDao;
import com.dp.plat.warrantyCallback.entity.ProjectWarrantyCallback;

public class ProjectWarrantyCallbackDaoImpl extends BaseDao implements ProjectWarrantyCallbackDao {

    public int deleteProjectWarrantyCallbackById(Integer id) {
        ProjectWarrantyCallback _key = new ProjectWarrantyCallback();
        _key.setId(id);
        int rows = getSqlMapClientTemplate().delete("deleteProjectWarrantyCallbackById", _key);
        return rows;
    }

    public Integer insertProjectWarrantyCallback(ProjectWarrantyCallback record) {
        Object newKey = getSqlMapClientTemplate().insert("insertProjectWarrantyCallback", record);
        return (Integer) newKey;
    }

    public Integer insertProjectWarrantyCallbackSelective(ProjectWarrantyCallback record) {
        Object newKey = getSqlMapClientTemplate().insert("insertProjectWarrantyCallbackSelective", record);
        return (Integer) newKey;
    }

    public ProjectWarrantyCallback selectProjectWarrantyCallbackById(Integer id) {
        ProjectWarrantyCallback _key = new ProjectWarrantyCallback();
        _key.setId(id);
        ProjectWarrantyCallback record = (ProjectWarrantyCallback) getSqlMapClientTemplate().queryForObject("selectById", _key);
        return record;
    }

    public int updateProjectWarrantyCallbackByIdSelective(ProjectWarrantyCallback record) {
        int rows = getSqlMapClientTemplate().update("updateProjectWarrantyCallbackByIdSelective", record);
        return rows;
    }

    public int updateProjectWarrantyCallbackById(ProjectWarrantyCallback record) {
        int rows = getSqlMapClientTemplate().update("updateProjectWarrantyCallbackById", record);
        return rows;
    }
}
