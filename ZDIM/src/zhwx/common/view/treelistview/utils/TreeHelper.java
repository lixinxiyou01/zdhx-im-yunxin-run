package zhwx.common.view.treelistview.utils;

import com.netease.nim.demo.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import zhwx.common.view.treelistview.utils.annotating.TreeNodeHeadPortraitUrl;
import zhwx.common.view.treelistview.utils.annotating.TreeNodeId;
import zhwx.common.view.treelistview.utils.annotating.TreeNodeLabel;
import zhwx.common.view.treelistview.utils.annotating.TreeNodeLeafSize;
import zhwx.common.view.treelistview.utils.annotating.TreeNodePid;
import zhwx.common.view.treelistview.utils.annotating.TreeNodeTerminalId;

public class TreeHelper {

	/**
	 * 将用户的数据转化为树形数据
	 * @return
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public static <T> List<Node> convertDates2Nodes(List<T> datas) throws IllegalAccessException, IllegalArgumentException{
		List<Node> nodes = new ArrayList<Node>();
		Node node = null;
		for (T t:datas) {
			int id = -1;
			int pid = -1;
			String label = null;
			String terminalId = null;  //聊天id
			String treeNodeHeadPortraitUrl = null; //头像
			String leafSize = null; //
			node = new Node();
			/**
			 * 由于用户定义的变量名不固定
			 * 使用反射+注解获取变量
			 */
			Class clazz = t.getClass();
			Field[] fields = clazz.getDeclaredFields();
			for (Field field:fields) {
				if(field.getAnnotation(TreeNodeId.class) != null){
					field.setAccessible(true);
					id = field.getInt(t);
				};
				if(field.getAnnotation(TreeNodePid.class) != null){
					field.setAccessible(true);
					pid = field.getInt(t);
				};
				if(field.getAnnotation(TreeNodeLabel.class) != null){
					field.setAccessible(true);
					label = (String) field.get(t);
				};
				if(field.getAnnotation(TreeNodeTerminalId.class) != null){
					field.setAccessible(true);
					terminalId = (String) field.get(t);
				};
				if(field.getAnnotation(TreeNodeHeadPortraitUrl.class) != null){
					field.setAccessible(true);
					treeNodeHeadPortraitUrl = (String) field.get(t);
				};
				if(field.getAnnotation(TreeNodeLeafSize.class) != null){
					field.setAccessible(true);
					leafSize = (String) field.get(t);
				};
			}
			node = new Node(id, pid, label,terminalId,treeNodeHeadPortraitUrl,leafSize);
			nodes.add(node);
		}
		
		/**
		 * 设置Node间的节点关系
		 */
		for (int i = 0; i < nodes.size(); i++) {
			Node n = nodes.get(i);
			for (int j = i+1; j < nodes.size(); j++) {
				Node m = nodes.get(j);
				if(m.getpId() == n.getId()){
					n.getChildren().add(m);
					m.setParent(n);
				}else if(m.getId() == n.getpId()){
					m.getChildren().add(n);
					n.setParent(m);
				}
			}
		}
		
		/**
		 * 设置箭头图标
		 */
		for (Node n:nodes) {
			setNodeIcon(n);
		}
		return nodes;
	}
	
	/**
	 * 对所有节点进行排序
	 * @param datas
	 * @param defaultExpandLevel 默认展开层数
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static <T> List<Node> getSortedNodes(List<T> datas, int defaultExpandLevel) throws IllegalAccessException, IllegalArgumentException {
		List<Node> result = new ArrayList<Node>();
		List<Node> nodes = convertDates2Nodes(datas);
		//获取树的根节点
		List<Node> rootNodes = getRootNodes(nodes);
		for (Node node:rootNodes) {
			addNode(result, node, defaultExpandLevel, 1);
		}
		return result;
	}

	/**
	 * 把一个节点的所有孩子节点都放入result
	 * @param result
	 * @param node
	 * @param defaultExpandLevel 默认展开层数
	 */
	private static void addNode(List<Node> result, Node node,
			int defaultExpandLevel, int currentLevel) {
		result.add(node);
		if(defaultExpandLevel >= currentLevel){
			node.setExpand(true);
		}
		if(node.isLeaf()){
			return;
		}
		for (int i = 0; i < node.getChildren().size(); i++) {
			addNode(result, node.getChildren().get(i), defaultExpandLevel, currentLevel+1);
		}
	}
	
	/**
	 * 过滤出可见节点
	 * @param nodes
	 * @return
	 */
	public static List<Node> filterVisibelNodes(List<Node> nodes) {
		List<Node> result = new ArrayList<Node>();
		for (Node node:nodes) {
			if(node.isRoot() || node.isParentExpand()){
				setNodeIcon(node);
				result.add(node);
			}
		}
		return result;
	}

	/**
	 * 从所有节点中获取根节点
	 * @param nodes
	 * @return
	 */
	private static List<Node> getRootNodes(List<Node> nodes) {
		List<Node> root = new ArrayList<Node>();
		for (Node node:nodes) {
			if(node.isRoot()){
				root.add(node);
			}
		}
		return root;
	}

	/**
	 * 为Node设置图标
	 * @param n
	 */
	private static void setNodeIcon(Node n) {
		if(n.getChildren().size()>0 && n.isExpand()){
			n.setIcon(R.drawable.xiajt);
		}else if(n.getChildren().size()>0 && !n.isExpand()){
			n.setIcon(R.drawable.youjt);
		}else{
			n.setIcon(-1);
		}
	}
}
