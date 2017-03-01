/**   
* @Title: TreeUser.java 
* @Package com.lanxum.smscenter.entity 
* @author Li.xin @ 中电和讯
* @date 2015-11-27 下午4:19:16 
*/
package zhwx.common.model;

import java.util.List;


/**   
 * @Title: TreeUser.java 
 * @Package com.lanxum.smscenter.entity 
 * @author Li.xin @ 中电和讯
 * @date 2015-11-27 下午4:19:16 
 */
public class TreeNode {
	private String id;
	private String parentId;
	private String name;
	private List<TreeUser> userList;
	public TreeNode() {
		super();
	}
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
	public List<TreeUser> getUserList() {
		return userList;
	}
	public void setUserList(List<TreeUser> userList) {
		this.userList = userList;
	}
}
