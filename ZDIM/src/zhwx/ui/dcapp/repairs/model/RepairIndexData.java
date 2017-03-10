package zhwx.ui.dcapp.repairs.model;

/**   
 * @Title: RepairIndexData.java
 * @Package zhwx.ui.dcapp.carmanage
 * @author Li.xin @ 中电和讯
 * @date 2016-3-11 下午2:34:24 
 */
public class RepairIndexData {

	/**
	 * myRepair : {"dfk":17831,"wjd":30857,"wxz":80573,"yxh":65171}
	 * myTask : {"wjd":55226,"wxz":43468,"yfk":88072}
	 * repairManage : {"dcl":41454,"fysp":84852,"ypd":40528,"ywc":13830}
	 */

	private MyRepairBean myRepair;
	private MyTaskBean myTask;
	private RepairManageBean repairManage;

	public MyRepairBean getMyRepair() {
		return myRepair;
	}

	public void setMyRepair(MyRepairBean myRepair) {
		this.myRepair = myRepair;
	}

	public MyTaskBean getMyTask() {
		return myTask;
	}

	public void setMyTask(MyTaskBean myTask) {
		this.myTask = myTask;
	}

	public RepairManageBean getRepairManage() {
		return repairManage;
	}

	public void setRepairManage(RepairManageBean repairManage) {
		this.repairManage = repairManage;
	}

	public static class MyRepairBean {
		/**
		 * dfk : 17831
		 * wjd : 30857
		 * wxz : 80573
		 * yxh : 65171
		 */

		private int dfk;
		private int wjd;
		private int wxz;
		private int yxh;

		public int getDfk() {
			return dfk;
		}

		public void setDfk(int dfk) {
			this.dfk = dfk;
		}

		public int getWjd() {
			return wjd;
		}

		public void setWjd(int wjd) {
			this.wjd = wjd;
		}

		public int getWxz() {
			return wxz;
		}

		public void setWxz(int wxz) {
			this.wxz = wxz;
		}

		public int getYxh() {
			return yxh;
		}

		public void setYxh(int yxh) {
			this.yxh = yxh;
		}
	}

	public static class MyTaskBean {
		/**
		 * wjd : 55226
		 * wxz : 43468
		 * yfk : 88072
		 */

		private int wjd;
		private int wxz;
		private int yfk;

		public int getWjd() {
			return wjd;
		}

		public void setWjd(int wjd) {
			this.wjd = wjd;
		}

		public int getWxz() {
			return wxz;
		}

		public void setWxz(int wxz) {
			this.wxz = wxz;
		}

		public int getYfk() {
			return yfk;
		}

		public void setYfk(int yfk) {
			this.yfk = yfk;
		}
	}

	public static class RepairManageBean {
		/**
		 * dcl : 41454
		 * fysp : 84852
		 * ypd : 40528
		 * ywc : 13830
		 */

		private int dcl;
		private int fysp;
		private int ypd;
		private int ywc;

		public int getDcl() {
			return dcl;
		}

		public void setDcl(int dcl) {
			this.dcl = dcl;
		}

		public int getFysp() {
			return fysp;
		}

		public void setFysp(int fysp) {
			this.fysp = fysp;
		}

		public int getYpd() {
			return ypd;
		}

		public void setYpd(int ypd) {
			this.ypd = ypd;
		}

		public int getYwc() {
			return ywc;
		}

		public void setYwc(int ywc) {
			this.ywc = ywc;
		}
	}
}
