package zhwx.ui.dcapp.takecourse.listviewgroup.bean;

import java.util.ArrayList;
import java.util.HashMap;

public class MyEletiveCourse {
	private ArrayList<schoolTermInfoList> schoolTermInfoList;
	private HashMap<String, ArrayList<schoolTermStudentCourseMap>> schoolTermStudentCourseMap;
	private HashMap<String, String> schoolTermSumScoreMap;

	public ArrayList<schoolTermInfoList> getSchoolTermInfoList() {
		return schoolTermInfoList;
	}

	public void setSchoolTermInfoList(
			ArrayList<schoolTermInfoList> schoolTermInfoList) {
		this.schoolTermInfoList = schoolTermInfoList;
	}

	public HashMap<String, ArrayList<schoolTermStudentCourseMap>> getSchoolTermStudentCourseMap() {
		return schoolTermStudentCourseMap;
	}

	public void setSchoolTermStudentCourseMap(
			HashMap<String, ArrayList<schoolTermStudentCourseMap>> schoolTermStudentCourseMap) {
		this.schoolTermStudentCourseMap = schoolTermStudentCourseMap;
	}

	public HashMap<String, String> getSchoolTermSumScoreMap() {
		return schoolTermSumScoreMap;
	}

	public void setSchoolTermSumScoreMap(
			HashMap<String, String> schoolTermSumScoreMap) {
		this.schoolTermSumScoreMap = schoolTermSumScoreMap;
	}

	public class schoolTermInfoList {
		private String fullName;
		private String id;

		public String getFullName() {
			return fullName;
		}

		public void setFullName(String fullName) {
			this.fullName = fullName;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	public class schoolTermStudentCourseMap {
		private String courseDisplayName;
		private String classTimeOfWeekStr;
		private String classTimeStr;
		private String courseId;
		private String ecActivityCourseId;
		private String ecActivityId;
		private String ecCourseId;
		private String evaluate;
		private String hour;
		private String hourStr;
		private String id;
		private String kind;
		private String maxCount;
		private String schoolTermFullName;
		private String schoolTermId;
		private String schoolTermStartDate;
		private String schoolYearName;
		private String score;
		private String scoreStr;
		private String studentId;
		private String schoolTermName;
		private String teacherName;
		private String time;

		public String getCourseDisplayName() {
			return courseDisplayName;
		}

		public void setCourseDisplayName(String courseDisplayName) {
			this.courseDisplayName = courseDisplayName;
		}

		public String getClassTimeOfWeekStr() {
			return classTimeOfWeekStr;
		}

		public void setClassTimeOfWeekStr(String classTimeOfWeekStr) {
			this.classTimeOfWeekStr = classTimeOfWeekStr;
		}

		public String getClassTimeStr() {
			return classTimeStr;
		}

		public void setClassTimeStr(String classTimeStr) {
			this.classTimeStr = classTimeStr;
		}

		public String getCourseId() {
			return courseId;
		}

		public void setCourseId(String courseId) {
			this.courseId = courseId;
		}

		public String getEcActivityCourseId() {
			return ecActivityCourseId;
		}

		public void setEcActivityCourseId(String ecActivityCourseId) {
			this.ecActivityCourseId = ecActivityCourseId;
		}

		public String getEcActivityId() {
			return ecActivityId;
		}

		public void setEcActivityId(String ecActivityId) {
			this.ecActivityId = ecActivityId;
		}

		public String getEcCourseId() {
			return ecCourseId;
		}

		public void setEcCourseId(String ecCourseId) {
			this.ecCourseId = ecCourseId;
		}

		public String getEvaluate() {
			return evaluate;
		}

		public void setEvaluate(String evaluate) {
			this.evaluate = evaluate;
		}

		public String getHour() {
			return hour;
		}

		public void setHour(String hour) {
			this.hour = hour;
		}

		public String getHourStr() {
			return hourStr;
		}

		public void setHourStr(String hourStr) {
			this.hourStr = hourStr;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getKind() {
			return kind;
		}

		public void setKind(String kind) {
			this.kind = kind;
		}

		public String getMaxCount() {
			return maxCount;
		}

		public void setMaxCount(String maxCount) {
			this.maxCount = maxCount;
		}

		public String getSchoolTermFullName() {
			return schoolTermFullName;
		}

		public void setSchoolTermFullName(String schoolTermFullName) {
			this.schoolTermFullName = schoolTermFullName;
		}

		public String getSchoolTermId() {
			return schoolTermId;
		}

		public void setSchoolTermId(String schoolTermId) {
			this.schoolTermId = schoolTermId;
		}

		public String getSchoolTermStartDate() {
			return schoolTermStartDate;
		}

		public void setSchoolTermStartDate(String schoolTermStartDate) {
			this.schoolTermStartDate = schoolTermStartDate;
		}

		public String getSchoolYearName() {
			return schoolYearName;
		}

		public void setSchoolYearName(String schoolYearName) {
			this.schoolYearName = schoolYearName;
		}

		public String getScore() {
			return score;
		}

		public void setScore(String score) {
			this.score = score;
		}

		public String getScoreStr() {
			return scoreStr;
		}

		public void setScoreStr(String scoreStr) {
			this.scoreStr = scoreStr;
		}

		public String getStudentId() {
			return studentId;
		}

		public void setStudentId(String studentId) {
			this.studentId = studentId;
		}

		public String getSchoolTermName() {
			return schoolTermName;
		}

		public void setSchoolTermName(String schoolTermName) {
			this.schoolTermName = schoolTermName;
		}

		public String getTeacherName() {
			return teacherName;
		}

		public void setTeacherName(String teacherName) {
			this.teacherName = teacherName;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}
	}
}
