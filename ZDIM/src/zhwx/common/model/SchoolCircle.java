package zhwx.common.model;

import java.util.List;

public class SchoolCircle {
	private String content;
	private String headPortraitUrl;
	private String id;
	private List<PicUrl> picUrls;
	private String publishTime;
	private String time;
	private String userId;
	private String userName;
	private String commentCount;
	private String isThumbsup = "false";
	private String thumbsupCount;
	private String thumbsupId;
	private List<Comment> comments;
	private List<Thumbsup> thumbsups;
	
	
	public SchoolCircle() {
		super();
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getHeadPortraitUrl() {
		return headPortraitUrl;
	}
	public void setHeadPortraitUrl(String headPortraitUrl) {
		this.headPortraitUrl = headPortraitUrl;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<PicUrl> getPicUrls() {
		return picUrls;
	}
	public void setPicUrls(List<PicUrl> picUrls) {
		this.picUrls = picUrls;
	}
	public String getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
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
	public String getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(String commentCount) {
		this.commentCount = commentCount;
	}
	public String getIsThumbsup() {
		return isThumbsup;
	}
	public void setIsThumbsup(String isThumbsup) {
		this.isThumbsup = isThumbsup;
	}
	public String getThumbsupCount() {
		return thumbsupCount;
	}
	public void setThumbsupCount(String thumbsupCount) {
		this.thumbsupCount = thumbsupCount;
	}
	public String getThumbsupId() {
		return thumbsupId;
	}
	public void setThumbsupId(String thumbsupId) {
		this.thumbsupId = thumbsupId;
	}
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	public List<Thumbsup> getThumbsups() {
		return thumbsups;
	}
	public void setThumbsups(List<Thumbsup> thumbsups) {
		this.thumbsups = thumbsups;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}
