package zhwx.ui.dcapp.storeroom.model;

import java.util.List;

/**   
 * @Title: ApplyJsonModel.java 
 * @Package com.lanxum.hzth.im.ui.v3.storeroom.model 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ zdhx
 * @date 2017年1月14日 下午5:34:19 
 * 
 * 
 */

/**
 * 提交申领
 * @param baseUrl
 * @param  resultJson=
	{"code":"20170113003","departmentId":"20130307155116931287536222991657","deptCheckUserId":
	"20140923142329769262316155434533","kind":"grsl","userId":"20140923142329769262316155434533","date":
	"2017-01-12","reason":"123","note":"456","goodsInfos":
	[{"goodsInfoId":"20161215145230657733134522844537","count":"4"},
	{"goodsInfoId":"20161215145142513113863726007316","count":"5"}]}]}
	deptCheckUserId 可以去掉或者为空，假如设置部门不需要审核的话 
 * @throws IOException
 */
public class ApplyJsonModel {
	private String code;
	private String departmentId;
	private String deptCheckUserId = "";
	private String kind;
	private String userId;
	private String date;
	private String reason;
	private String note;
	private List<GoodInfo> goodsInfos;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	public String getDeptCheckUserId() {
		return deptCheckUserId;
	}
	public void setDeptCheckUserId(String deptCheckUserId) {
		this.deptCheckUserId = deptCheckUserId;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public List<GoodInfo> getGoodsInfos() {
		return goodsInfos;
	}
	public void setGoodsInfos(List<GoodInfo> goodsInfos) {
		this.goodsInfos = goodsInfos;
	}
	
	public static class GoodInfo {
		public String goodsInfoId;
		public String count;
		public String getGoodsInfoId() {
			return goodsInfoId;
		}
		public void setGoodsInfoId(String goodsInfoId) {
			this.goodsInfoId = goodsInfoId;
		}
		public String getCount() {
			return count;
		}
		public void setCount(String count) {
			this.count = count;
		}
	}
}
