package com.dp.plat.core.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dp.plat.core.pojo.Dictionary;
import com.dp.plat.core.vo.PageParam;

public interface DictionaryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Dictionary record);

    int insertSelective(Dictionary record);

    Dictionary selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Dictionary record);

    int updateByPrimaryKey(Dictionary record);
    
    long countBySelective(PageParam<Dictionary> pageParam);
    
    List<Dictionary> selectBySelective(PageParam<Dictionary> pageParam);
    
    Integer selectDicTypeIdByDicTypeName(@Param("dicTypeName") String dicTypeName);
    
    int selectMaxDicTypeId();
    
    List<Dictionary> selectByDicTypeId(int dicTypeId);
}