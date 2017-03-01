package zhwx.ui.dcapp.carmanage.model;

/**   
 * @Title: OrderCarListItem.java 
 * @Package zhwx.ui.dcapp.carmanage
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ 中电和讯
 * @date 2016-3-10 下午5:45:18 
 * [{"userDate":"2016-03-11","checkStatusView":"订车单完成","checkStatus":"4","reason":"","address":"区教委","arriveTime":"16:00","orderTime":"2016-03-10 17:03:11"}]
 *  status状态
	statusView状态名
	carUserName用车人
	departmentName用车部门
	telephone联系方式
	arriveTime 到达时间
	address 到达地点
	leaveDate出车日期
	leaveTime出车时间
	carName车辆名称
	carNum车牌
	useTime 上车时间
	useAddress上车地点
 */
public class OrderCarListItem {
	
	private String  orderCarId;
	
	private String  assignCarId;
	
	private String  userDate;
	
	private String  checkStatusView;
	
	private String  checkStatus;
	
	private String  evaluateFlag; //0未评价  1已评价
	
	private String  reason;
	
	private String  address;
	
	private String  arriveTime;
	
	private String  orderTime;
	
	private String  telephone;
	
	private String  departmentName;  //用车部门
	
	/** 司机 */
	private String  leaveDate;   //出车日期
	
	private String  carName;    //车型
	
	private String  carNum;     //车牌号
	
	private String  statusView; //状态
	
	private String  leaveTime;  //出车时间
	
	private String  carUserName ;  //用车人
	
	private String  useAddress ;  //上车地点
	
	public static final String CHECKSTATUS_ASSIGNING = "0";// 派车中(派车一部分)
	
	public static final String CHECKSTATUS_DRAFT = "1";//未派车(待派车)
	
	public static final String CHECKSTATUS_PASS = "2";// 已派车(管理员派车触发)
	
	public static final String CHECKSTATUS_UNPASS = "3";// 不予派车
	
	public static final String CHECKSTATUS_FINISH = "4";// 订车单完成(待评价)
	
	public static final String CHECKSTATUS_CANCEL = "5";// 订车单已取消
	
	public static final String CHECKSTATUS_CHECK = "uncheck";// 待审核
	
	public static final String CHECKSTATUS_CHECK_UNPASS = "unpass";//审核未通过
	
	public static final String CHECKSTATUS_DQR = "0";  //待确认
	
	public static final String CHECKSTATUS_DJS = "1";  //待结束
	
	public static final String CHECKSTATUS_DPJ = "2";    //待评价
	
	public static final String CHECKSTATUS_ALL = "";// 全部
	
	public String getUserDate() {
		return userDate;
	}

	public void setUserDate(String userDate) {
		this.userDate = userDate;
	}

	public String getCheckStatusView() {
		return checkStatusView;
	}

	public void setCheckStatusView(String checkStatusView) {
		this.checkStatusView = checkStatusView;
	}

	public String getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getArriveTime() {
		return arriveTime;
	}

	public void setArriveTime(String arriveTime) {
		this.arriveTime = arriveTime;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getCarUserName() {
		return carUserName;
	}

	public void setCarUserName(String carUserName) {
		this.carUserName = carUserName;
	}

	public String getLeaveDate() {
		return leaveDate;
	}

	public void setLeaveDate(String leaveDate) {
		this.leaveDate = leaveDate;
	}

	public String getCarName() {
		return carName;
	}

	public void setCarName(String carName) {
		this.carName = carName;
	}

	public String getCarNum() {
		return carNum;
	}

	public void setCarNum(String carNum) {
		this.carNum = carNum;
	}

	public String getStatusView() {
		return statusView;
	}

	public void setStatusView(String statusView) {
		this.statusView = statusView;
	}

	public String getLeaveTime() {
		return leaveTime;
	}

	public void setLeaveTime(String leaveTime) {
		this.leaveTime = leaveTime;
	}

	public String getUseAddress() {
		return useAddress;
	}

	public void setUseAddress(String useAddress) {
		this.useAddress = useAddress;
	}

	public String getEvaluateFlag() {
		return evaluateFlag;
	}

	public void setEvaluateFlag(String evaluateFlag) {
		this.evaluateFlag = evaluateFlag;
	}

	public String getOrderCarId() {
		return orderCarId;
	}

	public void setOrderCarId(String orderCarId) {
		this.orderCarId = orderCarId;
	}

	public String getAssignCarId() {
		return assignCarId;
	}

	public void setAssignCarId(String assignCarId) {
		this.assignCarId = assignCarId;
	}
}
