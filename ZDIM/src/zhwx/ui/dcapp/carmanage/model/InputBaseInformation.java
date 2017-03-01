package zhwx.ui.dcapp.carmanage.model;

import java.util.List;

/**   
 * @Title: InputBaseInformation.java 
 * @Package zhwx.ui.dcapp.carmanage.model
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ 中电和讯
 * @date 2016-3-23 下午5:16:38 
 */
public class InputBaseInformation {
	private String phone;
	private String addresses;
	private String reasons;
	private List<DeptData> deptData;
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddresses() {
		return addresses;
	}

	public void setAddresses(String addresses) {
		this.addresses = addresses;
	}

	public String getReasons() {
		return reasons;
	}

	public void setReasons(String reasons) {
		this.reasons = reasons;
	}

	public List<DeptData> getDeptData() {
		return deptData;
	}

	public void setDeptData(List<DeptData> deptData) {
		this.deptData = deptData;
	}

	public class DeptData {
		private String id;
		private String name;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
}
