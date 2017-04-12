package zhwx.ui.dcapp.storeroom.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Android on 2017/1/14.
 */

public class GetOutDetail implements Serializable {

    /**
     * warehouseName : 数码摄影室
     * goodsList : [{"count":1,"name":"耳机","sum":" 0.00"}]
     * outKindValue : 个人领用
     * userName : 技术支持
     * receiverName : CSPX_admin
     * code : 20170412003
     * date : 2017-04-12
     * departmentName : 语文教研组
     * url : null
     * note :
     * productDate : 2017-04-12
     */

    private String warehouseName;
    private String outKindValue;
    private String userName;
    private String receiverName;
    private String code;
    private String date;
    private String departmentName;
    private String url;
    private String note;
    private String productDate;
    private List<GoodsListBean> goodsList;

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
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

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getProductDate() {
        return productDate;
    }

    public void setProductDate(String productDate) {
        this.productDate = productDate;
    }

    public List<GoodsListBean> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<GoodsListBean> goodsList) {
        this.goodsList = goodsList;
    }

    public static class GoodsListBean {
        /**
         * count : 1
         * name : 耳机
         * sum :  0.00
         */

        private int count;
        private String name;
        private String sum;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSum() {
            return sum;
        }

        public void setSum(String sum) {
            this.sum = sum;
        }
    }
}
