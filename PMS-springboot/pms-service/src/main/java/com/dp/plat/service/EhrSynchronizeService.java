package com.dp.plat.service;

import com.dp.plat.model.entity.Employee;
import java.util.List;

/**
 * EHR同步服务 - 负责从EHR同步员工数据
 */
public interface EhrSynchronizeService extends BaseService<Employee> {
    void syncEmployeeFromEHR();
}
