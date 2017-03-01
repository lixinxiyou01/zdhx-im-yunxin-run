package zhwx.ui.dcapp.homework;

public class HomeWorkCourse {
	
	private String courseId;
	private String courseName;
	public String getCourseId() {
		return courseId;
	}
	public HomeWorkCourse(String courseId, String courseName) {
		super();
		this.courseId = courseId;
		this.courseName = courseName;
	}
	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
}
