package zhwx.ui.dcapp.assets.model;

import java.io.Serializable;
import java.util.List;

/**   
 * @Title: MyAssets.java 
 * @Package zhwx.ui.dcapp.assets.model
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ zdhx
 * @date 2016年8月19日 下午5:05:04 
 */
public class MyAssets implements Serializable{
	
	
	public static final String STATUS_MYASSETS = "0";//我的资产
	public static final String STATUS_APPLYRECORD = "1";//状态：申请记录
	public static final String STATUS_REBACKED = "2";//状态：已归还

    /**
     * operatorId : 20160702182314809783152354208866
     * signatureFlag : false
     * departmentName : 后援分队
     * id : 20160829123850357115827295184148
     * operateDate : 2016-08-29
     * departmentParentId : 20160630212809474395708214601308
     * operatorName : 李鑫
     * userId : 20160702182314809783152354208866
     * userName : 李鑫
     * signatureShow : 否
     * departmentId : 20160701170647473059152328254903
     * departmentParentName : 北京中电学校
     * note : null
     */

    private List<ReturnRecordsBean> returnRecords;
    /**
     * checkStatusView : 已发放
     * reason :
     * departmentName : 北京中电学校
     * assetKindId : 20160824090940233101889987175481
     * assetKindName : 台式机
     * applyDate : 2016-08-30
     * id : 20160830144037698564857400024212
     * kindParentIName :
     * checkStatus : 4
     * school : 北京中电学校
     * departmentParentId :
     * applyUserId : 20160702182314809783152354208866
     * checkReason : 通过
     * applyUserName : 李鑫
     * checkDate : 2016-08-30
     * kindParentId :
     * departmentId : 20160630212809474395708214601308
     * departmentParentName :
     * user : 李鑫
     * demand :
     */

    private List<ApplicationRecordsBean> applicationRecords;
    /**
     * unitPrice : 100
     * purchaseFactory : 313
     * brandName : apple
     * brandId : 20160826114642561207395244685687
     * contact : 22
     * assetKindId : 20160826114557468310565827123421
     * id : 20160826114759443988436744740610
     * registrationDate : 2016-08-26
     * name : apple打印机21
     * patternName : Apple004
     * patternId : 20160826114648828515702469442657
     * status : 1
     * statusView : 正常
     * code : dyj-dyj-21-001
     * departmentName : 攻坚组
     * warrantyPeriod : 213
     * assetKindName : 打印机21
     * kindParentIName : 打印机
     * increaseWay :
     * departmentParentId : 20160630212809474395708214601308
     * originalPrice : 3300
     * stockNumber : 2
     * purchaseDate : 2016-08-17
     * kindParentId : 20160824091030500257404170917159
     * departmentId : 20160701170636840049084169648024
     * departmentParentName : 北京中电学校
     */

    private List<MyAssetListBean> myAssetList;

    public List<ReturnRecordsBean> getReturnRecords() {
        return returnRecords;
    }

    public void setReturnRecords(List<ReturnRecordsBean> returnRecords) {
        this.returnRecords = returnRecords;
    }

    public List<ApplicationRecordsBean> getApplicationRecords() {
        return applicationRecords;
    }

    public void setApplicationRecords(List<ApplicationRecordsBean> applicationRecords) {
        this.applicationRecords = applicationRecords;
    }

    public List<MyAssetListBean> getMyAssetList() {
        return myAssetList;
    }

    public void setMyAssetList(List<MyAssetListBean> myAssetList) {
        this.myAssetList = myAssetList;
    }

    public static class ReturnRecordsBean implements Serializable{
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
        private Object note;

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

        public Object getNote() {
            return note;
        }

        public void setNote(Object note) {
            this.note = note;
        }
    }

    public static class ApplicationRecordsBean implements Serializable{
        private String checkStatusView;
        private String reason;
        private String departmentName;
        private String assetKindId;
        private String assetKindName;
        private String applyDate;
        private String id;
        private String kindParentIName;
        private String checkStatus;
        private String school;
        private String departmentParentId;
        private String applyUserId;
        private String checkReason;
        private String applyUserName;
        private String checkDate;
        private String kindParentId;
        private String departmentId;
        private String departmentParentName;
        private String user;
        private String demand;

        public String getCheckStatusView() {
            return checkStatusView;
        }

        public void setCheckStatusView(String checkStatusView) {
            this.checkStatusView = checkStatusView;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getDepartmentName() {
            return departmentName;
        }

        public void setDepartmentName(String departmentName) {
            this.departmentName = departmentName;
        }

        public String getAssetKindId() {
            return assetKindId;
        }

        public void setAssetKindId(String assetKindId) {
            this.assetKindId = assetKindId;
        }

        public String getAssetKindName() {
            return assetKindName;
        }

        public void setAssetKindName(String assetKindName) {
            this.assetKindName = assetKindName;
        }

        public String getApplyDate() {
            return applyDate;
        }

        public void setApplyDate(String applyDate) {
            this.applyDate = applyDate;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getKindParentIName() {
            return kindParentIName;
        }

        public void setKindParentIName(String kindParentIName) {
            this.kindParentIName = kindParentIName;
        }

        public String getCheckStatus() {
            return checkStatus;
        }

        public void setCheckStatus(String checkStatus) {
            this.checkStatus = checkStatus;
        }

        public String getSchool() {
            return school;
        }

        public void setSchool(String school) {
            this.school = school;
        }

        public String getDepartmentParentId() {
            return departmentParentId;
        }

        public void setDepartmentParentId(String departmentParentId) {
            this.departmentParentId = departmentParentId;
        }

        public String getApplyUserId() {
            return applyUserId;
        }

        public void setApplyUserId(String applyUserId) {
            this.applyUserId = applyUserId;
        }

        public String getCheckReason() {
            return checkReason;
        }

        public void setCheckReason(String checkReason) {
            this.checkReason = checkReason;
        }

        public String getApplyUserName() {
            return applyUserName;
        }

        public void setApplyUserName(String applyUserName) {
            this.applyUserName = applyUserName;
        }

        public String getCheckDate() {
            return checkDate;
        }

        public void setCheckDate(String checkDate) {
            this.checkDate = checkDate;
        }

        public String getKindParentId() {
            return kindParentId;
        }

        public void setKindParentId(String kindParentId) {
            this.kindParentId = kindParentId;
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

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getDemand() {
            return demand;
        }

        public void setDemand(String demand) {
            this.demand = demand;
        }
    }

    public static class MyAssetListBean implements Serializable{
        private int unitPrice;
        private String purchaseFactory;
        private String brandName;
        private String brandId;
        private String contact;
        private String assetKindId;
        private String id;
        private String registrationDate;
        private String name;
        private String patternName;
        private String patternId;
        private String status;
        private String statusView;
        private String code;
        private String departmentName;
        private int warrantyPeriod;
        private String assetKindName;
        private String kindParentIName;
        private String increaseWay;
        private String departmentParentId;
        private int originalPrice;
        private String stockNumber;
        private String purchaseDate;
        private String kindParentId;
        private String departmentId;
        private String departmentParentName;

        public int getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(int unitPrice) {
            this.unitPrice = unitPrice;
        }

        public String getPurchaseFactory() {
            return purchaseFactory;
        }

        public void setPurchaseFactory(String purchaseFactory) {
            this.purchaseFactory = purchaseFactory;
        }

        public String getBrandName() {
            return brandName;
        }

        public void setBrandName(String brandName) {
            this.brandName = brandName;
        }

        public String getBrandId() {
            return brandId;
        }

        public void setBrandId(String brandId) {
            this.brandId = brandId;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getAssetKindId() {
            return assetKindId;
        }

        public void setAssetKindId(String assetKindId) {
            this.assetKindId = assetKindId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRegistrationDate() {
            return registrationDate;
        }

        public void setRegistrationDate(String registrationDate) {
            this.registrationDate = registrationDate;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPatternName() {
            return patternName;
        }

        public void setPatternName(String patternName) {
            this.patternName = patternName;
        }

        public String getPatternId() {
            return patternId;
        }

        public void setPatternId(String patternId) {
            this.patternId = patternId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatusView() {
            return statusView;
        }

        public void setStatusView(String statusView) {
            this.statusView = statusView;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDepartmentName() {
            return departmentName;
        }

        public void setDepartmentName(String departmentName) {
            this.departmentName = departmentName;
        }

        public int getWarrantyPeriod() {
            return warrantyPeriod;
        }

        public void setWarrantyPeriod(int warrantyPeriod) {
            this.warrantyPeriod = warrantyPeriod;
        }

        public String getAssetKindName() {
            return assetKindName;
        }

        public void setAssetKindName(String assetKindName) {
            this.assetKindName = assetKindName;
        }

        public String getKindParentIName() {
            return kindParentIName;
        }

        public void setKindParentIName(String kindParentIName) {
            this.kindParentIName = kindParentIName;
        }

        public String getIncreaseWay() {
            return increaseWay;
        }

        public void setIncreaseWay(String increaseWay) {
            this.increaseWay = increaseWay;
        }

        public String getDepartmentParentId() {
            return departmentParentId;
        }

        public void setDepartmentParentId(String departmentParentId) {
            this.departmentParentId = departmentParentId;
        }

        public int getOriginalPrice() {
            return originalPrice;
        }

        public void setOriginalPrice(int originalPrice) {
            this.originalPrice = originalPrice;
        }

        public String getStockNumber() {
            return stockNumber;
        }

        public void setStockNumber(String stockNumber) {
            this.stockNumber = stockNumber;
        }

        public String getPurchaseDate() {
            return purchaseDate;
        }

        public void setPurchaseDate(String purchaseDate) {
            this.purchaseDate = purchaseDate;
        }

        public String getKindParentId() {
            return kindParentId;
        }

        public void setKindParentId(String kindParentId) {
            this.kindParentId = kindParentId;
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
    }
}
