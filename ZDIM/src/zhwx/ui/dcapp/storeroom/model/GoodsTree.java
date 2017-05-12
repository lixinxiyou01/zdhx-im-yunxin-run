package zhwx.ui.dcapp.storeroom.model;

import java.util.List;

/**   
 * @Title: GoodsTree.java 
 * @Package com.zdhx.edu.im.ui.v3.storeroom.model
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ zdhx
 * @date 2017年1月14日 下午3:15:05 
 */
public class GoodsTree {
	private String id;
	private String parentId;
	private String name;
	private List<GoodsNode> nodes;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<GoodsNode> getNodes() {
		return nodes;
	}
	public void setNodes(List<GoodsNode> nodes) {
		this.nodes = nodes;
	}
	
	
	public class GoodsNode{
		private String id;
		private String parentId;
		private String name;
		private List<GoodsNode> nodes;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getParentId() {
			return parentId;
		}
		public void setParentId(String parentId) {
			this.parentId = parentId;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public List<GoodsNode> getNodes() {
			return nodes;
		}
		public void setNodes(List<GoodsNode> nodes) {
			this.nodes = nodes;
		}
	}
}
