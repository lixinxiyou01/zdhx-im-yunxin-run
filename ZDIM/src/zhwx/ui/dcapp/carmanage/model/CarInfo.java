package zhwx.ui.dcapp.carmanage.model;

import java.io.Serializable;
import java.util.List;

/**   
 * @Title: CarInfo.java 
 * @Package zhwx.ui.dcapp.carmanage.model
 * @author Li.xin @ 中电和讯
 * @date 2016-3-28 上午11:41:03 
 */
public class CarInfo implements Serializable{
	private String userDate;
	private String userCount;
	private String schoolName;
	private String reason;
	private String personList;
	private List<SchoolsData> schoolsData;
	private String orderTime;
	private String backCount;
	private String departmentName;
	private String backTime;
	private String carUserName;
	private String arriveTime;
	private String address;
	private String backAddress;
	private String instruction;
	private String schoolId;
	private String backDate;
	private String telephone;
	private String noAssignCount;
	private String assignCount;
	private String backPerson;
	
	public String getUserDate() {
		return userDate;
	}

	public void setUserDate(String userDate) {
		this.userDate = userDate;
	}

	public String getUserCount() {
		return userCount;
	}

	public void setUserCount(String userCount) {
		this.userCount = userCount;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getPersonList() {
		return personList;
	}

	public void setPersonList(String personList) {
		this.personList = personList;
	}
	
	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public String getBackCount() {
		return backCount;
	}

	public void setBackCount(String backCount) {
		this.backCount = backCount;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getBackTime() {
		return backTime;
	}

	public void setBackTime(String backTime) {
		this.backTime = backTime;
	}

	public String getCarUserName() {
		return carUserName;
	}

	public void setCarUserName(String carUserName) {
		this.carUserName = carUserName;
	}

	public String getArriveTime() {
		return arriveTime;
	}

	public void setArriveTime(String arriveTime) {
		this.arriveTime = arriveTime;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBackAddress() {
		return backAddress;
	}

	public void setBackAddress(String backAddress) {
		this.backAddress = backAddress;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public String getBackDate() {
		return backDate;
	}

	public void setBackDate(String backDate) {
		this.backDate = backDate;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getNoAssignCount() {
		return noAssignCount;
	}

	public void setNoAssignCount(String noAssignCount) {
		this.noAssignCount = noAssignCount;
	}

	public String getAssignCount() {
		return assignCount;
	}

	public void setAssignCount(String assignCount) {
		this.assignCount = assignCount;
	}

	public String getBackPerson() {
		return backPerson;
	}

	public void setBackPerson(String backPerson) {
		this.backPerson = backPerson;
	}
	
	public List<SchoolsData> getSchoolsData() {
		return schoolsData;
	}

	public void setSchoolsData(List<SchoolsData> schoolsData) {
		this.schoolsData = schoolsData;
	}



	public class SchoolsData implements Serializable{
		private boolean isScope;
		private boolean isCurrSchool; //当前学校标记 true false
		private String schoolName;
		private List<CarData> carData;
		
		public boolean isScope() {
			return isScope;
		}
		public void setScope(boolean isScope) {
			this.isScope = isScope;
		}
		public String getSchoolName() {
			return schoolName;
		}
		public void setSchoolName(String schoolName) {
			this.schoolName = schoolName;
		}
		public List<CarData> getCarData() {
			return carData;
		}
		public void setCarData(List<CarData> carData) {
			this.carData = carData;
		}
		public boolean isCurrSchool() {
			return isCurrSchool;
		}
		public void setCurrSchool(boolean isCurrSchool) {
			this.isCurrSchool = isCurrSchool;
		}
	}
	
	
	public class CarData implements Serializable{
		private boolean fullFlag; 
		private String limitCount;
		private String carPicUrl;
		private String carId;
		private String carNum;
		private String carName;
		private String assignCount;
		public boolean isFullFlag() {
			return fullFlag;
		}
		public void setFullFlag(boolean fullFlag) {
			this.fullFlag = fullFlag;
		}
		public String getLimitCount() {
			return limitCount;
		}
		public void setLimitCount(String limitCount) {
			this.limitCount = limitCount;
		}
		public String getCarPicUrl() {
			return carPicUrl;
		}
		public void setCarPicUrl(String carPicUrl) {
			this.carPicUrl = carPicUrl;
		}
		public String getCarId() {
			return carId;
		}
		public void setCarId(String carId) {
			this.carId = carId;
		}
		public String getCarNum() {
			return carNum;
		}
		public void setCarNum(String carNum) {
			this.carNum = carNum;
		}
		public String getCarName() {
			return carName;
		}
		public void setCarName(String carName) {
			this.carName = carName;
		}
		public String getAssignCount() {
			return assignCount;
		}
		public void setAssignCount(String assignCount) {
			this.assignCount = assignCount;
		}
	}
}
