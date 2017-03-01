package zhwx.ui.dcapp.carmanage.model;

/**   
 * @Title: ListKind.java 
 * @Package zhwx.ui.dcapp.carmanage.model
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ 中电和讯
 * @date 2016-3-16 上午11:03:40 
 */
public class ListKind {
//	public static final int CODE_
	
	private String name;
	private String code;
	
	public ListKind(String name, String code) {
		super();
		this.name = name;
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
