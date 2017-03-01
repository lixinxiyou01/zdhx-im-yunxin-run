package zhwx.ui.dcapp.assets.model;

import java.io.Serializable;

/**   
 * @Title: OrderCarListItem.java 
 * 
 */


public class CheckListItem implements Serializable{
	
	private String  checkStatusView;
	
	private String  id;
	
	private String  applyUserName;
	
	private String  school;
	
	private String  checkStatus;
	
	private String  reason; //用途 
	
	private String  departmentName;
	
	private String  applyDate;
	
	private String  demand;//需求说明
	
	private String  assetKindName;
	
	private String  checkReason;
	
	private String applyUserId;
	private String departmentId;
	
	public static final String STATUS_ALL = "";//全部
	public static final String STATUS_NOTAUDIT = "1";//状态：未审核
	public static final String STATUS_NOTPASS = "2";//状态：未通过
	public static final String STATUS_PASS = "3";//状态：已通过
	public static final String STATUS_GRANTED = "4";//状态：已发放


	public static final String STATUSNAME_NOTAUDIT = "未审核";
	public static final String STATUSNAME_NOTPASS = "未通过";
	public static final String STATUSNAME_PASS = "已通过";
	public static final String STATUSNAME_GRANTED = "已发放";
	public String getCheckStatusView() {
		return checkStatusView;
	}
	public void setCheckStatusView(String checkStatusView) {
		this.checkStatusView = checkStatusView;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getApplyUser() {
		return applyUserName;
	}
	public void setApplyUser(String applyUser) {
		this.applyUserName = applyUser;
	}
	public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
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
	public String getDepartment() {
		return departmentName;
	}
	public void setDepartment(String department) {
		this.departmentName = department;
	}
	public String getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}
	public String getDemand() {
		return demand;
	}
	public void setDemand(String demand) {
		this.demand = demand;
	}
	public String getAssetKindName() {
		return assetKindName;
	}
	public void setAssetKindName(String assetKindName) {
		this.assetKindName = assetKindName;
	}
	public String getCheckReason() {
		return checkReason;
	}
	public void setCheckReason(String checkReason) {
		this.checkReason = checkReason;
	}
	public String getApplyUserId() {
		return applyUserId;
	}
	public void setApplyUserId(String applyUserId) {
		this.applyUserId = applyUserId;
	}
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
}
