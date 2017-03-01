package zhwx.ui.imapp.notice.model;

import java.io.Serializable;
import java.util.List;

import zhwx.common.model.Attachment;


/**
 * 消息
 * @author 容联•云通讯 Modify By Li.Xin @ 中电和讯
 * 2014-6-16 上午10:44:53
 */
public class V3Notice implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String noticeId;
	private String sendUser;
	private String kind;			 	 //消息来源    
	private String kindFlag;			 //消息类型编码
	private String attentionFlag = "0";  //是否被关注  0 1
	private String readFlag;			 //是否已读 0 1
	private String title;
	private String content;
	private String time;     			  
	private String sendTime;     			  //2014-06-12 16:33:27
	private String url;     			  //内容Url
	
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	private List<Attachment> attachmentFlag; //附件
	
	public V3Notice() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSendUser() {
		return sendUser;
	}

	public void setSendUser(String sendUser) {
		this.sendUser = sendUser;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getKindFlag() {
		return kindFlag;
	}

	public void setKindFlag(String kindFlag) {
		this.kindFlag = kindFlag;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public List<Attachment> getAttachmentFlag() {
		return attachmentFlag;
	}

	public void setAttachmentFlag(List<Attachment> attachmentFlag) {
		this.attachmentFlag = attachmentFlag;
	}

	public String getAttentionFlag() {
		return attentionFlag;
	}

	public void setAttentionFlag(String attentionFlag) {
		this.attentionFlag = attentionFlag;
	}

	public String getReadFlag() {
		return readFlag;
	}

	public void setReadFlag(String readFlag) {
		this.readFlag = readFlag;
	}

	public String getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
}
