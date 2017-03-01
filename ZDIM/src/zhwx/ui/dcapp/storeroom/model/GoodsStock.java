package zhwx.ui.dcapp.storeroom.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Android on 2017/1/14.
 */

public class GoodsStock implements Serializable {

    private List<IntogoodsstatisticsBean> intogoodsstatistics;
    private List<IntogoodsstatisticsBean> outgoodsstatistics;

    public List<IntogoodsstatisticsBean> getIntogoodsstatistics() {
        return intogoodsstatistics;
    }

    public void setIntogoodsstatistics(List<IntogoodsstatisticsBean> intogoodsstatistics) {
        this.intogoodsstatistics = intogoodsstatistics;
    }
    

    public List<IntogoodsstatisticsBean> getOutgoodsstatistics() {
		return outgoodsstatistics;
	}

	public void setOutgoodsstatistics(
			List<IntogoodsstatisticsBean> outgoodsstatistics) {
		this.outgoodsstatistics = outgoodsstatistics;
	}



	public static class IntogoodsstatisticsBean {
        /**
         * warehouseName : 测试仓库1
         * schoolName : 中电小学
         * count : 101
         */

        private String warehouseName;
        private String schoolName;
        private int count;
        private int moneycount;

        public String getWarehouseName() {
            return warehouseName;
        }

        public void setWarehouseName(String warehouseName) {
            this.warehouseName = warehouseName;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

		public int getMoneycount() {
			return moneycount;
		}

		public void setMoneycount(int moneycount) {
			this.moneycount = moneycount;
		}
    }
    
    
}
