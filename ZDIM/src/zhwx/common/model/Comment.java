package zhwx.common.model;

import java.util.List;

public class Comment{
	private String content;
	private String userId;
	private String userName;
	private String time;
	private List<Reply> replys;
	private String id;
	public Comment() {
		super();
	}
	

	public Comment(String content, String userId, String userName, String id) {
		super();
		this.content = content;
		this.userId = userId;
		this.userName = userName;
		this.id = id;
	}

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Reply> getReplys() {
		return replys;
	}

	public void setReplys(List<Reply> replys) {
		this.replys = replys;
	}
}
