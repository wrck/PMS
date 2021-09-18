package com.dp.plat.core.service;

import java.util.List;

import com.dp.plat.core.pojo.SysLog;
import com.dp.plat.core.vo.PageParam;

public interface ISysLogService {
	int deleteByPrimaryKey(Integer id);

    int insert(SysLog record);

    int insertSelective(SysLog record);

    SysLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysLog record);

    int updateByPrimaryKey(SysLog record);
    
    List<SysLog> selectBySelective(PageParam<SysLog> pageParam);

	long countBySelective(PageParam<SysLog> pageParam);
}
