package zhwx.common.model;

import java.io.Serializable;

public class Organization implements Serializable{
	private String id;
	private String name;
	private String dataSourceName;
	public Organization(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public Organization() {
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
	public String getDataSourceName() {
		return dataSourceName;
	}
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

}
