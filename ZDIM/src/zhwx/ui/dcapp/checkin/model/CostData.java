package zhwx.ui.dcapp.checkin.model;

import java.util.List;

/**   
 * @Title: CostType.java 
 * @Package com.zdhx.edu.im.ui.v3.checkin.model 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ zdhx
 * @date 2016年10月19日 下午3:24:02 
 */
public class CostData {
	private String remark;
	private String status;
	private List<CostType> costTypeList;
	private List<Cust> costList;
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<CostType> getCostTypeList() {
		return costTypeList;
	}
	public void setCostTypeList(List<CostType> costTypeList) {
		this.costTypeList = costTypeList;
	}
	public List<Cust> getCostList() {
		return costList;
	}
	public void setCostList(List<Cust> costList) {
		this.costList = costList;
	}
}
