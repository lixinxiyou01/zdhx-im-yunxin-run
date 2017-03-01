package zhwx.ui.dcapp.takecourse.listviewgroup.bean;

/**
 * 欢迎加入QQ开发群讨论：88130145
 * 
 * @Description:
 * @author: zhuanggy
 * @see:
 * @since:
 * @copyright © 35.com
 * @Date:2013-10-14
 */
public class Group {

	private String name;
	private boolean isShown = true;
	private int count;
	private int firstPositionInList;

	/**
	 * @param name
	 * @param isShown
	 * @param count
	 * @param firstPositionInList
	 */
	public Group(String name, boolean isShown, int count, int firstPositionInList) {
		super();
		this.name = name;
		this.isShown = isShown;
		this.count = count;
		this.firstPositionInList = firstPositionInList;
	}

	public int getFirstPositionInList() {
		return firstPositionInList;
	}

	public void setFirstPositionInList(int firstPositionInList) {
		this.firstPositionInList = firstPositionInList;
	}

	public String getName() {
		return name;
	}

	public void setName(String groupName) {
		this.name = groupName;
	}

	public boolean isShown() {
		return isShown;
	}

	// public void setShown(boolean isShown) {
	// this.isShown = isShown;
	// }

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
