package zhwx.ui.dcapp.repairs.model;

import java.io.Serializable;

/**
 * Created by Android on 2017/3/14.
 */

public class RepairListItem implements Serializable {

    /**
     * id : 20170314152210472078266413328200
     * applyTime : 2017-03-14
     * placeName : 102
     * malfunction : 不可描述
     * statusCode : 0
     * statusView : 未接单
     * DeviceName : 台式机
     */

    private String id;
    private String userId;
    private String applyTime;
    private String placeName;
    private String malfunction;
    private String faultDescription;
    private String costApplication;
    private String checkStatus;
    private String statusCode;
    private String statusView;
    private String DeviceName;
    private String requestUserName;
    private String requestUserPhone;

    /**
     * 参数
     */
    public static final String CHECKSTATUS_ALL = "0";//全部-ALL

    public static final String CHECKSTATUS_WJD = "1";//未接单-报修，维修

    public static final String CHECKSTATUS_DCL = "1";//待处理-管理

    public static final String CHECKSTATUS_WXZ = "2";//维修中-报修，维修

    public static final String CHECKSTATUS_YPD = "2";//已派单-管理

    public static final String CHECKSTATUS_YFK = "3";//已反馈-维修

    public static final String CHECKSTATUS_DFK = "3";//待反馈-报修

    public static final String CHECKSTATUS_YWC = "3";//已完成-管理

    public static final String CHECKSTATUS_YXH = "4";//已修好-报修

    public static final String CHECKSTATUS_FYSP = "4";//费用审批-管理

    /**
     * 返回状态
     */
    public static final String STATUS_NOT_ACCEPTED = "0"; //未接单

    public static final String STATUS_REPAIRING = "1";   //维修中

    public static final String STATUS_REPAIRED_OK = "2"; //已维修

    public static final String STATUS_CANNOT_BE_FIXED = "3"; //不能维修

    public static final String STATUS_DELAYFIX = "4"; //延迟维修

    /**
     * 审批状态
     */
    public static final String COST_STATUS_NEED = "0"; //需要审核但是未审核

    public static final String COST_STATUS_PASS = "1";   //审核通过

    public static final String COST_STATUS_PASS_NOT = "2"; //审核不通过

    public static final String COST_STATUS_NEED_NOT = "3"; //不需要审核


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getMalfunction() {
        return malfunction;
    }

    public void setMalfunction(String malfunction) {
        this.malfunction = malfunction;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusView() {
        return statusView;
    }

    public void setStatusView(String statusView) {
        this.statusView = statusView;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String DeviceName) {
        this.DeviceName = DeviceName;
    }

    public String getRequestUserName() {
        return requestUserName;
    }

    public void setRequestUserName(String requestUserName) {
        this.requestUserName = requestUserName;
    }

    public String getRequestUserPhone() {
        return requestUserPhone;
    }

    public void setRequestUserPhone(String requestUserPhone) {
        this.requestUserPhone = requestUserPhone;
    }

    public String getFaultDescription() {
        return faultDescription;
    }

    public void setFaultDescription(String faultDescription) {
        this.faultDescription = faultDescription;
    }

    public String getCostApplication() {
        return costApplication;
    }

    public void setCostApplication(String costApplication) {
        this.costApplication = costApplication;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
