package zhwx.common.model;

import java.io.Serializable;

/**
 * 用户
 * @author 容联•云通讯 Modify By Li.Xin @ 中电和讯
 *
 */
public class User implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String v3Id;
	private String v3pwd;
	private String name;
	private String terminalId;  //subAccountSid
	private String subToken;
	private String voipAccount;  //Contactid
	private String voipPwd;

	private String loginName;
	private String mobileNum;
	private String sex;
	private String passWord;
	private String organizationId;
	private String dataSourceName;
	private String headPortraitUrl;
	private String kind;
	private String token;

	private String v3Url;
	private String appCode;

	private String accId;   //云信Id
	private String neteaseToken;  //云信登录Token

	public User() {
		super();
	}
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
	public String getTerminalId() {
		return terminalId;
	}
	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}
	public String getSubToken() {
		return subToken;
	}
	public void setSubToken(String subToken) {
		this.subToken = subToken;
	}
	public String getVoipAccount() {
		return voipAccount;
	}
	public void setVoipAccount(String voipAccount) {
		this.voipAccount = voipAccount;
	}
	public String getVoipPwd() {
		return voipPwd;
	}
	public void setVoipPwd(String voipPwd) {
		this.voipPwd = voipPwd;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getMobileNum() {
		return mobileNum;
	}
	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public String getOrganizationId() {
		return organizationId;
	}
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
	public String getHeadPortraitUrl() {
		return headPortraitUrl;
	}
	public void setHeadPortraitUrl(String headPortraitUrl) {
		this.headPortraitUrl = headPortraitUrl;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getV3Id() {
		return "20140923142329769262316155434533";
	}
	public void setV3Id(String v3Id) {
		this.v3Id = v3Id;
	}
	public String getV3Pwd() {
		return v3pwd;
	}
	public void setV3Pwd(String v3Pwd) {
		this.v3pwd = v3Pwd;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getV3pwd() {
		return v3pwd;
	}
	public void setV3pwd(String v3pwd) {
		this.v3pwd = v3pwd;
	}
	public String getV3Url() {
		return v3Url;
	}
	public void setV3Url(String v3Url) {
		this.v3Url = v3Url;
	}
	public String getAppCode() {
		return appCode;
	}
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	public String getDataSourceName() {
		return dataSourceName;
	}
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public String getAccId() {
		return accId;
	}

	public void setAccId(String accId) {
		this.accId = accId;
	}

	public String getNeteaseToken() {
		return neteaseToken;
	}

	public void setNeteaseToken(String neteaseToken) {
		this.neteaseToken = neteaseToken;
	}
}
