/**
 * 
 */
package com.dp.plat.ehr.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.dp.plat.core.vo.TreeNode;
import com.dp.plat.core.vo.TreeNodeState;
import com.dp.plat.ehr.annotation.TreeNodeParam;

/**
 * @author w02611
 *
 */
public class TreeNodeUtils {
	/**
	 * 根据菜单构造成bootrStrap-treeview 组件能够识别的树形结构
	 * 
	 * @param nodeList
	 * @return
	 * @throws @throws
	 *             Exception
	 */
	public static List<TreeNode> constructTreeNodeData(List<?> nodeList, Map<Integer, TreeNodeState> stateMap)
			throws Exception {
		Map<String, TreeNode> nodeMap = new LinkedHashMap<String, TreeNode>();
		List<TreeNode> treeNodeList = new ArrayList<TreeNode>();
		Method method = null;
		for (Object node : nodeList) {
			TreeNode treeNode = new TreeNode();
			Class<?> clazz = node.getClass();
			TreeNodeParam treeNodeParam = clazz.getAnnotation(TreeNodeParam.class);
			if (treeNodeParam == null && clazz.getSuperclass() != null) {
				treeNodeParam = clazz.getSuperclass().getAnnotation(TreeNodeParam.class);
			}
			if (treeNodeParam == null) {
				continue;
			}
			String[] fieldNames = treeNodeParam.fields();
			for (String fieldName : fieldNames) {
				String[] filed = null;
				if (StringUtils.isNotBlank(fieldName)) {
					filed = fieldName.split(":");
				} else {
					continue;
				}
				String[] abbrs = filed[1].split(",");
				Object value = null;
				String flag = node.getClass().getSimpleName();
				for (String abbr : abbrs) {
					String[] relation = abbr.split("-");
					if (relation.length > 1) {
						flag = relation[0];
						abbr = relation[1];
					}
					method = clazz.getMethod("get" + abbr.substring(0, 1).toUpperCase() + abbr.substring(1));
					value = method.invoke(node);
					if ("parentId".equals(filed[0]) && "0".equals(String.valueOf(value))) {
						continue;
					}
					if (value != null ) {
						break;
					}
				}
				if ("parentId".equals(filed[0])) {
					value = flag + ":" + String.valueOf(value);
				}
				if (value != null) {
					method = treeNode.getClass().getMethod("set" + filed[0].substring(0, 1).toUpperCase() + filed[0].substring(1), value.getClass());
					method.invoke(treeNode, value);
				}
			}

			if (stateMap != null) {
				method = clazz.getMethod("getId");
				treeNode.setState(stateMap.get(method.invoke(node)));
			}

			nodeMap.put(node.getClass().getSimpleName() + ":" + String.valueOf(treeNode.getId()), treeNode);
		}

		// 构造树形结构
		for (String id : nodeMap.keySet()) {
			TreeNode node = nodeMap.get(id);
			String parentId = node.getParentId();
			String[] parent = parentId.split(":");
			if (StringUtils.isBlank(parent[1]) || "null".equals(parent[1]) || "0".equals(parent[1])) {
				treeNodeList.add(node);
			} else {
				System.out.println(parentId);
				if (nodeMap.get(parentId) == null) { 
					treeNodeList.add(node);
					continue;
				}
				if (nodeMap.get(parentId).getNodes() == null)
					nodeMap.get(parentId).setNodes(new ArrayList<TreeNode>());
				nodeMap.get(parentId).getNodes().add(node);
			}
		}
		return treeNodeList;
	}
}
