package com.dp.plat.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dp.plat.core.dao.MenuMapper;
import com.dp.plat.core.pojo.Menu;
import com.dp.plat.core.service.IMenuService;
import com.dp.plat.core.util.MenuUtil;
import com.dp.plat.core.vo.TreeNode;

@Service("menuService")
public class MenuService implements IMenuService {

	@Resource
	private MenuMapper menuMapper;

	@Override
	public int updateByPrimaryKeySelective(Menu menu) {
		return menuMapper.updateByPrimaryKeySelective(menu);
	}

	@Override
	public int deleteByPrimaryKey(Integer userId) {
		return menuMapper.deleteByPrimaryKey(userId);
	}

	@Override
	public int insert(Menu record) {
		return menuMapper.insert(record);
	}

	@Override
	public int insertSelective(Menu record) {
		return menuMapper.insertSelective(record);
	}

	public Menu selectByPrimaryKey(Integer userId) {
		return menuMapper.selectByPrimaryKey(userId);
	}

	@Override
	public int updateByPrimaryKey(Menu record) {
		return menuMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<Menu> selectAll() {
		return menuMapper.selectAll();
	}

	public List<Menu> selectBySelective(Menu menu) {
		return menuMapper.selectBySelective(menu);
	}

	@Override
	public List<TreeNode> getTreeData() {
		List<Menu> menus = menuMapper.selectBySelective(null);
		return MenuUtil.constructTreeNodeData(menus, null);
	
		/*Map<Integer, TreeNode> nodelist = new LinkedHashMap<Integer, TreeNode>();
		List<TreeNode> tnlist = new ArrayList<TreeNode>();
		for (Menu menu : menus) {
			TreeNode node = new TreeNode();
			node.setText(menu.getName());
			node.setId(menu.getId());
			node.setParentId(menu.getPid());
			node.setIcon(menu.getIcon());
			node.setSort(menu.getSort());
			node.setStatus(menu.getStatus());
			
			TreeNodeState state = new TreeNodeState();
//			state.setDisabled(menu.getStatus());
			//state.setChecked(menu.getStatus());
			//state.setExpanded(menu.getStatus());
			//state.setSelected(menu.getStatus());
			node.setState(state);
			
			nodelist.put(node.getId(), node);
		}
		// 构造树形结构
		for (Integer id : nodelist.keySet()) {
			TreeNode node = nodelist.get(id);
			Integer parentId = node.getParentId();
			if (parentId != null && parentId == 0) {
				tnlist.add(node);
			} else {
				if (nodelist.get(parentId).getNodes() == null)
					nodelist.get(parentId).setNodes(new ArrayList<TreeNode>());
				nodelist.get(parentId).getNodes().add(node);
			}
		}
		return tnlist;*/
	}
}
