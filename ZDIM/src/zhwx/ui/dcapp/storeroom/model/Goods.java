package zhwx.ui.dcapp.storeroom.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Android on 2017/1/14.
 */

public class Goods implements Serializable{

    /**
     * gridModel : [{"securityInventory":1,"maxInventory":1,"status":"0","minInventory":2,"lu":null,"code":"cswp1","lt":null,"cost":"1","size":"规格/型号","id":"20170113162759380181494323559704","unit":"单 位","enName":"外文名称","GOODSINFO_STATUS_OUT":"2","inventory":"0","name":"测试物品1","GOODSINFO_STATUS_UNABLE":"0","brand":"品 牌","fu":"20151116112937165940444498014398","ft":"2017-01-13 16:27:59","GOODSINFO_STATUS_INTO":"1"},{"securityInventory":null,"maxInventory":null,"status":"0","minInventory":null,"lu":null,"code":"cswp1。1","lt":null,"cost":"","size":"","id":"20170113162848114013909169274699","unit":"","enName":"","GOODSINFO_STATUS_OUT":"2","inventory":"0","name":"测试物品1。1","GOODSINFO_STATUS_UNABLE":"0","brand":"","fu":"20151116112937165940444498014398","ft":"2017-01-13 16:28:48","GOODSINFO_STATUS_INTO":"1"}]
     * page : 1
     * record : 2
     * rows : 50
     * total : 1
     */

    private int page;
    private int record;
    private int rows;
    private int total;
    private List<GridModelBean> gridModel;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getRecord() {
        return record;
    }

    public void setRecord(int record) {
        this.record = record;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<GridModelBean> getGridModel() {
        return gridModel;
    }

    public void setGridModel(List<GridModelBean> gridModel) {
        this.gridModel = gridModel;
    }

    public static class GridModelBean {
        /**
         * securityInventory : 1
         * maxInventory : 1
         * status : 0
         * minInventory : 2
         * lu : null
         * code : cswp1
         * lt : null
         * cost : 1
         * size : 规格/型号
         * id : 20170113162759380181494323559704
         * unit : 单 位
         * enName : 外文名称
         * GOODSINFO_STATUS_OUT : 2
         * inventory : 0
         * name : 测试物品1
         * GOODSINFO_STATUS_UNABLE : 0
         * brand : 品 牌
         * fu : 20151116112937165940444498014398
         * ft : 2017-01-13 16:27:59
         * GOODSINFO_STATUS_INTO : 1
         */

        private int securityInventory;
        private int maxInventory;
        private String status;
        private int minInventory;
        private Object lu;
        private String code;
        private Object lt;
        private String cost;
        private String size;
        private String id;
        private String unit;
        private String enName;
        private String GOODSINFO_STATUS_OUT;
        private String inventory;
        private String name;
        private String GOODSINFO_STATUS_UNABLE;
        private String brand;
        private String fu;
        private String ft;
        private String GOODSINFO_STATUS_INTO;
        private int count = 1;

        public int getSecurityInventory() {
            return securityInventory;
        }

        public void setSecurityInventory(int securityInventory) {
            this.securityInventory = securityInventory;
        }

        public int getMaxInventory() {
            return maxInventory;
        }

        public void setMaxInventory(int maxInventory) {
            this.maxInventory = maxInventory;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getMinInventory() {
            return minInventory;
        }

        public void setMinInventory(int minInventory) {
            this.minInventory = minInventory;
        }

        public Object getLu() {
            return lu;
        }

        public void setLu(Object lu) {
            this.lu = lu;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public Object getLt() {
            return lt;
        }

        public void setLt(Object lt) {
            this.lt = lt;
        }

        public String getCost() {
            return cost;
        }

        public void setCost(String cost) {
            this.cost = cost;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

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

        public String getEnName() {
            return enName;
        }

        public void setEnName(String enName) {
            this.enName = enName;
        }

        public String getGOODSINFO_STATUS_OUT() {
            return GOODSINFO_STATUS_OUT;
        }

        public void setGOODSINFO_STATUS_OUT(String GOODSINFO_STATUS_OUT) {
            this.GOODSINFO_STATUS_OUT = GOODSINFO_STATUS_OUT;
        }

        public String getInventory() {
            return inventory;
        }

        public void setInventory(String inventory) {
            this.inventory = inventory;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGOODSINFO_STATUS_UNABLE() {
            return GOODSINFO_STATUS_UNABLE;
        }

        public void setGOODSINFO_STATUS_UNABLE(String GOODSINFO_STATUS_UNABLE) {
            this.GOODSINFO_STATUS_UNABLE = GOODSINFO_STATUS_UNABLE;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getFu() {
            return fu;
        }

        public void setFu(String fu) {
            this.fu = fu;
        }

        public String getFt() {
            return ft;
        }

        public void setFt(String ft) {
            this.ft = ft;
        }

        public String getGOODSINFO_STATUS_INTO() {
            return GOODSINFO_STATUS_INTO;
        }

        public void setGOODSINFO_STATUS_INTO(String GOODSINFO_STATUS_INTO) {
            this.GOODSINFO_STATUS_INTO = GOODSINFO_STATUS_INTO;
        }

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}
    }
}
