package com.dp.plat.core.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.dp.plat.core.pojo.Menu;
import com.dp.plat.core.vo.TreeNode;
import com.dp.plat.core.vo.TreeNodeState;
/**
 * 只能支持二级菜单
 * @author j01441
 *
 */
public class MenuUtil {
	
	private static String contextPath = "";

	public static String drow(List<Menu> nodes, String path) {
		if (StringUtils.isNotBlank(path)) {
			contextPath = path;
		}
		// 获取项目绝对路径
		Map<Integer, List<Menu>> treeMenu = constructParentMenu(nodes);
		StringBuilder nodestr = new StringBuilder();
		nodestr.append("<ul class=\"sidebar-menu\">");
		
		if (!treeMenu.isEmpty()) {
			for (Menu pearentMenu : treeMenu.get(0)) {
				nodestr.append(drowPenu(pearentMenu, treeMenu));
			}
		}
		nodestr.append("</ul>");
		return nodestr.toString();
	}

	/**
	 * 根据菜单构造成bootrStrap-treeview 组件能够识别的树形结构
	 * @param menus
	 * @return
	 */
	public static List<TreeNode> constructTreeNodeData(List<Menu> menus, Map<Integer, TreeNodeState> stateMap) {
		Map<String, TreeNode> nodelist = new LinkedHashMap<String, TreeNode>();
		List<TreeNode> tnlist = new ArrayList<TreeNode>();
		for (Menu menu : menus) {
			TreeNode node = new TreeNode();
			node.setText(menu.getName());
			node.setId(menu.getId());
			node.setParentId(menu.getPid().toString());
			node.setIcon(menu.getIcon());
			node.setSort(menu.getSort());
			node.setStatus(menu.getStatus());
			
			if (stateMap != null) {
				node.setState(stateMap.get(menu.getId()));
			}
			
			nodelist.put(node.getId().toString(), node);
		}
		// 构造树形结构
		for (String id : nodelist.keySet()) {
			TreeNode node = nodelist.get(id);
			String parentId = node.getParentId();
			if (StringUtils.isBlank(parentId) || "0".equals(parentId)) {
				tnlist.add(node);
			} else {
				if (nodelist.get(parentId).getNodes() == null)
					nodelist.get(parentId).setNodes(new ArrayList<TreeNode>());
				nodelist.get(parentId).getNodes().add(node);
			}
		}
		return tnlist;
	}
	
	/**
	 * 有子菜单
	 * @param menu
	 * @param nodes
	 * @deprecated 用drowPenu(Menu menu, Map＜Integer, List＜Menu＞＞ treeMenu)替代
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private static String drowPenu(Menu menu, List<Menu> nodes) {
		StringBuilder nodestr = new StringBuilder();
		nodestr.append("<li class=\"treeview\">");
		nodestr.append("<a href=\"#\"> <i class=\""+menu.getIcon()+"\"></i> <span class='menu-name'>"+menu.getName()+"</span> ");
		nodestr.append("<span class=\"pull-right-container\"> <i class=\"fa fa-angle-left pull-right\"></i></span>");
		nodestr.append("</a>");
		for(int i = 0;i < nodes.size(); i++){
			if(nodes.get(i).getPid() == menu.getId()){
				nodestr.append("<li>");
				nodestr.append("<a href=\""+contextPath+nodes.get(i).getUrl()+"\"><i class=\""+nodes.get(i).getIcon()+"\"></i> <span class='menu-name'>"+nodes.get(i).getName()+"</span></a>");
				nodestr.append("</li>");
			}
		}
		nodestr.append("</li>");
		return nodestr.toString();
	}

	/**
	 * 一级菜单
	 * @param pearentMenu
	 * @param treeMenu
	 * @return
	 */
	private static Object drowPenu(Menu menu, Map<Integer, List<Menu>> treeMenu) {
		StringBuilder nodestr = new StringBuilder();
		nodestr.append("<li class=\"treeview\">");
		nodestr.append("<a href=\"#\"> <i class=\""+menu.getIcon()+"\"></i> <span class='menu-name'>"+menu.getName()+"</span> ");
		
		nodestr.append(drowChildMenu(menu.getId(), treeMenu));
		
		nodestr.append("</li>");
		return nodestr.toString();
	}
	
	/**
	 * 子菜单
	 * @param pid
	 * @param nodes
	 */
	private static String drowChildMenu(Integer pid, Map<Integer, List<Menu>> treeMenu) {
		List<Menu> nodes = treeMenu.get(pid);
		if(nodes == null){
			return "</a>";
		}
		StringBuilder nodestr = new StringBuilder();
		// 父菜单的展开、收缩图表
		nodestr.append("<span class=\"pull-right-container\"> <i class=\"fa fa-angle-left pull-right\"></i></span>");
		nodestr.append("</a>");
		// 子菜单
		nodestr.append("<ul class='treeview-menu'>");
		for (Menu menu : nodes) {
			nodestr.append("<li>");
			nodestr.append("<a href='" + contextPath + menu.getUrl() + "'><i class='" + menu.getIcon() + "'></i> <span class='menu-name'>" + menu.getName() + "</span>");

			nodestr.append(drowChildMenu(menu.getId(), treeMenu));
			
			nodestr.append("</li>");
		}
		nodestr.append("</ul>");
		return nodestr.toString();
	}
	
	/**
	 * 构造Map<父节点pid,子节点list>
	 * @param nodes
	 * @return Map
	 */
	private static Map<Integer, List<Menu>> constructParentMenu(List<Menu> nodes){
		Map<Integer, List<Menu>> nodelist = new LinkedHashMap<Integer, List<Menu>>();
		for (Menu menu : nodes) {
			List<Menu> temp = nodelist.get(menu.getPid());
			if (temp == null) {
				temp = new ArrayList<Menu>();
			}
			temp.add(menu);
			nodelist.put(menu.getPid(), temp);
		}
		return nodelist;
	}
	
}
