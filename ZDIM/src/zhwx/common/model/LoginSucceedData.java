package zhwx.common.model;

import java.io.Serializable;

/**
 * 登录出错信息
* @Title: LoginErrorData.java
* @Package com.bj.android.hzth.parentcircle.domain
* @author 容联•云通讯 Modify By Li.Xin @ 中电和讯
* @date 2014-11-21 下午2:40:32
 */
public class LoginSucceedData implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String successStatus;
	private User userData;
	private String token;
	public LoginSucceedData() {
		super();
	}
	public LoginSucceedData(String successStatus, User userData) {
		super();
		this.successStatus = successStatus;
		this.userData = userData;
	}
	public String getSuccessStatus() {
		return successStatus;
	}
	public void setSuccessStatus(String successStatus) {
		this.successStatus = successStatus;
	}
	public User getUser() {
		return userData;
	}
	public void setUser(User user) {
		this.userData = user;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
