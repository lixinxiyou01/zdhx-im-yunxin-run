package zhwx.ui.dcapp.carmanage.model;

/**   
 * @Title: OrderCarTStatus.java 
 * @Package zhwx.ui.dcapp.carmanage.model
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ 中电和讯
 * @date 2016-3-18 下午2:10:46 
 */
public class OrderCarTStatus {
	private String opTime;
	private String status;
	private String name;
	private String note;
	public String getOpTime() {
		return opTime;
	}
	public void setOpTime(String opTime) {
		this.opTime = opTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
}
