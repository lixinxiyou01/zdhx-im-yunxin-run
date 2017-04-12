package zhwx.ui.dcapp.storeroom.model;

import java.io.Serializable;

/**
 * Created by Android on 2017/1/14.
 */

public class GetOutBean implements Serializable {

    /**
     * id : 20161208155126257034860924580224
     * userName : 技术支持
     * date : 2016-12-08
     * receiverName : 戴蓉江
     * warehouseName : 数码摄影室
     * outKindValue : 部门领用
     * signatureFlag : false
     */

    private String id;
    private String userName;
    private String date;
    private String receiverName;
    private String warehouseName;
    private String outKindValue;
    private String code;
    private boolean signatureFlag;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getOutKindValue() {
        return outKindValue;
    }

    public void setOutKindValue(String outKindValue) {
        this.outKindValue = outKindValue;
    }

    public boolean isSignatureFlag() {
        return signatureFlag;
    }

    public void setSignatureFlag(boolean signatureFlag) {
        this.signatureFlag = signatureFlag;
    }
}
