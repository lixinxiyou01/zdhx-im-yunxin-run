package zhwx.ui.dcapp.assets.model;

import java.io.Serializable;
import java.util.List;

/**   
 * @Title: AssetDetail.java 
 * @Package zhwx.ui.dcapp.assets.model
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ zdhx
 * @date 2016年8月19日 上午11:44:33 
 */
public class AssetDetail implements Serializable{

    /**
     * status : 1
     * department : 中电小学
     * statusView : 正常
     * custodyStartDate : 2016-08-18
     * unitPrice : 432423400
     * purchaseFactory : 4214
     * code : bjb001
     * contact : 1231241
     * brandName : appel
     * warrantyPeriod : 412
     * assetKindName : 笔记本1
     * id : 20160818182106983939059958197191
     * custodian : 123
     * registrationDate : 2016-08-19
     * increaseWay : 
     * originalPrice : 2300
     * stockNumber : 2
     * purchaseDate : 2016-08-10
     * name : appel笔记本1
     * attachments : [{"id":"20160818184009965989173234880030","url":"/component/attachment!download.action?checkUser=false&period=&downloadToken=201608181840099659891732348800306210e537ab7425ada8f8cd2430ccb36f","name":"ic_launcher.png"}]
     * patternName : E123
     */

    private String status;
    private String department;
    private String statusView;
    private String custodyStartDate;
    private int unitPrice;
    private String purchaseFactory;
    private String code;
    private String contact;
    private String brandName;
    private int warrantyPeriod;
    private String assetKindName;
    private String id;
    private String custodian;
    private String registrationDate;
    private String increaseWay;
    private int originalPrice;
    private String stockNumber;
    private String purchaseDate;
    private String name;
    private String patternName;
    /**
     * id : 20160818184009965989173234880030
     * url : /component/attachment!download.action?checkUser=false&period=&downloadToken=201608181840099659891732348800306210e537ab7425ada8f8cd2430ccb36f
     * name : ic_launcher.png
     */

    private List<AttachmentsBean> attachments;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStatusView() {
        return statusView;
    }

    public void setStatusView(String statusView) {
        this.statusView = statusView;
    }

    public String getCustodyStartDate() {
        return custodyStartDate;
    }

    public void setCustodyStartDate(String custodyStartDate) {
        this.custodyStartDate = custodyStartDate;
    }

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustodian() {
        return custodian;
    }

    public void setCustodian(String custodian) {
        this.custodian = custodian;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getIncreaseWay() {
        return increaseWay;
    }

    public void setIncreaseWay(String increaseWay) {
        this.increaseWay = increaseWay;
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

    public List<AttachmentsBean> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentsBean> attachments) {
        this.attachments = attachments;
    }

    public static class AttachmentsBean {
        private String id;
        private String url;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
