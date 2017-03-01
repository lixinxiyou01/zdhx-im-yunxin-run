package zhwx.ui.dcapp.assets.model;

import java.io.Serializable;

/**   
 * @Title: IdAndName.java 
 * @Package zhwx.ui.dcapp.assets.model
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ zdhx
 * @date 2016年8月17日 下午4:36:36 
 */
public class IdAndName implements Serializable{

	private boolean isSelected;
	private String id;
	private String name;
	private int quantity;
	private int count;

	public IdAndName() {
		super();
	}
	public IdAndName(String name) {
		super();
		this.name = name;
	}

	public IdAndName(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
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
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
