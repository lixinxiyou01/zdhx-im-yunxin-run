package zhwx.ui.dcapp.homework;

import java.io.Serializable;
import java.util.List;

/**   
 * @Title: Result.java 
 * @Package zhwx.ui.dcapp.homework
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ 中电和讯
 * @date 2016年6月27日 下午4:47:11 
 */
public class Result implements Serializable{
	private String content;
	private List<String> imageList;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<String> getImageList() {
		return imageList;
	}
	public void setImageList(List<String> imageList) {
		this.imageList = imageList;
	}
}
