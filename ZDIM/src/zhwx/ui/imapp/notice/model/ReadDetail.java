package zhwx.ui.imapp.notice.model;

import java.util.List;

/**   
 * @Title: ReadDetail.java 
 * @Package com.zdhx.edu.im.ui.v3.notice
 * @author Li.xin @ 中电和讯
 * @date 2015-12-10 下午2:26:39 
 */
public class ReadDetail {
	
	private List<NameAndTime> readList;
	private List<NameAndTime> unReadList;
	
	public List<NameAndTime> getReadList() {
		return readList;
	}
	public void setReadList(List<NameAndTime> readList) {
		this.readList = readList;
	}
	public List<NameAndTime> getUnReadList() {
		return unReadList;
	}
	public void setUnReadList(List<NameAndTime> unReadList) {
		this.unReadList = unReadList;
	}

	public class NameAndTime {
		
		private String name;
		private String readingTime;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getReadingTime() {
			return readingTime;
		}
		public void setReadingTime(String readingTime) {
			this.readingTime = readingTime;
		}
	}
}
