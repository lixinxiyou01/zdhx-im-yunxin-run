/**   
* @Title: TreeUser.java 
* @Package com.lanxum.smscenter.entity 
* @author Li.xin @ 中电和讯
* @date 2015-11-27 下午4:19:16 
*/
package zhwx.common.model;


/**   
 * @Title: TreeUser.java 
 * @Package com.lanxum.smscenter.entity 
 * @author Li.xin @ 中电和讯
 * @date 2015-11-27 下午4:19:16 
 */
public class TreeUser {
	private String id;
	private String mobileNum;
	private String name;
	private String accId;
	private String headPortraitUrl;
	private String v3Id;
	public TreeUser() {
		super();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMobileNum() {
		return mobileNum;
	}
	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getAccId() {
		return accId;
	}

	public void setAccId(String accId) {
		this.accId = accId;
	}

	public String getHeadPortraitUrl() {
		return headPortraitUrl;
	}

	public void setHeadPortraitUrl(String headPortraitUrl) {
		this.headPortraitUrl = headPortraitUrl;
	}
	public String getV3Id() {
		return v3Id;
	}

	public void setV3Id(String v3Id) {
		this.v3Id = v3Id;
	}

}
