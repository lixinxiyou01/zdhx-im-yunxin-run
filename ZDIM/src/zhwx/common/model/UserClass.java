package zhwx.common.model;

import java.io.Serializable;

public class UserClass implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String operateFlag;
	private String kind;
	private String headPortraitUrl;
	private String accId;

	public UserClass() {
		super();
	}
	
	public UserClass(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public UserClass(String name, String headPortraitUrl, String accId) {
		this.name = name;
		this.headPortraitUrl = headPortraitUrl;
		this.accId = accId;
	}

	public String getHeadPortraitUrl() {
		return headPortraitUrl;
	}

	public void setHeadPortraitUrl(String headPortraitUrl) {
		this.headPortraitUrl = headPortraitUrl;
	}


	public String getAccId() {
		return accId;
	}

	public void setAccId(String accId) {
		this.accId = accId;
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

	public String getOperateFlag() {
		return operateFlag;
	}

	public void setOperateFlag(String operateFlag) {
		this.operateFlag = operateFlag;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}
}
