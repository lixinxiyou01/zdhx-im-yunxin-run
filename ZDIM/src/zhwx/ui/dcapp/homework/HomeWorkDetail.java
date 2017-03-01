package zhwx.ui.dcapp.homework;

import java.io.Serializable;

public class HomeWorkDetail implements Serializable{
	private String content;
	private String title;
	private String attachmentUrl;
	private String fileSize;
	private String endTime;
	private String attachmentName;
	private String workEclass;
	private String courseName;
	private String status;
	private String statusName;
	private String studentWorkId;
	private Result result;
	
	public Result getResult() {
		return result;
	}
	public void setResult(Result result) {
		this.result = result;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAttachmentUrl() {
		return attachmentUrl;
	}
	public void setAttachmentUrl(String attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getAttachmentName() {
		return attachmentName;
	}
	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}
	public String getWorkEclass() {
		return workEclass;
	}
	public void setWorkEclass(String workEclass) {
		this.workEclass = workEclass;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getStudentWorkId() {
		return studentWorkId;
	}
	public void setStudentWorkId(String studentWorkId) {
		this.studentWorkId = studentWorkId;
	}
}
