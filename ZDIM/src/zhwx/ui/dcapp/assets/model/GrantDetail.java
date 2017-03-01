package zhwx.ui.dcapp.assets.model;
import java.util.List;

/**
 * Created by LX on 2016/9/18.
 */

public class GrantDetail {

    /**
     * operateDate : 2016-09-05
     * grantAssets : [{"unitPrice":300,"purchaseFactory":"3","brandName":"a","brandId":"20160824091140291805299458750883","contact":"3","assetKindId":"20160824091030500257404170917159","id":"20160825184318901413367694932089","registrationDate":"2016-08-26","name":"thinkpadx260","patternName":"d","patternId":"20160824091150528684440050581351","status":"1","statusView":"正常","code":"dyj-001","departmentName":"后援分队","warrantyPeriod":3,"assetKindName":"打印机","kindParentIName":"","increaseWay":"","departmentParentId":"20160630212809474395708214601308","originalPrice":300,"stockNumber":"1","purchaseDate":"2016-08-10","kindParentId":"","departmentId":"20160701170647473059152328254903","departmentParentName":"北京中电学校"}]
     * department : 北京中电学校
     * user : 李鑫
     * note : null
     * operator : 李鑫
     */

    private String operateDate;
    private String department;
    private String user;
    private String note;
    private String operator;
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

    private List<GrantAssetsBean> grantAssets;

    public String getOperateDate() {
        return operateDate;
    }

    public void setOperateDate(String operateDate) {
        this.operateDate = operateDate;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public List<GrantAssetsBean> getGrantAssets() {
        return grantAssets;
    }

    public void setGrantAssets(List<GrantAssetsBean> grantAssets) {
        this.grantAssets = grantAssets;
    }

    public static class GrantAssetsBean {
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
