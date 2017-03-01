package zhwx.common.model;

import java.io.Serializable;

/**
 * 登录出错信息
* @Title: LoginErrorData.java
* @Package com.bj.android.hzth.parentcircle.domain 
* @author 容联•云通讯 Modify By Li.Xin @ 中电和讯
* @date 2014-11-21 下午2:40:32
 */
public class LoginErrorData implements Serializable{
	
	private String successStatus;
	private String errorMsg;
	public LoginErrorData() {
		super();
	}
	public LoginErrorData(String successStatus, String errorMsg) {
		super();
		this.successStatus = successStatus;
		this.errorMsg = errorMsg;
	}
	public String getSuccessStatus() {
		return successStatus;
	}
	public void setSuccessStatus(String successStatus) {
		this.successStatus = successStatus;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
