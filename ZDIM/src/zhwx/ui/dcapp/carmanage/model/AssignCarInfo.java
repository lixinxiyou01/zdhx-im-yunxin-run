package zhwx.ui.dcapp.carmanage.model;

import java.util.List;

import zhwx.common.model.Attachment;


/**
 * @Title: AssignCarInfo.java
 * @Package zhwx.ui.dcapp.carmanage.model
 * @Description: (订单详情)
 * @author Li.xin @ 中电和讯
 * @date 2016-3-18 上午10:01:41
 */
public class AssignCarInfo {
	private String realTime;
	private String realCount;
	private String carNum;
	private String leaveTime;
	private String carName;
	private String driver;
	private String note;
	private String realAddress;
	private String leaveDate;
	private List<OaCarData> oaCarData;

	public String getRealTime() {
		return realTime;
	}

	public void setRealTime(String realTime) {
		this.realTime = realTime;
	}

	public String getRealCount() {
		return realCount;
	}

	public void setRealCount(String realCount) {
		this.realCount = realCount;
	}

	public String getCarNum() {
		return carNum;
	}

	public void setCarNum(String carNum) {
		this.carNum = carNum;
	}

	public String getLeaveTime() {
		return leaveTime;
	}

	public void setLeaveTime(String leaveTime) {
		this.leaveTime = leaveTime;
	}

	public String getCarName() {
		return carName;
	}

	public void setCarName(String carName) {
		this.carName = carName;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getRealAddress() {
		return realAddress;
	}

	public void setRealAddress(String realAddress) {
		this.realAddress = realAddress;
	}

	public String getLeaveDate() {
		return leaveDate;
	}

	public void setLeaveDate(String leaveDate) {
		this.leaveDate = leaveDate;
	}

	public List<OaCarData> getOaCarData() {
		return oaCarData;
	}

	public void setOaCarData(List<OaCarData> oaCarData) {
		this.oaCarData = oaCarData;
	}



	public class OaCarData {
		private String orderUser;
		private String userDate;
		private String userCount;
		private String feedBackFlag;
		private String personList;
		private String reason;
		private String feedBackAdvice;
		private String orderTime;
		private String backCount;
		private String departmentName;
		private String backTime;
		private String carUserName;
		private String arriveTime;
		private String address;
		private String backAddress;
		private String useAddress;
		private String instruction;
		private List<Attachment> attachments;
		private String backDate;
		private String telephone;
		private String useTime;
		private String backPerson;
		private List<StarData> starData;
		public String getOrderUser() {
			return orderUser;
		}
		public void setOrderUser(String orderUser) {
			this.orderUser = orderUser;
		}
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
		public String getFeedBackFlag() {
			return feedBackFlag;
		}
		public void setFeedBackFlag(String feedBackFlag) {
			this.feedBackFlag = feedBackFlag;
		}
		public String getPersonList() {
			return personList;
		}
		public void setPersonList(String personList) {
			this.personList = personList;
		}
		public String getReason() {
			return reason;
		}
		public void setReason(String reason) {
			this.reason = reason;
		}
		public String getFeedBackAdvice() {
			return feedBackAdvice;
		}
		public void setFeedBackAdvice(String feedBackAdvice) {
			this.feedBackAdvice = feedBackAdvice;
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
		public String getUseAddress() {
			return useAddress;
		}
		public void setUseAddress(String useAddress) {
			this.useAddress = useAddress;
		}
		public String getInstruction() {
			return instruction;
		}
		public void setInstruction(String instruction) {
			this.instruction = instruction;
		}
		public List<Attachment> getAttachments() {
			return attachments;
		}
		public void setAttachments(List<Attachment> attachments) {
			this.attachments = attachments;
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
		public String getUseTime() {
			return useTime;
		}
		public void setUseTime(String useTime) {
			this.useTime = useTime;
		}
		public String getBackPerson() {
			return backPerson;
		}
		public void setBackPerson(String backPerson) {
			this.backPerson = backPerson;
		}
		public List<StarData> getStarData() {
			return starData;
		}
		public void setStarData(List<StarData> starData) {
			this.starData = starData;
		}
	}

	public class StarData {
		private String name;
		private String value;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

}
