package zhwx.ui.dcapp.homework;

public class HomeWork {
	private String setTime;
	private String studentWorkId;
	private String title;
	private String startTime;
	private String endTime;
	private String courseName;
	private String status;
	private String statusName;
	
	public static final String STATE_SUBMIT="1";//已提交
	public static final String STATE_DOING="2";//做作业中
	public static final String STATE_NO="3";//还没开始做
	public static final String STATE_OVER="4"; //已过期
	
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
	public String getSetTime() {
		return setTime;
	}
	public void setSetTime(String setTime) {
		this.setTime = setTime;
	}
	public String getStudentWorkId() {
		return studentWorkId;
	}
	public void setStudentWorkId(String studentWorkId) {
		this.studentWorkId = studentWorkId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
}
