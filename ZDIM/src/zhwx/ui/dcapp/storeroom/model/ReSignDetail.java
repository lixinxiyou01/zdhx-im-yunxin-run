package zhwx.ui.dcapp.storeroom.model;

import java.util.List;

/**
 * Created by LX on 2016/9/18.
 */

public class ReSignDetail {

    /**
     * goodsList : [{"goodsCount":1,"goodsName":"耳机"}]
     * grantTime : 2017-04-11
     * department : 语文教研组
     * userName : 技术支持
     */

    private String grantTime;
    private String department;
    private String userName;
    private List<GoodsListBean> goodsList;

    public String getGrantTime() {
        return grantTime;
    }

    public void setGrantTime(String grantTime) {
        this.grantTime = grantTime;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<GoodsListBean> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<GoodsListBean> goodsList) {
        this.goodsList = goodsList;
    }

    public static class GoodsListBean {
        /**
         * goodsCount : 1
         * goodsName : 耳机
         */

        private int goodsCount;
        private String goodsName;

        public int getGoodsCount() {
            return goodsCount;
        }

        public void setGoodsCount(int goodsCount) {
            this.goodsCount = goodsCount;
        }

        public String getGoodsName() {
            return goodsName;
        }

        public void setGoodsName(String goodsName) {
            this.goodsName = goodsName;
        }
    }
}
