package zhwx.ui.dcapp.storeroom.model;

import java.util.List;

/**
 * Created by Android on 2017/1/18.
 */

public class GrantResult {

    /**
     * applyReceiveId : 申领单id
     * code : 出库单号
     * date : 2017-01-12
     * warehouseId : 20161215144649001974178888537724
     * outKind : grly
     * receiveUserId : 20140923142329769262316155434533
     * note : 好东西
     * signatureId : 20161226150029460238377955427093
     * locationId : 20150519204038324417928924235555
     * goodsInfos : [{"goodsInfoId":"20161215145230657733134522844537","count":"1","sum":"70"},{"goodsInfoId":"20161215145305724085501607875380","count":"1","sum":"300"}]
     */

    private String applyReceiveId;
    private String code;
    private String date;
    private String warehouseId;
    private String outKind;
    private String receiveUserId;
    private String note;
    private String signatureId;
    private String locationId;
    private List<GoodsInfosBean> goodsInfos;

    public String getApplyReceiveId() {
        return applyReceiveId;
    }

    public void setApplyReceiveId(String applyReceiveId) {
        this.applyReceiveId = applyReceiveId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getOutKind() {
        return outKind;
    }

    public void setOutKind(String outKind) {
        this.outKind = outKind;
    }

    public String getReceiveUserId() {
        return receiveUserId;
    }

    public void setReceiveUserId(String receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getSignatureId() {
        return signatureId;
    }

    public void setSignatureId(String signatureId) {
        this.signatureId = signatureId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public List<GoodsInfosBean> getGoodsInfos() {
        return goodsInfos;
    }

    public void setGoodsInfos(List<GoodsInfosBean> goodsInfos) {
        this.goodsInfos = goodsInfos;
    }

    public static class GoodsInfosBean {
        /**
         * goodsInfoId : 20161215145230657733134522844537
         * count : 1
         * sum : 70
         */

        private String goodsInfoId;
        private String count;
        private String sum;

        public String getGoodsInfoId() {
            return goodsInfoId;
        }

        public void setGoodsInfoId(String goodsInfoId) {
            this.goodsInfoId = goodsInfoId;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getSum() {
            return sum;
        }

        public void setSum(String sum) {
            this.sum = sum;
        }
    }
}
