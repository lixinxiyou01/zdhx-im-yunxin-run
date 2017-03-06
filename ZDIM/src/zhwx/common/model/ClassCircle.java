package zhwx.common.model;

public class ClassCircle extends SchoolCircle {
	private String departmentId;
	private String departmentName;
	public ClassCircle() {
		super();
	}
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
}
