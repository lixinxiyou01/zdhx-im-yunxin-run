package zhwx.ui.contact;
/**
 * 查询好友 后的结果
* @Description: TODO
* @Title: SearchFriendResult.java 
* @Package com.bj.android.hzth.parentcircle.domain 
* @author 容联•云通讯 Modify By Li.Xin @ 中电和讯
* @date 2014-11-27 下午2:11:18
 */
public class SearchFriendResult {
	private String id;
	private String headPortraitUrl;
	private String voipAccount;
	private String name;
	private String accId;
	public SearchFriendResult() {
		super();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getHeadPortraitUrl() {
		return headPortraitUrl;
	}
	public void setHeadPortraitUrl(String headPortraitUrl) {
		this.headPortraitUrl = headPortraitUrl;
	}
	public String getVoipAccount() {
		return voipAccount;
	}
	public void setVoipAccount(String voipAccount) {
		this.voipAccount = voipAccount;
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
}
