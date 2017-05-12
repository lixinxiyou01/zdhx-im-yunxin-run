package zhwx.ui.dcapp.score.model;

/**   
 * @Title: StudentScoreInfo.java 
 * @Package com.zdhx.edu.im.ui.v3.score
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ zdhx
 * @date 2016年7月15日 下午4:18:51 
 */
public class StudentScoreInfo {
	private String eclassAvg; //平均分
	private String gradeAvg;
	private String gradeRank; //年级排名
	private String eclassRank;//班级排名
	private String score;
	private String gradeMax;
	private String eclassMax;
	private String courseName;
	public String getEclassAvg() {
		return eclassAvg;
	}
	public void setEclassAvg(String eclassAvg) {
		this.eclassAvg = eclassAvg;
	}
	public String getGradeAvg() {
		return gradeAvg;
	}
	public void setGradeAvg(String gradeAvg) {
		this.gradeAvg = gradeAvg;
	}
	public String getGradeRank() {
		return gradeRank;
	}
	public void setGradeRank(String gradeRank) {
		this.gradeRank = gradeRank;
	}
	public String getEclassRank() {
		return eclassRank;
	}
	public void setEclassRank(String eclassRank) {
		this.eclassRank = eclassRank;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getGradeMax() {
		return gradeMax;
	}
	public void setGradeMax(String gradeMax) {
		this.gradeMax = gradeMax;
	}
	public String getEclassMax() {
		return eclassMax;
	}
	public void setEclassMax(String eclassMax) {
		this.eclassMax = eclassMax;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
}
