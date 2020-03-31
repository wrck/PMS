package com.dp.plat.pms.springmvc.service;

import com.dp.plat.pms.springmvc.vo.DispatchVO;
import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.pms.springmvc.entity.DispatchProject;

/**
 *
 * Created by CodeGenerator
 */
public interface IDispatchProjectService extends IAbstractBaseService<DispatchProject> {

    void dispatchSubmit(Integer id, DispatchVO dispatch);

    void insertOrUpdateSelective(DispatchProject dispatch);
}
