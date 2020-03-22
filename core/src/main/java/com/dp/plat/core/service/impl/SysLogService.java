package com.dp.plat.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dp.plat.core.dao.SysLogMapper;
import com.dp.plat.core.pojo.SysLog;
import com.dp.plat.core.service.ISysLogService;
import com.dp.plat.core.vo.PageParam;

@Service("sysLogService")
public class SysLogService implements ISysLogService {
	@Resource
	private SysLogMapper sysLogMapper;
	@Override
	public int deleteByPrimaryKey(Integer id) {
		return sysLogMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(SysLog record) {
		return sysLogMapper.insert(record);
	}

	@Override
	public int insertSelective(SysLog record) {
		return sysLogMapper.insert(record);
	}

	@Override
	public SysLog selectByPrimaryKey(Integer id) {
		return sysLogMapper.selectByPrimaryKey(id);
	}

	@Override
	public int updateByPrimaryKeySelective(SysLog record) {
		return sysLogMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(SysLog record) {
		return sysLogMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<SysLog> selectBySelective(PageParam<SysLog> pageParam) {
		return sysLogMapper.selectBySelective(pageParam);
	}

	@Override
	public long countBySelective(PageParam<SysLog> pageParam) {
		return sysLogMapper.countBySelective(pageParam);
	}

}
