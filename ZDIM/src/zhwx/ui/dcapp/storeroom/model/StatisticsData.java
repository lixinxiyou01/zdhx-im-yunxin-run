package zhwx.ui.dcapp.storeroom.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Android on 2017/1/14.
 */

public class StatisticsData implements Serializable{

    private List<GoodsstatisticsBean> goodsstatistics;

    public List<GoodsstatisticsBean> getGoodsstatistics() {
        return goodsstatistics;
    }

    public void setGoodsstatistics(List<GoodsstatisticsBean> goodsstatistics) {
        this.goodsstatistics = goodsstatistics;
    }

    public static class GoodsstatisticsBean {
        /**
         * outWarehouseCount : 0
         * intoWarehouseMoney : 0
         * outWarehouseMoney : 0
         * inventory : 0
         * intoWarehouseCount : 0
         * goodsInfoName : 测试物品1
         */
    	private String id;
        private String outWarehouseCount;
        private String intoWarehouseMoney;
        private String outWarehouseMoney;
        private String inventory;
        private String intoWarehouseCount;
        private String goodsInfoName;
        
        public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getOutWarehouseCount() {
            return outWarehouseCount;
        }

        public void setOutWarehouseCount(String outWarehouseCount) {
            this.outWarehouseCount = outWarehouseCount;
        }

        public String getIntoWarehouseMoney() {
            return intoWarehouseMoney;
        }

        public void setIntoWarehouseMoney(String intoWarehouseMoney) {
            this.intoWarehouseMoney = intoWarehouseMoney;
        }

        public String getOutWarehouseMoney() {
            return outWarehouseMoney;
        }

        public void setOutWarehouseMoney(String outWarehouseMoney) {
            this.outWarehouseMoney = outWarehouseMoney;
        }

        public String getInventory() {
            return inventory;
        }

        public void setInventory(String inventory) {
            this.inventory = inventory;
        }

        public String getIntoWarehouseCount() {
            return intoWarehouseCount;
        }

        public void setIntoWarehouseCount(String intoWarehouseCount) {
            this.intoWarehouseCount = intoWarehouseCount;
        }

        public String getGoodsInfoName() {
            return goodsInfoName;
        }

        public void setGoodsInfoName(String goodsInfoName) {
            this.goodsInfoName = goodsInfoName;
        }
    }
}
