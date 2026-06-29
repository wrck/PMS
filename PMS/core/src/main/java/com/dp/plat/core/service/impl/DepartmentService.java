package com.dp.plat.core.service.impl;

import org.springframework.stereotype.Service;
import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.service.IDepartmentService;

import com.dp.plat.core.pojo.Department;
import com.dp.plat.core.dao.DepartmentMapper;

/**
 *
 * Created by CodeGenerator
 */
@Service("departmentService")
public class DepartmentService extends AbstractBaseService<DepartmentMapper, Department> implements IDepartmentService {
}