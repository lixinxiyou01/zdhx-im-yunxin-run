package zhwx.ui.dcapp.carmanage.model;

/**   
 * @Title: RepairIndexData.java
 * @Package zhwx.ui.dcapp.carmanage
 * @author Li.xin @ 中电和讯
 * @date 2016-3-11 下午2:34:24 
 */
public class IndexData {
	
	private OrderCarManage orderCarManage;
	private MyTask myTask;
	private MyOrderCar myOrderCar;
	
	public OrderCarManage getOrderCarManage() {
		return orderCarManage;
	}

	public void setOrderCarManage(OrderCarManage orderCarManage) {
		this.orderCarManage = orderCarManage;
	}

	public MyTask getMyTask() {
		return myTask;
	}

	public void setMyTask(MyTask myTask) {
		this.myTask = myTask;
	}

	public MyOrderCar getMyOrderCar() {
		return myOrderCar;
	}

	public void setMyOrderCar(MyOrderCar myOrderCar) {
		this.myOrderCar = myOrderCar;
	}


	public class OrderCarManage {
		private String dpc;
		private String pcz;
		private String ypc;
		private String orderCheck;
		public String getDpc() {
			return dpc;
		}
		public void setDpc(String dpc) {
			this.dpc = dpc;
		}
		public String getPcz() {
			return pcz;
		}
		public void setPcz(String pcz) {
			this.pcz = pcz;
		}
		public String getYpc() {
			return ypc;
		}
		public void setYpc(String ypc) {
			this.ypc = ypc;
		}
		public String getOrderCheck() {
			return orderCheck;
		}
		public void setOrderCheck(String orderCheck) {
			this.orderCheck = orderCheck;
		}
	}
	
	public class MyTask {
		private String wqr;
		private String wjs;
		private String wpj;
		public String getWqr() {
			return wqr;
		}
		public void setWqr(String wqr) {
			this.wqr = wqr;
		}
		public String getWjs() {
			return wjs;
		}
		public void setWjs(String wjs) {
			this.wjs = wjs;
		}
		public String getWpj() {
			return wpj;
		}
		public void setWpj(String wpj) {
			this.wpj = wpj;
		}
	}
	
	public class MyOrderCar {
		private String dpc;
		private String pcz;
		private String dpj;
		private String ypc;
		public String getDpc() {
			return dpc;
		}
		public void setDpc(String dpc) {
			this.dpc = dpc;
		}
		public String getPcz() {
			return pcz;
		}
		public void setPcz(String pcz) {
			this.pcz = pcz;
		}
		public String getDpj() {
			return dpj;
		}
		public void setDpj(String dpj) {
			this.dpj = dpj;
		}
		public String getYpc() {
			return ypc;
		}
		public void setYpc(String ypc) {
			this.ypc = ypc;
		}
	}
}
