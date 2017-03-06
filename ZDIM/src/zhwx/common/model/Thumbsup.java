package zhwx.common.model;

public class Thumbsup{
	private String userId;
	private String userName;
	public Thumbsup() {
		super();
	}
	
	public Thumbsup(String userName) {
		super();
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
}