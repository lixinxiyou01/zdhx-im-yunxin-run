package zhwx.ui.dcapp.takecourse.listviewgroup.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Elective implements Serializable{
	private HashMap<String, String> ecActivity;
	
	private ArrayList<EcActivityCourseList> ecElectiveGroupList;
	// ecAlternativeCourse
	private ElectiveRuleMap electiveRuleMap;

	public HashMap<String, String> getEcActivity() {
		return ecActivity;
	}

	public void setEcActivity(HashMap<String, String> ecActivity) {
		this.ecActivity = ecActivity;
	}

	public ArrayList<EcActivityCourseList> getEcElectiveGroupList() {
		return ecElectiveGroupList;
	}

	public void setEcElectiveGroupList(
			ArrayList<EcActivityCourseList> ecElectiveGroupList) {
		this.ecElectiveGroupList = ecElectiveGroupList;
	}

	public ElectiveRuleMap getElectiveRuleMap() {
		return electiveRuleMap;
	}

	public void setElectiveRuleMap(ElectiveRuleMap electiveRuleMap) {
		this.electiveRuleMap = electiveRuleMap;
	}
	
	
	public class EcActivityCourseList implements Serializable{
		private ArrayList<EcActivityCourse> ecActivityCourseList;
		private String groupId;
		private String groupName;
		private String groupNum;

		public ArrayList<EcActivityCourse> getEcActivityCourseList() {
			return ecActivityCourseList;
		}

		public void setEcActivityCourseList(ArrayList<EcActivityCourse> ecActivityCourseList) {
			this.ecActivityCourseList = ecActivityCourseList;
		}

		public String getGroupId() {
			return groupId;
		}

		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}

		public String getGroupName() {
			return groupName;
		}

		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}

		public String getGroupNum() {
			return groupNum;
		}

		public void setGroupNum(String groupNum) {
			this.groupNum = groupNum;
		}
		
		public class EcActivityCourse implements Serializable {
			private String beforeCourseId;
			private String classroomDisplayName;
			private String classroomId;
			private String classTimeOfWeekStr;
			private String classTimeStr;
			private String courseCycle;
			private String courseDisplayName;
			private String courseId;
			private String ecCourseId;
			private String groupId;
			private String groupName;
			private String groupNum;
			private String hour;
			private String hourStr;
			private String id;
			private String kind;
			private String maxCount;
			private String score;
			private String scoreStr;
			private String selected;
			private String selectedNum;
			private String status;
			private String teacherName;
			private String time;
			private String versioned;

			private int selectedint; //1 0 -1 -2
			private String cantSelectReasonPreview;
			private String cantSelectReason;
			
			public String getCantSelectReasonPreview() {
				return cantSelectReasonPreview;
			}

			public void setCantSelectReasonPreview(String cantSelectReasonPreview) {
				this.cantSelectReasonPreview = cantSelectReasonPreview;
			}

			public String getCantSelectReason() {
				return cantSelectReason;
			}

			public void setCantSelectReason(String cantSelectReason) {
				this.cantSelectReason = cantSelectReason;
			}

			public int getSelectedint() {
				return selectedint;
			}

			public void setSelectedint(int selectedint) {
				this.selectedint = selectedint;
			}

			public String getBeforeCourseId() {
				return beforeCourseId;
			}

			public void setBeforeCourseId(String beforeCourseId) {
				this.beforeCourseId = beforeCourseId;
			}

			public String getClassroomDisplayName() {
				return classroomDisplayName;
			}

			public void setClassroomDisplayName(String classroomDisplayName) {
				this.classroomDisplayName = classroomDisplayName;
			}

			public String getClassroomId() {
				return classroomId;
			}

			public void setClassroomId(String classroomId) {
				this.classroomId = classroomId;
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

			public String getCourseCycle() {
				return courseCycle;
			}

			public void setCourseCycle(String courseCycle) {
				this.courseCycle = courseCycle;
			}

			public String getCourseDisplayName() {
				return courseDisplayName;
			}

			public void setCourseDisplayName(String courseDisplayName) {
				this.courseDisplayName = courseDisplayName;
			}

			public String getCourseId() {
				return courseId;
			}

			public void setCourseId(String courseId) {
				this.courseId = courseId;
			}

			public String getEcCourseId() {
				return ecCourseId;
			}

			public void setEcCourseId(String ecCourseId) {
				this.ecCourseId = ecCourseId;
			}

			public String getGroupId() {
				return groupId;
			}

			public void setGroupId(String groupId) {
				this.groupId = groupId;
			}

			public String getGroupName() {
				return groupName;
			}

			public void setGroupName(String groupName) {
				this.groupName = groupName;
			}

			public String getGroupNum() {
				return groupNum;
			}

			public void setGroupNum(String groupNum) {
				this.groupNum = groupNum;
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

			public String getSelected() {
				return selected;
			}

			public void setSelected(String selected) {
				this.selected = selected;
			}

			public String getSelectedNum() {
				return selectedNum;
			}

			public void setSelectedNum(String selectedNum) {
				this.selectedNum = selectedNum;
			}

			public String getStatus() {
				return status;
			}

			public void setStatus(String status) {
				this.status = status;
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

			public String getVersioned() {
				return versioned;
			}

			public void setVersioned(String versioned) {
				this.versioned = versioned;
			}

		}
	}
	
	@SuppressWarnings("serial")
	public class ElectiveRuleMap implements Serializable{

		private String classTimeFlag;  //时间冲突是否允许选择
		private String maxCount;
		private String groupNum;
		private String maxHour;
		private String maxScore;
		private ArrayList<ruleList> ruleList;

		public String getClassTimeFlag() {
			return classTimeFlag;
		}

		public void setClassTimeFlag(String classTimeFlag) {
			this.classTimeFlag = classTimeFlag;
		}

		public String getMaxCount() {
			return maxCount;
		}

		public void setMaxCount(String maxCount) {
			this.maxCount = maxCount;
		}

		public String getGroupNum() {
			return groupNum;
		}

		public void setGroupNum(String groupNum) {
			this.groupNum = groupNum;
		}

		public String getMaxHour() {
			return maxHour;
		}

		public void setMaxHour(String maxHour) {
			this.maxHour = maxHour;
		}

		public String getMaxScore() {
			return maxScore;
		}

		public void setMaxScore(String maxScore) {
			this.maxScore = maxScore;
		}

		public ArrayList<ruleList> getRuleList() {
			return ruleList;
		}

		public void setRuleList(ArrayList<ruleList> ruleList) {
			this.ruleList = ruleList;
		}
		public class ruleList {
			List<Course> courseList;
			String maxQuantity;
			String minQuantity;
			public List<Course> getCourseList() {
				return courseList;
			}

			public void setCourseList(List<Course> courseList) {
				this.courseList = courseList;
			}

			public String getMaxQuantity() {
				return maxQuantity;
			}

			public void setMaxQuantity(String maxQuantity) {
				this.maxQuantity = maxQuantity;
			}

			public String getMinQuantity() {
				return minQuantity;
			}

			public void setMinQuantity(String minQuantity) {
				this.minQuantity = minQuantity;
			}

			public class Course{
				private String courseName;
				private String courseId;
				public String getCourseName() {
					return courseName;
				}
				public void setCourseName(String courseName) {
					this.courseName = courseName;
				}
				public String getCourseId() {
					return courseId;
				}
				public void setCourseId(String courseId) {
					this.courseId = courseId;
				}
			}
		}
	}
}
