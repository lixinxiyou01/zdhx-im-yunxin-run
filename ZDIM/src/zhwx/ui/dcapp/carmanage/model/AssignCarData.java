package zhwx.ui.dcapp.carmanage.model;

import java.util.List;

/**   
 * @Title: DriverData.java 
 * @Package zhwx.ui.dcapp.carmanage
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ 中电和讯
 * @date 2016-3-29 上午11:48:05 
 */
public class AssignCarData {
	
	private List<DriverData> driverData;
	
	public List<DriverData> getDriverData() {
		return driverData;
	}


	public void setDriverData(List<DriverData> driverData) {
		this.driverData = driverData;
	}

	public class DriverData {
		private String phone;
		private String driverId;
		private String driverName;
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getDriverId() {
			return driverId;
		}
		public void setDriverId(String driverId) {
			this.driverId = driverId;
		}
		public String getDriverName() {
			return driverName;
		}
		public void setDriverName(String driverName) {
			this.driverName = driverName;
		}
	}
}
