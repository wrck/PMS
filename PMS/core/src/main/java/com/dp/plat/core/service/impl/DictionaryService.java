package com.dp.plat.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dp.plat.core.annotation.SystemServiceLog;
import com.dp.plat.core.dao.DictionaryMapper;
import com.dp.plat.core.pojo.Dictionary;
import com.dp.plat.core.service.IDictionaryService;
import com.dp.plat.core.vo.PageParam;

@Service("dictionaryService")
public class DictionaryService implements IDictionaryService {
	@Resource
	private DictionaryMapper dictionaryMapper;

	@Override
	@SystemServiceLog(description = "查询字典信息")
	public List<Dictionary> selectBySelective(PageParam<Dictionary> pageParam) {
		return dictionaryMapper.selectBySelective(pageParam);
	}

	@Override
	public long countBySelective(PageParam<Dictionary> pageParam) {
		return dictionaryMapper.countBySelective(pageParam);
	}

	@Override
	public int deleteByPrimaryKey(Integer id) {
		return dictionaryMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(Dictionary record) {
		return dictionaryMapper.insert(record);
	}

	@Override
	public int insertSelective(Dictionary record) {
		if (record.getDicTypeId() == null) {
			Integer dictypeId = dictionaryMapper.selectDicTypeIdByDicTypeName(record.getDicTypeName());
			if (dictypeId != null) {
				record.setDicTypeId(dictypeId);
			} else {
				int maxDicTypeId = dictionaryMapper.selectMaxDicTypeId();
				record.setDicTypeId(maxDicTypeId + 1);
			}
		}
		return dictionaryMapper.insertSelective(record);
	}

	@Override
	public Dictionary selectByPrimaryKey(Integer id) {
		return dictionaryMapper.selectByPrimaryKey(id);
	}

	@Override
	public int updateByPrimaryKeySelective(Dictionary record) {
		return dictionaryMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(Dictionary record) {
		return dictionaryMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<Dictionary> selectByDicTypeId(int dicTypeId) {
		return dictionaryMapper.selectByDicTypeId(dicTypeId);
	}

}
