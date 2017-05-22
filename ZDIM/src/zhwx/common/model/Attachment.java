package zhwx.common.model;

import java.io.Serializable;

public class Attachment implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String url;
	private String name;
	private String id;
	private long lenth;
	public Attachment() {
		super();
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public long getLenth() {
		return lenth;
	}

	public void setLenth(long lenth) {
		this.lenth = lenth;
	}
}
