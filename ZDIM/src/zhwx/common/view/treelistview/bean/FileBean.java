package zhwx.common.view.treelistview.bean;


import zhwx.common.view.treelistview.utils.annotating.TreeNodeHeadPortraitUrl;
import zhwx.common.view.treelistview.utils.annotating.TreeNodeHeadRemark;
import zhwx.common.view.treelistview.utils.annotating.TreeNodeId;
import zhwx.common.view.treelistview.utils.annotating.TreeNodeLabel;
import zhwx.common.view.treelistview.utils.annotating.TreeNodeLeafSize;
import zhwx.common.view.treelistview.utils.annotating.TreeNodePid;
import zhwx.common.view.treelistview.utils.annotating.TreeNodeTerminalId;

public class FileBean {
	@TreeNodeId
	private int id;		//标识
	@TreeNodePid
	private int pId;	//指向父节点的标识
	@TreeNodeLabel
	private String label;	//
	@TreeNodeTerminalId
	private String contactId;	//
	@TreeNodeHeadPortraitUrl
	private String headPortraitUrl;
	@TreeNodeHeadRemark
	private String remark;
	@TreeNodeLeafSize
	private String LeafSize;
	
	public FileBean(int id, int pId, String label) {
		super();
		this.id = id;
		this.pId = pId;
		this.label = label;
	}
	

	public FileBean(int id, int pId, String label, String contactId,String headPortraitUrl) {
		super();
		this.id = id;
		this.pId = pId;
		this.label = label;
		this.contactId = contactId;
		this.headPortraitUrl = headPortraitUrl;
	}

	public FileBean(int id, int pId, String label, String contactId,
			String headPortraitUrl, String remark) {
		super();
		this.id = id;
		this.pId = pId;
		this.label = label;
		this.contactId = contactId;
		this.headPortraitUrl = headPortraitUrl;
		this.remark = remark;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getpId() {
		return pId;
	}
	public void setpId(int pId) {
		this.pId = pId;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getHeadPortraitUrl() {
		return headPortraitUrl;
	}

	public void setHeadPortraitUrl(String headPortraitUrl) {
		this.headPortraitUrl = headPortraitUrl;
	}

	public String getLeafSize() {
		return LeafSize;
	}

	public void setLeafSize(String leafSize) {
		LeafSize = leafSize;
	}
	
}
