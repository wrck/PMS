/**
 * 
 */
package com.dp.plat.core.service;

import java.util.List;

import com.dp.plat.core.pojo.Menu;
import com.dp.plat.core.vo.TreeNode;

/**
 * @author w02611
 *
 */
public interface IMenuService {
	int deleteByPrimaryKey(Integer id);

	int insert(Menu record);

	int insertSelective(Menu record);

	Menu selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(Menu record);

	int updateByPrimaryKey(Menu record);

	List<Menu> selectAll();

	List<Menu> selectBySelective(Menu menu);

	List<TreeNode> getTreeData();
}
