package zhwx.ui.dcapp.carmanage.model;

import java.util.List;

import zhwx.common.model.Attachment;


/**
 * @Title: OrderDetail.java
 * @Package zhwx.ui.dcapp.carmanage.model
 * @Description: (订单详情)
 * @author Li.xin @ 中电和讯
 * @date 2016-3-18 上午10:01:41
 */
public class OrderDetail {
	private String checkStatusView;
	private String checkUser;
	private String checkStatus;
	private String orderUser;
	private String userDate;
	private String userCount;
	private String personList;
	private String reason;
	private String orderTime;
	private String backCount;
	private String departmentName;
	private String checkFlag;
	private String backTime;
	private String carUserName;
	private String startTime;
	private String arriveTime;
	private String address;
	private String checkAdvice;
	private String backAddress;
	private String instruction;
	private String backDate;
	private String telephone;
	private String backPerson;
	private List<Attachment> attachments;
	private List<OaCarData> oaCarData;

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getCheckStatusView() {
		return checkStatusView;
	}

	public void setCheckStatusView(String checkStatusView) {
		this.checkStatusView = checkStatusView;
	}

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

	public String getCheckFlag() {
		return checkFlag;
	}

	public void setCheckFlag(String checkFlag) {
		this.checkFlag = checkFlag;
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

	public String getCheckAdvice() {
		return checkAdvice;
	}

	public void setCheckAdvice(String checkAdvice) {
		this.checkAdvice = checkAdvice;
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

	public String getBackPerson() {
		return backPerson;
	}

	public void setBackPerson(String backPerson) {
		this.backPerson = backPerson;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public List<OaCarData> getOaCarData() {
		return oaCarData;
	}

	public void setOaCarData(List<OaCarData> oaCarData) {
		this.oaCarData = oaCarData;
	}

	public String getCheckUser() {
		return checkUser;
	}

	public void setCheckUser(String checkUser) {
		this.checkUser = checkUser;
	}

	public String getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}



	public class OaCarData {
		private String feedBackFlag;
		private String carNum;
		private String feedBackAdvice;
		private String carName;
		private String useAddress;
		private String driver;
		private String useTime;
		private String phone;
		private String realCount;
		private String realAddress;
		private String realTime;
		private String note;
		
		private List<StarData> starData;

		public String getFeedBackFlag() {
			return feedBackFlag;
		}

		public void setFeedBackFlag(String feedBackFlag) {
			this.feedBackFlag = feedBackFlag;
		}

		public String getCarNum() {
			return carNum;
		}

		public void setCarNum(String carNum) {
			this.carNum = carNum;
		}

		public String getFeedBackAdvice() {
			return feedBackAdvice;
		}

		public void setFeedBackAdvice(String feedBackAdvice) {
			this.feedBackAdvice = feedBackAdvice;
		}

		public String getCarName() {
			return carName;
		}

		public void setCarName(String carName) {
			this.carName = carName;
		}

		public String getUseAddress() {
			return useAddress;
		}

		public void setUseAddress(String useAddress) {
			this.useAddress = useAddress;
		}

		public String getDriver() {
			return driver;
		}

		public void setDriver(String driver) {
			this.driver = driver;
		}

		public String getUseTime() {
			return useTime;
		}

		public void setUseTime(String useTime) {
			this.useTime = useTime;
		}

		public List<StarData> getStarData() {
			return starData;
		}

		public void setStarData(List<StarData> starData) {
			this.starData = starData;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getRealCount() {
			return realCount;
		}

		public void setRealCount(String realCount) {
			this.realCount = realCount;
		}

		public String getRealAddress() {
			return realAddress;
		}

		public void setRealAddress(String realAddress) {
			this.realAddress = realAddress;
		}

		public String getRealTime() {
			return realTime;
		}

		public void setRealTime(String realTime) {
			this.realTime = realTime;
		}

		public String getNote() {
			return note;
		}

		public void setNote(String note) {
			this.note = note;
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
