package com.dp.plat.pms.springmvc.dao;

import com.dp.plat.core.dao.AbstractBaseMapper;
import java.util.List;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.vo.DispatchVO;
import com.dp.plat.pms.springmvc.entity.DispatchProject;

public interface DispatchProjectMapper extends AbstractBaseMapper<DispatchProject> {

    int updateByPrimaryKeyWithBLOBs(DispatchProject record);

    void insertOrUpdateSelective(DispatchProject dispatch);

    DispatchVO selectDispatchVOWithAmountById(DispatchVO dispatchVO);

    List<DispatchVO> selectDispatchVOWithAmountBySelective(DispatchVO dispatchProject);

    List<DispatchVO> selectDispatchVOWithAmountBySelectivePageable(PageParam<Object> pageParam);
}
