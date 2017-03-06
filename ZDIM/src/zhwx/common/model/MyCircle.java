package zhwx.common.model;

import java.util.List;

public class MyCircle {
	private String departmentId;
	private String headPortraitUrl;
	private String name;
	private String userId;
	private String voipAccount;
	private List<Moment> moments;
	public MyCircle() {
		super();
	}
	public String getHeadPortraitUrl() {
		return headPortraitUrl;
	}
	public void setHeadPortraitUrl(String headPortraitUrl) {
		this.headPortraitUrl = headPortraitUrl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public List<Moment> getMoments() {
		return moments;
	}
	public void setMoments(List<Moment> moments) {
		this.moments = moments;
	}
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	public String getVoipAccount() {
		return voipAccount;
	}
	public void setVoipAccount(String voipAccount) {
		this.voipAccount = voipAccount;
	}
}
