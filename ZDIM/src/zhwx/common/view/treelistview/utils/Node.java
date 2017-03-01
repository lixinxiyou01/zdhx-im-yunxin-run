package zhwx.common.view.treelistview.utils;

import java.util.ArrayList;
import java.util.List;

public class Node {
	private int id;	
	private int pId;		//根节点的pid=0
	private String name;	//用于显示
	private int level;		//树的层级
	private boolean isExpand = false;//是否是展开
	private int icon;		//图片
	private Node parent;	//父节点
	private String contactId;  //voip
	private String headPortraitUrl;
	private String leafSize;
	private List<Node> children = new ArrayList<Node>();//子节点
	
	public Node(){
		
	}
	
	public Node(int id, int pId, String name) {
		this.id = id;
		this.pId = pId;
		this.name = name;
	}
	
	public Node(int id, int pId, String name, String contactId,String headPortraitUrl) {
		super();
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.contactId = contactId;
		this.headPortraitUrl = headPortraitUrl;
	}
	
	public Node(int id, int pId, String name, String terminalId,
			String headPortraitUrl, String leafSize) {
		super();
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.contactId = terminalId;
		this.headPortraitUrl = headPortraitUrl;
		this.leafSize = leafSize;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getpId() {
		return pId;
	}
	public void setpId(int pId) {
		this.pId = pId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIcon() {
		return icon;
	}
	public void setIcon(int icon) {
		this.icon = icon;
	}
	public Node getParent() {
		return parent;
	}
	public void setParent(Node parent) {
		this.parent = parent;
	}
	public List<Node> getChildren() {
		return children;
	}
	public void setChildren(List<Node> children) {
		this.children = children;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getHeadPortraitUrl() {
		return headPortraitUrl;
	}

	public void setHeadPortraitUrl(String headPortraitUrl) {
		this.headPortraitUrl = headPortraitUrl;
	}

	/**
	 * 判断是否是根节点
	 * @return
	 */
	public boolean isRoot(){
		return parent == null;
	}
	
	/**
	 * 判断父节点是否展开
	 * @return
	 */
	public boolean isParentExpand(){
		if(parent == null){
			return false;
		}
		return parent.isExpand();
	}
	
	/**
	 * 是否是叶子节点
	 * @return
	 */
	public boolean isLeaf(){
		return children.size() == 0;
	}
	
	/**
	 * 得到当前节点的层级
	 * @return
	 */
	public int getLevel() {
		return parent == null ? 0 : parent.getLevel() + 1;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
	public boolean isExpand() {
		return isExpand;
	}
	public void setExpand(boolean isExpand) {
		this.isExpand = isExpand;
		if (!isExpand) {
			for (Node node:children) {
				node.setExpand(false);
			}
		}
	}
	public String getLeafSize() {
		return leafSize;
	}

	public void setLeafSize(String leafSize) {
		this.leafSize = leafSize;
	}
}
