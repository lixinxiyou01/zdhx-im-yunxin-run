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
	private String statement;

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

	public String getNote() {
		return statement;
	}

	public void setNote(String note) {
		this.statement = note;
	}

	public static class MyRepairBean {
		/**
		 * dfk : 17831
		 * wjd : 30857
		 * wxz : 80573
		 * yxh : 65171
		 */

		private String dfk;
		private String wjd;
		private String wxz;
		private String yxh;

		public String getDfk() {
			return dfk;
		}

		public void setDfk(String dfk) {
			this.dfk = dfk;
		}

		public String getWjd() {
			return wjd;
		}

		public void setWjd(String wjd) {
			this.wjd = wjd;
		}

		public String getWxz() {
			return wxz;
		}

		public void setWxz(String wxz) {
			this.wxz = wxz;
		}

		public String getYxh() {
			return yxh;
		}

		public void setYxh(String yxh) {
			this.yxh = yxh;
		}
	}

	public static class MyTaskBean {
		/**
		 * wjd : 55226
		 * wxz : 43468
		 * yfk : 88072
		 */

		private String wjd;
		private String wxz;
		private String yfk;

		public String getWjd() {
			return wjd;
		}

		public void setWjd(String wjd) {
			this.wjd = wjd;
		}

		public String getWxz() {
			return wxz;
		}

		public void setWxz(String wxz) {
			this.wxz = wxz;
		}

		public String getYfk() {
			return yfk;
		}

		public void setYfk(String yfk) {
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

		private String dcl;
		private String fysp;
		private String ypd;
		private String ywc;

		public String getDcl() {
			return dcl;
		}

		public void setDcl(String dcl) {
			this.dcl = dcl;
		}

		public String getFysp() {
			return fysp;
		}

		public void setFysp(String fysp) {
			this.fysp = fysp;
		}

		public String getYpd() {
			return ypd;
		}

		public void setYpd(String ypd) {
			this.ypd = ypd;
		}

		public String getYwc() {
			return ywc;
		}

		public void setYwc(String ywc) {
			this.ywc = ywc;
		}


	}
}
