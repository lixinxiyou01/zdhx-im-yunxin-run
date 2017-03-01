package zhwx.ui.imapp.notice.model;

import java.io.File;

import zhwx.common.model.Attachment;

/**   
 * @Title: SendFile.java 
 * @Package com.zdhx.edu.im.ui.v3.notice 
 * @author Li.xin @ 中电和讯
 * @date 2015-12-15 下午2:12:57 
 */
public class SendFile {
	
	public static int FILE = 0;
	public static int ATTACHMENT = 1;
	
	private int kind;
	private File file;
	private Attachment attachment;
	public int getKind() {
		return kind;
	}
	public void setKind(int kind) {
		this.kind = kind;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public Attachment getAttachment() {
		return attachment;
	}
	public void setAttachment(Attachment attachment) {
		this.attachment = attachment;
	}
}
