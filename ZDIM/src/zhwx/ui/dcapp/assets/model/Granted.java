package zhwx.ui.dcapp.assets.model;

import java.io.Serializable;

/**   
 * @Title: Granted.java 
 * @Package zhwx.ui.dcapp.assets.model
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ zdhx
 * @date 2016年9月18日 下午5:21:03 
 */
public class Granted implements Serializable{

    /**
     * operatorId : 20160702182314809783152354208866
     * signatureFlag : true
     * departmentName : 北京中电学校
     * id : 20160905105853233786646417213711
     * operateDate : 2016-09-05
     * departmentParentId : 
     * operatorName : 李鑫
     * userId : 20160702182314809783152354208866
     * userName : 李鑫
     * signatureShow : 是
     * departmentId : 20160630212809474395708214601308
     * departmentParentName : 
     * note : null
     */

    private String operatorId;
    private boolean signatureFlag;
    private String departmentName;
    private String id;
    private String operateDate;
    private String departmentParentId;
    private String operatorName;
    private String userId;
    private String userName;
    private String signatureShow;
    private String departmentId;
    private String departmentParentName;
    private String note;

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public boolean isSignatureFlag() {
        return signatureFlag;
    }

    public void setSignatureFlag(boolean signatureFlag) {
        this.signatureFlag = signatureFlag;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOperateDate() {
        return operateDate;
    }

    public void setOperateDate(String operateDate) {
        this.operateDate = operateDate;
    }

    public String getDepartmentParentId() {
        return departmentParentId;
    }

    public void setDepartmentParentId(String departmentParentId) {
        this.departmentParentId = departmentParentId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSignatureShow() {
        return signatureShow;
    }

    public void setSignatureShow(String signatureShow) {
        this.signatureShow = signatureShow;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentParentName() {
        return departmentParentName;
    }

    public void setDepartmentParentName(String departmentParentName) {
        this.departmentParentName = departmentParentName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
