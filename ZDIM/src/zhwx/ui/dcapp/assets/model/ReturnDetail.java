package zhwx.ui.dcapp.assets.model;
import java.util.List;

/**
 * Created by LX on 2016/8/31.
 */

public class ReturnDetail {

    /**
     * operatorId : 20160702182314809783152354208866
     * returnAssets : [{"unitPrice":300,"purchaseFactory":"3","brandName":"a","brandId":"20160824091140291805299458750883","contact":"3","assetKindId":"20160824091030500257404170917159","id":"20160825184318901413367694932089","registrationDate":"2016-08-26","name":"thinkpadx260","patternName":"d","patternId":"20160824091150528684440050581351","status":"1","statusView":"正常","code":"dyj-001","departmentName":"后援分队","warrantyPeriod":3,"assetKindName":"打印机","kindParentIName":"","increaseWay":"","departmentParentId":"20160630212809474395708214601308","originalPrice":300,"stockNumber":"1","purchaseDate":"2016-08-10","kindParentId":"","departmentId":"20160701170647473059152328254903","departmentParentName":"北京中电学校"}]
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
    /**
     * unitPrice : 300
     * purchaseFactory : 3
     * brandName : a
     * brandId : 20160824091140291805299458750883
     * contact : 3
     * assetKindId : 20160824091030500257404170917159
     * id : 20160825184318901413367694932089
     * registrationDate : 2016-08-26
     * name : thinkpadx260
     * patternName : d
     * patternId : 20160824091150528684440050581351
     * status : 1
     * statusView : 正常
     * code : dyj-001
     * departmentName : 后援分队
     * warrantyPeriod : 3
     * assetKindName : 打印机
     * kindParentIName :
     * increaseWay :
     * departmentParentId : 20160630212809474395708214601308
     * originalPrice : 300
     * stockNumber : 1
     * purchaseDate : 2016-08-10
     * kindParentId :
     * departmentId : 20160701170647473059152328254903
     * departmentParentName : 北京中电学校
     */

    private List<ReturnAssetsBean> returnAssets;

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

    public List<ReturnAssetsBean> getReturnAssets() {
        return returnAssets;
    }

    public void setReturnAssets(List<ReturnAssetsBean> returnAssets) {
        this.returnAssets = returnAssets;
    }

    public static class ReturnAssetsBean {
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
