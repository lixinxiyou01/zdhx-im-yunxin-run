package zhwx.ui.dcapp.carmanage.model;

import java.util.List;

/**   
 * @Title: EvaluateData.java 
 * @Package zhwx.ui.dcapp.carmanage.model
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ 中电和讯
 * @date 2016-3-30 下午3:19:10 
 */
public class EvaluateInfo {
	
	List<StarData> starData;
	List<AssignData> assignData;
	public List<StarData> getStarData() {
		return starData;
	}

	public void setStarData(List<StarData> starData) {
		this.starData = starData;
	}

	public List<AssignData> getAssignData() {
		return assignData;
	}

	public void setAssignData(List<AssignData> assignData) {
		this.assignData = assignData;
	}

	public class StarData{
		private String name;
		private String code;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
	}
	
	public class AssignData {
		private String carNum;
		private String carName;
		private String assignId;
		private String driver;
		public String getCarNum() {
			return carNum;
		}
		public void setCarNum(String carNum) {
			this.carNum = carNum;
		}
		public String getCarName() {
			return carName;
		}
		public void setCarName(String carName) {
			this.carName = carName;
		}
		public String getAssignId() {
			return assignId;
		}
		public void setAssignId(String assignId) {
			this.assignId = assignId;
		}
		public String getDriver() {
			return driver;
		}
		public void setDriver(String driver) {
			this.driver = driver;
		}
	}
}
