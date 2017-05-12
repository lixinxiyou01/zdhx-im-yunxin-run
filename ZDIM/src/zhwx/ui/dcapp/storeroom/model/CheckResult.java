package zhwx.ui.dcapp.storeroom.model;

/**   
 * @Title: CheckResult.java 
 * @Package com.zdhx.edu.im.ui.v3.storeroom.model
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ zdhx
 * @date 2017年1月17日 上午11:14:15 
 * {"id":申领单id,"checkStatus":审核状态,"checkOpinion":审核意见,"kind":审核类型}
		
		审核状态 ：  0-未审核  1-部门不通过   2-部门通过    3-总务不通过   4-总务通过
		审核类型 :   dept-部门审核     zw-总务审

 */
public class CheckResult {
	private String id;
	private String checkStatus;
	private String checkOpinion;
	private String kind;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCheckStatus() {
		return checkStatus;
	}
	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}
	public String getCheckOpinion() {
		return checkOpinion;
	}
	public void setCheckOpinion(String checkOpinion) {
		this.checkOpinion = checkOpinion;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
}
