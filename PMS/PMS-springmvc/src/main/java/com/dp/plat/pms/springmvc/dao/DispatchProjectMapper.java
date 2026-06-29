package com.dp.plat.pms.springmvc.dao;

import java.util.List;

import com.dp.plat.core.dao.AbstractBaseMapper;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.entity.DispatchProject;
import com.dp.plat.pms.springmvc.vo.DispatchVO;

public interface DispatchProjectMapper extends AbstractBaseMapper<DispatchProject> {

    void insertOrUpdateSelective(DispatchProject dispatch);

    List<DispatchVO> selectDispatchProjectVOList(DispatchVO dispatch);
    
    Long countDispatchProjectVOList(DispatchVO dispatch);

    DispatchVO selectDispatchVOWithAmountById(DispatchVO dispatchVO);

    List<DispatchVO> selectDispatchVOWithAmountBySelective(DispatchVO dispatchProject);

    List<DispatchVO> selectDispatchVOWithAmountBySelectivePageable(PageParam<Object> pageParam);

}
