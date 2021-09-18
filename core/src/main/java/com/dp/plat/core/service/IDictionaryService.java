package com.dp.plat.core.service;

import java.util.List;

import com.dp.plat.core.pojo.Dictionary;
import com.dp.plat.core.vo.PageParam;

public interface IDictionaryService {
	int deleteByPrimaryKey(Integer id);

	int insert(Dictionary record);

	int insertSelective(Dictionary record);

	Dictionary selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(Dictionary record);

	int updateByPrimaryKey(Dictionary record);

	List<Dictionary> selectBySelective(PageParam<Dictionary> pageParam);

	long countBySelective(PageParam<Dictionary> pageParam);
	
	List<Dictionary> selectByDicTypeId(int dicTypeId);
}
