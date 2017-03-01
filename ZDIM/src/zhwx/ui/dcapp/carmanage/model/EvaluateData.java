package zhwx.ui.dcapp.carmanage.model;


/**   
 * @Title: EvaluateData.java 
 * @Package zhwx.ui.dcapp.carmanage.model
 * @Description:[{oaCarId:派车记录id,satisfaction:2,4,5,feedback:意见}]
 * @author Li.xin @ 中电和讯
 * @date 2016-3-30 下午3:19:10 
 */
public class EvaluateData {
	
	private String oaCarId;
	private String satisfaction;
	private String feedback;
	public String getOaCarId() {
		return oaCarId;
	}
	public void setOaCarId(String oaCarId) {
		this.oaCarId = oaCarId;
	}
	public String getSatisfaction() {
		return satisfaction;
	}
	public void setSatisfaction(String satisfaction) {
		this.satisfaction = satisfaction;
	}
	public String getFeedback() {
		return feedback;
	}
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

}
