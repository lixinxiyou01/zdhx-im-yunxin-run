 package zhwx.common.model;

public class Reply {
	private String content;
	private String replyUserId;
	private String replyUserName;
	private String targetUserId;
	private String targetUserName;
	private String id;
	public Reply() {
		super();
	}
	
	public Reply(String content, String replyUserId, String replyUserName,
			String targetUserName,String id,String targetUserId) {
		super();
		this.content = content;
		this.replyUserId = replyUserId;
		this.replyUserName = replyUserName;
		this.targetUserName = targetUserName;
		this.id = id;
		this.targetUserId = targetUserId;
	}

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getReplyUserId() {
		return replyUserId;
	}
	public void setReplyUserId(String replyUserId) {
		this.replyUserId = replyUserId;
	}
	public String getReplyUserName() {
		return replyUserName;
	}
	public void setReplyUserName(String replyUserName) {
		this.replyUserName = replyUserName;
	}
	public String getTargetUserId() {
		return targetUserId;
	}
	public void setTargetUserId(String targetUserId) {
		this.targetUserId = targetUserId;
	}
	public String getTargetUserName() {
		return targetUserName;
	}
	public void setTargetUserName(String targetUserName) {
		this.targetUserName = targetUserName;
	}
}
