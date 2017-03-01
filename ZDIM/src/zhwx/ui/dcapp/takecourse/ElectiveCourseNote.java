package zhwx.ui.dcapp.takecourse;

import java.io.Serializable;

/**   
 * @Title: ElectiveCourseNote.java 
 * @Package com.zdhx.edu.im.ui.v3.takecourse 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ zdhx
 * @date 2016年7月21日 上午11:27:33 
 */
public class ElectiveCourseNote implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ecActivityNote;
	private String userId;
	private String ecActivityId;
	/**
	 * 校本选课 kind=0    显示内容：名称、任课教师、上课时间、学分、人数上限、已选人数
     * 分班选课 kind=1  显示内容：名称、学分、已选人数
	 */
	private String kind;
	public String getEcActivityNote() {
		return ecActivityNote;
	}
	public void setEcActivityNote(String ecActivityNote) {
		this.ecActivityNote = ecActivityNote;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getEcActivityId() {
		return ecActivityId;
	}
	public void setEcActivityId(String ecActivityId) {
		this.ecActivityId = ecActivityId;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
}
