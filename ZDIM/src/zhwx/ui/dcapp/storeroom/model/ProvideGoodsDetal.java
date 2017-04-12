package zhwx.ui.dcapp.storeroom.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Android on 2017/1/14.
 */

public class ProvideGoodsDetal implements Serializable {


    /**
     * outwarehouseKind : {"grly":"个人领用","bmly":"部门领用","hdly":"活动领用","pk":"盘亏","qt":"其他"}
     * warehouseRecordData : [{"userId":"20151116112937165940444498014398","makerUserName":"123","userName":"123","warehouseId":"20170113162637091934873903924779","code":"20170118001","productDate":"2017-01-18","makerUserId":"20151116112937165940444498014398"}]
     * warehouseRecordGoodsList : [{"unit":"","count":2,"goodsKindName":"打印纸","brand":"","goodsSupplierName":"","goodsInfoName":"A4打印纸","code":"a4dyz","size":""},{"unit":"","count":3,"goodsKindName":"打印纸","brand":"","goodsSupplierName":"","goodsInfoName":"b5打印纸","code":"b5dyz","size":""}]
     * schoolwarehouseTree : [{"schoolName":"中电小学","warehouseList":[{"warehouseName":"测试仓库1","warehouseId":"20170113162637091934873903924779"}],"schoolId":"20150915151222630637587846800681"},{"schoolName":"中电中学","warehouseList":[],"schoolId":"20150520125000317903308409460795"},{"schoolName":"中电测试学校","warehouseList":[],"schoolId":"20151020125000317903308409460795"}]
     */

    private Map<String,String> outwarehouseKind;
    private List<WarehouseRecordDataBean> warehouseRecordData;
    private List<WarehouseRecordGoodsListBean> warehouseRecordGoodsList;
    private List<SchoolwarehouseTreeBean> schoolwarehouseTree;

    public Map<String,String> getOutwarehouseKind() {
        return outwarehouseKind;
    }

    public void setOutwarehouseKind(Map<String,String> outwarehouseKind) {
        this.outwarehouseKind = outwarehouseKind;
    }

    public List<WarehouseRecordDataBean> getWarehouseRecordData() {
        return warehouseRecordData;
    }

    public void setWarehouseRecordData(List<WarehouseRecordDataBean> warehouseRecordData) {
        this.warehouseRecordData = warehouseRecordData;
    }

    public List<WarehouseRecordGoodsListBean> getWarehouseRecordGoodsList() {
        return warehouseRecordGoodsList;
    }

    public void setWarehouseRecordGoodsList(List<WarehouseRecordGoodsListBean> warehouseRecordGoodsList) {
        this.warehouseRecordGoodsList = warehouseRecordGoodsList;
    }

    public List<SchoolwarehouseTreeBean> getSchoolwarehouseTree() {
        return schoolwarehouseTree;
    }

    public void setSchoolwarehouseTree(List<SchoolwarehouseTreeBean> schoolwarehouseTree) {
        this.schoolwarehouseTree = schoolwarehouseTree;
    }

    public static class OutwarehouseKindBean {
        /**
         * grly : 个人领用
         * bmly : 部门领用
         * hdly : 活动领用
         * pk : 盘亏
         * qt : 其他
         */

        private String grly;
        private String bmly;
        private String hdly;
        private String pk;
        private String qt;

        public String getGrly() {
            return grly;
        }

        public void setGrly(String grly) {
            this.grly = grly;
        }

        public String getBmly() {
            return bmly;
        }

        public void setBmly(String bmly) {
            this.bmly = bmly;
        }

        public String getHdly() {
            return hdly;
        }

        public void setHdly(String hdly) {
            this.hdly = hdly;
        }

        public String getPk() {
            return pk;
        }

        public void setPk(String pk) {
            this.pk = pk;
        }

        public String getQt() {
            return qt;
        }

        public void setQt(String qt) {
            this.qt = qt;
        }
    }

    public static class WarehouseRecordDataBean {
        /**
         * userId : 20151116112937165940444498014398
         * makerUserName : 123
         * userName : 123
         * warehouseId : 20170113162637091934873903924779
         * code : 20170118001
         * productDate : 2017-01-18
         * makerUserId : 20151116112937165940444498014398
         */

        private String userId;
        private String makerUserName;
        private String userName;
        private String warehouseId;
        private String code;
        private String productDate;
        private String makerUserId;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getMakerUserName() {
            return makerUserName;
        }

        public void setMakerUserName(String makerUserName) {
            this.makerUserName = makerUserName;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getWarehouseId() {
            return warehouseId;
        }

        public void setWarehouseId(String warehouseId) {
            this.warehouseId = warehouseId;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getProductDate() {
            return productDate;
        }

        public void setProductDate(String productDate) {
            this.productDate = productDate;
        }

        public String getMakerUserId() {
            return makerUserId;
        }

        public void setMakerUserId(String makerUserId) {
            this.makerUserId = makerUserId;
        }
    }

    public static class WarehouseRecordGoodsListBean {
        /**
         * unit :
         * count : 2
         * goodsKindName : 打印纸
         * brand :
         * goodsSupplierName :
         * goodsInfoName : A4打印纸
         * code : a4dyz
         * size :
         */

        private String unit;
        private String id;
        private int count;
        private String goodsKindName;
        private String brand;
        private String goodsSupplierName;
        private String goodsInfoName;
        private String code;
        private String size;

        public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getGoodsKindName() {
            return goodsKindName;
        }

        public void setGoodsKindName(String goodsKindName) {
            this.goodsKindName = goodsKindName;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getGoodsSupplierName() {
            return goodsSupplierName;
        }

        public void setGoodsSupplierName(String goodsSupplierName) {
            this.goodsSupplierName = goodsSupplierName;
        }

        public String getGoodsInfoName() {
            return goodsInfoName;
        }

        public void setGoodsInfoName(String goodsInfoName) {
            this.goodsInfoName = goodsInfoName;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }
    }

    public static class SchoolwarehouseTreeBean {
        /**
         * schoolName : 中电小学
         * warehouseList : [{"warehouseName":"测试仓库1","warehouseId":"20170113162637091934873903924779"}]
         * schoolId : 20150915151222630637587846800681
         */

        private String schoolName;
        private String schoolId;
        private List<WarehouseListBean> warehouseList;

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public String getSchoolId() {
            return schoolId;
        }

        public void setSchoolId(String schoolId) {
            this.schoolId = schoolId;
        }

        public List<WarehouseListBean> getWarehouseList() {
            return warehouseList;
        }

        public void setWarehouseList(List<WarehouseListBean> warehouseList) {
            this.warehouseList = warehouseList;
        }

        public static class WarehouseListBean {
            /**
             * warehouseName : 测试仓库1
             * warehouseId : 20170113162637091934873903924779
             */

            private String warehouseName;
            private String warehouseId;

            public String getWarehouseName() {
                return warehouseName;
            }

            public void setWarehouseName(String warehouseName) {
                this.warehouseName = warehouseName;
            }

            public String getWarehouseId() {
                return warehouseId;
            }

            public void setWarehouseId(String warehouseId) {
                this.warehouseId = warehouseId;
            }
        }
    }
}
